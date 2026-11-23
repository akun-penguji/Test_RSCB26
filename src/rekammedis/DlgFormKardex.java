package rekammedis;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import kepegawaian.DlgCariPetugas;
import kepegawaian.DlgCariDokter;

/**
 * Form Kardex (Kartu Dekat) - Daftar Instruksi Medis Farmakologis
 * RSUD Cabangbungin, Kabupaten Bekasi
 * 
 * @author Your Name
 */
public class DlgFormKardex extends javax.swing.JDialog {
    private final DefaultTableModel tabModeKardex;
    private final DefaultTableModel tabModeObat;
    private final DefaultTableModel tabModePemberian;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    
    private String idKardexAktif = "";
    private String idObatAktif = "";
    private boolean isEditMode = false;
    
    private DlgCariPetugas petugas1 = new DlgCariPetugas(null, false);
    private DlgCariPetugas petugas2 = new DlgCariPetugas(null, false);
    private DlgCariDokter dokter = new DlgCariDokter(null, false);

    /**
     * Creates new form DlgFormKardex
     */
    public DlgFormKardex(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        this.setLocation(8, 1);
        this.setSize(1100, 680);

        // Initialize table models
        tabModeKardex = new DefaultTableModel(null, new Object[]{
            "ID", "No. Rawat", "No. RM", "Nama Pasien", "Ruang Rawat", "DPJP", "Tgl. Input"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbKardex.setModel(tabModeKardex);
        tbKardex.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbKardex.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        tabModeObat = new DefaultTableModel(null, new Object[]{
            "ID", "Nama Obat", "Dosis", "Frekuensi", "Kecepatan", "Rute", 
            "Kode AB", "Tgl. Mulai", "Tgl. Stop", "Dokter Order", "Review Farmasi", 
            "Jenis Obat", "Status"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbObat.setModel(tabModeObat);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        tabModePemberian = new DefaultTableModel(null, new Object[]{
            "ID", "Tgl. Pemberian", "Jam", "PSN/KLG", "Petugas 1", "Petugas 2", 
            "Status", "Catatan"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbPemberian.setModel(tabModePemberian);
        tbPemberian.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbPemberian.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Set column widths
        setColumnWidths();
        
        // Set document size limits
        setDocumentLimits();
        
        // Initialize components
        initAfterComponents();
        
        // Load initial data
        tampilKardex();
    }

    private void setColumnWidths() {
        // Kardex table columns
        int[] kardexWidths = {50, 130, 80, 200, 150, 200, 100};
        for (int i = 0; i < kardexWidths.length; i++) {
            TableColumn column = tbKardex.getColumnModel().getColumn(i);
            column.setPreferredWidth(kardexWidths[i]);
        }

        // Obat table columns
        int[] obatWidths = {50, 200, 100, 100, 100, 80, 80, 100, 100, 150, 200, 100, 80};
        for (int i = 0; i < obatWidths.length; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            column.setPreferredWidth(obatWidths[i]);
        }

        // Pemberian table columns
        int[] pemberianWidths = {50, 120, 80, 100, 150, 150, 100, 200};
        for (int i = 0; i < pemberianWidths.length; i++) {
            TableColumn column = tbPemberian.getColumnModel().getColumn(i);
            column.setPreferredWidth(pemberianWidths[i]);
        }
    }

    private void setDocumentLimits() {
        TNoRW.setDocument(new batasInput((byte) 17).getKata(TNoRW));
        TNoRM.setDocument(new batasInput((byte) 15).getKata(TNoRM));
        TNmPasien.setDocument(new batasInput((byte) 100).getKata(TNmPasien));
        TRuangRawat.setDocument(new batasInput((byte) 50).getKata(TRuangRawat));
        TNmDPJP.setDocument(new batasInput((byte) 100).getKata(TNmDPJP));
        TNipDPJP.setDocument(new batasInput((byte) 20).getKata(TNipDPJP));
        TBeratBadan.setDocument(new batasInput((byte) 10).getKata(TBeratBadan));
        TRiwayatAlergi.setDocument(new batasInput((byte) 200).getKata(TRiwayatAlergi));
        
        TNamaObat.setDocument(new batasInput((byte) 150).getKata(TNamaObat));
        TDosis.setDocument(new batasInput((byte) 50).getKata(TDosis));
        TFrekuensi.setDocument(new batasInput((byte) 50).getKata(TFrekuensi));
        TKecepatan.setDocument(new batasInput((byte) 50).getKata(TKecepatan));
        TRute.setDocument(new batasInput((byte) 50).getKata(TRute));
        TKodeAB.setDocument(new batasInput((byte) 20).getKata(TKodeAB));
        TNmDokter.setDocument(new batasInput((byte) 100).getKata(TNmDokter));
        TNipDokter.setDocument(new batasInput((byte) 20).getKata(TNipDokter));
        TReviewFarmasi.setDocument(new batasInput((byte) 200).getKata(TReviewFarmasi));
        
        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));
    }

    private void initAfterComponents() {
        try {
            // Initialize dialog petugas
            petugas1.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {}
                @Override
                public void windowClosing(WindowEvent e) {}
                @Override
                public void windowClosed(WindowEvent e) {
                    if (petugas1.getTable().getSelectedRow() != -1) {
                        TNipPetugas1.setText(petugas1.getTable().getValueAt(petugas1.getTable().getSelectedRow(), 0).toString());
                        TNmPetugas1.setText(petugas1.getTable().getValueAt(petugas1.getTable().getSelectedRow(), 1).toString());
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

            petugas2.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {}
                @Override
                public void windowClosing(WindowEvent e) {}
                @Override
                public void windowClosed(WindowEvent e) {
                    if (petugas2.getTable().getSelectedRow() != -1) {
                        TNipPetugas2.setText(petugas2.getTable().getValueAt(petugas2.getTable().getSelectedRow(), 0).toString());
                        TNmPetugas2.setText(petugas2.getTable().getValueAt(petugas2.getTable().getSelectedRow(), 1).toString());
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

            dokter.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {}
                @Override
                public void windowClosing(WindowEvent e) {}
                @Override
                public void windowClosed(WindowEvent e) {
                    if (dokter.getTable().getSelectedRow() != -1) {
                        TNipDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                        TNmDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
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
            
            // Set initial button states
            BtnEditObat.setEnabled(false);
            BtnHapusObat.setEnabled(false);
            BtnSimpanPemberian.setEnabled(false);
            
        } catch (Exception e) {
            System.out.println("Error in initAfterComponents: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnEditObat = new javax.swing.JMenuItem();
        MnHapusObat = new javax.swing.JMenuItem();
        MnReviewFarmasi = new javax.swing.JMenuItem();
        
        internalFrame1 = new widget.InternalFrame();
        TabRawat = new javax.swing.JTabbedPane();
        
        // Panel Kardex
        internalFrame2 = new widget.InternalFrame();
        Scroll1 = new widget.ScrollPane();
        tbKardex = new widget.Table();
        panelGlass9 = new widget.panelisi();
        jLabel6 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel7 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel8 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        BtnTambah = new widget.Button();
        BtnHapusKardex = new widget.Button();
        BtnCetak = new widget.Button();
        BtnKeluar = new widget.Button();
        
        // Panel Input Data Pasien
        panelGlass8 = new widget.panelisi();
        jLabel3 = new widget.Label();
        TNoRW = new widget.TextBox();
        jLabel4 = new widget.Label();
        TNoRM = new widget.TextBox();
        jLabel5 = new widget.Label();
        TNmPasien = new widget.TextBox();
        jLabel9 = new widget.Label();
        TRuangRawat = new widget.TextBox();
        jLabel10 = new widget.Label();
        TNmDPJP = new widget.TextBox();
        TNipDPJP = new widget.TextBox();
        BtnDokter = new widget.Button();
        jLabel11 = new widget.Label();
        TBeratBadan = new widget.TextBox();
        jLabel12 = new widget.Label();
        tglLahir = new widget.Tanggal();
        jLabel13 = new widget.Label();
        TRiwayatAlergi = new widget.TextBox();
        
        // Panel Data Obat
        internalFrame3 = new widget.InternalFrame();
        Scroll2 = new widget.ScrollPane();
        tbObat = new widget.Table();
        panelGlass10 = new widget.panelisi();
        jLabel14 = new widget.Label();
        CmbJenisObat = new widget.ComboBox();
        jLabel15 = new widget.Label();
        TNamaObat = new widget.TextBox();
        jLabel16 = new widget.Label();
        TDosis = new widget.TextBox();
        jLabel17 = new widget.Label();
        TFrekuensi = new widget.TextBox();
        jLabel18 = new widget.Label();
        TKecepatan = new widget.TextBox();
        jLabel19 = new widget.Label();
        TRute = new widget.TextBox();
        jLabel20 = new widget.Label();
        TKodeAB = new widget.TextBox();
        jLabel21 = new widget.Label();
        tglMulai = new widget.Tanggal();
        jLabel22 = new widget.Label();
        tglStop = new widget.Tanggal();
        jLabel23 = new widget.Label();
        TNmDokter = new widget.TextBox();
        TNipDokter = new widget.TextBox();
        BtnDokterOrder = new widget.Button();
        jLabel24 = new widget.Label();
        TReviewFarmasi = new widget.TextBox();
        BtnSimpanObat = new widget.Button();
        BtnEditObat = new widget.Button();
        BtnHapusObat = new widget.Button();
        BtnBatalObat = new widget.Button();
        
        // Panel Pemberian Obat
        internalFrame4 = new widget.InternalFrame();
        Scroll3 = new widget.ScrollPane();
        tbPemberian = new widget.Table();
        panelGlass11 = new widget.panelisi();
        jLabel25 = new widget.Label();
        tglPemberian = new widget.Tanggal();
        jLabel26 = new widget.Label();
        TJamPemberian = new widget.TextBox();
        jLabel27 = new widget.Label();
        TPsnKlg = new widget.TextBox();
        jLabel28 = new widget.Label();
        TNmPetugas1 = new widget.TextBox();
        TNipPetugas1 = new widget.TextBox();
        BtnPetugas1 = new widget.Button();
        jLabel29 = new widget.Label();
        TNmPetugas2 = new widget.TextBox();
        TNipPetugas2 = new widget.TextBox();
        BtnPetugas2 = new widget.Button();
        jLabel30 = new widget.Label();
        CmbStatusPemberian = new widget.ComboBox();
        jLabel31 = new widget.Label();
        TCatatanPemberian = new widget.TextBox();
        BtnSimpanPemberian = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Form Kardex (Kartu Dekat) - Daftar Instruksi Medis Farmakologis ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        TabRawat.setBackground(new java.awt.Color(255, 255, 254));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.setName("TabRawat"); // NOI18N

        // ===== TAB 1: DATA KARDEX =====
        internalFrame2.setBorder(null);
        internalFrame2.setName("internalFrame2"); // NOI18N
        internalFrame2.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll1.setName("Scroll1"); // NOI18N
        Scroll1.setOpaque(true);

        tbKardex.setAutoCreateRowSorter(true);
        tbKardex.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbKardex.setName("tbKardex"); // NOI18N
        tbKardex.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbKardexMouseClicked(evt);
            }
        });
        tbKardex.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbKardexKeyPressed(evt);
            }
        });
        Scroll1.setViewportView(tbKardex);

        internalFrame2.add(Scroll1, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel6.setText("Tanggal :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel6);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "30-09-2025" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari1);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("s.d.");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(30, 23));
        panelGlass9.add(jLabel7);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "30-09-2025" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari2);

        jLabel8.setText("Key Word :");
        jLabel8.setName("jLabel8"); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel8);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(250, 23));
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

        BtnTambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        BtnTambah.setMnemonic('3');
        BtnTambah.setText("Tambah");
        BtnTambah.setToolTipText("Alt+3");
        BtnTambah.setName("BtnTambah"); // NOI18N
        BtnTambah.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTambahActionPerformed(evt);
            }
        });
        panelGlass9.add(BtnTambah);

        BtnHapusKardex.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapusKardex.setMnemonic('H');
        BtnHapusKardex.setText("Hapus");
        BtnHapusKardex.setToolTipText("Alt+H");
        BtnHapusKardex.setName("BtnHapusKardex"); // NOI18N
        BtnHapusKardex.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapusKardex.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusKardexActionPerformed(evt);
            }
        });
        panelGlass9.add(BtnHapusKardex);

        BtnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
        BtnCetak.setMnemonic('T');
        BtnCetak.setText("Cetak");
        BtnCetak.setToolTipText("Alt+T");
        BtnCetak.setName("BtnCetak"); // NOI18N
        BtnCetak.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCetakActionPerformed(evt);
            }
        });
        panelGlass9.add(BtnCetak);

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
        panelGlass9.add(BtnKeluar);

        internalFrame2.add(panelGlass9, java.awt.BorderLayout.PAGE_END);

        // Panel Input Data Pasien
        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 120));
        panelGlass8.setLayout(null);

        jLabel3.setText("No. Rawat :");
        jLabel3.setName("jLabel3"); // NOI18N
        panelGlass8.add(jLabel3);
        jLabel3.setBounds(10, 10, 90, 23);

        TNoRW.setHighlighter(null);
        TNoRW.setName("TNoRW"); // NOI18N
        panelGlass8.add(TNoRW);
        TNoRW.setBounds(100, 10, 150, 23);

        jLabel4.setText("No. RM :");
        jLabel4.setName("jLabel4"); // NOI18N
        panelGlass8.add(jLabel4);
        jLabel4.setBounds(260, 10, 60, 23);

        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        panelGlass8.add(TNoRM);
        TNoRM.setBounds(320, 10, 120, 23);

        jLabel5.setText("Nama Pasien :");
        jLabel5.setName("jLabel5"); // NOI18N
        panelGlass8.add(jLabel5);
        jLabel5.setBounds(450, 10, 90, 23);

        TNmPasien.setHighlighter(null);
        TNmPasien.setName("TNmPasien"); // NOI18N
        panelGlass8.add(TNmPasien);
        TNmPasien.setBounds(540, 10, 250, 23);

        jLabel9.setText("Ruang Rawat :");
        jLabel9.setName("jLabel9"); // NOI18N
        panelGlass8.add(jLabel9);
        jLabel9.setBounds(10, 40, 90, 23);

        TRuangRawat.setHighlighter(null);
        TRuangRawat.setName("TRuangRawat"); // NOI18N
        panelGlass8.add(TRuangRawat);
        TRuangRawat.setBounds(100, 40, 200, 23);

        jLabel10.setText("DPJP :");
        jLabel10.setName("jLabel10"); // NOI18N
        panelGlass8.add(jLabel10);
        jLabel10.setBounds(310, 40, 50, 23);

        TNmDPJP.setEditable(false);
        TNmDPJP.setHighlighter(null);
        TNmDPJP.setName("TNmDPJP"); // NOI18N
        panelGlass8.add(TNmDPJP);
        TNmDPJP.setBounds(360, 40, 200, 23);

        TNipDPJP.setEditable(false);
        TNipDPJP.setHighlighter(null);
        TNipDPJP.setName("TNipDPJP"); // NOI18N
        panelGlass8.add(TNipDPJP);
        TNipDPJP.setBounds(565, 40, 100, 23);

        BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokter.setMnemonic('X');
        BtnDokter.setToolTipText("Alt+X");
        BtnDokter.setName("BtnDokter"); // NOI18N
        BtnDokter.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokterActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnDokter);
        BtnDokter.setBounds(670, 40, 28, 23);

        jLabel11.setText("Berat Badan :");
        jLabel11.setName("jLabel11"); // NOI18N
        panelGlass8.add(jLabel11);
        jLabel11.setBounds(10, 70, 90, 23);

        TBeratBadan.setHighlighter(null);
        TBeratBadan.setName("TBeratBadan"); // NOI18N
        panelGlass8.add(TBeratBadan);
        TBeratBadan.setBounds(100, 70, 80, 23);

        jLabel12.setText("Tgl. Lahir :");
        jLabel12.setName("jLabel12"); // NOI18N
        panelGlass8.add(jLabel12);
        jLabel12.setBounds(190, 70, 70, 23);

        tglLahir.setForeground(new java.awt.Color(50, 70, 50));
        tglLahir.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "30-09-2025" }));
        tglLahir.setDisplayFormat("dd-MM-yyyy");
        tglLahir.setName("tglLahir"); // NOI18N
        tglLahir.setOpaque(false);
        panelGlass8.add(tglLahir);
        tglLahir.setBounds(260, 70, 110, 23);

        jLabel13.setText("Riwayat Alergi :");
        jLabel13.setName("jLabel13"); // NOI18N
        panelGlass8.add(jLabel13);
        jLabel13.setBounds(380, 70, 100, 23);

        TRiwayatAlergi.setBackground(new java.awt.Color(255, 255, 153));
        TRiwayatAlergi.setHighlighter(null);
        TRiwayatAlergi.setName("TRiwayatAlergi"); // NOI18N
        panelGlass8.add(TRiwayatAlergi);
        TRiwayatAlergi.setBounds(480, 70, 310, 23);

        internalFrame2.add(panelGlass8, java.awt.BorderLayout.PAGE_START);

        TabRawat.addTab("Data Kardex", internalFrame2);

        // ===== TAB 2: DATA OBAT =====
        internalFrame3.setBorder(null);
        internalFrame3.setName("internalFrame3"); // NOI18N
        internalFrame3.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll2.setName("Scroll2"); // NOI18N
        Scroll2.setOpaque(true);

        tbObat.setAutoCreateRowSorter(true);
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
        Scroll2.setViewportView(tbObat);

        internalFrame3.add(Scroll2, java.awt.BorderLayout.CENTER);

        panelGlass10.setName("panelGlass10"); // NOI18N
        panelGlass10.setPreferredSize(new java.awt.Dimension(44, 150));
        panelGlass10.setLayout(null);

        jLabel14.setText("Jenis Obat :");
        jLabel14.setName("jLabel14"); // NOI18N
        panelGlass10.add(jLabel14);
        jLabel14.setBounds(10, 10, 90, 23);

        CmbJenisObat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Parenteral", "Oral" }));
        CmbJenisObat.setName("CmbJenisObat"); // NOI18N
        panelGlass10.add(CmbJenisObat);
        CmbJenisObat.setBounds(100, 10, 120, 23);

        jLabel15.setText("Nama Obat :");
        jLabel15.setName("jLabel15"); // NOI18N
        panelGlass10.add(jLabel15);
        jLabel15.setBounds(230, 10, 80, 23);

        TNamaObat.setHighlighter(null);
        TNamaObat.setName("TNamaObat"); // NOI18N
        panelGlass10.add(TNamaObat);
        TNamaObat.setBounds(310, 10, 250, 23);

        jLabel16.setText("Dosis :");
        jLabel16.setName("jLabel16"); // NOI18N
        panelGlass10.add(jLabel16);
        jLabel16.setBounds(570, 10, 50, 23);

        TDosis.setHighlighter(null);
        TDosis.setName("TDosis"); // NOI18N
        panelGlass10.add(TDosis);
        TDosis.setBounds(620, 10, 150, 23);

        jLabel17.setText("Frekuensi :");
        jLabel17.setName("jLabel17"); // NOI18N
        panelGlass10.add(jLabel17);
        jLabel17.setBounds(10, 40, 90, 23);

        TFrekuensi.setHighlighter(null);
        TFrekuensi.setName("TFrekuensi"); // NOI18N
        panelGlass10.add(TFrekuensi);
        TFrekuensi.setBounds(100, 40, 120, 23);

        jLabel18.setText("Kecepatan :");
        jLabel18.setName("jLabel18"); // NOI18N
        panelGlass10.add(jLabel18);
        jLabel18.setBounds(230, 40, 80, 23);

        TKecepatan.setHighlighter(null);
        TKecepatan.setName("TKecepatan"); // NOI18N
        panelGlass10.add(TKecepatan);
        TKecepatan.setBounds(310, 40, 120, 23);

        jLabel19.setText("Rute :");
        jLabel19.setName("jLabel19"); // NOI18N
        panelGlass10.add(jLabel19);
        jLabel19.setBounds(440, 40, 50, 23);

        TRute.setHighlighter(null);
        TRute.setName("TRute"); // NOI18N
        panelGlass10.add(TRute);
        TRute.setBounds(490, 40, 120, 23);

        jLabel20.setText("Kode AB :");
        jLabel20.setName("jLabel20"); // NOI18N
        panelGlass10.add(jLabel20);
        jLabel20.setBounds(620, 40, 70, 23);

        TKodeAB.setHighlighter(null);
        TKodeAB.setName("TKodeAB"); // NOI18N
        panelGlass10.add(TKodeAB);
        TKodeAB.setBounds(690, 40, 80, 23);

        jLabel21.setText("Tgl. Mulai :");
        jLabel21.setName("jLabel21"); // NOI18N
        panelGlass10.add(jLabel21);
        jLabel21.setBounds(10, 70, 90, 23);

        tglMulai.setForeground(new java.awt.Color(50, 70, 50));
        tglMulai.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "30-09-2025" }));
        tglMulai.setDisplayFormat("dd-MM-yyyy");
        tglMulai.setName("tglMulai"); // NOI18N
        tglMulai.setOpaque(false);
        panelGlass10.add(tglMulai);
        tglMulai.setBounds(100, 70, 110, 23);

        jLabel22.setText("Tgl. Stop :");
        jLabel22.setName("jLabel22"); // NOI18N
        panelGlass10.add(jLabel22);
        jLabel22.setBounds(220, 70, 70, 23);

        tglStop.setForeground(new java.awt.Color(50, 70, 50));
        tglStop.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "30-09-2025" }));
        tglStop.setDisplayFormat("dd-MM-yyyy");
        tglStop.setName("tglStop"); // NOI18N
        tglStop.setOpaque(false);
        panelGlass10.add(tglStop);
        tglStop.setBounds(290, 70, 110, 23);

        jLabel23.setText("Dokter Order :");
        jLabel23.setName("jLabel23"); // NOI18N
        panelGlass10.add(jLabel23);
        jLabel23.setBounds(410, 70, 90, 23);

        TNmDokter.setEditable(false);
        TNmDokter.setHighlighter(null);
        TNmDokter.setName("TNmDokter"); // NOI18N
        panelGlass10.add(TNmDokter);
        TNmDokter.setBounds(500, 70, 180, 23);

        TNipDokter.setEditable(false);
        TNipDokter.setHighlighter(null);
        TNipDokter.setName("TNipDokter"); // NOI18N
        panelGlass10.add(TNipDokter);
        TNipDokter.setBounds(685, 70, 100, 23);

        BtnDokterOrder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokterOrder.setMnemonic('X');
        BtnDokterOrder.setToolTipText("Alt+X");
        BtnDokterOrder.setName("BtnDokterOrder"); // NOI18N
        BtnDokterOrder.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnDokterOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokterOrderActionPerformed(evt);
            }
        });
        panelGlass10.add(BtnDokterOrder);
        BtnDokterOrder.setBounds(790, 70, 28, 23);

        jLabel24.setText("Review Farmasi :");
        jLabel24.setName("jLabel24"); // NOI18N
        panelGlass10.add(jLabel24);
        jLabel24.setBounds(10, 100, 100, 23);

        TReviewFarmasi.setHighlighter(null);
        TReviewFarmasi.setName("TReviewFarmasi"); // NOI18N
        panelGlass10.add(TReviewFarmasi);
        TReviewFarmasi.setBounds(110, 100, 400, 23);

        BtnSimpanObat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpanObat.setMnemonic('S');
        BtnSimpanObat.setText("Simpan");
        BtnSimpanObat.setToolTipText("Alt+S");
        BtnSimpanObat.setName("BtnSimpanObat"); // NOI18N
        BtnSimpanObat.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpanObat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanObatActionPerformed(evt);
            }
        });
        panelGlass10.add(BtnSimpanObat);
        BtnSimpanObat.setBounds(520, 100, 100, 30);

        BtnEditObat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        BtnEditObat.setMnemonic('E');
        BtnEditObat.setText("Edit");
        BtnEditObat.setToolTipText("Alt+E");
        BtnEditObat.setName("BtnEditObat"); // NOI18N
        BtnEditObat.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEditObat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEditObatActionPerformed(evt);
            }
        });
        panelGlass10.add(BtnEditObat);
        BtnEditObat.setBounds(625, 100, 100, 30);

        BtnHapusObat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapusObat.setMnemonic('H');
        BtnHapusObat.setText("Hapus");
        BtnHapusObat.setToolTipText("Alt+H");
        BtnHapusObat.setName("BtnHapusObat"); // NOI18N
        BtnHapusObat.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapusObat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusObatActionPerformed(evt);
            }
        });
        panelGlass10.add(BtnHapusObat);
        BtnHapusObat.setBounds(730, 100, 100, 30);

        BtnBatalObat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); // NOI18N
        BtnBatalObat.setMnemonic('B');
        BtnBatalObat.setText("Batal");
        BtnBatalObat.setToolTipText("Alt+B");
        BtnBatalObat.setName("BtnBatalObat"); // NOI18N
        BtnBatalObat.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBatalObat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBatalObatActionPerformed(evt);
            }
        });
        panelGlass10.add(BtnBatalObat);
        BtnBatalObat.setBounds(835, 100, 100, 30);

        internalFrame3.add(panelGlass10, java.awt.BorderLayout.PAGE_END);

        TabRawat.addTab("Data Obat", internalFrame3);

        // ===== TAB 3: PENCATATAN PEMBERIAN =====
        internalFrame4.setBorder(null);
        internalFrame4.setName("internalFrame4"); // NOI18N
        internalFrame4.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll3.setName("Scroll3"); // NOI18N
        Scroll3.setOpaque(true);

        tbPemberian.setAutoCreateRowSorter(true);
        tbPemberian.setToolTipText("Silahkan klik untuk memilih data yang mau dilihat");
        tbPemberian.setName("tbPemberian"); // NOI18N
        Scroll3.setViewportView(tbPemberian);

        internalFrame4.add(Scroll3, java.awt.BorderLayout.CENTER);

        panelGlass11.setName("panelGlass11"); // NOI18N
        panelGlass11.setPreferredSize(new java.awt.Dimension(44, 120));
        panelGlass11.setLayout(null);

        jLabel25.setText("Tgl. Pemberian :");
        jLabel25.setName("jLabel25"); // NOI18N
        panelGlass11.add(jLabel25);
        jLabel25.setBounds(10, 10, 100, 23);

        tglPemberian.setForeground(new java.awt.Color(50, 70, 50));
        tglPemberian.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "30-09-2025" }));
        tglPemberian.setDisplayFormat("dd-MM-yyyy");
        tglPemberian.setName("tglPemberian"); // NOI18N
        tglPemberian.setOpaque(false);
        panelGlass11.add(tglPemberian);
        tglPemberian.setBounds(110, 10, 110, 23);

        jLabel26.setText("Jam :");
        jLabel26.setName("jLabel26"); // NOI18N
        panelGlass11.add(jLabel26);
        jLabel26.setBounds(230, 10, 40, 23);

        TJamPemberian.setHighlighter(null);
        TJamPemberian.setName("TJamPemberian"); // NOI18N
        panelGlass11.add(TJamPemberian);
        TJamPemberian.setBounds(270, 10, 80, 23);

        jLabel27.setText("PSN/KLG :");
        jLabel27.setName("jLabel27"); // NOI18N
        panelGlass11.add(jLabel27);
        jLabel27.setBounds(360, 10, 70, 23);

        TPsnKlg.setHighlighter(null);
        TPsnKlg.setName("TPsnKlg"); // NOI18N
        panelGlass11.add(TPsnKlg);
        TPsnKlg.setBounds(430, 10, 100, 23);

        jLabel28.setText("Petugas 1 :");
        jLabel28.setName("jLabel28"); // NOI18N
        panelGlass11.add(jLabel28);
        jLabel28.setBounds(10, 40, 100, 23);

        TNmPetugas1.setEditable(false);
        TNmPetugas1.setHighlighter(null);
        TNmPetugas1.setName("TNmPetugas1"); // NOI18N
        panelGlass11.add(TNmPetugas1);
        TNmPetugas1.setBounds(110, 40, 200, 23);

        TNipPetugas1.setEditable(false);
        TNipPetugas1.setHighlighter(null);
        TNipPetugas1.setName("TNipPetugas1"); // NOI18N
        panelGlass11.add(TNipPetugas1);
        TNipPetugas1.setBounds(315, 40, 100, 23);

        BtnPetugas1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPetugas1.setMnemonic('1');
        BtnPetugas1.setToolTipText("Alt+1");
        BtnPetugas1.setName("BtnPetugas1"); // NOI18N
        BtnPetugas1.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnPetugas1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPetugas1ActionPerformed(evt);
            }
        });
        panelGlass11.add(BtnPetugas1);
        BtnPetugas1.setBounds(420, 40, 28, 23);

        jLabel29.setText("Petugas 2 :");
        jLabel29.setName("jLabel29"); // NOI18N
        panelGlass11.add(jLabel29);
        jLabel29.setBounds(458, 40, 80, 23);

        TNmPetugas2.setEditable(false);
        TNmPetugas2.setHighlighter(null);
        TNmPetugas2.setName("TNmPetugas2"); // NOI18N
        panelGlass11.add(TNmPetugas2);
        TNmPetugas2.setBounds(538, 40, 200, 23);

        TNipPetugas2.setEditable(false);
        TNipPetugas2.setHighlighter(null);
        TNipPetugas2.setName("TNipPetugas2"); // NOI18N
        panelGlass11.add(TNipPetugas2);
        TNipPetugas2.setBounds(743, 40, 100, 23);

        BtnPetugas2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPetugas2.setMnemonic('2');
        BtnPetugas2.setToolTipText("Alt+2");
        BtnPetugas2.setName("BtnPetugas2"); // NOI18N
        BtnPetugas2.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnPetugas2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPetugas2ActionPerformed(evt);
            }
        });
        panelGlass11.add(BtnPetugas2);
        BtnPetugas2.setBounds(848, 40, 28, 23);

        jLabel30.setText("Status :");
        jLabel30.setName("jLabel30"); // NOI18N
        panelGlass11.add(jLabel30);
        jLabel30.setBounds(10, 70, 100, 23);

        CmbStatusPemberian.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "diberikan", "tidak_diberikan", "ditunda" }));
        CmbStatusPemberian.setName("CmbStatusPemberian"); // NOI18N
        panelGlass11.add(CmbStatusPemberian);
        CmbStatusPemberian.setBounds(110, 70, 140, 23);

        jLabel31.setText("Catatan :");
        jLabel31.setName("jLabel31"); // NOI18N
        panelGlass11.add(jLabel31);
        jLabel31.setBounds(260, 70, 60, 23);

        TCatatanPemberian.setHighlighter(null);
        TCatatanPemberian.setName("TCatatanPemberian"); // NOI18N
        panelGlass11.add(TCatatanPemberian);
        TCatatanPemberian.setBounds(320, 70, 300, 23);

        BtnSimpanPemberian.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpanPemberian.setMnemonic('S');
        BtnSimpanPemberian.setText("Simpan");
        BtnSimpanPemberian.setToolTipText("Alt+S");
        BtnSimpanPemberian.setName("BtnSimpanPemberian"); // NOI18N
        BtnSimpanPemberian.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpanPemberian.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanPemberianActionPerformed(evt);
            }
        });
        panelGlass11.add(BtnSimpanPemberian);
        BtnSimpanPemberian.setBounds(630, 70, 100, 30);

        internalFrame4.add(panelGlass11, java.awt.BorderLayout.PAGE_END);

        TabRawat.addTab("Pencatatan Pemberian", internalFrame4);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);

        // Popup Menu
        MnEditObat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnEditObat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        MnEditObat.setText("Edit Obat");
        MnEditObat.setName("MnEditObat"); // NOI18N
        MnEditObat.setPreferredSize(new java.awt.Dimension(200, 26));
        MnEditObat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnEditObatActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnEditObat);

        MnHapusObat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnHapusObat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        MnHapusObat.setText("Hapus Obat");
        MnHapusObat.setName("MnHapusObat"); // NOI18N
        MnHapusObat.setPreferredSize(new java.awt.Dimension(200, 26));
        MnHapusObat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnHapusObatActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnHapusObat);

        MnReviewFarmasi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnReviewFarmasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnReviewFarmasi.setText("Review Farmasi");
        MnReviewFarmasi.setName("MnReviewFarmasi"); // NOI18N
        MnReviewFarmasi.setPreferredSize(new java.awt.Dimension(200, 26));
        MnReviewFarmasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnReviewFarmasiActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnReviewFarmasi);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>                        

    // ===== EVENT HANDLERS =====
    
    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {                                          
        dispose();
    }                                         

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {                                        
        tampilKardex();
    }                                       

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {                                   
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnCariActionPerformed(null);
        } else {
            Valid.pindah(evt, TCari, BtnAll);
        }
    }                                  

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {                                       
        TCari.setText("");
        tampilKardex();
    }                                      

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {                                  
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            TCari.setText("");
            tampilKardex();
        } else {
            Valid.pindah(evt, BtnCari, TCari);
        }
    }                                 

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {                                 
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnCari.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            BtnKeluar.requestFocus();
        }
    }                                

    private void tbKardexMouseClicked(java.awt.event.MouseEvent evt) {                                      
        if (tabModeKardex.getRowCount() != 0) {
            try {
                getDataKardex();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }                                     

    private void tbKardexKeyPressed(java.awt.event.KeyEvent evt) {                                    
        if (tabModeKardex.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                try {
                    getDataKardex();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }                                   

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {                                    
        if (tabModeObat.getRowCount() != 0) {
            try {
                getDataObat();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }                                   

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {                                  
        if (tabModeObat.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                try {
                    getDataObat();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }                                 

    private void BtnTambahActionPerformed(java.awt.event.ActionEvent evt) {                                          
        emptTeks();
        TabRawat.setSelectedIndex(1);
    }                                         

    private void BtnDokterActionPerformed(java.awt.event.ActionEvent evt) {                                          
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setAlwaysOnTop(false);
        dokter.setVisible(true);
    }                                         

    private void BtnDokterOrderActionPerformed(java.awt.event.ActionEvent evt) {                                               
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setAlwaysOnTop(false);
        dokter.setVisible(true);
    }                                              

    private void BtnPetugas1ActionPerformed(java.awt.event.ActionEvent evt) {                                            
        petugas1.isCek();
        petugas1.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas1.setLocationRelativeTo(internalFrame1);
        petugas1.setAlwaysOnTop(false);
        petugas1.setVisible(true);
    }                                           

    private void BtnPetugas2ActionPerformed(java.awt.event.ActionEvent evt) {                                            
        petugas2.isCek();
        petugas2.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas2.setLocationRelativeTo(internalFrame1);
        petugas2.setAlwaysOnTop(false);
        petugas2.setVisible(true);
    }                                           

    private void BtnSimpanObatActionPerformed(java.awt.event.ActionEvent evt) {                                              
        simpanObat();
    }                                             

    private void BtnEditObatActionPerformed(java.awt.event.ActionEvent evt) {                                            
        editObat();
    }                                           

    private void BtnHapusObatActionPerformed(java.awt.event.ActionEvent evt) {                                             
        hapusObat();
    }                                            

    private void BtnBatalObatActionPerformed(java.awt.event.ActionEvent evt) {                                             
        clearInputObat();
    }                                            

    private void BtnSimpanPemberianActionPerformed(java.awt.event.ActionEvent evt) {                                                   
        simpanPemberian();
    }                                                  

    private void BtnHapusKardexActionPerformed(java.awt.event.ActionEvent evt) {                                               
        hapusKardex();
    }                                              

    private void BtnCetakActionPerformed(java.awt.event.ActionEvent evt) {                                         
        cetakKardex();
    }                                        

    private void MnEditObatActionPerformed(java.awt.event.ActionEvent evt) {                                           
        editObat();
    }                                          

    private void MnHapusObatActionPerformed(java.awt.event.ActionEvent evt) {                                            
        hapusObat();
    }                                           

    private void MnReviewFarmasiActionPerformed(java.awt.event.ActionEvent evt) {                                                
        simpanReviewFarmasi();
    }                                               

    // ===== METHODS =====
    
    public void emptTeks() {
        idKardexAktif = "";
        TNoRW.setText("");
        TNoRM.setText("");
        TNmPasien.setText("");
        TRuangRawat.setText("");
        TNmDPJP.setText("");
        TNipDPJP.setText("");
        TBeratBadan.setText("");
        tglLahir.setDate(new Date());
        TRiwayatAlergi.setText("");
        
        clearInputObat();
        Valid.tabelKosong(tabModeObat);
        Valid.tabelKosong(tabModePemberian);
    }

    private void clearInputObat() {
        idObatAktif = "";
        isEditMode = false;
        TNamaObat.setText("");
        TDosis.setText("");
        TFrekuensi.setText("");
        TKecepatan.setText("");
        TRute.setText("");
        TKodeAB.setText("");
        tglMulai.setDate(new Date());
        tglStop.setDate(null);
        TNmDokter.setText("");
        TNipDokter.setText("");
        TReviewFarmasi.setText("");
        CmbJenisObat.setSelectedIndex(0);
        
        BtnSimpanObat.setText("Simpan");
        BtnEditObat.setEnabled(false);
        BtnHapusObat.setEnabled(false);
    }

    private void simpanObat() {
        // Validasi kardex aktif
        if (idKardexAktif.equals("")) {
            JOptionPane.showMessageDialog(null, "Silakan pilih pasien terlebih dahulu!");
            return;
        }
        
        // Validasi input obat
        if (TNamaObat.getText().trim().equals("")) {
            Valid.textKosong(TNamaObat, "Nama Obat");
            return;
        }
        
        try {
            String sql;
            PreparedStatement ps;
            
            // Get tanggal stop (bisa null)
            String tglStopValue = null;
            if (tglStop.getDate() != null) {
                tglStopValue = Valid.SetTgl(tglStop.getSelectedItem() + "");
            }
            
            if (isEditMode) {
                // Update obat
                sql = "UPDATE kardex_obat SET " +
                      "nama_obat=?, dosis=?, frekuensi=?, kecepatan=?, rute=?, " +
                      "kode_ab=?, tgl_mulai=?, tgl_stop=?, nip_dokter=?, review_farmasi=?, " +
                      "jenis_obat=? WHERE id=?";
                      
                ps = koneksi.prepareStatement(sql);
                ps.setString(1, TNamaObat.getText());
                ps.setString(2, TDosis.getText());
                ps.setString(3, TFrekuensi.getText());
                ps.setString(4, TKecepatan.getText());
                ps.setString(5, TRute.getText());
                ps.setString(6, TKodeAB.getText());
                ps.setString(7, Valid.SetTgl(tglMulai.getSelectedItem() + ""));
                ps.setString(8, tglStopValue);
                ps.setString(9, TNipDokter.getText());
                ps.setString(10, TReviewFarmasi.getText());
                ps.setString(11, CmbJenisObat.getSelectedItem().toString());
                ps.setString(12, idObatAktif);
                
            } else {
                // Insert obat baru
                sql = "INSERT INTO kardex_obat (id_kardex, nama_obat, dosis, frekuensi, " +
                      "kecepatan, rute, kode_ab, tgl_mulai, tgl_stop, nip_dokter, " +
                      "review_farmasi, jenis_obat, status) " +
                      "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,'aktif')";
                      
                ps = koneksi.prepareStatement(sql);
                ps.setString(1, idKardexAktif);
                ps.setString(2, TNamaObat.getText());
                ps.setString(3, TDosis.getText());
                ps.setString(4, TFrekuensi.getText());
                ps.setString(5, TKecepatan.getText());
                ps.setString(6, TRute.getText());
                ps.setString(7, TKodeAB.getText());
                ps.setString(8, Valid.SetTgl(tglMulai.getSelectedItem() + ""));
                ps.setString(9, tglStopValue);
                ps.setString(10, TNipDokter.getText());
                ps.setString(11, TReviewFarmasi.getText());
                ps.setString(12, CmbJenisObat.getSelectedItem().toString());
            }
            
            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "Obat berhasil " + (isEditMode ? "diupdate" : "disimpan") + "!");
                clearInputObat();
                tampilObat();
            }
            
            ps.close();
            
        } catch (Exception e) {
            System.out.println("Error simpan obat: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal menyimpan obat: " + e.getMessage());
        }
    }

    private void editObat() {
        if (idObatAktif.equals("")) {
            JOptionPane.showMessageDialog(null, "Silakan pilih obat yang akan diedit!");
            return;
        }
        
        isEditMode = true;
    }

    private void hapusObat() {
        if (idObatAktif.equals("")) {
            JOptionPane.showMessageDialog(null, "Silakan pilih obat yang akan dihapus!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(null, 
            "Apakah Anda yakin ingin menghapus obat ini?\nData pemberian obat juga akan ikut terhapus!", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Hapus data pemberian terkait
                Sequel.meghapus("kardex_obat", "id", idObatAktif);
                
                JOptionPane.showMessageDialog(null, "Obat berhasil dihapus!");
                clearInputObat();
                tampilObat();
                Valid.tabelKosong(tabModePemberian);
                
            } catch (Exception e) {
                System.out.println("Error hapus obat: " + e.getMessage());
                JOptionPane.showMessageDialog(null, "Gagal menghapus obat: " + e.getMessage());
            }
        }
    }

    private void simpanReviewFarmasi() {
        if (idObatAktif.equals("")) {
            JOptionPane.showMessageDialog(null, "Silakan pilih obat yang akan direview!");
            return;
        }
        
        String review = TReviewFarmasi.getText().trim();
        
        if (review != null && !review.trim().equals("")) {
            try {
                String sql = "UPDATE kardex_obat SET review_farmasi=? WHERE id=?";
                PreparedStatement ps = koneksi.prepareStatement(sql);
                ps.setString(1, review);
                ps.setString(2, idObatAktif);
                
                if (ps.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(null, "Review farmasi berhasil disimpan!");
                    tampilObat();
                }
                
                ps.close();
            } catch (Exception e) {
                System.out.println("Error simpan review: " + e.getMessage());
                JOptionPane.showMessageDialog(null, "Gagal menyimpan review: " + e.getMessage());
            }
        }
    }

    private void hapusKardex() {
        if (idKardexAktif.equals("")) {
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(null, 
            "Apakah Anda yakin ingin menghapus kardex ini?\nSemua data obat dan pemberian akan ikut terhapus!", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Hapus data pemberian terkait
                String sqlPemberian = "DELETE FROM kardex_pemberian WHERE id_obat IN (SELECT id FROM kardex_obat WHERE id_kardex=?)";
                PreparedStatement psPemberian = koneksi.prepareStatement(sqlPemberian);
                psPemberian.setString(1, idKardexAktif);
                psPemberian.executeUpdate();
                psPemberian.close();
                
                // Hapus data obat terkait
                Sequel.meghapus("kardex_obat", "id_kardex", idKardexAktif);
                
                // Hapus kardex pasien
                Sequel.meghapus("kardex_pasien", "id", idKardexAktif);
                
                JOptionPane.showMessageDialog(null, "Kardex berhasil dihapus!");
                emptTeks();
                tampilKardex();
                
            } catch (Exception e) {
                System.out.println("Error hapus kardex: " + e.getMessage());
                JOptionPane.showMessageDialog(null, "Gagal menghapus kardex: " + e.getMessage());
            }
        }
    }

    private void cetakKardex() {
        if (idKardexAktif.equals("")) {
            JOptionPane.showMessageDialog(null, "Silakan pilih kardex yang akan dicetak!");
            return;
        }
        
        // TO DO: Implementasi cetak menggunakan JasperReport
        JOptionPane.showMessageDialog(null, "Fitur cetak akan segera ditambahkan!");
    }

    private void simpanPemberian() {
        if (idObatAktif.equals("")) {
            JOptionPane.showMessageDialog(null, "Silakan pilih obat terlebih dahulu!");
            return;
        }
        
        // Validasi input
        if (TJamPemberian.getText().trim().equals("")) {
            Valid.textKosong(TJamPemberian, "Jam Pemberian");
            return;
        }
        
        if (TNipPetugas1.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Silakan pilih Petugas 1!");
            return;
        }
        
        try {
            String sql = "INSERT INTO kardex_pemberian (id_obat, tgl_pemberian, jam_pemberian, " +
                        "psn_klg, nip_petugas1, nip_petugas2, status_pemberian, catatan) " +
                        "VALUES (?,?,?,?,?,?,?,?)";
                        
            PreparedStatement ps = koneksi.prepareStatement(sql);
            ps.setString(1, idObatAktif);
            ps.setString(2, Valid.SetTgl(tglPemberian.getSelectedItem() + ""));
            ps.setString(3, TJamPemberian.getText());
            ps.setString(4, TPsnKlg.getText());
            ps.setString(5, TNipPetugas1.getText());
            ps.setString(6, TNipPetugas2.getText());
            ps.setString(7, CmbStatusPemberian.getSelectedItem().toString());
            ps.setString(8, TCatatanPemberian.getText());
            
            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(null, "Pemberian obat berhasil dicatat!");
                tampilPemberian();
                
                // Clear input
                TJamPemberian.setText("");
                TPsnKlg.setText("");
                TNmPetugas1.setText("");
                TNipPetugas1.setText("");
                TNmPetugas2.setText("");
                TNipPetugas2.setText("");
                CmbStatusPemberian.setSelectedIndex(0);
                TCatatanPemberian.setText("");
            }
            
            ps.close();
            
        } catch (Exception e) {
            System.out.println("Error simpan pemberian: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Gagal menyimpan pemberian: " + e.getMessage());
        }
    }

    private void tampilKardex() {
        Valid.tabelKosong(tabModeKardex);
        try {
            String sql = "SELECT k.id, k.no_rawat, p.no_rkm_medis, p.nm_pasien, " +
                        "b.nm_bangsal as ruang_rawat, d.nm_dokter, k.tgl_input " +
                        "FROM kardex_pasien k " +
                        "INNER JOIN reg_periksa rp ON rp.no_rawat = k.no_rawat " +
                        "INNER JOIN pasien p ON p.no_rkm_medis = rp.no_rkm_medis " +
                        "INNER JOIN kamar_inap ki ON ki.no_rawat = k.no_rawat AND ki.stts_pulang = '-' " +
                        "INNER JOIN kamar km ON km.kd_kamar = ki.kd_kamar " +
                        "INNER JOIN bangsal b ON b.kd_bangsal = km.kd_bangsal " +
                        "LEFT JOIN dpjp_ranap dr ON dr.no_rawat = k.no_rawat " +
                        "LEFT JOIN dokter d ON d.kd_dokter = dr.kd_dokter " +
                        "WHERE k.tgl_input BETWEEN ? AND ? " +
                        "AND (p.no_rkm_medis LIKE ? OR p.nm_pasien LIKE ? OR k.no_rawat LIKE ?) " +
                        "ORDER BY k.tgl_input DESC";
                        
            PreparedStatement ps = koneksi.prepareStatement(sql);
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
            ps.setString(3, "%" + TCari.getText() + "%");
            ps.setString(4, "%" + TCari.getText() + "%");
            ps.setString(5, "%" + TCari.getText() + "%");
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tabModeKardex.addRow(new Object[]{
                    rs.getString("id"),
                    rs.getString("no_rawat"),
                    rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"),
                    rs.getString("ruang_rawat"),
                    rs.getString("nm_dokter"),
                    rs.getDate("tgl_input")
                });
            }
            
            ps.close();
            
        } catch (Exception e) {
            System.out.println("Error tampil kardex: " + e.getMessage());
        }
    }

    private void tampilObat() {
        if (!idKardexAktif.equals("")) {
            Valid.tabelKosong(tabModeObat);
            try {
                String sql = "SELECT o.id, o.nama_obat, o.dosis, o.frekuensi, " +
                            "o.kecepatan, o.rute, o.kode_ab, o.tgl_mulai, " +
                            "o.tgl_stop, d.nm_dokter, o.review_farmasi, o.jenis_obat, o.status " +
                            "FROM kardex_obat o " +
                            "LEFT JOIN dokter d ON d.kd_dokter = o.nip_dokter " +
                            "WHERE o.id_kardex = ? " +
                            "ORDER BY o.tgl_mulai DESC";
                            
                PreparedStatement ps = koneksi.prepareStatement(sql);
                ps.setString(1, idKardexAktif);
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    tabModeObat.addRow(new Object[]{
                        rs.getString("id"),
                        rs.getString("nama_obat"),
                        rs.getString("dosis"),
                        rs.getString("frekuensi"),
                        rs.getString("kecepatan"),
                        rs.getString("rute"),
                        rs.getString("kode_ab"),
                        rs.getDate("tgl_mulai"),
                        rs.getDate("tgl_stop"),
                        rs.getString("nm_dokter"),
                        rs.getString("review_farmasi"),
                        rs.getString("jenis_obat"),
                        rs.getString("status")
                    });
                }
                
                ps.close();
                
            } catch (Exception e) {
                System.out.println("Error tampil obat: " + e.getMessage());
            }
        }
    }

    private void tampilPemberian() {
        if (!idObatAktif.equals("")) {
            Valid.tabelKosong(tabModePemberian);
            try {
                String sql = "SELECT pb.id, pb.tgl_pemberian, pb.jam_pemberian, " +
                            "pb.psn_klg, p1.nama as petugas1, p2.nama as petugas2, " +
                            "pb.status_pemberian, pb.catatan " +
                            "FROM kardex_pemberian pb " +
                            "LEFT JOIN petugas p1 ON p1.nip = pb.nip_petugas1 " +
                            "LEFT JOIN petugas p2 ON p2.nip = pb.nip_petugas2 " +
                            "WHERE pb.id_obat = ? " +
                            "ORDER BY pb.tgl_pemberian DESC, pb.jam_pemberian DESC";
                            
                PreparedStatement ps = koneksi.prepareStatement(sql);
                ps.setString(1, idObatAktif);
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    tabModePemberian.addRow(new Object[]{
                        rs.getString("id"),
                        rs.getDate("tgl_pemberian"),
                        rs.getString("jam_pemberian"),
                        rs.getString("psn_klg"),
                        rs.getString("petugas1"),
                        rs.getString("petugas2"),
                        rs.getString("status_pemberian"),
                        rs.getString("catatan")
                    });
                }
                
                ps.close();
                
            } catch (Exception e) {
                System.out.println("Error tampil pemberian: " + e.getMessage());
            }
        }
    }

    private void getDataKardex() {
        int row = tbKardex.getSelectedRow();
        if (row != -1) {
            idKardexAktif = tbKardex.getValueAt(row, 0).toString();
            TNoRW.setText(tbKardex.getValueAt(row, 1).toString());
            TNoRM.setText(tbKardex.getValueAt(row, 2).toString());
            TNmPasien.setText(tbKardex.getValueAt(row, 3).toString());
            TRuangRawat.setText(tbKardex.getValueAt(row, 4).toString());
            TNmDPJP.setText(tbKardex.getValueAt(row, 5).toString());
            
            // Load data tambahan
            try {
                String sql = "SELECT berat_badan, tgl_lahir, nip_dpjp, riwayat_alergi FROM kardex_pasien WHERE id=?";
                PreparedStatement ps = koneksi.prepareStatement(sql);
                ps.setString(1, idKardexAktif);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    TBeratBadan.setText(rs.getString("berat_badan") != null ? rs.getString("berat_badan") : "");
                    
                    // Set tanggal lahir dengan null check
                    if (rs.getDate("tgl_lahir") != null) {
                        tglLahir.setDate(rs.getDate("tgl_lahir"));
                    } else {
                        tglLahir.setDate(new Date());
                    }
                    
                    TNipDPJP.setText(rs.getString("nip_dpjp") != null ? rs.getString("nip_dpjp") : "");
                    TRiwayatAlergi.setText(rs.getString("riwayat_alergi") != null ? rs.getString("riwayat_alergi") : "");
                }
                
                ps.close();
                rs.close();
            } catch (Exception e) {
                System.out.println("Error get data kardex: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Tampilkan obat terkait
            tampilObat();
        }
    }

    private void getDataObat() {
        int row = tbObat.getSelectedRow();
        if (row != -1) {
            idObatAktif = tbObat.getValueAt(row, 0).toString();
            
            // Load data ke form dengan null check
            TNamaObat.setText(tbObat.getValueAt(row, 1) != null ? tbObat.getValueAt(row, 1).toString() : "");
            TDosis.setText(tbObat.getValueAt(row, 2) != null ? tbObat.getValueAt(row, 2).toString() : "");
            TFrekuensi.setText(tbObat.getValueAt(row, 3) != null ? tbObat.getValueAt(row, 3).toString() : "");
            TKecepatan.setText(tbObat.getValueAt(row, 4) != null ? tbObat.getValueAt(row, 4).toString() : "");
            TRute.setText(tbObat.getValueAt(row, 5) != null ? tbObat.getValueAt(row, 5).toString() : "");
            TKodeAB.setText(tbObat.getValueAt(row, 6) != null ? tbObat.getValueAt(row, 6).toString() : "");
            TReviewFarmasi.setText(tbObat.getValueAt(row, 10) != null ? tbObat.getValueAt(row, 10).toString() : "");
            
            // Set jenis obat
            if (tbObat.getValueAt(row, 11) != null) {
                CmbJenisObat.setSelectedItem(tbObat.getValueAt(row, 11).toString());
            }
            
            // Set tanggal mulai dengan null check
            try {
                if (tbObat.getValueAt(row, 7) != null) {
                    tglMulai.setDate((Date) tbObat.getValueAt(row, 7));
                } else {
                    tglMulai.setDate(new Date());
                }
                
                // Set tanggal stop dengan null check
                if (tbObat.getValueAt(row, 8) != null) {
                    tglStop.setDate((Date) tbObat.getValueAt(row, 8));
                } else {
                    tglStop.setDate(new Date());
                }
            } catch (Exception e) {
                System.out.println("Error set date: " + e.getMessage());
                tglMulai.setDate(new Date());
                tglStop.setDate(new Date());
            }
            
            // Load dokter order
            try {
                String sql = "SELECT nip_dokter FROM kardex_obat WHERE id=?";
                PreparedStatement ps = koneksi.prepareStatement(sql);
                ps.setString(1, idObatAktif);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    String nipDokter = rs.getString("nip_dokter");
                    if (nipDokter != null && !nipDokter.isEmpty()) {
                        TNipDokter.setText(nipDokter);
                        
                        // Get nama dokter
                        String sqlDokter = "SELECT nm_dokter FROM dokter WHERE kd_dokter=?";
                        PreparedStatement psDokter = koneksi.prepareStatement(sqlDokter);
                        psDokter.setString(1, nipDokter);
                        ResultSet rsDokter = psDokter.executeQuery();
                        
                        if (rsDokter.next()) {
                            TNmDokter.setText(rsDokter.getString("nm_dokter"));
                        }
                        
                        psDokter.close();
                        rsDokter.close();
                    }
                }
                
                ps.close();
                rs.close();
            } catch (Exception e) {
                System.out.println("Error get dokter: " + e.getMessage());
            }
            
            // Enable button
            BtnEditObat.setEnabled(true);
            BtnHapusObat.setEnabled(true);
            BtnSimpanPemberian.setEnabled(true);
            
            // Tampilkan pemberian terkait
            tampilPemberian();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgFormKardex dialog = new DlgFormKardex(new javax.swing.JFrame(), true);
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
    private widget.Button BtnBatalObat;
    private widget.Button BtnCari;
    private widget.Button BtnCetak;
    private widget.Button BtnDokter;
    private widget.Button BtnDokterOrder;
    private widget.Button BtnEditObat;
    private widget.Button BtnHapusKardex;
    private widget.Button BtnHapusObat;
    private widget.Button BtnKeluar;
    private widget.Button BtnPetugas1;
    private widget.Button BtnPetugas2;
    private widget.Button BtnSimpanObat;
    private widget.Button BtnSimpanPemberian;
    private widget.Button BtnTambah;
    private widget.ComboBox CmbJenisObat;
    private widget.ComboBox CmbStatusPemberian;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private javax.swing.JMenuItem MnEditObat;
    private javax.swing.JMenuItem MnHapusObat;
    private javax.swing.JMenuItem MnReviewFarmasi;
    private widget.ScrollPane Scroll1;
    private widget.ScrollPane Scroll2;
    private widget.ScrollPane Scroll3;
    private widget.TextBox TBeratBadan;
    private widget.TextBox TCari;
    private widget.TextBox TCatatanPemberian;
    private widget.TextBox TDosis;
    private widget.TextBox TFrekuensi;
    private widget.TextBox TJamPemberian;
    private widget.TextBox TKecepatan;
    private widget.TextBox TKodeAB;
    private widget.TextBox TNamaObat;
    private widget.TextBox TNipDPJP;
    private widget.TextBox TNipDokter;
    private widget.TextBox TNipPetugas1;
    private widget.TextBox TNipPetugas2;
    private widget.TextBox TNmDPJP;
    private widget.TextBox TNmDokter;
    private widget.TextBox TNmPasien;
    private widget.TextBox TNmPetugas1;
    private widget.TextBox TNmPetugas2;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRW;
    private widget.TextBox TPsnKlg;
    private widget.TextBox TReviewFarmasi;
    private widget.TextBox TRiwayatAlergi;
    private widget.TextBox TRuangRawat;
    private widget.TextBox TRute;
    private javax.swing.JTabbedPane TabRawat;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame2;
    private widget.InternalFrame internalFrame3;
    private widget.InternalFrame internalFrame4;
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
    private widget.Label jLabel3;
    private widget.Label jLabel30;
    private widget.Label jLabel31;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.panelisi panelGlass10;
    private widget.panelisi panelGlass11;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbKardex;
    private widget.Table tbObat;
    private widget.Table tbPemberian;
    private widget.Tanggal tglLahir;
    private widget.Tanggal tglMulai;
    private widget.Tanggal tglPemberian;
    private widget.Tanggal tglStop;
    // End of variables declaration                   
}