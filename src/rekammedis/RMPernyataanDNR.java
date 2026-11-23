package rekammedis;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import kepegawaian.DlgCariDokter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import widget.Button;
import widget.InternalFrame;
import widget.Label;
import widget.TextBox;
import widget.ComboBox;
import widget.PanelBiasa;
import widget.ScrollPane;
import widget.panelisi;

public class RMPernyataanDNR extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar;
    private Button BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2;
    private TextBox TCari;
    private Label LCount;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;

    // Header
    private TextBox TNoRw, TNoRM, TPasien;

    // Form fields
    private widget.Tanggal TglAsuhan;
    private TextBox TNamaPasien, TTglLahir, THubungan, TNamaSaksi, TNamaPembuat;
    private TextBox KdDokter, NmDokter;
    private Button BtnDokter;
    private ComboBox CmbJK;

    private DlgCariDokter dokterDlg = new DlgCariDokter(null, false);

    // Table
    private widget.Table tbObat;
    private javax.swing.table.DefaultTableModel tabMode;
    private ScrollPane Scroll;

    // Input panel
    private widget.CekBox ChkInput;
    private PanelBiasa FormInput;
    private ScrollPane ScrollInput;
    private JPanel PanelInput;

    public widget.editorpane LoadHTML;

    public RMPernyataanDNR(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();

        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Pernyataan DNR (Do Not Resuscitate) ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11), new Color(50, 50, 50)
        ));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // PANEL ATAS
        panelisi panelAtas = new panelisi();
        panelAtas.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 4));
        panelAtas.setPreferredSize(new Dimension(1024, 33));

        Label lblNoRw = new Label(); lblNoRw.setText("No.Rawat :");
        TNoRw = new TextBox(); TNoRw.setPreferredSize(new Dimension(130, 23));
        TNoRM = new TextBox(); TNoRM.setPreferredSize(new Dimension(100, 23));
        TPasien = new TextBox(); TPasien.setPreferredSize(new Dimension(200, 23));

        panelAtas.add(lblNoRw); panelAtas.add(TNoRw); panelAtas.add(TNoRM); panelAtas.add(TPasien);

        // TABLE
        Scroll = new ScrollPane();
        tbObat = new widget.Table();
        tabMode = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat", "No.R.M.", "Nama Pasien", "Tanggal", "Nama", "L/P", "Tgl.Lahir", "Hubungan", "Dokter", "Saksi", "Pembuat Pernyataan"}
        ) {
            @Override public boolean isCellEditable(int rowIndex, int colIndex) { return false; }
        };
        tbObat.setModel(tabMode);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (tabMode.getRowCount() != 0) { try { getData(); } catch (Exception e) {} }
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_SPACE) { getData(); }
            }
        });
        javax.swing.JPopupMenu popupMenu = new javax.swing.JPopupMenu();
        javax.swing.JMenuItem mnCetak = new javax.swing.JMenuItem("Cetak Pernyataan DNR");
        mnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetak.addActionListener(evt -> BtnPrint.doClick());
        popupMenu.add(mnCetak);
        tbObat.setComponentPopupMenu(popupMenu);

        Scroll.setViewportView(tbObat);

        // FORM INPUT
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(900, 280));
        FormInput.setLayout(null);

        int y = 10;
        JLabel lblHeader = new JLabel("FORMULIR PERNYATAAN JANGAN DILAKUKAN RESUSITASI - DNR (DO NOT RESUSCITATE)");
        lblHeader.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblHeader.setBounds(10, y, 600, 23); FormInput.add(lblHeader);

        y += 30;
        JLabel lblInfo = new JLabel("Saya yang bertanda tangan di bawah ini :");
        lblInfo.setBounds(10, y, 300, 23); FormInput.add(lblInfo);

        y += 28;
        Label lblNama = new Label(); lblNama.setText("Nama :"); lblNama.setBounds(10, y, 120, 23); FormInput.add(lblNama);
        TNamaPasien = new TextBox(); TNamaPasien.setBounds(135, y, 350, 23); FormInput.add(TNamaPasien);
        Label lblJK = new Label(); lblJK.setText("L/P :"); lblJK.setBounds(500, y, 30, 23); FormInput.add(lblJK);
        CmbJK = new ComboBox();
        CmbJK.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"L", "P"}));
        CmbJK.setBounds(535, y, 50, 23); FormInput.add(CmbJK);

        y += 28;
        Label lblTglLahir = new Label(); lblTglLahir.setText("Tanggal Lahir :"); lblTglLahir.setBounds(10, y, 120, 23); FormInput.add(lblTglLahir);
        TTglLahir = new TextBox(); TTglLahir.setBounds(135, y, 120, 23); FormInput.add(TTglLahir);

        y += 28;
        Label lblTgl = new Label(); lblTgl.setText("Tanggal :"); lblTgl.setBounds(10, y, 120, 23); FormInput.add(lblTgl);
        TglAsuhan = new widget.Tanggal();
        TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setBounds(135, y, 130, 23); FormInput.add(TglAsuhan);

        y += 28;
        Label lblHub = new Label(); lblHub.setText("Hubungan :"); lblHub.setBounds(10, y, 120, 23); FormInput.add(lblHub);
        THubungan = new TextBox(); THubungan.setBounds(135, y, 200, 23); FormInput.add(THubungan);
        JLabel lblHubInfo = new JLabel("(kosongkan jika pasien sendiri)");
        lblHubInfo.setFont(new java.awt.Font("Tahoma", 2, 10));
        lblHubInfo.setBounds(345, y, 200, 23); FormInput.add(lblHubInfo);

        y += 35;
        Label lblDokter = new Label(); lblDokter.setText("Dokter :"); lblDokter.setBounds(10, y, 120, 23); FormInput.add(lblDokter);
        KdDokter = new TextBox(); KdDokter.setBounds(135, y, 80, 23); FormInput.add(KdDokter);
        NmDokter = new TextBox(); NmDokter.setBounds(217, y, 210, 23); FormInput.add(NmDokter);
        BtnDokter = new Button(); BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokter.setBounds(429, y, 28, 23); FormInput.add(BtnDokter);
        BtnDokter.addActionListener(evt -> {
            dokterDlg.emptTeks(); dokterDlg.isCek();
            dokterDlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            dokterDlg.setLocationRelativeTo(internalFrame1); dokterDlg.setVisible(true);
        });
        dokterDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (dokterDlg.getTable().getSelectedRow() != -1) {
                    KdDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(), 0).toString());
                    NmDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

        y += 28;
        Label lblSaksi = new Label(); lblSaksi.setText("Saksi :"); lblSaksi.setBounds(10, y, 120, 23); FormInput.add(lblSaksi);
        TNamaSaksi = new TextBox(); TNamaSaksi.setBounds(135, y, 250, 23); FormInput.add(TNamaSaksi);

        Label lblPembuat = new Label(); lblPembuat.setText("Pembuat Pernyataan :"); lblPembuat.setBounds(400, y, 130, 23); FormInput.add(lblPembuat);
        TNamaPembuat = new TextBox(); TNamaPembuat.setBounds(535, y, 250, 23); FormInput.add(TNamaPembuat);

        // Input toggle
        ChkInput = new widget.CekBox();
        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setMnemonic('1'); ChkInput.setText(".: Input Data"); ChkInput.setToolTipText("Alt+1");
        ChkInput.setBorderPainted(true); ChkInput.setBorderPaintedFlat(true); ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput.setPreferredSize(new Dimension(192, 20));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setSelected(true);
        ChkInput.addActionListener(evt -> isForm());

        ScrollInput = new ScrollPane();
        ScrollInput.setViewportView(FormInput);
        ScrollInput.setPreferredSize(new Dimension(900, 300));

        PanelInput = new JPanel(new BorderLayout());
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new Dimension(WIDTH, 320));
        PanelInput.add(ScrollInput, BorderLayout.CENTER);
        PanelInput.add(ChkInput, BorderLayout.PAGE_END);

        // PANEL BAWAH
        panelBawah = new panelisi();
        panelBawah.setPreferredSize(new Dimension(44, 54));
        panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));

        BtnSimpan = new Button(); BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setMnemonic('S'); BtnSimpan.setText("Simpan"); BtnSimpan.setToolTipText("Alt+S"); BtnSimpan.setPreferredSize(new Dimension(100, 30));
        BtnBatal = new Button(); BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBatal.setMnemonic('B'); BtnBatal.setText("Baru"); BtnBatal.setToolTipText("Alt+B"); BtnBatal.setPreferredSize(new Dimension(100, 30));
        BtnHapus = new Button(); BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setMnemonic('H'); BtnHapus.setText("Hapus"); BtnHapus.setToolTipText("Alt+H"); BtnHapus.setPreferredSize(new Dimension(100, 30));
        BtnEdit = new Button(); BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnEdit.setMnemonic('G'); BtnEdit.setText("Ganti"); BtnEdit.setToolTipText("Alt+G"); BtnEdit.setPreferredSize(new Dimension(100, 30));
        BtnPrint = new Button(); BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        BtnPrint.setMnemonic('T'); BtnPrint.setText("Cetak"); BtnPrint.setToolTipText("Alt+T"); BtnPrint.setPreferredSize(new Dimension(100, 30));
        BtnKeluar = new Button(); BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K'); BtnKeluar.setText("Keluar"); BtnKeluar.setToolTipText("Alt+K"); BtnKeluar.setPreferredSize(new Dimension(100, 30));

        Label lblTglCari = new Label(); lblTglCari.setText("Tanggal :"); lblTglCari.setPreferredSize(new Dimension(55, 23));
        DTPCari1 = new widget.Tanggal(); DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setPreferredSize(new Dimension(90, 23));
        Label lblSd = new Label(); lblSd.setText("s.d."); lblSd.setPreferredSize(new Dimension(25, 23));
        DTPCari2 = new widget.Tanggal(); DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setPreferredSize(new Dimension(90, 23));
        Label lblKey = new Label(); lblKey.setText("Key Word :"); lblKey.setPreferredSize(new Dimension(70, 23));
        TCari = new TextBox(); TCari.setPreferredSize(new Dimension(150, 23));
        BtnCari = new Button(); BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setMnemonic('4'); BtnCari.setToolTipText("Alt+4"); BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnAll = new Button(); BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setMnemonic('M'); BtnAll.setToolTipText("Alt+M"); BtnAll.setPreferredSize(new Dimension(28, 23));
        LCount = new Label(); LCount.setText("Record : 0"); LCount.setPreferredSize(new Dimension(100, 23));

        panelBawah.add(BtnSimpan); panelBawah.add(BtnBatal); panelBawah.add(BtnHapus);
        panelBawah.add(BtnEdit); panelBawah.add(BtnPrint); panelBawah.add(BtnKeluar);
        panelBawah.add(lblTglCari); panelBawah.add(DTPCari1); panelBawah.add(lblSd);
        panelBawah.add(DTPCari2); panelBawah.add(lblKey); panelBawah.add(TCari);
        panelBawah.add(BtnCari); panelBawah.add(BtnAll); panelBawah.add(LCount);

        // ACTION LISTENERS
        BtnKeluar.addActionListener(evt -> dispose());
        BtnBatal.addActionListener(evt -> emptTeks());
        BtnCari.addActionListener(evt -> tampil());
        BtnAll.addActionListener(evt -> { TCari.setText(""); tampil(); });
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) tampil();
            }
        });

        BtnSimpan.addActionListener(evt -> {
            if (TNoRw.getText().trim().equals("")) {
                Valid.textKosong(TNoRw, "Pasien");
            } else if (TNamaPasien.getText().trim().equals("")) {
                Valid.textKosong(TNamaPasien, "Nama");
            } else if (KdDokter.getText().trim().equals("")) {
                Valid.textKosong(KdDokter, "Dokter");
            } else {
                if (Sequel.menyimpantf("pernyataan_dnr", "?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 13, new String[]{
                    TNoRw.getText(),
                    Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + " " + TglAsuhan.getSelectedItem().toString().substring(11, 19),
                    TNamaPasien.getText(), CmbJK.getSelectedItem().toString(), TTglLahir.getText(),
                    THubungan.getText(), KdDokter.getText(), TNamaSaksi.getText(), TNamaPembuat.getText(),
                    "", "", "", ""
                })) {
                    tampil(); emptTeks();
                }
            }
        });

        BtnHapus.addActionListener(evt -> {
            if (tbObat.getSelectedRow() != -1) {
                if (Sequel.queryu2tf("delete from pernyataan_dnr where no_rawat=? and tanggal=?", 2, new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
                })) { tampil(); emptTeks(); }
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dihapus...!!!!");
            }
        });

        BtnEdit.addActionListener(evt -> {
            if (TNoRw.getText().trim().equals("")) {
                Valid.textKosong(TNoRw, "Pasien");
            } else if (tbObat.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau diganti...!!!!");
            } else {
                if (Sequel.mengedittf("pernyataan_dnr", "no_rawat=? and tanggal=?",
                    "nama_pasien=?,jenis_kelamin=?,tgl_lahir=?,hubungan=?,kd_dokter=?,nama_saksi=?,nama_pembuat=?", 9, new String[]{
                    TNamaPasien.getText(), CmbJK.getSelectedItem().toString(), TTglLahir.getText(),
                    THubungan.getText(), KdDokter.getText(), TNamaSaksi.getText(), TNamaPembuat.getText(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
                })) { tampil(); emptTeks(); }
            }
        });

        BtnPrint.addActionListener(evt -> {
            if (TNoRw.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu..!!!");
            } else {
                Map<String, Object> param = new HashMap<>();
                param.put("namars", akses.getnamars());
                param.put("alamatrs", akses.getalamatrs());
                param.put("propinsirs", akses.getpropinsirs());
                param.put("kabupatenrs", akses.getkabupatenrs());
                param.put("kontakrs", akses.getkontakrs());
                param.put("emailrs", akses.getemailrs());
                param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
                String baseUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/pernyataandnr/images/";
                String tglCetak = Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + " " + TglAsuhan.getSelectedItem().toString().substring(11, 19);
                String photoPembuat = Sequel.cariIsi("select photo_pembuat from pernyataan_dnr where no_rawat='" + TNoRw.getText() + "' and tanggal='" + tglCetak + "'");
                String photoSaksi = Sequel.cariIsi("select photo_saksi from pernyataan_dnr where no_rawat='" + TNoRw.getText() + "' and tanggal='" + tglCetak + "'");
                String ttdPembuat = Sequel.cariIsi("select ttd_pembuat from pernyataan_dnr where no_rawat='" + TNoRw.getText() + "' and tanggal='" + tglCetak + "'");
                String ttdSaksi = Sequel.cariIsi("select ttd_saksi from pernyataan_dnr where no_rawat='" + TNoRw.getText() + "' and tanggal='" + tglCetak + "'");
                param.put("photo_pembuat", photoPembuat.isEmpty() ? "" : baseUrl + photoPembuat);
                param.put("photo_saksi", photoSaksi.isEmpty() ? "" : baseUrl + photoSaksi);
                param.put("ttd_pembuat", ttdPembuat.isEmpty() ? "" : baseUrl + ttdPembuat);
                param.put("ttd_saksi", ttdSaksi.isEmpty() ? "" : baseUrl + ttdSaksi);
                Valid.MyReportqry("rptPernyataanDNR.jasper", "report", "::[ Pernyataan DNR ]::",
                    "select d.*, r.no_rkm_medis, p.nm_pasien as nm_pasien_reg, dk.nm_dokter " +
                    "from pernyataan_dnr d " +
                    "inner join reg_periksa r on d.no_rawat=r.no_rawat " +
                    "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                    "left join dokter dk on d.kd_dokter=dk.kd_dokter " +
                    "where d.no_rawat='" + TNoRw.getText() + "' and d.tanggal='" + tglCetak + "'", param);
            }
        });

        // SIDEBAR - Photo & Signature
        JPanel PanelAccor = new JPanel(new BorderLayout());
        PanelAccor.setPreferredSize(new Dimension(250, 400));
        PanelAccor.setBackground(new Color(245, 250, 240));

        JPanel FormPhoto = new JPanel(new BorderLayout());
        FormPhoto.setBackground(new Color(245, 250, 240));

        JLabel lblSidebar = new JLabel("  Bukti Pernyataan");
        lblSidebar.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblSidebar.setPreferredSize(new Dimension(250, 25));
        FormPhoto.add(lblSidebar, BorderLayout.PAGE_START);

        widget.editorpane LoadHTML2 = new widget.editorpane();
        LoadHTML2.setBorder(null);
        LoadHTML2.setEditable(false);
        javax.swing.text.html.HTMLEditorKit kit = new javax.swing.text.html.HTMLEditorKit();
        LoadHTML2.setEditorKit(kit);
        javax.swing.text.html.StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule("body{background-color:#f5faf0;}");
        LoadHTML2.setDocument(kit.createDefaultDocument());
        LoadHTML2.setText("<html><body><center><br><br><font face='tahoma' size='2' color='#434343'>Pilih data untuk melihat bukti pernyataan</font></center></body></html>");

        ScrollPane Scroll5 = new ScrollPane();
        Scroll5.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(230, 230, 230)));
        Scroll5.setViewportView(LoadHTML2);
        FormPhoto.add(Scroll5, BorderLayout.CENTER);

        // Tombol buka form PHP
        JPanel FormPass3 = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        FormPass3.setBackground(new Color(245, 250, 240));
        FormPass3.setPreferredSize(new Dimension(250, 35));

        Button BtnBukaFormPHP = new Button();
        BtnBukaFormPHP.setText("Ambil Photo & TTD");
        BtnBukaFormPHP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        BtnBukaFormPHP.setPreferredSize(new Dimension(240, 30));
        BtnBukaFormPHP.addActionListener(evt -> {
            if (tbObat.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu..!!!");
            } else {
                try {
                    Sequel.queryu("delete from antripernyataandnr");
                    Sequel.queryu("insert into antripernyataandnr values('" + tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "','" + tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString() + "')");
                    String url = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB()
                            + "/pernyataandnr/index.php?act=Kamera";
                    java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
                } catch (Exception ex) {
                    System.out.println("Notif buka form PHP : " + ex);
                }
            }
        });
        FormPass3.add(BtnBukaFormPHP);
        FormPhoto.add(FormPass3, BorderLayout.PAGE_END);

        PanelAccor.add(FormPhoto, BorderLayout.CENTER);

        // Load photo/signature saat klik row tabel
        final widget.editorpane htmlPane = LoadHTML2;
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (tbObat.getSelectedRow() != -1) {
                    try {
                        String photoPembuat = "", photoSaksi = "", ttdPembuat = "", ttdSaksi = "";
                        ps = koneksi.prepareStatement("select photo_pembuat, photo_saksi, ttd_pembuat, ttd_saksi from pernyataan_dnr where no_rawat=? and tanggal=?");
                        ps.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                        ps.setString(2, tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
                        rs = ps.executeQuery();
                        if (rs.next()) {
                            photoPembuat = rs.getString("photo_pembuat") == null ? "" : rs.getString("photo_pembuat");
                            photoSaksi = rs.getString("photo_saksi") == null ? "" : rs.getString("photo_saksi");
                            ttdPembuat = rs.getString("ttd_pembuat") == null ? "" : rs.getString("ttd_pembuat");
                            ttdSaksi = rs.getString("ttd_saksi") == null ? "" : rs.getString("ttd_saksi");
                        }
                        if (rs != null) rs.close();
                        if (ps != null) ps.close();

                        String baseUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/pernyataandnr/images/";
                        if (photoPembuat.isEmpty() && photoSaksi.isEmpty() && ttdPembuat.isEmpty() && ttdSaksi.isEmpty()) {
                            htmlPane.setText("<html><body><center><br><br><font face='tahoma' size='2' color='#434343'>Belum ada bukti pernyataan</font></center></body></html>");
                        } else {
                            StringBuilder html = new StringBuilder();
                            html.append("<html><body style='font-family:tahoma;font-size:10px;'>");
                            html.append("<b>Pembuat Pernyataan</b><br>");
                            if (!photoPembuat.isEmpty()) html.append("Foto:<br><img src='").append(baseUrl).append(photoPembuat).append("' width='210' height='158'/><br>");
                            if (!ttdPembuat.isEmpty()) html.append("TTD:<br><img src='").append(baseUrl).append(ttdPembuat).append("' width='210' height='100'/><br>");
                            html.append("<hr><b>Saksi</b><br>");
                            if (!photoSaksi.isEmpty()) html.append("Foto:<br><img src='").append(baseUrl).append(photoSaksi).append("' width='210' height='158'/><br>");
                            if (!ttdSaksi.isEmpty()) html.append("TTD:<br><img src='").append(baseUrl).append(ttdSaksi).append("' width='210' height='100'/><br>");
                            html.append("</body></html>");
                            htmlPane.setText(html.toString());
                        }
                    } catch (Exception e) {
                        System.out.println("Notif load bukti : " + e);
                    }
                }
            }
        });

        // ASSEMBLY
        InternalFrame internalFrameData = new InternalFrame();
        internalFrameData.setLayout(new BorderLayout());
        internalFrameData.setBackground(new Color(255, 255, 255));
        internalFrameData.add(PanelInput, BorderLayout.NORTH);
        internalFrameData.add(Scroll, BorderLayout.CENTER);

        internalFrame1.add(panelAtas, BorderLayout.PAGE_START);
        internalFrame1.add(internalFrameData, BorderLayout.CENTER);
        internalFrame1.add(PanelAccor, BorderLayout.EAST);
        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);

        setLayout(new BorderLayout());
        add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
    }

    private void isForm() {
        if (ChkInput.isSelected()) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 320));
            FormInput.setVisible(true); ChkInput.setVisible(true);
        } else {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 20));
            FormInput.setVisible(false); ChkInput.setVisible(true);
        }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select d.no_rawat, r.no_rkm_medis, p.nm_pasien, d.tanggal, d.nama_pasien, d.jenis_kelamin, " +
                "d.tgl_lahir, d.hubungan, dk.nm_dokter, d.nama_saksi, d.nama_pembuat " +
                "from pernyataan_dnr d " +
                "inner join reg_periksa r on d.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                "left join dokter dk on d.kd_dokter=dk.kd_dokter " +
                "where d.tanggal between ? and ? and (d.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) " +
                "order by d.tanggal desc");
            try {
                ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
                ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                ps.setString(3, "%" + TCari.getText().trim() + "%");
                ps.setString(4, "%" + TCari.getText().trim() + "%");
                ps.setString(5, "%" + TCari.getText().trim() + "%");
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("tanggal"), rs.getString("nama_pasien"), rs.getString("jenis_kelamin"),
                        rs.getString("tgl_lahir"), rs.getString("hubungan"), rs.getString("nm_dokter"),
                        rs.getString("nama_saksi"), rs.getString("nama_pembuat")
                    });
                }
                LCount.setText("Record : " + tabMode.getRowCount());
            } catch (Exception e) { System.out.println("Notif DNR : " + e);
            } finally { if (rs != null) rs.close(); if (ps != null) ps.close(); }
        } catch (Exception e) { System.out.println("Notif DNR : " + e); }
    }

    public void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            try {
                TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()));
            } catch (Exception e) {}
            try {
                ps = koneksi.prepareStatement(
                    "select d.*, dk.nm_dokter from pernyataan_dnr d " +
                    "left join dokter dk on d.kd_dokter=dk.kd_dokter " +
                    "where d.no_rawat=? and d.tanggal=?");
                ps.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                ps.setString(2, tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TNamaPasien.setText(rs.getString("nama_pasien"));
                    CmbJK.setSelectedItem(rs.getString("jenis_kelamin"));
                    TTglLahir.setText(rs.getString("tgl_lahir"));
                    THubungan.setText(rs.getString("hubungan"));
                    KdDokter.setText(rs.getString("kd_dokter"));
                    NmDokter.setText(rs.getString("nm_dokter"));
                    TNamaSaksi.setText(rs.getString("nama_saksi"));
                    TNamaPembuat.setText(rs.getString("nama_pembuat"));
                }
            } catch (Exception e) { System.out.println("Notif getData DNR : " + e);
            } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
        }
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date());
        TNamaPasien.setText(""); TTglLahir.setText(""); THubungan.setText("");
        KdDokter.setText(""); NmDokter.setText("");
        TNamaSaksi.setText(""); TNamaPembuat.setText("");
    }

    public void setNoRm(String norwt, String norm, String namapasien) {
        TNoRw.setText(norwt); TNoRM.setText(norm); TPasien.setText(namapasien);
    }

    public void setNoRm(String norwt, Date tgl) {
        TNoRw.setText(norwt);
        TglAsuhan.setDate(tgl);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", norwt));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
        TNamaPasien.setText(TPasien.getText());
        TTglLahir.setText(Sequel.cariIsi("select tgl_lahir from pasien where no_rkm_medis=?", TNoRM.getText()));
        String jk = Sequel.cariIsi("select jk from pasien where no_rkm_medis=?", TNoRM.getText());
        if (jk.equals("L") || jk.equals("P")) CmbJK.setSelectedItem(jk);
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getrujukan_keluar());
        BtnHapus.setEnabled(akses.getrujukan_keluar());
        BtnEdit.setEnabled(akses.getrujukan_keluar());
        BtnPrint.setEnabled(akses.getrujukan_keluar());
    }
}
