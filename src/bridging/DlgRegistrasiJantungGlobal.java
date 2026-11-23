/*
 * Monitor global Registrasi Jantung (semua pasien) — Fase 5.
 * Filter tgl/jalur/status/keyword/status-kirim, buka detail, dan kirim ke SatuSehat.
 * Hand-built (tanpa .form), meniru DlgRegistrasiKankerGlobal.
 * Dibuka dari menu SatuSehat frmUtama.
 *
 * Hak akses menumpang satu_sehat_kirim_condition — modul ini TIDAK menambah
 * kolom permission baru di tabel `user` (keputusan 2026-07-19).
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

public final class DlgRegistrasiJantungGlobal extends javax.swing.JDialog {

    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();

    private widget.InternalFrame internalFrame1;
    private widget.Tanggal DTPCari1, DTPCari2;
    private widget.ComboBox cmbJalur, cmbStatus, cmbKirim;
    private widget.TextBox TCari;
    private widget.Button BtnRefresh, BtnBuka, BtnKirim, BtnKandidat, BtnTutup;
    private widget.Label lblInfo;
    private widget.Table tbData;
    private DefaultTableModel tabMode;

    private static final String[] JALUR_FILTER  = {"Semua", "hipertensi", "iskemik_stabil", "ska_ste", "ska_nste", "pjr"};
    private static final String[] STATUS_FILTER = {"Semua", "waitlist", "active", "finished"};
    private static final String[] KIRIM_FILTER  = {"Semua", "Belum terkirim", "Sudah terkirim"};

    /** Kolom tersembunyi berisi id_registrasi. */
    private static final int COL_ID = 11;

    public DlgRegistrasiJantungGlobal(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle(":: Monitor Registrasi Jantung (SatuSehat) ::");
        java.awt.Dimension scr = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(scr.width, scr.height - 40);
        setLocation(0, 0);
        buildUI();
        tampilData();
    }

    public void isCek() {
        boolean boleh = fungsi.akses.getsatu_sehat_kirim_condition();
        BtnKirim.setEnabled(boleh);
        BtnBuka.setEnabled(boleh);
    }

    private void buildUI() {
        internalFrame1 = new widget.InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(240, 245, 235)),
                "::[ Monitor Registrasi Jantung ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(0, 3));

        widget.panelisi bar = new widget.panelisi();
        bar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 6));
        addLbl(bar, "Tgl:");
        DTPCari1 = mkTgl(); bar.add(DTPCari1);
        addLbl(bar, "s/d");
        DTPCari2 = mkTgl(); bar.add(DTPCari2);
        addLbl(bar, "Jalur:");
        cmbJalur = new widget.ComboBox(); cmbJalur.setModel(new DefaultComboBoxModel<>(JALUR_FILTER));
        cmbJalur.setPreferredSize(new Dimension(120, 23)); bar.add(cmbJalur);
        addLbl(bar, "Status:");
        cmbStatus = new widget.ComboBox(); cmbStatus.setModel(new DefaultComboBoxModel<>(STATUS_FILTER));
        cmbStatus.setPreferredSize(new Dimension(100, 23)); bar.add(cmbStatus);
        addLbl(bar, "Kirim:");
        cmbKirim = new widget.ComboBox(); cmbKirim.setModel(new DefaultComboBoxModel<>(KIRIM_FILTER));
        cmbKirim.setPreferredSize(new Dimension(130, 23)); bar.add(cmbKirim);
        addLbl(bar, "Cari:");
        TCari = new widget.TextBox(); TCari.setPreferredSize(new Dimension(170, 23)); bar.add(TCari);
        BtnRefresh = mkButton("Tampilkan", 'M', 110, e -> tampilData()); bar.add(BtnRefresh);
        internalFrame1.add(bar, BorderLayout.NORTH);

        tabMode = new DefaultTableModel(null, new String[]{
                "No.Rawat", "No.RM", "Nama Pasien", "Jalur", "ICD-10", "Status",
                "Kirim", "Care Manager", "Tgl Registrasi", "Tgl Diagnosis", "Onset Keluhan", "id_reg"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbData = new widget.Table();
        tbData.setModel(tabMode);
        tbData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] w = {130, 80, 200, 110, 70, 80, 70, 150, 110, 110, 140, 0};
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

        widget.panelisi bottom = new widget.panelisi();
        bottom.setLayout(new BorderLayout());
        lblInfo = new widget.Label(); lblInfo.setText("Total: 0");
        lblInfo.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        bottom.add(lblInfo, BorderLayout.WEST);
        widget.panelisi btns = new widget.panelisi();
        btns.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 6, 4));
        BtnKandidat = mkButton("Kandidat Belum Teregistrasi", 'N', 200, e -> tampilKandidat());
        BtnBuka     = mkButton("Buka Detail", 'D', 130, e -> bukaDetail());
        BtnKirim    = mkButton("Kirim ke SatuSehat", 'K', 160, e -> kirim());
        BtnTutup    = mkButton("Tutup", 'T', 100, e -> dispose());
        btns.add(BtnKandidat); btns.add(BtnBuka); btns.add(BtnKirim); btns.add(BtnTutup);
        bottom.add(btns, BorderLayout.EAST);
        internalFrame1.add(bottom, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(internalFrame1, BorderLayout.CENTER);
    }

    private void addLbl(widget.panelisi bar, String teks) {
        widget.Label l = new widget.Label(); l.setText(teks); bar.add(l);
    }
    private widget.Tanggal mkTgl() {
        widget.Tanggal t = new widget.Tanggal();
        t.setDisplayFormat("dd-MM-yyyy");
        t.setPreferredSize(new Dimension(120, 23));
        return t;
    }
    private widget.Button mkButton(String label, char mn, int lebar, java.awt.event.ActionListener act) {
        widget.Button b = new widget.Button();
        b.setText(label); b.setMnemonic(mn); b.setToolTipText("Alt+" + mn);
        b.setFont(new Font("Tahoma", 0, 11));
        b.setPreferredSize(new Dimension(lebar, 28));
        b.addActionListener(act);
        return b;
    }

    private void tampilData() {
        Valid.tabelKosong(tabMode);
        String d1 = Valid.SetTgl(DTPCari1.getSelectedItem() + "");
        String d2 = Valid.SetTgl(DTPCari2.getSelectedItem() + "");
        String jalur  = String.valueOf(cmbJalur.getSelectedItem());
        String status = String.valueOf(cmbStatus.getSelectedItem());
        String kirim  = String.valueOf(cmbKirim.getSelectedItem());
        String kw     = TCari.getText().trim();

        StringBuilder sql = new StringBuilder(
                "select rj.no_rawat, rj.no_rkm_medis, ifnull(p.nm_pasien,'') nama, rj.jalur, "
              + "ifnull(rj.kd_penyakit,'') icd10, rj.status, ifnull(pg.nama,ifnull(rj.kd_dokter,'')) cm, "
              + "rj.tgl_registrasi, ifnull(rj.tgl_diagnosis,'') tgl_diagnosis, "
              + "ifnull(rj.onset_keluhan,'') onset, rj.id_registrasi, "
              + "(select count(*) from satu_sehat_jantung_kirim k where k.id_registrasi=rj.id_registrasi) nkirim "
              + "from registrasi_jantung rj "
              + "left join pasien p on p.no_rkm_medis=rj.no_rkm_medis "
              + "left join pegawai pg on pg.nik=rj.kd_dokter "
              + "where rj.tgl_registrasi between ? and ? ");
        // Nilai filter berasal dari daftar konstanta, bukan input bebas — aman disisipkan.
        if (!"Semua".equals(jalur))  sql.append("and rj.jalur='").append(jalur).append("' ");
        if (!"Semua".equals(status)) sql.append("and rj.status='").append(status).append("' ");
        if ("Belum terkirim".equals(kirim))
            sql.append("and (select count(*) from satu_sehat_jantung_kirim k where k.id_registrasi=rj.id_registrasi)=0 ");
        else if ("Sudah terkirim".equals(kirim))
            sql.append("and (select count(*) from satu_sehat_jantung_kirim k where k.id_registrasi=rj.id_registrasi)>0 ");
        if (!kw.isEmpty()) sql.append("and (p.nm_pasien like ? or rj.no_rkm_medis like ? or rj.no_rawat like ?) ");
        sql.append("order by rj.tgl_registrasi desc, rj.id_registrasi desc limit 1000");

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
                        rs.getString("jalur"), rs.getString("icd10"), rs.getString("status"),
                        rs.getInt("nkirim") > 0 ? "Sudah" : "Belum",
                        rs.getString("cm"), rs.getString("tgl_registrasi"),
                        rs.getString("tgl_diagnosis"), rs.getString("onset"),
                        rs.getString("id_registrasi")
                    });
                }
            }
            lblInfo.setText("Total: " + tabMode.getRowCount());
        } catch (Exception ex) {
            System.out.println("Notif tampilData monitor jantung : " + ex);
            JOptionPane.showMessageDialog(this, "Gagal memuat data:\n" + ex.getMessage());
        }
    }

    /**
     * Pasien yang memenuhi kriteria inklusi tetapi BELUM teregistrasi.
     * ICD-10 I10-I15 (hipertensi), I20-I25 (jantung iskemik), I50 (gagal jantung),
     * I05-I09 (penyakit jantung rematik).
     */
    private void tampilKandidat() {
        Valid.tabelKosong(tabMode);
        String d1 = Valid.SetTgl(DTPCari1.getSelectedItem() + "");
        String d2 = Valid.SetTgl(DTPCari2.getSelectedItem() + "");
        String sql =
                "select rp.no_rawat, rp.no_rkm_medis, ifnull(p.nm_pasien,'') nama, "
              + "dp.kd_penyakit, ifnull(pk.nm_penyakit,'') nm_penyakit, rp.tgl_registrasi "
              + "from diagnosa_pasien dp "
              + "inner join reg_periksa rp on rp.no_rawat=dp.no_rawat "
              + "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
              + "left join penyakit pk on pk.kd_penyakit=dp.kd_penyakit "
              + "left join registrasi_jantung rj on rj.no_rkm_medis=rp.no_rkm_medis "
              + "where rj.id_registrasi is null "
              + "and dp.kd_penyakit regexp '^I(0[5-9]|1[0-5]|2[0-5]|50)' "
              + "and rp.tgl_registrasi between ? and ? "
              + "group by rp.no_rkm_medis "
              + "order by rp.tgl_registrasi desc limit 1000";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, d1);
            ps.setString(2, d2);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabMode.addRow(new Object[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nama"),
                        "(kandidat)", rs.getString("kd_penyakit"), "-", "-",
                        rs.getString("nm_penyakit"), rs.getString("tgl_registrasi"), "", "", ""
                    });
                }
            }
            lblInfo.setText("Kandidat belum teregistrasi: " + tabMode.getRowCount()
                    + "  — klik dua kali untuk membuka form registrasi");
        } catch (Exception ex) {
            System.out.println("Notif tampilKandidat jantung : " + ex);
            JOptionPane.showMessageDialog(this, "Gagal memuat kandidat:\n" + ex.getMessage());
        }
    }

    private void bukaDetail() {
        int r = tbData.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Pilih baris dulu."); return; }
        String noRawat = String.valueOf(tabMode.getValueAt(r, 0));
        String noRkm   = String.valueOf(tabMode.getValueAt(r, 1));
        String nama    = String.valueOf(tabMode.getValueAt(r, 2));
        DlgRegistrasiJantung form = new DlgRegistrasiJantung(null, false);
        form.isCek();
        form.setSize(internalFrame1.getWidth() - 40, internalFrame1.getHeight() - 40);
        form.setLocationRelativeTo(internalFrame1);
        form.setVisible(true);
        form.setPasien(noRawat, noRkm, nama);
    }

    private void kirim() {
        if (!fungsi.akses.getsatu_sehat_kirim_condition()) {
            JOptionPane.showMessageDialog(this, "Anda tidak memiliki hak akses untuk Registrasi Jantung.");
            return;
        }
        int r = tbData.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Pilih baris dulu."); return; }
        final long id;
        try { id = Long.parseLong(String.valueOf(tabMode.getValueAt(r, COL_ID)).trim()); }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Baris ini belum punya registrasi (kemungkinan baris kandidat).\n"
                  + "Buka detail dan simpan registrasinya dulu.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
                "Kirim registrasi jantung ini ke SatuSehat?\n"
              + "(Encounter kunjungan harus sudah terkirim lebih dulu.)",
                "Konfirmasi Kirim", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        BtnKirim.setEnabled(false);
        new Thread(() -> {
            final String hasil = SatuSehatKirimJantung.kirim(id);
            javax.swing.SwingUtilities.invokeLater(() -> {
                setCursor(java.awt.Cursor.getDefaultCursor());
                BtnKirim.setEnabled(true);
                JTextArea ta = new JTextArea(hasil, 18, 70);
                ta.setEditable(false);
                ta.setCaretPosition(0);
                JOptionPane.showMessageDialog(this, new JScrollPane(ta),
                        "Hasil Kirim SatuSehat", JOptionPane.INFORMATION_MESSAGE);
                tampilData();
            });
        }, "kirim-jantung-global").start();
    }
}
