package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.koneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

/**
 * Kirim Formulir Transfer Internal RS (tabel transfer_internal_rs) ke SatuSehat.
 *
 * Perpindahan ruang itu sendiri sudah direpresentasikan di Encounter.location[]
 * (dibangun sender Encounter dari kamar_inap). Sender ini melengkapi dokumentasi
 * KLINIS handoff transfer sebagai Composition "Transfer summary note" (LOINC
 * 18761-7) yang me-link Observation tanda vital sebelum & sesudah transfer,
 * plus narasi (ruang asal/tujuan, DPJP, derajat, alat medis, rencana, hasil).
 *
 * Dipakai lewat form Kirim Bundel (kirimSatuNoRawat / updateSatuNoRawat),
 * mengikuti pola sender headless lain (output via System.out).
 */
public final class SatuSehatKirimTransferInternal {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final ObjectMapper mapper = new ObjectMapper();
    private final SatuSehatCekNIK cekViaSatuSehat = new SatuSehatCekNIK();
    private final String link = koneksiDB.URLFHIRSATUSEHAT();
    private boolean modeUpdate = false;

    private static final String UC = "http://unitsofmeasure.org";

    public int updateSatuNoRawat(String noRawat) {
        modeUpdate = true;
        try { return kirimSatuNoRawat(noRawat); }
        finally { modeUpdate = false; }
    }

