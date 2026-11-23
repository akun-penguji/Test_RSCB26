package rekammedis;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import kepegawaian.DlgCariPetugas;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import widget.Button;
import widget.InternalFrame;
import widget.Label;
import widget.TextBox;
import widget.CekBox;
import widget.ComboBox;
import widget.PanelBiasa;
import widget.ScrollPane;
import widget.TextArea;
import widget.panelisi;

public class RMCatatanPerkembanganKeperawatan extends JDialog {

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
    private TextBox TNoRw, TNoRM, TPasien, TPavKamar;
    private widget.Tanggal TglAsuhan;

    // Tabs
    private JTabbedPane TabRawat;

    // Per-shift fields (0=Pagi, 1=Sore, 2=Malam)
    private TextArea[] TSubjektif = new TextArea[3];

    // Objektif Left
    private ComboBox[] CmbSesak = new ComboBox[3];
    private TextBox[] TRR = new TextBox[3];
    private TextBox[] TO2 = new TextBox[3];
    private TextBox[] TSpO2 = new TextBox[3];
    private TextBox[] TSuaraNapas = new TextBox[3];
    private TextBox[] TSekret = new TextBox[3];
    private TextBox[] TPerfusi = new TextBox[3];
    private TextBox[] TNadi = new TextBox[3];
    private TextBox[] TTDSistolik = new TextBox[3];
    private TextBox[] TTDDiastolik = new TextBox[3];
    private TextBox[] TSuhu = new TextBox[3];
    private CekBox[] ChkPerdarahan = new CekBox[3];
    private TextBox[] TVolPerdarahan = new TextBox[3];
    private TextBox[] TInfus = new TextBox[3];
    private TextBox[] TGCSE = new TextBox[3];
    private TextBox[] TGCSV = new TextBox[3];
    private TextBox[] TGCSM = new TextBox[3];
    private ComboBox[] CmbPupil = new ComboBox[3];
    private TextBox[] TKesadaran = new TextBox[3];
    private TextBox[] TSkalaNyeri = new TextBox[3];
    private TextBox[] TMobilisasi = new TextBox[3];
    private TextBox[] TMorseFall = new TextBox[3];

    // Objektif Right
    private CekBox[] ChkHemiparaseTidak = new CekBox[3];
    private CekBox[] ChkHemiparaseYa = new CekBox[3];
    private CekBox[] ChkParapareseTidak = new CekBox[3];
    private CekBox[] ChkParaparaseYa = new CekBox[3];
    private CekBox[] ChkKateterTidak = new CekBox[3];
    private CekBox[] ChkKateterYa = new CekBox[3];
    private TextBox[] TProdUrineCC = new TextBox[3];
    private TextBox[] TProdUrineJam = new TextBox[3];
    private TextBox[] TWarnaUrine = new TextBox[3];
    private TextBox[] TMakan = new TextBox[3];
    private TextBox[] TMinum = new TextBox[3];
    private TextBox[] TSonde = new TextBox[3];
    private TextBox[] TMuntah = new TextBox[3];
    private TextBox[] TCMS = new TextBox[3];
    private CekBox[] ChkKembungTidak = new CekBox[3];
    private CekBox[] ChkKembungYa = new CekBox[3];
    private TextBox[] TBisingUsus = new TextBox[3];
    private CekBox[] ChkFlatusTidak = new CekBox[3];
    private CekBox[] ChkFlatusYa = new CekBox[3];
    private TextBox[] TDiare = new TextBox[3];
    private CekBox[] ChkLukaTidak = new CekBox[3];
    private CekBox[] ChkLukaYa = new CekBox[3];
    private TextBox[] TCairanMasuk = new TextBox[3];
    private TextBox[] TCairanKeluar = new TextBox[3];
    private TextBox[] TBalanceCairan = new TextBox[3];
    private TextBox[] TLainLain = new TextBox[3];
    private TextBox[] TPemeriksaPenunjang = new TextBox[3];

    // Assessment
    private TextArea[] TMasalahKeperawatan = new TextArea[3];
    private ComboBox[] CmbStatus = new ComboBox[3];
    private TextArea[] TMasalahBaru = new TextArea[3];

    // Planning
    private CekBox[] ChkObsTTV = new CekBox[3];
    private CekBox[] ChkObsInfeksi = new CekBox[3];
    private CekBox[] ChkKajiKesadaran = new CekBox[3];
    private CekBox[] ChkKajiNyeri = new CekBox[3];
    private CekBox[] ChkPersiapanTindakan = new CekBox[3];
    private TextBox[] TPersiapanTindakan = new TextBox[3];
    private CekBox[] ChkObsCairan = new CekBox[3];
    private CekBox[] ChkBalansCairan = new CekBox[3];
    private CekBox[] ChkObsKeluhan = new CekBox[3];
    private CekBox[] ChkKajiJatuh = new CekBox[3];
    private TextArea[] TKolaborasi = new TextArea[3];
    private TextArea[] TCatatanPenting = new TextArea[3];

