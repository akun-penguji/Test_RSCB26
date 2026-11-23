/*
 * Form Input Data Imunisasi Pasien — terstruktur untuk FHIR Immunization SatuSehat.
 * Menulis ke tabel `imunisasi_pasien` (lihat create_imunisasi_pasien_table.sql).
 *
 * Dibuka dari DlgRawatJalan / DlgRawatInap via tombol "Data Imunisasi Pasien"
 * setelah user pilih pasien (caller wajib panggil setNoRm() dulu).
 *
 * Layout: pure programmatic Swing dengan widget custom Khanza
 * (widget.Tanggal date picker, widget.TextBox, widget.ComboBox, dll).
 * Tidak pakai .form NetBeans agar tidak ada risiko rusak saat dibuka IDE.
 */
package simrskhanza;

import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import kepegawaian.DlgCariPetugas;

public final class DlgDataImunisasiPasien extends javax.swing.JDialog {

    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();

    // Context pasien
    private String noRawat = "", noRkm = "", namaPasien = "";

    // ---- Header ----
    private widget.InternalFrame internalFrame1;
    private widget.Label lblPasien;

    // ---- Input fields (Indonesia) ----
    private widget.Tanggal   DTPTgl;          // Tanggal Imunisasi
    private widget.ComboBox  cmbJam, cmbMnt, cmbDtk;   // Jam HH:MM:SS
    private widget.ComboBox  cmbVaksin;       // Vaksin (KFA-mapped)
    private widget.ComboBox  cmbLokasi;       // Lokasi Suntik
    private javax.swing.JSpinner spnDosis;    // Dosis ke-
    private javax.swing.JSpinner spnTotalDosis;  // Total Dosis Seri
    private widget.ComboBox  cmbNamaSeri;
    private widget.ComboBox  cmbICD10;       // ICD-10 + nama penyakit dicegah (editable)

    /** ICD-10 untuk vaksin yang beredar — diperbarui saat ada vaksin baru.
     *  Format item: "kode - nama" supaya user lihat keduanya saat pilih. */
    private static final String[] ICD10_VAKSIN = {
        "",
        "B05.9 - Campak (Measles)",
        "B06.9 - Rubella (Campak Jerman)",
        "B26.9 - Mumps (Parotitis Epidemika)",
        "A37.0 - Pertusis (Batuk Rejan)",
        "A36.9 - Difteri",
        "A35 - Tetanus",
        "B16.9 - Hepatitis B",
        "B15.9 - Hepatitis A",
        "A80.9 - Polio (Poliomyelitis)",
        "A19.9 - Tuberkulosis Milier (BCG)",
        "U07.1 - COVID-19",
        "J11.1 - Influenza",
        "A08.0 - Rotavirus Diarrhea",
        "G00.1 - Meningitis Pneumokokus",
        "A39.9 - Penyakit Meningokokus",
        "B97.7 - HPV (Human Papillomavirus)",
        "A01.0 - Demam Tifoid (Typhoid)",
        "B01.9 - Varisela (Cacar Air)",
        "B26.0 - Mumps Orkitis",
        "A27.9 - Leptospirosis",
        "A82.9 - Rabies",
        "A92.5 - Demam Zika",
        "A95.9 - Demam Kuning (Yellow Fever)",
        "A90 - Demam Dengue"
    };
    private widget.TextBox   txtNoBatch;
    private widget.Tanggal   DTPKadaluarsa;
    private widget.TextBox   txtManufaktur;
    private widget.ComboBox  cmbSumberDana;
    private widget.ComboBox  cmbWaktuImunisasi;
    private widget.TextArea  txtKeterangan;

    // ---- Petugas ----
    private widget.TextBox KdPeg, TPegawai;
    private widget.Button  BtnSeekPetugas;
    private DlgCariPetugas dlgPetugas;

    // ---- Buttons ----
    private widget.Button BtnSimpan, BtnBersihkan, BtnHapus, BtnTutup;

    // ---- Table ----
    private widget.Table tbList;

    // Filter panel bawah (riwayat)
    private widget.Tanggal DTPCari1, DTPCari2;
    private widget.TextBox TCari;
    private widget.Button  BtnCari;
    private DefaultTableModel tabMode;

    // ---- Constants ----
    private static final String[] LOKASI_DISP = {
        "Lengan Kiri (LA)", "Lengan Kanan (RA)",
        "Paha Kiri (LL)",   "Paha Kanan (RL)",
        "Bokong (BU)",      "Lainnya (OT)"
    };
    private static final String[] LOKASI_CODE = {"LA","RA","LL","RL","BU","OT"};
    private static final String[] SUMBER_DANA = {"BPJS/JKN (public)","Umum (private)","Lainnya (other)"};
    private static final String[] SUMBER_CODE = {"public","private","other"};
    private static final String[] WAKTU_DISP  = {"Ideal (IM-Ideal)","Kejar (IM-Kejar)","Tidak Diatur (-)"};
    private static final String[] WAKTU_CODE  = {"IM-Ideal","IM-Kejar","-"};

