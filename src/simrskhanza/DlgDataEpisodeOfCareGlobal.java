/*
 * DlgDataEpisodeOfCareGlobal — dashboard chronic care monitoring lintas pasien.
 *
 * Tujuan:
 *   - List semua EpisodeOfCare semua pasien (cohort view)
 *   - Filter per type (TB-DOTS, HIV-AIDS, HD, dst), status, date range, nama/no_rm
 *   - Flag "perlu follow-up" kalau status='active' dan days_since_last_visit > threshold
 *     (threshold per type: TB 30hr, HIV 90hr, HD 7hr, ONCO 30hr, ANC 60hr, dst)
 *   - Bulk action: ubah status (mis. active → onhold) untuk row terpilih
 *   - Drill-down: double-click row → buka DlgDataEpisodeOfCarePasien (per-pasien detail)
 *
 * Dibuka dari menu utama frmUtama (btnDataEpisodeOfCareGlobal).
 */
package simrskhanza;

import bridging.SatuSehatEpisodeOfCareHelper;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class DlgDataEpisodeOfCareGlobal extends javax.swing.JDialog {

    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();

    // ---- Layout ----
    private widget.InternalFrame internalFrame1;
    private widget.Label lblSummary;

    // ---- Filter ----
    private widget.ComboBox cmbType;
    private widget.ComboBox cmbStatus;
    private widget.Tanggal  DTPStart;
    private widget.Tanggal  DTPEnd;
    private widget.TextBox  txtCari;
    private JCheckBox       chkOnlyFollowup;
    private widget.Button   btnRefresh;

    // ---- Table ----
    private widget.Table tbList;
    private DefaultTableModel tabMode;

    // ---- Button bar ----
    private widget.Button btnDetail, btnUpdateStatus, btnExport, btnTutup;

    /** Threshold follow-up per type EOC (hari). */
    private static final Map<String, Integer> FOLLOWUP_THRESHOLD = new HashMap<>();
    static {
        FOLLOWUP_THRESHOLD.put("TB-DOTS",  30);   // DOTS protokol bulanan
        FOLLOWUP_THRESHOLD.put("HIV-AIDS", 90);   // CD4/VL 3 bulan sekali
        FOLLOWUP_THRESHOLD.put("HD",        7);   // Hemodialisa 2-3x seminggu
        FOLLOWUP_THRESHOLD.put("ONCO",     30);   // Kemoterapi cycle bulanan
        FOLLOWUP_THRESHOLD.put("ANC",      60);   // ANC per trimester (~6-8 minggu)
        FOLLOWUP_THRESHOLD.put("PNC",      42);   // PNC ~6 minggu postpartum
        FOLLOWUP_THRESHOLD.put("REHAB",    14);   // Fisioterapi mingguan
        FOLLOWUP_THRESHOLD.put("MENTAL",   30);   // Konsul psikiatri bulanan
        FOLLOWUP_THRESHOLD.put("CHRONIC",  90);   // Prolanis/penyakit kronis
    }
    private static final int DEFAULT_FOLLOWUP_THRESHOLD = 90;

    /** Type list untuk filter combo (urut sama dgn helper). */
    private static final String[][] TYPE_OPTIONS = {
        {"", "-- Semua Tipe --"},
        {"ANC",      "ANC — Antenatal Care"},
        {"PNC",      "PNC — Postnatal Care"},
        {"TB-DOTS",  "TB-DOTS Program"},
        {"HIV-AIDS", "HIV-AIDS Program"},
        {"HD",       "HD — Hemodialysis"},
        {"ONCO",     "ONCO — Oncology"},
        {"REHAB",    "REHAB — Rehabilitation"},
        {"MENTAL",   "MENTAL — Mental Health"},
        {"CHRONIC",  "CHRONIC — Chronic Disease"}
    };

    private static final String[] STATUS_OPTIONS = {
        "", "active", "planned", "waitlist", "onhold", "finished", "cancelled", "entered-in-error"
    };

    public DlgDataEpisodeOfCareGlobal(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle(":: Data Episode of Care (Global Cohort Dashboard) ::");
        java.awt.Dimension scr = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(scr.width, scr.height - 40);
        setLocation(0, 0);
        buildUI();
        emptyFilter();
        tampil();
    }

    public void isCek() { /* no-op */ }

    // ====================================================================
    // UI BUILD
    // ====================================================================
    private void buildUI() {
        internalFrame1 = new widget.InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 235, 245)),
            "::[ Data Episode of Care — Cohort Dashboard ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11),
            new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(0, 4));

        internalFrame1.add(buildFilterPanel(), BorderLayout.NORTH);
        internalFrame1.add(buildTableArea(),   BorderLayout.CENTER);

        widget.panelisi bottomWrap = new widget.panelisi();
        bottomWrap.setLayout(new BorderLayout(0, 2));
        bottomWrap.add(buildSummaryPanel(), BorderLayout.NORTH);
        bottomWrap.add(buildButtonBar(),    BorderLayout.CENTER);
        internalFrame1.add(bottomWrap, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(internalFrame1, BorderLayout.CENTER);
    }

    private widget.panelisi buildFilterPanel() {
        widget.panelisi p = new widget.panelisi();
        p.setLayout(null);
        p.setPreferredSize(new Dimension(880, 90));
        p.setBorder(BorderFactory.createTitledBorder(":: Filter ::"));

        Font lblFont = new Font("Tahoma", 0, 11);
        int y1 = 24, y2 = 56;

        addLabel(p, "Tipe EOC :", 10, y1, 70, 22, lblFont);
        cmbType = new widget.ComboBox();
        String[] typeDisp = new String[TYPE_OPTIONS.length];
        for (int i = 0; i < TYPE_OPTIONS.length; i++) typeDisp[i] = TYPE_OPTIONS[i][1];
        cmbType.setModel(new DefaultComboBoxModel<>(typeDisp));
        cmbType.setBounds(80, y1, 230, 23);
        cmbType.addActionListener(e -> tampil());
        p.add(cmbType);

        addLabel(p, "Status :", 320, y1, 50, 22, lblFont);
        cmbStatus = new widget.ComboBox();
        cmbStatus.setModel(new DefaultComboBoxModel<>(STATUS_OPTIONS));
        cmbStatus.setBounds(370, y1, 130, 23);
        cmbStatus.addActionListener(e -> tampil());
        p.add(cmbStatus);

        addLabel(p, "Periode Tgl Buat :", 510, y1, 110, 22, lblFont);
        DTPStart = new widget.Tanggal();
        DTPStart.setDisplayFormat("dd-MM-yyyy");
        DTPStart.setBounds(620, y1, 120, 23);
        p.add(DTPStart);
        addLabel(p, "s/d", 745, y1, 25, 22, lblFont);
        DTPEnd = new widget.Tanggal();
        DTPEnd.setDisplayFormat("dd-MM-yyyy");
        DTPEnd.setBounds(770, y1, 120, 23);
        p.add(DTPEnd);

        addLabel(p, "Cari (Nama/No.RM) :", 10, y2, 130, 22, lblFont);
        txtCari = new widget.TextBox();
        txtCari.setBounds(140, y2, 250, 23);
        txtCari.setToolTipText("Ketik nama pasien atau no.RM, lalu klik Refresh");
        p.add(txtCari);

        chkOnlyFollowup = new JCheckBox("Hanya yang perlu follow-up");
        chkOnlyFollowup.setFont(lblFont);
        chkOnlyFollowup.setToolTipText("Filter: status active + days_since_last_visit > threshold per tipe");
        chkOnlyFollowup.setBounds(400, y2, 230, 23);
        chkOnlyFollowup.addActionListener(e -> tampil());
        p.add(chkOnlyFollowup);

        btnRefresh = new widget.Button();
        btnRefresh.setText("Refresh");
        btnRefresh.setMnemonic('R');
        btnRefresh.setBounds(640, y2, 100, 23);
        try {
            btnRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/refresh.png")));
        } catch (Exception ex) { /* icon optional */ }
        btnRefresh.addActionListener(e -> tampil());
        p.add(btnRefresh);

        return p;
    }

    private widget.panelisi buildTableArea() {
        widget.panelisi wrap = new widget.panelisi();
        wrap.setLayout(new BorderLayout());
        wrap.setBorder(BorderFactory.createTitledBorder(":: Daftar EpisodeOfCare ::"));

        tabMode = new DefaultTableModel(null, new String[]{
            "ID EOC", "No.RM", "Nama Pasien", "Tipe", "Status", "Tgl Mulai", "Tgl Selesai",
            "ICD-10", "Diagnosa", "Care Manager", "Kunjungan Terakhir", "Hari Sejak Kunjungan",
            "Threshold (hr)", "Follow-up?", "Total Kunjungan"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbList = new widget.Table();
        tbList.setModel(tabMode);
        tbList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {180, 80, 180, 90, 80, 90, 90, 70, 200, 150, 130, 110, 90, 90, 90};
        for (int i = 0; i < widths.length; i++) {
            tbList.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        // Renderer untuk highlight row yang perlu follow-up (color-coded)
        tbList.setDefaultRenderer(Object.class, new FollowupRenderer());

        // Double-click row → buka per-pasien detail
        tbList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && tbList.getSelectedRow() >= 0) {
                    openDetailPerPasien();
                }
            }
        });

        widget.ScrollPane scroll = new widget.ScrollPane();
        scroll.setViewportView(tbList);
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    private widget.panelisi buildSummaryPanel() {
        widget.panelisi p = new widget.panelisi();
        p.setLayout(new BorderLayout());
        p.setPreferredSize(new Dimension(880, 30));
        lblSummary = new widget.Label();
        lblSummary.setText("Total: 0  |  Active: 0  |  Perlu follow-up: 0");
        lblSummary.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblSummary.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        p.add(lblSummary, BorderLayout.CENTER);
        return p;
    }

    private widget.panelisi buildButtonBar() {
        widget.panelisi bar = new widget.panelisi();
        bar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 4));
        bar.setPreferredSize(new Dimension(880, 38));

        btnDetail       = mkButton("Detail per Pasien",  "Alt+D - Buka dialog EOC detail untuk pasien terpilih (atau double-click row)", 'D', "/picture/Search-16x16.png", e -> openDetailPerPasien());
        btnUpdateStatus = mkButton("Bulk Update Status", "Alt+U - Update status EOC terpilih (mis. mark onhold/finished)", 'U', "/picture/save-16x16.png",   e -> bulkUpdateStatus());
        btnExport       = mkButton("Export CSV",          "Alt+E - Export tabel ke CSV (untuk laporan kohort)",          'E', "/picture/save-16x16.png",   e -> exportCsv());
        btnTutup        = mkButton("Tutup",                "Alt+T - Tutup form",                                          'T', "/picture/exit.png",         e -> dispose());

        bar.add(btnDetail);
        bar.add(btnUpdateStatus);
        bar.add(btnExport);
        bar.add(btnTutup);
        return bar;
    }

    // ====================================================================
    // HELPERS UI
    // ====================================================================
    private void addLabel(JPanel parent, String text, int x, int y, int w, int h, Font f) {
        widget.Label lbl = new widget.Label();
        lbl.setText(text);
        lbl.setFont(f);
        lbl.setBounds(x, y, w, h);
        parent.add(lbl);
    }

    private widget.Button mkButton(String label, String tip, char mnemonic, String iconPath, java.awt.event.ActionListener act) {
        widget.Button b = new widget.Button();
        b.setText(label);
        b.setToolTipText(tip);
        b.setMnemonic(mnemonic);
        b.setFont(new Font("Tahoma", 0, 11));
        if (iconPath != null) {
            try { b.setIcon(new javax.swing.ImageIcon(getClass().getResource(iconPath))); }
            catch (Exception ex) { /* icon optional */ }
        }
        b.addActionListener(act);
        return b;
    }

    private void emptyFilter() {
        cmbType.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        // Default date range: 1 tahun terakhir
        java.util.Calendar cal = java.util.Calendar.getInstance();
        Valid.SetTgl(DTPEnd, new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
        cal.add(java.util.Calendar.YEAR, -1);
        Valid.SetTgl(DTPStart, new java.text.SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
        txtCari.setText("");
        chkOnlyFollowup.setSelected(false);
    }

    // ====================================================================
    // DATA OPS
    // ====================================================================
    private void tampil() {
        Valid.tabelKosong(tabMode);

        StringBuilder sql = new StringBuilder(
            "select eoc.id_eoc, eoc.no_rkm_medis, p.nm_pasien, eoc.type_code, eoc.type_display, "
          + "eoc.status, eoc.period_start, eoc.period_end, "
          + "ifnull(eoc.condition_kd_penyakit,'') as kd_penyakit, "
          + "ifnull(pk.nm_penyakit,'') as nm_penyakit, "
          + "ifnull(pg.nama,'') as care_manager_nama, "
          + "eoc.tgl_create, "
          + "(select max(rp.tgl_registrasi) from satu_sehat_encounter_episodeofcare lnk "
          + "  inner join reg_periksa rp on rp.no_rawat = lnk.no_rawat "
          + "  where lnk.id_eoc = eoc.id_eoc) as last_visit_date, "
          + "(select count(*) from satu_sehat_encounter_episodeofcare lnk2 "
          + "  where lnk2.id_eoc = eoc.id_eoc) as total_kunjungan "
          + "from satu_sehat_episodeofcare eoc "
          + "inner join pasien p on p.no_rkm_medis = eoc.no_rkm_medis "
          + "left join penyakit pk on pk.kd_penyakit = eoc.condition_kd_penyakit "
          + "left join pegawai pg on pg.nik = eoc.care_manager_nik "
          + "where eoc.tgl_create between ? and ? ");

        String typeFilter = TYPE_OPTIONS[cmbType.getSelectedIndex()][0];
        String statusFilter = STATUS_OPTIONS[cmbStatus.getSelectedIndex()];
        String cari = txtCari.getText().trim();

        if (!typeFilter.isEmpty())   sql.append("and eoc.type_code = ? ");
        if (!statusFilter.isEmpty()) sql.append("and eoc.status = ? ");
        if (!cari.isEmpty())         sql.append("and (p.nm_pasien like ? or eoc.no_rkm_medis like ?) ");
        sql.append("order by eoc.tgl_create desc");

        int totalCount = 0, activeCount = 0, followupCount = 0;

        try (PreparedStatement ps = koneksi.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setString(idx++, Valid.SetTgl(DTPStart.getSelectedItem() + "") + " 00:00:00");
            ps.setString(idx++, Valid.SetTgl(DTPEnd.getSelectedItem() + "") + " 23:59:59");
            if (!typeFilter.isEmpty())   ps.setString(idx++, typeFilter);
            if (!statusFilter.isEmpty()) ps.setString(idx++, statusFilter);
            if (!cari.isEmpty()) {
                ps.setString(idx++, "%" + cari + "%");
                ps.setString(idx++, "%" + cari + "%");
            }
            ResultSet rs = ps.executeQuery();
            java.text.SimpleDateFormat dateFmt = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Date now = new java.util.Date();

            while (rs.next()) {
                String type = rs.getString("type_code");
                String status = rs.getString("status");
                java.sql.Date lastVisit = rs.getDate("last_visit_date");
                java.sql.Date periodStart = rs.getDate("period_start");
                java.sql.Date baseline = lastVisit != null ? lastVisit : periodStart;

                long daysSince = -1;
                if (baseline != null) {
                    daysSince = (now.getTime() - baseline.getTime()) / (1000L * 60 * 60 * 24);
                }
                int threshold = FOLLOWUP_THRESHOLD.getOrDefault(type, DEFAULT_FOLLOWUP_THRESHOLD);
                boolean needsFollowup = "active".equals(status) && daysSince > threshold;

                // Apply follow-up filter
                if (chkOnlyFollowup.isSelected() && !needsFollowup) continue;

                tabMode.addRow(new Object[]{
                    rs.getString("id_eoc"),
                    rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"),
                    type,
                    status,
                    periodStart != null ? dateFmt.format(periodStart) : "",
                    rs.getDate("period_end") != null ? dateFmt.format(rs.getDate("period_end")) : "",
                    rs.getString("kd_penyakit"),
                    rs.getString("nm_penyakit"),
                    rs.getString("care_manager_nama"),
                    lastVisit != null ? dateFmt.format(lastVisit) : "(belum ada link)",
                    daysSince >= 0 ? String.valueOf(daysSince) : "-",
                    String.valueOf(threshold),
                    needsFollowup ? "YA" : "-",
                    String.valueOf(rs.getInt("total_kunjungan"))
                });

                totalCount++;
                if ("active".equals(status)) activeCount++;
                if (needsFollowup) followupCount++;
            }
            rs.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal load data: " + ex.getMessage());
            System.out.println("Notif tampil EOC Global : " + ex);
        }

        lblSummary.setText("Total: " + totalCount + "  |  Active: " + activeCount
                         + "  |  Perlu follow-up: " + followupCount
                         + "  (filter: " + (typeFilter.isEmpty() ? "Semua tipe" : typeFilter)
                         + ", " + (statusFilter.isEmpty() ? "Semua status" : statusFilter) + ")");
    }

    /** Buka DlgDataEpisodeOfCarePasien dengan pasien terpilih (per-detail view). */
    private void openDetailPerPasien() {
        int r = tbList.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu baris EOC di tabel.");
            return;
        }
        String noRm = String.valueOf(tabMode.getValueAt(r, 1));
        String nmPasien = String.valueOf(tabMode.getValueAt(r, 2));
        // Cari no_rawat terakhir untuk pasien ini (best-effort, untuk context)
        String noRawat = lookupLatestNoRawat(noRm);

        DlgDataEpisodeOfCarePasien form = new DlgDataEpisodeOfCarePasien(null, false);
        form.isCek();
        form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        form.setLocationRelativeTo(internalFrame1);
        form.setVisible(true);
        form.setNoRm(noRawat == null ? "" : noRawat, noRm, nmPasien);
    }

    private String lookupLatestNoRawat(String noRm) {
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select no_rawat from reg_periksa where no_rkm_medis=? order by tgl_registrasi desc, jam_reg desc limit 1")) {
            ps.setString(1, noRm);
            ResultSet rs = ps.executeQuery();
            String r = rs.next() ? rs.getString(1) : null;
            rs.close();
            return r;
        } catch (Exception ex) { return null; }
    }

    /** Bulk update status untuk row terpilih (single-select dulu untuk safety). */
    private void bulkUpdateStatus() {
        int r = tbList.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Pilih dulu baris EOC di tabel.");
            return;
        }
        String idEoc = String.valueOf(tabMode.getValueAt(r, 0));
        String currentStatus = String.valueOf(tabMode.getValueAt(r, 4));

        String[] choices = {"active", "onhold", "finished", "cancelled", "entered-in-error"};
        String newStatus = (String) JOptionPane.showInputDialog(this,
            "Pilih status baru untuk EOC " + idEoc + "\n(current: " + currentStatus + ")",
            "Update Status EOC",
            JOptionPane.QUESTION_MESSAGE, null, choices, currentStatus);
        if (newStatus == null || newStatus.equals(currentStatus)) return;

        String periodEnd = null;
        if ("finished".equals(newStatus) || "cancelled".equals(newStatus)) {
            int ok = JOptionPane.showConfirmDialog(this,
                "Status " + newStatus + " biasanya disertai Tgl Selesai = hari ini.\nSet period_end ke hari ini?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                periodEnd = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
            }
        }

        boolean ok = SatuSehatEpisodeOfCareHelper.updateStatus(idEoc, newStatus, periodEnd);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Gagal update status ke SatuSehat. Cek log console.");
            return;
        }
        JOptionPane.showMessageDialog(this, "Status EOC berhasil di-update ke: " + newStatus);
        tampil();
    }

    /** Export tabel ke CSV untuk laporan kohort. */
    private void exportCsv() {
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tabel kosong, tidak ada data untuk di-export.");
            return;
        }
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setSelectedFile(new java.io.File("eoc_cohort_"
            + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date())
            + ".csv"));
        if (chooser.showSaveDialog(this) != javax.swing.JFileChooser.APPROVE_OPTION) return;
        java.io.File f = chooser.getSelectedFile();

        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(f))) {
            // Header
            StringBuilder header = new StringBuilder();
            for (int c = 0; c < tabMode.getColumnCount(); c++) {
                if (c > 0) header.append(",");
                header.append(csvEscape(tabMode.getColumnName(c)));
            }
            pw.println(header);
            // Rows
            for (int r = 0; r < tabMode.getRowCount(); r++) {
                StringBuilder row = new StringBuilder();
                for (int c = 0; c < tabMode.getColumnCount(); c++) {
                    if (c > 0) row.append(",");
                    Object val = tabMode.getValueAt(r, c);
                    row.append(csvEscape(val == null ? "" : val.toString()));
                }
                pw.println(row);
            }
            JOptionPane.showMessageDialog(this, "Export sukses: " + f.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal export: " + ex.getMessage());
        }
    }

    private static String csvEscape(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    /** Cell renderer dgn color-coding untuk highlight follow-up needed. */
    private class FollowupRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                String followup = String.valueOf(tabMode.getValueAt(row, 13)); // "Follow-up?" column
                String status = String.valueOf(tabMode.getValueAt(row, 4));    // "Status" column
                if ("YA".equals(followup)) {
                    c.setBackground(new Color(255, 220, 220));   // light red
                    c.setForeground(new Color(140, 0, 0));
                } else if ("finished".equals(status)) {
                    c.setBackground(new Color(230, 240, 230));   // light green
                    c.setForeground(new Color(50, 100, 50));
                } else if ("cancelled".equals(status) || "entered-in-error".equals(status)) {
                    c.setBackground(new Color(240, 240, 240));   // gray
                    c.setForeground(new Color(120, 120, 120));
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
            }
            // Align numeric columns center
            if (column == 11 || column == 12 || column == 14) {
                setHorizontalAlignment(SwingConstants.CENTER);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }
            return c;
        }
    }
}
