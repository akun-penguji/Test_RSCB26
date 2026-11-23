/*
 * DlgRegisterPenyampaianLaporan.java
 *
 * Register Penyampaian Laporan Rumah Sakit.
 *
 *  1. Setiap penyampaian laporan dicatat di register beserta bukti kirimnya.
 *  2. Bukti berupa tangkapan layar keberhasilan unggah, tanda terima surat,
 *     atau bukti surel terkirim (berkas disalin ke folder berkasregisterlaporan).
 *  3. Keterlambatan dicatat beserta alasan dan tindak lanjut kepala unit.
 *  4. Ketepatan waktu dievaluasi per semester (tombol Evaluasi) untuk
 *     dilaporkan kepada Direktur.
 *
 * Status kirim dihitung otomatis dari tgl_kirim vs tgl_jatuh_tempo, jadi tidak
 * bisa diakali manual oleh petugas.
 */
package laporan;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class DlgRegisterPenyampaianLaporan extends javax.swing.JDialog {

    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final SimpleDateFormat FDB = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat FTAMPIL = new SimpleDateFormat("dd-MM-yyyy");
    private PreparedStatement ps;
    private ResultSet rs;
    private DefaultTableModel tabMode;

    /** Folder penyimpanan bukti, relatif terhadap folder aplikasi. */
    private static final String FOLDER_BUKTI = "berkasregisterlaporan";

    /** Creates new form DlgRegisterPenyampaianLaporan */
    public DlgRegisterPenyampaianLaporan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(1160, 674);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Register", "Kode", "Jenis Laporan", "Unit", "Tujuan", "Periode", "Jatuh Tempo",
            "Tgl Kirim", "Jam", "Cara Kirim", "Status", "Telat", "Bukti",
            "Alasan Keterlambatan", "Tindak Lanjut", "NIP Ka.Unit", "Kepala Unit", "Keterangan"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbRegister.setModel(tabMode);
        tbRegister.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbRegister.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] lebar = {90, 0, 230, 110, 140, 100, 80, 80, 60, 105, 90, 45, 40, 220, 220, 0, 140, 150};
        for (int i = 0; i < lebar.length; i++) {
            TableColumn column = tbRegister.getColumnModel().getColumn(i);
            column.setPreferredWidth(lebar[i]);
            if (lebar[i] == 0) { // kolom teknis, disembunyikan
                column.setMinWidth(0);
                column.setMaxWidth(0);
                column.setWidth(0);
            }
        }
        tbRegister.setDefaultRenderer(Object.class, new WarnaTable());

        TPeriode.setDocument(new batasInput((byte) 30).getKata(TPeriode));
        TAlasan.setDocument(new batasInput((int) 500).getKata(TAlasan));
        TTindakLanjut.setDocument(new batasInput((int) 500).getKata(TTindakLanjut));
        TKeterangan.setDocument(new batasInput((byte) 200).getKata(TKeterangan));
        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));

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

        muatJenisLaporan();
        muatPetugas();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbRegister = new widget.Table();
        jPanel3 = new widget.panelisi();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnBukti = new widget.Button();
        BtnPrint = new widget.Button();
        BtnEvaluasi = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel20 = new widget.Label();
        Tgl1 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        Tgl2 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        panelGlass7 = new widget.panelisi();
        jLabel1 = new widget.Label();
        TNoReg = new widget.TextBox();
        jLabel2 = new widget.Label();
        cmbJenis = new widget.ComboBox();
        jLabel3 = new widget.Label();
        TPeriode = new widget.TextBox();
        jLabel4 = new widget.Label();
        TglTempo = new widget.Tanggal();
        jLabel5 = new widget.Label();
        TglKirim = new widget.Tanggal();
        jLabel8 = new widget.Label();
        TJam = new widget.TextBox();
        jLabel9 = new widget.Label();
        cmbCara = new widget.ComboBox();
        jLabel10 = new widget.Label();
        TAlasan = new widget.TextBox();
        jLabel11 = new widget.Label();
        cmbKepalaUnit = new widget.ComboBox();
        jLabel12 = new widget.Label();
        TTindakLanjut = new widget.TextBox();
        jLabel13 = new widget.Label();
        TKeterangan = new widget.TextBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Register Penyampaian Laporan Rumah Sakit ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbRegister.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbRegister.setName("tbRegister"); // NOI18N
        tbRegister.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbRegisterMouseClicked(evt);
            }
        });
        tbRegister.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbRegisterKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbRegister);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 88));
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
        panelGlass8.add(BtnSimpan);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); // NOI18N
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Batal");
        BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setName("BtnBatal"); // NOI18N
        BtnBatal.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBatalActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnBatal);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/delete-16x16.png"))); // NOI18N
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
        panelGlass8.add(BtnHapus);

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/file-edit-16x16.png"))); // NOI18N
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
        panelGlass8.add(BtnEdit);

        BtnBukti.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnBukti.setMnemonic('U');
        BtnBukti.setText("Bukti");
        BtnBukti.setToolTipText("Alt+U, lampiran bukti penyampaian laporan");
        BtnBukti.setName("BtnBukti"); // NOI18N
        BtnBukti.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBukti.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBuktiActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnBukti);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T, cetak register sesuai periode di atas");
        BtnPrint.setName("BtnPrint"); // NOI18N
        BtnPrint.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrintActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnPrint);

        BtnEvaluasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
        BtnEvaluasi.setMnemonic('V');
        BtnEvaluasi.setText("Evaluasi");
        BtnEvaluasi.setToolTipText("Alt+V, evaluasi ketepatan waktu per semester untuk Direktur");
        BtnEvaluasi.setName("BtnEvaluasi"); // NOI18N
        BtnEvaluasi.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEvaluasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEvaluasiActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnEvaluasi);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/logout.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispose();
            }
        });
        panelGlass8.add(BtnKeluar);

        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel20.setText("Jatuh Tempo :");
        jLabel20.setName("jLabel20"); // NOI18N
        jLabel20.setPreferredSize(new java.awt.Dimension(85, 23));
        panelGlass9.add(jLabel20);

        Tgl1.setDisplayFormat("dd-MM-yyyy");
        Tgl1.setName("Tgl1"); // NOI18N
        Tgl1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(Tgl1);

        jLabel21.setText("s.d.");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(25, 23));
        panelGlass9.add(jLabel21);

        Tgl2.setDisplayFormat("dd-MM-yyyy");
        Tgl2.setName("Tgl2"); // NOI18N
        Tgl2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(Tgl2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel6);

        TCari.setToolTipText("Alt+C, cari no.register/jenis laporan/unit/periode/status");
        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(230, 23));
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
                tampil();
            }
        });
        panelGlass9.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setToolTipText("Alt+M, tampilkan semua");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TCari.setText("");
                tampil();
            }
        });
        panelGlass9.add(BtnAll);

        jLabel7.setText("Tampil :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(55, 23));
        panelGlass9.add(jLabel7);

        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass9.add(LCount);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        panelGlass7.setName("panelGlass7"); // NOI18N
        panelGlass7.setPreferredSize(new java.awt.Dimension(44, 105));
        panelGlass7.setLayout(null);

        // ---------------------------------------------------------- baris 1
        jLabel1.setText("No.Register :");
        jLabel1.setName("jLabel1"); // NOI18N
        panelGlass7.add(jLabel1);
        jLabel1.setBounds(0, 10, 90, 23);

        TNoReg.setEditable(false);
        TNoReg.setHighlighter(null);
        TNoReg.setName("TNoReg"); // NOI18N
        panelGlass7.add(TNoReg);
        TNoReg.setBounds(94, 10, 110, 23);

        jLabel2.setText("Jenis Laporan :");
        jLabel2.setName("jLabel2"); // NOI18N
        panelGlass7.add(jLabel2);
        jLabel2.setBounds(210, 10, 95, 23);

        cmbJenis.setName("cmbJenis"); // NOI18N
        cmbJenis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hitungJatuhTempoDefault();
            }
        });
        panelGlass7.add(cmbJenis);
        cmbJenis.setBounds(309, 10, 420, 23);

        jLabel13.setText("Keterangan :");
        jLabel13.setName("jLabel13"); // NOI18N
        panelGlass7.add(jLabel13);
        jLabel13.setBounds(735, 10, 85, 23);

        TKeterangan.setName("TKeterangan"); // NOI18N
        panelGlass7.add(TKeterangan);
        TKeterangan.setBounds(824, 10, 300, 23);

        // ---------------------------------------------------------- baris 2
        jLabel3.setText("Periode Lap. :");
        jLabel3.setName("jLabel3"); // NOI18N
        panelGlass7.add(jLabel3);
        jLabel3.setBounds(0, 37, 90, 23);

        TPeriode.setToolTipText("Periode laporan, mis. Juli 2026 / Triwulan II 2026");
        TPeriode.setName("TPeriode"); // NOI18N
        panelGlass7.add(TPeriode);
        TPeriode.setBounds(94, 37, 110, 23);

        jLabel4.setText("Jatuh Tempo :");
        jLabel4.setName("jLabel4"); // NOI18N
        panelGlass7.add(jLabel4);
        jLabel4.setBounds(210, 37, 95, 23);

        TglTempo.setDisplayFormat("dd-MM-yyyy");
        TglTempo.setName("TglTempo"); // NOI18N
        panelGlass7.add(TglTempo);
        TglTempo.setBounds(309, 37, 100, 23);

        jLabel5.setText("Tgl.Kirim :");
        jLabel5.setName("jLabel5"); // NOI18N
        panelGlass7.add(jLabel5);
        jLabel5.setBounds(415, 37, 70, 23);

        TglKirim.setDisplayFormat("dd-MM-yyyy");
        TglKirim.setName("TglKirim"); // NOI18N
        panelGlass7.add(TglKirim);
        TglKirim.setBounds(489, 37, 100, 23);

        jLabel8.setText("Jam :");
        jLabel8.setName("jLabel8"); // NOI18N
        panelGlass7.add(jLabel8);
        jLabel8.setBounds(595, 37, 40, 23);

        TJam.setText("00:00:00");
        TJam.setName("TJam"); // NOI18N
        panelGlass7.add(TJam);
        TJam.setBounds(639, 37, 90, 23);

        jLabel9.setText("Cara Kirim :");
        jLabel9.setName("jLabel9"); // NOI18N
        panelGlass7.add(jLabel9);
        jLabel9.setBounds(735, 37, 85, 23);

        cmbCara.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Unggah Aplikasi", "Surat", "Surel", "WhatsApp", "Lainnya"}));
        cmbCara.setName("cmbCara"); // NOI18N
        panelGlass7.add(cmbCara);
        cmbCara.setBounds(824, 37, 150, 23);

        jLabel11.setText("Ka.Unit :");
        jLabel11.setName("jLabel11"); // NOI18N
        panelGlass7.add(jLabel11);
        jLabel11.setBounds(980, 37, 60, 23);

        cmbKepalaUnit.setName("cmbKepalaUnit"); // NOI18N
        panelGlass7.add(cmbKepalaUnit);
        cmbKepalaUnit.setBounds(1044, 37, 80, 23);

        // ---------------------------------------------------------- baris 3
        jLabel10.setText("Alasan Telat :");
        jLabel10.setName("jLabel10"); // NOI18N
        panelGlass7.add(jLabel10);
        jLabel10.setBounds(0, 64, 90, 23);

        TAlasan.setToolTipText("Wajib diisi bila penyampaian terlambat");
        TAlasan.setName("TAlasan"); // NOI18N
        panelGlass7.add(TAlasan);
        TAlasan.setBounds(94, 64, 420, 23);

        jLabel12.setText("Tindak Lanjut :");
        jLabel12.setName("jLabel12"); // NOI18N
        panelGlass7.add(jLabel12);
        jLabel12.setBounds(520, 64, 95, 23);

        TTindakLanjut.setToolTipText("Tindak lanjut oleh kepala unit yang bersangkutan");
        TTindakLanjut.setName("TTindakLanjut"); // NOI18N
        panelGlass7.add(TTindakLanjut);
        TTindakLanjut.setBounds(619, 64, 505, 23);

        internalFrame1.add(panelGlass7, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }

    // ------------------------------------------------------------- event
    private void formWindowOpened(java.awt.event.WindowEvent evt) {
        emptTeks();
        tampil();
    }

    private void formWindowActivated(java.awt.event.WindowEvent evt) {
        setLocationRelativeTo(getParent());
    }

    private void tbRegisterMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabMode.getRowCount() != 0) {
            getData();
        }
    }

    private void tbRegisterKeyPressed(java.awt.event.KeyEvent evt) {
        if (tabMode.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP)
                    || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                getData();
            }
        }
    }

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampil();
        }
    }

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {
        emptTeks();
    }

    // ------------------------------------------------------------- master
    /** Isi combo jenis laporan: "kode|nama|unit|tujuan|tenggat". */
    private void muatJenisLaporan() {
        cmbJenis.removeAllItems();
        try {
            ps = koneksi.prepareStatement("select kode_laporan,nama_laporan,unit,tujuan,tenggat_hari "
                    + "from laporan_jenis where status='1' order by nama_laporan");
            rs = ps.executeQuery();
            while (rs.next()) {
                cmbJenis.addItem(rs.getString("kode_laporan") + "|" + rs.getString("nama_laporan")
                        + "|" + rs.getString("unit") + "|" + rs.getString("tujuan") + "|" + rs.getInt("tenggat_hari"));
            }
        } catch (Exception e) {
            System.out.println("Notif muatJenisLaporan : " + e);
        } finally {
            tutup();
        }
    }

    private void muatPetugas() {
        cmbKepalaUnit.removeAllItems();
        cmbKepalaUnit.addItem("|-");
        try {
            ps = koneksi.prepareStatement("select nip,nama from petugas where status='1' order by nama");
            rs = ps.executeQuery();
            while (rs.next()) {
                cmbKepalaUnit.addItem(rs.getString("nip") + "|" + rs.getString("nama"));
            }
        } catch (Exception e) {
            System.out.println("Notif muatPetugas : " + e);
        } finally {
            tutup();
        }
    }

    private void tutup() {
        try {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private String bagian(widget.ComboBox c, int idx) {
        Object v = c.getSelectedItem();
        if (v == null) {
            return "";
        }
        String[] p = v.toString().split("\\|", -1);
        return idx < p.length ? p[idx] : "";
    }

    /** Jatuh tempo default = tanggal 1 bulan berjalan + tenggat_hari jenis laporan. */
    private void hitungJatuhTempoDefault() {
        if (TNoReg == null || !TNoReg.getText().trim().isEmpty()) {
            return; // sedang menampilkan data lama, jangan diubah
        }
        try {
            int tenggat = Integer.parseInt(bagian(cmbJenis, 4));
            java.util.Calendar c = java.util.Calendar.getInstance();
            c.set(java.util.Calendar.DAY_OF_MONTH, 1);
            c.add(java.util.Calendar.DAY_OF_MONTH, tenggat - 1);
            TglTempo.setDate(c.getTime());
        } catch (Exception e) {
            // biarkan tanggal apa adanya
        }
    }

    private String tglDb(widget.Tanggal t) {
        try {
            return FDB.format(FTAMPIL.parse(t.getSelectedItem() + ""));
        } catch (Exception e) {
            return FDB.format(new Date());
        }
    }

    /** Status dihitung dari tanggal, bukan diinput petugas. */
    private String[] hitungStatus(String tglTempo, String tglKirim, boolean adaKirim) {
        if (!adaKirim) {
            return new String[]{"Belum Dikirim", "0"};
        }
        try {
            long selisih = (FDB.parse(tglKirim).getTime() - FDB.parse(tglTempo).getTime()) / 86400000L;
            if (selisih > 0) {
                return new String[]{"Terlambat", String.valueOf(selisih)};
            }
            return new String[]{"Tepat Waktu", "0"};
        } catch (Exception e) {
            return new String[]{"Belum Dikirim", "0"};
        }
    }

    // ------------------------------------------------------------- tabel
    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                    "select r.no_register,r.kode_laporan,j.nama_laporan,j.unit,j.tujuan,r.periode_laporan,"
                    + "r.tgl_jatuh_tempo,r.tgl_kirim,r.jam_kirim,r.cara_kirim,r.status_kirim,r.hari_terlambat,"
                    + "r.alasan_terlambat,r.tindak_lanjut,r.nip_kepala_unit,ifnull(p.nama,'') as kepala_unit,"
                    + "r.keterangan,"
                    + "(select count(*) from laporan_register_bukti b where b.no_register=r.no_register) as jml_bukti "
                    + "from laporan_register r "
                    + "inner join laporan_jenis j on r.kode_laporan=j.kode_laporan "
                    + "left join petugas p on p.nip=r.nip_kepala_unit "
                    + "where r.tgl_jatuh_tempo between ? and ? and (r.no_register like ? or j.nama_laporan like ? "
                    + "or j.unit like ? or r.periode_laporan like ? or r.status_kirim like ?) "
                    + "order by r.tgl_jatuh_tempo, j.unit, j.nama_laporan");
            String cari = "%" + TCari.getText().trim() + "%";
            ps.setString(1, tglDb(Tgl1));
            ps.setString(2, tglDb(Tgl2));
            ps.setString(3, cari);
            ps.setString(4, cari);
            ps.setString(5, cari);
            ps.setString(6, cari);
            ps.setString(7, cari);
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new Object[]{
                    rs.getString("no_register"), rs.getString("kode_laporan"), rs.getString("nama_laporan"),
                    rs.getString("unit"), rs.getString("tujuan"), rs.getString("periode_laporan"),
                    rs.getString("tgl_jatuh_tempo"),
                    rs.getString("tgl_kirim") == null ? "" : rs.getString("tgl_kirim"),
                    rs.getString("jam_kirim") == null ? "" : rs.getString("jam_kirim"),
                    rs.getString("cara_kirim"), rs.getString("status_kirim"), rs.getInt("hari_terlambat"),
                    rs.getInt("jml_bukti"), rs.getString("alasan_terlambat"), rs.getString("tindak_lanjut"),
                    rs.getString("nip_kepala_unit"), rs.getString("kepala_unit"), rs.getString("keterangan")
                });
            }
        } catch (Exception e) {
            System.out.println("Notif tampil register : " + e);
        } finally {
            tutup();
        }
        LCount.setText("" + tabMode.getRowCount());
    }

    private void getData() {
        int r = tbRegister.getSelectedRow();
        if (r < 0) {
            return;
        }
        TNoReg.setText(tabMode.getValueAt(r, 0).toString());
        pilihCombo(cmbJenis, 0, tabMode.getValueAt(r, 1).toString());
        TPeriode.setText(tabMode.getValueAt(r, 5).toString());
        setTanggal(TglTempo, tabMode.getValueAt(r, 6).toString());
        String tglKirim = tabMode.getValueAt(r, 7).toString();
        if (!tglKirim.isEmpty()) {
            setTanggal(TglKirim, tglKirim);
        }
        TJam.setText(tabMode.getValueAt(r, 8).toString().isEmpty() ? "00:00:00" : tabMode.getValueAt(r, 8).toString());
        cmbCara.setSelectedItem(tabMode.getValueAt(r, 9).toString());
        TAlasan.setText(tabMode.getValueAt(r, 13).toString());
        TTindakLanjut.setText(tabMode.getValueAt(r, 14).toString());
        pilihCombo(cmbKepalaUnit, 0, tabMode.getValueAt(r, 15).toString());
        TKeterangan.setText(tabMode.getValueAt(r, 17).toString());
    }

    private void pilihCombo(widget.ComboBox c, int idx, String nilai) {
        for (int i = 0; i < c.getItemCount(); i++) {
            String[] p = c.getItemAt(i).toString().split("\\|", -1);
            if (idx < p.length && p[idx].equals(nilai)) {
                c.setSelectedIndex(i);
                return;
            }
        }
    }

    private void setTanggal(widget.Tanggal t, String tglDb) {
        try {
            t.setDate(FDB.parse(tglDb));
        } catch (Exception e) {
            // abaikan
        }
    }

    private void emptTeks() {
        TNoReg.setText("");
        TPeriode.setText("");
        TAlasan.setText("");
        TTindakLanjut.setText("");
        TKeterangan.setText("");
        TJam.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        tbRegister.clearSelection();
        hitungJatuhTempoDefault();
        TPeriode.requestFocus();
    }

    private String noRegisterBaru() {
        String prefix = "LR" + new SimpleDateFormat("yyMMdd").format(new Date());
        String maks = Sequel.cariIsi("select ifnull(max(no_register),'') from laporan_register "
                + "where no_register like '" + prefix + "%'");
        int urut = 1;
        if (maks != null && maks.length() == 12) {
            try {
                urut = Integer.parseInt(maks.substring(8)) + 1;
            } catch (NumberFormatException e) {
                urut = 1;
            }
        }
        return prefix + String.format("%04d", urut);
    }

    private boolean adaTglKirim() {
        return !(TglKirim.getSelectedItem() + "").trim().isEmpty();
    }

    /** Cek isian + aturan alasan wajib saat terlambat. Mengembalikan status atau null. */
    private String[] cekIsian() {
        if (cmbJenis.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(null, "Master jenis laporan masih kosong. Isi dulu tabel laporan_jenis..!!");
            return null;
        }
        if (TPeriode.getText().trim().equals("")) {
            Valid.textKosong(TPeriode, "Periode Laporan");
            return null;
        }
        String[] st = hitungStatus(tglDb(TglTempo), tglDb(TglKirim), adaTglKirim());
        if (st[0].equals("Terlambat") && TAlasan.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Penyampaian TERLAMBAT " + st[1]
                    + " hari. Alasan keterlambatan wajib diisi..!!");
            TAlasan.requestFocus();
            return null;
        }
        return st;
    }

    private void isiParam(PreparedStatement p, String[] st, boolean insert, String no) throws Exception {
        int i = 1;
        if (insert) {
            p.setString(i++, no);
        }
        p.setString(i++, bagian(cmbJenis, 0));
        p.setString(i++, TPeriode.getText().trim());
        p.setString(i++, tglDb(TglTempo));
        if (adaTglKirim()) {
            p.setString(i++, tglDb(TglKirim));
            p.setString(i++, TJam.getText().trim());
        } else {
            p.setNull(i++, java.sql.Types.DATE);
            p.setNull(i++, java.sql.Types.TIME);
        }
        p.setString(i++, cmbCara.getSelectedItem() + "");
        p.setString(i++, st[0]);
        p.setInt(i++, Integer.parseInt(st[1]));
        p.setString(i++, TAlasan.getText().trim());
        p.setString(i++, TTindakLanjut.getText().trim());
        p.setString(i++, akses.getkode());
        p.setString(i++, bagian(cmbKepalaUnit, 0));
        p.setString(i++, TKeterangan.getText().trim());
        if (!insert) {
            p.setString(i, TNoReg.getText().trim());
        }
    }

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
        String[] st = cekIsian();
        if (st == null) {
            return;
        }
        String no = noRegisterBaru();
        try {
            ps = koneksi.prepareStatement(
                    "insert into laporan_register (no_register,kode_laporan,periode_laporan,tgl_jatuh_tempo,"
                    + "tgl_kirim,jam_kirim,cara_kirim,status_kirim,hari_terlambat,alasan_terlambat,tindak_lanjut,"
                    + "nip_pengirim,nip_kepala_unit,keterangan,tgl_input) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,now())");
            isiParam(ps, st, true, no);
            ps.executeUpdate();
            TNoReg.setText(no);
            tampil();
            JOptionPane.showMessageDialog(null, "Tersimpan dengan No.Register " + no
                    + "\nStatus : " + st[0] + "\n\nJangan lupa lampirkan bukti penyampaiannya lewat tombol Bukti.");
        } catch (Exception e) {
            System.out.println("Notif simpan register : " + e);
            JOptionPane.showMessageDialog(null, "Gagal menyimpan : " + e);
        } finally {
            tutup();
        }
    }

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {
        if (TNoReg.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Silahkan pilih dulu data yang mau diganti..!!");
            return;
        }
        String[] st = cekIsian();
        if (st == null) {
            return;
        }
        try {
            ps = koneksi.prepareStatement(
                    "update laporan_register set kode_laporan=?,periode_laporan=?,tgl_jatuh_tempo=?,tgl_kirim=?,"
                    + "jam_kirim=?,cara_kirim=?,status_kirim=?,hari_terlambat=?,alasan_terlambat=?,tindak_lanjut=?,"
                    + "nip_pengirim=?,nip_kepala_unit=?,keterangan=? where no_register=?");
            isiParam(ps, st, false, null);
            ps.executeUpdate();
            tampil();
            JOptionPane.showMessageDialog(null, "Data diperbarui. Status : " + st[0]);
        } catch (Exception e) {
            System.out.println("Notif ubah register : " + e);
            JOptionPane.showMessageDialog(null, "Gagal mengubah : " + e);
        } finally {
            tutup();
        }
    }

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {
        if (TNoReg.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Silahkan pilih dulu data yang mau dihapus..!!");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Hapus register " + TNoReg.getText()
                + " beserta seluruh buktinya?", "Konfirmasi", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            ps = koneksi.prepareStatement("delete from laporan_register where no_register=?");
            ps.setString(1, TNoReg.getText().trim());
            ps.executeUpdate();
            emptTeks();
            tampil();
        } catch (Exception e) {
            System.out.println("Notif hapus register : " + e);
            JOptionPane.showMessageDialog(null, "Gagal menghapus : " + e);
        } finally {
            tutup();
        }
    }

    // ------------------------------------------------------------------ bukti
    private void BtnBuktiActionPerformed(java.awt.event.ActionEvent evt) {
        if (TNoReg.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Silahkan pilih dulu register yang mau dilampiri bukti..!!");
            return;
        }
        DlgBuktiPenyampaian bukti = new DlgBuktiPenyampaian(this, true, TNoReg.getText().trim());
        bukti.setSize(internalFrame1.getWidth() - 120, 340);
        bukti.setLocationRelativeTo(internalFrame1);
        bukti.setVisible(true);
        tampil();
    }

    /** Dialog pengelola bukti penyampaian satu register, gaya Khanza. */
    private final class DlgBuktiPenyampaian extends javax.swing.JDialog {

        private final String noRegister;
        private final DefaultTableModel mode;
        private final widget.Table tabel;
        private final widget.ComboBox cmbJenisBukti;
        private final widget.TextBox TKet;

        DlgBuktiPenyampaian(java.awt.Dialog induk, boolean modal, String noRegister) {
            super(induk, modal);
            this.noRegister = noRegister;
            setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
            setUndecorated(true);
            setResizable(false);

            widget.InternalFrame frame = new widget.InternalFrame();
            frame.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
                    "::[ Bukti Penyampaian Laporan - " + noRegister + " ]::",
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION,
                    new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));
            frame.setLayout(new java.awt.BorderLayout(1, 1));

            mode = new DefaultTableModel(null, new Object[]{"No", "Jenis Bukti", "Berkas", "Keterangan", "Tgl.Upload"}) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
            tabel = new widget.Table();
            tabel.setModel(mode);
            tabel.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            int[] w = {40, 140, 380, 200, 130};
            for (int i = 0; i < w.length; i++) {
                tabel.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
            }
            tabel.setDefaultRenderer(Object.class, new WarnaTable());
            widget.ScrollPane sc = new widget.ScrollPane();
            sc.setViewportView(tabel);
            frame.add(sc, java.awt.BorderLayout.CENTER);

            widget.panelisi atas = new widget.panelisi();
            atas.setPreferredSize(new java.awt.Dimension(44, 44));
            atas.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));
            widget.Label l1 = new widget.Label();
            l1.setText("Jenis Bukti :");
            l1.setPreferredSize(new java.awt.Dimension(80, 23));
            atas.add(l1);
            cmbJenisBukti = new widget.ComboBox();
            cmbJenisBukti.setModel(new javax.swing.DefaultComboBoxModel(
                    new String[]{"Tangkapan Layar", "Tanda Terima Surat", "Bukti Surel", "Lainnya"}));
            cmbJenisBukti.setPreferredSize(new java.awt.Dimension(160, 23));
            atas.add(cmbJenisBukti);
            widget.Label l2 = new widget.Label();
            l2.setText("Keterangan :");
            l2.setPreferredSize(new java.awt.Dimension(80, 23));
            atas.add(l2);
            TKet = new widget.TextBox();
            TKet.setDocument(new batasInput((byte) 200).getKata(TKet));
            TKet.setPreferredSize(new java.awt.Dimension(260, 23));
            atas.add(TKet);
            frame.add(atas, java.awt.BorderLayout.PAGE_START);

            widget.panelisi bawah = new widget.panelisi();
            bawah.setPreferredSize(new java.awt.Dimension(44, 44));
            bawah.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));
            bawah.add(tombol("Tambah Berkas", 'T', "/picture/save-16x16.png", e -> tambahBukti(), 140));
            bawah.add(tombol("Buka", 'A', "/picture/190.png", e -> bukaFile(), 100));
            bawah.add(tombol("Hapus", 'H', "/picture/delete-16x16.png", e -> hapusBukti(), 100));
            bawah.add(tombol("Keluar", 'K', "/picture/logout.png", e -> dispose(), 100));
            frame.add(bawah, java.awt.BorderLayout.PAGE_END);

            getContentPane().add(frame, java.awt.BorderLayout.CENTER);
            muat();
        }

        private widget.Button tombol(String teks, char mnemonic, String ikon,
                java.awt.event.ActionListener aksi, int lebar) {
            widget.Button b = new widget.Button();
            b.setIcon(new javax.swing.ImageIcon(getClass().getResource(ikon)));
            b.setText(teks);
            b.setMnemonic(mnemonic);
            b.setToolTipText("Alt+" + mnemonic);
            b.setPreferredSize(new java.awt.Dimension(lebar, 30));
            b.addActionListener(aksi);
            return b;
        }

        private void muat() {
            Valid.tabelKosong(mode);
            try (PreparedStatement p = koneksi.prepareStatement(
                    "select no_urut,jenis_bukti,lokasi_file,keterangan,tgl_upload from laporan_register_bukti "
                    + "where no_register=? order by no_urut")) {
                p.setString(1, noRegister);
                ResultSet r = p.executeQuery();
                while (r.next()) {
                    mode.addRow(new Object[]{r.getInt("no_urut"), r.getString("jenis_bukti"),
                        r.getString("lokasi_file"), r.getString("keterangan"),
                        r.getString("tgl_upload") == null ? "" : r.getString("tgl_upload")});
                }
                r.close();
            } catch (Exception e) {
                System.out.println("Notif muat bukti : " + e);
            }
        }

        private void tambahBukti() {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Pilih berkas bukti (tangkapan layar / scan tanda terima / bukti surel)");
            if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File asal = fc.getSelectedFile();
            try {
                File folder = new File(FOLDER_BUKTI);
                if (!folder.exists() && !folder.mkdirs()) {
                    JOptionPane.showMessageDialog(null, "Gagal membuat folder " + folder.getAbsolutePath());
                    return;
                }
                String nama = asal.getName();
                String ext = nama.lastIndexOf('.') > 0 ? nama.substring(nama.lastIndexOf('.')) : "";
                int urut = mode.getRowCount() + 1;
                String tujuan = FOLDER_BUKTI + File.separator + noRegister + "_" + urut + ext;
                Files.copy(asal.toPath(), new File(tujuan).toPath(), StandardCopyOption.REPLACE_EXISTING);
                try (PreparedStatement p = koneksi.prepareStatement(
                        "insert into laporan_register_bukti (no_register,no_urut,jenis_bukti,lokasi_file,keterangan,tgl_upload) "
                        + "values (?,?,?,?,?,now())")) {
                    p.setString(1, noRegister);
                    p.setInt(2, urut);
                    p.setString(3, cmbJenisBukti.getSelectedItem() + "");
                    p.setString(4, tujuan);
                    p.setString(5, TKet.getText().trim());
                    p.executeUpdate();
                }
                TKet.setText("");
                muat();
            } catch (Exception e) {
                System.out.println("Notif tambah bukti : " + e);
                JOptionPane.showMessageDialog(null, "Gagal menyalin berkas : " + e);
            }
        }

        private void bukaFile() {
            int r = tabel.getSelectedRow();
            if (r < 0) {
                JOptionPane.showMessageDialog(null, "Silahkan pilih dulu berkasnya..!!");
                return;
            }
            try {
                File f = new File(mode.getValueAt(r, 2).toString());
                if (!f.exists()) {
                    JOptionPane.showMessageDialog(null, "Berkas tidak ditemukan : " + f.getAbsolutePath());
                    return;
                }
                Desktop.getDesktop().open(f);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Gagal membuka berkas : " + e);
            }
        }

        private void hapusBukti() {
            int r = tabel.getSelectedRow();
            if (r < 0) {
                JOptionPane.showMessageDialog(null, "Silahkan pilih dulu berkasnya..!!");
                return;
            }
            try (PreparedStatement p = koneksi.prepareStatement(
                    "delete from laporan_register_bukti where no_register=? and no_urut=?")) {
                p.setString(1, noRegister);
                p.setInt(2, Integer.parseInt(mode.getValueAt(r, 0).toString()));
                p.executeUpdate();
                muat();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Gagal menghapus : " + e);
            }
        }
    }

    // ------------------------------------------------------------------ cetak
    private Map<String, Object> paramRs() {
        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        return param;
    }

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print..!!");
            return;
        }
        Map<String, Object> param = paramRs();
        param.put("periode", "Jatuh Tempo " + Tgl1.getSelectedItem() + " s.d. " + Tgl2.getSelectedItem());
        Valid.MyReportqry("rptRegisterPenyampaianLaporan.jasper", "report",
                "::[ Register Penyampaian Laporan ]::",
                "select r.no_register,j.nama_laporan,j.unit,j.tujuan,r.periode_laporan,"
                + "date_format(r.tgl_jatuh_tempo,'%d-%m-%Y') as tgl_jatuh_tempo,"
                + "ifnull(date_format(r.tgl_kirim,'%d-%m-%Y'),'') as tgl_kirim,"
                + "r.cara_kirim,r.status_kirim,r.hari_terlambat,r.alasan_terlambat,r.tindak_lanjut,"
                + "ifnull(p.nama,'') as kepala_unit,"
                + "(select count(*) from laporan_register_bukti b where b.no_register=r.no_register) as jml_bukti "
                + "from laporan_register r "
                + "inner join laporan_jenis j on r.kode_laporan=j.kode_laporan "
                + "left join petugas p on p.nip=r.nip_kepala_unit "
                + "where r.tgl_jatuh_tempo between '" + tglDb(Tgl1) + "' and '" + tglDb(Tgl2) + "' "
                + "order by j.unit,r.tgl_jatuh_tempo,j.nama_laporan", param);
    }

    /** Evaluasi ketepatan waktu per semester, per unit — untuk Direktur. */
    private void BtnEvaluasiActionPerformed(java.awt.event.ActionEvent evt) {
        String[] pilihan = {"Semester I (Januari - Juni)", "Semester II (Juli - Desember)"};
        Object sem = JOptionPane.showInputDialog(this, "Pilih semester :", "Evaluasi Ketepatan Waktu Pelaporan",
                JOptionPane.QUESTION_MESSAGE, null, pilihan, pilihan[0]);
        if (sem == null) {
            return;
        }
        String thn = JOptionPane.showInputDialog(this, "Tahun :", new SimpleDateFormat("yyyy").format(new Date()));
        if (thn == null || !thn.trim().matches("\\d{4}")) {
            return;
        }
        boolean sem1 = sem.toString().startsWith("Semester I ");
        String awal = thn.trim() + (sem1 ? "-01-01" : "-07-01");
        String akhir = thn.trim() + (sem1 ? "-06-30" : "-12-31");

        Map<String, Object> param = paramRs();
        param.put("periode", (sem1 ? "Semester I" : "Semester II") + " Tahun " + thn.trim()
                + "  (" + awal + " s.d. " + akhir + ")");
        Valid.MyReportqry("rptEvaluasiKetepatanLaporan.jasper", "report",
                "::[ Evaluasi Ketepatan Waktu Penyampaian Laporan ]::",
                "select j.unit,j.nama_laporan,"
                + "count(*) as jml_wajib,"
                + "sum(case when r.status_kirim='Tepat Waktu' then 1 else 0 end) as jml_tepat,"
                + "sum(case when r.status_kirim='Terlambat' then 1 else 0 end) as jml_telat,"
                + "sum(case when r.status_kirim='Belum Dikirim' then 1 else 0 end) as jml_belum,"
                + "round(sum(case when r.status_kirim='Tepat Waktu' then 1 else 0 end)/count(*)*100,2) as persen_tepat,"
                + "ifnull(round(avg(case when r.status_kirim='Terlambat' then r.hari_terlambat end),1),0) as rerata_telat "
                + "from laporan_register r inner join laporan_jenis j on r.kode_laporan=j.kode_laporan "
                + "where r.tgl_jatuh_tempo between '" + awal + "' and '" + akhir + "' "
                + "group by j.unit,j.nama_laporan order by j.unit,j.nama_laporan", param);
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getregister_laporan());
        BtnEdit.setEnabled(akses.getregister_laporan());
        BtnHapus.setEnabled(akses.getregister_laporan());
        BtnBukti.setEnabled(akses.getregister_laporan());
    }

    // Variables declaration
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnBukti;
    private widget.Button BtnCari;
    private widget.Button BtnEdit;
    private widget.Button BtnEvaluasi;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.Label LCount;
    private widget.ScrollPane Scroll;
    private widget.TextBox TAlasan;
    private widget.TextBox TCari;
    private widget.TextBox TJam;
    private widget.TextBox TKeterangan;
    private widget.TextBox TNoReg;
    private widget.TextBox TPeriode;
    private widget.TextBox TTindakLanjut;
    private widget.Tanggal Tgl1;
    private widget.Tanggal Tgl2;
    private widget.Tanggal TglKirim;
    private widget.Tanggal TglTempo;
    private widget.ComboBox cmbCara;
    private widget.ComboBox cmbJenis;
    private widget.ComboBox cmbKepalaUnit;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel1;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel2;
    private widget.Label jLabel20;
    private widget.Label jLabel21;
    private widget.Label jLabel3;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private widget.panelisi jPanel3;
    private widget.panelisi panelGlass7;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbRegister;
    // End of variables declaration
}
