package bridging;

import fungsi.koneksiDB;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Mapping spesialis Khanza (kd_sps) -> SNOMED specialty code untuk PractitionerRole
 * (eligibility TTE). Hasil disimpan ke satu_sehat_mapping_spesialis dan dipakai
 * {@link SatuSehatPractitionerRole} saat membuat PractitionerRole.
 *
 * Dibangun programatik (tanpa .form) supaya ringkas & mudah dipanggil dari menu.
 */
public final class SatuSehatMappingSpesialis extends JDialog {

    private final Connection koneksi = koneksiDB.condb();
    private final DefaultTableModel model;
    private final JTable tabel = new JTable();
    private final JTextField tKdSps = new JTextField();
    private final JTextField tNmSps = new JTextField();
    private final JTextField tKode = new JTextField();
    private final JTextField tDisplay = new JTextField();

    public SatuSehatMappingSpesialis(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Mapping Spesialis → SNOMED Specialty (PractitionerRole / Eligibility TTE)");
        setSize(760, 520);
        setLocationRelativeTo(parent);

        model = new DefaultTableModel(null, new Object[]{"Kd.Sps", "Nama Spesialis", "Kode SNOMED", "Display SNOMED"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabel.setModel(model);
        tabel.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) isiForm(); });

        JPanel form = new JPanel(null);
        form.setPreferredSize(new java.awt.Dimension(760, 130));
        JLabel l1 = new JLabel("Kd. Spesialis :"); l1.setBounds(15, 10, 100, 22); form.add(l1);
        tKdSps.setEditable(false); tKdSps.setBounds(120, 10, 90, 22); form.add(tKdSps);
        JLabel l2 = new JLabel("Nama :"); l2.setBounds(225, 10, 45, 22); form.add(l2);
        tNmSps.setEditable(false); tNmSps.setBounds(270, 10, 240, 22); form.add(tNmSps);

        JLabel l3 = new JLabel("Kode SNOMED :"); l3.setBounds(15, 42, 100, 22); form.add(l3);
        tKode.setBounds(120, 42, 130, 22); form.add(tKode);
        JLabel l4 = new JLabel("Display :"); l4.setBounds(265, 42, 55, 22); form.add(l4);
        tDisplay.setBounds(320, 42, 300, 22); form.add(tDisplay);

        JButton bSimpan = new JButton("Simpan Mapping"); bSimpan.setBounds(120, 78, 150, 26); form.add(bSimpan);
        bSimpan.addActionListener(e -> simpan());
        JButton bMuat = new JButton("Muat Ulang"); bMuat.setBounds(280, 78, 120, 26); form.add(bMuat);
        bMuat.addActionListener(e -> tampil());
        JLabel info = new JLabel("<html><i>Pilih baris, ubah Kode/Display SNOMED, lalu Simpan. Referensi kode: spreadsheet Specialty Code Kemenkes.</i></html>");
        info.setBounds(15, 108, 720, 18); form.add(info);

        add(form, BorderLayout.NORTH);
        add(new JScrollPane(tabel), BorderLayout.CENTER);
        tampil();
    }

    private void tampil() {
        model.setRowCount(0);
        String sql = "select s.kd_sps, s.nm_sps, ifnull(m.snomed_code,'') kode, ifnull(m.snomed_display,'') disp "
                   + "from spesialis s left join satu_sehat_mapping_spesialis m on m.kd_sps=s.kd_sps order by s.kd_sps";
        try (PreparedStatement p = koneksi.prepareStatement(sql); ResultSet r = p.executeQuery()) {
            while (r.next()) model.addRow(new Object[]{ r.getString(1), r.getString(2), r.getString(3), r.getString(4) });
        } catch (Exception e) { System.out.println("Notif tampil mapping spesialis : " + e); }
    }

    private void isiForm() {
        int row = tabel.getSelectedRow();
        if (row < 0) return;
        tKdSps.setText(v(row, 0)); tNmSps.setText(v(row, 1)); tKode.setText(v(row, 2)); tDisplay.setText(v(row, 3));
    }

    private void simpan() {
        if (tKdSps.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Pilih baris spesialis dulu."); return; }
        try (PreparedStatement p = koneksi.prepareStatement(
                "insert into satu_sehat_mapping_spesialis (kd_sps, nm_sps, snomed_code, snomed_display) values (?,?,?,?) "
              + "on duplicate key update nm_sps=values(nm_sps), snomed_code=values(snomed_code), snomed_display=values(snomed_display)")) {
            p.setString(1, tKdSps.getText().trim());
            p.setString(2, tNmSps.getText().trim());
            p.setString(3, tKode.getText().trim());
            p.setString(4, tDisplay.getText().trim());
            p.executeUpdate();
            JOptionPane.showMessageDialog(this, "Mapping tersimpan.");
            tampil();
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + e); }
    }

    private String v(int row, int col) { Object o = model.getValueAt(row, col); return o == null ? "" : o.toString(); }
}
