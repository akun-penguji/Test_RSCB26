/*
 * AutoMapObatKFA.java
 *
 * Pemetaan otomatis obat Khanza (databarang) ke kode KFA SatuSehat
 * (satu_sehat_mapping_obat), memakai master lokal satu_sehat_kfa_master.
 *
 * Cara kerja:
 *   1. Ambil item yang dipakai di pelayanan tapi belum ada di mapping.
 *   2. Nama Khanza dibersihkan: segmen pabrik & sumber dana (APBD/BLUD + tahun)
 *      dibuang, lalu diambil zat aktif, kekuatan, dan bentuk sediaannya.
 *      Contoh: "OMEprazole Inj 40 mg - Dexa - BLUD 2025"
 *              -> zat "omeprazole", kekuatan "40 mg", bentuk "Injeksi", pabrik "dexa".
 *   3. Cari kandidat di KFA master: nama mengandung zat + kekuatan + bentuk.
 *   4. Kandidat diberi skor (pabrik cocok, active, bukan "tidak dipakai"),
 *      dan hanya ditulis bila skor pemenang jelas di atas pesaingnya.
 *   5. Kolom form/route/numerator/denominator diisi dari tabel padanan bentuk
 *      sediaan yang disusun dari pemetaan manual yang sudah ada sebelumnya.
 *
 * Item alkes/BHP (spuit, kateter, kasa, dll) sengaja dilewati — itu ranah KFA
 * alkes/Device, bukan Medication.
 */
