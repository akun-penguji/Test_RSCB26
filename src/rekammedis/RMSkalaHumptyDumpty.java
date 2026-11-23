package rekammedis;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import kepegawaian.DlgCariPetugas;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import widget.*;

public class RMSkalaHumptyDumpty extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll, BtnPetugas;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan;
    private TextBox TNoRw, TNoRM, TPasien, TCari, KdPetugas, NmPetugas;
    private Label LCount, LTotalNilai, LLevelResiko;
    private ComboBox CmbSkoring;

    private JRadioButton RdUmur4, RdUmur3, RdUmur2, RdUmur1;
    private JRadioButton RdJKL, RdJKP;
    private JRadioButton RdDiag4, RdDiag3, RdDiag2, RdDiag1;
    private JRadioButton RdKog3, RdKog2, RdKog1;
    private JRadioButton RdLing4, RdLing3, RdLing2, RdLing1;
    private JRadioButton RdResp3, RdResp2, RdResp1;
    private JRadioButton RdObat3, RdObat2, RdObat1;

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
    private JPopupMenu jPopupMenu1;
    private JMenuItem MnCetak;

    public RMSkalaHumptyDumpty(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private int getVal(JRadioButton... rds) {
        // helper - not used, values extracted in save
        return 0;
    }

    private void hitungTotal() {
        int u = RdUmur4.isSelected()?4:(RdUmur3.isSelected()?3:(RdUmur2.isSelected()?2:1));
        int j = RdJKL.isSelected()?2:1;
        int d = RdDiag4.isSelected()?4:(RdDiag3.isSelected()?3:(RdDiag2.isSelected()?2:1));
        int k = RdKog3.isSelected()?3:(RdKog2.isSelected()?2:1);
        int l = RdLing4.isSelected()?4:(RdLing3.isSelected()?3:(RdLing2.isSelected()?2:1));
        int r = RdResp3.isSelected()?3:(RdResp2.isSelected()?2:1);
        int o = RdObat3.isSelected()?3:(RdObat2.isSelected()?2:1);
        int total = u+j+d+k+l+r+o;
        LTotalNilai.setText("Total Nilai : " + total);
        if (total <= 11) LLevelResiko.setText("Tidak beresiko (7-11)");
        else if (total <= 15) LLevelResiko.setText("Resiko rendah (12-15)");
        else LLevelResiko.setText("Risiko tinggi (>=16)");
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(240,245,235)),
            "::[ Skala Humpty Dumpty ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
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
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","Skoring","Umur","JK","Diagnosa","Kognitif","Lingkungan","Respon","Obat","Total","Level","Petugas"}
        ) { @Override public boolean isCellEditable(int r,int c){return false;} };
        tbObat.setModel(tabMode);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override public void mouseClicked(java.awt.event.MouseEvent e){if(tabMode.getRowCount()!=0) getData();}
        });

        jPopupMenu1 = new JPopupMenu();
        MnCetak = new JMenuItem();
        MnCetak.setBackground(new Color(255,255,254));
        MnCetak.setFont(new java.awt.Font("Tahoma",0,11));
        MnCetak.setForeground(new Color(50,50,50));
        MnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        MnCetak.setText("Cetak");
        MnCetak.setPreferredSize(new Dimension(220,26));
        MnCetak.addActionListener(ev -> cetakPerBaris());
        jPopupMenu1.add(MnCetak);
        tbObat.setComponentPopupMenu(jPopupMenu1);

        Scroll.setViewportView(tbObat);

        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(880,650));
        FormInput.setLayout(null);

        java.awt.event.ActionListener rc = e -> hitungTotal();
        int y = 10;

        JLabel h = new JLabel("SKALA HUMPTY DUMPTY"); h.setFont(new java.awt.Font("Tahoma",1,13)); h.setBounds(10,y,300,20); FormInput.add(h);
        JLabel hk = new JLabel("RM.KEP.08.034.2019"); hk.setFont(new java.awt.Font("Tahoma",0,8)); hk.setBounds(700,y,150,20); FormInput.add(hk);

        y+=32;
        Label lt = new Label(); lt.setText("Tanggal :"); lt.setBounds(10,y,80,23); FormInput.add(lt);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TglAsuhan.setBounds(95,y,130,23); FormInput.add(TglAsuhan);
        Label ls = new Label(); ls.setText("Skoring :"); ls.setBounds(260,y,60,23); FormInput.add(ls);
        CmbSkoring = new ComboBox(); CmbSkoring.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"1 - Saat Masuk","2","3"}));
        CmbSkoring.setBounds(325,y,130,23); FormInput.add(CmbSkoring);

        // 1. Umur
        y+=38;
        JLabel l1 = new JLabel("Umur"); l1.setFont(new java.awt.Font("Tahoma",1,10)); l1.setBounds(10,y,200,20); FormInput.add(l1);
        RdUmur4 = new JRadioButton("Di bawah 3 tahun (4)"); RdUmur4.setBounds(220,y,200,18); RdUmur4.setOpaque(false); RdUmur4.addActionListener(rc); FormInput.add(RdUmur4);
        RdUmur3 = new JRadioButton("3 sampai < 7 tahun (3)"); RdUmur3.setBounds(220,y+18,200,18); RdUmur3.setOpaque(false); RdUmur3.addActionListener(rc); FormInput.add(RdUmur3);
        RdUmur2 = new JRadioButton("7 - 13 tahun (2)"); RdUmur2.setBounds(220,y+36,200,18); RdUmur2.setOpaque(false); RdUmur2.addActionListener(rc); FormInput.add(RdUmur2);
        RdUmur1 = new JRadioButton("> 13 tahun (1)"); RdUmur1.setBounds(220,y+54,200,18); RdUmur1.setOpaque(false); RdUmur1.setSelected(true); RdUmur1.addActionListener(rc); FormInput.add(RdUmur1);
        ButtonGroup g1=new ButtonGroup(); g1.add(RdUmur4);g1.add(RdUmur3);g1.add(RdUmur2);g1.add(RdUmur1);

        // 2. Jenis Kelamin
        y+=78;
        JLabel l2 = new JLabel("Jenis Kelamin"); l2.setFont(new java.awt.Font("Tahoma",1,10)); l2.setBounds(10,y,200,20); FormInput.add(l2);
        RdJKL = new JRadioButton("Laki-laki (2)"); RdJKL.setBounds(220,y,150,18); RdJKL.setOpaque(false); RdJKL.addActionListener(rc); FormInput.add(RdJKL);
        RdJKP = new JRadioButton("Perempuan (1)"); RdJKP.setBounds(220,y+18,150,18); RdJKP.setOpaque(false); RdJKP.setSelected(true); RdJKP.addActionListener(rc); FormInput.add(RdJKP);
        ButtonGroup g2=new ButtonGroup(); g2.add(RdJKL);g2.add(RdJKP);

        // 3. Diagnosa
        y+=44;
        JLabel l3 = new JLabel("Diagnosa"); l3.setFont(new java.awt.Font("Tahoma",1,10)); l3.setBounds(10,y,200,20); FormInput.add(l3);
        RdDiag4 = new JRadioButton("Diagnosis terkait neurologis (4)"); RdDiag4.setBounds(220,y,280,18); RdDiag4.setOpaque(false); RdDiag4.addActionListener(rc); FormInput.add(RdDiag4);
        RdDiag3 = new JRadioButton("Perubahan oksigenasi/dehidrasi/anemia/dll (3)"); RdDiag3.setBounds(220,y+18,350,18); RdDiag3.setOpaque(false); RdDiag3.addActionListener(rc); FormInput.add(RdDiag3);
        RdDiag2 = new JRadioButton("Gangguan psikis/perilaku (2)"); RdDiag2.setBounds(220,y+36,250,18); RdDiag2.setOpaque(false); RdDiag2.addActionListener(rc); FormInput.add(RdDiag2);
        RdDiag1 = new JRadioButton("Diagnosa lain (1)"); RdDiag1.setBounds(220,y+54,200,18); RdDiag1.setOpaque(false); RdDiag1.setSelected(true); RdDiag1.addActionListener(rc); FormInput.add(RdDiag1);
        ButtonGroup g3=new ButtonGroup(); g3.add(RdDiag4);g3.add(RdDiag3);g3.add(RdDiag2);g3.add(RdDiag1);

        // 4. Gangguan Kognitif
        y+=78;
        JLabel l4 = new JLabel("Gangguan Kognitif"); l4.setFont(new java.awt.Font("Tahoma",1,10)); l4.setBounds(10,y,200,20); FormInput.add(l4);
        RdKog3 = new JRadioButton("Tidak menyadari keterbatasan (3)"); RdKog3.setBounds(220,y,280,18); RdKog3.setOpaque(false); RdKog3.addActionListener(rc); FormInput.add(RdKog3);
        RdKog2 = new JRadioButton("Lupa akan adanya keterbatasan (2)"); RdKog2.setBounds(220,y+18,280,18); RdKog2.setOpaque(false); RdKog2.addActionListener(rc); FormInput.add(RdKog2);
        RdKog1 = new JRadioButton("Orientasi baik terhadap diri sendiri (1)"); RdKog1.setBounds(220,y+36,300,18); RdKog1.setOpaque(false); RdKog1.setSelected(true); RdKog1.addActionListener(rc); FormInput.add(RdKog1);
        ButtonGroup g4=new ButtonGroup(); g4.add(RdKog3);g4.add(RdKog2);g4.add(RdKog1);

        // 5. Faktor Lingkungan
        y+=62;
        JLabel l5 = new JLabel("Faktor Lingkungan"); l5.setFont(new java.awt.Font("Tahoma",1,10)); l5.setBounds(10,y,200,20); FormInput.add(l5);
        RdLing4 = new JRadioButton("Riwayat jatuh / bayi tempat tidur besar (4)"); RdLing4.setBounds(220,y,330,18); RdLing4.setOpaque(false); RdLing4.addActionListener(rc); FormInput.add(RdLing4);
        RdLing3 = new JRadioButton("Alat bantu / bayi box / furnitur kurang (3)"); RdLing3.setBounds(220,y+18,330,18); RdLing3.setOpaque(false); RdLing3.addActionListener(rc); FormInput.add(RdLing3);
        RdLing2 = new JRadioButton("Pasien di tempat tidur (2)"); RdLing2.setBounds(220,y+36,250,18); RdLing2.setOpaque(false); RdLing2.addActionListener(rc); FormInput.add(RdLing2);
        RdLing1 = new JRadioButton("Area rawat jalan (1)"); RdLing1.setBounds(220,y+54,200,18); RdLing1.setOpaque(false); RdLing1.setSelected(true); RdLing1.addActionListener(rc); FormInput.add(RdLing1);
        ButtonGroup g5=new ButtonGroup(); g5.add(RdLing4);g5.add(RdLing3);g5.add(RdLing2);g5.add(RdLing1);

        // 6. Respon terhadap operasi/obat
        y+=78;
        JLabel l6 = new JLabel("<html>Respon terhadap operasi/<br>obat penenang/efek samping</html>"); l6.setFont(new java.awt.Font("Tahoma",1,10)); l6.setBounds(10,y,210,34); FormInput.add(l6);
        RdResp3 = new JRadioButton("Dalam 24 jam (3)"); RdResp3.setBounds(220,y,200,18); RdResp3.setOpaque(false); RdResp3.addActionListener(rc); FormInput.add(RdResp3);
        RdResp2 = new JRadioButton("Dalam 48 jam (2)"); RdResp2.setBounds(220,y+18,200,18); RdResp2.setOpaque(false); RdResp2.addActionListener(rc); FormInput.add(RdResp2);
        RdResp1 = new JRadioButton("> 48 jam (1)"); RdResp1.setBounds(220,y+36,200,18); RdResp1.setOpaque(false); RdResp1.setSelected(true); RdResp1.addActionListener(rc); FormInput.add(RdResp1);
        ButtonGroup g6=new ButtonGroup(); g6.add(RdResp3);g6.add(RdResp2);g6.add(RdResp1);

        // 7. Penggunaan Obat
        y+=62;
        JLabel l7 = new JLabel("Penggunaan Obat"); l7.setFont(new java.awt.Font("Tahoma",1,10)); l7.setBounds(10,y,200,20); FormInput.add(l7);
        RdObat3 = new JRadioButton("Bermacam-macam obat sedatif/dll (3)"); RdObat3.setBounds(220,y,300,18); RdObat3.setOpaque(false); RdObat3.addActionListener(rc); FormInput.add(RdObat3);
        RdObat2 = new JRadioButton("Salah satu dari obat diatas (2)"); RdObat2.setBounds(220,y+18,260,18); RdObat2.setOpaque(false); RdObat2.addActionListener(rc); FormInput.add(RdObat2);
        RdObat1 = new JRadioButton("Pengobatan lain (1)"); RdObat1.setBounds(220,y+36,200,18); RdObat1.setOpaque(false); RdObat1.setSelected(true); RdObat1.addActionListener(rc); FormInput.add(RdObat1);
        ButtonGroup g7=new ButtonGroup(); g7.add(RdObat3);g7.add(RdObat2);g7.add(RdObat1);

        // Total & Level
        y+=62;
        LTotalNilai = new Label(); LTotalNilai.setText("Total Nilai : 7"); LTotalNilai.setFont(new java.awt.Font("Tahoma",1,12)); LTotalNilai.setBounds(10,y,250,23); FormInput.add(LTotalNilai);
        LLevelResiko = new Label(); LLevelResiko.setText("Tidak beresiko (7-11)"); LLevelResiko.setFont(new java.awt.Font("Tahoma",1,12)); LLevelResiko.setBounds(280,y,300,23); LLevelResiko.setForeground(new Color(0,128,0)); FormInput.add(LLevelResiko);

        // Petugas
        y+=32;
        Label lp = new Label(); lp.setText("Petugas :"); lp.setBounds(10,y,65,23); FormInput.add(lp);
        KdPetugas = new TextBox(); KdPetugas.setBounds(80,y,80,23); FormInput.add(KdPetugas);
        NmPetugas = new TextBox(); NmPetugas.setBounds(162,y,165,23); FormInput.add(NmPetugas);
        BtnPetugas = new Button(); BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); BtnPetugas.setBounds(329,y,28,23); FormInput.add(BtnPetugas);
        BtnPetugas.addActionListener(ev->{petugasDlg.emptTeks();petugasDlg.isCek();petugasDlg.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);petugasDlg.setLocationRelativeTo(internalFrame1);petugasDlg.setVisible(true);});
        petugasDlg.addWindowListener(new java.awt.event.WindowAdapter(){@Override public void windowClosed(java.awt.event.WindowEvent e){if(petugasDlg.getTable().getSelectedRow()!=-1){KdPetugas.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(),0).toString());NmPetugas.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(),1).toString());}}});

        ChkInput = new CekBox(); ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setText(".: Input Data"); ChkInput.setSelected(true); ChkInput.setBorderPainted(true); ChkInput.setBorderPaintedFlat(true); ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(SwingConstants.LEFT); ChkInput.setPreferredSize(new Dimension(192,20));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.addActionListener(ev->isForm());

        ScrollInput = new ScrollPane(); ScrollInput.setViewportView(FormInput); ScrollInput.setPreferredSize(new Dimension(880,440));
        PanelInput = new JPanel(new BorderLayout()); PanelInput.setOpaque(false); PanelInput.setPreferredSize(new Dimension(WIDTH,460));
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
            if(KdPetugas.getText().trim().equals("")){Valid.textKosong(KdPetugas,"Petugas");return;}
            String sk=CmbSkoring.getSelectedItem().toString().substring(0,1);
            int u=RdUmur4.isSelected()?4:(RdUmur3.isSelected()?3:(RdUmur2.isSelected()?2:1));
            int j=RdJKL.isSelected()?2:1;
            int d=RdDiag4.isSelected()?4:(RdDiag3.isSelected()?3:(RdDiag2.isSelected()?2:1));
            int k=RdKog3.isSelected()?3:(RdKog2.isSelected()?2:1);
            int l=RdLing4.isSelected()?4:(RdLing3.isSelected()?3:(RdLing2.isSelected()?2:1));
            int r=RdResp3.isSelected()?3:(RdResp2.isSelected()?2:1);
            int o=RdObat3.isSelected()?3:(RdObat2.isSelected()?2:1);
            int total=u+j+d+k+l+r+o;
            String level=total<=11?"Tidak beresiko":(total<=15?"Resiko rendah":"Risiko tinggi");
            if(Sequel.menyimpantf("skala_humpty_dumpty","?,?,?,?,?,?,?,?,?,?,?,?,?","Data",13,new String[]{
                TNoRw.getText(),Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19),
                sk,""+u,""+j,""+d,""+k,""+l,""+r,""+o,""+total,level,KdPetugas.getText()
            })){tampil();emptTeks();}
        });

        BtnHapus.addActionListener(ev->{
            if(tbObat.getSelectedRow()!=-1){if(Sequel.queryu2tf("delete from skala_humpty_dumpty where no_rawat=? and tanggal=? and skoring=?",3,new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),3).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),4).toString()
            })){tampil();emptTeks();}}else JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");
        });

        BtnEdit.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")||tbObat.getSelectedRow()==-1){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            if(Sequel.queryu2tf("delete from skala_humpty_dumpty where no_rawat=? and tanggal=? and skoring=?",3,new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),3).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),4).toString()
            })){BtnSimpan.doClick();}
        });

        BtnPrint.addActionListener(ev->{
            if(TNoRw.getText().trim().equals("")){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            Map<String,Object> p=new HashMap<>();p.put("namars",akses.getnamars());p.put("alamatrs",akses.getalamatrs());
            p.put("propinsirs",akses.getpropinsirs());p.put("kabupatenrs",akses.getkabupatenrs());
            p.put("kontakrs",akses.getkontakrs());p.put("emailrs",akses.getemailrs());
            p.put("logo",Sequel.cariGambar("select setting.logo from setting"));
            Valid.MyReportqry("rptSkalaHumptyDumpty.jasper","report","::[ Skala Humpty Dumpty ]::",
                "select s.*,r.no_rkm_medis,p.nm_pasien,p.tgl_lahir,p.no_ktp,pt.nama as nm_petugas from skala_humpty_dumpty s inner join reg_periksa r on s.no_rawat=r.no_rawat inner join pasien p on r.no_rkm_medis=p.no_rkm_medis left join petugas pt on s.nip=pt.nip where s.no_rawat='"+TNoRw.getText()+"'",p);
        });

        InternalFrame ifd=new InternalFrame();ifd.setLayout(new BorderLayout());ifd.setBackground(new Color(255,255,255));
        ifd.add(PanelInput,BorderLayout.NORTH);ifd.add(Scroll,BorderLayout.CENTER);
        internalFrame1.add(panelAtas,BorderLayout.PAGE_START);internalFrame1.add(ifd,BorderLayout.CENTER);internalFrame1.add(panelBawah,BorderLayout.PAGE_END);
        setLayout(new BorderLayout());add(internalFrame1,BorderLayout.CENTER);setDefaultCloseOperation(DISPOSE_ON_CLOSE);setUndecorated(true);
    }

    private void isForm(){if(ChkInput.isSelected()){ChkInput.setVisible(false);PanelInput.setPreferredSize(new Dimension(WIDTH,460));FormInput.setVisible(true);ChkInput.setVisible(true);}else{ChkInput.setVisible(false);PanelInput.setPreferredSize(new Dimension(WIDTH,20));FormInput.setVisible(false);ChkInput.setVisible(true);}}

    public void tampil(){
        Valid.tabelKosong(tabMode);
        try{ps=koneksi.prepareStatement("select s.no_rawat,r.no_rkm_medis,p.nm_pasien,s.tanggal,s.skoring,s.umur,s.jenis_kelamin,s.diagnosa,s.gangguan_kognitif,s.faktor_lingkungan,s.respon_operasi,s.penggunaan_obat,s.total_nilai,s.level_resiko,pt.nama as nm_petugas from skala_humpty_dumpty s inner join reg_periksa r on s.no_rawat=r.no_rawat inner join pasien p on r.no_rkm_medis=p.no_rkm_medis left join petugas pt on s.nip=pt.nip where s.tanggal between ? and ? and (s.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) order by s.tanggal desc,s.skoring");
        ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
        ps.setString(3,"%"+TCari.getText().trim()+"%");ps.setString(4,"%"+TCari.getText().trim()+"%");ps.setString(5,"%"+TCari.getText().trim()+"%");
        rs=ps.executeQuery();while(rs.next()){tabMode.addRow(new String[]{rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),rs.getString("tanggal"),rs.getString("skoring"),rs.getString("umur"),rs.getString("jenis_kelamin"),rs.getString("diagnosa"),rs.getString("gangguan_kognitif"),rs.getString("faktor_lingkungan"),rs.getString("respon_operasi"),rs.getString("penggunaan_obat"),rs.getString("total_nilai"),rs.getString("level_resiko"),rs.getString("nm_petugas")});}
        LCount.setText("Record : "+tabMode.getRowCount());}catch(Exception e){System.out.println("Notif HD : "+e);}finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
    }

    public void getData(){
        if(tbObat.getSelectedRow()!=-1){
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            try{TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()));}catch(Exception e){}
            String sk=tbObat.getValueAt(tbObat.getSelectedRow(),4).toString();CmbSkoring.setSelectedIndex(sk.equals("2")?1:(sk.equals("3")?2:0));
            int u=Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString());
            if(u==4)RdUmur4.setSelected(true);else if(u==3)RdUmur3.setSelected(true);else if(u==2)RdUmur2.setSelected(true);else RdUmur1.setSelected(true);
            int j=Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString());if(j==2)RdJKL.setSelected(true);else RdJKP.setSelected(true);
            int d=Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString());
            if(d==4)RdDiag4.setSelected(true);else if(d==3)RdDiag3.setSelected(true);else if(d==2)RdDiag2.setSelected(true);else RdDiag1.setSelected(true);
            int k=Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(),8).toString());if(k==3)RdKog3.setSelected(true);else if(k==2)RdKog2.setSelected(true);else RdKog1.setSelected(true);
            int l=Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(),9).toString());
            if(l==4)RdLing4.setSelected(true);else if(l==3)RdLing3.setSelected(true);else if(l==2)RdLing2.setSelected(true);else RdLing1.setSelected(true);
            int r=Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(),10).toString());if(r==3)RdResp3.setSelected(true);else if(r==2)RdResp2.setSelected(true);else RdResp1.setSelected(true);
            int o=Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(),11).toString());if(o==3)RdObat3.setSelected(true);else if(o==2)RdObat2.setSelected(true);else RdObat1.setSelected(true);
            hitungTotal();
            KdPetugas.setText(Sequel.cariIsi("select nip from skala_humpty_dumpty where no_rawat='"+tbObat.getValueAt(tbObat.getSelectedRow(),0)+"' and tanggal='"+tbObat.getValueAt(tbObat.getSelectedRow(),3)+"' and skoring='"+sk+"'"));
            NmPetugas.setText(tbObat.getValueAt(tbObat.getSelectedRow(),14).toString());
        }
    }

    private void cetakPerBaris(){
        if(tbObat.getSelectedRow()>-1){
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String,Object> param=new HashMap<>();
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kabupatenrs",akses.getkabupatenrs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());
            param.put("logo",Sequel.cariGambar("select setting.logo from setting"));
            String nip=Sequel.cariIsi("select nip from skala_humpty_dumpty where no_rawat='"+tbObat.getValueAt(tbObat.getSelectedRow(),0)+"' and tanggal='"+tbObat.getValueAt(tbObat.getSelectedRow(),3)+"' and skoring='"+tbObat.getValueAt(tbObat.getSelectedRow(),4)+"'");
            String finger=Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",nip);
            param.put("finger","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+tbObat.getValueAt(tbObat.getSelectedRow(),14).toString()+"\nID "+(finger.equals("")?nip:finger)+"\n"+tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            Valid.MyReportqry("rptSkalaHumptyDumpty.jasper","report","::[ Skala Humpty Dumpty ]::",
                "select s.*,r.no_rkm_medis,p.nm_pasien,p.tgl_lahir,p.no_ktp,pt.nama as nm_petugas "+
                "from skala_humpty_dumpty s inner join reg_periksa r on s.no_rawat=r.no_rawat "+
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis left join petugas pt on s.nip=pt.nip "+
                "where s.no_rawat='"+tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()+"' "+
                "and s.tanggal='"+tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()+"' "+
                "and s.skoring='"+tbObat.getValueAt(tbObat.getSelectedRow(),4).toString()+"'",param);
            this.setCursor(Cursor.getDefaultCursor());
        }else{
            JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");
        }
    }

    public void emptTeks(){TglAsuhan.setDate(new Date());CmbSkoring.setSelectedIndex(0);RdUmur1.setSelected(true);RdJKP.setSelected(true);RdDiag1.setSelected(true);RdKog1.setSelected(true);RdLing1.setSelected(true);RdResp1.setSelected(true);RdObat1.setSelected(true);KdPetugas.setText("");NmPetugas.setText("");hitungTotal();}
    public void setNoRm(String n,String m,String p){TNoRw.setText(n);TNoRM.setText(m);TPasien.setText(p);}
    public void setNoRm(String n,Date t){TNoRw.setText(n);TglAsuhan.setDate(t);TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?",n));TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?",TNoRM.getText()));}
    public void isCek(){BtnSimpan.setEnabled(akses.getrujukan_keluar());BtnHapus.setEnabled(akses.getrujukan_keluar());BtnEdit.setEnabled(akses.getrujukan_keluar());BtnPrint.setEnabled(akses.getrujukan_keluar());}
}
