/*
 * Kontribusi dari tim IT RSUD Prembun
 */
package rekammedis;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import kepegawaian.DlgCariDokter;

/**
 *
 * @author developer
 */
public final class RMAsesmenPraAnestesi extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private DlgCariDokter dokter = new DlgCariDokter(null, false);
    private StringBuilder htmlContent;
    private String finger = "";

    // Variables declaration
    private widget.editorpane LoadHTML;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JMenuItem MnAsesmenPraAnestesi;
    private widget.InternalFrame internalFrame1;
    private widget.panelisi panelGlass8;
    private widget.Button BtnSimpan;
    private widget.Button BtnBatal;
    private widget.Button BtnHapus;
    private widget.Button BtnEdit;
    private widget.Button BtnPrint;
    private widget.Button BtnAll;
    private widget.Button BtnKeluar;
    private javax.swing.JTabbedPane TabRawat;
    private widget.InternalFrame internalFrame2;
    private widget.ScrollPane scrollInput;
    private widget.PanelBiasa FormInput;
    private widget.Label jLabel1;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.TextBox TNoRM;
    private widget.TextBox TglLahir;
    private widget.TextBox Jk;
    private widget.Label label14;
    private widget.TextBox KdDokter;
    private widget.TextBox NmDokter;
    private widget.Button BtnDokter;
    private widget.Label label11;
    private widget.Tanggal TglAsesmen;
    private widget.Label jLabel2;
    private widget.TextBox Ruangan;
    private widget.Label jLabel3;
    private widget.TextBox Diagnosa;
    private widget.Label jLabel4;
    private widget.TextBox TindakanBedah;
    private javax.swing.JSeparator jSeparator1;
    private widget.Label jLabel100;
    private widget.Label jLabel5;
    private widget.TextBox BeratBadan;
    private widget.Label jLabel6;
    private widget.TextBox TinggiBadan;
    private widget.Label jLabel7;
    private widget.TextArea Anamnesis;
    private widget.Label jLabel8;
    private widget.CekBox ChkAsthma;
    private widget.CekBox ChkAlergi;
    private widget.CekBox ChkDiabetes;
    private widget.CekBox ChkHipertensi;
    private widget.Label jLabel9;
    private widget.TextBox JenisAnestesiSebelum;
    private widget.Label jLabel10;
    private widget.TextBox Komplikasi;
    private javax.swing.JSeparator jSeparator2;
    private widget.Label jLabel101;
    private widget.Label jLabel11;
    private widget.TextArea PemeriksaanFisik;
    private widget.Label jLabel12;
    private widget.ComboBox KeadaanUmum;
    private widget.Label jLabel13;
    private widget.TextBox GCS;
    private widget.Label jLabel14;
    private widget.TextBox TekananDarah;
    private widget.Label jLabel15;
    private widget.TextBox Nadi;
    private widget.Label jLabel16;
    private widget.TextBox RespirasiRate;
    private widget.Label jLabel17;
    private widget.TextBox Suhu;
    private widget.Label jLabel18;
    private widget.TextBox GerakanLeher;
    private widget.Label jLabel19;
    private widget.TextBox Methohyoid;
    private widget.Label jLabel20;
    private widget.ComboBox Mallampati;
    private widget.Label jLabel21;
    private widget.TextArea Thorax;
    private widget.Label jLabel22;
    private widget.TextArea Abdomen;
    private widget.Label jLabel23;
    private widget.TextArea Ekstremitas;
    private widget.Label jLabel24;
    private widget.TextArea Laboratorium;
    private widget.Label jLabel25;
    private widget.TextArea EKG;
    private widget.Label jLabel26;
    private widget.TextArea RoThorax;
    private widget.Label jLabel27;
    private widget.TextArea LainLain;
    private javax.swing.JSeparator jSeparator3;
    private widget.Label jLabel102;
    private widget.Label jLabel28;
    private widget.ComboBox ASA;
    private widget.Label jLabel29;
    private widget.TextBox Premedikasi;
    private widget.Label jLabel30;
    private widget.TextBox Puasa;
    private javax.swing.JSeparator jSeparator4;
    private widget.Label jLabel103;
    private widget.TextArea Penyulit;
    private javax.swing.JSeparator jSeparator5;
    private widget.Label jLabel104;
    private widget.Label jLabel31;
    private widget.Label jLabel32;
    private widget.CekBox ChkGALMA;
    private widget.CekBox ChkGAETT;
    private widget.CekBox ChkGATIVA;
    private widget.Label jLabel33;
    private widget.CekBox ChkRegionalSpinal;
    private widget.CekBox ChkRegionalEpidural;
    private widget.CekBox ChkRegionalKaudal;
    private widget.CekBox ChkRegionalBlokPerifer;
    private widget.Label jLabel34;
    private widget.TextArea PersediaanDarah;
    private widget.Label jLabel35;
    private widget.CekBox ChkTeknikHipotensi;
    private widget.CekBox ChkTeknikVentilasiSatuParu;
    private widget.Label jLabel36;
    private widget.TextBox TeknikKhususLainnya;
    private widget.Label jLabel37;
    private widget.CekBox ChkMonitorEKG;
    private widget.CekBox ChkMonitorSpO2;
    private widget.CekBox ChkMonitorNIBP;
    private widget.CekBox ChkMonitorTemp;
    private widget.CekBox ChkMonitorCVP;
    private widget.CekBox ChkMonitorEtCO2;
    private widget.Label jLabel38;
    private widget.TextBox MonitorArteriLine;
    private widget.Label jLabel39;
    private widget.TextBox MonitorLainnya;
    private widget.Label jLabel40;
    private widget.ComboBox PerawatanPasca;
    private widget.InternalFrame internalFrame3;
    private widget.ScrollPane Scroll;
    private widget.Table tbObat;
    private widget.panelisi panelGlass9;
    private widget.Label jLabel41;
    private widget.Tanggal DTPCari1;
    private widget.Label jLabel42;
    private widget.Tanggal DTPCari2;
    private widget.Label jLabel43;
    private widget.TextBox TCari;
    private widget.Button BtnCari;
    private widget.Label jLabel44;
    private widget.Label LCount;

    /**
     * Creates new form RMAsesmenPraAnestesi
     */
    public RMAsesmenPraAnestesi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        // Initialize table model first
        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "J.K.", "Kode Dokter", "Nama Dokter", "Tanggal", "Ruangan",
            "Diagnosa", "Tindakan Bedah", "BB", "TB", "Anamnesis", "Riwayat Asthma", "Riwayat Alergi", "Riwayat DM",
            "Riwayat Hipertensi", "Jenis Anestesi Sebelum", "Komplikasi", "Pemeriksaan Fisik", "Keadaan Umum", "GCS",
            "TD", "Nadi", "RR", "Suhu", "Gerakan Leher", "Methohyoid", "Mallampati", "Thorax", "Abdomen", "Ekstremitas",
            "Laboratorium", "EKG", "Ro Thorax", "Lain-lain", "ASA", "Premedikasi", "Puasa", "Penyulit", "GA LMA", "GA ETT",
            "GA TIVA", "Regional Spinal", "Regional Epidural", "Regional Kaudal", "Regional Blok Perifer",
            "Persediaan Darah", "Teknik Hipotensi", "Teknik Ventilasi Satu Paru", "Teknik Khusus Lainnya",
            "Monitor EKG", "Monitor SpO2", "Monitor NIBP", "Monitor Temp", "Monitor CVP", "Monitor Arteri Line",
            "Monitor EtCO2", "Monitor Lainnya", "Perawatan Pasca"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        initComponents();
        setupTableModel();
        setupInputLimits();
        setupListeners();
        setupHTMLEditor();
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnAsesmenPraAnestesi = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        BtnAll = new widget.Button();
        BtnKeluar = new widget.Button();
        TabRawat = new javax.swing.JTabbedPane();
        internalFrame2 = new widget.InternalFrame();
        
        internalFrame2.setLayout(new java.awt.BorderLayout(1, 1));
