/*
 * AnalisaVENABCObat.java
 *
 * Modul Analisis VEN-ABC & Perencanaan Pengadaan Obat (Metode Konsumsi).
 * Menghitung otomatis dari database SIMRS:
 *   - Pemakaian (CT)  : SUM(detail_pemberian_obat.jml) pada periode terpilih
 *   - Stok (S1)       : SUM(gudangbarang.stok)
 *   - Harga satuan    : databarang.h_beli
 * Lalu menghitung Analisis ABC (nilai pakai), VEN (input manual, disimpan di
 * tabel ven_obat), VEN-ABC + skor prioritas, serta perencanaan pengadaan
 * (rata-rata pemakaian, safety stock, re-order point, qty to order, warning).
 *
 * Dasar teori: lihat Request/Dokumentasi_Dasar_Teori_Analisis_VEN-ABC_Obat.md
 */
package inventory;

import fungsi.koneksiDB;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public final class AnalisaVENABCObat extends JDialog {

    private final Connection koneksi = koneksiDB.condb();
    private final DecimalFormat df0 = new DecimalFormat("#,##0");
    private final DecimalFormat df1 = new DecimalFormat("#,##0.0");
    private final DecimalFormat df2 = new DecimalFormat("#,##0.00");

    private DefaultTableModel model;
    private JTable tabel;
    private TableRowSorter<DefaultTableModel> sorter;
    private widget.Tanggal DTPAwal, DTPAkhir;
    private JTextField tBulan, tLead, tPeriode, tThA, tThB, tCari;
    private JComboBox<JenisItem> cbJenis;
    private JLabel lblSummary;

    private static final String[] KOLOM = {
        "No", "Kode", "Nama Obat", "Satuan", "Stok", "Pemakaian",
        "Harga", "Nilai Pakai", "%", "% Kum", "ABC", "VEN", "VEN-ABC",
        "Prioritas", "Rata2/bln", "Safety Stock", "RoP", "Qty Order", "Warning"
    };
    private static final int COL_VEN = 11;

    public AnalisaVENABCObat(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Analisis VEN-ABC & Perencanaan Pengadaan Obat");
        initUI();
        loadJenis();
        setDefaultPeriode();
    }

    // ================= UI =================
    private void initUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        top.setBorder(BorderFactory.createTitledBorder("Parameter Analisis"));
        DTPAwal = new widget.Tanggal();
        DTPAkhir = new widget.Tanggal();
        DTPAwal.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        DTPAkhir.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        tBulan = field(3);
        tLead = field(3);
        tPeriode = field(3);
        tThA = field(4);
        tThB = field(4);
        tBulan.setText("6");
        tLead.setText("2");
        tPeriode.setText("3");
        tThA.setText("80");
        tThB.setText("95");
        cbJenis = new JComboBox<>();
        cbJenis.setPreferredSize(new Dimension(170, 24));

        top.add(new JLabel("Tgl:"));
        top.add(DTPAwal);
        top.add(new JLabel("s/d"));
        top.add(DTPAkhir);
        top.add(new JLabel("Bulan:"));
        top.add(tBulan);
        top.add(new JLabel("Jenis:"));
        top.add(cbJenis);
        top.add(new JLabel("Lead(bln):"));
        top.add(tLead);
        top.add(new JLabel("Pengadaan(bln):"));
        top.add(tPeriode);
        top.add(new JLabel("Batas A%:"));
        top.add(tThA);
        top.add(new JLabel("B%:"));
        top.add(tThB);
        JButton btnProses = new JButton("Proses");
        btnProses.addActionListener(e -> proses());
        top.add(btnProses);

        model = new DefaultTableModel(null, KOLOM) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == COL_VEN;
            }
        };
        tabel = new JTable(model);
        tabel.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabel.getTableHeader().setReorderingAllowed(false);
        sorter = new TableRowSorter<>(model);
        tabel.setRowSorter(sorter);
        setupKolom();
        setupRenderer();

        // editor VEN
        JComboBox<String> edVen = new JComboBox<>(new String[]{"", "V", "E", "N"});
        tabel.getColumnModel().getColumn(COL_VEN).setCellEditor(new DefaultCellEditor(edVen));
        model.addTableModelListener(e -> {
            if (e.getColumn() == COL_VEN && e.getFirstRow() >= 0) {
                recalcRow(e.getFirstRow());
                refreshSummary();
            }
        });

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        tCari = field(20);
        tCari.addCaretListener(e -> filter());
        JButton btnSimpanVen = new JButton("Simpan VEN");
        btnSimpanVen.addActionListener(e -> simpanVen());
        JButton btnExport = new JButton("Export CSV");
        btnExport.addActionListener(e -> exportCsv());
        lblSummary = new JLabel(" ");
        bottom.add(new JLabel("Cari:"));
        bottom.add(tCari);
        bottom.add(btnSimpanVen);
        bottom.add(btnExport);
        bottom.add(new JLabel("   "));
        bottom.add(lblSummary);

        // Filter & aksi ditaruh di panel BAWAH; tabel mengisi tengah.
        JPanel south = new JPanel(new BorderLayout());
        south.add(top, BorderLayout.NORTH);
        south.add(bottom, BorderLayout.SOUTH);
        setLayout(new BorderLayout());
        add(new JScrollPane(tabel), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
        setSize(1180, 640);
    }

    private JTextField field(int cols) {
        JTextField f = new JTextField(cols);
        return f;
    }

    private void setupKolom() {
        int[] w = {38, 95, 220, 60, 60, 75, 80, 95, 55, 55, 40, 40, 60, 60, 75, 85, 70, 80, 65};
        for (int i = 0; i < w.length && i < tabel.getColumnCount(); i++) {
            tabel.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        }
    }

    private void setupRenderer() {
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int c : new int[]{4, 5, 6, 7, 8, 9, 14, 15, 16, 17}) {
            tabel.getColumnModel().getColumn(c).setCellRenderer(right);
        }
        // Warning + ABC + prioritas: center + warna
        DefaultTableCellRenderer warn = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                    boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!sel) {
                    c.setBackground("YES".equals(String.valueOf(v)) ? new Color(255, 205, 205) : Color.WHITE);
                }
                return c;
            }
        };
        tabel.getColumnModel().getColumn(18).setCellRenderer(warn);
        for (int c : new int[]{10, 12, 13}) {
            tabel.getColumnModel().getColumn(c).setCellRenderer(center);
        }
    }

    // ================= Data =================
    private static class JenisItem {
        final String kode, nama;
        JenisItem(String k, String n) { kode = k; nama = n; }
        @Override public String toString() { return nama; }
    }

    private void loadJenis() {
        cbJenis.addItem(new JenisItem("", "-- Semua Barang --"));
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select kdjns, nama from jenis order by nama")) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String kode = rs.getString(1);
                    String nm = rs.getString(2);
                    // tampilkan hanya jenis yang punya NAMA (lebih mudah dipahami),
                    // jenis tanpa nama/"-" dilewati (tetap tercakup di "Semua Barang").
                    if (nm == null || nm.trim().isEmpty() || "-".equals(nm.trim())) continue;
                    cbJenis.addItem(new JenisItem(kode, nm.trim()));
                }
            }
        } catch (Exception ex) {
            System.out.println("loadJenis err: " + ex);
        }
    }

    private void setDefaultPeriode() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        DTPAkhir.setDate(c.getTime());
        c.add(Calendar.MONTH, -6);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        DTPAwal.setDate(c.getTime());
    }

    private static class Row {
        String kode, nama, satuan, ven;
        double hbeli, stok, ct, nilai, pct, cum, ca, ss, rop, qo;
        String abc, venabc, warning;
        int prioritas;
    }

    private void proses() {
        if (DTPAwal.getDate() == null || DTPAkhir.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Tanggal awal/akhir belum diisi.");
            return;
        }
        SimpleDateFormat fd = new SimpleDateFormat("yyyy-MM-dd");
        String awal = fd.format(DTPAwal.getDate()) + " 00:00:00";
        String akhir = fd.format(DTPAkhir.getDate()) + " 23:59:59";
        double bulan = parseD(tBulan.getText(), 6);
        if (bulan <= 0) bulan = 6;
        JenisItem ji = (JenisItem) cbJenis.getSelectedItem();
        String kdjns = ji == null ? "" : ji.kode;

        List<Row> rows = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "select b.kode_brng, b.nama_brng, coalesce(sat.satuan,'') satuan, "
                + " coalesce(b.h_beli,0) hbeli, "
                + " coalesce((select sum(g.stok) from gudangbarang g where g.kode_brng=b.kode_brng),0) stok, "
                + " coalesce((select sum(d.jml) from detail_pemberian_obat d "
                + "    where d.kode_brng=b.kode_brng and d.tgl_perawatan between ? and ?),0) ct, "
                + " coalesce(v.ven,'') ven "
                + "from databarang b "
                + "left join kodesatuan sat on sat.kode_sat=b.kode_sat "
                + "left join ven_obat v on v.kode_brng=b.kode_brng "
                + "where b.status='1' ");
        if (!kdjns.isEmpty()) sql.append("and b.kdjns=? ");
        sql.append("order by b.nama_brng");

        try (PreparedStatement ps = koneksi.prepareStatement(sql.toString())) {
            ps.setString(1, awal);
            ps.setString(2, akhir);
            if (!kdjns.isEmpty()) ps.setString(3, kdjns);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Row r = new Row();
                    r.kode = rs.getString(1);
                    r.nama = rs.getString(2);
                    r.satuan = rs.getString(3);
                    r.hbeli = rs.getDouble(4);
                    r.stok = rs.getDouble(5);
                    r.ct = rs.getDouble(6);
                    r.ven = rs.getString(7);
                    r.nilai = r.ct * r.hbeli;
                    rows.add(r);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data:\n" + ex.getMessage());
            return;
        }
        hitung(rows, bulan);
        tampilkan(rows);
    }

    /** Hitung ABC, VEN-ABC, prioritas, dan perencanaan pengadaan. */
    private void hitung(List<Row> rows, double bulan) {
        double lead = parseD(tLead.getText(), 2);
        double periode = parseD(tPeriode.getText(), 3);
        double thA = parseD(tThA.getText(), 80) / 100.0;
        double thB = parseD(tThB.getText(), 95) / 100.0;

        double total = 0;
        for (Row r : rows) total += r.nilai;
        // urut menurun berdasarkan nilai pakai (ABC)
        rows.sort((a, b) -> Double.compare(b.nilai, a.nilai));
        double cum = 0;
        for (Row r : rows) {
            r.pct = total > 0 ? r.nilai / total : 0;
            cum += r.pct;
            r.cum = cum;
            r.abc = cum <= thA ? "A" : (cum <= thB ? "B" : "C");
            // perencanaan pengadaan (metode konsumsi)
            r.ca = bulan > 0 ? r.ct / bulan : 0;
            r.ss = r.ca * lead;
            r.rop = (r.ca * lead) + r.ss;
            r.qo = (r.ca * (lead + periode)) + r.ss - r.stok;
            if (r.qo < 0) r.qo = 0;
            r.warning = (r.ct > 0 && r.stok <= r.rop) ? "YES" : "NO";
            setVenAbc(r);
        }
    }

    private void setVenAbc(Row r) {
        if (r.ven == null || r.ven.isEmpty()) {
            r.venabc = r.abc;
            r.prioritas = 0;
        } else {
            r.venabc = r.ven + r.abc;
            r.prioritas = prioritas(r.venabc);
        }
    }

    private int prioritas(String va) {
        switch (va) {
            case "VA": return 9;
            case "VB": return 8;
            case "EA": return 7;
            case "VC": return 6;
            case "EB": return 5;
            case "NA": return 4;
            case "EC": return 3;
            case "NB": return 2;
            case "NC": return 1;
            default: return 0;
        }
    }

    private void tampilkan(List<Row> rows) {
        model.setRowCount(0);
        int no = 1;
        for (Row r : rows) {
            model.addRow(new Object[]{
                no++, r.kode, r.nama, r.satuan,
                df0.format(r.stok), df0.format(r.ct), df0.format(r.hbeli),
                df0.format(r.nilai), df2.format(r.pct * 100), df2.format(r.cum * 100),
                r.abc, r.ven == null ? "" : r.ven, r.venabc,
                r.prioritas == 0 ? "" : String.valueOf(r.prioritas),
                df1.format(r.ca), df1.format(r.ss), df1.format(r.rop), df1.format(r.qo),
                r.warning
            });
        }
        refreshSummary();
    }

    /** Re-hitung VEN-ABC & prioritas satu baris saat VEN diedit. */
    private void recalcRow(int viewOrModelRow) {
        int row = viewOrModelRow;
        String ven = String.valueOf(model.getValueAt(row, COL_VEN)).trim().toUpperCase();
        String abc = String.valueOf(model.getValueAt(row, 10));
        String venabc;
        int prio;
        if (ven.isEmpty()) {
            venabc = abc;
            prio = 0;
        } else {
            venabc = ven + abc;
            prio = prioritas(venabc);
        }
        model.setValueAt(ven, row, COL_VEN);
        model.setValueAt(venabc, row, 12);
        model.setValueAt(prio == 0 ? "" : String.valueOf(prio), row, 13);
    }

    private void refreshSummary() {
        int a = 0, b = 0, c = 0, warn = 0, n = model.getRowCount();
        for (int i = 0; i < n; i++) {
            String abc = String.valueOf(model.getValueAt(i, 10));
            if ("A".equals(abc)) a++;
            else if ("B".equals(abc)) b++;
            else if ("C".equals(abc)) c++;
            if ("YES".equals(String.valueOf(model.getValueAt(i, 18)))) warn++;
        }
        lblSummary.setText(String.format(
                "Total item: %d   |   Kelas A: %d   B: %d   C: %d   |   Perlu pesan (Warning): %d",
                n, a, b, c, warn));
        lblSummary.setFont(lblSummary.getFont().deriveFont(Font.BOLD));
    }

    private void filter() {
        String q = tCari.getText().trim();
        if (q.isEmpty()) sorter.setRowFilter(null);
        else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(q), 1, 2));
    }

    private void simpanVen() {
        int n = model.getRowCount(), ok = 0;
        try {
            for (int i = 0; i < n; i++) {
                String kode = String.valueOf(model.getValueAt(i, 1));
                String ven = String.valueOf(model.getValueAt(i, COL_VEN)).trim().toUpperCase();
                if (ven.isEmpty()) {
                    try (PreparedStatement ps = koneksi.prepareStatement(
                            "delete from ven_obat where kode_brng=?")) {
                        ps.setString(1, kode);
                        ps.executeUpdate();
                    }
                } else if (ven.equals("V") || ven.equals("E") || ven.equals("N")) {
                    try (PreparedStatement ps = koneksi.prepareStatement(
                            "insert into ven_obat (kode_brng, ven) values (?,?) "
                            + "on duplicate key update ven=values(ven)")) {
                        ps.setString(1, kode);
                        ps.setString(2, ven);
                        ps.executeUpdate();
                    }
                    ok++;
                }
            }
            JOptionPane.showMessageDialog(this, "VEN tersimpan (" + ok + " obat berkategori).");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal simpan VEN:\n" + ex.getMessage());
        }
    }

    private void exportCsv() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Belum ada data. Klik Proses dulu.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("Analisis_VEN-ABC_Obat.csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try (FileWriter w = new FileWriter(fc.getSelectedFile())) {
            for (int c = 0; c < model.getColumnCount(); c++) {
                w.write((c > 0 ? "," : "") + "\"" + model.getColumnName(c) + "\"");
            }
            w.write("\n");
            for (int r = 0; r < model.getRowCount(); r++) {
                for (int c = 0; c < model.getColumnCount(); c++) {
                    String v = String.valueOf(model.getValueAt(r, c));
                    w.write((c > 0 ? "," : "") + "\"" + v.replace("\"", "\"\"") + "\"");
                }
                w.write("\n");
            }
            JOptionPane.showMessageDialog(this, "Export selesai:\n" + fc.getSelectedFile().getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal export:\n" + ex.getMessage());
        }
    }

    private double parseD(String s, double def) {
        try {
            return Double.parseDouble(s.trim().replace(",", "."));
        } catch (Exception e) {
            return def;
        }
    }
}
