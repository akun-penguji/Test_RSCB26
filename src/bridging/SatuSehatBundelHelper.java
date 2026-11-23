/*
  Orchestrator "Kirim Bundel SatuSehat" — kirim resource klinis turunan untuk 1 kunjungan.
  Idempoten (cek tabel tracking yang sama dgn sender individual).
  Prasyarat: Encounter sudah terkirim (satu_sehat_encounter.id_encounter).
  Disediakan per-resource (kirimCondition/kirimProcedure) + kirimBundel (semua).
  By Mas Elkhanza
 */
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

public final class SatuSehatBundelHelper {

    private static final ObjectMapper mapper = new ObjectMapper();
    private SatuSehatBundelHelper() {}

    private static String esc(String s){ return s==null?"":s.replace("\\","\\\\").replace("\"","\\\""); }

    private static String[] snomedCategoryForIcd9(String code) {
        if (code == null || code.length() < 2) return new String[]{"103693007","Diagnostic procedure"};
        try {
            int chap = Integer.parseInt(code.substring(0,2));
            if (chap <= 86) return new String[]{"387713003","Surgical procedure"};
            if (chap <= 89) return new String[]{"103693007","Diagnostic procedure"};
            return new String[]{"277132007","Therapeutic procedure"};
        } catch (NumberFormatException e) { return new String[]{"103693007","Diagnostic procedure"}; }
    }

