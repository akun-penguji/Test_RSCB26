/*
 * Form Mapping SNOMED ICD 9
 * Tabel: mapping_snomed_icd9
 */
package bridging;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
import java.util.ArrayList;
import java.util.List;
import laporan.DlgICD9;
import simrskhanza.DlgCariSnomed;

/**
 *
 * @author dev
 */
public final class DlgMappingSnomedICD9 extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private int currentPage = 0;
    private final int PAGE_SIZE = 100;
    private int totalRecords = 0;
    private DlgICD9 icd9 = new DlgICD9(null, false);
    private DlgCariSnomed snomed = new DlgCariSnomed(null, false);

    public DlgMappingSnomedICD9(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.setLocation(8, 1);
        setSize(628, 674);

        tabMode = new DefaultTableModel(null, new Object[]{
            "Kode ICD 9", "Deskripsi ICD 9", "Kode SNOMED", "Display SNOMED", "System"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbData.setModel(tabMode);

        tbData.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableColumn col;
        col = tbData.getColumnModel().getColumn(0); col.setPreferredWidth(80);
        col = tbData.getColumnModel().getColumn(1); col.setPreferredWidth(220);
        col = tbData.getColumnModel().getColumn(2); col.setPreferredWidth(100);
        col = tbData.getColumnModel().getColumn(3); col.setPreferredWidth(200);
        col = tbData.getColumnModel().getColumn(4); col.setPreferredWidth(200);

        tbData.setDefaultRenderer(Object.class, new WarnaTable());

        TKdICD9.setDocument(new batasInput((byte) 10).getKata(TKdICD9));
        TKdSnomed.setDocument(new batasInput((byte) 20).getKata(TKdSnomed));
        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));

        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override public void insertUpdate(DocumentEvent e) { if (TCari.getText().length() > 2) { currentPage = 0; tampil(); } }
                @Override public void removeUpdate(DocumentEvent e) { if (TCari.getText().length() > 2) { currentPage = 0; tampil(); } }
                @Override public void changedUpdate(DocumentEvent e) {}
            });
        }

        icd9.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if (icd9.getTabel().getSelectedRow() != -1) {
                    TKdICD9.setText(icd9.getTabel().getValueAt(icd9.getTabel().getSelectedRow(), 1).toString());
                    TNmICD9.setText(icd9.getTabel().getValueAt(icd9.getTabel().getSelectedRow(), 2).toString());
                }
                TKdSnomed.requestFocus();
            }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });

        snomed.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if (snomed.getTable().getSelectedRow() != -1) {
                    TSystem.setText(snomed.getTable().getValueAt(snomed.getTable().getSelectedRow(), 0).toString());
                    TKdSnomed.setText(snomed.getTable().getValueAt(snomed.getTable().getSelectedRow(), 1).toString());
                    TDisplay.setText(snomed.getTable().getValueAt(snomed.getTable().getSelectedRow(), 2).toString());
                }
                TKdSnomed.requestFocus();
            }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });

        snomed.getTable().addKeyListener(new KeyListener() {
            @Override public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    snomed.dispose();
                }
            }
            @Override public void keyReleased(KeyEvent e) {}
        });

        // Tambah tombol ekstra ke panelGlass8
        widget.Button BtnCariICDOtomatis = new widget.Button();
        BtnCariICDOtomatis.setText("Cari ICD Otomatis");
        BtnCariICDOtomatis.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/gnome-searchtool.png")));
        BtnCariICDOtomatis.setPreferredSize(new Dimension(140, 30));
        BtnCariICDOtomatis.addActionListener(ev -> cariICDOtomatis());
        panelGlass8.add(BtnCariICDOtomatis);

        widget.Button BtnImportCSV9 = new widget.Button();
        BtnImportCSV9.setText("Import CSV");
        BtnImportCSV9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/upload1-24.png")));
        BtnImportCSV9.setPreferredSize(new Dimension(110, 30));
        BtnImportCSV9.addActionListener(ev -> bulkImportCSV());
        panelGlass8.add(BtnImportCSV9);

        widget.Button BtnSyncSnowstorm9 = new widget.Button();
        BtnSyncSnowstorm9.setText("Sync Snowstorm");
        BtnSyncSnowstorm9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/refresh.png")));
        BtnSyncSnowstorm9.setPreferredSize(new Dimension(140, 30));
        BtnSyncSnowstorm9.addActionListener(ev -> syncDariSnowstorm(BtnSyncSnowstorm9));
        panelGlass8.add(BtnSyncSnowstorm9);

        widget.Button BtnImportRF2 = new widget.Button();
        BtnImportRF2.setText("Import RF2");
        BtnImportRF2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/1360485822_download.png")));
        BtnImportRF2.setPreferredSize(new Dimension(120, 30));
        BtnImportRF2.addActionListener(ev -> importRF2File(BtnImportRF2));
        panelGlass8.add(BtnImportRF2);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbData = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnAll = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        BtnPrev = new widget.Button();
        LHalaman = new widget.Label();
        BtnNext = new widget.Button();
        FormInput = new widget.PanelBiasa();
        jLabel1 = new widget.Label();
        TKdICD9 = new widget.TextBox();
        TNmICD9 = new widget.TextBox();
        btnCariICD9 = new widget.Button();
        jLabel2 = new widget.Label();
        TKdSnomed = new widget.TextBox();
        TDisplay = new widget.TextBox();
        btnCariSnomed = new widget.Button();
        jLabel3 = new widget.Label();
        TSystem = new widget.TextBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                tampil();
                emptTeks();
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
            "::[ Data Mapping SNOMED ICD 9 ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11),
            new java.awt.Color(50, 50, 50)));
        internalFrame1.setName("internalFrame1");
        internalFrame1.setLayout(new BorderLayout(1, 1));

        Scroll.setName("Scroll");
        Scroll.setOpaque(true);

        tbData.setAutoCreateRowSorter(true);
        tbData.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbData.setName("tbData");
        tbData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (tabMode.getRowCount() != 0) {
                    try { getData(); } catch (java.lang.NullPointerException e) {}
                }
            }
        });
        tbData.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if (tabMode.getRowCount() != 0) {
                    if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                        try { getData(); } catch (java.lang.NullPointerException e) {}
                    }
                }
            }
        });
        Scroll.setViewportView(tbData);
        internalFrame1.add(Scroll, BorderLayout.CENTER);

        // === Panel Tombol ===
        jPanel3.setName("jPanel3");
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new Dimension(44, 100));
        jPanel3.setLayout(new BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8");
        panelGlass8.setPreferredSize(new Dimension(44, 44));
        panelGlass8.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setPreferredSize(new Dimension(100, 30));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { BtnSimpanActionPerformed(); }
        });
        BtnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_SPACE) BtnSimpanActionPerformed();
                else Valid.pindah(evt, TSystem, BtnBatal);
            }
        });
        panelGlass8.add(BtnSimpan);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Baru");
        BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setPreferredSize(new Dimension(100, 30));
        BtnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { emptTeks(); }
        });
        BtnBatal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_SPACE) emptTeks();
                else Valid.pindah(evt, BtnSimpan, BtnHapus);
            }
        });
        panelGlass8.add(BtnBatal);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setPreferredSize(new Dimension(100, 30));
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { BtnHapusActionPerformed(); }
        });
        BtnHapus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_SPACE) BtnHapusActionPerformed();
                else Valid.pindah(evt, BtnBatal, BtnEdit);
            }
        });
        panelGlass8.add(BtnHapus);

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnEdit.setMnemonic('G');
        BtnEdit.setText("Ganti");
        BtnEdit.setToolTipText("Alt+G");
        BtnEdit.setPreferredSize(new Dimension(100, 30));
        BtnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { BtnEditActionPerformed(); }
        });
        BtnEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_SPACE) BtnEditActionPerformed();
                else Valid.pindah(evt, BtnHapus, BtnAll);
            }
        });
        panelGlass8.add(BtnEdit);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setMnemonic('M');
        BtnAll.setText("Semua");
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setPreferredSize(new Dimension(100, 30));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { TCari.setText(""); currentPage = 0; tampil(); }
        });
        BtnAll.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_SPACE) { TCari.setText(""); currentPage = 0; tampil(); }
                else Valid.pindah(evt, BtnEdit, BtnKeluar);
            }
        });
        panelGlass8.add(BtnAll);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setPreferredSize(new Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { dispose(); }
        });
        BtnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_SPACE) dispose();
                else Valid.pindah(evt, BtnAll, TCari);
            }
        });
        panelGlass8.add(BtnKeluar);

        jPanel3.add(panelGlass8, BorderLayout.CENTER);

        // === Panel Pencarian ===
        panelGlass9.setName("panelGlass9");
        panelGlass9.setPreferredSize(new Dimension(44, 44));
        panelGlass9.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));

        jLabel6.setText("Key Word :");
        jLabel6.setPreferredSize(new Dimension(70, 23));
        panelGlass9.add(jLabel6);

        TCari.setPreferredSize(new Dimension(400, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) { currentPage = 0; tampil(); }
                else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) BtnCari.requestFocus();
                else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) BtnKeluar.requestFocus();
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setMnemonic('2');
        BtnCari.setToolTipText("Alt+2");
        BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { currentPage = 0; tampil(); }
        });
        BtnCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_SPACE) { currentPage = 0; tampil(); }
                else Valid.pindah(evt, TCari, BtnAll);
            }
        });
        panelGlass9.add(BtnCari);

        jLabel7.setText("Record :");
        jLabel7.setPreferredSize(new Dimension(60, 23));
        panelGlass9.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setPreferredSize(new Dimension(60, 23));
        panelGlass9.add(LCount);

        BtnPrev.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/2leftarrow.png")));
        BtnPrev.setToolTipText("Halaman Sebelumnya");
        BtnPrev.setName("BtnPrev");
        BtnPrev.setPreferredSize(new Dimension(28, 23));
        BtnPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (currentPage > 0) { currentPage--; tampil(); }
            }
        });
        BtnPrev.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_SPACE && currentPage > 0) { currentPage--; tampil(); }
            }
        });
        panelGlass9.add(BtnPrev);

        LHalaman.setText("Hal 1 / 1");
        LHalaman.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LHalaman.setName("LHalaman");
        LHalaman.setPreferredSize(new Dimension(80, 23));
        panelGlass9.add(LHalaman);

        BtnNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/2rightarrow.png")));
        BtnNext.setToolTipText("Halaman Berikutnya");
        BtnNext.setName("BtnNext");
        BtnNext.setPreferredSize(new Dimension(28, 23));
        BtnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
                if (currentPage < totalPages - 1) { currentPage++; tampil(); }
            }
        });
        BtnNext.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                    int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
                    if (currentPage < totalPages - 1) { currentPage++; tampil(); }
                }
            }
        });
        panelGlass9.add(BtnNext);

        jPanel3.add(panelGlass9, BorderLayout.PAGE_START);
        internalFrame1.add(jPanel3, BorderLayout.PAGE_END);

        // === Form Input ===
        FormInput.setPreferredSize(new Dimension(100, 70));
        FormInput.setLayout(null);

        // Baris 1: ICD 9
        jLabel1.setText("ICD 9 :");
        FormInput.add(jLabel1);
        jLabel1.setBounds(0, 8, 50, 23);

        TKdICD9.setEditable(false);
        TKdICD9.setHighlighter(null);
        FormInput.add(TKdICD9);
        TKdICD9.setBounds(53, 8, 80, 23);

        TNmICD9.setEditable(false);
        TNmICD9.setHighlighter(null);
        FormInput.add(TNmICD9);
        TNmICD9.setBounds(136, 8, 200, 23);

        btnCariICD9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnCariICD9.setMnemonic('1');
        btnCariICD9.setToolTipText("Alt+1 Cari ICD 9");
        FormInput.add(btnCariICD9);
        btnCariICD9.setBounds(339, 8, 28, 23);
        btnCariICD9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { bukaICD9(); }
        });
        btnCariICD9.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_SPACE) bukaICD9();
                else if (evt.getKeyCode() == KeyEvent.VK_ENTER) bukaICD9();
                else Valid.pindah(evt, TNmICD9, TKdSnomed);
            }
        });

        // SNOMED
        jLabel2.setText("SNOMED :");
        FormInput.add(jLabel2);
        jLabel2.setBounds(375, 8, 60, 23);

        TKdSnomed.setEditable(false);
        TKdSnomed.setHighlighter(null);
        FormInput.add(TKdSnomed);
        TKdSnomed.setBounds(438, 8, 90, 23);

        TDisplay.setEditable(false);
        TDisplay.setHighlighter(null);
        FormInput.add(TDisplay);
        TDisplay.setBounds(531, 8, 160, 23);

        btnCariSnomed.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnCariSnomed.setMnemonic('3');
        btnCariSnomed.setToolTipText("Alt+3 Cari SNOMED");
        FormInput.add(btnCariSnomed);
        btnCariSnomed.setBounds(694, 8, 28, 23);
        btnCariSnomed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { bukaSnomed(); }
        });
        btnCariSnomed.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_SPACE) bukaSnomed();
                else if (evt.getKeyCode() == KeyEvent.VK_ENTER) bukaSnomed();
                else Valid.pindah(evt, TDisplay, TSystem);
            }
        });

        // Baris 2: System
        jLabel3.setText("System :");
        FormInput.add(jLabel3);
        jLabel3.setBounds(0, 36, 50, 23);

        TSystem.setEditable(false);
        TSystem.setHighlighter(null);
        FormInput.add(TSystem);
        TSystem.setBounds(53, 36, 669, 23);

        internalFrame1.add(FormInput, BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, BorderLayout.CENTER);
        pack();
    }

    private void bukaICD9() {
        icd9.isCek();
        icd9.emptTeks();
        icd9.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        icd9.setLocationRelativeTo(internalFrame1);
        icd9.setVisible(true);
    }

    private void bukaSnomed() {
        snomed.isCek();
        snomed.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        snomed.setLocationRelativeTo(internalFrame1);
        snomed.setVisible(true);
    }

    private void BtnSimpanActionPerformed() {
        if (TKdICD9.getText().trim().equals("")) {
            Valid.textKosong(TKdICD9, "Kode ICD 9");
        } else if (TKdSnomed.getText().trim().equals("")) {
            Valid.textKosong(TKdSnomed, "Kode SNOMED");
        } else {
            try {
                ps = koneksi.prepareStatement("SELECT COUNT(*) AS jml FROM mapping_snomed_icd9 WHERE icd9=?");
                ps.setString(1, TKdICD9.getText().trim());
                rs = ps.executeQuery();
                if (rs.next() && rs.getInt("jml") > 0) {
                    JOptionPane.showMessageDialog(null, "Mapping untuk ICD 9 " + TKdICD9.getText() + " sudah ada!");
                    return;
                }
            } catch (Exception e) {
                System.out.println("Error cek duplikasi: " + e);
            } finally {
                try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception ex) {}
            }

            if (Sequel.menyimpantf("mapping_snomed_icd9", "?,?,?,?", "Mapping SNOMED ICD9", 4, new String[]{
                TKdSnomed.getText().trim(),
                TDisplay.getText().trim(),
                TKdICD9.getText().trim(),
                TSystem.getText().trim()
            }) == true) {
                tampil();
                emptTeks();
            }
        }
    }

    private void BtnHapusActionPerformed() {
        if (tbData.getSelectedRow() > -1) {
            String kd_icd9 = tbData.getValueAt(tbData.getSelectedRow(), 0).toString();
            if (Sequel.queryu2tf("DELETE FROM mapping_snomed_icd9 WHERE icd9=?",
                1, new String[]{kd_icd9}) == true) {
                tampil();
                emptTeks();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Silahkan pilih data yang akan dihapus!");
        }
    }

    private void BtnEditActionPerformed() {
        if (TKdICD9.getText().trim().equals("")) {
            Valid.textKosong(TKdICD9, "Kode ICD 9");
        } else if (TKdSnomed.getText().trim().equals("")) {
            Valid.textKosong(TKdSnomed, "Kode SNOMED");
        } else {
            if (tbData.getSelectedRow() > -1) {
                String kd_icd9_lama = tbData.getValueAt(tbData.getSelectedRow(), 0).toString();
                if (Sequel.mengedittf("mapping_snomed_icd9", "icd9=?",
                    "kd_snomed=?,display=?,system=?", 4, new String[]{
                        TKdSnomed.getText().trim(),
                        TDisplay.getText().trim(),
                        TSystem.getText().trim(),
                        kd_icd9_lama
                    }) == true) {
                    emptTeks();
                    tampil();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Silahkan pilih data yang akan diubah!");
            }
        }
    }

    private void tampil() {
        Valid.tabelKosong(tabMode);
        String cari = "%" + TCari.getText() + "%";
        String sql = "FROM mapping_snomed_icd9 m LEFT JOIN icd9 i ON m.icd9 = i.kode " +
                     "WHERE (m.icd9 LIKE ? OR i.deskripsi_panjang LIKE ? OR m.kd_snomed LIKE ? OR m.display LIKE ?)";
        try {
            ps = koneksi.prepareStatement("SELECT COUNT(*) " + sql);
            ps.setString(1, cari); ps.setString(2, cari); ps.setString(3, cari); ps.setString(4, cari);
            rs = ps.executeQuery();
            if (rs.next()) totalRecords = rs.getInt(1);
            rs.close(); ps.close();

            ps = koneksi.prepareStatement(
                "SELECT m.icd9, COALESCE(i.deskripsi_panjang,'') as deskripsi, m.kd_snomed, m.display, m.system " +
                sql + " ORDER BY m.icd9 LIMIT ? OFFSET ?");
            ps.setString(1, cari); ps.setString(2, cari); ps.setString(3, cari); ps.setString(4, cari);
            ps.setInt(5, PAGE_SIZE);
            ps.setInt(6, currentPage * PAGE_SIZE);
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new Object[]{
                    rs.getString("icd9"), rs.getString("deskripsi"),
                    rs.getString("kd_snomed"), rs.getString("display"), rs.getString("system")
                });
            }
        } catch (Exception e) {
            System.out.println("Error tampil ICD9: " + e);
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception ignored) {}
        }
        int totalPages = (totalRecords == 0) ? 1 : (int) Math.ceil((double) totalRecords / PAGE_SIZE);
        LCount.setText("" + totalRecords);
        LHalaman.setText("Hal " + (currentPage + 1) + " / " + totalPages);
        BtnPrev.setEnabled(currentPage > 0);
        BtnNext.setEnabled(currentPage < totalPages - 1);
    }

    public void emptTeks() {
        TKdICD9.setText("");
        TNmICD9.setText("");
        TKdSnomed.setText("");
        TDisplay.setText("");
        TSystem.setText("");
        TKdICD9.requestFocus();
    }

    private void getData() {
        int viewRow = tbData.getSelectedRow();
        if (viewRow != -1) {
            int row = tbData.convertRowIndexToModel(viewRow);
            TKdICD9.setText  (sv(tabMode.getValueAt(row, 0)));
            TNmICD9.setText  (sv(tabMode.getValueAt(row, 1)));
            TKdSnomed.setText(sv(tabMode.getValueAt(row, 2)));
            TDisplay.setText (sv(tabMode.getValueAt(row, 3)));
            TSystem.setText  (sv(tabMode.getValueAt(row, 4)));
        }
    }

    private String sv(Object v) { return v != null ? v.toString() : ""; }

    private void cariICDOtomatis() {
        String display = TDisplay.getText().trim();
        if (display.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih SNOMED terlebih dahulu (isi field Display).", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            // Cari di mapping yang sudah ada (kd_snomed sama)
            if (!TKdSnomed.getText().trim().isEmpty()) {
                ps = koneksi.prepareStatement(
                    "SELECT m.icd9, COALESCE(i.deskripsi_panjang,'') FROM mapping_snomed_icd9 m " +
                    "LEFT JOIN icd9 i ON m.icd9=i.kode WHERE m.kd_snomed=? LIMIT 1");
                ps.setString(1, TKdSnomed.getText().trim());
                rs = ps.executeQuery();
                if (rs.next()) {
                    String kode = rs.getString(1);
                    String desc = rs.getString(2);
                    rs.close(); ps.close();
                    TKdICD9.setText(kode);
                    TNmICD9.setText(desc);
                    JOptionPane.showMessageDialog(this, "ICD 9 ditemukan dari mapping yang ada: " + kode, "Otomatis", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                rs.close(); ps.close();
            }
            // Cari di tabel RF2 berdasarkan SNOMED code (offline map dari SNOMED International)
            if (!TKdSnomed.getText().trim().isEmpty()) {
                ps = koneksi.prepareStatement(
                    "SELECT r.icd_code, COALESCE(i.deskripsi_panjang,'') FROM snomedct_icd9_rf2 r " +
                    "LEFT JOIN icd9 i ON r.icd_code = i.kode " +
                    "WHERE r.snomed_code = ? AND r.active = 1 AND r.icd_code IS NOT NULL AND r.icd_code != '' " +
                    "ORDER BY r.map_group, r.map_priority LIMIT 10");
                ps.setString(1, TKdSnomed.getText().trim());
                rs = ps.executeQuery();
                List<String> rf2Opts  = new ArrayList<>();
                List<String> rf2Codes = new ArrayList<>();
                List<String> rf2Descs = new ArrayList<>();
                while (rs.next()) {
                    String code = rs.getString(1);
                    String desc = rs.getString(2);
                    rf2Opts.add(code + (desc.isEmpty() ? "" : " - " + desc));
                    rf2Codes.add(code);
                    rf2Descs.add(desc);
                }
                rs.close(); ps.close();
                if (!rf2Opts.isEmpty()) {
                    if (rf2Codes.size() == 1) {
                        TKdICD9.setText(rf2Codes.get(0));
                        TNmICD9.setText(rf2Descs.get(0));
                        JOptionPane.showMessageDialog(this,
                            "ICD 9 ditemukan dari data RF2 SNOMED: " + rf2Codes.get(0),
                            "RF2 Map", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    String pilihan = (String) JOptionPane.showInputDialog(this,
                        "Pilih ICD 9 dari data RF2 SNOMED CT:", "Saran ICD 9 (RF2)",
                        JOptionPane.QUESTION_MESSAGE, null, rf2Opts.toArray(), rf2Opts.get(0));
                    if (pilihan != null) {
                        int idx = rf2Opts.indexOf(pilihan);
                        TKdICD9.setText(rf2Codes.get(idx));
                        TNmICD9.setText(rf2Descs.get(idx));
                    }
                    return;
                }
            }

            // Cari di tabel icd9 berdasarkan display SNOMED
            ps = koneksi.prepareStatement(
                "SELECT kode, deskripsi_panjang FROM icd9 WHERE deskripsi_panjang LIKE ? ORDER BY deskripsi_panjang LIMIT 10");
            ps.setString(1, "%" + display + "%");
            rs = ps.executeQuery();
            List<String> opts  = new ArrayList<>();
            List<String> kodes = new ArrayList<>();
            List<String> descs = new ArrayList<>();
            while (rs.next()) {
                opts.add(rs.getString("kode") + " - " + rs.getString("deskripsi_panjang"));
                kodes.add(rs.getString("kode"));
                descs.add(rs.getString("deskripsi_panjang"));
            }
            rs.close(); ps.close();
            if (opts.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tidak ditemukan saran ICD 9 untuk: " + display, "Tidak Ada Hasil", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String pilihan = (String) JOptionPane.showInputDialog(this,
                "Pilih ICD 9 yang sesuai:", "Saran ICD 9 Otomatis",
                JOptionPane.QUESTION_MESSAGE, null,
                opts.toArray(), opts.get(0));
            if (pilihan != null) {
                int idx = opts.indexOf(pilihan);
                TKdICD9.setText(kodes.get(idx));
                TNmICD9.setText(descs.get(idx));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bulkImportCSV() {
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));
        if (fc.showOpenDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION) return;
        int inserted = 0, skipped = 0;
        try (java.io.BufferedReader br = new java.io.BufferedReader(
                new java.io.InputStreamReader(new java.io.FileInputStream(fc.getSelectedFile()),
                    java.nio.charset.StandardCharsets.UTF_8))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                if (line.startsWith("\uFEFF")) line = line.substring(1);
                String[] col = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (col.length < 3) { skipped++; continue; }
                String kdSnomed = col[0].trim().replaceAll("^\"|\"$", "");
                String disp     = col[1].trim().replaceAll("^\"|\"$", "");
                String icd9     = col[2].trim().replaceAll("^\"|\"$", "");
                String system   = col.length > 3 ? col[3].trim().replaceAll("^\"|\"$", "") : "http://snomed.info/sct";
                if (kdSnomed.isEmpty() || icd9.isEmpty()) { skipped++; continue; }
                try {
                    ps = koneksi.prepareStatement(
                        "INSERT IGNORE INTO mapping_snomed_icd9 (kd_snomed, display, icd9, system) VALUES (?,?,?,?)");
                    ps.setString(1, kdSnomed);
                    ps.setString(2, disp);
                    ps.setString(3, icd9);
                    ps.setString(4, system);
                    int r = ps.executeUpdate(); ps.close();
                    if (r > 0) inserted++; else skipped++;
                } catch (Exception ex) { skipped++; }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal baca CSV: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
            "Import selesai!\nBerhasil: " + inserted + " baris\nDilewati/duplikat: " + skipped + " baris",
            "Hasil Import", JOptionPane.INFORMATION_MESSAGE);
        tampil();
    }

    private void importRF2File(widget.Button btnSrc) {
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        fc.setDialogTitle("Pilih file RF2 Extended Map (der2_iisssccRefset_ExtendedMap*.txt)");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "RF2 Map Files (*.txt)", "txt"));
        if (fc.showOpenDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION) return;

        java.io.File file = fc.getSelectedFile();
        int konfirm = JOptionPane.showConfirmDialog(this,
            "Import RF2 dari:\n" + file.getName() + "\n\n"
            + "Hanya record aktif dengan status 'Properly Classified' yang diimpor.\n"
            + "File besar (>500MB) mungkin memerlukan beberapa menit.\n\nLanjutkan?",
            "Konfirmasi Import RF2", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (konfirm != JOptionPane.YES_OPTION) return;

        btnSrc.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        final Connection conn = koneksi;
        final ApiSNOMED api = new ApiSNOMED();

        new javax.swing.SwingWorker<ApiSNOMED.SyncResult, Void>() {
            @Override
            protected ApiSNOMED.SyncResult doInBackground() {
                return api.importRF2MapFile(file, conn, "snomedct_icd9_rf2");
            }
            @Override
            protected void done() {
                try {
                    ApiSNOMED.SyncResult res = get();
                    JOptionPane.showMessageDialog(DlgMappingSnomedICD9.this,
                        res.getMessage(),
                        res.isSuccess() ? "Import RF2 Selesai" : "Import RF2 Gagal",
                        res.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DlgMappingSnomedICD9.this,
                        "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnSrc.setEnabled(true);
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }.execute();
    }

    private void syncDariSnowstorm(widget.Button btnSrc) {
        String kw = TDisplay.getText().trim().isEmpty() ? TCari.getText().trim() : TDisplay.getText().trim();
        if (kw.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Isi keyword (TCari atau pilih SNOMED) sebelum sync.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        ApiSNOMED api = new ApiSNOMED(this);
        if (!api.isConfigured()) {
            JOptionPane.showMessageDialog(this, "URL Snowstorm belum dikonfigurasi di database.xml (URLAPISNOWSTORM).");
            return;
        }
        btnSrc.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        final Connection conn = koneksi;
        new javax.swing.SwingWorker<ApiSNOMED.SyncResult, Void>() {
            @Override protected ApiSNOMED.SyncResult doInBackground() { return api.syncToLocalDB(kw, 50, conn); }
            @Override protected void done() {
                try {
                    ApiSNOMED.SyncResult res = get();
                    JOptionPane.showMessageDialog(DlgMappingSnomedICD9.this,
                        res.isSuccess() ? "Sync OK: " + res.getMessage() : "Gagal: " + res.getMessage());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DlgMappingSnomedICD9.this, "Error: " + ex.getMessage());
                } finally {
                    btnSrc.setEnabled(true);
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }.execute();
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.geticd9());
        BtnHapus.setEnabled(akses.geticd9());
        BtnEdit.setEnabled(akses.geticd9());
    }

    public JTable getTable() {
        return tbData;
    }

    // Variables declaration
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnNext;
    private widget.Button BtnPrev;
    private widget.Button BtnSimpan;
    private widget.Label LHalaman;
    private widget.PanelBiasa FormInput;
    private widget.Label LCount;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TDisplay;
    private widget.TextBox TKdICD9;
    private widget.TextBox TKdSnomed;
    private widget.TextBox TNmICD9;
    private widget.TextBox TSystem;
    private widget.Button btnCariICD9;
    private widget.Button btnCariSnomed;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel1;
    private widget.Label jLabel2;
    private widget.Label jLabel3;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private javax.swing.JPanel jPanel3;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbData;
    // End of variables declaration
}
