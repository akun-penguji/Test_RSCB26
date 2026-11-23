/*
 * Form Input Data Episode of Care Pasien — manual CRUD untuk EpisodeOfCare FHIR SatuSehat.
 *
 * Dipakai untuk kasus chronic disease (TB-DOTS, HIV, HD, ONCO) atau program longitudinal
 * (ANC, PNC, REHAB, MENTAL, CHRONIC) dimana 1 EOC menampung banyak Encounter pasien yang
 * sama. Bedakan dengan auto-creation di SatuSehatKirimEncounter yang trigger by poli.
 *
 * Workflow:
 *   - Tambah: pilih type + period_start + (opsional) condition ICD-10 + care manager →
 *     panggil SatuSehatEpisodeOfCareHelper.getOrCreate() (REUSE kalau ada active sama)
 *   - Update Status: select row di tabel → ubah status (mis. active→finished + period_end) →
 *     panggil SatuSehatEpisodeOfCareHelper.updateStatus() (PUT ke SatuSehat)
 *   - Hapus: hanya hapus local (tidak DELETE ke SatuSehat — FHIR EpisodeOfCare tidak
 *     mendukung DELETE; pakai status entered-in-error untuk soft-delete)
 *
 * Dibuka dari DlgRawatJalan / DlgRawatInap via tombol "Data Episode of Care Pasien"
 * setelah user pilih pasien (caller wajib panggil setNoRm()).
 */
package simrskhanza;

import bridging.SatuSehatCekNIK;
import bridging.SatuSehatEpisodeOfCareHelper;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import kepegawaian.DlgCariPetugas;
import laporan.DlgCariPenyakit;

public final class DlgDataEpisodeOfCarePasien extends javax.swing.JDialog {

    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();
    private final SatuSehatCekNIK cekViaSatuSehat = new SatuSehatCekNIK();

    // Context pasien
    private String noRawat = "", noRkm = "", namaPasien = "";

    // Mode edit: kalau idEocSelected != "" berarti sedang edit EOC existing
    private String idEocSelected = "";

    // ---- Header ----
    private widget.InternalFrame internalFrame1;
    private widget.Label lblPasien;

    // ---- Input fields ----
    private widget.ComboBox cmbType;
    private widget.ComboBox cmbStatus;
    private widget.Tanggal  DTPStart;
    private widget.Tanggal  DTPEnd;
    private javax.swing.JCheckBox chkPeriodEnd;
    private widget.TextBox  txtConditionKd;
    private widget.TextBox  txtConditionNm;
    private widget.Button   BtnSeekCondition;
    private widget.TextBox  txtCareManagerNik;
    private widget.TextBox  txtCareManagerNama;
    private widget.Button   BtnSeekCareManager;
    private widget.TextArea txtKeterangan;
    private widget.TextBox  txtIdEoc;

    // ---- Buttons ----
    private widget.Button BtnSimpan, BtnBersihkan, BtnUpdate, BtnHapus, BtnTutup;

    // ---- Table ----
    private widget.Table tbList;
    private DefaultTableModel tabMode;

    // ---- Dialog cari ----
    private DlgCariPetugas dlgPetugas;
    private DlgCariPenyakit dlgPenyakit;

    /** 9 type EpisodeOfCare Kemkes (lihat SatuSehatEpisodeOfCareHelper.getTypeByPoli). */
    private static final String[][] EOC_TYPES = {
        {"ANC",      "Antenatal Care"},
        {"PNC",      "Postnatal Care"},
        {"TB-DOTS",  "TB-DOTS Program"},
        {"HIV-AIDS", "HIV-AIDS Program"},
        {"HD",       "Hemodialysis Program"},
        {"ONCO",     "Oncology Program"},
        {"REHAB",    "Rehabilitation Program"},
        {"MENTAL",   "Mental Health Program"},
        {"CHRONIC",  "Chronic Disease Management"}
    };

    /** Status FHIR EpisodeOfCare valid values. */
    private static final String[] EOC_STATUS = {
        "active", "planned", "waitlist", "onhold", "finished", "cancelled", "entered-in-error"
    };

