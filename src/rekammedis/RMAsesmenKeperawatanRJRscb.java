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
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import widget.*;

public class RMAsesmenKeperawatanRJRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private Label LCount;

    // Pengkajian
    private TextArea TKeluhanUtama, TRiwayatPenyakitSekarang, TRiwayatAlergi, TRiwayatPenyakitDahulu;
    private TextArea TDiagnosaKeperawatan, TRencanaAsuhan, TTindakan;
    private ComboBox CmbSumberPengkajian;
    private TextBox TSumberLain, THubunganPasien;

    // Pemeriksaan Fisik
    private TextBox TTD, TSuhu, TPernafasan, TNadi, TBB, TTB;
    private ComboBox CmbKesadaran, CmbNyeri;
    private TextBox TNyeriLokasi, TNyeriScore;

    // Skrining Nyeri
    private ComboBox CmbSkriningNyeri;
    private TextBox TSkalaNyeri, TLokasiNyeri, TDurasiNyeri, TFrekuensiNyeri;
    private CekBox ChkNyeriObat, ChkNyeriMusik, ChkNyeriIstirahat, ChkNyeriPosisi;
    private TextBox TNyeriHilangLainnya;
    private CekBox ChkDiberitahuDokter;
    private TextBox TDiberitahuPukul;

    // Skrining Risiko Jatuh
    private ComboBox CmbJatuhA, CmbJatuhB;
    private TextBox TJatuhHasil;
    private CekBox ChkJatuhDiberitahuDokter;
    private TextBox TJatuhDiberitahuPukul;

    // Status Fungsional
    private ComboBox CmbStatusFungsional;
    private TextBox TFungsionalBantuan, TFungsionalDilaporkanPukul;

    // Status Ekonomi
    private ComboBox CmbStatusEkonomi;
    private TextBox TStatusEkonomiLain;

    // Bio-Psiko-Sosio-Kultur-Spiritual
    private ComboBox CmbStatusPernikahan, CmbPekerjaan, CmbAgama, CmbPsiko, CmbMental;
    private TextBox TSuku;

    // Skrining Gizi Dewasa
    private ComboBox CmbGiziDewasa1, CmbGiziDewasa2, CmbGiziDewasa3, CmbGiziDewasa4, CmbGiziDewasa5, CmbGiziDewasa6;
    private TextBox TGiziDewasaTotal, TGiziDewasaKesimpulan;

    // Skrining Gizi Anak
    private ComboBox CmbGiziAnak1, CmbGiziAnak2, CmbGiziAnak3, CmbGiziAnak4;
    private TextBox TGiziAnakTotal, TGiziAnakKesimpulan;

    // Masalah Keperawatan
    private CekBox ChkMasalahNyeri, ChkMasalahJalanNafas, ChkMasalahDefisitNutrisi, ChkMasalahKurangPengetahuan;
    private CekBox ChkMasalahGangguanMobilitas, ChkMasalahKecemasan, ChkMasalahIntegritasKulit, ChkMasalahHipertermi;
    private TextBox TMasalahLainnya;
    private TextArea TTujuanTarget;

    // Petugas
    private TextBox KdPetugas, NmPetugas;
    private Button BtnPetugas;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);

    private widget.Table tbObat;
    private DefaultTableModel tabMode;
    private ScrollPane Scroll;
    private JTabbedPane tabPane;
    private PanelBiasa FormInput;
    private ScrollPane ScrollInput;
    public widget.editorpane LoadHTML;

    public RMAsesmenKeperawatanRJRscb(java.awt.Frame parent, boolean modal) {
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
            "::[ Asesmen Keperawatan Pasien Rawat Jalan ]::",
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
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","Keluhan Utama","Kesadaran","Petugas"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbObat.setModel(tabMode);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { if (tabMode.getRowCount() != 0) getData(); }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE && tabMode.getRowCount() != 0) {
                    getData();
                    if (tabPane != null) tabPane.setSelectedIndex(0);
                    e.consume();
                }
            }
        });
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem mnCetak = new JMenuItem("Cetak Asesmen Keperawatan RJ");
        mnCetak.setIcon(new ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetak.addActionListener(evt -> BtnPrint.doClick());
        popupMenu.add(mnCetak);
        tbObat.setComponentPopupMenu(popupMenu);
        Scroll.setViewportView(tbObat);

        // FORM INPUT
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(920, 2800));
        FormInput.setLayout(null);

        int y = 10;
        JLabel lblHeader = new JLabel("ASESMEN KEPERAWATAN PASIEN RAWAT JALAN");
        lblHeader.setFont(new java.awt.Font("Tahoma", 1, 13)); lblHeader.setBounds(10, y, 500, 20); FormInput.add(lblHeader);
        JLabel lblKode = new JLabel("RM.KEP.08.016.A.2019"); lblKode.setFont(new java.awt.Font("Tahoma", 0, 8)); lblKode.setBounds(700, y, 150, 20); FormInput.add(lblKode);

        // === SECTION: TANGGAL ===
        y += 30;
        Label lblTgl = new Label(); lblTgl.setText("Tanggal :"); lblTgl.setBounds(10, y, 130, 23); FormInput.add(lblTgl);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setBounds(145, y, 130, 23); FormInput.add(TglAsuhan);

        // === SECTION 1: PENGKAJIAN ===
        y += 35;
        JLabel lblSec1 = new JLabel("I. PENGKAJIAN");
        lblSec1.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec1.setBounds(10, y, 200, 20); FormInput.add(lblSec1);

        y += 25;
        Label lblKeluhan = new Label(); lblKeluhan.setText("Keluhan Utama :"); lblKeluhan.setBounds(10, y, 130, 23); FormInput.add(lblKeluhan);
        TKeluhanUtama = new TextArea(); TKeluhanUtama.setBounds(145, y, 400, 50); TKeluhanUtama.setBorder(BorderFactory.createLineBorder(Color.BLACK)); FormInput.add(TKeluhanUtama);

        y += 55;
        Label lblSumber = new Label(); lblSumber.setText("Sumber Pengkajian :"); lblSumber.setBounds(10, y, 130, 23); FormInput.add(lblSumber);
        CmbSumberPengkajian = new ComboBox();
        CmbSumberPengkajian.setModel(new DefaultComboBoxModel(new String[]{"Pasien Sendiri","Lainnya"}));
        CmbSumberPengkajian.setBounds(145, y, 150, 23); FormInput.add(CmbSumberPengkajian);
        CmbSumberPengkajian.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                TSumberLain.setEnabled("Lainnya".equals(CmbSumberPengkajian.getSelectedItem().toString()));
                if (!"Lainnya".equals(CmbSumberPengkajian.getSelectedItem().toString())) TSumberLain.setText("");
            }
        });

        y += 27;
        Label lblSumberLain = new Label(); lblSumberLain.setText("Sumber Lain :"); lblSumberLain.setBounds(10, y, 130, 23); FormInput.add(lblSumberLain);
        TSumberLain = new TextBox(); TSumberLain.setBounds(145, y, 250, 23); TSumberLain.setEnabled(false); FormInput.add(TSumberLain);

        y += 27;
        Label lblHubungan = new Label(); lblHubungan.setText("Hubungan Pasien :"); lblHubungan.setBounds(10, y, 130, 23); FormInput.add(lblHubungan);
        THubunganPasien = new TextBox(); THubunganPasien.setBounds(145, y, 250, 23); FormInput.add(THubunganPasien);

        y += 27;
        Label lblRPS = new Label(); lblRPS.setText("Riw. Peny. Sekarang :"); lblRPS.setBounds(10, y, 130, 23); FormInput.add(lblRPS);
        TRiwayatPenyakitSekarang = new TextArea(); TRiwayatPenyakitSekarang.setBounds(145, y, 400, 50); TRiwayatPenyakitSekarang.setBorder(BorderFactory.createLineBorder(Color.BLACK)); FormInput.add(TRiwayatPenyakitSekarang);

        y += 55;
        Label lblAlergi = new Label(); lblAlergi.setText("Riwayat Alergi :"); lblAlergi.setBounds(10, y, 130, 23); FormInput.add(lblAlergi);
        TRiwayatAlergi = new TextArea(); TRiwayatAlergi.setBounds(145, y, 400, 50); TRiwayatAlergi.setBorder(BorderFactory.createLineBorder(Color.BLACK)); FormInput.add(TRiwayatAlergi);

        y += 55;
        Label lblRPD = new Label(); lblRPD.setText("Riw. Peny. Dahulu :"); lblRPD.setBounds(10, y, 130, 23); FormInput.add(lblRPD);
        TRiwayatPenyakitDahulu = new TextArea(); TRiwayatPenyakitDahulu.setBounds(145, y, 400, 50); TRiwayatPenyakitDahulu.setBorder(BorderFactory.createLineBorder(Color.BLACK)); FormInput.add(TRiwayatPenyakitDahulu);

        y += 55;
        Label lblDiagKep = new Label(); lblDiagKep.setText("Diagnosa Kep. :"); lblDiagKep.setBounds(10, y, 130, 23); FormInput.add(lblDiagKep);
        TDiagnosaKeperawatan = new TextArea(); TDiagnosaKeperawatan.setBounds(145, y, 400, 50); TDiagnosaKeperawatan.setBorder(BorderFactory.createLineBorder(Color.BLACK)); FormInput.add(TDiagnosaKeperawatan);

        y += 55;
        Label lblRencana = new Label(); lblRencana.setText("Rencana Asuhan :"); lblRencana.setBounds(10, y, 130, 23); FormInput.add(lblRencana);
        TRencanaAsuhan = new TextArea(); TRencanaAsuhan.setBounds(145, y, 400, 50); TRencanaAsuhan.setBorder(BorderFactory.createLineBorder(Color.BLACK)); FormInput.add(TRencanaAsuhan);

        y += 55;
        Label lblTindakan = new Label(); lblTindakan.setText("Tindakan :"); lblTindakan.setBounds(10, y, 130, 23); FormInput.add(lblTindakan);
        TTindakan = new TextArea(); TTindakan.setBounds(145, y, 400, 50); TTindakan.setBorder(BorderFactory.createLineBorder(Color.BLACK)); FormInput.add(TTindakan);

        // === SECTION 2: PEMERIKSAAN FISIK ===
        y += 60;
        JLabel lblSec2 = new JLabel("II. PEMERIKSAAN FISIK");
        lblSec2.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec2.setBounds(10, y, 200, 20); FormInput.add(lblSec2);

        y += 25;
        Label lblTDFisik = new Label(); lblTDFisik.setText("TD (mmHg) :"); lblTDFisik.setBounds(10, y, 130, 23); FormInput.add(lblTDFisik);
        TTD = new TextBox(); TTD.setBounds(145, y, 80, 23); FormInput.add(TTD);
        Label lblSuhu = new Label(); lblSuhu.setText("Suhu :"); lblSuhu.setBounds(240, y, 40, 23); FormInput.add(lblSuhu);
        TSuhu = new TextBox(); TSuhu.setBounds(285, y, 60, 23); FormInput.add(TSuhu);
        Label lblPernafasan = new Label(); lblPernafasan.setText("Pernafasan :"); lblPernafasan.setBounds(360, y, 75, 23); FormInput.add(lblPernafasan);
        TPernafasan = new TextBox(); TPernafasan.setBounds(440, y, 60, 23); FormInput.add(TPernafasan);

        y += 27;
        Label lblNadi = new Label(); lblNadi.setText("Nadi :"); lblNadi.setBounds(10, y, 130, 23); FormInput.add(lblNadi);
        TNadi = new TextBox(); TNadi.setBounds(145, y, 60, 23); FormInput.add(TNadi);
        Label lblBB = new Label(); lblBB.setText("BB (kg) :"); lblBB.setBounds(220, y, 60, 23); FormInput.add(lblBB);
        TBB = new TextBox(); TBB.setBounds(285, y, 60, 23); FormInput.add(TBB);
        Label lblTB = new Label(); lblTB.setText("TB (cm) :"); lblTB.setBounds(360, y, 60, 23); FormInput.add(lblTB);
        TTB = new TextBox(); TTB.setBounds(425, y, 60, 23); FormInput.add(TTB);

        y += 27;
        Label lblKesadaran = new Label(); lblKesadaran.setText("Kesadaran :"); lblKesadaran.setBounds(10, y, 130, 23); FormInput.add(lblKesadaran);
        CmbKesadaran = new ComboBox();
        CmbKesadaran.setModel(new DefaultComboBoxModel(new String[]{"CM","Apatis","Somnolent","Soporocoma","Koma"}));
        CmbKesadaran.setBounds(145, y, 130, 23); FormInput.add(CmbKesadaran);

        y += 27;
        Label lblNyeriFisik = new Label(); lblNyeriFisik.setText("Nyeri :"); lblNyeriFisik.setBounds(10, y, 130, 23); FormInput.add(lblNyeriFisik);
        CmbNyeri = new ComboBox();
        CmbNyeri.setModel(new DefaultComboBoxModel(new String[]{"Ya","Tidak"}));
        CmbNyeri.setBounds(145, y, 80, 23); FormInput.add(CmbNyeri);
        Label lblNyeriLokasi = new Label(); lblNyeriLokasi.setText("Lokasi :"); lblNyeriLokasi.setBounds(240, y, 50, 23); FormInput.add(lblNyeriLokasi);
        TNyeriLokasi = new TextBox(); TNyeriLokasi.setBounds(295, y, 120, 23); FormInput.add(TNyeriLokasi);
        Label lblNyeriScore = new Label(); lblNyeriScore.setText("Score :"); lblNyeriScore.setBounds(430, y, 45, 23); FormInput.add(lblNyeriScore);
        TNyeriScore = new TextBox(); TNyeriScore.setBounds(480, y, 50, 23); FormInput.add(TNyeriScore);

        // === SECTION 3: SKRINING NYERI ===
        y += 35;
        JLabel lblSec3 = new JLabel("III. SKRINING NYERI");
        lblSec3.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec3.setBounds(10, y, 200, 20); FormInput.add(lblSec3);

        y += 25;
        Label lblSkriningNyeri = new Label(); lblSkriningNyeri.setText("Skrining Nyeri :"); lblSkriningNyeri.setBounds(10, y, 130, 23); FormInput.add(lblSkriningNyeri);
        CmbSkriningNyeri = new ComboBox();
        CmbSkriningNyeri.setModel(new DefaultComboBoxModel(new String[]{"Tidak ada","Nyeri Kronis","Nyeri akut"}));
        CmbSkriningNyeri.setBounds(145, y, 150, 23); FormInput.add(CmbSkriningNyeri);

        y += 27;
        Label lblSkala = new Label(); lblSkala.setText("Skala Nyeri :"); lblSkala.setBounds(10, y, 130, 23); FormInput.add(lblSkala);
        TSkalaNyeri = new TextBox(); TSkalaNyeri.setBounds(145, y, 80, 23); FormInput.add(TSkalaNyeri);
        Label lblLokNyeri = new Label(); lblLokNyeri.setText("Lokasi :"); lblLokNyeri.setBounds(240, y, 50, 23); FormInput.add(lblLokNyeri);
        TLokasiNyeri = new TextBox(); TLokasiNyeri.setBounds(295, y, 150, 23); FormInput.add(TLokasiNyeri);

        y += 27;
        Label lblDurasi = new Label(); lblDurasi.setText("Durasi Nyeri :"); lblDurasi.setBounds(10, y, 130, 23); FormInput.add(lblDurasi);
        TDurasiNyeri = new TextBox(); TDurasiNyeri.setBounds(145, y, 120, 23); FormInput.add(TDurasiNyeri);
        Label lblFrekuensi = new Label(); lblFrekuensi.setText("Frekuensi :"); lblFrekuensi.setBounds(280, y, 70, 23); FormInput.add(lblFrekuensi);
        TFrekuensiNyeri = new TextBox(); TFrekuensiNyeri.setBounds(355, y, 120, 23); FormInput.add(TFrekuensiNyeri);

        y += 27;
        Label lblHilang = new Label(); lblHilang.setText("Nyeri Hilang dgn :"); lblHilang.setBounds(10, y, 130, 23); FormInput.add(lblHilang);
        ChkNyeriObat = new CekBox(); ChkNyeriObat.setText("Obat"); ChkNyeriObat.setBounds(145, y, 65, 23); ChkNyeriObat.setSelected(false); ChkNyeriObat.setOpaque(false); FormInput.add(ChkNyeriObat);
        ChkNyeriMusik = new CekBox(); ChkNyeriMusik.setText("Musik"); ChkNyeriMusik.setBounds(215, y, 70, 23); ChkNyeriMusik.setSelected(false); ChkNyeriMusik.setOpaque(false); FormInput.add(ChkNyeriMusik);
        ChkNyeriIstirahat = new CekBox(); ChkNyeriIstirahat.setText("Istirahat"); ChkNyeriIstirahat.setBounds(290, y, 80, 23); ChkNyeriIstirahat.setSelected(false); ChkNyeriIstirahat.setOpaque(false); FormInput.add(ChkNyeriIstirahat);
        ChkNyeriPosisi = new CekBox(); ChkNyeriPosisi.setText("Posisi"); ChkNyeriPosisi.setBounds(375, y, 70, 23); ChkNyeriPosisi.setSelected(false); ChkNyeriPosisi.setOpaque(false); FormInput.add(ChkNyeriPosisi);

        y += 25;
        Label lblHilangLain = new Label(); lblHilangLain.setText("Lainnya :"); lblHilangLain.setBounds(10, y, 130, 23); FormInput.add(lblHilangLain);
        TNyeriHilangLainnya = new TextBox(); TNyeriHilangLainnya.setBounds(145, y, 250, 23); FormInput.add(TNyeriHilangLainnya);

        y += 30;
        JLabel lblImgNyeri = new JLabel(new javax.swing.ImageIcon(getClass().getResource("/picture/nyeri.png")));
        lblImgNyeri.setBounds(40, y, 520, 130); FormInput.add(lblImgNyeri);
        y += 130;

        y += 27;
        Label lblDbrthDokter = new Label(); lblDbrthDokter.setText("Diberitahu Dokter :"); lblDbrthDokter.setBounds(10, y, 130, 23); FormInput.add(lblDbrthDokter);
        ChkDiberitahuDokter = new CekBox(); ChkDiberitahuDokter.setText("Ya"); ChkDiberitahuDokter.setBounds(145, y, 55, 23); ChkDiberitahuDokter.setSelected(false); ChkDiberitahuDokter.setOpaque(false); FormInput.add(ChkDiberitahuDokter);
        Label lblPukul = new Label(); lblPukul.setText("Pukul :"); lblPukul.setBounds(210, y, 45, 23); FormInput.add(lblPukul);
        TDiberitahuPukul = new TextBox(); TDiberitahuPukul.setBounds(260, y, 80, 23); FormInput.add(TDiberitahuPukul);

        // === SECTION 4: SKRINING RISIKO JATUH ===
        y += 35;
        JLabel lblSec4 = new JLabel("IV. SKRINING RISIKO JATUH");
        lblSec4.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec4.setBounds(10, y, 250, 20); FormInput.add(lblSec4);

        y += 25;
        Label lblJatuhA = new Label(); lblJatuhA.setText("Tidak seimbang? :"); lblJatuhA.setBounds(10, y, 130, 23); FormInput.add(lblJatuhA);
        CmbJatuhA = new ComboBox();
        CmbJatuhA.setModel(new DefaultComboBoxModel(new String[]{"Ya","Tidak"}));
        CmbJatuhA.setBounds(145, y, 80, 23); FormInput.add(CmbJatuhA);
        CmbJatuhA.addItemListener(e -> { if (e.getStateChange() == ItemEvent.SELECTED) hitungJatuh(); });

        y += 27;
        Label lblJatuhB = new Label(); lblJatuhB.setText("Memegang pinggiran? :"); lblJatuhB.setBounds(10, y, 130, 23); FormInput.add(lblJatuhB);
        CmbJatuhB = new ComboBox();
        CmbJatuhB.setModel(new DefaultComboBoxModel(new String[]{"Ya","Tidak"}));
        CmbJatuhB.setBounds(145, y, 80, 23); FormInput.add(CmbJatuhB);
        CmbJatuhB.addItemListener(e -> { if (e.getStateChange() == ItemEvent.SELECTED) hitungJatuh(); });

        y += 27;
        Label lblJatuhHasil = new Label(); lblJatuhHasil.setText("Hasil :"); lblJatuhHasil.setBounds(10, y, 130, 23); FormInput.add(lblJatuhHasil);
        TJatuhHasil = new TextBox(); TJatuhHasil.setBounds(145, y, 150, 23); TJatuhHasil.setEditable(false); FormInput.add(TJatuhHasil);

        y += 27;
        Label lblJDokter = new Label(); lblJDokter.setText("Diberitahu Dokter :"); lblJDokter.setBounds(10, y, 130, 23); FormInput.add(lblJDokter);
        ChkJatuhDiberitahuDokter = new CekBox(); ChkJatuhDiberitahuDokter.setText("Ya"); ChkJatuhDiberitahuDokter.setBounds(145, y, 55, 23); ChkJatuhDiberitahuDokter.setSelected(false); ChkJatuhDiberitahuDokter.setOpaque(false); FormInput.add(ChkJatuhDiberitahuDokter);
        Label lblJPukul = new Label(); lblJPukul.setText("Pukul :"); lblJPukul.setBounds(210, y, 45, 23); FormInput.add(lblJPukul);
        TJatuhDiberitahuPukul = new TextBox(); TJatuhDiberitahuPukul.setBounds(260, y, 80, 23); FormInput.add(TJatuhDiberitahuPukul);

        // === SECTION 5: STATUS FUNGSIONAL ===
        y += 35;
        JLabel lblSec5 = new JLabel("V. STATUS FUNGSIONAL");
        lblSec5.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec5.setBounds(10, y, 250, 20); FormInput.add(lblSec5);

        y += 25;
        Label lblFungsional = new Label(); lblFungsional.setText("Status Fungsional :"); lblFungsional.setBounds(10, y, 130, 23); FormInput.add(lblFungsional);
        CmbStatusFungsional = new ComboBox();
        CmbStatusFungsional.setModel(new DefaultComboBoxModel(new String[]{"Mandiri","Perlu bantuan","Ketergantungan total"}));
        CmbStatusFungsional.setBounds(145, y, 180, 23); FormInput.add(CmbStatusFungsional);
        CmbStatusFungsional.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                TFungsionalBantuan.setEnabled("Perlu bantuan".equals(CmbStatusFungsional.getSelectedItem().toString()));
                if (!"Perlu bantuan".equals(CmbStatusFungsional.getSelectedItem().toString())) TFungsionalBantuan.setText("");
            }
        });

        y += 27;
        Label lblBantuan = new Label(); lblBantuan.setText("Bantuan :"); lblBantuan.setBounds(10, y, 130, 23); FormInput.add(lblBantuan);
        TFungsionalBantuan = new TextBox(); TFungsionalBantuan.setBounds(145, y, 300, 23); TFungsionalBantuan.setEnabled(false); FormInput.add(TFungsionalBantuan);

        y += 27;
        Label lblFDilaporkan = new Label(); lblFDilaporkan.setText("Dilaporkan Pukul :"); lblFDilaporkan.setBounds(10, y, 130, 23); FormInput.add(lblFDilaporkan);
        TFungsionalDilaporkanPukul = new TextBox(); TFungsionalDilaporkanPukul.setBounds(145, y, 100, 23); FormInput.add(TFungsionalDilaporkanPukul);

        // === SECTION 6: STATUS EKONOMI ===
        y += 35;
        JLabel lblSec6 = new JLabel("VI. STATUS EKONOMI");
        lblSec6.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec6.setBounds(10, y, 250, 20); FormInput.add(lblSec6);

        y += 25;
        Label lblEkonomi = new Label(); lblEkonomi.setText("Status Ekonomi :"); lblEkonomi.setBounds(10, y, 130, 23); FormInput.add(lblEkonomi);
        CmbStatusEkonomi = new ComboBox();
        CmbStatusEkonomi.setModel(new DefaultComboBoxModel(new String[]{"Biaya Sendiri","Asuransi","Perusahaan","Lainnya"}));
        CmbStatusEkonomi.setBounds(145, y, 150, 23); FormInput.add(CmbStatusEkonomi);
        CmbStatusEkonomi.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                TStatusEkonomiLain.setEnabled("Lainnya".equals(CmbStatusEkonomi.getSelectedItem().toString()));
                if (!"Lainnya".equals(CmbStatusEkonomi.getSelectedItem().toString())) TStatusEkonomiLain.setText("");
            }
        });

        y += 27;
        Label lblEkonomiLain = new Label(); lblEkonomiLain.setText("Lainnya :"); lblEkonomiLain.setBounds(10, y, 130, 23); FormInput.add(lblEkonomiLain);
        TStatusEkonomiLain = new TextBox(); TStatusEkonomiLain.setBounds(145, y, 250, 23); TStatusEkonomiLain.setEnabled(false); FormInput.add(TStatusEkonomiLain);

        // === SECTION 7: BIO-PSIKO-SOSIO-KULTUR-SPIRITUAL ===
        y += 35;
        JLabel lblSec7 = new JLabel("VII. BIO-PSIKO-SOSIO-KULTUR-SPIRITUAL");
        lblSec7.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec7.setBounds(10, y, 350, 20); FormInput.add(lblSec7);

        y += 25;
        Label lblPernikahan = new Label(); lblPernikahan.setText("Status Pernikahan :"); lblPernikahan.setBounds(10, y, 130, 23); FormInput.add(lblPernikahan);
        CmbStatusPernikahan = new ComboBox();
        CmbStatusPernikahan.setModel(new DefaultComboBoxModel(new String[]{"Menikah","Belum Menikah"}));
        CmbStatusPernikahan.setBounds(145, y, 150, 23); FormInput.add(CmbStatusPernikahan);

        y += 27;
        Label lblPekerjaan = new Label(); lblPekerjaan.setText("Pekerjaan :"); lblPekerjaan.setBounds(10, y, 130, 23); FormInput.add(lblPekerjaan);
        CmbPekerjaan = new ComboBox();
        CmbPekerjaan.setModel(new DefaultComboBoxModel(new String[]{"PNS","Karyawan Swasta","Lainnya"}));
        CmbPekerjaan.setBounds(145, y, 150, 23); FormInput.add(CmbPekerjaan);

        y += 27;
        Label lblSuku = new Label(); lblSuku.setText("Suku :"); lblSuku.setBounds(10, y, 130, 23); FormInput.add(lblSuku);
        TSuku = new TextBox(); TSuku.setBounds(145, y, 200, 23); FormInput.add(TSuku);

        y += 27;
        Label lblAgama = new Label(); lblAgama.setText("Agama :"); lblAgama.setBounds(10, y, 130, 23); FormInput.add(lblAgama);
        CmbAgama = new ComboBox();
        CmbAgama.setModel(new DefaultComboBoxModel(new String[]{"Islam","Hindu","Kristin","Lainnya"}));
        CmbAgama.setBounds(145, y, 130, 23); FormInput.add(CmbAgama);

        y += 27;
        Label lblPsiko = new Label(); lblPsiko.setText("Psikologis :"); lblPsiko.setBounds(10, y, 130, 23); FormInput.add(lblPsiko);
        CmbPsiko = new ComboBox();
        CmbPsiko.setModel(new DefaultComboBoxModel(new String[]{"Tenang","Cemas","Takut"}));
        CmbPsiko.setBounds(145, y, 120, 23); FormInput.add(CmbPsiko);

        y += 27;
        Label lblMental = new Label(); lblMental.setText("Mental :"); lblMental.setBounds(10, y, 130, 23); FormInput.add(lblMental);
        CmbMental = new ComboBox();
        CmbMental.setModel(new DefaultComboBoxModel(new String[]{"Kooperatif","Tidak kooperatif"}));
        CmbMental.setBounds(145, y, 160, 23); FormInput.add(CmbMental);

        // === SECTION 8: SKRINING GIZI DEWASA ===
        y += 35;
        JLabel lblSec8 = new JLabel("VIII. SKRINING GIZI PASIEN DEWASA");
        lblSec8.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec8.setBounds(10, y, 400, 20); FormInput.add(lblSec8);

        final int gdX = 10, gdColNo = 50, gdColQ = 420, gdColA = 230, gdRowH = 50, gdHdrH = 25;
        String[] gdQuestions = {
            "Apakah pasien terlihat kurus ?",
            "Apakah pakaian anda terasa lebih longgar?",
            "Apakah akhir-akhir ini Anda kehilangan berat badan secara tidak sengaja (3-6 bulan terakhir)?",
            "Apakah Anda mengalami penurunan asupan makan selama 1 minggu terakhir?",
            "Apakah Anda merasakan lemah, loyo dan tidak bertenaga ?",
            "Apakah Anda menderita suatu penyakit yang mengakibatkan adanya perubahan jumlah atau jenis makanan yang Anda makan?"
        };
        javax.swing.border.Border gdBorder = BorderFactory.createLineBorder(Color.BLACK);

        y += 25;
        JLabel gdH1 = new JLabel("No.", SwingConstants.CENTER); gdH1.setFont(new java.awt.Font("Tahoma",1,10)); gdH1.setBorder(gdBorder); gdH1.setOpaque(true); gdH1.setBackground(Color.WHITE); gdH1.setBounds(gdX, y, gdColNo, gdHdrH); FormInput.add(gdH1);
        JLabel gdH2 = new JLabel("Pertanyaan", SwingConstants.CENTER); gdH2.setFont(new java.awt.Font("Tahoma",1,10)); gdH2.setBorder(gdBorder); gdH2.setOpaque(true); gdH2.setBackground(Color.WHITE); gdH2.setBounds(gdX+gdColNo, y, gdColQ, gdHdrH); FormInput.add(gdH2);
        JLabel gdH3 = new JLabel("Jawaban (Skor)", SwingConstants.CENTER); gdH3.setFont(new java.awt.Font("Tahoma",1,10)); gdH3.setBorder(gdBorder); gdH3.setOpaque(true); gdH3.setBackground(Color.WHITE); gdH3.setBounds(gdX+gdColNo+gdColQ, y, gdColA, gdHdrH); FormInput.add(gdH3);
        y += gdHdrH;

        ComboBox[] gdCombos = new ComboBox[6];
        for (int i = 0; i < 6; i++) {
            JLabel noCell = new JLabel((i+1)+".", SwingConstants.CENTER); noCell.setFont(new java.awt.Font("Tahoma",0,10)); noCell.setBorder(gdBorder); noCell.setOpaque(true); noCell.setBackground(Color.WHITE); noCell.setBounds(gdX, y, gdColNo, gdRowH); FormInput.add(noCell);
            JLabel qCell = new JLabel("<html><div style='padding:4px;'>"+gdQuestions[i]+"</div></html>"); qCell.setFont(new java.awt.Font("Tahoma",0,10)); qCell.setVerticalAlignment(SwingConstants.CENTER); qCell.setBorder(gdBorder); qCell.setOpaque(true); qCell.setBackground(Color.WHITE); qCell.setBounds(gdX+gdColNo, y, gdColQ, gdRowH); FormInput.add(qCell);
            JPanel aCell = new JPanel(null); aCell.setBackground(Color.WHITE); aCell.setBorder(gdBorder); aCell.setBounds(gdX+gdColNo+gdColQ, y, gdColA, gdRowH); FormInput.add(aCell);
            JLabel opt1 = new JLabel("a.  Ya = 1"); opt1.setFont(new java.awt.Font("Tahoma",0,10)); opt1.setBounds(8, 6, 80, 16); aCell.add(opt1);
            JLabel opt2 = new JLabel("b.  Tidak = 0"); opt2.setFont(new java.awt.Font("Tahoma",0,10)); opt2.setBounds(8, 26, 80, 16); aCell.add(opt2);
            gdCombos[i] = new ComboBox();
            gdCombos[i].setModel(new DefaultComboBoxModel(new String[]{"Tidak","Ya"}));
            gdCombos[i].setBounds(gdColA-90, 14, 80, 23); aCell.add(gdCombos[i]);
            gdCombos[i].addItemListener(e -> { if (e.getStateChange() == ItemEvent.SELECTED) hitungGiziDewasa(); });
            y += gdRowH;
        }
        CmbGiziDewasa1 = gdCombos[0]; CmbGiziDewasa2 = gdCombos[1]; CmbGiziDewasa3 = gdCombos[2];
        CmbGiziDewasa4 = gdCombos[3]; CmbGiziDewasa5 = gdCombos[4]; CmbGiziDewasa6 = gdCombos[5];

        JLabel gdTotLbl = new JLabel("TOTAL SKOR"); gdTotLbl.setFont(new java.awt.Font("Tahoma",1,10)); gdTotLbl.setBorder(gdBorder); gdTotLbl.setOpaque(true); gdTotLbl.setBackground(Color.WHITE); gdTotLbl.setBounds(gdX, y, gdColNo+gdColQ, gdHdrH);
        gdTotLbl.setHorizontalAlignment(SwingConstants.CENTER); FormInput.add(gdTotLbl);
        JPanel totCell = new JPanel(null); totCell.setBackground(Color.WHITE); totCell.setBorder(gdBorder); totCell.setBounds(gdX+gdColNo+gdColQ, y, gdColA, gdHdrH); FormInput.add(totCell);
        TGiziDewasaTotal = new TextBox(); TGiziDewasaTotal.setBounds(8, 2, 60, 22); TGiziDewasaTotal.setEditable(false); TGiziDewasaTotal.setText("0"); totCell.add(TGiziDewasaTotal);
        TGiziDewasaKesimpulan = new TextBox(); TGiziDewasaKesimpulan.setBounds(75, 2, gdColA-85, 22); TGiziDewasaKesimpulan.setEditable(false); TGiziDewasaKesimpulan.setText("Tidak Berisiko"); totCell.add(TGiziDewasaKesimpulan);
        y += gdHdrH;

        JLabel gdKesimp = new JLabel("<html><b>KESIMPULAN</b><br><span style='padding-left:15px;'>Skor 0-2 = Tidak Berisiko</span><br><span style='padding-left:15px;'>Skor 3-6 = Berisiko Malnutrisi</span></html>");
        gdKesimp.setFont(new java.awt.Font("Tahoma",0,10)); gdKesimp.setVerticalAlignment(SwingConstants.TOP); gdKesimp.setBorder(gdBorder); gdKesimp.setOpaque(true); gdKesimp.setBackground(Color.WHITE);
        gdKesimp.setBounds(gdX, y, gdColNo+gdColQ+gdColA, 55); FormInput.add(gdKesimp);
        y += 55;

        // === SECTION 9: SKRINING GIZI ANAK / BAYI ===
        y += 35;
        JLabel lblSec9 = new JLabel("IX. SKRINING PASIEN ANAK ATAU BAYI");
        lblSec9.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec9.setBounds(10, y, 400, 20); FormInput.add(lblSec9);

        final int gaX = 10, gaColNo = 50, gaColQ = 420, gaColA = 230, gaHdrH = 25;
        String[] gaQuestions = {
            "<html><div style='padding:4px;'>Apakah ada penyakit yang dapat menyebabkan malnutrisi atau pengobatan yang mengharuskan adanya operasi bedah?</div></html>",
            "<html><div style='padding:4px;'>Apakah pasien memiliki status gizi yang buruk berdasarkan penilaian klinis?</div></html>",
            "<html><div style='padding:4px;'>Apakah pasien mengalami (salah satu atau lebih dari):"
                + "<ul style='margin-top:0;margin-bottom:0;margin-left:15px;padding-left:15px;'>"
                + "<li>Diare berlebihan&gt;5 x sehari dan/atau muntah&gt;3 x sehari?</li>"
                + "<li>Penurunan asupan makanan selama 3 hari terakhir?</li>"
                + "<li>Pernah mendapat intervensi gizi sebelumnya?</li>"
                + "<li>Asupan makanan kurang yang disebabkan oleh rasa sakit?</li>"
                + "</ul></div></html>",
            "<html><div style='padding:4px;'>Apakah ada penurunan berat badan atau tidak ada kenaikan berat badan (bayi&lt;1 tahun) selama 1 minggu terakhir?</div></html>"
        };
        int[] gaRowH = {50, 50, 110, 50};
        String[] gaYaScore = {"Ya = 2", "Ya = 1", "Ya = 1", "Ya = 1"};
        javax.swing.border.Border gaBorder = BorderFactory.createLineBorder(Color.BLACK);

        y += 25;
        JLabel gaH1 = new JLabel("No.", SwingConstants.CENTER); gaH1.setFont(new java.awt.Font("Tahoma",1,10)); gaH1.setBorder(gaBorder); gaH1.setOpaque(true); gaH1.setBackground(Color.WHITE); gaH1.setBounds(gaX, y, gaColNo, gaHdrH); FormInput.add(gaH1);
        JLabel gaH2 = new JLabel("Pertanyaan", SwingConstants.CENTER); gaH2.setFont(new java.awt.Font("Tahoma",1,10)); gaH2.setBorder(gaBorder); gaH2.setOpaque(true); gaH2.setBackground(Color.WHITE); gaH2.setBounds(gaX+gaColNo, y, gaColQ, gaHdrH); FormInput.add(gaH2);
        JLabel gaH3 = new JLabel("Jawaban (Skor)", SwingConstants.CENTER); gaH3.setFont(new java.awt.Font("Tahoma",1,10)); gaH3.setBorder(gaBorder); gaH3.setOpaque(true); gaH3.setBackground(Color.WHITE); gaH3.setBounds(gaX+gaColNo+gaColQ, y, gaColA, gaHdrH); FormInput.add(gaH3);
        y += gaHdrH;

        ComboBox[] gaCombos = new ComboBox[4];
        for (int i = 0; i < 4; i++) {
            int rowH = gaRowH[i];
            JLabel noCell = new JLabel((i+1)+".", SwingConstants.CENTER); noCell.setFont(new java.awt.Font("Tahoma",0,10)); noCell.setBorder(gaBorder); noCell.setOpaque(true); noCell.setBackground(Color.WHITE); noCell.setBounds(gaX, y, gaColNo, rowH); FormInput.add(noCell);
            JLabel qCell = new JLabel(gaQuestions[i]); qCell.setFont(new java.awt.Font("Tahoma",0,10)); qCell.setVerticalAlignment(SwingConstants.CENTER); qCell.setBorder(gaBorder); qCell.setOpaque(true); qCell.setBackground(Color.WHITE); qCell.setBounds(gaX+gaColNo, y, gaColQ, rowH); FormInput.add(qCell);
            JPanel aCell = new JPanel(null); aCell.setBackground(Color.WHITE); aCell.setBorder(gaBorder); aCell.setBounds(gaX+gaColNo+gaColQ, y, gaColA, rowH); FormInput.add(aCell);
            int optY = (rowH - 40) / 2;
            JLabel opt1 = new JLabel("a.  " + gaYaScore[i]); opt1.setFont(new java.awt.Font("Tahoma",0,10)); opt1.setBounds(8, optY, 80, 16); aCell.add(opt1);
            JLabel opt2 = new JLabel("b.  Tidak = 0"); opt2.setFont(new java.awt.Font("Tahoma",0,10)); opt2.setBounds(8, optY+20, 80, 16); aCell.add(opt2);
            gaCombos[i] = new ComboBox();
            gaCombos[i].setModel(new DefaultComboBoxModel(new String[]{"Tidak","Ya"}));
            gaCombos[i].setBounds(gaColA-90, optY+8, 80, 23); aCell.add(gaCombos[i]);
            gaCombos[i].addItemListener(e -> { if (e.getStateChange() == ItemEvent.SELECTED) hitungGiziAnak(); });
            y += rowH;
        }
        CmbGiziAnak1 = gaCombos[0]; CmbGiziAnak2 = gaCombos[1]; CmbGiziAnak3 = gaCombos[2]; CmbGiziAnak4 = gaCombos[3];

        JLabel gaTotLbl = new JLabel("TOTAL SKOR"); gaTotLbl.setFont(new java.awt.Font("Tahoma",1,10)); gaTotLbl.setBorder(gaBorder); gaTotLbl.setOpaque(true); gaTotLbl.setBackground(Color.WHITE); gaTotLbl.setBounds(gaX, y, gaColNo+gaColQ, gaHdrH);
        gaTotLbl.setHorizontalAlignment(SwingConstants.CENTER); FormInput.add(gaTotLbl);
        JPanel gaTotCell = new JPanel(null); gaTotCell.setBackground(Color.WHITE); gaTotCell.setBorder(gaBorder); gaTotCell.setBounds(gaX+gaColNo+gaColQ, y, gaColA, gaHdrH); FormInput.add(gaTotCell);
        TGiziAnakTotal = new TextBox(); TGiziAnakTotal.setBounds(8, 2, 60, 22); TGiziAnakTotal.setEditable(false); TGiziAnakTotal.setText("0"); gaTotCell.add(TGiziAnakTotal);
        TGiziAnakKesimpulan = new TextBox(); TGiziAnakKesimpulan.setBounds(75, 2, gaColA-85, 22); TGiziAnakKesimpulan.setEditable(false); TGiziAnakKesimpulan.setText("Risiko Rendah"); gaTotCell.add(TGiziAnakKesimpulan);
        y += gaHdrH;

        JLabel gaKesimp = new JLabel("<html><b>KESIMPULAN</b><br>"
            + "<span style='padding-left:15px;'>Skor 4-5 = Risiko Tinggi</span><br>"
            + "<span style='padding-left:15px;'>Skor 1-3 = Risiko Sedang</span><br>"
            + "<span style='padding-left:15px;'>Skor 0 = Risiko Rendah</span></html>");
        gaKesimp.setFont(new java.awt.Font("Tahoma",0,10)); gaKesimp.setVerticalAlignment(SwingConstants.TOP); gaKesimp.setBorder(gaBorder); gaKesimp.setOpaque(true); gaKesimp.setBackground(Color.WHITE);
        gaKesimp.setBounds(gaX, y, gaColNo+gaColQ+gaColA, 70); FormInput.add(gaKesimp);
        y += 70;

        // === SECTION 10: MASALAH KEPERAWATAN ===
        y += 35;
        JLabel lblSec10 = new JLabel("X. MASALAH KEPERAWATAN");
        lblSec10.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec10.setBounds(10, y, 300, 20); FormInput.add(lblSec10);

        y += 25;
        ChkMasalahNyeri = new CekBox(); ChkMasalahNyeri.setText("Nyeri"); ChkMasalahNyeri.setBounds(10, y, 120, 23); ChkMasalahNyeri.setSelected(false); ChkMasalahNyeri.setOpaque(false); FormInput.add(ChkMasalahNyeri);
        ChkMasalahJalanNafas = new CekBox(); ChkMasalahJalanNafas.setText("Jalan Nafas"); ChkMasalahJalanNafas.setBounds(135, y, 120, 23); ChkMasalahJalanNafas.setSelected(false); ChkMasalahJalanNafas.setOpaque(false); FormInput.add(ChkMasalahJalanNafas);
        ChkMasalahDefisitNutrisi = new CekBox(); ChkMasalahDefisitNutrisi.setText("Defisit Nutrisi"); ChkMasalahDefisitNutrisi.setBounds(260, y, 120, 23); ChkMasalahDefisitNutrisi.setSelected(false); ChkMasalahDefisitNutrisi.setOpaque(false); FormInput.add(ChkMasalahDefisitNutrisi);
        ChkMasalahKurangPengetahuan = new CekBox(); ChkMasalahKurangPengetahuan.setText("Kurang Pengetahuan"); ChkMasalahKurangPengetahuan.setBounds(385, y, 160, 23); ChkMasalahKurangPengetahuan.setSelected(false); ChkMasalahKurangPengetahuan.setOpaque(false); FormInput.add(ChkMasalahKurangPengetahuan);

        y += 25;
        ChkMasalahGangguanMobilitas = new CekBox(); ChkMasalahGangguanMobilitas.setText("Gangguan Mobilitas"); ChkMasalahGangguanMobilitas.setBounds(10, y, 160, 23); ChkMasalahGangguanMobilitas.setSelected(false); ChkMasalahGangguanMobilitas.setOpaque(false); FormInput.add(ChkMasalahGangguanMobilitas);
        ChkMasalahKecemasan = new CekBox(); ChkMasalahKecemasan.setText("Kecemasan"); ChkMasalahKecemasan.setBounds(175, y, 110, 23); ChkMasalahKecemasan.setSelected(false); ChkMasalahKecemasan.setOpaque(false); FormInput.add(ChkMasalahKecemasan);
        ChkMasalahIntegritasKulit = new CekBox(); ChkMasalahIntegritasKulit.setText("Integritas Kulit"); ChkMasalahIntegritasKulit.setBounds(290, y, 120, 23); ChkMasalahIntegritasKulit.setSelected(false); ChkMasalahIntegritasKulit.setOpaque(false); FormInput.add(ChkMasalahIntegritasKulit);
        ChkMasalahHipertermi = new CekBox(); ChkMasalahHipertermi.setText("Hipertermi"); ChkMasalahHipertermi.setBounds(415, y, 100, 23); ChkMasalahHipertermi.setSelected(false); ChkMasalahHipertermi.setOpaque(false); FormInput.add(ChkMasalahHipertermi);

        y += 27;
        Label lblMasalahLain = new Label(); lblMasalahLain.setText("Masalah Lainnya :"); lblMasalahLain.setBounds(10, y, 130, 23); FormInput.add(lblMasalahLain);
        TMasalahLainnya = new TextBox(); TMasalahLainnya.setBounds(145, y, 350, 23); FormInput.add(TMasalahLainnya);

        y += 27;
        Label lblTujuan = new Label(); lblTujuan.setText("Tujuan / Target :"); lblTujuan.setBounds(10, y, 130, 23); FormInput.add(lblTujuan);
        TTujuanTarget = new TextArea(); TTujuanTarget.setBounds(145, y, 400, 50); TTujuanTarget.setBorder(BorderFactory.createLineBorder(Color.BLACK)); FormInput.add(TTujuanTarget);

        // === PETUGAS ===
        y += 60;
        Label lblPetugas = new Label(); lblPetugas.setText("Petugas :"); lblPetugas.setBounds(10, y, 130, 23); FormInput.add(lblPetugas);
        KdPetugas = new TextBox(); KdPetugas.setBounds(145, y, 80, 23); FormInput.add(KdPetugas);
        NmPetugas = new TextBox(); NmPetugas.setBounds(227, y, 180, 23); FormInput.add(NmPetugas);
        BtnPetugas = new Button(); BtnPetugas.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPetugas.setBounds(409, y, 28, 23); FormInput.add(BtnPetugas);
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

        // Input toggle
        ScrollInput = new ScrollPane(); ScrollInput.setViewportView(FormInput);

        tabPane = new JTabbedPane();
        tabPane.addTab("Input Data", ScrollInput);
        tabPane.addTab("Data", Scroll);

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
        BtnCari.addActionListener(evt -> { tampil(); tabPane.setSelectedIndex(1); });
        BtnAll.addActionListener(evt -> { TCari.setText(""); tampil(); tabPane.setSelectedIndex(1); });
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) { if (evt.getKeyCode() == KeyEvent.VK_ENTER) { tampil(); tabPane.setSelectedIndex(1); } }
        });

        // 72 columns: no_rawat, tanggal, keluhan_utama, sumber_pengkajian, sumber_lain, hubungan_pasien,
        // riwayat_penyakit_sekarang, riwayat_alergi, riwayat_penyakit_dahulu, diagnosa_keperawatan, rencana_asuhan, tindakan,
        // td, suhu, pernafasan, nadi, bb, tb, kesadaran, nyeri, nyeri_lokasi, nyeri_score,
        // skrining_nyeri, skala_nyeri, lokasi_nyeri, durasi_nyeri, frekuensi_nyeri,
        // nyeri_hilang_obat, nyeri_hilang_musik, nyeri_hilang_istirahat, nyeri_hilang_posisi, nyeri_hilang_lainnya,
        // diberitahu_dokter, diberitahu_pukul,
        // jatuh_a, jatuh_b, jatuh_hasil, jatuh_diberitahu_dokter, jatuh_diberitahu_pukul,
        // status_fungsional, fungsional_bantuan, fungsional_dilaporkan_pukul,
        // status_ekonomi, status_ekonomi_lain,
        // status_pernikahan, pekerjaan, suku, agama, psiko, mental,
        // gizi_dewasa_1, gizi_dewasa_2, gizi_dewasa_3, gizi_dewasa_4, gizi_dewasa_5, gizi_dewasa_6, gizi_dewasa_total, gizi_dewasa_kesimpulan,
        // gizi_anak_1, gizi_anak_2, gizi_anak_3, gizi_anak_4, gizi_anak_total, gizi_anak_kesimpulan,
        // masalah_nyeri, masalah_jalan_nafas, masalah_defisit_nutrisi, masalah_kurang_pengetahuan,
        // masalah_gangguan_mobilitas, masalah_kecemasan, masalah_integritas_kulit, masalah_hipertermi,
        // masalah_lainnya, tujuan_target, nip
        // Total = 75 columns

        BtnSimpan.addActionListener(evt -> {
            if (TNoRw.getText().trim().equals("")) { Valid.textKosong(TNoRw, "Pasien");
            } else if (KdPetugas.getText().trim().equals("")) { Valid.textKosong(KdPetugas, "Petugas");
            } else {
                String tglS = Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19);
                if (Sequel.menyimpantf("asesmen_kep_rj_rscb",
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?",
                    "Data", 75, new String[]{
                    TNoRw.getText(), tglS,
                    TKeluhanUtama.getText(), CmbSumberPengkajian.getSelectedItem().toString(), TSumberLain.getText(), THubunganPasien.getText(),
                    TRiwayatPenyakitSekarang.getText(), TRiwayatAlergi.getText(), TRiwayatPenyakitDahulu.getText(),
                    TDiagnosaKeperawatan.getText(), TRencanaAsuhan.getText(), TTindakan.getText(),
                    TTD.getText(), TSuhu.getText(), TPernafasan.getText(), TNadi.getText(), TBB.getText(), TTB.getText(),
                    CmbKesadaran.getSelectedItem().toString(), CmbNyeri.getSelectedItem().toString(), TNyeriLokasi.getText(), TNyeriScore.getText(),
                    CmbSkriningNyeri.getSelectedItem().toString(), TSkalaNyeri.getText(), TLokasiNyeri.getText(), TDurasiNyeri.getText(), TFrekuensiNyeri.getText(),
                    ChkNyeriObat.isSelected() ? "Ya" : "Tidak",
                    ChkNyeriMusik.isSelected() ? "Ya" : "Tidak",
                    ChkNyeriIstirahat.isSelected() ? "Ya" : "Tidak",
                    ChkNyeriPosisi.isSelected() ? "Ya" : "Tidak",
                    TNyeriHilangLainnya.getText(),
                    ChkDiberitahuDokter.isSelected() ? "Ya" : "Tidak", TDiberitahuPukul.getText(),
                    CmbJatuhA.getSelectedItem().toString(), CmbJatuhB.getSelectedItem().toString(), TJatuhHasil.getText(),
                    ChkJatuhDiberitahuDokter.isSelected() ? "Ya" : "Tidak", TJatuhDiberitahuPukul.getText(),
                    CmbStatusFungsional.getSelectedItem().toString(), TFungsionalBantuan.getText(), TFungsionalDilaporkanPukul.getText(),
                    CmbStatusEkonomi.getSelectedItem().toString(), TStatusEkonomiLain.getText(),
                    CmbStatusPernikahan.getSelectedItem().toString(), CmbPekerjaan.getSelectedItem().toString(), TSuku.getText(),
                    CmbAgama.getSelectedItem().toString(), CmbPsiko.getSelectedItem().toString(), CmbMental.getSelectedItem().toString(),
                    "Ya".equals(CmbGiziDewasa1.getSelectedItem().toString()) ? "1" : "0",
                    "Ya".equals(CmbGiziDewasa2.getSelectedItem().toString()) ? "1" : "0",
                    "Ya".equals(CmbGiziDewasa3.getSelectedItem().toString()) ? "1" : "0",
                    "Ya".equals(CmbGiziDewasa4.getSelectedItem().toString()) ? "1" : "0",
                    "Ya".equals(CmbGiziDewasa5.getSelectedItem().toString()) ? "1" : "0",
                    "Ya".equals(CmbGiziDewasa6.getSelectedItem().toString()) ? "1" : "0",
                    TGiziDewasaTotal.getText(), TGiziDewasaKesimpulan.getText(),
                    "Ya".equals(CmbGiziAnak1.getSelectedItem().toString()) ? "2" : "0",
                    "Ya".equals(CmbGiziAnak2.getSelectedItem().toString()) ? "1" : "0",
                    "Ya".equals(CmbGiziAnak3.getSelectedItem().toString()) ? "1" : "0",
                    "Ya".equals(CmbGiziAnak4.getSelectedItem().toString()) ? "1" : "0",
                    TGiziAnakTotal.getText(), TGiziAnakKesimpulan.getText(),
                    ChkMasalahNyeri.isSelected() ? "Ya" : "Tidak",
                    ChkMasalahJalanNafas.isSelected() ? "Ya" : "Tidak",
                    ChkMasalahDefisitNutrisi.isSelected() ? "Ya" : "Tidak",
                    ChkMasalahKurangPengetahuan.isSelected() ? "Ya" : "Tidak",
                    ChkMasalahGangguanMobilitas.isSelected() ? "Ya" : "Tidak",
                    ChkMasalahKecemasan.isSelected() ? "Ya" : "Tidak",
                    ChkMasalahIntegritasKulit.isSelected() ? "Ya" : "Tidak",
                    ChkMasalahHipertermi.isSelected() ? "Ya" : "Tidak",
                    TMasalahLainnya.getText(), TTujuanTarget.getText(),
                    KdPetugas.getText()
                })) { tampil(); emptTeks(); }
            }
        });

        BtnHapus.addActionListener(evt -> {
            if (tbObat.getSelectedRow() != -1) {
                if (Sequel.queryu2tf("delete from asesmen_kep_rj_rscb where no_rawat=? and tanggal=?", 2, new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
                })) { tampil(); emptTeks(); }
            } else JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dihapus...!!!!");
        });

        BtnEdit.addActionListener(evt -> {
            if (TNoRw.getText().trim().equals("") || tbObat.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau diganti...!!!!");
                return;
            }
            if (Sequel.queryu2tf("delete from asesmen_kep_rj_rscb where no_rawat=? and tanggal=?", 2, new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
            })) { BtnSimpan.doClick(); }
        });

        BtnPrint.addActionListener(evt -> {
            if (TNoRw.getText().trim().equals("")) { JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dicetak...!!!!"); return; }
            Map<String,Object> p = new HashMap<>();
            p.put("namars", akses.getnamars()); p.put("alamatrs", akses.getalamatrs());
            p.put("propinsirs", akses.getpropinsirs()); p.put("kabupatenrs", akses.getkabupatenrs());
            p.put("kontakrs", akses.getkontakrs()); p.put("emailrs", akses.getemailrs());
            p.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            String tglS = Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19);
            Valid.MyReportqry("rptAsesmenKeperawatanRJRscb.jasper","report","::[ Asesmen Keperawatan Pasien Rawat Jalan ]::",
                "select a.*,r.no_rkm_medis,pa.nm_pasien,pa.jk,concat(r.umurdaftar,' ',r.sttsumur) as umur,pt.nama as nm_petugas "+
                "from asesmen_kep_rj_rscb a inner join reg_periksa r on a.no_rawat=r.no_rawat "+
                "inner join pasien pa on r.no_rkm_medis=pa.no_rkm_medis left join petugas pt on a.nip=pt.nip "+
                "where a.no_rawat='"+TNoRw.getText()+"' and a.tanggal='"+tglS+"'",p);
        });

        // Layout assembly
        internalFrame1.add(panelAtas, BorderLayout.PAGE_START); internalFrame1.add(tabPane, BorderLayout.CENTER); internalFrame1.add(panelBawah, BorderLayout.PAGE_END);
        setLayout(new BorderLayout()); add(internalFrame1, BorderLayout.CENTER); setDefaultCloseOperation(DISPOSE_ON_CLOSE); setUndecorated(true);
    }

    private void hitungJatuh() {
        boolean a = "Ya".equals(CmbJatuhA.getSelectedItem().toString());
        boolean b = "Ya".equals(CmbJatuhB.getSelectedItem().toString());
        if (a && b) {
            TJatuhHasil.setText("Risiko Tinggi");
        } else if (a || b) {
            TJatuhHasil.setText("Risiko Rendah");
        } else {
            TJatuhHasil.setText("Tidak Berisiko");
        }
    }

    private void hitungGiziDewasa() {
        int total = 0;
        if ("Ya".equals(CmbGiziDewasa1.getSelectedItem().toString())) total++;
        if ("Ya".equals(CmbGiziDewasa2.getSelectedItem().toString())) total++;
        if ("Ya".equals(CmbGiziDewasa3.getSelectedItem().toString())) total++;
        if ("Ya".equals(CmbGiziDewasa4.getSelectedItem().toString())) total++;
        if ("Ya".equals(CmbGiziDewasa5.getSelectedItem().toString())) total++;
        if ("Ya".equals(CmbGiziDewasa6.getSelectedItem().toString())) total++;
        TGiziDewasaTotal.setText(String.valueOf(total));
        TGiziDewasaKesimpulan.setText(total >= 3 ? "Berisiko Malnutrisi" : "Tidak Berisiko");
    }

    private void hitungGiziAnak() {
        int total = 0;
        if ("Ya".equals(CmbGiziAnak1.getSelectedItem().toString())) total += 2;
        if ("Ya".equals(CmbGiziAnak2.getSelectedItem().toString())) total += 1;
        if ("Ya".equals(CmbGiziAnak3.getSelectedItem().toString())) total += 1;
        if ("Ya".equals(CmbGiziAnak4.getSelectedItem().toString())) total += 1;
        TGiziAnakTotal.setText(String.valueOf(total));
        if (total == 0) {
            TGiziAnakKesimpulan.setText("Risiko Rendah");
        } else if (total <= 3) {
            TGiziAnakKesimpulan.setText("Risiko Sedang");
        } else {
            TGiziAnakKesimpulan.setText("Risiko Tinggi");
        }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select a.no_rawat,r.no_rkm_medis,pa.nm_pasien,a.tanggal,a.keluhan_utama,a.kesadaran,pt.nama as nm_petugas "+
                "from asesmen_kep_rj_rscb a inner join reg_periksa r on a.no_rawat=r.no_rawat "+
                "inner join pasien pa on r.no_rkm_medis=pa.no_rkm_medis left join petugas pt on a.nip=pt.nip "+
                "where a.tanggal between ? and ? and (a.no_rawat like ? or r.no_rkm_medis like ? or pa.nm_pasien like ?) order by a.tanggal desc");
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
            ps.setString(3, "%"+TCari.getText().trim()+"%");
            ps.setString(4, "%"+TCari.getText().trim()+"%");
            ps.setString(5, "%"+TCari.getText().trim()+"%");
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new String[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7)});
            }
            LCount.setText("Record : "+tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif : "+e);
        } finally { try { if (rs!=null) rs.close(); if (ps!=null) ps.close(); } catch (Exception e) {} }
    }

    public void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            try { TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString())); } catch (Exception e) {}
            try {
                ps = koneksi.prepareStatement("select a.*,pt.nama as nm_petugas from asesmen_kep_rj_rscb a left join petugas pt on a.nip=pt.nip where a.no_rawat=? and a.tanggal=?");
                ps.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                ps.setString(2, tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TKeluhanUtama.setText(rs.getString("keluhan_utama"));
                    CmbSumberPengkajian.setSelectedItem(rs.getString("sumber_pengkajian"));
                    TSumberLain.setText(rs.getString("sumber_lain"));
                    THubunganPasien.setText(rs.getString("hubungan_pasien"));
                    TRiwayatPenyakitSekarang.setText(rs.getString("riwayat_penyakit_sekarang"));
                    TRiwayatAlergi.setText(rs.getString("riwayat_alergi"));
                    TRiwayatPenyakitDahulu.setText(rs.getString("riwayat_penyakit_dahulu"));
                    TDiagnosaKeperawatan.setText(rs.getString("diagnosa_keperawatan"));
                    TRencanaAsuhan.setText(rs.getString("rencana_asuhan"));
                    TTindakan.setText(rs.getString("tindakan"));
                    TTD.setText(rs.getString("td"));
                    TSuhu.setText(rs.getString("suhu"));
                    TPernafasan.setText(rs.getString("pernafasan"));
                    TNadi.setText(rs.getString("nadi"));
                    TBB.setText(rs.getString("bb"));
                    TTB.setText(rs.getString("tb"));
                    CmbKesadaran.setSelectedItem(rs.getString("kesadaran"));
                    CmbNyeri.setSelectedItem(rs.getString("nyeri"));
                    TNyeriLokasi.setText(rs.getString("nyeri_lokasi"));
                    TNyeriScore.setText(rs.getString("nyeri_score"));
                    CmbSkriningNyeri.setSelectedItem(rs.getString("skrining_nyeri"));
                    TSkalaNyeri.setText(rs.getString("skala_nyeri"));
                    TLokasiNyeri.setText(rs.getString("lokasi_nyeri"));
                    TDurasiNyeri.setText(rs.getString("durasi_nyeri"));
                    TFrekuensiNyeri.setText(rs.getString("frekuensi_nyeri"));
                    ChkNyeriObat.setSelected("Ya".equals(rs.getString("nyeri_hilang_obat")));
                    ChkNyeriMusik.setSelected("Ya".equals(rs.getString("nyeri_hilang_musik")));
                    ChkNyeriIstirahat.setSelected("Ya".equals(rs.getString("nyeri_hilang_istirahat")));
                    ChkNyeriPosisi.setSelected("Ya".equals(rs.getString("nyeri_hilang_posisi")));
                    TNyeriHilangLainnya.setText(rs.getString("nyeri_hilang_lainnya"));
                    ChkDiberitahuDokter.setSelected("Ya".equals(rs.getString("diberitahu_dokter")));
                    TDiberitahuPukul.setText(rs.getString("diberitahu_pukul"));
                    CmbJatuhA.setSelectedItem(rs.getString("jatuh_a"));
                    CmbJatuhB.setSelectedItem(rs.getString("jatuh_b"));
                    TJatuhHasil.setText(rs.getString("jatuh_hasil"));
                    ChkJatuhDiberitahuDokter.setSelected("Ya".equals(rs.getString("jatuh_diberitahu_dokter")));
                    TJatuhDiberitahuPukul.setText(rs.getString("jatuh_diberitahu_pukul"));
                    CmbStatusFungsional.setSelectedItem(rs.getString("status_fungsional"));
                    TFungsionalBantuan.setText(rs.getString("fungsional_bantuan"));
                    TFungsionalDilaporkanPukul.setText(rs.getString("fungsional_dilaporkan_pukul"));
                    CmbStatusEkonomi.setSelectedItem(rs.getString("status_ekonomi"));
                    TStatusEkonomiLain.setText(rs.getString("status_ekonomi_lain"));
                    CmbStatusPernikahan.setSelectedItem(rs.getString("status_pernikahan"));
                    CmbPekerjaan.setSelectedItem(rs.getString("pekerjaan"));
                    TSuku.setText(rs.getString("suku"));
                    CmbAgama.setSelectedItem(rs.getString("agama"));
                    CmbPsiko.setSelectedItem(rs.getString("psiko"));
                    CmbMental.setSelectedItem(rs.getString("mental"));
                    CmbGiziDewasa1.setSelectedItem(rs.getInt("gizi_dewasa_1") == 1 ? "Ya" : "Tidak");
                    CmbGiziDewasa2.setSelectedItem(rs.getInt("gizi_dewasa_2") == 1 ? "Ya" : "Tidak");
                    CmbGiziDewasa3.setSelectedItem(rs.getInt("gizi_dewasa_3") == 1 ? "Ya" : "Tidak");
                    CmbGiziDewasa4.setSelectedItem(rs.getInt("gizi_dewasa_4") == 1 ? "Ya" : "Tidak");
                    CmbGiziDewasa5.setSelectedItem(rs.getInt("gizi_dewasa_5") == 1 ? "Ya" : "Tidak");
                    CmbGiziDewasa6.setSelectedItem(rs.getInt("gizi_dewasa_6") == 1 ? "Ya" : "Tidak");
                    TGiziDewasaTotal.setText(rs.getString("gizi_dewasa_total"));
                    TGiziDewasaKesimpulan.setText(rs.getString("gizi_dewasa_kesimpulan"));
                    CmbGiziAnak1.setSelectedItem(rs.getInt("gizi_anak_1") >= 1 ? "Ya" : "Tidak");
                    CmbGiziAnak2.setSelectedItem(rs.getInt("gizi_anak_2") == 1 ? "Ya" : "Tidak");
                    CmbGiziAnak3.setSelectedItem(rs.getInt("gizi_anak_3") == 1 ? "Ya" : "Tidak");
                    CmbGiziAnak4.setSelectedItem(rs.getInt("gizi_anak_4") == 1 ? "Ya" : "Tidak");
                    TGiziAnakTotal.setText(rs.getString("gizi_anak_total"));
                    TGiziAnakKesimpulan.setText(rs.getString("gizi_anak_kesimpulan"));
                    ChkMasalahNyeri.setSelected("Ya".equals(rs.getString("masalah_nyeri")));
                    ChkMasalahJalanNafas.setSelected("Ya".equals(rs.getString("masalah_jalan_nafas")));
                    ChkMasalahDefisitNutrisi.setSelected("Ya".equals(rs.getString("masalah_defisit_nutrisi")));
                    ChkMasalahKurangPengetahuan.setSelected("Ya".equals(rs.getString("masalah_kurang_pengetahuan")));
                    ChkMasalahGangguanMobilitas.setSelected("Ya".equals(rs.getString("masalah_gangguan_mobilitas")));
                    ChkMasalahKecemasan.setSelected("Ya".equals(rs.getString("masalah_kecemasan")));
                    ChkMasalahIntegritasKulit.setSelected("Ya".equals(rs.getString("masalah_integritas_kulit")));
                    ChkMasalahHipertermi.setSelected("Ya".equals(rs.getString("masalah_hipertermi")));
                    TMasalahLainnya.setText(rs.getString("masalah_lainnya"));
                    TTujuanTarget.setText(rs.getString("tujuan_target"));
                    KdPetugas.setText(rs.getString("nip"));
                    NmPetugas.setText(rs.getString("nm_petugas"));
                }
            } catch (Exception e) { System.out.println("Notif getData : "+e);
            } finally { try { if (rs!=null) rs.close(); if (ps!=null) ps.close(); } catch (Exception e) {} }
        }
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date());
        TKeluhanUtama.setText(""); CmbSumberPengkajian.setSelectedIndex(0); TSumberLain.setText(""); TSumberLain.setEnabled(false);
        THubunganPasien.setText(""); TRiwayatPenyakitSekarang.setText(""); TRiwayatAlergi.setText(""); TRiwayatPenyakitDahulu.setText("");
        TDiagnosaKeperawatan.setText(""); TRencanaAsuhan.setText(""); TTindakan.setText("");
        TTD.setText(""); TSuhu.setText(""); TPernafasan.setText(""); TNadi.setText(""); TBB.setText(""); TTB.setText("");
        CmbKesadaran.setSelectedIndex(0); CmbNyeri.setSelectedIndex(0); TNyeriLokasi.setText(""); TNyeriScore.setText("");
        CmbSkriningNyeri.setSelectedIndex(0); TSkalaNyeri.setText(""); TLokasiNyeri.setText(""); TDurasiNyeri.setText(""); TFrekuensiNyeri.setText("");
        ChkNyeriObat.setSelected(false); ChkNyeriMusik.setSelected(false); ChkNyeriIstirahat.setSelected(false); ChkNyeriPosisi.setSelected(false);
        TNyeriHilangLainnya.setText(""); ChkDiberitahuDokter.setSelected(false); TDiberitahuPukul.setText("");
        CmbJatuhA.setSelectedIndex(1); CmbJatuhB.setSelectedIndex(1); TJatuhHasil.setText("Tidak Berisiko");
        ChkJatuhDiberitahuDokter.setSelected(false); TJatuhDiberitahuPukul.setText("");
        CmbStatusFungsional.setSelectedIndex(0); TFungsionalBantuan.setText(""); TFungsionalBantuan.setEnabled(false); TFungsionalDilaporkanPukul.setText("");
        CmbStatusEkonomi.setSelectedIndex(0); TStatusEkonomiLain.setText(""); TStatusEkonomiLain.setEnabled(false);
        CmbStatusPernikahan.setSelectedIndex(0); CmbPekerjaan.setSelectedIndex(0); TSuku.setText(""); CmbAgama.setSelectedIndex(0); CmbPsiko.setSelectedIndex(0); CmbMental.setSelectedIndex(0);
        CmbGiziDewasa1.setSelectedIndex(0); CmbGiziDewasa2.setSelectedIndex(0); CmbGiziDewasa3.setSelectedIndex(0);
        CmbGiziDewasa4.setSelectedIndex(0); CmbGiziDewasa5.setSelectedIndex(0); CmbGiziDewasa6.setSelectedIndex(0);
        TGiziDewasaTotal.setText("0"); TGiziDewasaKesimpulan.setText("Tidak Berisiko");
        CmbGiziAnak1.setSelectedIndex(0); CmbGiziAnak2.setSelectedIndex(0); CmbGiziAnak3.setSelectedIndex(0); CmbGiziAnak4.setSelectedIndex(0);
        TGiziAnakTotal.setText("0"); TGiziAnakKesimpulan.setText("Risiko Rendah");
        ChkMasalahNyeri.setSelected(false); ChkMasalahJalanNafas.setSelected(false); ChkMasalahDefisitNutrisi.setSelected(false);
        ChkMasalahKurangPengetahuan.setSelected(false); ChkMasalahGangguanMobilitas.setSelected(false); ChkMasalahKecemasan.setSelected(false);
        ChkMasalahIntegritasKulit.setSelected(false); ChkMasalahHipertermi.setSelected(false);
        TMasalahLainnya.setText(""); TTujuanTarget.setText("");
        KdPetugas.setText(""); NmPetugas.setText("");
    }

    public void setNoRm(String n, String m, String p) { TNoRw.setText(n); TNoRM.setText(m); TPasien.setText(p); }

    public void setNoRm(String n, Date t) {
        TNoRw.setText(n); TglAsuhan.setDate(t);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", n));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getrujukan_keluar()); BtnHapus.setEnabled(akses.getrujukan_keluar());
        BtnEdit.setEnabled(akses.getrujukan_keluar()); BtnPrint.setEnabled(akses.getrujukan_keluar());
    }
}
