package simrskhanza;

import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Worklist / monitoring NILAI KRITIS lab (KARS PAP 3). Menampilkan
 * {@code audit_nilai_kritis} dgn filter tanggal & status, tindak lanjut
 * pelaporan (buka {@link DlgLaporKritis}), tandai "tidak perlu", dan cetak
 * rekap jasper (rptLaporanNilaiKritis).
 */
public class DlgDaftarNilaiKritis extends JDialog {

    private final validasi Valid = new validasi();
    private final fungsi.sekuel Sequel = new fungsi.sekuel();
    private final DefaultTableModel model = new DefaultTableModel(null, new Object[]{
        "id", "Tgl", "Jam", "No.RM", "Pasien", "Pemeriksaan", "Hasil", "Sat",
        "Kritis", "Status", "Petugas Input", "Waktu Notif", "no_rawat"
    }) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabel = new JTable(model);
    private final JTextField Tgl1 = new JTextField(10);
    private final JTextField Tgl2 = new JTextField(10);
    private final JComboBox<String> CmbStatus =
        new JComboBox<>(new String[]{"Semua", "PENDING", "TERNOTIFIKASI", "TIDAK_PERLU"});
    private final JLabel lblInfo = new JLabel(" ");

    public DlgDaftarNilaiKritis(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Daftar Nilai Kritis Lab — KARS PAP 3");

        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        long now = System.currentTimeMillis();
        Tgl1.setText(f.format(new java.util.Date(now - 7L*24*60*60*1000)));
        Tgl2.setText(f.format(new java.util.Date(now)));

        // ---- toolbar filter ----
        JPanel bar = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 6));
        bar.add(new JLabel("Tgl:"));
        bar.add(Tgl1);
        bar.add(new JLabel("s/d"));
        bar.add(Tgl2);
        bar.add(new JLabel("Status:"));
        bar.add(CmbStatus);
        JButton btnCari = new JButton("Tampilkan");
        btnCari.addActionListener(e -> muat());
        bar.add(btnCari);

        // ---- table ----
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.setRowHeight(22);
        tabel.getColumnModel().getColumn(0).setMinWidth(0);
        tabel.getColumnModel().getColumn(0).setMaxWidth(0);   // sembunyikan id
        tabel.getColumnModel().getColumn(0).setWidth(0);
        int last = model.getColumnCount() - 1;
        tabel.getColumnModel().getColumn(last).setMinWidth(0); // sembunyikan no_rawat
        tabel.getColumnModel().getColumn(last).setMaxWidth(0);
        // status berwarna
        tabel.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public java.awt.Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                java.awt.Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                String s = v == null ? "" : v.toString();
                if ("PENDING".equals(s)) comp.setForeground(new Color(200,0,0));
                else if ("TERNOTIFIKASI".equals(s)) comp.setForeground(new Color(0,130,60));
                else comp.setForeground(sel ? t.getSelectionForeground() : t.getForeground());
                comp.setFont(comp.getFont().deriveFont("PENDING".equals(s) ? Font.BOLD : Font.PLAIN));
                return comp;
            }
        });

        // ---- action buttons ----
        JPanel south = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 6));
        JButton btnLapor = new JButton("Lapor (TBaK)");
        btnLapor.setBackground(new Color(200,0,0)); btnLapor.setForeground(Color.WHITE);
        btnLapor.addActionListener(e -> laporTerpilih());
        JButton btnTidakPerlu = new JButton("Tandai Tidak Perlu");
        btnTidakPerlu.addActionListener(e -> tandaiTidakPerlu());
        JButton btnCetak = new JButton("Cetak Rekap");
        btnCetak.addActionListener(e -> cetak());
        JButton btnTutup = new JButton("Tutup");
        btnTutup.addActionListener(e -> dispose());
        south.add(btnLapor);
        south.add(btnTidakPerlu);
        south.add(btnCetak);
        south.add(btnTutup);
        south.add(lblInfo);

        JPanel head = new JPanel(new BorderLayout());
        head.add(bar, BorderLayout.CENTER);
        head.setBorder(BorderFactory.createEmptyBorder(2,4,0,4));

        setLayout(new BorderLayout());
        add(head, BorderLayout.NORTH);
        add(new JScrollPane(tabel), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
        setSize(1000, 560);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        muat();
    }

    /** Dipanggil frmUtama setelah setVisible (konvensi Khanza). */
    public void isCek() { muat(); }

    private void muat() {
        model.setRowCount(0);
        String status = String.valueOf(CmbStatus.getSelectedItem());
        StringBuilder sql = new StringBuilder(
            "SELECT a.id, a.tgl_kejadian, a.jam_kejadian, a.no_rawat, a.no_rkm_medis, " +
            "IFNULL(p.nm_pasien,'') nm_pasien, a.nama_pemeriksaan, a.nilai_hasil, IFNULL(a.satuan,'') satuan, " +
            "a.kritis_min, a.kritis_max, a.status_notifikasi, IFNULL(a.petugas_nama,'') petugas_nama, a.waktu_notifikasi " +
            "FROM audit_nilai_kritis a LEFT JOIN pasien p ON p.no_rkm_medis=a.no_rkm_medis " +
            "WHERE a.tgl_kejadian BETWEEN ? AND ? ");
        if (!"Semua".equals(status)) sql.append("AND a.status_notifikasi=? ");
        sql.append("ORDER BY a.tgl_kejadian DESC, a.jam_kejadian DESC");

        int n = 0;
        try (Connection conn = koneksiDB.condb();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setString(1, Tgl1.getText().trim());
            ps.setString(2, Tgl2.getText().trim());
            if (!"Semua".equals(status)) ps.setString(3, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String kMin = rs.getString("kritis_min");
                    String kMax = rs.getString("kritis_max");
                    String rng = "";
                    if (kMin != null && !kMin.isEmpty()) rng += "< " + kMin;
                    if (kMax != null && !kMax.isEmpty()) rng += (rng.isEmpty() ? "" : " / ") + "> " + kMax;
                    java.sql.Timestamp wn = rs.getTimestamp("waktu_notifikasi");
                    model.addRow(new Object[]{
                        rs.getInt("id"), rs.getString("tgl_kejadian"), rs.getString("jam_kejadian"),
                        rs.getString("no_rkm_medis"), rs.getString("nm_pasien"), rs.getString("nama_pemeriksaan"),
                        rs.getString("nilai_hasil"), rs.getString("satuan"), rng,
                        rs.getString("status_notifikasi"), rs.getString("petugas_nama"),
                        wn == null ? "" : wn.toString(), rs.getString("no_rawat")
                    });
                    n++;
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data: " + ex.getMessage());
            System.out.println("DlgDaftarNilaiKritis.muat: " + ex);
        }
        int pending = 0;
        for (int i = 0; i < model.getRowCount(); i++)
            if ("PENDING".equals(model.getValueAt(i, 9))) pending++;
        lblInfo.setText("   " + n + " data  (" + pending + " PENDING belum dilaporkan)");
    }

    private void laporTerpilih() {
        int r = tabel.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Pilih satu baris dulu."); return; }
        if (!"PENDING".equals(model.getValueAt(r, 9))) {
            int t = JOptionPane.showConfirmDialog(this,
                "Baris ini sudah berstatus " + model.getValueAt(r, 9) + ".\nTetap catat pelaporan lagi?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (t != JOptionPane.YES_OPTION) return;
        }
        List<Integer> ids = new ArrayList<>();
        ids.add((Integer) model.getValueAt(r, 0));
        DlgLaporKritis d = new DlgLaporKritis(null, true, ids,
            String.valueOf(model.getValueAt(r, 12)), String.valueOf(model.getValueAt(r, 3)),
            String.valueOf(model.getValueAt(r, 4)), akses.getkode(), akses.getnamauser());
        d.setLocationRelativeTo(this);
        d.setVisible(true);
        muat();
    }

    private void tandaiTidakPerlu() {
        int r = tabel.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Pilih satu baris dulu."); return; }
        int t = JOptionPane.showConfirmDialog(this,
            "Tandai nilai kritis ini 'TIDAK_PERLU' dilaporkan?\n(mis. hasil sudah diketahui/diverifikasi ulang)",
            "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (t != JOptionPane.YES_OPTION) return;
        try (Connection conn = koneksiDB.condb();
             PreparedStatement ps = conn.prepareStatement(
                 "UPDATE audit_nilai_kritis SET status_notifikasi='TIDAK_PERLU', " +
                 "waktu_notifikasi=NOW(), petugas_notifikasi=? WHERE id=?")) {
            ps.setString(1, akses.getnamauser() == null ? akses.getkode() : akses.getnamauser());
            ps.setInt(2, (Integer) model.getValueAt(r, 0));
            ps.executeUpdate();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage());
        }
        muat();
    }

    private void cetak() {
        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        param.put("periode", Tgl1.getText().trim() + " s/d " + Tgl2.getText().trim());
        String status = String.valueOf(CmbStatus.getSelectedItem());
        String filterStatus = "Semua".equals(status) ? "" : " and a.status_notifikasi='" + status + "' ";
        Valid.MyReportqry("rptLaporanNilaiKritis.jasper", "report",
            "::[ Laporan Nilai Kritis Lab (PAP 3) ]::",
            "select a.tgl_kejadian, a.jam_kejadian, a.no_rawat, a.no_rkm_medis, ifnull(p.nm_pasien,'') nm_pasien, " +
            "a.nama_pemeriksaan, a.nilai_hasil, ifnull(a.satuan,'') satuan, a.kritis_min, a.kritis_max, " +
            "a.status_notifikasi, ifnull(a.petugas_nama,'') petugas_nama, a.waktu_notifikasi, " +
            "ifnull(l.penerima_nm_dokter,'') penerima, ifnull(l.penerima_tipe,'') penerima_tipe, " +
            "ifnull(l.metode_komunikasi,'') metode, l.read_back_confirmed, ifnull(l.instruksi_tindak_lanjut,'') instruksi, " +
            "ifnull(l.petugas_pelapor_nama,'') pelapor, l.waktu_lapor " +
            "from audit_nilai_kritis a " +
            "left join pasien p on p.no_rkm_medis=a.no_rkm_medis " +
            "left join lapor_kritis_rm l on l.id = (select max(l2.id) from lapor_kritis_rm l2 where l2.id_audit=a.id) " +
            "where a.tgl_kejadian between '" + Tgl1.getText().trim() + "' and '" + Tgl2.getText().trim() + "' " +
            filterStatus +
            "order by a.tgl_kejadian desc, a.jam_kejadian desc", param);
    }
}
