package rekammedis;

import fungsi.koneksiDB;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import widget.*;

/**
 * Master SDKI / SLKI / SIKI
 * Manajemen data master asuhan keperawatan berbasis 3S (SDKI, SLKI, SIKI)
 */
public class MasterAskep3S extends JDialog {

    // -------------------------------------------------------------------------
    // Koneksi & state
    // -------------------------------------------------------------------------
    private Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;

    // ID baris yang sedang terpilih di masing-masing tab
    private int selectedIdSdki = -1;
    private int selectedIdSlki = -1;
    private int selectedIdSiki = -1;

    // -------------------------------------------------------------------------
    // Widget SDKI
    // -------------------------------------------------------------------------
    private widget.Table    tbSdki;
    private DefaultTableModel modelSdki;
    private widget.TextBox  TKodeSdki, TLabelSdki;
    private widget.ComboBox CmbJenisSdki, CmbKategoriSdki;
    private widget.TextArea TADefinisiSdki;

    // -------------------------------------------------------------------------
    // Widget SLKI
    // -------------------------------------------------------------------------
    private widget.Table    tbSlki;
    private DefaultTableModel modelSlki;
    private widget.TextBox  TKodeSlki, TLabelSlki;
    private widget.TextArea TADefinisiSlki;
    private widget.Table    tbIndikator;
    private DefaultTableModel modelIndikator;

    // -------------------------------------------------------------------------
    // Widget SIKI
    // -------------------------------------------------------------------------
    private widget.Table    tbSiki;
    private DefaultTableModel modelSiki;
    private widget.TextBox  TKodeSiki, TLabelSiki;
    private widget.ComboBox CmbTipeSiki;
    private widget.TextArea TADefinisiSiki;
    private widget.Table    tbTindakan;
    private DefaultTableModel modelTindakan;

