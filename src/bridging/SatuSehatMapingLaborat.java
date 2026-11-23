/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package bridging;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import keuangan.DlgJnsPerawatanRadiologi;
import simrskhanza.DlgCariSnomed;

/**
 *
 * @author dosen
 */
public final class SatuSehatMapingLaborat extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private Connection koneksi=koneksiDB.condb();
    private DlgCariLoincLabSatuSehat loinc=new DlgCariLoincLabSatuSehat(null,false);
    private DlgCariSnomed snomed=new DlgCariSnomed(null,false);
    private PreparedStatement ps;
    private ResultSet rs;    
    private int i=0;
    private DlgCariTemplateLaborat pemeriksaan=new DlgCariTemplateLaborat(null,false);

    /** Creates new form DlgJnsPerawatanRalan
     * @param parent
     * @param modal */
    public SatuSehatMapingLaborat(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.setLocation(8,1);
        setSize(628,674);

        tabMode=new DefaultTableModel(null,new Object[]{
                "Periksa Code","Pemeriksaan System","ID Detail","Detail Pemeriksaan","Pemeriksaan Display",
                "Sampel Code","Sampel System","Sampel Display"
            }){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbJnsPerawatan.setModel(tabMode);

        tbJnsPerawatan.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbJnsPerawatan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 8; i++) {
            TableColumn column = tbJnsPerawatan.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(80);
            }else if(i==1){
                column.setPreferredWidth(200);
            }else if(i==2){
                column.setPreferredWidth(85);
            }else if(i==3){
                column.setPreferredWidth(200);
            }else if(i==4){
                column.setPreferredWidth(200);
            }else if(i==5){
                column.setPreferredWidth(85);
            }else if(i==6){
                column.setPreferredWidth(200);
            }else if(i==7){
                column.setPreferredWidth(200);
            }
        }
        tbJnsPerawatan.setDefaultRenderer(Object.class, new WarnaTable());

        KodePemeriksaan.setDocument(new batasInput((byte)15).getKata(KodePemeriksaan)); 
        Code.setDocument(new batasInput((byte)15).getKata(Code)); 
        RadiologiSystem.setDocument(new batasInput((byte)100).getKata(RadiologiSystem)); 
        Display.setDocument(new batasInput((byte)80).getKata(Display)); 
        SampelCode.setDocument(new batasInput((byte)15).getKata(SampelCode)); 
        SampelRadiologiSystem.setDocument(new batasInput((byte)100).getKata(SampelRadiologiSystem)); 
        SampelDisplay.setDocument(new batasInput((byte)80).getKata(SampelDisplay)); 
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));                  
        
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
        
        
        loinc.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(loinc.getTable().getSelectedRow()!= -1){                    
                    Code.setText(loinc.getTable().getValueAt(loinc.getTable().getSelectedRow(),0).toString());
                    Display.setText(loinc.getTable().getValueAt(loinc.getTable().getSelectedRow(),1).toString());
                    RadiologiSystem.setText(loinc.getTable().getValueAt(loinc.getTable().getSelectedRow(),2).toString());
                                        
                }
                loinc.requestFocus();
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
        
        loinc.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    loinc.dispose();
                }  
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        pemeriksaan.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(pemeriksaan.getTable().getSelectedRow()!= -1){                    
                    KodePemeriksaan.setText(pemeriksaan.getTable().getValueAt(pemeriksaan.getTable().getSelectedRow(),2).toString());
                    NamaPemeriksaan.setText(pemeriksaan.getTable().getValueAt(pemeriksaan.getTable().getSelectedRow(),3).toString());
                }
                btnBarang.requestFocus();
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
        
        pemeriksaan.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    pemeriksaan.dispose();
                }  
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        snomed.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(snomed.getTable().getSelectedRow()!= -1){                    
                    SampelRadiologiSystem.setText(snomed.getTable().getValueAt(snomed.getTable().getSelectedRow(),0).toString());
                    SampelCode.setText(snomed.getTable().getValueAt(snomed.getTable().getSelectedRow(),1).toString());
                    SampelDisplay.setText(snomed.getTable().getValueAt(snomed.getTable().getSelectedRow(),2).toString());
                                        
                }
                snomed.requestFocus();
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
        
        snomed.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    snomed.dispose();
                }  
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
        
        ChkInput.setSelected(false);
        isForm();

        // Tombol Auto Map (di luar GEN code)
        BtnAutoMap = new widget.Button();
        BtnAutoMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/system-software-update.png")));
        BtnAutoMap.setText("Auto Map");
        BtnAutoMap.setToolTipText("Auto mapping LOINC untuk item laboratorium yang belum dimapping");
        BtnAutoMap.setPreferredSize(new java.awt.Dimension(110, 30));
        BtnAutoMap.addActionListener(e -> btnAutoMapActionPerformed());
        panelGlass8.add(BtnAutoMap);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        NamaPemeriksaan = new widget.TextBox();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbJnsPerawatan = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        BtnAll = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        PanelInput = new javax.swing.JPanel();
        ChkInput = new widget.CekBox();
        FormInput = new widget.PanelBiasa();
        jLabel4 = new widget.Label();
        KodePemeriksaan = new widget.TextBox();
        btnBarang = new widget.Button();
        Code = new widget.TextBox();
        jLabel9 = new widget.Label();
        jLabel10 = new widget.Label();
        Display = new widget.TextBox();
        RadiologiSystem = new widget.TextBox();
        jLabel11 = new widget.Label();
        jLabel12 = new widget.Label();
        SampelCode = new widget.TextBox();
        SampelDisplay = new widget.TextBox();
        jLabel5 = new widget.Label();
        SampelRadiologiSystem = new widget.TextBox();
        btnLoinc = new widget.Button();
        btnSnomed = new widget.Button();

        NamaPemeriksaan.setEditable(false);
        NamaPemeriksaan.setHighlighter(null);
        NamaPemeriksaan.setName("NamaPemeriksaan"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Mapping Tindakan Laboratorium PK & MB Satu Sehat ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbJnsPerawatan.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbJnsPerawatan.setName("tbJnsPerawatan"); // NOI18N
        tbJnsPerawatan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbJnsPerawatanMouseClicked(evt);
            }
        });
        tbJnsPerawatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbJnsPerawatanKeyReleased(evt);
            }
        });
        Scroll.setViewportView(tbJnsPerawatan);

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

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setText("Semua");
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(100, 30));
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
        panelGlass8.add(BtnAll);

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

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(450, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('2');
        BtnCari.setToolTipText("Alt+2");
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

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(75, 23));
        panelGlass9.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass9.add(LCount);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(660, 155));
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

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 104));
        FormInput.setLayout(null);

        jLabel4.setText("Periksa System :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(365, 10, 100, 23);

        KodePemeriksaan.setEditable(false);
        KodePemeriksaan.setHighlighter(null);
        KodePemeriksaan.setName("KodePemeriksaan"); // NOI18N
        FormInput.add(KodePemeriksaan);
        KodePemeriksaan.setBounds(240, 10, 110, 23);

        btnBarang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnBarang.setMnemonic('1');
        btnBarang.setToolTipText("Alt+1");
        btnBarang.setName("btnBarang"); // NOI18N
        btnBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBarangActionPerformed(evt);
            }
        });
        btnBarang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnBarangKeyPressed(evt);
            }
        });
        FormInput.add(btnBarang);
        btnBarang.setBounds(350, 10, 28, 23);

        Code.setHighlighter(null);
        Code.setName("Code"); // NOI18N
        Code.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CodeKeyPressed(evt);
            }
        });
        FormInput.add(Code);
        Code.setBounds(99, 10, 110, 23);

        jLabel9.setText("Periksa Code :");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(0, 10, 95, 23);

        jLabel10.setText("Periksa Display :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(0, 40, 95, 23);

        Display.setHighlighter(null);
        Display.setName("Display"); // NOI18N
        Display.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DisplayKeyPressed(evt);
            }
        });
        FormInput.add(Display);
        Display.setBounds(99, 40, 625, 23);

        RadiologiSystem.setHighlighter(null);
        RadiologiSystem.setName("RadiologiSystem"); // NOI18N
        RadiologiSystem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RadiologiSystemKeyPressed(evt);
            }
        });
        FormInput.add(RadiologiSystem);
        RadiologiSystem.setBounds(469, 10, 255, 23);

        jLabel11.setText("Sampel Code :");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(0, 70, 95, 23);

        jLabel12.setText("Sampel Display :");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(0, 100, 95, 23);

        SampelCode.setHighlighter(null);
        SampelCode.setName("SampelCode"); // NOI18N
        SampelCode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SampelCodeKeyPressed(evt);
            }
        });
        FormInput.add(SampelCode);
        SampelCode.setBounds(99, 70, 160, 23);

        SampelDisplay.setHighlighter(null);
        SampelDisplay.setName("SampelDisplay"); // NOI18N
        SampelDisplay.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SampelDisplayKeyPressed(evt);
            }
        });
        FormInput.add(SampelDisplay);
        SampelDisplay.setBounds(99, 100, 625, 23);

        jLabel5.setText("Sampel System :");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(305, 70, 100, 23);

        SampelRadiologiSystem.setHighlighter(null);
        SampelRadiologiSystem.setName("SampelRadiologiSystem"); // NOI18N
        SampelRadiologiSystem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SampelRadiologiSystemKeyPressed(evt);
            }
        });
        FormInput.add(SampelRadiologiSystem);
        SampelRadiologiSystem.setBounds(409, 70, 315, 23);

        btnLoinc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnLoinc.setMnemonic('1');
        btnLoinc.setToolTipText("Alt+1");
        btnLoinc.setName("btnLoinc"); // NOI18N
        btnLoinc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoincActionPerformed(evt);
            }
        });
        btnLoinc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnLoincKeyPressed(evt);
            }
        });
        FormInput.add(btnLoinc);
        btnLoinc.setBounds(210, 10, 28, 23);

        btnSnomed.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnSnomed.setMnemonic('1');
        btnSnomed.setToolTipText("Alt+1");
        btnSnomed.setName("btnSnomed"); // NOI18N
        btnSnomed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSnomedActionPerformed(evt);
            }
        });
        btnSnomed.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnSnomedKeyPressed(evt);
            }
        });
        FormInput.add(btnSnomed);
        btnSnomed.setBounds(260, 70, 28, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBarangActionPerformed
        pemeriksaan.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        pemeriksaan.setLocationRelativeTo(internalFrame1);
        pemeriksaan.setVisible(true);
}//GEN-LAST:event_btnBarangActionPerformed

    private void btnBarangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnBarangKeyPressed
        Valid.pindah(evt, RadiologiSystem, Display);
}//GEN-LAST:event_btnBarangKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(Code.getText().trim().equals("")){
            Valid.textKosong(Code,"Periksa Code");
        }else if(RadiologiSystem.getText().trim().equals("")){
            Valid.textKosong(RadiologiSystem,"Pemeriksaan System");
        }else if(NamaPemeriksaan.getText().trim().equals("")){
            Valid.textKosong(NamaPemeriksaan,"Nama Pemeriksaan");
        }else if(Display.getText().trim().equals("")){
            Valid.textKosong(Display,"Pemeriksaan Display");
        }else if(SampelCode.getText().trim().equals("")){
            Valid.textKosong(SampelCode,"Sampel Code");
        }else if(SampelRadiologiSystem.getText().trim().equals("")){
            Valid.textKosong(SampelRadiologiSystem,"Sampel System");
        }else if(SampelDisplay.getText().trim().equals("")){
            Valid.textKosong(SampelDisplay,"Sampel Display");
        }else{
            if(Sequel.menyimpantf("satu_sehat_mapping_lab","?,?,?,?,?,?,?","Mapping Tindakan Radiologi",7,new String[]{
                KodePemeriksaan.getText(),Code.getText(),RadiologiSystem.getText(),Display.getText(),SampelCode.getText(),SampelRadiologiSystem.getText(),SampelDisplay.getText()
            })==true){
                tabMode.addRow(new String[]{
                    Code.getText(),RadiologiSystem.getText(),KodePemeriksaan.getText(),NamaPemeriksaan.getText(),Display.getText(),SampelCode.getText(),SampelRadiologiSystem.getText(),SampelDisplay.getText()
                });
                emptTeks();
                LCount.setText(""+tabMode.getRowCount());
            }                
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{Valid.pindah(evt,SampelDisplay, BtnBatal);}
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
}//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            emptTeks();
        }else{Valid.pindah(evt, BtnSimpan, BtnHapus);}
}//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if(Valid.hapusTabletf(tabMode,KodePemeriksaan,"satu_sehat_mapping_lab","id_template")==true){
            tabMode.removeRow(tbJnsPerawatan.getSelectedRow());
            emptTeks();
            LCount.setText(""+tabMode.getRowCount());
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
        if(Code.getText().trim().equals("")){
            Valid.textKosong(Code,"Periksa Code");
        }else if(RadiologiSystem.getText().trim().equals("")){
            Valid.textKosong(RadiologiSystem,"Pemeriksaan System");
        }else if(NamaPemeriksaan.getText().trim().equals("")){
            Valid.textKosong(NamaPemeriksaan,"Nama Pemeriksaan");
        }else if(Display.getText().trim().equals("")){
            Valid.textKosong(Display,"Pemeriksaan Display");
        }else if(SampelCode.getText().trim().equals("")){
            Valid.textKosong(SampelCode,"Sampel Code");
        }else if(SampelRadiologiSystem.getText().trim().equals("")){
            Valid.textKosong(SampelRadiologiSystem,"Sampel System");
        }else if(SampelDisplay.getText().trim().equals("")){
            Valid.textKosong(SampelDisplay,"Sampel Display");
        }else{
            if(tbJnsPerawatan.getSelectedRow()>-1){
                if(Sequel.mengedittf("satu_sehat_mapping_lab","id_template=?","id_template=?,code=?,system=?,display=?,sampel_code=?,sampel_system=?,sampel_display=?",8,new String[]{
                        KodePemeriksaan.getText(),Code.getText(),RadiologiSystem.getText(),Display.getText(),SampelCode.getText(),SampelRadiologiSystem.getText(),SampelDisplay.getText(),tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),2).toString()
                    })==true){
                    tabMode.setValueAt(Code.getText(),tbJnsPerawatan.getSelectedRow(),0);
                    tabMode.setValueAt(RadiologiSystem.getText(),tbJnsPerawatan.getSelectedRow(),1);
                    tabMode.setValueAt(KodePemeriksaan.getText(),tbJnsPerawatan.getSelectedRow(),2);
                    tabMode.setValueAt(NamaPemeriksaan.getText(),tbJnsPerawatan.getSelectedRow(),3);
                    tabMode.setValueAt(Display.getText(),tbJnsPerawatan.getSelectedRow(),4);
                    tabMode.setValueAt(SampelCode.getText(),tbJnsPerawatan.getSelectedRow(),5);
                    tabMode.setValueAt(SampelRadiologiSystem.getText(),tbJnsPerawatan.getSelectedRow(),6);
                    tabMode.setValueAt(SampelDisplay.getText(),tbJnsPerawatan.getSelectedRow(),7);
                    emptTeks();
                }
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
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{Valid.pindah(evt,BtnEdit,TCari);}
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
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
                param.put("parameter","%"+TCari.getText().trim()+"%");
                Valid.MyReport("rptMapingPemeriksaanLaboratSatuSehat.jasper","report","::[ Mapping Pemeriksaan Laboratorium Satu Sehat Kemenkes ]::",param);            
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
            TCari.setText("");
            tampil();
        }else{
            Valid.pindah(evt, BtnPrint, BtnKeluar);
        }
}//GEN-LAST:event_BtnAllKeyPressed

    private void tbJnsPerawatanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbJnsPerawatanMouseClicked
        if(tabMode.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
}//GEN-LAST:event_tbJnsPerawatanMouseClicked

    private void tbJnsPerawatanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbJnsPerawatanKeyReleased
        if(tabMode.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }//GEN-LAST:event_tbJnsPerawatanKeyReleased

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void CodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CodeKeyPressed
        Valid.pindah(evt, TCari, RadiologiSystem);
    }//GEN-LAST:event_CodeKeyPressed

    private void RadiologiSystemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RadiologiSystemKeyPressed
        Valid.pindah(evt, btnBarang, Display);
    }//GEN-LAST:event_RadiologiSystemKeyPressed

    private void DisplayKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DisplayKeyPressed
        Valid.pindah(evt, RadiologiSystem, SampelCode);
    }//GEN-LAST:event_DisplayKeyPressed

    private void SampelCodeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SampelCodeKeyPressed
        Valid.pindah(evt, Display, SampelRadiologiSystem);
    }//GEN-LAST:event_SampelCodeKeyPressed

    private void SampelDisplayKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SampelDisplayKeyPressed
        Valid.pindah(evt, SampelRadiologiSystem, BtnSimpan);
    }//GEN-LAST:event_SampelDisplayKeyPressed

    private void SampelRadiologiSystemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SampelRadiologiSystemKeyPressed
        Valid.pindah(evt, SampelCode, SampelDisplay);
    }//GEN-LAST:event_SampelRadiologiSystemKeyPressed

    private void btnLoincActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoincActionPerformed
        loinc.isCek();
        loinc.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        loinc.setLocationRelativeTo(internalFrame1);
        loinc.setVisible(true);        // TODO add your handling code here:
    }//GEN-LAST:event_btnLoincActionPerformed

    private void btnLoincKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnLoincKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnLoincKeyPressed

    private void btnSnomedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSnomedActionPerformed
        snomed.isCek();
        snomed.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        snomed.setLocationRelativeTo(internalFrame1);
        snomed.setVisible(true);
    }//GEN-LAST:event_btnSnomedActionPerformed

    private void btnSnomedKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnSnomedKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSnomedKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            SatuSehatMapingLaborat dialog = new SatuSehatMapingLaborat(new javax.swing.JFrame(), true);
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
    private widget.TextBox Code;
    private widget.TextBox Display;
    private widget.PanelBiasa FormInput;
    private widget.TextBox KodePemeriksaan;
    private widget.Label LCount;
    private widget.TextBox NamaPemeriksaan;
    private javax.swing.JPanel PanelInput;
    private widget.TextBox RadiologiSystem;
    private widget.TextBox SampelCode;
    private widget.TextBox SampelDisplay;
    private widget.TextBox SampelRadiologiSystem;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.Button btnBarang;
    private widget.Button btnLoinc;
    private widget.Button btnSnomed;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel9;
    private javax.swing.JPanel jPanel3;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbJnsPerawatan;
    // End of variables declaration//GEN-END:variables
    private widget.Button BtnAutoMap;

    /**
     * Data mapping LOINC: {keywords_pipe_delimited, loincCode, loincDisplay, sampelCode, sampelDisplay}
     * Sistem LOINC = http://loinc.org, Sistem SNOMED = http://snomed.info/sct
     */
    private String[][] getLoincMappings() {
        return new String[][] {
            // HEMATOLOGI
            {"HEMOGLOBIN|HGB| HB |^HB$", "718-7", "Hemoglobin [Mass/volume] in Blood", "119297000", "Blood specimen (specimen)"},
            {"HEMATOKRIT|HCT|PCV", "4544-3", "Hematocrit [Volume Fraction] of Blood by Automated count", "119297000", "Blood specimen (specimen)"},
            {"ERITROSIT|RBC|SEL DARAH MERAH", "789-8", "Erythrocytes [#/volume] in Blood by Automated count", "119297000", "Blood specimen (specimen)"},
            {"LEUKOSIT|WBC|SEL DARAH PUTIH", "6690-2", "Leukocytes [#/volume] in Blood by Automated count", "119297000", "Blood specimen (specimen)"},
            {"TROMBOSIT|PLT|PLATELET", "777-3", "Platelets [#/volume] in Blood by Automated count", "119297000", "Blood specimen (specimen)"},
            {"MCV", "787-2", "MCV [Entitic volume] by Automated count", "119297000", "Blood specimen (specimen)"},
            {"MCHC", "786-4", "MCHC [Mass/volume] by Automated count", "119297000", "Blood specimen (specimen)"},
            {"MCH", "785-6", "MCH [Entitic mass] by Automated count", "119297000", "Blood specimen (specimen)"},
            {"RETIKULOSIT|RETICULOCYTE", "17849-1", "Reticulocytes/100 erythrocytes in Blood by Automated count", "119297000", "Blood specimen (specimen)"},
            {"EOSINOFIL|EOSINOPHIL", "713-8", "Eosinophils/100 leukocytes in Blood by Automated count", "119297000", "Blood specimen (specimen)"},
            {"BASOFIL|BASOPHIL", "706-2", "Basophils/100 leukocytes in Blood", "119297000", "Blood specimen (specimen)"},
            {"NEUTROFIL BATANG|BANDS|STAB|BATANG", "764-1", "Band form neutrophils/100 leukocytes in Blood", "119297000", "Blood specimen (specimen)"},
            {"NEUTROFIL SEGMEN|NEUTROFIL|GRANULOSIT|SEGMEN", "770-8", "Neutrophils/100 leukocytes in Blood by Automated count", "119297000", "Blood specimen (specimen)"},
            {"LIMFOSIT|LYMPHOCYTE", "736-9", "Lymphocytes/100 leukocytes in Blood by Automated count", "119297000", "Blood specimen (specimen)"},
            {"MONOSIT|MONOCYTE", "5905-5", "Monocytes/100 leukocytes in Blood by Automated count", "119297000", "Blood specimen (specimen)"},
            {"LED|LAJU ENDAP|ESR|WESTERGREN", "4537-7", "Erythrocyte sedimentation rate by Westergren method", "119297000", "Blood specimen (specimen)"},
            // KOAGULASI
            {"INR", "6301-6", "INR in Platelet poor plasma by Coagulation assay", "119297000", "Blood specimen (specimen)"},
            {"APTT|PTT|ACTIVATED PARTIAL", "3173-2", "APTT in Platelet poor plasma by Coagulation assay", "119297000", "Blood specimen (specimen)"},
            {"D-DIMER|D DIMER|FIBRIN D", "48065-7", "Fibrin D-dimer DDU [Mass/volume] in Platelet poor plasma", "119297000", "Blood specimen (specimen)"},
            {"FIBRINOGEN", "3255-7", "Fibrinogen [Mass/volume] in Platelet poor plasma by Clauss method", "119297000", "Blood specimen (specimen)"},
            {"PROTHROMBIN|PROTROMBIN| PT |WAKTU PROTROMBIN", "5902-2", "Prothrombin time (PT)", "119297000", "Blood specimen (specimen)"},
            // KIMIA - HATI
            {"ALKALI FOSFATASE|ALKALINE PHOSPHATASE|ALP", "6768-6", "Alkaline phosphatase [Enzymatic activity/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"SGPT|ALT|ALANINE AMINOTRANSFERASE|ALANIN AMINO", "1742-6", "Alanine aminotransferase [Enzymatic activity/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"SGOT|AST|ASPARTATE AMINOTRANSFERASE|ASPARTAT AMINO", "1920-8", "Aspartate aminotransferase [Enzymatic activity/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"BILIRUBIN TOTAL|TOTAL BILIRUBIN", "1975-2", "Bilirubin.total [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"BILIRUBIN DIREK|BILIRUBIN DIRECT|DIRECT BILIRUBIN", "1968-7", "Bilirubin.direct [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"BILIRUBIN INDIREK|BILIRUBIN INDIRECT|INDIRECT BILIRUBIN|BILIRUBIN INDIR", "1971-1", "Bilirubin.indirect [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"GAMMA GT|GGT|GAMMA GLUTAMYL", "2324-2", "Gamma glutamyl transferase [Enzymatic activity/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"PROTEIN TOTAL|TOTAL PROTEIN", "2885-2", "Protein [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"ALBUMIN", "1751-7", "Albumin [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"GLOBULIN", "10834-0", "Globulin [Mass/volume] in Serum by calculation", "119364003", "Serum specimen (specimen)"},
            // KIMIA - GULA DARAH
            {"GLUKOSA SEWAKTU|GLUKOSA ACAK|GDS|BLOOD GLUCOSE RANDOM", "2339-0", "Glucose [Mass/volume] in Blood", "119297000", "Blood specimen (specimen)"},
            {"GLUKOSA PUASA|GDP|FASTING GLUCOSE", "1558-6", "Fasting glucose [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"GLUKOSA 2 JAM|GD 2 JAM|GDPP|POSTPRANDIAL|PP GLUCOSE", "1523-0", "Glucose [Mass/volume] in Serum or Plasma --2 hours post 75 g glucose PO", "119364003", "Serum specimen (specimen)"},
            {"HBA1C|HB A1C|HEMOGLOBIN A1C|GLIKOHEMOGLOBIN|A1C", "4548-4", "Hemoglobin A1c/Hemoglobin.total in Blood", "119297000", "Blood specimen (specimen)"},
            // KIMIA - GINJAL
            {"UREUM|UREA|BUN", "3094-0", "Urea nitrogen [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"KREATININ|CREATININE", "2160-0", "Creatinine [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"ASAM URAT|URIC ACID|URATE", "3084-1", "Urate [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            // KIMIA - LEMAK
            {"KOLESTEROL TOTAL|TOTAL CHOLESTEROL|CHOLESTEROL TOTAL", "2093-3", "Cholesterol [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"KOLESTEROL HDL|HDL CHOLESTEROL|HDL", "2085-9", "HDL Cholesterol", "119364003", "Serum specimen (specimen)"},
            {"KOLESTEROL LDL|LDL CHOLESTEROL|LDL", "18262-6", "LDL Cholesterol [Mass/volume] in Serum or Plasma by Direct assay", "119364003", "Serum specimen (specimen)"},
            {"TRIGLISERIDA|TRIGLYCERIDE", "2571-8", "Triglyceride [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            // KIMIA - ELEKTROLIT
            {"NATRIUM|SODIUM", "2951-2", "Sodium [Moles/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"KALIUM|POTASSIUM", "2823-3", "Potassium [Moles/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"KLORIDA|CHLORIDE", "2075-0", "Chloride [Moles/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"KALSIUM|CALCIUM", "17861-6", "Calcium [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"MAGNESIUM", "19123-9", "Magnesium [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"FOSFOR|FOSFAT|PHOSPHATE", "2777-1", "Phosphate [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            // KIMIA - LAIN
            {"CRP|C REACTIVE PROTEIN|C-REACTIVE", "1988-5", "C reactive protein [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"LDH|LACTATE DEHYDROGENASE|LAKTAT DEHIDROGENASE", "2532-0", "Lactate dehydrogenase [Enzymatic activity/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"AMILASE|AMYLASE", "1798-8", "Amylase [Enzymatic activity/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"LIPASE", "3040-3", "Lipase [Enzymatic activity/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"CKMB|CK-MB|CREATINE KINASE MB|CK MB", "13969-1", "Creatine kinase.MB [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"CK TOTAL|CK-TOTAL|CREATIN KINASE|CREATINE KINASE", "2157-6", "Creatine kinase [Enzymatic activity/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"TROPONIN I|TROPONIN-I", "10839-9", "Troponin I.cardiac [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"TROPONIN T|TROPONIN-T|TROPONIN", "6598-7", "Troponin T.cardiac [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"FERRITIN", "2276-4", "Ferritin [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"BESI SERUM|SERUM IRON|IRON SERUM|FE SERUM", "2498-4", "Iron [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            // TIROID
            {"TSH|THYROID STIMULATING|TIROTROPIN", "3016-3", "Thyrotropin [Units/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"FT4|FREE T4|T4 BEBAS|FREE THYROXINE", "3024-7", "Thyroxine (T4) free [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"FT3|FREE T3|T3 BEBAS|FREE TRIIODOTHYRONINE", "3051-0", "Triiodothyronine (T3) Free [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            // TUMOR MARKER
            {"CA 125|CA125|CANCER ANTIGEN 125", "10334-1", "Cancer Ag 125 [Units/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"CA 19-9|CA19-9|CA199|CANCER ANTIGEN 19", "24108-3", "Cancer Ag 19-9 [Units/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"CEA|CARCINOEMBRYONIC", "2857-1", "Carcinoembryonic Ag [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"AFP|ALPHA FETOPROTEIN|ALFA FETOPROTEIN", "1834-1", "AFP [Mass/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            // SEROLOGI
            {"HIV|HUMAN IMMUNODEFICIENCY", "44533-8", "HIV 1 Ab [Presence] in Serum or Plasma by Immunoassay", "119364003", "Serum specimen (specimen)"},
            {"ANTI HCV|ANTI-HCV|HCV AB|HEPATITIS C AB", "13955-0", "Hepatitis C virus Ab [Presence] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"HBSAG|HBS AG|HEPATITIS B SURFACE AG|HB SAG", "5196-1", "Hepatitis B virus surface Ag [Presence] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"ANTI HBS|ANTI-HBS|HBS AB|HEPATITIS B SURFACE AB", "16935-9", "Hepatitis B virus surface Ab [Units/volume] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"DENGUE NS1|NS1 DENGUE|NS1 AG", "75223-0", "Dengue virus NS1 Ag [Presence] in Serum or Plasma by Immunoassay", "119364003", "Serum specimen (specimen)"},
            {"DENGUE IGG|IGG DENGUE|DENGUE-IGG", "23898-2", "Dengue virus Ab.IgG [Presence] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"DENGUE IGM|IGM DENGUE|DENGUE-IGM", "23869-3", "Dengue virus Ab.IgM [Presence] in Serum or Plasma", "119364003", "Serum specimen (specimen)"},
            {"COVID|SARS-COV|SARS COV|CORONA|SWAB ANTIGEN|RAPID ANTIGEN", "94500-6", "SARS-CoV-2 (COVID-19) RNA [Presence] in Respiratory specimen", "119342007", "Saliva specimen (specimen)"},
            {"WIDAL|SALMONELLA TYPHI H|SALMONELLA TYPHI O", "23877-6", "Salmonella typhi H Ab [Titer] in Serum by Agglutination", "119364003", "Serum specimen (specimen)"},
            {"MALARIA|PLASMODIUM|RAPID TEST MALARIA", "51587-3", "Plasmodium sp [Presence] in Blood by Thick film", "119297000", "Blood specimen (specimen)"},
            // ANALISA GAS DARAH
            {"PH DARAH|BLOOD PH|PH ARTERI|PH VENA|DERAJAT KEASAMAN", "2744-1", "pH of Blood", "122554006", "Capillary blood specimen (specimen)"},
            {"PCO2|PACO2|TEKANAN CO2|PARTIAL PRESSURE CO2", "2019-8", "Carbon dioxide [Partial pressure] in Blood", "122554006", "Capillary blood specimen (specimen)"},
            {"PO2|PAO2|TEKANAN O2|PARTIAL PRESSURE O2", "2703-7", "Oxygen [Partial pressure] in Blood", "122554006", "Capillary blood specimen (specimen)"},
            {"HCO3|BIKARBONAT|BICARBONATE", "1963-8", "Bicarbonate [Moles/volume] in Venous blood", "122554006", "Capillary blood specimen (specimen)"},
            {"SATURASI O2|SPO2|OXYGEN SATURATION|SO2", "2708-6", "Oxygen saturation in Arterial blood", "122554006", "Capillary blood specimen (specimen)"},
            {"BASE EXCESS|BEECF|BASE DEFICIT|BASA BERLEBIH", "11556-8", "Base excess in Blood by calculation", "122554006", "Capillary blood specimen (specimen)"},
            // URINALISIS
            {"WARNA URIN|COLOR OF URINE|WARNA URINE", "5778-6", "Color of Urine", "122575003", "Urine specimen (specimen)"},
            {"PH URIN|PH URINE|URINE PH", "5803-2", "pH of Urine by Test strip", "122575003", "Urine specimen (specimen)"},
            {"BERAT JENIS URIN|BJ URIN|SPECIFIC GRAVITY URINE|BERAT JENIS URINE", "5811-5", "Specific gravity of Urine by Test strip", "122575003", "Urine specimen (specimen)"},
            {"PROTEIN URIN|PROTEIN URINE|ALBUMIN URIN", "20454-5", "Protein [Mass/volume] in Urine", "122575003", "Urine specimen (specimen)"},
            {"GLUKOSA URIN|GLUKOSA URINE|GLUCOSE URINE", "25428-4", "Glucose [Presence] in Urine by Test strip", "122575003", "Urine specimen (specimen)"},
            {"ERITROSIT URIN|ERITROSIT URINE|RBC URINE", "13945-1", "Erythrocytes [#/area] in Urine sediment by Microscopy", "122575003", "Urine specimen (specimen)"},
            {"LEUKOSIT URIN|LEUKOSIT URINE|WBC URINE", "5821-4", "Leukocytes [#/area] in Urine sediment by Microscopy", "122575003", "Urine specimen (specimen)"},
            {"KETON URIN|KETON URINE|KETONE URINE|KETON", "2514-8", "Ketones [Mass/volume] in Urine", "122575003", "Urine specimen (specimen)"},
            {"BILIRUBIN URIN|BILIRUBIN URINE|URINE BILIRUBIN", "5770-3", "Bilirubin.total [Presence] in Urine by Test strip", "122575003", "Urine specimen (specimen)"},
            {"UROBILINOGEN", "13658-0", "Urobilinogen [Mass/volume] in Urine", "122575003", "Urine specimen (specimen)"},
            {"SILINDER URIN|SILINDER URINE|CAST URINE|CAST URIN", "24124-0", "Casts [#/area] in Urine sediment by Microscopy", "122575003", "Urine specimen (specimen)"},
            {"NITRIT URIN|NITRIT URINE|URINE NITRIT|NITRITE", "5802-4", "Nitrite [Presence] in Urine by Test strip", "122575003", "Urine specimen (specimen)"},
            // MIKROBIOLOGI
            {"BTA|BASIL TAHAN ASAM|ACID FAST|AFB", "11545-1", "Microscopy study (procedure)", "119334006", "Sputum specimen (specimen)"},
            {"TCM|XPERT|GENEXPERT|MTB|MYCOBACTERIUM|TUBERKULOSIS PCR", "92830-6", "MTB complex DNA [Presence] in Sputum by NAA with non-probe detection", "119334006", "Sputum specimen (specimen)"},
            {"KULTUR DARAH|BLOOD CULTURE|HEMOKULTUR", "600-7", "Bacteria identified in Blood by Culture", "119297000", "Blood specimen (specimen)"},
            {"KULTUR URIN|URINE CULTURE|KULTUR URINE", "630-4", "Bacteria identified in Urine by Culture", "122575003", "Urine specimen (specimen)"},
        };
    }

    private boolean keywordMatch(String name, String keywords) {
        String nameUpper = name.toUpperCase();
        for (String kw : keywords.split("\\|")) {
            if (nameUpper.contains(kw.toUpperCase())) return true;
        }
        return false;
    }

    private void btnAutoMapActionPerformed() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Auto mapping LOINC untuk item laboratorium yang belum dimapping?\n" +
            "Item yang sudah ada mapping tidak akan diubah.",
            "Konfirmasi Auto Map",
            JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String[][] mappings = getLoincMappings();
        int berhasil = 0, tidakDikenal = 0;
        StringBuilder tidakDikenalList = new StringBuilder();

        PreparedStatement psSelect = null, psInsert = null;
        ResultSet rsSelect = null;
        try {
            // Ambil semua template_laboratorium yang belum dimapping
            psSelect = koneksi.prepareStatement(
                "SELECT tl.id_template, tl.Pemeriksaan FROM template_laboratorium tl " +
                "WHERE NOT EXISTS (SELECT 1 FROM satu_sehat_mapping_lab m WHERE m.id_template = tl.id_template) " +
                "AND tl.Pemeriksaan IS NOT NULL AND tl.Pemeriksaan != '' " +
                "ORDER BY tl.Pemeriksaan");
            rsSelect = psSelect.executeQuery();

            psInsert = koneksi.prepareStatement(
                "INSERT INTO satu_sehat_mapping_lab (id_template, code, system, display, sampel_code, sampel_system, sampel_display) " +
                "VALUES (?, ?, 'http://loinc.org', ?, ?, 'http://snomed.info/sct', ?)");

            while (rsSelect.next()) {
                String idTemplate = rsSelect.getString("id_template");
                String nama = rsSelect.getString("Pemeriksaan");
                if (nama == null || nama.trim().isEmpty()) continue;

                String[] matched = null;
                for (String[] m : mappings) {
                    if (keywordMatch(nama, m[0])) {
                        matched = m;
                        break;
                    }
                }

                if (matched != null) {
                    String disp = matched[2].length() > 80 ? matched[2].substring(0, 80) : matched[2];
                    String sampDisp = matched[4].length() > 80 ? matched[4].substring(0, 80) : matched[4];
                    psInsert.setString(1, idTemplate);
                    psInsert.setString(2, matched[1]);
                    psInsert.setString(3, disp);
                    psInsert.setString(4, matched[3]);
                    psInsert.setString(5, sampDisp);
                    psInsert.executeUpdate();
                    berhasil++;
                } else {
                    tidakDikenal++;
                    if (tidakDikenalList.length() < 500) {
                        tidakDikenalList.append("  - ").append(nama).append("\n");
                    }
                }
            }

            JOptionPane.showMessageDialog(this,
                "Auto mapping selesai!\n" +
                "Berhasil dimapping : " + berhasil + " item\n" +
                "Tidak dikenal      : " + tidakDikenal + " item" +
                (tidakDikenalList.length() > 0 ? "\n\nItem tidak dikenal (sebagian):\n" + tidakDikenalList : ""),
                "Hasil Auto Map", JOptionPane.INFORMATION_MESSAGE);

            tampil();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error auto mapping: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("Error btnAutoMapActionPerformed: " + ex.getMessage());
        } finally {
            try { if (rsSelect != null) rsSelect.close(); } catch (Exception e) {}
            try { if (psSelect != null) psSelect.close(); } catch (Exception e) {}
            try { if (psInsert != null) psInsert.close(); } catch (Exception e) {}
        }
    }

    private void tampil() {
        Valid.tabelKosong(tabMode);
        try{
           ps=koneksi.prepareStatement(
                   "select satu_sehat_mapping_lab.id_template,template_laboratorium.Pemeriksaan,satu_sehat_mapping_lab.code,satu_sehat_mapping_lab.system,"+
                   "satu_sehat_mapping_lab.display,satu_sehat_mapping_lab.sampel_code,satu_sehat_mapping_lab.sampel_system,satu_sehat_mapping_lab.sampel_display "+
                   "from satu_sehat_mapping_lab inner join template_laboratorium on satu_sehat_mapping_lab.id_template=template_laboratorium.id_template "+
                   (TCari.getText().equals("")?"":"where satu_sehat_mapping_lab.id_template like ? or template_laboratorium.Pemeriksaan like ? or "+
                   "satu_sehat_mapping_lab.code like ? or satu_sehat_mapping_lab.display like ? ")+
                   " order by satu_sehat_mapping_lab.code");
            try {
                if(!TCari.getText().equals("")){
                    ps.setString(1,"%"+TCari.getText()+"%");
                    ps.setString(2,"%"+TCari.getText()+"%");
                    ps.setString(3,"%"+TCari.getText()+"%");
                    ps.setString(4,"%"+TCari.getText()+"%");
                }
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        rs.getString("code"),rs.getString("system"),rs.getString("id_template"),rs.getString("Pemeriksaan"),rs.getString("display"),rs.getString("sampel_code"),rs.getString("sampel_system"),rs.getString("sampel_display")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif Ketersediaan : "+e);
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
        Code.setText("");
        RadiologiSystem.setText("");
        KodePemeriksaan.setText("");
        NamaPemeriksaan.setText("");
        Display.setText("");
        SampelCode.setText("");
        SampelRadiologiSystem.setText("");
        SampelDisplay.setText("");
        ChkInput.setSelected(true);
        isForm();
        Code.requestFocus();
    }

    private void getData() {
       if(tbJnsPerawatan.getSelectedRow()!= -1){
           Code.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),0).toString());
           RadiologiSystem.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),1).toString());
           KodePemeriksaan.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),2).toString());
           NamaPemeriksaan.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),3).toString());
           Display.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),4).toString());
           SampelCode.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),5).toString());
           SampelRadiologiSystem.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),6).toString());
           SampelDisplay.setText(tbJnsPerawatan.getValueAt(tbJnsPerawatan.getSelectedRow(),7).toString());
        }
    }
    
    public void isCek(){
        BtnSimpan.setEnabled(akses.getsatu_sehat_mapping_lab());
        BtnHapus.setEnabled(akses.getsatu_sehat_mapping_lab());
        BtnEdit.setEnabled(akses.getsatu_sehat_mapping_lab());
        BtnPrint.setEnabled(akses.getsatu_sehat_mapping_lab());
    }
    
    public JTable getTable(){
        return tbJnsPerawatan;
    }  
    
    private void isForm() {
        if (ChkInput.isSelected() == true) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 155));
            FormInput.setVisible(true);
            ChkInput.setVisible(true);
        } else if (ChkInput.isSelected() == false) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 20));
            FormInput.setVisible(false);
            ChkInput.setVisible(true);
        }
    }
}
