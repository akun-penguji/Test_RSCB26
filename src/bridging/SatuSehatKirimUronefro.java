/*
 * Orkestrator pengiriman Registrasi Uronefrologi ke SatuSehat (FHIR R4) — Fase 2.
 *
 * Urutan per registrasi (idempoten via satu_sehat_uronefro_kirim):
 *   1. Prasyarat : Encounter kunjungan sudah terkirim.
 *   2. Condition : diagnosis ICD-10 + stage derajat hipertensi.
 *   3. Condition : riwayat penyakit (kode berprefiks CC), satu per kode.
 *   4. EpisodeOfCare (CKD) : getOrCreate berstatus awal + link Encounter.
 *   5. Observation faktor risiko : hipertensi & DM — kode DIPAKAI BERSAMA
 *                  dengan modul jantung dan stroke.
 *   6. Observation batu : SNOMED 95566004 dengan TIGA komponen (lokasi,
 *                  derajat hidronefrosis, keparahan) + derivedFrom.
 *   7. Procedure : terapi pengganti ginjal (HD / PD / transplantasi).
 *
 * CATATAN derivedFrom
 *   Observation batu semestinya menunjuk bukti pencitraan. Referensi hanya
 *   dikirim bila kolom ref_pencitraan terisi id resource yang MEMANG sudah
 *   ada di SatuSehat. Kalau kosong, elemen derivedFrom dilewati dan dicatat
 *   di log — tidak mengarang referensi ke resource yang tidak ada.
 *
 * TERMINOLOGI TIDAK DI-HARDCODE — semua dari ref_registry_kode.
 * Lihat docs/registrasi-uronefrologi-satusehat.md.
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

public final class SatuSehatKirimUronefro {

    private SatuSehatKirimUronefro() { }

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

            String noKtp = scalar(kon, "select ifnull(no_ktp,'') from pasien where no_rkm_medis=? limit 1", r.noRkm);
            if (noKtp.isEmpty()) return "Pasien tidak punya NIK — wajib untuk lookup IHS SatuSehat.";
            String idPasien = cek.tampilIDPasien(noKtp);
            if (idPasien == null || idPasien.isEmpty()) return "Pasien belum ter-IHS di SatuSehat (Patient ID kosong).";
            log.append("IHS Pasien: ").append(idPasien).append("\n");

            String idDokter = r.kdDokter.isEmpty() ? "" : cek.tampilIDParktisi(r.kdDokter);

            String idEnc = scalar(kon, "select ifnull(id_encounter,'') from satu_sehat_encounter where no_rawat=? limit 1", r.noRawat);
            if (idEnc.isEmpty())
                return "Encounter untuk No.Rawat " + r.noRawat + " belum dikirim ke SatuSehat.\n"
                     + "Kirim Encounter dulu, baru Registrasi Uronefrologi.";
            log.append("Encounter: ").append(idEnc).append("\n");

            String waktu = efektif(r);

            // ================= 1. Condition diagnosis =================
            String idCondition = alreadySent(kon, idRegistrasi, "condition", "");
            if (idCondition.isEmpty()) {
                if (r.kdPenyakit.isEmpty()) {
                    log.append("Condition dilewati: kode ICD-10 belum diisi.\n");
                } else {
                    idCondition = postFhir(api, mapper, link, "Condition",
                            buildConditionJson(kon, r, idPasien, idEnc), log);
                    if (!idCondition.isEmpty()) recordSent(kon, idRegistrasi, "condition", "", idCondition);
                }
            } else {
                log.append("Condition sudah terkirim (").append(idCondition).append(").\n");
            }

            // ================= 2. Condition riwayat =================
            kirimRiwayat(kon, api, mapper, link, log, idRegistrasi, idPasien, idEnc);

            // ================= 3. EpisodeOfCare (CKD) =================
            String periodStart = firstNonEmpty(safeDate(r.tglDiagnosis), safeDate(r.tglRegistrasi));
            String idEoc = SatuSehatEpisodeOfCareHelper.getOrCreate(
                    r.noRkm, idPasien, r.nama,
                    SatuSehatEpisodeOfCareHelper.TYPE_CKD[0],
                    SatuSehatEpisodeOfCareHelper.TYPE_CKD[1],
                    emptyToNull(r.kdPenyakit), emptyToNull(idCondition),
                    periodStart, emptyToNull(r.kdDokter),
                    r.status.isEmpty() ? "waitlist" : r.status);
            if (idEoc != null && !idEoc.isEmpty()) {
                log.append("EpisodeOfCare (CKD): ").append(idEoc).append("\n");
                SatuSehatEpisodeOfCareHelper.linkEncounter(r.noRawat, idEoc);
                recordSent(kon, idRegistrasi, "episodeofcare", "", idEoc);
                // period.end diisi saat episode ditutup, apa pun alasannya
                // (meninggal / sembuh batu / hilang kontak).
                String periodEnd = "finished".equals(r.status)
                        ? firstNonEmpty(safeDate(r.tglFinish), safeDate(r.tglMeninggal)) : null;
                boolean ok = SatuSehatEpisodeOfCareHelper.patchStatus(idEoc, r.status, periodEnd);
                log.append("  status -> ").append(r.status);
                if ("finished".equals(r.status) && !r.alasanFinish.isEmpty())
                    log.append(" (alasan: ").append(r.alasanFinish).append(")");
                log.append(ok ? " (ok)\n" : " (gagal)\n");
            } else {
                log.append("EpisodeOfCare GAGAL dibuat.\n");
            }

            // ================= 4. Observation faktor risiko =================
            kirimFaktorRisiko(kon, api, mapper, link, log, idRegistrasi, r, idPasien, idEnc, idDokter, waktu);

            // ================= 5. Observation batu =================
            kirimBatu(kon, api, mapper, link, log, idRegistrasi, idPasien, idEnc, idDokter);

            // ================= 6. Procedure terapi =================
            kirimTerapi(kon, api, mapper, link, log, idRegistrasi, idPasien, idEnc, idDokter);

            log.append("\nSelesai.");
        } catch (Exception ex) {
            log.append("\nERROR: ").append(ex.getMessage());
            System.out.println("Notif SatuSehatKirimUronefro : " + ex);
        }
        return log.toString();
    }

    // ==================================================================== Condition

    private static String buildConditionJson(Connection kon, Reg r, String idPasien, String idEnc) {
        StringBuilder j = new StringBuilder();
        j.append("{\"resourceType\":\"Condition\"")
         .append(",\"clinicalStatus\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-clinical\",\"code\":\"active\"}]}")
         .append(",\"verificationStatus\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-ver-status\",\"code\":\"confirmed\"}]}")
         .append(",\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-category\",\"code\":\"problem-list-item\"}]}]")
         .append(",\"code\":{\"coding\":[{\"system\":\"http://hl7.org/fhir/sid/icd-10\",\"code\":\"").append(esc(r.kdPenyakit))
         .append("\",\"display\":\"").append(esc(r.icd10Nama.isEmpty() ? r.kdPenyakit : r.icd10Nama)).append("\"}]}");

        // stage derajat hipertensi — memakai kelompok ht_derajat_cond (bukan _obs)
        Faktor f = loadFaktor(kon, r.idRegistrasi, r.noRawat);
        if (f != null && !f.htDerajat.isEmpty()) {
            Kode tipe  = lookupKode(kon, "component_code", "ht_derajat");
            Kode nilai = lookupKode(kon, "ht_derajat_cond", f.htDerajat);
            if (tipe != null && nilai != null) {
                j.append(",\"stage\":[{\"type\":{\"coding\":[").append(codingJson(tipe))
                 .append("]},\"summary\":{\"coding\":[").append(codingJson(nilai)).append("]}}]");
            }
        }
        if (!safeDate(r.tglDiagnosis).isEmpty())
            j.append(",\"onsetDateTime\":\"").append(safeDate(r.tglDiagnosis)).append("T00:00:00+07:00\"");
        j.append(",\"subject\":{\"reference\":\"Patient/").append(esc(idPasien)).append("\"}")
         .append(",\"encounter\":{\"reference\":\"Encounter/").append(esc(idEnc)).append("\"}")
         .append("}");
        return j.toString();
    }

    private static void kirimRiwayat(Connection kon, ApiSatuSehat api, ObjectMapper mapper, String link,
            StringBuilder log, long idReg, String idPasien, String idEnc) {
        int n = 0, ok = 0;
        try (PreparedStatement ps = kon.prepareStatement(
                "select kode from registrasi_uronefro_riwayat where id_registrasi=?")) {
            ps.setLong(1, idReg);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    n++;
                    String lokal = nz(rs.getString("kode"));
                    if (!alreadySent(kon, idReg, "condition_riwayat", lokal).isEmpty()) { ok++; continue; }
                    Kode k = lookupKode(kon, "uro_riwayat", lokal);
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
        } catch (Exception e) { System.out.println("Notif kirimRiwayat uronefro : " + e); }
        if (n > 0) log.append("Condition riwayat: ").append(ok).append("/").append(n).append(" terkirim.\n");
    }

    // ==================================================================== Observation

    private static void kirimFaktorRisiko(Connection kon, ApiSatuSehat api, ObjectMapper mapper, String link,
            StringBuilder log, long idReg, Reg r, String idPasien, String idEnc, String idDokter, String waktu) {
        Faktor f = loadFaktor(kon, idReg, r.noRawat);
        if (f == null) { log.append("Faktor risiko belum diisi — Observation dilewati.\n"); return; }

        // Hipertensi + tiga komponen
        if (f.htAda != null && alreadySent(kon, idReg, "obs_ht", "").isEmpty()) {
            Kode kode = lookupKode(kon, "jantung_observation", "hipertensi");
            if (kode != null) {
                StringBuilder c = new StringBuilder();
                Kode kD = lookupKode(kon, "component_code", "ht_derajat");
                Kode vD = f.htDerajat.isEmpty() ? null : lookupKode(kon, "ht_derajat_obs", f.htDerajat);
                if (kD != null && vD != null) c.append(componentCodeable(kD, vD));
                tambah(c, komponenBool(kon, "dapat_obat", f.htDapatObat));
                tambah(c, komponenBool(kon, "terkontrol", f.htTerkontrol));
                String id = postFhir(api, mapper, link, "Observation",
                        obsJson(kode, idPasien, idEnc, idDokter, waktu, "survey",
                                "\"valueBoolean\":" + (f.htAda ? "true" : "false"), c.toString(), ""), log);
                if (!id.isEmpty()) recordSent(kon, idReg, "obs_ht", "", id);
            }
        }

        // Diabetes Mellitus + dua komponen
        if (f.dmAda != null && alreadySent(kon, idReg, "obs_dm", "").isEmpty()) {
            Kode kode = lookupKode(kon, "jantung_observation", "dm");
            if (kode != null) {
                StringBuilder c = new StringBuilder();
                tambah(c, komponenBool(kon, "dapat_obat", f.dmDapatObat));
                tambah(c, komponenBool(kon, "terkontrol", f.dmTerkontrol));
                String id = postFhir(api, mapper, link, "Observation",
                        obsJson(kode, idPasien, idEnc, idDokter, waktu, "survey",
                                "\"valueBoolean\":" + (f.dmAda ? "true" : "false"), c.toString(), ""), log);
                if (!id.isEmpty()) recordSent(kon, idReg, "obs_dm", "", id);
            }
        }
    }

    /**
     * Observation batu saluran kemih: satu resource membawa tiga komponen dari
     * tiga sumber kode berbeda, plus derivedFrom ke bukti pencitraan.
     */
    private static void kirimBatu(Connection kon, ApiSatuSehat api, ObjectMapper mapper, String link,
            StringBuilder log, long idReg, String idPasien, String idEnc, String idDokter) {
        int n = 0, ok = 0, tanpaBukti = 0;
        try (PreparedStatement ps = kon.prepareStatement(
                "select id_batu, tanggal, ifnull(lokasi,'') lokasi, ifnull(hidronefrosis,'') hidro, "
              + "ifnull(keparahan,'') keparahan, ifnull(ref_pencitraan,'') ref_citra "
              + "from registrasi_uronefro_batu where id_registrasi=? order by id_batu")) {
            ps.setLong(1, idReg);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    n++;
                    String idBatu = nz(rs.getString("id_batu"));
                    if (!alreadySent(kon, idReg, "obs_batu", idBatu).isEmpty()) { ok++; continue; }
                    Kode kode = lookupKode(kon, "uro_observation", "batu");
                    if (kode == null) continue;

                    StringBuilder c = new StringBuilder();
                    // Lokasi batu — nilai SNOMED bodySite
                    Kode vLok = lookupKode(kon, "uro_lokasi_batu", nz(rs.getString("lokasi")));
                    if (vLok != null) {
                        tambah(c, "{\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\","
                                + "\"code\":\"363698007\",\"display\":\"Finding site\"}]},"
                                + "\"valueCodeableConcept\":{\"coding\":[" + codingJson(vLok) + "]}}");
                    }
                    // Derajat hidronefrosis
                    Kode kHid = lookupKode(kon, "uro_observation", "hidronefrosis");
                    Kode vHid = lookupKode(kon, "uro_hidronefrosis", nz(rs.getString("hidro")));
                    if (kHid != null && vHid != null) tambah(c, componentCodeable(kHid, vHid));
                    // Keparahan
                    Kode kKep = lookupKode(kon, "uro_observation", "keparahan");
                    Kode vKep = lookupKode(kon, "uro_keparahan_batu", nz(rs.getString("keparahan")));
                    if (kKep != null && vKep != null) tambah(c, componentCodeable(kKep, vKep));

                    // derivedFrom hanya bila id pencitraan benar-benar ada.
                    String refCitra = nz(rs.getString("ref_citra"));
                    String derived = "";
                    if (!refCitra.isEmpty()) {
                        derived = "\"derivedFrom\":[{\"reference\":\"" + esc(refCitra) + "\"}]";
                    } else {
                        tanpaBukti++;
                    }

                    String id = postFhir(api, mapper, link, "Observation",
                            obsJson(kode, idPasien, idEnc, idDokter, isoDariDatetime(nz(rs.getString("tanggal"))),
                                    "exam", "\"valueBoolean\":true", c.toString(), derived), log);
                    if (!id.isEmpty()) { recordSent(kon, idReg, "obs_batu", idBatu, id); ok++; }
                }
            }
        } catch (Exception e) { System.out.println("Notif kirimBatu : " + e); }
        if (n > 0) {
            log.append("Observation batu: ").append(ok).append("/").append(n).append(" terkirim.\n");
            if (tanpaBukti > 0)
                log.append("  ").append(tanpaBukti)
                   .append(" tanpa derivedFrom (id pencitraan belum diisi — isi setelah hasil radiologi terkirim).\n");
        }
    }

    // ==================================================================== Procedure

    private static void kirimTerapi(Connection kon, ApiSatuSehat api, ObjectMapper mapper, String link,
            StringBuilder log, long idReg, String idPasien, String idEnc, String idDokter) {
        int n = 0, ok = 0;
        try (PreparedStatement ps = kon.prepareStatement(
                "select id_terapi, jenis, ifnull(kode_icd9,'') kode, ifnull(deskripsi,'') deskripsi, tanggal "
              + "from registrasi_uronefro_terapi where id_registrasi=? order by id_terapi")) {
            ps.setLong(1, idReg);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    n++;
                    String idTer = nz(rs.getString("id_terapi"));
                    if (!alreadySent(kon, idReg, "procedure", idTer).isEmpty()) { ok++; continue; }
                    String kode = nz(rs.getString("kode"));
                    if (kode.isEmpty()) continue;
                    StringBuilder j = new StringBuilder();
                    j.append("{\"resourceType\":\"Procedure\",\"status\":\"completed\"")
                     .append(",\"code\":{\"coding\":[{\"system\":\"http://hl7.org/fhir/sid/icd-9-cm\",\"code\":\"")
                     .append(esc(kode)).append("\",\"display\":\"")
                     .append(esc(nz(rs.getString("deskripsi")).isEmpty() ? kode : rs.getString("deskripsi")))
                     .append("\"}]}")
                     .append(",\"subject\":{\"reference\":\"Patient/").append(esc(idPasien)).append("\"}")
                     .append(",\"encounter\":{\"reference\":\"Encounter/").append(esc(idEnc)).append("\"}")
                     .append(",\"performedDateTime\":\"").append(esc(isoDariDatetime(nz(rs.getString("tanggal"))))).append("\"");
                    if (!idDokter.isEmpty())
                        j.append(",\"performer\":[{\"actor\":{\"reference\":\"Practitioner/").append(esc(idDokter)).append("\"}}]");
                    j.append("}");
                    String id = postFhir(api, mapper, link, "Procedure", j.toString(), log);
                    if (!id.isEmpty()) { recordSent(kon, idReg, "procedure", idTer, id); ok++; }
                }
            }
        } catch (Exception e) { System.out.println("Notif kirimTerapi : " + e); }
        if (n > 0) log.append("Procedure terapi: ").append(ok).append("/").append(n).append(" terkirim.\n");
    }

    // ==================================================================== Observation generik

    /** Varian obsJson dengan slot tambahan (dipakai untuk derivedFrom). */
    private static String obsJson(Kode kode, String idPasien, String idEnc, String idDokter,
                                  String waktu, String kategori, String valueJson,
                                  String componentJson, String extraJson) {
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
        if (extraJson != null && !extraJson.isEmpty()) j.append(",").append(extraJson);
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
    private static void tambah(StringBuilder sb, String bagian) {
        if (bagian == null || bagian.isEmpty()) return;
        if (sb.length() > 0) sb.append(",");
        sb.append(bagian);
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
                "select id_fhir from satu_sehat_uronefro_kirim where id_registrasi=? and jenis=? and ref_lokal=?")) {
            ps.setLong(1, idReg);
            ps.setString(2, jenis);
            ps.setString(3, refLokal == null ? "" : refLokal);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? nz(rs.getString(1)) : ""; }
        } catch (Exception e) { return ""; }
    }

    private static void recordSent(Connection kon, long idReg, String jenis, String refLokal, String idFhir) {
        try (PreparedStatement ps = kon.prepareStatement(
                "insert into satu_sehat_uronefro_kirim (id_registrasi,jenis,ref_lokal,id_fhir) values (?,?,?,?) "
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
        String kdPenyakit = "", icd10Nama = "", tglDiagnosis = "";
        String status = "", alasanFinish = "", tglFinish = "", kdDokter = "", tglMeninggal = "";
    }

    private static final class Faktor {
        Boolean htAda, htDapatObat, htTerkontrol, dmAda, dmDapatObat, dmTerkontrol;
        String htDerajat = "";
    }

    private static Reg loadReg(Connection kon, long idReg) {
        try (PreparedStatement ps = kon.prepareStatement(
                "select ru.*, ifnull(p.nm_pasien,'') nm_pasien, ifnull(pk.nm_penyakit,'') icd10nm "
              + "from registrasi_uronefro ru "
              + "left join pasien p on p.no_rkm_medis=ru.no_rkm_medis "
              + "left join penyakit pk on pk.kd_penyakit=ru.kd_penyakit "
              + "where ru.id_registrasi=?")) {
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
                r.status        = nz(rs.getString("status"));
                r.alasanFinish  = nz(rs.getString("alasan_finish"));
                r.tglFinish     = nz(rs.getString("tgl_finish"));
                r.kdDokter      = nz(rs.getString("kd_dokter"));
                r.tglMeninggal  = nz(rs.getString("tgl_meninggal"));
                return r;
            }
        } catch (Exception e) {
            System.out.println("Notif loadReg uronefro : " + e);
            return null;
        }
    }

    private static Faktor loadFaktor(Connection kon, long idReg, String noRawat) {
        try (PreparedStatement ps = kon.prepareStatement(
                "select * from registrasi_uronefro_faktor_risiko where id_registrasi=? and no_rawat=? "
              + "order by tanggal desc limit 1")) {
            ps.setLong(1, idReg);
            ps.setString(2, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Faktor f = new Faktor();
                f.htAda        = bool(rs, "ht_ada");
                f.htDerajat    = nz(rs.getString("ht_derajat"));
                f.htDapatObat  = bool(rs, "ht_dapat_obat");
                f.htTerkontrol = bool(rs, "ht_terkontrol");
                f.dmAda        = bool(rs, "dm_ada");
                f.dmDapatObat  = bool(rs, "dm_dapat_obat");
                f.dmTerkontrol = bool(rs, "dm_terkontrol");
                return f;
            }
        } catch (Exception e) {
            System.out.println("Notif loadFaktor uronefro : " + e);
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
