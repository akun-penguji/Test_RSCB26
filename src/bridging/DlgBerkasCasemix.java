package bridging;

import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

// HttpClient 5 (org.apache.hc.*), BUKAN org.apache.http.* (HttpClient 4 classic) — pakai yang
// sama dengan pola upload yang sudah terbukti jalan di RMDataResumePasien.java. HttpClient 4
// classic (dipakai DlgBerkasRawat.uploadDraft()) pernah terbukti bentrok versi httpclient/httpcore
// di classpath IDE/runtime (NoSuchFieldError: INSTANCE di SSLConnectionSocketFactory.<clinit>).
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

/**
 * Berkas Casemix — deteksi otomatis berkas rekam medis yang sudah terisi
 * untuk satu kunjungan (Rajal/IGD/Ranap), gabung jadi satu PDF beserta
 * identitas dokter pengisi tiap berkas, lalu kirim ke DlgBerkasKlaim untuk
 * TTE estafet. Tidak memerlukan "Tag Signer" manual per form lebih dulu.
 *
 * Desain mengikuti pola "BPJSKompilasiBerkasKlaim" (referensi RS lain,
 * basis kode sama/Khanza) untuk bagian deteksi+gabung, tapi terhubung
 * langsung ke berkas_klaim/DlgBerkasKlaim yang SUDAH ADA di proyek ini
 * (DlgBerkasKlaim.java TIDAK diubah sama sekali).
 *
 * Tahap 1 — 9 jenis berkas yang terbukti punya sumber data jelas di skema
 * Cabangbungin: SEP, Resume Ralan, Resume Ranap, Billing, Triase IGD,
 * Asesmen Awal Medis IGD, Hasil Lab, Hasil Radiologi, Laporan Operasi.
 * Jenis lain (Kronologi, SJP Jamkesda, KTP/KK, dll) tidak punya generator
 * otomatis di kode — ditangani lewat tab "Berkas Digital Manual" yang
 * menampilkan apa pun yang sudah diunggah lewat alur Tag Signer/DlgBerkasRawat
 * lama, supaya tetap bisa diikutkan tanpa harus dibangun dari nol.
 *
 * Dokumen yang NAMA DOKTERNYA tercatat di tabel sumber (Resume Ralan/Ranap,
 * Asesmen Awal Medis IGD, Hasil Lab, Hasil Radiologi, Laporan Operasi) diberi
 * tag "#...#" (format sama dengan TagSignerDetector yang sudah dipakai
 * DlgBerkasKlaim) dan dicatat ke berkas_digital_perawatan supaya otomatis
 * masuk daftar penandatangan estafet. SEP, Billing, Triase IGD (tidak ada
 * kolom dokter di data_triase_igd) dan berkas manual ikut digabung sebagai
 * lampiran tanpa tanda tangan dokter sendiri.
 */
public final class DlgBerkasCasemix extends javax.swing.JDialog {

    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();

    private final widget.InternalFrame internalFrame1 = new widget.InternalFrame();

    private DefaultTableModel tabPasien;
    private DefaultTableModel tabManual;
    private DefaultTableModel tabBilling;
    private final widget.Table tbPasien = new widget.Table();
    private final widget.Table tbManual = new widget.Table();
    private final widget.Table tbBilling = new widget.Table();
    private final widget.Label lblTotalBilling = new widget.Label();

    private final widget.Tanggal DTPCari1 = new widget.Tanggal();
    private final widget.Tanggal DTPCari2 = new widget.Tanggal();
    private final widget.ComboBox CmbJenis = new widget.ComboBox();
    private final widget.TextBox TCari = new widget.TextBox();
    private final widget.Button BtnCari = new widget.Button();
    private final widget.Button BtnGabung = new widget.Button();
    private final widget.Button BtnINACBG = new widget.Button();
    private final widget.Button BtnKeluar = new widget.Button();

    // Panel Diagnosa & Prosedur — komponen bawaan yang sama dipakai di form "Diagnosa Pasien"
    // (laporan.DlgDiagnosaPenyakit), sudah mendukung diagnosa+prosedur sekaligus (2 tab internal).
    private final laporan.PanelDiagnosa panelDiagnosa1 = new laporan.PanelDiagnosa();

    private final widget.Label lblPasienInfo = new widget.Label();
    private final JTextAreaLog areaLog = new JTextAreaLog();

    // key -> {label, kolom_checklist}
    private final LinkedHashMap<String, widget.CekBox> centangDok = new LinkedHashMap<>();
    private final LinkedHashMap<String, widget.Button> tombolLihat = new LinkedHashMap<>();

    private String selNoRawat = "", selNoSep = "", selNoRM = "", selNamaPasien = "", selStatusLanjut = "", selTglRegistrasi = "";
    private String Verif = "";

    private static final String[][] JENIS_BERKAS = {
        {"sep", "SEP"},
        {"resume", "Resume (Ralan/Ranap)"},
        {"billing", "Billing"},
        {"triase", "Triase IGD"},
        {"asesmen_igd", "Asesmen Awal Medis IGD"},
        {"lab", "Hasil Laboratorium"},
        {"radiologi", "Hasil Radiologi"},
        {"operasi", "Laporan Operasi"},
    };
    // key -> perlu tag TTE dokter atau tidak (nama dokter tercantum di berkasnya)
    private static final java.util.Set<String> PERLU_TTE = new java.util.HashSet<>(Arrays.asList(
            "resume", "asesmen_igd", "lab", "radiologi", "operasi"));

    // kode master_berkas_digital untuk tiap kodeFile yang perlu TTE — dibuat singkat (<=10 karakter)
    // karena kolom "kode" sudah terbukti (STRICT sql_mode) menolak nilai yang lebih panjang dari itu.
    private static final java.util.Map<String, String> DB_KODE_TTE = new java.util.HashMap<>();
    static {
        DB_KODE_TTE.put("RESUME", "CMX_RESUME");
        DB_KODE_TTE.put("ASESMENIGD", "CMX_ASIGD");
        DB_KODE_TTE.put("LAB", "CMX_LAB");
        DB_KODE_TTE.put("RADIOLOGI", "CMX_RADIO");
        DB_KODE_TTE.put("OPERASI", "CMX_OPS");
    }

    public DlgBerkasCasemix(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Berkas Casemix — Kompilasi Berkas Klaim");
        buildUI();
        // Sama seperti laporan.DlgDiagnosaPenyakit: tombol tambah diagnosa/prosedur ikut hak akses.
        panelDiagnosa1.btnTambahPenyakit.setEnabled(akses.getpenyakit());
        panelDiagnosa1.btnTambahProsedur.setEnabled(akses.geticd9());
        setSize(1480, 780);
        setLocationRelativeTo(parent);
    }

