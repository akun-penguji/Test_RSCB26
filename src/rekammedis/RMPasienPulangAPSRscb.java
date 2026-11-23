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

public class RMPasienPulangAPSRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll, BtnDokter, BtnPetugas;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private TextBox KdDokter, NmDokter, NipPetugas, NmPetugas;
    private TextBox TPemberiInformasi, TPenerimaInformasi, TNamaPJ, TUmurPJ, TAlamatPJ;
    private TextBox TNamaPasienPJ, TUmurPasienPJ, TAlamatPasienPJ;
    private TextBox TRuangan, TNamaSaksi;
    private TextArea TAAlasan;
    private ComboBox CmbJKPJ, CmbHubungan, CmbJKPasienPJ;
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

    public RMPasienPulangAPSRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(240,245,235)),
            "::[ Formulir Pasien Pulang Atas Permintaan Sendiri ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
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
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","Nama PJ","Alasan","Dokter","Petugas"}
        ) { @Override public boolean isCellEditable(int r,int c){return false;} };
        tbObat.setModel(tabMode);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override public void mouseClicked(java.awt.event.MouseEvent e){if(tabMode.getRowCount()!=0) getData();}
        });
        JPopupMenu pop = new JPopupMenu();
        JMenuItem mnCetak = new JMenuItem("Cetak Pasien Pulang APS");
        mnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetak.addActionListener(evt -> BtnPrint.doClick());
        pop.add(mnCetak);
        tbObat.setComponentPopupMenu(pop);
        Scroll.setViewportView(tbObat);

        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(880,620));
        FormInput.setLayout(null);

        int y = 10;
        JLabel h = new JLabel("FORMULIR PASIEN PULANG ATAS PERMINTAAN SENDIRI");
        h.setFont(new java.awt.Font("Tahoma",1,12)); h.setBounds(10,y,500,20); FormInput.add(h);
        JLabel hk = new JLabel("RM.KEP.08.005.2019"); hk.setFont(new java.awt.Font("Tahoma",0,8)); hk.setBounds(700,y,150,20); FormInput.add(hk);

        y+=32;
        Label lt = new Label(); lt.setText("Tanggal :"); lt.setBounds(10,y,80,23); FormInput.add(lt);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TglAsuhan.setBounds(95,y,130,23); FormInput.add(TglAsuhan);

        y+=32;
        Label ldk = new Label(); ldk.setText("DPJP :"); ldk.setBounds(10,y,80,23); FormInput.add(ldk);
        KdDokter = new TextBox(); KdDokter.setBounds(95,y,80,23); FormInput.add(KdDokter);
        NmDokter = new TextBox(); NmDokter.setBounds(177,y,200,23); FormInput.add(NmDokter);
        BtnDokter = new Button(); BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); BtnDokter.setBounds(379,y,28,23); FormInput.add(BtnDokter);
        BtnDokter.addActionListener(ev->{dokterDlg.emptTeks();dokterDlg.isCek();dokterDlg.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);dokterDlg.setLocationRelativeTo(internalFrame1);dokterDlg.setVisible(true);});
        dokterDlg.addWindowListener(new java.awt.event.WindowAdapter(){@Override public void windowClosed(java.awt.event.WindowEvent e){if(dokterDlg.getTable().getSelectedRow()!=-1){KdDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(),0).toString());NmDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(),1).toString());}}});

        y+=32;
        Label lpi = new Label(); lpi.setText("Pemberi Info :"); lpi.setBounds(10,y,90,23); FormInput.add(lpi);
        TPemberiInformasi = new TextBox(); TPemberiInformasi.setBounds(105,y,300,23); FormInput.add(TPemberiInformasi);

        y+=32;
        Label lpni = new Label(); lpni.setText("Penerima Info :"); lpni.setBounds(10,y,95,23); FormInput.add(lpni);
        TPenerimaInformasi = new TextBox(); TPenerimaInformasi.setBounds(110,y,300,23); FormInput.add(TPenerimaInformasi);

        y+=38;
        JLabel hpj = new JLabel("Data Penanggung Jawab :"); hpj.setFont(new java.awt.Font("Tahoma",1,11)); hpj.setBounds(10,y,250,20); FormInput.add(hpj);

        y+=28;
        Label lnpj = new Label(); lnpj.setText("Nama PJ :"); lnpj.setBounds(10,y,70,23); FormInput.add(lnpj);
        TNamaPJ = new TextBox(); TNamaPJ.setBounds(85,y,250,23); FormInput.add(TNamaPJ);
        Label lupj = new Label(); lupj.setText("Umur :"); lupj.setBounds(350,y,45,23); FormInput.add(lupj);
        TUmurPJ = new TextBox(); TUmurPJ.setBounds(400,y,60,23); FormInput.add(TUmurPJ);
        Label ljpj = new Label(); ljpj.setText("JK :"); ljpj.setBounds(475,y,25,23); FormInput.add(ljpj);
        CmbJKPJ = new ComboBox(); CmbJKPJ.setModel(new DefaultComboBoxModel(new String[]{"Laki-Laki","Perempuan"}));
        CmbJKPJ.setBounds(505,y,120,23); FormInput.add(CmbJKPJ);

        y+=28;
        Label lapj = new Label(); lapj.setText("Alamat PJ :"); lapj.setBounds(10,y,75,23); FormInput.add(lapj);
        TAlamatPJ = new TextBox(); TAlamatPJ.setBounds(90,y,535,23); FormInput.add(TAlamatPJ);

        y+=28;
        Label lhub = new Label(); lhub.setText("Hubungan :"); lhub.setBounds(10,y,75,23); FormInput.add(lhub);
        CmbHubungan = new ComboBox(); CmbHubungan.setModel(new DefaultComboBoxModel(new String[]{"Diri Sendiri","Suami","Istri","Ayah","Ibu","Anak","Saudara","Lainnya"}));
        CmbHubungan.setBounds(90,y,150,23); FormInput.add(CmbHubungan);

        y+=38;
        JLabel hps = new JLabel("Data Pasien yang Diwakili :"); hps.setFont(new java.awt.Font("Tahoma",1,11)); hps.setBounds(10,y,250,20); FormInput.add(hps);

        y+=28;
        Label lnps = new Label(); lnps.setText("Nama Pasien :"); lnps.setBounds(10,y,90,23); FormInput.add(lnps);
        TNamaPasienPJ = new TextBox(); TNamaPasienPJ.setBounds(105,y,230,23); FormInput.add(TNamaPasienPJ);
        Label lups = new Label(); lups.setText("Umur :"); lups.setBounds(350,y,45,23); FormInput.add(lups);
        TUmurPasienPJ = new TextBox(); TUmurPasienPJ.setBounds(400,y,60,23); FormInput.add(TUmurPasienPJ);
        Label ljps = new Label(); ljps.setText("JK :"); ljps.setBounds(475,y,25,23); FormInput.add(ljps);
        CmbJKPasienPJ = new ComboBox(); CmbJKPasienPJ.setModel(new DefaultComboBoxModel(new String[]{"Laki-Laki","Perempuan"}));
        CmbJKPasienPJ.setBounds(505,y,120,23); FormInput.add(CmbJKPasienPJ);

        y+=28;
        Label laps = new Label(); laps.setText("Alamat Pasien :"); laps.setBounds(10,y,95,23); FormInput.add(laps);
        TAlamatPasienPJ = new TextBox(); TAlamatPasienPJ.setBounds(110,y,515,23); FormInput.add(TAlamatPasienPJ);

        y+=38;
        Label lal = new Label(); lal.setText("Alasan :"); lal.setBounds(10,y,60,23); FormInput.add(lal);
        TAAlasan = new TextArea(); TAAlasan.setBounds(75,y,580,50); FormInput.add(TAAlasan);

        y+=58;
        Label lru = new Label(); lru.setText("Ruangan :"); lru.setBounds(10,y,70,23); FormInput.add(lru);
        TRuangan = new TextBox(); TRuangan.setBounds(85,y,200,23); FormInput.add(TRuangan);

        y+=32;
        Label lns = new Label(); lns.setText("Nama Saksi :"); lns.setBounds(10,y,85,23); FormInput.add(lns);
        TNamaSaksi = new TextBox(); TNamaSaksi.setBounds(100,y,250,23); FormInput.add(TNamaSaksi);

        y+=32;
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
            if(Sequel.menyimpantf("pasien_pulang_aps_rscb","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?","Data",18,new String[]{
                TNoRw.getText(),tglS,
                KdDokter.getText(),TPemberiInformasi.getText(),TPenerimaInformasi.getText(),
                TNamaPJ.getText(),TUmurPJ.getText(),CmbJKPJ.getSelectedItem().toString(),TAlamatPJ.getText(),
                CmbHubungan.getSelectedItem().toString(),
                TNamaPasienPJ.getText(),TUmurPasienPJ.getText(),CmbJKPasienPJ.getSelectedItem().toString(),TAlamatPasienPJ.getText(),
                TAAlasan.getText(),TRuangan.getText(),TNamaSaksi.getText(),
                NipPetugas.getText()
            })){tampil();emptTeks();}
        });

        BtnHapus.addActionListener(ev->{
            if(tbObat.getSelectedRow()!=-1){if(Sequel.queryu2tf("delete from pasien_pulang_aps_rscb where no_rawat=? and tanggal=?",2,new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()
            })){tampil();emptTeks();}}else JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");
        });

        BtnEdit.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")||tbObat.getSelectedRow()==-1){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            if(Sequel.queryu2tf("delete from pasien_pulang_aps_rscb where no_rawat=? and tanggal=?",2,new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()
            })){BtnSimpan.doClick();}
        });

        BtnPrint.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            Map<String,Object> p=new HashMap<>();p.put("namars",akses.getnamars());p.put("alamatrs",akses.getalamatrs());
            p.put("propinsirs",akses.getpropinsirs());p.put("kabupatenrs",akses.getkabupatenrs());
            p.put("kontakrs",akses.getkontakrs());p.put("emailrs",akses.getemailrs());
            p.put("logo",Sequel.cariGambar("select setting.logo from setting"));
            Valid.MyReportqry("rptPasienPulangAPSRscb.jasper","report","::[ Formulir Pasien Pulang APS ]::",
                "select f.*,r.no_rkm_medis,p.nm_pasien,p.jk,concat(r.umurdaftar,' ',r.sttsumur) as umur,d.nm_dokter,pt.nama as nm_petugas "+
                "from pasien_pulang_aps_rscb f inner join reg_periksa r on f.no_rawat=r.no_rawat "+
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
        try{ps=koneksi.prepareStatement("select f.no_rawat,r.no_rkm_medis,p.nm_pasien,f.tanggal,f.nama_pj,f.alasan,d.nm_dokter,pt.nama as nm_petugas "+
            "from pasien_pulang_aps_rscb f inner join reg_periksa r on f.no_rawat=r.no_rawat inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "+
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
            try{ps=koneksi.prepareStatement("select f.*,d.nm_dokter,pt.nama as nm_petugas from pasien_pulang_aps_rscb f left join dokter d on f.kd_dokter=d.kd_dokter left join petugas pt on f.nip=pt.nip where f.no_rawat=? and f.tanggal=?");
            ps.setString(1,tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());ps.setString(2,tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            rs=ps.executeQuery();if(rs.next()){
                KdDokter.setText(rs.getString("kd_dokter"));NmDokter.setText(rs.getString("nm_dokter"));
                TPemberiInformasi.setText(rs.getString("pemberi_informasi"));TPenerimaInformasi.setText(rs.getString("penerima_informasi"));
                TNamaPJ.setText(rs.getString("nama_pj"));TUmurPJ.setText(rs.getString("umur_pj"));CmbJKPJ.setSelectedItem(rs.getString("jk_pj"));TAlamatPJ.setText(rs.getString("alamat_pj"));
                CmbHubungan.setSelectedItem(rs.getString("hubungan"));
                TNamaPasienPJ.setText(rs.getString("nama_pasien_pj"));TUmurPasienPJ.setText(rs.getString("umur_pasien_pj"));CmbJKPasienPJ.setSelectedItem(rs.getString("jk_pasien_pj"));TAlamatPasienPJ.setText(rs.getString("alamat_pasien_pj"));
                TAAlasan.setText(rs.getString("alasan"));TRuangan.setText(rs.getString("ruangan"));TNamaSaksi.setText(rs.getString("nama_saksi"));
                NipPetugas.setText(rs.getString("nip"));NmPetugas.setText(rs.getString("nm_petugas"));
            }}catch(Exception e){System.out.println("Notif getData : "+e);}finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
        }
    }

    public void emptTeks(){TglAsuhan.setDate(new Date());
        KdDokter.setText("");NmDokter.setText("");TPemberiInformasi.setText("");TPenerimaInformasi.setText("");
        TNamaPJ.setText("");TUmurPJ.setText("");CmbJKPJ.setSelectedIndex(0);TAlamatPJ.setText("");CmbHubungan.setSelectedIndex(0);
        TNamaPasienPJ.setText("");TUmurPasienPJ.setText("");CmbJKPasienPJ.setSelectedIndex(0);TAlamatPasienPJ.setText("");
        TAAlasan.setText("");TRuangan.setText("");TNamaSaksi.setText("");NipPetugas.setText("");NmPetugas.setText("");}
    public void setNoRm(String n,String m,String p){TNoRw.setText(n);TNoRM.setText(m);TPasien.setText(p);}
    public void setNoRm(String n,Date t){TNoRw.setText(n);TglAsuhan.setDate(t);TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?",n));TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?",TNoRM.getText()));}
    public void isCek(){BtnSimpan.setEnabled(akses.getrujukan_keluar());BtnHapus.setEnabled(akses.getrujukan_keluar());BtnEdit.setEnabled(akses.getrujukan_keluar());BtnPrint.setEnabled(akses.getrujukan_keluar());}
}
