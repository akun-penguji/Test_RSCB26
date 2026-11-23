package inventaris;

import bridging.koneksiDBWa;
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
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariPegawai;

/**
 * Form Lapor IT - Monitoring dan Tracking
 *
 * @author IT Support
 */
public final class LaporITMonitoring extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private Connection koneksi = koneksiDB.condb();
    private Connection koneksiwa;
    private PreparedStatement ps;
    private ResultSet rs;
    private DlgCariPegawai pegawai = new DlgCariPegawai(null, false);
    private InventarisRuang ruang = new InventarisRuang(null, false);
    private String notifwait = "", idgroupwait = "", pesan = "", tanggaljamkirim = "", petugaswa = "";

    public LaporITMonitoring(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.setLocation(8, 1);
        setSize(1250, 750);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Tiket", "Kategori", "Lokasi", "Yang Melaporkan", "Jabatan", "Unit",
            "Tgl.Laporan", "Deskripsi Masalah", "Status", "Tgl.Laporan"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbMonitoring.setModel(tabMode);

        tbMonitoring.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbMonitoring.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Set column widths
        for (int i = 0; i < 10; i++) {
            TableColumn column = tbMonitoring.getColumnModel().getColumn(i);
            switch (i) {
                case 0:
                    column.setPreferredWidth(120);
                    break; // No.Tiket
                case 1:
                    column.setPreferredWidth(150);
                    break; // Kategori
                case 2:
                    column.setPreferredWidth(150);
                    break; // Lokasi
                case 3:
                    column.setPreferredWidth(150);
                    break; // Yang Melaporkan
                case 4:
                    column.setPreferredWidth(120);
                    break; // Jabatan
                case 5:
                    column.setPreferredWidth(100);
                    break; // Unit
                case 6:
                    column.setPreferredWidth(120);
                    break; // Tgl.Laporan
                case 7:
                    column.setPreferredWidth(250);
                    break; // Deskripsi Masalah
                case 8:
                    column.setPreferredWidth(100);
                    break; // Status
                case 9:
                    column.setPreferredWidth(120);
                    break; // Tgl.Laporan (Proses)
            }
        }
        tbMonitoring.setDefaultRenderer(Object.class, new WarnaTable());

        // Input field restrictions
        noTiket.setDocument(new batasInput((byte) 20).getKata(noTiket));
        unit.setDocument(new batasInput((byte) 50).getKata(unit));
        deskripsiMasalah.setDocument(new batasInput((int) 500).getKata(deskripsiMasalah));
        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));
        TCari.requestFocus();

        ChkInput.setSelected(false);
        isForm();

        // Window listeners for pegawai dialog
        pegawai.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (akses.getform().equals("LaporITMonitoring")) {
                    if (pegawai.getTable().getSelectedRow() != -1) {
                        nikPelapor.setText(pegawai.getTable().getValueAt(pegawai.getTable().getSelectedRow(), 0).toString());
                        namaPelapor.setText(pegawai.getTable().getValueAt(pegawai.getTable().getSelectedRow(), 1).toString());
                        jabatan.setText(pegawai.getTable().getValueAt(pegawai.getTable().getSelectedRow(), 3).toString());
                        // Auto fill unit based on department
                        String dept = pegawai.getTable().getValueAt(pegawai.getTable().getSelectedRow(), 4).toString();
                        Sequel.cariIsi("select nm_bangsal from bangsal where kd_bangsal=?", unit, dept);
                        nikPelapor.requestFocus();
                    }
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

        // Window listeners for ruang dialog - DIGUNAKAN UNTUK UNIT DI FORM INPUT
        ruang.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (ruang.getTable().getSelectedRow() != -1) {
                    // Untuk filter di atas
                    if (akses.getform().equals("LaporITMonitoring-Filter")) {
                        nmRuangCari.setText(ruang.getTable().getValueAt(ruang.getTable().getSelectedRow(), 1).toString());
                        TCari.requestFocus();
                    } // Untuk input unit di form
                    else if (akses.getform().equals("LaporITMonitoring-Input")) {
                        unit.setText(ruang.getTable().getValueAt(ruang.getTable().getSelectedRow(), 1).toString());
                        cmbStatusTiket.requestFocus();
                    }
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

        // Auto search if enabled
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

        try {
            notifwait = koneksiDB.NOTIFWAIT();
            idgroupwait = koneksiDB.IDGROUPWAIT();
        } catch (Exception e) {
            notifwait = "no";
            idgroupwait = "no";
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbMonitoring = new widget.Table();
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
        label23 = new widget.Label();
        nmRuangCari = new widget.TextBox();
        btnRuang1 = new widget.Button();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        label24 = new widget.Label();
        cmbStatus = new widget.ComboBox();
        PanelInput = new javax.swing.JPanel();
        FormInput = new widget.PanelBiasa();
        label1 = new widget.Label();
        noTiket = new widget.TextBox();
        label8 = new widget.Label();
        tglLaporan = new widget.Tanggal();
        label2 = new widget.Label();
        cmbKategori = new widget.ComboBox();
        label3 = new widget.Label();
        lokasi = new widget.TextBox();
        label14 = new widget.Label();
        unit = new widget.TextBox();
        btnUnit = new widget.Button(); // BUTTON UNTUK UNIT
        label5 = new widget.Label();
        cmbStatusTiket = new widget.ComboBox();
        label9 = new widget.Label();
        nikPelapor = new widget.TextBox();
        namaPelapor = new widget.TextBox();
        btnPelapor = new widget.Button();
        label13 = new widget.Label();
        jabatan = new widget.TextBox();
        label10 = new widget.Label();
        tglProses = new widget.Tanggal(); // DIGANTI NAMA DARI tglMulai
        label7 = new widget.Label();
        scrollDeskripsi = new widget.ScrollPane();
        deskripsiMasalah = new widget.TextArea();
        ChkInput = new widget.CekBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
                "::[ Lapor IT - Monitoring dan Tracking ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 11),
                new java.awt.Color(50, 50, 50)
        ));
        internalFrame1.setName("internalFrame1");
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll");
        Scroll.setOpaque(true);

        tbMonitoring.setAutoCreateRowSorter(true);
        tbMonitoring.setToolTipText("Silahkan klik untuk memilih data yang akan diupdate");
        tbMonitoring.setName("tbMonitoring");
        tbMonitoring.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbMonitoringMouseClicked(evt);
            }
        });
        tbMonitoring.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbMonitoringKeyPressed(evt);
            }

            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbMonitoringKeyReleased(evt);
            }
        });
        Scroll.setViewportView(tbMonitoring);

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
        BtnEdit.setText("Update");
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

        jLabel7.setText("Record  :");
        jLabel7.setName("jLabel7");
        jLabel7.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass8.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount");
        LCount.setPreferredSize(new java.awt.Dimension(75, 23));
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
        panelGlass9.setLayout(null);

        jLabel19.setText("Tanggal :");
        jLabel19.setName("jLabel19");
        jLabel19.setPreferredSize(new java.awt.Dimension(54, 23));
        panelGlass9.add(jLabel19);
        jLabel19.setBounds(6, 10, 54, 23);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"25-08-2025"}));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1");
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari1);
        DTPCari1.setBounds(65, 10, 90, 23);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setName("jLabel21");
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel21);
        jLabel21.setBounds(160, 10, 23, 23);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"25-08-2025"}));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2");
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari2);
        DTPCari2.setBounds(188, 10, 90, 23);

        label23.setText("Unit :");
        label23.setName("label23");
        label23.setPreferredSize(new java.awt.Dimension(35, 23));
        panelGlass9.add(label23);
        label23.setBounds(283, 10, 35, 23);

        nmRuangCari.setEditable(false);
        nmRuangCari.setName("nmRuangCari");
        nmRuangCari.setPreferredSize(new java.awt.Dimension(120, 23));
        panelGlass9.add(nmRuangCari);
        nmRuangCari.setBounds(323, 10, 120, 23);

        btnRuang1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnRuang1.setMnemonic('1');
        btnRuang1.setToolTipText("Alt+1");
        btnRuang1.setName("btnRuang1");
        btnRuang1.setPreferredSize(new java.awt.Dimension(28, 23));
        btnRuang1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRuang1ActionPerformed(evt);
            }
        });
        panelGlass9.add(btnRuang1);
        btnRuang1.setBounds(448, 10, 28, 23);

        label24.setText("Status :");
        label24.setName("label24");
        label24.setPreferredSize(new java.awt.Dimension(45, 23));
        panelGlass9.add(label24);
        label24.setBounds(481, 10, 45, 23);

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "Semua", "Open", "In Progress", "Waiting", "Resolved", "Closed"
        }));
        cmbStatus.setName("cmbStatus");
        cmbStatus.setPreferredSize(new java.awt.Dimension(100, 23));
        panelGlass9.add(cmbStatus);
        cmbStatus.setBounds(531, 10, 100, 23);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6");
        jLabel6.setPreferredSize(new java.awt.Dimension(65, 23));
        panelGlass9.add(jLabel6);
        jLabel6.setBounds(636, 10, 65, 23);

        TCari.setFont(new java.awt.Font("Tahoma", 0, 12));
        TCari.setName("TCari");
        TCari.setPreferredSize(new java.awt.Dimension(150, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);
        TCari.setBounds(706, 10, 150, 23);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setMnemonic('2');
        BtnCari.setToolTipText("Alt+2");
        BtnCari.setFont(new java.awt.Font("Tahoma", 1, 12));
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
        BtnCari.setBounds(861, 10, 28, 23);

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
        BtnAll.setBounds(894, 10, 28, 23);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput");
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 190));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setName("FormInput");
        FormInput.setPreferredSize(new java.awt.Dimension(100, 165));
        FormInput.setLayout(null);

        // Baris 1
        label1.setText("No.Tiket :");
        label1.setName("label1");
        FormInput.add(label1);
        label1.setBounds(0, 10, 80, 23);

        noTiket.setEditable(false);
        noTiket.setName("noTiket");
        FormInput.add(noTiket);
        noTiket.setBounds(85, 10, 150, 23);

        label8.setText("Tgl.Laporan :");
        label8.setName("label8");
        FormInput.add(label8);
        label8.setBounds(240, 10, 80, 23);

        tglLaporan.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"25-08-2025"}));
        tglLaporan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        tglLaporan.setName("tglLaporan");
        tglLaporan.setOpaque(false);
        tglLaporan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tglLaporanKeyPressed(evt);
            }
        });
        FormInput.add(tglLaporan);
        tglLaporan.setBounds(325, 10, 140, 23);

        label2.setText("Kategori :");
        label2.setName("label2");
        FormInput.add(label2);
        label2.setBounds(470, 10, 60, 23);

        cmbKategori.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "Hardware", "Software/Aplikasi", "Jaringan/Internet", "Telepon/PABX",
            "CCTV/Keamanan", "Email", "Akses Sistem/Login", "Backup/Recovery Data", "Lainnya"
        }));
        cmbKategori.setName("cmbKategori");
        cmbKategori.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbKategoriKeyPressed(evt);
            }
        });
        FormInput.add(cmbKategori);
        cmbKategori.setBounds(535, 10, 200, 23);

        // Baris 2
        label3.setText("Lokasi :");
        label3.setName("label3");
        FormInput.add(label3);
        label3.setBounds(0, 40, 80, 23);

        lokasi.setName("lokasi");
        lokasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lokasiKeyPressed(evt);
            }
        });
        FormInput.add(lokasi);
        lokasi.setBounds(85, 40, 150, 23);

        // UNIT DENGAN BUTTON KLIP
        label14.setText("Unit :");
        label14.setName("label14");
        FormInput.add(label14);
        label14.setBounds(240, 40, 80, 23);

        unit.setEditable(false); // Set editable false seperti di form filter
        unit.setName("unit");
        unit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                unitKeyPressed(evt);
            }
        });
        FormInput.add(unit);
        unit.setBounds(325, 40, 115, 23); // Dikurangi width untuk button

        // BUTTON KLIP UNTUK UNIT
        btnUnit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnUnit.setMnemonic('4');
        btnUnit.setToolTipText("Alt+4");
        btnUnit.setName("btnUnit");
        btnUnit.setPreferredSize(new java.awt.Dimension(28, 23));
        btnUnit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUnitActionPerformed(evt);
            }
        });
        FormInput.add(btnUnit);
        btnUnit.setBounds(445, 40, 28, 23);

        label5.setText("Status :");
        label5.setName("label5");
        FormInput.add(label5);
        label5.setBounds(478, 40, 50, 23);

        cmbStatusTiket.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "Open", "In Progress", "Waiting", "Resolved", "Closed"
        }));
        cmbStatusTiket.setName("cmbStatusTiket");
        cmbStatusTiket.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbStatusTiketKeyPressed(evt);
            }
        });
        FormInput.add(cmbStatusTiket);
        cmbStatusTiket.setBounds(533, 40, 120, 23);

        // Baris 3
        label9.setText("Yang Melaporkan :");
        label9.setName("label9");
        FormInput.add(label9);
        label9.setBounds(0, 70, 120, 23);

        nikPelapor.setEditable(false);
        nikPelapor.setName("nikPelapor");
        nikPelapor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nikPelaporKeyPressed(evt);
            }
        });
        FormInput.add(nikPelapor);
        nikPelapor.setBounds(125, 70, 100, 23);

        namaPelapor.setEditable(false);
        namaPelapor.setName("namaPelapor");
        FormInput.add(namaPelapor);
        namaPelapor.setBounds(230, 70, 180, 23);

        btnPelapor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnPelapor.setMnemonic('3');
        btnPelapor.setToolTipText("Alt+3");
        btnPelapor.setFont(new java.awt.Font("Tahoma", 1, 12));
        btnPelapor.setName("btnPelapor");
        btnPelapor.setPreferredSize(new java.awt.Dimension(100, 30));
        btnPelapor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPelaporActionPerformed(evt);
            }
        });
        FormInput.add(btnPelapor);
        btnPelapor.setBounds(415, 70, 25, 23);

        label13.setText("Jabatan :");
        label13.setName("label13");
        FormInput.add(label13);
        label13.setBounds(445, 70, 60, 23);

        jabatan.setEditable(false);
        jabatan.setName("jabatan");
        FormInput.add(jabatan);
        jabatan.setBounds(510, 70, 120, 23);

        // LABEL DIUBAH DARI TGL.MULAI MENJADI TGL.LAPORAN
        label10.setText("Tgl.Laporan :");
        label10.setName("label10");
        FormInput.add(label10);
        label10.setBounds(635, 70, 80, 23);

        tglProses.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"25-08-2025"}));
        tglProses.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        tglProses.setName("tglProses");
        tglProses.setOpaque(false);
        tglProses.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tglProsesKeyPressed(evt);
            }
        });
        FormInput.add(tglProses);
        tglProses.setBounds(720, 70, 140, 23);

        // Baris 4 - Deskripsi Masalah
        label7.setText("Deskripsi Masalah :");
        label7.setName("label7");
        FormInput.add(label7);
        label7.setBounds(0, 100, 120, 23);

        scrollDeskripsi = new widget.ScrollPane();
        scrollDeskripsi.setName("scrollDeskripsi");
        scrollDeskripsi.setPreferredSize(new java.awt.Dimension(580, 60));

        deskripsiMasalah = new widget.TextArea();
        deskripsiMasalah.setName("deskripsiMasalah");
        deskripsiMasalah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                deskripsiMasalahKeyPressed(evt);
            }
        });
        scrollDeskripsi.setViewportView(deskripsiMasalah);

        FormInput.add(scrollDeskripsi);
        scrollDeskripsi.setBounds(125, 100, 735, 55);

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
    }

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, deskripsiMasalah, BtnBatal);
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
        if (akses.getjml2() >= 1) {
            if (akses.getkode().equals(nikPelapor.getText())) {
                Valid.hapusTable(tabMode, noTiket, "lapor_it_monitoring", "no_tiket");
            } else {
                JOptionPane.showMessageDialog(null, "Hanya bisa dihapus oleh yang melaporkan...!!");
            }
        } else {
            Valid.hapusTable(tabMode, noTiket, "lapor_it_monitoring", "no_tiket");
        }
        BtnCariActionPerformed(evt);
        emptTeks();
    }

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnHapusActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
    }

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {
        if (noTiket.getText().trim().equals("")) {
            Valid.textKosong(noTiket, "No.Tiket");
        } else if (lokasi.getText().trim().equals("")) {
            Valid.textKosong(lokasi, "Lokasi");
        } else if (unit.getText().trim().equals("")) {
            Valid.textKosong(unit, "Unit");
        } else if (nikPelapor.getText().trim().equals("")) {
            Valid.textKosong(nikPelapor, "Yang Melaporkan");
        } else if (deskripsiMasalah.getText().trim().equals("")) {
            Valid.textKosong(deskripsiMasalah, "Deskripsi Masalah");
        } else {
            if (tbMonitoring.getSelectedRow() > -1) {
                if (Sequel.mengedittf("lapor_it_monitoring", "no_tiket=?", "kategori=?,lokasi=?,unit=?,nik_pelapor=?,status=?,tgl_proses=?,deskripsi_masalah=?", 8, new String[]{
                    cmbKategori.getSelectedItem().toString(),
                    lokasi.getText(),
                    unit.getText(),
                    nikPelapor.getText(),
                    cmbStatusTiket.getSelectedItem().toString(),
                    Valid.SetTgl(tglProses.getSelectedItem() + "") + " " + tglProses.getSelectedItem().toString().substring(11, 19),
                    deskripsiMasalah.getText(),
                    tbMonitoring.getValueAt(tbMonitoring.getSelectedRow(), 0).toString()
                }) == true) {
                    tampil();
                    emptTeks();
                }
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
            dispose();
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
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));

            String whereClause = buildWhereClause();
            Valid.MyReportqry("rptLaporITMonitoring.jasper", "report", "::[ Laporan Monitoring IT ]::",
                    "select * from lapor_it_monitoring " + whereClause + " order by no_tiket", param);
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
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            tbMonitoring.requestFocus();
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
        nmRuangCari.setText("");
        cmbStatus.setSelectedIndex(0);
        tampil();
    }

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnAllActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnPrint, BtnKeluar);
        }
    }

    private void tbMonitoringMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }

    private void tbMonitoringKeyPressed(java.awt.event.KeyEvent evt) {
        if (tabMode.getRowCount() != 0) {
            if (evt.getKeyCode() == KeyEvent.VK_SHIFT) {
                TCari.setText("");
                TCari.requestFocus();
            }
        }
    }

    private void tbMonitoringKeyReleased(java.awt.event.KeyEvent evt) {
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

    private void formWindowOpened(java.awt.event.WindowEvent evt) {
        tampil();
    }

    // EVENT HANDLER UNTUK BUTTON RUANG DI FILTER (ATAS)
    private void btnRuang1ActionPerformed(java.awt.event.ActionEvent evt) {
        akses.setform("LaporITMonitoring-Filter"); // Set form identifier untuk filter
        ruang.isCek();
        ruang.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        ruang.setLocationRelativeTo(internalFrame1);
        ruang.setAlwaysOnTop(false);
        ruang.setVisible(true);
    }

    // EVENT HANDLER UNTUK BUTTON UNIT DI FORM INPUT
    private void btnUnitActionPerformed(java.awt.event.ActionEvent evt) {
        akses.setform("LaporITMonitoring-Input"); // Set form identifier untuk input
        ruang.isCek();
        ruang.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        ruang.setLocationRelativeTo(internalFrame1);
        ruang.setAlwaysOnTop(false);
        ruang.setVisible(true);
    }

    private void btnPelaporActionPerformed(java.awt.event.ActionEvent evt) {
        akses.setform("LaporITMonitoring");
        pegawai.emptTeks();
        pegawai.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        pegawai.setLocationRelativeTo(internalFrame1);
        pegawai.setVisible(true);
    }

    // Key event handlers - URUTAN FOCUS DIUPDATE
    private void tglLaporanKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, noTiket, cmbKategori);
    }

    private void cmbKategoriKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, tglLaporan, lokasi);
    }

    private void lokasiKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, cmbKategori, unit);
    }

    private void unitKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, lokasi, cmbStatusTiket);
    }

    private void cmbStatusTiketKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, unit, nikPelapor);
    }

    private void nikPelaporKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, cmbStatusTiket, jabatan);
    }

    private void tglProsesKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, jabatan, deskripsiMasalah);
    }

    private void deskripsiMasalahKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, tglProses, BtnSimpan);
    }

    // Main method
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            LaporITMonitoring dialog = new LaporITMonitoring(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - DIUPDATE
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.PanelBiasa FormInput;
    private widget.Label LCount;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.Button btnRuang1;
    private widget.Button btnPelapor;
    private widget.Button btnUnit; // Button untuk unit
    private widget.ComboBox cmbKategori;
    private widget.ComboBox cmbStatus;
    private widget.ComboBox cmbStatusTiket;
    private widget.TextArea deskripsiMasalah;
    private widget.ScrollPane scrollDeskripsi;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private javax.swing.JPanel jPanel3;
    private widget.TextBox jabatan;
    private widget.Label label1;
    private widget.Label label10;
    private widget.Label label13;
    private widget.Label label14;
    private widget.Label label2;
    private widget.Label label23;
    private widget.Label label24;
    private widget.Label label3;
    private widget.Label label5;
    private widget.Label label7;
    private widget.Label label8;
    private widget.Label label9;
    private widget.TextBox lokasi;
    private widget.TextBox namaPelapor;
    private widget.TextBox nikPelapor;
    private widget.TextBox nmRuangCari;
    private widget.TextBox noTiket;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbMonitoring;
    private widget.Tanggal tglLaporan;
    private widget.Tanggal tglProses; // Diganti dari tglMulai
    private widget.TextBox unit;

    // Methods - DIUPDATE
    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            String whereClause = buildWhereClause();
            ps = koneksi.prepareStatement(
                    "SELECT lim.no_tiket, lim.kategori, lim.lokasi, "
                    + "p.nama as yang_melaporkan, p.jbtn as jabatan, lim.unit, "
                    + "lim.tgl_laporan, lim.deskripsi_masalah, lim.status, "
                    + "lim.tgl_proses "
                    + "FROM lapor_it_monitoring lim "
                    + "LEFT JOIN pegawai p ON lim.nik_pelapor = p.nik "
                    + whereClause + " ORDER BY lim.no_tiket"
            );

            setWhereParameters();
            rs = ps.executeQuery();

            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_tiket"),
                    rs.getString("kategori"),
                    rs.getString("lokasi"),
                    rs.getString("yang_melaporkan"),
                    rs.getString("jabatan"),
                    rs.getString("unit"),
                    rs.getString("tgl_laporan"),
                    rs.getString("deskripsi_masalah"),
                    rs.getString("status"),
                    rs.getString("tgl_proses")
                });
            }
            LCount.setText(tabMode.getRowCount() + "");
        } catch (Exception e) {
            System.out.println("Error tampil: " + e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                System.out.println("Error close: " + e);
            }
        }
    }

    private String buildWhereClause() {
        StringBuilder where = new StringBuilder("WHERE lim.tgl_laporan BETWEEN ? AND ?");

        if (!nmRuangCari.getText().trim().equals("")) {
            where.append(" AND lim.unit LIKE ?");
        }

        if (!cmbStatus.getSelectedItem().toString().equals("Semua")) {
            where.append(" AND lim.status = ?");
        }

        if (!TCari.getText().trim().equals("")) {
            where.append(" AND (lim.no_tiket LIKE ? OR lim.kategori LIKE ? OR p.nama LIKE ? OR lim.deskripsi_masalah LIKE ?)");
        }

        return where.toString();
    }

    private void setWhereParameters() throws Exception {
        int paramIndex = 1;

        // Date parameters
        ps.setString(paramIndex++, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
        ps.setString(paramIndex++, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");

        // Unit filter
        if (!nmRuangCari.getText().trim().equals("")) {
            ps.setString(paramIndex++, "%" + nmRuangCari.getText().trim() + "%");
        }

        // Status filter
        if (!cmbStatus.getSelectedItem().toString().equals("Semua")) {
            ps.setString(paramIndex++, cmbStatus.getSelectedItem().toString());
        }

        // Search filter
        if (!TCari.getText().trim().equals("")) {
            String searchTerm = "%" + TCari.getText().trim() + "%";
            ps.setString(paramIndex++, searchTerm);
            ps.setString(paramIndex++, searchTerm);
            ps.setString(paramIndex++, searchTerm);
            ps.setString(paramIndex++, searchTerm);
        }
    }

    public void emptTeks() {
        ChkInput.setSelected(true);
        isForm();
        noTiket.setText("");
        lokasi.setText("");
        unit.setText("");
        nikPelapor.setText("");
        namaPelapor.setText("");
        jabatan.setText("");
        deskripsiMasalah.setText("");
        cmbKategori.setSelectedIndex(0);
        cmbStatusTiket.setSelectedIndex(0);
        tglLaporan.setDate(new Date());
        tglProses.setDate(new Date());

        // Auto generate ticket number
        Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(no_tiket,3),signed)),0) from lapor_it_monitoring where date_format(tgl_laporan,'%Y-%m-%d')='" + Valid.SetTgl(tglLaporan.getSelectedItem() + "") + "' ",
                "IT" + tglLaporan.getSelectedItem().toString().substring(6, 10) + tglLaporan.getSelectedItem().toString().substring(3, 5) + tglLaporan.getSelectedItem().toString().substring(0, 2), 3, noTiket);
        noTiket.requestFocus();
    }

    private void getData() {
        if (tbMonitoring.getSelectedRow() != -1) {
            noTiket.setText(tbMonitoring.getValueAt(tbMonitoring.getSelectedRow(), 0).toString());
            cmbKategori.setSelectedItem(tbMonitoring.getValueAt(tbMonitoring.getSelectedRow(), 1).toString());
            lokasi.setText(tbMonitoring.getValueAt(tbMonitoring.getSelectedRow(), 2).toString());

            // Get reporter info
            String pelapor = tbMonitoring.getValueAt(tbMonitoring.getSelectedRow(), 3).toString();
            namaPelapor.setText(pelapor);
            jabatan.setText(tbMonitoring.getValueAt(tbMonitoring.getSelectedRow(), 4).toString());
            unit.setText(tbMonitoring.getValueAt(tbMonitoring.getSelectedRow(), 5).toString());

            // Get NIK from database based on nama
            try {
                Sequel.cariIsi("select nik from pegawai where nama=?", nikPelapor, pelapor);
            } catch (Exception e) {
                System.out.println("Error getting employee data: " + e);
            }

            cmbStatusTiket.setSelectedItem(tbMonitoring.getValueAt(tbMonitoring.getSelectedRow(), 8).toString());

            // Set dates
            Valid.SetTgl2(tglLaporan, tbMonitoring.getValueAt(tbMonitoring.getSelectedRow(), 6).toString());
            Valid.SetTgl2(tglProses, tbMonitoring.getValueAt(tbMonitoring.getSelectedRow(), 9).toString());

            // Set description
            deskripsiMasalah.setText(tbMonitoring.getValueAt(tbMonitoring.getSelectedRow(), 7).toString());
        }
    }

    public JTable getTable() {
        return tbMonitoring;
    }

    private void isForm() {
        if (ChkInput.isSelected() == true) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(getWidth(), 190));
            FormInput.setVisible(true);
            ChkInput.setVisible(true);
        } else if (ChkInput.isSelected() == false) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(getWidth(), 20));
            FormInput.setVisible(false);
            ChkInput.setVisible(true);
        }
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getperbaikan_inventaris());
        BtnHapus.setEnabled(akses.getperbaikan_inventaris());
        BtnEdit.setEnabled(akses.getperbaikan_inventaris());
        BtnPrint.setEnabled(akses.getperbaikan_inventaris());
        TCari.requestFocus();
    }

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
        if (noTiket.getText().trim().equals("")) {
            Valid.textKosong(noTiket, "No.Tiket");
        } else if (lokasi.getText().trim().equals("")) {
            Valid.textKosong(lokasi, "Lokasi");
        } else if (unit.getText().trim().equals("")) {
            Valid.textKosong(unit, "Unit");
        } else if (nikPelapor.getText().trim().equals("")) {
            Valid.textKosong(nikPelapor, "Yang Melaporkan");
        } else if (deskripsiMasalah.getText().trim().equals("")) {
            Valid.textKosong(deskripsiMasalah, "Deskripsi Masalah");
        } else {
            try {
                // Format tanggal dengan benar
                String tanggalLaporan = Valid.SetTgl(tglLaporan.getSelectedItem() + "") + " "
                        + tglLaporan.getSelectedItem().toString().substring(11, 19);
                String tanggalProses = Valid.SetTgl(tglProses.getSelectedItem() + "") + " "
                        + tglProses.getSelectedItem().toString().substring(11, 19);

                // Debug info
                System.out.println("=== DEBUG INSERT ===");
                System.out.println("No Tiket: [" + noTiket.getText() + "]");
                System.out.println("Kategori: [" + cmbKategori.getSelectedItem().toString() + "]");
                System.out.println("Lokasi: [" + lokasi.getText() + "]");
                System.out.println("Unit: [" + unit.getText() + "]");
                System.out.println("NIK: [" + nikPelapor.getText() + "]");
                System.out.println("Tgl Laporan: [" + tanggalLaporan + "]");
                System.out.println("Deskripsi: [" + deskripsiMasalah.getText() + "]");
                System.out.println("Status: [" + cmbStatusTiket.getSelectedItem().toString() + "]");
                System.out.println("Tgl Proses: [" + tanggalProses + "]");

                // GUNAKAN METHOD SEQUEL YANG BENAR - menyimpantf dengan parameter array
                boolean result = Sequel.menyimpantf("lapor_it_monitoring", "?,?,?,?,?,?,?,?,?", "No.Tiket", 9, new String[]{
                    noTiket.getText(), // 1. no_tiket
                    cmbKategori.getSelectedItem().toString(), // 2. kategori  
                    lokasi.getText(), // 3. lokasi
                    unit.getText(), // 4. unit
                    nikPelapor.getText(), // 5. nik_pelapor
                    tanggalLaporan, // 6. tgl_laporan
                    deskripsiMasalah.getText(), // 7. deskripsi_masalah
                    cmbStatusTiket.getSelectedItem().toString(),// 8. status
                    tanggalProses // 9. tgl_proses
                });

                System.out.println("Result menyimpantf: " + result);

                if (result) {
                    System.out.println("Data berhasil disimpan, akan kirim notifikasi WA...");

                    // Kirim notifikasi WA
                    try {
                        NotifWa();
                        System.out.println("Notifikasi WA berhasil dikirim");
                    } catch (Exception ex) {
                        System.out.println("Error kirim notifikasi WA: " + ex.getMessage());
                        // Tidak menghentikan proses jika notifikasi gagal
                    }

                    // Refresh dan reset form
                    tampil();
                    emptTeks();

                    JOptionPane.showMessageDialog(null,
                            "Data laporan IT berhasil disimpan!\nNo. Tiket: " + noTiket.getText(),
                            "Sukses",
                            JOptionPane.INFORMATION_MESSAGE);

                } else {
                    System.out.println("GAGAL MENYIMPAN DATA!");
                    JOptionPane.showMessageDialog(null,
                            "Gagal menyimpan data ke database!\nSilakan periksa data yang diinput.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception e) {
                System.out.println("EXCEPTION: " + e.getClass().getSimpleName());
                System.out.println("ERROR MESSAGE: " + e.getMessage());
                e.printStackTrace();

                JOptionPane.showMessageDialog(null,
                        "Terjadi error saat menyimpan data:\n" + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

// Method NotifWa yang sudah diperbaiki
    private void NotifWa() {
        if (!notifwait.equals("yes")) {
            System.out.println("Notifikasi WA tidak aktif: " + notifwait);
            return;
        }

        if (idgroupwait == null || idgroupwait.equals("no") || idgroupwait.trim().equals("")) {
            System.out.println("ID Group WA tidak valid: " + idgroupwait);
            return;
        }

        try {
            koneksiwa = koneksiDBWa.condb();
            if (koneksiwa == null) {
                System.out.println("Koneksi WA database gagal");
                return;
            }

            tanggaljamkirim = Valid.SetTgl(tglLaporan.getSelectedItem() + "") + " "
                    + tglLaporan.getSelectedItem().toString().substring(11, 19);

            String pesan = "*LAPORAN IT - TIKET BARU*\n"
                    + "==========================\n"
                    + "No Tiket: " + noTiket.getText() + "\n"
                    + "Tanggal: " + tglLaporan.getSelectedItem() + "\n"
                    + "Kategori: *" + cmbKategori.getSelectedItem().toString() + "*\n"
                    + "Lokasi: " + lokasi.getText() + "\n"
                    + "Unit: " + unit.getText() + "\n\n"
                    + "Pelapor: " + namaPelapor.getText() + "\n"
                    + "Jabatan: " + jabatan.getText() + "\n\n"
                    + "Deskripsi:\n" + deskripsiMasalah.getText() + "\n\n"
                    + "Status: " + cmbStatusTiket.getSelectedItem().toString() + "\n"
                    + "==========================\n"
                    + "_Mohon segera ditindaklanjuti_";

            System.out.println("=== DEBUG NOTIF WA ===");
            System.out.println("ID Group: " + idgroupwait);
            System.out.println("Tanggal Kirim: " + tanggaljamkirim);
            System.out.println("Panjang Pesan: " + pesan.length());

            String sql = "INSERT INTO wa_outbox (nowa, pesan, tanggal_jam, status, source, sender, success, response, request, type, file) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            ps = koneksiwa.prepareStatement(sql);
            ps.setString(1, idgroupwait);           // nowa
            ps.setString(2, pesan);                 // pesan
            ps.setString(3, tanggaljamkirim);       // tanggal_jam
            ps.setString(4, "ANTRIAN");             // status
            ps.setString(5, "KHANZA");              // source
            ps.setString(6, "NODEJS");              // sender
            ps.setString(7, "");                    // success
            ps.setString(8, "");                    // response
            ps.setString(9, "");                    // request
            ps.setString(10, "TEXT");               // type
            ps.setString(11, "");                   // file

            int result = ps.executeUpdate();

            if (result > 0) {
                System.out.println("Notifikasi WA berhasil masuk antrian");
            } else {
                System.out.println("Gagal masukan notifikasi WA");
            }

        } catch (Exception e) {
            System.out.println("Error NotifWa: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Gagal kirim notifikasi WA: " + e.getMessage());
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (koneksiwa != null) {
                    koneksiwa.close();
                }
            } catch (Exception e) {
                System.out.println("Error closing WA connection: " + e.getMessage());
            }
        }
    }

}