    // ==================================================== UI ====================================================
    // Mengikuti pola layout standar Khanza (lihat DlgBerkasKlaim): widget.InternalFrame berbingkai
    // judul "::[ ... ]::", isi di CENTER, dan satu panel filter+tombol (widget.panelisi, FlowLayout)
    // di PAGE_END — bukan filter di atas & tombol tersebar seperti draft awal.
    private void buildUI() {
        setLayout(new BorderLayout());

        internalFrame1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(240, 245, 235)),
                "::[ Berkas Casemix — Kompilasi Berkas Klaim ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Tahoma", Font.PLAIN, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(4, 4));

        // ---- Kiri: daftar pasien ----
        tabPasien = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.SEP", "No.RM", "Nama Pasien", "Jenis", "Tgl SEP", "Ruangan/Poli"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbPasien.setModel(tabPasien);
        tbPasien.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbPasien.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tbPasien.getSelectedRow() != -1) pilihPasien();
        });
        widget.ScrollPane spPasien = new widget.ScrollPane();
        spPasien.setViewportView(tbPasien);
        spPasien.setPreferredSize(new Dimension(620, 500));

        // ---- Kanan: info pasien + tab (checklist berkas / diagnosa&prosedur) + log ----
        JPanel panelKanan = new JPanel();
        panelKanan.setLayout(new javax.swing.BoxLayout(panelKanan, javax.swing.BoxLayout.Y_AXIS));
        lblPasienInfo.setText("Pilih pasien dari daftar di sebelah kiri.");
        lblPasienInfo.setFont(lblPasienInfo.getFont().deriveFont(Font.BOLD, 13f));
        lblPasienInfo.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        panelKanan.add(lblPasienInfo);

        javax.swing.JTabbedPane tabKanan = new javax.swing.JTabbedPane();

        JPanel tabChecklist = new JPanel();
        tabChecklist.setLayout(new javax.swing.BoxLayout(tabChecklist, javax.swing.BoxLayout.Y_AXIS));

        JPanel panelCentang = new JPanel(new GridLayout(0, 1, 2, 2));
        panelCentang.setBorder(BorderFactory.createTitledBorder("Berkas terdeteksi — otomatis tercentang bila ada datanya"));
        for (String[] jb : JENIS_BERKAS) {
            String key = jb[0];
            widget.CekBox c = new widget.CekBox();
            c.setText(jb[1] + (PERLU_TTE.contains(key) ? "  (perlu TTE dokter)" : ""));
            c.setEnabled(false);
            centangDok.put(key, c);

            widget.Button btnLihat = new widget.Button();
            btnLihat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
            btnLihat.setText("Lihat");
            btnLihat.setPreferredSize(new Dimension(80, 20));
            btnLihat.setEnabled(false);
            btnLihat.addActionListener(e -> previewBerkas(key));
            tombolLihat.put(key, btnLihat);

            JPanel baris = new JPanel(new BorderLayout(4, 0));
            baris.add(c, BorderLayout.CENTER);
            baris.add(btnLihat, BorderLayout.EAST);
            panelCentang.add(baris);
        }
        tabChecklist.add(panelCentang);

        tabManual = new DefaultTableModel(null, new Object[]{"P", "Jenis Berkas", "Lokasi File"}) {
            @Override public boolean isCellEditable(int r, int c) { return c == 0; }
            @Override public Class<?> getColumnClass(int c) { return c == 0 ? Boolean.class : String.class; }
        };
        tbManual.setModel(tabManual);
        widget.ScrollPane spManual = new widget.ScrollPane();
        spManual.setViewportView(tbManual);
        spManual.setPreferredSize(new Dimension(420, 160));
        spManual.setBorder(BorderFactory.createTitledBorder("Berkas digital manual (hasil Tag Signer/DlgBerkasRawat lama) — opsional, centang untuk ikut digabung"));
        tabChecklist.add(spManual);
        tabKanan.addTab("Checklist Berkas", tabChecklist);

        // Tab Diagnosa & Prosedur — panel bawaan Khanza (laporan.PanelDiagnosa), persis yang
        // dipakai form "Diagnosa Pasien", supaya koder bisa input/lengkapi diagnosa+prosedur
        // di sini juga sebelum Bridging INACBG, tanpa harus pindah form.
        widget.ScrollPane spDiagnosa = new widget.ScrollPane();
        spDiagnosa.setViewportView(panelDiagnosa1);
        tabKanan.addTab("Diagnosa & Prosedur", spDiagnosa);

        // Tab Billing — rincian tabel billing per kunjungan, pola sama dengan tab "Billing" di
        // BPJSKompilasiBerkasKlaim (referensi), tapi JTable native Khanza, bukan JavaFX WebView.
        JPanel tabBillingPanel = new JPanel(new BorderLayout(4, 4));
        tabBilling = new DefaultTableModel(null, new Object[]{"No", "Nama Perawatan", "P", "Biaya", "Jumlah", "Tambahan", "Total"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbBilling.setModel(tabBilling);
        widget.ScrollPane spBilling = new widget.ScrollPane();
        spBilling.setViewportView(tbBilling);
        tabBillingPanel.add(spBilling, BorderLayout.CENTER);
        lblTotalBilling.setText("Total Biaya: -");
        lblTotalBilling.setFont(lblTotalBilling.getFont().deriveFont(Font.BOLD, 12f));
        lblTotalBilling.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotalBilling.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 10));
        tabBillingPanel.add(lblTotalBilling, BorderLayout.SOUTH);
        tabKanan.addTab("Billing", tabBillingPanel);

        panelKanan.add(tabKanan);
        panelKanan.add(areaLog.scroll());

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spPasien, panelKanan);
        split.setDividerLocation(500);
        internalFrame1.add(split, BorderLayout.CENTER);

        // ---- Bawah: filter + tombol (satu panel, pola Khanza) ----
        widget.panelisi panelBawah = new widget.panelisi();
        panelBawah.setPreferredSize(new Dimension(55, 55));
        panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));

        widget.Label lbl1 = new widget.Label();
        lbl1.setText("Tgl SEP :");
        lbl1.setPreferredSize(new Dimension(60, 23));
        panelBawah.add(lbl1);

        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setPreferredSize(new Dimension(90, 23));
        panelBawah.add(DTPCari1);

        widget.Label lbl2 = new widget.Label();
        lbl2.setText("s.d.");
        lbl2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl2.setPreferredSize(new Dimension(25, 23));
        panelBawah.add(lbl2);

        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setPreferredSize(new Dimension(90, 23));
        panelBawah.add(DTPCari2);

        widget.Label lbl3 = new widget.Label();
        lbl3.setText("Jenis :");
        lbl3.setPreferredSize(new Dimension(40, 23));
        panelBawah.add(lbl3);

        CmbJenis.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Semua", "Rawat Jalan", "IGD", "Rawat Inap"}));
        CmbJenis.setPreferredSize(new Dimension(120, 23));
        panelBawah.add(CmbJenis);

        widget.Label lbl4 = new widget.Label();
        lbl4.setText("Cari :");
        lbl4.setPreferredSize(new Dimension(35, 23));
        panelBawah.add(lbl4);

        TCari.setPreferredSize(new Dimension(180, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) tampilDaftarPasien();
            }
        });
        panelBawah.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setMnemonic('1');
        BtnCari.setToolTipText("Alt+1");
        BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnCari.addActionListener(e -> tampilDaftarPasien());
        panelBawah.add(BtnCari);

        widget.Label lbl5 = new widget.Label();
        lbl5.setPreferredSize(new Dimension(20, 23));
        panelBawah.add(lbl5);

        BtnGabung.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/if_Layers_22893.png")));
        BtnGabung.setMnemonic('G');
        BtnGabung.setText("Gabung PDF & Kirim ke Berkas Klaim");
        BtnGabung.setToolTipText("Alt+G");
        BtnGabung.setPreferredSize(new Dimension(260, 23));
        BtnGabung.setEnabled(false);
        BtnGabung.addActionListener(e -> gabungDanKirim());
        panelBawah.add(BtnGabung);

        BtnINACBG.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/checked.png")));
        BtnINACBG.setMnemonic('I');
        BtnINACBG.setText("Bridging INACBG");
        BtnINACBG.setToolTipText("Alt+I");
        BtnINACBG.setPreferredSize(new Dimension(150, 23));
        BtnINACBG.setEnabled(false);
        BtnINACBG.addActionListener(e -> bukaBridgingINACBG());
        panelBawah.add(BtnINACBG);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setPreferredSize(new Dimension(90, 23));
        BtnKeluar.addActionListener(e -> dispose());
        panelBawah.add(BtnKeluar);

        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);

        add(internalFrame1, BorderLayout.CENTER);
    }

    /** Dipanggil setelah dialog tampil (meniru pola tampil() form lain). */
    public void tampil() {
        DTPCari1.setDate(new java.util.Date());
        DTPCari2.setDate(new java.util.Date());
        tampilDaftarPasien();
    }

    // ============================================= DAFTAR PASIEN =============================================
    private void tampilDaftarPasien() {
        tabPasien.setRowCount(0);
        int jenisIdx = CmbJenis.getSelectedIndex();
        String cari = TCari.getText().trim();
        String sql =
            "select bridging_sep.no_rawat, bridging_sep.no_sep, reg_periksa.no_rkm_medis, pasien.nm_pasien, " +
            "reg_periksa.status_lanjut, bridging_sep.tglsep, reg_periksa.kd_poli, " +
            "case when reg_periksa.status_lanjut='Ranap' then concat(ifnull(kamar.kd_kamar,''),' ',ifnull(bangsal.nm_bangsal,'')) " +
            "     else poliklinik.nm_poli end as ruangan " +
            "from bridging_sep " +
            "inner join reg_periksa on bridging_sep.no_rawat=reg_periksa.no_rawat " +
            "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis " +
            "left join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli " +
            "left join kamar_inap on reg_periksa.no_rawat=kamar_inap.no_rawat and kamar_inap.stts_pulang<>'Pindah Kamar' " +
            "left join kamar on kamar_inap.kd_kamar=kamar.kd_kamar " +
            "left join bangsal on kamar.kd_bangsal=bangsal.kd_bangsal " +
            "where bridging_sep.tglsep between ? and ? and length(bridging_sep.no_sep)=19 ";
        if (jenisIdx == 1) sql += " and reg_periksa.status_lanjut='Ralan' and reg_periksa.kd_poli<>'IGDK' ";
        else if (jenisIdx == 2) sql += " and reg_periksa.kd_poli='IGDK' ";
        else if (jenisIdx == 3) sql += " and reg_periksa.status_lanjut='Ranap' ";
        if (!cari.isEmpty()) {
            sql += " and (reg_periksa.no_rawat like ? or bridging_sep.no_sep like ? or reg_periksa.no_rkm_medis like ? or pasien.nm_pasien like ?) ";
        }
        sql += " order by bridging_sep.tglsep desc, bridging_sep.no_sep desc limit 300";

        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            int idx = 1;
            ps.setString(idx++, new SimpleDateFormat("yyyy-MM-dd").format(DTPCari1.getDate()));
            ps.setString(idx++, new SimpleDateFormat("yyyy-MM-dd").format(DTPCari2.getDate()));
            if (!cari.isEmpty()) {
                String like = "%" + cari + "%";
                ps.setString(idx++, like); ps.setString(idx++, like); ps.setString(idx++, like); ps.setString(idx++, like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String jenis = "IGDK".equals(rs.getString("kd_poli")) ? "IGD" : rs.getString("status_lanjut");
                    tabPasien.addRow(new Object[]{
                        rs.getString("no_rawat"), rs.getString("no_sep"), rs.getString("no_rkm_medis"),
                        rs.getString("nm_pasien"), jenis, rs.getString("tglsep"), rs.getString("ruangan")
                    });
                }
            }
        } catch (Exception e) {
            areaLog.tulis("Gagal memuat daftar pasien: " + e);
        }
    }

    // ============================================= DETEKSI BERKAS =============================================
    private void pilihPasien() {
        int r = tbPasien.getSelectedRow();
        selNoRawat = tabPasien.getValueAt(r, 0).toString();
        selNoSep = tabPasien.getValueAt(r, 1).toString();
        selNoRM = tabPasien.getValueAt(r, 2).toString();
        selNamaPasien = tabPasien.getValueAt(r, 3).toString();
        selStatusLanjut = tabPasien.getValueAt(r, 4).toString();
        lblPasienInfo.setText(selNoRawat + " — " + selNamaPasien + " (No.RM " + selNoRM + ", " + selStatusLanjut + ")");

        for (widget.CekBox c : centangDok.values()) { c.setSelected(false); c.setEnabled(false); }
        centang("sep", true); // anchor daftar pasien = bridging_sep, pasti ada
        centangAman("resume", "select 1 from resume_pasien where no_rawat=?", "select 1 from resume_pasien_ranap where no_rawat=?");
        centangAman("billing", "select 1 from billing where no_rawat=?");
        centangAman("triase", "select 1 from data_triase_igd where no_rawat=?");
        centangAman("asesmen_igd", "select 1 from penilaian_medis_igd where no_rawat=?");
        centangAman("lab", "select 1 from periksa_lab where no_rawat=?");
        centangAman("radiologi", "select 1 from periksa_radiologi where no_rawat=?");
        centangAman("operasi", "select 1 from laporan_operasi_casemix where no_rawat=?");
        muatBerkasManual();
        muatPanelDiagnosa();
        muatBilling();
        BtnGabung.setEnabled(true);
        BtnINACBG.setEnabled(true);
    }

    /** Isi tab Billing — rincian item billing kunjungan terpilih, sql persis sama dengan yang
     *  dipakai tab "Billing" di BPJSKompilasiBerkasKlaim (referensi): billing diurutkan noindex,
     *  baris tanpa biaya/total (header sub-bagian) ditampilkan apa adanya. */
    private void muatBilling() {
        tabBilling.setRowCount(0);
        lblTotalBilling.setText("Total Biaya: -");
        long total = 0;
        String sql = "select no, nm_perawatan, pemisah, " +
                "if(biaya=0,'',biaya), if(jumlah=0,'',jumlah), if(tambahan=0,'',tambahan), " +
                "if(totalbiaya=0,'',totalbiaya), totalbiaya " +
                "from billing where no_rawat=? order by noindex";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, selNoRawat);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    total += rs.getLong(8);
                    tabBilling.addRow(new Object[]{
                        rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)
                    });
                }
            }
            lblTotalBilling.setText("Total Biaya: " + String.format("%,d", total).replace(',', '.'));
        } catch (Exception e) {
            areaLog.tulis("Gagal memuat Billing: " + e);
        }
    }

    /** Isi panel Diagnosa & Prosedur bawaan Khanza untuk kunjungan terpilih. "IGD" (label
     *  tampilan Jenis) dipetakan balik ke "Ralan" karena itu status_lanjut aslinya di
     *  reg_periksa — PanelDiagnosa.simpan() membandingkan status persis "Ralan"/"Ranap" untuk
     *  tahu tabel resume_pasien mana yang disinkronkan, jadi "IGD" harus dikonversi dulu. */
    private void muatPanelDiagnosa() {
        try {
            selTglRegistrasi = nilaiKolom("select tgl_registrasi from reg_periksa where no_rawat=?", selNoRawat);
            if (selTglRegistrasi == null || selTglRegistrasi.isEmpty()) {
                selTglRegistrasi = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
            }
            String statusDiagnosa = "Ranap".equals(selStatusLanjut) ? "Ranap" : "Ralan";
            panelDiagnosa1.setRM(selNoRawat, selNoRM, selTglRegistrasi, selTglRegistrasi, statusDiagnosa, "");
            panelDiagnosa1.pilihTab();
        } catch (Exception e) {
            areaLog.tulis("Gagal memuat panel Diagnosa & Prosedur: " + e);
        }
    }

    /** Replikasi persis BtnINACBGActionPerformed di laporan.DlgDiagnosaPenyakit: cek login
     *  adalah koder terverifikasi, pastikan SEP ada, baru buka INACBGDataIDRG. */
    private void bukaBridgingINACBG() {
        if (selNoRawat.isEmpty() || selNamaPasien.isEmpty()) return;
        if (akses.getkode().equals("Admin Utama")) {
            Verif = Sequel.cariIsi("select no_ik from inacbg_coder_nik limit 1");
        } else {
            Verif = Sequel.cariIsi("select no_ik from inacbg_coder_nik where nik='" + akses.getkode() + "'");
        }
        if (Verif == null || Verif.equals("")) {
            JOptionPane.showMessageDialog(this, "Maaf, Anda bukan VERIFIKATOR KLAIM !!!!");
            return;
        }
        if (Sequel.cariIsi("select no_sep from bridging_sep where no_rawat='" + selNoRawat + "'").equals("")) {
            JOptionPane.showMessageDialog(this, "Maaf, TIDAK ADA NOMOR SEP PASIEN INI !!!!!");
            return;
        }
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        INACBGDataIDRG ina = new INACBGDataIDRG(null, true);
        ina.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        ina.setLocationRelativeTo(internalFrame1);
        ina.setDiagnosa(selNoRawat, selNoRM, selNamaPasien);
        ina.setVisible(true);
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
    }

    private boolean ada(String sqlExists) throws Exception {
        try (PreparedStatement ps = koneksi.prepareStatement(sqlExists + " limit 1")) {
            ps.setString(1, selNoRawat);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    /** Deteksi 1 jenis berkas tanpa menjatuhkan deteksi jenis lain kalau query ini gagal
     *  (mis. tabel/kolom bermasalah di satu instalasi) — tiap jenis berdiri sendiri. */
    private void centangAman(String key, String... sqlExistsVariants) {
        try {
            boolean adaData = false;
            for (String sql : sqlExistsVariants) {
                if (ada(sql)) { adaData = true; break; }
            }
            centang(key, adaData);
        } catch (Exception e) {
            areaLog.tulis("Gagal deteksi " + key + ": " + e);
        }
    }

    private void centang(String key, boolean adaData) {
        widget.CekBox c = centangDok.get(key);
        if (c == null) return;
        c.setEnabled(adaData);
        c.setSelected(adaData);
        widget.Button btnLihat = tombolLihat.get(key);
        if (btnLihat != null) btnLihat.setEnabled(adaData);
    }

    /** Daftar berkas yang sudah pernah diunggah manual lewat Tag Signer/DlgBerkasRawat lama. */
    private void muatBerkasManual() {
        tabManual.setRowCount(0);
        // kode CMX_% dikecualikan: itu baris yang kita sendiri tulis (lihat tulisHalamanTte) semata
        // untuk menandai NIP dokter buat DlgBerkasKlaim, bukan berkas yang diunggah ke web — filenya
        // cuma ada di scratch lokal saat proses gabung, jadi tidak relevan ditampilkan/diunduh ulang di sini.
        String sql = "select bdp.lokasi_file, mbd.nama from berkas_digital_perawatan bdp " +
                "join master_berkas_digital mbd on mbd.kode=bdp.kode " +
                "where bdp.no_rawat=? and bdp.kode not like 'CMX\\_%' order by mbd.nama";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, selNoRawat);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabManual.addRow(new Object[]{false, rs.getString("nama"), rs.getString("lokasi_file")});
                }
            }
        } catch (Exception e) {
            areaLog.tulis("Gagal memuat berkas manual: " + e);
        }
    }

    // ============================================= GABUNG + KIRIM =============================================
    private File scratchDir;

    private void gabungDanKirim() {
        if (selNoRawat.isEmpty()) return;
        BtnGabung.setEnabled(false);
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        areaLog.bersih();
        List<String> gagal = new ArrayList<>();
        scratchDir = new File("./berkascasemix/" + selNoRawat.replaceAll("/", "_"));
        scratchDir.mkdirs();
        File[] sisa = scratchDir.listFiles();
        if (sisa != null) for (File old : sisa) old.delete();

        int urutan = 1;
        try {
            if (isCentang("sep")) urutan = coba("SEP", urutan, gagal, this::exportSEP);
            if (isCentang("resume")) urutan = coba("Resume", urutan, gagal, this::exportResume);
            if (isCentang("billing")) urutan = coba("Billing", urutan, gagal, this::exportBilling);
            if (isCentang("triase")) urutan = coba("Triase IGD", urutan, gagal, this::exportTriase);
            if (isCentang("asesmen_igd")) urutan = coba("Asesmen Awal Medis IGD", urutan, gagal, this::exportAsesmenIGD);
            if (isCentang("lab")) urutan = coba("Hasil Lab", urutan, gagal, this::exportLab);
            if (isCentang("radiologi")) urutan = coba("Hasil Radiologi", urutan, gagal, this::exportRadiologi);
            if (isCentang("operasi")) urutan = coba("Laporan Operasi", urutan, gagal, this::exportOperasi);

            // Berkas manual yang dicentang: salin apa adanya (download dari hybrid web) ke scratch, ikut digabung.
            for (int i = 0; i < tabManual.getRowCount(); i++) {
                Boolean cek = (Boolean) tabManual.getValueAt(i, 0);
                if (cek != null && cek) {
                    String lokasi = tabManual.getValueAt(i, 2).toString();
                    try {
                        unduhBerkasManual(lokasi, urutan++);
                    } catch (Exception e) {
                        gagal.add("Berkas manual (" + tabManual.getValueAt(i, 1) + "): " + e.getMessage());
                    }
                }
            }

            File merged = mergePDF();
            if (merged == null) {
                JOptionPane.showMessageDialog(this, "Tidak ada berkas yang berhasil digabung.", "Gagal", JOptionPane.ERROR_MESSAGE);
                return;
            }
            areaLog.tulis("Gabung PDF berhasil: " + merged.getName());

            boolean terkirim = kirimKeBerkasKlaim(merged);
            if (terkirim) {
                String pesan = "Berkas casemix berhasil digabung & dikirim ke Berkas Klaim (status MENUNGGU_TTD).\n" +
                        "Buka menu Berkas Klaim untuk TTE estafet.";
                if (!gagal.isEmpty()) {
                    pesan += "\n\nBeberapa dokumen gagal diikutkan:\n- " + String.join("\n- ", gagal);
                }
                JOptionPane.showMessageDialog(this, pesan, "Selesai", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Gabung PDF berhasil tapi gagal mengirim ke Berkas Klaim. Cek log.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }
        } finally {
            setCursor(java.awt.Cursor.getDefaultCursor());
            BtnGabung.setEnabled(true);
        }
    }

    private boolean isCentang(String key) {
        widget.CekBox c = centangDok.get(key);
        return c != null && c.isSelected();
    }

    /** Return value = berapa file yang sebenarnya dihasilkan (biasanya 1, tapi mis. Hasil Lab
     *  pasien Ranap bisa menghasilkan >1 file kalau ada beberapa kali pemeriksaan selama dirawat). */
    @FunctionalInterface private interface Export { int jalan(int urutan) throws Exception; }

    private int coba(String label, int urutan, List<String> gagal, Export ex) {
        try {
            areaLog.tulis("Memproses: " + label + " ...");
            int jumlah = ex.jalan(urutan);
            areaLog.tulis("  -> OK" + (jumlah > 1 ? " (" + jumlah + " berkas)" : ""));
            return urutan + Math.max(1, jumlah);
        } catch (Exception e) {
            areaLog.tulis("  -> GAGAL: " + e);
            gagal.add(label + ": " + e.getMessage());
            return urutan + 1;
        }
    }

    // ============================================= EXPORT PER JENIS =============================================
    // 6 dari 8 jenis berkas TERNYATA PUNYA Jasper report resmi yang headless-callable (terbukti
    // lewat riset ke form asalnya masing-masing: BPJSDataSEP, RMDataResumePasien(Ranap),
    // DlgCariTagihanOperasi, DlgCariPeriksaRadiologi, RMPenilaianAwalMedisIGD, DlgCariPeriksaLab)
    // — dipakai APA ADANYA di sini (renderJasperConn/renderJasperQry), bukan ringkasan buatan
    // sendiri lagi. Billing TIDAK punya Jasper report sama sekali (Nota/Kwitansi asli dirender
    // PHP di server, dibuka lewat browser via Valid.panggilUrl() — tidak bisa dipanggil headless
    // dari Java), dan Triase IGD print resminya terlalu terikat ke state UI form asal (5 varian
    // skala dipilih dari warna hasil deteksi JEditorPane tersembunyi, plus tabel scratch
    // "temporary" yang di-reset per sesi) — keduanya tetap pakai ringkasan PDFBox (DokData).

    /** Hasil pengambilan data 1 jenis berkas (jalur PDFBox, cadangan kalau jasper asli gagal). */
    private static final class DokData {
        final List<String[]> baris; final String judul; final String kdDokter;
        DokData(List<String[]> baris, String judul, String kdDokter) { this.baris = baris; this.judul = judul; this.kdDokter = kdDokter; }
    }

    /** Ringkasan cadangan (field:value) kalau Jasper rptLembarTriaseSkalaN gagal dirender. */
    private DokData dataTriase() throws Exception {
        List<String[]> baris = ambilBaris(
            "select data_triase_igd.tgl_kunjungan as 'Tgl Kunjungan', " +
            "data_triase_igd.cara_masuk as 'Cara Masuk', " +
            "master_triase_macam_kasus.macam_kasus as 'Macam Kasus', " +
            "data_triase_igdprimer.keluhan_utama as 'Keluhan Utama', " +
            "data_triase_igdprimer.kebutuhan_khusus as 'Kebutuhan Khusus', " +
            "data_triase_igdprimer.catatan as 'Catatan', " +
            "data_triase_igdprimer.plan as 'Plan / Tindak Lanjut', " +
            "data_triase_igd.tekanan_darah as 'Tekanan Darah', " +
            "data_triase_igd.nadi as 'Nadi', " +
            "data_triase_igd.pernapasan as 'Pernapasan', " +
            "data_triase_igd.suhu as 'Suhu', " +
            "data_triase_igd.saturasi_o2 as 'Saturasi O2', " +
            "data_triase_igd.nyeri as 'Skala Nyeri', " +
            "pegawai.nama as 'Petugas Triase', " +
            "data_triase_igdprimer.tanggaltriase as 'Tgl Triase' " +
            "from data_triase_igd " +
            "left join data_triase_igdprimer on data_triase_igd.no_rawat=data_triase_igdprimer.no_rawat " +
            "left join master_triase_macam_kasus on master_triase_macam_kasus.kode_kasus=data_triase_igd.kode_kasus " +
            "left join pegawai on pegawai.nik=data_triase_igdprimer.nik " +
            "where data_triase_igd.no_rawat=?", selNoRawat);
        return new DokData(baris, "Triase IGD — " + selNamaPasien, null);
    }

    // ---------------------------------- Triase IGD — 5 varian skala (lihat RMTriaseIGD.CreatePDFV3) ----------------------------------
    // Triase IGD TERNYATA punya 5 lembar berbeda (rptLembarTriaseSkala1..5.jasper) tergantung
    // skala mana yang dipakai perawat saat menilai pasien (skala1/2 = form "primer" dgn keluhan
    // utama+kebutuhan khusus, skala3/4/5 = form "sekunder" dgn anamnesa singkat). Formnya sendiri
    // pilih skala dari WARNA hasil deteksi JEditorPane tersembunyi; di sini dideteksi langsung
    // dari tabel mana yang ADA datanya untuk no_rawat ini — sama persis fallback yang dipakai
    // CreatePDFV3 sendiri kalau warnanya tidak diketahui. Grid pemeriksaan per-skalanya disuplai
    // lewat tabel scratch "temporary" (scope per IP klien, persis CreatePDFV3), BUKAN dari query
    // ber-parameter no_rawat biasa.

    private static final class SkalaInfo {
        final String jasper, masterTable, detailTable, codeCol, contentCol; final boolean primer;
        SkalaInfo(String jasper, String masterTable, String detailTable, String codeCol, String contentCol, boolean primer) {
            this.jasper = jasper; this.masterTable = masterTable; this.detailTable = detailTable;
            this.codeCol = codeCol; this.contentCol = contentCol; this.primer = primer;
        }
    }

    private static final SkalaInfo[] SKALA_TRIASE = {
        new SkalaInfo("rptLembarTriaseSkala1.jasper", "master_triase_skala1", "data_triase_igddetail_skala1", "kode_skala1", "pengkajian_skala1", true),
        new SkalaInfo("rptLembarTriaseSkala2.jasper", "master_triase_skala2", "data_triase_igddetail_skala2", "kode_skala2", "pengkajian_skala2", true),
        new SkalaInfo("rptLembarTriaseSkala3.jasper", "master_triase_skala3", "data_triase_igddetail_skala3", "kode_skala3", "pengkajian_skala3", false),
        new SkalaInfo("rptLembarTriaseSkala4.jasper", "master_triase_skala4", "data_triase_igddetail_skala4", "kode_skala4", "pengkajian_skala4", false),
        new SkalaInfo("rptLembarTriaseSkala5.jasper", "master_triase_skala5", "data_triase_igddetail_skala5", "kode_skala5", "pengkajian_skala5", false),
    };

    private SkalaInfo cariSkalaTriase() throws Exception {
        for (SkalaInfo s : SKALA_TRIASE) {
            if (ada("select 1 from " + s.detailTable + " where no_rawat=?")) return s;
        }
        return null;
    }

    private String qryTriase(SkalaInfo s) {
        String tabelPrimer = s.primer ? "data_triase_igdprimer" : "data_triase_igdsekunder";
        String kolomKeluhan = s.primer ? tabelPrimer + ".keluhan_utama," + tabelPrimer + ".kebutuhan_khusus," : tabelPrimer + ".anamnesa_singkat,";
        return "select " + kolomKeluhan + tabelPrimer + ".catatan," + tabelPrimer + ".plan," + tabelPrimer + ".tanggaltriase," + tabelPrimer + ".nik,"
            + "data_triase_igd.tekanan_darah,data_triase_igd.nadi,data_triase_igd.pernapasan,data_triase_igd.suhu,data_triase_igd.saturasi_o2,data_triase_igd.nyeri,"
            + "data_triase_igd.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir,pegawai.nama,data_triase_igd.tgl_kunjungan,"
            + "data_triase_igd.cara_masuk,master_triase_macam_kasus.macam_kasus from " + tabelPrimer + " inner join data_triase_igd "
            + "inner join pasien inner join pegawai inner join reg_periksa inner join master_triase_macam_kasus on "
            + "data_triase_igd.no_rawat=" + tabelPrimer + ".no_rawat and reg_periksa.no_rawat=data_triase_igd.no_rawat "
            + "and reg_periksa.no_rkm_medis=pasien.no_rkm_medis and pegawai.nik=" + tabelPrimer + ".nik "
            + "and master_triase_macam_kasus.kode_kasus=data_triase_igd.kode_kasus where data_triase_igd.no_rawat='" + selNoRawat + "'";
    }

    private Map<String, Object> paramTriase(SkalaInfo s) throws Exception {
        Map<String, Object> p = paramRS();
        try (PreparedStatement ps = koneksi.prepareStatement(qryTriase(s))) {
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new Exception("Data triase (skala " + s.jasper + ") tidak ditemukan.");
                p.put("norawat", rs.getString("no_rawat"));
                p.put("norm", rs.getString("no_rkm_medis"));
                p.put("namapasien", rs.getString("nm_pasien"));
                p.put("tanggallahir", rs.getDate("tgl_lahir"));
                String jk = rs.getString("jk");
                p.put("jk", jk == null ? "" : jk.replace("L", "Laki-Laki").replace("P", "Perempuan"));
                p.put("tanggalkunjungan", rs.getDate("tgl_kunjungan"));
                String tglKunjungan = rs.getString("tgl_kunjungan");
                p.put("jamkunjungan", tglKunjungan != null && tglKunjungan.length() >= 19 ? tglKunjungan.substring(11, 19) : "");
                p.put("caradatang", rs.getString("cara_masuk"));
                p.put("macamkasus", rs.getString("macam_kasus"));
                if (s.primer) {
                    p.put("keluhanutama", rs.getString("keluhan_utama"));
                    p.put("kebutuhankhusus", rs.getString("kebutuhan_khusus"));
                } else {
                    p.put("keluhanutama", rs.getString("anamnesa_singkat"));
                    p.put("kebutuhankhusus", "-");
                }
                p.put("plan", rs.getString("plan"));
                p.put("tanggaltriase", rs.getDate("tanggaltriase"));
                String tglTriase = rs.getString("tanggaltriase");
                p.put("jamtriase", tglTriase != null && tglTriase.length() >= 19 ? tglTriase.substring(11, 19) : "");
                p.put("pegawai", rs.getString("nama"));
                p.put("catatan", rs.getString("catatan"));
                p.put("tandavital", "Suhu (C) : " + rs.getString("suhu") + ", Nyeri : " + rs.getString("nyeri") + ", Tensi : " + rs.getString("tekanan_darah")
                        + ", Nadi(/menit) : " + rs.getString("nadi") + ", Saturasi O2(%) : " + rs.getString("saturasi_o2") + ", Respirasi(/menit) : " + rs.getString("pernapasan"));
                // Petugas triase = perawat (pegawai.nik), bukan dokter -> tidak didaftarkan sbg signer TTE.
                p.put("finger", "#");
            }
        }
        return p;
    }

    /** Staging detail skala ke tabel scratch "temporary" (scope per-IP klien) — persis logic
     *  CreatePDFV3; jrxml rptLembarTriaseSkalaN membaca grid pemeriksaannya dari sini, bukan
     *  query ber-parameter no_rawat biasa. */
    private void stageTemporaryTriase(SkalaInfo s) throws Exception {
        String ip = akses.getalamatip();
        try (java.sql.Statement st = koneksi.createStatement()) {
            st.executeUpdate("delete from temporary where temp37='" + sqlEsc(ip) + "'");
        }
        try (PreparedStatement ps2 = koneksi.prepareStatement(
                "select master_triase_pemeriksaan.kode_pemeriksaan, master_triase_pemeriksaan.nama_pemeriksaan " +
                "from master_triase_pemeriksaan inner join " + s.masterTable + " inner join " + s.detailTable + " " +
                "on master_triase_pemeriksaan.kode_pemeriksaan=" + s.masterTable + ".kode_pemeriksaan and " +
                s.masterTable + "." + s.codeCol + "=" + s.detailTable + "." + s.codeCol + " where " + s.detailTable + ".no_rawat=? " +
                "group by master_triase_pemeriksaan.kode_pemeriksaan order by master_triase_pemeriksaan.kode_pemeriksaan")) {
            ps2.setString(1, selNoRawat);
            try (ResultSet rs2 = ps2.executeQuery()) {
                int i = 0;
                while (rs2.next()) {
                    StringBuilder data = new StringBuilder();
                    try (PreparedStatement ps3 = koneksi.prepareStatement(
                            "select " + s.masterTable + "." + s.contentCol + " from " + s.masterTable + " inner join " + s.detailTable + " " +
                            "on " + s.masterTable + "." + s.codeCol + "=" + s.detailTable + "." + s.codeCol + " where " +
                            s.masterTable + ".kode_pemeriksaan=? and " + s.detailTable + ".no_rawat=? order by " + s.detailTable + "." + s.codeCol)) {
                        ps3.setString(1, rs2.getString("kode_pemeriksaan"));
                        ps3.setString(2, selNoRawat);
                        try (ResultSet rs3 = ps3.executeQuery()) {
                            while (rs3.next()) {
                                if (data.length() > 0) data.append(", ");
                                data.append(rs3.getString(s.contentCol));
                            }
                        }
                    }
                    // Literal kolom sengaja disalin APA ADANYA dari RMTriaseIGD.CreatePDFV3 (bukan
                    // dihitung ulang manual) supaya jumlah kolom "temporary" pasti cocok.
                    Sequel.menyimpan2("temporary",
                            "'" + i + "','" + sqlEsc(rs2.getString("nama_pemeriksaan")) + "','" + sqlEsc(data.toString()) +
                            "','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','" + sqlEsc(ip) + "'",
                            "Transaksi");
                    i++;
                }
            }
        }
    }

    private int exportTriase(int urutan) throws Exception {
        try {
            SkalaInfo s = cariSkalaTriase();
            if (s == null) throw new Exception("Tidak ada data detail skala triase untuk kunjungan ini.");
            stageTemporaryTriase(s);
            String qry = "select * from temporary where temp37='" + sqlEsc(akses.getalamatip()) + "' order by no";
            renderJasperQry(urutan, "TRIASE", s.jasper, "::[ Triase IGD ]::", qry, paramTriase(s), null);
        } catch (Exception e) {
            areaLog.tulis("  gagal render Jasper Triase (" + e + "), pakai ringkasan sendiri sebagai fallback");
            DokData d = dataTriase();
            tulisHalaman(urutan, "TRIASE", d.judul, d.baris, null);
        }
        return 1;
    }

    // ---------------------------------- Billing — tabel PDFBox sendiri (bukan "field: value") ----------------------------------
    // Billing tidak punya Jasper report sama sekali (Nota/Kwitansi asli dirender PHP di server,
    // dibuka lewat browser via Valid.panggilUrl() — tidak bisa dipanggil headless dari Java).
    // Dulu dirender pakai renderKe() yang format "field: value" per baris — cocok untuk data
    // 1 record banyak kolom (SEP dll), TAPI RUSAK untuk billing: tiap baris billing adalah ITEM
    // berbeda (bisa puluhan untuk Ranap lama dirawat), jadi perlu tabel beneran dengan pagination
    // + baris total, bukan daftar "field: value" yang kepotong di 20 baris tanpa total.

    private static final class BillingRow { final String no, nama, biaya; BillingRow(String no, String nama, String biaya) { this.no = no; this.nama = nama; this.biaya = biaya; } }

    private List<BillingRow> billingRows() throws Exception {
        List<BillingRow> out = new ArrayList<>();
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select no, nm_perawatan, if(totalbiaya=0,'',totalbiaya) from billing where no_rawat=? order by noindex")) {
            ps.setString(1, selNoRawat);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(new BillingRow(rs.getString(1), rs.getString(2), rs.getString(3)));
            }
        }
        return out;
    }

    private String formatRupiah(String s) {
        if (s == null || s.trim().isEmpty()) return "";
        try { return String.format("%,d", Long.parseLong(s.trim())).replace(',', '.'); } catch (Exception e) { return s; }
    }

    private float tabelHeaderBilling(PDPageContentStream cs, float left, float right, float y) throws Exception {
        cs.setFont(PDType1Font.HELVETICA_BOLD, 9);
        cs.beginText(); cs.newLineAtOffset(left, y); cs.showText("No"); cs.endText();
        cs.beginText(); cs.newLineAtOffset(left + 70, y); cs.showText("Nama Perawatan"); cs.endText();
        cs.beginText(); cs.newLineAtOffset(right - 40, y); cs.showText("Biaya"); cs.endText();
        y -= 4;
        cs.setLineWidth(0.7f); cs.moveTo(left, y); cs.lineTo(right, y); cs.stroke();
        return y - 12;
    }

    /** Kop surat RS (logo + nama + alamat + kontak) — dipakai supaya halaman fallback PDFBox
     *  (Billing, dan Triase kalau Jasper-nya gagal) tidak polos tanpa identitas RS seperti
     *  report resmi lainnya. Gagal ambil logo tidak menggagalkan render (teks tetap tampil). */
    private float gambarKopRS(PDDocument doc, PDPageContentStream cs, float left, float right, float y) {
        float textLeft = left;
        try {
            java.io.ByteArrayInputStream logoStream = Sequel.cariGambar("select setting.logo from setting");
            if (logoStream != null) {
                byte[] logoBytes = logoStream.readAllBytes();
                if (logoBytes != null && logoBytes.length > 0) {
                    org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject img =
                            org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject.createFromByteArray(doc, logoBytes, "logo");
                    cs.drawImage(img, left, y - 38, 42, 42);
                    textLeft = left + 52;
                }
            }
        } catch (Exception ignore) { }

        try {
            cs.setFont(PDType1Font.HELVETICA_BOLD, 13);
            cs.beginText(); cs.newLineAtOffset(textLeft, y); cs.showText(potong(akses.getnamars(), 70)); cs.endText();
            y -= 15;
            cs.setFont(PDType1Font.HELVETICA, 8);
            String alamat = (akses.getalamatrs() == null ? "" : akses.getalamatrs()) + ", "
                    + (akses.getkabupatenrs() == null ? "" : akses.getkabupatenrs()) + ", "
                    + (akses.getpropinsirs() == null ? "" : akses.getpropinsirs());
            cs.beginText(); cs.newLineAtOffset(textLeft, y); cs.showText(potong(alamat, 95)); cs.endText();
            y -= 11;
            cs.beginText(); cs.newLineAtOffset(textLeft, y); cs.showText(potong(akses.getkontakrs() == null ? "" : akses.getkontakrs(), 95)); cs.endText();
            y -= 14;
            cs.setLineWidth(1f);
            cs.moveTo(left, y); cs.lineTo(right, y); cs.stroke();
        } catch (Exception ignore) { }
        return y - 16;
    }

    private void renderTabelBilling(File out, String judul) throws Exception {
        List<BillingRow> rows = billingRows();
        String totalStr = cari("select ifnull(sum(totalbiaya),0) from billing where no_rawat=?", selNoRawat);
        long total = totalStr.trim().isEmpty() ? 0 : Long.parseLong(totalStr.trim());

        float left = 40, right = 555, top = 800;
        PDDocument doc = new PDDocument();
        try {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            PDPageContentStream cs = new PDPageContentStream(doc, page);
            float y = top;
            y = gambarKopRS(doc, cs, left, right, y);
            cs.setFont(PDType1Font.HELVETICA_BOLD, 13);
            cs.beginText(); cs.newLineAtOffset(left, y); cs.showText(potong(judul, 90)); cs.endText();
            y -= 20;
            y = tabelHeaderBilling(cs, left, right, y);
            cs.setFont(PDType1Font.HELVETICA, 8);

            if (rows.isEmpty()) {
                cs.setFont(PDType1Font.HELVETICA_OBLIQUE, 9);
                cs.beginText(); cs.newLineAtOffset(left, y); cs.showText("(tidak ada data billing)"); cs.endText();
                y -= 14;
            }
            for (BillingRow r : rows) {
                if (y < 70) {
                    cs.close();
                    page = new PDPage(PDRectangle.A4);
                    doc.addPage(page);
                    cs = new PDPageContentStream(doc, page);
                    y = top;
                    y = tabelHeaderBilling(cs, left, right, y);
                    cs.setFont(PDType1Font.HELVETICA, 8);
                }
                cs.beginText(); cs.newLineAtOffset(left, y); cs.showText(potong(r.no == null ? "" : r.no, 10)); cs.endText();
                cs.beginText(); cs.newLineAtOffset(left + 70, y); cs.showText(potong(r.nama == null ? "" : r.nama, 62)); cs.endText();
                String biayaFmt = formatRupiah(r.biaya);
                float biayaW = PDType1Font.HELVETICA.getStringWidth(biayaFmt) / 1000f * 8f;
                cs.beginText(); cs.newLineAtOffset(right - biayaW, y); cs.showText(biayaFmt); cs.endText();
                y -= 12;
            }

            y -= 6;
            cs.setLineWidth(0.7f); cs.moveTo(left, y); cs.lineTo(right, y); cs.stroke();
            y -= 14;
            cs.setFont(PDType1Font.HELVETICA_BOLD, 10);
            cs.beginText(); cs.newLineAtOffset(left, y); cs.showText("TOTAL BIAYA"); cs.endText();
            String totalFmt = formatRupiah(String.valueOf(total));
            float totalW = PDType1Font.HELVETICA_BOLD.getStringWidth(totalFmt) / 1000f * 10f;
            cs.beginText(); cs.newLineAtOffset(right - totalW, y); cs.showText(totalFmt); cs.endText();
            cs.close();
            doc.save(out);
        } finally {
            doc.close();
        }
    }

    /** URL PHP Nota resmi — persis pola "Nota PDF TTE" yang sudah ada di DlgBilingRalan/Ranap.
     *  Ralan pakai billing/LaporanBilling.php (param tanggal=tgl registrasi), Ranap pakai
     *  billing/LaporanBilling2.php (tambahan param ttl=total biaya, dipakai utk header cetak). */
    private String urlBillingPHP() throws Exception {
        boolean ranap = "Ranap".equals(selStatusLanjut);
        String petugasEnc = java.net.URLEncoder.encode(akses.getkode(), "UTF-8");
        String phpQuery;
        if (ranap) {
            String tanggal = cari("select DATE_FORMAT(tgl_keluar,'%d-%m-%Y') from kamar_inap where no_rawat=? order by tgl_keluar desc limit 1", selNoRawat);
            if (tanggal.trim().isEmpty()) tanggal = new SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date());
            String ttl = cari("select ifnull(sum(totalbiaya),0) from billing where no_rawat=?", selNoRawat);
            if (ttl.trim().isEmpty()) ttl = "0";
            phpQuery = "billing/LaporanBilling2.php?usere=" + koneksiDB.USERHYBRIDWEB() + "&passwordte=" + koneksiDB.PASHYBRIDWEB()
                    + "&petugas=" + petugasEnc + "&tanggal=" + java.net.URLEncoder.encode(tanggal, "UTF-8") + "&ttl=" + ttl;
        } else {
            String tanggal = cari("select DATE_FORMAT(tgl_registrasi,'%d-%m-%Y') from reg_periksa where no_rawat=?", selNoRawat);
            if (tanggal.trim().isEmpty()) tanggal = new SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date());
            phpQuery = "billing/LaporanBilling.php?usere=" + koneksiDB.USERHYBRIDWEB() + "&passwordte=" + koneksiDB.PASHYBRIDWEB()
                    + "&petugas=" + petugasEnc + "&tanggal=" + java.net.URLEncoder.encode(tanggal, "UTF-8");
        }
        return "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/" + phpQuery;
    }

    private String fetchHtml(String url) throws Exception {
        java.net.URL u = new java.net.URL(url);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) u.openConnection();
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(30000);
        try (java.io.InputStream is = conn.getInputStream()) {
            byte[] data = is.readAllBytes();
            return new String(data, java.nio.charset.StandardCharsets.UTF_8);
        } finally {
            conn.disconnect();
        }
    }

    private int exportBilling(int urutan) throws Exception {
        File out = new File(scratchDir, String.format("%03d_%s.pdf", urutan, "BILLING"));
        try {
            String html = fetchHtml(urlBillingPHP());
            try (java.io.OutputStream os = new java.io.FileOutputStream(out)) {
                com.itextpdf.html2pdf.HtmlConverter.convertToPdf(html, os);
            }
            areaLog.tulis("  file: " + out.getName() + " (Nota PHP resmi)");
        } catch (Exception e) {
            areaLog.tulis("  gagal ambil Nota PHP (" + e + "), pakai tabel ringkasan sendiri sebagai fallback");
            renderTabelBilling(out, "Billing — " + selNamaPasien);
            areaLog.tulis("  file: " + out.getName());
        }
        return 1;
    }

    // ---------------------------------- Infrastruktur render Jasper resmi ----------------------------------

    /** Param RS profile standar yang dipakai hampir semua report Khanza. */
    private Map<String, Object> paramRS() {
        Map<String, Object> p = new HashMap<>();
        p.put("namars", akses.getnamars());
        p.put("alamatrs", akses.getalamatrs());
        p.put("kotars", akses.getkabupatenrs());
        p.put("propinsirs", akses.getpropinsirs());
        p.put("kontakrs", akses.getkontakrs());
        p.put("emailrs", akses.getemailrs());
        p.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        return p;
    }

    /** select 1 kolom dengan N parameter — generalisasi nilaiKolom (yang cuma 1 parameter). */
    private String cari(String sql, String... params) throws Exception {
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) ps.setString(i + 1, params[i]);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return "";
                String v = rs.getString(1);
                return v == null ? "" : v;
            }
        }
    }

    /** Pindahkan hasil render jasper (./tmpPDF/{uniqueName}.pdf) ke scratch dengan nomor urut,
     *  lalu—kalau perlu TTE—catat ke berkas_digital_perawatan. Dipakai bersama oleh render
     *  connection-based maupun qry-based supaya logic "finalisasi"-nya tidak dobel. */
    private void finalisasiJasper(String uniqueName, int urutan, String kodeFile, String reportName, String judul, String kdDokter) throws Exception {
        finalisasiJasper(uniqueName, urutan, kodeFile, reportName, judul, kdDokter, DB_KODE_TTE.get(kodeFile));
    }

    /** Varian dengan dbKode eksplisit — dipakai saat 1 jenis berkas bisa menghasilkan >1 file
     *  dalam satu proses gabung (mis. Hasil Lab utk Ranap dgn beberapa kali pemeriksaan): tiap
     *  file butuh kode master_berkas_digital SENDIRI, kalau tidak baris berkas_digital_perawatan
     *  file sebelumnya akan ketimpa (DELETE+INSERT pakai kode yang sama) dan dokter-nya hilang
     *  dari daftar signer. */
    /** Buang halaman kosong di AKHIR PDF. Sebagian report Khanza punya band tinggi yang melar
     *  (mis. uraian operasi panjang) sehingga border tabelnya tumpah ke halaman berikut yang
     *  isinya cuma garis tanpa teks — halaman seperti itu dibuang. Hanya dari belakang, berhenti
     *  di halaman pertama (dari belakang) yang masih ada teksnya, dan selalu sisakan min 1 halaman.
     *  Tidak mengubah report bersama (.jasper/.jrxml) — murni pasca-proses PDF di fitur ini. */
    private void buangHalamanKosongAkhir(File pdf) {
        try (org.apache.pdfbox.pdmodel.PDDocument doc = org.apache.pdfbox.pdmodel.PDDocument.load(pdf)) {
            org.apache.pdfbox.text.PDFTextStripper stripper = new org.apache.pdfbox.text.PDFTextStripper();
            boolean adaYgDibuang = false;
            for (int i = doc.getNumberOfPages() - 1; i >= 1; i--) {
                stripper.setStartPage(i + 1);
                stripper.setEndPage(i + 1);
                String teks = stripper.getText(doc).replaceAll("\\s+", "");
                if (teks.isEmpty()) {
                    doc.removePage(i);
                    adaYgDibuang = true;
                } else {
                    break;
                }
            }
            if (adaYgDibuang) doc.save(pdf);
        } catch (Exception e) {
            areaLog.tulis("  (gagal buang halaman kosong: " + e + " — PDF dibiarkan apa adanya)");
        }
    }

    private void finalisasiJasper(String uniqueName, int urutan, String kodeFile, String reportName, String judul, String kdDokter, String dbKode) throws Exception {
        File src = new File("./tmpPDF/" + uniqueName + ".pdf");
        if (!src.exists() || src.length() == 0) {
            throw new Exception("Jasper (" + reportName + ") tidak menghasilkan PDF — cek console untuk error JasperReports.");
        }
        buangHalamanKosongAkhir(src);
        File dest = new File(scratchDir, String.format("%03d_%s.pdf", urutan, kodeFile));
        if (!src.renameTo(dest)) {
            java.nio.file.Files.copy(src.toPath(), dest.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            src.delete();
        }
        areaLog.tulis("  file: " + dest.getName() + " (jasper resmi: " + reportName + ")");
        if (kdDokter != null && !kdDokter.trim().isEmpty()) {
            String kode = kodeBerkas(dbKode, "Casemix - " + judul);
            String lokasiFile = "casemix/" + dest.getName();
            try (PreparedStatement ps = koneksi.prepareStatement(
                    "delete from berkas_digital_perawatan where no_rawat=? and kode=? and nip=?")) {
                ps.setString(1, selNoRawat); ps.setString(2, kode); ps.setString(3, kdDokter);
                ps.executeUpdate();
            }
            Sequel.menyimpantf("berkas_digital_perawatan", "?,?,?,?", "No.Rawat", 4,
                    new String[]{selNoRawat, kode, lokasiFile, kdDokter});
            areaLog.tulis("  tag dokter " + kdDokter);
        } else {
            areaLog.tulis("  tanpa tag dokter");
        }
    }

    /** Jasper yang SQL utamanya sudah embedded di .jrxml sendiri (fill pakai Connection). */
    private void renderJasperConn(int urutan, String kodeFile, String reportName, String judul, Map<String, Object> param, String kdDokter) throws Exception {
        String uniqueName = String.format("casemix_%s_%d", kodeFile, System.nanoTime());
        Valid.MyReportPDFUpload(reportName, "report", judul, uniqueName, param);
        finalisasiJasper(uniqueName, urutan, kodeFile, reportName, judul, kdDokter);
    }

    private void renderJasperConn(int urutan, String kodeFile, String reportName, String judul, Map<String, Object> param, String kdDokter, String dbKode) throws Exception {
        String uniqueName = String.format("casemix_%s_%d", kodeFile, System.nanoTime());
        Valid.MyReportPDFUpload(reportName, "report", judul, uniqueName, param);
        finalisasiJasper(uniqueName, urutan, kodeFile, reportName, judul, kdDokter, dbKode);
    }

    /** Jasper yang datanya dipasok Java sendiri lewat SQL (qry-variant, JRResultSetDataSource). */
    private void renderJasperQry(int urutan, String kodeFile, String reportName, String judul, String qry, Map<String, Object> param, String kdDokter) throws Exception {
        String uniqueName = String.format("casemix_%s_%d", kodeFile, System.nanoTime());
        Valid.MyReportPDFqryUpload(reportName, "report", judul, qry, uniqueName, param);
        finalisasiJasper(uniqueName, urutan, kodeFile, reportName, judul, kdDokter);
    }

    /** Versi pratinjau — sama persis tapi hasilnya langsung dibuka (Desktop), TIDAK masuk
     *  scratch/merge dan TIDAK mendaftarkan signer (murni baca, tombol "Lihat"). */
    private void previewJasperConn(String reportName, String judul, Map<String, Object> param) throws Exception {
        String uniqueName = "preview_" + System.nanoTime();
        Valid.MyReportPDFUpload(reportName, "report", judul, uniqueName, param);
        File src = new File("./tmpPDF/" + uniqueName + ".pdf");
        if (!src.exists()) throw new Exception("Jasper (" + reportName + ") tidak menghasilkan PDF.");
        buangHalamanKosongAkhir(src);
        bukaFile(src);
    }

    private void previewJasperQry(String reportName, String judul, String qry, Map<String, Object> param) throws Exception {
        String uniqueName = "preview_" + System.nanoTime();
        Valid.MyReportPDFqryUpload(reportName, "report", judul, qry, uniqueName, param);
        File src = new File("./tmpPDF/" + uniqueName + ".pdf");
        if (!src.exists()) throw new Exception("Jasper (" + reportName + ") tidak menghasilkan PDF.");
        buangHalamanKosongAkhir(src);
        bukaFile(src);
    }

    // ---------------------------------- SEP (rptBridgingSEP[2].jasper, lihat BPJSDataSEP.java) ----------------------------------

    private String reportSEP() throws Exception {
        // Patokan Rajal/Ranap pakai selStatusLanjut (sumber yang sama dipakai konsisten di semua
        // jenis berkas lain di sini) — BUKAN bridging_sep.jnspelayanan, karena kolom itu kadang
        // tidak sinkron dengan status kunjungan aslinya di reg_periksa (mis. pasien yang masuk
        // IGD lalu diputuskan rawat inap, SEP-nya bisa masih tercatat jnspelayanan Rajal).
        return "Ranap".equals(selStatusLanjut) ? "rptBridgingSEP2.jasper" : "rptBridgingSEP.jasper";
    }

    private Map<String, Object> paramSEP() throws Exception {
        Map<String, Object> p = new HashMap<>();
        p.put("namars", akses.getnamars());
        p.put("alamatrs", akses.getalamatrs());
        p.put("kotars", akses.getkabupatenrs());
        p.put("propinsirs", akses.getpropinsirs());
        p.put("kontakrs", akses.getkontakrs());
        p.put("prb", cari("select bpjs_prb.prb from bpjs_prb where bpjs_prb.no_sep=?", selNoSep));
        p.put("logo", Sequel.cariGambar("select gambar.bpjs from gambar"));
        p.put("parameter", selNoSep); // dipakai jrxml sebagai filter no_sep
        p.put("jk", cari("select pasien.jk from pasien inner join reg_periksa on pasien.no_rkm_medis=reg_periksa.no_rkm_medis where reg_periksa.no_rawat=?", selNoRawat));
        return p;
    }

    private int exportSEP(int urutan) throws Exception {
        renderJasperConn(urutan, "SEP", reportSEP(), "::[ Cetak SEP ]::", paramSEP(), null);
        return 1;
    }

    // ---------------------------------- Resume Ralan/Ranap (lihat RMDataResumePasien[Ranap].java) ----------------------------------

    private String qryResumeRalan() {
        return "select reg_periksa.tgl_registrasi,reg_periksa.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir, " +
            "reg_periksa.umurdaftar,reg_periksa.sttsumur,pasien.pekerjaan,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab,', ',propinsi.nm_prop) as alamat, " +
            "resume_pasien.kd_dokter,dokter.nm_dokter,resume_pasien.kondisi_pulang,resume_pasien.keluhan_utama,resume_pasien.jalannya_penyakit, " +
            "resume_pasien.pemeriksaan_penunjang,resume_pasien.hasil_laborat,resume_pasien.diagnosa_utama,resume_pasien.kd_diagnosa_utama, " +
            "resume_pasien.diagnosa_sekunder,resume_pasien.kd_diagnosa_sekunder,resume_pasien.diagnosa_sekunder2,resume_pasien.kd_diagnosa_sekunder2, " +
            "resume_pasien.diagnosa_sekunder3,resume_pasien.kd_diagnosa_sekunder3,resume_pasien.diagnosa_sekunder4,resume_pasien.kd_diagnosa_sekunder4, " +
            "resume_pasien.prosedur_utama,resume_pasien.kd_prosedur_utama,resume_pasien.prosedur_sekunder,resume_pasien.kd_prosedur_sekunder, " +
            "resume_pasien.prosedur_sekunder2,resume_pasien.kd_prosedur_sekunder2,resume_pasien.prosedur_sekunder3,resume_pasien.kd_prosedur_sekunder3, " +
            "resume_pasien.obat_pulang,resume_pasien.keadaan,resume_pasien.ket_keadaan,resume_pasien.cara_keluar,resume_pasien.ket_keluar " +
            "from resume_pasien inner join reg_periksa on resume_pasien.no_rawat=reg_periksa.no_rawat " +
            "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis inner join dokter on resume_pasien.kd_dokter=dokter.kd_dokter " +
            "inner join kelurahan on pasien.kd_kel=kelurahan.kd_kel inner join kecamatan on pasien.kd_kec=kecamatan.kd_kec " +
            "inner join kabupaten on pasien.kd_kab=kabupaten.kd_kab inner join propinsi on pasien.kd_prop=propinsi.kd_prop " +
            "where resume_pasien.no_rawat='" + selNoRawat + "'";
    }

    private String qryResumeRanap() {
        return "select reg_periksa.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,resume_pasien_ranap.kd_dokter,dokter.nm_dokter,reg_periksa.kd_dokter as kodepengirim,pengirim.nm_dokter as pengirim," +
            "reg_periksa.tgl_registrasi,reg_periksa.jam_reg,resume_pasien_ranap.diagnosa_awal,resume_pasien_ranap.alasan,resume_pasien_ranap.keluhan_utama,resume_pasien_ranap.pemeriksaan_fisik," +
            "resume_pasien_ranap.jalannya_penyakit,resume_pasien_ranap.pemeriksaan_penunjang,resume_pasien_ranap.hasil_laborat,resume_pasien_ranap.tindakan_dan_operasi,resume_pasien_ranap.obat_di_rs," +
            "resume_pasien_ranap.diagnosa_utama,resume_pasien_ranap.kd_diagnosa_utama,resume_pasien_ranap.diagnosa_sekunder,resume_pasien_ranap.kd_diagnosa_sekunder,resume_pasien_ranap.diagnosa_sekunder2," +
            "resume_pasien_ranap.kd_diagnosa_sekunder2,resume_pasien_ranap.diagnosa_sekunder3,resume_pasien_ranap.kd_diagnosa_sekunder3,resume_pasien_ranap.diagnosa_sekunder4," +
            "resume_pasien_ranap.kd_diagnosa_sekunder4,resume_pasien_ranap.prosedur_utama,resume_pasien_ranap.kd_prosedur_utama,resume_pasien_ranap.prosedur_sekunder,resume_pasien_ranap.kd_prosedur_sekunder," +
            "resume_pasien_ranap.prosedur_sekunder2,resume_pasien_ranap.kd_prosedur_sekunder2,resume_pasien_ranap.prosedur_sekunder3,resume_pasien_ranap.kd_prosedur_sekunder3,resume_pasien_ranap.alergi," +
            "resume_pasien_ranap.diet,resume_pasien_ranap.lab_belum,resume_pasien_ranap.edukasi,resume_pasien_ranap.cara_keluar,resume_pasien_ranap.ket_keluar,resume_pasien_ranap.keadaan," +
            "resume_pasien_ranap.ket_keadaan,resume_pasien_ranap.dilanjutkan,resume_pasien_ranap.ket_dilanjutkan,resume_pasien_ranap.kontrol,resume_pasien_ranap.obat_pulang,reg_periksa.kd_pj,penjab.png_jawab," +
            "reg_periksa.umurdaftar,reg_periksa.sttsumur,pasien.pekerjaan,pasien.jk,pasien.tgl_lahir,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat " +
            "from resume_pasien_ranap inner join reg_periksa on resume_pasien_ranap.no_rawat=reg_periksa.no_rawat inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis " +
            "inner join dokter on resume_pasien_ranap.kd_dokter=dokter.kd_dokter inner join dokter as pengirim on reg_periksa.kd_dokter=pengirim.kd_dokter " +
            "inner join kelurahan on pasien.kd_kel=kelurahan.kd_kel inner join kecamatan on pasien.kd_kec=kecamatan.kd_kec inner join kabupaten on pasien.kd_kab=kabupaten.kd_kab " +
            "inner join penjab on penjab.kd_pj=reg_periksa.kd_pj " +
            "where resume_pasien_ranap.no_rawat='" + selNoRawat + "'";
    }

    private Map<String, Object> paramResume(boolean ranap, String kdDokter) throws Exception {
        Map<String, Object> p = paramRS();
        p.put("logobsre", Sequel.cariGambar("select gambar.bsre from gambar"));
        p.put("norawat", selNoRawat);
        if (ranap) {
            p.put("logo2", Sequel.cariGambar("select setting.logo2 from setting"));
            p.put("ruang", cari("select nm_bangsal from bangsal inner join kamar inner join kamar_inap on bangsal.kd_bangsal=kamar.kd_bangsal and kamar_inap.kd_kamar=kamar.kd_kamar where no_rawat=? order by tgl_masuk desc limit 1", selNoRawat));
            p.put("tanggalkeluar", cari("select DATE_FORMAT(tgl_keluar,'%d-%m-%Y') from kamar_inap where no_rawat=? order by tgl_keluar desc limit 1", selNoRawat));
            p.put("jamkeluar", cari("select DATE_FORMAT(tgl_keluar,'%H:%i') from kamar_inap where no_rawat=? order by tgl_keluar desc limit 1", selNoRawat));
        } else {
            p.put("ruang", cari("select poliklinik.nm_poli from poliklinik inner join reg_periksa on reg_periksa.kd_poli=poliklinik.kd_poli where reg_periksa.no_rawat=?", selNoRawat));
            p.put("tanggalkeluar", cari("select DATE_FORMAT(tgl_registrasi,'%d-%m-%Y') from reg_periksa where no_rawat=?", selNoRawat));
        }
        String tag = resolveTagSigner(kdDokter);
        p.put("finger", tag != null ? tag : "#");
        return p;
    }

    /** Resume bisa ada di resume_pasien ATAU resume_pasien_ranap — jangan cuma tebak dari
     *  selStatusLanjut (yang untuk pasien IGD berisi label tampilan "IGD", bukan status_lanjut
     *  asli), tapi cek langsung mana yang BENAR-BENAR ada datanya, persis seperti centangAman
     *  di pilihPasien(). Itu sebabnya sebelumnya muncul "tidak ada" walau datanya ada. */
    private String[] cariTabelResume() throws Exception {
        boolean adaRalan = ada("select 1 from resume_pasien where no_rawat=?");
        boolean adaRanap = ada("select 1 from resume_pasien_ranap where no_rawat=?");
        if (!adaRalan && !adaRanap) return null;
        boolean ranap = adaRanap && !adaRalan;
        return new String[]{ranap ? "resume_pasien_ranap" : "resume_pasien", ranap ? "1" : "0"};
    }

    private int exportResume(int urutan) throws Exception {
        String[] t = cariTabelResume();
        if (t == null) throw new Exception("Tidak ada data resume_pasien/resume_pasien_ranap untuk kunjungan ini.");
        boolean ranap = "1".equals(t[1]);
        String tabel = t[0];
        String kdDokter = cari("select kd_dokter from " + tabel + " where no_rawat=?", selNoRawat);
        String reportName = ranap ? "rptLaporanResumeRanapTTE.jasper" : "rptLaporanResumeTte.jasper";
        String qry = ranap ? qryResumeRanap() : qryResumeRalan();
        String judul = "Resume " + (ranap ? "Rawat Inap" : "Rawat Jalan") + " — " + selNamaPasien;
        renderJasperQry(urutan, "RESUME", reportName, "::[ Laporan Resume ]::", qry, paramResume(ranap, kdDokter), kdDokter);
        return 1;
    }

    // ---------------------------------- Laporan Operasi (lihat RMLaporanOperasi.java, tabel laporan_operasi_casemix) ----------------------------------
    // Form "Laporan Operasi" yang benar untuk casemix PUNYA tabelnya sendiri (laporan_operasi_casemix,
    // diisi lewat RMLaporanOperasi.java), terpisah dari tabel klinis lama operasi/laporan_operasi yang
    // dipakai DlgCariTagihanOperasi. rptLaporanOperasiCasemix.jasper adalah report resminya.

    private String qryOperasiCasemix(String tglOperasi) {
        return "SELECT reg_periksa.no_rawat, reg_periksa.no_rkm_medis, pasien.nm_pasien, pasien.tgl_lahir, pasien.jk, " +
            "reg_periksa.umurdaftar, reg_periksa.sttsumur, pasien.alamat, pasien.no_tlp, " +
            "laporan_operasi_casemix.no_rawat, laporan_operasi_casemix.tanggal, laporan_operasi_casemix.kddokterbedah, laporan_operasi_casemix.kddokteranestesi, " +
            "laporan_operasi_casemix.asistenbedah, laporan_operasi_casemix.asistenanestesi, laporan_operasi_casemix.jenisoperasi, laporan_operasi_casemix.jenisanestesi, laporan_operasi_casemix.diagnosaprabedah, laporan_operasi_casemix.diagnosapascabedah, " +
            "laporan_operasi_casemix.tindakan, laporan_operasi_casemix.jammulaioperasi, laporan_operasi_casemix.jamselesaioperasi, laporan_operasi_casemix.lama, laporan_operasi_casemix.pembiusan, laporan_operasi_casemix.posisi, " +
            "laporan_operasi_casemix.jammulaianestesi, laporan_operasi_casemix.jamselesaianestesi, laporan_operasi_casemix.jampindah, laporan_operasi_casemix.uraian, laporan_operasi_casemix.komplikasi, laporan_operasi_casemix.perdarahan, " +
            "laporan_operasi_casemix.dikirim, laporan_operasi_casemix.dikirimket, laporan_operasi_casemix.asaljaringan, laporan_operasi_casemix.pemberianobat, laporan_operasi_casemix.infus, laporan_operasi_casemix.diet, laporan_operasi_casemix.pemantauan, laporan_operasi_casemix.jadwal, laporan_operasi_casemix.noregist, " +
            "dokter_bedah.nm_dokter AS nama_dokter_bedah, dokter_anestesi.nm_dokter AS nama_dokter_anestesi, " +
            "petugas_bedah.nama AS nama_asisten_bedah, petugas_anestesi.nama AS nama_asisten_anestesi, " +
            "TIMESTAMPDIFF(MINUTE, laporan_operasi_casemix.jammulaioperasi, laporan_operasi_casemix.jamselesaioperasi) AS durasi_operasi, " +
            "TIMESTAMPDIFF(MINUTE, laporan_operasi_casemix.jammulaianestesi, laporan_operasi_casemix.jamselesaianestesi) AS durasi_anestesi " +
            "FROM laporan_operasi_casemix " +
            "INNER JOIN reg_periksa ON laporan_operasi_casemix.no_rawat = reg_periksa.no_rawat " +
            "INNER JOIN pasien ON reg_periksa.no_rkm_medis = pasien.no_rkm_medis " +
            "LEFT JOIN dokter AS dokter_bedah ON laporan_operasi_casemix.kddokterbedah = dokter_bedah.kd_dokter " +
            "LEFT JOIN dokter AS dokter_anestesi ON laporan_operasi_casemix.kddokteranestesi = dokter_anestesi.kd_dokter " +
            "LEFT JOIN petugas AS petugas_bedah ON laporan_operasi_casemix.asistenbedah = petugas_bedah.nip " +
            "LEFT JOIN petugas AS petugas_anestesi ON laporan_operasi_casemix.asistenanestesi = petugas_anestesi.nip " +
            "WHERE laporan_operasi_casemix.no_rawat = '" + selNoRawat + "' " +
            "AND laporan_operasi_casemix.tanggal = '" + tglOperasi + "'";
    }

    private Map<String, Object> paramOperasiCasemix(String tglOperasi) throws Exception {
        Map<String, Object> p = paramRS();
        p.put("logo2", Sequel.cariGambar("select setting.logo2 from setting"));
        p.put("norawat", selNoRawat);
        p.put("tanggaloperasi", tglOperasi);
        return p;
    }

    private int exportOperasi(int urutan) throws Exception {
        String tglOperasi = cari("select tanggal from laporan_operasi_casemix where no_rawat=? order by tanggal desc limit 1", selNoRawat);
        if (tglOperasi.isEmpty()) throw new Exception("Tidak ada data laporan_operasi_casemix untuk kunjungan ini.");
        String kdDokter = cari("select kddokterbedah from laporan_operasi_casemix where no_rawat=? and tanggal=?", selNoRawat, tglOperasi);
        renderJasperQry(urutan, "OPERASI", "rptLaporanOperasiCasemix.jasper", "::[ Laporan Operasi ]::", qryOperasiCasemix(tglOperasi), paramOperasiCasemix(tglOperasi), kdDokter);
        return 1;
    }

    // ---------------------------------- Hasil Radiologi (lihat DlgCariPeriksaRadiologi.java) ----------------------------------

    private Map<String, Object> paramRadiologi(String tglPeriksa, String jam) throws Exception {
        List<String> pemeriksaan = new ArrayList<>();
        String kdpenjab = "", kdpetugas = "", dokterPerujuk = "";
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select jns_perawatan_radiologi.nm_perawatan, periksa_radiologi.kd_dokter, periksa_radiologi.nip, periksa_radiologi.dokter_perujuk " +
                "from periksa_radiologi inner join jns_perawatan_radiologi on periksa_radiologi.kd_jenis_prw=jns_perawatan_radiologi.kd_jenis_prw " +
                "where periksa_radiologi.no_rawat=? and periksa_radiologi.tgl_periksa=? and periksa_radiologi.jam=?")) {
            ps.setString(1, selNoRawat); ps.setString(2, tglPeriksa); ps.setString(3, jam);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pemeriksaan.add(rs.getString(1));
                    kdpenjab = rs.getString(2); kdpetugas = rs.getString(3); dokterPerujuk = rs.getString(4);
                }
            }
        }
        boolean ranap = "Ranap".equals(selStatusLanjut);
        Map<String, Object> p = new HashMap<>();
        p.put("noperiksa", selNoRawat);
        p.put("norm", selNoRM);
        p.put("namapasien", selNamaPasien);
        p.put("jkel", cari("select jk from pasien where no_rkm_medis=?", selNoRM));
        p.put("umur", cari("select umur from pasien where no_rkm_medis=?", selNoRM));
        p.put("lahir", cari("select DATE_FORMAT(tgl_lahir,'%d-%m-%Y') from pasien where no_rkm_medis=?", selNoRM));
        p.put("pengirim", cari("select nm_dokter from dokter where kd_dokter=?", dokterPerujuk));
        p.put("tanggal", tglPeriksa);
        p.put("penjab", cari("select nm_dokter from dokter where kd_dokter=?", kdpenjab));
        p.put("petugas", cari("select nama from petugas where nip=?", kdpetugas));
        p.put("alamat", cari("select concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) from pasien " +
                "left join kelurahan on pasien.kd_kel=kelurahan.kd_kel left join kecamatan on pasien.kd_kec=kecamatan.kd_kec " +
                "left join kabupaten on pasien.kd_kab=kabupaten.kd_kab where pasien.no_rkm_medis=?", selNoRM));
        p.put("kamar", ranap ? "Kamar" : "Poli");
        p.put("namakamar", ranap
                ? cari("select nm_bangsal from bangsal inner join kamar inner join kamar_inap on bangsal.kd_bangsal=kamar.kd_bangsal and kamar_inap.kd_kamar=kamar.kd_kamar where no_rawat=? order by tgl_masuk desc limit 1", selNoRawat)
                : cari("select poliklinik.nm_poli from poliklinik inner join reg_periksa on reg_periksa.kd_poli=poliklinik.kd_poli where reg_periksa.no_rawat=?", selNoRawat));
        p.put("pemeriksaan", String.join(", ", pemeriksaan));
        p.put("jam", jam);
        p.putAll(paramRS());
        p.put("hasil", cari("select hasil from hasil_radiologi where no_rawat=? and tgl_periksa=? and jam=?", selNoRawat, tglPeriksa, jam));
        p.put("logobsre", Sequel.cariGambar("select gambar.bsre from gambar"));
        String tag = resolveTagSigner(kdpenjab);
        p.put("finger", tag != null ? tag : "#");
        p.put("finger2", "");
        return p;
    }

    private int exportRadiologi(int urutan) throws Exception {
        String tglPeriksa = cari("select tgl_periksa from periksa_radiologi where no_rawat=? order by tgl_periksa desc, jam desc limit 1", selNoRawat);
        String jam = cari("select jam from periksa_radiologi where no_rawat=? order by tgl_periksa desc, jam desc limit 1", selNoRawat);
        if (tglPeriksa.isEmpty()) throw new Exception("Tidak ada data radiologi untuk kunjungan ini.");
        String kdDokter = cari("select kd_dokter from periksa_radiologi where no_rawat=? and tgl_periksa=? and jam=?", selNoRawat, tglPeriksa, jam);
        renderJasperConn(urutan, "RADIOLOGI", "rptPeriksaRadiologiTte.jasper", "::[ Pemeriksaan Radiologi ]::", paramRadiologi(tglPeriksa, jam), kdDokter);
        return 1;
    }

    // ---------------------------------- Hasil Lab, varian "9" (lihat DlgCariPeriksaLab.java) ----------------------------------

    private Map<String, Object> paramLab(String tglPeriksa, String jam) throws Exception {
        String namakamar;
        String kamarId = cari("select ifnull(kamar_inap.kd_kamar,'') from kamar_inap where kamar_inap.no_rawat=? order by kamar_inap.tgl_masuk desc,kamar_inap.jam_masuk desc limit 1", selNoRawat);
        String kamar;
        if (!kamarId.isEmpty()) {
            kamar = kamarId;
            namakamar = kamarId + ", " + cari("select bangsal.nm_bangsal from bangsal inner join kamar on bangsal.kd_bangsal=kamar.kd_bangsal where kamar.kd_kamar=?", kamarId);
        } else {
            kamar = "Poli";
            namakamar = cari("select poliklinik.nm_poli from poliklinik inner join reg_periksa on poliklinik.kd_poli=reg_periksa.kd_poli where reg_periksa.no_rawat=?", selNoRawat);
        }
        String nip = cari("select nip from periksa_lab where no_rawat=? and tgl_periksa=? and jam=?", selNoRawat, tglPeriksa, jam);
        String dokterPerujuk = cari("select dokter_perujuk from periksa_lab where no_rawat=? and tgl_periksa=? and jam=?", selNoRawat, tglPeriksa, jam);
        String kdDokter = cari("select kd_dokter from periksa_lab where no_rawat=? and tgl_periksa=? and jam=?", selNoRawat, tglPeriksa, jam);

        Map<String, Object> p = new HashMap<>();
        p.put("noperiksa", selNoRawat);
        p.put("norm", selNoRM);
        p.put("namapasien", selNamaPasien);
        p.put("jkel", cari("select jk from pasien where no_rkm_medis=?", selNoRM));
        p.put("umur", cari("select umur from pasien where no_rkm_medis=?", selNoRM));
        p.put("pengirim", cari("select nm_dokter from dokter where kd_dokter=?", dokterPerujuk));
        p.put("tanggal", cari("select DATE_FORMAT(tgl_periksa,'%d-%m-%Y') from periksa_lab where no_rawat=? and tgl_periksa=? and jam=?", selNoRawat, tglPeriksa, jam));
        p.put("penjab", cari("select nm_dokter from dokter where kd_dokter=?", kdDokter));
        p.put("petugas", cari("select nama from petugas where nip=?", nip));
        p.put("jam", jam);
        p.put("alamat", cari("select concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) from pasien " +
                "left join kelurahan on pasien.kd_kel=kelurahan.kd_kel left join kecamatan on pasien.kd_kec=kecamatan.kd_kec " +
                "left join kabupaten on pasien.kd_kab=kabupaten.kd_kab where pasien.no_rkm_medis=?", selNoRM));
        p.put("kamar", kamar);
        p.put("namakamar", namakamar);
        p.putAll(paramRS());
        p.put("logobsre", Sequel.cariGambar("select gambar.bsre from gambar"));
        p.put("logo2", Sequel.cariGambar("select setting.logo2 from setting"));
        try (PreparedStatement ps = koneksi.prepareStatement("select saran, kesan from saran_kesan_lab where no_rawat=? and tgl_periksa=? and jam=?")) {
            ps.setString(1, selNoRawat); ps.setString(2, tglPeriksa); ps.setString(3, jam);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { p.put("saran", rs.getString(1)); p.put("kesan", rs.getString(2)); }
                else { p.put("saran", ""); p.put("kesan", ""); }
            }
        }
        String tag = resolveTagSigner(kdDokter);
        p.put("finger", tag != null ? tag : "#");
        return p;
    }

    private String reportLab(String tglPeriksa, String jam) throws Exception {
        boolean adaPermintaan = !cari("select noorder from permintaan_lab where no_rawat=? and tgl_hasil=? and jam_hasil=?", selNoRawat, tglPeriksa, jam).isEmpty();
        return adaPermintaan ? "rptPeriksaLab9PermintaanTte.jasper" : "rptPeriksaLab9Tte.jasper";
    }

    private String sqlEsc(String s) {
        return s == null ? "" : s.replace("'", "`");
    }

    /** temporary_lab punya 38 kolom (no auto_increment + temp1..temp37). INSERT ... VALUES harus
     *  pas 38 nilai atau MySQL tolak ("Column count doesn't match value count") dan baris tidak
     *  masuk — bikin kolom Hasil kosong walau datanya ada. Pad otomatis ke 38 supaya tak perlu
     *  hitung manual '' yang rawan salah. Kolom pertama (no) selalu '0' (auto_increment). */
    private static final int TEMPORARY_LAB_COLS = 38;

    /** vals = nilai untuk temp1..tempN (mentah, belum di-escape/-quote); sisanya dikosongkan. */
    private void insertTemporaryLab(String... vals) throws Exception {
        StringBuilder sb = new StringBuilder("'0'"); // kolom `no`
        for (int i = 0; i < TEMPORARY_LAB_COLS - 1; i++) {
            String v = i < vals.length && vals[i] != null ? sqlEsc(vals[i]) : "";
            sb.append(",'").append(v).append("'");
        }
        try (java.sql.Statement st = koneksi.createStatement()) {
            st.executeUpdate("insert into temporary_lab values(" + sb + ")");
        }
    }

    /** rptPeriksaLab9[Permintaan]Tte.jasper TIDAK membaca hasil pemeriksaan dari no_rawat sama
     *  sekali — grid hasilnya diambil .jrxml dari tabel scratch "temporary_lab" yang harus
     *  diisi lebih dulu (persis logic CreatePDF() di DlgCariPeriksaLab.java). Tanpa langkah ini
     *  isi dokumen akan kosong/salah walau param lain sudah benar. */
    private void stageTemporaryLab(String tglPeriksa, String jam) throws Exception {
        try (java.sql.Statement st = koneksi.createStatement()) {
            st.executeUpdate("delete from temporary_lab");
        }
        try (PreparedStatement ps2 = koneksi.prepareStatement(
                "select jns_perawatan_lab.kd_jenis_prw, jns_perawatan_lab.nm_perawatan from periksa_lab inner join jns_perawatan_lab " +
                "on periksa_lab.kd_jenis_prw=jns_perawatan_lab.kd_jenis_prw " +
                "where periksa_lab.kategori='PK' and periksa_lab.no_rawat=? and periksa_lab.tgl_periksa=? and periksa_lab.jam=?")) {
            ps2.setString(1, selNoRawat); ps2.setString(2, tglPeriksa); ps2.setString(3, jam);
            try (ResultSet rs2 = ps2.executeQuery()) {
                while (rs2.next()) {
                    // Baris header jenis pemeriksaan: hanya temp1 (nm_perawatan) terisi.
                    insertTemporaryLab(rs2.getString("nm_perawatan"));
                    try (PreparedStatement ps3 = koneksi.prepareStatement(
                            "select template_laboratorium.Pemeriksaan, detail_periksa_lab.nilai, template_laboratorium.satuan, " +
                            "detail_periksa_lab.nilai_rujukan, detail_periksa_lab.keterangan from detail_periksa_lab " +
                            "inner join template_laboratorium on detail_periksa_lab.id_template=template_laboratorium.id_template " +
                            "where detail_periksa_lab.no_rawat=? and detail_periksa_lab.kd_jenis_prw=? and detail_periksa_lab.tgl_periksa=? and detail_periksa_lab.jam=? " +
                            "order by template_laboratorium.urut")) {
                        ps3.setString(1, selNoRawat); ps3.setString(2, rs2.getString("kd_jenis_prw")); ps3.setString(3, tglPeriksa); ps3.setString(4, jam);
                        try (ResultSet rs3 = ps3.executeQuery()) {
                            while (rs3.next()) {
                                // Sebagian data lama menyimpan hasil+flag abnormal digabung di kolom
                                // keterangan (mis. "H 12,100") sementara nilai dikosongkan, bukan
                                // disimpan terpisah seperti seharusnya. Kalau nilai kosong tapi
                                // keterangan berpola "<HURUF> <angka>", pisahkan: angkanya pindah ke
                                // Hasil, hurufnya tetap di Keterangan — supaya tidak ada huruf nempel
                                // di depan angka hasil pada kolom Hasil.
                                String nilai = rs3.getString("nilai");
                                String keterangan = rs3.getString("keterangan");
                                if ((nilai == null || nilai.trim().isEmpty()) && keterangan != null) {
                                    java.util.regex.Matcher m = java.util.regex.Pattern.compile("^([A-Za-z])\\s+(.+)$").matcher(keterangan.trim());
                                    if (m.matches()) {
                                        nilai = m.group(2);
                                        keterangan = m.group(1);
                                    }
                                }
                                // temp1=Pemeriksaan(indent), temp2=Hasil(nilai), temp3=satuan,
                                // temp4=nilai_rujukan, temp5=keterangan/flag. Urutan persis CreatePDF.
                                insertTemporaryLab("  " + rs3.getString("Pemeriksaan"), nilai,
                                        rs3.getString("satuan"), rs3.getString("nilai_rujukan"), keterangan);
                            }
                        }
                    }
                }
            }
        }
    }

    private static final class EksamLab {
        final String tgl, jam;
        EksamLab(String tgl, String jam) { this.tgl = tgl; this.jam = jam; }
    }

    /** Semua sesi pemeriksaan lab (tgl_periksa+jam berbeda = sesi berbeda) untuk kunjungan ini.
     *  Ranap bisa punya banyak sesi selama masa rawat — ambil SEMUANYA, bukan cuma yang terbaru. */
    private List<EksamLab> semuaEksamLab() throws Exception {
        List<EksamLab> out = new ArrayList<>();
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select distinct tgl_periksa, jam from periksa_lab where no_rawat=? and kategori='PK' order by tgl_periksa, jam")) {
            ps.setString(1, selNoRawat);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(new EksamLab(rs.getString(1), rs.getString(2)));
            }
        }
        return out;
    }

    private int exportLab(int urutan) throws Exception {
        List<EksamLab> semua = semuaEksamLab();
        if (semua.isEmpty()) throw new Exception("Tidak ada data lab (kategori PK) untuk kunjungan ini.");
        int jumlah = 0;
        List<String> gagalSatu = new ArrayList<>();
        for (EksamLab e : semua) {
            try {
                String kdDokter = cari("select kd_dokter from periksa_lab where no_rawat=? and tgl_periksa=? and jam=?", selNoRawat, e.tgl, e.jam);
                stageTemporaryLab(e.tgl, e.jam);
                // Tiap sesi butuh kode master_berkas_digital SENDIRI (lihat catatan di
                // finalisasiJasper) supaya dokter tiap sesi tetap tercatat sbg signer, tidak
                // saling timpa kalau beda dokter per sesi.
                String dbKode = jumlah == 0 ? DB_KODE_TTE.get("LAB") : potong("CMX_LB" + (jumlah + 1), 10);
                renderJasperConn(urutan + jumlah, "LAB" + (jumlah + 1), reportLab(e.tgl, e.jam),
                        "::[ Pemeriksaan Laboratorium ]::", paramLab(e.tgl, e.jam), kdDokter, dbKode);
                jumlah++;
            } catch (Exception ex) {
                gagalSatu.add(e.tgl + " " + e.jam + ": " + ex.getMessage());
            }
        }
        if (jumlah == 0) throw new Exception("Semua sesi lab gagal dirender: " + String.join("; ", gagalSatu));
        if (!gagalSatu.isEmpty()) areaLog.tulis("  sebagian sesi lab gagal: " + String.join("; ", gagalSatu));
        return jumlah;
    }

    // ---------------------------------- Asesmen Awal Medis IGD (lihat RMPenilaianAwalMedisIGD.java) ----------------------------------

    /** Replikasi RMPenilaianAwalMedisIGD.lokalisStream(): gambar anotasi lokasi luka dari kolom
     *  status_lokalis (format "x,y;x,y;..."), fallback ke gambar polos kalau kosong/gagal. */
    private java.io.InputStream lokalisStream(String statusLokalis) {
        try {
            if (statusLokalis != null && !statusLokalis.trim().isEmpty()) {
                String path = widget.StatusLokalis.renderPng("/picture/semua.png", statusLokalis, 809, 400);
                if (path != null && !path.isEmpty()) return new java.io.FileInputStream(path);
            }
        } catch (Exception ignore) { }
        try {
            return getClass().getResource("/picture/semua.png").openStream();
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, Object> paramAsesmenIGD(String kdDokter) throws Exception {
        Map<String, Object> p = paramRS();
        p.put("logo2", Sequel.cariGambar("select setting.logo2 from setting"));
        p.put("logobsre", Sequel.cariGambar("select gambar.bsre from gambar"));
        String statusLokalis = cari("select status_lokalis from penilaian_medis_igd where no_rawat=?", selNoRawat);
        p.put("lokalis", lokalisStream(statusLokalis));
        String tag = resolveTagSigner(kdDokter);
        p.put("finger", tag != null ? tag : "#");
        return p;
    }

    private int exportAsesmenIGD(int urutan) throws Exception {
        String kdDokter = cari("select kd_dokter from penilaian_medis_igd where no_rawat=?", selNoRawat);
        if (kdDokter.isEmpty()) throw new Exception("Tidak ada data Asesmen Awal Medis IGD untuk kunjungan ini.");
        renderJasperConn(urutan, "ASESMENIGD", "rptCetakPenilaianAwalMedisIGDTte.jasper", "::[ Laporan Penilaian Awal Medis IGD ]::", paramAsesmenIGD(kdDokter), kdDokter);
        return 1;
    }

    /** Tombol "Lihat" per baris checklist — render ulang berkas yang sama (data+tag identik
     *  dengan yang akan digabung) ke file sementara lalu buka dengan viewer PDF default OS.
     *  Murni baca: TIDAK menyentuh berkas_digital_perawatan (beda dengan proses Gabung PDF). */
    private void previewBerkas(String key) {
        if (selNoRawat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih pasien dulu.");
            return;
        }
        try {
            switch (key) {
                case "sep":
                    previewJasperConn(reportSEP(), "::[ Cetak SEP ]::", paramSEP());
                    return;
                case "resume": {
                    String[] t = cariTabelResume();
                    if (t == null) { JOptionPane.showMessageDialog(this, "Tidak ada data resume_pasien/resume_pasien_ranap untuk kunjungan ini."); return; }
                    boolean ranap = "1".equals(t[1]);
                    String tabel = t[0];
                    String kdDokter = cari("select kd_dokter from " + tabel + " where no_rawat=?", selNoRawat);
                    String reportName = ranap ? "rptLaporanResumeRanapTTE.jasper" : "rptLaporanResumeTte.jasper";
                    String qry = ranap ? qryResumeRanap() : qryResumeRalan();
                    previewJasperQry(reportName, "::[ Laporan Resume ]::", qry, paramResume(ranap, kdDokter));
                    return;
                }
                case "operasi": {
                    String tglOperasi = cari("select tanggal from laporan_operasi_casemix where no_rawat=? order by tanggal desc limit 1", selNoRawat);
                    if (tglOperasi.isEmpty()) { JOptionPane.showMessageDialog(this, "Tidak ada data laporan_operasi_casemix untuk kunjungan ini."); return; }
                    previewJasperQry("rptLaporanOperasiCasemix.jasper", "::[ Laporan Operasi ]::", qryOperasiCasemix(tglOperasi), paramOperasiCasemix(tglOperasi));
                    return;
                }
                case "radiologi": {
                    String tglPeriksa = cari("select tgl_periksa from periksa_radiologi where no_rawat=? order by tgl_periksa desc, jam desc limit 1", selNoRawat);
                    String jam = cari("select jam from periksa_radiologi where no_rawat=? order by tgl_periksa desc, jam desc limit 1", selNoRawat);
                    if (tglPeriksa.isEmpty()) { JOptionPane.showMessageDialog(this, "Tidak ada data radiologi untuk kunjungan ini."); return; }
                    previewJasperConn("rptPeriksaRadiologiTte.jasper", "::[ Pemeriksaan Radiologi ]::", paramRadiologi(tglPeriksa, jam));
                    return;
                }
                case "lab": {
                    String tglPeriksa = cari("select tgl_periksa from periksa_lab where no_rawat=? and kategori='PK' order by tgl_periksa desc, jam desc limit 1", selNoRawat);
                    String jam = cari("select jam from periksa_lab where no_rawat=? and kategori='PK' order by tgl_periksa desc, jam desc limit 1", selNoRawat);
                    if (tglPeriksa.isEmpty()) { JOptionPane.showMessageDialog(this, "Tidak ada data lab (kategori PK) untuk kunjungan ini."); return; }
                    stageTemporaryLab(tglPeriksa, jam);
                    previewJasperConn(reportLab(tglPeriksa, jam), "::[ Pemeriksaan Laboratorium ]::", paramLab(tglPeriksa, jam));
                    return;
                }
                case "asesmen_igd": {
                    String kdDokter = cari("select kd_dokter from penilaian_medis_igd where no_rawat=?", selNoRawat);
                    if (kdDokter.isEmpty()) { JOptionPane.showMessageDialog(this, "Tidak ada data Asesmen Awal Medis IGD untuk kunjungan ini."); return; }
                    previewJasperConn("rptCetakPenilaianAwalMedisIGDTte.jasper", "::[ Laporan Penilaian Awal Medis IGD ]::", paramAsesmenIGD(kdDokter));
                    return;
                }
                case "billing": {
                    File tmp = File.createTempFile("preview_casemix_billing_", ".pdf");
                    tmp.deleteOnExit();
                    try {
                        String html = fetchHtml(urlBillingPHP());
                        try (java.io.OutputStream os = new java.io.FileOutputStream(tmp)) {
                            com.itextpdf.html2pdf.HtmlConverter.convertToPdf(html, os);
                        }
                    } catch (Exception e) {
                        areaLog.tulis("Gagal ambil Nota PHP (" + e + "), pakai tabel ringkasan sendiri sebagai fallback");
                        renderTabelBilling(tmp, "Billing — " + selNamaPasien);
                    }
                    bukaFile(tmp);
                    return;
                }
                case "triase": {
                    try {
                        SkalaInfo s = cariSkalaTriase();
                        if (s == null) { JOptionPane.showMessageDialog(this, "Tidak ada data detail skala triase untuk kunjungan ini."); return; }
                        stageTemporaryTriase(s);
                        String qry = "select * from temporary where temp37='" + sqlEsc(akses.getalamatip()) + "' order by no";
                        previewJasperQry(s.jasper, "::[ Triase IGD ]::", qry, paramTriase(s));
                    } catch (Exception e) {
                        areaLog.tulis("Gagal render Jasper Triase (" + e + "), pakai ringkasan sendiri sebagai fallback");
                        DokData d = dataTriase();
                        File tmp = File.createTempFile("preview_casemix_triase_", ".pdf");
                        tmp.deleteOnExit();
                        renderKe(tmp, d.judul, d.baris, null);
                        bukaFile(tmp);
                    }
                    return;
                }
                default:
                    return;
            }
        } catch (Exception e) {
            areaLog.tulis("Gagal membuat pratinjau " + key + ": " + e);
            JOptionPane.showMessageDialog(this, "Gagal membuat pratinjau: " + e.getMessage());
        }
    }

    private void bukaFile(File f) {
        try {
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(f);
            } else {
                JOptionPane.showMessageDialog(this, "File dibuat di: " + f.getAbsolutePath());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal membuka file:\n" + f.getAbsolutePath() + "\n" + e.getMessage());
        }
    }

    // ============================================= HELPER SQL/PDF =============================================
    private List<String[]> ambilBaris(String sql, String noRawat) throws Exception {
        List<String[]> out = new ArrayList<>();
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                int cols = md.getColumnCount();
                int baris = 0;
                while (rs.next() && baris < 20) {
                    if (baris > 0) out.add(new String[]{"", ""});
                    for (int c = 1; c <= cols; c++) {
                        String val = rs.getString(c);
                        if (val != null && !val.trim().isEmpty()) {
                            out.add(new String[]{md.getColumnLabel(c), val});
                        }
                    }
                    baris++;
                }
            }
        }
        return out;
    }

    private String nilaiKolom(String sql, String noRawat) throws Exception {
        try (PreparedStatement ps = koneksi.prepareStatement(sql + " limit 1")) {
            ps.setString(1, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        }
        return "";
    }

    /** Kode di master_berkas_digital untuk jenis berkas tsb — dibuat otomatis kalau belum ada.
     *  Pakai INSERT kolom bernama (bukan positional) karena tabel ini tidak punya pola INSERT
     *  lain di kode manapun untuk dijadikan acuan urutan kolom yang pasti. */
    private String kodeBerkas(String kodeDefault, String nama) throws Exception {
        try (PreparedStatement ps = koneksi.prepareStatement("select kode from master_berkas_digital where kode=?")) {
            ps.setString(1, kodeDefault);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        }
        try (PreparedStatement ps = koneksi.prepareStatement("insert into master_berkas_digital (kode, nama) values (?, ?)")) {
            ps.setString(1, kodeDefault);
            ps.setString(2, nama);
            ps.executeUpdate();
        }
        return kodeDefault;
    }

    private void tulisHalaman(int urutan, String kodeFile, String judul, List<String[]> baris, String tagUntukWatermark) throws Exception {
        File f = renderHalaman(urutan, kodeFile, judul, baris, tagUntukWatermark);
        areaLog.tulis("  file: " + f.getName());
    }

    /** Sama seperti tulisHalaman, tapi tambahan: sisipkan tag TTE dokter + catat ke berkas_digital_perawatan. */
    // Format tag resmi TagSignerDetector — nilai tag_signer yang tidak cocok pola ini (mis.
    // kolomnya kosong/berisi karakter sampah seperti "&") TIDAK berguna buat deteksi TTE dan
    // hanya akan tampil sebagai watermark aneh di PDF, jadi diperlakukan sebagai "tanpa tag".
    private static final java.util.regex.Pattern TAG_VALID = java.util.regex.Pattern.compile("^#[A-Za-z0-9_]+#$");

    /** Cari tag_signer dokter dan validasi formatnya "#TAG#" (TagSignerDetector). Nilai yang
     *  kosong/tidak berformat benar dianggap "tidak ada tag" (dilaporkan ke log oleh pemanggil
     *  yang membutuhkan konteks lebih, bukan di sini, supaya helper ini tetap bisa dipakai diam2
     *  oleh pratinjau tanpa membanjiri log). */
    private String resolveTagSigner(String kdDokter) {
        if (kdDokter == null || kdDokter.trim().isEmpty()) return null;
        try {
            String t = Sequel.cariIsi("select tag_signer from dokter where kd_dokter=?", kdDokter);
            if (t == null || t.trim().isEmpty()) t = Sequel.cariIsi("select tag_signer from petugas where nip=?", kdDokter);
            if (t != null && TAG_VALID.matcher(t.trim()).matches()) return t.trim();
        } catch (Exception e) {
            areaLog.tulis("Gagal mengambil tag_signer dokter " + kdDokter + ": " + e);
        }
        return null;
    }

    private void tulisHalamanTte(int urutan, String kodeFile, String judul, List<String[]> baris, String kdDokter) throws Exception {
        String tagSigner = resolveTagSigner(kdDokter);
        if (tagSigner == null && kdDokter != null && !kdDokter.trim().isEmpty()) {
            String t = Sequel.cariIsi("select tag_signer from dokter where kd_dokter=?", kdDokter);
            if (t == null || t.trim().isEmpty()) t = Sequel.cariIsi("select tag_signer from petugas where nip=?", kdDokter);
            if (t != null && !t.trim().isEmpty()) {
                areaLog.tulis("  peringatan: tag_signer dokter " + kdDokter + " ('" + t.trim()
                        + "') tidak berformat #TAG# yang benar — watermark dilewati, tapi dokter tetap dicatat sebagai signer.");
            }
        }
        File f = renderHalaman(urutan, kodeFile, judul, baris, tagSigner);
        areaLog.tulis("  file: " + f.getName() + (kdDokter == null || kdDokter.isEmpty() ? " (dokter tidak ditemukan, tanpa tag)" : " (tag dokter " + kdDokter + ")"));

        if (kdDokter != null && !kdDokter.trim().isEmpty()) {
            String kode = kodeBerkas(DB_KODE_TTE.get(kodeFile), "Casemix - " + judul.split(" — ")[0]);
            String lokasiFile = "casemix/" + f.getName();
            try (PreparedStatement ps = koneksi.prepareStatement(
                    "delete from berkas_digital_perawatan where no_rawat=? and kode=? and nip=?")) {
                ps.setString(1, selNoRawat); ps.setString(2, kode); ps.setString(3, kdDokter);
                ps.executeUpdate();
            }
            Sequel.menyimpantf("berkas_digital_perawatan", "?,?,?,?", "No.Rawat", 4,
                    new String[]{selNoRawat, kode, lokasiFile, kdDokter});
        }
    }

    private File renderHalaman(int urutan, String kodeFile, String judul, List<String[]> baris, String tagSigner) throws Exception {
        File out = new File(scratchDir, String.format("%03d_%s.pdf", urutan, kodeFile));
        renderKe(out, judul, baris, tagSigner);
        return out;
    }

    /** Logic PDFBox murni — tulis 1 halaman ringkasan ke file tujuan mana pun. Dipakai baik oleh
     *  renderHalaman() (bagian dari gabung PDF, ke scratchDir) maupun previewBerkas() (ke file
     *  sementara, murni untuk dilihat). */
    private void renderKe(File out, String judul, List<String[]> baris, String tagSigner) throws Exception {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            float y = 800, left = 50, right = 545;
            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.setFont(PDType1Font.HELVETICA_BOLD, 13);
                cs.beginText(); cs.newLineAtOffset(left, y); cs.showText(potong(judul.split("\n")[0], 90)); cs.endText();
                y -= 16;
                for (String sub : judul.split("\n")) {
                    if (sub.equals(judul.split("\n")[0])) continue;
                    cs.setFont(PDType1Font.HELVETICA_OBLIQUE, 9);
                    cs.beginText(); cs.newLineAtOffset(left, y); cs.showText(potong(sub, 100)); cs.endText();
                    y -= 14;
                }
                y -= 8;
                cs.setLineWidth(0.7f);
                cs.moveTo(left, y); cs.lineTo(right, y); cs.stroke();
                y -= 16;

                if (baris.isEmpty()) {
                    cs.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
                    cs.beginText(); cs.newLineAtOffset(left, y); cs.showText("(tidak ada data)"); cs.endText();
                    y -= 16;
                }
                for (String[] f : baris) {
                    if (y < 60) break; // 1 halaman cukup untuk ringkasan casemix
                    if (f[0].isEmpty() && f[1].isEmpty()) { // separator antar baris data
                        y -= 6;
                        cs.setLineWidth(0.3f);
                        cs.moveTo(left, y); cs.lineTo(right, y); cs.stroke();
                        y -= 10;
                        continue;
                    }
                    cs.setFont(PDType1Font.HELVETICA_BOLD, 9);
                    cs.beginText(); cs.newLineAtOffset(left, y); cs.showText(potong(f[0], 28) + " :"); cs.endText();
                    cs.setFont(PDType1Font.HELVETICA, 9);
                    cs.beginText(); cs.newLineAtOffset(left + 165, y); cs.showText(potong(f[1].replaceAll("[\\r\\n]+", " / "), 85)); cs.endText();
                    y -= 14;
                }

                if (tagSigner != null && !tagSigner.trim().isEmpty() && !tagSigner.equals("#")) {
                    cs.setFont(PDType1Font.HELVETICA, 6);
                    cs.beginText(); cs.newLineAtOffset(left, 18); cs.showText(tagSigner); cs.endText();
                }
            }
            doc.save(out);
        }
    }

    private String potong(String s, int max) {
        if (s == null) return "";
        s = s.trim();
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }

    /** Unduh berkas manual (sudah di-upload lewat alur lama) dari hybrid web ke scratch, ikut antre gabung. */
    private void unduhBerkasManual(String lokasiFile, int urutan) throws Exception {
        String clean = lokasiFile.startsWith("berkasrawat/") ? lokasiFile.substring("berkasrawat/".length()) : lokasiFile;
        if (clean.startsWith("/")) clean = clean.substring(1);
        String url = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/berkasrawat/" + clean;
        File out = new File(scratchDir, String.format("%03d_MANUAL.pdf", urutan));
        try (java.io.InputStream is = new java.net.URL(url).openStream();
             java.io.FileOutputStream fos = new java.io.FileOutputStream(out)) {
            is.transferTo(fos);
        }
        areaLog.tulis("  berkas manual diunduh: " + out.getName());
    }

    private File mergePDF() {
        File[] files = scratchDir.listFiles((d, n) -> n.endsWith(".pdf"));
        if (files == null || files.length == 0) return null;
        Arrays.sort(files, (a, b) -> a.getName().compareTo(b.getName()));
        PDFMergerUtility merger = new PDFMergerUtility();
        File dest = new File(scratchDir, "CASEMIX_" + selNoRawat.replaceAll("/", "") + ".pdf");
        merger.setDestinationFileName(dest.getAbsolutePath());
        for (File f : files) {
            if (f.getName().startsWith("CASEMIX_")) continue;
            try { merger.addSource(f); } catch (Exception e) { areaLog.tulis("Lewati (rusak): " + f.getName()); }
        }
        try {
            merger.mergeDocuments(MemoryUsageSetting.setupMixed(200_000_000));
            return dest;
        } catch (Exception e) {
            areaLog.tulis("Gagal merge: " + e);
            return null;
        }
    }

    /** Upload hasil gabungan + catat ke berkas_klaim — persis pola DlgBerkasRawat.uploadDraft(), supaya
     *  DlgBerkasKlaim bisa langsung membacanya tanpa perubahan apa pun di file itu. */
    private boolean kirimKeBerkasKlaim(File merged) {
        try (PreparedStatement ps = koneksi.prepareStatement(
                "create table if not exists berkas_klaim (no_rawat varchar(20), tgl_upload datetime, " +
                "nama_berkas varchar(200), lokasi_file varchar(200), status varchar(20) default 'MENUNGGU_TTD', primary key(no_rawat))")) {
            ps.execute();
        } catch (Exception ignore) { }

        String draftName = "CASEMIX_" + selNoRawat.replaceAll("/", "") + ".pdf";
        boolean uploaded;
        try (CloseableHttpClient http = HttpClients.createDefault()) {
            String url = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/berkasklaim/upload.php";
            HttpPost post = new HttpPost(url);
            MultipartEntityBuilder b = MultipartEntityBuilder.create();
            b.setMode(HttpMultipartMode.LEGACY);
            b.addBinaryBody("file", merged, ContentType.APPLICATION_OCTET_STREAM, draftName);
            post.setEntity(b.build());
            try (CloseableHttpResponse resp = http.execute(post)) {
                int status = resp.getCode();
                String body = "";
                try { body = EntityUtils.toString(resp.getEntity()); } catch (Exception ignore) { }
                uploaded = status == 200;
                areaLog.tulis("Upload berkasklaim -> HTTP " + status + (body.isEmpty() ? "" : " : " + potong(body, 200)));
            }
        } catch (Exception e) {
            areaLog.tulis("Gagal upload ke berkasklaim: " + e);
            uploaded = false;
        }
        if (!uploaded) return false;

        try {
            boolean sudahAda;
            try (PreparedStatement ps = koneksi.prepareStatement("select count(*) from berkas_klaim where no_rawat=?")) {
                ps.setString(1, selNoRawat);
                try (ResultSet rs = ps.executeQuery()) { rs.next(); sudahAda = rs.getInt(1) > 0; }
            }
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            boolean sukses;
            if (sudahAda) {
                sukses = Sequel.mengedittf("berkas_klaim", "no_rawat=?",
                        "tgl_upload=?,nama_berkas=?,lokasi_file=?,status='MENUNGGU_TTD'", 4,
                        new String[]{now, draftName, draftName, selNoRawat});
            } else {
                sukses = Sequel.menyimpantf("berkas_klaim", "?,?,?,?,?", "no_rawat", 5,
                        new String[]{selNoRawat, now, draftName, draftName, "MENUNGGU_TTD"});
            }
            areaLog.tulis((sukses ? "Tercatat" : "GAGAL tercatat") + " di berkas_klaim: no_rawat=" + selNoRawat
                    + ", tgl_upload=" + now + " (cek rentang tanggal & status filter di form Berkas Klaim)");
            return sukses;
        } catch (Exception e) {
            areaLog.tulis("Upload berhasil tapi gagal catat ke berkas_klaim: " + e);
            return false;
        }
    }

    /** Kotak log sederhana (JTextArea + scroll) supaya tidak perlu field terpisah. */
    private static final class JTextAreaLog {
        private final javax.swing.JTextArea area = new javax.swing.JTextArea(8, 40);
        JTextAreaLog() { area.setEditable(false); area.setFont(new Font("Monospaced", Font.PLAIN, 11)); }
        JScrollPane scroll() {
            JScrollPane sp = new JScrollPane(area);
            sp.setPreferredSize(new Dimension(420, 140));
            sp.setBorder(BorderFactory.createTitledBorder("Log proses"));
            return sp;
        }
        void tulis(String s) { area.append(s + "\n"); area.setCaretPosition(area.getDocument().getLength()); }
        void bersih() { area.setText(""); }
    }
}
