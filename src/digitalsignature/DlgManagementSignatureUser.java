package digitalsignature;

import bridging.EsignServiceV2;
import fungsi.WarnaTableTTE;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariDokter;
import kepegawaian.DlgCariPetugas;

/**
 * Modul Manajemen & Monitoring Sertifikat TTE Dokter & Pegawai BSrE
 */
public class DlgManagementSignatureUser extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private Connection koneksi = koneksiDB.condb();
    public DlgCariDokter dokter = new DlgCariDokter(null, false);
    public DlgCariPetugas petugas = new DlgCariPetugas(null, false);
    private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * Creates new form DlgManagementSignatureUser
     *
     * @param parent
     * @param modal
     */
    public DlgManagementSignatureUser(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(980, 520);

        tambahPanel.setVisible(false);

        Object[] row = {"P", "Kode/NIP", "Nama Dokter / Pegawai", "NIK KTP", "Tipe", "Jabatan / Spesialis", "Kode Status", "Status TTE", "Pesan BSrE", "Terakhir Diperbaharui"};
        tabMode = new DefaultTableModel(null, row) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }

            Class[] types = new Class[]{
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class,
                java.lang.Object.class, java.lang.Object.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbMapingPetugas.setModel(tabMode);
        tbMapingPetugas.setDefaultRenderer(Object.class, new WarnaTableTTE());
        tbMapingPetugas.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbMapingPetugas.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 10; i++) {
            TableColumn column = tbMapingPetugas.getColumnModel().getColumn(i);
            switch (i) {
                case 0:
                    column.setPreferredWidth(35);
                    break;
                case 1:
                    column.setPreferredWidth(90);
                    break;
                case 2:
                    column.setPreferredWidth(230);
                    break;
                case 3:
                    column.setPreferredWidth(145);
                    break;
                case 4:
                    column.setPreferredWidth(75);
                    break;
                case 5:
                    column.setPreferredWidth(150);
                    break;
                case 6:
                    column.setPreferredWidth(75);
                    break;
                case 7:
                    column.setPreferredWidth(140);
                    break;
                case 8:
                    column.setPreferredWidth(280);
                    break;
                case 9:
                    column.setPreferredWidth(145);
                    break;
                default:
                    column.setPreferredWidth(120);
                    break;
            }
        }

        petugas.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}

            @Override
            public void windowClosing(WindowEvent e) {}

            @Override
            public void windowClosed(WindowEvent e) {
                if (petugas.getTable().getSelectedRow() != -1) {
                    kdPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                    nik.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 11).toString());
                    nmPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
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

        tampil();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        ppSemua = new javax.swing.JMenuItem();
        ppBersihkan = new javax.swing.JMenuItem();
        jSeparatorMenu1 = new javax.swing.JPopupMenu.Separator();
        ppUpdateTerpilih = new javax.swing.JMenuItem();
        ppResetPassphrase = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        panelGlass8 = new widget.panelisi();
        jLabelFilter = new widget.Label();
        CmbFilter = new widget.ComboBox();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnSemua = new widget.Button();
        BtnUpdateStatus = new widget.Button();
        BtnResetPassphrase = new widget.Button();
        BtnKeluar = new widget.Button();
        intMapingDokter = new widget.ScrollPane();
        tbMapingPetugas = new widget.Table();
        tambahPanel = new widget.panelisi();
        jLabel37 = new widget.Label();
        nmPetugas = new widget.TextBox();
        kdPetugas = new widget.TextBox();
        jLabel38 = new widget.Label();
        nik = new widget.TextBox();
        BtnTambahPetugas = new widget.Button();
        BtnDataPetugas = new widget.Button();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        ppSemua.setBackground(new java.awt.Color(255, 255, 254));
        ppSemua.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppSemua.setForeground(new java.awt.Color(70, 70, 70));
        ppSemua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppSemua.setText("Pilih Semua");
        ppSemua.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppSemua.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppSemua.setName("ppSemua"); // NOI18N
        ppSemua.setPreferredSize(new java.awt.Dimension(220, 25));
        ppSemua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppSemuaActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppSemua);

        ppBersihkan.setBackground(new java.awt.Color(255, 255, 254));
        ppBersihkan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppBersihkan.setForeground(new java.awt.Color(70, 70, 70));
        ppBersihkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppBersihkan.setText("Bersihkan Pilihan");
        ppBersihkan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppBersihkan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppBersihkan.setName("ppBersihkan"); // NOI18N
        ppBersihkan.setPreferredSize(new java.awt.Dimension(220, 25));
        ppBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppBersihkanActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppBersihkan);
        jPopupMenu1.add(jSeparatorMenu1);

        ppUpdateTerpilih.setBackground(new java.awt.Color(255, 255, 254));
        ppUpdateTerpilih.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppUpdateTerpilih.setForeground(new java.awt.Color(70, 70, 70));
        ppUpdateTerpilih.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/upload24.png"))); // NOI18N
        ppUpdateTerpilih.setText("Perbaharui Status TTE (BSrE)");
        ppUpdateTerpilih.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppUpdateTerpilih.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppUpdateTerpilih.setName("ppUpdateTerpilih"); // NOI18N
        ppUpdateTerpilih.setPreferredSize(new java.awt.Dimension(220, 25));
        ppUpdateTerpilih.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnUpdateStatusActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppUpdateTerpilih);

        ppResetPassphrase.setBackground(new java.awt.Color(255, 255, 254));
        ppResetPassphrase.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppResetPassphrase.setForeground(new java.awt.Color(70, 70, 70));
        ppResetPassphrase.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/lock.png"))); // NOI18N
        ppResetPassphrase.setText("Reset Passphrase Akun BSrE");
        ppResetPassphrase.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppResetPassphrase.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppResetPassphrase.setName("ppResetPassphrase"); // NOI18N
        ppResetPassphrase.setPreferredSize(new java.awt.Dimension(220, 25));
        ppResetPassphrase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnResetPassphraseActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppResetPassphrase);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true), "::[ Manajemen & Monitoring Sertifikat TTE Dokter & Pegawai (BSrE) ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(70, 70, 70))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setPreferredSize(new java.awt.Dimension(875, 200));
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(100, 56));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabelFilter.setText("Tipe :");
        jLabelFilter.setName("jLabelFilter"); // NOI18N
        jLabelFilter.setPreferredSize(new java.awt.Dimension(40, 23));
        panelGlass8.add(jLabelFilter);

        CmbFilter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semua", "Dokter Saja", "Petugas Saja" }));
        CmbFilter.setName("CmbFilter"); // NOI18N
        CmbFilter.setPreferredSize(new java.awt.Dimension(110, 23));
        CmbFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tampil();
            }
        });
        panelGlass8.add(CmbFilter);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(180, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    tampil();
                }
            }
        });
        panelGlass8.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('7');
        BtnCari.setToolTipText("Alt+7, Cari Data");
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tampil();
            }
        });
        panelGlass8.add(BtnCari);

        BtnSemua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search.png"))); // NOI18N
        BtnSemua.setMnemonic('T');
        BtnSemua.setText("Semua");
        BtnSemua.setToolTipText("Alt+T, Tampilkan Semua");
        BtnSemua.setName("BtnSemua"); // NOI18N
        BtnSemua.setPreferredSize(new java.awt.Dimension(85, 30));
        BtnSemua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TCari.setText("");
                tampil();
            }
        });
        panelGlass8.add(BtnSemua);

        BtnUpdateStatus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/upload24.png"))); // NOI18N
        BtnUpdateStatus.setMnemonic('S');
        BtnUpdateStatus.setText("Perbaharui Status TTE");
        BtnUpdateStatus.setToolTipText("Alt+S, Cek Status Akun & Sertifikat ke BSrE");
        BtnUpdateStatus.setGlassColor(new java.awt.Color(0, 153, 0));
        BtnUpdateStatus.setName("BtnUpdateStatus"); // NOI18N
        BtnUpdateStatus.setPreferredSize(new java.awt.Dimension(175, 30));
        BtnUpdateStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnUpdateStatusActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnUpdateStatus);

        BtnResetPassphrase.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/lock.png"))); // NOI18N
        BtnResetPassphrase.setMnemonic('R');
        BtnResetPassphrase.setText("Reset Passphrase");
        BtnResetPassphrase.setToolTipText("Alt+R, Kirim Tautan Reset Kata Sandi Resmi ke Email BSrE");
        BtnResetPassphrase.setGlassColor(new java.awt.Color(255, 153, 0));
        BtnResetPassphrase.setName("BtnResetPassphrase"); // NOI18N
        BtnResetPassphrase.setPreferredSize(new java.awt.Dimension(150, 30));
        BtnResetPassphrase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnResetPassphraseActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnResetPassphrase);

        BtnLogTte = new widget.Button();
        java.net.URL urlLogTte = getClass().getResource("/picture/History.png");
        if (urlLogTte != null) {
            BtnLogTte.setIcon(new javax.swing.ImageIcon(urlLogTte)); // NOI18N
        }
        BtnLogTte.setMnemonic('L');
        BtnLogTte.setText("Log TTE");
        BtnLogTte.setToolTipText("Alt+L, Buka Riwayat Log Aktivitas TTE BSrE");
        BtnLogTte.setName("BtnLogTte"); // NOI18N
        BtnLogTte.setPreferredSize(new java.awt.Dimension(95, 30));
        BtnLogTte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                digitalsignature.DlgListLogTte dlg = new digitalsignature.DlgListLogTte(null, false);
                dlg.setSize(850, 520);
                dlg.setLocationRelativeTo(internalFrame1);
                dlg.setVisible(true);
            }
        });
        panelGlass8.add(BtnLogTte);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/cross.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K, Tutup Dialog");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(85, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispose();
            }
        });
        panelGlass8.add(BtnKeluar);

        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        intMapingDokter.setComponentPopupMenu(jPopupMenu1);
        intMapingDokter.setName("intMapingDokter"); // NOI18N
        intMapingDokter.setOpaque(true);

        tbMapingPetugas.setAutoCreateRowSorter(true);
        tbMapingPetugas.setToolTipText("Klik kanan untuk opsi seleksi dan aksi BSrE");
        tbMapingPetugas.setComponentPopupMenu(jPopupMenu1);
        tbMapingPetugas.setName("tbMapingPetugas"); // NOI18N
        intMapingDokter.setViewportView(tbMapingPetugas);

        internalFrame1.add(intMapingDokter, java.awt.BorderLayout.CENTER);

        tambahPanel.setMinimumSize(new java.awt.Dimension(531, 200));
        tambahPanel.setName("tambahPanel"); // NOI18N
        tambahPanel.setPreferredSize(new java.awt.Dimension(200, 80));
        tambahPanel.setLayout(null);

        jLabel37.setText("Petugas :");
        jLabel37.setName("jLabel37"); // NOI18N
        tambahPanel.add(jLabel37);
        jLabel37.setBounds(0, 10, 80, 23);

        nmPetugas.setEditable(false);
        nmPetugas.setName("nmPetugas"); // NOI18N
        tambahPanel.add(nmPetugas);
        nmPetugas.setBounds(220, 10, 260, 23);

        kdPetugas.setEditable(false);
        kdPetugas.setName("kdPetugas"); // NOI18N
        tambahPanel.add(kdPetugas);
        kdPetugas.setBounds(90, 10, 130, 23);

        jLabel38.setText("NIK :");
        jLabel38.setName("jLabel38"); // NOI18N
        tambahPanel.add(jLabel38);
        jLabel38.setBounds(0, 40, 80, 23);

        nik.setEditable(false);
        nik.setName("nik"); // NOI18N
        tambahPanel.add(nik);
        nik.setBounds(90, 40, 390, 23);

        BtnTambahPetugas.setText("Tambah");
        BtnTambahPetugas.setName("BtnTambahPetugas"); // NOI18N
        tambahPanel.add(BtnTambahPetugas);
        BtnTambahPetugas.setBounds(490, 40, 130, 30);

        BtnDataPetugas.setName("BtnDataPetugas"); // NOI18N
        tambahPanel.add(BtnDataPetugas);
        BtnDataPetugas.setBounds(490, 10, 30, 20);

        internalFrame1.add(tambahPanel, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ppSemuaActionPerformed(java.awt.event.ActionEvent evt) {
        for (i = 0; i < tbMapingPetugas.getRowCount(); i++) {
            tbMapingPetugas.setValueAt(true, i, 0);
        }
    }

    private void ppBersihkanActionPerformed(java.awt.event.ActionEvent evt) {
        for (i = 0; i < tbMapingPetugas.getRowCount(); i++) {
            tbMapingPetugas.setValueAt(false, i, 0);
        }
    }

    /**
     * Memeriksa dan memperbarui status akun & sertifikat BSrE
     */
    private void BtnUpdateStatusActionPerformed(java.awt.event.ActionEvent evt) {
        List<Integer> rowsToProcess = new ArrayList<>();
        for (int row = 0; row < tbMapingPetugas.getRowCount(); row++) {
            Boolean checked = (Boolean) tbMapingPetugas.getValueAt(row, 0);
            if (checked != null && checked) {
                rowsToProcess.add(row);
            }
        }

        if (rowsToProcess.isEmpty()) {
            if (tbMapingPetugas.getSelectedRow() != -1) {
                rowsToProcess.add(tbMapingPetugas.getSelectedRow());
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Silakan centang pada kolom 'P' atau pilih baris dokter/petugas yang ingin diperiksa statusnya.", 
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Periksa status akun dan sertifikat TTE BSrE untuk " + rowsToProcess.size() + " data yang dipilih?", 
            "Konfirmasi Pengecekan BSrE", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        EsignServiceV2 esign = new EsignServiceV2();
        int successCount = 0;
        int errorCount = 0;
        int total = rowsToProcess.size();
        int idx = 0;

        System.out.println("=== Perbaharui Status TTE BSrE: mulai (" + total + " data) ===");
        for (int r : rowsToProcess) {
            idx++;
            String nip = tbMapingPetugas.getValueAt(r, 1).toString();
            String nama = tbMapingPetugas.getValueAt(r, 2).toString();
            String nikKtp = tbMapingPetugas.getValueAt(r, 3) != null ? tbMapingPetugas.getValueAt(r, 3).toString().trim() : "";

            System.out.println("[" + idx + "/" + total + "] " + nama + " (NIP " + nip + ", NIK " + nikKtp + ") ...");

            if (nikKtp.isEmpty() || nikKtp.equals("0") || nikKtp.length() != 16) {
                System.out.println("    -> dilewati: NIK KTP tidak valid");
                errorCount++;
                continue;
            }

            EsignServiceV2.StatusResult res = esign.checkStatus(nikKtp);
            System.out.println("    -> status: " + res.status + " (kode " + res.code + ")"
                    + (res.message != null && !res.message.isEmpty() ? " - " + res.message : ""));
            String now = dateformat.format(new Date());

            try {
                Boolean isExist = !Sequel.cariIsi("SELECT nip FROM akun_tte WHERE nip = '" + nip + "'").equals("");
                if (isExist) {
                    Sequel.queryu2("UPDATE akun_tte SET kd_status = ?, status = ?, pesan = ?, waktu_pembaharuan = ? WHERE nip = '" + nip + "'", 4,
                        new String[]{String.valueOf(res.code), res.status, res.message, now}
                    );
                } else {
                    Sequel.menyimpantf("akun_tte", "?,?,?,?,?", "nip", 5,
                        new String[]{nip, res.status, res.message, String.valueOf(res.code), now}
                    );
                }
                successCount++;
            } catch (Exception ex) {
                System.err.println("    -> GAGAL menyimpan akun_tte: " + ex.getMessage());
                errorCount++;
            }
        }
        System.out.println("=== Perbaharui Status TTE BSrE: selesai — berhasil " + successCount + ", gagal/invalid " + errorCount + " ===");

        this.setCursor(Cursor.getDefaultCursor());
        tampil();
        JOptionPane.showMessageDialog(this, 
            "Pemeriksaan status akun TTE BSrE selesai!\n" +
            "- Berhasil diperbaharui: " + successCount + " pengguna\n" +
            (errorCount > 0 ? "- Gagal/NIK tidak valid: " + errorCount + " pengguna" : ""),
            "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Meminta reset passphrase resmi BSrE ke email pegawai/dokter
     */
    private void BtnResetPassphraseActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = tbMapingPetugas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Silakan pilih satu baris dokter/petugas yang ingin di-reset passphrasenya.", 
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nama = tbMapingPetugas.getValueAt(selectedRow, 2).toString();
        String nikKtp = tbMapingPetugas.getValueAt(selectedRow, 3) != null ? tbMapingPetugas.getValueAt(selectedRow, 3).toString().trim() : "";

        if (nikKtp.isEmpty() || nikKtp.equals("0") || nikKtp.length() != 16) {
            JOptionPane.showMessageDialog(this, 
                "NIK KTP tidak valid (" + nikKtp + "). NIK KTP wajib 16 digit terdaftar di data Kepegawaian.", 
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Apakah Anda yakin ingin mengirim tautan RESET PASSPHRASE BSrE untuk:\n\n" +
            "Nama    : " + nama + "\n" +
            "NIK KTP : " + nikKtp + "\n\n" +
            "Sistem BSrE akan mengirimkan link reset resmi ke email pemilik akun.\nLanjutkan?", 
            "Konfirmasi Reset Passphrase", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        EsignServiceV2 esign = new EsignServiceV2();
        EsignServiceV2.ResetResult res = esign.requestResetPassphrase(nikKtp);
        this.setCursor(Cursor.getDefaultCursor());

        if (res.success) {
            JOptionPane.showMessageDialog(this, 
                "Permintaan Reset Passphrase Berhasil!\n\n" + res.message, 
                "Sukses BSrE", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Gagal Meminta Reset Passphrase:\n\n" + res.message, 
                "Gagal BSrE", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Menampilkan data monitoring dokter dan pegawai beserta status akun TTE
     */
    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            String cari = TCari.getText().trim();
            String filterTipe = CmbFilter.getSelectedItem().toString();

            StringBuilder sql = new StringBuilder();
            sql.append("SELECT p.nik, p.nama, p.no_ktp, ")
               .append("IF(d.kd_dokter IS NOT NULL, 'Dokter', 'Petugas') as tipe, ")
               .append("IFNULL(s.nm_sps, IFNULL(j.nm_jbtn, p.jbtn)) as jabatan, ")
               .append("IFNULL(a.kd_status, '-') as kd_status, ")
               .append("IFNULL(a.status, 'Belum Diperiksa') as status, ")
               .append("IFNULL(a.pesan, '-') as pesan, ")
               .append("IFNULL(a.waktu_pembaharuan, '-') as waktu_pembaharuan ")
               .append("FROM pegawai p ")
               .append("LEFT JOIN dokter d ON p.nik = d.kd_dokter ")
               .append("LEFT JOIN spesialis s ON d.kd_sps = s.kd_sps ")
               .append("LEFT JOIN petugas pt ON p.nik = pt.nip ")
               .append("LEFT JOIN jabatan j ON pt.kd_jbtn = j.kd_jbtn ")
               .append("LEFT JOIN akun_tte a ON p.nik = a.nip ")
               .append("WHERE p.stts_aktif = 'AKTIF' ");

            if (filterTipe.equalsIgnoreCase("Dokter Saja")) {
                sql.append("AND d.kd_dokter IS NOT NULL ");
            } else if (filterTipe.equalsIgnoreCase("Petugas Saja")) {
                sql.append("AND d.kd_dokter IS NULL ");
            }

            if (!cari.isEmpty()) {
                sql.append("AND (p.nik LIKE ? OR p.nama LIKE ? OR p.no_ktp LIKE ? OR IFNULL(s.nm_sps, IFNULL(j.nm_jbtn, p.jbtn)) LIKE ?) ");
            }

            sql.append("ORDER BY tipe ASC, p.nama ASC");

            ps = koneksi.prepareStatement(sql.toString());
            if (!cari.isEmpty()) {
                String keyword = "%" + cari + "%";
                ps.setString(1, keyword);
                ps.setString(2, keyword);
                ps.setString(3, keyword);
                ps.setString(4, keyword);
            }

            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new Object[]{
                        false,
                        rs.getString("nik"),
                        rs.getString("nama"),
                        rs.getString("no_ktp"),
                        rs.getString("tipe"),
                        rs.getString("jabatan"),
                        rs.getString("kd_status"),
                        rs.getString("status"),
                        rs.getString("pesan"),
                        rs.getString("waktu_pembaharuan")
                    });
                }
            } finally {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            }
        } catch (SQLException e) {
            System.err.println("Notifikasi SQL DlgManagementSignatureUser: " + e);
        } catch (Exception e) {
            System.err.println("Error DlgManagementSignatureUser: " + e);
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgManagementSignatureUser dialog = new DlgManagementSignatureUser(new javax.swing.JFrame(), true);
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
    private widget.Button BtnCari;
    private widget.Button BtnDataPetugas;
    private widget.Button BtnKeluar;
    private widget.Button BtnLogTte;
    private widget.Button BtnResetPassphrase;
    private widget.Button BtnSemua;
    private widget.Button BtnTambahPetugas;
    private widget.Button BtnUpdateStatus;
    private widget.ComboBox CmbFilter;
    private widget.TextBox TCari;
    private widget.ScrollPane intMapingDokter;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel37;
    private widget.Label jLabel38;
    private widget.Label jLabelFilter;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu.Separator jSeparatorMenu1;
    private widget.TextBox kdPetugas;
    private widget.TextBox nik;
    private widget.TextBox nmPetugas;
    private widget.panelisi panelGlass8;
    private javax.swing.JMenuItem ppBersihkan;
    private javax.swing.JMenuItem ppResetPassphrase;
    private javax.swing.JMenuItem ppSemua;
    private javax.swing.JMenuItem ppUpdateTerpilih;
    private widget.panelisi tambahPanel;
    private widget.Table tbMapingPetugas;
    // End of variables declaration//GEN-END:variables
}
