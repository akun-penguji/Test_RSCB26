/*
 * Kontribusi RSUD Cabangbungin
 */

package rekammedis;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariPetugas;

/**
 *
 * @author perpustakaan
 */
public final class RMPemantauanCURB65 extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i=0;    
    private DlgCariPetugas petugas=new DlgCariPetugas(null,false);
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JMenuItem MnCetak;
    
    public RMPemantauanCURB65(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);
        setSize(628,674);

        tabMode=new DefaultTableModel(null,new Object[]{
            "No.Rawat","No.R.M.","Nama Pasien","Umur","JK","Tanggal",
            "Confusion","Skor C","BUN > 19 mg/dL","Skor U","RR >= 30","Skor R",
            "BP < 90/60","Skor B","Usia >= 65","Skor 65","Total Skor","Kategori","NIP","Petugas"
        }){
            @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbObat.setModel(tabMode);
        tbObat.setComponentPopupMenu(jPopupMenu1);

        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 20; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                 column.setPreferredWidth(105);
            }else if(i==1){
                 column.setPreferredWidth(65);
            }else if(i==2){
                 column.setPreferredWidth(160);
            }else if(i==3){
                 column.setPreferredWidth(35);
            }else if(i==4){
                 column.setPreferredWidth(20);
            }else if(i==5){
                 column.setPreferredWidth(120);
            }else if(i==6){
                 column.setPreferredWidth(80);
            }else if(i==7){
                 column.setPreferredWidth(40);
            }else if(i==8){
                 column.setPreferredWidth(80);
            }else if(i==9){
                 column.setPreferredWidth(40);
            }else if(i==10){
                 column.setPreferredWidth(80);
            }else if(i==11){
                 column.setPreferredWidth(40);
            }else if(i==12){
                 column.setPreferredWidth(80);
            }else if(i==13){
                 column.setPreferredWidth(40);
            }else if(i==14){
                 column.setPreferredWidth(80);
            }else if(i==15){
                 column.setPreferredWidth(40);
            }else if(i==16){
                 column.setPreferredWidth(60);
            }else if(i==17){
                 column.setPreferredWidth(200);
            }else if(i==18){
                 column.setPreferredWidth(80);
            }else if(i==19){
                 column.setPreferredWidth(150);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());
        
        TNoRw.setDocument(new batasInput((byte)17).getKata(TNoRw));
        KdPetugas.setDocument(new batasInput((byte)20).getKata(KdPetugas));
        TCari.setDocument(new batasInput((int)100).getKata(TCari));
        
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
            });
        }
        
        petugas.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(petugas.getTable().getSelectedRow()!= -1){                   
                    KdPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                    NmPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                }  
                KdPetugas.requestFocus();
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        }); 
        
        ChkInput.setSelected(false);
        isForm();
        jam();
    }

    // ... initComponents will be inserted here ...
    private void initComponents() {
        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnCetak = new javax.swing.JMenuItem();
        
        jPopupMenu1.setName("jPopupMenu1"); 
        MnCetak.setBackground(new java.awt.Color(255, 255, 254));
        MnCetak.setFont(new java.awt.Font("Tahoma", 0, 11)); 
        MnCetak.setForeground(new java.awt.Color(50, 50, 50));
        MnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); 
        MnCetak.setText("Cetak Data");
        MnCetak.setName("MnCetak"); 
        MnCetak.setPreferredSize(new java.awt.Dimension(200, 26));
        MnCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cetakSurat();
            }
        });
        jPopupMenu1.add(MnCetak);

        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel19 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        PanelInput = new javax.swing.JPanel();
        FormInput = new widget.PanelBiasa();
        jLabel4 = new widget.Label();
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        Tanggal = new widget.Tanggal();
        TNoRM = new widget.TextBox();
        jLabel16 = new widget.Label();
        Jam = new widget.ComboBox();
        Menit = new widget.ComboBox();
        Detik = new widget.ComboBox();
        ChkKejadian = new widget.CekBox();
        jLabel18 = new widget.Label();
        NmPetugas = new widget.TextBox();
        KdPetugas = new widget.TextBox();
        btnPetugas = new widget.Button();
        
        jLabelConfusion = new widget.Label();
        cmbConfusion = new widget.ComboBox();
        SkorConfusion = new widget.TextBox();
        
        jLabelBUN = new widget.Label();
        cmbBUN = new widget.ComboBox();
        SkorBUN = new widget.TextBox();
        
        jLabelRR = new widget.Label();
        cmbRR = new widget.ComboBox();
        SkorRR = new widget.TextBox();
        
        jLabelBP = new widget.Label();
        cmbBP = new widget.ComboBox();
        SkorBP = new widget.TextBox();
        
        jLabelUsia = new widget.Label();
        cmbUsia = new widget.ComboBox();
        SkorUsia = new widget.TextBox();
        
        jLabelTotal = new widget.Label();
        TotalSkor = new widget.TextBox();
        Kategori = new widget.TextBox();
        
        ChkInput = new widget.CekBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Pemantauan CURB-65 ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbObatKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbObat);
        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnSimpan);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Baru");
        BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBatalActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnBatal);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnHapus);

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnEdit.setMnemonic('G');
        BtnEdit.setText("Ganti");
        BtnEdit.setToolTipText("Alt+G");
        BtnEdit.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEditActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnEdit);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrintActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnPrint);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnKeluar);

        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);
        
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));
        jLabel19.setText("Tanggal :");
        jLabel19.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel19);
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari1);
        jLabel21.setText("s.d.");
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel21);
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari2);
        jLabel6.setText("Key Word :");
        jLabel6.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(jLabel6);
        TCari.setPreferredSize(new java.awt.Dimension(315, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if(evt.getKeyCode()==KeyEvent.VK_ENTER){
                    BtnCariActionPerformed(null);
                }
            }
        });
        panelGlass9.add(TCari);
        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        panelGlass9.add(BtnCari);
        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllActionPerformed(evt);
            }
        });
        panelGlass9.add(BtnAll);
        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);
        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));
        FormInput.setLayout(null);
        
        jLabel4.setText("No.Rawat :");
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 10, 75, 23);
        FormInput.add(TNoRw);
        TNoRw.setBounds(79, 10, 141, 23);
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
                    isRawat();
                }else{            
                    Valid.pindah(evt,TCari,Tanggal);
                }
            }
        });
        
        FormInput.add(TNoRM);
        TNoRM.setBounds(222, 10, 112, 23);
        FormInput.add(TPasien);
        TPasien.setBounds(336, 10, 285, 23);
        
        jLabel16.setText("Tanggal :");
        FormInput.add(jLabel16);
        jLabel16.setBounds(0, 40, 75, 23);
        FormInput.add(Tanggal);
        Tanggal.setBounds(79, 40, 90, 23);
        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "01-06-2023" }));
        Tanggal.setDisplayFormat("dd-MM-yyyy");

        Jam.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        FormInput.add(Jam);
        Jam.setBounds(173, 40, 62, 23);
        Menit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        FormInput.add(Menit);
        Menit.setBounds(238, 40, 62, 23);
        Detik.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        FormInput.add(Detik);
        Detik.setBounds(303, 40, 62, 23);
        
        ChkKejadian.setSelected(true);
        FormInput.add(ChkKejadian);
        ChkKejadian.setBounds(368, 40, 23, 23);
        
        jLabel18.setText("Petugas :");
        FormInput.add(jLabel18);
        jLabel18.setBounds(400, 40, 70, 23);
        FormInput.add(KdPetugas);
        KdPetugas.setBounds(474, 40, 94, 23);
        FormInput.add(NmPetugas);
        NmPetugas.setBounds(571, 40, 187, 23);
        btnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        FormInput.add(btnPetugas);
        btnPetugas.setBounds(761, 40, 28, 23);
        btnPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                petugas.emptTeks();
                petugas.isCek();
                petugas.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
                petugas.setLocationRelativeTo(internalFrame1);
                petugas.setVisible(true);
            }
        });

        // CURB-65 Components
        jLabelConfusion.setText("Confusion :");
        FormInput.add(jLabelConfusion);
        jLabelConfusion.setBounds(0, 70, 150, 23);
        cmbConfusion.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        FormInput.add(cmbConfusion);
        cmbConfusion.setBounds(154, 70, 80, 23);
        FormInput.add(SkorConfusion);
        SkorConfusion.setBounds(238, 70, 40, 23);
        SkorConfusion.setEditable(false);
        cmbConfusion.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                hitungSkor();
            }
        });

        jLabelBUN.setText("BUN > 19 mg/dL :");
        FormInput.add(jLabelBUN);
        jLabelBUN.setBounds(0, 100, 150, 23);
        cmbBUN.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        FormInput.add(cmbBUN);
        cmbBUN.setBounds(154, 100, 80, 23);
        FormInput.add(SkorBUN);
        SkorBUN.setBounds(238, 100, 40, 23);
        SkorBUN.setEditable(false);
        cmbBUN.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                hitungSkor();
            }
        });

        jLabelRR.setText("RR >= 30 x/m :");
        FormInput.add(jLabelRR);
        jLabelRR.setBounds(0, 130, 150, 23);
        cmbRR.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        FormInput.add(cmbRR);
        cmbRR.setBounds(154, 130, 80, 23);
        FormInput.add(SkorRR);
        SkorRR.setBounds(238, 130, 40, 23);
        SkorRR.setEditable(false);
        cmbRR.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                hitungSkor();
            }
        });

        jLabelBP.setText("BP < 90/60 mmHg :");
        FormInput.add(jLabelBP);
        jLabelBP.setBounds(300, 70, 130, 23);
        cmbBP.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        FormInput.add(cmbBP);
        cmbBP.setBounds(434, 70, 80, 23);
        FormInput.add(SkorBP);
        SkorBP.setBounds(518, 70, 40, 23);
        SkorBP.setEditable(false);
        cmbBP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                hitungSkor();
            }
        });

        jLabelUsia.setText("Usia >= 65 Th :");
        FormInput.add(jLabelUsia);
        jLabelUsia.setBounds(300, 100, 130, 23);
        cmbUsia.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        FormInput.add(cmbUsia);
        cmbUsia.setBounds(434, 100, 80, 23);
        FormInput.add(SkorUsia);
        SkorUsia.setBounds(518, 100, 40, 23);
        SkorUsia.setEditable(false);
        cmbUsia.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                hitungSkor();
            }
        });

        jLabelTotal.setText("Total Skor :");
        FormInput.add(jLabelTotal);
        jLabelTotal.setBounds(300, 130, 130, 23);
        FormInput.add(TotalSkor);
        TotalSkor.setBounds(434, 130, 40, 23);
        TotalSkor.setEditable(false);
        
        FormInput.add(Kategori);
        Kategori.setBounds(79, 160, 710, 23);
        Kategori.setEditable(false);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);
        
        ChkInput.setText(".: Input Data");
        ChkInput.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isForm();
            }
        });
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);
        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
        pack();
    }
    
    private void hitungSkor() {
        int skor = 0;
        if(cmbConfusion.getSelectedIndex()==1){
            SkorConfusion.setText("1");
            skor++;
        }else{
            SkorConfusion.setText("0");
        }
        
        if(cmbBUN.getSelectedIndex()==1){
            SkorBUN.setText("1");
            skor++;
        }else{
            SkorBUN.setText("0");
        }
        
        if(cmbRR.getSelectedIndex()==1){
            SkorRR.setText("1");
            skor++;
        }else{
            SkorRR.setText("0");
        }
        
        if(cmbBP.getSelectedIndex()==1){
            SkorBP.setText("1");
            skor++;
        }else{
            SkorBP.setText("0");
        }
        
        if(cmbUsia.getSelectedIndex()==1){
            SkorUsia.setText("1");
            skor++;
        }else{
            SkorUsia.setText("0");
        }
        
        TotalSkor.setText(String.valueOf(skor));
        
        if(skor==0 || skor==1){
            Kategori.setText("Resiko Rendah (Mortalitas 0.6% - 2.7%). Perawatan yang disarankan: Rawat Jalan.");
        }else if(skor==2){
            Kategori.setText("Resiko Sedang (Mortalitas 6.8%). Perawatan yang disarankan: Rawat Inap Singkat / Rawat Jalan dengan Pengawasan.");
        }else if(skor>=3){
            if(skor==3){
                 Kategori.setText("Resiko Sedang-Berat (Mortalitas 14.0%). Perawatan yang disarankan: Rawat Inap.");
            }else{
                 Kategori.setText("Resiko Berat (Mortalitas 27.8%). Perawatan yang disarankan: Rawat ICU.");
            }
        }
    }

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
        if(TNoRw.getText().trim().equals("")||TPasien.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"pasien");
        }else if(KdPetugas.getText().trim().equals("")||NmPetugas.getText().trim().equals("")){
            Valid.textKosong(KdPetugas,"Petugas");
        }else{
            if(Sequel.menyimpantf("pemantauan_curb65","?,?,?,?,?,?,?,?,?","Data",9,new String[]{
                TNoRw.getText(),Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem(),
                SkorConfusion.getText(),SkorBUN.getText(),SkorRR.getText(),SkorBP.getText(),SkorUsia.getText(),
                TotalSkor.getText(),KdPetugas.getText()
            })==true){
                tampil();
                emptTeks();
            }
        }
    }

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {
        emptTeks();
        ChkInput.setSelected(true);
        isForm(); 
    }

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {
        if(tbObat.getSelectedRow()>-1){
            if(akses.getkode().equals("Admin Utama")){
                hapus();
            }else{
                if(KdPetugas.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(),18).toString())){
                    hapus();
                }else{
                    JOptionPane.showMessageDialog(null,"Hanya bisa dihapus oleh petugas yang bersangkutan..!!");
                }
            }
        }else{
            JOptionPane.showMessageDialog(rootPane,"Silahkan anda pilih data terlebih dahulu..!!");
        }
    }

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {
        if(TNoRw.getText().trim().equals("")||TPasien.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"pasien");
        }else{
            if(tbObat.getSelectedRow()>-1){
                if(akses.getkode().equals("Admin Utama")){
                    ganti();
                }else{
                    if(KdPetugas.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(),18).toString())){
                        ganti();
                    }else{
                        JOptionPane.showMessageDialog(null,"Hanya bisa diganti oleh petugas yang bersangkutan..!!");
                    }
                }
            }else{
                JOptionPane.showMessageDialog(rootPane,"Silahkan anda pilih data terlebih dahulu..!!");
            }
        }
    }

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        }else if(tabMode.getRowCount()!=0){
            Map<String, Object> param = new HashMap<>(); 
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());   
            param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
            Valid.MyReportqry("rptDataCURB65.jasper","report","::[ Data Pemantauan CURB-65 ]::",
                "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,reg_periksa.sttsumur,"+
                "pasien.jk,pemantauan_curb65.tanggal,pemantauan_curb65.skor_confusion,pemantauan_curb65.skor_bun,"+
                "pemantauan_curb65.skor_rr,pemantauan_curb65.skor_bp,pemantauan_curb65.skor_usia,pemantauan_curb65.total_skor,"+
                "pemantauan_curb65.nip,petugas.nama "+
                "from pemantauan_curb65 inner join reg_periksa on pemantauan_curb65.no_rawat=reg_periksa.no_rawat "+
                "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                "inner join petugas on pemantauan_curb65.nip=petugas.nip where "+
                "pemantauan_curb65.tanggal between '"+Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00' and '"+Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59' order by pemantauan_curb65.tanggal ",param);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {
        tampil();
    }

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {
        TCari.setText("");
        tampil();
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try{
            ps=koneksi.prepareStatement(
                "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,reg_periksa.sttsumur,"+
                "pasien.jk,pemantauan_curb65.tanggal,pemantauan_curb65.skor_confusion,pemantauan_curb65.skor_bun,"+
                "pemantauan_curb65.skor_rr,pemantauan_curb65.skor_bp,pemantauan_curb65.skor_usia,pemantauan_curb65.total_skor,"+
                "pemantauan_curb65.nip,petugas.nama "+
                "from pemantauan_curb65 inner join reg_periksa on pemantauan_curb65.no_rawat=reg_periksa.no_rawat "+
                "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                "inner join petugas on pemantauan_curb65.nip=petugas.nip where "+
                "pemantauan_curb65.tanggal between ? and ? and (reg_periksa.no_rawat like ? or pasien.no_rkm_medis like ? or pasien.nm_pasien like ?) "+
                "order by pemantauan_curb65.tanggal");
            try {
                ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
                ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
                ps.setString(3,"%"+TCari.getText()+"%");
                ps.setString(4,"%"+TCari.getText()+"%");
                ps.setString(5,"%"+TCari.getText()+"%");
                rs=ps.executeQuery();
                while(rs.next()){
                    String kategori = "";
                    int skor = rs.getInt("total_skor");
                    if(skor==0 || skor==1){
                        kategori = "Resiko Rendah";
                    }else if(skor==2){
                        kategori = "Resiko Sedang";
                    }else if(skor>=3){
                        if(skor==3){
                             kategori = "Resiko Sedang-Berat";
                        }else{
                             kategori = "Resiko Berat";
                        }
                    }
                    
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),
                        rs.getString("umurdaftar")+" "+rs.getString("sttsumur"),rs.getString("jk"),
                        rs.getString("tanggal"),
                        rs.getString("skor_confusion").equals("1")?"Ya":"Tidak",rs.getString("skor_confusion"),
                        rs.getString("skor_bun").equals("1")?"Ya":"Tidak",rs.getString("skor_bun"),
                        rs.getString("skor_rr").equals("1")?"Ya":"Tidak",rs.getString("skor_rr"),
                        rs.getString("skor_bp").equals("1")?"Ya":"Tidak",rs.getString("skor_bp"),
                        rs.getString("skor_usia").equals("1")?"Ya":"Tidak",rs.getString("skor_usia"),
                        rs.getString("total_skor"),kategori,rs.getString("nip"),rs.getString("nama")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif : "+e);
            } finally{
                if(rs!=null){ rs.close(); }
                if(ps!=null){ ps.close(); }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
    }

    public void emptTeks() {
        Tanggal.setDate(new Date());
        cmbConfusion.setSelectedIndex(0);
        cmbBUN.setSelectedIndex(0);
        cmbRR.setSelectedIndex(0);
        cmbBP.setSelectedIndex(0);
        cmbUsia.setSelectedIndex(0);
        hitungSkor();
        cmbConfusion.requestFocus();
    }

    private void isRawat() {
        Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat=? ",TNoRM,TNoRw.getText());
        Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=? ",TPasien,TNoRM.getText());
    }

    private void isForm(){
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,265));
            FormInput.setVisible(true);      
            ChkInput.setVisible(true);
        }else if(ChkInput.isSelected()==false){           
            ChkInput.setVisible(false);            
            PanelInput.setPreferredSize(new Dimension(WIDTH,20));
            FormInput.setVisible(false);      
            ChkInput.setVisible(true);
        }
    }
    
    private void jam(){
        ActionListener taskPerformer = new ActionListener(){
            private int nilai_jam;
            private int nilai_menit;
            private int nilai_detik;
            public void actionPerformed(ActionEvent e) {
                String nol_jam = "";
                String nol_menit = "";
                String nol_detik = "";
                Date now = Calendar.getInstance().getTime();
                if(ChkKejadian.isSelected()==true){
                    nilai_jam = now.getHours();
                    nilai_menit = now.getMinutes();
                    nilai_detik = now.getSeconds();
                }else if(ChkKejadian.isSelected()==false){
                    nilai_jam =Jam.getSelectedIndex();
                    nilai_menit =Menit.getSelectedIndex();
                    nilai_detik =Detik.getSelectedIndex();
                }
                if (nilai_jam <= 9) { nol_jam = "0"; }
                if (nilai_menit <= 9) { nol_menit = "0"; }
                if (nilai_detik <= 9) { nol_detik = "0"; }
                String jam = nol_jam + Integer.toString(nilai_jam);
                String menit = nol_menit + Integer.toString(nilai_menit);
                String detik = nol_detik + Integer.toString(nilai_detik);
                Jam.setSelectedItem(jam);
                Menit.setSelectedItem(menit);
                Detik.setSelectedItem(detik);
            }
        };
        new Timer(1000, taskPerformer).start();
    }

    private void hapus() {
        if(Sequel.queryu2tf("delete from pemantauan_curb65 where tanggal=? and no_rawat=?",2,new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(),5).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()
        })==true){
            tampil();
            emptTeks();
        }else{
            JOptionPane.showMessageDialog(null,"Gagal menghapus..!!");
        }
    }

    private void ganti() {
        if(Sequel.mengedittf("pemantauan_curb65","tanggal=? and no_rawat=?","no_rawat=?,tanggal=?,skor_confusion=?,skor_bun=?,skor_rr=?,skor_bp=?,skor_usia=?,total_skor=?,nip=?",11,new String[]{
            TNoRw.getText(),Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem(),
            SkorConfusion.getText(),SkorBUN.getText(),SkorRR.getText(),SkorBP.getText(),SkorUsia.getText(),
            TotalSkor.getText(),KdPetugas.getText(),
            tbObat.getValueAt(tbObat.getSelectedRow(),5).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()
        })==true){
            tampil();
            emptTeks();
        }
    }

    private void cetakSurat() {
        if(tbObat.getSelectedRow()!= -1){
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> param = new HashMap<>();
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());
            param.put("logo",Sequel.cariGambar("select setting.logo from setting"));
            param.put("no_rawat",tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
            param.put("tanggal",tbObat.getValueAt(tbObat.getSelectedRow(),5).toString());  
            Valid.MyReport("rptSuratCURB65.jasper","report","::[ Surat Hasil Pemantauan Skor CURB-65 ]::",param);
            this.setCursor(Cursor.getDefaultCursor());
        }else{
            JOptionPane.showMessageDialog(null,"Maaf, silahkan pilih data terlebih dahulu..!!");
        }
    }

    private void getData() {
        if(tbObat.getSelectedRow()!= -1){
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            
            // Confusion
            cmbConfusion.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString());
            SkorConfusion.setText(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString());
            
            // BUN
            cmbBUN.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),8).toString());
            SkorBUN.setText(tbObat.getValueAt(tbObat.getSelectedRow(),9).toString());
            
            // Respiratory Rate
            cmbRR.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),10).toString());
            SkorRR.setText(tbObat.getValueAt(tbObat.getSelectedRow(),11).toString());
            
            // Blood Pressure
            cmbBP.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),12).toString());
            SkorBP.setText(tbObat.getValueAt(tbObat.getSelectedRow(),13).toString());
            
            // Usia
            cmbUsia.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),14).toString());
            SkorUsia.setText(tbObat.getValueAt(tbObat.getSelectedRow(),15).toString());
            
            // Total Skor dan Kategori
            TotalSkor.setText(tbObat.getValueAt(tbObat.getSelectedRow(),16).toString());
            Kategori.setText(tbObat.getValueAt(tbObat.getSelectedRow(),17).toString());
            
            // Petugas
            KdPetugas.setText(tbObat.getValueAt(tbObat.getSelectedRow(),18).toString());
            NmPetugas.setText(tbObat.getValueAt(tbObat.getSelectedRow(),19).toString());
            
            // Tanggal dan Jam
            Valid.SetTgl(Tanggal,tbObat.getValueAt(tbObat.getSelectedRow(),5).toString());
            Jam.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString().substring(11,13));
            Menit.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString().substring(14,16));
            Detik.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString().substring(17,19));
        }
    }

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {
        if(tabMode.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {
        if(tabMode.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }
    
    public void isCek(){
        BtnSimpan.setEnabled(akses.getpemantauan_pews_dewasa()); // Using existing access control
        BtnHapus.setEnabled(akses.getpemantauan_pews_dewasa());
        BtnEdit.setEnabled(akses.getpemantauan_pews_dewasa());
        BtnPrint.setEnabled(akses.getpemantauan_pews_dewasa()); 
        if(akses.getjml2()>=1){
            KdPetugas.setEditable(false);
            btnPetugas.setEnabled(false);
            KdPetugas.setText(akses.getkode());
            NmPetugas.setText(petugas.tampil3(KdPetugas.getText()));
            if(NmPetugas.getText().equals("")){
                KdPetugas.setText("");
                JOptionPane.showMessageDialog(null,"User login tidak ada dalam data petugas, silahkan hubungi administrator, data tidak dapat disimpan..!!");
            }
        }
    }
    
    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        DTPCari2.setDate(tgl2);    
        isRawat(); 
        ChkInput.setSelected(true);
        isForm();
    }
    
    // Variables declaration
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.CekBox ChkKejadian;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.ComboBox Detik;
    private widget.PanelBiasa FormInput;
    private widget.ComboBox Jam;
    private widget.TextBox KdPetugas;
    private widget.ComboBox Menit;
    private widget.TextBox NmPetugas;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Tanggal Tanggal;
    private widget.Button btnPetugas;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel16;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel4;
    private widget.Label jLabel6;
    private javax.swing.JPanel jPanel3;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbObat;
    
    private widget.Label jLabelConfusion;
    private widget.ComboBox cmbConfusion;
    private widget.TextBox SkorConfusion;
    private widget.Label jLabelBUN;
    private widget.ComboBox cmbBUN;
    private widget.TextBox SkorBUN;
    private widget.Label jLabelRR;
    private widget.ComboBox cmbRR;
    private widget.TextBox SkorRR;
    private widget.Label jLabelBP;
    private widget.ComboBox cmbBP;
    private widget.TextBox SkorBP;
    private widget.Label jLabelUsia;
    private widget.ComboBox cmbUsia;
    private widget.TextBox SkorUsia;
    private widget.Label jLabelTotal;
    private widget.TextBox TotalSkor;
    private widget.TextBox Kategori;
}