    public DlgDataEpisodeOfCarePasien(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle(":: Data Episode of Care Pasien (FHIR EpisodeOfCare SatuSehat) ::");
        java.awt.Dimension scr = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(scr.width, scr.height - 40);
        setLocation(0, 0);
        buildUI();
        emptyFields();
    }

    /** Caller wajib panggil setelah konstruktor. */
    public void setNoRm(String noRwt, String noRm, String nama) {
        this.noRawat = noRwt;
        this.noRkm = noRm;
        this.namaPasien = nama;
        lblPasien.setText("Pasien: " + nama + "  |  No.RM: " + noRm + "  |  No.Rawat: " + noRwt);
        tampilData();
    }

    /** Placeholder untuk konsistensi pattern Khanza. */
    public void isCek() { /* no-op */ }

    // ====================================================================
    // UI BUILD
    // ====================================================================
    private void buildUI() {
        internalFrame1 = new widget.InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(220, 235, 245)),
            "::[ Data Episode of Care Pasien ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11),
            new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(0, 4));

        internalFrame1.add(buildHeader(), BorderLayout.NORTH);
        internalFrame1.add(buildInputForm(), BorderLayout.CENTER);

        widget.panelisi bottomWrap = new widget.panelisi();
        bottomWrap.setLayout(new BorderLayout(0, 2));
        bottomWrap.add(buildTableArea(), BorderLayout.CENTER);
        bottomWrap.add(buildButtonBar(), BorderLayout.SOUTH);
        internalFrame1.add(bottomWrap, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(internalFrame1, BorderLayout.CENTER);

        // Dialog cari petugas (modal=true penting kalau parent ini sendiri modal —
        // hindari issue focus seperti yang sebelumnya muncul di KIPI dialog)
        dlgPetugas = new DlgCariPetugas(null, true);
        dlgPetugas.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override public void windowClosed(WindowEvent e) {
                if (dlgPetugas.getTable().getSelectedRow() != -1) {
                    txtCareManagerNik.setText(dlgPetugas.getTable().getValueAt(
                        dlgPetugas.getTable().getSelectedRow(), 0).toString());
                    txtCareManagerNama.setText(dlgPetugas.getTable().getValueAt(
                        dlgPetugas.getTable().getSelectedRow(), 1).toString());
                }
            }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });

        // Dialog cari penyakit
        dlgPenyakit = new DlgCariPenyakit(null, true);
        dlgPenyakit.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override public void windowClosed(WindowEvent e) {
                if (dlgPenyakit.getTable().getSelectedRow() != -1) {
                    txtConditionKd.setText(dlgPenyakit.getTable().getValueAt(
                        dlgPenyakit.getTable().getSelectedRow(), 0).toString());
                    txtConditionNm.setText(dlgPenyakit.getTable().getValueAt(
                        dlgPenyakit.getTable().getSelectedRow(), 1).toString());
                }
            }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });
    }

    private widget.panelisi buildHeader() {
        widget.panelisi p = new widget.panelisi();
        p.setLayout(null);
        p.setPreferredSize(new Dimension(880, 32));
        lblPasien = new widget.Label();
        lblPasien.setText("Pasien: -");
        lblPasien.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblPasien.setBounds(10, 6, 870, 22);
        p.add(lblPasien);
        return p;
    }

    private widget.panelisi buildInputForm() {
        widget.panelisi p = new widget.panelisi();
        p.setLayout(null);
        p.setPreferredSize(new Dimension(880, 380));
        p.setBorder(BorderFactory.createTitledBorder(":: Input Data ::"));

        Font lblFont = new Font("Tahoma", 0, 11);
        int LX = 14, FX = 170, RH = 28, W2 = 200, W4 = 600;
        int y = 26;

        // ID EOC (read-only — hanya display saat edit existing)
        addLabel(p, "ID EOC (SatuSehat) :", LX, y, 150, 22, lblFont);
        txtIdEoc = new widget.TextBox();
        txtIdEoc.setEditable(false);
        txtIdEoc.setBackground(new Color(245, 245, 245));
        txtIdEoc.setToolTipText("UUID dari SatuSehat — kosong artinya EOC belum dibuat (CREATE mode)");
        txtIdEoc.setBounds(FX, y, W4, 23);
        p.add(txtIdEoc);
        y += RH;

        // Type EOC
        addLabel(p, "Tipe EOC :", LX, y, 150, 22, lblFont);
        cmbType = new widget.ComboBox();
        String[] typeDisp = new String[EOC_TYPES.length];
        for (int i = 0; i < EOC_TYPES.length; i++) typeDisp[i] = EOC_TYPES[i][0] + " — " + EOC_TYPES[i][1];
        cmbType.setModel(new DefaultComboBoxModel<>(typeDisp));
        cmbType.setBounds(FX, y, W4, 23);
        cmbType.setToolTipText("Pilih program/jenis episode. Helper akan REUSE EOC active dengan type sama.");
        p.add(cmbType);
        y += RH;

        // Status
        addLabel(p, "Status :", LX, y, 150, 22, lblFont);
        cmbStatus = new widget.ComboBox();
        cmbStatus.setModel(new DefaultComboBoxModel<>(EOC_STATUS));
        cmbStatus.setBounds(FX, y, W2, 23);
        cmbStatus.setToolTipText("active = sedang berjalan; finished = selesai; entered-in-error = soft-delete");
        p.add(cmbStatus);
        y += RH;

        // Period Start
        addLabel(p, "Tgl Mulai :", LX, y, 150, 22, lblFont);
        DTPStart = new widget.Tanggal();
        DTPStart.setDisplayFormat("dd-MM-yyyy");
        DTPStart.setBounds(FX, y, 130, 23);
        p.add(DTPStart);

        // Period End (optional via checkbox)
        chkPeriodEnd = new javax.swing.JCheckBox("Set Tgl Selesai");
        chkPeriodEnd.setBounds(FX + 145, y, 130, 23);
        chkPeriodEnd.setToolTipText("Centang kalau EOC sudah selesai (akan set period.end)");
        chkPeriodEnd.addActionListener(e -> DTPEnd.setEnabled(chkPeriodEnd.isSelected()));
        p.add(chkPeriodEnd);

        DTPEnd = new widget.Tanggal();
        DTPEnd.setDisplayFormat("dd-MM-yyyy");
        DTPEnd.setBounds(FX + 280, y, 130, 23);
        DTPEnd.setEnabled(false);
        p.add(DTPEnd);
        y += RH;

        // Condition ICD-10
        addLabel(p, "Diagnosa (ICD-10) :", LX, y, 150, 22, lblFont);
        txtConditionKd = new widget.TextBox();
        txtConditionKd.setDocument(new batasInput((byte) 10).getKata(txtConditionKd));
        txtConditionKd.setBounds(FX, y, 80, 23);
        p.add(txtConditionKd);
        txtConditionNm = new widget.TextBox();
        txtConditionNm.setEditable(false);
        txtConditionNm.setBounds(FX + 85, y, 480, 23);
        p.add(txtConditionNm);
        BtnSeekCondition = new widget.Button();
        BtnSeekCondition.setText("...");
        BtnSeekCondition.setToolTipText("Cari diagnosa");
        BtnSeekCondition.setBounds(FX + 568, y, 30, 23);
        BtnSeekCondition.addActionListener(e -> cariPenyakit());
        p.add(BtnSeekCondition);
        y += RH;

        // Care Manager
        addLabel(p, "Care Manager (NIK) :", LX, y, 150, 22, lblFont);
        txtCareManagerNik = new widget.TextBox();
        txtCareManagerNik.setDocument(new batasInput((byte) 20).getKata(txtCareManagerNik));
        txtCareManagerNik.setBounds(FX, y, 110, 23);
        p.add(txtCareManagerNik);
        txtCareManagerNama = new widget.TextBox();
        txtCareManagerNama.setEditable(false);
        txtCareManagerNama.setBounds(FX + 115, y, 450, 23);
        p.add(txtCareManagerNama);
        BtnSeekCareManager = new widget.Button();
        BtnSeekCareManager.setText("...");
        BtnSeekCareManager.setToolTipText("Cari pegawai");
        BtnSeekCareManager.setBounds(FX + 568, y, 30, 23);
        BtnSeekCareManager.addActionListener(e -> cariPetugas());
        p.add(BtnSeekCareManager);
        y += RH;

        // Keterangan
        addLabel(p, "Keterangan :", LX, y, 150, 22, lblFont);
        txtKeterangan = new widget.TextArea();
        txtKeterangan.setRows(2);
        JScrollPane spKet = new JScrollPane(txtKeterangan);
        spKet.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        spKet.setBounds(FX, y, W4, 46);
        p.add(spKet);
        y += 52;

        // Petunjuk Pengisian
        widget.Label lblPetunjuk = new widget.Label();
        lblPetunjuk.setText(
            "<html><div style='font-family:Tahoma; font-size:10px; padding:6px;'>"
          + "<b>Petunjuk Pengisian:</b><br>"
          + "&bull; <b>Tipe EOC</b>: program longitudinal pasien. Mis. <i>TB-DOTS</i>, <i>HIV-AIDS</i>, <i>HD</i> (Hemodialisis), <i>ONCO</i> (kemoterapi). 1 EOC active per pasien per tipe — helper akan REUSE bukan create baru.<br>"
          + "&bull; <b>Status</b>: <i>active</i>=sedang berjalan; <i>finished</i>=selesai (set Tgl Selesai); <i>cancelled</i>=batal; <i>entered-in-error</i>=soft-delete (FHIR tidak support DELETE).<br>"
          + "&bull; <b>Tgl Mulai</b>: tanggal episode dimulai. Default hari ini kalau kosong.<br>"
          + "&bull; <b>Tgl Selesai</b>: centang &amp; isi <u>hanya</u> kalau status finished/cancelled.<br>"
          + "&bull; <b>Diagnosa</b>: ICD-10 utama untuk episode (opsional). Mis. <code>A15</code>=TB Paru, <code>B20</code>=HIV, <code>N18</code>=CKD, <code>C50</code>=Ca Mammae.<br>"
          + "&bull; <b>Care Manager</b>: case manager / DPJP penanggung jawab program (opsional).<br>"
          + "&bull; <b>Tombol Simpan</b>: kalau ID EOC kosong → CREATE (POST ke SatuSehat). Kalau sudah ada → silakan pakai tombol Update Status."
          + "</div></html>");
        lblPetunjuk.setBackground(new Color(255, 252, 220));
        lblPetunjuk.setOpaque(true);
        lblPetunjuk.setBorder(BorderFactory.createLineBorder(new Color(220, 200, 100)));
        lblPetunjuk.setBounds(LX, y, W4 + 150, 130);
        p.add(lblPetunjuk);

        return p;
    }

    private widget.panelisi buildTableArea() {
        widget.panelisi wrap = new widget.panelisi();
        wrap.setLayout(new BorderLayout());
        wrap.setPreferredSize(new Dimension(880, 220));
        wrap.setBorder(BorderFactory.createTitledBorder(":: Daftar Episode of Care Pasien ::"));

        tabMode = new DefaultTableModel(null, new String[]{
            "ID EOC", "Tipe", "Display", "Status", "Tgl Mulai", "Tgl Selesai",
            "ICD-10", "Diagnosa", "Care Manager NIK", "Care Manager"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbList = new widget.Table();
        tbList.setModel(tabMode);
        tbList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {260, 80, 200, 90, 90, 90, 70, 220, 130, 180};
        for (int i = 0; i < widths.length; i++) {
            tbList.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
        tbList.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedRowToForm();
        });

        widget.ScrollPane scroll = new widget.ScrollPane();
        scroll.setViewportView(tbList);
        wrap.add(scroll, BorderLayout.CENTER);
        return wrap;
    }

    private widget.panelisi buildButtonBar() {
        widget.panelisi bar = new widget.panelisi();
        bar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 4));
        bar.setPreferredSize(new Dimension(880, 38));
        BtnSimpan    = mkButton("Simpan & Sync", "Alt+S - POST ke SatuSehat & simpan local (CREATE)",  'S', "/picture/save-16x16.png",     e -> simpan());
        BtnUpdate    = mkButton("Update Status", "Alt+U - PUT update status ke SatuSehat",             'U', "/picture/save-16x16.png",     e -> updateStatus());
        BtnBersihkan = mkButton("Bersihkan",    "Alt+B - Reset form ke kondisi awal",                  'B', "/picture/Cancel-2-16x16.png", e -> emptyFields());
        BtnHapus     = mkButton("Hapus Lokal",  "Alt+H - Hapus dari DB lokal saja (tidak dari SatuSehat)", 'H', "/picture/stop_f2.png",        e -> hapus());
        BtnTutup     = mkButton("Tutup",        "Alt+T - Tutup form",                                  'T', "/picture/exit.png",           e -> dispose());
        bar.add(BtnSimpan); bar.add(BtnUpdate); bar.add(BtnBersihkan); bar.add(BtnHapus); bar.add(BtnTutup);
        return bar;
    }

    // ============================================================
    // HELPERS UI
    // ============================================================
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

    private void cariPetugas() {
        dlgPetugas.emptTeks();
        dlgPetugas.isCek();
        dlgPetugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dlgPetugas.setLocationRelativeTo(internalFrame1);
        dlgPetugas.setVisible(true);
    }

    private void cariPenyakit() {
        dlgPenyakit.emptTeks();
        dlgPenyakit.isCek();
        dlgPenyakit.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dlgPenyakit.setLocationRelativeTo(internalFrame1);
        dlgPenyakit.setVisible(true);
    }

    // ============================================================
    // DATA OPS
    // ============================================================
    private void tampilData() {
        Valid.tabelKosong(tabMode);
        if (noRkm == null || noRkm.isEmpty()) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select eoc.id_eoc, eoc.type_code, eoc.type_display, eoc.status, "
              + "eoc.period_start, eoc.period_end, eoc.condition_kd_penyakit, "
              + "ifnull(pk.nm_penyakit,'') as nm_penyakit, "
              + "ifnull(eoc.care_manager_nik,'') as nik, "
              + "ifnull(pg.nama,'') as nama_cm "
              + "from satu_sehat_episodeofcare eoc "
              + "left join penyakit pk on pk.kd_penyakit = eoc.condition_kd_penyakit "
              + "left join pegawai pg on pg.nik = eoc.care_manager_nik "
              + "where eoc.no_rkm_medis = ? "
              + "order by eoc.tgl_create desc")) {
            ps.setString(1, noRkm);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new Object[]{
                    rs.getString("id_eoc"),
                    rs.getString("type_code"),
                    rs.getString("type_display"),
                    rs.getString("status"),
                    rs.getString("period_start"),
                    rs.getString("period_end"),
                    rs.getString("condition_kd_penyakit"),
                    rs.getString("nm_penyakit"),
                    rs.getString("nik"),
                    rs.getString("nama_cm")
                });
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("Notif tampilData EOC : " + ex);
        }
    }

    private void loadSelectedRowToForm() {
        int r = tbList.getSelectedRow();
        if (r < 0) return;
        idEocSelected = String.valueOf(tabMode.getValueAt(r, 0));
        txtIdEoc.setText(idEocSelected);

        String tc = String.valueOf(tabMode.getValueAt(r, 1));
        for (int k = 0; k < EOC_TYPES.length; k++) {
            if (EOC_TYPES[k][0].equals(tc)) { cmbType.setSelectedIndex(k); break; }
        }
        String st = String.valueOf(tabMode.getValueAt(r, 3));
        for (int k = 0; k < EOC_STATUS.length; k++) {
            if (EOC_STATUS[k].equals(st)) { cmbStatus.setSelectedIndex(k); break; }
        }
        String ps_ = String.valueOf(tabMode.getValueAt(r, 4));
        if (ps_ != null && !"null".equals(ps_) && !ps_.isEmpty()) Valid.SetTgl(DTPStart, ps_);

        String pe = String.valueOf(tabMode.getValueAt(r, 5));
        if (pe != null && !"null".equals(pe) && !pe.isEmpty()) {
            chkPeriodEnd.setSelected(true);
            DTPEnd.setEnabled(true);
            Valid.SetTgl(DTPEnd, pe);
        } else {
            chkPeriodEnd.setSelected(false);
            DTPEnd.setEnabled(false);
        }

        String kd = String.valueOf(tabMode.getValueAt(r, 6));
        String nm = String.valueOf(tabMode.getValueAt(r, 7));
        txtConditionKd.setText("null".equals(kd) ? "" : (kd == null ? "" : kd));
        txtConditionNm.setText("null".equals(nm) ? "" : (nm == null ? "" : nm));

        String nik = String.valueOf(tabMode.getValueAt(r, 8));
        String nama = String.valueOf(tabMode.getValueAt(r, 9));
        txtCareManagerNik.setText("null".equals(nik) ? "" : (nik == null ? "" : nik));
        txtCareManagerNama.setText("null".equals(nama) ? "" : (nama == null ? "" : nama));
    }

    private void emptyFields() {
        idEocSelected = "";
        txtIdEoc.setText("");
        cmbType.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        Valid.SetTgl(DTPStart, new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        chkPeriodEnd.setSelected(false);
        DTPEnd.setEnabled(false);
        Valid.SetTgl(DTPEnd, new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));
        txtConditionKd.setText("");
        txtConditionNm.setText("");
        // Care manager auto-fill kalau akses.getkode() valid NIP
        String kd = akses.getkode();
        String nama = lookupNamaPegawai(kd);
        if (!nama.isEmpty()) {
            txtCareManagerNik.setText(kd);
            txtCareManagerNama.setText(nama);
        } else {
            txtCareManagerNik.setText("");
            txtCareManagerNama.setText("");
        }
        txtKeterangan.setText("");
        tbList.clearSelection();
    }

    private String lookupNamaPegawai(String nik) {
        if (nik == null || nik.isEmpty()) return "";
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select nama from pegawai where nik=? limit 1")) {
            ps.setString(1, nik);
            ResultSet rs = ps.executeQuery();
            String n = rs.next() ? rs.getString(1) : "";
            rs.close();
            return n == null ? "" : n;
        } catch (Exception ex) { return ""; }
    }

    /**
     * Simpan = CREATE new EOC. Kalau ada EOC active dengan type yang sama,
     * helper getOrCreate akan REUSE (return id existing) — tidak duplicate.
     */
    private void simpan() {
        if (noRawat == null || noRawat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No.Rawat kosong — silakan pilih pasien dulu dari layar Rawat Jalan/Inap.");
            return;
        }
        if (!txtIdEoc.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "EOC ini sudah ada di SatuSehat (ID terisi).\nUntuk ubah status pakai tombol [Update Status].");
            return;
        }
        // Lookup IHS patient ID
        String noKtp = lookupNoKtpPasien(noRkm);
        if (noKtp == null || noKtp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pasien tidak punya No.KTP — wajib untuk lookup IHS SatuSehat.");
            return;
        }
        String idPasienIhs = cekViaSatuSehat.tampilIDPasien(noKtp);
        if (idPasienIhs == null || idPasienIhs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pasien belum terdaftar di SatuSehat (IHS Patient ID kosong).");
            return;
        }

        String typeCode = EOC_TYPES[cmbType.getSelectedIndex()][0];
        String typeDisplay = EOC_TYPES[cmbType.getSelectedIndex()][1];
        String periodStart = Valid.SetTgl(DTPStart.getSelectedItem() + "");
        String condKd = nullIfEmpty(txtConditionKd.getText());
        String careNik = nullIfEmpty(txtCareManagerNik.getText());

        // Cek REUSE warning (UX)
        String existing = SatuSehatEpisodeOfCareHelper.findActiveEoc(noRkm, typeCode);
        if (!existing.isEmpty()) {
            int ok = JOptionPane.showConfirmDialog(this,
                "Sudah ada EOC active untuk pasien ini dengan tipe " + typeCode
              + " (ID: " + existing + ").\nLanjut? Helper akan REUSE EOC existing (tidak buat baru).",
                "EOC Sudah Ada", JOptionPane.YES_NO_OPTION);
            if (ok != JOptionPane.YES_OPTION) return;
        }

        String idEoc = SatuSehatEpisodeOfCareHelper.getOrCreate(
            noRkm, idPasienIhs, namaPasien,
            typeCode, typeDisplay,
            condKd, null,
            periodStart,
            careNik);

        if (idEoc == null || idEoc.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Gagal create/sync EOC ke SatuSehat. Cek log console (NetBeans Output) untuk detail.");
            return;
        }

        // Optional: link Encounter saat ini
        if (noRawat != null && !noRawat.isEmpty()) {
            SatuSehatEpisodeOfCareHelper.linkEncounter(noRawat, idEoc);
        }

        // Update keterangan kalau ada
        String ket = nullIfEmpty(txtKeterangan.getText());
        if (ket != null) {
            try (PreparedStatement ps = koneksi.prepareStatement(
                    "update satu_sehat_episodeofcare set keterangan=? where id_eoc=?")) {
                ps.setString(1, ket);
                ps.setString(2, idEoc);
                ps.executeUpdate();
            } catch (Exception ex) {
                System.out.println("Notif update keterangan EOC : " + ex);
            }
        }

        JOptionPane.showMessageDialog(this,
            "EOC tersimpan & ter-sync.\nID EOC: " + idEoc
          + (noRawat != null && !noRawat.isEmpty() ? "\nEncounter " + noRawat + " ter-link." : ""));
        tampilData();
        emptyFields();
    }

    /**
     * Update Status = PUT existing EOC. Wajib pilih row dulu.
     */
    private void updateStatus() {
        if (idEocSelected == null || idEocSelected.isEmpty() || txtIdEoc.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih dulu EOC di tabel.");
            return;
        }
        String newStatus = String.valueOf(cmbStatus.getSelectedItem());
        String periodEnd = null;
        if (chkPeriodEnd.isSelected()) {
            periodEnd = Valid.SetTgl(DTPEnd.getSelectedItem() + "");
        } else if ("finished".equals(newStatus) || "cancelled".equals(newStatus)) {
            // Auto-set period_end ke hari ini kalau status finished/cancelled tapi belum dicentang
            int ok = JOptionPane.showConfirmDialog(this,
                "Status " + newStatus + " tapi Tgl Selesai belum diisi.\nSet Tgl Selesai ke hari ini?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                periodEnd = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
            }
        }

        boolean ok = SatuSehatEpisodeOfCareHelper.updateStatus(idEocSelected, newStatus, periodEnd);
        if (!ok) {
            JOptionPane.showMessageDialog(this, "Gagal update status ke SatuSehat. Cek log console.");
            return;
        }
        JOptionPane.showMessageDialog(this, "Status EOC berhasil di-update ke: " + newStatus);
        tampilData();
        emptyFields();
    }

    /** Hapus lokal saja (FHIR EpisodeOfCare tidak support DELETE).
     *  Untuk soft-delete di SatuSehat, user harus update status ke entered-in-error dulu. */
    private void hapus() {
        if (idEocSelected == null || idEocSelected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih dulu EOC di tabel.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
            "Hapus EOC ini dari DB LOKAL saja?\n"
          + "EOC di SatuSehat TIDAK akan terhapus (FHIR tidak support DELETE).\n"
          + "Untuk soft-delete di SatuSehat, pakai status 'entered-in-error' lalu Update Status dulu.",
            "Konfirmasi Hapus Lokal", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            // Hapus link Encounter dulu (FK)
            try (PreparedStatement psLink = koneksi.prepareStatement(
                    "delete from satu_sehat_encounter_episodeofcare where id_eoc=?")) {
                psLink.setString(1, idEocSelected);
                psLink.executeUpdate();
            }
            try (PreparedStatement ps = koneksi.prepareStatement(
                    "delete from satu_sehat_episodeofcare where id_eoc=?")) {
                ps.setString(1, idEocSelected);
                ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "EOC dihapus dari DB lokal.");
            tampilData();
            emptyFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal hapus: " + ex.getMessage());
            System.out.println("Notif hapus EOC : " + ex);
        }
    }

    private String lookupNoKtpPasien(String noRm) {
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select no_ktp from pasien where no_rkm_medis=? limit 1")) {
            ps.setString(1, noRm);
            ResultSet rs = ps.executeQuery();
            String r = rs.next() ? rs.getString(1) : "";
            rs.close();
            return r == null ? "" : r;
        } catch (Exception ex) { return ""; }
    }

    private static String nullIfEmpty(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
