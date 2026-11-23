package rekammedis;

import fungsi.WarnaTable;
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
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariPetugas;

public final class RMFlaccPainScale extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);

    public RMFlaccPainScale(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setUndecorated(true);
        setSize(880, 674);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.R.M.", "Nama Pasien", "Tgl.Pengkajian",
            "Wajah", "Kaki", "Aktivitas", "Tangisan", "Ketenangan",
            "Total Skor", "Interpretasi", "NIP", "Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] colWidths = {105, 65, 160, 80, 110, 90, 100, 100, 110, 60, 130, 90, 160};
        for (i = 0; i < colWidths.length; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            column.setPreferredWidth(colWidths[i]);
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        petugas.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if (petugas.getTable().getSelectedRow() != -1) {
                    KdPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                    NmPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                    BtnPetugas.requestFocus();
                }
            }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });

        ChkInput.setSelected(true);
        isForm(true);
        isCek();

        ActionListener calcListener = (ActionEvent e) -> hitungSkor();
        FaceWajah.addActionListener(calcListener);
        LegsKaki.addActionListener(calcListener);
        ActivityAktivitas.addActionListener(calcListener);
        CryTangisan.addActionListener(calcListener);
        ConsolabilityKetenangan.addActionListener(calcListener);
    }

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
        TNoRM = new widget.TextBox();
        TPasien = new widget.TextBox();
        jLabel16 = new widget.Label();
        TglPengkajian = new widget.Tanggal();
        label14 = new widget.Label();
        KdPetugas = new widget.TextBox();
        NmPetugas = new widget.TextBox();
        BtnPetugas = new widget.Button();
        jLabelFW = new widget.Label();
        FaceWajah = new javax.swing.JComboBox<>();
        jLabelLK = new widget.Label();
        LegsKaki = new javax.swing.JComboBox<>();
        jLabelAA = new widget.Label();
        ActivityAktivitas = new javax.swing.JComboBox<>();
        jLabelCT = new widget.Label();
        CryTangisan = new javax.swing.JComboBox<>();
        jLabelCK = new widget.Label();
        ConsolabilityKetenangan = new javax.swing.JComboBox<>();
        jLabelTS = new widget.Label();
        TotalSkor = new widget.TextBox();
        jLabelIntp = new widget.Label();
        Interpretasi = new widget.TextBox();
        ChkInput = new widget.CekBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
            "::[ FLACC Pain Scale (Face - Legs - Activity - Cry - Consolability) ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11),
            new java.awt.Color(50, 50, 50)));
        internalFrame1.setName("internalFrame1");
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll");
        tbObat.setName("tbObat");
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

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Baru");
        BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setName("BtnBatal");
        BtnBatal.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBatal.addActionListener(e -> BtnBatalActionPerformed(e));
        panelGlass8.add(BtnBatal);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus");
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapus.addActionListener(e -> BtnHapusActionPerformed(e));
        panelGlass8.add(BtnHapus);

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnEdit.setMnemonic('G');
        BtnEdit.setText("Ganti");
        BtnEdit.setToolTipText("Alt+G");
        BtnEdit.setName("BtnEdit");
        BtnEdit.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit.addActionListener(e -> BtnEditActionPerformed(e));
        panelGlass8.add(BtnEdit);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setName("BtnPrint");
        BtnPrint.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnPrint.addActionListener(e -> BtnPrintActionPerformed(e));
        panelGlass8.add(BtnPrint);

        jLabel7.setText("Record :");
        jLabel7.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass8.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass8.add(LCount);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar");
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(e -> BtnKeluarActionPerformed(e));
        panelGlass8.add(BtnKeluar);

        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9");
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel19.setText("Tanggal :");
        jLabel19.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"21-03-2026"}));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1");
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari1);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel21);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"21-03-2026"}));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2");
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :");
        jLabel6.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari");
        TCari.setPreferredSize(new java.awt.Dimension(310, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    BtnCari.requestFocus();
                } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
                    BtnCari.requestFocus();
                } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
                    BtnKeluar.requestFocus();
                }
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setMnemonic('2');
        BtnCari.setToolTipText("Alt+2");
        BtnCari.setName("BtnCari");
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(e -> tampil());
        panelGlass9.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setMnemonic('M');
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll");
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll.addActionListener(e -> { TCari.setText(""); tampil(); });
        panelGlass9.add(BtnAll);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);
        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput");
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 215));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setBackground(new java.awt.Color(250, 255, 245));
        FormInput.setName("FormInput");
        FormInput.setLayout(null);

        // Row 1: No Rawat, No RM, Nama Pasien
        jLabel4.setText("No.Rawat :");
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 10, 80, 23);

        TNoRw.setEditable(false);
        TNoRw.setName("TNoRw");
        FormInput.add(TNoRw);
        TNoRw.setBounds(84, 10, 136, 23);

        TNoRM.setEditable(false);
        TNoRM.setName("TNoRM");
        FormInput.add(TNoRM);
        TNoRM.setBounds(222, 10, 100, 23);

        TPasien.setEditable(false);
        TPasien.setName("TPasien");
        FormInput.add(TPasien);
        TPasien.setBounds(324, 10, 310, 23);

        // Row 2: Tanggal Pengkajian, Petugas
        jLabel16.setText("Tgl.Pengkajian :");
        FormInput.add(jLabel16);
        jLabel16.setBounds(0, 40, 100, 23);

        TglPengkajian.setForeground(new java.awt.Color(50, 70, 50));
        TglPengkajian.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"21-03-2026"}));
        TglPengkajian.setDisplayFormat("dd-MM-yyyy");
        TglPengkajian.setName("TglPengkajian");
        TglPengkajian.setOpaque(false);
        FormInput.add(TglPengkajian);
        TglPengkajian.setBounds(104, 40, 100, 23);

        label14.setText("Petugas :");
        FormInput.add(label14);
        label14.setBounds(215, 40, 60, 23);

        KdPetugas.setEditable(false);
        KdPetugas.setName("KdPetugas");
        FormInput.add(KdPetugas);
        KdPetugas.setBounds(278, 40, 100, 23);

        NmPetugas.setEditable(false);
        NmPetugas.setName("NmPetugas");
        FormInput.add(NmPetugas);
        NmPetugas.setBounds(380, 40, 200, 23);

        BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPetugas.setMnemonic('2');
        BtnPetugas.setToolTipText("Alt+2");
        BtnPetugas.setName("BtnPetugas");
        BtnPetugas.addActionListener(e -> {
            petugas.isCek();
            petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            petugas.setLocationRelativeTo(internalFrame1);
            petugas.setVisible(true);
        });
        FormInput.add(BtnPetugas);
        BtnPetugas.setBounds(582, 40, 28, 23);

        // Row 3: Face/Wajah
        jLabelFW.setText("Face/Wajah :");
        FormInput.add(jLabelFW);
        jLabelFW.setBounds(0, 70, 90, 23);

        FaceWajah.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{
            "Tidak ada ekspresi khusus atau senyum (0)",
            "Kadang menyeringai atau mengerutkan dahi, menarik diri, tidak tertarik (1)",
            "Sering dagu gemetar, rahang tertangkap (2)"
        }));
        FaceWajah.setName("FaceWajah");
        FormInput.add(FaceWajah);
        FaceWajah.setBounds(94, 70, 400, 23);

        // Row 4: Legs/Kaki
        jLabelLK.setText("Legs/Kaki :");
        FormInput.add(jLabelLK);
        jLabelLK.setBounds(0, 100, 90, 23);

        LegsKaki.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{
            "Normal, posisi rileks (0)",
            "Tidak nyaman, gelisah, tegang (1)",
            "Menendang atau mengangkat kaki (2)"
        }));
        LegsKaki.setName("LegsKaki");
        FormInput.add(LegsKaki);
        LegsKaki.setBounds(94, 100, 400, 23);

        // Row 5: Activity/Aktivitas
        jLabelAA.setText("Activity/Aktivitas :");
        FormInput.add(jLabelAA);
        jLabelAA.setBounds(0, 130, 110, 23);

        ActivityAktivitas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{
            "Berbaring dengan tenang, posisi normal, bergerak dengan mudah (0)",
            "Menggeliat, bergeser maju mundur, tegang (1)",
            "Melengkung, kaku, atau menyentak (2)"
        }));
        ActivityAktivitas.setName("ActivityAktivitas");
        FormInput.add(ActivityAktivitas);
        ActivityAktivitas.setBounds(114, 130, 400, 23);

        // Row 6: Cry/Tangisan
        jLabelCT.setText("Cry/Tangisan :");
        FormInput.add(jLabelCT);
        jLabelCT.setBounds(0, 160, 90, 23);

        CryTangisan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{
            "Tidak menangis (0)",
            "Erangan atau rengekan, sesekali mengeluh (1)",
            "Menangis terus-menerus, berteriak atau terisak-isak, sering mengeluh (2)"
        }));
        CryTangisan.setName("CryTangisan");
        FormInput.add(CryTangisan);
        CryTangisan.setBounds(94, 160, 400, 23);

        // Row 7: Consolability/Ketenangan
        jLabelCK.setText("Consolability :");
        FormInput.add(jLabelCK);
        jLabelCK.setBounds(0, 190, 90, 23);

        ConsolabilityKetenangan.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{
            "Senang, rileks (0)",
            "Dapat ditenangkan dengan sentuhan, pelukan atau kata-kata sesekali, dapat dialihkan (1)",
            "Sulit untuk dihibur atau dibuat nyaman (2)"
        }));
        ConsolabilityKetenangan.setName("ConsolabilityKetenangan");
        FormInput.add(ConsolabilityKetenangan);
        ConsolabilityKetenangan.setBounds(94, 190, 400, 23);

        // Row 8: Total Skor, Interpretasi
        jLabelTS.setText("Total Skor :");
        FormInput.add(jLabelTS);
        jLabelTS.setBounds(0, 220, 70, 23);

        TotalSkor.setEditable(false);
        TotalSkor.setName("TotalSkor");
        FormInput.add(TotalSkor);
        TotalSkor.setBounds(74, 220, 40, 23);

        jLabelIntp.setText("Interpretasi :");
        FormInput.add(jLabelIntp);
        jLabelIntp.setBounds(120, 220, 80, 23);

        Interpretasi.setEditable(false);
        Interpretasi.setName("Interpretasi");
        FormInput.add(Interpretasi);
        Interpretasi.setBounds(204, 220, 300, 23);

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
        ChkInput.addActionListener(e -> isForm(ChkInput.isSelected()));

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }

    private void isCek() {
        if (!akses.getstatus()) {
            JOptionPane.showMessageDialog(null, "Maaf, anda tidak mempunyai akses ke form ini....!!!!");
            dispose();
        }
    }

    private void hitungSkor() {
        int skor = 0;

        // Face/Wajah: index 0=0, 1=1, 2=2
        skor += FaceWajah.getSelectedIndex();

        // Legs/Kaki: index 0=0, 1=1, 2=2
        skor += LegsKaki.getSelectedIndex();

        // Activity/Aktivitas: index 0=0, 1=1, 2=2
        skor += ActivityAktivitas.getSelectedIndex();

        // Cry/Tangisan: index 0=0, 1=1, 2=2
        skor += CryTangisan.getSelectedIndex();

        // Consolability/Ketenangan: index 0=0, 1=1, 2=2
        skor += ConsolabilityKetenangan.getSelectedIndex();

        TotalSkor.setText(String.valueOf(skor));

        if (skor == 0) {
            Interpretasi.setText("Santai/Nyaman");
        } else if (skor <= 3) {
            Interpretasi.setText("Nyeri ringan / Tidak nyaman");
        } else if (skor <= 6) {
            Interpretasi.setText("Nyeri sedang");
        } else {
            Interpretasi.setText("Nyeri berat");
        }
    }

    private void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "SELECT s.no_rawat, s.no_rm, p.nm_pasien, s.tgl_pengkajian, s.face_wajah, " +
                "s.legs_kaki, s.activity_aktivitas, s.cry_tangisan, s.consolability_ketenangan, " +
                "s.total_skor, s.interpretasi, s.id_petugas, s.nm_petugas " +
                "FROM skrining_nyeri_flacc s LEFT JOIN pasien p ON p.no_rkm_medis=s.no_rm " +
                "WHERE DATE(s.tgl_pengkajian) BETWEEN ? AND ? " +
                "AND (s.no_rawat LIKE ? OR s.no_rm LIKE ? OR p.nm_pasien LIKE ?) " +
                "ORDER BY s.tgl_pengkajian DESC"
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
                        rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8),
                        rs.getString(9), rs.getString(10), rs.getString(11),
                        rs.getString(12), rs.getString(13)
                    });
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            } finally {
                if (rs != null) try { rs.close(); } catch (SQLException ex) {}
                if (ps != null) try { ps.close(); } catch (SQLException ex) {}
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        LCount.setText("" + tabMode.getRowCount());
    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tabMode.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tabMode.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tabMode.getValueAt(tbObat.getSelectedRow(), 2).toString());
            Valid.SetTgl(TglPengkajian, tabMode.getValueAt(tbObat.getSelectedRow(), 3).toString());

            String fw = tabMode.getValueAt(tbObat.getSelectedRow(), 4).toString();
            FaceWajah.setSelectedIndex(fw.contains("(0)") ? 0 : fw.contains("(1)") ? 1 : 2);

            String lk = tabMode.getValueAt(tbObat.getSelectedRow(), 5).toString();
            LegsKaki.setSelectedIndex(lk.contains("(0)") ? 0 : lk.contains("(1)") ? 1 : 2);

            String aa = tabMode.getValueAt(tbObat.getSelectedRow(), 6).toString();
            ActivityAktivitas.setSelectedIndex(aa.contains("(0)") ? 0 : aa.contains("(1)") ? 1 : 2);

            String ct = tabMode.getValueAt(tbObat.getSelectedRow(), 7).toString();
            CryTangisan.setSelectedIndex(ct.contains("(0)") ? 0 : ct.contains("(1)") ? 1 : 2);

            String ck = tabMode.getValueAt(tbObat.getSelectedRow(), 8).toString();
            ConsolabilityKetenangan.setSelectedIndex(ck.contains("(0)") ? 0 : ck.contains("(1)") ? 1 : 2);

            TotalSkor.setText(tabMode.getValueAt(tbObat.getSelectedRow(), 9).toString());
            Interpretasi.setText(tabMode.getValueAt(tbObat.getSelectedRow(), 10).toString());
            KdPetugas.setText(tabMode.getValueAt(tbObat.getSelectedRow(), 11).toString());
            NmPetugas.setText(tabMode.getValueAt(tbObat.getSelectedRow(), 12).toString());
        }
    }

    private void emptTeks() {
        TglPengkajian.setSelectedIndex(0);
        FaceWajah.setSelectedIndex(0);
        LegsKaki.setSelectedIndex(0);
        ActivityAktivitas.setSelectedIndex(0);
        CryTangisan.setSelectedIndex(0);
        ConsolabilityKetenangan.setSelectedIndex(0);
        TotalSkor.setText("0");
        Interpretasi.setText("");
        hitungSkor();
    }

    private void isForm(boolean show) {
        ChkInput.setVisible(false);
        if (show) {
            PanelInput.setPreferredSize(new Dimension(getWidth(), 260));
            FormInput.setVisible(true);
        } else {
            PanelInput.setPreferredSize(new Dimension(getWidth(), 20));
            FormInput.setVisible(false);
        }
        ChkInput.setVisible(true);
    }

    public void setNoRm(String norwt, java.util.Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        try {
            ps = koneksi.prepareStatement(
                "SELECT p.no_rkm_medis, p.nm_pasien FROM reg_periksa r " +
                "INNER JOIN pasien p ON r.no_rkm_medis=p.no_rkm_medis WHERE r.no_rawat=?"
            );
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TNoRM.setText(rs.getString(1));
                    TPasien.setText(rs.getString(2));
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
        isForm(true);
    }

    private void BtnSimpanActionPerformed(ActionEvent evt) {
        if (TNoRw.getText().trim().isEmpty() || TPasien.getText().trim().isEmpty()) {
            Valid.textKosong(TNoRw, "Pasien");
        } else if (KdPetugas.getText().trim().isEmpty() || NmPetugas.getText().trim().isEmpty()) {
            Valid.textKosong(KdPetugas, "Petugas");
        } else {
            try {
                String tgl = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
                // Create table if not exists
                koneksi.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS skrining_nyeri_flacc (" +
                    "no_rawat VARCHAR(20), no_rm VARCHAR(20), tgl_pengkajian DATE, " +
                    "face_wajah VARCHAR(100), legs_kaki VARCHAR(100), activity_aktivitas VARCHAR(100), " +
                    "cry_tangisan VARCHAR(100), consolability_ketenangan VARCHAR(100), " +
                    "total_skor INT, interpretasi VARCHAR(50), " +
                    "id_petugas VARCHAR(50), nm_petugas VARCHAR(100), " +
                    "PRIMARY KEY (no_rawat, tgl_pengkajian))"
                );
                ps = koneksi.prepareStatement(
                    "INSERT INTO skrining_nyeri_flacc VALUES(?,?,?,?,?,?,?,?,?,?,?,?)"
                );
                ps.setString(1, TNoRw.getText());
                ps.setString(2, TNoRM.getText());
                ps.setString(3, Valid.SetTgl(TglPengkajian.getSelectedItem().toString()));
                ps.setString(4, FaceWajah.getSelectedItem().toString());
                ps.setString(5, LegsKaki.getSelectedItem().toString());
                ps.setString(6, ActivityAktivitas.getSelectedItem().toString());
                ps.setString(7, CryTangisan.getSelectedItem().toString());
                ps.setString(8, ConsolabilityKetenangan.getSelectedItem().toString());
                ps.setInt(9, Integer.parseInt(TotalSkor.getText()));
                ps.setString(10, Interpretasi.getText());
                ps.setString(11, KdPetugas.getText());
                ps.setString(12, NmPetugas.getText());
                int result = ps.executeUpdate();
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "Data berhasil disimpan.");
                    tampil();
                    emptTeks();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Gagal menyimpan data: " + e.getMessage());
                System.out.println("Notifikasi : " + e);
            } finally {
                if (ps != null) try { ps.close(); } catch (SQLException ex) {}
            }
        }
    }

    private void BtnHapusActionPerformed(ActionEvent evt) {
        if (tbObat.getSelectedRow() != -1) {
            int confirm = JOptionPane.showConfirmDialog(null, "Yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    ps = koneksi.prepareStatement(
                        "DELETE FROM skrining_nyeri_flacc WHERE no_rawat=? AND tgl_pengkajian=?"
                    );
                    ps.setString(1, tabMode.getValueAt(tbObat.getSelectedRow(), 0).toString());
                    ps.setString(2, tabMode.getValueAt(tbObat.getSelectedRow(), 3).toString());
                    int result = ps.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(null, "Data berhasil dihapus.");
                        tampil();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Gagal menghapus data: " + e.getMessage());
                    System.out.println("Notifikasi : " + e);
                } finally {
                    if (ps != null) try { ps.close(); } catch (SQLException ex) {}
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Pilih data yang akan dihapus terlebih dahulu.");
        }
    }

    private void BtnEditActionPerformed(ActionEvent evt) {
        if (TNoRw.getText().trim().isEmpty() || TPasien.getText().trim().isEmpty()) {
            Valid.textKosong(TNoRw, "Pasien");
        } else if (KdPetugas.getText().trim().isEmpty() || NmPetugas.getText().trim().isEmpty()) {
            Valid.textKosong(KdPetugas, "Petugas");
        } else {
            ganti();
        }
    }

    private void ganti() {
        try {
            ps = koneksi.prepareStatement(
                "UPDATE skrining_nyeri_flacc SET face_wajah=?, legs_kaki=?, activity_aktivitas=?, " +
                "cry_tangisan=?, consolability_ketenangan=?, total_skor=?, interpretasi=?, " +
                "id_petugas=?, nm_petugas=? WHERE no_rawat=? AND tgl_pengkajian=?"
            );
            ps.setString(1, FaceWajah.getSelectedItem().toString());
            ps.setString(2, LegsKaki.getSelectedItem().toString());
            ps.setString(3, ActivityAktivitas.getSelectedItem().toString());
            ps.setString(4, CryTangisan.getSelectedItem().toString());
            ps.setString(5, ConsolabilityKetenangan.getSelectedItem().toString());
            ps.setInt(6, Integer.parseInt(TotalSkor.getText()));
            ps.setString(7, Interpretasi.getText());
            ps.setString(8, KdPetugas.getText());
            ps.setString(9, NmPetugas.getText());
            ps.setString(10, TNoRw.getText());
            ps.setString(11, Valid.SetTgl(TglPengkajian.getSelectedItem().toString()));
            int result = ps.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(null, "Data berhasil diperbarui.");
                tampil();
                emptTeks();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal memperbarui data: " + e.getMessage());
            System.out.println("Notifikasi : " + e);
        } finally {
            if (ps != null) try { ps.close(); } catch (SQLException ex) {}
        }
    }

    private void BtnBatalActionPerformed(ActionEvent evt) {
        emptTeks();
    }

    private void BtnPrintActionPerformed(ActionEvent evt) {
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        } else {
            JOptionPane.showMessageDialog(null, "Fitur cetak belum tersedia untuk form ini.");
        }
    }

    private void BtnKeluarActionPerformed(ActionEvent evt) {
        dispose();
    }

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }

    // UI component declarations
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
    private widget.TextBox KdPetugas;
    private widget.Label LCount;
    private widget.TextBox NmPetugas;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Tanggal TglPengkajian;
    private widget.InternalFrame internalFrame1;
    private javax.swing.JPanel jPanel3;
    private widget.Label jLabel16;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel4;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label label14;
    private widget.Table tbObat;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private javax.swing.JComboBox<String> FaceWajah;
    private javax.swing.JComboBox<String> LegsKaki;
    private javax.swing.JComboBox<String> ActivityAktivitas;
    private javax.swing.JComboBox<String> CryTangisan;
    private javax.swing.JComboBox<String> ConsolabilityKetenangan;
    private widget.TextBox TotalSkor;
    private widget.TextBox Interpretasi;
    private widget.Label jLabelFW;
    private widget.Label jLabelLK;
    private widget.Label jLabelAA;
    private widget.Label jLabelCT;
    private widget.Label jLabelCK;
    private widget.Label jLabelTS;
    private widget.Label jLabelIntp;
}
