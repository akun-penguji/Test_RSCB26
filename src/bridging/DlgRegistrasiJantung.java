/*
 * Form Registrasi Jantung per pasien (SatuSehat) — Fase 4.
 *
 * Hand-built tanpa .form (meniru DlgRegistrasiKanker): field-nya banyak dan panel
 * SKA/PJR disembunyikan dinamis menurut jalur, yang merepotkan bila lewat GUI designer.
 *
 * Registry LONGITUDINAL: satu kasus jantung per pasien (UNIQUE no_rkm_medis pada
 * tabel registrasi_jantung). Pasien yang datang lagi MEMAKAI ULANG registry yang
 * sama; status 'finished' hanya saat pasien meninggal.
 *
 * Nilai combo terminologi diambil dari tabel ref_registry_kode, bukan konstanta,
 * agar koreksi kode kemkes cukup lewat UPDATE tanpa rebuild.
 *
 * Hak akses menumpang satu_sehat_kirim_condition — modul ini TIDAK menambah kolom
 * permission baru di tabel `user` (keputusan 2026-07-19).
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

public final class DlgRegistrasiJantung extends javax.swing.JDialog {

    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();

    // Identitas kunjungan yang sedang dibuka
    private String noRawat = "", noRkm = "", namaPasien = "";
    private long idRegistrasi = 0;

    private widget.InternalFrame internalFrame1;
    private widget.Label lblPasien;

    // Diagnosis
    private widget.ComboBox cmbJalur;
    private widget.TextBox txtIcd10Kd, txtIcd10Nm;
    private widget.Tanggal DTPReg, DTPDiag;
    private widget.TextBox txtOnset;

    // Faktor risiko
    private widget.ComboBox cmbHtAda, cmbHtDerajat, cmbHtObat, cmbHtKontrol;
    private widget.ComboBox cmbDmAda, cmbDmObat, cmbDmKontrol;
    private widget.ComboBox cmbDlAda, cmbDlObat, cmbDlStatin;
    private widget.ComboBox cmbMerokok;

    // Target organ damage (checkbox dinamis dari ref_registry_kode)
    private JPanel pnlTod;
    private final Map<String, javax.swing.JCheckBox> chkTod = new LinkedHashMap<>();

    // SKA
    private JPanel pnlSka;
    private widget.TextBox txtGrace;
    private widget.ComboBox cmbGraceRisiko, cmbKillip;
    private widget.TextBox txtReperfusiKode;

    // PJR
    private JPanel pnlPjr;
    private widget.ComboBox cmbPjrKeteraturan, cmbPjrObat;

    // Registry
    private widget.ComboBox cmbStatus, cmbDasarAktivasi, cmbVital;
    private widget.TextBox txtCmNik, txtCmNama, txtSebab;
    private widget.Tanggal DTPMeninggal;

    // Tindakan
    private widget.Table tbTindakan;
    private DefaultTableModel tabTindakan;
    private widget.ComboBox cmbTKategori;
    private widget.TextBox txtTKode, txtTDesk;

    private widget.Button BtnSimpan, BtnKirim, BtnHapus, BtnTutup;

    private static final String[] JALUR   = {"hipertensi", "iskemik_stabil", "ska_ste", "ska_nste", "pjr"};
    private static final String[] STATUS  = {"waitlist", "active", "finished"};
    private static final String[] DASAR   = {"", "riwayat_ima", "pencitraan", "patologi", "ska_aktif", "lainnya"};
    private static final String[] VITAL   = {"hidup", "meninggal", "tidak diketahui"};
    private static final String[] YATIDAK = {"", "Ya", "Tidak"};
    private static final String[] KATEGORI_TINDAKAN = {"diagnostik", "reperfusi", "invasif_pjr", "iskemik_stabil"};

    public DlgRegistrasiJantung(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle(":: Registrasi Jantung (SatuSehat) ::");
        java.awt.Dimension scr = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(Math.min(1180, scr.width - 40), Math.min(760, scr.height - 60));
        setLocationRelativeTo(null);
        buildUI();
    }

    public void isCek() {
        boolean boleh = fungsi.akses.getsatu_sehat_kirim_condition();
        BtnSimpan.setEnabled(boleh);
        BtnKirim.setEnabled(boleh);
        BtnHapus.setEnabled(boleh);
    }

    /** Dipanggil dari worklist/registrasi untuk mengisi konteks pasien. */
    public void setPasien(String noRawat, String noRkm, String nama) {
        this.noRawat = nz(noRawat);
        this.noRkm = nz(noRkm);
        this.namaPasien = nz(nama);
        lblPasien.setText("  " + this.namaPasien + "   |   No.RM: " + this.noRkm + "   |   No.Rawat: " + this.noRawat);
        prefillIcd10();
        muatRegistrasi();
    }

    // ==================================================================== UI

    private void buildUI() {
        internalFrame1 = new widget.InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(240, 245, 235)),
                "::[ Registrasi Jantung Satu Sehat ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(0, 3));

        lblPasien = new widget.Label();
        lblPasien.setText("  (belum ada pasien dipilih)");
        lblPasien.setFont(new Font("Tahoma", 1, 12));
        lblPasien.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
        internalFrame1.add(lblPasien, BorderLayout.NORTH);

        JPanel isi = new JPanel();
        isi.setLayout(null);
        isi.setPreferredSize(new Dimension(1120, 900));
        Font f = new Font("Tahoma", 0, 11);
        int LX = 12, FX = 150, RX = 580, FX2 = 720;
        int y = 10;

        // ---------- Diagnosis ----------
        judul(isi, "Diagnosis & Jalur Registry", LX, y); y += 24;
        addLabel(isi, "Jalur :", LX, y, 130, 22, f);
        cmbJalur = mkCombo(JALUR); cmbJalur.setBounds(FX, y, 160, 23);
        cmbJalur.addActionListener(e -> perbaruiPanelJalur()); isi.add(cmbJalur);
        addLabel(isi, "Status Registry :", RX, y, 130, 22, f);
        cmbStatus = mkCombo(STATUS); cmbStatus.setBounds(FX2, y, 140, 23); isi.add(cmbStatus);
        y += 28;

        addLabel(isi, "Diagnosa ICD-10 :", LX, y, 130, 22, f);
        txtIcd10Kd = new widget.TextBox(); txtIcd10Kd.setBounds(FX, y, 80, 23); isi.add(txtIcd10Kd);
        txtIcd10Nm = new widget.TextBox(); txtIcd10Nm.setBounds(FX + 85, y, 340, 23);
        txtIcd10Nm.setEditable(false); isi.add(txtIcd10Nm);
        addLabel(isi, "Dasar Aktivasi :", RX, y, 130, 22, f);
        cmbDasarAktivasi = mkCombo(DASAR); cmbDasarAktivasi.setBounds(FX2, y, 160, 23); isi.add(cmbDasarAktivasi);
        y += 28;

        addLabel(isi, "Tgl Registrasi :", LX, y, 130, 22, f);
        DTPReg = mkTgl(); DTPReg.setBounds(FX, y, 120, 23); isi.add(DTPReg);
        addLabel(isi, "Tgl Diagnosis :", RX, y, 130, 22, f);
        DTPDiag = mkTgl(); DTPDiag.setBounds(FX2, y, 120, 23); isi.add(DTPDiag);
        y += 28;

        addLabel(isi, "Onset Keluhan :", LX, y, 130, 22, f);
        txtOnset = new widget.TextBox(); txtOnset.setBounds(FX, y, 170, 23); isi.add(txtOnset);
        addLabel(isi, "(yyyy-MM-dd HH:mm — WAJIB untuk jalur SKA)", FX + 178, y, 380, 22, f);
        y += 34;

        // ---------- Faktor risiko ----------
        judul(isi, "Faktor Risiko", LX, y); y += 24;
        addLabel(isi, "Hipertensi :", LX, y, 130, 22, f);
        cmbHtAda = mkCombo(YATIDAK); cmbHtAda.setBounds(FX, y, 90, 23); isi.add(cmbHtAda);
        addLabel(isi, "Derajat :", FX + 100, y, 60, 22, f);
        cmbHtDerajat = mkComboRef("ht_derajat_obs"); cmbHtDerajat.setBounds(FX + 165, y, 190, 23); isi.add(cmbHtDerajat);
        addLabel(isi, "Dapat obat :", RX, y, 80, 22, f);
        cmbHtObat = mkCombo(YATIDAK); cmbHtObat.setBounds(RX + 85, y, 80, 23); isi.add(cmbHtObat);
        addLabel(isi, "Terkontrol :", RX + 175, y, 80, 22, f);
        cmbHtKontrol = mkCombo(YATIDAK); cmbHtKontrol.setBounds(RX + 260, y, 80, 23); isi.add(cmbHtKontrol);
        y += 28;

        addLabel(isi, "Diabetes Mellitus :", LX, y, 130, 22, f);
        cmbDmAda = mkCombo(YATIDAK); cmbDmAda.setBounds(FX, y, 90, 23); isi.add(cmbDmAda);
        addLabel(isi, "Dapat obat :", RX, y, 80, 22, f);
        cmbDmObat = mkCombo(YATIDAK); cmbDmObat.setBounds(RX + 85, y, 80, 23); isi.add(cmbDmObat);
        addLabel(isi, "Terkontrol :", RX + 175, y, 80, 22, f);
        cmbDmKontrol = mkCombo(YATIDAK); cmbDmKontrol.setBounds(RX + 260, y, 80, 23); isi.add(cmbDmKontrol);
        y += 28;

        addLabel(isi, "Dislipidemia :", LX, y, 130, 22, f);
        cmbDlAda = mkCombo(YATIDAK); cmbDlAda.setBounds(FX, y, 90, 23); isi.add(cmbDlAda);
        addLabel(isi, "Statin :", FX + 100, y, 60, 22, f);
        cmbDlStatin = mkComboRef("statin_intensitas"); cmbDlStatin.setBounds(FX + 165, y, 190, 23); isi.add(cmbDlStatin);
        addLabel(isi, "Dapat obat :", RX, y, 80, 22, f);
        cmbDlObat = mkCombo(YATIDAK); cmbDlObat.setBounds(RX + 85, y, 80, 23); isi.add(cmbDlObat);
        y += 28;

        addLabel(isi, "Status Merokok :", LX, y, 130, 22, f);
        cmbMerokok = mkComboRef("merokok"); cmbMerokok.setBounds(FX, y, 190, 23); isi.add(cmbMerokok);
        y += 32;

        // ---------- Target organ damage ----------
        judul(isi, "Target Organ Damage", LX, y); y += 24;
        pnlTod = new JPanel();
        pnlTod.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 2));
        pnlTod.setBounds(LX, y, 1080, 30);
        pnlTod.setOpaque(false);
        for (Ref rf : muatRef("jantung_tod")) {
            javax.swing.JCheckBox cb = new javax.swing.JCheckBox(rf.display);
            cb.setFont(f); cb.setOpaque(false);
            chkTod.put(rf.kode, cb);
            pnlTod.add(cb);
        }
        isi.add(pnlTod);
        y += 36;

        // ---------- SKA ----------
        pnlSka = seksi(isi, "Sindrom Koroner Akut (GRACE & Killip)", LX, y, 1080, 62); y += 24;
        addLabel(isi, "GRACE Score :", LX, y, 130, 22, f);
        txtGrace = new widget.TextBox(); txtGrace.setBounds(FX, y, 80, 23); isi.add(txtGrace);
        addLabel(isi, "Risiko :", FX + 90, y, 50, 22, f);
        cmbGraceRisiko = mkComboRef("jantung_grace_risiko"); cmbGraceRisiko.setBounds(FX + 145, y, 160, 23); isi.add(cmbGraceRisiko);
        addLabel(isi, "Killip :", RX, y, 60, 22, f);
        cmbKillip = mkComboRef("jantung_killip"); cmbKillip.setBounds(RX + 65, y, 260, 23); isi.add(cmbKillip);
        y += 28;
        addLabel(isi, "Kode Reperfusi :", LX, y, 130, 22, f);
        txtReperfusiKode = new widget.TextBox(); txtReperfusiKode.setBounds(FX, y, 100, 23); isi.add(txtReperfusiKode);
        addLabel(isi, "(ICD-9-CM)", FX + 108, y, 120, 22, f);
        y += 34;

        // ---------- PJR ----------
        pnlPjr = seksi(isi, "Pencegahan Sekunder Penyakit Jantung Rematik", LX, y, 1080, 34); y += 24;
        addLabel(isi, "Keteraturan :", LX, y, 130, 22, f);
        cmbPjrKeteraturan = mkComboRef("jantung_pjr_keteraturan"); cmbPjrKeteraturan.setBounds(FX, y, 220, 23); isi.add(cmbPjrKeteraturan);
        addLabel(isi, "Jenis Obat :", RX, y, 90, 22, f);
        cmbPjrObat = mkComboRef("jantung_pjr_obat"); cmbPjrObat.setBounds(FX2, y, 300, 23); isi.add(cmbPjrObat);
        y += 34;

        // ---------- Registry & outcome ----------
        judul(isi, "Care Manager & Outcome", LX, y); y += 24;
        addLabel(isi, "Care Manager NIK :", LX, y, 130, 22, f);
        txtCmNik = new widget.TextBox(); txtCmNik.setBounds(FX, y, 120, 23); isi.add(txtCmNik);
        txtCmNama = new widget.TextBox(); txtCmNama.setBounds(FX + 128, y, 280, 23);
        txtCmNama.setEditable(false); isi.add(txtCmNama);
        widget.Button bCm = new widget.Button(); bCm.setText("?"); bCm.setBounds(FX + 412, y, 30, 23);
        bCm.addActionListener(e -> cariCareManager()); isi.add(bCm);
        addLabel(isi, "Status Vital :", RX, y, 90, 22, f);
        cmbVital = mkCombo(VITAL); cmbVital.setBounds(FX2, y, 150, 23); isi.add(cmbVital);
        y += 28;
        addLabel(isi, "Tgl Meninggal :", LX, y, 130, 22, f);
        DTPMeninggal = mkTgl(); DTPMeninggal.setBounds(FX, y, 120, 23); isi.add(DTPMeninggal);
        addLabel(isi, "Sebab :", RX, y, 60, 22, f);
        txtSebab = new widget.TextBox(); txtSebab.setBounds(FX2, y, 300, 23); isi.add(txtSebab);
        y += 34;

        // ---------- Tindakan ----------
        judul(isi, "Tindakan (Procedure ICD-9-CM)", LX, y); y += 24;
        addLabel(isi, "Kategori :", LX, y, 70, 22, f);
        cmbTKategori = mkCombo(KATEGORI_TINDAKAN); cmbTKategori.setBounds(LX + 75, y, 140, 23); isi.add(cmbTKategori);
        addLabel(isi, "Kode :", LX + 225, y, 45, 22, f);
        txtTKode = new widget.TextBox(); txtTKode.setBounds(LX + 272, y, 90, 23); isi.add(txtTKode);
        addLabel(isi, "Deskripsi :", LX + 370, y, 70, 22, f);
        txtTDesk = new widget.TextBox(); txtTDesk.setBounds(LX + 442, y, 380, 23); isi.add(txtTDesk);
        widget.Button bTambah = new widget.Button(); bTambah.setText("Tambah"); bTambah.setBounds(LX + 830, y, 90, 23);
        bTambah.addActionListener(e -> tambahTindakan()); isi.add(bTambah);
        widget.Button bHapusT = new widget.Button(); bHapusT.setText("Hapus"); bHapusT.setBounds(LX + 926, y, 90, 23);
        bHapusT.addActionListener(e -> hapusTindakan()); isi.add(bHapusT);
        y += 28;

        tabTindakan = new DefaultTableModel(null, new String[]{"Kategori", "Kode ICD-9", "Deskripsi", "Tanggal", "id"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbTindakan = new widget.Table();
        tbTindakan.setModel(tabTindakan);
        tbTindakan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] wT = {130, 100, 500, 140, 0};
        for (int i = 0; i < wT.length; i++) tbTindakan.getColumnModel().getColumn(i).setPreferredWidth(wT[i]);
        tbTindakan.setDefaultRenderer(Object.class, new WarnaTable());
        widget.ScrollPane scT = new widget.ScrollPane();
        scT.setViewportView(tbTindakan);
        scT.setBounds(LX, y, 1080, 110);
        isi.add(scT);
        y += 120;

        isi.setPreferredSize(new Dimension(1120, y + 20));
        JScrollPane scroll = new JScrollPane(isi);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        internalFrame1.add(scroll, BorderLayout.CENTER);

        // ---------- Tombol ----------
        widget.panelisi btns = new widget.panelisi();
        btns.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 6, 6));
        BtnSimpan = mkButton("Simpan", 'S', 120, e -> simpan());
        BtnKirim  = mkButton("Kirim ke SatuSehat", 'K', 170, e -> kirim());
        BtnHapus  = mkButton("Hapus Registrasi", 'H', 150, e -> hapusRegistrasi());
        BtnTutup  = mkButton("Tutup", 'T', 100, e -> dispose());
        btns.add(BtnSimpan); btns.add(BtnKirim); btns.add(BtnHapus); btns.add(BtnTutup);
        internalFrame1.add(btns, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(internalFrame1, BorderLayout.CENTER);

        perbaruiPanelJalur();
    }

    /** Panel SKA/PJR hanya aktif bila jalurnya relevan, agar form ringan untuk RS
     *  yang hanya menangani hipertensi. */
    private void perbaruiPanelJalur() {
        String jalur = String.valueOf(cmbJalur.getSelectedItem());
        boolean ska = jalur.startsWith("ska_");
        boolean pjr = "pjr".equals(jalur);
        aktifkan(ska, txtGrace, cmbGraceRisiko, cmbKillip, txtReperfusiKode);
        aktifkan(pjr, cmbPjrKeteraturan, cmbPjrObat);
        if (pnlSka != null) pnlSka.setVisible(true);
        if (pnlPjr != null) pnlPjr.setVisible(true);
    }

    private void aktifkan(boolean on, javax.swing.JComponent... comps) {
        for (javax.swing.JComponent c : comps) if (c != null) c.setEnabled(on);
    }

    // ==================================================================== Referensi terminologi

    private static final class Ref { String kode = "", display = ""; }

    /** Muat pilihan combo dari ref_registry_kode — bukan konstanta Java. */
    private List<Ref> muatRef(String kelompok) {
        List<Ref> out = new ArrayList<>();
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select kode, display from ref_registry_kode where kelompok=? and aktif=1 order by urut, kode")) {
            ps.setString(1, kelompok);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ref r = new Ref();
                    r.kode = nz(rs.getString("kode"));
                    r.display = nz(rs.getString("display"));
                    out.add(r);
                }
            }
        } catch (Exception e) {
            System.out.println("Notif muatRef " + kelompok + " : " + e);
        }
        return out;
    }

    /** Combo yang menampilkan display tapi menyimpan kode lokal. */
    private widget.ComboBox mkComboRef(String kelompok) {
        List<Ref> refs = muatRef(kelompok);
        List<String> tampil = new ArrayList<>();
        List<String> kode = new ArrayList<>();
        tampil.add(""); kode.add("");
        for (Ref r : refs) { tampil.add(r.display); kode.add(r.kode); }
        widget.ComboBox c = new widget.ComboBox();
        c.setModel(new DefaultComboBoxModel<>(tampil.toArray(new String[0])));
        c.putClientProperty("kodeLokal", kode);
        return c;
    }

    @SuppressWarnings("unchecked")
    private String kodeDari(widget.ComboBox c) {
        Object prop = c.getClientProperty("kodeLokal");
        int i = c.getSelectedIndex();
        if (!(prop instanceof List) || i < 0) return "";
        List<String> kode = (List<String>) prop;
        return i < kode.size() ? kode.get(i) : "";
    }

    @SuppressWarnings("unchecked")
    private void pilihKode(widget.ComboBox c, String kodeLokal) {
        Object prop = c.getClientProperty("kodeLokal");
        if (!(prop instanceof List)) return;
        List<String> kode = (List<String>) prop;
        int idx = kode.indexOf(nz(kodeLokal));
        c.setSelectedIndex(idx >= 0 ? idx : 0);
    }

    // ==================================================================== Muat & simpan

    private void prefillIcd10() {
        // Diagnosa kardiovaskular pertama pada kunjungan ini, kalau ada.
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select dp.kd_penyakit, ifnull(pk.nm_penyakit,'') nm from diagnosa_pasien dp "
              + "left join penyakit pk on pk.kd_penyakit=dp.kd_penyakit "
              + "where dp.no_rawat=? and dp.kd_penyakit regexp '^I(0[5-9]|1[0-5]|2[0-5]|50)' "
              + "order by dp.prioritas limit 1")) {
            ps.setString(1, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    txtIcd10Kd.setText(nz(rs.getString("kd_penyakit")));
                    txtIcd10Nm.setText(nz(rs.getString("nm")));
                }
            }
        } catch (Exception e) {
            System.out.println("Notif prefillIcd10 : " + e);
        }
    }

    private void muatRegistrasi() {
        idRegistrasi = 0;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select * from registrasi_jantung where no_rkm_medis=? limit 1")) {
            ps.setString(1, noRkm);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idRegistrasi = rs.getLong("id_registrasi");
                    pilihNilai(cmbJalur, nz(rs.getString("jalur")));
                    pilihNilai(cmbStatus, nz(rs.getString("status")));
                    pilihNilai(cmbDasarAktivasi, nz(rs.getString("dasar_aktivasi")));
                    pilihNilai(cmbVital, nz(rs.getString("status_vital")));
                    if (!nz(rs.getString("kd_penyakit")).isEmpty()) txtIcd10Kd.setText(rs.getString("kd_penyakit"));
                    txtOnset.setText(nz(rs.getString("onset_keluhan")));
                    txtCmNik.setText(nz(rs.getString("kd_dokter")));
                    txtSebab.setText(nz(rs.getString("sebab_kematian")));
                    setTgl(DTPReg, nz(rs.getString("tgl_registrasi")));
                    setTgl(DTPDiag, nz(rs.getString("tgl_diagnosis")));
                    setTgl(DTPMeninggal, nz(rs.getString("tgl_meninggal")));
                    isiNamaCareManager();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif muatRegistrasi : " + e);
        }
        perbaruiPanelJalur();
        muatFaktor();
        muatTod();
        muatSka();
        muatPjr();
        muatTindakan();
    }

    private void muatFaktor() {
        if (idRegistrasi == 0) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select * from registrasi_jantung_faktor_risiko where id_registrasi=? and no_rawat=? "
              + "order by tanggal desc limit 1")) {
            ps.setLong(1, idRegistrasi);
            ps.setString(2, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    setYaTidak(cmbHtAda, rs, "ht_ada");
                    pilihKode(cmbHtDerajat, nz(rs.getString("ht_derajat")));
                    setYaTidak(cmbHtObat, rs, "ht_dapat_obat");
                    setYaTidak(cmbHtKontrol, rs, "ht_terkontrol");
                    setYaTidak(cmbDmAda, rs, "dm_ada");
                    setYaTidak(cmbDmObat, rs, "dm_dapat_obat");
                    setYaTidak(cmbDmKontrol, rs, "dm_terkontrol");
                    setYaTidak(cmbDlAda, rs, "dl_ada");
                    setYaTidak(cmbDlObat, rs, "dl_dapat_obat");
                    pilihKode(cmbDlStatin, nz(rs.getString("dl_statin")));
                    pilihKode(cmbMerokok, nz(rs.getString("merokok")));
                }
            }
        } catch (Exception e) { System.out.println("Notif muatFaktor : " + e); }
    }

    private void muatTod() {
        for (javax.swing.JCheckBox cb : chkTod.values()) cb.setSelected(false);
        if (idRegistrasi == 0) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select kode from registrasi_jantung_tod where id_registrasi=? and no_rawat=?")) {
            ps.setLong(1, idRegistrasi);
            ps.setString(2, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    javax.swing.JCheckBox cb = chkTod.get(nz(rs.getString("kode")));
                    if (cb != null) cb.setSelected(true);
                }
            }
        } catch (Exception e) { System.out.println("Notif muatTod : " + e); }
    }

    private void muatSka() {
        if (idRegistrasi == 0) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select * from registrasi_jantung_ska where id_registrasi=? and no_rawat=? order by tanggal desc limit 1")) {
            ps.setLong(1, idRegistrasi);
            ps.setString(2, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    txtGrace.setText(nz(rs.getString("grace_skor")));
                    pilihKode(cmbGraceRisiko, nz(rs.getString("grace_risiko")));
                    pilihKode(cmbKillip, nz(rs.getString("killip")));
                    txtReperfusiKode.setText(nz(rs.getString("reperfusi_kode")));
                }
            }
        } catch (Exception e) { System.out.println("Notif muatSka : " + e); }
    }

    private void muatPjr() {
        if (idRegistrasi == 0) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select * from registrasi_jantung_pjr where id_registrasi=? and no_rawat=? order by tanggal desc limit 1")) {
            ps.setLong(1, idRegistrasi);
            ps.setString(2, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pilihKode(cmbPjrKeteraturan, nz(rs.getString("keteraturan")));
                    pilihKode(cmbPjrObat, nz(rs.getString("jenis_obat")));
                }
            }
        } catch (Exception e) { System.out.println("Notif muatPjr : " + e); }
    }

    private void muatTindakan() {
        Valid.tabelKosong(tabTindakan);
        if (idRegistrasi == 0) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select id_tindakan, kategori, kode_icd9, ifnull(deskripsi,'') deskripsi, tanggal "
              + "from registrasi_jantung_tindakan where id_registrasi=? order by id_tindakan")) {
            ps.setLong(1, idRegistrasi);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabTindakan.addRow(new Object[]{
                        rs.getString("kategori"), rs.getString("kode_icd9"),
                        rs.getString("deskripsi"), rs.getString("tanggal"), rs.getString("id_tindakan")
                    });
                }
            }
        } catch (Exception e) { System.out.println("Notif muatTindakan : " + e); }
    }

    private void simpan() {
        if (noRkm.isEmpty() || noRawat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pasien belum dipilih.");
            return;
        }
        String jalur = String.valueOf(cmbJalur.getSelectedItem());
        String onset = txtOnset.getText().trim();
        if (jalur.startsWith("ska_") && onset.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Onset Keluhan wajib diisi untuk jalur SKA.\n"
                  + "Playbook SatuSehat memakainya sebagai Condition.onsetDateTime.");
            return;
        }
        try {
            // ---- Master (UNIQUE no_rkm_medis: registry longitudinal, satu per pasien) ----
            try (PreparedStatement ps = koneksi.prepareStatement(
                    "insert into registrasi_jantung (no_rkm_medis,no_rawat,tgl_registrasi,jalur,kd_penyakit,"
                  + "tgl_diagnosis,onset_keluhan,status,dasar_aktivasi,kd_dokter,status_vital,tgl_meninggal,sebab_kematian) "
                  + "values (?,?,?,?,?,?,?,?,?,?,?,?,?) "
                  + "on duplicate key update no_rawat=values(no_rawat), jalur=values(jalur), "
                  + "kd_penyakit=values(kd_penyakit), tgl_diagnosis=values(tgl_diagnosis), "
                  + "onset_keluhan=values(onset_keluhan), status=values(status), "
                  + "dasar_aktivasi=values(dasar_aktivasi), kd_dokter=values(kd_dokter), "
                  + "status_vital=values(status_vital), tgl_meninggal=values(tgl_meninggal), "
                  + "sebab_kematian=values(sebab_kematian)")) {
                int i = 1;
                ps.setString(i++, noRkm);
                ps.setString(i++, noRawat);
                ps.setString(i++, Valid.SetTgl(DTPReg.getSelectedItem() + ""));
                ps.setString(i++, jalur);
                setStrNull(ps, i++, txtIcd10Kd.getText().trim());
                setStrNull(ps, i++, Valid.SetTgl(DTPDiag.getSelectedItem() + ""));
                setStrNull(ps, i++, onset);
                ps.setString(i++, String.valueOf(cmbStatus.getSelectedItem()));
                setStrNull(ps, i++, String.valueOf(cmbDasarAktivasi.getSelectedItem()));
                setStrNull(ps, i++, txtCmNik.getText().trim());
                ps.setString(i++, String.valueOf(cmbVital.getSelectedItem()));
                setStrNull(ps, i++, "meninggal".equals(String.valueOf(cmbVital.getSelectedItem()))
                        ? Valid.SetTgl(DTPMeninggal.getSelectedItem() + "") : "");
                setStrNull(ps, i++, txtSebab.getText().trim());
                ps.executeUpdate();
            }
            if (idRegistrasi == 0) {
                idRegistrasi = Long.parseLong(scalar("select id_registrasi from registrasi_jantung where no_rkm_medis=?", noRkm, "0"));
            }

            simpanFaktor();
            simpanTod();
            simpanSka();
            simpanPjr();

            JOptionPane.showMessageDialog(this, "Registrasi jantung tersimpan (id " + idRegistrasi + ").");
            muatTindakan();
        } catch (Exception e) {
            System.out.println("Notif simpan registrasi jantung : " + e);
            JOptionPane.showMessageDialog(this, "Gagal menyimpan:\n" + e.getMessage());
        }
    }

    private void simpanFaktor() throws Exception {
        // Satu baris per kunjungan: hapus lalu tulis ulang agar tidak menumpuk.
        try (PreparedStatement d = koneksi.prepareStatement(
                "delete from registrasi_jantung_faktor_risiko where id_registrasi=? and no_rawat=?")) {
            d.setLong(1, idRegistrasi); d.setString(2, noRawat); d.executeUpdate();
        }
        try (PreparedStatement ps = koneksi.prepareStatement(
                "insert into registrasi_jantung_faktor_risiko (id_registrasi,no_rawat,tanggal,"
              + "ht_ada,ht_derajat,ht_dapat_obat,ht_terkontrol,dm_ada,dm_dapat_obat,dm_terkontrol,"
              + "dl_ada,dl_dapat_obat,dl_statin,merokok,kd_dokter) values (?,?,now(),?,?,?,?,?,?,?,?,?,?,?,?)")) {
            int i = 1;
            ps.setLong(i++, idRegistrasi);
            ps.setString(i++, noRawat);
            setBool(ps, i++, cmbHtAda);
            setStrNull(ps, i++, kodeDari(cmbHtDerajat));
            setBool(ps, i++, cmbHtObat);
            setBool(ps, i++, cmbHtKontrol);
            setBool(ps, i++, cmbDmAda);
            setBool(ps, i++, cmbDmObat);
            setBool(ps, i++, cmbDmKontrol);
            setBool(ps, i++, cmbDlAda);
            setBool(ps, i++, cmbDlObat);
            setStrNull(ps, i++, kodeDari(cmbDlStatin));
            setStrNull(ps, i++, kodeDari(cmbMerokok));
            setStrNull(ps, i++, txtCmNik.getText().trim());
            ps.executeUpdate();
        }
    }

    private void simpanTod() throws Exception {
        try (PreparedStatement d = koneksi.prepareStatement(
                "delete from registrasi_jantung_tod where id_registrasi=? and no_rawat=?")) {
            d.setLong(1, idRegistrasi); d.setString(2, noRawat); d.executeUpdate();
        }
        for (Map.Entry<String, javax.swing.JCheckBox> e : chkTod.entrySet()) {
            if (!e.getValue().isSelected()) continue;
            try (PreparedStatement ps = koneksi.prepareStatement(
                    "insert ignore into registrasi_jantung_tod (id_registrasi,no_rawat,kode) values (?,?,?)")) {
                ps.setLong(1, idRegistrasi); ps.setString(2, noRawat); ps.setString(3, e.getKey());
                ps.executeUpdate();
            }
        }
    }

    private void simpanSka() throws Exception {
        String skor = txtGrace.getText().trim();
        String killip = kodeDari(cmbKillip);
        if (skor.isEmpty() && killip.isEmpty() && txtReperfusiKode.getText().trim().isEmpty()) return;
        try (PreparedStatement d = koneksi.prepareStatement(
                "delete from registrasi_jantung_ska where id_registrasi=? and no_rawat=?")) {
            d.setLong(1, idRegistrasi); d.setString(2, noRawat); d.executeUpdate();
        }
        try (PreparedStatement ps = koneksi.prepareStatement(
                "insert into registrasi_jantung_ska (id_registrasi,no_rawat,tanggal,grace_skor,grace_risiko,"
              + "killip,reperfusi_kode,kd_dokter) values (?,?,now(),?,?,?,?,?)")) {
            int i = 1;
            ps.setLong(i++, idRegistrasi);
            ps.setString(i++, noRawat);
            if (skor.isEmpty()) ps.setNull(i++, java.sql.Types.INTEGER);
            else ps.setInt(i++, Integer.parseInt(skor));
            setStrNull(ps, i++, kodeDari(cmbGraceRisiko));
            setStrNull(ps, i++, killip);
            setStrNull(ps, i++, txtReperfusiKode.getText().trim());
            setStrNull(ps, i++, txtCmNik.getText().trim());
            ps.executeUpdate();
        }
    }

    private void simpanPjr() throws Exception {
        String ket = kodeDari(cmbPjrKeteraturan);
        if (ket.isEmpty()) return;
        try (PreparedStatement d = koneksi.prepareStatement(
                "delete from registrasi_jantung_pjr where id_registrasi=? and no_rawat=?")) {
            d.setLong(1, idRegistrasi); d.setString(2, noRawat); d.executeUpdate();
        }
        try (PreparedStatement ps = koneksi.prepareStatement(
                "insert into registrasi_jantung_pjr (id_registrasi,no_rawat,tanggal,keteraturan,jenis_obat,kd_dokter) "
              + "values (?,?,now(),?,?,?)")) {
            int i = 1;
            ps.setLong(i++, idRegistrasi);
            ps.setString(i++, noRawat);
            ps.setString(i++, ket);
            setStrNull(ps, i++, kodeDari(cmbPjrObat));
            setStrNull(ps, i++, txtCmNik.getText().trim());
            ps.executeUpdate();
        }
    }

    private void tambahTindakan() {
        if (idRegistrasi == 0) { JOptionPane.showMessageDialog(this, "Simpan registrasi dulu."); return; }
        String kode = txtTKode.getText().trim();
        if (kode.isEmpty()) { JOptionPane.showMessageDialog(this, "Kode ICD-9-CM belum diisi."); return; }
        try (PreparedStatement ps = koneksi.prepareStatement(
                "insert into registrasi_jantung_tindakan (id_registrasi,no_rawat,kategori,kode_icd9,deskripsi,tanggal,kd_dokter) "
              + "values (?,?,?,?,?,now(),?)")) {
            ps.setLong(1, idRegistrasi);
            ps.setString(2, noRawat);
            ps.setString(3, String.valueOf(cmbTKategori.getSelectedItem()));
            ps.setString(4, kode);
            ps.setString(5, txtTDesk.getText().trim());
            setStrNull(ps, 6, txtCmNik.getText().trim());
            ps.executeUpdate();
            txtTKode.setText(""); txtTDesk.setText("");
            muatTindakan();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menambah tindakan:\n" + e.getMessage());
        }
    }

    private void hapusTindakan() {
        int r = tbTindakan.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Pilih baris tindakan dulu."); return; }
        String id = String.valueOf(tabTindakan.getValueAt(r, 4));
        try (PreparedStatement ps = koneksi.prepareStatement(
                "delete from registrasi_jantung_tindakan where id_tindakan=?")) {
            ps.setString(1, id);
            ps.executeUpdate();
            muatTindakan();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus:\n" + e.getMessage());
        }
    }

    private void hapusRegistrasi() {
        if (idRegistrasi == 0) { JOptionPane.showMessageDialog(this, "Belum ada registrasi."); return; }
        int ok = JOptionPane.showConfirmDialog(this,
                "Hapus registrasi jantung pasien ini beserta seluruh detailnya?\n"
              + "Resource yang sudah terkirim ke SatuSehat TIDAK ikut terhapus.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            for (String t : new String[]{"registrasi_jantung_tindakan", "registrasi_jantung_pjr",
                    "registrasi_jantung_ska", "registrasi_jantung_tod",
                    "registrasi_jantung_faktor_risiko", "satu_sehat_jantung_kirim"}) {
                try (PreparedStatement ps = koneksi.prepareStatement("delete from " + t + " where id_registrasi=?")) {
                    ps.setLong(1, idRegistrasi); ps.executeUpdate();
                }
            }
            try (PreparedStatement ps = koneksi.prepareStatement("delete from registrasi_jantung where id_registrasi=?")) {
                ps.setLong(1, idRegistrasi); ps.executeUpdate();
            }
            idRegistrasi = 0;
            JOptionPane.showMessageDialog(this, "Registrasi dihapus.");
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus:\n" + e.getMessage());
        }
    }

    private void kirim() {
        if (!fungsi.akses.getsatu_sehat_kirim_condition()) {
            JOptionPane.showMessageDialog(this, "Anda tidak memiliki hak akses untuk Registrasi Jantung.");
            return;
        }
        if (idRegistrasi == 0) { JOptionPane.showMessageDialog(this, "Simpan registrasi dulu."); return; }
        int ok = JOptionPane.showConfirmDialog(this,
                "Kirim registrasi jantung ini ke SatuSehat?\n(Encounter kunjungan harus sudah terkirim lebih dulu.)",
                "Konfirmasi Kirim", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        final long id = idRegistrasi;
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
            });
        }, "kirim-jantung").start();
    }

    private void cariCareManager() {
        String nik = txtCmNik.getText().trim();
        if (nik.isEmpty()) { JOptionPane.showMessageDialog(this, "Isi NIK pegawai dulu."); return; }
        isiNamaCareManager();
        if (txtCmNama.getText().trim().isEmpty())
            JOptionPane.showMessageDialog(this, "Pegawai dengan NIK " + nik + " tidak ditemukan.");
    }

    private void isiNamaCareManager() {
        txtCmNama.setText(scalar("select nama from pegawai where nik=? limit 1", txtCmNik.getText().trim(), ""));
    }

    // ==================================================================== Util UI

    private void judul(JPanel p, String teks, int x, int y) {
        widget.Label l = new widget.Label();
        l.setText(teks);
        l.setFont(new Font("Tahoma", 1, 11));
        l.setForeground(new Color(0, 90, 140));
        l.setBounds(x, y, 500, 20);
        p.add(l);
    }

    /** Judul seksi yang panelnya bisa disembunyikan; mengembalikan panel penanda. */
    private JPanel seksi(JPanel parent, String teks, int x, int y, int w, int h) {
        judul(parent, teks, x, y);
        JPanel marker = new JPanel();
        marker.setOpaque(false);
        marker.setBounds(x, y, 1, 1);
        parent.add(marker);
        return marker;
    }

    private void addLabel(JPanel parent, String text, int x, int y, int w, int h, Font f) {
        widget.Label lbl = new widget.Label();
        lbl.setText(text); lbl.setFont(f); lbl.setBounds(x, y, w, h);
        parent.add(lbl);
    }
    private widget.ComboBox mkCombo(String[] vals) {
        widget.ComboBox c = new widget.ComboBox();
        c.setModel(new DefaultComboBoxModel<>(vals));
        return c;
    }
    private widget.Tanggal mkTgl() {
        widget.Tanggal t = new widget.Tanggal();
        t.setDisplayFormat("dd-MM-yyyy");
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

    private void pilihNilai(widget.ComboBox c, String nilai) {
        if (nilai == null || nilai.isEmpty()) return;
        for (int i = 0; i < c.getItemCount(); i++) {
            if (nilai.equals(String.valueOf(c.getItemAt(i)))) { c.setSelectedIndex(i); return; }
        }
    }

    private void setTgl(widget.Tanggal t, String tgl) {
        if (tgl == null || tgl.isEmpty() || tgl.startsWith("0000")) return;
        try { t.setDate(java.sql.Date.valueOf(tgl.substring(0, 10))); } catch (Exception ignored) { }
    }

    /** "" -> NULL, agar "belum dinilai" tidak tersimpan sebagai string kosong. */
    private void setStrNull(PreparedStatement ps, int idx, String v) throws Exception {
        if (v == null || v.trim().isEmpty()) ps.setNull(idx, java.sql.Types.VARCHAR);
        else ps.setString(idx, v.trim());
    }

    /** Combo Ya/Tidak/kosong -> Boolean/NULL. NULL berarti belum dinilai, beda dari Tidak. */
    private void setBool(PreparedStatement ps, int idx, widget.ComboBox c) throws Exception {
        String v = String.valueOf(c.getSelectedItem());
        if ("Ya".equals(v))         ps.setInt(idx, 1);
        else if ("Tidak".equals(v)) ps.setInt(idx, 0);
        else                        ps.setNull(idx, java.sql.Types.TINYINT);
    }

    private void setYaTidak(widget.ComboBox c, ResultSet rs, String kolom) {
        try {
            boolean v = rs.getBoolean(kolom);
            if (rs.wasNull()) c.setSelectedIndex(0);
            else c.setSelectedItem(v ? "Ya" : "Tidak");
        } catch (Exception e) { c.setSelectedIndex(0); }
    }

    private String scalar(String sql, String p1, String dflt) {
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, p1);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? nz(rs.getString(1)) : dflt; }
        } catch (Exception e) { return dflt; }
    }

    private static String nz(String s) { return s == null ? "" : s; }
}