    // PJ
    private TextBox[] KdPJKaTim = new TextBox[3];
    private TextBox[] NmPJKaTim = new TextBox[3];
    private Button[] BtnPJKaTim = new Button[3];
    private TextBox[] KdPJSerahTerima = new TextBox[3];
    private TextBox[] NmPJSerahTerima = new TextBox[3];
    private Button[] BtnPJSerahTerima = new Button[3];

    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);

    // Table
    private widget.Table tbObat;
    private javax.swing.table.DefaultTableModel tabMode;
    private ScrollPane Scroll;

    // Input panel per tab
    private CekBox[] ChkInput = new CekBox[3];
    private PanelBiasa[] FormInput = new PanelBiasa[3];
    private ScrollPane[] ScrollInput = new ScrollPane[3];
    private JPanel[] PanelInput = new JPanel[3];

    public widget.editorpane LoadHTML;

    // Track which petugas field to fill
    private int petugasTarget = 0; // 0-5

    public RMCatatanPerkembanganKeperawatan(java.awt.Frame parent, boolean modal) {
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
            "::[ Catatan Perkembangan Keperawatan ]::",
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
        Label lblTgl = new Label(); lblTgl.setText("Tanggal :");
        TglAsuhan = new widget.Tanggal();
        TglAsuhan.setDisplayFormat("dd-MM-yyyy");
        TglAsuhan.setPreferredSize(new Dimension(90, 23));
        Label lblPav = new Label(); lblPav.setText("Pav/Kamar :");
        TPavKamar = new TextBox(); TPavKamar.setPreferredSize(new Dimension(150, 23));

        panelAtas.add(lblNoRw); panelAtas.add(TNoRw); panelAtas.add(TNoRM); panelAtas.add(TPasien);
        panelAtas.add(lblTgl); panelAtas.add(TglAsuhan);
        panelAtas.add(lblPav); panelAtas.add(TPavKamar);

        // TABLE
        Scroll = new ScrollPane();
        tbObat = new widget.Table();
        tabMode = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat", "No.R.M.", "Nama Pasien", "Tanggal", "Shift", "PJ/Ka.Tim"}
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
        Scroll.setViewportView(tbObat);

        // TABS
        TabRawat = new JTabbedPane();
        String[] shiftNames = {"Dinas Pagi", "Dinas Sore", "Dinas Malam"};
        for (int i = 0; i < 3; i++) {
            buildShiftTab(i);
            JPanel tabPanel = new JPanel(new BorderLayout());
            tabPanel.setOpaque(false);
            tabPanel.add(PanelInput[i], BorderLayout.NORTH);
            tabPanel.add(new JLabel(""), BorderLayout.CENTER);
            TabRawat.addTab(shiftNames[i], tabPanel);
        }

        // Petugas dialog listener
        petugasDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (petugasDlg.getTable().getSelectedRow() != -1) {
                    String kd = petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(), 0).toString();
                    String nm = petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(), 1).toString();
                    int tab = petugasTarget / 2;
                    int field = petugasTarget % 2;
                    if (field == 0) {
                        KdPJKaTim[tab].setText(kd);
                        NmPJKaTim[tab].setText(nm);
                    } else {
                        KdPJSerahTerima[tab].setText(kd);
                        NmPJSerahTerima[tab].setText(nm);
                    }
                }
            }
        });

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
            int idx = TabRawat.getSelectedIndex();
            String shift = idx == 0 ? "Pagi" : (idx == 1 ? "Sore" : "Malam");
            if (TNoRw.getText().trim().equals("")) {
                Valid.textKosong(TNoRw, "Pasien");
            } else if (KdPJKaTim[idx].getText().trim().equals("")) {
                Valid.textKosong(KdPJKaTim[idx], "PJ/Ka.Tim");
            } else {
                // 60 kolom sesuai DB
                if (Sequel.menyimpantf("catatan_perkembangan_keperawatan",
                    "?,?,?,?,?,?,?,?,?,?," + // 1-10
                    "?,?,?,?,?,?,?,?,?,?," + // 11-20
                    "?,?,?,?,?,?,?,?,?,?," + // 21-30
                    "?,?,?,?,?,?,?,?,?,?," + // 31-40
                    "?,?,?,?,?,?,?,?,?,?," + // 41-50
                    "?,?,?,?,?,?,?,?,?,?",   // 51-60
                    "Data", 60, new String[]{
                    TNoRw.getText(),                                              // 1 no_rawat
                    Valid.SetTgl(TglAsuhan.getSelectedItem() + ""),               // 2 tanggal
                    shift,                                                         // 3 shift
                    TSubjektif[idx].getText(),                                     // 4 subjektif
                    CmbSesak[idx].getSelectedItem().toString(),                    // 5 sesak
                    TRR[idx].getText(),                                            // 6 rr
                    TO2[idx].getText(),                                            // 7 o2
                    TSpO2[idx].getText(),                                          // 8 spo2
                    TSuaraNapas[idx].getText(),                                    // 9 suara_napas
                    TSekret[idx].getText(),                                        // 10 sekret
                    TPerfusi[idx].getText(),                                       // 11 perfusi
                    TNadi[idx].getText(),                                          // 12 nadi
                    TTDSistolik[idx].getText()+"/"+TTDDiastolik[idx].getText(),     // 13 td
                    TSuhu[idx].getText(),                                          // 14 suhu
                    ChkHemiparaseYa[idx].isSelected()?"Ya":"Tidak",                // 15 hemiparase
                    ChkParaparaseYa[idx].isSelected()?"Ya":"Tidak",                // 16 paraparese
                    ChkKateterYa[idx].isSelected()?"Ya":"Tidak",                   // 17 kateter_kondom
                    TProdUrineCC[idx].getText()+"/"+TProdUrineJam[idx].getText(),   // 18 prod_urine
                    TWarnaUrine[idx].getText(),                                    // 19 warna_urine
                    TMakan[idx].getText(),                                         // 20 makan_porsi
                    TMinum[idx].getText(),                                         // 21 minum
                    TSonde[idx].getText(),                                         // 22 sonde
                    TMuntah[idx].getText(),                                        // 23 muntah
                    TCMS[idx].getText(),                                           // 24 cms
                    ChkPerdarahan[idx].isSelected()?"Ya":"Tidak",                  // 25 perdarahan
                    TVolPerdarahan[idx].getText(),                                 // 26 volume_perdarahan
                    TInfus[idx].getText(),                                        // 27 infus
                    TGCSE[idx].getText()+"/"+TGCSV[idx].getText()+"/"+TGCSM[idx].getText(), // 28 gcs
                    CmbPupil[idx].getSelectedItem().toString(),                    // 29 pupil
                    TKesadaran[idx].getText(),                                     // 30 kesadaran_obj
                    TSkalaNyeri[idx].getText(),                                    // 31 skala_nyeri
                    TMobilisasi[idx].getText(),                                    // 32 mobilisasi
                    TMorseFall[idx].getText(),                                     // 33 morse_fall_scale
                    ChkKembungYa[idx].isSelected()?"Ya":"Tidak",                   // 34 kembung
                    TBisingUsus[idx].getText(),                                    // 35 bising_usus
                    ChkFlatusYa[idx].isSelected()?"Ya":"Tidak",                    // 36 flatus
                    TDiare[idx].getText(),                                        // 37 diare
                    ChkLukaYa[idx].isSelected()?"Ya":"Tidak",                      // 38 luka
                    TCairanMasuk[idx].getText(),                                   // 39 cairan_masuk
                    TCairanKeluar[idx].getText(),                                  // 40 cairan_keluar
                    TBalanceCairan[idx].getText(),                                 // 41 balance_cairan
                    TLainLain[idx].getText(),                                      // 42 lain_lain
                    TPemeriksaPenunjang[idx].getText(),                            // 43 pemeriksa_penunjang
                    TMasalahKeperawatan[idx].getText(),                            // 44 masalah_keperawatan
                    CmbStatus[idx].getSelectedItem().toString(),                   // 45 status_masalah
                    TMasalahBaru[idx].getText(),                                   // 46 masalah_baru
                    ChkObsTTV[idx].isSelected()?"Ya":"Tidak",                      // 47 obs_tanda_vital
                    ChkObsInfeksi[idx].isSelected()?"Ya":"Tidak",                  // 48 obs_tanda_infeksi
                    ChkKajiKesadaran[idx].isSelected()?"Ya":"Tidak",               // 49 kaji_kesadaran
                    ChkKajiNyeri[idx].isSelected()?"Ya":"Tidak",                   // 50 kaji_nyeri
                    TPersiapanTindakan[idx].getText(),                             // 51 persiapan_tindakan
                    ChkObsCairan[idx].isSelected()?"Ya":"Tidak",                   // 52 obs_cairan
                    ChkBalansCairan[idx].isSelected()?"Ya":"Tidak",                // 53 ukur_balans
                    ChkObsKeluhan[idx].isSelected()?"Ya":"Tidak",                  // 54 obs_keluhan
                    ChkKajiJatuh[idx].isSelected()?"Ya":"Tidak",                   // 55 kaji_resiko_jatuh
                    TKolaborasi[idx].getText(),                                    // 56 kolaborasi
                    TCatatanPenting[idx].getText(),                                // 57 catatan_penting
                    KdPJKaTim[idx].getText(),                                      // 58 nip_pj
                    KdPJSerahTerima[idx].getText(),                                // 59 nip_pj_serah
                    KdPJKaTim[idx].getText()                                       // 60 nip
                })) {
                    tampil(); emptTeks();
                }
            }
        });

        BtnHapus.addActionListener(evt -> {
            if (tbObat.getSelectedRow() != -1) {
                if (Sequel.queryu2tf("delete from catatan_perkembangan_keperawatan where no_rawat=? and tanggal=? and shift=?", 3, new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString()
                })) { tampil(); emptTeks(); }
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dihapus...!!!!");
            }
        });

        BtnEdit.addActionListener(evt -> {
            int idx = TabRawat.getSelectedIndex();
            if (TNoRw.getText().trim().equals("")) {
                Valid.textKosong(TNoRw, "Pasien");
            } else if (tbObat.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau diganti...!!!!");
            } else {
                // Edit: delete lalu simpan ulang
                if (Sequel.queryu2tf("delete from catatan_perkembangan_keperawatan where no_rawat=? and tanggal=? and shift=?", 3, new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString()
                })) {
                    BtnSimpan.doClick();
                }
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
                Valid.MyReportqry("rptCatatanPerkembanganKeperawatan.jasper", "report",
                    "::[ Catatan Perkembangan Keperawatan ]::",
                    "select cpk.*, r.no_rkm_medis, p.nm_pasien, " +
                    "pt1.nama as nm_pj_katim, pt2.nama as nm_pj_serah_terima " +
                    "from catatan_perkembangan_keperawatan cpk " +
                    "inner join reg_periksa r on cpk.no_rawat=r.no_rawat " +
                    "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                    "left join petugas pt1 on cpk.nip_pj=pt1.nip " +
                    "left join petugas pt2 on cpk.nip_pj_serah=pt2.nip " +
                    "where cpk.no_rawat='" + TNoRw.getText() + "' " +
                    "and cpk.tanggal='" + Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + "' " +
                    "order by cpk.shift", param);
            }
        });

        // ASSEMBLY
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(TabRawat, BorderLayout.NORTH);
        centerPanel.add(Scroll, BorderLayout.CENTER);

        internalFrame1.add(panelAtas, BorderLayout.PAGE_START);
        internalFrame1.add(centerPanel, BorderLayout.CENTER);
        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);

        setLayout(new BorderLayout());
        add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
    }

    private void buildShiftTab(int idx) {
        FormInput[idx] = new PanelBiasa();
        FormInput[idx].setPreferredSize(new Dimension(1150, 1380));
        FormInput[idx].setLayout(null);

        int y = 10;
        int leftCol = 10;
        int rightCol = 570;
        int lblW = 140;
        int fldX = 155;
        int fldXR = 715;
        int lblWR = 140;

        // === S (SUBJEKTIF) ===
        JLabel lblS = new JLabel("S (SUBJEKTIF)");
        lblS.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblS.setBounds(leftCol, y, 200, 23); FormInput[idx].add(lblS);

        y += 25;
        Label lblSubjektif = new Label(); lblSubjektif.setText("Subjektif :"); lblSubjektif.setBounds(leftCol, y, lblW, 23); FormInput[idx].add(lblSubjektif);
        TSubjektif[idx] = new TextArea();
        TSubjektif[idx].setBorder(javax.swing.BorderFactory.createLineBorder(new Color(180, 180, 180)));
        ScrollPane scrSubjektif = new ScrollPane();
        scrSubjektif.setViewportView(TSubjektif[idx]);
        scrSubjektif.setBounds(fldX, y, 400, 50); FormInput[idx].add(scrSubjektif);

        y += 60;
        // === O (OBJEKTIF) ===
        JLabel lblO = new JLabel("O (OBJEKTIF)");
        lblO.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblO.setBounds(leftCol, y, 200, 23); FormInput[idx].add(lblO);

        y += 28;
        int oy = y; // save start y for right column

        // Left column objektif
        Label lblSesak = new Label(); lblSesak.setText("Sesak :"); lblSesak.setBounds(leftCol, y, lblW, 23); FormInput[idx].add(lblSesak);
        CmbSesak[idx] = new ComboBox();
        CmbSesak[idx].setModel(new javax.swing.DefaultComboBoxModel(new String[]{"+", "-"}));
        CmbSesak[idx].setBounds(fldX, y, 50, 23); FormInput[idx].add(CmbSesak[idx]);

        y += 28;
        Label lblRR = new Label(); lblRR.setText("RR :"); lblRR.setBounds(leftCol, y, lblW, 23); FormInput[idx].add(lblRR);
        TRR[idx] = new TextBox(); TRR[idx].setBounds(fldX, y, 60, 23); FormInput[idx].add(TRR[idx]);
        JLabel lblRRu = new JLabel("x/mnt"); lblRRu.setBounds(220, y, 40, 23); FormInput[idx].add(lblRRu);

        y += 28;
        Label lblO2 = new Label(); lblO2.setText("O2 :"); lblO2.setBounds(leftCol, y, lblW, 23); FormInput[idx].add(lblO2);
        TO2[idx] = new TextBox(); TO2[idx].setBounds(fldX, y, 60, 23); FormInput[idx].add(TO2[idx]);
        JLabel lblO2u = new JLabel("Lt/mnt"); lblO2u.setBounds(220, y, 50, 23); FormInput[idx].add(lblO2u);

        y += 28;
        Label lblSpO2 = new Label(); lblSpO2.setText("SpO2 :"); lblSpO2.setBounds(leftCol, y, lblW, 23); FormInput[idx].add(lblSpO2);
        TSpO2[idx] = new TextBox(); TSpO2[idx].setBounds(fldX, y, 60, 23); FormInput[idx].add(TSpO2[idx]);
        JLabel lblSpO2u = new JLabel("%"); lblSpO2u.setBounds(220, y, 30, 23); FormInput[idx].add(lblSpO2u);

        y += 28;
        Label lblSuaraNapas = new Label(); lblSuaraNapas.setText("Suara napas :"); lblSuaraNapas.setBounds(leftCol, y, lblW, 23); FormInput[idx].add(lblSuaraNapas);
        TSuaraNapas[idx] = new TextBox(); TSuaraNapas[idx].setBounds(fldX, y, 150, 23); FormInput[idx].add(TSuaraNapas[idx]);

        y += 28;
        Label lblSekret = new Label(); lblSekret.setText("Sekret :"); lblSekret.setBounds(leftCol, y, lblW, 23); FormInput[idx].add(lblSekret);
        TSekret[idx] = new TextBox(); TSekret[idx].setBounds(fldX, y, 150, 23); FormInput[idx].add(TSekret[idx]);

        y += 28;
        Label lblPerfusi = new Label(); lblPerfusi.setText("Perfusi :"); lblPerfusi.setBounds(leftCol, y, lblW, 23); FormInput[idx].add(lblPerfusi);
        TPerfusi[idx] = new TextBox(); TPerfusi[idx].setBounds(fldX, y, 150, 23); FormInput[idx].add(TPerfusi[idx]);

        y += 28;
        Label lblNadi = new Label(); lblNadi.setText("Nadi :"); lblNadi.setBounds(leftCol, y, lblW, 23); FormInput[idx].add(lblNadi);
        TNadi[idx] = new TextBox(); TNadi[idx].setBounds(fldX, y, 60, 23); FormInput[idx].add(TNadi[idx]);
        JLabel lblNadiU = new JLabel("x/mnt"); lblNadiU.setBounds(220, y, 40, 23); FormInput[idx].add(lblNadiU);

        y += 28;
        Label lblTD = new Label(); lblTD.setText("TD :"); lblTD.setBounds(leftCol, y, lblW, 23); FormInput[idx].add(lblTD);
        TTDSistolik[idx] = new TextBox(); TTDSistolik[idx].setBounds(fldX, y, 50, 23); FormInput[idx].add(TTDSistolik[idx]);
        JLabel lblSlash = new JLabel("/"); lblSlash.setBounds(208, y, 10, 23); FormInput[idx].add(lblSlash);
        TTDDiastolik[idx] = new TextBox(); TTDDiastolik[idx].setBounds(220, y, 50, 23); FormInput[idx].add(TTDDiastolik[idx]);
        JLabel lblTDu = new JLabel("mmHg"); lblTDu.setBounds(275, y, 50, 23); FormInput[idx].add(lblTDu);

        y += 28;
        Label lblSuhu = new Label(); lblSuhu.setText("Suhu :"); lblSuhu.setBounds(leftCol, y, lblW, 23); FormInput[idx].add(lblSuhu);
        TSuhu[idx] = new TextBox(); TSuhu[idx].setBounds(fldX, y, 60, 23); FormInput[idx].add(TSuhu[idx]);
        JLabel lblSuhuU = new JLabel("\u00B0C"); lblSuhuU.setBounds(220, y, 30, 23); FormInput[idx].add(lblSuhuU);

        y += 28;
        Label lblPerdarahan = new Label(); lblPerdarahan.setText("Perdarahan :"); lblPerdarahan.setBounds(leftCol, y, lblW, 23); FormInput[idx].add(lblPerdarahan);
        ChkPerdarahan[idx] = new CekBox(); ChkPerdarahan[idx].setText(""); ChkPerdarahan[idx].setBounds(fldX, y, 23, 23); FormInput[idx].add(ChkPerdarahan[idx]);
        JLabel lblVol = new JLabel("Volume :"); lblVol.setBounds(185, y, 55, 23); FormInput[idx].add(lblVol);
        TVolPerdarahan[idx] = new TextBox(); TVolPerdarahan[idx].setBounds(245, y, 60, 23); FormInput[idx].add(TVolPerdarahan[idx]);
        JLabel lblVolU = new JLabel("cc"); lblVolU.setBounds(310, y, 20, 23); FormInput[idx].add(lblVolU);

        y += 28;
        Label lblInfus = new Label(); lblInfus.setText("Infus :"); lblInfus.setBounds(leftCol, y, lblW, 23); FormInput[idx].add(lblInfus);
        TInfus[idx] = new TextBox(); TInfus[idx].setBounds(fldX, y, 200, 23); FormInput[idx].add(TInfus[idx]);

        y += 28;
        Label lblGCS = new Label(); lblGCS.setText("GCS :"); lblGCS.setBounds(leftCol, y, lblW, 23); FormInput[idx].add(lblGCS);
        TGCSE[idx] = new TextBox(); TGCSE[idx].setBounds(fldX, y, 35, 23); FormInput[idx].add(TGCSE[idx]);
        JLabel lblS1 = new JLabel("/"); lblS1.setBounds(193, y, 10, 23); FormInput[idx].add(lblS1);
        TGCSV[idx] = new TextBox(); TGCSV[idx].setBounds(205, y, 35, 23); FormInput[idx].add(TGCSV[idx]);
        JLabel lblS2 = new JLabel("/"); lblS2.setBounds(243, y, 10, 23); FormInput[idx].add(lblS2);
        TGCSM[idx] = new TextBox(); TGCSM[idx].setBounds(255, y, 35, 23); FormInput[idx].add(TGCSM[idx]);

        y += 28;
        Label lblPupil = new Label(); lblPupil.setText("Pupil :"); lblPupil.setBounds(leftCol, y, lblW, 23); FormInput[idx].add(lblPupil);
        CmbPupil[idx] = new ComboBox();
        CmbPupil[idx].setModel(new javax.swing.DefaultComboBoxModel(new String[]{"isokor", "an isokor"}));
        CmbPupil[idx].setBounds(fldX, y, 100, 23); FormInput[idx].add(CmbPupil[idx]);

        y += 28;
        Label lblKesadaran = new Label(); lblKesadaran.setText("Kesadaran :"); lblKesadaran.setBounds(leftCol, y, lblW, 23); FormInput[idx].add(lblKesadaran);
        TKesadaran[idx] = new TextBox(); TKesadaran[idx].setBounds(fldX, y, 150, 23); FormInput[idx].add(TKesadaran[idx]);

        y += 28;
        Label lblSkalaNyeri = new Label(); lblSkalaNyeri.setText("Skala nyeri :"); lblSkalaNyeri.setBounds(leftCol, y, lblW, 23); FormInput[idx].add(lblSkalaNyeri);
        TSkalaNyeri[idx] = new TextBox(); TSkalaNyeri[idx].setBounds(fldX, y, 60, 23); FormInput[idx].add(TSkalaNyeri[idx]);

        y += 28;
        Label lblMobilisasi = new Label(); lblMobilisasi.setText("Mobilisasi :"); lblMobilisasi.setBounds(leftCol, y, lblW, 23); FormInput[idx].add(lblMobilisasi);
        TMobilisasi[idx] = new TextBox(); TMobilisasi[idx].setBounds(fldX, y, 150, 23); FormInput[idx].add(TMobilisasi[idx]);

        y += 28;
        Label lblMorse = new Label(); lblMorse.setText("Morse fall scale :"); lblMorse.setBounds(leftCol, y, lblW, 23); FormInput[idx].add(lblMorse);
        TMorseFall[idx] = new TextBox(); TMorseFall[idx].setBounds(fldX, y, 60, 23); FormInput[idx].add(TMorseFall[idx]);

        // Right column objektif - start from saved oy
        int ry = oy;
        Label lblHemi = new Label(); lblHemi.setText("Hemiparase/Hemiplegi :"); lblHemi.setBounds(rightCol, ry, 140, 23); FormInput[idx].add(lblHemi);
        ChkHemiparaseTidak[idx] = new CekBox(); ChkHemiparaseTidak[idx].setText("Tidak"); ChkHemiparaseTidak[idx].setBounds(fldXR, ry, 65, 23); FormInput[idx].add(ChkHemiparaseTidak[idx]);
        ChkHemiparaseYa[idx] = new CekBox(); ChkHemiparaseYa[idx].setText("Ya"); ChkHemiparaseYa[idx].setBounds(fldXR + 70, ry, 50, 23); FormInput[idx].add(ChkHemiparaseYa[idx]);
        ChkHemiparaseTidak[idx].addActionListener(evt -> { if (ChkHemiparaseTidak[idx].isSelected()) ChkHemiparaseYa[idx].setSelected(false); });
        ChkHemiparaseYa[idx].addActionListener(evt -> { if (ChkHemiparaseYa[idx].isSelected()) ChkHemiparaseTidak[idx].setSelected(false); });

        ry += 28;
        Label lblPara = new Label(); lblPara.setText("Paraparese/paraplegi :"); lblPara.setBounds(rightCol, ry, 140, 23); FormInput[idx].add(lblPara);
        ChkParapareseTidak[idx] = new CekBox(); ChkParapareseTidak[idx].setText("Tidak"); ChkParapareseTidak[idx].setBounds(fldXR, ry, 65, 23); FormInput[idx].add(ChkParapareseTidak[idx]);
        ChkParaparaseYa[idx] = new CekBox(); ChkParaparaseYa[idx].setText("Ya"); ChkParaparaseYa[idx].setBounds(fldXR + 70, ry, 50, 23); FormInput[idx].add(ChkParaparaseYa[idx]);
        ChkParapareseTidak[idx].addActionListener(evt -> { if (ChkParapareseTidak[idx].isSelected()) ChkParaparaseYa[idx].setSelected(false); });
        ChkParaparaseYa[idx].addActionListener(evt -> { if (ChkParaparaseYa[idx].isSelected()) ChkParapareseTidak[idx].setSelected(false); });

        ry += 28;
        Label lblKateter = new Label(); lblKateter.setText("Kateter/Kondom kat :"); lblKateter.setBounds(rightCol, ry, 140, 23); FormInput[idx].add(lblKateter);
        ChkKateterTidak[idx] = new CekBox(); ChkKateterTidak[idx].setText("Tidak"); ChkKateterTidak[idx].setBounds(fldXR, ry, 65, 23); FormInput[idx].add(ChkKateterTidak[idx]);
        ChkKateterYa[idx] = new CekBox(); ChkKateterYa[idx].setText("Ya"); ChkKateterYa[idx].setBounds(fldXR + 70, ry, 50, 23); FormInput[idx].add(ChkKateterYa[idx]);
        ChkKateterTidak[idx].addActionListener(evt -> { if (ChkKateterTidak[idx].isSelected()) ChkKateterYa[idx].setSelected(false); });
        ChkKateterYa[idx].addActionListener(evt -> { if (ChkKateterYa[idx].isSelected()) ChkKateterTidak[idx].setSelected(false); });

        ry += 28;
        Label lblProdUrine = new Label(); lblProdUrine.setText("Prod. Urine :"); lblProdUrine.setBounds(rightCol, ry, 140, 23); FormInput[idx].add(lblProdUrine);
        TProdUrineCC[idx] = new TextBox(); TProdUrineCC[idx].setBounds(fldXR, ry, 50, 23); FormInput[idx].add(TProdUrineCC[idx]);
        JLabel lblCCJam = new JLabel("CC/"); lblCCJam.setBounds(fldXR + 53, ry, 25, 23); FormInput[idx].add(lblCCJam);
        TProdUrineJam[idx] = new TextBox(); TProdUrineJam[idx].setBounds(fldXR + 80, ry, 40, 23); FormInput[idx].add(TProdUrineJam[idx]);
        JLabel lblJam = new JLabel("Jam"); lblJam.setBounds(fldXR + 123, ry, 30, 23); FormInput[idx].add(lblJam);

        ry += 28;
        Label lblWarnaUrine = new Label(); lblWarnaUrine.setText("Warna urine :"); lblWarnaUrine.setBounds(rightCol, ry, 140, 23); FormInput[idx].add(lblWarnaUrine);
        TWarnaUrine[idx] = new TextBox(); TWarnaUrine[idx].setBounds(fldXR, ry, 120, 23); FormInput[idx].add(TWarnaUrine[idx]);

        ry += 28;
        Label lblMakan = new Label(); lblMakan.setText("Makan :"); lblMakan.setBounds(rightCol, ry, 140, 23); FormInput[idx].add(lblMakan);
        TMakan[idx] = new TextBox(); TMakan[idx].setBounds(fldXR, ry, 60, 23); FormInput[idx].add(TMakan[idx]);
        JLabel lblPorsi = new JLabel("Porsi"); lblPorsi.setBounds(fldXR + 65, ry, 40, 23); FormInput[idx].add(lblPorsi);

        ry += 28;
        Label lblMinum = new Label(); lblMinum.setText("Minum :"); lblMinum.setBounds(rightCol, ry, 140, 23); FormInput[idx].add(lblMinum);
        TMinum[idx] = new TextBox(); TMinum[idx].setBounds(fldXR, ry, 120, 23); FormInput[idx].add(TMinum[idx]);

        ry += 28;
        Label lblSonde = new Label(); lblSonde.setText("Sonde :"); lblSonde.setBounds(rightCol, ry, 140, 23); FormInput[idx].add(lblSonde);
        TSonde[idx] = new TextBox(); TSonde[idx].setBounds(fldXR, ry, 120, 23); FormInput[idx].add(TSonde[idx]);

        ry += 28;
        Label lblMuntah = new Label(); lblMuntah.setText("Muntah :"); lblMuntah.setBounds(rightCol, ry, 140, 23); FormInput[idx].add(lblMuntah);
        TMuntah[idx] = new TextBox(); TMuntah[idx].setBounds(fldXR, ry, 60, 23); FormInput[idx].add(TMuntah[idx]);
        JLabel lblMuntahU = new JLabel("cc"); lblMuntahU.setBounds(fldXR + 65, ry, 20, 23); FormInput[idx].add(lblMuntahU);

        ry += 28;
        Label lblCMS = new Label(); lblCMS.setText("CMS :"); lblCMS.setBounds(rightCol, ry, 140, 23); FormInput[idx].add(lblCMS);
        TCMS[idx] = new TextBox(); TCMS[idx].setBounds(fldXR, ry, 60, 23); FormInput[idx].add(TCMS[idx]);
        JLabel lblCMSU = new JLabel("cc"); lblCMSU.setBounds(fldXR + 65, ry, 20, 23); FormInput[idx].add(lblCMSU);

        ry += 28;
        Label lblKembung = new Label(); lblKembung.setText("Kembung :"); lblKembung.setBounds(rightCol, ry, 140, 23); FormInput[idx].add(lblKembung);
        ChkKembungTidak[idx] = new CekBox(); ChkKembungTidak[idx].setText("Tidak"); ChkKembungTidak[idx].setBounds(fldXR, ry, 65, 23); FormInput[idx].add(ChkKembungTidak[idx]);
        ChkKembungYa[idx] = new CekBox(); ChkKembungYa[idx].setText("Ya"); ChkKembungYa[idx].setBounds(fldXR + 70, ry, 50, 23); FormInput[idx].add(ChkKembungYa[idx]);
        ChkKembungTidak[idx].addActionListener(evt -> { if (ChkKembungTidak[idx].isSelected()) ChkKembungYa[idx].setSelected(false); });
        ChkKembungYa[idx].addActionListener(evt -> { if (ChkKembungYa[idx].isSelected()) ChkKembungTidak[idx].setSelected(false); });

        ry += 28;
        Label lblBisingUsus = new Label(); lblBisingUsus.setText("Bising usus :"); lblBisingUsus.setBounds(rightCol, ry, 140, 23); FormInput[idx].add(lblBisingUsus);
        TBisingUsus[idx] = new TextBox(); TBisingUsus[idx].setBounds(fldXR, ry, 60, 23); FormInput[idx].add(TBisingUsus[idx]);
        JLabel lblBUU = new JLabel("x/mnt"); lblBUU.setBounds(fldXR + 65, ry, 40, 23); FormInput[idx].add(lblBUU);

        ry += 28;
        Label lblFlatus = new Label(); lblFlatus.setText("Flatus :"); lblFlatus.setBounds(rightCol, ry, 140, 23); FormInput[idx].add(lblFlatus);
        ChkFlatusTidak[idx] = new CekBox(); ChkFlatusTidak[idx].setText("Tidak"); ChkFlatusTidak[idx].setBounds(fldXR, ry, 65, 23); FormInput[idx].add(ChkFlatusTidak[idx]);
        ChkFlatusYa[idx] = new CekBox(); ChkFlatusYa[idx].setText("Ya"); ChkFlatusYa[idx].setBounds(fldXR + 70, ry, 50, 23); FormInput[idx].add(ChkFlatusYa[idx]);
        ChkFlatusTidak[idx].addActionListener(evt -> { if (ChkFlatusTidak[idx].isSelected()) ChkFlatusYa[idx].setSelected(false); });
        ChkFlatusYa[idx].addActionListener(evt -> { if (ChkFlatusYa[idx].isSelected()) ChkFlatusTidak[idx].setSelected(false); });

        ry += 28;
        Label lblDiare = new Label(); lblDiare.setText("Diare :"); lblDiare.setBounds(rightCol, ry, 140, 23); FormInput[idx].add(lblDiare);
        TDiare[idx] = new TextBox(); TDiare[idx].setBounds(fldXR, ry, 60, 23); FormInput[idx].add(TDiare[idx]);
        JLabel lblDiareU = new JLabel("x"); lblDiareU.setBounds(fldXR + 65, ry, 15, 23); FormInput[idx].add(lblDiareU);

        ry += 28;
        Label lblLuka = new Label(); lblLuka.setText("Luka :"); lblLuka.setBounds(rightCol, ry, 140, 23); FormInput[idx].add(lblLuka);
        ChkLukaTidak[idx] = new CekBox(); ChkLukaTidak[idx].setText("Tidak"); ChkLukaTidak[idx].setBounds(fldXR, ry, 65, 23); FormInput[idx].add(ChkLukaTidak[idx]);
        ChkLukaYa[idx] = new CekBox(); ChkLukaYa[idx].setText("Ya"); ChkLukaYa[idx].setBounds(fldXR + 70, ry, 50, 23); FormInput[idx].add(ChkLukaYa[idx]);
        ChkLukaTidak[idx].addActionListener(evt -> { if (ChkLukaTidak[idx].isSelected()) ChkLukaYa[idx].setSelected(false); });
        ChkLukaYa[idx].addActionListener(evt -> { if (ChkLukaYa[idx].isSelected()) ChkLukaTidak[idx].setSelected(false); });

        ry += 28;
        Label lblCairanMasuk = new Label(); lblCairanMasuk.setText("Cairan masuk :"); lblCairanMasuk.setBounds(rightCol, ry, 140, 23); FormInput[idx].add(lblCairanMasuk);
        TCairanMasuk[idx] = new TextBox(); TCairanMasuk[idx].setBounds(fldXR, ry, 60, 23); FormInput[idx].add(TCairanMasuk[idx]);
        JLabel lblCMU = new JLabel("cc"); lblCMU.setBounds(fldXR + 65, ry, 20, 23); FormInput[idx].add(lblCMU);

        ry += 28;
        Label lblCairanKeluar = new Label(); lblCairanKeluar.setText("Cairan keluar :"); lblCairanKeluar.setBounds(rightCol, ry, 140, 23); FormInput[idx].add(lblCairanKeluar);
        TCairanKeluar[idx] = new TextBox(); TCairanKeluar[idx].setBounds(fldXR, ry, 60, 23); FormInput[idx].add(TCairanKeluar[idx]);
        JLabel lblCKU = new JLabel("cc"); lblCKU.setBounds(fldXR + 65, ry, 20, 23); FormInput[idx].add(lblCKU);

        ry += 28;
        Label lblBalance = new Label(); lblBalance.setText("Balance cairan :"); lblBalance.setBounds(rightCol, ry, 140, 23); FormInput[idx].add(lblBalance);
        TBalanceCairan[idx] = new TextBox(); TBalanceCairan[idx].setBounds(fldXR, ry, 60, 23); FormInput[idx].add(TBalanceCairan[idx]);
        JLabel lblBCU = new JLabel("cc"); lblBCU.setBounds(fldXR + 65, ry, 20, 23); FormInput[idx].add(lblBCU);

        ry += 28;
        Label lblLainLain = new Label(); lblLainLain.setText("Lain-lain :"); lblLainLain.setBounds(rightCol, ry, 140, 23); FormInput[idx].add(lblLainLain);
        TLainLain[idx] = new TextBox(); TLainLain[idx].setBounds(fldXR, ry, 200, 23); FormInput[idx].add(TLainLain[idx]);

        ry += 28;
        Label lblPenunjang = new Label(); lblPenunjang.setText("Pemeriksa penunjang :"); lblPenunjang.setBounds(rightCol, ry, 140, 23); FormInput[idx].add(lblPenunjang);
        TPemeriksaPenunjang[idx] = new TextBox(); TPemeriksaPenunjang[idx].setBounds(fldXR, ry, 200, 23); FormInput[idx].add(TPemeriksaPenunjang[idx]);

        // Continue below both columns
        y = Math.max(y, ry) + 32;

        // === A (ASSESSMENT) ===
        JLabel lblA = new JLabel("A (ASSESSMENT)");
        lblA.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblA.setBounds(leftCol, y, 200, 23); FormInput[idx].add(lblA);

        y += 25;
        Label lblMasalah = new Label(); lblMasalah.setText("Masalah keperawatan :"); lblMasalah.setBounds(leftCol, y, 140, 23); FormInput[idx].add(lblMasalah);
        TMasalahKeperawatan[idx] = new TextArea();
        TMasalahKeperawatan[idx].setBorder(javax.swing.BorderFactory.createLineBorder(new Color(180, 180, 180)));
        ScrollPane scrMasalah = new ScrollPane();
        scrMasalah.setViewportView(TMasalahKeperawatan[idx]);
        scrMasalah.setBounds(fldX, y, 400, 50); FormInput[idx].add(scrMasalah);

        Label lblStatus = new Label(); lblStatus.setText("Status :"); lblStatus.setBounds(rightCol, y, 60, 23); FormInput[idx].add(lblStatus);
        CmbStatus[idx] = new ComboBox();
        CmbStatus[idx].setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Teratasi", "Sebagian", "Belum"}));
        CmbStatus[idx].setBounds(rightCol + 65, y, 100, 23); FormInput[idx].add(CmbStatus[idx]);

        y += 60;
        Label lblMasalahBaru = new Label(); lblMasalahBaru.setText("Masalah Baru :"); lblMasalahBaru.setBounds(leftCol, y, 140, 23); FormInput[idx].add(lblMasalahBaru);
        TMasalahBaru[idx] = new TextArea();
        TMasalahBaru[idx].setBorder(javax.swing.BorderFactory.createLineBorder(new Color(180, 180, 180)));
        ScrollPane scrMasalahBaru = new ScrollPane();
        scrMasalahBaru.setViewportView(TMasalahBaru[idx]);
        scrMasalahBaru.setBounds(fldX, y, 400, 50); FormInput[idx].add(scrMasalahBaru);

        y += 60;
        // === P (PLANNING) ===
        JLabel lblP = new JLabel("P (PLANNING)");
        lblP.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblP.setBounds(leftCol, y, 200, 23); FormInput[idx].add(lblP);

        y += 25;
        ChkObsTTV[idx] = new CekBox(); ChkObsTTV[idx].setText("Observasi tanda vital"); ChkObsTTV[idx].setBounds(leftCol, y, 180, 23); FormInput[idx].add(ChkObsTTV[idx]);
        ChkObsInfeksi[idx] = new CekBox(); ChkObsInfeksi[idx].setText("Observasi tanda infeksi"); ChkObsInfeksi[idx].setBounds(200, y, 180, 23); FormInput[idx].add(ChkObsInfeksi[idx]);
        ChkKajiKesadaran[idx] = new CekBox(); ChkKajiKesadaran[idx].setText("Kaji tingkat kesadaran"); ChkKajiKesadaran[idx].setBounds(390, y, 180, 23); FormInput[idx].add(ChkKajiKesadaran[idx]);

        y += 25;
        ChkKajiNyeri[idx] = new CekBox(); ChkKajiNyeri[idx].setText("Kaji ulang tingkat nyeri"); ChkKajiNyeri[idx].setBounds(leftCol, y, 180, 23); FormInput[idx].add(ChkKajiNyeri[idx]);
        ChkPersiapanTindakan[idx] = new CekBox(); ChkPersiapanTindakan[idx].setText("Persiapan tindakan :"); ChkPersiapanTindakan[idx].setBounds(200, y, 160, 23); FormInput[idx].add(ChkPersiapanTindakan[idx]);
        TPersiapanTindakan[idx] = new TextBox(); TPersiapanTindakan[idx].setBounds(365, y, 200, 23); FormInput[idx].add(TPersiapanTindakan[idx]);

        y += 25;
        ChkObsCairan[idx] = new CekBox(); ChkObsCairan[idx].setText("Observasi cairan masuk & keluar"); ChkObsCairan[idx].setBounds(leftCol, y, 230, 23); FormInput[idx].add(ChkObsCairan[idx]);
        ChkBalansCairan[idx] = new CekBox(); ChkBalansCairan[idx].setText("Ukur balans cairan"); ChkBalansCairan[idx].setBounds(250, y, 160, 23); FormInput[idx].add(ChkBalansCairan[idx]);

        y += 25;
        ChkObsKeluhan[idx] = new CekBox(); ChkObsKeluhan[idx].setText("Observasi keluhan pasien"); ChkObsKeluhan[idx].setBounds(leftCol, y, 200, 23); FormInput[idx].add(ChkObsKeluhan[idx]);
        ChkKajiJatuh[idx] = new CekBox(); ChkKajiJatuh[idx].setText("Kaji ulang resiko jatuh"); ChkKajiJatuh[idx].setBounds(250, y, 180, 23); FormInput[idx].add(ChkKajiJatuh[idx]);

        y += 30;
        Label lblKolaborasi = new Label(); lblKolaborasi.setText("Kolaborasi :"); lblKolaborasi.setBounds(leftCol, y, 140, 23); FormInput[idx].add(lblKolaborasi);
        TKolaborasi[idx] = new TextArea();
        TKolaborasi[idx].setBorder(javax.swing.BorderFactory.createLineBorder(new Color(180, 180, 180)));
        ScrollPane scrKolaborasi = new ScrollPane();
        scrKolaborasi.setViewportView(TKolaborasi[idx]);
        scrKolaborasi.setBounds(fldX, y, 400, 50); FormInput[idx].add(scrKolaborasi);

        y += 60;
        Label lblCatatan = new Label(); lblCatatan.setText("Catatan penting :"); lblCatatan.setBounds(leftCol, y, 140, 23); FormInput[idx].add(lblCatatan);
        TCatatanPenting[idx] = new TextArea();
        TCatatanPenting[idx].setBorder(javax.swing.BorderFactory.createLineBorder(new Color(180, 180, 180)));
        ScrollPane scrCatatan = new ScrollPane();
        scrCatatan.setViewportView(TCatatanPenting[idx]);
        scrCatatan.setBounds(fldX, y, 400, 50); FormInput[idx].add(scrCatatan);

        y += 60;
        // PJ Section
        JLabel lblPJSection = new JLabel("PENANGGUNG JAWAB");
        lblPJSection.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblPJSection.setBounds(leftCol, y, 200, 23); FormInput[idx].add(lblPJSection);

        y += 25;
        final int tabIdx = idx;
        Label lblPJKaTim = new Label(); lblPJKaTim.setText("PJ/Ka.Tim :"); lblPJKaTim.setBounds(leftCol, y, 140, 23); FormInput[idx].add(lblPJKaTim);
        KdPJKaTim[idx] = new TextBox(); KdPJKaTim[idx].setBounds(fldX, y, 80, 23); FormInput[idx].add(KdPJKaTim[idx]);
        NmPJKaTim[idx] = new TextBox(); NmPJKaTim[idx].setBounds(237, y, 170, 23); FormInput[idx].add(NmPJKaTim[idx]);
        BtnPJKaTim[idx] = new Button(); BtnPJKaTim[idx].setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPJKaTim[idx].setBounds(409, y, 28, 23); FormInput[idx].add(BtnPJKaTim[idx]);
        BtnPJKaTim[idx].addActionListener(evt -> {
            petugasTarget = tabIdx * 2;
            petugasDlg.emptTeks(); petugasDlg.isCek();
            petugasDlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            petugasDlg.setLocationRelativeTo(internalFrame1); petugasDlg.setVisible(true);
        });

        y += 28;
        Label lblPJSerah = new Label(); lblPJSerah.setText("PJ Serah Terima :"); lblPJSerah.setBounds(leftCol, y, 140, 23); FormInput[idx].add(lblPJSerah);
        KdPJSerahTerima[idx] = new TextBox(); KdPJSerahTerima[idx].setBounds(fldX, y, 80, 23); FormInput[idx].add(KdPJSerahTerima[idx]);
        NmPJSerahTerima[idx] = new TextBox(); NmPJSerahTerima[idx].setBounds(237, y, 170, 23); FormInput[idx].add(NmPJSerahTerima[idx]);
        BtnPJSerahTerima[idx] = new Button(); BtnPJSerahTerima[idx].setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPJSerahTerima[idx].setBounds(409, y, 28, 23); FormInput[idx].add(BtnPJSerahTerima[idx]);
        BtnPJSerahTerima[idx].addActionListener(evt -> {
            petugasTarget = tabIdx * 2 + 1;
            petugasDlg.emptTeks(); petugasDlg.isCek();
            petugasDlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            petugasDlg.setLocationRelativeTo(internalFrame1); petugasDlg.setVisible(true);
        });

        y += 35;
        // Contekan Masalah Keperawatan
        JLabel lblContekan = new JLabel("Contekan Masalah Keperawatan :");
        lblContekan.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblContekan.setBounds(leftCol, y, 250, 23); FormInput[idx].add(lblContekan);
        y += 22;

        String[] contekan = {
            "1. Ketidakefektifan bersihan jalan napas",
            "2. Ketidakefektifan pola napas",
            "3. Gangguan pertukaran gas",
            "4. Resiko aspirasi",
            "5. Gangguan perfusi jaringan perifer/cerebral",
            "6. Pk. Peningkatan TIK",
            "7. Peningkatan suhu tubuh",
            "8. Resiko intoleransi / intoleransi aktivitas",
            "9. PK : Penurunan Curah Jantung",
            "10. Kekurangan / kelebihan volume cairan",
            "11. Resiko ketidakseimbangan asam/basa",
            "12. Nyeri",
            "13. Resiko cidera",
            "14. Retensi/Inkontinensia urin",
            "15. Perubahan eliminasi urin",
            "16. Mual",
            "17. Konstipasi / diare",
            "18. Perubahan nutrisi kurang / lebih dari kebutuhan",
            "19. Resiko kerusakan / Kerusakan integritas kulit",
            "20. Defisit perawatan diri",
            "21. Resti infeksi",
            "22. Ansietas",
            "23. Ketidakefektifan koping",
            "24. Gangguan identitas personal"
        };

        // Layout in 3 columns
        int colWidth = 350;
        int startY = y;
        for (int c = 0; c < contekan.length; c++) {
            int col = c < 7 ? 0 : (c < 14 ? 1 : (c < 21 ? 2 : 3));
            int row = c < 7 ? c : (c < 14 ? c - 7 : (c < 21 ? c - 14 : c - 21));
            int cx = leftCol + (col * colWidth);
            int cy = startY + (row * 16);
            JLabel lblC = new JLabel(contekan[c]);
            lblC.setFont(new java.awt.Font("Tahoma", 0, 10));
            lblC.setForeground(new Color(80, 80, 80));
            lblC.setBounds(cx, cy, colWidth, 16); FormInput[idx].add(lblC);
        }
        y = startY + (8 * 16) + 10;

        // Update preferred size
        FormInput[idx].setPreferredSize(new Dimension(1150, y + 10));

        // ChkInput toggle
        ChkInput[idx] = new CekBox();
        ChkInput[idx].setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput[idx].setMnemonic('1'); ChkInput[idx].setText(".: Input Data"); ChkInput[idx].setToolTipText("Alt+1");
        ChkInput[idx].setBorderPainted(true); ChkInput[idx].setBorderPaintedFlat(true); ChkInput[idx].setFocusable(false);
        ChkInput[idx].setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput[idx].setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput[idx].setPreferredSize(new Dimension(192, 20));
        ChkInput[idx].setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput[idx].setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput[idx].setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput[idx].setSelected(true);
        final int fi = idx;
        ChkInput[idx].addActionListener(evt -> isForm(fi));

        ScrollInput[idx] = new ScrollPane();
        ScrollInput[idx].setViewportView(FormInput[idx]);
        ScrollInput[idx].setPreferredSize(new Dimension(1100, 450));

        PanelInput[idx] = new JPanel(new BorderLayout());
        PanelInput[idx].setOpaque(false);
        PanelInput[idx].setPreferredSize(new Dimension(WIDTH, 470));
        PanelInput[idx].add(ScrollInput[idx], BorderLayout.CENTER);
        PanelInput[idx].add(ChkInput[idx], BorderLayout.PAGE_END);
    }

    private void isForm(int idx) {
        if (ChkInput[idx].isSelected()) {
            ChkInput[idx].setVisible(false);
            PanelInput[idx].setPreferredSize(new Dimension(WIDTH, 470));
            FormInput[idx].setVisible(true);
            ChkInput[idx].setVisible(true);
        } else {
            ChkInput[idx].setVisible(false);
            PanelInput[idx].setPreferredSize(new Dimension(WIDTH, 20));
            FormInput[idx].setVisible(false);
            ChkInput[idx].setVisible(true);
        }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select cpk.no_rawat, r.no_rkm_medis, p.nm_pasien, cpk.tanggal, cpk.shift, " +
                "pt.nama as nm_petugas " +
                "from catatan_perkembangan_keperawatan cpk " +
                "inner join reg_periksa r on cpk.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                "left join petugas pt on cpk.nip_pj=pt.nip " +
                "where cpk.tanggal between ? and ? and (cpk.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) " +
                "order by cpk.tanggal desc, cpk.shift");
            try {
                ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
                ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
                ps.setString(3, "%" + TCari.getText().trim() + "%");
                ps.setString(4, "%" + TCari.getText().trim() + "%");
                ps.setString(5, "%" + TCari.getText().trim() + "%");
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("tanggal"), rs.getString("shift"), rs.getString("nm_petugas")
                    });
                }
                LCount.setText("Record : " + tabMode.getRowCount());
            } catch (Exception e) {
                System.out.println("Notif Catatan Perkembangan Keperawatan : " + e);
            } finally {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            }
        } catch (Exception e) {
            System.out.println("Notif Catatan Perkembangan Keperawatan : " + e);
        }
    }

    public void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());

            String shift = tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString();
            int idx = shift.equals("Pagi") ? 0 : (shift.equals("Sore") ? 1 : 2);
            TabRawat.setSelectedIndex(idx);

            try {
                ps = koneksi.prepareStatement(
                    "select cpk.*, pt1.nama as nm_pj_katim, pt2.nama as nm_pj_serah_terima " +
                    "from catatan_perkembangan_keperawatan cpk " +
                    "left join petugas pt1 on cpk.nip_pj=pt1.nip " +
                    "left join petugas pt2 on cpk.nip_pj_serah=pt2.nip " +
                    "where cpk.no_rawat=? and cpk.tanggal=? and cpk.shift=?");
                ps.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                ps.setString(2, tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
                ps.setString(3, shift);
                rs = ps.executeQuery();
                if (rs.next()) {
                    try { TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("tanggal"))); } catch (Exception e) {}
                    // pav_kamar loaded from setNoRm
                    TSubjektif[idx].setText(rs.getString("subjektif"));
                    CmbSesak[idx].setSelectedItem(rs.getString("sesak"));
                    TRR[idx].setText(rs.getString("rr"));
                    TO2[idx].setText(rs.getString("o2"));
                    TSpO2[idx].setText(rs.getString("spo2"));
                    TSuaraNapas[idx].setText(rs.getString("suara_napas"));
                    TSekret[idx].setText(rs.getString("sekret"));
                    TPerfusi[idx].setText(rs.getString("perfusi"));
                    TNadi[idx].setText(rs.getString("nadi"));
                    String tdVal = rs.getString("td");
                    if (tdVal != null && tdVal.contains("/")) { String[] tdParts = tdVal.split("/"); TTDSistolik[idx].setText(tdParts[0]); TTDDiastolik[idx].setText(tdParts[1]); }
                    else { TTDSistolik[idx].setText(tdVal != null ? tdVal : ""); TTDDiastolik[idx].setText(""); }
                    TSuhu[idx].setText(rs.getString("suhu"));
                    ChkPerdarahan[idx].setSelected(rs.getString("perdarahan").equals("Ya"));
                    TVolPerdarahan[idx].setText(rs.getString("volume_perdarahan"));
                    TInfus[idx].setText(rs.getString("infus"));
                    String gcsVal = rs.getString("gcs");
                    if (gcsVal != null && gcsVal.contains("/")) { String[] gcsParts = gcsVal.split("/"); TGCSE[idx].setText(gcsParts[0]); if(gcsParts.length>1) TGCSV[idx].setText(gcsParts[1]); if(gcsParts.length>2) TGCSM[idx].setText(gcsParts[2]); }
                    else { TGCSE[idx].setText(gcsVal != null ? gcsVal : ""); TGCSV[idx].setText(""); TGCSM[idx].setText(""); }
                    CmbPupil[idx].setSelectedItem(rs.getString("pupil"));
                    TKesadaran[idx].setText(rs.getString("kesadaran_obj"));
                    TSkalaNyeri[idx].setText(rs.getString("skala_nyeri"));
                    TMobilisasi[idx].setText(rs.getString("mobilisasi"));
                    TMorseFall[idx].setText(rs.getString("morse_fall_scale"));
                    boolean hemiYa = rs.getString("hemiparase").equals("Ya");
                    ChkHemiparaseYa[idx].setSelected(hemiYa);
                    ChkHemiparaseTidak[idx].setSelected(!hemiYa);
                    boolean paraYa = rs.getString("paraparese").equals("Ya");
                    ChkParaparaseYa[idx].setSelected(paraYa);
                    ChkParapareseTidak[idx].setSelected(!paraYa);
                    boolean katYa = rs.getString("kateter_kondom").equals("Ya");
                    ChkKateterYa[idx].setSelected(katYa);
                    ChkKateterTidak[idx].setSelected(!katYa);
                    String urineVal = rs.getString("prod_urine");
                    if (urineVal != null && urineVal.contains("/")) { String[] uParts = urineVal.split("/"); TProdUrineCC[idx].setText(uParts[0]); TProdUrineJam[idx].setText(uParts[1]); }
                    else { TProdUrineCC[idx].setText(urineVal != null ? urineVal : ""); TProdUrineJam[idx].setText(""); }
                    TWarnaUrine[idx].setText(rs.getString("warna_urine"));
                    TMakan[idx].setText(rs.getString("makan_porsi"));
                    TMinum[idx].setText(rs.getString("minum"));
                    TSonde[idx].setText(rs.getString("sonde"));
                    TMuntah[idx].setText(rs.getString("muntah"));
                    TCMS[idx].setText(rs.getString("cms"));
                    boolean kembungYa = rs.getString("kembung").equals("Ya");
                    ChkKembungYa[idx].setSelected(kembungYa);
                    ChkKembungTidak[idx].setSelected(!kembungYa);
                    TBisingUsus[idx].setText(rs.getString("bising_usus"));
                    boolean flatusYa = rs.getString("flatus").equals("Ya");
                    ChkFlatusYa[idx].setSelected(flatusYa);
                    ChkFlatusTidak[idx].setSelected(!flatusYa);
                    TDiare[idx].setText(rs.getString("diare"));
                    boolean lukaYa = rs.getString("luka").equals("Ya");
                    ChkLukaYa[idx].setSelected(lukaYa);
                    ChkLukaTidak[idx].setSelected(!lukaYa);
                    TCairanMasuk[idx].setText(rs.getString("cairan_masuk"));
                    TCairanKeluar[idx].setText(rs.getString("cairan_keluar"));
                    TBalanceCairan[idx].setText(rs.getString("balance_cairan"));
                    TLainLain[idx].setText(rs.getString("lain_lain"));
                    TPemeriksaPenunjang[idx].setText(rs.getString("pemeriksa_penunjang"));
                    TMasalahKeperawatan[idx].setText(rs.getString("masalah_keperawatan"));
                    CmbStatus[idx].setSelectedItem(rs.getString("status_masalah"));
                    TMasalahBaru[idx].setText(rs.getString("masalah_baru"));
                    ChkObsTTV[idx].setSelected(rs.getString("obs_tanda_vital").equals("Ya"));
                    ChkObsInfeksi[idx].setSelected(rs.getString("obs_tanda_infeksi").equals("Ya"));
                    ChkKajiKesadaran[idx].setSelected(rs.getString("kaji_kesadaran").equals("Ya"));
                    ChkKajiNyeri[idx].setSelected(rs.getString("kaji_nyeri").equals("Ya"));
                    ChkPersiapanTindakan[idx].setSelected(rs.getString("obs_tanda_vital")!=null);
                    TPersiapanTindakan[idx].setText(rs.getString("persiapan_tindakan"));
                    ChkObsCairan[idx].setSelected(rs.getString("obs_cairan").equals("Ya"));
                    ChkBalansCairan[idx].setSelected(rs.getString("ukur_balans").equals("Ya"));
                    ChkObsKeluhan[idx].setSelected(rs.getString("obs_keluhan").equals("Ya"));
                    ChkKajiJatuh[idx].setSelected(rs.getString("kaji_resiko_jatuh").equals("Ya"));
                    TKolaborasi[idx].setText(rs.getString("kolaborasi"));
                    TCatatanPenting[idx].setText(rs.getString("catatan_penting"));
                    KdPJKaTim[idx].setText(rs.getString("nip_pj"));
                    NmPJKaTim[idx].setText(rs.getString("nm_pj_katim"));
                    KdPJSerahTerima[idx].setText(rs.getString("nip_pj_serah"));
                    NmPJSerahTerima[idx].setText(rs.getString("nm_pj_serah_terima"));
                }
            } catch (Exception e) {
                System.out.println("Notif getData CatatanKeperawatan : " + e);
            } finally {
                try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {}
            }
        }
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date());
        TPavKamar.setText("");
        for (int i = 0; i < 3; i++) {
            TSubjektif[i].setText("");
            CmbSesak[i].setSelectedIndex(0);
            TRR[i].setText(""); TO2[i].setText(""); TSpO2[i].setText("");
            TSuaraNapas[i].setText(""); TSekret[i].setText(""); TPerfusi[i].setText("");
            TNadi[i].setText(""); TTDSistolik[i].setText(""); TTDDiastolik[i].setText("");
            TSuhu[i].setText("");
            ChkPerdarahan[i].setSelected(false); TVolPerdarahan[i].setText("");
            TInfus[i].setText("");
            TGCSE[i].setText(""); TGCSV[i].setText(""); TGCSM[i].setText("");
            CmbPupil[i].setSelectedIndex(0);
            TKesadaran[i].setText(""); TSkalaNyeri[i].setText("");
            TMobilisasi[i].setText(""); TMorseFall[i].setText("");
            ChkHemiparaseTidak[i].setSelected(false); ChkHemiparaseYa[i].setSelected(false);
            ChkParapareseTidak[i].setSelected(false); ChkParaparaseYa[i].setSelected(false);
            ChkKateterTidak[i].setSelected(false); ChkKateterYa[i].setSelected(false);
            TProdUrineCC[i].setText(""); TProdUrineJam[i].setText("");
            TWarnaUrine[i].setText("");
            TMakan[i].setText(""); TMinum[i].setText("");
            TSonde[i].setText(""); TMuntah[i].setText(""); TCMS[i].setText("");
            ChkKembungTidak[i].setSelected(false); ChkKembungYa[i].setSelected(false);
            TBisingUsus[i].setText("");
            ChkFlatusTidak[i].setSelected(false); ChkFlatusYa[i].setSelected(false);
            TDiare[i].setText("");
            ChkLukaTidak[i].setSelected(false); ChkLukaYa[i].setSelected(false);
            TCairanMasuk[i].setText(""); TCairanKeluar[i].setText(""); TBalanceCairan[i].setText("");
            TLainLain[i].setText(""); TPemeriksaPenunjang[i].setText("");
            TMasalahKeperawatan[i].setText(""); CmbStatus[i].setSelectedIndex(0); TMasalahBaru[i].setText("");
            ChkObsTTV[i].setSelected(false); ChkObsInfeksi[i].setSelected(false);
            ChkKajiKesadaran[i].setSelected(false); ChkKajiNyeri[i].setSelected(false);
            ChkPersiapanTindakan[i].setSelected(false); TPersiapanTindakan[i].setText("");
            ChkObsCairan[i].setSelected(false); ChkBalansCairan[i].setSelected(false);
            ChkObsKeluhan[i].setSelected(false); ChkKajiJatuh[i].setSelected(false);
            TKolaborasi[i].setText(""); TCatatanPenting[i].setText("");
            KdPJKaTim[i].setText(""); NmPJKaTim[i].setText("");
            KdPJSerahTerima[i].setText(""); NmPJSerahTerima[i].setText("");
        }
    }

    public void setNoRm(String norwt, Date tgl) {
        TNoRw.setText(norwt);
        TglAsuhan.setDate(tgl);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", norwt));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
        TPavKamar.setText(Sequel.cariIsi(
            "select concat(bangsal.nm_bangsal,' / ',kamar.kd_kamar) from kamar_inap " +
            "inner join kamar on kamar_inap.kd_kamar=kamar.kd_kamar " +
            "inner join bangsal on kamar.kd_bangsal=bangsal.kd_bangsal " +
            "where kamar_inap.no_rawat=? order by kamar_inap.tgl_masuk desc limit 1", norwt));
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getrujukan_keluar());
        BtnHapus.setEnabled(akses.getrujukan_keluar());
        BtnEdit.setEnabled(akses.getrujukan_keluar());
        BtnPrint.setEnabled(akses.getrujukan_keluar());
    }
}
