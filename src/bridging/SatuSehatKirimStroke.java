/*
 * Orkestrator pengiriman Registrasi Stroke ke SatuSehat (FHIR R4) — Fase 2.
 *
 * Urutan per registrasi (idempoten via tabel satu_sehat_stroke_kirim):
 *   1. Prasyarat   : Encounter kunjungan sudah terkirim.
 *   2. Condition   : stroke (clinicalStatus active / recurrence) + onsetDateTime.
 *   3. Condition   : riwayat penyakit (Lampiran 1), satu resource per kode.
 *   4. EpisodeOfCare (CVD) : getOrCreate berstatus awal + link Encounter.
 *   5. Observation faktor risiko : hipertensi, DM, dislipidemia, merokok
 *                    (kode DIPAKAI BERSAMA dengan modul jantung) + alkohol,
 *                    KB hormonal, riwayat keluarga.
 *   6. Observation skor : NIHSS & mRS, SATU RESOURCE PER FASE.
 *   7. Observation lain : wake-up stroke, disfagia, ASPECTS, hasil CT/CTA.
 *   8. Observation tindakan : TICI, time to recanalization.
 *   9. Procedure   : rTPA, trombektomi, rehabilitasi.
 *
 * KUNCI IDEMPOTENSI SKOR WAJIB MENYERTAKAN FASE.
 *   NIHSS diukur di 4 titik waktu dan mRS di 5, tetapi Observation.code-nya
 *   IDENTIK — titik waktu hanya dibedakan effectiveDateTime. Kalau ref_lokal
 *   dikosongkan, pengukuran kedua akan dianggap sudah terkirim dan hilang
 *   tanpa pesan apa pun. Konvensi: jenis='obs_nihss', ref_lokal='<fase>'.
 *
 * TERMINOLOGI TIDAK DI-HARDCODE — semua dari ref_registry_kode.
 * Lihat docs/registrasi-stroke-satusehat.md.
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

public final class SatuSehatKirimStroke {

    private SatuSehatKirimStroke() { }

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

            String noKtpPasien = scalar(kon, "select ifnull(no_ktp,'') from pasien where no_rkm_medis=? limit 1", r.noRkm);
            if (noKtpPasien.isEmpty()) return "Pasien tidak punya NIK — wajib untuk lookup IHS SatuSehat.";
            String idPasien = cek.tampilIDPasien(noKtpPasien);
            if (idPasien == null || idPasien.isEmpty()) return "Pasien belum ter-IHS di SatuSehat (Patient ID kosong).";
            log.append("IHS Pasien: ").append(idPasien).append("\n");

            String idDokter = r.kdDokter.isEmpty() ? "" : cek.tampilIDParktisi(r.kdDokter);

            String idEnc = scalar(kon, "select ifnull(id_encounter,'') from satu_sehat_encounter where no_rawat=? limit 1", r.noRawat);
            if (idEnc.isEmpty())
                return "Encounter untuk No.Rawat " + r.noRawat + " belum dikirim ke SatuSehat.\n"
                     + "Kirim Encounter dulu (menu Kirim Encounter Satu Sehat), baru Registrasi Stroke.";
            log.append("Encounter: ").append(idEnc).append("\n");

            String waktuIndeks = efektif(r);

            // ================= 1. Condition stroke =================
            String idCondition = alreadySent(kon, idRegistrasi, "condition", "");
            if (idCondition.isEmpty()) {
                if (r.kdPenyakit.isEmpty()) {
                    log.append("Condition dilewati: kode ICD-10 belum diisi.\n");
                } else {
                    idCondition = postFhir(api, mapper, link, "Condition",
                            buildConditionStroke(r, idPasien, idEnc), log);
                    if (!idCondition.isEmpty()) recordSent(kon, idRegistrasi, "condition", "", idCondition);
                }
            } else {
                log.append("Condition sudah terkirim (").append(idCondition).append(").\n");
            }

            // ================= 2. Condition riwayat penyakit =================
            kirimRiwayat(kon, api, mapper, link, log, idRegistrasi, idPasien, idEnc);

            // ================= 3. EpisodeOfCare (CVD) =================
            String periodStart = firstNonEmpty(safeDate(r.tglDiagnosis), safeDate(r.tglRegistrasi));
            String idEoc = SatuSehatEpisodeOfCareHelper.getOrCreate(
                    r.noRkm, idPasien, r.nama,
                    SatuSehatEpisodeOfCareHelper.TYPE_CVD[0],
                    SatuSehatEpisodeOfCareHelper.TYPE_CVD[1],
                    emptyToNull(r.kdPenyakit), emptyToNull(idCondition),
                    periodStart, emptyToNull(r.kdDokter),
                    r.status.isEmpty() ? "waitlist" : r.status);
            if (idEoc != null && !idEoc.isEmpty()) {
                log.append("EpisodeOfCare (CVD): ").append(idEoc).append("\n");
                SatuSehatEpisodeOfCareHelper.linkEncounter(r.noRawat, idEoc);
                recordSent(kon, idRegistrasi, "episodeofcare", "", idEoc);
                String periodEnd = "finished".equals(r.status) ? safeDate(r.tglMeninggal) : null;
                boolean ok = SatuSehatEpisodeOfCareHelper.patchStatus(idEoc, r.status, periodEnd);
                log.append("  status -> ").append(r.status).append(ok ? " (ok)\n" : " (gagal)\n");
            } else {
                log.append("EpisodeOfCare GAGAL dibuat.\n");
            }

            // ================= 4. Observation faktor risiko =================
            kirimFaktorRisiko(kon, api, mapper, link, log, idRegistrasi, r, idPasien, idEnc, idDokter, waktuIndeks);

            // ================= 5. Observation skor berfase =================
            kirimSkor(kon, api, mapper, link, log, idRegistrasi, idPasien, idEnc, idDokter);

            // ================= 6. Observation lain =================
            kirimWakeUp(kon, api, mapper, link, log, idRegistrasi, r, idPasien, idEnc, idDokter);
            kirimDisfagia(kon, api, mapper, link, log, idRegistrasi, idPasien, idEnc, idDokter);
            kirimPencitraan(kon, api, mapper, link, log, idRegistrasi, idPasien, idEnc, idDokter);

            // ================= 7. Tindakan + observasi terkait =================
            kirimTindakan(kon, api, mapper, link, log, idRegistrasi, idPasien, idEnc, idDokter);

            log.append("\nSelesai.");
        } catch (Exception ex) {
            log.append("\nERROR: ").append(ex.getMessage());
            System.out.println("Notif SatuSehatKirimStroke : " + ex);
        }
        return log.toString();
    }

    // ==================================================================== Condition

    private static String buildConditionStroke(Reg r, String idPasien, String idEnc) {
        // Serangan ke-2 dan seterusnya memakai clinicalStatus 'recurrence',
        // bukan 'active' — registry longitudinal merekam stroke berulang.
        String clinical = r.seranganKe > 1 ? "recurrence" : "active";
        StringBuilder j = new StringBuilder();
        j.append("{\"resourceType\":\"Condition\"")
         .append(",\"clinicalStatus\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-clinical\",\"code\":\"")
         .append(clinical).append("\"}]}")
         .append(",\"verificationStatus\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-ver-status\",\"code\":\"confirmed\"}]}")
         .append(",\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-category\",\"code\":\"problem-list-item\"}]}]")
         .append(",\"code\":{\"coding\":[{\"system\":\"http://hl7.org/fhir/sid/icd-10\",\"code\":\"").append(esc(r.kdPenyakit))
         .append("\",\"display\":\"").append(esc(r.icd10Nama.isEmpty() ? r.kdPenyakit : r.icd10Nama)).append("\"}]}");
        String onset = !r.onsetSerangan.isEmpty() ? isoDariDatetime(r.onsetSerangan)
                     : (!safeDate(r.tglDiagnosis).isEmpty() ? safeDate(r.tglDiagnosis) + "T00:00:00+07:00" : "");
        if (!onset.isEmpty()) j.append(",\"onsetDateTime\":\"").append(esc(onset)).append("\"");
        j.append(",\"subject\":{\"reference\":\"Patient/").append(esc(idPasien)).append("\"}")
         .append(",\"encounter\":{\"reference\":\"Encounter/").append(esc(idEnc)).append("\"}")
         .append("}");
        return j.toString();
    }

    /** Riwayat penyakit (Lampiran 1) — Condition tersendiri per kode, kategori problem-list-item. */
    private static void kirimRiwayat(Connection kon, ApiSatuSehat api, ObjectMapper mapper, String link,
            StringBuilder log, long idReg, String idPasien, String idEnc) {
        int n = 0, ok = 0;
        try (PreparedStatement ps = kon.prepareStatement(
                "select kode from registrasi_stroke_riwayat where id_registrasi=?")) {
            ps.setLong(1, idReg);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    n++;
                    String lokal = nz(rs.getString("kode"));
                    if (!alreadySent(kon, idReg, "condition_riwayat", lokal).isEmpty()) { ok++; continue; }
                    Kode k = lookupKode(kon, "stroke_riwayat", lokal);
                    if (k == null) continue;
                    String json = "{\"resourceType\":\"Condition\""
                            + ",\"clinicalStatus\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-clinical\",\"code\":\"active\"}]}"
                            + ",\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-category\",\"code\":\"problem-list-item\"}]}]"
                            + ",\"code\":{\"coding\":[" + codingJson(k) + "]}"
                            + ",\"subject\":{\"reference\":\"Patient/" + esc(idPasien) + "\"}"
                            + ",\"encounter\":{\"reference\":\"Encounter/" + esc(idEnc) + "\"}}";
                    String id = postFhir(api, mapper, link, "Condition", json, log);
                    if (!id.isEmpty()) { recordSent(kon, idReg, "condition_riwayat", lokal, id); ok++; }
                }
            }
        } catch (Exception e) { System.out.println("Notif kirimRiwayat : " + e); }
        if (n > 0) log.append("Condition riwayat: ").append(ok).append("/").append(n).append(" terkirim.\n");
    }

    // ==================================================================== Faktor risiko

    private static void kirimFaktorRisiko(Connection kon, ApiSatuSehat api, ObjectMapper mapper, String link,
            StringBuilder log, long idReg, Reg r, String idPasien, String idEnc, String idDokter, String waktu) {
        Faktor f = loadFaktor(kon, idReg, r.noRawat);
        if (f == null) { log.append("Faktor risiko belum diisi — Observation dilewati.\n"); return; }

        // Empat pertama memakai kelompok BERSAMA dengan modul jantung.
        obsBool(kon, api, mapper, link, log, idReg, "obs_ht", "hipertensi", f.htAda, idPasien, idEnc, idDokter, waktu,
                komponenHt(kon, f));
        obsBool(kon, api, mapper, link, log, idReg, "obs_dm", "dm", f.dmAda, idPasien, idEnc, idDokter, waktu,
                komponenBool(kon, "dapat_obat", f.dmDapatObat) + gabung(komponenBool(kon, "terkontrol", f.dmTerkontrol)));
        obsBool(kon, api, mapper, link, log, idReg, "obs_dl", "dislipidemia", f.dlAda, idPasien, idEnc, idDokter, waktu,
                komponenDl(kon, f));

        // Merokok — valueCodeableConcept
        if (!f.merokok.isEmpty() && alreadySent(kon, idReg, "obs_merokok", "").isEmpty()) {
            Kode kode = lookupKode(kon, "stroke_observation", "merokok_kode");
            if (kode == null) kode = lookupKode(kon, "jantung_observation", "merokok"); // kode LOINC sama
            Kode nilai = lookupKode(kon, "merokok", f.merokok);
            if (kode != null && nilai != null) {
                String id = postFhir(api, mapper, link, "Observation",
                        obsJson(kode, idPasien, idEnc, idDokter, waktu, "social-history", valueCodeable(nilai), ""), log);
                if (!id.isEmpty()) recordSent(kon, idReg, "obs_merokok", "", id);
            }
        }

        // Khas stroke
        obsBool(kon, api, mapper, link, log, idReg, "obs_alkohol", "alkohol", f.alkohol, idPasien, idEnc, idDokter, waktu, "");
        obsBool(kon, api, mapper, link, log, idReg, "obs_kb", "kb_hormonal", f.kbHormonal, idPasien, idEnc, idDokter, waktu, "");
        obsBool(kon, api, mapper, link, log, idReg, "obs_riwayat_keluarga", "riwayat_keluarga",
                f.riwayatKeluarga, idPasien, idEnc, idDokter, waktu, "");
    }

    private static String komponenHt(Connection kon, Faktor f) {
        StringBuilder c = new StringBuilder();
        Kode kD = lookupKode(kon, "component_code", "ht_derajat");
        Kode vD = f.htDerajat.isEmpty() ? null : lookupKode(kon, "ht_derajat_obs", f.htDerajat);
        if (kD != null && vD != null) c.append(componentCodeable(kD, vD));
        c.append(gabung(komponenBool(kon, "dapat_obat", f.htDapatObat)));
        c.append(gabung(komponenBool(kon, "terkontrol", f.htTerkontrol)));
        return bersihkanKoma(c.toString());
    }

    private static String komponenDl(Connection kon, Faktor f) {
        StringBuilder c = new StringBuilder();
        c.append(komponenBool(kon, "dapat_obat", f.dlDapatObat));
        Kode kS = lookupKode(kon, "component_code", "statin_intensitas");
        Kode vS = f.dlStatin.isEmpty() ? null : lookupKode(kon, "statin_intensitas", f.dlStatin);
        if (kS != null && vS != null) c.append(gabung(componentCodeable(kS, vS)));
        return bersihkanKoma(c.toString());
    }

    // ==================================================================== Skor berfase

    /**
     * NIHSS & mRS — satu Observation per fase.
     * ref_lokal diisi fase; tanpa itu pengukuran kedua dianggap sudah terkirim.
     */
    private static void kirimSkor(Connection kon, ApiSatuSehat api, ObjectMapper mapper, String link,
            StringBuilder log, long idReg, String idPasien, String idEnc, String idDokter) {
        int n = 0, ok = 0;
        try (PreparedStatement ps = kon.prepareStatement(
                "select instrumen, fase, tanggal, nilai_angka, ifnull(nilai_kode,'') nilai_kode, "
              + "ifnull(interpretasi,'') interpretasi from registrasi_stroke_skor "
              + "where id_registrasi=? order by instrumen, tanggal")) {
            ps.setLong(1, idReg);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    n++;
                    String instrumen = nz(rs.getString("instrumen"));
                    String fase      = nz(rs.getString("fase"));
                    String jenis     = "nihss".equals(instrumen) ? "obs_nihss" : "obs_mrs";
                    if (!alreadySent(kon, idReg, jenis, fase).isEmpty()) { ok++; continue; }

                    Kode kode = lookupKode(kon, "stroke_observation", instrumen);
                    if (kode == null) continue;
                    String waktu = isoDariDatetime(nz(rs.getString("tanggal")));
                    String value;

                    if ("nihss".equals(instrumen)) {
                        String angka = nz(rs.getString("nilai_angka"));
                        if (angka.isEmpty()) continue;
                        StringBuilder v = new StringBuilder();
                        v.append("\"valueQuantity\":{\"value\":").append(esc(angka))
                         .append(",\"unit\":\"{score}\",\"system\":\"http://unitsofmeasure.org\",\"code\":\"{score}\"}");
                        Kode interp = lookupKode(kon, "stroke_nihss_interpretasi", nz(rs.getString("interpretasi")));
                        if (interp != null)
                            v.append(",\"interpretation\":[{\"coding\":[").append(codingJson(interp)).append("]}]");
                        value = v.toString();
                    } else {
                        Kode nilai = lookupKode(kon, "stroke_mrs", nz(rs.getString("nilai_kode")));
                        if (nilai == null) continue;
                        value = valueCodeable(nilai);
                    }

                    String id = postFhir(api, mapper, link, "Observation",
                            obsJson(kode, idPasien, idEnc, idDokter, waktu, "exam", value, ""), log);
                    if (!id.isEmpty()) { recordSent(kon, idReg, jenis, fase, id); ok++; }
                }
            }
        } catch (Exception e) { System.out.println("Notif kirimSkor : " + e); }
        if (n > 0) log.append("Skor NIHSS/mRS: ").append(ok).append("/").append(n).append(" terkirim (per fase).\n");
    }

    // ==================================================================== Observasi lain

    private static void kirimWakeUp(Connection kon, ApiSatuSehat api, ObjectMapper mapper, String link,
            StringBuilder log, long idReg, Reg r, String idPasien, String idEnc, String idDokter) {
        if (r.wakeUpStroke.isEmpty()) return;
        if (!alreadySent(kon, idReg, "obs_wakeup", "").isEmpty()) return;
        Kode kode = lookupKode(kon, "stroke_observation", "wake_up");
        if (kode == null) return;
        String waktu = isoDariDatetime(r.wakeUpStroke);
        String json = obsJson(kode, idPasien, idEnc, idDokter, waktu, "survey",
                "\"valueDateTime\":\"" + esc(waktu) + "\"", "");
        String id = postFhir(api, mapper, link, "Observation", json, log);
        if (!id.isEmpty()) recordSent(kon, idReg, "obs_wakeup", "", id);
    }

    private static void kirimDisfagia(Connection kon, ApiSatuSehat api, ObjectMapper mapper, String link,
            StringBuilder log, long idReg, String idPasien, String idEnc, String idDokter) {
        try (PreparedStatement ps = kon.prepareStatement(
                "select hasil, tanggal from registrasi_stroke_disfagia where id_registrasi=? limit 1")) {
            ps.setLong(1, idReg);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return;
                if (!alreadySent(kon, idReg, "obs_disfagia", "").isEmpty()) return;
                Kode kode  = lookupKode(kon, "stroke_observation", "disfagia");
                Kode nilai = lookupKode(kon, "stroke_disfagia", nz(rs.getString("hasil")));
                if (kode == null || nilai == null) return;
                String json = obsJson(kode, idPasien, idEnc, idDokter,
                        isoDariDatetime(nz(rs.getString("tanggal"))), "exam", valueCodeable(nilai), "");
                String id = postFhir(api, mapper, link, "Observation", json, log);
                if (!id.isEmpty()) recordSent(kon, idReg, "obs_disfagia", "", id);
            }
        } catch (Exception e) { System.out.println("Notif kirimDisfagia : " + e); }
    }

    /** ASPECTS (valueQuantity) + hasil CT/CTA (valueCodeableConcept). */
    private static void kirimPencitraan(Connection kon, ApiSatuSehat api, ObjectMapper mapper, String link,
            StringBuilder log, long idReg, String idPasien, String idEnc, String idDokter) {
        try (PreparedStatement ps = kon.prepareStatement(
                "select id_citra, modalitas, ifnull(kode_modalitas,'') kode_modalitas, "
              + "ifnull(hasil_kode,'') hasil_kode, aspects, tanggal "
              + "from registrasi_stroke_pencitraan where id_registrasi=? order by id_citra")) {
            ps.setLong(1, idReg);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String idCitra = nz(rs.getString("id_citra"));
                    String modal   = nz(rs.getString("modalitas"));
                    String waktu   = isoDariDatetime(nz(rs.getString("tanggal")));

                    // ASPECTS
                    String aspects = nz(rs.getString("aspects"));
                    if (!aspects.isEmpty() && alreadySent(kon, idReg, "obs_aspects", idCitra).isEmpty()) {
                        Kode kode = lookupKode(kon, "stroke_observation", "aspects");
                        if (kode != null) {
                            String v = "\"valueQuantity\":{\"value\":" + esc(aspects)
                                     + ",\"unit\":\"{score}\",\"system\":\"http://unitsofmeasure.org\",\"code\":\"{score}\"}";
                            String id = postFhir(api, mapper, link, "Observation",
                                    obsJson(kode, idPasien, idEnc, idDokter, waktu, "exam", v, ""), log);
                            if (!id.isEmpty()) recordSent(kon, idReg, "obs_aspects", idCitra, id);
                        }
                    }

                    // Hasil CT / CTA. Observation.code = kode modalitas (LOINC),
                    // value = temuan (SNOMED).
                    String hasil = nz(rs.getString("hasil_kode"));
                    if (hasil.isEmpty() || "mri".equals(modal)) continue;
                    if (!alreadySent(kon, idReg, "obs_citra", idCitra).isEmpty()) continue;
                    Kode kode  = lookupKode(kon, "stroke_" + modal + "_kode", nz(rs.getString("kode_modalitas")));
                    Kode nilai = lookupKode(kon, "stroke_" + modal + "_hasil", hasil);
                    if (kode == null || nilai == null) continue;
                    String id = postFhir(api, mapper, link, "Observation",
                            obsJson(kode, idPasien, idEnc, idDokter, waktu, "imaging", valueCodeable(nilai), ""), log);
                    if (!id.isEmpty()) recordSent(kon, idReg, "obs_citra", idCitra, id);
                }
            }
        } catch (Exception e) { System.out.println("Notif kirimPencitraan : " + e); }
    }

    // ==================================================================== Tindakan

    private static void kirimTindakan(Connection kon, ApiSatuSehat api, ObjectMapper mapper, String link,
            StringBuilder log, long idReg, String idPasien, String idEnc, String idDokter) {
        int n = 0, ok = 0;
        try (PreparedStatement ps = kon.prepareStatement(
                "select id_tindakan, jenis, ifnull(kode,'') kode, waktu, "
              + "ifnull(waktu_rekanalisasi,'') waktu_rekanalisasi, ifnull(tici,'') tici "
              + "from registrasi_stroke_tindakan where id_registrasi=? order by id_tindakan")) {
            ps.setLong(1, idReg);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    n++;
                    String idTin = nz(rs.getString("id_tindakan"));
                    String jenis = nz(rs.getString("jenis"));
                    String waktu = isoDariDatetime(nz(rs.getString("waktu")));

                    if (alreadySent(kon, idReg, "procedure", idTin).isEmpty()) {
                        String json = buildProcedureJson(kon, jenis, nz(rs.getString("kode")),
                                idPasien, idEnc, idDokter, waktu);
                        if (json != null) {
                            String id = postFhir(api, mapper, link, "Procedure", json, log);
                            if (!id.isEmpty()) { recordSent(kon, idReg, "procedure", idTin, id); ok++; }
                        }
                    } else { ok++; }

                    // Time to recanalization — valueDateTime
                    String wr = nz(rs.getString("waktu_rekanalisasi"));
                    if (!wr.isEmpty() && alreadySent(kon, idReg, "obs_rekanalisasi", idTin).isEmpty()) {
                        Kode kode = lookupKode(kon, "stroke_observation", "rekanalisasi");
                        if (kode != null) {
                            String w = isoDariDatetime(wr);
                            String id = postFhir(api, mapper, link, "Observation",
                                    obsJson(kode, idPasien, idEnc, idDokter, w, "procedure",
                                            "\"valueDateTime\":\"" + esc(w) + "\"", ""), log);
                            if (!id.isEmpty()) recordSent(kon, idReg, "obs_rekanalisasi", idTin, id);
                        }
                    }

                    // TICI — valueCodeableConcept
                    String tici = nz(rs.getString("tici"));
                    if (!tici.isEmpty() && alreadySent(kon, idReg, "obs_tici", idTin).isEmpty()) {
                        Kode kode  = lookupKode(kon, "stroke_observation", "tici");
                        Kode nilai = lookupKode(kon, "stroke_tici", tici);
                        if (kode != null && nilai != null) {
                            String id = postFhir(api, mapper, link, "Observation",
                                    obsJson(kode, idPasien, idEnc, idDokter, waktu, "procedure",
                                            valueCodeable(nilai), ""), log);
                            if (!id.isEmpty()) recordSent(kon, idReg, "obs_tici", idTin, id);
                        }
                    }
                }
            }
        } catch (Exception e) { System.out.println("Notif kirimTindakan : " + e); }
        if (n > 0) log.append("Procedure tindakan: ").append(ok).append("/").append(n).append(" terkirim.\n");
    }

    private static String buildProcedureJson(Connection kon, String jenis, String kodeLokal,
                                             String idPasien, String idEnc, String idDokter, String waktu) {
        StringBuilder j = new StringBuilder();
        j.append("{\"resourceType\":\"Procedure\",\"status\":\"completed\"");

        if ("rehabilitasi".equals(jenis)) {
            Kode kat  = lookupKode(kon, "stroke_rehab_kategori", "rehab");
            Kode kode = lookupKode(kon, "stroke_rehab", kodeLokal);
            if (kode == null) return null;
            if (kat != null) j.append(",\"category\":{\"coding\":[").append(codingJson(kat)).append("]}");
            j.append(",\"code\":{\"coding\":[").append(codingJson(kode)).append("]}");
        } else {
            // rTPA / trombektomi / lainnya -> ICD-9-CM.
            // Trombektomi: `waktu` adalah waktu groin puncture, sesuai playbook.
            if (kodeLokal.isEmpty()) return null;
            j.append(",\"code\":{\"coding\":[{\"system\":\"http://hl7.org/fhir/sid/icd-9-cm\",\"code\":\"")
             .append(esc(kodeLokal)).append("\"}]}");
        }

        j.append(",\"subject\":{\"reference\":\"Patient/").append(esc(idPasien)).append("\"}")
         .append(",\"encounter\":{\"reference\":\"Encounter/").append(esc(idEnc)).append("\"}");
        if (!waktu.isEmpty()) j.append(",\"performedDateTime\":\"").append(esc(waktu)).append("\"");
        if (idDokter != null && !idDokter.isEmpty())
            j.append(",\"performer\":[{\"actor\":{\"reference\":\"Practitioner/").append(esc(idDokter)).append("\"}}]");
        j.append("}");
        return j.toString();
    }

    // ==================================================================== Observation generik

    private static void obsBool(Connection kon, ApiSatuSehat api, ObjectMapper mapper, String link,
            StringBuilder log, long idReg, String jenis, String kodeLokal, Boolean nilai,
            String idPasien, String idEnc, String idDokter, String waktu, String komponen) {
        if (nilai == null) return;
        if (!alreadySent(kon, idReg, jenis, "").isEmpty()) return;
        Kode kode = lookupKode(kon, "stroke_observation", kodeLokal);
        if (kode == null) kode = lookupKode(kon, "jantung_observation", kodeLokal); // kode bersama
        if (kode == null) { log.append("Obs ").append(kodeLokal).append(" dilewati: kode tidak ada.\n"); return; }
        String json = obsJson(kode, idPasien, idEnc, idDokter, waktu, "survey",
                "\"valueBoolean\":" + (nilai ? "true" : "false"), komponen);
        String id = postFhir(api, mapper, link, "Observation", json, log);
        if (!id.isEmpty()) recordSent(kon, idReg, jenis, "", id);
    }

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
        if (componentJson != null && !componentJson.isEmpty())
            j.append(",\"component\":[").append(componentJson).append("]");
        j.append("}");
        return j.toString();
    }

    // ==================================================================== Terminologi

    private static final class Kode { String system, code, display; }

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
    private static String komponenBool(Connection kon, String kodeKomponen, Boolean nilai) {
        if (nilai == null) return "";
        Kode k = lookupKode(kon, "component_code", kodeKomponen);
        if (k == null) return "";
        return "{\"code\":{\"coding\":[" + codingJson(k) + "]},\"valueBoolean\":" + (nilai ? "true" : "false") + "}";
    }
    private static String gabung(String bagian) { return bagian.isEmpty() ? "" : "," + bagian; }
    /** Buang koma menggantung di awal/akhir daftar komponen. */
    private static String bersihkanKoma(String s) {
        String t = s.trim();
        while (t.startsWith(",")) t = t.substring(1);
        while (t.endsWith(","))   t = t.substring(0, t.length() - 1);
        return t;
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

    // ==================================================================== DB

    private static String alreadySent(Connection kon, long idReg, String jenis, String refLokal) {
        try (PreparedStatement ps = kon.prepareStatement(
                "select id_fhir from satu_sehat_stroke_kirim where id_registrasi=? and jenis=? and ref_lokal=?")) {
            ps.setLong(1, idReg);
            ps.setString(2, jenis);
            ps.setString(3, refLokal == null ? "" : refLokal);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? nz(rs.getString(1)) : ""; }
        } catch (Exception e) { return ""; }
    }

    private static void recordSent(Connection kon, long idReg, String jenis, String refLokal, String idFhir) {
        try (PreparedStatement ps = kon.prepareStatement(
                "insert into satu_sehat_stroke_kirim (id_registrasi,jenis,ref_lokal,id_fhir) values (?,?,?,?) "
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
        int seranganKe = 1;
        String noRkm = "", noRawat = "", nama = "", tglRegistrasi = "", jenisStroke = "";
        String kdPenyakit = "", icd10Nama = "", tglDiagnosis = "", onsetSerangan = "", wakeUpStroke = "";
        String status = "", kdDokter = "", tglMeninggal = "";
    }

    private static final class Faktor {
        Boolean htAda, htDapatObat, htTerkontrol, dmAda, dmDapatObat, dmTerkontrol;
        Boolean dlAda, dlDapatObat, alkohol, kbHormonal, riwayatKeluarga;
        String htDerajat = "", dlStatin = "", merokok = "";
    }

    private static Reg loadReg(Connection kon, long idReg) {
        try (PreparedStatement ps = kon.prepareStatement(
                "select rs.*, ifnull(p.nm_pasien,'') nm_pasien, ifnull(pk.nm_penyakit,'') icd10nm "
              + "from registrasi_stroke rs "
              + "left join pasien p on p.no_rkm_medis=rs.no_rkm_medis "
              + "left join penyakit pk on pk.kd_penyakit=rs.kd_penyakit "
              + "where rs.id_registrasi=?")) {
            ps.setLong(1, idReg);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Reg r = new Reg();
                r.idRegistrasi  = idReg;
                r.noRkm         = nz(rs.getString("no_rkm_medis"));
                r.noRawat       = nz(rs.getString("no_rawat"));
                r.nama          = nz(rs.getString("nm_pasien"));
                r.tglRegistrasi = nz(rs.getString("tgl_registrasi"));
                r.jenisStroke   = nz(rs.getString("jenis_stroke"));
                r.seranganKe    = rs.getInt("serangan_ke");
                r.kdPenyakit    = nz(rs.getString("kd_penyakit"));
                r.icd10Nama     = nz(rs.getString("icd10nm"));
                r.tglDiagnosis  = nz(rs.getString("tgl_diagnosis"));
                r.onsetSerangan = nz(rs.getString("onset_serangan"));
                r.wakeUpStroke  = nz(rs.getString("wake_up_stroke"));
                r.status        = nz(rs.getString("status"));
                r.kdDokter      = nz(rs.getString("kd_dokter"));
                r.tglMeninggal  = nz(rs.getString("tgl_meninggal"));
                return r;
            }
        } catch (Exception e) {
            System.out.println("Notif loadReg stroke : " + e);
            return null;
        }
    }

    private static Faktor loadFaktor(Connection kon, long idReg, String noRawat) {
        Faktor f = loadFaktorSql(kon,
                "select * from registrasi_stroke_faktor_risiko where id_registrasi=? and no_rawat=? "
              + "order by tanggal desc limit 1", idReg, noRawat);
        if (f != null) return f;
        return loadFaktorSql(kon,
                "select * from registrasi_stroke_faktor_risiko where id_registrasi=? "
              + "order by tanggal desc limit 1", idReg, null);
    }

    private static Faktor loadFaktorSql(Connection kon, String sql, long idReg, String noRawat) {
        try (PreparedStatement ps = kon.prepareStatement(sql)) {
            ps.setLong(1, idReg);
            if (noRawat != null) ps.setString(2, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Faktor f = new Faktor();
                f.htAda           = bool(rs, "ht_ada");
                f.htDerajat       = nz(rs.getString("ht_derajat"));
                f.htDapatObat     = bool(rs, "ht_dapat_obat");
                f.htTerkontrol    = bool(rs, "ht_terkontrol");
                f.dmAda           = bool(rs, "dm_ada");
                f.dmDapatObat     = bool(rs, "dm_dapat_obat");
                f.dmTerkontrol    = bool(rs, "dm_terkontrol");
                f.dlAda           = bool(rs, "dl_ada");
                f.dlDapatObat     = bool(rs, "dl_dapat_obat");
                f.dlStatin        = nz(rs.getString("dl_statin"));
                f.merokok         = nz(rs.getString("merokok"));
                f.alkohol         = bool(rs, "alkohol");
                f.kbHormonal      = bool(rs, "kb_hormonal");
                f.riwayatKeluarga = bool(rs, "riwayat_keluarga");
                return f;
            }
        } catch (Exception e) {
            System.out.println("Notif loadFaktor stroke : " + e);
            return null;
        }
    }

    /** NULL berarti "belum dinilai", dibedakan dari false. */
    private static Boolean bool(ResultSet rs, String kolom) {
        try {
            boolean v = rs.getBoolean(kolom);
            return rs.wasNull() ? null : v;
        } catch (Exception e) { return null; }
    }

    // ==================================================================== Util

    private static String efektif(Reg r) {
        if (!r.onsetSerangan.isEmpty()) return isoDariDatetime(r.onsetSerangan);
        String d = firstNonEmpty(safeDate(r.tglDiagnosis), safeDate(r.tglRegistrasi));
        return d.isEmpty() ? "" : d + "T00:00:00+07:00";
    }

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

    private static String firstNonEmpty(String a, String b) { return (a != null && !a.isEmpty()) ? a : nz(b); }
    private static String emptyToNull(String s) { return (s == null || s.isEmpty()) ? null : s; }
    private static String nz(String s) { return s == null ? "" : s; }

    private static String ringkas(String s) {
        if (s == null) return "";
        String t = s.replaceAll("\\s+", " ").trim();
        return t.length() > 300 ? t.substring(0, 300) + "..." : t;
    }

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
