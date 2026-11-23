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

public class RMDugaanReaksiTransfusiRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private Button BtnDokter, BtnPerawat, BtnPetugas;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan, TglPermintaan, TglDarahDiambil, TglReaksi;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private TextBox TRuangRawat, TJamMulaiTransfusi, TJamSelesaiTransfusi;
    private TextBox TJenisKomponen, TVolMasuk, TNoKantongDarah, TPemberianKe;
    private TextBox TSuhuSebelum, TTdSebelum, TNadiSebelum, TRrSebelum;
    private TextBox TSuhuSesudah, TTdSesudah, TNadiSesudah, TRrSesudah;
    private TextBox TGejalaLainnya;
    private TextBox KdDokter, NmDokter, NipPerawat, NmPerawat, NIP, NamaPetugas;
    private TextArea TDiagnosa, TRiwayatTransfusi, TPenanganan;
    private ComboBox CmbIdentitasBenar, CmbNoKantongBenar, CmbGoldarSesuai, CmbSuhu24jam;
    private ComboBox CmbKategoriReaksi, CmbPemeriksaanLanjutan;
    private CekBox ChkGatal, ChkSesak, ChkNyeriDada, ChkHipotensi, ChkPalpitasi, ChkSakitKepala;
    private CekBox ChkNyeriPunggung, ChkTakikardi, ChkLemas, ChkDemam, ChkGelisah, ChkPerdarahan;
    private Label LCount;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariDokter dokterDlg = new DlgCariDokter(null, false);
    private DlgCariPetugas perawatDlg = new DlgCariPetugas(null, false);
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);

    private widget.Table tbObat;
    private javax.swing.table.DefaultTableModel tabMode;
    private ScrollPane Scroll, ScrollInput;
    private CekBox ChkInput;
    private PanelBiasa FormInput;
    private JPanel PanelInput;
    public widget.editorpane LoadHTML;

    public RMDugaanReaksiTransfusiRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(240,245,235)),
            "::[ Formulir Laporan Dugaan Reaksi Transfusi ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
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
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","Ruang","Jenis Komponen","Kategori","Dokter","Petugas"}
        ) { @Override public boolean isCellEditable(int r,int c){return false;} };
        tbObat.setModel(tabMode);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override public void mouseClicked(java.awt.event.MouseEvent e){if(tabMode.getRowCount()!=0) getData();}
            @Override public void mousePressed(java.awt.event.MouseEvent e){
                int row=tbObat.rowAtPoint(e.getPoint());
                if(row>=0&&row<tabMode.getRowCount()){tbObat.setRowSelectionInterval(row,row);}
                if(javax.swing.SwingUtilities.isRightMouseButton(e)&&tabMode.getRowCount()!=0) getData();
            }
        });
        JPopupMenu pop = new JPopupMenu();
        JMenuItem mnCetak = new JMenuItem("Cetak Dugaan Reaksi Transfusi");
        mnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetak.addActionListener(evt -> BtnPrint.doClick());
        pop.add(mnCetak);
        JMenuItem mnBankDarah = new JMenuItem("Form Petugas Bank Darah");
        mnBankDarah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        mnBankDarah.addActionListener(evt -> {
            if(tbObat.getSelectedRow()!=-1){
                RMPetugasBankDarahRscb dlgBD = new RMPetugasBankDarahRscb(null, true);
                dlgBD.setData(
                    tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(), // no_rawat
                    tbObat.getValueAt(tbObat.getSelectedRow(),3).toString(), // tanggal
                    tbObat.getValueAt(tbObat.getSelectedRow(),2).toString(), // nm_pasien
                    tbObat.getValueAt(tbObat.getSelectedRow(),4).toString()  // ruang_rawat
                );
                dlgBD.isCek();
                dlgBD.setVisible(true);
            } else JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");
        });
        pop.add(mnBankDarah);
        tbObat.setComponentPopupMenu(pop);
        Scroll.setViewportView(tbObat);

        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(880,1200));
        FormInput.setLayout(null);

        int y = 10;
        JLabel h = new JLabel("FORMULIR LAPORAN DUGAAN REAKSI TRANSFUSI");
        h.setFont(new java.awt.Font("Tahoma",1,12)); h.setBounds(10,y,450,20); FormInput.add(h);
        JLabel hk = new JLabel("RM.LAB.06.003.2023"); hk.setFont(new java.awt.Font("Tahoma",0,8)); hk.setBounds(700,y,150,20); FormInput.add(hk);

        y+=32;
        Label lt = new Label(); lt.setText("Tanggal :"); lt.setBounds(10,y,80,23); FormInput.add(lt);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TglAsuhan.setBounds(95,y,130,23); FormInput.add(TglAsuhan);

        // === Section 1: Data Pasien ===
        y+=38;
        JLabel hs1 = new JLabel("Data Pasien :"); hs1.setFont(new java.awt.Font("Tahoma",1,11)); hs1.setBounds(10,y,200,20); FormInput.add(hs1);

        y+=25;
        Label lrr = new Label(); lrr.setText("Ruang Rawat :"); lrr.setBounds(10,y,90,23); FormInput.add(lrr);
        TRuangRawat = new TextBox(); TRuangRawat.setBounds(105,y,200,23); FormInput.add(TRuangRawat);

        y+=28;
        Label ldg = new Label(); ldg.setText("Diagnosa :"); ldg.setBounds(10,y,90,23); FormInput.add(ldg);
        TDiagnosa = new TextArea(); TDiagnosa.setBounds(105,y,500,50); TDiagnosa.setBorder(BorderFactory.createLineBorder(Color.BLACK)); FormInput.add(TDiagnosa);

        y+=55;
        Label lrt = new Label(); lrt.setText("Riwayat Transfusi :"); lrt.setBounds(10,y,120,23); FormInput.add(lrt);
        TRiwayatTransfusi = new TextArea(); TRiwayatTransfusi.setBounds(135,y,470,50); TRiwayatTransfusi.setBorder(BorderFactory.createLineBorder(Color.BLACK)); FormInput.add(TRiwayatTransfusi);

        // === Section 2: Tanggal/Jam ===
        y+=60;
        JLabel hs2 = new JLabel("Data Transfusi :"); hs2.setFont(new java.awt.Font("Tahoma",1,11)); hs2.setBounds(10,y,200,20); FormInput.add(hs2);

        y+=25;
        Label ltp = new Label(); ltp.setText("Tgl Permintaan :"); ltp.setBounds(10,y,105,23); FormInput.add(ltp);
        TglPermintaan = new widget.Tanggal(); TglPermintaan.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TglPermintaan.setBounds(120,y,165,23); FormInput.add(TglPermintaan);

        y+=28;
        Label ltda = new Label(); ltda.setText("Tgl Darah Diambil :"); ltda.setBounds(10,y,120,23); FormInput.add(ltda);
        TglDarahDiambil = new widget.Tanggal(); TglDarahDiambil.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TglDarahDiambil.setBounds(135,y,165,23); FormInput.add(TglDarahDiambil);

        y+=28;
        Label ljm = new Label(); ljm.setText("Jam Mulai :"); ljm.setBounds(10,y,75,23); FormInput.add(ljm);
        TJamMulaiTransfusi = new TextBox(); TJamMulaiTransfusi.setBounds(90,y,70,23); FormInput.add(TJamMulaiTransfusi);
        Label ljs = new Label(); ljs.setText("Jam Selesai :"); ljs.setBounds(180,y,85,23); FormInput.add(ljs);
        TJamSelesaiTransfusi = new TextBox(); TJamSelesaiTransfusi.setBounds(270,y,70,23); FormInput.add(TJamSelesaiTransfusi);

        y+=28;
        Label ltr = new Label(); ltr.setText("Tgl Reaksi :"); ltr.setBounds(10,y,80,23); FormInput.add(ltr);
        TglReaksi = new widget.Tanggal(); TglReaksi.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TglReaksi.setBounds(95,y,165,23); FormInput.add(TglReaksi);

        y+=28;
        Label ljk = new Label(); ljk.setText("Jenis Komponen :"); ljk.setBounds(10,y,110,23); FormInput.add(ljk);
        TJenisKomponen = new TextBox(); TJenisKomponen.setBounds(125,y,200,23); FormInput.add(TJenisKomponen);
        Label lvm = new Label(); lvm.setText("Vol Masuk :"); lvm.setBounds(345,y,75,23); FormInput.add(lvm);
        TVolMasuk = new TextBox(); TVolMasuk.setBounds(425,y,80,23); FormInput.add(TVolMasuk);

        y+=28;
        Label lnk = new Label(); lnk.setText("No Kantong Darah :"); lnk.setBounds(10,y,125,23); FormInput.add(lnk);
        TNoKantongDarah = new TextBox(); TNoKantongDarah.setBounds(140,y,200,23); FormInput.add(TNoKantongDarah);

        // === Section 3: Daftar Check ===
        y+=35;
        JLabel hs3 = new JLabel("Verifikasi :"); hs3.setFont(new java.awt.Font("Tahoma",1,11)); hs3.setBounds(10,y,200,20); FormInput.add(hs3);

        y+=25;
        Label lib = new Label(); lib.setText("Identitas Benar :"); lib.setBounds(10,y,110,23); FormInput.add(lib);
        CmbIdentitasBenar = new ComboBox(); CmbIdentitasBenar.setModel(new DefaultComboBoxModel(new String[]{"Ya","Tidak"})); CmbIdentitasBenar.setBounds(125,y,80,23); FormInput.add(CmbIdentitasBenar);
        Label lnkb = new Label(); lnkb.setText("No Kantong Benar :"); lnkb.setBounds(230,y,125,23); FormInput.add(lnkb);
        CmbNoKantongBenar = new ComboBox(); CmbNoKantongBenar.setModel(new DefaultComboBoxModel(new String[]{"Ya","Tidak"})); CmbNoKantongBenar.setBounds(360,y,80,23); FormInput.add(CmbNoKantongBenar);

        y+=28;
        Label lgs = new Label(); lgs.setText("Goldar Sesuai :"); lgs.setBounds(10,y,100,23); FormInput.add(lgs);
        CmbGoldarSesuai = new ComboBox(); CmbGoldarSesuai.setModel(new DefaultComboBoxModel(new String[]{"Ya","Tidak"})); CmbGoldarSesuai.setBounds(115,y,80,23); FormInput.add(CmbGoldarSesuai);
        Label ls24 = new Label(); ls24.setText("Suhu 24 Jam :"); ls24.setBounds(230,y,90,23); FormInput.add(ls24);
        CmbSuhu24jam = new ComboBox(); CmbSuhu24jam.setModel(new DefaultComboBoxModel(new String[]{"Tidak Demam","Demam"})); CmbSuhu24jam.setBounds(325,y,110,23); FormInput.add(CmbSuhu24jam);

        y+=28;
        Label lpk = new Label(); lpk.setText("Pemberian Kantong Ke :"); lpk.setBounds(10,y,150,23); FormInput.add(lpk);
        TPemberianKe = new TextBox(); TPemberianKe.setBounds(165,y,60,23); FormInput.add(TPemberianKe);

        // === Section 4: Tanda Vital ===
        y+=35;
        JLabel hs4 = new JLabel("Tanda Vital :"); hs4.setFont(new java.awt.Font("Tahoma",1,11)); hs4.setBounds(10,y,200,20); FormInput.add(hs4);

        y+=25;
        JLabel hsb = new JLabel("Sebelum Transfusi"); hsb.setFont(new java.awt.Font("Tahoma",2,11)); hsb.setBounds(30,y,150,20); FormInput.add(hsb);
        JLabel hss = new JLabel("Sesudah Transfusi"); hss.setFont(new java.awt.Font("Tahoma",2,11)); hss.setBounds(400,y,150,20); FormInput.add(hss);

        y+=22;
        Label lssb = new Label(); lssb.setText("Suhu :"); lssb.setBounds(30,y,45,23); FormInput.add(lssb);
        TSuhuSebelum = new TextBox(); TSuhuSebelum.setBounds(80,y,80,23); FormInput.add(TSuhuSebelum);
        Label ltdsb = new Label(); ltdsb.setText("TD :"); ltdsb.setBounds(175,y,30,23); FormInput.add(ltdsb);
        TTdSebelum = new TextBox(); TTdSebelum.setBounds(210,y,80,23); FormInput.add(TTdSebelum);

        Label lsss = new Label(); lsss.setText("Suhu :"); lsss.setBounds(400,y,45,23); FormInput.add(lsss);
        TSuhuSesudah = new TextBox(); TSuhuSesudah.setBounds(450,y,80,23); FormInput.add(TSuhuSesudah);
        Label ltdss = new Label(); ltdss.setText("TD :"); ltdss.setBounds(545,y,30,23); FormInput.add(ltdss);
        TTdSesudah = new TextBox(); TTdSesudah.setBounds(580,y,80,23); FormInput.add(TTdSesudah);

        y+=28;
        Label lnsb = new Label(); lnsb.setText("Nadi :"); lnsb.setBounds(30,y,45,23); FormInput.add(lnsb);
        TNadiSebelum = new TextBox(); TNadiSebelum.setBounds(80,y,80,23); FormInput.add(TNadiSebelum);
        Label lrsb = new Label(); lrsb.setText("RR :"); lrsb.setBounds(175,y,30,23); FormInput.add(lrsb);
        TRrSebelum = new TextBox(); TRrSebelum.setBounds(210,y,80,23); FormInput.add(TRrSebelum);

        Label lnss = new Label(); lnss.setText("Nadi :"); lnss.setBounds(400,y,45,23); FormInput.add(lnss);
        TNadiSesudah = new TextBox(); TNadiSesudah.setBounds(450,y,80,23); FormInput.add(TNadiSesudah);
        Label lrss = new Label(); lrss.setText("RR :"); lrss.setBounds(545,y,30,23); FormInput.add(lrss);
        TRrSesudah = new TextBox(); TRrSesudah.setBounds(580,y,80,23); FormInput.add(TRrSesudah);

        // === Section 5: Gejala ===
        y+=35;
        JLabel hs5 = new JLabel("Gejala :"); hs5.setFont(new java.awt.Font("Tahoma",1,11)); hs5.setBounds(10,y,200,20); FormInput.add(hs5);

        y+=22;
        ChkGatal = new CekBox(); ChkGatal.setText("Gatal"); ChkGatal.setBounds(30,y,100,23); FormInput.add(ChkGatal);
        ChkSesak = new CekBox(); ChkSesak.setText("Sesak"); ChkSesak.setBounds(140,y,100,23); FormInput.add(ChkSesak);
        ChkNyeriDada = new CekBox(); ChkNyeriDada.setText("Nyeri Dada"); ChkNyeriDada.setBounds(250,y,110,23); FormInput.add(ChkNyeriDada);
        ChkHipotensi = new CekBox(); ChkHipotensi.setText("Hipotensi"); ChkHipotensi.setBounds(370,y,110,23); FormInput.add(ChkHipotensi);

        y+=25;
        ChkPalpitasi = new CekBox(); ChkPalpitasi.setText("Palpitasi"); ChkPalpitasi.setBounds(30,y,100,23); FormInput.add(ChkPalpitasi);
        ChkSakitKepala = new CekBox(); ChkSakitKepala.setText("Sakit Kepala"); ChkSakitKepala.setBounds(140,y,120,23); FormInput.add(ChkSakitKepala);
        ChkNyeriPunggung = new CekBox(); ChkNyeriPunggung.setText("Nyeri Punggung"); ChkNyeriPunggung.setBounds(270,y,130,23); FormInput.add(ChkNyeriPunggung);
        ChkTakikardi = new CekBox(); ChkTakikardi.setText("Takikardi"); ChkTakikardi.setBounds(410,y,110,23); FormInput.add(ChkTakikardi);

        y+=25;
        ChkLemas = new CekBox(); ChkLemas.setText("Lemas"); ChkLemas.setBounds(30,y,100,23); FormInput.add(ChkLemas);
        ChkDemam = new CekBox(); ChkDemam.setText("Demam"); ChkDemam.setBounds(140,y,100,23); FormInput.add(ChkDemam);
        ChkGelisah = new CekBox(); ChkGelisah.setText("Gelisah"); ChkGelisah.setBounds(250,y,100,23); FormInput.add(ChkGelisah);
        ChkPerdarahan = new CekBox(); ChkPerdarahan.setText("Perdarahan"); ChkPerdarahan.setBounds(370,y,120,23); FormInput.add(ChkPerdarahan);

        y+=28;
        Label lgl = new Label(); lgl.setText("Gejala Lainnya :"); lgl.setBounds(10,y,105,23); FormInput.add(lgl);
        TGejalaLainnya = new TextBox(); TGejalaLainnya.setBounds(120,y,400,23); FormInput.add(TGejalaLainnya);

        // === Section 6: Kategori, Penanganan ===
        y+=35;
        JLabel hs6 = new JLabel("Kategori & Penanganan :"); hs6.setFont(new java.awt.Font("Tahoma",1,11)); hs6.setBounds(10,y,250,20); FormInput.add(hs6);

        y+=25;
        Label lkr = new Label(); lkr.setText("Kategori Reaksi :"); lkr.setBounds(10,y,110,23); FormInput.add(lkr);
        CmbKategoriReaksi = new ComboBox(); CmbKategoriReaksi.setModel(new DefaultComboBoxModel(new String[]{"I","II","III"})); CmbKategoriReaksi.setBounds(125,y,60,23); FormInput.add(CmbKategoriReaksi);

        y+=28;
        Label lpn = new Label(); lpn.setText("Penanganan :"); lpn.setBounds(10,y,90,23); FormInput.add(lpn);
        TPenanganan = new TextArea(); TPenanganan.setBounds(105,y,500,50); TPenanganan.setBorder(BorderFactory.createLineBorder(Color.BLACK)); FormInput.add(TPenanganan);

        y+=55;
        Label lpl = new Label(); lpl.setText("Pemeriksaan Lanjutan :"); lpl.setBounds(10,y,145,23); FormInput.add(lpl);
        CmbPemeriksaanLanjutan = new ComboBox(); CmbPemeriksaanLanjutan.setModel(new DefaultComboBoxModel(new String[]{"Ya","Tidak"})); CmbPemeriksaanLanjutan.setBounds(160,y,80,23); FormInput.add(CmbPemeriksaanLanjutan);

        // === Section 7: Dokter, Perawat, Petugas ===
        y+=35;
        JLabel hs7 = new JLabel("Penanggung Jawab :"); hs7.setFont(new java.awt.Font("Tahoma",1,11)); hs7.setBounds(10,y,200,20); FormInput.add(hs7);

        y+=25;
        Label ldk = new Label(); ldk.setText("Dokter :"); ldk.setBounds(10,y,60,23); FormInput.add(ldk);
        KdDokter = new TextBox(); KdDokter.setBounds(75,y,80,23); FormInput.add(KdDokter);
        NmDokter = new TextBox(); NmDokter.setBounds(157,y,200,23); FormInput.add(NmDokter);
        BtnDokter = new Button(); BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); BtnDokter.setBounds(359,y,28,23); FormInput.add(BtnDokter);
        BtnDokter.addActionListener(ev->{dokterDlg.emptTeks();dokterDlg.isCek();dokterDlg.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);dokterDlg.setLocationRelativeTo(internalFrame1);dokterDlg.setVisible(true);});
        dokterDlg.addWindowListener(new java.awt.event.WindowAdapter(){@Override public void windowClosed(java.awt.event.WindowEvent e){if(dokterDlg.getTable().getSelectedRow()!=-1){KdDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(),0).toString());NmDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(),1).toString());}}});

        y+=28;
        Label lpr = new Label(); lpr.setText("Perawat :"); lpr.setBounds(10,y,65,23); FormInput.add(lpr);
        NipPerawat = new TextBox(); NipPerawat.setBounds(75,y,80,23); FormInput.add(NipPerawat);
        NmPerawat = new TextBox(); NmPerawat.setBounds(157,y,200,23); FormInput.add(NmPerawat);
        BtnPerawat = new Button(); BtnPerawat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); BtnPerawat.setBounds(359,y,28,23); FormInput.add(BtnPerawat);
        BtnPerawat.addActionListener(ev->{perawatDlg.emptTeks();perawatDlg.isCek();perawatDlg.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);perawatDlg.setLocationRelativeTo(internalFrame1);perawatDlg.setVisible(true);});
        perawatDlg.addWindowListener(new java.awt.event.WindowAdapter(){@Override public void windowClosed(java.awt.event.WindowEvent e){if(perawatDlg.getTable().getSelectedRow()!=-1){NipPerawat.setText(perawatDlg.getTable().getValueAt(perawatDlg.getTable().getSelectedRow(),0).toString());NmPerawat.setText(perawatDlg.getTable().getValueAt(perawatDlg.getTable().getSelectedRow(),1).toString());}}});

        y+=28;
        Label lpt = new Label(); lpt.setText("Petugas :"); lpt.setBounds(10,y,65,23); FormInput.add(lpt);
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
        Label lk=new Label();lk.setText("Key Word :");lk.setPreferredSize(new Dimension(70,23));
        TCari=new TextBox();TCari.setPreferredSize(new Dimension(150,23));
        BtnCari=new Button();BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));BtnCari.setPreferredSize(new Dimension(28,23));
        BtnAll=new Button();BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));BtnAll.setPreferredSize(new Dimension(28,23));
        LCount=new Label();LCount.setText("Record : 0");LCount.setPreferredSize(new Dimension(100,23));
        panelBawah.add(BtnSimpan);panelBawah.add(BtnBatal);panelBawah.add(BtnHapus);panelBawah.add(BtnEdit);panelBawah.add(BtnPrint);panelBawah.add(BtnKeluar);
        panelBawah.add(lc);panelBawah.add(DTPCari1);panelBawah.add(sd);panelBawah.add(DTPCari2);panelBawah.add(lk);panelBawah.add(TCari);panelBawah.add(BtnCari);panelBawah.add(BtnAll);panelBawah.add(LCount);

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
            String tglDarah=Valid.SetTgl(TglDarahDiambil.getSelectedItem()+"");
            String jamDarah=TglDarahDiambil.getSelectedItem().toString().substring(11,19);
            String tglReak=Valid.SetTgl(TglReaksi.getSelectedItem()+"");
            String jamReak=TglReaksi.getSelectedItem().toString().substring(11,19);
            if(Sequel.menyimpantf("dugaan_reaksi_transfusi_rscb","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?","Data",47,new String[]{
                TNoRw.getText(),tglS,TRuangRawat.getText(),TDiagnosa.getText(),TRiwayatTransfusi.getText(),
                tglPerm,tglDarah,jamDarah,TJamMulaiTransfusi.getText(),TJamSelesaiTransfusi.getText(),
                tglReak,jamReak,TJenisKomponen.getText(),TVolMasuk.getText(),TNoKantongDarah.getText(),
                CmbIdentitasBenar.getSelectedItem().toString(),CmbNoKantongBenar.getSelectedItem().toString(),
                CmbGoldarSesuai.getSelectedItem().toString(),CmbSuhu24jam.getSelectedItem().toString(),TPemberianKe.getText(),
                TSuhuSebelum.getText(),TTdSebelum.getText(),TNadiSebelum.getText(),TRrSebelum.getText(),
                TSuhuSesudah.getText(),TTdSesudah.getText(),TNadiSesudah.getText(),TRrSesudah.getText(),
                ChkGatal.isSelected()?"Ya":"Tidak",ChkSesak.isSelected()?"Ya":"Tidak",
                ChkNyeriDada.isSelected()?"Ya":"Tidak",ChkHipotensi.isSelected()?"Ya":"Tidak",
                ChkPalpitasi.isSelected()?"Ya":"Tidak",ChkSakitKepala.isSelected()?"Ya":"Tidak",
                ChkNyeriPunggung.isSelected()?"Ya":"Tidak",ChkTakikardi.isSelected()?"Ya":"Tidak",
                ChkLemas.isSelected()?"Ya":"Tidak",ChkDemam.isSelected()?"Ya":"Tidak",
                ChkGelisah.isSelected()?"Ya":"Tidak",ChkPerdarahan.isSelected()?"Ya":"Tidak",
                TGejalaLainnya.getText(),CmbKategoriReaksi.getSelectedItem().toString(),TPenanganan.getText(),
                CmbPemeriksaanLanjutan.getSelectedItem().toString(),KdDokter.getText(),NipPerawat.getText(),NIP.getText()
            })){tampil();emptTeks();}
        });

        BtnHapus.addActionListener(ev->{
            if(tbObat.getSelectedRow()!=-1){if(Sequel.queryu2tf("delete from dugaan_reaksi_transfusi_rscb where no_rawat=? and tanggal=?",2,new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()
            })){tampil();emptTeks();}}else JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");
        });

        BtnEdit.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")||tbObat.getSelectedRow()==-1){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            if(Sequel.queryu2tf("delete from dugaan_reaksi_transfusi_rscb where no_rawat=? and tanggal=?",2,new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()
            })){BtnSimpan.doClick();}
        });

        BtnPrint.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            Map<String,Object> p=new HashMap<>();p.put("namars",akses.getnamars());p.put("alamatrs",akses.getalamatrs());
            p.put("propinsirs",akses.getpropinsirs());p.put("kabupatenrs",akses.getkabupatenrs());
            p.put("kontakrs",akses.getkontakrs());p.put("emailrs",akses.getemailrs());
            p.put("logo",Sequel.cariGambar("select setting.logo from setting"));
            String fDokter=Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",KdDokter.getText());
            p.put("fingerDokter","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+NmDokter.getText()+"\nID "+(fDokter.equals("")?KdDokter.getText():fDokter)+"\n"+Valid.SetTgl3(TglAsuhan.getSelectedItem()+""));
            String fPerawat=Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",NipPerawat.getText());
            p.put("fingerPerawat","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+NmPerawat.getText()+"\nID "+(fPerawat.equals("")?NipPerawat.getText():fPerawat)+"\n"+Valid.SetTgl3(TglAsuhan.getSelectedItem()+""));
            Valid.MyReportqry("rptDugaanReaksiTransfusiRscb.jasper","report","::[ Formulir Laporan Dugaan Reaksi Transfusi ]::",
                "select f.*,r.no_rkm_medis,p.nm_pasien,p.jk,p.tgl_lahir,concat(r.umurdaftar,' ',r.sttsumur) as umur,d.nm_dokter,"+
                "pt.nama as nm_perawat,pt2.nama as nm_petugas "+
                "from dugaan_reaksi_transfusi_rscb f inner join reg_periksa r on f.no_rawat=r.no_rawat "+
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis left join dokter d on f.kd_dokter=d.kd_dokter "+
                "left join petugas pt on f.nip_perawat=pt.nip left join petugas pt2 on f.nip=pt2.nip "+
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
        try{ps=koneksi.prepareStatement("select f.no_rawat,r.no_rkm_medis,p.nm_pasien,f.tanggal,f.ruang_rawat,f.jenis_komponen,f.kategori_reaksi,d.nm_dokter,pt.nama as nm_petugas "+
            "from dugaan_reaksi_transfusi_rscb f inner join reg_periksa r on f.no_rawat=r.no_rawat inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "+
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
            try{ps=koneksi.prepareStatement("select f.*,d.nm_dokter,pt.nama as nm_perawat,pt2.nama as nm_petugas from dugaan_reaksi_transfusi_rscb f "+
                "left join dokter d on f.kd_dokter=d.kd_dokter left join petugas pt on f.nip_perawat=pt.nip left join petugas pt2 on f.nip=pt2.nip "+
                "where f.no_rawat=? and f.tanggal=?");
            ps.setString(1,tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());ps.setString(2,tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            rs=ps.executeQuery();if(rs.next()){
                TRuangRawat.setText(rs.getString("ruang_rawat"));TDiagnosa.setText(rs.getString("diagnosa"));TRiwayatTransfusi.setText(rs.getString("riwayat_transfuse"));
                try{if(rs.getString("tgl_permintaan")!=null){String tp=rs.getString("tgl_permintaan");TglPermintaan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tp.length()>10?tp:tp+" 00:00:00"));}}catch(Exception ex){}
                try{if(rs.getString("tgl_darah_diambil")!=null){String jam=rs.getString("jam_darah_diambil")!=null?rs.getString("jam_darah_diambil"):"00:00:00";TglDarahDiambil.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("tgl_darah_diambil")+" "+jam));}}catch(Exception ex){}
                TJamMulaiTransfusi.setText(rs.getString("jam_mulai_transfusi"));TJamSelesaiTransfusi.setText(rs.getString("jam_selesai_transfusi"));
                try{if(rs.getString("tgl_reaksi")!=null){String jam=rs.getString("jam_reaksi")!=null?rs.getString("jam_reaksi"):"00:00:00";TglReaksi.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("tgl_reaksi")+" "+jam));}}catch(Exception ex){}TJenisKomponen.setText(rs.getString("jenis_komponen"));TVolMasuk.setText(rs.getString("vol_masuk"));TNoKantongDarah.setText(rs.getString("no_kantong_darah"));
                CmbIdentitasBenar.setSelectedItem(rs.getString("identitas_benar"));CmbNoKantongBenar.setSelectedItem(rs.getString("no_kantong_benar"));
                CmbGoldarSesuai.setSelectedItem(rs.getString("goldar_sesuai"));CmbSuhu24jam.setSelectedItem(rs.getString("suhu_24jam"));TPemberianKe.setText(rs.getString("pemberian_kantong_ke"));
                TSuhuSebelum.setText(rs.getString("suhu_sebelum"));TTdSebelum.setText(rs.getString("td_sebelum"));TNadiSebelum.setText(rs.getString("nadi_sebelum"));TRrSebelum.setText(rs.getString("rr_sebelum"));
                TSuhuSesudah.setText(rs.getString("suhu_sesudah"));TTdSesudah.setText(rs.getString("td_sesudah"));TNadiSesudah.setText(rs.getString("nadi_sesudah"));TRrSesudah.setText(rs.getString("rr_sesudah"));
                ChkGatal.setSelected(rs.getString("gatal").equals("Ya"));ChkSesak.setSelected(rs.getString("sesak").equals("Ya"));
                ChkNyeriDada.setSelected(rs.getString("nyeri_dada").equals("Ya"));ChkHipotensi.setSelected(rs.getString("hipotensi").equals("Ya"));
                ChkPalpitasi.setSelected(rs.getString("palpitasi").equals("Ya"));ChkSakitKepala.setSelected(rs.getString("sakit_kepala").equals("Ya"));
                ChkNyeriPunggung.setSelected(rs.getString("nyeri_punggung").equals("Ya"));ChkTakikardi.setSelected(rs.getString("takikardi").equals("Ya"));
                ChkLemas.setSelected(rs.getString("lemas").equals("Ya"));ChkDemam.setSelected(rs.getString("demam").equals("Ya"));
                ChkGelisah.setSelected(rs.getString("gelisah").equals("Ya"));ChkPerdarahan.setSelected(rs.getString("perdarahan").equals("Ya"));
                TGejalaLainnya.setText(rs.getString("gejala_lainnya"));CmbKategoriReaksi.setSelectedItem(rs.getString("kategori_reaksi"));TPenanganan.setText(rs.getString("penanganan"));
                CmbPemeriksaanLanjutan.setSelectedItem(rs.getString("pemeriksaan_lanjutan"));
                KdDokter.setText(rs.getString("kd_dokter"));NmDokter.setText(rs.getString("nm_dokter"));
                NipPerawat.setText(rs.getString("nip_perawat"));NmPerawat.setText(rs.getString("nm_perawat"));
                NIP.setText(rs.getString("nip"));NamaPetugas.setText(rs.getString("nm_petugas"));
            }}catch(Exception e){System.out.println("Notif getData : "+e);}finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
        }
    }

    public void emptTeks(){
        TglAsuhan.setDate(new Date());TglPermintaan.setDate(new Date());TglDarahDiambil.setDate(new Date());TglReaksi.setDate(new Date());
        TRuangRawat.setText("");TDiagnosa.setText("");TRiwayatTransfusi.setText("");
        TJamMulaiTransfusi.setText("");TJamSelesaiTransfusi.setText("");
        TJenisKomponen.setText("");TVolMasuk.setText("");TNoKantongDarah.setText("");TPemberianKe.setText("");
        CmbIdentitasBenar.setSelectedIndex(0);CmbNoKantongBenar.setSelectedIndex(0);CmbGoldarSesuai.setSelectedIndex(0);CmbSuhu24jam.setSelectedIndex(0);
        TSuhuSebelum.setText("");TTdSebelum.setText("");TNadiSebelum.setText("");TRrSebelum.setText("");
        TSuhuSesudah.setText("");TTdSesudah.setText("");TNadiSesudah.setText("");TRrSesudah.setText("");
        ChkGatal.setSelected(false);ChkSesak.setSelected(false);ChkNyeriDada.setSelected(false);ChkHipotensi.setSelected(false);
        ChkPalpitasi.setSelected(false);ChkSakitKepala.setSelected(false);ChkNyeriPunggung.setSelected(false);ChkTakikardi.setSelected(false);
        ChkLemas.setSelected(false);ChkDemam.setSelected(false);ChkGelisah.setSelected(false);ChkPerdarahan.setSelected(false);
        TGejalaLainnya.setText("");CmbKategoriReaksi.setSelectedIndex(0);TPenanganan.setText("");CmbPemeriksaanLanjutan.setSelectedIndex(0);
        KdDokter.setText("");NmDokter.setText("");NipPerawat.setText("");NmPerawat.setText("");NIP.setText("");NamaPetugas.setText("");
    }

    public void setNoRm(String n,String m,String p){TNoRw.setText(n);TNoRM.setText(m);TPasien.setText(p);TRuangRawat.setText(Sequel.cariIsi("select bangsal.nm_bangsal from bangsal inner join kamar on bangsal.kd_bangsal=kamar.kd_bangsal inner join kamar_inap on kamar.kd_kamar=kamar_inap.kd_kamar where kamar_inap.no_rawat=? and stts_pulang='-' order by STR_TO_DATE(concat(kamar_inap.tgl_masuk,' ',jam_masuk),'%Y-%m-%d %H:%i:%s') desc limit 1",n));}
    public void setNoRm(String n,Date t){TNoRw.setText(n);TglAsuhan.setDate(t);TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?",n));TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?",TNoRM.getText()));TRuangRawat.setText(Sequel.cariIsi("select bangsal.nm_bangsal from bangsal inner join kamar on bangsal.kd_bangsal=kamar.kd_bangsal inner join kamar_inap on kamar.kd_kamar=kamar_inap.kd_kamar where kamar_inap.no_rawat=? and stts_pulang='-' order by STR_TO_DATE(concat(kamar_inap.tgl_masuk,' ',jam_masuk),'%Y-%m-%d %H:%i:%s') desc limit 1",n));}
    public void isCek(){BtnSimpan.setEnabled(akses.getrujukan_keluar());BtnHapus.setEnabled(akses.getrujukan_keluar());BtnEdit.setEnabled(akses.getrujukan_keluar());BtnPrint.setEnabled(akses.getrujukan_keluar());}
}
