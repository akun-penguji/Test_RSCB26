package rekammedis;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariPetugas;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public final class RMScoringVBAC extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);

    public RMScoringVBAC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(880, 674);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.R.M.", "Nama Pasien", "Tgl.Pengkajian", "Jam", "Usia < 40Th", "Riw.Pervaginam", "Alasan SC Bukan CPD", "Pendataran Serviks", "Dilatasi Serviks", "Total Skor", "Interpretasi", "NIP", "Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 14; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(105);
            } else if (i == 1) {
                column.setPreferredWidth(65);
            } else if (i == 2) {
                column.setPreferredWidth(160);
            } else if (i == 3) {
                column.setPreferredWidth(80);
            } else if (i == 4) {
                column.setPreferredWidth(60);
            } else if (i == 5) {
                column.setPreferredWidth(80);
            } else if (i == 6) {
                column.setPreferredWidth(100);
            } else if (i == 7) {
                column.setPreferredWidth(120);
            } else if (i == 8) {
                column.setPreferredWidth(110);
            } else if (i == 9) {
                column.setPreferredWidth(100);
            } else if (i == 10) {
                column.setPreferredWidth(60);
            } else if (i == 11) {
                column.setPreferredWidth(150);
            } else if (i == 12) {
                column.setPreferredWidth(90);
            } else if (i == 13) {
                column.setPreferredWidth(160);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        // Note: batasInput not applied because widget.TextBox doesn't support setDocument directly in this context without casting properly or using specialized textboxes.

        petugas.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if (petugas.getTable().getSelectedRow() != -1) {
                    KdPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                    NmPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                    BtnPetugas.requestFocus();
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

        ChkInput.setSelected(true);
        isForm();
        jam();
        
        // Listeners for auto calculation
        ActionListener calcListener = (ActionEvent e) -> {
            hitungSkor();
        };
        Usia40.addActionListener(calcListener);
        RiwayatPervaginam.addActionListener(calcListener);
        AlasanSC.addActionListener(calcListener);
        PendarahanServiks.addActionListener(calcListener);
        DilatasiServiks.addActionListener(calcListener);
    }

    // Manual init because I don't have NetBeans IDE to generate .form
    private void initComponents() {
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
        FormInput = new widget.PanelBiasa();
        jLabel4 = new widget.Label();
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        TglPengkajian = new widget.Tanggal();
        TNoRM = new widget.TextBox();
        jLabel16 = new widget.Label();
        Jam = new javax.swing.JComboBox<>();
        Menit = new javax.swing.JComboBox<>();
        Detik = new javax.swing.JComboBox<>();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        jLabel12 = new widget.Label();
        Usia40 = new javax.swing.JComboBox<>();
        jLabel23 = new widget.Label();
        RiwayatPervaginam = new javax.swing.JComboBox<>();
        jLabel28 = new widget.Label();
        AlasanSC = new javax.swing.JComboBox<>();
        jLabel29 = new widget.Label();
        PendarahanServiks = new javax.swing.JComboBox<>();
        jLabel30 = new widget.Label();
        DilatasiServiks = new javax.swing.JComboBox<>();
        jLabel31 = new widget.Label();
        TotalSkor = new widget.TextBox();
        jLabel32 = new widget.Label();
        Interpretasi = new widget.TextBox();
        label14 = new widget.Label();
        KdPetugas = new widget.TextBox();
        NmPetugas = new widget.TextBox();
        BtnPetugas = new widget.Button();
        ChkInput = new widget.CekBox();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnCetakSkoringVBAC = new javax.swing.JMenuItem();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnCetakSkoringVBAC.setBackground(new java.awt.Color(255, 255, 254));
        MnCetakSkoringVBAC.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnCetakSkoringVBAC.setForeground(new java.awt.Color(50, 50, 50));
        MnCetakSkoringVBAC.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnCetakSkoringVBAC.setText("Formulir Skoring VBAC Flam Geiger");
        MnCetakSkoringVBAC.setName("MnCetakSkoringVBAC"); // NOI18N
        MnCetakSkoringVBAC.setPreferredSize(new java.awt.Dimension(260, 26));
        MnCetakSkoringVBAC.addActionListener(e -> MnCetakSkoringVBACActionPerformed(e));
        jPopupMenu1.add(MnCetakSkoringVBAC);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Skoring VBAC Flam Geiger ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));
        internalFrame1.setName("internalFrame1");
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll");
        tbObat.setName("tbObat");
        tbObat.setComponentPopupMenu(jPopupMenu1);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
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
        BtnSimpan.setText("Simpan");
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpan.addActionListener(e -> BtnSimpanActionPerformed(e));
        panelGlass8.add(BtnSimpan);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); // NOI18N
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Baru");
        BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setName("BtnBatal"); // NOI18N
        BtnBatal.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBatal.addActionListener(e -> BtnBatalActionPerformed(e));
        panelGlass8.add(BtnBatal);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus"); // NOI18N
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapus.addActionListener(e -> BtnHapusActionPerformed(e));
        panelGlass8.add(BtnHapus);

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        BtnEdit.setMnemonic('G');
        BtnEdit.setText("Ganti");
        BtnEdit.setToolTipText("Alt+G");
        BtnEdit.setName("BtnEdit"); // NOI18N
        BtnEdit.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit.addActionListener(e -> BtnEditActionPerformed(e));
        panelGlass8.add(BtnEdit);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setName("BtnPrint"); // NOI18N
        BtnPrint.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnPrint.addActionListener(e -> BtnPrintActionPerformed(e));
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
        BtnKeluar.addActionListener(e -> BtnKeluarActionPerformed(e));
        panelGlass8.add(BtnKeluar);

        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9");
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel19.setText("Tanggal :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "25-02-2026" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "25-02-2026" }));
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
                if(evt.getKeyCode()==KeyEvent.VK_ENTER){
                    BtnCari.requestFocus();
                }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
                    BtnCari.requestFocus();
                }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
                    BtnKeluar.requestFocus();
                }
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('2');
        BtnCari.setToolTipText("Alt+2");
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(e -> tampil());
        panelGlass9.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll.addActionListener(e -> { TCari.setText(""); tampil(); });
        panelGlass9.add(BtnAll);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);
        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput");
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 185));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setBackground(new java.awt.Color(250, 255, 245));
        FormInput.setName("FormInput");
        FormInput.setLayout(null);

        jLabel4.setText("No.Rawat :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 10, 80, 23);

        TNoRw.setEditable(false);
        TNoRw.setName("TNoRw"); // NOI18N
        FormInput.add(TNoRw);
        TNoRw.setBounds(84, 10, 136, 23);

        TNoRM.setEditable(false);
        TNoRM.setName("TNoRM"); // NOI18N
        FormInput.add(TNoRM);
        TNoRM.setBounds(222, 10, 100, 23);

        TPasien.setEditable(false);
        TPasien.setName("TPasien"); // NOI18N
        FormInput.add(TPasien);
        TPasien.setBounds(324, 10, 250, 23);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(580, 10, 60, 23);

        TglLahir.setEditable(false);
        TglLahir.setName("TglLahir"); // NOI18N
        FormInput.add(TglLahir);
        TglLahir.setBounds(645, 10, 100, 23);

        jLabel16.setText("Tanggal :");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(0, 40, 80, 23);

        TglPengkajian.setForeground(new java.awt.Color(50, 70, 50));
        TglPengkajian.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "25-02-2026" }));
        TglPengkajian.setDisplayFormat("dd-MM-yyyy");
        TglPengkajian.setName("TglPengkajian"); // NOI18N
        TglPengkajian.setOpaque(false);
        FormInput.add(TglPengkajian);
        TglPengkajian.setBounds(84, 40, 90, 23);

        String[] jamStrings = new String[24];
        for(int i=0; i<24; i++) jamStrings[i] = String.format("%02d", i);
        Jam.setModel(new javax.swing.DefaultComboBoxModel<>(jamStrings));
        Jam.setName("Jam"); // NOI18N
        FormInput.add(Jam);
        Jam.setBounds(178, 40, 50, 23);

        String[] menitStrings = new String[60];
        for(int i=0; i<60; i++) menitStrings[i] = String.format("%02d", i);
        Menit.setModel(new javax.swing.DefaultComboBoxModel<>(menitStrings));
        Menit.setName("Menit"); // NOI18N
        FormInput.add(Menit);
        Menit.setBounds(232, 40, 50, 23);

        Detik.setModel(new javax.swing.DefaultComboBoxModel<>(menitStrings));
        Detik.setName("Detik"); // NOI18N
        FormInput.add(Detik);
        Detik.setBounds(286, 40, 50, 23);

        label14.setText("Petugas :");
        label14.setName("label14"); // NOI18N
        FormInput.add(label14);
        label14.setBounds(350, 40, 60, 23);

        KdPetugas.setEditable(false);
        KdPetugas.setName("KdPetugas"); // NOI18N
        FormInput.add(KdPetugas);
        KdPetugas.setBounds(415, 40, 100, 23);

        NmPetugas.setEditable(false);
        NmPetugas.setName("NmPetugas"); // NOI18N
        FormInput.add(NmPetugas);
        NmPetugas.setBounds(517, 40, 180, 23);

        BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPetugas.setMnemonic('2');
        BtnPetugas.setToolTipText("Alt+2");
        BtnPetugas.setName("BtnPetugas"); // NOI18N
        BtnPetugas.addActionListener(e -> { petugas.isCek(); petugas.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20); petugas.setLocationRelativeTo(internalFrame1); petugas.setVisible(true); });
        FormInput.add(BtnPetugas);
        BtnPetugas.setBounds(700, 40, 28, 23);

        jLabel12.setText("Usia < 40 th :");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(0, 70, 80, 23);
        
        Usia40.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Ya (Skor 2)", "Tidak (Skor 0)"}));
        Usia40.setName("Usia40"); // NOI18N
        FormInput.add(Usia40);
        Usia40.setBounds(84, 70, 140, 23);

        jLabel23.setText("Riwayat Pervaginam :");
        jLabel23.setName("jLabel23"); // NOI18N
        FormInput.add(jLabel23);
        jLabel23.setBounds(230, 70, 130, 23);

        RiwayatPervaginam.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Sebelum dan sesudah SC (Skor 4)", "Setelah SC pertama (Skor 2)", "Sebelum SC pertama (Skor 1)", "Tidak pernah (Skor 0)"}));
        RiwayatPervaginam.setName("RiwayatPervaginam"); // NOI18N
        FormInput.add(RiwayatPervaginam);
        RiwayatPervaginam.setBounds(365, 70, 200, 23);

        jLabel28.setText("Alasan SC Bukan CPD :");
        jLabel28.setName("jLabel28"); // NOI18N
        FormInput.add(jLabel28);
        jLabel28.setBounds(570, 70, 130, 23);

        AlasanSC.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Ya (Skor 1)", "Tidak (Skor 0)"}));
        AlasanSC.setName("AlasanSC"); // NOI18N
        FormInput.add(AlasanSC);
        AlasanSC.setBounds(705, 70, 100, 23);

        jLabel29.setText("Pendataran Serviks :");
        jLabel29.setName("jLabel29"); // NOI18N
        FormInput.add(jLabel29);
        jLabel29.setBounds(0, 100, 105, 23);

        PendarahanServiks.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"75% (Skor 2)", "25-75% (Skor 1)", "<25% (Skor 0)"}));
        PendarahanServiks.setName("PendarahanServiks"); // NOI18N
        FormInput.add(PendarahanServiks);
        PendarahanServiks.setBounds(110, 100, 130, 23);

        jLabel30.setText("Dilatasi Serviks :");
        jLabel30.setName("jLabel30"); // NOI18N
        FormInput.add(jLabel30);
        jLabel30.setBounds(250, 100, 100, 23);

        DilatasiServiks.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Ya (>= 4 cm) (Skor 1)", "Tidak (< 4 cm) (Skor 0)"}));
        DilatasiServiks.setName("DilatasiServiks"); // NOI18N
        FormInput.add(DilatasiServiks);
        DilatasiServiks.setBounds(355, 100, 130, 23);

        jLabel31.setText("Total Skor :");
        jLabel31.setName("jLabel31"); // NOI18N
        FormInput.add(jLabel31);
        jLabel31.setBounds(500, 100, 70, 23);

        TotalSkor.setEditable(false);
        TotalSkor.setName("TotalSkor"); // NOI18N
        FormInput.add(TotalSkor);
        TotalSkor.setBounds(575, 100, 40, 23);

        jLabel32.setText("Interpretasi :");
        jLabel32.setName("jLabel32"); // NOI18N
        FormInput.add(jLabel32);
        jLabel32.setBounds(0, 130, 80, 23);

        Interpretasi.setEditable(false);
        Interpretasi.setName("Interpretasi"); // NOI18N
        FormInput.add(Interpretasi);
        Interpretasi.setBounds(84, 130, 400, 23);

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
        ChkInput.addActionListener(e -> isForm());
        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }

    private void hitungSkor() {
        int skor = 0;
        // Usia < 40
        if(Usia40.getSelectedIndex() == 0) skor += 2;
        
        // Riwayat Pervaginam
        if(RiwayatPervaginam.getSelectedIndex() == 0) skor += 4;
        else if(RiwayatPervaginam.getSelectedIndex() == 1) skor += 2;
        else if(RiwayatPervaginam.getSelectedIndex() == 2) skor += 1;
        
        // Alasan SC
        if(AlasanSC.getSelectedIndex() == 0) skor += 1;
        
        // Pendataran (User table used pendarahan_serviks name)
        if(PendarahanServiks.getSelectedIndex() == 0) skor += 2;
        else if(PendarahanServiks.getSelectedIndex() == 1) skor += 1;
        
        // Dilatasi
        if(DilatasiServiks.getSelectedIndex() == 0) skor += 1;
        
        TotalSkor.setText(String.valueOf(skor));
        
        if(skor >= 4) Interpretasi.setText("Baik");
        else Interpretasi.setText("Tidak baik");
    }

    private void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select skoring_vbac.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, skoring_vbac.tgl_pengkajian, skoring_vbac.jam_pengkajian, " +
                "skoring_vbac.usia_40_tahun, skoring_vbac.riwayat_pervaginam, skoring_vbac.alasan_sc_selain_cpd, " +
                "skoring_vbac.pendarahan_serviks, skoring_vbac.dilatasi_serviks, skoring_vbac.total_skor, " +
                "skoring_vbac.evaluasi_interpretasi, skoring_vbac.kd_petugas, petugas.nama " +
                "from skoring_vbac inner join reg_periksa on skoring_vbac.no_rawat=reg_periksa.no_rawat " +
                "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis " +
                "inner join petugas on skoring_vbac.kd_petugas=petugas.nip " +
                "where skoring_vbac.tgl_pengkajian between ? and ? and (skoring_vbac.no_rawat like ? or pasien.no_rkm_medis like ? or pasien.nm_pasien like ?) " +
                "order by skoring_vbac.tgl_pengkajian, skoring_vbac.jam_pengkajian"
            );
            try {
                ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem().toString()));
                ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem().toString()));
                ps.setString(3, "%" + TCari.getText() + "%");
                ps.setString(4, "%" + TCari.getText() + "%");
                ps.setString(5, "%" + TCari.getText() + "%");
            
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new Object[]{
                        rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getString(6), 
                        rs.getString(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getString(10),
                        rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14)
                    });
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            } finally {
                if (rs != null) try { rs.close(); } catch (SQLException e) {}
                if (ps != null) try { ps.close(); } catch (SQLException e) {}
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        LCount.setText("" + tabMode.getRowCount());
    }

    private void emptTeks() {
        TglPengkajian.setSelectedIndex(0);
        jam();
        Usia40.setSelectedIndex(0);
        RiwayatPervaginam.setSelectedIndex(2);
        AlasanSC.setSelectedIndex(0);
        PendarahanServiks.setSelectedIndex(0);
        DilatasiServiks.setSelectedIndex(0);
        TotalSkor.setText("0");
        Interpretasi.setText("");
        hitungSkor();
    }

    private void jam() {
        Calendar now = Calendar.getInstance();
        Jam.setSelectedItem(String.format("%02d", now.get(Calendar.HOUR_OF_DAY)));
        Menit.setSelectedItem(String.format("%02d", now.get(Calendar.MINUTE)));
        Detik.setSelectedItem(String.format("%02d", now.get(Calendar.SECOND)));
    }

    private void isForm() {
        if (ChkInput.isSelected() == true) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 185));
            FormInput.setVisible(true);      
            ChkInput.setVisible(true);
        } else if (ChkInput.isSelected() == false) {           
            ChkInput.setVisible(false);            
            PanelInput.setPreferredSize(new Dimension(WIDTH, 20));
            FormInput.setVisible(false);      
            ChkInput.setVisible(true);
        }
    }

    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        try {
            ps = koneksi.prepareStatement("select pasien.no_rkm_medis, pasien.nm_pasien, pasien.tgl_lahir from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis where reg_periksa.no_rawat=?");
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TNoRM.setText(rs.getString(1));
                    TPasien.setText(rs.getString(2));
                    TglLahir.setText(rs.getString(3));
                }
            } finally {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        DTPCari2.setDate(tgl2);
        ChkInput.setSelected(true);
        isForm();
    }

    private void BtnSimpanActionPerformed(ActionEvent evt) {
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "Pasien");
        } else if (KdPetugas.getText().trim().equals("") || NmPetugas.getText().trim().equals("")) {
            Valid.textKosong(KdPetugas, "Petugas");
        } else {
            if (Sequel.menyimpantf2("skoring_vbac", "?,?,?,?,?,?,?,?,?,?,?", 11, new String[]{
                TNoRw.getText(), Valid.SetTgl(TglPengkajian.getSelectedItem().toString()), 
                Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                Usia40.getSelectedIndex()==0?"Ya":"Tidak",
                RiwayatPervaginam.getSelectedIndex()==0?"Sebelum dan sesudah SC":RiwayatPervaginam.getSelectedIndex()==1?"Setelah SC pertama":RiwayatPervaginam.getSelectedIndex()==2?"Sebelum SC pertama":"Tidak pernah",
                AlasanSC.getSelectedIndex()==0?"Ya":"Tidak",
                PendarahanServiks.getSelectedIndex()==0?"75%":PendarahanServiks.getSelectedIndex()==1?"25-75%":"<25%",
                DilatasiServiks.getSelectedIndex()==0?"Ya":"Tidak",
                TotalSkor.getText(), Interpretasi.getText(), KdPetugas.getText()
            }) == true) {
                tampil();
                emptTeks();
            }
        }
    }

    private void BtnBatalActionPerformed(ActionEvent evt) {
        emptTeks();
    }

    private void BtnHapusActionPerformed(ActionEvent evt) {
        if (tbObat.getSelectedRow() != -1) {
            if (Sequel.queryu2tf("delete from skoring_vbac where no_rawat=? and tgl_pengkajian=? and jam_pengkajian=?", 3, new String[]{
                tabMode.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                tabMode.getValueAt(tbObat.getSelectedRow(), 3).toString(),
                tabMode.getValueAt(tbObat.getSelectedRow(), 4).toString()
            }) == true) {
                tampil();
            }
        }
    }

    private void BtnEditActionPerformed(ActionEvent evt) {
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "Pasien");
        } else if (KdPetugas.getText().trim().equals("") || NmPetugas.getText().trim().equals("")) {
            Valid.textKosong(KdPetugas, "Petugas");
        } else {
            if (Sequel.mengedittf("skoring_vbac", "no_rawat=? and tgl_pengkajian=? and jam_pengkajian=?", "no_rawat=?,tgl_pengkajian=?,jam_pengkajian=?,usia_40_tahun=?,riwayat_pervaginam=?,alasan_sc_selain_cpd=?,pendarahan_serviks=?,dilatasi_serviks=?,total_skor=?,evaluasi_interpretasi=?,kd_petugas=?", 14, new String[]{
                TNoRw.getText(), Valid.SetTgl(TglPengkajian.getSelectedItem().toString()), 
                Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                Usia40.getSelectedIndex()==0?"Ya":"Tidak",
                RiwayatPervaginam.getSelectedIndex()==0?"Sebelum dan sesudah SC":RiwayatPervaginam.getSelectedIndex()==1?"Setelah SC pertama":RiwayatPervaginam.getSelectedIndex()==2?"Sebelum SC pertama":"Tidak pernah",
                AlasanSC.getSelectedIndex()==0?"Ya":"Tidak",
                PendarahanServiks.getSelectedIndex()==0?"75%":PendarahanServiks.getSelectedIndex()==1?"25-75%":"<25%",
                DilatasiServiks.getSelectedIndex()==0?"Ya":"Tidak",
                TotalSkor.getText(), Interpretasi.getText(), KdPetugas.getText(),
                tabMode.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                tabMode.getValueAt(tbObat.getSelectedRow(), 3).toString(),
                tabMode.getValueAt(tbObat.getSelectedRow(), 4).toString()
            }) == true) {
                tampil();
                emptTeks();
            }
        }
    }

    private void BtnPrintActionPerformed(ActionEvent evt) {
        if(tabMode.getRowCount()==0){
            javax.swing.JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        }else if(tabMode.getRowCount()!=0){
            try{
                java.util.Map<String, Object> param = new java.util.HashMap<>();
                param.put("namars",akses.getnamars());
                param.put("alamatrs",akses.getalamatrs());
                param.put("kotars",akses.getkabupatenrs());
                param.put("propinsirs",akses.getpropinsirs());
                param.put("kontakrs",akses.getkontakrs());
                param.put("emailrs",akses.getemailrs()); 
                param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
                Valid.MyReportqry("rptFormulirSkoringVBAC.jasper","report","::[ Data Skoring VBAC Flam Geiger ]::",
                    "select skoring_vbac.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,reg_periksa.sttsumur,pasien.jk,pasien.tgl_lahir, "+
                    "skoring_vbac.tgl_pengkajian,skoring_vbac.jam_pengkajian,skoring_vbac.usia_40_tahun,skoring_vbac.riwayat_pervaginam, "+
                    "skoring_vbac.alasan_sc_selain_cpd,skoring_vbac.pendarahan_serviks,skoring_vbac.dilatasi_serviks,skoring_vbac.total_skor, "+
                    "skoring_vbac.evaluasi_interpretasi,skoring_vbac.kd_petugas,petugas.nama "+
                    "from skoring_vbac inner join reg_periksa on skoring_vbac.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join petugas on skoring_vbac.kd_petugas=petugas.nip where skoring_vbac.tgl_pengkajian between '"+Valid.SetTgl(DTPCari1.getSelectedItem()+"")+"' and '"+Valid.SetTgl(DTPCari2.getSelectedItem()+"")+"' order by skoring_vbac.tgl_pengkajian",param);
            }catch(Exception e){
                System.out.println(e);
            }
        }
    }

    private void MnCetakSkoringVBACActionPerformed(java.awt.event.ActionEvent evt) {
        if(tbObat.getSelectedRow()>-1){
            java.util.Map<String, Object> param = new java.util.HashMap<>();
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs()); 
            param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
            param.put("logo2", Sequel.cariGambar("select setting.logo2 from setting"));
            
            String dpjp=Sequel.cariIsi("select dokter.nm_dokter from dpjp_ranap inner join dokter on dpjp_ranap.kd_dokter=dokter.kd_dokter where dpjp_ranap.no_rawat=?",tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
            if(dpjp.equals("")){
                dpjp=Sequel.cariIsi("select dokter.nm_dokter from reg_periksa inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter where reg_periksa.no_rawat=?",tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
            }
            param.put("dpjp",dpjp);   
            param.put("finger","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+tabMode.getValueAt(tbObat.getSelectedRow(),13).toString()+"\nID "+(tabMode.getValueAt(tbObat.getSelectedRow(),12).toString().equals("")?tbObat.getValueAt(tbObat.getSelectedRow(),13).toString():tabMode.getValueAt(tbObat.getSelectedRow(),12).toString())+"\n"+Valid.SetTgl3(tabMode.getValueAt(tbObat.getSelectedRow(),3).toString())); 

            Valid.MyReportqry("rptFormulirSkoringVBAC.jasper","report","::[ Formulir Skoring VBAC Flam Geiger ]::",
                    "select skoring_vbac.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,reg_periksa.sttsumur,pasien.jk,pasien.tgl_lahir, "+
                    "skoring_vbac.tgl_pengkajian,skoring_vbac.jam_pengkajian,skoring_vbac.usia_40_tahun,skoring_vbac.riwayat_pervaginam, "+
                    "skoring_vbac.alasan_sc_selain_cpd,skoring_vbac.pendarahan_serviks,skoring_vbac.dilatasi_serviks,skoring_vbac.total_skor, "+
                    "skoring_vbac.evaluasi_interpretasi,skoring_vbac.kd_petugas,petugas.nama "+
                    "from skoring_vbac inner join reg_periksa on skoring_vbac.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "inner join petugas on skoring_vbac.kd_petugas=petugas.nip where skoring_vbac.no_rawat='"+tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()+"'",param);
        }
    }

    private void BtnKeluarActionPerformed(ActionEvent evt) {
        dispose();
    }

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {                                    
        if(tabMode.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }                                   

    private void getData() {
        if(tbObat.getSelectedRow()!= -1){
            TNoRw.setText(tabMode.getValueAt(tbObat.getSelectedRow(),0).toString());
            TNoRM.setText(tabMode.getValueAt(tbObat.getSelectedRow(),1).toString());
            TPasien.setText(tabMode.getValueAt(tbObat.getSelectedRow(),2).toString());
            
            Valid.SetTgl(TglPengkajian,tabMode.getValueAt(tbObat.getSelectedRow(),3).toString());
            Jam.setSelectedItem(tabMode.getValueAt(tbObat.getSelectedRow(),4).toString().substring(0,2));
            Menit.setSelectedItem(tabMode.getValueAt(tbObat.getSelectedRow(),4).toString().substring(3,5));
            Detik.setSelectedItem(tabMode.getValueAt(tbObat.getSelectedRow(),4).toString().substring(6,8));
            
            Usia40.setSelectedIndex(tabMode.getValueAt(tbObat.getSelectedRow(),5).toString().equals("Ya")?0:1);
            RiwayatPervaginam.setSelectedIndex(tabMode.getValueAt(tbObat.getSelectedRow(),6).toString().equals("Sebelum dan sesudah SC")?0:tabMode.getValueAt(tbObat.getSelectedRow(),6).toString().equals("Setelah SC pertama")?1:tabMode.getValueAt(tbObat.getSelectedRow(),6).toString().equals("Sebelum SC pertama")?2:3);
            AlasanSC.setSelectedIndex(tabMode.getValueAt(tbObat.getSelectedRow(),7).toString().equals("Ya")?0:1);
            PendarahanServiks.setSelectedIndex(tabMode.getValueAt(tbObat.getSelectedRow(),8).toString().equals("75%")?0:tabMode.getValueAt(tbObat.getSelectedRow(),8).toString().equals("25-75%")?1:2);
            DilatasiServiks.setSelectedIndex(tabMode.getValueAt(tbObat.getSelectedRow(),9).toString().equals("Ya")?0:1);
            
            TotalSkor.setText(tabMode.getValueAt(tbObat.getSelectedRow(),10).toString());
            Interpretasi.setText(tabMode.getValueAt(tbObat.getSelectedRow(),11).toString());
            KdPetugas.setText(tabMode.getValueAt(tbObat.getSelectedRow(),12).toString());
            NmPetugas.setText(tabMode.getValueAt(tbObat.getSelectedRow(),13).toString());
        }
    }

    // UI components declaration
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.Button BtnPetugas;
    private widget.CekBox ChkInput;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.PanelBiasa FormInput;
    private javax.swing.JComboBox<String> Jam;
    private javax.swing.JComboBox<String> Menit;
    private javax.swing.JComboBox<String> Detik;
    private widget.TextBox KdPetugas;
    private widget.Label LCount;
    private widget.TextBox NmPetugas;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.TextBox TglLahir;
    private widget.Tanggal TglPengkajian;
    private widget.InternalFrame internalFrame1;
    private javax.swing.JPanel jPanel3;
    private widget.Label jLabel16;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel4;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label label14;
    private widget.Table tbObat;
    private javax.swing.JComboBox<String> Usia40;
    private javax.swing.JComboBox<String> RiwayatPervaginam;
    private javax.swing.JComboBox<String> AlasanSC;
    private javax.swing.JComboBox<String> PendarahanServiks;
    private javax.swing.JComboBox<String> DilatasiServiks;
    private widget.TextBox TotalSkor;
    private widget.TextBox Interpretasi;
    private widget.Label jLabel12;
    private widget.Label jLabel23;
    private widget.Label jLabel28;
    private widget.Label jLabel29;
    private widget.Label jLabel30;
    private widget.Label jLabel31;
    private widget.Label jLabel32;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JMenuItem MnCetakSkoringVBAC;
}
