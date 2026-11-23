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
import widget.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;

public class RMAsesmenMedisRJRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll, BtnDokter;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private TextBox KdDokter, NmDokter;
    private Label LCount;

    // SUBJEKTIF
    private ComboBox CmbAnamnesa;
    private TextArea TKeluhanUtama, TRiwayatPenyakitSekarang, TRiwayatPenyakitDahulu, TRiwayatPenyakitKeluarga, TRiwayatAlergi;

    // OBJEKTIF - Tanda Vital
    private TextBox TKeadaanUmum, TGCS, TTekananDarah, TNadi, TSuhu, TRespirasi;
    private ComboBox CmbKesadaran;

    // Antropometri
    private TextBox TBeratBadan, TTinggiBadan, TLingkarKepala;
    private ComboBox CmbStatusGizi;

    // Organ exam (14 organs: normal checkbox + temuan textbox)
    private CekBox ChkKepala, ChkMata, ChkTHT, ChkMulut, ChkLeher, ChkDada, ChkJantung;
    private CekBox ChkParu, ChkPerut, ChkUrogenital, ChkAnggotaGerak, ChkMuskuloskeletal, ChkStatusNeurologis, ChkStatusLokasi;
    private TextBox TTemuanKepala, TTemuanMata, TTemuanTHT, TTemuanMulut, TTemuanLeher, TTemuanDada, TTemuanJantung;
    private TextBox TTemuanParu, TTemuanPerut, TTemuanUrogenital, TTemuanAnggotaGerak, TTemuanMuskuloskeletal, TTemuanStatusNeurologis, TTemuanStatusLokasi;

    // Penunjang
    private CekBox ChkLab, ChkRadiologi, ChkEKG;
    private TextBox TPenunjangLainnya;

    // ASSESSMENT
    private TextArea TDiagnosaKerja, TDiagnosaBanding;

    // PLANNING
    private TextArea TTerapi, TRencanaTindakan;

    // EDUKASI
    private CekBox ChkEdukasiPerjalanan, ChkEdukasiDiagnosa, ChkEdukasiPengobatan;

    // DISCHARGE PLANNING
    private CekBox ChkDPPulang, ChkDPKontrol, ChkDPRawatInap, ChkDPRujuk;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariDokter dokterDlg = new DlgCariDokter(null, false);

    private widget.Table tbObat;
    private javax.swing.table.DefaultTableModel tabMode;
    private ScrollPane Scroll, ScrollInput;
    private PanelBiasa FormInput;
    private JTabbedPane tabPane;
    private CoretPanel panelCoret;
    public widget.editorpane LoadHTML;

    public RMAsesmenMedisRJRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(240,245,235)),
            "::[ Asesmen Medis Rawat Jalan ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma",0,11), new Color(50,50,50)));
        internalFrame1.setLayout(new BorderLayout(1,1));

        panelisi panelAtas = new panelisi();
        panelAtas.setLayout(new FlowLayout(FlowLayout.LEFT,5,4));
        panelAtas.setPreferredSize(new Dimension(1024,33));
        Label lblNoRw = new Label(); lblNoRw.setText("No.Rawat :");
        TNoRw = new TextBox(); TNoRw.setPreferredSize(new Dimension(130,23));
        TNoRM = new TextBox(); TNoRM.setPreferredSize(new Dimension(100,23));
        TPasien = new TextBox(); TPasien.setPreferredSize(new Dimension(200,23));
        panelAtas.add(lblNoRw); panelAtas.add(TNoRw); panelAtas.add(TNoRM); panelAtas.add(TPasien);

        Scroll = new ScrollPane();
        tbObat = new widget.Table();
        tabMode = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","Anamnesa","Diagnosa Kerja","Dokter","Petugas"}
        ) { @Override public boolean isCellEditable(int r,int c){return false;} };
        tbObat.setModel(tabMode);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override public void mouseClicked(java.awt.event.MouseEvent e){if(tabMode.getRowCount()!=0) getData();}
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter(){
            @Override public void keyPressed(java.awt.event.KeyEvent e){
                if(e.getKeyCode()==KeyEvent.VK_SPACE && tabMode.getRowCount()!=0){
                    getData();
                    if(tabPane!=null) tabPane.setSelectedIndex(0);
                    e.consume();
                }
            }
        });
        JPopupMenu pop = new JPopupMenu();
        JMenuItem mnCetak = new JMenuItem("Cetak Asesmen Medis Rawat Jalan");
        mnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetak.addActionListener(evt -> BtnPrint.doClick());
        pop.add(mnCetak);
        tbObat.setComponentPopupMenu(pop);
        Scroll.setViewportView(tbObat);

        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(880,1800));
        FormInput.setLayout(null);

        int y = 10;
        JLabel h = new JLabel("ASESMEN MEDIS RAWAT JALAN");
        h.setFont(new java.awt.Font("Tahoma",1,12)); h.setBounds(10,y,400,20); FormInput.add(h);
        JLabel hk = new JLabel("RM.KEP.08.041A.2019"); hk.setFont(new java.awt.Font("Tahoma",0,8)); hk.setBounds(700,y,150,20); FormInput.add(hk);

        y+=32;
        Label lt = new Label(); lt.setText("Tanggal :"); lt.setBounds(10,y,80,23); FormInput.add(lt);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TglAsuhan.setBounds(95,y,130,23); FormInput.add(TglAsuhan);

        // ============ SUBJEKTIF ============
        y+=38;
        JLabel hs = new JLabel("I. SUBJEKTIF"); hs.setFont(new java.awt.Font("Tahoma",1,11)); hs.setBounds(10,y,200,20); FormInput.add(hs);

        y+=25;
        Label la = new Label(); la.setText("Anamnesa :"); la.setBounds(10,y,80,23); FormInput.add(la);
        CmbAnamnesa = new ComboBox(); CmbAnamnesa.setModel(new DefaultComboBoxModel(new String[]{"Autoanamnesis","Alloanamnesis"}));
        CmbAnamnesa.setBounds(95,y,150,23); FormInput.add(CmbAnamnesa);

        y+=30;
        Label lku = new Label(); lku.setText("Keluhan Utama :"); lku.setBounds(10,y,100,23); FormInput.add(lku);
        TKeluhanUtama = new TextArea();
        JScrollPane scrKU = new JScrollPane(TKeluhanUtama);
        scrKU.setBounds(115,y,480,50); FormInput.add(scrKU);

        y+=55;
        Label lrps = new Label(); lrps.setText("Riwayat Peny. Skrg :"); lrps.setBounds(10,y,125,23); FormInput.add(lrps);
        TRiwayatPenyakitSekarang = new TextArea();
        JScrollPane scrRPS = new JScrollPane(TRiwayatPenyakitSekarang);
        scrRPS.setBounds(140,y,455,50); FormInput.add(scrRPS);

        y+=55;
        Label lrpd = new Label(); lrpd.setText("Riwayat Peny. Dahulu :"); lrpd.setBounds(10,y,135,23); FormInput.add(lrpd);
        TRiwayatPenyakitDahulu = new TextArea();
        JScrollPane scrRPD = new JScrollPane(TRiwayatPenyakitDahulu);
        scrRPD.setBounds(150,y,445,50); FormInput.add(scrRPD);

        y+=55;
        Label lrpk = new Label(); lrpk.setText("Riwayat Peny. Kelg :"); lrpk.setBounds(10,y,125,23); FormInput.add(lrpk);
        TRiwayatPenyakitKeluarga = new TextArea();
        JScrollPane scrRPK = new JScrollPane(TRiwayatPenyakitKeluarga);
        scrRPK.setBounds(140,y,455,50); FormInput.add(scrRPK);

        y+=55;
        Label lra = new Label(); lra.setText("Riwayat Alergi :"); lra.setBounds(10,y,100,23); FormInput.add(lra);
        TRiwayatAlergi = new TextArea();
        JScrollPane scrRA = new JScrollPane(TRiwayatAlergi);
        scrRA.setBounds(115,y,480,50); FormInput.add(scrRA);

        // ============ OBJEKTIF ============
        y+=60;
        JLabel ho = new JLabel("II. OBJEKTIF - Tanda Vital"); ho.setFont(new java.awt.Font("Tahoma",1,11)); ho.setBounds(10,y,300,20); FormInput.add(ho);

        y+=25;
        Label lkm = new Label(); lkm.setText("Keadaan Umum :"); lkm.setBounds(10,y,105,23); FormInput.add(lkm);
        TKeadaanUmum = new TextBox(); TKeadaanUmum.setBounds(120,y,150,23); FormInput.add(TKeadaanUmum);
        Label lks = new Label(); lks.setText("Kesadaran :"); lks.setBounds(290,y,75,23); FormInput.add(lks);
        CmbKesadaran = new ComboBox(); CmbKesadaran.setModel(new DefaultComboBoxModel(new String[]{"Compos Mentis","Somnolen","Sopor","Koma"}));
        CmbKesadaran.setBounds(370,y,130,23); FormInput.add(CmbKesadaran);

        y+=28;
        Label lgcs = new Label(); lgcs.setText("GCS :"); lgcs.setBounds(10,y,40,23); FormInput.add(lgcs);
        TGCS = new TextBox(); TGCS.setBounds(55,y,80,23); FormInput.add(TGCS);
        Label ltd = new Label(); ltd.setText("TD :"); ltd.setBounds(150,y,30,23); FormInput.add(ltd);
        TTekananDarah = new TextBox(); TTekananDarah.setBounds(185,y,80,23); FormInput.add(TTekananDarah);
        Label ln = new Label(); ln.setText("Nadi :"); ln.setBounds(280,y,40,23); FormInput.add(ln);
        TNadi = new TextBox(); TNadi.setBounds(325,y,60,23); FormInput.add(TNadi);

        y+=28;
        Label lsu = new Label(); lsu.setText("Suhu :"); lsu.setBounds(10,y,40,23); FormInput.add(lsu);
        TSuhu = new TextBox(); TSuhu.setBounds(55,y,60,23); FormInput.add(TSuhu);
        Label lrsp = new Label(); lrsp.setText("Respirasi :"); lrsp.setBounds(130,y,65,23); FormInput.add(lrsp);
        TRespirasi = new TextBox(); TRespirasi.setBounds(200,y,60,23); FormInput.add(TRespirasi);

        // Antropometri
        y+=35;
        JLabel hat = new JLabel("Antropometri"); hat.setFont(new java.awt.Font("Tahoma",1,11)); hat.setBounds(10,y,200,20); FormInput.add(hat);

        y+=25;
        Label lbb = new Label(); lbb.setText("BB :"); lbb.setBounds(10,y,30,23); FormInput.add(lbb);
        TBeratBadan = new TextBox(); TBeratBadan.setBounds(45,y,60,23); FormInput.add(TBeratBadan);
        Label ltb = new Label(); ltb.setText("TB :"); ltb.setBounds(120,y,30,23); FormInput.add(ltb);
        TTinggiBadan = new TextBox(); TTinggiBadan.setBounds(155,y,60,23); FormInput.add(TTinggiBadan);
        Label llk = new Label(); llk.setText("L.Kepala :"); llk.setBounds(230,y,65,23); FormInput.add(llk);
        TLingkarKepala = new TextBox(); TLingkarKepala.setBounds(300,y,60,23); FormInput.add(TLingkarKepala);
        Label lsg = new Label(); lsg.setText("Status Gizi :"); lsg.setBounds(380,y,80,23); FormInput.add(lsg);
        CmbStatusGizi = new ComboBox(); CmbStatusGizi.setModel(new DefaultComboBoxModel(new String[]{"Baik","Cukup","Kurang"}));
        CmbStatusGizi.setBounds(465,y,100,23); FormInput.add(CmbStatusGizi);

        // Gambar anatomi tubuh (bisa di corat-coret)
        y+=35;
        panelCoret = new CoretPanel(new javax.swing.ImageIcon(getClass().getResource("/picture/awal_medis_rscb.png")).getImage());
        panelCoret.setBounds(10,y,585,160); FormInput.add(panelCoret);
        Button btnHapusCoret = new Button(); btnHapusCoret.setText("Hapus Coretan"); btnHapusCoret.setBounds(605,y,100,23);
        btnHapusCoret.addActionListener(ev->panelCoret.clear()); FormInput.add(btnHapusCoret);

        // ============ Pemeriksaan Organ ============
        y+=170;
        JLabel hpo = new JLabel("Pemeriksaan Organ"); hpo.setFont(new java.awt.Font("Tahoma",1,11)); hpo.setBounds(10,y,200,20); FormInput.add(hpo);
        JLabel hNorm = new JLabel("Normal"); hNorm.setFont(new java.awt.Font("Tahoma",0,10)); hNorm.setBounds(150,y,50,20); FormInput.add(hNorm);
        JLabel hTem = new JLabel("Temuan"); hTem.setFont(new java.awt.Font("Tahoma",0,10)); hTem.setBounds(210,y,50,20); FormInput.add(hTem);

        // Create organ exam rows
        String[] organNames = {"Kepala","Mata","THT","Mulut","Leher","Dada","Jantung","Paru","Perut","Urogenital","Anggota Gerak","Muskuloskeletal","Status Neurologis","Status Lokasi"};
        CekBox[] chkOrgans = new CekBox[14];
        TextBox[] txtOrgans = new TextBox[14];

        for(int i=0;i<14;i++){
            y+=25;
            Label lo2 = new Label(); lo2.setText(organNames[i]+" :"); lo2.setBounds(10,y,130,23); FormInput.add(lo2);
            chkOrgans[i] = new CekBox(); chkOrgans[i].setText(""); chkOrgans[i].setSelected(true); chkOrgans[i].setBounds(150,y,23,23); FormInput.add(chkOrgans[i]);
            txtOrgans[i] = new TextBox(); txtOrgans[i].setBounds(210,y,385,23); FormInput.add(txtOrgans[i]);
        }
        ChkKepala=chkOrgans[0]; ChkMata=chkOrgans[1]; ChkTHT=chkOrgans[2]; ChkMulut=chkOrgans[3];
        ChkLeher=chkOrgans[4]; ChkDada=chkOrgans[5]; ChkJantung=chkOrgans[6]; ChkParu=chkOrgans[7];
        ChkPerut=chkOrgans[8]; ChkUrogenital=chkOrgans[9]; ChkAnggotaGerak=chkOrgans[10]; ChkMuskuloskeletal=chkOrgans[11];
        ChkStatusNeurologis=chkOrgans[12]; ChkStatusLokasi=chkOrgans[13];
        TTemuanKepala=txtOrgans[0]; TTemuanMata=txtOrgans[1]; TTemuanTHT=txtOrgans[2]; TTemuanMulut=txtOrgans[3];
        TTemuanLeher=txtOrgans[4]; TTemuanDada=txtOrgans[5]; TTemuanJantung=txtOrgans[6]; TTemuanParu=txtOrgans[7];
        TTemuanPerut=txtOrgans[8]; TTemuanUrogenital=txtOrgans[9]; TTemuanAnggotaGerak=txtOrgans[10]; TTemuanMuskuloskeletal=txtOrgans[11];
        TTemuanStatusNeurologis=txtOrgans[12]; TTemuanStatusLokasi=txtOrgans[13];

        // ============ Pemeriksaan Penunjang ============
        y+=35;
        JLabel hpp = new JLabel("Pemeriksaan Penunjang"); hpp.setFont(new java.awt.Font("Tahoma",1,11)); hpp.setBounds(10,y,200,20); FormInput.add(hpp);

        y+=25;
        ChkLab = new CekBox(); ChkLab.setText("Lab"); ChkLab.setBounds(10,y,70,23); FormInput.add(ChkLab);
        ChkRadiologi = new CekBox(); ChkRadiologi.setText("Radiologi"); ChkRadiologi.setBounds(90,y,100,23); FormInput.add(ChkRadiologi);
        ChkEKG = new CekBox(); ChkEKG.setText("EKG"); ChkEKG.setBounds(200,y,70,23); FormInput.add(ChkEKG);
        Label lpl = new Label(); lpl.setText("Lainnya :"); lpl.setBounds(290,y,60,23); FormInput.add(lpl);
        TPenunjangLainnya = new TextBox(); TPenunjangLainnya.setBounds(355,y,240,23); FormInput.add(TPenunjangLainnya);

        // ============ ASSESSMENT ============
        y+=35;
        JLabel hass = new JLabel("III. ASSESSMENT"); hass.setFont(new java.awt.Font("Tahoma",1,11)); hass.setBounds(10,y,200,20); FormInput.add(hass);

        y+=25;
        Label ldk = new Label(); ldk.setText("Diagnosa Kerja :"); ldk.setBounds(10,y,110,23); FormInput.add(ldk);
        TDiagnosaKerja = new TextArea();
        JScrollPane scrDK = new JScrollPane(TDiagnosaKerja);
        scrDK.setBounds(125,y,470,50); FormInput.add(scrDK);

        y+=55;
        Label ldb = new Label(); ldb.setText("Diagnosa Banding :"); ldb.setBounds(10,y,120,23); FormInput.add(ldb);
        TDiagnosaBanding = new TextArea();
        JScrollPane scrDB = new JScrollPane(TDiagnosaBanding);
        scrDB.setBounds(135,y,460,50); FormInput.add(scrDB);

        // ============ PLANNING ============
        y+=60;
        JLabel hpl = new JLabel("IV. PLANNING"); hpl.setFont(new java.awt.Font("Tahoma",1,11)); hpl.setBounds(10,y,200,20); FormInput.add(hpl);

        y+=25;
        Label ltr = new Label(); ltr.setText("Terapi :"); ltr.setBounds(10,y,60,23); FormInput.add(ltr);
        TTerapi = new TextArea();
        JScrollPane scrTerapi = new JScrollPane(TTerapi);
        scrTerapi.setBounds(75,y,520,50); FormInput.add(scrTerapi);

        y+=55;
        Label lrt = new Label(); lrt.setText("Rencana Tindakan :"); lrt.setBounds(10,y,120,23); FormInput.add(lrt);
        TRencanaTindakan = new TextArea();
        JScrollPane scrRT = new JScrollPane(TRencanaTindakan);
        scrRT.setBounds(135,y,460,50); FormInput.add(scrRT);

        // ============ EDUKASI ============
        y+=60;
        JLabel hed = new JLabel("V. EDUKASI"); hed.setFont(new java.awt.Font("Tahoma",1,11)); hed.setBounds(10,y,200,20); FormInput.add(hed);

        y+=25;
        ChkEdukasiPerjalanan = new CekBox(); ChkEdukasiPerjalanan.setText("Perjalanan Penyakit"); ChkEdukasiPerjalanan.setBounds(10,y,170,23); FormInput.add(ChkEdukasiPerjalanan);
        ChkEdukasiDiagnosa = new CekBox(); ChkEdukasiDiagnosa.setText("Diagnosa"); ChkEdukasiDiagnosa.setBounds(190,y,100,23); FormInput.add(ChkEdukasiDiagnosa);
        ChkEdukasiPengobatan = new CekBox(); ChkEdukasiPengobatan.setText("Pengobatan"); ChkEdukasiPengobatan.setBounds(300,y,120,23); FormInput.add(ChkEdukasiPengobatan);

        // ============ DISCHARGE PLANNING ============
        y+=35;
        JLabel hdp = new JLabel("VI. DISCHARGE PLANNING"); hdp.setFont(new java.awt.Font("Tahoma",1,11)); hdp.setBounds(10,y,250,20); FormInput.add(hdp);

        y+=25;
        ChkDPPulang = new CekBox(); ChkDPPulang.setText("Pulang"); ChkDPPulang.setBounds(10,y,80,23); FormInput.add(ChkDPPulang);
        ChkDPKontrol = new CekBox(); ChkDPKontrol.setText("Kontrol"); ChkDPKontrol.setBounds(100,y,90,23); FormInput.add(ChkDPKontrol);
        ChkDPRawatInap = new CekBox(); ChkDPRawatInap.setText("Rawat Inap"); ChkDPRawatInap.setBounds(200,y,110,23); FormInput.add(ChkDPRawatInap);
        ChkDPRujuk = new CekBox(); ChkDPRujuk.setText("Rujuk"); ChkDPRujuk.setBounds(320,y,80,23); FormInput.add(ChkDPRujuk);

        // ============ Dokter ============
        y+=35;
        Label ldok = new Label(); ldok.setText("Dokter :"); ldok.setBounds(10,y,60,23); FormInput.add(ldok);
        KdDokter = new TextBox(); KdDokter.setBounds(75,y,80,23); FormInput.add(KdDokter);
        NmDokter = new TextBox(); NmDokter.setBounds(157,y,200,23); FormInput.add(NmDokter);
        BtnDokter = new Button(); BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); BtnDokter.setBounds(359,y,28,23); FormInput.add(BtnDokter);
        BtnDokter.addActionListener(ev->{dokterDlg.emptTeks();dokterDlg.isCek();dokterDlg.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);dokterDlg.setLocationRelativeTo(internalFrame1);dokterDlg.setVisible(true);});
        dokterDlg.addWindowListener(new java.awt.event.WindowAdapter(){@Override public void windowClosed(java.awt.event.WindowEvent e){if(dokterDlg.getTable().getSelectedRow()!=-1){KdDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(),0).toString());NmDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(),1).toString());}}});


        ScrollInput = new ScrollPane(); ScrollInput.setViewportView(FormInput);

        tabPane = new JTabbedPane();
        tabPane.addTab("Input Data", ScrollInput);
        tabPane.addTab("Data", Scroll);

        // Bottom
        panelBawah = new panelisi(); panelBawah.setPreferredSize(new Dimension(44,54)); panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT,5,9));
        BtnSimpan=new Button();BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));BtnSimpan.setText("Simpan");BtnSimpan.setPreferredSize(new Dimension(100,30));
        BtnBatal=new Button();BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));BtnBatal.setText("Baru");BtnBatal.setPreferredSize(new Dimension(100,30));
        BtnHapus=new Button();BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));BtnHapus.setText("Hapus");BtnHapus.setPreferredSize(new Dimension(100,30));
        BtnEdit=new Button();BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));BtnEdit.setText("Ganti");BtnEdit.setPreferredSize(new Dimension(100,30));
        BtnPrint=new Button();BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));BtnPrint.setText("Cetak");BtnPrint.setPreferredSize(new Dimension(100,30));
        BtnKeluar=new Button();BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));BtnKeluar.setText("Keluar");BtnKeluar.setPreferredSize(new Dimension(100,30));
        Label lc=new Label();lc.setText("Tanggal :");lc.setPreferredSize(new Dimension(55,23));
        DTPCari1=new widget.Tanggal();DTPCari1.setDisplayFormat("dd-MM-yyyy");DTPCari1.setPreferredSize(new Dimension(90,23));
        Label sd=new Label();sd.setText("s.d.");sd.setPreferredSize(new Dimension(25,23));
        DTPCari2=new widget.Tanggal();DTPCari2.setDisplayFormat("dd-MM-yyyy");DTPCari2.setPreferredSize(new Dimension(90,23));
        Label lk2=new Label();lk2.setText("Key Word :");lk2.setPreferredSize(new Dimension(70,23));
        TCari=new TextBox();TCari.setPreferredSize(new Dimension(150,23));
        BtnCari=new Button();BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));BtnCari.setPreferredSize(new Dimension(28,23));
        BtnAll=new Button();BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));BtnAll.setPreferredSize(new Dimension(28,23));
        LCount=new Label();LCount.setText("Record : 0");LCount.setPreferredSize(new Dimension(100,23));
        panelBawah.add(BtnSimpan);panelBawah.add(BtnBatal);panelBawah.add(BtnHapus);panelBawah.add(BtnEdit);panelBawah.add(BtnPrint);panelBawah.add(BtnKeluar);
        panelBawah.add(lc);panelBawah.add(DTPCari1);panelBawah.add(sd);panelBawah.add(DTPCari2);panelBawah.add(lk2);panelBawah.add(TCari);panelBawah.add(BtnCari);panelBawah.add(BtnAll);panelBawah.add(LCount);

        BtnKeluar.addActionListener(ev->dispose());
        BtnBatal.addActionListener(ev->emptTeks());
        BtnCari.addActionListener(ev->{tampil();tabPane.setSelectedIndex(1);});
        BtnAll.addActionListener(ev->{TCari.setText("");tampil();tabPane.setSelectedIndex(1);});
        TCari.addKeyListener(new java.awt.event.KeyAdapter(){@Override public void keyPressed(java.awt.event.KeyEvent e){if(e.getKeyCode()==KeyEvent.VK_ENTER){tampil();tabPane.setSelectedIndex(1);}}});

        BtnSimpan.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")){Valid.textKosong(TNoRw,"Pasien");return;}
            if(KdDokter.getText().trim().equals("")){Valid.textKosong(KdDokter,"Dokter");return;}
            String tglS=Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19);
            String relPathGambar = saveCoretImage(TNoRw.getText(), tglS);
            if(Sequel.menyimpantf("asesmen_medis_rj_rscb",
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?",
                "Data",65,new String[]{
                TNoRw.getText(),tglS,
                CmbAnamnesa.getSelectedItem().toString(),TKeluhanUtama.getText(),TRiwayatPenyakitSekarang.getText(),
                TRiwayatPenyakitDahulu.getText(),TRiwayatPenyakitKeluarga.getText(),TRiwayatAlergi.getText(),
                TKeadaanUmum.getText(),CmbKesadaran.getSelectedItem().toString(),TGCS.getText(),
                TTekananDarah.getText(),TNadi.getText(),TSuhu.getText(),TRespirasi.getText(),
                TBeratBadan.getText(),TTinggiBadan.getText(),TLingkarKepala.getText(),CmbStatusGizi.getSelectedItem().toString(),
                ChkKepala.isSelected()?"Ya":"Tidak",TTemuanKepala.getText(),
                ChkMata.isSelected()?"Ya":"Tidak",TTemuanMata.getText(),
                ChkTHT.isSelected()?"Ya":"Tidak",TTemuanTHT.getText(),
                ChkMulut.isSelected()?"Ya":"Tidak",TTemuanMulut.getText(),
                ChkLeher.isSelected()?"Ya":"Tidak",TTemuanLeher.getText(),
                ChkDada.isSelected()?"Ya":"Tidak",TTemuanDada.getText(),
                ChkJantung.isSelected()?"Ya":"Tidak",TTemuanJantung.getText(),
                ChkParu.isSelected()?"Ya":"Tidak",TTemuanParu.getText(),
                ChkPerut.isSelected()?"Ya":"Tidak",TTemuanPerut.getText(),
                ChkUrogenital.isSelected()?"Ya":"Tidak",TTemuanUrogenital.getText(),
                ChkAnggotaGerak.isSelected()?"Ya":"Tidak",TTemuanAnggotaGerak.getText(),
                ChkMuskuloskeletal.isSelected()?"Ya":"Tidak",TTemuanMuskuloskeletal.getText(),
                ChkStatusNeurologis.isSelected()?"Ya":"Tidak",TTemuanStatusNeurologis.getText(),
                ChkStatusLokasi.isSelected()?"Ya":"Tidak",TTemuanStatusLokasi.getText(),
                ChkLab.isSelected()?"Ya":"Tidak",ChkRadiologi.isSelected()?"Ya":"Tidak",
                ChkEKG.isSelected()?"Ya":"Tidak",TPenunjangLainnya.getText(),
                TDiagnosaKerja.getText(),TDiagnosaBanding.getText(),
                TTerapi.getText(),TRencanaTindakan.getText(),
                ChkEdukasiPerjalanan.isSelected()?"Ya":"Tidak",ChkEdukasiDiagnosa.isSelected()?"Ya":"Tidak",
                ChkEdukasiPengobatan.isSelected()?"Ya":"Tidak",
                ChkDPPulang.isSelected()?"Ya":"Tidak",ChkDPKontrol.isSelected()?"Ya":"Tidak",
                ChkDPRawatInap.isSelected()?"Ya":"Tidak",ChkDPRujuk.isSelected()?"Ya":"Tidak",
                KdDokter.getText(),akses.getkode(),relPathGambar
            })){tampil();emptTeks();}
        });

        BtnHapus.addActionListener(ev->{
            if(tbObat.getSelectedRow()!=-1){if(Sequel.queryu2tf("delete from asesmen_medis_rj_rscb where no_rawat=? and tanggal=?",2,new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()
            })){tampil();emptTeks();}}else JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");
        });

        BtnEdit.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")||tbObat.getSelectedRow()==-1){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            if(Sequel.queryu2tf("delete from asesmen_medis_rj_rscb where no_rawat=? and tanggal=?",2,new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()
            })){BtnSimpan.doClick();}
        });

        BtnPrint.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            Map<String,Object> p=new HashMap<>();p.put("namars",akses.getnamars());p.put("alamatrs",akses.getalamatrs());
            p.put("propinsirs",akses.getpropinsirs());p.put("kabupatenrs",akses.getkabupatenrs());
            p.put("kontakrs",akses.getkontakrs());p.put("emailrs",akses.getemailrs());
            p.put("logo",Sequel.cariGambar("select setting.logo from setting"));
            p.put("baseurlweb", getWebBaseUrl()+"/");
            Valid.MyReportqry("rptAsesmenMedisRJRscb.jasper","report","::[ Asesmen Medis Rawat Jalan ]::",
                "select f.*,r.no_rkm_medis,p.nm_pasien,p.jk,concat(r.umurdaftar,' ',r.sttsumur) as umur,d.nm_dokter,pt.nama as nm_petugas "+
                "from asesmen_medis_rj_rscb f inner join reg_periksa r on f.no_rawat=r.no_rawat "+
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis left join dokter d on f.kd_dokter=d.kd_dokter "+
                "left join petugas pt on f.nip=pt.nip "+
                "where f.no_rawat='"+TNoRw.getText()+"' and f.tanggal='"+Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19)+"'",p);
        });

        internalFrame1.add(panelAtas,BorderLayout.PAGE_START);internalFrame1.add(tabPane,BorderLayout.CENTER);internalFrame1.add(panelBawah,BorderLayout.PAGE_END);
        setLayout(new BorderLayout());add(internalFrame1,BorderLayout.CENTER);setDefaultCloseOperation(DISPOSE_ON_CLOSE);setUndecorated(true);
    }

    public void tampil(){
        Valid.tabelKosong(tabMode);
        try{ps=koneksi.prepareStatement("select f.no_rawat,r.no_rkm_medis,p.nm_pasien,f.tanggal,f.anamnesa,f.diagnosa_kerja,d.nm_dokter,pt.nama as nm_petugas "+
            "from asesmen_medis_rj_rscb f inner join reg_periksa r on f.no_rawat=r.no_rawat inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "+
            "left join dokter d on f.kd_dokter=d.kd_dokter left join petugas pt on f.nip=pt.nip "+
            "where f.tanggal between ? and ? and (f.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) order by f.tanggal desc");
        ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
        ps.setString(3,"%"+TCari.getText().trim()+"%");ps.setString(4,"%"+TCari.getText().trim()+"%");ps.setString(5,"%"+TCari.getText().trim()+"%");
        rs=ps.executeQuery();while(rs.next()){tabMode.addRow(new String[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8)});}
        LCount.setText("Record : "+tabMode.getRowCount());}catch(Exception e){System.out.println("Notif : "+e);}finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
    }

    public void getData(){
        if(tbObat.getSelectedRow()!=-1){
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            try{TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()));}catch(Exception e){}
            try{ps=koneksi.prepareStatement("select f.*,d.nm_dokter,pt.nama as nm_petugas from asesmen_medis_rj_rscb f "+
                "left join dokter d on f.kd_dokter=d.kd_dokter left join petugas pt on f.nip=pt.nip "+
                "where f.no_rawat=? and f.tanggal=?");
            ps.setString(1,tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());ps.setString(2,tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            rs=ps.executeQuery();if(rs.next()){
                CmbAnamnesa.setSelectedItem(rs.getString("anamnesa"));
                TKeluhanUtama.setText(rs.getString("keluhan_utama"));TRiwayatPenyakitSekarang.setText(rs.getString("riwayat_penyakit_sekarang"));
                TRiwayatPenyakitDahulu.setText(rs.getString("riwayat_penyakit_dahulu"));TRiwayatPenyakitKeluarga.setText(rs.getString("riwayat_penyakit_keluarga"));
                TRiwayatAlergi.setText(rs.getString("riwayat_alergi"));
                TKeadaanUmum.setText(rs.getString("keadaan_umum"));CmbKesadaran.setSelectedItem(rs.getString("kesadaran"));
                TGCS.setText(rs.getString("gcs"));TTekananDarah.setText(rs.getString("tekanan_darah"));
                TNadi.setText(rs.getString("nadi"));TSuhu.setText(rs.getString("suhu"));TRespirasi.setText(rs.getString("respirasi"));
                TBeratBadan.setText(rs.getString("berat_badan"));TTinggiBadan.setText(rs.getString("tinggi_badan"));
                TLingkarKepala.setText(rs.getString("lingkar_kepala"));CmbStatusGizi.setSelectedItem(rs.getString("status_gizi"));
                ChkKepala.setSelected(rs.getString("kepala_normal").equals("Ya"));TTemuanKepala.setText(rs.getString("kepala_temuan"));
                ChkMata.setSelected(rs.getString("mata_normal").equals("Ya"));TTemuanMata.setText(rs.getString("mata_temuan"));
                ChkTHT.setSelected(rs.getString("tht_normal").equals("Ya"));TTemuanTHT.setText(rs.getString("tht_temuan"));
                ChkMulut.setSelected(rs.getString("mulut_normal").equals("Ya"));TTemuanMulut.setText(rs.getString("mulut_temuan"));
                ChkLeher.setSelected(rs.getString("leher_normal").equals("Ya"));TTemuanLeher.setText(rs.getString("leher_temuan"));
                ChkDada.setSelected(rs.getString("dada_normal").equals("Ya"));TTemuanDada.setText(rs.getString("dada_temuan"));
                ChkJantung.setSelected(rs.getString("jantung_normal").equals("Ya"));TTemuanJantung.setText(rs.getString("jantung_temuan"));
                ChkParu.setSelected(rs.getString("paru_normal").equals("Ya"));TTemuanParu.setText(rs.getString("paru_temuan"));
                ChkPerut.setSelected(rs.getString("perut_normal").equals("Ya"));TTemuanPerut.setText(rs.getString("perut_temuan"));
                ChkUrogenital.setSelected(rs.getString("urogenital_normal").equals("Ya"));TTemuanUrogenital.setText(rs.getString("urogenital_temuan"));
                ChkAnggotaGerak.setSelected(rs.getString("anggota_gerak_normal").equals("Ya"));TTemuanAnggotaGerak.setText(rs.getString("anggota_gerak_temuan"));
                ChkMuskuloskeletal.setSelected(rs.getString("muskuloskeletal_normal").equals("Ya"));TTemuanMuskuloskeletal.setText(rs.getString("muskuloskeletal_temuan"));
                ChkStatusNeurologis.setSelected(rs.getString("status_neurologis_normal").equals("Ya"));TTemuanStatusNeurologis.setText(rs.getString("status_neurologis_temuan"));
                ChkStatusLokasi.setSelected(rs.getString("status_lokasi_normal").equals("Ya"));TTemuanStatusLokasi.setText(rs.getString("status_lokasi_temuan"));
                ChkLab.setSelected(rs.getString("penunjang_lab").equals("Ya"));ChkRadiologi.setSelected(rs.getString("penunjang_radiologi").equals("Ya"));
                ChkEKG.setSelected(rs.getString("penunjang_ekg").equals("Ya"));TPenunjangLainnya.setText(rs.getString("penunjang_lainnya"));
                TDiagnosaKerja.setText(rs.getString("diagnosa_kerja"));TDiagnosaBanding.setText(rs.getString("diagnosa_banding"));
                TTerapi.setText(rs.getString("terapi"));TRencanaTindakan.setText(rs.getString("rencana_tindakan"));
                ChkEdukasiPerjalanan.setSelected(rs.getString("edukasi_perjalanan").equals("Ya"));
                ChkEdukasiDiagnosa.setSelected(rs.getString("edukasi_diagnosa").equals("Ya"));
                ChkEdukasiPengobatan.setSelected(rs.getString("edukasi_pengobatan").equals("Ya"));
                ChkDPPulang.setSelected(rs.getString("dp_pulang").equals("Ya"));ChkDPKontrol.setSelected(rs.getString("dp_kontrol").equals("Ya"));
                ChkDPRawatInap.setSelected(rs.getString("dp_rawat_inap").equals("Ya"));ChkDPRujuk.setSelected(rs.getString("dp_rujuk").equals("Ya"));
                KdDokter.setText(rs.getString("kd_dokter"));NmDokter.setText(rs.getString("nm_dokter"));
                loadCoretImage(rs.getString("gambar_anatomi"));
            }}catch(Exception e){System.out.println("Notif getData : "+e);}finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
        }
    }

    public void emptTeks(){TglAsuhan.setDate(new Date());
        CmbAnamnesa.setSelectedIndex(0);TKeluhanUtama.setText("");TRiwayatPenyakitSekarang.setText("");
        TRiwayatPenyakitDahulu.setText("");TRiwayatPenyakitKeluarga.setText("");TRiwayatAlergi.setText("");
        TKeadaanUmum.setText("");CmbKesadaran.setSelectedIndex(0);TGCS.setText("");TTekananDarah.setText("");TNadi.setText("");TSuhu.setText("");TRespirasi.setText("");
        TBeratBadan.setText("");TTinggiBadan.setText("");TLingkarKepala.setText("");CmbStatusGizi.setSelectedIndex(0);
        ChkKepala.setSelected(true);TTemuanKepala.setText("");ChkMata.setSelected(true);TTemuanMata.setText("");
        ChkTHT.setSelected(true);TTemuanTHT.setText("");ChkMulut.setSelected(true);TTemuanMulut.setText("");
        ChkLeher.setSelected(true);TTemuanLeher.setText("");ChkDada.setSelected(true);TTemuanDada.setText("");
        ChkJantung.setSelected(true);TTemuanJantung.setText("");ChkParu.setSelected(true);TTemuanParu.setText("");
        ChkPerut.setSelected(true);TTemuanPerut.setText("");ChkUrogenital.setSelected(true);TTemuanUrogenital.setText("");
        ChkAnggotaGerak.setSelected(true);TTemuanAnggotaGerak.setText("");ChkMuskuloskeletal.setSelected(true);TTemuanMuskuloskeletal.setText("");
        ChkStatusNeurologis.setSelected(true);TTemuanStatusNeurologis.setText("");ChkStatusLokasi.setSelected(true);TTemuanStatusLokasi.setText("");
        ChkLab.setSelected(false);ChkRadiologi.setSelected(false);ChkEKG.setSelected(false);TPenunjangLainnya.setText("");
        TDiagnosaKerja.setText("");TDiagnosaBanding.setText("");TTerapi.setText("");TRencanaTindakan.setText("");
        ChkEdukasiPerjalanan.setSelected(false);ChkEdukasiDiagnosa.setSelected(false);ChkEdukasiPengobatan.setSelected(false);
        ChkDPPulang.setSelected(false);ChkDPKontrol.setSelected(false);ChkDPRawatInap.setSelected(false);ChkDPRujuk.setSelected(false);
        KdDokter.setText("");NmDokter.setText("");
        if(panelCoret!=null){panelCoret.clear();panelCoret.setBackgroundImage(new javax.swing.ImageIcon(getClass().getResource("/picture/awal_medis_rscb.png")).getImage());}}
    public void setNoRm(String n,String m,String p){TNoRw.setText(n);TNoRM.setText(m);TPasien.setText(p);}
    public void setNoRm(String n,Date t){TNoRw.setText(n);TglAsuhan.setDate(t);TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?",n));TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?",TNoRM.getText()));}
    public void isCek(){BtnSimpan.setEnabled(akses.getrujukan_keluar());BtnHapus.setEnabled(akses.getrujukan_keluar());BtnEdit.setEnabled(akses.getrujukan_keluar());BtnPrint.setEnabled(akses.getrujukan_keluar());}

    private static final String SUBDIR_GAMBAR = "asesmen_medis_rj_rscb";

    private String getWebBaseUrl(){
        return "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB();
    }

    private String saveCoretImage(String noRawat, String tglS){
        try{
            if(panelCoret==null||panelCoret.getWidth()<=0||panelCoret.getHeight()<=0) return "";
            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(panelCoret.getWidth(), panelCoret.getHeight(), java.awt.image.BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g = img.createGraphics();
            g.setColor(Color.WHITE); g.fillRect(0,0,img.getWidth(),img.getHeight());
            panelCoret.paint(g);
            g.dispose();
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            javax.imageio.ImageIO.write(img, "PNG", baos);
            byte[] data = baos.toByteArray();
            String safeNoRw = noRawat.replace("/","-").replace("\\","-");
            String safeTgl = tglS.replace(":","").replace("-","").replace(" ","_");
            String fileName = safeNoRw + "_" + safeTgl + ".png";
            String rel = SUBDIR_GAMBAR + "/" + fileName;
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost(getWebBaseUrl() + "/upload.php?doc=" + SUBDIR_GAMBAR + "/");
            postRequest.addHeader("X-Upload-Token", fungsi.koneksiDB.TOKENUNGGAH());
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart("file", new ByteArrayBody(data, fileName));
            postRequest.setEntity(reqEntity);
            httpClient.execute(postRequest);
            return rel;
        }catch(Exception e){System.out.println("Notif saveCoret : "+e);return "";}
    }

    private void loadCoretImage(String relPath){
        if(panelCoret==null) return;
        panelCoret.clear();
        java.awt.Image bg = null;
        if(relPath!=null && !relPath.trim().isEmpty()){
            try{
                java.net.URL url = new java.net.URL(getWebBaseUrl() + "/" + relPath);
                bg = javax.imageio.ImageIO.read(url);
            }catch(Exception e){System.out.println("Notif loadCoret : "+e);}
        }
        if(bg==null) bg = new javax.swing.ImageIcon(getClass().getResource("/picture/awal_medis_rscb.png")).getImage();
        panelCoret.setBackgroundImage(bg);
    }

    private static class CoretPanel extends JPanel {
        private java.awt.Image bg;
        private final java.util.List<java.util.List<java.awt.Point>> strokes = new java.util.ArrayList<>();
        private java.util.List<java.awt.Point> current;

        CoretPanel(java.awt.Image bg){
            this.bg = bg;
            setOpaque(true);
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.CROSSHAIR_CURSOR));
            java.awt.event.MouseAdapter ma = new java.awt.event.MouseAdapter(){
                @Override public void mousePressed(java.awt.event.MouseEvent e){
                    current = new java.util.ArrayList<>();
                    current.add(e.getPoint());
                    strokes.add(current);
                    repaint();
                }
                @Override public void mouseDragged(java.awt.event.MouseEvent e){
                    if(current!=null){current.add(e.getPoint());repaint();}
                }
                @Override public void mouseReleased(java.awt.event.MouseEvent e){current = null;}
            };
            addMouseListener(ma);
            addMouseMotionListener(ma);
        }

        void clear(){strokes.clear();repaint();}

        void setBackgroundImage(java.awt.Image img){this.bg=img;repaint();}

        @Override protected void paintComponent(java.awt.Graphics g){
            super.paintComponent(g);
            if(bg!=null) g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.RED);
            g2.setStroke(new java.awt.BasicStroke(2f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));
            for(java.util.List<java.awt.Point> s : strokes){
                for(int i=1;i<s.size();i++){
                    java.awt.Point a = s.get(i-1), b = s.get(i);
                    g2.drawLine(a.x, a.y, b.x, b.y);
                }
            }
            g2.dispose();
        }
    }
}
