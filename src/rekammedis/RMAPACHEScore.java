/*
 * APACHE II Score Calculator
 * Acute Physiology and Chronic Health Evaluation II
 * 
 * Referensi: Knaus WA, Draper EA, Wagner DP, Zimmerman JE. 
 * APACHE II: a severity of disease classification system. 
 * Crit Care Med. 1985;13(10):818-29.
 */
package rekammedis;

import fungsi.WarnaTable;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import fungsi.validasi;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.batasInput;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import kepegawaian.DlgCariPetugas;

/**
 * Form APACHE II Score Calculator - NetBeans Version
 *
 * @author SIMRS Khanza
 */
public class RMAPACHEScore extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private int i = 0;
    private PreparedStatement ps;
    private ResultSet rs;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);

    /**
     * Creates new form RMAPACHEScore
     */
    public RMAPACHEScore(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.R.M.", "Nama Pasien", "Umur", "JK", "Tanggal",
            "APS", "Age Points", "Chronic Health", "Total APACHE II",
            "Mortalitas (%)", "Kategori Risiko", "NIP", "Petugas", "Tgl.Lahir"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        initComponents();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("APACHE II Score Calculator (Acute Physiology and Chronic Health Evaluation II)");
        setResizable(false);

        tbObat.setModel(tabMode);
        setupTable();
        jam();

        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));

        // Set input limitations for numeric fields
        txtSuhuTubuh.setDocument(new batasInput((byte) 5).getOnlyAngka(txtSuhuTubuh));
        txtMAP.setDocument(new batasInput((byte) 3).getOnlyAngka(txtMAP));
        txtNadi.setDocument(new batasInput((byte) 3).getOnlyAngka(txtNadi));
        txtRespirasi.setDocument(new batasInput((byte) 2).getOnlyAngka(txtRespirasi));
        txtFiO2.setDocument(new batasInput((byte) 3).getOnlyAngka(txtFiO2));
        txtPaO2.setDocument(new batasInput((byte) 5).getOnlyAngka(txtPaO2));
        txtpH.setDocument(new batasInput((byte) 4).getOnlyAngka(txtpH));
        txtNatrium.setDocument(new batasInput((byte) 3).getOnlyAngka(txtNatrium));
        txtKalium.setDocument(new batasInput((byte) 4).getOnlyAngka(txtKalium));
        txtKreatinin.setDocument(new batasInput((byte) 4).getOnlyAngka(txtKreatinin));
        txtHematokrit.setDocument(new batasInput((byte) 2).getOnlyAngka(txtHematokrit));
        txtLeukosit.setDocument(new batasInput((byte) 5).getOnlyAngka(txtLeukosit));
        txtGCS.setDocument(new batasInput((byte) 2).getOnlyAngka(txtGCS));

        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }

                @Override
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }

                @Override
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }
            });
        }

        // Window listener for petugas dialog
        petugas.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (petugas.getTable().getSelectedRow() != -1) {
                    KdPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                    NmPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                }
                KdPetugas.requestFocus();
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

        ChkInput.setSelected(false);
        isForm();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnCetakAPACHE = new javax.swing.JMenuItem();
        JK = new widget.TextBox();
        Umur = new widget.TextBox();
        TanggalRegistrasi = new widget.TextBox();
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
        jLabel21 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel29 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        PanelInput = new javax.swing.JPanel();
        FormInput = new widget.PanelBiasa();
        jLabel4 = new widget.Label();
        TNoRw = new widget.TextBox();
        TNoRM = new widget.TextBox();
        TPasien = new widget.TextBox();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        jLabel16 = new widget.Label();
        Tanggal = new widget.Tanggal();
        Jam = new widget.ComboBox();
        Menit = new widget.ComboBox();
        Detik = new widget.ComboBox();
        ChkKejadian = new widget.CekBox();
        jLabel18 = new widget.Label();
        KdPetugas = new widget.TextBox();
        NmPetugas = new widget.TextBox();
        btnPetugas = new widget.Button();
        jLabel19 = new widget.Label();
        jLabel20 = new widget.Label();

        // Physiological Variables
        jLabel22 = new widget.Label();
        txtSuhuTubuh = new widget.TextBox();
        jLabel23 = new widget.Label();
        SkorSuhu = new widget.TextBox();

        jLabel24 = new widget.Label();
        txtMAP = new widget.TextBox();
        jLabel25 = new widget.Label();
        SkorMAP = new widget.TextBox();

        jLabel26 = new widget.Label();
        txtNadi = new widget.TextBox();
        jLabel27 = new widget.Label();
        SkorNadi = new widget.TextBox();

        jLabel28 = new widget.Label();
        txtRespirasi = new widget.TextBox();
        jLabel30 = new widget.Label();
        SkorRespirasi = new widget.TextBox();

        jLabel31 = new widget.Label();
        txtFiO2 = new widget.TextBox();
        jLabel32 = new widget.Label();
        txtPaO2 = new widget.TextBox();
        jLabel33 = new widget.Label();
        SkorOksigenasi = new widget.TextBox();

        jLabel34 = new widget.Label();
        txtpH = new widget.TextBox();
        jLabel35 = new widget.Label();
        SkorpH = new widget.TextBox();

        jLabel36 = new widget.Label();
        txtNatrium = new widget.TextBox();
        jLabel37 = new widget.Label();
        SkorNatrium = new widget.TextBox();

        jLabel38 = new widget.Label();
        txtKalium = new widget.TextBox();
        jLabel39 = new widget.Label();
        SkorKalium = new widget.TextBox();

        jLabel40 = new widget.Label();
        txtKreatinin = new widget.TextBox();
        jLabel41 = new widget.Label();
        SkorKreatinin = new widget.TextBox();

        jLabel42 = new widget.Label();
        txtHematokrit = new widget.TextBox();
        jLabel43 = new widget.Label();
        SkorHematokrit = new widget.TextBox();

        jLabel44 = new widget.Label();
        txtLeukosit = new widget.TextBox();
        jLabel45 = new widget.Label();
        SkorLeukosit = new widget.TextBox();

        jLabel46 = new widget.Label();
        txtGCS = new widget.TextBox();
        jLabel47 = new widget.Label();
        SkorGCS = new widget.TextBox();

        // Age and Chronic Health
        jLabel48 = new widget.Label();
        SkorUmur = new widget.TextBox();

        jLabel49 = new widget.Label();
        cmbChronicHealth = new widget.ComboBox();
        jLabel50 = new widget.Label();
        SkorChronicHealth = new widget.TextBox();

        // Results
        jLabel51 = new widget.Label();
        TotalAPS = new widget.TextBox();

        jLabel52 = new widget.Label();
        TotalAPACHE = new widget.TextBox();

        jLabel53 = new widget.Label();
        EstimasiMortalitas = new widget.TextBox();

        jLabel54 = new widget.Label();
        KategoriRisiko = new widget.TextBox();

        ChkInput = new widget.CekBox();

        jPopupMenu1.setName("jPopupMenu1");

        MnCetakAPACHE = new javax.swing.JMenuItem();
        MnCetakAPACHE.setBackground(new java.awt.Color(255, 255, 254));
        MnCetakAPACHE.setFont(new java.awt.Font("Tahoma", 0, 11));
        MnCetakAPACHE.setForeground(new java.awt.Color(50, 50, 50));
        MnCetakAPACHE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        MnCetakAPACHE.setText("Cetak APACHE II Score");
        MnCetakAPACHE.setName("MnCetakAPACHE");
        MnCetakAPACHE.setPreferredSize(new java.awt.Dimension(200, 26));
        MnCetakAPACHE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnCetakAPACHEActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnCetakAPACHE);

        JK.setHighlighter(null);
        JK.setName("JK");

        Umur.setHighlighter(null);
        Umur.setName("Umur");

        TanggalRegistrasi.setHighlighter(null);
        TanggalRegistrasi.setName("TanggalRegistrasi");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
                "::[ APACHE II Score Calculator - Acute Physiology and Chronic Health Evaluation II ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 11),
                new java.awt.Color(50, 50, 50)));
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12));
        internalFrame1.setName("internalFrame1");
        internalFrame1.setPreferredSize(new java.awt.Dimension(559, 334));
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll");
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(300, 200));

        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat.setComponentPopupMenu(jPopupMenu1);
        tbObat.setName("tbObat");
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

        jPanel3.setName("jPanel3");
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8");
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
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

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7");
        jLabel7.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass8.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount");
        LCount.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass8.add(LCount);

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

        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9");
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel21.setText("Tanggal :");
        jLabel21.setName("jLabel21");
        jLabel21.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel21);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1");
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari1);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("s.d.");
        jLabel29.setName("jLabel29");
        jLabel29.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel29);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2");
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6");
        jLabel6.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari");
        TCari.setPreferredSize(new java.awt.Dimension(315, 23));
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
        BtnAll.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnAllKeyPressed(evt);
            }
        });
        panelGlass9.add(BtnAll);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput");
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 600));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setBackground(new java.awt.Color(250, 255, 245));
        FormInput.setName("FormInput");
        FormInput.setPreferredSize(new java.awt.Dimension(870, 600));
        FormInput.setLayout(null);

        // Basic Patient Info
        jLabel4.setText("No.Rawat :");
        jLabel4.setName("jLabel4");
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 10, 75, 23);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw");
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(79, 10, 141, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM");
        FormInput.add(TNoRM);
        TNoRM.setBounds(222, 10, 112, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien");
        FormInput.add(TPasien);
        TPasien.setBounds(336, 10, 285, 23);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8");
        FormInput.add(jLabel8);
        jLabel8.setBounds(625, 10, 60, 23);

        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir");
        FormInput.add(TglLahir);
        TglLahir.setBounds(689, 10, 100, 23);

        // Date and Time
        jLabel16.setText("Tanggal :");
        jLabel16.setName("jLabel16");
        FormInput.add(jLabel16);
        jLabel16.setBounds(0, 40, 75, 23);

        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setDisplayFormat("dd-MM-yyyy");
        Tanggal.setName("Tanggal");
        Tanggal.setOpaque(false);
        Tanggal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalKeyPressed(evt);
            }
        });
        FormInput.add(Tanggal);
        Tanggal.setBounds(79, 40, 90, 23);

        Jam.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"}));
        Jam.setName("Jam");
        Jam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JamKeyPressed(evt);
            }
        });
        FormInput.add(Jam);
        Jam.setBounds(173, 40, 62, 23);

        Menit.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"}));
        Menit.setName("Menit");
        Menit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MenitKeyPressed(evt);
            }
        });
        FormInput.add(Menit);
        Menit.setBounds(238, 40, 62, 23);

        Detik.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"}));
        Detik.setName("Detik");
        Detik.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DetikKeyPressed(evt);
            }
        });
        FormInput.add(Detik);
        Detik.setBounds(303, 40, 62, 23);

        ChkKejadian.setBorder(null);
        ChkKejadian.setSelected(true);
        ChkKejadian.setFont(new java.awt.Font("Tahoma", 1, 11));
        ChkKejadian.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setName("ChkKejadian");
        FormInput.add(ChkKejadian);
        ChkKejadian.setBounds(368, 40, 23, 23);

        // Petugas
        jLabel18.setText("Petugas :");
        jLabel18.setName("jLabel18");
        FormInput.add(jLabel18);
        jLabel18.setBounds(400, 40, 70, 23);

        KdPetugas.setEditable(false);
        KdPetugas.setHighlighter(null);
        KdPetugas.setName("KdPetugas");
        KdPetugas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdPetugasKeyPressed(evt);
            }
        });
        FormInput.add(KdPetugas);
        KdPetugas.setBounds(474, 40, 94, 23);

        NmPetugas.setEditable(false);
        NmPetugas.setName("NmPetugas");
        FormInput.add(NmPetugas);
        NmPetugas.setBounds(571, 40, 187, 23);

        btnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnPetugas.setMnemonic('2');
        btnPetugas.setText("...");
        btnPetugas.setToolTipText("Alt+2");
        btnPetugas.setName("btnPetugas");
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

        // APACHE II Header
        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel19.setText("APACHE II Score Calculator - Acute Physiology and Chronic Health Evaluation :");
        jLabel19.setName("jLabel19");
        FormInput.add(jLabel19);
        jLabel19.setBounds(0, 80, 500, 23);

        jLabel20.setFont(new java.awt.Font("Tahoma", 2, 11));
        jLabel20.setText("Gunakan nilai terburuk dalam 24 jam pertama masuk ICU");
        jLabel20.setName("jLabel20");
        FormInput.add(jLabel20);
        jLabel20.setBounds(0, 100, 350, 23);

        // PHYSIOLOGICAL VARIABLES
        // 1. Suhu Tubuh
        jLabel22.setText("Suhu Tubuh (°C) :");
        jLabel22.setName("jLabel22");
        FormInput.add(jLabel22);
        jLabel22.setBounds(0, 130, 100, 23);

        txtSuhuTubuh.setName("txtSuhuTubuh");
        txtSuhuTubuh.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSuhuTubuhKeyReleased(evt);
            }
        });
        FormInput.add(txtSuhuTubuh);
        txtSuhuTubuh.setBounds(104, 130, 80, 23);

        jLabel23.setText("Skor :");
        jLabel23.setName("jLabel23");
        FormInput.add(jLabel23);
        jLabel23.setBounds(190, 130, 40, 23);

        SkorSuhu.setEditable(false);
        SkorSuhu.setText("0");
        SkorSuhu.setName("SkorSuhu");
        FormInput.add(SkorSuhu);
        SkorSuhu.setBounds(234, 130, 40, 23);

        // 2. Mean Arterial Pressure
        jLabel24.setText("MAP (mmHg) :");
        jLabel24.setName("jLabel24");
        FormInput.add(jLabel24);
        jLabel24.setBounds(290, 130, 80, 23);

        txtMAP.setName("txtMAP");
        txtMAP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMAPKeyReleased(evt);
            }
        });
        FormInput.add(txtMAP);
        txtMAP.setBounds(374, 130, 80, 23);

        jLabel25.setText("Skor :");
        jLabel25.setName("jLabel25");
        FormInput.add(jLabel25);
        jLabel25.setBounds(460, 130, 40, 23);

        SkorMAP.setEditable(false);
        SkorMAP.setText("0");
        SkorMAP.setName("SkorMAP");
        FormInput.add(SkorMAP);
        SkorMAP.setBounds(504, 130, 40, 23);

        // 3. Heart Rate
        jLabel26.setText("Nadi (/mnt) :");
        jLabel26.setName("jLabel26");
        FormInput.add(jLabel26);
        jLabel26.setBounds(560, 130, 70, 23);

        txtNadi.setName("txtNadi");
        txtNadi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNadiKeyReleased(evt);
            }
        });
        FormInput.add(txtNadi);
        txtNadi.setBounds(634, 130, 70, 23);

        jLabel27.setText("Skor :");
        jLabel27.setName("jLabel27");
        FormInput.add(jLabel27);
        jLabel27.setBounds(710, 130, 40, 23);

        SkorNadi.setEditable(false);
        SkorNadi.setText("0");
        SkorNadi.setName("SkorNadi");
        FormInput.add(SkorNadi);
        SkorNadi.setBounds(754, 130, 40, 23);

        // 4. Respiratory Rate
        jLabel28.setText("Respirasi (/mnt) :");
        jLabel28.setName("jLabel28");
        FormInput.add(jLabel28);
        jLabel28.setBounds(0, 160, 100, 23);

        txtRespirasi.setName("txtRespirasi");
        txtRespirasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtRespirasiKeyReleased(evt);
            }
        });
        FormInput.add(txtRespirasi);
        txtRespirasi.setBounds(104, 160, 80, 23);

        jLabel30.setText("Skor :");
        jLabel30.setName("jLabel30");
        FormInput.add(jLabel30);
        jLabel30.setBounds(190, 160, 40, 23);

        SkorRespirasi.setEditable(false);
        SkorRespirasi.setText("0");
        SkorRespirasi.setName("SkorRespirasi");
        FormInput.add(SkorRespirasi);
        SkorRespirasi.setBounds(234, 160, 40, 23);

        // 5. Oxygenation
        jLabel31.setText("FiO2 (%) :");
        jLabel31.setName("jLabel31");
        FormInput.add(jLabel31);
        jLabel31.setBounds(290, 160, 60, 23);

        txtFiO2.setName("txtFiO2");
        txtFiO2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFiO2KeyReleased(evt);
            }
        });
        FormInput.add(txtFiO2);
        txtFiO2.setBounds(354, 160, 60, 23);

        jLabel32.setText("PaO2 :");
        jLabel32.setName("jLabel32");
        FormInput.add(jLabel32);
        jLabel32.setBounds(420, 160, 40, 23);

        txtPaO2.setName("txtPaO2");
        txtPaO2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPaO2KeyReleased(evt);
            }
        });
        FormInput.add(txtPaO2);
        txtPaO2.setBounds(464, 160, 80, 23);

        jLabel33.setText("Skor :");
        jLabel33.setName("jLabel33");
        FormInput.add(jLabel33);
        jLabel33.setBounds(550, 160, 40, 23);

        SkorOksigenasi.setEditable(false);
        SkorOksigenasi.setText("0");
        SkorOksigenasi.setName("SkorOksigenasi");
        FormInput.add(SkorOksigenasi);
        SkorOksigenasi.setBounds(594, 160, 40, 23);

        // 6. Arterial pH
        jLabel34.setText("pH Arteri :");
        jLabel34.setName("jLabel34");
        FormInput.add(jLabel34);
        jLabel34.setBounds(0, 190, 70, 23);

        txtpH.setName("txtpH");
        txtpH.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtpHKeyReleased(evt);
            }
        });
        FormInput.add(txtpH);
        txtpH.setBounds(74, 190, 80, 23);

        jLabel35.setText("Skor :");
        jLabel35.setName("jLabel35");
        FormInput.add(jLabel35);
        jLabel35.setBounds(160, 190, 40, 23);

        SkorpH.setEditable(false);
        SkorpH.setText("0");
        SkorpH.setName("SkorpH");
        FormInput.add(SkorpH);
        SkorpH.setBounds(204, 190, 40, 23);

        // 7. Serum Sodium
        jLabel36.setText("Natrium (mEq/L) :");
        jLabel36.setName("jLabel36");
        FormInput.add(jLabel36);
        jLabel36.setBounds(260, 190, 100, 23);

        txtNatrium.setName("txtNatrium");
        txtNatrium.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNatriumKeyReleased(evt);
            }
        });
        FormInput.add(txtNatrium);
        txtNatrium.setBounds(364, 190, 80, 23);

        jLabel37.setText("Skor :");
        jLabel37.setName("jLabel37");
        FormInput.add(jLabel37);
        jLabel37.setBounds(450, 190, 40, 23);

        SkorNatrium.setEditable(false);
        SkorNatrium.setText("0");
        SkorNatrium.setName("SkorNatrium");
        FormInput.add(SkorNatrium);
        SkorNatrium.setBounds(494, 190, 40, 23);

        // 8. Serum Potassium
        jLabel38.setText("Kalium (mEq/L) :");
        jLabel38.setName("jLabel38");
        FormInput.add(jLabel38);
        jLabel38.setBounds(550, 190, 100, 23);

        txtKalium.setName("txtKalium");
        txtKalium.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKaliumKeyReleased(evt);
            }
        });
        FormInput.add(txtKalium);
        txtKalium.setBounds(654, 190, 80, 23);

        jLabel39.setText("Skor :");
        jLabel39.setName("jLabel39");
        FormInput.add(jLabel39);
        jLabel39.setBounds(740, 190, 40, 23);

        SkorKalium.setEditable(false);
        SkorKalium.setText("0");
        SkorKalium.setName("SkorKalium");
        FormInput.add(SkorKalium);
        SkorKalium.setBounds(784, 190, 40, 23);

        // 9. Serum Creatinine
        jLabel40.setText("Kreatinin (mg/dL) :");
        jLabel40.setName("jLabel40");
        FormInput.add(jLabel40);
        jLabel40.setBounds(0, 220, 110, 23);

        txtKreatinin.setName("txtKreatinin");
        txtKreatinin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKreatininKeyReleased(evt);
            }
        });
        FormInput.add(txtKreatinin);
        txtKreatinin.setBounds(114, 220, 80, 23);

        jLabel41.setText("Skor :");
        jLabel41.setName("jLabel41");
        FormInput.add(jLabel41);
        jLabel41.setBounds(200, 220, 40, 23);

        SkorKreatinin.setEditable(false);
        SkorKreatinin.setText("0");
        SkorKreatinin.setName("SkorKreatinin");
        FormInput.add(SkorKreatinin);
        SkorKreatinin.setBounds(244, 220, 40, 23);

        // 10. Hematocrit
        jLabel42.setText("Hematokrit (%) :");
        jLabel42.setName("jLabel42");
        FormInput.add(jLabel42);
        jLabel42.setBounds(300, 220, 100, 23);

        txtHematokrit.setName("txtHematokrit");
        txtHematokrit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtHematokritKeyReleased(evt);
            }
        });
        FormInput.add(txtHematokrit);
        txtHematokrit.setBounds(404, 220, 80, 23);

        jLabel43.setText("Skor :");
        jLabel43.setName("jLabel43");
        FormInput.add(jLabel43);
        jLabel43.setBounds(490, 220, 40, 23);

        SkorHematokrit.setEditable(false);
        SkorHematokrit.setText("0");
        SkorHematokrit.setName("SkorHematokrit");
        FormInput.add(SkorHematokrit);
        SkorHematokrit.setBounds(534, 220, 40, 23);

        // 11. White Blood Cell Count
        jLabel44.setText("Leukosit (/mm³) :");
        jLabel44.setName("jLabel44");
        FormInput.add(jLabel44);
        jLabel44.setBounds(590, 220, 100, 23);

        txtLeukosit.setName("txtLeukosit");
        txtLeukosit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtLeukositKeyReleased(evt);
            }
        });
        FormInput.add(txtLeukosit);
        txtLeukosit.setBounds(694, 220, 100, 23);

        jLabel45.setText("Skor :");
        jLabel45.setName("jLabel45");
        FormInput.add(jLabel45);
        jLabel45.setBounds(800, 220, 40, 23);

        SkorLeukosit.setEditable(false);
        SkorLeukosit.setText("0");
        SkorLeukosit.setName("SkorLeukosit");
        FormInput.add(SkorLeukosit);
        SkorLeukosit.setBounds(844, 220, 40, 23);

        // 12. Glasgow Coma Scale
        jLabel46.setText("GCS :");
        jLabel46.setName("jLabel46");
        FormInput.add(jLabel46);
        jLabel46.setBounds(0, 250, 40, 23);

        txtGCS.setName("txtGCS");
        txtGCS.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtGCSKeyReleased(evt);
            }
        });
        FormInput.add(txtGCS);
        txtGCS.setBounds(44, 250, 60, 23);

        jLabel47.setText("Skor :");
        jLabel47.setName("jLabel47");
        FormInput.add(jLabel47);
        jLabel47.setBounds(110, 250, 40, 23);

        SkorGCS.setEditable(false);
        SkorGCS.setText("0");
        SkorGCS.setName("SkorGCS");
        FormInput.add(SkorGCS);
        SkorGCS.setBounds(154, 250, 40, 23);

        // Age Points
        jLabel48.setText("Skor Umur :");
        jLabel48.setName("jLabel48");
        FormInput.add(jLabel48);
        jLabel48.setBounds(210, 250, 80, 23);

        SkorUmur.setEditable(false);
        SkorUmur.setText("0");
        SkorUmur.setName("SkorUmur");
        FormInput.add(SkorUmur);
        SkorUmur.setBounds(294, 250, 60, 23);

        // Chronic Health Points
        jLabel49.setText("Chronic Health :");
        jLabel49.setName("jLabel49");
        FormInput.add(jLabel49);
        jLabel49.setBounds(370, 250, 100, 23);

        cmbChronicHealth.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "Tidak ada",
            "Non-operatif atau post-operatif emergency",
            "Post-operatif elektif"
        }));
        cmbChronicHealth.setName("cmbChronicHealth");
        cmbChronicHealth.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbChronicHealthItemStateChanged(evt);
            }
        });
        FormInput.add(cmbChronicHealth);
        cmbChronicHealth.setBounds(474, 250, 200, 23);

        jLabel50.setText("Skor :");
        jLabel50.setName("jLabel50");
        FormInput.add(jLabel50);
        jLabel50.setBounds(680, 250, 40, 23);

        SkorChronicHealth.setEditable(false);
        SkorChronicHealth.setText("0");
        SkorChronicHealth.setName("SkorChronicHealth");
        FormInput.add(SkorChronicHealth);
        SkorChronicHealth.setBounds(724, 250, 40, 23);

        // RESULTS
        jLabel51.setText("Total APS :");
        jLabel51.setName("jLabel51");
        FormInput.add(jLabel51);
        jLabel51.setBounds(0, 290, 80, 23);

        TotalAPS.setEditable(false);
        TotalAPS.setText("0");
        TotalAPS.setForeground(new java.awt.Color(0, 0, 153));
        TotalAPS.setFont(new java.awt.Font("Tahoma", 1, 11));
        TotalAPS.setName("TotalAPS");
        FormInput.add(TotalAPS);
        TotalAPS.setBounds(84, 290, 60, 23);

        jLabel52.setText("Total APACHE II :");
        jLabel52.setName("jLabel52");
        FormInput.add(jLabel52);
        jLabel52.setBounds(160, 290, 100, 23);

        TotalAPACHE.setEditable(false);
        TotalAPACHE.setText("0");
        TotalAPACHE.setForeground(new java.awt.Color(153, 0, 0));
        TotalAPACHE.setFont(new java.awt.Font("Tahoma", 1, 12));
        TotalAPACHE.setName("TotalAPACHE");
        FormInput.add(TotalAPACHE);
        TotalAPACHE.setBounds(264, 290, 60, 23);

        jLabel53.setText("Mortalitas (%) :");
        jLabel53.setName("jLabel53");
        FormInput.add(jLabel53);
        jLabel53.setBounds(340, 290, 90, 23);

        EstimasiMortalitas.setEditable(false);
        EstimasiMortalitas.setForeground(new java.awt.Color(153, 0, 0));
        EstimasiMortalitas.setFont(new java.awt.Font("Tahoma", 1, 11));
        EstimasiMortalitas.setName("EstimasiMortalitas");
        FormInput.add(EstimasiMortalitas);
        EstimasiMortalitas.setBounds(434, 290, 80, 23);

        jLabel54.setText("Kategori Risiko :");
        jLabel54.setName("jLabel54");
        FormInput.add(jLabel54);
        jLabel54.setBounds(530, 290, 100, 23);

        KategoriRisiko.setEditable(false);
        KategoriRisiko.setForeground(new java.awt.Color(153, 0, 0));
        KategoriRisiko.setFont(new java.awt.Font("Tahoma", 1, 11));
        KategoriRisiko.setName("KategoriRisiko");
        FormInput.add(KategoriRisiko);
        KategoriRisiko.setBounds(634, 290, 120, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setMnemonic('I');
        ChkInput.setText(".: Input Data");
        ChkInput.setToolTipText("Alt+I");
        ChkInput.setBorderPainted(true);
        ChkInput.setBorderPaintedFlat(true);
        ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput.setName("ChkInput");
        ChkInput.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputActionPerformed(evt);
            }
        });
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>

    // Event Handlers
    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No. Rawat dan Nama Pasien");
            return;
        }
        if (KdPetugas.getText().trim().equals("") || NmPetugas.getText().trim().equals("")) {
            Valid.textKosong(KdPetugas, "Petugas");
            return;
        }

        calculateAPACHE();

        // Gunakan format yang sesuai dengan method menyimpantf
        if (Sequel.menyimpantf("apache_score", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 34, new String[]{
            TNoRw.getText(),
            Valid.SetTgl(Tanggal.getSelectedItem() + "") + " " + Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
            txtSuhuTubuh.getText().trim().isEmpty() ? "0" : txtSuhuTubuh.getText(),
            txtMAP.getText().trim().isEmpty() ? "0" : txtMAP.getText(),
            txtNadi.getText().trim().isEmpty() ? "0" : txtNadi.getText(),
            txtRespirasi.getText().trim().isEmpty() ? "0" : txtRespirasi.getText(),
            txtFiO2.getText().trim().isEmpty() ? "0" : txtFiO2.getText(),
            txtPaO2.getText().trim().isEmpty() ? "0" : txtPaO2.getText(),
            txtpH.getText().trim().isEmpty() ? "0" : txtpH.getText(),
            txtNatrium.getText().trim().isEmpty() ? "0" : txtNatrium.getText(),
            txtKalium.getText().trim().isEmpty() ? "0" : txtKalium.getText(),
            txtKreatinin.getText().trim().isEmpty() ? "0" : txtKreatinin.getText(),
            txtHematokrit.getText().trim().isEmpty() ? "0" : txtHematokrit.getText(),
            txtLeukosit.getText().trim().isEmpty() ? "0" : txtLeukosit.getText(),
            txtGCS.getText().trim().isEmpty() ? "0" : txtGCS.getText(),
            SkorSuhu.getText(),
            SkorMAP.getText(),
            SkorNadi.getText(),
            SkorRespirasi.getText(),
            SkorOksigenasi.getText(),
            SkorpH.getText(),
            SkorNatrium.getText(),
            SkorKalium.getText(),
            SkorKreatinin.getText(),
            SkorHematokrit.getText(),
            SkorLeukosit.getText(),
            SkorGCS.getText(),
            SkorUmur.getText(),
            SkorChronicHealth.getText(),
            TotalAPS.getText(),
            TotalAPACHE.getText(),
            EstimasiMortalitas.getText(),
            KategoriRisiko.getText(),
            KdPetugas.getText()
        }) == true) {
            emptTeks();
            tampil();
        }
    }


    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, KategoriRisiko, BtnBatal);
        }
    }

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {
        ChkInput.setSelected(true);
        isForm();
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
        if (tbObat.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from apache_score where no_rawat=? and tanggal=?", 2, new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString()
            }) == true) {
                tampil();
                emptTeks();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan anda pilih data terlebih dahulu..!!");
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
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No. Rawat dan Nama Pasien");
            return;
        }
        if (KdPetugas.getText().trim().equals("") || NmPetugas.getText().trim().equals("")) {
            Valid.textKosong(KdPetugas, "Petugas");
            return;
        }

        if (tbObat.getSelectedRow() > -1) {
            calculateAPACHE();

            if (Sequel.mengedittf("apache_score", "no_rawat=? and tanggal=?",
                    "suhu_tubuh=?,map=?,nadi=?,respirasi=?,fio2=?,pao2=?,ph=?,natrium=?,kalium=?,"
                    + "kreatinin=?,hematokrit=?,leukosit=?,gcs=?,skor_suhu=?,skor_map=?,skor_nadi=?,skor_respirasi=?,"
                    + "skor_oksigenasi=?,skor_ph=?,skor_natrium=?,skor_kalium=?,skor_kreatinin=?,skor_hematokrit=?,"
                    + "skor_leukosit=?,skor_gcs=?,skor_umur=?,skor_chronic_health=?,total_aps=?,total_apache=?,"
                    + "estimasi_mortalitas=?,kategori_risiko=?,nip=?", 35, new String[]{
                        txtSuhuTubuh.getText().trim().isEmpty() ? "0" : txtSuhuTubuh.getText(),
                        txtMAP.getText().trim().isEmpty() ? "0" : txtMAP.getText(),
                        txtNadi.getText().trim().isEmpty() ? "0" : txtNadi.getText(),
                        txtRespirasi.getText().trim().isEmpty() ? "0" : txtRespirasi.getText(),
                        txtFiO2.getText().trim().isEmpty() ? "0" : txtFiO2.getText(),
                        txtPaO2.getText().trim().isEmpty() ? "0" : txtPaO2.getText(),
                        txtpH.getText().trim().isEmpty() ? "0" : txtpH.getText(),
                        txtNatrium.getText().trim().isEmpty() ? "0" : txtNatrium.getText(),
                        txtKalium.getText().trim().isEmpty() ? "0" : txtKalium.getText(),
                        txtKreatinin.getText().trim().isEmpty() ? "0" : txtKreatinin.getText(),
                        txtHematokrit.getText().trim().isEmpty() ? "0" : txtHematokrit.getText(),
                        txtLeukosit.getText().trim().isEmpty() ? "0" : txtLeukosit.getText(),
                        txtGCS.getText().trim().isEmpty() ? "0" : txtGCS.getText(),
                        SkorSuhu.getText(),
                        SkorMAP.getText(),
                        SkorNadi.getText(),
                        SkorRespirasi.getText(),
                        SkorOksigenasi.getText(),
                        SkorpH.getText(),
                        SkorNatrium.getText(),
                        SkorKalium.getText(),
                        SkorKreatinin.getText(),
                        SkorHematokrit.getText(),
                        SkorLeukosit.getText(),
                        SkorGCS.getText(),
                        SkorUmur.getText(),
                        SkorChronicHealth.getText(),
                        TotalAPS.getText(),
                        TotalAPACHE.getText(),
                        EstimasiMortalitas.getText(),
                        KategoriRisiko.getText(),
                        KdPetugas.getText(),
                        tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                        tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString()
                    }) == true) {
                tampil();
                emptTeks();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Silahkan anda pilih data terlebih dahulu..!!");
        }
    }

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnEditActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
    }

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Valid.MyReportqry("rpapachescore.jasper", "report", "::[ Laporan APACHE II Score ]::",
                    "select p.no_rawat,p.no_rkm_medis,ps.nm_pasien,concat(concat(concat(umurdaftar,' '),sttsumur),' / ',if(ps.jk='L','Laki-laki','Perempuan')) as umur,"
                    + "ps.jk,aps.tanggal,aps.total_aps,aps.skor_umur,aps.skor_chronic_health,aps.total_apache,"
                    + "aps.estimasi_mortalitas,aps.kategori_risiko,aps.nip,pg.nama,ps.tgl_lahir "
                    + "from reg_periksa p inner join pasien ps on p.no_rkm_medis=ps.no_rkm_medis "
                    + "inner join apache_score aps on p.no_rawat=aps.no_rawat "
                    + "inner join pegawai pg on aps.nip=pg.nik "
                    + "where aps.tanggal between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59' "
                    + "order by aps.tanggal", null);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPrintActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnEdit, BtnKeluar);
        }
    }

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {
        petugas.dispose();
        dispose();
    }

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnKeluarActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnPrint, TCari);
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
            BtnAllActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnCari, TPasien);
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

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {
        if (tabMode.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {
        isForm();
    }

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            isRawat();
        } else if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            Valid.pindah(evt, TNoRw, Tanggal);
        }
    }

    private void TanggalKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, TNoRw, Jam);
    }

    private void JamKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, Tanggal, Menit);
    }

    private void MenitKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, Jam, Detik);
    }

    private void DetikKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, Menit, btnPetugas);
    }

    private void KdPetugasKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            NmPetugas.setText(petugas.tampil3(KdPetugas.getText()));
            if (!NmPetugas.getText().equals("")) {
                btnPetugas.requestFocus();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            NmPetugas.setText(petugas.tampil3(KdPetugas.getText()));
            if (!NmPetugas.getText().equals("")) {
                btnPetugas.requestFocus();
            }
        }
    }

    private void btnPetugasActionPerformed(java.awt.event.ActionEvent evt) {
        akses.setform("RMAPACHEScore");
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setAlwaysOnTop(false);
        petugas.setVisible(true);
    }

    private void btnPetugasKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            btnPetugasActionPerformed(null);
        } else {
            Valid.pindah(evt, KdPetugas, txtSuhuTubuh);
        }
    }

    // Key Released Events for automatic calculation
    private void txtSuhuTubuhKeyReleased(java.awt.event.KeyEvent evt) {
        calculateAPACHE();
    }

    private void txtMAPKeyReleased(java.awt.event.KeyEvent evt) {
        calculateAPACHE();
    }

    private void txtNadiKeyReleased(java.awt.event.KeyEvent evt) {
        calculateAPACHE();
    }

    private void txtRespirasiKeyReleased(java.awt.event.KeyEvent evt) {
        calculateAPACHE();
    }

    private void txtFiO2KeyReleased(java.awt.event.KeyEvent evt) {
        calculateAPACHE();
    }

    private void txtPaO2KeyReleased(java.awt.event.KeyEvent evt) {
        calculateAPACHE();
    }

    private void txtpHKeyReleased(java.awt.event.KeyEvent evt) {
        calculateAPACHE();
    }

    private void txtNatriumKeyReleased(java.awt.event.KeyEvent evt) {
        calculateAPACHE();
    }

    private void txtKaliumKeyReleased(java.awt.event.KeyEvent evt) {
        calculateAPACHE();
    }

    private void txtKreatininKeyReleased(java.awt.event.KeyEvent evt) {
        calculateAPACHE();
    }

    private void txtHematokritKeyReleased(java.awt.event.KeyEvent evt) {
        calculateAPACHE();
    }

    private void txtLeukositKeyReleased(java.awt.event.KeyEvent evt) {
        calculateAPACHE();
    }

    private void txtGCSKeyReleased(java.awt.event.KeyEvent evt) {
        calculateAPACHE();
    }

    private void cmbChronicHealthItemStateChanged(java.awt.event.ItemEvent evt) {
        calculateAPACHE();
    }

    private void MnCetakAPACHEActionPerformed(java.awt.event.ActionEvent evt) {
        if (tbObat.getSelectedRow() > -1) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Valid.MyReportqry("rpapachescoresingle.jasper", "report", "::[ Laporan APACHE II Score Individual ]::",
                    "select p.no_rawat,p.no_rkm_medis,ps.nm_pasien,concat(concat(concat(umurdaftar,' '),sttsumur),' / ',if(ps.jk='L','Laki-laki','Perempuan')) as umur,"
                    + "ps.jk,aps.tanggal,aps.suhu_tubuh,aps.map,aps.nadi,aps.respirasi,aps.fio2,aps.pao2,aps.ph,aps.natrium,aps.kalium,"
                    + "aps.kreatinin,aps.hematokrit,aps.leukosit,aps.gcs,aps.skor_suhu,aps.skor_map,aps.skor_nadi,aps.skor_respirasi,"
                    + "aps.skor_oksigenasi,aps.skor_ph,aps.skor_natrium,aps.skor_kalium,aps.skor_kreatinin,aps.skor_hematokrit,"
                    + "aps.skor_leukosit,aps.skor_gcs,aps.skor_umur,aps.skor_chronic_health,aps.total_aps,aps.total_apache,"
                    + "aps.estimasi_mortalitas,aps.kategori_risiko,aps.nip,pg.nama,ps.tgl_lahir "
                    + "from reg_periksa p inner join pasien ps on p.no_rkm_medis=ps.no_rkm_medis "
                    + "inner join apache_score aps on p.no_rawat=aps.no_rawat "
                    + "inner join pegawai pg on aps.nip=pg.nik "
                    + "where aps.no_rawat='" + tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "' "
                    + "and aps.tanggal='" + tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString() + "'", null);
            this.setCursor(Cursor.getDefaultCursor());
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu..!!");
        }

    }

    /**
     * Method untuk menghitung APACHE II Score berdasarkan semua parameter
     */
    private void calculateAPACHE() {
        try {
            // Calculate individual physiological scores
            int skorSuhu = calculateTemperatureScore();
            SkorSuhu.setText(String.valueOf(skorSuhu));

            int skorMAP = calculateMAPScore();
            SkorMAP.setText(String.valueOf(skorMAP));

            int skorNadi = calculateHeartRateScore();
            SkorNadi.setText(String.valueOf(skorNadi));

            int skorRespirasi = calculateRespiratoryRateScore();
            SkorRespirasi.setText(String.valueOf(skorRespirasi));

            int skorOksigenasi = calculateOxygenationScore();
            SkorOksigenasi.setText(String.valueOf(skorOksigenasi));

            int skorpH = calculatepHScore();
            SkorpH.setText(String.valueOf(skorpH));

            int skorNatrium = calculateSodiumScore();
            SkorNatrium.setText(String.valueOf(skorNatrium));

            int skorKalium = calculatePotassiumScore();
            SkorKalium.setText(String.valueOf(skorKalium));

            int skorKreatinin = calculateCreatinineScore();
            SkorKreatinin.setText(String.valueOf(skorKreatinin));

            int skorHematokrit = calculateHematocritScore();
            SkorHematokrit.setText(String.valueOf(skorHematokrit));

            int skorLeukosit = calculateWBCScore();
            SkorLeukosit.setText(String.valueOf(skorLeukosit));

            int skorGCS = calculateGCSScore();
            SkorGCS.setText(String.valueOf(skorGCS));

            // Calculate age score
            int skorUmur = calculateAgeScore();
            SkorUmur.setText(String.valueOf(skorUmur));

            // Calculate chronic health score
            int skorChronicHealth = calculateChronicHealthScore();
            SkorChronicHealth.setText(String.valueOf(skorChronicHealth));

            // Calculate total APS (Acute Physiology Score)
            int totalAPS = skorSuhu + skorMAP + skorNadi + skorRespirasi + skorOksigenasi
                    + skorpH + skorNatrium + skorKalium + skorKreatinin + skorHematokrit
                    + skorLeukosit + skorGCS;
            TotalAPS.setText(String.valueOf(totalAPS));

            // Calculate total APACHE II score
            int totalAPACHE = totalAPS + skorUmur + skorChronicHealth;
            TotalAPACHE.setText(String.valueOf(totalAPACHE));

            // Calculate mortality and risk category
            String[] mortalitasRisiko = getMortalityAndRisk(totalAPACHE);
            EstimasiMortalitas.setText(mortalitasRisiko[0]);
            KategoriRisiko.setText(mortalitasRisiko[1]);

        } catch (NumberFormatException e) {
            // Handle invalid input
        }
    }

    /**
     * Calculate Temperature Score (Rectal Temperature)
     */
    private int calculateTemperatureScore() {
        try {
            if (txtSuhuTubuh.getText().isEmpty()) {
                return 0;
            }

            double temp = Double.parseDouble(txtSuhuTubuh.getText());

            if (temp >= 41.0) {
                return 4;
            } else if (temp >= 39.0) {
                return 3;
            } else if (temp >= 38.5) {
                return 1;
            } else if (temp >= 36.0) {
                return 0;
            } else if (temp >= 34.0) {
                return 1;
            } else if (temp >= 32.0) {
                return 2;
            } else if (temp >= 30.0) {
                return 3;
            } else {
                return 4;
            }

        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Calculate Mean Arterial Pressure Score
     */
    private int calculateMAPScore() {
        try {
            if (txtMAP.getText().isEmpty()) {
                return 0;
            }

            int map = Integer.parseInt(txtMAP.getText());

            if (map >= 160) {
                return 4;
            } else if (map >= 130) {
                return 3;
            } else if (map >= 110) {
                return 2;
            } else if (map >= 70) {
                return 0;
            } else if (map >= 50) {
                return 2;
            } else {
                return 4;
            }

        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Calculate Heart Rate Score
     */
    private int calculateHeartRateScore() {
        try {
            if (txtNadi.getText().isEmpty()) {
                return 0;
            }

            int hr = Integer.parseInt(txtNadi.getText());

            if (hr >= 180) {
                return 4;
            } else if (hr >= 140) {
                return 3;
            } else if (hr >= 110) {
                return 2;
            } else if (hr >= 70) {
                return 0;
            } else if (hr >= 55) {
                return 2;
            } else if (hr >= 40) {
                return 3;
            } else {
                return 4;
            }

        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Calculate Respiratory Rate Score
     */
    private int calculateRespiratoryRateScore() {
        try {
            if (txtRespirasi.getText().isEmpty()) {
                return 0;
            }

            int rr = Integer.parseInt(txtRespirasi.getText());

            if (rr >= 50) {
                return 4;
            } else if (rr >= 35) {
                return 3;
            } else if (rr >= 25) {
                return 1;
            } else if (rr >= 12) {
                return 0;
            } else if (rr >= 10) {
                return 1;
            } else if (rr >= 6) {
                return 2;
            } else {
                return 4;
            }

        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Calculate Oxygenation Score (A-aDO2 or PaO2)
     */
    private int calculateOxygenationScore() {
        try {
            if (txtFiO2.getText().isEmpty() || txtPaO2.getText().isEmpty()) {
                return 0;
            }

            double fio2 = Double.parseDouble(txtFiO2.getText());
            double pao2 = Double.parseDouble(txtPaO2.getText());

            if (fio2 >= 50) {
                // Use A-aDO2 when FiO2 ≥ 0.5
                double aado2 = (fio2 * 713 - 40) - pao2; // Simplified A-aDO2 calculation

                if (aado2 >= 500) {
                    return 4;
                } else if (aado2 >= 350) {
                    return 3;
                } else if (aado2 >= 200) {
                    return 2;
                } else {
                    return 0;
                }
            } else {
                // Use PaO2 when FiO2 < 0.5
                if (pao2 >= 70) {
                    return 0;
                } else if (pao2 >= 61) {
                    return 1;
                } else if (pao2 >= 55) {
                    return 3;
                } else {
                    return 4;
                }
            }

        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Calculate Arterial pH Score
     */
    private int calculatepHScore() {
        try {
            if (txtpH.getText().isEmpty()) {
                return 0;
            }

            double ph = Double.parseDouble(txtpH.getText());

            if (ph >= 7.7) {
                return 4;
            } else if (ph >= 7.6) {
                return 3;
            } else if (ph >= 7.5) {
                return 1;
            } else if (ph >= 7.33) {
                return 0;
            } else if (ph >= 7.25) {
                return 2;
            } else if (ph >= 7.15) {
                return 3;
            } else {
                return 4;
            }

        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Calculate Serum Sodium Score
     */
    private int calculateSodiumScore() {
        try {
            if (txtNatrium.getText().isEmpty()) {
                return 0;
            }

            int sodium = Integer.parseInt(txtNatrium.getText());

            if (sodium >= 180) {
                return 4;
            } else if (sodium >= 160) {
                return 3;
            } else if (sodium >= 155) {
                return 2;
            } else if (sodium >= 150) {
                return 1;
            } else if (sodium >= 130) {
                return 0;
            } else if (sodium >= 120) {
                return 2;
            } else if (sodium >= 111) {
                return 3;
            } else {
                return 4;
            }

        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Calculate Serum Potassium Score
     */
    private int calculatePotassiumScore() {
        try {
            if (txtKalium.getText().isEmpty()) {
                return 0;
            }

            double potassium = Double.parseDouble(txtKalium.getText());

            if (potassium >= 7.0) {
                return 4;
            } else if (potassium >= 6.0) {
                return 3;
            } else if (potassium >= 5.5) {
                return 1;
            } else if (potassium >= 3.5) {
                return 0;
            } else if (potassium >= 3.0) {
                return 1;
            } else if (potassium >= 2.5) {
                return 2;
            } else {
                return 4;
            }

        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Calculate Serum Creatinine Score
     */
    private int calculateCreatinineScore() {
        try {
            if (txtKreatinin.getText().isEmpty()) {
                return 0;
            }

            double creatinine = Double.parseDouble(txtKreatinin.getText());

            if (creatinine >= 3.5) {
                return 4;
            } else if (creatinine >= 2.0) {
                return 3;
            } else if (creatinine >= 1.5) {
                return 2;
            } else if (creatinine >= 0.6) {
                return 0;
            } else {
                return 2;
            }

        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Calculate Hematocrit Score
     */
    private int calculateHematocritScore() {
        try {
            if (txtHematokrit.getText().isEmpty()) {
                return 0;
            }

            double hematocrit = Double.parseDouble(txtHematokrit.getText());

            if (hematocrit >= 60) {
                return 4;
            } else if (hematocrit >= 50) {
                return 2;
            } else if (hematocrit >= 46) {
                return 1;
            } else if (hematocrit >= 30) {
                return 0;
            } else if (hematocrit >= 20) {
                return 2;
            } else {
                return 4;
            }

        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Calculate White Blood Cell Count Score
     */
    private int calculateWBCScore() {
        try {
            if (txtLeukosit.getText().isEmpty()) {
                return 0;
            }

            double wbc = Double.parseDouble(txtLeukosit.getText());

            if (wbc >= 40.0) {
                return 4;
            } else if (wbc >= 20.0) {
                return 2;
            } else if (wbc >= 15.0) {
                return 1;
            } else if (wbc >= 3.0) {
                return 0;
            } else if (wbc >= 1.0) {
                return 2;
            } else {
                return 4;
            }

        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Calculate Glasgow Coma Scale Score
     */
    private int calculateGCSScore() {
        try {
            if (txtGCS.getText().isEmpty()) {
                return 0;
            }

            int gcs = Integer.parseInt(txtGCS.getText());

            // APACHE II uses (15 - actual GCS)
            int score = 15 - gcs;
            if (score < 0) {
                score = 0;
            }
            if (score > 15) {
                score = 15;
            }

            return score;

        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Calculate Age Score based on patient's age
     */
    private int calculateAgeScore() {
        try {
            // Calculate age from birth date
            if (TglLahir.getText().isEmpty()) {
                return 0;
            }

            String[] birthDate = TglLahir.getText().split("-");
            if (birthDate.length != 3) {
                return 0;
            }

            Calendar birth = Calendar.getInstance();
            birth.set(Integer.parseInt(birthDate[0]),
                    Integer.parseInt(birthDate[1]) - 1,
                    Integer.parseInt(birthDate[2]));

            Calendar now = Calendar.getInstance();
            int age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);

            if (now.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            if (age >= 75) {
                return 6;
            } else if (age >= 65) {
                return 5;
            } else if (age >= 55) {
                return 3;
            } else if (age >= 45) {
                return 2;
            } else {
                return 0;
            }

        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Calculate Chronic Health Points
     */
    private int calculateChronicHealthScore() {
        int selectedIndex = cmbChronicHealth.getSelectedIndex();

        switch (selectedIndex) {
            case 0:
                return 0; // No chronic health problems
            case 1:
                return 5; // Non-operative or post emergency operative
            case 2:
                return 2; // Post elective operative
            default:
                return 0;
        }
    }

    /**
     * Get mortality percentage and risk category based on APACHE II score
     */
    private String[] getMortalityAndRisk(int apacheScore) {
        String mortality;
        String riskCategory;

        if (apacheScore <= 4) {
            mortality = "4%";
            riskCategory = "Risiko Sangat Rendah";
        } else if (apacheScore <= 9) {
            mortality = "8%";
            riskCategory = "Risiko Rendah";
        } else if (apacheScore <= 14) {
            mortality = "15%";
            riskCategory = "Risiko Sedang";
        } else if (apacheScore <= 19) {
            mortality = "25%";
            riskCategory = "Risiko Tinggi";
        } else if (apacheScore <= 24) {
            mortality = "40%";
            riskCategory = "Risiko Sangat Tinggi";
        } else if (apacheScore <= 29) {
            mortality = "55%";
            riskCategory = "Risiko Kritis";
        } else if (apacheScore <= 34) {
            mortality = "75%";
            riskCategory = "Risiko Sangat Kritis";
        } else {
            mortality = ">85%";
            riskCategory = "Risiko Ekstrem";
        }

        return new String[]{mortality, riskCategory};
    }

    /**
     * Method untuk mengosongkan semua input fields
     */
    public void emptTeks() {
        TNoRw.setText("");
        TNoRM.setText("");
        TPasien.setText("");
        TglLahir.setText("");
        KdPetugas.setText("");
        NmPetugas.setText("");

        // Clear all physiological inputs
        txtSuhuTubuh.setText("");
        txtMAP.setText("");
        txtNadi.setText("");
        txtRespirasi.setText("");
        txtFiO2.setText("");
        txtPaO2.setText("");
        txtpH.setText("");
        txtNatrium.setText("");
        txtKalium.setText("");
        txtKreatinin.setText("");
        txtHematokrit.setText("");
        txtLeukosit.setText("");
        txtGCS.setText("");

        // Reset combo box
        cmbChronicHealth.setSelectedIndex(0);

        // Clear all scores
        SkorSuhu.setText("0");
        SkorMAP.setText("0");
        SkorNadi.setText("0");
        SkorRespirasi.setText("0");
        SkorOksigenasi.setText("0");
        SkorpH.setText("0");
        SkorNatrium.setText("0");
        SkorKalium.setText("0");
        SkorKreatinin.setText("0");
        SkorHematokrit.setText("0");
        SkorLeukosit.setText("0");
        SkorGCS.setText("0");
        SkorUmur.setText("0");
        SkorChronicHealth.setText("0");

        // Clear results
        TotalAPS.setText("0");
        TotalAPACHE.setText("0");
        EstimasiMortalitas.setText("");
        KategoriRisiko.setText("");

        TNoRw.requestFocus();
    }

    /**
     * Method untuk setup table properties
     */
    private void setupTable() {
        tbObat.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);

        // Set column widths
        TableColumn column;
        for (int i = 0; i < tbObat.getColumnCount(); i++) {
            column = tbObat.getColumnModel().getColumn(i);
            switch (i) {
                case 0:
                    column.setPreferredWidth(105);
                    break; // No.Rawat
                case 1:
                    column.setPreferredWidth(70);
                    break;  // No.R.M.
                case 2:
                    column.setPreferredWidth(160);
                    break; // Nama Pasien
                case 3:
                    column.setPreferredWidth(40);
                    break;  // Umur
                case 4:
                    column.setPreferredWidth(25);
                    break;  // JK
                case 5:
                    column.setPreferredWidth(115);
                    break; // Tanggal
                case 6:
                    column.setPreferredWidth(50);
                    break;  // APS
                case 7:
                    column.setPreferredWidth(70);
                    break;  // Age Points
                case 8:
                    column.setPreferredWidth(90);
                    break;  // Chronic Health
                case 9:
                    column.setPreferredWidth(90);
                    break;  // Total APACHE II
                case 10:
                    column.setPreferredWidth(80);
                    break; // Mortalitas
                case 11:
                    column.setPreferredWidth(120);
                    break;// Kategori Risiko
                case 12:
                    column.setPreferredWidth(80);
                    break; // NIP
                case 13:
                    column.setPreferredWidth(150);
                    break;// Petugas
                case 14:
                    column.setPreferredWidth(65);
                    break; // Tgl.Lahir
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());
    }

    /**
     * Method untuk menampilkan data
     */
    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            if (TCari.getText().equals("")) {
                ps = koneksi.prepareStatement(
                        "select p.no_rawat,p.no_rkm_medis,ps.nm_pasien,concat(concat(concat(umurdaftar,' '),sttsumur),' / ',if(ps.jk='L','Laki-laki','Perempuan')) as umur,"
                        + "ps.jk,aps.tanggal,aps.total_aps,aps.skor_umur,aps.skor_chronic_health,aps.total_apache,"
                        + "aps.estimasi_mortalitas,aps.kategori_risiko,aps.nip,pg.nama,ps.tgl_lahir "
                        + "from reg_periksa p inner join pasien ps on p.no_rkm_medis=ps.no_rkm_medis "
                        + "inner join apache_score aps on p.no_rawat=aps.no_rawat "
                        + "inner join pegawai pg on aps.nip=pg.nik "
                        + "where aps.tanggal between ? and ? order by aps.tanggal desc");
            } else {
                ps = koneksi.prepareStatement(
                        "select p.no_rawat,p.no_rkm_medis,ps.nm_pasien,concat(concat(concat(umurdaftar,' '),sttsumur),' / ',if(ps.jk='L','Laki-laki','Perempuan')) as umur,"
                        + "ps.jk,aps.tanggal,aps.total_aps,aps.skor_umur,aps.skor_chronic_health,aps.total_apache,"
                        + "aps.estimasi_mortalitas,aps.kategori_risiko,aps.nip,pg.nama,ps.tgl_lahir "
                        + "from reg_periksa p inner join pasien ps on p.no_rkm_medis=ps.no_rkm_medis "
                        + "inner join apache_score aps on p.no_rawat=aps.no_rawat "
                        + "inner join pegawai pg on aps.nip=pg.nik "
                        + "where aps.tanggal between ? and ? and (p.no_rawat like ? or ps.nm_pasien like ? or pg.nama like ?) "
                        + "order by aps.tanggal desc");
            }

            try {
                if (TCari.getText().equals("")) {
                    ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                    ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                } else {
                    ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                    ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                    ps.setString(3, "%" + TCari.getText() + "%");
                    ps.setString(4, "%" + TCari.getText() + "%");
                    ps.setString(5, "%" + TCari.getText() + "%");
                }
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat"),
                        rs.getString("no_rkm_medis"),
                        rs.getString("nm_pasien"),
                        rs.getString("umur"),
                        rs.getString("jk"),
                        rs.getString("tanggal"),
                        rs.getString("total_aps"),
                        rs.getString("skor_umur"),
                        rs.getString("skor_chronic_health"),
                        rs.getString("total_apache"),
                        rs.getString("estimasi_mortalitas"),
                        rs.getString("kategori_risiko"),
                        rs.getString("nip"),
                        rs.getString("nama"),
                        rs.getString("tgl_lahir")
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

    /**
     * Method untuk mengambil data dari table yang dipilih
     */
    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            TglLahir.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 14).toString());
            
            Valid.SetTgl2(Tanggal, tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            
            // Get detailed data from database
            try {
                ps = koneksi.prepareStatement(
                    "select * from apache_score where no_rawat=? and tanggal=?");
                ps.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                ps.setString(2, tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    txtSuhuTubuh.setText(rs.getString("suhu_tubuh"));
                    txtMAP.setText(rs.getString("map"));
                    txtNadi.setText(rs.getString("nadi"));
                    txtRespirasi.setText(rs.getString("respirasi"));
                    txtFiO2.setText(rs.getString("fio2"));
                    txtPaO2.setText(rs.getString("pao2"));
                    txtpH.setText(rs.getString("ph"));
                    txtNatrium.setText(rs.getString("natrium"));
                    txtKalium.setText(rs.getString("kalium"));
                    txtKreatinin.setText(rs.getString("kreatinin"));
                    txtHematokrit.setText(rs.getString("hematokrit"));
                    txtLeukosit.setText(rs.getString("leukosit"));
                    txtGCS.setText(rs.getString("gcs"));
                    
                    // Set chronic health combo
                    int chronicHealthScore = Integer.parseInt(rs.getString("skor_chronic_health"));
                    if (chronicHealthScore == 0) cmbChronicHealth.setSelectedIndex(0);
                    else if (chronicHealthScore == 5) cmbChronicHealth.setSelectedIndex(1);
                    else if (chronicHealthScore == 2) cmbChronicHealth.setSelectedIndex(2);
                    
                    KdPetugas.setText(rs.getString("nip"));
                    NmPetugas.setText(petugas.tampil3(rs.getString("nip")));
                    
                    calculateAPACHE(); // Recalculate to ensure consistency
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (Exception e) {
                    }
                }
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
    }
    /**
     * Method untuk validasi rawat
     */
    public void isRawat() {
        try {
            ps = koneksi.prepareStatement(
                    "select p.no_rkm_medis,ps.nm_pasien,if(ps.jk='L','Laki-laki','Perempuan') as jk,"
                    + "ps.tgl_lahir,p.tgl_registrasi,concat(concat(concat(umurdaftar,' '),sttsumur),' / ',if(ps.jk='L','Laki-laki','Perempuan')) as umur "
                    + "from reg_periksa p inner join pasien ps on p.no_rkm_medis=ps.no_rkm_medis "
                    + "where p.no_rawat=?");
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    TPasien.setText(rs.getString("nm_pasien"));
                    JK.setText(rs.getString("jk"));
                    TglLahir.setText(rs.getString("tgl_lahir"));
                    TanggalRegistrasi.setText(rs.getString("tgl_registrasi"));
                    Umur.setText(rs.getString("umur"));
                    calculateAPACHE(); // Recalculate age score when patient data is loaded
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
    }

    /**
     * Method untuk mengatur tampilan form input
     */
    public void isForm() {
        if (ChkInput.isSelected() == true) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(getWidth(), 350));
            FormInput.setVisible(true);
            ChkInput.setVisible(true);
        } else if (ChkInput.isSelected() == false) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(getWidth(), 20));
            FormInput.setVisible(false);
            ChkInput.setVisible(true);
        }
    }

    /**
     * Method untuk mengatur waktu saat ini
     */
    private void jam() {
        Calendar cal = Calendar.getInstance();
        Jam.setSelectedItem(String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)));
        Menit.setSelectedItem(String.format("%02d", cal.get(Calendar.MINUTE)));
        Detik.setSelectedItem(String.format("%02d", cal.get(Calendar.SECOND)));
    }

    /**
     * Method untuk mengecek akses user
     */
    public void isCek() {
        BtnSimpan.setEnabled(akses.gettindakan_ranap());
        BtnHapus.setEnabled(akses.gettindakan_ranap());
        BtnEdit.setEnabled(akses.gettindakan_ranap());
        BtnPrint.setEnabled(akses.gettindakan_ranap());
        if (akses.getjml2() >= 1) {
            KdPetugas.setEditable(false);
            btnPetugas.setEnabled(false);
            KdPetugas.setText(akses.getkode());
            NmPetugas.setText(petugas.tampil3(akses.getkode()));
            if (NmPetugas.getText().equals("")) {
                KdPetugas.setText("");
                JOptionPane.showMessageDialog(null, "User login bukan petugas...!!");
            }
        }
    }

    /**
     * Method untuk set nilai default
     */
    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        Tanggal.setSelectedItem(tgl2);
        isRawat();
    }

    /**
     * Method untuk set nilai default dengan petugas
     */
    public void setNoRm(String norwt, String norm, String nama) {
        TNoRw.setText(norwt);
        TNoRM.setText(norm);
        TPasien.setText(nama);
        TCari.setText(norwt);
        ChkInput.setSelected(true);
        isForm();
    }

    /**
     * Main method untuk menjalankan form
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMAPACHEScore dialog = new RMAPACHEScore(new javax.swing.JFrame(), true);
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
    private widget.CekBox ChkInput;
    private widget.CekBox ChkKejadian;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.ComboBox Detik;
    private widget.TextBox EstimasiMortalitas;
    private widget.PanelBiasa FormInput;
    private widget.TextBox JK;
    private widget.ComboBox Jam;
    private widget.TextBox KategoriRisiko;
    private widget.TextBox KdPetugas;
    private widget.Label LCount;
    private widget.ComboBox Menit;
    private javax.swing.JMenuItem MnCetakAPACHE;
    private widget.TextBox NmPetugas;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox SkorChronicHealth;
    private widget.TextBox SkorGCS;
    private widget.TextBox SkorHematokrit;
    private widget.TextBox SkorKalium;
    private widget.TextBox SkorKreatinin;
    private widget.TextBox SkorLeukosit;
    private widget.TextBox SkorMAP;
    private widget.TextBox SkorNadi;
    private widget.TextBox SkorNatrium;
    private widget.TextBox SkorOksigenasi;
    private widget.TextBox SkorRespirasi;
    private widget.TextBox SkorSuhu;
    private widget.TextBox SkorUmur;
    private widget.TextBox SkorpH;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Tanggal Tanggal;
    private widget.TextBox TanggalRegistrasi;
    private widget.TextBox TglLahir;
    private widget.TextBox TotalAPACHE;
    private widget.TextBox TotalAPS;
    private widget.TextBox Umur;
    private widget.Button btnPetugas;
    private widget.ComboBox cmbChronicHealth;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel16;
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
    private widget.Label jLabel32;
    private widget.Label jLabel33;
    private widget.Label jLabel34;
    private widget.Label jLabel35;
    private widget.Label jLabel36;
    private widget.Label jLabel37;
    private widget.Label jLabel38;
    private widget.Label jLabel39;
    private widget.Label jLabel4;
    private widget.Label jLabel40;
    private widget.Label jLabel41;
    private widget.Label jLabel42;
    private widget.Label jLabel43;
    private widget.Label jLabel44;
    private widget.Label jLabel45;
    private widget.Label jLabel46;
    private widget.Label jLabel47;
    private widget.Label jLabel48;
    private widget.Label jLabel49;
    private widget.Label jLabel50;
    private widget.Label jLabel51;
    private widget.Label jLabel52;
    private widget.Label jLabel53;
    private widget.Label jLabel54;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbObat;
    private widget.TextBox txtFiO2;
    private widget.TextBox txtGCS;
    private widget.TextBox txtHematokrit;
    private widget.TextBox txtKalium;
    private widget.TextBox txtKreatinin;
    private widget.TextBox txtLeukosit;
    private widget.TextBox txtMAP;
    private widget.TextBox txtNadi;
    private widget.TextBox txtNatrium;
    private widget.TextBox txtPaO2;
    private widget.TextBox txtRespirasi;
    private widget.TextBox txtSuhuTubuh;
    private widget.TextBox txtpH;
    // End of variables declaration
}
