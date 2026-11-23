package rekammedis;

import fungsi.koneksiDB;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * Daftar Singkatan, Simbol, & Kode dalam Rekam Medis (SK Direktur RSUD
 * Cabangbungin No. 400.7.31/112/RSUD-CB/2026). Sumber data: tabel
 * master_singkatan_rscb. Read-only viewer + pencarian + filter kategori.
 */
public class RMDaftarSingkatan extends javax.swing.JDialog {

    private final Connection koneksi = koneksiDB.condb();
    private final JTextField txtCari = new JTextField();
    private final JComboBox<String> cmbKategori =
            new JComboBox<>(new String[]{"Semua", "Diperbolehkan", "Dilarang", "Simbol"});
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"No", "Kategori", "Singkatan", "Keterangan / Maksud", "Misinterpretasi", "Wajib Gunakan"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabel = new JTable(model);
    private final JLabel lblInfo = new JLabel(" ");

    public RMDaftarSingkatan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Daftar Singkatan, Simbol & Kode dalam Rekam Medis - RSUD Cabangbungin");
        setSize(920, 560);
        setLocationRelativeTo(parent);
        buildUI();
        tampil();
    }

    private void buildUI() {
        getContentPane().setLayout(new BorderLayout(6, 6));

        JPanel atas = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 8));
        atas.add(new JLabel("Cari:"));
        txtCari.setPreferredSize(new Dimension(260, 26));
        txtCari.setToolTipText("Ketik singkatan atau keterangan, lalu Enter");
        atas.add(txtCari);
        atas.add(new JLabel("Kategori:"));
        cmbKategori.setPreferredSize(new Dimension(150, 26));
        atas.add(cmbKategori);
        JButton btnCari = new JButton("Tampilkan");
        atas.add(btnCari);
        JButton btnReset = new JButton("Reset");
        atas.add(btnReset);
        getContentPane().add(atas, BorderLayout.NORTH);

        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.setRowHeight(22);
        tabel.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 11));
        tabel.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] w = {40, 100, 120, 340, 300, 260};
        for (int i = 0; i < w.length; i++) tabel.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        // Warnai baris kategori "Dilarang" merah.
        tabel.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public java.awt.Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                java.awt.Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                String kat = String.valueOf(t.getValueAt(row, 1));
                if (!sel) {
                    if ("Dilarang".equals(kat)) { c.setBackground(new Color(255, 235, 235)); c.setForeground(new Color(180, 0, 0)); }
                    else if ("Simbol".equals(kat)) { c.setBackground(new Color(235, 245, 255)); c.setForeground(Color.BLACK); }
                    else { c.setBackground(Color.WHITE); c.setForeground(Color.BLACK); }
                }
                return c;
            }
        });
        getContentPane().add(new JScrollPane(tabel), BorderLayout.CENTER);

        JPanel bawah = new JPanel(new BorderLayout());
        lblInfo.setFont(new Font("Tahoma", Font.PLAIN, 11));
        lblInfo.setForeground(new Color(80, 80, 80));
        bawah.add(lblInfo, BorderLayout.WEST);
        JLabel ket = new JLabel("Baris merah = singkatan DILARANG digunakan. Sumber: SK Direktur No.400.7.31/112/RSUD-CB/2026   ");
        ket.setFont(new Font("Tahoma", Font.ITALIC, 11));
        ket.setForeground(new Color(120, 120, 120));
        bawah.add(ket, BorderLayout.EAST);
        getContentPane().add(bawah, BorderLayout.SOUTH);

        btnCari.addActionListener(e -> tampil());
        btnReset.addActionListener(e -> { txtCari.setText(""); cmbKategori.setSelectedIndex(0); tampil(); });
        cmbKategori.addActionListener(e -> tampil());
        txtCari.addActionListener(e -> tampil()); // Enter
        txtCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent evt) { tampil(); }
        });
    }

    private void tampil() {
        model.setRowCount(0);
        String kat = String.valueOf(cmbKategori.getSelectedItem());
        String cari = "%" + txtCari.getText().trim() + "%";
        String sql = "SELECT kategori, singkatan, keterangan, misinterpretasi, wajib_gunakan "
                + "FROM master_singkatan_rscb WHERE (singkatan LIKE ? OR keterangan LIKE ?) "
                + ("Semua".equals(kat) ? "" : "AND kategori=? ")
                + "ORDER BY (kategori='Dilarang') DESC, singkatan";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, cari);
            ps.setString(2, cari);
            if (!"Semua".equals(kat)) ps.setString(3, kat);
            try (ResultSet rs = ps.executeQuery()) {
                int no = 0;
                while (rs.next()) {
                    no++;
                    model.addRow(new Object[]{
                        no, rs.getString("kategori"), rs.getString("singkatan"),
                        rs.getString("keterangan"),
                        rs.getString("misinterpretasi") == null ? "" : rs.getString("misinterpretasi"),
                        rs.getString("wajib_gunakan") == null ? "" : rs.getString("wajib_gunakan"),
                    });
                }
                lblInfo.setText("  Ditemukan " + no + " entri  ");
            }
        } catch (Exception e) {
            lblInfo.setText("  Gagal memuat data: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new RMDaftarSingkatan(null, true).setVisible(true));
    }
}
