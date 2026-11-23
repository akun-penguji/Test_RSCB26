/*
 * DlgMasterLoinc.java
 * Master data LOINC (Logical Observation Identifiers Names and Codes) v2.81
 */
package simrskhanza;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * Master LOINC v2.81 — CRUD + Paging
 * @author simrs
 */
public final class DlgMasterLoinc extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;

    private int currentPage = 0;
    private final int PAGE_SIZE = 100;
    private int totalRecords = 0;

    public DlgMasterLoinc(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(10, 10);
        setSize(1000, 640);

        tabMode = new DefaultTableModel(null, new Object[]{
            "Kode LOINC", "Nama Pemeriksaan", "Komponen", "Sistem/Spesimen",
            "Kategori", "Satuan", "Metode"
        }) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tbLoinc.setModel(tabMode);
        tbLoinc.setAutoCreateRowSorter(true);
        tbLoinc.setPreferredScrollableViewportSize(new Dimension(970, 420));
        tbLoinc.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] colWidths = {90, 350, 170, 120, 80, 80, 80};
        for (i = 0; i < colWidths.length; i++) {
            tbLoinc.getColumnModel().getColumn(i).setPreferredWidth(colWidths[i]);
        }
        tbLoinc.setDefaultRenderer(Object.class, new WarnaTable());

        TKode.setDocument(new batasInput((byte) 20).getKata(TKode));
        TNama.setDocument(new batasInput((byte) 200).getKata(TNama));
        TKomponen.setDocument(new batasInput((byte) 200).getKata(TKomponen));
        TSpesimen.setDocument(new batasInput((byte) 100).getKata(TSpesimen));
        TKategori.setDocument(new batasInput((byte) 50).getKata(TKategori));
        TSatuan.setDocument(new batasInput((byte) 50).getKata(TSatuan));
        TMetode.setDocument(new batasInput((byte) 50).getKata(TMetode));
        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));

        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override public void insertUpdate(DocumentEvent e) { if (TCari.getText().length() > 2) { currentPage = 0; tampil(); } }
                @Override public void removeUpdate(DocumentEvent e) { if (TCari.getText().length() > 2) { currentPage = 0; tampil(); } }
                @Override public void changedUpdate(DocumentEvent e) {}
            });
        }
        TKode.requestFocus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1  = new widget.InternalFrame();
        panelGlassForm  = new widget.panelisi();
        lbKode          = new widget.Label();
        TKode           = new widget.TextBox();
        lbNama          = new widget.Label();
        TNama           = new widget.TextBox();
        lbKomponen      = new widget.Label();
        TKomponen       = new widget.TextBox();
        lbSpesimen      = new widget.Label();
        TSpesimen       = new widget.TextBox();
        lbKategori      = new widget.Label();
        TKategori       = new widget.TextBox();
        lbSatuan        = new widget.Label();
        TSatuan         = new widget.TextBox();
        lbMetode        = new widget.Label();
        TMetode         = new widget.TextBox();
        Scroll          = new widget.ScrollPane();
        tbLoinc         = new widget.Table();
        jPanel1         = new javax.swing.JPanel();
        panelGlassCari  = new widget.panelisi();
        lbCari          = new widget.Label();
        TCari           = new widget.TextBox();
        BtnCari         = new widget.Button();
        BtnAll          = new widget.Button();
        lbRecord        = new widget.Label();
        LCount          = new widget.Label();
        panelGlassPaging= new widget.panelisi();
        BtnPrev         = new widget.Button();
        LHalaman        = new widget.Label();
        BtnNext         = new widget.Button();
        panelGlassAksi  = new widget.panelisi();
        BtnSimpan       = new widget.Button();
        BtnBatal        = new widget.Button();
        BtnHapus        = new widget.Button();
        BtnEdit         = new widget.Button();
        BtnPilih        = new widget.Button();
        BtnKeluar       = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(null);
        setIconImages(null);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
            "::[ Master LOINC - Logical Observation Identifiers Names and Codes v2.81 ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));
        internalFrame1.setFont(new java.awt.Font("Tahoma", 0, 12));
        internalFrame1.setName("internalFrame1");
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        // ── PANEL FORM (PAGE_START — atas tabel) ─────────────────────
        panelGlassForm.setName("panelGlassForm");
        panelGlassForm.setPreferredSize(new java.awt.Dimension(44, 78));
        panelGlassForm.setLayout(null);

        lbKode.setText("Kode LOINC :");
        lbKode.setName("lbKode");
        panelGlassForm.add(lbKode);
        lbKode.setBounds(5, 8, 90, 23);

        TKode.setName("TKode");
        TKode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) { TKodeKeyPressed(evt); }
        });
        panelGlassForm.add(TKode);
        TKode.setBounds(98, 8, 100, 23);

        lbKategori.setText("Kategori :");
        lbKategori.setName("lbKategori");
        panelGlassForm.add(lbKategori);
        lbKategori.setBounds(210, 8, 65, 23);

        TKategori.setName("TKategori");
        TKategori.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) { TKategoriKeyPressed(evt); }
        });
        panelGlassForm.add(TKategori);
        TKategori.setBounds(278, 8, 110, 23);

        lbSatuan.setText("Satuan :");
        lbSatuan.setName("lbSatuan");
        panelGlassForm.add(lbSatuan);
        lbSatuan.setBounds(400, 8, 55, 23);

        TSatuan.setName("TSatuan");
        TSatuan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) { TSatuanKeyPressed(evt); }
        });
        panelGlassForm.add(TSatuan);
        TSatuan.setBounds(458, 8, 110, 23);

        lbMetode.setText("Metode :");
        lbMetode.setName("lbMetode");
        panelGlassForm.add(lbMetode);
        lbMetode.setBounds(580, 8, 60, 23);

        TMetode.setName("TMetode");
        TMetode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) { TMetodeKeyPressed(evt); }
        });
        panelGlassForm.add(TMetode);
        TMetode.setBounds(643, 8, 110, 23);

        lbNama.setText("Nama Pem. :");
        lbNama.setName("lbNama");
        panelGlassForm.add(lbNama);
        lbNama.setBounds(5, 38, 90, 23);

        TNama.setName("TNama");
        TNama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) { TNamaKeyPressed(evt); }
        });
        panelGlassForm.add(TNama);
        TNama.setBounds(98, 38, 330, 23);

        lbKomponen.setText("Komponen :");
        lbKomponen.setName("lbKomponen");
        panelGlassForm.add(lbKomponen);
        lbKomponen.setBounds(440, 38, 75, 23);

        TKomponen.setName("TKomponen");
        TKomponen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) { TKomponenKeyPressed(evt); }
        });
        panelGlassForm.add(TKomponen);
        TKomponen.setBounds(518, 38, 170, 23);

        lbSpesimen.setText("Spesimen :");
        lbSpesimen.setName("lbSpesimen");
        panelGlassForm.add(lbSpesimen);
        lbSpesimen.setBounds(700, 38, 70, 23);

        TSpesimen.setName("TSpesimen");
        TSpesimen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) { TSpesimenKeyPressed(evt); }
        });
        panelGlassForm.add(TSpesimen);
        TSpesimen.setBounds(773, 38, 150, 23);

        internalFrame1.add(panelGlassForm, java.awt.BorderLayout.PAGE_START);

        // ── TABEL (CENTER) ────────────────────────────────────────────
        Scroll.setName("Scroll");
        Scroll.setOpaque(true);
        tbLoinc.setToolTipText("Klik untuk memilih data");
        tbLoinc.setName("tbLoinc");
        tbLoinc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { tbLoincMouseClicked(evt); }
        });
        tbLoinc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt)  { tbLoincKeyPressed(evt);  }
            public void keyReleased(java.awt.event.KeyEvent evt) { tbLoincKeyReleased(evt); }
        });
        Scroll.setViewportView(tbLoinc);
        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        // ── PANEL BAWAH (PAGE_END) ────────────────────────────────────
        jPanel1.setName("jPanel1");
        jPanel1.setOpaque(false);
        jPanel1.setLayout(new java.awt.BorderLayout(1, 1));

        // Panel keyword cari (PAGE_START dalam jPanel1)
        panelGlassCari.setName("panelGlassCari");
        panelGlassCari.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlassCari.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        lbCari.setText("Key Word :");
        lbCari.setName("lbCari");
        lbCari.setPreferredSize(new java.awt.Dimension(70, 23));
        lbCari.setRequestFocusEnabled(false);
        panelGlassCari.add(lbCari);

        TCari.setName("TCari");
        TCari.setPreferredSize(new java.awt.Dimension(350, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) { TCariKeyPressed(evt); }
        });
        panelGlassCari.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setMnemonic('1');
        BtnCari.setToolTipText("Alt+1");
        BtnCari.setName("BtnCari");
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { currentPage = 0; tampil(); }
        });
        BtnCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) { BtnCariKeyPressed(evt); }
        });
        panelGlassCari.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setMnemonic('2');
        BtnAll.setToolTipText("Alt+2");
        BtnAll.setName("BtnAll");
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { TCari.setText(""); currentPage = 0; tampil(); }
        });
        BtnAll.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) { BtnAllKeyPressed(evt); }
        });
        panelGlassCari.add(BtnAll);

        lbRecord.setText("Record :");
        lbRecord.setName("lbRecord");
        lbRecord.setPreferredSize(new java.awt.Dimension(55, 23));
        panelGlassCari.add(lbRecord);

        LCount.setText("0");
        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setName("LCount");
        LCount.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlassCari.add(LCount);

        // Paging (ikut di panel cari, sebelah kanan)
        panelGlassPaging.setName("panelGlassPaging");
        panelGlassPaging.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlassPaging.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnPrev.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/2leftarrow.png")));
        BtnPrev.setToolTipText("Halaman Sebelumnya");
        BtnPrev.setName("BtnPrev");
        BtnPrev.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { BtnPrevActionPerformed(evt); }
        });
        BtnPrev.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) { BtnPrevKeyPressed(evt); }
        });
        panelGlassPaging.add(BtnPrev);

        LHalaman.setText("Hal 0 / 0");
        LHalaman.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LHalaman.setName("LHalaman");
        LHalaman.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlassPaging.add(LHalaman);

        BtnNext.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/2rightarrow.png")));
        BtnNext.setToolTipText("Halaman Berikutnya");
        BtnNext.setName("BtnNext");
        BtnNext.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { BtnNextActionPerformed(evt); }
        });
        BtnNext.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) { BtnNextKeyPressed(evt); }
        });
        panelGlassPaging.add(BtnNext);

        // Gabungkan cari + paging dalam satu baris
        javax.swing.JPanel panelTopBawah = new javax.swing.JPanel();
        panelTopBawah.setOpaque(false);
        panelTopBawah.setLayout(new java.awt.BorderLayout());
        panelTopBawah.add(panelGlassCari, java.awt.BorderLayout.WEST);
        panelTopBawah.add(panelGlassPaging, java.awt.BorderLayout.EAST);

        jPanel1.add(panelTopBawah, java.awt.BorderLayout.PAGE_START);

        // Panel tombol CRUD (CENTER dalam jPanel1)
        panelGlassAksi.setName("panelGlassAksi");
        panelGlassAksi.setPreferredSize(new java.awt.Dimension(55, 55));
        panelGlassAksi.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan");
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { BtnSimpanActionPerformed(evt); }
        });
        BtnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) { BtnSimpanKeyPressed(evt); }
        });
        panelGlassAksi.add(BtnSimpan);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Baru");
        BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setName("BtnBatal");
        BtnBatal.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { emptTeks(); }
        });
        BtnBatal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) { BtnBatalKeyPressed(evt); }
        });
        panelGlassAksi.add(BtnBatal);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus");
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { BtnHapusActionPerformed(evt); }
        });
        BtnHapus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) { BtnHapusKeyPressed(evt); }
        });
        panelGlassAksi.add(BtnHapus);

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnEdit.setMnemonic('G');
        BtnEdit.setText("Ganti");
        BtnEdit.setToolTipText("Alt+G");
        BtnEdit.setName("BtnEdit");
        BtnEdit.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { BtnEditActionPerformed(evt); }
        });
        BtnEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) { BtnEditKeyPressed(evt); }
        });
        panelGlassAksi.add(BtnEdit);

        BtnPilih.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/editcopy.png")));
        BtnPilih.setMnemonic('P');
        BtnPilih.setText("Pilih");
        BtnPilih.setToolTipText("Alt+P");
        BtnPilih.setName("BtnPilih");
        BtnPilih.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnPilih.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { BtnPilihActionPerformed(evt); }
        });
        BtnPilih.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) { BtnPilihKeyPressed(evt); }
        });
        panelGlassAksi.add(BtnPilih);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar");
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { dispose(); }
        });
        BtnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) { BtnKeluarKeyPressed(evt); }
        });
        panelGlassAksi.add(BtnKeluar);

        jPanel1.add(panelGlassAksi, java.awt.BorderLayout.CENTER);

        internalFrame1.add(jPanel1, java.awt.BorderLayout.PAGE_END);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(internalFrame1, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(internalFrame1, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    // ── Window events ─────────────────────────────────────────────────
    private void formWindowOpened(java.awt.event.WindowEvent evt) {
        emptTeks();
        tampil();
    }

    // ── Key navigation ────────────────────────────────────────────────
    private void TKodeKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, BtnKeluar, TNama);
    }
    private void TNamaKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, TKode, TKomponen);
    }
    private void TKomponenKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, TNama, TSpesimen);
    }
    private void TSpesimenKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, TKomponen, TKategori);
    }
    private void TKategoriKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, TSpesimen, TSatuan);
    }
    private void TSatuanKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, TKategori, TMetode);
    }
    private void TMetodeKeyPressed(java.awt.event.KeyEvent evt) {
        Valid.pindah(evt, TSatuan, BtnSimpan);
    }

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) { currentPage = 0; tampil(); }
        else if (evt.getKeyCode() == KeyEvent.VK_UP) tbLoinc.requestFocus();
    }

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) { currentPage = 0; tampil(); }
        else Valid.pindah(evt, TCari, BtnAll);
    }

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) { TCari.setText(""); currentPage = 0; tampil(); }
        else Valid.pindah(evt, BtnCari, BtnSimpan);
    }

    // ── Paging ────────────────────────────────────────────────────────
    private void BtnPrevActionPerformed(java.awt.event.ActionEvent evt) {
        if (currentPage > 0) { currentPage--; tampil(); }
    }
    private void BtnPrevKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) BtnPrevActionPerformed(null);
    }

    private void BtnNextActionPerformed(java.awt.event.ActionEvent evt) {
        int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
        if (currentPage < totalPages - 1) { currentPage++; tampil(); }
    }
    private void BtnNextKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) BtnNextActionPerformed(null);
    }

    // ── CRUD ──────────────────────────────────────────────────────────
    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
        if (TKode.getText().trim().isEmpty()) {
            Valid.textKosong(TKode, "kode LOINC");
        } else if (TNama.getText().trim().isEmpty()) {
            Valid.textKosong(TNama, "nama pemeriksaan");
        } else {
            try {
                ps = koneksi.prepareStatement(
                    "INSERT INTO loinc (code, nama_pemeriksaan, component, spesimen, kategori, " +
                    "satuan, metode_analisis, display, permintaan_hasil, tipe_hasil, timing, " +
                    "system, scale, method, unit_of_measure, property, code_system) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,'http://loinc.org')");
                ps.setString(1, TKode.getText().trim());
                ps.setString(2, TNama.getText().trim());
                ps.setString(3, TKomponen.getText().trim());
                ps.setString(4, TSpesimen.getText().trim());
                ps.setString(5, TKategori.getText().trim());
                ps.setString(6, TSatuan.getText().trim());
                ps.setString(7, TMetode.getText().trim());
                ps.setString(8, TNama.getText().trim());
                ps.setString(9, ""); ps.setString(10, ""); ps.setString(11, "");
                ps.setString(12, TSpesimen.getText().trim());
                ps.setString(13, ""); ps.setString(14, TMetode.getText().trim());
                ps.setString(15, TSatuan.getText().trim()); ps.setString(16, "");
                ps.executeUpdate();
                tampil();
                emptTeks();
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062) {
                    JOptionPane.showMessageDialog(this, "Kode LOINC '" + TKode.getText().trim() + "' sudah ada!", "Duplikat", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                TKode.requestFocus();
            } finally {
                try { if (ps != null) ps.close(); } catch (SQLException ignored) {}
            }
        }
    }

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) BtnSimpanActionPerformed(null);
        else Valid.pindah(evt, TMetode, BtnBatal);
    }

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) emptTeks();
        else Valid.pindah(evt, BtnSimpan, BtnHapus);
    }

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {
        if (tbLoinc.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus dari tabel!", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String kode = tabMode.getValueAt(tbLoinc.getSelectedRow(), 0).toString();
        int ok = JOptionPane.showConfirmDialog(this, "Hapus kode LOINC '" + kode + "'?",
            "Konfirmasi", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok == JOptionPane.YES_OPTION) {
            try {
                ps = koneksi.prepareStatement("DELETE FROM loinc WHERE code=?");
                ps.setString(1, kode);
                ps.executeUpdate();
                tampil();
                emptTeks();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try { if (ps != null) ps.close(); } catch (SQLException ignored) {}
            }
        }
    }

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) BtnHapusActionPerformed(null);
        else Valid.pindah(evt, BtnBatal, BtnEdit);
    }

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {
        if (TKode.getText().trim().isEmpty()) {
            Valid.textKosong(TKode, "kode LOINC");
        } else if (TNama.getText().trim().isEmpty()) {
            Valid.textKosong(TNama, "nama pemeriksaan");
        } else if (tbLoinc.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan diganti dari tabel!", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String kodeAsal = tabMode.getValueAt(tbLoinc.getSelectedRow(), 0).toString();
            try {
                ps = koneksi.prepareStatement(
                    "UPDATE loinc SET code=?, nama_pemeriksaan=?, component=?, spesimen=?, " +
                    "kategori=?, satuan=?, metode_analisis=?, display=?, system=?, method=?, unit_of_measure=? " +
                    "WHERE code=?");
                ps.setString(1, TKode.getText().trim());
                ps.setString(2, TNama.getText().trim());
                ps.setString(3, TKomponen.getText().trim());
                ps.setString(4, TSpesimen.getText().trim());
                ps.setString(5, TKategori.getText().trim());
                ps.setString(6, TSatuan.getText().trim());
                ps.setString(7, TMetode.getText().trim());
                ps.setString(8, TNama.getText().trim());
                ps.setString(9, TSpesimen.getText().trim());
                ps.setString(10, TMetode.getText().trim());
                ps.setString(11, TSatuan.getText().trim());
                ps.setString(12, kodeAsal);
                ps.executeUpdate();
                tampil();
                emptTeks();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try { if (ps != null) ps.close(); } catch (SQLException ignored) {}
            }
        }
    }

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) BtnEditActionPerformed(null);
        else Valid.pindah(evt, BtnHapus, BtnPilih);
    }

    private void BtnPilihActionPerformed(java.awt.event.ActionEvent evt) {
        if (tbLoinc.getSelectedRow() >= 0) dispose();
    }

    private void BtnPilihKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) BtnPilihActionPerformed(null);
        else Valid.pindah(evt, BtnEdit, BtnKeluar);
    }

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) dispose();
        else Valid.pindah(evt, BtnPilih, TKode);
    }

    // ── Table events ──────────────────────────────────────────────────
    private void tbLoincMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabMode.getRowCount() != 0) {
            getData();
            if (evt.getClickCount() == 2) BtnPilihActionPerformed(null);
        }
    }

    private void tbLoincKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SHIFT) {
            TCari.setText("");
            TCari.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnPilihActionPerformed(null);
        }
    }

    private void tbLoincKeyReleased(java.awt.event.KeyEvent evt) {
        if (tabMode.getRowCount() != 0 &&
            (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN)) {
            getData();
        }
    }

    // ── Main ──────────────────────────────────────────────────────────
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgMasterLoinc dialog = new DlgMasterLoinc(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosing(java.awt.event.WindowEvent e) { System.exit(0); }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnNext;
    private widget.Button BtnPilih;
    private widget.Button BtnPrev;
    private widget.Button BtnSimpan;
    private widget.Label LCount;
    private widget.Label LHalaman;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TKategori;
    private widget.TextBox TKode;
    private widget.TextBox TKomponen;
    private widget.TextBox TMetode;
    private widget.TextBox TNama;
    private widget.TextBox TSatuan;
    private widget.TextBox TSpesimen;
    private widget.InternalFrame internalFrame1;
    private javax.swing.JPanel jPanel1;
    private widget.Label lbCari;
    private widget.Label lbKategori;
    private widget.Label lbKode;
    private widget.Label lbKomponen;
    private widget.Label lbMetode;
    private widget.Label lbNama;
    private widget.Label lbRecord;
    private widget.Label lbSatuan;
    private widget.Label lbSpesimen;
    private widget.panelisi panelGlassAksi;
    private widget.panelisi panelGlassCari;
    private widget.panelisi panelGlassForm;
    private widget.panelisi panelGlassPaging;
    private widget.Table tbLoinc;
    // End of variables declaration//GEN-END:variables

    // ── Helpers ───────────────────────────────────────────────────────
    private void tampil() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Valid.tabelKosong(tabMode);
        String keyword = "%" + TCari.getText().trim() + "%";

        // Hitung total record untuk paging
        try {
            ps = koneksi.prepareStatement(
                "SELECT COUNT(*) FROM loinc " +
                "WHERE code LIKE ? OR nama_pemeriksaan LIKE ? OR component LIKE ?");
            ps.setString(1, keyword); ps.setString(2, keyword); ps.setString(3, keyword);
            rs = ps.executeQuery();
            if (rs.next()) totalRecords = rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("DlgMasterLoinc count: " + e);
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException ignored) {}
        }

        // Ambil data halaman saat ini
        try {
            ps = koneksi.prepareStatement(
                "SELECT code, nama_pemeriksaan, component, spesimen, kategori, satuan, metode_analisis " +
                "FROM loinc WHERE code LIKE ? OR nama_pemeriksaan LIKE ? OR component LIKE ? " +
                "ORDER BY code LIMIT ? OFFSET ?");
            ps.setString(1, keyword); ps.setString(2, keyword); ps.setString(3, keyword);
            ps.setInt(4, PAGE_SIZE);
            ps.setInt(5, currentPage * PAGE_SIZE);
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)
                });
            }
        } catch (SQLException e) {
            System.out.println("DlgMasterLoinc tampil: " + e);
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (SQLException ignored) {}
        }

        int totalPages = (totalRecords == 0) ? 1 : (int) Math.ceil((double) totalRecords / PAGE_SIZE);
        LCount.setText("" + totalRecords);
        LHalaman.setText("Hal " + (currentPage + 1) + " / " + totalPages);
        BtnPrev.setEnabled(currentPage > 0);
        BtnNext.setEnabled(currentPage < totalPages - 1);
        this.setCursor(Cursor.getDefaultCursor());
    }

    public void emptTeks() {
        TKode.setText("");
        TNama.setText("");
        TKomponen.setText("");
        TSpesimen.setText("");
        TKategori.setText("");
        TSatuan.setText("");
        TMetode.setText("");
        TKode.requestFocus();
    }

    private void getData() {
        int row = tbLoinc.getSelectedRow();
        if (row >= 0) {
            TKode.setText(tabMode.getValueAt(row, 0).toString());
            TNama.setText(tabMode.getValueAt(row, 1).toString());
            TKomponen.setText(tabMode.getValueAt(row, 2).toString());
            TSpesimen.setText(tabMode.getValueAt(row, 3).toString());
            TKategori.setText(tabMode.getValueAt(row, 4).toString());
            TSatuan.setText(tabMode.getValueAt(row, 5).toString());
            TMetode.setText(tabMode.getValueAt(row, 6).toString());
        }
    }

    // Public getters untuk integrasi form lain
    public String getKodeLoinc() {
        int row = tbLoinc.getSelectedRow();
        return row >= 0 ? tabMode.getValueAt(row, 0).toString() : "";
    }

    public String getNamaLoinc() {
        int row = tbLoinc.getSelectedRow();
        return row >= 0 ? tabMode.getValueAt(row, 1).toString() : "";
    }

    public String getKategoriLoinc() {
        int row = tbLoinc.getSelectedRow();
        return row >= 0 ? tabMode.getValueAt(row, 4).toString() : "";
    }

    public JTextField getTextField() { return TKode; }
    public JTable getTable()        { return tbLoinc; }
}