    /** Daftar Nama Seri imunisasi standar Kemkes (Permenkes 12/2017 + program nasional).
     *  ComboBox editable — kalau dokter butuh tulis manual, bisa ketik bebas. */
    private static final String[] NAMA_SERI = {
        "",  // kosong (default)
        "Imunisasi Dasar Lengkap",
        "Imunisasi Lanjutan Baduta",
        "Imunisasi Anak Sekolah (BIAS)",
        "Vaksinasi COVID-19 Primer",
        "Vaksinasi COVID-19 Booster 1",
        "Vaksinasi COVID-19 Booster 2",
        "Imunisasi Tetanus Difteri Ibu Hamil",
        "Imunisasi Tetanus Difteri Catin",
        "Imunisasi HPV Anak Sekolah",
        "Vaksinasi Anti Rabies Pasca Pajanan (VAR PEP)",
        "Vaksinasi Hepatitis B Dewasa",
        "Vaksinasi Influenza Tahunan",
        "Lainnya"
    };

    public DlgDataImunisasiPasien(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle(":: Data Imunisasi Pasien (FHIR Immunization SatuSehat) ::");
        // Full screen — sesuai ukuran layar
        java.awt.Dimension scr = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(scr.width, scr.height - 40);  // -40 untuk taskbar
        setLocation(0, 0);
        buildUI();
        loadVaksinList();
        emptyFields();
    }

    /** Caller wajib panggil setelah konstruktor. */
    public void setNoRm(String noRwt, String noRm, String nama) {
        this.noRawat = noRwt;
        this.noRkm = noRm;
        this.namaPasien = nama;
        lblPasien.setText("Pasien: " + nama + "  |  No.RM: " + noRm + "  |  No.Rawat: " + noRwt);
        tampilData();
    }

    /** Placeholder untuk konsistensi pattern Khanza (caller existing panggil isCek). */
    public void isCek() { /* no-op */ }

