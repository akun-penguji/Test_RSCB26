/*
 * Kontribusi dari RSUD Cabang Bungin, Bekasi
 */
package rekammedis;

import AESsecurity.EnkripsiAES;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
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
 *
 * @author RSUD Cabang Bungin
 */
public final class RMHasilMCUBaru extends javax.swing.JDialog {

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

    /**
     * Creates new form RMHasilMCUBaru
     *
     * @param parent
     * @param modal
     */
    public RMHasilMCUBaru(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.MCU", "No.RM", "Nama Pasien", "Tgl.Lahir", "J.K.", "Kode Dokter", "Nama Dokter",
            "Tanggal", "Riwayat Sekarang", "Riwayat Dahulu", "Riwayat Keluarga", "Riwayat Kebiasaan",
            "Tanda Vital", "BB/TB", "Kepala", "Mulut", "Leher", "THT", "Thorax", "Abdomen",
            "Ekstremitas", "Hematologi", "Urin", "Kimia Darah", "Radiologi", "EKG", "Kesimpulan", "Saran"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbObat.setModel(tabMode);

        // Set column widths
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 29; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(105);
            } else if (i == 1) {
                column.setPreferredWidth(100);
            } else if (i == 2) {
                column.setPreferredWidth(65);
            } else if (i == 3) {
                column.setPreferredWidth(160);
            } else if (i == 4) {
                column.setPreferredWidth(65);
            } else if (i == 5) {
                column.setPreferredWidth(25);
            } else if (i == 6) {
                column.setPreferredWidth(80);
            } else if (i == 7) {
                column.setPreferredWidth(150);
            } else if (i == 8) {
                column.setPreferredWidth(115);
            } else {
                column.setPreferredWidth(200);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        KdDokter.setDocument(new batasInput((byte) 20).getKata(KdDokter));
        RiwayatPenyakitSekarang.setDocument(new batasInput((int) 2000).getKata(RiwayatPenyakitSekarang));
        RiwayatPenyakitDahulu.setDocument(new batasInput((int) 2000).getKata(RiwayatPenyakitDahulu));
        RiwayatPenyakitKeluarga.setDocument(new batasInput((int) 2000).getKata(RiwayatPenyakitKeluarga));
        RiwayatKebiasaan.setDocument(new batasInput((int) 2000).getKata(RiwayatKebiasaan));
        TandaVital.setDocument(new batasInput((int) 2000).getKata(TandaVital));
        BeratTinggi.setDocument(new batasInput((int) 2000).getKata(BeratTinggi));
        KeadaanUmumKepala.setDocument(new batasInput((int) 2000).getKata(KeadaanUmumKepala));
        KeadaanUmumMulut.setDocument(new batasInput((int) 2000).getKata(KeadaanUmumMulut));
        KeadaanUmumLeher.setDocument(new batasInput((int) 2000).getKata(KeadaanUmumLeher));
        KeadaanUmumTHT.setDocument(new batasInput((int) 2000).getKata(KeadaanUmumTHT));
        KeadaanUmumThorax.setDocument(new batasInput((int) 2000).getKata(KeadaanUmumThorax));
        KeadaanUmumAbdomen.setDocument(new batasInput((int) 2000).getKata(KeadaanUmumAbdomen));
        KeadaanUmumEkstremitas.setDocument(new batasInput((int) 2000).getKata(KeadaanUmumEkstremitas));
        PemeriksaanHematologi.setDocument(new batasInput((int) 2000).getKata(PemeriksaanHematologi));
        PemeriksaanUrin.setDocument(new batasInput((int) 2000).getKata(PemeriksaanUrin));
        PemeriksaanKimiaDarah.setDocument(new batasInput((int) 2000).getKata(PemeriksaanKimiaDarah));
        PemeriksaanRadiologi.setDocument(new batasInput((int) 2000).getKata(PemeriksaanRadiologi));
        PemeriksaanEKG.setDocument(new batasInput((int) 2000).getKata(PemeriksaanEKG));
        Kesimpulan.setDocument(new batasInput((int) 2000).getKata(Kesimpulan));
        Saran.setDocument(new batasInput((int) 2000).getKata(Saran));

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

        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (dokter.getTable().getSelectedRow() != -1) {
                    KdDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                    NmDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                    KdDokter.requestFocus();
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        HTMLEditorKit kit = new HTMLEditorKit();
        LoadHTML.setEditable(true);
        LoadHTML.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule(
                ".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"
                + ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}"
                + ".isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"
                + ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"
                + ".isi5 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#AA0000;}"
                + ".isi6 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#FF0000;}"
                + ".isi7 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#C8C800;}"
                + ".isi8 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#00AA00;}"
                + ".isi9 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#969696;}"
        );
        Document doc = kit.createDefaultDocument();
        LoadHTML.setDocument(doc);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LoadHTML = new widget.editorpane();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnLaporanHasilMCU = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBaru = new widget.Button();
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
        TNoMCU = new widget.TextBox();
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
        jLabel12 = new widget.Label();
        TglAsuhan = new widget.Tanggal();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel38 = new widget.Label();
        jLabel33 = new widget.Label();
        scrollPane7 = new widget.ScrollPane();
        RiwayatPenyakitSekarang = new widget.TextArea();
        jLabel30 = new widget.Label();
        scrollPane2 = new widget.ScrollPane();
        RiwayatPenyakitDahulu = new widget.TextArea();
        jLabel31 = new widget.Label();
        scrollPane3 = new widget.ScrollPane();
        RiwayatPenyakitKeluarga = new widget.TextArea();
        jLabel32 = new widget.Label();
        scrollPane4 = new widget.ScrollPane();
        RiwayatKebiasaan = new widget.TextArea();
        jSeparator12 = new javax.swing.JSeparator();
        jLabel39 = new widget.Label();
        jLabel40 = new widget.Label();
        scrollPane5 = new widget.ScrollPane();
        TandaVital = new widget.TextArea();
        jLabel41 = new widget.Label();
        scrollPane6 = new widget.ScrollPane();
        BeratTinggi = new widget.TextArea();
        jLabel35 = new widget.Label();
        scrollPane8 = new widget.ScrollPane();
        KeadaanUmumKepala = new widget.TextArea();
        jLabel44 = new widget.Label();
        scrollPane9 = new widget.ScrollPane();
        KeadaanUmumMulut = new widget.TextArea();
        jLabel45 = new widget.Label();
        scrollPane10 = new widget.ScrollPane();
        KeadaanUmumLeher = new widget.TextArea();
        jLabel46 = new widget.Label();
        scrollPane11 = new widget.ScrollPane();
        KeadaanUmumTHT = new widget.TextArea();
        jLabel49 = new widget.Label();
        scrollPane12 = new widget.ScrollPane();
        KeadaanUmumThorax = new widget.TextArea();
        jLabel50 = new widget.Label();
        scrollPane13 = new widget.ScrollPane();
        KeadaanUmumAbdomen = new widget.TextArea();
        jLabel51 = new widget.Label();
        scrollPane14 = new widget.ScrollPane();
        KeadaanUmumEkstremitas = new widget.TextArea();
        jSeparator13 = new javax.swing.JSeparator();
        jLabel99 = new widget.Label();
        jLabel79 = new widget.Label();
        jSeparator14 = new javax.swing.JSeparator();
        jLabel100 = new widget.Label();
        scrollPane15 = new widget.ScrollPane();
        PemeriksaanHematologi = new widget.TextArea();
        jLabel76 = new widget.Label();
        scrollPane16 = new widget.ScrollPane();
        PemeriksaanUrin = new widget.TextArea();
        jLabel77 = new widget.Label();
        scrollPane17 = new widget.ScrollPane();
        PemeriksaanKimiaDarah = new widget.TextArea();
        jLabel78 = new widget.Label();
        scrollPane18 = new widget.ScrollPane();
        PemeriksaanRadiologi = new widget.TextArea();
        jLabel52 = new widget.Label();
        scrollPane19 = new widget.ScrollPane();
        PemeriksaanEKG = new widget.TextArea();
        jSeparator15 = new javax.swing.JSeparator();
        jLabel101 = new widget.Label();
        scrollPane20 = new widget.ScrollPane();
        Kesimpulan = new widget.TextArea();
        jSeparator16 = new javax.swing.JSeparator();
        jLabel102 = new widget.Label();
        scrollPane21 = new widget.ScrollPane();
        Saran = new widget.TextArea();
        internalFrame3 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        panelGlass9 = new widget.panelisi();
        jLabel19 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();

        LoadHTML.setBorder(null);
        LoadHTML.setName("LoadHTML"); // NOI18N

        MnLaporanHasilMCU.setBackground(new java.awt.Color(255, 255, 254));
        MnLaporanHasilMCU.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnLaporanHasilMCU.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnLaporanHasilMCU.setText("Laporan Hasil MCU");
        MnLaporanHasilMCU.setName("MnLaporanHasilMCU"); // NOI18N
        MnLaporanHasilMCU.setPreferredSize(new java.awt.Dimension(200, 26));
        MnLaporanHasilMCU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnLaporanHasilMCUActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnLaporanHasilMCU);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Resume Hasil Medical Check Up ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));

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

        BtnBaru.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/New.png"))); // NOI18N
        BtnBaru.setMnemonic('B');
        BtnBaru.setText("Baru");
        BtnBaru.setToolTipText("Alt+B");
        BtnBaru.setName("BtnBaru"); // NOI18N
        BtnBaru.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBaru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBaruActionPerformed(evt);
            }
        });
        BtnBaru.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnBaruKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnBaru);

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

        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        TabRawat.setBackground(new java.awt.Color(254, 255, 254));
        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.setName("TabRawat"); // NOI18N

        internalFrame2.setBorder(null);
        internalFrame2.setName("internalFrame2"); // NOI18N
        internalFrame2.setLayout(new java.awt.BorderLayout(1, 1));

        scrollInput.setName("scrollInput"); // NOI18N
        scrollInput.setPreferredSize(new java.awt.Dimension(102, 557));

        FormInput.setBackground(new java.awt.Color(250, 255, 245));
        FormInput.setBorder(null);
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 2500));
        FormInput.setLayout(null);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(74, 10, 141, 23);

        jLabel16 = new widget.Label();
        jLabel16.setText("No.MCU :");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(220, 36, 70, 23);

        TNoMCU.setHighlighter(null);
        TNoMCU.setName("TNoMCU"); // NOI18N
        FormInput.add(TNoMCU);
        TNoMCU.setBounds(294, 36, 120, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        FormInput.add(TPasien);
        TPasien.setBounds(425, 10, 200, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        FormInput.add(TNoRM);
        TNoRM.setBounds(267, 10, 70, 23);

        label14.setText("Dokter :");
        label14.setName("label14"); // NOI18N
        label14.setPreferredSize(new java.awt.Dimension(35, 23));
        FormInput.add(label14);
        label14.setBounds(445, 40, 50, 23);

        KdDokter.setHighlighter(null);
        KdDokter.setName("KdDokter"); // NOI18N
        KdDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdDokterKeyPressed(evt);
            }
        });
        FormInput.add(KdDokter);
        KdDokter.setBounds(499, 40, 80, 23);

        NmDokter.setEditable(false);
        NmDokter.setName("NmDokter"); // NOI18N
        FormInput.add(NmDokter);
        NmDokter.setBounds(581, 40, 187, 23);

        BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokter.setMnemonic('2');
        BtnDokter.setToolTipText("Alt+2");
        BtnDokter.setName("BtnDokter"); // NOI18N
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
        BtnDokter.setBounds(770, 40, 28, 23);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(630, 10, 60, 23);

        TglLahir.setEditable(false);
        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir"); // NOI18N
        FormInput.add(TglLahir);
        TglLahir.setBounds(695, 10, 80, 23);

        jLabel9.setText("J.K. :");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(780, 10, 30, 23);

        Jk.setEditable(false);
        Jk.setHighlighter(null);
        Jk.setName("Jk"); // NOI18N
        FormInput.add(Jk);
        Jk.setBounds(815, 10, 40, 23);

        jLabel10.setText("No.Rawat :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(0, 10, 70, 23);

        jLabel11.setText("No.RM :");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(220, 10, 45, 23);

        jLabel12.setText("Nama Pasien :");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(340, 10, 85, 23);

        TglAsuhan.setForeground(new java.awt.Color(50, 70, 50));
        TglAsuhan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "13-05-2019" }));
        TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setName("TglAsuhan"); // NOI18N
        TglAsuhan.setOpaque(false);
        TglAsuhan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TglAsuhanKeyPressed(evt);
            }
        });
        FormInput.add(TglAsuhan);
        TglAsuhan.setBounds(74, 40, 130, 23);

        jSeparator1.setBackground(new java.awt.Color(105, 175, 90));
        jSeparator1.setForeground(new java.awt.Color(105, 175, 90));
        jSeparator1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(105, 175, 90)));
        jSeparator1.setName("jSeparator1"); // NOI18N
        FormInput.add(jSeparator1);
        jSeparator1.setBounds(0, 70, 880, 1);

        jLabel38.setForeground(new java.awt.Color(0, 0, 0));
        jLabel38.setText("I. RIWAYAT KESEHATAN");
        jLabel38.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel38.setName("jLabel38"); // NOI18N
        FormInput.add(jLabel38);
        jLabel38.setBounds(10, 80, 150, 23);

        jLabel33.setText("Riwayat Penyakit Sekarang :");
        jLabel33.setName("jLabel33"); // NOI18N
        FormInput.add(jLabel33);
        jLabel33.setBounds(0, 110, 150, 23);

        scrollPane7.setName("scrollPane7"); // NOI18N

        RiwayatPenyakitSekarang.setColumns(20);
        RiwayatPenyakitSekarang.setRows(5);
        RiwayatPenyakitSekarang.setName("RiwayatPenyakitSekarang"); // NOI18N
        scrollPane7.setViewportView(RiwayatPenyakitSekarang);

        FormInput.add(scrollPane7);
        scrollPane7.setBounds(154, 110, 724, 50);

        jLabel30.setText("Riwayat Penyakit Dahulu :");
        jLabel30.setName("jLabel30"); // NOI18N
        FormInput.add(jLabel30);
        jLabel30.setBounds(0, 170, 150, 23);

        scrollPane2.setName("scrollPane2"); // NOI18N

        RiwayatPenyakitDahulu.setColumns(20);
        RiwayatPenyakitDahulu.setRows(5);
        RiwayatPenyakitDahulu.setName("RiwayatPenyakitDahulu"); // NOI18N
        scrollPane2.setViewportView(RiwayatPenyakitDahulu);

        FormInput.add(scrollPane2);
        scrollPane2.setBounds(154, 170, 724, 50);

        jLabel31.setText("Riwayat Penyakit Keluarga :");
        jLabel31.setName("jLabel31"); // NOI18N
        FormInput.add(jLabel31);
        jLabel31.setBounds(0, 230, 150, 23);

        scrollPane3.setName("scrollPane3"); // NOI18N

        RiwayatPenyakitKeluarga.setColumns(20);
        RiwayatPenyakitKeluarga.setRows(5);
        RiwayatPenyakitKeluarga.setName("RiwayatPenyakitKeluarga"); // NOI18N
        scrollPane3.setViewportView(RiwayatPenyakitKeluarga);

        FormInput.add(scrollPane3);
        scrollPane3.setBounds(154, 230, 724, 50);

        jLabel32.setText("Riwayat Kebiasaan :");
        jLabel32.setName("jLabel32"); // NOI18N
        FormInput.add(jLabel32);
        jLabel32.setBounds(0, 290, 150, 23);

        scrollPane4.setName("scrollPane4"); // NOI18N

        RiwayatKebiasaan.setColumns(20);
        RiwayatKebiasaan.setRows(5);
        RiwayatKebiasaan.setName("RiwayatKebiasaan"); // NOI18N
        scrollPane4.setViewportView(RiwayatKebiasaan);

        FormInput.add(scrollPane4);
        scrollPane4.setBounds(154, 290, 724, 50);

        jSeparator12.setBackground(new java.awt.Color(105, 175, 90));
        jSeparator12.setForeground(new java.awt.Color(105, 175, 90));
        jSeparator12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(105, 175, 90)));
        jSeparator12.setName("jSeparator12"); // NOI18N
        FormInput.add(jSeparator12);
        jSeparator12.setBounds(0, 350, 880, 1);

        jLabel39.setForeground(new java.awt.Color(0, 0, 0));
        jLabel39.setText("II. PEMERIKSAAN FISIK");
        jLabel39.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel39.setName("jLabel39"); // NOI18N
        FormInput.add(jLabel39);
        jLabel39.setBounds(10, 360, 150, 23);

        jLabel40.setText("Tanda-tanda Vital :");
        jLabel40.setName("jLabel40"); // NOI18N
        FormInput.add(jLabel40);
        jLabel40.setBounds(0, 390, 150, 23);

        scrollPane5.setName("scrollPane5"); // NOI18N

        TandaVital.setColumns(20);
        TandaVital.setRows(5);
        TandaVital.setName("TandaVital"); // NOI18N
        scrollPane5.setViewportView(TandaVital);

        FormInput.add(scrollPane5);
        scrollPane5.setBounds(154, 390, 724, 50);

        jLabel41.setText("Berat Badan/Tinggi Badan :");
        jLabel41.setName("jLabel41"); // NOI18N
        FormInput.add(jLabel41);
        jLabel41.setBounds(0, 450, 150, 23);

        scrollPane6.setName("scrollPane6"); // NOI18N

        BeratTinggi.setColumns(20);
        BeratTinggi.setRows(5);
        BeratTinggi.setName("BeratTinggi"); // NOI18N
        scrollPane6.setViewportView(BeratTinggi);

        FormInput.add(scrollPane6);
        scrollPane6.setBounds(154, 450, 724, 50);

        jLabel35.setText("Kepala :");
        jLabel35.setName("jLabel35"); // NOI18N
        FormInput.add(jLabel35);
        jLabel35.setBounds(0, 510, 150, 23);

        scrollPane8.setName("scrollPane8"); // NOI18N

        KeadaanUmumKepala.setColumns(20);
        KeadaanUmumKepala.setRows(5);
        KeadaanUmumKepala.setName("KeadaanUmumKepala"); // NOI18N
        scrollPane8.setViewportView(KeadaanUmumKepala);

        FormInput.add(scrollPane8);
        scrollPane8.setBounds(154, 510, 724, 50);

        jLabel44.setText("Mulut :");
        jLabel44.setName("jLabel44"); // NOI18N
        FormInput.add(jLabel44);
        jLabel44.setBounds(0, 570, 150, 23);

        scrollPane9.setName("scrollPane9"); // NOI18N

        KeadaanUmumMulut.setColumns(20);
        KeadaanUmumMulut.setRows(5);
        KeadaanUmumMulut.setName("KeadaanUmumMulut"); // NOI18N
        scrollPane9.setViewportView(KeadaanUmumMulut);

        FormInput.add(scrollPane9);
        scrollPane9.setBounds(154, 570, 724, 50);

        jLabel45.setText("Leher :");
        jLabel45.setName("jLabel45"); // NOI18N
        FormInput.add(jLabel45);
        jLabel45.setBounds(0, 630, 150, 23);

        scrollPane10.setName("scrollPane10"); // NOI18N

        KeadaanUmumLeher.setColumns(20);
        KeadaanUmumLeher.setRows(5);
        KeadaanUmumLeher.setName("KeadaanUmumLeher"); // NOI18N
        scrollPane10.setViewportView(KeadaanUmumLeher);

        FormInput.add(scrollPane10);
        scrollPane10.setBounds(154, 630, 724, 50);

        jLabel46.setText("THT :");
        jLabel46.setName("jLabel46"); // NOI18N
        FormInput.add(jLabel46);
        jLabel46.setBounds(0, 690, 150, 23);

        scrollPane11.setName("scrollPane11"); // NOI18N

        KeadaanUmumTHT.setColumns(20);
        KeadaanUmumTHT.setRows(5);
        KeadaanUmumTHT.setName("KeadaanUmumTHT"); // NOI18N
        scrollPane11.setViewportView(KeadaanUmumTHT);

        FormInput.add(scrollPane11);
        scrollPane11.setBounds(154, 690, 724, 50);

        jLabel49.setText("Thorax :");
        jLabel49.setName("jLabel49"); // NOI18N
        FormInput.add(jLabel49);
        jLabel49.setBounds(0, 750, 150, 23);

        scrollPane12.setName("scrollPane12"); // NOI18N

        KeadaanUmumThorax.setColumns(20);
        KeadaanUmumThorax.setRows(5);
        KeadaanUmumThorax.setName("KeadaanUmumThorax"); // NOI18N
        scrollPane12.setViewportView(KeadaanUmumThorax);

        FormInput.add(scrollPane12);
        scrollPane12.setBounds(154, 750, 724, 50);

        jLabel50.setText("Abdomen :");
        jLabel50.setName("jLabel50"); // NOI18N
        FormInput.add(jLabel50);
        jLabel50.setBounds(0, 810, 150, 23);

        scrollPane13.setName("scrollPane13"); // NOI18N

        KeadaanUmumAbdomen.setColumns(20);
        KeadaanUmumAbdomen.setRows(5);
        KeadaanUmumAbdomen.setName("KeadaanUmumAbdomen"); // NOI18N
        scrollPane13.setViewportView(KeadaanUmumAbdomen);

        FormInput.add(scrollPane13);
        scrollPane13.setBounds(154, 810, 724, 50);

        jLabel51.setText("Ekstremitas :");
        jLabel51.setName("jLabel51"); // NOI18N
        FormInput.add(jLabel51);
        jLabel51.setBounds(0, 870, 150, 23);

        scrollPane14.setName("scrollPane14"); // NOI18N

        KeadaanUmumEkstremitas.setColumns(20);
        KeadaanUmumEkstremitas.setRows(5);
        KeadaanUmumEkstremitas.setName("KeadaanUmumEkstremitas"); // NOI18N
        scrollPane14.setViewportView(KeadaanUmumEkstremitas);

        FormInput.add(scrollPane14);
        scrollPane14.setBounds(154, 870, 724, 50);

        jSeparator13.setBackground(new java.awt.Color(105, 175, 90));
        jSeparator13.setForeground(new java.awt.Color(105, 175, 90));
        jSeparator13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(105, 175, 90)));
        jSeparator13.setName("jSeparator13"); // NOI18N
        FormInput.add(jSeparator13);
        jSeparator13.setBounds(0, 930, 880, 1);

        jLabel99.setForeground(new java.awt.Color(0, 0, 0));
        jLabel99.setText("III. PEMERIKSAAN PENUNJANG");
        jLabel99.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel99.setName("jLabel99"); // NOI18N
        FormInput.add(jLabel99);
        jLabel99.setBounds(10, 940, 200, 23);

        jLabel79.setText("Pemeriksaan Laboratorium :");
        jLabel79.setName("jLabel79"); // NOI18N
        FormInput.add(jLabel79);
        jLabel79.setBounds(0, 970, 150, 23);

        jSeparator14.setBackground(new java.awt.Color(105, 175, 90));
        jSeparator14.setForeground(new java.awt.Color(105, 175, 90));
        jSeparator14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(105, 175, 90)));
        jSeparator14.setName("jSeparator14"); // NOI18N
        FormInput.add(jSeparator14);
        jSeparator14.setBounds(0, 960, 880, 1);

        jLabel100.setText("Hematologi :");
        jLabel100.setName("jLabel100"); // NOI18N
        FormInput.add(jLabel100);
        jLabel100.setBounds(0, 1000, 150, 23);

        scrollPane15.setName("scrollPane15"); // NOI18N

        PemeriksaanHematologi.setColumns(20);
        PemeriksaanHematologi.setRows(5);
        PemeriksaanHematologi.setName("PemeriksaanHematologi"); // NOI18N
        scrollPane15.setViewportView(PemeriksaanHematologi);

        FormInput.add(scrollPane15);
        scrollPane15.setBounds(154, 1000, 724, 50);

        jLabel76.setText("Urin :");
        jLabel76.setName("jLabel76"); // NOI18N
        FormInput.add(jLabel76);
        jLabel76.setBounds(0, 1060, 150, 23);

        scrollPane16.setName("scrollPane16"); // NOI18N

        PemeriksaanUrin.setColumns(20);
        PemeriksaanUrin.setRows(5);
        PemeriksaanUrin.setName("PemeriksaanUrin"); // NOI18N
        scrollPane16.setViewportView(PemeriksaanUrin);

        FormInput.add(scrollPane16);
        scrollPane16.setBounds(154, 1060, 724, 50);

        jLabel77.setText("Kimia Darah :");
        jLabel77.setName("jLabel77"); // NOI18N
        FormInput.add(jLabel77);
        jLabel77.setBounds(0, 1120, 150, 23);

        scrollPane17.setName("scrollPane17"); // NOI18N

        PemeriksaanKimiaDarah.setColumns(20);
        PemeriksaanKimiaDarah.setRows(5);
        PemeriksaanKimiaDarah.setName("PemeriksaanKimiaDarah"); // NOI18N
        scrollPane17.setViewportView(PemeriksaanKimiaDarah);

        FormInput.add(scrollPane17);
        scrollPane17.setBounds(154, 1120, 724, 50);

        jLabel78.setText("Radiologi (Thorax PA) :");
        jLabel78.setName("jLabel78"); // NOI18N
        FormInput.add(jLabel78);
        jLabel78.setBounds(0, 1180, 150, 23);

        scrollPane18.setName("scrollPane18"); // NOI18N

        PemeriksaanRadiologi.setColumns(20);
        PemeriksaanRadiologi.setRows(5);
        PemeriksaanRadiologi.setName("PemeriksaanRadiologi"); // NOI18N
        scrollPane18.setViewportView(PemeriksaanRadiologi);

        FormInput.add(scrollPane18);
        scrollPane18.setBounds(154, 1180, 724, 50);

        jLabel52.setText("Elektrokardiografi (EKG) :");
        jLabel52.setName("jLabel52"); // NOI18N
        FormInput.add(jLabel52);
        jLabel52.setBounds(0, 1240, 150, 23);

        scrollPane19.setName("scrollPane19"); // NOI18N

        PemeriksaanEKG.setColumns(20);
        PemeriksaanEKG.setRows(5);
        PemeriksaanEKG.setName("PemeriksaanEKG"); // NOI18N
        scrollPane19.setViewportView(PemeriksaanEKG);

        FormInput.add(scrollPane19);
        scrollPane19.setBounds(154, 1240, 724, 50);

        jSeparator15.setBackground(new java.awt.Color(105, 175, 90));
        jSeparator15.setForeground(new java.awt.Color(105, 175, 90));
        jSeparator15.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(105, 175, 90)));
        jSeparator15.setName("jSeparator15"); // NOI18N
        FormInput.add(jSeparator15);
        jSeparator15.setBounds(0, 1300, 880, 1);

        jLabel101.setForeground(new java.awt.Color(0, 0, 0));
        jLabel101.setText("KESIMPULAN");
        jLabel101.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel101.setName("jLabel101"); // NOI18N
        FormInput.add(jLabel101);
        jLabel101.setBounds(10, 1310, 100, 23);

        scrollPane20.setName("scrollPane20"); // NOI18N

        Kesimpulan.setColumns(20);
        Kesimpulan.setRows(5);
        Kesimpulan.setName("Kesimpulan"); // NOI18N
        scrollPane20.setViewportView(Kesimpulan);

        FormInput.add(scrollPane20);
        scrollPane20.setBounds(154, 1310, 724, 60);

        jSeparator16.setBackground(new java.awt.Color(105, 175, 90));
        jSeparator16.setForeground(new java.awt.Color(105, 175, 90));
        jSeparator16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(105, 175, 90)));
        jSeparator16.setName("jSeparator16"); // NOI18N
        FormInput.add(jSeparator16);
        jSeparator16.setBounds(0, 1380, 880, 1);

        jLabel102.setForeground(new java.awt.Color(0, 0, 0));
        jLabel102.setText("SARAN");
        jLabel102.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel102.setName("jLabel102"); // NOI18N
        FormInput.add(jLabel102);
        jLabel102.setBounds(10, 1390, 100, 23);

        scrollPane21.setName("scrollPane21"); // NOI18N

        Saran.setColumns(20);
        Saran.setRows(5);
        Saran.setName("Saran"); // NOI18N
        scrollPane21.setViewportView(Saran);

        FormInput.add(scrollPane21);
        scrollPane21.setBounds(154, 1390, 724, 60);

        scrollInput.setViewportView(FormInput);

        internalFrame2.add(scrollInput, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Input Hasil MCU", internalFrame2);

        internalFrame3.setBorder(null);
        internalFrame3.setName("internalFrame3"); // NOI18N
        internalFrame3.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setComponentPopupMenu(jPopupMenu1);

        tbObat.setAutoCreateRowSorter(true);
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

        internalFrame3.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));

        jLabel19.setText("Tanggal :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "13-05-2019" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "13-05-2019" }));
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
        TCari.setPreferredSize(new java.awt.Dimension(205, 23));
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

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(LCount);

        internalFrame3.add(panelGlass9, java.awt.BorderLayout.PAGE_END);

        TabRawat.addTab("Data Hasil MCU", internalFrame3);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else if (KdDokter.getText().trim().equals("")) {
            Valid.textKosong(KdDokter, "Dokter");
        } else {
            if (Sequel.menyimpantf("hasil_mcu", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "No.Rawat", 24, new String[]{
                TNoRw.getText(), TNoMCU.getText(), Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + " " + TglAsuhan.getSelectedItem().toString().substring(11, 19), KdDokter.getText(),
            RiwayatPenyakitSekarang.getText(), RiwayatPenyakitDahulu.getText(), RiwayatPenyakitKeluarga.getText(), RiwayatKebiasaan.getText(),
            TandaVital.getText(), BeratTinggi.getText(), KeadaanUmumKepala.getText(), KeadaanUmumMulut.getText(), KeadaanUmumLeher.getText(),
            KeadaanUmumTHT.getText(), KeadaanUmumThorax.getText(), KeadaanUmumAbdomen.getText(), KeadaanUmumEkstremitas.getText(),
            PemeriksaanHematologi.getText(), PemeriksaanUrin.getText(), PemeriksaanKimiaDarah.getText(), PemeriksaanRadiologi.getText(),
            PemeriksaanEKG.getText(), Kesimpulan.getText(), Saran.getText()
        }) == true) {
            tabMode.addRow(new String[]{
                TNoRw.getText(), TNoRM.getText(), TPasien.getText(), TglLahir.getText(), Jk.getText(), KdDokter.getText(), NmDokter.getText(),
                Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + " " + TglAsuhan.getSelectedItem().toString().substring(11, 19),
                RiwayatPenyakitSekarang.getText(), RiwayatPenyakitDahulu.getText(), RiwayatPenyakitKeluarga.getText(), RiwayatKebiasaan.getText(),
                TandaVital.getText(), BeratTinggi.getText(), KeadaanUmumKepala.getText(), KeadaanUmumMulut.getText(), KeadaanUmumLeher.getText(),
                KeadaanUmumTHT.getText(), KeadaanUmumThorax.getText(), KeadaanUmumAbdomen.getText(), KeadaanUmumEkstremitas.getText(),
                PemeriksaanHematologi.getText(), PemeriksaanUrin.getText(), PemeriksaanKimiaDarah.getText(), PemeriksaanRadiologi.getText(),
                PemeriksaanEKG.getText(), Kesimpulan.getText(), Saran.getText()
            });
            emptTeks();
            LCount.setText("" + tabMode.getRowCount());
        }
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnKeluar, BtnBaru);
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBaruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBaruActionPerformed
        emptTeks();
    }//GEN-LAST:event_BtnBaruActionPerformed

    private void BtnBaruKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBaruKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            emptTeks();
        } else {
            Valid.pindah(evt, BtnSimpan, BtnHapus);
        }
    }//GEN-LAST:event_BtnBaruKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (tbObat.getSelectedRow() > -1) {
            if (akses.getpenilaian_mcu() == true) {
                hapus();
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
        }
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnHapusActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnBaru, BtnEdit);
        }
    }//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else if (KdDokter.getText().trim().equals("")) {
            Valid.textKosong(KdDokter, "Dokter");
        } else {
            if (tbObat.getSelectedRow() > -1) {
                if (akses.getpenilaian_mcu() == true) {
                    ganti();
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
            }
        }
    }//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnEditActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
    }//GEN-LAST:event_BtnEditKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dokter.dispose();
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnKeluarActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnEdit, TCari);
        }
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (tbObat.getSelectedRow() > -1) {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));            
            param.put("logo2", Sequel.cariGambar("select setting.logo2 from setting"));
            param.put("verifikasi", "https://" + koneksiDB.URLVERIFIKASI() + "/verify/?id=" + EnkripsiAES.encrypt("{'x': 'mcu', 't':'" + TNoRw.getText() + "'}"));
            finger = Sequel.cariIsi("select sha1(concat(ifnull(pasien.no_ktp,''),ifnull(pasien.nm_pasien,''),ifnull(date_format(pasien.tgl_lahir,'%d-%m-%Y'),''),ifnull(pasien.jk,''),ifnull(pasien.nm_ibu,''),ifnull(hasil_mcu.tanggal,''),ifnull(hasil_mcu.kd_dokter,''),ifnull(hasil_mcu.riwayat_sekarang,''),ifnull(hasil_mcu.riwayat_dahulu,''),ifnull(hasil_mcu.riwayat_keluarga,''))) as finger from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis inner join hasil_mcu on reg_periksa.no_rawat=hasil_mcu.no_rawat where hasil_mcu.no_rawat=?", TNoRw.getText());
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString() + "\nID " + (finger.equals("") ? tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString() : finger) + "\n" + Sequel.cariIsi("select DATE_FORMAT(reg_periksa.tgl_registrasi,'%d-%m-%Y') from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText()));
            String qrText = "Dr. " + tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString() + 
                           " | Kode: " + tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString() +
                           " | MCU: " + TNoRw.getText();
            param.put("qrcode", generateQRCodeImage(qrText));
            param.put("no_rawat", TNoRw.getText());
            Valid.MyReportqry("rptHasilMCULengkap.jasper", "report", "::[ Laporan Hasil MCU ]::",
                    "select reg_periksa.no_rawat,hasil_mcu.no_mcu,pasien.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tmp_lahir,pasien.tgl_lahir,pasien.gol_darah,pasien.stts_nikah,"
                    + "pasien.agama,pasien.tgl_daftar,pasien.no_tlp,pasien.alamat,pasien.pekerjaan,pasien.umur,pasien.pnd,pasien.keluarga,pasien.namakeluarga,penjab.png_jawab,"
                    + "hasil_mcu.tanggal,hasil_mcu.kd_dokter,dokter.nm_dokter,hasil_mcu.riwayat_sekarang,hasil_mcu.riwayat_dahulu,hasil_mcu.riwayat_keluarga,"
                    + "hasil_mcu.riwayat_kebiasaan,hasil_mcu.tanda_vital,hasil_mcu.bb_tb,hasil_mcu.kepala,hasil_mcu.mulut,hasil_mcu.leher,hasil_mcu.tht,"
                    + "hasil_mcu.thorax,hasil_mcu.abdomen,hasil_mcu.ekstremitas,hasil_mcu.hematologi,hasil_mcu.urin,hasil_mcu.kimia_darah,hasil_mcu.radiologi,"
                    + "hasil_mcu.ekg,hasil_mcu.kesimpulan,hasil_mcu.saran from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                    + "inner join hasil_mcu on reg_periksa.no_rawat=hasil_mcu.no_rawat inner join dokter on hasil_mcu.kd_dokter=dokter.kd_dokter "
                    + "inner join penjab on reg_periksa.kd_pj=penjab.kd_pj where hasil_mcu.no_rawat='" + TNoRw.getText() + "'", param);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPrintActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnEdit, BtnKeluar);
        }
    }//GEN-LAST:event_BtnPrintKeyPressed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException ex) {
            }
        }
    }//GEN-LAST:event_tbObatMouseClicked

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyPressed
        if (tabMode.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                try {
                    getData();
                } catch (java.lang.NullPointerException ex) {
                }
            }
        }
    }//GEN-LAST:event_tbObatKeyPressed

    private void MnLaporanHasilMCUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnLaporanHasilMCUActionPerformed
        if (tbObat.getSelectedRow() > -1) {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            param.put("verifikasi", "https://" + koneksiDB.URLVERIFIKASI() + "/verify/?id=" + EnkripsiAES.encrypt("{'x': 'mcu', 't':'" + TNoRw.getText() + "'}"));
            finger = Sequel.cariIsi("select sha1(concat(ifnull(pasien.no_ktp,''),ifnull(pasien.nm_pasien,''),ifnull(date_format(pasien.tgl_lahir,'%d-%m-%Y'),''),ifnull(pasien.jk,''),ifnull(pasien.nm_ibu,''),ifnull(hasil_mcu.tanggal,''),ifnull(hasil_mcu.kd_dokter,''),ifnull(hasil_mcu.riwayat_sekarang,''),ifnull(hasil_mcu.riwayat_dahulu,''),ifnull(hasil_mcu.riwayat_keluarga,''))) as finger from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis inner join hasil_mcu on reg_periksa.no_rawat=hasil_mcu.no_rawat where hasil_mcu.no_rawat=?", TNoRw.getText());
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + Sequel.cariIsi("select dokter.nm_dokter from hasil_mcu inner join dokter on hasil_mcu.kd_dokter=dokter.kd_dokter where hasil_mcu.no_rawat=?", TNoRw.getText()) + "\nID " + (finger.equals("") ? Sequel.cariIsi("select hasil_mcu.kd_dokter from hasil_mcu where hasil_mcu.no_rawat=?", TNoRw.getText()) : finger) + "\n" + Sequel.cariIsi("select DATE_FORMAT(reg_periksa.tgl_registrasi,'%d-%m-%Y') from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText()));
            param.put("no_rawat", TNoRw.getText());
            Valid.MyReportqry("rptHasilMCULengkap.jasper", "report", "::[ Laporan Hasil MCU ]::",
                    "select reg_periksa.no_rawat,hasil_mcu.no_mcu,pasien.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tmp_lahir,pasien.tgl_lahir,pasien.gol_darah,pasien.stts_nikah,"
                    + "pasien.agama,pasien.tgl_daftar,pasien.no_tlp,pasien.alamat,pasien.pekerjaan,pasien.umur,pasien.pnd,pasien.keluarga,pasien.namakeluarga,penjab.png_jawab,"
                    + "hasil_mcu.tanggal,hasil_mcu.kd_dokter,dokter.nm_dokter,hasil_mcu.riwayat_sekarang,hasil_mcu.riwayat_dahulu,hasil_mcu.riwayat_keluarga,"
                    + "hasil_mcu.riwayat_kebiasaan,hasil_mcu.tanda_vital,hasil_mcu.bb_tb,hasil_mcu.kepala,hasil_mcu.mulut,hasil_mcu.leher,hasil_mcu.tht,"
                    + "hasil_mcu.thorax,hasil_mcu.abdomen,hasil_mcu.ekstremitas,hasil_mcu.hematologi,hasil_mcu.urin,hasil_mcu.kimia_darah,hasil_mcu.radiologi,"
                    + "hasil_mcu.ekg,hasil_mcu.kesimpulan,hasil_mcu.saran from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                    + "inner join hasil_mcu on reg_periksa.no_rawat=hasil_mcu.no_rawat inner join dokter on hasil_mcu.kd_dokter=dokter.kd_dokter "
                    + "inner join penjab on reg_periksa.kd_pj=penjab.kd_pj where hasil_mcu.no_rawat='" + TNoRw.getText() + "'", param);
        }
    }//GEN-LAST:event_MnLaporanHasilMCUActionPerformed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        tampil();
        emptTeks();
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            TCari.setText("");
            tampil();
            emptTeks();
        } else {
            Valid.pindah(evt, BtnPrint, BtnKeluar);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", TNoRM, TNoRw.getText());
            Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TPasien, TNoRM.getText());
            Sequel.cariIsi("select tgl_lahir from pasien where no_rkm_medis=?", TglLahir, TNoRM.getText());
            Sequel.cariIsi("select jk from pasien where no_rkm_medis=?", Jk, TNoRM.getText());
        } else {
            Valid.pindah(evt, TCari, TglAsuhan);
        }
    }//GEN-LAST:event_TNoRwKeyPressed

    private void TglAsuhanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TglAsuhanKeyPressed
        Valid.pindah(evt, TNoRw, KdDokter);
    }//GEN-LAST:event_TglAsuhanKeyPressed

    private void KdDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdDokterKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            NmDokter.setText(dokter.tampil3(KdDokter.getText()));
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            BtnDokterActionPerformed(null);
        } else {
            Valid.pindah(evt, TglAsuhan, BtnDokter);
        }
    }//GEN-LAST:event_KdDokterKeyPressed

    private void BtnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokterActionPerformed
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setAlwaysOnTop(false);
        dokter.setVisible(true);
    }//GEN-LAST:event_BtnDokterActionPerformed

    private void BtnDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnDokterKeyPressed
        Valid.pindah(evt, KdDokter, RiwayatPenyakitSekarang);
    }//GEN-LAST:event_BtnDokterKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampil();
    }//GEN-LAST:event_formWindowOpened

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampil();
        }
    }//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnCariActionPerformed(null);
        } else {
            Valid.pindah(evt, TCari, BtnAll);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMHasilMCUBaru dialog = new RMHasilMCUBaru(new javax.swing.JFrame(), true);
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
    private widget.TextArea BeratTinggi;
    private widget.Button BtnAll;
    private widget.Button BtnBaru;
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
    private widget.TextBox Jk;
    private widget.TextBox KdDokter;
    private widget.TextArea KeadaanUmumAbdomen;
    private widget.TextArea KeadaanUmumEkstremitas;
    private widget.TextArea KeadaanUmumKepala;
    private widget.TextArea KeadaanUmumLeher;
    private widget.TextArea KeadaanUmumMulut;
    private widget.TextArea KeadaanUmumTHT;
    private widget.TextArea KeadaanUmumThorax;
    private widget.TextArea Kesimpulan;
    private widget.Label LCount;
    private widget.editorpane LoadHTML;
    private javax.swing.JMenuItem MnLaporanHasilMCU;
    private widget.TextBox NmDokter;
    private widget.TextArea PemeriksaanEKG;
    private widget.TextArea PemeriksaanHematologi;
    private widget.TextArea PemeriksaanKimiaDarah;
    private widget.TextArea PemeriksaanRadiologi;
    private widget.TextArea PemeriksaanUrin;
    private widget.TextArea RiwayatKebiasaan;
    private widget.TextArea RiwayatPenyakitDahulu;
    private widget.TextArea RiwayatPenyakitKeluarga;
    private widget.TextArea RiwayatPenyakitSekarang;
    private widget.TextArea Saran;
    private widget.ScrollPane Scroll;
    private javax.swing.JTabbedPane TabRawat;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TNoMCU;
    private widget.TextBox TPasien;
    private widget.TextBox TglLahir;
    private widget.Tanggal TglAsuhan;
    private widget.TextArea TandaVital;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame2;
    private widget.InternalFrame internalFrame3;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel16;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel30;
    private widget.Label jLabel31;
    private widget.Label jLabel32;
    private widget.Label jLabel33;
    private widget.Label jLabel35;
    private widget.Label jLabel38;
    private widget.Label jLabel39;
    private widget.Label jLabel40;
    private widget.Label jLabel41;
    private widget.Label jLabel44;
    private widget.Label jLabel45;
    private widget.Label jLabel46;
    private widget.Label jLabel49;
    private widget.Label jLabel50;
    private widget.Label jLabel51;
    private widget.Label jLabel52;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel76;
    private widget.Label jLabel77;
    private widget.Label jLabel78;
    private widget.Label jLabel79;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private widget.Label jLabel99;
    private widget.Label jLabel100;
    private widget.Label jLabel101;
    private widget.Label jLabel102;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JSeparator jSeparator16;
    private widget.Label label14;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.ScrollPane scrollInput;
    private widget.ScrollPane scrollPane10;
    private widget.ScrollPane scrollPane11;
    private widget.ScrollPane scrollPane12;
    private widget.ScrollPane scrollPane13;
    private widget.ScrollPane scrollPane14;
    private widget.ScrollPane scrollPane15;
    private widget.ScrollPane scrollPane16;
    private widget.ScrollPane scrollPane17;
    private widget.ScrollPane scrollPane18;
    private widget.ScrollPane scrollPane19;
    private widget.ScrollPane scrollPane2;
    private widget.ScrollPane scrollPane20;
    private widget.ScrollPane scrollPane21;
    private widget.ScrollPane scrollPane3;
    private widget.ScrollPane scrollPane4;
    private widget.ScrollPane scrollPane5;
    private widget.ScrollPane scrollPane6;
    private widget.ScrollPane scrollPane7;
    private widget.ScrollPane scrollPane8;
    private widget.ScrollPane scrollPane9;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoMCU.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
            TglLahir.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
            Jk.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            KdDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
            NmDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString());
            Valid.SetTgl2(TglAsuhan, tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString());
            RiwayatPenyakitSekarang.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString());
            RiwayatPenyakitDahulu.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString());
            RiwayatPenyakitKeluarga.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString());
            RiwayatKebiasaan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString());
            TandaVital.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 13).toString());
            BeratTinggi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 14).toString());
            KeadaanUmumKepala.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 15).toString());
            KeadaanUmumMulut.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 16).toString());
            KeadaanUmumLeher.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 17).toString());
            KeadaanUmumTHT.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 18).toString());
            KeadaanUmumThorax.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 19).toString());
            KeadaanUmumAbdomen.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 20).toString());
            KeadaanUmumEkstremitas.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 21).toString());
            PemeriksaanHematologi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 22).toString());
            PemeriksaanUrin.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 23).toString());
            PemeriksaanKimiaDarah.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 24).toString());
            PemeriksaanRadiologi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 25).toString());
            PemeriksaanEKG.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 26).toString());
            Kesimpulan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 27).toString());
            Saran.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 28).toString());
        }
    }

    private void isRawat() {
        try {
            ps = koneksi.prepareStatement("select no_rkm_medis from reg_periksa where no_rawat=?");
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    ps2 = koneksi.prepareStatement("select nm_pasien, tgl_lahir, jk from pasien where no_rkm_medis=?");
                    try {
                        ps2.setString(1, TNoRM.getText());
                        rs2 = ps.executeQuery();
                        if (rs2.next()) {
                            TPasien.setText(rs2.getString("nm_pasien"));
                            TglLahir.setText(rs2.getString("tgl_lahir"));
                            Jk.setText(rs2.getString("jk"));
                        }
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                    } finally {
                        if (rs2 != null) {
                            rs2.close();
                        }
                        if (ps2 != null) {
                            ps2.close();
                        }
                    }
                } else {
                    TNoRM.setText("");
                    TPasien.setText("");
                    TglLahir.setText("");
                    Jk.setText("");
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

    private PreparedStatement ps2;
    private ResultSet rs2;

    public void setNoRm(String norwt, Date date1, Date date2) {
        TNoRw.setText(norwt);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat='" + norwt + "'"));
        TPasien.setText(Sequel.cariIsi("select pasien.nm_pasien from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis where reg_periksa.no_rawat='" + norwt + "'"));
        TglLahir.setText(Sequel.cariIsi("select pasien.tgl_lahir from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis where reg_periksa.no_rawat='" + norwt + "'"));
        Jk.setText(Sequel.cariIsi("select pasien.jk from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis where reg_periksa.no_rawat='" + norwt + "'"));
        KdDokter.setText(Sequel.cariIsi("select kd_dokter from reg_periksa where no_rawat='" + norwt + "'"));
        NmDokter.setText(Sequel.cariIsi("select dokter.nm_dokter from reg_periksa inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter where reg_periksa.no_rawat='" + norwt + "'"));
        if (date1 != null) {
            DTPCari1.setDate(date1);
        }
        if (date2 != null) {
            DTPCari2.setDate(date2);
        }
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getpenilaian_mcu());
        BtnHapus.setEnabled(akses.getpenilaian_mcu());
        BtnEdit.setEnabled(akses.getpenilaian_mcu());
        BtnPrint.setEnabled(akses.getpenilaian_mcu());
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date());
        
        // Auto generate MCU number - similar to SuratKeteranganSehatJasmani pattern
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM");
        String currentYearMonth = sdf.format(new Date());
        Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(hasil_mcu.no_mcu,3),signed)),0) from hasil_mcu where DATE_FORMAT(hasil_mcu.tanggal,'%Y/%m')='" + currentYearMonth + "'",
                "MCU/" + currentYearMonth + "/", 3, TNoMCU);
        
        RiwayatPenyakitSekarang.setText("");
        RiwayatPenyakitDahulu.setText("");
        RiwayatPenyakitKeluarga.setText("");
        RiwayatKebiasaan.setText("");
        TandaVital.setText("");
        BeratTinggi.setText("");
        KeadaanUmumKepala.setText("");
        KeadaanUmumMulut.setText("");
        KeadaanUmumLeher.setText("");
        KeadaanUmumTHT.setText("");
        KeadaanUmumThorax.setText("");
        KeadaanUmumAbdomen.setText("");
        KeadaanUmumEkstremitas.setText("");
        PemeriksaanHematologi.setText("");
        PemeriksaanUrin.setText("");
        PemeriksaanKimiaDarah.setText("");
        PemeriksaanRadiologi.setText("");
        PemeriksaanEKG.setText("");
        Kesimpulan.setText("");
        Saran.setText("");
        TabRawat.setSelectedIndex(0);
        TNoRw.requestFocus();
    }

    private void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            if (TCari.getText().toString().trim().equals("")) {
                ps = koneksi.prepareStatement(
                        "select reg_periksa.no_rawat,IFNULL(hasil_mcu.no_mcu,'') as no_mcu,pasien.no_rkm_medis,pasien.nm_pasien,pasien.tgl_lahir,pasien.jk,hasil_mcu.kd_dokter,dokter.nm_dokter,"
                        + "hasil_mcu.tanggal,hasil_mcu.riwayat_sekarang,hasil_mcu.riwayat_dahulu,hasil_mcu.riwayat_keluarga,hasil_mcu.riwayat_kebiasaan,"
                        + "hasil_mcu.tanda_vital,hasil_mcu.bb_tb,hasil_mcu.kepala,hasil_mcu.mulut,hasil_mcu.leher,hasil_mcu.tht,hasil_mcu.thorax,"
                        + "hasil_mcu.abdomen,hasil_mcu.ekstremitas,hasil_mcu.hematologi,hasil_mcu.urin,hasil_mcu.kimia_darah,hasil_mcu.radiologi,"
                        + "hasil_mcu.ekg,hasil_mcu.kesimpulan,hasil_mcu.saran from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "inner join hasil_mcu on reg_periksa.no_rawat=hasil_mcu.no_rawat inner join dokter on hasil_mcu.kd_dokter=dokter.kd_dokter where "
                        + "DATE(hasil_mcu.tanggal) between ? and ? order by hasil_mcu.tanggal");
            } else {
                ps = koneksi.prepareStatement(
                        "select reg_periksa.no_rawat,IFNULL(hasil_mcu.no_mcu,'') as no_mcu,pasien.no_rkm_medis,pasien.nm_pasien,pasien.tgl_lahir,pasien.jk,hasil_mcu.kd_dokter,dokter.nm_dokter,"
                        + "hasil_mcu.tanggal,hasil_mcu.riwayat_sekarang,hasil_mcu.riwayat_dahulu,hasil_mcu.riwayat_keluarga,hasil_mcu.riwayat_kebiasaan,"
                        + "hasil_mcu.tanda_vital,hasil_mcu.bb_tb,hasil_mcu.kepala,hasil_mcu.mulut,hasil_mcu.leher,hasil_mcu.tht,hasil_mcu.thorax,"
                        + "hasil_mcu.abdomen,hasil_mcu.ekstremitas,hasil_mcu.hematologi,hasil_mcu.urin,hasil_mcu.kimia_darah,hasil_mcu.radiologi,"
                        + "hasil_mcu.ekg,hasil_mcu.kesimpulan,hasil_mcu.saran from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "inner join hasil_mcu on reg_periksa.no_rawat=hasil_mcu.no_rawat inner join dokter on hasil_mcu.kd_dokter=dokter.kd_dokter where "
                        + "DATE(hasil_mcu.tanggal) between ? and ? and (reg_periksa.no_rawat like ? or pasien.no_rkm_medis like ? or pasien.nm_pasien like ? or "
                        + "dokter.nm_dokter like ?) order by hasil_mcu.tanggal");
            }
            try {
                if (TCari.getText().toString().trim().equals("")) {
                    ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
                    ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
                } else {
                    ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
                    ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
                    ps.setString(3, "%" + TCari.getText() + "%");
                    ps.setString(4, "%" + TCari.getText() + "%");
                    ps.setString(5, "%" + TCari.getText() + "%");
                    ps.setString(6, "%" + TCari.getText() + "%");
                }
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat"), rs.getString("no_mcu"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"), rs.getString("tgl_lahir"), rs.getString("jk"),
                        rs.getString("kd_dokter"), rs.getString("nm_dokter"), rs.getString("tanggal"), rs.getString("riwayat_sekarang"), rs.getString("riwayat_dahulu"),
                        rs.getString("riwayat_keluarga"), rs.getString("riwayat_kebiasaan"), rs.getString("tanda_vital"), rs.getString("bb_tb"), rs.getString("kepala"),
                        rs.getString("mulut"), rs.getString("leher"), rs.getString("tht"), rs.getString("thorax"), rs.getString("abdomen"), rs.getString("ekstremitas"),
                        rs.getString("hematologi"), rs.getString("urin"), rs.getString("kimia_darah"), rs.getString("radiologi"), rs.getString("ekg"),
                        rs.getString("kesimpulan"), rs.getString("saran")
                    });
                }
            } catch (Exception e) {
                System.out.println("Error in tampil(): " + e.getMessage());
                e.printStackTrace();
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

    public void hapus() {
        if (Sequel.queryu2tf("delete from hasil_mcu where no_rawat=?", 1, new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
        }) == true) {
            tabMode.removeRow(tbObat.getSelectedRow());
            LCount.setText("" + tabMode.getRowCount());
            TabRawat.setSelectedIndex(1);
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
        }
    }

    public void ganti() {
        if (Sequel.mengedittf("hasil_mcu", "no_rawat=?", "no_mcu=?,tanggal=?,kd_dokter=?,riwayat_sekarang=?,riwayat_dahulu=?,riwayat_keluarga=?,riwayat_kebiasaan=?,"
                + "tanda_vital=?,bb_tb=?,kepala=?,mulut=?,leher=?,tht=?,thorax=?,abdomen=?,ekstremitas=?,hematologi=?,urin=?,kimia_darah=?,"
                + "radiologi=?,ekg=?,kesimpulan=?,saran=?", 24, new String[]{
            TNoMCU.getText(),
            Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + " " + TglAsuhan.getSelectedItem().toString().substring(11, 19), KdDokter.getText(),
            RiwayatPenyakitSekarang.getText(), RiwayatPenyakitDahulu.getText(), RiwayatPenyakitKeluarga.getText(), RiwayatKebiasaan.getText(),
            TandaVital.getText(), BeratTinggi.getText(), KeadaanUmumKepala.getText(), KeadaanUmumMulut.getText(), KeadaanUmumLeher.getText(),
            KeadaanUmumTHT.getText(), KeadaanUmumThorax.getText(), KeadaanUmumAbdomen.getText(), KeadaanUmumEkstremitas.getText(),
            PemeriksaanHematologi.getText(), PemeriksaanUrin.getText(), PemeriksaanKimiaDarah.getText(), PemeriksaanRadiologi.getText(),
            PemeriksaanEKG.getText(), Kesimpulan.getText(), Saran.getText(), tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
        }) == true) {
            tbObat.setValueAt(TNoRw.getText(), tbObat.getSelectedRow(), 0);
            tbObat.setValueAt(TNoRM.getText(), tbObat.getSelectedRow(), 1);
            tbObat.setValueAt(TPasien.getText(), tbObat.getSelectedRow(), 2);
            tbObat.setValueAt(TglLahir.getText(), tbObat.getSelectedRow(), 3);
            tbObat.setValueAt(Jk.getText(), tbObat.getSelectedRow(), 4);
            tbObat.setValueAt(KdDokter.getText(), tbObat.getSelectedRow(), 5);
            tbObat.setValueAt(NmDokter.getText(), tbObat.getSelectedRow(), 6);
            tbObat.setValueAt(Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + " " + TglAsuhan.getSelectedItem().toString().substring(11, 19), tbObat.getSelectedRow(), 7);
            tbObat.setValueAt(RiwayatPenyakitSekarang.getText(), tbObat.getSelectedRow(), 8);
            tbObat.setValueAt(RiwayatPenyakitDahulu.getText(), tbObat.getSelectedRow(), 9);
            tbObat.setValueAt(RiwayatPenyakitKeluarga.getText(), tbObat.getSelectedRow(), 10);
            tbObat.setValueAt(RiwayatKebiasaan.getText(), tbObat.getSelectedRow(), 11);
            tbObat.setValueAt(TandaVital.getText(), tbObat.getSelectedRow(), 12);
            tbObat.setValueAt(BeratTinggi.getText(), tbObat.getSelectedRow(), 13);
            tbObat.setValueAt(KeadaanUmumKepala.getText(), tbObat.getSelectedRow(), 14);
            tbObat.setValueAt(KeadaanUmumMulut.getText(), tbObat.getSelectedRow(), 15);
            tbObat.setValueAt(KeadaanUmumLeher.getText(), tbObat.getSelectedRow(), 16);
            tbObat.setValueAt(KeadaanUmumTHT.getText(), tbObat.getSelectedRow(), 17);
            tbObat.setValueAt(KeadaanUmumThorax.getText(), tbObat.getSelectedRow(), 18);
            tbObat.setValueAt(KeadaanUmumAbdomen.getText(), tbObat.getSelectedRow(), 19);
            tbObat.setValueAt(KeadaanUmumEkstremitas.getText(), tbObat.getSelectedRow(), 20);
            tbObat.setValueAt(PemeriksaanHematologi.getText(), tbObat.getSelectedRow(), 21);
            tbObat.setValueAt(PemeriksaanUrin.getText(), tbObat.getSelectedRow(), 22);
            tbObat.setValueAt(PemeriksaanKimiaDarah.getText(), tbObat.getSelectedRow(), 23);
            tbObat.setValueAt(PemeriksaanRadiologi.getText(), tbObat.getSelectedRow(), 24);
            tbObat.setValueAt(PemeriksaanEKG.getText(), tbObat.getSelectedRow(), 25);
            tbObat.setValueAt(Kesimpulan.getText(), tbObat.getSelectedRow(), 26);
            tbObat.setValueAt(Saran.getText(), tbObat.getSelectedRow(), 27);
            emptTeks();
            TabRawat.setSelectedIndex(1);
        }
    }

    private ByteArrayInputStream generateQRCodeImage(String text) {
        try {
            return new ByteArrayInputStream(new byte[0]); // Placeholder
        } catch (Exception e) {
            return new ByteArrayInputStream(new byte[0]);
        }
    }
}