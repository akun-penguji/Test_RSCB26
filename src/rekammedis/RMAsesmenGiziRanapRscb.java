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
import widget.*;

public class RMAsesmenGiziRanapRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll, BtnPetugas;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    // Antropometri
    private TextBox TBB, TTB, TLILA, TIMT, TStatusGizi, TLilaU;
    // Fisik Klinis
    private TextBox TTekananDarah, TRespiratori, TNadi, TSuhu;
    // Biokimia
    private TextBox TGlukosaDarah, TUreum, TKreatinin, THb, TKolesterol;
    // Riwayat Makan
    private TextBox TAlergiMakanan;
    // Intervensi Gizi
    private TextBox TEnergi, TKarbohidrat, TLemak, TProtein, TBentukMakanan, TCaraPemberian;
    // Petugas
    private TextBox NIP, NamaPetugas;
    // TextAreas
    private widget.TextArea TDiagnosaMedis, TRiwayatPersonal, TKesanUmum;
    private widget.TextArea TBiokimiaLain, TPolaMakan;
    private widget.TextArea TDiagnosisGizi, TTujuanDiet, TJenisDiet, TMonitoringEvaluasi;
    // CekBox masalah GI
    private CekBox ChkMual, ChkMuntah, ChkDiare, ChkKonstipasi, ChkAnoreksia, ChkSulitMenelan, ChkSesak, ChkOedema;
    private Label LCount;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);

    private widget.Table tbObat;
    private javax.swing.table.DefaultTableModel tabMode;
    private ScrollPane Scroll, ScrollInput;
    private CekBox ChkInput;
    private PanelBiasa FormInput;
    private JPanel PanelInput;
    public widget.editorpane LoadHTML;

    public RMAsesmenGiziRanapRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initTables();
        initComponents();
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initTables() {
        try {
            koneksi.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS asesmen_gizi_ranap_rscb (" +
                "no_rawat varchar(20) NOT NULL," +
                "tanggal varchar(30) NULL," +
                "diagnosa_medis text NULL," +
                "riwayat_personal text NULL," +
                "bb varchar(20) NULL," +
                "tb varchar(20) NULL," +
                "lila varchar(20) NULL," +
                "imt varchar(20) NULL," +
                "status_gizi varchar(50) NULL," +
                "lila_u varchar(20) NULL," +
                "kesan_umum text NULL," +
                "tekanan_darah varchar(20) NULL," +
                "respiratori varchar(20) NULL," +
                "nadi varchar(20) NULL," +
                "suhu varchar(20) NULL," +
                "masalah_mual enum('Ya','Tidak') DEFAULT 'Tidak'," +
                "masalah_muntah enum('Ya','Tidak') DEFAULT 'Tidak'," +
                "masalah_diare enum('Ya','Tidak') DEFAULT 'Tidak'," +
                "masalah_konstipasi enum('Ya','Tidak') DEFAULT 'Tidak'," +
                "masalah_anoreksia enum('Ya','Tidak') DEFAULT 'Tidak'," +
                "masalah_sulit_menelan enum('Ya','Tidak') DEFAULT 'Tidak'," +
                "masalah_sesak enum('Ya','Tidak') DEFAULT 'Tidak'," +
                "masalah_oedema enum('Ya','Tidak') DEFAULT 'Tidak'," +
                "glukosa_darah varchar(20) NULL," +
                "ureum varchar(20) NULL," +
                "kreatinin varchar(20) NULL," +
                "hb varchar(20) NULL," +
                "kolesterol varchar(20) NULL," +
                "biokimia_lain text NULL," +
                "alergi_makanan varchar(200) NULL," +
                "pola_makan text NULL," +
                "diagnosis_gizi text NULL," +
                "tujuan_diet text NULL," +
                "jenis_diet text NULL," +
                "energi varchar(20) NULL," +
                "karbohidrat varchar(20) NULL," +
                "lemak varchar(20) NULL," +
                "protein varchar(20) NULL," +
                "bentuk_makanan varchar(100) NULL," +
                "cara_pemberian varchar(100) NULL," +
                "monitoring_evaluasi text NULL," +
                "nip varchar(20) NULL," +
                "PRIMARY KEY (no_rawat, tanggal(30))" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
        } catch (Exception e) { System.out.println("Init tables asesmen gizi: " + e); }
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(240,245,235)),
            "::[ Assessment Awal Asuhan Gizi Rawat Inap ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
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
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","Diagnosa Medis","Status Gizi","Jenis Diet","Petugas"}
        ) { @Override public boolean isCellEditable(int r,int c){return false;} };
        tbObat.setModel(tabMode);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override public void mouseClicked(java.awt.event.MouseEvent e){if(tabMode.getRowCount()!=0) getData();}
        });
        JPopupMenu pop = new JPopupMenu();
        JMenuItem mnCetak = new JMenuItem("Cetak Asesmen Gizi Rawat Inap");
        mnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetak.addActionListener(evt -> BtnPrint.doClick());
        pop.add(mnCetak);
        tbObat.setComponentPopupMenu(pop);
        Scroll.setViewportView(tbObat);

        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(880,1200));
        FormInput.setLayout(null);

        int y = 10;
        JLabel h = new JLabel("ASSESSMENT AWAL ASUHAN GIZI RAWAT INAP");
        h.setFont(new java.awt.Font("Tahoma",1,12)); h.setBounds(10,y,450,20); FormInput.add(h);
        JLabel hk = new JLabel("RM.GZ.09.002.2019"); hk.setFont(new java.awt.Font("Tahoma",0,8)); hk.setBounds(700,y,150,20); FormInput.add(hk);

        y+=30;
        Label lt = new Label(); lt.setText("Tanggal :"); lt.setBounds(10,y,65,23); FormInput.add(lt);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TglAsuhan.setBounds(80,y,130,23); FormInput.add(TglAsuhan);

        // === ANAMNESIS ===
        y+=35;
        JLabel ha = new JLabel("A. ANAMNESIS"); ha.setFont(new java.awt.Font("Tahoma",1,11)); ha.setBounds(10,y,200,20); FormInput.add(ha);

        y+=25;
        Label ldm = new Label(); ldm.setText("Diagnosa Medis :"); ldm.setBounds(10,y,110,23); FormInput.add(ldm);
        TDiagnosaMedis = new widget.TextArea(); TDiagnosaMedis.setBorder(BorderFactory.createLineBorder(Color.BLACK)); TDiagnosaMedis.setBounds(125,y,540,45); FormInput.add(TDiagnosaMedis);

        y+=52;
        Label lrp = new Label(); lrp.setText("Riwayat Personal :"); lrp.setBounds(10,y,115,23); FormInput.add(lrp);
        TRiwayatPersonal = new widget.TextArea(); TRiwayatPersonal.setBorder(BorderFactory.createLineBorder(Color.BLACK)); TRiwayatPersonal.setBounds(130,y,535,45); FormInput.add(TRiwayatPersonal);

        // === ANTROPOMETRI ===
        y+=58;
        JLabel hb = new JLabel("B. ANTROPOMETRI"); hb.setFont(new java.awt.Font("Tahoma",1,11)); hb.setBounds(10,y,200,20); FormInput.add(hb);

        y+=25;
        Label lbb = new Label(); lbb.setText("BB (kg) :"); lbb.setBounds(10,y,60,23); FormInput.add(lbb);
        TBB = new TextBox(); TBB.setBounds(75,y,60,23); FormInput.add(TBB);
        Label ltb = new Label(); ltb.setText("TB (cm) :"); ltb.setBounds(150,y,60,23); FormInput.add(ltb);
        TTB = new TextBox(); TTB.setBounds(215,y,60,23); FormInput.add(TTB);
        Label llila = new Label(); llila.setText("LiLA (cm) :"); llila.setBounds(290,y,70,23); FormInput.add(llila);
        TLILA = new TextBox(); TLILA.setBounds(365,y,60,23); FormInput.add(TLILA);

        y+=28;
        Label limt = new Label(); limt.setText("IMT :"); limt.setBounds(10,y,35,23); FormInput.add(limt);
        TIMT = new TextBox(); TIMT.setBounds(50,y,60,23); FormInput.add(TIMT);
        Label lsg = new Label(); lsg.setText("Status Gizi :"); lsg.setBounds(130,y,80,23); FormInput.add(lsg);
        TStatusGizi = new TextBox(); TStatusGizi.setBounds(215,y,150,23); FormInput.add(TStatusGizi);
        Label llu = new Label(); llu.setText("LiLA/U :"); llu.setBounds(380,y,55,23); FormInput.add(llu);
        TLilaU = new TextBox(); TLilaU.setBounds(440,y,80,23); FormInput.add(TLilaU);

        // === FISIK / KLINIS ===
        y+=38;
        JLabel hc = new JLabel("C. FISIK / KLINIS"); hc.setFont(new java.awt.Font("Tahoma",1,11)); hc.setBounds(10,y,200,20); FormInput.add(hc);

        y+=25;
        Label lku = new Label(); lku.setText("Kesan Umum :"); lku.setBounds(10,y,90,23); FormInput.add(lku);
        TKesanUmum = new widget.TextArea(); TKesanUmum.setBorder(BorderFactory.createLineBorder(Color.BLACK)); TKesanUmum.setBounds(105,y,560,45); FormInput.add(TKesanUmum);

        y+=52;
        Label ltd = new Label(); ltd.setText("TD :"); ltd.setBounds(10,y,30,23); FormInput.add(ltd);
        TTekananDarah = new TextBox(); TTekananDarah.setBounds(45,y,80,23); FormInput.add(TTekananDarah);
        Label lresp = new Label(); lresp.setText("Resp :"); lresp.setBounds(140,y,40,23); FormInput.add(lresp);
        TRespiratori = new TextBox(); TRespiratori.setBounds(185,y,60,23); FormInput.add(TRespiratori);
        Label lnadi = new Label(); lnadi.setText("Nadi :"); lnadi.setBounds(260,y,40,23); FormInput.add(lnadi);
        TNadi = new TextBox(); TNadi.setBounds(305,y,60,23); FormInput.add(TNadi);
        Label lsuhu = new Label(); lsuhu.setText("Suhu :"); lsuhu.setBounds(380,y,40,23); FormInput.add(lsuhu);
        TSuhu = new TextBox(); TSuhu.setBounds(425,y,60,23); FormInput.add(TSuhu);

        // Masalah GI
        y+=32;
        JLabel hgi = new JLabel("Masalah GI :"); hgi.setFont(new java.awt.Font("Tahoma",0,11)); hgi.setBounds(10,y,80,20); FormInput.add(hgi);
        y+=22;
        ChkMual = new CekBox(); ChkMual.setText("Mual"); ChkMual.setBounds(30,y,80,23); FormInput.add(ChkMual);
        ChkMuntah = new CekBox(); ChkMuntah.setText("Muntah"); ChkMuntah.setBounds(115,y,90,23); FormInput.add(ChkMuntah);
        ChkDiare = new CekBox(); ChkDiare.setText("Diare"); ChkDiare.setBounds(210,y,80,23); FormInput.add(ChkDiare);
        ChkKonstipasi = new CekBox(); ChkKonstipasi.setText("Konstipasi"); ChkKonstipasi.setBounds(295,y,100,23); FormInput.add(ChkKonstipasi);
        y+=25;
        ChkAnoreksia = new CekBox(); ChkAnoreksia.setText("Anoreksia"); ChkAnoreksia.setBounds(30,y,100,23); FormInput.add(ChkAnoreksia);
        ChkSulitMenelan = new CekBox(); ChkSulitMenelan.setText("Sulit Menelan"); ChkSulitMenelan.setBounds(135,y,115,23); FormInput.add(ChkSulitMenelan);
        ChkSesak = new CekBox(); ChkSesak.setText("Sesak"); ChkSesak.setBounds(255,y,80,23); FormInput.add(ChkSesak);
        ChkOedema = new CekBox(); ChkOedema.setText("Oedema"); ChkOedema.setBounds(340,y,90,23); FormInput.add(ChkOedema);

        // === BIOKIMIA ===
        y+=38;
        JLabel hd = new JLabel("D. BIOKIMIA"); hd.setFont(new java.awt.Font("Tahoma",1,11)); hd.setBounds(10,y,200,20); FormInput.add(hd);

        y+=25;
        Label lgd = new Label(); lgd.setText("Glukosa Darah :"); lgd.setBounds(10,y,100,23); FormInput.add(lgd);
        TGlukosaDarah = new TextBox(); TGlukosaDarah.setBounds(115,y,80,23); FormInput.add(TGlukosaDarah);
        Label lur = new Label(); lur.setText("Ureum :"); lur.setBounds(210,y,50,23); FormInput.add(lur);
        TUreum = new TextBox(); TUreum.setBounds(265,y,80,23); FormInput.add(TUreum);
        Label lkr = new Label(); lkr.setText("Kreatinin :"); lkr.setBounds(360,y,65,23); FormInput.add(lkr);
        TKreatinin = new TextBox(); TKreatinin.setBounds(430,y,80,23); FormInput.add(TKreatinin);

        y+=28;
        Label lhb = new Label(); lhb.setText("Hb :"); lhb.setBounds(10,y,30,23); FormInput.add(lhb);
        THb = new TextBox(); THb.setBounds(45,y,80,23); FormInput.add(THb);
        Label lkol = new Label(); lkol.setText("Kolesterol :"); lkol.setBounds(140,y,70,23); FormInput.add(lkol);
        TKolesterol = new TextBox(); TKolesterol.setBounds(215,y,80,23); FormInput.add(TKolesterol);

        y+=28;
        Label lbl = new Label(); lbl.setText("Lain-lain :"); lbl.setBounds(10,y,65,23); FormInput.add(lbl);
        TBiokimiaLain = new widget.TextArea(); TBiokimiaLain.setBorder(BorderFactory.createLineBorder(Color.BLACK)); TBiokimiaLain.setBounds(80,y,770,45); FormInput.add(TBiokimiaLain);

        // === RIWAYAT MAKAN ===
        y+=58;
        JLabel he = new JLabel("E. RIWAYAT MAKAN"); he.setFont(new java.awt.Font("Tahoma",1,11)); he.setBounds(10,y,200,20); FormInput.add(he);

        y+=25;
        Label lam = new Label(); lam.setText("Alergi Makanan :"); lam.setBounds(10,y,105,23); FormInput.add(lam);
        TAlergiMakanan = new TextBox(); TAlergiMakanan.setBounds(120,y,350,23); FormInput.add(TAlergiMakanan);

        y+=28;
        Label lpm = new Label(); lpm.setText("Pola Makan :"); lpm.setBounds(10,y,85,23); FormInput.add(lpm);
        TPolaMakan = new widget.TextArea(); TPolaMakan.setBorder(BorderFactory.createLineBorder(Color.BLACK)); TPolaMakan.setBounds(100,y,750,45); FormInput.add(TPolaMakan);

        // === DIAGNOSIS GIZI ===
        y+=58;
        JLabel hf = new JLabel("F. DIAGNOSIS GIZI"); hf.setFont(new java.awt.Font("Tahoma",1,11)); hf.setBounds(10,y,200,20); FormInput.add(hf);

        y+=25;
        TDiagnosisGizi = new widget.TextArea(); TDiagnosisGizi.setBorder(BorderFactory.createLineBorder(Color.BLACK)); TDiagnosisGizi.setBounds(10,y,840,50); FormInput.add(TDiagnosisGizi);

        // === INTERVENSI GIZI ===
        y+=63;
        JLabel hg = new JLabel("G. INTERVENSI GIZI"); hg.setFont(new java.awt.Font("Tahoma",1,11)); hg.setBounds(10,y,200,20); FormInput.add(hg);

        y+=25;
        Label ltjd = new Label(); ltjd.setText("Tujuan Diet :"); ltjd.setBounds(10,y,80,23); FormInput.add(ltjd);
        TTujuanDiet = new widget.TextArea(); TTujuanDiet.setBorder(BorderFactory.createLineBorder(Color.BLACK)); TTujuanDiet.setBounds(95,y,755,45); FormInput.add(TTujuanDiet);

        y+=52;
        Label ljd = new Label(); ljd.setText("Jenis Diet :"); ljd.setBounds(10,y,75,23); FormInput.add(ljd);
        TJenisDiet = new widget.TextArea(); TJenisDiet.setBorder(BorderFactory.createLineBorder(Color.BLACK)); TJenisDiet.setBounds(90,y,760,45); FormInput.add(TJenisDiet);

        y+=52;
        Label len = new Label(); len.setText("Energi :"); len.setBounds(10,y,55,23); FormInput.add(len);
        TEnergi = new TextBox(); TEnergi.setBounds(70,y,80,23); FormInput.add(TEnergi);
        Label lkh = new Label(); lkh.setText("KH :"); lkh.setBounds(165,y,30,23); FormInput.add(lkh);
        TKarbohidrat = new TextBox(); TKarbohidrat.setBounds(200,y,80,23); FormInput.add(TKarbohidrat);
        Label llm = new Label(); llm.setText("Lemak :"); llm.setBounds(295,y,50,23); FormInput.add(llm);
        TLemak = new TextBox(); TLemak.setBounds(350,y,80,23); FormInput.add(TLemak);
        Label lpr = new Label(); lpr.setText("Protein :"); lpr.setBounds(445,y,55,23); FormInput.add(lpr);
        TProtein = new TextBox(); TProtein.setBounds(505,y,80,23); FormInput.add(TProtein);

        y+=28;
        Label lbm = new Label(); lbm.setText("Bentuk Makanan :"); lbm.setBounds(10,y,110,23); FormInput.add(lbm);
        TBentukMakanan = new TextBox(); TBentukMakanan.setBounds(125,y,200,23); FormInput.add(TBentukMakanan);
        Label lcp = new Label(); lcp.setText("Cara Pemberian :"); lcp.setBounds(340,y,105,23); FormInput.add(lcp);
        TCaraPemberian = new TextBox(); TCaraPemberian.setBounds(450,y,200,23); FormInput.add(TCaraPemberian);

        // === MONITORING ===
        y+=38;
        JLabel hh = new JLabel("H. MONITORING & EVALUASI"); hh.setFont(new java.awt.Font("Tahoma",1,11)); hh.setBounds(10,y,250,20); FormInput.add(hh);

        y+=25;
        TMonitoringEvaluasi = new widget.TextArea(); TMonitoringEvaluasi.setBorder(BorderFactory.createLineBorder(Color.BLACK)); TMonitoringEvaluasi.setBounds(10,y,840,50); FormInput.add(TMonitoringEvaluasi);

        // === PETUGAS ===
        y+=63;
        Label lp = new Label(); lp.setText("Nutrisionis :"); lp.setBounds(10,y,80,23); FormInput.add(lp);
        Label lpk = new Label(); lpk.setText("Kode Petugas :"); lpk.setBounds(95,y,95,23); FormInput.add(lpk);
        NIP = new TextBox(); NIP.setBounds(193,y,80,23); FormInput.add(NIP);
        Label lpn = new Label(); lpn.setText("Nama Petugas :"); lpn.setBounds(280,y,95,23); FormInput.add(lpn);
        NamaPetugas = new TextBox(); NamaPetugas.setBounds(378,y,200,23); FormInput.add(NamaPetugas);
        BtnPetugas = new Button(); BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); BtnPetugas.setBounds(580,y,28,23); FormInput.add(BtnPetugas);
        BtnPetugas.addActionListener(ev->{petugasDlg.emptTeks();petugasDlg.isCek();petugasDlg.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);petugasDlg.setLocationRelativeTo(internalFrame1);petugasDlg.setVisible(true);});
        petugasDlg.addWindowListener(new java.awt.event.WindowAdapter(){@Override public void windowClosed(java.awt.event.WindowEvent e){if(petugasDlg.getTable().getSelectedRow()!=-1){NIP.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(),0).toString());NamaPetugas.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(),1).toString());}}});

        // Toggle
        ChkInput = new CekBox(); ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setText(".: Input Data"); ChkInput.setSelected(true); ChkInput.setBorderPainted(true); ChkInput.setBorderPaintedFlat(true); ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(SwingConstants.LEFT); ChkInput.setPreferredSize(new Dimension(192,20));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.addActionListener(ev->isForm());

        ScrollInput = new ScrollPane(); ScrollInput.setViewportView(FormInput); ScrollInput.setPreferredSize(new Dimension(880,420));
        PanelInput = new JPanel(new BorderLayout()); PanelInput.setOpaque(false); PanelInput.setPreferredSize(new Dimension(WIDTH,440));
        PanelInput.add(ScrollInput, BorderLayout.CENTER); PanelInput.add(ChkInput, BorderLayout.PAGE_END);

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
        Label lkw=new Label();lkw.setText("Key Word :");lkw.setPreferredSize(new Dimension(70,23));
        TCari=new TextBox();TCari.setPreferredSize(new Dimension(150,23));
        BtnCari=new Button();BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));BtnCari.setPreferredSize(new Dimension(28,23));
        BtnAll=new Button();BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));BtnAll.setPreferredSize(new Dimension(28,23));
        LCount=new Label();LCount.setText("Record : 0");LCount.setPreferredSize(new Dimension(100,23));
        panelBawah.add(BtnSimpan);panelBawah.add(BtnBatal);panelBawah.add(BtnHapus);panelBawah.add(BtnEdit);panelBawah.add(BtnPrint);panelBawah.add(BtnKeluar);
        panelBawah.add(lc);panelBawah.add(DTPCari1);panelBawah.add(sd);panelBawah.add(DTPCari2);panelBawah.add(lkw);panelBawah.add(TCari);panelBawah.add(BtnCari);panelBawah.add(BtnAll);panelBawah.add(LCount);

        BtnKeluar.addActionListener(ev->dispose());
        BtnBatal.addActionListener(ev->emptTeks());
        BtnCari.addActionListener(ev->tampil());
        BtnAll.addActionListener(ev->{TCari.setText("");tampil();});
        TCari.addKeyListener(new java.awt.event.KeyAdapter(){@Override public void keyPressed(java.awt.event.KeyEvent e){if(e.getKeyCode()==KeyEvent.VK_ENTER)tampil();}});

        BtnSimpan.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")){Valid.textKosong(TNoRw,"Pasien");return;}
            if(NIP.getText().trim().equals("")){Valid.textKosong(NIP,"Nutrisionis");return;}
            String tglS=Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19);
            if(Sequel.menyimpantf("asesmen_gizi_ranap_rscb",
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?","Data",42,new String[]{
                TNoRw.getText(),tglS,
                TDiagnosaMedis.getText(),TRiwayatPersonal.getText(),
                TBB.getText(),TTB.getText(),TLILA.getText(),TIMT.getText(),TStatusGizi.getText(),TLilaU.getText(),
                TKesanUmum.getText(),TTekananDarah.getText(),TRespiratori.getText(),TNadi.getText(),TSuhu.getText(),
                ChkMual.isSelected()?"Ya":"Tidak",ChkMuntah.isSelected()?"Ya":"Tidak",
                ChkDiare.isSelected()?"Ya":"Tidak",ChkKonstipasi.isSelected()?"Ya":"Tidak",
                ChkAnoreksia.isSelected()?"Ya":"Tidak",ChkSulitMenelan.isSelected()?"Ya":"Tidak",
                ChkSesak.isSelected()?"Ya":"Tidak",ChkOedema.isSelected()?"Ya":"Tidak",
                TGlukosaDarah.getText(),TUreum.getText(),TKreatinin.getText(),THb.getText(),TKolesterol.getText(),
                TBiokimiaLain.getText(),
                TAlergiMakanan.getText(),TPolaMakan.getText(),
                TDiagnosisGizi.getText(),
                TTujuanDiet.getText(),TJenisDiet.getText(),
                TEnergi.getText(),TKarbohidrat.getText(),TLemak.getText(),TProtein.getText(),
                TBentukMakanan.getText(),TCaraPemberian.getText(),
                TMonitoringEvaluasi.getText(),
                NIP.getText()
            })){tampil();emptTeks();}
        });

        BtnHapus.addActionListener(ev->{
            if(tbObat.getSelectedRow()!=-1){if(Sequel.queryu2tf("delete from asesmen_gizi_ranap_rscb where no_rawat=? and tanggal=?",2,new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()
            })){tampil();emptTeks();}}else JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");
        });

        BtnEdit.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")||tbObat.getSelectedRow()==-1){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            if(Sequel.queryu2tf("delete from asesmen_gizi_ranap_rscb where no_rawat=? and tanggal=?",2,new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()
            })){BtnSimpan.doClick();}
        });

        BtnPrint.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            Map<String,Object> p=new HashMap<>();p.put("namars",akses.getnamars());p.put("alamatrs",akses.getalamatrs());
            p.put("propinsirs",akses.getpropinsirs());p.put("kabupatenrs",akses.getkabupatenrs());
            p.put("kontakrs",akses.getkontakrs());p.put("emailrs",akses.getemailrs());
            p.put("logo",Sequel.cariGambar("select setting.logo from setting"));
            Valid.MyReportqry("rptAsesmenGiziRanapRscb.jasper","report","::[ Assessment Awal Asuhan Gizi Rawat Inap ]::",
                "select f.*,r.no_rkm_medis,p.nm_pasien,p.jk,concat(r.umurdaftar,' ',r.sttsumur) as umur,pt.nama as nm_petugas "+
                "from asesmen_gizi_ranap_rscb f inner join reg_periksa r on f.no_rawat=r.no_rawat "+
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "+
                "left join petugas pt on f.nip=pt.nip "+
                "where f.no_rawat='"+TNoRw.getText()+"' and f.tanggal='"+Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19)+"'",p);
        });

        InternalFrame ifd=new InternalFrame();ifd.setLayout(new BorderLayout());ifd.setBackground(new Color(255,255,255));
        ifd.add(PanelInput,BorderLayout.NORTH);ifd.add(Scroll,BorderLayout.CENTER);
        internalFrame1.add(panelAtas,BorderLayout.PAGE_START);internalFrame1.add(ifd,BorderLayout.CENTER);internalFrame1.add(panelBawah,BorderLayout.PAGE_END);
        setLayout(new BorderLayout());add(internalFrame1,BorderLayout.CENTER);setDefaultCloseOperation(DISPOSE_ON_CLOSE);setUndecorated(true);
    }

    private void isForm(){if(ChkInput.isSelected()){ChkInput.setVisible(false);PanelInput.setPreferredSize(new Dimension(WIDTH,550));FormInput.setVisible(true);ChkInput.setVisible(true);}else{ChkInput.setVisible(false);PanelInput.setPreferredSize(new Dimension(WIDTH,20));FormInput.setVisible(false);ChkInput.setVisible(true);}}

    public void tampil(){
        Valid.tabelKosong(tabMode);
        try{ps=koneksi.prepareStatement("select f.no_rawat,r.no_rkm_medis,p.nm_pasien,f.tanggal,f.diagnosa_medis,f.status_gizi,f.jenis_diet,pt.nama "+
            "from asesmen_gizi_ranap_rscb f inner join reg_periksa r on f.no_rawat=r.no_rawat inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "+
            "left join petugas pt on f.nip=pt.nip "+
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
            try{ps=koneksi.prepareStatement("select f.*,pt.nama as nm_petugas from asesmen_gizi_ranap_rscb f left join petugas pt on f.nip=pt.nip where f.no_rawat=? and f.tanggal=?");
            ps.setString(1,tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());ps.setString(2,tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            rs=ps.executeQuery();if(rs.next()){
                TDiagnosaMedis.setText(rs.getString("diagnosa_medis"));TRiwayatPersonal.setText(rs.getString("riwayat_personal"));
                TBB.setText(rs.getString("bb"));TTB.setText(rs.getString("tb"));TLILA.setText(rs.getString("lila"));
                TIMT.setText(rs.getString("imt"));TStatusGizi.setText(rs.getString("status_gizi"));TLilaU.setText(rs.getString("lila_u"));
                TKesanUmum.setText(rs.getString("kesan_umum"));TTekananDarah.setText(rs.getString("tekanan_darah"));
                TRespiratori.setText(rs.getString("respiratori"));TNadi.setText(rs.getString("nadi"));TSuhu.setText(rs.getString("suhu"));
                ChkMual.setSelected(rs.getString("masalah_mual").equals("Ya"));
                ChkMuntah.setSelected(rs.getString("masalah_muntah").equals("Ya"));
                ChkDiare.setSelected(rs.getString("masalah_diare").equals("Ya"));
                ChkKonstipasi.setSelected(rs.getString("masalah_konstipasi").equals("Ya"));
                ChkAnoreksia.setSelected(rs.getString("masalah_anoreksia").equals("Ya"));
                ChkSulitMenelan.setSelected(rs.getString("masalah_sulit_menelan").equals("Ya"));
                ChkSesak.setSelected(rs.getString("masalah_sesak").equals("Ya"));
                ChkOedema.setSelected(rs.getString("masalah_oedema").equals("Ya"));
                TGlukosaDarah.setText(rs.getString("glukosa_darah"));TUreum.setText(rs.getString("ureum"));
                TKreatinin.setText(rs.getString("kreatinin"));THb.setText(rs.getString("hb"));TKolesterol.setText(rs.getString("kolesterol"));
                TBiokimiaLain.setText(rs.getString("biokimia_lain"));
                TAlergiMakanan.setText(rs.getString("alergi_makanan"));TPolaMakan.setText(rs.getString("pola_makan"));
                TDiagnosisGizi.setText(rs.getString("diagnosis_gizi"));
                TTujuanDiet.setText(rs.getString("tujuan_diet"));TJenisDiet.setText(rs.getString("jenis_diet"));
                TEnergi.setText(rs.getString("energi"));TKarbohidrat.setText(rs.getString("karbohidrat"));
                TLemak.setText(rs.getString("lemak"));TProtein.setText(rs.getString("protein"));
                TBentukMakanan.setText(rs.getString("bentuk_makanan"));TCaraPemberian.setText(rs.getString("cara_pemberian"));
                TMonitoringEvaluasi.setText(rs.getString("monitoring_evaluasi"));
                NIP.setText(rs.getString("nip"));NamaPetugas.setText(rs.getString("nm_petugas")==null?"":rs.getString("nm_petugas"));
            }}catch(Exception e){System.out.println("Notif getData : "+e);}finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
        }
    }

    public void emptTeks(){TglAsuhan.setDate(new Date());
        TDiagnosaMedis.setText("");TRiwayatPersonal.setText("");
        TBB.setText("");TTB.setText("");TLILA.setText("");TIMT.setText("");TStatusGizi.setText("");TLilaU.setText("");
        TKesanUmum.setText("");TTekananDarah.setText("");TRespiratori.setText("");TNadi.setText("");TSuhu.setText("");
        ChkMual.setSelected(false);ChkMuntah.setSelected(false);ChkDiare.setSelected(false);ChkKonstipasi.setSelected(false);
        ChkAnoreksia.setSelected(false);ChkSulitMenelan.setSelected(false);ChkSesak.setSelected(false);ChkOedema.setSelected(false);
        TGlukosaDarah.setText("");TUreum.setText("");TKreatinin.setText("");THb.setText("");TKolesterol.setText("");TBiokimiaLain.setText("");
        TAlergiMakanan.setText("");TPolaMakan.setText("");TDiagnosisGizi.setText("");
        TTujuanDiet.setText("");TJenisDiet.setText("");TEnergi.setText("");TKarbohidrat.setText("");TLemak.setText("");TProtein.setText("");
        TBentukMakanan.setText("");TCaraPemberian.setText("");TMonitoringEvaluasi.setText("");
        NIP.setText("");NamaPetugas.setText("");}
    public void setNoRm(String n,String m,String p){TNoRw.setText(n);TNoRM.setText(m);TPasien.setText(p);}
    public void setNoRm(String n,Date t){TNoRw.setText(n);TglAsuhan.setDate(t);TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?",n));TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?",TNoRM.getText()));}
    public void isCek(){BtnSimpan.setEnabled(akses.getrujukan_keluar());BtnHapus.setEnabled(akses.getrujukan_keluar());BtnEdit.setEnabled(akses.getrujukan_keluar());BtnPrint.setEnabled(akses.getrujukan_keluar());}
}