    // =========================================================================
    // KONSTRUKTOR
    // =========================================================================
    public MasterAskep3S(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setUndecorated(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(1100, 680);
        setLocationRelativeTo(parent);

        initComponents();

        tampilSdki();
        tampilSlki();
        tampilSiki();
        loadKategoriSdki();
    }

    // =========================================================================
    // initComponents – membangun seluruh UI secara programatik
    // =========================================================================
    private void initComponents() {
        // ---- InternalFrame utama --------------------------------------------
        widget.InternalFrame internalFrame1 = new widget.InternalFrame();
        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Master SDKI / SLKI / SIKI ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11),
            new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // ---- JTabbedPane di CENTER ------------------------------------------
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Tahoma", Font.BOLD, 11));
        tabs.setBackground(new Color(255, 255, 255));

        tabs.addTab("  Master SDKI  ", buildTabSdki());
        tabs.addTab("  Master SLKI  ", buildTabSlki());
        tabs.addTab("  Master SIKI  ", buildTabSiki());

        internalFrame1.add(tabs, BorderLayout.CENTER);

        // ---- Panel bawah (BtnKeluar) ----------------------------------------
        widget.panelisi pBottom = new widget.panelisi();
        pBottom.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));
        pBottom.setPreferredSize(new Dimension(1100, 44));

        widget.Button BtnKeluar = new widget.Button();
        BtnKeluar.setIcon(new ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setText("Keluar");
        BtnKeluar.setPreferredSize(new Dimension(100, 30));
        BtnKeluar.addActionListener(e -> dispose());
        pBottom.add(BtnKeluar);

        internalFrame1.add(pBottom, BorderLayout.SOUTH);

        // ---- Tambahkan ke JDialog ------------------------------------------
        setLayout(new BorderLayout());
        add(internalFrame1, BorderLayout.CENTER);
    }

    // =========================================================================
    // TAB 1 – Master SDKI
    // =========================================================================
    private JPanel buildTabSdki() {
        JPanel tab = new JPanel(new BorderLayout(2, 2));
        tab.setOpaque(false);

        // ---- Tabel kiri -----------------------------------------------------
        modelSdki = new DefaultTableModel(null,
            new Object[]{"No", "Kode", "Label", "Jenis", "Kategori", "id"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbSdki = new widget.Table();
        tbSdki.setModel(modelSdki);
        tbSdki.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbSdki.setPreferredScrollableViewportSize(new Dimension(450, 400));

        // Lebar kolom
        int[] wSdki = {35, 70, 180, 70, 110};
        for (int i = 0; i < wSdki.length; i++) {
            tbSdki.getColumnModel().getColumn(i).setPreferredWidth(wSdki[i]);
        }
        // Sembunyikan kolom id (index 5)
        TableColumn colIdSdki = tbSdki.getColumnModel().getColumn(5);
        colIdSdki.setMinWidth(0); colIdSdki.setMaxWidth(0); colIdSdki.setWidth(0);

        tbSdki.setDefaultRenderer(Object.class, new fungsi.WarnaTable());
        tbSdki.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tbSdki.getSelectedRow();
                if (row < 0) return;
                selectedIdSdki = Integer.parseInt(modelSdki.getValueAt(row, 5).toString());
                TKodeSdki.setText(modelSdki.getValueAt(row, 1).toString());
                TLabelSdki.setText(modelSdki.getValueAt(row, 2).toString());
                setComboByItem(CmbJenisSdki, modelSdki.getValueAt(row, 3).toString());
                setComboByItem(CmbKategoriSdki, modelSdki.getValueAt(row, 4).toString());
                loadDefinisiSdki(selectedIdSdki);
            }
        });

        widget.ScrollPane scrollSdki = new widget.ScrollPane();
        scrollSdki.setViewportView(tbSdki);
        scrollSdki.setPreferredSize(new Dimension(470, 500));

        // ---- Form kanan -----------------------------------------------------
        widget.panelisi formSdki = new widget.panelisi();
        formSdki.setLayout(null);
        formSdki.setPreferredSize(new Dimension(590, 500));

        int x = 5, y = 8, h = 23, gap = 30;

        widget.Label lKodeSdki = new widget.Label(); lKodeSdki.setText("Kode :"); lKodeSdki.setBounds(x, y, 80, h); formSdki.add(lKodeSdki);
        TKodeSdki = new widget.TextBox(); TKodeSdki.setBounds(x + 82, y, 80, h); formSdki.add(TKodeSdki);

        y += gap;
        widget.Label lLabelSdki = new widget.Label(); lLabelSdki.setText("Label :"); lLabelSdki.setBounds(x, y, 80, h); formSdki.add(lLabelSdki);
        TLabelSdki = new widget.TextBox(); TLabelSdki.setBounds(x + 82, y, 300, h); formSdki.add(TLabelSdki);

        y += gap;
        widget.Label lJenisSdki = new widget.Label(); lJenisSdki.setText("Jenis :"); lJenisSdki.setBounds(x, y, 80, h); formSdki.add(lJenisSdki);
        CmbJenisSdki = new widget.ComboBox();
        CmbJenisSdki.addItem("aktual");
        CmbJenisSdki.addItem("risiko");
        CmbJenisSdki.addItem("potensial");
        CmbJenisSdki.setBounds(x + 82, y, 150, h);
        formSdki.add(CmbJenisSdki);

        y += gap;
        widget.Label lKategoriSdki = new widget.Label(); lKategoriSdki.setText("Kategori :"); lKategoriSdki.setBounds(x, y, 80, h); formSdki.add(lKategoriSdki);
        CmbKategoriSdki = new widget.ComboBox();
        CmbKategoriSdki.setBounds(x + 82, y, 220, h);
        formSdki.add(CmbKategoriSdki);

        y += gap;
        widget.Label lDefSdki = new widget.Label(); lDefSdki.setText("Definisi :"); lDefSdki.setBounds(x, y, 80, h); formSdki.add(lDefSdki);
        TADefinisiSdki = new widget.TextArea();
        TADefinisiSdki.setRows(4);
        TADefinisiSdki.setLineWrap(true);
        TADefinisiSdki.setWrapStyleWord(true);
        TADefinisiSdki.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JScrollPane scDefSdki = new JScrollPane(TADefinisiSdki);
        scDefSdki.setBounds(x + 82, y, 470, 75);
        formSdki.add(scDefSdki);

        y += 80;

        // ---- Panel tombol CRUD per-tab SDKI ---------------------------------
        widget.panelisi pBotSdki = new widget.panelisi();
        pBotSdki.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));
        pBotSdki.setBounds(0, y, 590, 44);

        widget.Button btnSimpanSdki = new widget.Button();
        btnSimpanSdki.setIcon(new ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        btnSimpanSdki.setText("Simpan");
        btnSimpanSdki.setPreferredSize(new Dimension(100, 30));
        btnSimpanSdki.addActionListener(e -> simpanSdki());
        pBotSdki.add(btnSimpanSdki);

        widget.Button btnHapusSdki = new widget.Button();
        btnHapusSdki.setIcon(new ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        btnHapusSdki.setText("Hapus");
        btnHapusSdki.setPreferredSize(new Dimension(100, 30));
        btnHapusSdki.addActionListener(e -> hapusSdki());
        pBotSdki.add(btnHapusSdki);

        widget.Button btnBaruSdki = new widget.Button();
        btnBaruSdki.setIcon(new ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        btnBaruSdki.setText("Baru");
        btnBaruSdki.setPreferredSize(new Dimension(100, 30));
        btnBaruSdki.addActionListener(e -> bersihSdki());
        pBotSdki.add(btnBaruSdki);

        formSdki.add(pBotSdki);

        // ---- Gabungkan kiri + kanan -----------------------------------------
        JSplitPane splitSdki = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollSdki, formSdki);
        splitSdki.setDividerLocation(475);
        splitSdki.setDividerSize(4);
        splitSdki.setOpaque(false);
        tab.add(splitSdki, BorderLayout.CENTER);

        return tab;
    }

    // =========================================================================
    // TAB 2 – Master SLKI
    // =========================================================================
    private JPanel buildTabSlki() {
        JPanel tab = new JPanel(new BorderLayout(2, 2));
        tab.setOpaque(false);

        // ---- Tabel kiri -----------------------------------------------------
        modelSlki = new DefaultTableModel(null,
            new Object[]{"No", "Kode", "Label", "id"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbSlki = new widget.Table();
        tbSlki.setModel(modelSlki);
        tbSlki.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbSlki.setPreferredScrollableViewportSize(new Dimension(350, 300));

        int[] wSlki = {35, 80, 250};
        for (int i = 0; i < wSlki.length; i++) {
            tbSlki.getColumnModel().getColumn(i).setPreferredWidth(wSlki[i]);
        }
        TableColumn colIdSlki = tbSlki.getColumnModel().getColumn(3);
        colIdSlki.setMinWidth(0); colIdSlki.setMaxWidth(0); colIdSlki.setWidth(0);

        tbSlki.setDefaultRenderer(Object.class, new fungsi.WarnaTable());
        tbSlki.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tbSlki.getSelectedRow();
                if (row < 0) return;
                selectedIdSlki = Integer.parseInt(modelSlki.getValueAt(row, 3).toString());
                TKodeSlki.setText(modelSlki.getValueAt(row, 1).toString());
                TLabelSlki.setText(modelSlki.getValueAt(row, 2).toString());
                loadDefinisiSlki(selectedIdSlki);
                tampilIndikator(selectedIdSlki);
            }
        });

        widget.ScrollPane scrollSlki = new widget.ScrollPane();
        scrollSlki.setViewportView(tbSlki);
        scrollSlki.setPreferredSize(new Dimension(380, 260));

        // ---- Form kanan -----------------------------------------------------
        widget.panelisi formSlki = new widget.panelisi();
        formSlki.setLayout(null);
        formSlki.setPreferredSize(new Dimension(680, 600));

        int x = 5, y = 8, h = 23, gap = 30;

        widget.Label lKodeSlki = new widget.Label(); lKodeSlki.setText("Kode :"); lKodeSlki.setBounds(x, y, 80, h); formSlki.add(lKodeSlki);
        TKodeSlki = new widget.TextBox(); TKodeSlki.setBounds(x + 82, y, 80, h); formSlki.add(TKodeSlki);

        y += gap;
        widget.Label lLabelSlki = new widget.Label(); lLabelSlki.setText("Label :"); lLabelSlki.setBounds(x, y, 80, h); formSlki.add(lLabelSlki);
        TLabelSlki = new widget.TextBox(); TLabelSlki.setBounds(x + 82, y, 300, h); formSlki.add(TLabelSlki);

        y += gap;
        widget.Label lDefSlki = new widget.Label(); lDefSlki.setText("Definisi :"); lDefSlki.setBounds(x, y, 80, h); formSlki.add(lDefSlki);
        TADefinisiSlki = new widget.TextArea();
        TADefinisiSlki.setRows(4);
        TADefinisiSlki.setLineWrap(true);
        TADefinisiSlki.setWrapStyleWord(true);
        TADefinisiSlki.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JScrollPane scDefSlki = new JScrollPane(TADefinisiSlki);
        scDefSlki.setBounds(x + 82, y, 560, 70);
        formSlki.add(scDefSlki);

        y += 75;

        // Sub-tabel indikator
        widget.Label lIndikator = new widget.Label(); lIndikator.setText("Indikator :"); lIndikator.setHorizontalAlignment(SwingConstants.LEFT);
        lIndikator.setBounds(x, y, 80, h); formSlki.add(lIndikator);
        y += gap;

        modelIndikator = new DefaultTableModel(null,
            new Object[]{"Label", "TipeSkala", "Ket1", "Ket5", "id"}) {
            @Override public boolean isCellEditable(int r, int c) { return c < 4; }
        };
        tbIndikator = new widget.Table();
        tbIndikator.setModel(modelIndikator);
        tbIndikator.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbIndikator.setRowHeight(22);

        // Kolom TipeSkala pakai JComboBox editor
        JComboBox<String> cmbSkala = new JComboBox<>(new String[]{"meningkat", "menurun", "membaik"});
        cmbSkala.setFont(new Font("Tahoma", 0, 11));
        tbIndikator.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(cmbSkala));

        int[] wInd = {200, 90, 100, 100};
        for (int i = 0; i < wInd.length; i++) {
            tbIndikator.getColumnModel().getColumn(i).setPreferredWidth(wInd[i]);
        }
        // Sembunyikan kolom id indikator (index 4)
        TableColumn colIdInd = tbIndikator.getColumnModel().getColumn(4);
        colIdInd.setMinWidth(0); colIdInd.setMaxWidth(0); colIdInd.setWidth(0);

        widget.ScrollPane scIndikator = new widget.ScrollPane();
        scIndikator.setViewportView(tbIndikator);
        scIndikator.setBounds(x, y, 640, 140);
        formSlki.add(scIndikator);

        y += 145;

        // Tombol tambah/hapus baris indikator
        widget.Button btnTambahInd = new widget.Button();
        btnTambahInd.setText("+ Baris");
        btnTambahInd.setPreferredSize(new Dimension(90, 26));
        btnTambahInd.setBounds(x, y, 90, 26);
        btnTambahInd.addActionListener(e -> modelIndikator.addRow(new Object[]{"", "meningkat", "", "", ""}));
        formSlki.add(btnTambahInd);

        widget.Button btnHapusInd = new widget.Button();
        btnHapusInd.setText("- Baris");
        btnHapusInd.setPreferredSize(new Dimension(90, 26));
        btnHapusInd.setBounds(x + 95, y, 90, 26);
        btnHapusInd.addActionListener(e -> {
            int sel = tbIndikator.getSelectedRow();
            if (sel >= 0) modelIndikator.removeRow(sel);
        });
        formSlki.add(btnHapusInd);

        y += 32;

        // Tombol CRUD SLKI
        widget.panelisi pBotSlki = new widget.panelisi();
        pBotSlki.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));
        pBotSlki.setBounds(0, y, 680, 44);

        widget.Button btnSimpanSlki = new widget.Button();
        btnSimpanSlki.setIcon(new ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        btnSimpanSlki.setText("Simpan");
        btnSimpanSlki.setPreferredSize(new Dimension(100, 30));
        btnSimpanSlki.addActionListener(e -> simpanSlki());
        pBotSlki.add(btnSimpanSlki);

        widget.Button btnHapusSlki = new widget.Button();
        btnHapusSlki.setIcon(new ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        btnHapusSlki.setText("Hapus");
        btnHapusSlki.setPreferredSize(new Dimension(100, 30));
        btnHapusSlki.addActionListener(e -> hapusSlki());
        pBotSlki.add(btnHapusSlki);

        widget.Button btnBaruSlki = new widget.Button();
        btnBaruSlki.setIcon(new ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        btnBaruSlki.setText("Baru");
        btnBaruSlki.setPreferredSize(new Dimension(100, 30));
        btnBaruSlki.addActionListener(e -> bersihSlki());
        pBotSlki.add(btnBaruSlki);

        formSlki.add(pBotSlki);

        // ---- Gabungkan ------------------------------------------------------
        JSplitPane splitSlki = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollSlki, formSlki);
        splitSlki.setDividerLocation(385);
        splitSlki.setDividerSize(4);
        splitSlki.setOpaque(false);
        tab.add(splitSlki, BorderLayout.CENTER);

        return tab;
    }

    // =========================================================================
    // TAB 3 – Master SIKI
    // =========================================================================
    private JPanel buildTabSiki() {
        JPanel tab = new JPanel(new BorderLayout(2, 2));
        tab.setOpaque(false);

        // ---- Tabel kiri -----------------------------------------------------
        modelSiki = new DefaultTableModel(null,
            new Object[]{"No", "Kode", "Label", "Tipe", "id"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbSiki = new widget.Table();
        tbSiki.setModel(modelSiki);
        tbSiki.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbSiki.setPreferredScrollableViewportSize(new Dimension(370, 300));

        int[] wSiki = {35, 70, 200, 70};
        for (int i = 0; i < wSiki.length; i++) {
            tbSiki.getColumnModel().getColumn(i).setPreferredWidth(wSiki[i]);
        }
        TableColumn colIdSiki = tbSiki.getColumnModel().getColumn(4);
        colIdSiki.setMinWidth(0); colIdSiki.setMaxWidth(0); colIdSiki.setWidth(0);

        tbSiki.setDefaultRenderer(Object.class, new fungsi.WarnaTable());
        tbSiki.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tbSiki.getSelectedRow();
                if (row < 0) return;
                selectedIdSiki = Integer.parseInt(modelSiki.getValueAt(row, 4).toString());
                TKodeSiki.setText(modelSiki.getValueAt(row, 1).toString());
                TLabelSiki.setText(modelSiki.getValueAt(row, 2).toString());
                setComboByItem(CmbTipeSiki, modelSiki.getValueAt(row, 3).toString());
                loadDefinisiSiki(selectedIdSiki);
                tampilTindakan(selectedIdSiki);
            }
        });

        widget.ScrollPane scrollSiki = new widget.ScrollPane();
        scrollSiki.setViewportView(tbSiki);
        scrollSiki.setPreferredSize(new Dimension(390, 260));

        // ---- Form kanan -----------------------------------------------------
        widget.panelisi formSiki = new widget.panelisi();
        formSiki.setLayout(null);
        formSiki.setPreferredSize(new Dimension(680, 600));

        int x = 5, y = 8, h = 23, gap = 30;

        widget.Label lKodeSiki = new widget.Label(); lKodeSiki.setText("Kode :"); lKodeSiki.setBounds(x, y, 80, h); formSiki.add(lKodeSiki);
        TKodeSiki = new widget.TextBox(); TKodeSiki.setBounds(x + 82, y, 80, h); formSiki.add(TKodeSiki);

        y += gap;
        widget.Label lLabelSiki = new widget.Label(); lLabelSiki.setText("Label :"); lLabelSiki.setBounds(x, y, 80, h); formSiki.add(lLabelSiki);
        TLabelSiki = new widget.TextBox(); TLabelSiki.setBounds(x + 82, y, 300, h); formSiki.add(TLabelSiki);

        y += gap;
        widget.Label lTipeSiki = new widget.Label(); lTipeSiki.setText("Tipe :"); lTipeSiki.setBounds(x, y, 80, h); formSiki.add(lTipeSiki);
        CmbTipeSiki = new widget.ComboBox();
        CmbTipeSiki.addItem("utama");
        CmbTipeSiki.addItem("pendukung");
        CmbTipeSiki.setBounds(x + 82, y, 130, h);
        formSiki.add(CmbTipeSiki);

        y += gap;
        widget.Label lDefSiki = new widget.Label(); lDefSiki.setText("Definisi :"); lDefSiki.setBounds(x, y, 80, h); formSiki.add(lDefSiki);
        TADefinisiSiki = new widget.TextArea();
        TADefinisiSiki.setRows(3);
        TADefinisiSiki.setLineWrap(true);
        TADefinisiSiki.setWrapStyleWord(true);
        TADefinisiSiki.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(200, 200, 200)));
        JScrollPane scDefSiki = new JScrollPane(TADefinisiSiki);
        scDefSiki.setBounds(x + 82, y, 560, 60);
        formSiki.add(scDefSiki);

        y += 65;

        widget.Label lTindakan = new widget.Label(); lTindakan.setText("Tindakan :"); lTindakan.setHorizontalAlignment(SwingConstants.LEFT);
        lTindakan.setBounds(x, y, 80, h); formSiki.add(lTindakan);
        y += gap;

        // Sub-tabel tindakan
        modelTindakan = new DefaultTableModel(null,
            new Object[]{"Komponen", "Deskripsi", "id"}) {
            @Override public boolean isCellEditable(int r, int c) { return c < 2; }
        };
        tbTindakan = new widget.Table();
        tbTindakan.setModel(modelTindakan);
        tbTindakan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbTindakan.setRowHeight(22);

        JComboBox<String> cmbKomp = new JComboBox<>(
            new String[]{"observasi", "terapeutik", "edukasi", "kolaborasi"});
        cmbKomp.setFont(new Font("Tahoma", 0, 11));
        tbTindakan.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(cmbKomp));

        tbTindakan.getColumnModel().getColumn(0).setPreferredWidth(100);
        tbTindakan.getColumnModel().getColumn(1).setPreferredWidth(490);
        TableColumn colIdTind = tbTindakan.getColumnModel().getColumn(2);
        colIdTind.setMinWidth(0); colIdTind.setMaxWidth(0); colIdTind.setWidth(0);

        widget.ScrollPane scTindakan = new widget.ScrollPane();
        scTindakan.setViewportView(tbTindakan);
        scTindakan.setBounds(x, y, 640, 150);
        formSiki.add(scTindakan);

        y += 155;

        widget.Button btnTambahTind = new widget.Button();
        btnTambahTind.setText("+ Baris");
        btnTambahTind.setBounds(x, y, 90, 26);
        btnTambahTind.addActionListener(e -> modelTindakan.addRow(new Object[]{"observasi", "", ""}));
        formSiki.add(btnTambahTind);

        widget.Button btnHapusTind = new widget.Button();
        btnHapusTind.setText("- Baris");
        btnHapusTind.setBounds(x + 95, y, 90, 26);
        btnHapusTind.addActionListener(e -> {
            int sel = tbTindakan.getSelectedRow();
            if (sel >= 0) modelTindakan.removeRow(sel);
        });
        formSiki.add(btnHapusTind);

        y += 32;

        widget.panelisi pBotSiki = new widget.panelisi();
        pBotSiki.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));
        pBotSiki.setBounds(0, y, 680, 44);

        widget.Button btnSimpanSiki = new widget.Button();
        btnSimpanSiki.setIcon(new ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        btnSimpanSiki.setText("Simpan");
        btnSimpanSiki.setPreferredSize(new Dimension(100, 30));
        btnSimpanSiki.addActionListener(e -> simpanSiki());
        pBotSiki.add(btnSimpanSiki);

        widget.Button btnHapusSiki = new widget.Button();
        btnHapusSiki.setIcon(new ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        btnHapusSiki.setText("Hapus");
        btnHapusSiki.setPreferredSize(new Dimension(100, 30));
        btnHapusSiki.addActionListener(e -> hapusSiki());
        pBotSiki.add(btnHapusSiki);

        widget.Button btnBaruSiki = new widget.Button();
        btnBaruSiki.setIcon(new ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        btnBaruSiki.setText("Baru");
        btnBaruSiki.setPreferredSize(new Dimension(100, 30));
        btnBaruSiki.addActionListener(e -> bersihSiki());
        pBotSiki.add(btnBaruSiki);

        formSiki.add(pBotSiki);

        // ---- Gabungkan ------------------------------------------------------
        JSplitPane splitSiki = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollSiki, formSiki);
        splitSiki.setDividerLocation(395);
        splitSiki.setDividerSize(4);
        splitSiki.setOpaque(false);
        tab.add(splitSiki, BorderLayout.CENTER);

        return tab;
    }

    // =========================================================================
    // METODE – Tampil data
    // =========================================================================

    private void tampilSdki() {
        modelSdki.setRowCount(0);
        try {
            ps = koneksi.prepareStatement(
                "SELECT s.id, s.kode, s.label, s.jenis, k.nama " +
                "FROM mst_sdki s LEFT JOIN mst_sdki_kategori k ON k.id = s.id_kategori " +
                "ORDER BY s.kode");
            rs = ps.executeQuery();
            int no = 1;
            while (rs.next()) {
                modelSdki.addRow(new Object[]{
                    no++,
                    rs.getString("kode"),
                    rs.getString("label"),
                    rs.getString("jenis"),
                    rs.getString("nama"),
                    rs.getInt("id")
                });
            }
            rs.close(); ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal load SDKI:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tampilSlki() {
        modelSlki.setRowCount(0);
        try {
            ps = koneksi.prepareStatement(
                "SELECT id, kode, label FROM mst_slki ORDER BY kode");
            rs = ps.executeQuery();
            int no = 1;
            while (rs.next()) {
                modelSlki.addRow(new Object[]{
                    no++,
                    rs.getString("kode"),
                    rs.getString("label"),
                    rs.getInt("id")
                });
            }
            rs.close(); ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal load SLKI:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tampilSiki() {
        modelSiki.setRowCount(0);
        try {
            ps = koneksi.prepareStatement(
                "SELECT id, kode, label, tipe FROM mst_siki ORDER BY kode");
            rs = ps.executeQuery();
            int no = 1;
            while (rs.next()) {
                modelSiki.addRow(new Object[]{
                    no++,
                    rs.getString("kode"),
                    rs.getString("label"),
                    rs.getString("tipe"),
                    rs.getInt("id")
                });
            }
            rs.close(); ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal load SIKI:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tampilIndikator(int idSlki) {
        modelIndikator.setRowCount(0);
        try {
            ps = koneksi.prepareStatement(
                "SELECT id, label, tipe_skala, keterangan_1, keterangan_5 " +
                "FROM mst_slki_indikator WHERE id_slki = ? ORDER BY urutan");
            ps.setInt(1, idSlki);
            rs = ps.executeQuery();
            while (rs.next()) {
                modelIndikator.addRow(new Object[]{
                    rs.getString("label"),
                    rs.getString("tipe_skala"),
                    rs.getString("keterangan_1"),
                    rs.getString("keterangan_5"),
                    rs.getInt("id")
                });
            }
            rs.close(); ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal load indikator:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void tampilTindakan(int idSiki) {
        modelTindakan.setRowCount(0);
        try {
            ps = koneksi.prepareStatement(
                "SELECT id, komponen, deskripsi FROM mst_siki_tindakan " +
                "WHERE id_siki = ? ORDER BY komponen, urutan");
            ps.setInt(1, idSiki);
            rs = ps.executeQuery();
            while (rs.next()) {
                modelTindakan.addRow(new Object[]{
                    rs.getString("komponen"),
                    rs.getString("deskripsi"),
                    rs.getInt("id")
                });
            }
            rs.close(); ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal load tindakan:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // METODE – Load detail definisi (hanya definisi, disimpan di kolom 'definisi')
    // =========================================================================

    private void loadDefinisiSdki(int id) {
        try {
            ps = koneksi.prepareStatement("SELECT definisi FROM mst_sdki WHERE id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) TADefinisiSdki.setText(rs.getString("definisi"));
            rs.close(); ps.close();
        } catch (SQLException ex) { /* kolom mungkin belum ada – abaikan */ }
    }

    private void loadDefinisiSlki(int id) {
        try {
            ps = koneksi.prepareStatement("SELECT definisi FROM mst_slki WHERE id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) TADefinisiSlki.setText(rs.getString("definisi"));
            rs.close(); ps.close();
        } catch (SQLException ex) { /* abaikan */ }
    }

    private void loadDefinisiSiki(int id) {
        try {
            ps = koneksi.prepareStatement("SELECT definisi FROM mst_siki WHERE id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) TADefinisiSiki.setText(rs.getString("definisi"));
            rs.close(); ps.close();
        } catch (SQLException ex) { /* abaikan */ }
    }

    // =========================================================================
    // METODE – Load kategori SDKI ke ComboBox
    // =========================================================================

    private void loadKategoriSdki() {
        CmbKategoriSdki.removeAllItems();
        CmbKategoriSdki.addItem("");
        try {
            ps = koneksi.prepareStatement(
                "SELECT nama FROM mst_sdki_kategori ORDER BY nama");
            rs = ps.executeQuery();
            while (rs.next()) {
                CmbKategoriSdki.addItem(rs.getString("nama"));
            }
            rs.close(); ps.close();
        } catch (SQLException ex) {
            // Tabel mungkin belum ada – biarkan kosong
        }
    }

    // =========================================================================
    // METODE – Simpan
    // =========================================================================

    private void simpanSdki() {
        String kode    = TKodeSdki.getText().trim();
        String label   = TLabelSdki.getText().trim();
        String jenis   = CmbJenisSdki.getSelectedItem() != null ? CmbJenisSdki.getSelectedItem().toString() : "";
        String kategori = CmbKategoriSdki.getSelectedItem() != null ? CmbKategoriSdki.getSelectedItem().toString() : "";
        String definisi = TADefinisiSdki.getText().trim();

        if (kode.isEmpty() || label.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kode dan Label tidak boleh kosong.",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            // Cari id_kategori
            int idKategori = 0;
            ps = koneksi.prepareStatement("SELECT id FROM mst_sdki_kategori WHERE nama = ?");
            ps.setString(1, kategori);
            rs = ps.executeQuery();
            if (rs.next()) idKategori = rs.getInt("id");
            rs.close(); ps.close();

            if (selectedIdSdki <= 0) {
                // INSERT IGNORE
                ps = koneksi.prepareStatement(
                    "INSERT IGNORE INTO mst_sdki (kode, label, jenis, id_kategori, definisi) " +
                    "VALUES (?, ?, ?, ?, ?)");
                ps.setString(1, kode);
                ps.setString(2, label);
                ps.setString(3, jenis);
                ps.setInt(4, idKategori);
                ps.setString(5, definisi);
            } else {
                // UPDATE
                ps = koneksi.prepareStatement(
                    "UPDATE mst_sdki SET kode=?, label=?, jenis=?, id_kategori=?, definisi=? WHERE id=?");
                ps.setString(1, kode);
                ps.setString(2, label);
                ps.setString(3, jenis);
                ps.setInt(4, idKategori);
                ps.setString(5, definisi);
                ps.setInt(6, selectedIdSdki);
            }
            ps.executeUpdate(); ps.close();
            JOptionPane.showMessageDialog(this, "Data SDKI berhasil disimpan.");
            bersihSdki();
            tampilSdki();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal simpan SDKI:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void simpanSlki() {
        String kode    = TKodeSlki.getText().trim();
        String label   = TLabelSlki.getText().trim();
        String definisi = TADefinisiSlki.getText().trim();

        if (kode.isEmpty() || label.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kode dan Label tidak boleh kosong.",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int idSlkiSaved;
            if (selectedIdSlki <= 0) {
                ps = koneksi.prepareStatement(
                    "INSERT IGNORE INTO mst_slki (kode, label, definisi) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, kode);
                ps.setString(2, label);
                ps.setString(3, definisi);
                ps.executeUpdate();
                ResultSet gk = ps.getGeneratedKeys();
                idSlkiSaved = gk.next() ? gk.getInt(1) : -1;
                gk.close(); ps.close();
            } else {
                ps = koneksi.prepareStatement(
                    "UPDATE mst_slki SET kode=?, label=?, definisi=? WHERE id=?");
                ps.setString(1, kode);
                ps.setString(2, label);
                ps.setString(3, definisi);
                ps.setInt(4, selectedIdSlki);
                ps.executeUpdate(); ps.close();
                idSlkiSaved = selectedIdSlki;
            }
            // Simpan indikator: hapus lama, insert baru
            if (idSlkiSaved > 0) {
                ps = koneksi.prepareStatement(
                    "DELETE FROM mst_slki_indikator WHERE id_slki = ?");
                ps.setInt(1, idSlkiSaved);
                ps.executeUpdate(); ps.close();

                for (int i = 0; i < modelIndikator.getRowCount(); i++) {
                    String iLabel  = objStr(modelIndikator.getValueAt(i, 0));
                    String iTipe   = objStr(modelIndikator.getValueAt(i, 1));
                    String iKet1   = objStr(modelIndikator.getValueAt(i, 2));
                    String iKet5   = objStr(modelIndikator.getValueAt(i, 3));
                    ps = koneksi.prepareStatement(
                        "INSERT INTO mst_slki_indikator (id_slki, label, tipe_skala, keterangan_1, keterangan_5, urutan) " +
                        "VALUES (?, ?, ?, ?, ?, ?)");
                    ps.setInt(1, idSlkiSaved);
                    ps.setString(2, iLabel);
                    ps.setString(3, iTipe);
                    ps.setString(4, iKet1);
                    ps.setString(5, iKet5);
                    ps.setInt(6, i + 1);
                    ps.executeUpdate(); ps.close();
                }
            }
            JOptionPane.showMessageDialog(this, "Data SLKI berhasil disimpan.");
            bersihSlki();
            tampilSlki();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal simpan SLKI:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void simpanSiki() {
        String kode    = TKodeSiki.getText().trim();
        String label   = TLabelSiki.getText().trim();
        String tipe    = CmbTipeSiki.getSelectedItem() != null ? CmbTipeSiki.getSelectedItem().toString() : "utama";
        String definisi = TADefinisiSiki.getText().trim();

        if (kode.isEmpty() || label.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kode dan Label tidak boleh kosong.",
                "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int idSikiSaved;
            if (selectedIdSiki <= 0) {
                ps = koneksi.prepareStatement(
                    "INSERT IGNORE INTO mst_siki (kode, label, tipe, definisi) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, kode);
                ps.setString(2, label);
                ps.setString(3, tipe);
                ps.setString(4, definisi);
                ps.executeUpdate();
                ResultSet gk = ps.getGeneratedKeys();
                idSikiSaved = gk.next() ? gk.getInt(1) : -1;
                gk.close(); ps.close();
            } else {
                ps = koneksi.prepareStatement(
                    "UPDATE mst_siki SET kode=?, label=?, tipe=?, definisi=? WHERE id=?");
                ps.setString(1, kode);
                ps.setString(2, label);
                ps.setString(3, tipe);
                ps.setString(4, definisi);
                ps.setInt(5, selectedIdSiki);
                ps.executeUpdate(); ps.close();
                idSikiSaved = selectedIdSiki;
            }
            // Simpan tindakan: hapus lama, insert baru
            if (idSikiSaved > 0) {
                ps = koneksi.prepareStatement(
                    "DELETE FROM mst_siki_tindakan WHERE id_siki = ?");
                ps.setInt(1, idSikiSaved);
                ps.executeUpdate(); ps.close();

                for (int i = 0; i < modelTindakan.getRowCount(); i++) {
                    String tKomp  = objStr(modelTindakan.getValueAt(i, 0));
                    String tDesk  = objStr(modelTindakan.getValueAt(i, 1));
                    ps = koneksi.prepareStatement(
                        "INSERT INTO mst_siki_tindakan (id_siki, komponen, deskripsi, urutan) " +
                        "VALUES (?, ?, ?, ?)");
                    ps.setInt(1, idSikiSaved);
                    ps.setString(2, tKomp);
                    ps.setString(3, tDesk);
                    ps.setInt(4, i + 1);
                    ps.executeUpdate(); ps.close();
                }
            }
            JOptionPane.showMessageDialog(this, "Data SIKI berhasil disimpan.");
            bersihSiki();
            tampilSiki();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal simpan SIKI:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // METODE – Hapus
    // =========================================================================

    private void hapusSdki() {
        if (selectedIdSdki <= 0) {
            JOptionPane.showMessageDialog(this, "Pilih data SDKI terlebih dahulu.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Hapus data SDKI ini?", "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            ps = koneksi.prepareStatement("DELETE FROM mst_sdki WHERE id = ?");
            ps.setInt(1, selectedIdSdki);
            ps.executeUpdate(); ps.close();
            JOptionPane.showMessageDialog(this, "Data SDKI berhasil dihapus.");
            bersihSdki();
            tampilSdki();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal hapus SDKI:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusSlki() {
        if (selectedIdSlki <= 0) {
            JOptionPane.showMessageDialog(this, "Pilih data SLKI terlebih dahulu.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Hapus data SLKI beserta semua indikatornya?", "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            ps = koneksi.prepareStatement(
                "DELETE FROM mst_slki_indikator WHERE id_slki = ?");
            ps.setInt(1, selectedIdSlki); ps.executeUpdate(); ps.close();

            ps = koneksi.prepareStatement("DELETE FROM mst_slki WHERE id = ?");
            ps.setInt(1, selectedIdSlki); ps.executeUpdate(); ps.close();

            JOptionPane.showMessageDialog(this, "Data SLKI berhasil dihapus.");
            bersihSlki();
            tampilSlki();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal hapus SLKI:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusSiki() {
        if (selectedIdSiki <= 0) {
            JOptionPane.showMessageDialog(this, "Pilih data SIKI terlebih dahulu.",
                "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Hapus data SIKI beserta semua tindakannya?", "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            ps = koneksi.prepareStatement(
                "DELETE FROM mst_siki_tindakan WHERE id_siki = ?");
            ps.setInt(1, selectedIdSiki); ps.executeUpdate(); ps.close();

            ps = koneksi.prepareStatement("DELETE FROM mst_siki WHERE id = ?");
            ps.setInt(1, selectedIdSiki); ps.executeUpdate(); ps.close();

            JOptionPane.showMessageDialog(this, "Data SIKI berhasil dihapus.");
            bersihSiki();
            tampilSiki();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Gagal hapus SIKI:\n" + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // METODE – Bersihkan form
    // =========================================================================

    private void bersihSdki() {
        selectedIdSdki = -1;
        TKodeSdki.setText("");
        TLabelSdki.setText("");
        if (CmbJenisSdki.getItemCount() > 0) CmbJenisSdki.setSelectedIndex(0);
        if (CmbKategoriSdki.getItemCount() > 0) CmbKategoriSdki.setSelectedIndex(0);
        TADefinisiSdki.setText("");
    }

    private void bersihSlki() {
        selectedIdSlki = -1;
        TKodeSlki.setText("");
        TLabelSlki.setText("");
        TADefinisiSlki.setText("");
        modelIndikator.setRowCount(0);
    }

    private void bersihSiki() {
        selectedIdSiki = -1;
        TKodeSiki.setText("");
        TLabelSiki.setText("");
        if (CmbTipeSiki.getItemCount() > 0) CmbTipeSiki.setSelectedIndex(0);
        TADefinisiSiki.setText("");
        modelTindakan.setRowCount(0);
    }

    // =========================================================================
    // UTILITAS
    // =========================================================================

    /** Set pilihan ComboBox berdasarkan teks item (case-insensitive). */
    private void setComboByItem(JComboBox<?> cmb, String val) {
        if (val == null) return;
        for (int i = 0; i < cmb.getItemCount(); i++) {
            if (cmb.getItemAt(i).toString().equalsIgnoreCase(val)) {
                cmb.setSelectedIndex(i);
                return;
            }
        }
    }

    /** Konversi Object ke String, null-safe. */
    private String objStr(Object o) {
        return o == null ? "" : o.toString();
    }
}
