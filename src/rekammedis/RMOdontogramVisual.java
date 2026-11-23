package rekammedis;

import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import widget.CekBox;
import widget.InternalFrame;
import widget.Label;
import widget.ScrollPane;
import widget.Table;
import widget.TextBox;
import widget.panelisi;

/**
 * Form Odontogram Visual — Standar FDI / ISO 3950
 * RM.PGI.01.002.B.2022 | Permenkes No. 24 Tahun 2022
 */
public class RMOdontogramVisual extends JDialog {

    private TextBox  TNoRw, TNoRM, TPasien, TCari;
    private widget.Button BtnSimpan, BtnBaru, BtnHapus, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2;
    private Label LCount;
    private PanelOdontogramFDI panelOdonto;
    private CekBox ChkInput;
    private JPanel PanelInput;
    private ScrollPane ScrollInput;
    private Table tbObat;
    private DefaultTableModel tabMode;

    private final java.sql.Connection  koneksi = koneksiDB.condb();
    private final sekuel               Sequel  = new sekuel();
    private final validasi             Valid   = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet         rs;

    public RMOdontogramVisual(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        buatTabelJikaBelumAda();
        initComponents();
        initTanggalCari();
        java.awt.Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width, screen.height);
        setLocationRelativeTo(null);
    }

    private void initTanggalCari() {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");
            java.util.Calendar cal = java.util.Calendar.getInstance();
            String tglAkhir = sdf.format(cal.getTime());
            cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
            String tglAwal = sdf.format(cal.getTime());
            DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{tglAwal}));
            DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[]{tglAkhir}));
        } catch (Exception ex) {}
    }

    // ---- Auto-create tabel jika belum ada di DB ----
    private void buatTabelJikaBelumAda() {
        try {
            koneksi.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS `odontogram_gigi` (" +
                "  `no_rawat`    VARCHAR(17) NOT NULL," +
                "  `nomor_gigi`  SMALLINT NOT NULL," +
                "  `kondisi`     VARCHAR(10) NOT NULL DEFAULT 'N'," +
                "  `keterangan`  TEXT," +
                "  KEY `idx_no_rawat`   (`no_rawat`)," +
                "  KEY `idx_nomor_gigi` (`nomor_gigi`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8"
            );
            // Hapus kolom id jika ada (tabel lama mungkin punya AUTO_INCREMENT id)
            try { koneksi.createStatement().execute("ALTER TABLE odontogram_gigi DROP COLUMN id"); } catch (Exception ex) {}
            // rekam_medik_gigi diperlukan untuk cetak
            koneksi.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS `rekam_medik_gigi` (" +
                " `no_rawat` VARCHAR(17) NOT NULL UNIQUE," +
                " `suku_ras` VARCHAR(50)," +
                " `pekerjaan_kantor` VARCHAR(100)," +
                " `alamat_kantor` VARCHAR(200)," +
                " `telp_seluler` VARCHAR(20)," +
                " `goldar` VARCHAR(5)," +
                " `td_sistolik` VARCHAR(10)," +
                " `td_diastolik` VARCHAR(10)," +
                " `status_td` VARCHAR(20) DEFAULT 'Normal'," +
                " `penyakit_jantung` VARCHAR(10) DEFAULT 'Tidak Ada'," +
                " `diabetes` VARCHAR(10) DEFAULT 'Tidak Ada'," +
                " `haemopilia` VARCHAR(10) DEFAULT 'Tidak Ada'," +
                " `hepatitis` VARCHAR(10) DEFAULT 'Tidak Ada'," +
                " `gastritis` VARCHAR(10) DEFAULT 'Tidak Ada'," +
                " `penyakit_lainnya` VARCHAR(10) DEFAULT 'Tidak Ada'," +
                " `ket_penyakit_lainnya` TEXT," +
                " `alergi_obat` VARCHAR(10) DEFAULT 'Tidak Ada'," +
                " `ket_alergi_obat` TEXT," +
                " `alergi_makanan` VARCHAR(10) DEFAULT 'Tidak Ada'," +
                " `ket_alergi_makanan` TEXT," +
                " `tgl_pencatatan` DATE," +
                " `nip` VARCHAR(20)," +
                " `occlusi` VARCHAR(20) DEFAULT 'Normal Bite'," +
                " `torus_palatinus` VARCHAR(20) DEFAULT 'Tidak Ada'," +
                " `torus_mandibularis` VARCHAR(20) DEFAULT 'Tidak Ada'," +
                " `palatum` VARCHAR(20) DEFAULT 'Sedang'," +
                " `diastema` VARCHAR(10) DEFAULT 'Tidak Ada'," +
                " `ket_diastema` TEXT," +
                " `gigi_anomali` VARCHAR(10) DEFAULT 'Tidak Ada'," +
                " `ket_gigi_anomali` TEXT," +
                " `lain_lain` TEXT," +
                " `dmf_d` TINYINT DEFAULT 0," +
                " `dmf_m` TINYINT DEFAULT 0," +
                " `dmf_f` TINYINT DEFAULT 0," +
                " `jml_photo` TINYINT DEFAULT 0," +
                " `jml_rontgen` TINYINT DEFAULT 0," +
                " `tgl_periksa` DATE," +
                " `nip_pemeriksa` VARCHAR(20)," +
                " KEY `idx_no_rawat` (`no_rawat`)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8"
            );
            try { koneksi.createStatement().execute("ALTER TABLE rekam_medik_gigi DROP COLUMN id"); } catch (Exception ex) {}
        } catch (Exception e) {
            System.out.println("Notif buatTabel odontogram_gigi: " + e);
        }
    }

    private void initComponents() {
        InternalFrame frame1 = new InternalFrame();
        frame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240,245,235)),
            "::[ Odontogram Visual  —  RM.PGI.01.002.B.2022  |  Standar FDI / ISO 3950 ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11), new Color(50,50,50)));
        frame1.setLayout(new BorderLayout(1,1));

        // ===== PANEL ATAS: identitas pasien =====
        panelisi panelAtas = new panelisi();
        panelAtas.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 4));
        panelAtas.setPreferredSize(new Dimension(1024, 33));
        Label lNr = new Label(); lNr.setText("No.Rawat :");
        TNoRw   = new TextBox(); TNoRw.setPreferredSize(new Dimension(130,23)); TNoRw.setEditable(false);
        TNoRM   = new TextBox(); TNoRM.setPreferredSize(new Dimension(100,23)); TNoRM.setEditable(false);
        TPasien = new TextBox(); TPasien.setPreferredSize(new Dimension(250,23)); TPasien.setEditable(false);
        panelAtas.add(lNr); panelAtas.add(TNoRw); panelAtas.add(TNoRM); panelAtas.add(TPasien);

        // ===== PANEL INPUT: odontogram + ChkInput toggle =====
        widget.PanelBiasa FormInput = new widget.PanelBiasa();
        FormInput.setPreferredSize(new Dimension(900, 520));
        FormInput.setLayout(new BorderLayout());

        panelOdonto = new PanelOdontogramFDI();
        ScrollPane scrollOdonto = new ScrollPane();
        scrollOdonto.setViewportView(panelOdonto);
        FormInput.add(scrollOdonto, BorderLayout.CENTER);

        ChkInput = new CekBox();
        ChkInput.setIcon(new ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setText(".: Input Odontogram");
        ChkInput.setSelected(true);
        ChkInput.setBorderPainted(true);
        ChkInput.setRolloverIcon(new ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setSelectedIcon(new ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setRolloverSelectedIcon(new ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.addActionListener(ev -> toggleForm());

        ScrollInput = new ScrollPane();
        ScrollInput.setViewportView(FormInput);
        ScrollInput.setPreferredSize(new Dimension(900, 480));

        PanelInput = new JPanel(new BorderLayout());
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new Dimension(900, 500));
        PanelInput.add(ScrollInput, BorderLayout.CENTER);
        PanelInput.add(ChkInput,   BorderLayout.PAGE_END);

        // ===== TABEL: daftar rekaman odontogram =====
        ScrollPane Scroll = new ScrollPane();
        tbObat = new Table();
        tabMode = new DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat","No.R.M.","Nama Pasien","Tgl.Kunjungan","Poli","Dokter","Total Temuan"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbObat.setModel(tabMode);
        tbObat.setAutoResizeMode(Table.AUTO_RESIZE_OFF);
        int[] colW = {115, 80, 180, 90, 120, 160, 85};
        for (int i = 0; i < colW.length; i++) {
            TableColumn col = tbObat.getColumnModel().getColumn(i);
            col.setPreferredWidth(colW[i]);
        }
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (tabMode.getRowCount() != 0) getData();
            }
            @Override public void mousePressed(java.awt.event.MouseEvent e) {
                int row = tbObat.rowAtPoint(e.getPoint());
                if (row >= 0 && row < tabMode.getRowCount()) tbObat.setRowSelectionInterval(row, row);
                if (javax.swing.SwingUtilities.isRightMouseButton(e) && tabMode.getRowCount() != 0) {
                    getData();
                    javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();
                    javax.swing.JMenuItem miHdr = new javax.swing.JMenuItem(
                        tabMode.getValueAt(tbObat.getSelectedRow(), 2) + " — " +
                        tabMode.getValueAt(tbObat.getSelectedRow(), 0));
                    miHdr.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 10));
                    miHdr.setEnabled(false);
                    popup.add(miHdr);
                    popup.addSeparator();
                    javax.swing.JMenuItem miCetak = new javax.swing.JMenuItem("  Cetak Rekam Medik Gigi");
                    miCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
                    miCetak.addActionListener(ev -> cetakRekamMedikGigi());
                    popup.add(miCetak);
                    popup.show(tbObat, e.getX(), e.getY());
                }
            }
        });
        Scroll.setViewportView(tbObat);

        // ===== PANEL BAWAH: tombol aksi + pencarian =====
        panelisi panelBawah = new panelisi();
        panelBawah.setPreferredSize(new Dimension(44, 54));
        panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));

        BtnSimpan = new widget.Button();
        BtnSimpan.setIcon(new ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setText("Simpan"); BtnSimpan.setPreferredSize(new Dimension(100, 30));

        BtnBaru = new widget.Button();
        BtnBaru.setIcon(new ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBaru.setText("Baru"); BtnBaru.setPreferredSize(new Dimension(100, 30));

        BtnHapus = new widget.Button();
        BtnHapus.setIcon(new ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setText("Hapus"); BtnHapus.setPreferredSize(new Dimension(100, 30));

        BtnKeluar = new widget.Button();
        BtnKeluar.setIcon(new ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setText("Keluar"); BtnKeluar.setPreferredSize(new Dimension(100, 30));

        Label lc  = new Label(); lc.setText("Tanggal :"); lc.setPreferredSize(new Dimension(60,23));
        DTPCari1  = new widget.Tanggal(); DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setPreferredSize(new Dimension(95,23));
        Label sd  = new Label(); sd.setText("s.d."); sd.setPreferredSize(new Dimension(28,23));
        DTPCari2  = new widget.Tanggal(); DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setPreferredSize(new Dimension(95,23));
        Label lk  = new Label(); lk.setText("Key Word :"); lk.setPreferredSize(new Dimension(68,23));
        TCari     = new TextBox(); TCari.setPreferredSize(new Dimension(150,23));
        BtnCari   = new widget.Button(); BtnCari.setIcon(new ImageIcon(getClass().getResource("/picture/accept.png"))); BtnCari.setPreferredSize(new Dimension(28,23));
        BtnAll    = new widget.Button(); BtnAll.setIcon(new ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); BtnAll.setPreferredSize(new Dimension(28,23));
        LCount    = new Label(); LCount.setText("Record : 0"); LCount.setPreferredSize(new Dimension(100,23));

        panelBawah.add(BtnSimpan); panelBawah.add(BtnBaru); panelBawah.add(BtnHapus); panelBawah.add(BtnKeluar);
        panelBawah.add(lc); panelBawah.add(DTPCari1); panelBawah.add(sd); panelBawah.add(DTPCari2);
        panelBawah.add(lk); panelBawah.add(TCari); panelBawah.add(BtnCari); panelBawah.add(BtnAll);
        panelBawah.add(LCount);

        // ===== ACTION LISTENERS =====
        BtnKeluar.addActionListener(ev -> dispose());
        BtnBaru.addActionListener(ev -> emptTeks());
        BtnCari.addActionListener(ev -> tampilDaftar());
        BtnAll.addActionListener(ev -> { TCari.setText(""); tampilDaftar(); });
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) tampilDaftar();
            }
        });

        BtnSimpan.addActionListener(ev -> {
            if (TNoRw.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "No.Rawat kosong — pilih pasien dari tabel atau set dari luar.");
                return;
            }
            simpanData();
        });

        BtnHapus.addActionListener(ev -> {
            if (tbObat.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Pilih data dulu dari tabel...!!!!");
                return;
            }
            String nr = tabMode.getValueAt(tbObat.getSelectedRow(), 0).toString();
            if (JOptionPane.showConfirmDialog(null,
                    "Hapus data odontogram untuk " + nr + "?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION) == 0) {
                Sequel.meghapus("odontogram_gigi", "no_rawat", nr);
                tampilDaftar();
                emptTeks();
            }
        });

        // ===== SUSUN LAYOUT =====
        InternalFrame ifd = new InternalFrame();
        ifd.setLayout(new BorderLayout());
        ifd.setBackground(new Color(255,255,255));
        ifd.add(PanelInput, BorderLayout.NORTH);
        ifd.add(Scroll,     BorderLayout.CENTER);

        frame1.add(panelAtas, BorderLayout.PAGE_START);
        frame1.add(ifd,       BorderLayout.CENTER);
        frame1.add(panelBawah,BorderLayout.PAGE_END);

        setLayout(new BorderLayout());
        add(frame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
    }

    private void toggleForm() {
        ChkInput.setVisible(false);
        PanelInput.setPreferredSize(new Dimension(900, ChkInput.isSelected() ? 500 : 20));
        ScrollInput.getViewport().getView().setVisible(ChkInput.isSelected());
        ChkInput.setVisible(true);
        PanelInput.revalidate();
    }

    // ===== TAMPIL DAFTAR (tabel pencarian) =====
    public void tampilDaftar() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "SELECT og.no_rawat, r.no_rkm_medis, p.nm_pasien, r.tgl_registrasi, " +
                "po.nm_poli, d.nm_dokter, COUNT(og.nomor_gigi) as total_temuan " +
                "FROM odontogram_gigi og " +
                "INNER JOIN reg_periksa r ON og.no_rawat = r.no_rawat " +
                "INNER JOIN pasien p ON r.no_rkm_medis = p.no_rkm_medis " +
                "LEFT JOIN poliklinik po ON r.kd_poli = po.kd_poli " +
                "LEFT JOIN dokter d ON r.kd_dokter = d.kd_dokter " +
                "WHERE r.tgl_registrasi BETWEEN ? AND ? " +
                "AND (og.no_rawat LIKE ? OR r.no_rkm_medis LIKE ? OR p.nm_pasien LIKE ?) " +
                "GROUP BY og.no_rawat " +
                "ORDER BY r.tgl_registrasi DESC");
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem()+"") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem()+"") + " 23:59:59");
            String kw = "%" + TCari.getText().trim() + "%";
            ps.setString(3, kw); ps.setString(4, kw); ps.setString(5, kw);
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6),
                    rs.getString(7)
                });
            }
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) {
            System.out.println("Notif tampilDaftar: " + e);
        } finally {
            try { if (rs!=null) rs.close(); if (ps!=null) ps.close(); } catch (Exception e) {}
        }
    }

    // ===== LOAD DATA GIGI DARI TABEL KE VISUAL =====
    private void getData() {
        if (tbObat.getSelectedRow() == -1) return;
        String nr = tabMode.getValueAt(tbObat.getSelectedRow(), 0).toString();
        TNoRw.setText(nr);
        TNoRM.setText(tabMode.getValueAt(tbObat.getSelectedRow(), 1).toString());
        TPasien.setText(tabMode.getValueAt(tbObat.getSelectedRow(), 2).toString());
        tampil();
    }

    // ===== TAMPIL: load kondisi gigi dari DB ke visual =====
    public void tampil() {
        String nr = TNoRw.getText().trim();
        if (nr.isEmpty()) return;
        Map<Integer,String> kondisi    = new HashMap<>();
        Map<Integer,String> keterangan = new HashMap<>();
        try {
            ps = koneksi.prepareStatement(
                "SELECT nomor_gigi, kondisi, keterangan FROM odontogram_gigi WHERE no_rawat=? ORDER BY nomor_gigi");
            ps.setString(1, nr);
            rs = ps.executeQuery();
            while (rs.next()) {
                try {
                    int    num = Integer.parseInt(rs.getString(1));
                    String k   = rs.getString(2);
                    String t   = rs.getString(3);
                    kondisi.put(num, k != null ? k : "N");
                    keterangan.put(num, t != null ? t : "");
                } catch (Exception ex) { /* skip baris tidak valid */ }
            }
        } catch (Exception e) {
            System.out.println("Notif RMOdontogramVisual.tampil: " + e);
        } finally {
            try { if (rs!=null) rs.close(); if (ps!=null) ps.close(); } catch (Exception e) {}
        }
        panelOdonto.loadData(kondisi, keterangan);
    }

    // ===== SIMPAN KONDISI GIGI KE DB =====
    private void simpanData() {
        String nr = TNoRw.getText().trim();
        Sequel.meghapus("odontogram_gigi", "no_rawat", nr);
        Map<Integer,String> kondisi    = panelOdonto.getKondisiMap();
        Map<Integer,String> keterangan = panelOdonto.getKeteranganMap();
        boolean ada = false;
        for (Map.Entry<Integer,String> e : kondisi.entrySet()) {
            if (!"N".equals(e.getValue())) {
                Sequel.menyimpantf("odontogram_gigi", "?,?,?,?", "", 4, new String[]{
                    nr,
                    String.valueOf(e.getKey()),
                    e.getValue(),
                    keterangan.getOrDefault(e.getKey(), "")
                });
                ada = true;
            }
        }
        JOptionPane.showMessageDialog(null,
            "Odontogram berhasil disimpan." +
            (ada ? "" : "\n(Semua gigi Normal — tidak ada rekaman tersimpan.)"),
            "Sukses", JOptionPane.INFORMATION_MESSAGE);
        tampilDaftar();
    }

    // ===== CETAK =====

    private void cetakRekamMedikGigi() {
        String nr = TNoRw.getText().trim();
        if (nr.isEmpty()) { JOptionPane.showMessageDialog(null, "Pilih data dulu...!!!!"); return; }
        java.util.Map<String,Object> p = new java.util.HashMap<>();
        p.put("namars",    akses.getnamars());
        p.put("alamatrs",  akses.getalamatrs());
        p.put("kotars",    akses.getkabupatenrs());
        p.put("propinsirs",akses.getpropinsirs());
        p.put("kontakrs",  akses.getkontakrs());
        p.put("emailrs",   akses.getemailrs());
        p.put("logo",          Sequel.cariGambar("select setting.logo from setting"));
        p.put("no_rawat",      nr);
        p.put("odontogram_img", panelOdonto.renderToStream());
        Valid.MyReportqry("rptRekamMedikGigi.jasper", "report", "::[ Rekam Medik Kedokteran Gigi ]::",
            "select r.no_rawat,r.no_rkm_medis,p.nm_pasien,p.tgl_lahir,p.jk," +
            "p.alamat,p.no_tlp as tlp," +
            "sb.nama_suku_bangsa as suku_ras,p.pekerjaan as pekerjaan_kantor,p.gol_darah as goldar," +
            "m.td_sistolik,m.td_diastolik,m.status_td," +
            "m.penyakit_jantung,m.diabetes,m.haemopilia,m.hepatitis,m.gastritis," +
            "m.penyakit_lainnya,m.ket_penyakit_lainnya," +
            "m.alergi_obat,m.ket_alergi_obat,m.alergi_makanan,m.ket_alergi_makanan," +
            "m.tgl_pencatatan," +
            "m.occlusi,m.torus_palatinus,m.torus_mandibularis,m.palatum," +
            "m.diastema,m.ket_diastema,m.gigi_anomali,m.ket_gigi_anomali," +
            "m.lain_lain,m.dmf_d,m.dmf_m,m.dmf_f," +
            "m.jml_photo,m.jml_rontgen,m.tgl_periksa,dk2.nm_dokter as nm_petugas " +
            "from reg_periksa r " +
            "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
            "left join suku_bangsa sb on p.suku_bangsa=sb.id " +
            "left join rekam_medik_gigi m on r.no_rawat=m.no_rawat " +
            "left join dokter dk2 on m.nip_pemeriksa=dk2.kd_dokter " +
            "where r.no_rawat='" + nr + "'", p);
    }

    // ===== PUBLIC API =====

    public void emptTeks() {
        TNoRw.setText(""); TNoRM.setText(""); TPasien.setText("");
        panelOdonto.resetAll();
    }

    public void setNoRm(String noRawat, String noRM, String namaPasien) {
        TNoRw.setText(noRawat);
        TNoRM.setText(noRM);
        TPasien.setText(namaPasien);
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getrujukan_keluar());
        BtnHapus.setEnabled(akses.getrujukan_keluar());
    }
}
