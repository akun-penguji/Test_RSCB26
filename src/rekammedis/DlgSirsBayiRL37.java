package rekammedis;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Form input data SIRS RL 3.7 (Neonatal, Bayi, dan Balita) untuk field JUKNIS
 * yang TIDAK ada di skema Khanza: perawatan metode kanguru, IMD, skrining
 * hipotiroid kongenital (neonatal); Buku KIA & skrining tumbuh-kembang (balita).
 * Data disimpan ke tabel pendamping data_bayi_sirs; laporan RL 3.7 membacanya.
 *
 * Worklist menampilkan kunjungan pasien usia < 5 tahun pada rentang tanggal;
 * pilih baris lalu isi field, Simpan (upsert per no_rawat).
 */
public final class DlgSirsBayiRL37 extends javax.swing.JDialog {

    private static final String[] YN = {"Tidak", "Ya"};
    /** Kolom data_bayi_sirs (selain no_rawat) + label combo, urutan sama. */
    private static final String[] FIELDS = {
        "metode_kanguru", "imd", "skrining_hipotiroid", "buku_kia",
        "skr_pertumbuhan", "skr_perkembangan", "skr_bicara", "skr_motorik",
        "skr_perilaku", "skr_pendengaran", "skr_penglihatan"
    };
    private static final String[] LABELS = {
        "Metode Kanguru", "IMD", "Skrining Hipotiroid", "Buku KIA",
        "Skr. Pertumbuhan", "Skr. Perkembangan", "Skr. Bicara/Bahasa", "Skr. Motorik",
        "Skr. Perilaku", "Skr. Pendengaran", "Skr. Penglihatan"
    };

    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();
    private DefaultTableModel tabMode;
    private String rmAktif = ""; // no_rkm_medis anak terpilih (key data_bayi_sirs)

    private widget.InternalFrame internalFrame1;
    private widget.ScrollPane Scroll;
    private widget.Table tbData;
    private widget.Tanggal DTP1, DTP2;
    private widget.TextBox TCari, TNoRw, TPasien;
    private final widget.ComboBox[] combo = new widget.ComboBox[FIELDS.length];

    public DlgSirsBayiRL37(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initFrame();
        muat();
        setSize(940, 620);
        setLocationRelativeTo(null);
    }

