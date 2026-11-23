/*
 * Form Input Data KIPI (Kejadian Ikutan Pasca Imunisasi)
 * — terstruktur untuk FHIR Immunization.reaction SatuSehat.
 *
 * Dibuka dari DlgDataImunisasiPasien via klik kanan pada baris imunisasi
 * di tabel Riwayat Imunisasi → menu "Catat KIPI...". Caller wajib panggil
 * setContext() dulu untuk set imunisasi target.
 *
 * Menulis ke tabel imunisasi_pasien_kipi (lihat create_imunisasi_pasien_table.sql).
 *
 * Layout: pure programmatic Swing dengan widget custom Khanza.
 */
package simrskhanza;

import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
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
import javax.swing.table.DefaultTableModel;

public final class DlgDataKipiPasien extends javax.swing.JDialog {

    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();

    // Context imunisasi target (di-set caller via setContext)
    private String noRawat = "";
    private String tglPerawatan = "";
    private String jamPerawatan = "";
    private String kodeBrng = "";
    private String namaVaksin = "";
    private int    doseNumber = 0;
    private String namaPasien = "";
    private String noRkm = "";

    // ---- UI ----
    private widget.InternalFrame internalFrame1;
    private widget.Label  lblContext;
    private widget.Tanggal DTPTglReaksi;
    private widget.ComboBox cmbJam, cmbMnt, cmbDtk;
    private widget.ComboBox cmbSeverity;
    private widget.TextBox  txtManifestCode;
    private widget.ComboBox cmbManifestDisplay;   // editable combo (SNOMED auto-fill saat pilih)
    private widget.TextArea txtDescription;
    private javax.swing.JCheckBox chkReported;

    /** Daftar KIPI standar (SNOMED CT) yang paling sering muncul.
     *  Saat user pilih dari ComboBox → kode SNOMED auto-fill di txtManifestCode.
     *  ComboBox editable=true → bisa ketik manual untuk reaksi langka. */
    private static final String[][] KIPI_LIST = {
        {"", ""},  // index 0: kosong default
        // --- Reaksi ringan (mild) ---
        {"386661006", "Demam"},
        {"386617003", "Demam tinggi (>38.5°C)"},
        {"247441003", "Eritema/kemerahan di lokasi suntik"},
        {"42399005",  "Bengkak lokal di lokasi suntik"},
        {"162397003", "Nyeri di lokasi suntik"},
        {"126485001", "Urtikaria"},
        {"271807003", "Ruam kulit"},
        {"25064002",  "Sakit kepala"},
        {"422587007", "Mual"},
        {"422400008", "Muntah"},
        {"49727002",  "Batuk"},
        {"367391008", "Malaise/lemas"},
        // --- Reaksi sedang (moderate) ---
        {"40739000",  "Limfadenopati (kelenjar getah bening membengkak)"},
        {"300359004", "Sinkop/pingsan"},
        {"405729008", "Reaksi vasovagal"},
        {"2228002",   "Reaksi alergi pasca vaksin"},
        {"271737000", "Hipotonia (lemas otot)"},
        {"271594007", "Abses di lokasi suntik"},
        // --- Reaksi berat (severe) — WAJIB lapor KIPI nasional ---
        {"39579001",  "Anafilaksis"},
        {"91175000",  "Kejang (seizure)"},
        {"66857006",  "HHE (Hypotonic-Hyporesponsive Episode)"},
        {"19130008",  "Trombositopenia idiopatik pasca vaksin"},
        {"192763007", "Ensefalopati/ensefalitis"},
        {"66298002",  "Paralisis flaksid akut"},
        {"230745008", "Sindrom Guillain-Barré"},
        {"27624003",  "Abses serius"},
        {"419620001", "Kematian"},
        {"",          "Lainnya (ketik manual)"}
    };
    private widget.TextBox  KdPeg, TPegawai;
    private widget.Button   BtnSeekPetugas;
    private widget.Button   BtnSimpan, BtnBersihkan, BtnHapus, BtnTutup;
    private widget.Table    tbList;
    private DefaultTableModel tabMode;
    private kepegawaian.DlgCariPetugas dlgPetugas;