    // ====================================================================
    // UI BUILD
    // ====================================================================
    private void buildUI() {
        internalFrame1 = new widget.InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Data Imunisasi Pasien ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11),
            new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(0, 4));

        // NORTH: header pasien (shared di kedua tab)
        internalFrame1.add(buildHeader(), BorderLayout.NORTH);

        // CENTER: JTabbedPane — Tab 1 (Input form, di-scroll) + Tab 2 (Data Input table)
        javax.swing.JTabbedPane tabRoot = new javax.swing.JTabbedPane();
        tabRoot.setFont(new Font("Tahoma", 0, 11));
        JScrollPane spInput = new JScrollPane(buildInputForm());
        spInput.setBorder(BorderFactory.createEmptyBorder());
        spInput.getVerticalScrollBar().setUnitIncrement(16);
        tabRoot.addTab("Input", spInput);
        tabRoot.addTab("Data Input", buildTableArea());
        internalFrame1.add(tabRoot, BorderLayout.CENTER);

        // SOUTH: button bar (shared by kedua tab)
        internalFrame1.add(buildButtonBar(), BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(internalFrame1, BorderLayout.CENTER);

        // Dialog cari petugas + listener
        dlgPetugas = new DlgCariPetugas(null, false);
        dlgPetugas.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override public void windowClosed(WindowEvent e) {
                if (dlgPetugas.getTable().getSelectedRow() != -1) {
                    KdPeg.setText(dlgPetugas.getTable().getValueAt(dlgPetugas.getTable().getSelectedRow(), 0).toString());
                    TPegawai.setText(dlgPetugas.getTable().getValueAt(dlgPetugas.getTable().getSelectedRow(), 1).toString());
                }
            }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });
    }

    private widget.panelisi buildHeader() {
        widget.panelisi p = new widget.panelisi();
        p.setLayout(null);
        p.setPreferredSize(new Dimension(880, 32));
        lblPasien = new widget.Label();
        lblPasien.setText("Pasien: -");
        lblPasien.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblPasien.setBounds(10, 6, 870, 22);
        p.add(lblPasien);
        return p;
    }

    private widget.panelisi buildInputForm() {
        widget.panelisi p = new widget.panelisi();
        p.setLayout(null);
        p.setPreferredSize(new Dimension(880, 510));  // tambah ruang untuk panel Petunjuk
        p.setBorder(BorderFactory.createTitledBorder(":: Input Data ::"));

        Font lblFont = new Font("Tahoma", 0, 11);
        int LX = 14, FX = 150, RH = 28, W2 = 180, W3 = 320, W4 = 600;
        int y = 26;

        // Tanggal + Jam
        addLabel(p, "Tanggal Imunisasi :", LX, y, 130, 22, lblFont);
        DTPTgl = new widget.Tanggal();
        DTPTgl.setDisplayFormat("dd-MM-yyyy");
        DTPTgl.setBounds(FX, y, 130, 23);
        p.add(DTPTgl);

        addLabel(p, "Jam :", 290, y, 40, 22, lblFont);
        cmbJam = mkJamCombo(24); cmbJam.setBounds(335, y, 55, 23); p.add(cmbJam);
        cmbMnt = mkJamCombo(60); cmbMnt.setBounds(395, y, 55, 23); p.add(cmbMnt);
        cmbDtk = mkJamCombo(60); cmbDtk.setBounds(455, y, 55, 23); p.add(cmbDtk);
        y += RH;

        // Vaksin
        addLabel(p, "Jenis Vaksin :", LX, y, 130, 22, lblFont);
        cmbVaksin = new widget.ComboBox();
        cmbVaksin.setBounds(FX, y, W4, 23);
        p.add(cmbVaksin);
        y += RH;

        // Lokasi suntik + Dosis ke + Total dosis seri
        addLabel(p, "Lokasi Suntik :", LX, y, 130, 22, lblFont);
        cmbLokasi = new widget.ComboBox();
        cmbLokasi.setModel(new DefaultComboBoxModel<>(LOKASI_DISP));
        cmbLokasi.setBounds(FX, y, W2, 23);
        p.add(cmbLokasi);

        addLabel(p, "Dosis Ke- :", 350, y, 80, 22, lblFont);
        spnDosis = new javax.swing.JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        spnDosis.setBounds(430, y, 60, 23);
        p.add(spnDosis);

        addLabel(p, "Total Dosis Seri :", 510, y, 110, 22, lblFont);
        spnTotalDosis = new javax.swing.JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        spnTotalDosis.setToolTipText("Total dosis dalam seri (0 = tidak diisi)");
        spnTotalDosis.setBounds(625, y, 60, 23);
        p.add(spnTotalDosis);
        y += RH;

        // Nama Seri
        addLabel(p, "Nama Seri :", LX, y, 130, 22, lblFont);
        cmbNamaSeri = new widget.ComboBox();
        cmbNamaSeri.setModel(new DefaultComboBoxModel<>(NAMA_SERI));
        cmbNamaSeri.setEditable(true);  // boleh ketik manual kalau seri tidak ada di list
        cmbNamaSeri.setToolTipText("Pilih dari list standar Kemkes atau ketik manual. Bukan nama vaksin — ini nama rangkaian/program imunisasi.");
        cmbNamaSeri.setBounds(FX, y, W4, 23);
        p.add(cmbNamaSeri);
        y += RH;

        // ICD-10 cegah — combobox editable dari ICD10_VAKSIN.
        // Nama penyakit auto-derived dari label combobox (format "kode - nama") saat save.
        addLabel(p, "ICD-10 Penyakit Dicegah :", LX, y, 165, 22, lblFont);
        cmbICD10 = new widget.ComboBox();
        cmbICD10.setModel(new javax.swing.DefaultComboBoxModel<>(ICD10_VAKSIN));
        cmbICD10.setEditable(true);
        cmbICD10.setToolTipText("Pilih dari daftar atau ketik kode ICD-10 manual. Format item: 'kode - nama'.");
        cmbICD10.setBounds(FX+30, y, 250, 23);
        p.add(cmbICD10);
        y += RH;

        // No Batch + Tgl Kadaluarsa
        addLabel(p, "No. Batch :", LX, y, 130, 22, lblFont);
        txtNoBatch = new widget.TextBox();
        txtNoBatch.setDocument(new batasInput((byte) 20).getKata(txtNoBatch));
        txtNoBatch.setBounds(FX, y, W2, 23);
        p.add(txtNoBatch);

        addLabel(p, "Tgl Kadaluarsa :", 350, y, 110, 22, lblFont);
        DTPKadaluarsa = new widget.Tanggal();
        DTPKadaluarsa.setDisplayFormat("dd-MM-yyyy");
        DTPKadaluarsa.setBounds(460, y, 130, 23);
        p.add(DTPKadaluarsa);
        y += RH;

        // Manufaktur
        addLabel(p, "Manufaktur :", LX, y, 130, 22, lblFont);
        txtManufaktur = new widget.TextBox();
        txtManufaktur.setDocument(new batasInput((byte) 100).getKata(txtManufaktur));
        txtManufaktur.setToolTipText("Nama produsen vaksin (mis. Bio Farma, Sinovac)");
        txtManufaktur.setBounds(FX, y, W4, 23);
        p.add(txtManufaktur);
        y += RH;

        // Sumber Dana + Waktu
        addLabel(p, "Sumber Dana :", LX, y, 130, 22, lblFont);
        cmbSumberDana = new widget.ComboBox();
        cmbSumberDana.setModel(new DefaultComboBoxModel<>(SUMBER_DANA));
        cmbSumberDana.setBounds(FX, y, W2, 23);
        p.add(cmbSumberDana);

        addLabel(p, "Waktu Imunisasi :", 350, y, 110, 22, lblFont);
        cmbWaktuImunisasi = new widget.ComboBox();
        cmbWaktuImunisasi.setModel(new DefaultComboBoxModel<>(WAKTU_DISP));
        cmbWaktuImunisasi.setToolTipText("Ideal = jadwal Permenkes; Kejar = catch-up (terlambat)");
        cmbWaktuImunisasi.setBounds(460, y, W2, 23);
        p.add(cmbWaktuImunisasi);
        y += RH;

        // Petugas (NIP + Nama + tombol cari)
        addLabel(p, "Petugas :", LX, y, 130, 22, lblFont);
        KdPeg = new widget.TextBox();
        KdPeg.setDocument(new batasInput((byte) 20).getKata(KdPeg));
        KdPeg.setBounds(FX, y, 110, 23);
        KdPeg.setToolTipText("NIK pegawai yang memberikan imunisasi");
        p.add(KdPeg);
        TPegawai = new widget.TextBox();
        TPegawai.setEditable(false);
        TPegawai.setBounds(FX + 115, y, 350, 23);
        p.add(TPegawai);
        BtnSeekPetugas = new widget.Button();
        BtnSeekPetugas.setText("...");
        BtnSeekPetugas.setToolTipText("Cari petugas");
        BtnSeekPetugas.setBounds(FX + 468, y, 30, 23);
        BtnSeekPetugas.addActionListener(e -> cariPetugas());
        p.add(BtnSeekPetugas);
        y += RH;

        // Keterangan
        addLabel(p, "Keterangan :", LX, y, 130, 22, lblFont);
        txtKeterangan = new widget.TextArea();
        txtKeterangan.setRows(2);
        JScrollPane spKet = new JScrollPane(txtKeterangan);
        spKet.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        spKet.setBounds(FX, y, W4, 46);
        p.add(spKet);
        y += 50;

        // Petunjuk Pengisian (panel info dengan tips per field)
        widget.Label lblPetunjuk = new widget.Label();
        lblPetunjuk.setText(
            "<html><div style='font-family:Tahoma; font-size:10px; padding:6px;'>"
          + "<b>&#x1F4DD; Petunjuk Pengisian:</b><br>"
          + "&bull; <b>Nama Seri</b>: program imunisasi (mis. <i>\"Imunisasi Dasar Lengkap\"</i>), <u>bukan</u> nama vaksin. Pilih dari list atau ketik manual.<br>"
          + "&bull; <b>Dosis Ke- / Total Dosis Seri</b>: mis. DPT-HB-Hib dosis 2 dari 3 → Dosis Ke=2, Total=3.<br>"
          + "&bull; <b>ICD-10 Penyakit Dicegah</b>: kode penyakit yg dicegah vaksin. Contoh: <code>B05.9</code>=Campak, <code>A37.0</code>=Pertussis, <code>A36.9</code>=Difteri, <code>B16.9</code>=Hepatitis B, <code>U07.1</code>=COVID-19.<br>"
          + "&bull; <b>Lokasi Suntik</b>: anatomi tempat suntikan. Bayi <12 bulan biasanya <i>Paha (LL/RL)</i>; anak/dewasa biasanya <i>Lengan (LA/RA)</i>.<br>"
          + "&bull; <b>Sumber Dana</b>: <i>BPJS/JKN</i> = pemerintah/program; <i>Umum</i> = pasien bayar mandiri.<br>"
          + "&bull; <b>Waktu Imunisasi</b>: <i>Ideal</i> = tepat jadwal Permenkes 12/2017; <i>Kejar</i> = terlambat (catch-up); <i>Tidak Diatur</i> = vaksin non-rutin (VAR, influenza, dll).<br>"
          + "&bull; <b>No. Batch &amp; Kadaluarsa</b>: wajib diisi sesuai vial vaksin yg dipakai (untuk traceability)."
          + "</div></html>");
        lblPetunjuk.setBackground(new Color(255, 252, 220));  // kuning soft
        lblPetunjuk.setOpaque(true);
        lblPetunjuk.setBorder(BorderFactory.createLineBorder(new Color(220, 200, 100)));
        lblPetunjuk.setBounds(LX, y, W4 + 130, 122);
        p.add(lblPetunjuk);

        return p;
    }


    private widget.panelisi buildTableArea() {
        widget.panelisi wrap = new widget.panelisi();
        wrap.setLayout(new BorderLayout());
        wrap.setPreferredSize(new Dimension(880, 200));
        wrap.setBorder(BorderFactory.createTitledBorder(":: Riwayat Imunisasi Pasien ::"));

        tabMode = new DefaultTableModel(null, new String[]{
            "Tanggal","Jam","Vaksin","Lokasi","Dosis Ke-","No.Batch","Kadaluarsa",
            "Sumber Dana","Waktu","Nama Seri","Petugas","kode_brng","Pasien","No.Rawat"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbList = new widget.Table();
        tbList.setModel(tabMode);
        tbList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // width: kode_brng (idx 11) di-hidden 0 width, Pasien + No.Rawat tampil
        int[] widths = {80, 65, 220, 75, 60, 90, 80, 100, 90, 150, 130, 0, 200, 140};
        for (int i = 0; i < widths.length; i++) {
            tbList.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            if (i == 11) {  // hidden kode_brng
                tbList.getColumnModel().getColumn(i).setMinWidth(0);
                tbList.getColumnModel().getColumn(i).setMaxWidth(0);
            }
        }
        tbList.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedRowToForm();
        });

        // Popup menu klik kanan → "Catat KIPI..."
        javax.swing.JPopupMenu popupKipi = new javax.swing.JPopupMenu();
        javax.swing.JMenuItem miCatatKipi = new javax.swing.JMenuItem("Catat KIPI...");
        try {
            miCatatKipi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/5868989_coronavirus_drug_medic_medical_medicine_icon.png")));
        } catch (Exception ex) { /* icon optional */ }
        miCatatKipi.addActionListener(e -> openDlgKipi());
        popupKipi.add(miCatatKipi);
        tbList.setComponentPopupMenu(popupKipi);
        // Auto-select row di bawah cursor saat klik kanan
        tbList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent e) {
                if (javax.swing.SwingUtilities.isRightMouseButton(e)) {
                    int row = tbList.rowAtPoint(e.getPoint());
                    if (row >= 0) tbList.setRowSelectionInterval(row, row);
                }
            }
        });

        // Filter bar (NORTH): Tanggal (dd-MM-yyyy) s.d. + Keyword
        widget.panelisi filter = new widget.panelisi();
        filter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 4));
        filter.setPreferredSize(new Dimension(880, 32));
        String today = new java.text.SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date());
        addLabel(filter, "Tanggal :", 0, 0, 55, 23, new Font("Tahoma", 0, 11));
        DTPCari1 = new widget.Tanggal();
        DTPCari1.setModel(new DefaultComboBoxModel(new String[]{ "01-01-2000" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setPreferredSize(new Dimension(110, 23));
        filter.add(DTPCari1);
        addLabel(filter, "s.d.", 0, 0, 28, 23, new Font("Tahoma", 0, 11));
        DTPCari2 = new widget.Tanggal();
        DTPCari2.setModel(new DefaultComboBoxModel(new String[]{ today }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setPreferredSize(new Dimension(110, 23));
        filter.add(DTPCari2);
        addLabel(filter, "Keyword :", 0, 0, 60, 23, new Font("Tahoma", 0, 11));
        TCari = new widget.TextBox();
        TCari.setPreferredSize(new Dimension(200, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) tampilData();
            }
        });
        filter.add(TCari);
        BtnCari = mkButton("Cari", "Filter riwayat per tanggal & keyword", 'C', "/picture/Search-16x16.png", e -> tampilData());
        BtnCari.setPreferredSize(new Dimension(80, 30));
        filter.add(BtnCari);
        wrap.add(filter, BorderLayout.NORTH);

        widget.ScrollPane scroll = new widget.ScrollPane();
        scroll.setViewportView(tbList);
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    /** Buka DlgDataKipiPasien dengan context imunisasi yang dipilih di tabel. */
    private void openDlgKipi() {
        int r = tbList.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu baris imunisasi di tabel.");
            return;
        }
        String tglIm = String.valueOf(tabMode.getValueAt(r, 0));
        String jamIm = String.valueOf(tabMode.getValueAt(r, 1));
        String vaksinNm = String.valueOf(tabMode.getValueAt(r, 2));
        int dose;
        try { dose = Integer.parseInt(String.valueOf(tabMode.getValueAt(r, 4))); }
        catch (Exception ex) { dose = 0; }
        String kdBrng = String.valueOf(tabMode.getValueAt(r, 11));

        DlgDataKipiPasien form = new DlgDataKipiPasien(null, true);
        form.setContext(noRawat, tglIm, jamIm, kdBrng, vaksinNm, dose, namaPasien, noRkm);
        form.setLocationRelativeTo(this);
        form.setVisible(true);
    }

    // ============================================================
    // HELPERS UI
    // ============================================================
    private void addLabel(JPanel parent, String text, int x, int y, int w, int h, Font f) {
        widget.Label lbl = new widget.Label();
        lbl.setText(text);
        lbl.setFont(f);
        lbl.setBounds(x, y, w, h);
        parent.add(lbl);
    }

    private widget.ComboBox mkJamCombo(int max) {
        String[] items = new String[max];
        for (int k = 0; k < max; k++) items[k] = String.format("%02d", k);
        widget.ComboBox c = new widget.ComboBox();
        c.setModel(new DefaultComboBoxModel<>(items));
        return c;
    }

    private widget.Button mkButton(String label, String tip, char mnemonic, String iconPath, java.awt.event.ActionListener act) {
        widget.Button b = new widget.Button();
        b.setText(label);
        b.setToolTipText(tip);
        b.setMnemonic(mnemonic);
        b.setFont(new Font("Tahoma", 0, 11));
        if (iconPath != null) {
            try {
                b.setIcon(new javax.swing.ImageIcon(getClass().getResource(iconPath)));
            } catch (Exception ex) { /* icon optional, ignore */ }
        }
        b.addActionListener(act);
        return b;
    }

    /** Button bar di panel bawah (mirip jPanel3 di form Khanza lain) dgn icon standar. */
    private widget.panelisi buildButtonBar() {
        widget.panelisi bar = new widget.panelisi();
        bar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 4));
        bar.setPreferredSize(new Dimension(880, 38));
        BtnSimpan    = mkButton("Simpan",    "Alt+S - Simpan data imunisasi (CREATE/UPDATE)", 'S', "/picture/save-16x16.png",     e -> simpan());
        BtnBersihkan = mkButton("Bersihkan", "Alt+B - Reset form ke kondisi awal",            'B', "/picture/Cancel-2-16x16.png", e -> emptyFields());
        BtnHapus     = mkButton("Hapus",     "Alt+H - Hapus row terpilih dari tabel",         'H', "/picture/stop_f2.png",        e -> hapus());
        BtnTutup     = mkButton("Tutup",     "Alt+T - Tutup form",                            'T', "/picture/exit.png",           e -> dispose());
        bar.add(BtnSimpan); bar.add(BtnBersihkan); bar.add(BtnHapus); bar.add(BtnTutup);
        return bar;
    }

    private void cariPetugas() {
        dlgPetugas.emptTeks();
        dlgPetugas.isCek();
        dlgPetugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dlgPetugas.setLocationRelativeTo(internalFrame1);
        dlgPetugas.setVisible(true);
    }

    // ============================================================
    // DATA OPS
    // ============================================================
    private void loadVaksinList() {
        cmbVaksin.removeAllItems();
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select m.kode_brng, ifnull(m.vaksin_display, b.nama_brng) as display "
              + "from satu_sehat_mapping_vaksin m "
              + "inner join databarang b on b.kode_brng = m.kode_brng "
              + "order by display")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cmbVaksin.addItem(new VaksinItem(rs.getString("kode_brng"), rs.getString("display")));
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("Notif loadVaksinList : " + ex);
        }
    }

    private void tampilData() {
        Valid.tabelKosong(tabMode);
        String tgl1 = Valid.SetTgl(DTPCari1.getSelectedItem() + "");
        String tgl2 = Valid.SetTgl(DTPCari2.getSelectedItem() + "");
        String kw = TCari.getText() == null ? "" : TCari.getText().trim();
        // Filter pasien TIDAK lagi dipakai — tampil semua data RS, filter via TCari + tanggal.
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select ip.no_rawat, ip.tgl_perawatan, ip.jam, ip.kode_brng, "
              + "  ifnull(m.vaksin_display, b.nama_brng) as vaksin, "
              + "  ip.site, ip.dose_number, ip.no_batch, ip.tgl_kadaluarsa, "
              + "  ip.funding_source, ip.routine_timing, ifnull(ip.series,'') as series, "
              + "  ifnull(pg.nama,'') as petugas, ifnull(p.nm_pasien,'') as pasien "
              + "from imunisasi_pasien ip "
              + "inner join reg_periksa rp on rp.no_rawat = ip.no_rawat "
              + "left join pasien p on p.no_rkm_medis = rp.no_rkm_medis "
              + "left join satu_sehat_mapping_vaksin m on m.kode_brng = ip.kode_brng "
              + "left join databarang b on b.kode_brng = ip.kode_brng "
              + "left join pegawai pg on pg.nik = ip.nip "
              + "where ip.tgl_perawatan between ? and ? "
              + (kw.isEmpty() ? "" : "and (ifnull(m.vaksin_display,b.nama_brng) like ? or ip.no_batch like ? or pg.nama like ? or ip.kode_brng like ? or p.nm_pasien like ? or ip.no_rawat like ?) ")
              + "order by ip.tgl_perawatan desc, ip.jam desc")) {
            ps.setString(1, tgl1);
            ps.setString(2, tgl2);
            if (!kw.isEmpty()) {
                ps.setString(3, "%" + kw + "%");
                ps.setString(4, "%" + kw + "%");
                ps.setString(5, "%" + kw + "%");
                ps.setString(6, "%" + kw + "%");
                ps.setString(7, "%" + kw + "%");
                ps.setString(8, "%" + kw + "%");
            }
            System.out.println("==== [DlgDataImunisasiPasien.tampilData] DEBUG ====");
            System.out.println("tgl1='"+tgl1+"' tgl2='"+tgl2+"' kw='"+kw+"'");
            ResultSet rs = ps.executeQuery();
            int cnt = 0;
            while (rs.next()) {
                cnt++;
                tabMode.addRow(new Object[]{
                    rs.getString("tgl_perawatan"), rs.getString("jam"),
                    rs.getString("vaksin"), rs.getString("site"),
                    rs.getInt("dose_number"), rs.getString("no_batch"),
                    rs.getString("tgl_kadaluarsa"), rs.getString("funding_source"),
                    rs.getString("routine_timing"), rs.getString("series"),
                    rs.getString("petugas"), rs.getString("kode_brng"),
                    rs.getString("pasien"), rs.getString("no_rawat")
                });
            }
            rs.close();
            System.out.println("[tampilData] baris tampil = "+cnt);
        } catch (Exception ex) {
            System.out.println("Notif tampilData : " + ex);
        }
    }

    private void loadSelectedRowToForm() {
        int r = tbList.getSelectedRow();
        if (r < 0) return;
        Valid.SetTgl(DTPTgl, String.valueOf(tabMode.getValueAt(r, 0)));
        String jam = String.valueOf(tabMode.getValueAt(r, 1));
        if (jam != null && jam.length() >= 8) {
            cmbJam.setSelectedItem(jam.substring(0, 2));
            cmbMnt.setSelectedItem(jam.substring(3, 5));
            cmbDtk.setSelectedItem(jam.substring(6, 8));
        }
        String kb = String.valueOf(tabMode.getValueAt(r, 11));
        for (int k = 0; k < cmbVaksin.getItemCount(); k++) {
            Object it = cmbVaksin.getItemAt(k);
            if (it instanceof VaksinItem && ((VaksinItem) it).kodeBrng.equals(kb)) {
                cmbVaksin.setSelectedIndex(k); break;
            }
        }
        String site = String.valueOf(tabMode.getValueAt(r, 3));
        for (int k = 0; k < LOKASI_CODE.length; k++) {
            if (LOKASI_CODE[k].equals(site)) { cmbLokasi.setSelectedIndex(k); break; }
        }
        spnDosis.setValue(tabMode.getValueAt(r, 4));
        txtNoBatch.setText(String.valueOf(tabMode.getValueAt(r, 5)));
        String exp = String.valueOf(tabMode.getValueAt(r, 6));
        if (exp != null && !"null".equals(exp) && !exp.isEmpty()) {
            Valid.SetTgl(DTPKadaluarsa, exp);
        }
        String sumber = String.valueOf(tabMode.getValueAt(r, 7));
        for (int k = 0; k < SUMBER_CODE.length; k++) {
            if (SUMBER_CODE[k].equals(sumber)) { cmbSumberDana.setSelectedIndex(k); break; }
        }
        String waktu = String.valueOf(tabMode.getValueAt(r, 8));
        for (int k = 0; k < WAKTU_CODE.length; k++) {
            if (WAKTU_CODE[k].equals(waktu)) { cmbWaktuImunisasi.setSelectedIndex(k); break; }
        }
        String seri = String.valueOf(tabMode.getValueAt(r, 9));
        if (seri == null || "null".equals(seri)) seri = "";
        cmbNamaSeri.setSelectedItem(seri);
    }

    private void emptyFields() {
        java.util.Date now = new java.util.Date();
        Valid.SetTgl(DTPTgl, new java.text.SimpleDateFormat("yyyy-MM-dd").format(now));
        Valid.SetTgl(DTPKadaluarsa, new java.text.SimpleDateFormat("yyyy-MM-dd").format(now));
        String[] jamNow = new java.text.SimpleDateFormat("HH:mm:ss").format(now).split(":");
        cmbJam.setSelectedItem(jamNow[0]);
        cmbMnt.setSelectedItem(jamNow[1]);
        cmbDtk.setSelectedItem(jamNow[2]);
        if (cmbVaksin.getItemCount() > 0) cmbVaksin.setSelectedIndex(0);
        cmbLokasi.setSelectedIndex(0);
        spnDosis.setValue(1);
        spnTotalDosis.setValue(0);
        cmbNamaSeri.setSelectedIndex(0);  // kosong default
        cmbICD10.setSelectedIndex(0);
        txtNoBatch.setText("");
        txtManufaktur.setText("");
        cmbSumberDana.setSelectedIndex(0);
        cmbWaktuImunisasi.setSelectedIndex(0);
        txtKeterangan.setText("");
        // Auto-fill KdPeg HANYA kalau akses.getkode() valid NIK pegawai;
        // kalau bukan (mis. user "Admin Utama"), kosongkan supaya user wajib klik [...]
        String kd = akses.getkode();
        String nama = lookupNamaPegawai(kd);
        if (!nama.isEmpty()) {
            KdPeg.setText(kd);
            TPegawai.setText(nama);
        } else {
            KdPeg.setText("");
            TPegawai.setText("");
        }
    }

    private String lookupNamaPegawai(String nik) {
        if (nik == null || nik.isEmpty()) return "";
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select nama from pegawai where nik=? limit 1")) {
            ps.setString(1, nik);
            ResultSet rs = ps.executeQuery();
            String n = rs.next() ? rs.getString(1) : "";
            rs.close();
            return n == null ? "" : n;
        } catch (Exception ex) {
            return "";
        }
    }

    private void simpan() {
        if (noRawat == null || noRawat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No.Rawat kosong — silakan pilih pasien dulu dari layar Rawat Jalan/Inap.");
            return;
        }
        if (cmbVaksin.getSelectedItem() == null || !(cmbVaksin.getSelectedItem() instanceof VaksinItem)) {
            JOptionPane.showMessageDialog(this, "Jenis vaksin belum dipilih.");
            return;
        }
        if (KdPeg.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIP/Petugas wajib diisi. Klik tombol [...] untuk cari petugas.");
            KdPeg.requestFocus();
            return;
        }
        VaksinItem vk = (VaksinItem) cmbVaksin.getSelectedItem();
        String tgl = Valid.SetTgl(DTPTgl.getSelectedItem() + "");
        String jam = cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem();
        String tglExp = Valid.SetTgl(DTPKadaluarsa.getSelectedItem() + "");
        String site = LOKASI_CODE[cmbLokasi.getSelectedIndex()];
        int totalDosis = ((Number) spnTotalDosis.getValue()).intValue();

        try (PreparedStatement ps = koneksi.prepareStatement(
                "insert into imunisasi_pasien (no_rawat,tgl_perawatan,jam,kode_brng,site,"
              + "dose_number,series_doses,series,target_disease_code,target_disease_display,"
              + "no_batch,tgl_kadaluarsa,manufacturer,funding_source,routine_timing,keterangan,nip) "
              + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) "
              + "on duplicate key update site=values(site), dose_number=values(dose_number), "
              + "series_doses=values(series_doses), series=values(series), "
              + "target_disease_code=values(target_disease_code), target_disease_display=values(target_disease_display), "
              + "no_batch=values(no_batch), tgl_kadaluarsa=values(tgl_kadaluarsa), "
              + "manufacturer=values(manufacturer), funding_source=values(funding_source), "
              + "routine_timing=values(routine_timing), keterangan=values(keterangan), "
              + "nip=values(nip), tgl_input=now()")) {
            ps.setString(1, noRawat);
            ps.setString(2, tgl);
            ps.setString(3, jam);
            ps.setString(4, vk.kodeBrng);
            ps.setString(5, site);
            ps.setInt   (6, ((Number) spnDosis.getValue()).intValue());
            if (totalDosis == 0) ps.setNull(7, java.sql.Types.INTEGER);
            else ps.setInt(7, totalDosis);
            Object seriSel = cmbNamaSeri.getSelectedItem();
            ps.setString(8,  nullIfEmpty(seriSel == null ? "" : seriSel.toString()));
            // Extract code + nama dari combobox (format "kode - nama"), atau pakai apa adanya kalau user ketik manual
            String icdSel = cmbICD10.getSelectedItem() == null ? "" : cmbICD10.getSelectedItem().toString();
            int icdSep = icdSel.indexOf(" - ");
            String icdCode = icdSep > 0 ? icdSel.substring(0, icdSep).trim() : icdSel.trim();
            String icdNama = icdSep > 0 ? icdSel.substring(icdSep + 3).trim() : "";
            ps.setString(9,  nullIfEmpty(icdCode));
            ps.setString(10, nullIfEmpty(icdNama));
            ps.setString(11, nullIfEmpty(txtNoBatch.getText()));
            if (tglExp == null || tglExp.isEmpty()) ps.setNull(12, java.sql.Types.DATE);
            else ps.setString(12, tglExp);
            ps.setString(13, nullIfEmpty(txtManufaktur.getText()));
            ps.setString(14, SUMBER_CODE[cmbSumberDana.getSelectedIndex()]);
            ps.setString(15, WAKTU_CODE[cmbWaktuImunisasi.getSelectedIndex()]);
            ps.setString(16, nullIfEmpty(txtKeterangan.getText()));
            ps.setString(17, KdPeg.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data imunisasi tersimpan.");
            tampilData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal simpan: " + ex.getMessage());
            System.out.println("Notif simpan imunisasi : " + ex);
        }
    }

    private void hapus() {
        int r = tbList.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Silakan pilih baris dulu di tabel riwayat.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
            "Hapus data imunisasi ini?\nTindakan ini juga menghapus KIPI terkait.",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "delete from imunisasi_pasien where no_rawat=? and tgl_perawatan=? and jam=? and kode_brng=?")) {
            ps.setString(1, noRawat);
            ps.setString(2, String.valueOf(tabMode.getValueAt(r, 0)));
            ps.setString(3, String.valueOf(tabMode.getValueAt(r, 1)));
            ps.setString(4, String.valueOf(tabMode.getValueAt(r, 11)));
            ps.executeUpdate();
            tampilData();
            emptyFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal hapus: " + ex.getMessage());
        }
    }

    private static String nullIfEmpty(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    // Item wrapper untuk ComboBox vaksin
    private static class VaksinItem {
        final String kodeBrng;
        final String display;
        VaksinItem(String kb, String d) { this.kodeBrng = kb; this.display = d; }
        @Override public String toString() { return display + "  [" + kodeBrng + "]"; }
    }
}
