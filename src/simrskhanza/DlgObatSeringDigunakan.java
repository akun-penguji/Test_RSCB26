/*
 * DlgObatSeringDigunakan.java
 *
 * Daftar obat/alkes/BHP yang paling sering terpakai — sort by total qty keluar (DESC).
 * Sumber data: tabel riwayat_barang_medis (posisi = konsumsi pasien).
 *
 * Filter:
 *   - Periode (1 / 3 / 6 bulan atau custom)
 *   - Bangsal (dropdown; kosong = semua bangsal)
 *   - Keyword (kode/nama/jenis)
 *   - Limit (50 / 100 / 200 / 500)
 *
 * Kolom: Rank, Kode, Nama, Satuan, Jenis, Total Keluar, Frekuensi, Last Used, Stok Total.
 */
package simrskhanza;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public final class DlgObatSeringDigunakan extends javax.swing.JDialog {

    private final Connection koneksi = koneksiDB.condb();
    private final DefaultTableModel modelObat;
    private final validasi Valid = new validasi();
    private final sekuel Sequel = new sekuel();

    public DlgObatSeringDigunakan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width, screen.height - 40);
        setLocation(0, 0);

        modelObat = new DefaultTableModel(null,
            new Object[]{"#", "Kode", "Nama Barang", "Jenis", "Sat",
                         "Total Keluar", "Frekuensi", "Terakhir Dipakai", "Stok Saat Ini"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbObat.setModel(modelObat);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 400));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] w = {40, 90, 320, 140, 60, 110, 90, 120, 110};
        for (int i = 0; i < w.length; i++) {
            TableColumn c = tbObat.getColumnModel().getColumn(i);
            c.setPreferredWidth(w[i]);
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TCari.setDocument(new fungsi.batasInput((byte) 100).getKata(TCari));

        loadBangsal();
        tampilData();
    }

    // =================================================================
    // LAYOUT
    // =================================================================
    @SuppressWarnings("unchecked")
    private void initComponents() {
        internalFrame1 = new widget.InternalFrame();
        panelFilter = new widget.panelisi();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        panelBottom = new widget.panelisi();
        panelInfo = new widget.panelisi();

        tgl1 = new widget.Tanggal();
        tgl2 = new widget.Tanggal();

        lblBangsal = new widget.Label();
        cbBangsal = new widget.ComboBox();
        lblLimit = new widget.Label();
        cbLimit = new widget.ComboBox();
        lblKeyword = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        BtnRefresh = new widget.Button();
        BtnExcel = new widget.Button();
        BtnCSV = new widget.Button();
        BtnPDF = new widget.Button();
        BtnJasper = new widget.Button();
        BtnKeluar = new widget.Button();

        lblTotal = new widget.Label();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Obat / Alkes / BHP Sering Digunakan :: Urut qty keluar terbanyak");

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Obat / Alkes / BHP Sering Digunakan ]::",
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        // ---------- Filter panel (akan ditaruh di SOUTH) ----------
        panelFilter.setPreferredSize(new Dimension(44, 48));
        panelFilter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 6));

        widget.Label lblTgl = new widget.Label();
        lblTgl.setText("Tanggal :");
        lblTgl.setPreferredSize(new Dimension(60, 23));
        panelFilter.add(lblTgl);

        tgl1.setDisplayFormat("dd-MM-yyyy");
        tgl1.setDate(java.sql.Timestamp.valueOf(
            java.time.LocalDateTime.now().minusMonths(3)));
        tgl1.setOpaque(false);
        tgl1.setPreferredSize(new Dimension(110, 23));
        panelFilter.add(tgl1);

        widget.Label lblTglSd = new widget.Label();
        lblTglSd.setText("s/d");
        lblTglSd.setPreferredSize(new Dimension(25, 23));
        panelFilter.add(lblTglSd);

        tgl2.setDisplayFormat("dd-MM-yyyy");
        tgl2.setDate(new Date());
        tgl2.setOpaque(false);
        tgl2.setPreferredSize(new Dimension(110, 23));
        panelFilter.add(tgl2);

        lblBangsal.setText("Bangsal :");
        lblBangsal.setPreferredSize(new Dimension(60, 23));
        panelFilter.add(lblBangsal);
        cbBangsal.setPreferredSize(new Dimension(200, 23));
        cbBangsal.addActionListener(e -> tampilData());
        panelFilter.add(cbBangsal);

        lblLimit.setText("Top :");
        lblLimit.setPreferredSize(new Dimension(40, 23));
        panelFilter.add(lblLimit);
        cbLimit.addItem("50");
        cbLimit.addItem("100");
        cbLimit.addItem("200");
        cbLimit.addItem("500");
        cbLimit.setSelectedIndex(1); // default 100
        cbLimit.setPreferredSize(new Dimension(70, 23));
        cbLimit.addActionListener(e -> tampilData());
        panelFilter.add(cbLimit);

        lblKeyword.setText("Kata Kunci :");
        lblKeyword.setPreferredSize(new Dimension(75, 23));
        panelFilter.add(lblKeyword);
        TCari.setPreferredSize(new Dimension(220, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) tampilData();
            }
        });
        panelFilter.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setMnemonic('1');
        BtnCari.setToolTipText("Alt+1 · Cari");
        BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnCari.addActionListener(e -> tampilData());
        panelFilter.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setMnemonic('2');
        BtnAll.setToolTipText("Alt+2 · Reset filter");
        BtnAll.setPreferredSize(new Dimension(28, 23));
        BtnAll.addActionListener(e -> {
            TCari.setText("");
            cbBangsal.setSelectedIndex(0);
            cbLimit.setSelectedIndex(1);
            tgl1.setDate(java.sql.Timestamp.valueOf(
                java.time.LocalDateTime.now().minusMonths(3)));
            tgl2.setDate(new Date());
            tampilData();
        });
        panelFilter.add(BtnAll);

        // ---------- Table (CENTER) ----------
        tbObat.setAutoCreateRowSorter(true);
        tbObat.setToolTipText("Obat dipakai terbanyak berdasarkan SUM(keluar) di riwayat_barang_medis");
        Scroll.setViewportView(tbObat);
        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        // ---------- Bottom (info + tombol) ----------
        panelInfo.setPreferredSize(new Dimension(44, 26));
        panelInfo.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 4));
        lblTotal.setText(" ");
        lblTotal.setPreferredSize(new Dimension(1000, 20));
        panelInfo.add(lblTotal);

        panelBottom.setPreferredSize(new Dimension(44, 36));
        panelBottom.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 6));

        BtnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnRefresh.setMnemonic('R');
        BtnRefresh.setText("Refresh");
        BtnRefresh.setPreferredSize(new Dimension(100, 23));
        BtnRefresh.addActionListener(e -> tampilData());
        panelBottom.add(BtnRefresh);

        BtnExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        BtnExcel.setMnemonic('E');
        BtnExcel.setText("Excel");
        BtnExcel.setToolTipText("Alt+E · Export ke Excel (.xls)");
        BtnExcel.setPreferredSize(new Dimension(100, 23));
        BtnExcel.addActionListener(e -> exportExcel());
        panelBottom.add(BtnExcel);

        BtnCSV.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        BtnCSV.setMnemonic('C');
        BtnCSV.setText("CSV");
        BtnCSV.setToolTipText("Alt+C · Export ke CSV");
        BtnCSV.setPreferredSize(new Dimension(100, 23));
        BtnCSV.addActionListener(e -> exportCSV());
        panelBottom.add(BtnCSV);

        BtnPDF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        BtnPDF.setMnemonic('P');
        BtnPDF.setText("PDF");
        BtnPDF.setToolTipText("Alt+P · Export ke PDF");
        BtnPDF.setPreferredSize(new Dimension(100, 23));
        BtnPDF.addActionListener(e -> cetakPDF());
        panelBottom.add(BtnPDF);

        BtnJasper.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        BtnJasper.setMnemonic('J');
        BtnJasper.setText("Jasper");
        BtnJasper.setToolTipText("Alt+J · Cetak via JasperViewer");
        BtnJasper.setPreferredSize(new Dimension(100, 23));
        BtnJasper.addActionListener(e -> cetakJasper());
        panelBottom.add(BtnJasper);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setPreferredSize(new Dimension(100, 23));
        BtnKeluar.addActionListener(e -> dispose());
        panelBottom.add(BtnKeluar);

        javax.swing.JPanel south = new javax.swing.JPanel(new java.awt.BorderLayout(1, 1));
        south.setOpaque(false);
        south.add(panelFilter, java.awt.BorderLayout.NORTH);
        south.add(panelInfo, java.awt.BorderLayout.CENTER);
        south.add(panelBottom, java.awt.BorderLayout.SOUTH);
        internalFrame1.add(south, java.awt.BorderLayout.SOUTH);

        getContentPane().setLayout(new java.awt.BorderLayout(1, 1));
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
        pack();
    }

    // =================================================================
    // DATA
    // =================================================================
    private void loadBangsal() {
        cbBangsal.removeAllItems();
        cbBangsal.addItem(new BangsalItem("", "-- Semua Bangsal --"));
        String sql = "SELECT kd_bangsal, nm_bangsal FROM bangsal WHERE status='1' "
                   + "AND kd_bangsal <> '-' ORDER BY nm_bangsal";
        try (PreparedStatement p = koneksi.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                cbBangsal.addItem(new BangsalItem(r.getString(1), r.getString(2)));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Gagal muat bangsal", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getTglAwal() {
        return Valid.SetTgl(tgl1.getSelectedItem() + "");
    }

    private String getTglAkhir() {
        return Valid.SetTgl(tgl2.getSelectedItem() + "");
    }

    private int getLimit() {
        try { return Integer.parseInt((String) cbLimit.getSelectedItem()); }
        catch (NumberFormatException ex) { return 100; }
    }

    private String getKdBangsal() {
        Object sel = cbBangsal.getSelectedItem();
        return (sel instanceof BangsalItem) ? ((BangsalItem) sel).kd : "";
    }

    private void tampilData() {
        modelObat.setRowCount(0);
        String tglA = getTglAwal();
        String tglB = getTglAkhir();
        int limit = getLimit();
        String kdB = getKdBangsal();
        String keyword = TCari.getText().trim();

        StringBuilder sql = new StringBuilder(
              "SELECT db.kode_brng, db.nama_brng, IFNULL(j.nama,'-') AS jenis, "
            + "       db.kode_sat, "
            + "       SUM(rbm.keluar) AS total_keluar, "
            + "       COUNT(*)        AS frekuensi, "
            + "       MAX(rbm.tanggal) AS last_used "
            + "FROM riwayat_barang_medis rbm "
            + "INNER JOIN databarang db ON db.kode_brng = rbm.kode_brng AND db.status='1' "
            + "LEFT JOIN jenis j ON j.kdjns = db.kdjns "
            + "WHERE rbm.status = 'Simpan' "
            + "  AND rbm.keluar > 0 "
            + "  AND rbm.posisi IN ('Pemberian Obat','Resep Pulang','Penjualan',"
            +                      "'Stok Pasien Ranap','Pengambilan Medis','Piutang') "
            + "  AND rbm.tanggal BETWEEN ? AND ? ");

        List<Object> params = new ArrayList<>();
        params.add(tglA);
        params.add(tglB);

        if (!kdB.isEmpty()) {
            sql.append("  AND rbm.kd_bangsal = ? ");
            params.add(kdB);
        }
        if (!keyword.isEmpty()) {
            sql.append("  AND (db.kode_brng LIKE ? OR db.nama_brng LIKE ? OR j.nama LIKE ?) ");
            String k = "%" + keyword + "%";
            params.add(k); params.add(k); params.add(k);
        }
        sql.append("GROUP BY db.kode_brng, db.nama_brng, j.nama, db.kode_sat ")
           .append("ORDER BY total_keluar DESC ")
           .append("LIMIT ?");
        params.add(limit);

        this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        long total = 0;
        try (PreparedStatement p = koneksi.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object v = params.get(i);
                if (v instanceof Integer) p.setInt(i + 1, (Integer) v);
                else p.setString(i + 1, String.valueOf(v));
            }
            try (ResultSet r = p.executeQuery()) {
                int rank = 0;
                while (r.next()) {
                    rank++;
                    double tk = r.getDouble("total_keluar");
                    total += tk;
                    String kode = r.getString("kode_brng");
                    double stokNow = getStokSaatIni(kode, kdB);
                    modelObat.addRow(new Object[]{
                        rank, kode, r.getString("nama_brng"),
                        r.getString("jenis"), r.getString("kode_sat"),
                        fmt(tk), r.getInt("frekuensi"),
                        r.getString("last_used"), fmt(stokNow)
                    });
                }
            }
        } catch (SQLException ex) {
            this.setCursor(java.awt.Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Gagal query data", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.setCursor(java.awt.Cursor.getDefaultCursor());

        lblTotal.setText(String.format(
            "<html>Menampilkan <b>%d</b> item teratas · Periode <b>%s s/d %s</b> · "
            + "Bangsal <b>%s</b> · Total qty keluar (agregat tampilan): <b>%s</b></html>",
            modelObat.getRowCount(), tglA, tglB,
            kdB.isEmpty() ? "semua" : kdB,
            fmt(total)));
    }

    private double getStokSaatIni(String kodeBrng, String kdBangsal) {
        String sql = kdBangsal.isEmpty()
            ? "SELECT IFNULL(SUM(stok),0) FROM gudangbarang WHERE kode_brng=?"
            : "SELECT IFNULL(SUM(stok),0) FROM gudangbarang WHERE kode_brng=? AND kd_bangsal=?";
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            p.setString(1, kodeBrng);
            if (!kdBangsal.isEmpty()) p.setString(2, kdBangsal);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) return r.getDouble(1);
            }
        } catch (SQLException ignore) {}
        return 0;
    }

    // =================================================================
    // EXPORT
    // =================================================================
    private boolean cekData() {
        if (modelObat.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "Tidak ada data untuk diexport/cetak.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        return true;
    }

    private String fileBase() {
        return "ObatSeringDigunakan_"
            + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    }

    private static String esc(Object v) {
        if (v == null) return "";
        return v.toString()
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
    }

    private static String csv(Object v) {
        if (v == null) return "";
        return "\"" + v.toString().replace("\"", "\"\"") + "\"";
    }

    private void exportExcel() {
        if (!cekData()) return;
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(fileBase() + ".xls"));
        fc.setFileFilter(new FileNameExtensionFilter("Excel HTML (*.xls)", "xls"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File f = fc.getSelectedFile();
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) {
            bw.write("<html><head><meta charset=\"UTF-8\">");
            bw.write("<style>");
            bw.write("body{font-family:Tahoma;font-size:10px;}");
            bw.write("h2,h3{margin:2px 0;text-align:center;}");
            bw.write("table{border-collapse:collapse;}");
            bw.write("th,td{border:1px solid #555;padding:3px 5px;font-size:10px;}");
            bw.write("th{background:#dfe7f5;}");
            bw.write(".num{text-align:right;}");
            bw.write("</style></head><body>");
            bw.write("<h2>" + esc(akses.getnamars()) + "</h2>");
            bw.write("<div style=\"text-align:center;font-size:10px;\">"
                + esc(akses.getalamatrs()) + ", " + esc(akses.getkabupatenrs())
                + ", " + esc(akses.getpropinsirs()) + "<br>"
                + esc(akses.getkontakrs()) + " · E-mail : " + esc(akses.getemailrs())
                + "</div><br>");
            bw.write("<h3>OBAT / ALKES / BHP SERING DIGUNAKAN</h3>");
            bw.write("<div style=\"text-align:center;font-size:10px;\">Periode "
                + esc(getTglAwal()) + " s/d " + esc(getTglAkhir())
                + " · Bangsal : " + esc(getKdBangsal().isEmpty() ? "Semua" : getKdBangsal())
                + " · Top " + getLimit() + "</div><br>");

            bw.write("<table>");
            bw.write("<tr>");
            for (int c = 0; c < modelObat.getColumnCount(); c++) {
                bw.write("<th>" + esc(modelObat.getColumnName(c)) + "</th>");
            }
            bw.write("</tr>");
            for (int r = 0; r < modelObat.getRowCount(); r++) {
                bw.write("<tr>");
                for (int c = 0; c < modelObat.getColumnCount(); c++) {
                    boolean num = (c == 0 || c == 5 || c == 6 || c == 8);
                    bw.write("<td" + (num ? " class=\"num\"" : "") + ">"
                        + esc(modelObat.getValueAt(r, c)) + "</td>");
                }
                bw.write("</tr>");
            }
            bw.write("</table>");
            bw.write("</body></html>");
        } catch (Exception ex) {
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this, "Gagal export Excel : " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.setCursor(Cursor.getDefaultCursor());
        try { Desktop.getDesktop().open(f); } catch (Exception ignore) {}
        JOptionPane.showMessageDialog(this,
            "Export Excel berhasil :\n" + f.getAbsolutePath(),
            "Sukses", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportCSV() {
        if (!cekData()) return;
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(fileBase() + ".csv"));
        fc.setFileFilter(new FileNameExtensionFilter("CSV (*.csv)", "csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File f = fc.getSelectedFile();
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) {
            pw.print('﻿'); // BOM agar Excel deteksi UTF-8
            // header info
            pw.println(csv(akses.getnamars()));
            pw.println(csv("Obat / Alkes / BHP Sering Digunakan"));
            pw.println(csv("Periode " + getTglAwal() + " s/d " + getTglAkhir()
                + " · Bangsal : " + (getKdBangsal().isEmpty() ? "Semua" : getKdBangsal())
                + " · Top " + getLimit()));
            pw.println();
            // header kolom
            StringBuilder h = new StringBuilder();
            for (int c = 0; c < modelObat.getColumnCount(); c++) {
                if (c > 0) h.append(",");
                h.append(csv(modelObat.getColumnName(c)));
            }
            pw.println(h);
            // baris
            for (int r = 0; r < modelObat.getRowCount(); r++) {
                StringBuilder row = new StringBuilder();
                for (int c = 0; c < modelObat.getColumnCount(); c++) {
                    if (c > 0) row.append(",");
                    row.append(csv(modelObat.getValueAt(r, c)));
                }
                pw.println(row);
            }
        } catch (Exception ex) {
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(this, "Gagal export CSV : " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.setCursor(Cursor.getDefaultCursor());
        JOptionPane.showMessageDialog(this,
            "Export CSV berhasil :\n" + f.getAbsolutePath(),
            "Sukses", JOptionPane.INFORMATION_MESSAGE);
    }

    private Map<String, Object> dumpKeTemporary() {
        // temporary = no + temp1..temp37 (38 kolom)
        Sequel.queryu("delete from temporary where temp37='" + akses.getalamatip() + "'");
        String ip = akses.getalamatip();
        for (int r = 0; r < modelObat.getRowCount(); r++) {
            StringBuilder vals = new StringBuilder();
            vals.append("'").append(r).append("'"); // no
            // temp1..temp9 = isi 9 kolom tabel
            for (int c = 0; c < 9; c++) {
                vals.append(",'").append(sx(modelObat.getValueAt(r, c))).append("'");
            }
            // temp10..temp36 = 27 kolom kosong
            for (int c = 0; c < 27; c++) {
                vals.append(",''");
            }
            // temp37 = IP
            vals.append(",'").append(ip).append("'");
            Sequel.menyimpan("temporary", vals.toString(), "Obat Sering Digunakan");
        }

        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("periode", getTglAwal() + " s/d " + getTglAkhir());
        param.put("bangsal", getKdBangsal().isEmpty() ? "Semua Bangsal" : getKdBangsal());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        return param;
    }

    private static String sx(Object v) {
        if (v == null) return "";
        return v.toString().replace("'", "\\'");
    }

    private void cetakJasper() {
        if (!cekData()) return;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Map<String, Object> param = dumpKeTemporary();
        Valid.MyReportqry("rptObatSeringDigunakan.jasper", "report",
            "::[ Obat / Alkes / BHP Sering Digunakan ]::",
            "select * from temporary where temporary.temp37='" + akses.getalamatip() + "' order by temporary.no",
            param);
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void cetakPDF() {
        if (!cekData()) return;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Map<String, Object> param = dumpKeTemporary();
        Valid.MyReportqrypdf("rptObatSeringDigunakan.jasper", "report",
            "::[ Obat / Alkes / BHP Sering Digunakan ]::",
            "select * from temporary where temporary.temp37='" + akses.getalamatip() + "' order by temporary.no",
            param);
        this.setCursor(Cursor.getDefaultCursor());
    }

    // =================================================================
    // Helpers
    // =================================================================
    private static String fmt(double d) {
        if (d == (long) d) return String.format("%,d", (long) d);
        return String.format("%,.2f", d);
    }

    private static final class BangsalItem {
        final String kd, nm;
        BangsalItem(String kd, String nm) { this.kd = kd; this.nm = nm; }
        @Override public String toString() { return kd.isEmpty() ? nm : (kd + " — " + nm); }
    }

    // =================================================================
    // Components
    // =================================================================
    private widget.InternalFrame internalFrame1;
    private widget.panelisi panelFilter, panelBottom, panelInfo;
    private widget.ScrollPane Scroll;
    private widget.Table tbObat;
    private widget.Tanggal tgl1, tgl2;
    private widget.Label lblBangsal, lblLimit, lblKeyword, lblTotal;
    private widget.ComboBox cbBangsal, cbLimit;
    private widget.TextBox TCari;
    private widget.Button BtnCari, BtnAll, BtnRefresh, BtnExcel, BtnCSV, BtnPDF, BtnJasper, BtnKeluar;

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgObatSeringDigunakan d = new DlgObatSeringDigunakan(null, true);
            d.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            d.setVisible(true);
        });
    }
}
