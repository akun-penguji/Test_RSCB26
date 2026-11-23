/*
 * DlgStokMinimalDepo.java
 *
 * Form pengelolaan override stok minimal obat/BHP per depo (bangsal farmasi).
 * Tabel target: stok_minimal_bangsal
 *   PK (kode_brng, kd_bangsal) — stok_minimal, aktif
 *   fallback ke databarang.stokminimal kalau tidak ada row di sini.
 */
package simrskhanza;

import fungsi.WarnaTable;
import fungsi.koneksiDB;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public final class DlgStokMinimalDepo extends javax.swing.JDialog {

    private final Connection koneksi = koneksiDB.condb();
    private final DefaultTableModel modelOvr;
    private final DefaultTableModel modelCari;
    private final DefaultTableModel modelMinMax;
    /** Hasil perhitungan Min-Max terakhir — sumber kebenaran saat Simpan. */
    private final List<MinMaxRow> hasilMinMax = new ArrayList<>();

    public DlgStokMinimalDepo(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width, screen.height - 40);
        setLocation(0, 0);

        modelOvr = new DefaultTableModel(null,
            new Object[]{"Kode", "Nama Barang", "Satuan", "Bangsal", "Min Override",
                         "Min Global", "Aktif", "Updated"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbOverride.setModel(modelOvr);
        tbOverride.setPreferredScrollableViewportSize(new Dimension(500, 240));
        tbOverride.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] wO = {80, 260, 60, 140, 100, 90, 60, 140};
        for (int i = 0; i < wO.length; i++) {
            TableColumn c = tbOverride.getColumnModel().getColumn(i);
            c.setPreferredWidth(wO[i]);
        }
        tbOverride.setDefaultRenderer(Object.class, new WarnaTable());

        modelCari = new DefaultTableModel(null,
            new Object[]{"Kode", "Nama Barang", "Jenis", "Satuan", "Min Global"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbCari.setModel(modelCari);
        tbCari.setPreferredScrollableViewportSize(new Dimension(500, 180));
        tbCari.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] wC = {80, 320, 120, 70, 90};
        for (int i = 0; i < wC.length; i++) {
            TableColumn c = tbCari.getColumnModel().getColumn(i);
            c.setPreferredWidth(wC[i]);
        }
        tbCari.setDefaultRenderer(Object.class, new WarnaTable());

        modelMinMax = new DefaultTableModel(null,
            new Object[]{"Kode", "Nama Barang", "Satuan", "Bangsal", "Hari", "Total Pakai",
                         "CA/hari", "SS", "Smin (baru)", "Smax (baru)", "Stok Kini", "Min Lama"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbMinMax.setModel(modelMinMax);
        tbMinMax.setPreferredScrollableViewportSize(new Dimension(500, 240));
        tbMinMax.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] wM = {80, 240, 55, 150, 45, 80, 70, 65, 90, 90, 75, 75};
        for (int i = 0; i < wM.length; i++) {
            TableColumn c = tbMinMax.getColumnModel().getColumn(i);
            c.setPreferredWidth(wM[i]);
        }
        tbMinMax.setDefaultRenderer(Object.class, new WarnaTable());

        TKodeBrng.setDocument(new fungsi.batasInput((byte) 15).getKata(TKodeBrng));
        TStokMin.setDocument(new fungsi.batasInput((byte) 12).getOnlyAngka(TStokMin));
        TCari.setDocument(new fungsi.batasInput((byte) 100).getKata(TCari));
        THLeadTime.setDocument(new fungsi.batasInput((byte) 5).getOnlyAngka(THLeadTime));
        THProcPeriod.setDocument(new fungsi.batasInput((byte) 5).getOnlyAngka(THProcPeriod));
        THLeadTime.setText("7");
        THProcPeriod.setText("30");
        // Periode analisis default: 3 bulan terakhir s/d hari ini.
        java.util.Calendar cal = java.util.Calendar.getInstance();
        THAkhir.setDate(cal.getTime());
        cal.add(java.util.Calendar.MONTH, -3);
        THAwal.setDate(cal.getTime());

        loadBangsal();
        tampilOverride();
        cariBarang(); // pre-load semua obat ke tab Cari Barang
    }

    // =================================================================
    // LAYOUT
    // =================================================================
    @SuppressWarnings("unchecked")
    private void initComponents() {
        internalFrame1 = new widget.InternalFrame();
        TabPane = new widget.TabPane();

        // Tab 1 — Override list
        panelOvr = new javax.swing.JPanel();
        panelFilter = new widget.panelisi();
        lblFilterBang = new widget.Label();
        cbFilterBang = new widget.ComboBox();
        lblKeyword = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        ScrollOvr = new widget.ScrollPane();
        tbOverride = new widget.Table();

        panelDetail = new widget.panelisi();
        lblKode = new widget.Label();
        TKodeBrng = new widget.TextBox();
        BtnLookup = new widget.Button();
        lblNama = new widget.Label();
        TNamaBrng = new widget.TextBox();
        lblBangsal = new widget.Label();
        cbBangsal = new widget.ComboBox();
        lblStokMin = new widget.Label();
        TStokMin = new widget.TextBox();
        lblSatuan = new widget.Label();
        TSatuan = new widget.TextBox();
        lblGlobal = new widget.Label();
        TGlobal = new widget.TextBox();
        chkAktif = new widget.CekBox();

        panelAksi = new widget.panelisi();
        BtnBaru = new widget.Button();
        BtnSimpan = new widget.Button();
        BtnHapus = new widget.Button();
        BtnBatal = new widget.Button();
        BtnRefresh = new widget.Button();

        // Tab 2 — Pencarian barang
        panelBrng = new javax.swing.JPanel();
        panelCariBrng = new widget.panelisi();
        lblCariBrng = new widget.Label();
        TCariBrng = new widget.TextBox();
        BtnCariBrng = new widget.Button();
        ScrollBrng = new widget.ScrollPane();
        tbCari = new widget.Table();
        panelBrngHint = new widget.panelisi();
        lblHintBrng = new widget.Label();

        // Tab 3 — Hitung Min-Max (otomatis)
        panelHitung = new javax.swing.JPanel();
        panelParam = new widget.panelisi();
        lblHBang = new widget.Label();
        cbHitungBang = new widget.ComboBox();
        lblHPeriode = new widget.Label();
        THAwal = new widget.Tanggal();
        lblHsd = new widget.Label();
        THAkhir = new widget.Tanggal();
        lblHLT = new widget.Label();
        THLeadTime = new widget.TextBox();
        lblHPP = new widget.Label();
        THProcPeriod = new widget.TextBox();
        BtnHitung = new widget.Button();
        ScrollMinMax = new widget.ScrollPane();
        tbMinMax = new widget.Table();
        panelHitungAksi = new widget.panelisi();
        BtnSimpanMinMax = new widget.Button();
        lblHitungInfo = new widget.Label();

        // Bottom
        panelBottom = new widget.panelisi();
        BtnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Stok Minimal per Depo :: Override stokminimal per bangsal");

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Stok Minimal per Depo ]::",
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        // ---------- Tab 1 layout ----------
        panelOvr.setOpaque(false);
        panelOvr.setLayout(new java.awt.BorderLayout(1, 1));

        panelFilter.setPreferredSize(new Dimension(44, 40));
        panelFilter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 8));

        lblFilterBang.setText("Filter Bangsal :");
        lblFilterBang.setPreferredSize(new Dimension(95, 23));
        panelFilter.add(lblFilterBang);
        cbFilterBang.setPreferredSize(new Dimension(170, 23));
        cbFilterBang.addActionListener(e -> tampilOverride());
        panelFilter.add(cbFilterBang);

        lblKeyword.setText("Kata Kunci :");
        lblKeyword.setPreferredSize(new Dimension(75, 23));
        panelFilter.add(lblKeyword);
        TCari.setPreferredSize(new Dimension(250, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) tampilOverride();
            }
        });
        panelFilter.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setMnemonic('1');
        BtnCari.setToolTipText("Alt+1 · Cari");
        BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnCari.addActionListener(e -> tampilOverride());
        panelFilter.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setMnemonic('2');
        BtnAll.setToolTipText("Alt+2 · Semua");
        BtnAll.setPreferredSize(new Dimension(28, 23));
        BtnAll.addActionListener(e -> {
            TCari.setText("");
            cbFilterBang.setSelectedIndex(0);
            tampilOverride();
        });
        panelFilter.add(BtnAll);

        tbOverride.setAutoCreateRowSorter(true);
        tbOverride.setToolTipText("Klik untuk pilih override yang akan diedit / dihapus");
        tbOverride.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { tbOverrideMouseClicked(evt); }
        });
        ScrollOvr.setViewportView(tbOverride);
        panelOvr.add(ScrollOvr, java.awt.BorderLayout.CENTER);

        panelDetail.setPreferredSize(new Dimension(44, 90));
        panelDetail.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 5));

        lblKode.setText("Kode Barang :");
        lblKode.setPreferredSize(new Dimension(90, 23));
        panelDetail.add(lblKode);
        TKodeBrng.setPreferredSize(new Dimension(120, 23));
        TKodeBrng.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) lookupBarang();
            }
        });
        panelDetail.add(TKodeBrng);

        BtnLookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnLookup.setText("Cek / Cari");
        BtnLookup.setToolTipText("Cek kode barang atau buka tab Cari Barang");
        BtnLookup.setPreferredSize(new Dimension(105, 23));
        BtnLookup.addActionListener(e -> {
            if (TKodeBrng.getText().trim().isEmpty()) {
                TabPane.setSelectedIndex(1);
                TCariBrng.requestFocusInWindow();
            } else {
                lookupBarang();
            }
        });
        panelDetail.add(BtnLookup);

        lblNama.setText("Nama :");
        lblNama.setPreferredSize(new Dimension(50, 23));
        panelDetail.add(lblNama);
        TNamaBrng.setPreferredSize(new Dimension(320, 23));
        TNamaBrng.setEditable(false);
        panelDetail.add(TNamaBrng);

        lblSatuan.setText("Sat :");
        lblSatuan.setPreferredSize(new Dimension(30, 23));
        panelDetail.add(lblSatuan);
        TSatuan.setPreferredSize(new Dimension(60, 23));
        TSatuan.setEditable(false);
        panelDetail.add(TSatuan);

        lblBangsal.setText("Bangsal (depo) :");
        lblBangsal.setPreferredSize(new Dimension(100, 23));
        panelDetail.add(lblBangsal);
        cbBangsal.setPreferredSize(new Dimension(170, 23));
        panelDetail.add(cbBangsal);

        lblStokMin.setText("Stok Min Override :");
        lblStokMin.setPreferredSize(new Dimension(115, 23));
        panelDetail.add(lblStokMin);
        TStokMin.setPreferredSize(new Dimension(90, 23));
        panelDetail.add(TStokMin);

        lblGlobal.setText("Min Global :");
        lblGlobal.setPreferredSize(new Dimension(75, 23));
        panelDetail.add(lblGlobal);
        TGlobal.setPreferredSize(new Dimension(90, 23));
        TGlobal.setEditable(false);
        panelDetail.add(TGlobal);

        chkAktif.setText("Aktif");
        chkAktif.setSelected(true);
        chkAktif.setOpaque(false);
        panelDetail.add(chkAktif);

        panelAksi.setPreferredSize(new Dimension(44, 36));
        panelAksi.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 6));

        BtnBaru.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnBaru.setMnemonic('N');
        BtnBaru.setText("Baru");
        BtnBaru.setToolTipText("Alt+N, bersihkan form");
        BtnBaru.setPreferredSize(new Dimension(95, 23));
        BtnBaru.addActionListener(e -> clearForm());
        panelAksi.add(BtnBaru);

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S · INSERT atau UPDATE (upsert)");
        BtnSimpan.setPreferredSize(new Dimension(100, 23));
        BtnSimpan.addActionListener(e -> simpan());
        panelAksi.add(BtnSimpan);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H · DELETE override (kembali ke min global)");
        BtnHapus.setPreferredSize(new Dimension(100, 23));
        BtnHapus.addActionListener(e -> hapus());
        panelAksi.add(BtnHapus);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Batal");
        BtnBatal.setPreferredSize(new Dimension(95, 23));
        BtnBatal.addActionListener(e -> clearForm());
        panelAksi.add(BtnBatal);

        BtnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnRefresh.setMnemonic('R');
        BtnRefresh.setText("Refresh");
        BtnRefresh.setPreferredSize(new Dimension(100, 23));
        BtnRefresh.addActionListener(e -> tampilOverride());
        panelAksi.add(BtnRefresh);

        BtnImportAll = new widget.Button();
        BtnImportAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnImportAll.setMnemonic('I');
        BtnImportAll.setText("Import Semua Obat");
        BtnImportAll.setToolTipText("Alt+I · Insert semua obat aktif ke bangsal terpilih dgn nilai stok minimal yang sama");
        BtnImportAll.setPreferredSize(new Dimension(180, 23));
        BtnImportAll.addActionListener(e -> importSemua());
        panelAksi.add(BtnImportAll);

        // Input detail di ATAS
        panelOvr.add(panelDetail, java.awt.BorderLayout.NORTH);

        // Filter + tombol CRUD di BAWAH (filter dulu, CRUD paling bawah)
        javax.swing.JPanel south1 = new javax.swing.JPanel(new java.awt.BorderLayout(1, 1));
        south1.setOpaque(false);
        south1.add(panelFilter, java.awt.BorderLayout.NORTH);
        south1.add(panelAksi, java.awt.BorderLayout.SOUTH);
        panelOvr.add(south1, java.awt.BorderLayout.SOUTH);

        // ---------- Tab 2 layout ----------
        panelBrng.setOpaque(false);
        panelBrng.setLayout(new java.awt.BorderLayout(1, 1));

        panelCariBrng.setPreferredSize(new Dimension(44, 40));
        panelCariBrng.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 8));
        lblCariBrng.setText("Cari Barang (kode / nama) :");
        lblCariBrng.setPreferredSize(new Dimension(170, 23));
        panelCariBrng.add(lblCariBrng);
        TCariBrng = new widget.TextBox();
        TCariBrng.setPreferredSize(new Dimension(300, 23));
        TCariBrng.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) cariBarang();
            }
        });
        panelCariBrng.add(TCariBrng);
        BtnCariBrng.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCariBrng.setText("Cari");
        BtnCariBrng.setPreferredSize(new Dimension(80, 23));
        BtnCariBrng.addActionListener(e -> cariBarang());
        panelCariBrng.add(BtnCariBrng);

        panelBrng.add(panelCariBrng, java.awt.BorderLayout.NORTH);

        tbCari.setAutoCreateRowSorter(true);
        tbCari.setToolTipText("Double-klik untuk pilih barang & kembali ke tab Override");
        tbCari.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() >= 2) pilihBarang();
            }
        });
        ScrollBrng.setViewportView(tbCari);
        panelBrng.add(ScrollBrng, java.awt.BorderLayout.CENTER);

        panelBrngHint.setPreferredSize(new Dimension(44, 30));
        panelBrngHint.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 6));
        lblHintBrng.setText("Double-klik pada baris untuk pilih barang — lalu pilih bangsal & isi stok minimal di tab Override.");
        lblHintBrng.setForeground(new Color(120, 120, 120));
        panelBrngHint.add(lblHintBrng);
        panelBrng.add(panelBrngHint, java.awt.BorderLayout.SOUTH);

        // ---------- Tab 3 layout — Hitung Min-Max ----------
        panelHitung.setOpaque(false);
        panelHitung.setLayout(new java.awt.BorderLayout(1, 1));

        panelParam.setPreferredSize(new Dimension(44, 40));
        panelParam.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 8));

        lblHBang.setText("Bangsal (depo) :");
        lblHBang.setPreferredSize(new Dimension(95, 23));
        panelParam.add(lblHBang);
        cbHitungBang.setPreferredSize(new Dimension(170, 23));
        panelParam.add(cbHitungBang);

        lblHPeriode.setText("Periode :");
        lblHPeriode.setPreferredSize(new Dimension(55, 23));
        panelParam.add(lblHPeriode);
        THAwal.setPreferredSize(new Dimension(95, 23));
        panelParam.add(THAwal);
        lblHsd.setText("s/d");
        lblHsd.setPreferredSize(new Dimension(22, 23));
        panelParam.add(lblHsd);
        THAkhir.setPreferredSize(new Dimension(95, 23));
        panelParam.add(THAkhir);

        lblHLT.setText("Lead Time (hari) :");
        lblHLT.setPreferredSize(new Dimension(105, 23));
        panelParam.add(lblHLT);
        THLeadTime.setPreferredSize(new Dimension(50, 23));
        panelParam.add(THLeadTime);

        lblHPP.setText("Procurement Period (hari) :");
        lblHPP.setPreferredSize(new Dimension(160, 23));
        panelParam.add(lblHPP);
        THProcPeriod.setPreferredSize(new Dimension(50, 23));
        panelParam.add(THProcPeriod);

        BtnHitung.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnHitung.setMnemonic('T');
        BtnHitung.setText("Hitung");
        BtnHitung.setToolTipText("Alt+T · Hitung CA dari pemakaian obat, lalu SS / Smin / Smax");
        BtnHitung.setPreferredSize(new Dimension(100, 23));
        BtnHitung.addActionListener(e -> hitungMinMax());
        panelParam.add(BtnHitung);

        panelHitung.add(panelParam, java.awt.BorderLayout.NORTH);

        tbMinMax.setAutoCreateRowSorter(true);
        tbMinMax.setToolTipText("Hasil perhitungan Min-Max. Tekan Simpan untuk menulis ke override per depo.");
        ScrollMinMax.setViewportView(tbMinMax);
        panelHitung.add(ScrollMinMax, java.awt.BorderLayout.CENTER);

        panelHitungAksi.setPreferredSize(new Dimension(44, 36));
        panelHitungAksi.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 6));
        BtnSimpanMinMax.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpanMinMax.setMnemonic('M');
        BtnSimpanMinMax.setText("Simpan Hasil ke Override");
        BtnSimpanMinMax.setToolTipText("Alt+M · Tulis Smin & Smax hasil hitung ke tabel stok_minimal_bangsal");
        BtnSimpanMinMax.setPreferredSize(new Dimension(210, 23));
        BtnSimpanMinMax.addActionListener(e -> simpanMinMax());
        panelHitungAksi.add(BtnSimpanMinMax);
        lblHitungInfo.setText("SS = LT×CA   ·   Smin = 2×LT×CA   ·   Smax = Smin + PP×CA   ·   "
                + "CA = total pemakaian ÷ jumlah hari periode.");
        lblHitungInfo.setForeground(new Color(120, 120, 120));
        panelHitungAksi.add(lblHitungInfo);
        panelHitung.add(panelHitungAksi, java.awt.BorderLayout.SOUTH);

        // ---------- Tabs ----------
        TabPane.addTab("Override Stok Minimal per Depo", panelOvr);
        TabPane.addTab("Cari Barang", panelBrng);
        TabPane.addTab("Hitung Min-Max (Auto)", panelHitung);
        internalFrame1.add(TabPane, java.awt.BorderLayout.CENTER);

        panelBottom.setPreferredSize(new Dimension(44, 36));
        panelBottom.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 6));
        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setPreferredSize(new Dimension(100, 23));
        BtnKeluar.addActionListener(e -> dispose());
        panelBottom.add(BtnKeluar);
        internalFrame1.add(panelBottom, java.awt.BorderLayout.SOUTH);

        getContentPane().setLayout(new java.awt.BorderLayout(1, 1));
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
        pack();
    }

    // =================================================================
    // Bangsal dropdown (detail form + filter)
    // =================================================================
    private void loadBangsal() {
        cbBangsal.removeAllItems();
        cbBangsal.addItem(new BangsalItem("", "-- Pilih --"));

        cbFilterBang.removeAllItems();
        cbFilterBang.addItem(new BangsalItem("", "-- Semua --"));

        cbHitungBang.removeAllItems();
        cbHitungBang.addItem(new BangsalItem("", "-- Semua Bangsal --"));

        String sql = "SELECT kd_bangsal, nm_bangsal FROM bangsal WHERE status='1' "
                   + "AND kd_bangsal <> '-' ORDER BY nm_bangsal";
        try (PreparedStatement p = koneksi.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                BangsalItem it = new BangsalItem(r.getString(1), r.getString(2));
                cbBangsal.addItem(it);
                cbFilterBang.addItem(it);
                cbHitungBang.addItem(it);
            }
        } catch (SQLException ex) {
            showErr("Gagal muat bangsal", ex);
        }
    }

    // =================================================================
    // Override CRUD
    // =================================================================
    private void tampilOverride() {
        modelOvr.setRowCount(0);
        String filterKd = "";
        Object s = cbFilterBang.getSelectedItem();
        if (s instanceof BangsalItem) filterKd = ((BangsalItem) s).kd;
        String keyword = TCari.getText().trim();

        StringBuilder sql = new StringBuilder(
            "SELECT smb.kode_brng, smb.kd_bangsal, smb.stok_minimal, smb.aktif, smb.updated_at, "
            + "       db.nama_brng, db.kode_sat, db.stokminimal AS stok_global, "
            + "       IFNULL(b.nm_bangsal,'') AS nm_bangsal "
            + "FROM stok_minimal_bangsal smb "
            + "LEFT JOIN databarang db ON db.kode_brng = smb.kode_brng "
            + "LEFT JOIN bangsal b ON b.kd_bangsal = smb.kd_bangsal "
            + "WHERE 1=1");
        if (!filterKd.isEmpty()) sql.append(" AND smb.kd_bangsal = ?");
        if (!keyword.isEmpty())  sql.append(" AND (smb.kode_brng LIKE ? OR db.nama_brng LIKE ?)");
        sql.append(" ORDER BY b.nm_bangsal, db.nama_brng");

        try (PreparedStatement p = koneksi.prepareStatement(sql.toString())) {
            int idx = 1;
            if (!filterKd.isEmpty()) p.setString(idx++, filterKd);
            if (!keyword.isEmpty()) {
                p.setString(idx++, "%" + keyword + "%");
                p.setString(idx++, "%" + keyword + "%");
            }
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    String kdB = r.getString("kd_bangsal");
                    String nmB = r.getString("nm_bangsal");
                    String bang = (nmB == null || nmB.isEmpty()) ? kdB : (kdB + " / " + nmB);
                    modelOvr.addRow(new Object[]{
                        r.getString("kode_brng"),
                        r.getString("nama_brng"),
                        r.getString("kode_sat"),
                        bang,
                        fmt(r.getDouble("stok_minimal")),
                        fmt(r.getObject("stok_global") == null ? 0 : r.getDouble("stok_global")),
                        r.getInt("aktif") == 1 ? "Aktif" : "-",
                        r.getString("updated_at")
                    });
                }
            }
        } catch (SQLException ex) {
            showErr("Gagal muat data", ex);
        }
    }

    private void tbOverrideMouseClicked(java.awt.event.MouseEvent evt) {
        int row = tbOverride.getSelectedRow();
        if (row < 0) return;
        String kode = str(modelOvr.getValueAt(row, 0));
        String kdBang = extractKdBangsal(str(modelOvr.getValueAt(row, 3)));
        TKodeBrng.setText(kode);
        TNamaBrng.setText(str(modelOvr.getValueAt(row, 1)));
        TSatuan.setText(str(modelOvr.getValueAt(row, 2)));
        TStokMin.setText(str(modelOvr.getValueAt(row, 4)));
        TGlobal.setText(str(modelOvr.getValueAt(row, 5)));
        chkAktif.setSelected("Aktif".equals(modelOvr.getValueAt(row, 6)));
        selectBangsal(cbBangsal, kdBang);
    }

    private void clearForm() {
        TKodeBrng.setText("");
        TNamaBrng.setText("");
        TSatuan.setText("");
        TStokMin.setText("");
        TGlobal.setText("");
        chkAktif.setSelected(true);
        // Catatan: cbBangsal TIDAK di-reset — posisi bangsal terakhir tetap dipertahankan
        tbOverride.clearSelection();
        TKodeBrng.requestFocusInWindow();
    }

    private void simpan() {
        String kode = TKodeBrng.getText().trim();
        if (kode.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Isi Kode Barang terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // pastikan kode valid
        if (TNamaBrng.getText().trim().isEmpty()) {
            lookupBarang();
            if (TNamaBrng.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Kode barang tidak ditemukan di databarang.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        Object sel = cbBangsal.getSelectedItem();
        String kdB = (sel instanceof BangsalItem) ? ((BangsalItem) sel).kd : "";
        if (kdB.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih Bangsal (depo) terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String sm = TStokMin.getText().trim();
        if (sm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Isi Stok Minimal Override.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double stokMin;
        try { stokMin = Double.parseDouble(sm); }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Stok minimal harus angka.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO stok_minimal_bangsal (kode_brng, kd_bangsal, stok_minimal, aktif) "
                   + "VALUES (?, ?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE stok_minimal = VALUES(stok_minimal), aktif = VALUES(aktif)";
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            p.setString(1, kode);
            p.setString(2, kdB);
            p.setDouble(3, stokMin);
            p.setInt(4, chkAktif.isSelected() ? 1 : 0);
            p.executeUpdate();
            JOptionPane.showMessageDialog(this, "Override stok minimal disimpan.");
            clearForm();
            tampilOverride();
        } catch (SQLException ex) {
            showErr("Gagal simpan", ex);
        }
    }

    private void hapus() {
        String kode = TKodeBrng.getText().trim();
        Object sel = cbBangsal.getSelectedItem();
        String kdB = (sel instanceof BangsalItem) ? ((BangsalItem) sel).kd : "";
        if (kode.isEmpty() || kdB.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih row override di tabel terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
                "Hapus override untuk " + kode + " @ " + kdB + "?\n(Sistem akan kembali pakai stok minimal global.)",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try (PreparedStatement p = koneksi.prepareStatement(
                "DELETE FROM stok_minimal_bangsal WHERE kode_brng = ? AND kd_bangsal = ?")) {
            p.setString(1, kode);
            p.setString(2, kdB);
            p.executeUpdate();
            JOptionPane.showMessageDialog(this, "Override dihapus.");
            clearForm();
            tampilOverride();
        } catch (SQLException ex) {
            showErr("Gagal hapus", ex);
        }
    }

    private void lookupBarang() {
        String kode = TKodeBrng.getText().trim();
        if (kode.isEmpty()) return;
        try (PreparedStatement p = koneksi.prepareStatement(
                "SELECT nama_brng, kode_sat, stokminimal FROM databarang "
                + "WHERE kode_brng = ? AND status='1'")) {
            p.setString(1, kode);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    TNamaBrng.setText(r.getString(1));
                    TSatuan.setText(r.getString(2));
                    TGlobal.setText(fmt(r.getObject(3) == null ? 0 : r.getDouble(3)));
                } else {
                    TNamaBrng.setText("");
                    TSatuan.setText("");
                    TGlobal.setText("");
                    JOptionPane.showMessageDialog(this, "Kode barang tidak ditemukan.",
                            "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            showErr("Gagal lookup barang", ex);
        }
    }

    // =================================================================
    // Import semua obat aktif ke bangsal terpilih
    // =================================================================
    private void importSemua() {
        Object sel = cbBangsal.getSelectedItem();
        String kdB = (sel instanceof BangsalItem) ? ((BangsalItem) sel).kd : "";
        String nmB = (sel instanceof BangsalItem) ? ((BangsalItem) sel).nm : "";
        if (kdB.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Pilih Bangsal (depo) di panel input terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Pilih sumber nilai
        String[] opsi = {
            "A. Nilai sama untuk semua obat",
            "B. Dari kolom databarang.stokminimal (per obat)",
            "Batal"
        };
        int src = JOptionPane.showOptionDialog(this,
                "Pilih sumber nilai stok minimal untuk bangsal:\n"
                + kdB + " — " + nmB + "\n\n"
                + "A = satu nilai dipakai untuk semua obat\n"
                + "B = ambil per-obat dari kolom databarang.stokminimal "
                + "(obat dgn stokminimal NULL atau 0 di-skip)",
                "Sumber Nilai Stok Minimal",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opsi, opsi[0]);
        if (src < 0 || src == 2) return;
        boolean dariDatabarang = (src == 1);

        double stokMin = 0;
        if (!dariDatabarang) {
            String nilai = (String) JOptionPane.showInputDialog(this,
                    "Stok minimal untuk SEMUA obat aktif:",
                    "Import — Nilai Sama",
                    JOptionPane.QUESTION_MESSAGE, null, null, "100");
            if (nilai == null) return;
            try {
                stokMin = Double.parseDouble(nilai.trim());
                if (stokMin <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Nilai harus angka > 0.",
                        "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Hitung jumlah kandidat
        String sqlCount = dariDatabarang
            ? "SELECT COUNT(*) FROM databarang WHERE status='1' AND stokminimal IS NOT NULL AND stokminimal > 0"
            : "SELECT COUNT(*) FROM databarang WHERE status='1'";
        int jumlah = 0;
        try (PreparedStatement p = koneksi.prepareStatement(sqlCount);
             ResultSet r = p.executeQuery()) {
            if (r.next()) jumlah = r.getInt(1);
        } catch (SQLException ex) {
            showErr("Gagal hitung data", ex);
            return;
        }
        if (jumlah == 0) {
            JOptionPane.showMessageDialog(this,
                    dariDatabarang
                        ? "Tidak ada obat aktif dengan databarang.stokminimal > 0."
                        : "Tidak ada obat aktif.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Tanya overwrite?
        int ow = JOptionPane.showConfirmDialog(this,
                "Timpa nilai stok minimal yang sudah terdaftar untuk bangsal ini?\n\n"
                + "• Yes = Overwrite (nilai existing ditimpa)\n"
                + "• No  = Skip existing (hanya tambah obat yang belum terdaftar)",
                "Kebijakan Timpa", JOptionPane.YES_NO_CANCEL_OPTION);
        if (ow == JOptionPane.CANCEL_OPTION || ow == JOptionPane.CLOSED_OPTION) return;
        boolean overwrite = (ow == JOptionPane.YES_OPTION);

        // Konfirmasi akhir
        String nilaiDesc = dariDatabarang
            ? "nilai per obat dari databarang.stokminimal"
            : ("stok minimal = " + fmt(stokMin));
        int ok = JOptionPane.showConfirmDialog(this,
                "Akan import " + jumlah + " obat ke bangsal " + kdB + "\n"
                + nilaiDesc + "\n"
                + "Mode: " + (overwrite ? "OVERWRITE (timpa yang sudah ada)"
                                         : "SKIP existing (hanya tambah baru)")
                + "\n\nLanjut?",
                "Konfirmasi Import", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        // Susun SQL
        String sql;
        if (dariDatabarang) {
            String select = "SELECT kode_brng, ?, stokminimal, 1 "
                          + "FROM databarang "
                          + "WHERE status='1' AND stokminimal IS NOT NULL AND stokminimal > 0";
            sql = overwrite
                ? "INSERT INTO stok_minimal_bangsal (kode_brng, kd_bangsal, stok_minimal, aktif) "
                  + select + " ON DUPLICATE KEY UPDATE stok_minimal = VALUES(stok_minimal), aktif = 1"
                : "INSERT IGNORE INTO stok_minimal_bangsal (kode_brng, kd_bangsal, stok_minimal, aktif) "
                  + select;
        } else {
            String select = "SELECT kode_brng, ?, ?, 1 FROM databarang WHERE status='1'";
            sql = overwrite
                ? "INSERT INTO stok_minimal_bangsal (kode_brng, kd_bangsal, stok_minimal, aktif) "
                  + select + " ON DUPLICATE KEY UPDATE stok_minimal = VALUES(stok_minimal), aktif = 1"
                : "INSERT IGNORE INTO stok_minimal_bangsal (kode_brng, kd_bangsal, stok_minimal, aktif) "
                  + select;
        }

        this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        int affected = 0;
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            if (dariDatabarang) {
                p.setString(1, kdB);
            } else {
                p.setString(1, kdB);
                p.setDouble(2, stokMin);
            }
            affected = p.executeUpdate();
        } catch (SQLException ex) {
            this.setCursor(java.awt.Cursor.getDefaultCursor());
            showErr("Gagal import", ex);
            return;
        }
        this.setCursor(java.awt.Cursor.getDefaultCursor());

        JOptionPane.showMessageDialog(this,
                "Import selesai.\n"
                + "Rows affected: " + affected + "\n"
                + "(Untuk mode overwrite, angka ini termasuk UPDATE.)",
                "Sukses", JOptionPane.INFORMATION_MESSAGE);
        tampilOverride();
    }

    // =================================================================
    // Tab Cari Barang
    // =================================================================
    private void cariBarang() {
        modelCari.setRowCount(0);
        String q = TCariBrng.getText().trim();
        StringBuilder sql = new StringBuilder(
            "SELECT db.kode_brng, db.nama_brng, IFNULL(j.nama,'-') AS jenis, "
            + "       db.kode_sat, db.stokminimal "
            + "FROM databarang db "
            + "LEFT JOIN jenis j ON j.kdjns = db.kdjns "
            + "WHERE db.status='1' ");
        if (!q.isEmpty()) {
            sql.append("AND (db.kode_brng LIKE ? OR db.nama_brng LIKE ?) ");
        }
        sql.append("ORDER BY db.nama_brng");
        try (PreparedStatement p = koneksi.prepareStatement(sql.toString())) {
            if (!q.isEmpty()) {
                p.setString(1, "%" + q + "%");
                p.setString(2, "%" + q + "%");
            }
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    modelCari.addRow(new Object[]{
                        r.getString(1), r.getString(2), r.getString(3), r.getString(4),
                        fmt(r.getObject(5) == null ? 0 : r.getDouble(5))
                    });
                }
            }
        } catch (SQLException ex) {
            showErr("Gagal cari barang", ex);
        }
    }

    private void pilihBarang() {
        int row = tbCari.getSelectedRow();
        if (row < 0) return;
        TKodeBrng.setText(str(modelCari.getValueAt(row, 0)));
        TNamaBrng.setText(str(modelCari.getValueAt(row, 1)));
        TSatuan.setText(str(modelCari.getValueAt(row, 3)));
        TGlobal.setText(str(modelCari.getValueAt(row, 4)));
        TabPane.setSelectedIndex(0);
        TStokMin.requestFocusInWindow();
    }

    // =================================================================
    // Hitung Min-Max otomatis
    //   CA   = total pemakaian (detail_pemberian_obat) / jumlah hari periode
    //   SS   = LT × CA
    //   Smin = 2 × LT × CA   (= 2 × SS)
    //   Smax = Smin + PP × CA
    // =================================================================
    private void hitungMinMax() {
        Date d1 = THAwal.getDate();
        Date d2 = THAkhir.getDate();
        if (d1 == null || d2 == null) {
            JOptionPane.showMessageDialog(this, "Isi periode analisis (tanggal awal & akhir).",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String tgl1 = sdf.format(d1);
        String tgl2 = sdf.format(d2);
        long hari = 0;
        try {
            hari = (sdf.parse(tgl2).getTime() - sdf.parse(tgl1).getTime()) / 86_400_000L + 1;
        } catch (java.text.ParseException ignored) { }
        if (hari <= 0) {
            JOptionPane.showMessageDialog(this, "Tanggal akhir harus sama dengan / setelah tanggal awal.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double lt, pp;
        try {
            lt = Double.parseDouble(THLeadTime.getText().trim());
            pp = Double.parseDouble(THProcPeriod.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Lead Time & Procurement Period harus diisi angka.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (lt < 0 || pp < 0) {
            JOptionPane.showMessageDialog(this, "Lead Time & Procurement Period tidak boleh negatif.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object selB = cbHitungBang.getSelectedItem();
        String kdB = (selB instanceof BangsalItem) ? ((BangsalItem) selB).kd : "";

        // Pemakaian (CA) per barang per depo dari detail_pemberian_obat.
        StringBuilder sql = new StringBuilder(
            "SELECT dpo.kode_brng, dpo.kd_bangsal, db.nama_brng, db.kode_sat, "
            + "       IFNULL(b.nm_bangsal,'') AS nm_bangsal, "
            + "       SUM(dpo.jml) AS total_pakai, "
            + "       IFNULL(gb.stok_kini,0) AS stok_kini, "
            + "       IFNULL(smb.stok_minimal,0) AS min_lama "
            + "FROM detail_pemberian_obat dpo "
            + "JOIN databarang db ON db.kode_brng = dpo.kode_brng AND db.status='1' "
            + "LEFT JOIN bangsal b ON b.kd_bangsal = dpo.kd_bangsal "
            + "LEFT JOIN (SELECT kode_brng, kd_bangsal, SUM(stok) AS stok_kini "
            + "           FROM gudangbarang GROUP BY kode_brng, kd_bangsal) gb "
            + "       ON gb.kode_brng = dpo.kode_brng AND gb.kd_bangsal = dpo.kd_bangsal "
            + "LEFT JOIN stok_minimal_bangsal smb "
            + "       ON smb.kode_brng = dpo.kode_brng AND smb.kd_bangsal = dpo.kd_bangsal "
            + "WHERE dpo.tgl_perawatan BETWEEN ? AND ? "
            + "  AND dpo.kd_bangsal IS NOT NULL AND dpo.kd_bangsal <> ''");
        if (!kdB.isEmpty()) sql.append(" AND dpo.kd_bangsal = ?");
        sql.append(" GROUP BY dpo.kode_brng, dpo.kd_bangsal, db.nama_brng, db.kode_sat, "
                 + "         b.nm_bangsal, gb.stok_kini, smb.stok_minimal "
                 + "ORDER BY b.nm_bangsal, db.nama_brng");

        hasilMinMax.clear();
        modelMinMax.setRowCount(0);
        this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        try (PreparedStatement p = koneksi.prepareStatement(sql.toString())) {
            int idx = 1;
            p.setString(idx++, tgl1);
            p.setString(idx++, tgl2);
            if (!kdB.isEmpty()) p.setString(idx++, kdB);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    double totalPakai = r.getDouble("total_pakai");
                    double ca   = totalPakai / hari;
                    double ss   = Math.ceil(lt * ca);
                    double smin = Math.ceil(2 * lt * ca);     // = 2 × SS
                    double smax = Math.ceil(2 * lt * ca + pp * ca);
                    String nmB  = r.getString("nm_bangsal");
                    MinMaxRow mr = new MinMaxRow(
                        r.getString("kode_brng"), r.getString("nama_brng"),
                        r.getString("kode_sat"),  r.getString("kd_bangsal"), nmB,
                        hari, totalPakai, ca, ss, smin, smax,
                        r.getDouble("stok_kini"), r.getDouble("min_lama"),
                        (int) Math.round(lt), (int) Math.round(pp));
                    hasilMinMax.add(mr);
                    modelMinMax.addRow(new Object[]{
                        mr.kodeBrng, mr.namaBrng, mr.satuan,
                        mr.kdBangsal + (nmB.isEmpty() ? "" : " / " + nmB),
                        hari, fmt(totalPakai), fmt2(ca),
                        fmt(ss), fmt(smin), fmt(smax),
                        fmt(mr.stokKini), fmt(mr.minLama)
                    });
                }
            }
        } catch (SQLException ex) {
            this.setCursor(java.awt.Cursor.getDefaultCursor());
            showErr("Gagal hitung Min-Max", ex);
            return;
        }
        this.setCursor(java.awt.Cursor.getDefaultCursor());

        if (hasilMinMax.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Tidak ada pemakaian obat pada periode " + tgl1 + " s/d " + tgl2
                    + (kdB.isEmpty() ? "" : " untuk bangsal " + kdB) + ".\n"
                    + "(Sumber: detail_pemberian_obat)",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void simpanMinMax() {
        if (hasilMinMax.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Belum ada hasil perhitungan. Tekan tombol Hitung terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
                "Simpan " + hasilMinMax.size() + " baris hasil Min-Max ke stok_minimal_bangsal?\n\n"
                + "• Smin → kolom stok_minimal (override per depo)\n"
                + "• Smax → kolom stok_maksimal\n"
                + "• LT / PP / CA / tgl hitung disimpan sebagai catatan.\n"
                + "• Override lama untuk barang-depo yang sama akan DITIMPA, baris di-set Aktif.",
                "Konfirmasi Simpan Min-Max", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        String sql = "INSERT INTO stok_minimal_bangsal "
                   + "(kode_brng, kd_bangsal, stok_minimal, stok_maksimal, lead_time, "
                   + " procurement_period, consumption_avg, tgl_hitung, aktif) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, CURDATE(), 1) "
                   + "ON DUPLICATE KEY UPDATE "
                   + "  stok_minimal = VALUES(stok_minimal), "
                   + "  stok_maksimal = VALUES(stok_maksimal), "
                   + "  lead_time = VALUES(lead_time), "
                   + "  procurement_period = VALUES(procurement_period), "
                   + "  consumption_avg = VALUES(consumption_avg), "
                   + "  tgl_hitung = VALUES(tgl_hitung), "
                   + "  aktif = 1";

        this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        int n = 0;
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            for (MinMaxRow mr : hasilMinMax) {
                p.setString(1, mr.kodeBrng);
                p.setString(2, mr.kdBangsal);
                p.setDouble(3, mr.smin);
                p.setDouble(4, mr.smax);
                p.setInt(5, mr.leadTime);
                p.setInt(6, mr.procPeriod);
                p.setDouble(7, mr.ca);
                p.addBatch();
                n++;
            }
            p.executeBatch();
        } catch (SQLException ex) {
            this.setCursor(java.awt.Cursor.getDefaultCursor());
            showErr("Gagal simpan Min-Max", ex);
            return;
        }
        this.setCursor(java.awt.Cursor.getDefaultCursor());

        JOptionPane.showMessageDialog(this,
                "Hasil Min-Max tersimpan: " + n + " baris.",
                "Sukses", JOptionPane.INFORMATION_MESSAGE);
        tampilOverride();
    }

    // =================================================================
    // Helpers
    // =================================================================
    private static String str(Object o) { return o == null ? "" : o.toString(); }

    private static String fmt(double d) {
        if (d == (long) d) return String.valueOf((long) d);
        return String.valueOf(d);
    }

    /** Format 2 desimal (dipakai untuk CA / rata-rata pemakaian harian). */
    private static String fmt2(double d) {
        return String.format(java.util.Locale.US, "%.2f", d);
    }

    private static String extractKdBangsal(String cellText) {
        if (cellText == null) return "";
        int idx = cellText.indexOf(" / ");
        return (idx < 0) ? cellText.trim() : cellText.substring(0, idx).trim();
    }

    private static void selectBangsal(widget.ComboBox cb, String kd) {
        for (int i = 0; i < cb.getItemCount(); i++) {
            Object o = cb.getItemAt(i);
            if (o instanceof BangsalItem && ((BangsalItem) o).kd.equals(kd)) {
                cb.setSelectedIndex(i);
                return;
            }
        }
        cb.setSelectedIndex(0);
    }

    private void showErr(String title, Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), title, JOptionPane.ERROR_MESSAGE);
    }

    private static final class BangsalItem {
        final String kd, nm;
        BangsalItem(String kd, String nm) { this.kd = kd; this.nm = nm; }
        @Override public String toString() { return kd.isEmpty() ? nm : (kd + " — " + nm); }
    }

    /** Satu baris hasil perhitungan Min-Max untuk pasangan barang-depo. */
    private static final class MinMaxRow {
        final String kodeBrng, namaBrng, satuan, kdBangsal, nmBangsal;
        final long hari;
        final double totalPakai, ca, ss, smin, smax, stokKini, minLama;
        final int leadTime, procPeriod;
        MinMaxRow(String kodeBrng, String namaBrng, String satuan, String kdBangsal,
                  String nmBangsal, long hari, double totalPakai, double ca,
                  double ss, double smin, double smax, double stokKini, double minLama,
                  int leadTime, int procPeriod) {
            this.kodeBrng = kodeBrng; this.namaBrng = namaBrng; this.satuan = satuan;
            this.kdBangsal = kdBangsal; this.nmBangsal = nmBangsal; this.hari = hari;
            this.totalPakai = totalPakai; this.ca = ca; this.ss = ss;
            this.smin = smin; this.smax = smax; this.stokKini = stokKini;
            this.minLama = minLama; this.leadTime = leadTime; this.procPeriod = procPeriod;
        }
    }

    // =================================================================
    // Components
    // =================================================================
    private widget.InternalFrame internalFrame1;
    private widget.TabPane TabPane;
    // Tab 1
    private javax.swing.JPanel panelOvr;
    private widget.panelisi panelFilter, panelDetail, panelAksi;
    private widget.Label lblFilterBang, lblKeyword, lblKode, lblNama, lblSatuan,
                         lblBangsal, lblStokMin, lblGlobal;
    private widget.ComboBox cbFilterBang, cbBangsal;
    private widget.TextBox TCari, TKodeBrng, TNamaBrng, TSatuan, TStokMin, TGlobal;
    private widget.CekBox chkAktif;
    private widget.ScrollPane ScrollOvr;
    private widget.Table tbOverride;
    private widget.Button BtnCari, BtnAll, BtnLookup, BtnBaru, BtnSimpan, BtnHapus,
                          BtnBatal, BtnRefresh, BtnImportAll;
    // Tab 2
    private javax.swing.JPanel panelBrng;
    private widget.panelisi panelCariBrng, panelBrngHint;
    private widget.Label lblCariBrng, lblHintBrng;
    private widget.TextBox TCariBrng;
    private widget.Button BtnCariBrng;
    private widget.ScrollPane ScrollBrng;
    private widget.Table tbCari;
    // Tab 3 — Hitung Min-Max
    private javax.swing.JPanel panelHitung;
    private widget.panelisi panelParam, panelHitungAksi;
    private widget.Label lblHBang, lblHPeriode, lblHsd, lblHLT, lblHPP, lblHitungInfo;
    private widget.ComboBox cbHitungBang;
    private widget.Tanggal THAwal, THAkhir;
    private widget.TextBox THLeadTime, THProcPeriod;
    private widget.Button BtnHitung, BtnSimpanMinMax;
    private widget.ScrollPane ScrollMinMax;
    private widget.Table tbMinMax;
    // Bottom
    private widget.panelisi panelBottom;
    private widget.Button BtnKeluar;

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgStokMinimalDepo d = new DlgStokMinimalDepo(null, true);
            d.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            d.setVisible(true);
        });
    }
}
