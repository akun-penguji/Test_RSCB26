/*
 * RMPengkajianKeperawatanICU — Pengkajian Keperawatan Pasien ICU
 * (Comprehensive Admission Assessment, form RM.012). Metadata-driven Swing.
 * 2 tab: "Input" (form seksi A..T) & "Data Input" (tabel riwayat + klik-kanan Cetak).
 * Tombol CRUD gaya Khanza. Isian diserialisasi JSON (gson) ke tabel
 * pengkajian_keperawatan_icu — tabel yang SAMA dengan e-Nakes (data menyatu).
 * Hak akses akses.getchecklist_kriteria_masuk_icu(). Cetak Jasper via datasource.
 */
package rekammedis;

import com.google.gson.Gson;
import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariPetugas;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

public final class RMPengkajianKeperawatanICU extends javax.swing.JDialog {

    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);
    private final Gson gson = new Gson();
    private long currentId = 0;   // record terpilih (0 = baru)

    // Chrome
    private widget.InternalFrame internalFrame1;
    private javax.swing.JTabbedPane tabs;
    private widget.TextBox TNoRw, TNoRM, TPasien, Jk, TglLahir, KdPetugas, NmPetugas;
    private widget.Tanggal Tanggal;
    private JButton btnPetugas;
    private widget.Button BtnSimpan, BtnBatal, BtnEdit, BtnHapus, BtnKeluar;
    // Data tab
    private widget.Table tbData;
    private DefaultTableModel tabModel;
    private javax.swing.JPopupMenu popupCetak;
    private javax.swing.JMenuItem MnCetak;

    // Dinamis
    private final Map<String, JTextField> teks = new LinkedHashMap<>();
    private final Map<String, JTextArea> areas = new LinkedHashMap<>();
    private final Map<String, JComboBox<String>> combos = new LinkedHashMap<>();
    private final Map<String, List<JCheckBox>> multis = new LinkedHashMap<>();
    private DefaultTableModel obatModel, masalahModel;
    private javax.swing.JTable tbObat, tbMasalah;
    private final List<JComboBox<String>> dcYa = new ArrayList<>();
    private final List<JTextField> dcKet = new ArrayList<>();
    private JLabel giziLabel;

    private static final String[] YT = {"", "Ya", "Tidak"};
    private static final String[] DISCHARGE = {
        "Perlu pelayanan home care", "Perlu pemasangan implant", "Penggunaan alat bantu", "Telah dilakukan pemesanan alat",
        "Dirujuk komunitas tertentu", "Dirujuk ke tim terapis", "Dirujuk ke ahli gizi", "Perlu edukasi pasien / keluarga", "Perlu adanya inform concent"
    };

    private List<String[]> skema() {
        List<String[]> s = new ArrayList<>();
        sec(s, "Riwayat Kesehatan Yang Lalu");
        txt(s, "riw_kondisi", "Kondisi kesehatan sebelumnya"); txt(s, "riw_dirawat", "Pernah dirawat kasus sama");
        txt(s, "riw_alergi", "Riwayat Alergi"); txt(s, "riw_psikiater", "Riwayat masalah emosi/psikiater");
        sec(s, "A. Sistem Pernafasan");
        sel(s, "nafas_jalan", "Jalan nafas", "Bersih", "Sumbatan");
        mul(s, "nafas_sumbatan", "Sumbatan berupa", "Sputum", "Darah");
        txt(s, "nafas_rr", "RR (x/menit)");
        sel(s, "nafas_obat_bantu", "Obat bantu nafas", "Ya", "Tidak");
        sel(s, "nafas_ett", "Terpasang ETT", "Ya", "Tidak");
        sel(s, "nafas_ventilator", "Terpasang Ventilator", "Ya", "Tidak");
        txt(s, "vent_tv", "Ventilator: TV"); txt(s, "vent_rr", "Ventilator: RR"); txt(s, "vent_peep", "Ventilator: PEEP");
        txt(s, "vent_ie", "Ventilator: IE"); txt(s, "vent_fio2", "Ventilator: FiO2");
        sel(s, "nafas_irama", "Irama", "Teratur", "Tidak teratur");
        sel(s, "nafas_kedalaman", "Kedalaman", "Dalam", "Tidak dalam");
        sel(s, "nafas_sputum", "Sputum", "Putih", "Kuning", "Hijau");
        sel(s, "nafas_konsistensi", "Konsistensi", "Kental", "Tidak kental");
        sel(s, "nafas_suara", "Suara nafas", "Ronchi", "Wheezing", "Vesikuler");
        sec(s, "B. Sistem Kardiovaskuler");
        txt(s, "kv_nadi", "Nadi (x/menit)"); txt(s, "kv_td", "Tekanan darah");
        sel(s, "kv_pulsasi", "Pulsasi", "Kuat", "Lemah");
        sel(s, "kv_jvp", "Distensi vena jugularis", "Ada", "Tidak ada");
        sel(s, "kv_akral", "Akral", "Hangat", "Dingin");
        sel(s, "kv_warna", "Warna kulit", "Kemerahan", "Pucat", "Cyanosis");
        txt(s, "kv_crt", "Waktu pengisian kapiler (CRT)");
        sel(s, "kv_irama", "Irama jantung", "Teratur", "Tidak teratur");
        sel(s, "kv_nyeri_dada", "Nyeri dada", "Ya", "Tidak");
        sel(s, "kv_bunyi", "Bunyi jantung", "Murmur", "Gallop");
        sel(s, "kv_perdarahan", "Perdarahan", "Ya", "Tidak");
        txt(s, "kv_area", "Area perdarahan"); txt(s, "kv_jml", "Jumlah (cc/jam)");
        sec(s, "C. Sistem Syaraf Pusat");
        txt(s, "sp_kesadaran", "Tingkat kesadaran");
        sel(s, "sp_pupil", "Pupil", "Isokor", "Anisokor", "Miosis", "Midriasis");
        txt(s, "sp_gcs_e", "GCS Eye"); txt(s, "sp_gcs_m", "GCS Motorik"); txt(s, "sp_gcs_v", "GCS Verbal");
        txt(s, "sp_otot", "Kekuatan otot"); txt(s, "sp_koordinasi", "Koordinasi motorik");
        sec(s, "D. Sistem Gastrointestinal");
        sel(s, "gi_distensi", "Distensi", "Ya", "Tidak"); txt(s, "gi_lingkar", "Lingkar perut (cm)");
        sel(s, "gi_nyeri", "Nyeri", "Ya", "Tidak"); sel(s, "gi_peristaltik", "Peristaltik", "Ya", "Tidak");
        sel(s, "gi_defekasi", "Defekasi", "Normal", "Tidak normal"); txt(s, "gi_defekasi_ket", "Ket. defekasi");
        sec(s, "E. Sistem Perkemihan");
        sel(s, "km_warna", "Warna", "Bening", "Kuning", "Merah", "Kecoklatan");
        sel(s, "km_nyeri", "Nyeri", "Ya", "Tidak"); sel(s, "km_distensi", "Distensi", "Ya", "Tidak");
        sel(s, "km_ctheter", "Penggunaan ctheter urine", "Ya", "Tidak"); txt(s, "km_jml", "Jumlah urine (cc/jam)");
        sec(s, "F. Obstetri Ginekologi");
        sel(s, "obs_hamil", "Hamil", "Ya", "Tidak"); txt(s, "obs_hpht", "HPHT"); txt(s, "obs_keluhan", "Keluhan");
        sec(s, "G. Muskuloskeletal & Integumen");
        sel(s, "ms_tugor", "Tugor kulit", "Elastis", "Tidak elastis");
        sel(s, "ms_luka", "Terdapat luka", "Ya", "Tidak"); txt(s, "ms_luka_lok", "Lokasi luka");
        sel(s, "ms_fraktur", "Fraktur", "Ya", "Tidak"); txt(s, "ms_fraktur_dimana", "Fraktur dimana");
        sel(s, "ms_sulit", "Kesulitan bergerak", "Ya", "Tidak");
        sel(s, "ms_alat", "Penggunaan alat bantu", "Ya", "Tidak"); txt(s, "ms_alat_ket", "Alat bantu (sebutkan)");
        txt(s, "ms_lesi", "Lokasi luka / lesi lain");
        sec(s, "H. Sistem Hematologi");
        mul(s, "hema", "Perdarahan", "Gusi", "Nasal", "Pethecia", "Echimosis"); txt(s, "hema_lain", "Lain-lain");
        sec(s, "I. Psikososial");
        txt(s, "ps_komunikasi", "Komunikasi yang digunakan");
        sel(s, "ps_koping", "Koping", "Menerima", "Menolak", "Kehilangan", "Mandiri");
        sel(s, "ps_afek", "Afek", "Gelisah", "Insomnia", "Tegang", "Depresi", "Apatis");
        sel(s, "ps_hdr", "HDR", "Emosional", "Tidak berdaya", "Rasa bersalah");
        sel(s, "ps_persepsi", "Persepsi penyakit", "Menerima", "Menolak");
        sel(s, "ps_mengerti", "Mengerti tentang penyakit", "Berat", "Sedang", "Ringan");
        sel(s, "ps_hubungan", "Hubungan keluarga", "Harmonis", "Tidak harmonis"); txt(s, "ps_orang_dekat", "Orang terdekat");
        sec(s, "J. Spiritual");
        txt(s, "sp_agama", "Agama"); txt(s, "sp_kebiasaan", "Kebiasaan mengatasi stress dari sisi spiritual");
        sec(s, "K. Alat Invasif Yang Digunakan");
        sel(s, "ai_wsd", "Drain/WSD", "Ya", "Tidak"); txt(s, "ai_wsd_warna", "WSD warna"); txt(s, "ai_wsd_jml", "WSD jumlah (cc/jam)");
        sel(s, "ai_drainkepala", "Drain kepala", "Ya", "Tidak"); txt(s, "ai_dk_warna", "Drain kepala warna"); txt(s, "ai_dk_jml", "Drain kepala jumlah (cc/jam)");
        sel(s, "ai_ivline", "IV Line", "Ya", "Tidak"); txt(s, "ai_lain", "Lain-lain");
        sec(s, "L. Daftar Obat"); s.add(new String[]{"OBAT"});
        sec(s, "M. Skrining Gizi (MST)"); s.add(new String[]{"GIZI"});
        sec(s, "N. Risiko Cedera / Jatuh");
        sel(s, "jatuh", "Risiko cedera/jatuh", "Tidak", "Ya (isi form pencegahan jatuh)");
        sec(s, "O. Status Fungsional");
        sel(s, "fungsional", "Aktifitas dan mobilitas", "Mandiri", "Perlu bantuan"); txt(s, "fungsional_ket", "Perlu bantuan (sebutkan)");
        sec(s, "P. Pengkajian Nyeri");
        sel(s, "nyeri", "Nyeri / tidak nyaman", "Ya", "Tidak"); txt(s, "nyeri_lokasi", "Nyeri di"); txt(s, "nyeri_score", "Score (0-10)");
        sel(s, "nyeri_skala", "Skala digunakan", "Numeric", "Wong Baker", "CRIES", "FLACC", "COMFORT", "CPOT", "BPS"); txt(s, "nyeri_ai", "Nilai A/I");
        mul(s, "nyeri_pengaruh", "Nyeri mempengaruhi", "Tidur", "Aktifitas fisik", "Emosi", "Nafsu makan", "Konsentrasi");
        sec(s, "Q. Hasil Pemeriksaan Penunjang");
        txt(s, "pj_elektrolit", "Elektrolit");
        txt(s, "agda_ph", "AGDA: PH"); txt(s, "agda_paco2", "AGDA: PaCO2"); txt(s, "agda_pao2", "AGDA: PaO2");
        txt(s, "agda_hco3", "AGDA: HCO3"); txt(s, "agda_be", "AGDA: BE"); txt(s, "agda_sao2", "AGDA: SaO2");
        txt(s, "pj_hema", "Hematologi"); txt(s, "pj_hati", "Fungsi Hati"); txt(s, "pj_urine", "Urine (Ur/Creatinin)");
        txt(s, "pj_aptt", "Faktor pembekuan APTT/PTT"); txt(s, "pj_thoraks", "Foto Thoraks"); txt(s, "pj_ekg", "EKG"); txt(s, "pj_dll", "Dll.");
        sec(s, "R. Kebutuhan Edukasi");
        mul(s, "edu_hambatan", "Hambatan pembelajaran", "Pendengaran", "Penglihatan", "Kognitif", "Fisik", "Budaya", "Agama", "Emosi", "Bahasa");
        txt(s, "edu_hambatan_lain", "Hambatan lainnya"); txt(s, "edu_kebutuhan", "Kebutuhan pembelajaran pasien");
        mul(s, "edu_diagnosa", "Diagnosa mengenai penyakit", "Obat-obatan", "Diet dan Nutrisi", "Tindakan keperawatan", "Rehabilitasi", "Manajemen Nyeri");
        sec(s, "S. Discharge Planning"); s.add(new String[]{"DISCHARGE"});
        sec(s, "T. Daftar Masalah Keperawatan"); s.add(new String[]{"MASALAH"});
        return s;
    }
    private void sec(List<String[]> s, String t) { s.add(new String[]{"SEC", t}); }
    private void txt(List<String[]> s, String k, String l) { s.add(new String[]{"TXT", k, l}); }
    private void sel(List<String[]> s, String k, String l, String... o) {
        String[] a = new String[3 + o.length]; a[0] = "SEL"; a[1] = k; a[2] = l; System.arraycopy(o, 0, a, 3, o.length); s.add(a);
    }
    private void mul(List<String[]> s, String k, String l, String... o) {
        String[] a = new String[3 + o.length]; a[0] = "MULTI"; a[1] = k; a[2] = l; System.arraycopy(o, 0, a, 3, o.length); s.add(a);
    }

    public RMPengkajianKeperawatanICU(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Pengkajian Keperawatan Pasien ICU (Comprehensive Admission Assessment)");
        initComponents();
        setSize(760, 720);
        setLocationRelativeTo(null);

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        KdPetugas.setDocument(new batasInput((byte) 20).getKata(KdPetugas));
        petugas.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override public void windowClosed(WindowEvent e) {
                if (petugas.getTable().getSelectedRow() != -1) {
                    KdPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                    NmPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                }
            }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });
    }

    private JLabel lbl(String t) { JLabel l = new JLabel(t); l.setFont(new java.awt.Font("Tahoma", 0, 11)); return l; }

    private widget.Button tombol(String txt, char mn, String ikon) {
        widget.Button b = new widget.Button(); b.setText(txt); b.setMnemonic(mn);
        try { b.setIcon(new javax.swing.ImageIcon(getClass().getResource(ikon))); } catch (Exception e) { }
        return b;
    }

    private void initComponents() {
        internalFrame1 = new widget.InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
            "::[ Pengkajian Keperawatan Pasien ICU ]::"));
        internalFrame1.setLayout(new BorderLayout(2, 2));
        internalFrame1.setVisible(true);
        getContentPane().add(internalFrame1, BorderLayout.CENTER);

        // Header pasien + petugas (di atas, dipakai tab Input)
        JPanel head = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 3));
        TNoRw = new widget.TextBox(); TNoRw.setPreferredSize(new Dimension(120, 23));
        TNoRM = new widget.TextBox(); TNoRM.setPreferredSize(new Dimension(80, 23)); TNoRM.setEditable(false);
        TPasien = new widget.TextBox(); TPasien.setPreferredSize(new Dimension(160, 23)); TPasien.setEditable(false);
        Jk = new widget.TextBox(); Jk.setPreferredSize(new Dimension(70, 23)); Jk.setEditable(false);
        TglLahir = new widget.TextBox(); TglLahir.setPreferredSize(new Dimension(90, 23)); TglLahir.setEditable(false);
        Tanggal = new widget.Tanggal(); Tanggal.setDisplayFormat("dd-MM-yyyy"); Tanggal.setDate(new Date());
        KdPetugas = new widget.TextBox(); KdPetugas.setPreferredSize(new Dimension(90, 23));
        NmPetugas = new widget.TextBox(); NmPetugas.setPreferredSize(new Dimension(150, 23)); NmPetugas.setEditable(false);
        btnPetugas = new JButton("...");
        btnPetugas.addActionListener(e -> { petugas.emptTeks(); petugas.isCek(); petugas.setVisible(true); });
        head.add(lbl("No.Rawat")); head.add(TNoRw);
        head.add(lbl("RM")); head.add(TNoRM);
        head.add(lbl("Nama")); head.add(TPasien);
        head.add(Jk); head.add(TglLahir);
        head.add(lbl("Tgl")); head.add(Tanggal);
        head.add(lbl("Petugas")); head.add(KdPetugas); head.add(btnPetugas); head.add(NmPetugas);

        // Form dinamis
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(new java.awt.Color(250, 255, 245));
        JPanel cur = null;
        for (String[] it : skema()) {
            switch (it[0]) {
                case "SEC":
                    cur = new JPanel(); cur.setLayout(new BoxLayout(cur, BoxLayout.Y_AXIS));
                    cur.setBorder(BorderFactory.createTitledBorder(it[1]));
                    cur.setBackground(new java.awt.Color(255, 255, 255));
                    form.add(cur); break;
                case "TXT": { JTextField t = new JTextField(); teks.put(it[1], t); cur.add(baris(it[2], t)); break; }
                case "AREA": { JTextArea t = new JTextArea(2, 20); t.setLineWrap(true); areas.put(it[1], t);
                    cur.add(baris(it[2], new JScrollPane(t))); break; }
                case "SEL": { JComboBox<String> c = new JComboBox<>(); c.addItem("");
                    for (int i = 3; i < it.length; i++) c.addItem(it[i]); combos.put(it[1], c); cur.add(baris(it[2], c)); break; }
                case "MULTI": {
                    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0)); row.setOpaque(false);
                    List<JCheckBox> list = new ArrayList<>();
                    for (int i = 3; i < it.length; i++) { JCheckBox cb = new JCheckBox(it[i]); cb.setOpaque(false); list.add(cb); row.add(cb); }
                    multis.put(it[1], list); cur.add(baris(it[2], row)); break; }
                case "OBAT": cur.add(tabelObat()); break;
                case "MASALAH": cur.add(tabelMasalah()); break;
                case "DISCHARGE": cur.add(panelDischarge()); break;
                case "GIZI": cur.add(panelGizi()); break;
                default: break;
            }
        }
        JPanel tabInput = new JPanel(new BorderLayout());
        tabInput.add(head, BorderLayout.NORTH);
        tabInput.add(new JScrollPane(form), BorderLayout.CENTER);

        // Tab Data Input (tabel riwayat + klik-kanan Cetak)
        tabModel = new DefaultTableModel(null, new Object[]{"Tanggal", "Petugas", "Skor Gizi", "id"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbData = new widget.Table(); tbData.setModel(tabModel);
        tbData.setDefaultRenderer(Object.class, new WarnaTable());
        // sembunyikan kolom id
        TableColumn idc = tbData.getColumnModel().getColumn(3);
        idc.setMinWidth(0); idc.setMaxWidth(0); idc.setPreferredWidth(0);
        popupCetak = new javax.swing.JPopupMenu();
        MnCetak = new javax.swing.JMenuItem("Cetak Formulir Pengkajian ICU");
        try { MnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); } catch (Exception e) { }
        MnCetak.addActionListener(this::cetakAksi);
        popupCetak.add(MnCetak);
        tbData.setComponentPopupMenu(popupCetak);
        tbData.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { getData(); }
            @Override public void mousePressed(java.awt.event.MouseEvent evt) { pilihBaris(evt); }
            @Override public void mouseReleased(java.awt.event.MouseEvent evt) { pilihBaris(evt); }
        });
        JPanel tabData = new JPanel(new BorderLayout());
        tabData.add(new JScrollPane(tbData), BorderLayout.CENTER);

        tabs = new javax.swing.JTabbedPane();
        tabs.addTab("Input", tabInput);
        tabs.addTab("Data Input", tabData);
        internalFrame1.add(tabs, BorderLayout.CENTER);

        // Tombol CRUD gaya Khanza
        widget.panelisi bot = new widget.panelisi();
        bot.setLayout(new FlowLayout(FlowLayout.RIGHT, 4, 4));
        BtnSimpan = tombol("Simpan", 'S', "/picture/save-16x16.png");
        BtnBatal = tombol("Baru", 'B', "/picture/Cancel-2-16x16.png");
        BtnEdit = tombol("Ganti", 'G', "/picture/inventaris.png");
        BtnHapus = tombol("Hapus", 'H', "/picture/stop_f2.png");
        BtnKeluar = tombol("Keluar", 'K', "/picture/exit.png");
        BtnSimpan.addActionListener(this::simpanAksi);
        BtnBatal.addActionListener(e -> emptTeks());
        BtnEdit.addActionListener(this::gantiAksi);
        BtnHapus.addActionListener(this::hapusAksi);
        BtnKeluar.addActionListener(e -> { petugas.dispose(); dispose(); });
        bot.add(BtnSimpan); bot.add(BtnBatal); bot.add(BtnEdit); bot.add(BtnHapus); bot.add(BtnKeluar);
        internalFrame1.add(bot, BorderLayout.SOUTH);
    }

    private JPanel baris(String label, Component comp) {
        JPanel p = new JPanel(new BorderLayout(4, 0)); p.setOpaque(false);
        JLabel l = new JLabel(label); l.setPreferredSize(new Dimension(230, 22)); l.setFont(new java.awt.Font("Tahoma", 0, 11));
        p.add(l, BorderLayout.WEST); p.add(comp, BorderLayout.CENTER);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, comp instanceof JScrollPane ? 42 : 26));
        return p;
    }
    private JPanel tabelObat() {
        obatModel = new DefaultTableModel(new Object[]{"Nama Obat", "Dosis", "Cara Pemakaian", "Keterangan"}, 0);
        tbObat = new javax.swing.JTable(obatModel);
        return tabelPanel(tbObat, obatModel, 4);
    }
    private JPanel tabelMasalah() {
        masalahModel = new DefaultTableModel(new Object[]{"Masalah Keperawatan", "Tujuan / Target Terukur"}, 0);
        tbMasalah = new javax.swing.JTable(masalahModel);
        return tabelPanel(tbMasalah, masalahModel, 2);
    }
    private JPanel tabelPanel(javax.swing.JTable tb, DefaultTableModel m, int cols) {
        JPanel p = new JPanel(new BorderLayout()); p.setOpaque(false);
        JScrollPane sc = new JScrollPane(tb); sc.setPreferredSize(new Dimension(650, 90));
        p.add(sc, BorderLayout.CENTER);
        JPanel b = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0)); b.setOpaque(false);
        JButton add = new JButton("+ Baris"); JButton del = new JButton("- Baris");
        add.addActionListener(e -> m.addRow(new Object[cols]));
        del.addActionListener(e -> { if (tb.getSelectedRow() >= 0) m.removeRow(tb.getSelectedRow()); });
        b.add(add); b.add(del); p.add(b, BorderLayout.SOUTH);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        return p;
    }
    private JPanel panelDischarge() {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); p.setOpaque(false);
        for (int i = 0; i < DISCHARGE.length; i++) {
            JComboBox<String> ya = new JComboBox<>(YT); JTextField ket = new JTextField();
            dcYa.add(ya); dcKet.add(ket);
            JPanel row = new JPanel(new BorderLayout(4, 0)); row.setOpaque(false);
            JLabel l = new JLabel((i + 1) + ". " + DISCHARGE[i]); l.setPreferredSize(new Dimension(230, 22)); l.setFont(new java.awt.Font("Tahoma", 0, 11));
            JPanel c = new JPanel(new BorderLayout(4, 0)); c.setOpaque(false);
            ya.setPreferredSize(new Dimension(70, 22)); c.add(ya, BorderLayout.WEST); c.add(ket, BorderLayout.CENTER);
            row.add(l, BorderLayout.WEST); row.add(c, BorderLayout.CENTER);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26)); p.add(row);
        }
        return p;
    }
    private JPanel panelGizi() {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); p.setOpaque(false);
        JComboBox<String> g1 = new JComboBox<>(new String[]{"", "Tidak", "Tidak yakin / baju lebih longgar", "Ya"});
        JComboBox<String> g1t = new JComboBox<>(new String[]{"", "1-5 kg", "6-10 kg", "11-15 kg", ">15 kg", "Tidak yakin penurunannya"});
        JComboBox<String> g2 = new JComboBox<>(YT);
        JComboBox<String> g3 = new JComboBox<>(YT);
        JComboBox<String> gn = new JComboBox<>(YT);
        combos.put("gizi1", g1); combos.put("gizi1_turun", g1t); combos.put("gizi2", g2); combos.put("gizi3", g3); combos.put("gizi_nutritionis", gn);
        giziLabel = new JLabel("Total Skor MST: 0"); giziLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); giziLabel.setForeground(new java.awt.Color(170, 0, 0));
        java.awt.event.ActionListener up = e -> hitungGizi();
        g1.addActionListener(up); g1t.addActionListener(up); g2.addActionListener(up);
        p.add(baris("1. Penurunan BB tidak diinginkan (6 bln)", g1));
        p.add(baris("   Bila Ya, berapa penurunan", g1t));
        p.add(baris("2. Asupan makan berkurang", g2));
        p.add(baris("3. Diagnosis khusus (DM/Kemo/HD/Geriatri/imun)", g3));
        p.add(baris("4. Sudah dibaca & diketahui Nutrisionis", gn));
        JPanel r = new JPanel(new FlowLayout(FlowLayout.LEFT)); r.setOpaque(false); r.add(giziLabel);
        r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26)); p.add(r);
        return p;
    }
    private int hitungGizi() {
        int s1 = 0; String g1 = str(combos.get("gizi1"));
        if (g1.startsWith("Tidak yakin")) s1 = 2;
        else if (g1.equals("Ya")) {
            String t = str(combos.get("gizi1_turun"));
            s1 = t.startsWith("1-5") ? 1 : t.startsWith("6-10") ? 2 : t.startsWith("11-15") ? 3 : t.startsWith(">15") ? 4 : t.startsWith("Tidak yakin") ? 2 : 0;
        }
        int s2 = str(combos.get("gizi2")).equals("Ya") ? 1 : 0;
        int total = s1 + s2;
        if (giziLabel != null) giziLabel.setText("Total Skor MST: " + total + (total > 2 ? " (perlu pengkajian lanjut Dietisien)" : ""));
        return total;
    }
    private String str(JComboBox<String> c) { Object o = c.getSelectedItem(); return o == null ? "" : o.toString(); }

    // ── Collect / Populate ──
    private Map<String, Object> collect() {
        Map<String, Object> m = new LinkedHashMap<>();
        for (Map.Entry<String, JTextField> e : teks.entrySet()) m.put(e.getKey(), e.getValue().getText());
        for (Map.Entry<String, JTextArea> e : areas.entrySet()) m.put(e.getKey(), e.getValue().getText());
        for (Map.Entry<String, JComboBox<String>> e : combos.entrySet()) m.put(e.getKey(), str(e.getValue()));
        for (Map.Entry<String, List<JCheckBox>> e : multis.entrySet()) {
            List<String> sel = new ArrayList<>();
            for (JCheckBox cb : e.getValue()) if (cb.isSelected()) sel.add(cb.getText());
            m.put(e.getKey(), sel);
        }
        m.put("_obat", rows(obatModel, "nama", "dosis", "cara", "ket"));
        m.put("_masalah", rows(masalahModel, "masalah", "tujuan"));
        List<Map<String, String>> dc = new ArrayList<>();
        for (int i = 0; i < DISCHARGE.length; i++) {
            Map<String, String> r = new LinkedHashMap<>(); r.put("ya", str(dcYa.get(i))); r.put("ket", dcKet.get(i).getText()); dc.add(r);
        }
        m.put("_discharge", dc);
        m.put("gizi_skor", hitungGizi());
        return m;
    }
    private List<Map<String, String>> rows(DefaultTableModel model, String... keys) {
        List<Map<String, String>> l = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            Map<String, String> r = new LinkedHashMap<>(); boolean isi = false;
            for (int j = 0; j < keys.length; j++) {
                Object v = model.getValueAt(i, j); String sv = v == null ? "" : v.toString();
                r.put(keys[j], sv); if (!sv.trim().isEmpty()) isi = true;
            }
            if (isi) l.add(r);
        }
        return l;
    }
    @SuppressWarnings("unchecked")
    private void populate(String json) {
        emptForm();
        if (json == null || json.trim().isEmpty()) return;
        Map<String, Object> m = gson.fromJson(json, Map.class);
        if (m == null) return;
        for (Map.Entry<String, JTextField> e : teks.entrySet()) if (m.get(e.getKey()) != null) e.getValue().setText(m.get(e.getKey()).toString());
        for (Map.Entry<String, JTextArea> e : areas.entrySet()) if (m.get(e.getKey()) != null) e.getValue().setText(m.get(e.getKey()).toString());
        for (Map.Entry<String, JComboBox<String>> e : combos.entrySet()) if (m.get(e.getKey()) != null) e.getValue().setSelectedItem(m.get(e.getKey()).toString());
        for (Map.Entry<String, List<JCheckBox>> e : multis.entrySet()) {
            Object v = m.get(e.getKey());
            if (v instanceof List) { List<?> sel = (List<?>) v; for (JCheckBox cb : e.getValue()) cb.setSelected(sel.contains(cb.getText())); }
        }
        isiTabel(obatModel, m.get("_obat"), "nama", "dosis", "cara", "ket");
        isiTabel(masalahModel, m.get("_masalah"), "masalah", "tujuan");
        Object dc = m.get("_discharge");
        if (dc instanceof List) { List<?> l = (List<?>) dc;
            for (int i = 0; i < DISCHARGE.length && i < l.size(); i++) { Object o = l.get(i);
                if (o instanceof Map) { Map<?, ?> r = (Map<?, ?>) o;
                    if (r.get("ya") != null) dcYa.get(i).setSelectedItem(r.get("ya").toString());
                    if (r.get("ket") != null) dcKet.get(i).setText(r.get("ket").toString()); } } }
        hitungGizi();
    }
    private void isiTabel(DefaultTableModel model, Object v, String... keys) {
        model.setRowCount(0);
        if (v instanceof List) for (Object o : (List<?>) v) if (o instanceof Map) {
            Map<?, ?> r = (Map<?, ?>) o; Object[] row = new Object[keys.length];
            for (int j = 0; j < keys.length; j++) row[j] = r.get(keys[j]) == null ? "" : r.get(keys[j]).toString();
            model.addRow(row);
        }
    }

    // ── CRUD ──
    private String waktu() {
        return new SimpleDateFormat("yyyy-MM-dd").format(dateTanggal()) + " " + new SimpleDateFormat("HH:mm:ss").format(new Date());
    }
    private Date dateTanggal() { try { return Tanggal.getDate(); } catch (Exception e) { return new Date(); } }

    private boolean validasiInput() {
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) { Valid.textKosong(TNoRw, "pasien"); return false; }
        if (KdPetugas.getText().trim().equals("") || NmPetugas.getText().trim().equals("")) { Valid.textKosong(KdPetugas, "Petugas"); return false; }
        return true;
    }

    private void simpanAksi(ActionEvent e) {
        if (!validasiInput()) return;
        int gizi = hitungGizi(); String json = gson.toJson(collect());
        try {
            ps = koneksi.prepareStatement("insert into pengkajian_keperawatan_icu (no_rawat,tanggal,nip,data,gizi_skor,created_at) values (?,?,?,?,?,now())");
            ps.setString(1, TNoRw.getText()); ps.setString(2, waktu()); ps.setString(3, KdPetugas.getText());
            ps.setString(4, json); ps.setInt(5, gizi);
            if (ps.executeUpdate() > 0) { JOptionPane.showMessageDialog(null, "Pengkajian ICU tersimpan."); currentId = 0; tampil(); }
        } catch (Exception ex) { JOptionPane.showMessageDialog(null, "Notif : " + ex); }
        finally { try { if (ps != null) ps.close(); } catch (Exception ex) { } }
    }

    private void gantiAksi(ActionEvent e) {
        if (currentId <= 0) { JOptionPane.showMessageDialog(null, "Pilih data dari tab \"Data Input\" dulu untuk diganti."); return; }
        if (!validasiInput()) return;
        int gizi = hitungGizi(); String json = gson.toJson(collect());
        try {
            ps = koneksi.prepareStatement("update pengkajian_keperawatan_icu set tanggal=?, nip=?, data=?, gizi_skor=? where id=?");
            ps.setString(1, waktu()); ps.setString(2, KdPetugas.getText()); ps.setString(3, json); ps.setInt(4, gizi); ps.setLong(5, currentId);
            if (ps.executeUpdate() > 0) { JOptionPane.showMessageDialog(null, "Pengkajian ICU diperbarui."); tampil(); }
        } catch (Exception ex) { JOptionPane.showMessageDialog(null, "Notif : " + ex); }
        finally { try { if (ps != null) ps.close(); } catch (Exception ex) { } }
    }

    private void hapusAksi(ActionEvent e) {
        if (currentId <= 0) { JOptionPane.showMessageDialog(null, "Pilih data dari tab \"Data Input\" dulu untuk dihapus."); return; }
        if (JOptionPane.showConfirmDialog(null, "Hapus pengkajian terpilih?", "Konfirmasi", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            ps = koneksi.prepareStatement("delete from pengkajian_keperawatan_icu where id=?");
            ps.setLong(1, currentId);
            if (ps.executeUpdate() > 0) { JOptionPane.showMessageDialog(null, "Data dihapus."); emptTeks(); tampil(); }
        } catch (Exception ex) { JOptionPane.showMessageDialog(null, "Notif : " + ex); }
        finally { try { if (ps != null) ps.close(); } catch (Exception ex) { } }
    }

    public void tampil() {
        Valid.tabelKosong(tabModel);
        try {
            ps = koneksi.prepareStatement(
                "select p.id, cast(p.tanggal as char) tanggal, ifnull(pg.nama,p.nip) petugas, ifnull(p.gizi_skor,0) gizi "
                + "from pengkajian_keperawatan_icu p left join pegawai pg on pg.nik=p.nip where p.no_rawat=? order by p.id desc");
            ps.setString(1, TNoRw.getText()); rs = ps.executeQuery();
            while (rs.next()) tabModel.addRow(new Object[]{rs.getString("tanggal"), rs.getString("petugas"), rs.getString("gizi"), rs.getString("id")});
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notif : " + e); }
    }

    private void getData() {
        int r = tbData.getSelectedRow();
        if (r < 0) return;
        currentId = Long.parseLong(tabModel.getValueAt(r, 3).toString());
        try {
            ps = koneksi.prepareStatement("select data from pengkajian_keperawatan_icu where id=?");
            ps.setLong(1, currentId); rs = ps.executeQuery();
            if (rs.next()) populate(rs.getString("data"));
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notif : " + e); }
        tabs.setSelectedIndex(0);   // pindah ke tab Input
    }

    private void pilihBaris(java.awt.event.MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            int r = tbData.rowAtPoint(evt.getPoint());
            if (r >= 0) tbData.setRowSelectionInterval(r, r);
        }
    }

    public void setNoRm(String norwt, Date tgl) {
        TNoRw.setText(norwt);
        try {
            ps = koneksi.prepareStatement(
                "select pasien.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir "
                + "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis where reg_periksa.no_rawat=?");
            ps.setString(1, norwt); rs = ps.executeQuery();
            if (rs.next()) {
                TNoRM.setText(rs.getString("no_rkm_medis")); TPasien.setText(rs.getString("nm_pasien"));
                Jk.setText(rs.getString("jk").equals("L") ? "Laki-laki" : "Perempuan"); TglLahir.setText(rs.getString("tgl_lahir"));
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notif : " + e); }
        tampil();
    }

    public void isCek() {
        boolean ak = akses.getchecklist_kriteria_masuk_icu();
        BtnSimpan.setEnabled(ak); BtnEdit.setEnabled(ak); BtnHapus.setEnabled(ak);
        if (akses.getjml2() >= 1) {
            KdPetugas.setEditable(false); btnPetugas.setEnabled(false);
            KdPetugas.setText(akses.getkode()); NmPetugas.setText(petugas.tampil3(KdPetugas.getText()));
        }
    }

    private void emptForm() {
        for (JTextField t : teks.values()) t.setText("");
        for (JTextArea t : areas.values()) t.setText("");
        for (JComboBox<String> c : combos.values()) c.setSelectedIndex(0);
        for (List<JCheckBox> l : multis.values()) for (JCheckBox cb : l) cb.setSelected(false);
        if (obatModel != null) obatModel.setRowCount(0);
        if (masalahModel != null) masalahModel.setRowCount(0);
        for (JComboBox<String> c : dcYa) c.setSelectedIndex(0);
        for (JTextField t : dcKet) t.setText("");
        hitungGizi();
    }
    public void emptTeks() {
        emptForm(); currentId = 0; Tanggal.setDate(new Date());
        if (tbData != null) tbData.clearSelection();
    }

    // ── Cetak Jasper (dari baris terpilih di Data Input) ──
    private void cetakAksi(ActionEvent e) {
        int r = tbData.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(null, "Pilih data yang akan dicetak (klik baris) dulu."); return; }
        try {
            long id = Long.parseLong(tabModel.getValueAt(r, 3).toString());
            String json = "", nip = "";
            ps = koneksi.prepareStatement("select data, nip from pengkajian_keperawatan_icu where id=?");
            ps.setLong(1, id); rs = ps.executeQuery();
            if (rs.next()) { json = rs.getString("data"); nip = rs.getString("nip"); }
            rs.close(); ps.close();
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (json == null || json.isEmpty()) ? new HashMap<>() : gson.fromJson(json, Map.class);
            if (data == null) data = new HashMap<>();

            List<Map<String, ?>> ds = buildRowsDs(data);
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars()); param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs()); param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs()); param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            param.put("nm_pasien", TPasien.getText()); param.put("no_rm", TNoRM.getText());
            param.put("jk", Jk.getText()); param.put("tgl_lahir", TglLahir.getText()); param.put("no_rawat", TNoRw.getText());
            param.put("tanggal", tabModel.getValueAt(r, 0).toString());
            String nmPetugas = Sequel.cariIsi("select nama from pegawai where nik=?", nip);
            param.put("petugas", nmPetugas.equals("") ? nip : nmPetugas);
            String finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", nip);
            param.put("finger", "Ditandatangani secara elektronik oleh " + (nmPetugas.equals("") ? nip : nmPetugas) + "\nID " + (finger.equals("") ? nip : finger) + "\n" + akses.getnamars());
            File f = new File("./report/rptPengkajianKeperawatanICU.jasper");
            if (!f.isFile()) { JOptionPane.showMessageDialog(null, "File report tidak ditemukan: " + f.getPath()); return; }
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            JasperPrint jp = JasperFillManager.fillReport(f.getPath(), param, new net.sf.jasperreports.engine.data.JRMapCollectionDataSource(ds));
            JasperViewer jv = new JasperViewer(jp, false);
            jv.setTitle("::[ Formulir Pengkajian Keperawatan Pasien ICU ]::");
            jv.setVisible(true);
            this.setCursor(Cursor.getDefaultCursor());
        } catch (Exception ex) {
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(null, "Notif cetak : " + ex);
        }
    }

    private List<Map<String, ?>> buildRowsDs(Map<String, Object> data) {
        List<Map<String, ?>> ds = new ArrayList<>();
        String curSec = null;
        for (String[] it : skema()) {
            if (it[0].equals("SEC")) { curSec = it[1]; }
            else if (it[0].equals("TXT") || it[0].equals("SEL")) {
                String v = data.get(it[1]) == null ? "" : data.get(it[1]).toString();
                if (!v.trim().isEmpty()) addFld(ds, curSec, it[2], v);
            } else if (it[0].equals("MULTI")) {
                Object v = data.get(it[1]);
                String joined = (v instanceof List) ? String.join(", ", castStr((List<?>) v)) : "";
                if (!joined.isEmpty()) addFld(ds, curSec, it[2], joined);
            } else if (it[0].equals("OBAT")) {
                Object v = data.get("_obat");
                if (v instanceof List) for (Object o : (List<?>) v) if (o instanceof Map) { Map<?, ?> rr = (Map<?, ?>) o;
                    addFld(ds, curSec, str2(rr.get("nama")), str2(rr.get("dosis")) + " | " + str2(rr.get("cara")) + " | " + str2(rr.get("ket"))); }
            } else if (it[0].equals("MASALAH")) {
                Object v = data.get("_masalah");
                if (v instanceof List) for (Object o : (List<?>) v) if (o instanceof Map) { Map<?, ?> rr = (Map<?, ?>) o;
                    addFld(ds, curSec, str2(rr.get("masalah")), str2(rr.get("tujuan"))); }
            } else if (it[0].equals("DISCHARGE")) {
                Object v = data.get("_discharge");
                if (v instanceof List) { List<?> l = (List<?>) v;
                    for (int i = 0; i < DISCHARGE.length && i < l.size(); i++) if (l.get(i) instanceof Map) { Map<?, ?> rr = (Map<?, ?>) l.get(i);
                        String ya = str2(rr.get("ya")); if (!ya.isEmpty()) addFld(ds, curSec, DISCHARGE[i], ya + (str2(rr.get("ket")).isEmpty() ? "" : " — " + str2(rr.get("ket")))); } }
            } else if (it[0].equals("GIZI")) {
                addFld(ds, curSec, "Total Skor MST", str2(data.get("gizi_skor")));
            }
        }
        return ds;
    }
    private void addFld(List<Map<String, ?>> ds, String sec, String label, String nilai) {
        boolean adaSec = false;
        for (Map<String, ?> r : ds) if ("SEC".equals(r.get("tipe")) && sec != null && sec.equals(r.get("label"))) { adaSec = true; break; }
        if (!adaSec && sec != null) { Map<String, Object> h = new LinkedHashMap<>(); h.put("tipe", "SEC"); h.put("label", sec); h.put("nilai", ""); ds.add(h); }
        Map<String, Object> m = new LinkedHashMap<>(); m.put("tipe", "FLD"); m.put("label", label); m.put("nilai", nilai); ds.add(m);
    }
    private List<String> castStr(List<?> l) { List<String> o = new ArrayList<>(); for (Object x : l) o.add(x == null ? "" : x.toString()); return o; }
    private String str2(Object o) { return o == null ? "" : o.toString(); }
}
