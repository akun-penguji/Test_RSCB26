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
 * Kirim Penilaian Awal Medis IGD (tabel penilaian_medis_igd) ke SatuSehat.
 *
 * Sesuai Modul IGD SatuSehat: assessment awal IGD dikirim sebagai resource
 * DISKRET yang menempel ke Encounter IGD (tidak ada dokumen khusus), plus
 * Composition ringkas "Emergency medicine Note" (LOINC 34111-5) yang me-link
 * resource tersebut agar juga tampil sebagai satu dokumen.
 *
 * Resource yang dibuat: Condition keluhan utama (chief-complaint, SNOMED
 * 404684003), Observation tanda vital (TD/Nadi/RR/Suhu/SpO2/GCS/BB/TB),
 * Observation pemeriksaan fisik (LOINC 29545-1), Condition diagnosis.
 * Alergi di-link dari satu_sehat_allergy_intolerance bila sudah ada.
 *
 * Dipakai lewat form Kirim Bundel (kirimSatuNoRawat / updateSatuNoRawat),
 * mengikuti pola sender laporan lain (headless, output via System.out).
 */
public final class SatuSehatKirimPenilaianMedisIGD {

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

    /** Kirim penilaian medis IGD untuk 1 no_rawat. Return jumlah Composition terkirim. */
    public int kirimSatuNoRawat(String noRawat) {
        int terkirim = 0;
        String sql =
            "select pm.no_rawat, pm.tanggal, pm.kd_dokter, ifnull(peg.no_ktp,'') ktpdokter, ifnull(dok.nm_dokter,'') nm_dokter, "
          + "ps.no_ktp ktppasien, ps.nm_pasien, se.id_encounter, "
          + "ifnull(pm.anamnesis,'') anamnesis, ifnull(pm.keluhan_utama,'') keluhan_utama, ifnull(pm.rps,'') rps, "
          + "ifnull(pm.rpd,'') rpd, ifnull(pm.rpk,'') rpk, ifnull(pm.alergi,'') alergi, ifnull(pm.gcs,'') gcs, "
          + "ifnull(pm.kesadaran,'') kesadaran, ifnull(pm.td,'') td, ifnull(pm.nadi,'') nadi, ifnull(pm.rr,'') rr, "
          + "ifnull(pm.suhu,'') suhu, ifnull(pm.spo,'') spo, ifnull(pm.bb,'') bb, ifnull(pm.tb,'') tb, "
          + "ifnull(pm.ket_fisik,'') ket_fisik, ifnull(pm.ket_lokalis,'') ket_lokalis, ifnull(pm.diagnosis,'') diagnosis, "
          + "ifnull(pm.tata,'') tata, ifnull(sp.id_composition,'') id_composition "
          + "from penilaian_medis_igd pm "
          + "inner join reg_periksa rp on rp.no_rawat=pm.no_rawat "
          + "inner join pasien ps on ps.no_rkm_medis=rp.no_rkm_medis "
          + "inner join satu_sehat_encounter se on se.no_rawat=pm.no_rawat "
          + "left join dokter dok on dok.kd_dokter=pm.kd_dokter "
          + "left join pegawai peg on peg.nik=pm.kd_dokter "
          + "left join satu_sehat_penilaian_igd sp on sp.no_rawat=pm.no_rawat and sp.tanggal=pm.tanggal "
          + "where pm.no_rawat=? order by pm.tanggal";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, noRawat);
            try (ResultSet r = ps.executeQuery()) {
                while (r.next()) {
                    String idLama = s(r, "id_composition");
                    boolean sudah = !idLama.isEmpty();
                    if (modeUpdate ? !sudah : sudah) continue;   // kirim: lewati yg sudah; update: lewati yg belum
                    String idEnc = s(r, "id_encounter");
                    String ktpPas = s(r, "ktppasien");
                    if (idEnc.isEmpty() || ktpPas.isEmpty()) continue;
                    String idP = cekViaSatuSehat.tampilIDPasien(ktpPas);
                    if (idP == null || idP.isEmpty()) continue;
                    String ktpDok = s(r, "ktpdokter");
                    String idD = (ktpDok.isEmpty()) ? "" : cekViaSatuSehat.tampilIDParktisi(ktpDok);
                    if (idD == null) idD = "";
                    if (kirimSatuRow(r, idP, idD, idLama)) terkirim++;
                }
            }
        } catch (Exception e) {
            System.out.println("Notif Penilaian IGD tampil : " + e);
        }
        return terkirim;
    }

    private boolean kirimSatuRow(ResultSet r, String idP, String idD, String idLama) throws Exception {
        String noRawat = s(r, "no_rawat");
        String tglOp   = s(r, "tanggal");
        String nmPas   = s(r, "nm_pasien");
        String nmDok   = s(r, "nm_dokter");
        String idEnc   = s(r, "id_encounter");
        String anamnesis = s(r, "anamnesis"), keluhan = s(r, "keluhan_utama"), rps = s(r, "rps"),
               rpd = s(r, "rpd"), rpk = s(r, "rpk"), gcs = s(r, "gcs"), kesadaran = s(r, "kesadaran"),
               td = s(r, "td"), nadi = s(r, "nadi"), rr = s(r, "rr"), suhu = s(r, "suhu"), spo = s(r, "spo"),
               bb = s(r, "bb"), tb = s(r, "tb"), ketFisik = s(r, "ket_fisik"), ketLokalis = s(r, "ket_lokalis"),
               diagnosis = s(r, "diagnosis"), tata = s(r, "tata");
        String dateIso = toIso(tglOp);
        String performerRef = (idD != null && !idD.isEmpty()) ? "Practitioner/" + idD : "Organization/" + koneksiDB.IDSATUSEHAT();
        String subjRef = "Patient/" + idP;

        // ---- Resource diskret ----
        String idKeluhan = keluhan.isEmpty() ? "" : postOrPutRes(noRawat, tglOp, "keluhan", "Condition",
                condBody("404684003", "Clinical finding", keluhan, subjRef, idEnc, dateIso, "encounter-diagnosis"));
        String sys = "", dia = "";
        if (td.contains("/")) { String[] t = td.split("/"); sys = t[0].trim(); dia = t.length > 1 ? t[1].trim() : ""; }
        String idSys = isNum(sys) ? postOrPutRes(noRawat, tglOp, "vs_sys", "Observation",
                obsBody("8480-6", "Systolic blood pressure", subjRef, idEnc, dateIso, performerRef, vQty(sys, "mm[Hg]", "mm[Hg]"))) : "";
        String idDia = isNum(dia) ? postOrPutRes(noRawat, tglOp, "vs_dia", "Observation",
                obsBody("8462-4", "Diastolic blood pressure", subjRef, idEnc, dateIso, performerRef, vQty(dia, "mm[Hg]", "mm[Hg]"))) : "";
        String idNadi = isNum(nadi) ? postOrPutRes(noRawat, tglOp, "vs_nadi", "Observation",
                obsBody("8867-4", "Heart rate", subjRef, idEnc, dateIso, performerRef, vQty(nadi, "/min", "/min"))) : "";
        String idRr = isNum(rr) ? postOrPutRes(noRawat, tglOp, "vs_rr", "Observation",
                obsBody("9279-1", "Respiratory rate", subjRef, idEnc, dateIso, performerRef, vQty(rr, "/min", "/min"))) : "";
        String idSuhu = isNum(suhu) ? postOrPutRes(noRawat, tglOp, "vs_suhu", "Observation",
                obsBody("8310-5", "Body temperature", subjRef, idEnc, dateIso, performerRef, vQty(suhu, "Cel", "Cel"))) : "";
        String idSpo = isNum(spo) ? postOrPutRes(noRawat, tglOp, "vs_spo2", "Observation",
                obsBody("59408-5", "Oxygen saturation", subjRef, idEnc, dateIso, performerRef, vQty(spo, "%", "%"))) : "";
        String idGcs = isNum(gcs) ? postOrPutRes(noRawat, tglOp, "vs_gcs", "Observation",
                obsBody("9269-2", "Glasgow coma score total", subjRef, idEnc, dateIso, performerRef, vQty(gcs, "{score}", "{score}"))) : "";
        String idBb = isNum(bb) ? postOrPutRes(noRawat, tglOp, "vs_bb", "Observation",
                obsBody("29463-7", "Body weight", subjRef, idEnc, dateIso, performerRef, vQty(bb, "kg", "kg"))) : "";
        String idTb = isNum(tb) ? postOrPutRes(noRawat, tglOp, "vs_tb", "Observation",
                obsBody("8302-2", "Body height", subjRef, idEnc, dateIso, performerRef, vQty(tb, "cm", "cm"))) : "";
        String fisikTxt = gabung(ketFisik, ketLokalis);
        String idFisik = fisikTxt.isEmpty() ? "" : postOrPutRes(noRawat, tglOp, "fisik", "Observation",
                obsBody("29545-1", "Physical findings Narrative", subjRef, idEnc, dateIso, performerRef, vStr(fisikTxt)));
        String idDiag = diagnosis.isEmpty() ? "" : postOrPutRes(noRawat, tglOp, "diagnosis", "Condition",
                condBody(null, null, diagnosis, subjRef, idEnc, dateIso, "encounter-diagnosis"));
        String allergyEntries = linkAllergyEntries(noRawat);

        // ---- Composition "Emergency medicine Note" (34111-5) ----
        StringBuilder sec = new StringBuilder();
        String anamTxt = gabung(gabung(anamnesis, rps.isEmpty() ? "" : "RPS: " + rps),
                gabung(rpd.isEmpty() ? "" : "RPD: " + rpd, rpk.isEmpty() ? "" : "RPK: " + rpk));
        addNarrative(sec, "Anamnesis", anamTxt);
        addEntry(sec, "Chief Complaint", joinEntries(new String[]{ refEntry("Condition", idKeluhan) }));
        addEntry(sec, "Vital Signs", joinEntries(new String[]{
                refEntry("Observation", idSys), refEntry("Observation", idDia), refEntry("Observation", idNadi),
                refEntry("Observation", idRr), refEntry("Observation", idSuhu), refEntry("Observation", idSpo),
                refEntry("Observation", idGcs), refEntry("Observation", idBb), refEntry("Observation", idTb) }));
        addEntry(sec, "Physical Exam", joinEntries(new String[]{ refEntry("Observation", idFisik) }));
        addEntry(sec, "Diagnosis", joinEntries(new String[]{ refEntry("Condition", idDiag) }));
        addEntry(sec, "Allergies and Intolerances Section", allergyEntries);
        addNarrative(sec, "Plan of Care", tata);
        if (kesadaran != null && !kesadaran.isEmpty()) addNarrative(sec, "Kesadaran", kesadaran);

        StringBuilder cb = new StringBuilder();
        cb.append("{\"resourceType\":\"Composition\",");
        if (modeUpdate && !idLama.isEmpty()) cb.append("\"id\":\"").append(idLama).append("\",");
        cb.append("\"identifier\":{\"system\":\"http://sys-ids.kemkes.go.id/composition/").append(koneksiDB.IDSATUSEHAT())
          .append("\",\"value\":\"penilaian-medis-igd-").append(cleanId(noRawat)).append("-").append(cleanId(tglOp)).append("\"},");
        cb.append("\"status\":\"final\",");
        cb.append("\"type\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"34111-5\",\"display\":\"Emergency medicine Note\"}]},");
        cb.append("\"subject\":{\"reference\":\"Patient/").append(idP).append("\",\"display\":\"").append(jsonEscape(nmPas)).append("\"},");
        cb.append("\"encounter\":{\"reference\":\"Encounter/").append(idEnc).append("\"},");
        if (!dateIso.isEmpty()) cb.append("\"date\":\"").append(dateIso).append("\",");
        // author wajib (Rule 10133): Practitioner bila ketemu, selain itu fallback Organization.
        if (idD != null && !idD.isEmpty())
            cb.append("\"author\":[{\"reference\":\"Practitioner/").append(idD).append("\",\"display\":\"").append(jsonEscape(nmDok)).append("\"}],");
        else
            cb.append("\"author\":[{\"reference\":\"Organization/").append(koneksiDB.IDSATUSEHAT()).append("\"}],");
        cb.append("\"title\":\"Penilaian Medis IGD\",");
        cb.append("\"custodian\":{\"reference\":\"Organization/").append(koneksiDB.IDSATUSEHAT()).append("\"},");
        cb.append("\"section\":[").append(sec).append("]}");

        String idComposition = (modeUpdate && !idLama.isEmpty())
                ? putResource("Composition", idLama, cb.toString())
                : postResource("Composition", cb.toString());
        if (!idComposition.isEmpty()) {
            try (PreparedStatement p = koneksi.prepareStatement(
                    "replace into satu_sehat_penilaian_igd (no_rawat, tanggal, id_composition, tgl_kirim) values (?,?,?,now())")) {
                p.setString(1, noRawat); p.setString(2, tglOp); p.setString(3, idComposition); p.executeUpdate();
            } catch (Exception e) { System.out.println("Notif simpan tracking IGD : " + e); }
            return true;
        }
        return false;
    }

    // ===== builder & helper =====
    private String obsBody(String code, String display, String subjectRef, String idEnc,
                           String effIso, String performerRef, String valueJson) {
        StringBuilder b = new StringBuilder();
        b.append("{\"resourceType\":\"Observation\",\"status\":\"final\",");
        b.append("\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/observation-category\",\"code\":\"exam\",\"display\":\"Exam\"}]}],");
        b.append("\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"").append(code).append("\",\"display\":\"").append(jsonEscape(display)).append("\"}]},");
        b.append("\"subject\":{\"reference\":\"").append(subjectRef).append("\"},");
        b.append("\"encounter\":{\"reference\":\"Encounter/").append(idEnc).append("\"},");
        if (effIso != null && !effIso.isEmpty()) b.append("\"effectiveDateTime\":\"").append(effIso).append("\",");
        b.append("\"performer\":[{\"reference\":\"").append(performerRef).append("\"}]");
        if (valueJson != null && !valueJson.isEmpty()) b.append(",").append(valueJson);
        b.append("}");
        return b.toString();
    }
    /** Condition; codeSnomed/display boleh null (pakai code.text saja). category = chief-complaint | encounter-diagnosis. */
    private String condBody(String codeSnomed, String display, String text, String subjectRef, String idEnc, String recIso, String kategori) {
        StringBuilder b = new StringBuilder();
        b.append("{\"resourceType\":\"Condition\",");
        b.append("\"clinicalStatus\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-clinical\",\"code\":\"active\"}]},");
        b.append("\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-category\",\"code\":\"")
         .append(kategori).append("\"}]}],");
        b.append("\"code\":{");
        if (codeSnomed != null && !codeSnomed.isEmpty())
            b.append("\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"").append(codeSnomed).append("\",\"display\":\"").append(jsonEscape(display)).append("\"}],");
        b.append("\"text\":\"").append(jsonEscape(text)).append("\"},");
        b.append("\"subject\":{\"reference\":\"").append(subjectRef).append("\"},");
        b.append("\"encounter\":{\"reference\":\"Encounter/").append(idEnc).append("\"}");
        if (recIso != null && !recIso.isEmpty()) b.append(",\"recordedDate\":\"").append(recIso).append("\"");
        b.append("}");
        return b.toString();
    }
    private String vStr(String s) { return "\"valueString\":\"" + jsonEscape(s) + "\""; }
    private String vQty(String num, String unit, String code) {
        return "\"valueQuantity\":{\"value\":" + num + ",\"unit\":\"" + jsonEscape(unit) + "\",\"system\":\"" + UC + "\",\"code\":\"" + code + "\"}";
    }
    private String linkAllergyEntries(String noRawat) {
        StringBuilder b = new StringBuilder();
        try (PreparedStatement p = koneksi.prepareStatement(
                "select id_allergy_intolerance from satu_sehat_allergy_intolerance where no_rawat=? and ifnull(id_allergy_intolerance,'')<>''")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) { String id = r.getString(1);
                    if (id != null && !id.isEmpty()) { if (b.length() > 0) b.append(","); b.append("{\"reference\":\"AllergyIntolerance/").append(id).append("\"}"); } }
            }
        } catch (Exception e) { System.out.println("Notif link allergy IGD : " + e); }
        return b.toString();
    }

    private String postOrPutRes(String noRawat, String tglOp, String jenis, String resourceType, String bodyNoId) {
        String idLama = "";
        try (PreparedStatement p = koneksi.prepareStatement(
                "select id_resource from satu_sehat_laporan_res where laporan='penilaian_igd' and no_rawat=? and tanggal=? and jenis=?")) {
            p.setString(1, noRawat); p.setString(2, tglOp); p.setString(3, jenis);
            try (ResultSet r = p.executeQuery()) { if (r.next()) { String v = r.getString(1); idLama = v == null ? "" : v; } }
        } catch (Exception e) { System.out.println("Notif cek res " + jenis + " : " + e); }
        String id;
        if (modeUpdate && !idLama.isEmpty()) { id = putResource(resourceType, idLama, "{\"id\":\"" + idLama + "\"," + bodyNoId.substring(1)); }
        else if (!idLama.isEmpty()) { return idLama; }
        else { id = postResource(resourceType, bodyNoId); }
        if (id != null && !id.isEmpty()) {
            try (PreparedStatement p = koneksi.prepareStatement(
                    "replace into satu_sehat_laporan_res (laporan,no_rawat,tanggal,jenis,id_resource,tgl_kirim) values ('penilaian_igd',?,?,?,?,now())")) {
                p.setString(1, noRawat); p.setString(2, tglOp); p.setString(3, jenis); p.setString(4, id); p.executeUpdate();
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