package bridging;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AutoMapObatKFA {

    public static final String SYS_KFA = "http://sys-ids.kemkes.go.id/kfa";
    public static final String SYS_FORM = "http://terminology.kemkes.go.id/CodeSystem/medication-form";
    public static final String SYS_ROUTE = "http://www.whocc.no/atc";

    /** Padanan bentuk sediaan KFA -> {form_code, form_display, route_code, route_display, numerator, denominator}. */
    private static final String[][] BENTUK = {
        {"tablet salut selaput", "BS077", "Tablet Salut Selaput", "O", "Oral", "mg", "TAB"},
        {"tablet salut gula", "BS077", "Tablet Salut Selaput", "O", "Oral", "mg", "TAB"},
        {"tablet dispersibel", "BS073", "Tablet Dispersibel", "O", "Oral", "mg", "TAB"},
        {"tablet kunyah", "BS069", "Tablet Kunyah", "O", "Oral", "mg", "TAB"},
        {"tablet", "BS066", "Tablet", "O", "Oral", "mg", "TAB"},
        {"kaplet", "BS066", "Tablet", "O", "Oral", "mg", "TAB"},
        {"kapsul pelepasan lambat", "BS021", "Kapsul Pelepasan Lambat", "O", "Oral", "mg", "CAPS"},
        {"kapsul lepas tunda", "BS021", "Kapsul Pelepasan Lambat", "O", "Oral", "mg", "CAPS"},
        {"kapsul", "BS019", "Kapsul", "O", "Oral", "mg", "CAPS"},
        {"serbuk injeksi", "BS049", "Serbuk Injeksi", "P", "Parenteral", "g", "VIAL"},
        {"larutan injeksi", "BS034", "Larutan Injeksi", "P", "Parenteral", "mL", "AMP"},
        {"injeksi", "BS034", "Larutan Injeksi", "P", "Parenteral", "mL", "AMP"},
        {"infus", "BS035", "Infus", "P", "Parenteral", "mL", "BTL"},
        {"sirup kering", "BS056", "Sirup Kering", "O", "Oral", "mL", "BTL"},
        {"sirup", "BS046", "Sirup", "O", "Oral", "mL", "SYRUP"},
        {"suspensi", "BS060", "Suspensi", "O", "Oral", "mL", "ORSUSP"},
        {"larutan inhalasi", "BS033", "Larutan Inhalasi", "Inhal", "Inhalasi (dihirup)", "mL", "AMP"},
        {"cairan inhalasi", "BS033", "Larutan Inhalasi", "Inhal", "Inhalasi (dihirup)", "mL", "AMP"},
        {"enema", "BS011", "Enema", "R", "Rektum", "mL", "REC"},
        {"tetes oral", "BS087", "Tetes Oral (Oral Drops)", "O", "Oral", "mL", "DRP"}
    };

    /** Kata kunci bentuk pada nama Khanza -> frasa bentuk yang dicari di KFA. */
    private static final String[][] BENTUK_KHANZA = {
        {"serbuk inj", "serbuk injeksi"}, {"inj", "injeksi"}, {"injeksi", "injeksi"},
        {"infus", "infus"}, {"infuse", "infus"},
        {"tab salut", "tablet salut selaput"}, {"kapl", "tablet"}, {"kaplet", "tablet"},
        {"tab", "tablet"}, {"tablet", "tablet"},
        {"caps", "kapsul"}, {"kaps", "kapsul"}, {"kapsul", "kapsul"},
        {"syr", "sirup"}, {"sirup", "sirup"}, {"sirop", "sirup"},
        {"susp", "suspensi"}, {"drop", "tetes oral"}, {"tetes oral", "tetes oral"},
        {"nebu", "larutan inhalasi"}, {"inhalasi", "larutan inhalasi"}, {"enema", "enema"}
    };

    /** Item non-obat yang tidak perlu Medication (ranah alkes/BHP). */
    private static final Pattern ALKES = Pattern.compile(
            ".*(spuit|syringe|kateter|catheter|infus set|iv cath|wing|kasa|kassa|hypafix|cannofix|plester"
            + "|handscoon|sarung tangan|masker|underpad|urine bag|nasal|oksigen|elektrode|elektroda|jarum"
            + "|blood set|threeway|three way|nacl irigasi|alkohol swab|povidon.*swab|benang|silk|catgut"
            + "|blade|scalpel|verban|perban|micropore|tegaderm|stomach tube|ngt|folley|foley|cairan rinse"
            + "|reagen|strip|lancet|test|tabung|pot |gelas|tissue|tisu).*");

    private final Connection koneksi;
    private final List<String[]> laporan = new ArrayList<>();  // {kode_brng,nama,kfa,display,status}

    public AutoMapObatKFA(Connection koneksi) {
        this.koneksi = koneksi;
    }

    public List<String[]> getLaporan() {
        return laporan;
    }

    /**
     * Jalankan pemetaan otomatis.
     *
     * @param bulan   rentang pemakaian yang diperhitungkan (bulan ke belakang)
     * @param terapkan true = tulis ke satu_sehat_mapping_obat, false = simulasi
     * @return {diperiksa, cocok, dilewati alkes, tidak ketemu, ragu}
     */
    public int[] jalankan(int bulan, boolean terapkan) throws Exception {
        List<String[]> item = itemBelumMapping(bulan);
        int cocok = 0, alkes = 0, kosong = 0, ragu = 0;

        try (PreparedStatement ins = koneksi.prepareStatement(
                "insert ignore into satu_sehat_mapping_obat (kode_brng,obat_code,obat_system,obat_display,"
                + "form_code,form_system,form_display,numerator_code,numerator_system,denominator_code,"
                + "denominator_system,route_code,route_system,route_display) "
                + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {

            for (String[] it : item) {
                String kode = it[0], nama = it[1];
                String n = nama.toLowerCase(Locale.ROOT);
                if (ALKES.matcher(n).matches()) {
                    alkes++;
                    laporan.add(new String[]{kode, nama, "", "", "LEWAT-ALKES"});
                    continue;
                }
                Urai u = urai(nama);
                if (u.zat.isEmpty() || u.kekuatan.isEmpty()) {
                    kosong++;
                    laporan.add(new String[]{kode, nama, "", "", "TAK-TERURAI"});
                    continue;
                }
                Kandidat k = cari(u);
                if (k == null) {
                    kosong++;
                    laporan.add(new String[]{kode, nama, "", "", "TIDAK-KETEMU"});
                    continue;
                }
                if (k.ragu) {
                    ragu++;
                    laporan.add(new String[]{kode, nama, k.kfa, k.display, "RAGU"});
                    continue;
                }
                String[] b = padananBentuk(k.display);
                if (b == null) {
                    ragu++;
                    laporan.add(new String[]{kode, nama, k.kfa, k.display, "BENTUK-TAK-DIKENAL"});
                    continue;
                }
                cocok++;
                laporan.add(new String[]{kode, nama, k.kfa, k.display, "COCOK"});
                if (terapkan) {
                    int i = 1;
                    ins.setString(i++, kode);
                    ins.setString(i++, k.kfa);
                    ins.setString(i++, SYS_KFA);
                    ins.setString(i++, potong(k.display, 80));
                    ins.setString(i++, b[1]);
                    ins.setString(i++, SYS_FORM);
                    ins.setString(i++, b[2]);
                    ins.setString(i++, b[5]);
                    ins.setString(i++, SYS_KFA);
                    ins.setString(i++, b[6]);
                    ins.setString(i++, SYS_KFA);
                    ins.setString(i++, b[3]);
                    ins.setString(i++, SYS_ROUTE);
                    ins.setString(i, b[4]);
                    ins.executeUpdate();
                }
            }
        }
        return new int[]{item.size(), cocok, alkes, kosong, ragu};
    }

    /** Item yang dipakai di pelayanan tapi belum ada di satu_sehat_mapping_obat. */
    private List<String[]> itemBelumMapping(int bulan) throws Exception {
        Map<String, String> m = new LinkedHashMap<>();
        String sql = "select d.kode_brng, b.nama_brng, count(*) jml "
                + "from detail_pemberian_obat d inner join databarang b on b.kode_brng=d.kode_brng "
                + "left join satu_sehat_mapping_obat s on s.kode_brng=d.kode_brng "
                + "where s.kode_brng is null and d.tgl_perawatan >= date_sub(curdate(), interval ? month) "
                + "group by d.kode_brng, b.nama_brng order by jml desc";
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            p.setInt(1, bulan);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    m.put(r.getString(1), r.getString(2));
                }
            }
        }
        List<String[]> l = new ArrayList<>();
        for (Map.Entry<String, String> e : m.entrySet()) {
            l.add(new String[]{e.getKey(), e.getValue() == null ? "" : e.getValue()});
        }
        return l;
    }

    // ------------------------------------------------------------- penguraian
    static final class Urai {
        String zat = "", kekuatan = "", bentuk = "", pabrik = "";
    }

    private static final Pattern KEKUATAN = Pattern.compile(
            "(\\d+(?:[.,]\\d+)?)\\s*(mg|mcg|ug|g|ml|iu|%)\\s*(?:/\\s*(\\d+(?:[.,]\\d+)?)?\\s*(ml|mg|g))?",
            Pattern.CASE_INSENSITIVE);

    /** Pecah nama Khanza jadi zat aktif, kekuatan, bentuk, dan pabrik. */
    static Urai urai(String nama) {
        Urai u = new Urai();
        String s = nama.toLowerCase(Locale.ROOT).trim();
        // buang sumber dana + tahun anggaran
        s = s.replaceAll("\\b(apbd|blud|apbn|dak|bok|hibah)\\b\\s*\\d{0,4}", " ");
        String[] seg = s.split("\\s+-\\s+|\\s+-|-\\s+");
        String utama = seg[0];
        if (seg.length > 1) {
            u.pabrik = seg[seg.length - 1].replaceAll("[^a-z ]", " ").trim();
            if (u.pabrik.isEmpty() && seg.length > 2) {
                u.pabrik = seg[seg.length - 2].replaceAll("[^a-z ]", " ").trim();
            }
        }
        Matcher mk = KEKUATAN.matcher(utama);
        if (mk.find()) {
            String angka = mk.group(1).replace(',', '.');
            String satuan = mk.group(2).toLowerCase(Locale.ROOT);
            if (satuan.equals("ug")) {
                satuan = "mcg";
            }
            u.kekuatan = rapikan(angka) + " " + satuan;
            if (mk.group(4) != null) {
                String pembagi = mk.group(3) == null ? "" : rapikan(mk.group(3).replace(',', '.'));
                u.kekuatan += "/" + (pembagi.isEmpty() ? "" : pembagi + " ") + mk.group(4).toLowerCase(Locale.ROOT);
            }
        }
        for (String[] b : BENTUK_KHANZA) {
            if (utama.matches(".*\\b" + Pattern.quote(b[0]) + "\\b.*")) {
                u.bentuk = b[1];
                break;
            }
        }
        // zat aktif = sisa huruf setelah kekuatan/bentuk/angka dibuang
        String zat = utama.replaceAll(KEKUATAN.pattern(), " ");
        for (String[] b : BENTUK_KHANZA) {
            zat = zat.replaceAll("\\b" + Pattern.quote(b[0]) + "\\b", " ");
        }
        zat = zat.replaceAll("[^a-z ]", " ").replaceAll("\\s{2,}", " ").trim();
        // ambil kata terpanjang (nama zat), abaikan kata pendek/umum
        String terpanjang = "";
        for (String w : zat.split(" ")) {
            if (w.length() > terpanjang.length() && w.length() >= 4
                    && !w.matches("obat|generik|dewasa|anak|steril|forte|plus|drops|salut|film")) {
                terpanjang = w;
            }
        }
        u.zat = terpanjang;
        return u;
    }

    private static String rapikan(String angka) {
        if (angka.endsWith(".0")) {
            return angka.substring(0, angka.length() - 2);
        }
        return angka;
    }

    // -------------------------------------------------------------- pencarian
    static final class Kandidat {
        String kfa, display;
        boolean ragu;
    }

    /**
     * Cari kandidat KFA terbaik; tandai ragu bila pemenangnya tidak meyakinkan.
     *
     * Zat aktif dicocokkan sebagai KATA UTUH (regexp \b) — tanpa itu "omeprazole"
     * ikut cocok ke "Esomeprazole", zat yang berbeda. Kekuatan dicoba beberapa
     * varian satuan karena Khanza menulis "1000 mg" sementara KFA "1 g".
     */
    private Kandidat cari(Urai u) throws Exception {
        List<String[]> kand = new ArrayList<>();
        for (String kk : variasiKekuatan(u.kekuatan)) {
            kand = kandidat(u.zat, kk, u.bentuk);
            if (!kand.isEmpty()) {
                break;
            }
        }
        if (kand.isEmpty() && !u.bentuk.isEmpty()) {
            // longgarkan bentuk: Khanza menulis "Inj" untuk larutan maupun serbuk injeksi
            for (String kk : variasiKekuatan(u.kekuatan)) {
                kand = kandidat(u.zat, kk, "");
                if (!kand.isEmpty()) {
                    break;
                }
            }
        }
        if (kand.isEmpty()) {
            return null;
        }
        int skorTerbaik = Integer.MIN_VALUE, skorKedua = Integer.MIN_VALUE;
        String[] terbaik = null;
        for (String[] k : kand) {
            String nm = k[1].toLowerCase(Locale.ROOT);
            int skor = 0;
            if (nm.startsWith(u.zat)) {
                skor += 30;                       // nama KFA diawali zat yang sama
            }
            if (nm.contains("tidak dipakai")) {
                skor -= 60;
            }
            if ("true".equalsIgnoreCase(k[2])) {
                skor += 10;
            }
            if (!u.bentuk.isEmpty() && nm.contains(u.bentuk)) {
                skor += 15;
            }
            if (!u.pabrik.isEmpty()) {
                String pab = (k[3] + " " + k[1]).toLowerCase(Locale.ROOT);
                for (String w : u.pabrik.split(" ")) {
                    if (w.length() >= 4 && pab.contains(w)) {
                        skor += 40;
                        break;
                    }
                }
            }
            skor -= Math.min(nm.length() / 12, 8);
            if (skor > skorTerbaik) {
                skorKedua = skorTerbaik;
                skorTerbaik = skor;
                terbaik = k;
            } else if (skor > skorKedua) {
                skorKedua = skor;
            }
        }
        Kandidat c = new Kandidat();
        c.kfa = terbaik[0];
        c.display = terbaik[1];
        c.ragu = kand.size() > 1 && (skorTerbaik - skorKedua) < 5 && skorTerbaik < 40;
        return c;
    }

    /** Kandidat KFA: zat sebagai kata utuh + kekuatan + (opsional) bentuk sediaan. */
    private List<String[]> kandidat(String zat, String kekuatan, String bentuk) {
        List<String[]> l = new ArrayList<>();
        String sql = "select kfa_code,name,ifnull(active,''),ifnull(manufacturer,'') "
                + "from satu_sehat_kfa_master where lower(name) regexp ? "
                // kode gaya ATC dibuang: itu kelas obat, bukan produk
                + "and kfa_code not regexp '^[A-Z][0-9][0-9]' "
                + (kekuatan.isEmpty() ? "" : "and lower(name) like ? ")
                + (bentuk.isEmpty() ? "" : "and lower(name) like ? ")
                + "order by length(name) limit 40";
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            int i = 1;
            // Batas kata hanya di AWAL: "amlodipin" harus cocok ke "Amlodipine",
            // "sucralfat" ke "Sucralfate"; tapi "omeprazole" tetap TIDAK cocok ke
            // "Esomeprazole" karena di sana token tidak berada di awal kata.
            p.setString(i++, "\\b" + zat);
            if (!kekuatan.isEmpty()) {
                p.setString(i++, "%" + kekuatan + "%");
            }
            if (!bentuk.isEmpty()) {
                p.setString(i, "%" + bentuk + "%");
            }
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    l.add(new String[]{r.getString(1), r.getString(2), r.getString(3), r.getString(4)});
                }
            }
        } catch (Exception e) {
            System.out.println("Notif kandidat KFA : " + e);
        }
        return l;
    }

    /**
     * Varian penulisan kekuatan: Khanza "1000 mg" vs KFA "1 g", "0.5 g" vs
     * "500 mg", "1000 mcg" vs "1 mg". Urutan = prioritas pencarian.
     */
    static List<String> variasiKekuatan(String kekuatan) {
        List<String> v = new ArrayList<>();
        if (kekuatan == null || kekuatan.isEmpty()) {
            v.add("");
            return v;
        }
        v.add(kekuatan);
        java.util.regex.Matcher m = Pattern.compile("^(\\d+(?:\\.\\d+)?) (mg|g|mcg)(.*)$").matcher(kekuatan);
        if (m.matches()) {
            double n = Double.parseDouble(m.group(1));
            String sat = m.group(2), sisa = m.group(3);
            if (sat.equals("mg") && n >= 1000 && n % 1000 == 0) {
                v.add(rapikan(trimNol(n / 1000)) + " g" + sisa);
            } else if (sat.equals("g")) {
                v.add(rapikan(trimNol(n * 1000)) + " mg" + sisa);
            } else if (sat.equals("mcg") && n >= 1000 && n % 1000 == 0) {
                v.add(rapikan(trimNol(n / 1000)) + " mg" + sisa);
            }
        }
        return v;
    }

    private static String trimNol(double d) {
        return (d == Math.floor(d)) ? String.valueOf((long) d) : String.valueOf(d);
    }

    /** Cari padanan bentuk sediaan dari nama KFA. */
    static String[] padananBentuk(String displayKfa) {
        String n = displayKfa == null ? "" : displayKfa.toLowerCase(Locale.ROOT);
        for (String[] b : BENTUK) {
            if (n.contains(b[0])) {
                return new String[]{b[0], b[1], b[2], b[3], b[4], b[5], b[6]};
            }
        }
        return null;
    }

    private static String potong(String s, int n) {
        return s == null ? "" : (s.length() > n ? s.substring(0, n) : s);
    }
}