scrollInput = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        jLabel1 = new widget.Label();
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        TNoRM = new widget.TextBox();
        TglLahir = new widget.TextBox();
        Jk = new widget.TextBox();
        label14 = new widget.Label();
        KdDokter = new widget.TextBox();
        NmDokter = new widget.TextBox();
        BtnDokter = new widget.Button();
        label11 = new widget.Label();
        TglAsesmen = new widget.Tanggal();
        jLabel2 = new widget.Label();
        Ruangan = new widget.TextBox();
        jLabel3 = new widget.Label();
        Diagnosa = new widget.TextBox();
        jLabel4 = new widget.Label();
        TindakanBedah = new widget.TextBox();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel100 = new widget.Label();
        jLabel5 = new widget.Label();
        BeratBadan = new widget.TextBox();
        jLabel6 = new widget.Label();
        TinggiBadan = new widget.TextBox();
        jLabel7 = new widget.Label();
        Anamnesis = new widget.TextArea();
        jLabel8 = new widget.Label();
        ChkAsthma = new widget.CekBox();
        ChkAlergi = new widget.CekBox();
        ChkDiabetes = new widget.CekBox();
        ChkHipertensi = new widget.CekBox();
        jLabel9 = new widget.Label();
        JenisAnestesiSebelum = new widget.TextBox();
        jLabel10 = new widget.Label();
        Komplikasi = new widget.TextBox();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel101 = new widget.Label();
        jLabel11 = new widget.Label();
        PemeriksaanFisik = new widget.TextArea();
        jLabel12 = new widget.Label();
        KeadaanUmum = new widget.ComboBox();
        jLabel13 = new widget.Label();
        GCS = new widget.TextBox();
        jLabel14 = new widget.Label();
        TekananDarah = new widget.TextBox();
        jLabel15 = new widget.Label();
        Nadi = new widget.TextBox();
        jLabel16 = new widget.Label();
        RespirasiRate = new widget.TextBox();
        jLabel17 = new widget.Label();
        Suhu = new widget.TextBox();
        jLabel18 = new widget.Label();
        GerakanLeher = new widget.TextBox();
        jLabel19 = new widget.Label();
        Methohyoid = new widget.TextBox();
        jLabel20 = new widget.Label();
        Mallampati = new widget.ComboBox();
        jLabel21 = new widget.Label();
        Thorax = new widget.TextArea();
        jLabel22 = new widget.Label();
        Abdomen = new widget.TextArea();
        jLabel23 = new widget.Label();
        Ekstremitas = new widget.TextArea();
        jLabel24 = new widget.Label();
        Laboratorium = new widget.TextArea();
        jLabel25 = new widget.Label();
        EKG = new widget.TextArea();
        jLabel26 = new widget.Label();
        RoThorax = new widget.TextArea();
        jLabel27 = new widget.Label();
        LainLain = new widget.TextArea();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel102 = new widget.Label();
        jLabel28 = new widget.Label();
        ASA = new widget.ComboBox();
        jLabel29 = new widget.Label();
        Premedikasi = new widget.TextBox();
        jLabel30 = new widget.Label();
        Puasa = new widget.TextBox();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel103 = new widget.Label();
        Penyulit = new widget.TextArea();
        jSeparator5 = new javax.swing.JSeparator();
        jLabel104 = new widget.Label();
        jLabel31 = new widget.Label();
        jLabel32 = new widget.Label();
        ChkGALMA = new widget.CekBox();
        ChkGAETT = new widget.CekBox();
        ChkGATIVA = new widget.CekBox();
        jLabel33 = new widget.Label();
        ChkRegionalSpinal = new widget.CekBox();
        ChkRegionalEpidural = new widget.CekBox();
        ChkRegionalKaudal = new widget.CekBox();
        ChkRegionalBlokPerifer = new widget.CekBox();
        jLabel34 = new widget.Label();
        PersediaanDarah = new widget.TextArea();
        jLabel35 = new widget.Label();
        ChkTeknikHipotensi = new widget.CekBox();
        ChkTeknikVentilasiSatuParu = new widget.CekBox();
        jLabel36 = new widget.Label();
        TeknikKhususLainnya = new widget.TextBox();
        jLabel37 = new widget.Label();
        ChkMonitorEKG = new widget.CekBox();
        ChkMonitorSpO2 = new widget.CekBox();
        ChkMonitorNIBP = new widget.CekBox();
        ChkMonitorTemp = new widget.CekBox();
        ChkMonitorCVP = new widget.CekBox();
        ChkMonitorEtCO2 = new widget.CekBox();
        jLabel38 = new widget.Label();
        MonitorArteriLine = new widget.TextBox();
        jLabel39 = new widget.Label();
        MonitorLainnya = new widget.TextBox();
        jLabel40 = new widget.Label();
        PerawatanPasca = new widget.ComboBox();
        internalFrame3 = new widget.InternalFrame();
        
        internalFrame3.setLayout(new java.awt.BorderLayout(1, 1));
Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        panelGlass9 = new widget.panelisi();
        jLabel41 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel42 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel43 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel44 = new widget.Label();
        LCount = new widget.Label();

        // Component setup
        LoadHTML.setBorder(null);
        LoadHTML.setName("LoadHTML");

        jPopupMenu1.setName("jPopupMenu1");

        MnAsesmenPraAnestesi.setBackground(new java.awt.Color(255, 255, 254));
        MnAsesmenPraAnestesi.setFont(new java.awt.Font("Tahoma", 0, 11));
        MnAsesmenPraAnestesi.setForeground(new java.awt.Color(50, 50, 50));
        MnAsesmenPraAnestesi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        MnAsesmenPraAnestesi.setText("Laporan Asesmen Pra Anestesi");
        MnAsesmenPraAnestesi.setName("MnAsesmenPraAnestesi");
        MnAsesmenPraAnestesi.setPreferredSize(new java.awt.Dimension(220, 26));
        MnAsesmenPraAnestesi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnAsesmenPraAnestesiActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnAsesmenPraAnestesi);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
                "::[ Asesmen Pra Anestesi ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 11),
                new java.awt.Color(50, 50, 50)
        ));
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12));
        internalFrame1.setName("internalFrame1");
        internalFrame1.setPreferredSize(new java.awt.Dimension(467, 500));
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        setupButtonPanel();
        setupTabPane();
        setupFormLayout();
        setupDataTable();

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
        pack();
    }

    private void setupButtonPanel() {
        panelGlass8.setName("panelGlass8");
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 54));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan");
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpan.addActionListener(this::BtnSimpanActionPerformed);
        BtnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSimpanKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnSimpan);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Baru");
        BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setName("BtnBatal");
        BtnBatal.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBatal.addActionListener(this::BtnBatalActionPerformed);
        BtnBatal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnBatalKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnBatal);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus");
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapus.addActionListener(this::BtnHapusActionPerformed);
        BtnHapus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnHapusKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnHapus);

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnEdit.setMnemonic('G');
        BtnEdit.setText("Ganti");
        BtnEdit.setToolTipText("Alt+G");
        BtnEdit.setName("BtnEdit");
        BtnEdit.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit.addActionListener(this::BtnEditActionPerformed);
        BtnEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnEditKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnEdit);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setName("BtnPrint");
        BtnPrint.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnPrint.addActionListener(this::BtnPrintActionPerformed);
        BtnPrint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnPrintKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnPrint);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setMnemonic('M');
        BtnAll.setText("Semua");
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll");
        BtnAll.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnAll.addActionListener(this::BtnAllActionPerformed);
        BtnAll.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnAllKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnAll);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar");
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(this::BtnKeluarActionPerformed);
        BtnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnKeluarKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnKeluar);

        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);
    }

    private void setupTabPane() {
        TabRawat.setBackground(new java.awt.Color(254, 255, 254));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11));
        TabRawat.setName("TabRawat");
        TabRawat.setPreferredSize(new java.awt.Dimension(457, 480));

        // Initialize combo boxes
        KeadaanUmum.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "Baik", "Sedang", "Buruk"
        }));

        Mallampati.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "I", "II", "III", "IV"
        }));

        ASA.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "1", "2", "3", "4", "5"
        }));

        PerawatanPasca.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "Rawat Jalan", "Rawat Inap", "ICU"
        }));

        TabRawat.addTab("Input Asesmen", internalFrame2);
        TabRawat.addTab("Data Asesmen", internalFrame3);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);
    }

    private void setupFormLayout() {
        FormInput.setBackground(new java.awt.Color(255, 255, 255));
        FormInput.setBorder(null);
        FormInput.setName("FormInput");
        FormInput.setPreferredSize(new java.awt.Dimension(1100, 1200));
        FormInput.setLayout(null);

        // Setup form components with positions
        jLabel1.setText("No.Rawat :");
        jLabel1.setName("jLabel1");
        FormInput.add(jLabel1);
        jLabel1.setBounds(0, 10, 70, 23);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw");
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(74, 10, 131, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM");
        FormInput.add(TNoRM);
        TNoRM.setBounds(207, 10, 100, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien");
        FormInput.add(TPasien);
        TPasien.setBounds(309, 10, 260, 23);

        TglLahir.setEditable(false);
        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir");
        FormInput.add(TglLahir);
        TglLahir.setBounds(571, 10, 80, 23);

        Jk.setEditable(false);
        Jk.setHighlighter(null);
        Jk.setName("Jk");
        FormInput.add(Jk);
        Jk.setBounds(653, 10, 70, 23);

        // Continue with other form components
        label14.setText("Dokter :");
        label14.setName("label14");
        FormInput.add(label14);
        label14.setBounds(0, 38, 70, 23);

        KdDokter.setName("KdDokter");
        FormInput.add(KdDokter);
        KdDokter.setBounds(74, 38, 100, 23);

        NmDokter.setEditable(false);
        NmDokter.setName("NmDokter");
        FormInput.add(NmDokter);
        NmDokter.setBounds(176, 38, 300, 23);

        BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokter.setMnemonic('2');
        BtnDokter.setToolTipText("Alt+2");
        BtnDokter.setName("BtnDokter");
        BtnDokter.addActionListener(this::BtnDokterActionPerformed);
        BtnDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnDokterKeyPressed(evt);
            }
        });
        FormInput.add(BtnDokter);
        BtnDokter.setBounds(479, 38, 28, 23);

        label11.setText("Tanggal :");
        label11.setName("label11");
        FormInput.add(label11);
        label11.setBounds(510, 38, 60, 23);

        TglAsesmen.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsesmen.setName("TglAsesmen");
        TglAsesmen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TglAsesmenKeyPressed(evt);
            }
        });
        FormInput.add(TglAsesmen);
        TglAsesmen.setBounds(573, 38, 150, 23);

        jLabel2.setText("Ruangan :");
        jLabel2.setName("jLabel2");
        FormInput.add(jLabel2);
        jLabel2.setBounds(0, 70, 70, 23);

        Ruangan.setName("Ruangan");
        Ruangan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RuanganKeyPressed(evt);
            }
        });
        FormInput.add(Ruangan);
        Ruangan.setBounds(74, 70, 200, 23);

        jLabel3.setText("Diagnosa :");
        jLabel3.setName("jLabel3");
        FormInput.add(jLabel3);
        jLabel3.setBounds(0, 98, 70, 23);

        Diagnosa.setName("Diagnosa");
        Diagnosa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaKeyPressed(evt);
            }
        });
        FormInput.add(Diagnosa);
        Diagnosa.setBounds(74, 98, 400, 23);

        jLabel4.setText("Tindakan Bedah :");
        jLabel4.setName("jLabel4");
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 126, 100, 23);

        TindakanBedah.setName("TindakanBedah");
        TindakanBedah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TindakanBedahKeyPressed(evt);
            }
        });
        FormInput.add(TindakanBedah);
        TindakanBedah.setBounds(104, 126, 400, 23);

        FormInput.add(jSeparator1);
        jSeparator1.setBounds(0, 156, 750, 2);

        jLabel100.setText("SUBYEKTIF");
        jLabel100.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel100.setName("jLabel100");
        FormInput.add(jLabel100);
        jLabel100.setBounds(10, 168, 100, 23);

        jLabel5.setText("BB (kg) :");
        jLabel5.setName("jLabel5");
        FormInput.add(jLabel5);
        jLabel5.setBounds(0, 196, 70, 23);

        BeratBadan.setName("BeratBadan");
        BeratBadan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BeratBadanKeyPressed(evt);
            }
        });
        FormInput.add(BeratBadan);
        BeratBadan.setBounds(74, 196, 80, 23);

        jLabel6.setText("TB (cm) :");
        jLabel6.setName("jLabel6");
        FormInput.add(jLabel6);
        jLabel6.setBounds(160, 196, 70, 23);

        TinggiBadan.setName("TinggiBadan");
        TinggiBadan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TinggiBadanKeyPressed(evt);
            }
        });
        FormInput.add(TinggiBadan);
        TinggiBadan.setBounds(234, 196, 80, 23);

        jLabel7.setText("Anamnesis :");
        jLabel7.setName("jLabel7");
        FormInput.add(jLabel7);
        jLabel7.setBounds(0, 224, 70, 23);

        Anamnesis.setColumns(20);
        Anamnesis.setRows(5);
        Anamnesis.setName("Anamnesis");
        Anamnesis.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AnamnesisKeyPressed(evt);
            }
        });
        widget.ScrollPane scrollAnamnesis = new widget.ScrollPane();
        scrollAnamnesis.setViewportView(Anamnesis);
        FormInput.add(scrollAnamnesis);
        scrollAnamnesis.setBounds(74, 224, 400, 60);

        scrollInput.setViewportView(FormInput);
        internalFrame2.add(scrollInput, java.awt.BorderLayout.CENTER);
    }

    private void setupDataTable() {
        Scroll.setName("Scroll");
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 200));

        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat.setComponentPopupMenu(jPopupMenu1);
        tbObat.setName("tbObat");
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbObatKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbObat);

        internalFrame3.add(Scroll, java.awt.BorderLayout.CENTER);

        setupSearchPanel();
    }

    private void setupSearchPanel() {
        panelGlass9.setName("panelGlass9");
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel41.setText("Tgl.Asesmen :");
        jLabel41.setName("jLabel41");
        jLabel41.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass9.add(jLabel41);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"28-08-2025"}));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1");
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari1);

        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setText("s.d.");
        jLabel42.setName("jLabel42");
        jLabel42.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel42);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"28-08-2025"}));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2");
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari2);

        jLabel43.setText("Key Word :");
        jLabel43.setName("jLabel43");
        jLabel43.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass9.add(jLabel43);

        TCari.setName("TCari");
        TCari.setPreferredSize(new java.awt.Dimension(195, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setMnemonic('3');
        BtnCari.setToolTipText("Alt+3");
        BtnCari.setName("BtnCari");
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(this::BtnCariActionPerformed);
        BtnCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariKeyPressed(evt);
            }
        });
        panelGlass9.add(BtnCari);

        jLabel44.setText("Record :");
        jLabel44.setName("jLabel44");
        jLabel44.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel44);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount");
        LCount.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(LCount);

        internalFrame3.add(panelGlass9, java.awt.BorderLayout.PAGE_END);
    }

    private void setupTableModel() {
        if (tbObat != null) {
            tbObat.setModel(tabMode);
            tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
            tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            for (i = 0; i < 58; i++) {
                TableColumn column = tbObat.getColumnModel().getColumn(i);
                if (i == 0) {
                    column.setPreferredWidth(105);
                } else if (i == 1) {
                    column.setPreferredWidth(70);
                } else if (i == 2) {
                    column.setPreferredWidth(150);
                } else if (i == 3) {
                    column.setPreferredWidth(65);
                } else if (i == 4) {
                    column.setPreferredWidth(65);
                } else if (i == 5) {
                    column.setPreferredWidth(80);
                } else if (i == 6) {
                    column.setPreferredWidth(150);
                } else if (i == 7) {
                    column.setPreferredWidth(115);
                } else if (i == 8) {
                    column.setPreferredWidth(100);
                } else if (i == 9) {
                    column.setPreferredWidth(120);
                } else if (i == 10) {
                    column.setPreferredWidth(120);
                } else {
                    column.setPreferredWidth(100);
                }
            }
            tbObat.setDefaultRenderer(Object.class, new WarnaTable());
        }
    }

    private void setupInputLimits() {
        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        Diagnosa.setDocument(new batasInput((byte) 100).getKata(Diagnosa));
        TindakanBedah.setDocument(new batasInput((byte) 100).getKata(TindakanBedah));
        BeratBadan.setDocument(new batasInput((byte) 5).getKata(BeratBadan));
        TinggiBadan.setDocument(new batasInput((byte) 5).getKata(TinggiBadan));
        TekananDarah.setDocument(new batasInput((byte) 8).getKata(TekananDarah));
        Nadi.setDocument(new batasInput((byte) 5).getKata(Nadi));
        RespirasiRate.setDocument(new batasInput((byte) 5).getKata(RespirasiRate));
        Suhu.setDocument(new batasInput((byte) 5).getKata(Suhu));
        GCS.setDocument(new batasInput((byte) 10).getKata(GCS));
        Anamnesis.setDocument(new batasInput((byte) 500).getKata(Anamnesis));
        TCari.setDocument(new batasInput((int) 100).getKata(TCari));
    }

    private void setupListeners() {
        // Fast search listener
        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }
            });
        }

        // Doctor selection dialog
        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (dokter.getTable().getSelectedRow() != -1) {
                    KdDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                    NmDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                    KdDokter.requestFocus();
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
    }

    private void setupHTMLEditor() {
        HTMLEditorKit kit = new HTMLEditorKit();
        LoadHTML.setEditable(true);
        LoadHTML.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule(
                ".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"
                + ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}"
                + ".isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"
                + ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"
                + ".isi5 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#AA0000;}"
                + ".isi6 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#FF0000;}"
                + ".isi7 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#C8C800;}"
                + ".isi8 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#00AA00;}"
                + ".isi9 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#969696;}"
        );
        Document doc = kit.createDefaultDocument();
        LoadHTML.setDocument(doc);
    }

    // Event Handlers
    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            isRawat();
        } else {
            Valid.pindah(evt, TCari, BtnDokter);
        }
    }

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
        if (TNoRM.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else if (NmDokter.getText().trim().equals("")) {
            Valid.textKosong(BtnDokter, "Dokter");
        } else if (Diagnosa.getText().trim().equals("")) {
            Valid.textKosong(Diagnosa, "Diagnosa");
        } else if (TindakanBedah.getText().trim().equals("")) {
            Valid.textKosong(TindakanBedah, "Tindakan Bedah");
        } else {
            if (Sequel.menyimpantf("asesmen_pra_anestesi", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 57, new String[]{
                TNoRw.getText(),
                Valid.SetTgl(TglAsesmen.getSelectedItem() + "") + " " + TglAsesmen.getSelectedItem().toString().substring(11, 19),
                KdDokter.getText(),
                Ruangan.getText(),
                Diagnosa.getText(),
                TindakanBedah.getText(),
                BeratBadan.getText(),
                TinggiBadan.getText(),
                Anamnesis.getText(),
                ChkAsthma != null ? (ChkAsthma.isSelected() ? "Ya" : "Tidak") : "Tidak",
                ChkAlergi != null ? (ChkAlergi.isSelected() ? "Ya" : "Tidak") : "Tidak",
                ChkDiabetes != null ? (ChkDiabetes.isSelected() ? "Ya" : "Tidak") : "Tidak",
                ChkHipertensi != null ? (ChkHipertensi.isSelected() ? "Ya" : "Tidak") : "Tidak",
                JenisAnestesiSebelum != null ? JenisAnestesiSebelum.getText() : "",
                Komplikasi != null ? Komplikasi.getText() : "",
                PemeriksaanFisik != null ? PemeriksaanFisik.getText() : "",
                KeadaanUmum.getSelectedItem().toString(),
                GCS != null ? GCS.getText() : "",
                TekananDarah != null ? TekananDarah.getText() : "",
                Nadi != null ? Nadi.getText() : "",
                RespirasiRate != null ? RespirasiRate.getText() : "",
                Suhu != null ? Suhu.getText() : "",
                GerakanLeher != null ? GerakanLeher.getText() : "",
                Methohyoid != null ? Methohyoid.getText() : "",
                Mallampati.getSelectedItem().toString(),
                Thorax != null ? Thorax.getText() : "",
                Abdomen != null ? Abdomen.getText() : "",
                Ekstremitas != null ? Ekstremitas.getText() : "",
                Laboratorium != null ? Laboratorium.getText() : "",
                EKG != null ? EKG.getText() : "",
                RoThorax != null ? RoThorax.getText() : "",
                LainLain != null ? LainLain.getText() : "",
                ASA.getSelectedItem().toString(),
                Premedikasi != null ? Premedikasi.getText() : "",
                Puasa != null ? Puasa.getText() : "",
                Penyulit != null ? Penyulit.getText() : "",
                ChkGALMA != null ? (ChkGALMA.isSelected() ? "Ya" : "Tidak") : "Tidak",
                ChkGAETT != null ? (ChkGAETT.isSelected() ? "Ya" : "Tidak") : "Tidak",
                ChkGATIVA != null ? (ChkGATIVA.isSelected() ? "Ya" : "Tidak") : "Tidak",
                ChkRegionalSpinal != null ? (ChkRegionalSpinal.isSelected() ? "Ya" : "Tidak") : "Tidak",
                ChkRegionalEpidural != null ? (ChkRegionalEpidural.isSelected() ? "Ya" : "Tidak") : "Tidak",
                ChkRegionalKaudal != null ? (ChkRegionalKaudal.isSelected() ? "Ya" : "Tidak") : "Tidak",
                ChkRegionalBlokPerifer != null ? (ChkRegionalBlokPerifer.isSelected() ? "Ya" : "Tidak") : "Tidak",
                PersediaanDarah != null ? PersediaanDarah.getText() : "",
                ChkTeknikHipotensi != null ? (ChkTeknikHipotensi.isSelected() ? "Ya" : "Tidak") : "Tidak",
                ChkTeknikVentilasiSatuParu != null ? (ChkTeknikVentilasiSatuParu.isSelected() ? "Ya" : "Tidak") : "Tidak",
                TeknikKhususLainnya != null ? TeknikKhususLainnya.getText() : "",
                ChkMonitorEKG != null ? (ChkMonitorEKG.isSelected() ? "Ya" : "Tidak") : "Tidak",
                ChkMonitorSpO2 != null ? (ChkMonitorSpO2.isSelected() ? "Ya" : "Tidak") : "Tidak",
                ChkMonitorNIBP != null ? (ChkMonitorNIBP.isSelected() ? "Ya" : "Tidak") : "Tidak",
                ChkMonitorTemp != null ? (ChkMonitorTemp.isSelected() ? "Ya" : "Tidak") : "Tidak",
                ChkMonitorCVP != null ? (ChkMonitorCVP.isSelected() ? "Ya" : "Tidak") : "Tidak",
                MonitorArteriLine != null ? MonitorArteriLine.getText() : "",
                ChkMonitorEtCO2 != null ? (ChkMonitorEtCO2.isSelected() ? "Ya" : "Tidak") : "Tidak",
                MonitorLainnya != null ? MonitorLainnya.getText() : "",
                PerawatanPasca.getSelectedItem().toString()
            }) == true) {
                emptTeks();
                tampil();
            }
        }
    }

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, PerawatanPasca, BtnBatal);
        }
    }

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {
        emptTeks();
    }

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            emptTeks();
        } else {
            Valid.pindah(evt, BtnSimpan, BtnHapus);
        }
    }

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {
        if (tbObat.getSelectedRow() > -1) {
            if (akses.getkode().equals("Admin Utama")) {
                hapus();
            } else {
                if (KdDokter.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString())) {
                    hapus();
                } else {
                    JOptionPane.showMessageDialog(null, "Hanya bisa dihapus oleh dokter yang bersangkutan..!!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
        }
    }

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnHapusActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
    }

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {
        if (TNoRM.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else if (NmDokter.getText().trim().equals("")) {
            Valid.textKosong(BtnDokter, "Dokter");
        } else if (Diagnosa.getText().trim().equals("")) {
            Valid.textKosong(Diagnosa, "Diagnosa");
        } else if (TindakanBedah.getText().trim().equals("")) {
            Valid.textKosong(TindakanBedah, "Tindakan Bedah");
        } else {
            if (tbObat.getSelectedRow() > -1) {
                if (akses.getkode().equals("Admin Utama")) {
                    ganti();
                } else {
                    if (KdDokter.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString())) {
                        ganti();
                    } else {
                        JOptionPane.showMessageDialog(null, "Hanya bisa diganti oleh dokter yang bersangkutan..!!");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
            }
        }
    }

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnEditActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
    }

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnKeluarActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnEdit, TCari);
        }
    }

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        } else if (tabMode.getRowCount() != 0) {
            try {
                htmlContent = new StringBuilder();
                htmlContent.append(
                        "<tr class='isi'>"
                        + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>No.Rawat</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>No.RM</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Nama Pasien</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Tgl.Lahir</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>J.K.</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Kode Dokter</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Nama Dokter</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Tanggal</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Ruangan</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Diagnosa</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Tindakan Bedah</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>BB</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>TB</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>ASA</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Perawatan Pasca</b></td>"
                        + "</tr>"
                );

                for (int i = 0; i < tabMode.getRowCount(); i++) {
                    htmlContent.append(
                            "<tr class='isi'>"
                            + "<td valign='top'>" + tabMode.getValueAt(i, 0).toString() + "</td>"
                            + "<td valign='top'>" + tabMode.getValueAt(i, 1).toString() + "</td>"
                            + "<td valign='top'>" + tabMode.getValueAt(i, 2).toString() + "</td>"
                            + "<td valign='top'>" + tabMode.getValueAt(i, 3).toString() + "</td>"
                            + "<td valign='top'>" + tabMode.getValueAt(i, 4).toString() + "</td>"
                            + "<td valign='top'>" + tabMode.getValueAt(i, 5).toString() + "</td>"
                            + "<td valign='top'>" + tabMode.getValueAt(i, 6).toString() + "</td>"
                            + "<td valign='top'>" + tabMode.getValueAt(i, 7).toString() + "</td>"
                            + "<td valign='top'>" + tabMode.getValueAt(i, 8).toString() + "</td>"
                            + "<td valign='top'>" + tabMode.getValueAt(i, 9).toString() + "</td>"
                            + "<td valign='top'>" + tabMode.getValueAt(i, 10).toString() + "</td>"
                            + "<td valign='top'>" + tabMode.getValueAt(i, 11).toString() + "</td>"
                            + "<td valign='top'>" + tabMode.getValueAt(i, 12).toString() + "</td>"
                            + "<td valign='top'>" + tabMode.getValueAt(i, 37).toString() + "</td>"
                            + "<td valign='top'>" + tabMode.getValueAt(i, 57).toString() + "</td>"
                            + "</tr>"
                    );
                }

                LoadHTML.setText(
                        "<html>"
                        + "<table width='1500px' border='0' align='center' cellpadding='1px' cellspacing='0' class='tbl_form'>"
                        + htmlContent.toString()
                        + "</table>"
                        + "</html>"
                );

                File g = new File("file2.css");
                BufferedWriter bg = new BufferedWriter(new FileWriter(g));
                bg.write(
                        ".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"
                        + ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}"
                        + ".isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"
                        + ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"
                        + ".isi5 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#AA0000;}"
                        + ".isi6 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#FF0000;}"
                        + ".isi7 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#C8C800;}"
                        + ".isi8 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#00AA00;}"
                        + ".isi9 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#969696;}"
                );
                bg.close();

                File f = new File("DataAsesmenPraAnestesi.html");
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                bw.write(LoadHTML.getText().replaceAll("<head>", "<head>"
                        + "<link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" />"
                        + "<table width='1500px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"
                        + "<tr class='isi2'>"
                        + "<td valign='top' align='center'>"
                        + "<font size='4' face='Tahoma'>" + akses.getnamars() + "</font><br>"
                        + akses.getalamatrs() + ", " + akses.getkabupatenrs() + ", " + akses.getpropinsirs() + "<br>"
                        + akses.getkontakrs() + ", E-mail : " + akses.getemailrs() + "<br><br>"
                        + "<font size='2' face='Tahoma'>DATA ASESMEN PRA ANESTESI<br><br></font>"
                        + "</td>"
                        + "</tr>"
                        + "</table>")
                );
                bw.close();
                Desktop.getDesktop().browse(f.toURI());
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPrintActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnEdit, BtnKeluar);
        }
    }

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnCari.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            BtnKeluar.requestFocus();
        }
    }

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {
        tampil();
    }

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnCariActionPerformed(null);
        } else {
            Valid.pindah(evt, TCari, BtnAll);
        }
    }

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {
        TCari.setText("");
        tampil();
    }

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            TCari.setText("");
            tampil();
        } else {
            Valid.pindah(evt, BtnCari, TPasien);
        }
    }

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
            if ((evt.getClickCount() == 2) && (tbObat.getSelectedColumn() == 0)) {
                TabRawat.setSelectedIndex(0);
            }
        }
    }

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {
        if (tabMode.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            } else if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                try {
                    getData();
                    TabRawat.setSelectedIndex(0);
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }

    private void BtnDokterActionPerformed(java.awt.event.ActionEvent evt) {
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setAlwaysOnTop(false);
        dokter.setVisible(true);
    }

    private void BtnDokterKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, TNoRw, TglAsesmen);
    }

    private void MnAsesmenPraAnestesiActionPerformed(java.awt.event.ActionEvent evt) {
        if (tbObat.getSelectedRow() > -1) {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            param.put("logo2", Sequel.cariGambar("select setting.logo2 from setting"));
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString() + "\nID " + (finger.equals("") ? tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString() : finger) + "\n" + Valid.SetTgl3(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString()));

            Valid.MyReportqry("rptCetakAsesmenPraAnestesi.jasper", "report", "::[ Laporan Asesmen Pra Anestesi ]::",
                    "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir,"
                    + "asesmen_pra_anestesi.tanggal,asesmen_pra_anestesi.kd_dokter,dokter.nm_dokter,asesmen_pra_anestesi.* "
                    + "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                    + "inner join asesmen_pra_anestesi on reg_periksa.no_rawat=asesmen_pra_anestesi.no_rawat "
                    + "inner join dokter on asesmen_pra_anestesi.kd_dokter=dokter.kd_dokter where asesmen_pra_anestesi.no_rawat='" + tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "' "
                    + "and asesmen_pra_anestesi.tanggal='" + tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString() + "'", param);
        }
    }

    // Field event handlers
    private void DiagnosaKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, TglAsesmen, TindakanBedah);
    }

    private void TindakanBedahKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, Diagnosa, Ruangan);
    }

    private void RuanganKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, TindakanBedah, BeratBadan);
    }

    private void BeratBadanKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, Ruangan, TinggiBadan);
    }

    private void TinggiBadanKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, BeratBadan, Anamnesis);
    }

    private void AnamnesisKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah2(evt, TinggiBadan, JenisAnestesiSebelum);
    }

    private void TglAsesmenKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, BtnDokter, Diagnosa);
    }

    // Utility methods
    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            if (TCari.getText().trim().equals("")) {
                ps = koneksi.prepareStatement(
                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir,"
                        + "asesmen_pra_anestesi.tanggal,asesmen_pra_anestesi.kd_dokter,dokter.nm_dokter,asesmen_pra_anestesi.ruangan,"
                        + "asesmen_pra_anestesi.diagnosa,asesmen_pra_anestesi.tindakan_bedah,asesmen_pra_anestesi.bb,asesmen_pra_anestesi.tb,"
                        + "asesmen_pra_anestesi.anamnesis,asesmen_pra_anestesi.riwayat_asthma,asesmen_pra_anestesi.riwayat_alergi,"
                        + "asesmen_pra_anestesi.riwayat_dm,asesmen_pra_anestesi.riwayat_hipertensi,asesmen_pra_anestesi.jenis_anestesi_sebelum,"
                        + "asesmen_pra_anestesi.komplikasi,asesmen_pra_anestesi.pemeriksaan_fisik,asesmen_pra_anestesi.keadaan_umum,"
                        + "asesmen_pra_anestesi.gcs,asesmen_pra_anestesi.td,asesmen_pra_anestesi.nadi,asesmen_pra_anestesi.rr,"
                        + "asesmen_pra_anestesi.suhu,asesmen_pra_anestesi.gerakan_leher,asesmen_pra_anestesi.methohyoid,"
                        + "asesmen_pra_anestesi.mallampati,asesmen_pra_anestesi.thorax,asesmen_pra_anestesi.abdomen,"
                        + "asesmen_pra_anestesi.ekstremitas,asesmen_pra_anestesi.laboratorium,asesmen_pra_anestesi.ekg,"
                        + "asesmen_pra_anestesi.ro_thorax,asesmen_pra_anestesi.lain_lain,asesmen_pra_anestesi.asa,"
                        + "asesmen_pra_anestesi.premedikasi,asesmen_pra_anestesi.puasa,asesmen_pra_anestesi.penyulit,"
                        + "asesmen_pra_anestesi.ga_lma,asesmen_pra_anestesi.ga_ett,asesmen_pra_anestesi.ga_tiva,"
                        + "asesmen_pra_anestesi.regional_spinal,asesmen_pra_anestesi.regional_epidural,asesmen_pra_anestesi.regional_kaudal,"
                        + "asesmen_pra_anestesi.regional_blok_perifer,asesmen_pra_anestesi.persediaan_darah,"
                        + "asesmen_pra_anestesi.teknik_hipotensi,asesmen_pra_anestesi.teknik_ventilasi_satu_paru,"
                        + "asesmen_pra_anestesi.teknik_khusus_lainnya,asesmen_pra_anestesi.monitor_ekg,asesmen_pra_anestesi.monitor_spo2,"
                        + "asesmen_pra_anestesi.monitor_nibp,asesmen_pra_anestesi.monitor_temp,asesmen_pra_anestesi.monitor_cvp,"
                        + "asesmen_pra_anestesi.monitor_arteri_line,asesmen_pra_anestesi.monitor_etco2,asesmen_pra_anestesi.monitor_lainnya,"
                        + "asesmen_pra_anestesi.perawatan_pasca "
                        + "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "inner join asesmen_pra_anestesi on reg_periksa.no_rawat=asesmen_pra_anestesi.no_rawat "
                        + "inner join dokter on asesmen_pra_anestesi.kd_dokter=dokter.kd_dokter where "
                        + "asesmen_pra_anestesi.tanggal between ? and ? order by asesmen_pra_anestesi.tanggal");
            } else {
                ps = koneksi.prepareStatement(
                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir,"
                        + "asesmen_pra_anestesi.tanggal,asesmen_pra_anestesi.kd_dokter,dokter.nm_dokter,asesmen_pra_anestesi.ruangan,"
                        + "asesmen_pra_anestesi.diagnosa,asesmen_pra_anestesi.tindakan_bedah,asesmen_pra_anestesi.bb,asesmen_pra_anestesi.tb,"
                        + "asesmen_pra_anestesi.anamnesis,asesmen_pra_anestesi.riwayat_asthma,asesmen_pra_anestesi.riwayat_alergi,"
                        + "asesmen_pra_anestesi.riwayat_dm,asesmen_pra_anestesi.riwayat_hipertensi,asesmen_pra_anestesi.jenis_anestesi_sebelum,"
                        + "asesmen_pra_anestesi.komplikasi,asesmen_pra_anestesi.pemeriksaan_fisik,asesmen_pra_anestesi.keadaan_umum,"
                        + "asesmen_pra_anestesi.gcs,asesmen_pra_anestesi.td,asesmen_pra_anestesi.nadi,asesmen_pra_anestesi.rr,"
                        + "asesmen_pra_anestesi.suhu,asesmen_pra_anestesi.gerakan_leher,asesmen_pra_anestesi.methohyoid,"
                        + "asesmen_pra_anestesi.mallampati,asesmen_pra_anestesi.thorax,asesmen_pra_anestesi.abdomen,"
                        + "asesmen_pra_anestesi.ekstremitas,asesmen_pra_anestesi.laboratorium,asesmen_pra_anestesi.ekg,"
                        + "asesmen_pra_anestesi.ro_thorax,asesmen_pra_anestesi.lain_lain,asesmen_pra_anestesi.asa,"
                        + "asesmen_pra_anestesi.premedikasi,asesmen_pra_anestesi.puasa,asesmen_pra_anestesi.penyulit,"
                        + "asesmen_pra_anestesi.ga_lma,asesmen_pra_anestesi.ga_ett,asesmen_pra_anestesi.ga_tiva,"
                        + "asesmen_pra_anestesi.regional_spinal,asesmen_pra_anestesi.regional_epidural,asesmen_pra_anestesi.regional_kaudal,"
                        + "asesmen_pra_anestesi.regional_blok_perifer,asesmen_pra_anestesi.persediaan_darah,"
                        + "asesmen_pra_anestesi.teknik_hipotensi,asesmen_pra_anestesi.teknik_ventilasi_satu_paru,"
                        + "asesmen_pra_anestesi.teknik_khusus_lainnya,asesmen_pra_anestesi.monitor_ekg,asesmen_pra_anestesi.monitor_spo2,"
                        + "asesmen_pra_anestesi.monitor_nibp,asesmen_pra_anestesi.monitor_temp,asesmen_pra_anestesi.monitor_cvp,"
                        + "asesmen_pra_anestesi.monitor_arteri_line,asesmen_pra_anestesi.monitor_etco2,asesmen_pra_anestesi.monitor_lainnya,"
                        + "asesmen_pra_anestesi.perawatan_pasca "
                        + "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "inner join asesmen_pra_anestesi on reg_periksa.no_rawat=asesmen_pra_anestesi.no_rawat "
                        + "inner join dokter on asesmen_pra_anestesi.kd_dokter=dokter.kd_dokter where "
                        + "asesmen_pra_anestesi.tanggal between ? and ? and (reg_periksa.no_rawat like ? or pasien.no_rkm_medis like ? or "
                        + "pasien.nm_pasien like ? or asesmen_pra_anestesi.kd_dokter like ? or dokter.nm_dokter like ?) order by asesmen_pra_anestesi.tanggal");
            }

            try {
                if (TCari.getText().trim().equals("")) {
                    ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                    ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                } else {
                    ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                    ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                    ps.setString(3, "%" + TCari.getText() + "%");
                    ps.setString(4, "%" + TCari.getText() + "%");
                    ps.setString(5, "%" + TCari.getText() + "%");
                    ps.setString(6, "%" + TCari.getText() + "%");
                    ps.setString(7, "%" + TCari.getText() + "%");
                }
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("tgl_lahir"), rs.getString("jk"), rs.getString("kd_dokter"), rs.getString("nm_dokter"),
                        rs.getString("tanggal"), rs.getString("ruangan"), rs.getString("diagnosa"), rs.getString("tindakan_bedah"),
                        rs.getString("bb"), rs.getString("tb"), rs.getString("anamnesis"), rs.getString("riwayat_asthma"),
                        rs.getString("riwayat_alergi"), rs.getString("riwayat_dm"), rs.getString("riwayat_hipertensi"),
                        rs.getString("jenis_anestesi_sebelum"), rs.getString("komplikasi"), rs.getString("pemeriksaan_fisik"),
                        rs.getString("keadaan_umum"), rs.getString("gcs"), rs.getString("td"), rs.getString("nadi"),
                        rs.getString("rr"), rs.getString("suhu"), rs.getString("gerakan_leher"), rs.getString("methohyoid"),
                        rs.getString("mallampati"), rs.getString("thorax"), rs.getString("abdomen"), rs.getString("ekstremitas"),
                        rs.getString("laboratorium"), rs.getString("ekg"), rs.getString("ro_thorax"), rs.getString("lain_lain"),
                        rs.getString("asa"), rs.getString("premedikasi"), rs.getString("puasa"), rs.getString("penyulit"),
                        rs.getString("ga_lma"), rs.getString("ga_ett"), rs.getString("ga_tiva"), rs.getString("regional_spinal"),
                        rs.getString("regional_epidural"), rs.getString("regional_kaudal"), rs.getString("regional_blok_perifer"),
                        rs.getString("persediaan_darah"), rs.getString("teknik_hipotensi"), rs.getString("teknik_ventilasi_satu_paru"),
                        rs.getString("teknik_khusus_lainnya"), rs.getString("monitor_ekg"), rs.getString("monitor_spo2"),
                        rs.getString("monitor_nibp"), rs.getString("monitor_temp"), rs.getString("monitor_cvp"),
                        rs.getString("monitor_arteri_line"), rs.getString("monitor_etco2"), rs.getString("monitor_lainnya"),
                        rs.getString("perawatan_pasca")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        LCount.setText("" + tabMode.getRowCount());
    }

    public void emptTeks() {
        Diagnosa.setText("");
        TindakanBedah.setText("");
        Ruangan.setText("");
        BeratBadan.setText("");
        TinggiBadan.setText("");
        Anamnesis.setText("");
        if (ChkAsthma != null) {
            ChkAsthma.setSelected(false);
        }
        if (ChkAlergi != null) {
            ChkAlergi.setSelected(false);
        }
        if (ChkDiabetes != null) {
            ChkDiabetes.setSelected(false);
        }
        if (ChkHipertensi != null) {
            ChkHipertensi.setSelected(false);
        }
        if (JenisAnestesiSebelum != null) {
            JenisAnestesiSebelum.setText("");
        }
        if (Komplikasi != null) {
            Komplikasi.setText("");
        }
        if (PemeriksaanFisik != null) {
            PemeriksaanFisik.setText("");
        }
        if (KeadaanUmum != null) {
            KeadaanUmum.setSelectedIndex(0);
        }
        if (GCS != null) {
            GCS.setText("");
        }
        if (TekananDarah != null) {
            TekananDarah.setText("");
        }
        if (Nadi != null) {
            Nadi.setText("");
        }
        if (RespirasiRate != null) {
            RespirasiRate.setText("");
        }
        if (Suhu != null) {
            Suhu.setText("");
        }
        if (GerakanLeher != null) {
            GerakanLeher.setText("");
        }
        if (Methohyoid != null) {
            Methohyoid.setText("");
        }
        if (Mallampati != null) {
            Mallampati.setSelectedIndex(0);
        }
        if (Thorax != null) {
            Thorax.setText("");
        }
        if (Abdomen != null) {
            Abdomen.setText("");
        }
        if (Ekstremitas != null) {
            Ekstremitas.setText("");
        }
        if (Laboratorium != null) {
            Laboratorium.setText("");
        }
        if (EKG != null) {
            EKG.setText("");
        }
        if (RoThorax != null) {
            RoThorax.setText("");
        }
        if (LainLain != null) {
            LainLain.setText("");
        }
        if (ASA != null) {
            ASA.setSelectedIndex(0);
        }
        if (Premedikasi != null) {
            Premedikasi.setText("");
        }
        if (Puasa != null) {
            Puasa.setText("");
        }
        if (Penyulit != null) {
            Penyulit.setText("");
        }
        if (ChkGALMA != null) {
            ChkGALMA.setSelected(false);
        }
        if (ChkGATIVA != null) {
            ChkGATIVA.setSelected(false);
        }
        if (ChkRegionalSpinal != null) {
            ChkRegionalSpinal.setSelected(false);
        }
        if (ChkRegionalEpidural != null) {
            ChkRegionalEpidural.setSelected(false);
        }
        if (ChkRegionalKaudal != null) {
            ChkRegionalKaudal.setSelected(false);
        }
        if (ChkRegionalBlokPerifer != null) {
            ChkRegionalBlokPerifer.setSelected(false);
        }
        if (PersediaanDarah != null) {
            PersediaanDarah.setText("");
        }
        if (ChkTeknikHipotensi != null) {
            ChkTeknikHipotensi.setSelected(false);
        }
        if (ChkTeknikVentilasiSatuParu != null) {
            ChkTeknikVentilasiSatuParu.setSelected(false);
        }
        if (TeknikKhususLainnya != null) {
            TeknikKhususLainnya.setText("");
        }
        if (ChkMonitorEKG != null) {
            ChkMonitorEKG.setSelected(false);
        }
        if (ChkMonitorSpO2 != null) {
            ChkMonitorSpO2.setSelected(false);
        }
        if (ChkMonitorNIBP != null) {
            ChkMonitorNIBP.setSelected(false);
        }
        if (ChkMonitorTemp != null) {
            ChkMonitorTemp.setSelected(false);
        }
        if (ChkMonitorCVP != null) {
            ChkMonitorCVP.setSelected(false);
        }
        if (MonitorArteriLine != null) {
            MonitorArteriLine.setText("");
        }
        if (ChkMonitorEtCO2 != null) {
            ChkMonitorEtCO2.setSelected(false);
        }
        if (MonitorLainnya != null) {
            MonitorLainnya.setText("");
        }
        if (PerawatanPasca != null) {
            PerawatanPasca.setSelectedIndex(0);
        }
        if (TglAsesmen != null) {
            TglAsesmen.setDate(new Date());
        }
        if (TabRawat != null) {
            TabRawat.setSelectedIndex(0);
        }
        if (Diagnosa != null) {
            Diagnosa.requestFocus();
        }
    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            TglLahir.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
            Jk.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
            KdDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            NmDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
            Valid.SetTgl2(TglAsesmen, tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString());
            Ruangan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString());
            Diagnosa.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString());
            TindakanBedah.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString());
            BeratBadan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString());
            TinggiBadan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString());
            Anamnesis.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 13).toString());
            if (ChkAsthma != null) {
                ChkAsthma.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 14).toString().equals("Ya"));
            }
            if (ChkAlergi != null) {
                ChkAlergi.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 15).toString().equals("Ya"));
            }
            if (ChkDiabetes != null) {
                ChkDiabetes.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 16).toString().equals("Ya"));
            }
            if (ChkHipertensi != null) {
                ChkHipertensi.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 17).toString().equals("Ya"));
            }
            if (JenisAnestesiSebelum != null) {
                JenisAnestesiSebelum.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 18).toString());
            }
            if (Komplikasi != null) {
                Komplikasi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 19).toString());
            }
            if (PemeriksaanFisik != null) {
                PemeriksaanFisik.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 20).toString());
            }
            if (KeadaanUmum != null) {
                KeadaanUmum.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 21).toString());
            }
            if (GCS != null) {
                GCS.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 22).toString());
            }
            if (TekananDarah != null) {
                TekananDarah.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 23).toString());
            }
            if (Nadi != null) {
                Nadi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 24).toString());
            }
            if (RespirasiRate != null) {
                RespirasiRate.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 25).toString());
            }
            if (Suhu != null) {
                Suhu.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 26).toString());
            }
            if (GerakanLeher != null) {
                GerakanLeher.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 27).toString());
            }
            if (Methohyoid != null) {
                Methohyoid.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 28).toString());
            }
            if (Mallampati != null) {
                Mallampati.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 29).toString());
            }
            if (Thorax != null) {
                Thorax.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 30).toString());
            }
            if (Abdomen != null) {
                Abdomen.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 31).toString());
            }
            if (Ekstremitas != null) {
                Ekstremitas.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 32).toString());
            }
            if (Laboratorium != null) {
                Laboratorium.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 33).toString());
            }
            if (EKG != null) {
                EKG.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 34).toString());
            }
            if (RoThorax != null) {
                RoThorax.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 35).toString());
            }
            if (LainLain != null) {
                LainLain.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 36).toString());
            }
            if (ASA != null) {
                ASA.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 37).toString());
            }
            if (Premedikasi != null) {
                Premedikasi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 38).toString());
            }
            if (Puasa != null) {
                Puasa.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 39).toString());
            }
            if (Penyulit != null) {
                Penyulit.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 40).toString());
            }
            if (ChkGALMA != null) {
                ChkGALMA.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 41).toString().equals("Ya"));
            }
            if (ChkGAETT != null) {
                ChkGAETT.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 42).toString().equals("Ya"));
            }
            if (ChkGATIVA != null) {
                ChkGATIVA.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 43).toString().equals("Ya"));
            }
            if (ChkRegionalSpinal != null) {
                ChkRegionalSpinal.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 44).toString().equals("Ya"));
            }
            if (ChkRegionalEpidural != null) {
                ChkRegionalEpidural.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 45).toString().equals("Ya"));
            }
            if (ChkRegionalKaudal != null) {
                ChkRegionalKaudal.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 46).toString().equals("Ya"));
            }
            if (ChkRegionalBlokPerifer != null) {
                ChkRegionalBlokPerifer.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 47).toString().equals("Ya"));
            }
            if (PersediaanDarah != null) {
                PersediaanDarah.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 48).toString());
            }
            if (ChkTeknikHipotensi != null) {
                ChkTeknikHipotensi.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 49).toString().equals("Ya"));
            }
            if (ChkTeknikVentilasiSatuParu != null) {
                ChkTeknikVentilasiSatuParu.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 50).toString().equals("Ya"));
            }
            if (TeknikKhususLainnya != null) {
                TeknikKhususLainnya.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 51).toString());
            }
            if (ChkMonitorEKG != null) {
                ChkMonitorEKG.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 52).toString().equals("Ya"));
            }
            if (ChkMonitorSpO2 != null) {
                ChkMonitorSpO2.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 53).toString().equals("Ya"));
            }
            if (ChkMonitorNIBP != null) {
                ChkMonitorNIBP.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 54).toString().equals("Ya"));
            }
            if (ChkMonitorTemp != null) {
                ChkMonitorTemp.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 55).toString().equals("Ya"));
            }
            if (ChkMonitorCVP != null) {
                ChkMonitorCVP.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 56).toString().equals("Ya"));
            }
            if (MonitorArteriLine != null) {
                MonitorArteriLine.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 57).toString());
            }
            if (ChkMonitorEtCO2 != null) {
                ChkMonitorEtCO2.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 58).toString().equals("Ya"));
            }
            if (MonitorLainnya != null) {
                MonitorLainnya.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 59).toString());
            }
            if (PerawatanPasca != null) {
                PerawatanPasca.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 60).toString());
            }
        }
    }

    private void isRawat() {
        try {
            ps = koneksi.prepareStatement(
                    "select reg_periksa.no_rkm_medis,pasien.nm_pasien, if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir,reg_periksa.tgl_registrasi "
                    + "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                    + "where reg_periksa.no_rawat=?");
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    if (DTPCari1 != null) {
                        DTPCari1.setDate(rs.getDate("tgl_registrasi"));
                    }
                    TPasien.setText(rs.getString("nm_pasien"));
                    Jk.setText(rs.getString("jk"));
                    TglLahir.setText(rs.getString("tgl_lahir"));
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        if (DTPCari2 != null) {
            DTPCari2.setDate(tgl2);
        }
        isRawat();
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getpenilaian_pre_anestesi());
        BtnHapus.setEnabled(akses.getpenilaian_pre_anestesi());
        BtnEdit.setEnabled(akses.getpenilaian_pre_anestesi());

        if (akses.getjml2() >= 1) {
            KdDokter.setEditable(false);
            BtnDokter.setEnabled(false);
            KdDokter.setText(akses.getkode());
            Sequel.cariIsi("select dokter.nm_dokter from dokter where dokter.kd_dokter=?", NmDokter, KdDokter.getText());
            if (NmDokter.getText().equals("")) {
                KdDokter.setText("");
                JOptionPane.showMessageDialog(null, "User login bukan Dokter...!!");
            }
        }
    }

    public void setTampil() {
        if (TabRawat != null) {
            TabRawat.setSelectedIndex(1);
        }
        tampil();
    }

    private void hapus() {
        if (Sequel.queryu2tf("delete from asesmen_pra_anestesi where no_rawat=? and tanggal=?", 2, new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString()
        }) == true) {
            tampil();
            if (TabRawat != null) {
                TabRawat.setSelectedIndex(1);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
        }
    }

    private void ganti() {
        if (Sequel.mengedittf("asesmen_pra_anestesi", "no_rawat=? and tanggal=?", "no_rawat=?,tanggal=?,kd_dokter=?,ruangan=?,diagnosa=?,tindakan_bedah=?,bb=?,tb=?,anamnesis=?,"
                + "riwayat_asthma=?,riwayat_alergi=?,riwayat_dm=?,riwayat_hipertensi=?,jenis_anestesi_sebelum=?,komplikasi=?,pemeriksaan_fisik=?,keadaan_umum=?,gcs=?,td=?,nadi=?,rr=?,suhu=?,"
                + "gerakan_leher=?,methohyoid=?,mallampati=?,thorax=?,abdomen=?,ekstremitas=?,laboratorium=?,ekg=?,ro_thorax=?,lain_lain=?,asa=?,premedikasi=?,puasa=?,penyulit=?,"
                + "ga_lma=?,ga_ett=?,ga_tiva=?,regional_spinal=?,regional_epidural=?,regional_kaudal=?,regional_blok_perifer=?,persediaan_darah=?,teknik_hipotensi=?,"
                + "teknik_ventilasi_satu_paru=?,teknik_khusus_lainnya=?,monitor_ekg=?,monitor_spo2=?,monitor_nibp=?,monitor_temp=?,monitor_cvp=?,monitor_arteri_line=?,"
                + "monitor_etco2=?,monitor_lainnya=?,perawatan_pasca=?", 59, new String[]{
                    TNoRw.getText(),
                    Valid.SetTgl(TglAsesmen.getSelectedItem() + "") + " " + TglAsesmen.getSelectedItem().toString().substring(11, 19),
                    KdDokter.getText(),
                    Ruangan.getText(),
                    Diagnosa.getText(),
                    TindakanBedah.getText(),
                    BeratBadan.getText(),
                    TinggiBadan.getText(),
                    Anamnesis.getText(),
                    ChkAsthma != null ? (ChkAsthma.isSelected() ? "Ya" : "Tidak") : "Tidak",
                    ChkAlergi != null ? (ChkAlergi.isSelected() ? "Ya" : "Tidak") : "Tidak",
                    ChkDiabetes != null ? (ChkDiabetes.isSelected() ? "Ya" : "Tidak") : "Tidak",
                    ChkHipertensi != null ? (ChkHipertensi.isSelected() ? "Ya" : "Tidak") : "Tidak",
                    JenisAnestesiSebelum != null ? JenisAnestesiSebelum.getText() : "",
                    Komplikasi != null ? Komplikasi.getText() : "",
                    PemeriksaanFisik != null ? PemeriksaanFisik.getText() : "",
                    KeadaanUmum.getSelectedItem().toString(),
                    GCS != null ? GCS.getText() : "",
                    TekananDarah != null ? TekananDarah.getText() : "",
                    Nadi != null ? Nadi.getText() : "",
                    RespirasiRate != null ? RespirasiRate.getText() : "",
                    Suhu != null ? Suhu.getText() : "",
                    GerakanLeher != null ? GerakanLeher.getText() : "",
                    Methohyoid != null ? Methohyoid.getText() : "",
                    Mallampati.getSelectedItem().toString(),
                    Thorax != null ? Thorax.getText() : "",
                    Abdomen != null ? Abdomen.getText() : "",
                    Ekstremitas != null ? Ekstremitas.getText() : "",
                    Laboratorium != null ? Laboratorium.getText() : "",
                    EKG != null ? EKG.getText() : "",
                    RoThorax != null ? RoThorax.getText() : "",
                    LainLain != null ? LainLain.getText() : "",
                    ASA.getSelectedItem().toString(),
                    Premedikasi != null ? Premedikasi.getText() : "",
                    Puasa != null ? Puasa.getText() : "",
                    Penyulit != null ? Penyulit.getText() : "",
                    ChkGALMA != null ? (ChkGALMA.isSelected() ? "Ya" : "Tidak") : "Tidak",
                    ChkGAETT != null ? (ChkGAETT.isSelected() ? "Ya" : "Tidak") : "Tidak",
                    ChkGATIVA != null ? (ChkGATIVA.isSelected() ? "Ya" : "Tidak") : "Tidak",
                    ChkRegionalSpinal != null ? (ChkRegionalSpinal.isSelected() ? "Ya" : "Tidak") : "Tidak",
                    ChkRegionalEpidural != null ? (ChkRegionalEpidural.isSelected() ? "Ya" : "Tidak") : "Tidak",
                    ChkRegionalKaudal != null ? (ChkRegionalKaudal.isSelected() ? "Ya" : "Tidak") : "Tidak",
                    ChkRegionalBlokPerifer != null ? (ChkRegionalBlokPerifer.isSelected() ? "Ya" : "Tidak") : "Tidak",
                    PersediaanDarah != null ? PersediaanDarah.getText() : "",
                    ChkTeknikHipotensi != null ? (ChkTeknikHipotensi.isSelected() ? "Ya" : "Tidak") : "Tidak",
                    ChkTeknikVentilasiSatuParu != null ? (ChkTeknikVentilasiSatuParu.isSelected() ? "Ya" : "Tidak") : "Tidak",
                    TeknikKhususLainnya != null ? TeknikKhususLainnya.getText() : "",
                    ChkMonitorEKG != null ? (ChkMonitorEKG.isSelected() ? "Ya" : "Tidak") : "Tidak",
                    ChkMonitorSpO2 != null ? (ChkMonitorSpO2.isSelected() ? "Ya" : "Tidak") : "Tidak",
                    ChkMonitorNIBP != null ? (ChkMonitorNIBP.isSelected() ? "Ya" : "Tidak") : "Tidak",
                    ChkMonitorTemp != null ? (ChkMonitorTemp.isSelected() ? "Ya" : "Tidak") : "Tidak",
                    ChkMonitorCVP != null ? (ChkMonitorCVP.isSelected() ? "Ya" : "Tidak") : "Tidak",
                    MonitorArteriLine != null ? MonitorArteriLine.getText() : "",
                    ChkMonitorEtCO2 != null ? (ChkMonitorEtCO2.isSelected() ? "Ya" : "Tidak") : "Tidak",
                    MonitorLainnya != null ? MonitorLainnya.getText() : "",
                    PerawatanPasca.getSelectedItem().toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString()
                }) == true) {
            tampil();
            emptTeks();
            if (TabRawat != null) {
                TabRawat.setSelectedIndex(1);
            }
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMAsesmenPraAnestesi dialog = new RMAsesmenPraAnestesi(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }
}