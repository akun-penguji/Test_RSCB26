/*
 * DlgPemetaanAlergi.java
 *
 * Memetakan data alergi teks-bebas yang selama ini diinput di banyak tabel
 * asesmen (pemeriksaan_ralan.alergi, pemeriksaan_ranap.alergi,
 * penilaian_medis_igd.alergi, rekonsiliasi_obat.alergi_obat, dst — daftarnya
 * ada di tabel alergi_sumber_kolom) menjadi data terstruktur di alergi_pasien.
 *
 * Alur:
 *   1. [Pindai & Petakan Otomatis]  -> baca semua sumber, pecah teks jadi token
 *      ("udang, telur" -> "udang" + "telur"), lalu tebak kodenya otomatis dari
 *      kamus sinonim + master satu_sehat_ref_allergy. Hasilnya masuk ke
 *      alergi_mapping_teks. Baris yang sudah diperbaiki manual TIDAK ditimpa.
 *   2. Petugas RM memperbaiki/melengkapi baris yang belum dapat kode.
 *   3. [Terapkan ke alergi_pasien] -> tulis satu baris per (no_rawat, kode).
 *
 * Karena PK alergi_pasien = (no_rawat, allergy_code), satu kunjungan bisa punya
 * beberapa alergi dan penerapan ulang bersifat idempoten.
 */
package rekammedis;