    public DlgDataKipiPasien(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle(":: Catat KIPI (Kejadian Ikutan Pasca Imunisasi) ::");
        // Full screen — sesuai ukuran layar
        java.awt.Dimension scr = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(scr.width, scr.height - 40);  // -40 untuk taskbar
        setLocation(0, 0);
        buildUI();
        emptyFields();
    }

    /** Caller wajib panggil setelah konstruktor — context imunisasi yang akan dicatat KIPI-nya. */
    public void setContext(String noRwt, String tglIm, String jamIm, String kdBrng,
                           String vaksinName, int dose, String namaPas, String noRm) {
        this.noRawat = noRwt;
        this.tglPerawatan = tglIm;
        this.jamPerawatan = jamIm;
        this.kodeBrng = kdBrng;
        this.namaVaksin = vaksinName;
        this.doseNumber = dose;
        this.namaPasien = namaPas;
        this.noRkm = noRm;
        lblContext.setText(
            "<html><b>Pasien:</b> " + namaPas + " (" + noRm + ")"
          + " &nbsp; | &nbsp; <b>Vaksin:</b> " + vaksinName
          + " &nbsp; | &nbsp; <b>Tgl Imunisasi:</b> " + tglIm + " " + jamIm
          + " &nbsp; | &nbsp; <b>Dosis ke-</b> " + dose + "</html>");
        tampilData();
    }

