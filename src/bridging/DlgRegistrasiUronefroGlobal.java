/*
 * Monitor global Registrasi Uronefrologi — Fase 3 & 4.
 * Hand-built tanpa .form.
 *
 * Fase 4 khas modul ini: mode "Kandidat Penutupan Episode".
 * Uronefrologi punya TIGA pemicu finished — meninggal, sembuh dari batu
 * (khusus usia >18), dan hilang kontak >=5 tahun. Dua terakhir tidak bisa
 * diinput manual per pasien karena tidak ada momen yang memicu petugas
 * membukanya; harus dideteksi dari data.
 *
 * Hak akses menumpang satu_sehat_kirim_condition.
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

public final class DlgRegistrasiUronefroGlobal extends javax.swing.JDialog {

    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();

    private widget.InternalFrame internalFrame1;
    private widget.Tanggal DTPCari1, DTPCari2;
    private widget.ComboBox cmbJalur, cmbStatus, cmbKirim;
    private widget.TextBox TCari;
    private widget.Button BtnRefresh, BtnBuka, BtnKirim, BtnKandidat, BtnTutupEpisode, BtnTutup;
    private widget.Label lblInfo;
    private widget.Table tbData;
    private DefaultTableModel tabMode;

    private static final String[] JALUR_FILTER  =
            {"Semua", "ckd", "batu", "cakut", "glomerulonefritis", "nefropati_diabetik", "nefropati_hipertensi"};
    private static final String[] STATUS_FILTER = {"Semua", "waitlist", "active", "finished"};
    private static final String[] KIRIM_FILTER  = {"Semua", "Belum terkirim", "Sudah terkirim"};

    private static final int COL_ID = 11;

    public DlgRegistrasiUronefroGlobal(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle(":: Monitor Registrasi Uronefrologi (SatuSehat) ::");
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
                "::[ Monitor Registrasi Uronefrologi ]::",
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
        cmbJalur.setPreferredSize(new Dimension(160, 23)); bar.add(cmbJalur);
        addLbl(bar, "Status:");
        cmbStatus = new widget.ComboBox(); cmbStatus.setModel(new DefaultComboBoxModel<>(STATUS_FILTER));
        cmbStatus.setPreferredSize(new Dimension(100, 23)); bar.add(cmbStatus);
        addLbl(bar, "Kirim:");
        cmbKirim = new widget.ComboBox(); cmbKirim.setModel(new DefaultComboBoxModel<>(KIRIM_FILTER));
        cmbKirim.setPreferredSize(new Dimension(130, 23)); bar.add(cmbKirim);
        addLbl(bar, "Cari:");
        TCari = new widget.TextBox(); TCari.setPreferredSize(new Dimension(160, 23)); bar.add(TCari);
        BtnRefresh = mkButton("Tampilkan", 'M', 110, e -> tampilData()); bar.add(BtnRefresh);
        internalFrame1.add(bar, BorderLayout.NORTH);

        tabMode = new DefaultTableModel(null, new String[]{
                "No.Rawat", "No.RM", "Nama Pasien", "Jalur", "ICD-10", "Status",
                "Alasan Tutup", "Kirim", "Keterangan", "Tgl Registrasi", "Info", "id_reg"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbData = new widget.Table();
        tbData.setModel(tabMode);
        tbData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] w = {130, 80, 200, 150, 70, 80, 110, 70, 200, 110, 120, 0};
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
        BtnKandidat     = mkButton("Kandidat Belum Teregistrasi", 'N', 195, e -> tampilKandidat());
        BtnTutupEpisode = mkButton("Kandidat Penutupan Episode", 'P', 190, e -> tampilKandidatTutup());
        BtnBuka         = mkButton("Buka Detail", 'D', 115, e -> bukaDetail());
        BtnKirim        = mkButton("Kirim ke SatuSehat", 'K', 155, e -> kirim());
        BtnTutup        = mkButton("Tutup", 'T', 85, e -> dispose());
        btns.add(BtnKandidat); btns.add(BtnTutupEpisode); btns.add(BtnBuka); btns.add(BtnKirim); btns.add(BtnTutup);
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
                "select ru.no_rawat, ru.no_rkm_medis, ifnull(p.nm_pasien,'') nama, ru.jalur, "
              + "ifnull(ru.kd_penyakit,'') icd10, ru.status, ifnull(ru.alasan_finish,'') alasan, "
              + "ru.tgl_registrasi, ru.id_registrasi, "
              + "(select count(*) from satu_sehat_uronefro_kirim k where k.id_registrasi=ru.id_registrasi) nkirim, "
              + "(select count(*) from registrasi_uronefro_batu b where b.id_registrasi=ru.id_registrasi) nbatu "
              + "from registrasi_uronefro ru "
              + "left join pasien p on p.no_rkm_medis=ru.no_rkm_medis "
              + "where ru.tgl_registrasi between ? and ? ");
        if (!"Semua".equals(jalur))  sql.append("and ru.jalur='").append(jalur).append("' ");
        if (!"Semua".equals(status)) sql.append("and ru.status='").append(status).append("' ");
        if ("Belum terkirim".equals(kirim))
            sql.append("and (select count(*) from satu_sehat_uronefro_kirim k where k.id_registrasi=ru.id_registrasi)=0 ");
        else if ("Sudah terkirim".equals(kirim))
            sql.append("and (select count(*) from satu_sehat_uronefro_kirim k where k.id_registrasi=ru.id_registrasi)>0 ");
        if (!kw.isEmpty()) sql.append("and (p.nm_pasien like ? or ru.no_rkm_medis like ? or ru.no_rawat like ?) ");
        sql.append("order by ru.tgl_registrasi desc, ru.id_registrasi desc limit 1000");

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
                        rs.getString("alasan"), rs.getInt("nkirim") > 0 ? "Sudah" : "Belum",
                        rs.getInt("nbatu") + " temuan batu",
                        rs.getString("tgl_registrasi"), "", rs.getString("id_registrasi")
                    });
                }
            }
            lblInfo.setText("Total: " + tabMode.getRowCount());
        } catch (Exception ex) {
            System.out.println("Notif tampilData monitor uronefro : " + ex);
            JOptionPane.showMessageDialog(this, "Gagal memuat data:\n" + ex.getMessage());
        }
    }

    /** Pasien ber-ICD-10 uro-nefro yang belum punya registry. */
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
              + "left join registrasi_uronefro ru on ru.no_rkm_medis=rp.no_rkm_medis "
              + "where ru.id_registrasi is null "
              // CKD, batu saluran kemih, hidronefrosis, glomerulonefritis
              + "and dp.kd_penyakit regexp '^N(0[0-5]|1[3-9]|2[0-3])' "
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
                        "(kandidat)", rs.getString("kd_penyakit"), "-", "-", "-",
                        rs.getString("nm_penyakit"), rs.getString("tgl_registrasi"), "", ""
                    });
                }
            }
            lblInfo.setText("Kandidat belum teregistrasi: " + tabMode.getRowCount()
                    + "  — klik dua kali untuk membuka form registrasi");
        } catch (Exception ex) {
            System.out.println("Notif tampilKandidat uronefro : " + ex);
            JOptionPane.showMessageDialog(this, "Gagal memuat kandidat:\n" + ex.getMessage());
        }
    }

    /**
     * FASE 4 — registry yang memenuhi syarat penutupan episode tetapi belum ditutup.
     *
     * Dua pemicu yang dideteksi otomatis:
     *   - hilang kontak: tidak ada kunjungan >= 5 tahun (1825 hari)
     *   - sembuh batu  : jalur batu, usia > 18 tahun; penilaian sembuh tetap
     *                    keputusan klinisi, di sini hanya diusulkan
     *
     * Pemicu "meninggal" tidak masuk daftar ini karena sudah tertangkap lewat
     * status vital yang diisi petugas saat kejadian.
     */
    private void tampilKandidatTutup() {
        Valid.tabelKosong(tabMode);
        String sql =
                "select ru.no_rawat, ru.no_rkm_medis, ifnull(p.nm_pasien,'') nama, ru.jalur, ru.status, "
              + "ru.tgl_registrasi, ru.id_registrasi, "
              + "timestampdiff(year, p.tgl_lahir, curdate()) umur, "
              + "(select max(rp.tgl_registrasi) from reg_periksa rp "
              + " where rp.no_rkm_medis=ru.no_rkm_medis) kunjungan_terakhir, "
              + "datediff(curdate(), (select max(rp.tgl_registrasi) from reg_periksa rp "
              + " where rp.no_rkm_medis=ru.no_rkm_medis)) hari_sejak_kunjungan "
              + "from registrasi_uronefro ru "
              + "left join pasien p on p.no_rkm_medis=ru.no_rkm_medis "
              + "where ru.status <> 'finished' "
              + "having hari_sejak_kunjungan >= 1825 or (ru.jalur='batu' and umur > 18) "
              + "order by hari_sejak_kunjungan desc limit 1000";
        try (PreparedStatement ps = koneksi.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int hari = rs.getInt("hari_sejak_kunjungan");
                int umur = rs.getInt("umur");
                String usul;
                if (hari >= 1825) {
                    usul = "hilang_kontak (" + (hari / 365) + " thn tanpa kunjungan)";
                } else {
                    usul = "sembuh_batu? (usia " + umur + " thn — perlu penilaian klinisi)";
                }
                tabMode.addRow(new Object[]{
                    rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nama"),
                    rs.getString("jalur"), "-", rs.getString("status"), "USULAN", "-",
                    usul, rs.getString("tgl_registrasi"),
                    "terakhir: " + nz(rs.getString("kunjungan_terakhir")),
                    rs.getString("id_registrasi")
                });
            }
            lblInfo.setText("Kandidat penutupan episode: " + tabMode.getRowCount()
                    + "  — buka detail untuk menutup dengan alasan yang sesuai");
        } catch (Exception ex) {
            System.out.println("Notif tampilKandidatTutup : " + ex);
            JOptionPane.showMessageDialog(this, "Gagal memuat kandidat penutupan:\n" + ex.getMessage());
        }
    }

    private void bukaDetail() {
        int r = tbData.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Pilih baris dulu."); return; }
        String noRawat = String.valueOf(tabMode.getValueAt(r, 0));
        String noRkm   = String.valueOf(tabMode.getValueAt(r, 1));
        String nama    = String.valueOf(tabMode.getValueAt(r, 2));
        DlgRegistrasiUronefro form = new DlgRegistrasiUronefro(null, false);
        form.isCek();
        form.setSize(internalFrame1.getWidth() - 40, internalFrame1.getHeight() - 40);
        form.setLocationRelativeTo(internalFrame1);
        form.setVisible(true);
        form.setPasien(noRawat, noRkm, nama);
    }

    private void kirim() {
        if (!fungsi.akses.getsatu_sehat_kirim_condition()) {
            JOptionPane.showMessageDialog(this, "Anda tidak memiliki hak akses untuk Registrasi Uronefrologi.");
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
                "Kirim registrasi uronefrologi ini ke SatuSehat?\n"
              + "(Encounter kunjungan harus sudah terkirim lebih dulu.)",
                "Konfirmasi Kirim", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        BtnKirim.setEnabled(false);
        new Thread(() -> {
            final String hasil = SatuSehatKirimUronefro.kirim(id);
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
        }, "kirim-uronefro-global").start();
    }

    private static String nz(String s) { return s == null ? "" : s; }
}
