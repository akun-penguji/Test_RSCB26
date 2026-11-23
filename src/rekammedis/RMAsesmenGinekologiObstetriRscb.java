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
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import widget.*;

public class RMAsesmenGinekologiObstetriRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private Label LCount;

    // Header
    private ComboBox CmbInformasiDari, CmbCaraMasuk, CmbAsalMasuk;
    private TextBox TAsalRuangan;

    // Identitas Pasien
    private TextBox TNamaPasien, TPendidikanPasien, TPekerjaanPasien, TAgamaPasien, TSukuPasien, TGoldarPasien;
    // Identitas Suami
    private TextBox TNamaSuami, TPendidikanSuami, TPekerjaanSuami, TAgamaSuami, TSukuSuami, TGoldarSuami;
    // Alamat & Telp
    private TextBox TAlamatRumah, TAlamatKantor, TNoTelp;

    // Keluhan Utama
    private JTextArea TKeluhanUtama;

    // Riwayat Kesehatan
    private ComboBox CmbRiwPenyakitLalu, CmbPernahDirawat, CmbPernahOperasi, CmbMasihPengobatan;
    private ComboBox CmbRiwKeluarga, CmbRiwPekerjaanBahaya, CmbRiwAlergi, CmbKontrasepsi;
    private ComboBox CmbStatusPernikahan, CmbRiwGinekologi;
    private TextBox TRiwPenyakitLaluKet;
    private TextBox TDirawatDiagnosa, TDirawatKapan, TDirawatDi;
    private TextBox TOperasiJenis, TOperasiKapan;
    private TextBox TMasihPengobatanKet;
    private TextBox TRiwKeluargaKet;
    private TextBox TRiwPekerjaanKet;
    private TextBox TAlergiObat, TAlergiMakanan, TAlergiLainnya;
    private TextBox TKontrasepsiJenis, TKontrasepsiLama, TKontrasepsiKeluhan;
    private TextBox TRiwNikah;

    // Riwayat Menstruasi
    private TextBox TMenarche, TSiklusHaid, TLamaHaid, TJumlahDarah, TDismenore, TFlourAlbus;

    // Riwayat Kehamilan
    private TextBox THPHT, TTaksiranPersalinan, TKeluhanMuda, TKeluhanTua, TTTImunisasi, TANC;

    // Riwayat Ginekologi
    private TextBox TRiwGinekologiKet;

    // Pemeriksaan Fisik
    private TextBox TTD, TNadi, TRR, TSuhu, TBB, TTB, TKeadaanUmum, TKesadaran;
    private TextBox TKepala, TMata, THidung, TMulut, TTelinga, TLeher;
    private TextBox TMammaeDx, TMammaeSin, TAxillaDx, TAxillaSin;
    private TextBox TEkstremitasAtas, TEkstremitasBawah;

    // Pemeriksaan Obstetri
    private TextBox TAbdomenInspeksi, TAbdomenPalpasi, TAbdomenAuskultasi;
    private TextBox TTFU, TLetakJanin, TDJJ, THis;

    // Pemeriksaan Ano-Genital
    private TextBox TVulva, TVagina, TPortio, TCorpusUteri, TAdnexa, TCavumDouglas, TPerineum, TAnus;

    // Psikososial & Spiritual
    private TextBox TPsikologis, TSosial, TSpiritual, TBudaya;

    // Risiko Jatuh
    private ComboBox CmbRisikoJatuh;

    // Skrining Nyeri
    private ComboBox CmbNyeri;
    private TextBox TLokasiNyeri, TDurasiNyeri, TFrekuensiNyeri, TSkalaVAS;
    private ComboBox CmbNyeriMenyebar;

    // Status Fungsional
    private TextBox TStatusFungsional, TBarthelIndex;

    // Nutrisi Obstetri
    private ComboBox CmbNutrisiObs1, CmbNutrisiObs2, CmbNutrisiObs3, CmbNutrisiObs4;

    // Nutrisi Ginekologi
    private TextBox TNutrisiGinSkor1, TNutrisiGinSkor2, TNutrisiGinSkor3, TNutrisiGinSkor4;

    // Evaluasi SOAP
    private JTextArea TEvaluasiSOAP;

    // Petugas
    private TextBox KdPetugas, NmPetugas, KdPetugas2, NmPetugas2;
    private Button BtnPetugas, BtnPetugas2;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);
    private DlgCariPetugas petugasDlg2 = new DlgCariPetugas(null, false);

    private widget.Table tbObat;
    private DefaultTableModel tabMode;
    private ScrollPane Scroll;
    private CekBox ChkInput;
    private PanelBiasa FormInput;
    private ScrollPane ScrollInput;
    private JPanel PanelInput;
    public widget.editorpane LoadHTML;

    public RMAsesmenGinekologiObstetriRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(950, 700);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Asesmen Awal Ginekologi & Obstetri Kebidanan (RM.KEP.08.017.A.2019) ]::",
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
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","Keluhan Utama","Asal Masuk","Petugas"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbObat.setModel(tabMode);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { if (tabMode.getRowCount() != 0) getData(); }
        });
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem mnCetak = new JMenuItem("Cetak Asesmen Ginekologi Obstetri");
        mnCetak.setIcon(new ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetak.addActionListener(evt -> BtnPrint.doClick());
        popupMenu.add(mnCetak);
        tbObat.setComponentPopupMenu(popupMenu);
        Scroll.setViewportView(tbObat);

        // FORM INPUT
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(920, 3200));
        FormInput.setLayout(null);

        int y = 10;
        JLabel lblHeader = new JLabel("ASESMEN AWAL GINEKOLOGI & OBSTETRI KEBIDANAN");
        lblHeader.setFont(new java.awt.Font("Tahoma", 1, 13)); lblHeader.setBounds(10, y, 600, 20); FormInput.add(lblHeader);
        JLabel lblKode = new JLabel("RM.KEP.08.017.A.2019");
        lblKode.setFont(new java.awt.Font("Tahoma", 0, 10)); lblKode.setBounds(620, y, 200, 20); FormInput.add(lblKode);

        // ================================================================
        // SECTION 1: HEADER - Informasi, Cara Masuk, Asal Masuk
        // ================================================================
        y += 30;
        JLabel lblSec1 = new JLabel("A. DATA MASUK");
        lblSec1.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec1.setBounds(10, y, 200, 20); FormInput.add(lblSec1);

        y += 25;
        Label lblTgl = new Label(); lblTgl.setText("Tanggal :"); lblTgl.setBounds(10, y, 130, 23); FormInput.add(lblTgl);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setBounds(145, y, 130, 23); FormInput.add(TglAsuhan);

        y += 27;
        Label lblInfoDari = new Label(); lblInfoDari.setText("Informasi dari :"); lblInfoDari.setBounds(10, y, 130, 23); FormInput.add(lblInfoDari);
        CmbInformasiDari = new ComboBox();
        CmbInformasiDari.setModel(new DefaultComboBoxModel(new String[]{"Auto-Anamnesa","Allo-Anamnesa"}));
        CmbInformasiDari.setBounds(145, y, 150, 23); FormInput.add(CmbInformasiDari);

        y += 27;
        Label lblCaraMasuk = new Label(); lblCaraMasuk.setText("Cara Masuk :"); lblCaraMasuk.setBounds(10, y, 130, 23); FormInput.add(lblCaraMasuk);
        CmbCaraMasuk = new ComboBox();
        CmbCaraMasuk.setModel(new DefaultComboBoxModel(new String[]{"Jalan tanpa bantuan","Kursi roda","Brankard","Jalan dengan bantuan"}));
        CmbCaraMasuk.setBounds(145, y, 180, 23); FormInput.add(CmbCaraMasuk);

        y += 27;
        Label lblAsalMasuk = new Label(); lblAsalMasuk.setText("Asal Masuk :"); lblAsalMasuk.setBounds(10, y, 130, 23); FormInput.add(lblAsalMasuk);
        CmbAsalMasuk = new ComboBox();
        CmbAsalMasuk.setModel(new DefaultComboBoxModel(new String[]{"UGD","Poliklinik","Ruangan"}));
        CmbAsalMasuk.setBounds(145, y, 120, 23); FormInput.add(CmbAsalMasuk);
        Label lblAsalRuangan = new Label(); lblAsalRuangan.setText("Ruangan :"); lblAsalRuangan.setBounds(280, y, 65, 23); FormInput.add(lblAsalRuangan);
        TAsalRuangan = new TextBox(); TAsalRuangan.setBounds(350, y, 200, 23); FormInput.add(TAsalRuangan);

        // ================================================================
        // SECTION 2: IDENTITAS PASIEN & SUAMI
        // ================================================================
        y += 35;
        JLabel lblSec2 = new JLabel("B. IDENTITAS PASIEN & SUAMI");
        lblSec2.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec2.setBounds(10, y, 300, 20); FormInput.add(lblSec2);

        // Pasien (left column)
        y += 25;
        JLabel lblPasienHdr = new JLabel("Pasien :"); lblPasienHdr.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblPasienHdr.setBounds(10, y, 100, 20); FormInput.add(lblPasienHdr);
        JLabel lblSuamiHdr = new JLabel("Suami :"); lblSuamiHdr.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblSuamiHdr.setBounds(470, y, 100, 20); FormInput.add(lblSuamiHdr);

        y += 25;
        Label lblNmPas = new Label(); lblNmPas.setText("Nama :"); lblNmPas.setBounds(10, y, 80, 23); FormInput.add(lblNmPas);
        TNamaPasien = new TextBox(); TNamaPasien.setBounds(95, y, 200, 23); FormInput.add(TNamaPasien);
        Label lblNmSuami = new Label(); lblNmSuami.setText("Nama :"); lblNmSuami.setBounds(470, y, 80, 23); FormInput.add(lblNmSuami);
        TNamaSuami = new TextBox(); TNamaSuami.setBounds(555, y, 200, 23); FormInput.add(TNamaSuami);

        y += 27;
        Label lblPendPas = new Label(); lblPendPas.setText("Pendidikan :"); lblPendPas.setBounds(10, y, 80, 23); FormInput.add(lblPendPas);
        TPendidikanPasien = new TextBox(); TPendidikanPasien.setBounds(95, y, 150, 23); FormInput.add(TPendidikanPasien);
        Label lblPendSuami = new Label(); lblPendSuami.setText("Pendidikan :"); lblPendSuami.setBounds(470, y, 80, 23); FormInput.add(lblPendSuami);
        TPendidikanSuami = new TextBox(); TPendidikanSuami.setBounds(555, y, 150, 23); FormInput.add(TPendidikanSuami);

        y += 27;
        Label lblPekPas = new Label(); lblPekPas.setText("Pekerjaan :"); lblPekPas.setBounds(10, y, 80, 23); FormInput.add(lblPekPas);
        TPekerjaanPasien = new TextBox(); TPekerjaanPasien.setBounds(95, y, 150, 23); FormInput.add(TPekerjaanPasien);
        Label lblPekSuami = new Label(); lblPekSuami.setText("Pekerjaan :"); lblPekSuami.setBounds(470, y, 80, 23); FormInput.add(lblPekSuami);
        TPekerjaanSuami = new TextBox(); TPekerjaanSuami.setBounds(555, y, 150, 23); FormInput.add(TPekerjaanSuami);

        y += 27;
        Label lblAgmPas = new Label(); lblAgmPas.setText("Agama :"); lblAgmPas.setBounds(10, y, 80, 23); FormInput.add(lblAgmPas);
        TAgamaPasien = new TextBox(); TAgamaPasien.setBounds(95, y, 150, 23); FormInput.add(TAgamaPasien);
        Label lblAgmSuami = new Label(); lblAgmSuami.setText("Agama :"); lblAgmSuami.setBounds(470, y, 80, 23); FormInput.add(lblAgmSuami);
        TAgamaSuami = new TextBox(); TAgamaSuami.setBounds(555, y, 150, 23); FormInput.add(TAgamaSuami);

        y += 27;
        Label lblSukuPas = new Label(); lblSukuPas.setText("Suku :"); lblSukuPas.setBounds(10, y, 80, 23); FormInput.add(lblSukuPas);
        TSukuPasien = new TextBox(); TSukuPasien.setBounds(95, y, 150, 23); FormInput.add(TSukuPasien);
        Label lblSukuSuami = new Label(); lblSukuSuami.setText("Suku :"); lblSukuSuami.setBounds(470, y, 80, 23); FormInput.add(lblSukuSuami);
        TSukuSuami = new TextBox(); TSukuSuami.setBounds(555, y, 150, 23); FormInput.add(TSukuSuami);

        y += 27;
        Label lblGolPas = new Label(); lblGolPas.setText("Gol. Darah :"); lblGolPas.setBounds(10, y, 80, 23); FormInput.add(lblGolPas);
        TGoldarPasien = new TextBox(); TGoldarPasien.setBounds(95, y, 80, 23); FormInput.add(TGoldarPasien);
        Label lblGolSuami = new Label(); lblGolSuami.setText("Gol. Darah :"); lblGolSuami.setBounds(470, y, 80, 23); FormInput.add(lblGolSuami);
        TGoldarSuami = new TextBox(); TGoldarSuami.setBounds(555, y, 80, 23); FormInput.add(TGoldarSuami);

        y += 27;
        Label lblAlmRumah = new Label(); lblAlmRumah.setText("Alamat Rumah :"); lblAlmRumah.setBounds(10, y, 130, 23); FormInput.add(lblAlmRumah);
        TAlamatRumah = new TextBox(); TAlamatRumah.setBounds(145, y, 400, 23); FormInput.add(TAlamatRumah);

        y += 27;
        Label lblAlmKantor = new Label(); lblAlmKantor.setText("Alamat Kantor :"); lblAlmKantor.setBounds(10, y, 130, 23); FormInput.add(lblAlmKantor);
        TAlamatKantor = new TextBox(); TAlamatKantor.setBounds(145, y, 400, 23); FormInput.add(TAlamatKantor);

        y += 27;
        Label lblTelp = new Label(); lblTelp.setText("No. Telp :"); lblTelp.setBounds(10, y, 130, 23); FormInput.add(lblTelp);
        TNoTelp = new TextBox(); TNoTelp.setBounds(145, y, 150, 23); FormInput.add(TNoTelp);

        // ================================================================
        // SECTION 3: KELUHAN UTAMA
        // ================================================================
        y += 35;
        JLabel lblSec3 = new JLabel("C. KELUHAN UTAMA");
        lblSec3.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec3.setBounds(10, y, 200, 20); FormInput.add(lblSec3);

        y += 25;
        TKeluhanUtama = new JTextArea(); TKeluhanUtama.setLineWrap(true); TKeluhanUtama.setWrapStyleWord(true);
        TKeluhanUtama.setBorder(BorderFactory.createLineBorder(new Color(180,180,180)));
        JScrollPane scrKeluhan = new JScrollPane(TKeluhanUtama);
        scrKeluhan.setBounds(10, y, 750, 60); FormInput.add(scrKeluhan);

        // ================================================================
        // SECTION 4: RIWAYAT KESEHATAN
        // ================================================================
        y += 70;
        JLabel lblSec4 = new JLabel("D. RIWAYAT KESEHATAN");
        lblSec4.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec4.setBounds(10, y, 300, 20); FormInput.add(lblSec4);

        // a. Riwayat Penyakit Lalu
        y += 25;
        Label lblRPL = new Label(); lblRPL.setText("a. Riwayat Penyakit Lalu :"); lblRPL.setBounds(10, y, 170, 23); FormInput.add(lblRPL);
        CmbRiwPenyakitLalu = new ComboBox();
        CmbRiwPenyakitLalu.setModel(new DefaultComboBoxModel(new String[]{"Tidak","Ya"}));
        CmbRiwPenyakitLalu.setBounds(185, y, 70, 23); FormInput.add(CmbRiwPenyakitLalu);
        Label lblRPLKet = new Label(); lblRPLKet.setText("Ket :"); lblRPLKet.setBounds(265, y, 35, 23); FormInput.add(lblRPLKet);
        TRiwPenyakitLaluKet = new TextBox(); TRiwPenyakitLaluKet.setBounds(305, y, 350, 23); FormInput.add(TRiwPenyakitLaluKet);

        // b. Pernah Dirawat
        y += 27;
        Label lblPD = new Label(); lblPD.setText("b. Pernah Dirawat :"); lblPD.setBounds(10, y, 170, 23); FormInput.add(lblPD);
        CmbPernahDirawat = new ComboBox();
        CmbPernahDirawat.setModel(new DefaultComboBoxModel(new String[]{"Tidak","Ya"}));
        CmbPernahDirawat.setBounds(185, y, 70, 23); FormInput.add(CmbPernahDirawat);
        Label lblPDDiag = new Label(); lblPDDiag.setText("Diagnosa :"); lblPDDiag.setBounds(265, y, 65, 23); FormInput.add(lblPDDiag);
        TDirawatDiagnosa = new TextBox(); TDirawatDiagnosa.setBounds(335, y, 150, 23); FormInput.add(TDirawatDiagnosa);
        y += 27;
        Label lblPDKapan = new Label(); lblPDKapan.setText("   Kapan :"); lblPDKapan.setBounds(265, y, 65, 23); FormInput.add(lblPDKapan);
        TDirawatKapan = new TextBox(); TDirawatKapan.setBounds(335, y, 120, 23); FormInput.add(TDirawatKapan);
        Label lblPDDi = new Label(); lblPDDi.setText("Di :"); lblPDDi.setBounds(465, y, 25, 23); FormInput.add(lblPDDi);
        TDirawatDi = new TextBox(); TDirawatDi.setBounds(495, y, 160, 23); FormInput.add(TDirawatDi);

        // c. Pernah Operasi
        y += 27;
        Label lblPO = new Label(); lblPO.setText("c. Pernah Operasi :"); lblPO.setBounds(10, y, 170, 23); FormInput.add(lblPO);
        CmbPernahOperasi = new ComboBox();
        CmbPernahOperasi.setModel(new DefaultComboBoxModel(new String[]{"Tidak","Ya"}));
        CmbPernahOperasi.setBounds(185, y, 70, 23); FormInput.add(CmbPernahOperasi);
        Label lblPOJenis = new Label(); lblPOJenis.setText("Jenis :"); lblPOJenis.setBounds(265, y, 45, 23); FormInput.add(lblPOJenis);
        TOperasiJenis = new TextBox(); TOperasiJenis.setBounds(315, y, 170, 23); FormInput.add(TOperasiJenis);
        Label lblPOKapan = new Label(); lblPOKapan.setText("Kapan :"); lblPOKapan.setBounds(495, y, 45, 23); FormInput.add(lblPOKapan);
        TOperasiKapan = new TextBox(); TOperasiKapan.setBounds(545, y, 110, 23); FormInput.add(TOperasiKapan);

        // d. Masih dalam pengobatan
        y += 27;
        Label lblMP = new Label(); lblMP.setText("d. Masih Pengobatan :"); lblMP.setBounds(10, y, 170, 23); FormInput.add(lblMP);
        CmbMasihPengobatan = new ComboBox();
        CmbMasihPengobatan.setModel(new DefaultComboBoxModel(new String[]{"Tidak","Ya"}));
        CmbMasihPengobatan.setBounds(185, y, 70, 23); FormInput.add(CmbMasihPengobatan);
        Label lblMPKet = new Label(); lblMPKet.setText("Ket :"); lblMPKet.setBounds(265, y, 35, 23); FormInput.add(lblMPKet);
        TMasihPengobatanKet = new TextBox(); TMasihPengobatanKet.setBounds(305, y, 350, 23); FormInput.add(TMasihPengobatanKet);

        // e. Riwayat Penyakit Keluarga
        y += 27;
        Label lblRK = new Label(); lblRK.setText("e. Riwayat Kes. Keluarga :"); lblRK.setBounds(10, y, 170, 23); FormInput.add(lblRK);
        CmbRiwKeluarga = new ComboBox();
        CmbRiwKeluarga.setModel(new DefaultComboBoxModel(new String[]{"Tidak","Ya"}));
        CmbRiwKeluarga.setBounds(185, y, 70, 23); FormInput.add(CmbRiwKeluarga);
        Label lblRKKet = new Label(); lblRKKet.setText("Ket :"); lblRKKet.setBounds(265, y, 35, 23); FormInput.add(lblRKKet);
        TRiwKeluargaKet = new TextBox(); TRiwKeluargaKet.setBounds(305, y, 350, 23); FormInput.add(TRiwKeluargaKet);

        // f. Riwayat Pekerjaan Bahaya
        y += 27;
        Label lblRPB = new Label(); lblRPB.setText("f. Riw. Pekerjaan Bahaya :"); lblRPB.setBounds(10, y, 170, 23); FormInput.add(lblRPB);
        CmbRiwPekerjaanBahaya = new ComboBox();
        CmbRiwPekerjaanBahaya.setModel(new DefaultComboBoxModel(new String[]{"Tidak","Ya"}));
        CmbRiwPekerjaanBahaya.setBounds(185, y, 70, 23); FormInput.add(CmbRiwPekerjaanBahaya);
        Label lblRPBKet = new Label(); lblRPBKet.setText("Ket :"); lblRPBKet.setBounds(265, y, 35, 23); FormInput.add(lblRPBKet);
        TRiwPekerjaanKet = new TextBox(); TRiwPekerjaanKet.setBounds(305, y, 350, 23); FormInput.add(TRiwPekerjaanKet);

        // g. Riwayat Alergi
        y += 27;
        Label lblRA = new Label(); lblRA.setText("g. Riwayat Alergi :"); lblRA.setBounds(10, y, 170, 23); FormInput.add(lblRA);
        CmbRiwAlergi = new ComboBox();
        CmbRiwAlergi.setModel(new DefaultComboBoxModel(new String[]{"Tidak","Ya"}));
        CmbRiwAlergi.setBounds(185, y, 70, 23); FormInput.add(CmbRiwAlergi);
        Label lblRAObat = new Label(); lblRAObat.setText("Obat :"); lblRAObat.setBounds(265, y, 40, 23); FormInput.add(lblRAObat);
        TAlergiObat = new TextBox(); TAlergiObat.setBounds(310, y, 130, 23); FormInput.add(TAlergiObat);
        Label lblRAMkn = new Label(); lblRAMkn.setText("Makanan :"); lblRAMkn.setBounds(450, y, 60, 23); FormInput.add(lblRAMkn);
        TAlergiMakanan = new TextBox(); TAlergiMakanan.setBounds(515, y, 100, 23); FormInput.add(TAlergiMakanan);
        Label lblRALain = new Label(); lblRALain.setText("Lainnya :"); lblRALain.setBounds(625, y, 55, 23); FormInput.add(lblRALain);
        TAlergiLainnya = new TextBox(); TAlergiLainnya.setBounds(685, y, 100, 23); FormInput.add(TAlergiLainnya);

        // h. Kontrasepsi
        y += 27;
        Label lblKontra = new Label(); lblKontra.setText("h. Kontrasepsi :"); lblKontra.setBounds(10, y, 170, 23); FormInput.add(lblKontra);
        CmbKontrasepsi = new ComboBox();
        CmbKontrasepsi.setModel(new DefaultComboBoxModel(new String[]{"Tidak","Ya"}));
        CmbKontrasepsi.setBounds(185, y, 70, 23); FormInput.add(CmbKontrasepsi);
        Label lblKonJenis = new Label(); lblKonJenis.setText("Jenis :"); lblKonJenis.setBounds(265, y, 40, 23); FormInput.add(lblKonJenis);
        TKontrasepsiJenis = new TextBox(); TKontrasepsiJenis.setBounds(310, y, 110, 23); FormInput.add(TKontrasepsiJenis);
        Label lblKonLama = new Label(); lblKonLama.setText("Lama :"); lblKonLama.setBounds(430, y, 40, 23); FormInput.add(lblKonLama);
        TKontrasepsiLama = new TextBox(); TKontrasepsiLama.setBounds(475, y, 80, 23); FormInput.add(TKontrasepsiLama);
        Label lblKonKel = new Label(); lblKonKel.setText("Keluhan :"); lblKonKel.setBounds(565, y, 55, 23); FormInput.add(lblKonKel);
        TKontrasepsiKeluhan = new TextBox(); TKontrasepsiKeluhan.setBounds(625, y, 130, 23); FormInput.add(TKontrasepsiKeluhan);

        // i. Status Pernikahan
        y += 27;
        Label lblSP = new Label(); lblSP.setText("i. Status Pernikahan :"); lblSP.setBounds(10, y, 170, 23); FormInput.add(lblSP);
        CmbStatusPernikahan = new ComboBox();
        CmbStatusPernikahan.setModel(new DefaultComboBoxModel(new String[]{"Belum Menikah","Menikah","Janda","Duda"}));
        CmbStatusPernikahan.setBounds(185, y, 130, 23); FormInput.add(CmbStatusPernikahan);
        Label lblSPRiw = new Label(); lblSPRiw.setText("Riw. Nikah :"); lblSPRiw.setBounds(325, y, 70, 23); FormInput.add(lblSPRiw);
        TRiwNikah = new TextBox(); TRiwNikah.setBounds(400, y, 250, 23); FormInput.add(TRiwNikah);

        // Riwayat Menstruasi
        y += 35;
        JLabel lblMens = new JLabel("Riwayat Menstruasi :");
        lblMens.setFont(new java.awt.Font("Tahoma", 2, 11)); lblMens.setBounds(10, y, 200, 20); FormInput.add(lblMens);

        y += 25;
        Label lblMenarche = new Label(); lblMenarche.setText("Menarche :"); lblMenarche.setBounds(10, y, 80, 23); FormInput.add(lblMenarche);
        TMenarche = new TextBox(); TMenarche.setBounds(95, y, 80, 23); FormInput.add(TMenarche);
        Label lblSiklus = new Label(); lblSiklus.setText("Siklus :"); lblSiklus.setBounds(185, y, 50, 23); FormInput.add(lblSiklus);
        TSiklusHaid = new TextBox(); TSiklusHaid.setBounds(240, y, 80, 23); FormInput.add(TSiklusHaid);
        Label lblLamaHaid = new Label(); lblLamaHaid.setText("Lama :"); lblLamaHaid.setBounds(330, y, 40, 23); FormInput.add(lblLamaHaid);
        TLamaHaid = new TextBox(); TLamaHaid.setBounds(375, y, 60, 23); FormInput.add(TLamaHaid);

        y += 27;
        Label lblJmlDarah = new Label(); lblJmlDarah.setText("Jumlah Darah :"); lblJmlDarah.setBounds(10, y, 100, 23); FormInput.add(lblJmlDarah);
        TJumlahDarah = new TextBox(); TJumlahDarah.setBounds(115, y, 100, 23); FormInput.add(TJumlahDarah);
        Label lblDismenore = new Label(); lblDismenore.setText("Dismenore :"); lblDismenore.setBounds(225, y, 70, 23); FormInput.add(lblDismenore);
        TDismenore = new TextBox(); TDismenore.setBounds(300, y, 100, 23); FormInput.add(TDismenore);
        Label lblFlourAlbus = new Label(); lblFlourAlbus.setText("Flour Albus :"); lblFlourAlbus.setBounds(410, y, 75, 23); FormInput.add(lblFlourAlbus);
        TFlourAlbus = new TextBox(); TFlourAlbus.setBounds(490, y, 100, 23); FormInput.add(TFlourAlbus);

        // Riwayat Kehamilan
        y += 35;
        JLabel lblHamil = new JLabel("Riwayat Kehamilan :");
        lblHamil.setFont(new java.awt.Font("Tahoma", 2, 11)); lblHamil.setBounds(10, y, 200, 20); FormInput.add(lblHamil);

        y += 25;
        Label lblHPHT = new Label(); lblHPHT.setText("HPHT :"); lblHPHT.setBounds(10, y, 80, 23); FormInput.add(lblHPHT);
        THPHT = new TextBox(); THPHT.setBounds(95, y, 100, 23); FormInput.add(THPHT);
        Label lblTaksiran = new Label(); lblTaksiran.setText("Taksiran :"); lblTaksiran.setBounds(205, y, 60, 23); FormInput.add(lblTaksiran);
        TTaksiranPersalinan = new TextBox(); TTaksiranPersalinan.setBounds(270, y, 100, 23); FormInput.add(TTaksiranPersalinan);

        y += 27;
        Label lblKelMuda = new Label(); lblKelMuda.setText("Keluhan Muda :"); lblKelMuda.setBounds(10, y, 100, 23); FormInput.add(lblKelMuda);
        TKeluhanMuda = new TextBox(); TKeluhanMuda.setBounds(115, y, 200, 23); FormInput.add(TKeluhanMuda);
        Label lblKelTua = new Label(); lblKelTua.setText("Keluhan Tua :"); lblKelTua.setBounds(325, y, 85, 23); FormInput.add(lblKelTua);
        TKeluhanTua = new TextBox(); TKeluhanTua.setBounds(415, y, 200, 23); FormInput.add(TKeluhanTua);

        y += 27;
        Label lblTT = new Label(); lblTT.setText("TT Imunisasi :"); lblTT.setBounds(10, y, 100, 23); FormInput.add(lblTT);
        TTTImunisasi = new TextBox(); TTTImunisasi.setBounds(115, y, 100, 23); FormInput.add(TTTImunisasi);
        Label lblANC = new Label(); lblANC.setText("ANC :"); lblANC.setBounds(225, y, 40, 23); FormInput.add(lblANC);
        TANC = new TextBox(); TANC.setBounds(270, y, 200, 23); FormInput.add(TANC);

        // Riwayat Ginekologi
        y += 30;
        Label lblRG = new Label(); lblRG.setText("Riwayat Ginekologi :"); lblRG.setBounds(10, y, 170, 23); FormInput.add(lblRG);
        CmbRiwGinekologi = new ComboBox();
        CmbRiwGinekologi.setModel(new DefaultComboBoxModel(new String[]{"Tidak","Ya"}));
        CmbRiwGinekologi.setBounds(185, y, 70, 23); FormInput.add(CmbRiwGinekologi);
        Label lblRGKet = new Label(); lblRGKet.setText("Ket :"); lblRGKet.setBounds(265, y, 35, 23); FormInput.add(lblRGKet);
        TRiwGinekologiKet = new TextBox(); TRiwGinekologiKet.setBounds(305, y, 350, 23); FormInput.add(TRiwGinekologiKet);

        // ================================================================
        // SECTION 5: PEMERIKSAAN FISIK
        // ================================================================
        y += 35;
        JLabel lblSec5 = new JLabel("E. PEMERIKSAAN FISIK");
        lblSec5.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec5.setBounds(10, y, 300, 20); FormInput.add(lblSec5);

        y += 25;
        Label lblKU = new Label(); lblKU.setText("Keadaan Umum :"); lblKU.setBounds(10, y, 130, 23); FormInput.add(lblKU);
        TKeadaanUmum = new TextBox(); TKeadaanUmum.setBounds(145, y, 150, 23); FormInput.add(TKeadaanUmum);
        Label lblKesadaran = new Label(); lblKesadaran.setText("Kesadaran :"); lblKesadaran.setBounds(310, y, 75, 23); FormInput.add(lblKesadaran);
        TKesadaran = new TextBox(); TKesadaran.setBounds(390, y, 150, 23); FormInput.add(TKesadaran);

        y += 27;
        Label lblTDFisik = new Label(); lblTDFisik.setText("TD (mmHg) :"); lblTDFisik.setBounds(10, y, 130, 23); FormInput.add(lblTDFisik);
        TTD = new TextBox(); TTD.setBounds(145, y, 80, 23); FormInput.add(TTD);
        Label lblNadi = new Label(); lblNadi.setText("Nadi :"); lblNadi.setBounds(235, y, 40, 23); FormInput.add(lblNadi);
        TNadi = new TextBox(); TNadi.setBounds(280, y, 60, 23); FormInput.add(TNadi);
        Label lblRR = new Label(); lblRR.setText("RR :"); lblRR.setBounds(350, y, 30, 23); FormInput.add(lblRR);
        TRR = new TextBox(); TRR.setBounds(385, y, 50, 23); FormInput.add(TRR);
        Label lblSuhu = new Label(); lblSuhu.setText("Suhu :"); lblSuhu.setBounds(445, y, 40, 23); FormInput.add(lblSuhu);
        TSuhu = new TextBox(); TSuhu.setBounds(490, y, 50, 23); FormInput.add(TSuhu);

        y += 27;
        Label lblBBFisik = new Label(); lblBBFisik.setText("BB (kg) :"); lblBBFisik.setBounds(10, y, 130, 23); FormInput.add(lblBBFisik);
        TBB = new TextBox(); TBB.setBounds(145, y, 60, 23); FormInput.add(TBB);
        Label lblTBFisik = new Label(); lblTBFisik.setText("TB (cm) :"); lblTBFisik.setBounds(220, y, 55, 23); FormInput.add(lblTBFisik);
        TTB = new TextBox(); TTB.setBounds(280, y, 60, 23); FormInput.add(TTB);

        y += 27;
        Label lblKepala = new Label(); lblKepala.setText("Kepala :"); lblKepala.setBounds(10, y, 130, 23); FormInput.add(lblKepala);
        TKepala = new TextBox(); TKepala.setBounds(145, y, 350, 23); FormInput.add(TKepala);

        y += 27;
        Label lblMata = new Label(); lblMata.setText("Mata :"); lblMata.setBounds(10, y, 130, 23); FormInput.add(lblMata);
        TMata = new TextBox(); TMata.setBounds(145, y, 200, 23); FormInput.add(TMata);
        Label lblHidung = new Label(); lblHidung.setText("Hidung :"); lblHidung.setBounds(355, y, 50, 23); FormInput.add(lblHidung);
        THidung = new TextBox(); THidung.setBounds(410, y, 200, 23); FormInput.add(THidung);

        y += 27;
        Label lblMulut = new Label(); lblMulut.setText("Mulut :"); lblMulut.setBounds(10, y, 130, 23); FormInput.add(lblMulut);
        TMulut = new TextBox(); TMulut.setBounds(145, y, 200, 23); FormInput.add(TMulut);
        Label lblTelinga = new Label(); lblTelinga.setText("Telinga :"); lblTelinga.setBounds(355, y, 50, 23); FormInput.add(lblTelinga);
        TTelinga = new TextBox(); TTelinga.setBounds(410, y, 200, 23); FormInput.add(TTelinga);

        y += 27;
        Label lblLeher = new Label(); lblLeher.setText("Leher :"); lblLeher.setBounds(10, y, 130, 23); FormInput.add(lblLeher);
        TLeher = new TextBox(); TLeher.setBounds(145, y, 350, 23); FormInput.add(TLeher);

        y += 27;
        Label lblMamDx = new Label(); lblMamDx.setText("Mammae Dx :"); lblMamDx.setBounds(10, y, 130, 23); FormInput.add(lblMamDx);
        TMammaeDx = new TextBox(); TMammaeDx.setBounds(145, y, 200, 23); FormInput.add(TMammaeDx);
        Label lblMamSin = new Label(); lblMamSin.setText("Sin :"); lblMamSin.setBounds(355, y, 30, 23); FormInput.add(lblMamSin);
        TMammaeSin = new TextBox(); TMammaeSin.setBounds(390, y, 200, 23); FormInput.add(TMammaeSin);

        y += 27;
        Label lblAxDx = new Label(); lblAxDx.setText("Axilla Dx :"); lblAxDx.setBounds(10, y, 130, 23); FormInput.add(lblAxDx);
        TAxillaDx = new TextBox(); TAxillaDx.setBounds(145, y, 200, 23); FormInput.add(TAxillaDx);
        Label lblAxSin = new Label(); lblAxSin.setText("Sin :"); lblAxSin.setBounds(355, y, 30, 23); FormInput.add(lblAxSin);
        TAxillaSin = new TextBox(); TAxillaSin.setBounds(390, y, 200, 23); FormInput.add(TAxillaSin);

        y += 27;
        Label lblEksAtas = new Label(); lblEksAtas.setText("Ekstremitas Atas :"); lblEksAtas.setBounds(10, y, 130, 23); FormInput.add(lblEksAtas);
        TEkstremitasAtas = new TextBox(); TEkstremitasAtas.setBounds(145, y, 200, 23); FormInput.add(TEkstremitasAtas);
        Label lblEksBawah = new Label(); lblEksBawah.setText("Bawah :"); lblEksBawah.setBounds(355, y, 50, 23); FormInput.add(lblEksBawah);
        TEkstremitasBawah = new TextBox(); TEkstremitasBawah.setBounds(410, y, 200, 23); FormInput.add(TEkstremitasBawah);

        // ================================================================
        // SECTION 6: PEMERIKSAAN KHUSUS OBSTETRI
        // ================================================================
        y += 35;
        JLabel lblSec6 = new JLabel("F. PEMERIKSAAN KHUSUS OBSTETRI");
        lblSec6.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec6.setBounds(10, y, 350, 20); FormInput.add(lblSec6);

        y += 25;
        Label lblAbdInsp = new Label(); lblAbdInsp.setText("Abdomen Inspeksi :"); lblAbdInsp.setBounds(10, y, 130, 23); FormInput.add(lblAbdInsp);
        TAbdomenInspeksi = new TextBox(); TAbdomenInspeksi.setBounds(145, y, 400, 23); FormInput.add(TAbdomenInspeksi);

        y += 27;
        Label lblAbdPalp = new Label(); lblAbdPalp.setText("Abdomen Palpasi :"); lblAbdPalp.setBounds(10, y, 130, 23); FormInput.add(lblAbdPalp);
        TAbdomenPalpasi = new TextBox(); TAbdomenPalpasi.setBounds(145, y, 400, 23); FormInput.add(TAbdomenPalpasi);

        y += 27;
        Label lblAbdAusk = new Label(); lblAbdAusk.setText("Abdomen Auskultasi :"); lblAbdAusk.setBounds(10, y, 135, 23); FormInput.add(lblAbdAusk);
        TAbdomenAuskultasi = new TextBox(); TAbdomenAuskultasi.setBounds(145, y, 400, 23); FormInput.add(TAbdomenAuskultasi);

        y += 27;
        Label lblTFU = new Label(); lblTFU.setText("TFU :"); lblTFU.setBounds(10, y, 130, 23); FormInput.add(lblTFU);
        TTFU = new TextBox(); TTFU.setBounds(145, y, 100, 23); FormInput.add(TTFU);
        Label lblLetakJanin = new Label(); lblLetakJanin.setText("Letak Janin :"); lblLetakJanin.setBounds(260, y, 80, 23); FormInput.add(lblLetakJanin);
        TLetakJanin = new TextBox(); TLetakJanin.setBounds(345, y, 150, 23); FormInput.add(TLetakJanin);

        y += 27;
        Label lblDJJ = new Label(); lblDJJ.setText("DJJ :"); lblDJJ.setBounds(10, y, 130, 23); FormInput.add(lblDJJ);
        TDJJ = new TextBox(); TDJJ.setBounds(145, y, 100, 23); FormInput.add(TDJJ);
        Label lblHis = new Label(); lblHis.setText("His :"); lblHis.setBounds(260, y, 80, 23); FormInput.add(lblHis);
        THis = new TextBox(); THis.setBounds(345, y, 150, 23); FormInput.add(THis);

        // Ano-Genital
        y += 35;
        JLabel lblAnoGen = new JLabel("Pemeriksaan Ano-Genital :");
        lblAnoGen.setFont(new java.awt.Font("Tahoma", 2, 11)); lblAnoGen.setBounds(10, y, 250, 20); FormInput.add(lblAnoGen);

        y += 25;
        Label lblVulva = new Label(); lblVulva.setText("Vulva :"); lblVulva.setBounds(10, y, 80, 23); FormInput.add(lblVulva);
        TVulva = new TextBox(); TVulva.setBounds(95, y, 170, 23); FormInput.add(TVulva);
        Label lblVagina = new Label(); lblVagina.setText("Vagina :"); lblVagina.setBounds(275, y, 55, 23); FormInput.add(lblVagina);
        TVagina = new TextBox(); TVagina.setBounds(335, y, 170, 23); FormInput.add(TVagina);

        y += 27;
        Label lblPortio = new Label(); lblPortio.setText("Portio :"); lblPortio.setBounds(10, y, 80, 23); FormInput.add(lblPortio);
        TPortio = new TextBox(); TPortio.setBounds(95, y, 170, 23); FormInput.add(TPortio);
        Label lblCorpus = new Label(); lblCorpus.setText("Corpus Uteri :"); lblCorpus.setBounds(275, y, 85, 23); FormInput.add(lblCorpus);
        TCorpusUteri = new TextBox(); TCorpusUteri.setBounds(365, y, 170, 23); FormInput.add(TCorpusUteri);

        y += 27;
        Label lblAdnexa = new Label(); lblAdnexa.setText("Adnexa :"); lblAdnexa.setBounds(10, y, 80, 23); FormInput.add(lblAdnexa);
        TAdnexa = new TextBox(); TAdnexa.setBounds(95, y, 170, 23); FormInput.add(TAdnexa);
        Label lblCavum = new Label(); lblCavum.setText("Cavum Douglas :"); lblCavum.setBounds(275, y, 100, 23); FormInput.add(lblCavum);
        TCavumDouglas = new TextBox(); TCavumDouglas.setBounds(380, y, 170, 23); FormInput.add(TCavumDouglas);

        y += 27;
        Label lblPerineum = new Label(); lblPerineum.setText("Perineum :"); lblPerineum.setBounds(10, y, 80, 23); FormInput.add(lblPerineum);
        TPerineum = new TextBox(); TPerineum.setBounds(95, y, 170, 23); FormInput.add(TPerineum);
        Label lblAnus = new Label(); lblAnus.setText("Anus :"); lblAnus.setBounds(275, y, 55, 23); FormInput.add(lblAnus);
        TAnus = new TextBox(); TAnus.setBounds(335, y, 170, 23); FormInput.add(TAnus);

        // ================================================================
        // SECTION 7: PSIKOSOSIAL & SPIRITUAL
        // ================================================================
        y += 35;
        JLabel lblSec7 = new JLabel("G. PSIKOSOSIAL & SPIRITUAL");
        lblSec7.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec7.setBounds(10, y, 300, 20); FormInput.add(lblSec7);

        y += 25;
        Label lblPsiko = new Label(); lblPsiko.setText("Psikologis :"); lblPsiko.setBounds(10, y, 130, 23); FormInput.add(lblPsiko);
        TPsikologis = new TextBox(); TPsikologis.setBounds(145, y, 350, 23); FormInput.add(TPsikologis);

        y += 27;
        Label lblSosial = new Label(); lblSosial.setText("Sosial :"); lblSosial.setBounds(10, y, 130, 23); FormInput.add(lblSosial);
        TSosial = new TextBox(); TSosial.setBounds(145, y, 350, 23); FormInput.add(TSosial);

        y += 27;
        Label lblSpiritual = new Label(); lblSpiritual.setText("Spiritual :"); lblSpiritual.setBounds(10, y, 130, 23); FormInput.add(lblSpiritual);
        TSpiritual = new TextBox(); TSpiritual.setBounds(145, y, 350, 23); FormInput.add(TSpiritual);

        y += 27;
        Label lblBudaya = new Label(); lblBudaya.setText("Budaya :"); lblBudaya.setBounds(10, y, 130, 23); FormInput.add(lblBudaya);
        TBudaya = new TextBox(); TBudaya.setBounds(145, y, 350, 23); FormInput.add(TBudaya);

        // ================================================================
        // SECTION 8: RISIKO JATUH & SKRINING NYERI
        // ================================================================
        y += 35;
        JLabel lblSec8 = new JLabel("H. RISIKO JATUH & SKRINING NYERI");
        lblSec8.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec8.setBounds(10, y, 350, 20); FormInput.add(lblSec8);

        y += 25;
        Label lblRisJatuh = new Label(); lblRisJatuh.setText("Risiko Jatuh :"); lblRisJatuh.setBounds(10, y, 130, 23); FormInput.add(lblRisJatuh);
        CmbRisikoJatuh = new ComboBox();
        CmbRisikoJatuh.setModel(new DefaultComboBoxModel(new String[]{"Tidak","Ya"}));
        CmbRisikoJatuh.setBounds(145, y, 70, 23); FormInput.add(CmbRisikoJatuh);

        y += 30;
        JLabel lblNyeriHdr = new JLabel("Skrining Nyeri :");
        lblNyeriHdr.setFont(new java.awt.Font("Tahoma", 2, 11)); lblNyeriHdr.setBounds(10, y, 200, 20); FormInput.add(lblNyeriHdr);

        y += 25;
        Label lblNyeriAda = new Label(); lblNyeriAda.setText("Nyeri :"); lblNyeriAda.setBounds(10, y, 130, 23); FormInput.add(lblNyeriAda);
        CmbNyeri = new ComboBox();
        CmbNyeri.setModel(new DefaultComboBoxModel(new String[]{"Tidak","Ya"}));
        CmbNyeri.setBounds(145, y, 70, 23); FormInput.add(CmbNyeri);

        y += 27;
        Label lblLokNyeri = new Label(); lblLokNyeri.setText("Lokasi Nyeri :"); lblLokNyeri.setBounds(10, y, 130, 23); FormInput.add(lblLokNyeri);
        TLokasiNyeri = new TextBox(); TLokasiNyeri.setBounds(145, y, 200, 23); FormInput.add(TLokasiNyeri);
        Label lblDurNyeri = new Label(); lblDurNyeri.setText("Durasi :"); lblDurNyeri.setBounds(355, y, 50, 23); FormInput.add(lblDurNyeri);
        TDurasiNyeri = new TextBox(); TDurasiNyeri.setBounds(410, y, 100, 23); FormInput.add(TDurasiNyeri);

        y += 27;
        Label lblFrekNyeri = new Label(); lblFrekNyeri.setText("Frekuensi :"); lblFrekNyeri.setBounds(10, y, 130, 23); FormInput.add(lblFrekNyeri);
        TFrekuensiNyeri = new TextBox(); TFrekuensiNyeri.setBounds(145, y, 100, 23); FormInput.add(TFrekuensiNyeri);
        Label lblSkalaVAS = new Label(); lblSkalaVAS.setText("Skala VAS :"); lblSkalaVAS.setBounds(255, y, 75, 23); FormInput.add(lblSkalaVAS);
        TSkalaVAS = new TextBox(); TSkalaVAS.setBounds(335, y, 60, 23); FormInput.add(TSkalaVAS);
        Label lblMenyebar = new Label(); lblMenyebar.setText("Menyebar :"); lblMenyebar.setBounds(405, y, 70, 23); FormInput.add(lblMenyebar);
        CmbNyeriMenyebar = new ComboBox();
        CmbNyeriMenyebar.setModel(new DefaultComboBoxModel(new String[]{"Tidak","Ya"}));
        CmbNyeriMenyebar.setBounds(480, y, 70, 23); FormInput.add(CmbNyeriMenyebar);

        // ================================================================
        // SECTION 9: STATUS FUNGSIONAL
        // ================================================================
        y += 35;
        JLabel lblSec9 = new JLabel("I. STATUS FUNGSIONAL");
        lblSec9.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec9.setBounds(10, y, 300, 20); FormInput.add(lblSec9);

        y += 25;
        Label lblStatFunc = new Label(); lblStatFunc.setText("Status Fungsional :"); lblStatFunc.setBounds(10, y, 130, 23); FormInput.add(lblStatFunc);
        TStatusFungsional = new TextBox(); TStatusFungsional.setBounds(145, y, 300, 23); FormInput.add(TStatusFungsional);

        y += 27;
        Label lblBarthel = new Label(); lblBarthel.setText("Barthel Index :"); lblBarthel.setBounds(10, y, 130, 23); FormInput.add(lblBarthel);
        TBarthelIndex = new TextBox(); TBarthelIndex.setBounds(145, y, 100, 23); FormInput.add(TBarthelIndex);

        // ================================================================
        // SECTION 10: NUTRISI
        // ================================================================
        y += 35;
        JLabel lblSec10 = new JLabel("J. SKRINING NUTRISI");
        lblSec10.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec10.setBounds(10, y, 300, 20); FormInput.add(lblSec10);

        // Nutrisi Obstetri
        y += 25;
        JLabel lblNutrObs = new JLabel("Nutrisi Obstetri :");
        lblNutrObs.setFont(new java.awt.Font("Tahoma", 2, 11)); lblNutrObs.setBounds(10, y, 200, 20); FormInput.add(lblNutrObs);

        y += 25;
        Label lblNO1 = new Label(); lblNO1.setText("Mual/Muntah :"); lblNO1.setBounds(10, y, 200, 23); FormInput.add(lblNO1);
        CmbNutrisiObs1 = new ComboBox();
        CmbNutrisiObs1.setModel(new DefaultComboBoxModel(new String[]{"Tidak","Ya"}));
        CmbNutrisiObs1.setBounds(215, y, 70, 23); FormInput.add(CmbNutrisiObs1);

        y += 27;
        Label lblNO2 = new Label(); lblNO2.setText("Nafsu makan menurun :"); lblNO2.setBounds(10, y, 200, 23); FormInput.add(lblNO2);
        CmbNutrisiObs2 = new ComboBox();
        CmbNutrisiObs2.setModel(new DefaultComboBoxModel(new String[]{"Tidak","Ya"}));
        CmbNutrisiObs2.setBounds(215, y, 70, 23); FormInput.add(CmbNutrisiObs2);

        y += 27;
        Label lblNO3 = new Label(); lblNO3.setText("BB turun dlm 6 bln terakhir :"); lblNO3.setBounds(10, y, 200, 23); FormInput.add(lblNO3);
        CmbNutrisiObs3 = new ComboBox();
        CmbNutrisiObs3.setModel(new DefaultComboBoxModel(new String[]{"Tidak","Ya"}));
        CmbNutrisiObs3.setBounds(215, y, 70, 23); FormInput.add(CmbNutrisiObs3);

        y += 27;
        Label lblNO4 = new Label(); lblNO4.setText("Kondisi khusus (DM, dll) :"); lblNO4.setBounds(10, y, 200, 23); FormInput.add(lblNO4);
        CmbNutrisiObs4 = new ComboBox();
        CmbNutrisiObs4.setModel(new DefaultComboBoxModel(new String[]{"Tidak","Ya"}));
        CmbNutrisiObs4.setBounds(215, y, 70, 23); FormInput.add(CmbNutrisiObs4);

        // Nutrisi Ginekologi
        y += 35;
        JLabel lblNutrGin = new JLabel("Nutrisi Ginekologi (Skor) :");
        lblNutrGin.setFont(new java.awt.Font("Tahoma", 2, 11)); lblNutrGin.setBounds(10, y, 250, 20); FormInput.add(lblNutrGin);

        y += 25;
        Label lblNG1 = new Label(); lblNG1.setText("Penurunan BB (0-2) :"); lblNG1.setBounds(10, y, 180, 23); FormInput.add(lblNG1);
        TNutrisiGinSkor1 = new TextBox(); TNutrisiGinSkor1.setBounds(195, y, 40, 23); FormInput.add(TNutrisiGinSkor1);
        Label lblNG2 = new Label(); lblNG2.setText("Asupan mkn (0-2) :"); lblNG2.setBounds(250, y, 150, 23); FormInput.add(lblNG2);
        TNutrisiGinSkor2 = new TextBox(); TNutrisiGinSkor2.setBounds(405, y, 40, 23); FormInput.add(TNutrisiGinSkor2);

        y += 27;
        Label lblNG3 = new Label(); lblNG3.setText("Penyakit berat (0-2) :"); lblNG3.setBounds(10, y, 180, 23); FormInput.add(lblNG3);
        TNutrisiGinSkor3 = new TextBox(); TNutrisiGinSkor3.setBounds(195, y, 40, 23); FormInput.add(TNutrisiGinSkor3);
        Label lblNG4 = new Label(); lblNG4.setText("Usia > 70 thn (0-1) :"); lblNG4.setBounds(250, y, 150, 23); FormInput.add(lblNG4);
        TNutrisiGinSkor4 = new TextBox(); TNutrisiGinSkor4.setBounds(405, y, 40, 23); FormInput.add(TNutrisiGinSkor4);

        // ================================================================
        // SECTION 11: EVALUASI SOAP
        // ================================================================
        y += 35;
        JLabel lblSec11 = new JLabel("K. EVALUASI (S.O.A.P)");
        lblSec11.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec11.setBounds(10, y, 300, 20); FormInput.add(lblSec11);

        y += 25;
        TEvaluasiSOAP = new JTextArea(); TEvaluasiSOAP.setLineWrap(true); TEvaluasiSOAP.setWrapStyleWord(true);
        TEvaluasiSOAP.setBorder(BorderFactory.createLineBorder(new Color(180,180,180)));
        JScrollPane scrSOAP = new JScrollPane(TEvaluasiSOAP);
        scrSOAP.setBounds(10, y, 750, 80); FormInput.add(scrSOAP);

        // ================================================================
        // SECTION 12: BIDAN / PETUGAS
        // ================================================================
        y += 90;
        JLabel lblSec12 = new JLabel("L. PETUGAS");
        lblSec12.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec12.setBounds(10, y, 200, 20); FormInput.add(lblSec12);

        y += 25;
        Label lblBidan1 = new Label(); lblBidan1.setText("Bidan Pelaksana :"); lblBidan1.setBounds(10, y, 130, 23); FormInput.add(lblBidan1);
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

        y += 32;
        Label lblBidan2 = new Label(); lblBidan2.setText("Bidan Melengkapi :"); lblBidan2.setBounds(10, y, 130, 23); FormInput.add(lblBidan2);
        KdPetugas2 = new TextBox(); KdPetugas2.setBounds(145, y, 80, 23); FormInput.add(KdPetugas2);
        NmPetugas2 = new TextBox(); NmPetugas2.setBounds(227, y, 200, 23); FormInput.add(NmPetugas2);
        BtnPetugas2 = new Button(); BtnPetugas2.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPetugas2.setBounds(429, y, 28, 23); FormInput.add(BtnPetugas2);
        BtnPetugas2.addActionListener(evt -> {
            petugasDlg2.emptTeks(); petugasDlg2.isCek();
            petugasDlg2.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20);
            petugasDlg2.setLocationRelativeTo(internalFrame1); petugasDlg2.setVisible(true);
        });
        petugasDlg2.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (petugasDlg2.getTable().getSelectedRow() != -1) {
                    KdPetugas2.setText(petugasDlg2.getTable().getValueAt(petugasDlg2.getTable().getSelectedRow(), 0).toString());
                    NmPetugas2.setText(petugasDlg2.getTable().getValueAt(petugasDlg2.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

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
        ScrollInput.setPreferredSize(new Dimension(920, 470));
        PanelInput = new JPanel(new BorderLayout());
        PanelInput.setOpaque(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 490));
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
            if (TNoRw.getText().trim().equals("")) { Valid.textKosong(TNoRw, "Pasien");
            } else if (KdPetugas.getText().trim().equals("")) { Valid.textKosong(KdPetugas, "Bidan Pelaksana");
            } else {
                // Build concatenated text columns
                String pernahDirawatKet = "Diagnosa: " + TDirawatDiagnosa.getText() + " | Kapan: " + TDirawatKapan.getText() + " | Di: " + TDirawatDi.getText();
                String pernahOperasiKet = "Jenis: " + TOperasiJenis.getText() + " | Kapan: " + TOperasiKapan.getText();
                String alergiKet = "Obat: " + TAlergiObat.getText() + " | Makanan: " + TAlergiMakanan.getText() + " | Lainnya: " + TAlergiLainnya.getText();
                String kontrasepsiKet = "Jenis: " + TKontrasepsiJenis.getText() + " | Lama: " + TKontrasepsiLama.getText() + " | Keluhan: " + TKontrasepsiKeluhan.getText();
                String riwMenstruasi = "Menarche: " + TMenarche.getText() + " | Siklus: " + TSiklusHaid.getText() + " | Lama: " + TLamaHaid.getText()
                    + " | Jumlah Darah: " + TJumlahDarah.getText() + " | Dismenore: " + TDismenore.getText() + " | Flour Albus: " + TFlourAlbus.getText();
                String riwHamil = "HPHT: " + THPHT.getText() + " | Taksiran: " + TTaksiranPersalinan.getText()
                    + " | Keluhan Muda: " + TKeluhanMuda.getText() + " | Keluhan Tua: " + TKeluhanTua.getText()
                    + " | TT: " + TTTImunisasi.getText() + " | ANC: " + TANC.getText();
                String pemFisik = "KU: " + TKeadaanUmum.getText() + " | Kesadaran: " + TKesadaran.getText()
                    + " | TD: " + TTD.getText() + " | Nadi: " + TNadi.getText() + " | RR: " + TRR.getText() + " | Suhu: " + TSuhu.getText()
                    + " | BB: " + TBB.getText() + " | TB: " + TTB.getText()
                    + " | Kepala: " + TKepala.getText() + " | Mata: " + TMata.getText() + " | Hidung: " + THidung.getText()
                    + " | Mulut: " + TMulut.getText() + " | Telinga: " + TTelinga.getText() + " | Leher: " + TLeher.getText()
                    + " | Mammae Dx: " + TMammaeDx.getText() + " | Mammae Sin: " + TMammaeSin.getText()
                    + " | Axilla Dx: " + TAxillaDx.getText() + " | Axilla Sin: " + TAxillaSin.getText()
                    + " | Eks. Atas: " + TEkstremitasAtas.getText() + " | Eks. Bawah: " + TEkstremitasBawah.getText();
                String pemObstetri = "Inspeksi: " + TAbdomenInspeksi.getText() + " | Palpasi: " + TAbdomenPalpasi.getText()
                    + " | Auskultasi: " + TAbdomenAuskultasi.getText()
                    + " | TFU: " + TTFU.getText() + " | Letak Janin: " + TLetakJanin.getText()
                    + " | DJJ: " + TDJJ.getText() + " | His: " + THis.getText();
                String pemAnoGenital = "Vulva: " + TVulva.getText() + " | Vagina: " + TVagina.getText()
                    + " | Portio: " + TPortio.getText() + " | Corpus Uteri: " + TCorpusUteri.getText()
                    + " | Adnexa: " + TAdnexa.getText() + " | Cavum Douglas: " + TCavumDouglas.getText()
                    + " | Perineum: " + TPerineum.getText() + " | Anus: " + TAnus.getText();
                String psikososialSpiritual = "Psikologis: " + TPsikologis.getText() + " | Sosial: " + TSosial.getText()
                    + " | Spiritual: " + TSpiritual.getText() + " | Budaya: " + TBudaya.getText();
                String skriningNyeri = "Nyeri: " + CmbNyeri.getSelectedItem().toString()
                    + " | Lokasi: " + TLokasiNyeri.getText() + " | Durasi: " + TDurasiNyeri.getText()
                    + " | Frekuensi: " + TFrekuensiNyeri.getText() + " | VAS: " + TSkalaVAS.getText()
                    + " | Menyebar: " + CmbNyeriMenyebar.getSelectedItem().toString();
                String statusFungsional = "Status: " + TStatusFungsional.getText() + " | Barthel: " + TBarthelIndex.getText();
                String nutrisiObstetri = "Mual/Muntah: " + CmbNutrisiObs1.getSelectedItem().toString()
                    + " | Nafsu Makan: " + CmbNutrisiObs2.getSelectedItem().toString()
                    + " | BB Turun: " + CmbNutrisiObs3.getSelectedItem().toString()
                    + " | Kondisi Khusus: " + CmbNutrisiObs4.getSelectedItem().toString();
                String nutrisiGinekologi = "Skor1: " + TNutrisiGinSkor1.getText() + " | Skor2: " + TNutrisiGinSkor2.getText()
                    + " | Skor3: " + TNutrisiGinSkor3.getText() + " | Skor4: " + TNutrisiGinSkor4.getText();

                if (Sequel.menyimpantf("asesmen_ginekologi_obstetri_rscb",
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?",
                    "Data", 56, new String[]{
                    TNoRw.getText(),
                    Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19),
                    CmbInformasiDari.getSelectedItem().toString(),
                    CmbCaraMasuk.getSelectedItem().toString(),
                    CmbAsalMasuk.getSelectedItem().toString(),
                    TAsalRuangan.getText(),
                    TNamaPasien.getText(), TPendidikanPasien.getText(), TPekerjaanPasien.getText(),
                    TAgamaPasien.getText(), TSukuPasien.getText(), TGoldarPasien.getText(),
                    TNamaSuami.getText(), TPendidikanSuami.getText(), TPekerjaanSuami.getText(),
                    TAgamaSuami.getText(), TSukuSuami.getText(), TGoldarSuami.getText(),
                    TAlamatRumah.getText(), TAlamatKantor.getText(), TNoTelp.getText(),
                    TKeluhanUtama.getText(),
                    CmbRiwPenyakitLalu.getSelectedItem().toString(), TRiwPenyakitLaluKet.getText(),
                    CmbPernahDirawat.getSelectedItem().toString(), pernahDirawatKet,
                    CmbPernahOperasi.getSelectedItem().toString(), pernahOperasiKet,
                    CmbMasihPengobatan.getSelectedItem().toString(), TMasihPengobatanKet.getText(),
                    CmbRiwKeluarga.getSelectedItem().toString(), TRiwKeluargaKet.getText(),
                    CmbRiwPekerjaanBahaya.getSelectedItem().toString(), TRiwPekerjaanKet.getText(),
                    CmbRiwAlergi.getSelectedItem().toString(), alergiKet,
                    CmbKontrasepsi.getSelectedItem().toString(), kontrasepsiKet,
                    CmbStatusPernikahan.getSelectedItem().toString(), TRiwNikah.getText(),
                    riwMenstruasi,
                    riwHamil,
                    CmbRiwGinekologi.getSelectedItem().toString(), TRiwGinekologiKet.getText(),
                    pemFisik,
                    pemObstetri,
                    pemAnoGenital,
                    psikososialSpiritual,
                    CmbRisikoJatuh.getSelectedItem().toString(),
                    skriningNyeri,
                    statusFungsional,
                    nutrisiObstetri,
                    nutrisiGinekologi,
                    TEvaluasiSOAP.getText(),
                    KdPetugas2.getText(),
                    KdPetugas.getText()
                })) { tampil(); emptTeks(); }
            }
        });

        BtnHapus.addActionListener(evt -> {
            if (tbObat.getSelectedRow() != -1) {
                if (Sequel.queryu2tf("delete from asesmen_ginekologi_obstetri_rscb where no_rawat=? and tanggal=?", 2, new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
                })) { tampil(); emptTeks(); }
            } else JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dihapus...!!!!");
        });

        BtnEdit.addActionListener(evt -> {
            if (TNoRw.getText().trim().equals("") || tbObat.getSelectedRow()==-1) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            } else {
                if (Sequel.queryu2tf("delete from asesmen_ginekologi_obstetri_rscb where no_rawat=? and tanggal=?", 2, new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
                })) { BtnSimpan.doClick(); }
            }
        });

        BtnPrint.addActionListener(evt -> {
            if (TNoRw.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu..!!!");
            } else if (tbObat.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu..!!!");
            } else {
                Map<String, Object> param = new HashMap<>();
                param.put("namars", akses.getnamars()); param.put("alamatrs", akses.getalamatrs());
                param.put("propinsirs", akses.getpropinsirs()); param.put("kabupatenrs", akses.getkabupatenrs());
                param.put("kontakrs", akses.getkontakrs()); param.put("emailrs", akses.getemailrs());
                param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
                Valid.MyReportqry("rptAsesmenGinekologiObstetriRscb.jasper", "report", "::[ Asesmen Ginekologi Obstetri ]::",
                    "select s.*, r.no_rkm_medis, p.nm_pasien, pt.nama as nm_petugas, pt2.nama as nm_melengkapi "+
                    "from asesmen_ginekologi_obstetri_rscb s inner join reg_periksa r on s.no_rawat=r.no_rawat "+
                    "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "+
                    "left join petugas pt on s.nip=pt.nip "+
                    "left join petugas pt2 on s.nip_melengkapi=pt2.nip "+
                    "where s.no_rawat='"+TNoRw.getText()+"' and s.tanggal='"+
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()+"'", param);
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
        if (ChkInput.isSelected()) { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 490)); FormInput.setVisible(true); ChkInput.setVisible(true);
        } else { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 20)); FormInput.setVisible(false); ChkInput.setVisible(true); }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select s.no_rawat, r.no_rkm_medis, p.nm_pasien, s.tanggal, s.keluhan_utama, s.asal_masuk, "+
                "pt.nama as nm_petugas "+
                "from asesmen_ginekologi_obstetri_rscb s inner join reg_periksa r on s.no_rawat=r.no_rawat "+
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "+
                "left join petugas pt on s.nip=pt.nip "+
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
                    rs.getString("tanggal"), rs.getString("keluhan_utama"), rs.getString("asal_masuk"),
                    rs.getString("nm_petugas")
                });
            }
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif Asesmen Ginekologi Obstetri : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            try { TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString())); } catch (Exception e) {}
            try {
                ps = koneksi.prepareStatement("select * from asesmen_ginekologi_obstetri_rscb where no_rawat=? and tanggal=?");
                ps.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                ps.setString(2, tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    CmbInformasiDari.setSelectedItem(rs.getString("informasi_dari"));
                    CmbCaraMasuk.setSelectedItem(rs.getString("cara_masuk"));
                    CmbAsalMasuk.setSelectedItem(rs.getString("asal_masuk"));
                    TAsalRuangan.setText(rs.getString("asal_ruangan"));
                    TNamaPasien.setText(rs.getString("nama_pasien"));
                    TPendidikanPasien.setText(rs.getString("pendidikan_pasien"));
                    TPekerjaanPasien.setText(rs.getString("pekerjaan_pasien"));
                    TAgamaPasien.setText(rs.getString("agama_pasien"));
                    TSukuPasien.setText(rs.getString("suku_pasien"));
                    TGoldarPasien.setText(rs.getString("goldar_pasien"));
                    TNamaSuami.setText(rs.getString("nama_suami"));
                    TPendidikanSuami.setText(rs.getString("pendidikan_suami"));
                    TPekerjaanSuami.setText(rs.getString("pekerjaan_suami"));
                    TAgamaSuami.setText(rs.getString("agama_suami"));
                    TSukuSuami.setText(rs.getString("suku_suami"));
                    TGoldarSuami.setText(rs.getString("goldar_suami"));
                    TAlamatRumah.setText(rs.getString("alamat_rumah"));
                    TAlamatKantor.setText(rs.getString("alamat_kantor"));
                    TNoTelp.setText(rs.getString("no_telp"));
                    TKeluhanUtama.setText(rs.getString("keluhan_utama"));

                    // Riwayat Kesehatan
                    CmbRiwPenyakitLalu.setSelectedItem(rs.getString("riwayat_penyakit_lalu"));
                    TRiwPenyakitLaluKet.setText(rs.getString("riwayat_penyakit_lalu_ket"));
                    CmbPernahDirawat.setSelectedItem(rs.getString("pernah_dirawat"));
                    parseDirawat(rs.getString("pernah_dirawat_ket"));
                    CmbPernahOperasi.setSelectedItem(rs.getString("pernah_operasi"));
                    parseOperasi(rs.getString("pernah_operasi_ket"));
                    CmbMasihPengobatan.setSelectedItem(rs.getString("masih_pengobatan"));
                    TMasihPengobatanKet.setText(rs.getString("masih_pengobatan_ket"));
                    CmbRiwKeluarga.setSelectedItem(rs.getString("riwayat_keluarga"));
                    TRiwKeluargaKet.setText(rs.getString("riwayat_keluarga_ket"));
                    CmbRiwPekerjaanBahaya.setSelectedItem(rs.getString("riwayat_pekerjaan_bahaya"));
                    TRiwPekerjaanKet.setText(rs.getString("riwayat_pekerjaan_ket"));
                    CmbRiwAlergi.setSelectedItem(rs.getString("riwayat_alergi"));
                    parseAlergi(rs.getString("alergi_ket"));
                    CmbKontrasepsi.setSelectedItem(rs.getString("kontrasepsi"));
                    parseKontrasepsi(rs.getString("kontrasepsi_ket"));
                    CmbStatusPernikahan.setSelectedItem(rs.getString("status_pernikahan"));
                    TRiwNikah.setText(rs.getString("riwayat_nikah"));
                    parseMenstruasi(rs.getString("riwayat_menstruasi"));
                    parseHamil(rs.getString("riwayat_hamil"));
                    CmbRiwGinekologi.setSelectedItem(rs.getString("riwayat_ginekologi"));
                    TRiwGinekologiKet.setText(rs.getString("riwayat_ginekologi_ket"));

                    // Pemeriksaan Fisik
                    parsePemFisik(rs.getString("pemeriksaan_fisik"));
                    parsePemObstetri(rs.getString("pemeriksaan_obstetri"));
                    parsePemAnoGenital(rs.getString("pemeriksaan_ano_genital"));
                    parsePsikososial(rs.getString("psikososial_spiritual"));
                    CmbRisikoJatuh.setSelectedItem(rs.getString("risiko_jatuh"));
                    parseNyeri(rs.getString("skrining_nyeri"));
                    parseFungsional(rs.getString("status_fungsional"));
                    parseNutrisiObs(rs.getString("nutrisi_obstetri"));
                    parseNutrisiGin(rs.getString("nutrisi_ginekologi"));
                    TEvaluasiSOAP.setText(rs.getString("evaluasi_soap"));

                    KdPetugas2.setText(rs.getString("nip_melengkapi"));
                    NmPetugas2.setText(Sequel.cariIsi("select nama from petugas where nip=?", rs.getString("nip_melengkapi")));
                    KdPetugas.setText(rs.getString("nip"));
                    NmPetugas.setText(Sequel.cariIsi("select nama from petugas where nip=?", rs.getString("nip")));
                }
            } catch (Exception e) { System.out.println("Notif getData Asesmen Ginekologi Obstetri : " + e);
            } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
        }
    }

    // ====== PARSE HELPERS ======

    private String extractField(String data, String key) {
        if (data == null || data.isEmpty()) return "";
        String[] parts = data.split(" \\| ");
        for (String part : parts) {
            part = part.trim();
            if (part.startsWith(key + ": ")) {
                return part.substring(key.length() + 2);
            }
        }
        return "";
    }

    private void parseDirawat(String data) {
        TDirawatDiagnosa.setText(extractField(data, "Diagnosa"));
        TDirawatKapan.setText(extractField(data, "Kapan"));
        TDirawatDi.setText(extractField(data, "Di"));
    }

    private void parseOperasi(String data) {
        TOperasiJenis.setText(extractField(data, "Jenis"));
        TOperasiKapan.setText(extractField(data, "Kapan"));
    }

    private void parseAlergi(String data) {
        TAlergiObat.setText(extractField(data, "Obat"));
        TAlergiMakanan.setText(extractField(data, "Makanan"));
        TAlergiLainnya.setText(extractField(data, "Lainnya"));
    }

    private void parseKontrasepsi(String data) {
        TKontrasepsiJenis.setText(extractField(data, "Jenis"));
        TKontrasepsiLama.setText(extractField(data, "Lama"));
        TKontrasepsiKeluhan.setText(extractField(data, "Keluhan"));
    }

    private void parseMenstruasi(String data) {
        TMenarche.setText(extractField(data, "Menarche"));
        TSiklusHaid.setText(extractField(data, "Siklus"));
        TLamaHaid.setText(extractField(data, "Lama"));
        TJumlahDarah.setText(extractField(data, "Jumlah Darah"));
        TDismenore.setText(extractField(data, "Dismenore"));
        TFlourAlbus.setText(extractField(data, "Flour Albus"));
    }

    private void parseHamil(String data) {
        THPHT.setText(extractField(data, "HPHT"));
        TTaksiranPersalinan.setText(extractField(data, "Taksiran"));
        TKeluhanMuda.setText(extractField(data, "Keluhan Muda"));
        TKeluhanTua.setText(extractField(data, "Keluhan Tua"));
        TTTImunisasi.setText(extractField(data, "TT"));
        TANC.setText(extractField(data, "ANC"));
    }

    private void parsePemFisik(String data) {
        TKeadaanUmum.setText(extractField(data, "KU"));
        TKesadaran.setText(extractField(data, "Kesadaran"));
        TTD.setText(extractField(data, "TD"));
        TNadi.setText(extractField(data, "Nadi"));
        TRR.setText(extractField(data, "RR"));
        TSuhu.setText(extractField(data, "Suhu"));
        TBB.setText(extractField(data, "BB"));
        TTB.setText(extractField(data, "TB"));
        TKepala.setText(extractField(data, "Kepala"));
        TMata.setText(extractField(data, "Mata"));
        THidung.setText(extractField(data, "Hidung"));
        TMulut.setText(extractField(data, "Mulut"));
        TTelinga.setText(extractField(data, "Telinga"));
        TLeher.setText(extractField(data, "Leher"));
        TMammaeDx.setText(extractField(data, "Mammae Dx"));
        TMammaeSin.setText(extractField(data, "Mammae Sin"));
        TAxillaDx.setText(extractField(data, "Axilla Dx"));
        TAxillaSin.setText(extractField(data, "Axilla Sin"));
        TEkstremitasAtas.setText(extractField(data, "Eks. Atas"));
        TEkstremitasBawah.setText(extractField(data, "Eks. Bawah"));
    }

    private void parsePemObstetri(String data) {
        TAbdomenInspeksi.setText(extractField(data, "Inspeksi"));
        TAbdomenPalpasi.setText(extractField(data, "Palpasi"));
        TAbdomenAuskultasi.setText(extractField(data, "Auskultasi"));
        TTFU.setText(extractField(data, "TFU"));
        TLetakJanin.setText(extractField(data, "Letak Janin"));
        TDJJ.setText(extractField(data, "DJJ"));
        THis.setText(extractField(data, "His"));
    }

    private void parsePemAnoGenital(String data) {
        TVulva.setText(extractField(data, "Vulva"));
        TVagina.setText(extractField(data, "Vagina"));
        TPortio.setText(extractField(data, "Portio"));
        TCorpusUteri.setText(extractField(data, "Corpus Uteri"));
        TAdnexa.setText(extractField(data, "Adnexa"));
        TCavumDouglas.setText(extractField(data, "Cavum Douglas"));
        TPerineum.setText(extractField(data, "Perineum"));
        TAnus.setText(extractField(data, "Anus"));
    }

    private void parsePsikososial(String data) {
        TPsikologis.setText(extractField(data, "Psikologis"));
        TSosial.setText(extractField(data, "Sosial"));
        TSpiritual.setText(extractField(data, "Spiritual"));
        TBudaya.setText(extractField(data, "Budaya"));
    }

    private void parseNyeri(String data) {
        CmbNyeri.setSelectedItem(extractField(data, "Nyeri"));
        TLokasiNyeri.setText(extractField(data, "Lokasi"));
        TDurasiNyeri.setText(extractField(data, "Durasi"));
        TFrekuensiNyeri.setText(extractField(data, "Frekuensi"));
        TSkalaVAS.setText(extractField(data, "VAS"));
        CmbNyeriMenyebar.setSelectedItem(extractField(data, "Menyebar"));
    }

    private void parseFungsional(String data) {
        TStatusFungsional.setText(extractField(data, "Status"));
        TBarthelIndex.setText(extractField(data, "Barthel"));
    }

    private void parseNutrisiObs(String data) {
        String mualMuntah = extractField(data, "Mual/Muntah");
        String nafsuMakan = extractField(data, "Nafsu Makan");
        String bbTurun = extractField(data, "BB Turun");
        String kondisiKhusus = extractField(data, "Kondisi Khusus");
        CmbNutrisiObs1.setSelectedItem(mualMuntah.isEmpty() ? "Tidak" : mualMuntah);
        CmbNutrisiObs2.setSelectedItem(nafsuMakan.isEmpty() ? "Tidak" : nafsuMakan);
        CmbNutrisiObs3.setSelectedItem(bbTurun.isEmpty() ? "Tidak" : bbTurun);
        CmbNutrisiObs4.setSelectedItem(kondisiKhusus.isEmpty() ? "Tidak" : kondisiKhusus);
    }

    private void parseNutrisiGin(String data) {
        TNutrisiGinSkor1.setText(extractField(data, "Skor1"));
        TNutrisiGinSkor2.setText(extractField(data, "Skor2"));
        TNutrisiGinSkor3.setText(extractField(data, "Skor3"));
        TNutrisiGinSkor4.setText(extractField(data, "Skor4"));
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date());
        CmbInformasiDari.setSelectedIndex(0); CmbCaraMasuk.setSelectedIndex(0); CmbAsalMasuk.setSelectedIndex(0);
        TAsalRuangan.setText("");
        TNamaPasien.setText(""); TPendidikanPasien.setText(""); TPekerjaanPasien.setText("");
        TAgamaPasien.setText(""); TSukuPasien.setText(""); TGoldarPasien.setText("");
        TNamaSuami.setText(""); TPendidikanSuami.setText(""); TPekerjaanSuami.setText("");
        TAgamaSuami.setText(""); TSukuSuami.setText(""); TGoldarSuami.setText("");
        TAlamatRumah.setText(""); TAlamatKantor.setText(""); TNoTelp.setText("");
        TKeluhanUtama.setText("");
        CmbRiwPenyakitLalu.setSelectedIndex(0); TRiwPenyakitLaluKet.setText("");
        CmbPernahDirawat.setSelectedIndex(0); TDirawatDiagnosa.setText(""); TDirawatKapan.setText(""); TDirawatDi.setText("");
        CmbPernahOperasi.setSelectedIndex(0); TOperasiJenis.setText(""); TOperasiKapan.setText("");
        CmbMasihPengobatan.setSelectedIndex(0); TMasihPengobatanKet.setText("");
        CmbRiwKeluarga.setSelectedIndex(0); TRiwKeluargaKet.setText("");
        CmbRiwPekerjaanBahaya.setSelectedIndex(0); TRiwPekerjaanKet.setText("");
        CmbRiwAlergi.setSelectedIndex(0); TAlergiObat.setText(""); TAlergiMakanan.setText(""); TAlergiLainnya.setText("");
        CmbKontrasepsi.setSelectedIndex(0); TKontrasepsiJenis.setText(""); TKontrasepsiLama.setText(""); TKontrasepsiKeluhan.setText("");
        CmbStatusPernikahan.setSelectedIndex(0); TRiwNikah.setText("");
        TMenarche.setText(""); TSiklusHaid.setText(""); TLamaHaid.setText("");
        TJumlahDarah.setText(""); TDismenore.setText(""); TFlourAlbus.setText("");
        THPHT.setText(""); TTaksiranPersalinan.setText(""); TKeluhanMuda.setText(""); TKeluhanTua.setText("");
        TTTImunisasi.setText(""); TANC.setText("");
        CmbRiwGinekologi.setSelectedIndex(0); TRiwGinekologiKet.setText("");
        TKeadaanUmum.setText(""); TKesadaran.setText(""); TTD.setText(""); TNadi.setText(""); TRR.setText(""); TSuhu.setText("");
        TBB.setText(""); TTB.setText("");
        TKepala.setText(""); TMata.setText(""); THidung.setText(""); TMulut.setText(""); TTelinga.setText(""); TLeher.setText("");
        TMammaeDx.setText(""); TMammaeSin.setText(""); TAxillaDx.setText(""); TAxillaSin.setText("");
        TEkstremitasAtas.setText(""); TEkstremitasBawah.setText("");
        TAbdomenInspeksi.setText(""); TAbdomenPalpasi.setText(""); TAbdomenAuskultasi.setText("");
        TTFU.setText(""); TLetakJanin.setText(""); TDJJ.setText(""); THis.setText("");
        TVulva.setText(""); TVagina.setText(""); TPortio.setText(""); TCorpusUteri.setText("");
        TAdnexa.setText(""); TCavumDouglas.setText(""); TPerineum.setText(""); TAnus.setText("");
        TPsikologis.setText(""); TSosial.setText(""); TSpiritual.setText(""); TBudaya.setText("");
        CmbRisikoJatuh.setSelectedIndex(0);
        CmbNyeri.setSelectedIndex(0); TLokasiNyeri.setText(""); TDurasiNyeri.setText("");
        TFrekuensiNyeri.setText(""); TSkalaVAS.setText(""); CmbNyeriMenyebar.setSelectedIndex(0);
        TStatusFungsional.setText(""); TBarthelIndex.setText("");
        CmbNutrisiObs1.setSelectedIndex(0); CmbNutrisiObs2.setSelectedIndex(0);
        CmbNutrisiObs3.setSelectedIndex(0); CmbNutrisiObs4.setSelectedIndex(0);
        TNutrisiGinSkor1.setText(""); TNutrisiGinSkor2.setText("");
        TNutrisiGinSkor3.setText(""); TNutrisiGinSkor4.setText("");
        TEvaluasiSOAP.setText("");
        KdPetugas.setText(""); NmPetugas.setText(""); KdPetugas2.setText(""); NmPetugas2.setText("");
    }

    public void setNoRm(String norwt, Date tgl) {
        TNoRw.setText(norwt); TglAsuhan.setDate(tgl);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", norwt));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
    }

    public void setNoRm(String norwt, String norm, String nama) {
        TNoRw.setText(norwt); TNoRM.setText(norm); TPasien.setText(nama);
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getrujukan_keluar()); BtnHapus.setEnabled(akses.getrujukan_keluar());
        BtnEdit.setEnabled(akses.getrujukan_keluar()); BtnPrint.setEnabled(akses.getrujukan_keluar());
    }
}
