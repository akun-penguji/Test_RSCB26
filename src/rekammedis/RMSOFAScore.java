/*
 * SOFA Score Calculator
 * Sequential Organ Failure Assessment
 * 
 * Referensi: Vincent JL, Moreno R, Takala J, et al. 
 * The SOFA (Sepsis-related Organ Failure Assessment) score to describe organ dysfunction/failure.
 * Intensive Care Medicine. 1996;22(7):707-710.
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
 * Form SOFA Score Calculator - NetBeans Version
 *
 * @author SIMRS Khanza
 */
public class RMSOFAScore extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private int i = 0;
    private PreparedStatement ps;
    private ResultSet rs;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);

    /**
     * Creates new form RMSOFAScore
     */
    public RMSOFAScore(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.R.M.", "Nama Pasien", "Umur", "JK", "Tanggal",
            "Respirasi", "Kardio", "Neuro", "Hemato", "Renal", "Hepatik",
            "Total SOFA", "Delta SOFA", "Kategori Risiko", "Mortalitas", "NIP", "Petugas", "Tgl.Lahir"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        initComponents();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("SOFA Score Calculator (Sequential Organ Failure Assessment)");
        setResizable(false);

        tbObat.setModel(tabMode);
        setupTable();
        jam();

        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));
        txtPaO2.setDocument(new batasInput((byte) 5).getOnlyAngka(txtPaO2));
        txtFiO2.setDocument(new batasInput((byte) 3).getOnlyAngka(txtFiO2));
        txtMAP.setDocument(new batasInput((byte) 5).getOnlyAngka(txtMAP));
        txtGCS.setDocument(new batasInput((byte) 2).getOnlyAngka(txtGCS));
        txtPlatelet.setDocument(new batasInput((byte) 5).getOnlyAngka(txtPlatelet));
        txtKreatinin.setDocument(new batasInput((byte) 5).getOnlyAngka(txtKreatinin));
        txtUrinOutput.setDocument(new batasInput((byte) 5).getOnlyAngka(txtUrinOutput));
        txtBilirubin.setDocument(new batasInput((byte) 5).getOnlyAngka(txtBilirubin));
        txtSOFASebelum.setDocument(new batasInput((byte) 2).getOnlyAngka(txtSOFASebelum));

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
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnCetakSOFA = new javax.swing.JMenuItem();
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
        txtSOFASebelum = new widget.TextBox();
        jLabel14 = new widget.Label();
        jLabel15 = new widget.Label();
        txtPaO2 = new widget.TextBox();
        jLabel22 = new widget.Label();
        txtFiO2 = new widget.TextBox();
        jLabel23 = new widget.Label();
        cmbVentilator = new widget.ComboBox();
        jLabel24 = new widget.Label();
        SkorRespirasi = new widget.TextBox();
        jLabel25 = new widget.Label();
        txtMAP = new widget.TextBox();
        jLabel26 = new widget.Label();
        cmbVasopresor = new widget.ComboBox();
        jLabel27 = new widget.Label();
        SkorKardio = new widget.TextBox();
        jLabel28 = new widget.Label();
        txtGCS = new widget.TextBox();
        jLabel30 = new widget.Label();
        SkorNeuro = new widget.TextBox();
        jLabel31 = new widget.Label();
        txtPlatelet = new widget.TextBox();
        jLabel32 = new widget.Label();
        SkorHemato = new widget.TextBox();
        jLabel33 = new widget.Label();
        txtKreatinin = new widget.TextBox();
        jLabel34 = new widget.Label();
        txtUrinOutput = new widget.TextBox();
        jLabel35 = new widget.Label();
        SkorRenal = new widget.TextBox();
        jLabel36 = new widget.Label();
        txtBilirubin = new widget.TextBox();
        jLabel37 = new widget.Label();
        SkorHepatik = new widget.TextBox();
        jLabel38 = new widget.Label();
        TotalSOFA = new widget.TextBox();
        jLabel39 = new widget.Label();
        DeltaSOFA = new widget.TextBox();
        jLabel40 = new widget.Label();
        KategoriRisiko = new widget.TextBox();
        jLabel41 = new widget.Label();
        EstimasiMortalitas = new widget.TextBox();
        ChkInput = new widget.CekBox();
        // Tambahkan di dalam method initComponents() sebelum setText() dipanggil:

        jLabel40 = new widget.Label();
        jLabel41 = new widget.Label();
        jLabel42 = new widget.Label();
        jLabel43 = new widget.Label();
        jLabel44 = new widget.Label();
        jLabel45 = new widget.Label();
        jLabel46 = new widget.Label();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnCetakSOFA.setBackground(new java.awt.Color(255, 255, 254));
        MnCetakSOFA.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnCetakSOFA.setForeground(new java.awt.Color(50, 50, 50));
        MnCetakSOFA.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnCetakSOFA.setText("Cetak SOFA Score");
        MnCetakSOFA.setName("MnCetakSOFA"); // NOI18N
        MnCetakSOFA.setPreferredSize(new java.awt.Dimension(200, 26));
        MnCetakSOFA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnCetakSOFAActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnCetakSOFA);

        JK.setHighlighter(null);
        JK.setName("JK"); // NOI18N

        Umur.setHighlighter(null);
        Umur.setName("Umur"); // NOI18N

        TanggalRegistrasi.setHighlighter(null);
        TanggalRegistrasi.setName("TanggalRegistrasi"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ SOFA Score Calculator - Sequential Organ Failure Assessment ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setPreferredSize(new java.awt.Dimension(559, 334));
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(300, 200));

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

        jLabel21.setText("Tanggal :");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel21);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari1);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("s.d.");
        jLabel29.setName("jLabel29"); // NOI18N
        jLabel29.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel29);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
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
        TCari.setPreferredSize(new java.awt.Dimension(315, 23));
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
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 500));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

