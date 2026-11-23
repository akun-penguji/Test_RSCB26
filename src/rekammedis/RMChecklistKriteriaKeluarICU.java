/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package rekammedis;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import kepegawaian.DlgCariPegawai;


/**
 *
 * @author perpustakaan
 */
public final class RMChecklistKriteriaKeluarICU extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i=0;    
    private DlgCariPegawai pegawai=new DlgCariPegawai(null,false);
    private String finger="";
    private StringBuilder htmlContent;
    /** Creates new form DlgRujuk
     * @param parent
     * @param modal */
    public RMChecklistKriteriaKeluarICU(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);
        setSize(628,674);

        tabMode=new DefaultTableModel(null,new Object[]{
            "No.Rawat","No.RM","Nama Pasien","Tgl.Lahir","J.K.","Tanggal","Denyut Jantung","Systolic Arterial Pressure",
            "Laju Pernafasan (tidak ada obstruksi jalan nafas)","Produksi Urin > 0.5 ml/kgBB","GCS > 10 atau pasien GCS > 8 dengan nafas spontan",
            "Topangan Inotropic atau vesoprossor intravena dan vasodilator tidak diperlukan lagi","Distrimia jantung teratasi","Kondisi Neurologis Stabill","PaO2>60 mmHg atau SaO2 > 90%","FiO2 < 0.50 tanpa peningkatan kebutugan Fio2",
            "PaCo2 < 65 mmHg dan pH 7,1-7,7 dgn Hemodinamik stabil","Serum natrium 120-155 mEq/L","Serum Kalium 2.0-6.0 mEq/L","Serum Glukosa 60-300 mg/dL","Trombosit 50.000 dan tidak ada pendarahan aktif","Laktat < 4.0 mmol/L","Ureum < 200 mg/dL dan atau tidak ada tanda-tanda azotermia berat","NIP/Kode Dokter","DPJP/Dokter Jaga/ICU"
        }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbObat.setModel(tabMode);

        //tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 27; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(105);
            }else if(i==1){
                column.setPreferredWidth(70);
            }else if(i==2){
                column.setPreferredWidth(150);
            }else if(i==3){
                column.setPreferredWidth(65);
            }else if(i==4){
                column.setPreferredWidth(25);
            }else if(i==5){
                column.setPreferredWidth(115);
            }else if(i==17){
                column.setPreferredWidth(90);
            }else if(i==18){
                column.setPreferredWidth(150);
            }else{
                column.setPreferredWidth(100);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte)17).getKata(TNoRw));
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
        
        pegawai.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(pegawai.getTable().getSelectedRow()!= -1){  
                    KodePetugas.setText(pegawai.getTable().getValueAt(pegawai.getTable().getSelectedRow(),0).toString());
                    NamaPetugas.setText(pegawai.getTable().getValueAt(pegawai.getTable().getSelectedRow(),1).toString());
                    btnPetugas.requestFocus();
                }  
                    
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
        
        HTMLEditorKit kit = new HTMLEditorKit();
        LoadHTML.setEditable(true);
        LoadHTML.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule(
                ".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}"+
                ".isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                ".isi5 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#AA0000;}"+
                ".isi6 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#FF0000;}"+
                ".isi7 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#C8C800;}"+
                ".isi8 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#00AA00;}"+
                ".isi9 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#969696;}"
        );
        Document doc = kit.createDefaultDocument();
        LoadHTML.setDocument(doc);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnKriteriaKeluarICU = new javax.swing.JMenuItem();
        LoadHTML = new widget.editorpane();
        JK = new widget.TextBox();
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
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
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
        ChkInput = new widget.CekBox();
        scrollInput = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        jLabel4 = new widget.Label();
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        TNoRM = new widget.TextBox();
        jLabel16 = new widget.Label();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        Tanggal = new widget.Tanggal();
        jLabel23 = new widget.Label();
        KodePetugas = new widget.TextBox();
        NamaPetugas = new widget.TextBox();
        btnPetugas = new widget.Button();
        jLabel5 = new widget.Label();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel65 = new widget.Label();
        Kriteria1 = new widget.ComboBox();
        jLabel66 = new widget.Label();
        Kriteria3 = new widget.ComboBox();
        jLabel62 = new widget.Label();
        jLabel67 = new widget.Label();
        Kriteria2 = new widget.ComboBox();
        jLabel63 = new widget.Label();
        Kriteria4 = new widget.ComboBox();
        jLabel68 = new widget.Label();
        Kriteria5 = new widget.ComboBox();
        Kriteria8 = new widget.ComboBox();
        Kriteria9 = new widget.ComboBox();
        Kriteria6 = new widget.ComboBox();
        jLabel70 = new widget.Label();
        jLabel69 = new widget.Label();
        jLabel71 = new widget.Label();
        Kriteria11 = new widget.ComboBox();
        jLabel72 = new widget.Label();
        Kriteria7 = new widget.ComboBox();
        jLabel73 = new widget.Label();
        jLabel74 = new widget.Label();
        jLabel78 = new widget.Label();
        jLabel79 = new widget.Label();
        jLabel81 = new widget.Label();
        Kriteria10 = new widget.ComboBox();
        jLabel75 = new widget.Label();
        Kriteria12 = new widget.ComboBox();
        jLabel76 = new widget.Label();
        Kriteria13 = new widget.ComboBox();
        jLabel77 = new widget.Label();
        Kriteria14 = new widget.ComboBox();
        jLabel80 = new widget.Label();
        Kriteria15 = new widget.ComboBox();
        jLabel82 = new widget.Label();
        Kriteria16 = new widget.ComboBox();
        jLabel83 = new widget.Label();
        Kriteria17 = new widget.ComboBox();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnKriteriaKeluarICU.setBackground(new java.awt.Color(255, 255, 254));
        MnKriteriaKeluarICU.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnKriteriaKeluarICU.setForeground(new java.awt.Color(50, 50, 50));
        MnKriteriaKeluarICU.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnKriteriaKeluarICU.setText("Formulir Checklist Kriteria Keluar ICU");
        MnKriteriaKeluarICU.setName("MnKriteriaKeluarICU"); // NOI18N
        MnKriteriaKeluarICU.setPreferredSize(new java.awt.Dimension(260, 26));
        MnKriteriaKeluarICU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnKriteriaKeluarICUActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnKriteriaKeluarICU);

        LoadHTML.setBorder(null);
        LoadHTML.setName("LoadHTML"); // NOI18N

        JK.setHighlighter(null);
        JK.setName("JK"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Check List Kriteria Keluar ICU ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 200));

        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat.setComponentPopupMenu(jPopupMenu1);
        tbObat.setName("tbObat"); // NOI18N
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

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });
        BtnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSimpanKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnSimpan);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); // NOI18N
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Baru");
        BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setName("BtnBatal"); // NOI18N
        BtnBatal.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBatalActionPerformed(evt);
            }
        });
        BtnBatal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnBatalKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnBatal);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus"); // NOI18N
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusActionPerformed(evt);
            }
        });
        BtnHapus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnHapusKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnHapus);

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        BtnEdit.setMnemonic('G');
        BtnEdit.setText("Ganti");
        BtnEdit.setToolTipText("Alt+G");
        BtnEdit.setName("BtnEdit"); // NOI18N
        BtnEdit.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEditActionPerformed(evt);
            }
        });
        BtnEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnEditKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnEdit);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setName("BtnPrint"); // NOI18N
        BtnPrint.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrintActionPerformed(evt);
            }
        });
        BtnPrint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnPrintKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnPrint);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass8.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass8.add(LCount);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        BtnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnKeluarKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnKeluar);

        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel19.setText("Tanggal :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "25-11-2024" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari1);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel21);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "25-11-2024" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(310, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('3');
        BtnCari.setToolTipText("Alt+3");
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        BtnCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariKeyPressed(evt);
            }
        });
        panelGlass9.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllActionPerformed(evt);
            }
        });
        BtnAll.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnAllKeyPressed(evt);
            }
        });
        panelGlass9.add(BtnAll);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 256));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setMnemonic('I');
        ChkInput.setText(".: Input Data");
        ChkInput.setToolTipText("Alt+I");
        ChkInput.setBorderPainted(true);
        ChkInput.setBorderPaintedFlat(true);
        ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput.setName("ChkInput"); // NOI18N
        ChkInput.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputActionPerformed(evt);
            }
        });
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        scrollInput.setName("scrollInput"); // NOI18N
        scrollInput.setPreferredSize(new java.awt.Dimension(102, 557));

        FormInput.setBackground(new java.awt.Color(250, 255, 245));
        FormInput.setBorder(null);
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 203));
        FormInput.setLayout(null);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("No.Rawat");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(21, 10, 75, 23);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(79, 10, 141, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        TPasien.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TPasienKeyPressed(evt);
            }
        });
        FormInput.add(TPasien);
        TPasien.setBounds(336, 10, 285, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        TNoRM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRMKeyPressed(evt);
            }
        });
        FormInput.add(TNoRM);
        TNoRM.setBounds(222, 10, 112, 23);

        jLabel16.setText("Tanggal :");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setVerifyInputWhenFocusTarget(false);
        FormInput.add(jLabel16);
        jLabel16.setBounds(0, 40, 75, 23);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(625, 10, 60, 23);

        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir"); // NOI18N
        FormInput.add(TglLahir);
        TglLahir.setBounds(689, 10, 100, 23);

        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "25-11-2024 13:57:26" }));
        Tanggal.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        Tanggal.setName("Tanggal"); // NOI18N
        Tanggal.setOpaque(false);
        Tanggal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalKeyPressed(evt);
            }
        });
        FormInput.add(Tanggal);
        Tanggal.setBounds(79, 40, 130, 23);

        jLabel23.setText("DPJP / Dokter Jaga / ICU :");
        jLabel23.setName("jLabel23"); // NOI18N
        FormInput.add(jLabel23);
        jLabel23.setBounds(221, 40, 160, 23);

        KodePetugas.setEditable(false);
        KodePetugas.setHighlighter(null);
        KodePetugas.setName("KodePetugas"); // NOI18N
        FormInput.add(KodePetugas);
        KodePetugas.setBounds(385, 40, 127, 23);

        NamaPetugas.setEditable(false);
        NamaPetugas.setName("NamaPetugas"); // NOI18N
        FormInput.add(NamaPetugas);
        NamaPetugas.setBounds(514, 40, 245, 23);

        btnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPetugas.setMnemonic('2');
        btnPetugas.setToolTipText("ALt+2");
        btnPetugas.setName("btnPetugas"); // NOI18N
        btnPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPetugasActionPerformed(evt);
            }
        });
        btnPetugas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnPetugasKeyPressed(evt);
            }
        });
        FormInput.add(btnPetugas);
        btnPetugas.setBounds(761, 40, 28, 23);

        jLabel5.setText(":");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(0, 10, 75, 23);

        jSeparator1.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator1.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator1.setName("jSeparator1"); // NOI18N
        FormInput.add(jSeparator1);
        jSeparator1.setBounds(0, 70, 810, 1);

        jSeparator2.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator2.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator2.setName("jSeparator2"); // NOI18N
        FormInput.add(jSeparator2);
        jSeparator2.setBounds(0, 70, 810, 1);

        jLabel65.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel65.setText("LABORATORIUM");
        jLabel65.setName("jLabel65"); // NOI18N
        FormInput.add(jLabel65);
        jLabel65.setBounds(20, 190, 310, 23);

        Kriteria1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0 Hari-1 Bulan: 100-190 kali/menit", "1 Bulan-2 tahun: 90-180 kali/menit", "2-5 tahun : < 160 kali/menit", "6-12 tahun : < 140 kali/menit", "13-18 tahun : < 130 kali/menit", "Dewasa: 50-150 kali/menit" }));
        Kriteria1.setName("Kriteria1"); // NOI18N
        Kriteria1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Kriteria1ActionPerformed(evt);
            }
        });
        Kriteria1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Kriteria1KeyPressed(evt);
            }
        });
        FormInput.add(Kriteria1);
        Kriteria1.setBounds(120, 100, 230, 23);

        jLabel66.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel66.setText("3. Laju Pernafasan (tidak ada obstruksi jalan nafas)");
        jLabel66.setName("jLabel66"); // NOI18N
        FormInput.add(jLabel66);
        jLabel66.setBounds(810, 100, 270, 23);

        Kriteria3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0 hari-1 bulan: 40-60 kali/menit", "1 bulan-1 tahun: 35-40 kali/menit", "1-5 tahun: 20-30 kali/menit", "6-12 tahun: 18-20 kali/menit ", "12-18 tahun: 16-18 kali/menit", "Dewasa: < 30 kali/menit" }));
        Kriteria3.setAutoscrolls(true);
        Kriteria3.setName("Kriteria3"); // NOI18N
        Kriteria3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Kriteria3ActionPerformed(evt);
            }
        });
        Kriteria3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Kriteria3KeyPressed(evt);
            }
        });
        FormInput.add(Kriteria3);
        Kriteria3.setBounds(1090, 100, 200, 23);

        jLabel62.setText("3. PaCo2 < 65 mmHg dan pH 7,1-7,7 dgn Hemodinamik stabil :");
        jLabel62.setName("jLabel62"); // NOI18N
        FormInput.add(jLabel62);
        jLabel62.setBounds(590, 210, 300, 23);

        jLabel67.setText("2. Systolic Arterial Pressure :");
        jLabel67.setName("jLabel67"); // NOI18N
        FormInput.add(jLabel67);
        jLabel67.setBounds(370, 100, 140, 23);

        Kriteria2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0 hari-1bulan: 60 mmHg", "1 bulan-1 tahun: 70 mmHg", "1-10 tahun: 70+(2xusia dalam tahun) mmHg", ">10 tahun: 90 mmHg", "Dewasa: > 80 mmHg" }));
        Kriteria2.setName("Kriteria2"); // NOI18N
        Kriteria2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Kriteria2KeyPressed(evt);
            }
        });
        FormInput.add(Kriteria2);
        Kriteria2.setBounds(510, 100, 290, 23);

        jLabel63.setText("2. FiO2 < 0.50 tanpa peningkatan kebutugan Fio2 :");
        jLabel63.setName("jLabel63"); // NOI18N
        FormInput.add(jLabel63);
        jLabel63.setBounds(260, 210, 260, 20);

        Kriteria4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "√", "-" }));
        Kriteria4.setName("Kriteria4"); // NOI18N
        Kriteria4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Kriteria4KeyPressed(evt);
            }
        });
        FormInput.add(Kriteria4);
        Kriteria4.setBounds(180, 130, 40, 23);

        jLabel68.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel68.setText("5. GCS > 10 atau pasien GCS > 8 dengan nafas spontan :");
        jLabel68.setName("jLabel68"); // NOI18N
        FormInput.add(jLabel68);
        jLabel68.setBounds(230, 130, 280, 20);

        Kriteria5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "√", "-" }));
        Kriteria5.setName("Kriteria5"); // NOI18N
        Kriteria5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Kriteria5ActionPerformed(evt);
            }
        });
        Kriteria5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Kriteria5KeyPressed(evt);
            }
        });
        FormInput.add(Kriteria5);
        Kriteria5.setBounds(510, 130, 50, 23);

        Kriteria8.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "√", "-" }));
        Kriteria8.setAutoscrolls(true);
        Kriteria8.setName("Kriteria8"); // NOI18N
        Kriteria8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Kriteria8KeyPressed(evt);
            }
        });
        FormInput.add(Kriteria8);
        Kriteria8.setBounds(180, 160, 40, 20);

        Kriteria9.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "√", "-" }));
        Kriteria9.setAutoscrolls(true);
        Kriteria9.setName("Kriteria9"); // NOI18N
        Kriteria9.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Kriteria9KeyPressed(evt);
            }
        });
        FormInput.add(Kriteria9);
        Kriteria9.setBounds(210, 210, 40, 20);

        Kriteria6.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "√", "-" }));
        Kriteria6.setName("Kriteria6"); // NOI18N
        Kriteria6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Kriteria6KeyPressed(evt);
            }
        });
        FormInput.add(Kriteria6);
        Kriteria6.setBounds(1010, 130, 40, 20);

        jLabel70.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel70.setText("1. PaO2>60 mmHg atau SaO2 > 90% :");
        jLabel70.setName("jLabel70"); // NOI18N
        FormInput.add(jLabel70);
        jLabel70.setBounds(20, 210, 190, 23);

        jLabel69.setText("6. Topangan Inotropic atau vesoprossor intravena dan vasodilator tidak diperlukan lagi :");
        jLabel69.setName("jLabel69"); // NOI18N
        FormInput.add(jLabel69);
        jLabel69.setBounds(570, 130, 430, 23);

        jLabel71.setText("8. Kondisi Neurologis Stabill :");
        jLabel71.setName("jLabel71"); // NOI18N
        FormInput.add(jLabel71);
        jLabel71.setBounds(-10, 160, 170, 20);

        Kriteria11.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "√", "-" }));
        Kriteria11.setName("Kriteria11"); // NOI18N
        Kriteria11.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Kriteria11KeyPressed(evt);
            }
        });
        FormInput.add(Kriteria11);
        Kriteria11.setBounds(900, 210, 40, 20);

        jLabel72.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel72.setText("7. Distrimia jantung teratasi :");
        jLabel72.setName("jLabel72"); // NOI18N
        FormInput.add(jLabel72);
        jLabel72.setBounds(1090, 130, 150, 23);

        Kriteria7.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "√", "-" }));
        Kriteria7.setName("Kriteria7"); // NOI18N
        Kriteria7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Kriteria7KeyPressed(evt);
            }
        });
        FormInput.add(Kriteria7);
        Kriteria7.setBounds(1250, 130, 40, 23);

        jLabel73.setText(":");
        jLabel73.setName("jLabel73"); // NOI18N
        FormInput.add(jLabel73);
        jLabel73.setBounds(110, 100, 10, 23);

        jLabel74.setText(":");
        jLabel74.setName("jLabel74"); // NOI18N
        FormInput.add(jLabel74);
        jLabel74.setBounds(810, 100, 270, 23);

        jLabel78.setText("4. Produksi Urin > 0.5 ml/kgBB :");
        jLabel78.setName("jLabel78"); // NOI18N
        FormInput.add(jLabel78);
        jLabel78.setBounds(10, 130, 160, 23);

        jLabel79.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel79.setText("1. Denyut Jantung");
        jLabel79.setName("jLabel79"); // NOI18N
        FormInput.add(jLabel79);
        jLabel79.setBounds(20, 100, 80, 23);

        jLabel81.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel81.setText("TANDA VITAL DAN KONDISI FISIOLOGI");
        jLabel81.setName("jLabel81"); // NOI18N
        FormInput.add(jLabel81);
        jLabel81.setBounds(20, 80, 310, 23);

        Kriteria10.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "√", "-" }));
        Kriteria10.setName("Kriteria10"); // NOI18N
        Kriteria10.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Kriteria10KeyPressed(evt);
            }
        });
        FormInput.add(Kriteria10);
        Kriteria10.setBounds(530, 210, 50, 20);

        jLabel75.setText("4. Serum natrium 120-155 mEq/L :");
        jLabel75.setName("jLabel75"); // NOI18N
        FormInput.add(jLabel75);
        jLabel75.setBounds(950, 210, 180, 20);

        Kriteria12.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "√", "-" }));
        Kriteria12.setName("Kriteria12"); // NOI18N
        Kriteria12.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Kriteria12KeyPressed(evt);
            }
        });
        FormInput.add(Kriteria12);
        Kriteria12.setBounds(1140, 210, 50, 20);

        jLabel76.setText("5. Serum Kalium 2.0-6.0 mEq/L :");
        jLabel76.setName("jLabel76"); // NOI18N
        FormInput.add(jLabel76);
        jLabel76.setBounds(0, 240, 170, 20);

        Kriteria13.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "√", "-" }));
        Kriteria13.setName("Kriteria13"); // NOI18N
        Kriteria13.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Kriteria13KeyPressed(evt);
            }
        });
        FormInput.add(Kriteria13);
        Kriteria13.setBounds(170, 240, 50, 20);

        jLabel77.setText("6. Serum Glukosa 60-300 mg/dL :");
        jLabel77.setName("jLabel77"); // NOI18N
        FormInput.add(jLabel77);
        jLabel77.setBounds(220, 240, 160, 20);

        Kriteria14.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "√", "-" }));
        Kriteria14.setName("Kriteria14"); // NOI18N
        Kriteria14.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Kriteria14KeyPressed(evt);
            }
        });
        FormInput.add(Kriteria14);
        Kriteria14.setBounds(380, 240, 50, 20);

        jLabel80.setText("7. Trombosit 50.000 dan tidak ada pendarahan aktif :");
        jLabel80.setName("jLabel80"); // NOI18N
        FormInput.add(jLabel80);
        jLabel80.setBounds(440, 240, 260, 20);

        Kriteria15.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "√", "-" }));
        Kriteria15.setName("Kriteria15"); // NOI18N
        Kriteria15.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Kriteria15KeyPressed(evt);
            }
        });
        FormInput.add(Kriteria15);
        Kriteria15.setBounds(700, 240, 50, 20);

        jLabel82.setText("8. Laktat < 4.0 mmol/L :");
        jLabel82.setName("jLabel82"); // NOI18N
        FormInput.add(jLabel82);
        jLabel82.setBounds(760, 240, 120, 20);

        Kriteria16.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "√", "-" }));
        Kriteria16.setName("Kriteria16"); // NOI18N
        Kriteria16.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Kriteria16KeyPressed(evt);
            }
        });
        FormInput.add(Kriteria16);
        Kriteria16.setBounds(880, 240, 50, 20);

        jLabel83.setText("9. Ureum < 200 mg/dL dan atau tidak ada tanda-tanda azotermia berat :");
        jLabel83.setName("jLabel83"); // NOI18N
        FormInput.add(jLabel83);
        jLabel83.setBounds(10, 270, 360, 20);

        Kriteria17.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "√", "-" }));
        Kriteria17.setName("Kriteria17"); // NOI18N
        Kriteria17.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Kriteria17KeyPressed(evt);
            }
        });
        FormInput.add(Kriteria17);
        Kriteria17.setBounds(380, 270, 50, 20);

        scrollInput.setViewportView(FormInput);

        PanelInput.add(scrollInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            isRawat();
        }else{            
            Valid.pindah(evt,TCari,Tanggal);
        }
}//GEN-LAST:event_TNoRwKeyPressed

    private void TPasienKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TPasienKeyPressed
        Valid.pindah(evt,TCari,BtnSimpan);
}//GEN-LAST:event_TPasienKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(TNoRw.getText().trim().equals("")||TPasien.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"pasien");
        }else if(KodePetugas.getText().trim().equals("")||NamaPetugas.getText().trim().equals("")){
            Valid.textKosong(btnPetugas,"DPJP/Dokter Jaga/ICU");
        }else{
            if(Sequel.menyimpantf("checklist_kriteria_keluar_icu","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?","Data",20,new String[]{
                TNoRw.getText(),Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Tanggal.getSelectedItem().toString().substring(11,19),Kriteria1.getSelectedItem().toString(),
                Kriteria2.getSelectedItem().toString(),Kriteria3.getSelectedItem().toString(),Kriteria4.getSelectedItem().toString(),Kriteria5.getSelectedItem().toString(),
                Kriteria6.getSelectedItem().toString(),Kriteria7.getSelectedItem().toString(),Kriteria8.getSelectedItem().toString(),Kriteria9.getSelectedItem().toString(),
                Kriteria10.getSelectedItem().toString(),Kriteria11.getSelectedItem().toString(),Kriteria12.getSelectedItem().toString(),Kriteria13.getSelectedItem().toString(),Kriteria14.getSelectedItem().toString(),Kriteria15.getSelectedItem().toString(),Kriteria16.getSelectedItem().toString(),Kriteria17.getSelectedItem().toString(),KodePetugas.getText()
            })==true){
                tabMode.addRow(new String[]{
                    TNoRw.getText(),TNoRM.getText(),TPasien.getText(),TglLahir.getText(),JK.getText(),Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Tanggal.getSelectedItem().toString().substring(11,19),
                    Kriteria1.getSelectedItem().toString(),Kriteria2.getSelectedItem().toString(),Kriteria3.getSelectedItem().toString(),Kriteria4.getSelectedItem().toString(),Kriteria5.getSelectedItem().toString(),
                    Kriteria6.getSelectedItem().toString(),Kriteria7.getSelectedItem().toString(),Kriteria8.getSelectedItem().toString(),Kriteria9.getSelectedItem().toString(),Kriteria10.getSelectedItem().toString(),
                    Kriteria11.getSelectedItem().toString(),Kriteria12.getSelectedItem().toString(),Kriteria13.getSelectedItem().toString(),Kriteria14.getSelectedItem().toString(),Kriteria15.getSelectedItem().toString(),Kriteria16.getSelectedItem().toString(),Kriteria17.getSelectedItem().toString(),KodePetugas.getText(),NamaPetugas.getText()
                });
                LCount.setText(""+tabMode.getRowCount());
                emptTeks();
            } 
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
            Valid.pindah(evt,Kriteria11,BtnBatal);
        }
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        ChkInput.setSelected(true);
        isForm(); 
        emptTeks();
}//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            emptTeks();
        }else{Valid.pindah(evt, BtnSimpan, BtnHapus);}
}//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if(tbObat.getSelectedRow()>-1){
            if(akses.getkode().equals("Admin Utama")){
                hapus();
            }else {
                if(akses.getkode().equals(tbObat.getValueAt(tbObat.getSelectedRow(),17).toString())){
                    hapus();
                }else{
                    JOptionPane.showMessageDialog(null,"Harus salah satu petugas sesuai user login..!!");
                }
            }
        }else{
            JOptionPane.showMessageDialog(rootPane,"Silahkan anda pilih data terlebih dahulu..!!");
        }   
}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnHapusActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if(TNoRw.getText().trim().equals("")||TPasien.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"pasien");
        }else if(KodePetugas.getText().trim().equals("")||NamaPetugas.getText().trim().equals("")){
            Valid.textKosong(btnPetugas,"DPJP/Dokter Jaga/ICU");
        }else{  
            if(tbObat.getSelectedRow()>-1){
                if(akses.getkode().equals("Admin Utama")){
                    ganti();
                }else {
                    if(akses.getkode().equals(tbObat.getValueAt(tbObat.getSelectedRow(),17).toString())){
                        ganti();
                    }else{
                        JOptionPane.showMessageDialog(null,"Harus salah satu petugas sesuai user login..!!");
                    }
                }
            }else{
                JOptionPane.showMessageDialog(rootPane,"Silahkan anda pilih data terlebih dahulu..!!");
            }
        }
}//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnEditActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
}//GEN-LAST:event_BtnEditKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        pegawai.dispose();
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnKeluarActionPerformed(null);
        }else{Valid.pindah(evt,BtnEdit,TCari);}
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        }else if(tabMode.getRowCount()!=0){
            try{
                htmlContent = new StringBuilder();
                htmlContent.append(                             
                    "<tr class='isi'>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.Rawat</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.RM</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Nama Pasien</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Tgl.Lahir</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>J.K.</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Tanggal</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Denyut Jantung</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Systolic Arterial Pressure</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Laju Pernafasan (tidak ada obstruksi jalan nafas)</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Produksi Urin > 0.5 ml/kgBB</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>GCS > 10 atau pasien GCS > 8 dengan nafas spontan</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Topangan Inotropic atau vesoprossor intravena dan vasodilator tidak diperlukan lagi</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Distrimia jantung teratasi</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Kondisi Neurologis Stabil</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>PaO2>60 mmHg atau SaO2 > 90%</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>FiO2 < 0.50 tanpa peningkatan kebutugan Fio2</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>PaCo2 < 65 mmHg dan pH 7,1-7,7 dgn Hemodinamik stabil</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Serum natrium 120-155 mEq/L</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Serum Kalium 2.0-6.0 mEq/L</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Serum Glukosa 60-300 mg/dL</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Trombosit 50.000 dan tidak ada pendarahan aktif</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Laktat < 4.0 mmol/L</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Ureum < 200 mg/dL dan atau tidak ada tanda-tanda azotermia berat</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>NIP/Kode Dokter</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>DPJP/Dokter Jaga/ICU</b></td>"+
                    "</tr>"
                );
                
                for (i = 0; i < tabMode.getRowCount(); i++) {
                    htmlContent.append(
                        "<tr class='isi'>"+
                           "<td valign='top'>"+tbObat.getValueAt(i,0).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,1).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,2).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,3).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,4).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,5).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,6).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,7).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,8).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,9).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,10).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,11).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,12).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,13).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,14).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,15).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,16).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,17).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,18).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,19).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,20).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,21).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,22).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,23).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,24).toString()+"</td>"+
                            "</tr>");
                }
                
                LoadHTML.setText(
                    "<html>"+
                      "<table width='2500px' border='0' align='center' cellpadding='1px' cellspacing='0' class='tbl_form'>"+
                       htmlContent.toString()+
                      "</table>"+
                    "</html>"
                );

                File g = new File("file2.css");            
                BufferedWriter bg = new BufferedWriter(new FileWriter(g));
                bg.write(
                    ".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                    ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}"+
                    ".isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                    ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                    ".isi5 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#AA0000;}"+
                    ".isi6 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#FF0000;}"+
                    ".isi7 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#C8C800;}"+
                    ".isi8 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#00AA00;}"+
                    ".isi9 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#969696;}"
                );
                bg.close();

                File f = new File("DataChecklistKriteriaKeluarICU.html");            
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));            
                bw.write(LoadHTML.getText().replaceAll("<head>","<head>"+
                            "<link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" />"+
                            "<table width='2500px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                                "<tr class='isi2'>"+
                                    "<td valign='top' align='center'>"+
                                        "<font size='4' face='Tahoma'>"+akses.getnamars()+"</font><br>"+
                                        akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs()+"<br>"+
                                        akses.getkontakrs()+", E-mail : "+akses.getemailrs()+"<br><br>"+
                                        "<font size='2' face='Tahoma'>DATA CHECK LIST KRITERIA KELUAR ICU<br><br></font>"+        
                                    "</td>"+
                               "</tr>"+
                            "</table>")
                );
                bw.close();                         
                Desktop.getDesktop().browse(f.toURI());

            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnPrintActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnEdit, BtnKeluar);
        }
}//GEN-LAST:event_BtnPrintKeyPressed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            BtnKeluar.requestFocus();
        }
}//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
}//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt, TCari, BtnAll);
        }
}//GEN-LAST:event_BtnCariKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        tampil();
}//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            tampil();
            TCari.setText("");
        }else{
            Valid.pindah(evt, BtnCari, TPasien);
        }
}//GEN-LAST:event_BtnAllKeyPressed

    private void TNoRMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRMKeyPressed
        // Valid.pindah(evt, TNm, BtnSimpan);
}//GEN-LAST:event_TNoRMKeyPressed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if(tabMode.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
}//GEN-LAST:event_tbObatMouseClicked

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyPressed
        if(tabMode.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
}//GEN-LAST:event_tbObatKeyPressed

    private void MnKriteriaKeluarICUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnKriteriaKeluarICUActionPerformed
        if(tbObat.getSelectedRow()>-1){
            Map<String, Object> param = new HashMap<>();
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());   
            param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
            finger=Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",tbObat.getValueAt(tbObat.getSelectedRow(),17).toString());
            param.put("finger","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+tbObat.getValueAt(tbObat.getSelectedRow(),18).toString()+"\nID "+(finger.equals("")?tbObat.getValueAt(tbObat.getSelectedRow(),17).toString():finger)+"\n"+Tanggal.getSelectedItem()); 
            Valid.MyReportqry("rptFormulirChecklistKriteriaKeluarICU.jasper","report","::[ Formulir Check List Kriteria Keluar ICU ]::",
                    "select reg_periksa.no_rawrptat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.tgl_lahir,pasien.jk,checklist_kriteria_keluar_icu.tanggal,"+
                    "checklist_kriteria_keluar_icu.kriteria1,checklist_kriteria_keluar_icu.kriteria2,checklist_kriteria_keluar_icu.kriteria3,"+
                    "checklist_kriteria_keluar_icu.kriteria4,checklist_kriteria_keluar_icu.kriteria5,checklist_kriteria_keluar_icu.kriteria6,"+
                    "checklist_kriteria_keluar_icu.kriteria7,checklist_kriteria_keluar_icu.kriteria8,checklist_kriteria_keluar_icu.kriteria9,"+
                    "checklist_kriteria_keluar_icu.kriteria10,checklist_kriteria_keluar_icu.kriteria11,checklist_kriteria_keluar_icu.kriteria12,checklist_kriteria_keluar_icu.kriteria13,checklist_kriteria_keluar_icu.kriteria14,checklist_kriteria_keluar_icu.kriteria15,checklist_kriteria_keluar_icu.kriteria16,checklist_kriteria_keluar_icu.kriteria17,checklist_kriteria_keluar_icu.nik,pegawai.nama "+
                    "from checklist_kriteria_keluar_icu inner join reg_periksa on checklist_kriteria_keluar_icu.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join pegawai on pegawai.nik=checklist_kriteria_keluar_icu.nik "+
                    "where checklist_kriteria_keluar_icu.no_rawat='"+tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()+"' and checklist_kriteria_keluar_icu.tanggal='"+tbObat.getValueAt(tbObat.getSelectedRow(),5).toString()+"' ",param);
        }
    }//GEN-LAST:event_MnKriteriaKeluarICUActionPerformed

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void TanggalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalKeyPressed
       Valid.pindah(evt,TCari,btnPetugas);
    }//GEN-LAST:event_TanggalKeyPressed

    private void btnPetugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPetugasActionPerformed
        pegawai.emptTeks();
        pegawai.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        pegawai.setLocationRelativeTo(internalFrame1);
        pegawai.setVisible(true);
    }//GEN-LAST:event_btnPetugasActionPerformed

    private void btnPetugasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnPetugasKeyPressed
       Valid.pindah(evt,Tanggal,Kriteria1);
    }//GEN-LAST:event_btnPetugasKeyPressed

    private void Kriteria1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Kriteria1KeyPressed
        Valid.pindah(evt,btnPetugas,Kriteria2);
    }//GEN-LAST:event_Kriteria1KeyPressed

    private void Kriteria3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Kriteria3KeyPressed
        Valid.pindah(evt,Kriteria2,Kriteria4);
    }//GEN-LAST:event_Kriteria3KeyPressed

    private void Kriteria2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Kriteria2KeyPressed
        Valid.pindah(evt,Kriteria1,Kriteria3);
    }//GEN-LAST:event_Kriteria2KeyPressed

    private void Kriteria4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Kriteria4KeyPressed
        Valid.pindah(evt,Kriteria3,Kriteria5);
    }//GEN-LAST:event_Kriteria4KeyPressed

    private void Kriteria5KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Kriteria5KeyPressed
        Valid.pindah(evt,Kriteria4,Kriteria6);
    }//GEN-LAST:event_Kriteria5KeyPressed

    private void Kriteria8KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Kriteria8KeyPressed
        Valid.pindah(evt,Kriteria7,Kriteria9);
    }//GEN-LAST:event_Kriteria8KeyPressed

    private void Kriteria9KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Kriteria9KeyPressed
        Valid.pindah(evt,Kriteria8,Kriteria11);
    }//GEN-LAST:event_Kriteria9KeyPressed

    private void Kriteria6KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Kriteria6KeyPressed
        Valid.pindah(evt,Kriteria5,Kriteria7);
    }//GEN-LAST:event_Kriteria6KeyPressed

    private void Kriteria11KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Kriteria11KeyPressed
        Valid.pindah(evt,Kriteria9,Kriteria11);
    }//GEN-LAST:event_Kriteria11KeyPressed

    private void Kriteria7KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Kriteria7KeyPressed
        Valid.pindah(evt,Kriteria6,Kriteria8);
    }//GEN-LAST:event_Kriteria7KeyPressed

    private void Kriteria1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Kriteria1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Kriteria1ActionPerformed

    private void Kriteria3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Kriteria3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Kriteria3ActionPerformed

    private void Kriteria5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Kriteria5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Kriteria5ActionPerformed

    private void Kriteria10KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Kriteria10KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Kriteria10KeyPressed

    private void Kriteria12KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Kriteria12KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Kriteria12KeyPressed

    private void Kriteria13KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Kriteria13KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Kriteria13KeyPressed

    private void Kriteria14KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Kriteria14KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Kriteria14KeyPressed

    private void Kriteria15KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Kriteria15KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Kriteria15KeyPressed

    private void Kriteria16KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Kriteria16KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Kriteria16KeyPressed

    private void Kriteria17KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Kriteria17KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_Kriteria17KeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMChecklistKriteriaKeluarICU dialog = new RMChecklistKriteriaKeluarICU(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.PanelBiasa FormInput;
    private widget.TextBox JK;
    private widget.TextBox KodePetugas;
    private widget.ComboBox Kriteria1;
    private widget.ComboBox Kriteria10;
    private widget.ComboBox Kriteria11;
    private widget.ComboBox Kriteria12;
    private widget.ComboBox Kriteria13;
    private widget.ComboBox Kriteria14;
    private widget.ComboBox Kriteria15;
    private widget.ComboBox Kriteria16;
    private widget.ComboBox Kriteria17;
    private widget.ComboBox Kriteria2;
    private widget.ComboBox Kriteria3;
    private widget.ComboBox Kriteria4;
    private widget.ComboBox Kriteria5;
    private widget.ComboBox Kriteria6;
    private widget.ComboBox Kriteria7;
    private widget.ComboBox Kriteria8;
    private widget.ComboBox Kriteria9;
    private widget.Label LCount;
    private widget.editorpane LoadHTML;
    private javax.swing.JMenuItem MnKriteriaKeluarICU;
    private widget.TextBox NamaPetugas;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Tanggal Tanggal;
    private widget.TextBox TglLahir;
    private widget.Button btnPetugas;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel16;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel23;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel62;
    private widget.Label jLabel63;
    private widget.Label jLabel65;
    private widget.Label jLabel66;
    private widget.Label jLabel67;
    private widget.Label jLabel68;
    private widget.Label jLabel69;
    private widget.Label jLabel7;
    private widget.Label jLabel70;
    private widget.Label jLabel71;
    private widget.Label jLabel72;
    private widget.Label jLabel73;
    private widget.Label jLabel74;
    private widget.Label jLabel75;
    private widget.Label jLabel76;
    private widget.Label jLabel77;
    private widget.Label jLabel78;
    private widget.Label jLabel79;
    private widget.Label jLabel8;
    private widget.Label jLabel80;
    private widget.Label jLabel81;
    private widget.Label jLabel82;
    private widget.Label jLabel83;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.ScrollPane scrollInput;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables
    
    public void tampil() {
        Valid.tabelKosong(tabMode);
        try{
            if(TCari.getText().trim().equals("")){
                ps=koneksi.prepareStatement(
                    "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.tgl_lahir,pasien.jk,checklist_kriteria_keluar_icu.tanggal,"+
                    "checklist_kriteria_keluar_icu.kriteria1,checklist_kriteria_keluar_icu.kriteria2,checklist_kriteria_keluar_icu.kriteria3,"+
                    "checklist_kriteria_keluar_icu.kriteria4,checklist_kriteria_keluar_icu.kriteria5,checklist_kriteria_keluar_icu.kriteria6,"+
                    "checklist_kriteria_keluar_icu.kriteria7,checklist_kriteria_keluar_icu.kriteria8,checklist_kriteria_keluar_icu.kriteria9,"+
                    "checklist_kriteria_keluar_icu.kriteria10,checklist_kriteria_keluar_icu.kriteria11,checklist_kriteria_keluar_icu.kriteria12,checklist_kriteria_keluar_icu.kriteria13,checklist_kriteria_keluar_icu.kriteria14,checklist_kriteria_keluar_icu.kriteria15,checklist_kriteria_keluar_icu.kriteria16,checklist_kriteria_keluar_icu.kriteria17,checklist_kriteria_keluar_icu.nik,pegawai.nama "+
                    "from checklist_kriteria_keluar_icu inner join reg_periksa on checklist_kriteria_keluar_icu.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join pegawai on pegawai.nik=checklist_kriteria_keluar_icu.nik "+
                    "where checklist_kriteria_keluar_icu.tanggal between ? and ? order by checklist_kriteria_keluar_icu.tanggal ");
            }else{
                ps=koneksi.prepareStatement(
                    "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.tgl_lahir,pasien.jk,checklist_kriteria_keluar_icu.tanggal,"+
                    "checklist_kriteria_keluar_icu.kriteria1,checklist_kriteria_keluar_icu.kriteria2,checklist_kriteria_keluar_icu.kriteria3,"+
                    "checklist_kriteria_keluar_icu.kriteria4,checklist_kriteria_keluar_icu.kriteria5,checklist_kriteria_keluar_icu.kriteria6,"+
                    "checklist_kriteria_keluar_icu.kriteria7,checklist_kriteria_keluar_icu.kriteria8,checklist_kriteria_keluar_icu.kriteria9,"+
                    "checklist_kriteria_keluar_icu.kriteria10,checklist_kriteria_keluar_icu.kriteria11,checklist_kriteria_keluar_icu.kriteria12,checklist_kriteria_keluar_icu.kriteria13,checklist_kriteria_keluar_icu.kriteria14,checklist_kriteria_keluar_icu.kriteria15,checklist_kriteria_keluar_icu.kriteria16,checklist_kriteria_keluar_icu.kriteria17,checklist_kriteria_keluar_icu.nik,pegawai.nama "+
                    "from checklist_kriteria_keluar_icu inner join reg_periksa on checklist_kriteria_keluar_icu.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join pegawai on pegawai.nik=checklist_kriteria_keluar_icu.nik "+
                    "where checklist_kriteria_keluar_icu.tanggal between ? and ? and (reg_periksa.no_rawat like ? or pasien.no_rkm_medis like ? or "+
                    "pasien.nm_pasien like ? or pegawai.nama like ? or checklist_kriteria_keluar_icu.nik like ?) order by checklist_kriteria_keluar_icu.tanggal ");
            }
                
            try {
                if(TCari.getText().trim().equals("")){
                    ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
                    ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
                }else{
                    ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
                    ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
                    ps.setString(3,"%"+TCari.getText()+"%");
                    ps.setString(4,"%"+TCari.getText()+"%");
                    ps.setString(5,"%"+TCari.getText()+"%");
                    ps.setString(6,"%"+TCari.getText()+"%");
                    ps.setString(7,"%"+TCari.getText()+"%");
                }
                    
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),rs.getString("tgl_lahir"),rs.getString("jk"),
                        rs.getString("tanggal"),rs.getString("kriteria1"),rs.getString("kriteria2"),rs.getString("kriteria3"),rs.getString("kriteria4"),
                        rs.getString("kriteria5"),rs.getString("kriteria6"),rs.getString("kriteria7"),rs.getString("kriteria8"),rs.getString("kriteria9"),
                        rs.getString("kriteria10"),rs.getString("kriteria11"),rs.getString("nik"),rs.getString("nama")
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
            System.out.println("Notifikasi : "+e);
        }
        LCount.setText(""+tabMode.getRowCount());
    }
    
    public void emptTeks() {
        Kriteria1.setSelectedItem("Ya");
        Kriteria2.setSelectedItem("Ya");
        Kriteria3.setSelectedItem("Ya");
        Kriteria4.setSelectedItem("Ya");
        Kriteria5.setSelectedItem("Ya");
        Kriteria6.setSelectedItem("Ya");
        Kriteria7.setSelectedItem("Ya");
        Kriteria8.setSelectedItem("Ya");
        Kriteria9.setSelectedItem("Ya");
        Kriteria10.setSelectedItem("Ya");
        Kriteria11.setSelectedItem("Ya");
        Kriteria12.setSelectedItem("Ya");
        Kriteria13.setSelectedItem("Ya");
        Kriteria14.setSelectedItem("Ya");
        Kriteria15.setSelectedItem("Ya");
        Kriteria16.setSelectedItem("Ya");
        Kriteria17.setSelectedItem("Ya");
        Tanggal.setDate(new Date());
        Kriteria1.requestFocus();
    } 

    private void getData() {
        if(tbObat.getSelectedRow()!= -1){
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            TglLahir.setText(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            JK.setText(tbObat.getValueAt(tbObat.getSelectedRow(),4).toString());
            Kriteria1.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString());
            Kriteria2.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString());
            Kriteria3.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),8).toString());
            Kriteria4.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),9).toString());
            Kriteria5.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),10).toString());
            Kriteria6.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),11).toString());
            Kriteria7.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),12).toString());
            Kriteria8.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),13).toString());
            Kriteria9.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),14).toString());
            Kriteria10.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),15).toString());
            Kriteria11.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),16).toString());
            Kriteria12.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),17).toString());
            Kriteria13.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),18).toString());
            Kriteria14.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),19).toString());
            Kriteria15.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),20).toString());
            Kriteria16.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),21).toString());
            Kriteria17.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),22).toString());
            Valid.SetTgl2(Tanggal,tbObat.getValueAt(tbObat.getSelectedRow(),5).toString());
        }
    }
    
    private void isRawat() {
        try {
            ps=koneksi.prepareStatement(
                    "select reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir,reg_periksa.tgl_registrasi "+
                    "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "where reg_periksa.no_rawat=?");
            try {
                ps.setString(1,TNoRw.getText());
                rs=ps.executeQuery();
                if(rs.next()){
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    DTPCari1.setDate(rs.getDate("tgl_registrasi"));
                    TPasien.setText(rs.getString("nm_pasien"));
                    JK.setText(rs.getString("jk"));
                    TglLahir.setText(rs.getString("tgl_lahir"));
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
        } catch (Exception e) {
            System.out.println("Notif : "+e);
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
    
    private void isForm(){
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,256));
            FormInput.setVisible(true);      
            ChkInput.setVisible(true);
        }else if(ChkInput.isSelected()==false){           
            ChkInput.setVisible(false);            
            PanelInput.setPreferredSize(new Dimension(WIDTH,20));
            FormInput.setVisible(false);      
            ChkInput.setVisible(true);
        }
    }
    
    public void isCek(){
        BtnSimpan.setEnabled(akses.getchecklist_kriteria_keluar_icu());
        BtnHapus.setEnabled(akses.getchecklist_kriteria_keluar_icu());
        BtnEdit.setEnabled(akses.getchecklist_kriteria_keluar_icu());
        BtnPrint.setEnabled(akses.getchecklist_kriteria_keluar_icu()); 
        if(akses.getjml2()>=1){
            btnPetugas.setEnabled(false);
            KodePetugas.setText(akses.getkode());
            NamaPetugas.setText(pegawai.tampil3(akses.getkode()));
        }
    }

    private void ganti() {
        if(Sequel.mengedittf("checklist_kriteria_keluar_icu","no_rawat=? and tanggal=?","no_rawat=?,tanggal=?,kriteria1=?,kriteria2=?,kriteria3=?,kriteria4=?,kriteria5=?,"+
                "kriteria6=?,kriteria7=?,kriteria8=?,kriteria9=?,kriteria10=?,kriteria11=?,nik=?",16,new String[]{
                TNoRw.getText(),Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Tanggal.getSelectedItem().toString().substring(11,19),Kriteria1.getSelectedItem().toString(),
                Kriteria2.getSelectedItem().toString(),Kriteria3.getSelectedItem().toString(),Kriteria4.getSelectedItem().toString(),Kriteria5.getSelectedItem().toString(),
                Kriteria6.getSelectedItem().toString(),Kriteria7.getSelectedItem().toString(),Kriteria8.getSelectedItem().toString(),Kriteria9.getSelectedItem().toString(),
                Kriteria11.getSelectedItem().toString(),Kriteria11.getSelectedItem().toString(),Kriteria10.getSelectedItem().toString(),Kriteria12.getSelectedItem().toString(),
                Kriteria13.getSelectedItem().toString(),Kriteria14.getSelectedItem().toString(),Kriteria15.getSelectedItem().toString(),Kriteria16.getSelectedItem().toString(),Kriteria17.getSelectedItem().toString(),KodePetugas.getText(),tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),
                tbObat.getValueAt(tbObat.getSelectedRow(),5).toString()
        })==true){
            tbObat.setValueAt(TNoRw.getText(),tbObat.getSelectedRow(),0);
            tbObat.setValueAt(TNoRM.getText(),tbObat.getSelectedRow(),1);
            tbObat.setValueAt(TPasien.getText(),tbObat.getSelectedRow(),2);
            tbObat.setValueAt(TglLahir.getText(),tbObat.getSelectedRow(),3);
            tbObat.setValueAt(JK.getText(),tbObat.getSelectedRow(),4);
            tbObat.setValueAt(Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Tanggal.getSelectedItem().toString().substring(11,19),tbObat.getSelectedRow(),5);
            tbObat.setValueAt(Kriteria1.getSelectedItem().toString(),tbObat.getSelectedRow(),6);
            tbObat.setValueAt(Kriteria2.getSelectedItem().toString(),tbObat.getSelectedRow(),7);
            tbObat.setValueAt(Kriteria3.getSelectedItem().toString(),tbObat.getSelectedRow(),8);
            tbObat.setValueAt(Kriteria4.getSelectedItem().toString(),tbObat.getSelectedRow(),9);
            tbObat.setValueAt(Kriteria5.getSelectedItem().toString(),tbObat.getSelectedRow(),10);
            tbObat.setValueAt(Kriteria6.getSelectedItem().toString(),tbObat.getSelectedRow(),11);
            tbObat.setValueAt(Kriteria7.getSelectedItem().toString(),tbObat.getSelectedRow(),12);
            tbObat.setValueAt(Kriteria8.getSelectedItem().toString(),tbObat.getSelectedRow(),13);
            tbObat.setValueAt(Kriteria9.getSelectedItem().toString(),tbObat.getSelectedRow(),14);
            tbObat.setValueAt(Kriteria10.getSelectedItem().toString(),tbObat.getSelectedRow(),15);
            tbObat.setValueAt(Kriteria11.getSelectedItem().toString(),tbObat.getSelectedRow(),16);
            tbObat.setValueAt(Kriteria12.getSelectedItem().toString(),tbObat.getSelectedRow(),17);
            tbObat.setValueAt(Kriteria13.getSelectedItem().toString(),tbObat.getSelectedRow(),18);
            tbObat.setValueAt(Kriteria14.getSelectedItem().toString(),tbObat.getSelectedRow(),19);
            tbObat.setValueAt(Kriteria15.getSelectedItem().toString(),tbObat.getSelectedRow(),20);
            tbObat.setValueAt(Kriteria16.getSelectedItem().toString(),tbObat.getSelectedRow(),21);
            tbObat.setValueAt(Kriteria17.getSelectedItem().toString(),tbObat.getSelectedRow(),22);
            tbObat.setValueAt(KodePetugas.getText(),tbObat.getSelectedRow(),21);
            tbObat.setValueAt(NamaPetugas.getText(),tbObat.getSelectedRow(),22);
            emptTeks();
        }
    }

    private void hapus() {
        if(Sequel.queryu2tf("delete from checklist_kriteria_keluar_icu where no_rawat=? and tanggal=?",2,new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),5).toString()
        })==true){
            tabMode.removeRow(tbObat.getSelectedRow());
            LCount.setText(""+tabMode.getRowCount());
            emptTeks();
        }else{
            JOptionPane.showMessageDialog(null,"Gagal menghapus..!!");
        }
    }
}
