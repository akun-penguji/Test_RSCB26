/*
 * DlgRiwayatObatPasien.java
 *
 * Riwayat obat/BHP yang benar-benar diserahkan/dipakai pasien.
 * Menampilkan semua pasien yang menerima obat pada rentang tanggal tertentu.
 * Sumber: detail_pemberian_obat (rawat inap & rawat jalan) + detailpiutang (kredit obat).
 * Default tanggal: hari ini.
 */
package simrskhanza;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.util.HashMap;
import java.util.Map;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public final class DlgRiwayatObatPasien extends javax.swing.JDialog {

    private final Connection koneksi = koneksiDB.condb();
    private final DefaultTableModel modelObat;
    private final validasi Valid = new validasi();
    private final sekuel Sequel = new sekuel();
    private String ringkasanInfo = "";

    public DlgRiwayatObatPasien(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width, screen.height - 40);
        setLocation(0, 0);

        modelObat = new DefaultTableModel(null,
            new Object[]{"Tanggal", "Jam", "No RM", "Nama Pasien", "Sumber",
                         "Kode", "Nama Obat", "Qty", "Sat", "Bangsal",
                         "Jenis Rawat", "No.Rawat / Nota"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbObat.setModel(modelObat);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 400));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] w = {90, 70, 80, 220, 110, 90, 260, 70, 50, 80, 90, 170};
        for (int i = 0; i < w.length; i++) {
            TableColumn c = tbObat.getColumnModel().getColumn(i);
            c.setPreferredWidth(w[i]);
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TCari.setDocument(new fungsi.batasInput((byte) 100).getKata(TCari));

        tampilRiwayat(); // load default = hari ini
    }

    // =================================================================
    // LAYOUT
    // =================================================================
    @SuppressWarnings("unchecked")
    private void initComponents() {
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        panelFilter = new widget.panelisi();
        panelInfo = new widget.panelisi();
        panelBottom = new widget.panelisi();
        lblInfo = new widget.Label();
        lblKeyword = new widget.Label();
        TCari = new widget.TextBox();
        tgl1 = new widget.Tanggal();
        tgl2 = new widget.Tanggal();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        BtnRefresh = new widget.Button();
        BtnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Riwayat Obat Pasien :: detail_pemberian_obat + piutang");

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Riwayat Obat / BHP yang Digunakan Pasien ]::",
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        // CENTER — table
        tbObat.setAutoCreateRowSorter(true);
        tbObat.setToolTipText("Riwayat obat/BHP yang dipakai pasien");
        Scroll.setViewportView(tbObat);
        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        // Filter (bawah atas)
        panelFilter.setPreferredSize(new Dimension(44, 44));
        panelFilter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 6));

        widget.Label lblTgl = new widget.Label();
        lblTgl.setText("Tanggal :");
        lblTgl.setPreferredSize(new Dimension(60, 23));
        panelFilter.add(lblTgl);
        tgl1.setDisplayFormat("dd-MM-yyyy");
        tgl1.setDate(new Date()); // default hari ini
        tgl1.setOpaque(false);
        tgl1.setPreferredSize(new Dimension(110, 23));
        panelFilter.add(tgl1);
        widget.Label lblSd = new widget.Label();
        lblSd.setText("s/d");
        lblSd.setPreferredSize(new Dimension(25, 23));
        panelFilter.add(lblSd);
        tgl2.setDisplayFormat("dd-MM-yyyy");
        tgl2.setDate(new Date()); // default hari ini
        tgl2.setOpaque(false);
        tgl2.setPreferredSize(new Dimension(110, 23));
        panelFilter.add(tgl2);

        lblKeyword.setText("Cari (nama pasien / obat / No RM) :");
        lblKeyword.setPreferredSize(new Dimension(205, 23));
        panelFilter.add(lblKeyword);
        TCari.setPreferredSize(new Dimension(260, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) tampilRiwayat();
            }
        });
        panelFilter.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setMnemonic('1');
        BtnCari.setText("Tampilkan");
        BtnCari.setToolTipText("Alt+1");
        BtnCari.setPreferredSize(new Dimension(110, 23));
        BtnCari.addActionListener(e -> tampilRiwayat());
        panelFilter.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnAll.setMnemonic('2');
        BtnAll.setText("Reset");
        BtnAll.setPreferredSize(new Dimension(90, 23));
        BtnAll.addActionListener(e -> {
            TCari.setText("");
            tgl1.setDate(new Date());
            tgl2.setDate(new Date());
            tampilRiwayat();
        });
        panelFilter.add(BtnAll);

        // Info
        panelInfo.setPreferredSize(new Dimension(44, 26));
        panelInfo.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 4));
        lblInfo.setText(" ");
        lblInfo.setPreferredSize(new Dimension(1400, 20));
        panelInfo.add(lblInfo);

        javax.swing.JPanel south = new javax.swing.JPanel(new java.awt.BorderLayout(1, 1));
        south.setOpaque(false);
        south.add(panelFilter, java.awt.BorderLayout.NORTH);
        south.add(panelInfo, java.awt.BorderLayout.CENTER);

        panelBottom.setPreferredSize(new Dimension(44, 36));
        panelBottom.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 6));
        BtnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnRefresh.setText("Refresh");
        BtnRefresh.setPreferredSize(new Dimension(100, 23));
        BtnRefresh.addActionListener(e -> tampilRiwayat());
        panelBottom.add(BtnRefresh);

        BtnCetak = new widget.Button();
        BtnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Print.png")));
        BtnCetak.setMnemonic('C');
        BtnCetak.setText("Cetak");
        BtnCetak.setToolTipText("Alt+C · Cetak daftar riwayat");
        BtnCetak.setPreferredSize(new Dimension(100, 23));
        BtnCetak.addActionListener(e -> cetakTabel());
        panelBottom.add(BtnCetak);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setPreferredSize(new Dimension(100, 23));
        BtnKeluar.addActionListener(e -> dispose());
        panelBottom.add(BtnKeluar);
        south.add(panelBottom, java.awt.BorderLayout.SOUTH);

        internalFrame1.add(south, java.awt.BorderLayout.SOUTH);

        getContentPane().setLayout(new java.awt.BorderLayout(1, 1));
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
        pack();
    }

    // =================================================================
    // Riwayat query (pemberian_obat + piutang, semua pasien)
    // =================================================================
    private void tampilRiwayat() {
        modelObat.setRowCount(0);
        String tglA = Valid.SetTgl(tgl1.getSelectedItem() + "");
        String tglB = Valid.SetTgl(tgl2.getSelectedItem() + "");
        String keyword = TCari.getText().trim();
        boolean pakaiKeyword = !keyword.isEmpty();

        StringBuilder sql = new StringBuilder(
              "SELECT tgl, jam, no_rkm_medis, nm_pasien, sumber, kode_brng, nama_brng, "
            + "       qty, sat, bangsal, jenis_rawat, ref FROM (\n"
            + "  SELECT dpo.tgl_perawatan AS tgl, dpo.jam, 'Pemberian Obat' AS sumber,\n"
            + "         dpo.kode_brng, IFNULL(db.nama_brng,'-') AS nama_brng,\n"
            + "         dpo.jml AS qty, IFNULL(db.kode_sat,'') AS sat,\n"
            + "         IFNULL(dpo.kd_bangsal,'') AS bangsal,\n"
            + "         IFNULL(dpo.status,'') AS jenis_rawat,\n"
            + "         dpo.no_rawat AS ref,\n"
            + "         IFNULL(rp.no_rkm_medis,'-') AS no_rkm_medis,\n"
            + "         IFNULL(pas.nm_pasien,'-') AS nm_pasien\n"
            + "  FROM detail_pemberian_obat dpo\n"
            + "  LEFT JOIN databarang  db  ON db.kode_brng     = dpo.kode_brng\n"
            + "  LEFT JOIN reg_periksa rp  ON rp.no_rawat      = dpo.no_rawat\n"
            + "  LEFT JOIN pasien      pas ON pas.no_rkm_medis = rp.no_rkm_medis\n"
            + "  WHERE dpo.tgl_perawatan BETWEEN ? AND ?\n"
            + "  UNION ALL\n"
            + "  SELECT p.tgl_piutang, '00:00:00', 'Piutang',\n"
            + "         dp.kode_brng, IFNULL(db.nama_brng,'-'),\n"
            + "         dp.jumlah, IFNULL(db.kode_sat,''),\n"
            + "         IFNULL(p.kd_bangsal,''), '', p.nota_piutang,\n"
            + "         IFNULL(p.no_rkm_medis,'-'), IFNULL(p.nm_pasien,'-')\n"
            + "  FROM detailpiutang dp\n"
            + "  JOIN piutang p ON p.nota_piutang = dp.nota_piutang\n"
            + "  LEFT JOIN databarang db ON db.kode_brng = dp.kode_brng\n"
            + "  WHERE p.tgl_piutang BETWEEN ? AND ?\n"
            + ") x");
        if (pakaiKeyword) {
            sql.append(" WHERE no_rkm_medis LIKE ? OR nm_pasien LIKE ? "
                     + "OR kode_brng LIKE ? OR nama_brng LIKE ?");
        }
        sql.append(" ORDER BY tgl DESC, jam DESC");

        this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        int total = 0, jmlPemberian = 0, jmlPiutang = 0;
        double qtyTotal = 0;
        Set<String> uniqObat = new HashSet<>();
        Set<String> uniqPasien = new HashSet<>();
        try (PreparedStatement p = koneksi.prepareStatement(sql.toString())) {
            int idx = 1;
            p.setString(idx++, tglA); p.setString(idx++, tglB);
            p.setString(idx++, tglA); p.setString(idx++, tglB);
            if (pakaiKeyword) {
                String k = "%" + keyword + "%";
                p.setString(idx++, k); p.setString(idx++, k);
                p.setString(idx++, k); p.setString(idx++, k);
            }
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    total++;
                    qtyTotal += r.getDouble("qty");
                    uniqObat.add(r.getString("kode_brng"));
                    uniqPasien.add(r.getString("no_rkm_medis"));
                    String sumber = r.getString("sumber");
                    if ("Pemberian Obat".equals(sumber)) jmlPemberian++;
                    else if ("Piutang".equals(sumber)) jmlPiutang++;
                    modelObat.addRow(new Object[]{
                        r.getString("tgl"), r.getString("jam"),
                        r.getString("no_rkm_medis"), r.getString("nm_pasien"),
                        sumber, r.getString("kode_brng"), r.getString("nama_brng"),
                        fmt(r.getDouble("qty")), r.getString("sat"),
                        r.getString("bangsal"), r.getString("jenis_rawat"),
                        r.getString("ref")
                    });
                }
            }
        } catch (SQLException ex) {
            this.setCursor(java.awt.Cursor.getDefaultCursor());
            showErr("Gagal query riwayat", ex);
            return;
        }
        this.setCursor(java.awt.Cursor.getDefaultCursor());

        lblInfo.setText(String.format(
            "<html>Periode <b>%s s/d %s</b> &nbsp;·&nbsp; "
            + "Total baris <b>%d</b> (Pemberian %d, Piutang %d) &nbsp;·&nbsp; "
            + "Pasien unik <b>%d</b> &nbsp;·&nbsp; Obat unik <b>%d</b> &nbsp;·&nbsp; "
            + "Total qty <b>%s</b></html>",
            tglA, tglB, total, jmlPemberian, jmlPiutang,
            uniqPasien.size(), uniqObat.size(), fmt(qtyTotal)));

        ringkasanInfo = String.format(
            "Total baris %d (Pemberian %d, Piutang %d) · Pasien unik %d · Obat unik %d · Total qty %s",
            total, jmlPemberian, jmlPiutang,
            uniqPasien.size(), uniqObat.size(), fmt(qtyTotal));
    }

    // =================================================================
    // Cetak (JasperReports via Valid.MyReportqry + temporary table)
    // =================================================================
    private void cetakTabel() {
        if (modelObat.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Tidak ada data untuk dicetak. Tampilkan data dulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        try {
            String ip = akses.getalamatip();
            // Bersihkan temporary slot user ini
            Sequel.queryu("delete from temporary where temp37='" + ip + "'");

            int rows = modelObat.getRowCount();
            for (int i = 0; i < rows; i++) {
                // Bangun string value: no + temp1..temp12 (data) + temp13..temp36 (kosong) + temp37 (IP)
                StringBuilder sb = new StringBuilder();
                sb.append("'").append(i).append("'");
                for (int c = 0; c < 12; c++) {
                    sb.append(",'").append(esc(modelObat.getValueAt(i, c))).append("'");
                }
                // temp13..temp36 = 24 kolom kosong
                for (int k = 0; k < 24; k++) {
                    sb.append(",''");
                }
                // temp37 = IP address
                sb.append(",'").append(ip).append("'");
                Sequel.menyimpan("temporary", sb.toString(), "Riwayat Obat Pasien");
            }

            String tglA = Valid.SetTgl(tgl1.getSelectedItem() + "");
            String tglB = Valid.SetTgl(tgl2.getSelectedItem() + "");
            String periode = tglA + "  s/d  " + tglB;

            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            param.put("periode", periode);
            param.put("ringkasan", ringkasanInfo);

            Valid.MyReportqry(
                "rptRiwayatObatPasien.jasper",
                "report",
                "::[ Riwayat Obat / BHP Pasien ]::",
                "select * from temporary where temporary.temp37='" + ip + "' order by temporary.no",
                param
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal cetak: " + ex.getMessage(),
                    "Error Cetak", JOptionPane.ERROR_MESSAGE);
        } finally {
            this.setCursor(java.awt.Cursor.getDefaultCursor());
        }
    }

    /** Escape single-quote untuk nilai string SQL inline (pola SIMRS Khanza). */
    private static String esc(Object o) {
        if (o == null) return "";
        return o.toString().replace("'", "''").replace("\\", "\\\\");
    }

    // =================================================================
    // Helpers
    // =================================================================
    private static String fmt(double d) {
        if (d == (long) d) return String.format("%,d", (long) d);
        return String.format("%,.2f", d);
    }

    private void showErr(String title, Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), title, JOptionPane.ERROR_MESSAGE);
    }

    // =================================================================
    // Components
    // =================================================================
    private widget.InternalFrame internalFrame1;
    private widget.ScrollPane Scroll;
    private widget.Table tbObat;
    private widget.panelisi panelFilter, panelInfo, panelBottom;
    private widget.Label lblInfo, lblKeyword;
    private widget.TextBox TCari;
    private widget.Tanggal tgl1, tgl2;
    private widget.Button BtnCari, BtnAll, BtnRefresh, BtnCetak, BtnKeluar;

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgRiwayatObatPasien d = new DlgRiwayatObatPasien(null, true);
            d.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            d.setVisible(true);
        });
    }
}