    private void initFrame() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Input SIRS RL 3.7 - Neonatal, Bayi, dan Balita");
        setUndecorated(true);
        internalFrame1 = new widget.InternalFrame();
        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
                "::[ Input Data SIRS RL 3.7 (Neonatal / Bayi / Balita) ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // ── Filter (atas) ──
        widget.panelisi filter = new widget.panelisi();
        filter.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 8));
        filter.setPreferredSize(new Dimension(60, 42));
        DTP1 = new widget.Tanggal();
        DTP2 = new widget.Tanggal();
        DTP1.setDate(new Date());
        DTP2.setDate(new Date());
        TCari = new widget.TextBox();
        TCari.setPreferredSize(new Dimension(160, 25));
        widget.Button btnTampil = tombol("Tampilkan", evt -> muat());
        filter.add(new JLabel("Tanggal :"));
        filter.add(DTP1);
        filter.add(new JLabel("s/d"));
        filter.add(DTP2);
        filter.add(new JLabel("Cari Nama :"));
        filter.add(TCari);
        filter.add(btnTampil);
        internalFrame1.add(filter, BorderLayout.PAGE_START);

        // ── Tabel worklist (tengah) ──
        tabMode = new DefaultTableModel(null, new String[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Umur", "Tgl Registrasi", "Terisi?"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbData = new widget.Table();
        tbData.setModel(tabMode);
        tbData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbData.setDefaultRenderer(Object.class, new WarnaTable());
        tbData.setRowHeight(22);
        int[] w = {110, 75, 230, 90, 130, 70};
        for (int i = 0; i < w.length; i++) tbData.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        tbData.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { pilihBaris(); }
        });
        Scroll = new widget.ScrollPane();
        Scroll.setViewportView(tbData);
        internalFrame1.add(Scroll, BorderLayout.CENTER);

        // ── Panel input + tombol (bawah) ──
        widget.panelisi bawah = new widget.panelisi();
        bawah.setPreferredSize(new Dimension(60, 210));
        bawah.setLayout(new BorderLayout());

        widget.PanelBiasa grid = new widget.PanelBiasa();
        grid.setLayout(null);
        JLabel lr = new JLabel("No.Rawat :"); lr.setBounds(6, 8, 70, 23); grid.add(lr);
        TNoRw = new widget.TextBox(); TNoRw.setEditable(false); TNoRw.setBounds(78, 8, 130, 23); grid.add(TNoRw);
        JLabel lp = new JLabel("Pasien :"); lp.setBounds(220, 8, 55, 23); grid.add(lp);
        TPasien = new widget.TextBox(); TPasien.setEditable(false); TPasien.setBounds(275, 8, 300, 23); grid.add(TPasien);

        // 11 field: grid 3 kolom.
        int colW = 300, x0 = 6, y0 = 40, dy = 30, labW = 128, cbW = 75;
        for (int i = 0; i < FIELDS.length; i++) {
            int col = i % 3, row = i / 3;
            int x = x0 + col * colW, y = y0 + row * dy;
            JLabel l = new JLabel(LABELS[i] + " :"); l.setBounds(x, y, labW, 23); grid.add(l);
            combo[i] = new widget.ComboBox();
            combo[i].setModel(new javax.swing.DefaultComboBoxModel(YN));
            combo[i].setBounds(x + labW, y, cbW, 23);
            grid.add(combo[i]);
        }
        bawah.add(grid, BorderLayout.CENTER);

        widget.panelisi tombol = new widget.panelisi();
        tombol.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 8));
        tombol.add(tombol("Simpan", evt -> simpan()));
        tombol.add(tombol("Baru", evt -> emptTeks()));
        tombol.add(tombol("Keluar", evt -> dispose()));
        bawah.add(tombol, BorderLayout.PAGE_END);

        internalFrame1.add(bawah, BorderLayout.PAGE_END);
        getContentPane().add(internalFrame1, BorderLayout.CENTER);
    }

    private widget.Button tombol(String teks, java.awt.event.ActionListener act) {
        widget.Button b = new widget.Button();
        b.setText(teks);
        b.setPreferredSize(new Dimension(Math.max(90, 24 + teks.length() * 9), 30));
        b.addActionListener(act);
        return b;
    }

    /** Muat kunjungan pasien usia < 5 tahun pada rentang tanggal. */
    private void muat() {
        tabMode.setRowCount(0);
        String d1 = Valid.SetTgl(DTP1.getSelectedItem() + "");
        String d2 = Valid.SetTgl(DTP2.getSelectedItem() + "");
        String cari = "%" + TCari.getText().trim() + "%";
        String sql = "SELECT rp.no_rawat, rp.no_rkm_medis, ps.nm_pasien, "
                + "CONCAT(rp.umurdaftar,' ',rp.sttsumur) umur, rp.tgl_registrasi, "
                + "IF(b.no_rkm_medis IS NULL,'', 'Ya') terisi "
                + "FROM reg_periksa rp JOIN pasien ps ON ps.no_rkm_medis=rp.no_rkm_medis "
                + "LEFT JOIN data_bayi_sirs b ON b.no_rkm_medis=rp.no_rkm_medis "
                + "WHERE rp.tgl_registrasi BETWEEN ? AND ? AND rp.stts<>'Batal' AND ps.nm_pasien LIKE ? "
                + "AND ((rp.sttsumur='Th' AND CAST(rp.umurdaftar AS UNSIGNED)<5) OR rp.sttsumur IN ('Bl','Hr')) "
                + "ORDER BY rp.tgl_registrasi DESC, ps.nm_pasien";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, d1);
            ps.setString(2, d2);
            ps.setString(3, cari);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabMode.addRow(new Object[]{rs.getString("no_rawat"), rs.getString("no_rkm_medis"),
                        rs.getString("nm_pasien"), rs.getString("umur"), rs.getString("tgl_registrasi"),
                        rs.getString("terisi")});
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data :\n" + e);
            System.out.println("DlgSirsBayiRL37 muat : " + e);
        }
    }

    /** Baris dipilih -> isi No.Rawat + Pasien + muat data_bayi_sirs ke combo. */
    private void pilihBaris() {
        int r = tbData.getSelectedRow();
        if (r < 0) return;
        TNoRw.setText(String.valueOf(tabMode.getValueAt(r, 0)));
        rmAktif = String.valueOf(tabMode.getValueAt(r, 1));
        TPasien.setText(String.valueOf(tabMode.getValueAt(r, 2)));
        for (widget.ComboBox c : combo) c.setSelectedIndex(0);
        try (PreparedStatement ps = koneksi.prepareStatement(
                "SELECT " + String.join(",", FIELDS) + " FROM data_bayi_sirs WHERE no_rkm_medis=?")) {
            ps.setString(1, rmAktif);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    for (int i = 0; i < FIELDS.length; i++) combo[i].setSelectedItem(rs.getString(FIELDS[i]));
                }
            }
        } catch (Exception e) {
            System.out.println("DlgSirsBayiRL37 pilihBaris : " + e);
        }
    }

    private void emptTeks() {
        TNoRw.setText("");
        TPasien.setText("");
        rmAktif = "";
        for (widget.ComboBox c : combo) c.setSelectedIndex(0);
    }

    /** Upsert data_bayi_sirs untuk no_rkm_medis anak terpilih. */
    private void simpan() {
        if (rmAktif.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih baris pasien dulu.");
            return;
        }
        String u = akses.getkode() == null ? "" : akses.getkode();
        if (u.length() > 20) u = u.substring(0, 20);
        StringBuilder cols = new StringBuilder("no_rkm_medis"), vals = new StringBuilder("?"), upd = new StringBuilder();
        for (String f : FIELDS) {
            cols.append(",").append(f);
            vals.append(",?");
            if (upd.length() > 0) upd.append(",");
            upd.append(f).append("=VALUES(").append(f).append(")");
        }
        cols.append(",updated_by");
        vals.append(",?");
        upd.append(",updated_by=VALUES(updated_by),updated_at=NOW()");
        String sql = "INSERT INTO data_bayi_sirs (" + cols + ") VALUES (" + vals + ") ON DUPLICATE KEY UPDATE " + upd;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, rmAktif);
            for (int i = 0; i < FIELDS.length; i++) ps.setString(i + 2, combo[i].getSelectedItem().toString());
            ps.setString(FIELDS.length + 2, u);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data SIRS RL 3.7 tersimpan.");
            int r = tbData.getSelectedRow();
            if (r >= 0) tabMode.setValueAt("Ya", r, 5);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan :\n" + e);
            System.out.println("DlgSirsBayiRL37 simpan : " + e);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgSirsBayiRL37 d = new DlgSirsBayiRL37(new javax.swing.JFrame(), true);
            d.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosing(java.awt.event.WindowEvent e) { System.exit(0); }
            });
            d.setVisible(true);
        });
    }
}
