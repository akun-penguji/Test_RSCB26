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

public class RMRingkasanRiwayatMKRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll, BtnDokter, BtnPetugas;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan, TglMasuk, TglKeluar;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private TextBox TTmpTglLahir, TAlamat, TKelurahan, TKecamatan, TKabupaten, TTelpon;
    private TextBox KdDokter, NmDokter, NipPetugas, NmPetugas;
    private TextBox TBBLahir, TPerujuk, TPJPasien, TRuangRawat, TKelas, TLamaRawat;
    private TextBox TDiagnosaUtama, TIcdXUtama, TDiagTambahan1, TDiagTambahan2, TDiagTambahan3, TDiagTambahan4;
    private TextBox TStatusPulangLain, TTindakanUtama, TIcdIXUtama, TTindTambahan1, TTindTambahan2, TTindTambahan3, TTindTambahan4;
    private TextArea TADiagnosaMasuk, TAPenyebabKematian;
    private ComboBox CmbPembiayaan, CmbStatusNikah, CmbStatusPulang;
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
    private JTabbedPane TabRM;
    public widget.editorpane LoadHTML;

    public RMRingkasanRiwayatMKRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(240,245,235)),
            "::[ Ringkasan Riwayat Masuk & Keluar ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma",0,11), new Color(50,50,50)));
        internalFrame1.setLayout(new BorderLayout(1,1));

        panelisi panelAtas = new panelisi();
        panelAtas.setLayout(new java.awt.GridLayout(2,1));
        panelAtas.setPreferredSize(new Dimension(1024,62));

        // Baris 1: No.Rawat, No.RM, Nama, Tmp/Tgl Lahir, Status (read-only auto dari pasien)
        javax.swing.JPanel baris1 = new javax.swing.JPanel(new FlowLayout(FlowLayout.LEFT,5,3)); baris1.setOpaque(false);
        Label lblNoRw = new Label(); lblNoRw.setText("No.Rawat :");
        TNoRw = new TextBox(); TNoRw.setPreferredSize(new Dimension(130,23));
        TNoRM = new TextBox(); TNoRM.setEditable(false); TNoRM.setPreferredSize(new Dimension(90,23));
        TPasien = new TextBox(); TPasien.setEditable(false); TPasien.setPreferredSize(new Dimension(180,23));
        baris1.add(lblNoRw); baris1.add(TNoRw); baris1.add(TNoRM); baris1.add(TPasien);
        Label lTtl = new Label(); lTtl.setText("Tmp/Tgl Lahir :"); baris1.add(lTtl);
        TTmpTglLahir = new TextBox(); TTmpTglLahir.setEditable(false); TTmpTglLahir.setPreferredSize(new Dimension(180,23)); baris1.add(TTmpTglLahir);
        Label lSn = new Label(); lSn.setText("Status Nikah :"); baris1.add(lSn);
        CmbStatusNikah = new ComboBox(); CmbStatusNikah.setModel(new DefaultComboBoxModel(new String[]{"Menikah","Belum Menikah"})); CmbStatusNikah.setPreferredSize(new Dimension(120,23)); baris1.add(CmbStatusNikah);
        Label lPb = new Label(); lPb.setText("Pembiayaan :"); baris1.add(lPb);
        CmbPembiayaan = new ComboBox(); CmbPembiayaan.setModel(new DefaultComboBoxModel(new String[]{"Umum","Asuransi","Perusahaan","BPJS"})); CmbPembiayaan.setPreferredSize(new Dimension(120,23)); baris1.add(CmbPembiayaan);

        // Baris 2: Alamat, Kel, Kec, Kab, Telp (read-only auto dari pasien)
        javax.swing.JPanel baris2 = new javax.swing.JPanel(new FlowLayout(FlowLayout.LEFT,5,3)); baris2.setOpaque(false);
        Label lAl = new Label(); lAl.setText("Alamat :"); baris2.add(lAl);
        TAlamat = new TextBox(); TAlamat.setEditable(false); TAlamat.setPreferredSize(new Dimension(230,23)); baris2.add(TAlamat);
        Label lKel = new Label(); lKel.setText("Kel :"); baris2.add(lKel);
        TKelurahan = new TextBox(); TKelurahan.setEditable(false); TKelurahan.setPreferredSize(new Dimension(120,23)); baris2.add(TKelurahan);
        Label lKec = new Label(); lKec.setText("Kec :"); baris2.add(lKec);
        TKecamatan = new TextBox(); TKecamatan.setEditable(false); TKecamatan.setPreferredSize(new Dimension(120,23)); baris2.add(TKecamatan);
        Label lKab = new Label(); lKab.setText("Kab :"); baris2.add(lKab);
        TKabupaten = new TextBox(); TKabupaten.setEditable(false); TKabupaten.setPreferredSize(new Dimension(150,23)); baris2.add(TKabupaten);
        Label lTlp = new Label(); lTlp.setText("Telp :"); baris2.add(lTlp);
        TTelpon = new TextBox(); TTelpon.setEditable(false); TTelpon.setPreferredSize(new Dimension(110,23)); baris2.add(TTelpon);
        Label lDpjp = new Label(); lDpjp.setText("DPJP :"); baris2.add(lDpjp);
        KdDokter = new TextBox(); KdDokter.setEditable(false); KdDokter.setPreferredSize(new Dimension(70,23)); baris2.add(KdDokter);
        NmDokter = new TextBox(); NmDokter.setEditable(false); NmDokter.setPreferredSize(new Dimension(170,23)); baris2.add(NmDokter);

        panelAtas.add(baris1); panelAtas.add(baris2);

        Scroll = new ScrollPane();
        tbObat = new widget.Table();
        tabMode = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","Diagnosa Masuk","Status Pulang","DPJP","Petugas"}
        ) { @Override public boolean isCellEditable(int r,int c){return false;} };
        tbObat.setModel(tabMode);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override public void mouseClicked(java.awt.event.MouseEvent e){
                if(tabMode.getRowCount()!=0){ getData(); if(TabRM!=null) TabRM.setSelectedIndex(0); }
            }
        });
        JPopupMenu pop = new JPopupMenu();
        JMenuItem mnCetak = new JMenuItem("Cetak Ringkasan Riwayat Masuk Keluar");
        mnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetak.addActionListener(evt -> BtnPrint.doClick());
        pop.add(mnCetak);
        tbObat.setComponentPopupMenu(pop);
        Scroll.setViewportView(tbObat);

        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(880,780));
        FormInput.setLayout(null);

        int y = 10;
        JLabel h = new JLabel("RINGKASAN RIWAYAT MASUK & KELUAR");
        h.setFont(new java.awt.Font("Tahoma",1,12)); h.setBounds(10,y,400,20); FormInput.add(h);
        JLabel hk = new JLabel("RM.KEP.08.027.2019"); hk.setFont(new java.awt.Font("Tahoma",0,8)); hk.setBounds(700,y,150,20); FormInput.add(hk);

        y+=32;
        Label lt = new Label(); lt.setText("Tanggal :"); lt.setBounds(10,y,80,23); FormInput.add(lt);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TglAsuhan.setBounds(95,y,130,23); FormInput.add(TglAsuhan);

        y+=32;
        Label lbb = new Label(); lbb.setText("BB Lahir :"); lbb.setBounds(10,y,80,23); FormInput.add(lbb);
        TBBLahir = new TextBox(); TBBLahir.setBounds(95,y,80,23); FormInput.add(TBBLahir);
        Label lgr = new Label(); lgr.setText("gr"); lgr.setBounds(178,y,20,23); FormInput.add(lgr);

        Label lpr = new Label(); lpr.setText("Perujuk :"); lpr.setBounds(280,y,70,23); FormInput.add(lpr);
        TPerujuk = new TextBox(); TPerujuk.setBounds(355,y,300,23); FormInput.add(TPerujuk);

        y+=32;
        Label ltm = new Label(); ltm.setText("Tgl Masuk :"); ltm.setBounds(10,y,80,23); FormInput.add(ltm);
        TglMasuk = new widget.Tanggal(); TglMasuk.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TglMasuk.setBounds(95,y,130,23); FormInput.add(TglMasuk);

        Label ltk = new Label(); ltk.setText("Tgl Keluar :"); ltk.setBounds(280,y,80,23); FormInput.add(ltk);
        TglKeluar = new widget.Tanggal(); TglKeluar.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TglKeluar.setBounds(365,y,130,23); FormInput.add(TglKeluar);

        y+=32;
        Label lpj = new Label(); lpj.setText("PJ Pasien :"); lpj.setBounds(10,y,80,23); FormInput.add(lpj);
        TPJPasien = new TextBox(); TPJPasien.setBounds(95,y,300,23); FormInput.add(TPJPasien);

        y+=32;
        Label lrr = new Label(); lrr.setText("Ruang Rawat :"); lrr.setBounds(10,y,90,23); FormInput.add(lrr);
        TRuangRawat = new TextBox(); TRuangRawat.setBounds(105,y,200,23); FormInput.add(TRuangRawat);
        Label lkl = new Label(); lkl.setText("Kelas :"); lkl.setBounds(320,y,50,23); FormInput.add(lkl);
        TKelas = new TextBox(); TKelas.setBounds(375,y,100,23); FormInput.add(TKelas);

        y+=32;
        Label ldm = new Label(); ldm.setText("Diagnosa Masuk :"); ldm.setBounds(10,y,110,23); FormInput.add(ldm);
        TADiagnosaMasuk = new TextArea(); TADiagnosaMasuk.setBounds(125,y,530,50); FormInput.add(TADiagnosaMasuk);

        y+=58;
        Label llr = new Label(); llr.setText("Lama Rawat :"); llr.setBounds(10,y,90,23); FormInput.add(llr);
        TLamaRawat = new TextBox(); TLamaRawat.setBounds(105,y,60,23); FormInput.add(TLamaRawat);
        Label lhr = new Label(); lhr.setText("hari"); lhr.setBounds(168,y,30,23); FormInput.add(lhr);

        y+=32;
        Label ldu = new Label(); ldu.setText("Diagnosa Utama :"); ldu.setBounds(10,y,110,23); FormInput.add(ldu);
        TDiagnosaUtama = new TextBox(); TDiagnosaUtama.setBounds(125,y,350,23); FormInput.add(TDiagnosaUtama);
        Label lix = new Label(); lix.setText("ICD-X :"); lix.setBounds(490,y,50,23); FormInput.add(lix);
        TIcdXUtama = new TextBox(); TIcdXUtama.setBounds(545,y,110,23); FormInput.add(TIcdXUtama);

        y+=32;
        Label ldt1 = new Label(); ldt1.setText("Diag. Tambahan 1 :"); ldt1.setBounds(10,y,120,23); FormInput.add(ldt1);
        TDiagTambahan1 = new TextBox(); TDiagTambahan1.setBounds(135,y,520,23); FormInput.add(TDiagTambahan1);
        y+=28;
        Label ldt2 = new Label(); ldt2.setText("Diag. Tambahan 2 :"); ldt2.setBounds(10,y,120,23); FormInput.add(ldt2);
        TDiagTambahan2 = new TextBox(); TDiagTambahan2.setBounds(135,y,520,23); FormInput.add(TDiagTambahan2);
        y+=28;
        Label ldt3 = new Label(); ldt3.setText("Diag. Tambahan 3 :"); ldt3.setBounds(10,y,120,23); FormInput.add(ldt3);
        TDiagTambahan3 = new TextBox(); TDiagTambahan3.setBounds(135,y,520,23); FormInput.add(TDiagTambahan3);
        y+=28;
        Label ldt4 = new Label(); ldt4.setText("Diag. Tambahan 4 :"); ldt4.setBounds(10,y,120,23); FormInput.add(ldt4);
        TDiagTambahan4 = new TextBox(); TDiagTambahan4.setBounds(135,y,520,23); FormInput.add(TDiagTambahan4);

        y+=32;
        Label lsp = new Label(); lsp.setText("Status Pulang :"); lsp.setBounds(10,y,100,23); FormInput.add(lsp);
        CmbStatusPulang = new ComboBox(); CmbStatusPulang.setModel(new DefaultComboBoxModel(new String[]{"Dipulangkan","Rujuk","APS","Meninggal < 48 Jam","Meninggal > 48 Jam","Lain-lain"}));
        CmbStatusPulang.setBounds(115,y,180,23); FormInput.add(CmbStatusPulang);
        Label lspl = new Label(); lspl.setText("Lain-lain :"); lspl.setBounds(310,y,65,23); FormInput.add(lspl);
        TStatusPulangLain = new TextBox(); TStatusPulangLain.setBounds(380,y,275,23); TStatusPulangLain.setEnabled(false); FormInput.add(TStatusPulangLain);
        CmbStatusPulang.addActionListener(ev->{TStatusPulangLain.setEnabled("Lain-lain".equals(CmbStatusPulang.getSelectedItem()));});

        y+=32;
        Label ltu = new Label(); ltu.setText("Tindakan Utama :"); ltu.setBounds(10,y,110,23); FormInput.add(ltu);
        TTindakanUtama = new TextBox(); TTindakanUtama.setBounds(125,y,350,23); FormInput.add(TTindakanUtama);
        Label lix2 = new Label(); lix2.setText("ICD-IX :"); lix2.setBounds(490,y,50,23); FormInput.add(lix2);
        TIcdIXUtama = new TextBox(); TIcdIXUtama.setBounds(545,y,110,23); FormInput.add(TIcdIXUtama);

        y+=32;
        Label ltt1 = new Label(); ltt1.setText("Tind. Tambahan 1 :"); ltt1.setBounds(10,y,120,23); FormInput.add(ltt1);
        TTindTambahan1 = new TextBox(); TTindTambahan1.setBounds(135,y,520,23); FormInput.add(TTindTambahan1);
        y+=28;
        Label ltt2 = new Label(); ltt2.setText("Tind. Tambahan 2 :"); ltt2.setBounds(10,y,120,23); FormInput.add(ltt2);
        TTindTambahan2 = new TextBox(); TTindTambahan2.setBounds(135,y,520,23); FormInput.add(TTindTambahan2);
        y+=28;
        Label ltt3 = new Label(); ltt3.setText("Tind. Tambahan 3 :"); ltt3.setBounds(10,y,120,23); FormInput.add(ltt3);
        TTindTambahan3 = new TextBox(); TTindTambahan3.setBounds(135,y,520,23); FormInput.add(TTindTambahan3);
        y+=28;
        Label ltt4 = new Label(); ltt4.setText("Tind. Tambahan 4 :"); ltt4.setBounds(10,y,120,23); FormInput.add(ltt4);
        TTindTambahan4 = new TextBox(); TTindTambahan4.setBounds(135,y,520,23); FormInput.add(TTindTambahan4);

        y+=32;
        Label lpk = new Label(); lpk.setText("Penyebab Kematian :"); lpk.setBounds(10,y,125,23); FormInput.add(lpk);
        TAPenyebabKematian = new TextArea(); TAPenyebabKematian.setBounds(140,y,515,50); FormInput.add(TAPenyebabKematian);

        y+=58;
        Label lpt = new Label(); lpt.setText("Petugas :"); lpt.setBounds(10,y,70,23); FormInput.add(lpt);
        NipPetugas = new TextBox(); NipPetugas.setBounds(85,y,80,23); FormInput.add(NipPetugas);
        NmPetugas = new TextBox(); NmPetugas.setBounds(167,y,200,23); FormInput.add(NmPetugas);
        BtnPetugas = new Button(); BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); BtnPetugas.setBounds(369,y,28,23); FormInput.add(BtnPetugas);
        BtnPetugas.addActionListener(ev->{petugasDlg.emptTeks();petugasDlg.isCek();petugasDlg.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);petugasDlg.setLocationRelativeTo(internalFrame1);petugasDlg.setVisible(true);});
        petugasDlg.addWindowListener(new java.awt.event.WindowAdapter(){@Override public void windowClosed(java.awt.event.WindowEvent e){if(petugasDlg.getTable().getSelectedRow()!=-1){NipPetugas.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(),0).toString());NmPetugas.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(),1).toString());}}});

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
            if(KdDokter.getText().trim().equals("")){Valid.textKosong(KdDokter,"Dokter");return;}
            if(NipPetugas.getText().trim().equals("")){Valid.textKosong(NipPetugas,"Petugas");return;}
            String tglS=Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19);
            String tglM=Valid.SetTgl(TglMasuk.getSelectedItem()+"")+" "+TglMasuk.getSelectedItem().toString().substring(11,19);
            String tglK=Valid.SetTgl(TglKeluar.getSelectedItem()+"")+" "+TglKeluar.getSelectedItem().toString().substring(11,19);
            if(Sequel.menyimpantf("ringkasan_riwayat_masuk_keluar_rscb","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?","Data",30,new String[]{
                TNoRw.getText(),tglS,
                CmbPembiayaan.getSelectedItem().toString(),CmbStatusNikah.getSelectedItem().toString(),
                TBBLahir.getText(),TPerujuk.getText(),KdDokter.getText(),
                tglM,tglK,
                TPJPasien.getText(),TRuangRawat.getText(),TKelas.getText(),
                TADiagnosaMasuk.getText(),TLamaRawat.getText(),
                TDiagnosaUtama.getText(),TIcdXUtama.getText(),
                TDiagTambahan1.getText(),TDiagTambahan2.getText(),TDiagTambahan3.getText(),TDiagTambahan4.getText(),
                CmbStatusPulang.getSelectedItem().toString(),TStatusPulangLain.getText(),
                TTindakanUtama.getText(),TIcdIXUtama.getText(),
                TAPenyebabKematian.getText(),
                TTindTambahan1.getText(),TTindTambahan2.getText(),TTindTambahan3.getText(),TTindTambahan4.getText(),
                NipPetugas.getText()
            })){tampil();emptTeks();}
        });

        BtnHapus.addActionListener(ev->{
            if(tbObat.getSelectedRow()!=-1){if(Sequel.queryu2tf("delete from ringkasan_riwayat_masuk_keluar_rscb where no_rawat=? and tanggal=?",2,new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()
            })){tampil();emptTeks();}}else JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");
        });

        BtnEdit.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")||tbObat.getSelectedRow()==-1){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            if(Sequel.queryu2tf("delete from ringkasan_riwayat_masuk_keluar_rscb where no_rawat=? and tanggal=?",2,new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()
            })){BtnSimpan.doClick();}
        });

        BtnPrint.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")||tbObat.getSelectedRow()==-1){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            Map<String,Object> p=new HashMap<>();p.put("namars",akses.getnamars());p.put("alamatrs",akses.getalamatrs());
            p.put("propinsirs",akses.getpropinsirs());p.put("kabupatenrs",akses.getkabupatenrs());
            p.put("kontakrs",akses.getkontakrs());p.put("emailrs",akses.getemailrs());
            p.put("logo",Sequel.cariGambar("select setting.logo from setting"));
            // f.tanggal diambil persis dari nilai tabel (sama seperti BtnHapus/BtnEdit),
            // BUKAN direkonstruksi dari TglAsuhan.getSelectedItem() — itu bisa meleset
            // dari nilai yang tersimpan di DB sehingga WHERE tidak match ("no pages").
            Valid.MyReportqry("rptRingkasanRiwayatMKRscb.jasper","report","::[ Ringkasan Riwayat Masuk & Keluar ]::",
                "select f.*,r.no_rkm_medis,p.nm_pasien,p.jk,concat(r.umurdaftar,' ',r.sttsumur) as umur,d.nm_dokter,pt.nama as nm_petugas "+
                "from ringkasan_riwayat_masuk_keluar_rscb f inner join reg_periksa r on f.no_rawat=r.no_rawat "+
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis left join dokter d on f.kd_dokter=d.kd_dokter "+
                "left join petugas pt on f.nip=pt.nip "+
                "where f.no_rawat='"+TNoRw.getText()+"' and f.tanggal='"+tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()+"'",p);
        });

        // 2 tab: "Input" (form) & "Data Inputan" (tabel)
        TabRM = new JTabbedPane();
        TabRM.addTab("Input", PanelInput);
        TabRM.addTab("Data Inputan", Scroll);
        internalFrame1.add(panelAtas,BorderLayout.PAGE_START);internalFrame1.add(TabRM,BorderLayout.CENTER);internalFrame1.add(panelBawah,BorderLayout.PAGE_END);
        setLayout(new BorderLayout());add(internalFrame1,BorderLayout.CENTER);setDefaultCloseOperation(DISPOSE_ON_CLOSE);setUndecorated(true);
    }

    private void isForm(){if(ChkInput.isSelected()){ChkInput.setVisible(false);PanelInput.setPreferredSize(new Dimension(WIDTH,440));FormInput.setVisible(true);ChkInput.setVisible(true);}else{ChkInput.setVisible(false);PanelInput.setPreferredSize(new Dimension(WIDTH,20));FormInput.setVisible(false);ChkInput.setVisible(true);}}

    public void tampil(){
        Valid.tabelKosong(tabMode);
        try{ps=koneksi.prepareStatement("select f.no_rawat,r.no_rkm_medis,p.nm_pasien,f.tanggal,f.diagnosa_masuk,f.status_pulang,d.nm_dokter,pt.nama as nm_petugas "+
            "from ringkasan_riwayat_masuk_keluar_rscb f inner join reg_periksa r on f.no_rawat=r.no_rawat inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "+
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
            isiDataPasien(); // demografi + default; nilai tersimpan di-override di bawah
            try{TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()));}catch(Exception e){}
            try{ps=koneksi.prepareStatement("select f.*,d.nm_dokter,pt.nama as nm_petugas from ringkasan_riwayat_masuk_keluar_rscb f left join dokter d on f.kd_dokter=d.kd_dokter left join petugas pt on f.nip=pt.nip where f.no_rawat=? and f.tanggal=?");
            ps.setString(1,tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());ps.setString(2,tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            rs=ps.executeQuery();if(rs.next()){
                CmbPembiayaan.setSelectedItem(rs.getString("pembiayaan"));CmbStatusNikah.setSelectedItem(rs.getString("status_nikah"));
                TBBLahir.setText(rs.getString("bb_lahir"));TPerujuk.setText(rs.getString("perujuk"));
                KdDokter.setText(rs.getString("kd_dokter"));NmDokter.setText(rs.getString("nm_dokter"));
                try{if(rs.getString("tgl_masuk")!=null)TglMasuk.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("tgl_masuk")));}catch(Exception ex){}
                try{if(rs.getString("tgl_keluar")!=null)TglKeluar.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("tgl_keluar")));}catch(Exception ex){}
                TPJPasien.setText(rs.getString("pj_pasien"));TRuangRawat.setText(rs.getString("ruang_rawat_terakhir"));TKelas.setText(rs.getString("kelas"));
                TADiagnosaMasuk.setText(rs.getString("diagnosa_masuk"));TLamaRawat.setText(rs.getString("lama_rawat"));
                TDiagnosaUtama.setText(rs.getString("diagnosa_utama"));TIcdXUtama.setText(rs.getString("icd_x_utama"));
                TDiagTambahan1.setText(rs.getString("diagnosa_tambahan_1"));TDiagTambahan2.setText(rs.getString("diagnosa_tambahan_2"));
                TDiagTambahan3.setText(rs.getString("diagnosa_tambahan_3"));TDiagTambahan4.setText(rs.getString("diagnosa_tambahan_4"));
                CmbStatusPulang.setSelectedItem(rs.getString("status_pulang"));TStatusPulangLain.setText(rs.getString("status_pulang_lain"));
                TStatusPulangLain.setEnabled("Lain-lain".equals(rs.getString("status_pulang")));
                TTindakanUtama.setText(rs.getString("tindakan_utama"));TIcdIXUtama.setText(rs.getString("icd_ix_utama"));
                TAPenyebabKematian.setText(rs.getString("penyebab_kematian"));
                TTindTambahan1.setText(rs.getString("tindakan_tambahan_1"));TTindTambahan2.setText(rs.getString("tindakan_tambahan_2"));
                TTindTambahan3.setText(rs.getString("tindakan_tambahan_3"));TTindTambahan4.setText(rs.getString("tindakan_tambahan_4"));
                NipPetugas.setText(rs.getString("nip"));NmPetugas.setText(rs.getString("nm_petugas"));
            }}catch(Exception e){System.out.println("Notif getData : "+e);}finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
        }
    }

    public void emptTeks(){TglAsuhan.setDate(new Date());TglMasuk.setDate(new Date());TglKeluar.setDate(new Date());
        CmbPembiayaan.setSelectedIndex(0);CmbStatusNikah.setSelectedIndex(0);CmbStatusPulang.setSelectedIndex(0);
        TBBLahir.setText("");TPerujuk.setText("");KdDokter.setText("");NmDokter.setText("");
        TPJPasien.setText("");TRuangRawat.setText("");TKelas.setText("");TADiagnosaMasuk.setText("");TLamaRawat.setText("");
        TDiagnosaUtama.setText("");TIcdXUtama.setText("");TDiagTambahan1.setText("");TDiagTambahan2.setText("");TDiagTambahan3.setText("");TDiagTambahan4.setText("");
        TStatusPulangLain.setText("");TStatusPulangLain.setEnabled(false);TTindakanUtama.setText("");TIcdIXUtama.setText("");
        TAPenyebabKematian.setText("");TTindTambahan1.setText("");TTindTambahan2.setText("");TTindTambahan3.setText("");TTindTambahan4.setText("");
        NipPetugas.setText("");NmPetugas.setText("");
        TTmpTglLahir.setText("");TAlamat.setText("");TKelurahan.setText("");TKecamatan.setText("");TKabupaten.setText("");TTelpon.setText("");}
    public void setNoRm(String n,String m,String p){TNoRw.setText(n);TNoRM.setText(m);TPasien.setText(p);isiDataPasien();}
    public void setNoRm(String n,Date t){TNoRw.setText(n);TglAsuhan.setDate(t);TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?",n));TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?",TNoRM.getText()));isiDataPasien();}

    /** Auto-isi data pasien (read-only) + default Pembiayaan/Status/DPJP/PJ dari DB. */
    public void isiDataPasien(){
        if(TNoRw.getText().trim().isEmpty()) return;
        try{
            ps=koneksi.prepareStatement(
                "select ps.tmp_lahir,ps.tgl_lahir,ps.stts_nikah,ps.alamat,ps.no_tlp,"+
                "IFNULL(kel.nm_kel,''),IFNULL(kec.nm_kec,''),IFNULL(kab.nm_kab,''),"+
                "IFNULL(pj.png_jawab,''),IFNULL(reg.p_jawab,'') "+
                "from reg_periksa reg inner join pasien ps on reg.no_rkm_medis=ps.no_rkm_medis "+
                "left join kelurahan kel on ps.kd_kel=kel.kd_kel "+
                "left join kecamatan kec on ps.kd_kec=kec.kd_kec "+
                "left join kabupaten kab on ps.kd_kab=kab.kd_kab "+
                "left join penjab pj on reg.kd_pj=pj.kd_pj where reg.no_rawat=?");
            ps.setString(1,TNoRw.getText());
            rs=ps.executeQuery();
            boolean ada=rs.next();
            if(ada){
                String tmp=rs.getString(1)==null?"":rs.getString(1);
                String tgl=rs.getString(2)==null?"":rs.getString(2);
                if(tgl.length()==10){String[] d=tgl.split("-");tgl=d[2]+"-"+d[1]+"-"+d[0];}
                TTmpTglLahir.setText((tmp+" / "+tgl).trim());
                String sn=rs.getString(3)==null?"":rs.getString(3);
                CmbStatusNikah.setSelectedItem("MENIKAH".equalsIgnoreCase(sn)?"Menikah":"Belum Menikah");
                TAlamat.setText(rs.getString(4)==null?"":rs.getString(4));
                TTelpon.setText(rs.getString(5)==null?"":rs.getString(5));
                TKelurahan.setText(rs.getString(6));TKecamatan.setText(rs.getString(7));TKabupaten.setText(rs.getString(8));
                CmbPembiayaan.setSelectedItem(mapPembiayaan(rs.getString(9)==null?"":rs.getString(9)));
                TPJPasien.setText(rs.getString(10)==null?"":rs.getString(10));
            }
            rs.close();ps.close();
            // DPJP dari dpjp_ranap (ambil 1)
            ps=koneksi.prepareStatement("select d.kd_dokter,IFNULL(dok.nm_dokter,'') from dpjp_ranap d left join dokter dok on d.kd_dokter=dok.kd_dokter where d.no_rawat=? limit 1");
            ps.setString(1,TNoRw.getText());rs=ps.executeQuery();
            if(rs.next()){KdDokter.setText(rs.getString(1));NmDokter.setText(rs.getString(2));}
            rs.close();ps.close();

            // Berat Badan Lahir dari pasien_bayi (by no_rkm_medis)
            TBBLahir.setText(Sequel.cariIsi("select berat_badan from pasien_bayi where no_rkm_medis=?",TNoRM.getText()));

            // Perujuk dari rujuk_masuk
            TPerujuk.setText(Sequel.cariIsi("select perujuk from rujuk_masuk where no_rawat=?",TNoRw.getText()));

            // Data ranap dari kamar_inap: episode PERTAMA untuk tgl masuk,
            // episode TERAKHIR untuk ruang/kelas/tgl keluar/status pulang.
            // Lama rawat dijumlah dari semua episode (pasien bisa pindah kamar).
            jamRawat=-1;
            ps=koneksi.prepareStatement(
                "select min(concat(ki.tgl_masuk,' ',ki.jam_masuk)) as masuk_awal,"+
                "sum(ifnull(ki.lama,0)) as total_lama,"+
                // Selisih jam sebenarnya, dipakai untuk membedakan meninggal <48/>48 jam.
                // Kolom `lama` tidak bisa dipakai: itu hitungan hari kalender yang
                // dibulatkan, mis. rawat 61 jam pun bisa tercatat lama=2.
                "timestampdiff(hour,min(concat(ki.tgl_masuk,' ',ki.jam_masuk)),"+
                "max(if(ki.tgl_keluar is null or ki.tgl_keluar='0000-00-00',null,"+
                "concat(ki.tgl_keluar,' ',ifnull(ki.jam_keluar,'00:00:00'))))) as jam_rawat "+
                "from kamar_inap ki where ki.no_rawat=?");
            ps.setString(1,TNoRw.getText());rs=ps.executeQuery();
            if(rs.next()){
                setTanggal(TglMasuk,rs.getString("masuk_awal"));
                String lama=g(rs.getString("total_lama"));
                if(!lama.isEmpty()) TLamaRawat.setText(lama.endsWith(".0")?lama.substring(0,lama.length()-2):lama);
                String jr=g(rs.getString("jam_rawat"));
                if(!jr.isEmpty()){try{jamRawat=Integer.parseInt(jr);}catch(Exception e){}}
            }
            rs.close();ps.close();

            ps=koneksi.prepareStatement(
                // LEFT JOIN: kd_kamar lama kadang sudah tidak ada di master kamar.
                // Dengan INNER JOIN barisnya hilang dan tgl keluar/status pulang
                // ikut tidak tertarik padahal datanya ada di kamar_inap.
                "select ifnull(b.nm_bangsal,'') as nm_bangsal,ifnull(k.kelas,'') as kelas,"+
                "ki.tgl_keluar,ki.jam_keluar,ki.stts_pulang,ki.diagnosa_awal "+
                "from kamar_inap ki left join kamar k on ki.kd_kamar=k.kd_kamar "+
                "left join bangsal b on k.kd_bangsal=b.kd_bangsal "+
                "where ki.no_rawat=? order by ki.tgl_masuk desc,ki.jam_masuk desc limit 1");
            ps.setString(1,TNoRw.getText());rs=ps.executeQuery();
            if(rs.next()){
                TRuangRawat.setText(g(rs.getString("nm_bangsal")));
                TKelas.setText(g(rs.getString("kelas")));
                String tglKel=g(rs.getString("tgl_keluar"));
                // tgl_keluar masih null selama pasien belum pulang — biarkan default hari ini
                if(!tglKel.isEmpty()&&!tglKel.startsWith("0000")){
                    setTanggal(TglKeluar,tglKel+" "+(g(rs.getString("jam_keluar")).isEmpty()?"00:00:00":rs.getString("jam_keluar")));
                }
                String sp=mapStatusPulang(g(rs.getString("stts_pulang")));
                if(!sp.isEmpty()){
                    CmbStatusPulang.setSelectedItem(sp);
                    TStatusPulangLain.setEnabled("Lain-lain".equals(sp));
                }
                // Diagnosa masuk: kamar_inap lebih dulu, nanti ditimpa resume kalau terisi
                if(!g(rs.getString("diagnosa_awal")).isEmpty()) TADiagnosaMasuk.setText(rs.getString("diagnosa_awal"));
            }
            rs.close();ps.close();

            // Data klinis dari resume_pasien_ranap (kalau ada)
            ps=koneksi.prepareStatement("select diagnosa_awal,diagnosa_utama,kd_diagnosa_utama,"+
                "diagnosa_sekunder,diagnosa_sekunder2,diagnosa_sekunder3,diagnosa_sekunder4,"+
                "prosedur_utama,kd_prosedur_utama,prosedur_sekunder,prosedur_sekunder2,prosedur_sekunder3,"+
                "IFNULL(dok2.nm_dokter,'') "+
                "from resume_pasien_ranap rp left join dokter dok2 on rp.kd_dokter=dok2.kd_dokter "+
                "where rp.no_rawat=? limit 1");
            ps.setString(1,TNoRw.getText());rs=ps.executeQuery();
            if(rs.next()){
                if(!g(rs.getString(1)).isEmpty()) TADiagnosaMasuk.setText(rs.getString(1));
                TDiagnosaUtama.setText(g(rs.getString(2)));TIcdXUtama.setText(g(rs.getString(3)));
                TDiagTambahan1.setText(g(rs.getString(4)));TDiagTambahan2.setText(g(rs.getString(5)));
                TDiagTambahan3.setText(g(rs.getString(6)));TDiagTambahan4.setText(g(rs.getString(7)));
                TTindakanUtama.setText(g(rs.getString(8)));TIcdIXUtama.setText(g(rs.getString(9)));
                TTindTambahan1.setText(g(rs.getString(10)));TTindTambahan2.setText(g(rs.getString(11)));TTindTambahan3.setText(g(rs.getString(12)));
                // Fallback DPJP dari resume bila dpjp_ranap kosong
                if(KdDokter.getText().trim().isEmpty()){KdDokter.setText("");NmDokter.setText(g(rs.getString(13)));}
            }
            rs.close();ps.close();

            // Penyebab kematian dari pasien_mati (by no_rkm_medis): keterangan + ICD
            ps=koneksi.prepareStatement("select IFNULL(keterangan,''),IFNULL(icd1,''),IFNULL(icd2,''),IFNULL(icd3,''),IFNULL(icd4,'') from pasien_mati where no_rkm_medis=?");
            ps.setString(1,TNoRM.getText());rs=ps.executeQuery();
            if(rs.next()){
                StringBuilder sb=new StringBuilder(rs.getString(1));
                String icd="";
                for(int i=2;i<=5;i++){String c=rs.getString(i);if(c!=null&&!c.trim().isEmpty())icd+=(icd.isEmpty()?"":", ")+c;}
                if(!icd.isEmpty())sb.append(sb.length()>0?" ":"").append("(ICD: ").append(icd).append(")");
                TAPenyebabKematian.setText(sb.toString());
            }
            rs.close();ps.close();
        }catch(Exception e){System.out.println("Notif isiDataPasien : "+e);}
    }

    /** Selisih jam masuk-keluar hasil isiDataPasien(); -1 bila belum diketahui. */
    private int jamRawat=-1;

    private String g(String s){return s==null?"":s;}

    /** Set widget Tanggal dari string "yyyy-MM-dd HH:mm:ss" atau "yyyy-MM-dd". */
    private void setTanggal(widget.Tanggal t,String nilai){
        String v=g(nilai).trim();
        if(v.isEmpty()||v.startsWith("0000")) return;
        try{
            t.setDate(new java.text.SimpleDateFormat(v.length()>10?"yyyy-MM-dd HH:mm:ss":"yyyy-MM-dd").parse(v));
        }catch(Exception e){/* format tak terduga, biarkan nilai lama */}
    }

    /**
     * Petakan enum stts_pulang kamar_inap ke pilihan combo di form ini.
     * Mengembalikan "" bila status belum menentukan pasien pulang — pemanggil
     * membiarkan pilihan combo apa adanya supaya tidak salah tebak.
     */
    private String mapStatusPulang(String stts){
        String s=g(stts).trim();
        // "-", "+", "Pindah Kamar", "Status Belum Lengkap" = pasien belum pulang
        if(s.isEmpty()||s.equals("-")||s.equals("+")
            ||s.equalsIgnoreCase("Pindah Kamar")||s.equalsIgnoreCase("Status Belum Lengkap")) return "";
        if(s.equalsIgnoreCase("Rujuk")) return "Rujuk";
        if(s.equalsIgnoreCase("APS")||s.equalsIgnoreCase("Pulang Paksa")||s.equalsIgnoreCase("Atas Permintaan Sendiri")) return "APS";
        // Meninggal <48 / >48 jam tidak ada di stts_pulang, dihitung dari selisih
        // jam masuk-keluar. Kalau jam keluar belum terisi (jamRawat<0) pilih yang
        // lebih aman (<48 jam) dan biarkan petugas mengoreksi.
        if(s.equalsIgnoreCase("Meninggal")){
            return jamRawat>48 ? "Meninggal > 48 Jam" : "Meninggal < 48 Jam";
        }
        if(s.equalsIgnoreCase("Sehat")||s.equalsIgnoreCase("Sembuh")||s.equalsIgnoreCase("Membaik")
            ||s.equalsIgnoreCase("Atas Persetujuan Dokter")) return "Dipulangkan";
        return "Lain-lain";
    }

    private String mapPembiayaan(String png){
        String p=png.toLowerCase().trim();
        if(p.contains("bpjs")) return "BPJS";
        if(p.contains("perusahaan")) return "Perusahaan";
        if(p.contains("asuransi")) return "Asuransi";
        if(p.isEmpty()||p.equals("-")||p.contains("umum")) return "Umum";
        return "Asuransi";
    }
    public void isCek(){BtnSimpan.setEnabled(akses.getrujukan_keluar());BtnHapus.setEnabled(akses.getrujukan_keluar());BtnEdit.setEnabled(akses.getrujukan_keluar());BtnPrint.setEnabled(akses.getrujukan_keluar());}
}
