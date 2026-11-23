/*
 * PemakaianObatPerDepo.java
 *
 * Laporan pemakaian (pengeluaran) obat PER HARI PER DEPO, menggabungkan
 * seluruh jalur pengeluaran obat yang tersebar di beberapa tabel:
 *   1. Pemberian ke pasien Ralan/Ranap : detail_pemberian_obat
 *   2. Resep pulang                    : resep_pulang
 *   3. Penjualan bebas (tunai)         : detailjual  x penjualan (header)
 *   4. Obat piutang (kredit)           : detailpiutang x piutang (header)
 *
 * detailjual & detailpiutang tidak menyimpan tgl/depo, sehingga di-join ke
 * header penjualan/piutang (tgl_jual/tgl_piutang + kd_bangsal).
 *
 * Catatan: racikan (detail_obat_racikan, tanpa kolom jml) & obat operasi
 * (beri_obat_operasi, tanpa depo) belum diikutkan - bisa ditambah bila perlu.
 */
package inventory;

import fungsi.koneksiDB;
import fungsi.validasi;
import fungsi.akses;
import fungsi.sekuel;
import java.util.HashMap;
import java.util.Map;
import java.awt.BorderLayout;
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
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public final class PemakaianObatPerDepo extends JDialog {

    private final Connection koneksi = koneksiDB.condb();
    private final DecimalFormat df0 = new DecimalFormat("#,##0.###");
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();

    private DefaultTableModel model;
    private JTable tabel;
    private TableRowSorter<DefaultTableModel> sorter;
    private widget.Tanggal DTPAwal, DTPAkhir;
    private JComboBox<DepoItem> cbDepo;
    private JComboBox<String> cbSumber;
    private javax.swing.JTextField tCari;
    private JLabel lblSummary;

    private static final String[] KOLOM = {
        "No", "Tanggal", "Kd Depo", "Nama Depo", "Kode Obat", "Nama Obat", "Satuan", "Sumber", "Qty"
    };

    public PemakaianObatPerDepo(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Pemakaian Obat per Hari per Depo");
        initUI();
        loadDepo();
        setDefaultPeriode();
    }

    private void initUI() {
        model = new DefaultTableModel(null, KOLOM) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tabel = new JTable(model);
        tabel.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabel.getTableHeader().setReorderingAllowed(false);
        sorter = new TableRowSorter<>(model);
        tabel.setRowSorter(sorter);
        int[] w = {40, 90, 60, 150, 95, 240, 60, 130, 70};
        for (int i = 0; i < w.length; i++) {
            tabel.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        }
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        tabel.getColumnModel().getColumn(8).setCellRenderer(right);

        // ---- strip TOTAL (tepat di bawah data) ----
        JPanel totalStrip = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 2));
        lblSummary = new JLabel(" ");
        lblSummary.setFont(lblSummary.getFont().deriveFont(Font.BOLD, 13f));
        totalStrip.add(lblSummary);

        // ---- filter tanggal + filter cepat Depo/Sumber/cari ----
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        filter.setBorder(BorderFactory.createTitledBorder("Filter"));
        DTPAwal = new widget.Tanggal();
        DTPAkhir = new widget.Tanggal();
        DTPAwal.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        DTPAkhir.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        tCari = new javax.swing.JTextField(14);
        tCari.addCaretListener(e -> applyFilter());
        cbDepo = new JComboBox<>();
        cbDepo.setPreferredSize(new Dimension(160, 24));
        cbDepo.addActionListener(e -> applyFilter());
        cbSumber = new JComboBox<>(new String[]{
            "Semua Sumber", "Pemberian (Ralan/Ranap)", "Resep Pulang", "Jual Bebas", "Piutang"
        });
        cbSumber.addActionListener(e -> applyFilter());
        filter.add(new JLabel("Tgl:"));
        filter.add(DTPAwal);
        filter.add(new JLabel("s/d"));
        filter.add(DTPAkhir);
        filter.add(new JLabel("Cari obat:"));
        filter.add(tCari);
        filter.add(new JLabel("Depo:"));
        filter.add(cbDepo);
        filter.add(new JLabel("Sumber:"));
        filter.add(cbSumber);

        // ---- bar tombol (widget.Button, konsisten form lain) ----
        widget.panelisi bar = new widget.panelisi();
        bar.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));
        widget.Button BtnProses = new widget.Button();
        BtnProses.setIcon(ic("refresh.png")); BtnProses.setText("Proses"); BtnProses.setMnemonic('P');
        BtnProses.setToolTipText("Alt+P"); BtnProses.setPreferredSize(new Dimension(110, 30));
        BtnProses.addActionListener(e -> proses());
        widget.Button BtnCetak = new widget.Button();
        BtnCetak.setIcon(ic("b_print.png")); BtnCetak.setText("Cetak Jasper"); BtnCetak.setMnemonic('T');
        BtnCetak.setToolTipText("Alt+T"); BtnCetak.setPreferredSize(new Dimension(130, 30));
        BtnCetak.addActionListener(e -> cetakJasper());
        widget.Button BtnCsv = new widget.Button();
        BtnCsv.setIcon(ic("export-excel.png")); BtnCsv.setText("Cetak CSV"); BtnCsv.setMnemonic('C');
        BtnCsv.setToolTipText("Alt+C"); BtnCsv.setPreferredSize(new Dimension(120, 30));
        BtnCsv.addActionListener(e -> exportCsv());
        widget.Button BtnKeluar = new widget.Button();
        BtnKeluar.setIcon(ic("exit.png")); BtnKeluar.setText("Keluar"); BtnKeluar.setMnemonic('K');
        BtnKeluar.setToolTipText("Alt+K"); BtnKeluar.setPreferredSize(new Dimension(110, 30));
        BtnKeluar.addActionListener(e -> dispose());
        bar.add(BtnProses); bar.add(BtnCetak); bar.add(BtnCsv); bar.add(BtnKeluar);

        JPanel south = new JPanel();
        south.setLayout(new javax.swing.BoxLayout(south, javax.swing.BoxLayout.Y_AXIS));
        south.add(totalStrip);
        south.add(filter);
        south.add(bar);

        setLayout(new BorderLayout());
        add(new JScrollPane(tabel), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
        setSize(1100, 620);
    }

    private static class DepoItem {
        final String kode, nama;
        DepoItem(String k, String n) { kode = k; nama = n; }
        @Override public String toString() { return nama; }
    }

    private void loadDepo() {
        cbDepo.addItem(new DepoItem("", "-- Semua Depo --"));
        // hanya depo yang benar-benar muncul di transaksi pengeluaran obat
        String sql = "select kd, nm from ("
                + " select d.kd_bangsal kd, coalesce(b.nm_bangsal,d.kd_bangsal) nm from detail_pemberian_obat d left join bangsal b on b.kd_bangsal=d.kd_bangsal where d.kd_bangsal is not null"
                + " union select rp.kd_bangsal, coalesce(b.nm_bangsal,rp.kd_bangsal) from resep_pulang rp left join bangsal b on b.kd_bangsal=rp.kd_bangsal where rp.kd_bangsal is not null"
                + " union select p.kd_bangsal, coalesce(b.nm_bangsal,p.kd_bangsal) from penjualan p left join bangsal b on b.kd_bangsal=p.kd_bangsal where p.kd_bangsal is not null"
                + " union select pt.kd_bangsal, coalesce(b.nm_bangsal,pt.kd_bangsal) from piutang pt left join bangsal b on b.kd_bangsal=pt.kd_bangsal where pt.kd_bangsal is not null"
                + ") z group by kd order by nm";
        try (PreparedStatement ps = koneksi.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cbDepo.addItem(new DepoItem(rs.getString(1), rs.getString(2)));
            }
        } catch (Exception ex) {
            System.out.println("loadDepo err: " + ex);
        }
    }

    private void setDefaultPeriode() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        DTPAkhir.setDate(c.getTime());
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        DTPAwal.setDate(c.getTime());
    }

    private void proses() {
        if (DTPAwal.getDate() == null || DTPAkhir.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Tanggal awal/akhir belum diisi.");
            return;
        }
        SimpleDateFormat fd = new SimpleDateFormat("yyyy-MM-dd");
        String awal = fd.format(DTPAwal.getDate()) + " 00:00:00";
        String akhir = fd.format(DTPAkhir.getDate()) + " 23:59:59";

        String union = "("
                + " select tgl_perawatan tgl, kd_bangsal, kode_brng, 'Pemberian (Ralan/Ranap)' sumber, jml"
                + "   from detail_pemberian_obat where tgl_perawatan between ? and ?"
                + " union all"
                + " select tanggal, kd_bangsal, kode_brng, 'Resep Pulang', jml_barang"
                + "   from resep_pulang where tanggal between ? and ?"
                + " union all"
                + " select p.tgl_jual, p.kd_bangsal, dj.kode_brng, 'Jual Bebas', dj.jumlah"
                + "   from detailjual dj join penjualan p on p.nota_jual=dj.nota_jual"
                + "   where p.tgl_jual between ? and ?"
                + " union all"
                + " select pt.tgl_piutang, pt.kd_bangsal, dp.kode_brng, 'Piutang', dp.jumlah"
                + "   from detailpiutang dp join piutang pt on pt.nota_piutang=dp.nota_piutang"
                + "   where pt.tgl_piutang between ? and ?"
                + ") x";

        StringBuilder sql = new StringBuilder()
                .append("select date(x.tgl) tanggal, coalesce(nullif(x.kd_bangsal,''),'-') depo, ")
                .append(" coalesce(b2.nm_bangsal,'') nm_depo, x.kode_brng, coalesce(db.nama_brng,'') nama, ")
                .append(" coalesce(sat.satuan,'') satuan, x.sumber, sum(x.jml) qty ")
                .append("from ").append(union)
                .append(" left join databarang db on db.kode_brng=x.kode_brng")
                .append(" left join kodesatuan sat on sat.kode_sat=db.kode_sat")
                .append(" left join bangsal b2 on b2.kd_bangsal=x.kd_bangsal");
        sql.append(" group by date(x.tgl), x.kd_bangsal, x.kode_brng, x.sumber")
           .append(" order by tanggal desc, depo, nama");

        model.setRowCount(0);
        int no = 1;
        double totalQty = 0;
        try (PreparedStatement ps = koneksi.prepareStatement(sql.toString())) {
            int p = 1;
            for (int i = 0; i < 4; i++) {
                ps.setString(p++, awal);
                ps.setString(p++, akhir);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    double qty = rs.getDouble("qty");
                    totalQty += qty;
                    model.addRow(new Object[]{
                        no++, rs.getString("tanggal"), rs.getString("depo"),
                        rs.getString("nm_depo"), rs.getString("kode_brng"), rs.getString("nama"),
                        rs.getString("satuan"), rs.getString("sumber"), df0.format(qty)
                    });
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal mengambil data:\n" + ex.getMessage());
            return;
        }
        applyFilter();
    }

    private void applyFilter() {
        if (sorter == null) return;
        String q = tCari.getText().trim();
        String sumber = cbSumber.getSelectedIndex() == 0 ? "" : String.valueOf(cbSumber.getSelectedItem());
        DepoItem depo = (DepoItem) cbDepo.getSelectedItem();
        String kdDepo = depo == null ? "" : depo.kode;
        java.util.List<RowFilter<Object, Object>> fs = new ArrayList<>();
        if (!q.isEmpty()) {
            fs.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(q), 4, 5));
        }
        if (!sumber.isEmpty()) {
            fs.add(RowFilter.regexFilter("^" + java.util.regex.Pattern.quote(sumber) + "$", 7));
        }
        if (!kdDepo.isEmpty()) {
            fs.add(RowFilter.regexFilter("^" + java.util.regex.Pattern.quote(kdDepo) + "$", 2));
        }
        sorter.setRowFilter(fs.isEmpty() ? null : RowFilter.andFilter(fs));
        updateTotal();
    }

    /** Jumlahkan kolom Qty atas baris yang sedang TAMPIL (terfilter). */
    private void updateTotal() {
        double sum = 0;
        int rows = tabel.getRowCount();
        for (int i = 0; i < rows; i++) {
            try { sum += df0.parse(String.valueOf(tabel.getValueAt(i, 8))).doubleValue(); } catch (Exception ignore) {}
        }
        lblSummary.setText("Baris tampil: " + rows + "      Total Qty: " + df0.format(sum));
    }

    private javax.swing.Icon ic(String name) {
        java.net.URL u = getClass().getResource("/picture/" + name);
        return u == null ? null : new javax.swing.ImageIcon(u);
    }

    private void cetakJasper() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Belum ada data. Klik Proses dulu.");
            return;
        }
        SimpleDateFormat fd = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat fd2 = new SimpleDateFormat("dd-MM-yyyy");
        String awal = fd.format(DTPAwal.getDate()) + " 00:00:00";
        String akhir = fd.format(DTPAkhir.getDate()) + " 23:59:59";
        String union = "("
                + " select tgl_perawatan tgl, kd_bangsal, kode_brng, 'Pemberian (Ralan/Ranap)' sumber, jml from detail_pemberian_obat where tgl_perawatan between '" + awal + "' and '" + akhir + "'"
                + " union all select tanggal, kd_bangsal, kode_brng, 'Resep Pulang', jml_barang from resep_pulang where tanggal between '" + awal + "' and '" + akhir + "'"
                + " union all select p.tgl_jual, p.kd_bangsal, dj.kode_brng, 'Jual Bebas', dj.jumlah from detailjual dj join penjualan p on p.nota_jual=dj.nota_jual where p.tgl_jual between '" + awal + "' and '" + akhir + "'"
                + " union all select pt.tgl_piutang, pt.kd_bangsal, dp.kode_brng, 'Piutang', dp.jumlah from detailpiutang dp join piutang pt on pt.nota_piutang=dp.nota_piutang where pt.tgl_piutang between '" + awal + "' and '" + akhir + "'"
                + ") x";
        StringBuilder sql = new StringBuilder()
                .append("select date(x.tgl) tanggal, coalesce(nullif(x.kd_bangsal,''),'-') depo, coalesce(b2.nm_bangsal,'') nm_depo, x.kode_brng, coalesce(db.nama_brng,'') nama, coalesce(sat.satuan,'') satuan, x.sumber, sum(x.jml) qty ")
                .append("from ").append(union)
                .append(" left join databarang db on db.kode_brng=x.kode_brng left join kodesatuan sat on sat.kode_sat=db.kode_sat left join bangsal b2 on b2.kd_bangsal=x.kd_bangsal");
        List<String> wh = new ArrayList<>();
        DepoItem depo = (DepoItem) cbDepo.getSelectedItem();
        if (depo != null && !depo.kode.isEmpty()) wh.add("x.kd_bangsal='" + depo.kode.replace("'", "''") + "'");
        if (cbSumber.getSelectedIndex() > 0) wh.add("x.sumber='" + String.valueOf(cbSumber.getSelectedItem()).replace("'", "''") + "'");
        String cari = tCari.getText().trim().replace("'", "''");
        if (!cari.isEmpty()) wh.add("(db.nama_brng like '%" + cari + "%' or x.kode_brng like '%" + cari + "%')");
        if (!wh.isEmpty()) sql.append(" where ").append(String.join(" and ", wh));
        sql.append(" group by date(x.tgl), x.kd_bangsal, x.kode_brng, x.sumber order by tanggal desc, depo, nama");

        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        param.put("periode", fd2.format(DTPAwal.getDate()) + " s/d " + fd2.format(DTPAkhir.getDate()));
        Valid.MyReportqry("rptPemakaianObatPerDepo.jasper", "report", "::[ Pemakaian Obat per Depo ]::", sql.toString(), param);
    }

    private void exportCsv() {
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Belum ada data. Klik Proses dulu.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("Pemakaian_Obat_per_Depo.csv"));
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
}
