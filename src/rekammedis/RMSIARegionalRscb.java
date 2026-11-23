package rekammedis;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import kepegawaian.DlgCariDokter;
import kepegawaian.DlgCariPegawai;
import kepegawaian.DlgCariPetugas;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.awt.Cursor;
import java.awt.Desktop;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import widget.*;

public class RMSIARegionalRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private Label LCount;

    private TextBox KdDokterPelaksana, NmDokterPelaksana;
    private TextBox KdDokterPemberi, NmDokterPemberi;
    private TextBox TxtPenerimaInformasi, TxtDiagnosis;
    private JCheckBox chkBlokSubArakhnoid, chkSpinal;
    private TextBox TxtIndikasi;
    private TextArea TxtTujuan;
    private TextBox TxtResiko, TxtAlternatif, TxtLainLain;
    // Checkbox Dasar Diagnosis
    private JCheckBox chkAnamnesis, chkPemFisik, chkPemPenunjang, chkTSBedah, chkObgyn, chkMata, chkTHT, chkPenyakitDalam;
    // Checkbox Komplikasi
    private JCheckBox chkMual, chkKakiBerat, chkTensiMenurun, chkNadiMenurun, chkMenggigil, chkPusing, chkSesakNafas, chkSyok;
    // Checkbox Prognosis
    private JCheckBox chkDubia, chkAdBonam, chkMalam;
    private ComboBox CmbPersetujuan;
    private TextBox TxtNamaMenyatakan, TxtTempatLahir, TxtTglLahirPJ, TxtAlamat;
    private ComboBox CmbJKPJ;
    private ComboBox CmbHubungan;
    private TextBox KdDokterAnestesi, NmDokterAnestesi;
    private TextBox TxtNamaSaksi;
    private TextBox KdPetugas, NmPetugas;
    private Button BtnDokterPelaksana, BtnDokterPemberi, BtnDokterAnestesi, BtnPetugas;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariDokter dokterPelaksanaDlg = new DlgCariDokter(null, false);
    private DlgCariPegawai pemberiInfoDlg = new DlgCariPegawai(null, false);
    private DlgCariDokter dokterAnestesiDlg = new DlgCariDokter(null, false);
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);

    private widget.Table tbObat;
    private DefaultTableModel tabMode;
    private ScrollPane Scroll;
    private CekBox ChkInput;
    private PanelBiasa FormInput;
    private ScrollPane ScrollInput;
    private JPanel PanelInput;
    public widget.editorpane LoadHTML;

    public RMSIARegionalRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(900, 650);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ SIA Regional Anestesi ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11), new Color(50, 50, 50)));
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
        tabMode = new DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","Dokter Pelaksana","Pemberi Info",
                         "Penerima Info","Diagnosis","Dasar Diagnosis","Tindakan Kedokteran",
                         "Indikasi","Tujuan","Resiko","Komplikasi","Prognosis",
                         "Alternatif","Lain-lain","Persetujuan","Nama Menyatakan",
                         "Tgl Lahir/JK","Alamat","Hubungan","Dokter Anestesi","Saksi"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbObat.setModel(tabMode);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { if (tabMode.getRowCount() != 0) getData(); }
        });
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem mnCetak = new JMenuItem("Cetak SIA Regional Anestesi");
        mnCetak.setIcon(new ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetak.addActionListener(evt -> cetakSIARegional());
        popupMenu.add(mnCetak);
        tbObat.setComponentPopupMenu(popupMenu);
        Scroll.setViewportView(tbObat);

        // FORM INPUT
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(880, 1050));
        FormInput.setLayout(null);

        int y = 10;
        JLabel lblHeader = new JLabel("SURAT IZIN ANESTESI - REGIONAL ANESTESI");
        lblHeader.setFont(new java.awt.Font("Tahoma", 1, 13)); lblHeader.setBounds(10, y, 500, 20); FormInput.add(lblHeader);

        y += 32;
        Label lblTgl = new Label(); lblTgl.setText("Tanggal :"); lblTgl.setBounds(10, y, 80, 23); FormInput.add(lblTgl);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setBounds(95, y, 130, 23); FormInput.add(TglAsuhan);

        // === INFORMASI SECTION ===
        y += 32;
        JLabel lblInfoHeader = new JLabel("--- INFORMASI ---");
        lblInfoHeader.setFont(new java.awt.Font("Tahoma", 1, 11)); lblInfoHeader.setBounds(10, y, 200, 20); FormInput.add(lblInfoHeader);

        y += 28;
        addDokterLookupPelaksana(y, "Dokter Pelaksana :");

        y += 28;
        addDokterLookupPemberi(y, "Pemberi Informasi :");

        y += 28;
        Label lblPenerima = new Label(); lblPenerima.setText("Penerima Informasi :"); lblPenerima.setBounds(10, y, 130, 23); FormInput.add(lblPenerima);
        TxtPenerimaInformasi = new TextBox(); TxtPenerimaInformasi.setBounds(145, y, 350, 23); FormInput.add(TxtPenerimaInformasi);

        y += 28;
        Label lblDiag = new Label(); lblDiag.setText("Diagnosis :"); lblDiag.setBounds(10, y, 130, 23); FormInput.add(lblDiag);
        TxtDiagnosis = new TextBox(); TxtDiagnosis.setBounds(145, y, 350, 23); FormInput.add(TxtDiagnosis);

        y += 28;
        Label lblDasar = new Label(); lblDasar.setText("Dasar Diagnosis :"); lblDasar.setBounds(10, y, 130, 23); FormInput.add(lblDasar);
        chkAnamnesis = new JCheckBox("Anamnesis", true); chkAnamnesis.setBounds(145, y, 110, 23); chkAnamnesis.setOpaque(false); FormInput.add(chkAnamnesis);
        chkPemFisik = new JCheckBox("Pem. Fisik", true); chkPemFisik.setBounds(260, y, 100, 23); chkPemFisik.setOpaque(false); FormInput.add(chkPemFisik);
        chkPemPenunjang = new JCheckBox("Pem. Penunjang", true); chkPemPenunjang.setBounds(365, y, 130, 23); chkPemPenunjang.setOpaque(false); FormInput.add(chkPemPenunjang);
        y += 23;
        chkTSBedah = new JCheckBox("TS Bedah"); chkTSBedah.setBounds(145, y, 90, 23); chkTSBedah.setOpaque(false); FormInput.add(chkTSBedah);
        chkObgyn = new JCheckBox("Obgyn"); chkObgyn.setBounds(240, y, 75, 23); chkObgyn.setOpaque(false); FormInput.add(chkObgyn);
        chkMata = new JCheckBox("Mata"); chkMata.setBounds(320, y, 65, 23); chkMata.setOpaque(false); FormInput.add(chkMata);
        chkTHT = new JCheckBox("THT"); chkTHT.setBounds(390, y, 60, 23); chkTHT.setOpaque(false); FormInput.add(chkTHT);
        chkPenyakitDalam = new JCheckBox("Penyakit Dalam"); chkPenyakitDalam.setBounds(455, y, 130, 23); chkPenyakitDalam.setOpaque(false); FormInput.add(chkPenyakitDalam);

        y += 28;
        Label lblTindakan = new Label(); lblTindakan.setText("Tindakan Kedokteran :"); lblTindakan.setBounds(10, y, 140, 23); FormInput.add(lblTindakan);
        chkBlokSubArakhnoid = new JCheckBox("Anestesi Blok Sub Arakhnoid", true); chkBlokSubArakhnoid.setBounds(155, y, 220, 23); chkBlokSubArakhnoid.setOpaque(false); FormInput.add(chkBlokSubArakhnoid);
        chkSpinal = new JCheckBox("Spinal"); chkSpinal.setBounds(380, y, 80, 23); chkSpinal.setOpaque(false); FormInput.add(chkSpinal);

        y += 28;
        Label lblIndikasi = new Label(); lblIndikasi.setText("Indikasi :"); lblIndikasi.setBounds(10, y, 130, 23); FormInput.add(lblIndikasi);
        TxtIndikasi = new TextBox(); TxtIndikasi.setBounds(145, y, 350, 23); FormInput.add(TxtIndikasi);
        TxtIndikasi.setText("Untuk operasi : bawah pusar, analgesi persalinan (painless labour)");

        y += 28;
        Label lblTujuan = new Label(); lblTujuan.setText("Tujuan :"); lblTujuan.setBounds(10, y, 130, 23); FormInput.add(lblTujuan);
        TxtTujuan = new TextArea();
        TxtTujuan.setText("Hambatan impuls nyeri suatu bagian tubuh pada impuls saraf sensorik dan motorik sehingga impuls nyeri dari satu bagian tubuh diblokir untuk sementara");
        JScrollPane scrTujuan = new JScrollPane(TxtTujuan);
        scrTujuan.setBounds(145, y, 450, 50); FormInput.add(scrTujuan);

        y += 55;
        Label lblResiko = new Label(); lblResiko.setText("Resiko :"); lblResiko.setBounds(10, y, 130, 23); FormInput.add(lblResiko);
        TxtResiko = new TextBox(); TxtResiko.setBounds(145, y, 350, 23); FormInput.add(TxtResiko);

        y += 28;
        Label lblKomplikasi = new Label(); lblKomplikasi.setText("Komplikasi :"); lblKomplikasi.setBounds(10, y, 130, 23); FormInput.add(lblKomplikasi);
        chkMual = new JCheckBox("Mual", true); chkMual.setBounds(145, y, 70, 23); chkMual.setOpaque(false); FormInput.add(chkMual);
        chkKakiBerat = new JCheckBox("Kedua kaki berat", true); chkKakiBerat.setBounds(220, y, 130, 23); chkKakiBerat.setOpaque(false); FormInput.add(chkKakiBerat);
        chkTensiMenurun = new JCheckBox("Tensi Menurun", true); chkTensiMenurun.setBounds(355, y, 120, 23); chkTensiMenurun.setOpaque(false); FormInput.add(chkTensiMenurun);
        chkNadiMenurun = new JCheckBox("Nadi Menurun", true); chkNadiMenurun.setBounds(480, y, 115, 23); chkNadiMenurun.setOpaque(false); FormInput.add(chkNadiMenurun);
        y += 23;
        chkMenggigil = new JCheckBox("Menggigil", true); chkMenggigil.setBounds(145, y, 90, 23); chkMenggigil.setOpaque(false); FormInput.add(chkMenggigil);
        chkPusing = new JCheckBox("Pusing", true); chkPusing.setBounds(240, y, 75, 23); chkPusing.setOpaque(false); FormInput.add(chkPusing);
        chkSesakNafas = new JCheckBox("Sesak Nafas", true); chkSesakNafas.setBounds(320, y, 105, 23); chkSesakNafas.setOpaque(false); FormInput.add(chkSesakNafas);
        chkSyok = new JCheckBox("Syok", true); chkSyok.setBounds(430, y, 60, 23); chkSyok.setOpaque(false); FormInput.add(chkSyok);

        y += 28;
        Label lblPrognosis = new Label(); lblPrognosis.setText("Prognosis :"); lblPrognosis.setBounds(10, y, 130, 23); FormInput.add(lblPrognosis);
        chkDubia = new JCheckBox("Dubia"); chkDubia.setBounds(145, y, 75, 23); chkDubia.setOpaque(false); FormInput.add(chkDubia);
        chkAdBonam = new JCheckBox("Ad bonam"); chkAdBonam.setBounds(225, y, 100, 23); chkAdBonam.setOpaque(false); FormInput.add(chkAdBonam);
        chkMalam = new JCheckBox("Malam"); chkMalam.setBounds(330, y, 75, 23); chkMalam.setOpaque(false); FormInput.add(chkMalam);

        y += 28;
        Label lblAlternatif = new Label(); lblAlternatif.setText("Alternatif :"); lblAlternatif.setBounds(10, y, 130, 23); FormInput.add(lblAlternatif);
        TxtAlternatif = new TextBox(); TxtAlternatif.setBounds(145, y, 350, 23); FormInput.add(TxtAlternatif);

        y += 28;
        Label lblLain = new Label(); lblLain.setText("Lain-lain :"); lblLain.setBounds(10, y, 130, 23); FormInput.add(lblLain);
        TxtLainLain = new TextBox(); TxtLainLain.setBounds(145, y, 350, 23); FormInput.add(TxtLainLain);

        // === PERSETUJUAN SECTION ===
        y += 32;
        JLabel lblPersetujuanHeader = new JLabel("--- PERSETUJUAN ---");
        lblPersetujuanHeader.setFont(new java.awt.Font("Tahoma", 1, 11)); lblPersetujuanHeader.setBounds(10, y, 200, 20); FormInput.add(lblPersetujuanHeader);

        y += 28;
        Label lblPersetujuan = new Label(); lblPersetujuan.setText("Persetujuan :"); lblPersetujuan.setBounds(10, y, 130, 23); FormInput.add(lblPersetujuan);
        CmbPersetujuan = new ComboBox();
        CmbPersetujuan.setModel(new DefaultComboBoxModel(new String[]{"Setuju","Tolak"}));
        CmbPersetujuan.setBounds(145, y, 150, 23); FormInput.add(CmbPersetujuan);

        y += 28;
        Label lblNama = new Label(); lblNama.setText("Nama Menyatakan :"); lblNama.setBounds(10, y, 130, 23); FormInput.add(lblNama);
        TxtNamaMenyatakan = new TextBox(); TxtNamaMenyatakan.setBounds(145, y, 250, 23); FormInput.add(TxtNamaMenyatakan);

        y += 28;
        Label lblTempatLahir = new Label(); lblTempatLahir.setText("Tempat Lahir :"); lblTempatLahir.setBounds(10, y, 130, 23); FormInput.add(lblTempatLahir);
        TxtTempatLahir = new TextBox(); TxtTempatLahir.setBounds(145, y, 200, 23); FormInput.add(TxtTempatLahir);

        y += 28;
        Label lblTglLahir = new Label(); lblTglLahir.setText("Tgl Lahir :"); lblTglLahir.setBounds(10, y, 130, 23); FormInput.add(lblTglLahir);
        TxtTglLahirPJ = new TextBox(); TxtTglLahirPJ.setBounds(145, y, 120, 23); FormInput.add(TxtTglLahirPJ);
        Label lblJKPJ = new Label(); lblJKPJ.setText("JK :"); lblJKPJ.setBounds(275, y, 25, 23); FormInput.add(lblJKPJ);
        CmbJKPJ = new ComboBox();
        CmbJKPJ.setModel(new DefaultComboBoxModel(new String[]{"Laki-laki", "Perempuan"}));
        CmbJKPJ.setBounds(305, y, 120, 23); FormInput.add(CmbJKPJ);

        y += 28;
        Label lblAlamat = new Label(); lblAlamat.setText("Alamat :"); lblAlamat.setBounds(10, y, 130, 23); FormInput.add(lblAlamat);
        TxtAlamat = new TextBox(); TxtAlamat.setBounds(145, y, 350, 23); FormInput.add(TxtAlamat);

        y += 28;
        Label lblHubungan = new Label(); lblHubungan.setText("Hubungan :"); lblHubungan.setBounds(10, y, 130, 23); FormInput.add(lblHubungan);
        CmbHubungan = new ComboBox();
        CmbHubungan.setModel(new DefaultComboBoxModel(new String[]{"Diri Sendiri","Suami","Istri","Ayah","Ibu","Anak","Saudara","Lainnya"}));
        CmbHubungan.setBounds(145, y, 180, 23); FormInput.add(CmbHubungan);

        y += 28;
        addDokterAnestesi(y, "Dokter Anestesi :");

        y += 28;
        Label lblSaksi = new Label(); lblSaksi.setText("Nama Saksi :"); lblSaksi.setBounds(10, y, 130, 23); FormInput.add(lblSaksi);
        TxtNamaSaksi = new TextBox(); TxtNamaSaksi.setBounds(145, y, 250, 23); FormInput.add(TxtNamaSaksi);

        y += 28;
        addPetugasLookup(y, "Petugas :");

        // Input toggle
        ChkInput = new CekBox();
        ChkInput.setIcon(new ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setMnemonic('1'); ChkInput.setText(".: Input Data"); ChkInput.setToolTipText("Alt+1");
        ChkInput.setBorderPainted(true); ChkInput.setBorderPaintedFlat(true); ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(SwingConstants.RIGHT);
        ChkInput.setPreferredSize(new Dimension(192, 20));
        ChkInput.setRolloverIcon(new ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setRolloverSelectedIcon(new ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setSelectedIcon(new ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setSelected(true);
        ChkInput.addActionListener(evt -> isForm());

        ScrollInput = new ScrollPane(); ScrollInput.setViewportView(FormInput);
        ScrollInput.setPreferredSize(new Dimension(880, 530));
        PanelInput = new JPanel(new BorderLayout());
        PanelInput.setOpaque(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 550));
        PanelInput.add(ScrollInput, BorderLayout.CENTER); PanelInput.add(ChkInput, BorderLayout.PAGE_END);

        // PANEL BAWAH
        panelBawah = new panelisi(); panelBawah.setPreferredSize(new Dimension(44, 54));
        panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));
        BtnSimpan = new Button(); BtnSimpan.setIcon(new ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setMnemonic('S'); BtnSimpan.setText("Simpan"); BtnSimpan.setPreferredSize(new Dimension(100, 30));
        BtnBatal = new Button(); BtnBatal.setIcon(new ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBatal.setMnemonic('B'); BtnBatal.setText("Baru"); BtnBatal.setPreferredSize(new Dimension(100, 30));
        BtnHapus = new Button(); BtnHapus.setIcon(new ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setMnemonic('H'); BtnHapus.setText("Hapus"); BtnHapus.setPreferredSize(new Dimension(100, 30));
        BtnEdit = new Button(); BtnEdit.setIcon(new ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnEdit.setMnemonic('G'); BtnEdit.setText("Ganti"); BtnEdit.setPreferredSize(new Dimension(100, 30));
        BtnPrint = new Button(); BtnPrint.setIcon(new ImageIcon(getClass().getResource("/picture/b_print.png")));
        BtnPrint.setMnemonic('T'); BtnPrint.setText("Cetak"); BtnPrint.setPreferredSize(new Dimension(100, 30));
        BtnKeluar = new Button(); BtnKeluar.setIcon(new ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K'); BtnKeluar.setText("Keluar"); BtnKeluar.setPreferredSize(new Dimension(100, 30));
        Label lblTglC = new Label(); lblTglC.setText("Tanggal :"); lblTglC.setPreferredSize(new Dimension(55, 23));
        DTPCari1 = new widget.Tanggal(); DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setPreferredSize(new Dimension(90, 23));
        Label lblSd = new Label(); lblSd.setText("s.d."); lblSd.setPreferredSize(new Dimension(25, 23));
        DTPCari2 = new widget.Tanggal(); DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setPreferredSize(new Dimension(90, 23));
        Label lblKey = new Label(); lblKey.setText("Key Word :"); lblKey.setPreferredSize(new Dimension(70, 23));
        TCari = new TextBox(); TCari.setPreferredSize(new Dimension(150, 23));
        BtnCari = new Button(); BtnCari.setIcon(new ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnAll = new Button(); BtnAll.setIcon(new ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setPreferredSize(new Dimension(28, 23));
        LCount = new Label(); LCount.setText("Record : 0"); LCount.setPreferredSize(new Dimension(100, 23));
        panelBawah.add(BtnSimpan); panelBawah.add(BtnBatal); panelBawah.add(BtnHapus);
        panelBawah.add(BtnEdit); panelBawah.add(BtnPrint); panelBawah.add(BtnKeluar);
        panelBawah.add(lblTglC); panelBawah.add(DTPCari1); panelBawah.add(lblSd);
        panelBawah.add(DTPCari2); panelBawah.add(lblKey); panelBawah.add(TCari);
        panelBawah.add(BtnCari); panelBawah.add(BtnAll); panelBawah.add(LCount);

        // ACTIONS
        BtnKeluar.addActionListener(evt -> dispose());
        BtnBatal.addActionListener(evt -> emptTeks());
        BtnCari.addActionListener(evt -> tampil());
        BtnAll.addActionListener(evt -> { TCari.setText(""); tampil(); });
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) { if (evt.getKeyCode() == KeyEvent.VK_ENTER) tampil(); }
        });

        BtnSimpan.addActionListener(evt -> {
            if (TNoRw.getText().trim().isEmpty()) { Valid.textKosong(TNoRw, "Pasien");
            } else if (KdDokterPelaksana.getText().trim().isEmpty()) { Valid.textKosong(KdDokterPelaksana, "Dokter Pelaksana");
            } else if (KdDokterAnestesi.getText().trim().isEmpty()) { Valid.textKosong(KdDokterAnestesi, "Dokter Anestesi");
            } else if (KdPetugas.getText().trim().isEmpty()) { Valid.textKosong(KdPetugas, "Petugas");
            } else {
                String tglLahirJk = TxtTempatLahir.getText() + ", " + TxtTglLahirPJ.getText() + " / " + CmbJKPJ.getSelectedItem().toString();
                if (Sequel.menyimpantf("sia_regional_rscb", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 23, new String[]{
                    TNoRw.getText(),
                    Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19),
                    KdDokterPelaksana.getText(), KdDokterPemberi.getText(),
                    TxtPenerimaInformasi.getText(), TxtDiagnosis.getText(), buildDasarDiagnosis(),
                    buildTindakanKedokteran(), TxtIndikasi.getText(), TxtTujuan.getText(),
                    TxtResiko.getText(), buildKomplikasi(),
                    buildPrognosis(),
                    TxtAlternatif.getText(), TxtLainLain.getText(),
                    CmbPersetujuan.getSelectedItem().toString(),
                    TxtNamaMenyatakan.getText(), tglLahirJk,
                    TxtAlamat.getText(),
                    CmbHubungan.getSelectedItem().toString(),
                    KdDokterAnestesi.getText(), TxtNamaSaksi.getText(),
                    KdPetugas.getText()
                })) { tampil(); emptTeks(); }
            }
        });

        BtnHapus.addActionListener(evt -> {
            if (tbObat.getSelectedRow() != -1) {
                if (Sequel.queryu2tf("delete from sia_regional_rscb where no_rawat=? and tanggal=?", 2, new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
                })) { tampil(); emptTeks(); }
            } else JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dihapus...!!!!");
        });

        BtnEdit.addActionListener(evt -> {
            if (TNoRw.getText().trim().isEmpty() || tbObat.getSelectedRow()==-1) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            } else {
                if (Sequel.queryu2tf("delete from sia_regional_rscb where no_rawat=? and tanggal=?", 2, new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
                })) { BtnSimpan.doClick(); }
            }
        });

        BtnPrint.addActionListener(evt -> cetakSIARegional());

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

    private void addDokterLookupPelaksana(int y, String labelText) {
        Label lbl = new Label(); lbl.setText(labelText); lbl.setBounds(10, y, 130, 23); FormInput.add(lbl);
        KdDokterPelaksana = new TextBox(); KdDokterPelaksana.setBounds(145, y, 80, 23); FormInput.add(KdDokterPelaksana);
        NmDokterPelaksana = new TextBox(); NmDokterPelaksana.setBounds(227, y, 200, 23); FormInput.add(NmDokterPelaksana);
        BtnDokterPelaksana = new Button(); BtnDokterPelaksana.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokterPelaksana.setBounds(429, y, 28, 23); FormInput.add(BtnDokterPelaksana);
        BtnDokterPelaksana.addActionListener(evt -> {
            dokterPelaksanaDlg.emptTeks(); dokterPelaksanaDlg.isCek();
            dokterPelaksanaDlg.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20);
            dokterPelaksanaDlg.setLocationRelativeTo(internalFrame1); dokterPelaksanaDlg.setVisible(true);
        });
        dokterPelaksanaDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (dokterPelaksanaDlg.getTable().getSelectedRow() != -1) {
                    KdDokterPelaksana.setText(dokterPelaksanaDlg.getTable().getValueAt(dokterPelaksanaDlg.getTable().getSelectedRow(), 0).toString());
                    NmDokterPelaksana.setText(dokterPelaksanaDlg.getTable().getValueAt(dokterPelaksanaDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });
    }

    private void addDokterLookupPemberi(int y, String labelText) {
        Label lbl = new Label(); lbl.setText(labelText); lbl.setBounds(10, y, 130, 23); FormInput.add(lbl);
        KdDokterPemberi = new TextBox(); KdDokterPemberi.setBounds(145, y, 80, 23); FormInput.add(KdDokterPemberi);
        NmDokterPemberi = new TextBox(); NmDokterPemberi.setBounds(227, y, 200, 23); FormInput.add(NmDokterPemberi);
        BtnDokterPemberi = new Button(); BtnDokterPemberi.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokterPemberi.setBounds(429, y, 28, 23); FormInput.add(BtnDokterPemberi);
        BtnDokterPemberi.addActionListener(evt -> {
            pemberiInfoDlg.emptTeks();
            pemberiInfoDlg.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20);
            pemberiInfoDlg.setLocationRelativeTo(internalFrame1); pemberiInfoDlg.setVisible(true);
        });
        pemberiInfoDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (pemberiInfoDlg.getTable().getSelectedRow() != -1) {
                    KdDokterPemberi.setText(pemberiInfoDlg.getTable().getValueAt(pemberiInfoDlg.getTable().getSelectedRow(), 0).toString());
                    NmDokterPemberi.setText(pemberiInfoDlg.getTable().getValueAt(pemberiInfoDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });
    }

    private void addDokterAnestesi(int y, String labelText) {
        Label lbl = new Label(); lbl.setText(labelText); lbl.setBounds(10, y, 130, 23); FormInput.add(lbl);
        KdDokterAnestesi = new TextBox(); KdDokterAnestesi.setBounds(145, y, 80, 23); FormInput.add(KdDokterAnestesi);
        NmDokterAnestesi = new TextBox(); NmDokterAnestesi.setBounds(227, y, 200, 23); FormInput.add(NmDokterAnestesi);
        BtnDokterAnestesi = new Button(); BtnDokterAnestesi.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokterAnestesi.setBounds(429, y, 28, 23); FormInput.add(BtnDokterAnestesi);
        BtnDokterAnestesi.addActionListener(evt -> {
            dokterAnestesiDlg.emptTeks(); dokterAnestesiDlg.isCek();
            dokterAnestesiDlg.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20);
            dokterAnestesiDlg.setLocationRelativeTo(internalFrame1); dokterAnestesiDlg.setVisible(true);
        });
        dokterAnestesiDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (dokterAnestesiDlg.getTable().getSelectedRow() != -1) {
                    KdDokterAnestesi.setText(dokterAnestesiDlg.getTable().getValueAt(dokterAnestesiDlg.getTable().getSelectedRow(), 0).toString());
                    NmDokterAnestesi.setText(dokterAnestesiDlg.getTable().getValueAt(dokterAnestesiDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });
    }

    private void addPetugasLookup(int y, String labelText) {
        Label lbl = new Label(); lbl.setText(labelText); lbl.setBounds(10, y, 130, 23); FormInput.add(lbl);
        KdPetugas = new TextBox(); KdPetugas.setBounds(145, y, 80, 23); FormInput.add(KdPetugas);
        NmPetugas = new TextBox(); NmPetugas.setBounds(227, y, 200, 23); FormInput.add(NmPetugas);
        BtnPetugas = new Button(); BtnPetugas.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPetugas.setBounds(429, y, 28, 23); FormInput.add(BtnPetugas);
        BtnPetugas.addActionListener(evt -> {
            petugasDlg.emptTeks(); petugasDlg.isCek();
            petugasDlg.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20);
            petugasDlg.setLocationRelativeTo(internalFrame1); petugasDlg.setVisible(true);
        });
        petugasDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (petugasDlg.getTable().getSelectedRow() != -1) {
                    KdPetugas.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(), 0).toString());
                    NmPetugas.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });
    }

    private void isForm() {
        if (ChkInput.isSelected()) { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 550)); FormInput.setVisible(true); ChkInput.setVisible(true);
        } else { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 20)); FormInput.setVisible(false); ChkInput.setVisible(true); }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select s.no_rawat, r.no_rkm_medis, p.nm_pasien, s.tanggal, "+
                "d1.nm_dokter as nm_dokter_pelaksana, ifnull(peg.nama,d2.nm_dokter) as nm_pemberi_info, "+
                "s.penerima_informasi, s.diagnosis, s.dasar_diagnosis, s.tindakan_kedokteran, "+
                "s.indikasi, s.tujuan, s.resiko, s.komplikasi, s.prognosis, "+
                "s.alternatif, s.lain_lain, s.persetujuan, s.nama_penyatakan, "+
                "s.tgl_lahir_jk, s.alamat, s.hubungan, "+
                "d3.nm_dokter as nm_dokter_anestesi, s.nama_saksi "+
                "from sia_regional_rscb s inner join reg_periksa r on s.no_rawat=r.no_rawat "+
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "+
                "left join dokter d1 on s.kd_dokter_pelaksana=d1.kd_dokter "+
                "left join dokter d2 on s.kd_dokter_pemberi=d2.kd_dokter "+
                "left join pegawai peg on s.kd_dokter_pemberi=peg.nik "+
                "left join dokter d3 on s.kd_dokter_anestesi=d3.kd_dokter "+
                "where s.tanggal between ? and ? and (s.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) "+
                "order by s.tanggal desc");
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
            ps.setString(3, "%"+TCari.getText().trim()+"%");
            ps.setString(4, "%"+TCari.getText().trim()+"%");
            ps.setString(5, "%"+TCari.getText().trim()+"%");
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                    rs.getString("tanggal"), rs.getString("nm_dokter_pelaksana"),
                    rs.getString("nm_pemberi_info"), rs.getString("penerima_informasi"),
                    rs.getString("diagnosis"), rs.getString("dasar_diagnosis"),
                    rs.getString("tindakan_kedokteran"), rs.getString("indikasi"),
                    rs.getString("tujuan"), rs.getString("resiko"),
                    rs.getString("komplikasi"), rs.getString("prognosis"),
                    rs.getString("alternatif"), rs.getString("lain_lain"),
                    rs.getString("persetujuan"), rs.getString("nama_penyatakan"),
                    rs.getString("tgl_lahir_jk"),
                    rs.getString("alamat"), rs.getString("hubungan"),
                    rs.getString("nm_dokter_anestesi"), rs.getString("nama_saksi")
                });
            }
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif SIA Regional : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            String tgl = tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString();
            try { TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tgl)); } catch (Exception e) {}
            try {
                ps = koneksi.prepareStatement("select * from sia_regional_rscb where no_rawat=? and tanggal=?");
                ps.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                ps.setString(2, tgl);
                rs = ps.executeQuery();
                if (rs.next()) {
                    KdDokterPelaksana.setText(rs.getString("kd_dokter_pelaksana"));
                    NmDokterPelaksana.setText(Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", rs.getString("kd_dokter_pelaksana")));
                    KdDokterPemberi.setText(rs.getString("kd_dokter_pemberi"));
                    NmDokterPemberi.setText(Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", rs.getString("kd_dokter_pemberi")));
                    TxtPenerimaInformasi.setText(rs.getString("penerima_informasi"));
                    TxtDiagnosis.setText(rs.getString("diagnosis"));
                    parseDasarDiagnosis(rs.getString("dasar_diagnosis") != null ? rs.getString("dasar_diagnosis") : "");
                    parseTindakanKedokteran(rs.getString("tindakan_kedokteran") != null ? rs.getString("tindakan_kedokteran") : "");
                    TxtIndikasi.setText(rs.getString("indikasi"));
                    TxtTujuan.setText(rs.getString("tujuan"));
                    TxtResiko.setText(rs.getString("resiko"));
                    parseKomplikasi(rs.getString("komplikasi") != null ? rs.getString("komplikasi") : "");
                    parsePrognosis(rs.getString("prognosis") != null ? rs.getString("prognosis") : "");
                    TxtAlternatif.setText(rs.getString("alternatif"));
                    TxtLainLain.setText(rs.getString("lain_lain"));
                    CmbPersetujuan.setSelectedItem(rs.getString("persetujuan"));
                    TxtNamaMenyatakan.setText(rs.getString("nama_penyatakan"));
                    // Parse tempat_lahir, tgl_lahir_pj, jk_pj from individual columns
                    String tempatLahir = rs.getString("tempat_lahir");
                    String tglLahirPj = rs.getString("tgl_lahir_pj");
                    String jkPj = rs.getString("jk_pj");
                    if (tempatLahir != null && !tempatLahir.isEmpty()) {
                        TxtTempatLahir.setText(tempatLahir);
                    }
                    if (tglLahirPj != null && !tglLahirPj.isEmpty()) {
                        TxtTglLahirPJ.setText(tglLahirPj);
                    }
                    if (jkPj != null && !jkPj.isEmpty()) {
                        CmbJKPJ.setSelectedItem(jkPj);
                    } else if (rs.getString("tgl_lahir_jk") != null) {
                        // Backward compatible: parse dari tgl_lahir_jk lama
                        String combined = rs.getString("tgl_lahir_jk");
                        String[] parts = combined.split("/");
                        if (parts.length > 0) {
                            String leftPart = parts[0].trim();
                            // leftPart = "tempat, tgl_lahir"
                            int commaIdx = leftPart.indexOf(",");
                            if (commaIdx > 0) {
                                TxtTempatLahir.setText(leftPart.substring(0, commaIdx).trim());
                                TxtTglLahirPJ.setText(leftPart.substring(commaIdx + 1).trim());
                            } else {
                                TxtTglLahirPJ.setText(leftPart);
                            }
                        }
                        if (parts.length > 1) {
                            String jkVal = parts[1].trim();
                            if (jkVal.contains("P") || jkVal.contains("perempuan") || jkVal.contains("Perempuan")) {
                                CmbJKPJ.setSelectedItem("Perempuan");
                            } else {
                                CmbJKPJ.setSelectedItem("Laki-laki");
                            }
                        }
                    }
                    TxtAlamat.setText(rs.getString("alamat"));
                    CmbHubungan.setSelectedItem(rs.getString("hubungan"));
                    KdDokterAnestesi.setText(rs.getString("kd_dokter_anestesi"));
                    NmDokterAnestesi.setText(Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", rs.getString("kd_dokter_anestesi")));
                    TxtNamaSaksi.setText(rs.getString("nama_saksi"));
                    KdPetugas.setText(rs.getString("nip"));
                    NmPetugas.setText(Sequel.cariIsi("select nama from petugas where nip=?", rs.getString("nip")));
                }
            } catch (Exception e) { System.out.println("Notif getData SIA Regional : " + e);
            } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
        }
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date());
        KdDokterPelaksana.setText(""); NmDokterPelaksana.setText("");
        KdDokterPemberi.setText(""); NmDokterPemberi.setText("");
        TxtPenerimaInformasi.setText(""); TxtDiagnosis.setText("");
        chkAnamnesis.setSelected(true); chkPemFisik.setSelected(true); chkPemPenunjang.setSelected(true);
        chkTSBedah.setSelected(false); chkObgyn.setSelected(false); chkMata.setSelected(false); chkTHT.setSelected(false); chkPenyakitDalam.setSelected(false);
        chkBlokSubArakhnoid.setSelected(true); chkSpinal.setSelected(false);
        TxtIndikasi.setText("Untuk operasi : bawah pusar, analgesi persalinan (painless labour)");
        TxtTujuan.setText("Hambatan impuls nyeri suatu bagian tubuh pada impuls saraf sensorik dan motorik sehingga impuls nyeri dari satu bagian tubuh diblokir untuk sementara");
        TxtResiko.setText("");
        chkMual.setSelected(true); chkKakiBerat.setSelected(true); chkTensiMenurun.setSelected(true); chkNadiMenurun.setSelected(true);
        chkMenggigil.setSelected(true); chkPusing.setSelected(true); chkSesakNafas.setSelected(true); chkSyok.setSelected(true);
        chkDubia.setSelected(false); chkAdBonam.setSelected(false); chkMalam.setSelected(false);
        TxtAlternatif.setText(""); TxtLainLain.setText("");
        CmbPersetujuan.setSelectedIndex(0); TxtNamaMenyatakan.setText(""); TxtTempatLahir.setText(""); TxtTglLahirPJ.setText(""); CmbJKPJ.setSelectedIndex(0);
        TxtAlamat.setText(""); CmbHubungan.setSelectedIndex(0);
        KdDokterAnestesi.setText(""); NmDokterAnestesi.setText("");
        TxtNamaSaksi.setText(""); KdPetugas.setText(""); NmPetugas.setText("");
    }

    public void setNoRm(String norwt, String norm, String nama) {
        TNoRw.setText(norwt); TNoRM.setText(norm); TPasien.setText(nama);
    }

    public void setNoRm(String norwt, Date tgl) {
        TNoRw.setText(norwt); TglAsuhan.setDate(tgl);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", norwt));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
    }

    private String chkCetak(String data, String item) {
        return data.contains(item) ? "&#9745;" : "&#9744;";
    }

    private String buildDasarDiagnosis() {
        StringBuilder sb = new StringBuilder();
        if (chkAnamnesis.isSelected()) sb.append("Anamnesis, ");
        if (chkPemFisik.isSelected()) sb.append("Pemeriksaan fisik, ");
        if (chkPemPenunjang.isSelected()) sb.append("Pemeriksaan penunjang, ");
        if (chkTSBedah.isSelected()) sb.append("TS Bedah, ");
        if (chkObgyn.isSelected()) sb.append("Obgyn, ");
        if (chkMata.isSelected()) sb.append("Mata, ");
        if (chkTHT.isSelected()) sb.append("THT, ");
        if (chkPenyakitDalam.isSelected()) sb.append("Penyakit Dalam, ");
        return sb.length() > 2 ? sb.substring(0, sb.length() - 2) : sb.toString();
    }

    private void parseDasarDiagnosis(String val) {
        chkAnamnesis.setSelected(val.contains("Anamnesis"));
        chkPemFisik.setSelected(val.contains("Pemeriksaan fisik"));
        chkPemPenunjang.setSelected(val.contains("Pemeriksaan penunjang"));
        chkTSBedah.setSelected(val.contains("TS Bedah"));
        chkObgyn.setSelected(val.contains("Obgyn"));
        chkMata.setSelected(val.contains("Mata"));
        chkTHT.setSelected(val.contains("THT"));
        chkPenyakitDalam.setSelected(val.contains("Penyakit Dalam"));
    }

    private String buildTindakanKedokteran() {
        StringBuilder sb = new StringBuilder();
        if (chkBlokSubArakhnoid.isSelected()) sb.append("Anestesi Blok Sub Arakhnoid, ");
        if (chkSpinal.isSelected()) sb.append("Spinal, ");
        return sb.length() > 2 ? sb.substring(0, sb.length() - 2) : sb.toString();
    }

    private void parseTindakanKedokteran(String val) {
        chkBlokSubArakhnoid.setSelected(val.contains("Blok Sub Arakhnoid"));
        chkSpinal.setSelected(val.contains("Spinal"));
    }

    private String buildKomplikasi() {
        StringBuilder sb = new StringBuilder();
        if (chkMual.isSelected()) sb.append("Mual, ");
        if (chkKakiBerat.isSelected()) sb.append("Kedua kaki berat, ");
        if (chkTensiMenurun.isSelected()) sb.append("Tensi Menurun, ");
        if (chkNadiMenurun.isSelected()) sb.append("Nadi Menurun, ");
        if (chkMenggigil.isSelected()) sb.append("Menggigil, ");
        if (chkPusing.isSelected()) sb.append("Pusing, ");
        if (chkSesakNafas.isSelected()) sb.append("Sesak Nafas, ");
        if (chkSyok.isSelected()) sb.append("Syok, ");
        return sb.length() > 2 ? sb.substring(0, sb.length() - 2) : sb.toString();
    }

    private void parseKomplikasi(String val) {
        chkMual.setSelected(val.contains("Mual"));
        chkKakiBerat.setSelected(val.contains("Kedua kaki berat"));
        chkTensiMenurun.setSelected(val.contains("Tensi Menurun"));
        chkNadiMenurun.setSelected(val.contains("Nadi Menurun"));
        chkMenggigil.setSelected(val.contains("Menggigil"));
        chkPusing.setSelected(val.contains("Pusing"));
        chkSesakNafas.setSelected(val.contains("Sesak Nafas"));
        chkSyok.setSelected(val.contains("Syok"));
    }

    private String buildPrognosis() {
        StringBuilder sb = new StringBuilder();
        if (chkDubia.isSelected()) sb.append("Dubia, ");
        if (chkAdBonam.isSelected()) sb.append("Ad bonam, ");
        if (chkMalam.isSelected()) sb.append("Malam, ");
        return sb.length() > 2 ? sb.substring(0, sb.length() - 2) : sb.toString();
    }

    private void parsePrognosis(String val) {
        chkDubia.setSelected(val.contains("Dubia"));
        chkAdBonam.setSelected(val.contains("Ad bonam"));
        chkMalam.setSelected(val.contains("Malam"));
    }

    private void cetakSIARegional() {
        if (tbObat.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu..!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        String noRawat = tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString();
        String tanggal = tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString();

        String noRM = "", nmPasien = "", nmDokterPelaksanaVal = "", nmDokterPemberiVal = "";
        String penerimaInfo = "", diagnosis = "", dasarDiagnosis = "", tindakanKedokteran = "";
        String indikasi = "", tujuan = "", resiko = "", komplikasi = "", prognosis = "";
        String alternatif = "", lainLain = "", persetujuan = "";
        String namaMenyatakan = "", tglLahirJK = "", alamat = "", hubungan = "";
        String kdDokAnestesi = "", nmDokterAnestesiVal = "", namaSaksi = "";

        try {
            ps = koneksi.prepareStatement(
                "select s.*, r.no_rkm_medis, p.nm_pasien, d1.nm_dokter as nm_dokter_pelaksana, " +
                "d2.nm_dokter as nm_dokter_pemberi, d3.nm_dokter as nm_dokter_anestesi " +
                "from sia_regional_rscb s inner join reg_periksa r on s.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                "left join dokter d1 on s.kd_dokter_pelaksana=d1.kd_dokter " +
                "left join dokter d2 on s.kd_dokter_pemberi=d2.kd_dokter " +
                "left join dokter d3 on s.kd_dokter_anestesi=d3.kd_dokter " +
                "where s.no_rawat=? and s.tanggal=?");
            ps.setString(1, noRawat); ps.setString(2, tanggal);
            rs = ps.executeQuery();
            if (rs.next()) {
                noRM = rs.getString("no_rkm_medis");
                nmPasien = rs.getString("nm_pasien");
                nmDokterPelaksanaVal = rs.getString("nm_dokter_pelaksana") != null ? rs.getString("nm_dokter_pelaksana") : "";
                nmDokterPemberiVal = rs.getString("nm_dokter_pemberi") != null ? rs.getString("nm_dokter_pemberi") : "";
                penerimaInfo = rs.getString("penerima_informasi") != null ? rs.getString("penerima_informasi") : "";
                diagnosis = rs.getString("diagnosis") != null ? rs.getString("diagnosis") : "";
                dasarDiagnosis = rs.getString("dasar_diagnosis") != null ? rs.getString("dasar_diagnosis") : "";
                tindakanKedokteran = rs.getString("tindakan_kedokteran") != null ? rs.getString("tindakan_kedokteran") : "";
                indikasi = rs.getString("indikasi") != null ? rs.getString("indikasi") : "";
                tujuan = rs.getString("tujuan") != null ? rs.getString("tujuan") : "";
                resiko = rs.getString("resiko") != null ? rs.getString("resiko") : "";
                komplikasi = rs.getString("komplikasi") != null ? rs.getString("komplikasi") : "";
                prognosis = rs.getString("prognosis") != null ? rs.getString("prognosis") : "";
                alternatif = rs.getString("alternatif") != null ? rs.getString("alternatif") : "";
                lainLain = rs.getString("lain_lain") != null ? rs.getString("lain_lain") : "";
                persetujuan = rs.getString("persetujuan") != null ? rs.getString("persetujuan") : "";
                namaMenyatakan = rs.getString("nama_penyatakan") != null ? rs.getString("nama_penyatakan") : "";
                tglLahirJK = rs.getString("tgl_lahir_jk") != null ? rs.getString("tgl_lahir_jk") : "";
                alamat = rs.getString("alamat") != null ? rs.getString("alamat") : "";
                hubungan = rs.getString("hubungan") != null ? rs.getString("hubungan") : "";
                kdDokAnestesi = rs.getString("kd_dokter_anestesi") != null ? rs.getString("kd_dokter_anestesi") : "";
                nmDokterAnestesiVal = rs.getString("nm_dokter_anestesi") != null ? rs.getString("nm_dokter_anestesi") : "";
                namaSaksi = rs.getString("nama_saksi") != null ? rs.getString("nama_saksi") : "";
            }
        } catch (Exception e) {
            System.out.println("Notif cetak SIA Regional : " + e);
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {}
        }

        // QR Code dokter anestesi
        String qrUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB()
            + "/penggajian/generateqrcode.php?kodedokter=" + kdDokAnestesi.replace(" ", "_");
        String qrImgUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB()
            + "/penggajian/temp/" + kdDokAnestesi + ".png";
        try { new java.net.URL(qrUrl).openStream().close(); } catch (Exception e) {}

        // Logo RS
        String logoBase64 = "";
        try {
            java.io.ByteArrayInputStream logoStream = Sequel.cariGambar("select setting.logo from setting");
            if (logoStream != null) {
                byte[] logoBytes = logoStream.readAllBytes();
                logoBase64 = java.util.Base64.getEncoder().encodeToString(logoBytes);
            }
        } catch (Exception e) {}

        String CK = "&#9745;"; String UC = "&#9744;";

        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
            .append("body{font-family:Tahoma;font-size:9px;margin:15px;color:#000;}")
            .append("table{border-collapse:collapse;}")
            .append(".hdr{width:100%;border-bottom:2px solid #000;padding-bottom:5px;margin-bottom:5px;}")
            .append(".rm-box{border:1px solid #000;padding:5px;text-align:center;font-weight:bold;font-size:8px;width:120px;}")
            .append(".title{text-align:center;font-weight:bold;font-size:10px;margin:8px 0;}")
            .append(".subtitle{text-align:center;font-weight:bold;font-size:9px;margin:3px 0;}")
            .append(".info-line{font-size:8px;margin:2px 0;}")
            .append(".main{width:100%;border:1px solid #000;margin:8px 0;}")
            .append(".main td,.main th{border:1px solid #000;padding:4px 6px;font-size:8px;}")
            .append(".main th{background:#e8e8e8;font-weight:bold;text-align:center;}")
            .append(".pernyataan{font-size:8px;text-align:justify;line-height:1.6;margin:8px 0;padding:5px;border:1px solid #ccc;}")
            .append(".ttd{width:100%;margin-top:20px;}")
            .append(".ttd td{width:33%;text-align:center;vertical-align:top;padding:5px;font-size:8px;}")
            .append(".pi td{padding:2px 5px;font-size:8px;}")
            .append("@media print{body{margin:10px;}}")
            .append("</style></head><body>");

        // Header RS
        html.append("<table class='hdr'><tr>");
        if (!logoBase64.isEmpty()) {
            html.append("<td style='width:60px;vertical-align:middle;'><img src='data:image/jpeg;base64,").append(logoBase64).append("' style='width:55px;height:55px;'/></td>");
        }
        html.append("<td style='text-align:center;'>")
            .append("<div style='font-size:7px;'>DINAS KESEHATAN KABUPATEN BEKASI</div>")
            .append("<div style='font-size:10px;font-weight:bold;'>RUMAH SAKIT UMUM DAERAH (RSUD)</div>")
            .append("<div style='font-size:11px;font-weight:bold;letter-spacing:5px;'>").append(akses.getnamars().toUpperCase().replace("RSUD","").replace("RUMAH SAKIT UMUM DAERAH","").trim()).append("</div>")
            .append("<div style='font-size:7px;'>").append(akses.getalamatrs()).append("</div>")
            .append("<div style='font-size:7px;'>Email : ").append(akses.getemailrs()).append(" Telp : ").append(akses.getkontakrs()).append("</div>")
            .append("</td>")
            .append("<td class='rm-box'>RM.IBS.11.002.2019</td>")
            .append("</tr></table>");

        // Title
        html.append("<div class='title'>PERSETUJUAN TINDAKAN MEDIS ANESTESI</div>");
        html.append("<div class='subtitle'>PEMBERIAN INFORMASI ANESTESI REGIONAL &ndash; SPINAL BLOK SUB ARAKHNOID</div>");

        // Info dokter & penerima
        html.append("<div class='info-line'>* Berilah tanda &#10003; pada pernyataan yang benar</div>");
        html.append("<div class='info-line'><b>Dokter pelaksana tindakan</b> : ").append(nmDokterPelaksanaVal).append("</div>");
        html.append("<div class='info-line'><b>Pemberi Informasi</b> : ").append(nmDokterPemberiVal).append("</div>");
        html.append("<div class='info-line'><b>Penerima Informasi / Pemberi Persetujuan</b> : ").append(penerimaInfo).append("</div>");

        // Tabel informasi
        html.append("<table class='main'>");
        html.append("<tr><th style='width:30px;'>NO</th><th style='width:150px;'>JENIS INFORMASI</th><th>ISI INFORMASI</th><th style='width:50px;'>TANDA<br>(&#10003;)</th></tr>");
        html.append("<tr><td>1</td><td>Diagnosis (WD dan DD)</td><td>").append(diagnosis).append("</td><td style='text-align:center;'>").append(CK).append("</td></tr>");
        html.append("<tr><td>2</td><td>Dasar Diagnosis</td><td>")
            .append(chkCetak(dasarDiagnosis, "Anamnesis")).append(" Anamnesis ")
            .append(chkCetak(dasarDiagnosis, "Pemeriksaan fisik")).append(" Pem. Fisik ")
            .append(chkCetak(dasarDiagnosis, "Pemeriksaan penunjang")).append(" Pem. Penunjang<br>")
            .append("(").append(chkCetak(dasarDiagnosis, "TS Bedah")).append(" TS Bedah ")
            .append(chkCetak(dasarDiagnosis, "Obgyn")).append(" Obgyn ")
            .append(chkCetak(dasarDiagnosis, "Mata")).append(" Mata ")
            .append(chkCetak(dasarDiagnosis, "THT")).append(" THT ")
            .append(chkCetak(dasarDiagnosis, "Penyakit Dalam")).append(" Penyakit Dalam)")
            .append("</td><td style='text-align:center;'>").append(CK).append("</td></tr>");
        html.append("<tr><td>3</td><td>Tindakan Kedokteran</td><td>")
            .append(chkCetak(tindakanKedokteran, "Blok Sub Arakhnoid")).append(" Anestesi Blok Sub Arakhnoid ")
            .append(chkCetak(tindakanKedokteran, "Spinal")).append(" Spinal")
            .append("</td><td style='text-align:center;'>").append(CK).append("</td></tr>");
        html.append("<tr><td>4</td><td>Indikasi Tindakan</td><td>").append(indikasi.isEmpty() ? "Untuk operasi : bawah pusar, analgesi persalinan (painless labour)" : indikasi).append("</td><td style='text-align:center;'>").append(CK).append("</td></tr>");
        html.append("<tr><td>5</td><td>Tujuan</td><td>").append(tujuan.isEmpty() ? "Hambatan impuls nyeri suatu bagian tubuh pada impuls saraf sensorik dan motorik sehingga impuls nyeri dari satu bagian tubuh diblokir untuk sementara" : tujuan).append("</td><td style='text-align:center;'>").append(CK).append("</td></tr>");
        html.append("<tr><td>6</td><td>Resiko Jika Tidak Dilakukan</td><td>").append(resiko).append("</td><td style='text-align:center;'>").append(CK).append("</td></tr>");
        html.append("<tr><td>7</td><td>Komplikasi Tindakan</td><td>")
            .append(chkCetak(komplikasi, "Mual")).append(" Mual ")
            .append(chkCetak(komplikasi, "Kedua kaki berat")).append(" Kedua kaki berat ")
            .append(chkCetak(komplikasi, "Tensi Menurun")).append(" Tensi Menurun ")
            .append(chkCetak(komplikasi, "Nadi Menurun")).append(" Nadi Menurun<br>")
            .append(chkCetak(komplikasi, "Menggigil")).append(" Menggigil ")
            .append(chkCetak(komplikasi, "Pusing")).append(" Pusing ")
            .append(chkCetak(komplikasi, "Sesak Nafas")).append(" Sesak Nafas ")
            .append(chkCetak(komplikasi, "Syok")).append(" Syok")
            .append("</td><td style='text-align:center;'>").append(CK).append("</td></tr>");
        html.append("<tr><td>8</td><td>Prognosis</td><td>")
            .append(chkCetak(prognosis, "Dubia")).append(" Dubia ")
            .append(chkCetak(prognosis, "Ad bonam")).append(" Ad bonam ")
            .append(chkCetak(prognosis, "Malam")).append(" Malam")
            .append("</td><td style='text-align:center;'>").append(CK).append("</td></tr>");
        html.append("<tr><td>9</td><td>Alternatif</td><td>").append(alternatif.isEmpty() ? "Regional Anestesi" : alternatif).append("</td><td style='text-align:center;'>").append(CK).append("</td></tr>");
        html.append("<tr><td>10</td><td>Lain - lain</td><td>").append(lainLain).append("</td><td style='text-align:center;'>").append(CK).append("</td></tr>");
        html.append("</table>");

        // Pernyataan dokter
        html.append("<div class='pernyataan'>");
        html.append("Dengan ini saya menyatakan bahwa saya dokter <b>").append(nmDokterPemberiVal).append("</b> Telah menerangkan hal-hal diatas secara benar dan jelas dan memberikan kesempatan untuk bertanya dan/atau berdiskusi");
        html.append("<div style='text-align:right;margin-top:3px;'>(paraf Dokter)</div>");
        html.append("</div>");

        // Pernyataan pasien/keluarga
        html.append("<div class='pernyataan'>");
        html.append("Dengan ini saya menyatakan bahwa saya /Keluarga pasien <b>").append(penerimaInfo).append("</b> Telah menerima informasi sebagaimana diatas yang saya beri tanda / paraf di kolom kanannya serta telah diberi kesempatan untuk bertanya / berdiskusi, dan telah memahaminya");
        html.append("<div style='text-align:right;margin-top:3px;'>(paraf Pasien/Keluarga)</div>");
        html.append("</div>");

        html.append("<div style='font-size:7px;font-style:italic;margin:5px 0;'>Keterangan : jika pasien tidak kompeten atau tidak mau menerima informasi, maka penerima informasi adalah wali atau keluarga terdekat</div>");

        // PERSETUJUAN / PENOLAKAN
        html.append("<div class='title'>PERSETUJUAN / PENOLAKAN</div>");
        html.append("<div style='font-size:8px;'>Yang bertanda tangan di bawah ini :</div>");

        html.append("<table class='pi' style='margin:5px 0;'>");
        html.append("<tr><td style='width:120px;'>Nama</td><td>: ").append(namaMenyatakan).append("</td></tr>");
        html.append("<tr><td>Tempat/Tgl Lahir / JK</td><td>: ").append(tglLahirJK).append("</td></tr>");
        html.append("<tr><td>Alamat</td><td>: ").append(alamat).append("</td></tr>");
        html.append("</table>");

        html.append("<div style='font-size:8px;text-align:justify;margin:8px 0;'>");
        html.append("Dengan ini menyatakan <b>").append(persetujuan.equalsIgnoreCase("Setuju") ? "PERSETUJUAN" : "PENOLAKAN").append("</b> ");
        html.append("untuk dilakukan tindakan anestesi regional / spinal blok sub arakhnoid terhadap ");
        html.append("saya/").append(hubungan.equalsIgnoreCase("Diri Sendiri") ? "" : hubungan).append(" saya *(coret yang tidak perlu)<br><br>");
        html.append("Saya memahami perlunya dan manfaat tindakan tersebut sebagaimana telah dijelaskan kepada saya termasuk resiko dan komplikasi yang mungkin timbul apabila tindakan tersebut dilaksanakan/tidak dilaksanakan. Saya bertanggung jawab secara penuh atas segala akibat yang mungkin timbul sebagai akibat dilakukan/tidak dilakukannya tindakan tersebut");
        html.append("</div>");

        // Tanggal & Jam
        html.append("<div style='text-align:right;font-size:8px;margin:10px 0;'>Bekasi, ").append(tanggal).append("</div>");

        // Tanda tangan 3 kolom
        html.append("<table class='ttd'><tr>");
        html.append("<td style='height:130px;'>Yang Menyatakan Pasien / Wali<br><div style='height:95px;'></div>( ").append(namaMenyatakan).append(" )</td>");
        html.append("<td style='height:130px;'>Dokter Anestesi<br>");
        html.append("<img src='").append(qrImgUrl).append("' style='width:100px;height:100px;'/><br>");
        html.append("( ").append(nmDokterAnestesiVal).append(" )</td>");
        html.append("<td style='height:130px;'>Saksi<br><div style='height:95px;'></div>( ").append(namaSaksi).append(" )</td>");
        html.append("</tr></table>");

        html.append("<script>window.onload=function(){window.print();}</script>")
            .append("</body></html>");

        try {
            java.io.File tmpFile = java.io.File.createTempFile("sia_regional_", ".html");
            tmpFile.deleteOnExit();
            java.io.FileWriter fw = new java.io.FileWriter(tmpFile);
            fw.write(html.toString());
            fw.close();
            Desktop.getDesktop().browse(tmpFile.toURI());
        } catch (Exception e) {
            System.out.println("Notif buka browser : " + e);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getrujukan_keluar()); BtnHapus.setEnabled(akses.getrujukan_keluar());
        BtnEdit.setEnabled(akses.getrujukan_keluar()); BtnPrint.setEnabled(akses.getrujukan_keluar());
    }
}
