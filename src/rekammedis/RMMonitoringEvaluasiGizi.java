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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import widget.*;

public class RMMonitoringEvaluasiGizi extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll, BtnPetugas;
    private widget.Tanggal DTPCari1, DTPCari2, TglMonitoring;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private TextBox TDiet, NIP, NamaPetugas;
    private widget.TextArea TCatatanPerkembangan, TRencanaPenatalaksanaan;
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

    public RMMonitoringEvaluasiGizi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initTables();
        initComponents();
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initTables() {
        try {
            koneksi.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS monitoring_evaluasi_gizi_rscb (" +
                "no_rawat varchar(20) NOT NULL," +
                "tanggal varchar(30) NOT NULL," +
                "diet varchar(200) NULL," +
                "catatan_perkembangan text NULL," +
                "rencana_penatalaksanaan text NULL," +
                "nip varchar(20) NULL," +
                "PRIMARY KEY (no_rawat, tanggal(30))" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
        } catch (Exception e) { System.out.println("Init tables monitoring evaluasi gizi: " + e); }
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(240,245,235)),
            "::[ Monitoring dan Evaluasi Asuhan Gizi ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
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
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","Diet","Petugas"}
        ) { @Override public boolean isCellEditable(int r,int c){return false;} };
        tbObat.setModel(tabMode);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override public void mouseClicked(java.awt.event.MouseEvent e){if(tabMode.getRowCount()!=0) getData();}
        });
        JPopupMenu pop = new JPopupMenu();
        JMenuItem mnCetak = new JMenuItem("Cetak Monitoring dan Evaluasi Asuhan Gizi");
        mnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetak.addActionListener(evt -> BtnPrint.doClick());
        pop.add(mnCetak);
        tbObat.setComponentPopupMenu(pop);
        Scroll.setViewportView(tbObat);

        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(880, 280));
        FormInput.setLayout(null);

        int y = 10;
        JLabel h = new JLabel("MONITORING DAN EVALUASI ASUHAN GIZI");
        h.setFont(new java.awt.Font("Tahoma",1,12)); h.setBounds(10,y,420,20); FormInput.add(h);
        JLabel hk = new JLabel("RM.GZ.09.006.2019"); hk.setFont(new java.awt.Font("Tahoma",0,8)); hk.setBounds(700,y,150,20); FormInput.add(hk);

        y+=30;
        Label lt = new Label(); lt.setText("Tanggal :"); lt.setBounds(10,y,65,23); FormInput.add(lt);
        TglMonitoring = new widget.Tanggal(); TglMonitoring.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TglMonitoring.setBounds(80,y,160,23); FormInput.add(TglMonitoring);

        y+=30;
        Label ld = new Label(); ld.setText("Diet :"); ld.setBounds(10,y,50,23); FormInput.add(ld);
        TDiet = new TextBox(); TDiet.setBounds(65,y,600,23); FormInput.add(TDiet);

        y+=30;
        Label lcp = new Label(); lcp.setText("Catatan Perkembangan :"); lcp.setBounds(10,y,150,23); FormInput.add(lcp);
        TCatatanPerkembangan = new widget.TextArea();
        TCatatanPerkembangan.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        TCatatanPerkembangan.setBounds(165,y,500,60); FormInput.add(TCatatanPerkembangan);

        y+=67;
        Label lrp = new Label(); lrp.setText("Rencana Penatalaksanaan :"); lrp.setBounds(10,y,170,23); FormInput.add(lrp);
        TRencanaPenatalaksanaan = new widget.TextArea();
        TRencanaPenatalaksanaan.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        TRencanaPenatalaksanaan.setBounds(185,y,480,60); FormInput.add(TRencanaPenatalaksanaan);

        y+=67;
        Label lp = new Label(); lp.setText("Nutrisionis :"); lp.setBounds(10,y,80,23); FormInput.add(lp);
        Label lpk = new Label(); lpk.setText("Kode Petugas :"); lpk.setBounds(95,y,95,23); FormInput.add(lpk);
        NIP = new TextBox(); NIP.setBounds(193,y,80,23); FormInput.add(NIP);
        Label lpn = new Label(); lpn.setText("Nama Petugas :"); lpn.setBounds(280,y,95,23); FormInput.add(lpn);
        NamaPetugas = new TextBox(); NamaPetugas.setBounds(378,y,200,23); FormInput.add(NamaPetugas);
        BtnPetugas = new Button(); BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPetugas.setBounds(580,y,28,23); FormInput.add(BtnPetugas);
        BtnPetugas.addActionListener(ev->{
            petugasDlg.emptTeks(); petugasDlg.isCek();
            petugasDlg.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20);
            petugasDlg.setLocationRelativeTo(internalFrame1); petugasDlg.setVisible(true);
        });
        petugasDlg.addWindowListener(new java.awt.event.WindowAdapter(){
            @Override public void windowClosed(java.awt.event.WindowEvent e){
                if(petugasDlg.getTable().getSelectedRow()!=-1){
                    NIP.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(),0).toString());
                    NamaPetugas.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(),1).toString());
                }
            }
        });

        // Toggle
        ChkInput = new CekBox(); ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setText(".: Input Data"); ChkInput.setSelected(true); ChkInput.setBorderPainted(true); ChkInput.setBorderPaintedFlat(true); ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(SwingConstants.LEFT); ChkInput.setPreferredSize(new Dimension(192,20));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.addActionListener(ev->isForm());

        ScrollInput = new ScrollPane(); ScrollInput.setViewportView(FormInput); ScrollInput.setPreferredSize(new Dimension(880,300));
        PanelInput = new JPanel(new BorderLayout()); PanelInput.setOpaque(false); PanelInput.setPreferredSize(new Dimension(WIDTH,320));
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
        TCari.addKeyListener(new java.awt.event.KeyAdapter(){
            @Override public void keyPressed(java.awt.event.KeyEvent e){if(e.getKeyCode()==java.awt.event.KeyEvent.VK_ENTER)tampil();}
        });

        BtnSimpan.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")){Valid.textKosong(TNoRw,"Pasien");return;}
            if(NIP.getText().trim().equals("")){Valid.textKosong(NIP,"Nutrisionis");return;}
            String tglS=Valid.SetTgl(TglMonitoring.getSelectedItem()+"")+" "+TglMonitoring.getSelectedItem().toString().substring(11,19);
            if(Sequel.menyimpantf("monitoring_evaluasi_gizi_rscb",
                "?,?,?,?,?,?","Data",6,new String[]{
                TNoRw.getText(),tglS,
                TDiet.getText(),
                TCatatanPerkembangan.getText(),
                TRencanaPenatalaksanaan.getText(),
                NIP.getText()
            })){tampil();emptTeks();}
        });

        BtnHapus.addActionListener(ev->{
            if(tbObat.getSelectedRow()!=-1){if(Sequel.queryu2tf("delete from monitoring_evaluasi_gizi_rscb where no_rawat=? and tanggal=?",2,new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()
            })){tampil();emptTeks();}}else JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");
        });

        BtnEdit.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")||tbObat.getSelectedRow()==-1){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            if(Sequel.queryu2tf("delete from monitoring_evaluasi_gizi_rscb where no_rawat=? and tanggal=?",2,new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()
            })){BtnSimpan.doClick();}
        });

        BtnPrint.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")){JOptionPane.showMessageDialog(null,"Pilih pasien dulu...!!!!");return;}
            Map<String,Object> p=new HashMap<>();
            p.put("namars",akses.getnamars());p.put("alamatrs",akses.getalamatrs());
            p.put("propinsirs",akses.getpropinsirs());p.put("kabupatenrs",akses.getkabupatenrs());
            p.put("kontakrs",akses.getkontakrs());p.put("emailrs",akses.getemailrs());
            p.put("logo",Sequel.cariGambar("select setting.logo from setting"));
            p.put("no_rm",TNoRM.getText());
            p.put("nm_pasien",TPasien.getText());
            p.put("tgl_lahir",Sequel.cariIsi("select DATE_FORMAT(tgl_lahir,'%d-%m-%Y') from pasien where no_rkm_medis=?",TNoRM.getText()));
            p.put("jk",Sequel.cariIsi("select jk from pasien where no_rkm_medis=?",TNoRM.getText()));
            p.put("diagnosa",Sequel.cariIsi("select diagnosa_awal from kamar_inap where no_rawat=? order by tgl_masuk desc limit 1",TNoRw.getText()));
            Valid.MyReportqry("rptMonitoringEvaluasiGizi.jasper","report","::[ Monitoring dan Evaluasi Asuhan Gizi ]::",
                "select meg.tanggal, IFNULL(meg.diet,'') as diet, " +
                "IFNULL(meg.catatan_perkembangan,'') as catatan_perkembangan, " +
                "IFNULL(meg.rencana_penatalaksanaan,'') as rencana_penatalaksanaan, " +
                "IFNULL(pt.nama,'') as nm_petugas " +
                "from monitoring_evaluasi_gizi_rscb meg " +
                "left join petugas pt on meg.nip=pt.nip " +
                "where meg.no_rawat='"+TNoRw.getText()+"' " +
                "order by meg.tanggal",p);
        });

        InternalFrame ifd=new InternalFrame();ifd.setLayout(new BorderLayout());ifd.setBackground(new Color(255,255,255));
        ifd.add(PanelInput,BorderLayout.NORTH);ifd.add(Scroll,BorderLayout.CENTER);
        internalFrame1.add(panelAtas,BorderLayout.PAGE_START);internalFrame1.add(ifd,BorderLayout.CENTER);internalFrame1.add(panelBawah,BorderLayout.PAGE_END);
        setLayout(new BorderLayout());add(internalFrame1,BorderLayout.CENTER);setDefaultCloseOperation(DISPOSE_ON_CLOSE);setUndecorated(true);
    }

    private void isForm(){
        if(ChkInput.isSelected()){ChkInput.setVisible(false);PanelInput.setPreferredSize(new Dimension(WIDTH,320));FormInput.setVisible(true);ChkInput.setVisible(true);}
        else{ChkInput.setVisible(false);PanelInput.setPreferredSize(new Dimension(WIDTH,20));FormInput.setVisible(false);ChkInput.setVisible(true);}
    }

    public void tampil(){
        Valid.tabelKosong(tabMode);
        try{ps=koneksi.prepareStatement(
            "select meg.no_rawat,r.no_rkm_medis,p.nm_pasien,meg.tanggal,IFNULL(meg.diet,''),IFNULL(pt.nama,'') " +
            "from monitoring_evaluasi_gizi_rscb meg " +
            "inner join reg_periksa r on meg.no_rawat=r.no_rawat " +
            "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
            "left join petugas pt on meg.nip=pt.nip " +
            "where meg.tanggal between ? and ? " +
            "and (meg.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) " +
            "order by meg.tanggal desc");
        ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
        ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
        ps.setString(3,"%"+TCari.getText().trim()+"%");
        ps.setString(4,"%"+TCari.getText().trim()+"%");
        ps.setString(5,"%"+TCari.getText().trim()+"%");
        rs=ps.executeQuery();
        while(rs.next()){tabMode.addRow(new String[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6)});}
        LCount.setText("Record : "+tabMode.getRowCount());
        }catch(Exception e){System.out.println("Notif : "+e);}
        finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
    }

    public void getData(){
        if(tbObat.getSelectedRow()!=-1){
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            try{TglMonitoring.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()));}catch(Exception e){}
            try{ps=koneksi.prepareStatement("select meg.*,pt.nama as nm_petugas from monitoring_evaluasi_gizi_rscb meg left join petugas pt on meg.nip=pt.nip where meg.no_rawat=? and meg.tanggal=?");
            ps.setString(1,tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
            ps.setString(2,tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            rs=ps.executeQuery();
            if(rs.next()){
                TDiet.setText(rs.getString("diet")==null?"":rs.getString("diet"));
                TCatatanPerkembangan.setText(rs.getString("catatan_perkembangan")==null?"":rs.getString("catatan_perkembangan"));
                TRencanaPenatalaksanaan.setText(rs.getString("rencana_penatalaksanaan")==null?"":rs.getString("rencana_penatalaksanaan"));
                NIP.setText(rs.getString("nip")==null?"":rs.getString("nip"));
                NamaPetugas.setText(rs.getString("nm_petugas")==null?"":rs.getString("nm_petugas"));
            }}catch(Exception e){System.out.println("Notif getData: "+e);}
            finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
        }
    }

    public void emptTeks(){
        TglMonitoring.setDate(new Date());
        TDiet.setText(""); TCatatanPerkembangan.setText(""); TRencanaPenatalaksanaan.setText("");
        NIP.setText(""); NamaPetugas.setText("");
    }

    public void setNoRm(String n,String m,String p){TNoRw.setText(n);TNoRM.setText(m);TPasien.setText(p);}
    public void setNoRm(String n,Date t){
        TNoRw.setText(n);TglMonitoring.setDate(t);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?",n));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?",TNoRM.getText()));
    }
    public void isCek(){BtnSimpan.setEnabled(akses.getrujukan_keluar());BtnHapus.setEnabled(akses.getrujukan_keluar());BtnEdit.setEnabled(akses.getrujukan_keluar());BtnPrint.setEnabled(akses.getrujukan_keluar());}
}
