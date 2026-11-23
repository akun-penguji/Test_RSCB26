package rekammedis;

import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import kepegawaian.DlgCariDokter;
import org.json.JSONObject;

/**
 * Formulir Kriteria Pasien Masuk Instalasi Rawat Intensif (RM.KEP.08.052.2022).
 * UI/UX mengikuti gaya Khanza (widget.*), 2 tab: Input Data & Data Input.
 * Prioritas 1-4 + skor EWS/qSofa/APACHE II. Disimpan blob JSON di
 * kriteria_masuk_icu_2022 (satu format dengan modul web simrscb).
 * Dibuka dari DlgIGD & DlgKamarInap. Cetak: rptKriteriaMasukICU2022.jasper.
 */
public final class RMKriteriaMasukICU2022 extends javax.swing.JDialog {

    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final DlgCariDokter dokter = new DlgCariDokter(null, false);
    private final SimpleDateFormat DFdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SimpleDateFormat DFd = new SimpleDateFormat("yyyy-MM-dd");
    private final Color BG = new Color(250, 255, 245);
    private final Color BORDER = new Color(240, 245, 235);

    private String noRawat = "";
    private String noRM = "";
    private String namaPasien = "";
    private String origTanggal = null;
    private int targetDokter = 0;

    private static final String[][] P1 = {
        {"p1_depresi_nafas", "Depresi Pernafasan"}, {"p1_butuh_ventilator", "Butuh Ventilator"},
        {"p1_pengobatan_titrasi", "Pengobatan Continue Tertitrasi"}, {"p1_sepsis_berat", "Pasien Sepsis Berat"},
        {"p1_gangguan_asam_basa", "Gangguan Keseimbangan Asam Basa dan Elektrolit Mengancam Nyawa"}};
    private static final String[][] P2 = {
        {"p2_pemantauan_canggih", "Memerlukan pelayanan pemantauan canggih di ICU"},
        {"p2_jantung_paru", "Menderita penyakit dasar jantung-paru"},
        {"p2_gagal_ginjal", "Gagal ginjal akut dan berat"}, {"p2_pembedahan_major", "Pembedahan Major"}};
    private static final String[][] P3 = {{"p3_kanker_lanjut", "Manifestasi kanker stadium lanjut"}};
    private static final String[][] P4 = {
        {"p4_dnr", "Kategori DNR"}, {"p4_mbo", "Kategori MBO"}, {"p4_vegetatif", "Vegetatif Permanen"}};
    // Bagian bawah mengikuti form lama: Kriteria Fisiologis (Tanda Vital/Lab/Radiologi/Klinis).
    private static final String[][] FIS_VITAL = {
        {"kriteria_fisiologis_tanda_vital_1", "Nadi < 40 atau > 150 (x/menit)"},
        {"kriteria_fisiologis_tanda_vital_2", "SBP < 80 mmHg Atau 20 mmHg Di Bawah SBP Pasien"},
        {"kriteria_fisiologis_tanda_vital_3", "MAP < 60 mmHg"},
        {"kriteria_fisiologis_tanda_vital_4", "DBP > 120 mmHg"},
        {"kriteria_fisiologis_tanda_vital_5", "R > 35 x/menit"}};
    private static final String[][] FIS_LAB = {
        {"kriteria_fisiologis_laborat_1", "Na < 110 meq/L Atau > 170 meq/L"},
        {"kriteria_fisiologis_laborat_2", "Ca > 15 mg/dl"},
        {"kriteria_fisiologis_laborat_3", "GDS > 800 mg/dl"},
        {"kriteria_fisiologis_laborat_4", "K < 2 meq/L Atau 7 meq/L"},
        {"kriteria_fisiologis_laborat_5", "PaO2 < 50 mmHg"},
        {"kriteria_fisiologis_laborat_6", "PH < 7,1 Atau 7,7"}};
    private static final String[][] FIS_RAD = {
        {"kriteria_fisiologis_radiologi_1", "Perdarahan Cerebrovaskuler, SAH, Atau Contusion Dengan Gangguan Kesadaran Atau Neurologi"},
        {"kriteria_fisiologis_radiologi_2", "Ruptur Organ Dalam, Kandung Kemih, Hati, Varices Esophagus Atau Uterus Dengan Gangguan Hemodinamik"}};
    private static final String[][] FIS_KLINIS = {
        {"kriteria_fisiologis_klinis_1", "Pupil Anisokor"}, {"kriteria_fisiologis_klinis_2", "Obstruksi Jalan Nafas"},
        {"kriteria_fisiologis_klinis_3", "Anuria"}, {"kriteria_fisiologis_klinis_4", "Kejang Berulang"},
        {"kriteria_fisiologis_klinis_5", "Tamponade Jantung"}, {"kriteria_fisiologis_klinis_6", "Coma"},
        {"kriteria_fisiologis_klinis_7", "Sianosis"}, {"kriteria_fisiologis_klinis_8", "Luka Bakar > 10 % BSA"}};

