/*
 * Monitor global Registrasi Kanker (semua pasien) — Fase 3.
 * Filter tgl/status/keyword/status-kirim, buka detail, dan kirim ke SatuSehat.
 * Hand-built (tanpa .form). Dibuka dari menu SatuSehat frmUtama.
 */
package bridging;

import fungsi.WarnaTable;
import fungsi.koneksiDB;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

public final class DlgRegistrasiKankerGlobal extends javax.swing.JDialog {

    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();

    private widget.InternalFrame internalFrame1;
    private widget.Tanggal DTPCari1, DTPCari2;
    private widget.ComboBox cmbStatus, cmbKirim;
    private widget.TextBox TCari;
    private widget.Button BtnRefresh, BtnBuka, BtnKirim, BtnTutup;
    private widget.Label lblInfo;
    private widget.Table tbData;
    private DefaultTableModel tabMode;

    private static final String[] STATUS_FILTER = {"Semua", "waitlist", "active", "onhold", "finished", "cancelled"};
    private static final String[] KIRIM_FILTER  = {"Semua", "Belum terkirim", "Sudah terkirim"};

    public DlgRegistrasiKankerGlobal(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle(":: Monitor Registrasi Kanker (SatuSehat) ::");
        java.awt.Dimension scr = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(scr.width, scr.height - 40);
        setLocation(0, 0);
        buildUI();
        tampilData();
    }

    public void isCek() { /* no-op */ }

