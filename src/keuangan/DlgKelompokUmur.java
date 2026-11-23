package keuangan;

import fungsi.koneksiDB;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Master CRUD untuk {@code master_kelompok_umur}.
 * Dipakai oleh NilaiRujukanResolver untuk menentukan kelompok umur pasien
 * dari umur dalam hari.
 */
public class DlgKelompokUmur extends JDialog {

    private final Connection koneksi = koneksiDB.condb();

    private final JTextField tKode    = new JTextField();
    private final JTextField tNama    = new JTextField();
    private final JTextField tMin     = new JTextField();
    private final JTextField tMax     = new JTextField();
    private final JTextField tUrut    = new JTextField();

    private final DefaultTableModel model = new DefaultTableModel(null,
        new Object[]{"Kode", "Nama", "Umur Min (hari)", "Umur Max (hari)", "Urut"}) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tb = new JTable(model);

    public DlgKelompokUmur(java.awt.Frame parent, boolean modal) {
        super(parent, "Master Kelompok Umur", modal);
        setSize(720, 500);
        setLocationRelativeTo(parent);
        buildUI();
        tampil();
        tb.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int r = tb.getSelectedRow();
            if (r < 0) return;
            tKode.setText(n(model.getValueAt(r, 0)));
            tNama.setText(n(model.getValueAt(r, 1)));
            tMin.setText(n(model.getValueAt(r, 2)));
            tMax.setText(n(model.getValueAt(r, 3)));
            tUrut.setText(n(model.getValueAt(r, 4)));
        });
    }

    private void buildUI() {
        JPanel form = new JPanel(new GridLayout(5, 2, 6, 6));
        form.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(" Input Kelompok Umur "),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        form.add(new JLabel("Kode (max 5):"));           form.add(tKode);
        form.add(new JLabel("Nama:"));                   form.add(tNama);
        form.add(new JLabel("Umur Min (hari):"));        form.add(tMin);
        form.add(new JLabel("Umur Max (hari, kosong=∞):")); form.add(tMax);
        form.add(new JLabel("Urut:"));                   form.add(tUrut);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        JButton bSimpan = primary("Simpan");
        JButton bUbah   = new JButton("Ubah");
        JButton bHapus  = new JButton("Hapus");
        JButton bBatal  = new JButton("Batal");
        JButton bKeluar = new JButton("Keluar");
        bSimpan.addActionListener(e -> simpan());
        bUbah.addActionListener(e -> ubah());
        bHapus.addActionListener(e -> hapus());
        bBatal.addActionListener(e -> clear());
        bKeluar.addActionListener(e -> dispose());
        buttons.add(bSimpan); buttons.add(bUbah); buttons.add(bHapus);
        buttons.add(bBatal);  buttons.add(bKeluar);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form,    BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        tb.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tb.setRowHeight(24);
        tb.getColumnModel().getColumn(0).setPreferredWidth(60);
        tb.getColumnModel().getColumn(1).setPreferredWidth(160);
        tb.getColumnModel().getColumn(2).setPreferredWidth(120);
        tb.getColumnModel().getColumn(3).setPreferredWidth(120);
        tb.getColumnModel().getColumn(4).setPreferredWidth(50);

        JPanel wrap = new JPanel(new BorderLayout(6, 6));
        wrap.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        wrap.add(top, BorderLayout.NORTH);
        wrap.add(new JScrollPane(tb), BorderLayout.CENTER);
        setContentPane(wrap);
    }

    private static JButton primary(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(0x2563EB));
        b.setForeground(Color.WHITE);
        b.setFont(b.getFont().deriveFont(Font.BOLD));
        return b;
    }

    private void tampil() {
        model.setRowCount(0);
        try (PreparedStatement ps = koneksi.prepareStatement(
                "SELECT kd_kelompok_umur, nama, umur_min_hari, umur_max_hari, urut " +
                "FROM master_kelompok_umur ORDER BY urut, umur_min_hari");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int max = rs.getInt("umur_max_hari");
                String maxStr = rs.wasNull() ? "" : String.valueOf(max);
                model.addRow(new Object[]{
                    rs.getString("kd_kelompok_umur"),
                    rs.getString("nama"),
                    rs.getInt("umur_min_hari"),
                    maxStr,
                    rs.getInt("urut")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal load: " + e.getMessage());
        }
    }

    private void simpan() {
        if (!validateInput()) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "INSERT INTO master_kelompok_umur (kd_kelompok_umur, nama, umur_min_hari, umur_max_hari, urut) " +
                "VALUES (?,?,?,?,?)")) {
            ps.setString(1, tKode.getText().trim().toUpperCase());
            ps.setString(2, tNama.getText().trim());
            ps.setInt(3, Integer.parseInt(tMin.getText().trim()));
            String max = tMax.getText().trim();
            if (max.isEmpty()) ps.setNull(4, java.sql.Types.INTEGER);
            else               ps.setInt(4, Integer.parseInt(max));
            ps.setInt(5, Integer.parseInt(tUrut.getText().trim()));
            ps.executeUpdate();
            NilaiRujukanResolver.invalidateCache();
            tampil();
            clear();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal simpan: " + e.getMessage());
        }
    }

    private void ubah() {
        if (!validateInput()) return;
        int r = tb.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Pilih row di tabel dulu."); return; }
        String oldKode = model.getValueAt(r, 0).toString();
        try (PreparedStatement ps = koneksi.prepareStatement(
                "UPDATE master_kelompok_umur SET kd_kelompok_umur=?, nama=?, umur_min_hari=?, " +
                "umur_max_hari=?, urut=? WHERE kd_kelompok_umur=?")) {
            ps.setString(1, tKode.getText().trim().toUpperCase());
            ps.setString(2, tNama.getText().trim());
            ps.setInt(3, Integer.parseInt(tMin.getText().trim()));
            String max = tMax.getText().trim();
            if (max.isEmpty()) ps.setNull(4, java.sql.Types.INTEGER);
            else               ps.setInt(4, Integer.parseInt(max));
            ps.setInt(5, Integer.parseInt(tUrut.getText().trim()));
            ps.setString(6, oldKode);
            ps.executeUpdate();
            NilaiRujukanResolver.invalidateCache();
            tampil();
            clear();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal ubah: " + e.getMessage());
        }
    }

    private void hapus() {
        int r = tb.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Pilih row di tabel dulu."); return; }
        String kode = model.getValueAt(r, 0).toString();
        // Cek dependency
        int refs = countRujukanByKode(kode);
        String warn = refs > 0
            ? "Ada " + refs + " row nilai rujukan yang pakai kode ini.\nHapus akan fail (FK constraint)."
            : "Yakin hapus kelompok umur '" + kode + "'?";
        if (JOptionPane.showConfirmDialog(this, warn, "Konfirmasi", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "DELETE FROM master_kelompok_umur WHERE kd_kelompok_umur=?")) {
            ps.setString(1, kode);
            ps.executeUpdate();
            NilaiRujukanResolver.invalidateCache();
            tampil();
            clear();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal hapus: " + e.getMessage());
        }
    }

    private int countRujukanByKode(String kode) {
        try (PreparedStatement ps = koneksi.prepareStatement(
                "SELECT COUNT(*) FROM template_lab_nilai_rujukan WHERE kd_kelompok_umur=?")) {
            ps.setString(1, kode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception ignored) {}
        return 0;
    }

    private boolean validateInput() {
        String kode = tKode.getText().trim();
        String nama = tNama.getText().trim();
        String min  = tMin.getText().trim();
        String max  = tMax.getText().trim();
        String urut = tUrut.getText().trim();
        if (kode.isEmpty() || nama.isEmpty() || min.isEmpty() || urut.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kode, Nama, Min, Urut wajib diisi.");
            return false;
        }
        if (kode.length() > 5) {
            JOptionPane.showMessageDialog(this, "Kode max 5 karakter.");
            return false;
        }
        try {
            int mn = Integer.parseInt(min);
            if (mn < 0) throw new NumberFormatException();
            if (!max.isEmpty()) {
                int mx = Integer.parseInt(max);
                if (mx <= mn) {
                    JOptionPane.showMessageDialog(this, "Umur Max harus > Umur Min.");
                    return false;
                }
            }
            Integer.parseInt(urut);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Min/Max/Urut harus angka valid (non-negatif).");
            return false;
        }
        return true;
    }

    private void clear() {
        tKode.setText(""); tNama.setText("");
        tMin.setText("");  tMax.setText(""); tUrut.setText("");
        tb.clearSelection();
    }

    private static String n(Object o) { return o == null ? "" : o.toString(); }
}
