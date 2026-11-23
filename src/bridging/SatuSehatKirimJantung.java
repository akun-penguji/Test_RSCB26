/*
 * Orkestrator pengiriman Registrasi Jantung ke SatuSehat (FHIR R4) — Fase 3.
 *
 * Urutan per registrasi (idempoten via tabel satu_sehat_jantung_kirim):
 *   1. Prasyarat : Encounter kunjungan sudah terkirim (satu_sehat_encounter.id_encounter).
 *   2. Condition : ICD-10 + stage (derajat hipertensi) + onsetDateTime (wajib untuk SKA).
 *   3. EpisodeOfCare (CAD) : getOrCreate dengan status awal waitlist/active + link Encounter.
 *   4. Observation faktor risiko : hipertensi, DM, dislipidemia, merokok.
 *   5. Observation target organ damage : satu resource per kode (valueCodeableConcept 0..1).
 *   6. Observation SKA : GRACE Score + Killip (wajib untuk jalur SKA).
 *   7. Observation PJR : pencegahan sekunder + jenis obat.
 *   8. Procedure : satu per baris tindakan, kode ICD-9-CM.
 *
 * TERMINOLOGI TIDAK DI-HARDCODE. Semua kode diambil dari tabel ref_registry_kode,
 * karena kode kemkes (OCxxxxxx, OVxxxxxx, OIxxxxxx) belum diverifikasi final ke Postman collection
 * resmi — koreksi cukup lewat UPDATE tanpa rebuild aplikasi.
 * Lihat docs/registrasi-jantung-satusehat.md §Status verifikasi terminologi.
 *
 * Registry ini LONGITUDINAL: EpisodeOfCare hanya ditutup (finished) saat pasien
 * meninggal, bukan saat kunjungan selesai.
 *
 * Dipakai dari DlgRegistrasiJantung / DlgRegistrasiJantungGlobal (tombol Kirim).
 */
package bridging;