    private void buildUI() {
        internalFrame1 = new widget.InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 220, 235)),
                "::[ Monitor Registrasi Kanker ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(0, 3));

        // Filter bar
        widget.panelisi bar = new widget.panelisi();
        bar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 6));
        widget.Label l1 = new widget.Label(); l1.setText("Tgl:"); bar.add(l1);
        DTPCari1 = mkTgl(); bar.add(DTPCari1);
        widget.Label l2 = new widget.Label(); l2.setText("s/d"); bar.add(l2);
        DTPCari2 = mkTgl(); bar.add(DTPCari2);
        widget.Label l3 = new widget.Label(); l3.setText("Status:"); bar.add(l3);
        cmbStatus = new widget.ComboBox(); cmbStatus.setModel(new DefaultComboBoxModel<>(STATUS_FILTER));
        cmbStatus.setPreferredSize(new Dimension(120, 23)); bar.add(cmbStatus);
        widget.Label l4 = new widget.Label(); l4.setText("Kirim:"); bar.add(l4);
        cmbKirim = new widget.ComboBox(); cmbKirim.setModel(new DefaultComboBoxModel<>(KIRIM_FILTER));
        cmbKirim.setPreferredSize(new Dimension(140, 23)); bar.add(cmbKirim);
        widget.Label l5 = new widget.Label(); l5.setText("Cari:"); bar.add(l5);
        TCari = new widget.TextBox(); TCari.setPreferredSize(new Dimension(180, 23)); bar.add(TCari);
        BtnRefresh = new widget.Button(); BtnRefresh.setText("Tampilkan"); BtnRefresh.setPreferredSize(new Dimension(100, 23));
        BtnRefresh.addActionListener(e -> tampilData()); bar.add(BtnRefresh);
        internalFrame1.add(bar, BorderLayout.NORTH);

        // Table
        tabMode = new DefaultTableModel(null, new String[]{
                "No.Rawat", "No.RM", "Nama", "ICD-10", "Topografi", "Morfologi", "Stadium",
                "Status", "Kirim", "Care Manager", "Tgl Registrasi", "id_reg"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbData = new widget.Table();
        tbData.setModel(tabMode);
        tbData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] w = {130, 90, 200, 70, 90, 90, 90, 90, 90, 150, 110, 0};
        for (int i = 0; i < w.length; i++) tbData.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        tbData.setDefaultRenderer(Object.class, new WarnaTable());
        tbData.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) bukaDetail();
            }
        });
        widget.ScrollPane sc = new widget.ScrollPane();
        sc.setViewportView(tbData);
        internalFrame1.add(sc, BorderLayout.CENTER);

        // Bottom: info + buttons
        widget.panelisi bottom = new widget.panelisi();
        bottom.setLayout(new BorderLayout());
        lblInfo = new widget.Label(); lblInfo.setText("Total: 0");
        lblInfo.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        bottom.add(lblInfo, BorderLayout.WEST);
        widget.panelisi btns = new widget.panelisi();
        btns.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 6, 4));
        BtnBuka  = mkButton("Buka Detail", 'D', e -> bukaDetail());
        BtnKirim = mkButton("Kirim ke SatuSehat", 'K', e -> kirim());
        BtnTutup = mkButton("Tutup", 'T', e -> dispose());
        btns.add(BtnBuka); btns.add(BtnKirim); btns.add(BtnTutup);
        bottom.add(btns, BorderLayout.EAST);
        internalFrame1.add(bottom, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(internalFrame1, BorderLayout.CENTER);
    }

    private widget.Tanggal mkTgl() {
        widget.Tanggal t = new widget.Tanggal(); t.setDisplayFormat("dd-MM-yyyy"); t.setPreferredSize(new Dimension(120, 23)); return t;
    }
    private widget.Button mkButton(String label, char mn, java.awt.event.ActionListener act) {
        widget.Button b = new widget.Button(); b.setText(label); b.setMnemonic(mn);
        b.setFont(new Font("Tahoma", 0, 11)); b.setPreferredSize(new Dimension(150, 28)); b.addActionListener(act); return b;
    }

    private void tampilData() {
        Valid.tabelKosong(tabMode);
        String d1 = Valid.SetTgl(DTPCari1.getSelectedItem() + "");
        String d2 = Valid.SetTgl(DTPCari2.getSelectedItem() + "");
        String status = String.valueOf(cmbStatus.getSelectedItem());
        String kirim = String.valueOf(cmbKirim.getSelectedItem());
        String kw = TCari.getText().trim();

        StringBuilder sql = new StringBuilder(
                "select rk.no_rawat, rk.no_rkm_medis, ifnull(p.nm_pasien,'') nama, ifnull(rk.kd_penyakit,'') icd10, "
              + "ifnull(rk.icdo_topografi,'') topo, ifnull(rk.icdo_morfologi,'') morf, ifnull(rk.stage_group,'') stg, "
              + "rk.status, ifnull(pg.nama,rk.kd_dokter) cm, rk.tgl_registrasi, rk.id_registrasi, "
              + "(select count(*) from satu_sehat_kanker_kirim k where k.id_registrasi=rk.id_registrasi) nkirim "
              + "from registrasi_kanker rk "
              + "left join pasien p on p.no_rkm_medis=rk.no_rkm_medis "
              + "left join pegawai pg on pg.nik=rk.kd_dokter "
              + "where rk.tgl_registrasi between ? and ? ");
        if (!"Semua".equals(status)) sql.append("and rk.status='").append(status).append("' ");
        if ("Belum terkirim".equals(kirim)) sql.append("and (select count(*) from satu_sehat_kanker_kirim k where k.id_registrasi=rk.id_registrasi)=0 ");
        else if ("Sudah terkirim".equals(kirim)) sql.append("and (select count(*) from satu_sehat_kanker_kirim k where k.id_registrasi=rk.id_registrasi)>0 ");
        if (!kw.isEmpty()) sql.append("and (p.nm_pasien like ? or rk.no_rkm_medis like ? or rk.no_rawat like ?) ");
        sql.append("order by rk.tgl_registrasi desc, rk.id_registrasi desc limit 1000");

        try (PreparedStatement ps = koneksi.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setString(idx++, d1);
            ps.setString(idx++, d2);
            if (!kw.isEmpty()) {
                ps.setString(idx++, "%" + kw + "%");
                ps.setString(idx++, "%" + kw + "%");
                ps.setString(idx++, "%" + kw + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabMode.addRow(new Object[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nama"),
                        rs.getString("icd10"), rs.getString("topo"), rs.getString("morf"),
                        rs.getString("stg"), rs.getString("status"),
                        rs.getInt("nkirim") > 0 ? "Sudah" : "Belum",
                        rs.getString("cm"), rs.getString("tgl_registrasi"), rs.getString("id_registrasi")
                    });
                }
            }
            lblInfo.setText("Total: " + tabMode.getRowCount());
        } catch (Exception ex) {
            System.out.println("Notif tampilData monitor kanker : " + ex);
        }
    }

    private void bukaDetail() {
        int r = tbData.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Pilih baris dulu."); return; }
        String noRawat = String.valueOf(tabMode.getValueAt(r, 0));
        String noRkm = String.valueOf(tabMode.getValueAt(r, 1));
        String nama = String.valueOf(tabMode.getValueAt(r, 2));
        DlgRegistrasiKanker form = new DlgRegistrasiKanker(null, false);
        form.isCek();
        form.setSize(internalFrame1.getWidth() - 40, internalFrame1.getHeight() - 40);
        form.setLocationRelativeTo(internalFrame1);
        form.setVisible(true);
        form.setPasien(noRawat, noRkm, nama);
    }

    private void kirim() {
        if (!fungsi.akses.getregistrasi_kanker()) {
            JOptionPane.showMessageDialog(this, "Anda tidak memiliki hak akses untuk Registrasi Kanker.");
            return;
        }
        int r = tbData.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Pilih baris dulu."); return; }
        final long id;
        try { id = Long.parseLong(String.valueOf(tabMode.getValueAt(r, 11)).trim()); }
        catch (Exception e) { JOptionPane.showMessageDialog(this, "ID registrasi tidak valid."); return; }
        int ok = JOptionPane.showConfirmDialog(this,
                "Kirim registrasi kanker ini ke SatuSehat?\n(Encounter kunjungan harus sudah terkirim lebih dulu.)",
                "Konfirmasi Kirim", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        BtnKirim.setEnabled(false);
        new Thread(() -> {
            final String hasil = SatuSehatKirimKanker.kirim(id);
            javax.swing.SwingUtilities.invokeLater(() -> {
                setCursor(java.awt.Cursor.getDefaultCursor());
                BtnKirim.setEnabled(true);
                JTextArea ta = new JTextArea(hasil, 16, 60); ta.setEditable(false);
                JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Hasil Kirim SatuSehat", JOptionPane.INFORMATION_MESSAGE);
                tampilData();
            });
        }, "kirim-kanker-global").start();
    }
}
