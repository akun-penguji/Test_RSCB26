/*
 * Form Robson Klasifikasi - Sistem Informasi Rumah Sakit
 * Berdasarkan WHO Robson Classification untuk monitoring Sectio Caesarea
 */

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
import kepegawaian.DlgCariDokter;

/**
 * Form Robson Classification untuk Audit Sectio Caesarea
 * @author sistem
 */
public final class RMFormRobsonKlasifikasi extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private DlgCariDokter dokter = new DlgCariDokter(null, false);
    private StringBuilder htmlContent;
    private String finger = "";
    
    /** Creates new form RMFormRobsonKlasifikasi
     * @param parent
     * @param modal */
    public RMFormRobsonKlasifikasi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "J.K.", "Kode Dokter", "Nama Dokter", "Tanggal", 
            "Kelompok Robson", "Paritas", "Riwayat SC", "Onset Persalinan", "Presentasi", "Usia Kehamilan", 
            "Jumlah Janin", "Indikasi SC", "Kondisi Ibu", "Kondisi Bayi", "Berat Bayi", "Apgar Score",
            "Komplikasi", "Tindakan Tambahan", "Lama Rawat", "Keterangan"
        }) {
            @Override 
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        
        tbRobson.setModel(tabMode);
        tbRobson.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbRobson.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 24; i++) {
            TableColumn column = tbRobson.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(105);
            } else if (i == 1) {
                column.setPreferredWidth(70);
            } else if (i == 2) {
                column.setPreferredWidth(150);
            } else if (i == 3) {
                column.setPreferredWidth(75);
            } else if (i == 4) {
                column.setPreferredWidth(50);
            } else if (i == 5) {
                column.setPreferredWidth(80);
            } else if (i == 6) {
                column.setPreferredWidth(150);
            } else if (i == 7) {
                column.setPreferredWidth(120);
            } else if (i == 8) {
                column.setPreferredWidth(120);
            } else if (i == 9) {
                column.setPreferredWidth(80);
            } else if (i == 10) {
                column.setPreferredWidth(80);
            } else if (i == 11) {
                column.setPreferredWidth(120);
            } else if (i == 12) {
                column.setPreferredWidth(100);
            } else if (i == 13) {
                column.setPreferredWidth(100);
            } else if (i == 14) {
                column.setPreferredWidth(80);
            } else if (i == 15) {
                column.setPreferredWidth(200);
            } else if (i == 16) {
                column.setPreferredWidth(100);
            } else if (i == 17) {
                column.setPreferredWidth(100);
            } else if (i == 18) {
                column.setPreferredWidth(80);
            } else if (i == 19) {
                column.setPreferredWidth(80);
            } else if (i == 20) {
                column.setPreferredWidth(150);
            } else if (i == 21) {
                column.setPreferredWidth(120);
            } else if (i == 22) {
                column.setPreferredWidth(80);
            } else if (i == 23) {
                column.setPreferredWidth(200);
            }
        }
        tbRobson.setDefaultRenderer(Object.class, new WarnaTable());
        
        // Set input limits
        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        BeratBayi.setDocument(new batasInput((byte) 6).getKata(BeratBayi));
        ApgarScore.setDocument(new batasInput((byte) 10).getKata(ApgarScore));
        LamaRawat.setDocument(new batasInput((byte) 3).getKata(LamaRawat));
        IndikasiSC.setDocument(new batasInput((int) 500).getKata(IndikasiSC));
        Komplikasi.setDocument(new batasInput((int) 500).getKata(Komplikasi));
        TindakanTambahan.setDocument(new batasInput((int) 500).getKata(TindakanTambahan));
        Keterangan.setDocument(new batasInput((int) 1000).getKata(Keterangan));
        TCari.setDocument(new batasInput((int) 100).getKata(TCari));
        
        // Auto search
        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }
            });
        }
        
        // Doctor dialog listener
        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if (dokter.getTable().getSelectedRow() != -1) {
                    KdDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                    NmDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                    KdDokter.requestFocus();
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
        
        // HTML Editor setup
        HTMLEditorKit kit = new HTMLEditorKit();
        LoadHTML.setEditable(true);
        LoadHTML.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule(
            ".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}" +
            ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}" +
            ".isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}" +
            ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"
        );
        Document doc = kit.createDefaultDocument();
        LoadHTML.setDocument(doc);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        LoadHTML = new widget.editorpane();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnLaporanRobson = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        BtnAll = new widget.Button();
        BtnKeluar = new widget.Button();
        TabRawat = new javax.swing.JTabbedPane();
        internalFrame2 = new widget.InternalFrame();
        scrollInput = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        TNoRM = new widget.TextBox();
        label14 = new widget.Label();
        KdDokter = new widget.TextBox();
        NmDokter = new widget.TextBox();
        BtnDokter = new widget.Button();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        jLabel9 = new widget.Label();
        Jk = new widget.TextBox();
        jLabel10 = new widget.Label();
        jLabel11 = new widget.Label();
        label11 = new widget.Label();
        TglAudit = new widget.Tanggal();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel99 = new widget.Label();
        jLabel12 = new widget.Label();
        KelompokRobson = new widget.ComboBox();
        jLabel13 = new widget.Label();
        Paritas = new widget.ComboBox();
        jLabel15 = new widget.Label();
        RiwayatSC = new widget.ComboBox();
        jLabel16 = new widget.Label();
        OnsetPersalinan = new widget.ComboBox();
        jLabel17 = new widget.Label();
        Presentasi = new widget.ComboBox();
        jLabel18 = new widget.Label();
        UsiaKehamilan = new widget.ComboBox();
        jLabel19 = new widget.Label();
        JumlahJanin = new widget.ComboBox();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel100 = new widget.Label();
        jLabel20 = new widget.Label();
        scrollPane1 = new widget.ScrollPane();
        IndikasiSC = new widget.TextArea();
        jLabel21 = new widget.Label();
        KondisiIbu = new widget.ComboBox();
        jLabel22 = new widget.Label();
        KondisiBayi = new widget.ComboBox();
        jLabel23 = new widget.Label();
        BeratBayi = new widget.TextBox();
        jLabel24 = new widget.Label();
        ApgarScore = new widget.TextBox();
        jLabel25 = new widget.Label();
        scrollPane2 = new widget.ScrollPane();
        Komplikasi = new widget.TextArea();
        jLabel26 = new widget.Label();
        scrollPane3 = new widget.ScrollPane();
        TindakanTambahan = new widget.TextArea();
        jLabel27 = new widget.Label();
        LamaRawat = new widget.TextBox();
        jLabel28 = new widget.Label();
        jLabel29 = new widget.Label();
        scrollPane4 = new widget.ScrollPane();
        Keterangan = new widget.TextArea();
        internalFrame3 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbRobson = new widget.Table();
        panelGlass9 = new widget.panelisi();
        jLabel30 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel31 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();

        LoadHTML.setBorder(null);
        LoadHTML.setName("LoadHTML");

        jPopupMenu1.setName("jPopupMenu1");

        MnLaporanRobson.setBackground(new java.awt.Color(255, 255, 254));
        MnLaporanRobson.setFont(new java.awt.Font("Tahoma", 0, 11));
        MnLaporanRobson.setForeground(new java.awt.Color(50, 50, 50));
        MnLaporanRobson.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        MnLaporanRobson.setText("Laporan Form Robson");
        MnLaporanRobson.setName("MnLaporanRobson");
        MnLaporanRobson.setPreferredSize(new java.awt.Dimension(220, 26));
        MnLaporanRobson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnLaporanRobsonActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnLaporanRobson);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Form Robson Classification - Audit Sectio Caesarea ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12));
        internalFrame1.setName("internalFrame1");
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
        BtnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSimpanKeyPressed(evt);
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
        BtnBatal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnBatalKeyPressed(evt);
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
        BtnHapus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnHapusKeyPressed(evt);
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
        BtnEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnEditKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnEdit);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setName("BtnPrint");
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

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setMnemonic('M');
        BtnAll.setText("Semua");
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll");
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
        BtnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnKeluarKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnKeluar);

        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        TabRawat.setBackground(new java.awt.Color(254, 255, 254));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11));
        TabRawat.setName("TabRawat");
        TabRawat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TabRawatMouseClicked(evt);
            }
        });

        internalFrame2.setBorder(null);
        internalFrame2.setName("internalFrame2");
        internalFrame2.setLayout(new java.awt.BorderLayout(1, 1));

        scrollInput.setName("scrollInput");
        scrollInput.setPreferredSize(new java.awt.Dimension(102, 557));

        FormInput.setBackground(new java.awt.Color(255, 255, 255));
        FormInput.setBorder(null);
        FormInput.setName("FormInput");
        FormInput.setPreferredSize(new java.awt.Dimension(870, 800));
        FormInput.setLayout(null);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw");
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(74, 10, 131, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien");
        FormInput.add(TPasien);
        TPasien.setBounds(309, 10, 260, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM");
        FormInput.add(TNoRM);
        TNoRM.setBounds(207, 10, 100, 23);

        label14.setText("Dokter :");
        label14.setName("label14");
        label14.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label14);
        label14.setBounds(0, 40, 70, 23);

        KdDokter.setEditable(false);
        KdDokter.setName("KdDokter");
        KdDokter.setPreferredSize(new java.awt.Dimension(80, 23));
        KdDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdDokterKeyPressed(evt);
            }
        });
        FormInput.add(KdDokter);
        KdDokter.setBounds(74, 40, 90, 23);

        NmDokter.setEditable(false);
        NmDokter.setName("NmDokter");
        NmDokter.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(NmDokter);
        NmDokter.setBounds(166, 40, 180, 23);

        BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokter.setMnemonic('2');
        BtnDokter.setToolTipText("Alt+2");
        BtnDokter.setName("BtnDokter");
        BtnDokter.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokterActionPerformed(evt);
            }
        });
        BtnDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnDokterKeyPressed(evt);
            }
        });
        FormInput.add(BtnDokter);
        BtnDokter.setBounds(348, 40, 28, 23);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8");
        FormInput.add(jLabel8);
        jLabel8.setBounds(580, 10, 60, 23);

        TglLahir.setEditable(false);
        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir");
        FormInput.add(TglLahir);
        TglLahir.setBounds(644, 10, 80, 23);

        jLabel9.setText("Kelompok Robson :");
        jLabel9.setName("jLabel9");
        FormInput.add(jLabel9);
        jLabel9.setBounds(0, 110, 120, 23);

        Jk.setEditable(false);
        Jk.setHighlighter(null);
        Jk.setName("Jk");
        FormInput.add(Jk);
        Jk.setBounds(774, 10, 80, 23);

        jLabel10.setText("No.Rawat :");
        jLabel10.setName("jLabel10");
        FormInput.add(jLabel10);
        jLabel10.setBounds(0, 10, 70, 23);

        jLabel11.setText("J.K. :");
        jLabel11.setName("jLabel11");
        FormInput.add(jLabel11);
        jLabel11.setBounds(740, 10, 30, 23);

        label11.setText("Tanggal :");
        label11.setName("label11");
        label11.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label11);
        label11.setBounds(380, 40, 52, 23);

        TglAudit.setForeground(new java.awt.Color(50, 70, 50));
        TglAudit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "14-09-2025 10:00:00" }));
        TglAudit.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAudit.setName("TglAudit");
        TglAudit.setOpaque(false);
        TglAudit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TglAuditKeyPressed(evt);
            }
        });
        FormInput.add(TglAudit);
        TglAudit.setBounds(436, 40, 130, 23);

        jSeparator1.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator1.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator1.setName("jSeparator1");
        FormInput.add(jSeparator1);
        jSeparator1.setBounds(0, 70, 880, 1);

        jLabel99.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel99.setText("I. KLASIFIKASI ROBSON");
        jLabel99.setName("jLabel99");
        FormInput.add(jLabel99);
        jLabel99.setBounds(10, 80, 180, 23);

        jLabel12.setText("Kelompok Robson :");
        jLabel12.setName("jLabel12");
        FormInput.add(jLabel12);
        jLabel12.setBounds(0, 110, 120, 23);

        KelompokRobson.setModel(new javax.swing.DefaultComboBoxModel(new String[] { 
            "Grup 1 - Nullipara, tunggal, kepala, ≥37 minggu, spontan", 
            "Grup 2 - Nullipara, tunggal, kepala, ≥37 minggu, induksi/SC", 
            "Grup 3 - Multipara, tunggal, kepala, ≥37 minggu, spontan", 
            "Grup 4 - Multipara, tunggal, kepala, ≥37 minggu, induksi/SC", 
            "Grup 5 - Multipara, riwayat SC, tunggal, kepala, ≥37 minggu", 
            "Grup 6 - Nullipara, sungsang", 
            "Grup 7 - Multipara, sungsang", 
            "Grup 8 - Kehamilan ganda", 
            "Grup 9 - Presentasi abnormal", 
            "Grup 10 - Preterm <37 minggu" 
        }));
        KelompokRobson.setName("KelompokRobson");
        KelompokRobson.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KelompokRobsonKeyPressed(evt);
            }
        });
        FormInput.add(KelompokRobson);
        KelompokRobson.setBounds(124, 110, 400, 23);

        jLabel13.setText("Paritas :");
        jLabel13.setName("jLabel13");
        FormInput.add(jLabel13);
        jLabel13.setBounds(530, 110, 60, 23);

        Paritas.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Nullipara", "Multipara" }));
        Paritas.setName("Paritas");
        Paritas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ParitasKeyPressed(evt);
            }
        });
        FormInput.add(Paritas);
        Paritas.setBounds(594, 110, 100, 23);

        jLabel15.setText("Riwayat SC :");
        jLabel15.setName("jLabel15");
        FormInput.add(jLabel15);
        jLabel15.setBounds(700, 110, 80, 23);

        RiwayatSC.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        RiwayatSC.setName("RiwayatSC");
        RiwayatSC.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RiwayatSCKeyPressed(evt);
            }
        });
        FormInput.add(RiwayatSC);
        RiwayatSC.setBounds(784, 110, 70, 23);

        jLabel16.setText("Onset Persalinan :");
        jLabel16.setName("jLabel16");
        FormInput.add(jLabel16);
        jLabel16.setBounds(0, 140, 120, 23);

        OnsetPersalinan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Spontan", "Induksi", "SC Elektif" }));
        OnsetPersalinan.setName("OnsetPersalinan");
        OnsetPersalinan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                OnsetPersalinanKeyPressed(evt);
            }
        });
        FormInput.add(OnsetPersalinan);
        OnsetPersalinan.setBounds(124, 140, 120, 23);

        jLabel17.setText("Presentasi :");
        jLabel17.setName("jLabel17");
        FormInput.add(jLabel17);
        jLabel17.setBounds(250, 140, 80, 23);

        Presentasi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Kepala", "Sungsang", "Lintang", "Oblique" }));
        Presentasi.setName("Presentasi");
        Presentasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PresentasiKeyPressed(evt);
            }
        });
        FormInput.add(Presentasi);
        Presentasi.setBounds(334, 140, 100, 23);

        jLabel18.setText("Usia Kehamilan :");
        jLabel18.setName("jLabel18");
        FormInput.add(jLabel18);
        jLabel18.setBounds(440, 140, 100, 23);

        UsiaKehamilan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<37 minggu", "≥37 minggu" }));
        UsiaKehamilan.setName("UsiaKehamilan");
        UsiaKehamilan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                UsiaKehamilanKeyPressed(evt);
            }
        });
        FormInput.add(UsiaKehamilan);
        UsiaKehamilan.setBounds(544, 140, 100, 23);

        jLabel19.setText("Jumlah Janin :");
        jLabel19.setName("jLabel19");
        FormInput.add(jLabel19);
        jLabel19.setBounds(650, 140, 90, 23);

        JumlahJanin.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tunggal", "Ganda" }));
        JumlahJanin.setName("JumlahJanin");
        JumlahJanin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JumlahJaninKeyPressed(evt);
            }
        });
        FormInput.add(JumlahJanin);
        JumlahJanin.setBounds(744, 140, 80, 23);

        jSeparator2.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator2.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator2.setName("jSeparator2");
        FormInput.add(jSeparator2);
        jSeparator2.setBounds(0, 170, 880, 1);

        jLabel100.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel100.setText("II. DATA SECTIO CAESAREA");
        jLabel100.setName("jLabel100");
        FormInput.add(jLabel100);
        jLabel100.setBounds(10, 180, 180, 23);

        jLabel20.setText("Indikasi SC :");
        jLabel20.setName("jLabel20");
        FormInput.add(jLabel20);
        jLabel20.setBounds(0, 210, 120, 23);

        scrollPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane1.setName("scrollPane1");

        IndikasiSC.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        IndikasiSC.setColumns(20);
        IndikasiSC.setRows(3);
        IndikasiSC.setName("IndikasiSC");
        IndikasiSC.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                IndikasiSCKeyPressed(evt);
            }
        });
        scrollPane1.setViewportView(IndikasiSC);

        FormInput.add(scrollPane1);
        scrollPane1.setBounds(124, 210, 730, 50);

        jLabel21.setText("Kondisi Ibu :");
        jLabel21.setName("jLabel21");
        FormInput.add(jLabel21);
        jLabel21.setBounds(0, 270, 120, 23);

        KondisiIbu.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Baik", "Komplikasi", "Meninggal" }));
        KondisiIbu.setName("KondisiIbu");
        KondisiIbu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KondisiIbuKeyPressed(evt);
            }
        });
        FormInput.add(KondisiIbu);
        KondisiIbu.setBounds(124, 270, 120, 23);

        jLabel22.setText("Kondisi Bayi :");
        jLabel22.setName("jLabel22");
        FormInput.add(jLabel22);
        jLabel22.setBounds(250, 270, 90, 23);

        KondisiBayi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Baik", "Komplikasi", "Meninggal" }));
        KondisiBayi.setName("KondisiBayi");
        KondisiBayi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KondisiBayiKeyPressed(evt);
            }
        });
        FormInput.add(KondisiBayi);
        KondisiBayi.setBounds(344, 270, 120, 23);

        jLabel23.setText("Berat Bayi :");
        jLabel23.setName("jLabel23");
        FormInput.add(jLabel23);
        jLabel23.setBounds(470, 270, 80, 23);

        BeratBayi.setName("BeratBayi");
        BeratBayi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BeratBayiKeyPressed(evt);
            }
        });
        FormInput.add(BeratBayi);
        BeratBayi.setBounds(554, 270, 80, 23);

        jLabel24.setText("gram        APGAR :");
        jLabel24.setName("jLabel24");
        FormInput.add(jLabel24);
        jLabel24.setBounds(638, 270, 100, 23);

        ApgarScore.setName("ApgarScore");
        ApgarScore.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ApgarScoreKeyPressed(evt);
            }
        });
        FormInput.add(ApgarScore);
        ApgarScore.setBounds(742, 270, 80, 23);

        jLabel25.setText("Komplikasi :");
        jLabel25.setName("jLabel25");
        FormInput.add(jLabel25);
        jLabel25.setBounds(0, 300, 120, 23);

        scrollPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane2.setName("scrollPane2");

        Komplikasi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Komplikasi.setColumns(20);
        Komplikasi.setRows(3);
        Komplikasi.setName("Komplikasi");
        Komplikasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KomplikasiKeyPressed(evt);
            }
        });
        scrollPane2.setViewportView(Komplikasi);

        FormInput.add(scrollPane2);
        scrollPane2.setBounds(124, 300, 730, 50);

        jLabel26.setText("Tindakan Tambahan :");
        jLabel26.setName("jLabel26");
        FormInput.add(jLabel26);
        jLabel26.setBounds(0, 360, 120, 23);

        scrollPane3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane3.setName("scrollPane3");

        TindakanTambahan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        TindakanTambahan.setColumns(20);
        TindakanTambahan.setRows(3);
        TindakanTambahan.setName("TindakanTambahan");
        TindakanTambahan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TindakanTambahanKeyPressed(evt);
            }
        });
        scrollPane3.setViewportView(TindakanTambahan);

        FormInput.add(scrollPane3);
        scrollPane3.setBounds(124, 360, 730, 50);

        jLabel27.setText("Lama Rawat :");
        jLabel27.setName("jLabel27");
        FormInput.add(jLabel27);
        jLabel27.setBounds(0, 420, 120, 23);

        LamaRawat.setName("LamaRawat");
        LamaRawat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LamaRawatKeyPressed(evt);
            }
        });
        FormInput.add(LamaRawat);
        LamaRawat.setBounds(124, 420, 80, 23);

        jLabel28.setText("hari");
        jLabel28.setName("jLabel28");
        FormInput.add(jLabel28);
        jLabel28.setBounds(208, 420, 30, 23);

        jLabel29.setText("Keterangan :");
        jLabel29.setName("jLabel29");
        FormInput.add(jLabel29);
        jLabel29.setBounds(0, 450, 120, 23);

        scrollPane4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane4.setName("scrollPane4");

        Keterangan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Keterangan.setColumns(20);
        Keterangan.setRows(4);
        Keterangan.setName("Keterangan");
        Keterangan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeteranganKeyPressed(evt);
            }
        });
        scrollPane4.setViewportView(Keterangan);

        FormInput.add(scrollPane4);
        scrollPane4.setBounds(124, 450, 730, 70);

        scrollInput.setViewportView(FormInput);

        internalFrame2.add(scrollInput, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Input Robson", internalFrame2);

        internalFrame3.setBorder(null);
        internalFrame3.setName("internalFrame3");
        internalFrame3.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll");
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 200));

        tbRobson.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbRobson.setComponentPopupMenu(jPopupMenu1);
        tbRobson.setName("tbRobson");
        tbRobson.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbRobsonMouseClicked(evt);
            }
        });
        tbRobson.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbRobsonKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbRobson);

        internalFrame3.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9");
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel30.setText("Tgl.Audit :");
        jLabel30.setName("jLabel30");
        jLabel30.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel30);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "14-09-2025" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1");
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari1);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("s.d.");
        jLabel31.setName("jLabel31");
        jLabel31.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel31);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "14-09-2025" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2");
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6");
        jLabel6.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari");
        TCari.setPreferredSize(new java.awt.Dimension(195, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setMnemonic('3');
        BtnCari.setToolTipText("Alt+3");
        BtnCari.setName("BtnCari");
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
        jLabel7.setName("jLabel7");
        jLabel7.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount");
        LCount.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(LCount);

        internalFrame3.add(panelGlass9, java.awt.BorderLayout.PAGE_END);

        TabRawat.addTab("Data Robson", internalFrame3);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>                        

    // Event handlers
    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            isRawat();
        } else {
            Valid.pindah(evt, TCari, BtnDokter);
        }
    }

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
        if (TNoRM.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No Rawat");
        } else if (NmDokter.getText().trim().equals("")) {
            Valid.textKosong(BtnDokter, "Dokter");
        } else {
            if (Sequel.menyimpantf("form_robson_klasifikasi", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "No.Rawat", 19, new String[]{
                TNoRw.getText(), Valid.SetTgl(TglAudit.getSelectedItem() + "") + " " + TglAudit.getSelectedItem().toString().substring(11, 19), 
                KdDokter.getText(), KelompokRobson.getSelectedItem().toString(), Paritas.getSelectedItem().toString(), 
                RiwayatSC.getSelectedItem().toString(), OnsetPersalinan.getSelectedItem().toString(), Presentasi.getSelectedItem().toString(),
                UsiaKehamilan.getSelectedItem().toString(), JumlahJanin.getSelectedItem().toString(), IndikasiSC.getText(),
                KondisiIbu.getSelectedItem().toString(), KondisiBayi.getSelectedItem().toString(), BeratBayi.getText(),
                ApgarScore.getText(), Komplikasi.getText(), TindakanTambahan.getText(), LamaRawat.getText(), Keterangan.getText()
            }) == true) {
                emptTeks();
            }
        }
    }

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, Keterangan, BtnBatal);
        }
    }

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {
        emptTeks();
    }

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            emptTeks();
        } else {
            Valid.pindah(evt, BtnSimpan, BtnHapus);
        }
    }

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {
        if (tbRobson.getSelectedRow() > -1) {
            if (akses.getkode().equals("Admin Utama")) {
                hapus();
            } else {
                if (KdDokter.getText().equals(tbRobson.getValueAt(tbRobson.getSelectedRow(), 5).toString())) {
                    hapus();
                } else {
                    JOptionPane.showMessageDialog(null, "Hanya bisa dihapus oleh dokter yang bersangkutan..!!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
        }
    }

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnHapusActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
    }

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {
        if (TNoRM.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No Rawat");
        } else if (NmDokter.getText().trim().equals("")) {
            Valid.textKosong(BtnDokter, "Dokter");
        } else {
            if (tbRobson.getSelectedRow() > -1) {
                if (akses.getkode().equals("Admin Utama")) {
                    ganti();
                } else {
                    if (KdDokter.getText().equals(tbRobson.getValueAt(tbRobson.getSelectedRow(), 5).toString())) {
                        ganti();
                    } else {
                        JOptionPane.showMessageDialog(null, "Hanya bisa diganti oleh dokter yang bersangkutan..!!");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
            }
        }
    }

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnEditActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
    }

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnKeluarActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnEdit, TCari);
        }
    }

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        } else if (tabMode.getRowCount() != 0) {
            try {
                // Generate HTML report
                htmlContent = new StringBuilder();
                htmlContent.append("<tr class='isi'>");
                htmlContent.append("<td valign='middle' bgcolor='#FFFAF8' align='center'><b>No.Rawat</b></td>");
                htmlContent.append("<td valign='middle' bgcolor='#FFFAF8' align='center'><b>No.RM</b></td>");
                htmlContent.append("<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Nama Pasien</b></td>");
                htmlContent.append("<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Kelompok Robson</b></td>");
                htmlContent.append("<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Indikasi SC</b></td>");
                htmlContent.append("<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Kondisi Ibu</b></td>");
                htmlContent.append("<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Kondisi Bayi</b></td>");
                htmlContent.append("</tr>");

                for (int i = 0; i < tbRobson.getRowCount(); i++) {
                    htmlContent.append("<tr class='isi'>");
                    htmlContent.append("<td>").append(tbRobson.getValueAt(i, 0)).append("</td>");
                    htmlContent.append("<td>").append(tbRobson.getValueAt(i, 1)).append("</td>");
                    htmlContent.append("<td>").append(tbRobson.getValueAt(i, 2)).append("</td>");
                    htmlContent.append("<td>").append(tbRobson.getValueAt(i, 8)).append("</td>");
                    htmlContent.append("<td>").append(tbRobson.getValueAt(i, 15)).append("</td>");
                    htmlContent.append("<td>").append(tbRobson.getValueAt(i, 16)).append("</td>");
                    htmlContent.append("<td>").append(tbRobson.getValueAt(i, 17)).append("</td>");
                    htmlContent.append("</tr>");
                }

                LoadHTML.setText("<html><table width='100%' border='0' align='center' cellpadding='1px' cellspacing='0' class='tbl_form'>" + htmlContent.toString() + "</table></html>");

                File f = new File("DataFormRobson.html");
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                bw.write(LoadHTML.getText().replaceAll("<head>", "<head><title>Laporan Form Robson</title>"));
                bw.close();
                Desktop.getDesktop().browse(f.toURI());

            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPrintActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnEdit, BtnKeluar);
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

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {
        tampil();
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
        tampil();
    }

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            TCari.setText("");
            tampil();
        } else {
            Valid.pindah(evt, BtnCari, TPasien);
        }
    }

    private void tbRobsonMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
            if ((evt.getClickCount() == 2) && (tbRobson.getSelectedColumn() == 0)) {
                TabRawat.setSelectedIndex(0);
            }
        }
    }

    private void tbRobsonKeyPressed(java.awt.event.KeyEvent evt) {
        if (tabMode.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            } else if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                try {
                    getData();
                    TabRawat.setSelectedIndex(0);
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }

    private void KdDokterKeyPressed(java.awt.event.KeyEvent evt) {
        // Placeholder
    }

    private void BtnDokterActionPerformed(java.awt.event.ActionEvent evt) {
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setAlwaysOnTop(false);
        dokter.setVisible(true);
    }

    private void BtnDokterKeyPressed(java.awt.event.KeyEvent evt) {
        // Placeholder
    }

    private void TglAuditKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, NmDokter, KelompokRobson);
    }

    private void KelompokRobsonKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, TglAudit, Paritas);
    }

    private void ParitasKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, KelompokRobson, RiwayatSC);
    }

    private void RiwayatSCKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, Paritas, OnsetPersalinan);
    }

    private void OnsetPersalinanKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, RiwayatSC, Presentasi);
    }

    private void PresentasiKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, OnsetPersalinan, UsiaKehamilan);
    }

    private void UsiaKehamilanKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, Presentasi, JumlahJanin);
    }

    private void JumlahJaninKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, UsiaKehamilan, IndikasiSC);
    }

    private void IndikasiSCKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah2(evt, JumlahJanin, KondisiIbu);
    }

    private void KondisiIbuKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, IndikasiSC, KondisiBayi);
    }

    private void KondisiBayiKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, KondisiIbu, BeratBayi);
    }

    private void BeratBayiKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, KondisiBayi, ApgarScore);
    }

    private void ApgarScoreKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, BeratBayi, Komplikasi);
    }

    private void KomplikasiKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah2(evt, ApgarScore, TindakanTambahan);
    }

    private void TindakanTambahanKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah2(evt, Komplikasi, LamaRawat);
    }

    private void LamaRawatKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, TindakanTambahan, Keterangan);
    }

    private void KeteranganKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah2(evt, LamaRawat, BtnSimpan);
    }

    private void TabRawatMouseClicked(java.awt.event.MouseEvent evt) {
        if (TabRawat.getSelectedIndex() == 1) {
            tampil();
        }
    }

    private void MnLaporanRobsonActionPerformed(java.awt.event.ActionEvent evt) {
        if (tbRobson.getSelectedRow() > -1) {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            param.put("logo2", Sequel.cariGambar("select setting.logo2 from setting"));
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", tbRobson.getValueAt(tbRobson.getSelectedRow(), 5).toString());
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbRobson.getValueAt(tbRobson.getSelectedRow(), 6).toString() + "\nID " + (finger.equals("") ? tbRobson.getValueAt(tbRobson.getSelectedRow(), 5).toString() : finger) + "\n" + Valid.SetTgl3(tbRobson.getValueAt(tbRobson.getSelectedRow(), 7).toString()));

            Valid.MyReportqry("rptFormRobsonKlasifikasi.jasper", "report", "::[ Laporan Form Robson Classification ]::",
                "select * from form_robson_klasifikasi_view where no_rawat='" + tbRobson.getValueAt(tbRobson.getSelectedRow(), 0).toString() + "'", param);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMFormRobsonKlasifikasi dialog = new RMFormRobsonKlasifikasi(new javax.swing.JFrame(), true);
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
    private widget.TextBox ApgarScore;
    private widget.TextBox BeratBayi;
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnDokter;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.PanelBiasa FormInput;
    private widget.TextArea IndikasiSC;
    private widget.TextBox Jk;
    private widget.ComboBox JumlahJanin;
    private widget.TextBox KdDokter;
    private widget.ComboBox KelompokRobson;
    private widget.TextArea Keterangan;
    private widget.TextArea Komplikasi;
    private widget.ComboBox KondisiBayi;
    private widget.ComboBox KondisiIbu;
    private widget.Label LCount;
    private widget.TextBox LamaRawat;
    private widget.editorpane LoadHTML;
    private javax.swing.JMenuItem MnLaporanRobson;
    private widget.TextBox NmDokter;
    private widget.ComboBox OnsetPersalinan;
    private widget.ComboBox Paritas;
    private widget.ComboBox Presentasi;
    private widget.ComboBox RiwayatSC;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private javax.swing.JTabbedPane TabRawat;
    private widget.Tanggal TglAudit;
    private widget.TextBox TglLahir;
    private widget.TextArea TindakanTambahan;
    private widget.ComboBox UsiaKehamilan;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame2;
    private widget.InternalFrame internalFrame3;
    private widget.Label jLabel10;
    private widget.Label jLabel100;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
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
    private widget.Label jLabel30;
    private widget.Label jLabel31;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private widget.Label jLabel99;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private widget.Label label11;
    private widget.Label label14;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.ScrollPane scrollInput;
    private widget.ScrollPane scrollPane1;
    private widget.ScrollPane scrollPane2;
    private widget.ScrollPane scrollPane3;
    private widget.ScrollPane scrollPane4;
    private widget.Table tbRobson;
    // End of variables declaration                   

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            if (TCari.getText().trim().equals("")) {
                ps = koneksi.prepareStatement(
                    "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir," +
                    "form_robson_klasifikasi.tanggal,form_robson_klasifikasi.kd_dokter,dokter.nm_dokter," +
                    "form_robson_klasifikasi.kelompok_robson,form_robson_klasifikasi.paritas,form_robson_klasifikasi.riwayat_sc," +
                    "form_robson_klasifikasi.onset_persalinan,form_robson_klasifikasi.presentasi,form_robson_klasifikasi.usia_kehamilan," +
                    "form_robson_klasifikasi.jumlah_janin,form_robson_klasifikasi.indikasi_sc," +
                    "form_robson_klasifikasi.kondisi_ibu,form_robson_klasifikasi.kondisi_bayi,form_robson_klasifikasi.berat_bayi," +
                    "form_robson_klasifikasi.apgar_score,form_robson_klasifikasi.komplikasi,form_robson_klasifikasi.tindakan_tambahan," +
                    "form_robson_klasifikasi.lama_rawat,form_robson_klasifikasi.keterangan " +
                    "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis " +
                    "inner join form_robson_klasifikasi on reg_periksa.no_rawat=form_robson_klasifikasi.no_rawat " +
                    "inner join dokter on form_robson_klasifikasi.kd_dokter=dokter.kd_dokter where " +
                    "form_robson_klasifikasi.tanggal between ? and ? order by form_robson_klasifikasi.tanggal"
                );
            } else {
                ps = koneksi.prepareStatement(
                    "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir," +
                    "form_robson_klasifikasi.tanggal,form_robson_klasifikasi.kd_dokter,dokter.nm_dokter," +
                    "form_robson_klasifikasi.kelompok_robson,form_robson_klasifikasi.paritas,form_robson_klasifikasi.riwayat_sc," +
                    "form_robson_klasifikasi.onset_persalinan,form_robson_klasifikasi.presentasi,form_robson_klasifikasi.usia_kehamilan," +
                    "form_robson_klasifikasi.jumlah_janin,form_robson_klasifikasi.indikasi_sc," +
                    "form_robson_klasifikasi.kondisi_ibu,form_robson_klasifikasi.kondisi_bayi,form_robson_klasifikasi.berat_bayi," +
                    "form_robson_klasifikasi.apgar_score,form_robson_klasifikasi.komplikasi,form_robson_klasifikasi.tindakan_tambahan," +
                    "form_robson_klasifikasi.lama_rawat,form_robson_klasifikasi.keterangan " +
                    "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis " +
                    "inner join form_robson_klasifikasi on reg_periksa.no_rawat=form_robson_klasifikasi.no_rawat " +
                    "inner join dokter on form_robson_klasifikasi.kd_dokter=dokter.kd_dokter where " +
                    "form_robson_klasifikasi.tanggal between ? and ? and (reg_periksa.no_rawat like ? or pasien.no_rkm_medis like ? or " +
                    "pasien.nm_pasien like ? or form_robson_klasifikasi.kd_dokter like ? or dokter.nm_dokter like ?) " +
                    "order by form_robson_klasifikasi.tanggal"
                );
            }

            try {
                if (TCari.getText().trim().equals("")) {
                    ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                    ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                } else {
                    ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                    ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                    ps.setString(3, "%" + TCari.getText() + "%");
                    ps.setString(4, "%" + TCari.getText() + "%");
                    ps.setString(5, "%" + TCari.getText() + "%");
                    ps.setString(6, "%" + TCari.getText() + "%");
                    ps.setString(7, "%" + TCari.getText() + "%");
                }
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"), 
                        rs.getString("tgl_lahir"), rs.getString("jk"), rs.getString("kd_dokter"), 
                        rs.getString("nm_dokter"), rs.getString("tanggal"), rs.getString("kelompok_robson"), 
                        rs.getString("paritas"), rs.getString("riwayat_sc"), rs.getString("onset_persalinan"), 
                        rs.getString("presentasi"), rs.getString("usia_kehamilan"), rs.getString("jumlah_janin"), 
                        rs.getString("indikasi_sc"), rs.getString("kondisi_ibu"), rs.getString("kondisi_bayi"), 
                        rs.getString("berat_bayi"), rs.getString("apgar_score"), rs.getString("komplikasi"), 
                        rs.getString("tindakan_tambahan"), rs.getString("lama_rawat"), rs.getString("keterangan")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }

        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        LCount.setText("" + tabMode.getRowCount());
    }

    public void emptTeks() {
        KelompokRobson.setSelectedIndex(0);
        Paritas.setSelectedIndex(0);
        RiwayatSC.setSelectedIndex(0);
        OnsetPersalinan.setSelectedIndex(0);
        Presentasi.setSelectedIndex(0);
        UsiaKehamilan.setSelectedIndex(0);
        JumlahJanin.setSelectedIndex(0);
        IndikasiSC.setText("");
        KondisiIbu.setSelectedIndex(0);
        KondisiBayi.setSelectedIndex(0);
        BeratBayi.setText("");
        ApgarScore.setText("");
        Komplikasi.setText("");
        TindakanTambahan.setText("");
        LamaRawat.setText("");
        Keterangan.setText("");
        TglAudit.setDate(new Date());
        TabRawat.setSelectedIndex(0);
        KelompokRobson.requestFocus();
    }

    private void getData() {
        if (tbRobson.getSelectedRow() != -1) {
            TNoRw.setText(tbRobson.getValueAt(tbRobson.getSelectedRow(), 0).toString());
            TNoRM.setText(tbRobson.getValueAt(tbRobson.getSelectedRow(), 1).toString());
            TPasien.setText(tbRobson.getValueAt(tbRobson.getSelectedRow(), 2).toString());
            TglLahir.setText(tbRobson.getValueAt(tbRobson.getSelectedRow(), 3).toString());
            Jk.setText(tbRobson.getValueAt(tbRobson.getSelectedRow(), 4).toString());
            KdDokter.setText(tbRobson.getValueAt(tbRobson.getSelectedRow(), 5).toString());
            NmDokter.setText(tbRobson.getValueAt(tbRobson.getSelectedRow(), 6).toString());
            Valid.SetTgl2(TglAudit, tbRobson.getValueAt(tbRobson.getSelectedRow(), 7).toString());
            KelompokRobson.setSelectedItem(tbRobson.getValueAt(tbRobson.getSelectedRow(), 8).toString());
            Paritas.setSelectedItem(tbRobson.getValueAt(tbRobson.getSelectedRow(), 9).toString());
            RiwayatSC.setSelectedItem(tbRobson.getValueAt(tbRobson.getSelectedRow(), 10).toString());
            OnsetPersalinan.setSelectedItem(tbRobson.getValueAt(tbRobson.getSelectedRow(), 11).toString());
            Presentasi.setSelectedItem(tbRobson.getValueAt(tbRobson.getSelectedRow(), 12).toString());
            UsiaKehamilan.setSelectedItem(tbRobson.getValueAt(tbRobson.getSelectedRow(), 13).toString());
            JumlahJanin.setSelectedItem(tbRobson.getValueAt(tbRobson.getSelectedRow(), 14).toString());
            IndikasiSC.setText(tbRobson.getValueAt(tbRobson.getSelectedRow(), 15).toString());
            KondisiIbu.setSelectedItem(tbRobson.getValueAt(tbRobson.getSelectedRow(), 16).toString());
            KondisiBayi.setSelectedItem(tbRobson.getValueAt(tbRobson.getSelectedRow(), 17).toString());
            BeratBayi.setText(tbRobson.getValueAt(tbRobson.getSelectedRow(), 18).toString());
            ApgarScore.setText(tbRobson.getValueAt(tbRobson.getSelectedRow(), 19).toString());
            Komplikasi.setText(tbRobson.getValueAt(tbRobson.getSelectedRow(), 20).toString());
            TindakanTambahan.setText(tbRobson.getValueAt(tbRobson.getSelectedRow(), 21).toString());
            LamaRawat.setText(tbRobson.getValueAt(tbRobson.getSelectedRow(), 22).toString());
            Keterangan.setText(tbRobson.getValueAt(tbRobson.getSelectedRow(), 23).toString());
        }
    }

    private void isRawat() {
        try {
            ps = koneksi.prepareStatement(
                "select reg_periksa.no_rkm_medis,pasien.nm_pasien, if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir,reg_periksa.tgl_registrasi " +
                "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis " +
                "where reg_periksa.no_rawat=?"
            );
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    DTPCari1.setDate(rs.getDate("tgl_registrasi"));
                    TPasien.setText(rs.getString("nm_pasien"));
                    Jk.setText(rs.getString("jk"));
                    TglLahir.setText(rs.getString("tgl_lahir"));
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        DTPCari2.setDate(tgl2);
        isRawat();
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.gettindakan_ranap());
        BtnHapus.setEnabled(akses.gettindakan_ranap());
        BtnEdit.setEnabled(akses.gettindakan_ranap());
        if (akses.getjml2() >= 1) {
            KdDokter.setEditable(false);
            BtnDokter.setEnabled(false);
            KdDokter.setText(akses.getkode());
            NmDokter.setText(dokter.tampil3(KdDokter.getText()));
            if (NmDokter.getText().equals("")) {
                KdDokter.setText("");
                JOptionPane.showMessageDialog(null, "User login bukan Dokter...!!");
            }
        }
    }

    public void setTampil() {
        TabRawat.setSelectedIndex(1);
    }

    private void hapus() {
        if (Sequel.queryu2tf("delete from form_robson_klasifikasi where no_rawat=?", 1, new String[]{
            tbRobson.getValueAt(tbRobson.getSelectedRow(), 0).toString()
        }) == true) {
            tabMode.removeRow(tbRobson.getSelectedRow());
            LCount.setText("" + tabMode.getRowCount());
            TabRawat.setSelectedIndex(1);
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
        }
    }

    private void ganti() {
        if (Sequel.mengedittf("form_robson_klasifikasi", "no_rawat=?", "tanggal=?,kd_dokter=?,kelompok_robson=?,paritas=?,riwayat_sc=?,onset_persalinan=?,presentasi=?,usia_kehamilan=?,jumlah_janin=?,indikasi_sc=?,kondisi_ibu=?,kondisi_bayi=?,berat_bayi=?,apgar_score=?,komplikasi=?,tindakan_tambahan=?,lama_rawat=?,keterangan=?", 19, new String[]{
            Valid.SetTgl(TglAudit.getSelectedItem() + "") + " " + TglAudit.getSelectedItem().toString().substring(11, 19),
            KdDokter.getText(), KelompokRobson.getSelectedItem().toString(), Paritas.getSelectedItem().toString(),
            RiwayatSC.getSelectedItem().toString(), OnsetPersalinan.getSelectedItem().toString(), Presentasi.getSelectedItem().toString(),
            UsiaKehamilan.getSelectedItem().toString(), JumlahJanin.getSelectedItem().toString(), IndikasiSC.getText(),
            KondisiIbu.getSelectedItem().toString(), KondisiBayi.getSelectedItem().toString(), BeratBayi.getText(),
            ApgarScore.getText(), Komplikasi.getText(), TindakanTambahan.getText(), LamaRawat.getText(), Keterangan.getText(),
            tbRobson.getValueAt(tbRobson.getSelectedRow(), 0).toString()
        }) == true) {
            tampil();
            emptTeks();
            TabRawat.setSelectedIndex(1);
        }
    }
}