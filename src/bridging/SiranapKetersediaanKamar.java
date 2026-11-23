/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgJnsPerawatanRalan.java
 *
 * Created on May 22, 2010, 11:58:21 PM
 */

package bridging;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import simrskhanza.DlgCariBangsal;

/**
 *
 * @author dosen
 */
public final class SiranapKetersediaanKamar extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;    
    private int i=0;
    private DlgCariBangsal bangsal=new DlgCariBangsal(null,false);
    private final Properties prop = new Properties();
    private String requestXML,URL="",respon="",idrs="";
    private final String link = "https://sirs.kemkes.go.id/fo/index.php/Fasyankes";
    private ApiKemenkesSirs api=new ApiKemenkesSirs();
    private HttpHeaders headers;
    private HttpEntity requestEntity;
    private ObjectMapper mapper= new ObjectMapper();
    private JsonNode root;
    private JsonNode nameNode;

    /** Creates new form DlgJnsPerawatanRalan
     * @param parent
     * @param modal */
    public SiranapKetersediaanKamar(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.setLocation(8,1);
        setSize(628,674);

        // v3: agregat per id_t_tt (1 kelas SIRANAP = gabungan banyak bangsal)
        Object[] row={"P","id_t_tt","Kelas SIRANAP","Ruang (Bangsal)","Jumlah","Terpakai"};
        tabMode=new DefaultTableModel(null,row){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){
                return colIndex==0;
             }
             Class[] types = new Class[] {
                java.lang.Boolean.class,java.lang.Object.class,java.lang.Object.class,
                java.lang.Object.class,java.lang.Object.class,java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbJnsPerawatan.setModel(tabMode);

        tbJnsPerawatan.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbJnsPerawatan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 6; i++) {
            TableColumn column = tbJnsPerawatan.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(90);
            }else if(i==2){
                column.setPreferredWidth(160);
            }else if(i==3){
                column.setPreferredWidth(280);
            }else if(i==4){
                column.setPreferredWidth(65);
            }else if(i==5){
                column.setPreferredWidth(65);
            }
        }
        tbJnsPerawatan.setDefaultRenderer(Object.class, new WarnaTable());

        Tersedia.setDocument(new batasInput((byte)4).getOnlyAngka(Tersedia)); 
        Kapasitas.setDocument(new batasInput((byte)4).getOnlyAngka(Kapasitas)); 
        TersediaMenunggu.setDocument(new batasInput((byte)4).getOnlyAngka(TersediaMenunggu)); 
        TersediaPria.setDocument(new batasInput((byte)4).getOnlyAngka(TersediaPria)); 
        TersediaWanita.setDocument(new batasInput((byte)4).getOnlyAngka(TersediaWanita)); 
        KdKamar.setDocument(new batasInput((byte)5).getKata(KdKamar)); 
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
        ChkInput.setSelected(false);
        isForm(); 
        
        bangsal.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(bangsal.getTable().getSelectedRow()!= -1){                   
                    KdKamar.setText(bangsal.getTable().getValueAt(bangsal.getTable().getSelectedRow(),0).toString());
                    NmKamar.setText(bangsal.getTable().getValueAt(bangsal.getTable().getSelectedRow(),1).toString());
                }     
                isCariKetersediaan();
                KdKamar.requestFocus();
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
        
        
        try {
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            URL = prop.getProperty("URLAPISIRS");
            idrs=koneksiDB.IDSIRS();
        } catch (Exception e) {
            System.out.println("E : "+e);
        }
    
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Tanggal = new widget.Tanggal();
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
        FormInput = new widget.PanelBiasa();
        jLabel8 = new widget.Label();
        Kapasitas = new widget.TextBox();
        jLabel4 = new widget.Label();
        jLabel19 = new widget.Label();
        KdKamar = new widget.TextBox();
        NmKamar = new widget.TextBox();
        btnKamar = new widget.Button();
        jLabel5 = new widget.Label();
        Kelas = new widget.ComboBox();
        jLabel9 = new widget.Label();
        Tersedia = new widget.TextBox();
        jLabel10 = new widget.Label();
        jLabel11 = new widget.Label();
        TersediaMenunggu = new widget.TextBox();
        TersediaPria = new widget.TextBox();
        jLabel12 = new widget.Label();
        TersediaWanita = new widget.TextBox();
        RuangSiranap = new widget.ComboBox();
        KelasSiranap = new widget.ComboBox();
        jLabel13 = new widget.Label();
        ChkInput = new widget.CekBox();

        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2022-11-07 11:44:39" }));
        Tanggal.setDisplayFormat("yyyy-MM-dd HH:mm:ss");
        Tanggal.setName("Tanggal"); // NOI18N
        Tanggal.setOpaque(false);
        Tanggal.setPreferredSize(new java.awt.Dimension(95, 23));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Ketersediaan Kamar SIRANAP KEMENKES ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbJnsPerawatan.setAutoCreateRowSorter(true);
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
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 130));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 197));
        FormInput.setLayout(null);

        jLabel8.setText("Kapasitas/Jumlah Bed :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(228, 72, 120, 23);

        Kapasitas.setText("0");
        Kapasitas.setHighlighter(null);
        Kapasitas.setName("Kapasitas"); // NOI18N
        Kapasitas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KapasitasKeyPressed(evt);
            }
        });
        FormInput.add(Kapasitas);
        Kapasitas.setBounds(351, 72, 50, 23);

        jLabel4.setText("Ruang SIRANAP :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 12, 97, 23);

        jLabel19.setText("Kamar/Ruang :");
        jLabel19.setName("jLabel19"); // NOI18N
        FormInput.add(jLabel19);
        jLabel19.setBounds(0, 42, 97, 23);

        KdKamar.setEditable(false);
        KdKamar.setHighlighter(null);
        KdKamar.setName("KdKamar"); // NOI18N
        KdKamar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdKamarKeyPressed(evt);
            }
        });
        FormInput.add(KdKamar);
        KdKamar.setBounds(100, 42, 77, 23);

        NmKamar.setEditable(false);
        NmKamar.setName("NmKamar"); // NOI18N
        FormInput.add(NmKamar);
        NmKamar.setBounds(180, 42, 190, 23);

        btnKamar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnKamar.setMnemonic('3');
        btnKamar.setToolTipText("ALt+3");
        btnKamar.setName("btnKamar"); // NOI18N
        btnKamar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKamarActionPerformed(evt);
            }
        });
        FormInput.add(btnKamar);
        btnKamar.setBounds(373, 42, 28, 23);

        jLabel5.setText("Kelas :");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(0, 72, 97, 23);

        Kelas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Kelas 1", "Kelas 2", "Kelas 3", "Kelas Utama", "Kelas VIP", "Kelas VVIP" }));
        Kelas.setName("Kelas"); // NOI18N
        Kelas.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                KelasItemStateChanged(evt);
            }
        });
        Kelas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KelasKeyPressed(evt);
            }
        });
        FormInput.add(Kelas);
        Kelas.setBounds(100, 72, 120, 23);

        jLabel9.setText("Tersedia :");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(418, 42, 77, 23);

        Tersedia.setText("0");
        Tersedia.setHighlighter(null);
        Tersedia.setName("Tersedia"); // NOI18N
        Tersedia.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TersediaKeyPressed(evt);
            }
        });
        FormInput.add(Tersedia);
        Tersedia.setBounds(498, 42, 50, 23);

        jLabel10.setText("Tersedia Pria :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(418, 72, 77, 23);

        jLabel11.setText("Menunggu :");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(548, 42, 125, 23);

        TersediaMenunggu.setText("0");
        TersediaMenunggu.setHighlighter(null);
        TersediaMenunggu.setName("TersediaMenunggu"); // NOI18N
        TersediaMenunggu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TersediaMenungguKeyPressed(evt);
            }
        });
        FormInput.add(TersediaMenunggu);
        TersediaMenunggu.setBounds(676, 42, 50, 23);

        TersediaPria.setText("0");
        TersediaPria.setHighlighter(null);
        TersediaPria.setName("TersediaPria"); // NOI18N
        TersediaPria.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TersediaPriaKeyPressed(evt);
            }
        });
        FormInput.add(TersediaPria);
        TersediaPria.setBounds(498, 72, 50, 23);

        jLabel12.setText("Tersedia Wanita :");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(548, 72, 125, 23);

        TersediaWanita.setText("0");
        TersediaWanita.setHighlighter(null);
        TersediaWanita.setName("TersediaWanita"); // NOI18N
        TersediaWanita.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TersediaWanitaKeyPressed(evt);
            }
        });
        FormInput.add(TersediaWanita);
        TersediaWanita.setBounds(676, 72, 50, 23);

        RuangSiranap.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0000 Umum", "0001 Anak", "0002 Anak (Luka Bakar)", "0003 Penyakit Dalam", "0004 Kebidanan", "0005 Kandungan", "0006 Bedah", "0007 Kanker", "0008 Mata", "0009 THT", "0010 Paru", "0011 Jantung", "0012 Orthopedi", "0013 Kulit dan Kelamin", "0014 Saraf", "0015 Jiwa", "0016 Infeksi", "0017 Luka Bakar", "0018 NAPZA", "0019 Isolasi Air Borne", "0020 Isolasi TB MDR", "0021 Kulit dan Kelamin", "0022 Isolasi Imunitas menurun", "0023 Isolasi Radioaktif", "0024 ICU/RICU", "0025 NICU", "0026 PICU", "0027 CVCU/ICCU", "0029 HCU", "0030 Kedokteran Nuklir" }));
        RuangSiranap.setName("RuangSiranap"); // NOI18N
        RuangSiranap.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RuangSiranapKeyPressed(evt);
            }
        });
        FormInput.add(RuangSiranap);
        RuangSiranap.setBounds(100, 12, 302, 23);

        KelasSiranap.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0001 Super VIP", "0002 VIP", "0003 Kelas 1", "0004 Kelas 2", "0005 Kelas 3", "0006 Intermediate", "0007 Isolasi", "0008 Rawat Khusus", "0009 Stroke Care Unit" }));
        KelasSiranap.setName("KelasSiranap"); // NOI18N
        KelasSiranap.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KelasSiranapKeyPressed(evt);
            }
        });
        FormInput.add(KelasSiranap);
        KelasSiranap.setBounds(498, 12, 228, 23);

        jLabel13.setText("Kelas SIRANAP :");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(405, 12, 90, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

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

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void KapasitasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KapasitasKeyPressed
        Valid.pindah(evt,Kelas,Tersedia);
}//GEN-LAST:event_KapasitasKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        // v3: kirim SEMUA baris (agregat per id_t_tt) ke RS Online /Fasyankes (PUT)
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Tidak ada data untuk dikirim. Muat data dulu (Cari/refresh).");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        int ok=0,gagal=0; StringBuilder logKirim=new StringBuilder();
        for(i=0;i<tabMode.getRowCount();i++){
            String idtt=tabMode.getValueAt(i,1).toString();
            String ruang=tabMode.getValueAt(i,3).toString();
            int jml=0,ter=0;
            try{ jml=Integer.parseInt(tabMode.getValueAt(i,4).toString()); }catch(Exception e){}
            try{ ter=Integer.parseInt(tabMode.getValueAt(i,5).toString()); }catch(Exception e){}
            String msg=kirimTT(idtt,ruang,jml,ter);
            if(msg!=null && msg.toLowerCase().contains("diupdate")){ ok++; }else{ gagal++; }
            logKirim.append(tabMode.getValueAt(i,2)).append(" → ").append(msg).append("\n");
        }
        this.setCursor(Cursor.getDefaultCursor());
        JOptionPane.showMessageDialog(null,"Kirim ketersediaan TT ke SIRANAP selesai.\nBerhasil: "+ok+"   Gagal: "+gagal+"\n\n"+logKirim.toString());
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{Valid.pindah(evt, TersediaWanita, BtnBatal);}
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
        // v3: hapus data TT yang dicentang dari RS Online /Fasyankes (DELETE body {id_t_tt})
        StringBuilder logHapus=new StringBuilder(); int n=0;
        for(i=0;i<tbJnsPerawatan.getRowCount();i++){
            if(tbJnsPerawatan.getValueAt(i,0).toString().equals("true")){
                String idtt=tbJnsPerawatan.getValueAt(i,1).toString();
                String msg=hapusTT(idtt);
                // jika berhasil terhapus di SIRANAP, hapus juga mapping lokal (sirs_mapping_kamar)
                boolean sukses = msg!=null && msg.toLowerCase().contains("hapus") && !msg.toLowerCase().contains("gagal");
                if(sukses){
                    Sequel.meghapus("sirs_mapping_kamar","id_t_tt",idtt);
                    msg = msg+" (mapping lokal ikut dihapus)";
                }
                logHapus.append(tbJnsPerawatan.getValueAt(i,2)).append(" → ").append(msg).append("\n");
                n++;
            }
        }
        if(n==0){
            JOptionPane.showMessageDialog(null,"Centang dulu baris yang mau dihapus.");
        }else{
            JOptionPane.showMessageDialog(null,"Hapus data TT di SIRANAP selesai.\n\n"+logHapus.toString());
        }
        tampil();
}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnHapusActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        // v3: update 1 baris terpilih ke RS Online /Fasyankes (PUT)
        if(tbJnsPerawatan.getSelectedRow()==-1){
            JOptionPane.showMessageDialog(null,"Pilih baris yang mau di-update dulu.");
            return;
        }
        int r=tbJnsPerawatan.getSelectedRow();
        String idtt=tabMode.getValueAt(r,1).toString();
        String ruang=tabMode.getValueAt(r,3).toString();
        int jml=0,ter=0;
        try{ jml=Integer.parseInt(tabMode.getValueAt(r,4).toString()); }catch(Exception e){}
        try{ ter=Integer.parseInt(tabMode.getValueAt(r,5).toString()); }catch(Exception e){}
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String msg=kirimTT(idtt,ruang,jml,ter);
        this.setCursor(Cursor.getDefaultCursor());
        JOptionPane.showMessageDialog(null,tabMode.getValueAt(r,2)+" → "+msg);
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
        if(! TCari.getText().trim().equals("")){
            BtnCariActionPerformed(evt);
        }
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
                Valid.MyReport("rptKamarSiranap.jasper","report","::[ Data Ketersediaan Kamar Siranap ]::",param);            
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