    /** POST resource baru. Package-private: dipakai juga oleh SatuSehatAlkes. */
    static String post(ApiSatuSehat api, String link, String resourceType, String json, StringBuilder log) {
        if (log != null) {
            log.append("<div style='background:#f6f6f6;border:1px solid #e0e0e0;padding:3px;margin:3px 0;"
                    + "font-family:monospace;font-size:9px;color:#444'>&rarr; POST /").append(resourceType).append("<br>")
               .append(json.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")).append("</div>");
        }
        try {
            HttpHeaders h = new HttpHeaders();
            h.setContentType(MediaType.APPLICATION_JSON);
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            System.out.println("BUNDEL URL : " + link + "/" + resourceType);
            System.out.println("BUNDEL REQ : " + json);
            HttpEntity req = new HttpEntity(json, h);
            String resp = api.getRest().exchange(link + "/" + resourceType, HttpMethod.POST, req, String.class).getBody();
            System.out.println("BUNDEL RESP : " + resp);
            JsonNode id = mapper.readTree(resp).path("id");
            return id == null ? "" : id.asText();
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.out.println("BUNDEL ERR " + resourceType + " : " + e + " => " + e.getResponseBodyAsString());
        } catch (Exception e) { System.out.println("BUNDEL ERR " + resourceType + " : " + e); }
        return "";
    }

    /** PUT resource yang sudah punya id (dipakai siklus hidup Encounter). true bila 2xx. */
    static boolean put(ApiSatuSehat api, String link, String resourceType, String id, String json, StringBuilder log) {
        if (log != null) {
            log.append("<div style='background:#f6f6f6;border:1px solid #e0e0e0;padding:3px;margin:3px 0;"
                    + "font-family:monospace;font-size:9px;color:#444'>&rarr; PUT /").append(resourceType).append("/").append(id).append("<br>")
               .append(json.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")).append("</div>");
        }
        try {
            HttpHeaders h = new HttpHeaders();
            h.setContentType(MediaType.APPLICATION_JSON);
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            System.out.println("BUNDEL URL : " + link + "/" + resourceType + "/" + id);
            System.out.println("BUNDEL REQ : " + json);
            HttpEntity req = new HttpEntity(json, h);
            String resp = api.getRest().exchange(link + "/" + resourceType + "/" + id, HttpMethod.PUT, req, String.class).getBody();
            System.out.println("BUNDEL RESP : " + resp);
            return true;
        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            System.out.println("BUNDEL ERR PUT " + resourceType + " : " + e + " => " + e.getResponseBodyAsString());
            if (log != null) log.append("&nbsp;&nbsp;<font color='red'>✗ ")
                    .append(e.getResponseBodyAsString().replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")).append("</font><br>");
        } catch (Exception e) {
            System.out.println("BUNDEL ERR PUT " + resourceType + " : " + e);
            if (log != null) log.append("&nbsp;&nbsp;<font color='red'>✗ ").append(e.toString()).append("</font><br>");
        }
        return false;
    }

    /**
     * Encounter.location[] — WAJIB diisi supaya kiriman setara form "Kirim Encounter".
     * Ranap memakai kamar terakhir (satu_sehat_mapping_lokasi_ranap), selain itu poli
     * (satu_sehat_mapping_lokasi_ralan). Bila kamar belum di-mapping, jatuh ke poli.
     * String kosong berarti benar-benar tidak ada mapping -> field tidak dikirim.
     */
    private static String lokasiJson(Connection kon, String noRawat, boolean isRanap, String startUtc, String endUtc) {
        String idLok = "", nama = "", kelas = "";
        if (isRanap) {
            try (PreparedStatement p = kon.prepareStatement(
                    "select ifnull(m.id_lokasi_satusehat,'') id, ifnull(b.nm_bangsal,ki.kd_kamar) nm, ifnull(k.kelas,'') kelas "
                  + "from kamar_inap ki left join kamar k on k.kd_kamar=ki.kd_kamar "
                  + "left join bangsal b on b.kd_bangsal=k.kd_bangsal "
                  + "left join satu_sehat_mapping_lokasi_ranap m on m.kd_kamar=ki.kd_kamar "
                  + "where ki.no_rawat=? and ifnull(ki.stts_pulang,'')<>'Pindah Kamar' "
                  + "order by ki.tgl_masuk desc, ki.jam_masuk desc limit 1")) {
                p.setString(1, noRawat);
                try (ResultSet r = p.executeQuery()) {
                    if (r.next()) { idLok = r.getString("id"); nama = r.getString("nm"); kelas = r.getString("kelas"); }
                }
            } catch (Exception e) { System.out.println("BUNDEL lokasi ranap err: " + e); }
        }
        if (idLok == null || idLok.isEmpty()) {
            try (PreparedStatement p = kon.prepareStatement(
                    "select ifnull(m.id_lokasi_satusehat,'') id, ifnull(pol.nm_poli,rp.kd_poli) nm from reg_periksa rp "
                  + "left join poliklinik pol on pol.kd_poli=rp.kd_poli "
                  + "left join satu_sehat_mapping_lokasi_ralan m on m.kd_poli=rp.kd_poli where rp.no_rawat=?")) {
                p.setString(1, noRawat);
                try (ResultSet r = p.executeQuery()) {
                    if (r.next()) { idLok = r.getString("id"); nama = r.getString("nm"); }
                }
            } catch (Exception e) { System.out.println("BUNDEL lokasi ralan err: " + e); }
        }
        if (idLok == null || idLok.isEmpty()) return "";
        return SatuSehatKirimEncounter.buildLocationJson(idLok, nama, isRanap, startUtc, endUtc, kelas);
    }

    /**
     * Encounter.diagnosis dari satu_sehat_condition — WAJIB untuk status finished
     * (Rule 10457). rank 1 = diagnosa utama (DD), sisanya komorbid (CM). Salinan
     * logika SatuSehatKirimEncounter.getDiagnosisEncounter supaya Bundel konsisten.
     */
    private static String diagnosisEncounter(Connection kon, String noRawat) {
        StringBuilder d = new StringBuilder();
        try (PreparedStatement p = kon.prepareStatement(
                "select sc.id_condition from satu_sehat_condition sc "
              + "inner join diagnosa_pasien dp on dp.no_rawat=sc.no_rawat and dp.kd_penyakit=sc.kd_penyakit and dp.status=sc.status "
              + "where sc.no_rawat=? and ifnull(sc.id_condition,'')<>'' order by dp.prioritas")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                int rank = 0;
                while (r.next()) {
                    rank++;
                    if (d.length() > 0) d.append(",");
                    String[] peran = (rank == 1) ? new String[]{"DD","Discharge diagnosis"}
                                                 : new String[]{"CM","Comorbidity diagnosis"};
                    d.append("{\"condition\":{\"reference\":\"Condition/").append(r.getString("id_condition")).append("\"},")
                     .append("\"use\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/diagnosis-role\",")
                     .append("\"code\":\"").append(peran[0]).append("\",\"display\":\"").append(peran[1]).append("\"}]},")
                     .append("\"rank\":").append(rank).append("}");
                }
            }
        } catch (Exception e) { System.out.println("BUNDEL diagnosis err: " + e); }
        return d.toString();
    }

    /** Map kamar_inap.stts_pulang -> HL7 DischargeDisposition (sama dgn form Encounter). */
    private static String[] mapDischargeDisposition(String sttsPulang) {
        String s = sttsPulang == null ? "" : sttsPulang.trim();
        switch (s) {
            case "Sehat": case "Sembuh": case "Membaik": case "Atas Persetujuan Dokter":
                return new String[]{"home","Home"};
            case "Isoman":      return new String[]{"alt-home","Alternative home"};
            case "Rujuk":       return new String[]{"other-hcf","Other healthcare facility"};
            case "APS": case "Pulang Paksa": case "Atas Permintaan Sendiri":
                return new String[]{"aadvice","Left against advice"};
            case "Meninggal":   return new String[]{"exp","Expired"};
            default:            return new String[]{"oth","Other"};
        }
    }

    /**
     * Waktu selesai pelayanan (ISO WIB) untuk Encounter.period.end. Ranap memakai
     * tanggal keluar kamar; ralan/IGD memakai pemeriksaan terakhir, lalu tindakan
     * terakhir. "" berarti pasien belum selesai dilayani -> Encounter belum boleh
     * di-finish (playbook mewajibkan period.end terisi waktu selesai).
     */
    private static String akhirPelayananWib(Connection kon, String noRawat, boolean isRanap) {
        if (isRanap) {
            // Ranap HANYA boleh selesai lewat tgl_keluar kamar. Tidak ada fallback ke
            // pemeriksaan/tindakan: pasien yang masih dirawat akan ter-finish dengan
            // period.end beberapa detik setelah admisi — salah dan tidak bisa ditarik.
            return cariSatu(kon,
                "select concat(tgl_keluar,'T',jam_keluar,'+07:00') from kamar_inap where no_rawat=? "
              + "and tgl_keluar is not null and tgl_keluar<>'0000-00-00' and ifnull(stts_pulang,'')<>'Pindah Kamar' "
              + "order by tgl_keluar desc, jam_keluar desc limit 1", noRawat);
        }
        String v = cariSatu(kon,
            "select concat(tgl_perawatan,'T',jam_rawat,'+07:00') from pemeriksaan_ralan where no_rawat=? "
          + "order by tgl_perawatan desc, jam_rawat desc limit 1", noRawat);
        if (!v.isEmpty()) return v;
        return cariSatu(kon,
            "select concat(tgl_perawatan,'T',jam_rawat,'+07:00') tgl from ("
          + "  select tgl_perawatan, jam_rawat from rawat_jl_dr where no_rawat=? "
          + "  union all select tgl_perawatan, jam_rawat from rawat_jl_pr where no_rawat=? "
          + "  union all select tgl_perawatan, jam_rawat from rawat_inap_dr where no_rawat=? "
          + "  union all select tgl_perawatan, jam_rawat from rawat_inap_pr where no_rawat=? "
          + ") x order by tgl_perawatan desc, jam_rawat desc limit 1", noRawat, noRawat, noRawat, noRawat);
    }

    /** Status Encounter yang tersimpan di server; "" bila gagal dibaca. */
    private static String statusDiServer(ApiSatuSehat api, String link, String idEnc) {
        try {
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            String resp = api.getRest().exchange(link + "/Encounter/" + idEnc, HttpMethod.GET,
                    new HttpEntity(h), String.class).getBody();
            JsonNode st = mapper.readTree(resp).path("status");
            return st == null ? "" : st.asText();
        } catch (Exception e) {
            System.out.println("BUNDEL cek status encounter : " + e);
            return "";
        }
    }

    /** Ambil satu nilai string dari query berparameter; "" bila tidak ada. */
    private static String cariSatu(Connection kon, String sql, String... params) {
        try (PreparedStatement p = kon.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) p.setString(i + 1, params[i]);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) { String v = r.getString(1); return v == null ? "" : v.trim(); }
            }
        } catch (Exception e) { System.out.println("BUNDEL cariSatu err: " + e); }
        return "";
    }

    /** FHIR menolak period dengan end &lt; start. Gagal parse dianggap tidak sah. */
    private static boolean periodeValid(String startUtc, String endUtc) {
        if (startUtc == null || endUtc == null || startUtc.isEmpty() || endUtc.isEmpty()) return false;
        try {
            return !java.time.OffsetDateTime.parse(endUtc).isBefore(java.time.OffsetDateTime.parse(startUtc));
        } catch (Exception e) { return false; }
    }

    /** Konteks kunjungan: [0]idEncounter [1]nik [2]nama [3]status [4]tglIso [5]idP. null bila belum siap. */
    private static String[] resolveCtx(Connection kon, ApiSatuSehat api, SatuSehatCekNIK cek, String noRawat, StringBuilder log) {
        String idEnc="", nik="", nama="", status="", tglIso="";
        try (PreparedStatement ps = kon.prepareStatement(
                "select ifnull(se.id_encounter,'') id_encounter, ifnull(p.no_ktp,'') no_ktp, p.nm_pasien, rp.status_lanjut, "
              + "concat(rp.tgl_registrasi,'T',rp.jam_reg,'+07:00') tgl "
              + "from reg_periksa rp join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
              + "left join satu_sehat_encounter se on se.no_rawat=rp.no_rawat where rp.no_rawat=?")) {
            ps.setString(1, noRawat);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idEnc=rs.getString("id_encounter"); nik=rs.getString("no_ktp");
                nama=rs.getString("nm_pasien"); status=rs.getString("status_lanjut"); tglIso=rs.getString("tgl");
            }
            rs.close();
        } catch (Exception e) { log.append("&nbsp;&nbsp;<font color='red'>✗ Error data kunjungan: "+esc(e.toString())+"</font><br>"); return null; }
        if (idEnc==null || idEnc.isEmpty()) { log.append("&nbsp;&nbsp;<font color='red'>✗ Encounter belum dikirim — kirim Encounter dulu.</font><br>"); return null; }
        String idP = cek.tampilIDPasien(nik);
        if (idP==null || idP.isEmpty()) { log.append("&nbsp;&nbsp;<font color='red'>✗ IHS Patient tidak ditemukan (NIK "+esc(nik)+").</font><br>"); return null; }
        return new String[]{idEnc, nik, nama, status, tglIso, idP};
    }

    /** Kirim Encounter (kalau belum ada). class by kategori: IGD=EMER, Ranap=IMP, Ralan=AMB.
     *  Status awal mengikuti playbook seperti form "Kirim Encounter": ranap POST dengan
     *  in-progress (pasien sudah dalam pelayanan), ralan/IGD dengan arrived.
     *  Bila Encounter sudah ada, tombol Kirim MENUNTASKAN siklus hidupnya ke finished
     *  lewat {@link #finishEncounter} — sebelumnya berhenti selamanya di "arrived". */
    public static int kirimEncounter(String noRawat, StringBuilder log) {
        Connection kon = koneksiDB.condb(); ApiSatuSehat api = new ApiSatuSehat(); SatuSehatCekNIK cek = new SatuSehatCekNIK();
        String link; try { link = koneksiDB.URLFHIRSATUSEHAT(); } catch (Exception e) { link=""; }
        try {
            String idLama = cariSatu(kon, "select ifnull(id_encounter,'') from satu_sehat_encounter where no_rawat=?", noRawat);
            if (!idLama.isEmpty()) {
                log.append("&nbsp;&nbsp;<i>Encounter sudah ada (").append(idLama)
                   .append(") — melanjutkan ke status <b>finished</b>.</i><br>");
                return finishEncounter(noRawat, log);
            }
            String nik="", nama="", status="", kdpoli="", startUtc="", ktpDok="", nmDok="";
            try (PreparedStatement p = kon.prepareStatement(
                    "select ifnull(p.no_ktp,'') nik, p.nm_pasien, rp.status_lanjut, rp.kd_poli, "
                  + "concat(rp.tgl_registrasi,'T',rp.jam_reg,'+07:00') mulai, "
                  + "ifnull(pg.no_ktp,'') ktpdok, ifnull(dr.nm_dokter,'') nmdok "
                  + "from reg_periksa rp join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                  + "left join dokter dr on dr.kd_dokter=rp.kd_dokter "
                  + "left join pegawai pg on pg.nik=rp.kd_dokter where rp.no_rawat=?")) {
                p.setString(1, noRawat); ResultSet r = p.executeQuery();
                if (r.next()) { nik=r.getString("nik"); nama=r.getString("nm_pasien"); status=r.getString("status_lanjut");
                    kdpoli=r.getString("kd_poli"); startUtc=fungsi.IsoTime.toUtc(r.getString("mulai")); ktpDok=r.getString("ktpdok"); nmDok=r.getString("nmdok"); }
                r.close();
            }
            String idP = cek.tampilIDPasien(nik);
            if (idP==null || idP.isEmpty()) { log.append("&nbsp;&nbsp;<font color='red'>✗ IHS Patient tidak ditemukan (NIK "+esc(nik)+").</font><br>"); return 0; }
            String idDok = ktpDok.isEmpty()?"":cek.tampilIDParktisi(ktpDok); if (idDok==null) idDok="";
            boolean isIGD = "IGDK".equalsIgnoreCase(kdpoli), isRanap = "Ranap".equals(status);
            String cc = isIGD?"EMER":(isRanap?"IMP":"AMB"), cd = isIGD?"emergency":(isRanap?"inpatient encounter":"ambulatory");
            String participant = idDok.isEmpty()?"":
                "\"participant\":[{\"type\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/v3-ParticipationType\",\"code\":\"ATND\",\"display\":\"attender\"}]}],"
                + "\"individual\":{\"reference\":\"Practitioner/"+idDok+"\",\"display\":\""+esc(nmDok)+"\"}}],";
            // Ranap: admisi = pasien sudah dalam pelayanan -> in-progress, dan riwayat
            // "arrived" ditutup di titik yang sama. Ralan/IGD berhenti di arrived sampai
            // pelayanan selesai (lalu Kirim lagi untuk finished).
            String statusAwal = isRanap ? "in-progress" : "arrived";
            String statusHistory = isRanap
                ? "{\"status\":\"arrived\",\"period\":{\"start\":\""+startUtc+"\",\"end\":\""+startUtc+"\"}},"
                + "{\"status\":\"in-progress\",\"period\":{\"start\":\""+startUtc+"\"}}"
                : "{\"status\":\"arrived\",\"period\":{\"start\":\""+startUtc+"\"}}";
            String lokasi = lokasiJson(kon, noRawat, isRanap, startUtc, "");
            String json = "{\"resourceType\":\"Encounter\",\"status\":\""+statusAwal+"\","
                + "\"class\":{\"system\":\"http://terminology.hl7.org/CodeSystem/v3-ActCode\",\"code\":\""+cc+"\",\"display\":\""+cd+"\"},"
                + "\"subject\":{\"reference\":\"Patient/"+idP+"\",\"display\":\""+esc(nama)+"\"},"
                + participant
                + "\"period\":{\"start\":\""+startUtc+"\"},"
                + (lokasi.isEmpty()?"":"\"location\":"+lokasi+",")
                + "\"statusHistory\":["+statusHistory+"],"
                + "\"serviceProvider\":{\"reference\":\"Organization/"+koneksiDB.IDSATUSEHAT()+"\"},"
                + "\"identifier\":[{\"system\":\"http://sys-ids.kemkes.go.id/encounter/"+koneksiDB.IDSATUSEHAT()+"\",\"value\":\""+esc(noRawat)+"\"}]}";
            String id = post(api, link, "Encounter", json, log);
            if (!id.isEmpty()) {
                try (PreparedStatement w = kon.prepareStatement("replace into satu_sehat_encounter (no_rawat,id_encounter) values (?,?)")) {
                    w.setString(1, noRawat); w.setString(2, id); w.executeUpdate();
                }
                log.append("&nbsp;&nbsp;<font color='green'>✓ Encounter ("+cc+") terkirim, status <b>"+statusAwal+"</b></font><br>");
                if (lokasi.isEmpty()) log.append("&nbsp;&nbsp;<font color='#c80'>• Location tidak dikirim — poli/kamar belum di-mapping ke Location SatuSehat.</font><br>");
                log.append("&nbsp;&nbsp;<i>Setelah pasien selesai dilayani, tekan Kirim lagi untuk menuntaskan ke finished.</i><br>");
                return 1;
            }
            log.append("&nbsp;&nbsp;<font color='red'>✗ Encounter gagal</font><br>"); return 0;
        } catch (Exception e) { log.append("&nbsp;&nbsp;<font color='red'>✗ "+esc(e.toString())+"</font><br>"); return 0; }
    }

    /**
     * Tuntaskan Encounter ke status <b>finished</b> (PUT). Sebelum ini Bundel hanya
     * bisa membuat Encounter dan meninggalkannya di "arrived" selamanya, sehingga
     * capaian di dashboard SatuSehat tidak pernah terhitung.
     *
     * Syarat yang ditegakkan (sama dengan form "Kirim Encounter"):
     * period.end terisi (pasien sudah selesai dilayani), end &gt;= start, dan
     * Encounter.diagnosis ada — artinya Condition harus sudah dikirim (Rule 10457).
     * Setiap syarat yang gagal dilaporkan dengan langkah perbaikannya, bukan
     * dibiarkan ditolak server.
     */
    public static int finishEncounter(String noRawat, StringBuilder log) {
        return putEncounter(noRawat, log, true);
    }

    /**
     * Update (PUT) Encounter dengan data terbaru dari SIMRS tanpa memaksa finished.
     * Bila syarat finished sudah terpenuhi, statusnya sekalian dituntaskan; kalau
     * belum, Encounter tetap diperbarui pada tahap yang berlaku (ranap
     * in-progress, ralan/IGD arrived) — berguna setelah dokter/poli/kamar
     * diperbaiki, atau setelah mapping Location dilengkapi.
     */
    public static int updateEncounter(String noRawat, StringBuilder log) {
        return putEncounter(noRawat, log, false);
    }

    private static int putEncounter(String noRawat, StringBuilder log, boolean wajibFinish) {
        Connection kon = koneksiDB.condb(); ApiSatuSehat api = new ApiSatuSehat(); SatuSehatCekNIK cek = new SatuSehatCekNIK();
        String link; try { link = koneksiDB.URLFHIRSATUSEHAT(); } catch (Exception e) { link=""; }
        try {
            String idEnc = cariSatu(kon, "select ifnull(id_encounter,'') from satu_sehat_encounter where no_rawat=?", noRawat);
            if (idEnc.isEmpty()) { log.append("&nbsp;&nbsp;<font color='red'>✗ Encounter belum pernah dikirim.</font><br>"); return 0; }

            String nik="", nama="", status="", kdpoli="", mulaiWib="", ktpDok="", nmDok="";
            try (PreparedStatement p = kon.prepareStatement(
                    "select ifnull(p.no_ktp,'') nik, p.nm_pasien, rp.status_lanjut, rp.kd_poli, "
                  + "concat(rp.tgl_registrasi,'T',rp.jam_reg,'+07:00') mulai, "
                  + "ifnull(pg.no_ktp,'') ktpdok, ifnull(dr.nm_dokter,'') nmdok "
                  + "from reg_periksa rp join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                  + "left join dokter dr on dr.kd_dokter=rp.kd_dokter "
                  + "left join pegawai pg on pg.nik=rp.kd_dokter where rp.no_rawat=?")) {
                p.setString(1, noRawat);
                try (ResultSet r = p.executeQuery()) {
                    if (r.next()) { nik=r.getString("nik"); nama=r.getString("nm_pasien"); status=r.getString("status_lanjut");
                        kdpoli=r.getString("kd_poli"); mulaiWib=r.getString("mulai"); ktpDok=r.getString("ktpdok"); nmDok=r.getString("nmdok"); }
                }
            }
            boolean isIGD = "IGDK".equalsIgnoreCase(kdpoli), isRanap = "Ranap".equals(status);
            String startUtc = fungsi.IsoTime.toUtc(mulaiWib);
            String selesaiWib = akhirPelayananWib(kon, noRawat, isRanap);
            String endUtc = selesaiWib.isEmpty() ? "" : fungsi.IsoTime.toUtc(selesaiWib);
            String diagnosis = diagnosisEncounter(kon, noRawat);
            boolean bisaFinish = !endUtc.isEmpty() && periodeValid(startUtc, endUtc) && !diagnosis.isEmpty();

            if (!bisaFinish) {
                String alasan;
                if (endUtc.isEmpty()) {
                    alasan = "pasien belum punya waktu selesai pelayanan "
                           + (isRanap ? "(kamar_inap.tgl_keluar masih kosong)" : "(belum ada pemeriksaan/tindakan)");
                } else if (!periodeValid(startUtc, endUtc)) {
                    alasan = "waktu selesai (" + selesaiWib + ") mendahului registrasi (" + mulaiWib + ")";
                } else {
                    alasan = "Encounter.diagnosis wajib (Rule 10457) — kirim <b>Condition</b> dulu";
                }
                if (wajibFinish) {
                    log.append("&nbsp;&nbsp;<font color='#c80'>• Belum bisa di-finish — ").append(alasan).append(".</font><br>");
                    return 0;
                }
                // Encounter yang sudah finished di server TIDAK boleh diturunkan lagi
                // hanya karena data lokal belum cukup membangun body finished —
                // status di SatuSehat akan mundur dan capaian ikut hilang.
                if ("finished".equals(statusDiServer(api, link, idEnc))) {
                    log.append("&nbsp;&nbsp;<font color='#c80'>• Update dilewati — Encounter di SatuSehat sudah <b>finished</b>, ")
                       .append("sedangkan data lokal belum cukup (").append(alasan)
                       .append("). Memperbaruinya sekarang justru akan menurunkan statusnya.</font><br>");
                    return 0;
                }
                // Mode update: tetap perbarui Encounter pada tahap yang berlaku sekarang.
                log.append("&nbsp;&nbsp;<i>Belum bisa di-finish (").append(alasan)
                   .append("). Encounter diperbarui pada tahap berjalan.</i><br>");
            }
            String idP = cek.tampilIDPasien(nik);
            if (idP == null || idP.isEmpty()) { log.append("&nbsp;&nbsp;<font color='red'>✗ IHS Patient tidak ditemukan (NIK "+esc(nik)+").</font><br>"); return 0; }
            String idDok = ktpDok.isEmpty() ? "" : cek.tampilIDParktisi(ktpDok); if (idDok == null) idDok = "";

            String cc = isIGD?"EMER":(isRanap?"IMP":"AMB"), cd = isIGD?"emergency":(isRanap?"inpatient encounter":"ambulatory");
            String participant = idDok.isEmpty()?"":
                "\"participant\":[{\"type\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/v3-ParticipationType\",\"code\":\"ATND\",\"display\":\"attender\"}]}],"
                + "\"individual\":{\"reference\":\"Practitioner/"+idDok+"\",\"display\":\""+esc(nmDok)+"\"}}],";
            String lokasi = lokasiJson(kon, noRawat, isRanap, startUtc, bisaFinish ? endUtc : "");
            String[] dd = mapDischargeDisposition(cariSatu(kon,
                "select ifnull(stts_pulang,'') from kamar_inap where no_rawat=? and ifnull(stts_pulang,'')<>'Pindah Kamar' "
              + "and tgl_keluar is not null and tgl_keluar<>'0000-00-00' order by tgl_keluar desc, jam_keluar desc limit 1", noRawat));

            String statusEnc = bisaFinish ? "finished" : (isRanap ? "in-progress" : "arrived");
            String periode = bisaFinish
                ? "\"period\":{\"start\":\""+startUtc+"\",\"end\":\""+endUtc+"\"},"
                : "\"period\":{\"start\":\""+startUtc+"\"},";
            String riwayat = bisaFinish
                ? "{\"status\":\"arrived\",\"period\":{\"start\":\""+startUtc+"\",\"end\":\""+startUtc+"\"}},"
                + "{\"status\":\"in-progress\",\"period\":{\"start\":\""+startUtc+"\",\"end\":\""+endUtc+"\"}},"
                + "{\"status\":\"finished\",\"period\":{\"start\":\""+endUtc+"\",\"end\":\""+endUtc+"\"}}"
                : (isRanap
                    ? "{\"status\":\"arrived\",\"period\":{\"start\":\""+startUtc+"\",\"end\":\""+startUtc+"\"}},"
                    + "{\"status\":\"in-progress\",\"period\":{\"start\":\""+startUtc+"\"}}"
                    : "{\"status\":\"arrived\",\"period\":{\"start\":\""+startUtc+"\"}}");

            String json = "{\"resourceType\":\"Encounter\",\"id\":\""+esc(idEnc)+"\","
                + "\"identifier\":[{\"system\":\"http://sys-ids.kemkes.go.id/encounter/"+koneksiDB.IDSATUSEHAT()+"\",\"use\":\"official\",\"value\":\""+esc(noRawat)+"\"}],"
                + "\"status\":\""+statusEnc+"\","
                + "\"class\":{\"system\":\"http://terminology.hl7.org/CodeSystem/v3-ActCode\",\"code\":\""+cc+"\",\"display\":\""+cd+"\"},"
                + "\"subject\":{\"reference\":\"Patient/"+idP+"\",\"display\":\""+esc(nama)+"\"},"
                + participant
                + periode
                + (lokasi.isEmpty()?"":"\"location\":"+lokasi+",")
                + "\"statusHistory\":["+riwayat+"],"
                + (diagnosis.isEmpty()?"":"\"diagnosis\":["+diagnosis+"],")
                + (bisaFinish && isRanap ? "\"hospitalization\":{\"dischargeDisposition\":{\"coding\":[{"
                    + "\"system\":\"http://terminology.hl7.org/CodeSystem/discharge-disposition\","
                    + "\"code\":\""+dd[0]+"\",\"display\":\""+dd[1]+"\"}]}}," : "")
                + "\"serviceProvider\":{\"reference\":\"Organization/"+koneksiDB.IDSATUSEHAT()+"\"}}";

            if (put(api, link, "Encounter", idEnc, json, log)) {
                log.append("&nbsp;&nbsp;<font color='green'>✓ Encounter (").append(cc).append(") diperbarui, status <b>")
                   .append(statusEnc).append("</b>.</font><br>");
                return 1;
            }
            log.append("&nbsp;&nbsp;<font color='red'>✗ Gagal memperbarui Encounter.</font><br>"); return 0;
        } catch (Exception e) { log.append("&nbsp;&nbsp;<font color='red'>✗ "+esc(e.toString())+"</font><br>"); return 0; }
    }

    /** Body Condition — dipakai bersama jalur kirim (POST) dan update (PUT). */
    private static String jsonCondition(String idResource, String icd, String disp, String idP, String nama, String idEnc) {
        return "{\"resourceType\":\"Condition\","
            + (idResource.isEmpty() ? "" : "\"id\":\""+esc(idResource)+"\",")
            + "\"clinicalStatus\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-clinical\",\"code\":\"active\",\"display\":\"Active\"}]},"
            + "\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-category\",\"code\":\"encounter-diagnosis\",\"display\":\"Encounter Diagnosis\"}]}],"
            + "\"code\":{\"coding\":[{\"system\":\"http://hl7.org/fhir/sid/icd-10\",\"code\":\""+esc(icd)+"\",\"display\":\""+esc(disp)+"\"}]},"
            + "\"subject\":{\"reference\":\"Patient/"+idP+"\",\"display\":\""+esc(nama)+"\"},"
            + "\"encounter\":{\"reference\":\"Encounter/"+idEnc+"\"}}";
    }

    /** Body Procedure — dipakai bersama jalur kirim (POST) dan update (PUT). */
    private static String jsonProcedure(String idResource, String icd9, String disp, String idP, String nama,
                                        String idEnc, String tglIso) {
        String[] cat = snomedCategoryForIcd9(icd9);
        return "{\"resourceType\":\"Procedure\","
            + (idResource.isEmpty() ? "" : "\"id\":\""+esc(idResource)+"\",")
            + "\"status\":\"completed\","
            + "\"category\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\""+cat[0]+"\",\"display\":\""+cat[1]+"\"}]},"
            + "\"code\":{\"coding\":[{\"system\":\"http://hl7.org/fhir/sid/icd-9-cm\",\"code\":\""+esc(icd9)+"\",\"display\":\""+esc(disp)+"\"}]},"
            + "\"subject\":{\"reference\":\"Patient/"+idP+"\",\"display\":\""+esc(nama)+"\"},"
            + "\"encounter\":{\"reference\":\"Encounter/"+idEnc+"\"},"
            + "\"performedDateTime\":\""+fungsi.IsoTime.toUtc(tglIso)+"\","
            + "\"performer\":[{\"actor\":{\"reference\":\"Organization/"+koneksiDB.IDSATUSEHAT()+"\"}}]}";
    }

    /**
     * Update (PUT) semua Condition yang sudah punya id, memakai kode/nama penyakit
     * terbaru. Diagnosa yang belum pernah dikirim TIDAK dibuat di sini — pakai
     * tombol Kirim, supaya Update tidak diam-diam menambah resource baru.
     */
    public static int updateCondition(String noRawat, StringBuilder log) {
        Connection kon = koneksiDB.condb(); ApiSatuSehat api = new ApiSatuSehat(); SatuSehatCekNIK cek = new SatuSehatCekNIK();
        String link; try { link = koneksiDB.URLFHIRSATUSEHAT(); } catch (Exception e) { link=""; }
        String[] ctx = resolveCtx(kon, api, cek, noRawat, log); if (ctx==null) return 0;
        String idEnc=ctx[0], nama=ctx[2], idP=ctx[5];
        int n=0, belum=0;
        try (PreparedStatement ps = kon.prepareStatement(
                "select sc.kd_penyakit, sc.id_condition, ifnull(pny.nm_penyakit,'') nm from satu_sehat_condition sc "
              + "left join penyakit pny on pny.kd_penyakit=sc.kd_penyakit "
              + "where sc.no_rawat=? and ifnull(sc.id_condition,'')<>''")) {
            ps.setString(1, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String icd=rs.getString("kd_penyakit"), disp=rs.getString("nm"), id=rs.getString("id_condition");
                    if (put(api, link, "Condition", id, jsonCondition(id, icd, disp, idP, nama, idEnc), log)) {
                        n++; log.append("&nbsp;&nbsp;<font color='green'>✓ Condition "+esc(icd)+" — "+esc(disp)+" diperbarui</font><br>");
                    } else log.append("&nbsp;&nbsp;<font color='red'>✗ Condition "+esc(icd)+" gagal diperbarui</font><br>");
                }
            }
        } catch (Exception e) { log.append("&nbsp;&nbsp;<font color='red'>✗ "+esc(e.toString())+"</font><br>"); }
        belum = jumlahBelumTerkirim(kon, "diagnosa_pasien dp left join satu_sehat_condition sc "
                + "on sc.no_rawat=dp.no_rawat and sc.kd_penyakit=dp.kd_penyakit", "dp.no_rawat", "sc.id_condition", noRawat);
        if (belum > 0) log.append("&nbsp;&nbsp;<font color='#c80'>• ").append(belum)
                .append(" diagnosa belum pernah dikirim — pakai tombol <b>Kirim</b>.</font><br>");
        if (n==0 && belum==0) log.append("&nbsp;&nbsp;<i>Tidak ada Condition untuk diperbarui.</i><br>");
        return n;
    }

    /** Update (PUT) semua Procedure yang sudah punya id, memakai kode/uraian ICD-9 terbaru. */
    public static int updateProcedure(String noRawat, StringBuilder log) {
        Connection kon = koneksiDB.condb(); ApiSatuSehat api = new ApiSatuSehat(); SatuSehatCekNIK cek = new SatuSehatCekNIK();
        String link; try { link = koneksiDB.URLFHIRSATUSEHAT(); } catch (Exception e) { link=""; }
        String[] ctx = resolveCtx(kon, api, cek, noRawat, log); if (ctx==null) return 0;
        String idEnc=ctx[0], nama=ctx[2], tglIso=ctx[4], idP=ctx[5];
        int n=0, belum=0;
        try (PreparedStatement ps = kon.prepareStatement(
                "select sp.kode, sp.id_procedure, ifnull(i9.deskripsi_panjang,'') nm from satu_sehat_procedure sp "
              + "left join icd9 i9 on i9.kode=sp.kode "
              + "where sp.no_rawat=? and ifnull(sp.id_procedure,'')<>''")) {
            ps.setString(1, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String icd9=rs.getString("kode"), disp=rs.getString("nm"), id=rs.getString("id_procedure");
                    if (put(api, link, "Procedure", id, jsonProcedure(id, icd9, disp, idP, nama, idEnc, tglIso), log)) {
                        n++; log.append("&nbsp;&nbsp;<font color='green'>✓ Procedure "+esc(icd9)+" — "+esc(disp)+" diperbarui</font><br>");
                    } else log.append("&nbsp;&nbsp;<font color='red'>✗ Procedure "+esc(icd9)+" gagal diperbarui</font><br>");
                }
            }
        } catch (Exception e) { log.append("&nbsp;&nbsp;<font color='red'>✗ "+esc(e.toString())+"</font><br>"); }
        belum = jumlahBelumTerkirim(kon, "prosedur_pasien pp left join satu_sehat_procedure sp "
                + "on sp.no_rawat=pp.no_rawat and sp.kode=pp.kode", "pp.no_rawat", "sp.id_procedure", noRawat);
        if (belum > 0) log.append("&nbsp;&nbsp;<font color='#c80'>• ").append(belum)
                .append(" prosedur belum pernah dikirim — pakai tombol <b>Kirim</b>.</font><br>");
        if (n==0 && belum==0) log.append("&nbsp;&nbsp;<i>Tidak ada Procedure untuk diperbarui.</i><br>");
        return n;
    }

    /** Hitung baris sumber yang belum punya id di tabel tracking. */
    private static int jumlahBelumTerkirim(Connection kon, String fromJoin, String kolomNoRawat, String kolomId, String noRawat) {
        try (PreparedStatement p = kon.prepareStatement(
                "select count(*) from " + fromJoin + " where " + kolomNoRawat + "=? and ifnull(" + kolomId + ",'')=''")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) { if (r.next()) return r.getInt(1); }
        } catch (Exception e) { System.out.println("BUNDEL jumlahBelumTerkirim: " + e); }
        return 0;
    }

    /** Kirim semua Condition (diagnosa_pasien) yang belum terkirim. Return jumlah terkirim. */
    public static int kirimCondition(String noRawat, StringBuilder log) {
        Connection kon = koneksiDB.condb(); ApiSatuSehat api = new ApiSatuSehat(); SatuSehatCekNIK cek = new SatuSehatCekNIK();
        String link; try { link = koneksiDB.URLFHIRSATUSEHAT(); } catch (Exception e) { link=""; }
        String[] ctx = resolveCtx(kon, api, cek, noRawat, log); if (ctx==null) return 0;
        String idEnc=ctx[0], nama=ctx[2], status=ctx[3], idP=ctx[5];
        int sent=0;
        try (PreparedStatement ps = kon.prepareStatement(
                "select dp.kd_penyakit, ifnull(pny.nm_penyakit,'') nm from diagnosa_pasien dp "
              + "left join penyakit pny on pny.kd_penyakit=dp.kd_penyakit where dp.no_rawat=? "
              + "and not exists (select 1 from satu_sehat_condition sc where sc.no_rawat=dp.no_rawat and sc.kd_penyakit=dp.kd_penyakit and sc.id_condition is not null and sc.id_condition<>'') "
              + "order by dp.prioritas")) {
            ps.setString(1, noRawat);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String icd=rs.getString("kd_penyakit"), disp=rs.getString("nm");
                String json = jsonCondition("", icd, disp, idP, nama, idEnc);
                String id = post(api, link, "Condition", json, log);
                if (!id.isEmpty()) {
                    try (PreparedStatement w = kon.prepareStatement("replace into satu_sehat_condition (no_rawat,kd_penyakit,status,id_condition) values (?,?,?,?)")) {
                        w.setString(1,noRawat); w.setString(2,icd); w.setString(3,status); w.setString(4,id); w.executeUpdate();
                    }
                    sent++; log.append("&nbsp;&nbsp;<font color='green'>✓ Condition "+esc(icd)+" — "+esc(disp)+"</font><br>");
                } else log.append("&nbsp;&nbsp;<font color='red'>✗ Condition "+esc(icd)+" gagal</font><br>");
            }
            rs.close();
        } catch (Exception e) { log.append("&nbsp;&nbsp;<font color='red'>✗ "+esc(e.toString())+"</font><br>"); }
        if (sent==0) log.append("&nbsp;&nbsp;<i>Tidak ada Condition baru.</i><br>");
        return sent;
    }

    /** Kirim semua Procedure (prosedur_pasien) yang belum terkirim. Return jumlah terkirim. */
    public static int kirimProcedure(String noRawat, StringBuilder log) {
        Connection kon = koneksiDB.condb(); ApiSatuSehat api = new ApiSatuSehat(); SatuSehatCekNIK cek = new SatuSehatCekNIK();
        String link; try { link = koneksiDB.URLFHIRSATUSEHAT(); } catch (Exception e) { link=""; }
        String[] ctx = resolveCtx(kon, api, cek, noRawat, log); if (ctx==null) return 0;
        String idEnc=ctx[0], nama=ctx[2], status=ctx[3], tglIso=ctx[4], idP=ctx[5];
        int sent=0;
        try (PreparedStatement ps = kon.prepareStatement(
                "select pp.kode, ifnull(i9.deskripsi_panjang,'') nm from prosedur_pasien pp "
              + "left join icd9 i9 on i9.kode=pp.kode where pp.no_rawat=? "
              + "and not exists (select 1 from satu_sehat_procedure sp where sp.no_rawat=pp.no_rawat and sp.kode=pp.kode and sp.id_procedure is not null and sp.id_procedure<>'') "
              + "order by pp.prioritas")) {
            ps.setString(1, noRawat);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String icd9=rs.getString("kode"), disp=rs.getString("nm");
                String json = jsonProcedure("", icd9, disp, idP, nama, idEnc, tglIso);
                String id = post(api, link, "Procedure", json, log);
                if (!id.isEmpty()) {
                    try (PreparedStatement w = kon.prepareStatement("replace into satu_sehat_procedure (no_rawat,kode,status,id_procedure) values (?,?,?,?)")) {
                        w.setString(1,noRawat); w.setString(2,icd9); w.setString(3,status); w.setString(4,id); w.executeUpdate();
                    }
                    sent++; log.append("&nbsp;&nbsp;<font color='green'>✓ Procedure "+esc(icd9)+" — "+esc(disp)+"</font><br>");
                } else log.append("&nbsp;&nbsp;<font color='red'>✗ Procedure "+esc(icd9)+" gagal</font><br>");
            }
            rs.close();
        } catch (Exception e) { log.append("&nbsp;&nbsp;<font color='red'>✗ "+esc(e.toString())+"</font><br>"); }
        if (sent==0) log.append("&nbsp;&nbsp;<i>Tidak ada Procedure baru.</i><br>");
        return sent;
    }

    private static String occUtc(String tgl, String jam) {
        try { return fungsi.IsoTime.toUtc(tgl + "T" + (jam==null||jam.isEmpty()?"00:00:00":jam) + "+07:00"); }
        catch (Exception e) { return ""; }
    }

    /** Satu baris tagihan untuk Bundle billing. */
    private static final class Line {
        String system, code, display, performerIHS="", occ, srcTable, keyCode, tgl="", jam="", prakKey="";
        int qty=1; long unit=0, total=0;
    }

    private static void addLines(Connection kon, java.util.List<Line> out, String sql, String noRawat,
            boolean kptl, boolean withPerformer, SatuSehatCekNIK cek, String srcTable) {
        try (PreparedStatement ps = kon.prepareStatement(sql)) {
            ps.setString(1, noRawat);
            ResultSet r = ps.executeQuery();
            while (r.next()) {
                Line ln = new Line();
                ln.srcTable = srcTable;
                if (kptl) {
                    ln.system = "http://terminology.kemkes.go.id/CodeSystem/kptl";
                    ln.code = r.getString("code"); ln.display = r.getString("disp");
                    ln.qty = 1; ln.unit = (long) r.getDouble("biaya"); ln.total = ln.unit;
                    ln.tgl = r.getString("tgl"); ln.jam = r.getString("jam");
                    ln.keyCode = r.getString("kj"); ln.prakKey = r.getString("prak")==null?"":r.getString("prak");
                    if (withPerformer) {
                        String ktp = r.getString("ktpdok");
                        if (ktp != null && !ktp.isEmpty()) { String id = cek.tampilIDParktisi(ktp); if (id!=null) ln.performerIHS = id; }
                    }
                } else { // obat/alkes (KFA)
                    ln.system = r.getString("sys"); ln.code = r.getString("code"); ln.display = r.getString("disp");
                    ln.qty = r.getInt("jml"); if (ln.qty<=0) ln.qty=1;
                    ln.total = (long) r.getDouble("total"); ln.unit = ln.total/ln.qty;
                    ln.tgl = r.getString("tgl"); ln.jam = r.getString("jam"); ln.keyCode = r.getString("kode_brng");
                }
                ln.occ = occUtc(ln.tgl, ln.jam);
                if (ln.code != null && !ln.code.isEmpty()) out.add(ln);
            }
            r.close();
        } catch (Exception e) { System.out.println("addLines ("+srcTable+") err: "+e); }
    }

    /** Kirim BILLING sbg transaction Bundle (ChargeItem+Account+Invoice) lalu PATCH Encounter (tautkan account).
     *  Sumber: tindakan(dr/pr ralan&ranap), lab, radiologi -> KPTL; obat/alkes (resep ralan + detail_pemberian_obat ranap) -> KFA.
     *  Coverage disertakan hanya bila ada satu_sehat_coverage utk pasien. Return jumlah ChargeItem terkirim. */
    public static int kirimBillingBundle(String noRawat, StringBuilder log) {
        Connection kon = koneksiDB.condb(); ApiSatuSehat api = new ApiSatuSehat(); SatuSehatCekNIK cek = new SatuSehatCekNIK();
        String link; try { link = koneksiDB.URLFHIRSATUSEHAT(); } catch (Exception e) { link=""; }
        String[] ctx = resolveCtx(kon, api, cek, noRawat, log); if (ctx==null) return 0;
        String idEnc=ctx[0], nama=ctx[2], tglIso=ctx[4], idP=ctx[5];
        String org; try { org = koneksiDB.IDSATUSEHAT(); } catch (Exception e) { org=""; }

        // no_rkm_medis + coverage (opsional)
        String noRm="", idCoverage="";
        try (PreparedStatement p = kon.prepareStatement("select no_rkm_medis from reg_periksa where no_rawat=?")) {
            p.setString(1, noRawat); ResultSet r=p.executeQuery(); if (r.next()) noRm=r.getString(1); r.close();
        } catch (Exception e) {}
        if (!noRm.isEmpty()) {
            try (PreparedStatement p = kon.prepareStatement("select ifnull(id_coverage,'') id from satu_sehat_coverage where no_rkm_medis=?")) {
                p.setString(1, noRm); ResultSet r=p.executeQuery(); if (r.next()) idCoverage=r.getString("id"); r.close();
            } catch (Exception e) {}
        }

        // Kumpulkan semua baris tagihan
        java.util.List<Line> lines = new java.util.ArrayList<>();
        String KPTLJOIN = "inner join satu_sehat_mapping_kptl mk on mk.kd_jenis_prw=t.kd_jenis_prw ";
        // tindakan dokter (ralan & ranap)
        for (String src : new String[]{"rawat_jl_dr","rawat_inap_dr"}) {
            addLines(kon, lines,
                "select mk.kode_kptl code, mk.display disp, t.biaya_rawat biaya, t.tgl_perawatan tgl, t.jam_rawat jam, "
              + "ifnull(pg.no_ktp,'') ktpdok, t.kd_jenis_prw kj, t.kd_dokter prak from "+src+" t "
              + KPTLJOIN + "left join pegawai pg on pg.nik=t.kd_dokter where t.no_rawat=?",
                noRawat, true, true, cek, src);
        }
        // tindakan petugas (ralan & ranap) - tanpa performer
        for (String src : new String[]{"rawat_jl_pr","rawat_inap_pr"}) {
            addLines(kon, lines,
                "select mk.kode_kptl code, mk.display disp, t.biaya_rawat biaya, t.tgl_perawatan tgl, t.jam_rawat jam, "
              + "'' ktpdok, t.kd_jenis_prw kj, t.nip prak from "+src+" t "+KPTLJOIN+"where t.no_rawat=?",
                noRawat, true, false, cek, src);
        }
        // lab
        addLines(kon, lines,
            "select mk.kode_kptl code, mk.display disp, t.biaya biaya, t.tgl_periksa tgl, t.jam jam, "
          + "ifnull(pg.no_ktp,'') ktpdok, t.kd_jenis_prw kj, t.kd_dokter prak from periksa_lab t "
          + "inner join satu_sehat_mapping_kptl mk on mk.kd_jenis_prw=t.kd_jenis_prw "
          + "left join pegawai pg on pg.nik=t.kd_dokter where t.no_rawat=?",
            noRawat, true, true, cek, "periksa_lab");
        // radiologi
        addLines(kon, lines,
            "select mk.kode_kptl code, mk.display disp, t.biaya biaya, t.tgl_periksa tgl, t.jam jam, "
          + "ifnull(pg.no_ktp,'') ktpdok, t.kd_jenis_prw kj, t.kd_dokter prak from periksa_radiologi t "
          + "inner join satu_sehat_mapping_kptl mk on mk.kd_jenis_prw=t.kd_jenis_prw "
          + "left join pegawai pg on pg.nik=t.kd_dokter where t.no_rawat=?",
            noRawat, true, true, cek, "periksa_radiologi");
        // obat/alkes ralan (resep)
        addLines(kon, lines,
            "select mo.obat_code code, mo.obat_system sys, mo.obat_display disp, (db.ralan*rd.jml) total, rd.jml jml, "
          + "r.tgl_perawatan tgl, r.jam jam, rd.kode_brng kode_brng from resep_obat r "
          + "inner join resep_dokter rd on rd.no_resep=r.no_resep "
          + "inner join databarang db on db.kode_brng=rd.kode_brng "
          + "inner join satu_sehat_mapping_obat mo on mo.kode_brng=rd.kode_brng where r.no_rawat=?",
            noRawat, false, false, cek, "resep_obat");
        // obat/alkes ranap (detail_pemberian_obat)
        addLines(kon, lines,
            "select mo.obat_code code, mo.obat_system sys, mo.obat_display disp, d.total total, d.jml jml, "
          + "d.tgl_perawatan tgl, d.jam jam, d.kode_brng kode_brng from detail_pemberian_obat d "
          + "inner join satu_sehat_mapping_obat mo on mo.kode_brng=d.kode_brng where d.no_rawat=?",
            noRawat, false, false, cek, "detail_pemberian_obat");

        if (lines.isEmpty()) { log.append("&nbsp;&nbsp;<i>Tidak ada item tagihan ter-mapping (KPTL/KFA) utk no.rawat ini.</i><br>"); return 0; }

        // Rakit transaction Bundle: [0]Account, [1..n]ChargeItem, [n+1]Invoice
        long totalNet = 0; for (Line l : lines) totalNet += l.total;
        StringBuilder e = new StringBuilder();
        // Account (urn:uuid:acct)
        String cov = idCoverage.isEmpty() ? "" :
            "\"coverage\":[{\"coverage\":{\"reference\":\"Coverage/"+idCoverage+"\"},\"priority\":1}],";
        e.append("{\"fullUrl\":\"urn:uuid:acct\",\"resource\":{\"resourceType\":\"Account\",\"status\":\"active\","
          + "\"type\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/v3-ActCode\",\"code\":\"PBILLACCT\",\"display\":\"patient billing account\"}],\"text\":\"patient\"},"
          + "\"name\":\"Akun Billing Pasien\",\"subject\":[{\"reference\":\"Patient/"+idP+"\",\"display\":\""+esc(nama)+"\"}],"
          + "\"servicePeriod\":{\"start\":\""+fungsi.IsoTime.toUtc(tglIso)+"\"},"+cov
          + "\"owner\":{\"reference\":\"Organization/"+org+"\"},\"description\":\"Akun Billing Pasien\"},"
          + "\"request\":{\"method\":\"POST\",\"url\":\"Account\"}}");
        // ChargeItems
        StringBuilder invLines = new StringBuilder();
        for (int i=0;i<lines.size();i++) {
            Line l = lines.get(i);
            String perf = l.performerIHS.isEmpty() ? "" :
                "\"performer\":[{\"actor\":{\"reference\":\"Practitioner/"+l.performerIHS+"\"}}],";
            e.append(",{\"fullUrl\":\"urn:uuid:ci").append(i).append("\",\"resource\":{\"resourceType\":\"ChargeItem\",\"status\":\"billable\","
              + "\"code\":{\"coding\":[{\"system\":\""+l.system+"\",\"code\":\""+esc(l.code)+"\",\"display\":\""+esc(l.display)+"\"}]},"
              + "\"subject\":{\"reference\":\"Patient/"+idP+"\"},\"context\":{\"reference\":\"Encounter/"+idEnc+"\"},"
              + (l.occ.isEmpty()?"":"\"occurrencePeriod\":{\"start\":\""+l.occ+"\",\"end\":\""+l.occ+"\"},")
              + perf + "\"quantity\":{\"value\":"+l.qty+"},\"account\":[{\"reference\":\"urn:uuid:acct\"}],"
              + "\"extension\":[{\"url\":\"https://fhir.kemkes.go.id/r4/StructureDefinition/unitPrice\",\"valueMoney\":{\"value\":"+l.unit+",\"currency\":\"IDR\"}},"
              + "{\"url\":\"https://fhir.kemkes.go.id/r4/StructureDefinition/totalPrice\",\"valueMoney\":{\"value\":"+l.total+",\"currency\":\"IDR\"}}]},"
              + "\"request\":{\"method\":\"POST\",\"url\":\"ChargeItem\"}}");
            invLines.append(i==0?"":",").append("{\"sequence\":").append(i+1).append(",\"chargeItemReference\":{\"reference\":\"urn:uuid:ci").append(i).append("\"},"
              + "\"priceComponent\":[{\"type\":\"surcharge\",\"code\":{\"coding\":[{\"system\":\""+l.system+"\",\"code\":\""+esc(l.code)+"\",\"display\":\""+esc(l.display)+"\"}]},"
              + "\"amount\":{\"value\":"+l.total+",\"currency\":\"IDR\"}}]}");
        }
        // Invoice
        e.append(",{\"fullUrl\":\"urn:uuid:inv\",\"resource\":{\"resourceType\":\"Invoice\",\"status\":\"issued\","
          + "\"subject\":{\"reference\":\"Patient/"+idP+"\"},\"issuer\":{\"reference\":\"Organization/"+org+"\"},"
          + "\"account\":{\"reference\":\"urn:uuid:acct\"},\"lineItem\":["+invLines+"],"
          + "\"totalNet\":{\"value\":"+totalNet+",\"currency\":\"IDR\"}},\"request\":{\"method\":\"POST\",\"url\":\"Invoice\"}}");

        String bundle = "{\"resourceType\":\"Bundle\",\"type\":\"transaction\",\"entry\":["+e+"]}";

        // POST transaction bundle ke base /fhir-r4/v1
        String resp;
        try {
            HttpHeaders h = new HttpHeaders(); h.setContentType(MediaType.APPLICATION_JSON);
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            System.out.println("BILLING BUNDLE REQ : " + bundle);
            resp = api.getRest().exchange(link, HttpMethod.POST, new HttpEntity(bundle, h), String.class).getBody();
            System.out.println("BILLING BUNDLE RESP : " + resp);
        } catch (org.springframework.web.client.HttpClientErrorException ex) {
            log.append("&nbsp;&nbsp;<font color='red'>✗ Bundle gagal: "+esc(ex.getResponseBodyAsString())+"</font><br>"); return 0;
        } catch (Exception ex) { log.append("&nbsp;&nbsp;<font color='red'>✗ Bundle error: "+esc(ex.toString())+"</font><br>"); return 0; }

        // Parse transaction-response (urutan = urutan request: Account, ChargeItem.., Invoice)
        int sent=0; String idAccount="", idInvoice="";
        try {
            JsonNode entries = mapper.readTree(resp).path("entry");
            if (entries.isArray() && entries.size()>=1) {
                idAccount = entries.get(0).path("response").path("resourceID").asText();
                for (int i=0;i<lines.size() && (i+1)<entries.size();i++) {
                    String cid = entries.get(i+1).path("response").path("resourceID").asText();
                    Line l = lines.get(i);
                    if (!cid.isEmpty()) {
                        sent++;
                        try (PreparedStatement w = kon.prepareStatement(
                            "replace into satu_sehat_chargeitem (source,no_rawat,kd_jenis_prw,practitioner_id,tgl_perawatan,jam_rawat,id_chargeitem,biaya,tgl_kirim) values (?,?,?,?,?,?,?,?,now())")) {
                            w.setString(1,l.srcTable); w.setString(2,noRawat); w.setString(3,l.keyCode); w.setString(4,l.prakKey);
                            w.setString(5,l.tgl); w.setString(6,l.jam); w.setString(7,cid); w.setString(8,String.valueOf(l.total)); w.executeUpdate();
                        }
                    }
                }
                int invIdx = lines.size()+1;
                if (invIdx<entries.size()) idInvoice = entries.get(invIdx).path("response").path("resourceID").asText();
            }
        } catch (Exception ex) { log.append("&nbsp;&nbsp;<font color='red'>✗ Parse respon: "+esc(ex.toString())+"</font><br>"); }

        if (!idAccount.isEmpty()) {
            try (PreparedStatement w = kon.prepareStatement("replace into satu_sehat_account (no_rawat,id_account,tgl_kirim) values (?,?,now())")) {
                w.setString(1,noRawat); w.setString(2,idAccount); w.executeUpdate();
            } catch (Exception ex) {}
        }
        if (!idInvoice.isEmpty()) {
            try (PreparedStatement w = kon.prepareStatement("replace into satu_sehat_invoice (no_rawat,id_invoice,total,jml_item,tgl_kirim) values (?,?,?,?,now())")) {
                w.setString(1,noRawat); w.setString(2,idInvoice); w.setString(3,String.valueOf(totalNet)); w.setString(4,String.valueOf(lines.size())); w.executeUpdate();
            } catch (Exception ex) {}
        }

        // PATCH Encounter -> tautkan account (JSON-Patch add /account)
        if (!idAccount.isEmpty() && !idEnc.isEmpty()) {
            try {
                HttpHeaders h = new HttpHeaders(); h.setContentType(MediaType.valueOf("application/json-patch+json"));
                h.add("Authorization", "Bearer " + api.TokenSatuSehat());
                String patch = "[{\"op\":\"add\",\"path\":\"/account\",\"value\":[{\"reference\":\"Account/"+idAccount+"\"}]}]";
                api.getRest().exchange(link+"/Encounter/"+idEnc, HttpMethod.valueOf("PATCH"), new HttpEntity(patch, h), String.class);
                log.append("&nbsp;&nbsp;<font color='green'>✓ Encounter ditautkan ke Account</font><br>");
            } catch (Exception ex) { log.append("&nbsp;&nbsp;<font color='orange'>⚠ Account/ChargeItem terkirim tapi PATCH Encounter gagal: "+esc(ex.toString())+"</font><br>"); }
        }

        log.append("&nbsp;&nbsp;<font color='green'>✓ Billing Bundle: "+sent+" ChargeItem, Account="+(idAccount.isEmpty()?"-":idAccount.substring(0,Math.min(8,idAccount.length())))+", Invoice="+(idInvoice.isEmpty()?"-":"ok")+", Total Rp"+totalNet+"</font><br>");
        return sent;
    }

    /** Kirim seluruh resource turunan (v1: Condition + Procedure). Return total terkirim. */
    public static int kirimBundel(String noRawat, StringBuilder log) {
        int n = 0;
        log.append("<u>Encounter</u><br>");  n += kirimEncounter(noRawat, log);
        log.append("<u>Condition</u><br>");  n += kirimCondition(noRawat, log);
        log.append("<u>Procedure</u><br>");  n += kirimProcedure(noRawat, log);
        return n;
    }
}
