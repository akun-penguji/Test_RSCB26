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

public class RMFormulirIntubasiVentilatorRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll, BtnDokter;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan, TglIntubasi, TglPasangVentilator, TglExtubasi, TglLepasVentilator;
    private TextBox TNoRw, TNoRM, TPasien, TCari, TDiagnosa, TIndikasi;
    private TextBox KdDokter, NmDokter, TTekananDarah, TNadi, TRR, TSatO2;
    private TextBox TMode, TFiO2, TPeep, TIERatio;
    private Label LCount;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariDokter dokterDlg = new DlgCariDokter(null, false);

    private widget.Table tbObat;
    private javax.swing.table.DefaultTableModel tabMode;
    private ScrollPane Scroll, ScrollInput;
    private CekBox ChkInput;
    private PanelBiasa FormInput;
    private JPanel PanelInput;
    public widget.editorpane LoadHTML;

    public RMFormulirIntubasiVentilatorRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(240,245,235)),
            "::[ Formulir Tindakan Intubasi & Ventilator ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
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
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","Diagnosa","Dokter","TD","Nadi","RR","SpO2","Mode"}
        ) { @Override public boolean isCellEditable(int r,int c){return false;} };
        tbObat.setModel(tabMode);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override public void mouseClicked(java.awt.event.MouseEvent e){if(tabMode.getRowCount()!=0) getData();}
        });
        // Popup menu cetak
        JPopupMenu pop = new JPopupMenu();
        JMenuItem mnCetak = new JMenuItem("Cetak Formulir Intubasi & Ventilator");
        mnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetak.addActionListener(evt -> BtnPrint.doClick());
        pop.add(mnCetak);
        tbObat.setComponentPopupMenu(pop);
        Scroll.setViewportView(tbObat);

        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(880,580));
        FormInput.setLayout(null);

        int y = 10;
        JLabel h = new JLabel("FORMULIR TINDAKAN INTUBASI & VENTILATOR");
        h.setFont(new java.awt.Font("Tahoma",1,12)); h.setBounds(10,y,400,20); FormInput.add(h);
        JLabel hk = new JLabel("RM.KEP.08.052.2023"); hk.setFont(new java.awt.Font("Tahoma",0,8)); hk.setBounds(700,y,150,20); FormInput.add(hk);

        y+=32;
        Label lt = new Label(); lt.setText("Tanggal :"); lt.setBounds(10,y,80,23); FormInput.add(lt);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TglAsuhan.setBounds(95,y,130,23); FormInput.add(TglAsuhan);

        y+=32;
        Label ld = new Label(); ld.setText("Diagnosa :"); ld.setBounds(10,y,80,23); FormInput.add(ld);
        TDiagnosa = new TextBox(); TDiagnosa.setBounds(95,y,500,23); FormInput.add(TDiagnosa);

        y+=32;
        Label ldk = new Label(); ldk.setText("Nama Dokter :"); ldk.setBounds(10,y,90,23); FormInput.add(ldk);
        KdDokter = new TextBox(); KdDokter.setBounds(105,y,80,23); FormInput.add(KdDokter);
        NmDokter = new TextBox(); NmDokter.setBounds(187,y,200,23); FormInput.add(NmDokter);
        BtnDokter = new Button(); BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); BtnDokter.setBounds(389,y,28,23); FormInput.add(BtnDokter);
        BtnDokter.addActionListener(ev->{dokterDlg.emptTeks();dokterDlg.isCek();dokterDlg.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);dokterDlg.setLocationRelativeTo(internalFrame1);dokterDlg.setVisible(true);});
        dokterDlg.addWindowListener(new java.awt.event.WindowAdapter(){@Override public void windowClosed(java.awt.event.WindowEvent e){if(dokterDlg.getTable().getSelectedRow()!=-1){KdDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(),0).toString());NmDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(),1).toString());}}});

        y+=32;
        Label li = new Label(); li.setText("Indikasi :"); li.setBounds(10,y,80,23); FormInput.add(li);
        TIndikasi = new TextBox(); TIndikasi.setBounds(95,y,500,23); FormInput.add(TIndikasi);

        // Tanggal-tanggal
        y+=38;
        Label l1 = new Label(); l1.setText("Tgl Intubasi :"); l1.setBounds(10,y,90,23); FormInput.add(l1);
        TglIntubasi = new widget.Tanggal(); TglIntubasi.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TglIntubasi.setBounds(105,y,130,23); FormInput.add(TglIntubasi);

        y+=32;
        Label l2 = new Label(); l2.setText("Tgl Pasang Vent :"); l2.setBounds(10,y,110,23); FormInput.add(l2);
        TglPasangVentilator = new widget.Tanggal(); TglPasangVentilator.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TglPasangVentilator.setBounds(125,y,130,23); FormInput.add(TglPasangVentilator);

        y+=32;
        Label l3 = new Label(); l3.setText("Tgl Extubasi :"); l3.setBounds(10,y,90,23); FormInput.add(l3);
        TglExtubasi = new widget.Tanggal(); TglExtubasi.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TglExtubasi.setBounds(105,y,130,23); FormInput.add(TglExtubasi);

        y+=32;
        Label l4 = new Label(); l4.setText("Tgl Lepas Vent :"); l4.setBounds(10,y,100,23); FormInput.add(l4);
        TglLepasVentilator = new widget.Tanggal(); TglLepasVentilator.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TglLepasVentilator.setBounds(115,y,130,23); FormInput.add(TglLepasVentilator);

        // Monitor
        y+=38;
        JLabel lm = new JLabel("Monitor :"); lm.setFont(new java.awt.Font("Tahoma",1,11)); lm.setBounds(10,y,100,20); FormInput.add(lm);
        y+=25;
        Label ltd = new Label(); ltd.setText("Tekanan Darah :"); ltd.setBounds(30,y,100,23); FormInput.add(ltd);
        TTekananDarah = new TextBox(); TTekananDarah.setBounds(135,y,100,23); FormInput.add(TTekananDarah);
        Label ln = new Label(); ln.setText("Nadi :"); ln.setBounds(260,y,40,23); FormInput.add(ln);
        TNadi = new TextBox(); TNadi.setBounds(305,y,80,23); FormInput.add(TNadi);
        y+=28;
        Label lr = new Label(); lr.setText("Respiratory Rate :"); lr.setBounds(30,y,110,23); FormInput.add(lr);
        TRR = new TextBox(); TRR.setBounds(145,y,80,23); FormInput.add(TRR);
        Label ls2 = new Label(); ls2.setText("Saturasi O2 :"); ls2.setBounds(260,y,80,23); FormInput.add(ls2);
        TSatO2 = new TextBox(); TSatO2.setBounds(345,y,80,23); FormInput.add(TSatO2);

        // Setting Ventilator
        y+=38;
        JLabel lsv = new JLabel("Setting Ventilator :"); lsv.setFont(new java.awt.Font("Tahoma",1,11)); lsv.setBounds(10,y,200,20); FormInput.add(lsv);
        y+=25;
        Label lmo = new Label(); lmo.setText("Mode :"); lmo.setBounds(30,y,50,23); FormInput.add(lmo);
        TMode = new TextBox(); TMode.setBounds(85,y,120,23); FormInput.add(TMode);
        Label lf = new Label(); lf.setText("FiO2 :"); lf.setBounds(230,y,40,23); FormInput.add(lf);
        TFiO2 = new TextBox(); TFiO2.setBounds(275,y,80,23); FormInput.add(TFiO2);
        y+=28;
        Label lp = new Label(); lp.setText("Peep :"); lp.setBounds(30,y,50,23); FormInput.add(lp);
        TPeep = new TextBox(); TPeep.setBounds(85,y,80,23); FormInput.add(TPeep);
        Label lie = new Label(); lie.setText("i : E :"); lie.setBounds(230,y,40,23); FormInput.add(lie);
        TIERatio = new TextBox(); TIERatio.setBounds(275,y,80,23); FormInput.add(TIERatio);

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
            String tglS=Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19);
            String tglI=Valid.SetTgl(TglIntubasi.getSelectedItem()+"")+" "+TglIntubasi.getSelectedItem().toString().substring(11,19);
            String tglPV=Valid.SetTgl(TglPasangVentilator.getSelectedItem()+"")+" "+TglPasangVentilator.getSelectedItem().toString().substring(11,19);
            String tglE=Valid.SetTgl(TglExtubasi.getSelectedItem()+"")+" "+TglExtubasi.getSelectedItem().toString().substring(11,19);
            String tglLV=Valid.SetTgl(TglLepasVentilator.getSelectedItem()+"")+" "+TglLepasVentilator.getSelectedItem().toString().substring(11,19);
            if(Sequel.menyimpantf("formulir_intubasi_ventilator_rscb","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?","Data",18,new String[]{
                TNoRw.getText(),tglS,TDiagnosa.getText(),KdDokter.getText(),TIndikasi.getText(),
                tglI,tglPV,tglE,tglLV,
                TTekananDarah.getText(),TNadi.getText(),TRR.getText(),TSatO2.getText(),
                TMode.getText(),TFiO2.getText(),TPeep.getText(),TIERatio.getText(),KdDokter.getText()
            })){tampil();emptTeks();}
        });

        BtnHapus.addActionListener(ev->{
            if(tbObat.getSelectedRow()!=-1){if(Sequel.queryu2tf("delete from formulir_intubasi_ventilator_rscb where no_rawat=? and tanggal=?",2,new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()
            })){tampil();emptTeks();}}else JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");
        });

        BtnEdit.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")||tbObat.getSelectedRow()==-1){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            if(Sequel.queryu2tf("delete from formulir_intubasi_ventilator_rscb where no_rawat=? and tanggal=?",2,new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()
            })){BtnSimpan.doClick();}
        });

        BtnPrint.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            Map<String,Object> p=new HashMap<>();p.put("namars",akses.getnamars());p.put("alamatrs",akses.getalamatrs());
            p.put("propinsirs",akses.getpropinsirs());p.put("kabupatenrs",akses.getkabupatenrs());
            p.put("kontakrs",akses.getkontakrs());p.put("emailrs",akses.getemailrs());
            p.put("logo",Sequel.cariGambar("select setting.logo from setting"));
            Valid.MyReportqry("rptFormulirIntubasiVentilatorRscb.jasper","report","::[ Formulir Intubasi & Ventilator ]::",
                "select f.*,r.no_rkm_medis,p.nm_pasien,p.jk,concat(r.umurdaftar,' ',r.sttsumur) as umur,d.nm_dokter "+
                "from formulir_intubasi_ventilator_rscb f inner join reg_periksa r on f.no_rawat=r.no_rawat "+
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis left join dokter d on f.kd_dokter=d.kd_dokter "+
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
        try{ps=koneksi.prepareStatement("select f.no_rawat,r.no_rkm_medis,p.nm_pasien,f.tanggal,f.diagnosa,d.nm_dokter,f.tekanan_darah,f.nadi,f.respiratory_rate,f.saturasi_o2,f.mode_ventilator "+
            "from formulir_intubasi_ventilator_rscb f inner join reg_periksa r on f.no_rawat=r.no_rawat inner join pasien p on r.no_rkm_medis=p.no_rkm_medis left join dokter d on f.kd_dokter=d.kd_dokter "+
            "where f.tanggal between ? and ? and (f.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) order by f.tanggal desc");
        ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
        ps.setString(3,"%"+TCari.getText().trim()+"%");ps.setString(4,"%"+TCari.getText().trim()+"%");ps.setString(5,"%"+TCari.getText().trim()+"%");
        rs=ps.executeQuery();while(rs.next()){tabMode.addRow(new String[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11)});}
        LCount.setText("Record : "+tabMode.getRowCount());}catch(Exception e){System.out.println("Notif : "+e);}finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
    }

    public void getData(){
        if(tbObat.getSelectedRow()!=-1){
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            try{TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()));}catch(Exception e){}
            try{ps=koneksi.prepareStatement("select f.*,d.nm_dokter from formulir_intubasi_ventilator_rscb f left join dokter d on f.kd_dokter=d.kd_dokter where f.no_rawat=? and f.tanggal=?");
            ps.setString(1,tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());ps.setString(2,tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            rs=ps.executeQuery();if(rs.next()){
                TDiagnosa.setText(rs.getString("diagnosa"));KdDokter.setText(rs.getString("kd_dokter"));NmDokter.setText(rs.getString("nm_dokter"));
                TIndikasi.setText(rs.getString("indikasi_pemasangan"));
                try{if(rs.getString("tgl_intubasi")!=null)TglIntubasi.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("tgl_intubasi")));}catch(Exception ex){}
                try{if(rs.getString("tgl_pasang_ventilator")!=null)TglPasangVentilator.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("tgl_pasang_ventilator")));}catch(Exception ex){}
                try{if(rs.getString("tgl_extubasi")!=null)TglExtubasi.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("tgl_extubasi")));}catch(Exception ex){}
                try{if(rs.getString("tgl_lepas_ventilator")!=null)TglLepasVentilator.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("tgl_lepas_ventilator")));}catch(Exception ex){}
                TTekananDarah.setText(rs.getString("tekanan_darah"));TNadi.setText(rs.getString("nadi"));TRR.setText(rs.getString("respiratory_rate"));TSatO2.setText(rs.getString("saturasi_o2"));
                TMode.setText(rs.getString("mode_ventilator"));TFiO2.setText(rs.getString("fio2"));TPeep.setText(rs.getString("peep"));TIERatio.setText(rs.getString("ie_ratio"));
            }}catch(Exception e){System.out.println("Notif getData : "+e);}finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
        }
    }

    public void emptTeks(){TglAsuhan.setDate(new Date());TglIntubasi.setDate(new Date());TglPasangVentilator.setDate(new Date());TglExtubasi.setDate(new Date());TglLepasVentilator.setDate(new Date());
        TDiagnosa.setText("");KdDokter.setText("");NmDokter.setText("");TIndikasi.setText("");TTekananDarah.setText("");TNadi.setText("");TRR.setText("");TSatO2.setText("");TMode.setText("");TFiO2.setText("");TPeep.setText("");TIERatio.setText("");}
    public void setNoRm(String n,String m,String p){TNoRw.setText(n);TNoRM.setText(m);TPasien.setText(p);}
    public void setNoRm(String n,Date t){TNoRw.setText(n);TglAsuhan.setDate(t);TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?",n));TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?",TNoRM.getText()));}
    public void isCek(){BtnSimpan.setEnabled(akses.getrujukan_keluar());BtnHapus.setEnabled(akses.getrujukan_keluar());BtnEdit.setEnabled(akses.getrujukan_keluar());BtnPrint.setEnabled(akses.getrujukan_keluar());}
}