private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
  isForm();                
}//GEN-LAST:event_ChkInputActionPerformed

private void KdKamarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdKamarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_UP){
            btnKamarActionPerformed(null);
        }else{
            Valid.pindah(evt,KelasSiranap,Kelas);
        }
}//GEN-LAST:event_KdKamarKeyPressed

private void btnKamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKamarActionPerformed
        bangsal.emptTeks();
        bangsal.isCek();
        bangsal.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        bangsal.setLocationRelativeTo(internalFrame1);
        bangsal.setVisible(true);
}//GEN-LAST:event_btnKamarActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampil();
        emptTeks();
    }//GEN-LAST:event_formWindowOpened

    private void KelasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KelasKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            isCariKetersediaan();
            Kapasitas.requestFocus();            
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            KdKamar.requestFocus();
        }
    }//GEN-LAST:event_KelasKeyPressed

    private void TersediaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TersediaKeyPressed
        Valid.pindah(evt,Kapasitas,TersediaMenunggu);
    }//GEN-LAST:event_TersediaKeyPressed

    private void TersediaMenungguKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TersediaMenungguKeyPressed
        Valid.pindah(evt,Tersedia,TersediaPria);
    }//GEN-LAST:event_TersediaMenungguKeyPressed

    private void TersediaPriaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TersediaPriaKeyPressed
        Valid.pindah(evt,TersediaMenunggu,TersediaWanita);
    }//GEN-LAST:event_TersediaPriaKeyPressed

    private void TersediaWanitaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TersediaWanitaKeyPressed
        Valid.pindah(evt,TersediaPria,BtnSimpan);
    }//GEN-LAST:event_TersediaWanitaKeyPressed

    private void KelasItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_KelasItemStateChanged
        isCariKetersediaan();
    }//GEN-LAST:event_KelasItemStateChanged

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

    private void RuangSiranapKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RuangSiranapKeyPressed
         Valid.pindah(evt,TCari,KelasSiranap);
    }//GEN-LAST:event_RuangSiranapKeyPressed

    private void KelasSiranapKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KelasSiranapKeyPressed
         Valid.pindah(evt,RuangSiranap,KdKamar);
    }//GEN-LAST:event_KelasSiranapKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            SiranapKetersediaanKamar dialog = new SiranapKetersediaanKamar(new javax.swing.JFrame(), true);
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
    private widget.PanelBiasa FormInput;
    private widget.TextBox Kapasitas;
    private widget.TextBox KdKamar;
    private widget.ComboBox Kelas;
    private widget.ComboBox KelasSiranap;
    private widget.Label LCount;
    private widget.TextBox NmKamar;
    private javax.swing.JPanel PanelInput;
    private widget.ComboBox RuangSiranap;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.Tanggal Tanggal;
    private widget.TextBox Tersedia;
    private widget.TextBox TersediaMenunggu;
    private widget.TextBox TersediaPria;
    private widget.TextBox TersediaWanita;
    private widget.Button btnKamar;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel19;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private javax.swing.JPanel jPanel3;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbJnsPerawatan;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        Valid.tabelKosong(tabMode);
        try{
           // v3: agregat per id_t_tt (1 kelas SIRANAP = gabungan bangsal). jumlah/terpakai dari kamar.
           ps=koneksi.prepareStatement(
                   "SELECT m.id_t_tt AS id_t_tt, m.tt_kelas AS tt_kelas, "+
                   "GROUP_CONCAT(DISTINCT b.nm_bangsal ORDER BY b.nm_bangsal SEPARATOR ', ') AS ruang, "+
                   "COUNT(k.kd_kamar) AS jumlah, "+
                   "SUM(CASE WHEN k.status='ISI' THEN 1 ELSE 0 END) AS terpakai "+
                   "FROM kamar k "+
                   "INNER JOIN bangsal b ON b.kd_bangsal=k.kd_bangsal "+
                   "INNER JOIN sirs_mapping_kamar m ON m.kd_bangsal=k.kd_bangsal "+
                   "WHERE k.statusdata='1' AND m.id_t_tt IS NOT NULL AND m.id_t_tt<>'' "+
                   "AND (m.id_t_tt LIKE ? OR m.tt_kelas LIKE ? OR b.nm_bangsal LIKE ?) "+
                   "GROUP BY m.id_t_tt, m.tt_kelas ORDER BY m.tt_kelas");
            try {
                ps.setString(1,"%"+TCari.getText()+"%");
                ps.setString(2,"%"+TCari.getText()+"%");
                ps.setString(3,"%"+TCari.getText()+"%");
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        false,rs.getString("id_t_tt"),rs.getString("tt_kelas"),
                        rs.getString("ruang"),rs.getString("jumlah"),rs.getString("terpakai")
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
        KdKamar.setText("");
        NmKamar.setText("");
        Kapasitas.setText("0");
        Tersedia.setText("0");
        TersediaMenunggu.setText("0");
        TersediaPria.setText("0");
        TersediaWanita.setText("0");
        RuangSiranap.requestFocus();
    }

    private void getData() {
       // v3: tabel agregat per id_t_tt. Klik baris -> tampilkan detail baris terpilih di panel.
       int r = tbJnsPerawatan.getSelectedRow();
       if(r==-1){ return; }
       NmKamar.setText(tbJnsPerawatan.getValueAt(r,3).toString());   // ruang (gabungan bangsal)
       int jml=0, ter=0;
       try{ jml=Integer.parseInt(tbJnsPerawatan.getValueAt(r,4).toString()); }catch(Exception e){}
       try{ ter=Integer.parseInt(tbJnsPerawatan.getValueAt(r,5).toString()); }catch(Exception e){}
       Kapasitas.setText(""+jml);          // jumlah TT
       Tersedia.setText(""+(jml-ter));     // kosong = jumlah - terpakai
       TersediaMenunggu.setText("0");
       TersediaPria.setText("0");
       TersediaWanita.setText("0");
    }

    
   
    private void isForm(){
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,130));
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
        BtnSimpan.setEnabled(akses.getsiranap_ketersediaan_kamar());
        BtnHapus.setEnabled(akses.getsiranap_ketersediaan_kamar());
        BtnEdit.setEnabled(akses.getsiranap_ketersediaan_kamar());
        BtnPrint.setEnabled(akses.getsiranap_ketersediaan_kamar());
    }
    
    /** Kirim 1 baris ketersediaan TT ke RS Online v3 (/Fasyankes, PUT). Return pesan server. */
    private String kirimTT(String idtt, String ruang, int jumlah, int terpakai) {
        try {
            String ts = String.valueOf(api.GetUTCdatetimeAsString());
            String body = "{\"id_t_tt\":\""+idtt+"\",\"ruang\":\""+ruang.replace("\"","")+"\","
                + "\"jumlah\":\""+jumlah+"\",\"terpakai\":\""+terpakai+"\",\"jumlah_ruang\":\"0\","
                + "\"terpakai_suspek\":\"0\",\"terpakai_konfirmasi\":\"0\",\"antrian\":\"0\","
                + "\"prepare\":\"0\",\"prepare_plan\":\"0\",\"covid\":0,"
                + "\"terpakai_dbd\":\"0\",\"terpakai_dbd_anak\":\"0\",\"jumlah_dbd\":\"0\"}";
            headers = new HttpHeaders();
            headers.add("X-rs-id", idrs);
            headers.add("X-Timestamp", ts);
            headers.add("X-pass", koneksiDB.PASSSIRS());
            headers.add("Content-Type", "application/json");
            requestEntity = new HttpEntity(body, headers);
            String resp = api.getRest().exchange(link, HttpMethod.PUT, requestEntity, String.class).getBody();
            root = mapper.readTree(resp);
            JsonNode f = root.path("fasyankes");
            if (f.isArray() && f.size() > 0) return f.get(0).path("message").asText();
            return resp;
        } catch (Exception ex) {
            return "Gagal: " + ex.getMessage();
        }
    }

    /** Hapus 1 data TT di RS Online v3 (/Fasyankes, DELETE body {id_t_tt}). */
    private String hapusTT(String idtt) {
        try {
            String ts = String.valueOf(api.GetUTCdatetimeAsString());
            headers = new HttpHeaders();
            headers.add("X-rs-id", idrs);
            headers.add("X-Timestamp", ts);
            headers.add("X-pass", koneksiDB.PASSSIRS());
            headers.add("Content-Type", "application/json");
            requestEntity = new HttpEntity("{\"id_t_tt\":\""+idtt+"\"}", headers);
            String resp = api.getRest().exchange(link, HttpMethod.DELETE, requestEntity, String.class).getBody();
            root = mapper.readTree(resp);
            JsonNode f = root.path("fasyankes");
            if (f.isArray() && f.size() > 0) return f.get(0).path("message").asText();
            return resp;
        } catch (Exception ex) {
            return "Gagal: " + ex.getMessage();
        }
    }

    public JTable getTable(){
        return tbJnsPerawatan;
    }

    private void isCariKetersediaan() {
        if(!KdKamar.getText().equals("")){
            Kapasitas.setText(Sequel.cariIsi("select count(kd_kamar) from kamar where statusdata='1' and kelas='"+Kelas.getSelectedItem()+"' and kd_bangsal=?",KdKamar.getText()));
            Tersedia.setText(Sequel.cariIsi("select count(kd_kamar) from kamar where statusdata='1' and kelas='"+Kelas.getSelectedItem()+"' and status='KOSONG' and kd_bangsal=?",KdKamar.getText()));
            TersediaMenunggu.setText(Sequel.cariIsi("select count(kd_kamar) from kamar where statusdata='1' and kelas='"+Kelas.getSelectedItem()+"' and status='DIBERSIHKAN' and kd_bangsal=?",KdKamar.getText()));
        }
    }
    
    
    

    
}
