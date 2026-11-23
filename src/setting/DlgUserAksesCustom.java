package setting;

import fungsi.koneksiDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Form Hak Akses Custom — terpisah dari tabel user bawaan Khanza.
 * Menyimpan ke tabel simrscb_user_akses (kolom enum('true','false')),
 * supaya tabel user tetap selaras dengan skema upstream Khanza.
 *
 * Kolom yang dikelola di sini adalah kolom yang benar-benar dibaca oleh
 * fungsi.akses saat login, termasuk edit_lewat_batas_waktu (izin mengubah/
 * menghapus data rekam medis lewat batas 2x24 jam — PMK No. 24 Tahun 2022).
 */
public class DlgUserAksesCustom extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private final Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private String idUser = "";
    private String nmUser = "";

    // Kolom DB dan label tampilan — urutan harus sinkron
    private static final String[] KOLOM_DB = {
        "checklist_kriteria_masuk_nicu", "checklist_kriteria_keluar_nicu",
        "checklist_kriteria_masuk_picu", "checklist_kriteria_keluar_picu",
        "indikator_mutu", "register_laporan", "pemetaan_alergi",
        "edit_lewat_batas_waktu"
    };
    private static final String[] LABEL = {
        "Check List Kriteria Masuk NICU",
        "Check List Kriteria Keluar NICU",
        "Check List Kriteria Masuk PICU",
        "Check List Kriteria Keluar PICU",
        "Indikator Mutu",
        "Register Penyampaian Laporan",
        "Pemetaan Alergi",
        "Edit / Hapus Data Rekam Medis Lewat 2x24 Jam (PMK 24/2022)"
    };

    public DlgUserAksesCustom(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        tabMode = new DefaultTableModel(
            new Object[]{"Hak Akses Menu", "Status"},
            0
        ) {
            @Override
            public Class<?> getColumnClass(int col) {
                return col == 1 ? Boolean.class : Object.class;
            }
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 1;
            }
        };
        initComponents();
        tbAkses.setModel(tabMode);
        tbAkses.getColumnModel().getColumn(0).setPreferredWidth(380);
        tbAkses.getColumnModel().getColumn(1).setPreferredWidth(60);
        tbAkses.setRowHeight(22);
    }

    /** Panggil setelah konstruktor untuk mengisi form dengan data user. */
    public void isUser(String user, String nama) {
        this.idUser = user;
        this.nmUser = nama;
        setBorderTitle("::[ Akses Custom — " + nama + " ]::");
        tampil();
    }

    private void setBorderTitle(String judul) {
        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(100, 120, 200)),
            judul,
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11),
            new java.awt.Color(50, 50, 50)));
    }

    private void tampil() {
        tabMode.setRowCount(0);
        boolean[] vals = new boolean[KOLOM_DB.length];
        if (!idUser.isEmpty()) {
            try {
                StringBuilder sb = new StringBuilder("select ");
                for (int i = 0; i < KOLOM_DB.length; i++) {
                    sb.append(i == 0 ? "" : ",").append(KOLOM_DB[i]);
                }
                sb.append(" from simrscb_user_akses where id_user=AES_ENCRYPT(?,'nur')");
                ps = koneksi.prepareStatement(sb.toString());
                ps.setString(1, idUser);
                rs = ps.executeQuery();
                if (rs.next()) {
                    for (int i = 0; i < KOLOM_DB.length; i++) {
                        vals[i] = rs.getBoolean(KOLOM_DB[i]);
                    }
                }
            } catch (Exception e) {
                System.out.println("DlgUserAksesCustom.tampil: " + e);
            } finally {
                try { if (rs != null) rs.close(); } catch (Exception ex) {}
                try { if (ps != null) ps.close(); } catch (Exception ex) {}
            }
        }
        for (int i = 0; i < LABEL.length; i++) {
            tabMode.addRow(new Object[]{LABEL[i], vals[i]});
        }
    }

    private void simpan() {
        if (idUser.isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, "Pilih user terlebih dahulu.");
            return;
        }
        try {
            // Kolom enum('true','false') → simpan sebagai string 'true'/'false'.
            String[] vals = new String[KOLOM_DB.length];
            for (int i = 0; i < KOLOM_DB.length; i++) {
                Object v = tabMode.getValueAt(i, 1);
                boolean b = (v instanceof Boolean) && (Boolean) v;
                vals[i] = b ? "true" : "false";
            }
            StringBuilder cols = new StringBuilder();
            StringBuilder upd = new StringBuilder();
            for (int i = 0; i < KOLOM_DB.length; i++) {
                cols.append(",").append(KOLOM_DB[i]);
                if (upd.length() > 0) upd.append(",");
                upd.append(KOLOM_DB[i]).append("=VALUES(").append(KOLOM_DB[i]).append(")");
            }
            StringBuilder q = new StringBuilder("INSERT INTO simrscb_user_akses (id_user");
            q.append(cols).append(") VALUES (AES_ENCRYPT(?,'nur')");
            for (int i = 0; i < KOLOM_DB.length; i++) q.append(",?");
            q.append(") ON DUPLICATE KEY UPDATE ").append(upd);

            ps = koneksi.prepareStatement(q.toString());
            ps.setString(1, idUser);
            for (int i = 0; i < vals.length; i++) {
                ps.setString(i + 2, vals[i]);
            }
            ps.executeUpdate();
            JOptionPane.showMessageDialog(rootPane, "Hak akses custom berhasil disimpan untuk " + nmUser + ".");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(rootPane, "Gagal menyimpan: " + e.getMessage());
            System.out.println("DlgUserAksesCustom.simpan: " + e);
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception ex) {}
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        jScrollPane1 = new widget.ScrollPane();
        tbAkses = new widget.Table();
        panelFooter = new widget.panelGlass();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(100, 120, 200)),
            "::[ Akses Custom ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11),
            new java.awt.Color(50, 50, 50)));
        internalFrame1.setVisible(true);
        internalFrame1.setLayout(new java.awt.BorderLayout(4, 4));

        tbAkses.setModel(tabMode);
        jScrollPane1.setViewportView(tbAkses);
        internalFrame1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        panelFooter.setPreferredSize(new java.awt.Dimension(490, 44));
        panelFooter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 8));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simpan();
            }
        });
        panelFooter.add(BtnSimpan);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnBatal.setMnemonic('K');
        BtnBatal.setText("Keluar");
        BtnBatal.setToolTipText("Alt+K");
        BtnBatal.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispose();
            }
        });
        panelFooter.add(BtnBatal);

        internalFrame1.add(panelFooter, java.awt.BorderLayout.SOUTH);

        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        setSize(520, 360);
        setLocationRelativeTo(null);
    }

    private widget.InternalFrame internalFrame1;
    private widget.ScrollPane jScrollPane1;
    private widget.Table tbAkses;
    private widget.panelGlass panelFooter;
    private widget.Button BtnSimpan;
    private widget.Button BtnBatal;
}
