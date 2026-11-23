package rekammedis;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import kepegawaian.DlgCariDokter;
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

public class RMPermintaanDarahRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private Button BtnDokter, BtnPetugas;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan, TglPermintaan, TglDiperlukan;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private TextBox TBagian, TKelas, TNamaSuamiIstri, THemoglobin;
    private TextBox TTransfusiKapan, TTransfusiJumlah, TReaksiGejala;
    private TextBox TSerologiDimana, TSerologiKapan, TSerologiHasil;
    private TextBox TJumlahKehamilan;
    private TextBox TWbSegar, TWbBaru, TWbSimpan;
    private TextBox TPrcBiasa, TPrcCuci, TPrcLeukodeplated;
    private TextBox TPlasmaBiasa, TPlasmaFfp;
    private TextBox TFaktorTc, TFaktorAhf;
    private TextBox TLainBuffyCoat, TLainLainnya;
    private TextBox KdDokter, NmDokter, NIP, NamaPetugas;
    private TextArea TDiagnosaKlinis, TAlasanTransfusi;
    private ComboBox CmbTransfusiSebelumnya, CmbReaksiTransfusi, CmbSerologiPernah;
    private ComboBox CmbPernahAbortus, CmbHdn;
    private Label LCount;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariDokter dokterDlg = new DlgCariDokter(null, false);
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);

    private widget.Table tbObat;
    private javax.swing.table.DefaultTableModel tabMode;
    private ScrollPane Scroll, ScrollInput;
    private CekBox ChkInput;
    private PanelBiasa FormInput;
    private JPanel PanelInput;
    public widget.editorpane LoadHTML;

    public RMPermintaanDarahRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(240,245,235)),
            "::[ Formulir Permintaan Darah Untuk Transfusi Darah ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
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
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","Bagian","Diagnosa","Hb","Dokter","Petugas"}
        ) { @Override public boolean isCellEditable(int r,int c){return false;} };
        tbObat.setModel(tabMode);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override public void mouseClicked(java.awt.event.MouseEvent e){if(tabMode.getRowCount()!=0) getData();}
        });
        JPopupMenu pop = new JPopupMenu();
        JMenuItem mnCetak = new JMenuItem("Cetak Permintaan Darah");
        mnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetak.addActionListener(evt -> BtnPrint.doClick());
        pop.add(mnCetak);
        tbObat.setComponentPopupMenu(pop);
        Scroll.setViewportView(tbObat);

        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(880,900));
        FormInput.setLayout(null);

        int y = 10;
        JLabel h = new JLabel("FORMULIR PERMINTAAN DARAH UNTUK TRANSFUSI DARAH");
        h.setFont(new java.awt.Font("Tahoma",1,12)); h.setBounds(10,y,500,20); FormInput.add(h);
        JLabel hk = new JLabel("RM.LAB.06.004.2023"); hk.setFont(new java.awt.Font("Tahoma",0,8)); hk.setBounds(700,y,150,20); FormInput.add(hk);

        y+=32;
        Label lt = new Label(); lt.setText("Tanggal :"); lt.setBounds(10,y,80,23); FormInput.add(lt);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TglAsuhan.setBounds(95,y,130,23); FormInput.add(TglAsuhan);

        // === Section 1: Data Umum ===
        y+=38;
        JLabel hs1 = new JLabel("Data Umum :"); hs1.setFont(new java.awt.Font("Tahoma",1,11)); hs1.setBounds(10,y,200,20); FormInput.add(hs1);

        y+=25;
        Label lb = new Label(); lb.setText("Bagian :"); lb.setBounds(10,y,55,23); FormInput.add(lb);
        TBagian = new TextBox(); TBagian.setBounds(70,y,200,23); FormInput.add(TBagian);
        Label lkl = new Label(); lkl.setText("Kelas :"); lkl.setBounds(290,y,45,23); FormInput.add(lkl);
        TKelas = new TextBox(); TKelas.setBounds(340,y,100,23); FormInput.add(TKelas);

        y+=28;
        Label ldk = new Label(); ldk.setText("Dokter :"); ldk.setBounds(10,y,55,23); FormInput.add(ldk);
        KdDokter = new TextBox(); KdDokter.setBounds(70,y,80,23); FormInput.add(KdDokter);
        NmDokter = new TextBox(); NmDokter.setBounds(152,y,200,23); FormInput.add(NmDokter);
        BtnDokter = new Button(); BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); BtnDokter.setBounds(354,y,28,23); FormInput.add(BtnDokter);
        BtnDokter.addActionListener(ev->{dokterDlg.emptTeks();dokterDlg.isCek();dokterDlg.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);dokterDlg.setLocationRelativeTo(internalFrame1);dokterDlg.setVisible(true);});
        dokterDlg.addWindowListener(new java.awt.event.WindowAdapter(){@Override public void windowClosed(java.awt.event.WindowEvent e){if(dokterDlg.getTable().getSelectedRow()!=-1){KdDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(),0).toString());NmDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(),1).toString());}}});

        y+=28;
        Label lsi = new Label(); lsi.setText("Nama Suami/Istri :"); lsi.setBounds(10,y,115,23); FormInput.add(lsi);
        TNamaSuamiIstri = new TextBox(); TNamaSuamiIstri.setBounds(130,y,250,23); FormInput.add(TNamaSuamiIstri);

        y+=28;
        Label ltp = new Label(); ltp.setText("Tgl Permintaan :"); ltp.setBounds(10,y,105,23); FormInput.add(ltp);
        TglPermintaan = new widget.Tanggal(); TglPermintaan.setDisplayFormat("dd-MM-yyyy"); TglPermintaan.setBounds(120,y,100,23); FormInput.add(TglPermintaan);
        Label ltd = new Label(); ltd.setText("Tgl Diperlukan :"); ltd.setBounds(240,y,100,23); FormInput.add(ltd);
        TglDiperlukan = new widget.Tanggal(); TglDiperlukan.setDisplayFormat("dd-MM-yyyy"); TglDiperlukan.setBounds(345,y,100,23); FormInput.add(TglDiperlukan);

        y+=28;
        Label ldx = new Label(); ldx.setText("Diagnosa Klinis :"); ldx.setBounds(10,y,110,23); FormInput.add(ldx);
        TDiagnosaKlinis = new TextArea(); TDiagnosaKlinis.setBounds(125,y,480,50); FormInput.add(TDiagnosaKlinis);

        y+=55;
        Label lat = new Label(); lat.setText("Alasan Transfusi :"); lat.setBounds(10,y,115,23); FormInput.add(lat);
        TAlasanTransfusi = new TextArea(); TAlasanTransfusi.setBounds(130,y,475,50); FormInput.add(TAlasanTransfusi);

        y+=55;
        Label lhb = new Label(); lhb.setText("Hemoglobin :"); lhb.setBounds(10,y,85,23); FormInput.add(lhb);
        THemoglobin = new TextBox(); THemoglobin.setBounds(100,y,70,23); FormInput.add(THemoglobin);
        Label lhbs = new Label(); lhbs.setText("gr/dl"); lhbs.setBounds(175,y,35,23); FormInput.add(lhbs);

        // === Section 2: Riwayat Transfusi ===
        y+=35;
        JLabel hs2 = new JLabel("Riwayat Transfusi :"); hs2.setFont(new java.awt.Font("Tahoma",1,11)); hs2.setBounds(10,y,200,20); FormInput.add(hs2);

        y+=25;
        Label lts = new Label(); lts.setText("Transfusi Sebelumnya :"); lts.setBounds(10,y,145,23); FormInput.add(lts);
        CmbTransfusiSebelumnya = new ComboBox(); CmbTransfusiSebelumnya.setModel(new DefaultComboBoxModel(new String[]{"Ya","Tidak"})); CmbTransfusiSebelumnya.setBounds(160,y,80,23); FormInput.add(CmbTransfusiSebelumnya);
        Label ltk = new Label(); ltk.setText("Kapan :"); ltk.setBounds(260,y,50,23); FormInput.add(ltk);
        TTransfusiKapan = new TextBox(); TTransfusiKapan.setBounds(315,y,100,23); FormInput.add(TTransfusiKapan);
        Label ltj = new Label(); ltj.setText("Jumlah :"); ltj.setBounds(430,y,55,23); FormInput.add(ltj);
        TTransfusiJumlah = new TextBox(); TTransfusiJumlah.setBounds(490,y,80,23); FormInput.add(TTransfusiJumlah);

        y+=28;
        Label lrt = new Label(); lrt.setText("Reaksi Transfusi :"); lrt.setBounds(10,y,115,23); FormInput.add(lrt);
        CmbReaksiTransfusi = new ComboBox(); CmbReaksiTransfusi.setModel(new DefaultComboBoxModel(new String[]{"Ya","Tidak"})); CmbReaksiTransfusi.setBounds(130,y,80,23); FormInput.add(CmbReaksiTransfusi);
        Label lrg = new Label(); lrg.setText("Gejala :"); lrg.setBounds(230,y,50,23); FormInput.add(lrg);
        TReaksiGejala = new TextBox(); TReaksiGejala.setBounds(285,y,200,23); FormInput.add(TReaksiGejala);

        y+=28;
        Label lsp = new Label(); lsp.setText("Serologi Pernah :"); lsp.setBounds(10,y,115,23); FormInput.add(lsp);
        CmbSerologiPernah = new ComboBox(); CmbSerologiPernah.setModel(new DefaultComboBoxModel(new String[]{"Ya","Tidak"})); CmbSerologiPernah.setBounds(130,y,80,23); FormInput.add(CmbSerologiPernah);
        Label lsd = new Label(); lsd.setText("Dimana :"); lsd.setBounds(230,y,55,23); FormInput.add(lsd);
        TSerologiDimana = new TextBox(); TSerologiDimana.setBounds(290,y,120,23); FormInput.add(TSerologiDimana);

        y+=28;
        Label lsk = new Label(); lsk.setText("Kapan :"); lsk.setBounds(130,y,50,23); FormInput.add(lsk);
        TSerologiKapan = new TextBox(); TSerologiKapan.setBounds(185,y,100,23); FormInput.add(TSerologiKapan);
        Label lsh = new Label(); lsh.setText("Hasil :"); lsh.setBounds(300,y,45,23); FormInput.add(lsh);
        TSerologiHasil = new TextBox(); TSerologiHasil.setBounds(350,y,150,23); FormInput.add(TSerologiHasil);

        // === Section 3: Khusus Wanita ===
        y+=35;
        JLabel hs3 = new JLabel("Khusus Wanita :"); hs3.setFont(new java.awt.Font("Tahoma",1,11)); hs3.setBounds(10,y,200,20); FormInput.add(hs3);

        y+=25;
        Label ljh = new Label(); ljh.setText("Jumlah Kehamilan :"); ljh.setBounds(10,y,125,23); FormInput.add(ljh);
        TJumlahKehamilan = new TextBox(); TJumlahKehamilan.setBounds(140,y,60,23); FormInput.add(TJumlahKehamilan);
        Label lpa = new Label(); lpa.setText("Pernah Abortus :"); lpa.setBounds(220,y,110,23); FormInput.add(lpa);
        CmbPernahAbortus = new ComboBox(); CmbPernahAbortus.setModel(new DefaultComboBoxModel(new String[]{"Ya","Tidak"})); CmbPernahAbortus.setBounds(335,y,80,23); FormInput.add(CmbPernahAbortus);
        Label lhn = new Label(); lhn.setText("HDN :"); lhn.setBounds(430,y,40,23); FormInput.add(lhn);
        CmbHdn = new ComboBox(); CmbHdn.setModel(new DefaultComboBoxModel(new String[]{"Ya","Tidak"})); CmbHdn.setBounds(475,y,80,23); FormInput.add(CmbHdn);

        // === Section 4: Komponen Darah ===
        y+=35;
        JLabel hs4 = new JLabel("Komponen Darah Yang Diminta :"); hs4.setFont(new java.awt.Font("Tahoma",1,11)); hs4.setBounds(10,y,300,20); FormInput.add(hs4);

        // WB
        y+=25;
        JLabel lwb = new JLabel("WB :"); lwb.setFont(new java.awt.Font("Tahoma",2,11)); lwb.setBounds(30,y,40,20); FormInput.add(lwb);
        Label lws = new Label(); lws.setText("Segar :"); lws.setBounds(80,y,50,23); FormInput.add(lws);
        TWbSegar = new TextBox(); TWbSegar.setBounds(135,y,60,23); FormInput.add(TWbSegar);
        Label lwsc = new Label(); lwsc.setText("cc"); lwsc.setBounds(198,y,20,23); FormInput.add(lwsc);
        Label lwb2 = new Label(); lwb2.setText("Baru :"); lwb2.setBounds(230,y,45,23); FormInput.add(lwb2);
        TWbBaru = new TextBox(); TWbBaru.setBounds(280,y,60,23); FormInput.add(TWbBaru);
        Label lwbc = new Label(); lwbc.setText("cc"); lwbc.setBounds(343,y,20,23); FormInput.add(lwbc);
        Label lwsm = new Label(); lwsm.setText("Simpan :"); lwsm.setBounds(375,y,55,23); FormInput.add(lwsm);
        TWbSimpan = new TextBox(); TWbSimpan.setBounds(435,y,60,23); FormInput.add(TWbSimpan);
        Label lwsmc = new Label(); lwsmc.setText("cc"); lwsmc.setBounds(498,y,20,23); FormInput.add(lwsmc);

        // PRC
        y+=28;
        JLabel lprc = new JLabel("PRC :"); lprc.setFont(new java.awt.Font("Tahoma",2,11)); lprc.setBounds(30,y,40,20); FormInput.add(lprc);
        Label lpb = new Label(); lpb.setText("Biasa :"); lpb.setBounds(80,y,50,23); FormInput.add(lpb);
        TPrcBiasa = new TextBox(); TPrcBiasa.setBounds(135,y,60,23); FormInput.add(TPrcBiasa);
        Label lpbc = new Label(); lpbc.setText("cc"); lpbc.setBounds(198,y,20,23); FormInput.add(lpbc);
        Label lpc = new Label(); lpc.setText("Cuci :"); lpc.setBounds(230,y,45,23); FormInput.add(lpc);
        TPrcCuci = new TextBox(); TPrcCuci.setBounds(280,y,60,23); FormInput.add(TPrcCuci);
        Label lpcc = new Label(); lpcc.setText("cc"); lpcc.setBounds(343,y,20,23); FormInput.add(lpcc);
        Label lpl2 = new Label(); lpl2.setText("Leukodeplated :"); lpl2.setBounds(375,y,100,23); FormInput.add(lpl2);
        TPrcLeukodeplated = new TextBox(); TPrcLeukodeplated.setBounds(480,y,60,23); FormInput.add(TPrcLeukodeplated);
        Label lplc = new Label(); lplc.setText("cc"); lplc.setBounds(543,y,20,23); FormInput.add(lplc);

        // Plasma
        y+=28;
        JLabel lplm = new JLabel("Plasma :"); lplm.setFont(new java.awt.Font("Tahoma",2,11)); lplm.setBounds(30,y,55,20); FormInput.add(lplm);
        Label lpmb = new Label(); lpmb.setText("Biasa :"); lpmb.setBounds(95,y,50,23); FormInput.add(lpmb);
        TPlasmaBiasa = new TextBox(); TPlasmaBiasa.setBounds(150,y,60,23); FormInput.add(TPlasmaBiasa);
        Label lpmbc = new Label(); lpmbc.setText("cc"); lpmbc.setBounds(213,y,20,23); FormInput.add(lpmbc);
        Label lpff = new Label(); lpff.setText("FFP :"); lpff.setBounds(250,y,35,23); FormInput.add(lpff);
        TPlasmaFfp = new TextBox(); TPlasmaFfp.setBounds(290,y,60,23); FormInput.add(TPlasmaFfp);
        Label lpffc = new Label(); lpffc.setText("cc"); lpffc.setBounds(353,y,20,23); FormInput.add(lpffc);

        // Faktor
        y+=28;
        JLabel lfk = new JLabel("Faktor :"); lfk.setFont(new java.awt.Font("Tahoma",2,11)); lfk.setBounds(30,y,50,20); FormInput.add(lfk);
        Label lftc = new Label(); lftc.setText("TC :"); lftc.setBounds(90,y,30,23); FormInput.add(lftc);
        TFaktorTc = new TextBox(); TFaktorTc.setBounds(125,y,60,23); FormInput.add(TFaktorTc);
        Label lftcu = new Label(); lftcu.setText("unit"); lftcu.setBounds(188,y,30,23); FormInput.add(lftcu);
        Label lfah = new Label(); lfah.setText("AHF :"); lfah.setBounds(230,y,40,23); FormInput.add(lfah);
        TFaktorAhf = new TextBox(); TFaktorAhf.setBounds(275,y,60,23); FormInput.add(TFaktorAhf);
        Label lfahu = new Label(); lfahu.setText("unit"); lfahu.setBounds(338,y,30,23); FormInput.add(lfahu);

        // Lain-lain
        y+=28;
        JLabel lln = new JLabel("Lain :"); lln.setFont(new java.awt.Font("Tahoma",2,11)); lln.setBounds(30,y,40,20); FormInput.add(lln);
        Label llbc = new Label(); llbc.setText("Buffy Coat :"); llbc.setBounds(80,y,80,23); FormInput.add(llbc);
        TLainBuffyCoat = new TextBox(); TLainBuffyCoat.setBounds(165,y,60,23); FormInput.add(TLainBuffyCoat);
        Label llbck = new Label(); llbck.setText("kantong"); llbck.setBounds(228,y,50,23); FormInput.add(llbck);
        Label lll = new Label(); lll.setText("Lainnya :"); lll.setBounds(300,y,60,23); FormInput.add(lll);
        TLainLainnya = new TextBox(); TLainLainnya.setBounds(365,y,200,23); FormInput.add(TLainLainnya);

        // === Section 5: Petugas ===
        y+=35;
        JLabel hs5 = new JLabel("Petugas :"); hs5.setFont(new java.awt.Font("Tahoma",1,11)); hs5.setBounds(10,y,200,20); FormInput.add(hs5);

        y+=25;
        Label lpt = new Label(); lpt.setText("Petugas :"); lpt.setBounds(10,y,60,23); FormInput.add(lpt);
        NIP = new TextBox(); NIP.setBounds(75,y,80,23); FormInput.add(NIP);
        NamaPetugas = new TextBox(); NamaPetugas.setBounds(157,y,200,23); FormInput.add(NamaPetugas);
        BtnPetugas = new Button(); BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); BtnPetugas.setBounds(359,y,28,23); FormInput.add(BtnPetugas);
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
        Label lk2=new Label();lk2.setText("Key Word :");lk2.setPreferredSize(new Dimension(70,23));
        TCari=new TextBox();TCari.setPreferredSize(new Dimension(150,23));
        BtnCari=new Button();BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));BtnCari.setPreferredSize(new Dimension(28,23));
        BtnAll=new Button();BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));BtnAll.setPreferredSize(new Dimension(28,23));
        LCount=new Label();LCount.setText("Record : 0");LCount.setPreferredSize(new Dimension(100,23));
        panelBawah.add(BtnSimpan);panelBawah.add(BtnBatal);panelBawah.add(BtnHapus);panelBawah.add(BtnEdit);panelBawah.add(BtnPrint);panelBawah.add(BtnKeluar);
        panelBawah.add(lc);panelBawah.add(DTPCari1);panelBawah.add(sd);panelBawah.add(DTPCari2);panelBawah.add(lk2);panelBawah.add(TCari);panelBawah.add(BtnCari);panelBawah.add(BtnAll);panelBawah.add(LCount);

        BtnKeluar.addActionListener(ev->dispose());
        BtnBatal.addActionListener(ev->emptTeks());
        BtnCari.addActionListener(ev->tampil());
        BtnAll.addActionListener(ev->{TCari.setText("");tampil();});
        TCari.addKeyListener(new java.awt.event.KeyAdapter(){@Override public void keyPressed(java.awt.event.KeyEvent e){if(e.getKeyCode()==KeyEvent.VK_ENTER)tampil();}});

        BtnSimpan.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")){Valid.textKosong(TNoRw,"Pasien");return;}
            if(KdDokter.getText().trim().equals("")){Valid.textKosong(KdDokter,"Dokter");return;}
            if(NIP.getText().trim().equals("")){Valid.textKosong(NIP,"Petugas");return;}
            String tglS=Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19);
            String tglPerm=Valid.SetTgl(TglPermintaan.getSelectedItem()+"");
            String tglDip=Valid.SetTgl(TglDiperlukan.getSelectedItem()+"");
            if(Sequel.menyimpantf("permintaan_darah_rscb","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?","Data",36,new String[]{
                TNoRw.getText(),tglS,TBagian.getText(),TKelas.getText(),KdDokter.getText(),TNamaSuamiIstri.getText(),
                tglPerm,tglDip,TDiagnosaKlinis.getText(),TAlasanTransfusi.getText(),THemoglobin.getText(),
                CmbTransfusiSebelumnya.getSelectedItem().toString(),TTransfusiKapan.getText(),TTransfusiJumlah.getText(),
                CmbReaksiTransfusi.getSelectedItem().toString(),TReaksiGejala.getText(),
                CmbSerologiPernah.getSelectedItem().toString(),TSerologiDimana.getText(),TSerologiKapan.getText(),TSerologiHasil.getText(),
                TJumlahKehamilan.getText(),CmbPernahAbortus.getSelectedItem().toString(),CmbHdn.getSelectedItem().toString(),
                TWbSegar.getText(),TWbBaru.getText(),TWbSimpan.getText(),
                TPrcBiasa.getText(),TPrcCuci.getText(),TPrcLeukodeplated.getText(),
                TPlasmaBiasa.getText(),TPlasmaFfp.getText(),
                TFaktorTc.getText(),TFaktorAhf.getText(),
                TLainBuffyCoat.getText(),TLainLainnya.getText(),NIP.getText()
            })){tampil();emptTeks();}
        });

        BtnHapus.addActionListener(ev->{
            if(tbObat.getSelectedRow()!=-1){if(Sequel.queryu2tf("delete from permintaan_darah_rscb where no_rawat=? and tanggal=?",2,new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()
            })){tampil();emptTeks();}}else JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");
        });

        BtnEdit.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")||tbObat.getSelectedRow()==-1){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            if(Sequel.queryu2tf("delete from permintaan_darah_rscb where no_rawat=? and tanggal=?",2,new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()
            })){BtnSimpan.doClick();}
        });

        BtnPrint.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            Map<String,Object> p=new HashMap<>();p.put("namars",akses.getnamars());p.put("alamatrs",akses.getalamatrs());
            p.put("propinsirs",akses.getpropinsirs());p.put("kabupatenrs",akses.getkabupatenrs());
            p.put("kontakrs",akses.getkontakrs());p.put("emailrs",akses.getemailrs());
            p.put("logo",Sequel.cariGambar("select setting.logo from setting"));
            Valid.MyReportqry("rptPermintaanDarahRscb.jasper","report","::[ Formulir Permintaan Darah Untuk Transfusi Darah ]::",
                "select f.*,r.no_rkm_medis,p.nm_pasien,p.jk,concat(r.umurdaftar,' ',r.sttsumur) as umur,d.nm_dokter,"+
                "pt.nama as nm_petugas "+
                "from permintaan_darah_rscb f inner join reg_periksa r on f.no_rawat=r.no_rawat "+
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis left join dokter d on f.kd_dokter=d.kd_dokter "+
                "left join petugas pt on f.nip=pt.nip "+
                "where f.no_rawat='"+TNoRw.getText()+"' and f.tanggal='"+Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19)+"'",p);
        });

        InternalFrame ifd=new InternalFrame();ifd.setLayout(new BorderLayout());ifd.setBackground(new Color(255,255,255));
        ifd.add(PanelInput,BorderLayout.NORTH);ifd.add(Scroll,BorderLayout.CENTER);
        internalFrame1.add(panelAtas,BorderLayout.PAGE_START);internalFrame1.add(ifd,BorderLayout.CENTER);internalFrame1.add(panelBawah,BorderLayout.PAGE_END);
        setLayout(new BorderLayout());add(internalFrame1,BorderLayout.CENTER);setDefaultCloseOperation(DISPOSE_ON_CLOSE);setUndecorated(true);
    }

    private void isForm(){if(ChkInput.isSelected()){ChkInput.setVisible(false);PanelInput.setPreferredSize(new Dimension(WIDTH,440));FormInput.setVisible(true);ChkInput.setVisible(true);}else{ChkInput.setVisible(false);PanelInput.setPreferredSize(new Dimension(WIDTH,20));FormInput.setVisible(false);ChkInput.setVisible(true);}}

    public void tampil(){
        Valid.tabelKosong(tabMode);
        try{ps=koneksi.prepareStatement("select f.no_rawat,r.no_rkm_medis,p.nm_pasien,f.tanggal,f.bagian,f.diagnosa_klinis,f.hemoglobin,d.nm_dokter,pt.nama as nm_petugas "+
            "from permintaan_darah_rscb f inner join reg_periksa r on f.no_rawat=r.no_rawat inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "+
            "left join dokter d on f.kd_dokter=d.kd_dokter left join petugas pt on f.nip=pt.nip "+
            "where f.tanggal between ? and ? and (f.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) order by f.tanggal desc");
        ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
        ps.setString(3,"%"+TCari.getText().trim()+"%");ps.setString(4,"%"+TCari.getText().trim()+"%");ps.setString(5,"%"+TCari.getText().trim()+"%");
        rs=ps.executeQuery();while(rs.next()){tabMode.addRow(new String[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9)});}
        LCount.setText("Record : "+tabMode.getRowCount());}catch(Exception e){System.out.println("Notif : "+e);}finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
    }

    public void getData(){
        if(tbObat.getSelectedRow()!=-1){
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            try{TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()));}catch(Exception e){}
            try{ps=koneksi.prepareStatement("select f.*,d.nm_dokter,pt.nama as nm_petugas from permintaan_darah_rscb f "+
                "left join dokter d on f.kd_dokter=d.kd_dokter left join petugas pt on f.nip=pt.nip "+
                "where f.no_rawat=? and f.tanggal=?");
            ps.setString(1,tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());ps.setString(2,tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            rs=ps.executeQuery();if(rs.next()){
                TBagian.setText(rs.getString("bagian"));TKelas.setText(rs.getString("kelas"));
                KdDokter.setText(rs.getString("kd_dokter"));NmDokter.setText(rs.getString("nm_dokter"));
                TNamaSuamiIstri.setText(rs.getString("nama_suami_istri"));
                try{if(rs.getString("tgl_permintaan")!=null)TglPermintaan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("tgl_permintaan")));}catch(Exception ex){}
                try{if(rs.getString("tgl_diperlukan")!=null)TglDiperlukan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("tgl_diperlukan")));}catch(Exception ex){}
                TDiagnosaKlinis.setText(rs.getString("diagnosa_klinis"));TAlasanTransfusi.setText(rs.getString("alasan_transfusi"));THemoglobin.setText(rs.getString("hemoglobin"));
                CmbTransfusiSebelumnya.setSelectedItem(rs.getString("transfusi_sebelumnya"));TTransfusiKapan.setText(rs.getString("transfusi_kapan"));TTransfusiJumlah.setText(rs.getString("transfusi_jumlah"));
                CmbReaksiTransfusi.setSelectedItem(rs.getString("reaksi_transfusi"));TReaksiGejala.setText(rs.getString("reaksi_gejala"));
                CmbSerologiPernah.setSelectedItem(rs.getString("serologi_pernah"));TSerologiDimana.setText(rs.getString("serologi_dimana"));TSerologiKapan.setText(rs.getString("serologi_kapan"));TSerologiHasil.setText(rs.getString("serologi_hasil"));
                TJumlahKehamilan.setText(rs.getString("jumlah_kehamilan"));CmbPernahAbortus.setSelectedItem(rs.getString("pernah_abortus"));CmbHdn.setSelectedItem(rs.getString("hdn"));
                TWbSegar.setText(rs.getString("wb_segar"));TWbBaru.setText(rs.getString("wb_baru"));TWbSimpan.setText(rs.getString("wb_simpan"));
                TPrcBiasa.setText(rs.getString("prc_biasa"));TPrcCuci.setText(rs.getString("prc_cuci"));TPrcLeukodeplated.setText(rs.getString("prc_leukodeplated"));
                TPlasmaBiasa.setText(rs.getString("plasma_biasa"));TPlasmaFfp.setText(rs.getString("plasma_ffp"));
                TFaktorTc.setText(rs.getString("faktor_tc"));TFaktorAhf.setText(rs.getString("faktor_ahf"));
                TLainBuffyCoat.setText(rs.getString("lain_buffy_coat"));TLainLainnya.setText(rs.getString("lain_lainnya"));
                NIP.setText(rs.getString("nip"));NamaPetugas.setText(rs.getString("nm_petugas"));
            }}catch(Exception e){System.out.println("Notif getData : "+e);}finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
        }
    }

    public void emptTeks(){
        TglAsuhan.setDate(new Date());TglPermintaan.setDate(new Date());TglDiperlukan.setDate(new Date());
        TBagian.setText("");TKelas.setText("");KdDokter.setText("");NmDokter.setText("");TNamaSuamiIstri.setText("");
        TDiagnosaKlinis.setText("");TAlasanTransfusi.setText("");THemoglobin.setText("");
        CmbTransfusiSebelumnya.setSelectedIndex(0);TTransfusiKapan.setText("");TTransfusiJumlah.setText("");
        CmbReaksiTransfusi.setSelectedIndex(0);TReaksiGejala.setText("");
        CmbSerologiPernah.setSelectedIndex(0);TSerologiDimana.setText("");TSerologiKapan.setText("");TSerologiHasil.setText("");
        TJumlahKehamilan.setText("");CmbPernahAbortus.setSelectedIndex(0);CmbHdn.setSelectedIndex(0);
        TWbSegar.setText("");TWbBaru.setText("");TWbSimpan.setText("");
        TPrcBiasa.setText("");TPrcCuci.setText("");TPrcLeukodeplated.setText("");
        TPlasmaBiasa.setText("");TPlasmaFfp.setText("");
        TFaktorTc.setText("");TFaktorAhf.setText("");
        TLainBuffyCoat.setText("");TLainLainnya.setText("");
        NIP.setText("");NamaPetugas.setText("");
    }

    public void setNoRm(String n,String m,String p){TNoRw.setText(n);TNoRM.setText(m);TPasien.setText(p);}
    public void setNoRm(String n,Date t){TNoRw.setText(n);TglAsuhan.setDate(t);TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?",n));TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?",TNoRM.getText()));}
    public void isCek(){BtnSimpan.setEnabled(akses.getrujukan_keluar());BtnHapus.setEnabled(akses.getrujukan_keluar());BtnEdit.setEnabled(akses.getrujukan_keluar());BtnPrint.setEnabled(akses.getrujukan_keluar());}
}
