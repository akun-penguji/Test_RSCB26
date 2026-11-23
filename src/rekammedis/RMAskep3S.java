package rekammedis;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariPetugas;
import widget.*;

public class RMAskep3S extends JDialog {

    // ─── DB / helpers ────────────────────────────────────────────────────────
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;

    // ─── Header fields ────────────────────────────────────────────────────────
    private TextBox TNoRw, TNoRM, TPasien, KdPetugas, NmPetugas;
    private widget.Tanggal TglAskep;
    private Button BtnPetugas, BtnSimpan, BtnBatal, BtnHapus, BtnKeluar;
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);
    {
        petugasDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (petugasDlg.getTable().getSelectedRow() != -1) {
                    KdPetugas.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(), 0).toString());
                    NmPetugas.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });
    }

    // ─── Patient list (left) ──────────────────────────────────────────────────
    private widget.Table tblList;
    private DefaultTableModel tabList;
    private TextBox TCari;
    private Button BtnCari;
    private int currentHeaderId = -1;

    // ─── Main tab pane ────────────────────────────────────────────────────────
    private JTabbedPane TabMain;

    // === TAB 1 – SDKI =========================================================
    private TextBox TSearchSdki;
    private ComboBox CmbKategori;
    private Button BtnCariSdki;
    private widget.Table tblSdki;
    private DefaultTableModel tabSdki;
    private JLabel LblNamaSdki, LblDefinisiSdki;
    private widget.Table tblMayor, tblMinor;
    private DefaultTableModel tabMayor, tabMinor;
    private ComboBox CmbEtiologi;
    private Button BtnTambahDiagnosis;
    private widget.Table tblDiagnosisTerpilih;
    private DefaultTableModel tabDiagnosisTerpilih;
    private Button BtnHapusDiagnosis, BtnSimpanDiagnosis;
    private int selectedSdkiId = -1;

    // === TAB 2 – SLKI =========================================================
    private ComboBox CmbDiagnosisSlki, CmbLuaran;
    private JSpinner SpnWaktu;
    private widget.Table tblIndikator, tblLuaranList;
    private DefaultTableModel tabIndikator, tabLuaranList;
    private Button BtnSimpanLuaran, BtnHapusLuaran;
    private int currentLuaranId = -1;

    // === TAB 3 – SIKI =========================================================
    private ComboBox CmbDiagnosisSiki;
    private widget.Table tblIntervensi, tblIntervensiList;
    private DefaultTableModel tabIntervensi, tabIntervensiList;
    private JPanel PnlTindakan;
    private JScrollPane scrollTindakan;
    private List<JCheckBox> listTindakanChk = new ArrayList<>();
    private int selectedSikiId = -1;
    private Button BtnSimpanIntervensi, BtnHapusIntervensi;
    private int currentIntervensiId = -1;

    // === TAB 4 – Evaluasi =====================================================
    private ComboBox CmbDiagnosisEval, CmbShift;
    private widget.Table tblProgressIndikator, tblEvaluasiList;
    private DefaultTableModel tabProgressIndikator, tabEvaluasiList;
    private JTextArea TAreaS, TAreaO, TAreaA, TAreaP;
    private ComboBox CmbKesimpulan;
    private Button BtnSimpanEvaluasi, BtnHapusEvaluasi;
    private int currentEvaluasiId = -1;

    // ─── Layout wrapper ──────────────────────────────────────────────────────
    private InternalFrame internalFrame1;

    // =========================================================================
    public RMAskep3S(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setUndecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(1200, 720);
        setLocationRelativeTo(parent);
        buatTabelJikaPerlu();
        initComponents();
        isCek();
        tampil();
    }

    // ─── buatTabelJikaPerlu ───────────────────────────────────────────────────
    private void buatTabelJikaPerlu() {
        try (Statement st = koneksi.createStatement()) {

            // Schema sesuai seed_master_3s.sql (DPP PPNI SDKI/SLKI/SIKI Ed.1)
            st.execute("CREATE TABLE IF NOT EXISTS mst_sdki_kategori ("
                    + "id INT NOT NULL, kode VARCHAR(10), nama VARCHAR(50), subkategori VARCHAR(100),"
                    + "PRIMARY KEY (id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            st.execute("CREATE TABLE IF NOT EXISTS mst_sdki ("
                    + "id INT NOT NULL, kode VARCHAR(10), label VARCHAR(255),"
                    + "definisi TEXT, jenis VARCHAR(20), id_kategori INT, aktif TINYINT(1) DEFAULT 1,"
                    + "PRIMARY KEY (id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            st.execute("CREATE TABLE IF NOT EXISTS mst_sdki_tanda_gejala ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, id_sdki INT NOT NULL,"
                    + "tipe VARCHAR(10), kelompok VARCHAR(20), deskripsi TEXT, urutan INT DEFAULT 0)"
                    + " ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            st.execute("CREATE TABLE IF NOT EXISTS mst_sdki_etiologi ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, id_sdki INT NOT NULL, deskripsi TEXT)"
                    + " ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            st.execute("CREATE TABLE IF NOT EXISTS mst_slki ("
                    + "id INT NOT NULL, kode VARCHAR(10), label VARCHAR(255),"
                    + "definisi TEXT, aktif TINYINT(1) DEFAULT 1,"
                    + "PRIMARY KEY (id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            st.execute("CREATE TABLE IF NOT EXISTS mst_slki_indikator ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, id_slki INT NOT NULL,"
                    + "label VARCHAR(255), tipe_skala VARCHAR(20),"
                    + "keterangan_1 VARCHAR(100), keterangan_5 VARCHAR(100), urutan INT DEFAULT 0)"
                    + " ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            st.execute("CREATE TABLE IF NOT EXISTS mst_siki ("
                    + "id INT NOT NULL, kode VARCHAR(10), label VARCHAR(255),"
                    + "definisi TEXT, tipe VARCHAR(20), aktif TINYINT(1) DEFAULT 1,"
                    + "PRIMARY KEY (id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            st.execute("CREATE TABLE IF NOT EXISTS mst_siki_tindakan ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, id_siki INT NOT NULL,"
                    + "komponen VARCHAR(20), deskripsi TEXT, urutan INT DEFAULT 0)"
                    + " ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            st.execute("CREATE TABLE IF NOT EXISTS map_sdki_slki ("
                    + "id_sdki INT NOT NULL, id_slki INT NOT NULL, prioritas INT DEFAULT 1,"
                    + "PRIMARY KEY (id_sdki, id_slki)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            st.execute("CREATE TABLE IF NOT EXISTS map_sdki_siki ("
                    + "id_sdki INT NOT NULL, id_siki INT NOT NULL, tipe VARCHAR(20),"
                    + "PRIMARY KEY (id_sdki, id_siki)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");

            st.execute("CREATE TABLE IF NOT EXISTS askep_header ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, no_rawat VARCHAR(30), no_rm VARCHAR(20),"
                    + "id_perawat VARCHAR(20), nm_perawat VARCHAR(100),"
                    + "tgl_askep DATETIME, catatan TEXT)");
            try { st.execute("ALTER TABLE askep_header MODIFY tgl_askep DATETIME"); } catch (Exception ignored) {}

            st.execute("CREATE TABLE IF NOT EXISTS askep_diagnosis ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, id_header INT, id_sdki INT,"
                    + "kode_sdki VARCHAR(20), label_sdki VARCHAR(200), etiologi TEXT,"
                    + "tanda_mayor TEXT, tanda_minor TEXT, tgl_diagnosa DATE,"
                    + "urutan INT DEFAULT 0, status VARCHAR(30) DEFAULT 'Aktif')");

            st.execute("CREATE TABLE IF NOT EXISTS askep_luaran ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, id_diagnosis INT, id_slki INT,"
                    + "label_slki VARCHAR(200), skala_awal INT, skala_target INT, waktu_target_hari INT)");

            st.execute("CREATE TABLE IF NOT EXISTS askep_luaran_indikator ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, id_luaran INT, id_indikator INT,"
                    + "label_indikator TEXT, skala_awal INT, skala_target INT)");

            st.execute("CREATE TABLE IF NOT EXISTS askep_intervensi ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, id_diagnosis INT, id_siki INT,"
                    + "label_siki VARCHAR(200), tindakan_dipilih TEXT)");

            st.execute("CREATE TABLE IF NOT EXISTS askep_evaluasi ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, id_diagnosis INT, tgl_evaluasi DATE,"
                    + "shift VARCHAR(20), soap_s TEXT, soap_o TEXT, soap_a TEXT, soap_p TEXT,"
                    + "kesimpulan VARCHAR(50), id_perawat VARCHAR(20), nm_perawat VARCHAR(100))");

            st.execute("CREATE TABLE IF NOT EXISTS askep_evaluasi_indikator ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY, id_evaluasi INT, label_indikator TEXT,"
                    + "skala_awal INT, skala_target INT, skala_saat_ini INT)");

        } catch (Exception e) {
            System.err.println("buatTabelJikaPerlu: " + e.getMessage());
        }
    }

    // ─── initComponents ───────────────────────────────────────────────────────
    private void initComponents() {
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(240, 245, 235)),
                "::[ Asuhan Keperawatan SDKI/SLKI/SIKI ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(2, 2));

        // ── Header panel ──────────────────────────────────────────────────────
        panelisi panelHeader = new panelisi();
        panelHeader.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
        panelHeader.setPreferredSize(new Dimension(1200, 38));

        Label lNoRw = new Label(); lNoRw.setText("No.Rawat:"); lNoRw.setPreferredSize(new Dimension(65, 23));
        TNoRw = new TextBox(); TNoRw.setPreferredSize(new Dimension(130, 23)); TNoRw.setEditable(false);
        Label lNoRM = new Label(); lNoRM.setText("No.RM:"); lNoRM.setPreferredSize(new Dimension(45, 23));
        TNoRM = new TextBox(); TNoRM.setPreferredSize(new Dimension(80, 23)); TNoRM.setEditable(false);
        Label lPasien = new Label(); lPasien.setText("Pasien:"); lPasien.setPreferredSize(new Dimension(45, 23));
        TPasien = new TextBox(); TPasien.setPreferredSize(new Dimension(170, 23)); TPasien.setEditable(false);
        Label lTgl = new Label(); lTgl.setText("Tgl:"); lTgl.setPreferredSize(new Dimension(25, 23));
        TglAskep = new widget.Tanggal(); TglAskep.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TglAskep.setPreferredSize(new Dimension(175, 23));
        Label lPtg = new Label(); lPtg.setText("Petugas:"); lPtg.setPreferredSize(new Dimension(55, 23));
        KdPetugas = new TextBox(); KdPetugas.setPreferredSize(new Dimension(70, 23)); KdPetugas.setEditable(false);
        NmPetugas = new TextBox(); NmPetugas.setPreferredSize(new Dimension(150, 23)); NmPetugas.setEditable(false);
        BtnPetugas = new Button(); BtnPetugas.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPetugas.setPreferredSize(new Dimension(28, 23));

        panelHeader.add(lNoRw); panelHeader.add(TNoRw);
        panelHeader.add(lNoRM); panelHeader.add(TNoRM);
        panelHeader.add(lPasien); panelHeader.add(TPasien);
        panelHeader.add(lTgl); panelHeader.add(TglAskep);
        panelHeader.add(lPtg); panelHeader.add(KdPetugas); panelHeader.add(NmPetugas); panelHeader.add(BtnPetugas);

        // ── Center split: left list + right tabs ──────────────────────────────
        JPanel panelCenter = new JPanel(new BorderLayout(2, 0));

        // Left patient list
        JPanel panelKiri = new JPanel(new BorderLayout(0, 2));
        panelKiri.setPreferredSize(new Dimension(280, 600));
        panelKiri.setBorder(BorderFactory.createTitledBorder("Daftar Askep"));

        panelisi panelCari = new panelisi();
        panelCari.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
        panelCari.setPreferredSize(new Dimension(278, 35));
        Label lCari = new Label(); lCari.setText("Cari:"); lCari.setPreferredSize(new Dimension(32, 23));
        TCari = new TextBox(); TCari.setPreferredSize(new Dimension(148, 23));
        BtnCari = new Button(); BtnCari.setText("Cari"); BtnCari.setPreferredSize(new Dimension(56, 23));
        panelCari.add(lCari); panelCari.add(TCari); panelCari.add(BtnCari);

        tabList = new DefaultTableModel(new Object[][]{}, new String[]{"No", "Pasien", "Tanggal", "id", "no_rawat", "no_rm"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblList = new widget.Table(); tblList.setModel(tabList);
        tblList.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblList.getColumnModel().getColumn(1).setPreferredWidth(140);
        tblList.getColumnModel().getColumn(2).setPreferredWidth(90);
        tblList.removeColumn(tblList.getColumnModel().getColumn(3));
        tblList.removeColumn(tblList.getColumnModel().getColumn(3));
        tblList.removeColumn(tblList.getColumnModel().getColumn(3));

        panelKiri.add(panelCari, BorderLayout.NORTH);
        panelKiri.add(new JScrollPane(tblList), BorderLayout.CENTER);

        // Right: tabs
        TabMain = new JTabbedPane();

        TabMain.addTab("1. SDKI - Diagnosis", buildTabSdki());
        TabMain.addTab("2. SLKI - Luaran", buildTabSlki());
        TabMain.addTab("3. SIKI - Intervensi", buildTabSiki());
        TabMain.addTab("4. Evaluasi", buildTabEvaluasi());

        panelCenter.add(panelKiri, BorderLayout.WEST);
        panelCenter.add(TabMain, BorderLayout.CENTER);

        // ── Bottom CRUD buttons ──────────────────────────────────────────────
        panelisi panelGlass8 = new panelisi();
        panelGlass8.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));
        panelGlass8.setPreferredSize(new Dimension(1200, 44));

        BtnSimpan = new Button();
        BtnSimpan.setIcon(new ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setText("Simpan");
        BtnSimpan.setName("BtnSimpan");
        BtnSimpan.setPreferredSize(new Dimension(100, 30));
        panelGlass8.add(BtnSimpan);

        BtnBatal = new Button();
        BtnBatal.setIcon(new ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBatal.setText("Baru");
        BtnBatal.setName("BtnBatal");
        BtnBatal.setPreferredSize(new Dimension(100, 30));
        panelGlass8.add(BtnBatal);

        BtnHapus = new Button();
        BtnHapus.setIcon(new ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setText("Hapus");
        BtnHapus.setName("BtnHapus");
        BtnHapus.setPreferredSize(new Dimension(100, 30));
        panelGlass8.add(BtnHapus);

        Button BtnLihatAskep = new Button();
        BtnLihatAskep.setIcon(new ImageIcon(getClass().getResource("/picture/category.png")));
        BtnLihatAskep.setText("Lihat Askep");
        BtnLihatAskep.setName("BtnLihatAskep");
        BtnLihatAskep.setPreferredSize(new Dimension(120, 30));
        panelGlass8.add(BtnLihatAskep);

        BtnKeluar = new Button();
        BtnKeluar.setIcon(new ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setText("Keluar");
        BtnKeluar.setName("BtnKeluar");
        BtnKeluar.setPreferredSize(new Dimension(100, 30));
        panelGlass8.add(BtnKeluar);

        internalFrame1.add(panelHeader, BorderLayout.NORTH);
        internalFrame1.add(panelCenter, BorderLayout.CENTER);
        internalFrame1.add(panelGlass8, BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        add(internalFrame1, BorderLayout.CENTER);

        // ── Event wiring ──────────────────────────────────────────────────────
        BtnKeluar.addActionListener(e -> dispose());
        BtnLihatAskep.addActionListener(e -> lihatAskep());

        BtnPetugas.addActionListener(e -> petugasDlg.setVisible(true));

        BtnSimpan.addActionListener(e -> simpanHeader());
        BtnBatal.addActionListener(e -> bersihForm());
        BtnHapus.addActionListener(e -> hapusHeader());
        BtnCari.addActionListener(e -> tampil());
        TCari.addActionListener(e -> tampil());

        tblList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { getData(); }
        });

        // Tab1
        BtnCariSdki.addActionListener(e -> loadSdki());
        TSearchSdki.addActionListener(e -> loadSdki());
        tblSdki.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) {
                int r = tblSdki.getSelectedRow();
                if (r >= 0) {
                    Object idObj = tabSdki.getValueAt(tblSdki.convertRowIndexToModel(r), 3);
                    if (idObj != null) {
                        selectedSdkiId = Integer.parseInt(idObj.toString());
                        loadDetail(selectedSdkiId);
                    }
                }
            }
        });
        BtnTambahDiagnosis.addActionListener(e -> tambahDiagnosis());
        BtnHapusDiagnosis.addActionListener(e -> hapusDiagnosisTerpilih());
        BtnSimpanDiagnosis.addActionListener(e -> simpanDiagnosis());
        tblDiagnosisTerpilih.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { loadDetailDariTerpilih(); }
        });

        // Tab2
        CmbDiagnosisSlki.addItemListener(ev -> {
            if (ev.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                Object sel = CmbDiagnosisSlki.getSelectedItem();
                if (sel instanceof DiagnosisItem) {
                    loadLuaranSuggestions(((DiagnosisItem) sel).idSdki);
                }
            }
        });
        CmbLuaran.addItemListener(ev -> {
            if (ev.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                Object sel = CmbLuaran.getSelectedItem();
                if (sel instanceof SlkiItem) {
                    loadIndikator(((SlkiItem) sel).id);
                }
            }
        });
        BtnSimpanLuaran.addActionListener(e -> simpanLuaran());
        BtnHapusLuaran.addActionListener(e -> hapusLuaran());
        tblLuaranList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { loadLuaranDariList(); }
        });

        // Tab3
        CmbDiagnosisSiki.addItemListener(ev -> {
            if (ev.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                Object sel = CmbDiagnosisSiki.getSelectedItem();
                if (sel instanceof DiagnosisItem) {
                    loadIntervensiSuggestions(((DiagnosisItem) sel).idSdki);
                }
            }
        });
        tblIntervensi.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) {
                int r = tblIntervensi.getSelectedRow();
                if (r >= 0) {
                    Object idObj = tabIntervensi.getValueAt(tblIntervensi.convertRowIndexToModel(r), 4);
                    if (idObj != null) {
                        selectedSikiId = Integer.parseInt(idObj.toString());
                        loadTindakan(selectedSikiId);
                    }
                }
            }
        });
        BtnSimpanIntervensi.addActionListener(e -> simpanIntervensi());
        BtnHapusIntervensi.addActionListener(e -> hapusIntervensi());
        tblIntervensiList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { loadIntervensiDariList(); }
        });

        // Tab4
        CmbDiagnosisEval.addItemListener(ev -> {
            if (ev.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                Object sel = CmbDiagnosisEval.getSelectedItem();
                if (sel instanceof DiagnosisItem) {
                    loadProgressIndikator(((DiagnosisItem) sel).idDiagnosis);
                }
            }
        });
        BtnSimpanEvaluasi.addActionListener(e -> simpanEvaluasi());
        BtnHapusEvaluasi.addActionListener(e -> hapusEvaluasi());
        tblEvaluasiList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { loadEvaluasiDariList(); }
        });

        loadKategoriSdki();
    }

    // ─── Build Tab 1 ──────────────────────────────────────────────────────────
    private JPanel buildTabSdki() {
        JPanel panel = new JPanel(new BorderLayout(2, 2));

        // Search row
        panelisi pSearch = new panelisi();
        pSearch.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
        pSearch.setPreferredSize(new Dimension(900, 65));
        Label lSrch = new Label(); lSrch.setText("Cari:"); lSrch.setPreferredSize(new Dimension(32, 23));
        TSearchSdki = new TextBox(); TSearchSdki.setPreferredSize(new Dimension(180, 23));
        Label lKat = new Label(); lKat.setText("Kategori:"); lKat.setPreferredSize(new Dimension(58, 23));
        CmbKategori = new ComboBox(); CmbKategori.setPreferredSize(new Dimension(180, 23));
        BtnCariSdki = new Button(); BtnCariSdki.setText("Cari SDKI"); BtnCariSdki.setPreferredSize(new Dimension(90, 23));
        pSearch.add(lSrch); pSearch.add(TSearchSdki); pSearch.add(lKat); pSearch.add(CmbKategori); pSearch.add(BtnCariSdki);

        // Left: SDKI list
        tabSdki = new DefaultTableModel(new Object[][]{}, new String[]{"Kode", "Diagnosis", "Jenis", "id"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblSdki = new widget.Table(); tblSdki.setModel(tabSdki);
        tblSdki.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblSdki.getColumnModel().getColumn(1).setPreferredWidth(220);
        tblSdki.getColumnModel().getColumn(2).setPreferredWidth(80);
        tblSdki.removeColumn(tblSdki.getColumnModel().getColumn(3));

        JPanel pLeft = new JPanel(new BorderLayout());
        pLeft.add(pSearch, BorderLayout.NORTH);
        pLeft.add(new JScrollPane(tblSdki), BorderLayout.CENTER);

        // Right: detail
        JPanel pRight = new JPanel(new BorderLayout(2, 2));
        pRight.setBorder(BorderFactory.createTitledBorder("Detail Diagnosis"));

        LblNamaSdki = new JLabel("<html><b>Pilih diagnosis SDKI</b></html>");
        LblNamaSdki.setFont(new Font("Tahoma", Font.BOLD, 12));
        LblDefinisiSdki = new JLabel("<html>-</html>");
        LblDefinisiSdki.setFont(new Font("Tahoma", 0, 11));

        JPanel pInfo = new JPanel();
        pInfo.setLayout(new BoxLayout(pInfo, BoxLayout.Y_AXIS));
        pInfo.add(LblNamaSdki);
        pInfo.add(Box.createVerticalStrut(4));
        pInfo.add(LblDefinisiSdki);

        // Tanda gejala mayor
        tabMayor = new DefaultTableModel(new Object[][]{}, new String[]{"✓", "Kelompok", "Gejala"}) {
            @Override public Class<?> getColumnClass(int col) { return col == 0 ? Boolean.class : String.class; }
            @Override public boolean isCellEditable(int r, int c) { return c == 0; }
        };
        tblMayor = new widget.Table(); tblMayor.setModel(tabMayor);
        tblMayor.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblMayor.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblMayor.getColumnModel().getColumn(2).setPreferredWidth(250);

        // Tanda gejala minor
        tabMinor = new DefaultTableModel(new Object[][]{}, new String[]{"✓", "Kelompok", "Gejala"}) {
            @Override public Class<?> getColumnClass(int col) { return col == 0 ? Boolean.class : String.class; }
            @Override public boolean isCellEditable(int r, int c) { return c == 0; }
        };
        tblMinor = new widget.Table(); tblMinor.setModel(tabMinor);
        tblMinor.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblMinor.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblMinor.getColumnModel().getColumn(2).setPreferredWidth(250);

        JPanel pGejala = new JPanel(new GridLayout(2, 1, 2, 2));
        JPanel pMayor = new JPanel(new BorderLayout());
        pMayor.setBorder(BorderFactory.createTitledBorder("Tanda Gejala Mayor"));
        pMayor.add(new JScrollPane(tblMayor), BorderLayout.CENTER);
        JPanel pMinor = new JPanel(new BorderLayout());
        pMinor.setBorder(BorderFactory.createTitledBorder("Tanda Gejala Minor"));
        pMinor.add(new JScrollPane(tblMinor), BorderLayout.CENTER);
        pGejala.add(pMayor); pGejala.add(pMinor);

        panelisi pEtiologi = new panelisi();
        pEtiologi.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
        pEtiologi.setPreferredSize(new Dimension(400, 35));
        Label lEtio = new Label(); lEtio.setText("Etiologi:"); lEtio.setPreferredSize(new Dimension(55, 23));
        CmbEtiologi = new ComboBox(); CmbEtiologi.setPreferredSize(new Dimension(250, 23));
        BtnTambahDiagnosis = new Button(); BtnTambahDiagnosis.setText("Tambah Diagnosa");
        BtnTambahDiagnosis.setIcon(new ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnTambahDiagnosis.setPreferredSize(new Dimension(150, 30));
        pEtiologi.add(lEtio); pEtiologi.add(CmbEtiologi); pEtiologi.add(BtnTambahDiagnosis);

        JPanel pDetail = new JPanel(new BorderLayout(2, 2));
        pDetail.add(pInfo, BorderLayout.NORTH);
        pDetail.add(pGejala, BorderLayout.CENTER);
        pDetail.add(pEtiologi, BorderLayout.SOUTH);

        pRight.add(pDetail, BorderLayout.CENTER);

        JSplitPane splitSDKI = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pLeft, pRight);
        splitSDKI.setDividerLocation(380);
        splitSDKI.setResizeWeight(0.4);

        // Bottom: diagnosis terpilih
        tabDiagnosisTerpilih = new DefaultTableModel(
                new Object[][]{}, new String[]{"No", "Kode", "Diagnosis", "Jenis", "Etiologi", "Status", "idSdki", "idDiag"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDiagnosisTerpilih = new widget.Table(); tblDiagnosisTerpilih.setModel(tabDiagnosisTerpilih);
        // hide hidden cols
        tblDiagnosisTerpilih.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblDiagnosisTerpilih.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblDiagnosisTerpilih.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblDiagnosisTerpilih.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblDiagnosisTerpilih.getColumnModel().getColumn(4).setPreferredWidth(180);
        tblDiagnosisTerpilih.getColumnModel().getColumn(5).setPreferredWidth(80);
        tblDiagnosisTerpilih.removeColumn(tblDiagnosisTerpilih.getColumnModel().getColumn(6));
        tblDiagnosisTerpilih.removeColumn(tblDiagnosisTerpilih.getColumnModel().getColumn(6));

        BtnHapusDiagnosis = new Button(); BtnHapusDiagnosis.setText("Hapus");
        BtnHapusDiagnosis.setIcon(new ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapusDiagnosis.setPreferredSize(new Dimension(100, 30));
        BtnSimpanDiagnosis = new Button(); BtnSimpanDiagnosis.setText("Simpan Diagnosa");
        BtnSimpanDiagnosis.setIcon(new ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpanDiagnosis.setPreferredSize(new Dimension(155, 30));

        panelisi pDiagBtn = new panelisi();
        pDiagBtn.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
        pDiagBtn.setPreferredSize(new Dimension(900, 35));
        pDiagBtn.add(new Label() {{ setText("Daftar Diagnosa Terpilih:"); setPreferredSize(new Dimension(160, 23)); }});
        pDiagBtn.add(BtnHapusDiagnosis); pDiagBtn.add(BtnSimpanDiagnosis);

        JPanel pBottom = new JPanel(new BorderLayout(0, 2));
        pBottom.setPreferredSize(new Dimension(900, 180));
        pBottom.add(pDiagBtn, BorderLayout.NORTH);
        pBottom.add(new JScrollPane(tblDiagnosisTerpilih), BorderLayout.CENTER);

        panel.add(splitSDKI, BorderLayout.CENTER);
        panel.add(pBottom, BorderLayout.SOUTH);
        return panel;
    }

    // ─── Build Tab 2 ──────────────────────────────────────────────────────────
    private JPanel buildTabSlki() {
        JPanel panel = new JPanel(new BorderLayout(2, 2));

        panelisi pTop = new panelisi();
        pTop.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
        pTop.setPreferredSize(new Dimension(900, 35));

        Label lDiag = new Label(); lDiag.setText("Diagnosa:"); lDiag.setPreferredSize(new Dimension(65, 23));
        CmbDiagnosisSlki = new ComboBox(); CmbDiagnosisSlki.setPreferredSize(new Dimension(250, 23));
        Label lLuaran = new Label(); lLuaran.setText("Luaran:"); lLuaran.setPreferredSize(new Dimension(50, 23));
        CmbLuaran = new ComboBox(); CmbLuaran.setPreferredSize(new Dimension(250, 23));
        Label lWaktu = new Label(); lWaktu.setText("Target hari:"); lWaktu.setPreferredSize(new Dimension(72, 23));
        SpnWaktu = new JSpinner(new SpinnerNumberModel(3, 1, 30, 1)); SpnWaktu.setPreferredSize(new Dimension(55, 23));
        BtnSimpanLuaran = new Button(); BtnSimpanLuaran.setText("Simpan Luaran");
        BtnSimpanLuaran.setIcon(new ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpanLuaran.setPreferredSize(new Dimension(145, 30));
        BtnHapusLuaran = new Button(); BtnHapusLuaran.setText("Hapus Luaran");
        BtnHapusLuaran.setIcon(new ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapusLuaran.setPreferredSize(new Dimension(130, 30));

        pTop.add(lDiag); pTop.add(CmbDiagnosisSlki);
        pTop.add(lLuaran); pTop.add(CmbLuaran);
        pTop.add(lWaktu); pTop.add(SpnWaktu);

        // Indikator table
        tabIndikator = new DefaultTableModel(
                new Object[][]{}, new String[]{"Indikator", "Skala Awal", "Skala Target", "Tipe"}) {
            @Override public boolean isCellEditable(int r, int c) { return c == 1 || c == 2; }
            @Override public Class<?> getColumnClass(int c) { return (c == 1 || c == 2) ? Integer.class : String.class; }
        };
        tblIndikator = new widget.Table(); tblIndikator.setModel(tabIndikator);
        tblIndikator.getColumnModel().getColumn(0).setPreferredWidth(280);
        tblIndikator.getColumnModel().getColumn(1).setPreferredWidth(90);
        tblIndikator.getColumnModel().getColumn(2).setPreferredWidth(90);
        tblIndikator.getColumnModel().getColumn(3).setPreferredWidth(100);

        Integer[] skalaNilai = {1, 2, 3, 4, 5};
        JComboBox<Integer> skalaCombo1 = new JComboBox<>(skalaNilai);
        JComboBox<Integer> skalaCombo2 = new JComboBox<>(skalaNilai);
        tblIndikator.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(skalaCombo1));
        tblIndikator.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(skalaCombo2));

        JPanel pIndikator = new JPanel(new BorderLayout());
        pIndikator.setBorder(BorderFactory.createTitledBorder("Indikator Luaran"));
        pIndikator.add(new JScrollPane(tblIndikator), BorderLayout.CENTER);

        // Luaran list
        tabLuaranList = new DefaultTableModel(
                new Object[][]{}, new String[]{"No", "Diagnosa", "Luaran", "Skala Awal", "Skala Target", "Hari", "id"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblLuaranList = new widget.Table(); tblLuaranList.setModel(tabLuaranList);
        tblLuaranList.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblLuaranList.getColumnModel().getColumn(1).setPreferredWidth(180);
        tblLuaranList.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblLuaranList.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblLuaranList.getColumnModel().getColumn(4).setPreferredWidth(80);
        tblLuaranList.getColumnModel().getColumn(5).setPreferredWidth(50);
        tblLuaranList.removeColumn(tblLuaranList.getColumnModel().getColumn(6));

        JPanel pLuaranList = new JPanel(new BorderLayout());
        pLuaranList.setBorder(BorderFactory.createTitledBorder("Daftar Luaran Tersimpan"));
        pLuaranList.add(new JScrollPane(tblLuaranList), BorderLayout.CENTER);

        JSplitPane splitSlki = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pIndikator, pLuaranList);
        splitSlki.setDividerLocation(220);
        splitSlki.setResizeWeight(0.5);

        panelisi pBotSlki = new panelisi();
        pBotSlki.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));
        pBotSlki.setPreferredSize(new Dimension(900, 44));
        pBotSlki.add(BtnSimpanLuaran);
        pBotSlki.add(BtnHapusLuaran);

        panel.add(pTop, BorderLayout.NORTH);
        panel.add(splitSlki, BorderLayout.CENTER);
        panel.add(pBotSlki, BorderLayout.SOUTH);
        return panel;
    }

    // ─── Build Tab 3 ──────────────────────────────────────────────────────────
    private JPanel buildTabSiki() {
        JPanel panel = new JPanel(new BorderLayout(2, 2));

        panelisi pTop = new panelisi();
        pTop.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
        pTop.setPreferredSize(new Dimension(900, 35));
        Label lDiag = new Label(); lDiag.setText("Diagnosa:"); lDiag.setPreferredSize(new Dimension(65, 23));
        CmbDiagnosisSiki = new ComboBox(); CmbDiagnosisSiki.setPreferredSize(new Dimension(280, 23));
        BtnSimpanIntervensi = new Button(); BtnSimpanIntervensi.setText("Simpan Intervensi");
        BtnSimpanIntervensi.setIcon(new ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpanIntervensi.setPreferredSize(new Dimension(160, 30));
        BtnHapusIntervensi = new Button(); BtnHapusIntervensi.setText("Hapus Intervensi");
        BtnHapusIntervensi.setIcon(new ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapusIntervensi.setPreferredSize(new Dimension(145, 30));
        pTop.add(lDiag); pTop.add(CmbDiagnosisSiki);

        // Left: intervensi table
        tabIntervensi = new DefaultTableModel(
                new Object[][]{}, new String[]{"", "Kode", "Intervensi", "Tipe", "id"}) {
            @Override public Class<?> getColumnClass(int col) { return col == 0 ? Boolean.class : String.class; }
            @Override public boolean isCellEditable(int r, int c) { return c == 0; }
        };
        tblIntervensi = new widget.Table(); tblIntervensi.setModel(tabIntervensi);
        tblIntervensi.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblIntervensi.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblIntervensi.getColumnModel().getColumn(2).setPreferredWidth(220);
        tblIntervensi.getColumnModel().getColumn(3).setPreferredWidth(80);
        tblIntervensi.removeColumn(tblIntervensi.getColumnModel().getColumn(4));

        JPanel pIntervensiList = new JPanel(new BorderLayout());
        pIntervensiList.setBorder(BorderFactory.createTitledBorder("Intervensi SIKI"));
        pIntervensiList.setPreferredSize(new Dimension(420, 300));
        pIntervensiList.add(new JScrollPane(tblIntervensi), BorderLayout.CENTER);

        // Right: tindakan checkboxes
        PnlTindakan = new JPanel();
        PnlTindakan.setLayout(new BoxLayout(PnlTindakan, BoxLayout.Y_AXIS));
        scrollTindakan = new JScrollPane(PnlTindakan);

        JPanel pTindakanWrap = new JPanel(new BorderLayout());
        pTindakanWrap.setBorder(BorderFactory.createTitledBorder("Pilih Tindakan"));
        pTindakanWrap.add(scrollTindakan, BorderLayout.CENTER);

        JSplitPane splitSiki = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pIntervensiList, pTindakanWrap);
        splitSiki.setDividerLocation(420);

        // Bottom: intervensi list
        tabIntervensiList = new DefaultTableModel(
                new Object[][]{}, new String[]{"No", "Diagnosa", "Intervensi", "Tindakan dipilih", "id"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblIntervensiList = new widget.Table(); tblIntervensiList.setModel(tabIntervensiList);
        tblIntervensiList.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblIntervensiList.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblIntervensiList.getColumnModel().getColumn(2).setPreferredWidth(180);
        tblIntervensiList.getColumnModel().getColumn(3).setPreferredWidth(300);
        tblIntervensiList.removeColumn(tblIntervensiList.getColumnModel().getColumn(4));

        JPanel pIntervensiSaved = new JPanel(new BorderLayout());
        pIntervensiSaved.setBorder(BorderFactory.createTitledBorder("Intervensi Tersimpan"));
        pIntervensiSaved.setPreferredSize(new Dimension(900, 160));
        pIntervensiSaved.add(new JScrollPane(tblIntervensiList), BorderLayout.CENTER);

        panelisi pBotSiki = new panelisi();
        pBotSiki.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));
        pBotSiki.setPreferredSize(new Dimension(900, 44));
        pBotSiki.add(BtnSimpanIntervensi);
        pBotSiki.add(BtnHapusIntervensi);

        JPanel pSouth = new JPanel(new BorderLayout(0, 2));
        pSouth.add(pIntervensiSaved, BorderLayout.CENTER);
        pSouth.add(pBotSiki, BorderLayout.SOUTH);

        panel.add(pTop, BorderLayout.NORTH);
        panel.add(splitSiki, BorderLayout.CENTER);
        panel.add(pSouth, BorderLayout.SOUTH);
        return panel;
    }

    // ─── Build Tab 4 ──────────────────────────────────────────────────────────
    private JPanel buildTabEvaluasi() {
        JPanel panel = new JPanel(new BorderLayout(2, 2));

        panelisi pTop = new panelisi();
        pTop.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
        pTop.setPreferredSize(new Dimension(900, 35));
        Label lDiag = new Label(); lDiag.setText("Diagnosa:"); lDiag.setPreferredSize(new Dimension(65, 23));
        CmbDiagnosisEval = new ComboBox(); CmbDiagnosisEval.setPreferredSize(new Dimension(250, 23));
        Label lShift = new Label(); lShift.setText("Shift:"); lShift.setPreferredSize(new Dimension(35, 23));
        CmbShift = new ComboBox(); CmbShift.setPreferredSize(new Dimension(100, 23));
        CmbShift.addItem("Pagi"); CmbShift.addItem("Sore"); CmbShift.addItem("Malam");
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        JLabel lTglHariIni = new JLabel("Tanggal: " + today);
        lTglHariIni.setFont(new Font("Tahoma", Font.BOLD, 11));
        BtnSimpanEvaluasi = new Button(); BtnSimpanEvaluasi.setText("Simpan Evaluasi");
        BtnSimpanEvaluasi.setIcon(new ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpanEvaluasi.setPreferredSize(new Dimension(155, 30));
        BtnHapusEvaluasi = new Button(); BtnHapusEvaluasi.setText("Hapus Evaluasi");
        BtnHapusEvaluasi.setIcon(new ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapusEvaluasi.setPreferredSize(new Dimension(140, 30));
        pTop.add(lDiag); pTop.add(CmbDiagnosisEval);
        pTop.add(lShift); pTop.add(CmbShift);
        pTop.add(lTglHariIni);

        // Progress indikator
        tabProgressIndikator = new DefaultTableModel(
                new Object[][]{}, new String[]{"Indikator", "Awal", "Target", "Saat Ini"}) {
            @Override public boolean isCellEditable(int r, int c) { return c == 3; }
            @Override public Class<?> getColumnClass(int c) { return c >= 1 ? Integer.class : String.class; }
        };
        tblProgressIndikator = new widget.Table(); tblProgressIndikator.setModel(tabProgressIndikator);
        tblProgressIndikator.getColumnModel().getColumn(0).setPreferredWidth(280);
        tblProgressIndikator.getColumnModel().getColumn(1).setPreferredWidth(60);
        tblProgressIndikator.getColumnModel().getColumn(2).setPreferredWidth(60);
        tblProgressIndikator.getColumnModel().getColumn(3).setPreferredWidth(100);
        JComboBox<Integer> skalaEval = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        tblProgressIndikator.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(skalaEval));

        JPanel pIndikatorWrap = new JPanel(new BorderLayout());
        pIndikatorWrap.setBorder(BorderFactory.createTitledBorder("Progress Indikator"));
        pIndikatorWrap.setPreferredSize(new Dimension(900, 160));
        pIndikatorWrap.add(new JScrollPane(tblProgressIndikator), BorderLayout.CENTER);

        // SOAP textareas
        TAreaS = new JTextArea(4, 30); TAreaS.setLineWrap(true); TAreaS.setWrapStyleWord(true);
        TAreaO = new JTextArea(4, 30); TAreaO.setLineWrap(true); TAreaO.setWrapStyleWord(true);
        TAreaA = new JTextArea(4, 30); TAreaA.setLineWrap(true); TAreaA.setWrapStyleWord(true);
        TAreaP = new JTextArea(4, 30); TAreaP.setLineWrap(true); TAreaP.setWrapStyleWord(true);

        JPanel pS = new JPanel(new BorderLayout()); pS.setBorder(BorderFactory.createTitledBorder("S - Subjektif")); pS.add(new JScrollPane(TAreaS));
        JPanel pO = new JPanel(new BorderLayout()); pO.setBorder(BorderFactory.createTitledBorder("O - Objektif")); pO.add(new JScrollPane(TAreaO));
        JPanel pA = new JPanel(new BorderLayout()); pA.setBorder(BorderFactory.createTitledBorder("A - Analisa")); pA.add(new JScrollPane(TAreaA));
        JPanel pP = new JPanel(new BorderLayout()); pP.setBorder(BorderFactory.createTitledBorder("P - Planning")); pP.add(new JScrollPane(TAreaP));

        JPanel pSoap = new JPanel(new GridLayout(2, 2, 4, 4));
        pSoap.add(pS); pSoap.add(pO); pSoap.add(pA); pSoap.add(pP);

        panelisi pKesimpulan = new panelisi();
        pKesimpulan.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 4));
        pKesimpulan.setPreferredSize(new Dimension(900, 35));
        Label lKes = new Label(); lKes.setText("Kesimpulan:"); lKes.setPreferredSize(new Dimension(75, 23));
        CmbKesimpulan = new ComboBox(); CmbKesimpulan.setPreferredSize(new Dimension(200, 23));
        CmbKesimpulan.addItem("Teratasi"); CmbKesimpulan.addItem("Teratasi Sebagian");
        CmbKesimpulan.addItem("Belum Teratasi"); CmbKesimpulan.addItem("Lanjutkan");
        pKesimpulan.add(lKes); pKesimpulan.add(CmbKesimpulan);

        JPanel pCenter = new JPanel(new BorderLayout(2, 2));
        pCenter.add(pIndikatorWrap, BorderLayout.NORTH);
        pCenter.add(pSoap, BorderLayout.CENTER);
        pCenter.add(pKesimpulan, BorderLayout.SOUTH);

        // Riwayat evaluasi list
        tabEvaluasiList = new DefaultTableModel(
                new Object[][]{}, new String[]{"No", "Tanggal", "Shift", "Kesimpulan", "Perawat", "id"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblEvaluasiList = new widget.Table(); tblEvaluasiList.setModel(tabEvaluasiList);
        tblEvaluasiList.getColumnModel().getColumn(0).setPreferredWidth(30);
        tblEvaluasiList.getColumnModel().getColumn(1).setPreferredWidth(90);
        tblEvaluasiList.getColumnModel().getColumn(2).setPreferredWidth(70);
        tblEvaluasiList.getColumnModel().getColumn(3).setPreferredWidth(140);
        tblEvaluasiList.getColumnModel().getColumn(4).setPreferredWidth(160);
        tblEvaluasiList.removeColumn(tblEvaluasiList.getColumnModel().getColumn(5));
        JPanel pEvalList = new JPanel(new BorderLayout());
        pEvalList.setBorder(BorderFactory.createTitledBorder("Riwayat Evaluasi (klik baris untuk muat)"));
        pEvalList.setPreferredSize(new Dimension(900, 130));
        pEvalList.add(new JScrollPane(tblEvaluasiList), BorderLayout.CENTER);

        panelisi pBotEval = new panelisi();
        pBotEval.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));
        pBotEval.setPreferredSize(new Dimension(900, 44));
        pBotEval.add(BtnSimpanEvaluasi);
        pBotEval.add(BtnHapusEvaluasi);

        JPanel pSouth = new JPanel(new BorderLayout(0, 2));
        pSouth.add(pEvalList, BorderLayout.CENTER);
        pSouth.add(pBotEval, BorderLayout.SOUTH);

        panel.add(pTop, BorderLayout.NORTH);
        panel.add(pCenter, BorderLayout.CENTER);
        panel.add(pSouth, BorderLayout.SOUTH);
        return panel;
    }

    // ─── isCek ────────────────────────────────────────────────────────────────
    public void isCek() {
        try {
            akses a = new akses();
            // basic access check - can be extended if needed
        } catch (Exception e) {
            System.err.println("isCek: " + e.getMessage());
        }
    }

    // ─── tampil ───────────────────────────────────────────────────────────────
    public void tampil() {
        try {
            tabList.setRowCount(0);
            String kata = TCari.getText().trim();
            String sql = "SELECT ah.id, COALESCE(p.nm_pasien, ah.no_rm) AS nm_pasien, ah.no_rawat, ah.tgl_askep, ah.no_rm "
                    + "FROM askep_header ah "
                    + "LEFT JOIN pasien p ON p.no_rkm_medis = ah.no_rm "
                    + (kata.isEmpty() ? "" : "WHERE p.nm_pasien LIKE ? OR ah.no_rawat LIKE ? ")
                    + "ORDER BY ah.tgl_askep DESC LIMIT 200";
            if (kata.isEmpty()) {
                ps = koneksi.prepareStatement(sql);
            } else {
                ps = koneksi.prepareStatement(sql);
                ps.setString(1, "%" + kata + "%");
                ps.setString(2, "%" + kata + "%");
            }
            rs = ps.executeQuery();
            int no = 1;
            while (rs.next()) {
                tabList.addRow(new Object[]{no++, rs.getString("nm_pasien"), rs.getString("tgl_askep"),
                        rs.getInt("id"), rs.getString("no_rawat"), rs.getString("no_rm")});
            }
            rs.close(); ps.close();
        } catch (Exception e) {
            System.err.println("tampil: " + e.getMessage());
        }
    }

    // ─── getData ──────────────────────────────────────────────────────────────
    private void getData() {
        int r = tblList.getSelectedRow();
        if (r < 0) return;
        try {
            // The hidden cols 3,4,5 exist in model but removed from view - use model index
            int modelRow = tblList.convertRowIndexToModel(r);
            // tabList has 6 model columns: No, Pasien, Tanggal, id, no_rawat, no_rm
            // But we only added 3 visible - let's use the full model
            // Re-query header by id
            // Since hidden columns were removed from view, we store id in a separate list approach
            // Instead, re-query using row data we have
            String noRawat = "";
            // use stored data in model - need to expose hidden cols
            // Workaround: query by row no
            Object idObj = tabList.getValueAt(modelRow, 3);
            if (idObj == null) return;
            currentHeaderId = Integer.parseInt(idObj.toString());

            ps = koneksi.prepareStatement(
                    "SELECT ah.*, COALESCE(p.nm_pasien,'') AS nm_pasien "
                    + "FROM askep_header ah LEFT JOIN pasien p ON p.no_rkm_medis = ah.no_rm "
                    + "WHERE ah.id = ?");
            ps.setInt(1, currentHeaderId);
            rs = ps.executeQuery();
            if (rs.next()) {
                TNoRw.setText(rs.getString("no_rawat"));
                TNoRM.setText(rs.getString("no_rm"));
                TPasien.setText(rs.getString("nm_pasien"));
                KdPetugas.setText(rs.getString("id_perawat"));
                NmPetugas.setText(rs.getString("nm_perawat"));
                try { java.sql.Timestamp ts = rs.getTimestamp("tgl_askep"); TglAskep.setDate(ts != null ? new java.util.Date(ts.getTime()) : new java.util.Date()); } catch (Exception ex) {}
            }
            rs.close(); ps.close();

            loadDiagnosisTerpilih();
            loadLuaranList();
            loadIntervensiList();
            loadEvaluasiList();
        } catch (Exception e) {
            System.err.println("getData: " + e.getMessage());
        }
    }

    // ─── simpanHeader ─────────────────────────────────────────────────────────
    private void simpanHeader() {
        if (TNoRw.getText().isEmpty() || KdPetugas.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No Rawat dan Petugas harus diisi!");
            return;
        }
        try {
            String today = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(TglAskep.getDate() != null ? TglAskep.getDate() : new java.util.Date());
            if (currentHeaderId < 0) {
                ps = koneksi.prepareStatement(
                        "INSERT INTO askep_header (no_rawat, no_rm, id_perawat, nm_perawat, tgl_askep) VALUES (?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, TNoRw.getText());
                ps.setString(2, TNoRM.getText());
                ps.setString(3, KdPetugas.getText());
                ps.setString(4, NmPetugas.getText());
                ps.setString(5, today);
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                if (rs.next()) currentHeaderId = rs.getInt(1);
                rs.close();
            } else {
                ps = koneksi.prepareStatement(
                        "UPDATE askep_header SET id_perawat=?, nm_perawat=?, tgl_askep=? WHERE id=?");
                ps.setString(1, KdPetugas.getText());
                ps.setString(2, NmPetugas.getText());
                ps.setString(3, today);
                ps.setInt(4, currentHeaderId);
                ps.executeUpdate();
            }
            ps.close();
            JOptionPane.showMessageDialog(this, "Header berhasil disimpan.");
            tampil();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal simpan header: " + e.getMessage());
        }
    }

    // ─── hapusHeader ──────────────────────────────────────────────────────────
    private void hapusHeader() {
        if (currentHeaderId < 0) { JOptionPane.showMessageDialog(this, "Pilih data terlebih dahulu!"); return; }
        int ok = JOptionPane.showConfirmDialog(this, "Hapus askep ini beserta semua detail?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            // delete cascading - get diagnosis ids first
            List<Integer> diagIds = new ArrayList<>();
            ps = koneksi.prepareStatement("SELECT id FROM askep_diagnosis WHERE id_header = ?");
            ps.setInt(1, currentHeaderId); rs = ps.executeQuery();
            while (rs.next()) diagIds.add(rs.getInt(1));
            rs.close(); ps.close();

            for (int dId : diagIds) {
                // luaran
                List<Integer> luaranIds = new ArrayList<>();
                ps = koneksi.prepareStatement("SELECT id FROM askep_luaran WHERE id_diagnosis = ?");
                ps.setInt(1, dId); rs = ps.executeQuery();
                while (rs.next()) luaranIds.add(rs.getInt(1));
                rs.close(); ps.close();
                for (int lId : luaranIds) {
                    ps = koneksi.prepareStatement("DELETE FROM askep_luaran_indikator WHERE id_luaran = ?");
                    ps.setInt(1, lId); ps.executeUpdate(); ps.close();
                }
                ps = koneksi.prepareStatement("DELETE FROM askep_luaran WHERE id_diagnosis = ?");
                ps.setInt(1, dId); ps.executeUpdate(); ps.close();
                // evaluasi
                List<Integer> evalIds = new ArrayList<>();
                ps = koneksi.prepareStatement("SELECT id FROM askep_evaluasi WHERE id_diagnosis = ?");
                ps.setInt(1, dId); rs = ps.executeQuery();
                while (rs.next()) evalIds.add(rs.getInt(1));
                rs.close(); ps.close();
                for (int eId : evalIds) {
                    ps = koneksi.prepareStatement("DELETE FROM askep_evaluasi_indikator WHERE id_evaluasi = ?");
                    ps.setInt(1, eId); ps.executeUpdate(); ps.close();
                }
                ps = koneksi.prepareStatement("DELETE FROM askep_evaluasi WHERE id_diagnosis = ?");
                ps.setInt(1, dId); ps.executeUpdate(); ps.close();
                // intervensi
                ps = koneksi.prepareStatement("DELETE FROM askep_intervensi WHERE id_diagnosis = ?");
                ps.setInt(1, dId); ps.executeUpdate(); ps.close();
            }
            ps = koneksi.prepareStatement("DELETE FROM askep_diagnosis WHERE id_header = ?");
            ps.setInt(1, currentHeaderId); ps.executeUpdate(); ps.close();
            ps = koneksi.prepareStatement("DELETE FROM askep_header WHERE id = ?");
            ps.setInt(1, currentHeaderId); ps.executeUpdate(); ps.close();

            currentHeaderId = -1;
            TNoRw.setText(""); TNoRM.setText(""); TPasien.setText(""); KdPetugas.setText(""); NmPetugas.setText("");
            tabDiagnosisTerpilih.setRowCount(0);
            JOptionPane.showMessageDialog(this, "Data berhasil dihapus.");
            tampil();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal hapus: " + e.getMessage());
        }
    }

    // ─── bersihForm ──────────────────────────────────────────────────────────
    private void bersihForm() {
        currentHeaderId = -1; currentLuaranId = -1; currentIntervensiId = -1; currentEvaluasiId = -1;
        TNoRw.setText(""); TNoRM.setText(""); TPasien.setText("");
        KdPetugas.setText(""); NmPetugas.setText("");
        TglAskep.setDate(new java.util.Date());
        tabDiagnosisTerpilih.setRowCount(0);
        tabLuaranList.setRowCount(0);
        tabIntervensiList.setRowCount(0);
        tabProgressIndikator.setRowCount(0);
        tabEvaluasiList.setRowCount(0);
        TAreaS.setText(""); TAreaO.setText(""); TAreaA.setText(""); TAreaP.setText("");
        CmbDiagnosisSlki.removeAllItems(); CmbDiagnosisSiki.removeAllItems(); CmbDiagnosisEval.removeAllItems();
    }

    // ─── loadKategoriSdki ────────────────────────────────────────────────────
    private void loadKategoriSdki() {
        try {
            CmbKategori.removeAllItems();
            CmbKategori.addItem("Semua");
            ps = koneksi.prepareStatement("SELECT id, nama FROM mst_sdki_kategori ORDER BY nama");
            rs = ps.executeQuery();
            while (rs.next()) {
                CmbKategori.addItem(new KategoriItem(rs.getInt("id"), rs.getString("nama")));
            }
            rs.close(); ps.close();
        } catch (Exception e) {
            System.err.println("loadKategoriSdki: " + e.getMessage());
        }
    }

    // ─── loadSdki ─────────────────────────────────────────────────────────────
    private void loadSdki() {
        try {
            tabSdki.setRowCount(0);
            String kata = TSearchSdki.getText().trim();
            Object kat = CmbKategori.getSelectedItem();
            boolean filterKat = (kat instanceof KategoriItem);

            StringBuilder sb = new StringBuilder(
                    "SELECT s.id, s.kode, s.label, s.jenis FROM mst_sdki s WHERE 1=1 ");
            if (!kata.isEmpty()) sb.append("AND (s.label LIKE ? OR s.kode LIKE ?) ");
            if (filterKat) sb.append("AND s.id_kategori = ? ");
            sb.append("ORDER BY s.kode LIMIT 200");

            ps = koneksi.prepareStatement(sb.toString());
            int idx = 1;
            if (!kata.isEmpty()) {
                ps.setString(idx++, "%" + kata + "%");
                ps.setString(idx++, "%" + kata + "%");
            }
            if (filterKat) ps.setInt(idx, ((KategoriItem) kat).id);
            rs = ps.executeQuery();
            while (rs.next()) {
                tabSdki.addRow(new Object[]{rs.getString("kode"), rs.getString("label"), rs.getString("jenis"), rs.getInt("id")});
            }
            rs.close(); ps.close();
        } catch (Exception e) {
            System.err.println("loadSdki: " + e.getMessage());
        }
    }

    // ─── loadDetail ───────────────────────────────────────────────────────────
    private void loadDetail(int idSdki) {
        try {
            tabMayor.setRowCount(0);
            tabMinor.setRowCount(0);
            CmbEtiologi.removeAllItems();

            ps = koneksi.prepareStatement("SELECT kode, label, definisi FROM mst_sdki WHERE id = ?");
            ps.setInt(1, idSdki); rs = ps.executeQuery();
            if (rs.next()) {
                LblNamaSdki.setText("<html><b>" + rs.getString("kode") + " - " + rs.getString("label") + "</b></html>");
                String def = rs.getString("definisi");
                LblDefinisiSdki.setText("<html>" + (def != null ? def : "-") + "</html>");
            }
            rs.close(); ps.close();

            ps = koneksi.prepareStatement(
                    "SELECT kelompok, deskripsi, tipe FROM mst_sdki_tanda_gejala WHERE id_sdki = ? ORDER BY tipe, urutan");
            ps.setInt(1, idSdki); rs = ps.executeQuery();
            while (rs.next()) {
                String tipe = rs.getString("tipe");
                Object[] row = {Boolean.FALSE, rs.getString("kelompok"), rs.getString("deskripsi")};
                if ("mayor".equalsIgnoreCase(tipe)) tabMayor.addRow(row);
                else tabMinor.addRow(row);
            }
            rs.close(); ps.close();

            ps = koneksi.prepareStatement("SELECT deskripsi FROM mst_sdki_etiologi WHERE id_sdki = ? ORDER BY id");
            ps.setInt(1, idSdki); rs = ps.executeQuery();
            while (rs.next()) CmbEtiologi.addItem(rs.getString("deskripsi"));
            rs.close(); ps.close();
        } catch (Exception e) {
            System.err.println("loadDetail: " + e.getMessage());
        }
    }

    // ─── tambahDiagnosis ─────────────────────────────────────────────────────
    private void tambahDiagnosis() {
        if (selectedSdkiId < 0) { JOptionPane.showMessageDialog(this, "Pilih diagnosis SDKI terlebih dahulu!"); return; }
        try {
            ps = koneksi.prepareStatement("SELECT kode, label, jenis FROM mst_sdki WHERE id = ?");
            ps.setInt(1, selectedSdkiId); rs = ps.executeQuery();
            if (!rs.next()) { rs.close(); ps.close(); return; }
            String kode = rs.getString("kode");
            String nama = rs.getString("label");
            String jenis = rs.getString("jenis");
            rs.close(); ps.close();

            String etiologi = CmbEtiologi.getSelectedItem() != null ? CmbEtiologi.getSelectedItem().toString() : "";

            // Check duplicate in table
            for (int i = 0; i < tabDiagnosisTerpilih.getRowCount(); i++) {
                Object existId = tabDiagnosisTerpilih.getValueAt(i, 6);
                if (existId != null && Integer.parseInt(existId.toString()) == selectedSdkiId) {
                    JOptionPane.showMessageDialog(this, "Diagnosis ini sudah ada di daftar.");
                    return;
                }
            }

            int no = tabDiagnosisTerpilih.getRowCount() + 1;
            tabDiagnosisTerpilih.addRow(new Object[]{no, kode, nama, jenis, etiologi, "Aktif", selectedSdkiId, -1});
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal tambah diagnosis: " + e.getMessage());
        }
    }

    // ─── hapusDiagnosisTerpilih ───────────────────────────────────────────────
    private void hapusDiagnosisTerpilih() {
        int r = tblDiagnosisTerpilih.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Pilih baris yang ingin dihapus!"); return; }
        int modelRow = tblDiagnosisTerpilih.convertRowIndexToModel(r);
        Object idDiagObj = tabDiagnosisTerpilih.getValueAt(modelRow, 7);
        if (idDiagObj != null) {
            int idDiag = Integer.parseInt(idDiagObj.toString());
            if (idDiag > 0) {
                int ok = JOptionPane.showConfirmDialog(this, "Hapus diagnosa ini dari database?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    try {
                        ps = koneksi.prepareStatement("DELETE FROM askep_diagnosis WHERE id = ?");
                        ps.setInt(1, idDiag); ps.executeUpdate(); ps.close();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Gagal hapus dari DB: " + e.getMessage());
                        return;
                    }
                }
            }
        }
        tabDiagnosisTerpilih.removeRow(modelRow);
        renumberDiagnosisTerpilih();
    }

    private void renumberDiagnosisTerpilih() {
        for (int i = 0; i < tabDiagnosisTerpilih.getRowCount(); i++) {
            tabDiagnosisTerpilih.setValueAt(i + 1, i, 0);
        }
    }

    // ─── simpanDiagnosis ─────────────────────────────────────────────────────
    private void simpanDiagnosis() {
        // Jika tabel kosong tapi ada diagnosis yang dipilih, otomatis tambahkan
        if (tabDiagnosisTerpilih.getRowCount() == 0 && selectedSdkiId >= 0) tambahDiagnosis();
        if (tabDiagnosisTerpilih.getRowCount() == 0) { JOptionPane.showMessageDialog(this, "Pilih diagnosis SDKI terlebih dahulu!"); return; }
        if (TNoRw.getText().isEmpty()) { JOptionPane.showMessageDialog(this, "Header belum diisi / disimpan!"); return; }
        try {
            // Ensure header exists
            if (currentHeaderId < 0) simpanHeader();
            if (currentHeaderId < 0) return;

            String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
            int saved = 0;
            for (int i = 0; i < tabDiagnosisTerpilih.getRowCount(); i++) {
                Object idDiagObj = tabDiagnosisTerpilih.getValueAt(i, 7);
                int idDiag = idDiagObj != null ? Integer.parseInt(idDiagObj.toString()) : -1;
                int idSdki = Integer.parseInt(tabDiagnosisTerpilih.getValueAt(i, 6).toString());
                String kode = tabDiagnosisTerpilih.getValueAt(i, 1).toString();
                String nama = tabDiagnosisTerpilih.getValueAt(i, 2).toString();
                String etiologi = tabDiagnosisTerpilih.getValueAt(i, 4).toString();

                // Collect tanda gejala mayor checked
                StringBuilder mayor = new StringBuilder();
                for (int mr = 0; mr < tabMayor.getRowCount(); mr++) {
                    if (Boolean.TRUE.equals(tabMayor.getValueAt(mr, 0))) {
                        if (mayor.length() > 0) mayor.append("; ");
                        mayor.append(tabMayor.getValueAt(mr, 2));
                    }
                }
                StringBuilder minor = new StringBuilder();
                for (int mr = 0; mr < tabMinor.getRowCount(); mr++) {
                    if (Boolean.TRUE.equals(tabMinor.getValueAt(mr, 0))) {
                        if (minor.length() > 0) minor.append("; ");
                        minor.append(tabMinor.getValueAt(mr, 2));
                    }
                }

                if (idDiag <= 0) {
                    ps = koneksi.prepareStatement(
                            "INSERT INTO askep_diagnosis (id_header, id_sdki, kode_sdki, label_sdki, etiologi, tanda_mayor, tanda_minor, tgl_diagnosa, urutan, status) VALUES (?,?,?,?,?,?,?,?,?,?)",
                            Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, currentHeaderId);
                    ps.setInt(2, idSdki);
                    ps.setString(3, kode);
                    ps.setString(4, nama);
                    ps.setString(5, etiologi);
                    ps.setString(6, mayor.toString());
                    ps.setString(7, minor.toString());
                    ps.setString(8, today);
                    ps.setInt(9, i + 1);
                    ps.setString(10, "Aktif");
                    ps.executeUpdate();
                    ResultSet gk = ps.getGeneratedKeys();
                    if (gk.next()) tabDiagnosisTerpilih.setValueAt(gk.getInt(1), i, 7);
                    gk.close();
                    ps.close();
                } else {
                    ps = koneksi.prepareStatement(
                            "UPDATE askep_diagnosis SET etiologi=?, tanda_mayor=?, tanda_minor=?, urutan=? WHERE id=?");
                    ps.setString(1, etiologi);
                    ps.setString(2, mayor.toString());
                    ps.setString(3, minor.toString());
                    ps.setInt(4, i + 1);
                    ps.setInt(5, idDiag);
                    ps.executeUpdate();
                    ps.close();
                }
                saved++;
            }
            JOptionPane.showMessageDialog(this, saved + " diagnosa berhasil disimpan.");
            loadDiagnosisTerpilih();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal simpan diagnosa: " + e.getMessage());
        }
    }

    // ─── loadDiagnosisTerpilih ───────────────────────────────────────────────
    private void loadDiagnosisTerpilih() {
        try {
            tabDiagnosisTerpilih.setRowCount(0);
            CmbDiagnosisSlki.removeAllItems();
            CmbDiagnosisSiki.removeAllItems();
            CmbDiagnosisEval.removeAllItems();

            if (currentHeaderId < 0) return;
            ps = koneksi.prepareStatement(
                    "SELECT id, id_sdki, kode_sdki, label_sdki, etiologi, status FROM askep_diagnosis WHERE id_header = ? ORDER BY urutan");
            ps.setInt(1, currentHeaderId); rs = ps.executeQuery();
            int no = 1;
            while (rs.next()) {
                int idDiag = rs.getInt("id");
                int idSdki = rs.getInt("id_sdki");
                String kode = rs.getString("kode_sdki");
                String nama = rs.getString("label_sdki");
                String etiologi = rs.getString("etiologi");
                String status = rs.getString("status");
                tabDiagnosisTerpilih.addRow(new Object[]{no++, kode, nama, "", etiologi, status, idSdki, idDiag});

                DiagnosisItem di = new DiagnosisItem(idDiag, idSdki, kode + " - " + nama);
                CmbDiagnosisSlki.addItem(di);
                CmbDiagnosisSiki.addItem(di);
                CmbDiagnosisEval.addItem(di);
            }
            rs.close(); ps.close();
        } catch (Exception e) {
            System.err.println("loadDiagnosisTerpilih: " + e.getMessage());
        }
    }

    // ─── loadLuaranSuggestions ────────────────────────────────────────────────
    private void loadLuaranSuggestions(int idSdki) {
        try {
            CmbLuaran.removeAllItems();
            ps = koneksi.prepareStatement(
                    "SELECT sl.id, sl.kode, sl.label FROM map_sdki_slki m "
                    + "JOIN mst_slki sl ON sl.id = m.id_slki WHERE m.id_sdki = ? ORDER BY sl.label");
            ps.setInt(1, idSdki); rs = ps.executeQuery();
            while (rs.next()) {
                CmbLuaran.addItem(new SlkiItem(rs.getInt("id"), rs.getString("kode"), rs.getString("label")));
            }
            rs.close(); ps.close();
        } catch (Exception e) {
            System.err.println("loadLuaranSuggestions: " + e.getMessage());
        }
    }

    // ─── loadIndikator ────────────────────────────────────────────────────────
    private void loadIndikator(int idSlki) {
        try {
            tabIndikator.setRowCount(0);
            ps = koneksi.prepareStatement(
                    "SELECT id, label, tipe_skala FROM mst_slki_indikator WHERE id_slki = ? ORDER BY urutan, id");
            ps.setInt(1, idSlki); rs = ps.executeQuery();
            while (rs.next()) {
                tabIndikator.addRow(new Object[]{rs.getString("label"), 1, 5, rs.getString("tipe_skala")});
            }
            rs.close(); ps.close();
        } catch (Exception e) {
            System.err.println("loadIndikator: " + e.getMessage());
        }
    }

    // ─── simpanLuaran ─────────────────────────────────────────────────────────
    private void simpanLuaran() {
        Object diagSel = CmbDiagnosisSlki.getSelectedItem();
        Object luaranSel = CmbLuaran.getSelectedItem();
        if (!(diagSel instanceof DiagnosisItem) || !(luaranSel instanceof SlkiItem)) {
            JOptionPane.showMessageDialog(this, "Pilih diagnosa dan luaran terlebih dahulu!"); return;
        }
        try {
            DiagnosisItem di = (DiagnosisItem) diagSel;
            SlkiItem si = (SlkiItem) luaranSel;
            int waktu = (Integer) SpnWaktu.getValue();

            int skalaAwal = 1, skalaTarget = 5;
            if (tabIndikator.getRowCount() > 0) {
                Object a = tabIndikator.getValueAt(0, 1);
                Object t = tabIndikator.getValueAt(0, 2);
                if (a != null) try { skalaAwal = Integer.parseInt(a.toString()); } catch (Exception ex) {}
                if (t != null) try { skalaTarget = Integer.parseInt(t.toString()); } catch (Exception ex) {}
            }

            int idLuaran;
            if (currentLuaranId > 0) {
                // UPDATE existing
                ps = koneksi.prepareStatement(
                        "UPDATE askep_luaran SET id_slki=?, label_slki=?, skala_awal=?, skala_target=?, waktu_target_hari=? WHERE id=?");
                ps.setInt(1, si.id); ps.setString(2, si.nama);
                ps.setInt(3, skalaAwal); ps.setInt(4, skalaTarget);
                ps.setInt(5, waktu); ps.setInt(6, currentLuaranId);
                ps.executeUpdate(); ps.close();
                // refresh indikator
                ps = koneksi.prepareStatement("DELETE FROM askep_luaran_indikator WHERE id_luaran = ?");
                ps.setInt(1, currentLuaranId); ps.executeUpdate(); ps.close();
                idLuaran = currentLuaranId;
            } else {
                // INSERT new
                ps = koneksi.prepareStatement(
                        "INSERT INTO askep_luaran (id_diagnosis, id_slki, label_slki, skala_awal, skala_target, waktu_target_hari) VALUES (?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, di.idDiagnosis); ps.setInt(2, si.id); ps.setString(3, si.nama);
                ps.setInt(4, skalaAwal); ps.setInt(5, skalaTarget); ps.setInt(6, waktu);
                ps.executeUpdate();
                rs = ps.getGeneratedKeys(); idLuaran = -1;
                if (rs.next()) idLuaran = rs.getInt(1);
                rs.close(); ps.close();
            }

            // Save indikator rows
            if (idLuaran > 0) {
                for (int r = 0; r < tabIndikator.getRowCount(); r++) {
                    String lbl = tabIndikator.getValueAt(r, 0).toString();
                    Object aObj = tabIndikator.getValueAt(r, 1);
                    Object tObj = tabIndikator.getValueAt(r, 2);
                    int sa = aObj != null ? Integer.parseInt(aObj.toString()) : 1;
                    int st = tObj != null ? Integer.parseInt(tObj.toString()) : 5;
                    ps = koneksi.prepareStatement(
                            "INSERT INTO askep_luaran_indikator (id_luaran, id_indikator, label_indikator, skala_awal, skala_target) VALUES (?,?,?,?,?)");
                    ps.setInt(1, idLuaran); ps.setInt(2, 0); ps.setString(3, lbl);
                    ps.setInt(4, sa); ps.setInt(5, st);
                    ps.executeUpdate(); ps.close();
                }
            }

            currentLuaranId = -1;
            JOptionPane.showMessageDialog(this, "Luaran berhasil disimpan.");
            loadLuaranList();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal simpan luaran: " + e.getMessage());
        }
    }

    // ─── loadLuaranList ───────────────────────────────────────────────────────
    private void loadLuaranList() {
        try {
            tabLuaranList.setRowCount(0);
            if (currentHeaderId < 0) return;
            ps = koneksi.prepareStatement(
                    "SELECT al.id, ad.label_sdki, al.label_slki, al.skala_awal, al.skala_target, al.waktu_target_hari "
                    + "FROM askep_luaran al "
                    + "JOIN askep_diagnosis ad ON ad.id = al.id_diagnosis "
                    + "WHERE ad.id_header = ? ORDER BY al.id");
            ps.setInt(1, currentHeaderId); rs = ps.executeQuery();
            int no = 1;
            while (rs.next()) {
                tabLuaranList.addRow(new Object[]{no++, rs.getString("label_sdki"), rs.getString("label_slki"),
                        rs.getInt("skala_awal"), rs.getInt("skala_target"), rs.getInt("waktu_target_hari"), rs.getInt(1)});
            }
            rs.close(); ps.close();
        } catch (Exception e) {
            System.err.println("loadLuaranList: " + e.getMessage());
        }
    }

    // ─── loadIntervensiSuggestions ────────────────────────────────────────────
    private void loadIntervensiSuggestions(int idSdki) {
        try {
            tabIntervensi.setRowCount(0);
            ps = koneksi.prepareStatement(
                    "SELECT sk.id, sk.kode, sk.label, m.tipe FROM map_sdki_siki m "
                    + "JOIN mst_siki sk ON sk.id = m.id_siki WHERE m.id_sdki = ? ORDER BY m.tipe, sk.label");
            ps.setInt(1, idSdki); rs = ps.executeQuery();
            while (rs.next()) {
                tabIntervensi.addRow(new Object[]{Boolean.FALSE, rs.getString("kode"), rs.getString("label"), rs.getString("tipe"), rs.getInt("id")});
            }
            rs.close(); ps.close();
        } catch (Exception e) {
            System.err.println("loadIntervensiSuggestions: " + e.getMessage());
        }
    }

    // ─── loadTindakan ─────────────────────────────────────────────────────────
    private void loadTindakan(int idSiki) {
        PnlTindakan.removeAll();
        listTindakanChk.clear();
        try {
            ps = koneksi.prepareStatement(
                    "SELECT id, komponen, deskripsi FROM mst_siki_tindakan WHERE id_siki = ? ORDER BY komponen, urutan, id");
            ps.setInt(1, idSiki); rs = ps.executeQuery();
            String lastKomponen = "";
            JPanel pKomp = null;
            while (rs.next()) {
                String komp = rs.getString("komponen");
                String tindakan = rs.getString("deskripsi");
                if (!komp.equals(lastKomponen)) {
                    pKomp = new JPanel();
                    pKomp.setLayout(new BoxLayout(pKomp, BoxLayout.Y_AXIS));
                    pKomp.setBorder(BorderFactory.createTitledBorder(komp));
                    PnlTindakan.add(pKomp);
                    lastKomponen = komp;
                }
                JCheckBox chk = new JCheckBox("<html>" + tindakan + "</html>");
                chk.setFont(new Font("Tahoma", 0, 11));
                if (pKomp != null) pKomp.add(chk);
                listTindakanChk.add(chk);
            }
            rs.close(); ps.close();
            PnlTindakan.revalidate();
            PnlTindakan.repaint();
        } catch (Exception e) {
            System.err.println("loadTindakan: " + e.getMessage());
        }
    }

    // ─── simpanIntervensi ─────────────────────────────────────────────────────
    private void simpanIntervensi() {
        Object diagSel = CmbDiagnosisSiki.getSelectedItem();
        if (!(diagSel instanceof DiagnosisItem)) {
            JOptionPane.showMessageDialog(this, "Pilih diagnosa terlebih dahulu!"); return;
        }
        if (selectedSikiId < 0) {
            JOptionPane.showMessageDialog(this, "Pilih intervensi SIKI terlebih dahulu!"); return;
        }
        try {
            DiagnosisItem di = (DiagnosisItem) diagSel;
            String labelSiki = "";
            ps = koneksi.prepareStatement("SELECT label FROM mst_siki WHERE id = ?");
            ps.setInt(1, selectedSikiId); rs = ps.executeQuery();
            if (rs.next()) labelSiki = rs.getString("label");
            rs.close(); ps.close();

            StringBuilder tindakanDipilih = new StringBuilder();
            for (JCheckBox chk : listTindakanChk) {
                if (chk.isSelected()) {
                    if (tindakanDipilih.length() > 0) tindakanDipilih.append(" | ");
                    tindakanDipilih.append(chk.getText().replaceAll("<[^>]+>", ""));
                }
            }

            if (currentIntervensiId > 0) {
                ps = koneksi.prepareStatement(
                        "UPDATE askep_intervensi SET id_siki=?, label_siki=?, tindakan_dipilih=? WHERE id=?");
                ps.setInt(1, selectedSikiId); ps.setString(2, labelSiki);
                ps.setString(3, tindakanDipilih.toString()); ps.setInt(4, currentIntervensiId);
                ps.executeUpdate(); ps.close();
            } else {
                ps = koneksi.prepareStatement(
                        "INSERT INTO askep_intervensi (id_diagnosis, id_siki, label_siki, tindakan_dipilih) VALUES (?,?,?,?)");
                ps.setInt(1, di.idDiagnosis); ps.setInt(2, selectedSikiId);
                ps.setString(3, labelSiki); ps.setString(4, tindakanDipilih.toString());
                ps.executeUpdate(); ps.close();
            }

            currentIntervensiId = -1;
            JOptionPane.showMessageDialog(this, "Intervensi berhasil disimpan.");
            loadIntervensiList();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal simpan intervensi: " + e.getMessage());
        }
    }

    // ─── loadIntervensiList ───────────────────────────────────────────────────
    private void loadIntervensiList() {
        try {
            tabIntervensiList.setRowCount(0);
            if (currentHeaderId < 0) return;
            ps = koneksi.prepareStatement(
                    "SELECT ai.id, ad.label_sdki, ai.label_siki, ai.tindakan_dipilih "
                    + "FROM askep_intervensi ai "
                    + "JOIN askep_diagnosis ad ON ad.id = ai.id_diagnosis "
                    + "WHERE ad.id_header = ? ORDER BY ai.id");
            ps.setInt(1, currentHeaderId); rs = ps.executeQuery();
            int no = 1;
            while (rs.next()) {
                tabIntervensiList.addRow(new Object[]{no++, rs.getString("label_sdki"),
                        rs.getString("label_siki"), rs.getString("tindakan_dipilih"), rs.getInt(1)});
            }
            rs.close(); ps.close();
        } catch (Exception e) {
            System.err.println("loadIntervensiList: " + e.getMessage());
        }
    }

    // ─── loadProgressIndikator ───────────────────────────────────────────────
    private void loadProgressIndikator(int idDiagnosis) {
        try {
            tabProgressIndikator.setRowCount(0);
            ps = koneksi.prepareStatement(
                    "SELECT ali.label_indikator, ali.skala_awal, ali.skala_target "
                    + "FROM askep_luaran_indikator ali "
                    + "JOIN askep_luaran al ON al.id = ali.id_luaran "
                    + "WHERE al.id_diagnosis = ? ORDER BY ali.id");
            ps.setInt(1, idDiagnosis); rs = ps.executeQuery();
            while (rs.next()) {
                tabProgressIndikator.addRow(new Object[]{
                        rs.getString("label_indikator"),
                        rs.getInt("skala_awal"),
                        rs.getInt("skala_target"),
                        3
                });
            }
            rs.close(); ps.close();
        } catch (Exception e) {
            System.err.println("loadProgressIndikator: " + e.getMessage());
        }
    }

    // ─── simpanEvaluasi ───────────────────────────────────────────────────────
    private void simpanEvaluasi() {
        Object diagSel = CmbDiagnosisEval.getSelectedItem();
        if (!(diagSel instanceof DiagnosisItem)) {
            JOptionPane.showMessageDialog(this, "Pilih diagnosa terlebih dahulu!"); return;
        }
        try {
            DiagnosisItem di = (DiagnosisItem) diagSel;
            String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
            String shift = CmbShift.getSelectedItem() != null ? CmbShift.getSelectedItem().toString() : "Pagi";
            String kesimpulan = CmbKesimpulan.getSelectedItem() != null ? CmbKesimpulan.getSelectedItem().toString() : "";

            ps = koneksi.prepareStatement(
                    "INSERT INTO askep_evaluasi (id_diagnosis, tgl_evaluasi, shift, soap_s, soap_o, soap_a, soap_p, kesimpulan, id_perawat, nm_perawat) VALUES (?,?,?,?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, di.idDiagnosis);
            ps.setString(2, today);
            ps.setString(3, shift);
            ps.setString(4, TAreaS.getText());
            ps.setString(5, TAreaO.getText());
            ps.setString(6, TAreaA.getText());
            ps.setString(7, TAreaP.getText());
            ps.setString(8, kesimpulan);
            ps.setString(9, KdPetugas.getText());
            ps.setString(10, NmPetugas.getText());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            int idEval = -1;
            if (rs.next()) idEval = rs.getInt(1);
            rs.close(); ps.close();

            // Save indikator progress
            if (idEval > 0) {
                for (int r = 0; r < tabProgressIndikator.getRowCount(); r++) {
                    String lbl = tabProgressIndikator.getValueAt(r, 0).toString();
                    Object aObj = tabProgressIndikator.getValueAt(r, 1);
                    Object tObj = tabProgressIndikator.getValueAt(r, 2);
                    Object sObj = tabProgressIndikator.getValueAt(r, 3);
                    int sa = aObj != null ? Integer.parseInt(aObj.toString()) : 1;
                    int st = tObj != null ? Integer.parseInt(tObj.toString()) : 5;
                    int ss = sObj != null ? Integer.parseInt(sObj.toString()) : 3;
                    ps = koneksi.prepareStatement(
                            "INSERT INTO askep_evaluasi_indikator (id_evaluasi, label_indikator, skala_awal, skala_target, skala_saat_ini) VALUES (?,?,?,?,?)");
                    ps.setInt(1, idEval);
                    ps.setString(2, lbl);
                    ps.setInt(3, sa);
                    ps.setInt(4, st);
                    ps.setInt(5, ss);
                    ps.executeUpdate(); ps.close();
                }
            }

            // Update status diagnosis if resolved
            if (kesimpulan.equals("Teratasi")) {
                ps = koneksi.prepareStatement("UPDATE askep_diagnosis SET status='Teratasi' WHERE id = ?");
                ps.setInt(1, di.idDiagnosis); ps.executeUpdate(); ps.close();
            }

            JOptionPane.showMessageDialog(this, "Evaluasi berhasil disimpan.");
            TAreaS.setText(""); TAreaO.setText(""); TAreaA.setText(""); TAreaP.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal simpan evaluasi: " + e.getMessage());
        }
    }

    // ─── loadDetailDariTerpilih (Tab1 – klik baris diagnosa terpilih) ──────────
    private void loadDetailDariTerpilih() {
        int r = tblDiagnosisTerpilih.getSelectedRow();
        if (r < 0) return;
        int modelRow = tblDiagnosisTerpilih.convertRowIndexToModel(r);
        Object idSdkiObj = tabDiagnosisTerpilih.getValueAt(modelRow, 6);
        if (idSdkiObj == null) return;
        selectedSdkiId = Integer.parseInt(idSdkiObj.toString());
        loadDetail(selectedSdkiId);
        // Pre-check tanda gejala dari data tersimpan
        try {
            Object idDiagObj = tabDiagnosisTerpilih.getValueAt(modelRow, 7);
            if (idDiagObj == null || Integer.parseInt(idDiagObj.toString()) <= 0) return;
            int idDiag = Integer.parseInt(idDiagObj.toString());
            ps = koneksi.prepareStatement("SELECT tanda_mayor, tanda_minor FROM askep_diagnosis WHERE id = ?");
            ps.setInt(1, idDiag); rs = ps.executeQuery();
            if (rs.next()) {
                String savedMayor = rs.getString("tanda_mayor") != null ? rs.getString("tanda_mayor") : "";
                String savedMinor = rs.getString("tanda_minor") != null ? rs.getString("tanda_minor") : "";
                for (int i = 0; i < tabMayor.getRowCount(); i++) {
                    Object desc = tabMayor.getValueAt(i, 2);
                    tabMayor.setValueAt(desc != null && savedMayor.contains(desc.toString()), i, 0);
                }
                for (int i = 0; i < tabMinor.getRowCount(); i++) {
                    Object desc = tabMinor.getValueAt(i, 2);
                    tabMinor.setValueAt(desc != null && savedMinor.contains(desc.toString()), i, 0);
                }
                // Pre-select etiologi
                Object savedEtiologi = tabDiagnosisTerpilih.getValueAt(modelRow, 4);
                if (savedEtiologi != null) {
                    for (int i = 0; i < CmbEtiologi.getItemCount(); i++) {
                        if (CmbEtiologi.getItemAt(i).toString().equals(savedEtiologi.toString())) {
                            CmbEtiologi.setSelectedIndex(i); break;
                        }
                    }
                }
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.err.println("loadDetailDariTerpilih: " + e.getMessage()); }
    }

    // ─── loadLuaranDariList (Tab2 – klik baris luaran tersimpan) ─────────────
    private void loadLuaranDariList() {
        int r = tblLuaranList.getSelectedRow();
        if (r < 0) return;
        int modelRow = tblLuaranList.convertRowIndexToModel(r);
        Object idObj = tabLuaranList.getValueAt(modelRow, 6);
        if (idObj == null) return;
        currentLuaranId = Integer.parseInt(idObj.toString());
        try {
            ps = koneksi.prepareStatement(
                    "SELECT al.id_slki, al.skala_awal, al.skala_target, al.waktu_target_hari, "
                    + "ali.label_indikator, ali.skala_awal AS sa, ali.skala_target AS st "
                    + "FROM askep_luaran al "
                    + "LEFT JOIN askep_luaran_indikator ali ON ali.id_luaran = al.id "
                    + "WHERE al.id = ? ORDER BY ali.id");
            ps.setInt(1, currentLuaranId); rs = ps.executeQuery();
            tabIndikator.setRowCount(0);
            int idSlki = -1; int waktu = 3;
            while (rs.next()) {
                if (idSlki < 0) {
                    idSlki = rs.getInt("id_slki");
                    waktu = rs.getInt("waktu_target_hari");
                }
                String lbl = rs.getString("label_indikator");
                if (lbl != null) tabIndikator.addRow(new Object[]{lbl, rs.getInt("sa"), rs.getInt("st"), ""});
            }
            rs.close(); ps.close();
            SpnWaktu.setValue(waktu);
            // select luaran in combo
            if (idSlki > 0) {
                for (int i = 0; i < CmbLuaran.getItemCount(); i++) {
                    Object item = CmbLuaran.getItemAt(i);
                    if (item instanceof SlkiItem && ((SlkiItem) item).id == idSlki) {
                        CmbLuaran.setSelectedIndex(i); break;
                    }
                }
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Gagal muat luaran: " + e.getMessage()); }
    }

    // ─── hapusLuaran ─────────────────────────────────────────────────────────
    private void hapusLuaran() {
        int r = tblLuaranList.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Pilih baris luaran yang ingin dihapus!"); return; }
        int ok = JOptionPane.showConfirmDialog(this, "Hapus luaran ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        int modelRow = tblLuaranList.convertRowIndexToModel(r);
        Object idObj = tabLuaranList.getValueAt(modelRow, 6);
        if (idObj == null) return;
        int id = Integer.parseInt(idObj.toString());
        try {
            ps = koneksi.prepareStatement("DELETE FROM askep_luaran_indikator WHERE id_luaran = ?");
            ps.setInt(1, id); ps.executeUpdate(); ps.close();
            ps = koneksi.prepareStatement("DELETE FROM askep_luaran WHERE id = ?");
            ps.setInt(1, id); ps.executeUpdate(); ps.close();
            currentLuaranId = -1;
            JOptionPane.showMessageDialog(this, "Luaran berhasil dihapus.");
            loadLuaranList();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Gagal hapus luaran: " + e.getMessage()); }
    }

    // ─── loadIntervensiDariList (Tab3 – klik baris intervensi tersimpan) ─────
    private void loadIntervensiDariList() {
        int r = tblIntervensiList.getSelectedRow();
        if (r < 0) return;
        int modelRow = tblIntervensiList.convertRowIndexToModel(r);
        Object idObj = tabIntervensiList.getValueAt(modelRow, 4);
        if (idObj == null) return;
        currentIntervensiId = Integer.parseInt(idObj.toString());
        try {
            ps = koneksi.prepareStatement("SELECT id_siki, tindakan_dipilih FROM askep_intervensi WHERE id = ?");
            ps.setInt(1, currentIntervensiId); rs = ps.executeQuery();
            if (rs.next()) {
                int idSiki = rs.getInt("id_siki");
                String saved = rs.getString("tindakan_dipilih");
                rs.close(); ps.close();
                selectedSikiId = idSiki;
                loadTindakan(idSiki);
                // pre-check saved tindakan
                if (saved != null) {
                    for (JCheckBox chk : listTindakanChk) {
                        String t = chk.getText().replaceAll("<[^>]+>", "").trim();
                        chk.setSelected(saved.contains(t));
                    }
                }
                // select intervensi in table
                for (int i = 0; i < tabIntervensi.getRowCount(); i++) {
                    Object idObj2 = tabIntervensi.getValueAt(i, 4);
                    if (idObj2 != null && Integer.parseInt(idObj2.toString()) == idSiki) {
                        tblIntervensi.setRowSelectionInterval(i, i); break;
                    }
                }
            } else { rs.close(); ps.close(); }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Gagal muat intervensi: " + e.getMessage()); }
    }

    // ─── hapusIntervensi ─────────────────────────────────────────────────────
    private void hapusIntervensi() {
        int r = tblIntervensiList.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Pilih baris intervensi yang ingin dihapus!"); return; }
        int ok = JOptionPane.showConfirmDialog(this, "Hapus intervensi ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        int modelRow = tblIntervensiList.convertRowIndexToModel(r);
        Object idObj = tabIntervensiList.getValueAt(modelRow, 4);
        if (idObj == null) return;
        int id = Integer.parseInt(idObj.toString());
        try {
            ps = koneksi.prepareStatement("DELETE FROM askep_intervensi WHERE id = ?");
            ps.setInt(1, id); ps.executeUpdate(); ps.close();
            currentIntervensiId = -1;
            JOptionPane.showMessageDialog(this, "Intervensi berhasil dihapus.");
            loadIntervensiList();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Gagal hapus intervensi: " + e.getMessage()); }
    }

    // ─── loadEvaluasiList ────────────────────────────────────────────────────
    private void loadEvaluasiList() {
        try {
            tabEvaluasiList.setRowCount(0);
            if (currentHeaderId < 0) return;
            ps = koneksi.prepareStatement(
                    "SELECT ae.id, ae.tgl_evaluasi, ae.shift, ae.kesimpulan, ae.nm_perawat "
                    + "FROM askep_evaluasi ae "
                    + "JOIN askep_diagnosis ad ON ad.id = ae.id_diagnosis "
                    + "WHERE ad.id_header = ? ORDER BY ae.tgl_evaluasi DESC, ae.id DESC");
            ps.setInt(1, currentHeaderId); rs = ps.executeQuery();
            int no = 1;
            while (rs.next()) {
                tabEvaluasiList.addRow(new Object[]{no++, rs.getString("tgl_evaluasi"),
                        rs.getString("shift"), rs.getString("kesimpulan"), rs.getString("nm_perawat"), rs.getInt(1)});
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.err.println("loadEvaluasiList: " + e.getMessage()); }
    }

    // ─── loadEvaluasiDariList (Tab4 – klik baris riwayat evaluasi) ───────────
    private void loadEvaluasiDariList() {
        int r = tblEvaluasiList.getSelectedRow();
        if (r < 0) return;
        int modelRow = tblEvaluasiList.convertRowIndexToModel(r);
        Object idObj = tabEvaluasiList.getValueAt(modelRow, 5);
        if (idObj == null) return;
        currentEvaluasiId = Integer.parseInt(idObj.toString());
        try {
            ps = koneksi.prepareStatement(
                    "SELECT ae.soap_s, ae.soap_o, ae.soap_a, ae.soap_p, ae.shift, ae.kesimpulan, "
                    + "aei.label_indikator, aei.skala_awal, aei.skala_target, aei.skala_saat_ini "
                    + "FROM askep_evaluasi ae "
                    + "LEFT JOIN askep_evaluasi_indikator aei ON aei.id_evaluasi = ae.id "
                    + "WHERE ae.id = ? ORDER BY aei.id");
            ps.setInt(1, currentEvaluasiId); rs = ps.executeQuery();
            tabProgressIndikator.setRowCount(0);
            String s = "", o = "", a = "", p = "", shift = "Pagi", kes = "";
            boolean first = true;
            while (rs.next()) {
                if (first) {
                    s = rs.getString("soap_s") != null ? rs.getString("soap_s") : "";
                    o = rs.getString("soap_o") != null ? rs.getString("soap_o") : "";
                    a = rs.getString("soap_a") != null ? rs.getString("soap_a") : "";
                    p = rs.getString("soap_p") != null ? rs.getString("soap_p") : "";
                    shift = rs.getString("shift") != null ? rs.getString("shift") : "Pagi";
                    kes = rs.getString("kesimpulan") != null ? rs.getString("kesimpulan") : "";
                    first = false;
                }
                String lbl = rs.getString("label_indikator");
                if (lbl != null) tabProgressIndikator.addRow(new Object[]{lbl,
                    rs.getInt("skala_awal"), rs.getInt("skala_target"), rs.getInt("skala_saat_ini")});
            }
            rs.close(); ps.close();
            TAreaS.setText(s); TAreaO.setText(o); TAreaA.setText(a); TAreaP.setText(p);
            for (int i = 0; i < CmbShift.getItemCount(); i++) {
                if (CmbShift.getItemAt(i).toString().equals(shift)) { CmbShift.setSelectedIndex(i); break; }
            }
            for (int i = 0; i < CmbKesimpulan.getItemCount(); i++) {
                if (CmbKesimpulan.getItemAt(i).toString().equals(kes)) { CmbKesimpulan.setSelectedIndex(i); break; }
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Gagal muat evaluasi: " + e.getMessage()); }
    }

    // ─── hapusEvaluasi ───────────────────────────────────────────────────────
    private void hapusEvaluasi() {
        int r = tblEvaluasiList.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Pilih baris evaluasi yang ingin dihapus!"); return; }
        int ok = JOptionPane.showConfirmDialog(this, "Hapus evaluasi ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        int modelRow = tblEvaluasiList.convertRowIndexToModel(r);
        Object idObj = tabEvaluasiList.getValueAt(modelRow, 5);
        if (idObj == null) return;
        int id = Integer.parseInt(idObj.toString());
        try {
            ps = koneksi.prepareStatement("DELETE FROM askep_evaluasi_indikator WHERE id_evaluasi = ?");
            ps.setInt(1, id); ps.executeUpdate(); ps.close();
            ps = koneksi.prepareStatement("DELETE FROM askep_evaluasi WHERE id = ?");
            ps.setInt(1, id); ps.executeUpdate(); ps.close();
            currentEvaluasiId = -1;
            TAreaS.setText(""); TAreaO.setText(""); TAreaA.setText(""); TAreaP.setText("");
            JOptionPane.showMessageDialog(this, "Evaluasi berhasil dihapus.");
            loadEvaluasiList();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Gagal hapus evaluasi: " + e.getMessage()); }
    }

    // ─── lihatAskep (cetak laporan jasper) ───────────────────────────────────
    public void lihatAskep() {
        if (currentHeaderId < 0) { JOptionPane.showMessageDialog(this, "Pilih data askep dari daftar kiri terlebih dahulu!"); return; }
        try {
            String rptDir = "report/";
            // Compile jrxml ke jasper jika belum ada
            kompileJrxml(rptDir + "rptAskep3SSLKI.jrxml",  rptDir + "rptAskep3SSLKI.jasper");
            kompileJrxml(rptDir + "rptAskep3SSIKI.jrxml",  rptDir + "rptAskep3SSIKI.jasper");
            kompileJrxml(rptDir + "rptAskep3SEval.jrxml",  rptDir + "rptAskep3SEval.jasper");
            kompileJrxml(rptDir + "rptAskep3S.jrxml",      rptDir + "rptAskep3S.jasper");

            Map<String, Object> param = new HashMap<>();
            param.put("HEADER_ID",    currentHeaderId);
            param.put("NM_PASIEN",    TPasien.getText());
            param.put("NO_RAWAT",     TNoRw.getText());
            param.put("NO_RM",        TNoRM.getText());
            param.put("NM_PERAWAT",   NmPetugas.getText());
            param.put("TGL_ASKEP",    new java.text.SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date()));
            param.put("namars",       akses.getnamars());
            param.put("alamatrs",     akses.getalamatrs());
            param.put("kotars",       akses.getkabupatenrs());
            param.put("logo",         Sequel.cariGambar("select setting.logo from setting"));
            param.put("SUBREPORT_DIR", new File(rptDir).getAbsolutePath() + File.separator);
            param.put("PETUGAS_QR",    KdPetugas.getText() + " - " + NmPetugas.getText());

            JasperPrint jp = JasperFillManager.fillReport(rptDir + "rptAskep3S.jasper", param, koneksi);
            JasperViewer viewer = new JasperViewer(jp, false);
            viewer.setTitle("Asuhan Keperawatan – " + TPasien.getText());
            java.awt.Dimension sc = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            viewer.setSize(sc.width - 60, sc.height - 60);
            viewer.setLocationRelativeTo(null);
            viewer.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal cetak askep: " + e.getMessage());
        }
    }

    private void kompileJrxml(String jrxml, String jasper) throws JRException {
        File fj = new File(jasper), fjx = new File(jrxml);
        if (!fj.exists() || fjx.lastModified() > fj.lastModified())
            JasperCompileManager.compileReportToFile(jrxml, jasper);
    }

    // ─── setNoRawat (dipanggil dari DlgKamarInap/DlgKasirRalan/DlgIGD) ────────
    public void setNoRawat(String noRawat, String noRM) {
        TNoRw.setText(noRawat);
        if (noRM != null && !noRM.isEmpty()) {
            TNoRM.setText(noRM);
            try {
                PreparedStatement p2 = koneksi.prepareStatement(
                        "SELECT nm_pasien FROM pasien WHERE no_rkm_medis = ? LIMIT 1");
                p2.setString(1, noRM);
                ResultSet r2 = p2.executeQuery();
                if (r2.next()) TPasien.setText(r2.getString("nm_pasien"));
                r2.close(); p2.close();
            } catch (Exception e) { System.err.println("setNoRawat: " + e.getMessage()); }
        } else if (!noRawat.isEmpty()) {
            try {
                PreparedStatement p2 = koneksi.prepareStatement(
                        "SELECT r.no_rkm_medis, p.nm_pasien FROM reg_periksa r "
                        + "LEFT JOIN pasien p ON p.no_rkm_medis = r.no_rkm_medis "
                        + "WHERE r.no_rawat = ? LIMIT 1");
                p2.setString(1, noRawat);
                ResultSet r2 = p2.executeQuery();
                if (r2.next()) {
                    TNoRM.setText(r2.getString("no_rkm_medis"));
                    TPasien.setText(r2.getString("nm_pasien"));
                }
                r2.close(); p2.close();
            } catch (Exception e) { System.err.println("setNoRawat: " + e.getMessage()); }
        }
        TCari.setText(noRawat);
        tampil();
    }

    // ─── Inner helper classes ─────────────────────────────────────────────────
    private static class KategoriItem {
        int id; String nama;
        KategoriItem(int id, String nama) { this.id = id; this.nama = nama; }
        @Override public String toString() { return nama; }
    }

    private static class DiagnosisItem {
        int idDiagnosis, idSdki; String label;
        DiagnosisItem(int idDiagnosis, int idSdki, String label) {
            this.idDiagnosis = idDiagnosis; this.idSdki = idSdki; this.label = label;
        }
        @Override public String toString() { return label; }
    }

    private static class SlkiItem {
        int id; String kode, nama;
        SlkiItem(int id, String kode, String nama) { this.id = id; this.kode = kode; this.nama = nama; }
        @Override public String toString() { return kode + " - " + nama; }
    }
}
