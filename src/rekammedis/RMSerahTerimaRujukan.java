package rekammedis;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import kepegawaian.DlgCariDokter;
import kepegawaian.DlgCariPetugas;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import widget.Button;
import widget.InternalFrame;
import widget.Label;
import widget.TextBox;
import widget.CekBox;
import widget.ComboBox;
import widget.PanelBiasa;
import widget.ScrollPane;
import widget.panelisi;

public class RMSerahTerimaRujukan extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar;
    private Button BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2;
    private TextBox TCari;
    private Label LCount;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;

    // Header
    private TextBox TNoRw, TNoRM, TPasien, TUmur, TAlamat, TTglLahir;

    // Form fields
    private widget.Tanggal TglAsuhan;
    private TextBox THari, TRujukKe, TPukul, TDiagnosa, TRSTujuan, TPenanggungJawab, TLainLain;
    private TextBox NamaMenerima, NamaKeluarga;
    private TextBox KdDokter, NmDokter, KdPetugas, NmPetugas;
    private Button BtnDokter, BtnPetugas;
    private ComboBox CmbAlasan;
    private CekBox ChkSuratRujukan, ChkHasilLab, ChkHasilRadiologi, ChkLainLain;

    private DlgCariDokter dokterDlg = new DlgCariDokter(null, false);
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);

    // Table
    private widget.Table tbObat;
    private javax.swing.table.DefaultTableModel tabMode;
    private ScrollPane Scroll;

    // Input panel
    private CekBox ChkInput;
    private PanelBiasa FormInput;
    private ScrollPane ScrollInput;
    private JPanel PanelInput;

    public widget.editorpane LoadHTML;

    public RMSerahTerimaRujukan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();

        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Serah Terima Pasien Rujukan ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11),
            new Color(50, 50, 50)
        ));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // PANEL ATAS (Header)
        panelisi panelAtas = new panelisi();
        panelAtas.setLayout(new java.awt.GridLayout(2, 1));
        panelAtas.setPreferredSize(new Dimension(1024, 60));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 4));
        row1.setOpaque(false);
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 4));
        row2.setOpaque(false);

        Label lblNoRw = new Label(); lblNoRw.setText("No.Rawat :");
        TNoRw = new TextBox(); TNoRw.setPreferredSize(new Dimension(130, 23));
        TNoRM = new TextBox(); TNoRM.setPreferredSize(new Dimension(100, 23));
        TPasien = new TextBox(); TPasien.setPreferredSize(new Dimension(200, 23));
        Label lblUmur = new Label(); lblUmur.setText("Umur :");
        TUmur = new TextBox(); TUmur.setPreferredSize(new Dimension(80, 23));

        row1.add(lblNoRw); row1.add(TNoRw); row1.add(TNoRM); row1.add(TPasien);
        row1.add(lblUmur); row1.add(TUmur);

        Label lblTglLahir = new Label(); lblTglLahir.setText("Tgl.Lahir :");
        TTglLahir = new TextBox(); TTglLahir.setPreferredSize(new Dimension(90, 23));
        Label lblAlamat = new Label(); lblAlamat.setText("Alamat :");
        TAlamat = new TextBox(); TAlamat.setPreferredSize(new Dimension(300, 23));

        row2.add(lblTglLahir); row2.add(TTglLahir);
        row2.add(lblAlamat); row2.add(TAlamat);

        panelAtas.add(row1);
        panelAtas.add(row2);

        // TABLE
        Scroll = new ScrollPane();
        tbObat = new widget.Table();
        tabMode = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat", "No.R.M.", "Nama Pasien", "Tanggal", "Hari", "Rujuk Ke", "Pukul", "Dokter", "Diagnosa", "Alasan Rujukan", "RS Tujuan", "Penanggung Jawab", "Surat Rujukan", "Hasil Lab", "Hasil Radiologi", "Lain-lain", "Menyerahkan", "Menerima", "Keluarga"}
        ) {
            @Override public boolean isCellEditable(int rowIndex, int colIndex) { return false; }
        };
        tbObat.setModel(tabMode);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (tabMode.getRowCount() != 0) {
                    try { getData(); } catch (Exception e) {}
                }
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                    getData();
                }
            }
        });
        // Popup menu klik kanan di tabel
        javax.swing.JPopupMenu popupMenu = new javax.swing.JPopupMenu();
        javax.swing.JMenuItem mnCetak = new javax.swing.JMenuItem("Cetak Serah Terima Rujukan");
        mnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetak.addActionListener(evt -> BtnPrint.doClick());
        popupMenu.add(mnCetak);
        tbObat.setComponentPopupMenu(popupMenu);

        Scroll.setViewportView(tbObat);

        // FORM INPUT
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(900, 370));
        FormInput.setLayout(null);

        int y = 10;
        JLabel lblHeader = new JLabel("SERAH TERIMA PASIEN RUJUKAN");
        lblHeader.setFont(new java.awt.Font("Tahoma", 1, 12));
        lblHeader.setBounds(10, y, 350, 23);
        FormInput.add(lblHeader);

        y += 30;
        Label lblTgl = new Label(); lblTgl.setText("Tanggal :"); lblTgl.setBounds(10, y, 120, 23); FormInput.add(lblTgl);
        TglAsuhan = new widget.Tanggal();
        TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setBounds(135, y, 130, 23); FormInput.add(TglAsuhan);

        Label lblHari = new Label(); lblHari.setText("Hari :"); lblHari.setBounds(280, y, 40, 23); FormInput.add(lblHari);
        THari = new TextBox(); THari.setBounds(325, y, 100, 23); FormInput.add(THari);

        Label lblPukul = new Label(); lblPukul.setText("Pukul :"); lblPukul.setBounds(440, y, 50, 23); FormInput.add(lblPukul);
        TPukul = new TextBox(); TPukul.setBounds(495, y, 60, 23); FormInput.add(TPukul);

        y += 28;
        Label lblRujukKe = new Label(); lblRujukKe.setText("Rujuk ke :"); lblRujukKe.setBounds(10, y, 120, 23); FormInput.add(lblRujukKe);
        TRujukKe = new TextBox(); TRujukKe.setBounds(135, y, 420, 23); FormInput.add(TRujukKe);

        y += 28;
        Label lblDokter = new Label(); lblDokter.setText("Dokter yg merawat :"); lblDokter.setBounds(10, y, 120, 23); FormInput.add(lblDokter);
        KdDokter = new TextBox(); KdDokter.setBounds(135, y, 80, 23); FormInput.add(KdDokter);
        NmDokter = new TextBox(); NmDokter.setBounds(217, y, 210, 23); FormInput.add(NmDokter);
        BtnDokter = new Button(); BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokter.setBounds(429, y, 28, 23); FormInput.add(BtnDokter);
        BtnDokter.addActionListener(evt -> {
            dokterDlg.emptTeks(); dokterDlg.isCek();
            dokterDlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            dokterDlg.setLocationRelativeTo(internalFrame1); dokterDlg.setVisible(true);
        });
        dokterDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (dokterDlg.getTable().getSelectedRow() != -1) {
                    KdDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(), 0).toString());
                    NmDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

        y += 28;
        Label lblDiagnosa = new Label(); lblDiagnosa.setText("Diagnosa :"); lblDiagnosa.setBounds(10, y, 120, 23); FormInput.add(lblDiagnosa);
        TDiagnosa = new TextBox(); TDiagnosa.setBounds(135, y, 420, 23); FormInput.add(TDiagnosa);

        y += 28;
        Label lblAlasan = new Label(); lblAlasan.setText("Alasan Rujukan :"); lblAlasan.setBounds(10, y, 120, 23); FormInput.add(lblAlasan);
        CmbAlasan = new ComboBox();
        CmbAlasan.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "Atas instruksi dokter yang merawat",
            "Atas permintaan sendiri atau keluarga",
            "Atas permintaan perusahaan",
            "Keterbatasan fasilitas di RSUD Cabangbungin"
        }));
        CmbAlasan.setBounds(135, y, 420, 23); FormInput.add(CmbAlasan);

        y += 28;
        Label lblRSTujuan = new Label(); lblRSTujuan.setText("RS Tujuan :"); lblRSTujuan.setBounds(10, y, 120, 23); FormInput.add(lblRSTujuan);
        TRSTujuan = new TextBox(); TRSTujuan.setBounds(135, y, 420, 23); FormInput.add(TRSTujuan);

        y += 28;
        Label lblPJ = new Label(); lblPJ.setText("Penanggung Jawab :"); lblPJ.setBounds(10, y, 120, 23); FormInput.add(lblPJ);
        TPenanggungJawab = new TextBox(); TPenanggungJawab.setBounds(135, y, 420, 23); FormInput.add(TPenanggungJawab);

        y += 28;
        JLabel lblData = new JLabel("Data pasien yang disertakan :");
        lblData.setBounds(10, y, 200, 23); FormInput.add(lblData);
        ChkSuratRujukan = new CekBox(); ChkSuratRujukan.setText("Surat Rujukan Pasien"); ChkSuratRujukan.setBounds(210, y, 160, 23); FormInput.add(ChkSuratRujukan);
        ChkHasilLab = new CekBox(); ChkHasilLab.setText("Hasil Laboratorium"); ChkHasilLab.setBounds(380, y, 160, 23); FormInput.add(ChkHasilLab);

        y += 25;
        ChkHasilRadiologi = new CekBox(); ChkHasilRadiologi.setText("Hasil Radiologi"); ChkHasilRadiologi.setBounds(210, y, 130, 23); FormInput.add(ChkHasilRadiologi);
        ChkLainLain = new CekBox(); ChkLainLain.setText("Lain-lain :"); ChkLainLain.setBounds(380, y, 90, 23); FormInput.add(ChkLainLain);
        TLainLain = new TextBox(); TLainLain.setBounds(475, y, 200, 23); FormInput.add(TLainLain);

        y += 30;
        Label lblMenyerahkan = new Label(); lblMenyerahkan.setText("Yang menyerahkan :"); lblMenyerahkan.setBounds(10, y, 120, 23); FormInput.add(lblMenyerahkan);
        KdPetugas = new TextBox(); KdPetugas.setBounds(135, y, 80, 23); FormInput.add(KdPetugas);
        NmPetugas = new TextBox(); NmPetugas.setBounds(217, y, 170, 23); FormInput.add(NmPetugas);
        BtnPetugas = new Button(); BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPetugas.setBounds(389, y, 28, 23); FormInput.add(BtnPetugas);
        BtnPetugas.addActionListener(evt -> {
            petugasDlg.emptTeks(); petugasDlg.isCek();
            petugasDlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            petugasDlg.setLocationRelativeTo(internalFrame1); petugasDlg.setVisible(true);
        });
        petugasDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (petugasDlg.getTable().getSelectedRow() != -1) {
                    KdPetugas.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(), 0).toString());
                    NmPetugas.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

        y += 28;
        Label lblMenerima = new Label(); lblMenerima.setText("Yang Menerima :"); lblMenerima.setBounds(10, y, 120, 23); FormInput.add(lblMenerima);
        NamaMenerima = new TextBox(); NamaMenerima.setBounds(135, y, 250, 23); FormInput.add(NamaMenerima);

        Label lblKeluarga = new Label(); lblKeluarga.setText("Keluarga Pasien :"); lblKeluarga.setBounds(400, y, 110, 23); FormInput.add(lblKeluarga);
        NamaKeluarga = new TextBox(); NamaKeluarga.setBounds(515, y, 200, 23); FormInput.add(NamaKeluarga);

        // Input toggle checkbox
        ChkInput = new CekBox();
        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setMnemonic('1');
        ChkInput.setText(".: Input Data");
        ChkInput.setToolTipText("Alt+1");
        ChkInput.setBorderPainted(true);
        ChkInput.setBorderPaintedFlat(true);
        ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput.setPreferredSize(new Dimension(192, 20));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setSelected(true);
        ChkInput.addActionListener(evt -> isForm());

        ScrollInput = new ScrollPane();
        ScrollInput.setViewportView(FormInput);
        ScrollInput.setPreferredSize(new Dimension(900, 390));

        PanelInput = new JPanel(new BorderLayout());
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new Dimension(WIDTH, 410));
        PanelInput.add(ScrollInput, BorderLayout.CENTER);
        PanelInput.add(ChkInput, BorderLayout.PAGE_END);

        // PANEL BAWAH
        panelBawah = new panelisi();
        panelBawah.setPreferredSize(new Dimension(44, 54));
        panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));

        BtnSimpan = new Button();
        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setMnemonic('S'); BtnSimpan.setText("Simpan"); BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setPreferredSize(new Dimension(100, 30));

        BtnBatal = new Button();
        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBatal.setMnemonic('B'); BtnBatal.setText("Baru"); BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setPreferredSize(new Dimension(100, 30));

        BtnHapus = new Button();
        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setMnemonic('H'); BtnHapus.setText("Hapus"); BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setPreferredSize(new Dimension(100, 30));

        BtnEdit = new Button();
        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnEdit.setMnemonic('G'); BtnEdit.setText("Ganti"); BtnEdit.setToolTipText("Alt+G");
        BtnEdit.setPreferredSize(new Dimension(100, 30));

        BtnPrint = new Button();
        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        BtnPrint.setMnemonic('T'); BtnPrint.setText("Cetak"); BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setPreferredSize(new Dimension(100, 30));

        BtnKeluar = new Button();
        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K'); BtnKeluar.setText("Keluar"); BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setPreferredSize(new Dimension(100, 30));

        Label lblTglCari = new Label(); lblTglCari.setText("Tanggal :"); lblTglCari.setPreferredSize(new Dimension(55, 23));
        DTPCari1 = new widget.Tanggal(); DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setPreferredSize(new Dimension(90, 23));
        Label lblSd = new Label(); lblSd.setText("s.d."); lblSd.setPreferredSize(new Dimension(25, 23));
        DTPCari2 = new widget.Tanggal(); DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setPreferredSize(new Dimension(90, 23));
        Label lblKey = new Label(); lblKey.setText("Key Word :"); lblKey.setPreferredSize(new Dimension(70, 23));
        TCari = new TextBox(); TCari.setPreferredSize(new Dimension(150, 23));
        BtnCari = new Button(); BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setMnemonic('4'); BtnCari.setToolTipText("Alt+4"); BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnAll = new Button(); BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setMnemonic('M'); BtnAll.setToolTipText("Alt+M"); BtnAll.setPreferredSize(new Dimension(28, 23));
        LCount = new Label(); LCount.setText("Record : 0"); LCount.setPreferredSize(new Dimension(100, 23));

        panelBawah.add(BtnSimpan); panelBawah.add(BtnBatal); panelBawah.add(BtnHapus);
        panelBawah.add(BtnEdit); panelBawah.add(BtnPrint); panelBawah.add(BtnKeluar);
        panelBawah.add(lblTglCari); panelBawah.add(DTPCari1); panelBawah.add(lblSd);
        panelBawah.add(DTPCari2); panelBawah.add(lblKey); panelBawah.add(TCari);
        panelBawah.add(BtnCari); panelBawah.add(BtnAll); panelBawah.add(LCount);

        // ACTION LISTENERS
        BtnKeluar.addActionListener(evt -> dispose());
        BtnBatal.addActionListener(evt -> emptTeks());
        BtnCari.addActionListener(evt -> tampil());
        BtnAll.addActionListener(evt -> { TCari.setText(""); tampil(); });

        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) { tampil(); }
            }
        });

        BtnSimpan.addActionListener(evt -> {
            if (TNoRw.getText().trim().equals("")) {
                Valid.textKosong(TNoRw, "Pasien");
            } else if (KdPetugas.getText().trim().equals("")) {
                Valid.textKosong(KdPetugas, "Petugas yang menyerahkan");
            } else {
                if (Sequel.menyimpantf("serah_terima_rujukan", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 17, new String[]{
                    TNoRw.getText(),
                    Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + " " + TglAsuhan.getSelectedItem().toString().substring(11, 19),
                    THari.getText(), TRujukKe.getText(), TPukul.getText(), KdDokter.getText(),
                    TDiagnosa.getText(), CmbAlasan.getSelectedItem().toString(), TRSTujuan.getText(),
                    TPenanggungJawab.getText(),
                    ChkSuratRujukan.isSelected() ? "Ya" : "Tidak",
                    ChkHasilLab.isSelected() ? "Ya" : "Tidak",
                    ChkHasilRadiologi.isSelected() ? "Ya" : "Tidak",
                    ChkLainLain.isSelected() ? TLainLain.getText() : "",
                    KdPetugas.getText(), NamaMenerima.getText(), NamaKeluarga.getText()
                })) {
                    tampil();
                    emptTeks();
                }
            }
        });

        BtnHapus.addActionListener(evt -> {
            if (tbObat.getSelectedRow() != -1) {
                if (Sequel.queryu2tf("delete from serah_terima_rujukan where no_rawat=? and tanggal=?",
                    2, new String[]{
                        tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                        tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
                    })) {
                    tampil();
                    emptTeks();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dihapus...!!!!");
            }
        });

        BtnEdit.addActionListener(evt -> {
            if (TNoRw.getText().trim().equals("")) {
                Valid.textKosong(TNoRw, "Pasien");
            } else if (tbObat.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau diganti...!!!!");
            } else {
                if (Sequel.mengedittf("serah_terima_rujukan", "no_rawat=? and tanggal=?",
                    "hari=?,rujuk_ke=?,pukul=?,dokter_merawat=?,diagnosa=?,alasan_rujukan=?,rs_tujuan=?,penanggung_jawab_biaya=?," +
                    "surat_rujukan=?,hasil_lab=?,hasil_radiologi=?,lain_lain=?,nip_menyerahkan=?,nama_menerima=?,nama_keluarga=?", 17, new String[]{
                    THari.getText(), TRujukKe.getText(), TPukul.getText(), KdDokter.getText(),
                    TDiagnosa.getText(), CmbAlasan.getSelectedItem().toString(), TRSTujuan.getText(),
                    TPenanggungJawab.getText(),
                    ChkSuratRujukan.isSelected() ? "Ya" : "Tidak",
                    ChkHasilLab.isSelected() ? "Ya" : "Tidak",
                    ChkHasilRadiologi.isSelected() ? "Ya" : "Tidak",
                    ChkLainLain.isSelected() ? TLainLain.getText() : "",
                    KdPetugas.getText(), NamaMenerima.getText(), NamaKeluarga.getText(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
                })) {
                    tampil();
                    emptTeks();
                }
            }
        });

        BtnPrint.addActionListener(evt -> {
            if (TNoRw.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu..!!!");
            } else {
                Map<String, Object> param = new HashMap<>();
                param.put("namars", akses.getnamars());
                param.put("alamatrs", akses.getalamatrs());
                param.put("propinsirs", akses.getpropinsirs());
                param.put("kabupatenrs", akses.getkabupatenrs());
                param.put("kontakrs", akses.getkontakrs());
                param.put("emailrs", akses.getemailrs());
                param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
                Valid.MyReportqry("rptSerahTerimaRujukan.jasper", "report", "::[ Serah Terima Pasien Rujukan ]::",
                    "select str.*, r.no_rkm_medis, p.nm_pasien, p.tgl_lahir, p.alamat, concat(r.umurdaftar,' ',r.sttsumur) as umur, " +
                    "d.nm_dokter, pt.nama as nm_petugas " +
                    "from serah_terima_rujukan str " +
                    "inner join reg_periksa r on str.no_rawat=r.no_rawat " +
                    "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                    "left join dokter d on str.dokter_merawat=d.kd_dokter " +
                    "left join petugas pt on str.nip_menyerahkan=pt.nip " +
                    "where str.no_rawat='" + TNoRw.getText() + "' " +
                    "and str.tanggal='" + Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + " " + TglAsuhan.getSelectedItem().toString().substring(11, 19) + "'", param);
            }
        });

        // ASSEMBLY
        InternalFrame internalFrameData = new InternalFrame();
        internalFrameData.setLayout(new BorderLayout());
        internalFrameData.setBackground(new Color(255, 255, 255));
        internalFrameData.add(PanelInput, BorderLayout.NORTH);
        internalFrameData.add(Scroll, BorderLayout.CENTER);

        internalFrame1.add(panelAtas, BorderLayout.PAGE_START);
        internalFrame1.add(internalFrameData, BorderLayout.CENTER);
        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);

        setLayout(new BorderLayout());
        add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
    }

    private void isForm() {
        if (ChkInput.isSelected()) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 410));
            FormInput.setVisible(true);
            ChkInput.setVisible(true);
        } else {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 20));
            FormInput.setVisible(false);
            ChkInput.setVisible(true);
        }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select str.no_rawat, r.no_rkm_medis, p.nm_pasien, str.tanggal, str.hari, str.rujuk_ke, str.pukul, " +
                "d.nm_dokter, str.diagnosa, str.alasan_rujukan, str.rs_tujuan, str.penanggung_jawab_biaya, " +
                "str.surat_rujukan, str.hasil_lab, str.hasil_radiologi, str.lain_lain, " +
                "pt.nama as nm_petugas, str.nama_menerima, str.nama_keluarga " +
                "from serah_terima_rujukan str " +
                "inner join reg_periksa r on str.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                "left join dokter d on str.dokter_merawat=d.kd_dokter " +
                "left join petugas pt on str.nip_menyerahkan=pt.nip " +
                "where str.tanggal between ? and ? and (str.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) " +
                "order by str.tanggal desc");
            try {
                ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
                ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                ps.setString(3, "%" + TCari.getText().trim() + "%");
                ps.setString(4, "%" + TCari.getText().trim() + "%");
                ps.setString(5, "%" + TCari.getText().trim() + "%");
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("tanggal"), rs.getString("hari"), rs.getString("rujuk_ke"),
                        rs.getString("pukul"), rs.getString("nm_dokter"), rs.getString("diagnosa"),
                        rs.getString("alasan_rujukan"), rs.getString("rs_tujuan"), rs.getString("penanggung_jawab_biaya"),
                        rs.getString("surat_rujukan"), rs.getString("hasil_lab"), rs.getString("hasil_radiologi"),
                        rs.getString("lain_lain"), rs.getString("nm_petugas"), rs.getString("nama_menerima"),
                        rs.getString("nama_keluarga")
                    });
                }
                LCount.setText("Record : " + tabMode.getRowCount());
            } catch (Exception e) {
                System.out.println("Notif Serah Terima Rujukan : " + e);
            } finally {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            }
        } catch (Exception e) {
            System.out.println("Notif Serah Terima Rujukan : " + e);
        }
    }

    public void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            try {
                TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()));
            } catch (Exception e) {}

            try {
                ps = koneksi.prepareStatement(
                    "select str.*, d.nm_dokter, pt.nama as nm_petugas from serah_terima_rujukan str " +
                    "left join dokter d on str.dokter_merawat=d.kd_dokter " +
                    "left join petugas pt on str.nip_menyerahkan=pt.nip " +
                    "where str.no_rawat=? and str.tanggal=?");
                ps.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                ps.setString(2, tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    THari.setText(rs.getString("hari"));
                    TRujukKe.setText(rs.getString("rujuk_ke"));
                    TPukul.setText(rs.getString("pukul"));
                    KdDokter.setText(rs.getString("dokter_merawat"));
                    NmDokter.setText(rs.getString("nm_dokter"));
                    TDiagnosa.setText(rs.getString("diagnosa"));
                    CmbAlasan.setSelectedItem(rs.getString("alasan_rujukan"));
                    TRSTujuan.setText(rs.getString("rs_tujuan"));
                    TPenanggungJawab.setText(rs.getString("penanggung_jawab_biaya"));
                    ChkSuratRujukan.setSelected(rs.getString("surat_rujukan").equals("Ya"));
                    ChkHasilLab.setSelected(rs.getString("hasil_lab").equals("Ya"));
                    ChkHasilRadiologi.setSelected(rs.getString("hasil_radiologi").equals("Ya"));
                    String lain = rs.getString("lain_lain");
                    if (lain != null && !lain.isEmpty()) {
                        ChkLainLain.setSelected(true);
                        TLainLain.setText(lain);
                    }
                    KdPetugas.setText(rs.getString("nip_menyerahkan"));
                    NmPetugas.setText(rs.getString("nm_petugas"));
                    NamaMenerima.setText(rs.getString("nama_menerima"));
                    NamaKeluarga.setText(rs.getString("nama_keluarga"));
                }
            } catch (Exception e) {
                System.out.println("Notif getData SerahTerima : " + e);
            } finally {
                try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {}
            }
        }
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date());
        THari.setText(""); TRujukKe.setText(""); TPukul.setText("");
        KdDokter.setText(""); NmDokter.setText(""); TDiagnosa.setText("");
        TRSTujuan.setText(""); TPenanggungJawab.setText("");
        ChkSuratRujukan.setSelected(false); ChkHasilLab.setSelected(false);
        ChkHasilRadiologi.setSelected(false); ChkLainLain.setSelected(false);
        TLainLain.setText(""); KdPetugas.setText(""); NmPetugas.setText("");
        NamaMenerima.setText(""); NamaKeluarga.setText("");
    }

    public void setNoRm(String norwt, String norm, String namapasien) {
        TNoRw.setText(norwt);
        TNoRM.setText(norm);
        TPasien.setText(namapasien);
    }

    public void setNoRm(String norwt, Date tgl) {
        TNoRw.setText(norwt);
        TglAsuhan.setDate(tgl);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", norwt));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
        TUmur.setText(Sequel.cariIsi("select concat(umurdaftar,' ',sttsumur) from reg_periksa where no_rawat=?", norwt));
        TTglLahir.setText(Sequel.cariIsi("select tgl_lahir from pasien where no_rkm_medis=?", TNoRM.getText()));
        TAlamat.setText(Sequel.cariIsi("select almt_pj from reg_periksa where no_rawat=?", norwt));
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getrujukan_keluar());
        BtnHapus.setEnabled(akses.getrujukan_keluar());
        BtnEdit.setEnabled(akses.getrujukan_keluar());
        BtnPrint.setEnabled(akses.getrujukan_keluar());
    }
}