    /** Kirim semua record transfer internal untuk 1 no_rawat. Return jumlah Composition terkirim. */
    public int kirimSatuNoRawat(String noRawat) {
        int terkirim = 0;
        String sql =
            "select ti.no_rawat, ti.tanggal_masuk, ti.tanggal_pindah, "
          + "ifnull(ba.nm_bangsal, ti.asal_ruang) asal, ifnull(bs.nm_bangsal, ti.ruang_selanjutnya) tujuan, "
          + "ifnull(dok.nm_dokter,'') dpjp_nm, ifnull(ti.diagnosa_masuk,'') diag_masuk, ifnull(ti.diagnosa_sekarang,'') diag_skrg, "
          + "ifnull(ti.derajat,'') derajat, ifnull(ti.pendamping,'') pendamping, ifnull(ti.alergi,'') alergi, "
          + "ifnull(ti.td_sebelum_transfer,'') td_seb, ifnull(ti.nadi_sebelum_transfer,'') nadi_seb, "
          + "ifnull(ti.suhu_sebelum_transfer,'') suhu_seb, ifnull(ti.pernapasan_sebelum_transfer,'') rr_seb, ifnull(ti.saturasi_sebelum_transfer,'') spo_seb, "
          + "ifnull(ti.td_sesudah_transfer,'') td_ses, ifnull(ti.nadi_sesudah_transfer,'') nadi_ses, "
          + "ifnull(ti.suhu_sesudah_transfer,'') suhu_ses, ifnull(ti.pernapasan_sesudah_transfer,'') rr_ses, ifnull(ti.saturasi_sesudah_transfer,'') spo_ses, "
          + "ifnull(ti.keadaan_umum_sebelum_transfer,'') ku_seb, ifnull(ti.kesadaran_sebelum_transfer,'') sad_seb, "
          + "ifnull(ti.keadaan_umum_sesudah_transfer,'') ku_ses, ifnull(ti.kesadaran_sesudah_transfer,'') sad_ses, "
          + "ifnull(ti.diet,'') diet, ifnull(ti.keseimbangan_cairan,'') cairan, ifnull(ti.mobilisasi,'') mobilisasi, ifnull(ti.perawatan_luka,'') luka, "
          + "ifnull(ti.iv,'') iv, ifnull(ti.oksigen,'') oksigen, ifnull(ti.kateter,'') kateter, ifnull(ti.syring,'') syring, "
          + "ifnull(ti.ngt,'') ngt, ifnull(ti.dekubitus,'') dekubitus, ifnull(ti.infus,'') infus, ifnull(ti.cvp,'') cvp, ifnull(ti.drain,'') drain, ifnull(ti.lain,'') lain, "
          + "ifnull(ti.lab,'') lab, ifnull(ti.radiologi,'') radiologi, ifnull(ti.ctscan,'') ctscan, ifnull(ti.obat_infus,'') obat_infus, ifnull(ti.injeksi,'') injeksi, ifnull(ti.oral,'') oral, "
          + "ps.no_ktp ktppasien, ps.nm_pasien, se.id_encounter, ifnull(peg.no_ktp,'') ktppetugas, "
          + "ifnull(sp.id_composition,'') id_composition "
          + "from transfer_internal_rs ti "
          + "inner join reg_periksa rp on rp.no_rawat=ti.no_rawat "
          + "inner join pasien ps on ps.no_rkm_medis=rp.no_rkm_medis "
          + "inner join satu_sehat_encounter se on se.no_rawat=ti.no_rawat "
          + "left join bangsal ba on ba.kd_bangsal=ti.asal_ruang "
          + "left join bangsal bs on bs.kd_bangsal=ti.ruang_selanjutnya "
          + "left join dokter dok on dok.kd_dokter=ti.dpjp "
          + "left join pegawai peg on peg.nik=ti.nip_menyerahkan "
          + "left join satu_sehat_transfer_internal sp on sp.no_rawat=ti.no_rawat and sp.tanggal_masuk=ti.tanggal_masuk "
          + "where ti.no_rawat=? order by ti.tanggal_masuk";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, noRawat);
            try (ResultSet r = ps.executeQuery()) {
                while (r.next()) {
                    String idLama = s(r, "id_composition");
                    boolean sudah = !idLama.isEmpty();
                    if (modeUpdate ? !sudah : sudah) continue;
                    String idEnc = s(r, "id_encounter");
                    String ktpPas = s(r, "ktppasien");
                    if (idEnc.isEmpty() || ktpPas.isEmpty()) continue;
                    String idP = cekViaSatuSehat.tampilIDPasien(ktpPas);
                    if (idP == null || idP.isEmpty()) continue;
                    String ktpPetugas = s(r, "ktppetugas");
                    String idD = ktpPetugas.isEmpty() ? "" : cekViaSatuSehat.tampilIDParktisi(ktpPetugas);
                    if (idD == null) idD = "";
                    if (kirimSatuRow(r, idP, idD, idLama)) terkirim++;
                }
            }
        } catch (Exception e) {
            System.out.println("Notif Transfer Internal tampil : " + e);
        }
        return terkirim;
    }

    private boolean kirimSatuRow(ResultSet r, String idP, String idD, String idLama) throws Exception {
        String noRawat = s(r, "no_rawat");
        String tglMasuk = s(r, "tanggal_masuk");
        String tglPindah = s(r, "tanggal_pindah");
        String nmPas = s(r, "nm_pasien");
        String idEnc = s(r, "id_encounter");
        String dateIso = toIso(tglPindah.isEmpty() ? tglMasuk : tglPindah);
        String performerRef = (idD != null && !idD.isEmpty()) ? "Practitioner/" + idD : "Organization/" + koneksiDB.IDSATUSEHAT();
        String subjRef = "Patient/" + idP;

        // Observation tanda vital sebelum & sesudah transfer
        String sebEntries = vitalEntries(noRawat, tglMasuk, "seb", subjRef, idEnc, dateIso, performerRef,
                s(r, "td_seb"), s(r, "nadi_seb"), s(r, "suhu_seb"), s(r, "rr_seb"), s(r, "spo_seb"));
        String sesEntries = vitalEntries(noRawat, tglMasuk, "ses", subjRef, idEnc, dateIso, performerRef,
                s(r, "td_ses"), s(r, "nadi_ses"), s(r, "suhu_ses"), s(r, "rr_ses"), s(r, "spo_ses"));

        // Narasi
        String ringkas = "Transfer dari " + or(s(r, "asal"), "-") + " ke " + or(s(r, "tujuan"), "-")
                + ". DPJP: " + or(s(r, "dpjp_nm"), "-") + ". Derajat kondisi: " + or(s(r, "derajat"), "-")
                + ". Pendamping: " + or(s(r, "pendamping"), "-") + ".";
        String diag = gabung(s(r, "diag_masuk").isEmpty() ? "" : "Diagnosa masuk: " + s(r, "diag_masuk"),
                s(r, "diag_skrg").isEmpty() ? "" : "Diagnosa sekarang: " + s(r, "diag_skrg"));
        String kuSeb = gabung(s(r, "ku_seb").isEmpty() ? "" : "Keadaan umum: " + s(r, "ku_seb"),
                s(r, "sad_seb").isEmpty() ? "" : "Kesadaran: " + s(r, "sad_seb"));
        String kuSes = gabung(s(r, "ku_ses").isEmpty() ? "" : "Keadaan umum: " + s(r, "ku_ses"),
                s(r, "sad_ses").isEmpty() ? "" : "Kesadaran: " + s(r, "sad_ses"));
        String alat = alatTerpasang(r);
        String rencana = gabung(gabung(pfx("Diet", s(r, "diet")), pfx("Keseimbangan cairan", s(r, "cairan"))),
                gabung(pfx("Mobilisasi", s(r, "mobilisasi")), pfx("Perawatan luka", s(r, "luka"))));
        String hasil = gabung(gabung(pfx("Lab", s(r, "lab")), pfx("Radiologi", s(r, "radiologi"))),
                gabung(pfx("CT Scan", s(r, "ctscan")), gabung(pfx("Infus", s(r, "obat_infus")),
                gabung(pfx("Injeksi", s(r, "injeksi")), pfx("Oral", s(r, "oral"))))));

        StringBuilder sec = new StringBuilder();
        addNarrative(sec, "Ringkasan Transfer", ringkas);
        addNarrative(sec, "Diagnosis", diag);
        addEntry(sec, "Tanda Vital Sebelum Transfer", sebEntries);
        if (!kuSeb.isEmpty()) addNarrative(sec, "Keadaan Umum Sebelum Transfer", kuSeb);
        addEntry(sec, "Tanda Vital Sesudah Transfer", sesEntries);
        if (!kuSes.isEmpty()) addNarrative(sec, "Keadaan Umum Sesudah Transfer", kuSes);
        addNarrative(sec, "Alat Medis Terpasang", alat);
        addNarrative(sec, "Rencana Perawatan", rencana);
        addNarrative(sec, "Hasil Pemeriksaan Disertakan", hasil);

        StringBuilder cb = new StringBuilder();
        cb.append("{\"resourceType\":\"Composition\",");
        if (modeUpdate && !idLama.isEmpty()) cb.append("\"id\":\"").append(idLama).append("\",");
        cb.append("\"identifier\":{\"system\":\"http://sys-ids.kemkes.go.id/composition/").append(koneksiDB.IDSATUSEHAT())
          .append("\",\"value\":\"transfer-internal-").append(cleanId(noRawat)).append("-").append(cleanId(tglMasuk)).append("\"},");
        cb.append("\"status\":\"final\",");
        cb.append("\"type\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"18761-7\",\"display\":\"Transfer summary note\"}]},");
        cb.append("\"subject\":{\"reference\":\"Patient/").append(idP).append("\",\"display\":\"").append(jsonEscape(nmPas)).append("\"},");
        cb.append("\"encounter\":{\"reference\":\"Encounter/").append(idEnc).append("\"},");
        if (!dateIso.isEmpty()) cb.append("\"date\":\"").append(dateIso).append("\",");
        cb.append("\"author\":[{\"reference\":\"").append(performerRef).append("\"}],");  // wajib (Rule 10133); fallback Organization
        cb.append("\"title\":\"Transfer Internal RS\",");
        cb.append("\"custodian\":{\"reference\":\"Organization/").append(koneksiDB.IDSATUSEHAT()).append("\"},");
        cb.append("\"section\":[").append(sec).append("]}");

        String idComposition = (modeUpdate && !idLama.isEmpty())
                ? putResource("Composition", idLama, cb.toString())
                : postResource("Composition", cb.toString());
        if (!idComposition.isEmpty()) {
            try (PreparedStatement p = koneksi.prepareStatement(
                    "replace into satu_sehat_transfer_internal (no_rawat, tanggal_masuk, id_composition, tgl_kirim) values (?,?,?,now())")) {
                p.setString(1, noRawat); p.setString(2, tglMasuk); p.setString(3, idComposition); p.executeUpdate();
            } catch (Exception e) { System.out.println("Notif simpan tracking transfer : " + e); }
            return true;
        }
        return false;
    }

    /** Buat Observation tanda vital (fase 'seb'/'ses') & kembalikan entry[] gabungan. */
    private String vitalEntries(String noRawat, String tglKey, String fase, String subjRef, String idEnc,
                                String effIso, String performerRef, String td, String nadi, String suhu, String rr, String spo) {
        String sys = "", dia = "";
        if (td != null && td.contains("/")) { String[] t = td.split("/"); sys = t[0].trim(); dia = t.length > 1 ? t[1].trim() : ""; }
        String idSys = isNum(sys) ? postOrPutRes(noRawat, tglKey, "vs_" + fase + "_sys", "Observation",
                obsBody("8480-6", "Systolic blood pressure", subjRef, idEnc, effIso, performerRef, vQty(sys, "mm[Hg]", "mm[Hg]"))) : "";
        String idDia = isNum(dia) ? postOrPutRes(noRawat, tglKey, "vs_" + fase + "_dia", "Observation",
                obsBody("8462-4", "Diastolic blood pressure", subjRef, idEnc, effIso, performerRef, vQty(dia, "mm[Hg]", "mm[Hg]"))) : "";
        String idNadi = isNum(nadi) ? postOrPutRes(noRawat, tglKey, "vs_" + fase + "_nadi", "Observation",
                obsBody("8867-4", "Heart rate", subjRef, idEnc, effIso, performerRef, vQty(nadi, "/min", "/min"))) : "";
        String idSuhu = isNum(suhu) ? postOrPutRes(noRawat, tglKey, "vs_" + fase + "_suhu", "Observation",
                obsBody("8310-5", "Body temperature", subjRef, idEnc, effIso, performerRef, vQty(suhu, "Cel", "Cel"))) : "";
        String idRr = isNum(rr) ? postOrPutRes(noRawat, tglKey, "vs_" + fase + "_rr", "Observation",
                obsBody("9279-1", "Respiratory rate", subjRef, idEnc, effIso, performerRef, vQty(rr, "/min", "/min"))) : "";
        String idSpo = isNum(spo) ? postOrPutRes(noRawat, tglKey, "vs_" + fase + "_spo2", "Observation",
                obsBody("59408-5", "Oxygen saturation", subjRef, idEnc, effIso, performerRef, vQty(spo, "%", "%"))) : "";
        return joinEntries(new String[]{ refEntry("Observation", idSys), refEntry("Observation", idDia),
                refEntry("Observation", idNadi), refEntry("Observation", idSuhu), refEntry("Observation", idRr), refEntry("Observation", idSpo) });
    }

    private static String alatTerpasang(ResultSet r) {
        StringBuilder b = new StringBuilder();
        String[][] map = {{"iv","IV Cath"},{"oksigen","Oksigen"},{"kateter","Kateter"},{"syring","Syringe Pump"},
                {"ngt","NGT"},{"dekubitus","Kasur Dekubitus"},{"infus","Skema Infus"},{"cvp","CVP"},{"drain","Drain"},{"lain","Lain-lain"}};
        for (String[] m : map) {
            String v = s(r, m[0]);
            if (!v.isEmpty() && !v.equals("-") && !v.equalsIgnoreCase("tidak")) {
                if (b.length() > 0) b.append(", ");
                b.append(m[1]).append(v.equals("1") || v.equalsIgnoreCase("ya") ? "" : ": " + v);
            }
        }
        return b.length() == 0 ? "Tidak ada alat medis terpasang" : b.toString();
    }

    // ===== builder & helper =====
    private String obsBody(String code, String display, String subjectRef, String idEnc,
                           String effIso, String performerRef, String valueJson) {
        StringBuilder b = new StringBuilder();
        b.append("{\"resourceType\":\"Observation\",\"status\":\"final\",");
        b.append("\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/observation-category\",\"code\":\"vital-signs\",\"display\":\"Vital Signs\"}]}],");
        b.append("\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"").append(code).append("\",\"display\":\"").append(jsonEscape(display)).append("\"}]},");
        b.append("\"subject\":{\"reference\":\"").append(subjectRef).append("\"},");
        b.append("\"encounter\":{\"reference\":\"Encounter/").append(idEnc).append("\"},");
        if (effIso != null && !effIso.isEmpty()) b.append("\"effectiveDateTime\":\"").append(effIso).append("\",");
        b.append("\"performer\":[{\"reference\":\"").append(performerRef).append("\"}]");
        if (valueJson != null && !valueJson.isEmpty()) b.append(",").append(valueJson);
        b.append("}");
        return b.toString();
    }
    private String vQty(String num, String unit, String code) {
        return "\"valueQuantity\":{\"value\":" + num + ",\"unit\":\"" + jsonEscape(unit) + "\",\"system\":\"" + UC + "\",\"code\":\"" + code + "\"}";
    }

    private String postOrPutRes(String noRawat, String tglKey, String jenis, String resourceType, String bodyNoId) {
        String idLama = "";
        try (PreparedStatement p = koneksi.prepareStatement(
                "select id_resource from satu_sehat_laporan_res where laporan='transfer' and no_rawat=? and tanggal=? and jenis=?")) {
            p.setString(1, noRawat); p.setString(2, tglKey); p.setString(3, jenis);
            try (ResultSet r = p.executeQuery()) { if (r.next()) { String v = r.getString(1); idLama = v == null ? "" : v; } }
        } catch (Exception e) { System.out.println("Notif cek res " + jenis + " : " + e); }
        String id;
        if (modeUpdate && !idLama.isEmpty()) { id = putResource(resourceType, idLama, "{\"id\":\"" + idLama + "\"," + bodyNoId.substring(1)); }
        else if (!idLama.isEmpty()) { return idLama; }
        else { id = postResource(resourceType, bodyNoId); }
        if (id != null && !id.isEmpty()) {
            try (PreparedStatement p = koneksi.prepareStatement(
                    "replace into satu_sehat_laporan_res (laporan,no_rawat,tanggal,jenis,id_resource,tgl_kirim) values ('transfer',?,?,?,?,now())")) {
                p.setString(1, noRawat); p.setString(2, tglKey); p.setString(3, jenis); p.setString(4, id); p.executeUpdate();
            } catch (Exception e) { System.out.println("Notif simpan res " + jenis + " : " + e); }
            return id;
        }
        return idLama;
    }

    private String postResource(String resourceType, String body) {
        try {
            HttpHeaders h = new HttpHeaders(); h.setContentType(MediaType.APPLICATION_JSON);
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            System.out.println("URL : " + link + "/" + resourceType);
            System.out.println("Request JSON : " + body);
            String resp = api.getRest().exchange(link + "/" + resourceType, HttpMethod.POST, new HttpEntity(body, h), String.class).getBody();
            System.out.println("Result JSON : " + resp);
            JsonNode rid = mapper.readTree(resp).path("id");
            if (rid != null && !rid.asText().isEmpty()) return rid.asText();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.out.println("Notif POST " + resourceType + " : " + e + " | " + e.getResponseBodyAsString());
        } catch (Exception e) { System.out.println("Notif POST " + resourceType + " : " + e); }
        return "";
    }
    private String putResource(String resourceType, String id, String body) {
        try {
            HttpHeaders h = new HttpHeaders(); h.setContentType(MediaType.APPLICATION_JSON);
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            System.out.println("URL : " + link + "/" + resourceType + "/" + id);
            System.out.println("Request JSON : " + body);
            String resp = api.getRest().exchange(link + "/" + resourceType + "/" + id, HttpMethod.PUT, new HttpEntity(body, h), String.class).getBody();
            System.out.println("Result JSON : " + resp);
            JsonNode rid = mapper.readTree(resp).path("id");
            if (rid != null && !rid.asText().isEmpty()) return rid.asText();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.out.println("Notif PUT " + resourceType + " : " + e + " | " + e.getResponseBodyAsString());
        } catch (Exception e) { System.out.println("Notif PUT " + resourceType + " : " + e); }
        return "";
    }

    private static void addNarrative(StringBuilder sec, String title, String text) {
        if (text == null || text.trim().isEmpty()) return;
        if (sec.length() > 0) sec.append(",");
        sec.append("{\"title\":\"").append(jsonEscape(title)).append("\",\"text\":{\"status\":\"generated\",\"div\":\"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\">")
           .append(jsonEscape(htmlEscape(text))).append("</div>\"}}");
    }
    private static void addEntry(StringBuilder sec, String title, String entries) {
        if (entries == null || entries.isEmpty()) return;
        if (sec.length() > 0) sec.append(",");
        sec.append("{\"title\":\"").append(jsonEscape(title)).append("\",\"entry\":[").append(entries).append("]}");
    }
    private static String refEntry(String type, String id) { return (id == null || id.isEmpty()) ? "" : "{\"reference\":\"" + type + "/" + id + "\"}"; }
    private static String joinEntries(String[] arr) {
        StringBuilder b = new StringBuilder();
        for (String e : arr) { if (e != null && !e.isEmpty()) { if (b.length() > 0) b.append(","); b.append(e); } }
        return b.toString();
    }
    private static String gabung(String a, String b) {
        a = a == null ? "" : a.trim(); b = b == null ? "" : b.trim();
        if (a.isEmpty()) return b; if (b.isEmpty()) return a; return a + ". " + b;
    }
    private static String pfx(String label, String v) { return (v == null || v.trim().isEmpty()) ? "" : label + ": " + v.trim(); }
    private static String or(String v, String d) { return (v == null || v.trim().isEmpty()) ? d : v.trim(); }
    private static String s(ResultSet rd, String col) { try { String v = rd.getString(col); return v == null ? "" : v.trim(); } catch (Exception e) { return ""; } }
    private static boolean isNum(String v) { return v != null && v.trim().matches("\\d+(\\.\\d+)?"); }

    private static String jsonEscape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default: if (c < 0x20) sb.append(String.format("\\u%04x", (int) c)); else sb.append(c);
            }
        }
        return sb.toString();
    }
    private static String htmlEscape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>");
    }
    private static String toIso(String tgl) {
        if (tgl == null) return "";
        String t = tgl.trim();
        if (t.equals("") || t.startsWith("0000-00-00") || t.length() < 10) return "";
        if (t.length() == 10) return fungsi.IsoTime.toUtc(t, "00:00:00");
        return fungsi.IsoTime.toUtc(t);
    }
    private static String cleanId(String s) {
        if (s == null) return "";
        return s.replace("/", "").replace(":", "").replace(" ", "").replace("-", "");
    }
}
