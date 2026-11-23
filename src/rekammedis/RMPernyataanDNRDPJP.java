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
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import widget.Button;
import widget.InternalFrame;
import widget.Label;
import widget.TextBox;
import widget.ComboBox;
import widget.PanelBiasa;
import widget.ScrollPane;
import widget.panelisi;

public class RMPernyataanDNRDPJP extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2;
    private TextBox TCari;
    private Label LCount;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;

    private TextBox TNoRw, TNoRM, TPasien;
    private widget.Tanggal TglAsuhan;
    private TextBox TNamaPasien, TTglLahir, TNoTelp;
    private TextBox KdDokter, NmDokter;
    private Button BtnDokter;
    private JRadioButton RdPerintah1, RdPerintah2;
    private ComboBox CmbConsent;
    private ComboBox CmbDasar;

    private DlgCariDokter dokterDlg = new DlgCariDokter(null, false);

    private widget.Table tbObat;
    private javax.swing.table.DefaultTableModel tabMode;
    private ScrollPane Scroll;

    private widget.CekBox ChkInput;
    private PanelBiasa FormInput;
    private ScrollPane ScrollInput;
    private JPanel PanelInput;

    public widget.editorpane LoadHTML;

    public RMPernyataanDNRDPJP(java.awt.Frame parent, boolean modal) {
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
            "::[ Pernyataan DNR DPJP (Do Not Resuscitate) ]::",
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
            new String[]{"No.Rawat", "No.R.M.", "Nama Pasien", "Tanggal", "Nama", "Tgl.Lahir", "Perintah", "Consent Dari", "Dasar DNR", "Dokter PJ", "No.Telp"}
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
                if (evt.getKeyCode() == KeyEvent.VK_SPACE) getData();
            }
        });
        javax.swing.JPopupMenu popupMenu = new javax.swing.JPopupMenu();
        javax.swing.JMenuItem mnCetak = new javax.swing.JMenuItem("Cetak Pernyataan DNR DPJP");
        mnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetak.addActionListener(evt -> BtnPrint.doClick());
        popupMenu.add(mnCetak);
        tbObat.setComponentPopupMenu(popupMenu);

        Scroll.setViewportView(tbObat);

        // FORM INPUT
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(900, 370));
        FormInput.setLayout(null);

        int y = 10;
        JLabel lblHeader = new JLabel("FORMULIR PERNYATAAN DNR - DPJP (DO NOT RESUSCITATE)");
        lblHeader.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblHeader.setBounds(10, y, 500, 23); FormInput.add(lblHeader);

        y += 28;
        Label lblTgl = new Label(); lblTgl.setText("Tanggal :"); lblTgl.setBounds(10, y, 120, 23); FormInput.add(lblTgl);
        TglAsuhan = new widget.Tanggal();
        TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setBounds(135, y, 130, 23); FormInput.add(TglAsuhan);

        y += 28;
        Label lblNama = new Label(); lblNama.setText("Nama Pasien :"); lblNama.setBounds(10, y, 120, 23); FormInput.add(lblNama);
        TNamaPasien = new TextBox(); TNamaPasien.setBounds(135, y, 300, 23); FormInput.add(TNamaPasien);

        y += 28;
        Label lblTglLahir = new Label(); lblTglLahir.setText("Tanggal Lahir :"); lblTglLahir.setBounds(10, y, 120, 23); FormInput.add(lblTglLahir);
        TTglLahir = new TextBox(); TTglLahir.setBounds(135, y, 120, 23); FormInput.add(TTglLahir);

        y += 33;
        JLabel lblPerintah = new JLabel("Perintah DPJP :"); lblPerintah.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblPerintah.setBounds(10, y, 120, 23); FormInput.add(lblPerintah);

        y += 25;
        RdPerintah1 = new JRadioButton("1. Usaha komprehensif mencegah henti jantung/nafas tanpa intubasi, TIDAK DILAKUKAN RJP");
        RdPerintah1.setBounds(10, y, 600, 23); RdPerintah1.setSelected(true); RdPerintah1.setOpaque(false); FormInput.add(RdPerintah1);

        y += 23;
        RdPerintah2 = new JRadioButton("2. Usaha suportif sebelum henti nafas/jantung (jalan nafas non invasif, kontrol pendarahan, anti nyeri), TIDAK RJP");
        RdPerintah2.setBounds(10, y, 700, 23); RdPerintah2.setOpaque(false); FormInput.add(RdPerintah2);

        ButtonGroup bgPerintah = new ButtonGroup();
        bgPerintah.add(RdPerintah1); bgPerintah.add(RdPerintah2);

        y += 30;
        JLabel lblConsent = new JLabel("Informed consent diperoleh dari :"); lblConsent.setBounds(10, y, 210, 23); FormInput.add(lblConsent);
        CmbConsent = new ComboBox();
        CmbConsent.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "Pasien", "Tenaga kesehatan yang ditunjuk pasien",
            "Wali yang sah atas pasien (termasuk yang ditunjuk pengadilan)", "Anggota keluarga pasien"
        }));
        CmbConsent.setBounds(225, y, 400, 23); FormInput.add(CmbConsent);

        y += 28;
        JLabel lblDasar = new JLabel("Dasar perintah DNR :"); lblDasar.setBounds(10, y, 140, 23); FormInput.add(lblDasar);
        CmbDasar = new ComboBox();
        CmbDasar.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "Informed consent dari pasien/keluarga",
            "Instruksi pasien sebelumnya",
            "Keputusan dua orang dokter bahwa RJP tidak efektif"
        }));
        CmbDasar.setBounds(155, y, 400, 23); FormInput.add(CmbDasar);

        y += 33;
        JLabel lblDPJP = new JLabel("Dokter Penanggung Jawab :"); lblDPJP.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblDPJP.setBounds(10, y, 200, 23); FormInput.add(lblDPJP);

        y += 25;
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
                    TNoTelp.setText(Sequel.cariIsi("select no_hp from dokter where kd_dokter=?", KdDokter.getText()));
                }
            }
        });

        y += 28;
        Label lblTelp = new Label(); lblTelp.setText("Nomor Telepon :"); lblTelp.setBounds(10, y, 120, 23); FormInput.add(lblTelp);
        TNoTelp = new TextBox(); TNoTelp.setBounds(135, y, 150, 23); FormInput.add(TNoTelp);

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
        ScrollInput.setPreferredSize(new Dimension(900, 390));

        PanelInput = new JPanel(new BorderLayout());
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new Dimension(WIDTH, 410));
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
            if (TNoRw.getText().trim().equals("")) { Valid.textKosong(TNoRw, "Pasien");
            } else if (KdDokter.getText().trim().equals("")) { Valid.textKosong(KdDokter, "Dokter PJ");
            } else {
                if (Sequel.menyimpantf("pernyataan_dnr_dpjp", "?,?,?,?,?,?,?,?,?,?,?", "Data", 11, new String[]{
                    TNoRw.getText(),
                    Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + " " + TglAsuhan.getSelectedItem().toString().substring(11, 19),
                    TNamaPasien.getText(), TTglLahir.getText(),
                    RdPerintah1.isSelected() ? "1" : "2",
                    CmbConsent.getSelectedItem().toString(), CmbDasar.getSelectedItem().toString(),
                    KdDokter.getText(), TNoTelp.getText(), "", ""
                })) { tampil(); emptTeks(); }
            }
        });

        BtnHapus.addActionListener(evt -> {
            if (tbObat.getSelectedRow() != -1) {
                if (Sequel.queryu2tf("delete from pernyataan_dnr_dpjp where no_rawat=? and tanggal=?", 2, new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
                })) { tampil(); emptTeks(); }
            } else { JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dihapus...!!!!"); }
        });

        BtnEdit.addActionListener(evt -> {
            if (TNoRw.getText().trim().equals("")) { Valid.textKosong(TNoRw, "Pasien");
            } else if (tbObat.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau diganti...!!!!");
            } else {
                if (Sequel.mengedittf("pernyataan_dnr_dpjp", "no_rawat=? and tanggal=?",
                    "nama_pasien=?,tgl_lahir=?,perintah_dnr=?,informed_consent_dari=?,dasar_dnr=?,kd_dokter=?,no_telp=?", 9, new String[]{
                    TNamaPasien.getText(), TTglLahir.getText(),
                    RdPerintah1.isSelected() ? "1" : "2",
                    CmbConsent.getSelectedItem().toString(), CmbDasar.getSelectedItem().toString(),
                    KdDokter.getText(), TNoTelp.getText(),
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
                Valid.MyReportqry("rptPernyataanDNRDPJP.jasper", "report", "::[ Pernyataan DNR DPJP ]::",
                    "select d.*, r.no_rkm_medis, p.nm_pasien as nm_pasien_reg, dk.nm_dokter " +
                    "from pernyataan_dnr_dpjp d " +
                    "inner join reg_periksa r on d.no_rawat=r.no_rawat " +
                    "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                    "left join dokter dk on d.kd_dokter=dk.kd_dokter " +
                    "where d.no_rawat='" + TNoRw.getText() + "' " +
                    "and d.tanggal='" + Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + " " + TglAsuhan.getSelectedItem().toString().substring(11, 19) + "'", param);
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
        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);

        setLayout(new BorderLayout());
        add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
    }

    private void isForm() {
        if (ChkInput.isSelected()) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 410));
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
                "select d.no_rawat, r.no_rkm_medis, p.nm_pasien, d.tanggal, d.nama_pasien, d.tgl_lahir, " +
                "d.perintah_dnr, d.informed_consent_dari, d.dasar_dnr, dk.nm_dokter, d.no_telp " +
                "from pernyataan_dnr_dpjp d " +
                "inner join reg_periksa r on d.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                "left join dokter dk on d.kd_dokter=dk.kd_dokter " +
                "where d.tanggal between ? and ? and (d.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) " +
                "order by d.tanggal desc");
            try {
                ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                ps.setString(3, "%" + TCari.getText().trim() + "%");
                ps.setString(4, "%" + TCari.getText().trim() + "%");
                ps.setString(5, "%" + TCari.getText().trim() + "%");
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("tanggal"), rs.getString("nama_pasien"), rs.getString("tgl_lahir"),
                        rs.getString("perintah_dnr"), rs.getString("informed_consent_dari"),
                        rs.getString("dasar_dnr"), rs.getString("nm_dokter"), rs.getString("no_telp")
                    });
                }
                LCount.setText("Record : " + tabMode.getRowCount());
            } catch (Exception e) { System.out.println("Notif DNR DPJP : " + e);
            } finally { if (rs != null) rs.close(); if (ps != null) ps.close(); }
        } catch (Exception e) { System.out.println("Notif DNR DPJP : " + e); }
    }

    public void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            try { TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString())); } catch (Exception e) {}
            try {
                ps = koneksi.prepareStatement(
                    "select d.*, dk.nm_dokter from pernyataan_dnr_dpjp d " +
                    "left join dokter dk on d.kd_dokter=dk.kd_dokter " +
                    "where d.no_rawat=? and d.tanggal=?");
                ps.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                ps.setString(2, tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TNamaPasien.setText(rs.getString("nama_pasien"));
                    TTglLahir.setText(rs.getString("tgl_lahir"));
                    if ("2".equals(rs.getString("perintah_dnr"))) { RdPerintah2.setSelected(true); } else { RdPerintah1.setSelected(true); }
                    CmbConsent.setSelectedItem(rs.getString("informed_consent_dari"));
                    CmbDasar.setSelectedItem(rs.getString("dasar_dnr"));
                    KdDokter.setText(rs.getString("kd_dokter"));
                    NmDokter.setText(rs.getString("nm_dokter"));
                    TNoTelp.setText(rs.getString("no_telp"));
                }
            } catch (Exception e) { System.out.println("Notif getData DNR DPJP : " + e);
            } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
        }
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date());
        TNamaPasien.setText(""); TTglLahir.setText("");
        RdPerintah1.setSelected(true);
        KdDokter.setText(""); NmDokter.setText(""); TNoTelp.setText("");
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
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getrujukan_keluar());
        BtnHapus.setEnabled(akses.getrujukan_keluar());
        BtnEdit.setEnabled(akses.getrujukan_keluar());
        BtnPrint.setEnabled(akses.getrujukan_keluar());
    }
}
