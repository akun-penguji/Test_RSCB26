package laporan;

import fungsi.koneksiDB;
import fungsi.akses;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

/**
 * Sensus Harian Pasien Rawat Inap (mulai jam 00.00 - 24.00) — realtime.
 *
 * Pilih Tanggal + Ruangan (bangsal) lalu [Tampilkan]: dihitung otomatis dari
 * kamar_inap. Bagian:
 *   - Pasien Masuk / Keluar / Pindahan Dari / Dipindahkan Ke / Meninggal <48j & >=48j
 *   - Ikhtisar 24 jam: OS Kemarin/Masuk/Pindahan/Jumlah-1/Keluar/Dipindahkan/
 *     Meninggal/Jumlah-2/Sisa, dipecah per Kelas & per Spesialis (kolom dinamis).
 *   - BOR Harian = Sisa OS / Kapasitas TT.
 * Dirender sebagai HTML (JTextPane) yang bisa langsung [Cetak].
 *
 * Sumber: kamar_inap -> kamar -> bangsal (ruangan), reg_periksa -> pasien,
 * dokter -> spesialis (Bag), penjab (Cara Bayar). Transfer dibedakan lewat
 * stts_pulang='Pindah Kamar'.
 */
public final class DlgSensusHarianRanap extends javax.swing.JDialog {

    private final Connection koneksi = koneksiDB.condb();
    private final SimpleDateFormat FTGL = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat FTAMPIL = new SimpleDateFormat("dd-MM-yyyy");

    private widget.Tanggal DTPTanggal;
    private JComboBox<String> cmbRuangan;   // item = "kd_bangsal|nm_bangsal"
    private JButton BtnTampil, BtnCetak, BtnKeluar;
    private JTextPane isi;

    /** Satu baris pasien pada sensus. */
    private static final class Row {
        String noRawat, nama, noRM, bag, kelas, jk, penjab, sttsPulang, wardLain;
        int lama, jamRawat;
    }

    public DlgSensusHarianRanap(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        muatRuangan();
        setTitle("Sensus Harian Pasien Rawat Inap");
    }

    private void initComponents() {
        JPanel atas = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        atas.add(new JLabel("Tanggal :"));
        DTPTanggal = new widget.Tanggal();
        DTPTanggal.setDisplayFormat("dd-MM-yyyy");
        DTPTanggal.setPreferredSize(new Dimension(95, 24));
        atas.add(DTPTanggal);
        atas.add(new JLabel("  Ruangan :"));
        cmbRuangan = new JComboBox<>();
        cmbRuangan.setPreferredSize(new Dimension(220, 24));
        atas.add(cmbRuangan);
        BtnTampil = new JButton("Tampilkan");
        BtnCetak = new JButton("Cetak");
        BtnKeluar = new JButton("Keluar");
        atas.add(BtnTampil);
        atas.add(BtnCetak);
        atas.add(BtnKeluar);

        isi = new JTextPane();
        isi.setContentType("text/html");
        isi.setEditable(false);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(atas, BorderLayout.NORTH);
        getContentPane().add(new JScrollPane(isi), BorderLayout.CENTER);
        setSize(1050, 720);

        BtnTampil.addActionListener(e -> tampil());
        BtnCetak.addActionListener(e -> cetak());
        BtnKeluar.addActionListener(e -> dispose());
    }

