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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import kepegawaian.DlgCariPetugas;
import widget.*;

public final class RMCatatanObservasiRscb extends JDialog {

    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);

    private InternalFrame internalFrame1;
    private ScrollPane Scroll;
    private Table tbObat;
    private JPanel jPanel3, PanelInput;
    private panelisi panelGlass8, panelGlass9;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll, btnPetugas;
    private Label LCount;
    private Tanggal DTPCari1, DTPCari2, Tanggal;
    private TextBox TCari;
    private PanelBiasa FormInput;
    private CekBox ChkInput, ChkKejadian;
    private TextBox TNoRw, TNoRM, TNamaPasien, TTglLahir;
    private ComboBox Jam, Menit, Detik;
    private TextBox TNadi, TSuhu, TPernapasan, TTD, TGCS, TEWS, TSkorNyeri, TPivasSkor;
    private TextBox TIVPasang, TIVLepas, TNGTPasang, TNGTLepas;
    private TextBox TKateterPasang, TKateterLepas, TDrainPasang, TDrainLepas;
    private TextBox TSkorRisikoJatuh;
    private TextBox NIP, NamaPetugas;
    private JPopupMenu jPopupMenu1;
    private JMenuItem MnCetak;

    public RMCatatanObservasiRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(900, 700);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Obs", "Jam.Obs",
            "Nadi", "Suhu", "Pernapasan", "TD", "GCS", "EWS", "Skor Nyeri", "PIVAS",
            "IV Pasang", "IV Lepas", "NGT Pasang", "NGT Lepas",
            "Kttr Pasang", "Kttr Lepas", "Drain Pasang", "Drain Lepas",
            "Skor Risiko Jatuh", "NIP"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 300));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {105, 65, 170, 85, 65, 45, 45, 60, 70, 65, 40, 60, 50,
                        80, 80, 80, 80, 80, 80, 80, 80, 90, 80};
        for (i = 0; i < widths.length; i++)
            tbObat.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte)17).getKata(TNoRw));
        NIP.setDocument(new batasInput((byte)20).getKata(NIP));
        TGCS.setDocument(new batasInput((byte)10).getKata(TGCS));
        TTD.setDocument(new batasInput((byte)10).getKata(TTD));
        TNadi.setDocument(new batasInput((byte)5).getKata(TNadi));
        TSuhu.setDocument(new batasInput((byte)5).getKata(TSuhu));
        TPernapasan.setDocument(new batasInput((byte)5).getKata(TPernapasan));
        TEWS.setDocument(new batasInput((byte)5).getKata(TEWS));
        TSkorNyeri.setDocument(new batasInput((byte)5).getKata(TSkorNyeri));
        TPivasSkor.setDocument(new batasInput((byte)5).getKata(TPivasSkor));
        TSkorRisikoJatuh.setDocument(new batasInput((byte)5).getKata(TSkorRisikoJatuh));
        TCari.setDocument(new batasInput((int)100).getKata(TCari));

        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override public void insertUpdate(DocumentEvent e) { if (TCari.getText().length() > 2) tampil(); }
                @Override public void removeUpdate(DocumentEvent e) { if (TCari.getText().length() > 2) tampil(); }
                @Override public void changedUpdate(DocumentEvent e) { if (TCari.getText().length() > 2) tampil(); }
            });
        }

        petugas.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override public void windowClosed(WindowEvent e) {
                if (petugas.getTable().getSelectedRow() != -1) {
                    NIP.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                    NamaPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                }
                NIP.requestFocus();
            }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });

        buatTabelJikaPerlu();
        ChkInput.setSelected(false);
        isForm();
        jam();
    }

    private void buatTabelJikaPerlu() {
        try {
            koneksi.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS catatan_observasi_rscb (" +
                "no_rawat varchar(17) NOT NULL," +
                "tgl_observasi date NOT NULL," +
                "jam_observasi varchar(8) NOT NULL," +
                "nadi varchar(5)," +
                "suhu varchar(5)," +
                "pernapasan varchar(5)," +
                "tekanan_darah varchar(10)," +
                "gcs varchar(10)," +
                "ews varchar(5)," +
                "skor_nyeri varchar(5)," +
                "pivas_skor varchar(5)," +
                "iv_pasang varchar(12)," +
                "iv_lepas varchar(12)," +
                "ngt_pasang varchar(12)," +
                "ngt_lepas varchar(12)," +
                "kateter_pasang varchar(12)," +
                "kateter_lepas varchar(12)," +
                "drain_pasang varchar(12)," +
                "drain_lepas varchar(12)," +
                "skor_risiko_jatuh varchar(5)," +
                "nip varchar(20)," +
                "PRIMARY KEY (no_rawat,tgl_observasi,jam_observasi)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8"
            );
        } catch (Exception e) {
            System.out.println("buatTabelJikaPerlu: " + e);
        }
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        // Popup menu cetak
        jPopupMenu1 = new JPopupMenu();
        MnCetak = new JMenuItem("Cetak Formulir Catatan Observasi (RM.KEP.08.047.2020)");
        MnCetak.setBackground(new java.awt.Color(255, 255, 254));
        MnCetak.setFont(new java.awt.Font("Tahoma", 0, 11));
        MnCetak.setForeground(new java.awt.Color(50, 50, 50));
        java.net.URL iconCat = getClass().getResource("/picture/category.png");
        if (iconCat != null) MnCetak.setIcon(new ImageIcon(iconCat));
        MnCetak.setPreferredSize(new Dimension(330, 26));
        MnCetak.addActionListener(e -> MnCetakActionPerformed());
        jPopupMenu1.add(MnCetak);

        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
            "::[ Catatan Observasi ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12));
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        // Table
        Scroll = new ScrollPane();
        Scroll.setPreferredSize(new Dimension(600, 250));
        tbObat = new Table();
        tbObat.setToolTipText("Klik untuk memilih data yang mau diedit/dihapus");
        tbObat.setComponentPopupMenu(jPopupMenu1);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (tabMode.getRowCount() != 0) try { getData(); } catch (Exception ex) {}
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (tabMode.getRowCount() != 0 && (evt.getKeyCode() == KeyEvent.VK_ENTER ||
                    evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN))
                    try { getData(); } catch (Exception ex) {}
            }
        });
        Scroll.setViewportView(tbObat);
        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        // Button & search panel
        jPanel3 = new JPanel();
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8 = new panelisi();
        panelGlass8.setPreferredSize(new Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan = makeBtn("Simpan", 'S', "/picture/save-16x16.png", 100);
        BtnSimpan.addActionListener(e -> BtnSimpanActionPerformed());
        BtnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) BtnSimpanActionPerformed();
                else Valid.pindah(e, TSkorRisikoJatuh, BtnBatal);
            }
        });
        panelGlass8.add(BtnSimpan);

        BtnBatal = makeBtn("Baru", 'B', "/picture/Cancel-2-16x16.png", 100);
        BtnBatal.addActionListener(e -> { emptTeks(); ChkInput.setSelected(true); isForm(); });
        BtnBatal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) { emptTeks(); ChkInput.setSelected(true); isForm(); }
                else Valid.pindah(e, BtnSimpan, BtnHapus);
            }
        });
        panelGlass8.add(BtnBatal);

        BtnHapus = makeBtn("Hapus", 'H', "/picture/stop_f2.png", 100);
        BtnHapus.addActionListener(e -> {
            if (tbObat.getSelectedRow() > -1) {
                if (akses.getkode().equals("Admin Utama")) hapus();
                else if (NIP.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 22).toString())) hapus();
                else JOptionPane.showMessageDialog(null, "Hanya bisa dihapus oleh petugas yang bersangkutan..!!");
            } else JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data terlebih dahulu..!!");
        });
        BtnHapus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) BtnHapus.doClick();
                else Valid.pindah(e, BtnBatal, BtnEdit);
            }
        });
        panelGlass8.add(BtnHapus);

        BtnEdit = makeBtn("Ganti", 'G', "/picture/inventaris.png", 100);
        BtnEdit.addActionListener(e -> {
            if (TNoRw.getText().trim().isEmpty()) { Valid.textKosong(TNoRw, "pasien"); return; }
            if (NIP.getText().trim().isEmpty()) { Valid.textKosong(NIP, "Petugas"); return; }
            if (tbObat.getSelectedRow() > -1) {
                if (akses.getkode().equals("Admin Utama")) ganti();
                else if (NIP.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 22).toString())) ganti();
                else JOptionPane.showMessageDialog(null, "Hanya bisa diganti oleh petugas yang bersangkutan..!!");
            } else JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data terlebih dahulu..!!");
        });
        BtnEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) BtnEdit.doClick();
                else Valid.pindah(e, BtnHapus, BtnPrint);
            }
        });
        panelGlass8.add(BtnEdit);

        BtnPrint = makeBtn("Cetak", 'T', "/picture/b_print.png", 100);
        BtnPrint.addActionListener(e -> BtnPrintActionPerformed());
        BtnPrint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) BtnPrintActionPerformed();
                else Valid.pindah(e, BtnEdit, BtnKeluar);
            }
        });
        panelGlass8.add(BtnPrint);

        Label lblRec = new Label(); lblRec.setText("Record :"); lblRec.setPreferredSize(new Dimension(60, 23));
        panelGlass8.add(lblRec);
        LCount = new Label(); LCount.setText("0"); LCount.setHorizontalAlignment(SwingConstants.LEFT);
        LCount.setPreferredSize(new Dimension(50, 23));
        panelGlass8.add(LCount);

        BtnKeluar = makeBtn("Keluar", 'K', "/picture/exit.png", 100);
        BtnKeluar.addActionListener(e -> { petugas.dispose(); dispose(); });
        BtnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) { petugas.dispose(); dispose(); }
                else Valid.pindah(e, BtnPrint, TCari);
            }
        });
        panelGlass8.add(BtnKeluar);
        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        // Search bar
        panelGlass9 = new panelisi();
        panelGlass9.setPreferredSize(new Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        Label lblTgl = new Label(); lblTgl.setText("Tanggal :"); lblTgl.setPreferredSize(new Dimension(60, 23));
        panelGlass9.add(lblTgl);
        DTPCari1 = makeTanggal(); panelGlass9.add(DTPCari1);
        Label lblSd = new Label(); lblSd.setText("s.d."); lblSd.setPreferredSize(new Dimension(23, 23));
        panelGlass9.add(lblSd);
        DTPCari2 = makeTanggal(); panelGlass9.add(DTPCari2);

        Label lblCari = new Label(); lblCari.setText("Cari :"); lblCari.setPreferredSize(new Dimension(35, 23));
        panelGlass9.add(lblCari);
        TCari = new TextBox(); TCari.setPreferredSize(new Dimension(150, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) tampil(); }
        });
        panelGlass9.add(TCari);

        BtnCari = makeBtn("Cari", (char)0, "/picture/search.png", 80);
        BtnCari.addActionListener(e -> tampil());
        panelGlass9.add(BtnCari);
        BtnAll = makeBtn("Semua", (char)0, "/picture/list.png", 85);
        BtnAll.addActionListener(e -> { TCari.setText(""); tampil(); });
        panelGlass9.add(BtnAll);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.SOUTH);
        internalFrame1.add(jPanel3, java.awt.BorderLayout.SOUTH);

        // Input panel
        PanelInput = new JPanel();
        PanelInput.setOpaque(false);
        PanelInput.setLayout(new java.awt.BorderLayout());

        FormInput = new PanelBiasa();
        FormInput.setLayout(null);
        FormInput.setPreferredSize(new Dimension(890, 224));

        // Row 0 y=10: No.Rawat, No.RM, Nama Pasien, Tgl.Lahir
        lbl(FormInput, "No.Rawat :", 0, 10, 75);
        TNoRw = new TextBox(); TNoRw.setEditable(false); TNoRw.setHighlighter(null);
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) { isRawat(); isPasien(); }
                else Valid.pindah(e, TCari, Tanggal);
            }
        });
        set(FormInput, TNoRw, 78, 10, 130, 23);

        lbl(FormInput, "No.RM :", 212, 10, 52);
        TNoRM = new TextBox(); TNoRM.setEditable(false); TNoRM.setHighlighter(null);
        set(FormInput, TNoRM, 267, 10, 80, 23);

        lbl(FormInput, "Nama Pasien :", 350, 10, 90);
        TNamaPasien = new TextBox(); TNamaPasien.setEditable(false);
        set(FormInput, TNamaPasien, 443, 10, 230, 23);

        lbl(FormInput, "Tgl.Lahir :", 676, 10, 65);
        TTglLahir = new TextBox(); TTglLahir.setEditable(false); TTglLahir.setHighlighter(null);
        set(FormInput, TTglLahir, 744, 10, 100, 23);

        // Row 1 y=40: Tanggal, Jam, Menit, Detik, ChkKejadian, Petugas
        lbl(FormInput, "Tanggal :", 0, 40, 75);
        Tanggal = makeTanggal();
        Tanggal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, TCari, Jam); }
        });
        set(FormInput, Tanggal, 78, 40, 90, 23);

        lbl(FormInput, "Jam :", 172, 40, 35);
        Jam = new ComboBox(); Jam.setModel(new javax.swing.DefaultComboBoxModel(arr24()));
        Jam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, Tanggal, Menit); }
        });
        set(FormInput, Jam, 210, 40, 62, 23);

        Menit = new ComboBox(); Menit.setModel(new javax.swing.DefaultComboBoxModel(arr60()));
        Menit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, Jam, Detik); }
        });
        set(FormInput, Menit, 275, 40, 62, 23);

        Detik = new ComboBox(); Detik.setModel(new javax.swing.DefaultComboBoxModel(arr60()));
        Detik.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, Menit, btnPetugas); }
        });
        set(FormInput, Detik, 340, 40, 62, 23);

        ChkKejadian = new CekBox();
        ChkKejadian.setBorder(null); ChkKejadian.setSelected(true);
        ChkKejadian.setFont(new java.awt.Font("Tahoma", 1, 11));
        ChkKejadian.setHorizontalAlignment(SwingConstants.CENTER);
        ChkKejadian.setHorizontalTextPosition(SwingConstants.CENTER);
        set(FormInput, ChkKejadian, 405, 40, 23, 23);

        lbl(FormInput, "Petugas :", 430, 40, 60);
        NIP = new TextBox(); NIP.setEditable(false); NIP.setHighlighter(null);
        NIP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
                    Sequel.cariIsi("select petugas.nama from petugas where petugas.nip=?", NamaPetugas, NIP.getText());
                else if (e.getKeyCode() == KeyEvent.VK_UP) btnPetugas.doClick();
            }
        });
        set(FormInput, NIP, 493, 40, 90, 23);

        NamaPetugas = new TextBox(); NamaPetugas.setEditable(false);
        set(FormInput, NamaPetugas, 586, 40, 180, 23);

        btnPetugas = new Button();
        java.net.URL icon190 = getClass().getResource("/picture/190.png");
        if (icon190 != null) btnPetugas.setIcon(new ImageIcon(icon190));
        btnPetugas.setMnemonic('2'); btnPetugas.setToolTipText("Alt+2");
        btnPetugas.addActionListener(e -> {
            petugas.emptTeks(); petugas.isCek();
            petugas.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20);
            petugas.setLocationRelativeTo(internalFrame1); petugas.setVisible(true);
        });
        btnPetugas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, Detik, TNadi); }
        });
        set(FormInput, btnPetugas, 769, 40, 28, 23);

        // Row 2 y=70: Nadi, Suhu, Pernapasan, T.Darah
        lbl(FormInput, "Nadi :", 0, 70, 45);
        TNadi = new TextBox();
        TNadi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, btnPetugas, TSuhu); }
        });
        set(FormInput, TNadi, 48, 70, 50, 23);
        lbl(FormInput, "x/mnt", 101, 70, 40);

        lbl(FormInput, "Suhu :", 145, 70, 42);
        TSuhu = new TextBox();
        TSuhu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, TNadi, TPernapasan); }
        });
        set(FormInput, TSuhu, 190, 70, 50, 23);
        lbl(FormInput, "\u00B0C", 243, 70, 20);

        lbl(FormInput, "Pernapasan :", 267, 70, 82);
        TPernapasan = new TextBox();
        TPernapasan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, TSuhu, TTD); }
        });
        set(FormInput, TPernapasan, 352, 70, 50, 23);
        lbl(FormInput, "x/mnt", 405, 70, 40);

        lbl(FormInput, "T.Darah :", 449, 70, 62);
        TTD = new TextBox();
        TTD.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, TPernapasan, TGCS); }
        });
        set(FormInput, TTD, 514, 70, 70, 23);
        lbl(FormInput, "mmHg", 587, 70, 45);

        // Row 3 y=100: GCS, EWS, Skor Nyeri, PIVAS Skor
        lbl(FormInput, "GCS (E,M,V) :", 0, 100, 90);
        TGCS = new TextBox();
        TGCS.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, TTD, TEWS); }
        });
        set(FormInput, TGCS, 93, 100, 60, 23);

        lbl(FormInput, "EWS :", 160, 100, 40);
        TEWS = new TextBox();
        TEWS.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, TGCS, TSkorNyeri); }
        });
        set(FormInput, TEWS, 203, 100, 50, 23);

        lbl(FormInput, "Skor Nyeri :", 258, 100, 75);
        TSkorNyeri = new TextBox();
        TSkorNyeri.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, TEWS, TPivasSkor); }
        });
        set(FormInput, TSkorNyeri, 336, 100, 50, 23);

        lbl(FormInput, "PIVAS Skor :", 390, 100, 80);
        TPivasSkor = new TextBox();
        TPivasSkor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, TSkorNyeri, TIVPasang); }
        });
        set(FormInput, TPivasSkor, 473, 100, 50, 23);

        // Row 4 y=130: IV Pasang/Lepas, NGT Pasang/Lepas
        lbl(FormInput, "IV Pasang :", 0, 130, 70);
        TIVPasang = new TextBox();
        TIVPasang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, TPivasSkor, TIVLepas); }
        });
        set(FormInput, TIVPasang, 73, 130, 90, 23);

        lbl(FormInput, "Lepas :", 166, 130, 48);
        TIVLepas = new TextBox();
        TIVLepas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, TIVPasang, TNGTPasang); }
        });
        set(FormInput, TIVLepas, 217, 130, 90, 23);

        lbl(FormInput, "NGT Pasang :", 315, 130, 85);
        TNGTPasang = new TextBox();
        TNGTPasang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, TIVLepas, TNGTLepas); }
        });
        set(FormInput, TNGTPasang, 403, 130, 90, 23);

        lbl(FormInput, "Lepas :", 496, 130, 48);
        TNGTLepas = new TextBox();
        TNGTLepas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, TNGTPasang, TKateterPasang); }
        });
        set(FormInput, TNGTLepas, 547, 130, 90, 23);

        // Row 5 y=160: Kateter Pasang/Lepas, Drain Pasang/Lepas
        lbl(FormInput, "Kateter Pasang :", 0, 160, 105);
        TKateterPasang = new TextBox();
        TKateterPasang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, TNGTLepas, TKateterLepas); }
        });
        set(FormInput, TKateterPasang, 108, 160, 90, 23);

        lbl(FormInput, "Lepas :", 201, 160, 48);
        TKateterLepas = new TextBox();
        TKateterLepas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, TKateterPasang, TDrainPasang); }
        });
        set(FormInput, TKateterLepas, 252, 160, 90, 23);

        lbl(FormInput, "Drain Pasang :", 348, 160, 90);
        TDrainPasang = new TextBox();
        TDrainPasang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, TKateterLepas, TDrainLepas); }
        });
        set(FormInput, TDrainPasang, 441, 160, 90, 23);

        lbl(FormInput, "Lepas :", 534, 160, 48);
        TDrainLepas = new TextBox();
        TDrainLepas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, TDrainPasang, TSkorRisikoJatuh); }
        });
        set(FormInput, TDrainLepas, 585, 160, 90, 23);

        // Row 6 y=192: Skor Risiko Jatuh
        lbl(FormInput, "Skor Risiko Jatuh :", 0, 192, 120);
        TSkorRisikoJatuh = new TextBox();
        TSkorRisikoJatuh.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) { Valid.pindah(e, TDrainLepas, BtnSimpan); }
        });
        set(FormInput, TSkorRisikoJatuh, 123, 192, 60, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        // ChkInput toggle
        ChkInput = new CekBox();
        java.net.URL icon143 = getClass().getResource("/picture/143.png");
        java.net.URL icon145 = getClass().getResource("/picture/145.png");
        if (icon143 != null) { ChkInput.setIcon(new ImageIcon(icon143)); ChkInput.setRolloverIcon(new ImageIcon(icon143)); }
        if (icon145 != null) { ChkInput.setRolloverSelectedIcon(new ImageIcon(icon145)); ChkInput.setSelectedIcon(new ImageIcon(icon145)); }
        ChkInput.setMnemonic('I'); ChkInput.setText(".: Input Data"); ChkInput.setToolTipText("Alt+I");
        ChkInput.setBorderPainted(true); ChkInput.setBorderPaintedFlat(true); ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(SwingConstants.LEFT); ChkInput.setHorizontalTextPosition(SwingConstants.RIGHT);
        ChkInput.setPreferredSize(new Dimension(192, 20));
        ChkInput.addActionListener(e -> isForm());
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
        pack();
    }

    // ── helpers ────────────────────────────────────────────────────────────────
    private void lbl(JPanel p, String txt, int x, int y, int w) {
        Label l = new Label(); l.setText(txt); p.add(l); l.setBounds(x, y, w, 23);
    }
    private void set(JPanel p, JComponent c, int x, int y, int w, int h) {
        p.add(c); c.setBounds(x, y, w, h);
    }
    private Button makeBtn(String text, char mnemonic, String iconPath, int width) {
        Button b = new Button();
        java.net.URL url = getClass().getResource(iconPath);
        if (url != null) b.setIcon(new ImageIcon(url));
        b.setText(text);
        if (mnemonic != 0) b.setMnemonic(mnemonic);
        b.setPreferredSize(new Dimension(width, 30));
        return b;
    }
    private Tanggal makeTanggal() {
        Tanggal t = new Tanggal();
        t.setForeground(new java.awt.Color(50, 70, 50));
        t.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"24-05-2022"}));
        t.setDisplayFormat("dd-MM-yyyy");
        t.setPreferredSize(new Dimension(95, 23));
        return t;
    }
    private String[] arr24() {
        String[] a = new String[24]; for (int j=0;j<24;j++) a[j]=String.format("%02d",j); return a;
    }
    private String[] arr60() {
        String[] a = new String[60]; for (int j=0;j<60;j++) a[j]=String.format("%02d",j); return a;
    }

    // ── business logic ─────────────────────────────────────────────────────────
    private void BtnSimpanActionPerformed() {
        if (TNoRw.getText().trim().isEmpty()) { Valid.textKosong(TNoRw, "pasien"); return; }
        if (NIP.getText().trim().isEmpty()) { Valid.textKosong(NIP, "Petugas"); return; }
        if (Sequel.menyimpantf("catatan_observasi_rscb",
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 21, new String[]{
                TNoRw.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + ""),
                Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                TNadi.getText(), TSuhu.getText(), TPernapasan.getText(), TTD.getText(),
                TGCS.getText(), TEWS.getText(), TSkorNyeri.getText(), TPivasSkor.getText(),
                TIVPasang.getText(), TIVLepas.getText(), TNGTPasang.getText(), TNGTLepas.getText(),
                TKateterPasang.getText(), TKateterLepas.getText(),
                TDrainPasang.getText(), TDrainLepas.getText(),
                TSkorRisikoJatuh.getText(), NIP.getText()
            }) == true) {
            tampil(); emptTeks();
        }
    }

    private void ganti() {
        Sequel.mengedit("catatan_observasi_rscb",
            "tgl_observasi=? and jam_observasi=? and no_rawat=?",
            "no_rawat=?,tgl_observasi=?,jam_observasi=?,nadi=?,suhu=?,pernapasan=?,tekanan_darah=?," +
            "gcs=?,ews=?,skor_nyeri=?,pivas_skor=?,iv_pasang=?,iv_lepas=?,ngt_pasang=?,ngt_lepas=?," +
            "kateter_pasang=?,kateter_lepas=?,drain_pasang=?,drain_lepas=?,skor_risiko_jatuh=?,nip=?",
            24, new String[]{
                TNoRw.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + ""),
                Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                TNadi.getText(), TSuhu.getText(), TPernapasan.getText(), TTD.getText(),
                TGCS.getText(), TEWS.getText(), TSkorNyeri.getText(), TPivasSkor.getText(),
                TIVPasang.getText(), TIVLepas.getText(), TNGTPasang.getText(), TNGTLepas.getText(),
                TKateterPasang.getText(), TKateterLepas.getText(),
                TDrainPasang.getText(), TDrainLepas.getText(),
                TSkorRisikoJatuh.getText(), NIP.getText(),
                tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString(),
                tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString(),
                tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
            });
        if (tabMode.getRowCount() != 0) tampil();
        emptTeks();
    }

    private void hapus() {
        if (Sequel.queryu2tf("delete from catatan_observasi_rscb where tgl_observasi=? and jam_observasi=? and no_rawat=?",
            3, new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString(),
                tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString(),
                tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
            }) == true) {
            tampil(); emptTeks();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
        }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            String base = "select r.no_rawat,p.no_rkm_medis,p.nm_pasien,c.tgl_observasi,c.jam_observasi," +
                "c.nadi,c.suhu,c.pernapasan,c.tekanan_darah,c.gcs,c.ews,c.skor_nyeri,c.pivas_skor," +
                "c.iv_pasang,c.iv_lepas,c.ngt_pasang,c.ngt_lepas,c.kateter_pasang,c.kateter_lepas," +
                "c.drain_pasang,c.drain_lepas,c.skor_risiko_jatuh,c.nip " +
                "from catatan_observasi_rscb c " +
                "inner join reg_periksa r on c.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                "where c.tgl_observasi between ? and ?";
            if (TCari.getText().trim().isEmpty()) {
                ps = koneksi.prepareStatement(base + " order by c.tgl_observasi,c.jam_observasi");
                ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem()+"") + " 00:00:00");
                ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem()+"") + " 23:59:59");
            } else {
                ps = koneksi.prepareStatement(base +
                    " and (r.no_rawat like ? or p.no_rkm_medis like ? or p.nm_pasien like ?)" +
                    " order by c.tgl_observasi,c.jam_observasi");
                ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem()+"") + " 00:00:00");
                ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem()+"") + " 23:59:59");
                ps.setString(3, "%" + TCari.getText() + "%");
                ps.setString(4, "%" + TCari.getText() + "%");
                ps.setString(5, "%" + TCari.getText() + "%");
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                    rs.getString("tgl_observasi"), rs.getString("jam_observasi"),
                    rs.getString("nadi"), rs.getString("suhu"), rs.getString("pernapasan"),
                    rs.getString("tekanan_darah"), rs.getString("gcs"), rs.getString("ews"),
                    rs.getString("skor_nyeri"), rs.getString("pivas_skor"),
                    rs.getString("iv_pasang"), rs.getString("iv_lepas"),
                    rs.getString("ngt_pasang"), rs.getString("ngt_lepas"),
                    rs.getString("kateter_pasang"), rs.getString("kateter_lepas"),
                    rs.getString("drain_pasang"), rs.getString("drain_lepas"),
                    rs.getString("skor_risiko_jatuh"), rs.getString("nip")
                });
            }
        } catch (Exception e) {
            System.out.println("tampil: " + e);
        } finally {
            try { if (rs!=null) rs.close(); if (ps!=null) ps.close(); } catch (Exception ex) {}
        }
        LCount.setText("" + tabMode.getRowCount());
    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            int r = tbObat.getSelectedRow();
            TNoRw.setText(tbObat.getValueAt(r, 0).toString());
            TNoRM.setText(tbObat.getValueAt(r, 1).toString());
            TNamaPasien.setText(tbObat.getValueAt(r, 2).toString());
            Valid.SetTgl(Tanggal, tbObat.getValueAt(r, 3).toString());
            String jamStr = tbObat.getValueAt(r, 4).toString();
            if (jamStr.length() >= 8) {
                Jam.setSelectedItem(jamStr.substring(0,2));
                Menit.setSelectedItem(jamStr.substring(3,5));
                Detik.setSelectedItem(jamStr.substring(6,8));
            }
            TNadi.setText(tbObat.getValueAt(r, 5).toString());
            TSuhu.setText(tbObat.getValueAt(r, 6).toString());
            TPernapasan.setText(tbObat.getValueAt(r, 7).toString());
            TTD.setText(tbObat.getValueAt(r, 8).toString());
            TGCS.setText(tbObat.getValueAt(r, 9).toString());
            TEWS.setText(tbObat.getValueAt(r, 10).toString());
            TSkorNyeri.setText(tbObat.getValueAt(r, 11).toString());
            TPivasSkor.setText(tbObat.getValueAt(r, 12).toString());
            TIVPasang.setText(tbObat.getValueAt(r, 13).toString());
            TIVLepas.setText(tbObat.getValueAt(r, 14).toString());
            TNGTPasang.setText(tbObat.getValueAt(r, 15).toString());
            TNGTLepas.setText(tbObat.getValueAt(r, 16).toString());
            TKateterPasang.setText(tbObat.getValueAt(r, 17).toString());
            TKateterLepas.setText(tbObat.getValueAt(r, 18).toString());
            TDrainPasang.setText(tbObat.getValueAt(r, 19).toString());
            TDrainLepas.setText(tbObat.getValueAt(r, 20).toString());
            TSkorRisikoJatuh.setText(tbObat.getValueAt(r, 21).toString());
            NIP.setText(tbObat.getValueAt(r, 22).toString());
            Sequel.cariIsi("select nama from petugas where nip=?", NamaPetugas, NIP.getText());
            Sequel.cariIsi("select DATE_FORMAT(p.tgl_lahir,'%d-%m-%Y') from pasien p " +
                "inner join reg_periksa rp on p.no_rkm_medis=rp.no_rkm_medis where rp.no_rawat=?",
                TTglLahir, TNoRw.getText());
        }
    }

    public void emptTeks() {
        TNadi.setText(""); TSuhu.setText(""); TPernapasan.setText(""); TTD.setText("");
        TGCS.setText(""); TEWS.setText(""); TSkorNyeri.setText(""); TPivasSkor.setText("");
        TIVPasang.setText(""); TIVLepas.setText(""); TNGTPasang.setText(""); TNGTLepas.setText("");
        TKateterPasang.setText(""); TKateterLepas.setText("");
        TDrainPasang.setText(""); TDrainLepas.setText("");
        TSkorRisikoJatuh.setText("");
        Tanggal.setDate(new Date());
        TNadi.requestFocus();
    }

    private void isRawat() {
        Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat='" + TNoRw.getText() + "'", TNoRM);
    }
    private void isPasien() {
        Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis='" + TNoRM.getText() + "'", TNamaPasien);
        Sequel.cariIsi("select DATE_FORMAT(tgl_lahir,'%d-%m-%Y') from pasien where no_rkm_medis='" + TNoRM.getText() + "'", TTglLahir);
    }

    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        Sequel.cariIsi("select tgl_registrasi from reg_periksa where no_rawat='" + norwt + "'", DTPCari1);
        DTPCari2.setDate(tgl2);
        isRawat(); isPasien();
        ChkInput.setSelected(true);
        isForm();
    }

    private void isForm() {
        if (ChkInput.isSelected()) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 248));
            FormInput.setVisible(true);
            ChkInput.setVisible(true);
        } else {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 20));
            FormInput.setVisible(false);
            ChkInput.setVisible(true);
        }
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getcatatan_observasi_igd());
        BtnHapus.setEnabled(akses.getcatatan_observasi_igd());
        BtnEdit.setEnabled(akses.getcatatan_observasi_igd());
        BtnPrint.setEnabled(akses.getcatatan_observasi_igd());
        if (akses.getjml2() >= 1) {
            NIP.setEditable(false); btnPetugas.setEnabled(false);
            NIP.setText(akses.getkode());
            Sequel.cariIsi("select nama from petugas where nip=?", NamaPetugas, NIP.getText());
            if (NamaPetugas.getText().isEmpty()) {
                NIP.setText("");
                JOptionPane.showMessageDialog(null, "User login bukan petugas...!!");
            }
        }
    }

    private void BtnPrintActionPerformed() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, tidak ada data yang bisa dicetak...!!");
        } else if (!TNoRw.getText().trim().isEmpty()) {
            doCetak(TNoRw.getText());
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void MnCetakActionPerformed() {
        if (tbObat.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(null, "Silahkan pilih data terlebih dahulu.");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        doCetak(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void doCetak(String noRawat) {
        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kabupatenrs", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select logo from setting"));
        param.put("logo2", Sequel.cariGambar("select logo2 from setting"));
        param.put("no_rm", TNoRM.getText());
        param.put("nm_pasien", TNamaPasien.getText());
        param.put("tgl_lahir", TTglLahir.getText());
        param.put("ruangan", Sequel.cariIsi("select kd_kamar from kamar_inap where no_rawat=? order by tgl_masuk desc limit 1", noRawat));
        Valid.MyReportqry("rptCatatanObservasiRscb.jasper", "report",
            "::[ Catatan Observasi ]::",
            "select c.*,p.nm_pasien,p.no_rkm_medis,p.tgl_lahir,pt.nama as nm_petugas " +
            "from catatan_observasi_rscb c " +
            "inner join reg_periksa r on c.no_rawat=r.no_rawat " +
            "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
            "left join petugas pt on c.nip=pt.nip " +
            "where c.no_rawat='" + noRawat + "' " +
            "order by c.tgl_observasi,c.jam_observasi",
            param);
    }

    private void jam() {
        ActionListener task = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (ChkKejadian.isSelected()) {
                    Date now = Calendar.getInstance().getTime();
                    Jam.setSelectedItem(String.format("%02d", now.getHours()));
                    Menit.setSelectedItem(String.format("%02d", now.getMinutes()));
                    Detik.setSelectedItem(String.format("%02d", now.getSeconds()));
                }
            }
        };
        new Timer(1000, task).start();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMCatatanObservasiRscb dialog = new RMCatatanObservasiRscb(new JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosing(java.awt.event.WindowEvent e) { System.exit(0); }
            });
            dialog.setVisible(true);
        });
    }
}
