/*
 * DlgNotifStokFarmasi.java
 *
 * Form pengaturan modul Notifikasi Stok Minimal Farmasi via GOWA WhatsApp Gateway.
 * Tab 1: Grup WA  (CRUD nama, JID, device id, bangsal, aktif)
 * Tab 2: Konfigurasi GOWA (URL, user, password, device id global, jam op, dll)
 * Tab 3: Status & Kirim Manual
 *
 * Tabel DB terkait:
 *   - notif_wa_grup
 *   - config_notif_wa
 *   - v_stok_minimal_alert
 *   - log_notif_stok_minimal
 *   - bangsal (read-only)
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
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public final class DlgNotifStokFarmasi extends javax.swing.JDialog {

    private final Connection koneksi = koneksiDB.condb();
    private final DefaultTableModel modelGrup;
    private int editId = 0; // 0 = tambah mode, >0 = edit mode

    public DlgNotifStokFarmasi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(10, 10);
        setSize(950, 620);

        modelGrup = new DefaultTableModel(null,
            new Object[]{"ID", "Nama Grup", "JID", "Device ID", "Bangsal", "Keterangan", "Aktif"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbGrup.setModel(modelGrup);
        tbGrup.setPreferredScrollableViewportSize(new Dimension(500, 260));
        tbGrup.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] w = {40, 160, 220, 90, 120, 200, 60};
        for (int i = 0; i < w.length; i++) {
            TableColumn c = tbGrup.getColumnModel().getColumn(i);
            c.setPreferredWidth(w[i]);
        }
        tbGrup.setDefaultRenderer(Object.class, new WarnaTable());

        TNama.setDocument(new fungsi.batasInput((byte) 100).getKata(TNama));
        TJid.setDocument(new fungsi.batasInput((byte) 100).getKata(TJid));
        TDevice.setDocument(new fungsi.batasInput((byte) 100).getKata(TDevice));
        TKeterangan.setDocument(new fungsi.batasInput((byte) 255).getKata(TKeterangan));

        loadBangsal();
        tampilGrup();
        loadConfig();
        loadStatus();
    }

    // =================================================================
    // LAYOUT (manual — menggunakan widget.* SIMRS Khanza)
    // =================================================================
    @SuppressWarnings("unchecked")
    private void initComponents() {
        internalFrame1 = new widget.InternalFrame();
        TabPane = new widget.TabPane();

        // ---------------- Tab 1: Grup WA ----------------
        panelGrup = new javax.swing.JPanel();
        Scroll = new widget.ScrollPane();
        tbGrup = new widget.Table();

        panelDetail = new widget.panelisi();
        lblNama = new widget.Label();
        TNama = new widget.TextBox();
        lblJid = new widget.Label();
        TJid = new widget.TextBox();
        lblDevice = new widget.Label();
        TDevice = new widget.TextBox();
        lblBangsal = new widget.Label();
        cbBangsal = new widget.ComboBox();
        lblKeterangan = new widget.Label();
        TKeterangan = new widget.TextBox();
        chkAktif = new widget.CekBox();

        panelAksi = new widget.panelisi();
        BtnEdit = new widget.Button();
        BtnSimpan = new widget.Button();
        BtnHapus = new widget.Button();
        BtnBatal = new widget.Button();
        BtnRefresh = new widget.Button();

        // ---------------- Tab 2: Konfigurasi GOWA ----------------
        panelKonfig = new javax.swing.JPanel();
        panelKonfigIsi = new widget.panelisi();
        lblUrl = new widget.Label();
        TUrl = new widget.TextBox();
        lblUser = new widget.Label();
        TUser = new widget.TextBox();
        lblPass = new widget.Label();
        TPass = new widget.PasswordBox();
        lblDevGlobal = new widget.Label();
        TDevGlobal = new widget.TextBox();
        lblNamaRs = new widget.Label();
        TNamaRs = new widget.TextBox();
        lblJamMulai = new widget.Label();
        TJamMulai = new widget.TextBox();
        lblJamSelesai = new widget.Label();
        TJamSelesai = new widget.TextBox();
        lblCooldown = new widget.Label();
        TCooldown = new widget.TextBox();
        lblCritical = new widget.Label();
        TCritical = new widget.TextBox();
        lblMax = new widget.Label();
        TMax = new widget.TextBox();
        chkEnabled = new widget.CekBox();
        BtnSimpanConfig = new widget.Button();
        BtnReloadConfig = new widget.Button();

        // ---------------- Tab 3: Status & Kirim ----------------
        panelStatus = new javax.swing.JPanel();
        panelStatAtas = new widget.panelisi();
        lblTotalItems = new widget.Label();
        lblItemFRM00 = new widget.Label();
        lblItemFRM01 = new widget.Label();
        lblGrupAktif = new widget.Label();
        ScrollLog = new widget.ScrollPane();
        txtLog = new widget.TextArea();
        panelKirim = new widget.panelisi();
        BtnKirim = new widget.Button();
        BtnRefreshStat = new widget.Button();

        // ---------------- Bottom ----------------
        panelBottom = new widget.panelisi();
        BtnKeluar = new widget.Button();

        // ---------------- Configure ----------------
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Notifikasi Stok Minimal Farmasi :: GOWA WhatsApp Gateway");

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Notifikasi Stok Minimal Farmasi ]::",
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setFont(new Font("Tahoma", 0, 12));
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        // ---------- Tab 1 layout ----------
        panelGrup.setOpaque(false);
        panelGrup.setLayout(new java.awt.BorderLayout(1, 1));

        tbGrup.setAutoCreateRowSorter(true);
        tbGrup.setToolTipText("Klik untuk memilih grup yang akan diedit / dihapus");
        tbGrup.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { tbGrupMouseClicked(evt); }
        });
        Scroll.setViewportView(tbGrup);
        panelGrup.add(Scroll, java.awt.BorderLayout.CENTER);

        // Detail input
        panelDetail.setPreferredSize(new Dimension(44, 84));
        panelDetail.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 4));

        lblNama.setText("Nama Grup :");
        lblNama.setPreferredSize(new Dimension(80, 23));
        panelDetail.add(lblNama);
        TNama.setPreferredSize(new Dimension(160, 23));
        panelDetail.add(TNama);

        lblBangsal.setText("Bangsal :");
        lblBangsal.setPreferredSize(new Dimension(60, 23));
        panelDetail.add(lblBangsal);
        cbBangsal.setPreferredSize(new Dimension(170, 23));
        panelDetail.add(cbBangsal);

        lblJid.setText("JID :");
        lblJid.setPreferredSize(new Dimension(40, 23));
        panelDetail.add(lblJid);
        TJid.setPreferredSize(new Dimension(220, 23));
        panelDetail.add(TJid);

        lblDevice.setText("Device ID :");
        lblDevice.setPreferredSize(new Dimension(70, 23));
        panelDetail.add(lblDevice);
        TDevice.setPreferredSize(new Dimension(120, 23));
        panelDetail.add(TDevice);

        lblKeterangan.setText("Keterangan :");
        lblKeterangan.setPreferredSize(new Dimension(80, 23));
        panelDetail.add(lblKeterangan);
        TKeterangan.setPreferredSize(new Dimension(360, 23));
        panelDetail.add(TKeterangan);

        chkAktif.setText("Aktif");
        chkAktif.setSelected(true);
        chkAktif.setOpaque(false);
        panelDetail.add(chkAktif);

        // Action buttons for Tab 1
        panelAksi.setPreferredSize(new Dimension(44, 36));
        panelAksi.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 6));

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnEdit.setMnemonic('N');
        BtnEdit.setText("Baru");
        BtnEdit.setToolTipText("Alt+N, Reset form untuk tambah data baru");
        BtnEdit.setPreferredSize(new Dimension(95, 23));
        BtnEdit.addActionListener(e -> clearForm());
        panelAksi.add(BtnEdit);

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setPreferredSize(new Dimension(100, 23));
        BtnSimpan.addActionListener(e -> simpanGrup());
        panelAksi.add(BtnSimpan);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setPreferredSize(new Dimension(100, 23));
        BtnHapus.addActionListener(e -> hapusGrup());
        panelAksi.add(BtnHapus);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Batal");
        BtnBatal.setToolTipText("Alt+B, bersihkan form tanpa menyimpan");
        BtnBatal.setPreferredSize(new Dimension(95, 23));
        BtnBatal.addActionListener(e -> clearForm());
        panelAksi.add(BtnBatal);

        BtnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnRefresh.setMnemonic('R');
        BtnRefresh.setText("Refresh");
        BtnRefresh.setToolTipText("Alt+R");
        BtnRefresh.setPreferredSize(new Dimension(100, 23));
        BtnRefresh.addActionListener(e -> tampilGrup());
        panelAksi.add(BtnRefresh);

        javax.swing.JPanel bottomTab1 = new javax.swing.JPanel(new java.awt.BorderLayout(1, 1));
        bottomTab1.setOpaque(false);
        bottomTab1.add(panelDetail, java.awt.BorderLayout.CENTER);
        bottomTab1.add(panelAksi, java.awt.BorderLayout.SOUTH);
        panelGrup.add(bottomTab1, java.awt.BorderLayout.SOUTH);

        // ---------- Tab 2 layout ----------
        panelKonfig.setOpaque(false);
        panelKonfig.setLayout(new java.awt.BorderLayout(1, 1));

        panelKonfigIsi.setPreferredSize(new Dimension(900, 260));
        panelKonfigIsi.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 8));

        lblUrl.setText("URL GOWA :");
        lblUrl.setPreferredSize(new Dimension(100, 23));
        panelKonfigIsi.add(lblUrl);
        TUrl.setPreferredSize(new Dimension(400, 23));
        panelKonfigIsi.add(TUrl);

        lblUser.setText("Username :");
        lblUser.setPreferredSize(new Dimension(80, 23));
        panelKonfigIsi.add(lblUser);
        TUser.setPreferredSize(new Dimension(140, 23));
        panelKonfigIsi.add(TUser);

        lblPass.setText("Password :");
        lblPass.setPreferredSize(new Dimension(70, 23));
        panelKonfigIsi.add(lblPass);
        TPass.setPreferredSize(new Dimension(140, 23));
        panelKonfigIsi.add(TPass);

        lblDevGlobal.setText("Device ID (global) :");
        lblDevGlobal.setPreferredSize(new Dimension(110, 23));
        panelKonfigIsi.add(lblDevGlobal);
        TDevGlobal.setPreferredSize(new Dimension(140, 23));
        panelKonfigIsi.add(TDevGlobal);

        lblNamaRs.setText("Nama RS :");
        lblNamaRs.setPreferredSize(new Dimension(70, 23));
        panelKonfigIsi.add(lblNamaRs);
        TNamaRs.setPreferredSize(new Dimension(300, 23));
        panelKonfigIsi.add(TNamaRs);

        lblJamMulai.setText("Jam Mulai :");
        lblJamMulai.setPreferredSize(new Dimension(75, 23));
        panelKonfigIsi.add(lblJamMulai);
        TJamMulai.setPreferredSize(new Dimension(45, 23));
        panelKonfigIsi.add(TJamMulai);

        lblJamSelesai.setText("Jam Selesai :");
        lblJamSelesai.setPreferredSize(new Dimension(85, 23));
        panelKonfigIsi.add(lblJamSelesai);
        TJamSelesai.setPreferredSize(new Dimension(45, 23));
        panelKonfigIsi.add(TJamSelesai);

        lblCooldown.setText("Cooldown (jam) :");
        lblCooldown.setPreferredSize(new Dimension(100, 23));
        panelKonfigIsi.add(lblCooldown);
        TCooldown.setPreferredSize(new Dimension(50, 23));
        panelKonfigIsi.add(TCooldown);

        lblCritical.setText("Critical (0-1) :");
        lblCritical.setPreferredSize(new Dimension(90, 23));
        panelKonfigIsi.add(lblCritical);
        TCritical.setPreferredSize(new Dimension(50, 23));
        panelKonfigIsi.add(TCritical);

        lblMax.setText("Max Item :");
        lblMax.setPreferredSize(new Dimension(75, 23));
        panelKonfigIsi.add(lblMax);
        TMax.setPreferredSize(new Dimension(50, 23));
        panelKonfigIsi.add(TMax);

        chkEnabled.setText("Aktifkan Notifikasi (master switch)");
        chkEnabled.setOpaque(false);
        chkEnabled.setSelected(true);
        panelKonfigIsi.add(chkEnabled);

        BtnSimpanConfig.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpanConfig.setText("Simpan Konfigurasi");
        BtnSimpanConfig.setPreferredSize(new Dimension(180, 23));
        BtnSimpanConfig.addActionListener(e -> simpanConfig());
        panelKonfigIsi.add(BtnSimpanConfig);

        BtnReloadConfig.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnReloadConfig.setText("Muat Ulang");
        BtnReloadConfig.setPreferredSize(new Dimension(120, 23));
        BtnReloadConfig.addActionListener(e -> loadConfig());
        panelKonfigIsi.add(BtnReloadConfig);

        widget.ScrollPane scrollKonfig = new widget.ScrollPane();
        scrollKonfig.setViewportView(panelKonfigIsi);
        panelKonfig.add(scrollKonfig, java.awt.BorderLayout.CENTER);

        // ---------- Tab 3 layout ----------
        panelStatus.setOpaque(false);
        panelStatus.setLayout(new java.awt.BorderLayout(1, 1));

        panelStatAtas.setPreferredSize(new Dimension(44, 68));
        panelStatAtas.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 6));

        Font bigBold = new Font("Tahoma", Font.BOLD, 14);
        widget.Label lblT1 = new widget.Label(); lblT1.setText("Total Alert :");
        lblT1.setPreferredSize(new Dimension(80, 23));
        panelStatAtas.add(lblT1);
        lblTotalItems.setText("-");
        lblTotalItems.setFont(bigBold);
        lblTotalItems.setPreferredSize(new Dimension(60, 23));
        panelStatAtas.add(lblTotalItems);

        widget.Label lblT2 = new widget.Label(); lblT2.setText("   Gudang (FRM00) :");
        lblT2.setPreferredSize(new Dimension(130, 23));
        panelStatAtas.add(lblT2);
        lblItemFRM00.setText("-");
        lblItemFRM00.setFont(bigBold);
        lblItemFRM00.setPreferredSize(new Dimension(60, 23));
        panelStatAtas.add(lblItemFRM00);

        widget.Label lblT3 = new widget.Label(); lblT3.setText("   Apotek (FRM01) :");
        lblT3.setPreferredSize(new Dimension(130, 23));
        panelStatAtas.add(lblT3);
        lblItemFRM01.setText("-");
        lblItemFRM01.setFont(bigBold);
        lblItemFRM01.setPreferredSize(new Dimension(60, 23));
        panelStatAtas.add(lblItemFRM01);

        lblGrupAktif.setText(" ");
        lblGrupAktif.setPreferredSize(new Dimension(900, 23));
        panelStatAtas.add(lblGrupAktif);

        panelStatus.add(panelStatAtas, java.awt.BorderLayout.NORTH);

        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", 0, 11));
        ScrollLog.setViewportView(txtLog);
        panelStatus.add(ScrollLog, java.awt.BorderLayout.CENTER);

        panelKirim.setPreferredSize(new Dimension(44, 36));
        panelKirim.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 6));

        BtnKirim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnKirim.setText("Kirim Manual (cron PHP)");
        BtnKirim.setToolTipText("Jalankan cron/run.php --force --ignore-hours");
        BtnKirim.setPreferredSize(new Dimension(220, 23));
        BtnKirim.addActionListener(e -> runCronPhp());
        panelKirim.add(BtnKirim);

        BtnRefreshStat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnRefreshStat.setText("Refresh Statistik");
        BtnRefreshStat.setPreferredSize(new Dimension(160, 23));
        BtnRefreshStat.addActionListener(e -> loadStatus());
        panelKirim.add(BtnRefreshStat);

        panelStatus.add(panelKirim, java.awt.BorderLayout.SOUTH);

        // ---------- Tabs & Frame ----------
        TabPane.addTab("Grup WA", panelGrup);
        TabPane.addTab("Konfigurasi GOWA", panelKonfig);
        TabPane.addTab("Status & Kirim", panelStatus);

        internalFrame1.add(TabPane, java.awt.BorderLayout.CENTER);

        panelBottom.setPreferredSize(new Dimension(44, 36));
        panelBottom.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 6));

        BtnStokMinDepo = new widget.Button();
        BtnStokMinDepo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnStokMinDepo.setText("Stok Minimal per Depo");
        BtnStokMinDepo.setToolTipText("Kelola override stok minimal per bangsal (depo farmasi)");
        BtnStokMinDepo.setPreferredSize(new Dimension(195, 23));
        BtnStokMinDepo.addActionListener(e -> {
            DlgStokMinimalDepo d = new DlgStokMinimalDepo(
                (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this), false);
            d.setLocationRelativeTo(this);
            d.setVisible(true);
        });
        panelBottom.add(BtnStokMinDepo);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setPreferredSize(new Dimension(100, 23));
        BtnKeluar.addActionListener(e -> dispose());
        panelBottom.add(BtnKeluar);
        internalFrame1.add(panelBottom, java.awt.BorderLayout.SOUTH);

        getContentPane().setLayout(new java.awt.BorderLayout(1, 1));
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }

    // =================================================================
    // DB: Bangsal dropdown
    // =================================================================
    private void loadBangsal() {
        cbBangsal.removeAllItems();
        cbBangsal.addItem(new BangsalItem("", "(kosong — grup tidak dapat notif)"));
        String sql = "SELECT kd_bangsal, nm_bangsal FROM bangsal WHERE status='1' "
                   + "AND kd_bangsal <> '-' ORDER BY nm_bangsal";
        try (PreparedStatement p = koneksi.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                cbBangsal.addItem(new BangsalItem(r.getString(1), r.getString(2)));
            }
        } catch (SQLException ex) {
            showErr("Gagal muat bangsal", ex);
        }
    }

    private void selectBangsal(String kd) {
        for (int i = 0; i < cbBangsal.getItemCount(); i++) {
            Object o = cbBangsal.getItemAt(i);
            if (o instanceof BangsalItem && ((BangsalItem) o).kd.equals(kd)) {
                cbBangsal.setSelectedIndex(i);
                return;
            }
        }
        cbBangsal.setSelectedIndex(0);
    }

    // =================================================================
    // DB: Grup CRUD
    // =================================================================
    private void tampilGrup() {
        modelGrup.setRowCount(0);
        String sql = "SELECT g.id, g.nama_grup, g.jid, g.deviceid_gowa, g.kd_bangsal, "
                   + "g.keterangan, g.aktif, IFNULL(b.nm_bangsal,'') AS nm_bangsal "
                   + "FROM notif_wa_grup g "
                   + "LEFT JOIN bangsal b ON b.kd_bangsal = g.kd_bangsal "
                   + "ORDER BY g.nama_grup";
        try (PreparedStatement p = koneksi.prepareStatement(sql);
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                String kd = r.getString("kd_bangsal");
                if (kd == null) kd = "";
                String nm = r.getString("nm_bangsal");
                String bang = kd.isEmpty() ? "(kosong)"
                           : (nm == null || nm.isEmpty() ? kd : (kd + " / " + nm));
                modelGrup.addRow(new Object[]{
                    r.getInt("id"),
                    r.getString("nama_grup"),
                    r.getString("jid"),
                    r.getString("deviceid_gowa"),
                    bang,
                    r.getString("keterangan"),
                    r.getInt("aktif") == 1 ? "Aktif" : "-"
                });
            }
        } catch (SQLException ex) {
            showErr("Gagal muat grup WA", ex);
        }
    }

    private void tbGrupMouseClicked(java.awt.event.MouseEvent evt) {
        int row = tbGrup.getSelectedRow();
        if (row < 0) return;
        editId = (Integer) modelGrup.getValueAt(row, 0);
        TNama.setText(str(modelGrup.getValueAt(row, 1)));
        TJid.setText(str(modelGrup.getValueAt(row, 2)));
        TDevice.setText(str(modelGrup.getValueAt(row, 3)));
        TKeterangan.setText(str(modelGrup.getValueAt(row, 5)));
        chkAktif.setSelected("Aktif".equals(modelGrup.getValueAt(row, 6)));

        // Ambil kd_bangsal asli
        try (PreparedStatement p = koneksi.prepareStatement(
                "SELECT kd_bangsal FROM notif_wa_grup WHERE id=?")) {
            p.setInt(1, editId);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    String kd = r.getString(1);
                    selectBangsal(kd == null ? "" : kd);
                }
            }
        } catch (SQLException ex) {
            showErr("Gagal ambil detail", ex);
        }
    }

    private void clearForm() {
        editId = 0;
        TNama.setText("");
        TJid.setText("");
        TDevice.setText("");
        TKeterangan.setText("");
        cbBangsal.setSelectedIndex(0);
        chkAktif.setSelected(true);
        tbGrup.clearSelection();
        TNama.requestFocusInWindow();
    }

    private void simpanGrup() {
        String nama = TNama.getText().trim();
        String jid = TJid.getText().trim();
        if (nama.isEmpty() || jid.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama Grup dan JID wajib diisi.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Object sel = cbBangsal.getSelectedItem();
        String kd = (sel instanceof BangsalItem) ? ((BangsalItem) sel).kd : "";

        try {
            if (editId == 0) {
                try (PreparedStatement p = koneksi.prepareStatement(
                    "INSERT INTO notif_wa_grup "
                    + "(nama_grup, jid, deviceid_gowa, kd_bangsal, keterangan, aktif) "
                    + "VALUES (?,?,?,?,?,?)")) {
                    p.setString(1, nama);
                    p.setString(2, jid);
                    p.setString(3, TDevice.getText().trim());
                    p.setString(4, kd);
                    p.setString(5, TKeterangan.getText().trim());
                    p.setInt(6, chkAktif.isSelected() ? 1 : 0);
                    p.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Grup ditambahkan.");
            } else {
                try (PreparedStatement p = koneksi.prepareStatement(
                    "UPDATE notif_wa_grup SET nama_grup=?, jid=?, deviceid_gowa=?, "
                    + "kd_bangsal=?, keterangan=?, aktif=? WHERE id=?")) {
                    p.setString(1, nama);
                    p.setString(2, jid);
                    p.setString(3, TDevice.getText().trim());
                    p.setString(4, kd);
                    p.setString(5, TKeterangan.getText().trim());
                    p.setInt(6, chkAktif.isSelected() ? 1 : 0);
                    p.setInt(7, editId);
                    p.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Grup diperbarui.");
            }
            clearForm();
            tampilGrup();
            loadStatus();
        } catch (SQLException ex) {
            showErr("Gagal simpan grup", ex);
        }
    }

    private void hapusGrup() {
        if (editId == 0) {
            JOptionPane.showMessageDialog(this, "Pilih baris di tabel terlebih dahulu.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
                "Hapus grup \"" + TNama.getText() + "\" ?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        try (PreparedStatement p = koneksi.prepareStatement(
                "DELETE FROM notif_wa_grup WHERE id=?")) {
            p.setInt(1, editId);
            p.executeUpdate();
            JOptionPane.showMessageDialog(this, "Grup dihapus.");
            clearForm();
            tampilGrup();
            loadStatus();
        } catch (SQLException ex) {
            showErr("Gagal hapus grup", ex);
        }
    }

    // =================================================================
    // DB: Config
    // =================================================================
    private void loadConfig() {
        Map<String, String> cfg = new LinkedHashMap<>();
        try (PreparedStatement p = koneksi.prepareStatement(
                "SELECT `key`, `value` FROM config_notif_wa");
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                String v = r.getString(2);
                cfg.put(r.getString(1), v == null ? "" : v);
            }
        } catch (SQLException ex) {
            showErr("Gagal muat config", ex);
            return;
        }
        TUrl.setText(cfg.getOrDefault("url_gowa", ""));
        TUser.setText(cfg.getOrDefault("user_gowa", ""));
        TPass.setText(cfg.getOrDefault("password_gowa", ""));
        TDevGlobal.setText(cfg.getOrDefault("deviceid_gowa", ""));
        TNamaRs.setText(cfg.getOrDefault("nama_rs", ""));
        TJamMulai.setText(cfg.getOrDefault("jam_mulai", "7"));
        TJamSelesai.setText(cfg.getOrDefault("jam_selesai", "20"));
        TCooldown.setText(cfg.getOrDefault("cooldown_hours", "24"));
        TCritical.setText(cfg.getOrDefault("critical_threshold", "0.25"));
        TMax.setText(cfg.getOrDefault("max_items", "50"));
        chkEnabled.setSelected("1".equals(cfg.getOrDefault("enabled", "1")));
    }

    private void simpanConfig() {
        Map<String, String> kv = new LinkedHashMap<>();
        kv.put("url_gowa", TUrl.getText().trim());
        kv.put("user_gowa", TUser.getText().trim());
        kv.put("password_gowa", new String(TPass.getPassword()));
        kv.put("deviceid_gowa", TDevGlobal.getText().trim());
        kv.put("nama_rs", TNamaRs.getText().trim());
        kv.put("jam_mulai", TJamMulai.getText().trim());
        kv.put("jam_selesai", TJamSelesai.getText().trim());
        kv.put("cooldown_hours", TCooldown.getText().trim());
        kv.put("critical_threshold", TCritical.getText().trim());
        kv.put("max_items", TMax.getText().trim());
        kv.put("enabled", chkEnabled.isSelected() ? "1" : "0");

        String sql = "INSERT INTO config_notif_wa (`key`, `value`) VALUES (?, ?) "
                   + "ON DUPLICATE KEY UPDATE `value` = VALUES(`value`)";
        try (PreparedStatement p = koneksi.prepareStatement(sql)) {
            for (Map.Entry<String, String> e : kv.entrySet()) {
                p.setString(1, e.getKey());
                p.setString(2, e.getValue());
                p.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Konfigurasi disimpan.");
        } catch (SQLException ex) {
            showErr("Gagal simpan config", ex);
        }
    }

    // =================================================================
    // Status & statistik
    // =================================================================
    private void loadStatus() {
        long total = 0, frm00 = 0, frm01 = 0;
        try (PreparedStatement p = koneksi.prepareStatement(
                "SELECT kd_bangsal, COUNT(*) c FROM v_stok_minimal_alert GROUP BY kd_bangsal");
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                long c = r.getLong("c");
                total += c;
                String kd = r.getString("kd_bangsal");
                if ("FRM00".equals(kd)) frm00 = c;
                else if ("FRM01".equals(kd)) frm01 = c;
            }
        } catch (SQLException ex) {
            txtLog.setText("Gagal muat statistik: " + ex.getMessage()
                    + "\n(Pastikan view v_stok_minimal_alert sudah ada — run 02_bangsal.sql)");
            lblTotalItems.setText("?"); lblItemFRM00.setText("?"); lblItemFRM01.setText("?");
            return;
        }
        lblTotalItems.setText(String.valueOf(total));
        lblItemFRM00.setText(String.valueOf(frm00));
        lblItemFRM01.setText(String.valueOf(frm01));

        StringBuilder info = new StringBuilder("<html>Grup aktif: ");
        try (PreparedStatement p = koneksi.prepareStatement(
                "SELECT nama_grup, kd_bangsal FROM notif_wa_grup "
                + "WHERE aktif=1 AND kd_bangsal<>'' ORDER BY nama_grup");
             ResultSet r = p.executeQuery()) {
            boolean any = false;
            while (r.next()) {
                if (any) info.append("  ·  ");
                any = true;
                info.append("<b>").append(r.getString(1)).append("</b>")
                    .append(" (").append(r.getString(2)).append(")");
            }
            if (!any) info.append("<i>(tidak ada — isi kolom Bangsal di tab Grup WA)</i>");
        } catch (SQLException ignore) {}
        info.append("</html>");
        lblGrupAktif.setText(info.toString());

        StringBuilder logs = new StringBuilder();
        logs.append("30 log terakhir:\n");
        try (PreparedStatement p = koneksi.prepareStatement(
                "SELECT tgl_notif, target_grup, kd_bangsal, status_kirim, COUNT(*) items "
                + "FROM log_notif_stok_minimal "
                + "GROUP BY tgl_notif, target_grup, kd_bangsal, status_kirim "
                + "ORDER BY tgl_notif DESC LIMIT 30");
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                logs.append(String.format("%-20s  %-20s  %-8s  %-8s  %4d item%n",
                    r.getString(1), r.getString(2), r.getString(3), r.getString(4), r.getInt(5)));
            }
        } catch (SQLException ex) {
            logs.append("[error] ").append(ex.getMessage());
        }
        txtLog.setText(logs.toString());
        txtLog.setCaretPosition(0);
    }

    private void runCronPhp() {
        String[] candidates = {
            "/var/www/html/webappsrscb/notif-stok-farmasi/cron/run.php",
            "/var/www/html/notif-stok-farmasi/cron/run.php"
        };
        String path = null;
        for (String c : candidates) {
            if (new java.io.File(c).exists()) { path = c; break; }
        }
        if (path == null) {
            JOptionPane.showMessageDialog(this,
                "File cron/run.php tidak ditemukan di server ini.\n"
                + "Kirim manual dari server web (pakai crontab / terminal).",
                "Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "/usr/bin/php", path, "--force", "--ignore-hours", "--source=java");
            pb.redirectErrorStream(true);
            Process proc = pb.start();
            StringBuilder sb = new StringBuilder();
            try (java.io.BufferedReader br = new java.io.BufferedReader(
                    new java.io.InputStreamReader(proc.getInputStream()))) {
                String line;
                while ((line = br.readLine()) != null) sb.append(line).append("\n");
            }
            proc.waitFor();
            txtLog.setText(sb.toString());
            txtLog.setCaretPosition(0);
            loadStatus();
        } catch (Exception ex) {
            showErr("Gagal jalankan cron/run.php", ex);
        }
    }

    // =================================================================
    // Helpers
    // =================================================================
    private static String str(Object o) { return o == null ? "" : o.toString(); }

    private void showErr(String title, Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), title, JOptionPane.ERROR_MESSAGE);
    }

    // Helper class untuk ComboBox bangsal
    private static final class BangsalItem {
        final String kd, nm;
        BangsalItem(String kd, String nm) { this.kd = kd; this.nm = nm; }
        @Override public String toString() {
            return kd.isEmpty() ? nm : (kd + " — " + nm);
        }
    }

    // =================================================================
    // Component declarations
    // =================================================================
    private widget.InternalFrame internalFrame1;
    private widget.TabPane TabPane;
    // Tab 1: Grup WA
    private javax.swing.JPanel panelGrup;
    private widget.ScrollPane Scroll;
    private widget.Table tbGrup;
    private widget.panelisi panelDetail;
    private widget.panelisi panelAksi;
    private widget.Label lblNama, lblJid, lblDevice, lblBangsal, lblKeterangan;
    private widget.TextBox TNama, TJid, TDevice, TKeterangan;
    private widget.ComboBox cbBangsal;
    private widget.CekBox chkAktif;
    private widget.Button BtnSimpan, BtnHapus, BtnBatal, BtnEdit, BtnRefresh;
    // Tab 2: Konfigurasi
    private javax.swing.JPanel panelKonfig;
    private widget.panelisi panelKonfigIsi;
    private widget.Label lblUrl, lblUser, lblPass, lblDevGlobal, lblNamaRs,
                         lblJamMulai, lblJamSelesai, lblCooldown, lblCritical, lblMax;
    private widget.TextBox TUrl, TUser, TDevGlobal, TNamaRs,
                           TJamMulai, TJamSelesai, TCooldown, TCritical, TMax;
    private widget.PasswordBox TPass;
    private widget.CekBox chkEnabled;
    private widget.Button BtnSimpanConfig, BtnReloadConfig;
    // Tab 3: Status & Kirim
    private javax.swing.JPanel panelStatus;
    private widget.panelisi panelStatAtas;
    private widget.panelisi panelKirim;
    private widget.ScrollPane ScrollLog;
    private widget.TextArea txtLog;
    private widget.Label lblTotalItems, lblItemFRM00, lblItemFRM01, lblGrupAktif;
    private widget.Button BtnKirim, BtnRefreshStat;
    // Bottom
    private widget.panelisi panelBottom;
    private widget.Button BtnStokMinDepo, BtnKeluar;

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgNotifStokFarmasi d = new DlgNotifStokFarmasi(null, true);
            d.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            d.setVisible(true);
        });
    }
}
