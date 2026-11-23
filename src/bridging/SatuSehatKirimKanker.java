/*
 * Orkestrator pengiriman Registrasi Kanker ke SatuSehat (FHIR R4) — Fase 2.
 *
 * Urutan per registrasi (idempoten via tabel satu_sehat_kanker_kirim):
 *   1. Prasyarat: Encounter kunjungan sudah terkirim (satu_sehat_encounter.id_encounter).
 *   2. Condition  : ICD-10 + coding ICD-O morfologi + bodySite (topografi ICD-O) + stage (TNM/stage group + grade).
 *   3. EpisodeOfCare (ONCO) : getOrCreate + link Encounter + updateStatus sesuai lifecycle registry.
 *   4. Procedure  : satu per baris terapi (bedah/kemo/radiasi) — kode ICD-9-CM bila ada.
 *
 * CATATAN IG: elemen bodySite/stage dan sistem kode ICD-O di sini mengikuti FHIR R4 core.
 * Binding value set / profil persis SatuSehat Kanker WAJIB dikonfirmasi terhadap Implementation
 * Guide resmi sebelum go-live (lihat docs/registrasi-kanker-satusehat.md §7). Uji di sandbox dulu.
 *
 * Dipakai dari DlgRegistrasiKanker (tombol "Kirim ke SatuSehat").
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

public final class SatuSehatKirimKanker {

    private SatuSehatKirimKanker() { }

    /** Hasil kirim per registrasi (untuk ditampilkan ke user). */
    public static String kirim(long idRegistrasi) {
        StringBuilder log = new StringBuilder();
        Connection kon = koneksiDB.condb();
        ApiSatuSehat api = new ApiSatuSehat();
        SatuSehatCekNIK cek = new SatuSehatCekNIK();
        ObjectMapper mapper = new ObjectMapper();
        String link = api.getBaseUrl();

        try {
            // ---- Muat registrasi ----
            Reg r = loadReg(kon, idRegistrasi);
            if (r == null) return "Registrasi tidak ditemukan (id " + idRegistrasi + ").";

            // ---- Data pasien & dokter ----
            String noKtpPasien = scalar(kon, "select ifnull(no_ktp,'') from pasien where no_rkm_medis=? limit 1", r.noRkm);
            if (noKtpPasien.isEmpty()) return "Pasien tidak punya NIK — wajib untuk lookup IHS SatuSehat.";
            String idPasien = cek.tampilIDPasien(noKtpPasien);
            if (idPasien == null || idPasien.isEmpty()) return "Pasien belum ter-IHS di SatuSehat (Patient ID kosong).";
            log.append("IHS Pasien: ").append(idPasien).append("\n");

            String noKtpDokter = r.kdDokter; // kd_dokter ~ NIK pada data ini
            String idDokter = (noKtpDokter == null || noKtpDokter.isEmpty()) ? "" : cek.tampilIDParktisi(noKtpDokter);

            // ---- Prasyarat: Encounter ----
            String idEncounter = scalar(kon, "select ifnull(id_encounter,'') from satu_sehat_encounter where no_rawat=? limit 1", r.noRawat);
            if (idEncounter.isEmpty())
                return "Encounter untuk No.Rawat " + r.noRawat + " belum dikirim ke SatuSehat.\n"
                     + "Kirim Encounter dulu (menu Kirim Encounter Satu Sehat), baru Registrasi Kanker.";
            log.append("Encounter: ").append(idEncounter).append("\n");

            // ================= 1. Condition (+ morfologi/bodySite/stage) =================
            String idCondition = alreadySent(kon, idRegistrasi, "condition", "");
            if (idCondition.isEmpty()) {
                String cj = buildConditionJson(r, idPasien, idEncounter);
                idCondition = postFhir(api, mapper, link, "Condition", cj, log);
                if (!idCondition.isEmpty()) recordSent(kon, idRegistrasi, "condition", "", idCondition);
            } else {
                log.append("Condition sudah terkirim (").append(idCondition).append(").\n");
            }

            // ================= 2. EpisodeOfCare ONCO =================
            String periodStart = (r.tglDiagnosis != null && !r.tglDiagnosis.isEmpty() && !r.tglDiagnosis.startsWith("0000"))
                    ? r.tglDiagnosis : r.tglRegistrasi;
            String idEoc = SatuSehatEpisodeOfCareHelper.getOrCreate(
                    r.noRkm, idPasien, r.nama, "ONCO", "Oncology Program",
                    emptyToNull(r.kdPenyakit), emptyToNull(idCondition),
                    safeDate(periodStart), emptyToNull(noKtpDokter));
            if (idEoc != null && !idEoc.isEmpty()) {
                log.append("EpisodeOfCare (ONCO): ").append(idEoc).append("\n");
                SatuSehatEpisodeOfCareHelper.linkEncounter(r.noRawat, idEoc);
                recordSent(kon, idRegistrasi, "episodeofcare", "", idEoc);
                // Lifecycle: sesuaikan status registry
                String periodEnd = ("finished".equals(r.status) || "cancelled".equals(r.status))
                        ? safeDate(firstNonEmpty(r.tglOutcome, r.tglRegistrasi)) : null;
                boolean okStat = SatuSehatEpisodeOfCareHelper.updateStatus(idEoc, r.status, periodEnd);
                log.append("  status -> ").append(r.status).append(okStat ? " (ok)\n" : " (gagal)\n");
            } else {
                log.append("EpisodeOfCare GAGAL dibuat.\n");
            }

            // ================= 3. Procedure per terapi =================
            int nTerapi = 0, okTerapi = 0;
            try (PreparedStatement ps = kon.prepareStatement(
                    "select id_terapi, jenis, ifnull(kode_tindakan,'') kode, ifnull(deskripsi,'') deskripsi, "
                  + "ifnull(tgl_mulai,'') mulai, ifnull(tgl_selesai,'') selesai, status "
                  + "from registrasi_kanker_terapi where id_registrasi=? order by id_terapi")) {
                ps.setLong(1, idRegistrasi);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        nTerapi++;
                        String idTerapi = rs.getString("id_terapi");
                        String prev = alreadySent(kon, idRegistrasi, "procedure", idTerapi);
                        if (!prev.isEmpty()) { okTerapi++; continue; }
                        String pj = buildProcedureJson(r, idPasien, idEncounter, idDokter,
                                rs.getString("jenis"), rs.getString("kode"), rs.getString("deskripsi"),
                                rs.getString("mulai"), rs.getString("selesai"), rs.getString("status"));
                        String idProc = postFhir(api, mapper, link, "Procedure", pj, log);
                        if (!idProc.isEmpty()) { recordSent(kon, idRegistrasi, "procedure", idTerapi, idProc); okTerapi++; }
                    }
                }
            }
            if (nTerapi > 0) log.append("Procedure terapi: ").append(okTerapi).append("/").append(nTerapi).append(" terkirim.\n");

            log.append("\nSelesai.");
        } catch (Exception ex) {
            log.append("\nERROR: ").append(ex.getMessage());
            System.out.println("Notif SatuSehatKirimKanker : " + ex);
        }
        return log.toString();
    }

    // ==================================================================== JSON builders
    private static String buildConditionJson(Reg r, String idPasien, String idEncounter) {
        StringBuilder codings = new StringBuilder();
        codings.append("{\"system\":\"http://hl7.org/fhir/sid/icd-10\",\"code\":\"").append(esc(r.kdPenyakit))
               .append("\",\"display\":\"").append(esc(r.icd10Nama.isEmpty() ? r.kdPenyakit : r.icd10Nama)).append("\"}");
        if (!r.icdoMorf.isEmpty()) {
            codings.append(",{\"system\":\"http://terminology.hl7.org/CodeSystem/icd-o-3\",\"code\":\"")
                   .append(esc(r.icdoMorf)).append("\",\"display\":\"").append(esc(r.morfNama)).append("\"}");
        }
        StringBuilder j = new StringBuilder();
        j.append("{\"resourceType\":\"Condition\",")
         .append("\"clinicalStatus\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-clinical\",\"code\":\"active\",\"display\":\"Active\"}]},")
         .append("\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-category\",\"code\":\"encounter-diagnosis\",\"display\":\"Encounter Diagnosis\"}]}],")
         .append("\"code\":{\"coding\":[").append(codings).append("],\"text\":\"").append(esc(firstNonEmpty(r.icd10Nama, r.kdPenyakit))).append("\"},");
        // bodySite = topografi ICD-O
        if (!r.icdoTopo.isEmpty()) {
            j.append("\"bodySite\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/icd-o-3\",\"code\":\"")
             .append(esc(r.icdoTopo)).append("\",\"display\":\"").append(esc(r.topoNama)).append("\"}],\"text\":\"")
             .append(esc(r.topoNama)).append("\"}],");
        }
        j.append("\"subject\":{\"reference\":\"Patient/").append(idPasien).append("\",\"display\":\"").append(esc(r.nama)).append("\"},")
         .append("\"encounter\":{\"reference\":\"Encounter/").append(idEncounter).append("\"}");
        // stage = TNM + stage group + grade (ringkasan teks)
        String stageText = buildStageText(r);
        if (!stageText.isEmpty()) {
            j.append(",\"stage\":[{\"summary\":{\"text\":\"").append(esc(stageText)).append("\"},\"type\":{\"text\":\"TNM staging\"}}]");
        }
        if (r.tglDiagnosis != null && !r.tglDiagnosis.isEmpty() && !r.tglDiagnosis.startsWith("0000")) {
            j.append(",\"onsetDateTime\":\"").append(safeDate(r.tglDiagnosis)).append("T00:00:00+07:00\"");
        }
        j.append("}");
        return j.toString();
    }

    private static String buildStageText(Reg r) {
        StringBuilder s = new StringBuilder();
        if (!nz(r.stageGroup).isEmpty()) s.append("Stadium ").append(r.stageGroup);
        StringBuilder tnm = new StringBuilder();
        if (!nz(r.tnmT).isEmpty()) tnm.append(r.tnmT).append(" ");
        if (!nz(r.tnmN).isEmpty()) tnm.append(r.tnmN).append(" ");
        if (!nz(r.tnmM).isEmpty()) tnm.append(r.tnmM);
        if (tnm.length() > 0) { if (s.length() > 0) s.append(" — "); s.append(tnm.toString().trim()); }
        if (!nz(r.grade).isEmpty()) { if (s.length() > 0) s.append(" — "); s.append("Grade ").append(r.grade); }
        return s.toString();
    }

    private static String buildProcedureJson(Reg r, String idPasien, String idEncounter, String idDokter,
            String jenis, String kode, String deskripsi, String mulai, String selesai, String status) {
        StringBuilder j = new StringBuilder();
        j.append("{\"resourceType\":\"Procedure\",")
         .append("\"status\":\"").append(mapProcStatus(status)).append("\",");
        j.append("\"category\":{\"text\":\"Terapi kanker: ").append(esc(jenis)).append("\"},");
        j.append("\"code\":{");
        if (kode != null && !kode.trim().isEmpty()) {
            j.append("\"coding\":[{\"system\":\"http://hl7.org/fhir/sid/icd-9-cm\",\"code\":\"").append(esc(kode))
             .append("\",\"display\":\"").append(esc(firstNonEmpty(deskripsi, jenis))).append("\"}],");
        }
        j.append("\"text\":\"").append(esc(firstNonEmpty(deskripsi, jenis))).append("\"},");
        j.append("\"subject\":{\"reference\":\"Patient/").append(idPasien).append("\",\"display\":\"").append(esc(r.nama)).append("\"},");
        j.append("\"encounter\":{\"reference\":\"Encounter/").append(idEncounter).append("\"}");
        // performed
        if (mulai != null && !mulai.isEmpty() && !mulai.startsWith("0000")) {
            if (selesai != null && !selesai.isEmpty() && !selesai.startsWith("0000")) {
                j.append(",\"performedPeriod\":{\"start\":\"").append(safeDate(mulai)).append("T00:00:00+07:00\",\"end\":\"")
                 .append(safeDate(selesai)).append("T00:00:00+07:00\"}");
            } else {
                j.append(",\"performedDateTime\":\"").append(safeDate(mulai)).append("T00:00:00+07:00\"");
            }
        }
        if (idDokter != null && !idDokter.isEmpty()) {
            j.append(",\"performer\":[{\"actor\":{\"reference\":\"Practitioner/").append(idDokter).append("\"}}]");
        }
        j.append("}");
        return j.toString();
    }

    private static String mapProcStatus(String s) {
        if (s == null) return "unknown";
        switch (s) {
            case "rencana": return "preparation";
            case "berlangsung": return "in-progress";
            case "selesai": return "completed";
            case "dibatalkan": return "stopped";
            default: return "unknown";
        }
    }

    // ==================================================================== HTTP
    private static String postFhir(ApiSatuSehat api, ObjectMapper mapper, String link, String resource, String json, StringBuilder log) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + api.getValidToken());
            System.out.println("POST " + link + "/" + resource + " : " + json);
            HttpEntity<String> req = new HttpEntity<>(json, headers);
            String resp = api.getRest().exchange(link + "/" + resource, HttpMethod.POST, req, String.class).getBody();
            System.out.println("RESP " + resource + " : " + resp);
            // Pakai asText() tanpa argumen: overload asText(String) baru ada di Jackson 2.4+,
            // sedangkan classpath build project ini masih di bawah itu. Aman karena path()
            // mengembalikan MissingNode saat "id" tidak ada, dan MissingNode.asText() = "".
            String id = mapper.readTree(resp).path("id").asText();
            if (id.isEmpty()) log.append(resource).append(" GAGAL: ").append(ringkas(resp)).append("\n");
            else log.append(resource).append(": ").append(id).append("\n");
            return id;
        } catch (Exception e) {
            log.append(resource).append(" ERROR: ").append(e.getMessage()).append("\n");
            System.out.println("Notif postFhir " + resource + " : " + e);
            return "";
        }
    }

    // ==================================================================== DB helpers
    private static String alreadySent(Connection kon, long idReg, String jenis, String refLokal) {
        try (PreparedStatement ps = kon.prepareStatement(
                "select id_fhir from satu_sehat_kanker_kirim where id_registrasi=? and jenis=? and ref_lokal=?")) {
            ps.setLong(1, idReg); ps.setString(2, jenis); ps.setString(3, refLokal == null ? "" : refLokal);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? nz(rs.getString(1)) : ""; }
        } catch (Exception e) { return ""; }
    }

    private static void recordSent(Connection kon, long idReg, String jenis, String refLokal, String idFhir) {
        try (PreparedStatement ps = kon.prepareStatement(
                "insert into satu_sehat_kanker_kirim (id_registrasi,jenis,ref_lokal,id_fhir) values (?,?,?,?) "
              + "on duplicate key update id_fhir=values(id_fhir), tgl_kirim=current_timestamp")) {
            ps.setLong(1, idReg); ps.setString(2, jenis); ps.setString(3, refLokal == null ? "" : refLokal); ps.setString(4, idFhir);
            ps.executeUpdate();
        } catch (Exception e) { System.out.println("Notif recordSent : " + e); }
    }

    private static String scalar(Connection kon, String sql, String p1) {
        try (PreparedStatement ps = kon.prepareStatement(sql)) {
            ps.setString(1, p1);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? nz(rs.getString(1)) : ""; }
        } catch (Exception e) { return ""; }
    }

    private static Reg loadReg(Connection kon, long idReg) {
        try (PreparedStatement ps = kon.prepareStatement(
                "select rk.*, ifnull(p.nm_pasien,'') nm_pasien, ifnull(pk.nm_penyakit,'') icd10nm "
              + "from registrasi_kanker rk "
              + "left join pasien p on p.no_rkm_medis=rk.no_rkm_medis "
              + "left join penyakit pk on pk.kd_penyakit=rk.kd_penyakit "
              + "where rk.id_registrasi=?")) {
            ps.setLong(1, idReg);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Reg r = new Reg();
                r.noRkm = nz(rs.getString("no_rkm_medis"));
                r.noRawat = nz(rs.getString("no_rawat"));
                r.nama = nz(rs.getString("nm_pasien"));
                r.tglRegistrasi = nz(rs.getString("tgl_registrasi"));
                r.tglDiagnosis = nz(rs.getString("tgl_diagnosis"));
                r.kdPenyakit = nz(rs.getString("kd_penyakit"));
                r.icd10Nama = nz(rs.getString("icd10nm"));
                r.icdoTopo = nz(rs.getString("icdo_topografi"));
                r.topoNama = nz(rs.getString("topografi_nama"));
                r.icdoMorf = nz(rs.getString("icdo_morfologi"));
                r.morfNama = nz(rs.getString("morfologi_nama"));
                r.grade = nz(rs.getString("grade"));
                r.tnmT = nz(rs.getString("tnm_t"));
                r.tnmN = nz(rs.getString("tnm_n"));
                r.tnmM = nz(rs.getString("tnm_m"));
                r.stageGroup = nz(rs.getString("stage_group"));
                r.status = nz(rs.getString("status"));
                if (r.status.isEmpty()) r.status = "active";
                r.kdDokter = nz(rs.getString("kd_dokter"));
                r.tglOutcome = nz(rs.getString("tgl_outcome"));
                return r;
            }
        } catch (Exception e) { System.out.println("Notif loadReg : " + e); return null; }
    }

    private static final class Reg {
        String noRkm, noRawat, nama, tglRegistrasi, tglDiagnosis, kdPenyakit, icd10Nama,
               icdoTopo, topoNama, icdoMorf, morfNama, grade, tnmT, tnmN, tnmM, stageGroup,
               status, kdDokter, tglOutcome;
    }

    // ==================================================================== util
    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", " ").replace("\n", " ");
    }
    private static String nz(String s) { return s == null ? "" : s; }
    private static String emptyToNull(String s) { return (s == null || s.trim().isEmpty()) ? null : s.trim(); }
    private static String firstNonEmpty(String a, String b) { return (a != null && !a.trim().isEmpty()) ? a : nz(b); }
    private static String safeDate(String d) {
        if (d == null || d.isEmpty()) return d;
        return d.length() >= 10 ? d.substring(0, 10) : d;
    }
    private static String ringkas(String s) {
        if (s == null) return "";
        return s.length() > 300 ? s.substring(0, 300) + "..." : s;
    }
}
