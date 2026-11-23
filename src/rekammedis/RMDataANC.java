package rekammedis;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariDokter;
import laporan.DlgCariPenyakit;
import laporan.DlgCariPenyakit;

/**
 * Form Data ANC (Antenatal Care)
 * Mapping table: data_anc
 */
public class RMDataANC extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i=0;
    private DlgCariDokter dokter=new DlgCariDokter(null,false);
    private DlgCariPenyakit penyakit=new DlgCariPenyakit(null,false);
    
    public RMDataANC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        tabMode=new DefaultTableModel(null,new Object[]{
            "No.Rawat","No.RM","Nama Pasien","Tgl.Kunj","Ke","HPHT","HPL","Usia Hamil",
            "Keluhan","TD","Nadi","Suhu","RR","TB","BB","LILA","TFU","DJJ","Letak",
            "Diagnosa","Dokter"
        }){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 21; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0) column.setPreferredWidth(105);
            else if(i==1) column.setPreferredWidth(70);
            else if(i==2) column.setPreferredWidth(150);
            else if(i==3) column.setPreferredWidth(110); // Tgl Kunj
            else if(i==4) column.setPreferredWidth(30);  // Ke
            else if(i==5) column.setPreferredWidth(70);  // HPHT
            else if(i==6) column.setPreferredWidth(70);  // HPL
            else column.setPreferredWidth(80);
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
        TNoRw.setDocument(new batasInput((byte)17).getKata(TNoRw));
        KunjunganKe.setDocument(new batasInput((byte)3).getOnlyAngka(KunjunganKe));
        Keluhan.setDocument(new batasInput((int)400).getKata(Keluhan));
        TDSistole.setDocument(new batasInput((byte)5).getKata(TDSistole));
        TDDiastole.setDocument(new batasInput((byte)5).getKata(TDDiastole));
        Nadi.setDocument(new batasInput((byte)5).getKata(Nadi));
        Suhu.setDocument(new batasInput((byte)5).getKata(Suhu));
        Nafas.setDocument(new batasInput((byte)5).getKata(Nafas));
        TB.setDocument(new batasInput((byte)5).getKata(TB));
        BB.setDocument(new batasInput((byte)5).getKata(BB));
        LILA.setDocument(new batasInput((byte)5).getKata(LILA));
        TFU.setDocument(new batasInput((byte)5).getKata(TFU));
        DJJ.setDocument(new batasInput((byte)5).getKata(DJJ));
        LetakJanin.setDocument(new batasInput((byte)50).getKata(LetakJanin));
        KetEdema.setDocument(new batasInput((byte)100).getKata(KetEdema));
        
        // Initialize Edema ComboBox
        Edema.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak Ada", "Ada" }));
        
        // Set input limits for TextAreas
        PemeriksaanFisik.setDocument(new batasInput(2000).getKata(PemeriksaanFisik));
        PemeriksaanLab.setDocument(new batasInput(2000).getKata(PemeriksaanLab));
        Diagnosis.setDocument(new batasInput(2000).getKata(Diagnosis));
        Tatalaksana.setDocument(new batasInput(2000).getKata(Tatalaksana));
        
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override public void insertUpdate(DocumentEvent e) {if(TCari.getText().length()>2) tampil();}
                @Override public void removeUpdate(DocumentEvent e) {if(TCari.getText().length()>2) tampil();}
                @Override public void changedUpdate(DocumentEvent e) {if(TCari.getText().length()>2) tampil();}
            });
        }
        
        dokter.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override public void windowClosed(WindowEvent e) {
                if(dokter.getTable().getSelectedRow()!= -1){
                    KdDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                    NmDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                }
            }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });
        

        
        HPHT.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                if (HPHT.getDate()!=null){
                     hitungHPL();
                }
            }
        });
    }

    private void hitungHPL(){
        if(HPHT.getDate()!=null){
            // Naegele's Rule: +7 days, -3 months (or +9 months), +1 year (if needed)
            // Simplified: Add 280 days (40 weeks)
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(HPHT.getDate());
            cal.add(java.util.Calendar.DATE, 280);
            HPL.setDate(cal.getTime());
            
            // Hitung usia kehamilan
            java.util.Calendar today = java.util.Calendar.getInstance();
            java.util.Calendar start = java.util.Calendar.getInstance();
            start.setTime(HPHT.getDate());
            long diff = today.getTimeInMillis() - start.getTimeInMillis();
            long days = diff / (24 * 60 * 60 * 1000);
            long weeks = days / 7;
            long leftDays = days % 7;
            UsiaKehamilan.setText(weeks+" Mg "+leftDays+" Hr");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        internalFrame1 = new widget.InternalFrame();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnKeluar = new widget.Button();
        ScrollInput = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        jLabel4 = new widget.Label();
        TNoRw = new widget.TextBox();
        TNoRM = new widget.TextBox();
        TPasien = new widget.TextBox();
        jLabel5 = new widget.Label();
        Tanggal = new widget.Tanggal();
        jLabel6 = new widget.Label();
        KunjunganKe = new widget.TextBox();
        jLabel7 = new widget.Label();
        HPHT = new widget.Tanggal();
        jLabel8 = new widget.Label();
        HPL = new widget.Tanggal();
        jLabel9 = new widget.Label();
        UsiaKehamilan = new widget.TextBox();
        jLabel10 = new widget.Label();
        Keluhan = new widget.TextBox();
        jLabel11 = new widget.Label();
        TDSistole = new widget.TextBox();
        jLabel12 = new widget.Label();
        TDDiastole = new widget.TextBox();
        jLabel13 = new widget.Label();
        jLabel14 = new widget.Label();
        Nadi = new widget.TextBox();
        jLabel15 = new widget.Label();
        Suhu = new widget.TextBox();
        jLabel16 = new widget.Label();
        Nafas = new widget.TextBox();
        jLabel17 = new widget.Label();
        TB = new widget.TextBox();
        jLabel18 = new widget.Label();
        BB = new widget.TextBox();
        jLabel19 = new widget.Label();
        LILA = new widget.TextBox();
        jLabel20 = new widget.Label();
        TFU = new widget.TextBox();
        jLabel21 = new widget.Label();
        DJJ = new widget.TextBox();
        jLabel22 = new widget.Label();
        LetakJanin = new widget.TextBox();
        jLabel23 = new widget.Label();
        Edema = new widget.ComboBox();
        KetEdema = new widget.TextBox();
        BtnCariDokter = new widget.Button();
        jLabel26 = new widget.Label();
        ScrollPemeriksaanFisik = new widget.ScrollPane();
        PemeriksaanFisik = new widget.TextArea();
        jLabel27 = new widget.Label();
        ScrollPemeriksaanLab = new widget.ScrollPane();
        PemeriksaanLab = new widget.TextArea();
        jLabel28 = new widget.Label();
        ScrollDiagnosis = new widget.ScrollPane();
        Diagnosis = new widget.TextArea();
        jLabel29 = new widget.Label();
        ScrollTatalaksana = new widget.ScrollPane();
        Tatalaksana = new widget.TextArea();
        jLabel30 = new widget.Label();
        KdDokter = new widget.TextBox();
        NmDokter = new widget.TextBox();
        BtnCariDokter = new widget.Button();
        jLabel25 = new widget.Label();
        
        internalFrame3 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        panelGlass9 = new widget.panelisi();
        jLabel1 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        jLabel2 = new widget.Label();
        LCount = new widget.Label();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data ANC (Antenatal Care) ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); 
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8");
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 54));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); 
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); 
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
        BtnBatal.setName("BtnBatal"); 
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
        BtnHapus.setName("BtnHapus"); 
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
        BtnEdit.setName("BtnEdit"); 
        BtnEdit.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEditActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnEdit);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); 
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar"); 
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnKeluar);

        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        FormInput.setName("FormInput"); 
        FormInput.setLayout(null);

        jLabel4.setText("No.Rawat :");
        jLabel4.setName("jLabel4"); 
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 10, 80, 23);

        TNoRw.setEditable(false);
        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); 
        FormInput.add(TNoRw);
        TNoRw.setBounds(84, 10, 140, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); 
        FormInput.add(TNoRM);
        TNoRM.setBounds(226, 10, 90, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); 
        FormInput.add(TPasien);
        TPasien.setBounds(318, 10, 340, 23);

        jLabel5.setText("Tgl.Kunjungan :");
        jLabel5.setName("jLabel5"); 
        FormInput.add(jLabel5);
        jLabel5.setBounds(0, 40, 80, 23);

        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "25-12-2025 05:00:00" }));
        Tanggal.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        Tanggal.setName("Tanggal"); 
        FormInput.add(Tanggal);
        Tanggal.setBounds(84, 40, 130, 23);

        jLabel6.setText("Kunjungan Ke :");
        jLabel6.setName("jLabel6"); 
        FormInput.add(jLabel6);
        jLabel6.setBounds(220, 40, 80, 23);

        KunjunganKe.setName("KunjunganKe"); 
        FormInput.add(KunjunganKe);
        KunjunganKe.setBounds(304, 40, 40, 23);

        jLabel7.setText("HPHT :");
        jLabel7.setName("jLabel7"); 
        FormInput.add(jLabel7);
        jLabel7.setBounds(350, 40, 50, 23);

        HPHT.setForeground(new java.awt.Color(50, 70, 50));
        HPHT.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "25-12-2025" }));
        HPHT.setDisplayFormat("dd-MM-yyyy");
        HPHT.setName("HPHT"); 
        FormInput.add(HPHT);
        HPHT.setBounds(404, 40, 90, 23);
        
        jLabel8.setText("HPL :");
        jLabel8.setName("jLabel8"); 
        FormInput.add(jLabel8);
        jLabel8.setBounds(500, 40, 40, 23);

        HPL.setForeground(new java.awt.Color(50, 70, 50));
        HPL.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "25-12-2025" }));
        HPL.setDisplayFormat("dd-MM-yyyy");
        HPL.setName("HPL"); 
        FormInput.add(HPL);
        HPL.setBounds(544, 40, 90, 23);

        jLabel9.setText("Usia Hamil :");
        jLabel9.setName("jLabel9"); 
        FormInput.add(jLabel9);
        jLabel9.setBounds(0, 70, 80, 23);

        UsiaKehamilan.setName("UsiaKehamilan"); 
        FormInput.add(UsiaKehamilan);
        UsiaKehamilan.setBounds(84, 70, 130, 23);

        jLabel10.setText("Keluhan :");
        jLabel10.setName("jLabel10"); 
        FormInput.add(jLabel10);
        jLabel10.setBounds(220, 70, 80, 23);

        Keluhan.setName("Keluhan"); 
        FormInput.add(Keluhan);
        Keluhan.setBounds(304, 70, 350, 23);
        
        jLabel11.setText("TD :");
        jLabel11.setName("jLabel11"); 
        FormInput.add(jLabel11);
        jLabel11.setBounds(0, 100, 80, 23);
        
        TDSistole.setName("TDSistole"); 
        FormInput.add(TDSistole);
        TDSistole.setBounds(84, 100, 40, 23);
        
        jLabel12.setText("/");
        jLabel12.setName("jLabel12"); 
        FormInput.add(jLabel12);
        jLabel12.setBounds(124, 100, 10, 23);
        
        TDDiastole.setName("TDDiastole"); 
        FormInput.add(TDDiastole);
        TDDiastole.setBounds(134, 100, 40, 23);
        
        jLabel13.setText("mmHg");
        jLabel13.setName("jLabel13");
        FormInput.add(jLabel13);
        jLabel13.setBounds(176, 100, 35, 23);

        jLabel14.setText("Nadi :");
        jLabel14.setName("jLabel14"); 
        FormInput.add(jLabel14);
        jLabel14.setBounds(220, 100, 80, 23);
        
        Nadi.setName("Nadi"); 
        FormInput.add(Nadi);
        Nadi.setBounds(304, 100, 40, 23);
        
        jLabel15.setText("x/mnt");
        FormInput.add(jLabel15);
        jLabel15.setBounds(346, 100, 35, 23);

        jLabel16.setText("Suhu :");
        jLabel16.setName("jLabel16"); 
        FormInput.add(jLabel16);
        jLabel16.setBounds(390, 100, 50, 23);
        
        Suhu.setName("Suhu"); 
        FormInput.add(Suhu);
        Suhu.setBounds(444, 100, 40, 23);
        
        jLabel17.setText("C");
        FormInput.add(jLabel17);
        jLabel17.setBounds(486, 100, 20, 23);
        
        // Baris berikutnya: TB, BB, LILA
        
        jLabel18.setText("TB :");
        FormInput.add(jLabel18);
        jLabel18.setBounds(0, 130, 80, 23);
        
        TB.setName("TB");
        FormInput.add(TB);
        TB.setBounds(84, 130, 40, 23);
        
        jLabel19.setText("BB :");
        FormInput.add(jLabel19);
        jLabel19.setBounds(140, 130, 40, 23);
        
        BB.setName("BB");
        FormInput.add(BB);
        BB.setBounds(184, 130, 40, 23);
        
        jLabel20.setText("LILA :");
        FormInput.add(jLabel20);
        jLabel20.setBounds(240, 130, 40, 23);
        
        LILA.setName("LILA");
        FormInput.add(LILA);
        LILA.setBounds(284, 130, 40, 23);
        
        jLabel21.setText("TFU :");
        FormInput.add(jLabel21);
        jLabel21.setBounds(340, 130, 40, 23);
        
        TFU.setName("TFU");
        FormInput.add(TFU);
        TFU.setBounds(384, 130, 40, 23);
        
        jLabel22.setText("DJJ :");
        FormInput.add(jLabel22);
        jLabel22.setBounds(440, 130, 40, 23);
        
        DJJ.setName("DJJ");
        FormInput.add(DJJ);
        DJJ.setBounds(484, 130, 40, 23);

        // Diagnosa & Dokter
        
        BtnCariDokter.setBounds(628, 160, 28, 23);

        // Dokter field
        jLabel25.setText("Dokter :");
        FormInput.add(jLabel25);
        jLabel25.setBounds(0, 160, 80, 23);
        
        KdDokter.setEditable(false);
        KdDokter.setName("KdDokter");
        FormInput.add(KdDokter);
        KdDokter.setBounds(84, 160, 60, 23);
        
        NmDokter.setEditable(false);
        NmDokter.setName("NmDokter");
        FormInput.add(NmDokter);
        NmDokter.setBounds(146, 160, 200, 23);
        
        BtnCariDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnCariDokter.setMnemonic('2');
        BtnCariDokter.setName("BtnCariDokter");
        BtnCariDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dokter.isCek();
                dokter.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
                dokter.setLocationRelativeTo(internalFrame1);
                dokter.setVisible(true);
            }
        });
        FormInput.add(BtnCariDokter);
        BtnCariDokter.setBounds(348, 160, 28, 23);

        // Add TextArea fields to FormInput (continuing from previous fields)
        jLabel26.setText("Letak Janin :");
        jLabel26.setName("jLabel26");
        FormInput.add(jLabel26);
        jLabel26.setBounds(0, 190, 120, 23);
        
        FormInput.add(LetakJanin);
        LetakJanin.setBounds(124, 190, 200, 23);
        
        jLabel23.setText("Edema :");
        FormInput.add(jLabel23);
        jLabel23.setBounds(330, 190, 60, 23);
        
        FormInput.add(Edema);
        Edema.setBounds(394, 190, 100, 23);
        
        FormInput.add(KetEdema);
        KetEdema.setBounds(498, 190, 160, 23);

        jLabel27.setText("Pemeriksaan Fisik :");
        jLabel27.setName("jLabel27");
        FormInput.add(jLabel27);
        jLabel27.setBounds(0, 220, 120, 23);

        ScrollPemeriksaanFisik.setName("ScrollPemeriksaanFisik");
        PemeriksaanFisik.setColumns(20);
        PemeriksaanFisik.setRows(3);
        PemeriksaanFisik.setName("PemeriksaanFisik");
        ScrollPemeriksaanFisik.setViewportView(PemeriksaanFisik);
        FormInput.add(ScrollPemeriksaanFisik);
        ScrollPemeriksaanFisik.setBounds(124, 220, 530, 60);

        jLabel28.setText("Pemeriksaan Lab :");
        jLabel28.setName("jLabel28");
        FormInput.add(jLabel28);
        jLabel28.setBounds(0, 285, 120, 23);

        ScrollPemeriksaanLab.setName("ScrollPemeriksaanLab");
        PemeriksaanLab.setColumns(20);
        PemeriksaanLab.setRows(3);
        PemeriksaanLab.setName("PemeriksaanLab");
        ScrollPemeriksaanLab.setViewportView(PemeriksaanLab);
        FormInput.add(ScrollPemeriksaanLab);
        ScrollPemeriksaanLab.setBounds(124, 285, 530, 60);

        jLabel29.setText("Diagnosis :");
        jLabel29.setName("jLabel29");
        FormInput.add(jLabel29);
        jLabel29.setBounds(0, 350, 120, 23);

        ScrollDiagnosis.setName("ScrollDiagnosis");
        Diagnosis.setColumns(20);
        Diagnosis.setRows(3);
        Diagnosis.setName("Diagnosis");
        ScrollDiagnosis.setViewportView(Diagnosis);
        FormInput.add(ScrollDiagnosis);
        ScrollDiagnosis.setBounds(124, 350, 530, 60);

        jLabel30.setText("Tatalaksana/Rencana :");
        jLabel30.setName("jLabel30");
        FormInput.add(jLabel30);
        jLabel30.setBounds(0, 415, 140, 23);

        ScrollTatalaksana.setName("ScrollTatalaksana");
        Tatalaksana.setColumns(20);
        Tatalaksana.setRows(3);
        Tatalaksana.setName("Tatalaksana");
        ScrollTatalaksana.setViewportView(Tatalaksana);
        FormInput.add(ScrollTatalaksana);
        ScrollTatalaksana.setBounds(124, 438, 530, 60);
        
        FormInput.setPreferredSize(new java.awt.Dimension(680, 490));
        ScrollInput.setViewportView(FormInput);
        ScrollInput.setName("ScrollInput");

        internalFrame3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data History ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); 
        internalFrame3.setName("internalFrame3"); 
        internalFrame3.setLayout(new java.awt.BorderLayout(1, 1));
        internalFrame3.setPreferredSize(new java.awt.Dimension(800, 200));

        Scroll.setName("Scroll"); 

        tbObat.setName("tbObat"); 
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        Scroll.setViewportView(tbObat);

        internalFrame3.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); 
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel1.setText("Key Word :");
        jLabel1.setName("jLabel1"); 
        jLabel1.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel1);

        TCari.setName("TCari"); 
        TCari.setPreferredSize(new java.awt.Dimension(300, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); 
        BtnCari.setMnemonic('2');
        BtnCari.setName("BtnCari"); 
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        panelGlass9.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); 
        BtnAll.setMnemonic('M');
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); 
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllActionPerformed(evt);
            }
        });
        panelGlass9.add(BtnAll);
        
        jLabel2.setText("Record :");
        jLabel2.setName("jLabel2");
        jLabel2.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel2);
        
        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount");
        LCount.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass9.add(LCount);

        internalFrame3.add(panelGlass9, java.awt.BorderLayout.PAGE_END);

        // Create container panel to hold both form and table
        javax.swing.JPanel containerPanel = new javax.swing.JPanel();
        containerPanel.setLayout(new java.awt.BorderLayout());
        containerPanel.add(ScrollInput, java.awt.BorderLayout.CENTER);
        containerPanel.add(internalFrame3, java.awt.BorderLayout.SOUTH);
        
        internalFrame1.add(containerPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>
    
    public void isCek(){
        BtnSimpan.setEnabled(true);
        BtnHapus.setEnabled(true);
        BtnEdit.setEnabled(true);
    }

    public void setNoRm(String norwt, String norm, String namapasien) {
        TNoRw.setText(norwt);
        TNoRM.setText(norm);
        TPasien.setText(namapasien);
        tampil();
    }
    
    private void tampil() {
        Valid.tabelKosong(tabMode);
        try{
            ps=koneksi.prepareStatement(
                    "select data_anc.no_rawat,data_anc.no_rkm_medis,pasien.nm_pasien,data_anc.tgl_kunjungan,data_anc.kunjungan_ke,"+
                    "data_anc.hpht,data_anc.hpl,data_anc.usia_kehamilan_minggu,data_anc.keluhan,data_anc.td_sistole,data_anc.td_diastole,"+
                    "data_anc.nadi,data_anc.suhu,data_anc.nafas,data_anc.tb,data_anc.bb,data_anc.lila,data_anc.tfu,"+
                    "data_anc.djj,data_anc.letak_janin,data_anc.kd_penyakit,penyakit.nm_penyakit,data_anc.kd_dokter,dokter.nm_dokter "+
                    "from data_anc inner join pasien on data_anc.no_rkm_medis=pasien.no_rkm_medis "+
                    "left join penyakit on data_anc.kd_penyakit=penyakit.kd_penyakit "+
                    "left join dokter on data_anc.kd_dokter=dokter.kd_dokter "+
                    "where data_anc.no_rawat like ? or data_anc.no_rkm_medis like ? or pasien.nm_pasien like ? "+
                    "order by data_anc.tgl_kunjungan desc");
            try {
                ps.setString(1,"%"+TCari.getText()+"%");
                ps.setString(2,"%"+TCari.getText()+"%");
                ps.setString(3,"%"+TCari.getText()+"%");
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),
                        rs.getString("tgl_kunjungan"),rs.getString("kunjungan_ke"),
                        rs.getString("hpht"),rs.getString("hpl"),rs.getString("usia_kehamilan_minggu")+" Mg",
                        rs.getString("keluhan"),rs.getString("td_sistole")+"/"+rs.getString("td_diastole"),
                        rs.getString("nadi"),rs.getString("suhu"),rs.getString("nafas"),
                        rs.getString("tb"),rs.getString("bb"),rs.getString("lila"),
                        rs.getString("tfu"),rs.getString("djj"),rs.getString("letak_janin"),
                        rs.getString("kd_penyakit")+" "+rs.getString("nm_penyakit"),
                        rs.getString("nm_dokter")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }
        }catch(Exception e){
            System.out.println("Notif : "+e);
        }
        LCount.setText(""+tabMode.getRowCount());
    }

    public void emptTeks() {
        KunjunganKe.setText("1");
        UsiaKehamilan.setText("");
        Keluhan.setText("");
        TDSistole.setText("");
        TDDiastole.setText("");
        Nadi.setText("");
        Suhu.setText("");
        Nafas.setText("");
        TB.setText("");
        BB.setText("");
        LILA.setText("");
        TFU.setText("");
        DJJ.setText("");
        LetakJanin.setText("");
        NmDokter.setText("");
        // Clear new fields
        PemeriksaanFisik.setText("");
        PemeriksaanLab.setText("");
        Diagnosis.setText("");
        Tatalaksana.setText("");
    }

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
        if(TNoRw.getText().trim().equals("")||TNoRM.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"Pasien");
        }else if(KdDokter.getText().trim().equals("")){
            Valid.textKosong(KdDokter,"Dokter");
        }else{
            if(Sequel.menyimpantf("data_anc","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?","Data",28,new String[]{
                TNoRw.getText(),TNoRM.getText(),Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Tanggal.getSelectedItem().toString().substring(11,19),
                KunjunganKe.getText(),Valid.SetTgl(HPHT.getSelectedItem()+""),Valid.SetTgl(HPL.getSelectedItem()+""),
                UsiaKehamilan.getText().replaceAll("[^0-9]", ""), "0", // Usia hari 0 dulu
                "",Keluhan.getText(),TDSistole.getText(),TDDiastole.getText(),
                Nadi.getText(),Suhu.getText(),Nafas.getText(),TB.getText(),BB.getText(),LILA.getText(),
                TFU.getText(),DJJ.getText(),LetakJanin.getText(),Edema.getSelectedItem().toString(),KetEdema.getText(),
                PemeriksaanFisik.getText(),PemeriksaanLab.getText(),Diagnosis.getText(),Tatalaksana.getText(),
                KdDokter.getText()
            })==true){
                tampil();
                emptTeks();
            }
        }
    }

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {
        emptTeks();
    }

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis...!!!!");
            TNoRw.requestFocus();
        }else if(TPasien.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Gagal menghapus. Pilih dulu data yang mau dihapus.\nKlik data pada table untuk memilih...!!!!");
        }else if(!(TPasien.getText().trim().equals(""))){
            if(Sequel.queryu2tf("delete from data_anc where no_rawat=?",1,new String[]{
                TNoRw.getText()
            })==true){
                tampil();
                emptTeks();
            }
        }
    }

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {
        if(TNoRw.getText().trim().equals("")||TNoRM.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"Pasien");
        }else{
            // Edit not implemented fully for simplicity, usually involves checking no_rawat and tgl_kunjungan
            JOptionPane.showMessageDialog(null,"Fitur edit belum diaktifkan, silahkan hapus dan input ulang jika ada kesalahan");
        }
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
    
    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnKeluar, BtnCari);
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
    
    private void getData() {
        int row=tbObat.getSelectedRow();
        if(row!= -1){
            TNoRw.setText(tbObat.getValueAt(row,0).toString());
            TNoRM.setText(tbObat.getValueAt(row,1).toString());
            TPasien.setText(tbObat.getValueAt(row,2).toString());
            KunjunganKe.setText(tbObat.getValueAt(row,4).toString());
            // Other fields...
        }
    }

    // Variables declaration
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnCariDokter;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnSimpan;
    private widget.ComboBox Edema;
    private widget.PanelBiasa FormInput;
    private widget.Tanggal HPHT;
    private widget.Tanggal HPL;
    private widget.TextBox KdDokter;
    private widget.TextBox Keluhan;
    private widget.TextBox KetEdema;
    private widget.TextBox KunjunganKe;
    private widget.Label LCount;
    private widget.TextBox LILA; // Lingkar Lengan Atas
    private widget.TextBox LetakJanin;
    private widget.TextBox BB;
    private widget.TextBox Nadi;
    private widget.TextBox Nafas;
    private widget.TextBox NmDokter;
    private widget.ScrollPane Scroll;
    private widget.TextBox Suhu;
    private widget.TextBox TB;
    private widget.TextBox TCari;
    private widget.TextBox TDDiastole;
    private widget.TextBox TDSistole;
    private widget.TextBox TFU; // Tinggi Fundus Uteri
    private widget.TextBox DJJ;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;

    private widget.Tanggal Tanggal;
    private widget.TextBox UsiaKehamilan;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame3;
    private widget.Label jLabel1;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel2;
    private widget.Label jLabel20;
    private widget.Label jLabel21;
    private widget.Label jLabel22;
    private widget.Label jLabel23;
    private widget.Label jLabel24;
    private widget.Label jLabel25;
    private widget.Label jLabel26;
    private widget.Label jLabel27;
    private widget.Label jLabel28;
    private widget.Label jLabel29;
    private widget.Label jLabel30;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbObat;
    // New components for comprehensive ANC
    private widget.ScrollPane ScrollInput;
    private widget.ScrollPane ScrollPemeriksaanFisik;
    private widget.TextArea PemeriksaanFisik;
    private widget.ScrollPane ScrollPemeriksaanLab;
    private widget.TextArea PemeriksaanLab;
    private widget.ScrollPane ScrollDiagnosis;
    private widget.TextArea Diagnosis;
    private widget.ScrollPane ScrollTatalaksana;
    private widget.TextArea Tatalaksana;
    // End of variables declaration
}
