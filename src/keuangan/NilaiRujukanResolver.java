package keuangan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Resolver nilai rujukan lab per kelompok umur × JK × kondisi.
 *
 * <p>Dipakai dari DlgPeriksaLaboratorium (dan variant) saat load daftar
 * pemeriksaan — gantikan 4-way branch (ld/la/pd/pa) dengan lookup ke
 * {@code template_lab_nilai_rujukan}. Kalau tidak ada row untuk
 * (template, kelompok, jk), fallback ke kolom legacy di template_laboratorium
 * ({@code nilai_rujukan_ld/la/pd/pa}) biar backward-compatible.
 *
 * <p>Master kelompok umur di-cache di memory (rarely changes, hanya diubah
 * via DlgKelompokUmur admin form) — panggil {@link #invalidateCache()}
 * setelah edit master.
 */
public class NilaiRujukanResolver {

    private static final String KONDISI_DEFAULT = "";

    /** Satu entry master_kelompok_umur. */
    public static final class Kelompok {
        public final String kode;
        public final String nama;
        public final int    umurMinHari;
        public final Integer umurMaxHari; // null = infinity
        public final int    urut;

        Kelompok(String k, String n, int min, Integer max, int u) {
            this.kode = k; this.nama = n; this.umurMinHari = min;
            this.umurMaxHari = max; this.urut = u;
        }

        /** True kalau {@code umurHari} masuk range kelompok ini. */
        boolean contains(int umurHari) {
            if (umurHari < umurMinHari) return false;
            return umurMaxHari == null || umurHari <= umurMaxHari;
        }
    }

    private static volatile List<Kelompok> cache;

    /** Invalidate cache — panggil setelah edit master_kelompok_umur. */
    public static void invalidateCache() { cache = null; }

    /** Load semua kelompok umur (cached). Ordered by urut asc. */
    public static List<Kelompok> listKelompok(Connection conn) {
        List<Kelompok> c = cache;
        if (c != null) return c;
        List<Kelompok> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT kd_kelompok_umur, nama, umur_min_hari, umur_max_hari, urut " +
                "FROM master_kelompok_umur ORDER BY urut, umur_min_hari");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int max = rs.getInt("umur_max_hari");
                Integer maxV = rs.wasNull() ? null : max;
                list.add(new Kelompok(
                    rs.getString("kd_kelompok_umur"),
                    rs.getString("nama"),
                    rs.getInt("umur_min_hari"),
                    maxV,
                    rs.getInt("urut")));
            }
        } catch (Exception e) {
            System.out.println("NilaiRujukanResolver.listKelompok: " + e);
        }
        cache = list;
        return list;
    }

    /** Cari kode kelompok umur dari umur dalam hari. Return null kalau tidak ada match. */
    public static String kelompokUmurFor(Connection conn, int umurHari) {
        for (Kelompok k : listKelompok(conn)) {
            if (k.contains(umurHari)) return k.kode;
        }
        return null;
    }

    /** Container untuk hasil resolve yang butuh full info (normal + kritis). */
    public static final class Thresholds {
        public final String display;      // "min - max" atau nilai_text atau legacy
        public final Double normMin, normMax;     // numeric parsed dari nilai_min/max
        public final Double kritisMin, kritisMax; // numeric parsed dari kritis_min/max
        public Thresholds(String display, Double nMin, Double nMax, Double kMin, Double kMax) {
            this.display = display;
            this.normMin = nMin; this.normMax = nMax;
            this.kritisMin = kMin; this.kritisMax = kMax;
        }
    }

    /**
     * Full resolve: return display text + parsed thresholds untuk klasifikasi nilai kritis.
     * Dipakai oleh DlgCariPeriksaLab renderer untuk coloring hasil lab.
     */
    public static Thresholds resolveThresholds(Connection conn, int idTemplate, String jk, int umurHari,
                                               String legacyLd, String legacyLa,
                                               String legacyPd, String legacyPa) {
        String kd = kelompokUmurFor(conn, umurHari);
        Double nMin = null, nMax = null, kMin = null, kMax = null;
        String text = null;
        if (kd != null) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT nilai_min, nilai_max, nilai_text, kritis_min, kritis_max FROM template_lab_nilai_rujukan " +
                    "WHERE id_template=? AND kd_kelompok_umur=? AND kondisi='' AND (jk=? OR jk='') " +
                    "ORDER BY jk='' ASC LIMIT 1")) {
                ps.setInt(1, idTemplate);
                ps.setString(2, kd);
                ps.setString(3, jk == null ? "" : jk);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        nMin = parseNum(rs.getString("nilai_min"));
                        nMax = parseNum(rs.getString("nilai_max"));
                        kMin = parseNum(rs.getString("kritis_min"));
                        kMax = parseNum(rs.getString("kritis_max"));
                        String t = rs.getString("nilai_text");
                        if (t != null && !t.trim().isEmpty()) text = t;
                    }
                }
            } catch (Exception e) {
                System.out.println("NilaiRujukanResolver.resolveThresholds: " + e);
            }
        }
        String display;
        if (text != null) display = text;
        else if (nMin != null && nMax != null) display = nMin + " - " + nMax;
        else if (nMin != null) display = String.valueOf(nMin);
        else if (nMax != null) display = String.valueOf(nMax);
        else {
            // Fallback legacy
            boolean dewasa = isDewasaKelompok(kd);
            String leg = "L".equalsIgnoreCase(jk) ? (dewasa ? legacyLd : legacyLa)
                                                  : (dewasa ? legacyPd : legacyPa);
            display = leg == null ? "" : leg;
        }
        return new Thresholds(display, nMin, nMax, kMin, kMax);
    }

    /**
     * Parse nilai dari string hasil lab maupun ambang di master.
     *
     * Alat lab RSCB menulis pemisah ribuan dengan KOMA dan desimal dengan TITIK
     * ("357,000 /µl" = 357 ribu; "14.8 gr/dl" = empat belas koma delapan). Versi
     * lama membaca koma sebagai desimal, jadi 357,000 jadi 357 — di bawah ambang
     * 20.000 — dan memunculkan alarm nilai kritis palsu. Lihat tabel audit
     * 2026-10-03: 31 dari 32 catatan palsu gara-gara ini.
     *
     * Aturannya sekarang:
     *   - dua-duanya ada  -> yang muncul terakhir = desimal, satunya ribuan
     *   - koma saja       -> kelompok 3 digit ("357,000") = ribuan, selain itu desimal ("2,8")
     *   - titik saja      -> hanya dianggap ribuan kalau berkelompok ganda ("1.234.567");
     *                        "14.8" dan "150.000" dibaca apa adanya oleh Double
     */
    public static Double parseNum(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        // Ambang kadang ditulis bersama tanda pembanding: "< 20000", ">= 7.8"
        t = t.replaceAll("^[<>=≤≥]+\\s*", "").trim();
        if (t.isEmpty()) return null;

        boolean adaTitik = t.indexOf('.') >= 0, adaKoma = t.indexOf(',') >= 0;
        if (adaTitik && adaKoma) {
            if (t.lastIndexOf(',') > t.lastIndexOf('.')) t = t.replace(".", "").replace(',', '.'); // 1.234,56
            else                                         t = t.replace(",", "");                   // 1,234.56
        } else if (adaKoma) {
            t = t.matches("-?\\d{1,3}(,\\d{3})+") ? t.replace(",", "") : t.replace(',', '.');
        } else if (adaTitik && t.matches("-?\\d{1,3}(\\.\\d{3}){2,}")) {
            t = t.replace(".", "");
        }
        try { return Double.parseDouble(t); } catch (NumberFormatException ignored) {}
        return null;
    }

    /**
     * Resolve nilai rujukan siap-tampil untuk satu pemeriksaan.
     *
     * @param jk         "L" atau "P" (pasien).
     * @param umurHari   umur pasien dalam hari (dihitung dari tgl_lahir).
     * @param legacyLd,legacyLa,legacyPd,legacyPa  kolom legacy dari template_laboratorium
     *                                             — dipakai sebagai fallback.
     * @return String siap-tampil: {@code nilai_text} kalau ada, else {@code "min – max"},
     *         else legacy value, else empty string.
     */
    public static String resolve(Connection conn, int idTemplate, String jk, int umurHari,
                                 String legacyLd, String legacyLa,
                                 String legacyPd, String legacyPa) {
        String kd = kelompokUmurFor(conn, umurHari);
        // 1) Lookup child table — prefer jk-specific (L/P) over catch-all ('').
        if (kd != null) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT nilai_min, nilai_max, nilai_text FROM template_lab_nilai_rujukan " +
                    "WHERE id_template=? AND kd_kelompok_umur=? AND kondisi=? AND (jk=? OR jk='') " +
                    "ORDER BY jk='' ASC LIMIT 1")) {
                ps.setInt(1, idTemplate);
                ps.setString(2, kd);
                ps.setString(3, KONDISI_DEFAULT);
                ps.setString(4, jk == null ? "" : jk);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String text = rs.getString("nilai_text");
                        if (text != null && !text.trim().isEmpty()) return text;
                        String mn = rs.getString("nilai_min");
                        String mx = rs.getString("nilai_max");
                        if (mn != null && mx != null) return mn + " - " + mx;
                        if (mn != null) return mn;
                        if (mx != null) return mx;
                    }
                }
            } catch (Exception e) {
                System.out.println("NilaiRujukanResolver.resolve: " + e);
            }
        }
        // 2) Fallback ke legacy columns (ld/la/pd/pa).
        // Dewasa/Anak boundary: kelompok < DWS dianggap "Anak".
        boolean dewasa = isDewasaKelompok(kd);
        String legacy;
        if ("L".equalsIgnoreCase(jk)) legacy = dewasa ? legacyLd : legacyLa;
        else                          legacy = dewasa ? legacyPd : legacyPa;
        return legacy == null ? "" : legacy;
    }

    /** Kelompok DWS, PRL, LNS termasuk Dewasa; sisanya Anak. */
    private static boolean isDewasaKelompok(String kd) {
        if (kd == null) return true; // default Dewasa
        return "DWS".equals(kd) || "PRL".equals(kd) || "LNS".equals(kd);
    }

    /** Versi publik dari {@link #isDewasaKelompok} — dipakai saat migrasi kolom legacy. */
    public static boolean isDewasa(String kd) {
        return isDewasaKelompok(kd);
    }
}
