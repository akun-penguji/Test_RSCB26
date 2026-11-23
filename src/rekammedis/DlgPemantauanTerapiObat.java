package rekammedis;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariPetugas;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JDialog;

/**
 * Form Pemantauan Terapi Obat - Pure Java Implementation (No Stored Procedure)
 * Menggunakan tabel: pemantauan_terapi_obat & pemantauan_terapi_obat_harian
 * Mendukung input obat dari master RS dan obat luar (manual)
 *
 * @author Developer
 */
public class DlgPemantauanTerapiObat extends javax.swing.JDialog {

    private DefaultTableModel tabMode, tabMode1, tabModeTerapi;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps, ps1, ps2;
    private ResultSet rs, rs1, rs2;
    private int x = 0, i = 0;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);

    // Variabel untuk dual input obat (RS + Luar)
    private String kdobat = ""; // Kosong jika obat luar
    private String jenisObat = ""; // "rs" atau "luar"
    private boolean isObatFromRS = false;
    private String status = "", idTerapiAktif = "";

    // Variabel untuk auto-increment hari
    private int currentHariCount = 0;
    private final int MAX_HARI = 30; // Batas maksimal hari terapi
    private boolean isEditMode = false; // Flag untuk mode edit

    // Context Menu Components untuk Terapi Obat
    private JPopupMenu popupMenuTerapi;
    private JMenuItem menuLihatDetail, menuEditJadwal, menuUbahStatus, menuHapusObat;
    private JSeparator separator1, separator2;

    // Context Menu Components untuk Pemantauan Harian
    private JPopupMenu popupMenuPemantauan;
    private JMenuItem menuTambahHariPemantauan, menuHapusJadwalPemantauan;
    private JSeparator separatorPemantauan;

    // UI Components Declaration
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnTambahHari;
    private widget.Button BtnResetJadwal;
    private widget.Button BtnHapusHariTerakhir;
    private widget.Button BtnCari;
    private widget.Button BtnCari1;
    private widget.Button BtnCloseIn1;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnObat;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.Button BtnStatistik;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll1;
    private widget.ScrollPane Scroll2;
    private widget.TextBox TCari;
    private widget.TextBox TCari1;
    private widget.TextBox TNmPasien;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRW;
    private javax.swing.JDialog WindowObat;
    private widget.ComboBox caraPemberian;
    private widget.TextBox dosis;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame6;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel3;
    private widget.Label jLabel4;
    private widget.Label jLabel48;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel8;
    private javax.swing.JPanel jPanel3;
    private widget.TextBox jumlahHari;
    private widget.TextBox keterangan;
    private widget.Label lblStatusObat;
    private widget.TextBox nmObat;
    private widget.TextBox nmUnit;
    private widget.panelisi panelGlass10;
    private widget.panelisi panelGlass13;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.panelisi panelisi5;
    private widget.Table tbObat;
    private widget.Table tbPemantauan;
    private widget.Table tbTerapiObat;
    private widget.Tanggal tglMulai;
    private boolean isUpdatingTable = false; // Flag to prevent recursive updates

    // Context Menu untuk Edit Tanggal
    private JPopupMenu popupMenuTanggal;
    private JMenuItem menuEditTanggal;
    private JMenuItem menuResetTanggal;
    private JSeparator separatorTanggal;

    /**
     * Creates new form DlgPemantauanTerapiObat
     */
    public DlgPemantauanTerapiObat(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        // PENTING: initComponents() harus dipanggil pertama kali
        initComponents();

        // Setelah initComponents(), baru inisialisasi yang lain
        initAfterComponents();
    }

    /**
     * Modifikasi initAfterComponents untuk menambah context menu tanggal
     */
    private void initAfterComponents() {
        try {
            // Initialize context menus
            initContextMenu();
            updateContextMenuPemantauan(); // GANTI dari initContextMenuPemantauan()
            initContextMenuTanggal();

            // Initialize table models
            initTableModels();

            // Initialize event handlers
            initEventHandlers();

            // Setup auto-suggest
            setupAutoSuggestObat();

            // ===== TAMBAHKAN BARIS INI =====
            // Setup auto-save keterangan
            setupAutoSaveKeterangan();

            // Setup tooltip untuk keterangan
            setupKeteranganTooltip();

            // Setup keyboard shortcuts untuk hapus obat
            setupHapusObatKeyboardShortcuts();

            // Setup keyboard shortcuts untuk hapus jadwal
            setupHapusJadwalKeyboardShortcuts();

            // Tambahkan tombol hapus obat ke UI
            tambahTombolHapusObat();

            // Update context menu
            updateContextMenuTerapiObat();

            // TAMBAHKAN INI: Setup real-time search untuk dialog obat (optional)
            // setupRealTimeSearch(); // Uncomment jika ingin pencarian real-time
            // Set document limits - SETELAH UI components ter-create
            if (nmObat != null) {
                nmObat.setDocument(new batasInput((int) 255).getKata(nmObat));
            }
            if (dosis != null) {
                dosis.setDocument(new batasInput((int) 100).getKata(dosis));
            }

            if (caraPemberian != null) {
                // Set batas input untuk ComboBox editor jika editable
                if (caraPemberian.getEditor() != null && caraPemberian.getEditor().getEditorComponent() instanceof javax.swing.JTextField) {
                    javax.swing.JTextField editor = (javax.swing.JTextField) caraPemberian.getEditor().getEditorComponent();
                    editor.setDocument(new batasInput((int) 180).getKata(editor));
                }
            }

            if (keterangan != null) {
                keterangan.setDocument(new batasInput((int) 200).getKata(keterangan));
            }
            if (TCari != null) {
                TCari.setDocument(new batasInput((byte) 100).getKata(TCari));
            }
            // TAMBAHKAN BATAS INPUT UNTUK TCARI1
            if (TCari1 != null) {
                TCari1.setDocument(new batasInput((byte) 100).getKata(TCari1));
            }

            // Set jumlahHari sebagai read-only dan display counter
            if (jumlahHari != null) {
                jumlahHari.setEditable(false);
                jumlahHari.setText("0");
            }

            caraPemberian.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    handleCustomCaraPemberian();
                }
            });

            caraPemberian.setToolTipText("<html><b>Cara Pemberian Obat (Singkatan Standar)</b><br/>"
                    + "• <b>PO</b> = Per Oral (diminum)<br/>"
                    + "• <b>IV</b> = Intravena (suntik pembuluh darah)<br/>"
                    + "• <b>IM</b> = Intramuskular (suntik otot)<br/>"
                    + "• <b>SC</b> = Subkutan (suntik bawah kulit)<br/>"
                    + "• <b>INH</b> = Inhalasi (dihirup)<br/>"
                    + "• <b>PR</b> = Per Rektal (dubur)<br/>"
                    + "• <b>TOP</b> = Topikal (dioleskan)<br/>"
                    + "• <b>SL</b> = Sublingual (bawah lidah)<br/>"
                    + "• <b>NAS</b> = Nasal (hidung)<br/>"
                    + "• <b>OD/OS/OU</b> = Mata (kanan/kiri/keduanya)<br/>"
                    + "• Pilih 'Lainnya' untuk input khusus</html>");

            // Update button text dan tooltip
            updateButtonState();

            // Window listener untuk dialog obat
            if (WindowObat != null) {
                WindowObat.addWindowListener(new WindowListener() {
                    @Override
                    public void windowOpened(WindowEvent e) {
                        // Focus ke textbox pencarian saat dialog dibuka
                        TCari1.requestFocus();
                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        // Reset pencarian saat dialog ditutup
                        TCari1.setText("");
                    }

                    @Override
                    public void windowClosed(WindowEvent e) {
                    }

                    @Override
                    public void windowIconified(WindowEvent e) {
                    }

                    @Override
                    public void windowDeiconified(WindowEvent e) {
                    }

                    @Override
                    public void windowActivated(WindowEvent e) {
                        // Focus ke textbox saat dialog diaktifkan
                        if (TCari1 != null) {
                            TCari1.requestFocus();
                        }
                    }

                    @Override
                    public void windowDeactivated(WindowEvent e) {
                    }
                });
            }

        } catch (Exception e) {
            System.out.println("Error in initAfterComponents: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        WindowObat = new javax.swing.JDialog();
        internalFrame6 = new widget.InternalFrame();
        Scroll1 = new widget.ScrollPane();
        tbObat = new widget.Table();
        panelisi5 = new widget.panelisi();
        jLabel48 = new widget.Label();
        TCari1 = new widget.TextBox();
        BtnCari1 = new widget.Button();
        BtnCloseIn1 = new widget.Button();
        internalFrame1 = new widget.InternalFrame();
        panelGlass10 = new widget.panelisi();
        jLabel3 = new widget.Label();
        nmObat = new widget.TextBox();
        BtnObat = new widget.Button();
        jLabel4 = new widget.Label();
        dosis = new widget.TextBox();
        jLabel8 = new widget.Label();
        caraPemberian = new widget.ComboBox();
        jLabel10 = new widget.Label();
        tglMulai = new widget.Tanggal();
        jLabel11 = new widget.Label();
        jumlahHari = new widget.TextBox();
        jLabel12 = new widget.Label();
        jLabel13 = new widget.Label();
        keterangan = new widget.TextBox();
        BtnTambahHari = new widget.Button();
        BtnResetJadwal = new widget.Button();
        BtnHapusHariTerakhir = new widget.Button();
        jLabel5 = new widget.Label();
        TNoRW = new widget.TextBox();
        TNoRM = new widget.TextBox();
        TNmPasien = new widget.TextBox();
        jLabel14 = new widget.Label();
        nmUnit = new widget.TextBox();
        jLabel15 = new widget.Label();
        lblStatusObat = new widget.Label();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        BtnStatistik = new widget.Button();
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
        panelGlass13 = new widget.panelisi();
        Scroll = new widget.ScrollPane();
        tbTerapiObat = new widget.Table();
        Scroll2 = new widget.ScrollPane();
        tbPemantauan = new widget.Table();

        // Window Obat Dialog
        WindowObat.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        WindowObat.setName("WindowObat");
        WindowObat.setUndecorated(true);
        WindowObat.setResizable(false);

        internalFrame6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 255), 3), "::[ Daftar Obat RS (Master Farmasi) ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12)));
        internalFrame6.setName("internalFrame6");
        internalFrame6.setLayout(new java.awt.BorderLayout());

        Scroll1.setName("Scroll1");
        Scroll1.setOpaque(true);

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
        Scroll1.setViewportView(tbObat);
        internalFrame6.add(Scroll1, java.awt.BorderLayout.CENTER);

        panelisi5.setName("panelisi5");
        panelisi5.setPreferredSize(new java.awt.Dimension(100, 44));
        panelisi5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 7));

        jLabel48.setText("Cari Obat :");
        jLabel48.setName("jLabel48");
        panelisi5.add(jLabel48);

        TCari1.setName("TCari1");
        TCari1.setPreferredSize(new java.awt.Dimension(250, 23));
        TCari1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCari1KeyPressed(evt); // TAMBAHKAN EVENT HANDLER INI
            }
        });
        panelisi5.add(TCari1);

        BtnCari1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/checked.png")));
        BtnCari1.setMnemonic('1');
        BtnCari1.setText("Tampilkan Data");
        BtnCari1.setName("BtnCari1");
        BtnCari1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCari1ActionPerformed(evt);
            }
        });
        panelisi5.add(BtnCari1);

        BtnCloseIn1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/x.png")));
        BtnCloseIn1.setMnemonic('U');
        BtnCloseIn1.setText("Tutup");
        BtnCloseIn1.setName("BtnCloseIn1");
        BtnCloseIn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCloseIn1ActionPerformed(evt);
            }
        });
        panelisi5.add(BtnCloseIn1);

        internalFrame6.add(panelisi5, java.awt.BorderLayout.PAGE_END);
        WindowObat.getContentPane().add(internalFrame6, java.awt.BorderLayout.CENTER);

        // Main Dialog
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 255), 3), "::[ Pemantauan Terapi Obat Pasien (RS + Luar) ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12)));
        internalFrame1.setName("internalFrame1");
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        // Top Panel (Input Form)
        panelGlass10.setName("panelGlass10");
        panelGlass10.setPreferredSize(new java.awt.Dimension(44, 160));
        panelGlass10.setLayout(null);

        jLabel5.setText("Pasien :");
        jLabel5.setName("jLabel5");
        jLabel5.setBounds(0, 12, 50, 23);
        panelGlass10.add(jLabel5);

        TNoRW.setEditable(false);
        TNoRW.setName("TNoRW");
        TNoRW.setBounds(55, 12, 122, 23);
        panelGlass10.add(TNoRW);

        TNoRM.setEditable(false);
        TNoRM.setName("TNoRM");
        TNoRM.setBounds(180, 12, 70, 23);
        panelGlass10.add(TNoRM);

        TNmPasien.setEditable(false);
        TNmPasien.setName("TNmPasien");
        TNmPasien.setBounds(255, 12, 200, 23);
        panelGlass10.add(TNmPasien);

        jLabel14.setText("Ruangan :");
        jLabel14.setName("jLabel14");
        jLabel14.setBounds(470, 12, 60, 23);
        panelGlass10.add(jLabel14);

        nmUnit.setEditable(false);
        nmUnit.setName("nmUnit");
        nmUnit.setBounds(535, 12, 150, 23);
        panelGlass10.add(nmUnit);

        jLabel3.setText("Nama Obat :");
        jLabel3.setName("jLabel3");
        jLabel3.setBounds(0, 40, 80, 23);
        panelGlass10.add(jLabel3);

        nmObat.setName("nmObat");
        nmObat.setBounds(85, 40, 300, 23);
        nmObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nmObatKeyPressed(evt);
            }
        });
        panelGlass10.add(nmObat);

        BtnObat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnObat.setMnemonic('1');
        BtnObat.setText("RS");
        BtnObat.setName("BtnObat");
        BtnObat.setToolTipText("Pilih obat dari Master RS (F1)");
        BtnObat.setBounds(390, 40, 50, 23);
        BtnObat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnObatActionPerformed(evt);
            }
        });
        panelGlass10.add(BtnObat);

        jLabel15.setText("Status :");
        jLabel15.setName("jLabel15");
        jLabel15.setBounds(450, 40, 45, 23);
        panelGlass10.add(jLabel15);

        lblStatusObat.setText("Obat Luar");
        lblStatusObat.setName("lblStatusObat");
        lblStatusObat.setBounds(500, 40, 100, 23);
        lblStatusObat.setForeground(new java.awt.Color(255, 102, 0));
        lblStatusObat.setFont(new java.awt.Font("Tahoma", 1, 11));
        panelGlass10.add(lblStatusObat);

        jLabel4.setText("Dosis :");
        jLabel4.setName("jLabel4");
        jLabel4.setBounds(600, 40, 40, 23);
        panelGlass10.add(jLabel4);

        dosis.setName("dosis");
        dosis.setBounds(645, 40, 120, 23);
        dosis.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                dosisKeyPressed(evt);
            }
        });
        panelGlass10.add(dosis);

        jLabel8.setText("Cara Pemberian :");
        jLabel8.setName("jLabel8");
        jLabel8.setBounds(0, 68, 100, 23);
        panelGlass10.add(jLabel8);

        caraPemberian = new widget.ComboBox();
        caraPemberian.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "PO", // Per Oral
            "IV", // Intravena
            "IM", // Intramuskular
            "SC", // Subkutan
            "INH", // Inhalasi
            "PR", // Per Rektal
            "PV", // Per Vaginal
            "SL", // Sublingual
            "TOP", // Topikal
            "OD", // Oculus Dextra (Mata Kanan)
            "OS", // Oculus Sinistra (Mata Kiri)
            "OU", // Oculus Uterque (Kedua Mata)
            "AD", // Auris Dextra (Telinga Kanan)
            "AS", // Auris Sinistra (Telinga Kiri)
            "AU", // Auris Uterque (Kedua Telinga)
            "NAS", // Nasal
            "ID", // Intradermal
            "IT", // Intrathecal
            "INF", // Infus
            "NEB", // Nebulizer
            "Lainnya" // Custom input
        }));
        caraPemberian.setName("caraPemberian");
        caraPemberian.setBounds(105, 68, 200, 23);
        caraPemberian.setEnabled(true); // Bisa diedit untuk input custom
        caraPemberian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                caraPemberianKeyPressed(evt);
            }
        });
        panelGlass10.add(caraPemberian);

        jLabel10.setText("Tgl. Mulai :");
        jLabel10.setName("jLabel10");
        jLabel10.setBounds(320, 68, 70, 23);
        panelGlass10.add(jLabel10);

        tglMulai.setDisplayFormat("dd-MM-yyyy");
        tglMulai.setName("tglMulai");
        tglMulai.setBounds(395, 68, 100, 23);
        tglMulai.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                tglMulaiPropertyChange(evt);
            }
        });
        panelGlass10.add(tglMulai);

        jLabel11.setText("Total Hari :");
        jLabel11.setName("jLabel11");
        jLabel11.setBounds(510, 68, 80, 23);
        panelGlass10.add(jLabel11);

        jumlahHari.setEditable(false);
        jumlahHari.setText("0");
        jumlahHari.setName("jumlahHari");
        jumlahHari.setBounds(595, 68, 50, 23);
        panelGlass10.add(jumlahHari);

        jLabel12.setText("hari");
        jLabel12.setName("jLabel12");
        jLabel12.setBounds(650, 68, 30, 23);
        panelGlass10.add(jLabel12);

        jLabel13.setText("Keterangan :");
        jLabel13.setName("jLabel13");
        jLabel13.setBounds(0, 96, 80, 23);
        panelGlass10.add(jLabel13);

        keterangan.setName("keterangan");
        keterangan.setBounds(85, 96, 200, 23);
        panelGlass10.add(keterangan);

        BtnTambahHari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        BtnTambahHari.setMnemonic('T');
        BtnTambahHari.setText("Tambah Hari");
        BtnTambahHari.setName("BtnTambahHari");
        BtnTambahHari.setBounds(300, 96, 120, 23);
        BtnTambahHari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTambahHariActionPerformed(evt);
            }
        });
        panelGlass10.add(BtnTambahHari);

        BtnResetJadwal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/refresh.png")));
        BtnResetJadwal.setMnemonic('R');
        BtnResetJadwal.setText("Reset");
        BtnResetJadwal.setName("BtnResetJadwal");
        BtnResetJadwal.setBounds(430, 96, 80, 23);
        BtnResetJadwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnResetJadwalActionPerformed(evt);
            }
        });
        panelGlass10.add(BtnResetJadwal);

        BtnHapusHariTerakhir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/minus.png")));
        BtnHapusHariTerakhir.setMnemonic('H');
        BtnHapusHariTerakhir.setText("Hapus");
        BtnHapusHariTerakhir.setName("BtnHapusHariTerakhir");
        BtnHapusHariTerakhir.setBounds(520, 96, 80, 23);
        BtnHapusHariTerakhir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusHariTerakhirActionPerformed(evt);
            }
        });
        panelGlass10.add(BtnHapusHariTerakhir);

        internalFrame1.add(panelGlass10, java.awt.BorderLayout.PAGE_START);

        // Bottom Panel (Buttons and Search)
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
        BtnSimpan.setName("BtnSimpan");
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnSimpan);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/cancel.png")));
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Baru");
        BtnBatal.setName("BtnBatal");
        BtnBatal.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBatalActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnBatal);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setMnemonic('D');
        BtnHapus.setText("Hapus");
        BtnHapus.setName("BtnHapus");
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnHapus);

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnEdit.setMnemonic('G');
        BtnEdit.setText("Ganti");
        BtnEdit.setName("BtnEdit");
        BtnEdit.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEditActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnEdit);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Print.png")));
        BtnPrint.setMnemonic('P');
        BtnPrint.setText("Cetak");
        BtnPrint.setName("BtnPrint");
        BtnPrint.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrintActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnPrint);

        BtnStatistik.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        BtnStatistik.setMnemonic('K');
        BtnStatistik.setText("Statistik");
        BtnStatistik.setName("BtnStatistik");
        BtnStatistik.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnStatistik.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnStatistikActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnStatistik);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/keluar.png")));
        BtnKeluar.setMnemonic('X');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setName("BtnKeluar");
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnKeluar);

        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9");
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 3, 9));

        jLabel19.setText("Tgl. Terapi :");
        jLabel19.setName("jLabel19");
        jLabel19.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"26-06-2025"}));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1");
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari1);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setName("jLabel21");
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel21);

        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"26-06-2025"}));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2");
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6");
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari");
        TCari.setPreferredSize(new java.awt.Dimension(200, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/checked.png")));
        BtnCari.setMnemonic('1');
        BtnCari.setText("Tampilkan Data");
        BtnCari.setName("BtnCari");
        BtnCari.setPreferredSize(new java.awt.Dimension(130, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        panelGlass9.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/search_page.png")));
        BtnAll.setMnemonic('2');
        BtnAll.setText("Semua Data");
        BtnAll.setName("BtnAll");
        BtnAll.setPreferredSize(new java.awt.Dimension(120, 23));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllActionPerformed(evt);
            }
        });
        panelGlass9.add(BtnAll);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);
        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        // Center Panel (Tables)
        panelGlass13.setName("panelGlass13");
        panelGlass13.setPreferredSize(new java.awt.Dimension(44, 300));
        panelGlass13.setLayout(new java.awt.BorderLayout());

        Scroll.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
                "[ Data Obat Terapi - Klik untuk pilih/tambah jadwal (🟢=RS, 🟡=Luar, 📋=Draft) ]",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 12)));
        Scroll.setName("Scroll");
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(460, 200));

        tbTerapiObat.setName("tbTerapiObat");
        tbTerapiObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbTerapiObatMouseClicked(evt);
            }
        });
        tbTerapiObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbTerapiObatKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbTerapiObat);
        panelGlass13.add(Scroll, java.awt.BorderLayout.PAGE_START);

        Scroll2.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
                "[ Jadwal Pemberian Harian - Klik checkbox untuk update status pemberian ]",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 12)));
        Scroll2.setName("Scroll2");
        Scroll2.setOpaque(true);

        tbPemantauan.setName("tbPemantauan");
        tbPemantauan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbPemantauanMouseClicked(evt);
            }
        });
        Scroll2.setViewportView(tbPemantauan);
        panelGlass13.add(Scroll2, java.awt.BorderLayout.CENTER);

        internalFrame1.add(panelGlass13, java.awt.BorderLayout.CENTER);
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>

    private void debugKeteranganSave() {
        System.out.println("=== DEBUG KETERANGAN ===");
        System.out.println("Keterangan text: '" + keterangan.getText() + "'");
        System.out.println("Keterangan length: " + keterangan.getText().length());
        System.out.println("Keterangan editable: " + keterangan.isEditable());
        System.out.println("Keterangan enabled: " + keterangan.isEnabled());
        System.out.println("======================");
    }

    /**
     * Tambah hari ke jadwal terapi (Pure Java)
     */
    private boolean tambahHariTerapi(String idTerapi, int hariKe) {
        try {
            // 1. Ambil data terapi
            String sqlTerapi = "SELECT tgl_mulai_terapi FROM pemantauan_terapi_obat WHERE id = ?";
            String tglMulaiStr = Sequel.cariIsi(sqlTerapi, idTerapi);
            String namaObat = Sequel.cariIsi("SELECT nama_obat FROM pemantauan_terapi_obat WHERE id = ?", idTerapi);

            if (tglMulaiStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Data terapi tidak ditemukan!");
                return false;
            }

            // 2. Hitung tanggal untuk hari ini
            Date tglMulai = new SimpleDateFormat("yyyy-MM-dd").parse(tglMulaiStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(tglMulai);
            cal.add(Calendar.DAY_OF_MONTH, hariKe - 1);
            Date tanggalBaru = cal.getTime();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String tanggalStr = sdf.format(tanggalBaru);

            // 3. Cek apakah tanggal sudah ada
            String sqlCek = "SELECT COUNT(*) FROM pemantauan_terapi_obat_harian WHERE id_terapi = ? AND tgl_pemberian = ?";
            int jumlahAda = Sequel.cariInteger(sqlCek, idTerapi, tanggalStr);

            if (jumlahAda > 0) {
                JOptionPane.showMessageDialog(this, "Tanggal " + tanggalStr + " sudah ada dalam jadwal!");
                return false;
            }

            // 4. Insert jadwal harian baru menggunakan queryu2tf
            // PERBAIKAN: SQL INSERT dengan 5 kolom eksplisit (tanpa id dan waktu_input)
            String sqlInsert = "INSERT INTO pemantauan_terapi_obat_harian "
                    + "(id_terapi, tgl_pemberian, status_pemberian, keterangan, nip_petugas) "
                    + "VALUES (?, ?, ?, ?, ?)";

            // PERBAIKAN: Hanya 5 parameter untuk 5 kolom
            String[] dataHarian = new String[5];
            dataHarian[0] = idTerapi;                    // id_terapi
            dataHarian[1] = tanggalStr;                  // tgl_pemberian
            dataHarian[2] = "tidak";                     // status_pemberian
            dataHarian[3] = "";                          // keterangan
            dataHarian[4] = "-";                         // nip_petugas

            if (Sequel.queryu2tf(sqlInsert, 5, dataHarian)) {
                // 5. Update jumlah hari dan status di terapi utama
                String sqlUpdate = "UPDATE pemantauan_terapi_obat SET jumlah_hari = ?, status = 'aktif' WHERE id = ?";
                String[] dataUpdate = {String.valueOf(hariKe), idTerapi};

                if (Sequel.queryu2tf(sqlUpdate, 2, dataUpdate)) {
                    String pesanSukses = "✅ HARI KE-" + hariKe + " BERHASIL DITAMBAHKAN!\n\n";
                    pesanSukses += "Obat: " + namaObat + "\n";
                    pesanSukses += "Tanggal: " + sdf.format(tanggalBaru) + "\n";
                    pesanSukses += "Status Terapi: AKTIF\n";
                    pesanSukses += "Total Jadwal: " + hariKe + " hari";

                    JOptionPane.showMessageDialog(this, pesanSukses, "Jadwal Ditambahkan", JOptionPane.INFORMATION_MESSAGE);
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            System.out.println("Error tambah hari terapi: " + e);
            JOptionPane.showMessageDialog(this, "Gagal menambah hari: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update status pemberian obat (Pure Java)
     */
    private boolean updateStatusPemberian(String idTerapi, String tanggal, boolean diberikan) {
        try {
            // 1. Update status pemberian
            String[] dataUpdate = new String[4];
            dataUpdate[0] = diberikan ? "ya" : "tidak";              // status_pemberian
            dataUpdate[1] = diberikan ? akses.getkode() : "-";       // nip_petugas
            dataUpdate[2] = idTerapi;                                // id_terapi (WHERE)
            dataUpdate[3] = convertTanggalForDB(tanggal);            // tgl_pemberian (WHERE)

            String updateClause = "status_pemberian = ?, nip_petugas = ?, waktu_input = NOW()";
            String whereClause = "id_terapi = ? AND tgl_pemberian = ?";

            if (Sequel.mengedittf("pemantauan_terapi_obat_harian", whereClause, updateClause, 4, dataUpdate)) {
                // 2. Jika diberikan, otomatis ubah status terapi menjadi aktif
                if (diberikan) {
                    updateStatusTerapiToAktif(idTerapi);
                }

                // 3. Cek apakah terapi sudah selesai
                cekDanUpdateStatusSelesai(idTerapi);

                return true;
            }

            return false;

        } catch (Exception e) {
            System.out.println("Error update status pemberian: " + e);
            return false;
        }
    }

    /**
     * Update status terapi menjadi aktif (Pure Java)
     */
    private boolean updateStatusTerapiToAktif(String idTerapi) {
        try {
            String[] dataUpdate = new String[1];
            dataUpdate[0] = idTerapi;

            String updateClause = "status = 'aktif'";
            String whereClause = "id = ?";

            return Sequel.mengedittf2("pemantauan_terapi_obat", whereClause, updateClause, 1, dataUpdate);

        } catch (Exception e) {
            System.out.println("Error update status to aktif: " + e);
            return false;
        }
    }

    /**
     * Cek dan update status selesai jika semua hari sudah diberikan (Pure Java)
     */
    private void cekDanUpdateStatusSelesai(String idTerapi) {
        try {
            // 1. Ambil total hari terapi
            String sqlTotal = "SELECT jumlah_hari FROM pemantauan_terapi_obat WHERE id = ?";
            int totalHari = Sequel.cariInteger(sqlTotal, idTerapi);

            if (totalHari == 0) {
                return; // Skip jika belum ada jadwal
            }

            // 2. Hitung hari yang sudah diberikan
            String sqlDiberikan = "SELECT COUNT(*) FROM pemantauan_terapi_obat_harian "
                    + "WHERE id_terapi = ? AND status_pemberian = 'ya'";
            int hariDiberikan = Sequel.cariInteger(sqlDiberikan, idTerapi);

            // 3. Update status jika sudah selesai
            if (hariDiberikan >= totalHari) {
                String[] dataUpdate = new String[1];
                dataUpdate[0] = idTerapi;

                String updateClause = "status = 'selesai'";
                String whereClause = "id = ?";

                Sequel.mengedit3("pemantauan_terapi_obat", whereClause, updateClause, 1, dataUpdate);
                System.out.println("Status terapi " + idTerapi + " diubah menjadi SELESAI");
            }

        } catch (Exception e) {
            System.out.println("Error cek status selesai: " + e);
        }
    }

    /**
     * Hitung progress terapi (Pure Java)
     */
    private String hitungProgressTerapi(String idTerapi) {
        try {
            // Ambil jumlah hari total
            String sqlTotal = "SELECT jumlah_hari FROM pemantauan_terapi_obat WHERE id = ?";
            int totalHari = Sequel.cariInteger(sqlTotal, idTerapi);

            if (totalHari == 0) {
                return "DRAFT";
            }

            // Hitung hari yang sudah diberikan
            String sqlDiberikan = "SELECT COUNT(*) FROM pemantauan_terapi_obat_harian "
                    + "WHERE id_terapi = ? AND status_pemberian = 'ya'";
            int hariDiberikan = Sequel.cariInteger(sqlDiberikan, idTerapi);

            return hariDiberikan + "/" + totalHari;

        } catch (Exception e) {
            System.out.println("Error hitung progress: " + e);
            return "0/0";
        }
    }

    /**
     * Hapus terapi obat beserta jadwal harian (Pure Java)
     */
    private boolean hapusTerapiObat(String idTerapi) {
        try {
            // Mulai transaction
            Sequel.AutoComitFalse();

            try {
                // 1. Hapus jadwal harian terlebih dahulu
                if (Sequel.meghapustf("pemantauan_terapi_obat_harian", "id_terapi", idTerapi)) {
                    // 2. Hapus data terapi utama
                    if (Sequel.meghapustf("pemantauan_terapi_obat", "id", idTerapi)) {
                        // Commit transaction
                        Sequel.Commit();
                        Sequel.AutoComitTrue();
                        return true;
                    }
                }

                // Rollback jika ada yang gagal
                Sequel.RollBack();
                Sequel.AutoComitTrue();
                return false;

            } catch (Exception e) {
                Sequel.RollBack();
                Sequel.AutoComitTrue();
                throw e;
            }

        } catch (Exception e) {
            System.out.println("Error hapus terapi obat: " + e);
            JOptionPane.showMessageDialog(this,
                    "❌ GAGAL MENGHAPUS OBAT!\n\n"
                    + "Error: " + e.getMessage() + "\n\n"
                    + "Silahkan coba lagi atau hubungi administrator.",
                    "Error Penghapusan",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Ambil data gabungan terapi obat dengan pasien (Pure Java - replace view)
     */
    private void tampilTerapiObat() {
        Valid.tabelKosong(tabMode);
        try {
            String sql = "SELECT "
                    + "p.id, p.nama_obat, p.dosis, p.cara_pemberian, "
                    + "DATE_FORMAT(p.tgl_mulai_terapi,'%d-%m-%Y') as tgl_mulai, "
                    + "p.jumlah_hari, "
                    + "p.status, "
                    + "CASE "
                    + "  WHEN p.jenis_obat='rs' THEN 'Obat RS' "
                    + "  WHEN p.jenis_obat='luar' THEN 'Obat Luar' "
                    + "  ELSE 'Unknown' "
                    + "END as jenis, "
                    + "COALESCE(pg.nama, p.nip_petugas) as petugas "
                    + "FROM pemantauan_terapi_obat p "
                    + "INNER JOIN reg_periksa rp ON rp.no_rawat = p.no_rawat "
                    + "INNER JOIN pasien ps ON ps.no_rkm_medis = rp.no_rkm_medis "
                    + "LEFT JOIN pegawai pg ON pg.nik = p.nip_petugas "
                    + "WHERE p.tgl_mulai_terapi BETWEEN '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' AND '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' "
                    + "AND (p.no_rawat LIKE '%" + TCari.getText().trim() + "%' OR ps.no_rkm_medis LIKE '%" + TCari.getText().trim() + "%' OR ps.nm_pasien LIKE '%" + TCari.getText().trim() + "%' OR p.nama_obat LIKE '%" + TCari.getText().trim() + "%') "
                    + "ORDER BY "
                    + "  CASE WHEN p.status = 'draft' THEN 1 ELSE 2 END, "
                    + "  p.tgl_mulai_terapi DESC, p.waktu_simpan DESC";

            PreparedStatement ps = koneksi.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String idTerapi = rs.getString("id");
                String progress = hitungProgressTerapi(idTerapi);

                tabMode.addRow(new Object[]{
                    idTerapi,
                    rs.getString("nama_obat"),
                    rs.getString("dosis"),
                    rs.getString("cara_pemberian"),
                    rs.getString("tgl_mulai"),
                    rs.getString("jumlah_hari"),
                    progress,
                    rs.getString("status").toUpperCase(),
                    rs.getString("jenis"),
                    rs.getString("petugas")
                });
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            System.out.println("Error tampil terapi obat: " + e);
        }
    }

    /**
     * Ambil jadwal harian dengan info hari ke-berapa (Pure Java - replace view)
     */
    /**
     * Ambil jadwal harian dengan info hari ke-berapa (Pure Java - replace view)
     */
    private void tampilPemantauan() {
        Valid.tabelKosong(tabModeTerapi);
        if (!idTerapiAktif.equals("")) {
            try {
                String sql = "SELECT "
                        + "h.tgl_pemberian, h.status_pemberian, h.keterangan, "
                        + "COALESCE(pg.nama, h.nip_petugas) as petugas, "
                        + "DATE_FORMAT(h.waktu_input, '%d-%m-%Y %H:%i') as waktu_input, "
                        + "p.tgl_mulai_terapi "
                        + "FROM pemantauan_terapi_obat_harian h "
                        + "INNER JOIN pemantauan_terapi_obat p ON p.id = h.id_terapi "
                        + "LEFT JOIN pegawai pg ON pg.nik = h.nip_petugas "
                        + "WHERE h.id_terapi = ? "
                        + "ORDER BY h.tgl_pemberian";

                PreparedStatement ps = koneksi.prepareStatement(sql);
                ps.setString(1, idTerapiAktif);
                ResultSet rs = ps.executeQuery();

                Date tglMulaiTerapi = null;
                while (rs.next()) {
                    if (tglMulaiTerapi == null) {
                        tglMulaiTerapi = rs.getDate("tgl_mulai_terapi");
                    }

                    Date tglPemberian = rs.getDate("tgl_pemberian");
                    int hariKe = hitungHariKe(tglMulaiTerapi, tglPemberian);

                    String statusPemberian = rs.getString("status_pemberian");
                    boolean diberikan = "ya".equalsIgnoreCase(statusPemberian);

                    String keterangan = rs.getString("keterangan");
                    String petugas = rs.getString("petugas");
                    String waktuInput = rs.getString("waktu_input");

                    if (keterangan == null) {
                        keterangan = "";
                    }
                    if (petugas == null || petugas.equals("-")) {
                        petugas = "";
                    }
                    if (waktuInput == null) {
                        waktuInput = "";
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String tanggalStr = sdf.format(tglPemberian);

                    tabModeTerapi.addRow(new Object[]{
                        false,
                        "Hari " + hariKe,
                        tanggalStr,
                        diberikan,
                        keterangan,
                        petugas,
                        waktuInput
                    });
                }

                rs.close();
                ps.close();

            } catch (Exception e) {
                System.out.println("Error tampil pemantauan: " + e);
            }
        }
    }

    /**
     * Hitung hari ke berapa dari tanggal mulai (Pure Java)
     */
    private int hitungHariKe(Date tglMulai, Date tglPemberian) {
        try {
            long diffInMillies = tglPemberian.getTime() - tglMulai.getTime();
            int diffInDays = (int) (diffInMillies / (1000 * 60 * 60 * 24));
            return diffInDays + 1; // Hari ke-1, ke-2, dst
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * Statistik penggunaan obat (Pure Java - replace view)
     */
    private void tampilkanStatistikObat() {
        try {
            String sql = "SELECT "
                    + "jenis_obat, "
                    + "COUNT(*) as jumlah_terapi, "
                    + "COUNT(DISTINCT no_rawat) as jumlah_pasien, "
                    + "SUM(CASE WHEN status = 'aktif' THEN 1 ELSE 0 END) as terapi_aktif, "
                    + "SUM(CASE WHEN status = 'selesai' THEN 1 ELSE 0 END) as terapi_selesai "
                    + "FROM pemantauan_terapi_obat "
                    + "WHERE tgl_mulai_terapi >= DATE_SUB(NOW(), INTERVAL 30 DAY) "
                    + "GROUP BY jenis_obat";

            PreparedStatement ps = koneksi.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            StringBuilder stats = new StringBuilder("=== STATISTIK PENGGUNAAN OBAT (30 HARI TERAKHIR) ===\n\n");

            int totalTerapi = 0;
            int totalPasien = 0;

            while (rs.next()) {
                String jenis = rs.getString("jenis_obat");
                int jumlahTerapi = rs.getInt("jumlah_terapi");
                int jumlahPasien = rs.getInt("jumlah_pasien");
                int terapiAktif = rs.getInt("terapi_aktif");
                int terapiSelesai = rs.getInt("terapi_selesai");

                String jenisLabel = jenis.equals("rs") ? "📋 OBAT RS" : "🏠 OBAT LUAR";

                stats.append(jenisLabel).append(":\n");
                stats.append("• Total Terapi: ").append(jumlahTerapi).append("\n");
                stats.append("• Total Pasien: ").append(jumlahPasien).append("\n");
                stats.append("• Terapi Aktif: ").append(terapiAktif).append("\n");
                stats.append("• Terapi Selesai: ").append(terapiSelesai).append("\n\n");

                totalTerapi += jumlahTerapi;
                totalPasien += jumlahPasien;
            }

            stats.append("📊 TOTAL KESELURUHAN:\n");
            stats.append("• Total Terapi: ").append(totalTerapi).append("\n");
            stats.append("• Total Pasien: ").append(totalPasien).append("\n");

            rs.close();
            ps.close();

            // Statistik obat luar populer
            String sqlObatLuar = "SELECT nama_obat, COUNT(*) as frekuensi "
                    + "FROM pemantauan_terapi_obat "
                    + "WHERE jenis_obat='luar' AND tgl_mulai_terapi >= DATE_SUB(NOW(), INTERVAL 30 DAY) "
                    + "GROUP BY nama_obat ORDER BY frekuensi DESC LIMIT 5";

            PreparedStatement psLuar = koneksi.prepareStatement(sqlObatLuar);
            ResultSet rsLuar = psLuar.executeQuery();

            stats.append("\n🏆 TOP 5 OBAT LUAR POPULER:\n");
            int no = 1;
            while (rsLuar.next()) {
                stats.append(no++).append(". ").append(rsLuar.getString("nama_obat"))
                        .append(" (").append(rsLuar.getInt("frekuensi")).append("x)\n");
            }

            rsLuar.close();
            psLuar.close();

            JOptionPane.showMessageDialog(this, stats.toString(), "Statistik Obat", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            System.out.println("Error statistik obat: " + e);
            JOptionPane.showMessageDialog(this, "Gagal menampilkan statistik: " + e.getMessage());
        }
    }

    // Override method tambah hari
    private void tambahHariBerikutnya() {
        if (idTerapiAktif.equals("")) {
            JOptionPane.showMessageDialog(this,
                    "❌ BELUM ADA OBAT YANG DIPILIH!\n\n"
                    + "Langkah yang benar:\n"
                    + "1. Simpan data obat terlebih dahulu\n"
                    + "2. Klik obat yang sudah tersimpan di tabel\n"
                    + "3. Baru gunakan tombol 'Tambah Hari'",
                    "Pilih Obat Dulu",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (tglMulai.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Tanggal mulai terapi tidak valid!");
            return;
        }

        if (currentHariCount >= MAX_HARI) {
            JOptionPane.showMessageDialog(null, "Maksimal " + MAX_HARI + " hari terapi!");
            return;
        }

        // Increment counter hari
        currentHariCount++;

        // Gunakan method dengan sekuel
        if (tambahHariTerapi(idTerapiAktif, currentHariCount)) {
            // Update display
            jumlahHari.setText(String.valueOf(currentHariCount));
            updateButtonState();

            // Refresh tables menggunakan sekuel
            tampilTerapiObat();
            tampilPemantauan();
        } else {
            currentHariCount--; // Reset counter jika gagal
        }
    }

    private void updateStatusPemberian(int baris, boolean diberikan) {
        if (isUpdatingTable || idTerapiAktif.equals("")) {
            return;
        }

        String tanggal = tbPemantauan.getValueAt(baris, 2).toString();

        // Gunakan method dengan sekuel
        if (updateStatusPemberian(idTerapiAktif, tanggal, diberikan)) {
            // SET FLAG TO PREVENT RECURSIVE CALLS
            isUpdatingTable = true;

            try {
                if (diberikan) {
                    tbPemantauan.setValueAt(akses.getkode(), baris, 5);
                    tbPemantauan.setValueAt(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()), baris, 6);

                    JOptionPane.showMessageDialog(this,
                            "✅ STATUS PEMBERIAN OBAT BERHASIL DIUPDATE!\n\n"
                            + "Status Pemberian: DIBERIKAN\n"
                            + "Status Terapi: AKTIF (diubah otomatis)\n"
                            + "Petugas: " + akses.getkode(),
                            "Update Berhasil",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    tbPemantauan.setValueAt("-", baris, 5);
                    tbPemantauan.setValueAt("", baris, 6);
                    tbPemantauan.setValueAt("", baris, 4);

                    JOptionPane.showMessageDialog(this,
                            "Status pemberian obat berhasil diupdate: TIDAK DIBERIKAN",
                            "Update Berhasil",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } finally {
                isUpdatingTable = false;
            }

            // Refresh data terapi untuk update progress dan status menggunakan sekuel
            tampilTerapiObat();
        } else {
            isUpdatingTable = false;
            JOptionPane.showMessageDialog(null, "Gagal mengupdate status pemberian!");
        }
    }

    private boolean simpanTerapiObat() {
        try {
            // Tentukan jenis dan kode obat
            String finalKdobat = "";
            String finalJenisObat = "";
            if (isObatFromRS && !kdobat.isEmpty()) {
                finalKdobat = kdobat;
                finalJenisObat = "rs";
            } else {
                finalKdobat = generateKodeObatLuar();
                finalJenisObat = "luar";
            }

            // Cek duplikasi
            if (isDuplikatObat(TNoRW.getText(), nmObat.getText(), finalJenisObat)) {
                return false;
            }

            // Validasi keterangan
            String keteranganText = keterangan.getText();
            if (keteranganText == null) {
                keteranganText = "";
            }
            keteranganText = keteranganText.trim();

            // PERBAIKAN: Hanya 10 parameter untuk 10 placeholder ?
            String[] data = new String[10];
            data[0] = TNoRW.getText();                                    // no_rawat
            data[1] = finalKdobat;                                        // kd_obat
            data[2] = nmObat.getText();                                   // nama_obat
            data[3] = finalJenisObat;                                     // jenis_obat
            data[4] = dosis.getText();                                    // dosis
            data[5] = (caraPemberian.getSelectedItem() != null)
                    ? caraPemberian.getSelectedItem().toString() : "";
            data[6] = Valid.SetTgl(tglMulai.getSelectedItem() + "");     // tgl_mulai_terapi
            data[7] = "0";                                               // jumlah_hari (akan diupdate setelah tambah hari)
            data[8] = keteranganText;                                    // keterangan
            data[9] = "draft";                                           // status (akan diubah ke aktif setelah tambah hari)
            // TIDAK ADA data[10] karena nip_petugas akan diisi terpisah

            // PERBAIKAN: Gunakan SQL dengan 11 placeholder terpisah
            String sql = "INSERT INTO pemantauan_terapi_obat "
                    + "(no_rawat, kd_obat, nama_obat, jenis_obat, dosis, cara_pemberian, "
                    + "tgl_mulai_terapi, jumlah_hari, keterangan, status, nip_petugas, waktu_simpan) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

            String[] fullData = new String[11];
            System.arraycopy(data, 0, fullData, 0, 10);
            fullData[10] = akses.getkode(); // nip_petugas

            // Gunakan queryu2tf dengan 11 parameter
            if (Sequel.queryu2tf(sql, 11, fullData)) {
                // Ambil ID yang baru dibuat
                idTerapiAktif = Sequel.cariIsi("SELECT LAST_INSERT_ID()");

                // ===== AUTO ADD FIRST DAY TERAPI =====
                boolean berhasilTambahHari = false;
                String statusTerapi = "draft";
                String pesanTambahHari = "";

                try {
                    // Otomatis tambah hari pertama terapi
                    berhasilTambahHari = autoTambahHariPertama(idTerapiAktif);

                    if (berhasilTambahHari) {
                        currentHariCount = 1;
                        statusTerapi = "aktif";
                        pesanTambahHari = "\n🎯 HARI PERTAMA TERAPI OTOMATIS DITAMBAHKAN!";
                    } else {
                        pesanTambahHari = "\n⚠️ Obat tersimpan tapi gagal menambah hari pertama.\nSilahkan klik 'Tambah Hari' manual.";
                    }
                } catch (Exception e) {
                    System.out.println("Error auto tambah hari pertama: " + e);
                    pesanTambahHari = "\n⚠️ Obat tersimpan tapi gagal menambah hari pertama.\nSilahkan klik 'Tambah Hari' manual.";
                }

                String pesanSukses = "✅ DATA OBAT BERHASIL DISIMPAN!\n\n";
                pesanSukses += "Nama Obat: " + nmObat.getText() + "\n";
                pesanSukses += "Jenis: " + (finalJenisObat.equals("rs") ? "Obat RS" : "Obat Luar") + "\n";
                pesanSukses += "Dosis: " + dosis.getText() + "\n";
                pesanSukses += "Cara Pemberian: " + caraPemberian.getSelectedItem() + "\n";
                if (!keteranganText.isEmpty()) {
                    pesanSukses += "Keterangan: " + keteranganText + "\n";
                }
                pesanSukses += "Tanggal Mulai: " + Valid.SetTgl(tglMulai.getSelectedItem() + "") + "\n";
                pesanSukses += "Status: " + statusTerapi.toUpperCase() + pesanTambahHari;

                if (berhasilTambahHari) {
                    pesanSukses += "\n\n📋 LANGKAH SELANJUTNYA:\n";
                    pesanSukses += "1. Terapi sudah AKTIF dengan hari pertama\n";
                    pesanSukses += "2. Gunakan 'Tambah Hari' untuk hari berikutnya\n";
                    pesanSukses += "3. Klik checkbox 'Diberikan' saat obat diberikan";
                } else {
                    pesanSukses += "\n\n📋 LANGKAH SELANJUTNYA:\n";
                    pesanSukses += "1. Klik obat yang baru disimpan di tabel\n";
                    pesanSukses += "2. Gunakan tombol 'Tambah Hari' untuk membuat jadwal\n";
                    pesanSukses += "3. Status akan berubah menjadi 'AKTIF' setelah ada jadwal";
                }

                JOptionPane.showMessageDialog(this, pesanSukses, "Obat Tersimpan", JOptionPane.INFORMATION_MESSAGE);

                // ===== AUTO REFRESH SETELAH SIMPAN SUKSES =====
                // 1. Refresh tabel terapi obat (seperti klik "Tampilkan Data")
                BtnCariActionPerformed(null);

                // 2. Auto-select obat yang baru disimpan di tabel
                autoSelectNewObat();

                // 3. Refresh tabel pemantauan juga
                tampilPemantauan();

                // 4. Update UI counter jika berhasil tambah hari
                if (berhasilTambahHari) {
                    jumlahHari.setText("1");
                    updateButtonState();
                }

                // 5. Show status message
                System.out.println("✅ Data berhasil disimpan" + (berhasilTambahHari ? " dan hari pertama ditambahkan" : ""));

                return true;
            }

            return false;

        } catch (Exception e) {
            System.out.println("Error simpan terapi obat: " + e);
            JOptionPane.showMessageDialog(this, "Gagal menyimpan data: " + e.getMessage());
            return false;
        }
    }

    /**
     * METHOD BARU: Auto tambah hari pertama terapi
     */
    private boolean autoTambahHariPertama(String idTerapi) {
        try {
            // 1. Ambil tanggal mulai terapi
            String sqlTerapi = "SELECT tgl_mulai_terapi, nama_obat FROM pemantauan_terapi_obat WHERE id = ?";
            String tglMulaiStr = Sequel.cariIsi(sqlTerapi, idTerapi);

            if (tglMulaiStr.isEmpty()) {
                System.out.println("Error: Tidak bisa mengambil tanggal mulai terapi");
                return false;
            }

            // 2. Gunakan tanggal mulai sebagai hari pertama
            String tanggalHariPertama = tglMulaiStr; // Format sudah yyyy-MM-dd

            // 3. Cek apakah hari pertama sudah ada (seharusnya belum)
            String sqlCek = "SELECT COUNT(*) FROM pemantauan_terapi_obat_harian WHERE id_terapi = ? AND tgl_pemberian = ?";
            int jumlahAda = Sequel.cariInteger(sqlCek, idTerapi, tanggalHariPertama);

            if (jumlahAda > 0) {
                System.out.println("Warning: Hari pertama sudah ada, skip auto tambah");
                return false;
            }

            // 4. Insert hari pertama terapi
            String sqlInsert = "INSERT INTO pemantauan_terapi_obat_harian "
                    + "(id_terapi, tgl_pemberian, status_pemberian, keterangan, nip_petugas) "
                    + "VALUES (?, ?, ?, ?, ?)";

            String[] dataHarian = new String[5];
            dataHarian[0] = idTerapi;                           // id_terapi
            dataHarian[1] = tanggalHariPertama;                 // tgl_pemberian (tanggal mulai)
            dataHarian[2] = "tidak";                            // status_pemberian
            dataHarian[3] = "";   // keterangan
            dataHarian[4] = akses.getkode();                    // nip_petugas

            if (Sequel.queryu2tf(sqlInsert, 5, dataHarian)) {
                // 5. Update jumlah hari dan status di terapi utama
                String sqlUpdate = "UPDATE pemantauan_terapi_obat SET jumlah_hari = 1, status = 'aktif' WHERE id = ?";
                String[] dataUpdate = {idTerapi};

                if (Sequel.queryu2tf(sqlUpdate, 1, dataUpdate)) {
                    System.out.println("✅ Auto tambah hari pertama berhasil untuk terapi ID: " + idTerapi);
                    return true;
                }
            }

            return false;

        } catch (Exception e) {
            System.out.println("Error auto tambah hari pertama: " + e);
            return false;
        }
    }

    /**
     * METHOD TAMBAHAN: Auto-select obat yang baru disimpan di tabel
     */
    private void autoSelectNewObat() {
        try {
            if (!idTerapiAktif.equals("") && tbTerapiObat.getRowCount() > 0) {
                // Cari row dengan ID yang baru disimpan
                for (int i = 0; i < tbTerapiObat.getRowCount(); i++) {
                    String idDiTabel = tbTerapiObat.getValueAt(i, 0).toString();
                    if (idDiTabel.equals(idTerapiAktif)) {
                        // Select row yang baru disimpan
                        tbTerapiObat.setRowSelectionInterval(i, i);

                        // Scroll ke row yang dipilih
                        tbTerapiObat.scrollRectToVisible(tbTerapiObat.getCellRect(i, 0, true));

                        // Load data ke form
                        getDataTerapi();

                        System.out.println("✅ Auto-selected new obat at row: " + i);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error auto-select new obat: " + e);
        }
    }

    // ===== HELPER METHODS =====
    private boolean isDuplikatObat(String noRawat, String namaObat, String jenisObat) {
        try {
            String sql = "SELECT COUNT(*) FROM pemantauan_terapi_obat "
                    + "WHERE no_rawat = ? AND LOWER(nama_obat) = LOWER(?) AND jenis_obat = ? "
                    + "AND status IN ('aktif', 'ditunda')";

            // Menggunakan sekuel untuk cek duplikasi
            PreparedStatement ps = koneksi.prepareStatement(sql);
            ps.setString(1, noRawat);
            ps.setString(2, namaObat.trim());
            ps.setString(3, jenisObat);
            ResultSet rs = ps.executeQuery();

            boolean duplikat = false;
            if (rs.next()) {
                duplikat = (rs.getInt(1) > 0);
            }

            rs.close();
            ps.close();

            if (duplikat) {
                String jenisLabel = jenisObat.equals("rs") ? "Obat RS" : "Obat Luar";
                JOptionPane.showMessageDialog(this,
                        "Obat yang sama sudah ada dalam terapi aktif!\n\n"
                        + "Nama: " + namaObat + "\n"
                        + "Jenis: " + jenisLabel + "\n"
                        + "Pasien: " + TNmPasien.getText() + "\n\n"
                        + "Silahkan cek data terapi yang sudah ada.",
                        "Duplikasi Obat",
                        JOptionPane.WARNING_MESSAGE);
            }

            return duplikat;

        } catch (Exception e) {
            System.out.println("Error cek duplikasi: " + e);
            return false;
        }
    }

    private String generateKodeObatLuar() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String tanggal = sdf.format(new Date());

            String sqlCount = "SELECT COUNT(*) as jumlah FROM pemantauan_terapi_obat "
                    + "WHERE jenis_obat='luar' AND DATE(waktu_simpan) = CURDATE()";
            PreparedStatement psCount = koneksi.prepareStatement(sqlCount);
            ResultSet rsCount = psCount.executeQuery();

            int urutan = 1;
            if (rsCount.next()) {
                urutan = rsCount.getInt("jumlah") + 1;
            }

            rsCount.close();
            psCount.close();

            return String.format("LUAR-%s-%03d", tanggal, urutan);

        } catch (Exception e) {
            System.out.println("Error generate kode obat luar: " + e);
            return "LUAR-" + System.currentTimeMillis();
        }
    }

    private boolean isValidObatInput() {
        String namaObat = nmObat.getText().trim();
        String caraObat = "";

        // Ambil nilai dari ComboBox
        if (caraPemberian.getSelectedItem() != null) {
            caraObat = caraPemberian.getSelectedItem().toString().trim();
        }

        if (namaObat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama obat tidak boleh kosong!");
            nmObat.requestFocus();
            return false;
        }

        if (namaObat.length() < 3) {
            JOptionPane.showMessageDialog(this, "Nama obat minimal 3 karakter!");
            nmObat.requestFocus();
            return false;
        }

        if (caraObat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cara pemberian obat tidak boleh kosong!");
            caraPemberian.requestFocus();
            return false;
        }

        // Validasi khusus untuk obat luar
        if (!isObatFromRS) {
            return validateObatLuar(namaObat);
        }

        return true;
    }

    private boolean validateObatLuar(String namaObat) {
        // Cek karakter khusus yang tidak diizinkan
        if (namaObat.matches(".*[<>\"'%;()&+].*")) {
            JOptionPane.showMessageDialog(this,
                    "Nama obat tidak boleh mengandung karakter khusus: < > \" ' % ; ( ) & +");
            return false;
        }

        // Validasi panjang maksimal
        if (namaObat.length() > 100) {
            JOptionPane.showMessageDialog(this, "Nama obat maksimal 100 karakter!");
            return false;
        }

        return true;
    }

    private String convertTanggalForDB(String tanggal) {
        try {
            if (tanggal.contains("-")) {
                String[] parts = tanggal.split("-");
                if (parts[0].length() == 4) {
                    return tanggal; // Sudah format yyyy-MM-dd
                } else {
                    return parts[2] + "-" + parts[1] + "-" + parts[0]; // Convert dd-MM-yyyy ke yyyy-MM-dd
                }
            } else if (tanggal.contains("/")) {
                String[] parts = tanggal.split("/");
                return parts[2] + "-" + parts[1] + "-" + parts[0]; // Convert dd/MM/yyyy
            }
            return tanggal;
        } catch (Exception e) {
            System.out.println("Error convert tanggal: " + e);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                return sdf.format(new Date());
            } catch (Exception ex) {
                return tanggal;
            }
        }
    }

    // ===== EVENT HANDLERS =====
    private void formWindowOpened(java.awt.event.WindowEvent evt) {
        // Set tanggal mulai default jika belum ada
        if (tglMulai.getDate() == null) {
            tglMulai.setDate(new Date());
        }

        // Update button state untuk memastikan tombol obat aktif jika ada pasien
        updateButtonState();

        tampilTerapiObat();
        tampilPemantauan();
    }

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
        simpanTerapiObat();
    }

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {
        emptTeks();
    }

    private void BtnObatActionPerformed(java.awt.event.ActionEvent evt) {
        // Jika belum ada pasien yang dipilih
        if (TNoRW.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this,
                    "❌ BELUM ADA PASIEN!\n\n"
                    + "Silahkan pilih pasien terlebih dahulu melalui:\n"
                    + "• Dialog pencarian pasien\n"
                    + "• Form registrasi\n"
                    + "• Form rawat inap/jalan",
                    "Pilih Pasien Dulu",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // AKTIFKAN MODE INPUT OTOMATIS saat tombol obat diklik
        enableInputMode();

        // Reset pencarian sebelum buka dialog
        TCari1.setText("");

        // Buka dialog pemilihan obat RS
        WindowObat.setSize(720, 500);
        WindowObat.setLocationRelativeTo(internalFrame1);
        WindowObat.setAlwaysOnTop(false);
        WindowObat.setVisible(true);

        // Tampilkan semua obat terlebih dahulu
        tampilObat();

        // Focus ke textbox pencarian dan beri hint
        TCari1.requestFocus();
        TCari1.setToolTipText("<html><b>Tips Pencarian:</b><br/>"
                + "• Ketik nama obat lalu tekan <b>ENTER</b><br/>"
                + "• Tekan <b>ESC</b> untuk clear pencarian<br/>"
                + "• Tekan <b>F5</b> untuk refresh data</html>");
    }

    private void BtnTambahHariActionPerformed(java.awt.event.ActionEvent evt) {
        tambahHariBerikutnya();
    }

    private void BtnResetJadwalActionPerformed(java.awt.event.ActionEvent evt) {
        resetJadwalTerapi();
    }

    private void BtnHapusHariTerakhirActionPerformed(java.awt.event.ActionEvent evt) {
        hapusHariTerakhir();
    }

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {
        if (tbTerapiObat.getSelectedRow() > -1) {
            getDataTerapi();
            isEditMode = true;
            updateButtonState();
            nmObat.requestFocus();
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih salah satu datanya terlebih dulu..!!");
        }
    }

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {
        if (tbTerapiObat.getSelectedRow() > -1) {
            JOptionPane.showMessageDialog(this,
                    "Untuk menghapus obat terapi, gunakan menu klik kanan pada tabel Data Terapi Obat:\n\n"
                    + "• Hapus Obat Terapi = Hapus seluruh obat + jadwal\n"
                    + "• Atau klik kanan pada Pemantauan Harian untuk hapus hari tertentu",
                    "Info Hapus",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih salah satu datanya terlebih dahulu..!!");
        }
    }

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
        } else {
            JOptionPane.showMessageDialog(null, "Fitur cetak akan segera tersedia!");
        }
    }

    private void BtnStatistikActionPerformed(java.awt.event.ActionEvent evt) {
        tampilkanStatistikObat();
    }

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
        WindowObat.dispose();
    }

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariActionPerformed(null);
        }
    }

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {
        tampilTerapiObat();
    }

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {
        TCari.setText("");
        tampilTerapiObat();
    }

    private void BtnCari1ActionPerformed(java.awt.event.ActionEvent evt) {
        tampilObat();

        // Focus kembali ke textbox pencarian untuk kemudahan user
        TCari1.requestFocus();
        TCari1.selectAll(); // Select all text untuk memudahkan overwrite
    }

    private void BtnCloseIn1ActionPerformed(java.awt.event.ActionEvent evt) {
        // Reset pencarian saat tutup dialog
        TCari1.setText("");

        // Reset border title ke default
        internalFrame6.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 255), 3),
                "::[ Daftar Obat RS (Master Farmasi) ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 1, 12)
        ));

        WindowObat.dispose();
    }

    // 7. TAMBAHKAN METHOD untuk real-time search (optional - untuk pencarian langsung saat mengetik)
    private void setupRealTimeSearch() {
        // Timer untuk delay pencarian (mencegah query terlalu sering)
        javax.swing.Timer searchTimer = new javax.swing.Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tampilObat();
            }
        });
        searchTimer.setRepeats(false);

        // Document listener untuk real-time search
        TCari1.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                searchTimer.restart();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                searchTimer.restart();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                searchTimer.restart();
            }
        });
    }

    private void nmObatKeyPressed(java.awt.event.KeyEvent evt) {
        // Handle special keys tanpa mereset obat RS
        if (evt.getKeyCode() == KeyEvent.VK_TAB
                || evt.getKeyCode() == KeyEvent.VK_ENTER
                || evt.getKeyCode() == KeyEvent.VK_SHIFT
                || evt.getKeyCode() == KeyEvent.VK_CONTROL
                || evt.getKeyCode() == KeyEvent.VK_ALT
                || evt.getKeyCode() == KeyEvent.VK_UP
                || evt.getKeyCode() == KeyEvent.VK_DOWN
                || evt.getKeyCode() == KeyEvent.VK_LEFT
                || evt.getKeyCode() == KeyEvent.VK_RIGHT) {

            // Navigate ke field berikutnya untuk TAB/ENTER
            if (evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_TAB) {
                Valid.pindah(evt, nmObat, dosis);
            }
            return;
        }

        // Jika user mengetik karakter, berarti input obat luar
        // Reset data obat RS hanya jika user benar-benar mengetik
        if (isObatFromRS && nmObat.getText().length() > 0) {
            // Konfirmasi apakah user mau switch ke obat luar
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Anda akan mengganti dari Obat RS ke Obat Luar.\n"
                    + "Lanjutkan?",
                    "Konfirmasi Ganti Jenis Obat",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                resetObatRS();
            } else {
                // Cancel the keystroke
                evt.consume();
                return;
            }
        }

        // Set tanggal mulai jika belum ada
        if (tglMulai.getDate() == null) {
            tglMulai.setDate(new Date());
        }

        updateButtonState();
    }

    private void dosisKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, nmObat, caraPemberian);
    }

    private boolean isFormReadyForObatInput() {
        return !TNoRW.getText().trim().equals("") && tglMulai.getDate() != null;
    }

    private void caraPemberianKeyPressed(java.awt.event.KeyEvent evt) {
        // Handle navigasi untuk ComboBox
        if (evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_TAB) {
            keterangan.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_UP && evt.isShiftDown()) {
            dosis.requestFocus();
        }
    }

    private void tglMulaiPropertyChange(java.beans.PropertyChangeEvent evt) {
        updateButtonState();
    }

    private void tbTerapiObatMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabMode.getRowCount() != 0) {
            if (evt.getClickCount() == 1) {
                try {
                    getDataTerapi();
                    tampilPemantauan();
                } catch (java.lang.NullPointerException e) {
                    System.out.println("Error selecting terapi data: " + e);
                }
            }
        }
    }

    private void tbTerapiObatKeyPressed(java.awt.event.KeyEvent evt) {
        if (tabMode.getRowCount() != 0) {
            if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                try {
                    getDataTerapi();
                    tampilPemantauan();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }

    // ===== MODIFIKASI METHOD YANG SUDAH ADA =====
    // ===== MODIFIKASI METHOD YANG SUDAH ADA =====
    /**
     * Modifikasi tbPemantauanMouseClicked untuk menambah popup menu tanggal
     */
    private void tbPemantauanMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabModeTerapi.getRowCount() != 0) {
            int row = tbPemantauan.getSelectedRow();
            int col = tbPemantauan.getSelectedColumn();

            // KLIK KANAN untuk popup menu edit tanggal
            if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3 && col == 2) { // Klik kanan di kolom tanggal
                if (!idTerapiAktif.equals("")) {
                    popupMenuTanggal.show(tbPemantauan, evt.getX(), evt.getY());
                }
                return;
            }

            // DOUBLE CLICK untuk edit tanggal langsung (cara alternatif)
            if (col == 2 && evt.getClickCount() == 2 && !idTerapiAktif.equals("")) {
                editTanggalManual();
                return;
            }

            // SINGLE CLICK untuk checkbox "Diberikan" (kolom 3)
            if (col == 3 && evt.getClickCount() == 1 && !idTerapiAktif.equals("")) {
                Boolean diberikanObj = (Boolean) tbPemantauan.getValueAt(row, 3);
                boolean diberikan = (diberikanObj != null) ? diberikanObj : false;
                updateStatusPemberian(row, diberikan);
            }

            // SINGLE CLICK untuk checkbox "Cek" (kolom 0) untuk bulk action
            if (col == 0 && evt.getClickCount() == 1) {
                Boolean cekObj = (Boolean) tbPemantauan.getValueAt(row, 0);
                boolean cek = (cekObj != null) ? cekObj : false;
                // Handle checkbox cek jika diperlukan
                System.out.println("Checkbox cek untuk row " + row + ": " + cek);
            }
        }
    }

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabMode1.getRowCount() != 0) {
            if (evt.getClickCount() == 2) {
                if (tbObat.getSelectedRow() != -1) {
                    // Set data obat dari RS
                    kdobat = tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString();
                    nmObat.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());

                    // Set flag obat dari RS
                    jenisObat = "rs";
                    isObatFromRS = true;
                    updateStatusObatLabel();

                    // Set tanggal mulai jika belum ada
                    if (tglMulai.getDate() == null) {
                        tglMulai.setDate(new Date());
                    }

                    updateButtonState();
                    WindowObat.dispose();
                    dosis.requestFocus();
                }
            }
        }
    }

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {
        if (tabMode1.getRowCount() != 0) {
            if (evt.getKeyCode() == KeyEvent.VK_SPACE || evt.getKeyCode() == KeyEvent.VK_ENTER) {
                if (tbObat.getSelectedRow() != -1) {
                    // Set data obat dari RS
                    kdobat = tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString();
                    nmObat.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());

                    // Set flag obat dari RS
                    jenisObat = "rs";
                    isObatFromRS = true;
                    updateStatusObatLabel();

                    // Set tanggal mulai jika belum ada
                    if (tglMulai.getDate() == null) {
                        tglMulai.setDate(new Date());
                    }

                    updateButtonState();
                    WindowObat.dispose();
                    dosis.requestFocus();
                }
            }
        }
    }

    // ===== ADDITIONAL HELPER METHODS =====
    private void resetObatRS() {
        kdobat = "";
        jenisObat = "luar";
        isObatFromRS = false;
        updateStatusObatLabel();
    }

    private void updateStatusObatLabel() {
        if (isObatFromRS) {
            lblStatusObat.setText("Obat RS");
            lblStatusObat.setForeground(new Color(0, 153, 0)); // Green
            nmObat.setBackground(new Color(204, 255, 204)); // Light green
        } else {
            lblStatusObat.setText("Obat Luar");
            lblStatusObat.setForeground(new Color(255, 102, 0)); // Orange
            nmObat.setBackground(new Color(255, 255, 204)); // Light yellow
        }
    }

    private void enableInputMode() {
        // Reset ID terapi aktif jika ada
        idTerapiAktif = "";

        // Enable semua field input
        nmObat.setEditable(true);
        dosis.setEditable(true);
        caraPemberian.setEnabled(true);
        tglMulai.setEnabled(true);
        keterangan.setEditable(true); // FIX: Pastikan keterangan bisa diedit

        // Reset background ke putih (mode input)
        Color inputColor = Color.WHITE;
        nmObat.setBackground(inputColor);
        dosis.setBackground(inputColor);
        caraPemberian.setBackground(inputColor);
        keterangan.setBackground(inputColor); // FIX: Reset background keterangan

        // Clear tabel pemantauan
        Valid.tabelKosong(tabModeTerapi);

        // Reset counter hari ke 0
        currentHariCount = 0;
        jumlahHari.setText("0");
        isEditMode = false;

        // Set tanggal mulai ke hari ini jika belum ada
        if (tglMulai.getDate() == null) {
            tglMulai.setDate(new Date());
        }

        // Update button state - TOMBOL OBAT AKAN TETAP AKTIF
        updateButtonState();
    }

    private void updateButtonState() {
        boolean hasTanggalMulai = (tglMulai.getDate() != null);
        boolean hasObat = !nmObat.getText().trim().isEmpty();
        boolean hasPasien = !TNoRW.getText().trim().equals("");
        boolean isInInputMode = nmObat.isEditable();

        // TOMBOL SIMPAN - Aktif jika dalam mode input dan ada data minimal
        BtnSimpan.setEnabled(hasPasien && isInInputMode && hasObat && hasTanggalMulai);

        // TOMBOL OBAT (RS) - SELALU AKTIF jika ada pasien (PERUBAHAN UTAMA)
        BtnObat.setEnabled(hasPasien);

        // TOMBOL TAMBAH HARI: Aktif jika ada terapi yang dipilih
        BtnTambahHari.setEnabled(!idTerapiAktif.equals("") && currentHariCount < MAX_HARI);

        // Update tooltip berdasarkan kondisi
        if (!hasPasien) {
            BtnObat.setToolTipText("Pilih pasien terlebih dahulu");
            BtnSimpan.setToolTipText("Pilih pasien terlebih dahulu");
            BtnTambahHari.setToolTipText("Pilih pasien terlebih dahulu");
        } else {
            // TOMBOL OBAT SELALU AKTIF dengan tooltip yang sesuai
            if (isInInputMode) {
                BtnObat.setToolTipText("F1 - Pilih obat dari Master RS");
            } else {
                BtnObat.setToolTipText("Klik untuk input obat baru");
            }

            // Tooltip untuk tombol lain
            if (isInInputMode) {
                if (hasObat && hasTanggalMulai) {
                    BtnSimpan.setToolTipText("Simpan obat terapi (Alt+S)");
                } else if (!hasObat) {
                    BtnSimpan.setToolTipText("Input nama obat terlebih dahulu");
                } else {
                    BtnSimpan.setToolTipText("Set tanggal mulai terapi");
                }
                BtnTambahHari.setToolTipText("Simpan obat dulu untuk bisa tambah jadwal");
            } else if (!idTerapiAktif.equals("")) {
                BtnSimpan.setToolTipText("Klik tombol 'RS' untuk input obat baru");
                BtnTambahHari.setToolTipText("Alt+T - Tambah hari ke-" + (currentHariCount + 1));
            }
        }
    }

    private void resetJadwalTerapi() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Yakin ingin mereset seluruh jadwal terapi?\nSemua hari yang sudah ditambahkan akan dihapus.",
                "Konfirmasi Reset",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            currentHariCount = 0;
            jumlahHari.setText("0");
            Valid.tabelKosong(tabModeTerapi);
            updateButtonState();
            JOptionPane.showMessageDialog(null, "Jadwal terapi telah direset!");
        }
    }

    private void hapusHariTerakhir() {
        if (currentHariCount <= 0) {
            JOptionPane.showMessageDialog(null, "Tidak ada hari yang bisa dihapus!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Yakin ingin menghapus hari ke-" + currentHariCount + "?",
                "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (tabModeTerapi.getRowCount() > 0) {
                tabModeTerapi.removeRow(tabModeTerapi.getRowCount() - 1);
            }

            currentHariCount--;
            jumlahHari.setText(String.valueOf(currentHariCount));
            updateButtonState();

            JOptionPane.showMessageDialog(null, "Hari ke-" + (currentHariCount + 1) + " telah dihapus!");
        }
    }

    private void getDataTerapi() {
        if (tbTerapiObat.getSelectedRow() != -1) {
            try {
                // Clear mode edit dan reset counter hari UI
                isEditMode = false;
                currentHariCount = 0;

                // Load data terapi yang dipilih dari tabel
                int selectedRow = tbTerapiObat.getSelectedRow();
                idTerapiAktif = tbTerapiObat.getValueAt(selectedRow, 0).toString();
                nmObat.setText(tbTerapiObat.getValueAt(selectedRow, 1).toString());
                dosis.setText(tbTerapiObat.getValueAt(selectedRow, 2).toString());
                String caraPemberianText = tbTerapiObat.getValueAt(selectedRow, 3).toString();
                caraPemberian.setSelectedItem(caraPemberianText);

                // Load jenis obat untuk menentukan mode
                String jenisObatDB = tbTerapiObat.getValueAt(selectedRow, 8).toString();
                if (jenisObatDB.contains("RS") || jenisObatDB.contains("rs")) {
                    jenisObat = "rs";
                    isObatFromRS = true;
                } else {
                    jenisObat = "luar";
                    isObatFromRS = false;
                    kdobat = "";
                }

                // Update status obat label
                updateStatusObatLabel();

                // Load tanggal mulai terapi
                try {
                    String tglMulaiStr = tbTerapiObat.getValueAt(selectedRow, 4).toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                    Date tglMulaiDate = sdf.parse(tglMulaiStr);
                    tglMulai.setDate(tglMulaiDate);
                } catch (Exception e) {
                    System.out.println("Error parse tanggal: " + e);
                    tglMulai.setDate(new Date());
                }

                // Hitung jumlah hari yang sudah ada di database
                int jumlahHariDB = 0;
                try {
                    String jumlahHariStr = tbTerapiObat.getValueAt(selectedRow, 5).toString();
                    jumlahHariDB = Integer.parseInt(jumlahHariStr.trim());
                } catch (NumberFormatException e) {
                    System.out.println("Error parse jumlah hari: " + e);
                    jumlahHariDB = 0;
                }

                // Set counter sesuai database
                currentHariCount = jumlahHariDB;
                jumlahHari.setText(String.valueOf(currentHariCount));

                // FIX: Load keterangan dari database dengan lebih robust
                try {
                    String sql = "SELECT keterangan FROM pemantauan_terapi_obat WHERE id=?";
                    PreparedStatement ps = koneksi.prepareStatement(sql);
                    ps.setString(1, idTerapiAktif);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        String ket = rs.getString("keterangan");
                        // FIX: Handle null values properly
                        if (ket == null) {
                            ket = "";
                        }
                        keterangan.setText(ket);
                        System.out.println("Loaded keterangan: '" + ket + "'"); // Debug log
                    } else {
                        keterangan.setText("");
                        System.out.println("No keterangan found for ID: " + idTerapiAktif);
                    }
                    rs.close();
                    ps.close();
                } catch (Exception e) {
                    System.out.println("Error load keterangan: " + e);
                    keterangan.setText("");
                }

                // Set field menjadi read-only saat pilih obat
                nmObat.setEditable(false);
                dosis.setEditable(false);
                caraPemberian.setEnabled(false);
                tglMulai.setEnabled(false);
                keterangan.setEditable(false); // FIX: Pastikan keterangan juga read-only saat pilih obat

                // Set background form untuk indikasi mode pilih obat
                Color readOnlyColor = new Color(245, 245, 245); // Light gray
                nmObat.setBackground(readOnlyColor);
                dosis.setBackground(readOnlyColor);
                caraPemberian.setBackground(readOnlyColor);
                caraPemberian.setEnabled(false); // Disable saat mode view
                keterangan.setBackground(readOnlyColor); // FIX: Tambahkan background untuk keterangan

                // Update button state
                updateButtonState();

            } catch (Exception e) {
                System.out.println("Error getDataTerapi: " + e);
                JOptionPane.showMessageDialog(this,
                        "Error saat memuat data terapi:\n" + e.getMessage(),
                        "Error Load Data",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean isValidCaraPemberian(String caraPemberian) {
        if (caraPemberian == null || caraPemberian.trim().isEmpty()) {
            return false;
        }

        // Daftar singkatan yang valid
        String[] validRoutes = {
            "PO", "IV", "IM", "SC", "INH", "PR", "PV", "SL", "TOP",
            "OD", "OS", "OU", "AD", "AS", "AU", "NAS", "ID", "IT", "INF", "NEB"
        };

        String route = caraPemberian.trim().toUpperCase();

        // Cek apakah ada di daftar standar
        for (String validRoute : validRoutes) {
            if (validRoute.equals(route)) {
                return true;
            }
        }

        // Jika bukan standar, validasi format custom
        // Maksimal 10 karakter, hanya huruf dan angka
        if (route.length() <= 10 && route.matches("^[A-Z0-9\\-]+$")) {
            return true;
        }

        return false;
    }

    // 3. MODIFIKASI METHOD tampilObat() untuk handling pencarian yang lebih baik
    private void tampilObat() {
        Valid.tabelKosong(tabMode1);
        try {
            String keyword = TCari1.getText().trim();
            String sql;
            PreparedStatement ps;

            // Jika ada keyword pencarian
            if (!keyword.isEmpty()) {
                sql = "SELECT kode_brng, nama_brng, kode_sat FROM databarang WHERE "
                        + "(kode_brng LIKE ? OR nama_brng LIKE ? OR kode_sat LIKE ?) "
                        + "AND status='1' ORDER BY "
                        + "CASE "
                        + "  WHEN nama_brng LIKE ? THEN 1 " // Prioritas: nama yang dimulai dengan keyword
                        + "  WHEN nama_brng LIKE ? THEN 2 " // Prioritas: nama yang mengandung keyword
                        + "  ELSE 3 "
                        + "END, nama_brng";

                ps = koneksi.prepareStatement(sql);
                String likeKeyword = "%" + keyword + "%";
                String startWithKeyword = keyword + "%";

                ps.setString(1, likeKeyword);  // kode_brng LIKE
                ps.setString(2, likeKeyword);  // nama_brng LIKE
                ps.setString(3, likeKeyword);  // kode_sat LIKE
                ps.setString(4, startWithKeyword); // Priority 1: dimulai dengan keyword
                ps.setString(5, likeKeyword);  // Priority 2: mengandung keyword
            } // Jika tidak ada keyword, tampilkan semua (dengan limit untuk performa)
            else {
                sql = "SELECT kode_brng, nama_brng, kode_sat FROM databarang WHERE "
                        + "status='1' ORDER BY nama_brng LIMIT 500";
                ps = koneksi.prepareStatement(sql);
            }

            ResultSet rs = ps.executeQuery();
            int count = 0;

            while (rs.next()) {
                tabMode1.addRow(new Object[]{
                    rs.getString("kode_brng"),
                    rs.getString("nama_brng"),
                    rs.getString("kode_sat")
                });
                count++;
            }

            rs.close();
            ps.close();

            // Update info hasil pencarian di title border
            updateSearchResultInfo(keyword, count);

        } catch (Exception e) {
            System.out.println("Error tampil obat: " + e);
            JOptionPane.showMessageDialog(WindowObat,
                    "Error saat mencari obat: " + e.getMessage(),
                    "Error Pencarian",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // 4. TAMBAHKAN METHOD untuk update info hasil pencarian (method baru)
    private void updateSearchResultInfo(String keyword, int count) {
        try {
            String titleInfo;
            if (keyword.isEmpty()) {
                titleInfo = "::[ Daftar Obat RS (Master Farmasi) - Menampilkan " + count + " obat ]::";
            } else {
                titleInfo = "::[ Hasil Pencarian: \"" + keyword + "\" - " + count + " obat ditemukan ]::";
            }

            // Update border title
            internalFrame6.setBorder(javax.swing.BorderFactory.createTitledBorder(
                    javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 255), 3),
                    titleInfo,
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION,
                    new java.awt.Font("Tahoma", 1, 12)
            ));

            // Update tampilan
            internalFrame6.repaint();

        } catch (Exception e) {
            System.out.println("Error update search result info: " + e);
        }
    }

    public void emptTeks() {
        nmObat.setText("");
        dosis.setText("");
        caraPemberian.setSelectedIndex(0);
        keterangan.setText(""); // FIX: Reset keterangan

        // Reset data obat
        kdobat = "";
        jenisObat = "";
        isObatFromRS = false;
        idTerapiAktif = "";

        // Reset visual indicator
        nmObat.setBackground(Color.WHITE);
        dosis.setBackground(Color.WHITE);
        caraPemberian.setBackground(Color.WHITE);
        keterangan.setBackground(Color.WHITE); // FIX: Reset background keterangan

        lblStatusObat.setText("Obat Luar");
        lblStatusObat.setForeground(new Color(255, 102, 0));

        // Reset counter dan mode
        currentHariCount = 0;
        isEditMode = false;
        jumlahHari.setText("0");

        // Set tanggal mulai ke hari ini
        tglMulai.setDate(new Date());
        Valid.tabelKosong(tabModeTerapi);

        // Enable semua field untuk input
        nmObat.setEditable(true);
        dosis.setEditable(true);
        caraPemberian.setEnabled(true);
        tglMulai.setEnabled(true);
        keterangan.setEditable(true); // FIX: Enable keterangan untuk input

        // Update button state - tombol obat akan aktif jika ada pasien
        updateButtonState();
        nmObat.requestFocus();
    }

    /**
     * Added method to validate keterangan field
     */
    private boolean validateKeterangan() {
        String keteranganText = keterangan.getText().trim();

        // Keterangan boleh kosong, tapi jika diisi harus valid
        if (!keteranganText.isEmpty()) {
            // Check length
            if (keteranganText.length() > 200) {
                JOptionPane.showMessageDialog(this,
                        "Keterangan terlalu panjang (maksimal 200 karakter)!\n"
                        + "Panjang saat ini: " + keteranganText.length() + " karakter");
                keterangan.requestFocus();
                return false;
            }

            // Check for invalid characters
            if (keteranganText.matches(".*[<>\"'%;()&+].*")) {
                JOptionPane.showMessageDialog(this,
                        "Keterangan tidak boleh mengandung karakter khusus: < > \" ' % ; ( ) & +");
                keterangan.requestFocus();
                return false;
            }
        }

        return true;
    }

    public void setData(String norw, String norm, String nmpasien, String sttsrawat, String unit) {
        TNoRW.setText(norw);
        TNoRM.setText(norm);
        TNmPasien.setText(nmpasien);
        nmUnit.setText(unit);
        status = sttsrawat;
        TCari.setText(norw);

        // Set default date range
        DTPCari1.setDate(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 30);
        DTPCari2.setDate(cal.getTime());

        // Set tanggal mulai default
        tglMulai.setDate(new Date());

        // AKTIFKAN MODE INPUT dan update button state
        enableInputMode();

        // Tampilkan data
        tampilTerapiObat();
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getberi_obat());
        BtnHapus.setEnabled(akses.getberi_obat());
        BtnEdit.setEnabled(akses.getberi_obat());
        BtnPrint.setEnabled(akses.getberi_obat());
        BtnStatistik.setEnabled(akses.getberi_obat());
    }

    // ===== INITIALIZATION METHODS =====
    private void initContextMenu() {
        popupMenuTerapi = new JPopupMenu();

        // Menu Lihat Detail
        menuLihatDetail = new JMenuItem("Lihat Detail Terapi");
        menuLihatDetail.setIcon(new ImageIcon(getClass().getResource("/picture/search_page.png")));
        menuLihatDetail.addActionListener(e -> contextMenuLihatDetail());

        // Menu Edit Jadwal
        menuEditJadwal = new JMenuItem("Edit Jadwal Terapi");
        menuEditJadwal.setIcon(new ImageIcon(getClass().getResource("/picture/inventaris.png")));
        menuEditJadwal.addActionListener(e -> contextMenuEditJadwal());

        // Menu Ubah Status
        menuUbahStatus = new JMenuItem("Ubah Status Terapi");
        menuUbahStatus.setIcon(new ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        menuUbahStatus.addActionListener(e -> contextMenuUbahStatus());

        // Menu Hapus Obat
        menuHapusObat = new JMenuItem("Hapus Obat Terapi");
        menuHapusObat.setIcon(new ImageIcon(getClass().getResource("/picture/delete.png")));
        menuHapusObat.addActionListener(e -> contextMenuHapusObat());

        // Separators
        separator1 = new JSeparator();
        separator2 = new JSeparator();

        // Add items to popup menu
        popupMenuTerapi.add(menuLihatDetail);
        popupMenuTerapi.add(separator1);
        popupMenuTerapi.add(menuEditJadwal);
        popupMenuTerapi.add(menuUbahStatus);
        popupMenuTerapi.add(separator2);
        popupMenuTerapi.add(menuHapusObat);
    }

    private void initContextMenuPemantauan() {
        popupMenuPemantauan = new JPopupMenu();

        // Menu Tambah Hari Berikutnya
        menuTambahHariPemantauan = new JMenuItem("Tambah Hari Berikutnya");
        menuTambahHariPemantauan.setIcon(new ImageIcon(getClass().getResource("/picture/category.png")));
        menuTambahHariPemantauan.addActionListener(e -> contextMenuTambahHariPemantauan());

        // Menu Hapus Jadwal Terapi Keseluruhan
        menuHapusJadwalPemantauan = new JMenuItem("Hapus Seluruh Jadwal Terapi");
        menuHapusJadwalPemantauan.setIcon(new ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        menuHapusJadwalPemantauan.addActionListener(e -> contextMenuHapusJadwalPemantauan());

        // Separator
        separatorPemantauan = new JSeparator();

        // Add items to popup menu
        popupMenuPemantauan.add(menuTambahHariPemantauan);
        popupMenuPemantauan.add(separatorPemantauan);
        popupMenuPemantauan.add(menuHapusJadwalPemantauan);
    }

    private void initTableModels() {
        // Inisialisasi tabel obat farmasi
        Object[] rowObat = {"Kode Obat", "Nama Obat", "Satuan"};
        tabMode1 = new DefaultTableModel(null, rowObat) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbObat.setModel(tabMode1);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 3; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(100);
            } else if (i == 1) {
                column.setPreferredWidth(350);
            } else if (i == 2) {
                column.setPreferredWidth(60);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        // Inisialisasi tabel terapi obat
        Object[] rowTerapi = {"ID", "Nama Obat", "Dosis", "Cara Pemberian", "Tgl. Mulai",
            "Jumlah Hari", "Progress", "Status", "Jenis", "Petugas"};
        tabMode = new DefaultTableModel(null, rowTerapi) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbTerapiObat.setModel(tabMode);
        tbTerapiObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbTerapiObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 10; i++) {
            TableColumn column = tbTerapiObat.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setMinWidth(0);
                column.setMaxWidth(0); // ID hidden
            } else if (i == 1) {
                column.setPreferredWidth(200); // Nama Obat
            } else if (i == 2) {
                column.setPreferredWidth(100); // Dosis
            } else if (i == 3) {
                column.setPreferredWidth(120); // Cara Pemberian
            } else if (i == 4) {
                column.setPreferredWidth(80);  // Tgl Mulai
            } else if (i == 5) {
                column.setPreferredWidth(80);  // Jumlah Hari
            } else if (i == 6) {
                column.setPreferredWidth(80);  // Progress
            } else if (i == 7) {
                column.setPreferredWidth(70);  // Status
            } else if (i == 8) {
                column.setPreferredWidth(90);  // Jenis
            } else if (i == 9) {
                column.setPreferredWidth(150); // Petugas
            }
        }
        tbTerapiObat.setDefaultRenderer(Object.class, new WarnaTable());
        tbTerapiObat.setComponentPopupMenu(popupMenuTerapi);

        // Inisialisasi tabel pemantauan harian
        Object[] rowPemantauan = {"Cek", "Hari Ke-", "Tanggal", "Diberikan",
            "Keterangan", "Petugas", "Waktu Input"};
        tabModeTerapi = new DefaultTableModel(null, rowPemantauan) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
                if (colIndex == 0 || colIndex == 3 || colIndex == 4) {
                    a = true;
                }
                return a;
            }
            Class[] types = new Class[]{
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class,
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class,
                java.lang.Object.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbPemantauan.setModel(tabModeTerapi);
        tbPemantauan.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbPemantauan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 7; i++) {
            TableColumn column = tbPemantauan.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(40);
            } else if (i == 1) {
                column.setPreferredWidth(70);
            } else if (i == 2) {
                column.setPreferredWidth(80);
            } else if (i == 3) {
                column.setPreferredWidth(70);
            } else if (i == 4) {
                column.setPreferredWidth(200);
            } else if (i == 5) {
                column.setPreferredWidth(120);
            } else if (i == 6) {
                column.setPreferredWidth(120);
            }
        }
        tbPemantauan.setDefaultRenderer(Object.class, new WarnaTable());
        tbPemantauan.setComponentPopupMenu(popupMenuPemantauan);
    }

    private void initEventHandlers() {
        // Event handler untuk perubahan data di tabel pemantauan
        // Implementasi sesuai kebutuhan
    }

    private void setupAutoSuggestObat() {
        // Setup auto-suggest functionality
        // Implementasi sesuai kebutuhan
    }

    // ===== CONTEXT MENU METHODS =====
    private void contextMenuLihatDetail() {
        int selectedRow = tbTerapiObat.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data terapi terlebih dahulu!");
            return;
        }

        getDataTerapi();
        tampilPemantauan();

        String namaObat = tbTerapiObat.getValueAt(selectedRow, 1).toString();
        String dosis = tbTerapiObat.getValueAt(selectedRow, 2).toString();
        String cara = tbTerapiObat.getValueAt(selectedRow, 3).toString();
        String tglMulai = tbTerapiObat.getValueAt(selectedRow, 4).toString();
        String progress = tbTerapiObat.getValueAt(selectedRow, 6).toString();
        String status = tbTerapiObat.getValueAt(selectedRow, 7).toString();
        String jenis = tbTerapiObat.getValueAt(selectedRow, 8).toString();

        String detail = "=== DETAIL TERAPI OBAT ===\n\n"
                + "Nama Obat: " + namaObat + "\n"
                + "Jenis: " + jenis + "\n"
                + "Dosis: " + dosis + "\n"
                + "Cara Pemberian: " + cara + "\n"
                + "Tanggal Mulai: " + tglMulai + "\n"
                + "Progress: " + progress + "\n"
                + "Status: " + status + "\n\n";

        JOptionPane.showMessageDialog(this, detail, "Detail Terapi", JOptionPane.INFORMATION_MESSAGE);
    }

    private void contextMenuEditJadwal() {
        int selectedRow = tbTerapiObat.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data terapi terlebih dahulu!");
            return;
        }

        getDataTerapi();
        isEditMode = true;
        updateButtonState();
        nmObat.requestFocus();

        JOptionPane.showMessageDialog(this,
                "Data terapi telah dimuat ke form.\nSilahkan edit dan klik 'Ganti' untuk menyimpan perubahan.",
                "Mode Edit Aktif",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void contextMenuUbahStatus() {
        int selectedRow = tbTerapiObat.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data terapi terlebih dahulu!");
            return;
        }

        String currentStatus = tbTerapiObat.getValueAt(selectedRow, 7).toString();
        String[] statusOptions = {"aktif", "selesai", "dihentikan", "ditunda"};

        String selectedStatus = (String) JOptionPane.showInputDialog(
                this,
                "Pilih status terapi:",
                "Ubah Status Terapi",
                JOptionPane.QUESTION_MESSAGE,
                null,
                statusOptions,
                currentStatus
        );

        if (selectedStatus != null && !selectedStatus.equals(currentStatus)) {
            updateStatusTerapi(selectedRow, selectedStatus);
        }
    }

    private void updateStatusTerapi(int selectedRow, String newStatus) {
        if (selectedRow < 0) {
            return;
        }

        String idTerapi = tbTerapiObat.getValueAt(selectedRow, 0).toString();
        String namaObat = tbTerapiObat.getValueAt(selectedRow, 1).toString();

        try {
            String[] dataUpdate = new String[2];
            dataUpdate[0] = newStatus;  // status
            dataUpdate[1] = idTerapi;   // id (WHERE)

            String updateClause = "status = ?";
            String whereClause = "id = ?";

            if (Sequel.mengedittf("pemantauan_terapi_obat", whereClause, updateClause, 2, dataUpdate)) {
                tbTerapiObat.setValueAt(newStatus.toUpperCase(), selectedRow, 7);
                JOptionPane.showMessageDialog(this,
                        "Status terapi berhasil diubah menjadi: " + newStatus.toUpperCase()
                        + "\nObat: " + namaObat);
                tampilTerapiObat();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal mengubah status terapi!");
            }
        } catch (Exception e) {
            System.out.println("Error update status terapi: " + e);
            JOptionPane.showMessageDialog(this, "Error mengubah status: " + e.getMessage());
        }
    }

    private void contextMenuHapusObat() {
        int selectedRow = tbTerapiObat.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih data obat terapi terlebih dahulu!");
            return;
        }

        String idTerapi = tbTerapiObat.getValueAt(selectedRow, 0).toString();
        String namaObat = tbTerapiObat.getValueAt(selectedRow, 1).toString();
        String jenisObat = tbTerapiObat.getValueAt(selectedRow, 8).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "=== KONFIRMASI HAPUS OBAT TERAPI ===\n\n"
                + "Nama Obat: " + namaObat + "\n"
                + "Jenis: " + jenisObat + "\n\n"
                + "Yakin ingin menghapus obat terapi ini?\n"
                + "Semua data pemantauan akan ikut terhapus!",
                "Konfirmasi Hapus Obat Terapi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Gunakan method dengan sekuel
            if (hapusTerapiObat(idTerapi)) {
                if (idTerapiAktif.equals(idTerapi)) {
                    emptTeks();
                }

                tampilTerapiObat();
                tampilPemantauan();

                JOptionPane.showMessageDialog(this,
                        "✅ OBAT TERAPI BERHASIL DIHAPUS!\n\nNama Obat: " + namaObat,
                        "Hapus Berhasil",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "❌ Gagal menghapus obat terapi!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void contextMenuTambahHariPemantauan() {
        tambahHariBerikutnya();
    }

    private void contextMenuHapusJadwalPemantauan() {
        if (!idTerapiAktif.equals("")) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Yakin ingin menghapus seluruh jadwal terapi?",
                    "Konfirmasi Hapus",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (hapusTerapiObat(idTerapiAktif)) {
                    emptTeks();
                    tampilTerapiObat();
                    JOptionPane.showMessageDialog(this, "Jadwal terapi berhasil dihapus!");
                }
            }
        }
    }

    /**
     * Main method untuk testing
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgPemantauanTerapiObat dialog = new DlgPemantauanTerapiObat(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    /**
     * Edit tanggal manual dengan dialog input
     */
    private void editTanggalManual() {
        int selectedRow = tbPemantauan.getSelectedRow();
        if (selectedRow < 0 || idTerapiAktif.equals("")) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih jadwal terapi terlebih dahulu!");
            return;
        }

        try {
            String tanggalLama = tbPemantauan.getValueAt(selectedRow, 2).toString();
            String hariKe = tbPemantauan.getValueAt(selectedRow, 1).toString();

            // Create custom dialog untuk input tanggal
            JDialog dialogTanggal = new JDialog(this, "Edit Tanggal Pemberian", true);
            dialogTanggal.setSize(400, 250);
            dialogTanggal.setLocationRelativeTo(this);
            dialogTanggal.setLayout(new java.awt.BorderLayout());

            // Panel utama
            javax.swing.JPanel panelUtama = new javax.swing.JPanel();
            panelUtama.setLayout(new java.awt.GridBagLayout());
            panelUtama.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

            java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
            gbc.insets = new java.awt.Insets(5, 5, 5, 5);

            // Label info
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            gbc.anchor = java.awt.GridBagConstraints.CENTER;
            javax.swing.JLabel lblInfo = new javax.swing.JLabel("<html><b>EDIT TANGGAL PEMBERIAN</b></html>");
            lblInfo.setFont(new java.awt.Font("Tahoma", 1, 14));
            panelUtama.add(lblInfo, gbc);

            // Info hari ke
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            gbc.anchor = java.awt.GridBagConstraints.CENTER;
            javax.swing.JLabel lblHari = new javax.swing.JLabel(hariKe + " - Terapi: " + getSelectedTerapiName());
            lblHari.setForeground(new java.awt.Color(102, 102, 102));
            panelUtama.add(lblHari, gbc);

            // Label tanggal lama
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 1;
            gbc.anchor = java.awt.GridBagConstraints.WEST;
            panelUtama.add(new javax.swing.JLabel("Tanggal Lama:"), gbc);

            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
            javax.swing.JTextField txtTanggalLama = new javax.swing.JTextField(tanggalLama);
            txtTanggalLama.setEditable(false);
            txtTanggalLama.setBackground(new java.awt.Color(245, 245, 245));
            panelUtama.add(txtTanggalLama, gbc);

            // Label tanggal baru
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.fill = java.awt.GridBagConstraints.NONE;
            panelUtama.add(new javax.swing.JLabel("Tanggal Baru:"), gbc);

            gbc.gridx = 1;
            gbc.gridy = 3;
            gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
            javax.swing.JTextField txtTanggalBaru = new javax.swing.JTextField();
            txtTanggalBaru.setToolTipText("Format: yyyy-MM-dd, dd-MM-yyyy, atau dd/MM/yyyy");
            panelUtama.add(txtTanggalBaru, gbc);

            // Label format
            gbc.gridx = 1;
            gbc.gridy = 4;
            gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
            javax.swing.JLabel lblFormat = new javax.swing.JLabel("<html><i>Format: yyyy-MM-dd, dd-MM-yyyy, dd/MM/yyyy</i></html>");
            lblFormat.setForeground(new java.awt.Color(102, 102, 102));
            lblFormat.setFont(new java.awt.Font("Tahoma", 0, 11));
            panelUtama.add(lblFormat, gbc);

            dialogTanggal.add(panelUtama, java.awt.BorderLayout.CENTER);

            // Panel tombol
            javax.swing.JPanel panelTombol = new javax.swing.JPanel();
            panelTombol.setLayout(new java.awt.FlowLayout());

            javax.swing.JButton btnSimpan = new javax.swing.JButton("Simpan");
            btnSimpan.setIcon(new ImageIcon(getClass().getResource("/picture/save-16x16.png")));
            btnSimpan.addActionListener(e -> {
                String tanggalBaru = txtTanggalBaru.getText().trim();
                if (tanggalBaru.isEmpty()) {
                    JOptionPane.showMessageDialog(dialogTanggal, "Tanggal baru tidak boleh kosong!");
                    return;
                }

                if (prosesUpdateTanggalManual(selectedRow, tanggalBaru, tanggalLama)) {
                    dialogTanggal.dispose();
                }
            });

            javax.swing.JButton btnBatal = new javax.swing.JButton("Batal");
            btnBatal.setIcon(new ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
            btnBatal.addActionListener(e -> dialogTanggal.dispose());

            // Tombol quick set (hari ini, besok, lusa)
            javax.swing.JButton btnHariIni = new javax.swing.JButton("Hari Ini");
            btnHariIni.addActionListener(e -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                txtTanggalBaru.setText(sdf.format(new Date()));
            });

            javax.swing.JButton btnBesok = new javax.swing.JButton("Besok");
            btnBesok.addActionListener(e -> {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, 1);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                txtTanggalBaru.setText(sdf.format(cal.getTime()));
            });

            panelTombol.add(btnHariIni);
            panelTombol.add(btnBesok);
            panelTombol.add(new javax.swing.JSeparator(javax.swing.SwingConstants.VERTICAL));
            panelTombol.add(btnSimpan);
            panelTombol.add(btnBatal);

            dialogTanggal.add(panelTombol, java.awt.BorderLayout.SOUTH);

            // Focus ke input tanggal baru
            txtTanggalBaru.requestFocus();

            // Show dialog
            dialogTanggal.setVisible(true);

        } catch (Exception e) {
            System.out.println("Error edit tanggal manual: " + e);
            JOptionPane.showMessageDialog(this, "Error saat edit tanggal: " + e.getMessage());
        }
    }

    private boolean prosesUpdateTanggalManual(int row, String tanggalBaru, String tanggalLama) {
        try {
            // Validasi format tanggal
            if (!isValidDateFormat(tanggalBaru)) {
                JOptionPane.showMessageDialog(this,
                        "❌ FORMAT TANGGAL TIDAK VALID!\n\n"
                        + "Format yang diterima:\n"
                        + "• yyyy-MM-dd (contoh: 2025-06-26)\n"
                        + "• dd-MM-yyyy (contoh: 26-06-2025)\n"
                        + "• dd/MM/yyyy (contoh: 26/06/2025)\n\n"
                        + "Tanggal yang dimasukkan: " + tanggalBaru,
                        "Error Format Tanggal",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Convert tanggal untuk database
            String tanggalBaruDB = convertTanggalForDB(tanggalBaru);
            String tanggalLamaDB = convertTanggalForDB(tanggalLama);

            // Cek apakah tanggal sama
            if (tanggalBaruDB.equals(tanggalLamaDB)) {
                JOptionPane.showMessageDialog(this,
                        "Tanggal sama dengan sebelumnya, tidak ada perubahan.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                return true;
            }

            // Cek duplikasi tanggal dalam terapi yang sama
            if (isTanggalExistsInTerapi(tanggalBaruDB, tanggalLamaDB)) {
                JOptionPane.showMessageDialog(this,
                        "❌ TANGGAL DUPLIKAT!\n\n"
                        + "Tanggal " + tanggalBaru + " sudah ada dalam jadwal terapi ini!\n"
                        + "Silahkan pilih tanggal yang berbeda.",
                        "Tanggal Duplikat",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // Konfirmasi perubahan
            String hariKe = tbPemantauan.getValueAt(row, 1).toString();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "=== KONFIRMASI PERUBAHAN TANGGAL ===\n\n"
                    + hariKe + " - " + getSelectedTerapiName() + "\n\n"
                    + "Dari: " + tanggalLama + "\n"
                    + "Ke: " + tanggalBaru + "\n\n"
                    + "⚠️ PERHATIAN:\n"
                    + "• Tanggal akan diubah secara manual\n"
                    + "• Pastikan tanggal sesuai dengan kebutuhan terapi\n"
                    + "• Perubahan akan tersimpan ke database\n\n"
                    + "Lanjutkan perubahan tanggal?",
                    "Konfirmasi Edit Tanggal Manual",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                // Update tanggal ke database
                if (updateTanggalPemberianKeDatabase(row, tanggalBaruDB, tanggalLamaDB)) {
                    // Update tampilan tabel
                    isUpdatingTable = true;
                    try {
                        tbPemantauan.setValueAt(tanggalBaruDB, row, 2);
                        tbPemantauan.setValueAt(akses.getkode(), row, 5);
                        tbPemantauan.setValueAt(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()), row, 6);
                    } finally {
                        isUpdatingTable = false;
                    }

                    // Refresh tabel terapi
                    tampilTerapiObat();

                    JOptionPane.showMessageDialog(this,
                            "✅ TANGGAL BERHASIL DIUBAH!\n\n"
                            + "Tanggal baru: " + tanggalBaru + "\n"
                            + "Diupdate oleh: " + akses.getkode() + "\n"
                            + "Waktu update: " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()),
                            "Update Berhasil",
                            JOptionPane.INFORMATION_MESSAGE);

                    return true;
                } else {
                    JOptionPane.showMessageDialog(this,
                            "❌ Gagal mengubah tanggal ke database!",
                            "Error Update",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            return false;

        } catch (Exception e) {
            System.out.println("Error proses update tanggal manual: " + e);
            JOptionPane.showMessageDialog(this,
                    "❌ Error mengubah tanggal: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Reset tanggal ke default berdasarkan hari ke-x
     */
    private void resetTanggalDefault() {
        int selectedRow = tbPemantauan.getSelectedRow();
        if (selectedRow < 0 || idTerapiAktif.equals("")) {
            JOptionPane.showMessageDialog(this, "Silahkan pilih jadwal terapi terlebih dahulu!");
            return;
        }

        try {
            String hariKeStr = tbPemantauan.getValueAt(selectedRow, 1).toString();
            String tanggalLama = tbPemantauan.getValueAt(selectedRow, 2).toString();

            // Extract nomor hari
            int hariKe = 1;
            try {
                hariKe = Integer.parseInt(hariKeStr.replaceAll("[^0-9]", ""));
            } catch (Exception e) {
                hariKe = selectedRow + 1; // Fallback
            }

            // Hitung tanggal default berdasarkan tanggal mulai terapi + hari ke-x
            String tanggalDefault = hitungTanggalDefault(hariKe);

            if (tanggalDefault.equals(tanggalLama)) {
                JOptionPane.showMessageDialog(this,
                        "Tanggal sudah sesuai dengan default (" + tanggalDefault + ")",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Konfirmasi reset
            int confirm = JOptionPane.showConfirmDialog(this,
                    "=== KONFIRMASI RESET TANGGAL ===\n\n"
                    + hariKeStr + " - " + getSelectedTerapiName() + "\n\n"
                    + "Tanggal saat ini: " + tanggalLama + "\n"
                    + "Tanggal default: " + tanggalDefault + "\n\n"
                    + "Reset ke tanggal default?",
                    "Konfirmasi Reset Tanggal",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                // Cek duplikasi tanggal
                if (isTanggalExistsInTerapi(tanggalDefault, convertTanggalForDB(tanggalLama))) {
                    JOptionPane.showMessageDialog(this,
                            "❌ Tidak bisa reset ke tanggal default!\n\n"
                            + "Tanggal " + tanggalDefault + " sudah ada dalam jadwal terapi.",
                            "Tanggal Duplikat",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Update tanggal ke database
                if (updateTanggalPemberianKeDatabase(selectedRow, tanggalDefault, convertTanggalForDB(tanggalLama))) {
                    // Update tampilan tabel
                    isUpdatingTable = true;
                    try {
                        tbPemantauan.setValueAt(tanggalDefault, selectedRow, 2);
                        tbPemantauan.setValueAt(akses.getkode(), selectedRow, 5);
                        tbPemantauan.setValueAt(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()), selectedRow, 6);
                    } finally {
                        isUpdatingTable = false;
                    }

                    // Refresh tabel terapi
                    tampilTerapiObat();

                    JOptionPane.showMessageDialog(this,
                            "✅ TANGGAL BERHASIL DIRESET!\n\n"
                            + "Tanggal baru: " + tanggalDefault + "\n"
                            + "Diupdate oleh: " + akses.getkode(),
                            "Reset Berhasil",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } catch (Exception e) {
            System.out.println("Error reset tanggal default: " + e);
            JOptionPane.showMessageDialog(this, "Error reset tanggal: " + e.getMessage());
        }
    }

    /**
     * Update tanggal pemberian ke database
     */
    private boolean updateTanggalPemberianKeDatabase(int row, String tanggalBaru, String tanggalLama) {
        try {
            String sql = "UPDATE pemantauan_terapi_obat_harian SET "
                    + "tgl_pemberian = ?, nip_petugas = ?, waktu_input = NOW() "
                    + "WHERE id_terapi = ? AND tgl_pemberian = ?";

            PreparedStatement ps = koneksi.prepareStatement(sql);
            ps.setString(1, tanggalBaru);
            ps.setString(2, akses.getkode());
            ps.setString(3, idTerapiAktif);
            ps.setString(4, tanggalLama);

            int result = ps.executeUpdate();
            ps.close();

            return result > 0;

        } catch (Exception e) {
            System.out.println("Error update tanggal ke database: " + e);
            return false;
        }
    }

    /**
     * Cek apakah tanggal sudah ada dalam terapi yang sama
     */
    private boolean isTanggalExistsInTerapi(String tanggalBaru, String tanggalLama) {
        try {
            // Jika tanggal sama dengan yang lama, tidak duplikat
            if (tanggalBaru.equals(tanggalLama)) {
                return false;
            }

            String sql = "SELECT COUNT(*) FROM pemantauan_terapi_obat_harian "
                    + "WHERE id_terapi = ? AND tgl_pemberian = ?";
            PreparedStatement ps = koneksi.prepareStatement(sql);
            ps.setString(1, idTerapiAktif);
            ps.setString(2, tanggalBaru);

            ResultSet rs = ps.executeQuery();
            boolean exists = false;
            if (rs.next()) {
                exists = (rs.getInt(1) > 0);
            }

            rs.close();
            ps.close();
            return exists;

        } catch (Exception e) {
            System.out.println("Error cek tanggal exists in terapi: " + e);
            return true; // Return true untuk mencegah duplikat jika ada error
        }
    }

    /**
     * Hitung tanggal default berdasarkan hari ke-x dari tanggal mulai terapi
     */
    private String hitungTanggalDefault(int hariKe) {
        try {
            // Ambil tanggal mulai terapi dari database
            String sql = "SELECT tgl_mulai_terapi FROM pemantauan_terapi_obat WHERE id = ?";
            PreparedStatement ps = koneksi.prepareStatement(sql);
            ps.setString(1, idTerapiAktif);
            ResultSet rs = ps.executeQuery();

            Date tglMulaiTerapi = new Date();
            if (rs.next()) {
                tglMulaiTerapi = rs.getDate("tgl_mulai_terapi");
            }
            rs.close();
            ps.close();

            // Hitung tanggal default: tanggal mulai + (hari ke - 1)
            Calendar cal = Calendar.getInstance();
            cal.setTime(tglMulaiTerapi);
            cal.add(Calendar.DAY_OF_MONTH, hariKe - 1);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(cal.getTime());

        } catch (Exception e) {
            System.out.println("Error hitung tanggal default: " + e);
            // Return tanggal hari ini jika error
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(new Date());
        }
    }

    /**
     * Get nama terapi yang sedang dipilih
     */
    private String getSelectedTerapiName() {
        try {
            int selectedRow = tbTerapiObat.getSelectedRow();
            if (selectedRow >= 0) {
                return tbTerapiObat.getValueAt(selectedRow, 1).toString();
            }
            return "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /**
     * Initialize context menu untuk edit tanggal
     */
    private void initContextMenuTanggal() {
        popupMenuTanggal = new JPopupMenu();

        // Menu Edit Tanggal Manual
        menuEditTanggal = new JMenuItem("Edit Tanggal Manual");
        menuEditTanggal.setIcon(new ImageIcon(getClass().getResource("/picture/inventaris.png")));
        menuEditTanggal.addActionListener(e -> editTanggalManual());

        // Menu Reset ke Tanggal Default
        menuResetTanggal = new JMenuItem("Reset ke Tanggal Default");
        menuResetTanggal.setIcon(new ImageIcon(getClass().getResource("/picture/refresh.png")));
        menuResetTanggal.addActionListener(e -> resetTanggalDefault());

        // Separator
        separatorTanggal = new JSeparator();

        // Add items to popup menu
        popupMenuTanggal.add(menuEditTanggal);
        popupMenuTanggal.add(separatorTanggal);
        popupMenuTanggal.add(menuResetTanggal);

        // Add popup menu listener untuk update state
        popupMenuTanggal.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                updateContextMenuTanggalState();
            }

            @Override
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
            }
        });
    }

    /**
     * Validasi format tanggal yang diinput user
     */
    private boolean isValidDateFormat(String tanggal) {
        if (tanggal == null || tanggal.trim().isEmpty()) {
            return false;
        }

        tanggal = tanggal.trim();

        try {
            // Format yyyy-MM-dd
            if (tanggal.matches("\\d{4}-\\d{2}-\\d{2}")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                Date parsed = sdf.parse(tanggal);

                // Additional validation: check if date is reasonable (not too far in past/future)
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, -10); // 10 years ago
                Date minDate = cal.getTime();

                cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, 1); // 1 year from now
                Date maxDate = cal.getTime();

                return parsed.after(minDate) && parsed.before(maxDate);
            } // Format dd-MM-yyyy
            else if (tanggal.matches("\\d{2}-\\d{2}-\\d{4}")) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                sdf.setLenient(false);
                Date parsed = sdf.parse(tanggal);

                // Same date range validation
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, -10);
                Date minDate = cal.getTime();

                cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, 1);
                Date maxDate = cal.getTime();

                return parsed.after(minDate) && parsed.before(maxDate);
            } // Format dd/MM/yyyy
            else if (tanggal.matches("\\d{2}/\\d{2}/\\d{4}")) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                Date parsed = sdf.parse(tanggal);

                // Same date range validation
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, -10);
                Date minDate = cal.getTime();

                cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, 1);
                Date maxDate = cal.getTime();

                return parsed.after(minDate) && parsed.before(maxDate);
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Update state context menu tanggal
     */
    private void updateContextMenuTanggalState() {
        int selectedRow = tbPemantauan.getSelectedRow();
        int selectedCol = tbPemantauan.getSelectedColumn();

        boolean hasSelection = (selectedRow >= 0);
        boolean isTanggalColumn = (selectedCol == 2); // Kolom tanggal
        boolean hasActiveTerapi = !idTerapiAktif.equals("");

        // Enable menu jika ada terapi aktif dan klik di kolom tanggal
        menuEditTanggal.setEnabled(hasSelection && isTanggalColumn && hasActiveTerapi);
        menuResetTanggal.setEnabled(hasSelection && isTanggalColumn && hasActiveTerapi);

        // Update tooltip
        if (!hasActiveTerapi) {
            menuEditTanggal.setToolTipText("Pilih terapi obat terlebih dahulu");
            menuResetTanggal.setToolTipText("Pilih terapi obat terlebih dahulu");
        } else if (!isTanggalColumn) {
            menuEditTanggal.setToolTipText("Klik pada kolom tanggal untuk edit");
            menuResetTanggal.setToolTipText("Klik pada kolom tanggal untuk reset");
        } else {
            menuEditTanggal.setToolTipText("Edit tanggal pemberian manual");
            menuResetTanggal.setToolTipText("Reset ke tanggal default berdasarkan hari ke-x");
        }
    }

    // 2. TAMBAHKAN METHOD TCari1KeyPressed (method baru)
    private void TCari1KeyPressed(java.awt.event.KeyEvent evt) {
        // Jika tombol Enter ditekan, lakukan pencarian
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCari1ActionPerformed(null);
            evt.consume(); // Mencegah event bubbling
        } // Jika tombol Escape ditekan, clear pencarian
        else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            TCari1.setText("");
            tampilObat(); // Tampilkan semua obat
            evt.consume();
        } // Jika tombol F5 ditekan, refresh data
        else if (evt.getKeyCode() == KeyEvent.VK_F5) {
            tampilObat();
            evt.consume();
        }
    }

    /**
     * SOLUSI: Auto-save keterangan saat edit langsung di tabel Tambahkan
     * method-method ini ke class DlgPemantauanTerapiObat
     */
    /**
     * Method untuk setup auto-save keterangan di tabel Panggil method ini di
     * initAfterComponents()
     */
    private void setupAutoSaveKeterangan() {
        // Add table model listener untuk detect perubahan
        tabModeTerapi.addTableModelListener(new javax.swing.event.TableModelListener() {
            @Override
            public void tableChanged(javax.swing.event.TableModelEvent e) {
                // Cek apakah perubahan terjadi di kolom keterangan (kolom 4)
                if (e.getColumn() == 4 && e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    if (row >= 0 && !isUpdatingTable) {
                        // Auto-save keterangan setelah user selesai edit
                        javax.swing.SwingUtilities.invokeLater(() -> {
                            autoSaveKeterangan(row);
                        });
                    }
                }
            }
        });

        // Add cell editor listener untuk detect saat user selesai edit
        if (tbPemantauan.getColumnModel().getColumnCount() > 4) {
            TableColumn keteranganColumn = tbPemantauan.getColumnModel().getColumn(4);

            // Custom cell editor dengan auto-save
            javax.swing.DefaultCellEditor editor = new javax.swing.DefaultCellEditor(new javax.swing.JTextField()) {
                @Override
                public boolean stopCellEditing() {
                    // Simpan perubahan saat user selesai edit (Enter, Tab, atau klik lain)
                    boolean result = super.stopCellEditing();
                    if (result) {
                        // Delay sedikit untuk memastikan model sudah terupdate
                        javax.swing.Timer timer = new javax.swing.Timer(100, e -> {
                            int editingRow = tbPemantauan.getEditingRow();
                            if (editingRow == -1) {
                                // Ambil row yang baru saja diedit
                                int selectedRow = tbPemantauan.getSelectedRow();
                                if (selectedRow >= 0) {
                                    autoSaveKeterangan(selectedRow);
                                }
                            }
                        });
                        timer.setRepeats(false);
                        timer.start();
                    }
                    return result;
                }
            };

            keteranganColumn.setCellEditor(editor);
        }
    }

    /**
     * Method untuk auto-save keterangan ke database
     */
    // Override method auto-save keterangan
    private void autoSaveKeterangan(int row) {
        if (idTerapiAktif.equals("") || isUpdatingTable) {
            return;
        }

        try {
            // Ambil data dari tabel
            String keteranganBaru = "";
            String tanggal = "";

            Object keteranganObj = tbPemantauan.getValueAt(row, 4);
            Object tanggalObj = tbPemantauan.getValueAt(row, 2);

            if (keteranganObj != null) {
                keteranganBaru = keteranganObj.toString().trim();
            }

            if (tanggalObj != null) {
                tanggal = tanggalObj.toString();
            } else {
                System.out.println("Tanggal kosong untuk row " + row);
                return;
            }

            // Validasi keterangan
            if (keteranganBaru.length() > 200) {
                JOptionPane.showMessageDialog(this,
                        "Keterangan terlalu panjang (maksimal 200 karakter)!\n"
                        + "Keterangan akan dipotong otomatis.");
                keteranganBaru = keteranganBaru.substring(0, 200);

                // Update tabel dengan keterangan yang dipotong
                isUpdatingTable = true;
                tbPemantauan.setValueAt(keteranganBaru, row, 4);
                isUpdatingTable = false;
            }

            // Simpan ke database menggunakan sekuel
            if (updateKeteranganKeDatabase(idTerapiAktif, convertTanggalForDB(tanggal), keteranganBaru)) {
                // Update info petugas dan waktu
                isUpdatingTable = true;
                try {
                    tbPemantauan.setValueAt(akses.getkode(), row, 5);
                    tbPemantauan.setValueAt(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()), row, 6);

                    // Visual feedback - ubah background row sebentar
                    showSaveFeedback(row);

                } finally {
                    isUpdatingTable = false;
                }

                System.out.println("✅ Keterangan tersimpan: " + keteranganBaru);
            } else {
                // Jika gagal simpan, kembalikan ke nilai sebelumnya
                JOptionPane.showMessageDialog(this,
                        "❌ Gagal menyimpan keterangan!\n"
                        + "Silahkan coba lagi atau hubungi administrator.");
            }

        } catch (Exception e) {
            System.out.println("Error auto-save keterangan: " + e);
            JOptionPane.showMessageDialog(this,
                    "Error menyimpan keterangan: " + e.getMessage());
        }
    }

    /**
     * Method untuk update keterangan ke database
     */
    private boolean updateKeteranganKeDatabase(String idTerapi, String tanggal, String keterangan) {
        try {
            String[] dataUpdate = new String[4];
            dataUpdate[0] = keterangan;                    // keterangan
            dataUpdate[1] = akses.getkode();               // nip_petugas
            dataUpdate[2] = idTerapi;                      // id_terapi (WHERE)
            dataUpdate[3] = tanggal;                       // tgl_pemberian (WHERE)

            String updateClause = "keterangan = ?, nip_petugas = ?, waktu_input = NOW()";
            String whereClause = "id_terapi = ? AND tgl_pemberian = ?";

            return Sequel.mengedittf2("pemantauan_terapi_obat_harian", whereClause, updateClause, 4, dataUpdate);

        } catch (Exception e) {
            System.out.println("Error update keterangan: " + e);
            return false;
        }
    }

    /**
     * Method untuk memberikan visual feedback saat save
     */
    private void showSaveFeedback(int row) {
        try {
            // Simpan warna asli
            Color originalColor = tbPemantauan.getBackground();

            // Ubah background row menjadi hijau muda sebentar
            tbPemantauan.setRowSelectionInterval(row, row);
            tbPemantauan.setSelectionBackground(new Color(144, 238, 144)); // Light green

            // Kembalikan ke warna asli setelah 1 detik
            javax.swing.Timer timer = new javax.swing.Timer(1000, e -> {
                tbPemantauan.setSelectionBackground(originalColor);
                tbPemantauan.clearSelection();
            });
            timer.setRepeats(false);
            timer.start();

        } catch (Exception e) {
            System.out.println("Error show save feedback: " + e);
        }
    }

    /**
     * Method untuk setup tooltip dan hint di kolom keterangan
     */
    private void setupKeteranganTooltip() {
        try {
            // Setup tooltip untuk header kolom keterangan
            if (tbPemantauan.getTableHeader() != null) {
                tbPemantauan.getTableHeader().setToolTipText("Double-click pada kolom Keterangan untuk edit langsung");
            }

            // Custom renderer untuk kolom keterangan
            if (tbPemantauan.getColumnModel().getColumnCount() > 4) {
                TableColumn keteranganColumn = tbPemantauan.getColumnModel().getColumn(4);

                keteranganColumn.setCellRenderer(new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value,
                            boolean isSelected, boolean hasFocus, int row, int column) {

                        Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                        // Tooltip dengan hint
                        String tooltipText = "<html><b>Keterangan Pemberian Obat</b><br/>";
                        tooltipText += "• Double-click untuk edit<br/>";
                        tooltipText += "• Enter untuk simpan<br/>";
                        tooltipText += "• Maksimal 200 karakter<br/>";
                        if (value != null && !value.toString().trim().isEmpty()) {
                            tooltipText += "• Isi: " + value.toString() + "</html>";
                        } else {
                            tooltipText += "• Kosong (opsional)</html>";
                        }

                        ((JComponent) comp).setToolTipText(tooltipText);

                        // Ubah background jika kosong (hint visual)
                        if (value == null || value.toString().trim().isEmpty()) {
                            if (!isSelected) {
                                comp.setBackground(new Color(255, 255, 240)); // Light yellow hint
                            }
                        }

                        return comp;
                    }
                });
            }

        } catch (Exception e) {
            System.out.println("Error setup keterangan tooltip: " + e);
        }
    }

    /**
     * Method untuk memberikan konfirmasi visual saat keterangan tersimpan
     */
    private void showKeteranganSavedNotification(String keterangan) {
        // Buat notification kecil di pojok kanan bawah
        javax.swing.JWindow notification = new javax.swing.JWindow();
        notification.setSize(300, 80);
        notification.setAlwaysOnTop(true);

        // Posisi di pojok kanan bawah
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        notification.setLocation(
                screenSize.width - notification.getWidth() - 20,
                screenSize.height - notification.getHeight() - 100
        );

        // Panel konten
        javax.swing.JPanel panel = new javax.swing.JPanel();
        panel.setLayout(new java.awt.BorderLayout());
        panel.setBackground(new Color(144, 238, 144)); // Light green
        panel.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0, 128, 0), 2));

        // Icon dan text
        javax.swing.JLabel iconLabel = new javax.swing.JLabel("✅");
        iconLabel.setFont(new Font("Arial", Font.BOLD, 20));
        iconLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 5, 5));

        String displayText = "Keterangan tersimpan!";
        if (keterangan.length() > 30) {
            displayText += "\n" + keterangan.substring(0, 30) + "...";
        } else if (!keterangan.isEmpty()) {
            displayText += "\n" + keterangan;
        }

        javax.swing.JLabel textLabel = new javax.swing.JLabel("<html>" + displayText.replace("\n", "<br/>") + "</html>");
        textLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        textLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 10));

        panel.add(iconLabel, java.awt.BorderLayout.WEST);
        panel.add(textLabel, java.awt.BorderLayout.CENTER);

        notification.add(panel);
        notification.setVisible(true);

        // Auto-close setelah 3 detik
        javax.swing.Timer timer = new javax.swing.Timer(3000, e -> {
            notification.dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * FITUR HAPUS OBAT TERPILIH DI JADWAL OBAT Tambahkan method-method ini ke
     * class DlgPemantauanTerapiObat
     */
    /**
     * Method untuk menghapus obat yang terpilih dari jadwal
     */
    private void hapusObatTerpilih() {
        int selectedRow = tbTerapiObat.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "❌ BELUM ADA OBAT YANG DIPILIH!\n\n"
                    + "Langkah yang benar:\n"
                    + "1. Klik pada baris obat yang ingin dihapus\n"
                    + "2. Klik tombol 'Hapus Obat' atau tekan Delete\n"
                    + "3. Konfirmasi penghapusan",
                    "Pilih Obat Terlebih Dahulu",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ambil data obat yang akan dihapus
        String idTerapi = tbTerapiObat.getValueAt(selectedRow, 0).toString();
        String namaObat = tbTerapiObat.getValueAt(selectedRow, 1).toString();
        String dosis = tbTerapiObat.getValueAt(selectedRow, 2).toString();
        String jenisObat = tbTerapiObat.getValueAt(selectedRow, 8).toString();
        String status = tbTerapiObat.getValueAt(selectedRow, 7).toString();
        String progress = tbTerapiObat.getValueAt(selectedRow, 6).toString();

        // Hitung total jadwal yang akan terhapus
        int totalJadwal = hitungTotalJadwalObat(idTerapi);
        int jadwalDiberikan = hitungJadwalDiberikan(idTerapi);

        // Buat pesan konfirmasi yang detail
        String pesanKonfirmasi = "🗑️ KONFIRMASI HAPUS OBAT TERAPI\n\n";
        pesanKonfirmasi += "═══════════════════════════════════\n";
        pesanKonfirmasi += "📋 DETAIL OBAT:\n";
        pesanKonfirmasi += "• Nama Obat: " + namaObat + "\n";
        pesanKonfirmasi += "• Jenis: " + jenisObat + "\n";
        pesanKonfirmasi += "• Dosis: " + dosis + "\n";
        pesanKonfirmasi += "• Status: " + status + "\n";
        pesanKonfirmasi += "• Progress: " + progress + "\n\n";

        pesanKonfirmasi += "📊 STATISTIK JADWAL:\n";
        pesanKonfirmasi += "• Total Jadwal: " + totalJadwal + " hari\n";
        pesanKonfirmasi += "• Sudah Diberikan: " + jadwalDiberikan + " hari\n";
        pesanKonfirmasi += "• Belum Diberikan: " + (totalJadwal - jadwalDiberikan) + " hari\n\n";

        pesanKonfirmasi += "⚠️ PERINGATAN:\n";
        pesanKonfirmasi += "• Semua data terapi akan TERHAPUS PERMANEN\n";
        pesanKonfirmasi += "• Semua jadwal harian akan IKUT TERHAPUS\n";
        pesanKonfirmasi += "• Riwayat pemberian obat akan HILANG\n";
        pesanKonfirmasi += "• Tindakan ini TIDAK DAPAT DIBATALKAN\n\n";

        pesanKonfirmasi += "═══════════════════════════════════\n";
        pesanKonfirmasi += "Yakin ingin menghapus obat terapi ini?";

        // Tampilkan dialog konfirmasi dengan pilihan yang jelas
        String[] options = {"🗑️ Ya, Hapus Obat", "❌ Batal"};
        int confirm = JOptionPane.showOptionDialog(this,
                pesanKonfirmasi,
                "Konfirmasi Hapus Obat Terapi",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[1] // Default ke "Batal"
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Konfirmasi kedua untuk obat yang sudah ada pemberian
            if (jadwalDiberikan > 0) {
                String pesanKonfirmasiKedua = "⚠️ KONFIRMASI TERAKHIR\n\n"
                        + "Obat ini sudah diberikan " + jadwalDiberikan + " kali.\n"
                        + "Menghapus obat akan menghilangkan riwayat pemberian.\n\n"
                        + "Apakah Anda YAKIN ingin melanjutkan?";

                String[] finalOptions = {"🗑️ Ya, Tetap Hapus", "❌ Batal"};
                int finalConfirm = JOptionPane.showOptionDialog(this,
                        pesanKonfirmasiKedua,
                        "Konfirmasi Terakhir",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE,
                        null,
                        finalOptions,
                        finalOptions[1]
                );

                if (finalConfirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // Proses penghapusan
            if (prosesHapusObatTerapi(idTerapi, namaObat, totalJadwal)) {
                // Refresh tampilan
                tampilTerapiObat();

                // Clear form jika obat yang dihapus sedang dipilih
                if (idTerapiAktif.equals(idTerapi)) {
                    emptTeks();
                }

                // Tampilkan pesan sukses
                String pesanSukses = "✅ OBAT BERHASIL DIHAPUS!\n\n";
                pesanSukses += "Obat: " + namaObat + "\n";
                pesanSukses += "Total jadwal terhapus: " + totalJadwal + " hari\n";
                pesanSukses += "Dihapus oleh: " + akses.getkode() + "\n";
                pesanSukses += "Waktu: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

                JOptionPane.showMessageDialog(this,
                        pesanSukses,
                        "Penghapusan Berhasil",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * Method untuk menghitung total jadwal obat
     */
    private int hitungTotalJadwalObat(String idTerapi) {
        try {
            String sql = "SELECT COUNT(*) FROM pemantauan_terapi_obat_harian WHERE id_terapi = ?";
            PreparedStatement ps = koneksi.prepareStatement(sql);
            ps.setString(1, idTerapi);
            ResultSet rs = ps.executeQuery();

            int total = 0;
            if (rs.next()) {
                total = rs.getInt(1);
            }

            rs.close();
            ps.close();
            return total;

        } catch (Exception e) {
            System.out.println("Error hitung total jadwal: " + e);
            return 0;
        }
    }

    /**
     * Method untuk menghitung jadwal yang sudah diberikan
     */
    private int hitungJadwalDiberikan(String idTerapi) {
        try {
            String sql = "SELECT COUNT(*) FROM pemantauan_terapi_obat_harian "
                    + "WHERE id_terapi = ? AND status_pemberian = 'ya'";
            PreparedStatement ps = koneksi.prepareStatement(sql);
            ps.setString(1, idTerapi);
            ResultSet rs = ps.executeQuery();

            int diberikan = 0;
            if (rs.next()) {
                diberikan = rs.getInt(1);
            }

            rs.close();
            ps.close();
            return diberikan;

        } catch (Exception e) {
            System.out.println("Error hitung jadwal diberikan: " + e);
            return 0;
        }
    }

    /**
     * Method untuk proses penghapusan obat terapi
     */
    private boolean prosesHapusObatTerapi(String idTerapi, String namaObat, int totalJadwal) {
        try {
            // Mulai transaction
            koneksi.setAutoCommit(false);

            try {
                // 1. Hapus jadwal harian terlebih dahulu
                String sqlHapusHarian = "DELETE FROM pemantauan_terapi_obat_harian WHERE id_terapi = ?";
                PreparedStatement psHarian = koneksi.prepareStatement(sqlHapusHarian);
                psHarian.setString(1, idTerapi);
                int resultHarian = psHarian.executeUpdate();
                psHarian.close();

                System.out.println("Jadwal harian terhapus: " + resultHarian + " record");

                // 2. Hapus data terapi utama
                String sqlHapusTerapi = "DELETE FROM pemantauan_terapi_obat WHERE id = ?";
                PreparedStatement psTerapi = koneksi.prepareStatement(sqlHapusTerapi);
                psTerapi.setString(1, idTerapi);
                int resultTerapi = psTerapi.executeUpdate();
                psTerapi.close();

                System.out.println("Data terapi terhapus: " + resultTerapi + " record");

                // 3. Log penghapusan (optional - buat audit trail)
                logPenghapusanObat(idTerapi, namaObat, totalJadwal);

                // Commit transaction
                koneksi.commit();
                koneksi.setAutoCommit(true);

                return resultTerapi > 0;

            } catch (Exception e) {
                // Rollback jika ada error
                koneksi.rollback();
                koneksi.setAutoCommit(true);
                throw e;
            }

        } catch (Exception e) {
            System.out.println("Error proses hapus obat terapi: " + e);
            JOptionPane.showMessageDialog(this,
                    "❌ GAGAL MENGHAPUS OBAT!\n\n"
                    + "Error: " + e.getMessage() + "\n\n"
                    + "Silahkan coba lagi atau hubungi administrator.",
                    "Error Penghapusan",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Method untuk log penghapusan (audit trail)
     */
    private void logPenghapusanObat(String idTerapi, String namaObat, int totalJadwal) {
        try {
            // Buat log di tabel audit (jika ada) atau log ke file
            String logMessage = String.format(
                    "HAPUS_OBAT_TERAPI: ID=%s, Obat=%s, TotalJadwal=%d, User=%s, Waktu=%s",
                    idTerapi, namaObat, totalJadwal, akses.getkode(),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
            );

            System.out.println("AUDIT LOG: " + logMessage);

            // Jika ada tabel audit, bisa ditambahkan di sini:
            /*
        String sqlAudit = "INSERT INTO audit_log (action, table_name, record_id, details, user_id, timestamp) " +
                         "VALUES ('DELETE', 'pemantauan_terapi_obat', ?, ?, ?, NOW())";
        PreparedStatement psAudit = koneksi.prepareStatement(sqlAudit);
        psAudit.setString(1, idTerapi);
        psAudit.setString(2, "Hapus obat: " + namaObat + " (" + totalJadwal + " jadwal)");
        psAudit.setString(3, akses.getkode());
        psAudit.executeUpdate();
        psAudit.close();
             */
        } catch (Exception e) {
            System.out.println("Error log penghapusan: " + e);
        }
    }

    /**
     * Method untuk hapus multiple obat sekaligus (bulk delete)
     */
    private void hapusMultipleObat() {
        // Cek apakah ada obat yang terpilih
        if (tbTerapiObat.getSelectedRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "❌ BELUM ADA OBAT YANG DIPILIH!\n\n"
                    + "Untuk menghapus multiple obat:\n"
                    + "1. Tahan Ctrl dan klik pada baris obat yang ingin dihapus\n"
                    + "2. Klik tombol 'Hapus Multiple' atau tekan Shift+Delete\n"
                    + "3. Konfirmasi penghapusan",
                    "Pilih Obat Terlebih Dahulu",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int[] selectedRows = tbTerapiObat.getSelectedRows();

        // Kumpulkan info obat yang akan dihapus
        StringBuilder infoObat = new StringBuilder();
        int totalJadwalKeseluruhan = 0;
        int totalObatDiberikan = 0;

        for (int i = 0; i < selectedRows.length; i++) {
            int row = selectedRows[i];
            String idTerapi = tbTerapiObat.getValueAt(row, 0).toString();
            String namaObat = tbTerapiObat.getValueAt(row, 1).toString();
            String status = tbTerapiObat.getValueAt(row, 7).toString();

            int jadwalObat = hitungTotalJadwalObat(idTerapi);
            int diberikan = hitungJadwalDiberikan(idTerapi);

            totalJadwalKeseluruhan += jadwalObat;
            totalObatDiberikan += diberikan;

            infoObat.append((i + 1)).append(". ").append(namaObat)
                    .append(" (").append(status).append(") - ")
                    .append(jadwalObat).append(" jadwal\n");
        }

        // Pesan konfirmasi untuk multiple delete
        String pesanKonfirmasi = "🗑️ KONFIRMASI HAPUS MULTIPLE OBAT\n\n";
        pesanKonfirmasi += "═══════════════════════════════════\n";
        pesanKonfirmasi += "📋 OBAT YANG AKAN DIHAPUS (" + selectedRows.length + " obat):\n";
        pesanKonfirmasi += infoObat.toString() + "\n";
        pesanKonfirmasi += "📊 TOTAL STATISTIK:\n";
        pesanKonfirmasi += "• Total Jadwal: " + totalJadwalKeseluruhan + " hari\n";
        pesanKonfirmasi += "• Sudah Diberikan: " + totalObatDiberikan + " hari\n";
        pesanKonfirmasi += "• Belum Diberikan: " + (totalJadwalKeseluruhan - totalObatDiberikan) + " hari\n\n";
        pesanKonfirmasi += "⚠️ PERINGATAN:\n";
        pesanKonfirmasi += "• SEMUA " + selectedRows.length + " obat akan TERHAPUS PERMANEN\n";
        pesanKonfirmasi += "• SEMUA jadwal dan riwayat akan HILANG\n";
        pesanKonfirmasi += "• Tindakan ini TIDAK DAPAT DIBATALKAN\n\n";
        pesanKonfirmasi += "═══════════════════════════════════\n";
        pesanKonfirmasi += "Yakin ingin menghapus " + selectedRows.length + " obat terapi?";

        String[] options = {"🗑️ Ya, Hapus Semua", "❌ Batal"};
        int confirm = JOptionPane.showOptionDialog(this,
                pesanKonfirmasi,
                "Konfirmasi Hapus Multiple Obat",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[1]
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Proses penghapusan multiple
            prosesHapusMultipleObat(selectedRows);
        }
    }

    /**
     * Method untuk proses hapus multiple obat
     */
    private void prosesHapusMultipleObat(int[] selectedRows) {
        try {
            // Progress dialog
            javax.swing.JDialog progressDialog = new javax.swing.JDialog(this, "Menghapus Obat...", true);
            javax.swing.JProgressBar progressBar = new javax.swing.JProgressBar(0, selectedRows.length);
            javax.swing.JLabel statusLabel = new javax.swing.JLabel("Memulai penghapusan...");

            progressDialog.setLayout(new java.awt.BorderLayout());
            progressDialog.add(statusLabel, java.awt.BorderLayout.NORTH);
            progressDialog.add(progressBar, java.awt.BorderLayout.CENTER);
            progressDialog.setSize(400, 100);
            progressDialog.setLocationRelativeTo(this);
            progressDialog.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);

            // Worker thread untuk penghapusan
            javax.swing.SwingWorker<Boolean, String> worker = new javax.swing.SwingWorker<Boolean, String>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    int berhasil = 0;
                    int gagal = 0;

                    for (int i = 0; i < selectedRows.length; i++) {
                        int row = selectedRows[i];
                        String idTerapi = tbTerapiObat.getValueAt(row, 0).toString();
                        String namaObat = tbTerapiObat.getValueAt(row, 1).toString();
                        int totalJadwal = hitungTotalJadwalObat(idTerapi);

                        publish("Menghapus: " + namaObat + "...");

                        if (prosesHapusObatTerapi(idTerapi, namaObat, totalJadwal)) {
                            berhasil++;
                        } else {
                            gagal++;
                        }

                        progressBar.setValue(i + 1);
                        Thread.sleep(500); // Delay untuk UX
                    }

                    publish("Selesai. Berhasil: " + berhasil + ", Gagal: " + gagal);
                    return berhasil > 0;
                }

                @Override
                protected void process(java.util.List<String> chunks) {
                    for (String message : chunks) {
                        statusLabel.setText(message);
                    }
                }

                @Override
                protected void done() {
                    progressDialog.dispose();

                    try {
                        boolean success = get();

                        // Refresh tampilan
                        tampilTerapiObat();
                        emptTeks();

                        if (success) {
                            JOptionPane.showMessageDialog(DlgPemantauanTerapiObat.this,
                                    "✅ PENGHAPUSAN MULTIPLE OBAT SELESAI!\n\n"
                                    + "Silahkan cek hasil di tabel data terapi obat.",
                                    "Penghapusan Berhasil",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }

                    } catch (Exception e) {
                        System.out.println("Error dalam worker thread: " + e);
                        JOptionPane.showMessageDialog(DlgPemantauanTerapiObat.this,
                                "❌ Error dalam proses penghapusan: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            worker.execute();
            progressDialog.setVisible(true);

        } catch (Exception e) {
            System.out.println("Error hapus multiple obat: " + e);
            JOptionPane.showMessageDialog(this,
                    "❌ Error hapus multiple obat: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Method untuk keyboard shortcuts penghapusan
     */
    private void setupHapusObatKeyboardShortcuts() {
        // Delete key untuk hapus single obat
        tbTerapiObat.getInputMap(javax.swing.JComponent.WHEN_FOCUSED).put(
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0),
                "hapusObatTerpilih"
        );
        tbTerapiObat.getActionMap().put("hapusObatTerpilih", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                hapusObatTerpilih();
            }
        });

        // Shift+Delete untuk hapus multiple
        tbTerapiObat.getInputMap(javax.swing.JComponent.WHEN_FOCUSED).put(
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, java.awt.event.InputEvent.SHIFT_DOWN_MASK),
                "hapusMultipleObat"
        );
        tbTerapiObat.getActionMap().put("hapusMultipleObat", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                hapusMultipleObat();
            }
        });
    }

    private void tambahTombolHapusObat() {
        // Buat tombol hapus obat
        widget.Button BtnHapusObat = new widget.Button();
        BtnHapusObat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/delete.png")));
        BtnHapusObat.setMnemonic('D');
        BtnHapusObat.setText("Hapus Obat");
        BtnHapusObat.setName("BtnHapusObat");
        BtnHapusObat.setPreferredSize(new java.awt.Dimension(110, 30));
        BtnHapusObat.setToolTipText("Hapus obat yang dipilih (Delete)");
        BtnHapusObat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hapusObatTerpilih();
            }
        });

        // Buat tombol hapus multiple
        widget.Button BtnHapusMultiple = new widget.Button();
        BtnHapusMultiple.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/delete.png")));
        BtnHapusMultiple.setMnemonic('M');
        BtnHapusMultiple.setText("Hapus Multiple");
        BtnHapusMultiple.setName("BtnHapusMultiple");
        BtnHapusMultiple.setPreferredSize(new java.awt.Dimension(130, 30));
        BtnHapusMultiple.setToolTipText("Hapus multiple obat yang dipilih (Shift+Delete)");
        BtnHapusMultiple.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hapusMultipleObat();
            }
        });

        // Tambahkan ke panel tombol (panelGlass8)
        panelGlass8.add(BtnHapusObat);
        panelGlass8.add(BtnHapusMultiple);
    }

    /**
     * UPDATE CONTEXT MENU - Tambahkan opsi hapus obat
     */
    private void updateContextMenuTerapiObat() {
        // Tambahkan menu hapus obat ke context menu yang sudah ada
        if (popupMenuTerapi != null) {
            // Tambahkan separator
            popupMenuTerapi.add(new JSeparator());

            // Menu hapus obat terpilih
            JMenuItem menuHapusObatTerpilih = new JMenuItem("Hapus Obat Terpilih");
            menuHapusObatTerpilih.setIcon(new ImageIcon(getClass().getResource("/picture/delete.png")));
            menuHapusObatTerpilih.addActionListener(e -> hapusObatTerpilih());
            popupMenuTerapi.add(menuHapusObatTerpilih);

            // Menu hapus multiple (jika ada multiple selection)
            JMenuItem menuHapusMultiple = new JMenuItem("Hapus Multiple Obat");
            menuHapusMultiple.setIcon(new ImageIcon(getClass().getResource("/picture/delete.png")));
            menuHapusMultiple.addActionListener(e -> hapusMultipleObat());
            popupMenuTerapi.add(menuHapusMultiple);
        }
    }

    /**
     * Method untuk menghapus jadwal yang terpilih
     */
    private void hapusJadwalTerpilih() {
        int selectedRow = tbPemantauan.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "❌ BELUM ADA JADWAL YANG DIPILIH!\n\n"
                    + "Langkah yang benar:\n"
                    + "1. Klik pada baris jadwal yang ingin dihapus\n"
                    + "2. Klik kanan → Pilih 'Hapus Jadwal Terpilih'\n"
                    + "3. Konfirmasi penghapusan",
                    "Pilih Jadwal Terlebih Dahulu",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (idTerapiAktif.equals("")) {
            JOptionPane.showMessageDialog(this,
                    "❌ BELUM ADA TERAPI OBAT YANG DIPILIH!\n\n"
                    + "Silahkan pilih terapi obat terlebih dahulu di tabel atas.",
                    "Pilih Terapi Obat Dulu",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Ambil data jadwal yang akan dihapus
        String hariKe = tbPemantauan.getValueAt(selectedRow, 1).toString();
        String tanggal = tbPemantauan.getValueAt(selectedRow, 2).toString();
        Object diberikanObj = tbPemantauan.getValueAt(selectedRow, 3);
        boolean diberikan = (diberikanObj != null && (Boolean) diberikanObj);
        Object keteranganObj = tbPemantauan.getValueAt(selectedRow, 4);
        String keterangan = (keteranganObj != null) ? keteranganObj.toString() : "";
        Object petugasObj = tbPemantauan.getValueAt(selectedRow, 5);
        String petugas = (petugasObj != null) ? petugasObj.toString() : "";
        Object waktuObj = tbPemantauan.getValueAt(selectedRow, 6);
        String waktuInput = (waktuObj != null) ? waktuObj.toString() : "";

        // Ambil nama obat dari terapi aktif
        String namaObat = getSelectedTerapiName();

        // Buat pesan konfirmasi yang detail
        String pesanKonfirmasi = "🗑️ KONFIRMASI HAPUS JADWAL HARIAN\n\n";
        pesanKonfirmasi += "═══════════════════════════════════\n";
        pesanKonfirmasi += "📋 DETAIL JADWAL:\n";
        pesanKonfirmasi += "• Obat: " + namaObat + "\n";
        pesanKonfirmasi += "• " + hariKe + " (" + tanggal + ")\n";
        pesanKonfirmasi += "• Status: " + (diberikan ? "✅ SUDAH DIBERIKAN" : "❌ BELUM DIBERIKAN") + "\n";

        if (!keterangan.isEmpty()) {
            pesanKonfirmasi += "• Keterangan: " + keterangan + "\n";
        }

        if (!petugas.isEmpty() && !petugas.equals("-")) {
            pesanKonfirmasi += "• Petugas: " + petugas + "\n";
        }

        if (!waktuInput.isEmpty()) {
            pesanKonfirmasi += "• Waktu Input: " + waktuInput + "\n";
        }

        pesanKonfirmasi += "\n📊 STATISTIK TERAPI:\n";
        pesanKonfirmasi += "• Total Jadwal Tersisa: " + (tbPemantauan.getRowCount() - 1) + " hari\n";

        // Hitung jadwal yang sudah diberikan
        int jadwalDiberikan = 0;
        for (int i = 0; i < tbPemantauan.getRowCount(); i++) {
            if (i != selectedRow) { // Skip row yang akan dihapus
                Object statusObj = tbPemantauan.getValueAt(i, 3);
                if (statusObj != null && (Boolean) statusObj) {
                    jadwalDiberikan++;
                }
            }
        }

        pesanKonfirmasi += "• Jadwal Sudah Diberikan: " + jadwalDiberikan + " hari\n";
        pesanKonfirmasi += "• Jadwal Belum Diberikan: " + (tbPemantauan.getRowCount() - 1 - jadwalDiberikan) + " hari\n\n";

        // Peringatan khusus jika obat sudah diberikan
        if (diberikan) {
            pesanKonfirmasi += "⚠️ PERINGATAN KHUSUS:\n";
            pesanKonfirmasi += "• Jadwal ini SUDAH DIBERIKAN kepada pasien\n";
            pesanKonfirmasi += "• Menghapus akan MENGHILANGKAN RIWAYAT pemberian\n";
            pesanKonfirmasi += "• Data pemberian akan HILANG dari sistem\n\n";
        }

        pesanKonfirmasi += "⚠️ PERINGATAN UMUM:\n";
        pesanKonfirmasi += "• Jadwal akan TERHAPUS PERMANEN dari sistem\n";
        pesanKonfirmasi += "• Jumlah hari terapi akan BERKURANG 1 hari\n";
        pesanKonfirmasi += "• Tindakan ini TIDAK DAPAT DIBATALKAN\n\n";

        pesanKonfirmasi += "═══════════════════════════════════\n";
        pesanKonfirmasi += "Yakin ingin menghapus jadwal " + hariKe + " ini?";

        // Tampilkan dialog konfirmasi
        String[] options = {"🗑️ Ya, Hapus Jadwal", "❌ Batal"};
        int confirm = JOptionPane.showOptionDialog(this,
                pesanKonfirmasi,
                "Konfirmasi Hapus Jadwal Harian",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[1] // Default ke "Batal"
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Konfirmasi kedua untuk jadwal yang sudah diberikan
            if (diberikan) {
                String pesanKonfirmasiKedua = "⚠️ KONFIRMASI TERAKHIR\n\n"
                        + "Jadwal ini sudah diberikan kepada pasien.\n"
                        + "Menghapus akan menghilangkan riwayat pemberian obat.\n\n"
                        + "Apakah Anda YAKIN ingin melanjutkan?";

                String[] finalOptions = {"🗑️ Ya, Tetap Hapus", "❌ Batal"};
                int finalConfirm = JOptionPane.showOptionDialog(this,
                        pesanKonfirmasiKedua,
                        "Konfirmasi Terakhir",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE,
                        null,
                        finalOptions,
                        finalOptions[1]
                );

                if (finalConfirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // Proses penghapusan
            if (prosesHapusJadwalHarian(selectedRow, tanggal, hariKe, namaObat, diberikan)) {
                // Refresh tampilan
                tampilPemantauan();
                tampilTerapiObat(); // Update progress di tabel terapi

                // Tampilkan pesan sukses
                String pesanSukses = "✅ JADWAL BERHASIL DIHAPUS!\n\n";
                pesanSukses += "Obat: " + namaObat + "\n";
                pesanSukses += "Jadwal: " + hariKe + " (" + tanggal + ")\n";
                pesanSukses += "Status: " + (diberikan ? "Sudah diberikan" : "Belum diberikan") + "\n";
                pesanSukses += "Dihapus oleh: " + akses.getkode() + "\n";
                pesanSukses += "Waktu: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());

                JOptionPane.showMessageDialog(this,
                        pesanSukses,
                        "Penghapusan Berhasil",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    /**
     * Method untuk menghapus multiple jadwal yang terpilih
     */
    private void hapusMultipleJadwal() {
        if (idTerapiAktif.equals("")) {
            JOptionPane.showMessageDialog(this,
                    "❌ BELUM ADA TERAPI OBAT YANG DIPILIH!\n\n"
                    + "Silahkan pilih terapi obat terlebih dahulu di tabel atas.",
                    "Pilih Terapi Obat Dulu",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Cek checkbox yang dipilih
        java.util.List<Integer> selectedRows = new java.util.ArrayList<>();
        for (int i = 0; i < tbPemantauan.getRowCount(); i++) {
            Object checkedObj = tbPemantauan.getValueAt(i, 0);
            if (checkedObj != null && (Boolean) checkedObj) {
                selectedRows.add(i);
            }
        }

        if (selectedRows.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "❌ BELUM ADA JADWAL YANG DIPILIH!\n\n"
                    + "Untuk menghapus multiple jadwal:\n"
                    + "1. Centang checkbox di kolom 'Cek' untuk jadwal yang ingin dihapus\n"
                    + "2. Klik kanan → Pilih 'Hapus Multiple Jadwal'\n"
                    + "3. Konfirmasi penghapusan",
                    "Pilih Jadwal Terlebih Dahulu",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kumpulkan info jadwal yang akan dihapus
        StringBuilder infoJadwal = new StringBuilder();
        int jadwalDiberikan = 0;
        int jadwalBelumDiberikan = 0;
        String namaObat = getSelectedTerapiName();

        for (int i = 0; i < selectedRows.size(); i++) {
            int row = selectedRows.get(i);
            String hariKe = tbPemantauan.getValueAt(row, 1).toString();
            String tanggal = tbPemantauan.getValueAt(row, 2).toString();
            Object diberikanObj = tbPemantauan.getValueAt(row, 3);
            boolean diberikan = (diberikanObj != null && (Boolean) diberikanObj);

            if (diberikan) {
                jadwalDiberikan++;
            } else {
                jadwalBelumDiberikan++;
            }

            infoJadwal.append((i + 1)).append(". ").append(hariKe)
                    .append(" (").append(tanggal).append(") - ")
                    .append(diberikan ? "✅ Diberikan" : "❌ Belum diberikan").append("\n");
        }

        // Pesan konfirmasi untuk multiple delete
        String pesanKonfirmasi = "🗑️ KONFIRMASI HAPUS MULTIPLE JADWAL\n\n";
        pesanKonfirmasi += "═══════════════════════════════════\n";
        pesanKonfirmasi += "📋 OBAT: " + namaObat + "\n\n";
        pesanKonfirmasi += "📅 JADWAL YANG AKAN DIHAPUS (" + selectedRows.size() + " jadwal):\n";
        pesanKonfirmasi += infoJadwal.toString() + "\n";
        pesanKonfirmasi += "📊 STATISTIK PENGHAPUSAN:\n";
        pesanKonfirmasi += "• Jadwal Sudah Diberikan: " + jadwalDiberikan + " hari\n";
        pesanKonfirmasi += "• Jadwal Belum Diberikan: " + jadwalBelumDiberikan + " hari\n";
        pesanKonfirmasi += "• Sisa Jadwal Setelah Hapus: " + (tbPemantauan.getRowCount() - selectedRows.size()) + " hari\n\n";

        if (jadwalDiberikan > 0) {
            pesanKonfirmasi += "⚠️ PERINGATAN KHUSUS:\n";
            pesanKonfirmasi += "• " + jadwalDiberikan + " jadwal SUDAH DIBERIKAN kepada pasien\n";
            pesanKonfirmasi += "• Menghapus akan MENGHILANGKAN RIWAYAT pemberian\n";
            pesanKonfirmasi += "• Data pemberian akan HILANG dari sistem\n\n";
        }

        pesanKonfirmasi += "⚠️ PERINGATAN UMUM:\n";
        pesanKonfirmasi += "• SEMUA " + selectedRows.size() + " jadwal akan TERHAPUS PERMANEN\n";
        pesanKonfirmasi += "• Jumlah hari terapi akan BERKURANG " + selectedRows.size() + " hari\n";
        pesanKonfirmasi += "• Tindakan ini TIDAK DAPAT DIBATALKAN\n\n";
        pesanKonfirmasi += "═══════════════════════════════════\n";
        pesanKonfirmasi += "Yakin ingin menghapus " + selectedRows.size() + " jadwal?";

        String[] options = {"🗑️ Ya, Hapus Semua", "❌ Batal"};
        int confirm = JOptionPane.showOptionDialog(this,
                pesanKonfirmasi,
                "Konfirmasi Hapus Multiple Jadwal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[1]
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Konfirmasi kedua jika ada jadwal yang sudah diberikan
            if (jadwalDiberikan > 0) {
                String pesanKonfirmasiKedua = "⚠️ KONFIRMASI TERAKHIR\n\n"
                        + jadwalDiberikan + " jadwal sudah diberikan kepada pasien.\n"
                        + "Menghapus akan menghilangkan riwayat pemberian obat.\n\n"
                        + "Apakah Anda YAKIN ingin melanjutkan?";

                String[] finalOptions = {"🗑️ Ya, Tetap Hapus", "❌ Batal"};
                int finalConfirm = JOptionPane.showOptionDialog(this,
                        pesanKonfirmasiKedua,
                        "Konfirmasi Terakhir",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE,
                        null,
                        finalOptions,
                        finalOptions[1]
                );

                if (finalConfirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // Proses penghapusan multiple
            prosesHapusMultipleJadwal(selectedRows, namaObat);
        }
    }

    /**
     * Method untuk proses penghapusan jadwal harian
     */
    private boolean prosesHapusJadwalHarian(int selectedRow, String tanggal, String hariKe, String namaObat, boolean diberikan) {
        try {
            if (hapusJadwalHarian(idTerapiAktif, tanggal)) {
                // Update jumlah hari terapi
                updateJumlahHariTerapi();

                // Log penghapusan
                logPenghapusanJadwal(idTerapiAktif, namaObat, hariKe, tanggal, diberikan);

                return true;
            }

            return false;

        } catch (Exception e) {
            System.out.println("Error proses hapus jadwal harian: " + e);
            JOptionPane.showMessageDialog(this,
                    "❌ GAGAL MENGHAPUS JADWAL!\n\n"
                    + "Error: " + e.getMessage() + "\n\n"
                    + "Silahkan coba lagi atau hubungi administrator.",
                    "Error Penghapusan",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private boolean hapusJadwalHarian(String idTerapi, String tanggal) {
        try {
            // Convert tanggal untuk database
            String tanggalDB = convertTanggalForDB(tanggal);

            // Gunakan method meghapus yang sudah ada untuk 2 field
            // void meghapus(String table, String field, String field2, String nilai_field, String nilai_field2)
            Sequel.meghapus("pemantauan_terapi_obat_harian", "id_terapi", "tgl_pemberian", idTerapi, tanggalDB);

            return true; // Method meghapus tidak return boolean, asumsi berhasil jika tidak ada exception

        } catch (Exception e) {
            System.out.println("Error hapus jadwal harian: " + e);
            return false;
        }
    }

    /**
     * Method untuk proses penghapusan multiple jadwal
     */
    private void prosesHapusMultipleJadwal(java.util.List<Integer> selectedRows, String namaObat) {
        try {
            // Progress dialog
            javax.swing.JDialog progressDialog = new javax.swing.JDialog(this, "Menghapus Jadwal...", true);
            javax.swing.JProgressBar progressBar = new javax.swing.JProgressBar(0, selectedRows.size());
            javax.swing.JLabel statusLabel = new javax.swing.JLabel("Memulai penghapusan...");

            progressDialog.setLayout(new java.awt.BorderLayout());
            progressDialog.add(statusLabel, java.awt.BorderLayout.NORTH);
            progressDialog.add(progressBar, java.awt.BorderLayout.CENTER);
            progressDialog.setSize(400, 100);
            progressDialog.setLocationRelativeTo(this);
            progressDialog.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);

            // Worker thread untuk penghapusan
            javax.swing.SwingWorker<Integer, String> worker = new javax.swing.SwingWorker<Integer, String>() {
                @Override
                protected Integer doInBackground() throws Exception {
                    int berhasil = 0;

                    // Mulai transaction
                    Sequel.AutoComitFalse();

                    try {
                        // Hapus dari belakang ke depan untuk menghindari shifting index
                        java.util.Collections.sort(selectedRows, java.util.Collections.reverseOrder());

                        for (int i = 0; i < selectedRows.size(); i++) {
                            int row = selectedRows.get(i);
                            String hariKe = tbPemantauan.getValueAt(row, 1).toString();
                            String tanggal = tbPemantauan.getValueAt(row, 2).toString();

                            publish("Menghapus: " + hariKe + " (" + tanggal + ")...");

                            if (hapusJadwalHarian(idTerapiAktif, tanggal)) {
                                berhasil++;
                            }

                            progressBar.setValue(i + 1);
                            Thread.sleep(200); // Delay untuk UX
                        }

                        // Update jumlah hari terapi
                        updateJumlahHariTerapi();

                        // Commit transaction
                        Sequel.Commit();
                        Sequel.AutoComitTrue();

                        publish("Selesai. Berhasil menghapus " + berhasil + " jadwal.");

                    } catch (Exception e) {
                        // Rollback jika ada error
                        Sequel.RollBack();
                        Sequel.AutoComitTrue();
                        throw e;
                    }

                    return berhasil;
                }

                @Override
                protected void process(java.util.List<String> chunks) {
                    for (String message : chunks) {
                        statusLabel.setText(message);
                    }
                }

                @Override
                protected void done() {
                    progressDialog.dispose();

                    try {
                        int berhasil = get();

                        // Refresh tampilan
                        tampilPemantauan();
                        tampilTerapiObat();

                        if (berhasil > 0) {
                            JOptionPane.showMessageDialog(DlgPemantauanTerapiObat.this,
                                    "✅ PENGHAPUSAN MULTIPLE JADWAL SELESAI!\n\n"
                                    + "Jadwal berhasil dihapus: " + berhasil + " hari\n"
                                    + "Obat: " + namaObat + "\n"
                                    + "Dihapus oleh: " + akses.getkode(),
                                    "Penghapusan Berhasil",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }

                    } catch (Exception e) {
                        System.out.println("Error dalam worker thread: " + e);
                        JOptionPane.showMessageDialog(DlgPemantauanTerapiObat.this,
                                "❌ Error dalam proses penghapusan: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            worker.execute();
            progressDialog.setVisible(true);

        } catch (Exception e) {
            System.out.println("Error hapus multiple jadwal: " + e);
            JOptionPane.showMessageDialog(this,
                    "❌ Error hapus multiple jadwal: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Method untuk update jumlah hari terapi setelah penghapusan
     */
    private void updateJumlahHariTerapi() {
        try {
            // Hitung jumlah hari yang tersisa
            String sqlCount = "SELECT COUNT(*) FROM pemantauan_terapi_obat_harian WHERE id_terapi = ?";
            int jumlahHariTersisa = Sequel.cariInteger(sqlCount, idTerapiAktif);

            // Update jumlah hari dan status terapi
            String statusTerapi = "aktif";
            if (jumlahHariTersisa == 0) {
                statusTerapi = "draft";
            }

            String[] dataUpdate = new String[3];
            dataUpdate[0] = String.valueOf(jumlahHariTersisa);  // jumlah_hari
            dataUpdate[1] = statusTerapi;                       // status
            dataUpdate[2] = idTerapiAktif;                      // id (WHERE)

            String updateClause = "jumlah_hari = ?, status = ?";
            String whereClause = "id = ?";

            Sequel.mengedit3("pemantauan_terapi_obat", whereClause, updateClause, 3, dataUpdate);

            // Update counter UI
            currentHariCount = jumlahHariTersisa;
            jumlahHari.setText(String.valueOf(currentHariCount));

        } catch (Exception e) {
            System.out.println("Error update jumlah hari terapi: " + e);
        }
    }

    /**
     * Method untuk log penghapusan jadwal (audit trail)
     */
    private void logPenghapusanJadwal(String idTerapi, String namaObat, String hariKe, String tanggal, boolean diberikan) {
        try {
            String logMessage = String.format(
                    "HAPUS_JADWAL: ID=%s, Obat=%s, Jadwal=%s (%s), Status=%s, User=%s, Waktu=%s",
                    idTerapi, namaObat, hariKe, tanggal,
                    (diberikan ? "Diberikan" : "Belum"),
                    akses.getkode(),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
            );

            System.out.println("AUDIT LOG: " + logMessage);

        } catch (Exception e) {
            System.out.println("Error log penghapusan jadwal: " + e);
        }
    }

    /**
     * UPDATE CONTEXT MENU PEMANTAUAN - Tambahkan menu hapus jadwal
     */
    private void updateContextMenuPemantauan() {
        // Clear menu yang sudah ada
        if (popupMenuPemantauan != null) {
            popupMenuPemantauan.removeAll();
        } else {
            popupMenuPemantauan = new JPopupMenu();
        }

        // Menu Tambah Hari Berikutnya
        menuTambahHariPemantauan = new JMenuItem("Tambah Hari Berikutnya");
        menuTambahHariPemantauan.setIcon(new ImageIcon(getClass().getResource("/picture/category.png")));
        menuTambahHariPemantauan.addActionListener(e -> contextMenuTambahHariPemantauan());
        popupMenuPemantauan.add(menuTambahHariPemantauan);

        // Separator
        popupMenuPemantauan.add(new JSeparator());

        // ===== MENU HAPUS JADWAL BARU =====
        // Menu Hapus Jadwal Terpilih
        JMenuItem menuHapusJadwalTerpilih = new JMenuItem("Hapus Jadwal Terpilih");
        menuHapusJadwalTerpilih.setIcon(new ImageIcon(getClass().getResource("/picture/delete.png")));
        menuHapusJadwalTerpilih.addActionListener(e -> hapusJadwalTerpilih());
        popupMenuPemantauan.add(menuHapusJadwalTerpilih);

        // Menu Hapus Multiple Jadwal
        JMenuItem menuHapusMultipleJadwal = new JMenuItem("Hapus Multiple Jadwal");
        menuHapusMultipleJadwal.setIcon(new ImageIcon(getClass().getResource("/picture/delete.png")));
        menuHapusMultipleJadwal.addActionListener(e -> hapusMultipleJadwal());
        popupMenuPemantauan.add(menuHapusMultipleJadwal);

        // Separator
        popupMenuPemantauan.add(new JSeparator());

        // Menu Hapus Seluruh Jadwal Terapi (yang sudah ada)
        menuHapusJadwalPemantauan = new JMenuItem("Hapus Seluruh Jadwal Terapi");
        menuHapusJadwalPemantauan.setIcon(new ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        menuHapusJadwalPemantauan.addActionListener(e -> contextMenuHapusJadwalPemantauan());
        popupMenuPemantauan.add(menuHapusJadwalPemantauan);

        // Set popup menu ke tabel
        tbPemantauan.setComponentPopupMenu(popupMenuPemantauan);

        // Add popup menu listener untuk update state
        popupMenuPemantauan.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                updateContextMenuPemantauanState();
            }

            @Override
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
            }
        });
    }

    /**
     * Method untuk update state context menu pemantauan
     */
    private void updateContextMenuPemantauanState() {
        int selectedRow = tbPemantauan.getSelectedRow();
        boolean hasSelection = (selectedRow >= 0);
        boolean hasActiveTerapi = !idTerapiAktif.equals("");

        // Hitung checkbox yang dipilih
        int checkedCount = 0;
        for (int i = 0; i < tbPemantauan.getRowCount(); i++) {
            Object checkedObj = tbPemantauan.getValueAt(i, 0);
            if (checkedObj != null && (Boolean) checkedObj) {
                checkedCount++;
            }
        }

        // Enable/disable menu items
        if (menuTambahHariPemantauan != null) {
            menuTambahHariPemantauan.setEnabled(hasActiveTerapi && currentHariCount < MAX_HARI);
        }

        // Update menu hapus jadwal terpilih
        Component[] components = popupMenuPemantauan.getComponents();
        for (Component comp : components) {
            if (comp instanceof JMenuItem) {
                JMenuItem menuItem = (JMenuItem) comp;
                if (menuItem.getText().equals("Hapus Jadwal Terpilih")) {
                    menuItem.setEnabled(hasSelection && hasActiveTerapi);
                    if (!hasActiveTerapi) {
                        menuItem.setToolTipText("Pilih terapi obat terlebih dahulu");
                    } else if (!hasSelection) {
                        menuItem.setToolTipText("Pilih jadwal yang ingin dihapus");
                    } else {
                        menuItem.setToolTipText("Hapus jadwal yang dipilih");
                    }
                } else if (menuItem.getText().equals("Hapus Multiple Jadwal")) {
                    menuItem.setEnabled(checkedCount > 0 && hasActiveTerapi);
                    if (!hasActiveTerapi) {
                        menuItem.setToolTipText("Pilih terapi obat terlebih dahulu");
                    } else if (checkedCount == 0) {
                        menuItem.setToolTipText("Centang checkbox jadwal yang ingin dihapus");
                    } else {
                        menuItem.setToolTipText("Hapus " + checkedCount + " jadwal yang dipilih");
                    }
                } else if (menuItem.getText().equals("Hapus Seluruh Jadwal Terapi")) {
                    menuItem.setEnabled(hasActiveTerapi && tbPemantauan.getRowCount() > 0);
                    if (!hasActiveTerapi) {
                        menuItem.setToolTipText("Pilih terapi obat terlebih dahulu");
                    } else if (tbPemantauan.getRowCount() == 0) {
                        menuItem.setToolTipText("Tidak ada jadwal untuk dihapus");
                    } else {
                        menuItem.setToolTipText("Hapus semua jadwal terapi");
                    }
                }
            }
        }
    }

    /**
     * Method untuk keyboard shortcuts penghapusan jadwal
     */
    private void setupHapusJadwalKeyboardShortcuts() {
        // Delete key untuk hapus jadwal terpilih
        tbPemantauan.getInputMap(javax.swing.JComponent.WHEN_FOCUSED).put(
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0),
                "hapusJadwalTerpilih"
        );
        tbPemantauan.getActionMap().put("hapusJadwalTerpilih", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                hapusJadwalTerpilih();
            }
        });

        // Shift+Delete untuk hapus multiple jadwal
        tbPemantauan.getInputMap(javax.swing.JComponent.WHEN_FOCUSED).put(
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, java.awt.event.InputEvent.SHIFT_DOWN_MASK),
                "hapusMultipleJadwal"
        );
        tbPemantauan.getActionMap().put("hapusMultipleJadwal", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                hapusMultipleJadwal();
            }
        });

        // Ctrl+A untuk select all checkbox
        tbPemantauan.getInputMap(javax.swing.JComponent.WHEN_FOCUSED).put(
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_DOWN_MASK),
                "selectAllJadwal"
        );
        tbPemantauan.getActionMap().put("selectAllJadwal", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                selectAllJadwal();
            }
        });

        // Ctrl+D untuk deselect all checkbox
        tbPemantauan.getInputMap(javax.swing.JComponent.WHEN_FOCUSED).put(
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_DOWN_MASK),
                "deselectAllJadwal"
        );
        tbPemantauan.getActionMap().put("deselectAllJadwal", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                deselectAllJadwal();
            }
        });
    }

    /**
     * Method untuk select all checkbox jadwal
     */
    private void selectAllJadwal() {
        if (tbPemantauan.getRowCount() == 0) {
            return;
        }

        isUpdatingTable = true;
        try {
            for (int i = 0; i < tbPemantauan.getRowCount(); i++) {
                tbPemantauan.setValueAt(true, i, 0);
            }
        } finally {
            isUpdatingTable = false;
        }

        // Update tooltip untuk member feedback
        int count = tbPemantauan.getRowCount();
        tbPemantauan.setToolTipText(count + " jadwal dipilih untuk dihapus");

        // Auto-hide tooltip setelah 3 detik
        javax.swing.Timer timer = new javax.swing.Timer(3000, e -> {
            tbPemantauan.setToolTipText(null);
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Method untuk deselect all checkbox jadwal
     */
    private void deselectAllJadwal() {
        if (tbPemantauan.getRowCount() == 0) {
            return;
        }

        isUpdatingTable = true;
        try {
            for (int i = 0; i < tbPemantauan.getRowCount(); i++) {
                tbPemantauan.setValueAt(false, i, 0);
            }
        } finally {
            isUpdatingTable = false;
        }

        // Update tooltip
        tbPemantauan.setToolTipText("Semua jadwal tidak dipilih");

        // Auto-hide tooltip setelah 3 detik
        javax.swing.Timer timer = new javax.swing.Timer(3000, e -> {
            tbPemantauan.setToolTipText(null);
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Method untuk menampilkan konfirmasi sebelum hapus berdasarkan kondisi
     * jadwal
     */
    private boolean konfirmasiHapusJadwal(String hariKe, String tanggal, boolean diberikan, String keterangan) {
        String pesan = "Yakin ingin menghapus jadwal " + hariKe + " (" + tanggal + ")?";

        if (diberikan) {
            pesan += "\n\n⚠️ PERINGATAN: Jadwal ini sudah diberikan!";
            pesan += "\nMenghapus akan menghilangkan riwayat pemberian obat.";
        }

        if (!keterangan.isEmpty()) {
            pesan += "\n\n📝 Keterangan: " + keterangan;
        }

        int confirm = JOptionPane.showConfirmDialog(this, pesan,
                "Konfirmasi Hapus Jadwal",
                JOptionPane.YES_NO_OPTION,
                diberikan ? JOptionPane.WARNING_MESSAGE : JOptionPane.QUESTION_MESSAGE);

        return confirm == JOptionPane.YES_OPTION;
    }

    /**
     * Method untuk bulk action pada checkbox
     */
    private void bulkActionJadwal() {
        if (idTerapiAktif.equals("")) {
            JOptionPane.showMessageDialog(this, "Pilih terapi obat terlebih dahulu!");
            return;
        }

        // Hitung checkbox yang dipilih
        java.util.List<Integer> selectedRows = new java.util.ArrayList<>();
        for (int i = 0; i < tbPemantauan.getRowCount(); i++) {
            Object checkedObj = tbPemantauan.getValueAt(i, 0);
            if (checkedObj != null && (Boolean) checkedObj) {
                selectedRows.add(i);
            }
        }

        if (selectedRows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Centang checkbox jadwal yang ingin diproses!");
            return;
        }

        // Dialog pilihan bulk action
        String[] options = {
            "🗑️ Hapus Jadwal Terpilih",
            "✅ Tandai Sudah Diberikan",
            "❌ Tandai Belum Diberikan",
            "📝 Update Keterangan",
            "🔄 Batal"
        };

        int choice = JOptionPane.showOptionDialog(this,
                "Pilih aksi untuk " + selectedRows.size() + " jadwal yang dipilih:",
                "Bulk Action Jadwal",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[4]
        );

        switch (choice) {
            case 0: // Hapus
                hapusMultipleJadwal();
                break;
            case 1: // Tandai sudah diberikan
                bulkUpdateStatusPemberian(selectedRows, true);
                break;
            case 2: // Tandai belum diberikan
                bulkUpdateStatusPemberian(selectedRows, false);
                break;
            case 3: // Update keterangan
                bulkUpdateKeteranganJadwal(selectedRows);
                break;
            default:
                break;
        }
    }

    /**
     * Method untuk bulk update status pemberian
     */
    private void bulkUpdateStatusPemberian(java.util.List<Integer> selectedRows, boolean diberikan) {
        String statusText = diberikan ? "DIBERIKAN" : "BELUM DIBERIKAN";

        int confirm = JOptionPane.showConfirmDialog(this,
                "Yakin ingin mengubah status " + selectedRows.size() + " jadwal menjadi " + statusText + "?",
                "Konfirmasi Bulk Update Status",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int berhasil = 0;
                for (int row : selectedRows) {
                    String tanggal = tbPemantauan.getValueAt(row, 2).toString();
                    if (updateStatusPemberian(idTerapiAktif, tanggal, diberikan)) {
                        berhasil++;

                        // Update tampilan tabel
                        isUpdatingTable = true;
                        try {
                            tbPemantauan.setValueAt(diberikan, row, 3);
                            tbPemantauan.setValueAt(diberikan ? akses.getkode() : "-", row, 5);
                            tbPemantauan.setValueAt(diberikan
                                    ? new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()) : "", row, 6);
                        } finally {
                            isUpdatingTable = false;
                        }
                    }
                }

                if (berhasil > 0) {
                    JOptionPane.showMessageDialog(this,
                            "✅ Berhasil mengubah status " + berhasil + " jadwal menjadi " + statusText,
                            "Bulk Update Berhasil",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Refresh tabel terapi
                    tampilTerapiObat();

                    // Clear checkbox
                    deselectAllJadwal();
                }

            } catch (Exception e) {
                System.out.println("Error bulk update status: " + e);
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    /**
     * Method untuk bulk update keterangan jadwal
     */
    private void bulkUpdateKeteranganJadwal(java.util.List<Integer> selectedRows) {
        String keteranganBaru = JOptionPane.showInputDialog(this,
                "Masukkan keterangan untuk " + selectedRows.size() + " jadwal:\n"
                + "(Kosongkan untuk menghapus keterangan)",
                "Bulk Update Keterangan",
                JOptionPane.QUESTION_MESSAGE);

        if (keteranganBaru != null) {
            try {
                int berhasil = 0;
                for (int row : selectedRows) {
                    String tanggal = tbPemantauan.getValueAt(row, 2).toString();
                    String tanggalDB = convertTanggalForDB(tanggal);

                    if (updateKeteranganKeDatabase(idTerapiAktif, tanggalDB, keteranganBaru.trim())) {
                        berhasil++;

                        // Update tampilan tabel
                        isUpdatingTable = true;
                        try {
                            tbPemantauan.setValueAt(keteranganBaru.trim(), row, 4);
                            tbPemantauan.setValueAt(akses.getkode(), row, 5);
                            tbPemantauan.setValueAt(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()), row, 6);
                        } finally {
                            isUpdatingTable = false;
                        }
                    }
                }

                if (berhasil > 0) {
                    JOptionPane.showMessageDialog(this,
                            "✅ Berhasil mengubah keterangan " + berhasil + " jadwal",
                            "Bulk Update Berhasil",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Clear checkbox
                    deselectAllJadwal();
                }

            } catch (Exception e) {
                System.out.println("Error bulk update keterangan: " + e);
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void handleCustomCaraPemberian() {
        String selectedItem = (String) caraPemberian.getSelectedItem();

        if ("Lainnya".equals(selectedItem)) {
            String customCara = JOptionPane.showInputDialog(this,
                    "Masukkan singkatan cara pemberian:\n"
                    + "(contoh: PO-NGT, IV-DRIP, TOP-BID)\n"
                    + "Maksimal 10 karakter, huruf dan angka saja",
                    "Cara Pemberian Custom",
                    JOptionPane.QUESTION_MESSAGE);

            if (customCara != null && !customCara.trim().isEmpty()) {
                // Convert ke uppercase dan validasi
                String formattedCara = customCara.trim().toUpperCase();

                if (isValidCaraPemberian(formattedCara)) {
                    // Tambahkan ke model ComboBox jika belum ada
                    javax.swing.DefaultComboBoxModel<String> model
                            = (javax.swing.DefaultComboBoxModel<String>) caraPemberian.getModel();

                    boolean exists = false;
                    for (int i = 0; i < model.getSize(); i++) {
                        if (formattedCara.equals(model.getElementAt(i))) {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists) {
                        // Insert sebelum "Lainnya"
                        model.insertElementAt(formattedCara, model.getSize() - 1);
                    }

                    caraPemberian.setSelectedItem(formattedCara);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Format tidak valid!\n\n"
                            + "Aturan:\n"
                            + "• Maksimal 10 karakter\n"
                            + "• Hanya huruf, angka, dan tanda (-)\n"
                            + "• Contoh yang benar: PO-NGT, IV-DRIP, TOP-BID",
                            "Format Cara Pemberian Salah",
                            JOptionPane.WARNING_MESSAGE);

                    // Kembali ke pilihan sebelumnya
                    caraPemberian.setSelectedIndex(0);
                }
            } else {
                // User cancel, kembali ke pilihan sebelumnya
                caraPemberian.setSelectedIndex(0);
            }
        }
    }

    private void loadCaraPemberianFromDB() {
        try {
            javax.swing.DefaultComboBoxModel<String> model
                    = new javax.swing.DefaultComboBoxModel<>();

            // Tambahkan pilihan default dengan singkatan standar
            String[] defaultRoutes = {
                "PO", // Per Oral
                "IV", // Intravena  
                "IM", // Intramuskular
                "SC", // Subkutan
                "INH", // Inhalasi
                "PR", // Per Rektal
                "PV", // Per Vaginal
                "SL", // Sublingual
                "TOP", // Topikal
                "OD", // Oculus Dextra
                "OS", // Oculus Sinistra
                "OU", // Oculus Uterque
                "AD", // Auris Dextra
                "AS", // Auris Sinistra
                "AU", // Auris Uterque
                "NAS", // Nasal
                "ID", // Intradermal
                "IT", // Intrathecal
                "INF", // Infus
                "NEB" // Nebulizer
            };

            for (String route : defaultRoutes) {
                model.addElement(route);
            }

            // Load dari database (jika ada cara pemberian custom yang sudah digunakan)
            String sql = "SELECT DISTINCT cara_pemberian FROM pemantauan_terapi_obat "
                    + "WHERE cara_pemberian IS NOT NULL AND cara_pemberian != '' "
                    + "AND cara_pemberian NOT IN ('PO','IV','IM','SC','INH','PR','PV','SL','TOP','OD','OS','OU','AD','AS','AU','NAS','ID','IT','INF','NEB') "
                    + "ORDER BY cara_pemberian";
            PreparedStatement ps = koneksi.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            // Tambahkan cara pemberian custom dari database
            while (rs.next()) {
                String cara = rs.getString("cara_pemberian");
                if (cara != null && !cara.trim().isEmpty()) {
                    model.addElement(cara.trim());
                }
            }

            // Tambahkan "Lainnya" di akhir
            model.addElement("Lainnya");

            caraPemberian.setModel(model);

            rs.close();
            ps.close();

        } catch (Exception e) {
            System.out.println("Error load cara pemberian: " + e);
            // Jika error, tetap gunakan default
        }
    }

// PANGGIL loadCaraPemberianFromDB() di initAfterComponents() jika ingin menggunakan
// ===== DAFTAR SINGKATAN CARA PEMBERIAN OBAT STANDAR MEDIS =====
/*
SINGKATAN STANDAR YANG DIGUNAKAN:

ORAL & ENTERAL:
- PO    = Per Oral (diminum melalui mulut)
- SL    = Sublingual (dibawah lidah)
- PO-NGT = Per Oral via NGT (lewat selang nasogastrik)

PARENTERAL (SUNTIKAN):
- IV    = Intravena (suntik ke pembuluh darah vena)
- IM    = Intramuskular (suntik ke otot)
- SC    = Subkutan (suntik di bawah kulit)
- ID    = Intradermal (suntik di dalam kulit)
- IT    = Intrathecal (suntik ke ruang subarachnoid)
- INF   = Infus (tetesan intravena)

MATA (OCULAR):
- OD    = Oculus Dextra (mata kanan)
- OS    = Oculus Sinistra (mata kiri) 
- OU    = Oculus Uterque (kedua mata)

TELINGA (AURAL):
- AD    = Auris Dextra (telinga kanan)
- AS    = Auris Sinistra (telinga kiri)
- AU    = Auris Uterque (kedua telinga)

HIDUNG & INHALASI:
- NAS   = Nasal (hidung)
- INH   = Inhalasi (dihirup via nebulizer/inhaler)
- NEB   = Nebulizer (via nebulizer khusus)

REKTAL & VAGINAL:
- PR    = Per Rektal (melalui dubur)
- PV    = Per Vaginal (melalui vagina)

TOPIKAL:
- TOP   = Topikal (dioleskan pada kulit)

LAINNYA:
- Lainnya = Untuk cara pemberian khusus/custom

CONTOH PENGGUNAAN:
- Paracetamol 500mg PO q6h
- Insulin 10 unit SC bid
- Normal Saline 500ml IV drip 8h
- Chloramphenicol 0.5% OU q6h
- Salbutamol INH q4h prn
     */
}