import bridging.SatuSehatCariAllergy;
import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class DlgPemetaanAlergi extends javax.swing.JDialog {

    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final SatuSehatCariAllergy picker = new SatuSehatCariAllergy(null, false);
    private String modePicker = "alergi";
    private DefaultTableModel tabMode;
    private PreparedStatement ps;
    private ResultSet rs;

    private static final String KODE_TIDAK_ADA = AlergiMapper.KODE_TIDAK_ADA;

    public DlgPemetaanAlergi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(1100, 600);

        tabMode = new DefaultTableModel(null, new Object[]{
            "Teks Alergi", "Temuan", "Kode", "Nama Alergi (SNOMED)", "Kategori",
            "Kode Reaksi", "Reaksi", "Severity", "Tindakan", "Asal"
        }) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tbMapping.setModel(tabMode);
        tbMapping.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbMapping.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] lebar = {230, 60, 90, 260, 90, 90, 150, 90, 110, 70};
        for (int i = 0; i < lebar.length; i++) {
            TableColumn c = tbMapping.getColumnModel().getColumn(i);
            c.setPreferredWidth(lebar[i]);
        }
        tbMapping.setDefaultRenderer(Object.class, new WarnaTable());

        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));
        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }

                @Override
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }

                @Override
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }
            });
        }

        picker.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                JTable t = picker.getTable();
                if (t.getSelectedRow() != -1) {
                    if (modePicker.equals("alergi")) {
                        TKode.setText(t.getValueAt(t.getSelectedRow(), 0).toString());
                        TNamaAlergi.setText(t.getValueAt(t.getSelectedRow(), 2).toString());
                    } else {
                        TKodeReaksi.setText(t.getValueAt(t.getSelectedRow(), 0).toString());
                        TNamaReaksi.setText(t.getValueAt(t.getSelectedRow(), 2).toString());
                    }
                }
            }
        });
    }

    // ================================================================== UI
    private void initComponents() {
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbMapping = new widget.Table();
        jPanel3 = new widget.panelisi();
        panelGlass8 = new widget.panelisi();
        panelGlass9 = new widget.panelisi();
        panelGlass7 = new widget.panelisi();
        BtnPindai = new widget.Button();
        BtnSimpan = new widget.Button();
        BtnHapus = new widget.Button();
        BtnTerapkan = new widget.Button();
        BtnKeluar = new widget.Button();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        BtnPilihAlergi = new widget.Button();
        BtnPilihReaksi = new widget.Button();
        TCari = new widget.TextBox();
        TTeks = new widget.TextBox();
        TKode = new widget.TextBox();
        TNamaAlergi = new widget.TextBox();
        TKodeReaksi = new widget.TextBox();
        TNamaReaksi = new widget.TextBox();
        LCount = new widget.Label();
        cmbFilter = new widget.ComboBox();
        cmbKategori = new widget.ComboBox();
        cmbSeverity = new widget.ComboBox();
        cmbTindakan = new widget.ComboBox();
        jLabel1 = new widget.Label();
        jLabel2 = new widget.Label();
        jLabel3 = new widget.Label();
        jLabel4 = new widget.Label();
        jLabel5 = new widget.Label();
        jLabel6 = new widget.Label();
        jLabel7 = new widget.Label();
        jLabel8 = new widget.Label();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                tampil();
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
                "::[ Pemetaan Data Alergi Teks Bebas ke Alergi Terstruktur ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        tbMapping.setToolTipText("Klik baris untuk memperbaiki pemetaannya");
        tbMapping.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                getData();
            }
        });
        tbMapping.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER || evt.getKeyCode() == KeyEvent.VK_UP
                        || evt.getKeyCode() == KeyEvent.VK_DOWN) {
                    getData();
                }
            }
        });
        Scroll.setViewportView(tbMapping);
        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        // ---------------- panel input (atas) ----------------
        panelGlass7.setPreferredSize(new java.awt.Dimension(44, 78));
        panelGlass7.setLayout(null);

        jLabel1.setText("Teks Alergi :");
        panelGlass7.add(jLabel1);
        jLabel1.setBounds(0, 10, 80, 23);
        TTeks.setEditable(false);
        panelGlass7.add(TTeks);
        TTeks.setBounds(84, 10, 230, 23);

        jLabel2.setText("Kode Alergi :");
        panelGlass7.add(jLabel2);
        jLabel2.setBounds(320, 10, 80, 23);
        TKode.setEditable(false);
        panelGlass7.add(TKode);
        TKode.setBounds(404, 10, 90, 23);
        TNamaAlergi.setEditable(false);
        panelGlass7.add(TNamaAlergi);
        TNamaAlergi.setBounds(498, 10, 250, 23);

        BtnPilihAlergi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPilihAlergi.setMnemonic('1');
        BtnPilihAlergi.setToolTipText("Alt+1, pilih kode alergi");
        BtnPilihAlergi.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnPilihAlergi.addActionListener(evt -> pilih("alergi"));
        panelGlass7.add(BtnPilihAlergi);
        BtnPilihAlergi.setBounds(752, 10, 28, 23);

        jLabel3.setText("Kategori :");
        panelGlass7.add(jLabel3);
        jLabel3.setBounds(790, 10, 60, 23);
        cmbKategori.setModel(new javax.swing.DefaultComboBoxModel(
                new String[]{"Makanan", "Medication", "Lingkungan", "Biologis"}));
        panelGlass7.add(cmbKategori);
        cmbKategori.setBounds(854, 10, 120, 23);

        jLabel4.setText("Reaksi :");
        panelGlass7.add(jLabel4);
        jLabel4.setBounds(320, 40, 80, 23);
        TKodeReaksi.setEditable(false);
        panelGlass7.add(TKodeReaksi);
        TKodeReaksi.setBounds(404, 40, 90, 23);
        TNamaReaksi.setEditable(false);
        panelGlass7.add(TNamaReaksi);
        TNamaReaksi.setBounds(498, 40, 250, 23);

        BtnPilihReaksi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnPilihReaksi.setMnemonic('2');
        BtnPilihReaksi.setToolTipText("Alt+2, pilih kode reaksi");
        BtnPilihReaksi.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnPilihReaksi.addActionListener(evt -> pilih("reaksialergi"));
        panelGlass7.add(BtnPilihReaksi);
        BtnPilihReaksi.setBounds(752, 40, 28, 23);

        jLabel5.setText("Severity :");
        panelGlass7.add(jLabel5);
        jLabel5.setBounds(790, 40, 60, 23);
        cmbSeverity.setModel(new javax.swing.DefaultComboBoxModel(
                new String[]{"low", "high", "unable-to-assess"}));
        panelGlass7.add(cmbSeverity);
        cmbSeverity.setBounds(854, 40, 120, 23);

        jLabel6.setText("Tindakan :");
        panelGlass7.add(jLabel6);
        jLabel6.setBounds(0, 40, 80, 23);
        cmbTindakan.setModel(new javax.swing.DefaultComboBoxModel(
                new String[]{"Petakan", "Abaikan", "Tidak Ada Alergi"}));
        panelGlass7.add(cmbTindakan);
        cmbTindakan.setBounds(84, 40, 230, 23);

        internalFrame1.add(panelGlass7, java.awt.BorderLayout.PAGE_START);

        // ---------------- panel bawah ----------------
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 88));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel7.setText("Tampilkan :");
        jLabel7.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel7);
        cmbFilter.setModel(new javax.swing.DefaultComboBoxModel(
                new String[]{"Semua", "Belum Dipetakan", "Sudah Dipetakan", "Diabaikan"}));
        cmbFilter.setPreferredSize(new java.awt.Dimension(160, 23));
        cmbFilter.addActionListener(evt -> tampil());
        panelGlass9.add(cmbFilter);

        jLabel8.setText("Key Word :");
        jLabel8.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel8);
        TCari.setPreferredSize(new java.awt.Dimension(230, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    tampil();
                }
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('3');
        BtnCari.setToolTipText("Alt+3");
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(evt -> tampil());
        panelGlass9.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setToolTipText("Alt+M, tampilkan semua");
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll.addActionListener(evt -> {
            TCari.setText("");
            tampil();
        });
        panelGlass9.add(BtnAll);

        widget.Label l9 = new widget.Label();
        l9.setText("Tampil :");
        l9.setPreferredSize(new java.awt.Dimension(55, 23));
        panelGlass9.add(l9);
        LCount.setText("0");
        LCount.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass9.add(LCount);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnPindai.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnPindai.setMnemonic('P');
        BtnPindai.setText("Pindai & Petakan Otomatis");
        BtnPindai.setToolTipText("Alt+P, baca semua tabel sumber lalu tebak kodenya otomatis");
        BtnPindai.setPreferredSize(new java.awt.Dimension(210, 30));
        BtnPindai.addActionListener(evt -> pindaiOtomatis());
        panelGlass8.add(BtnPindai);

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpan.addActionListener(evt -> simpan());
        panelGlass8.add(BtnSimpan);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/delete-16x16.png"))); // NOI18N
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapus.addActionListener(evt -> hapus());
        panelGlass8.add(BtnHapus);

        BtnTerapkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnTerapkan.setMnemonic('T');
        BtnTerapkan.setText("Terapkan ke alergi_pasien");
        BtnTerapkan.setToolTipText("Alt+T, tulis hasil pemetaan ke tabel alergi_pasien");
        BtnTerapkan.setPreferredSize(new java.awt.Dimension(200, 30));
        BtnTerapkan.addActionListener(evt -> terapkan());
        panelGlass8.add(BtnTerapkan);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/logout.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(evt -> dispose());
        panelGlass8.add(BtnKeluar);

        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);
        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
        pack();
    }

    private void pilih(String mode) {
        modePicker = mode;
        picker.isCek(mode);
        picker.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        picker.setLocationRelativeTo(internalFrame1);
        picker.setVisible(true);
    }

    // ============================================================ pindai
    private void pindaiOtomatis() {
        if (JOptionPane.showConfirmDialog(this,
                "Pindai seluruh tabel sumber lalu petakan otomatis?\n"
                + "Baris kamus yang sudah diperbaiki manual tidak akan ditimpa.",
                "Konfirmasi", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        try {
            int[] h = AlergiMapper.pindai(koneksi);
            tampil();
            JOptionPane.showMessageDialog(null, "Selesai memindai " + h[0] + " tabel sumber.\n"
                    + "Teks unik            : " + h[1] + "\n"
                    + "Dapat kode otomatis  : " + h[2] + "\n"
                    + "Perlu koreksi manual : " + (h[1] - h[2])
                    + "\n\nPilih filter 'Belum Dipetakan' untuk melengkapinya.");
        } catch (Exception e) {
            System.out.println("Notif pindaiOtomatis : " + e);
            JOptionPane.showMessageDialog(null, "Gagal memindai : " + e);
        } finally {
            setCursor(java.awt.Cursor.getDefaultCursor());
        }
    }

    // ============================================================ tabel kamus
    public void tampil() {
        Valid.tabelKosong(tabMode);
        String filter = "";
        String f = cmbFilter.getSelectedItem() + "";
        if (f.equals("Belum Dipetakan")) {
            filter = " and m.tindakan='Petakan' and (m.allergy_code is null or m.allergy_code='') ";
        } else if (f.equals("Sudah Dipetakan")) {
            filter = " and m.allergy_code is not null and m.allergy_code<>'' and m.tindakan<>'Abaikan' ";
        } else if (f.equals("Diabaikan")) {
            filter = " and m.tindakan='Abaikan' ";
        }
        try {
            ps = koneksi.prepareStatement(
                    "select m.teks,m.jml_temuan,ifnull(m.allergy_code,'') as allergy_code,ifnull(a.display,'') as nm_alergi,"
                    + "ifnull(m.category,'') as category,ifnull(m.reactioncode,'') as reactioncode,"
                    + "ifnull(rk.display,'') as nm_reaksi,ifnull(m.severity,'') as severity,m.tindakan,m.asal "
                    + "from alergi_mapping_teks m "
                    + "left join satu_sehat_ref_allergy a on m.allergy_code=a.kode "
                    + "left join satu_sehat_ref_allergy_reaction rk on m.reactioncode=rk.kode "
                    + "where m.teks like ? " + filter
                    + "order by m.jml_temuan desc, m.teks");
            ps.setString(1, "%" + TCari.getText().trim() + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new Object[]{
                    rs.getString("teks"), rs.getInt("jml_temuan"), rs.getString("allergy_code"),
                    rs.getString("nm_alergi"), rs.getString("category"), rs.getString("reactioncode"),
                    rs.getString("nm_reaksi"), rs.getString("severity"), rs.getString("tindakan"), rs.getString("asal")
                });
            }
        } catch (Exception e) {
            System.out.println("Notif tampil pemetaan : " + e);
        } finally {
            tutup();
        }
        LCount.setText("" + tabMode.getRowCount());
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

    private void getData() {
        int r = tbMapping.getSelectedRow();
        if (r < 0) {
            return;
        }
        TTeks.setText(tabMode.getValueAt(r, 0).toString());
        TKode.setText(tabMode.getValueAt(r, 2).toString());
        TNamaAlergi.setText(tabMode.getValueAt(r, 3).toString());
        if (!tabMode.getValueAt(r, 4).toString().isEmpty()) {
            cmbKategori.setSelectedItem(tabMode.getValueAt(r, 4).toString());
        }
        TKodeReaksi.setText(tabMode.getValueAt(r, 5).toString());
        TNamaReaksi.setText(tabMode.getValueAt(r, 6).toString());
        if (!tabMode.getValueAt(r, 7).toString().isEmpty()) {
            cmbSeverity.setSelectedItem(tabMode.getValueAt(r, 7).toString());
        }
        cmbTindakan.setSelectedItem(tabMode.getValueAt(r, 8).toString());
    }

    private void simpan() {
        if (TTeks.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Pilih dulu baris teks yang mau dipetakan..!!");
            return;
        }
        String tindakan = cmbTindakan.getSelectedItem() + "";
        if (tindakan.equals("Petakan") && TKode.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Kode alergi belum dipilih. "
                    + "Kalau teks ini bukan alergi, ganti Tindakan jadi 'Abaikan'..!!");
            return;
        }
        String kode = tindakan.equals("Tidak Ada Alergi") ? KODE_TIDAK_ADA : TKode.getText().trim();
        try (PreparedStatement p = koneksi.prepareStatement(
                "update alergi_mapping_teks set allergy_code=?,category=?,reactioncode=?,severity=?,"
                + "tindakan=?,asal='Manual',tgl_update=now() where teks=?")) {
            p.setString(1, kode.isEmpty() ? null : kode);
            p.setString(2, cmbKategori.getSelectedItem() + "");
            p.setString(3, TKodeReaksi.getText().trim().isEmpty() ? null : TKodeReaksi.getText().trim());
            p.setString(4, cmbSeverity.getSelectedItem() + "");
            p.setString(5, tindakan);
            p.setString(6, TTeks.getText().trim());
            p.executeUpdate();
            tampil();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal menyimpan : " + e);
        }
    }

    private void hapus() {
        if (TTeks.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Pilih dulu barisnya..!!");
            return;
        }
        try (PreparedStatement p = koneksi.prepareStatement("delete from alergi_mapping_teks where teks=?")) {
            p.setString(1, TTeks.getText().trim());
            p.executeUpdate();
            tampil();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal menghapus : " + e);
        }
    }

    // ============================================================ backfill
    /**
     * Tulis hasil pemetaan ke alergi_pasien. Satu baris per (no_rawat, kode) —
     * "udang, telur" menghasilkan dua baris. INSERT IGNORE sehingga data yang
     * sudah diinput manual lewat tab Data Alergi tidak tertimpa.
     */
    private void terapkan() {
        if (JOptionPane.showConfirmDialog(this,
                "Tulis hasil pemetaan ke tabel alergi_pasien?\n"
                + "Data alergi yang sudah ada tidak akan ditimpa.",
                "Konfirmasi", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        try {
            int[] h = AlergiMapper.terapkan(koneksi);
            if (h[0] == 0 && h[1] == 0) {
                JOptionPane.showMessageDialog(null, "Belum ada teks yang dipetakan. Jalankan Pindai dulu..!!");
            } else {
                JOptionPane.showMessageDialog(null, "Selesai.\n"
                        + "Baris alergi_pasien baru : " + h[0] + "\n"
                        + "Token dilewati (belum dipetakan/diabaikan) : " + h[1]);
            }
        } catch (Exception e) {
            System.out.println("Notif terapkan : " + e);
            JOptionPane.showMessageDialog(null, "Gagal menerapkan : " + e);
        } finally {
            setCursor(java.awt.Cursor.getDefaultCursor());
        }
    }

    public void isCek() {
        BtnPindai.setEnabled(akses.getpemetaan_alergi());
        BtnSimpan.setEnabled(akses.getpemetaan_alergi());
        BtnHapus.setEnabled(akses.getpemetaan_alergi());
        BtnTerapkan.setEnabled(akses.getpemetaan_alergi());
    }

    private widget.Button BtnAll;
    private widget.Button BtnCari;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPilihAlergi;
    private widget.Button BtnPilihReaksi;
    private widget.Button BtnPindai;
    private widget.Button BtnSimpan;
    private widget.Button BtnTerapkan;
    private widget.Label LCount;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TKode;
    private widget.TextBox TKodeReaksi;
    private widget.TextBox TNamaAlergi;
    private widget.TextBox TNamaReaksi;
    private widget.TextBox TTeks;
    private widget.ComboBox cmbFilter;
    private widget.ComboBox cmbKategori;
    private widget.ComboBox cmbSeverity;
    private widget.ComboBox cmbTindakan;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel1;
    private widget.Label jLabel2;
    private widget.Label jLabel3;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.panelisi jPanel3;
    private widget.panelisi panelGlass7;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbMapping;
}