// Buat ScrollPane untuk FormInput
        ScrollFormInput = new widget.ScrollPane();
        ScrollFormInput.setName("ScrollFormInput");
        ScrollFormInput.setPreferredSize(new java.awt.Dimension(870, 500));

        FormInput.setBackground(new java.awt.Color(250, 255, 245));
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(870, 600)); // Tingkatkan height
        FormInput.setLayout(null);

        ScrollFormInput.setViewportView(FormInput);
        PanelInput.add(ScrollFormInput, java.awt.BorderLayout.CENTER);

        jLabel4.setText("No.Rawat :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 10, 75, 23);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(79, 10, 141, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        FormInput.add(TNoRM);
        TNoRM.setBounds(222, 10, 112, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        FormInput.add(TPasien);
        TPasien.setBounds(336, 10, 285, 23);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(625, 10, 60, 23);

        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir"); // NOI18N
        FormInput.add(TglLahir);
        TglLahir.setBounds(689, 10, 100, 23);

        jLabel16.setText("Tanggal :");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setVerifyInputWhenFocusTarget(false);
        FormInput.add(jLabel16);
        jLabel16.setBounds(0, 40, 75, 23);

        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setDisplayFormat("dd-MM-yyyy");
        Tanggal.setName("Tanggal"); // NOI18N
        Tanggal.setOpaque(false);
        Tanggal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalKeyPressed(evt);
            }
        });
        FormInput.add(Tanggal);
        Tanggal.setBounds(79, 40, 90, 23);

        Jam.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"}));
        Jam.setName("Jam"); // NOI18N
        Jam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JamKeyPressed(evt);
            }
        });
        FormInput.add(Jam);
        Jam.setBounds(173, 40, 62, 23);

        Menit.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"}));
        Menit.setName("Menit"); // NOI18N
        Menit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MenitKeyPressed(evt);
            }
        });
        FormInput.add(Menit);
        Menit.setBounds(238, 40, 62, 23);

        Detik.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"}));
        Detik.setName("Detik"); // NOI18N
        Detik.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DetikKeyPressed(evt);
            }
        });
        FormInput.add(Detik);
        Detik.setBounds(303, 40, 62, 23);

        ChkKejadian.setBorder(null);
        ChkKejadian.setSelected(true);
        ChkKejadian.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkKejadian.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setName("ChkKejadian"); // NOI18N
        FormInput.add(ChkKejadian);
        ChkKejadian.setBounds(368, 40, 23, 23);

        jLabel18.setText("Petugas :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(400, 40, 70, 23);

        KdPetugas.setEditable(false);
        KdPetugas.setHighlighter(null);
        KdPetugas.setName("KdPetugas"); // NOI18N
        KdPetugas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdPetugasKeyPressed(evt);
            }
        });
        FormInput.add(KdPetugas);
        KdPetugas.setBounds(474, 40, 94, 23);

        NmPetugas.setEditable(false);
        NmPetugas.setName("NmPetugas"); // NOI18N
        FormInput.add(NmPetugas);
        NmPetugas.setBounds(571, 40, 187, 23);

        btnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPetugas.setMnemonic('2');
        btnPetugas.setText("...");
        btnPetugas.setToolTipText("Alt+2");
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

        jLabel19.setText("SOFA Score Calculator - 6 Parameter Organ Dysfunction :");
        jLabel19.setName("jLabel19"); // NOI18N
        FormInput.add(jLabel19);
        jLabel19.setBounds(0, 80, 350, 23);

        jLabel20.setText("SOFA Sebelumnya :");
        jLabel20.setName("jLabel20"); // NOI18N
        FormInput.add(jLabel20);
        jLabel20.setBounds(400, 80, 100, 23);

        txtSOFASebelum.setName("txtSOFASebelum"); // NOI18N
        txtSOFASebelum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSOFASebelumKeyReleased(evt);
            }
        });
        FormInput.add(txtSOFASebelum);
        txtSOFASebelum.setBounds(504, 80, 60, 23);

        // 1. RESPIRASI
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("1. RESPIRASI");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(0, 110, 100, 23);

        jLabel15.setText("PaO2 :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(0, 140, 50, 23);

        txtPaO2.setName("txtPaO2"); // NOI18N
        txtPaO2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPaO2KeyReleased(evt);
            }
        });
        FormInput.add(txtPaO2);
        txtPaO2.setBounds(54, 140, 80, 23);

        jLabel22.setText("FiO2(%) :");
        jLabel22.setName("jLabel22"); // NOI18N
        FormInput.add(jLabel22);
        jLabel22.setBounds(140, 140, 60, 23);

        txtFiO2.setName("txtFiO2"); // NOI18N
        txtFiO2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtFiO2KeyReleased(evt);
            }
        });
        FormInput.add(txtFiO2);
        txtFiO2.setBounds(204, 140, 80, 23);

        jLabel23.setText("Ventilator :");
        jLabel23.setName("jLabel23"); // NOI18N
        FormInput.add(jLabel23);
        jLabel23.setBounds(290, 140, 70, 23);

        cmbVentilator.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Tidak", "Ya"}));
        cmbVentilator.setName("cmbVentilator"); // NOI18N
        cmbVentilator.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbVentilatorItemStateChanged(evt);
            }
        });
        FormInput.add(cmbVentilator);
        cmbVentilator.setBounds(364, 140, 80, 23);

        jLabel24.setText("Skor :");
        jLabel24.setName("jLabel24"); // NOI18N
        FormInput.add(jLabel24);
        jLabel24.setBounds(450, 140, 40, 23);

        SkorRespirasi.setEditable(false);
        SkorRespirasi.setText("0");
        SkorRespirasi.setFocusTraversalPolicyProvider(true);
        SkorRespirasi.setName("SkorRespirasi"); // NOI18N
        SkorRespirasi.setOpaque(true);
        FormInput.add(SkorRespirasi);
        SkorRespirasi.setBounds(494, 140, 44, 23);

        // 2. KARDIOVASKULAR
        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("2. KARDIOVASKULAR");
        jLabel25.setName("jLabel25"); // NOI18N
        FormInput.add(jLabel25);
        jLabel25.setBounds(0, 170, 150, 23);

        jLabel26.setText("MAP :");
        jLabel26.setName("jLabel26"); // NOI18N
        FormInput.add(jLabel26);
        jLabel26.setBounds(0, 200, 50, 23);

        txtMAP.setName("txtMAP"); // NOI18N
        txtMAP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMAPKeyReleased(evt);
            }
        });
        FormInput.add(txtMAP);
        txtMAP.setBounds(54, 200, 80, 23);

        jLabel27.setText("Vasopresor :");
        jLabel27.setName("jLabel27"); // NOI18N
        FormInput.add(jLabel27);
        jLabel27.setBounds(140, 200, 70, 23);

        cmbVasopresor.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "Tidak ada",
            "Dobutamin (any dose)",
            "Dopamin ≤ 5 μg/kg/min",
            "Dopamin > 5 μg/kg/min",
            "Dopamin > 15 μg/kg/min",
            "Epinefrin ≤ 0.1 μg/kg/min",
            "Epinefrin > 0.1 μg/kg/min",
            "Norepinefrin ≤ 0.1 μg/kg/min",
            "Norepinefrin > 0.1 μg/kg/min"
        }));
        cmbVasopresor.setName("cmbVasopresor"); // NOI18N
        cmbVasopresor.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbVasopresorItemStateChanged(evt);
            }
        });
        FormInput.add(cmbVasopresor);
        cmbVasopresor.setBounds(214, 200, 200, 23);

        jLabel28.setText("Skor :");
        jLabel28.setName("jLabel28"); // NOI18N
        FormInput.add(jLabel28);
        jLabel28.setBounds(420, 200, 40, 23);

        SkorKardio.setEditable(false);
        SkorKardio.setText("0");
        SkorKardio.setFocusTraversalPolicyProvider(true);
        SkorKardio.setName("SkorKardio"); // NOI18N
        SkorKardio.setOpaque(true);
        FormInput.add(SkorKardio);
        SkorKardio.setBounds(464, 200, 44, 23);

        // 3. NEUROLOGI
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel30.setText("3. NEUROLOGI");
        jLabel30.setName("jLabel30"); // NOI18N
        FormInput.add(jLabel30);
        jLabel30.setBounds(0, 230, 100, 23);

        jLabel31.setText("GCS :");
        jLabel31.setName("jLabel31"); // NOI18N
        FormInput.add(jLabel31);
        jLabel31.setBounds(0, 260, 50, 23);

        txtGCS.setName("txtGCS"); // NOI18N
        txtGCS.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtGCSKeyReleased(evt);
            }
        });
        FormInput.add(txtGCS);
        txtGCS.setBounds(54, 260, 80, 23);

        jLabel32.setText("Skor :");
        jLabel32.setName("jLabel32"); // NOI18N
        FormInput.add(jLabel32);
        jLabel32.setBounds(150, 260, 40, 23);

        SkorNeuro.setEditable(false);
        SkorNeuro.setText("0");
        SkorNeuro.setFocusTraversalPolicyProvider(true);
        SkorNeuro.setName("SkorNeuro"); // NOI18N
        SkorNeuro.setOpaque(true);
        FormInput.add(SkorNeuro);
        SkorNeuro.setBounds(194, 260, 44, 23);

        // 4. HEMATOLOGI
        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel33.setText("4. HEMATOLOGI");
        jLabel33.setName("jLabel33"); // NOI18N
        FormInput.add(jLabel33);
        jLabel33.setBounds(0, 290, 100, 23);

        jLabel34.setText("Platelet :");
        jLabel34.setName("jLabel34"); // NOI18N
        FormInput.add(jLabel34);
        jLabel34.setBounds(0, 320, 60, 23);

        txtPlatelet.setName("txtPlatelet"); // NOI18N
        txtPlatelet.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPlateletKeyReleased(evt);
            }
        });
        FormInput.add(txtPlatelet);
        txtPlatelet.setBounds(64, 320, 100, 23);

        jLabel35.setText("Skor :");
        jLabel35.setName("jLabel35"); // NOI18N
        FormInput.add(jLabel35);
        jLabel35.setBounds(170, 320, 40, 23);

        SkorHemato.setEditable(false);
        SkorHemato.setText("0");
        SkorHemato.setFocusTraversalPolicyProvider(true);
        SkorHemato.setName("SkorHemato"); // NOI18N
        SkorHemato.setOpaque(true);
        FormInput.add(SkorHemato);
        SkorHemato.setBounds(214, 320, 44, 23);

        // 5. RENAL
        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel36.setText("5. RENAL");
        jLabel36.setName("jLabel36"); // NOI18N
        FormInput.add(jLabel36);
        jLabel36.setBounds(0, 350, 100, 23);

        jLabel37.setText("Kreatinin :");
        jLabel37.setName("jLabel37"); // NOI18N
        FormInput.add(jLabel37);
        jLabel37.setBounds(0, 380, 70, 23);

        txtKreatinin.setName("txtKreatinin"); // NOI18N
        txtKreatinin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtKreatininKeyReleased(evt);
            }
        });
        FormInput.add(txtKreatinin);
        txtKreatinin.setBounds(74, 380, 100, 23);

        jLabel38.setText("Urin(mL/24h) :");
        jLabel38.setName("jLabel38"); // NOI18N
        FormInput.add(jLabel38);
        jLabel38.setBounds(180, 380, 90, 23);

        txtUrinOutput.setName("txtUrinOutput"); // NOI18N
        txtUrinOutput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtUrinOutputKeyReleased(evt);
            }
        });
        FormInput.add(txtUrinOutput);
        txtUrinOutput.setBounds(274, 380, 100, 23);

        jLabel39.setText("Skor :");
        jLabel39.setName("jLabel39"); // NOI18N
        FormInput.add(jLabel39);
        jLabel39.setBounds(380, 380, 40, 23);

        SkorRenal.setEditable(false);
        SkorRenal.setText("0");
        SkorRenal.setFocusTraversalPolicyProvider(true);
        SkorRenal.setName("SkorRenal"); // NOI18N
        SkorRenal.setOpaque(true);
        FormInput.add(SkorRenal);
        SkorRenal.setBounds(424, 380, 44, 23);

        // 6. HEPATIK
        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel40.setText("6. HEPATIK");
        jLabel40.setName("jLabel40"); // NOI18N
        FormInput.add(jLabel40);
        jLabel40.setBounds(0, 410, 100, 23);

        jLabel41.setText("Bilirubin :");
        jLabel41.setName("jLabel41"); // NOI18N
        FormInput.add(jLabel41);
        jLabel41.setBounds(0, 440, 70, 23);

        txtBilirubin.setName("txtBilirubin"); // NOI18N
        txtBilirubin.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBilirubinKeyReleased(evt);
            }
        });
        FormInput.add(txtBilirubin);
        txtBilirubin.setBounds(74, 440, 100, 23);

        jLabel42.setText("Skor :");
        jLabel42.setName("jLabel42"); // NOI18N
        FormInput.add(jLabel42);
        jLabel42.setBounds(180, 440, 40, 23);

        SkorHepatik.setEditable(false);
        SkorHepatik.setText("0");
        SkorHepatik.setFocusTraversalPolicyProvider(true);
        SkorHepatik.setName("SkorHepatik"); // NOI18N
        SkorHepatik.setOpaque(true);
        FormInput.add(SkorHepatik);
        SkorHepatik.setBounds(224, 440, 44, 23);

        // TOTAL SOFA
        jLabel43.setText("Total SOFA :");
        jLabel43.setName("jLabel43"); // NOI18N
        FormInput.add(jLabel43);
        jLabel43.setBounds(0, 470, 80, 23);

        TotalSOFA.setEditable(false);
        TotalSOFA.setText("0");
        TotalSOFA.setFocusTraversalPolicyProvider(true);
        TotalSOFA.setName("TotalSOFA"); // NOI18N
        FormInput.add(TotalSOFA);
        TotalSOFA.setBounds(84, 470, 60, 23);

        jLabel44.setText("Delta SOFA :");
        jLabel44.setName("jLabel44"); // NOI18N
        FormInput.add(jLabel44);
        jLabel44.setBounds(150, 470, 80, 23);

        DeltaSOFA.setEditable(false);
        DeltaSOFA.setText("0");
        DeltaSOFA.setFocusTraversalPolicyProvider(true);
        DeltaSOFA.setName("DeltaSOFA"); // NOI18N
        FormInput.add(DeltaSOFA);
        DeltaSOFA.setBounds(234, 470, 60, 23);

        jLabel45.setText("Kategori Risiko :");
        jLabel45.setName("jLabel45"); // NOI18N
        FormInput.add(jLabel45);
        jLabel45.setBounds(300, 470, 90, 23);

        KategoriRisiko.setEditable(false);
        KategoriRisiko.setFocusTraversalPolicyProvider(true);
        KategoriRisiko.setName("KategoriRisiko"); // NOI18N
        FormInput.add(KategoriRisiko);
        KategoriRisiko.setBounds(394, 470, 100, 23);

        jLabel46.setText("Mortalitas :");
        jLabel46.setName("jLabel46"); // NOI18N
        FormInput.add(jLabel46);
        jLabel46.setBounds(500, 470, 70, 23);

        EstimasiMortalitas.setEditable(false);
        EstimasiMortalitas.setFocusTraversalPolicyProvider(true);
        EstimasiMortalitas.setName("EstimasiMortalitas"); // NOI18N
        FormInput.add(EstimasiMortalitas);
        EstimasiMortalitas.setBounds(574, 470, 100, 23);

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

        calculateSOFA();

        // Di BtnSimpanActionPerformed, ganti array parameter menjadi:
        if (Sequel.menyimpantf("sofa_score", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 24, new String[]{
            TNoRw.getText(),
            Valid.SetTgl(Tanggal.getSelectedItem() + "") + " " + Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
            SkorRespirasi.getText(), SkorKardio.getText(), SkorNeuro.getText(),
            SkorHemato.getText(), SkorRenal.getText(), SkorHepatik.getText(),
            TotalSOFA.getText(), DeltaSOFA.getText(), KategoriRisiko.getText(),
            EstimasiMortalitas.getText(),
            txtPaO2.getText().trim().isEmpty() ? "0" : txtPaO2.getText(),
            txtFiO2.getText().trim().isEmpty() ? "0" : txtFiO2.getText(),
            cmbVentilator.getSelectedItem().toString(),
            txtMAP.getText().trim().isEmpty() ? "0" : txtMAP.getText(),
            cmbVasopresor.getSelectedItem().toString(),
            txtGCS.getText().trim().isEmpty() ? "0" : txtGCS.getText(),
            txtPlatelet.getText().trim().isEmpty() ? "0" : txtPlatelet.getText(),
            txtKreatinin.getText().trim().isEmpty() ? "0" : txtKreatinin.getText(),
            txtUrinOutput.getText().trim().isEmpty() ? "0" : txtUrinOutput.getText(),
            txtBilirubin.getText().trim().isEmpty() ? "0" : txtBilirubin.getText(),
            txtSOFASebelum.getText().trim().isEmpty() ? "0" : txtSOFASebelum.getText(),
            KdPetugas.getText()
        }) == true) {
            emptTeks();
            tampil();
        }
    }

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {
        ChkInput.setSelected(true);
        isForm();
        emptTeks();
    }

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {
        if (tbObat.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from sofa_score where no_rawat=? and tanggal=?", 2, new String[]{
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
            calculateSOFA();

            if (Sequel.mengedittf("sofa_score", "no_rawat=? and tanggal=?",
                    "no_rawat=?,tanggal=?,skor_respirasi=?,skor_kardio=?,skor_neuro=?,skor_hemato=?,skor_renal=?,skor_hepatik=?,"
                    + "total_sofa=?,delta_sofa=?,kategori_risiko=?,estimasi_mortalitas=?,pao2=?,fio2=?,ventilator=?,map=?,"
                    + "vasopresor=?,gcs=?,platelet=?,kreatinin=?,urin_output=?,bilirubin=?,sofa_sebelum=?,nip=?", 26, new String[]{
                        TNoRw.getText(),
                        Valid.SetTgl(Tanggal.getSelectedItem() + "") + " " + Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                        SkorRespirasi.getText(), SkorKardio.getText(), SkorNeuro.getText(),
                        SkorHemato.getText(), SkorRenal.getText(), SkorHepatik.getText(),
                        TotalSOFA.getText(), DeltaSOFA.getText(), KategoriRisiko.getText(),
                        EstimasiMortalitas.getText(),
                        txtPaO2.getText().trim().isEmpty() ? "0" : txtPaO2.getText(),
                        txtFiO2.getText().trim().isEmpty() ? "0" : txtFiO2.getText(),
                        cmbVentilator.getSelectedItem().toString(),
                        txtMAP.getText().trim().isEmpty() ? "0" : txtMAP.getText(),
                        cmbVasopresor.getSelectedItem().toString(),
                        txtGCS.getText().trim().isEmpty() ? "0" : txtGCS.getText(),
                        txtPlatelet.getText().trim().isEmpty() ? "0" : txtPlatelet.getText(),
                        txtKreatinin.getText().trim().isEmpty() ? "0" : txtKreatinin.getText(),
                        txtUrinOutput.getText().trim().isEmpty() ? "0" : txtUrinOutput.getText(),
                        txtBilirubin.getText().trim().isEmpty() ? "0" : txtBilirubin.getText(),
                        txtSOFASebelum.getText().trim().isEmpty() ? "0" : txtSOFASebelum.getText(),
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

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Valid.MyReportqry("rpsofascore.jasper", "report", "::[ Laporan SOFA Score ]::",
                    "select p.no_rawat,p.no_rkm_medis,ps.nm_pasien,concat(concat(concat(umurdaftar,' '),sttsumur),' / ',if(ps.jk='L','Laki-laki','Perempuan')) as umur,"
                    + "ps.jk,ss.tanggal,ss.skor_respirasi,ss.skor_kardio,ss.skor_neuro,ss.skor_hemato,ss.skor_renal,ss.skor_hepatik,"
                    + "ss.total_sofa,ss.delta_sofa,ss.kategori_risiko,ss.estimasi_mortalitas,ss.nip,pg.nama,ps.tgl_lahir "
                    + "from reg_periksa p inner join pasien ps on p.no_rkm_medis=ps.no_rkm_medis "
                    + "inner join sofa_score ss on p.no_rawat=ss.no_rawat "
                    + "inner join pegawai pg on ss.nip=pg.nik "
                    + "where ss.tanggal between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59' "
                    + "order by ss.tanggal", null);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {
        petugas.dispose();
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
        akses.setform("RMSOFAScore");
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
            Valid.pindah(evt, KdPetugas, txtSOFASebelum);
        }
    }

    private void txtSOFASebelumKeyReleased(java.awt.event.KeyEvent evt) {
        calculateSOFA();
    }

    private void txtPaO2KeyReleased(java.awt.event.KeyEvent evt) {
        calculateSOFA();
    }

    private void txtFiO2KeyReleased(java.awt.event.KeyEvent evt) {
        calculateSOFA();
    }

    private void cmbVentilatorItemStateChanged(java.awt.event.ItemEvent evt) {
        calculateSOFA();
    }

    private void txtMAPKeyReleased(java.awt.event.KeyEvent evt) {
        calculateSOFA();
    }

    private void cmbVasopresorItemStateChanged(java.awt.event.ItemEvent evt) {
        calculateSOFA();
    }

    private void txtGCSKeyReleased(java.awt.event.KeyEvent evt) {
        calculateSOFA();
    }

    private void txtPlateletKeyReleased(java.awt.event.KeyEvent evt) {
        calculateSOFA();
    }

    private void txtKreatininKeyReleased(java.awt.event.KeyEvent evt) {
        calculateSOFA();
    }

    private void txtUrinOutputKeyReleased(java.awt.event.KeyEvent evt) {
        calculateSOFA();
    }

    private void txtBilirubinKeyReleased(java.awt.event.KeyEvent evt) {
        calculateSOFA();
    }

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, EstimasiMortalitas, BtnBatal);
        }
    }

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            emptTeks();
        } else {
            Valid.pindah(evt, BtnSimpan, BtnHapus);
        }
    }

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnHapusActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
    }

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnEditActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
    }

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPrintActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnEdit, BtnKeluar);
        }
    }

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnKeluarActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnPrint, TCari);
        }
    }

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnCariActionPerformed(null);
        } else {
            Valid.pindah(evt, TCari, BtnAll);
        }
    }

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnAllActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnCari, TPasien);
        }
    }

    private void MnCetakSOFAActionPerformed(java.awt.event.ActionEvent evt) {
        if (tbObat.getSelectedRow() > -1) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Valid.MyReportqry("rpsofascoresingle.jasper", "report", "::[ Laporan SOFA Score Individual ]::",
                    "select p.no_rawat,p.no_rkm_medis,ps.nm_pasien,concat(concat(concat(umurdaftar,' '),sttsumur),' / ',if(ps.jk='L','Laki-laki','Perempuan')) as umur,"
                    + "ps.jk,ss.tanggal,ss.skor_respirasi,ss.skor_kardio,ss.skor_neuro,ss.skor_hemato,ss.skor_renal,ss.skor_hepatik,"
                    + "ss.total_sofa,ss.delta_sofa,ss.kategori_risiko,ss.estimasi_mortalitas,ss.nip,pg.nama,ps.tgl_lahir,"
                    + "ss.pao2,ss.fio2,ss.ventilator,ss.map,ss.vasopresor,ss.gcs,ss.platelet,ss.kreatinin,ss.urin_output,ss.bilirubin "
                    + "from reg_periksa p inner join pasien ps on p.no_rkm_medis=ps.no_rkm_medis "
                    + "inner join sofa_score ss on p.no_rawat=ss.no_rawat "
                    + "inner join pegawai pg on ss.nip=pg.nik "
                    + "where ss.no_rawat='" + tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "' "
                    + "and ss.tanggal='" + tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString() + "'", null);
            this.setCursor(Cursor.getDefaultCursor());
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu..!!");
        }
    }

    /**
     * Method untuk menghitung SOFA Score berdasarkan parameter organ
     * dysfunction
     */
    private void calculateSOFA() {
        try {
            // 1. RESPIRASI SCORE
            int skorRespirasi = calculateRespirationScore();
            SkorRespirasi.setText(String.valueOf(skorRespirasi));

            // 2. KARDIOVASKULAR SCORE
            int skorKardio = calculateCardiovascularScore();
            SkorKardio.setText(String.valueOf(skorKardio));

            // 3. NEUROLOGI SCORE
            int skorNeuro = calculateNeurologyScore();
            SkorNeuro.setText(String.valueOf(skorNeuro));

            // 4. HEMATOLOGI SCORE
            int skorHemato = calculateHematologyScore();
            SkorHemato.setText(String.valueOf(skorHemato));

            // 5. RENAL SCORE
            int skorRenal = calculateRenalScore();
            SkorRenal.setText(String.valueOf(skorRenal));

            // 6. HEPATIK SCORE
            int skorHepatik = calculateHepaticScore();
            SkorHepatik.setText(String.valueOf(skorHepatik));

            // TOTAL SOFA SCORE
            int totalSOFA = skorRespirasi + skorKardio + skorNeuro + skorHemato + skorRenal + skorHepatik;
            TotalSOFA.setText(String.valueOf(totalSOFA));

            // DELTA SOFA SCORE
            int sofaSebelum = 0;
            if (!txtSOFASebelum.getText().isEmpty()) {
                sofaSebelum = Integer.parseInt(txtSOFASebelum.getText());
            }
            int deltaSOFA = totalSOFA - sofaSebelum;
            DeltaSOFA.setText(String.valueOf(deltaSOFA));

            // KATEGORI RISIKO dan ESTIMASI MORTALITAS
            String[] risikoMortalitas = getRiskCategoryAndMortality(totalSOFA, deltaSOFA);
            KategoriRisiko.setText(risikoMortalitas[0]);
            EstimasiMortalitas.setText(risikoMortalitas[1]);

        } catch (NumberFormatException e) {
            // Handle jika ada input yang tidak valid
        }
    }

    /**
     * Menghitung skor respirasi berdasarkan PaO2/FiO2 ratio dan penggunaan
     * ventilator
     */
    private int calculateRespirationScore() {
        try {
            if (txtPaO2.getText().isEmpty() || txtFiO2.getText().isEmpty()) {
                return 0;
            }

            double paO2 = Double.parseDouble(txtPaO2.getText());
            double fiO2 = Double.parseDouble(txtFiO2.getText()) / 100.0; // Convert percentage to decimal
            double ratio = paO2 / fiO2;

            boolean onVentilator = cmbVentilator.getSelectedItem().toString().equals("Ya");

            if (ratio >= 400) {
                return 0;
            } else if (ratio >= 300) {
                return 1;
            } else if (ratio >= 200) {
                return 2;
            } else if (ratio >= 100) {
                return onVentilator ? 3 : 2;
            } else {
                return 4;
            }
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Menghitung skor kardiovaskular berdasarkan MAP dan penggunaan vasopresor
     */
    private int calculateCardiovascularScore() {
        try {
            String vasopresor = cmbVasopresor.getSelectedItem().toString();

            if (vasopresor.equals("Tidak ada")) {
                if (txtMAP.getText().isEmpty()) {
                    return 0;
                }
                double map = Double.parseDouble(txtMAP.getText());
                if (map >= 70) {
                    return 0;
                } else {
                    return 1;
                }
            } else if (vasopresor.equals("Dobutamin (any dose)")) {
                return 2;
            } else if (vasopresor.equals("Dopamin ≤ 5 μg/kg/min")
                    || vasopresor.equals("Epinefrin ≤ 0.1 μg/kg/min")
                    || vasopresor.equals("Norepinefrin ≤ 0.1 μg/kg/min")) {
                return 3;
            } else if (vasopresor.equals("Dopamin > 5 μg/kg/min")
                    || vasopresor.equals("Epinefrin > 0.1 μg/kg/min")
                    || vasopresor.equals("Norepinefrin > 0.1 μg/kg/min")) {
                return 4;
            } else if (vasopresor.equals("Dopamin > 15 μg/kg/min")) {
                return 4;
            }
            return 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Menghitung skor neurologi berdasarkan Glasgow Coma Scale (GCS)
     */
    private int calculateNeurologyScore() {
        try {
            if (txtGCS.getText().isEmpty()) {
                return 0;
            }

            int gcs = Integer.parseInt(txtGCS.getText());

            if (gcs == 15) {
                return 0;
            } else if (gcs >= 13) {
                return 1;
            } else if (gcs >= 10) {
                return 2;
            } else if (gcs >= 6) {
                return 3;
            } else {
                return 4;
            }
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Menghitung skor hematologi berdasarkan platelet count
     */
    private int calculateHematologyScore() {
        try {
            if (txtPlatelet.getText().isEmpty()) {
                return 0;
            }

            double platelet = Double.parseDouble(txtPlatelet.getText());

            if (platelet >= 150) {
                return 0;
            } else if (platelet >= 100) {
                return 1;
            } else if (platelet >= 50) {
                return 2;
            } else if (platelet >= 20) {
                return 3;
            } else {
                return 4;
            }
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Menghitung skor renal berdasarkan kreatinin dan urin output
     */
    private int calculateRenalScore() {
        try {
            double kreatinin = 0;
            double urinOutput = 0;

            if (!txtKreatinin.getText().isEmpty()) {
                kreatinin = Double.parseDouble(txtKreatinin.getText());
            }
            if (!txtUrinOutput.getText().isEmpty()) {
                urinOutput = Double.parseDouble(txtUrinOutput.getText());
            }

            // Kreatinin dalam mg/dL
            if (kreatinin < 1.2 && urinOutput >= 500) {
                return 0;
            } else if (kreatinin >= 1.2 && kreatinin < 2.0) {
                return 1;
            } else if (kreatinin >= 2.0 && kreatinin < 3.5) {
                return 2;
            } else if (kreatinin >= 3.5 && kreatinin < 5.0 || urinOutput < 500) {
                return 3;
            } else if (kreatinin >= 5.0 || urinOutput < 200) {
                return 4;
            }
            return 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Menghitung skor hepatik berdasarkan bilirubin level
     */
    private int calculateHepaticScore() {
        try {
            if (txtBilirubin.getText().isEmpty()) {
                return 0;
            }

            double bilirubin = Double.parseDouble(txtBilirubin.getText());

            // Bilirubin dalam mg/dL
            if (bilirubin < 1.2) {
                return 0;
            } else if (bilirubin >= 1.2 && bilirubin < 2.0) {
                return 1;
            } else if (bilirubin >= 2.0 && bilirubin < 6.0) {
                return 2;
            } else if (bilirubin >= 6.0 && bilirubin < 12.0) {
                return 3;
            } else {
                return 4;
            }
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Menentukan kategori risiko dan estimasi mortalitas berdasarkan total dan
     * delta SOFA
     */
    private String[] getRiskCategoryAndMortality(int totalSOFA, int deltaSOFA) {
        String kategori;
        String mortalitas;

        // Berdasarkan total SOFA score
        if (totalSOFA <= 6) {
            kategori = "Risiko Rendah";
            mortalitas = "< 10%";
        } else if (totalSOFA <= 9) {
            kategori = "Risiko Sedang";
            mortalitas = "15-20%";
        } else if (totalSOFA <= 12) {
            kategori = "Risiko Tinggi";
            mortalitas = "40-50%";
        } else if (totalSOFA <= 14) {
            kategori = "Risiko Sangat Tinggi";
            mortalitas = "50-60%";
        } else {
            kategori = "Risiko Kritis";
            mortalitas = "> 80%";
        }

        // Modifikasi berdasarkan delta SOFA
        if (deltaSOFA >= 2) {
            if (kategori.equals("Risiko Rendah")) {
                kategori = "Risiko Sedang";
                mortalitas = "15-25%";
            } else if (kategori.equals("Risiko Sedang")) {
                kategori = "Risiko Tinggi";
                mortalitas = "30-40%";
            }
        }

        return new String[]{kategori, mortalitas};
    }

    /**
     * Method untuk mengosongkan input fields
     */
    public void emptTeks() {
        TNoRw.setText("");
        TNoRM.setText("");
        TPasien.setText("");
        TglLahir.setText("");
        KdPetugas.setText("");
        NmPetugas.setText("");

        txtSOFASebelum.setText("0");
        txtPaO2.setText("");
        txtFiO2.setText("");
        cmbVentilator.setSelectedIndex(0);
        txtMAP.setText("");
        cmbVasopresor.setSelectedIndex(0);
        txtGCS.setText("");
        txtPlatelet.setText("");
        txtKreatinin.setText("");
        txtUrinOutput.setText("");
        txtBilirubin.setText("");

        SkorRespirasi.setText("0");
        SkorKardio.setText("0");
        SkorNeuro.setText("0");
        SkorHemato.setText("0");
        SkorRenal.setText("0");
        SkorHepatik.setText("0");
        TotalSOFA.setText("0");
        DeltaSOFA.setText("0");
        KategoriRisiko.setText("");
        EstimasiMortalitas.setText("");

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
                    column.setPreferredWidth(60);
                    break;  // Respirasi
                case 7:
                    column.setPreferredWidth(50);
                    break;  // Kardio
                case 8:
                    column.setPreferredWidth(50);
                    break;  // Neuro
                case 9:
                    column.setPreferredWidth(60);
                    break;  // Hemato
                case 10:
                    column.setPreferredWidth(45);
                    break; // Renal
                case 11:
                    column.setPreferredWidth(55);
                    break; // Hepatik
                case 12:
                    column.setPreferredWidth(70);
                    break; // Total SOFA
                case 13:
                    column.setPreferredWidth(70);
                    break; // Delta SOFA
                case 14:
                    column.setPreferredWidth(100);
                    break;// Kategori Risiko
                case 15:
                    column.setPreferredWidth(70);
                    break; // Mortalitas
                case 16:
                    column.setPreferredWidth(80);
                    break; // NIP
                case 17:
                    column.setPreferredWidth(150);
                    break;// Petugas
                case 18:
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
                        + "ps.jk,ss.tanggal,ss.skor_respirasi,ss.skor_kardio,ss.skor_neuro,ss.skor_hemato,ss.skor_renal,ss.skor_hepatik,"
                        + "ss.total_sofa,ss.delta_sofa,ss.kategori_risiko,ss.estimasi_mortalitas,ss.nip,pg.nama,ps.tgl_lahir "
                        + "from reg_periksa p inner join pasien ps on p.no_rkm_medis=ps.no_rkm_medis "
                        + "inner join sofa_score ss on p.no_rawat=ss.no_rawat "
                        + "inner join pegawai pg on ss.nip=pg.nik "
                        + "where ss.tanggal between ? and ? order by ss.tanggal desc");
            } else {
                ps = koneksi.prepareStatement(
                        "select p.no_rawat,p.no_rkm_medis,ps.nm_pasien,concat(concat(concat(umurdaftar,' '),sttsumur),' / ',if(ps.jk='L','Laki-laki','Perempuan')) as umur,"
                        + "ps.jk,ss.tanggal,ss.skor_respirasi,ss.skor_kardio,ss.skor_neuro,ss.skor_hemato,ss.skor_renal,ss.skor_hepatik,"
                        + "ss.total_sofa,ss.delta_sofa,ss.kategori_risiko,ss.estimasi_mortalitas,ss.nip,pg.nama,ps.tgl_lahir "
                        + "from reg_periksa p inner join pasien ps on p.no_rkm_medis=ps.no_rkm_medis "
                        + "inner join sofa_score ss on p.no_rawat=ss.no_rawat "
                        + "inner join pegawai pg on ss.nip=pg.nik "
                        + "where ss.tanggal between ? and ? and (p.no_rawat like ? or ps.nm_pasien like ? or pg.nama like ?) "
                        + "order by ss.tanggal desc");
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
                        rs.getString("skor_respirasi"),
                        rs.getString("skor_kardio"),
                        rs.getString("skor_neuro"),
                        rs.getString("skor_hemato"),
                        rs.getString("skor_renal"),
                        rs.getString("skor_hepatik"),
                        rs.getString("total_sofa"),
                        rs.getString("delta_sofa"),
                        rs.getString("kategori_risiko"),
                        rs.getString("estimasi_mortalitas"),
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
            TglLahir.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 18).toString());

            Valid.SetTgl2(Tanggal, tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());

            // Get detailed data from database
            try {
                ps = koneksi.prepareStatement(
                        "select * from sofa_score where no_rawat=? and tanggal=?");
                ps.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                ps.setString(2, tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    txtPaO2.setText(rs.getString("pao2"));
                    txtFiO2.setText(rs.getString("fio2"));
                    cmbVentilator.setSelectedItem(rs.getString("ventilator"));
                    txtMAP.setText(rs.getString("map"));
                    cmbVasopresor.setSelectedItem(rs.getString("vasopresor"));
                    txtGCS.setText(rs.getString("gcs"));
                    txtPlatelet.setText(rs.getString("platelet"));
                    txtKreatinin.setText(rs.getString("kreatinin"));
                    txtUrinOutput.setText(rs.getString("urin_output"));
                    txtBilirubin.setText(rs.getString("bilirubin"));
                    txtSOFASebelum.setText(rs.getString("sofa_sebelum"));
                    KdPetugas.setText(rs.getString("nip"));
                    NmPetugas.setText(petugas.tampil3(rs.getString("nip")));

                    calculateSOFA(); // Recalculate to ensure consistency
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
            PanelInput.setPreferredSize(new Dimension(WIDTH, 600));
            FormInput.setVisible(true);
            ChkInput.setVisible(true);
        } else if (ChkInput.isSelected() == false) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 20));
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
            RMSOFAScore dialog = new RMSOFAScore(new javax.swing.JFrame(), true);
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
    private widget.TextBox DeltaSOFA;
    private widget.ComboBox Detik;
    private widget.TextBox EstimasiMortalitas;
    private widget.PanelBiasa FormInput;
    private widget.TextBox JK;
    private widget.ComboBox Jam;
    private widget.TextBox KategoriRisiko;
    private widget.TextBox KdPetugas;
    private widget.Label LCount;
    private widget.ComboBox Menit;
    private javax.swing.JMenuItem MnCetakSOFA;
    private widget.TextBox NmPetugas;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox SkorHepatik;
    private widget.TextBox SkorHemato;
    private widget.TextBox SkorKardio;
    private widget.TextBox SkorNeuro;
    private widget.TextBox SkorRenal;
    private widget.TextBox SkorRespirasi;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Tanggal Tanggal;
    private widget.TextBox TanggalRegistrasi;
    private widget.TextBox TglLahir;
    private widget.TextBox TotalSOFA;
    private widget.TextBox Umur;
    private widget.Button btnPetugas;
    private widget.ComboBox cmbVasopresor;
    private widget.ComboBox cmbVentilator;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
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
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbObat;
    private widget.TextBox txtBilirubin;
    private widget.TextBox txtFiO2;
    private widget.TextBox txtGCS;
    private widget.TextBox txtKreatinin;
    private widget.TextBox txtMAP;
    private widget.TextBox txtPaO2;
    private widget.TextBox txtPlatelet;
    private widget.TextBox txtSOFASebelum;
    private widget.TextBox txtUrinOutput;
    private widget.ScrollPane ScrollFormInput;
    // End of variables declaration
}