    private final Map<String, widget.CekBox> cek = new HashMap<>();
    private final widget.TextBox TNoRawat = new widget.TextBox();
    private final widget.TextBox TPasien = new widget.TextBox();
    private final widget.Tanggal Tanggal = new widget.Tanggal();
    private final widget.TextBox Jam = new widget.TextBox();
    private final widget.TextBox TAsalRuang = new widget.TextBox();
    private final widget.TextBox TDiagnosa = new widget.TextBox();
    private final widget.TextBox TDpjpKd = new widget.TextBox();
    private final widget.TextBox TDpjpNama = new widget.TextBox();
    private final widget.TextBox TKonsultanKd = new widget.TextBox();
    private final widget.TextBox TKonsultanNama = new widget.TextBox();
    private final widget.TextBox TDokterJagaKd = new widget.TextBox();
    private final widget.TextBox TDokterJagaNama = new widget.TextBox();

    private final widget.Tanggal DTPCari1 = new widget.Tanggal();
    private final widget.Tanggal DTPCari2 = new widget.Tanggal();
    private final widget.TextBox TCari = new widget.TextBox();
    private final DefaultTableModel tabMode = new DefaultTableModel(new Object[]{"No.Rawat", "Tanggal", "Petugas"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final widget.Table tbObat = new widget.Table();
    private final widget.TabPane TabRawat = new widget.TabPane();

    public RMKriteriaMasukICU2022(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Formulir Kriteria Pasien Masuk Instalasi Rawat Intensif (RM.KEP.08.052.2022)");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        buildUI();
        dokter.addWindowListener(new WindowAdapter() {
            @Override public void windowDeactivated(WindowEvent e) { ambilDokterTerpilih(); }
        });
    }

    private widget.Button btn(String text, char mn, String icon) {
        widget.Button b = new widget.Button();
        b.setText(text);
        b.setMnemonic(mn);
        try { b.setIcon(new javax.swing.ImageIcon(getClass().getResource(icon))); } catch (Exception e) {}
        return b;
    }

    private widget.CekBox cb(String key, String label) {
        widget.CekBox c = new widget.CekBox();
        c.setText(label);
        c.setBackground(BG);
        cek.put(key, c);
        return c;
    }

    private JPanel grup(String judul, String[][] items) {
        JPanel p = new JPanel();
        p.setBackground(BG);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDER), judul,
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 1, 11), new Color(50, 50, 50)));
        for (String[] it : items) {
            widget.CekBox c = cb(it[0], it[1]);
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
            p.add(c);
        }
        return p;
    }

    private JPanel baris(String label, Component... comps) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        p.setBackground(BG);
        JLabel l = new JLabel(label);
        l.setPreferredSize(new Dimension(150, 23));
        l.setFont(new Font("Tahoma", 0, 11));
        p.add(l);
        for (Component c : comps) { p.add(c); }
        return p;
    }

    private Component dim(Component c, int w, int h) { c.setPreferredSize(new Dimension(w, h)); return c; }

    private void buildUI() {
        widget.InternalFrame internalFrame1 = new widget.InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDER),
                "::[ Formulir Kriteria Pasien Masuk Instalasi Rawat Intensif (RM.KEP.08.052.2022) ]::",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout());

        // ---------- TAB INPUT ----------
        TNoRawat.setEditable(false); TPasien.setEditable(false); TDokterJagaNama.setEditable(false); TDokterJagaKd.setEditable(false);
        TDpjpNama.setEditable(false); TKonsultanNama.setEditable(false); TDpjpKd.setEditable(false); TKonsultanKd.setEditable(false);

        widget.Button bDpjp = btn("Cari", '1', "/picture/accept.png"); bDpjp.addActionListener(e -> cariDokter(1));
        widget.Button bKons = btn("Cari", '2', "/picture/accept.png"); bKons.addActionListener(e -> cariDokter(2));
        widget.Button bJaga = btn("Cari", '4', "/picture/accept.png"); bJaga.addActionListener(e -> cariDokter(3));

        JPanel form = new JPanel();
        form.setBackground(BG);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        form.add(baris("No. Rawat", dim(TNoRawat, 180, 23)));
        form.add(baris("Nama Pasien", dim(TPasien, 340, 23)));
        form.add(baris("Tanggal & Jam Dibuat", dim(Tanggal, 120, 23), dim(Jam, 90, 23)));
        form.add(baris("Asal Ruangan", dim(TAsalRuang, 260, 23)));
        form.add(baris("Diagnosa", dim(TDiagnosa, 430, 23)));
        form.add(baris("DPJP", dim(TDpjpKd, 70, 23), dim(TDpjpNama, 250, 23), bDpjp));
        form.add(baris("Dokter Konsultan", dim(TKonsultanKd, 70, 23), dim(TKonsultanNama, 250, 23), bKons));
        form.add(baris("Dokter Jaga IGD/Ruangan", dim(TDokterJagaKd, 70, 23), dim(TDokterJagaNama, 250, 23), bJaga));
        form.add(grup("I. Pasien Prioritas 1", P1));
        form.add(grup("II. Pasien Prioritas 2", P2));
        form.add(grup("III. Pasien Prioritas 3", P3));
        form.add(grup("IV. Pasien Prioritas 4", P4));
        form.add(grup("V. Kriteria Fisiologis — Tanda Vital", FIS_VITAL));
        form.add(grup("VI. Kriteria Fisiologis — Laboratorium", FIS_LAB));
        form.add(grup("VII. Kriteria Fisiologis — Radiologi", FIS_RAD));
        form.add(grup("VIII. Kriteria Fisiologis — Klinis", FIS_KLINIS));

        widget.ScrollPane spForm = new widget.ScrollPane();
        spForm.setViewportView(form);
        spForm.getVerticalScrollBar().setUnitIncrement(16);
        JPanel tabInput = new JPanel(new BorderLayout());
        tabInput.setBackground(BG);
        tabInput.add(spForm, BorderLayout.CENTER);

        // ---------- TAB DATA ----------
        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
        filter.setBackground(BG);
        widget.Button bCari = btn("Cari", '3', "/picture/accept.png"); bCari.addActionListener(e -> tampil());
        widget.Button bAll = btn("Semua", 'M', "/picture/Search-16x16.png"); bAll.addActionListener(e -> { TCari.setText(""); tampil(); });
        filter.add(new JLabel("Tanggal :")); filter.add(dim(DTPCari1, 120, 23));
        filter.add(new JLabel("s.d.")); filter.add(dim(DTPCari2, 120, 23));
        filter.add(new JLabel("Kata Kunci :")); filter.add(dim(TCari, 180, 23));
        filter.add(bCari); filter.add(bAll);
        tbObat.setModel(tabMode);
        tbObat.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbObat.getColumnModel().getColumn(0).setPreferredWidth(140);
        tbObat.getColumnModel().getColumn(1).setPreferredWidth(140);
        tbObat.getColumnModel().getColumn(2).setPreferredWidth(220);
        tbObat.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { if (e.getClickCount() == 2) { muatTerpilih(); } }
        });
        widget.ScrollPane spTab = new widget.ScrollPane();
        spTab.setViewportView(tbObat);
        JPanel tabData = new JPanel(new BorderLayout());
        tabData.setBackground(BG);
        tabData.add(filter, BorderLayout.NORTH);
        tabData.add(spTab, BorderLayout.CENTER);

        TabRawat.addTab("Input Data", tabInput);
        TabRawat.addTab("Data Input", tabData);

        // ---------- TOMBOL BAWAH ----------
        widget.Button BtnBaru = btn("Baru", 'B', "/picture/Cancel-2-16x16.png"); BtnBaru.addActionListener(e -> emptTeks());
        widget.Button BtnSimpan = btn("Simpan", 'S', "/picture/save-16x16.png"); BtnSimpan.addActionListener(e -> simpan());
        widget.Button BtnEdit = btn("Ganti", 'G', "/picture/inventaris.png"); BtnEdit.addActionListener(e -> muatTerpilih());
        widget.Button BtnHapus = btn("Hapus", 'H', "/picture/stop_f2.png"); BtnHapus.addActionListener(e -> hapusTerpilih());
        widget.Button BtnPrint = btn("Cetak", 'T', "/picture/b_print.png"); BtnPrint.addActionListener(e -> cetakTerpilih());
        widget.Button BtnKeluar = btn("Keluar", 'K', "/picture/exit.png"); BtnKeluar.addActionListener(e -> dispose());
        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 4));
        pBtn.setBackground(BG);
        pBtn.add(BtnBaru); pBtn.add(BtnSimpan); pBtn.add(BtnEdit); pBtn.add(BtnHapus); pBtn.add(BtnPrint); pBtn.add(BtnKeluar);

        internalFrame1.add(TabRawat, BorderLayout.CENTER);
        internalFrame1.add(pBtn, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(internalFrame1, BorderLayout.CENTER);
        setSize(900, 650);
    }

    private void cariDokter(int target) {
        targetDokter = target;
        dokter.emptTeks();
        dokter.isCek();
        dokter.setLocationRelativeTo(this);
        dokter.setVisible(true);
    }

    private void ambilDokterTerpilih() {
        try {
            javax.swing.JTable t = dokter.getTable();
            if (t.getSelectedRow() == -1) { return; }
            String kd = t.getValueAt(t.getSelectedRow(), 0).toString();
            String nm = t.getValueAt(t.getSelectedRow(), 1).toString();
            if (targetDokter == 1) { TDpjpKd.setText(kd); TDpjpNama.setText(nm); }
            else if (targetDokter == 2) { TKonsultanKd.setText(kd); TKonsultanNama.setText(nm); }
            else if (targetDokter == 3) { TDokterJagaKd.setText(kd); TDokterJagaNama.setText(nm); }
            targetDokter = 0;
        } catch (Exception e) {
            System.out.println("Notif pilih dokter : " + e);
        }
    }

    public void isCek() {
        TDokterJagaKd.setText(akses.getkode());
        TDokterJagaNama.setText(akses.getnamauser());
    }

    public void emptTeks() {
        origTanggal = null;
        Date now = new Date();
        Tanggal.setDate(now);
        Jam.setText(new SimpleDateFormat("HH:mm:ss").format(now));
        TAsalRuang.setText(""); TDiagnosa.setText("");
        TDpjpKd.setText(""); TDpjpNama.setText(""); TKonsultanKd.setText(""); TKonsultanNama.setText("");
        TDokterJagaKd.setText(akses.getkode()); TDokterJagaNama.setText(akses.getnamauser());
        for (widget.CekBox c : cek.values()) { c.setSelected(false); }
        tbObat.clearSelection();
        TabRawat.setSelectedIndex(0);
    }

    public void setNoRm(String norwt, Date tgl) {
        this.noRawat = norwt == null ? "" : norwt.trim();
        TNoRawat.setText(this.noRawat);
        if (tgl != null) { DTPCari1.setDate(tgl); DTPCari2.setDate(tgl); }
        muatPasien();
        emptTeks();
        tampil();
    }

    private void muatPasien() {
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select pasien.nm_pasien,pasien.no_rkm_medis from reg_periksa "
              + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis where reg_periksa.no_rawat=?")) {
            ps.setString(1, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    namaPasien = rs.getString("nm_pasien");
                    noRM = rs.getString("no_rkm_medis");
                    TPasien.setText(noRM + " - " + namaPasien);
                }
            }
        } catch (Exception e) {
            System.out.println("Notif muat pasien : " + e);
        }
    }

    /** Isi tabel Data Input (dipanggil juga oleh tombol Cari/Semua). */
    public void tampil() {
        tabMode.setRowCount(0);
        String key = "%" + TCari.getText().trim() + "%";
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select k.tanggal, ifnull(pg.nama,k.nip) petugas from kriteria_masuk_icu_2022 k "
              + "left join pegawai pg on pg.nik=k.nip where k.no_rawat=? "
              + "and (?='%%' or ifnull(pg.nama,k.nip) like ?) order by k.id desc")) {
            ps.setString(1, noRawat);
            ps.setString(2, key);
            ps.setString(3, key);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabMode.addRow(new Object[]{noRawat, rs.getString("tanggal"), rs.getString("petugas")});
                }
            }
        } catch (Exception e) {
            System.out.println("Notif tampil : " + e);
        }
    }

    private String s(widget.TextBox t) { return t.getText() == null ? "" : t.getText().trim(); }
    private String yn(String key) { widget.CekBox c = cek.get(key); return (c != null && c.isSelected()) ? "Ya" : "Tidak"; }

    private String tglInput() {
        String d;
        try { d = DFd.format(Tanggal.getDate()); } catch (Exception e) { d = DFd.format(new Date()); }
        String j = Jam.getText() == null ? "" : Jam.getText().trim();
        if (j.isEmpty()) { j = "00:00:00"; }
        else if (j.length() == 5) { j = j + ":00"; }
        return d + " " + j;
    }

    private String buildJson() {
        JSONObject o = new JSONObject();
        try {
            o.put("asal_ruangan", s(TAsalRuang));
            o.put("diagnosa", s(TDiagnosa));
            o.put("dpjp_kd", s(TDpjpKd)); o.put("dpjp_nama", s(TDpjpNama));
            o.put("konsultan_kd", s(TKonsultanKd)); o.put("konsultan_nama", s(TKonsultanNama));
            o.put("dokter_jaga_kd", s(TDokterJagaKd).isEmpty() ? akses.getkode() : s(TDokterJagaKd));
            o.put("dokter_jaga_nama", s(TDokterJagaNama).isEmpty() ? akses.getnamauser() : s(TDokterJagaNama));
            for (String[][] grp : new String[][][]{P1, P2, P3, P4, FIS_VITAL, FIS_LAB, FIS_RAD, FIS_KLINIS}) {
                for (String[] it : grp) { o.put(it[0], yn(it[0])); }
            }
        } catch (Exception e) {
            System.out.println("Notif build json kriteria icu : " + e);
        }
        return o.toString();
    }

    private void isiDariJson(String json) {
        try {
            JSONObject o = new JSONObject(json == null || json.isEmpty() ? "{}" : json);
            TAsalRuang.setText(o.optString("asal_ruangan", ""));
            TDiagnosa.setText(o.optString("diagnosa", ""));
            TDpjpKd.setText(o.optString("dpjp_kd", "")); TDpjpNama.setText(o.optString("dpjp_nama", ""));
            TKonsultanKd.setText(o.optString("konsultan_kd", "")); TKonsultanNama.setText(o.optString("konsultan_nama", ""));
            TDokterJagaKd.setText(o.optString("dokter_jaga_kd", "")); TDokterJagaNama.setText(o.optString("dokter_jaga_nama", ""));
            for (String[][] grp : new String[][][]{P1, P2, P3, P4, FIS_VITAL, FIS_LAB, FIS_RAD, FIS_KLINIS}) {
                for (String[] it : grp) {
                    widget.CekBox c = cek.get(it[0]);
                    if (c != null) { c.setSelected("Ya".equalsIgnoreCase(o.optString(it[0], "Tidak"))); }
                }
            }
        } catch (Exception e) {
            System.out.println("Notif isi form : " + e);
        }
    }

    private void simpan() {
        if (noRawat.isEmpty()) { JOptionPane.showMessageDialog(this, "No. Rawat belum diisi."); return; }
        String tgl = tglInput();
        String data = buildJson();
        try {
            if (origTanggal == null) {
                try (PreparedStatement ps = koneksi.prepareStatement(
                        "insert into kriteria_masuk_icu_2022 (no_rawat,tanggal,nip,data,created_at) values (?,?,?,?,now())")) {
                    ps.setString(1, noRawat); ps.setString(2, tgl); ps.setString(3, akses.getkode()); ps.setString(4, data);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = koneksi.prepareStatement(
                        "update kriteria_masuk_icu_2022 set tanggal=?, data=? where no_rawat=? and tanggal=?")) {
                    ps.setString(1, tgl); ps.setString(2, data); ps.setString(3, noRawat); ps.setString(4, origTanggal);
                    ps.executeUpdate();
                }
            }
            JOptionPane.showMessageDialog(this, "Data kriteria masuk ICU tersimpan.");
            origTanggal = tgl;
            tampil();
        } catch (Exception e) {
            System.out.println("Notif simpan kriteria icu : " + e);
            JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + e);
        }
    }

    private String tglTerpilih() {
        int r = tbObat.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Pilih dulu data pada tab Data Input."); return null; }
        return tabMode.getValueAt(r, 1).toString();
    }

    private void muatTerpilih() {
        String tgl = tglTerpilih(); if (tgl == null) { return; }
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select data from kriteria_masuk_icu_2022 where no_rawat=? and tanggal=? order by id desc limit 1")) {
            ps.setString(1, noRawat); ps.setString(2, tgl);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    try {
                        Tanggal.setDate(DFdt.parse(tgl));
                        Jam.setText(tgl.length() >= 19 ? tgl.substring(11, 19) : "");
                    } catch (Exception ex) { /* biarkan */ }
                    origTanggal = tgl;
                    isiDariJson(rs.getString("data"));
                    TabRawat.setSelectedIndex(0);
                }
            }
        } catch (Exception e) {
            System.out.println("Notif muat kriteria icu : " + e);
        }
    }

    private void hapusTerpilih() {
        String tgl = tglTerpilih(); if (tgl == null) { return; }
        if (JOptionPane.showConfirmDialog(this, "Hapus data kriteria masuk ICU ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) { return; }
        try (PreparedStatement ps = koneksi.prepareStatement(
                "delete from kriteria_masuk_icu_2022 where no_rawat=? and tanggal=?")) {
            ps.setString(1, noRawat); ps.setString(2, tgl);
            ps.executeUpdate();
            emptTeks(); tampil();
        } catch (Exception e) {
            System.out.println("Notif hapus kriteria icu : " + e);
        }
    }

    private void cetakTerpilih() {
        String tgl = tglTerpilih(); if (tgl == null) { return; }
        Map<String, Object> param = new HashMap<>();
        try {
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        } catch (Exception e) { /* abaikan */ }
        String je = "JSON_UNQUOTE(JSON_EXTRACT(k.data,";
        String qry =
            "select k.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, pasien.tgl_lahir, pasien.jk, k.tanggal, "
          + je + "'$.asal_ruangan')) asal_ruangan, " + je + "'$.diagnosa')) diagnosa, "
          + je + "'$.dpjp_nama')) dpjp, " + je + "'$.konsultan_nama')) dokter_konsultan, "
          + je + "'$.dokter_jaga_nama')) dokter_jaga, "
          + je + "'$.p1_depresi_nafas')) p1_depresi_nafas, " + je + "'$.p1_butuh_ventilator')) p1_butuh_ventilator, "
          + je + "'$.p1_pengobatan_titrasi')) p1_pengobatan_titrasi, " + je + "'$.p1_sepsis_berat')) p1_sepsis_berat, "
          + je + "'$.p1_gangguan_asam_basa')) p1_gangguan_asam_basa, "
          + je + "'$.p2_pemantauan_canggih')) p2_pemantauan_canggih, " + je + "'$.p2_jantung_paru')) p2_jantung_paru, "
          + je + "'$.p2_gagal_ginjal')) p2_gagal_ginjal, " + je + "'$.p2_pembedahan_major')) p2_pembedahan_major, "
          + je + "'$.p3_kanker_lanjut')) p3_kanker_lanjut, "
          + je + "'$.p4_dnr')) p4_dnr, " + je + "'$.p4_mbo')) p4_mbo, " + je + "'$.p4_vegetatif')) p4_vegetatif, "
          + je + "'$.kriteria_fisiologis_tanda_vital_1')) fv1, " + je + "'$.kriteria_fisiologis_tanda_vital_2')) fv2, "
          + je + "'$.kriteria_fisiologis_tanda_vital_3')) fv3, " + je + "'$.kriteria_fisiologis_tanda_vital_4')) fv4, "
          + je + "'$.kriteria_fisiologis_tanda_vital_5')) fv5, "
          + je + "'$.kriteria_fisiologis_laborat_1')) fl1, " + je + "'$.kriteria_fisiologis_laborat_2')) fl2, "
          + je + "'$.kriteria_fisiologis_laborat_3')) fl3, " + je + "'$.kriteria_fisiologis_laborat_4')) fl4, "
          + je + "'$.kriteria_fisiologis_laborat_5')) fl5, " + je + "'$.kriteria_fisiologis_laborat_6')) fl6, "
          + je + "'$.kriteria_fisiologis_radiologi_1')) fr1, " + je + "'$.kriteria_fisiologis_radiologi_2')) fr2, "
          + je + "'$.kriteria_fisiologis_klinis_1')) fk1, " + je + "'$.kriteria_fisiologis_klinis_2')) fk2, "
          + je + "'$.kriteria_fisiologis_klinis_3')) fk3, " + je + "'$.kriteria_fisiologis_klinis_4')) fk4, "
          + je + "'$.kriteria_fisiologis_klinis_5')) fk5, " + je + "'$.kriteria_fisiologis_klinis_6')) fk6, "
          + je + "'$.kriteria_fisiologis_klinis_7')) fk7, " + je + "'$.kriteria_fisiologis_klinis_8')) fk8 "
          + "from kriteria_masuk_icu_2022 k "
          + "inner join reg_periksa on k.no_rawat=reg_periksa.no_rawat "
          + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
          + "where k.no_rawat='" + noRawat + "' and k.tanggal='" + tgl + "'";
        Valid.MyReportqry("rptKriteriaMasukICU2022.jasper", "report",
                "::[ Formulir Kriteria Pasien Masuk Instalasi Rawat Intensif ]::", qry, param);
    }
}
