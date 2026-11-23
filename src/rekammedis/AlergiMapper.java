/*
 * AlergiMapper.java
 *
 * Mesin pemetaan teks alergi bebas -> kode SNOMED. Dipakai bersama oleh
 * DlgPemetaanAlergi (tombol Pindai & Terapkan) supaya aturannya cuma satu
 * tempat, dan bisa dijalankan tanpa GUI bila perlu.
 */
package rekammedis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class AlergiMapper {

    /** Kode SNOMED "Tidak ada alergi yang diketahui". */
    public static final String KODE_TIDAK_ADA = "716186003";

    /** Teks yang jelas bukan alergi (isian nyasar) -> Abaikan. */
    private static final Pattern NOISE = Pattern.compile(
            "^(|-+|\\(-\\)|\\(\\+\\)|[0-9.,]+|na|n/a|nan|none|ya|ada|obat|makanan|alergi|alergi obat"
            + "|ku.*|kes.*|cm|composmentis|compos.*|akral.*|hipertermi|hipotermi|nyeri|lambung|demam"
            + "|baik|sedang|sakit|umum|lain-lain|lainnya|dbn|dalam batas normal)$");

    /** Teks yang berarti "tidak punya alergi". */
    private static final Pattern TIDAK_ADA = Pattern.compile(
            "^(tidak|tdk|tida|tidk|tidka|tidaka|tidaak|tidalk|tiidak|tiak|tdak|tidqak|tidsk|tidakn|tidak|tak|tida?k?)"
            + "\\s*(ada|da|ad|adda|adaa|alergi|alegi|alerg[iy])?\\s*(alergi|alegi|obat|makanan)?\\s*$");

    /** Kamus sinonim lokal: {regex, kode, kategori}. */
    private static final String[][] SINONIM = {
        {"(udang|seafood|sea food|seefood|seeafood|seaffod|seafod.*|tseafood|makanan laut|boga bahari"
            + "|kepiting|kerang|cumi|ikan.*|tongkol|teri|amis.*|makanan yang amis.*)", "91937001", "Makanan"},
        {"(telur|telor|kuning telur|putih telur)", "213020009", "Makanan"},
        {"(susu|susu sapi|susu kotak|susu protein|protein susu)", "15911003", "Makanan"},
        {"(susu kambing|kambing)", "AL000033", "Makanan"},
        {"(kacang.*)", "91934008", "Makanan"},
        {"(ayam|daging ayam|ayam negri|ayam negeri)", "AL000019", "Makanan"},
        {"(daging sapi|sapi|daging)", "AL000018", "Makanan"},
        {"(gandum|mie|mi instan|terigu|tepung.*)", "AL000030", "Makanan"},
        {"(kedelai|tahu|tempe)", "25868003", "Makanan"},
        {"(debu.*|tungau.*)", "AL000043", "Lingkungan"},
        {"(bulu ayam)", "AL000028", "Lingkungan"},
        {"(plester.*)", "V07AA", "Lingkungan"},
        {"(paracetamol|parasetamol|pct|asetaminofen|acetaminophen|sanmol)", "293584003", "Medication"},
        {"(ceftriaxone|ceftriaxon|ceptriaxone|ceptriaxon|ceftri|cefriaxone|ceftriakson)", "294551009", "Medication"},
        {"(amox.*|amok.*|yusimox|amoxsan)", "294505008", "Medication"},
        {"(ibuprofen|proris)", "293619005", "Medication"},
        {"(ketorolac|keterolac|ketorolak|ketorolac tromet.*)", "293622007", "Medication"},
        {"(ranitidin|ranitidine)", "293653009", "Medication"},
        {"(cipro|ciprofloxacin|ciprofloxaxin)", "294487000", "Medication"},
        {"(penisilin|penicillin|penicilin)", "91936005", "Medication"}
    };

    private AlergiMapper() {
    }

    /** Bersihkan satu nilai kolom lalu pecah jadi beberapa token alergi. */
    public static List<String> pecah(String nilai) {
        List<String> hasil = new ArrayList<>();
        if (nilai == null) {
            return hasil;
        }
        String v = nilai.toLowerCase().replace('\n', ',').replace('\r', ',');
        for (String t : v.split("[,;/]|\\+| dan | & | serta ")) {
            String s = t.trim()
                    .replaceAll("^(riwayat\\s+)?alergi(\\s+obat|\\s+makanan)?\\s+", "")
                    .replaceAll("^(obat|makanan|makan)\\s+", "")
                    .replaceAll("[\\s.\\-:]+$", "")
                    .replaceAll("^[\\s.\\-:]+", "")
                    .replaceAll("\\s{2,}", " ")
                    .trim();
            if (s.isEmpty() || s.length() > 150) {
                continue;
            }
            hasil.add(s);
        }
        return hasil;
    }

    /** Tebak pemetaan satu token. Hasil: {kode, kategori, tindakan}. */
    public static String[] tebak(Connection koneksi, String teks) {
        if (TIDAK_ADA.matcher(teks).matches()) {
            return new String[]{KODE_TIDAK_ADA, "Biologis", "Abaikan"};
        }
        if (NOISE.matcher(teks).matches() || !teks.matches(".*[a-z].*")) {
            return new String[]{null, null, "Abaikan"}; // simbol/angka saja
        }
        for (String[] s : SINONIM) {
            if (Pattern.matches(s[0], teks)) {
                return new String[]{s[1], s[2], "Petakan"};
            }
        }
        String kode = cariMaster(koneksi, "lower(display)=?", teks);
        if (kode == null) {
            kode = cariMaster(koneksi, "lower(display)=?", "alergi " + teks);
        }
        if (kode == null && teks.length() >= 5) {
            // Pencocokan longgar dibatasi ke entri yang memang konsep alergi
            // (display mengandung "alergi"/"sensitivitas") atau master alergen
            // lokal (kode AL*). Tanpa batasan ini "dingin" ikut tercocok ke
            // ATC R05X "Persiapan dingin lainnya".
            kode = cariMaster(koneksi,
                    "lower(display) like ? and (lower(display) like '%alergi%' or lower(display) like '%sensitiv%' or kode like 'AL%')",
                    "%" + teks + "%");
        }
        if (kode != null) {
            return new String[]{kode, "Medication", "Petakan"};
        }
        return new String[]{null, null, "Petakan"}; // perlu koreksi manual
    }

    private static String cariMaster(Connection koneksi, String kondisi, String nilai) {
        try (PreparedStatement p = koneksi.prepareStatement(
                "select kode from satu_sehat_ref_allergy where " + kondisi
                // Buang kode gaya ATC (mis. S01AA "Antibiotik", V07AA "Plester"):
                // itu kelas obat, bukan konsep alergi, dan sering salah cocok.
                + " and kode not regexp '^[A-Z][0-9][0-9]' order by length(display) limit 1")) {
            p.setString(1, nilai);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? r.getString(1) : null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /** Daftar sumber aktif: {tabel, kolom, kolom_nip, kolom_tanggal}. */
    public static List<String[]> sumberAktif(Connection koneksi) throws Exception {
        List<String[]> l = new ArrayList<>();
        try (PreparedStatement p = koneksi.prepareStatement(
                "select nama_tabel,nama_kolom,ifnull(kolom_nip,''),ifnull(kolom_tanggal,'') "
                + "from alergi_sumber_kolom where aktif='1'");
                ResultSet r = p.executeQuery()) {
            while (r.next()) {
                l.add(new String[]{r.getString(1), r.getString(2), r.getString(3), r.getString(4)});
            }
        }
        return l;
    }

    /**
     * Pindai semua sumber aktif lalu isi/kemaskini alergi_mapping_teks.
     * Baris yang sudah dikoreksi manual (asal='Manual') tidak ditimpa.
     *
     * @return {jumlah tabel dipindai, teks unik, yang dapat kode}
     */
    public static int[] pindai(Connection koneksi) throws Exception {
        Map<String, Integer> frek = new LinkedHashMap<>();
        int tabel = 0;
        for (String[] s : sumberAktif(koneksi)) {
            try (PreparedStatement p = koneksi.prepareStatement(
                    "select `" + s[1] + "` from `" + s[0] + "` where `" + s[1] + "` is not null and trim(`" + s[1] + "`)<>''");
                    ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    for (String t : pecah(r.getString(1))) {
                        frek.merge(t, 1, Integer::sum);
                    }
                }
                tabel++;
            } catch (Exception e) {
                System.out.println("Notif lewati sumber " + s[0] + "." + s[1] + " : " + e);
            }
        }
        int dapatKode = 0;
        try (PreparedStatement p = koneksi.prepareStatement(
                "insert into alergi_mapping_teks (teks,allergy_code,category,tindakan,asal,jml_temuan,tgl_update) "
                + "values (?,?,?,?,'Otomatis',?,now()) on duplicate key update "
                + "jml_temuan=values(jml_temuan),"
                + "allergy_code=if(asal='Manual',allergy_code,values(allergy_code)),"
                + "category=if(asal='Manual',category,values(category)),"
                + "tindakan=if(asal='Manual',tindakan,values(tindakan)),"
                + "tgl_update=now()")) {
            for (Map.Entry<String, Integer> e : frek.entrySet()) {
                String[] t = tebak(koneksi, e.getKey());
                p.setString(1, e.getKey());
                p.setString(2, t[0]);
                p.setString(3, t[1]);
                p.setString(4, t[2]);
                p.setInt(5, e.getValue());
                p.addBatch();
                if (t[0] != null && !"Abaikan".equals(t[2])) {
                    dapatKode++;
                }
            }
            p.executeBatch();
        }
        return new int[]{tabel, frek.size(), dapatKode};
    }

    /**
     * Tulis hasil pemetaan ke alergi_pasien, satu baris per (no_rawat, kode).
     *
     * @return {baris ditulis, token dilewati}
     */
    public static int[] terapkan(Connection koneksi) throws Exception {
        Map<String, String[]> kamus = new LinkedHashMap<>();
        try (PreparedStatement p = koneksi.prepareStatement(
                "select teks,allergy_code,category,reactioncode,severity from alergi_mapping_teks "
                + "where tindakan<>'Abaikan' and allergy_code is not null and allergy_code<>''");
                ResultSet r = p.executeQuery()) {
            while (r.next()) {
                kamus.put(r.getString(1), new String[]{r.getString(2), r.getString(3), r.getString(4), r.getString(5)});
            }
        }
        int ditulis = 0, dilewati = 0;
        if (kamus.isEmpty()) {
            return new int[]{0, 0};
        }
        try (PreparedStatement ins = koneksi.prepareStatement(
                "insert ignore into alergi_pasien "
                + "(no_rawat,tgl_perawatan,allergy_code,category,nippetugas,note,reactioncode,severity) "
                + "values (?,?,?,?,?,?,?,?)")) {
            for (String[] s : sumberAktif(koneksi)) {
                String kolTgl = s[3].isEmpty() ? "now()" : "`" + s[3] + "`";
                String kolNip = s[2].isEmpty() ? "''" : "`" + s[2] + "`";
                String sql = "select no_rawat,`" + s[1] + "` as teks," + kolTgl + " as tgl," + kolNip + " as nip "
                        + "from `" + s[0] + "` where `" + s[1] + "` is not null and trim(`" + s[1] + "`)<>''";
                try (PreparedStatement p = koneksi.prepareStatement(sql);
                        ResultSet r = p.executeQuery()) {
                    while (r.next()) {
                        String noRawat = r.getString("no_rawat");
                        String asli = r.getString("teks");
                        String tgl = r.getString("tgl");
                        String nip = r.getString("nip");
                        if (noRawat == null || noRawat.trim().isEmpty()) {
                            continue;
                        }
                        for (String t : pecah(asli)) {
                            String[] m = kamus.get(t);
                            if (m == null) {
                                dilewati++;
                                continue;
                            }
                            ins.setString(1, noRawat);
                            ins.setString(2, (tgl == null || tgl.length() < 10) ? null
                                    : (tgl.length() == 10 ? tgl + " 00:00:00" : tgl));
                            ins.setString(3, m[0]);
                            ins.setString(4, m[1] == null ? "Makanan" : m[1]);
                            ins.setString(5, nip == null ? "" : (nip.length() > 10 ? nip.substring(0, 10) : nip));
                            ins.setString(6, "Migrasi dari " + s[0] + "." + s[1] + " : " + asli);
                            ins.setString(7, m[2]);
                            ins.setString(8, m[3] == null ? "unable-to-assess" : m[3]);
                            try {
                                ditulis += ins.executeUpdate();
                            } catch (Exception ex) {
                                dilewati++; // mis. no_rawat tidak ada di reg_periksa (FK)
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Notif terapkan lewati " + s[0] + " : " + e);
                }
            }
        }
        return new int[]{ditulis, dilewati};
    }
}
