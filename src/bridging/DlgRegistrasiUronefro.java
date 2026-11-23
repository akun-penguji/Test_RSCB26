/*
 * Form Registrasi Uronefrologi per pasien (SatuSehat) — Fase 3.
 * Hand-built tanpa .form.
 *
 * Khas modul ini:
 *   - Penutupan episode punya TIGA alasan; "sembuh_batu" divalidasi usia >18
 *     sesuai spec, dan "hilang_kontak" biasanya berasal dari worklist.
 *   - Temuan batu membawa tiga komponen sekaligus (lokasi, hidronefrosis,
 *     keparahan) plus id pencitraan untuk Observation.derivedFrom.
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

public final class DlgRegistrasiUronefro extends javax.swing.JDialog {

    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();

    private String noRawat = "", noRkm = "", namaPasien = "";
    private long idRegistrasi = 0;

    private widget.InternalFrame internalFrame1;
    private widget.Label lblPasien;

    private widget.ComboBox cmbJalur, cmbStatus, cmbAlasanFinish, cmbVital;
    private widget.TextBox txtIcd10Kd, txtIcd10Nm, txtSebab;
    private widget.Tanggal DTPReg, DTPDiag, DTPFinish, DTPMeninggal;

    private widget.ComboBox cmbHtAda, cmbHtDerajat, cmbHtObat, cmbHtKontrol;
    private widget.ComboBox cmbDmAda, cmbDmObat, cmbDmKontrol;

    private final Map<String, javax.swing.JCheckBox> chkRiwayat = new LinkedHashMap<>();

    private widget.Table tbBatu;
    private DefaultTableModel tabBatu;
    private widget.ComboBox cmbBLokasi, cmbBHidro, cmbBKeparahan;
    private widget.TextBox txtBRefCitra;

    private widget.Table tbTerapi;
    private DefaultTableModel tabTerapi;
    private widget.ComboBox cmbTJenis;
    private widget.TextBox txtTKode, txtTDesk;

    private widget.TextBox txtCmNik, txtCmNama;
    private widget.Button BtnSimpan, BtnKirim, BtnHapus, BtnTutup;

    private static final String[] JALUR =
            {"ckd", "batu", "cakut", "glomerulonefritis", "nefropati_diabetik", "nefropati_hipertensi"};
    private static final String[] STATUS  = {"waitlist", "active", "finished"};
    private static final String[] ALASAN  = {"", "meninggal", "sembuh_batu", "hilang_kontak"};
    private static final String[] VITAL   = {"hidup", "meninggal", "tidak diketahui"};
    private static final String[] YATIDAK = {"", "Ya", "Tidak"};
    private static final String[] JENIS_TERAPI = {"hemodialisis", "peritoneal", "transplantasi", "lainnya"};

    public DlgRegistrasiUronefro(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle(":: Registrasi Uronefrologi (SatuSehat) ::");
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
                "::[ Registrasi Uronefrologi Satu Sehat ]::",
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
        judul(isi, "Diagnosis & Jalur Registry", LX, y); y += 24;
        addLabel(isi, "Jalur :", LX, y, 130, 22, f);
        cmbJalur = mkCombo(JALUR); cmbJalur.setBounds(FX, y, 200, 23); isi.add(cmbJalur);
        addLabel(isi, "Status Registry :", RX, y, 130, 22, f);
        cmbStatus = mkCombo(STATUS); cmbStatus.setBounds(FX2, y, 140, 23);
        cmbStatus.addActionListener(e -> perbaruiPanelTutup()); isi.add(cmbStatus);
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
        y += 32;

        // ---------- Penutupan episode ----------
        judul(isi, "Penutupan Episode (tiga alasan yang sah menurut spec)", LX, y); y += 24;
        addLabel(isi, "Alasan Tutup :", LX, y, 130, 22, f);
        cmbAlasanFinish = mkCombo(ALASAN); cmbAlasanFinish.setBounds(FX, y, 160, 23); isi.add(cmbAlasanFinish);
        addLabel(isi, "Tgl Tutup :", RX, y, 130, 22, f);
        DTPFinish = mkTgl(); DTPFinish.setBounds(FX2, y, 120, 23); isi.add(DTPFinish);
        y += 24;
        addLabel(isi, "meninggal · sembuh_batu (khusus usia >18) · hilang_kontak (>=5 thn tanpa kunjungan)",
                FX, y, 700, 20, f);
        y += 30;

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
        y += 32;

        // ---------- Riwayat ----------
        judul(isi, "Riwayat Penyakit (dikirim sebagai Condition tersendiri)", LX, y); y += 24;
        JPanel pnlRiw = new JPanel();
        pnlRiw.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 2));
        pnlRiw.setBounds(LX, y, 1080, 30);
        pnlRiw.setOpaque(false);
        for (Ref rf : muatRef("uro_riwayat")) {
            javax.swing.JCheckBox cb = new javax.swing.JCheckBox(rf.display);
            cb.setFont(f); cb.setOpaque(false);
            chkRiwayat.put(rf.kode, cb);
            pnlRiw.add(cb);
        }
        isi.add(pnlRiw);
        y += 36;

        // ---------- Temuan batu ----------
        judul(isi, "Temuan Batu Saluran Kemih", LX, y); y += 24;
        addLabel(isi, "Lokasi :", LX, y, 50, 22, f);
        cmbBLokasi = mkComboRef("uro_lokasi_batu"); cmbBLokasi.setBounds(LX + 55, y, 160, 23); isi.add(cmbBLokasi);
        addLabel(isi, "Hidronefrosis :", LX + 225, y, 90, 22, f);
        cmbBHidro = mkComboRef("uro_hidronefrosis"); cmbBHidro.setBounds(LX + 318, y, 180, 23); isi.add(cmbBHidro);
        addLabel(isi, "Keparahan :", LX + 508, y, 70, 22, f);
        cmbBKeparahan = mkComboRef("uro_keparahan_batu"); cmbBKeparahan.setBounds(LX + 580, y, 200, 23); isi.add(cmbBKeparahan);
        y += 26;
        addLabel(isi, "ID Pencitraan :", LX, y, 90, 22, f);
        txtBRefCitra = new widget.TextBox(); txtBRefCitra.setBounds(LX + 95, y, 300, 23); isi.add(txtBRefCitra);
        addLabel(isi, "(id Observation/DiagnosticReport radiologi yang SUDAH terkirim — untuk derivedFrom)",
                LX + 400, y, 520, 22, f);
        widget.Button bBatu = new widget.Button(); bBatu.setText("Tambah"); bBatu.setBounds(LX + 920, y, 75, 23);
        bBatu.addActionListener(e -> tambahBatu()); isi.add(bBatu);
        widget.Button bBatuH = new widget.Button(); bBatuH.setText("Hapus"); bBatuH.setBounds(LX + 1000, y, 70, 23);
        bBatuH.addActionListener(e -> hapusBatu()); isi.add(bBatuH);
        y += 28;

        tabBatu = new DefaultTableModel(null, new String[]{"Lokasi", "Hidronefrosis", "Keparahan", "ID Pencitraan", "Tanggal", "id"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbBatu = new widget.Table(); tbBatu.setModel(tabBatu);
        tbBatu.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setLebar(tbBatu, new int[]{150, 180, 200, 280, 140, 0});
        tbBatu.setDefaultRenderer(Object.class, new WarnaTable());
        widget.ScrollPane scB = new widget.ScrollPane(); scB.setViewportView(tbBatu);
        scB.setBounds(LX, y, 1080, 100); isi.add(scB);
        y += 110;

        // ---------- Terapi ----------
        judul(isi, "Terapi Pengganti Ginjal", LX, y); y += 24;
        addLabel(isi, "Jenis :", LX, y, 45, 22, f);
        cmbTJenis = mkCombo(JENIS_TERAPI); cmbTJenis.setBounds(LX + 48, y, 140, 23); isi.add(cmbTJenis);
        addLabel(isi, "Kode ICD-9 :", LX + 196, y, 80, 22, f);
        txtTKode = new widget.TextBox(); txtTKode.setBounds(LX + 280, y, 90, 23); isi.add(txtTKode);
        addLabel(isi, "Deskripsi :", LX + 380, y, 65, 22, f);
        txtTDesk = new widget.TextBox(); txtTDesk.setBounds(LX + 448, y, 450, 23); isi.add(txtTDesk);
        widget.Button bTer = new widget.Button(); bTer.setText("Tambah"); bTer.setBounds(LX + 920, y, 75, 23);
        bTer.addActionListener(e -> tambahTerapi()); isi.add(bTer);
        widget.Button bTerH = new widget.Button(); bTerH.setText("Hapus"); bTerH.setBounds(LX + 1000, y, 70, 23);
        bTerH.addActionListener(e -> hapusTerapi()); isi.add(bTerH);
        y += 28;

        tabTerapi = new DefaultTableModel(null, new String[]{"Jenis", "Kode ICD-9", "Deskripsi", "Tanggal", "id"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbTerapi = new widget.Table(); tbTerapi.setModel(tabTerapi);
        tbTerapi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        setLebar(tbTerapi, new int[]{140, 100, 500, 140, 0});
        tbTerapi.setDefaultRenderer(Object.class, new WarnaTable());
        widget.ScrollPane scT = new widget.ScrollPane(); scT.setViewportView(tbTerapi);
        scT.setBounds(LX, y, 1080, 90); isi.add(scT);
        y += 100;

        // ---------- Care manager & outcome ----------
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

        perbaruiPanelTutup();
    }

    private void perbaruiPanelTutup() {
        boolean finish = "finished".equals(String.valueOf(cmbStatus.getSelectedItem()));
        cmbAlasanFinish.setEnabled(finish);
        DTPFinish.setEnabled(finish);
    }

    // ==================================================================== Referensi

    private static final class Ref { String kode = "", display = ""; }

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
        } catch (Exception e) { System.out.println("Notif muatRef " + kelompok + " : " + e); }
        return out;
    }

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
        int idx = ((List<String>) prop).indexOf(nz(kodeLokal));
        c.setSelectedIndex(idx >= 0 ? idx : 0);
    }

    // ==================================================================== Muat

    private void prefillIcd10() {
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select dp.kd_penyakit, ifnull(pk.nm_penyakit,'') nm from diagnosa_pasien dp "
              + "left join penyakit pk on pk.kd_penyakit=dp.kd_penyakit "
              + "where dp.no_rawat=? and dp.kd_penyakit regexp '^N(0[0-5]|1[3-9]|2[0-3])' "
              + "order by dp.prioritas limit 1")) {
            ps.setString(1, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    txtIcd10Kd.setText(nz(rs.getString("kd_penyakit")));
                    txtIcd10Nm.setText(nz(rs.getString("nm")));
                }
            }
        } catch (Exception e) { System.out.println("Notif prefillIcd10 uronefro : " + e); }
    }

    private void muatRegistrasi() {
        idRegistrasi = 0;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select * from registrasi_uronefro where no_rkm_medis=? limit 1")) {
            ps.setString(1, noRkm);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idRegistrasi = rs.getLong("id_registrasi");
                    pilihNilai(cmbJalur, nz(rs.getString("jalur")));
                    pilihNilai(cmbStatus, nz(rs.getString("status")));
                    pilihNilai(cmbAlasanFinish, nz(rs.getString("alasan_finish")));
                    pilihNilai(cmbVital, nz(rs.getString("status_vital")));
                    if (!nz(rs.getString("kd_penyakit")).isEmpty()) txtIcd10Kd.setText(rs.getString("kd_penyakit"));
                    txtCmNik.setText(nz(rs.getString("kd_dokter")));
                    txtSebab.setText(nz(rs.getString("sebab_kematian")));
                    setTgl(DTPReg, nz(rs.getString("tgl_registrasi")));
                    setTgl(DTPDiag, nz(rs.getString("tgl_diagnosis")));
                    setTgl(DTPFinish, nz(rs.getString("tgl_finish")));
                    setTgl(DTPMeninggal, nz(rs.getString("tgl_meninggal")));
                    isiNamaCareManager();
                }
            }
        } catch (Exception e) { System.out.println("Notif muatRegistrasi uronefro : " + e); }
        perbaruiPanelTutup();
        muatFaktor();
        muatRiwayat();
        muatBatu();
        muatTerapi();
    }

    private void muatFaktor() {
        if (idRegistrasi == 0) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select * from registrasi_uronefro_faktor_risiko where id_registrasi=? and no_rawat=? "
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
                }
            }
        } catch (Exception e) { System.out.println("Notif muatFaktor uronefro : " + e); }
    }

    private void muatRiwayat() {
        for (javax.swing.JCheckBox cb : chkRiwayat.values()) cb.setSelected(false);
        if (idRegistrasi == 0) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select kode from registrasi_uronefro_riwayat where id_registrasi=?")) {
            ps.setLong(1, idRegistrasi);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    javax.swing.JCheckBox cb = chkRiwayat.get(nz(rs.getString("kode")));
                    if (cb != null) cb.setSelected(true);
                }
            }
        } catch (Exception e) { System.out.println("Notif muatRiwayat uronefro : " + e); }
    }

    private void muatBatu() {
        Valid.tabelKosong(tabBatu);
        if (idRegistrasi == 0) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select b.id_batu, ifnull(l.display,'') lokasi, ifnull(h.display,'') hidro, "
              + "ifnull(k.display,'') keparahan, ifnull(b.ref_pencitraan,'') ref_citra, b.tanggal "
              + "from registrasi_uronefro_batu b "
              + "left join ref_registry_kode l on l.kelompok='uro_lokasi_batu' and l.kode=b.lokasi "
              + "left join ref_registry_kode h on h.kelompok='uro_hidronefrosis' and h.kode=b.hidronefrosis "
              + "left join ref_registry_kode k on k.kelompok='uro_keparahan_batu' and k.kode=b.keparahan "
              + "where b.id_registrasi=? order by b.id_batu")) {
            ps.setLong(1, idRegistrasi);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String ref = nz(rs.getString("ref_citra"));
                    tabBatu.addRow(new Object[]{
                        rs.getString("lokasi"), rs.getString("hidro"), rs.getString("keparahan"),
                        ref.isEmpty() ? "(belum ada — derivedFrom dilewati)" : ref,
                        rs.getString("tanggal"), rs.getString("id_batu")
                    });
                }
            }
        } catch (Exception e) { System.out.println("Notif muatBatu : " + e); }
    }

    private void muatTerapi() {
        Valid.tabelKosong(tabTerapi);
        if (idRegistrasi == 0) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select id_terapi, jenis, ifnull(kode_icd9,'') kode, ifnull(deskripsi,'') deskripsi, tanggal "
              + "from registrasi_uronefro_terapi where id_registrasi=? order by id_terapi")) {
            ps.setLong(1, idRegistrasi);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabTerapi.addRow(new Object[]{
                        rs.getString("jenis"), rs.getString("kode"), rs.getString("deskripsi"),
                        rs.getString("tanggal"), rs.getString("id_terapi")
                    });
                }
            }
        } catch (Exception e) { System.out.println("Notif muatTerapi : " + e); }
    }

    // ==================================================================== Simpan

    private void simpan() {
        if (noRkm.isEmpty() || noRawat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pasien belum dipilih.");
            return;
        }
        String status = String.valueOf(cmbStatus.getSelectedItem());
        String alasan = String.valueOf(cmbAlasanFinish.getSelectedItem());
        if ("finished".equals(status) && alasan.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Status 'finished' wajib disertai alasan penutupan.\n"
                  + "Spec uronefrologi mengenal tiga alasan: meninggal, sembuh_batu, hilang_kontak.");
            return;
        }
        // Spec membatasi "sembuh dari batu" untuk pasien di atas 18 tahun.
        if ("sembuh_batu".equals(alasan)) {
            int umur = umurPasien();
            if (umur >= 0 && umur <= 18) {
                JOptionPane.showMessageDialog(this,
                        "Alasan 'sembuh_batu' hanya berlaku untuk pasien di atas 18 tahun.\n"
                      + "Usia pasien saat ini: " + umur + " tahun.");
                return;
            }
        }
        try {
            try (PreparedStatement ps = koneksi.prepareStatement(
                    "insert into registrasi_uronefro (no_rkm_medis,no_rawat,tgl_registrasi,jalur,kd_penyakit,"
                  + "tgl_diagnosis,status,alasan_finish,tgl_finish,kd_dokter,status_vital,tgl_meninggal,sebab_kematian) "
                  + "values (?,?,?,?,?,?,?,?,?,?,?,?,?) "
                  + "on duplicate key update no_rawat=values(no_rawat), jalur=values(jalur), "
                  + "kd_penyakit=values(kd_penyakit), tgl_diagnosis=values(tgl_diagnosis), status=values(status), "
                  + "alasan_finish=values(alasan_finish), tgl_finish=values(tgl_finish), kd_dokter=values(kd_dokter), "
                  + "status_vital=values(status_vital), tgl_meninggal=values(tgl_meninggal), "
                  + "sebab_kematian=values(sebab_kematian)")) {
                int i = 1;
                ps.setString(i++, noRkm);
                ps.setString(i++, noRawat);
                ps.setString(i++, Valid.SetTgl(DTPReg.getSelectedItem() + ""));
                ps.setString(i++, String.valueOf(cmbJalur.getSelectedItem()));
                setStrNull(ps, i++, txtIcd10Kd.getText().trim());
                setStrNull(ps, i++, Valid.SetTgl(DTPDiag.getSelectedItem() + ""));
                ps.setString(i++, status);
                setStrNull(ps, i++, alasan);
                setStrNull(ps, i++, "finished".equals(status) ? Valid.SetTgl(DTPFinish.getSelectedItem() + "") : "");
                setStrNull(ps, i++, txtCmNik.getText().trim());
                ps.setString(i++, String.valueOf(cmbVital.getSelectedItem()));
                setStrNull(ps, i++, "meninggal".equals(String.valueOf(cmbVital.getSelectedItem()))
                        ? Valid.SetTgl(DTPMeninggal.getSelectedItem() + "") : "");
                setStrNull(ps, i++, txtSebab.getText().trim());
                ps.executeUpdate();
            }
            if (idRegistrasi == 0) {
                idRegistrasi = Long.parseLong(scalar("select id_registrasi from registrasi_uronefro where no_rkm_medis=?", noRkm, "0"));
            }
            simpanFaktor();
            simpanRiwayat();
            JOptionPane.showMessageDialog(this, "Registrasi uronefrologi tersimpan (id " + idRegistrasi + ").");
            muatBatu(); muatTerapi();
        } catch (Exception e) {
            System.out.println("Notif simpan registrasi uronefro : " + e);
            JOptionPane.showMessageDialog(this, "Gagal menyimpan:\n" + e.getMessage());
        }
    }

    private int umurPasien() {
        String u = scalar("select timestampdiff(year, tgl_lahir, curdate()) from pasien where no_rkm_medis=?", noRkm, "");
        try { return Integer.parseInt(u); } catch (Exception e) { return -1; }
    }

    private void simpanFaktor() throws Exception {
        try (PreparedStatement d = koneksi.prepareStatement(
                "delete from registrasi_uronefro_faktor_risiko where id_registrasi=? and no_rawat=?")) {
            d.setLong(1, idRegistrasi); d.setString(2, noRawat); d.executeUpdate();
        }
        try (PreparedStatement ps = koneksi.prepareStatement(
                "insert into registrasi_uronefro_faktor_risiko (id_registrasi,no_rawat,tanggal,ht_ada,ht_derajat,"
              + "ht_dapat_obat,ht_terkontrol,dm_ada,dm_dapat_obat,dm_terkontrol,kd_dokter) "
              + "values (?,?,now(),?,?,?,?,?,?,?,?)")) {
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
            setStrNull(ps, i++, txtCmNik.getText().trim());
            ps.executeUpdate();
        }
    }

    private void simpanRiwayat() throws Exception {
        try (PreparedStatement d = koneksi.prepareStatement(
                "delete from registrasi_uronefro_riwayat where id_registrasi=?")) {
            d.setLong(1, idRegistrasi); d.executeUpdate();
        }
        for (Map.Entry<String, javax.swing.JCheckBox> e : chkRiwayat.entrySet()) {
            if (!e.getValue().isSelected()) continue;
            try (PreparedStatement ps = koneksi.prepareStatement(
                    "insert ignore into registrasi_uronefro_riwayat (id_registrasi,kode) values (?,?)")) {
                ps.setLong(1, idRegistrasi); ps.setString(2, e.getKey()); ps.executeUpdate();
            }
        }
    }

    private void tambahBatu() {
        if (idRegistrasi == 0) { JOptionPane.showMessageDialog(this, "Simpan registrasi dulu."); return; }
        if (kodeDari(cmbBLokasi).isEmpty()) { JOptionPane.showMessageDialog(this, "Lokasi batu belum dipilih."); return; }
        try (PreparedStatement ps = koneksi.prepareStatement(
                "insert into registrasi_uronefro_batu (id_registrasi,no_rawat,tanggal,lokasi,hidronefrosis,"
              + "keparahan,ref_pencitraan,kd_dokter) values (?,?,now(),?,?,?,?,?)")) {
            int i = 1;
            ps.setLong(i++, idRegistrasi);
            ps.setString(i++, noRawat);
            ps.setString(i++, kodeDari(cmbBLokasi));
            setStrNull(ps, i++, kodeDari(cmbBHidro));
            setStrNull(ps, i++, kodeDari(cmbBKeparahan));
            setStrNull(ps, i++, txtBRefCitra.getText().trim());
            setStrNull(ps, i++, txtCmNik.getText().trim());
            ps.executeUpdate();
            txtBRefCitra.setText("");
            muatBatu();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menambah temuan batu:\n" + e.getMessage());
        }
    }

    private void tambahTerapi() {
        if (idRegistrasi == 0) { JOptionPane.showMessageDialog(this, "Simpan registrasi dulu."); return; }
        String kode = txtTKode.getText().trim();
        if (kode.isEmpty()) { JOptionPane.showMessageDialog(this, "Kode ICD-9-CM belum diisi."); return; }
        try (PreparedStatement ps = koneksi.prepareStatement(
                "insert into registrasi_uronefro_terapi (id_registrasi,no_rawat,jenis,kode_icd9,deskripsi,tanggal,kd_dokter) "
              + "values (?,?,?,?,?,now(),?)")) {
            int i = 1;
            ps.setLong(i++, idRegistrasi);
            ps.setString(i++, noRawat);
            ps.setString(i++, String.valueOf(cmbTJenis.getSelectedItem()));
            ps.setString(i++, kode);
            setStrNull(ps, i++, txtTDesk.getText().trim());
            setStrNull(ps, i++, txtCmNik.getText().trim());
            ps.executeUpdate();
            txtTKode.setText(""); txtTDesk.setText("");
            muatTerapi();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menambah terapi:\n" + e.getMessage());
        }
    }

    private void hapusBatu()   { hapusBaris(tbBatu, tabBatu, 5, "registrasi_uronefro_batu", "id_batu", this::muatBatu); }
    private void hapusTerapi() { hapusBaris(tbTerapi, tabTerapi, 4, "registrasi_uronefro_terapi", "id_terapi", this::muatTerapi); }

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

    private void hapusRegistrasi() {
        if (idRegistrasi == 0) { JOptionPane.showMessageDialog(this, "Belum ada registrasi."); return; }
        int ok = JOptionPane.showConfirmDialog(this,
                "Hapus registrasi uronefrologi pasien ini beserta seluruh detailnya?\n"
              + "Resource yang sudah terkirim ke SatuSehat TIDAK ikut terhapus.",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            for (String t : new String[]{"registrasi_uronefro_terapi", "registrasi_uronefro_batu",
                    "registrasi_uronefro_riwayat", "registrasi_uronefro_faktor_risiko", "satu_sehat_uronefro_kirim"}) {
                try (PreparedStatement ps = koneksi.prepareStatement("delete from " + t + " where id_registrasi=?")) {
                    ps.setLong(1, idRegistrasi); ps.executeUpdate();
                }
            }
            try (PreparedStatement ps = koneksi.prepareStatement("delete from registrasi_uronefro where id_registrasi=?")) {
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
            JOptionPane.showMessageDialog(this, "Anda tidak memiliki hak akses untuk Registrasi Uronefrologi.");
            return;
        }
        if (idRegistrasi == 0) { JOptionPane.showMessageDialog(this, "Simpan registrasi dulu."); return; }
        int ok = JOptionPane.showConfirmDialog(this,
                "Kirim registrasi uronefrologi ini ke SatuSehat?\n(Encounter kunjungan harus sudah terkirim lebih dulu.)",
                "Konfirmasi Kirim", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        final long id = idRegistrasi;
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
            });
        }, "kirim-uronefro").start();
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
