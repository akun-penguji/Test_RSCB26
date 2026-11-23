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
 * Eligibility TTE — PractitionerRole (skema "Pengecekan SSM Practitioner eligible
 * untuk TTE di RME Faskes").
 *
 * Sebelum TTE, praktisi harus punya PractitionerRole di SatuSehat. Alur:
 *   GET PractitionerRole?organization=..&practitioner=..
 *     -> ada  : eligible (pakai id yang ada)
 *     -> kosong: POST PractitionerRole (SIP, profesi, specialty, email) lalu eligible.
 *
 * Data diambil dari tabel dokter (email, no_ijn_praktek=SIP, kd_sps) dan mapping
 * satu_sehat_mapping_spesialis (kd_sps -> SNOMED specialty). Bila kode specialty
 * ditolak SatuSehat, otomatis fallback ke General practitioner (394814009).
 */
public final class SatuSehatPractitionerRole {

    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String link = koneksiDB.URLFHIRSATUSEHAT();

    private static final String SIP_START = "2022-01-01T00:00:00+00:00";
    private static final String SIP_END   = "2027-12-31T00:00:00+00:00";

    /**
     * Pastikan praktisi punya PractitionerRole. Return id_role bila eligible, "" bila gagal.
     * Idempoten: GET dulu, POST hanya bila belum ada, hasil dicatat di tracking.
     */
    public String pastikanRole(String idPractitioner, String kdDokter, String nmDokter) {
        if (idPractitioner == null || idPractitioner.isEmpty()) return "";
        String org = koneksiDB.IDSATUSEHAT();

        // 1) GET — sudah punya role?
        try {
            String url = link + "/PractitionerRole/?organization=" + org + "&practitioner=" + idPractitioner;
            String resp = get(url);
            JsonNode root = mapper.readTree(resp);
            if (root.path("total").asInt(0) > 0) {
                JsonNode entry = root.path("entry");
                String idRole = entry.isArray() && entry.size() > 0 ? entry.get(0).path("resource").path("id").asText() : "";
                simpanTracking(idPractitioner, kdDokter, idRole);
                return idRole == null ? "" : idRole;
            }
        } catch (Exception e) { System.out.println("Notif GET PractitionerRole : " + e); }

        // 2) POST — belum ada, buat baru
        String email = "", sip = "", kdSps = "";
        try (PreparedStatement p = koneksi.prepareStatement(
                "select ifnull(email,'') email, ifnull(no_ijn_praktek,'') sip, ifnull(kd_sps,'') kd_sps from dokter where kd_dokter=?")) {
            p.setString(1, kdDokter);
            try (ResultSet r = p.executeQuery()) { if (r.next()) { email = r.getString("email"); sip = r.getString("sip"); kdSps = r.getString("kd_sps"); } }
        } catch (Exception e) { System.out.println("Notif data dokter role : " + e); }

        String[] sp = specialty(kdSps);   // {code, display}
        String id = postRole(idPractitioner, nmDokter, org, sip, email, sp[0], sp[1]);
        // fallback ke General practitioner bila specialty ditolak
        if (id.isEmpty() && !sp[0].equals("394814009")) {
            id = postRole(idPractitioner, nmDokter, org, sip, email, "394814009", "General practitioner");
        }
        if (!id.isEmpty()) simpanTracking(idPractitioner, kdDokter, id);
        return id;
    }

    private String[] specialty(String kdSps) {
        String code = "394814009", disp = "General practitioner";
        if (kdSps != null && !kdSps.isEmpty()) {
            try (PreparedStatement p = koneksi.prepareStatement(
                    "select snomed_code, snomed_display from satu_sehat_mapping_spesialis where kd_sps=?")) {
                p.setString(1, kdSps);
                try (ResultSet r = p.executeQuery()) {
                    if (r.next()) {
                        String c = r.getString(1), d = r.getString(2);
                        if (c != null && !c.trim().isEmpty()) { code = c.trim(); disp = d == null ? "" : d.trim(); }
                    }
                }
            } catch (Exception e) { System.out.println("Notif mapping spesialis : " + e); }
        }
        return new String[]{ code, disp };
    }

    private String postRole(String idPractitioner, String nmDokter, String org, String sip, String email,
                            String spCode, String spDisp) {
        StringBuilder b = new StringBuilder();
        b.append("{\"resourceType\":\"PractitionerRole\",\"active\":true,");
        b.append("\"code\":[{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"158965000\",\"display\":\"Medical practitioner\"}]}],");
        b.append("\"identifier\":[{\"system\":\"https://fhir.kemkes.go.id/id/sip-number\",\"use\":\"official\",")
         .append("\"value\":\"").append(jsonEscape(sip.isEmpty() ? "-" : sip)).append("\",")
         .append("\"type\":{\"coding\":[{\"system\":\"http://terminology.kemkes.go.id\",\"code\":\"sip_reguler\",\"display\":\"SIP Reguler\"}]},")
         .append("\"period\":{\"start\":\"").append(SIP_START).append("\",\"end\":\"").append(SIP_END).append("\"}}],");
        b.append("\"organization\":{\"reference\":\"Organization/").append(org).append("\"},");
        b.append("\"period\":{\"start\":\"").append(SIP_START).append("\",\"end\":\"").append(SIP_END).append("\"},");
        b.append("\"practitioner\":{\"reference\":\"Practitioner/").append(idPractitioner).append("\"");
        if (nmDokter != null && !nmDokter.trim().isEmpty()) b.append(",\"display\":\"").append(jsonEscape(nmDokter)).append("\"");
        b.append("},");
        b.append("\"specialty\":[{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"").append(spCode)
         .append("\",\"display\":\"").append(jsonEscape(spDisp)).append("\"}]}]");
        if (email != null && !email.trim().isEmpty())
            b.append(",\"telecom\":[{\"system\":\"email\",\"use\":\"work\",\"value\":\"").append(jsonEscape(email.trim())).append("\"}]");
        b.append("}");
        try {
            HttpHeaders h = new HttpHeaders(); h.setContentType(MediaType.APPLICATION_JSON);
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            System.out.println("URL : " + link + "/PractitionerRole");
            System.out.println("Request JSON : " + b);
            String resp = api.getRest().exchange(link + "/PractitionerRole", HttpMethod.POST, new HttpEntity(b.toString(), h), String.class).getBody();
            System.out.println("Result JSON : " + resp);
            JsonNode rid = mapper.readTree(resp).path("id");
            if (rid != null && !rid.asText().isEmpty()) return rid.asText();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.out.println("Notif POST PractitionerRole : " + e + " | " + e.getResponseBodyAsString());
        } catch (Exception e) { System.out.println("Notif POST PractitionerRole : " + e); }
        return "";
    }

    private void simpanTracking(String idPractitioner, String kdDokter, String idRole) {
        if (idRole == null || idRole.isEmpty()) return;
        try (PreparedStatement p = koneksi.prepareStatement(
                "replace into satu_sehat_practitioner_role (id_practitioner, kd_dokter, id_role, tgl_kirim) values (?,?,?,now())")) {
            p.setString(1, idPractitioner); p.setString(2, kdDokter); p.setString(3, idRole); p.executeUpdate();
        } catch (Exception e) { System.out.println("Notif simpan tracking role : " + e); }
    }

    private String get(String url) throws Exception {
        HttpHeaders h = new HttpHeaders(); h.setContentType(MediaType.APPLICATION_JSON);
        h.add("Authorization", "Bearer " + api.TokenSatuSehat());
        return api.getRest().exchange(url, HttpMethod.GET, new HttpEntity(h), String.class).getBody();
    }

    private static String jsonEscape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default: if (c < 0x20) sb.append(String.format("\\u%04x", (int) c)); else sb.append(c);
            }
        }
        return sb.toString();
    }
}