    private void muatRuangan() {
        cmbRuangan.addItem("|-- Semua Ruangan --");
        try (PreparedStatement ps = koneksi.prepareStatement(
                "SELECT kd_bangsal, nm_bangsal FROM bangsal WHERE status='1' ORDER BY nm_bangsal");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cmbRuangan.addItem(rs.getString("kd_bangsal") + "|" + rs.getString("nm_bangsal"));
            }
        } catch (Exception ex) {
            // fallback: tanpa filter status
            try (PreparedStatement ps = koneksi.prepareStatement(
                    "SELECT kd_bangsal, nm_bangsal FROM bangsal ORDER BY nm_bangsal");
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cmbRuangan.addItem(rs.getString("kd_bangsal") + "|" + rs.getString("nm_bangsal"));
                }
            } catch (Exception e2) {
                System.out.println("muatRuangan gagal: " + e2);
            }
        }
    }

    private String kdBangsal() {
        String v = (String) cmbRuangan.getSelectedItem();
        if (v == null) return "";
        int p = v.indexOf('|');
        return p < 0 ? "" : v.substring(0, p);
    }

    private String nmRuangan() {
        String v = (String) cmbRuangan.getSelectedItem();
        if (v == null) return "";
        int p = v.indexOf('|');
        String nm = (p < 0) ? v : v.substring(p + 1);
        return nm.startsWith("--") ? "Semua Ruangan" : nm;
    }

    private String tglSql() {
        try {
            Date d = FTAMPIL.parse(DTPTanggal.getSelectedItem() + "");
            return FTGL.format(d);
        } catch (Exception e) {
            return FTGL.format(new Date());
        }
    }

    // ==================== QUERY POPULASI ====================

    private static final String BASE =
        "SELECT ki.no_rawat, p.nm_pasien, r.no_rkm_medis, IFNULL(sp.nm_sps,'-') bag, "
      + "IFNULL(kmr.kelas,'-') kelas, p.jk, IFNULL(pj.png_jawab,'-') penjab, "
      + "IFNULL(ki.lama,0) lama, ki.stts_pulang, "
      + "TIMESTAMPDIFF(HOUR, CONCAT(ki.tgl_masuk,' ',ki.jam_masuk), "
      + "  CONCAT(NULLIF(ki.tgl_keluar,'0000-00-00'),' ',ki.jam_keluar)) jamrawat "
      + "FROM kamar_inap ki "
      + "JOIN kamar kmr ON ki.kd_kamar=kmr.kd_kamar "
      + "JOIN reg_periksa r ON r.no_rawat=ki.no_rawat "
      + "JOIN pasien p ON p.no_rkm_medis=r.no_rkm_medis "
      + "LEFT JOIN (SELECT no_rawat, MIN(kd_dokter) kd_dokter FROM dpjp_ranap GROUP BY no_rawat) dp ON dp.no_rawat=ki.no_rawat "
      + "LEFT JOIN dokter dk ON dk.kd_dokter=COALESCE(dp.kd_dokter, r.kd_dokter) "
      + "LEFT JOIN spesialis sp ON sp.kd_sps=dk.kd_sps "
      + "LEFT JOIN penjab pj ON pj.kd_pj=r.kd_pj ";

    /** Jalankan query populasi. bangsalOn=true -> filter kd_bangsal. */
    private List<Row> query(String extraWhere, String[] params) throws Exception {
        String kb = kdBangsal();
        StringBuilder sql = new StringBuilder(BASE).append("WHERE 1=1 ");
        List<String> ps = new ArrayList<>();
        if (kb != null && !kb.isEmpty()) {
            sql.append("AND kmr.kd_bangsal=? ");
            ps.add(kb);
        }
        sql.append("AND ").append(extraWhere).append(" ");
        sql.append("GROUP BY ki.no_rawat, ki.kd_kamar ORDER BY p.nm_pasien");
        for (String s : params) ps.add(s);

        List<Row> out = new ArrayList<>();
        try (PreparedStatement st = koneksi.prepareStatement(sql.toString())) {
            for (int i = 0; i < ps.size(); i++) st.setString(i + 1, ps.get(i));
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Row r = new Row();
                    r.noRawat = rs.getString("no_rawat");
                    r.nama = rs.getString("nm_pasien");
                    r.noRM = rs.getString("no_rkm_medis");
                    r.bag = rs.getString("bag");
                    r.kelas = rs.getString("kelas");
                    r.jk = rs.getString("jk");
                    r.penjab = rs.getString("penjab");
                    r.lama = rs.getInt("lama");
                    r.jamRawat = rs.getInt("jamrawat");
                    r.sttsPulang = rs.getString("stts_pulang");
                    out.add(r);
                }
            }
        }
        return out;
    }

    private static final String TRANSFER_IN =
        "EXISTS(SELECT 1 FROM kamar_inap x WHERE x.no_rawat=ki.no_rawat "
      + "AND x.stts_pulang='Pindah Kamar' AND x.tgl_keluar=ki.tgl_masuk)";
    private static final String KELUAR_HIDUP =
        "ki.stts_pulang NOT IN ('Pindah Kamar','Meninggal','-','','+') ";

    // ==================== RENDER ====================

    private void tampil() {
        try {
            isi.setText(bangunHtml(false));
            isi.setCaretPosition(0);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat sensus: " + ex);
            System.out.println("Sensus ranap gagal: " + ex);
        }
    }

    /** Wrapper untuk JTextPane layar. */
    private String bangunHtml(boolean cetak) throws Exception {
        String fs = cetak ? "9px" : "11px";
        return "<html><body style='font-family:Arial;font-size:" + fs + ";'>"
             + bodyHtml(cetak) + "</body></html>";
    }

    private String bodyHtml(boolean cetak) throws Exception {
        String t = tglSql();

        List<Row> kemarin = query("ki.tgl_masuk < ? AND (ki.tgl_keluar='0000-00-00' OR ki.tgl_keluar IS NULL OR ki.tgl_keluar >= ? OR ki.stts_pulang='-')",
                new String[]{t, t});
        List<Row> masuk = query("ki.tgl_masuk = ? AND NOT " + TRANSFER_IN, new String[]{t});
        List<Row> pindahan = query("ki.tgl_masuk = ? AND " + TRANSFER_IN, new String[]{t});
        List<Row> keluar = query("ki.tgl_keluar = ? AND " + KELUAR_HIDUP, new String[]{t});
        List<Row> dipindah = query("ki.tgl_keluar = ? AND ki.stts_pulang='Pindah Kamar'", new String[]{t});
        List<Row> meninggal = query("ki.tgl_keluar = ? AND ki.stts_pulang='Meninggal'", new String[]{t});
        List<Row> sisa = query("ki.tgl_masuk <= ? AND (ki.tgl_keluar='0000-00-00' OR ki.tgl_keluar IS NULL OR ki.tgl_keluar > ? OR ki.stts_pulang='-')",
                new String[]{t, t});

        List<Row> meninggalLt = new ArrayList<>(), meninggalGe = new ArrayList<>();
        for (Row r : meninggal) (r.jamRawat < 48 ? meninggalLt : meninggalGe).add(r);

        int kapasitas = kapasitasTT();

        String fs = cetak ? "9px" : "11px";
        StringBuilder h = new StringBuilder();
        h.append("<div style='text-align:center;'><b>SENSUS HARIAN PASIEN RAWAT INAP</b><br>")
         .append("MULAI JAM 00.00 - 24.00</div>");
        h.append("<table width='100%' style='font-size:").append(fs).append(";'><tr>")
         .append("<td>HARI/TANGGAL : <b>").append(FTAMPIL.format(new Date(parse(t)))).append("</b></td>")
         .append("<td>RUANGAN : <b>").append(esc(nmRuangan())).append("</b></td>")
         .append("<td>KAPASITAS T.T : <b>").append(kapasitas).append(" bed</b></td>")
         .append("</tr></table><hr>");

        // Masuk | Keluar
        h.append(dua(
            seksiMasuk("PASIEN MASUK", masuk),
            seksiKeluar("PASIEN KELUAR", keluar), cetak));
        // Pindahan | Dipindahkan
        h.append(dua(
            seksiMasuk("PASIEN PINDAHAN DARI", pindahan),
            seksiMasuk("PASIEN DIPINDAHKAN KE", dipindah), cetak));
        // Meninggal <48 | >=48
        h.append(dua(
            seksiMeninggal("PASIEN MENINGGAL < 48 JAM", meninggalLt),
            seksiMeninggal("PASIEN MENINGGAL &ge; 48 JAM", meninggalGe), cetak));

        // Ikhtisar 24 jam
        h.append(ikhtisar(kemarin, masuk, pindahan, keluar, dipindah, meninggal, sisa));

        // Footer BOR
        h.append(footer(sisa, masuk, pindahan, keluar, dipindah, meninggal, kapasitas));

        h.append("<br><table width='100%'><tr><td></td><td style='text-align:center;'>")
         .append("Pembuat Laporan<br><br><br>( ................................ )<br>Ttd. dan Nama Jelas</td></tr></table>");
        return h.toString();
    }

    private int kapasitasTT() {
        String kb = kdBangsal();
        String sql = "SELECT COUNT(*) FROM kamar k WHERE k.statusdata='1'"
                + (kb.isEmpty() ? "" : " AND k.kd_bangsal=?");
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            if (!kb.isEmpty()) ps.setString(1, kb);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("kapasitasTT gagal: " + e);
        }
        return 0;
    }

    private long parse(String ymd) {
        try { return FTGL.parse(ymd).getTime(); } catch (Exception e) { return System.currentTimeMillis(); }
    }

    /** Saat cetak: susun VERTIKAL (masing-masing full width) agar kolom lebar &
     *  tidak terpotong. Saat layar: 2 kolom bersebelahan. */
    private String dua(String kiri, String kanan, boolean cetak) {
        if (cetak) {
            return kiri + "<div style='height:4px;'></div>" + kanan;
        }
        return "<table width='100%' cellspacing='0'><tr valign='top'>"
             + "<td width='50%'>" + kiri + "</td><td width='50%'>" + kanan + "</td></tr></table>";
    }

    private String seksiMasuk(String judul, List<Row> rows) {
        StringBuilder s = new StringBuilder();
        s.append("<b>").append(judul).append("</b>");
        s.append("<table width='100%' border='1' cellspacing='0' cellpadding='2' style='border-collapse:collapse;table-layout:fixed;'>");
        s.append("<tr bgcolor='#f0f0f0'><td width='5%'>No</td><td width='39%'>Nama Pasien</td><td width='16%'>No.RM</td><td width='24%'>Bag</td><td width='16%'>Kelas</td></tr>");
        int n = 0;
        for (Row r : rows) {
            n++;
            s.append("<tr><td>").append(n).append("</td><td>").append(esc(r.nama))
             .append("</td><td>").append(esc(r.noRM)).append("</td><td>").append(esc(r.bag))
             .append("</td><td>").append(esc(r.kelas)).append("</td></tr>");
        }
        if (n == 0) s.append("<tr><td colspan='5'><i>- kosong -</i></td></tr>");
        s.append("</table>");
        return s.toString();
    }

    private String seksiKeluar(String judul, List<Row> rows) {
        StringBuilder s = new StringBuilder();
        s.append("<b>").append(judul).append("</b>");
        s.append("<table width='100%' border='1' cellspacing='0' cellpadding='2' style='border-collapse:collapse;table-layout:fixed;'>");
        s.append("<tr bgcolor='#f0f0f0'><td width='4%'>No</td><td width='27%'>Nama Pasien</td><td width='13%'>No.RM</td><td width='17%'>Bag</td><td width='8%'>Kls</td><td width='7%'>P/A/L</td><td width='7%'>Lama</td><td width='17%'>Cara Bayar</td></tr>");
        int n = 0;
        for (Row r : rows) {
            n++;
            s.append("<tr><td>").append(n).append("</td><td>").append(esc(r.nama))
             .append("</td><td>").append(esc(r.noRM)).append("</td><td>").append(esc(r.bag))
             .append("</td><td>").append(esc(r.kelas)).append("</td><td>").append(pal(r.sttsPulang))
             .append("</td><td>").append(r.lama).append("</td><td>").append(esc(r.penjab)).append("</td></tr>");
        }
        if (n == 0) s.append("<tr><td colspan='8'><i>- kosong -</i></td></tr>");
        s.append("</table>");
        return s.toString();
    }

    private String seksiMeninggal(String judul, List<Row> rows) {
        StringBuilder s = new StringBuilder();
        s.append("<b>").append(judul).append("</b>");
        s.append("<table width='100%' border='1' cellspacing='0' cellpadding='2' style='border-collapse:collapse;table-layout:fixed;'>");
        s.append("<tr bgcolor='#f0f0f0'><td width='4%'>No</td><td width='31%'>Nama Pasien</td><td width='14%'>No.RM</td><td width='18%'>Bag</td><td width='9%'>Kls</td><td width='8%'>Lama</td><td width='16%'>Cara Bayar</td></tr>");
        int n = 0;
        for (Row r : rows) {
            n++;
            s.append("<tr><td>").append(n).append("</td><td>").append(esc(r.nama))
             .append("</td><td>").append(esc(r.noRM)).append("</td><td>").append(esc(r.bag))
             .append("</td><td>").append(esc(r.kelas)).append("</td><td>").append(r.lama)
             .append("</td><td>").append(esc(r.penjab)).append("</td></tr>");
        }
        if (n == 0) s.append("<tr><td colspan='7'><i>- kosong -</i></td></tr>");
        s.append("</table>");
        return s.toString();
    }

    /** P=Pulang(sembuh/membaik/sehat), A=APS/Paksa, L=Lain(rujuk/dll). */
    private String pal(String stts) {
        if (stts == null) return "-";
        String s = stts.toLowerCase();
        if (s.contains("sembuh") || s.contains("membaik") || s.contains("sehat")) return "P";
        if (s.contains("aps") || s.contains("paksa") || s.contains("permintaan")) return "A";
        return "L";
    }

    // ==================== IKHTISAR MATRIKS ====================

    private String ikhtisar(List<Row> kemarin, List<Row> masuk, List<Row> pindahan,
                            List<Row> keluar, List<Row> dipindah, List<Row> meninggal, List<Row> sisa) {
        // Kumpulkan kolom kelas & spesialis (dinamis, urut).
        LinkedHashSet<String> kelasSet = new LinkedHashSet<>();
        LinkedHashSet<String> spesSet = new LinkedHashSet<>();
        for (List<Row> L : new List[]{kemarin, masuk, pindahan, keluar, dipindah, meninggal, sisa}) {
            for (Row r : L) {
                if (r.kelas != null && !r.kelas.equals("-")) kelasSet.add(r.kelas);
                if (r.bag != null && !r.bag.equals("-")) spesSet.add(r.bag);
            }
        }
        List<String> kelasCol = new ArrayList<>(kelasSet);
        List<String> spesCol = new ArrayList<>(spesSet);

        StringBuilder s = new StringBuilder();
        s.append("<br><div style='text-align:center;'><b>IKHTISAR SELAMA 24 JAM</b></div>");
        s.append("<table width='100%' border='1' cellspacing='0' cellpadding='2' style='border-collapse:collapse;font-size:10px;'>");
        // header
        s.append("<tr bgcolor='#f0f0f0'><td>Rekapitulasi</td>");
        for (String k : kelasCol) s.append("<td>").append(esc(k)).append("</td>");
        s.append("<td><b>Jumlah</b></td>");
        for (String sp : spesCol) s.append("<td>").append(esc(sp)).append("</td>");
        s.append("</tr>");

        int[] j1 = new int[1], j2 = new int[1];
        s.append(barisRekap("OS Kemarin", kemarin, kelasCol, spesCol, null));
        s.append(barisRekap("OS Masuk", masuk, kelasCol, spesCol, null));
        s.append(barisRekap("OS Pindahan", pindahan, kelasCol, spesCol, null));
        List<Row> jml1 = concat(kemarin, masuk, pindahan);
        s.append(barisRekap("<b>Jumlah - 1</b>", jml1, kelasCol, spesCol, "#eef7ee"));
        s.append(barisRekap("OS Keluar", keluar, kelasCol, spesCol, null));
        s.append(barisRekap("OS Dipindahkan", dipindah, kelasCol, spesCol, null));
        s.append(barisRekap("OS Meninggal", meninggal, kelasCol, spesCol, null));
        List<Row> jml2 = concat(keluar, dipindah, meninggal);
        s.append(barisRekap("<b>Jumlah - 2</b>", jml2, kelasCol, spesCol, "#f7eeee"));
        s.append(barisRekap("<b>Sisa OS masih dirawat</b>", sisa, kelasCol, spesCol, "#eeeef7"));
        s.append("</table>");
        s.append("<div style='font-size:9px;'><i>Sisa OS = Jumlah-1 &minus; Jumlah-2 (dihitung realtime dari kamar_inap).</i></div>");
        return s.toString();
    }

    private String barisRekap(String label, List<Row> rows, List<String> kelasCol, List<String> spesCol, String bg) {
        Map<String, Integer> perKelas = new TreeMap<>();
        Map<String, Integer> perSpes = new TreeMap<>();
        for (Row r : rows) {
            perKelas.merge(r.kelas, 1, Integer::sum);
            perSpes.merge(r.bag, 1, Integer::sum);
        }
        StringBuilder s = new StringBuilder();
        s.append("<tr").append(bg == null ? "" : " bgcolor='" + bg + "'").append("><td>").append(label).append("</td>");
        for (String k : kelasCol) s.append("<td align='center'>").append(nz(perKelas.get(k))).append("</td>");
        s.append("<td align='center'><b>").append(rows.size()).append("</b></td>");
        for (String sp : spesCol) s.append("<td align='center'>").append(nz(perSpes.get(sp))).append("</td>");
        s.append("</tr>");
        return s.toString();
    }

    // ==================== FOOTER BOR ====================

    private String footer(List<Row> sisa, List<Row> masuk, List<Row> pindahan, List<Row> keluar,
                          List<Row> dipindah, List<Row> meninggal, int kapasitas) {
        int sisaL = 0, sisaP = 0;
        for (Row r : sisa) { if ("L".equalsIgnoreCase(r.jk)) sisaL++; else sisaP++; }
        int sisaT = sisa.size();
        int pm = masuk.size() + pindahan.size();
        int pk = keluar.size() + dipindah.size();
        int mati = meninggal.size();
        // HP (Hari Perawatan) ~ pasien yang sempat dirawat hari ini yang dihitung
        // sebagai hari-rawat: sisa akhir + yang keluar/mati/dipindah hari ini.
        int hp = sisaT + keluar.size() + dipindah.size() + meninggal.size();
        // LO (Lama Opname / total lama dirawat pasien keluar+mati hari ini).
        int lo = 0;
        for (Row r : keluar) lo += r.lama;
        for (Row r : meninggal) lo += r.lama;

        StringBuilder s = new StringBuilder();
        s.append("<br><table width='100%' border='1' cellspacing='0' cellpadding='3' style='border-collapse:collapse;font-size:10px;'>");
        s.append("<tr bgcolor='#f0f0f0'><td>JK</td><td>Jumlah Pasien</td><td>BOR Harian</td><td>PM</td><td>PK</td><td>Mati</td><td>HP</td><td>LO</td></tr>");
        s.append(barisFooter("L", sisaL, kapasitas));
        s.append(barisFooter("P", sisaP, kapasitas));
        // total pakai angka agregat
        s.append("<tr bgcolor='#eef'><td><b>Total</b></td><td align='center'><b>").append(sisaT)
         .append("</b></td><td align='center'><b>").append(bor(sisaT, kapasitas))
         .append("</b></td><td align='center'>").append(pm).append("</td><td align='center'>").append(pk)
         .append("</td><td align='center'>").append(mati).append("</td><td align='center'>").append(hp)
         .append("</td><td align='center'>").append(lo).append("</td></tr>");
        s.append("</table>");
        s.append("<div style='font-size:9px;'><i>BOR Harian = Sisa OS / Kapasitas T.T &times; 100%. ")
         .append("PM=Pasien Masuk (masuk+pindahan), PK=Pasien Keluar (keluar+dipindahkan), ")
         .append("HP=Hari Perawatan, LO=total Lama Opname pasien keluar/mati. ")
         .append("(Formula HP/LO bisa disesuaikan aturan RS.)</i></div>");
        return s.toString();
    }

    private String barisFooter(String jk, int jml, int kapasitas) {
        return "<tr><td>" + jk + "</td><td align='center'>" + jml + "</td><td align='center'>"
             + bor(jml, kapasitas) + "</td><td colspan='5'></td></tr>";
    }

    private String bor(int jml, int kapasitas) {
        if (kapasitas <= 0) return "-";
        double b = jml * 100.0 / kapasitas;
        return String.format("%.2f%%", b);
    }

    // ==================== CETAK ====================

    /** Cetak = lempar ke file HTML lalu buka di browser (paginasi & layout jauh
     *  lebih rapi; user tinggal Ctrl+P pilih Landscape). */
    private void cetak() {
        try {
            String doc = "<!DOCTYPE html><html><head><meta charset='utf-8'>"
                + "<title>Sensus Harian Rawat Inap</title><style>"
                + "body{font-family:Arial;font-size:12px;margin:10px;}"
                + "table{border-collapse:collapse;}"
                + "hr{border:0;border-top:1px solid #999;}"
                + "@media print{@page{size:A4 landscape;margin:8mm;} .noprint{display:none;}}"
                + ".bar{text-align:center;margin:6px 0;}"
                + ".bar button{padding:4px 14px;font-size:13px;cursor:pointer;}"
                + "</style></head><body>"
                + "<div class='bar noprint'><button onclick='window.print()'>&#128424; Cetak (Ctrl+P) — pilih Landscape</button></div>"
                + bodyHtml(false)
                + "</body></html>";

            java.io.File dir = new java.io.File(System.getProperty("java.io.tmpdir"), "khanza_cetak");
            dir.mkdirs();
            java.io.File f = new java.io.File(dir, "SensusHarianRanap.html");
            try (java.io.Writer w = new java.io.OutputStreamWriter(
                    new java.io.FileOutputStream(f), "UTF-8")) {
                w.write(doc);
            }
            bukaBrowser(f);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal membuat cetakan HTML: " + ex);
            System.out.println("Cetak sensus ranap gagal: " + ex);
        }
    }

    private void bukaBrowser(java.io.File f) {
        try {
            if (java.awt.Desktop.isDesktopSupported()
                    && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.BROWSE)) {
                java.awt.Desktop.getDesktop().browse(f.toURI());
                return;
            }
        } catch (Exception ignore) { }
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                Runtime.getRuntime().exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", f.getAbsolutePath()});
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec(new String[]{"open", f.getAbsolutePath()});
            } else {
                Runtime.getRuntime().exec(new String[]{"xdg-open", f.getAbsolutePath()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Silakan buka manual file cetak:\n" + f.getAbsolutePath());
        }
    }

    // ==================== UTIL ====================

    @SafeVarargs
    private final List<Row> concat(List<Row>... lists) {
        List<Row> out = new ArrayList<>();
        for (List<Row> l : lists) out.addAll(l);
        return out;
    }

    private int nz(Integer i) { return i == null ? 0 : i; }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