import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.koneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public final class SatuSehatKirimJantung {

    private SatuSehatKirimJantung() { }

    /** Hasil kirim per registrasi, dikembalikan sebagai log untuk ditampilkan ke user. */
    public static String kirim(long idRegistrasi) {
        StringBuilder log = new StringBuilder();
        Connection kon = koneksiDB.condb();
        ApiSatuSehat api = new ApiSatuSehat();
        SatuSehatCekNIK cek = new SatuSehatCekNIK();
        ObjectMapper mapper = new ObjectMapper();
        String link = api.getBaseUrl();

        try {
            Reg r = loadReg(kon, idRegistrasi);
            if (r == null) return "Registrasi tidak ditemukan (id " + idRegistrasi + ").";

            // ---- Pasien & dokter ----
            String noKtpPasien = scalar(kon, "select ifnull(no_ktp,'') from pasien where no_rkm_medis=? limit 1", r.noRkm);
            if (noKtpPasien.isEmpty()) return "Pasien tidak punya NIK — wajib untuk lookup IHS SatuSehat.";
            String idPasien = cek.tampilIDPasien(noKtpPasien);
            if (idPasien == null || idPasien.isEmpty()) return "Pasien belum ter-IHS di SatuSehat (Patient ID kosong).";
            log.append("IHS Pasien: ").append(idPasien).append("\n");

            String noKtpDokter = r.kdDokter;
            String idDokter = (noKtpDokter == null || noKtpDokter.isEmpty()) ? "" : cek.tampilIDParktisi(noKtpDokter);

            // ---- Prasyarat: Encounter ----
            String idEnc = scalar(kon, "select ifnull(id_encounter,'') from satu_sehat_encounter where no_rawat=? limit 1", r.noRawat);
            if (idEnc.isEmpty())
                return "Encounter untuk No.Rawat " + r.noRawat + " belum dikirim ke SatuSehat.\n"
                     + "Kirim Encounter dulu (menu Kirim Encounter Satu Sehat), baru Registrasi Jantung.";
            log.append("Encounter: ").append(idEnc).append("\n");

            String waktu = efektif(r);

            // ================= 1. Condition =================
            String idCondition = alreadySent(kon, idRegistrasi, "condition", "");
            if (idCondition.isEmpty()) {
                if (r.kdPenyakit.isEmpty()) {
                    log.append("Condition dilewati: kode ICD-10 belum diisi.\n");
                } else {
                    String cj = buildConditionJson(kon, r, idPasien, idEnc);
                    idCondition = postFhir(api, mapper, link, "Condition", cj, log);
                    if (!idCondition.isEmpty()) recordSent(kon, idRegistrasi, "condition", "", idCondition);
                }
            } else {
                log.append("Condition sudah terkirim (").append(idCondition).append(").\n");
            }

            // ================= 2. EpisodeOfCare (CAD) =================
            // Memakai overload dengan status awal + findLiveEoc: registry jantung
            // dimulai sebagai 'waitlist', dan findActiveEoc tidak akan menemukannya
            // pada kunjungan berikutnya sehingga terbuat EOC duplikat.
            String periodStart = firstNonEmpty(safeDate(r.tglDiagnosis), safeDate(r.tglRegistrasi));
            String idEoc = SatuSehatEpisodeOfCareHelper.getOrCreate(
                    r.noRkm, idPasien, r.nama,
                    SatuSehatEpisodeOfCareHelper.TYPE_CAD[0],
                    SatuSehatEpisodeOfCareHelper.TYPE_CAD[1],
                    emptyToNull(r.kdPenyakit), emptyToNull(idCondition),
                    periodStart, emptyToNull(noKtpDokter),
                    r.status.isEmpty() ? "waitlist" : r.status);
            if (idEoc != null && !idEoc.isEmpty()) {
                log.append("EpisodeOfCare (CAD): ").append(idEoc).append("\n");
                SatuSehatEpisodeOfCareHelper.linkEncounter(r.noRawat, idEoc);
                recordSent(kon, idRegistrasi, "episodeofcare", "", idEoc);
                // Sinkronkan status bila registry sudah bergerak dari status awalnya.
                // 'finished' HANYA saat pasien meninggal — period.end diisi tgl meninggal.
                String periodEnd = "finished".equals(r.status) ? safeDate(r.tglMeninggal) : null;
                boolean ok = SatuSehatEpisodeOfCareHelper.patchStatus(idEoc, r.status, periodEnd);
                log.append("  status -> ").append(r.status).append(ok ? " (ok)\n" : " (gagal)\n");
            } else {
                log.append("EpisodeOfCare GAGAL dibuat.\n");
            }

            // ================= 3. Observation faktor risiko =================
            Faktor f = loadFaktor(kon, idRegistrasi, r.noRawat);
            if (f == null) {
                log.append("Faktor risiko belum diisi — Observation dilewati.\n");
            } else {
                kirimObsHipertensi(kon, api, mapper, link, log, idRegistrasi, idPasien, idEnc, idDokter, waktu, f);
                kirimObsDiabetes  (kon, api, mapper, link, log, idRegistrasi, idPasien, idEnc, idDokter, waktu, f);
                kirimObsDislipid  (kon, api, mapper, link, log, idRegistrasi, idPasien, idEnc, idDokter, waktu, f);
                kirimObsMerokok   (kon, api, mapper, link, log, idRegistrasi, idPasien, idEnc, idDokter, waktu, f);
            }

            // ================= 4. Observation target organ damage =================
            kirimObsTod(kon, api, mapper, link, log, idRegistrasi, r, idPasien, idEnc, idDokter, waktu);

            // ================= 5. Observation SKA (GRACE + Killip) =================
            kirimObsSka(kon, api, mapper, link, log, idRegistrasi, r, idPasien, idEnc, idDokter);

            // ================= 6. Observation PJR =================
            kirimObsPjr(kon, api, mapper, link, log, idRegistrasi, r, idPasien, idEnc, idDokter);

            // ================= 7. Procedure per tindakan =================
            kirimProcedure(kon, api, mapper, link, log, idRegistrasi, r, idPasien, idEnc, idDokter);

            log.append("\nSelesai.");
        } catch (Exception ex) {
            log.append("\nERROR: ").append(ex.getMessage());
            System.out.println("Notif SatuSehatKirimJantung : " + ex);
        }
        return log.toString();
    }

    // ==================================================================== Observation: faktor risiko

    private static void kirimObsHipertensi(Connection kon, ApiSatuSehat api, ObjectMapper mapper, String link,
            StringBuilder log, long idReg, String idPasien, String idEnc, String idDokter, String waktu, Faktor f) {
        if (f.htAda == null) return;
        if (!alreadySent(kon, idReg, "obs_ht", "").isEmpty()) { log.append("Obs hipertensi sudah terkirim.\n"); return; }

        Kode kode = lookupKode(kon, "jantung_observation", "hipertensi");
        if (kode == null) { log.append("Obs hipertensi dilewati: kode tidak ada di ref_registry_kode.\n"); return; }

        StringBuilder comp = new StringBuilder();
        Kode kDerajat = lookupKode(kon, "component_code", "ht_derajat");
        Kode vDerajat = f.htDerajat.isEmpty() ? null : lookupKode(kon, "ht_derajat_obs", f.htDerajat);
        if (kDerajat != null && vDerajat != null) tambah(comp, componentCodeable(kDerajat, vDerajat));
        tambahBool(kon, comp, "dapat_obat", f.htDapatObat);
        tambahBool(kon, comp, "terkontrol", f.htTerkontrol);

        String json = obsJson(kode, idPasien, idEnc, idDokter, waktu, "survey",
                "\"valueBoolean\":" + (f.htAda ? "true" : "false"), comp.toString());
        String id = postFhir(api, mapper, link, "Observation", json, log);
        if (!id.isEmpty()) recordSent(kon, idReg, "obs_ht", "", id);
    }

    private static void kirimObsDiabetes(Connection kon, ApiSatuSehat api, ObjectMapper mapper, String link,
            StringBuilder log, long idReg, String idPasien, String idEnc, String idDokter, String waktu, Faktor f) {
        if (f.dmAda == null) return;
        if (!alreadySent(kon, idReg, "obs_dm", "").isEmpty()) { log.append("Obs diabetes sudah terkirim.\n"); return; }

        Kode kode = lookupKode(kon, "jantung_observation", "dm");
        if (kode == null) { log.append("Obs diabetes dilewati: kode tidak ada di ref_registry_kode.\n"); return; }

        StringBuilder comp = new StringBuilder();
        tambahBool(kon, comp, "dapat_obat", f.dmDapatObat);
        tambahBool(kon, comp, "terkontrol", f.dmTerkontrol);

        String json = obsJson(kode, idPasien, idEnc, idDokter, waktu, "survey",
                "\"valueBoolean\":" + (f.dmAda ? "true" : "false"), comp.toString());
        String id = postFhir(api, mapper, link, "Observation", json, log);
        if (!id.isEmpty()) recordSent(kon, idReg, "obs_dm", "", id);
    }

    private static void kirimObsDislipid(Connection kon, ApiSatuSehat api, ObjectMapper mapper, String link,
            StringBuilder log, long idReg, String idPasien, String idEnc, String idDokter, String waktu, Faktor f) {
        if (f.dlAda == null) return;
        if (!alreadySent(kon, idReg, "obs_dl", "").isEmpty()) { log.append("Obs dislipidemia sudah terkirim.\n"); return; }

        Kode kode = lookupKode(kon, "jantung_observation", "dislipidemia");
        if (kode == null) { log.append("Obs dislipidemia dilewati: kode tidak ada di ref_registry_kode.\n"); return; }

        StringBuilder comp = new StringBuilder();
        tambahBool(kon, comp, "dapat_obat", f.dlDapatObat);
        // Komponen intensitas statin: kode komponen OC000180, nilainya OV000407-409
        Kode kStatin = lookupKode(kon, "component_code", "statin_intensitas");
        Kode vStatin = f.dlStatin.isEmpty() ? null : lookupKode(kon, "statin_intensitas", f.dlStatin);
        if (kStatin != null && vStatin != null) tambah(comp, componentCodeable(kStatin, vStatin));

        String json = obsJson(kode, idPasien, idEnc, idDokter, waktu, "survey",
                "\"valueBoolean\":" + (f.dlAda ? "true" : "false"), comp.toString());
        String id = postFhir(api, mapper, link, "Observation", json, log);
        if (!id.isEmpty()) recordSent(kon, idReg, "obs_dl", "", id);
    }

    private static void kirimObsMerokok(Connection kon, ApiSatuSehat api, ObjectMapper mapper, String link,
            StringBuilder log, long idReg, String idPasien, String idEnc, String idDokter, String waktu, Faktor f) {
        if (f.merokok.isEmpty()) return;
        if (!alreadySent(kon, idReg, "obs_merokok", "").isEmpty()) { log.append("Obs merokok sudah terkirim.\n"); return; }

        Kode kode  = lookupKode(kon, "jantung_observation", "merokok");
        Kode nilai = lookupKode(kon, "merokok", f.merokok);
        if (kode == null || nilai == null) { log.append("Obs merokok dilewati: kode tidak ada di ref_registry_kode.\n"); return; }

        String json = obsJson(kode, idPasien, idEnc, idDokter, waktu, "social-history", valueCodeable(nilai), "");
        String id = postFhir(api, mapper, link, "Observation", json, log);
        if (!id.isEmpty()) recordSent(kon, idReg, "obs_merokok", "", id);
    }

    // ==================================================================== Observation: TOD / SKA / PJR

    /** Target organ damage: valueCodeableConcept berkardinalitas 0..1, jadi satu Observation per kode. */
    private static void kirimObsTod(Connection kon, ApiSatuSehat api, ObjectMapper mapper, String link,
            StringBuilder log, long idReg, Reg r, String idPasien, String idEnc, String idDokter, String waktu) {
        Kode kode = lookupKode(kon, "jantung_observation", "tod");
        if (kode == null) return;
        int n = 0, ok = 0;
        try (PreparedStatement ps = kon.prepareStatement(
                "select kode from registrasi_jantung_tod where id_registrasi=? and no_rawat=?")) {
            ps.setLong(1, idReg);
            ps.setString(2, r.noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    n++;
                    String lokal = nz(rs.getString("kode"));
                    if (!alreadySent(kon, idReg, "obs_tod", lokal).isEmpty()) { ok++; continue; }
                    Kode nilai = lookupKode(kon, "jantung_tod", lokal);
                    if (nilai == null) continue;
                    String json = obsJson(kode, idPasien, idEnc, idDokter, waktu, "exam", valueCodeable(nilai), "");
                    String id = postFhir(api, mapper, link, "Observation", json, log);
                    if (!id.isEmpty()) { recordSent(kon, idReg, "obs_tod", lokal, id); ok++; }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif kirimObsTod : " + e);
        }
        if (n > 0) log.append("Obs target organ damage: ").append(ok).append("/").append(n).append(" terkirim.\n");
    }

    /** GRACE Score + Killip — wajib untuk jalur SKA menurut playbook. */
    private static void kirimObsSka(Connection kon, ApiSatuSehat api, ObjectMapper mapper, String link,
            StringBuilder log, long idReg, Reg r, String idPasien, String idEnc, String idDokter) {
        try (PreparedStatement ps = kon.prepareStatement(
                "select id_ska, tanggal, grace_skor, ifnull(grace_risiko,'') grace_risiko, ifnull(killip,'') killip "
              + "from registrasi_jantung_ska where id_registrasi=? order by id_ska")) {
            ps.setLong(1, idReg);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String idSka  = nz(rs.getString("id_ska"));
                    String waktu  = isoDariDatetime(nz(rs.getString("tanggal")));
                    String skor   = nz(rs.getString("grace_skor"));
                    String risiko = nz(rs.getString("grace_risiko"));
                    String killip = nz(rs.getString("killip"));

                    // GRACE Score — valueQuantity + interpretation
                    if (!skor.isEmpty() && alreadySent(kon, idReg, "obs_grace", idSka).isEmpty()) {
                        Kode kode = lookupKode(kon, "jantung_observation", "grace");
                        if (kode != null) {
                            Kode interp = risiko.isEmpty() ? null : lookupKode(kon, "jantung_grace_risiko", risiko);
                            StringBuilder extra = new StringBuilder();
                            extra.append("\"valueQuantity\":{\"value\":").append(esc(skor))
                                 .append(",\"unit\":\"{score}\",\"system\":\"http://unitsofmeasure.org\",\"code\":\"{score}\"}");
                            if (interp != null) {
                                extra.append(",\"interpretation\":[{\"coding\":[").append(codingJson(interp)).append("]}]");
                            }
                            String json = obsJson(kode, idPasien, idEnc, idDokter, waktu, "exam", extra.toString(), "");
                            String id = postFhir(api, mapper, link, "Observation", json, log);
                            if (!id.isEmpty()) recordSent(kon, idReg, "obs_grace", idSka, id);
                        }
                    }

                    // Killip — valueCodeableConcept
                    if (!killip.isEmpty() && alreadySent(kon, idReg, "obs_killip", idSka).isEmpty()) {
                        Kode kode  = lookupKode(kon, "jantung_observation", "killip");
                        Kode nilai = lookupKode(kon, "jantung_killip", killip);
                        if (kode != null && nilai != null) {
                            String json = obsJson(kode, idPasien, idEnc, idDokter, waktu, "exam", valueCodeable(nilai), "");
                            String id = postFhir(api, mapper, link, "Observation", json, log);
                            if (!id.isEmpty()) recordSent(kon, idReg, "obs_killip", idSka, id);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif kirimObsSka : " + e);
        }
    }

    /** Pencegahan sekunder PJR: nilai utama = keteraturan, komponen = jenis obat. */
    private static void kirimObsPjr(Connection kon, ApiSatuSehat api, ObjectMapper mapper, String link,
            StringBuilder log, long idReg, Reg r, String idPasien, String idEnc, String idDokter) {
        try (PreparedStatement ps = kon.prepareStatement(
                "select id_pjr, tanggal, ifnull(keteraturan,'') keteraturan, ifnull(jenis_obat,'') jenis_obat "
              + "from registrasi_jantung_pjr where id_registrasi=? order by id_pjr")) {
            ps.setLong(1, idReg);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String idPjr = nz(rs.getString("id_pjr"));
                    if (!alreadySent(kon, idReg, "obs_pjr", idPjr).isEmpty()) continue;
                    String ket = nz(rs.getString("keteraturan"));
                    if (ket.isEmpty()) continue;

                    Kode kode  = lookupKode(kon, "jantung_observation", "pjr");
                    Kode nilai = lookupKode(kon, "jantung_pjr_keteraturan", ket);
                    if (kode == null || nilai == null) continue;

                    StringBuilder comp = new StringBuilder();
                    String obat = nz(rs.getString("jenis_obat"));
                    Kode kObat = lookupKode(kon, "component_code", "pjr_jenis_obat");
                    Kode vObat = obat.isEmpty() ? null : lookupKode(kon, "jantung_pjr_obat", obat);
                    if (kObat != null && vObat != null) tambah(comp, componentCodeable(kObat, vObat));

                    String json = obsJson(kode, idPasien, idEnc, idDokter,
                            isoDariDatetime(nz(rs.getString("tanggal"))), "survey",
                            valueCodeable(nilai), comp.toString());
                    String id = postFhir(api, mapper, link, "Observation", json, log);
                    if (!id.isEmpty()) recordSent(kon, idReg, "obs_pjr", idPjr, id);
                }
            }
        } catch (Exception e) {
            System.out.println("Notif kirimObsPjr : " + e);
        }
    }

    // ==================================================================== Procedure

    private static void kirimProcedure(Connection kon, ApiSatuSehat api, ObjectMapper mapper, String link,
            StringBuilder log, long idReg, Reg r, String idPasien, String idEnc, String idDokter) {
        int n = 0, ok = 0;
        try (PreparedStatement ps = kon.prepareStatement(
                "select id_tindakan, kategori, kode_icd9, ifnull(deskripsi,'') deskripsi, tanggal "
              + "from registrasi_jantung_tindakan where id_registrasi=? order by id_tindakan")) {
            ps.setLong(1, idReg);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    n++;
                    String idTin = nz(rs.getString("id_tindakan"));
                    if (!alreadySent(kon, idReg, "procedure", idTin).isEmpty()) { ok++; continue; }
                    String json = buildProcedureJson(idPasien, idEnc, idDokter,
                            nz(rs.getString("kategori")), nz(rs.getString("kode_icd9")),
                            nz(rs.getString("deskripsi")), isoDariDatetime(nz(rs.getString("tanggal"))));
                    String id = postFhir(api, mapper, link, "Procedure", json, log);
                    if (!id.isEmpty()) { recordSent(kon, idReg, "procedure", idTin, id); ok++; }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif kirimProcedure : " + e);
        }
        if (n > 0) log.append("Procedure tindakan: ").append(ok).append("/").append(n).append(" terkirim.\n");
    }

    // ==================================================================== JSON builders

    private static String buildConditionJson(Connection kon, Reg r, String idPasien, String idEnc) {
        StringBuilder j = new StringBuilder();
        j.append("{\"resourceType\":\"Condition\"")
         .append(",\"clinicalStatus\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-clinical\",\"code\":\"active\"}]}")
         .append(",\"verificationStatus\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-ver-status\",\"code\":\"confirmed\"}]}")
         .append(",\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-category\",\"code\":\"problem-list-item\"}]}]")
         .append(",\"code\":{\"coding\":[{\"system\":\"http://hl7.org/fhir/sid/icd-10\",\"code\":\"").append(esc(r.kdPenyakit))
         .append("\",\"display\":\"").append(esc(r.icd10Nama.isEmpty() ? r.kdPenyakit : r.icd10Nama)).append("\"}]}");

        // Derajat hipertensi -> Condition.stage.summary.
        // PENTING: memakai kelompok 'ht_derajat_cond' (OI000042), BUKAN 'ht_derajat_obs'
        // (OC000042) — konsep sama, kode berbeda tergantung resource tujuan.
        Faktor f = loadFaktor(kon, r.idRegistrasi, r.noRawat);
        if (f != null && !f.htDerajat.isEmpty()) {
            Kode tipe  = lookupKode(kon, "component_code", "ht_derajat");
            Kode nilai = lookupKode(kon, "ht_derajat_cond", f.htDerajat);
            if (tipe != null && nilai != null) {
                j.append(",\"stage\":[{\"type\":{\"coding\":[").append(codingJson(tipe))
                 .append("]},\"summary\":{\"coding\":[").append(codingJson(nilai)).append("]}}]");
            }
        }

        // onsetDateTime — wajib untuk SKA ("Onset Keluhan Utama"), boleh mundur dari tgl kunjungan.
        String onset = !r.onsetKeluhan.isEmpty() ? isoDariDatetime(r.onsetKeluhan)
                     : (!r.tglDiagnosis.isEmpty() && !r.tglDiagnosis.startsWith("0000")
                        ? safeDate(r.tglDiagnosis) + "T00:00:00+07:00" : "");
        if (!onset.isEmpty()) j.append(",\"onsetDateTime\":\"").append(esc(onset)).append("\"");

        j.append(",\"subject\":{\"reference\":\"Patient/").append(esc(idPasien)).append("\"}")
         .append(",\"encounter\":{\"reference\":\"Encounter/").append(esc(idEnc)).append("\"}")
         .append("}");
        return j.toString();
    }

    /** Kerangka Observation yang dipakai semua observasi di modul ini. */
    private static String obsJson(Kode kode, String idPasien, String idEnc, String idDokter,
                                  String waktu, String kategori, String valueJson, String componentJson) {
        StringBuilder j = new StringBuilder();
        j.append("{\"resourceType\":\"Observation\",\"status\":\"final\"")
         .append(",\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/observation-category\",\"code\":\"")
         .append(esc(kategori)).append("\"}]}]")
         .append(",\"code\":{\"coding\":[").append(codingJson(kode)).append("]}")
         .append(",\"subject\":{\"reference\":\"Patient/").append(esc(idPasien)).append("\"}")
         .append(",\"encounter\":{\"reference\":\"Encounter/").append(esc(idEnc)).append("\"}");
        if (!waktu.isEmpty()) j.append(",\"effectiveDateTime\":\"").append(esc(waktu)).append("\"");
        if (idDokter != null && !idDokter.isEmpty())
            j.append(",\"performer\":[{\"reference\":\"Practitioner/").append(esc(idDokter)).append("\"}]");
        if (valueJson != null && !valueJson.isEmpty()) j.append(",").append(valueJson);
        if (componentJson != null && !componentJson.isEmpty()) j.append(",\"component\":[").append(componentJson).append("]");
        j.append("}");
        return j.toString();
    }

    private static String buildProcedureJson(String idPasien, String idEnc, String idDokter,
                                             String kategori, String kodeIcd9, String deskripsi, String waktu) {
        StringBuilder j = new StringBuilder();
        String[] kat = kategoriSnomed(kategori, kodeIcd9);
        j.append("{\"resourceType\":\"Procedure\",\"status\":\"completed\"")
         .append(",\"category\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"").append(kat[0])
         .append("\",\"display\":\"").append(kat[1]).append("\"}],\"text\":\"").append(kat[1]).append("\"}")
         .append(",\"code\":{\"coding\":[{\"system\":\"http://hl7.org/fhir/sid/icd-9-cm\",\"code\":\"").append(esc(kodeIcd9))
         .append("\",\"display\":\"").append(esc(deskripsi.isEmpty() ? kodeIcd9 : deskripsi)).append("\"}]}")
         .append(",\"subject\":{\"reference\":\"Patient/").append(esc(idPasien)).append("\"}")
         .append(",\"encounter\":{\"reference\":\"Encounter/").append(esc(idEnc)).append("\"}");
        if (!waktu.isEmpty()) j.append(",\"performedDateTime\":\"").append(esc(waktu)).append("\"");
        if (idDokter != null && !idDokter.isEmpty())
            j.append(",\"performer\":[{\"actor\":{\"reference\":\"Practitioner/").append(esc(idDokter)).append("\"}}]");
        j.append("}");
        return j.toString();
    }

    /**
     * Kategori SNOMED untuk Procedure jantung.
     *
     * TIDAK memakai SatuSehatKirimProcedure.snomedCategoryForIcd9(): fungsi itu
     * menurunkan kategori dari dua digit pertama ICD-9, sehingga 37.22 (kateterisasi
     * jantung kiri) jatuh ke "Surgical procedure" padahal tindakan diagnostik.
     * Di sini kategori diambil dari kolom `kategori` yang diisi user, dengan ICD-9
     * hanya sebagai cadangan.
     */
    private static String[] kategoriSnomed(String kategori, String kodeIcd9) {
        if ("diagnostik".equals(kategori))  return new String[]{"103693007", "Diagnostic procedure"};
        if ("reperfusi".equals(kategori)
         || "invasif_pjr".equals(kategori)) return new String[]{"387713003", "Surgical procedure"};
        if ("iskemik_stabil".equals(kategori)) return new String[]{"277132007", "Therapeutic procedure"};
        // Cadangan: turunkan dari ICD-9 bila kategori tidak dikenali
        try {
            int bab = Integer.parseInt(kodeIcd9.substring(0, 2));
            if (bab <= 86) return new String[]{"387713003", "Surgical procedure"};
            if (bab <= 89) return new String[]{"103693007", "Diagnostic procedure"};
        } catch (Exception ignored) { }
        return new String[]{"277132007", "Therapeutic procedure"};
    }

    // ==================================================================== Terminologi

    /** Satu baris ref_registry_kode. */
    private static final class Kode {
        String system, code, display;
    }

    /**
     * Ambil kode terminologi dari tabel ref_registry_kode.
     * Sengaja dari DB, bukan konstanta Java: kode kemkes belum final, sehingga
     * koreksi cukup UPDATE tanpa rebuild aplikasi.
     */
    private static Kode lookupKode(Connection kon, String kelompok, String kodeLokal) {
        if (kodeLokal == null || kodeLokal.isEmpty()) return null;
        try (PreparedStatement ps = kon.prepareStatement(
                "select system, code, display from ref_registry_kode "
              + "where kelompok=? and kode=? and aktif=1 limit 1")) {
            ps.setString(1, kelompok);
            ps.setString(2, kodeLokal);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Kode k = new Kode();
                k.system  = nz(rs.getString("system"));
                k.code    = nz(rs.getString("code"));
                k.display = nz(rs.getString("display"));
                return k;
            }
        } catch (Exception e) {
            System.out.println("Notif lookupKode " + kelompok + "/" + kodeLokal + " : " + e);
            return null;
        }
    }

    private static String codingJson(Kode k) {
        return "{\"system\":\"" + esc(k.system) + "\",\"code\":\"" + esc(k.code)
             + "\",\"display\":\"" + esc(k.display) + "\"}";
    }

    private static String valueCodeable(Kode k) {
        return "\"valueCodeableConcept\":{\"coding\":[" + codingJson(k) + "]}";
    }

    private static String componentCodeable(Kode kodeKomponen, Kode nilai) {
        return "{\"code\":{\"coding\":[" + codingJson(kodeKomponen) + "]},"
             + "\"valueCodeableConcept\":{\"coding\":[" + codingJson(nilai) + "]}}";
    }

    private static void tambah(StringBuilder sb, String bagian) {
        if (sb.length() > 0) sb.append(",");
        sb.append(bagian);
    }

    /** Komponen ber-valueBoolean (mis. "Does obtain medication", "Controlled"). */
    private static void tambahBool(Connection kon, StringBuilder comp, String kodeKomponen, Boolean nilai) {
        if (nilai == null) return;
        Kode k = lookupKode(kon, "component_code", kodeKomponen);
        if (k == null) return;
        tambah(comp, "{\"code\":{\"coding\":[" + codingJson(k) + "]},\"valueBoolean\":" + (nilai ? "true" : "false") + "}");
    }

    // ==================================================================== HTTP

    private static String postFhir(ApiSatuSehat api, ObjectMapper mapper, String link,
                                   String resource, String json, StringBuilder log) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + api.getValidToken());
            System.out.println("POST " + link + "/" + resource + " : " + json);
            HttpEntity<String> req = new HttpEntity<>(json, headers);
            String resp = api.getRest().exchange(link + "/" + resource, HttpMethod.POST, req, String.class).getBody();
            System.out.println("RESP " + resource + " : " + resp);
            // asText() tanpa argumen: overload asText(String) baru ada di Jackson 2.4+,
            // sedangkan classpath build project ini di bawah itu.
            String id = mapper.readTree(resp).path("id").asText();
            if (id.isEmpty()) log.append(resource).append(" GAGAL: ").append(ringkas(resp)).append("\n");
            else log.append(resource).append(": ").append(id).append("\n");
            return id;
        } catch (Exception e) {
            String detail = (e instanceof org.springframework.web.client.HttpStatusCodeException)
                    ? " => " + ((org.springframework.web.client.HttpStatusCodeException) e).getResponseBodyAsString() : "";
            log.append(resource).append(" ERROR: ").append(e.getMessage()).append("\n");
            System.out.println("Notif postFhir " + resource + " : " + e + detail);
            return "";
        }
    }

    // ==================================================================== DB helpers

    private static String alreadySent(Connection kon, long idReg, String jenis, String refLokal) {
        try (PreparedStatement ps = kon.prepareStatement(
                "select id_fhir from satu_sehat_jantung_kirim where id_registrasi=? and jenis=? and ref_lokal=?")) {
            ps.setLong(1, idReg);
            ps.setString(2, jenis);
            ps.setString(3, refLokal == null ? "" : refLokal);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? nz(rs.getString(1)) : ""; }
        } catch (Exception e) { return ""; }
    }

    private static void recordSent(Connection kon, long idReg, String jenis, String refLokal, String idFhir) {
        try (PreparedStatement ps = kon.prepareStatement(
                "insert into satu_sehat_jantung_kirim (id_registrasi,jenis,ref_lokal,id_fhir) values (?,?,?,?) "
              + "on duplicate key update id_fhir=values(id_fhir), tgl_kirim=current_timestamp")) {
            ps.setLong(1, idReg);
            ps.setString(2, jenis);
            ps.setString(3, refLokal == null ? "" : refLokal);
            ps.setString(4, idFhir);
            ps.executeUpdate();
        } catch (Exception e) { System.out.println("Notif recordSent : " + e); }
    }

    private static String scalar(Connection kon, String sql, String p1) {
        try (PreparedStatement ps = kon.prepareStatement(sql)) {
            ps.setString(1, p1);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? nz(rs.getString(1)) : ""; }
        } catch (Exception e) { return ""; }
    }

    // ==================================================================== Model

    private static final class Reg {
        long idRegistrasi;
        String noRkm = "", noRawat = "", nama = "", tglRegistrasi = "", jalur = "";
        String kdPenyakit = "", icd10Nama = "", tglDiagnosis = "", onsetKeluhan = "";
        String status = "", kdDokter = "", tglMeninggal = "";
    }

    private static final class Faktor {
        Boolean htAda, htDapatObat, htTerkontrol;
        Boolean dmAda, dmDapatObat, dmTerkontrol;
        Boolean dlAda, dlDapatObat;
        String htDerajat = "", dlStatin = "", merokok = "", tanggal = "";
    }

    private static Reg loadReg(Connection kon, long idReg) {
        try (PreparedStatement ps = kon.prepareStatement(
                "select rj.*, ifnull(p.nm_pasien,'') nm_pasien, ifnull(pk.nm_penyakit,'') icd10nm "
              + "from registrasi_jantung rj "
              + "left join pasien p on p.no_rkm_medis=rj.no_rkm_medis "
              + "left join penyakit pk on pk.kd_penyakit=rj.kd_penyakit "
              + "where rj.id_registrasi=?")) {
            ps.setLong(1, idReg);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Reg r = new Reg();
                r.idRegistrasi  = idReg;
                r.noRkm         = nz(rs.getString("no_rkm_medis"));
                r.noRawat       = nz(rs.getString("no_rawat"));
                r.nama          = nz(rs.getString("nm_pasien"));
                r.tglRegistrasi = nz(rs.getString("tgl_registrasi"));
                r.jalur         = nz(rs.getString("jalur"));
                r.kdPenyakit    = nz(rs.getString("kd_penyakit"));
                r.icd10Nama     = nz(rs.getString("icd10nm"));
                r.tglDiagnosis  = nz(rs.getString("tgl_diagnosis"));
                r.onsetKeluhan  = nz(rs.getString("onset_keluhan"));
                r.status        = nz(rs.getString("status"));
                r.kdDokter      = nz(rs.getString("kd_dokter"));
                r.tglMeninggal  = nz(rs.getString("tgl_meninggal"));
                return r;
            }
        } catch (Exception e) {
            System.out.println("Notif loadReg : " + e);
            return null;
        }
    }

    /** Faktor risiko untuk kunjungan ini; bila tidak ada, pakai yang terbaru. */
    private static Faktor loadFaktor(Connection kon, long idReg, String noRawat) {
        Faktor f = loadFaktorSql(kon,
                "select * from registrasi_jantung_faktor_risiko where id_registrasi=? and no_rawat=? "
              + "order by tanggal desc limit 1", idReg, noRawat);
        if (f != null) return f;
        return loadFaktorSql(kon,
                "select * from registrasi_jantung_faktor_risiko where id_registrasi=? "
              + "order by tanggal desc limit 1", idReg, null);
    }

    private static Faktor loadFaktorSql(Connection kon, String sql, long idReg, String noRawat) {
        try (PreparedStatement ps = kon.prepareStatement(sql)) {
            ps.setLong(1, idReg);
            if (noRawat != null) ps.setString(2, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Faktor f = new Faktor();
                f.tanggal      = nz(rs.getString("tanggal"));
                f.htAda        = bool(rs, "ht_ada");
                f.htDerajat    = nz(rs.getString("ht_derajat"));
                f.htDapatObat  = bool(rs, "ht_dapat_obat");
                f.htTerkontrol = bool(rs, "ht_terkontrol");
                f.dmAda        = bool(rs, "dm_ada");
                f.dmDapatObat  = bool(rs, "dm_dapat_obat");
                f.dmTerkontrol = bool(rs, "dm_terkontrol");
                f.dlAda        = bool(rs, "dl_ada");
                f.dlDapatObat  = bool(rs, "dl_dapat_obat");
                f.dlStatin     = nz(rs.getString("dl_statin"));
                f.merokok      = nz(rs.getString("merokok"));
                return f;
            }
        } catch (Exception e) {
            System.out.println("Notif loadFaktor : " + e);
            return null;
        }
    }

    /** NULL di DB berarti "belum dinilai", dibedakan dari false. */
    private static Boolean bool(ResultSet rs, String kolom) {
        try {
            boolean v = rs.getBoolean(kolom);
            return rs.wasNull() ? null : v;
        } catch (Exception e) { return null; }
    }

    // ==================================================================== Util

    private static String efektif(Reg r) {
        if (!r.onsetKeluhan.isEmpty()) return isoDariDatetime(r.onsetKeluhan);
        String d = firstNonEmpty(safeDate(r.tglDiagnosis), safeDate(r.tglRegistrasi));
        return d.isEmpty() ? "" : d + "T00:00:00+07:00";
    }

    /** "2026-07-19 08:30:00" -> "2026-07-19T08:30:00+07:00" */
    private static String isoDariDatetime(String dt) {
        if (dt == null || dt.isEmpty() || dt.startsWith("0000")) return "";
        String s = dt.trim().replace(' ', 'T');
        if (s.length() == 10) s = s + "T00:00:00";
        if (s.length() == 16) s = s + ":00";
        return s + "+07:00";
    }

    private static String safeDate(String d) {
        if (d == null) return "";
        String s = d.trim();
        if (s.isEmpty() || s.startsWith("0000")) return "";
        return s.length() >= 10 ? s.substring(0, 10) : s;
    }

    private static String firstNonEmpty(String a, String b) {
        return (a != null && !a.isEmpty()) ? a : nz(b);
    }

    private static String emptyToNull(String s) {
        return (s == null || s.isEmpty()) ? null : s;
    }

    private static String nz(String s) { return s == null ? "" : s; }

    private static String ringkas(String s) {
        if (s == null) return "";
        String t = s.replaceAll("\\s+", " ").trim();
        return t.length() > 300 ? t.substring(0, 300) + "..." : t;
    }

    /** Escape nilai yang disisipkan ke JSON literal yang dirangkai manual. */
    private static String esc(Object v) {
        if (v == null) return "";
        String s = v.toString();
        StringBuilder sb = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' : sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else          sb.append(c);
            }
        }
        return sb.toString();
    }
}
