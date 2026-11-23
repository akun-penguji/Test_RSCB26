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
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import widget.*;

public class RMAsesmenPraAnestesiRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private Label LCount;

    private TextBox TRuangan, TDiagnosa, TTindakanBedah, TBB, TTB;
    private TextBox TAnamnesi, TRiwayatOperasi, TJenisAnestesiRiwayat, TKomplikasiRiwayat;
    private CekBox ChkAsma, ChkAlergi, ChkDiabetes, ChkHipertensi;
    private TextBox TPemeriksaanFisik, TKeadaanUmum, TGCS, TTD, TNadi, TRR, TSuhu;
    private ComboBox CmbGerakanLeher, CmbMethohyoid, CmbMallampati;
    private TextBox TThorax, TAbdomen, TEkstremitas, TLaboratorium, TEKG, TRoThorax, TLainLain;
    private ComboBox CmbASA;
    private TextBox TPremedikasi, TPuasa, TPenyulit;
    private CekBox ChkGaLma, ChkGaEtt, ChkGaTiva, ChkSedasiRingan, ChkSedasiSedang, ChkSedasiDalam;
    private CekBox ChkSpinal, ChkEpidural, ChkKaudal, ChkBlokPerifer;
    private TextBox TPersediaanDarah;
    private CekBox ChkHipotensi, ChkVentilasi;
    private TextBox TTeknikKhususLain;
    private CekBox ChkMonEkg, ChkMonNadi, ChkMonSpo2, ChkMonNibp, ChkMonTemp, ChkMonCvp, ChkMonEtco2;
    private TextBox TMonArteri, TMonLain;
    private ComboBox CmbPerawatanPasca;
    private TextBox KdDokter, NmDokter;
    private Button BtnDokter;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariDokter dokterDlg = new DlgCariDokter(null, false);

    private widget.Table tbObat;
    private DefaultTableModel tabMode;
    private ScrollPane Scroll;
    private CekBox ChkInput;
    private PanelBiasa FormInput;
    private ScrollPane ScrollInput;
    private JPanel PanelInput;
    public widget.editorpane LoadHTML;

    public RMAsesmenPraAnestesiRscb(java.awt.Frame parent, boolean modal) {
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
            "::[ Asesmen Pra Anestesi ]::",
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
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","Ruangan","Diagnosa","ASA","Dokter Anestesi"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbObat.setModel(tabMode);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { if (tabMode.getRowCount() != 0) getData(); }
        });
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem mnCetak = new JMenuItem("Cetak Asesmen Pra Anestesi");
        mnCetak.setIcon(new ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetak.addActionListener(evt -> BtnPrint.doClick());
        popupMenu.add(mnCetak);
        tbObat.setComponentPopupMenu(popupMenu);
        Scroll.setViewportView(tbObat);

        // FORM INPUT
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(920, 1400));
        FormInput.setLayout(null);

        int y = 10;
        JLabel lblHeader = new JLabel("ASESMEN PRA ANESTESI");
        lblHeader.setFont(new java.awt.Font("Tahoma", 1, 13)); lblHeader.setBounds(10, y, 400, 20); FormInput.add(lblHeader);

        // === SECTION 1: DATA UMUM ===
        y += 30;
        JLabel lblSec1 = new JLabel("DATA UMUM");
        lblSec1.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec1.setBounds(10, y, 200, 20); FormInput.add(lblSec1);

        y += 25;
        Label lblTgl = new Label(); lblTgl.setText("Tanggal :"); lblTgl.setBounds(10, y, 120, 23); FormInput.add(lblTgl);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setBounds(135, y, 130, 23); FormInput.add(TglAsuhan);

        y += 27;
        Label lblRuangan = new Label(); lblRuangan.setText("Ruangan :"); lblRuangan.setBounds(10, y, 120, 23); FormInput.add(lblRuangan);
        TRuangan = new TextBox(); TRuangan.setBounds(135, y, 200, 23); TRuangan.setEditable(false); FormInput.add(TRuangan);

        y += 27;
        Label lblDiagnosa = new Label(); lblDiagnosa.setText("Diagnosa :"); lblDiagnosa.setBounds(10, y, 120, 23); FormInput.add(lblDiagnosa);
        TDiagnosa = new TextBox(); TDiagnosa.setBounds(135, y, 400, 23); FormInput.add(TDiagnosa);

        y += 27;
        Label lblTindakan = new Label(); lblTindakan.setText("Tindakan Bedah :"); lblTindakan.setBounds(10, y, 120, 23); FormInput.add(lblTindakan);
        TTindakanBedah = new TextBox(); TTindakanBedah.setBounds(135, y, 400, 23); TTindakanBedah.setEditable(false); FormInput.add(TTindakanBedah);

        y += 27;
        Label lblBB = new Label(); lblBB.setText("BB (kg) :"); lblBB.setBounds(10, y, 120, 23); FormInput.add(lblBB);
        TBB = new TextBox(); TBB.setBounds(135, y, 80, 23); FormInput.add(TBB);
        Label lblTB = new Label(); lblTB.setText("TB (cm) :"); lblTB.setBounds(230, y, 60, 23); FormInput.add(lblTB);
        TTB = new TextBox(); TTB.setBounds(295, y, 80, 23); FormInput.add(TTB);

        // === SECTION 2: ANAMNESIS ===
        y += 35;
        JLabel lblSec2 = new JLabel("ANAMNESIS");
        lblSec2.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec2.setBounds(10, y, 200, 20); FormInput.add(lblSec2);

        y += 25;
        Label lblAnamnesi = new Label(); lblAnamnesi.setText("Anamnesi :"); lblAnamnesi.setBounds(10, y, 120, 23); FormInput.add(lblAnamnesi);
        TAnamnesi = new TextBox(); TAnamnesi.setBounds(135, y, 400, 23); FormInput.add(TAnamnesi);

        y += 27;
        Label lblRiwPenyakit = new Label(); lblRiwPenyakit.setText("Riwayat Penyakit :"); lblRiwPenyakit.setBounds(10, y, 120, 23); FormInput.add(lblRiwPenyakit);
        ChkAsma = new CekBox(); ChkAsma.setText("Asma"); ChkAsma.setBounds(135, y, 70, 23); ChkAsma.setSelected(false); ChkAsma.setOpaque(false); FormInput.add(ChkAsma);
        ChkAlergi = new CekBox(); ChkAlergi.setText("Alergi"); ChkAlergi.setBounds(210, y, 75, 23); ChkAlergi.setSelected(false); ChkAlergi.setOpaque(false); FormInput.add(ChkAlergi);
        ChkDiabetes = new CekBox(); ChkDiabetes.setText("Diabetes"); ChkDiabetes.setBounds(290, y, 85, 23); ChkDiabetes.setSelected(false); ChkDiabetes.setOpaque(false); FormInput.add(ChkDiabetes);
        ChkHipertensi = new CekBox(); ChkHipertensi.setText("Hipertensi"); ChkHipertensi.setBounds(380, y, 95, 23); ChkHipertensi.setSelected(false); ChkHipertensi.setOpaque(false); FormInput.add(ChkHipertensi);

        y += 27;
        Label lblRiwOperasi = new Label(); lblRiwOperasi.setText("Riwayat Operasi :"); lblRiwOperasi.setBounds(10, y, 120, 23); FormInput.add(lblRiwOperasi);
        TRiwayatOperasi = new TextBox(); TRiwayatOperasi.setBounds(135, y, 400, 23); FormInput.add(TRiwayatOperasi);

        y += 27;
        Label lblJenisAnest = new Label(); lblJenisAnest.setText("Jenis Anestesi Riw. :"); lblJenisAnest.setBounds(10, y, 120, 23); FormInput.add(lblJenisAnest);
        TJenisAnestesiRiwayat = new TextBox(); TJenisAnestesiRiwayat.setBounds(135, y, 300, 23); FormInput.add(TJenisAnestesiRiwayat);

        y += 27;
        Label lblKomplikasiRiw = new Label(); lblKomplikasiRiw.setText("Komplikasi Riw. :"); lblKomplikasiRiw.setBounds(10, y, 120, 23); FormInput.add(lblKomplikasiRiw);
        TKomplikasiRiwayat = new TextBox(); TKomplikasiRiwayat.setBounds(135, y, 400, 23); FormInput.add(TKomplikasiRiwayat);

        // === SECTION 3: PEMERIKSAAN FISIK ===
        y += 35;
        JLabel lblSec3 = new JLabel("PEMERIKSAAN FISIK");
        lblSec3.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec3.setBounds(10, y, 200, 20); FormInput.add(lblSec3);

        y += 25;
        Label lblPemFisik = new Label(); lblPemFisik.setText("Pemeriksaan Fisik :"); lblPemFisik.setBounds(10, y, 120, 23); FormInput.add(lblPemFisik);
        TPemeriksaanFisik = new TextBox(); TPemeriksaanFisik.setBounds(135, y, 400, 23); FormInput.add(TPemeriksaanFisik);

        y += 27;
        Label lblKU = new Label(); lblKU.setText("Keadaan Umum :"); lblKU.setBounds(10, y, 120, 23); FormInput.add(lblKU);
        TKeadaanUmum = new TextBox(); TKeadaanUmum.setBounds(135, y, 400, 23); FormInput.add(TKeadaanUmum);

        y += 27;
        Label lblGCS = new Label(); lblGCS.setText("GCS :"); lblGCS.setBounds(10, y, 120, 23); FormInput.add(lblGCS);
        TGCS = new TextBox(); TGCS.setBounds(135, y, 100, 23); FormInput.add(TGCS);

        y += 27;
        Label lblTD = new Label(); lblTD.setText("TD (mmHg) :"); lblTD.setBounds(10, y, 120, 23); FormInput.add(lblTD);
        TTD = new TextBox(); TTD.setBounds(135, y, 100, 23); FormInput.add(TTD);
        Label lblNadi = new Label(); lblNadi.setText("Nadi :"); lblNadi.setBounds(250, y, 50, 23); FormInput.add(lblNadi);
        TNadi = new TextBox(); TNadi.setBounds(305, y, 80, 23); FormInput.add(TNadi);

        y += 27;
        Label lblRR = new Label(); lblRR.setText("RR (x/mnt) :"); lblRR.setBounds(10, y, 120, 23); FormInput.add(lblRR);
        TRR = new TextBox(); TRR.setBounds(135, y, 80, 23); FormInput.add(TRR);
        Label lblSuhu = new Label(); lblSuhu.setText("Suhu :"); lblSuhu.setBounds(230, y, 50, 23); FormInput.add(lblSuhu);
        TSuhu = new TextBox(); TSuhu.setBounds(285, y, 80, 23); FormInput.add(TSuhu);

        y += 27;
        Label lblLeher = new Label(); lblLeher.setText("Gerakan Leher :"); lblLeher.setBounds(10, y, 120, 23); FormInput.add(lblLeher);
        CmbGerakanLeher = new ComboBox();
        CmbGerakanLeher.setModel(new DefaultComboBoxModel(new String[]{"Bebas","Terbatas"}));
        CmbGerakanLeher.setBounds(135, y, 120, 23); FormInput.add(CmbGerakanLeher);

        y += 27;
        Label lblMetho = new Label(); lblMetho.setText("Methohyoid :"); lblMetho.setBounds(10, y, 120, 23); FormInput.add(lblMetho);
        CmbMethohyoid = new ComboBox();
        CmbMethohyoid.setModel(new DefaultComboBoxModel(new String[]{">3cm","<3cm"}));
        CmbMethohyoid.setBounds(135, y, 120, 23); FormInput.add(CmbMethohyoid);

        y += 27;
        Label lblMallam = new Label(); lblMallam.setText("Mallampati :"); lblMallam.setBounds(10, y, 120, 23); FormInput.add(lblMallam);
        CmbMallampati = new ComboBox();
        CmbMallampati.setModel(new DefaultComboBoxModel(new String[]{"I","II","III","IV"}));
        CmbMallampati.setBounds(135, y, 80, 23); FormInput.add(CmbMallampati);

        // === SECTION 4: PENUNJANG ===
        y += 35;
        JLabel lblSec4 = new JLabel("PENUNJANG");
        lblSec4.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec4.setBounds(10, y, 200, 20); FormInput.add(lblSec4);

        y += 25;
        Label lblThorax = new Label(); lblThorax.setText("Thorax :"); lblThorax.setBounds(10, y, 120, 23); FormInput.add(lblThorax);
        TThorax = new TextBox(); TThorax.setBounds(135, y, 400, 23); FormInput.add(TThorax);

        y += 27;
        Label lblAbdomen = new Label(); lblAbdomen.setText("Abdomen :"); lblAbdomen.setBounds(10, y, 120, 23); FormInput.add(lblAbdomen);
        TAbdomen = new TextBox(); TAbdomen.setBounds(135, y, 400, 23); FormInput.add(TAbdomen);

        y += 27;
        Label lblEkstremitas = new Label(); lblEkstremitas.setText("Ekstremitas :"); lblEkstremitas.setBounds(10, y, 120, 23); FormInput.add(lblEkstremitas);
        TEkstremitas = new TextBox(); TEkstremitas.setBounds(135, y, 400, 23); FormInput.add(TEkstremitas);

        y += 27;
        Label lblLab = new Label(); lblLab.setText("Laboratorium :"); lblLab.setBounds(10, y, 120, 23); FormInput.add(lblLab);
        TLaboratorium = new TextBox(); TLaboratorium.setBounds(135, y, 400, 23); FormInput.add(TLaboratorium);

        y += 27;
        Label lblEKG = new Label(); lblEKG.setText("EKG :"); lblEKG.setBounds(10, y, 120, 23); FormInput.add(lblEKG);
        TEKG = new TextBox(); TEKG.setBounds(135, y, 400, 23); FormInput.add(TEKG);

        y += 27;
        Label lblRoThorax = new Label(); lblRoThorax.setText("Ro. Thorax :"); lblRoThorax.setBounds(10, y, 120, 23); FormInput.add(lblRoThorax);
        TRoThorax = new TextBox(); TRoThorax.setBounds(135, y, 400, 23); FormInput.add(TRoThorax);

        y += 27;
        Label lblLainLain = new Label(); lblLainLain.setText("Lain-lain :"); lblLainLain.setBounds(10, y, 120, 23); FormInput.add(lblLainLain);
        TLainLain = new TextBox(); TLainLain.setBounds(135, y, 400, 23); FormInput.add(TLainLain);

        // === SECTION 5: RENCANA ANESTESI ===
        y += 35;
        JLabel lblSec5 = new JLabel("RENCANA ANESTESI");
        lblSec5.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec5.setBounds(10, y, 200, 20); FormInput.add(lblSec5);

        y += 25;
        Label lblASA = new Label(); lblASA.setText("ASA :"); lblASA.setBounds(10, y, 120, 23); FormInput.add(lblASA);
        CmbASA = new ComboBox();
        CmbASA.setModel(new DefaultComboBoxModel(new String[]{"I","II","III","IV","V","VI"}));
        CmbASA.setBounds(135, y, 80, 23); FormInput.add(CmbASA);

        y += 27;
        Label lblPremed = new Label(); lblPremed.setText("Premedikasi :"); lblPremed.setBounds(10, y, 120, 23); FormInput.add(lblPremed);
        TPremedikasi = new TextBox(); TPremedikasi.setBounds(135, y, 300, 23); FormInput.add(TPremedikasi);

        y += 27;
        Label lblPuasa = new Label(); lblPuasa.setText("Puasa :"); lblPuasa.setBounds(10, y, 120, 23); FormInput.add(lblPuasa);
        TPuasa = new TextBox(); TPuasa.setBounds(135, y, 150, 23); FormInput.add(TPuasa);

        y += 27;
        Label lblPenyulit = new Label(); lblPenyulit.setText("Penyulit :"); lblPenyulit.setBounds(10, y, 120, 23); FormInput.add(lblPenyulit);
        TPenyulit = new TextBox(); TPenyulit.setBounds(135, y, 400, 23); FormInput.add(TPenyulit);

        y += 27;
        Label lblTeknik = new Label(); lblTeknik.setText("Teknik Anestesi / Sedasi :"); lblTeknik.setBounds(10, y, 150, 23); FormInput.add(lblTeknik);
        Label lblGA = new Label(); lblGA.setText("GA :"); lblGA.setBounds(165, y, 35, 23); FormInput.add(lblGA);
        ChkGaLma = new CekBox(); ChkGaLma.setText("LMA"); ChkGaLma.setBounds(205, y, 65, 23); ChkGaLma.setSelected(false); ChkGaLma.setOpaque(false); FormInput.add(ChkGaLma);
        ChkGaEtt = new CekBox(); ChkGaEtt.setText("ETT"); ChkGaEtt.setBounds(275, y, 65, 23); ChkGaEtt.setSelected(false); ChkGaEtt.setOpaque(false); FormInput.add(ChkGaEtt);
        ChkGaTiva = new CekBox(); ChkGaTiva.setText("TIVA"); ChkGaTiva.setBounds(345, y, 70, 23); ChkGaTiva.setSelected(false); ChkGaTiva.setOpaque(false); FormInput.add(ChkGaTiva);

        y += 25;
        Label lblSedasi = new Label(); lblSedasi.setText("Sedasi :"); lblSedasi.setBounds(145, y, 55, 23); FormInput.add(lblSedasi);
        ChkSedasiRingan = new CekBox(); ChkSedasiRingan.setText("Ringan"); ChkSedasiRingan.setBounds(205, y, 75, 23); ChkSedasiRingan.setSelected(false); ChkSedasiRingan.setOpaque(false); FormInput.add(ChkSedasiRingan);
        ChkSedasiSedang = new CekBox(); ChkSedasiSedang.setText("Sedang"); ChkSedasiSedang.setBounds(285, y, 75, 23); ChkSedasiSedang.setSelected(false); ChkSedasiSedang.setOpaque(false); FormInput.add(ChkSedasiSedang);
        ChkSedasiDalam = new CekBox(); ChkSedasiDalam.setText("Dalam"); ChkSedasiDalam.setBounds(365, y, 75, 23); ChkSedasiDalam.setSelected(false); ChkSedasiDalam.setOpaque(false); FormInput.add(ChkSedasiDalam);

        y += 25;
        Label lblRegional = new Label(); lblRegional.setText("Regional :"); lblRegional.setBounds(135, y, 65, 23); FormInput.add(lblRegional);
        ChkSpinal = new CekBox(); ChkSpinal.setText("Spinal"); ChkSpinal.setBounds(205, y, 70, 23); ChkSpinal.setSelected(false); ChkSpinal.setOpaque(false); FormInput.add(ChkSpinal);
        ChkEpidural = new CekBox(); ChkEpidural.setText("Epidural"); ChkEpidural.setBounds(280, y, 75, 23); ChkEpidural.setSelected(false); ChkEpidural.setOpaque(false); FormInput.add(ChkEpidural);
        ChkKaudal = new CekBox(); ChkKaudal.setText("Kaudal"); ChkKaudal.setBounds(360, y, 70, 23); ChkKaudal.setSelected(false); ChkKaudal.setOpaque(false); FormInput.add(ChkKaudal);
        ChkBlokPerifer = new CekBox(); ChkBlokPerifer.setText("Blok Perifer"); ChkBlokPerifer.setBounds(435, y, 95, 23); ChkBlokPerifer.setSelected(false); ChkBlokPerifer.setOpaque(false); FormInput.add(ChkBlokPerifer);

        y += 27;
        Label lblDarah = new Label(); lblDarah.setText("Persediaan Darah :"); lblDarah.setBounds(10, y, 120, 23); FormInput.add(lblDarah);
        TPersediaanDarah = new TextBox(); TPersediaanDarah.setBounds(135, y, 400, 23); FormInput.add(TPersediaanDarah);

        // === SECTION 6: TEKNIK KHUSUS ===
        y += 35;
        JLabel lblSec6 = new JLabel("TEKNIK KHUSUS");
        lblSec6.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec6.setBounds(10, y, 200, 20); FormInput.add(lblSec6);

        y += 25;
        ChkHipotensi = new CekBox(); ChkHipotensi.setText("Hipotensi terkendali"); ChkHipotensi.setBounds(10, y, 160, 23); ChkHipotensi.setSelected(false); ChkHipotensi.setOpaque(false); FormInput.add(ChkHipotensi);
        ChkVentilasi = new CekBox(); ChkVentilasi.setText("Ventilasi satu paru"); ChkVentilasi.setBounds(180, y, 160, 23); ChkVentilasi.setSelected(false); ChkVentilasi.setOpaque(false); FormInput.add(ChkVentilasi);

        y += 27;
        Label lblTeknikLain = new Label(); lblTeknikLain.setText("Lain-lain :"); lblTeknikLain.setBounds(10, y, 120, 23); FormInput.add(lblTeknikLain);
        TTeknikKhususLain = new TextBox(); TTeknikKhususLain.setBounds(135, y, 400, 23); FormInput.add(TTeknikKhususLain);

        // === SECTION 7: MONITORING ===
        y += 35;
        JLabel lblSec7 = new JLabel("MONITORING");
        lblSec7.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec7.setBounds(10, y, 200, 20); FormInput.add(lblSec7);

        y += 25;
        ChkMonEkg = new CekBox(); ChkMonEkg.setText("EKG"); ChkMonEkg.setBounds(10, y, 60, 23); ChkMonEkg.setSelected(false); ChkMonEkg.setOpaque(false); FormInput.add(ChkMonEkg);
        ChkMonNadi = new CekBox(); ChkMonNadi.setText("Nadi"); ChkMonNadi.setBounds(75, y, 60, 23); ChkMonNadi.setSelected(false); ChkMonNadi.setOpaque(false); FormInput.add(ChkMonNadi);
        ChkMonSpo2 = new CekBox(); ChkMonSpo2.setText("SpO2"); ChkMonSpo2.setBounds(140, y, 65, 23); ChkMonSpo2.setSelected(false); ChkMonSpo2.setOpaque(false); FormInput.add(ChkMonSpo2);
        ChkMonTemp = new CekBox(); ChkMonTemp.setText("Temp"); ChkMonTemp.setBounds(210, y, 65, 23); ChkMonTemp.setSelected(false); ChkMonTemp.setOpaque(false); FormInput.add(ChkMonTemp);
        ChkMonNibp = new CekBox(); ChkMonNibp.setText("NIBP"); ChkMonNibp.setBounds(280, y, 65, 23); ChkMonNibp.setSelected(false); ChkMonNibp.setOpaque(false); FormInput.add(ChkMonNibp);
        ChkMonCvp = new CekBox(); ChkMonCvp.setText("CVP"); ChkMonCvp.setBounds(350, y, 55, 23); ChkMonCvp.setSelected(false); ChkMonCvp.setOpaque(false); FormInput.add(ChkMonCvp);
        ChkMonEtco2 = new CekBox(); ChkMonEtco2.setText("EtCO2"); ChkMonEtco2.setBounds(410, y, 70, 23); ChkMonEtco2.setSelected(false); ChkMonEtco2.setOpaque(false); FormInput.add(ChkMonEtco2);

        y += 27;
        Label lblMonArteri = new Label(); lblMonArteri.setText("Arteri :"); lblMonArteri.setBounds(10, y, 120, 23); FormInput.add(lblMonArteri);
        TMonArteri = new TextBox(); TMonArteri.setBounds(135, y, 300, 23); FormInput.add(TMonArteri);

        y += 27;
        Label lblMonLain = new Label(); lblMonLain.setText("Monitoring Lainnya :"); lblMonLain.setBounds(10, y, 120, 23); FormInput.add(lblMonLain);
        TMonLain = new TextBox(); TMonLain.setBounds(135, y, 300, 23); FormInput.add(TMonLain);

        // === PERAWATAN PASCA ===
        y += 35;
        Label lblPasca = new Label(); lblPasca.setText("Perawatan Pasca :"); lblPasca.setBounds(10, y, 120, 23); FormInput.add(lblPasca);
        CmbPerawatanPasca = new ComboBox();
        CmbPerawatanPasca.setModel(new DefaultComboBoxModel(new String[]{"Rawat Jalan","Rawat Inap","ICU / HCU","RR","Lainnya"}));
        CmbPerawatanPasca.setBounds(135, y, 120, 23); FormInput.add(CmbPerawatanPasca);

        // === DOKTER ANESTESI ===
        y += 35;
        Label lblDokter = new Label(); lblDokter.setText("Dokter Anestesi :"); lblDokter.setBounds(10, y, 120, 23); FormInput.add(lblDokter);
        KdDokter = new TextBox(); KdDokter.setBounds(135, y, 80, 23); FormInput.add(KdDokter);
        NmDokter = new TextBox(); NmDokter.setBounds(217, y, 180, 23); FormInput.add(NmDokter);
        BtnDokter = new Button(); BtnDokter.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokter.setBounds(399, y, 28, 23); FormInput.add(BtnDokter);
        BtnDokter.addActionListener(evt -> {
            dokterDlg.emptTeks(); dokterDlg.isCek();
            dokterDlg.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20);
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
            } else if (KdDokter.getText().trim().equals("")) { Valid.textKosong(KdDokter, "Dokter Anestesi");
            } else {
                if (Sequel.menyimpantf("asesmen_pra_anestesi_rscb",
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?",
                    "Data", 62, new String[]{
                    TNoRw.getText(),
                    Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19),
                    TRuangan.getText(), TDiagnosa.getText(), TTindakanBedah.getText(),
                    TBB.getText(), TTB.getText(), TAnamnesi.getText(),
                    ChkAsma.isSelected() ? "Ya" : "Tidak",
                    ChkAlergi.isSelected() ? "Ya" : "Tidak",
                    ChkDiabetes.isSelected() ? "Ya" : "Tidak",
                    ChkHipertensi.isSelected() ? "Ya" : "Tidak",
                    TRiwayatOperasi.getText(), TJenisAnestesiRiwayat.getText(), TKomplikasiRiwayat.getText(),
                    TPemeriksaanFisik.getText(), TKeadaanUmum.getText(), TGCS.getText(),
                    TTD.getText(), TNadi.getText(), TRR.getText(), TSuhu.getText(),
                    CmbGerakanLeher.getSelectedItem().toString(),
                    CmbMethohyoid.getSelectedItem().toString(),
                    CmbMallampati.getSelectedItem().toString(),
                    TThorax.getText(), TAbdomen.getText(), TEkstremitas.getText(),
                    TLaboratorium.getText(), TEKG.getText(), TRoThorax.getText(), TLainLain.getText(),
                    CmbASA.getSelectedItem().toString(),
                    TPremedikasi.getText(), TPuasa.getText(), TPenyulit.getText(),
                    ChkGaLma.isSelected() ? "Ya" : "Tidak",
                    ChkGaEtt.isSelected() ? "Ya" : "Tidak",
                    ChkGaTiva.isSelected() ? "Ya" : "Tidak",
                    ChkSedasiRingan.isSelected() ? "Ya" : "Tidak",
                    ChkSedasiSedang.isSelected() ? "Ya" : "Tidak",
                    ChkSedasiDalam.isSelected() ? "Ya" : "Tidak",
                    ChkSpinal.isSelected() ? "Ya" : "Tidak",
                    ChkEpidural.isSelected() ? "Ya" : "Tidak",
                    ChkKaudal.isSelected() ? "Ya" : "Tidak",
                    ChkBlokPerifer.isSelected() ? "Ya" : "Tidak",
                    TPersediaanDarah.getText(),
                    ChkHipotensi.isSelected() ? "Ya" : "Tidak",
                    ChkVentilasi.isSelected() ? "Ya" : "Tidak",
                    TTeknikKhususLain.getText(),
                    ChkMonEkg.isSelected() ? "Ya" : "Tidak",
                    ChkMonNadi.isSelected() ? "Ya" : "Tidak",
                    ChkMonSpo2.isSelected() ? "Ya" : "Tidak",
                    ChkMonNibp.isSelected() ? "Ya" : "Tidak",
                    ChkMonTemp.isSelected() ? "Ya" : "Tidak",
                    TMonLain.getText(),
                    ChkMonCvp.isSelected() ? "Ya" : "Tidak",
                    TMonArteri.getText(),
                    ChkMonEtco2.isSelected() ? "Ya" : "Tidak",
                    CmbPerawatanPasca.getSelectedItem().toString(),
                    KdDokter.getText(), ""
                })) { tampil(); emptTeks(); }
            }
        });

        BtnHapus.addActionListener(evt -> {
            if (tbObat.getSelectedRow() != -1) {
                if (Sequel.queryu2tf("delete from asesmen_pra_anestesi_rscb where no_rawat=? and tanggal=?", 2, new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
                })) { tampil(); emptTeks(); }
            } else JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dihapus...!!!!");
        });

        BtnEdit.addActionListener(evt -> {
            if (TNoRw.getText().trim().equals("") || tbObat.getSelectedRow()==-1) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            } else {
                if (Sequel.queryu2tf("delete from asesmen_pra_anestesi_rscb where no_rawat=? and tanggal=?", 2, new String[]{
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
                Valid.MyReportqry("rptAsesmenPraAnestesiRscb.jasper", "report", "::[ Asesmen Pra Anestesi ]::",
                    "select s.*, r.no_rkm_medis, p.nm_pasien, p.tgl_lahir, p.jk, d.nm_dokter "+
                    "from asesmen_pra_anestesi_rscb s inner join reg_periksa r on s.no_rawat=r.no_rawat "+
                    "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "+
                    "left join dokter d on s.kd_dokter=d.kd_dokter "+
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
                "select s.no_rawat, r.no_rkm_medis, p.nm_pasien, s.tanggal, s.ruangan, s.diagnosa, "+
                "s.asa, d.nm_dokter "+
                "from asesmen_pra_anestesi_rscb s inner join reg_periksa r on s.no_rawat=r.no_rawat "+
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "+
                "left join dokter d on s.kd_dokter=d.kd_dokter "+
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
                    rs.getString("tanggal"), rs.getString("ruangan"), rs.getString("diagnosa"),
                    rs.getString("asa"), rs.getString("nm_dokter")
                });
            }
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif Asesmen Pra Anestesi : " + e);
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
                ps = koneksi.prepareStatement("select * from asesmen_pra_anestesi_rscb where no_rawat=? and tanggal=?");
                ps.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                ps.setString(2, tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    loadFromBookingOperasi(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                    TDiagnosa.setText(rs.getString("diagnosa"));
                    TBB.setText(rs.getString("bb"));
                    TTB.setText(rs.getString("tb"));
                    TAnamnesi.setText(rs.getString("anamnesi"));
                    ChkAsma.setSelected("Ya".equals(rs.getString("riwayat_asma")));
                    ChkAlergi.setSelected("Ya".equals(rs.getString("alergi")));
                    ChkDiabetes.setSelected("Ya".equals(rs.getString("diabetes")));
                    ChkHipertensi.setSelected("Ya".equals(rs.getString("hipertensi")));
                    TRiwayatOperasi.setText(rs.getString("riwayat_operasi"));
                    TJenisAnestesiRiwayat.setText(rs.getString("jenis_anestesi_riwayat"));
                    TKomplikasiRiwayat.setText(rs.getString("komplikasi_riwayat"));
                    TPemeriksaanFisik.setText(rs.getString("pemeriksaan_fisik"));
                    TKeadaanUmum.setText(rs.getString("keadaan_umum"));
                    TGCS.setText(rs.getString("gcs"));
                    TTD.setText(rs.getString("td"));
                    TNadi.setText(rs.getString("nadi"));
                    TRR.setText(rs.getString("rr"));
                    TSuhu.setText(rs.getString("suhu"));
                    CmbGerakanLeher.setSelectedItem(rs.getString("gerakan_leher"));
                    CmbMethohyoid.setSelectedItem(rs.getString("methohyoid"));
                    CmbMallampati.setSelectedItem(rs.getString("mallampati"));
                    TThorax.setText(rs.getString("thorax"));
                    TAbdomen.setText(rs.getString("abdomen"));
                    TEkstremitas.setText(rs.getString("ekstremitas"));
                    TLaboratorium.setText(rs.getString("laboratorium"));
                    TEKG.setText(rs.getString("ekg"));
                    TRoThorax.setText(rs.getString("ro_thorax"));
                    TLainLain.setText(rs.getString("lain_lain"));
                    CmbASA.setSelectedItem(rs.getString("asa"));
                    TPremedikasi.setText(rs.getString("premedikasi"));
                    TPuasa.setText(rs.getString("puasa"));
                    TPenyulit.setText(rs.getString("penyulit"));
                    ChkGaLma.setSelected("Ya".equals(rs.getString("teknik_ga_lma")));
                    ChkGaEtt.setSelected("Ya".equals(rs.getString("teknik_ga_ett")));
                    ChkGaTiva.setSelected("Ya".equals(rs.getString("teknik_ga_tiva")));
                    ChkSedasiRingan.setSelected("Ya".equals(rs.getString("teknik_sedasi_ringan")));
                    ChkSedasiSedang.setSelected("Ya".equals(rs.getString("teknik_sedasi_sedang")));
                    ChkSedasiDalam.setSelected("Ya".equals(rs.getString("teknik_sedasi_dalam")));
                    ChkSpinal.setSelected("Ya".equals(rs.getString("teknik_spinal")));
                    ChkEpidural.setSelected("Ya".equals(rs.getString("teknik_epidural")));
                    ChkKaudal.setSelected("Ya".equals(rs.getString("teknik_kaudal")));
                    ChkBlokPerifer.setSelected("Ya".equals(rs.getString("teknik_blok_perifer")));
                    TPersediaanDarah.setText(rs.getString("persediaan_darah"));
                    ChkHipotensi.setSelected("Ya".equals(rs.getString("teknik_khusus_hipotensi")));
                    ChkVentilasi.setSelected("Ya".equals(rs.getString("teknik_khusus_ventilasi")));
                    TTeknikKhususLain.setText(rs.getString("teknik_khusus_lain"));
                    ChkMonEkg.setSelected("Ya".equals(rs.getString("monitoring_ekg")));
                    ChkMonNadi.setSelected("Ya".equals(rs.getString("monitoring_nadi")));
                    ChkMonSpo2.setSelected("Ya".equals(rs.getString("monitoring_spo2")));
                    ChkMonNibp.setSelected("Ya".equals(rs.getString("monitoring_nibp")));
                    ChkMonTemp.setSelected("Ya".equals(rs.getString("monitoring_temp")));
                    TMonLain.setText(rs.getString("monitoring_lain"));
                    ChkMonCvp.setSelected("Ya".equals(rs.getString("monitoring_cvp")));
                    TMonArteri.setText(rs.getString("monitoring_arteri"));
                    ChkMonEtco2.setSelected("Ya".equals(rs.getString("monitoring_etco2")));
                    CmbPerawatanPasca.setSelectedItem(rs.getString("perawatan_pasca"));
                    KdDokter.setText(rs.getString("kd_dokter"));
                    NmDokter.setText(Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", rs.getString("kd_dokter")));
                }
            } catch (Exception e) { System.out.println("Notif getData Asesmen Pra Anestesi : " + e);
            } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
        }
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date());
        TRuangan.setText(""); TDiagnosa.setText(""); TTindakanBedah.setText("");
        TBB.setText(""); TTB.setText(""); TAnamnesi.setText("");
        ChkAsma.setSelected(false); ChkAlergi.setSelected(false);
        ChkDiabetes.setSelected(false); ChkHipertensi.setSelected(false);
        TRiwayatOperasi.setText(""); TJenisAnestesiRiwayat.setText(""); TKomplikasiRiwayat.setText("");
        TPemeriksaanFisik.setText(""); TKeadaanUmum.setText(""); TGCS.setText("");
        TTD.setText(""); TNadi.setText(""); TRR.setText(""); TSuhu.setText("");
        CmbGerakanLeher.setSelectedIndex(0); CmbMethohyoid.setSelectedIndex(0); CmbMallampati.setSelectedIndex(0);
        TThorax.setText(""); TAbdomen.setText(""); TEkstremitas.setText("");
        TLaboratorium.setText(""); TEKG.setText(""); TRoThorax.setText(""); TLainLain.setText("");
        CmbASA.setSelectedIndex(0); TPremedikasi.setText(""); TPuasa.setText(""); TPenyulit.setText("");
        ChkGaLma.setSelected(false); ChkGaEtt.setSelected(false); ChkGaTiva.setSelected(false);
        ChkSedasiRingan.setSelected(false); ChkSedasiSedang.setSelected(false); ChkSedasiDalam.setSelected(false);
        ChkSpinal.setSelected(false); ChkEpidural.setSelected(false); ChkKaudal.setSelected(false);
        ChkBlokPerifer.setSelected(false); TPersediaanDarah.setText("");
        ChkHipotensi.setSelected(false); ChkVentilasi.setSelected(false); TTeknikKhususLain.setText("");
        ChkMonEkg.setSelected(false); ChkMonNadi.setSelected(false); ChkMonSpo2.setSelected(false); ChkMonNibp.setSelected(false);
        ChkMonTemp.setSelected(false); ChkMonCvp.setSelected(false); ChkMonEtco2.setSelected(false);
        TMonArteri.setText(""); TMonLain.setText("");
        CmbPerawatanPasca.setSelectedIndex(0);
        KdDokter.setText(""); NmDokter.setText("");
        TRuangan.setText(""); TTindakanBedah.setText("");
    }

    public void setNoRm(String norwt, Date tgl) {
        TNoRw.setText(norwt); TglAsuhan.setDate(tgl);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", norwt));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
        loadFromBookingOperasi(norwt);
    }

    public void setNoRm(String norwt, String norm, String nama) {
        TNoRw.setText(norwt); TNoRM.setText(norm); TPasien.setText(nama);
        loadFromBookingOperasi(norwt);
    }

    private void loadFromBookingOperasi(String noRawat) {
        try {
            ps = koneksi.prepareStatement(
                "select ro.nm_ruang_ok, po.nm_perawatan " +
                "from booking_operasi bo " +
                "left join ruang_ok ro on bo.kd_ruang_ok=ro.kd_ruang_ok " +
                "left join paket_operasi po on bo.kode_paket=po.kode_paket " +
                "where bo.no_rawat=? order by bo.tanggal desc limit 1");
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            if (rs.next()) {
                TRuangan.setText(rs.getString("nm_ruang_ok") != null ? rs.getString("nm_ruang_ok") : "");
                TTindakanBedah.setText(rs.getString("nm_perawatan") != null ? rs.getString("nm_perawatan") : "");
            }
        } catch (Exception e) { System.out.println("Notif loadFromBookingOperasi : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getrujukan_keluar()); BtnHapus.setEnabled(akses.getrujukan_keluar());
        BtnEdit.setEnabled(akses.getrujukan_keluar()); BtnPrint.setEnabled(akses.getrujukan_keluar());
    }
}
