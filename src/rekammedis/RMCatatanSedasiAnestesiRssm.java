package rekammedis;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import kepegawaian.DlgCariDokter;
import kepegawaian.DlgCariPetugas;
import kepegawaian.DlgCariPegawai;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import widget.*;

public class RMCatatanSedasiAnestesiRssm extends JDialog {

    // Panel atas
    private TextBox TNoRw, TNoRM, TPasien, TTglLahir, TJK, TUmur, TGolDarah;
    private TextBox KdDokter, NmDokter, KdPenata, NmPenata;
    private TextBox KdPenginput, NmPenginput;
    private widget.Tanggal TglAsuhan;
    private ComboBox Jam, Menit, Detik;
    private CekBox ChkInput, ChkKejadian;

    // Tab Pra Sedasi/Anestesi
    private TextBox TProsedur, TRhesus, TBB, TTB;
    private TextBox TRiwayat1, TRiwayat2, TRiwayat3;
    private CekBox ChkTidakAdaRiwayat;
    private TextBox TObatDikonsumsi;
    private CekBox ChkTidakAdaObat;
    private TextBox TAlergiObat;
    private CekBox ChkTidakAdaAlergi;
    private CekBox ChkKomplikasiSblm, ChkKomplikasiKeluarga;
    private TextBox TTdPra, TNadiPra, TRespPra, TSuhuPra, TVasPra;

    // Jalan napas — Obstruksi
    private CekBox ChkObsTidakAda, ChkObsTumor, ChkObsKelCongenital, ChkObsStridor, ChkObsGigiProm;
    // Jalan napas — Thoraks/Abdomen
    private CekBox ChkThorNormal, ChkThorHamil, ChkThorAscites, ChkThorObstruksi, ChkThorObesitas;
    // Jalan napas — Wajah
    private CekBox ChkWajahNormal, ChkWajahRahangSempit, ChkWajahGigiProm;
    private CekBox ChkWajahRambutWajah, ChkWajahGigiGoyang, ChkWajahEdema, ChkWajahKelRahang;
    // Jalan napas — Kanan (Ya/Tidak)
    private CekBox ChkBukaMulutYa, ChkBukaMulutTidak;
    private CekBox ChkJarakThyroYa, ChkJarakThyroTidak;
    private CekBox ChkGerakRahangYa, ChkGerakRahangTidak;
    private CekBox ChkFleksiYa, ChkFleksiTidak;
    private CekBox ChkPenyanggaYa, ChkPenyanggaTidak;
    private CekBox ChkJalanNapasYa, ChkJalanNapasTidak;
    private ComboBox CmbMallampati;

    // Sistem — Pernapasan
    private CekBox ChkNapasAsma, ChkNapasBatukProduktif, ChkNapasBronkitis, ChkNapasSesak;
    private CekBox ChkNapasPPOK, ChkNapasTuberkulosis, ChkNapasOrthopnea, ChkNapasPneumonia, ChkNapasNormal;
    // Sistem — Kardiovaskular
    private CekBox ChkKardioEKG, ChkKardioHipertensi, ChkKardioAngina, ChkKardioInfark;
    private CekBox ChkKardioASHD, ChkKardioMurmur, ChkKardioCHF, ChkKardioPacemaker;
    private CekBox ChkKardioDisritmia, ChkKardioDemamRheuma, ChkKardioLimitasi, ChkKardioPenyakitKatup, ChkKardioNormal;
    // Sistem — Neuro/Musculoskeletal
    private CekBox ChkNeuroArthritis, ChkNeuroKelemahanOtot, ChkNeuroCVA, ChkNeuroParalisis;
    private CekBox ChkNeuroDegeneratif, ChkNeuroParesthesia, ChkNeuroNyeriKepala, ChkNeuroPingsan;
    private CekBox ChkNeuroPenurunanKesadaran, ChkNeuroKejang, ChkNeuroGangguanTulang, ChkNeuroNormal;
    // Sistem — Renal/Endokrin
    private CekBox ChkRenalDiabetes, ChkRenalGagalGinjal, ChkRenalTiroid;
    private CekBox ChkRenalRetensiUrin, ChkRenalInfeksiSaluran, ChkRenalPenurunanBB, ChkRenalNormal;
    // Sistem — Hepato/Gastrointestinal
    private CekBox ChkHepatoObstruksiUsus, ChkHepatoSirosis, ChkHepatoHepatitis;
    private CekBox ChkHepatoHiatal, ChkHepatoMualMuntah, ChkHepatoUlkus, ChkHepatoNormal;
    // Sistem — Lain-lain
    private CekBox ChkLainAnemia, ChkLainKehamilan, ChkLainDehidrasi, ChkLainKemoterapi;
    private CekBox ChkLainHemofilia, ChkLainSickleCell, ChkLainImunosupresi, ChkLainRiwayatSteroid;
    private CekBox ChkLainKanker, ChkLainRiwayatTransfusi, ChkLainGangguanPerdarahan, ChkLainNormal;
    // Merokok / Alkohol
    private CekBox ChkMerokok, ChkMerokokTidak; private TextBox TMerokokPak, TMerokokTahun;
    private CekBox ChkAlkohol, ChkAlkoholTidak; private TextBox TAlkohol;
    // Penunjang (checkbox)
    private CekBox ChkPenunjangEKG, ChkPenunjangEvaluasiParu, ChkPenunjangXRay;
    private TextBox TPenunjangLain;
    // Lab (checkbox)
    private CekBox ChkLabHbHt, ChkLabUrinalisis, ChkLabElektrolit;
    private TextBox TLab, TLabLain;
    private TextBox TMasalah1, TMasalah2, TMasalah3;
    private ComboBox CmbASA;
    private TextBox TPraMedikasi;
    // Tab Plan of Care
    private CekBox ChkTeknikMAC, ChkTeknikSedasi, ChkTeknikAnestesiUmum;
    private CekBox ChkTeknikRegional, ChkTeknikKombinasi;
    private CekBox ChkTeknikSpinal, ChkTeknikEpidural, ChkTeknikPeriperal;
    private TextBox TTeknikLain, TTeknikAlternatif;
    private CekBox ChkMonitorALine, ChkMonitorCVC, ChkMonitorPAC, ChkMonitorTEE;
    private TextBox TMonitorLain;
    private CekBox ChkKontrolIntravena, ChkKontrolEpidural, ChkKontrolSpinal, ChkKontrolPCA;
    private TextBox TKontrolLain;
    private JTextArea TRisiko;

    // Tab Pra Induksi
    private TextBox KdDokterOp, TDokterOperator, TProsedurOperasi, TRuangOT;
    // Section A
    private TextBox TPuasaJam;
    private CekBox ChkPasienDiidentifikasi, ChkRekamMedis, ChkInformedConsent, ChkPuasa;
    private CekBox ChkMallampatiI, ChkMallampatiII, ChkMallampatiIII, ChkMallampatiIV;
    private CekBox ChkKondisiTenang, ChkKondisiSadar, ChkKondisiCemas, ChkKondisiDisorientasi;
    private CekBox ChkKondisiTdkKooperatif, ChkKondisiTdkRespon;
    private TextBox TTdPraInd, TNadiPraInd, TRespPraInd, TSpo2, TAlergiPra;
    // Section B
    private CekBox ChkPengecekanMesin, ChkTaliPengaman, ChkPenyanggaLengan;
    private CekBox ChkPressurePoint, ChkPenyanggaKetiak, ChkTanganTerlindungi;
    private CekBox ChkMatOintment, ChkMatSaline, ChkMatTapped, ChkMatPads, ChkMatGoggles;
    // Section C
    private CekBox ChkMonStetoskop, ChkMonPrekordial;
    private CekBox ChkMonNIBP, ChkMonNIBPKiri, ChkMonNIBPKanan;
    private CekBox ChkMonEKG, ChkMonEsoph;
    private TextBox TEKGLead;
    private CekBox ChkMonPulseOxy, ChkMonSensorO2;
    private CekBox ChkMonSelimut, ChkMonGasAnalyzer;
    private CekBox ChkMonEEG, ChkMonNerveStimulator;
    private CekBox ChkMonKateter, ChkMonDoppler;
    private CekBox ChkMonNGTOGT, ChkMonBIS, ChkMonIntravena;
    private CekBox ChkMonArteriLine, ChkMonCVC, ChkMonPAC;
    // Section D
    private CekBox ChkTUPreOxy, ChkTUIntravena, ChkTUInhalasi, ChkTUIntramuskular;
    private CekBox ChkTURSI, ChkTUCricoid;
    private CekBox ChkSedasiRingan, ChkSedasiSedang, ChkSedasiDalam;
    private TextBox TTeknikRegional, TTeknikRegional2;
    // Section E
    private CekBox ChkJNPernapasanSpontan, ChkJNNasalO2, ChkJNMask;
    private CekBox ChkJNSupraglotic;
    private TextBox TJNJenis, TJNUkuran;
    private CekBox ChkJNTrakeostomi;
    private CekBox ChkJNIntubasi, ChkJNNasal, ChkJNOral;
    private CekBox ChkJNSleep, ChkJNApnea, ChkJNDirect;
    private CekBox ChkJNAwake, ChkJNNonApnea, ChkJNBlind;
    private CekBox ChkETTRegular, ChkETTReformed;
    private CekBox ChkETTPreFormedBend, ChkETTDoubleLumenL, ChkETTDoubleLumenR;
    private TextBox TETTSize, TETTBatas;
    private CekBox ChkETTCuffed, ChkETTUncuffed, ChkETTUdara, ChkETTAir;
    private CekBox ChkJNStyle, ChkJNMagill, ChkJNLaryngoscope;
    private TextBox TLarynUkuran;
    private CekBox ChkJNVideoLaryngoscope;
    private TextBox TVLUkuran;
    private CekBox ChkJNFiberoptic;
    private TextBox TFiberPercobaan;
    private CekBox ChkSuaraSama, ChkSuaraTidakSama;
    // Section F - Cairan
    private CekBox ChkKoloid, ChkKristaloid, ChkWB, ChkPRC, ChkFFP, ChkCryo, ChkCairanLainChk;
    private TextBox TCairanKoloid, TCairanKristaloid;
    private TextBox TTransfusiWB, TTransfusiPRC, TTransfusiFFP, TTransfusiCryo, TCairanLain;
    private TextBox TOutputDarah, TOutputUrin, TOutputNGT;

    // Tab Pasca
    private widget.Tanggal TglPasca;
    private JSpinner SpJamPasca, SpMenitPasca;
    private TextBox TNamaPengkaji;
    private TextBox TDPasca, TNPasca, TRespPasca, TSPasca;
    // Skor Aidrete structured
    private ComboBox CmbAidreteWaktu;
    private ComboBox CmbAidreteSirkulasi, CmbAidreteKesadaran;
    private ComboBox CmbAidreteSatO2Dws, CmbAidreteSatO2Pedi;
    private ComboBox CmbAidretePernapasan, CmbAidreteAktifitas;
    private Label LblAidreteTotal;
    // Skor Nyeri
    private TextBox TVasNyeri;
    private ComboBox CmbNyeriWaktu;
    // Skor Bromage
    private ComboBox CmbBromageWaktu, CmbBromageLevel;
    private JTextArea TInstruksiRR;
    private ComboBox CmbDischargeDari;
    private JSpinner SpJamDischarge, SpMenitDischarge;
    private TextBox TTdDischarge, TNDischarge, TRespDischarge, TSDischarge, TVasDischarge;
    private JTextArea TInstruksiAnestesi;
    // Discharge checkboxes
    private CekBox ChkDischBangsal, ChkDischICU, ChkDischHCU;

    // Panel bawah & tabel
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnKeluar;
    private InternalFrame internalFrame1;

    // Per-tab tables — Pra Sedasi
    private DefaultTableModel tabModePra; private widget.Table tbPra;
    // Per-tab tables — Plan of Care
    private DefaultTableModel tabModePOC; private widget.Table tbPOC;
    // Per-tab tables — Pra Induksi
    private DefaultTableModel tabModePraInd; private widget.Table tbPraInd;
    // Per-tab tables — Pasca
    private DefaultTableModel tabModePasca; private widget.Table tbPasca;
    // Per-tab tables — Instruksi RR
    private DefaultTableModel tabModeRR; private widget.Table tbRR;
    // Per-tab tables — Discharge
    private DefaultTableModel tabModeDisch; private widget.Table tbDisch;
    // Per-tab tables — Durante
    private DefaultTableModel tabModeDur; private widget.Table tbDur;
    // Shared search bar (panel bawah)
    private widget.Tanggal DTPCari1, DTPCari2;
    private TextBox TCari;
    private Label LcntCari;
    private Button BtnGrafik, BtnCetak;
    // Durante — Obat grid
    private DefaultTableModel obatGridModel;
    private JTable obatGridTable;
    private JSpinner obatHour, obatMin;
    private final int[] obatInterval = {15};
    // Durante — Cairan grid
    private DefaultTableModel cairanGridModel;
    private JTable cairanGridTable;
    private JSpinner cairanHour, cairanMin;
    private final int[] cairanInterval = {15};
    private static final int N_SLOTS = 20;
    // Durante — Gas & Monitor
    private TextBox TOksigen, TN2O, TAirLmenit, TSaturasiO2, TFiO2, TEndTidalCO2;
    private TextBox TEKGDur, TCvpPcwp, TSuhuDur, TUrinDur, TLainLainDur;
    private CekBox ChkJacksonRees;
    // Durante — Ventilator
    private TextBox TFrekPernapasan, TVolumeTidal, TPeakPressure, TPEEP;
    // Durante — Komplikasi
    private JTextArea TKomplikasiDur;

    // Tab Catatan
    private widget.Tanggal TglOperasi, TglAnestesi, TglAntibiotik;
    private JSpinner SpJamOp, SpMenitOp, SpDetikOp;
    private JSpinner SpJamAn, SpMenitAn, SpDetikAn;
    private JSpinner SpJamAnti, SpMenitAnti, SpDetikAnti;
    private CekBox ChkEkstubasi, ChkIntubasi, ChkOralNasal;
    private CekBox ChkSadar, ChkMengantuk, ChkStabil, ChkTidakStabil;
    private DefaultTableModel tabModeCatatan; private widget.Table tbCatatan;

    private PanelBiasa FormPra, FormPraInd;
    private JPanel PanelInput;

    private JTabbedPane Tabs;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariDokter dokterDlg = new DlgCariDokter(null, false);
    private DlgCariDokter dokterOpDlg = new DlgCariDokter(null, false);
    private DlgCariPetugas penataDlg = new DlgCariPetugas(null, false);
    private DlgCariPegawai pegawaiDlg = new DlgCariPegawai(null, false);

