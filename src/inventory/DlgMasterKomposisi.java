/*
 * DlgMasterKomposisi.java
 * Dialog untuk mengelola master komposisi obat
 * 
 * Created on May 23, 2010, 12:57:16 AM
 * Modified for master_komposisi table
 */

package inventory;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Dialog Master Komposisi Obat
 * @author dosen
 */
public final class DlgMasterKomposisi extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private Connection koneksi=koneksiDB.condb();
    private int i=0;
    private ObjectMapper mapper = new ObjectMapper();
    
    /** Creates new form DlgMasterKomposisi
     * @param parent
     * @param modal */
    public DlgMasterKomposisi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(10,2);
        setSize(1000,700); // Diperbesar untuk lebih banyak field

        // Sesuaikan kolom dengan master_komposisi
        Object[] row={"P","Kode Komposisi","Nama Standar","Nama Generik","Kategori","Kekuatan","Satuan","Status"};
        tabMode=new DefaultTableModel(null,row){
             @Override 
             public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if (colIndex==0) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, 
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                java.lang.Object.class, java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbKamar.setModel(tabMode);
        tbKamar.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbKamar.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Set width untuk setiap kolom
        for (i = 0; i < 8; i++) {
            TableColumn column = tbKamar.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(25);  // Checkbox
            }else if(i==1){
                column.setPreferredWidth(120); // Kode Komposisi
            }else if(i==2){
                column.setPreferredWidth(200); // Nama Standar
            }else if(i==3){
                column.setPreferredWidth(180); // Nama Generik
            }else if(i==4){
                column.setPreferredWidth(150); // Kategori
            }else if(i==5){
                column.setPreferredWidth(120); // Kekuatan
            }else if(i==6){
                column.setPreferredWidth(80);  // Satuan
            }else if(i==7){
                column.setPreferredWidth(60);  // Status
            }
        }
        tbKamar.setDefaultRenderer(Object.class, new WarnaTable());
        
        // Set input limits sesuai dengan field database
        KodeKomposisi.setDocument(new batasInput((byte)20).getKata(KodeKomposisi));
        NamaStandar.setDocument(new batasInput((byte)100).getKata(NamaStandar));
        NamaGenerik.setDocument(new batasInput((byte)100).getKata(NamaGenerik));
        Kategori.setDocument(new batasInput((byte)50).getKata(Kategori));
        Kekuatan.setDocument(new batasInput((byte)100).getKata(Kekuatan));
        Satuan.setDocument(new batasInput((byte)20).getKata(Satuan));
        VariasiNama.setDocument(new batasInput((byte)500).getKata(VariasiNama));
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
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        Popup = new javax.swing.JPopupMenu();
        ppSimpan = new javax.swing.JMenuItem();
        ppGanti = new javax.swing.JMenuItem();
        ppHapus = new javax.swing.JMenuItem();
        ppCetak = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbKamar = new widget.Table();
        panelisi4 = new widget.panelisi();
        label1 = new widget.Label();
        KodeKomposisi = new widget.TextBox();
        label2 = new widget.Label();
        NamaStandar = new widget.TextBox();
        label3 = new widget.Label();
        NamaGenerik = new widget.TextBox();
        label4 = new widget.Label();
        Kategori = new widget.TextBox();
        label5 = new widget.Label();
        Kekuatan = new widget.TextBox();
        label6 = new widget.Label();
        Satuan = new widget.TextBox();
        label7 = new widget.Label();
        Status = new widget.ComboBox();
        label8 = new widget.Label();
        VariasiNama = new widget.TextBox();
        jPanel1 = new javax.swing.JPanel();
        panelisi3 = new widget.panelisi();
        label9 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        label10 = new widget.Label();
        LCount = new widget.Label();
        panelisi1 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        BtnKeluar = new widget.Button();

        Popup.setName("Popup"); // NOI18N

        ppSimpan.setBackground(new java.awt.Color(242, 242, 242));
        ppSimpan.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        ppSimpan.setForeground(new java.awt.Color(50, 50, 50));
        ppSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        ppSimpan.setText("Simpan");
        ppSimpan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppSimpan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppSimpan.setIconTextGap(8);
        ppSimpan.setName("ppSimpan"); // NOI18N
        ppSimpan.setPreferredSize(new java.awt.Dimension(150, 25));
        ppSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });
        Popup.add(ppSimpan);

        ppGanti.setBackground(new java.awt.Color(242, 242, 242));
        ppGanti.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        ppGanti.setForeground(new java.awt.Color(50, 50, 50));
        ppGanti.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        ppGanti.setText("Ganti");
        ppGanti.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppGanti.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppGanti.setIconTextGap(8);
        ppGanti.setName("ppGanti"); // NOI18N
        ppGanti.setPreferredSize(new java.awt.Dimension(150, 25));
        ppGanti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEditActionPerformed(evt);
            }
        });
        Popup.add(ppGanti);

        ppHapus.setBackground(new java.awt.Color(242, 242, 242));
        ppHapus.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        ppHapus.setForeground(new java.awt.Color(50, 50, 50));
        ppHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        ppHapus.setText("Hapus");
        ppHapus.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppHapus.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppHapus.setIconTextGap(8);
        ppHapus.setName("ppHapus"); // NOI18N
        ppHapus.setPreferredSize(new java.awt.Dimension(150, 25));
        ppHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusActionPerformed(evt);
            }
        });
        Popup.add(ppHapus);

        ppCetak.setBackground(new java.awt.Color(242, 242, 242));
        ppCetak.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        ppCetak.setForeground(new java.awt.Color(50, 50, 50));
        ppCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
        ppCetak.setText("Cetak");
        ppCetak.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppCetak.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppCetak.setIconTextGap(8);
        ppCetak.setName("ppCetak"); // NOI18N
        ppCetak.setPreferredSize(new java.awt.Dimension(150, 25));
        ppCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrintActionPerformed(evt);
            }
        });
        Popup.add(ppCetak);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Master Komposisi Obat ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setComponentPopupMenu(Popup);
        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbKamar.setAutoCreateRowSorter(true);
        tbKamar.setToolTipText("Silahkan klik untuk memilih data yang akan diedit/dihapus");
        tbKamar.setComponentPopupMenu(Popup);
        tbKamar.setName("tbKamar"); // NOI18N
        tbKamar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbKamarMouseClicked(evt);
            }
        });
        tbKamar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbKamarKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbKamarKeyReleased(evt);
            }
        });
        Scroll.setViewportView(tbKamar);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        panelisi4.setName("panelisi4"); // NOI18N
        panelisi4.setPreferredSize(new java.awt.Dimension(100, 130));
        panelisi4.setLayout(null);

        label1.setText("Kode Komposisi :");
        label1.setName("label1"); // NOI18N
        label1.setPreferredSize(new java.awt.Dimension(35, 23));
        panelisi4.add(label1);
        label1.setBounds(0, 12, 100, 23);

        KodeKomposisi.setHighlighter(null);
        KodeKomposisi.setName("KodeKomposisi"); // NOI18N
        KodeKomposisi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeKomposisiKeyPressed(evt);
            }
        });
        panelisi4.add(KodeKomposisi);
        KodeKomposisi.setBounds(104, 12, 120, 23);

        label2.setText("Nama Standar :");
        label2.setName("label2"); // NOI18N
        label2.setPreferredSize(new java.awt.Dimension(35, 23));
        panelisi4.add(label2);
        label2.setBounds(230, 12, 100, 23);

        NamaStandar.setHighlighter(null);
        NamaStandar.setName("NamaStandar"); // NOI18N
        NamaStandar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NamaStandarKeyPressed(evt);
            }
        });
        panelisi4.add(NamaStandar);
        NamaStandar.setBounds(334, 12, 250, 23);

        label3.setText("Nama Generik :");
        label3.setName("label3"); // NOI18N
        label3.setPreferredSize(new java.awt.Dimension(35, 23));
        panelisi4.add(label3);
        label3.setBounds(590, 12, 100, 23);

        NamaGenerik.setHighlighter(null);
        NamaGenerik.setName("NamaGenerik"); // NOI18N
        NamaGenerik.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NamaGenerikKeyPressed(evt);
            }
        });
        panelisi4.add(NamaGenerik);
        NamaGenerik.setBounds(694, 12, 200, 23);

        label4.setText("Kategori :");
        label4.setName("label4"); // NOI18N
        label4.setPreferredSize(new java.awt.Dimension(35, 23));
        panelisi4.add(label4);
        label4.setBounds(0, 42, 100, 23);

        Kategori.setHighlighter(null);
        Kategori.setName("Kategori"); // NOI18N
        Kategori.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KategoriKeyPressed(evt);
            }
        });
        panelisi4.add(Kategori);
        Kategori.setBounds(104, 42, 180, 23);

        label5.setText("Kekuatan :");
        label5.setName("label5"); // NOI18N
        label5.setPreferredSize(new java.awt.Dimension(35, 23));
        panelisi4.add(label5);
        label5.setBounds(290, 42, 80, 23);

        Kekuatan.setHighlighter(null);
        Kekuatan.setName("Kekuatan"); // NOI18N
        Kekuatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KekuatanKeyPressed(evt);
            }
        });
        panelisi4.add(Kekuatan);
        Kekuatan.setBounds(374, 42, 150, 23);

        label6.setText("Satuan :");
        label6.setName("label6"); // NOI18N
        label6.setPreferredSize(new java.awt.Dimension(35, 23));
        panelisi4.add(label6);
        label6.setBounds(530, 42, 60, 23);

        Satuan.setHighlighter(null);
        Satuan.setName("Satuan"); // NOI18N
        Satuan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SatuanKeyPressed(evt);
            }
        });
        panelisi4.add(Satuan);
        Satuan.setBounds(594, 42, 100, 23);

        label7.setText("Status :");
        label7.setName("label7"); // NOI18N
        label7.setPreferredSize(new java.awt.Dimension(35, 23));
        panelisi4.add(label7);
        label7.setBounds(700, 42, 60, 23);

        Status.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "0" }));
        Status.setName("Status"); // NOI18N
        Status.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                StatusKeyPressed(evt);
            }
        });
        panelisi4.add(Status);
        Status.setBounds(764, 42, 60, 23);

        label8.setText("Variasi Nama :");
        label8.setName("label8"); // NOI18N
        label8.setPreferredSize(new java.awt.Dimension(35, 23));
        panelisi4.add(label8);
        label8.setBounds(0, 72, 100, 23);

        VariasiNama.setHighlighter(null);
        VariasiNama.setName("VariasiNama"); // NOI18N
        VariasiNama.setToolTipText("Pisahkan dengan koma untuk multiple variasi nama. Contoh: paracetamol,parasetamol,acetaminophen");
        VariasiNama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                VariasiNamaKeyPressed(evt);
            }
        });
        panelisi4.add(VariasiNama);
        VariasiNama.setBounds(104, 72, 720, 23);

        internalFrame1.add(panelisi4, java.awt.BorderLayout.PAGE_START);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setOpaque(false);
        jPanel1.setPreferredSize(new java.awt.Dimension(816, 100));
        jPanel1.setLayout(new java.awt.BorderLayout(1, 1));

        panelisi3.setName("panelisi3"); // NOI18N
        panelisi3.setPreferredSize(new java.awt.Dimension(100, 44));
        panelisi3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 9));

        label9.setText("Cari Komposisi :");
        label9.setName("label9"); // NOI18N
        label9.setPreferredSize(new java.awt.Dimension(90, 23));
        panelisi3.add(label9);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(350, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelisi3.add(TCari);

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
        panelisi3.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('1');
        BtnAll.setToolTipText("Alt+1");
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
        panelisi3.add(BtnAll);

        label10.setText("Record :");
        label10.setName("label10"); // NOI18N
        label10.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(label10);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(LCount);

        jPanel1.add(panelisi3, java.awt.BorderLayout.PAGE_START);

        panelisi1.setName("panelisi1"); // NOI18N
        panelisi1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

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
        panelisi1.add(BtnSimpan);

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
        panelisi1.add(BtnBatal);

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
        panelisi1.add(BtnHapus);

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
        panelisi1.add(BtnEdit);

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
        panelisi1.add(BtnPrint);

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
        panelisi1.add(BtnKeluar);

        jPanel1.add(panelisi1, java.awt.BorderLayout.CENTER);

        internalFrame1.add(jPanel1, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>                        

    private void KodeKomposisiKeyPressed(java.awt.event.KeyEvent evt) {                              
        Valid.pindah(evt,TCari,NamaStandar);
    }                             

    private void NamaStandarKeyPressed(java.awt.event.KeyEvent evt) {                              
        Valid.pindah(evt,KodeKomposisi,NamaGenerik);
    }                             

    private void NamaGenerikKeyPressed(java.awt.event.KeyEvent evt) {                              
        Valid.pindah(evt,NamaStandar,Kategori);
    }                             

    private void KategoriKeyPressed(java.awt.event.KeyEvent evt) {                              
        Valid.pindah(evt,NamaGenerik,Kekuatan);
    }                             

    private void KekuatanKeyPressed(java.awt.event.KeyEvent evt) {                              
        Valid.pindah(evt,Kategori,Satuan);
    }                             

    private void SatuanKeyPressed(java.awt.event.KeyEvent evt) {                              
        Valid.pindah(evt,Kekuatan,Status);
    }                             

    private void StatusKeyPressed(java.awt.event.KeyEvent evt) {                              
        Valid.pindah(evt,Satuan,VariasiNama);
    }                             

    private void VariasiNamaKeyPressed(java.awt.event.KeyEvent evt) {                              
        Valid.pindah(evt,Status,BtnSimpan);
    }                             

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {                                          
        if(KodeKomposisi.getText().trim().equals("")){
            Valid.textKosong(KodeKomposisi,"Kode Komposisi");
        }else if(NamaStandar.getText().trim().equals("")){
            Valid.textKosong(NamaStandar,"Nama Standar");
        }else{
            try {
                // Convert variasi nama ke JSON array
                String variasiJson = "[]";
                if(!VariasiNama.getText().trim().equals("")){
                    String[] variasi = VariasiNama.getText().split(",");
                    ArrayNode arrayNode = mapper.createArrayNode();
                    for(String nama : variasi){
                        arrayNode.add(nama.trim());
                    }
                    variasiJson = mapper.writeValueAsString(arrayNode);
                }

                // Insert ke database master_komposisi
                ps = koneksi.prepareStatement(
                    "INSERT INTO master_komposisi " +
                    "(kode_komposisi, nama_standar, nama_generik, kategori, " +
                    "variasi_nama, kekuatan_umum, satuan, status_aktif) " +
                    "VALUES (?,?,?,?,?,?,?,?)"
                );
                ps.setString(1, KodeKomposisi.getText());
                ps.setString(2, NamaStandar.getText());
                ps.setString(3, NamaGenerik.getText().equals("") ? null : NamaGenerik.getText());
                ps.setString(4, Kategori.getText().equals("") ? null : Kategori.getText());
                ps.setString(5, variasiJson);
                ps.setString(6, Kekuatan.getText().equals("") ? null : Kekuatan.getText());
                ps.setString(7, Satuan.getText().equals("") ? null : Satuan.getText());
                ps.setString(8, Status.getSelectedItem().toString());

                ps.executeUpdate();
                ps.close();
                
                JOptionPane.showMessageDialog(null,"Data berhasil disimpan");
                BtnCariActionPerformed(evt);
                emptTeks();
                
            } catch (Exception e) {
                System.out.println("Error simpan: " + e);
                JOptionPane.showMessageDialog(null,"Gagal menyimpan data: " + e.getMessage());
            }
        }
    }                                         

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {                                     
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
            Valid.pindah(evt,VariasiNama,BtnBatal);
        }
    }                                    

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {                                         
        emptTeks();
    }                                        

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {                                    
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            emptTeks();
        }else{Valid.pindah(evt, BtnSimpan, BtnHapus);}
    }                                   

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {                                         
        for(i=0;i<tbKamar.getRowCount();i++){ 
            if(tbKamar.getValueAt(i,0).toString().equals("true")){
                try {
                    ps = koneksi.prepareStatement("DELETE FROM master_komposisi WHERE kode_komposisi=?");
                    ps.setString(1, tbKamar.getValueAt(i,1).toString());
                    ps.executeUpdate();
                    ps.close();
                } catch (Exception e) {
                    System.out.println("Error hapus: " + e);
                }
            }
        } 
        BtnCariActionPerformed(evt);
        emptTeks();
    }                                        

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {                                    
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnHapusActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
    }                                   

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {                                        
        if(KodeKomposisi.getText().trim().equals("")){
            Valid.textKosong(KodeKomposisi,"Kode Komposisi");
        }else if(NamaStandar.getText().trim().equals("")){
            Valid.textKosong(NamaStandar,"Nama Standar");
        }else{
            try {
                // Convert variasi nama ke JSON array
                String variasiJson = "[]";
                if(!VariasiNama.getText().trim().equals("")){
                    String[] variasi = VariasiNama.getText().split(",");
                    ArrayNode arrayNode = mapper.createArrayNode();
                    for(String nama : variasi){
                        arrayNode.add(nama.trim());
                    }
                    variasiJson = mapper.writeValueAsString(arrayNode);
                }

                // Update data
                ps = koneksi.prepareStatement(
                    "UPDATE master_komposisi SET " +
                    "nama_standar=?, nama_generik=?, kategori=?, " +
                    "variasi_nama=?, kekuatan_umum=?, satuan=?, status_aktif=? " +
                    "WHERE kode_komposisi=?"
                );
                ps.setString(1, NamaStandar.getText());
                ps.setString(2, NamaGenerik.getText().equals("") ? null : NamaGenerik.getText());
                ps.setString(3, Kategori.getText().equals("") ? null : Kategori.getText());
                ps.setString(4, variasiJson);
                ps.setString(5, Kekuatan.getText().equals("") ? null : Kekuatan.getText());
                ps.setString(6, Satuan.getText().equals("") ? null : Satuan.getText());
                ps.setString(7, Status.getSelectedItem().toString());
                ps.setString(8, tbKamar.getValueAt(tbKamar.getSelectedRow(),1).toString());

                ps.executeUpdate();
                ps.close();
                
                JOptionPane.showMessageDialog(null,"Data berhasil diubah");
                BtnCariActionPerformed(evt);
                emptTeks();
                
            } catch (Exception e) {
                System.out.println("Error edit: " + e);
                JOptionPane.showMessageDialog(null,"Gagal mengubah data: " + e.getMessage());
            }
        }
    }                                       

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {                                   
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnEditActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
    }                                  

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {                                          
        dispose();
    }                                         

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {                                     
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{Valid.pindah(evt,BtnEdit,TCari);}
    }                                    

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {                                         
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        BtnCariActionPerformed(evt);
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
            Valid.MyReportqry("rptMasterKomposisi.jasper","report","::[ Master Komposisi Obat ]::","select kode_komposisi, nama_standar, nama_generik, kategori, kekuatan_umum, satuan, "+
                "case when status_aktif='1' then 'Aktif' else 'Non-Aktif' end as status "+
                "from master_komposisi where "+
                "kode_komposisi like '%"+TCari.getText().trim()+"%' or "+
                "nama_standar like '%"+TCari.getText().trim()+"%' or "+
                "nama_generik like '%"+TCari.getText().trim()+"%' or "+
                "kategori like '%"+TCari.getText().trim()+"%' "+
                "order by nama_standar",param);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }                                        

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {                                    
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnPrintActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnEdit, BtnKeluar);
        }
    }                                   

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {                                 
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            BtnKeluar.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            tbKamar.requestFocus();
        }
    }                                

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {                                        
        tampil();
    }                                       

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {                                   
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt, TCari, BtnAll);
        }
    }                                  

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {                                       
        TCari.setText("");
        tampil();
    }                                      

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {                                  
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnAllActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnCari, TCari);
        }
    }                                 

    private void tbKamarMouseClicked(java.awt.event.MouseEvent evt) {                                     
        if(tabMode.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }                                    

    private void tbKamarKeyPressed(java.awt.event.KeyEvent evt) {                                   
        if(tabMode.getRowCount()!=0){
            if(evt.getKeyCode()==KeyEvent.VK_SHIFT){
                TCari.setText("");
                TCari.requestFocus();
            }
        }
    }                                  

    private void formWindowOpened(java.awt.event.WindowEvent evt) {                                  
        tampil();
    }                                 

    private void tbKamarKeyReleased(java.awt.event.KeyEvent evt) {                                    
        if(tabMode.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }                                   

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgMasterKomposisi dialog = new DlgMasterKomposisi(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify                     
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.TextBox KodeKomposisi;
    private widget.TextBox NamaStandar;
    private widget.TextBox NamaGenerik;
    private widget.TextBox Kategori;
    private widget.TextBox Kekuatan;
    private widget.TextBox Satuan;
    private widget.ComboBox Status;
    private widget.TextBox VariasiNama;
    private widget.Label LCount;
    private javax.swing.JPopupMenu Popup;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.InternalFrame internalFrame1;
    private javax.swing.JPanel jPanel1;
    private widget.Label label1;
    private widget.Label label2;
    private widget.Label label3;
    private widget.Label label4;
    private widget.Label label5;
    private widget.Label label6;
    private widget.Label label7;
    private widget.Label label8;
    private widget.Label label9;
    private widget.Label label10;
    private widget.panelisi panelisi1;
    private widget.panelisi panelisi3;
    private widget.panelisi panelisi4;
    private javax.swing.JMenuItem ppCetak;
    private javax.swing.JMenuItem ppGanti;
    private javax.swing.JMenuItem ppHapus;
    private javax.swing.JMenuItem ppSimpan;
    private widget.Table tbKamar;
    // End of variables declaration                   

    /**
     * Method untuk menampilkan data master_komposisi
     */
    private void tampil() {
        Valid.tabelKosong(tabMode);
        try{    
            ps=koneksi.prepareStatement(
                "SELECT kode_komposisi, nama_standar, nama_generik, kategori, " +
                "kekuatan_umum, satuan, status_aktif, variasi_nama " +
                "FROM master_komposisi WHERE " +
                "kode_komposisi LIKE ? OR nama_standar LIKE ? OR " +
                "nama_generik LIKE ? OR kategori LIKE ? OR " +
                "JSON_EXTRACT(variasi_nama, ') LIKE ? " +
                "ORDER BY nama_standar"
            );
            try {
                String cari = "%"+TCari.getText().trim()+"%";
                ps.setString(1, cari);
                ps.setString(2, cari);
                ps.setString(3, cari);
                ps.setString(4, cari);
                ps.setString(5, cari);
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        false,
                        rs.getString("kode_komposisi"),
                        rs.getString("nama_standar"),
                        rs.getString("nama_generik") != null ? rs.getString("nama_generik") : "",
                        rs.getString("kategori") != null ? rs.getString("kategori") : "",
                        rs.getString("kekuatan_umum") != null ? rs.getString("kekuatan_umum") : "",
                        rs.getString("satuan") != null ? rs.getString("satuan") : "",
                        rs.getString("status_aktif").equals("1") ? "Aktif" : "Non-Aktif"
                    });
                }                
            } catch (Exception e) {
                System.out.println("Error tampil: " + e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }            
        }catch(SQLException e){
            System.out.println("Notifikasi : "+e);
        }
        LCount.setText(""+tabMode.getRowCount());
    }

    /**
     * Method untuk menampilkan data master_komposisi dengan search di variasi nama
     */
    private void tampilDenganVariasi() {
        Valid.tabelKosong(tabMode);
        try{    
            // Kalau mau search di JSON variasi nama juga, pakai ini
            ps=koneksi.prepareStatement(
                "SELECT kode_komposisi, nama_standar, nama_generik, kategori, " +
                "kekuatan_umum, satuan, status_aktif, variasi_nama " +
                "FROM master_komposisi WHERE " +
                "kode_komposisi LIKE ? OR nama_standar LIKE ? OR " +
                "nama_generik LIKE ? OR kategori LIKE ? OR " +
                "variasi_nama LIKE ? " +  // Simple LIKE search untuk JSON
                "ORDER BY nama_standar"
            );
            try {
                String cari = "%"+TCari.getText().trim()+"%";
                ps.setString(1, cari);
                ps.setString(2, cari);
                ps.setString(3, cari);
                ps.setString(4, cari);
                ps.setString(5, cari);  // Sekarang ada 5 parameter
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        false,
                        rs.getString("kode_komposisi"),
                        rs.getString("nama_standar"),
                        rs.getString("nama_generik") != null ? rs.getString("nama_generik") : "",
                        rs.getString("kategori") != null ? rs.getString("kategori") : "",
                        rs.getString("kekuatan_umum") != null ? rs.getString("kekuatan_umum") : "",
                        rs.getString("satuan") != null ? rs.getString("satuan") : "",
                        rs.getString("status_aktif").equals("1") ? "Aktif" : "Non-Aktif"
                    });
                }                
            } catch (Exception e) {
                System.out.println("Error tampil: " + e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }            
        }catch(SQLException e){
            System.out.println("Notifikasi : "+e);
        }
        LCount.setText(""+tabMode.getRowCount());
    }
    
    public void emptTeks() {
        KodeKomposisi.setText("");
        NamaStandar.setText("");
        NamaGenerik.setText("");
        Kategori.setText("");
        Kekuatan.setText("");
        Satuan.setText("");
        VariasiNama.setText("");
        Status.setSelectedIndex(0);
        KodeKomposisi.requestFocus();        
        
        // Auto generate kode komposisi
        try {
            ps = koneksi.prepareStatement("SELECT CONCAT('KMP', LPAD(IFNULL(MAX(SUBSTRING(kode_komposisi,4)),0)+1,3,'0')) as kode FROM master_komposisi WHERE kode_komposisi LIKE 'KMP%'");
            rs = ps.executeQuery();
            if(rs.next()){
                KodeKomposisi.setText(rs.getString("kode"));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println("Error generate kode: " + e);
        }
    }

    /**
     * Method untuk mengambil data dari tabel ke form
     */
    private void getData() {
        int row=tbKamar.getSelectedRow();
        if(row!= -1){
            try {
                KodeKomposisi.setText(tbKamar.getValueAt(row,1).toString());
                NamaStandar.setText(tbKamar.getValueAt(row,2).toString());
                NamaGenerik.setText(tbKamar.getValueAt(row,3).toString());
                Kategori.setText(tbKamar.getValueAt(row,4).toString());
                Kekuatan.setText(tbKamar.getValueAt(row,5).toString());
                Satuan.setText(tbKamar.getValueAt(row,6).toString());
                Status.setSelectedItem(tbKamar.getValueAt(row,7).toString().equals("Aktif") ? "1" : "0");
                
                // Ambil variasi nama dari database
                ps = koneksi.prepareStatement("SELECT variasi_nama FROM master_komposisi WHERE kode_komposisi=?");
                ps.setString(1, KodeKomposisi.getText());
                rs = ps.executeQuery();
                if(rs.next()){
                    String variasiJson = rs.getString("variasi_nama");
                    if(variasiJson != null && !variasiJson.equals("[]")){
                        try {
                            // Parse JSON array ke string dengan koma
                            com.fasterxml.jackson.databind.JsonNode arrayNode = mapper.readTree(variasiJson);
                            StringBuilder variasi = new StringBuilder();
                            for(com.fasterxml.jackson.databind.JsonNode node : arrayNode){
                                if(variasi.length() > 0) variasi.append(",");
                                variasi.append(node.asText());
                            }
                            VariasiNama.setText(variasi.toString());
                        } catch (Exception e) {
                            VariasiNama.setText("");
                        }
                    } else {
                        VariasiNama.setText("");
                    }
                }
                rs.close();
                ps.close();
                
            } catch (Exception e) {
                System.out.println("Error getData: " + e);
            }
        }
    }

    public JTextField getTextField(){
        return KodeKomposisi;
    }

    public JButton getButton(){
        return BtnKeluar;
    }
    
    /**
     * Method untuk mengatur permission user
     */
    public void isCek(){
        BtnSimpan.setEnabled(akses.getobat());
        BtnHapus.setEnabled(akses.getobat());
        BtnEdit.setEnabled(akses.getobat());
        BtnPrint.setEnabled(akses.getobat());
        
        ppGanti.setEnabled(akses.getobat());        
        ppHapus.setEnabled(akses.getobat());
        ppCetak.setEnabled(akses.getobat());
        ppSimpan.setEnabled(akses.getobat());
    }
    
    /**
     * Method untuk standardisasi data existing di databarang
     */
    public void standardisasiDataBarang() {
        try {
            int konfirmasi = JOptionPane.showConfirmDialog(null, 
                "Apakah Anda yakin ingin melakukan standardisasi komposisi obat?\n" +
                "Proses ini akan mengubah data di tabel databarang.", 
                "Konfirmasi Standardisasi", 
                JOptionPane.YES_NO_OPTION);
                
            if(konfirmasi == JOptionPane.YES_OPTION) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                
                ps = koneksi.prepareStatement(
                    "UPDATE databarang SET letak_barang = cari_komposisi_standar(letak_barang) " +
                    "WHERE letak_barang IS NOT NULL"
                );
                int updated = ps.executeUpdate();
                ps.close();
                
                JOptionPane.showMessageDialog(null, 
                    "Standardisasi selesai!\n" + 
                    "Berhasil memproses " + updated + " record.", 
                    "Standardisasi Berhasil", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
                this.setCursor(Cursor.getDefaultCursor());
            }
        } catch (Exception e) {
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(null, 
                "Error dalam standardisasi: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}