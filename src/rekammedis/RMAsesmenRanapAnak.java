package rekammedis;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import kepegawaian.DlgCariPetugas;
import widget.*;

public final class RMAsesmenRanapAnak extends JDialog {

    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);

    // outer frame
    private InternalFrame internalFrame1;
    private JTabbedPane TabRawat;

    // Tab 1 – Input
    private InternalFrame internalFrame2;
    private javax.swing.JScrollPane scrollInput;
    private PanelBiasa FormInput;

    // Tab 2 – Data
    private InternalFrame internalFrame3;
    private javax.swing.JScrollPane Scroll;
    private Table tbObat;
    private panelisi panelGlass9;
    private Tanggal DTPCari1, DTPCari2;
    private TextBox TCari;
    private Button BtnCari, BtnAll;
    private Label LCount;

    // buttons toolbar
    private panelisi panelGlass8;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, btnPetugas;

    // header fields (auto-fill from no_rawat)
    private TextBox TNoRw, TNoRM, TNamaPasien, TTglLahir, TTglMasuk;

    // section fields
    private TextBox TAgama, TDxMedis;
    private ComboBox CInformasiDari;
    private TextBox TCaraMasuk, TAsalMasuk;
    private TextArea TAlergi, TKeluhanUtama, TRiwayatKesehatan, TImunisasiTumbuh;
    private TextBox TTD, TNadi, TSuhu, TPernapasan, TTB, TBB, TTingkatKesadaran, TGCS;
    private TextArea TPernafasanDetail, TKardio, TGI, TPerkemihan, TNeurosensori, TEkstremitas, TKulit;
    private TextBox TIstirahaTidur;
    private TextArea TPsikososial;
    private TextBox TSkriningNyeri, TBarthelSkor, THumptySkor, TGiziSkor;
    private TextArea TImunocompromise, TDataPenunjang, TMasalahKeperawatan;
    private TextBox NIP, NamaPetugas;

    // popup (right-click on table)
    private JPopupMenu jPopupMenu1;
    private JMenuItem MnCetak;

    public RMAsesmenRanapAnak(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(900, 720);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Asesmen",
            "Cara Masuk", "Keluhan Utama", "TD", "Nadi", "Suhu",
            "GCS", "Humpty", "Gizi", "NIP"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 300));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] ww = {105, 65, 170, 85, 90, 200, 65, 45, 45, 65, 50, 40, 80};
        for (i = 0; i < ww.length; i++) tbObat.getColumnModel().getColumn(i).setPreferredWidth(ww[i]);
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte)17).getKata(TNoRw));
        NIP.setDocument(new batasInput((byte)20).getKata(NIP));
        TGCS.setDocument(new batasInput((byte)10).getKata(TGCS));
        TTD.setDocument(new batasInput((byte)10).getKata(TTD));
        TNadi.setDocument(new batasInput((byte)5).getKata(TNadi));
        TSuhu.setDocument(new batasInput((byte)5).getKata(TSuhu));
        TPernapasan.setDocument(new batasInput((byte)5).getKata(TPernapasan));
        TTB.setDocument(new batasInput((byte)5).getKata(TTB));
        TBB.setDocument(new batasInput((byte)5).getKata(TBB));
        TSkriningNyeri.setDocument(new batasInput((byte)5).getKata(TSkriningNyeri));
        TBarthelSkor.setDocument(new batasInput((byte)3).getKata(TBarthelSkor));
        THumptySkor.setDocument(new batasInput((byte)3).getKata(THumptySkor));
        TGiziSkor.setDocument(new batasInput((byte)3).getKata(TGiziSkor));
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
    }

    private void buatTabelJikaPerlu() {
        try {
            koneksi.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS asesmen_ranap_anak (" +
                "no_rawat varchar(17) NOT NULL," +
                "tgl_asesmen date," +
                "agama varchar(30)," +
                "dx_medis varchar(100)," +
                "informasi_dari varchar(30)," +
                "cara_masuk varchar(60)," +
                "asal_masuk varchar(60)," +
                "alergi text," +
                "keluhan_utama text," +
                "riwayat_kesehatan text," +
                "imunisasi_tumbuh_kembang text," +
                "td varchar(10)," +
                "nadi varchar(5)," +
                "suhu varchar(5)," +
                "pernapasan varchar(5)," +
                "tb varchar(5)," +
                "bb varchar(5)," +
                "tingkat_kesadaran varchar(40)," +
                "gcs varchar(10)," +
                "pernafasan_detail text," +
                "kardio text," +
                "gi text," +
                "perkemihan text," +
                "neurosensori text," +
                "ekstremitas text," +
                "kulit text," +
                "istirahat_tidur varchar(60)," +
                "psikososial text," +
                "skrining_nyeri_skor varchar(5)," +
                "barthel_skor varchar(5)," +
                "humpty_skor varchar(5)," +
                "gizi_skor varchar(5)," +
                "imunocompromise text," +
                "data_penunjang text," +
                "masalah_keperawatan text," +
                "nip varchar(20)," +
                "PRIMARY KEY (no_rawat)" +
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

        // Popup for table right-click
        jPopupMenu1 = new JPopupMenu();
        MnCetak = new JMenuItem("Cetak Formulir Asesmen Awal Keperawatan Rawat Inap Anak (RM.KEP.08.021)");
        MnCetak.setBackground(new Color(255, 255, 254));
        MnCetak.setFont(new Font("Tahoma", 0, 11));
        MnCetak.setForeground(new Color(50, 50, 50));
        java.net.URL iconCat = getClass().getResource("/picture/category.png");
        if (iconCat != null) MnCetak.setIcon(new ImageIcon(iconCat));
        MnCetak.setPreferredSize(new Dimension(430, 26));
        MnCetak.addActionListener(e -> MnCetakActionPerformed());
        jPopupMenu1.add(MnCetak);

        // Outer frame
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Asesmen Awal Keperawatan Rawat Inap Anak ]::",
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        // ── Button toolbar ────────────────────────────────────────────────────
        panelGlass8 = new panelisi();
        panelGlass8.setPreferredSize(new Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan = makeBtn("Simpan", 'S', "/picture/save-16x16.png", 100);
        BtnSimpan.addActionListener(e -> BtnSimpanActionPerformed());
        BtnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) BtnSimpanActionPerformed();
                else Valid.pindah(e, TMasalahKeperawatan, BtnBatal);
            }
        });
        panelGlass8.add(BtnSimpan);

        BtnBatal = makeBtn("Baru", 'B', "/picture/Cancel-2-16x16.png", 100);
        BtnBatal.addActionListener(e -> emptTeks());
        BtnBatal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) emptTeks();
                else Valid.pindah(e, BtnSimpan, BtnHapus);
            }
        });
        panelGlass8.add(BtnBatal);

        BtnHapus = makeBtn("Hapus", 'H', "/picture/stop_f2.png", 100);
        BtnHapus.addActionListener(e -> {
            if (tbObat.getSelectedRow() > -1) {
                if (akses.getkode().equals("Admin Utama")) hapus();
                else if (NIP.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString())) hapus();
                else JOptionPane.showMessageDialog(null, "Hanya bisa dihapus oleh petugas yang bersangkutan..!!");
            } else JOptionPane.showMessageDialog(rootPane, "Pilih data terlebih dahulu..!!");
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
                else if (NIP.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString())) ganti();
                else JOptionPane.showMessageDialog(null, "Hanya bisa diganti oleh petugas yang bersangkutan..!!");
            } else JOptionPane.showMessageDialog(rootPane, "Pilih data terlebih dahulu..!!");
        });
        BtnEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) BtnEdit.doClick();
                else Valid.pindah(e, BtnHapus, BtnPrint);
            }
        });
        panelGlass8.add(BtnEdit);

        BtnPrint = makeBtn("Cetak", 'T', "/picture/b_print.png", 100);
        BtnPrint.addActionListener(e -> {
            if (tabMode.getRowCount() == 0) JOptionPane.showMessageDialog(null, "Tidak ada data...!!");
            else if (!TNoRw.getText().trim().isEmpty()) doCetak(TNoRw.getText());
        });
        BtnPrint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) BtnPrint.doClick();
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

        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        // ── Tabbed Pane ───────────────────────────────────────────────────────
        TabRawat = new JTabbedPane();
        TabRawat.setFont(new Font("Tahoma", Font.BOLD, 11));

        // ── Tab 1: Input ──────────────────────────────────────────────────────
        internalFrame2 = new InternalFrame();
        internalFrame2.setBorder(null);
        internalFrame2.setLayout(new java.awt.BorderLayout(1, 1));

        scrollInput = new javax.swing.JScrollPane();

        FormInput = new PanelBiasa();
        FormInput.setLayout(null);
        FormInput.setPreferredSize(new Dimension(870, 1560));

        int y = 5;

        // ── Row: No.Rawat, No.RM, Nama Pasien, Tgl.Lahir ─────────────────────
        lbl(FormInput, "No.Rawat :", 0, y, 75);
        TNoRw = new TextBox(); TNoRw.setEditable(false); TNoRw.setHighlighter(null);
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) { isRawat(); isPasien(); }
                else Valid.pindah(e, TCari, TAgama);
            }
        });
        set(FormInput, TNoRw, 78, y, 130, 23);
        lbl(FormInput, "No.RM :", 212, y, 52);
        TNoRM = new TextBox(); TNoRM.setEditable(false); TNoRM.setHighlighter(null);
        set(FormInput, TNoRM, 267, y, 80, 23);
        lbl(FormInput, "Nama Pasien :", 350, y, 90);
        TNamaPasien = new TextBox(); TNamaPasien.setEditable(false);
        set(FormInput, TNamaPasien, 443, y, 215, 23);
        lbl(FormInput, "Tgl.Lahir :", 661, y, 65);
        TTglLahir = new TextBox(); TTglLahir.setEditable(false); TTglLahir.setHighlighter(null);
        set(FormInput, TTglLahir, 729, y, 100, 23);

        y += 30;
        lbl(FormInput, "Tgl.Masuk RS :", 0, y, 90);
        TTglMasuk = new TextBox(); TTglMasuk.setEditable(false); TTglMasuk.setHighlighter(null);
        set(FormInput, TTglMasuk, 93, y, 100, 23);
        lbl(FormInput, "Agama :", 200, y, 50);
        TAgama = new TextBox(); TAgama.addKeyListener(kl(TNoRw, TDxMedis));
        set(FormInput, TAgama, 253, y, 100, 23);
        lbl(FormInput, "Dx. Medis :", 357, y, 70);
        TDxMedis = new TextBox(); TDxMedis.addKeyListener(kl(TAgama, CInformasiDari));
        set(FormInput, TDxMedis, 430, y, 300, 23);

        y += 30;
        lbl(FormInput, "Informasi dari :", 0, y, 95);
        CInformasiDari = new ComboBox();
        CInformasiDari.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "Auto-Anamnesa", "Allo-Anamnesa", "Auto & Allo-Anamnesa"}));
        set(FormInput, CInformasiDari, 98, y, 170, 23);
        lbl(FormInput, "Cara Masuk :", 275, y, 80);
        TCaraMasuk = new TextBox();
        TCaraMasuk.addKeyListener(kl(CInformasiDari, TAsalMasuk));
        set(FormInput, TCaraMasuk, 358, y, 200, 23);
        lbl(FormInput, "Asal Masuk :", 562, y, 80);
        TAsalMasuk = new TextBox();
        TAsalMasuk.addKeyListener(kl(TCaraMasuk, TAlergi));
        set(FormInput, TAsalMasuk, 645, y, 190, 23);

        y += 28;
        y = addSection(FormInput, "1. RIWAYAT ALERGI / REAKSI", y);
        lbl(FormInput, "Alergi :", 0, y, 55);
        TAlergi = makeArea(4);
        TAlergi.addKeyListener(kl(TAsalMasuk, TKeluhanUtama));
        set(FormInput, new javax.swing.JScrollPane(TAlergi), 58, y, 780, 65);

        y += 73;
        y = addSection(FormInput, "2. ALASAN MASUK RS - Keluhan Utama", y);
        TKeluhanUtama = makeArea(4);
        set(FormInput, new javax.swing.JScrollPane(TKeluhanUtama), 0, y, 838, 65);

        y += 73;
        y = addSection(FormInput, "3. RIWAYAT KESEHATAN, IMUNISASI & TUMBUH KEMBANG", y);
        TRiwayatKesehatan = makeArea(4);
        set(FormInput, new javax.swing.JScrollPane(TRiwayatKesehatan), 0, y, 838, 65);
        y += 73;
        lbl(FormInput, "Imunisasi & Tumbuh Kembang :", 0, y, 200);
        TImunisasiTumbuh = makeArea(3);
        set(FormInput, new javax.swing.JScrollPane(TImunisasiTumbuh), 203, y, 635, 50);

        y += 58;
        y = addSection(FormInput, "4. PEMERIKSAAN FISIK", y);

        // Vital signs
        lbl(FormInput, "a. Tanda Vital", 0, y, 100);
        lbl(FormInput, "TD :", 103, y, 30);
        TTD = new TextBox(); TTD.addKeyListener(kl(TAsalMasuk, TNadi));
        set(FormInput, TTD, 136, y, 65, 23);
        lbl(FormInput, "mmHg", 204, y, 40);
        lbl(FormInput, "Nadi :", 248, y, 38);
        TNadi = new TextBox(); TNadi.addKeyListener(kl(TTD, TSuhu));
        set(FormInput, TNadi, 289, y, 45, 23);
        lbl(FormInput, "x/mnt", 337, y, 40);
        lbl(FormInput, "Suhu :", 381, y, 40);
        TSuhu = new TextBox(); TSuhu.addKeyListener(kl(TNadi, TPernapasan));
        set(FormInput, TSuhu, 424, y, 45, 23);
        lbl(FormInput, "\u00B0C", 472, y, 20);
        lbl(FormInput, "Pernapasan :", 495, y, 82);
        TPernapasan = new TextBox(); TPernapasan.addKeyListener(kl(TSuhu, TTB));
        set(FormInput, TPernapasan, 580, y, 45, 23);
        lbl(FormInput, "x/mnt", 628, y, 40);

        y += 28;
        lbl(FormInput, "TB :", 103, y, 30);
        TTB = new TextBox(); TTB.addKeyListener(kl(TPernapasan, TBB));
        set(FormInput, TTB, 136, y, 45, 23);
        lbl(FormInput, "cm", 184, y, 25);
        lbl(FormInput, "BB :", 213, y, 30);
        TBB = new TextBox(); TBB.addKeyListener(kl(TTB, TTingkatKesadaran));
        set(FormInput, TBB, 246, y, 45, 23);
        lbl(FormInput, "kg", 294, y, 25);
        lbl(FormInput, "Tingkat Kesadaran :", 322, y, 125);
        TTingkatKesadaran = new TextBox(); TTingkatKesadaran.addKeyListener(kl(TBB, TGCS));
        set(FormInput, TTingkatKesadaran, 450, y, 150, 23);
        lbl(FormInput, "GCS (E,M,V) :", 603, y, 88);
        TGCS = new TextBox(); TGCS.addKeyListener(kl(TTingkatKesadaran, TPernafasanDetail));
        set(FormInput, TGCS, 694, y, 80, 23);

        y += 30;
        lbl(FormInput, "b. Fungsi Pernafasan :", 0, y, 140);
        TPernafasanDetail = makeArea(3);
        set(FormInput, new javax.swing.JScrollPane(TPernafasanDetail), 143, y, 695, 50);

        y += 58;
        lbl(FormInput, "c. Status Kardiovaskuler :", 0, y, 155);
        TKardio = makeArea(3);
        set(FormInput, new javax.swing.JScrollPane(TKardio), 158, y, 680, 50);

        y += 58;
        lbl(FormInput, "d. Fungsi Gastrointestinal :", 0, y, 170);
        TGI = makeArea(3);
        set(FormInput, new javax.swing.JScrollPane(TGI), 173, y, 665, 50);

        y += 58;
        lbl(FormInput, "e. Fungsi Perkemihan :", 0, y, 140);
        TPerkemihan = makeArea(3);
        set(FormInput, new javax.swing.JScrollPane(TPerkemihan), 143, y, 695, 50);

        y += 58;
        lbl(FormInput, "g. Status Neurosensori :", 0, y, 140);
        TNeurosensori = makeArea(4);
        set(FormInput, new javax.swing.JScrollPane(TNeurosensori), 143, y, 695, 65);

        y += 73;
        lbl(FormInput, "h. Fungsi Ekstremitas :", 0, y, 140);
        TEkstremitas = makeArea(3);
        set(FormInput, new javax.swing.JScrollPane(TEkstremitas), 143, y, 695, 50);

        y += 58;
        lbl(FormInput, "i. Kulit :", 0, y, 60);
        TKulit = makeArea(3);
        set(FormInput, new javax.swing.JScrollPane(TKulit), 63, y, 775, 50);

        y += 58;
        lbl(FormInput, "j. Istirahat/Tidur :", 0, y, 115);
        TIstirahaTidur = new TextBox(); TIstirahaTidur.addKeyListener(kl(TKulit, TPsikososial));
        set(FormInput, TIstirahaTidur, 118, y, 300, 23);

        y += 35;
        y = addSection(FormInput, "5. STATUS PSIKO-SOSIAL-EKONOMI DAN SPIRITUAL", y);
        TPsikososial = makeArea(4);
        set(FormInput, new javax.swing.JScrollPane(TPsikososial), 0, y, 838, 65);

        y += 73;
        y = addSection(FormInput, "1. SKRINING NYERI", y);
        lbl(FormInput, "Skor Nyeri :", 0, y, 78);
        TSkriningNyeri = new TextBox(); TSkriningNyeri.addKeyListener(kl(TPsikososial, TBarthelSkor));
        set(FormInput, TSkriningNyeri, 81, y, 60, 23);
        lbl(FormInput, "(0=tdk nyeri; 1-3=ringan; 4-6=sedang; 7-10=berat)", 144, y, 380);

        y += 30;
        y = addSection(FormInput, "2. STATUS FUNGSIONAL – Barthel Index", y);
        lbl(FormInput, "Total Skor Barthel :", 0, y, 130);
        TBarthelSkor = new TextBox(); TBarthelSkor.addKeyListener(kl(TSkriningNyeri, THumptySkor));
        set(FormInput, TBarthelSkor, 133, y, 60, 23);
        lbl(FormInput, "(20=Mandiri; 12-19=Ringan; 9-11=Sedang; 5-8=Berat; 0-4=Total)", 196, y, 450);

        y += 30;
        y = addSection(FormInput, "3. RISIKO CIDERA/JATUH – Humpty Dumpty", y);
        lbl(FormInput, "Total Skor Humpty Dumpty :", 0, y, 175);
        THumptySkor = new TextBox(); THumptySkor.addKeyListener(kl(TBarthelSkor, TGiziSkor));
        set(FormInput, THumptySkor, 178, y, 60, 23);
        lbl(FormInput, "(7-11=Risiko Rendah; \u226512=Risiko Tinggi)", 241, y, 310);

        y += 30;
        y = addSection(FormInput, "4. SKRINING GIZI – Strong Kids", y);
        lbl(FormInput, "Total Skor Gizi :", 0, y, 110);
        TGiziSkor = new TextBox(); TGiziSkor.addKeyListener(kl(THumptySkor, TImunocompromise));
        set(FormInput, TGiziSkor, 113, y, 60, 23);
        lbl(FormInput, "(0=Rendah; 1-3=Sedang; 4-5=Tinggi)", 176, y, 280);

        y += 35;
        y = addSection(FormInput, "10. SKRINING IMUNOCOMPROMISE", y);
        TImunocompromise = makeArea(3);
        set(FormInput, new javax.swing.JScrollPane(TImunocompromise), 0, y, 838, 50);

        y += 58;
        y = addSection(FormInput, "11. DATA PENUNJANG (Lab, Radiologi, EKG, dll)", y);
        TDataPenunjang = makeArea(3);
        set(FormInput, new javax.swing.JScrollPane(TDataPenunjang), 0, y, 838, 50);

        y += 58;
        y = addSection(FormInput, "12. MASALAH KEPERAWATAN", y);
        TMasalahKeperawatan = makeArea(4);
        set(FormInput, new javax.swing.JScrollPane(TMasalahKeperawatan), 0, y, 838, 65);

        y += 73;
        // Petugas
        lbl(FormInput, "NIP :", 0, y, 35);
        NIP = new TextBox(); NIP.setEditable(false); NIP.setHighlighter(null);
        NIP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
                    Sequel.cariIsi("select nama from petugas where nip=?", NamaPetugas, NIP.getText());
                else if (e.getKeyCode() == KeyEvent.VK_UP) btnPetugas.doClick();
            }
        });
        set(FormInput, NIP, 38, y, 90, 23);
        lbl(FormInput, "Nama Petugas :", 131, y, 95);
        NamaPetugas = new TextBox(); NamaPetugas.setEditable(false);
        set(FormInput, NamaPetugas, 229, y, 200, 23);
        btnPetugas = new Button();
        java.net.URL icon190 = getClass().getResource("/picture/190.png");
        if (icon190 != null) btnPetugas.setIcon(new ImageIcon(icon190));
        btnPetugas.setMnemonic('2'); btnPetugas.setToolTipText("Alt+2");
        btnPetugas.addActionListener(e -> {
            petugas.emptTeks(); petugas.isCek();
            petugas.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20);
            petugas.setLocationRelativeTo(internalFrame1); petugas.setVisible(true);
        });
        set(FormInput, btnPetugas, 432, y, 28, 23);

        scrollInput.setViewportView(FormInput);
        internalFrame2.add(scrollInput, java.awt.BorderLayout.CENTER);
        TabRawat.addTab("Input Asesmen", internalFrame2);

        // ── Tab 2: Data ───────────────────────────────────────────────────────
        internalFrame3 = new InternalFrame();
        internalFrame3.setBorder(null);
        internalFrame3.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll = new javax.swing.JScrollPane();
        Scroll.setPreferredSize(new Dimension(600, 300));
        tbObat = new Table();
        tbObat.setToolTipText("Klik kanan untuk mencetak formulir asesmen");
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
        internalFrame3.add(Scroll, java.awt.BorderLayout.CENTER);

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

        internalFrame3.add(panelGlass9, java.awt.BorderLayout.PAGE_END);
        TabRawat.addTab("Data Asesmen", internalFrame3);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);
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
    private int addSection(JPanel p, String title, int y) {
        Label l = new Label();
        l.setText(title);
        l.setFont(new Font("Tahoma", Font.BOLD, 11));
        l.setForeground(new Color(50, 50, 120));
        p.add(l); l.setBounds(0, y, 838, 20);
        return y + 22;
    }
    private TextArea makeArea(int rows) {
        TextArea ta = new TextArea();
        ta.setRows(rows);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        return ta;
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
        t.setForeground(new Color(50, 70, 50));
        t.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"24-05-2022"}));
        t.setDisplayFormat("dd-MM-yyyy");
        t.setPreferredSize(new Dimension(95, 23));
        return t;
    }
    /** Simple key listener: Enter/Down=focus next, PageUp/Up=focus prev */
    private java.awt.event.KeyAdapter kl(JComponent prev, JComponent next) {
        return new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) next.requestFocus();
                else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP || e.getKeyCode() == KeyEvent.VK_UP) prev.requestFocus();
            }
        };
    }

    // ── business logic ─────────────────────────────────────────────────────────
    private void BtnSimpanActionPerformed() {
        if (TNoRw.getText().trim().isEmpty()) { Valid.textKosong(TNoRw, "pasien"); return; }
        if (NIP.getText().trim().isEmpty()) { Valid.textKosong(NIP, "Petugas"); return; }
        if (Sequel.menyimpantf("asesmen_ranap_anak",
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?",
            "Data", 36, new String[]{
                TNoRw.getText(),
                new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date()),
                TAgama.getText(), TDxMedis.getText(),
                CInformasiDari.getSelectedItem().toString(),
                TCaraMasuk.getText(), TAsalMasuk.getText(),
                TAlergi.getText(), TKeluhanUtama.getText(),
                TRiwayatKesehatan.getText(), TImunisasiTumbuh.getText(),
                TTD.getText(), TNadi.getText(), TSuhu.getText(), TPernapasan.getText(),
                TTB.getText(), TBB.getText(),
                TTingkatKesadaran.getText(), TGCS.getText(),
                TPernafasanDetail.getText(), TKardio.getText(), TGI.getText(),
                TPerkemihan.getText(), TNeurosensori.getText(),
                TEkstremitas.getText(), TKulit.getText(), TIstirahaTidur.getText(),
                TPsikososial.getText(),
                TSkriningNyeri.getText(), TBarthelSkor.getText(),
                THumptySkor.getText(), TGiziSkor.getText(),
                TImunocompromise.getText(), TDataPenunjang.getText(),
                TMasalahKeperawatan.getText(), NIP.getText()
            }) == true) {
            tampil(); emptTeks();
        }
    }

    private void ganti() {
        Sequel.mengedit("asesmen_ranap_anak", "no_rawat=?",
            "tgl_asesmen=?,agama=?,dx_medis=?,informasi_dari=?,cara_masuk=?,asal_masuk=?," +
            "alergi=?,keluhan_utama=?,riwayat_kesehatan=?,imunisasi_tumbuh_kembang=?," +
            "td=?,nadi=?,suhu=?,pernapasan=?,tb=?,bb=?,tingkat_kesadaran=?,gcs=?," +
            "pernafasan_detail=?,kardio=?,gi=?,perkemihan=?,neurosensori=?," +
            "ekstremitas=?,kulit=?,istirahat_tidur=?,psikososial=?," +
            "skrining_nyeri_skor=?,barthel_skor=?,humpty_skor=?,gizi_skor=?," +
            "imunocompromise=?,data_penunjang=?,masalah_keperawatan=?,nip=?",
            36, new String[]{
                new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date()),
                TAgama.getText(), TDxMedis.getText(),
                CInformasiDari.getSelectedItem().toString(),
                TCaraMasuk.getText(), TAsalMasuk.getText(),
                TAlergi.getText(), TKeluhanUtama.getText(),
                TRiwayatKesehatan.getText(), TImunisasiTumbuh.getText(),
                TTD.getText(), TNadi.getText(), TSuhu.getText(), TPernapasan.getText(),
                TTB.getText(), TBB.getText(),
                TTingkatKesadaran.getText(), TGCS.getText(),
                TPernafasanDetail.getText(), TKardio.getText(), TGI.getText(),
                TPerkemihan.getText(), TNeurosensori.getText(),
                TEkstremitas.getText(), TKulit.getText(), TIstirahaTidur.getText(),
                TPsikososial.getText(),
                TSkriningNyeri.getText(), TBarthelSkor.getText(),
                THumptySkor.getText(), TGiziSkor.getText(),
                TImunocompromise.getText(), TDataPenunjang.getText(),
                TMasalahKeperawatan.getText(), NIP.getText(),
                tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
            });
        if (tabMode.getRowCount() != 0) tampil();
        emptTeks();
    }

    private void hapus() {
        if (Sequel.queryu2tf("delete from asesmen_ranap_anak where no_rawat=?",
            1, new String[]{tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()}) == true) {
            tampil(); emptTeks();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
        }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            String base = "select r.no_rawat,p.no_rkm_medis,p.nm_pasien,a.tgl_asesmen," +
                "a.cara_masuk,a.keluhan_utama,a.td,a.nadi,a.suhu,a.gcs,a.humpty_skor,a.gizi_skor,a.nip " +
                "from asesmen_ranap_anak a " +
                "inner join reg_periksa r on a.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                "where a.tgl_asesmen between ? and ?";
            if (TCari.getText().trim().isEmpty()) {
                ps = koneksi.prepareStatement(base + " order by a.tgl_asesmen");
                ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem()+"") + " 00:00:00");
                ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem()+"") + " 23:59:59");
            } else {
                ps = koneksi.prepareStatement(base +
                    " and (r.no_rawat like ? or p.no_rkm_medis like ? or p.nm_pasien like ?)" +
                    " order by a.tgl_asesmen");
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
                    rs.getString("tgl_asesmen"), rs.getString("cara_masuk"), rs.getString("keluhan_utama"),
                    rs.getString("td"), rs.getString("nadi"), rs.getString("suhu"),
                    rs.getString("gcs"), rs.getString("humpty_skor"), rs.getString("gizi_skor"), rs.getString("nip")
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
        if (tbObat.getSelectedRow() < 0) return;
        String noRawat = tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString();
        try {
            ps = koneksi.prepareStatement("select * from asesmen_ranap_anak where no_rawat=?");
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            if (rs.next()) {
                TNoRw.setText(rs.getString("no_rawat"));
                isRawat(); isPasien();
                TAgama.setText(rs.getString("agama"));
                TDxMedis.setText(rs.getString("dx_medis"));
                CInformasiDari.setSelectedItem(rs.getString("informasi_dari"));
                TCaraMasuk.setText(rs.getString("cara_masuk"));
                TAsalMasuk.setText(rs.getString("asal_masuk"));
                TAlergi.setText(rs.getString("alergi"));
                TKeluhanUtama.setText(rs.getString("keluhan_utama"));
                TRiwayatKesehatan.setText(rs.getString("riwayat_kesehatan"));
                TImunisasiTumbuh.setText(rs.getString("imunisasi_tumbuh_kembang"));
                TTD.setText(rs.getString("td"));
                TNadi.setText(rs.getString("nadi"));
                TSuhu.setText(rs.getString("suhu"));
                TPernapasan.setText(rs.getString("pernapasan"));
                TTB.setText(rs.getString("tb"));
                TBB.setText(rs.getString("bb"));
                TTingkatKesadaran.setText(rs.getString("tingkat_kesadaran"));
                TGCS.setText(rs.getString("gcs"));
                TPernafasanDetail.setText(rs.getString("pernafasan_detail"));
                TKardio.setText(rs.getString("kardio"));
                TGI.setText(rs.getString("gi"));
                TPerkemihan.setText(rs.getString("perkemihan"));
                TNeurosensori.setText(rs.getString("neurosensori"));
                TEkstremitas.setText(rs.getString("ekstremitas"));
                TKulit.setText(rs.getString("kulit"));
                TIstirahaTidur.setText(rs.getString("istirahat_tidur"));
                TPsikososial.setText(rs.getString("psikososial"));
                TSkriningNyeri.setText(rs.getString("skrining_nyeri_skor"));
                TBarthelSkor.setText(rs.getString("barthel_skor"));
                THumptySkor.setText(rs.getString("humpty_skor"));
                TGiziSkor.setText(rs.getString("gizi_skor"));
                TImunocompromise.setText(rs.getString("imunocompromise"));
                TDataPenunjang.setText(rs.getString("data_penunjang"));
                TMasalahKeperawatan.setText(rs.getString("masalah_keperawatan"));
                NIP.setText(rs.getString("nip"));
                Sequel.cariIsi("select nama from petugas where nip=?", NamaPetugas, NIP.getText());
            }
        } catch (Exception e) {
            System.out.println("getData: " + e);
        } finally {
            try { if (rs!=null) rs.close(); if (ps!=null) ps.close(); } catch (Exception ex) {}
        }
        TabRawat.setSelectedIndex(0); // switch to input tab
    }

    public void emptTeks() {
        TAgama.setText(""); TDxMedis.setText("");
        CInformasiDari.setSelectedIndex(0);
        TCaraMasuk.setText(""); TAsalMasuk.setText("");
        TAlergi.setText(""); TKeluhanUtama.setText("");
        TRiwayatKesehatan.setText(""); TImunisasiTumbuh.setText("");
        TTD.setText(""); TNadi.setText(""); TSuhu.setText(""); TPernapasan.setText("");
        TTB.setText(""); TBB.setText(""); TTingkatKesadaran.setText(""); TGCS.setText("");
        TPernafasanDetail.setText(""); TKardio.setText(""); TGI.setText("");
        TPerkemihan.setText(""); TNeurosensori.setText(""); TEkstremitas.setText("");
        TKulit.setText(""); TIstirahaTidur.setText(""); TPsikososial.setText("");
        TSkriningNyeri.setText(""); TBarthelSkor.setText(""); THumptySkor.setText(""); TGiziSkor.setText("");
        TImunocompromise.setText(""); TDataPenunjang.setText(""); TMasalahKeperawatan.setText("");
        TAgama.requestFocus();
    }

    private void isRawat() {
        Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat='" + TNoRw.getText() + "'", TNoRM);
    }
    private void isPasien() {
        Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis='" + TNoRM.getText() + "'", TNamaPasien);
        Sequel.cariIsi("select DATE_FORMAT(tgl_lahir,'%d-%m-%Y') from pasien where no_rkm_medis='" + TNoRM.getText() + "'", TTglLahir);
        Sequel.cariIsi("select DATE_FORMAT(tgl_registrasi,'%d-%m-%Y') from reg_periksa where no_rawat='" + TNoRw.getText() + "'", TTglMasuk);
    }

    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        Sequel.cariIsi("select tgl_registrasi from reg_periksa where no_rawat='" + norwt + "'", DTPCari1);
        DTPCari2.setDate(tgl2);
        isRawat(); isPasien();
        tampil();
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
        Valid.MyReportqry("rptAsesmenRanapAnak.jasper", "report",
            "::[ Asesmen Rawat Inap Anak ]::",
            "select a.*,p.nm_pasien,p.no_rkm_medis,p.tgl_lahir,p.jk," +
            "r.tgl_registrasi,r.kd_poli,pt.nama as nm_petugas " +
            "from asesmen_ranap_anak a " +
            "inner join reg_periksa r on a.no_rawat=r.no_rawat " +
            "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
            "left join petugas pt on a.nip=pt.nip " +
            "where a.no_rawat='" + noRawat + "'",
            param);
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

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMAsesmenRanapAnak dialog = new RMAsesmenRanapAnak(new JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosing(java.awt.event.WindowEvent e) { System.exit(0); }
            });
            dialog.setVisible(true);
        });
    }
}
