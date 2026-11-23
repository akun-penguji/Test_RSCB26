package simrskhanza;

import fungsi.koneksiDB;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Dialog pencatatan pelaporan NILAI KRITIS laboratorium (KARS PAP 3 / SKP 2).
 *
 * <p>Dibuka dari {@link DlgPeriksaLaboratorium#cekNilaiKritis()} setelah nilai
 * kritis terdeteksi & di-log ke {@code audit_nilai_kritis}. Menyimpan satu baris
 * {@code lapor_kritis_rm} per id audit (TBaK: Tulis-Baca-Konfirmasi / read-back)
 * lalu meng-update status audit menjadi {@code TERNOTIFIKASI}.
 */
public class DlgLaporKritis extends JDialog {

    private final List<Integer> auditIds;
    private final String pelaporNip, pelaporNama;

    private final JTextField TPenerima   = new JTextField(24);
    private final JComboBox<String> CmbTipe   = new JComboBox<>(new String[]{"Dokter","Perawat"});
    private final JComboBox<String> CmbMetode = new JComboBox<>(new String[]{"Telepon","WA","Tatap Muka","Lainnya"});
    private final JCheckBox ChkReadBack = new JCheckBox("Baca-ulang (read-back) dikonfirmasi penerima", true);
    private final JCheckBox ChkTBaK     = new JCheckBox("Ditulis dgn format TBaK/SBAR di CPPT", true);
    private final JTextArea TInstruksi  = new JTextArea(3,24);
    private final JTextArea TKondisi    = new JTextArea(3,24);
    private final JTextField TKeterangan = new JTextField(24);

    public DlgLaporKritis(java.awt.Frame parent, boolean modal, List<Integer> auditIds,
                          String noRawat, String noRkm, String pasien,
                          String pelaporNip, String pelaporNama) {
        super(parent, modal);
        this.auditIds = auditIds;
        this.pelaporNip = pelaporNip == null ? "" : pelaporNip;
        this.pelaporNama = pelaporNama == null ? "" : pelaporNama;
        setTitle("Lapor Nilai Kritis (TBaK) — KARS PAP 3");

        JPanel head = new JPanel(new BorderLayout());
        head.setBackground(new Color(200,0,0));
        head.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
        JLabel judul = new JLabel("⚠  PELAPORAN NILAI KRITIS — "
                + (auditIds==null?0:auditIds.size()) + " item");
        judul.setForeground(Color.WHITE);
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 14f));
        head.add(judul, BorderLayout.WEST);

        JPanel info = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 12, 2));
        info.setBorder(BorderFactory.createEmptyBorder(6,12,0,12));
        info.add(new JLabel("Pasien: " + (pasien==null?"":pasien)
                + "   |   No.RM: " + (noRkm==null?"":noRkm)
                + "   |   No.Rawat: " + (noRawat==null?"":noRawat)));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(8,12,8,12));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;
        int y = 0;

        TInstruksi.setLineWrap(true); TInstruksi.setWrapStyleWord(true);
        TKondisi.setLineWrap(true);   TKondisi.setWrapStyleWord(true);

        addRow(form, g, y++, "Dilaporkan kepada *", TPenerima);
        addRow(form, g, y++, "Tipe penerima", CmbTipe);
        addRow(form, g, y++, "Metode komunikasi", CmbMetode);
        addRow(form, g, y++, "", ChkReadBack);
        addRow(form, g, y++, "", ChkTBaK);
        addRow(form, g, y++, "Instruksi tindak lanjut", new JScrollPane(TInstruksi));
        addRow(form, g, y++, "Kondisi pasien", new JScrollPane(TKondisi));
        addRow(form, g, y++, "Keterangan", TKeterangan);

        JButton btnSimpan = new JButton("Simpan Laporan");
        btnSimpan.setBackground(new Color(0,140,60));
        btnSimpan.setForeground(Color.WHITE);
        btnSimpan.addActionListener(e -> simpan());
        JButton btnBatal = new JButton("Nanti Saja");
        btnBatal.addActionListener(e -> dispose());
        JPanel south = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 8, 8));
        south.add(Box.createHorizontalStrut(4));
        south.add(btnBatal);
        south.add(btnSimpan);

        JPanel center = new JPanel(new BorderLayout());
        center.add(info, BorderLayout.NORTH);
        center.add(form, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(head, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
        pack();
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        TPenerima.requestFocusInWindow();
    }

    private void addRow(JPanel p, GridBagConstraints g, int y, String label, java.awt.Component field) {
        g.gridx = 0; g.gridy = y; g.weightx = 0;
        p.add(new JLabel(label), g);
        g.gridx = 1; g.weightx = 1;
        p.add(field, g);
    }

    private void simpan() {
        if (TPenerima.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama penerima laporan wajib diisi.");
            TPenerima.requestFocus();
            return;
        }
        if (!ChkReadBack.isSelected()) {
            int t = JOptionPane.showConfirmDialog(this,
                "Read-back (baca-ulang) belum dikonfirmasi.\nTetap simpan laporan?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (t != JOptionPane.YES_OPTION) return;
        }
        int ok = 0;
        try (Connection conn = koneksiDB.condb()) {
            conn.setAutoCommit(false);
            final String insLapor =
                "INSERT INTO lapor_kritis_rm " +
                "(id_audit, waktu_lapor, petugas_pelapor_nip, petugas_pelapor_nama, " +
                " penerima_kd_dokter, penerima_nm_dokter, penerima_tipe, metode_komunikasi, " +
                " read_back_confirmed, tbak_format, instruksi_tindak_lanjut, kondisi_pasien, keterangan) " +
                "VALUES (?, NOW(), ?, ?, NULL, ?, ?, ?, ?, ?, ?, ?, ?)";
            final String updAudit =
                "UPDATE audit_nilai_kritis SET status_notifikasi='TERNOTIFIKASI', " +
                "waktu_notifikasi=NOW(), petugas_notifikasi=? WHERE id=?";
            try (PreparedStatement psL = conn.prepareStatement(insLapor);
                 PreparedStatement psU = conn.prepareStatement(updAudit)) {
                for (Integer idAudit : auditIds) {
                    psL.setInt(1, idAudit);
                    psL.setString(2, pelaporNip);
                    psL.setString(3, pelaporNama);
                    psL.setString(4, TPenerima.getText().trim());
                    psL.setString(5, String.valueOf(CmbTipe.getSelectedItem()));
                    psL.setString(6, String.valueOf(CmbMetode.getSelectedItem()));
                    psL.setInt(7, ChkReadBack.isSelected() ? 1 : 0);
                    psL.setInt(8, ChkTBaK.isSelected() ? 1 : 0);
                    psL.setString(9, TInstruksi.getText().trim());
                    psL.setString(10, TKondisi.getText().trim());
                    psL.setString(11, TKeterangan.getText().trim());
                    psL.executeUpdate();

                    psU.setString(1, pelaporNama.isEmpty() ? pelaporNip : pelaporNama);
                    psU.setInt(2, idAudit);
                    psU.executeUpdate();
                    ok++;
                }
            }
            conn.commit();
            JOptionPane.showMessageDialog(this,
                "Laporan nilai kritis tersimpan (" + ok + " item). Status: TERNOTIFIKASI.");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Gagal menyimpan laporan: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("DlgLaporKritis.simpan: " + ex);
        }
    }
}