    // ============================================================
    // UI BUILD
    // ============================================================
    private void buildUI() {
        internalFrame1 = new widget.InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Catat KIPI ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11),
            new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(0, 4));
        internalFrame1.add(buildHeader(),    BorderLayout.NORTH);
        internalFrame1.add(buildInputForm(), BorderLayout.CENTER);

        widget.panelisi bottomWrap = new widget.panelisi();
        bottomWrap.setLayout(new BorderLayout(0, 2));
        bottomWrap.add(buildTableArea(),  BorderLayout.CENTER);
        bottomWrap.add(buildButtonBar(),  BorderLayout.SOUTH);
        internalFrame1.add(bottomWrap, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(internalFrame1, BorderLayout.CENTER);

        // Modal=true agar saat KIPI dialog modal, child dlgPetugas tetap bisa interact
        // (jika non-modal akan ter-block oleh modal parent).
        dlgPetugas = new kepegawaian.DlgCariPetugas(null, true);
        dlgPetugas.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (dlgPetugas.getTable().getSelectedRow() != -1) {
                    KdPeg.setText(dlgPetugas.getTable().getValueAt(dlgPetugas.getTable().getSelectedRow(), 0).toString());
                    TPegawai.setText(dlgPetugas.getTable().getValueAt(dlgPetugas.getTable().getSelectedRow(), 1).toString());
                }
            }
        });
    }

    private widget.panelisi buildHeader() {
        widget.panelisi p = new widget.panelisi();
        p.setLayout(null);
        p.setPreferredSize(new Dimension(800, 42));
        lblContext = new widget.Label();
        lblContext.setText("[ Context belum di-set — caller wajib panggil setContext() ]");
        lblContext.setFont(new Font("Tahoma", 0, 11));
        lblContext.setBounds(10, 6, 790, 32);
        lblContext.setBackground(new Color(255, 252, 220));
        lblContext.setOpaque(true);
        lblContext.setBorder(BorderFactory.createLineBorder(new Color(220, 200, 100)));
        p.add(lblContext);
        return p;
    }

    private widget.panelisi buildInputForm() {
        widget.panelisi p = new widget.panelisi();
        p.setLayout(null);
        p.setPreferredSize(new Dimension(800, 220));
        p.setBorder(BorderFactory.createTitledBorder(":: Input Data KIPI ::"));

        Font lblFont = new Font("Tahoma", 0, 11);
        // LX = posisi awal label; LW = lebar label; FX = posisi awal field
        int LX = 14, LW = 160, FX = 180, RH = 28;
        int y = 26;

        // Tgl Reaksi + Jam
        addLabel(p, "Tgl Reaksi :", LX, y, LW, 22, lblFont);
        DTPTglReaksi = new widget.Tanggal();
        DTPTglReaksi.setDisplayFormat("dd-MM-yyyy");
        DTPTglReaksi.setBounds(FX, y, 130, 23);
        p.add(DTPTglReaksi);

        addLabel(p, "Jam :", 325, y, 40, 22, lblFont);
        cmbJam = mkJamCombo(24); cmbJam.setBounds(370, y, 55, 23); p.add(cmbJam);
        cmbMnt = mkJamCombo(60); cmbMnt.setBounds(430, y, 55, 23); p.add(cmbMnt);
        cmbDtk = mkJamCombo(60); cmbDtk.setBounds(490, y, 55, 23); p.add(cmbDtk);
        y += RH;

        // Severity + Reported
        addLabel(p, "Tingkat (Severity) :", LX, y, LW, 22, lblFont);
        cmbSeverity = new widget.ComboBox();
        cmbSeverity.setModel(new DefaultComboBoxModel<>(new String[]{
            "mild (Ringan)", "moderate (Sedang)", "severe (Berat)"
        }));
        cmbSeverity.setBounds(FX, y, 200, 23);
        p.add(cmbSeverity);
        chkReported = new javax.swing.JCheckBox("Dilaporkan ke sistem KIPI nasional", true);
        chkReported.setOpaque(false);
        chkReported.setFont(lblFont);
        chkReported.setBounds(FX + 215, y, 320, 23);
        p.add(chkReported);
        y += RH;

        // Manifestasi (SNOMED code auto-fill + ComboBox editable)
        addLabel(p, "Manifestasi :", LX, y, LW, 22, lblFont);
        txtManifestCode = new widget.TextBox();
        txtManifestCode.setDocument(new batasInput((byte) 15).getKata(txtManifestCode));
        txtManifestCode.setToolTipText("SNOMED code reaksi — auto-fill saat pilih dari combo");
        txtManifestCode.setBounds(FX, y, 110, 23);
        p.add(txtManifestCode);
        // ComboBox editable dengan list KIPI standar SNOMED
        String[] kipiDisplays = new String[KIPI_LIST.length];
        for (int k = 0; k < KIPI_LIST.length; k++) kipiDisplays[k] = KIPI_LIST[k][1];
        cmbManifestDisplay = new widget.ComboBox();
        cmbManifestDisplay.setModel(new DefaultComboBoxModel<>(kipiDisplays));
        cmbManifestDisplay.setEditable(true);  // boleh ketik manual untuk reaksi langka
        cmbManifestDisplay.setToolTipText("Pilih dari list KIPI standar SNOMED, kode auto-fill di kiri. Atau ketik manual untuk reaksi langka.");
        cmbManifestDisplay.setBounds(FX + 115, y, 700, 23);
        // Auto-fill kode SNOMED saat user pilih dari list
        cmbManifestDisplay.addActionListener((java.awt.event.ActionEvent e) -> {
            int idx = cmbManifestDisplay.getSelectedIndex();
            if (idx >= 0 && idx < KIPI_LIST.length) {
                txtManifestCode.setText(KIPI_LIST[idx][0]);
            }
            // idx == -1 berarti user ketik manual → biarkan kode existing
        });
        p.add(cmbManifestDisplay);
        y += RH;

        // Petugas
        addLabel(p, "Petugas (NIP) :", LX, y, LW, 22, lblFont);
        KdPeg = new widget.TextBox();
        KdPeg.setDocument(new batasInput((byte) 20).getKata(KdPeg));
        KdPeg.setBounds(FX, y, 130, 23);
        p.add(KdPeg);
        TPegawai = new widget.TextBox();
        TPegawai.setEditable(false);
        TPegawai.setBounds(FX + 135, y, 640, 23);
        p.add(TPegawai);
        BtnSeekPetugas = new widget.Button();
        BtnSeekPetugas.setText("...");
        BtnSeekPetugas.setToolTipText("Cari petugas");
        BtnSeekPetugas.setBounds(FX + 778, y, 30, 23);
        BtnSeekPetugas.addActionListener(e -> cariPetugas());
        p.add(BtnSeekPetugas);
        y += RH;

        // Deskripsi
        addLabel(p, "Deskripsi :", LX, y, LW, 22, lblFont);
        txtDescription = new widget.TextArea();
        txtDescription.setRows(2);
        JScrollPane spDesc = new JScrollPane(txtDescription);
        spDesc.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        spDesc.setBounds(FX, y, 815, 46);
        p.add(spDesc);

        return p;
    }

    private widget.panelisi buildTableArea() {
        widget.panelisi wrap = new widget.panelisi();
        wrap.setLayout(new BorderLayout());
        wrap.setPreferredSize(new Dimension(800, 170));
        wrap.setBorder(BorderFactory.createTitledBorder(":: Riwayat KIPI untuk Imunisasi Ini ::"));

        tabMode = new DefaultTableModel(null, new String[]{
            "Tgl Reaksi","Severity","Kode SNOMED","Manifestasi","Deskripsi","Dilaporkan","Petugas"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbList = new widget.Table();
        tbList.setModel(tabMode);
        tbList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] kw = {130, 90, 110, 220, 200, 80, 130};
        for (int i = 0; i < kw.length; i++) tbList.getColumnModel().getColumn(i).setPreferredWidth(kw[i]);
        tbList.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedRowToForm();
        });

        widget.ScrollPane sp = new widget.ScrollPane();
        sp.setViewportView(tbList);
        wrap.add(sp, BorderLayout.CENTER);
        return wrap;
    }

    private widget.panelisi buildButtonBar() {
        widget.panelisi bar = new widget.panelisi();
        bar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 4));
        bar.setPreferredSize(new Dimension(800, 38));
        BtnSimpan    = mkButton("Simpan",    "Alt+S - Simpan KIPI",                   'S', "/picture/save-16x16.png",     e -> simpan());
        BtnBersihkan = mkButton("Bersihkan", "Alt+B - Reset form",                    'B', "/picture/Cancel-2-16x16.png", e -> emptyFields());
        BtnHapus     = mkButton("Hapus",     "Alt+H - Hapus KIPI terpilih dari tabel",'H', "/picture/stop_f2.png",        e -> hapus());
        BtnTutup     = mkButton("Tutup",     "Alt+T - Tutup form",                    'T', "/picture/exit.png",           e -> dispose());
        bar.add(BtnSimpan); bar.add(BtnBersihkan); bar.add(BtnHapus); bar.add(BtnTutup);
        return bar;
    }

    // ============================================================
    // HELPERS UI
    // ============================================================
    private void addLabel(widget.panelisi parent, String text, int x, int y, int w, int h, Font f) {
        widget.Label lbl = new widget.Label();
        lbl.setText(text);
        lbl.setFont(f);
        lbl.setBounds(x, y, w, h);
        parent.add(lbl);
    }

    private widget.ComboBox mkJamCombo(int max) {
        String[] items = new String[max];
        for (int k = 0; k < max; k++) items[k] = String.format("%02d", k);
        widget.ComboBox c = new widget.ComboBox();
        c.setModel(new DefaultComboBoxModel<>(items));
        return c;
    }

    private widget.Button mkButton(String label, String tip, char mnemonic, String iconPath, java.awt.event.ActionListener act) {
        widget.Button b = new widget.Button();
        b.setText(label);
        b.setToolTipText(tip);
        b.setMnemonic(mnemonic);
        b.setFont(new Font("Tahoma", 0, 11));
        if (iconPath != null) {
            try {
                b.setIcon(new javax.swing.ImageIcon(getClass().getResource(iconPath)));
            } catch (Exception ex) { /* ignore */ }
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

    // ============================================================
    // DATA OPS
    // ============================================================
    private void tampilData() {
        Valid.tabelKosong(tabMode);
        if (noRawat.isEmpty()) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select k.tgl_reaksi, k.severity, k.manifestation_code, k.manifestation_display, "
              + "  k.description, k.reported, ifnull(pg.nama,'') as petugas "
              + "from imunisasi_pasien_kipi k "
              + "left join pegawai pg on pg.nik = k.nip "
              + "where k.no_rawat=? and k.tgl_perawatan=? and k.jam=? and k.kode_brng=? "
              + "order by k.tgl_reaksi desc")) {
            ps.setString(1, noRawat);
            ps.setString(2, tglPerawatan);
            ps.setString(3, jamPerawatan);
            ps.setString(4, kodeBrng);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new Object[]{
                    rs.getString("tgl_reaksi"), rs.getString("severity"),
                    rs.getString("manifestation_code"), rs.getString("manifestation_display"),
                    rs.getString("description"),
                    rs.getInt("reported") == 1 ? "Ya" : "Tidak",
                    rs.getString("petugas")
                });
            }
            rs.close();
        } catch (Exception ex) {
            System.out.println("Notif tampilData KIPI : " + ex);
        }
    }

    private void loadSelectedRowToForm() {
        int r = tbList.getSelectedRow();
        if (r < 0) return;
        String tglRks = String.valueOf(tabMode.getValueAt(r, 0));
        if (tglRks != null && tglRks.length() >= 10) {
            Valid.SetTgl(DTPTglReaksi, tglRks.substring(0, 10));
            if (tglRks.length() >= 19) {
                cmbJam.setSelectedItem(tglRks.substring(11, 13));
                cmbMnt.setSelectedItem(tglRks.substring(14, 16));
                cmbDtk.setSelectedItem(tglRks.substring(17, 19));
            }
        }
        String sev = String.valueOf(tabMode.getValueAt(r, 1));
        for (int k = 0; k < cmbSeverity.getItemCount(); k++) {
            if (cmbSeverity.getItemAt(k).toString().startsWith(sev)) {
                cmbSeverity.setSelectedIndex(k); break;
            }
        }
        txtManifestCode.setText(nzs(tabMode.getValueAt(r, 2)));
        String manDisp = nzs(tabMode.getValueAt(r, 3));
        // Cari di list KIPI; kalau cocok set selected, kalau tidak set sebagai text editable
        boolean foundInList = false;
        for (int k = 0; k < KIPI_LIST.length; k++) {
            if (KIPI_LIST[k][1].equals(manDisp)) {
                cmbManifestDisplay.setSelectedIndex(k);
                foundInList = true;
                break;
            }
        }
        if (!foundInList) cmbManifestDisplay.setSelectedItem(manDisp);
        txtDescription.setText(nzs(tabMode.getValueAt(r, 4)));
        chkReported.setSelected("Ya".equals(String.valueOf(tabMode.getValueAt(r, 5))));
    }

    private void emptyFields() {
        java.util.Date now = new java.util.Date();
        Valid.SetTgl(DTPTglReaksi, new java.text.SimpleDateFormat("yyyy-MM-dd").format(now));
        String[] jamNow = new java.text.SimpleDateFormat("HH:mm:ss").format(now).split(":");
        cmbJam.setSelectedItem(jamNow[0]);
        cmbMnt.setSelectedItem(jamNow[1]);
        cmbDtk.setSelectedItem(jamNow[2]);
        cmbSeverity.setSelectedIndex(0);
        chkReported.setSelected(true);
        txtManifestCode.setText("");
        if (cmbManifestDisplay != null) cmbManifestDisplay.setSelectedIndex(0);
        txtDescription.setText("");
        // Auto-fill KdPeg HANYA kalau akses.getkode() valid NIK pegawai;
        // kalau bukan (mis. user "Admin Utama"), kosongkan supaya user wajib klik [...]
        String kd = akses.getkode();
        String nama = lookupNamaPegawai(kd);
        if (!nama.isEmpty()) {
            KdPeg.setText(kd);
            TPegawai.setText(nama);
        } else {
            KdPeg.setText("");
            TPegawai.setText("");
        }
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

    private void simpan() {
        if (noRawat.isEmpty() || kodeBrng.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Context imunisasi kosong. Tutup dialog & buka ulang dari klik kanan baris imunisasi.");
            return;
        }
        if (KdPeg.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Petugas (NIP) wajib diisi.");
            KdPeg.requestFocus();
            return;
        }
        String tglR = Valid.SetTgl(DTPTglReaksi.getSelectedItem() + "");
        String jamR = cmbJam.getSelectedItem() + ":" + cmbMnt.getSelectedItem() + ":" + cmbDtk.getSelectedItem();
        String tglJamR = tglR + " " + jamR;
        // Extract severity code dari "mild (Ringan)" → "mild"
        String sevSel = cmbSeverity.getSelectedItem().toString();
        String sevCode = sevSel.split(" ")[0];

        try (PreparedStatement ps = koneksi.prepareStatement(
                "insert into imunisasi_pasien_kipi (no_rawat,tgl_perawatan,jam,kode_brng,"
              + "tgl_reaksi,severity,manifestation_code,manifestation_display,description,reported,nip) "
              + "values (?,?,?,?,?,?,?,?,?,?,?) "
              + "on duplicate key update severity=values(severity), "
              + "manifestation_code=values(manifestation_code), manifestation_display=values(manifestation_display), "
              + "description=values(description), reported=values(reported), nip=values(nip), tgl_input=now()")) {
            ps.setString(1, noRawat);
            ps.setString(2, tglPerawatan);
            ps.setString(3, jamPerawatan);
            ps.setString(4, kodeBrng);
            ps.setString(5, tglJamR);
            ps.setString(6, sevCode);
            ps.setString(7, nullIfEmpty(txtManifestCode.getText()));
            Object manSel = cmbManifestDisplay.getSelectedItem();
            ps.setString(8, nullIfEmpty(manSel == null ? "" : manSel.toString()));
            ps.setString(9, nullIfEmpty(txtDescription.getText()));
            ps.setInt   (10, chkReported.isSelected() ? 1 : 0);
            ps.setString(11, KdPeg.getText().trim());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "KIPI tersimpan.");
            tampilData();
            txtManifestCode.setText("");
            cmbManifestDisplay.setSelectedIndex(0);
            txtDescription.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal simpan KIPI: " + ex.getMessage());
            System.out.println("Notif simpan KIPI : " + ex);
        }
    }

    private void hapus() {
        int r = tbList.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Pilih baris KIPI di tabel.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Hapus KIPI ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "delete from imunisasi_pasien_kipi where no_rawat=? and tgl_perawatan=? and jam=? "
              + "and kode_brng=? and tgl_reaksi=?")) {
            ps.setString(1, noRawat);
            ps.setString(2, tglPerawatan);
            ps.setString(3, jamPerawatan);
            ps.setString(4, kodeBrng);
            ps.setString(5, String.valueOf(tabMode.getValueAt(r, 0)));
            ps.executeUpdate();
            tampilData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal hapus KIPI: " + ex.getMessage());
        }
    }

    private static String nullIfEmpty(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
    private static String nzs(Object o) {
        if (o == null) return "";
        String s = o.toString();
        return "null".equals(s) ? "" : s;
    }
}
