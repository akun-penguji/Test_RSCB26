/*
 * Form Registrasi Stroke per pasien (SatuSehat) — Fase 3 & 4.
 *
 * Hand-built tanpa .form, meniru DlgRegistrasiJantung.
 *
 * Yang khas dibanding modul lain: panel SKOR menampung NIHSS dan mRS di
 * BANYAK FASE (masuk, pasca-rTPA, 24 jam, pulang, pra-stroke, 3 bulan,
 * 6 bulan). Fase follow-up boleh diisi tanpa kunjungan — no_rawat dikosongkan.
 *
 * Registry LONGITUDINAL: satu kasus per pasien; status 'finished' hanya saat
 * pasien meninggal.
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

public final class DlgRegistrasiStroke extends javax.swing.JDialog {

    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();

    private String noRawat = "", noRkm = "", namaPasien = "";
    private long idRegistrasi = 0;

    private widget.InternalFrame internalFrame1;
    private widget.Label lblPasien;

    // Diagnosis
    private widget.ComboBox cmbJenisStroke, cmbStatus, cmbSerangan;
    private widget.TextBox txtIcd10Kd, txtIcd10Nm, txtOnset, txtWakeUp;
    private widget.Tanggal DTPReg, DTPDiag;

    // Faktor risiko
    private widget.ComboBox cmbHtAda, cmbHtDerajat, cmbHtObat, cmbHtKontrol;
    private widget.ComboBox cmbDmAda, cmbDmObat, cmbDmKontrol;
    private widget.ComboBox cmbDlAda, cmbDlObat, cmbDlStatin;
    private widget.ComboBox cmbMerokok, cmbAlkohol, cmbKb, cmbRiwKeluarga;

    // Riwayat penyakit (Lampiran 1)
    private final Map<String, javax.swing.JCheckBox> chkRiwayat = new LinkedHashMap<>();

    // Skor NIHSS / mRS berfase
    private widget.Table tbSkor;
    private DefaultTableModel tabSkor;
    private widget.ComboBox cmbSkInstrumen, cmbSkFase, cmbSkNilaiMrs, cmbSkInterp;
    private widget.TextBox txtSkNilai, txtSkTanggal;

    // Disfagia
    private widget.ComboBox cmbDisfagia;

    // Pencitraan
    private widget.Table tbCitra;
    private DefaultTableModel tabCitra;
    private widget.ComboBox cmbCtModal, cmbCtKode, cmbCtHasil;
    private widget.TextBox txtAspects;

    // Tindakan
    private widget.Table tbTindakan;
    private DefaultTableModel tabTindakan;
    private widget.ComboBox cmbTJenis, cmbTKode, cmbTTici;
    private widget.TextBox txtTKodeIcd9, txtTWaktu, txtTRekanalisasi;

    // Registry
    private widget.ComboBox cmbVital;
    private widget.TextBox txtCmNik, txtCmNama, txtSebab;
    private widget.Tanggal DTPMeninggal;

    private widget.Button BtnSimpan, BtnKirim, BtnHapus, BtnTutup;

    private static final String[] JENIS_STROKE = {"tidak_diketahui", "iskemik", "hemoragik", "tia"};
    private static final String[] STATUS   = {"waitlist", "active", "finished"};
    private static final String[] VITAL    = {"hidup", "meninggal", "tidak diketahui"};
    private static final String[] YATIDAK  = {"", "Ya", "Tidak"};
    private static final String[] SERANGAN = {"1", "2", "3", "4", "5"};
    private static final String[] INSTRUMEN = {"nihss", "mrs"};
    private static final String[] FASE_NIHSS = {"masuk", "pasca_rtpa", "rtpa_24jam", "pulang"};
    private static final String[] FASE_MRS   = {"pra_stroke", "masuk", "pulang", "bulan_3", "bulan_6"};
    private static final String[] MODALITAS  = {"ct", "cta", "mri"};
    private static final String[] JENIS_TIND = {"rtpa", "trombektomi", "rehabilitasi", "lainnya"};

    public DlgRegistrasiStroke(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle(":: Registrasi Stroke (SatuSehat) ::");
        java.awt.Dimension scr = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(Math.min(1180, scr.width - 40), Math.min(780, scr.height - 60));
        setLocationRelativeTo(null);
        buildUI();
    }

    public void isCek() {
        boolean boleh = fungsi.akses.getsatu_sehat_kirim_condition();
        BtnSimpan.setEnabled(boleh);
        BtnKirim.setEnabled(boleh);
        BtnHapus.setEnabled(boleh);
    }

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
                "::[ Registrasi Stroke Satu Sehat ]::",
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
        Font f = new Font("Tahoma", 0, 11);
        int LX = 12, FX = 150, RX = 580, FX2 = 720;
        int y = 10;

        // ---------- Diagnosis ----------
        judul(isi, "Diagnosis & Serangan", LX, y); y += 24;
        addLabel(isi, "Jenis Stroke :", LX, y, 130, 22, f);
        cmbJenisStroke = mkCombo(JENIS_STROKE); cmbJenisStroke.setBounds(FX, y, 160, 23); isi.add(cmbJenisStroke);
        addLabel(isi, "Status Registry :", RX, y, 130, 22, f);
        cmbStatus = mkCombo(STATUS); cmbStatus.setBounds(FX2, y, 140, 23); isi.add(cmbStatus);
        y += 28;

        addLabel(isi, "Serangan ke- :", LX, y, 130, 22, f);
        cmbSerangan = mkCombo(SERANGAN); cmbSerangan.setBounds(FX, y, 70, 23); isi.add(cmbSerangan);
        addLabel(isi, "(>1 dikirim sebagai Condition 'recurrence')", FX + 78, y, 320, 22, f);
        y += 28;

        addLabel(isi, "Diagnosa ICD-10 :", LX, y, 130, 22, f);
        txtIcd10Kd = new widget.TextBox(); txtIcd10Kd.setBounds(FX, y, 80, 23); isi.add(txtIcd10Kd);
        txtIcd10Nm = new widget.TextBox(); txtIcd10Nm.setBounds(FX + 85, y, 340, 23);
        txtIcd10Nm.setEditable(false); isi.add(txtIcd10Nm);
        y += 28;

        addLabel(isi, "Tgl Registrasi :", LX, y, 130, 22, f);
        DTPReg = mkTgl(); DTPReg.setBounds(FX, y, 120, 23); isi.add(DTPReg);
        addLabel(isi, "Tgl Diagnosis :", RX, y, 130, 22, f);
        DTPDiag = mkTgl(); DTPDiag.setBounds(FX2, y, 120, 23); isi.add(DTPDiag);
        y += 28;

        addLabel(isi, "Onset Serangan :", LX, y, 130, 22, f);
        txtOnset = new widget.TextBox(); txtOnset.setBounds(FX, y, 160, 23); isi.add(txtOnset);
        addLabel(isi, "Wake-up Stroke :", RX, y, 130, 22, f);
        txtWakeUp = new widget.TextBox(); txtWakeUp.setBounds(FX2, y, 160, 23); isi.add(txtWakeUp);
        y += 24;
        addLabel(isi, "(format yyyy-MM-dd HH:mm — onset dipakai sebagai Condition.onsetDateTime)", FX, y, 520, 20, f);
        y += 28;

        // ---------- Faktor risiko ----------
        judul(isi, "Faktor Risiko", LX, y); y += 24;
        addLabel(isi, "Hipertensi :", LX, y, 130, 22, f);
        cmbHtAda = mkCombo(YATIDAK); cmbHtAda.setBounds(FX, y, 80, 23); isi.add(cmbHtAda);
        addLabel(isi, "Derajat :", FX + 88, y, 55, 22, f);
        cmbHtDerajat = mkComboRef("ht_derajat_obs"); cmbHtDerajat.setBounds(FX + 145, y, 190, 23); isi.add(cmbHtDerajat);
        addLabel(isi, "Obat :", RX, y, 45, 22, f);
        cmbHtObat = mkCombo(YATIDAK); cmbHtObat.setBounds(RX + 50, y, 75, 23); isi.add(cmbHtObat);
        addLabel(isi, "Terkontrol :", RX + 135, y, 75, 22, f);
        cmbHtKontrol = mkCombo(YATIDAK); cmbHtKontrol.setBounds(RX + 215, y, 75, 23); isi.add(cmbHtKontrol);
        y += 28;

        addLabel(isi, "Diabetes :", LX, y, 130, 22, f);
        cmbDmAda = mkCombo(YATIDAK); cmbDmAda.setBounds(FX, y, 80, 23); isi.add(cmbDmAda);
        addLabel(isi, "Obat :", RX, y, 45, 22, f);
        cmbDmObat = mkCombo(YATIDAK); cmbDmObat.setBounds(RX + 50, y, 75, 23); isi.add(cmbDmObat);
        addLabel(isi, "Terkontrol :", RX + 135, y, 75, 22, f);
        cmbDmKontrol = mkCombo(YATIDAK); cmbDmKontrol.setBounds(RX + 215, y, 75, 23); isi.add(cmbDmKontrol);
        y += 28;

        addLabel(isi, "Dislipidemia :", LX, y, 130, 22, f);
        cmbDlAda = mkCombo(YATIDAK); cmbDlAda.setBounds(FX, y, 80, 23); isi.add(cmbDlAda);
        addLabel(isi, "Statin :", FX + 88, y, 55, 22, f);
        cmbDlStatin = mkComboRef("statin_intensitas"); cmbDlStatin.setBounds(FX + 145, y, 190, 23); isi.add(cmbDlStatin);
        addLabel(isi, "Obat :", RX, y, 45, 22, f);
        cmbDlObat = mkCombo(YATIDAK); cmbDlObat.setBounds(RX + 50, y, 75, 23); isi.add(cmbDlObat);
        y += 28;

        addLabel(isi, "Merokok :", LX, y, 130, 22, f);
        cmbMerokok = mkComboRef("merokok"); cmbMerokok.setBounds(FX, y, 185, 23); isi.add(cmbMerokok);
        addLabel(isi, "Alkohol :", FX + 195, y, 60, 22, f);
        cmbAlkohol = mkCombo(YATIDAK); cmbAlkohol.setBounds(FX + 258, y, 75, 23); isi.add(cmbAlkohol);
        addLabel(isi, "KB Hormonal :", RX, y, 90, 22, f);
        cmbKb = mkCombo(YATIDAK); cmbKb.setBounds(RX + 95, y, 75, 23); isi.add(cmbKb);
        addLabel(isi, "Riw. Keluarga :", RX + 180, y, 95, 22, f);
        cmbRiwKeluarga = mkCombo(YATIDAK); cmbRiwKeluarga.setBounds(RX + 280, y, 75, 23); isi.add(cmbRiwKeluarga);
        y += 32;

        // ---------- Riwayat penyakit ----------
        judul(isi, "Riwayat Penyakit (dikirim sebagai Condition tersendiri)", LX, y); y += 24;
        JPanel pnlRiw = new JPanel();
        pnlRiw.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 2));
        pnlRiw.setBounds(LX, y, 1080, 30);
        pnlRiw.setOpaque(false);
        for (Ref rf : muatRef("stroke_riwayat")) {
            javax.swing.JCheckBox cb = new javax.swing.JCheckBox(rf.display);
            cb.setFont(f); cb.setOpaque(false);
            chkRiwayat.put(rf.kode, cb);
            pnlRiw.add(cb);
        }
        isi.add(pnlRiw);
        y += 36;

        // ---------- Skor berfase ----------
        judul(isi, "Skor NIHSS & mRS — satu baris per fase", LX, y); y += 24;
        addLabel(isi, "Instrumen :", LX, y, 70, 22, f);
        cmbSkInstrumen = mkCombo(INSTRUMEN); cmbSkInstrumen.setBounds(LX + 72, y, 90, 23);
        cmbSkInstrumen.addActionListener(e -> perbaruiPanelSkor()); isi.add(cmbSkInstrumen);
        addLabel(isi, "Fase :", LX + 170, y, 40, 22, f);
        cmbSkFase = mkCombo(FASE_NIHSS); cmbSkFase.setBounds(LX + 212, y, 120, 23); isi.add(cmbSkFase);
        addLabel(isi, "Tanggal :", LX + 340, y, 55, 22, f);
        txtSkTanggal = new widget.TextBox(); txtSkTanggal.setBounds(LX + 398, y, 140, 23); isi.add(txtSkTanggal);
        addLabel(isi, "Skor :", LX + 546, y, 40, 22, f);
        txtSkNilai = new widget.TextBox(); txtSkNilai.setBounds(LX + 588, y, 60, 23); isi.add(txtSkNilai);
        cmbSkNilaiMrs = mkComboRef("stroke_mrs"); cmbSkNilaiMrs.setBounds(LX + 588, y, 90, 23);
        cmbSkNilaiMrs.setVisible(false); isi.add(cmbSkNilaiMrs);
        addLabel(isi, "Interpretasi :", LX + 690, y, 80, 22, f);
        cmbSkInterp = mkComboRef("stroke_nihss_interpretasi"); cmbSkInterp.setBounds(LX + 772, y, 200, 23); isi.add(cmbSkInterp);
        widget.Button bSk = new widget.Button(); bSk.setText("Tambah"); bSk.setBounds(LX + 980, y, 90, 23);
        bSk.addActionListener(e -> tambahSkor()); isi.add(bSk);
        y += 26;
        widget.Button bSkHapus = new widget.Button(); bSkHapus.setText("Hapus Skor"); bSkHapus.setBounds(LX + 980, y, 90, 23);
        bSkHapus.addActionListener(e -> hapusSkor()); isi.add(bSkHapus);
        addLabel(isi, "Fase bulan_3 / bulan_6 boleh diisi tanpa kunjungan (follow-up di luar rawat).", LX, y, 600, 22, f);
        y += 28;

        tabSkor = new DefaultTableModel(null, new String[]{"Instrumen", "Fase", "Tanggal", "Skor", "Nilai mRS", "Interpretasi", "No.Rawat", "id"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbSkor = new widget.Table(); tbSkor.setModel(tabSkor);
        tbSkor.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setLebar(tbSkor, new int[]{90, 110, 140, 60, 90, 260, 130, 0});
        tbSkor.setDefaultRenderer(Object.class, new WarnaTable());
        widget.ScrollPane scSk = new widget.ScrollPane(); scSk.setViewportView(tbSkor);
        scSk.setBounds(LX, y, 1080, 110); isi.add(scSk);
        y += 120;

        // ---------- Disfagia ----------
        judul(isi, "Skrining Disfagia", LX, y); y += 24;
        addLabel(isi, "Hasil :", LX, y, 60, 22, f);
        cmbDisfagia = mkComboRef("stroke_disfagia"); cmbDisfagia.setBounds(LX + 65, y, 260, 23); isi.add(cmbDisfagia);
        y += 32;

        // ---------- Pencitraan ----------
        judul(isi, "Pencitraan (CT / CTA / MRI) & ASPECTS", LX, y); y += 24;
        addLabel(isi, "Modalitas :", LX, y, 70, 22, f);
        cmbCtModal = mkCombo(MODALITAS); cmbCtModal.setBounds(LX + 72, y, 80, 23);
        cmbCtModal.addActionListener(e -> perbaruiPanelCitra()); isi.add(cmbCtModal);
        addLabel(isi, "Jenis :", LX + 160, y, 45, 22, f);
        cmbCtKode = mkComboRef("stroke_ct_kode"); cmbCtKode.setBounds(LX + 208, y, 250, 23); isi.add(cmbCtKode);
        addLabel(isi, "Hasil :", LX + 466, y, 45, 22, f);
        cmbCtHasil = mkComboRef("stroke_ct_hasil"); cmbCtHasil.setBounds(LX + 514, y, 260, 23); isi.add(cmbCtHasil);
        addLabel(isi, "ASPECTS :", LX + 782, y, 65, 22, f);
        txtAspects = new widget.TextBox(); txtAspects.setBounds(LX + 850, y, 60, 23); isi.add(txtAspects);
        widget.Button bCitra = new widget.Button(); bCitra.setText("Tambah"); bCitra.setBounds(LX + 920, y, 75, 23);
        bCitra.addActionListener(e -> tambahCitra()); isi.add(bCitra);
        widget.Button bCitraH = new widget.Button(); bCitraH.setText("Hapus"); bCitraH.setBounds(LX + 1000, y, 70, 23);
        bCitraH.addActionListener(e -> hapusCitra()); isi.add(bCitraH);
        y += 28;

        tabCitra = new DefaultTableModel(null, new String[]{"Modalitas", "Jenis", "Hasil", "ASPECTS", "Tanggal", "id"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbCitra = new widget.Table(); tbCitra.setModel(tabCitra);
        tbCitra.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setLebar(tbCitra, new int[]{90, 260, 260, 80, 140, 0});
        tbCitra.setDefaultRenderer(Object.class, new WarnaTable());
        widget.ScrollPane scC = new widget.ScrollPane(); scC.setViewportView(tbCitra);
        scC.setBounds(LX, y, 1080, 90); isi.add(scC);
        y += 100;

        // ---------- Tindakan ----------
        judul(isi, "Tindakan (rTPA / Trombektomi / Rehabilitasi)", LX, y); y += 24;
        addLabel(isi, "Jenis :", LX, y, 45, 22, f);
        cmbTJenis = mkCombo(JENIS_TIND); cmbTJenis.setBounds(LX + 48, y, 120, 23);
        cmbTJenis.addActionListener(e -> perbaruiPanelTindakan()); isi.add(cmbTJenis);
        addLabel(isi, "Kode :", LX + 176, y, 40, 22, f);
        txtTKodeIcd9 = new widget.TextBox(); txtTKodeIcd9.setBounds(LX + 218, y, 90, 23); isi.add(txtTKodeIcd9);
        cmbTKode = mkComboRef("stroke_rehab"); cmbTKode.setBounds(LX + 218, y, 200, 23);
        cmbTKode.setVisible(false); isi.add(cmbTKode);
        addLabel(isi, "Waktu :", LX + 430, y, 50, 22, f);
        txtTWaktu = new widget.TextBox(); txtTWaktu.setBounds(LX + 482, y, 140, 23); isi.add(txtTWaktu);
        addLabel(isi, "Rekanalisasi :", LX + 630, y, 85, 22, f);
        txtTRekanalisasi = new widget.TextBox(); txtTRekanalisasi.setBounds(LX + 718, y, 140, 23); isi.add(txtTRekanalisasi);
        addLabel(isi, "TICI :", LX + 866, y, 35, 22, f);
        cmbTTici = mkComboRef("stroke_tici"); cmbTTici.setBounds(LX + 903, y, 90, 23); isi.add(cmbTTici);
        y += 26;
        widget.Button bTin = new widget.Button(); bTin.setText("Tambah"); bTin.setBounds(LX + 920, y, 75, 23);
        bTin.addActionListener(e -> tambahTindakan()); isi.add(bTin);
        widget.Button bTinH = new widget.Button(); bTinH.setText("Hapus"); bTinH.setBounds(LX + 1000, y, 70, 23);
        bTinH.addActionListener(e -> hapusTindakan()); isi.add(bTinH);
        addLabel(isi, "Trombektomi: Waktu = groin puncture.", LX, y, 400, 22, f);
        y += 28;

        tabTindakan = new DefaultTableModel(null, new String[]{"Jenis", "Kode", "Waktu", "Rekanalisasi", "TICI", "id"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbTindakan = new widget.Table(); tbTindakan.setModel(tabTindakan);
        tbTindakan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setLebar(tbTindakan, new int[]{120, 200, 150, 150, 90, 0});
        tbTindakan.setDefaultRenderer(Object.class, new WarnaTable());
        widget.ScrollPane scT = new widget.ScrollPane(); scT.setViewportView(tbTindakan);
        scT.setBounds(LX, y, 1080, 90); isi.add(scT);
        y += 100;

        // ---------- Registry & outcome ----------
        judul(isi, "Care Manager & Outcome", LX, y); y += 24;
        addLabel(isi, "Care Manager NIK :", LX, y, 130, 22, f);
        txtCmNik = new widget.TextBox(); txtCmNik.setBounds(FX, y, 120, 23); isi.add(txtCmNik);
        txtCmNama = new widget.TextBox(); txtCmNama.setBounds(FX + 128, y, 280, 23);
        txtCmNama.setEditable(false); isi.add(txtCmNama);
        widget.Button bCm = new widget.Button(); bCm.setText("?"); bCm.setBounds(FX + 412, y, 30, 23);
        bCm.addActionListener(e -> isiNamaCareManager()); isi.add(bCm);
        addLabel(isi, "Status Vital :", RX, y, 90, 22, f);
        cmbVital = mkCombo(VITAL); cmbVital.setBounds(FX2, y, 150, 23); isi.add(cmbVital);
        y += 28;
        addLabel(isi, "Tgl Meninggal :", LX, y, 130, 22, f);
        DTPMeninggal = mkTgl(); DTPMeninggal.setBounds(FX, y, 120, 23); isi.add(DTPMeninggal);
        addLabel(isi, "Sebab :", RX, y, 60, 22, f);
        txtSebab = new widget.TextBox(); txtSebab.setBounds(FX2, y, 300, 23); isi.add(txtSebab);
        y += 40;

        isi.setPreferredSize(new Dimension(1120, y));
        JScrollPane scroll = new JScrollPane(isi);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        internalFrame1.add(scroll, BorderLayout.CENTER);

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

        perbaruiPanelSkor();
        perbaruiPanelCitra();
        perbaruiPanelTindakan();
    }

    /** NIHSS pakai skor angka + interpretasi; mRS pakai combo nilai. Fase pun berbeda. */
    private void perbaruiPanelSkor() {
        boolean nihss = "nihss".equals(String.valueOf(cmbSkInstrumen.getSelectedItem()));
        txtSkNilai.setVisible(nihss);
        cmbSkInterp.setEnabled(nihss);
        cmbSkNilaiMrs.setVisible(!nihss);
        cmbSkFase.setModel(new DefaultComboBoxModel<>(nihss ? FASE_NIHSS : FASE_MRS));
    }

    private void perbaruiPanelCitra() {
        String m = String.valueOf(cmbCtModal.getSelectedItem());
        ganti(cmbCtKode,  "stroke_" + m + "_kode");
        // MRI tidak punya value set hasil di lampiran — hanya kode modalitas.
        boolean adaHasil = !"mri".equals(m);
        ganti(cmbCtHasil, adaHasil ? "stroke_" + m + "_hasil" : "");
        cmbCtHasil.setEnabled(adaHasil);
        txtAspects.setEnabled("ct".equals(m));
    }

    private void perbaruiPanelTindakan() {
        boolean rehab = "rehabilitasi".equals(String.valueOf(cmbTJenis.getSelectedItem()));
        boolean trombo = "trombektomi".equals(String.valueOf(cmbTJenis.getSelectedItem()));
        cmbTKode.setVisible(rehab);
        txtTKodeIcd9.setVisible(!rehab);
        cmbTTici.setEnabled(trombo);
        txtTRekanalisasi.setEnabled(trombo);
    }

    // ==================================================================== Referensi

    private static final class Ref { String kode = "", display = ""; }

    private List<Ref> muatRef(String kelompok) {
        List<Ref> out = new ArrayList<>();
        if (kelompok == null || kelompok.isEmpty()) return out;
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
        } catch (Exception e) { System.out.println("Notif muatRef " + kelompok + " : " + e); }
        return out;
    }

    private widget.ComboBox mkComboRef(String kelompok) {
        widget.ComboBox c = new widget.ComboBox();
        ganti(c, kelompok);
        return c;
    }

    private void ganti(widget.ComboBox c, String kelompok) {
        List<Ref> refs = muatRef(kelompok);
        List<String> tampil = new ArrayList<>();
        List<String> kode = new ArrayList<>();
        tampil.add(""); kode.add("");
        for (Ref r : refs) { tampil.add(r.display); kode.add(r.kode); }
        c.setModel(new DefaultComboBoxModel<>(tampil.toArray(new String[0])));
        c.putClientProperty("kodeLokal", kode);
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
        int idx = ((List<String>) prop).indexOf(nz(kodeLokal));
        c.setSelectedIndex(idx >= 0 ? idx : 0);
    }

    // ==================================================================== Muat

    private void prefillIcd10() {
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select dp.kd_penyakit, ifnull(pk.nm_penyakit,'') nm from diagnosa_pasien dp "
              + "left join penyakit pk on pk.kd_penyakit=dp.kd_penyakit "
              + "where dp.no_rawat=? and dp.kd_penyakit regexp '^(I6[0-9]|G45)' "
              + "order by dp.prioritas limit 1")) {
            ps.setString(1, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    txtIcd10Kd.setText(nz(rs.getString("kd_penyakit")));
                    txtIcd10Nm.setText(nz(rs.getString("nm")));
                }
            }
        } catch (Exception e) { System.out.println("Notif prefillIcd10 stroke : " + e); }
    }

    private void muatRegistrasi() {
        idRegistrasi = 0;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select * from registrasi_stroke where no_rkm_medis=? limit 1")) {
            ps.setString(1, noRkm);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idRegistrasi = rs.getLong("id_registrasi");
                    pilihNilai(cmbJenisStroke, nz(rs.getString("jenis_stroke")));
                    pilihNilai(cmbStatus, nz(rs.getString("status")));
                    pilihNilai(cmbSerangan, nz(rs.getString("serangan_ke")));
                    pilihNilai(cmbVital, nz(rs.getString("status_vital")));
                    if (!nz(rs.getString("kd_penyakit")).isEmpty()) txtIcd10Kd.setText(rs.getString("kd_penyakit"));
                    txtOnset.setText(nz(rs.getString("onset_serangan")));
                    txtWakeUp.setText(nz(rs.getString("wake_up_stroke")));
                    txtCmNik.setText(nz(rs.getString("kd_dokter")));
                    txtSebab.setText(nz(rs.getString("sebab_kematian")));
                    setTgl(DTPReg, nz(rs.getString("tgl_registrasi")));
                    setTgl(DTPDiag, nz(rs.getString("tgl_diagnosis")));
                    setTgl(DTPMeninggal, nz(rs.getString("tgl_meninggal")));
                    isiNamaCareManager();
                }
            }
        } catch (Exception e) { System.out.println("Notif muatRegistrasi stroke : " + e); }
        muatFaktor();
        muatRiwayat();
        muatSkor();
        muatDisfagia();
        muatCitra();
        muatTindakan();
    }

    private void muatFaktor() {
        if (idRegistrasi == 0) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select * from registrasi_stroke_faktor_risiko where id_registrasi=? and no_rawat=? "
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
                    setYaTidak(cmbAlkohol, rs, "alkohol");
                    setYaTidak(cmbKb, rs, "kb_hormonal");
                    setYaTidak(cmbRiwKeluarga, rs, "riwayat_keluarga");
                }
            }
        } catch (Exception e) { System.out.println("Notif muatFaktor stroke : " + e); }
    }

    private void muatRiwayat() {
        for (javax.swing.JCheckBox cb : chkRiwayat.values()) cb.setSelected(false);
        if (idRegistrasi == 0) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select kode from registrasi_stroke_riwayat where id_registrasi=?")) {
            ps.setLong(1, idRegistrasi);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    javax.swing.JCheckBox cb = chkRiwayat.get(nz(rs.getString("kode")));
                    if (cb != null) cb.setSelected(true);
                }
            }
        } catch (Exception e) { System.out.println("Notif muatRiwayat : " + e); }
    }

    private void muatSkor() {
        Valid.tabelKosong(tabSkor);
        if (idRegistrasi == 0) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select s.id_skor, s.instrumen, s.fase, s.tanggal, ifnull(s.nilai_angka,'') nilai_angka, "
              + "ifnull(m.display,'') mrs_display, ifnull(i.display,'') interp_display, ifnull(s.no_rawat,'') no_rawat "
              + "from registrasi_stroke_skor s "
              + "left join ref_registry_kode m on m.kelompok='stroke_mrs' and m.kode=s.nilai_kode "
              + "left join ref_registry_kode i on i.kelompok='stroke_nihss_interpretasi' and i.kode=s.interpretasi "
              + "where s.id_registrasi=? order by s.instrumen, s.tanggal")) {
            ps.setLong(1, idRegistrasi);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabSkor.addRow(new Object[]{
                        rs.getString("instrumen"), rs.getString("fase"), rs.getString("tanggal"),
                        rs.getString("nilai_angka"), rs.getString("mrs_display"),
                        rs.getString("interp_display"),
                        nz(rs.getString("no_rawat")).isEmpty() ? "(follow-up)" : rs.getString("no_rawat"),
                        rs.getString("id_skor")
                    });
                }
            }
        } catch (Exception e) { System.out.println("Notif muatSkor : " + e); }
    }

    private void muatDisfagia() {
        if (idRegistrasi == 0) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select hasil from registrasi_stroke_disfagia where id_registrasi=? and no_rawat=? limit 1")) {
            ps.setLong(1, idRegistrasi);
            ps.setString(2, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) pilihKode(cmbDisfagia, nz(rs.getString("hasil")));
            }
        } catch (Exception e) { System.out.println("Notif muatDisfagia : " + e); }
    }

    private void muatCitra() {
        Valid.tabelKosong(tabCitra);
        if (idRegistrasi == 0) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select id_citra, modalitas, ifnull(kode_modalitas,'') km, ifnull(hasil_kode,'') hk, "
              + "ifnull(aspects,'') aspects, tanggal from registrasi_stroke_pencitraan "
              + "where id_registrasi=? order by id_citra")) {
            ps.setLong(1, idRegistrasi);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabCitra.addRow(new Object[]{
                        rs.getString("modalitas"), rs.getString("km"), rs.getString("hk"),
                        rs.getString("aspects"), rs.getString("tanggal"), rs.getString("id_citra")
                    });
                }
            }
        } catch (Exception e) { System.out.println("Notif muatCitra : " + e); }
    }

    private void muatTindakan() {
        Valid.tabelKosong(tabTindakan);
        if (idRegistrasi == 0) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select id_tindakan, jenis, ifnull(kode,'') kode, waktu, "
              + "ifnull(waktu_rekanalisasi,'') wr, ifnull(tici,'') tici "
              + "from registrasi_stroke_tindakan where id_registrasi=? order by id_tindakan")) {
            ps.setLong(1, idRegistrasi);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabTindakan.addRow(new Object[]{
                        rs.getString("jenis"), rs.getString("kode"), rs.getString("waktu"),
                        rs.getString("wr"), rs.getString("tici"), rs.getString("id_tindakan")
                    });
                }
            }
        } catch (Exception e) { System.out.println("Notif muatTindakan stroke : " + e); }
    }

    // ==================================================================== Simpan

    private void simpan() {
        if (noRkm.isEmpty() || noRawat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pasien belum dipilih.");
            return;
        }
        try {
            try (PreparedStatement ps = koneksi.prepareStatement(
                    "insert into registrasi_stroke (no_rkm_medis,no_rawat,tgl_registrasi,jenis_stroke,serangan_ke,"
                  + "kd_penyakit,tgl_diagnosis,onset_serangan,wake_up_stroke,status,kd_dokter,status_vital,"
                  + "tgl_meninggal,sebab_kematian) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?) "
                  + "on duplicate key update no_rawat=values(no_rawat), jenis_stroke=values(jenis_stroke), "
                  + "serangan_ke=values(serangan_ke), kd_penyakit=values(kd_penyakit), "
                  + "tgl_diagnosis=values(tgl_diagnosis), onset_serangan=values(onset_serangan), "
                  + "wake_up_stroke=values(wake_up_stroke), status=values(status), kd_dokter=values(kd_dokter), "
                  + "status_vital=values(status_vital), tgl_meninggal=values(tgl_meninggal), "
                  + "sebab_kematian=values(sebab_kematian)")) {
                int i = 1;
                ps.setString(i++, noRkm);
                ps.setString(i++, noRawat);
                ps.setString(i++, Valid.SetTgl(DTPReg.getSelectedItem() + ""));
                ps.setString(i++, String.valueOf(cmbJenisStroke.getSelectedItem()));
                ps.setInt(i++, Integer.parseInt(String.valueOf(cmbSerangan.getSelectedItem())));
                setStrNull(ps, i++, txtIcd10Kd.getText().trim());
                setStrNull(ps, i++, Valid.SetTgl(DTPDiag.getSelectedItem() + ""));
                setStrNull(ps, i++, txtOnset.getText().trim());
                setStrNull(ps, i++, txtWakeUp.getText().trim());
                ps.setString(i++, String.valueOf(cmbStatus.getSelectedItem()));
                setStrNull(ps, i++, txtCmNik.getText().trim());
                ps.setString(i++, String.valueOf(cmbVital.getSelectedItem()));
                setStrNull(ps, i++, "meninggal".equals(String.valueOf(cmbVital.getSelectedItem()))
                        ? Valid.SetTgl(DTPMeninggal.getSelectedItem() + "") : "");
                setStrNull(ps, i++, txtSebab.getText().trim());
                ps.executeUpdate();
            }
            if (idRegistrasi == 0) {
                idRegistrasi = Long.parseLong(scalar("select id_registrasi from registrasi_stroke where no_rkm_medis=?", noRkm, "0"));
            }
            simpanFaktor();
            simpanRiwayat();
            simpanDisfagia();
            JOptionPane.showMessageDialog(this, "Registrasi stroke tersimpan (id " + idRegistrasi + ").");
            muatSkor(); muatCitra(); muatTindakan();
        } catch (Exception e) {
            System.out.println("Notif simpan registrasi stroke : " + e);
            JOptionPane.showMessageDialog(this, "Gagal menyimpan:\n" + e.getMessage());
        }
    }

    private void simpanFaktor() throws Exception {
        try (PreparedStatement d = koneksi.prepareStatement(
                "delete from registrasi_stroke_faktor_risiko where id_registrasi=? and no_rawat=?")) {
            d.setLong(1, idRegistrasi); d.setString(2, noRawat); d.executeUpdate();
        }
        try (PreparedStatement ps = koneksi.prepareStatement(
                "insert into registrasi_stroke_faktor_risiko (id_registrasi,no_rawat,tanggal,ht_ada,ht_derajat,"
              + "ht_dapat_obat,ht_terkontrol,dm_ada,dm_dapat_obat,dm_terkontrol,dl_ada,dl_dapat_obat,dl_statin,"
              + "merokok,alkohol,kb_hormonal,riwayat_keluarga,kd_dokter) "
              + "values (?,?,now(),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
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
            setBool(ps, i++, cmbAlkohol);
            setBool(ps, i++, cmbKb);
            setBool(ps, i++, cmbRiwKeluarga);
            setStrNull(ps, i++, txtCmNik.getText().trim());
            ps.executeUpdate();
        }
    }

    private void simpanRiwayat() throws Exception {
        try (PreparedStatement d = koneksi.prepareStatement(
                "delete from registrasi_stroke_riwayat where id_registrasi=?")) {
            d.setLong(1, idRegistrasi); d.executeUpdate();
        }
        for (Map.Entry<String, javax.swing.JCheckBox> e : chkRiwayat.entrySet()) {
            if (!e.getValue().isSelected()) continue;
            try (PreparedStatement ps = koneksi.prepareStatement(
                    "insert ignore into registrasi_stroke_riwayat (id_registrasi,kode) values (?,?)")) {
                ps.setLong(1, idRegistrasi); ps.setString(2, e.getKey()); ps.executeUpdate();
            }
        }
    }

    private void simpanDisfagia() throws Exception {
        String hasil = kodeDari(cmbDisfagia);
        if (hasil.isEmpty()) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "insert into registrasi_stroke_disfagia (id_registrasi,no_rawat,hasil,tanggal) values (?,?,?,now()) "
              + "on duplicate key update hasil=values(hasil), tanggal=now()")) {
            ps.setLong(1, idRegistrasi); ps.setString(2, noRawat); ps.setString(3, hasil);
            ps.executeUpdate();
        }
    }

    private void tambahSkor() {
        if (idRegistrasi == 0) { JOptionPane.showMessageDialog(this, "Simpan registrasi dulu."); return; }
        String instrumen = String.valueOf(cmbSkInstrumen.getSelectedItem());
        String fase = String.valueOf(cmbSkFase.getSelectedItem());
        String tgl = txtSkTanggal.getText().trim();
        if (tgl.isEmpty()) { JOptionPane.showMessageDialog(this, "Tanggal wajib diisi — inilah yang membedakan antar fase."); return; }

        String nilaiAngka = "", nilaiKode = "", interp = "";
        if ("nihss".equals(instrumen)) {
            nilaiAngka = txtSkNilai.getText().trim();
            if (nilaiAngka.isEmpty()) { JOptionPane.showMessageDialog(this, "Skor NIHSS belum diisi."); return; }
            interp = kodeDari(cmbSkInterp);
            if (interp.isEmpty()) interp = interpretasiOtomatis(nilaiAngka);
        } else {
            nilaiKode = kodeDari(cmbSkNilaiMrs);
            if (nilaiKode.isEmpty()) { JOptionPane.showMessageDialog(this, "Nilai mRS belum dipilih."); return; }
        }
        // Fase follow-up terjadi di luar kunjungan -> no_rawat dikosongkan.
        boolean followUp = "bulan_3".equals(fase) || "bulan_6".equals(fase);

        try (PreparedStatement ps = koneksi.prepareStatement(
                "insert into registrasi_stroke_skor (id_registrasi,no_rawat,instrumen,fase,tanggal,"
              + "nilai_angka,nilai_kode,interpretasi,kd_dokter) values (?,?,?,?,?,?,?,?,?) "
              + "on duplicate key update tanggal=values(tanggal), nilai_angka=values(nilai_angka), "
              + "nilai_kode=values(nilai_kode), interpretasi=values(interpretasi)")) {
            int i = 1;
            ps.setLong(i++, idRegistrasi);
            if (followUp) ps.setNull(i++, java.sql.Types.VARCHAR); else ps.setString(i++, noRawat);
            ps.setString(i++, instrumen);
            ps.setString(i++, fase);
            ps.setString(i++, tgl);
            if (nilaiAngka.isEmpty()) ps.setNull(i++, java.sql.Types.INTEGER);
            else ps.setInt(i++, Integer.parseInt(nilaiAngka));
            setStrNull(ps, i++, nilaiKode);
            setStrNull(ps, i++, interp);
            setStrNull(ps, i++, txtCmNik.getText().trim());
            ps.executeUpdate();
            txtSkNilai.setText(""); txtSkTanggal.setText("");
            muatSkor();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menambah skor:\n" + e.getMessage());
        }
    }

    /**
     * Interpretasi NIHSS dari skor.
     * Ambang di Lampiran 3 TIDAK kontinu (<5 / 6-14 / 15-24 / >25), sehingga skor
     * 5 dan 25 tidak masuk rentang mana pun. Di sini dipakai <=5 dan >=25 agar
     * setiap skor punya interpretasi; petugas tetap bisa mengoreksi manual.
     */
    private String interpretasiOtomatis(String skorStr) {
        try {
            int s = Integer.parseInt(skorStr);
            if (s <= 5)  return "ringan";
            if (s <= 14) return "sedang";
            if (s <= 24) return "berat";
            return "sangat_berat";
        } catch (Exception e) { return ""; }
    }

    private void hapusSkor()     { hapusBaris(tbSkor, tabSkor, 7, "registrasi_stroke_skor", "id_skor", this::muatSkor); }
    private void hapusCitra()    { hapusBaris(tbCitra, tabCitra, 5, "registrasi_stroke_pencitraan", "id_citra", this::muatCitra); }
    private void hapusTindakan() { hapusBaris(tbTindakan, tabTindakan, 5, "registrasi_stroke_tindakan", "id_tindakan", this::muatTindakan); }

    private void hapusBaris(widget.Table tb, DefaultTableModel model, int colId,
                            String tabel, String kolomId, Runnable muatUlang) {
        int r = tb.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Pilih baris dulu."); return; }
        try (PreparedStatement ps = koneksi.prepareStatement("delete from " + tabel + " where " + kolomId + "=?")) {
            ps.setString(1, String.valueOf(model.getValueAt(r, colId)));
            ps.executeUpdate();
            muatUlang.run();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus:\n" + e.getMessage());
        }
    }

    private void tambahCitra() {
        if (idRegistrasi == 0) { JOptionPane.showMessageDialog(this, "Simpan registrasi dulu."); return; }
        try (PreparedStatement ps = koneksi.prepareStatement(
                "insert into registrasi_stroke_pencitraan (id_registrasi,no_rawat,modalitas,kode_modalitas,"
              + "hasil_kode,aspects,tanggal) values (?,?,?,?,?,?,now())")) {
            int i = 1;
            ps.setLong(i++, idRegistrasi);
            ps.setString(i++, noRawat);
            ps.setString(i++, String.valueOf(cmbCtModal.getSelectedItem()));
            setStrNull(ps, i++, kodeDari(cmbCtKode));
            setStrNull(ps, i++, kodeDari(cmbCtHasil));
            String asp = txtAspects.getText().trim();
            if (asp.isEmpty()) ps.setNull(i++, java.sql.Types.INTEGER); else ps.setInt(i++, Integer.parseInt(asp));
            ps.executeUpdate();
            txtAspects.setText("");
            muatCitra();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menambah pencitraan:\n" + e.getMessage());
        }
    }

    private void tambahTindakan() {
        if (idRegistrasi == 0) { JOptionPane.showMessageDialog(this, "Simpan registrasi dulu."); return; }
        String jenis = String.valueOf(cmbTJenis.getSelectedItem());
        String kode = "rehabilitasi".equals(jenis) ? kodeDari(cmbTKode) : txtTKodeIcd9.getText().trim();
        if (kode.isEmpty()) { JOptionPane.showMessageDialog(this, "Kode tindakan belum diisi."); return; }
        String waktu = txtTWaktu.getText().trim();
        if (waktu.isEmpty()) { JOptionPane.showMessageDialog(this, "Waktu tindakan belum diisi."); return; }
        try (PreparedStatement ps = koneksi.prepareStatement(
                "insert into registrasi_stroke_tindakan (id_registrasi,no_rawat,jenis,kode,waktu,"
              + "waktu_rekanalisasi,tici,kd_dokter) values (?,?,?,?,?,?,?,?)")) {
            int i = 1;
            ps.setLong(i++, idRegistrasi);
            ps.setString(i++, noRawat);
            ps.setString(i++, jenis);
            ps.setString(i++, kode);
            ps.setString(i++, waktu);
            setStrNull(ps, i++, txtTRekanalisasi.getText().trim());
            setStrNull(ps, i++, kodeDari(cmbTTici));
            setStrNull(ps, i++, txtCmNik.getText().trim());
            ps.executeUpdate();
            txtTKodeIcd9.setText(""); txtTWaktu.setText(""); txtTRekanalisasi.setText("");
            muatTindakan();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menambah tindakan:\n" + e.getMessage());
        }
    }

    private void hapusRegistrasi() {
        if (idRegistrasi == 0) { JOptionPane.showMessageDialog(this, "Belum ada registrasi."); return; }
        int ok = JOptionPane.showConfirmDialog(this,
                "Hapus registrasi stroke pasien ini beserta seluruh detailnya?\n"
              + "Resource yang sudah terkirim ke SatuSehat TIDAK ikut terhapus.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            for (String t : new String[]{"registrasi_stroke_tindakan", "registrasi_stroke_pencitraan",
                    "registrasi_stroke_disfagia", "registrasi_stroke_skor", "registrasi_stroke_riwayat",
                    "registrasi_stroke_faktor_risiko", "satu_sehat_stroke_kirim"}) {
                try (PreparedStatement ps = koneksi.prepareStatement("delete from " + t + " where id_registrasi=?")) {
                    ps.setLong(1, idRegistrasi); ps.executeUpdate();
                }
            }
            try (PreparedStatement ps = koneksi.prepareStatement("delete from registrasi_stroke where id_registrasi=?")) {
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
            JOptionPane.showMessageDialog(this, "Anda tidak memiliki hak akses untuk Registrasi Stroke.");
            return;
        }
        if (idRegistrasi == 0) { JOptionPane.showMessageDialog(this, "Simpan registrasi dulu."); return; }
        int ok = JOptionPane.showConfirmDialog(this,
                "Kirim registrasi stroke ini ke SatuSehat?\n(Encounter kunjungan harus sudah terkirim lebih dulu.)",
                "Konfirmasi Kirim", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        final long id = idRegistrasi;
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        BtnKirim.setEnabled(false);
        new Thread(() -> {
            final String hasil = SatuSehatKirimStroke.kirim(id);
            javax.swing.SwingUtilities.invokeLater(() -> {
                setCursor(java.awt.Cursor.getDefaultCursor());
                BtnKirim.setEnabled(true);
                JTextArea ta = new JTextArea(hasil, 18, 70);
                ta.setEditable(false);
                ta.setCaretPosition(0);
                JOptionPane.showMessageDialog(this, new JScrollPane(ta),
                        "Hasil Kirim SatuSehat", JOptionPane.INFORMATION_MESSAGE);
            });
        }, "kirim-stroke").start();
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
        l.setBounds(x, y, 600, 20);
        p.add(l);
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
    private void setLebar(widget.Table tb, int[] w) {
        for (int i = 0; i < w.length; i++) tb.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
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
    private void setStrNull(PreparedStatement ps, int idx, String v) throws Exception {
        if (v == null || v.trim().isEmpty()) ps.setNull(idx, java.sql.Types.VARCHAR);
        else ps.setString(idx, v.trim());
    }
    /** Kosong -> NULL ("belum dinilai"), berbeda dari Tidak. */
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