    public RMCatatanSedasiAnestesiRssm(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(1050, 800);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Catatan Sedasi / Anestesi ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // ===== PANEL ATAS =====
        panelisi panelAtas = new panelisi();
        panelAtas.setLayout(new BoxLayout(panelAtas, BoxLayout.Y_AXIS));
        panelAtas.setPreferredSize(new Dimension(1040, 85));

        JPanel baris1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        baris1.setOpaque(false);
        JPanel baris2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        baris2.setOpaque(false);
        JPanel baris3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        baris3.setOpaque(false);

        // === Baris 1 ===
        Label lblNoRw = new Label(); lblNoRw.setText("No.Rawat :");
        TNoRw = new TextBox(); TNoRw.setPreferredSize(new Dimension(130, 23));
        TNoRM = new TextBox(); TNoRM.setPreferredSize(new Dimension(90, 23)); TNoRM.setEditable(false);
        TPasien = new TextBox(); TPasien.setPreferredSize(new Dimension(180, 23)); TPasien.setEditable(false);
        Label lblTglLahir = new Label(); lblTglLahir.setText("Tgl.Lahir :");
        TTglLahir = new TextBox(); TTglLahir.setPreferredSize(new Dimension(85, 23)); TTglLahir.setEditable(false);
        Label lblUmur = new Label(); lblUmur.setText("Umur :");
        TUmur = new TextBox(); TUmur.setPreferredSize(new Dimension(70, 23)); TUmur.setEditable(false);
        Label lblJK = new Label(); lblJK.setText("JK :");
        TJK = new TextBox(); TJK.setPreferredSize(new Dimension(30, 23)); TJK.setEditable(false);
        Label lblGolDarah = new Label(); lblGolDarah.setText("Gol.Darah :");
        TGolDarah = new TextBox(); TGolDarah.setPreferredSize(new Dimension(40, 23)); TGolDarah.setEditable(false);

        baris1.add(lblNoRw); baris1.add(TNoRw); baris1.add(TNoRM); baris1.add(TPasien);
        baris1.add(lblTglLahir); baris1.add(TTglLahir);
        baris1.add(lblUmur); baris1.add(TUmur);
        baris1.add(lblJK); baris1.add(TJK);
        baris1.add(lblGolDarah); baris1.add(TGolDarah);

        // === Baris 2 ===
        Label lblKdDok = new Label(); lblKdDok.setText("Dokter Anestesi :");
        KdDokter = new TextBox(); KdDokter.setPreferredSize(new Dimension(70, 23));
        NmDokter = new TextBox(); NmDokter.setPreferredSize(new Dimension(150, 23)); NmDokter.setEditable(false);
        Button BtnDokter = new Button();
        BtnDokter.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokter.setPreferredSize(new Dimension(26, 23));

        Label lblKdPenata = new Label(); lblKdPenata.setText("Penata :");
        KdPenata = new TextBox(); KdPenata.setPreferredSize(new Dimension(70, 23));
        NmPenata = new TextBox(); NmPenata.setPreferredSize(new Dimension(140, 23)); NmPenata.setEditable(false);
        Button BtnPenata = new Button();
        BtnPenata.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPenata.setPreferredSize(new Dimension(26, 23));

        Label lblTgl = new Label(); lblTgl.setText("Tanggal :");
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy"); TglAsuhan.setPreferredSize(new Dimension(90, 23));
        Label lblJam = new Label(); lblJam.setText("Jam :");

        String[] jamItems = new String[24];
        for (int ii = 0; ii < 24; ii++) jamItems[ii] = String.format("%02d", ii);
        String[] menitItems = new String[60];
        for (int ii = 0; ii < 60; ii++) menitItems[ii] = String.format("%02d", ii);

        Jam = new ComboBox(); Jam.setModel(new DefaultComboBoxModel(jamItems)); Jam.setPreferredSize(new Dimension(55, 23));
        Menit = new ComboBox(); Menit.setModel(new DefaultComboBoxModel(menitItems)); Menit.setPreferredSize(new Dimension(55, 23));
        Detik = new ComboBox(); Detik.setModel(new DefaultComboBoxModel(menitItems)); Detik.setPreferredSize(new Dimension(55, 23));

        ChkKejadian = new CekBox();
        ChkKejadian.setText("Auto Jam"); ChkKejadian.setSelected(true);
        ChkKejadian.setOpaque(false); ChkKejadian.setPreferredSize(new Dimension(85, 23));

        baris2.add(lblKdDok); baris2.add(KdDokter); baris2.add(NmDokter); baris2.add(BtnDokter);
        baris2.add(lblKdPenata); baris2.add(KdPenata); baris2.add(NmPenata); baris2.add(BtnPenata);
        baris2.add(lblTgl); baris2.add(TglAsuhan);
        baris2.add(lblJam); baris2.add(Jam); baris2.add(Menit); baris2.add(Detik); baris2.add(ChkKejadian);

        // === Baris 3: Penginput ===
        Label lblKdPng = new Label(); lblKdPng.setText("Penginput :");
        KdPenginput = new TextBox(); KdPenginput.setPreferredSize(new Dimension(70, 23));
        NmPenginput = new TextBox(); NmPenginput.setPreferredSize(new Dimension(180, 23)); NmPenginput.setEditable(false);
        Button BtnPegawai = new Button();
        BtnPegawai.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPegawai.setPreferredSize(new Dimension(26, 23));
        baris3.add(lblKdPng); baris3.add(KdPenginput); baris3.add(NmPenginput); baris3.add(BtnPegawai);

        panelAtas.add(baris1);
        panelAtas.add(baris2);
        panelAtas.add(baris3);

        // ChkInput — ditambahkan ke layout dekat tabel (bukan di panel atas)
        ChkInput = new CekBox();
        ChkInput.setIcon(new ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setMnemonic('1'); ChkInput.setText(".: Input Data"); ChkInput.setToolTipText("Alt+1");
        ChkInput.setBorderPainted(true); ChkInput.setBorderPaintedFlat(true); ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(SwingConstants.RIGHT);
        ChkInput.setPreferredSize(new Dimension(100, 20));
        ChkInput.setRolloverIcon(new ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setRolloverSelectedIcon(new ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setSelectedIcon(new ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setSelected(true);
        ChkInput.addActionListener(evt -> isForm());

        // === Dokter dialog events ===
        BtnDokter.addActionListener(evt -> {
            dokterDlg.emptTeks(); dokterDlg.isCek();
            dokterDlg.setSize(800, 500); dokterDlg.setLocationRelativeTo(internalFrame1); dokterDlg.setVisible(true);
        });
        dokterDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (dokterDlg.getTable().getSelectedRow() != -1) {
                    KdDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(), 0).toString());
                    NmDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });
        BtnPenata.addActionListener(evt -> {
            penataDlg.emptTeks(); penataDlg.isCek();
            penataDlg.setSize(800, 500); penataDlg.setLocationRelativeTo(internalFrame1); penataDlg.setVisible(true);
        });
        penataDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (penataDlg.getTable().getSelectedRow() != -1) {
                    KdPenata.setText(penataDlg.getTable().getValueAt(penataDlg.getTable().getSelectedRow(), 0).toString());
                    NmPenata.setText(penataDlg.getTable().getValueAt(penataDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });
        BtnPegawai.addActionListener(evt -> {
            pegawaiDlg.emptTeks();
            pegawaiDlg.setSize(800, 500); pegawaiDlg.setLocationRelativeTo(internalFrame1); pegawaiDlg.setVisible(true);
        });
        pegawaiDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (pegawaiDlg.tbKamar.getSelectedRow() != -1) {
                    KdPenginput.setText(pegawaiDlg.tbKamar.getValueAt(pegawaiDlg.tbKamar.getSelectedRow(), 0).toString());
                    NmPenginput.setText(pegawaiDlg.tbKamar.getValueAt(pegawaiDlg.tbKamar.getSelectedRow(), 1).toString());
                }
            }
        });
        KdPenginput.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String nm = Sequel.cariIsi("SELECT nama FROM pegawai WHERE nip=?", KdPenginput.getText().trim());
                    if (nm != null) NmPenginput.setText(nm);
                }
            }
        });

        // ===== TABS =====
        Tabs = new JTabbedPane();
        Tabs.setFont(new java.awt.Font("Tahoma", 0, 11));

        buildTabPra();
        buildTabPlanOfCare();
        buildTabPraInduksi();
        buildTabPasca();
        buildTabInstruksiRR();
        buildTabDischarge();
        buildTabDurante();
        buildTabCatatan();

        PanelInput = new JPanel(new BorderLayout());
        PanelInput.setOpaque(false); PanelInput.setPreferredSize(new Dimension(1024, 645));
        PanelInput.add(Tabs, BorderLayout.CENTER);

        // ===== PANEL BAWAH (Buttons + shared search) =====
        panelBawah = new panelisi(); panelBawah.setPreferredSize(new Dimension(44, 82));
        panelBawah.setLayout(new java.awt.GridLayout(2, 1, 0, 0));

        JPanel rowBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        rowBtn.setOpaque(false);
        BtnSimpan = new Button(); BtnSimpan.setIcon(new ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setMnemonic('S'); BtnSimpan.setText("Simpan"); BtnSimpan.setPreferredSize(new Dimension(100, 30));
        BtnBatal = new Button(); BtnBatal.setIcon(new ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBatal.setMnemonic('B'); BtnBatal.setText("Baru"); BtnBatal.setPreferredSize(new Dimension(100, 30));
        BtnHapus = new Button(); BtnHapus.setIcon(new ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setMnemonic('H'); BtnHapus.setText("Hapus"); BtnHapus.setPreferredSize(new Dimension(100, 30));
        BtnEdit = new Button(); BtnEdit.setIcon(new ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnEdit.setMnemonic('G'); BtnEdit.setText("Ganti"); BtnEdit.setPreferredSize(new Dimension(100, 30));
        BtnKeluar = new Button(); BtnKeluar.setIcon(new ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K'); BtnKeluar.setText("Keluar"); BtnKeluar.setPreferredSize(new Dimension(100, 30));
        BtnGrafik = new Button(); BtnGrafik.setIcon(new ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnGrafik.setMnemonic('R'); BtnGrafik.setText("Grafik"); BtnGrafik.setPreferredSize(new Dimension(100, 30));
        BtnCetak = new Button(); { java.net.URL u=getClass().getResource("/picture/Print.png"); if(u!=null) BtnCetak.setIcon(new ImageIcon(u)); }
        BtnCetak.setMnemonic('P'); BtnCetak.setText("Cetak"); BtnCetak.setPreferredSize(new Dimension(100, 30));
        rowBtn.add(BtnSimpan); rowBtn.add(BtnBatal); rowBtn.add(BtnHapus);
        rowBtn.add(BtnEdit); rowBtn.add(BtnKeluar); rowBtn.add(BtnGrafik); rowBtn.add(BtnCetak);

        JPanel rowSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 3));
        rowSearch.setOpaque(false);
        DTPCari1 = mkDTP(); DTPCari2 = mkDTP();
        TCari = new TextBox(); TCari.setPreferredSize(new Dimension(130, 23));
        LcntCari = new Label(); LcntCari.setText("0 record");
        Label ltCari = new Label(); ltCari.setText("Tgl:"); ltCari.setPreferredSize(new Dimension(28, 23));
        Label lsCari = new Label(); lsCari.setText("s.d."); lsCari.setPreferredSize(new Dimension(28, 23));
        Label lkCari = new Label(); lkCari.setText("Cari:"); lkCari.setPreferredSize(new Dimension(32, 23));
        Button BtnCari = new Button(); BtnCari.setIcon(new ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnCari.addActionListener(e -> tampil());
        TCari.addKeyListener(mkEnter(() -> tampil()));
        rowSearch.add(ltCari); rowSearch.add(DTPCari1); rowSearch.add(lsCari); rowSearch.add(DTPCari2);
        rowSearch.add(lkCari); rowSearch.add(TCari); rowSearch.add(BtnCari); rowSearch.add(LcntCari);

        panelBawah.add(rowBtn);
        panelBawah.add(rowSearch);

        // ===== ACTIONS =====
        BtnKeluar.addActionListener(evt -> dispose());
        BtnBatal.addActionListener(evt -> emptTeks());
        BtnSimpan.addActionListener(evt -> simpan());
        BtnHapus.addActionListener(evt -> hapus());
        BtnEdit.addActionListener(evt -> ganti());
        BtnGrafik.addActionListener(evt -> {
            DlgGrafikAnestesi dlg = new DlgGrafikAnestesi(null, true);
            dlg.setNoRawat(TNoRw.getText().trim(), TPasien.getText().trim());
            dlg.setVisible(true);
        });
        BtnCetak.addActionListener(evt -> cetakHTML());

        // ===== ASSEMBLY =====
        JPanel topContent = new JPanel(new BorderLayout());
        topContent.setOpaque(false);
        topContent.add(ChkInput, BorderLayout.NORTH);
        topContent.add(PanelInput, BorderLayout.CENTER);

        internalFrame1.add(panelAtas, BorderLayout.PAGE_START);
        internalFrame1.add(topContent, BorderLayout.CENTER);
        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);
        setLayout(new BorderLayout());
        add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
        jam();
    }

    // ===== TAB 1: PRA SEDASI/ANESTESI =====
    private void buildTabPra() {
        FormPra = new PanelBiasa();
        FormPra.setLayout(null);
        FormPra.setPreferredSize(new Dimension(990, 1550));

        int x1 = 10, x2 = 155, w = 380, y = 8;
        java.awt.Font fBold = new java.awt.Font("Tahoma", 1, 11);

        // Header
        JLabel h1 = new JLabel("EVALUASI PRA SEDASI / ANESTESI");
        h1.setFont(fBold); h1.setBounds(x1, y, 400, 20); FormPra.add(h1);

        y += 25;
        addLbl(FormPra, "Prosedur :", x1, y); TProsedur = addTxt(FormPra, x2, y, 400, 23);
        y += 27;
        addLbl(FormPra, "Rhesus :", x1, y); TRhesus = addTxt(FormPra, x2, y, 50, 23);
        y += 27;
        addLbl(FormPra, "BB (kg) :", x1, y); TBB = addTxt(FormPra, x2, y, 60, 23);
        addLblS(FormPra, "TB (cm) :", x2 + 70, y, 75); TTB = addTxt(FormPra, x2 + 150, y, 60, 23);

        // Riwayat
        y += 35;
        JLabel h2 = new JLabel("RIWAYAT SEDASI / ANESTESI / OPERASI");
        h2.setFont(fBold); h2.setBounds(x1, y, 400, 20); FormPra.add(h2);
        y += 22;
        addLbl(FormPra, "Riwayat 1 :", x1, y); TRiwayat1 = addTxt(FormPra, x2, y, w, 23);
        y += 27;
        addLbl(FormPra, "Riwayat 2 :", x1, y); TRiwayat2 = addTxt(FormPra, x2, y, w, 23);
        y += 27;
        addLbl(FormPra, "Riwayat 3 :", x1, y); TRiwayat3 = addTxt(FormPra, x2, y, w, 23);
        y += 27;
        ChkTidakAdaRiwayat = addChk(FormPra, "Tidak Ada Riwayat Penyakit", x2, y, 220);

        // Obat dikonsumsi
        y += 30;
        JLabel h3 = new JLabel("OBAT YANG SEDANG DIKONSUMSI");
        h3.setFont(fBold); h3.setBounds(x1, y, 400, 20); FormPra.add(h3);
        y += 22;
        addLbl(FormPra, "Obat :", x1, y); TObatDikonsumsi = addTxt(FormPra, x2, y, w, 23);
        y += 27;
        ChkTidakAdaObat = addChk(FormPra, "Tidak Ada", x2, y, 120);

        // Alergi
        y += 30;
        JLabel h4 = new JLabel("ALERGI OBAT / BAHAN");
        h4.setFont(fBold); h4.setBounds(x1, y, 300, 20); FormPra.add(h4);
        y += 22;
        addLbl(FormPra, "Alergi :", x1, y); TAlergiObat = addTxt(FormPra, x2, y, w, 23);
        y += 27;
        ChkTidakAdaAlergi = addChk(FormPra, "Tidak Ada Alergi", x2, y, 150);

        // Komplikasi
        y += 30;
        JLabel h5 = new JLabel("KOMPLIKASI ANESTESI");
        h5.setFont(fBold); h5.setBounds(x1, y, 300, 20); FormPra.add(h5);
        y += 22;
        ChkKomplikasiSblm = addChk(FormPra, "Ada komplikasi sebelumnya", x1, y, 220);
        ChkKomplikasiKeluarga = addChk(FormPra, "Ada komplikasi dalam keluarga", x1 + 230, y, 240);

        // TTV Pra
        y += 30;
        JLabel h6 = new JLabel("TANDA-TANDA VITAL PRA SEDASI");
        h6.setFont(fBold); h6.setBounds(x1, y, 350, 20); FormPra.add(h6);
        y += 22;
        addLbl(FormPra, "TD (mmHg) :", x1, y); TTdPra = addTxt(FormPra, x2, y, 80, 23);
        addLblS(FormPra, "Nadi :", x2 + 90, y, 45); TNadiPra = addTxt(FormPra, x2 + 140, y, 60, 23);
        addLblS(FormPra, "RR :", x2 + 210, y, 35); TRespPra = addTxt(FormPra, x2 + 250, y, 60, 23);
        y += 27;
        addLbl(FormPra, "Suhu (°C) :", x1, y); TSuhuPra = addTxt(FormPra, x2, y, 60, 23);
        addLblS(FormPra, "VAS :", x2 + 70, y, 40); TVasPra = addTxt(FormPra, x2 + 115, y, 60, 23);

        // ===== PEMERIKSAAN JALAN NAPAS (2-kolom) =====
        y += 35;
        JLabel h7 = new JLabel("PEMERIKSAAN JALAN NAPAS");
        h7.setFont(fBold); h7.setBounds(x1, y, 600, 20); FormPra.add(h7);
        y += 25;

        // Posisi kolom kiri
        int xL=10, xC2=130, xC3=258;
        // Posisi kolom kanan
        int xM=415, wLbl=285, xYa=703, xTidak=743;
        int rH=22, chH=20;

        // --- Baris 0: header Obstruksi | Buka Mulut ---
        JLabel lbObsH = new JLabel("<html><b>Obstruksi jalan napas</b></html>");
        lbObsH.setBounds(xL, y, 200, chH); FormPra.add(lbObsH);
        JLabel lbBuka = new JLabel("• Buka mulut (jarak 3 jari)");
        lbBuka.setFont(new java.awt.Font("Tahoma", 0, 11)); lbBuka.setBounds(xM, y, wLbl, chH); FormPra.add(lbBuka);
        ChkBukaMulutYa   = addChk(FormPra, "Ya",    xYa,   y, 36);
        ChkBukaMulutTidak= addChk(FormPra, "Tidak", xTidak, y, 52);
        y += rH;

        // --- Baris 1: obs checkboxes row1 | Jarak thyro ---
        ChkObsTidakAda       = addChk(FormPra, "Tidak ada",          xL,  y, 117);
        ChkObsTumor          = addChk(FormPra, "Tumor",              xC2, y, 115);
        ChkObsKelCongenital  = addChk(FormPra, "Kelainan kongenital",xC3, y, 170);
        JLabel lbJarak = new JLabel("• Jarak thyro-metal (3 jari)");
        lbJarak.setFont(new java.awt.Font("Tahoma", 0, 11)); lbJarak.setBounds(xM, y, wLbl, chH); FormPra.add(lbJarak);
        ChkJarakThyroYa   = addChk(FormPra, "Ya",    xYa,   y, 36);
        ChkJarakThyroTidak= addChk(FormPra, "Tidak", xTidak, y, 52);
        y += rH;

        // --- Baris 2: obs checkboxes row2 | Skor Mallampati ---
        ChkObsStridor  = addChk(FormPra, "Stridor",       xC2, y, 115);
        ChkObsGigiProm = addChk(FormPra, "Gigi prominen", xC3, y, 135);
        JLabel lbMallH = new JLabel("<html><b>Skor Mallampati :</b></html>");
        lbMallH.setBounds(xM, y, 130, chH); FormPra.add(lbMallH);
        CmbMallampati = addCmb(FormPra, new String[]{"","I","II","III","IV"}, xM+135, y, 80);
        y += rH;

        // --- Baris 3: header Thoraks | header Pergerakan leher ---
        JLabel lbThorH = new JLabel("<html><b>Thoraks/Abdomen :</b></html>");
        lbThorH.setBounds(xL, y, 200, chH); FormPra.add(lbThorH);
        JLabel lbPerakH = new JLabel("<html><b>Pergerakan leher :</b></html>");
        lbPerakH.setBounds(xM, y, 200, chH); FormPra.add(lbPerakH);
        y += rH;

        // --- Baris 4: thorax row1 | Gerak rahang ---
        ChkThorNormal   = addChk(FormPra, "Dalam batas normal", xL,  y, 118);
        ChkThorHamil    = addChk(FormPra, "Hamil",              xC2, y, 100);
        ChkThorAscites  = addChk(FormPra, "Ascites masif",      xC3, y, 140);
        JLabel lbGerak = new JLabel("• Pasien dapat menggerakkan rahang ke depan?");
        lbGerak.setFont(new java.awt.Font("Tahoma", 0, 11)); lbGerak.setBounds(xM, y, wLbl, chH); FormPra.add(lbGerak);
        ChkGerakRahangYa   = addChk(FormPra, "Ya",    xYa,   y, 36);
        ChkGerakRahangTidak= addChk(FormPra, "Tidak", xTidak, y, 52);
        y += rH;

        // --- Baris 5: thorax row2 | Fleksi ekstensi ---
        ChkThorObstruksi = addChk(FormPra, "Obstruksi usus",  xC2, y, 125);
        ChkThorObesitas  = addChk(FormPra, "Obesitas morbid", xC3, y, 140);
        JLabel lbFleksi = new JLabel("<html>• Pasien dapat fleksi dan ekstensi kepala dan leher?</html>");
        lbFleksi.setFont(new java.awt.Font("Tahoma", 0, 11)); lbFleksi.setBounds(xM, y-2, wLbl, 38); FormPra.add(lbFleksi);
        ChkFleksiYa   = addChk(FormPra, "Ya",    xYa,   y, 36);
        ChkFleksiTidak= addChk(FormPra, "Tidak", xTidak, y, 52);
        y += rH;

        // --- Baris 6: header Wajah | Penyangga leher ---
        JLabel lbWajahH = new JLabel("<html><b>Wajah :</b></html>");
        lbWajahH.setBounds(xL, y, 80, chH); FormPra.add(lbWajahH);
        JLabel lbPenyangga = new JLabel("• Pasien menggunakan penyangga leher?");
        lbPenyangga.setFont(new java.awt.Font("Tahoma", 0, 11)); lbPenyangga.setBounds(xM, y, wLbl, chH); FormPra.add(lbPenyangga);
        ChkPenyanggaYa   = addChk(FormPra, "Ya",    xYa,   y, 36);
        ChkPenyanggaTidak= addChk(FormPra, "Tidak", xTidak, y, 52);
        y += rH;

        // --- Baris 7: wajah row1 | Jalan napas sulit ---
        ChkWajahNormal      = addChk(FormPra, "Dalam batas normal", xL,  y, 118);
        ChkWajahRahangSempit= addChk(FormPra, "Rahang sempit",      xC2, y, 120);
        ChkWajahGigiProm    = addChk(FormPra, "Gigi prominen",      xC3, y, 135);
        JLabel lbJalanSulit = new JLabel("<html><b>Jalan napas sulit :</b></html>");
        lbJalanSulit.setBounds(xM, y, 135, chH); FormPra.add(lbJalanSulit);
        ChkJalanNapasYa   = addChk(FormPra, "Ya",    xM+138, y, 36);
        ChkJalanNapasTidak= addChk(FormPra, "Tidak", xM+177, y, 52);
        y += rH;

        // --- Baris 8: wajah row2 ---
        ChkWajahRambutWajah= addChk(FormPra, "Rambut wajah", xC2, y, 120);
        ChkWajahGigiGoyang = addChk(FormPra, "Gigi goyang",  xC3, y, 120);
        y += rH;

        // --- Baris 9: wajah row3 ---
        ChkWajahEdema    = addChk(FormPra, "Edema",          xC2, y, 80);
        ChkWajahKelRahang= addChk(FormPra, "Kelainan rahang",xC3, y, 145);
        y += 5; // extra gap

        // ===== SISTEM =====
        y += 35;
        JLabel h8 = new JLabel("SISTEM");
        h8.setFont(fBold); h8.setBounds(x1, y, 200, 20); FormPra.add(h8);
        y += 25;

        // column x positions
        int s1c1=5,  s1c2=134, s1n=263;   // Pernapasan
        int s2c1=330, s2c2=459, s2n=588;  // Kardiovaskular
        int s3c1=655, s3c2=784, s3n=913;  // Neuro/Musculoskeletal
        int sw=126;                         // checkbox column width

        // --- ROW 1 HEADERS ---
        JLabel h8a = new JLabel("Pernapasan"); h8a.setFont(fBold); h8a.setBounds(s1c1, y, sw*2, 20); FormPra.add(h8a);
        JLabel h8b = new JLabel("Kardiovaskular"); h8b.setFont(fBold); h8b.setBounds(s2c1, y, sw*2, 20); FormPra.add(h8b);
        JLabel h8c = new JLabel("Neuro/Musculoskeletal"); h8c.setFont(fBold); h8c.setBounds(s3c1, y, sw*2+30, 20); FormPra.add(h8c);
        y += 22;

        int yS1 = y;

        // Pernapasan col1
        ChkNapasAsma           = addChk(FormPra, "Asma",            s1c1, yS1,       sw);
        ChkNapasBronkitis      = addChk(FormPra, "Bronkitis",       s1c1, yS1+rH,    sw);
        ChkNapasPPOK           = addChk(FormPra, "PPOK",            s1c1, yS1+rH*2,  sw);
        ChkNapasOrthopnea      = addChk(FormPra, "Orthopnea",       s1c1, yS1+rH*3,  sw);
        ChkNapasPneumonia      = addChk(FormPra, "Pneumonia",       s1c1, yS1+rH*4,  sw);
        // Pernapasan col2
        ChkNapasBatukProduktif = addChk(FormPra, "Batuk Produktif", s1c2, yS1,       sw);
        ChkNapasSesak          = addChk(FormPra, "Sesak napas",     s1c2, yS1+rH,    sw);
        ChkNapasTuberkulosis   = addChk(FormPra, "Tuberkulosis",    s1c2, yS1+rH*2,  sw);
        // Pernapasan DBN
        JLabel lbPN = new JLabel("<html><center>Dalam<br>Batas<br>normal</center></html>");
        lbPN.setFont(new java.awt.Font("Tahoma",0,10)); lbPN.setBounds(s1n, yS1, 60, 52); FormPra.add(lbPN);
        ChkNapasNormal = new CekBox(); ChkNapasNormal.setText(""); ChkNapasNormal.setOpaque(false);
        ChkNapasNormal.setBounds(s1n+20, yS1+54, 20, 20); FormPra.add(ChkNapasNormal);

        // Kardiovaskular col1
        ChkKardioEKG      = addChk(FormPra, "EKG Abnormal",      s2c1, yS1,       sw);
        ChkKardioAngina   = addChk(FormPra, "Angina",            s2c1, yS1+rH,    sw);
        ChkKardioASHD     = addChk(FormPra, "ASHD",              s2c1, yS1+rH*2,  sw);
        ChkKardioCHF      = addChk(FormPra, "CHF",               s2c1, yS1+rH*3,  sw);
        ChkKardioDisritmia= addChk(FormPra, "Disritmia",         s2c1, yS1+rH*4,  sw);
        ChkKardioLimitasi = addChk(FormPra, "Limitasi Aktifitas",s2c1, yS1+rH*5,  sw);
        // Kardiovaskular col2
        ChkKardioHipertensi    = addChk(FormPra, "Hipertensi",       s2c2, yS1,       sw);
        ChkKardioInfark        = addChk(FormPra, "Infark Miocard",   s2c2, yS1+rH,    sw);
        ChkKardioMurmur        = addChk(FormPra, "Murmur",           s2c2, yS1+rH*2,  sw);
        ChkKardioPacemaker     = addChk(FormPra, "Pacemaker",        s2c2, yS1+rH*3,  sw);
        ChkKardioDemamRheuma   = addChk(FormPra, "Demam Rheuma",     s2c2, yS1+rH*4,  sw);
        ChkKardioPenyakitKatup = addChk(FormPra, "Penyakit Katup",   s2c2, yS1+rH*5,  sw);
        // Kardiovaskular DBN
        JLabel lbKN = new JLabel("<html><center>Dalam<br>Batas<br>normal</center></html>");
        lbKN.setFont(new java.awt.Font("Tahoma",0,10)); lbKN.setBounds(s2n, yS1, 60, 52); FormPra.add(lbKN);
        ChkKardioNormal = new CekBox(); ChkKardioNormal.setText(""); ChkKardioNormal.setOpaque(false);
        ChkKardioNormal.setBounds(s2n+20, yS1+54, 20, 20); FormPra.add(ChkKardioNormal);

        // Neuro col1
        ChkNeuroArthritis          = addChk(FormPra, "Arthritis",             s3c1, yS1,       sw);
        ChkNeuroCVA                = addChk(FormPra, "CVA/Stroke/TIA",        s3c1, yS1+rH,    sw);
        ChkNeuroDegeneratif        = addChk(FormPra, "Penyakit Degeneratif",  s3c1, yS1+rH*2,  sw);
        ChkNeuroNyeriKepala        = addChk(FormPra, "Nyeri kepala/\u2191TIK",s3c1, yS1+rH*3,  sw);
        ChkNeuroPenurunanKesadaran = addChk(FormPra, "Penurunan kesadaran",   s3c1, yS1+rH*4,  sw);
        ChkNeuroGangguanTulang     = addChk(FormPra, "Gang. Tulang Belakang", s3c1, yS1+rH*5,  sw);
        // Neuro col2
        ChkNeuroKelemahanOtot = addChk(FormPra, "Kelemahan Otot", s3c2, yS1,       sw);
        ChkNeuroParalisis     = addChk(FormPra, "Paralisis",       s3c2, yS1+rH,    sw);
        ChkNeuroParesthesia   = addChk(FormPra, "Paresthesia",     s3c2, yS1+rH*2,  sw);
        ChkNeuroPingsan       = addChk(FormPra, "Pingsan",         s3c2, yS1+rH*3,  sw);
        ChkNeuroKejang        = addChk(FormPra, "Kejang",          s3c2, yS1+rH*4,  sw);
        // Neuro DBN
        JLabel lbNN = new JLabel("<html><center>Dalam<br>Batas<br>normal</center></html>");
        lbNN.setFont(new java.awt.Font("Tahoma",0,10)); lbNN.setBounds(s3n, yS1, 60, 52); FormPra.add(lbNN);
        ChkNeuroNormal = new CekBox(); ChkNeuroNormal.setText(""); ChkNeuroNormal.setOpaque(false);
        ChkNeuroNormal.setBounds(s3n+20, yS1+54, 20, 20); FormPra.add(ChkNeuroNormal);

        y = yS1 + rH*6 + 12;

        // --- ROW 2 HEADERS ---
        JLabel h8d = new JLabel("Renal / Endokrin"); h8d.setFont(fBold); h8d.setBounds(s1c1, y, sw*2, 20); FormPra.add(h8d);
        JLabel h8e = new JLabel("Hepato/Gastrointestinal"); h8e.setFont(fBold); h8e.setBounds(s2c1, y, sw*2+20, 20); FormPra.add(h8e);
        JLabel h8f = new JLabel("Lain - lain"); h8f.setFont(fBold); h8f.setBounds(s3c1, y, sw*2, 20); FormPra.add(h8f);
        y += 22;

        int yS2 = y;

        // Renal col1
        ChkRenalDiabetes       = addChk(FormPra, "Diabetes",              s1c1, yS2,       sw);
        ChkRenalGagalGinjal    = addChk(FormPra, "Gagal ginjal",          s1c1, yS2+rH,    sw);
        ChkRenalTiroid         = addChk(FormPra, "Penyakit Tiroid",       s1c1, yS2+rH*2,  sw);
        ChkRenalRetensiUrin    = addChk(FormPra, "Retensi Urin",          s1c1, yS2+rH*3,  sw);
        ChkRenalInfeksiSaluran = addChk(FormPra, "Infeksi Sal. Kemih",   s1c1, yS2+rH*4,  sw);
        ChkRenalPenurunanBB    = addChk(FormPra, "Penurunan/+BB",         s1c1, yS2+rH*5,  sw);
        // Renal DBN
        JLabel lbRN = new JLabel("<html><center>Dalam<br>Batas<br>normal</center></html>");
        lbRN.setFont(new java.awt.Font("Tahoma",0,10)); lbRN.setBounds(s1n, yS2, 60, 52); FormPra.add(lbRN);
        ChkRenalNormal = new CekBox(); ChkRenalNormal.setText(""); ChkRenalNormal.setOpaque(false);
        ChkRenalNormal.setBounds(s1n+20, yS2+54, 20, 20); FormPra.add(ChkRenalNormal);

        // Hepato col1
        ChkHepatoObstruksiUsus = addChk(FormPra, "Obstruksi Usus",      s2c1, yS2,       sw);
        ChkHepatoSirosis       = addChk(FormPra, "Sirosis",              s2c1, yS2+rH,    sw);
        ChkHepatoHepatitis     = addChk(FormPra, "Hepatitis/Ikterus",    s2c1, yS2+rH*2,  sw);
        ChkHepatoHiatal        = addChk(FormPra, "Hiatal Hernia/Reflux", s2c1, yS2+rH*3,  sw);
        ChkHepatoMualMuntah    = addChk(FormPra, "Mual/Muntah",          s2c1, yS2+rH*4,  sw);
        ChkHepatoUlkus         = addChk(FormPra, "Ulkus",                s2c1, yS2+rH*5,  sw);
        // Hepato DBN
        JLabel lbHN = new JLabel("<html><center>Dalam<br>Batas<br>normal</center></html>");
        lbHN.setFont(new java.awt.Font("Tahoma",0,10)); lbHN.setBounds(s2n, yS2, 60, 52); FormPra.add(lbHN);
        ChkHepatoNormal = new CekBox(); ChkHepatoNormal.setText(""); ChkHepatoNormal.setOpaque(false);
        ChkHepatoNormal.setBounds(s2n+20, yS2+54, 20, 20); FormPra.add(ChkHepatoNormal);

        // Lain col1
        ChkLainAnemia             = addChk(FormPra, "Anemia",             s3c1, yS2,       sw);
        ChkLainDehidrasi          = addChk(FormPra, "Dehidrasi",          s3c1, yS2+rH,    sw);
        ChkLainHemofilia          = addChk(FormPra, "Hemofilia",          s3c1, yS2+rH*2,  sw);
        ChkLainImunosupresi       = addChk(FormPra, "Imunosupresi",       s3c1, yS2+rH*3,  sw);
        ChkLainKanker             = addChk(FormPra, "Kanker",             s3c1, yS2+rH*4,  sw);
        ChkLainGangguanPerdarahan = addChk(FormPra, "Gang. Perdarahan",   s3c1, yS2+rH*5,  sw);
        // Lain col2
        ChkLainKehamilan        = addChk(FormPra, "Kehamilan",          s3c2, yS2,       sw);
        ChkLainKemoterapi       = addChk(FormPra, "Kemoterapi",         s3c2, yS2+rH,    sw);
        ChkLainSickleCell       = addChk(FormPra, "Sickle Cell Dis.",   s3c2, yS2+rH*2,  sw);
        ChkLainRiwayatSteroid   = addChk(FormPra, "Riwayat Steroid",    s3c2, yS2+rH*3,  sw);
        ChkLainRiwayatTransfusi = addChk(FormPra, "Riwayat Transfusi",  s3c2, yS2+rH*4,  sw);
        // Lain DBN
        JLabel lbLN = new JLabel("<html><center>Dalam<br>Batas<br>normal</center></html>");
        lbLN.setFont(new java.awt.Font("Tahoma",0,10)); lbLN.setBounds(s3n, yS2, 60, 52); FormPra.add(lbLN);
        ChkLainNormal = new CekBox(); ChkLainNormal.setText(""); ChkLainNormal.setOpaque(false);
        ChkLainNormal.setBounds(s3n+20, yS2+54, 20, 20); FormPra.add(ChkLainNormal);

        y = yS2 + rH*6 + 12;

        // ===== LAIN-LAIN (Merokok/Alkohol) =====
        y += 30;
        JLabel hLainLain = new JLabel("Lain - lain");
        hLainLain.setFont(fBold); hLainLain.setBounds(x1, y, 200, 20); FormPra.add(hLainLain);
        y += 25;

        addLblS(FormPra, "Merokok  :", x1, y, 75);
        ChkMerokokTidak = addChk(FormPra, "Tidak", x1+78, y, 60);
        ChkMerokok      = addChk(FormPra, "Ya",    x1+140, y, 42);
        addLblS(FormPra, "pak/hari,", x1+185, y, 65); TMerokokPak = addTxt(FormPra, x1+253, y, 65, 23);
        addLblS(FormPra, "selama", x1+323, y, 48); TMerokokTahun = addTxt(FormPra, x1+374, y, 60, 23);
        addLblS(FormPra, "tahun", x1+440, y, 45);
        y += 27;

        addLblS(FormPra, "Alkohol  :", x1, y, 75);
        ChkAlkoholTidak = addChk(FormPra, "Tidak", x1+78, y, 60);
        ChkAlkohol      = addChk(FormPra, "Ya",    x1+140, y, 42);
        addLblS(FormPra, "Selama :", x1+185, y, 60); TAlkohol = addTxt(FormPra, x1+248, y, 270, 23);

        // ===== PEMERIKSAAN PENUNJANG =====
        y += 35;
        JLabel h9 = new JLabel("PEMERIKSAAN PENUNJANG");
        h9.setFont(fBold); h9.setBounds(x1, y, 300, 20); FormPra.add(h9);
        y += 25;
        ChkPenunjangEKG         = addChk(FormPra, "EKG",                   x1,       y, 200);
        ChkPenunjangEvaluasiParu= addChk(FormPra, "Evaluasi Paru",         x1+205,   y, 180);
        y += 23;
        ChkPenunjangXRay        = addChk(FormPra, "Chest X-Ray / CT / MRI",x1,       y, 210);
        addLblS(FormPra, "Lainnya :", x1+215, y, 65); TPenunjangLain = addTxt(FormPra, x1+283, y, 270, 23);

        // ===== LABORATORIUM =====
        y += 35;
        JLabel h10 = new JLabel("LABORATORIUM");
        h10.setFont(fBold); h10.setBounds(x1, y, 200, 20); FormPra.add(h10);
        y += 25;
        ChkLabHbHt      = addChk(FormPra, "Hb / Ht / CBC", x1,       y, 200);
        ChkLabUrinalisis= addChk(FormPra, "Urinalisis",     x1+205,   y, 160);
        y += 23;
        ChkLabElektrolit= addChk(FormPra, "Elektrolit",     x1,       y, 200);
        addLblS(FormPra, "Lainnya :", x1+205, y, 65); TLabLain = addTxt(FormPra, x1+273, y, 270, 23);
        TLab = new TextBox(); // kept for compatibility, not shown

        // Masalah
        y += 35;
        JLabel h11 = new JLabel("DAFTAR MASALAH");
        h11.setFont(fBold); h11.setBounds(x1, y, 250, 20); FormPra.add(h11);
        y += 22;
        addLbl(FormPra, "Masalah 1 :", x1, y); TMasalah1 = addTxt(FormPra, x2, y, w, 23);
        y += 27;
        addLbl(FormPra, "Masalah 2 :", x1, y); TMasalah2 = addTxt(FormPra, x2, y, w, 23);
        y += 27;
        addLbl(FormPra, "Masalah 3 :", x1, y); TMasalah3 = addTxt(FormPra, x2, y, w, 23);

        // ASA & Pra Medikasi
        y += 35;
        addLbl(FormPra, "ASA Grade :", x1, y);
        CmbASA = addCmb(FormPra, new String[]{"I","II","III","IV","V","VI"}, x2, y, 70);
        y += 27;
        addLbl(FormPra, "Pra Medikasi :", x1, y); TPraMedikasi = addTxt(FormPra, x2, y, w, 23);

        FormPra.setPreferredSize(new Dimension(990, y + 40));

        ScrollPane sp1 = new ScrollPane(); sp1.setViewportView(FormPra);
        tabModePra = buildTM("No.Rawat","Pasien","Tanggal","Jam","Prosedur","ASA Grade","TD","Nadi","Suhu","Dokter Anestesi","Penata","Penginput");
        tbPra = buildTB(tabModePra, () -> getDataPra());
        Tabs.addTab("Pra Sedasi/Anestesi", mkTabPanel(sp1, tbPra));
    }

    // ===== TAB 2: PLAN OF CARE =====
    private void buildTabPlanOfCare() {
        PanelBiasa FormPOC = new PanelBiasa();
        FormPOC.setLayout(null);

        int x1 = 10, y = 15;
        java.awt.Font fBold2 = new java.awt.Font("Tahoma", 1, 11);

        JLabel title = new JLabel("II. PLAN OF CARE  SEDASI/ANESTESI");
        title.setFont(new java.awt.Font("Tahoma", 1, 12));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setBounds(x1, y, 960, 22); FormPOC.add(title);

        // --- Teknik Sedasi/Anestesi ---
        y += 40;
        JLabel h1 = new JLabel("Teknik sedasi / anestesi yang telah direncanakan :");
        h1.setFont(fBold2); h1.setBounds(x1, y, 500, 20); FormPOC.add(h1);
        y += 26;
        ChkTeknikMAC           = addChk(FormPOC, "MAC",           x1,       y, 130);
        ChkTeknikSedasi        = addChk(FormPOC, "Sedasi",        x1+140,   y, 130);
        ChkTeknikAnestesiUmum  = addChk(FormPOC, "Anestesi Umum", x1+280,   y, 150);
        y += 26;
        ChkTeknikRegional      = addChk(FormPOC, "Regional",      x1,       y, 130);
        ChkTeknikKombinasi     = addChk(FormPOC, "Kombinasi",     x1+140,   y, 130);
        y += 26;
        ChkTeknikSpinal        = addChk(FormPOC, "Spinal",        x1,       y, 130);
        ChkTeknikEpidural      = addChk(FormPOC, "Epidural",      x1+140,   y, 130);
        ChkTeknikPeriperal     = addChk(FormPOC, "Periperal",     x1+280,   y, 120);
        addLblS(FormPOC, "Lainnya :", x1+410, y, 65); TTeknikLain = addTxt(FormPOC, x1+478, y, 460, 23);
        y += 26;
        addLblS(FormPOC, "Teknik alternatif :", x1, y, 140); TTeknikAlternatif = addTxt(FormPOC, x1+143, y, 795, 23);

        // --- Monitoring Alat Invasif ---
        y += 55;
        JLabel h2 = new JLabel("Monitoring alat invasif yang telah direncanakan :");
        h2.setFont(fBold2); h2.setBounds(x1, y, 500, 20); FormPOC.add(h2);
        y += 26;
        ChkMonitorALine = addChk(FormPOC, "A Line", x1,       y, 120);
        ChkMonitorCVC   = addChk(FormPOC, "CVC",    x1+130,   y, 100);
        ChkMonitorPAC   = addChk(FormPOC, "PAC",    x1+240,   y, 100);
        ChkMonitorTEE   = addChk(FormPOC, "TEE",    x1+350,   y, 100);
        addLblS(FormPOC, "Lainnya :", x1+460, y, 65); TMonitorLain = addTxt(FormPOC, x1+528, y, 410, 23);

        // --- Kontrol Nyeri ---
        y += 55;
        JLabel h3 = new JLabel("Kontrol nyeri yang telah direncanakan :");
        h3.setFont(fBold2); h3.setBounds(x1, y, 400, 20); FormPOC.add(h3);
        y += 26;
        ChkKontrolIntravena = addChk(FormPOC, "Intravena", x1,       y, 130);
        ChkKontrolEpidural  = addChk(FormPOC, "Epidural",  x1+140,   y, 120);
        ChkKontrolSpinal    = addChk(FormPOC, "Spinal",    x1+270,   y, 110);
        ChkKontrolPCA       = addChk(FormPOC, "PCA",       x1+390,   y, 100);
        addLblS(FormPOC, "Lainnya :", x1+500, y, 65); TKontrolLain = addTxt(FormPOC, x1+568, y, 370, 23);

        // --- Risiko ---
        y += 55;
        JLabel h4 = new JLabel("Risiko yang mungkin terjadi :");
        h4.setFont(fBold2); h4.setBounds(x1, y, 300, 20); FormPOC.add(h4);
        y += 26;
        TRisiko = new JTextArea(5, 40);
        TRisiko.setFont(new java.awt.Font("Tahoma", 0, 11));
        TRisiko.setLineWrap(true); TRisiko.setWrapStyleWord(true);
        JScrollPane spRisiko = new JScrollPane(TRisiko);
        spRisiko.setBounds(x1, y, 950, 110); FormPOC.add(spRisiko);
        y += 110;

        FormPOC.setPreferredSize(new Dimension(990, y + 30));

        ScrollPane spPOC = new ScrollPane(); spPOC.setViewportView(FormPOC);
        tabModePOC = buildTM("No.Rawat","Pasien","Tanggal","Jam","Teknik Anestesi","Monitor Invasif","Kontrol Nyeri","Risiko","Dokter Anestesi","Penata","Penginput");
        tbPOC = buildTB(tabModePOC, () -> getDataPOC());
        Tabs.addTab("Plan of Care", mkTabPanel(spPOC, tbPOC));
    }

    // ===== TAB 3: PRA INDUKSI & DURANTE =====
    private void buildTabPraInduksi() {
        FormPraInd = new PanelBiasa();
        FormPraInd.setLayout(null);

        int xL=10, wL=440, xR=462, wR=508;
        int xFull=10, wFull=962;
        java.awt.Font fBold = new java.awt.Font("Tahoma", 1, 11);
        java.awt.Font fNorm = new java.awt.Font("Tahoma", 0, 11);
        int y = 8;
        int x2 = 155;

        // ===== SECTION A: Pengkajian Pra Induksi =====
        JLabel hA = new JLabel("A. Pengkajian Pra Induksi");
        hA.setFont(fBold); hA.setBounds(xFull, y, 400, 20); FormPraInd.add(hA);

        y += 22;
        addLbl(FormPraInd, "Dokter Operator :", xL, y);
        KdDokterOp = addTxt(FormPraInd, x2, y, 70, 23);
        TDokterOperator = addTxt(FormPraInd, x2+75, y, 290, 23); TDokterOperator.setEditable(false);
        Button BtnDokterOp = new Button();
        BtnDokterOp.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokterOp.setPreferredSize(new Dimension(26, 23));
        BtnDokterOp.setBounds(x2+370, y, 26, 23); FormPraInd.add(BtnDokterOp);
        BtnDokterOp.addActionListener(evt -> {
            dokterOpDlg.emptTeks(); dokterOpDlg.isCek();
            dokterOpDlg.setSize(800, 500); dokterOpDlg.setLocationRelativeTo(internalFrame1); dokterOpDlg.setVisible(true);
        });
        dokterOpDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (dokterOpDlg.getTable().getSelectedRow() != -1) {
                    KdDokterOp.setText(dokterOpDlg.getTable().getValueAt(dokterOpDlg.getTable().getSelectedRow(), 0).toString());
                    TDokterOperator.setText(dokterOpDlg.getTable().getValueAt(dokterOpDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

        y += 25;
        addLbl(FormPraInd, "Prosedur Operasi :", xL, y); TProsedurOperasi = addTxt(FormPraInd, x2, y, 500, 23);
        y += 25;
        addLbl(FormPraInd, "Ruang OT :", xL, y); TRuangOT = addTxt(FormPraInd, x2, y, 150, 23);

        y += 25;
        ChkPasienDiidentifikasi  = addChk(FormPraInd, "Pasien sudah diidentifikasi",  xFull,        y, 210);
        ChkRekamMedis            = addChk(FormPraInd, "Rekam Medis sudah dibaca ulang", xFull+215,   y, 215);
        addLblS(FormPraInd, "Mallampati :", xFull+435, y, 80);
        ChkMallampatiI   = addChk(FormPraInd, "I",   xFull+517, y, 35);
        ChkMallampatiII  = addChk(FormPraInd, "II",  xFull+554, y, 38);
        ChkMallampatiIII = addChk(FormPraInd, "III", xFull+594, y, 42);
        ChkMallampatiIV  = addChk(FormPraInd, "IV",  xFull+638, y, 38);

        y += 22;
        ChkInformedConsent = addChk(FormPraInd, "Informed Consent sudah ditandatangani", xFull,      y, 280);
        addLblS(FormPraInd, "Puasa :", xFull+285, y, 50);
        ChkPuasa   = addChk(FormPraInd, "", xFull+337, y, 20);
        TPuasaJam  = addTxt(FormPraInd, xFull+359, y, 60, 23);
        addLblS(FormPraInd, "Jam", xFull+422, y, 35);

        y += 25;
        addLblS(FormPraInd, "Kondisi Pasien :", xFull, y, 110);
        ChkKondisiTenang         = addChk(FormPraInd, "Tenang",          xFull+112, y, 80);
        ChkKondisiSadar          = addChk(FormPraInd, "Sadar",           xFull+194, y, 70);
        ChkKondisiCemas          = addChk(FormPraInd, "Cemas",           xFull+266, y, 70);
        ChkKondisiDisorientasi   = addChk(FormPraInd, "Disorientasi",    xFull+338, y, 110);
        ChkKondisiTdkKooperatif  = addChk(FormPraInd, "Tidak Kooperatif",xFull+450, y, 135);
        ChkKondisiTdkRespon      = addChk(FormPraInd, "Tidak Respon",    xFull+587, y, 120);

        y += 25;
        addLblS(FormPraInd, "TD :", xFull, y, 30);
        TTdPraInd   = addTxt(FormPraInd, xFull+32, y, 70, 23);
        addLblS(FormPraInd, "N :", xFull+105, y, 25);
        TNadiPraInd = addTxt(FormPraInd, xFull+132, y, 70, 23);
        addLblS(FormPraInd, "Resp :", xFull+205, y, 45);
        TRespPraInd = addTxt(FormPraInd, xFull+252, y, 60, 23);
        addLblS(FormPraInd, "SpO2 :", xFull+315, y, 45);
        TSpo2       = addTxt(FormPraInd, xFull+362, y, 60, 23);
        addLblS(FormPraInd, "Alergi :", xFull+425, y, 55);
        TAlergiPra  = addTxt(FormPraInd, xFull+482, y, 200, 23);

        // Separator
        y += 30;
        javax.swing.JSeparator sep = new javax.swing.JSeparator();
        sep.setBounds(xFull, y, wFull, 2); FormPraInd.add(sep);
        y += 8;

        // ===== SECTION B + C (side by side) =====
        int yB = y, yC = y;

        // --- SECTION B: Evaluasi Keselamatan Pasien (LEFT) ---
        JLabel hB = new JLabel("B. Evaluasi Keselamatan Pasien");
        hB.setFont(fBold); hB.setBounds(xL, yB, 300, 20); FormPraInd.add(hB);
        yB += 22;
        ChkPengecekanMesin   = addChk(FormPraInd, "Pengecekan mesin anestesi",         xL, yB, wL); yB += 22;
        ChkTaliPengaman      = addChk(FormPraInd, "Tali pengaman terpasang",            xL, yB, wL); yB += 22;
        ChkPenyanggaLengan   = addChk(FormPraInd, "Penyangga lengan",                   xL, yB, wL); yB += 22;
        ChkPressurePoint     = addChk(FormPraInd, "Pressure point checked & padded",    xL, yB, wL); yB += 22;
        ChkPenyanggaKetiak   = addChk(FormPraInd, "Penyangga ketiak",                   xL, yB, wL); yB += 22;
        ChkTanganTerlindungi = addChk(FormPraInd, "Tangan terlindungi",                 xL, yB, wL); yB += 25;
        addLblS(FormPraInd, "Perawatan Mata :", xL, yB, 110);
        ChkMatOintment = addChk(FormPraInd, "Ointment", xL+112, yB, 90);
        ChkMatSaline   = addChk(FormPraInd, "Saline",   xL+204, yB, 75);
        ChkMatTapped   = addChk(FormPraInd, "Tapped",   xL+281, yB, 75);
        yB += 22;
        ChkMatPads    = addChk(FormPraInd, "Pads",    xL+20, yB, 75);
        ChkMatGoggles = addChk(FormPraInd, "Goggles", xL+97, yB, 90);
        yB += 22;

        // --- SECTION C: Alat Monitoring (RIGHT) ---
        JLabel hC = new JLabel("C. Alat Monitoring");
        hC.setFont(fBold); hC.setBounds(xR, yC, 300, 20); FormPraInd.add(hC);
        yC += 22;
        ChkMonStetoskop  = addChk(FormPraInd, "Stetoskop",          xR,        yC, 120);
        ChkMonPrekordial = addChk(FormPraInd, "Prekordial stetoskop",xR+122,    yC, 180); yC += 22;
        ChkMonNIBP       = addChk(FormPraInd, "NIBP",               xR,        yC, 70);
        ChkMonNIBPKiri   = addChk(FormPraInd, "Kiri",               xR+72,     yC, 60);
        ChkMonNIBPKanan  = addChk(FormPraInd, "Kanan",              xR+134,    yC, 70); yC += 22;
        ChkMonEKG        = addChk(FormPraInd, "EKG,",               xR,        yC, 55);
        addLblS(FormPraInd, "Lead", xR+57, yC, 38);
        TEKGLead         = addTxt(FormPraInd, xR+97, yC, 70, 23);
        ChkMonEsoph      = addChk(FormPraInd, "Esoph",              xR+170,    yC, 80); yC += 22;
        ChkMonPulseOxy   = addChk(FormPraInd, "Pulse oxymeter",     xR,        yC, 140);
        ChkMonSensorO2   = addChk(FormPraInd, "Sensor oksigen",     xR+142,    yC, 140); yC += 22;
        ChkMonSelimut    = addChk(FormPraInd, "Selimut penghangat", xR,        yC, 160);
        ChkMonGasAnalyzer= addChk(FormPraInd, "Gas analyzer",       xR+162,    yC, 140); yC += 22;
        ChkMonEEG        = addChk(FormPraInd, "EEG",                xR,        yC, 70);
        ChkMonNerveStimulator = addChk(FormPraInd, "Nerve stimulator", xR+72,  yC, 160); yC += 22;
        ChkMonKateter    = addChk(FormPraInd, "Kateter",            xR,        yC, 90);
        ChkMonDoppler    = addChk(FormPraInd, "Doppler",            xR+92,     yC, 100); yC += 22;
        ChkMonNGTOGT     = addChk(FormPraInd, "NGT/OGT tube",       xR,        yC, 130);
        ChkMonBIS        = addChk(FormPraInd, "BIS",                xR+132,    yC, 70);
        ChkMonIntravena  = addChk(FormPraInd, "Intravena",          xR+204,    yC, 90); yC += 22;
        ChkMonArteriLine = addChk(FormPraInd, "Arteri line",        xR,        yC, 110);
        ChkMonCVC        = addChk(FormPraInd, "CVC",                xR+112,    yC, 70);
        ChkMonPAC        = addChk(FormPraInd, "PAC",                xR+184,    yC, 70); yC += 22;

        y = Math.max(yB, yC) + 10;

        // Separator
        javax.swing.JSeparator sep2 = new javax.swing.JSeparator();
        sep2.setBounds(xFull, y, wFull, 2); FormPraInd.add(sep2);
        y += 8;

        // ===== SECTION D: Teknik Sedasi / Anestesi =====
        JLabel hD = new JLabel("D. Teknik Sedasi / Anestesi");
        hD.setFont(fBold); hD.setBounds(xFull, y, 400, 20); FormPraInd.add(hD);
        y += 22;
        addLblS(FormPraInd, "Umum :", xFull, y, 55);
        ChkTUPreOxy       = addChk(FormPraInd, "Pre-Oxygenation",           xFull+57,  y, 160);
        ChkTUIntravena    = addChk(FormPraInd, "Intravena",                 xFull+219, y, 100);
        ChkTUInhalasi     = addChk(FormPraInd, "Inhalasi",                  xFull+321, y, 100);
        ChkTUIntramuskular= addChk(FormPraInd, "Intramuskular",             xFull+423, y, 140);
        y += 22;
        ChkTURSI   = addChk(FormPraInd, "Rapid Sequence Induction", xFull+70, y, 220);
        ChkTUCricoid = addChk(FormPraInd, "Cricoid Pressure",        xFull+292, y, 180);
        y += 22;
        addLblS(FormPraInd, "Sedasi :", xFull, y, 60);
        ChkSedasiRingan = addChk(FormPraInd, "Ringan", xFull+62, y, 100);
        ChkSedasiSedang = addChk(FormPraInd, "Sedang", xFull+164, y, 100);
        ChkSedasiDalam  = addChk(FormPraInd, "Dalam",  xFull+266, y, 100);
        y += 22;
        addLblS(FormPraInd, "Regional :", xFull, y, 70);
        TTeknikRegional  = addTxt(FormPraInd, xFull+72, y, 800, 23);
        y += 25;
        TTeknikRegional2 = addTxt(FormPraInd, xFull+72, y, 800, 23);

        // Separator
        y += 30;
        javax.swing.JSeparator sep3 = new javax.swing.JSeparator();
        sep3.setBounds(xFull, y, wFull, 2); FormPraInd.add(sep3);
        y += 8;

        // ===== SECTION E + F (side by side) =====
        int yE = y, yF = y;

        // --- SECTION E: Manajemen Jalan Napas (LEFT) ---
        JLabel hE = new JLabel("E. Manajemen Jalan Napas");
        hE.setFont(fBold); hE.setBounds(xL, yE, 300, 20); FormPraInd.add(hE);
        yE += 22;
        ChkJNPernapasanSpontan = addChk(FormPraInd, "Pernapasan spontan", xL, yE, wL); yE += 22;
        ChkJNNasalO2           = addChk(FormPraInd, "Nasal O2",           xL, yE, wL); yE += 22;
        ChkJNMask              = addChk(FormPraInd, "Mask",               xL, yE, wL); yE += 22;
        ChkJNSupraglotic       = addChk(FormPraInd, "Supraglotic airway device (LMA, Combitube, dll)", xL, yE, wL); yE += 22;
        addLblS(FormPraInd, "Jenis :", xL+20, yE, 50);
        TJNJenis  = addTxt(FormPraInd, xL+72, yE, 100, 23);
        addLblS(FormPraInd, "Ukuran :", xL+175, yE, 60);
        TJNUkuran = addTxt(FormPraInd, xL+237, yE, 70, 23); yE += 22;
        ChkJNTrakeostomi = addChk(FormPraInd, "Trakeostomi", xL, yE, wL); yE += 22;
        ChkJNIntubasi    = addChk(FormPraInd, "Intubasi",    xL,      yE, 90);
        ChkJNNasal       = addChk(FormPraInd, "Nasal",       xL+92,   yE, 65);
        ChkJNOral        = addChk(FormPraInd, "Oral",        xL+159,  yE, 60); yE += 22;
        ChkJNSleep    = addChk(FormPraInd, "Sleep",    xL+20, yE, 70);
        ChkJNApnea    = addChk(FormPraInd, "Apnea",    xL+92, yE, 75);
        ChkJNDirect   = addChk(FormPraInd, "Direct",   xL+169,yE, 75); yE += 22;
        ChkJNAwake    = addChk(FormPraInd, "Awake",    xL+20, yE, 70);
        ChkJNNonApnea = addChk(FormPraInd, "Non Apnea",xL+92, yE, 95);
        ChkJNBlind    = addChk(FormPraInd, "Blind",    xL+189,yE, 70); yE += 22;
        addLblS(FormPraInd, "ETT", xL+20, yE, 35);
        ChkETTRegular  = addChk(FormPraInd, "Regular",              xL+57,  yE, 90);
        ChkETTReformed = addChk(FormPraInd, "Re-informed / Armored", xL+149, yE, 170); yE += 22;
        ChkETTPreFormedBend  = addChk(FormPraInd, "Pre-formed bend",     xL+57,  yE, 140);
        ChkETTDoubleLumenL   = addChk(FormPraInd, "Double lumen tube L", xL+199, yE, 165);
        ChkETTDoubleLumenR   = addChk(FormPraInd, "R",                   xL+366, yE, 35); yE += 22;
        addLblS(FormPraInd, "Size :", xL+57, yE, 42);
        TETTSize  = addTxt(FormPraInd, xL+101, yE, 60, 23);
        addLblS(FormPraInd, "Batas :", xL+165, yE, 48);
        TETTBatas = addTxt(FormPraInd, xL+215, yE, 60, 23);
        addLblS(FormPraInd, "Cm", xL+278, yE, 30); yE += 22;
        ChkETTCuffed   = addChk(FormPraInd, "Cuffed",   xL+57, yE, 90);
        ChkETTUncuffed = addChk(FormPraInd, "Uncuffed", xL+149,yE, 90); yE += 22;
        ChkETTUdara = addChk(FormPraInd, "Udara", xL+57, yE, 80);
        ChkETTAir   = addChk(FormPraInd, "Air",   xL+139,yE, 60); yE += 22;
        ChkJNStyle = addChk(FormPraInd, "Style",  xL, yE, wL); yE += 22;
        ChkJNMagill= addChk(FormPraInd, "Magill", xL, yE, wL); yE += 22;
        ChkJNLaryngoscope = addChk(FormPraInd, "Laryngoscope", xL, yE, wL); yE += 22;
        addLblS(FormPraInd, "Blade : Macintosh / Miller / Mc Coy*", xL+20, yE, 250);
        addLblS(FormPraInd, "Ukuran :", xL+272, yE, 60);
        TLarynUkuran = addTxt(FormPraInd, xL+334, yE, 80, 23); yE += 22;
        ChkJNVideoLaryngoscope = addChk(FormPraInd, "Video laryngoscope", xL, yE, wL); yE += 22;
        addLblS(FormPraInd, "Blade : Macintosh / Miller / Mc Coy*", xL+20, yE, 250);
        addLblS(FormPraInd, "Ukuran :", xL+272, yE, 60);
        TVLUkuran = addTxt(FormPraInd, xL+334, yE, 80, 23); yE += 22;
        ChkJNFiberoptic = addChk(FormPraInd, "Fiberoptic laryngoscope", xL, yE, wL); yE += 22;
        addLblS(FormPraInd, "Percobaan Ke :", xL+20, yE, 100);
        TFiberPercobaan = addTxt(FormPraInd, xL+122, yE, 80, 23); yE += 22;
        addLblS(FormPraInd, "Suara Napas :", xL, yE, 95);
        ChkSuaraSama      = addChk(FormPraInd, "Sama",       xL+97,  yE, 75);
        ChkSuaraTidakSama = addChk(FormPraInd, "Tidak sama", xL+174, yE, 110); yE += 22;

        // --- SECTION F: Cairan (RIGHT) ---
        JLabel hF = new JLabel("F. Cairan, Transfusi, Kehilangan Darah Intra-operatif");
        hF.setFont(fBold); hF.setBounds(xR, yF, wR, 20); FormPraInd.add(hF);
        yF += 22;
        JLabel lblIntake = new JLabel("Intake :"); lblIntake.setFont(fBold);
        lblIntake.setBounds(xR, yF, 80, 20); FormPraInd.add(lblIntake); yF += 18;
        JLabel lblCairan = new JLabel("Cairan :"); lblCairan.setFont(fBold);
        lblCairan.setBounds(xR, yF, 80, 20); FormPraInd.add(lblCairan); yF += 20;
        ChkKoloid     = addChk(FormPraInd, "Koloid",     xR,      yF, 90);
        addLblS(FormPraInd, ":",            xR+92,  yF, 10);
        TCairanKoloid = addTxt(FormPraInd, xR+105, yF, 200, 23);
        addLblS(FormPraInd, "ml", xR+308, yF, 30); yF += 25;
        ChkKristaloid     = addChk(FormPraInd, "Kristaloid",  xR,      yF, 100);
        addLblS(FormPraInd, ":",               xR+102, yF, 10);
        TCairanKristaloid = addTxt(FormPraInd, xR+115, yF, 200, 23);
        addLblS(FormPraInd, "ml", xR+318, yF, 30); yF += 25;
        JLabel lblTransfusi = new JLabel("Transfusi darah :"); lblTransfusi.setFont(fBold);
        lblTransfusi.setBounds(xR, yF, 130, 20); FormPraInd.add(lblTransfusi); yF += 20;
        ChkWB   = addChk(FormPraInd, "WB",   xR,      yF, 60);
        addLblS(FormPraInd, ":",       xR+62,  yF, 10);
        TTransfusiWB = addTxt(FormPraInd, xR+75,  yF, 200, 23);
        addLblS(FormPraInd, "ml", xR+278, yF, 30); yF += 25;
        ChkPRC  = addChk(FormPraInd, "PRC",  xR,      yF, 60);
        addLblS(FormPraInd, ":",        xR+62,  yF, 10);
        TTransfusiPRC = addTxt(FormPraInd, xR+75,  yF, 200, 23);
        addLblS(FormPraInd, "ml", xR+278, yF, 30); yF += 25;
        ChkFFP  = addChk(FormPraInd, "FFP",  xR,      yF, 60);
        addLblS(FormPraInd, ":",        xR+62,  yF, 10);
        TTransfusiFFP = addTxt(FormPraInd, xR+75,  yF, 200, 23);
        addLblS(FormPraInd, "ml", xR+278, yF, 30); yF += 25;
        ChkCryo = addChk(FormPraInd, "Cryo", xR,      yF, 60);
        addLblS(FormPraInd, ":",        xR+62,  yF, 10);
        TTransfusiCryo = addTxt(FormPraInd, xR+75,  yF, 200, 23);
        addLblS(FormPraInd, "ml", xR+278, yF, 30); yF += 25;
        ChkCairanLainChk = addChk(FormPraInd, "Lain-lain", xR,      yF, 90);
        addLblS(FormPraInd, ":",                    xR+92,  yF, 10);
        TCairanLain = addTxt(FormPraInd, xR+105, yF, 200, 23);
        addLblS(FormPraInd, "ml", xR+308, yF, 30); yF += 25;
        JLabel lblOutput = new JLabel("Output :"); lblOutput.setFont(fBold);
        lblOutput.setBounds(xR, yF, 70, 20); FormPraInd.add(lblOutput); yF += 20;
        addLblS(FormPraInd, "\u2022 Kehilangan darah :", xR, yF, 145);
        TOutputDarah = addTxt(FormPraInd, xR+147, yF, 200, 23);
        addLblS(FormPraInd, "ml", xR+350, yF, 30); yF += 25;
        addLblS(FormPraInd, "\u2022 Urin total :", xR, yF, 95);
        TOutputUrin  = addTxt(FormPraInd, xR+97,  yF, 200, 23);
        addLblS(FormPraInd, "ml", xR+300, yF, 30); yF += 25;
        addLblS(FormPraInd, "\u2022 NGT / OGT :", xR, yF, 95);
        TOutputNGT   = addTxt(FormPraInd, xR+97,  yF, 200, 23);
        addLblS(FormPraInd, "ml", xR+300, yF, 30); yF += 25;

        y = Math.max(yE, yF) + 20;

        FormPraInd.setPreferredSize(new Dimension(990, y + 40));

        ScrollPane sp2 = new ScrollPane(); sp2.setViewportView(FormPraInd);
        tabModePraInd = buildTM("No.Rawat","Pasien","Tanggal","Jam","Dokter Operator","Prosedur Operasi","Ruang OT","TD","Nadi","SpO2","Dokter Anestesi","Penata","Penginput");
        tbPraInd = buildTB(tabModePraInd, () -> getDataPraInd());
        Tabs.addTab("Pra Induksi & Durante", mkTabPanel(sp2, tbPraInd));
    }

    // ===== TAB 4: PASCA SEDASI/ANESTESI (dengan sub-tab) =====
    private void buildTabPasca() {
        JTabbedPane subTabs = new JTabbedPane();
        subTabs.setFont(new java.awt.Font("Tahoma", 0, 11));

        // Inisialisasi field yang tidak ditampilkan (dibutuhkan untuk DB)
        TglPasca = new widget.Tanggal(); TglPasca.setDisplayFormat("dd-MM-yyyy"); TglPasca.setDate(new java.util.Date());
        SpJamPasca = new JSpinner(new SpinnerNumberModel(0, 0, 23, 1));
        SpMenitPasca = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        TNamaPengkaji = new TextBox();

        // ---- Sub-tab 1: Tanda Vital ----
        PanelBiasa formTV = new PanelBiasa();
        formTV.setLayout(null);
        int x1 = 10, x2 = 155, y = 15;

        addLbl(formTV, "Tensi :", x1, y);     TDPasca    = addTxt(formTV, x2, y, 120, 23); y += 30;
        addLbl(formTV, "Nadi :", x1, y);      TNPasca    = addTxt(formTV, x2, y, 120, 23); y += 30;
        addLbl(formTV, "Respirasi :", x1, y); TRespPasca = addTxt(formTV, x2, y, 120, 23); y += 30;
        addLbl(formTV, "Suhu :", x1, y);      TSPasca    = addTxt(formTV, x2, y, 120, 23); y += 15;

        formTV.setPreferredSize(new Dimension(990, y + 20));
        ScrollPane spTV = new ScrollPane(); spTV.setViewportView(formTV);
        subTabs.addTab("Tanda Vital", spTV);

        // ---- Sub-tab 2: Skor Aidrete ----
        String[] waktuOpt = {"","0 menit","15 menit","30 menit","45 menit","60 menit","90 menit","120 menit"};
        String[] skorOpt  = {"","2","1","0"};
        PanelBiasa formAi = new PanelBiasa(); formAi.setLayout(null);
        int ax = 10, ay = 10;
        addLblS(formAi, "Waktu :", ax, ay, 80);
        CmbAidreteWaktu = addCmb(formAi, waktuOpt, ax+85, ay, 130); ay += 35;

        // Header baris
        addLblS(formAi, "Kategori", ax, ay, 300);
        addLblS(formAi, "2", ax+310, ay, 30);
        addLblS(formAi, "1", ax+345, ay, 30);
        addLblS(formAi, "0", ax+380, ay, 30);
        addLblS(formAi, "Nilai", ax+420, ay, 50); ay += 25;

        // Sirkulasi
        addLblS(formAi, "Sirkulasi (TD):", ax, ay, 140);
        addLblS(formAi, "< 20% pra anestesi", ax+145, ay, 155);
        addLblS(formAi, "20-50%", ax+305, ay, 60);
        addLblS(formAi, "> 50%", ax+370, ay, 60);
        CmbAidreteSirkulasi = addCmb(formAi, skorOpt, ax+440, ay, 60); ay += 28;

        // Kesadaran
        addLblS(formAi, "Kesadaran:", ax, ay, 140);
        addLblS(formAi, "Sadar orientasi baik", ax+145, ay, 155);
        addLblS(formAi, "Dipanggil", ax+305, ay, 60);
        addLblS(formAi, "Tdk respon", ax+370, ay, 70);
        CmbAidreteKesadaran = addCmb(formAi, skorOpt, ax+440, ay, 60); ay += 28;

        // Saturasi O2 Dewasa
        addLblS(formAi, "Sat O2 (Dewasa):", ax, ay, 140);
        addLblS(formAi, "Warna kulit merah muda", ax+145, ay, 165);
        addLblS(formAi, "Pucat", ax+315, ay, 50);
        addLblS(formAi, "Sianosis", ax+370, ay, 70);
        CmbAidreteSatO2Dws = addCmb(formAi, skorOpt, ax+440, ay, 60); ay += 28;

        // Saturasi O2 Pediatrik
        addLblS(formAi, "Sat O2 (Pediatrik):", ax, ay, 140);
        addLblS(formAi, "SpO2 > 90% udara bebas", ax+145, ay, 165);
        addLblS(formAi, "Perlu O2", ax+315, ay, 55);
        addLblS(formAi, "SpO2 < 90%", ax+370, ay, 70);
        CmbAidreteSatO2Pedi = addCmb(formAi, skorOpt, ax+440, ay, 60); ay += 28;

        // Pernapasan
        addLblS(formAi, "Pernapasan:", ax, ay, 140);
        addLblS(formAi, "Bernapas dalam & batuk", ax+145, ay, 165);
        addLblS(formAi, "Dangkal adekuat", ax+315, ay, 100);
        addLblS(formAi, "Apnea/obstruksi", ax+420, ay, 110);
        CmbAidretePernapasan = addCmb(formAi, skorOpt, ax+535, ay, 60); ay += 28;

        // Aktifitas
        addLblS(formAi, "Aktifitas:", ax, ay, 140);
        addLblS(formAi, "Mampu gerak 4 ekstremitas", ax+145, ay, 175);
        addLblS(formAi, "2 ekstremitas", ax+325, ay, 90);
        addLblS(formAi, "Tidak mampu", ax+420, ay, 90);
        CmbAidreteAktifitas = addCmb(formAi, skorOpt, ax+515, ay, 60); ay += 35;

        // Total
        addLblS(formAi, "TOTAL :", ax, ay, 80);
        LblAidreteTotal = new Label(); LblAidreteTotal.setText("0");
        LblAidreteTotal.setBounds(ax+85, ay, 60, 23); formAi.add(LblAidreteTotal);

        // Listener untuk auto-hitung total
        ActionListener aiCalc = e -> {
            int tot = 0;
            for (ComboBox c : new ComboBox[]{CmbAidreteSirkulasi, CmbAidreteKesadaran,
                    CmbAidreteSatO2Dws, CmbAidreteSatO2Pedi, CmbAidretePernapasan, CmbAidreteAktifitas}) {
                String v = c.getSelectedItem() != null ? c.getSelectedItem().toString().trim() : "";
                if (!v.isEmpty()) try { tot += Integer.parseInt(v); } catch (NumberFormatException ex) {}
            }
            LblAidreteTotal.setText(String.valueOf(tot));
        };
        for (ComboBox c : new ComboBox[]{CmbAidreteSirkulasi, CmbAidreteKesadaran,
                CmbAidreteSatO2Dws, CmbAidreteSatO2Pedi, CmbAidretePernapasan, CmbAidreteAktifitas}) {
            c.addActionListener(aiCalc);
        }

        formAi.setPreferredSize(new Dimension(650, ay + 40));
        ScrollPane spAi = new ScrollPane(); spAi.setViewportView(formAi);
        subTabs.addTab("Skor Aidrete", spAi);

        // ---- Sub-tab 3: Skor Nyeri ----
        PanelBiasa formNy = new PanelBiasa(); formNy.setLayout(null);
        int nx = 10, ny2 = 15;
        addLbl(formNy, "Waktu :", nx, ny2);
        CmbNyeriWaktu = addCmb(formNy, waktuOpt, nx+155, ny2, 130); ny2 += 30;
        addLbl(formNy, "VAS :", nx, ny2);
        TVasNyeri = addTxt(formNy, nx+155, ny2, 120, 23); ny2 += 15;
        formNy.setPreferredSize(new Dimension(990, ny2 + 20));
        ScrollPane spNy = new ScrollPane(); spNy.setViewportView(formNy);
        subTabs.addTab("Skor Nyeri", spNy);

        // ---- Sub-tab 4: Skor Bromage ----
        PanelBiasa formBr = new PanelBiasa(); formBr.setLayout(null);
        int bx = 10, by = 15;
        addLbl(formBr, "Waktu :", bx, by);
        CmbBromageWaktu = addCmb(formBr, waktuOpt, bx+155, by, 130); by += 35;

        addLblS(formBr, "Level Bromage :", bx, by, 140);
        String[] bromOpt = {
            "",
            "IV - COMPLETE : Tidak mampu menggerakkan lutut & pergelangan kaki",
            "III - ALMOST COMPLETE : Mampu menggerakkan pergelangan kaki",
            "II - PARTIAL : Mampu menggerakkan kaki",
            "I - NONE : Mampu menggerakkan lutut & pergelangan kaki"
        };
        CmbBromageLevel = addCmb(formBr, bromOpt, bx+155, by, 700); by += 30;

        // Keterangan
        addLblS(formBr, "IV (Complete)     :", bx, by, 140);
        addLblS(formBr, "Tidak mampu menggerakkan lutut dan pergelangan kaki", bx+145, by, 450); by += 22;
        addLblS(formBr, "III (Almost Compl):", bx, by, 140);
        addLblS(formBr, "Mampu menggerakkan pergelangan kaki", bx+145, by, 350); by += 22;
        addLblS(formBr, "II (Partial)      :", bx, by, 140);
        addLblS(formBr, "Mampu menggerakkan kaki", bx+145, by, 250); by += 22;
        addLblS(formBr, "I (None)          :", bx, by, 140);
        addLblS(formBr, "Mampu menggerakkan lutut dan pergelangan kaki", bx+145, by, 400); by += 15;

        formBr.setPreferredSize(new Dimension(990, by + 20));
        ScrollPane spBr = new ScrollPane(); spBr.setViewportView(formBr);
        subTabs.addTab("Skor Bromage", spBr);

        tabModePasca = buildTM("No.Rawat","Pasien","Tanggal","Jam","Nama Pengkaji","TD","Nadi","Resp","Suhu","Skor Aidrete","Skor Nyeri","Skor Bromage","Dokter Anestesi","Penata","Penginput");
        tbPasca = buildTB(tabModePasca, () -> getDataPasca());
        Tabs.addTab("Pasca Sedasi/Anestesi", mkTabPanel(subTabs, tbPasca));
    }

    private void buildTabInstruksiRR() {
        PanelBiasa formRR = new PanelBiasa(); formRR.setLayout(null);
        int x1 = 10, y = 15;

        addLbl(formRR, "Prosedur :", x1, y);
        TInstruksiRR = new JTextArea(8, 60);
        TInstruksiRR.setFont(new java.awt.Font("Tahoma", 0, 11));
        TInstruksiRR.setLineWrap(true); TInstruksiRR.setWrapStyleWord(true);
        JScrollPane spIRR = new JScrollPane(TInstruksiRR);
        spIRR.setBounds(x1, y+25, 960, 300); formRR.add(spIRR); y += 340;

        formRR.setPreferredSize(new Dimension(990, y + 20));
        ScrollPane sp = new ScrollPane(); sp.setViewportView(formRR);
        tabModeRR = buildTM("No.Rawat","Pasien","Tanggal","Jam","Instruksi RR","Dokter Anestesi","Penata","Penginput");
        tbRR = buildTB(tabModeRR, () -> getDataRR());
        Tabs.addTab("Instruksi Khusus Recovery Room", mkTabPanel(sp, tbRR));
    }

    private void buildTabDischarge() {
        PanelBiasa formD = new PanelBiasa(); formD.setLayout(null);
        int x1 = 10, y = 20;

        // SpJamDischarge / SpMenitDischarge inisialisasi tersembunyi (untuk DB)
        SpJamDischarge = new JSpinner(new SpinnerNumberModel(0, 0, 23, 1));
        SpMenitDischarge = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));

        addLbl(formD, "Discharge ke :", x1, y);
        ChkDischBangsal = addChk(formD, "Bangsal", x1+155, y, 90);
        ChkDischICU     = addChk(formD, "ICU",     x1+250, y, 60);
        ChkDischHCU     = addChk(formD, "HCU",     x1+315, y, 60); y += 30;

        addLbl(formD, "Observasi terakhir :", x1, y);
        addLblS(formD, "TD :", x1+155, y, 35);  TTdDischarge   = addTxt(formD, x1+193, y, 80, 23);
        addLblS(formD, "N :", x1+280, y, 25);   TNDischarge    = addTxt(formD, x1+308, y, 70, 23);
        addLblS(formD, "Resp :", x1+385, y, 40); TRespDischarge = addTxt(formD, x1+428, y, 70, 23);
        addLblS(formD, "S :", x1+505, y, 25);   TSDischarge    = addTxt(formD, x1+530, y, 60, 23);
        addLblS(formD, "VAS :", x1+598, y, 40); TVasDischarge  = addTxt(formD, x1+640, y, 70, 23); y += 40;

        addLbl(formD, "Instruksi Anestesi :", x1, y);
        TInstruksiAnestesi = new JTextArea(8, 60);
        TInstruksiAnestesi.setFont(new java.awt.Font("Tahoma", 0, 11));
        TInstruksiAnestesi.setLineWrap(true); TInstruksiAnestesi.setWrapStyleWord(true);
        JScrollPane spIA = new JScrollPane(TInstruksiAnestesi);
        spIA.setBounds(x1, y+25, 960, 200); formD.add(spIA); y += 240;

        // CmbDischargeDari disimpan tersembunyi untuk kompatibilitas DB lama
        CmbDischargeDari = new ComboBox();
        CmbDischargeDari.setModel(new DefaultComboBoxModel(new String[]{"","Ruangan","ICU","HCU","NICU","Pulang"}));

        formD.setPreferredSize(new Dimension(990, y + 20));
        ScrollPane sp = new ScrollPane(); sp.setViewportView(formD);
        tabModeDisch = buildTM("No.Rawat","Pasien","Tanggal","Jam","Discharge Ke","TD","Nadi","Resp","Suhu","VAS","Dokter Anestesi","Penata","Penginput");
        tbDisch = buildTB(tabModeDisch, () -> getDataDisch());
        Tabs.addTab("Discharge", mkTabPanel(sp, tbDisch));
    }

    // ===== TAB 7: MONITORING DURANTE SEDASI/ANESTESI =====
    private void buildTabDurante() {
        java.awt.Font fBold = new java.awt.Font("Tahoma", 1, 11);
        java.awt.Font fNorm = new java.awt.Font("Tahoma", 0, 11);

        JTabbedPane subDur = new JTabbedPane();

        // --- Sub tab: Obat ---
        obatHour = new JSpinner(new SpinnerNumberModel(8, 0, 23, 1));
        obatMin  = new JSpinner(new SpinnerNumberModel(0, 0, 45, 15));
        obatHour.setPreferredSize(new java.awt.Dimension(50, 23));
        obatMin.setPreferredSize(new java.awt.Dimension(50, 23));
        obatGridModel = buildDurGridModel(8, 0, obatInterval[0], "Nama Obat");
        obatGridTable = buildDurGridTable(obatGridModel);
        subDur.addTab("Obat", buildDurGridPanel(obatGridModel, obatGridTable, obatHour, obatMin, obatInterval));

        // --- Sub tab: Cairan ---
        cairanHour = new JSpinner(new SpinnerNumberModel(8, 0, 23, 1));
        cairanMin  = new JSpinner(new SpinnerNumberModel(0, 0, 45, 15));
        cairanHour.setPreferredSize(new java.awt.Dimension(50, 23));
        cairanMin.setPreferredSize(new java.awt.Dimension(50, 23));
        cairanGridModel = buildDurGridModel(8, 0, cairanInterval[0], "Nama Cairan");
        cairanGridTable = buildDurGridTable(cairanGridModel);
        subDur.addTab("Cairan", buildDurGridPanel(cairanGridModel, cairanGridTable, cairanHour, cairanMin, cairanInterval));

        // --- Sub tab: Gas & Monitor ---
        PanelBiasa formGas = new PanelBiasa(); formGas.setLayout(null);
        int x1=10, x2=185, wTxt=100, y=10;
        int lw=160;
        addLbl(formGas,"Oksigen (L/menit) :",x1,y); TOksigen = addTxt(formGas,x2,y,wTxt,23); y+=28;
        addLbl(formGas,"N2O (L/menit) :",x1,y);     TN2O = addTxt(formGas,x2,y,wTxt,23); y+=28;
        addLbl(formGas,"Air (L/menit) :",x1,y);      TAirLmenit = addTxt(formGas,x2,y,wTxt,23); y+=28;
        addLbl(formGas,"Saturasi O2 :",x1,y);        TSaturasiO2 = addTxt(formGas,x2,y,wTxt,23); y+=28;
        addLbl(formGas,"FiO2 :",x1,y);               TFiO2 = addTxt(formGas,x2,y,wTxt,23); y+=28;
        addLbl(formGas,"End Tidal CO2 :",x1,y);      TEndTidalCO2 = addTxt(formGas,x2,y,wTxt,23); y+=28;
        addLbl(formGas,"EKG :",x1,y);                TEKGDur = addTxt(formGas,x2,y,wTxt,23); y+=28;
        addLbl(formGas,"CVP/PCWP :",x1,y);           TCvpPcwp = addTxt(formGas,x2,y,wTxt,23); y+=28;
        addLbl(formGas,"Suhu (C) :",x1,y);           TSuhuDur = addTxt(formGas,x2,y,wTxt,23); y+=28;
        addLbl(formGas,"Urin :",x1,y);               TUrinDur = addTxt(formGas,x2,y,wTxt,23); y+=28;
        addLbl(formGas,"Lain-lain :",x1,y);          TLainLainDur = addTxt(formGas,x2,y,300,23); y+=35;
        JLabel hCircuit = new JLabel("Circuit"); hCircuit.setFont(fBold);
        hCircuit.setBounds(x1,y,200,20); formGas.add(hCircuit); y+=22;
        ChkJacksonRees = new CekBox(); ChkJacksonRees.setText("Jackson Rees Circuit");
        ChkJacksonRees.setBounds(x1,y,220,22); formGas.add(ChkJacksonRees);
        formGas.setPreferredSize(new java.awt.Dimension(990, y+40));
        ScrollPane spGas = new ScrollPane(); spGas.setViewportView(formGas);
        subDur.addTab("Gas & Monitor", spGas);

        // --- Sub tab: Posisi: Ventilator ---
        PanelBiasa formVent = new PanelBiasa(); formVent.setLayout(null);
        y=10;
        addLbl(formVent,"Frekuensi Pernapasan :",x1,y); TFrekPernapasan = addTxt(formVent,x2,y,wTxt,23); y+=28;
        addLbl(formVent,"Volume Tidal :",x1,y);          TVolumeTidal = addTxt(formVent,x2,y,wTxt,23); y+=28;
        addLbl(formVent,"Peak Pressure :",x1,y);         TPeakPressure = addTxt(formVent,x2,y,wTxt,23); y+=28;
        addLbl(formVent,"PEEP :",x1,y);                  TPEEP = addTxt(formVent,x2,y,wTxt,23); y+=28;
        formVent.setPreferredSize(new java.awt.Dimension(990, y+20));
        ScrollPane spVent = new ScrollPane(); spVent.setViewportView(formVent);
        subDur.addTab("Posisi: Ventilator", spVent);

        // --- Sub tab: Komplikasi Perioperatif ---
        PanelBiasa formKomp = new PanelBiasa(); formKomp.setLayout(null);
        JLabel hKomp = new JLabel("Komplikasi Perioperatif");
        hKomp.setFont(fBold); hKomp.setBounds(10,10,300,20); formKomp.add(hKomp);
        TKomplikasiDur = new JTextArea(); TKomplikasiDur.setFont(fNorm);
        TKomplikasiDur.setLineWrap(true); TKomplikasiDur.setWrapStyleWord(true);
        JScrollPane spKompTA = new JScrollPane(TKomplikasiDur);
        spKompTA.setBounds(10,35,960,350); formKomp.add(spKompTA);
        formKomp.setPreferredSize(new java.awt.Dimension(990, 410));
        subDur.addTab("Komplikasi Perioperatif", new ScrollPane() {{ setViewportView(formKomp); }});

        // --- Tab panel wrapper ---
        tabModeDur = buildTM("No.Rawat","Pasien","Tanggal","Jam","O2 (L/mnt)","N2O","SpO2","EKG","Suhu","Urin","Komplikasi","Dokter Anestesi","Penata","Penginput");
        tbDur = buildTB(tabModeDur, () -> getDataDur());
        Tabs.addTab("Monitoring Durante", mkTabPanel(subDur, tbDur));
    }

    // ===== SKOR BUILD/LOAD =====
    private String buildAidreteStr() {
        return safeItem(CmbAidreteWaktu) + "|" + safeItem(CmbAidreteSirkulasi) + "|" +
               safeItem(CmbAidreteKesadaran) + "|" + safeItem(CmbAidreteSatO2Dws) + "|" +
               safeItem(CmbAidreteSatO2Pedi) + "|" + safeItem(CmbAidretePernapasan) + "|" +
               safeItem(CmbAidreteAktifitas);
    }
    private void loadAidreteStr(String val) {
        if (val == null || val.isEmpty()) return;
        // backward compat: old data might be free text (no pipes)
        if (!val.contains("|")) return;
        String[] p = val.split("\\|", -1);
        if (p.length > 0) CmbAidreteWaktu.setSelectedItem(p[0]);
        if (p.length > 1) CmbAidreteSirkulasi.setSelectedItem(p[1]);
        if (p.length > 2) CmbAidreteKesadaran.setSelectedItem(p[2]);
        if (p.length > 3) CmbAidreteSatO2Dws.setSelectedItem(p[3]);
        if (p.length > 4) CmbAidreteSatO2Pedi.setSelectedItem(p[4]);
        if (p.length > 5) CmbAidretePernapasan.setSelectedItem(p[5]);
        if (p.length > 6) CmbAidreteAktifitas.setSelectedItem(p[6]);
    }
    private String buildNyeriStr() {
        return safeItem(CmbNyeriWaktu) + "|" + TVasNyeri.getText();
    }
    private void loadNyeriStr(String val) {
        if (val == null || val.isEmpty()) return;
        if (!val.contains("|")) { TVasNyeri.setText(val); return; }
        String[] p = val.split("\\|", 2);
        CmbNyeriWaktu.setSelectedItem(p[0]);
        TVasNyeri.setText(p.length > 1 ? p[1] : "");
    }
    private String buildBromageStr() {
        return safeItem(CmbBromageWaktu) + "|" + safeItem(CmbBromageLevel);
    }
    private void loadBromageStr(String val) {
        if (val == null || val.isEmpty()) return;
        if (!val.contains("|")) return;
        String[] p = val.split("\\|", 2);
        CmbBromageWaktu.setSelectedItem(p[0]);
        if (p.length > 1) CmbBromageLevel.setSelectedItem(p[1]);
    }
    private String buildDischDest() {
        StringBuilder sb = new StringBuilder();
        if (ChkDischBangsal.isSelected()) sb.append("Bangsal,");
        if (ChkDischICU.isSelected()) sb.append("ICU,");
        if (ChkDischHCU.isSelected()) sb.append("HCU,");
        return sb.length() > 0 ? sb.substring(0, sb.length()-1) : "";
    }
    private void loadDischDest(String val) {
        if (val == null) val = "";
        ChkDischBangsal.setSelected(val.contains("Bangsal") || val.contains("Ruangan"));
        ChkDischICU.setSelected(val.contains("ICU"));
        ChkDischHCU.setSelected(val.contains("HCU"));
    }
    private String safeItem(ComboBox c) {
        return c.getSelectedItem() != null ? c.getSelectedItem().toString() : "";
    }

    // ===== TAB PANEL BUILDER HELPERS =====
    private DefaultTableModel buildTM(String... cols) {
        return new DefaultTableModel(new Object[][]{}, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }
    private widget.Table buildTB(DefaultTableModel tm, Runnable onSelect) {
        widget.Table tb = new widget.Table(); tb.setModel(tm);
        tb.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (tm.getRowCount() != 0) onSelect.run();
            }
        });
        return tb;
    }
    private widget.Tanggal mkDTP() {
        widget.Tanggal d = new widget.Tanggal(); d.setDisplayFormat("dd-MM-yyyy"); d.setPreferredSize(new Dimension(90,23)); return d;
    }
    private Button mkBtnCari() {
        Button b = new Button(); b.setIcon(new ImageIcon(getClass().getResource("/picture/accept.png")));
        b.setPreferredSize(new Dimension(28,23)); return b;
    }
    private java.awt.event.KeyAdapter mkEnter(Runnable r) {
        return new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) r.run();
            }
        };
    }
    private JPanel mkSearch(widget.Tanggal d1, widget.Tanggal d2, TextBox tc, Button bc, Label lc) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2)); p.setOpaque(false);
        Label lt = new Label(); lt.setText("Tgl:"); lt.setPreferredSize(new Dimension(28,23));
        Label ls = new Label(); ls.setText("s.d."); ls.setPreferredSize(new Dimension(28,23));
        Label lk = new Label(); lk.setText("Cari:"); lk.setPreferredSize(new Dimension(32,23));
        p.add(lt); p.add(d1); p.add(ls); p.add(d2); p.add(lk); p.add(tc); p.add(bc); p.add(lc);
        return p;
    }
    private JPanel mkTabPanel(java.awt.Component formScroll, widget.Table tb) {
        JPanel content = new JPanel(new BorderLayout());
        content.add(formScroll, BorderLayout.CENTER);
        JPanel tableArea = new JPanel(new BorderLayout());
        tableArea.setPreferredSize(new Dimension(1020, 160));
        ScrollPane sc = new ScrollPane(); sc.setViewportView(tb);
        tableArea.add(sc, BorderLayout.CENTER);
        content.add(tableArea, BorderLayout.SOUTH);
        return content;
    }

    // ===== PER-TAB CRUD =====
    private void simpan() {
        switch (Tabs.getSelectedIndex()) {
            case 0: simpanPra(); break;    case 1: simpanPOC(); break;
            case 2: simpanPraInd(); break; case 3: simpanPasca(); break;
            case 4: simpanRR(); break;     case 5: simpanDisch(); break;
            case 6: simpanDur(); break;    case 7: simpanCatatan(); break;
        }
    }
    private void hapus() {
        switch (Tabs.getSelectedIndex()) {
            case 0: hapusX("rm_sedasi_anamnesa", tabModePra, tbPra, () -> tampilPra()); break;
            case 1: hapusX("rm_sedasi_poc", tabModePOC, tbPOC, () -> tampilPOC()); break;
            case 2: hapusX("rm_sedasi_praind", tabModePraInd, tbPraInd, () -> tampilPraInd()); break;
            case 3: hapusX("rm_sedasi_pasca", tabModePasca, tbPasca, () -> tampilPasca()); break;
            case 4: hapusX("rm_sedasi_rr", tabModeRR, tbRR, () -> tampilRR()); break;
            case 5: hapusX("rm_sedasi_discharge", tabModeDisch, tbDisch, () -> tampilDisch()); break;
            case 6: hapusX("rm_sedasi_durante", tabModeDur, tbDur, () -> tampilDur()); break;
            case 7: hapusX("rm_sedasi_catatan", tabModeCatatan, tbCatatan, () -> tampilCatatan()); break;
        }
    }
    private void ganti() {
        switch (Tabs.getSelectedIndex()) {
            case 0: gantiX("rm_sedasi_anamnesa", tabModePra, tbPra, () -> { simpanPra(); }); break;
            case 1: gantiX("rm_sedasi_poc", tabModePOC, tbPOC, () -> { simpanPOC(); }); break;
            case 2: gantiX("rm_sedasi_praind", tabModePraInd, tbPraInd, () -> { simpanPraInd(); }); break;
            case 3: gantiX("rm_sedasi_pasca", tabModePasca, tbPasca, () -> { simpanPasca(); }); break;
            case 4: gantiX("rm_sedasi_rr", tabModeRR, tbRR, () -> { simpanRR(); }); break;
            case 5: gantiX("rm_sedasi_discharge", tabModeDisch, tbDisch, () -> { simpanDisch(); }); break;
            case 6: gantiX("rm_sedasi_durante", tabModeDur, tbDur, () -> { simpanDur(); }); break;
            case 7: gantiX("rm_sedasi_catatan", tabModeCatatan, tbCatatan, () -> { simpanCatatan(); }); break;
        }
    }
    private void hapusX(String tbl, DefaultTableModel tm, widget.Table tb, Runnable onDone) {
        if (tb.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null,"Pilih data terlebih dahulu!"); return; }
        if (JOptionPane.showConfirmDialog(null,"Hapus data ini?","Konfirmasi",JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            ps = koneksi.prepareStatement("DELETE FROM "+tbl+" WHERE no_rawat=? AND tgl_asuhan=? AND jam=?");
            ps.setString(1,tm.getValueAt(tb.getSelectedRow(),0).toString());
            ps.setString(2,tm.getValueAt(tb.getSelectedRow(),2).toString());
            ps.setString(3,tm.getValueAt(tb.getSelectedRow(),3).toString());
            ps.executeUpdate(); emptTeks(); onDone.run();
        } catch (Exception e) { System.out.println("hapus: "+e);
        } finally { try { if (ps!=null) ps.close(); } catch (Exception e) {} }
    }
    private void gantiX(String tbl, DefaultTableModel tm, widget.Table tb, Runnable doSimpan) {
        if (tm.getRowCount()==0 || tb.getSelectedRow()==-1) { JOptionPane.showMessageDialog(null,"Pilih data!"); return; }
        try {
            ps = koneksi.prepareStatement("DELETE FROM "+tbl+" WHERE no_rawat=? AND tgl_asuhan=? AND jam=?");
            ps.setString(1,tm.getValueAt(tb.getSelectedRow(),0).toString());
            ps.setString(2,tm.getValueAt(tb.getSelectedRow(),2).toString());
            ps.setString(3,tm.getValueAt(tb.getSelectedRow(),3).toString());
            ps.executeUpdate(); doSimpan.run();
        } catch (Exception e) { System.out.println("ganti: "+e);
        } finally { try { if (ps!=null) ps.close(); } catch (Exception e) {} }
    }
    public void tampil() { tampilPra(); tampilPOC(); tampilPraInd(); tampilPasca(); tampilRR(); tampilDisch(); tampilDur(); tampilCatatan(); }

    // --- Helper to run a SELECT on each tab table (uses shared search bar) ---
    private void tampilX(String sql, DefaultTableModel tm) {
        Valid.tabelKosong(tm);
        try {
            ps = koneksi.prepareStatement(sql);
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem()+""));
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem()+""));
            String noRawat = TNoRw.getText().trim();
            ps.setString(3, noRawat.isEmpty() ? "%" : noRawat);
            String kw = "%"+(TCari != null ? TCari.getText().trim() : "")+"%";
            ps.setString(4,kw); ps.setString(5,kw); ps.setString(6,kw);
            rs = ps.executeQuery();
            int cc = 0;
            while (rs.next()) {
                if (cc == 0) cc = rs.getMetaData().getColumnCount();
                String[] row = new String[cc];
                for (int ci = 1; ci <= cc; ci++) { String v = rs.getString(ci); row[ci-1] = v != null ? v : ""; }
                tm.addRow(row);
            }
            if (LcntCari != null) LcntCari.setText("Record: "+tm.getRowCount());
        } catch (Exception e) { System.out.println("tampil: "+e);
        } finally { try { if(rs!=null)rs.close(); if(ps!=null)ps.close(); } catch(Exception e) {} }
    }
    private static final String SQL_SEL =
        " INNER JOIN reg_periksa r ON s.no_rawat=r.no_rawat " +
        " INNER JOIN pasien p ON r.no_rkm_medis=p.no_rkm_medis " +
        " WHERE s.tgl_asuhan BETWEEN ? AND ? " +
        " AND s.no_rawat LIKE ? " +
        " AND (s.no_rawat LIKE ? OR r.no_rkm_medis LIKE ? OR p.nm_pasien LIKE ?) " +
        " ORDER BY s.tgl_asuhan DESC, s.jam DESC";
    private void tampilPra()    { tampilX("SELECT s.no_rawat,p.nm_pasien,s.tgl_asuhan,s.jam,s.prosedur,s.asa_grade,s.td_pra,s.nadi_pra,s.suhu_pra,s.nm_dokter_anestesi,s.nm_penata,s.nm_penginput FROM rm_sedasi_anamnesa s"+SQL_SEL, tabModePra); }
    private void tampilPOC()    { tampilX("SELECT s.no_rawat,p.nm_pasien,s.tgl_asuhan,s.jam,s.teknik_anestesi,s.monitor_invasif,s.kontrol_nyeri,s.risiko,s.nm_dokter_anestesi,s.nm_penata,s.nm_penginput FROM rm_sedasi_poc s"+SQL_SEL, tabModePOC); }
    private void tampilPraInd() { tampilX("SELECT s.no_rawat,p.nm_pasien,s.tgl_asuhan,s.jam,s.dokter_operator,s.prosedur_operasi,s.ruang_ot,s.td_pra_ind,s.nadi_pra_ind,s.spo2,s.nm_dokter_anestesi,s.nm_penata,s.nm_penginput FROM rm_sedasi_praind s"+SQL_SEL, tabModePraInd); }
    private void tampilPasca()  { tampilX("SELECT s.no_rawat,p.nm_pasien,s.tgl_asuhan,s.jam,s.nama_pengkaji,s.td_pasca,s.n_pasca,s.resp_pasca,s.s_pasca,s.skor_aidrete,s.skor_nyeri,s.skor_bromage,s.nm_dokter_anestesi,s.nm_penata,s.nm_penginput FROM rm_sedasi_pasca s"+SQL_SEL, tabModePasca); }
    private void tampilRR()     { tampilX("SELECT s.no_rawat,p.nm_pasien,s.tgl_asuhan,s.jam,s.instruksi_rr,s.nm_dokter_anestesi,s.nm_penata,s.nm_penginput FROM rm_sedasi_rr s"+SQL_SEL, tabModeRR); }
    private void tampilDisch()  { tampilX("SELECT s.no_rawat,p.nm_pasien,s.tgl_asuhan,s.jam,s.discharge_ke,s.td_discharge,s.n_discharge,s.resp_discharge,s.s_discharge,s.vas_discharge,s.nm_dokter_anestesi,s.nm_penata,s.nm_penginput FROM rm_sedasi_discharge s"+SQL_SEL, tabModeDisch); }
    private void tampilDur()    { tampilX("SELECT s.no_rawat,p.nm_pasien,s.tgl_asuhan,s.jam,s.oksigen,s.n2o,s.saturasi_o2,s.ekg,s.suhu,s.urin,s.komplikasi,s.nm_dokter_anestesi,s.nm_penata,s.nm_penginput FROM rm_sedasi_durante s"+SQL_SEL, tabModeDur); }
    private void tampilCatatan(){ tampilX("SELECT s.no_rawat,p.nm_pasien,s.tgl_asuhan,s.jam,s.operasi_tanggal,s.anestesi_tanggal,s.antibiotik_tanggal,s.nm_penginput FROM rm_sedasi_catatan s"+SQL_SEL, tabModeCatatan); }

    private boolean validasiUmum() {
        if (TNoRw.getText().trim().isEmpty()) { Valid.textKosong(TNoRw,"No. Rawat"); return false; }
        if (TglAsuhan.getSelectedItem() == null || TglAsuhan.getSelectedItem().toString().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,"Tanggal Asuhan harus diisi."); return false; }
        if (Jam.getSelectedItem() == null || Jam.getSelectedItem().toString().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,"Jam harus diisi."); return false; }
        if (KdPenginput.getText().trim().isEmpty()) { Valid.textKosong(KdPenginput,"Kode Penginput"); return false; }
        return true;
    }

    // --- simpanPra ---
    private void simpanPra() {
        if (!validasiUmum()) return;
        if (KdDokter.getText().trim().isEmpty()) { Valid.textKosong(KdDokter,"Dokter Anestesi"); return; }
        String sql = "INSERT INTO rm_sedasi_anamnesa (no_rawat,tgl_asuhan,jam,kd_dokter_anestesi,nm_dokter_anestesi," +
            "kd_penata,nm_penata,kd_penginput,nm_penginput,prosedur,umur,gol_darah,rhesus,bb,tb," +
            "riwayat1,riwayat2,riwayat3,tidak_ada_riwayat,obat_dikonsumsi,tidak_ada_obat,alergi_obat,tidak_ada_alergi," +
            "komplikasi_sblm,komplikasi_keluarga,td_pra,nadi_pra,resp_pra,suhu_pra,vas_pra," +
            "obstruksi_jalan_napas,thorax_abdomen,wajah,buka_mulut,jarak_thyro,mallampati," +
            "gerak_rahang,fleksi_ekstensi,penyangga_leher,jalan_napas_sulit," +
            "sistem_pernapasan,sistem_kardio,sistem_neuro,sistem_renal,sistem_hepato,sistem_lain," +
            "merokok,merokok_pak,merokok_tahun,alkohol,alkohol_ket,penunjang,penunjang_lain,lab,lab_lain," +
            "masalah1,masalah2,masalah3,asa_grade,pra_medikasi,user_input) VALUES (" +
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            ps = koneksi.prepareStatement(sql); int i=1;
            ps.setString(i++,TNoRw.getText()); ps.setString(i++,getTglAsuhan()); ps.setString(i++,getJam());
            ps.setString(i++,KdDokter.getText()); ps.setString(i++,NmDokter.getText());
            ps.setString(i++,KdPenata.getText()); ps.setString(i++,NmPenata.getText());
            ps.setString(i++,KdPenginput.getText()); ps.setString(i++,NmPenginput.getText());
            ps.setString(i++,TProsedur.getText()); ps.setString(i++,TUmur.getText());
            ps.setString(i++,TGolDarah.getText()); ps.setString(i++,TRhesus.getText());
            ps.setString(i++,TBB.getText()); ps.setString(i++,TTB.getText());
            ps.setString(i++,TRiwayat1.getText()); ps.setString(i++,TRiwayat2.getText()); ps.setString(i++,TRiwayat3.getText());
            ps.setString(i++,ChkTidakAdaRiwayat.isSelected()?"Y":"N");
            ps.setString(i++,TObatDikonsumsi.getText()); ps.setString(i++,ChkTidakAdaObat.isSelected()?"Y":"N");
            ps.setString(i++,TAlergiObat.getText()); ps.setString(i++,ChkTidakAdaAlergi.isSelected()?"Y":"N");
            ps.setString(i++,ChkKomplikasiSblm.isSelected()?"Y":"N"); ps.setString(i++,ChkKomplikasiKeluarga.isSelected()?"Y":"N");
            ps.setString(i++,TTdPra.getText()); ps.setString(i++,TNadiPra.getText());
            ps.setString(i++,TRespPra.getText()); ps.setString(i++,TSuhuPra.getText()); ps.setString(i++,TVasPra.getText());
            ps.setString(i++,buildObstruksi()); ps.setString(i++,buildThorax()); ps.setString(i++,buildWajah());
            ps.setString(i++,ChkBukaMulutYa.isSelected()?"Y":"N"); ps.setString(i++,ChkJarakThyroYa.isSelected()?"Y":"N");
            ps.setString(i++,CmbMallampati.getSelectedItem().toString());
            ps.setString(i++,ChkGerakRahangYa.isSelected()?"Y":"N"); ps.setString(i++,ChkFleksiYa.isSelected()?"Y":"N");
            ps.setString(i++,ChkPenyanggaYa.isSelected()?"Y":"N"); ps.setString(i++,ChkJalanNapasYa.isSelected()?"Y":"N");
            ps.setString(i++,buildPernapasan()); ps.setString(i++,buildKardio());
            ps.setString(i++,buildNeuro()); ps.setString(i++,buildRenal());
            ps.setString(i++,buildHepato()); ps.setString(i++,buildLainSistem());
            ps.setString(i++,ChkMerokok.isSelected()?"Y":"N"); ps.setString(i++,TMerokokPak.getText()); ps.setString(i++,TMerokokTahun.getText());
            ps.setString(i++,ChkAlkohol.isSelected()?"Y":"N"); ps.setString(i++,TAlkohol.getText());
            ps.setString(i++,buildPenunjang()); ps.setString(i++,TPenunjangLain.getText());
            ps.setString(i++,buildLab()); ps.setString(i++,TLabLain.getText());
            ps.setString(i++,TMasalah1.getText()); ps.setString(i++,TMasalah2.getText()); ps.setString(i++,TMasalah3.getText());
            ps.setString(i++,CmbASA.getSelectedItem().toString()); ps.setString(i++,TPraMedikasi.getText());
            ps.setString(i++,akses.getkode());
            if (ps.executeUpdate()>0) { JOptionPane.showMessageDialog(null,"Data berhasil disimpan."); tampilPra(); emptTeksKeepAtas(); }
        } catch(Exception e) { System.out.println("simpanPra:"+e); JOptionPane.showMessageDialog(null,"Gagal: "+e.getMessage());
        } finally { try{if(ps!=null)ps.close();}catch(Exception e){} }
    }
    private void getDataPra() {
        if (tbPra.getSelectedRow()==-1) return;
        String noRawat=tabModePra.getValueAt(tbPra.getSelectedRow(),0).toString();
        String tglA=tabModePra.getValueAt(tbPra.getSelectedRow(),2).toString();
        String jamA=tabModePra.getValueAt(tbPra.getSelectedRow(),3).toString();
        TNoRw.setText(noRawat);
        try {
            ps = koneksi.prepareStatement(
                "SELECT s.*,r.no_rkm_medis,p.nm_pasien,p.tgl_lahir,p.jk FROM rm_sedasi_anamnesa s " +
                "INNER JOIN reg_periksa r ON s.no_rawat=r.no_rawat INNER JOIN pasien p ON r.no_rkm_medis=p.no_rkm_medis " +
                "WHERE s.no_rawat=? AND s.tgl_asuhan=? AND s.jam=?");
            ps.setString(1,noRawat); ps.setString(2,tglA); ps.setString(3,jamA);
            rs = ps.executeQuery();
            if (rs.next()) {
                TNoRM.setText(rs.getString("no_rkm_medis")); TPasien.setText(rs.getString("nm_pasien"));
                TTglLahir.setText(rs.getString("tgl_lahir")!=null?rs.getString("tgl_lahir"):"");
                TJK.setText(rs.getString("jk")!=null?rs.getString("jk"):"");
                try{TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(tglA));}catch(Exception ex){}
                String[] j3=jamA.split(":"); Jam.setSelectedItem(j3[0]); Menit.setSelectedItem(j3[1]);
                KdDokter.setText(rs.getString("kd_dokter_anestesi")); NmDokter.setText(rs.getString("nm_dokter_anestesi"));
                KdPenata.setText(rs.getString("kd_penata")); NmPenata.setText(rs.getString("nm_penata"));
                KdPenginput.setText(rs.getString("kd_penginput")); NmPenginput.setText(rs.getString("nm_penginput"));
                TProsedur.setText(rs.getString("prosedur")); TUmur.setText(rs.getString("umur"));
                TGolDarah.setText(rs.getString("gol_darah")); TRhesus.setText(rs.getString("rhesus"));
                TBB.setText(rs.getString("bb")); TTB.setText(rs.getString("tb"));
                TRiwayat1.setText(rs.getString("riwayat1")); TRiwayat2.setText(rs.getString("riwayat2")); TRiwayat3.setText(rs.getString("riwayat3"));
                ChkTidakAdaRiwayat.setSelected("Y".equals(rs.getString("tidak_ada_riwayat")));
                TObatDikonsumsi.setText(rs.getString("obat_dikonsumsi")); ChkTidakAdaObat.setSelected("Y".equals(rs.getString("tidak_ada_obat")));
                TAlergiObat.setText(rs.getString("alergi_obat")); ChkTidakAdaAlergi.setSelected("Y".equals(rs.getString("tidak_ada_alergi")));
                ChkKomplikasiSblm.setSelected("Y".equals(rs.getString("komplikasi_sblm")));
                ChkKomplikasiKeluarga.setSelected("Y".equals(rs.getString("komplikasi_keluarga")));
                TTdPra.setText(rs.getString("td_pra")); TNadiPra.setText(rs.getString("nadi_pra"));
                TRespPra.setText(rs.getString("resp_pra")); TSuhuPra.setText(rs.getString("suhu_pra")); TVasPra.setText(rs.getString("vas_pra"));
                loadObstruksi(rs.getString("obstruksi_jalan_napas")); loadThorax(rs.getString("thorax_abdomen")); loadWajah(rs.getString("wajah"));
                boolean bm="Y".equals(rs.getString("buka_mulut")); ChkBukaMulutYa.setSelected(bm); ChkBukaMulutTidak.setSelected(!bm);
                boolean jt="Y".equals(rs.getString("jarak_thyro")); ChkJarakThyroYa.setSelected(jt); ChkJarakThyroTidak.setSelected(!jt);
                CmbMallampati.setSelectedItem(rs.getString("mallampati"));
                boolean gr="Y".equals(rs.getString("gerak_rahang")); ChkGerakRahangYa.setSelected(gr); ChkGerakRahangTidak.setSelected(!gr);
                boolean fl="Y".equals(rs.getString("fleksi_ekstensi")); ChkFleksiYa.setSelected(fl); ChkFleksiTidak.setSelected(!fl);
                boolean py="Y".equals(rs.getString("penyangga_leher")); ChkPenyanggaYa.setSelected(py); ChkPenyanggaTidak.setSelected(!py);
                boolean jns="Y".equals(rs.getString("jalan_napas_sulit")); ChkJalanNapasYa.setSelected(jns); ChkJalanNapasTidak.setSelected(!jns);
                loadPernapasan(rs.getString("sistem_pernapasan")); loadKardio(rs.getString("sistem_kardio"));
                loadNeuro(rs.getString("sistem_neuro")); loadRenal(rs.getString("sistem_renal"));
                loadHepato(rs.getString("sistem_hepato")); loadLainSistem(rs.getString("sistem_lain"));
                boolean mr="Y".equals(rs.getString("merokok")); ChkMerokok.setSelected(mr); ChkMerokokTidak.setSelected(!mr);
                TMerokokPak.setText(rs.getString("merokok_pak")); TMerokokTahun.setText(rs.getString("merokok_tahun"));
                boolean al="Y".equals(rs.getString("alkohol")); ChkAlkohol.setSelected(al); ChkAlkoholTidak.setSelected(!al);
                TAlkohol.setText(rs.getString("alkohol_ket"));
                loadPenunjang(rs.getString("penunjang")); TPenunjangLain.setText(rs.getString("penunjang_lain"));
                loadLab(rs.getString("lab")); TLabLain.setText(rs.getString("lab_lain"));
                TMasalah1.setText(rs.getString("masalah1")); TMasalah2.setText(rs.getString("masalah2")); TMasalah3.setText(rs.getString("masalah3"));
                CmbASA.setSelectedItem(rs.getString("asa_grade")); TPraMedikasi.setText(rs.getString("pra_medikasi"));
            }
        } catch(Exception e){System.out.println("getDataPra:"+e);
        } finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
    }

    // --- simpanPOC ---
    private void simpanPOC() {
        if (!validasiUmum()) return;
        String sql = "INSERT INTO rm_sedasi_poc (no_rawat,tgl_asuhan,jam,kd_dokter_anestesi,nm_dokter_anestesi," +
            "kd_penata,nm_penata,kd_penginput,nm_penginput," +
            "teknik_anestesi,teknik_lain,teknik_alternatif,monitor_invasif,monitor_lain," +
            "kontrol_nyeri,kontrol_lain,risiko,user_input) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            ps = koneksi.prepareStatement(sql); int i=1;
            ps.setString(i++,TNoRw.getText()); ps.setString(i++,getTglAsuhan()); ps.setString(i++,getJam());
            ps.setString(i++,KdDokter.getText()); ps.setString(i++,NmDokter.getText());
            ps.setString(i++,KdPenata.getText()); ps.setString(i++,NmPenata.getText());
            ps.setString(i++,KdPenginput.getText()); ps.setString(i++,NmPenginput.getText());
            ps.setString(i++,buildTeknikAnestesi()); ps.setString(i++,TTeknikLain.getText()); ps.setString(i++,TTeknikAlternatif.getText());
            ps.setString(i++,buildMonitorInvasif()); ps.setString(i++,TMonitorLain.getText());
            ps.setString(i++,buildKontrolNyeri()); ps.setString(i++,TKontrolLain.getText());
            ps.setString(i++,TRisiko.getText()); ps.setString(i++,akses.getkode());
            if (ps.executeUpdate()>0) { JOptionPane.showMessageDialog(null,"Data berhasil disimpan."); tampilPOC(); emptTeksKeepAtas(); }
        } catch(Exception e){System.out.println("simpanPOC:"+e);JOptionPane.showMessageDialog(null,"Gagal: "+e.getMessage());
        } finally{try{if(ps!=null)ps.close();}catch(Exception e){}}
    }
    private void getDataPOC() {
        if (tbPOC.getSelectedRow()==-1) return;
        String noRawat=tabModePOC.getValueAt(tbPOC.getSelectedRow(),0).toString();
        String tglA=tabModePOC.getValueAt(tbPOC.getSelectedRow(),2).toString();
        String jamA=tabModePOC.getValueAt(tbPOC.getSelectedRow(),3).toString();
        TNoRw.setText(noRawat);
        try {
            ps = koneksi.prepareStatement("SELECT * FROM rm_sedasi_poc WHERE no_rawat=? AND tgl_asuhan=? AND jam=?");
            ps.setString(1,noRawat); ps.setString(2,tglA); ps.setString(3,jamA);
            rs = ps.executeQuery();
            if (rs.next()) {
                KdDokter.setText(rs.getString("kd_dokter_anestesi")); NmDokter.setText(rs.getString("nm_dokter_anestesi"));
                KdPenata.setText(rs.getString("kd_penata")); NmPenata.setText(rs.getString("nm_penata"));
                KdPenginput.setText(rs.getString("kd_penginput")); NmPenginput.setText(rs.getString("nm_penginput"));
                loadTeknikAnestesi(rs.getString("teknik_anestesi")); TTeknikLain.setText(rs.getString("teknik_lain"));
                TTeknikAlternatif.setText(rs.getString("teknik_alternatif"));
                loadMonitorInvasif(rs.getString("monitor_invasif")); TMonitorLain.setText(rs.getString("monitor_lain"));
                loadKontrolNyeri(rs.getString("kontrol_nyeri")); TKontrolLain.setText(rs.getString("kontrol_lain"));
                TRisiko.setText(rs.getString("risiko"));
                try{TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(tglA));}catch(Exception ex){}
                String[] j3=jamA.split(":"); Jam.setSelectedItem(j3[0]); Menit.setSelectedItem(j3[1]);
            }
        } catch(Exception e){System.out.println("getDataPOC:"+e);
        } finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
    }

    // --- simpanPraInd ---
    private void simpanPraInd() {
        if (!validasiUmum()) return;
        String sql = "INSERT INTO rm_sedasi_praind (no_rawat,tgl_asuhan,jam,kd_dokter_anestesi,nm_dokter_anestesi," +
            "kd_penata,nm_penata,kd_penginput,nm_penginput,dokter_operator,prosedur_operasi,ruang_ot," +
            "jam_pra_induksi,cek_pra_induksi,mallampati_pra,kondisi_pasien," +
            "td_pra_ind,nadi_pra_ind,resp_pra_ind,spo2,alergi_pra," +
            "eval_keselamatan,perawatan_mata,alat_monitoring,teknik_umum,teknik_sedasi,teknik_regional," +
            "jalan_napas_mgmt,ett_detail,suara_napas," +
            "cairan_koloid,cairan_kristaloid,transfusi_wb,transfusi_prc,transfusi_ffp,transfusi_cryo,cairan_lain," +
            "output_darah,output_urin,output_ngt,user_input) VALUES (" +
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            ps = koneksi.prepareStatement(sql); int i=1;
            ps.setString(i++,TNoRw.getText()); ps.setString(i++,getTglAsuhan()); ps.setString(i++,getJam());
            ps.setString(i++,KdDokter.getText()); ps.setString(i++,NmDokter.getText());
            ps.setString(i++,KdPenata.getText()); ps.setString(i++,NmPenata.getText());
            ps.setString(i++,KdPenginput.getText()); ps.setString(i++,NmPenginput.getText());
            ps.setString(i++,TDokterOperator.getText()); ps.setString(i++,TProsedurOperasi.getText()); ps.setString(i++,TRuangOT.getText());
            ps.setString(i++,getJam()); ps.setString(i++,buildCekPraInd()); ps.setString(i++,buildMallampatiPra()); ps.setString(i++,buildKondisiPasien());
            ps.setString(i++,TTdPraInd.getText()); ps.setString(i++,TNadiPraInd.getText());
            ps.setString(i++,TRespPraInd.getText()); ps.setString(i++,TSpo2.getText()); ps.setString(i++,TAlergiPra.getText());
            ps.setString(i++,buildEvalKeselamatan()); ps.setString(i++,buildPerawatanMata()); ps.setString(i++,buildAlatMonitoring());
            ps.setString(i++,buildTeknikUmum()); ps.setString(i++,buildTeknikSedasi());
            ps.setString(i++,TTeknikRegional.getText()+"\n"+TTeknikRegional2.getText());
            ps.setString(i++,buildJalanNapasMgmt()); ps.setString(i++,buildEttDetail());
            ps.setString(i++,ChkSuaraSama.isSelected()?"Sama":(ChkSuaraTidakSama.isSelected()?"Tidak sama":""));
            ps.setString(i++,TCairanKoloid.getText()); ps.setString(i++,TCairanKristaloid.getText());
            ps.setString(i++,TTransfusiWB.getText()); ps.setString(i++,TTransfusiPRC.getText());
            ps.setString(i++,TTransfusiFFP.getText()); ps.setString(i++,TTransfusiCryo.getText()); ps.setString(i++,TCairanLain.getText());
            ps.setString(i++,TOutputDarah.getText()); ps.setString(i++,TOutputUrin.getText()); ps.setString(i++,TOutputNGT.getText());
            ps.setString(i++,akses.getkode());
            if (ps.executeUpdate()>0) { JOptionPane.showMessageDialog(null,"Data berhasil disimpan."); tampilPraInd(); emptTeksKeepAtas(); }
        } catch(Exception e){System.out.println("simpanPraInd:"+e);JOptionPane.showMessageDialog(null,"Gagal: "+e.getMessage());
        } finally{try{if(ps!=null)ps.close();}catch(Exception e){}}
    }
    private void getDataPraInd() {
        if (tbPraInd.getSelectedRow()==-1) return;
        String noRawat=tabModePraInd.getValueAt(tbPraInd.getSelectedRow(),0).toString();
        String tglA=tabModePraInd.getValueAt(tbPraInd.getSelectedRow(),2).toString();
        String jamA=tabModePraInd.getValueAt(tbPraInd.getSelectedRow(),3).toString();
        TNoRw.setText(noRawat);
        try {
            ps = koneksi.prepareStatement("SELECT * FROM rm_sedasi_praind WHERE no_rawat=? AND tgl_asuhan=? AND jam=?");
            ps.setString(1,noRawat); ps.setString(2,tglA); ps.setString(3,jamA);
            rs = ps.executeQuery();
            if (rs.next()) {
                KdDokter.setText(rs.getString("kd_dokter_anestesi")); NmDokter.setText(rs.getString("nm_dokter_anestesi"));
                KdPenata.setText(rs.getString("kd_penata")); NmPenata.setText(rs.getString("nm_penata"));
                KdPenginput.setText(rs.getString("kd_penginput")); NmPenginput.setText(rs.getString("nm_penginput"));
                TDokterOperator.setText(rs.getString("dokter_operator")); TProsedurOperasi.setText(rs.getString("prosedur_operasi")); TRuangOT.setText(rs.getString("ruang_ot"));
                loadCekPraInd(rs.getString("cek_pra_induksi")); loadMallampatiPra(rs.getString("mallampati_pra")); loadKondisiPasien(rs.getString("kondisi_pasien"));
                TTdPraInd.setText(rs.getString("td_pra_ind")); TNadiPraInd.setText(rs.getString("nadi_pra_ind"));
                TRespPraInd.setText(rs.getString("resp_pra_ind")); TSpo2.setText(rs.getString("spo2")); TAlergiPra.setText(rs.getString("alergi_pra"));
                loadEvalKeselamatan(rs.getString("eval_keselamatan")); loadPerawatanMata(rs.getString("perawatan_mata")); loadAlatMonitoring(rs.getString("alat_monitoring"));
                loadTeknikUmum(rs.getString("teknik_umum")); loadTeknikSedasi(rs.getString("teknik_sedasi"));
                {String tr=rs.getString("teknik_regional");if(tr!=null){int nl=tr.indexOf('\n');if(nl>=0){TTeknikRegional.setText(tr.substring(0,nl));TTeknikRegional2.setText(tr.substring(nl+1));}else{TTeknikRegional.setText(tr);TTeknikRegional2.setText("");}}else{TTeknikRegional.setText("");TTeknikRegional2.setText("");}}
                loadJalanNapasMgmt(rs.getString("jalan_napas_mgmt")); loadEttDetail(rs.getString("ett_detail"));
                {String sv=rs.getString("suara_napas"); ChkSuaraSama.setSelected("Sama".equals(sv)); ChkSuaraTidakSama.setSelected("Tidak sama".equals(sv));}
                TCairanKoloid.setText(rs.getString("cairan_koloid")); TCairanKristaloid.setText(rs.getString("cairan_kristaloid"));
                TTransfusiWB.setText(rs.getString("transfusi_wb")); TTransfusiPRC.setText(rs.getString("transfusi_prc"));
                TTransfusiFFP.setText(rs.getString("transfusi_ffp")); TTransfusiCryo.setText(rs.getString("transfusi_cryo")); TCairanLain.setText(rs.getString("cairan_lain"));
                TOutputDarah.setText(rs.getString("output_darah")); TOutputUrin.setText(rs.getString("output_urin")); TOutputNGT.setText(rs.getString("output_ngt"));
                try{TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(tglA));}catch(Exception ex){}
                String[] j3=jamA.split(":"); Jam.setSelectedItem(j3[0]); Menit.setSelectedItem(j3[1]);
            }
        } catch(Exception e){System.out.println("getDataPraInd:"+e);
        } finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
    }

    // --- simpanPasca ---
    private void simpanPasca() {
        if (!validasiUmum()) return;
        String sql = "INSERT INTO rm_sedasi_pasca (no_rawat,tgl_asuhan,jam,kd_dokter_anestesi,nm_dokter_anestesi," +
            "kd_penata,nm_penata,kd_penginput,nm_penginput,nama_pengkaji,td_pasca,n_pasca,resp_pasca,s_pasca," +
            "skor_aidrete,skor_nyeri,skor_bromage,user_input) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            ps = koneksi.prepareStatement(sql); int i=1;
            ps.setString(i++,TNoRw.getText()); ps.setString(i++,getTglAsuhan()); ps.setString(i++,getJam());
            ps.setString(i++,KdDokter.getText()); ps.setString(i++,NmDokter.getText());
            ps.setString(i++,KdPenata.getText()); ps.setString(i++,NmPenata.getText());
            ps.setString(i++,KdPenginput.getText()); ps.setString(i++,NmPenginput.getText());
            ps.setString(i++,TNamaPengkaji.getText());
            ps.setString(i++,TDPasca.getText()); ps.setString(i++,TNPasca.getText());
            ps.setString(i++,TRespPasca.getText()); ps.setString(i++,TSPasca.getText());
            ps.setString(i++,buildAidreteStr()); ps.setString(i++,buildNyeriStr()); ps.setString(i++,buildBromageStr());
            ps.setString(i++,akses.getkode());
            if (ps.executeUpdate()>0) { JOptionPane.showMessageDialog(null,"Data berhasil disimpan."); tampilPasca(); emptTeksKeepAtas(); }
        } catch(Exception e){System.out.println("simpanPasca:"+e);JOptionPane.showMessageDialog(null,"Gagal: "+e.getMessage());
        } finally{try{if(ps!=null)ps.close();}catch(Exception e){}}
    }
    private void getDataPasca() {
        if (tbPasca.getSelectedRow()==-1) return;
        String noRawat=tabModePasca.getValueAt(tbPasca.getSelectedRow(),0).toString();
        String tglA=tabModePasca.getValueAt(tbPasca.getSelectedRow(),2).toString();
        String jamA=tabModePasca.getValueAt(tbPasca.getSelectedRow(),3).toString();
        TNoRw.setText(noRawat);
        try {
            ps = koneksi.prepareStatement("SELECT * FROM rm_sedasi_pasca WHERE no_rawat=? AND tgl_asuhan=? AND jam=?");
            ps.setString(1,noRawat); ps.setString(2,tglA); ps.setString(3,jamA);
            rs = ps.executeQuery();
            if (rs.next()) {
                KdDokter.setText(rs.getString("kd_dokter_anestesi")); NmDokter.setText(rs.getString("nm_dokter_anestesi"));
                KdPenata.setText(rs.getString("kd_penata")); NmPenata.setText(rs.getString("nm_penata"));
                KdPenginput.setText(rs.getString("kd_penginput")); NmPenginput.setText(rs.getString("nm_penginput"));
                TNamaPengkaji.setText(rs.getString("nama_pengkaji"));
                TDPasca.setText(rs.getString("td_pasca")); TNPasca.setText(rs.getString("n_pasca"));
                TRespPasca.setText(rs.getString("resp_pasca")); TSPasca.setText(rs.getString("s_pasca"));
                loadAidreteStr(rs.getString("skor_aidrete")); loadNyeriStr(rs.getString("skor_nyeri")); loadBromageStr(rs.getString("skor_bromage"));
                try{TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(tglA));}catch(Exception ex){}
                String[] j3=jamA.split(":"); Jam.setSelectedItem(j3[0]); Menit.setSelectedItem(j3[1]);
            }
        } catch(Exception e){System.out.println("getDataPasca:"+e);
        } finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
    }

    // --- simpanRR ---
    private void simpanRR() {
        if (!validasiUmum()) return;
        String sql = "INSERT INTO rm_sedasi_rr (no_rawat,tgl_asuhan,jam,kd_dokter_anestesi,nm_dokter_anestesi," +
            "kd_penata,nm_penata,kd_penginput,nm_penginput,instruksi_rr,user_input) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try {
            ps = koneksi.prepareStatement(sql); int i=1;
            ps.setString(i++,TNoRw.getText()); ps.setString(i++,getTglAsuhan()); ps.setString(i++,getJam());
            ps.setString(i++,KdDokter.getText()); ps.setString(i++,NmDokter.getText());
            ps.setString(i++,KdPenata.getText()); ps.setString(i++,NmPenata.getText());
            ps.setString(i++,KdPenginput.getText()); ps.setString(i++,NmPenginput.getText());
            ps.setString(i++,TInstruksiRR.getText()); ps.setString(i++,akses.getkode());
            if (ps.executeUpdate()>0) { JOptionPane.showMessageDialog(null,"Data berhasil disimpan."); tampilRR(); emptTeksKeepAtas(); }
        } catch(Exception e){System.out.println("simpanRR:"+e);JOptionPane.showMessageDialog(null,"Gagal: "+e.getMessage());
        } finally{try{if(ps!=null)ps.close();}catch(Exception e){}}
    }
    private void getDataRR() {
        if (tbRR.getSelectedRow()==-1) return;
        String noRawat=tabModeRR.getValueAt(tbRR.getSelectedRow(),0).toString();
        String tglA=tabModeRR.getValueAt(tbRR.getSelectedRow(),2).toString();
        String jamA=tabModeRR.getValueAt(tbRR.getSelectedRow(),3).toString();
        TNoRw.setText(noRawat);
        try {
            ps = koneksi.prepareStatement("SELECT * FROM rm_sedasi_rr WHERE no_rawat=? AND tgl_asuhan=? AND jam=?");
            ps.setString(1,noRawat); ps.setString(2,tglA); ps.setString(3,jamA);
            rs = ps.executeQuery();
            if (rs.next()) {
                KdDokter.setText(rs.getString("kd_dokter_anestesi")); NmDokter.setText(rs.getString("nm_dokter_anestesi"));
                KdPenata.setText(rs.getString("kd_penata")); NmPenata.setText(rs.getString("nm_penata"));
                KdPenginput.setText(rs.getString("kd_penginput")); NmPenginput.setText(rs.getString("nm_penginput"));
                TInstruksiRR.setText(rs.getString("instruksi_rr"));
                try{TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(tglA));}catch(Exception ex){}
                String[] j3=jamA.split(":"); Jam.setSelectedItem(j3[0]); Menit.setSelectedItem(j3[1]);
            }
        } catch(Exception e){System.out.println("getDataRR:"+e);
        } finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
    }

    // --- simpanDisch ---
    private void simpanDisch() {
        if (!validasiUmum()) return;
        String sql = "INSERT INTO rm_sedasi_discharge (no_rawat,tgl_asuhan,jam,kd_dokter_anestesi,nm_dokter_anestesi," +
            "kd_penata,nm_penata,kd_penginput,nm_penginput,discharge_ke," +
            "td_discharge,n_discharge,resp_discharge,s_discharge,vas_discharge,instruksi_anestesi,user_input) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            ps = koneksi.prepareStatement(sql); int i=1;
            ps.setString(i++,TNoRw.getText()); ps.setString(i++,getTglAsuhan()); ps.setString(i++,getJam());
            ps.setString(i++,KdDokter.getText()); ps.setString(i++,NmDokter.getText());
            ps.setString(i++,KdPenata.getText()); ps.setString(i++,NmPenata.getText());
            ps.setString(i++,KdPenginput.getText()); ps.setString(i++,NmPenginput.getText());
            ps.setString(i++,buildDischDest());
            ps.setString(i++,TTdDischarge.getText()); ps.setString(i++,TNDischarge.getText());
            ps.setString(i++,TRespDischarge.getText()); ps.setString(i++,TSDischarge.getText()); ps.setString(i++,TVasDischarge.getText());
            ps.setString(i++,TInstruksiAnestesi.getText()); ps.setString(i++,akses.getkode());
            if (ps.executeUpdate()>0) { JOptionPane.showMessageDialog(null,"Data berhasil disimpan."); tampilDisch(); emptTeksKeepAtas(); }
        } catch(Exception e){System.out.println("simpanDisch:"+e);JOptionPane.showMessageDialog(null,"Gagal: "+e.getMessage());
        } finally{try{if(ps!=null)ps.close();}catch(Exception e){}}
    }
    private void getDataDisch() {
        if (tbDisch.getSelectedRow()==-1) return;
        String noRawat=tabModeDisch.getValueAt(tbDisch.getSelectedRow(),0).toString();
        String tglA=tabModeDisch.getValueAt(tbDisch.getSelectedRow(),2).toString();
        String jamA=tabModeDisch.getValueAt(tbDisch.getSelectedRow(),3).toString();
        TNoRw.setText(noRawat);
        try {
            ps = koneksi.prepareStatement("SELECT * FROM rm_sedasi_discharge WHERE no_rawat=? AND tgl_asuhan=? AND jam=?");
            ps.setString(1,noRawat); ps.setString(2,tglA); ps.setString(3,jamA);
            rs = ps.executeQuery();
            if (rs.next()) {
                KdDokter.setText(rs.getString("kd_dokter_anestesi")); NmDokter.setText(rs.getString("nm_dokter_anestesi"));
                KdPenata.setText(rs.getString("kd_penata")); NmPenata.setText(rs.getString("nm_penata"));
                KdPenginput.setText(rs.getString("kd_penginput")); NmPenginput.setText(rs.getString("nm_penginput"));
                loadDischDest(rs.getString("discharge_ke"));
                TTdDischarge.setText(rs.getString("td_discharge")); TNDischarge.setText(rs.getString("n_discharge"));
                TRespDischarge.setText(rs.getString("resp_discharge")); TSDischarge.setText(rs.getString("s_discharge")); TVasDischarge.setText(rs.getString("vas_discharge"));
                TInstruksiAnestesi.setText(rs.getString("instruksi_anestesi"));
                try{TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(tglA));}catch(Exception ex){}
                String[] j3=jamA.split(":"); Jam.setSelectedItem(j3[0]); Menit.setSelectedItem(j3[1]);
            }
        } catch(Exception e){System.out.println("getDataDisch:"+e);
        } finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
    }
    private void getData() {} // kept for legacy compatibility

    // --- simpanDur ---
    private void simpanDur() {
        if (!validasiUmum()) return;
        String sql = "INSERT INTO rm_sedasi_durante (no_rawat,tgl_asuhan,jam,kd_dokter_anestesi,nm_dokter_anestesi," +
            "kd_penata,nm_penata,kd_penginput,nm_penginput," +
            "obat,cairan,oksigen,n2o,air_lmenit,saturasi_o2,fio2,end_tidal_co2,ekg,cvp_pcwp,suhu,urin,lain_lain,circuit," +
            "frekuensi_pernapasan,volume_tidal,peak_pressure,peep,komplikasi,user_input) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            ps = koneksi.prepareStatement(sql); int i=1;
            ps.setString(i++,TNoRw.getText()); ps.setString(i++,getTglAsuhan()); ps.setString(i++,getJam());
            ps.setString(i++,KdDokter.getText()); ps.setString(i++,NmDokter.getText());
            ps.setString(i++,KdPenata.getText()); ps.setString(i++,NmPenata.getText());
            ps.setString(i++,KdPenginput.getText()); ps.setString(i++,NmPenginput.getText());
            ps.setString(i++, serializeDurGrid(obatGridModel, (int)obatHour.getValue(), (int)obatMin.getValue(), obatInterval[0]));
            ps.setString(i++, serializeDurGrid(cairanGridModel, (int)cairanHour.getValue(), (int)cairanMin.getValue(), cairanInterval[0]));
            ps.setString(i++,TOksigen.getText()); ps.setString(i++,TN2O.getText());
            ps.setString(i++,TAirLmenit.getText()); ps.setString(i++,TSaturasiO2.getText());
            ps.setString(i++,TFiO2.getText()); ps.setString(i++,TEndTidalCO2.getText());
            ps.setString(i++,TEKGDur.getText()); ps.setString(i++,TCvpPcwp.getText());
            ps.setString(i++,TSuhuDur.getText()); ps.setString(i++,TUrinDur.getText());
            ps.setString(i++,TLainLainDur.getText());
            ps.setString(i++,ChkJacksonRees.isSelected() ? "Jackson Rees" : "");
            ps.setString(i++,TFrekPernapasan.getText()); ps.setString(i++,TVolumeTidal.getText());
            ps.setString(i++,TPeakPressure.getText()); ps.setString(i++,TPEEP.getText());
            ps.setString(i++,TKomplikasiDur.getText()); ps.setString(i++,akses.getkode());
            if (ps.executeUpdate()>0) { JOptionPane.showMessageDialog(null,"Data berhasil disimpan."); tampilDur(); emptTeksKeepAtas(); }
        } catch(Exception e){System.out.println("simpanDur:"+e);JOptionPane.showMessageDialog(null,"Gagal: "+e.getMessage());
        } finally{try{if(ps!=null)ps.close();}catch(Exception e){}}
    }
    private void getDataDur() {
        if (tbDur.getSelectedRow()==-1) return;
        String noRawat=tabModeDur.getValueAt(tbDur.getSelectedRow(),0).toString();
        String tglA=tabModeDur.getValueAt(tbDur.getSelectedRow(),2).toString();
        String jamA=tabModeDur.getValueAt(tbDur.getSelectedRow(),3).toString();
        TNoRw.setText(noRawat);
        try {
            ps = koneksi.prepareStatement("SELECT * FROM rm_sedasi_durante WHERE no_rawat=? AND tgl_asuhan=? AND jam=?");
            ps.setString(1,noRawat); ps.setString(2,tglA); ps.setString(3,jamA);
            rs = ps.executeQuery();
            if (rs.next()) {
                KdDokter.setText(rs.getString("kd_dokter_anestesi")); NmDokter.setText(rs.getString("nm_dokter_anestesi"));
                KdPenata.setText(rs.getString("kd_penata")); NmPenata.setText(rs.getString("nm_penata"));
                KdPenginput.setText(rs.getString("kd_penginput")); NmPenginput.setText(rs.getString("nm_penginput"));
                deserializeDurGrid(rs.getString("obat"),   obatGridModel,   obatHour,   obatMin,   obatInterval);
                deserializeDurGrid(rs.getString("cairan"), cairanGridModel, cairanHour, cairanMin, cairanInterval);
                TOksigen.setText(rs.getString("oksigen")); TN2O.setText(rs.getString("n2o"));
                TAirLmenit.setText(rs.getString("air_lmenit")); TSaturasiO2.setText(rs.getString("saturasi_o2"));
                TFiO2.setText(rs.getString("fio2")); TEndTidalCO2.setText(rs.getString("end_tidal_co2"));
                TEKGDur.setText(rs.getString("ekg")); TCvpPcwp.setText(rs.getString("cvp_pcwp"));
                TSuhuDur.setText(rs.getString("suhu")); TUrinDur.setText(rs.getString("urin"));
                TLainLainDur.setText(rs.getString("lain_lain"));
                ChkJacksonRees.setSelected("Jackson Rees".equals(rs.getString("circuit")));
                TFrekPernapasan.setText(rs.getString("frekuensi_pernapasan"));
                TVolumeTidal.setText(rs.getString("volume_tidal"));
                TPeakPressure.setText(rs.getString("peak_pressure")); TPEEP.setText(rs.getString("peep"));
                TKomplikasiDur.setText(rs.getString("komplikasi"));
                try{TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(tglA));}catch(Exception ex){}
                String[] j3=jamA.split(":"); Jam.setSelectedItem(j3[0]); Menit.setSelectedItem(j3[1]);
            }
        } catch(Exception e){System.out.println("getDataDur:"+e);
        } finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
    }

    // ===== HELPER METHODS =====
    private void addLbl(JPanel p, String text, int x, int y) {
        Label l = new Label(); l.setText(text); l.setBounds(x, y, 140, 23); p.add(l);
    }
    private void addLblS(JPanel p, String text, int x, int y, int w) {
        Label l = new Label(); l.setText(text); l.setBounds(x, y, w, 23); p.add(l);
    }

    private String buildObstruksi() {
        StringBuilder sb = new StringBuilder();
        if (ChkObsTidakAda.isSelected()) sb.append("TidakAda,");
        if (ChkObsTumor.isSelected()) sb.append("Tumor,");
        if (ChkObsKelCongenital.isSelected()) sb.append("KelCongenital,");
        if (ChkObsStridor.isSelected()) sb.append("Stridor,");
        if (ChkObsGigiProm.isSelected()) sb.append("GigiProm,");
        return sb.length() > 0 ? sb.substring(0, sb.length()-1) : "";
    }
    private String buildThorax() {
        StringBuilder sb = new StringBuilder();
        if (ChkThorNormal.isSelected()) sb.append("Normal,");
        if (ChkThorHamil.isSelected()) sb.append("Hamil,");
        if (ChkThorAscites.isSelected()) sb.append("Ascites,");
        if (ChkThorObstruksi.isSelected()) sb.append("ObstruksiUsus,");
        if (ChkThorObesitas.isSelected()) sb.append("Obesitas,");
        return sb.length() > 0 ? sb.substring(0, sb.length()-1) : "";
    }
    private String buildWajah() {
        StringBuilder sb = new StringBuilder();
        if (ChkWajahNormal.isSelected()) sb.append("Normal,");
        if (ChkWajahRahangSempit.isSelected()) sb.append("RahangSempit,");
        if (ChkWajahGigiProm.isSelected()) sb.append("GigiProm,");
        if (ChkWajahRambutWajah.isSelected()) sb.append("RambutWajah,");
        if (ChkWajahGigiGoyang.isSelected()) sb.append("GigiGoyang,");
        if (ChkWajahEdema.isSelected()) sb.append("Edema,");
        if (ChkWajahKelRahang.isSelected()) sb.append("KelRahang,");
        return sb.length() > 0 ? sb.substring(0, sb.length()-1) : "";
    }
    private void loadObstruksi(String val) {
        if (val == null) val = "";
        ChkObsTidakAda.setSelected(val.contains("TidakAda"));
        ChkObsTumor.setSelected(val.contains("Tumor"));
        ChkObsKelCongenital.setSelected(val.contains("KelCongenital"));
        ChkObsStridor.setSelected(val.contains("Stridor"));
        ChkObsGigiProm.setSelected(val.contains("GigiProm"));
    }
    private void loadThorax(String val) {
        if (val == null) val = "";
        ChkThorNormal.setSelected(val.contains("Normal"));
        ChkThorHamil.setSelected(val.contains("Hamil"));
        ChkThorAscites.setSelected(val.contains("Ascites"));
        ChkThorObstruksi.setSelected(val.contains("ObstruksiUsus"));
        ChkThorObesitas.setSelected(val.contains("Obesitas"));
    }
    private void loadWajah(String val) {
        if (val == null) val = "";
        ChkWajahNormal.setSelected(val.contains("Normal"));
        ChkWajahRahangSempit.setSelected(val.contains("RahangSempit"));
        ChkWajahGigiProm.setSelected(val.contains("GigiProm"));
        ChkWajahRambutWajah.setSelected(val.contains("RambutWajah"));
        ChkWajahGigiGoyang.setSelected(val.contains("GigiGoyang"));
        ChkWajahEdema.setSelected(val.contains("Edema"));
        ChkWajahKelRahang.setSelected(val.contains("KelRahang"));
    }
    private String buildPernapasan() {
        StringBuilder sb = new StringBuilder();
        if (ChkNapasAsma.isSelected()) sb.append("Asma,");
        if (ChkNapasBatukProduktif.isSelected()) sb.append("BatukProduktif,");
        if (ChkNapasBronkitis.isSelected()) sb.append("Bronkitis,");
        if (ChkNapasSesak.isSelected()) sb.append("Sesak,");
        if (ChkNapasPPOK.isSelected()) sb.append("PPOK,");
        if (ChkNapasTuberkulosis.isSelected()) sb.append("Tuberkulosis,");
        if (ChkNapasOrthopnea.isSelected()) sb.append("Orthopnea,");
        if (ChkNapasPneumonia.isSelected()) sb.append("Pneumonia,");
        if (ChkNapasNormal.isSelected()) sb.append("Normal,");
        return sb.length()>0?sb.substring(0,sb.length()-1):"";
    }
    private void loadPernapasan(String v) {
        if(v==null)v="";
        ChkNapasAsma.setSelected(v.contains("Asma")); ChkNapasBatukProduktif.setSelected(v.contains("BatukProduktif"));
        ChkNapasBronkitis.setSelected(v.contains("Bronkitis")); ChkNapasSesak.setSelected(v.contains("Sesak"));
        ChkNapasPPOK.setSelected(v.contains("PPOK")); ChkNapasTuberkulosis.setSelected(v.contains("Tuberkulosis"));
        ChkNapasOrthopnea.setSelected(v.contains("Orthopnea")); ChkNapasPneumonia.setSelected(v.contains("Pneumonia"));
        ChkNapasNormal.setSelected(v.contains("Normal"));
    }
    private String buildKardio() {
        StringBuilder sb = new StringBuilder();
        if (ChkKardioEKG.isSelected()) sb.append("EKG,");
        if (ChkKardioHipertensi.isSelected()) sb.append("Hipertensi,");
        if (ChkKardioAngina.isSelected()) sb.append("Angina,");
        if (ChkKardioInfark.isSelected()) sb.append("Infark,");
        if (ChkKardioASHD.isSelected()) sb.append("ASHD,");
        if (ChkKardioMurmur.isSelected()) sb.append("Murmur,");
        if (ChkKardioCHF.isSelected()) sb.append("CHF,");
        if (ChkKardioPacemaker.isSelected()) sb.append("Pacemaker,");
        if (ChkKardioDisritmia.isSelected()) sb.append("Disritmia,");
        if (ChkKardioDemamRheuma.isSelected()) sb.append("DemamRheuma,");
        if (ChkKardioLimitasi.isSelected()) sb.append("Limitasi,");
        if (ChkKardioPenyakitKatup.isSelected()) sb.append("PenyakitKatup,");
        if (ChkKardioNormal.isSelected()) sb.append("Normal,");
        return sb.length()>0?sb.substring(0,sb.length()-1):"";
    }
    private void loadKardio(String v) {
        if(v==null)v="";
        ChkKardioEKG.setSelected(v.contains("EKG")); ChkKardioHipertensi.setSelected(v.contains("Hipertensi"));
        ChkKardioAngina.setSelected(v.contains("Angina")); ChkKardioInfark.setSelected(v.contains("Infark"));
        ChkKardioASHD.setSelected(v.contains("ASHD")); ChkKardioMurmur.setSelected(v.contains("Murmur"));
        ChkKardioCHF.setSelected(v.contains("CHF")); ChkKardioPacemaker.setSelected(v.contains("Pacemaker"));
        ChkKardioDisritmia.setSelected(v.contains("Disritmia")); ChkKardioDemamRheuma.setSelected(v.contains("DemamRheuma"));
        ChkKardioLimitasi.setSelected(v.contains("Limitasi")); ChkKardioPenyakitKatup.setSelected(v.contains("PenyakitKatup"));
        ChkKardioNormal.setSelected(v.contains("Normal"));
    }
    private String buildNeuro() {
        StringBuilder sb = new StringBuilder();
        if (ChkNeuroArthritis.isSelected()) sb.append("Arthritis,");
        if (ChkNeuroKelemahanOtot.isSelected()) sb.append("KelemahanOtot,");
        if (ChkNeuroCVA.isSelected()) sb.append("CVA,");
        if (ChkNeuroParalisis.isSelected()) sb.append("Paralisis,");
        if (ChkNeuroDegeneratif.isSelected()) sb.append("Degeneratif,");
        if (ChkNeuroParesthesia.isSelected()) sb.append("Paresthesia,");
        if (ChkNeuroNyeriKepala.isSelected()) sb.append("NyeriKepala,");
        if (ChkNeuroPingsan.isSelected()) sb.append("Pingsan,");
        if (ChkNeuroPenurunanKesadaran.isSelected()) sb.append("PenurunanKesadaran,");
        if (ChkNeuroKejang.isSelected()) sb.append("Kejang,");
        if (ChkNeuroGangguanTulang.isSelected()) sb.append("GangguanTulang,");
        if (ChkNeuroNormal.isSelected()) sb.append("Normal,");
        return sb.length()>0?sb.substring(0,sb.length()-1):"";
    }
    private void loadNeuro(String v) {
        if(v==null)v="";
        ChkNeuroArthritis.setSelected(v.contains("Arthritis")); ChkNeuroKelemahanOtot.setSelected(v.contains("KelemahanOtot"));
        ChkNeuroCVA.setSelected(v.contains("CVA")); ChkNeuroParalisis.setSelected(v.contains("Paralisis"));
        ChkNeuroDegeneratif.setSelected(v.contains("Degeneratif")); ChkNeuroParesthesia.setSelected(v.contains("Paresthesia"));
        ChkNeuroNyeriKepala.setSelected(v.contains("NyeriKepala")); ChkNeuroPingsan.setSelected(v.contains("Pingsan"));
        ChkNeuroPenurunanKesadaran.setSelected(v.contains("PenurunanKesadaran")); ChkNeuroKejang.setSelected(v.contains("Kejang"));
        ChkNeuroGangguanTulang.setSelected(v.contains("GangguanTulang")); ChkNeuroNormal.setSelected(v.contains("Normal"));
    }
    private String buildRenal() {
        StringBuilder sb = new StringBuilder();
        if (ChkRenalDiabetes.isSelected()) sb.append("Diabetes,");
        if (ChkRenalGagalGinjal.isSelected()) sb.append("GagalGinjal,");
        if (ChkRenalTiroid.isSelected()) sb.append("Tiroid,");
        if (ChkRenalRetensiUrin.isSelected()) sb.append("RetensiUrin,");
        if (ChkRenalInfeksiSaluran.isSelected()) sb.append("InfeksiSaluran,");
        if (ChkRenalPenurunanBB.isSelected()) sb.append("PenurunanBB,");
        if (ChkRenalNormal.isSelected()) sb.append("Normal,");
        return sb.length()>0?sb.substring(0,sb.length()-1):"";
    }
    private void loadRenal(String v) {
        if(v==null)v="";
        ChkRenalDiabetes.setSelected(v.contains("Diabetes")); ChkRenalGagalGinjal.setSelected(v.contains("GagalGinjal"));
        ChkRenalTiroid.setSelected(v.contains("Tiroid")); ChkRenalRetensiUrin.setSelected(v.contains("RetensiUrin"));
        ChkRenalInfeksiSaluran.setSelected(v.contains("InfeksiSaluran")); ChkRenalPenurunanBB.setSelected(v.contains("PenurunanBB"));
        ChkRenalNormal.setSelected(v.contains("Normal"));
    }
    private String buildHepato() {
        StringBuilder sb = new StringBuilder();
        if (ChkHepatoObstruksiUsus.isSelected()) sb.append("ObstruksiUsus,");
        if (ChkHepatoSirosis.isSelected()) sb.append("Sirosis,");
        if (ChkHepatoHepatitis.isSelected()) sb.append("Hepatitis,");
        if (ChkHepatoHiatal.isSelected()) sb.append("Hiatal,");
        if (ChkHepatoMualMuntah.isSelected()) sb.append("MualMuntah,");
        if (ChkHepatoUlkus.isSelected()) sb.append("Ulkus,");
        if (ChkHepatoNormal.isSelected()) sb.append("Normal,");
        return sb.length()>0?sb.substring(0,sb.length()-1):"";
    }
    private void loadHepato(String v) {
        if(v==null)v="";
        ChkHepatoObstruksiUsus.setSelected(v.contains("ObstruksiUsus")); ChkHepatoSirosis.setSelected(v.contains("Sirosis"));
        ChkHepatoHepatitis.setSelected(v.contains("Hepatitis")); ChkHepatoHiatal.setSelected(v.contains("Hiatal"));
        ChkHepatoMualMuntah.setSelected(v.contains("MualMuntah")); ChkHepatoUlkus.setSelected(v.contains("Ulkus"));
        ChkHepatoNormal.setSelected(v.contains("Normal"));
    }
    private String buildLainSistem() {
        StringBuilder sb = new StringBuilder();
        if (ChkLainAnemia.isSelected()) sb.append("Anemia,");
        if (ChkLainKehamilan.isSelected()) sb.append("Kehamilan,");
        if (ChkLainDehidrasi.isSelected()) sb.append("Dehidrasi,");
        if (ChkLainKemoterapi.isSelected()) sb.append("Kemoterapi,");
        if (ChkLainHemofilia.isSelected()) sb.append("Hemofilia,");
        if (ChkLainSickleCell.isSelected()) sb.append("SickleCell,");
        if (ChkLainImunosupresi.isSelected()) sb.append("Imunosupresi,");
        if (ChkLainRiwayatSteroid.isSelected()) sb.append("RiwayatSteroid,");
        if (ChkLainKanker.isSelected()) sb.append("Kanker,");
        if (ChkLainRiwayatTransfusi.isSelected()) sb.append("RiwayatTransfusi,");
        if (ChkLainGangguanPerdarahan.isSelected()) sb.append("GangguanPerdarahan,");
        if (ChkLainNormal.isSelected()) sb.append("Normal,");
        return sb.length()>0?sb.substring(0,sb.length()-1):"";
    }
    private void loadLainSistem(String v) {
        if(v==null)v="";
        ChkLainAnemia.setSelected(v.contains("Anemia")); ChkLainKehamilan.setSelected(v.contains("Kehamilan"));
        ChkLainDehidrasi.setSelected(v.contains("Dehidrasi")); ChkLainKemoterapi.setSelected(v.contains("Kemoterapi"));
        ChkLainHemofilia.setSelected(v.contains("Hemofilia")); ChkLainSickleCell.setSelected(v.contains("SickleCell"));
        ChkLainImunosupresi.setSelected(v.contains("Imunosupresi")); ChkLainRiwayatSteroid.setSelected(v.contains("RiwayatSteroid"));
        ChkLainKanker.setSelected(v.contains("Kanker")); ChkLainRiwayatTransfusi.setSelected(v.contains("RiwayatTransfusi"));
        ChkLainGangguanPerdarahan.setSelected(v.contains("GangguanPerdarahan")); ChkLainNormal.setSelected(v.contains("Normal"));
    }
    private String buildPenunjang() {
        StringBuilder sb = new StringBuilder();
        if (ChkPenunjangEKG.isSelected()) sb.append("EKG,");
        if (ChkPenunjangEvaluasiParu.isSelected()) sb.append("EvaluasiParu,");
        if (ChkPenunjangXRay.isSelected()) sb.append("XRay,");
        return sb.length()>0?sb.substring(0,sb.length()-1):"";
    }
    private void loadPenunjang(String v) {
        if(v==null)v="";
        ChkPenunjangEKG.setSelected(v.contains("EKG")); ChkPenunjangEvaluasiParu.setSelected(v.contains("EvaluasiParu"));
        ChkPenunjangXRay.setSelected(v.contains("XRay"));
    }
    private String buildLab() {
        StringBuilder sb = new StringBuilder();
        if (ChkLabHbHt.isSelected()) sb.append("HbHt,");
        if (ChkLabUrinalisis.isSelected()) sb.append("Urinalisis,");
        if (ChkLabElektrolit.isSelected()) sb.append("Elektrolit,");
        return sb.length()>0?sb.substring(0,sb.length()-1):"";
    }
    private void loadLab(String v) {
        if(v==null)v="";
        ChkLabHbHt.setSelected(v.contains("HbHt")); ChkLabUrinalisis.setSelected(v.contains("Urinalisis"));
        ChkLabElektrolit.setSelected(v.contains("Elektrolit"));
    }
    private String buildTeknikAnestesi() {
        StringBuilder sb = new StringBuilder();
        if (ChkTeknikMAC.isSelected())          sb.append("MAC,");
        if (ChkTeknikSedasi.isSelected())       sb.append("Sedasi,");
        if (ChkTeknikAnestesiUmum.isSelected()) sb.append("Anestesi Umum,");
        if (ChkTeknikRegional.isSelected())     sb.append("Regional,");
        if (ChkTeknikKombinasi.isSelected())    sb.append("Kombinasi,");
        if (ChkTeknikSpinal.isSelected())       sb.append("Spinal,");
        if (ChkTeknikEpidural.isSelected())     sb.append("Epidural,");
        if (ChkTeknikPeriperal.isSelected())    sb.append("Periperal,");
        return sb.length()>0 ? sb.substring(0,sb.length()-1) : "";
    }
    private void loadTeknikAnestesi(String v) {
        if(v==null)v="";
        ChkTeknikMAC.setSelected(v.contains("MAC"));
        ChkTeknikSedasi.setSelected(v.contains("Sedasi"));
        ChkTeknikAnestesiUmum.setSelected(v.contains("Anestesi Umum"));
        ChkTeknikRegional.setSelected(v.contains("Regional"));
        ChkTeknikKombinasi.setSelected(v.contains("Kombinasi"));
        ChkTeknikSpinal.setSelected(v.contains("Spinal"));
        ChkTeknikEpidural.setSelected(v.contains("Epidural"));
        ChkTeknikPeriperal.setSelected(v.contains("Periperal"));
    }
    private String buildMonitorInvasif() {
        StringBuilder sb = new StringBuilder();
        if (ChkMonitorALine.isSelected()) sb.append("A Line,");
        if (ChkMonitorCVC.isSelected())   sb.append("CVC,");
        if (ChkMonitorPAC.isSelected())   sb.append("PAC,");
        if (ChkMonitorTEE.isSelected())   sb.append("TEE,");
        return sb.length()>0 ? sb.substring(0,sb.length()-1) : "";
    }
    private void loadMonitorInvasif(String v) {
        if(v==null)v="";
        ChkMonitorALine.setSelected(v.contains("A Line"));
        ChkMonitorCVC.setSelected(v.contains("CVC"));
        ChkMonitorPAC.setSelected(v.contains("PAC"));
        ChkMonitorTEE.setSelected(v.contains("TEE"));
    }
    private String buildKontrolNyeri() {
        StringBuilder sb = new StringBuilder();
        if (ChkKontrolIntravena.isSelected()) sb.append("Intravena,");
        if (ChkKontrolEpidural.isSelected())  sb.append("Epidural,");
        if (ChkKontrolSpinal.isSelected())    sb.append("Spinal,");
        if (ChkKontrolPCA.isSelected())       sb.append("PCA,");
        return sb.length()>0 ? sb.substring(0,sb.length()-1) : "";
    }
    private void loadKontrolNyeri(String v) {
        if(v==null)v="";
        ChkKontrolIntravena.setSelected(v.contains("Intravena"));
        ChkKontrolEpidural.setSelected(v.contains("Epidural"));
        ChkKontrolSpinal.setSelected(v.contains("Spinal"));
        ChkKontrolPCA.setSelected(v.contains("PCA"));
    }
    private String buildCekPraInd() {
        StringBuilder sb = new StringBuilder();
        if (ChkPasienDiidentifikasi.isSelected()) sb.append("Pasien|");
        if (ChkRekamMedis.isSelected()) sb.append("RekamMedis|");
        if (ChkInformedConsent.isSelected()) sb.append("InformedConsent|");
        if (ChkPuasa.isSelected()) sb.append("Puasa|");
        sb.append("PuasaJam=").append(TPuasaJam.getText());
        return sb.toString();
    }
    private void loadCekPraInd(String v) {
        if(v==null)v="";
        ChkPasienDiidentifikasi.setSelected(v.contains("Pasien"));
        ChkRekamMedis.setSelected(v.contains("RekamMedis"));
        ChkInformedConsent.setSelected(v.contains("InformedConsent"));
        ChkPuasa.setSelected(v.contains("Puasa|") || v.startsWith("Puasa"));
        int i = v.indexOf("PuasaJam=");
        if(i>=0){ String rest=v.substring(i+9); int p=rest.indexOf('|'); TPuasaJam.setText(p>=0?rest.substring(0,p):rest); }
    }
    private String buildMallampatiPra() {
        StringBuilder sb = new StringBuilder();
        if(ChkMallampatiI.isSelected()) sb.append("I|");
        if(ChkMallampatiII.isSelected()) sb.append("II|");
        if(ChkMallampatiIII.isSelected()) sb.append("III|");
        if(ChkMallampatiIV.isSelected()) sb.append("IV|");
        return sb.length()>0?sb.substring(0,sb.length()-1):"";
    }
    private void loadMallampatiPra(String v) {
        if(v==null)v="";
        ChkMallampatiI.setSelected(v.contains("I")); ChkMallampatiII.setSelected(v.contains("II"));
        ChkMallampatiIII.setSelected(v.contains("III")); ChkMallampatiIV.setSelected(v.contains("IV"));
    }
    private String buildKondisiPasien() {
        StringBuilder sb = new StringBuilder();
        if(ChkKondisiTenang.isSelected()) sb.append("Tenang|");
        if(ChkKondisiSadar.isSelected()) sb.append("Sadar|");
        if(ChkKondisiCemas.isSelected()) sb.append("Cemas|");
        if(ChkKondisiDisorientasi.isSelected()) sb.append("Disorientasi|");
        if(ChkKondisiTdkKooperatif.isSelected()) sb.append("TdkKooperatif|");
        if(ChkKondisiTdkRespon.isSelected()) sb.append("TdkRespon|");
        return sb.length()>0?sb.substring(0,sb.length()-1):"";
    }
    private void loadKondisiPasien(String v) {
        if(v==null)v="";
        ChkKondisiTenang.setSelected(v.contains("Tenang")); ChkKondisiSadar.setSelected(v.contains("Sadar"));
        ChkKondisiCemas.setSelected(v.contains("Cemas")); ChkKondisiDisorientasi.setSelected(v.contains("Disorientasi"));
        ChkKondisiTdkKooperatif.setSelected(v.contains("TdkKooperatif")); ChkKondisiTdkRespon.setSelected(v.contains("TdkRespon"));
    }
    private String buildEvalKeselamatan() {
        StringBuilder sb = new StringBuilder();
        if(ChkPengecekanMesin.isSelected()) sb.append("Pengecekan|");
        if(ChkTaliPengaman.isSelected()) sb.append("Tali|");
        if(ChkPenyanggaLengan.isSelected()) sb.append("Lengan|");
        if(ChkPressurePoint.isSelected()) sb.append("Pressure|");
        if(ChkPenyanggaKetiak.isSelected()) sb.append("Ketiak|");
        if(ChkTanganTerlindungi.isSelected()) sb.append("Tangan|");
        return sb.length()>0?sb.substring(0,sb.length()-1):"";
    }
    private void loadEvalKeselamatan(String v) {
        if(v==null)v="";
        ChkPengecekanMesin.setSelected(v.contains("Pengecekan")); ChkTaliPengaman.setSelected(v.contains("Tali"));
        ChkPenyanggaLengan.setSelected(v.contains("Lengan")); ChkPressurePoint.setSelected(v.contains("Pressure"));
        ChkPenyanggaKetiak.setSelected(v.contains("Ketiak")); ChkTanganTerlindungi.setSelected(v.contains("Tangan"));
    }
    private String buildPerawatanMata() {
        StringBuilder sb = new StringBuilder();
        if(ChkMatOintment.isSelected()) sb.append("Ointment|");
        if(ChkMatSaline.isSelected()) sb.append("Saline|");
        if(ChkMatTapped.isSelected()) sb.append("Tapped|");
        if(ChkMatPads.isSelected()) sb.append("Pads|");
        if(ChkMatGoggles.isSelected()) sb.append("Goggles|");
        return sb.length()>0?sb.substring(0,sb.length()-1):"";
    }
    private void loadPerawatanMata(String v) {
        if(v==null)v="";
        ChkMatOintment.setSelected(v.contains("Ointment")); ChkMatSaline.setSelected(v.contains("Saline"));
        ChkMatTapped.setSelected(v.contains("Tapped")); ChkMatPads.setSelected(v.contains("Pads"));
        ChkMatGoggles.setSelected(v.contains("Goggles"));
    }
    private String buildAlatMonitoring() {
        StringBuilder sb = new StringBuilder();
        if(ChkMonStetoskop.isSelected()) sb.append("Stetoskop|");
        if(ChkMonPrekordial.isSelected()) sb.append("Prekordial|");
        if(ChkMonNIBP.isSelected()) sb.append("NIBP|");
        if(ChkMonNIBPKiri.isSelected()) sb.append("NIBPKiri|");
        if(ChkMonNIBPKanan.isSelected()) sb.append("NIBPKanan|");
        if(ChkMonEKG.isSelected()) sb.append("EKG|");
        sb.append("EKGLead=").append(TEKGLead.getText()).append("|");
        if(ChkMonEsoph.isSelected()) sb.append("Esoph|");
        if(ChkMonPulseOxy.isSelected()) sb.append("PulseOxy|");
        if(ChkMonSensorO2.isSelected()) sb.append("SensorO2|");
        if(ChkMonSelimut.isSelected()) sb.append("Selimut|");
        if(ChkMonGasAnalyzer.isSelected()) sb.append("GasAnalyzer|");
        if(ChkMonEEG.isSelected()) sb.append("EEG|");
        if(ChkMonNerveStimulator.isSelected()) sb.append("Nerve|");
        if(ChkMonKateter.isSelected()) sb.append("Kateter|");
        if(ChkMonDoppler.isSelected()) sb.append("Doppler|");
        if(ChkMonNGTOGT.isSelected()) sb.append("NGTOGT|");
        if(ChkMonBIS.isSelected()) sb.append("BIS|");
        if(ChkMonIntravena.isSelected()) sb.append("Intravena|");
        if(ChkMonArteriLine.isSelected()) sb.append("ArteriLine|");
        if(ChkMonCVC.isSelected()) sb.append("CVC|");
        if(ChkMonPAC.isSelected()) sb.append("PAC|");
        return sb.toString();
    }
    private void loadAlatMonitoring(String v) {
        if(v==null)v="";
        ChkMonStetoskop.setSelected(v.contains("Stetoskop")); ChkMonPrekordial.setSelected(v.contains("Prekordial"));
        ChkMonNIBP.setSelected(v.contains("NIBP|")||v.startsWith("NIBP")); ChkMonNIBPKiri.setSelected(v.contains("NIBPKiri")); ChkMonNIBPKanan.setSelected(v.contains("NIBPKanan"));
        ChkMonEKG.setSelected(v.contains("EKG|")||v.startsWith("EKG")); ChkMonEsoph.setSelected(v.contains("Esoph"));
        int i=v.indexOf("EKGLead="); if(i>=0){String r=v.substring(i+8);int p=r.indexOf('|');TEKGLead.setText(p>=0?r.substring(0,p):r);}
        ChkMonPulseOxy.setSelected(v.contains("PulseOxy")); ChkMonSensorO2.setSelected(v.contains("SensorO2"));
        ChkMonSelimut.setSelected(v.contains("Selimut")); ChkMonGasAnalyzer.setSelected(v.contains("GasAnalyzer"));
        ChkMonEEG.setSelected(v.contains("EEG|")||v.startsWith("EEG")); ChkMonNerveStimulator.setSelected(v.contains("Nerve"));
        ChkMonKateter.setSelected(v.contains("Kateter")); ChkMonDoppler.setSelected(v.contains("Doppler"));
        ChkMonNGTOGT.setSelected(v.contains("NGTOGT")); ChkMonBIS.setSelected(v.contains("BIS")); ChkMonIntravena.setSelected(v.contains("Intravena"));
        ChkMonArteriLine.setSelected(v.contains("ArteriLine")); ChkMonCVC.setSelected(v.contains("CVC")); ChkMonPAC.setSelected(v.contains("PAC"));
    }
    private String buildTeknikUmum() {
        StringBuilder sb = new StringBuilder();
        if(ChkTUPreOxy.isSelected()) sb.append("PreOxy|");
        if(ChkTUIntravena.isSelected()) sb.append("Intravena|");
        if(ChkTUInhalasi.isSelected()) sb.append("Inhalasi|");
        if(ChkTUIntramuskular.isSelected()) sb.append("Intramuskular|");
        if(ChkTURSI.isSelected()) sb.append("RSI|");
        if(ChkTUCricoid.isSelected()) sb.append("Cricoid|");
        return sb.toString();
    }
    private void loadTeknikUmum(String v) {
        if(v==null)v="";
        ChkTUPreOxy.setSelected(v.contains("PreOxy")); ChkTUIntravena.setSelected(v.contains("Intravena"));
        ChkTUInhalasi.setSelected(v.contains("Inhalasi")); ChkTUIntramuskular.setSelected(v.contains("Intramuskular"));
        ChkTURSI.setSelected(v.contains("RSI")); ChkTUCricoid.setSelected(v.contains("Cricoid"));
    }
    private String buildTeknikSedasi() {
        StringBuilder sb = new StringBuilder();
        if(ChkSedasiRingan.isSelected()) sb.append("Ringan|");
        if(ChkSedasiSedang.isSelected()) sb.append("Sedang|");
        if(ChkSedasiDalam.isSelected()) sb.append("Dalam|");
        return sb.toString();
    }
    private void loadTeknikSedasi(String v) {
        if(v==null)v="";
        ChkSedasiRingan.setSelected(v.contains("Ringan")); ChkSedasiSedang.setSelected(v.contains("Sedang")); ChkSedasiDalam.setSelected(v.contains("Dalam"));
    }
    private String buildJalanNapasMgmt() {
        StringBuilder sb = new StringBuilder();
        if(ChkJNPernapasanSpontan.isSelected()) sb.append("PernapasanSpontan|");
        if(ChkJNNasalO2.isSelected()) sb.append("NasalO2|");
        if(ChkJNMask.isSelected()) sb.append("Mask|");
        if(ChkJNSupraglotic.isSelected()) sb.append("Supraglotic|");
        sb.append("SGJenis=").append(TJNJenis.getText()).append("|");
        sb.append("SGUkuran=").append(TJNUkuran.getText()).append("|");
        if(ChkJNTrakeostomi.isSelected()) sb.append("Trakeostomi|");
        if(ChkJNIntubasi.isSelected()) sb.append("Intubasi|");
        if(ChkJNNasal.isSelected()) sb.append("IntNasal|");
        if(ChkJNOral.isSelected()) sb.append("IntOral|");
        if(ChkJNSleep.isSelected()) sb.append("Sleep|");
        if(ChkJNApnea.isSelected()) sb.append("Apnea|");
        if(ChkJNDirect.isSelected()) sb.append("Direct|");
        if(ChkJNAwake.isSelected()) sb.append("Awake|");
        if(ChkJNNonApnea.isSelected()) sb.append("NonApnea|");
        if(ChkJNBlind.isSelected()) sb.append("Blind|");
        if(ChkJNStyle.isSelected()) sb.append("Style|");
        if(ChkJNMagill.isSelected()) sb.append("Magill|");
        if(ChkJNLaryngoscope.isSelected()) sb.append("Laryngoscope|");
        sb.append("LUkuran=").append(TLarynUkuran.getText()).append("|");
        if(ChkJNVideoLaryngoscope.isSelected()) sb.append("VideoLaryngoscope|");
        sb.append("VLUkuran=").append(TVLUkuran.getText()).append("|");
        if(ChkJNFiberoptic.isSelected()) sb.append("Fiberoptic|");
        sb.append("FPercobaan=").append(TFiberPercobaan.getText()).append("|");
        if(ChkSuaraSama.isSelected()) sb.append("SuaraSama|");
        if(ChkSuaraTidakSama.isSelected()) sb.append("SuaraTidakSama|");
        return sb.toString();
    }
    private void loadJalanNapasMgmt(String v) {
        if(v==null)v="";
        ChkJNPernapasanSpontan.setSelected(v.contains("PernapasanSpontan")); ChkJNNasalO2.setSelected(v.contains("NasalO2")); ChkJNMask.setSelected(v.contains("Mask|")||v.startsWith("Mask"));
        ChkJNSupraglotic.setSelected(v.contains("Supraglotic"));
        int i; String r;
        i=v.indexOf("SGJenis="); if(i>=0){r=v.substring(i+8);int p=r.indexOf('|');TJNJenis.setText(p>=0?r.substring(0,p):r);}
        i=v.indexOf("SGUkuran="); if(i>=0){r=v.substring(i+9);int p=r.indexOf('|');TJNUkuran.setText(p>=0?r.substring(0,p):r);}
        ChkJNTrakeostomi.setSelected(v.contains("Trakeostomi")); ChkJNIntubasi.setSelected(v.contains("Intubasi")); ChkJNNasal.setSelected(v.contains("IntNasal")); ChkJNOral.setSelected(v.contains("IntOral"));
        ChkJNSleep.setSelected(v.contains("Sleep")); ChkJNApnea.setSelected(v.contains("Apnea")); ChkJNDirect.setSelected(v.contains("Direct"));
        ChkJNAwake.setSelected(v.contains("Awake")); ChkJNNonApnea.setSelected(v.contains("NonApnea")); ChkJNBlind.setSelected(v.contains("Blind"));
        ChkJNStyle.setSelected(v.contains("Style")); ChkJNMagill.setSelected(v.contains("Magill")); ChkJNLaryngoscope.setSelected(v.contains("Laryngoscope|")||v.contains("Laryngoscope\n"));
        i=v.indexOf("LUkuran="); if(i>=0){r=v.substring(i+8);int p=r.indexOf('|');TLarynUkuran.setText(p>=0?r.substring(0,p):r);}
        ChkJNVideoLaryngoscope.setSelected(v.contains("VideoLaryngoscope"));
        i=v.indexOf("VLUkuran="); if(i>=0){r=v.substring(i+9);int p=r.indexOf('|');TVLUkuran.setText(p>=0?r.substring(0,p):r);}
        ChkJNFiberoptic.setSelected(v.contains("Fiberoptic"));
        i=v.indexOf("FPercobaan="); if(i>=0){r=v.substring(i+11);int p=r.indexOf('|');TFiberPercobaan.setText(p>=0?r.substring(0,p):r);}
        ChkSuaraSama.setSelected(v.contains("SuaraSama")); ChkSuaraTidakSama.setSelected(v.contains("SuaraTidakSama"));
    }
    private String buildEttDetail() {
        StringBuilder sb = new StringBuilder();
        if(ChkETTRegular.isSelected()) sb.append("Regular|");
        if(ChkETTReformed.isSelected()) sb.append("Reformed|");
        if(ChkETTPreFormedBend.isSelected()) sb.append("PreFormedBend|");
        if(ChkETTDoubleLumenL.isSelected()) sb.append("DoubleLumenL|");
        if(ChkETTDoubleLumenR.isSelected()) sb.append("DoubleLumenR|");
        sb.append("Size=").append(TETTSize.getText()).append("|");
        sb.append("Batas=").append(TETTBatas.getText()).append("|");
        if(ChkETTCuffed.isSelected()) sb.append("Cuffed|");
        if(ChkETTUncuffed.isSelected()) sb.append("Uncuffed|");
        if(ChkETTUdara.isSelected()) sb.append("Udara|");
        if(ChkETTAir.isSelected()) sb.append("Air|");
        return sb.toString();
    }
    private void loadEttDetail(String v) {
        if(v==null)v="";
        ChkETTRegular.setSelected(v.contains("Regular")); ChkETTReformed.setSelected(v.contains("Reformed"));
        ChkETTPreFormedBend.setSelected(v.contains("PreFormedBend")); ChkETTDoubleLumenL.setSelected(v.contains("DoubleLumenL")); ChkETTDoubleLumenR.setSelected(v.contains("DoubleLumenR"));
        int i; String r;
        i=v.indexOf("Size="); if(i>=0){r=v.substring(i+5);int p=r.indexOf('|');TETTSize.setText(p>=0?r.substring(0,p):r);}
        i=v.indexOf("Batas="); if(i>=0){r=v.substring(i+6);int p=r.indexOf('|');TETTBatas.setText(p>=0?r.substring(0,p):r);}
        ChkETTCuffed.setSelected(v.contains("Cuffed")); ChkETTUncuffed.setSelected(v.contains("Uncuffed"));
        ChkETTUdara.setSelected(v.contains("Udara")); ChkETTAir.setSelected(v.contains("Air"));
    }
    private TextBox addTxt(JPanel p, int x, int y, int w, int h) {
        TextBox t = new TextBox(); t.setBounds(x, y, w, h); p.add(t); return t;
    }
    private CekBox addChk(JPanel p, String text, int x, int y, int w) {
        CekBox c = new CekBox(); c.setText(text); c.setBounds(x, y, w, 23);
        c.setSelected(false); c.setOpaque(false); p.add(c); return c;
    }
    private ComboBox addCmb(JPanel p, String[] items, int x, int y, int w) {
        ComboBox c = new ComboBox();
        c.setModel(new DefaultComboBoxModel(items));
        c.setBounds(x, y, w, 23); p.add(c); return c;
    }

    // ===== CRUD =====
    private String getJam() {
        return Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem();
    }

    private void jam() {
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Date now = Calendar.getInstance().getTime();
                if (ChkKejadian.isSelected()) {
                    Jam.setSelectedItem(String.format("%02d", now.getHours()));
                    Menit.setSelectedItem(String.format("%02d", now.getMinutes()));
                    Detik.setSelectedItem(String.format("%02d", now.getSeconds()));
                }
            }
        };
        new javax.swing.Timer(1000, taskPerformer).start();
    }
    private String getTglAsuhan() {
        return Valid.SetTgl(TglAsuhan.getSelectedItem() + "");
    }


    private void emptTeksKeepAtas() {
        String noRw=TNoRw.getText(), noRM=TNoRM.getText(), pas=TPasien.getText();
        String tglLahir=TTglLahir.getText(), jk=TJK.getText();
        java.util.Date tglSave=null;
        try { tglSave=new java.text.SimpleDateFormat("yyyy-MM-dd").parse(Valid.SetTgl(TglAsuhan.getSelectedItem()+"")); } catch(Exception ex){}
        Object jamSave=Jam.getSelectedItem(), menitSave=Menit.getSelectedItem(), detikSave=Detik.getSelectedItem();
        boolean kejSave=ChkKejadian.isSelected();
        String kdDok=KdDokter.getText(), nmDok=NmDokter.getText();
        String kdPen=KdPenata.getText(), nmPen=NmPenata.getText();
        String kdPing=KdPenginput.getText(), nmPing=NmPenginput.getText();
        emptTeks();
        TNoRw.setText(noRw); TNoRM.setText(noRM); TPasien.setText(pas);
        TTglLahir.setText(tglLahir); TJK.setText(jk);
        if (tglSave!=null) TglAsuhan.setDate(tglSave);
        Jam.setSelectedItem(jamSave); Menit.setSelectedItem(menitSave); Detik.setSelectedItem(detikSave);
        ChkKejadian.setSelected(kejSave);
        KdDokter.setText(kdDok); NmDokter.setText(nmDok);
        KdPenata.setText(kdPen); NmPenata.setText(nmPen);
        KdPenginput.setText(kdPing); NmPenginput.setText(nmPing);
    }

    public void emptTeks() {
        TNoRw.setText(""); TNoRM.setText(""); TPasien.setText(""); TTglLahir.setText(""); TJK.setText("");
        TglAsuhan.setDate(new Date()); Jam.setSelectedIndex(0); Menit.setSelectedIndex(0); Detik.setSelectedIndex(0);
        ChkKejadian.setSelected(true);
        KdDokter.setText(""); NmDokter.setText(""); KdPenata.setText(""); NmPenata.setText("");
        TProsedur.setText(""); TUmur.setText(""); TGolDarah.setText(""); TRhesus.setText("");
        TBB.setText(""); TTB.setText("");
        TRiwayat1.setText(""); TRiwayat2.setText(""); TRiwayat3.setText("");
        ChkTidakAdaRiwayat.setSelected(false); TObatDikonsumsi.setText("");
        ChkTidakAdaObat.setSelected(false); TAlergiObat.setText(""); ChkTidakAdaAlergi.setSelected(false);
        ChkKomplikasiSblm.setSelected(false); ChkKomplikasiKeluarga.setSelected(false);
        TTdPra.setText(""); TNadiPra.setText(""); TRespPra.setText(""); TSuhuPra.setText(""); TVasPra.setText("");
        ChkObsTidakAda.setSelected(false); ChkObsTumor.setSelected(false); ChkObsKelCongenital.setSelected(false);
        ChkObsStridor.setSelected(false); ChkObsGigiProm.setSelected(false);
        ChkThorNormal.setSelected(false); ChkThorHamil.setSelected(false); ChkThorAscites.setSelected(false);
        ChkThorObstruksi.setSelected(false); ChkThorObesitas.setSelected(false);
        ChkWajahNormal.setSelected(false); ChkWajahRahangSempit.setSelected(false); ChkWajahGigiProm.setSelected(false);
        ChkWajahRambutWajah.setSelected(false); ChkWajahGigiGoyang.setSelected(false);
        ChkWajahEdema.setSelected(false); ChkWajahKelRahang.setSelected(false);
        ChkBukaMulutYa.setSelected(false); ChkBukaMulutTidak.setSelected(false);
        ChkJarakThyroYa.setSelected(false); ChkJarakThyroTidak.setSelected(false);
        CmbMallampati.setSelectedIndex(0);
        ChkGerakRahangYa.setSelected(false); ChkGerakRahangTidak.setSelected(false);
        ChkFleksiYa.setSelected(false); ChkFleksiTidak.setSelected(false);
        ChkPenyanggaYa.setSelected(false); ChkPenyanggaTidak.setSelected(false);
        ChkJalanNapasYa.setSelected(false); ChkJalanNapasTidak.setSelected(false);
        ChkNapasAsma.setSelected(false); ChkNapasBatukProduktif.setSelected(false); ChkNapasBronkitis.setSelected(false);
        ChkNapasSesak.setSelected(false); ChkNapasPPOK.setSelected(false); ChkNapasTuberkulosis.setSelected(false);
        ChkNapasOrthopnea.setSelected(false); ChkNapasPneumonia.setSelected(false); ChkNapasNormal.setSelected(false);
        ChkKardioEKG.setSelected(false); ChkKardioHipertensi.setSelected(false); ChkKardioAngina.setSelected(false);
        ChkKardioInfark.setSelected(false); ChkKardioASHD.setSelected(false); ChkKardioMurmur.setSelected(false);
        ChkKardioCHF.setSelected(false); ChkKardioPacemaker.setSelected(false); ChkKardioDisritmia.setSelected(false);
        ChkKardioDemamRheuma.setSelected(false); ChkKardioLimitasi.setSelected(false); ChkKardioPenyakitKatup.setSelected(false); ChkKardioNormal.setSelected(false);
        ChkNeuroArthritis.setSelected(false); ChkNeuroKelemahanOtot.setSelected(false); ChkNeuroCVA.setSelected(false);
        ChkNeuroParalisis.setSelected(false); ChkNeuroDegeneratif.setSelected(false); ChkNeuroParesthesia.setSelected(false);
        ChkNeuroNyeriKepala.setSelected(false); ChkNeuroPingsan.setSelected(false); ChkNeuroPenurunanKesadaran.setSelected(false);
        ChkNeuroKejang.setSelected(false); ChkNeuroGangguanTulang.setSelected(false); ChkNeuroNormal.setSelected(false);
        ChkRenalDiabetes.setSelected(false); ChkRenalGagalGinjal.setSelected(false); ChkRenalTiroid.setSelected(false);
        ChkRenalRetensiUrin.setSelected(false); ChkRenalInfeksiSaluran.setSelected(false); ChkRenalPenurunanBB.setSelected(false); ChkRenalNormal.setSelected(false);
        ChkHepatoObstruksiUsus.setSelected(false); ChkHepatoSirosis.setSelected(false); ChkHepatoHepatitis.setSelected(false);
        ChkHepatoHiatal.setSelected(false); ChkHepatoMualMuntah.setSelected(false); ChkHepatoUlkus.setSelected(false); ChkHepatoNormal.setSelected(false);
        ChkLainAnemia.setSelected(false); ChkLainKehamilan.setSelected(false); ChkLainDehidrasi.setSelected(false);
        ChkLainKemoterapi.setSelected(false); ChkLainHemofilia.setSelected(false); ChkLainSickleCell.setSelected(false);
        ChkLainImunosupresi.setSelected(false); ChkLainRiwayatSteroid.setSelected(false); ChkLainKanker.setSelected(false);
        ChkLainRiwayatTransfusi.setSelected(false); ChkLainGangguanPerdarahan.setSelected(false); ChkLainNormal.setSelected(false);
        ChkMerokok.setSelected(false); ChkMerokokTidak.setSelected(false);
        TMerokokPak.setText(""); TMerokokTahun.setText("");
        ChkAlkohol.setSelected(false); ChkAlkoholTidak.setSelected(false); TAlkohol.setText("");
        ChkPenunjangEKG.setSelected(false); ChkPenunjangEvaluasiParu.setSelected(false); ChkPenunjangXRay.setSelected(false);
        TPenunjangLain.setText("");
        ChkLabHbHt.setSelected(false); ChkLabUrinalisis.setSelected(false); ChkLabElektrolit.setSelected(false);
        TLabLain.setText("");
        TMasalah1.setText(""); TMasalah2.setText(""); TMasalah3.setText("");
        CmbASA.setSelectedIndex(0); TPraMedikasi.setText("");
        ChkTeknikMAC.setSelected(false); ChkTeknikSedasi.setSelected(false); ChkTeknikAnestesiUmum.setSelected(false);
        ChkTeknikRegional.setSelected(false); ChkTeknikKombinasi.setSelected(false);
        ChkTeknikSpinal.setSelected(false); ChkTeknikEpidural.setSelected(false); ChkTeknikPeriperal.setSelected(false);
        TTeknikLain.setText(""); TTeknikAlternatif.setText("");
        ChkMonitorALine.setSelected(false); ChkMonitorCVC.setSelected(false); ChkMonitorPAC.setSelected(false); ChkMonitorTEE.setSelected(false);
        TMonitorLain.setText("");
        ChkKontrolIntravena.setSelected(false); ChkKontrolEpidural.setSelected(false); ChkKontrolSpinal.setSelected(false); ChkKontrolPCA.setSelected(false);
        TKontrolLain.setText(""); TRisiko.setText("");
        KdDokterOp.setText(""); TDokterOperator.setText(""); TProsedurOperasi.setText(""); TRuangOT.setText("");
        TPuasaJam.setText("");
        ChkPasienDiidentifikasi.setSelected(false); ChkRekamMedis.setSelected(false);
        ChkInformedConsent.setSelected(false); ChkPuasa.setSelected(false);
        ChkMallampatiI.setSelected(false); ChkMallampatiII.setSelected(false);
        ChkMallampatiIII.setSelected(false); ChkMallampatiIV.setSelected(false);
        ChkKondisiTenang.setSelected(false); ChkKondisiSadar.setSelected(false);
        ChkKondisiCemas.setSelected(false); ChkKondisiDisorientasi.setSelected(false);
        ChkKondisiTdkKooperatif.setSelected(false); ChkKondisiTdkRespon.setSelected(false);
        TTdPraInd.setText(""); TNadiPraInd.setText(""); TRespPraInd.setText(""); TSpo2.setText(""); TAlergiPra.setText("");
        ChkPengecekanMesin.setSelected(false); ChkTaliPengaman.setSelected(false); ChkPenyanggaLengan.setSelected(false);
        ChkPressurePoint.setSelected(false); ChkPenyanggaKetiak.setSelected(false); ChkTanganTerlindungi.setSelected(false);
        ChkMatOintment.setSelected(false); ChkMatSaline.setSelected(false); ChkMatTapped.setSelected(false);
        ChkMatPads.setSelected(false); ChkMatGoggles.setSelected(false);
        ChkMonStetoskop.setSelected(false); ChkMonPrekordial.setSelected(false);
        ChkMonNIBP.setSelected(false); ChkMonNIBPKiri.setSelected(false); ChkMonNIBPKanan.setSelected(false);
        ChkMonEKG.setSelected(false); TEKGLead.setText(""); ChkMonEsoph.setSelected(false);
        ChkMonPulseOxy.setSelected(false); ChkMonSensorO2.setSelected(false);
        ChkMonSelimut.setSelected(false); ChkMonGasAnalyzer.setSelected(false);
        ChkMonEEG.setSelected(false); ChkMonNerveStimulator.setSelected(false);
        ChkMonKateter.setSelected(false); ChkMonDoppler.setSelected(false);
        ChkMonNGTOGT.setSelected(false); ChkMonBIS.setSelected(false); ChkMonIntravena.setSelected(false);
        ChkMonArteriLine.setSelected(false); ChkMonCVC.setSelected(false); ChkMonPAC.setSelected(false);
        ChkTUPreOxy.setSelected(false); ChkTUIntravena.setSelected(false); ChkTUInhalasi.setSelected(false); ChkTUIntramuskular.setSelected(false);
        ChkTURSI.setSelected(false); ChkTUCricoid.setSelected(false);
        ChkSedasiRingan.setSelected(false); ChkSedasiSedang.setSelected(false); ChkSedasiDalam.setSelected(false);
        TTeknikRegional.setText(""); TTeknikRegional2.setText("");
        ChkJNPernapasanSpontan.setSelected(false); ChkJNNasalO2.setSelected(false); ChkJNMask.setSelected(false);
        ChkJNSupraglotic.setSelected(false); TJNJenis.setText(""); TJNUkuran.setText("");
        ChkJNTrakeostomi.setSelected(false); ChkJNIntubasi.setSelected(false); ChkJNNasal.setSelected(false); ChkJNOral.setSelected(false);
        ChkJNSleep.setSelected(false); ChkJNApnea.setSelected(false); ChkJNDirect.setSelected(false);
        ChkJNAwake.setSelected(false); ChkJNNonApnea.setSelected(false); ChkJNBlind.setSelected(false);
        ChkETTRegular.setSelected(false); ChkETTReformed.setSelected(false);
        ChkETTPreFormedBend.setSelected(false); ChkETTDoubleLumenL.setSelected(false); ChkETTDoubleLumenR.setSelected(false);
        TETTSize.setText(""); TETTBatas.setText("");
        ChkETTCuffed.setSelected(false); ChkETTUncuffed.setSelected(false); ChkETTUdara.setSelected(false); ChkETTAir.setSelected(false);
        ChkJNStyle.setSelected(false); ChkJNMagill.setSelected(false); ChkJNLaryngoscope.setSelected(false);
        TLarynUkuran.setText(""); ChkJNVideoLaryngoscope.setSelected(false); TVLUkuran.setText("");
        ChkJNFiberoptic.setSelected(false); TFiberPercobaan.setText("");
        ChkSuaraSama.setSelected(false); ChkSuaraTidakSama.setSelected(false);
        ChkKoloid.setSelected(false); ChkKristaloid.setSelected(false);
        ChkWB.setSelected(false); ChkPRC.setSelected(false); ChkFFP.setSelected(false); ChkCryo.setSelected(false); ChkCairanLainChk.setSelected(false);
        TCairanKoloid.setText(""); TCairanKristaloid.setText("");
        TTransfusiWB.setText(""); TTransfusiPRC.setText(""); TTransfusiFFP.setText("");
        TTransfusiCryo.setText(""); TCairanLain.setText("");
        TOutputDarah.setText(""); TOutputUrin.setText(""); TOutputNGT.setText("");
        TglPasca.setDate(new Date()); SpJamPasca.setValue(0); SpMenitPasca.setValue(0);
        TNamaPengkaji.setText(""); TDPasca.setText(""); TNPasca.setText(""); TRespPasca.setText(""); TSPasca.setText("");
        CmbAidreteWaktu.setSelectedIndex(0); CmbAidreteSirkulasi.setSelectedIndex(0);
        CmbAidreteKesadaran.setSelectedIndex(0); CmbAidreteSatO2Dws.setSelectedIndex(0);
        CmbAidreteSatO2Pedi.setSelectedIndex(0); CmbAidretePernapasan.setSelectedIndex(0);
        CmbAidreteAktifitas.setSelectedIndex(0); LblAidreteTotal.setText("0");
        CmbNyeriWaktu.setSelectedIndex(0); TVasNyeri.setText("");
        CmbBromageWaktu.setSelectedIndex(0); CmbBromageLevel.setSelectedIndex(0);
        TInstruksiRR.setText("");
        ChkDischBangsal.setSelected(false); ChkDischICU.setSelected(false); ChkDischHCU.setSelected(false);
        SpJamDischarge.setValue(0); SpMenitDischarge.setValue(0);
        TTdDischarge.setText(""); TNDischarge.setText(""); TRespDischarge.setText("");
        TSDischarge.setText(""); TVasDischarge.setText(""); TInstruksiAnestesi.setText("");
        // Durante
        clearDurGrid(obatGridModel); clearDurGrid(cairanGridModel);
        TOksigen.setText(""); TN2O.setText(""); TAirLmenit.setText(""); TSaturasiO2.setText("");
        TFiO2.setText(""); TEndTidalCO2.setText(""); TEKGDur.setText(""); TCvpPcwp.setText("");
        TSuhuDur.setText(""); TUrinDur.setText(""); TLainLainDur.setText("");
        ChkJacksonRees.setSelected(false);
        TFrekPernapasan.setText(""); TVolumeTidal.setText(""); TPeakPressure.setText(""); TPEEP.setText("");
        TKomplikasiDur.setText("");
        // Catatan
        TglOperasi.setDate(new Date()); SpJamOp.setValue(0); SpMenitOp.setValue(0); SpDetikOp.setValue(0);
        TglAnestesi.setDate(new Date()); SpJamAn.setValue(0); SpMenitAn.setValue(0); SpDetikAn.setValue(0);
        TglAntibiotik.setDate(new Date()); SpJamAnti.setValue(0); SpMenitAnti.setValue(0); SpDetikAnti.setValue(0);
        ChkEkstubasi.setSelected(false); ChkIntubasi.setSelected(false); ChkOralNasal.setSelected(false);
        ChkSadar.setSelected(false); ChkMengantuk.setSelected(false);
        ChkStabil.setSelected(false); ChkTidakStabil.setSelected(false);
    }

    // ===== DURANTE GRID HELPERS =====
    private String getNamaColForModel(DefaultTableModel model) {
        return (model == obatGridModel) ? "Nama Obat" : "Nama Cairan";
    }

    private String[] buildDurColNames(int startH, int startM, int intervalMin, String namaCol) {
        String[] names = new String[4 + N_SLOTS + 1];
        names[0] = "No."; names[1] = namaCol; names[2] = "Dosis"; names[3] = "Satuan";
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, startH);
        cal.set(Calendar.MINUTE, startM);
        for (int i = 0; i < N_SLOTS; i++) {
            cal.add(Calendar.MINUTE, intervalMin);
            names[4 + i] = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
        }
        names[4 + N_SLOTS] = "Total";
        return names;
    }

    private DefaultTableModel buildDurGridModel(int h, int m, int intv, String namaCol) {
        String[] cols = buildDurColNames(h, m, intv, namaCol);
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c > 0; }
            @Override public Class<?> getColumnClass(int c) { return String.class; }
        };
        // 6 empty rows by default
        for (int i = 0; i < 6; i++) {
            Object[] row = new Object[cols.length]; row[0] = String.valueOf(i + 1); model.addRow(row);
        }
        addTotalListener(model);
        return model;
    }

    private JTable buildDurGridTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setFont(new java.awt.Font("Tahoma", 0, 10));
        t.setRowHeight(22);
        t.setGridColor(new Color(180, 180, 180));
        t.setShowGrid(true);
        t.getTableHeader().setFont(new java.awt.Font("Tahoma", 1, 10));
        t.getTableHeader().setBackground(new Color(52, 73, 94));
        t.getTableHeader().setForeground(Color.WHITE);
        t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        t.getColumnModel().getColumn(0).setPreferredWidth(36);
        t.getColumnModel().getColumn(1).setPreferredWidth(160);
        t.getColumnModel().getColumn(2).setPreferredWidth(65);
        t.getColumnModel().getColumn(3).setPreferredWidth(55);
        for (int i = 4; i < 4 + N_SLOTS; i++) t.getColumnModel().getColumn(i).setPreferredWidth(44);
        t.getColumnModel().getColumn(4 + N_SLOTS).setPreferredWidth(55);
        return t;
    }

    private void addTotalListener(DefaultTableModel model) {
        final boolean[] updating = {false};
        model.addTableModelListener(e -> {
            if (updating[0]) return;
            int col = e.getColumn();
            if (col >= 4 && col < 4 + N_SLOTS) {
                updating[0] = true;
                int row = e.getFirstRow();
                double total = 0;
                for (int c = 4; c < 4 + N_SLOTS; c++) {
                    Object v = model.getValueAt(row, c);
                    if (v != null && !v.toString().trim().isEmpty()) {
                        try { total += Double.parseDouble(v.toString().trim()); } catch (Exception ex) {}
                    }
                }
                model.setValueAt(total > 0 ? (total == (long)total ? String.valueOf((long)total) : String.valueOf(total)) : "", row, 4 + N_SLOTS);
                updating[0] = false;
            }
        });
    }

    private JPanel buildDurGridPanel(DefaultTableModel model, JTable table, JSpinner hourSp, JSpinner minSp, int[] intervalHolder) {
        JPanel panel = new JPanel(new BorderLayout(0, 2));
        panel.setOpaque(false);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        toolbar.setOpaque(false);

        Button btnAdd = new Button(); btnAdd.setText("+ Baris"); btnAdd.setPreferredSize(new Dimension(80, 23));
        Button btnDel = new Button(); btnDel.setText("- Baris"); btnDel.setPreferredSize(new Dimension(80, 23));
        Label lbJam = new Label(); lbJam.setText("Jam Mulai:");
        Label lbColon = new Label(); lbColon.setText(":");  lbColon.setPreferredSize(new Dimension(10, 23));
        Label lbItvl = new Label(); lbItvl.setText("Interval(mnt):");
        ComboBox cmbIntv = new ComboBox();
        cmbIntv.setModel(new DefaultComboBoxModel(new String[]{"5","10","15","30"}));
        cmbIntv.setSelectedItem(String.valueOf(intervalHolder[0]));
        cmbIntv.setPreferredSize(new Dimension(55, 23));
        Button btnUpd = new Button(); btnUpd.setText("Update"); btnUpd.setPreferredSize(new Dimension(65, 23));

        toolbar.add(btnAdd); toolbar.add(btnDel);
        toolbar.add(lbJam); toolbar.add(hourSp); toolbar.add(lbColon); toolbar.add(minSp);
        toolbar.add(lbItvl); toolbar.add(cmbIntv); toolbar.add(btnUpd);

        btnAdd.addActionListener(e -> {
            Object[] row = new Object[model.getColumnCount()];
            row[0] = String.valueOf(model.getRowCount() + 1);
            model.addRow(row);
        });
        btnDel.addActionListener(e -> {
            if (table.isEditing()) table.getCellEditor().cancelCellEditing();
            int sel = table.getSelectedRow();
            if (sel >= 0) {
                model.removeRow(sel);
                for (int i = 0; i < model.getRowCount(); i++) model.setValueAt(String.valueOf(i + 1), i, 0);
            }
        });
        btnUpd.addActionListener(e -> {
            int h = (int) hourSp.getValue(); int m = (int) minSp.getValue();
            try { intervalHolder[0] = Integer.parseInt(cmbIntv.getSelectedItem().toString()); } catch (Exception ex) {}
            model.setColumnIdentifiers(buildDurColNames(h, m, intervalHolder[0], getNamaColForModel(model)));
        });

        JScrollPane sp = new JScrollPane(table,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);
        return panel;
    }

    private void clearDurGrid(DefaultTableModel model) {
        for (int r = 0; r < model.getRowCount(); r++)
            for (int c = 1; c < model.getColumnCount(); c++)
                model.setValueAt("", r, c);
    }

    private String serializeDurGrid(DefaultTableModel model, int jh, int jm, int intv) {
        StringBuilder sb = new StringBuilder();
        sb.append("__META__|").append(jh).append("|").append(jm).append("|").append(intv).append("\n");
        for (int r = 0; r < model.getRowCount(); r++) {
            boolean hasData = false;
            for (int c = 1; c < model.getColumnCount(); c++) {
                Object v = model.getValueAt(r, c);
                if (v != null && !v.toString().trim().isEmpty()) { hasData = true; break; }
            }
            if (!hasData) continue;
            for (int c = 1; c < model.getColumnCount(); c++) {
                if (c > 1) sb.append("\t");
                Object v = model.getValueAt(r, c);
                sb.append(v != null ? v.toString().trim() : "");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private void deserializeDurGrid(String data, DefaultTableModel model, JSpinner hourSp, JSpinner minSp, int[] intervalHolder) {
        clearDurGrid(model);
        // Remove extra rows beyond default 6
        while (model.getRowCount() > 6) model.removeRow(model.getRowCount() - 1);
        if (data == null || data.trim().isEmpty()) return;
        String[] lines = data.split("\n", -1);
        int rowNum = 0;
        for (String line : lines) {
            if (line.startsWith("__META__|")) {
                String[] meta = line.split("\\|", -1);
                try {
                    int h = Integer.parseInt(meta[1]); int m = Integer.parseInt(meta[2]);
                    int intv = Integer.parseInt(meta[3]);
                    hourSp.setValue(h); minSp.setValue(m);
                    intervalHolder[0] = intv;
                    model.setColumnIdentifiers(buildDurColNames(h, m, intv, getNamaColForModel(model)));
                } catch (Exception ex) {}
                continue;
            }
            if (line.trim().isEmpty()) continue;
            String[] cells = line.split("\t", -1);
            if (rowNum >= model.getRowCount()) {
                Object[] newRow = new Object[model.getColumnCount()];
                newRow[0] = String.valueOf(rowNum + 1);
                model.addRow(newRow);
            }
            model.setValueAt(String.valueOf(rowNum + 1), rowNum, 0);
            for (int c = 0; c < cells.length && (c + 1) < model.getColumnCount(); c++) {
                model.setValueAt(cells[c], rowNum, c + 1);
            }
            rowNum++;
        }
    }

    // ===== TAB 8: CATATAN =====
    private void buildTabCatatan() {
        PanelBiasa formCat = new PanelBiasa();
        formCat.setLayout(null);
        java.awt.Font fBold = new java.awt.Font("Tahoma", 1, 11);
        java.awt.Font fItal = new java.awt.Font("Tahoma", 2, 11);
        int x1 = 10, xLbl = 10, xDT = 220, y = 12;

        JLabel hdr = new JLabel("CATATAN");
        hdr.setFont(new java.awt.Font("Tahoma", 1, 13));
        hdr.setBounds(x1, y, 300, 22); formCat.add(hdr);
        y += 35;

        // ---- OPERASI ----
        JLabel lbOp = new JLabel("• Operasi");
        lbOp.setFont(fBold); lbOp.setBounds(x1, y, 200, 20); formCat.add(lbOp);
        y += 26;
        addLbl(formCat, "Tanggal dan Jam Mulai :", xLbl+15, y);
        TglOperasi = new widget.Tanggal(); TglOperasi.setDisplayFormat("dd-MM-yyyy");
        TglOperasi.setPreferredSize(new java.awt.Dimension(105, 23));
        TglOperasi.setBounds(xDT, y, 105, 23); formCat.add(TglOperasi);
        SpJamOp   = mkTimeSpinner(0, 23); SpMenitOp = mkTimeSpinner(0, 59); SpDetikOp = mkTimeSpinner(0, 59);
        addTimeSpinners(formCat, xDT+112, y, SpJamOp, SpMenitOp, SpDetikOp, fBold);
        y += 35;

        // ---- ANESTESI ----
        JLabel lbAn = new JLabel("• Anestesi");
        lbAn.setFont(fBold); lbAn.setBounds(x1, y, 200, 20); formCat.add(lbAn);
        y += 26;
        addLbl(formCat, "Tanggal dan Jam Mulai :", xLbl+15, y);
        TglAnestesi = new widget.Tanggal(); TglAnestesi.setDisplayFormat("dd-MM-yyyy");
        TglAnestesi.setPreferredSize(new java.awt.Dimension(105, 23));
        TglAnestesi.setBounds(xDT, y, 105, 23); formCat.add(TglAnestesi);
        SpJamAn   = mkTimeSpinner(0, 23); SpMenitAn = mkTimeSpinner(0, 59); SpDetikAn = mkTimeSpinner(0, 59);
        addTimeSpinners(formCat, xDT+112, y, SpJamAn, SpMenitAn, SpDetikAn, fBold);
        y += 35;

        // ---- ANTIBIOTIKA PROFILAKSIS ----
        JLabel lbAnti = new JLabel("• Antibiotika Profilaksis");
        lbAnti.setFont(fBold); lbAnti.setBounds(x1, y, 300, 20); formCat.add(lbAnti);
        y += 26;
        addLbl(formCat, "Tanggal dan Jam :", xLbl+15, y);
        TglAntibiotik = new widget.Tanggal(); TglAntibiotik.setDisplayFormat("dd-MM-yyyy");
        TglAntibiotik.setPreferredSize(new java.awt.Dimension(105, 23));
        TglAntibiotik.setBounds(xDT, y, 105, 23); formCat.add(TglAntibiotik);
        SpJamAnti   = mkTimeSpinner(0, 23); SpMenitAnti = mkTimeSpinner(0, 59); SpDetikAnti = mkTimeSpinner(0, 59);
        addTimeSpinners(formCat, xDT+112, y, SpJamAnti, SpMenitAnti, SpDetikAnti, fBold);
        y += 40;

        // ---- PASCA OPERASI ----
        JLabel lbPasca = new JLabel("• Pasca Operasi :");
        lbPasca.setFont(fBold); lbPasca.setBounds(x1, y, 300, 20); formCat.add(lbPasca);
        y += 28;
        ChkEkstubasi  = addChk(formCat, "Ekstubasi",           x1+20, y, 180); y += 26;
        ChkIntubasi   = addChk(formCat, "Intubasi",             x1+20, y, 180); y += 26;
        JLabel lbON = new JLabel("Oral/Nasal Airway"); lbON.setFont(fItal);
        lbON.setBounds(x1+38, y, 160, 22); formCat.add(lbON);
        ChkOralNasal = new CekBox(); ChkOralNasal.setBounds(x1+20, y, 18, 22); formCat.add(ChkOralNasal);
        y += 26;
        ChkSadar       = addChk(formCat, "Sadar",               x1+20, y, 180); y += 26;
        ChkMengantuk   = addChk(formCat, "Mengantuk",            x1+20, y, 180); y += 26;
        ChkStabil      = addChk(formCat, "Stabil",               x1+20, y, 180); y += 26;
        ChkTidakStabil = addChk(formCat, "Tidak Stabil",         x1+20, y, 180); y += 30;

        formCat.setPreferredSize(new java.awt.Dimension(990, y + 20));
        ScrollPane spCat = new ScrollPane(); spCat.setViewportView(formCat);
        tabModeCatatan = buildTM("No.Rawat","Pasien","Tanggal","Jam","Tgl Operasi","Tgl Anestesi","Tgl Antibiotik","Penginput");
        tbCatatan = buildTB(tabModeCatatan, () -> getDataCatatan());
        Tabs.addTab("Catatan", mkTabPanel(spCat, tbCatatan));
    }

    /** Helper: create a JSpinner for time (jam/menit/detik) */
    private JSpinner mkTimeSpinner(int min, int max) {
        JSpinner sp = new JSpinner(new SpinnerNumberModel(0, min, max, 1));
        sp.setPreferredSize(new java.awt.Dimension(50, 23));
        JSpinner.NumberEditor ed = new JSpinner.NumberEditor(sp, "00");
        sp.setEditor(ed);
        return sp;
    }

    /** Helper: place HH:mm:ss spinners on formPanel at x,y */
    private void addTimeSpinners(JPanel p, int x, int y, JSpinner spH, JSpinner spM, JSpinner spS, java.awt.Font fBold) {
        spH.setBounds(x,      y, 50, 23); p.add(spH);
        JLabel c1 = new JLabel(":"); c1.setFont(fBold); c1.setBounds(x+52, y, 8, 23); p.add(c1);
        spM.setBounds(x+62,   y, 50, 23); p.add(spM);
        JLabel c2 = new JLabel(":"); c2.setFont(fBold); c2.setBounds(x+114, y, 8, 23); p.add(c2);
        spS.setBounds(x+124,  y, 50, 23); p.add(spS);
    }

    private void simpanCatatan() {
        if (!validasiUmum()) return;
        String sql = "INSERT INTO rm_sedasi_catatan " +
            "(no_rawat,tgl_asuhan,jam,kd_penginput,nm_penginput," +
            "operasi_tanggal,operasi_jam,operasi_menit,operasi_detik," +
            "anestesi_tanggal,anestesi_jam,anestesi_menit,anestesi_detik," +
            "antibiotik_tanggal,antibiotik_jam,antibiotik_menit,antibiotik_detik," +
            "pasca_ekstubasi,pasca_intubasi,pasca_oral_nasal," +
            "pasca_sadar,pasca_mengantuk,pasca_stabil,pasca_tidak_stabil,user_input) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            ps = koneksi.prepareStatement(sql);
            int i = 1;
            ps.setString(i++, TNoRw.getText().trim());
            ps.setString(i++, Valid.SetTgl(TglAsuhan.getSelectedItem()+""));
            ps.setString(i++, Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem());
            ps.setString(i++, KdPenginput.getText().trim());
            ps.setString(i++, NmPenginput.getText().trim());
            ps.setString(i++, TglOperasi.getSelectedItem() != null ? Valid.SetTgl(TglOperasi.getSelectedItem()+"") : null);
            ps.setInt(i++, (int) SpJamOp.getValue()); ps.setInt(i++, (int) SpMenitOp.getValue()); ps.setInt(i++, (int) SpDetikOp.getValue());
            ps.setString(i++, TglAnestesi.getSelectedItem() != null ? Valid.SetTgl(TglAnestesi.getSelectedItem()+"") : null);
            ps.setInt(i++, (int) SpJamAn.getValue()); ps.setInt(i++, (int) SpMenitAn.getValue()); ps.setInt(i++, (int) SpDetikAn.getValue());
            ps.setString(i++, TglAntibiotik.getSelectedItem() != null ? Valid.SetTgl(TglAntibiotik.getSelectedItem()+"") : null);
            ps.setInt(i++, (int) SpJamAnti.getValue()); ps.setInt(i++, (int) SpMenitAnti.getValue()); ps.setInt(i++, (int) SpDetikAnti.getValue());
            ps.setString(i++, ChkEkstubasi.isSelected() ? "1" : "0");
            ps.setString(i++, ChkIntubasi.isSelected() ? "1" : "0");
            ps.setString(i++, ChkOralNasal.isSelected() ? "1" : "0");
            ps.setString(i++, ChkSadar.isSelected() ? "1" : "0");
            ps.setString(i++, ChkMengantuk.isSelected() ? "1" : "0");
            ps.setString(i++, ChkStabil.isSelected() ? "1" : "0");
            ps.setString(i++, ChkTidakStabil.isSelected() ? "1" : "0");
            ps.setString(i++, KdPenginput.getText().trim());
            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "Data catatan berhasil disimpan.");
                tampilCatatan(); emptTeksKeepAtas();
            }
        } catch (Exception e) { System.out.println("simpanCatatan: " + e);
        } finally { try { if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    private void getDataCatatan() {
        if (tabModeCatatan.getRowCount() == 0 || tbCatatan.getSelectedRow() == -1) return;
        int row = tbCatatan.getSelectedRow();
        String noRw = tabModeCatatan.getValueAt(row, 0).toString();
        String tgl  = tabModeCatatan.getValueAt(row, 2).toString();
        String jam  = tabModeCatatan.getValueAt(row, 3).toString();
        try {
            ps = koneksi.prepareStatement("SELECT * FROM rm_sedasi_catatan WHERE no_rawat=? AND tgl_asuhan=? AND jam=?");
            ps.setString(1, noRw); ps.setString(2, tgl); ps.setString(3, jam);
            rs = ps.executeQuery();
            if (rs.next()) {
                try { if (rs.getDate("operasi_tanggal") != null) TglOperasi.setDate(rs.getDate("operasi_tanggal")); } catch (Exception ex) {}
                SpJamOp.setValue(rs.getInt("operasi_jam")); SpMenitOp.setValue(rs.getInt("operasi_menit")); SpDetikOp.setValue(rs.getInt("operasi_detik"));
                try { if (rs.getDate("anestesi_tanggal") != null) TglAnestesi.setDate(rs.getDate("anestesi_tanggal")); } catch (Exception ex) {}
                SpJamAn.setValue(rs.getInt("anestesi_jam")); SpMenitAn.setValue(rs.getInt("anestesi_menit")); SpDetikAn.setValue(rs.getInt("anestesi_detik"));
                try { if (rs.getDate("antibiotik_tanggal") != null) TglAntibiotik.setDate(rs.getDate("antibiotik_tanggal")); } catch (Exception ex) {}
                SpJamAnti.setValue(rs.getInt("antibiotik_jam")); SpMenitAnti.setValue(rs.getInt("antibiotik_menit")); SpDetikAnti.setValue(rs.getInt("antibiotik_detik"));
                ChkEkstubasi.setSelected("1".equals(rs.getString("pasca_ekstubasi")));
                ChkIntubasi.setSelected("1".equals(rs.getString("pasca_intubasi")));
                ChkOralNasal.setSelected("1".equals(rs.getString("pasca_oral_nasal")));
                ChkSadar.setSelected("1".equals(rs.getString("pasca_sadar")));
                ChkMengantuk.setSelected("1".equals(rs.getString("pasca_mengantuk")));
                ChkStabil.setSelected("1".equals(rs.getString("pasca_stabil")));
                ChkTidakStabil.setSelected("1".equals(rs.getString("pasca_tidak_stabil")));
            }
        } catch (Exception e) { System.out.println("getDataCatatan: " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    private void isForm() {
        if (ChkInput.isSelected()) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(1024, 435));
            PanelInput.setVisible(true); ChkInput.setVisible(true);
        } else {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(1024, 20));
            PanelInput.setVisible(false); ChkInput.setVisible(true);
        }
    }

    public void setNoRm(String noRawat, String noRM, String nama) {
        TNoRw.setText(noRawat); TNoRM.setText(noRM); TPasien.setText(nama);
        TglAsuhan.setDate(new Date());
        String tglLahir = Sequel.cariIsi("SELECT tgl_lahir FROM pasien WHERE no_rkm_medis=?", noRM);
        TTglLahir.setText(tglLahir != null ? tglLahir : "");
        String jk = Sequel.cariIsi("SELECT jk FROM pasien WHERE no_rkm_medis=?", noRM);
        TJK.setText(jk != null ? jk : "");
        String umur = Sequel.cariIsi("SELECT CONCAT(umurdaftar,' ',sttsumur) FROM reg_periksa WHERE no_rawat=?", noRawat);
        TUmur.setText(umur != null ? umur : "");
        String golDarah = Sequel.cariIsi("SELECT gol_darah FROM pasien WHERE no_rkm_medis=?", noRM);
        TGolDarah.setText(golDarah != null ? golDarah : "");
        tampil();
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getrujukan_keluar());
        BtnHapus.setEnabled(akses.getrujukan_keluar());
        BtnEdit.setEnabled(akses.getrujukan_keluar());
    }

    // ==================== CETAK HTML ====================

    private String imgToBase64(BufferedImage img) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) { return ""; }
    }

    /** Trigger PHP QR generator and return the URL to the generated PNG. */
    private String triggerAndGetQrUrl(String baseUrl, String phpPath, String paramName, String kode) {
        if (kode == null || kode.trim().isEmpty()) return "";
        try {
            String genUrl = baseUrl + phpPath + "?" + paramName + "=" + java.net.URLEncoder.encode(kode, "UTF-8");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(genUrl).openConnection();
            conn.setConnectTimeout(3000); conn.setReadTimeout(5000);
            conn.getResponseCode(); conn.disconnect();
        } catch (Exception e) { System.out.println("QR gen: "+e); }
        return "";  // return empty — URL used directly in HTML
    }

    private String s(java.sql.ResultSet r, String col) {
        try { String v = r.getString(col); return v != null ? v : ""; }
        catch (Exception e) { return ""; }
    }

    private String row2(String label, String val) {
        return "<tr><td class='lbl'>"+label+"</td><td class='val'>"+val+"</td></tr>";
    }
    private String row4(String l1, String v1, String l2, String v2) {
        return "<tr><td class='lbl'>"+l1+"</td><td class='val'>"+v1+"</td>"
              +"<td class='lbl'>"+l2+"</td><td class='val'>"+v2+"</td></tr>";
    }
    private String sectionHead(String title) {
        return "<tr><td colspan='4' class='sec'>"+title+"</td></tr>";
    }

    private void cetakHTML() {
        String noRawat = TNoRw.getText().trim();
        if (noRawat.isEmpty()) { JOptionPane.showMessageDialog(null,"Pilih/isi No. Rawat dulu."); return; }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            java.sql.Connection c = koneksiDB.condb();
            java.sql.PreparedStatement pq; java.sql.ResultSet rq;
            StringBuilder sb = new StringBuilder();

            // ── Helpers: read latest row per table ──
            String qLatest = " WHERE no_rawat=? ORDER BY tgl_asuhan DESC, jam DESC LIMIT 1";

            // Pasien info from reg_periksa + pasien
            String nmPasien = TPasien.getText().trim();
            String noRM = TNoRM.getText().trim();
            String tglLahir = TTglLahir.getText().trim();
            String jk = TJK.getText().trim();
            String nmDokter  = NmDokter.getText().trim();
            String kdDokter  = KdDokter.getText().trim();
            String nmPenata  = NmPenata.getText().trim();
            String kdPenata  = KdPenata.getText().trim();
            String nmPetugas = NmPenginput.getText().trim();
            String kdPetugas = KdPenginput.getText().trim();

            // ── SECTION: Pra Sedasi/Anestesi ──
            pq = c.prepareStatement("SELECT * FROM rm_sedasi_anamnesa"+qLatest);
            pq.setString(1, noRawat); rq = pq.executeQuery();
            sb.append(sectionHead("1. Pra Sedasi / Anestesi (Anamnesa)"));
            if (rq.next()) {
                if (!nmDokter.isEmpty()||kdDokter.isEmpty()) { nmDokter=s(rq,"nm_dokter_anestesi"); kdDokter=s(rq,"kd_dokter_anestesi"); }
                if (!nmPenata.isEmpty()||kdPenata.isEmpty()) { nmPenata=s(rq,"nm_penata"); kdPenata=s(rq,"kd_penata"); }
                if (!nmPetugas.isEmpty()||kdPetugas.isEmpty()) { nmPetugas=s(rq,"nm_penginput"); kdPetugas=s(rq,"kd_penginput"); }
                sb.append(row4("Tanggal",s(rq,"tgl_asuhan"),"Jam",s(rq,"jam")));
                sb.append(row4("Prosedur",s(rq,"prosedur"),"ASA Grade",s(rq,"asa_grade")));
                sb.append(row4("Umur",s(rq,"umur"),"Gol. Darah",s(rq,"gol_darah")+" / "+s(rq,"rhesus")));
                sb.append(row4("BB (kg)",s(rq,"bb"),"TB (cm)",s(rq,"tb")));
                sb.append(row4("TD Pra",s(rq,"td_pra"),"Nadi Pra",s(rq,"nadi_pra")));
                sb.append(row4("Resp Pra",s(rq,"resp_pra"),"Suhu Pra",s(rq,"suhu_pra")));
                sb.append(row4("VAS Pra",s(rq,"vas_pra"),"Mallampati",s(rq,"mallampati")));
                sb.append(row4("Riwayat 1",s(rq,"riwayat1"),"Riwayat 2",s(rq,"riwayat2")));
                sb.append(row2("Riwayat 3",s(rq,"riwayat3")));
                sb.append(row4("Obat Dikonsumsi",s(rq,"obat_dikonsumsi"),"Alergi Obat",s(rq,"alergi_obat")));
                sb.append(row4("Masalah 1",s(rq,"masalah1"),"Masalah 2",s(rq,"masalah2")));
                sb.append(row2("Masalah 3",s(rq,"masalah3")));
                sb.append(row2("Pra Medikasi",s(rq,"pra_medikasi")));
            } else sb.append("<tr><td colspan='4' class='val'><i>Belum ada data</i></td></tr>");
            rq.close(); pq.close();

            // ── SECTION: Plan of Care ──
            pq = c.prepareStatement("SELECT * FROM rm_sedasi_poc"+qLatest);
            pq.setString(1, noRawat); rq = pq.executeQuery();
            sb.append(sectionHead("2. Plan of Care (POC)"));
            if (rq.next()) {
                sb.append(row4("Tanggal",s(rq,"tgl_asuhan"),"Jam",s(rq,"jam")));
                sb.append(row2("Teknik Anestesi",s(rq,"teknik_anestesi")));
                sb.append(row4("Teknik Lain",s(rq,"teknik_lain"),"Alternatif",s(rq,"teknik_alternatif")));
                sb.append(row2("Monitor Invasif",s(rq,"monitor_invasif")));
                sb.append(row4("Monitor Lain",s(rq,"monitor_lain"),"Kontrol Nyeri",s(rq,"kontrol_nyeri")));
                sb.append(row4("Kontrol Lain",s(rq,"kontrol_lain"),"Risiko",s(rq,"risiko")));
            } else sb.append("<tr><td colspan='4' class='val'><i>Belum ada data</i></td></tr>");
            rq.close(); pq.close();

            // ── SECTION: Pra Induksi ──
            pq = c.prepareStatement("SELECT * FROM rm_sedasi_praind"+qLatest);
            pq.setString(1, noRawat); rq = pq.executeQuery();
            sb.append(sectionHead("3. Pra Induksi &amp; Durante"));
            if (rq.next()) {
                sb.append(row4("Tanggal",s(rq,"tgl_asuhan"),"Jam",s(rq,"jam")));
                sb.append(row4("Dokter Operator",s(rq,"dokter_operator"),"Ruang OT",s(rq,"ruang_ot")));
                sb.append(row2("Prosedur Operasi",s(rq,"prosedur_operasi")));
                sb.append(row4("TD",s(rq,"td_pra_ind"),"Nadi",s(rq,"nadi_pra_ind")));
                sb.append(row4("SpO2",s(rq,"spo2"),"Resp",s(rq,"resp_pra_ind")));
                sb.append(row4("Cairan Koloid",s(rq,"cairan_koloid"),"Cairan Kristaloid",s(rq,"cairan_kristaloid")));
                sb.append(row4("Transfusi WB",s(rq,"transfusi_wb"),"Transfusi PRC",s(rq,"transfusi_prc")));
                sb.append(row4("Transfusi FFP",s(rq,"transfusi_ffp"),"Transfusi Cryo",s(rq,"transfusi_cryo")));
                sb.append(row4("Output Darah",s(rq,"output_darah"),"Output Urin",s(rq,"output_urin")));
                sb.append(row4("Output NGT",s(rq,"output_ngt"),"Suara Napas",s(rq,"suara_napas")));
            } else sb.append("<tr><td colspan='4' class='val'><i>Belum ada data</i></td></tr>");
            rq.close(); pq.close();

            // ── SECTION: Pasca ──
            pq = c.prepareStatement("SELECT * FROM rm_sedasi_pasca"+qLatest);
            pq.setString(1, noRawat); rq = pq.executeQuery();
            sb.append(sectionHead("4. Pasca Sedasi / Anestesi"));
            if (rq.next()) {
                sb.append(row4("Tanggal",s(rq,"tgl_asuhan"),"Jam",s(rq,"jam")));
                sb.append(row2("Nama Pengkaji",s(rq,"nama_pengkaji")));
                sb.append(row4("TD",s(rq,"td_pasca"),"Nadi",s(rq,"n_pasca")));
                sb.append(row4("Resp",s(rq,"resp_pasca"),"Suhu",s(rq,"s_pasca")));
                sb.append(row4("Skor Aidrete",s(rq,"skor_aidrete"),"Skor Nyeri",s(rq,"skor_nyeri")));
                sb.append(row2("Skor Bromage",s(rq,"skor_bromage")));
            } else sb.append("<tr><td colspan='4' class='val'><i>Belum ada data</i></td></tr>");
            rq.close(); pq.close();

            // ── SECTION: Recovery Room ──
            pq = c.prepareStatement("SELECT * FROM rm_sedasi_rr"+qLatest);
            pq.setString(1, noRawat); rq = pq.executeQuery();
            sb.append(sectionHead("5. Instruksi Khusus Recovery Room"));
            if (rq.next()) {
                sb.append(row4("Tanggal",s(rq,"tgl_asuhan"),"Jam",s(rq,"jam")));
                sb.append(row2("Instruksi RR",s(rq,"instruksi_rr").replace("\n","<br>")));
            } else sb.append("<tr><td colspan='4' class='val'><i>Belum ada data</i></td></tr>");
            rq.close(); pq.close();

            // ── SECTION: Discharge ──
            pq = c.prepareStatement("SELECT * FROM rm_sedasi_discharge"+qLatest);
            pq.setString(1, noRawat); rq = pq.executeQuery();
            sb.append(sectionHead("6. Discharge"));
            if (rq.next()) {
                sb.append(row4("Tanggal",s(rq,"tgl_asuhan"),"Jam",s(rq,"jam")));
                sb.append(row4("Discharge Ke",s(rq,"discharge_ke"),"",""));
                sb.append(row4("TD",s(rq,"td_discharge"),"Nadi",s(rq,"n_discharge")));
                sb.append(row4("Resp",s(rq,"resp_discharge"),"Suhu",s(rq,"s_discharge")));
                sb.append(row4("VAS",s(rq,"vas_discharge"),"",""));
                sb.append(row2("Instruksi Anestesi",s(rq,"instruksi_anestesi").replace("\n","<br>")));
            } else sb.append("<tr><td colspan='4' class='val'><i>Belum ada data</i></td></tr>");
            rq.close(); pq.close();

            // ── SECTION: Monitoring Durante (Gas) ──
            pq = c.prepareStatement("SELECT * FROM rm_sedasi_durante"+qLatest);
            pq.setString(1, noRawat); rq = pq.executeQuery();
            sb.append(sectionHead("7. Monitoring Durante Anestesi"));
            if (rq.next()) {
                sb.append(row4("Tanggal",s(rq,"tgl_asuhan"),"Jam",s(rq,"jam")));
                sb.append(row4("Oksigen",s(rq,"oksigen")+" L/mnt","N2O",s(rq,"n2o")+" L/mnt"));
                sb.append(row4("Air L/mnt",s(rq,"air_lmenit"),"SpO2",s(rq,"saturasi_o2")+"%"));
                sb.append(row4("FiO2",s(rq,"fio2"),"End Tidal CO2",s(rq,"end_tidal_co2")));
                sb.append(row4("EKG",s(rq,"ekg"),"CVP/PCWP",s(rq,"cvp_pcwp")));
                sb.append(row4("Suhu",s(rq,"suhu"),"Urin",s(rq,"urin")));
                sb.append(row4("Lain-lain",s(rq,"lain_lain"),"Circuit",s(rq,"circuit")));
                sb.append(row4("Frek. Napas",s(rq,"frekuensi_pernapasan"),"Vol. Tidal",s(rq,"volume_tidal")));
                sb.append(row4("Peak Pressure",s(rq,"peak_pressure"),"PEEP",s(rq,"peep")));
                sb.append(row2("Komplikasi",s(rq,"komplikasi")));
            } else sb.append("<tr><td colspan='4' class='val'><i>Belum ada data</i></td></tr>");
            rq.close(); pq.close();

            // ── SECTION: Catatan ──
            pq = c.prepareStatement("SELECT * FROM rm_sedasi_catatan"+qLatest);
            pq.setString(1, noRawat); rq = pq.executeQuery();
            sb.append(sectionHead("8. Catatan"));
            if (rq.next()) {
                sb.append(row4("Tgl Operasi",s(rq,"operasi_tanggal"),
                    "Jam Mulai Operasi", String.format("%02d:%02d:%02d",
                        safeInt(rq,"operasi_jam"), safeInt(rq,"operasi_menit"), safeInt(rq,"operasi_detik"))));
                sb.append(row4("Tgl Anestesi",s(rq,"anestesi_tanggal"),
                    "Jam Mulai Anestesi", String.format("%02d:%02d:%02d",
                        safeInt(rq,"anestesi_jam"), safeInt(rq,"anestesi_menit"), safeInt(rq,"anestesi_detik"))));
                sb.append(row4("Tgl Antibiotik",s(rq,"antibiotik_tanggal"),
                    "Jam Antibiotik", String.format("%02d:%02d:%02d",
                        safeInt(rq,"antibiotik_jam"), safeInt(rq,"antibiotik_menit"), safeInt(rq,"antibiotik_detik"))));
                String pasca = "";
                if ("1".equals(rq.getString("pasca_ekstubasi"))) pasca += "Ekstubasi, ";
                if ("1".equals(rq.getString("pasca_intubasi"))) pasca += "Intubasi, ";
                if ("1".equals(rq.getString("pasca_oral_nasal"))) pasca += "Oral/Nasal Airway, ";
                if ("1".equals(rq.getString("pasca_sadar"))) pasca += "Sadar, ";
                if ("1".equals(rq.getString("pasca_mengantuk"))) pasca += "Mengantuk, ";
                if ("1".equals(rq.getString("pasca_stabil"))) pasca += "Stabil, ";
                if ("1".equals(rq.getString("pasca_tidak_stabil"))) pasca += "Tidak Stabil, ";
                if (pasca.endsWith(", ")) pasca = pasca.substring(0, pasca.length()-2);
                sb.append(row2("Kondisi Pasca Operasi", pasca.isEmpty() ? "-" : pasca));
            } else sb.append("<tr><td colspan='4' class='val'><i>Belum ada data</i></td></tr>");
            rq.close(); pq.close();
            c.close();

            // ── GRAFIK ──
            DlgGrafikAnestesi grafik = new DlgGrafikAnestesi(null, false);
            grafik.setNoRawat(noRawat, nmPasien);
            String chartB64 = "";
            try {
                BufferedImage chartImg = grafik.getChartImage(1100, 450);
                chartB64 = imgToBase64(chartImg);
            } catch (Exception ex) { System.out.println("chartImg: "+ex); }

            // ── QR CODES (PHP) ──
            String baseUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB();
            // Trigger generation
            try { new java.net.URL(baseUrl+"/penggajian/generateqrcode.php?kodedokter="+java.net.URLEncoder.encode(kdDokter,"UTF-8")).openStream().close(); } catch(Exception e){}
            try { new java.net.URL(baseUrl+"/penggajian/generateqrcodepetugas.php?kodepetugas="+java.net.URLEncoder.encode(kdPetugas,"UTF-8")).openStream().close(); } catch(Exception e){}
            String qrDokterUrl  = baseUrl + "/penggajian/temp/" + kdDokter + ".png";
            String qrPetugasUrl = baseUrl + "/penggajian/temp/petugas_" + kdPetugas.replaceAll("[/\\\\: ]","_") + ".png";

            // ── BUILD HTML ──
            String css =
                "body{font-family:Tahoma,Arial,sans-serif;font-size:11px;margin:10px;color:#222;}" +
                "h2{font-size:14px;text-align:center;margin:0;}" +
                "h3{font-size:12px;text-align:center;margin:2px 0 8px 0;color:#444;}" +
                ".info-tbl{width:100%;border-collapse:collapse;margin-bottom:8px;}" +
                ".info-tbl td{padding:2px 6px;}" +
                ".main-tbl{width:100%;border-collapse:collapse;margin-bottom:10px;}" +
                ".sec{background:#1a3a6b;color:#fff;font-weight:bold;padding:4px 8px;font-size:11px;}" +
                ".lbl{background:#e8ecf4;color:#333;padding:3px 8px;width:18%;white-space:nowrap;font-weight:bold;border:1px solid #ccd;}" +
                ".val{background:#fff;padding:3px 8px;width:32%;border:1px solid #dde;}" +
                ".footer{display:flex;justify-content:space-around;margin-top:16px;border-top:1px solid #ccc;padding-top:12px;}" +
                ".signer{text-align:center;font-size:11px;}" +
                ".signer-name{font-weight:bold;margin-top:6px;}" +
                "@media print{body{margin:0;}.no-print{display:none;}}";

            String html =
                "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Catatan Sedasi &amp; Anestesi</title>" +
                "<style>"+css+"</style></head><body>" +
                "<div style='text-align:center;margin-bottom:8px;'>" +
                "<h2>"+akses.getnamars()+"</h2>" +
                "<div style='font-size:10px;'>"+akses.getalamatrs()+", "+akses.getkabupatenrs()+" &bull; "+akses.getkontakrs()+"</div>" +
                "<hr style='border:1px solid #1a3a6b;margin:4px 0;'>" +
                "<h3>CATATAN SEDASI &amp; ANESTESI</h3></div>" +
                "<table class='info-tbl'>" +
                "<tr><td><b>No. Rawat</b></td><td>: "+noRawat+"</td><td><b>No. RM</b></td><td>: "+noRM+"</td></tr>" +
                "<tr><td><b>Nama Pasien</b></td><td>: "+nmPasien+"</td><td><b>Tgl. Lahir</b></td><td>: "+tglLahir+" &nbsp; ("+jk+")</td></tr>" +
                "<tr><td><b>Dokter Anestesi</b></td><td>: "+nmDokter+"</td><td><b>Penata Anestesi</b></td><td>: "+nmPenata+"</td></tr>" +
                "</table>" +
                "<table class='main-tbl'>"+sb+"</table>" +
                (chartB64.isEmpty() ? "" :
                "<div style='margin:12px 0;'><b style='font-size:11px;'>Grafik Monitoring Anestesi:</b><br>" +
                "<img src='data:image/png;base64,"+chartB64+"' style='width:100%;border:1px solid #ccc;margin-top:4px;'/></div>") +
                "<div class='footer'>" +
                "<div class='signer'>" +
                (kdDokter.isEmpty() ? "" : "<img src='"+qrDokterUrl+"' width='120' height='120' onerror=\"this.style.display='none'\"/>") +
                "<div class='signer-name'>"+nmDokter+"</div>" +
                "<div>Dokter Anestesi</div></div>" +
                "<div class='signer'>" +
                (kdPetugas.isEmpty() ? "" : "<img src='"+qrPetugasUrl+"' width='120' height='120' onerror=\"this.style.display='none'\"/>") +
                "<div class='signer-name'>"+nmPetugas+"</div>" +
                "<div>Penata Anestesi</div></div>" +
                "</div>" +
                "<script>window.onload=function(){window.print();}</script>" +
                "</body></html>";

            File f = File.createTempFile("SedansiAnestesi_"+noRawat.replaceAll("[^A-Za-z0-9]","_")+"_", ".html");
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f))) { bw.write(html); }
            Desktop.getDesktop().browse(f.toURI());

        } catch (Exception ex) {
            System.out.println("cetakHTML: "+ex);
            JOptionPane.showMessageDialog(null, "Gagal cetak: "+ex.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private int safeInt(java.sql.ResultSet r, String col) {
        try { return r.getInt(col); } catch (Exception e) { return 0; }
    }
}
