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
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.Date;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import widget.*;

/**
 * Form Early Warning Score (EWS) - RM.KEP.08.015.2019
 * Untuk pasien usia > 18 tahun
 */
public class RMFormEWSRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2, TglObservasi;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private Label LCount;
    private widget.Table tbObat;
    private DefaultTableModel tabMode;
    private ScrollPane Scroll, ScrollInput;
    private CekBox ChkInput;
    private PanelBiasa FormInput;
    private JPanel PanelInput;

    // EWS Input Fields
    private ComboBox CmbKesadaran;
    private TextBox TTdSistolik, TNadi, TPernafasan, TSuhu, TSpO2;
    private ComboBox CmbOksigen;

    // Score Labels (auto-calculated)
    private JLabel LSkorKesadaran, LSkorTD, LSkorNadi, LSkorPernafasan, LSkorSuhu, LSkorSpO2, LSkorOksigen;
    private JLabel LTotalEWS, LKategori;

    // Petugas
    private TextBox NIP, NamaPetugas;
    private Button BtnPetugas;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);

    public RMFormEWSRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width, screen.height);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(240,245,235)),
            "::[ Form Early Warning Score (EWS) - RM.KEP.08.015.2019 ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION, new Font("Tahoma",0,11), new Color(50,50,50)));
        internalFrame1.setLayout(new BorderLayout(1,1));

        // === TOP PANEL ===
        panelisi panelAtas = new panelisi();
        panelAtas.setLayout(new FlowLayout(FlowLayout.LEFT,5,4));
        panelAtas.setPreferredSize(new Dimension(1024,33));
        Label lblNoRw = new Label(); lblNoRw.setText("No.Rawat :");
        TNoRw = new TextBox(); TNoRw.setPreferredSize(new Dimension(130,23));
        TNoRM = new TextBox(); TNoRM.setPreferredSize(new Dimension(100,23));
        TPasien = new TextBox(); TPasien.setPreferredSize(new Dimension(200,23));
        panelAtas.add(lblNoRw); panelAtas.add(TNoRw); panelAtas.add(TNoRM); panelAtas.add(TPasien);

        // === LIST TABLE ===
        Scroll = new ScrollPane();
        tbObat = new widget.Table();
        tabMode = new DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat","No.R.M.","Nama","Tgl.Observasi","Kesadaran","TD Sistolik","Nadi","Pernafasan","Suhu","SpO2","O2","Total EWS","Kategori","Petugas"}
        ) { @Override public boolean isCellEditable(int r,int c){return false;} };
        tbObat.setModel(tabMode);
        JPopupMenu popupTabel = new JPopupMenu();
        JMenuItem mnuCetakEWS = new JMenuItem("Cetak / Print");
        mnuCetakEWS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnuCetakEWS.addActionListener(e -> cetakReport());
        popupTabel.add(mnuCetakEWS);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter(){
            @Override public void mouseClicked(java.awt.event.MouseEvent e){if(tabMode.getRowCount()!=0) getData();}
            @Override public void mousePressed(java.awt.event.MouseEvent e){
                int row=tbObat.rowAtPoint(e.getPoint());
                if(row>=0&&row<tabMode.getRowCount()) tbObat.setRowSelectionInterval(row,row);
                if(javax.swing.SwingUtilities.isRightMouseButton(e)&&tabMode.getRowCount()!=0){
                    getData();
                    popupTabel.show(tbObat, e.getX(), e.getY());
                }
            }
        });
        Scroll.setViewportView(tbObat);

        // === FORM INPUT ===
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(900,460));
        FormInput.setLayout(null);

        int y = 10;
        JLabel hTitle = new JLabel("FORM EARLY WARNING SCORE (EWS)  —  Pasien Usia > 18 Tahun");
        hTitle.setFont(new Font("Tahoma",Font.BOLD,12)); hTitle.setBounds(10,y,600,20); FormInput.add(hTitle);
        JLabel hCode = new JLabel("RM.KEP.08.015.2019");
        hCode.setFont(new Font("Tahoma",0,9)); hCode.setBounds(700,y,150,20); FormInput.add(hCode);

        // Tanggal & Jam
        y += 32;
        Label ltgl = new Label(); ltgl.setText("Tgl. Observasi :"); ltgl.setBounds(10,y,110,23); FormInput.add(ltgl);
        TglObservasi = new widget.Tanggal(); TglObservasi.setDisplayFormat("dd-MM-yyyy HH:mm:ss"); TglObservasi.setBounds(125,y,175,23); FormInput.add(TglObservasi);

        // Header baris parameter
        y += 34;
        JLabel hParam = new JLabel("Parameter");
        hParam.setFont(new Font("Tahoma",Font.BOLD,10)); hParam.setForeground(new Color(0,0,120));
        hParam.setBounds(10,y,150,18); FormInput.add(hParam);
        JLabel hNilai = new JLabel("Nilai");
        hNilai.setFont(new Font("Tahoma",Font.BOLD,10)); hNilai.setForeground(new Color(0,0,120));
        hNilai.setBounds(200,y,80,18); FormInput.add(hNilai);
        JLabel hSkor = new JLabel("SKOR");
        hSkor.setFont(new Font("Tahoma",Font.BOLD,10)); hSkor.setForeground(new Color(0,0,120));
        hSkor.setBounds(380,y,80,18); FormInput.add(hSkor);
        JLabel hAcuan = new JLabel("Panduan Tindakan EWS");
        hAcuan.setFont(new Font("Tahoma",Font.BOLD,10)); hAcuan.setForeground(new Color(0,0,120));
        hAcuan.setBounds(460,y,300,18); FormInput.add(hAcuan);

        // Separator line
        y += 20;
        JSeparator sep = new JSeparator(); sep.setBounds(10,y,445,2); FormInput.add(sep);

        // ---- Panduan Tindakan EWS (kanan) ---- posisi fixed
        {
            int py = y - 20; // sejajar dengan header baris
            String[][] refData = {
                {"0",        "Min. tiap 12 jam",      "Lanjutkan observasi/monitoring rutin"},
                {"1 - 4",    "Min. tiap 4-6 jam",     "Informasikan ke ketua tim/PJ jaga; tingkatkan frekuensi observasi"},
                {"5-6/3*",   "Min. tiap 1 jam",       "Ketua Tim laporkan ke dokter jaga/DPJP; siapkan monitoring lebih canggih"},
                {"≥ 7",      "TTV lanjut",            "Laporkan ke Tim Code Blue; stabilisasi; rujuk Intermediate Care"}
            };
            String[] refCol = {"Skor","Frekuensi","Tindakan"};
            DefaultTableModel refModel = new DefaultTableModel(refData, refCol){@Override public boolean isCellEditable(int r,int c){return false;}};
            JTable tblRef = new JTable(refModel);
            tblRef.getColumnModel().getColumn(0).setPreferredWidth(45);
            tblRef.getColumnModel().getColumn(1).setPreferredWidth(90);
            tblRef.getColumnModel().getColumn(2).setPreferredWidth(245);
            tblRef.setRowHeight(50);
            tblRef.setFont(new Font("Tahoma",0,9));
            tblRef.getTableHeader().setFont(new Font("Tahoma",Font.BOLD,9));
            // wrap renderer for tindakan column
            javax.swing.table.DefaultTableCellRenderer wrap = new javax.swing.table.DefaultTableCellRenderer(){
                @Override public java.awt.Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
                    JTextArea ta=new JTextArea(v==null?"":v.toString());
                    ta.setWrapStyleWord(true); ta.setLineWrap(true); ta.setFont(new Font("Tahoma",0,9));
                    ta.setBackground(sel?t.getSelectionBackground():t.getBackground());
                    ta.setBorder(javax.swing.BorderFactory.createEmptyBorder(2,4,2,4));
                    return ta;
                }
            };
            tblRef.getColumnModel().getColumn(2).setCellRenderer(wrap);
            JScrollPane scrollRef = new JScrollPane(tblRef);
            scrollRef.setBounds(460, py, 390, 275); FormInput.add(scrollRef);
        }

        // ---- ROW 1: Tingkat Kesadaran ----
        y += 8;
        JLabel lKes = new JLabel("Tingkat Kesadaran");
        lKes.setFont(new Font("Tahoma",Font.BOLD,10)); lKes.setBounds(10,y,155,23); FormInput.add(lKes);
        CmbKesadaran = new ComboBox();
        CmbKesadaran.setModel(new DefaultComboBoxModel(new String[]{"Sadar","V/P/U"}));
        CmbKesadaran.setBounds(200,y,100,23); FormInput.add(CmbKesadaran);
        LSkorKesadaran = buatLabelSkor("0"); LSkorKesadaran.setBounds(380,y,60,23); FormInput.add(LSkorKesadaran);
        CmbKesadaran.addActionListener(e -> hitungSemua());

        // ---- ROW 2: Tekanan Darah Sistolik ----
        y += 28;
        JLabel lTD = new JLabel("Tekanan Darah (Sistolik)");
        lTD.setFont(new Font("Tahoma",Font.BOLD,10)); lTD.setBounds(10,y,175,23); FormInput.add(lTD);
        TTdSistolik = new TextBox(); TTdSistolik.setBounds(200,y,80,23); FormInput.add(TTdSistolik);
        JLabel lUnitTD = new JLabel("mmHg"); lUnitTD.setFont(new Font("Tahoma",0,9)); lUnitTD.setBounds(284,y,45,23); FormInput.add(lUnitTD);
        LSkorTD = buatLabelSkor(""); LSkorTD.setBounds(380,y,60,23); FormInput.add(LSkorTD);
        TTdSistolik.getDocument().addDocumentListener(dl());

        // ---- ROW 3: Nadi ----
        y += 28;
        JLabel lNadi = new JLabel("Nadi");
        lNadi.setFont(new Font("Tahoma",Font.BOLD,10)); lNadi.setBounds(10,y,155,23); FormInput.add(lNadi);
        TNadi = new TextBox(); TNadi.setBounds(200,y,80,23); FormInput.add(TNadi);
        JLabel lUnitNadi = new JLabel("x/mnt"); lUnitNadi.setFont(new Font("Tahoma",0,9)); lUnitNadi.setBounds(284,y,45,23); FormInput.add(lUnitNadi);
        LSkorNadi = buatLabelSkor(""); LSkorNadi.setBounds(380,y,60,23); FormInput.add(LSkorNadi);
        TNadi.getDocument().addDocumentListener(dl());

        // ---- ROW 4: Pernafasan ----
        y += 28;
        JLabel lRR = new JLabel("Pernafasan");
        lRR.setFont(new Font("Tahoma",Font.BOLD,10)); lRR.setBounds(10,y,155,23); FormInput.add(lRR);
        TPernafasan = new TextBox(); TPernafasan.setBounds(200,y,80,23); FormInput.add(TPernafasan);
        JLabel lUnitRR = new JLabel("x/mnt"); lUnitRR.setFont(new Font("Tahoma",0,9)); lUnitRR.setBounds(284,y,45,23); FormInput.add(lUnitRR);
        LSkorPernafasan = buatLabelSkor(""); LSkorPernafasan.setBounds(380,y,60,23); FormInput.add(LSkorPernafasan);
        TPernafasan.getDocument().addDocumentListener(dl());

        // ---- ROW 5: Suhu ----
        y += 28;
        JLabel lSuhu = new JLabel("Suhu");
        lSuhu.setFont(new Font("Tahoma",Font.BOLD,10)); lSuhu.setBounds(10,y,155,23); FormInput.add(lSuhu);
        TSuhu = new TextBox(); TSuhu.setBounds(200,y,80,23); FormInput.add(TSuhu);
        JLabel lUnitSuhu = new JLabel("°C"); lUnitSuhu.setFont(new Font("Tahoma",0,9)); lUnitSuhu.setBounds(284,y,25,23); FormInput.add(lUnitSuhu);
        LSkorSuhu = buatLabelSkor(""); LSkorSuhu.setBounds(380,y,60,23); FormInput.add(LSkorSuhu);
        TSuhu.getDocument().addDocumentListener(dl());

        // ---- ROW 6: SpO2 ----
        y += 28;
        JLabel lSpO2 = new JLabel("SpO2");
        lSpO2.setFont(new Font("Tahoma",Font.BOLD,10)); lSpO2.setBounds(10,y,155,23); FormInput.add(lSpO2);
        TSpO2 = new TextBox(); TSpO2.setBounds(200,y,80,23); FormInput.add(TSpO2);
        JLabel lUnitSpO2 = new JLabel("%"); lUnitSpO2.setFont(new Font("Tahoma",0,9)); lUnitSpO2.setBounds(284,y,20,23); FormInput.add(lUnitSpO2);
        LSkorSpO2 = buatLabelSkor(""); LSkorSpO2.setBounds(380,y,60,23); FormInput.add(LSkorSpO2);
        TSpO2.getDocument().addDocumentListener(dl());

        // ---- ROW 7: Oksigen ----
        y += 28;
        JLabel lO2 = new JLabel("Oksigen (Alat Bantu)");
        lO2.setFont(new Font("Tahoma",Font.BOLD,10)); lO2.setBounds(10,y,155,23); FormInput.add(lO2);
        CmbOksigen = new ComboBox();
        CmbOksigen.setModel(new DefaultComboBoxModel(new String[]{"Tidak","Ya"}));
        CmbOksigen.setBounds(200,y,80,23); FormInput.add(CmbOksigen);
        LSkorOksigen = buatLabelSkor("0"); LSkorOksigen.setBounds(380,y,60,23); FormInput.add(LSkorOksigen);
        CmbOksigen.addActionListener(e -> hitungSemua());

        // ---- Separator ----
        y += 32;
        JSeparator sep2 = new JSeparator(); sep2.setBounds(10,y,445,2); FormInput.add(sep2);

        // ---- TOTAL EWS ----
        y += 8;
        JLabel lTotalLabel = new JLabel("TOTAL Early Warning Score :");
        lTotalLabel.setFont(new Font("Tahoma",Font.BOLD,12)); lTotalLabel.setBounds(10,y,230,28); FormInput.add(lTotalLabel);
        LTotalEWS = new JLabel("—");
        LTotalEWS.setFont(new Font("Tahoma",Font.BOLD,18)); LTotalEWS.setForeground(new Color(0,80,0));
        LTotalEWS.setOpaque(true); LTotalEWS.setBackground(new Color(220,255,220));
        LTotalEWS.setBorder(BorderFactory.createLineBorder(new Color(0,120,0)));
        LTotalEWS.setHorizontalAlignment(SwingConstants.CENTER);
        LTotalEWS.setBounds(245,y,60,28); FormInput.add(LTotalEWS);

        // ---- Kategori / Tindakan ----
        y += 35;
        JLabel lKatLabel = new JLabel("Kategori / Tindakan :");
        lKatLabel.setFont(new Font("Tahoma",Font.BOLD,10)); lKatLabel.setBounds(10,y,155,23); FormInput.add(lKatLabel);
        LKategori = new JLabel("");
        LKategori.setFont(new Font("Tahoma",Font.BOLD,10)); LKategori.setForeground(new Color(180,0,0));
        LKategori.setOpaque(true); LKategori.setBackground(new Color(255,255,210));
        LKategori.setBorder(BorderFactory.createLineBorder(Color.ORANGE));
        LKategori.setBounds(170,y,280,23); FormInput.add(LKategori);

        // ---- Petugas ----
        y += 34;
        Label lnip = new Label(); lnip.setText("Paraf & Nama Petugas :"); lnip.setBounds(10,y,155,23); FormInput.add(lnip);
        NIP = new TextBox(); NIP.setBounds(170,y,80,23); FormInput.add(NIP);
        NamaPetugas = new TextBox(); NamaPetugas.setBounds(255,y,220,23); FormInput.add(NamaPetugas);
        BtnPetugas = new Button(); BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPetugas.setBounds(480,y,28,23); FormInput.add(BtnPetugas);
        BtnPetugas.addActionListener(ev->{petugasDlg.emptTeks();petugasDlg.isCek();petugasDlg.setSize(800,500);petugasDlg.setLocationRelativeTo(null);petugasDlg.setVisible(true);});
        petugasDlg.addWindowListener(new java.awt.event.WindowAdapter(){
            @Override public void windowClosed(java.awt.event.WindowEvent e){
                if(petugasDlg.getTable().getSelectedRow()!=-1){
                    NIP.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(),0).toString());
                    NamaPetugas.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(),1).toString());
                }
            }
        });


        // ---- ChkInput toggle ----
        ChkInput = new CekBox();
        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setText(".: Input Data"); ChkInput.setSelected(true); ChkInput.setBorderPainted(true);
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.addActionListener(ev->isForm());

        ScrollInput = new ScrollPane(); ScrollInput.setViewportView(FormInput); ScrollInput.setPreferredSize(new Dimension(900,470));
        PanelInput = new JPanel(new BorderLayout()); PanelInput.setOpaque(false); PanelInput.setPreferredSize(new Dimension(WIDTH,490));
        PanelInput.add(ScrollInput, BorderLayout.CENTER); PanelInput.add(ChkInput, BorderLayout.PAGE_END);

        // ---- Bottom buttons ----
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
            if(TNoRw.getText().trim().isEmpty()){Valid.textKosong(TNoRw,"Pasien");return;}
            if(NIP.getText().trim().isEmpty()){Valid.textKosong(NIP,"Petugas");return;}
            int total = hitungSemua();
            String tgl = Valid.SetTglJam(TglObservasi.getSelectedItem()+"");
            String kat = LKategori.getText();
            if(Sequel.menyimpantf("ews_monitoring_rscb",
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?",
                "Data",19, new String[]{
                    TNoRw.getText(), tgl,
                    CmbKesadaran.getSelectedItem().toString(), LSkorKesadaran.getText(),
                    TTdSistolik.getText(), LSkorTD.getText(),
                    TNadi.getText(), LSkorNadi.getText(),
                    TPernafasan.getText(), LSkorPernafasan.getText(),
                    TSuhu.getText(), LSkorSuhu.getText(),
                    TSpO2.getText(), LSkorSpO2.getText(),
                    CmbOksigen.getSelectedItem().toString(), LSkorOksigen.getText(),
                    String.valueOf(total), kat, NIP.getText()
                })){tampil();emptTeks();}
        });

        BtnHapus.addActionListener(ev->{
            if(tbObat.getSelectedRow()==-1){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            String nr=tabMode.getValueAt(tbObat.getSelectedRow(),0).toString();
            String tgl=tabMode.getValueAt(tbObat.getSelectedRow(),3).toString();
            if(JOptionPane.showConfirmDialog(null,"Hapus data EWS "+tgl+"?","Konfirmasi",JOptionPane.YES_NO_OPTION)==0){
                Sequel.queryu2tf("delete from ews_monitoring_rscb where no_rawat=? and tanggal=?",2,new String[]{nr,tgl});
                tampil();emptTeks();
            }
        });

        BtnEdit.addActionListener(ev->{
            if(TNoRw.getText().trim().isEmpty()||tbObat.getSelectedRow()==-1){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
            String nr=tabMode.getValueAt(tbObat.getSelectedRow(),0).toString();
            String tgl=tabMode.getValueAt(tbObat.getSelectedRow(),3).toString();
            Sequel.queryu2tf("delete from ews_monitoring_rscb where no_rawat=? and tanggal=?",2,new String[]{nr,tgl});
            BtnSimpan.doClick();
        });

        BtnPrint.addActionListener(ev -> cetakReport());

        InternalFrame ifd=new InternalFrame();ifd.setLayout(new BorderLayout());ifd.setBackground(Color.WHITE);
        ifd.add(PanelInput,BorderLayout.NORTH);ifd.add(Scroll,BorderLayout.CENTER);
        internalFrame1.add(panelAtas,BorderLayout.PAGE_START);internalFrame1.add(ifd,BorderLayout.CENTER);internalFrame1.add(panelBawah,BorderLayout.PAGE_END);
        setLayout(new BorderLayout());add(internalFrame1,BorderLayout.CENTER);setDefaultCloseOperation(DISPOSE_ON_CLOSE);setUndecorated(true);

        // Initial calc
        hitungSemua();
    }

    /** Buat JLabel skor dengan border, background warna sesuai nilai */
    private JLabel buatLabelSkor(String val) {
        JLabel lbl = new JLabel(val, SwingConstants.CENTER);
        lbl.setFont(new Font("Tahoma",Font.BOLD,12));
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        warnakanSkor(lbl, val.isEmpty() ? -1 : parseIntSafe(val));
        return lbl;
    }

    private void warnakanSkor(JLabel lbl, int skor) {
        if(skor < 0)       { lbl.setBackground(Color.LIGHT_GRAY); lbl.setForeground(Color.DARK_GRAY); }
        else if(skor == 0) { lbl.setBackground(new Color(200,255,200)); lbl.setForeground(new Color(0,100,0)); }
        else if(skor == 1) { lbl.setBackground(new Color(255,255,150)); lbl.setForeground(new Color(130,100,0)); }
        else if(skor == 2) { lbl.setBackground(new Color(255,200,100)); lbl.setForeground(new Color(150,60,0)); }
        else               { lbl.setBackground(new Color(255,100,100)); lbl.setForeground(new Color(180,0,0)); }
    }

    private void setSkor(JLabel lbl, int skor) {
        lbl.setText(String.valueOf(skor));
        warnakanSkor(lbl, skor);
    }

    /** DocumentListener shorthand */
    private DocumentListener dl() {
        return new DocumentListener(){
            public void insertUpdate(DocumentEvent e){hitungSemua();}
            public void removeUpdate(DocumentEvent e){hitungSemua();}
            public void changedUpdate(DocumentEvent e){}
        };
    }

    // ============================
    //  LOGIKA HITUNG SKOR EWS
    // ============================

    private int hitungSkorKesadaran() {
        return CmbKesadaran.getSelectedItem().toString().equals("Sadar") ? 0 : 3;
    }

    private int hitungSkorTD(int val) {
        if(val >= 230) return 3;
        if(val >= 201) return 2;
        if(val >= 161) return 1;
        if(val >= 101) return 0;
        if(val >= 81)  return 1;
        if(val >= 71)  return 2;
        return 3; // <= 70
    }

    private int hitungSkorNadi(int val) {
        if(val >= 131) return 3;
        if(val >= 111) return 2;
        if(val >= 101) return 1;
        if(val >= 51)  return 0;
        if(val >= 41)  return 1;
        return 3; // <= 40
    }

    private int hitungSkorPernafasan(int val) {
        if(val >= 30) return 3;
        if(val >= 21) return 2;
        if(val >= 18) return 1;
        if(val >= 9)  return 0;
        if(val >= 8)  return 1;
        return 3; // < 8
    }

    private int hitungSkorSuhu(double val) {
        if(val > 38.5)  return 2;
        if(val >= 38.05) return 1;
        if(val >= 36.05) return 0;
        if(val >= 35.0)  return 1;
        return 3; // < 35
    }

    private int hitungSkorSpO2(int val) {
        if(val >= 96) return 0;
        if(val >= 94) return 1;
        if(val >= 92) return 2;
        return 3; // <= 91
    }

    private int hitungSkorOksigen() {
        return CmbOksigen.getSelectedItem().toString().equals("Ya") ? 2 : 0;
    }

    /** Hitung semua skor, update label, return total */
    private int hitungSemua() {
        // Kesadaran
        int sKes = hitungSkorKesadaran();
        setSkor(LSkorKesadaran, sKes);

        // TD
        int td = parseIntSafe(TTdSistolik.getText());
        int sTD = (TTdSistolik.getText().trim().isEmpty()) ? 0 : hitungSkorTD(td);
        if(!TTdSistolik.getText().trim().isEmpty()) setSkor(LSkorTD, sTD);
        else { LSkorTD.setText(""); warnakanSkor(LSkorTD,-1); }

        // Nadi
        int nadi = parseIntSafe(TNadi.getText());
        int sNadi = (TNadi.getText().trim().isEmpty()) ? 0 : hitungSkorNadi(nadi);
        if(!TNadi.getText().trim().isEmpty()) setSkor(LSkorNadi, sNadi);
        else { LSkorNadi.setText(""); warnakanSkor(LSkorNadi,-1); }

        // Pernafasan
        int rr = parseIntSafe(TPernafasan.getText());
        int sRR = (TPernafasan.getText().trim().isEmpty()) ? 0 : hitungSkorPernafasan(rr);
        if(!TPernafasan.getText().trim().isEmpty()) setSkor(LSkorPernafasan, sRR);
        else { LSkorPernafasan.setText(""); warnakanSkor(LSkorPernafasan,-1); }

        // Suhu
        double suhu = parseDoubleSafe(TSuhu.getText());
        int sSuhu = (TSuhu.getText().trim().isEmpty()) ? 0 : hitungSkorSuhu(suhu);
        if(!TSuhu.getText().trim().isEmpty()) setSkor(LSkorSuhu, sSuhu);
        else { LSkorSuhu.setText(""); warnakanSkor(LSkorSuhu,-1); }

        // SpO2
        int spo2 = parseIntSafe(TSpO2.getText());
        int sSpO2 = (TSpO2.getText().trim().isEmpty()) ? 0 : hitungSkorSpO2(spo2);
        if(!TSpO2.getText().trim().isEmpty()) setSkor(LSkorSpO2, sSpO2);
        else { LSkorSpO2.setText(""); warnakanSkor(LSkorSpO2,-1); }

        // Oksigen
        int sO2 = hitungSkorOksigen();
        setSkor(LSkorOksigen, sO2);

        // Cek apakah ada parameter score >= 3
        boolean adaScore3 = (sKes>=3||sTD>=3||sNadi>=3||sRR>=3||sSuhu>=3||sSpO2>=3);

        int total = sKes + sTD + sNadi + sRR + sSuhu + sSpO2 + sO2;
        LTotalEWS.setText(String.valueOf(total));

        // Kategori dan warna total
        String kat;
        Color bgTotal;
        if(total >= 7) {
            kat="≥7: LAPORKAN TIM CODE BLUE - Stabilisasi & Rujuk Intermediate Care";
            bgTotal=new Color(255,80,80); LTotalEWS.setForeground(Color.WHITE); LTotalEWS.setBackground(bgTotal);
        } else if(total >= 5 || adaScore3) {
            kat="5-6 atau 3 dalam 1 parameter: Laporkan ke Dokter Jaga/DPJP - Monitoring min. tiap 1 jam";
            bgTotal=new Color(255,160,0); LTotalEWS.setForeground(Color.BLACK); LTotalEWS.setBackground(bgTotal);
        } else if(total >= 1) {
            kat="1-4: Informasikan ke Ketua Tim/PJ Jaga - Monitoring min. tiap 4-6 jam";
            bgTotal=new Color(255,255,100); LTotalEWS.setForeground(Color.BLACK); LTotalEWS.setBackground(bgTotal);
        } else {
            kat="0: Lanjutkan observasi/monitoring rutin min. tiap 12 jam";
            bgTotal=new Color(180,255,180); LTotalEWS.setForeground(new Color(0,100,0)); LTotalEWS.setBackground(bgTotal);
        }
        LKategori.setText("  "+kat);
        return total;
    }

    private int parseIntSafe(String s) {
        try { return Integer.parseInt(s.trim()); } catch(Exception e){ return 0; }
    }
    private double parseDoubleSafe(String s) {
        try { return Double.parseDouble(s.trim().replace(",",".")); } catch(Exception e){ return 0; }
    }

    private void isForm(){
        if(ChkInput.isSelected()){
            ChkInput.setVisible(false);PanelInput.setPreferredSize(new Dimension(WIDTH,490));FormInput.setVisible(true);ChkInput.setVisible(true);
        }else{
            ChkInput.setVisible(false);PanelInput.setPreferredSize(new Dimension(WIDTH,20));FormInput.setVisible(false);ChkInput.setVisible(true);
        }
    }

    public void tampil(){
        Valid.tabelKosong(tabMode);
        try{
            ps=koneksi.prepareStatement(
                "select e.no_rawat,r.no_rkm_medis,p.nm_pasien,e.tanggal,"+
                "e.tingkat_kesadaran,e.td_sistolik,e.nadi,e.pernafasan,e.suhu,e.spo2,e.oksigen,"+
                "e.total_ews,e.kategori,pt.nama as nm_petugas "+
                "from ews_monitoring_rscb e "+
                "inner join reg_periksa r on e.no_rawat=r.no_rawat "+
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "+
                "left join petugas pt on e.nip=pt.nip "+
                "where e.tanggal between ? and ? "+
                "and (e.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) "+
                "order by e.tanggal desc");
            ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
            ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
            ps.setString(3,"%"+TCari.getText().trim()+"%");
            ps.setString(4,"%"+TCari.getText().trim()+"%");
            ps.setString(5,"%"+TCari.getText().trim()+"%");
            rs=ps.executeQuery();
            while(rs.next()){
                tabMode.addRow(new String[]{rs.getString(1),rs.getString(2),rs.getString(3),
                    rs.getString(4),rs.getString(5),rs.getString(6),
                    rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),
                    rs.getString(12),rs.getString(13),rs.getString(14)});
            }
            LCount.setText("Record : "+tabMode.getRowCount());
        }catch(Exception e){System.out.println("Notif tampil EWS: "+e);}
        finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
    }

    public void getData(){
        if(tbObat.getSelectedRow()==-1) return;
        int row=tbObat.getSelectedRow();
        TNoRw.setText(tabMode.getValueAt(row,0).toString());
        TNoRM.setText(tabMode.getValueAt(row,1).toString());
        TPasien.setText(tabMode.getValueAt(row,2).toString());

        try{
            ps=koneksi.prepareStatement(
                "select e.*,pt.nama as nm_petugas from ews_monitoring_rscb e "+
                "left join petugas pt on e.nip=pt.nip "+
                "where e.no_rawat=? and e.tanggal=?");
            ps.setString(1,tabMode.getValueAt(row,0).toString());
            ps.setString(2,tabMode.getValueAt(row,3).toString());
            rs=ps.executeQuery();
            if(rs.next()){
                try{TglObservasi.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString("tanggal")));}catch(Exception ex){}
                if(rs.getString("tingkat_kesadaran")!=null) CmbKesadaran.setSelectedItem(rs.getString("tingkat_kesadaran"));
                TTdSistolik.setText(rs.getString("td_sistolik")==null?"":rs.getString("td_sistolik"));
                TNadi.setText(rs.getString("nadi")==null?"":rs.getString("nadi"));
                TPernafasan.setText(rs.getString("pernafasan")==null?"":rs.getString("pernafasan"));
                TSuhu.setText(rs.getString("suhu")==null?"":rs.getString("suhu"));
                TSpO2.setText(rs.getString("spo2")==null?"":rs.getString("spo2"));
                if(rs.getString("oksigen")!=null) CmbOksigen.setSelectedItem(rs.getString("oksigen"));
                NIP.setText(rs.getString("nip")==null?"":rs.getString("nip"));
                NamaPetugas.setText(rs.getString("nm_petugas")==null?"":rs.getString("nm_petugas"));
                hitungSemua();
            }
        }catch(Exception e){System.out.println("Notif getData EWS: "+e);}
        finally{try{if(rs!=null)rs.close();if(ps!=null)ps.close();}catch(Exception e){}}
    }

    public void emptTeks(){
        TglObservasi.setDate(new Date());
        CmbKesadaran.setSelectedIndex(0);
        TTdSistolik.setText(""); TNadi.setText(""); TPernafasan.setText("");
        TSuhu.setText(""); TSpO2.setText(""); CmbOksigen.setSelectedIndex(0);
        NIP.setText(""); NamaPetugas.setText("");
        hitungSemua();
    }

    private void cetakReport() {
        if(TNoRw.getText().trim().isEmpty()){JOptionPane.showMessageDialog(null,"Pilih data dulu...!!!!");return;}
        java.util.Map<String,Object> p=new java.util.HashMap<>();
        p.put("namars",akses.getnamars());p.put("alamatrs",akses.getalamatrs());
        p.put("kotars",akses.getkabupatenrs());p.put("propinsirs",akses.getpropinsirs());
        p.put("kontakrs",akses.getkontakrs());p.put("emailrs",akses.getemailrs());
        p.put("logo",Sequel.cariGambar("select setting.logo from setting"));
        p.put("no_rawat",TNoRw.getText());
        try {
            String urlQr = "http://"+fungsi.koneksiDB.HOSTHYBRIDWEB()+":"+fungsi.koneksiDB.PORTWEB()+"/"+fungsi.koneksiDB.HYBRIDWEB()+"/penggajian/generateqrcodepetugas.php?kodepetugas="+NIP.getText().trim().replace(" ","_");
            fungsi.koneksiDB.HOSTHYBRIDWEB(); // ensure initialized
            java.net.URL urlObj = new java.net.URL(urlQr);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) urlObj.openConnection();
            conn.setConnectTimeout(3000); conn.setReadTimeout(3000);
            p.put("qrcode", new java.io.ByteArrayInputStream(conn.getInputStream().readAllBytes()));
        } catch(Exception ex){ p.put("qrcode", new java.io.ByteArrayInputStream(new byte[0])); }
        Valid.MyReportqry("rptFormEWSRscb.jasper","report","::[ Form Early Warning Score (EWS) ]::",
            "select e.no_rawat,r.no_rkm_medis,p.nm_pasien,p.tgl_lahir,p.jk," +
            "e.tanggal,e.tingkat_kesadaran,e.skor_kesadaran," +
            "e.td_sistolik,e.skor_td,e.nadi,e.skor_nadi," +
            "e.pernafasan,e.skor_pernafasan,e.suhu,e.skor_suhu," +
            "e.spo2,e.skor_spo2,e.oksigen,e.skor_oksigen," +
            "e.total_ews,e.kategori,pt.nama as nm_petugas " +
            "from ews_monitoring_rscb e " +
            "inner join reg_periksa r on e.no_rawat=r.no_rawat " +
            "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
            "left join petugas pt on e.nip=pt.nip " +
            "where e.no_rawat='"+TNoRw.getText()+"' " +
            "order by e.tanggal",p);
    }

    public void setNoRm(String n, String m, String p){
        TNoRw.setText(n); TNoRM.setText(m); TPasien.setText(p);
    }

    public void isCek(){
        BtnSimpan.setEnabled(akses.getrujukan_keluar());
        BtnHapus.setEnabled(akses.getrujukan_keluar());
        BtnEdit.setEnabled(akses.getrujukan_keluar());
        BtnPrint.setEnabled(akses.getrujukan_keluar());
    }
}
