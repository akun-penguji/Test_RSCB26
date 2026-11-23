/*
  Kirim Akta Kelahiran ke SatuSehat — tahap 5 & 6 playbook Modul Data Kelahiran.

  Tahap 1-4 (Patient bayi, Encounter, Observation INC/Neonatus) sudah ditangani
  SatuSehatKirimBayiBaruLahir & SatuSehatKirimLaporanPersalinan. Kelas ini
  melanjutkan:
    5. RelatedPerson ibu (NMTH) / ayah (NFTH) / pelapor / saksi
       + Observation tanggal lapor + 3 DocumentReference persyaratan
    6. POST Task akta lahir (SNOMED 444561001) -> nomor transaksi dari SatuSehat

  Catatan lapangan yang sudah diuji ke server staging:
    - Encounter.serviceProvider & EpisodeOfCare.managingOrganization WAJIB
      Organization UTAMA (IDSATUSEHAT), bukan sub-organisasi. Sub-organisasi
      ditolak "wrong organization ID" (RuleNumber 10124).
    - Semua waktu dikirim UTC+00, bukan WIB.
    - Tanggal tidak boleh kurang dari 3 Juni 2014.
  Akta TIDAK diterbitkan SatuSehat: payload diteruskan ke SIAK, lalu Dukcapil
  yang memverifikasi, menandatangani elektronik, dan mengirim akta + KK ke
  email pelapor & faskes. Panel Progres menampilkan 9 tahap itu.
 */
package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import widget.*;

public final class SatuSehatKirimAktaKelahiran extends javax.swing.JDialog {

    private InternalFrame internalFrame1;
    private widget.Table tbData;
    private DefaultTableModel tabMode;
    private TextBox TCari, TNoRawat, TNamaBayi, TIhsBayi;
    private TextBox TNikIbu, TNamaIbu, THpIbu, TEmailIbu, TNoKK;
    private TextBox TAlamatIbu, TKotaIbu, TAlamatPelapor, TKotaPelapor, TAlamatSaksi, TKotaSaksi;
    private TextBox TNikAyah, TNamaAyah, THpAyah;
    private TextBox TNikPelapor, TNamaPelapor, THpPelapor, TEmailPelapor;
    private TextBox TNikSaksi, TNamaSaksi;
    private TextBox TFileNikah, TFileSKL, TFileKuasa;
    private widget.Tanggal TglLahirAyah, TglLahirPelapor, TglLahirSaksi, TglKawin, DTPCari1, DTPCari2;
    private Button BtnCari, BtnAll, BtnSimpan, BtnKirim, BtnKirimKelahiran, BtnProgres, BtnKeluar,
                   BtnPilihNikah, BtnPilihSKL, BtnPilihKuasa, BtnSklJasper, BtnPersetujuan;
    private Label LCount, LStatus;

    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();
    private final ApiSatuSehat api = new ApiSatuSehat();
    private final ObjectMapper mapper = new ObjectMapper();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String link = "", orgId = "";

    /** 9 tahap sesuai alur SATUSEHAT -> SIAK -> Dukcapil. */
    private static final String[][] TAHAP = {
        {"SATUSEHAT","Kirim Pelaporan Ke SatuSehat"},
        {"SATUSEHAT","POST Task Pelaporan SatuSehat"},
        {"SATUSEHAT","SatuSehat Kirim Pelaporan ke SIAK"},
        {"DUKCAPIL","1. Permohonan"},
        {"DUKCAPIL","2. Proses Biodata"},
        {"DUKCAPIL","3. Proses Pembuatan Akta"},
        {"DUKCAPIL","4. Pengajuan Tanda Tangan Elektronik (TTE)"},
        {"DUKCAPIL","5. Verifikasi Tanda Tangan Elektronik (TTE)"},
        {"DUKCAPIL","6. Sertifikasi Akta Kelahiran"}
    };

    public SatuSehatKirimAktaKelahiran(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(1000, 700);
        setLocationRelativeTo(null);
        try {
            link = koneksiDB.URLFHIRSATUSEHAT();
            orgId = koneksiDB.IDSATUSEHAT();
        } catch (Exception e) { System.out.println("Notif konfigurasi SatuSehat : " + e); }
    }

    // ── Tata letak ───────────────────────────────────────────────────────────
    private Label lbl(String t) { Label l = new Label(); l.setText(t); return l; }
    private TextBox tb(int w) { TextBox t = new TextBox(); t.setPreferredSize(new Dimension(w, 23)); return t; }
    private Button btn(String t, String ikon, int w) {
        Button b = new Button();
        try { b.setIcon(new ImageIcon(getClass().getResource(ikon))); } catch (Exception e) { }
        b.setText(t); b.setPreferredSize(new Dimension(w, 28)); return b;
    }
    private void baris(PanelBiasa p, int y, Object... isi) {
        int x = 10;
        for (Object o : isi) {
            if (o instanceof Integer) { x += (Integer) o; continue; }
            java.awt.Component c = (java.awt.Component) o;
            int w = c.getPreferredSize().width; if (w <= 0) w = 120;
            c.setBounds(x, y, w, 23); p.add(c); x += w + 6;
        }
    }

    private void initComponents() {
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Kirim Akta Kelahiran ke SatuSehat ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // Daftar bayi yang sudah punya IHS
        // Kolom klinis (Jam Lahir..Anak Ke) ikut ditampilkan supaya petugas bisa
        // melihat langsung data mana yang belum lengkap SEBELUM menekan Kirim —
        // semuanya ditarik otomatis dari pasien_bayi, tidak diketik ulang.
        // Kolom Encounter sengaja ditampilkan: ADANYA ID = sudah terkirim.
        tabMode = new DefaultTableModel(null, new String[]{
            "No.Rawat", "No.RM", "Nama Bayi", "Tgl Lahir", "IHS Bayi", "NIK Ibu", "Nama Ibu", "Status", "No.Transaksi",
            "Jam Lahir", "B.B.", "P.B.", "Anak Ke", "Penolong", "Encounter"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbData = new widget.Table(); tbData.setModel(tabMode);
        tbData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] w = {110, 70, 170, 80, 130, 130, 160, 80, 140, 70, 55, 55, 60, 140, 130};
        for (int i = 0; i < w.length; i++) tbData.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        tbData.setDefaultRenderer(Object.class, new WarnaTable());
        tbData.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { if (tabMode.getRowCount() > 0) getData(); } });
        ScrollPane scData = new ScrollPane(); scData.setViewportView(tbData);

        // Panel isian
        PanelBiasa p = new PanelBiasa(); p.setLayout(null);
        int y = 10;
        TNoRawat = tb(110); TNoRawat.setEditable(false);
        TNamaBayi = tb(180); TNamaBayi.setEditable(false);
        TIhsBayi = tb(140); TIhsBayi.setEditable(false);
        baris(p, y, lbl("No.Rawat Bayi :"), TNoRawat, lbl("Nama :"), TNamaBayi, lbl("IHS Bayi :"), TIhsBayi);

        y += 30;
        TNikIbu = tb(130); TNamaIbu = tb(170); THpIbu = tb(110); TEmailIbu = tb(150);
        baris(p, y, lbl("IBU — NIK :"), TNikIbu, lbl("Nama :"), TNamaIbu, lbl("HP :"), THpIbu, lbl("Email :"), TEmailIbu);

        y += 28;
        TNoKK = tb(130); TglKawin = new widget.Tanggal(); TglKawin.setDisplayFormat("dd-MM-yyyy");
        TglKawin.setPreferredSize(new Dimension(110, 23));
        baris(p, y, lbl("No.KK :"), TNoKK, lbl("Tgl Perkawinan :"), TglKawin);

        y += 28;
        TNikAyah = tb(130); TNamaAyah = tb(170); TglLahirAyah = new widget.Tanggal();
        TglLahirAyah.setDisplayFormat("dd-MM-yyyy"); TglLahirAyah.setPreferredSize(new Dimension(110, 23));
        THpAyah = tb(110);
        baris(p, y, lbl("AYAH — NIK :"), TNikAyah, lbl("Nama :"), TNamaAyah, lbl("Tgl Lahir :"), TglLahirAyah, lbl("HP :"), THpAyah);

        y += 28;
        TNikPelapor = tb(130); TNamaPelapor = tb(170); TglLahirPelapor = new widget.Tanggal();
        TglLahirPelapor.setDisplayFormat("dd-MM-yyyy"); TglLahirPelapor.setPreferredSize(new Dimension(110, 23));
        THpPelapor = tb(110);
        baris(p, y, lbl("PELAPOR — NIK :"), TNikPelapor, lbl("Nama :"), TNamaPelapor, lbl("Tgl Lahir :"), TglLahirPelapor, lbl("HP :"), THpPelapor);

        y += 28;
        TEmailPelapor = tb(180);
        TNikSaksi = tb(130); TNamaSaksi = tb(170); TglLahirSaksi = new widget.Tanggal();
        TglLahirSaksi.setDisplayFormat("dd-MM-yyyy"); TglLahirSaksi.setPreferredSize(new Dimension(110, 23));
        baris(p, y, lbl("Email Pelapor :"), TEmailPelapor, lbl("SAKSI — NIK :"), TNikSaksi, lbl("Nama :"), TNamaSaksi, lbl("Tgl Lahir :"), TglLahirSaksi);

        // Alamat keluarga — ikut dikirim sebagai RelatedPerson.address (rscb-43).
        // Alamat ibu terisi otomatis dari rekam medis ibu; ayah memakai alamat
        // ibu karena satu Kartu Keluarga. Pelapor & saksi bisa beda rumah, jadi
        // punya isian sendiri yang defaultnya menyalin alamat ibu.
        y += 28;
        TAlamatIbu = tb(330); TKotaIbu = tb(140);
        baris(p, y, lbl("Alamat Ibu/Ayah :"), TAlamatIbu, lbl("Kota/Kab :"), TKotaIbu);

        y += 28;
        TAlamatPelapor = tb(330); TKotaPelapor = tb(140);
        baris(p, y, lbl("Alamat Pelapor :"), TAlamatPelapor, lbl("Kota/Kab :"), TKotaPelapor);

        y += 28;
        TAlamatSaksi = tb(330); TKotaSaksi = tb(140);
        baris(p, y, lbl("Alamat Saksi :"), TAlamatSaksi, lbl("Kota/Kab :"), TKotaSaksi);

        y += 32;
        TFileNikah = tb(300); TFileSKL = tb(300); TFileKuasa = tb(300);
        BtnPilihNikah = btn("...", "/picture/iconfinder_document-open_118911.png", 40);
        BtnPilihSKL = btn("...", "/picture/iconfinder_document-open_118911.png", 40);
        BtnPilihKuasa = btn("...", "/picture/iconfinder_document-open_118911.png", 40);
        // SKL dibuat rumah sakit sendiri dan jasper-nya sudah ada — tidak perlu
        // dicetak lalu dipindai lagi hanya untuk diunggah.
        BtnSklJasper = btn("Ambil dari Jasper", "/picture/iconfinder_printer_43690.png", 140);
        BtnPersetujuan = btn("Cetak Lembar Persetujuan", "/picture/iconfinder_printer_43690.png", 180);
        baris(p, y, lbl("Buku Nikah / SPTJM :"), TFileNikah, BtnPilihNikah);
        y += 28; baris(p, y, lbl("Surat Ket. Lahir :"), TFileSKL, BtnPilihSKL, BtnSklJasper);
        y += 28; baris(p, y, lbl("Formulir Consent :"), TFileKuasa, BtnPilihKuasa, BtnPersetujuan);

        y += 32;
        LStatus = lbl(" "); LStatus.setPreferredSize(new Dimension(700, 20));
        LStatus.setBounds(10, y, 700, 20); p.add(LStatus);
        p.setPreferredSize(new Dimension(960, y + 40));
        ScrollPane scForm = new ScrollPane(); scForm.setViewportView(p);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scForm, scData);
        split.setResizeWeight(0.55); split.setDividerLocation(360);

        // Panel bawah
        panelisi bawah = new panelisi();
        bawah.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 8));
        bawah.setPreferredSize(new Dimension(44, 46));
        BtnSimpan = btn("Simpan", "/picture/save-16x16.png", 95);
        // Tahap 3 & 4 — datanya sudah lengkap dari pasien_bayi, jadi tombolnya
        // berdiri sendiri dan bisa ditekan tanpa mengisi form keluarga dulu.
        BtnKirimKelahiran = btn("Kirim Data Kelahiran", "/picture/if_mail-send-receive_118784.png", 165);
        BtnKirim = btn("Kirim Akta (Task)", "/picture/if_mail-send-receive_118784.png", 150);
        BtnProgres = btn("Progres", "/picture/refresh.png", 100);
        BtnKeluar = btn("Keluar", "/picture/exit.png", 90);
        DTPCari1 = new widget.Tanggal(); DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setPreferredSize(new Dimension(95, 23));
        DTPCari2 = new widget.Tanggal(); DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setPreferredSize(new Dimension(95, 23));
        TCari = tb(150);
        BtnCari = btn("", "/picture/accept.png", 30);
        BtnAll = btn("", "/picture/Search-16x16.png", 30);
        LCount = lbl("Record : 0"); LCount.setPreferredSize(new Dimension(100, 23));
        bawah.add(BtnSimpan); bawah.add(BtnKirimKelahiran); bawah.add(BtnKirim); bawah.add(BtnProgres); bawah.add(BtnKeluar);
        bawah.add(lbl("Tgl :")); bawah.add(DTPCari1); bawah.add(lbl("s.d.")); bawah.add(DTPCari2);
        bawah.add(lbl("Cari :")); bawah.add(TCari); bawah.add(BtnCari); bawah.add(BtnAll); bawah.add(LCount);

        BtnKeluar.addActionListener(e -> dispose());
        BtnCari.addActionListener(e -> tampil());
        BtnAll.addActionListener(e -> { TCari.setText(""); tampil(); });
        BtnSimpan.addActionListener(e -> simpan());
        BtnKirim.addActionListener(e -> kirim());
        BtnKirimKelahiran.addActionListener(e -> kirimDataKelahiran());
        BtnProgres.addActionListener(e -> tampilProgres());
        BtnPilihNikah.addActionListener(e -> pilihBerkas(TFileNikah));
        BtnPilihSKL.addActionListener(e -> pilihBerkas(TFileSKL));
        BtnPilihKuasa.addActionListener(e -> pilihBerkas(TFileKuasa));
        BtnSklJasper.addActionListener(e -> ambilSklDariJasper());
        BtnPersetujuan.addActionListener(e -> cetakLembarPersetujuan());

        internalFrame1.add(split, BorderLayout.CENTER);
        internalFrame1.add(bawah, BorderLayout.PAGE_END);
        setLayout(new BorderLayout());
        add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
    }

    /**
     * Pilih berkas lampiran.
     *
     * Juknis Pengiriman Data Kelahiran: "Pindai atau foto dokumen dalam format
     * JPG, SATU HALAMAN SATU FILE, teks terbaca jelas" dan "Modul upload dokumen
     * wajib JPEG". Jadi PDF dan PNG sengaja tidak ditawarkan.
     *
     * Boleh memilih beberapa berkas sekaligus untuk dokumen berhalaman banyak
     * (buku nikah biasanya 2 halaman: data suami-istri dan tanggal perkawinan).
     * Path-nya disimpan dipisah "|" dan dikirim sebagai beberapa attachment di
     * satu DocumentReference.
     */
    private void pilihBerkas(TextBox target) {
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Hasil pindai (jpg, jpeg, pdf) — boleh pilih beberapa halaman", "jpg", "jpeg", "pdf"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;
        List<File> berkas = new ArrayList<>(java.util.Arrays.asList(fc.getSelectedFiles()));
        if (berkas.isEmpty() && fc.getSelectedFile() != null) berkas.add(fc.getSelectedFile());
        // getSelectedFiles() memberi urutan KLIK, bukan urutan halaman. Kalau
        // petugas menyorot halaman 2 lebih dulu, lampirannya terkirim terbalik
        // dan tidak ada yang menyadarinya sampai Dukcapil mengeluh. Jadi
        // diurutkan sendiri berdasarkan nama berkas.
        berkas.sort((x, y) -> bandingAlami(x.getName(), y.getName()));

        // PDF diterima, lalu diubah sendiri jadi JPEG per halaman.
        //
        // SatuSehat sebenarnya MENERIMA application/pdf (sudah diuji) — yang
        // mewajibkan JPEG adalah juknis, dan yang membaca berkasnya di ujung
        // sana adalah Dukcapil, bukan SatuSehat. Menolak PDF mentah-mentah cuma
        // menyusahkan, karena pemindai rumah sakit lazimnya menghasilkan PDF;
        // mengirimnya apa adanya juga berisiko gagal diam-diam di Dukcapil.
        // Jadi PDF-nya dipecah jadi JPEG di sini.
        List<String> path = new ArrayList<>();
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            for (File f : berkas) {
                if (f.getName().toLowerCase().endsWith(".pdf")) path.addAll(pdfJadiJpeg(f));
                else path.add(f.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Notif ubah PDF : " + e);
            JOptionPane.showMessageDialog(this, "Gagal mengubah PDF jadi JPEG: " + e.getMessage());
            return;
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
        target.setText(String.join("|", path));
        if (path.size() > 1) {
            List<File> tampil = new ArrayList<>();
            for (String x : path) tampil.add(new File(x));
            JOptionPane.showMessageDialog(this,
                path.size() + " halaman dipilih, urutannya:\n\n" + ringkasNama(tampil)
                + "\n\nKalau urutannya salah, ganti nama berkasnya lalu pilih ulang.",
                "Urutan halaman", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Pecah satu PDF jadi beberapa JPEG, satu berkas per halaman.
     *
     * 150 dpi: cukup supaya teks hasil pindai terbaca jelas tanpa membuat
     * base64-nya membengkak. Hasilnya ditaruh di folder berkas_akta milik
     * pasien, jadi berkas asli petugas tidak disentuh.
     */
    private List<String> pdfJadiJpeg(File pdf) throws Exception {
        List<String> hasil = new ArrayList<>();
        File folder = new File("berkas_akta" + File.separator + TNoRawat.getText().replace("/", ""));
        if (!folder.isDirectory() && !folder.mkdirs()) {
            throw new IllegalStateException("tidak bisa membuat folder " + folder.getPath());
        }
        String dasar = pdf.getName().replaceFirst("(?i)\\.pdf$", "");
        try (org.apache.pdfbox.pdmodel.PDDocument dok = org.apache.pdfbox.pdmodel.PDDocument.load(pdf)) {
            org.apache.pdfbox.rendering.PDFRenderer r = new org.apache.pdfbox.rendering.PDFRenderer(dok);
            for (int i = 0; i < dok.getNumberOfPages(); i++) {
                java.awt.image.BufferedImage gbr = r.renderImageWithDPI(i, 150);
                hasil.add(simpanJpeg(gbr, new File(folder, dasar + "-hal" + (i + 1) + ".jpg")));
            }
        }
        return hasil;
    }

    /**
     * Urutkan nama berkas seperti orang membacanya: "hal2" sebelum "hal10",
     * bukan sesudahnya seperti pada urutan abjad biasa.
     */
    private static int bandingAlami(String a, String b) {
        int i = 0, j = 0;
        while (i < a.length() && j < b.length()) {
            char ca = a.charAt(i), cb = b.charAt(j);
            if (Character.isDigit(ca) && Character.isDigit(cb)) {
                int i2 = i, j2 = j;
                while (i2 < a.length() && Character.isDigit(a.charAt(i2))) i2++;
                while (j2 < b.length() && Character.isDigit(b.charAt(j2))) j2++;
                // Bandingkan sebagai bilangan, tanpa Integer.parseInt supaya
                // deretan angka sepanjang apa pun tidak melimpah.
                String na = a.substring(i, i2).replaceFirst("^0+(?=.)", "");
                String nb = b.substring(j, j2).replaceFirst("^0+(?=.)", "");
                if (na.length() != nb.length()) return na.length() - nb.length();
                int c = na.compareTo(nb);
                if (c != 0) return c;
                i = i2; j = j2;
            } else {
                int c = Character.toLowerCase(ca) - Character.toLowerCase(cb);
                if (c != 0) return c;
                i++; j++;
            }
        }
        return (a.length() - i) - (b.length() - j);
    }

    private static String ringkasNama(List<File> berkas) {
        List<String> baris = new ArrayList<>();
        for (int i = 0; i < berkas.size(); i++) baris.add("  " + (i + 1) + ". " + berkas.get(i).getName());
        return String.join("\n", baris);
    }

    /** Pecah isi kotak berkas menjadi daftar path halaman. */
    private static List<String> daftarBerkas(String isi) {
        List<String> hasil = new ArrayList<>();
        if (isi == null) return hasil;
        for (String x : isi.split("\\|")) {
            String t = x.trim();
            if (!t.isEmpty()) hasil.add(t);
        }
        return hasil;
    }

    // ── Daftar & isian ───────────────────────────────────────────────────────
    public void tampil() {
        Valid.tabelKosong(tabMode);
        // Daftar dimulai dari pasien_bayi — yaitu SEMUA bayi yang sudah dicatat di
        // form Kelahiran Bayi, bukan hanya yang sudah terlanjur punya IHS. Dengan
        // begitu petugas tinggal memilih lalu Kirim; kolom IHS Bayi/Encounter yang
        // terisi adalah tandanya sudah pernah terkirim.
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select bm.no_rawat_bayi no_rawat, pb.no_rkm_medis, p.nm_pasien, p.tgl_lahir, " +
            "ifnull(bl.ihs_bayi,'') ihs_bayi, " +
            "ifnull(ak.nik_ibu, ifnull(bm.nik_ibu,'')) nik_ibu, " +
            "ifnull(ak.nama_ibu, ifnull(bm.nm_ibu_teks, ifnull(p.nm_ibu,''))) nama_ibu, " +
            "ifnull(ak.status,'Draft') status, ifnull(ak.no_transaksi,'') no_transaksi, " +
            "pb.jam_lahir, pb.berat_badan, pb.panjang_badan, pb.anakke, " +
            "ifnull(pg.nama,'') nama_penolong, ifnull(ak.encounter_bayi,'') encounter_bayi " +
            "from pasien_bayi pb " +
            "inner join pasien p on p.no_rkm_medis = pb.no_rkm_medis " +
            "inner join bayi_ibu_mapping bm on bm.no_rkm_medis_bayi = pb.no_rkm_medis " +
            "left join pegawai pg on pg.nik = pb.penolong " +
            "left join satu_sehat_bayi_baru_lahir bl on bl.no_rawat = bm.no_rawat_bayi " +
            "left join satu_sehat_akta_kelahiran ak on ak.no_rawat = bm.no_rawat_bayi " +
            "where p.tgl_lahir between ? and ? " +
            "  and bm.status <> 'Ditolak' " +
            "  and (bm.no_rawat_bayi like ? or p.nm_pasien like ? or pb.no_rkm_medis like ?) " +
            "order by p.tgl_lahir desc, pb.jam_lahir desc")) {
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
            String cari = "%" + TCari.getText().trim() + "%";
            ps.setString(3, cari); ps.setString(4, cari); ps.setString(5, cari);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) tabMode.addRow(new String[]{
                    rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                    rs.getString("tgl_lahir"), rs.getString("ihs_bayi"), rs.getString("nik_ibu"),
                    rs.getString("nama_ibu"), rs.getString("status"), rs.getString("no_transaksi"),
                    rs.getString("jam_lahir"), rs.getString("berat_badan"), rs.getString("panjang_badan"),
                    rs.getString("anakke"), rs.getString("nama_penolong"), rs.getString("encounter_bayi")});
            }
        } catch (Exception e) { System.out.println("Notif tampil akta : " + e); }
        LCount.setText("Record : " + tabMode.getRowCount());
    }

    /**
     * Tahap 3 & 4: daftarkan bayi ke MPI lalu kirim EpisodeOfCare, Encounter, dan
     * delapan Observation. Seluruh isinya ditarik otomatis dari pasien_bayi oleh
     * SatuSehatDataKelahiranHelper — tidak ada yang perlu diketik di form ini.
     *
     * Aman ditekan berulang: yang sudah punya ID dilewati, bukan dikirim ulang.
     */
    private void kirimDataKelahiran() {
        int r = tbData.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Maaf, silahkan pilih dulu barisnya...!!!");
            return;
        }
        String noRawat = v(r, 0), noRm = v(r, 1);
        if (JOptionPane.showConfirmDialog(this,
                "Kirim data kelahiran " + v(r, 2) + " (" + noRm + ") ke Satu Sehat?",
                "Kirim Data Kelahiran", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SatuSehatDataKelahiranHelper.Hasil h;
        try {
            h = SatuSehatDataKelahiranHelper.kirim(noRawat, noRm);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
        JOptionPane.showMessageDialog(this,
                (h.sukses ? "Berhasil dikirim.\n\n" : "Belum lengkap.\n\n") + h.ringkasan(),
                "Kirim Data Kelahiran",
                h.sukses ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        tampil();
    }

    private void getData() {
        int r = tbData.getSelectedRow();
        if (r < 0) return;
        TNoRawat.setText(v(r, 0)); TNamaBayi.setText(v(r, 2)); TIhsBayi.setText(v(r, 4));
        TNikIbu.setText(v(r, 5)); TNamaIbu.setText(v(r, 6));
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select * from satu_sehat_akta_kelahiran where no_rawat=?")) {
            ps.setString(1, TNoRawat.getText());
            try (ResultSet s = ps.executeQuery()) {
                if (s.next()) {
                    THpIbu.setText(n(s.getString("hp_ibu"))); TEmailIbu.setText(n(s.getString("email_ibu")));
                    TNoKK.setText(n(s.getString("no_kk")));
                    TNikAyah.setText(n(s.getString("nik_ayah"))); TNamaAyah.setText(n(s.getString("nama_ayah")));
                    THpAyah.setText(n(s.getString("hp_ayah")));
                    TNikPelapor.setText(n(s.getString("nik_pelapor"))); TNamaPelapor.setText(n(s.getString("nama_pelapor")));
                    THpPelapor.setText(n(s.getString("hp_pelapor"))); TEmailPelapor.setText(n(s.getString("email_pelapor")));
                    TNikSaksi.setText(n(s.getString("nik_saksi"))); TNamaSaksi.setText(n(s.getString("nama_saksi")));
                    TFileNikah.setText(n(s.getString("file_buku_nikah")));
                    TFileSKL.setText(n(s.getString("file_skl")));
                    TFileKuasa.setText(n(s.getString("file_surat_kuasa")));
                    TAlamatIbu.setText(n(s.getString("alamat_ibu")));
                    TKotaIbu.setText(n(s.getString("kota_ibu")));
                    TAlamatPelapor.setText(n(s.getString("alamat_pelapor")));
                    TKotaPelapor.setText(n(s.getString("kota_pelapor")));
                    TAlamatSaksi.setText(n(s.getString("alamat_saksi")));
                    TKotaSaksi.setText(n(s.getString("kota_saksi")));
                    setTgl(TglKawin, s.getString("tgl_perkawinan"));
                    setTgl(TglLahirAyah, s.getString("tgl_lahir_ayah"));
                    setTgl(TglLahirPelapor, s.getString("tgl_lahir_pelapor"));
                    setTgl(TglLahirSaksi, s.getString("tgl_lahir_saksi"));
                } else {
                    for (TextBox t : new TextBox[]{THpIbu, TEmailIbu, TNoKK, TNikAyah, TNamaAyah, THpAyah,
                        TNikPelapor, TNamaPelapor, THpPelapor, TEmailPelapor, TNikSaksi, TNamaSaksi,
                        TFileNikah, TFileSKL, TFileKuasa, TAlamatIbu, TKotaIbu,
                        TAlamatPelapor, TKotaPelapor, TAlamatSaksi, TKotaSaksi}) t.setText("");
                }
            }
        } catch (Exception e) { System.out.println("Notif getData akta : " + e); }
        isiAlamatOtomatis();
        LStatus.setText(ringkasValidasi());
    }

    /**
     * Alamat ibu ditarik dari rekam medis ibu lewat bayi_ibu_mapping, jadi
     * petugas tidak perlu mengetik ulang apa yang sudah ada di Khanza. Alamat
     * pelapor & saksi defaultnya menyalin alamat ibu — pada kebanyakan kasus
     * memang serumah, dan kalau beda tinggal ditimpa lalu Simpan.
     *
     * Hanya mengisi yang MASIH KOSONG; yang sudah pernah disimpan tidak ditimpa.
     */
    private void isiAlamatOtomatis() {
        if (TNoRawat.getText().trim().isEmpty()) return;
        if (TAlamatIbu.getText().trim().isEmpty()) {
            try (PreparedStatement ps = koneksi.prepareStatement(
                "select ifnull(p.alamat,'') alamat, ifnull(kel.nm_kel,'') nm_kel, " +
                "ifnull(kec.nm_kec,'') nm_kec, ifnull(kab.nm_kab,'') nm_kab " +
                "from bayi_ibu_mapping bm " +
                "inner join pasien p on p.no_rkm_medis = bm.no_rkm_medis_ibu " +
                "left join kelurahan kel on kel.kd_kel = p.kd_kel " +
                "left join kecamatan kec on kec.kd_kec = p.kd_kec " +
                "left join kabupaten kab on kab.kd_kab = p.kd_kab " +
                "where bm.no_rawat_bayi=?")) {
                ps.setString(1, TNoRawat.getText());
                try (ResultSet s = ps.executeQuery()) {
                    if (s.next()) {
                        // Kelurahan & kecamatan digabung ke baris alamat supaya
                        // utuh; "-" dipakai Khanza untuk data yang belum diisi.
                        List<String> bagian = new ArrayList<>();
                        for (String x : new String[]{s.getString("alamat"), s.getString("nm_kel"), s.getString("nm_kec")})
                            if (x != null && !x.trim().isEmpty() && !x.trim().equals("-")) bagian.add(x.trim());
                        TAlamatIbu.setText(String.join(", ", bagian));
                        String kab = n(s.getString("nm_kab")).trim();
                        TKotaIbu.setText(kab.equals("-") ? "" : kab);
                    }
                }
            } catch (Exception e) { System.out.println("Notif alamat ibu akta : " + e); }
        }
        if (TAlamatPelapor.getText().trim().isEmpty()) {
            TAlamatPelapor.setText(TAlamatIbu.getText()); TKotaPelapor.setText(TKotaIbu.getText());
        }
        if (TAlamatSaksi.getText().trim().isEmpty()) {
            TAlamatSaksi.setText(TAlamatIbu.getText()); TKotaSaksi.setText(TKotaIbu.getText());
        }
    }

    private String v(int r, int c) { Object o = tbData.getValueAt(r, c); return o == null ? "" : o.toString(); }
    private String n(String s) { return s == null ? "" : s; }
    private void setTgl(widget.Tanggal t, String iso) {
        try { if (iso != null && iso.length() >= 10)
            t.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(iso.substring(0, 10))); }
        catch (Exception e) { }
    }

    /** Pusat validasi: 10 item wajib sebelum boleh dikirim. */
    private List<String> itemKurang() {
        List<String> kurang = new ArrayList<>();
        if (!TNikIbu.getText().trim().matches("\\d{16}")) kurang.add("NIK Ibu");
        if (TIhsBayi.getText().trim().isEmpty()) kurang.add("IHS Bayi (Satu Sehat)");
        if (TNamaBayi.getText().trim().isEmpty()) kurang.add("Nama Bayi");
        if (TNamaAyah.getText().trim().isEmpty()) kurang.add("Nama Ayah");
        if (!TNikAyah.getText().trim().matches("\\d{16}")) kurang.add("NIK Ayah");
        if (!TNikPelapor.getText().trim().matches("\\d{16}")) kurang.add("NIK Pelapor");
        if (TEmailPelapor.getText().trim().isEmpty()) kurang.add("Email Pelapor");
        if (!berkasAda(TFileNikah)) kurang.add("Buku Nikah / SPTJM");
        if (!berkasAda(TFileSKL)) kurang.add("Surat Keterangan Lahir");
        if (!berkasAda(TFileKuasa)) kurang.add("Surat Kuasa / Formulir");
        return kurang;
    }
    /** Semua halaman harus benar-benar ada di disk DAN berformat JPEG. */
    private boolean berkasAda(TextBox t) {
        List<String> path = daftarBerkas(t.getText());
        if (path.isEmpty()) return false;
        for (String p : path) {
            if (!new File(p).isFile() || !jpeg(p)) return false;
        }
        return true;
    }
    private String ringkasValidasi() {
        int kurang = itemKurang().size();
        return kurang == 0 ? "Pusat Validasi : 10/10 item wajib siap kirim."
                           : "Pusat Validasi : " + (10 - kurang) + "/10 siap — belum lengkap: " + String.join(", ", itemKurang());
    }

    private void simpan() {
        if (TNoRawat.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih bayi dulu dari daftar di bawah."); return; }
        try (PreparedStatement ps = koneksi.prepareStatement(
            "insert into satu_sehat_akta_kelahiran (no_rawat,ihs_bayi,nik_ibu,nama_ibu,hp_ibu,email_ibu,no_kk,tgl_perkawinan," +
            "nik_ayah,nama_ayah,tgl_lahir_ayah,hp_ayah,nik_pelapor,nama_pelapor,tgl_lahir_pelapor,hp_pelapor,email_pelapor," +
            "nik_saksi,nama_saksi,tgl_lahir_saksi,file_buku_nikah,file_skl,file_surat_kuasa," +
            "alamat_ibu,kota_ibu,alamat_pelapor,kota_pelapor,alamat_saksi,kota_saksi) " +
            "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) " +
            "on duplicate key update ihs_bayi=values(ihs_bayi),nik_ibu=values(nik_ibu),nama_ibu=values(nama_ibu)," +
            "hp_ibu=values(hp_ibu),email_ibu=values(email_ibu),no_kk=values(no_kk),tgl_perkawinan=values(tgl_perkawinan)," +
            "nik_ayah=values(nik_ayah),nama_ayah=values(nama_ayah),tgl_lahir_ayah=values(tgl_lahir_ayah),hp_ayah=values(hp_ayah)," +
            "nik_pelapor=values(nik_pelapor),nama_pelapor=values(nama_pelapor),tgl_lahir_pelapor=values(tgl_lahir_pelapor)," +
            "hp_pelapor=values(hp_pelapor),email_pelapor=values(email_pelapor),nik_saksi=values(nik_saksi)," +
            "nama_saksi=values(nama_saksi),tgl_lahir_saksi=values(tgl_lahir_saksi),file_buku_nikah=values(file_buku_nikah)," +
            "file_skl=values(file_skl),file_surat_kuasa=values(file_surat_kuasa)," +
            "alamat_ibu=values(alamat_ibu),kota_ibu=values(kota_ibu)," +
            "alamat_pelapor=values(alamat_pelapor),kota_pelapor=values(kota_pelapor)," +
            "alamat_saksi=values(alamat_saksi),kota_saksi=values(kota_saksi)")) {
            int i = 1;
            ps.setString(i++, TNoRawat.getText()); ps.setString(i++, TIhsBayi.getText());
            ps.setString(i++, TNikIbu.getText()); ps.setString(i++, TNamaIbu.getText());
            ps.setString(i++, THpIbu.getText()); ps.setString(i++, TEmailIbu.getText());
            ps.setString(i++, TNoKK.getText()); ps.setString(i++, tglIso(TglKawin));
            ps.setString(i++, TNikAyah.getText()); ps.setString(i++, TNamaAyah.getText());
            ps.setString(i++, tglIso(TglLahirAyah)); ps.setString(i++, THpAyah.getText());
            ps.setString(i++, TNikPelapor.getText()); ps.setString(i++, TNamaPelapor.getText());
            ps.setString(i++, tglIso(TglLahirPelapor)); ps.setString(i++, THpPelapor.getText());
            ps.setString(i++, TEmailPelapor.getText());
            ps.setString(i++, TNikSaksi.getText()); ps.setString(i++, TNamaSaksi.getText());
            ps.setString(i++, tglIso(TglLahirSaksi));
            ps.setString(i++, TFileNikah.getText()); ps.setString(i++, TFileSKL.getText());
            ps.setString(i++, TFileKuasa.getText());
            ps.setString(i++, TAlamatIbu.getText()); ps.setString(i++, TKotaIbu.getText());
            ps.setString(i++, TAlamatPelapor.getText()); ps.setString(i++, TKotaPelapor.getText());
            ps.setString(i++, TAlamatSaksi.getText()); ps.setString(i++, TKotaSaksi.getText());
            ps.executeUpdate();
            LStatus.setText(ringkasValidasi());
            JOptionPane.showMessageDialog(this, "Data akta tersimpan.");
            tampil();
        } catch (Exception e) {
            System.out.println("Notif simpan akta : " + e);
            JOptionPane.showMessageDialog(this, "Gagal menyimpan : " + e);
        }
    }
    private String tglIso(widget.Tanggal t) {
        try { return Valid.SetTgl(t.getSelectedItem() + ""); } catch (Exception e) { return null; }
    }

    // ── Pengiriman ke SatuSehat ──────────────────────────────────────────────
    /** Waktu SEKARANG dalam UTC+00, format yang diminta playbook. */
    private String utcSekarang() {
        return ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+00:00'"));
    }
    private String base64(String path) throws Exception {
        return java.util.Base64.getEncoder().encodeToString(Files.readAllBytes(new File(path).toPath()));
    }

    // ── Lembar persetujuan pemohon ───────────────────────────────────────────

    /**
     * Cetak "Formulir Permohonan Pelayanan Pencatatan Kelahiran" — lembar
     * persetujuan yang ditandatangani pemohon.
     *
     * Juknis langkah 11-13: petugas MENCETAK lembar persetujuan berisi data
     * yang akan dikirim, pelapor memeriksanya, lalu menandatanganinya. Hasil
     * tandatangannya yang kemudian dipindai dan diunggah sebagai `surat-kuasa`.
     *
     * Isinya diambil dari data yang benar-benar akan dikirim, bukan formulir
     * kosong: kalau yang tercetak beda dari yang dikirim, tanda tangan pemohon
     * tidak ada artinya.
     *
     * Jasper-nya rptFormulirPersetujuanKelahiran, seluruhnya berbasis parameter
     * dengan JREmptyDataSource — datanya ada di layar, sebagian belum tersimpan,
     * jadi tidak bisa diambil lewat query.
     */
    private void cetakLembarPersetujuan() {
        int r = tbData.getSelectedRow();
        if (r < 0 || TNoRawat.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih bayi dulu dari daftar."); return;
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            String noRm = v(r, 1);
            String jk = Sequel.cariIsi("select ifnull(jk,'') from pasien where no_rkm_medis=?", noRm);
            java.text.SimpleDateFormat hari = new java.text.SimpleDateFormat("dd-MM-yyyy");
            java.text.SimpleDateFormat panjang =
                new java.text.SimpleDateFormat("dd MMMM yyyy", new java.util.Locale("id", "ID"));

            Map<String, Object> p = new HashMap<>();
            p.put("no_transaksi", Sequel.cariIsi(
                "select ifnull(no_transaksi,'') from satu_sehat_akta_kelahiran where no_rawat=?", TNoRawat.getText()));
            p.put("ihs_bayi", TIhsBayi.getText());
            p.put("kode_faskes", orgId);
            p.put("nama_faskes", akses.getnamars());
            p.put("alamat_faskes", akses.getalamatrs());
            p.put("propinsi_faskes", akses.getpropinsirs());
            p.put("kabupaten_faskes", akses.getkabupatenrs());

            p.put("nama_bayi", TNamaBayi.getText());
            p.put("jk_bayi", jk.equalsIgnoreCase("P") ? "Perempuan" : jk.equalsIgnoreCase("L") ? "Laki-laki" : "");
            p.put("tempat_dilahirkan", akses.getnamars());
            p.put("tempat_kelahiran", Sequel.cariIsi("select ifnull(tmp_lahir,'') from pasien where no_rkm_medis=?", noRm));
            p.put("tgl_kelahiran", v(r, 3));
            p.put("waktu_kelahiran", Sequel.cariIsi("select ifnull(jam_lahir,'') from pasien_bayi where no_rkm_medis=?", noRm));
            p.put("jenis_kelahiran", Sequel.cariIsi("select ifnull(proses_lahir,'') from pasien_bayi where no_rkm_medis=?", noRm));
            p.put("anak_ke", Sequel.cariIsi("select ifnull(anakke,'') from pasien_bayi where no_rkm_medis=?", noRm));
            p.put("penolong", Sequel.cariIsi(
                "select ifnull(pg.nama,'') from pasien_bayi pb left join pegawai pg on pg.nik=pb.penolong "
              + "where pb.no_rkm_medis=?", noRm));
            p.put("berat", isi(Sequel.cariIsi("select ifnull(berat_badan,'') from pasien_bayi where no_rkm_medis=?", noRm), " gram"));
            p.put("panjang", isi(Sequel.cariIsi("select ifnull(panjang_badan,'') from pasien_bayi where no_rkm_medis=?", noRm), " cm"));
            p.put("agama", Sequel.cariIsi("select ifnull(agama,'') from pasien where no_rkm_medis=?", noRm));
            p.put("no_kk", TNoKK.getText());
            p.put("kepala_keluarga", TNamaAyah.getText());

            p.put("nik_ibu", TNikIbu.getText());   p.put("nama_ibu", TNamaIbu.getText());
            // Tanggal lahir ibu tidak ada widget-nya di form ini — diambil dari baris
            // akta yang sudah tersimpan.
            p.put("tgl_lahir_ibu", Sequel.cariIsi(
                "select ifnull(date_format(tgl_lahir_ibu,'%d-%m-%Y'),'') from satu_sehat_akta_kelahiran where no_rawat=?",
                TNoRawat.getText()));
            p.put("tgl_kawin", tglTeks(TglKawin));
            p.put("nik_ayah", TNikAyah.getText()); p.put("nama_ayah", TNamaAyah.getText());
            p.put("tgl_lahir_ayah", tglTeks(TglLahirAyah));
            p.put("nik_pelapor", TNikPelapor.getText()); p.put("nama_pelapor", TNamaPelapor.getText());
            p.put("tgl_lahir_pelapor", tglTeks(TglLahirPelapor));
            p.put("tgl_lapor", hari.format(new java.util.Date()));
            p.put("hp_pelapor", THpPelapor.getText()); p.put("email_pelapor", TEmailPelapor.getText());
            p.put("nik_saksi", TNikSaksi.getText()); p.put("nama_saksi", TNamaSaksi.getText());
            p.put("tgl_lahir_saksi", tglTeks(TglLahirSaksi));

            p.put("dok_skl",   berkasAda(TFileSKL)   ? "\u221a" : "-");
            p.put("dok_nikah", berkasAda(TFileNikah) ? "\u221a" : "-");
            p.put("dok_lain",  berkasAda(TFileKuasa) ? "\u221a" : "-");
            p.put("kota_tgl_ttd", akses.getkabupatenrs() + ", " + panjang.format(new java.util.Date()));
            p.put("nama_pemohon", "( " + TNamaPelapor.getText() + " )");

            net.sf.jasperreports.engine.JasperPrint jp =
                net.sf.jasperreports.engine.JasperFillManager.fillReport(
                    "./report/rptFormulirPersetujuanKelahiran.jasper", p,
                    new net.sf.jasperreports.engine.JREmptyDataSource());
            net.sf.jasperreports.view.JasperViewer v =
                new net.sf.jasperreports.view.JasperViewer(jp, false);
            v.setTitle("::[ Formulir Permohonan Pencatatan Kelahiran ]::");
            java.awt.Dimension layar = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            v.setSize(layar.width - 60, layar.height - 60);
            v.setLocationRelativeTo(null);
            v.setVisible(true);
        } catch (Exception e) {
            System.out.println("Notif cetak persetujuan : " + e);
            JOptionPane.showMessageDialog(this, "Gagal mencetak lembar persetujuan: " + e.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    /** Tambahkan satuan hanya kalau nilainya ada, supaya tidak tercetak " gram" sendirian. */
    private static String isi(String nilai, String satuan) {
        return (nilai == null || nilai.trim().isEmpty()) ? "" : nilai.trim() + satuan;
    }

    private static String tglTeks(widget.Tanggal t) {
        try { return new java.text.SimpleDateFormat("dd-MM-yyyy").format(t.getDate()); }
        catch (Exception e) { return ""; }
    }

    // ── Surat Keterangan Lahir langsung dari Jasper ──────────────────────────

    /**
     * Cetak Surat Keterangan Lahir dari Jasper lalu simpan sebagai JPEG, dan
     * pasang hasilnya ke isian berkas SKL.
     *
     * SKL itu surat yang DIBUAT rumah sakit sendiri dan jasper-nya sudah ada di
     * form Kelahiran Bayi, jadi tidak masuk akal kalau petugas harus mencetaknya
     * lalu memindainya lagi hanya untuk diunggah. Di sini jasper yang sama diisi
     * dengan query yang sama, lalu tiap halamannya dirender jadi JPEG — format
     * yang memang diwajibkan juknis.
     *
     * Ada tiga varian jasper SKL di Khanza; petugas memilih yang dipakai
     * rumah sakitnya.
     */
    private void ambilSklDariJasper() {
        String noRm = v(tbData.getSelectedRow() < 0 ? -1 : tbData.getSelectedRow(), 1);
        if (TNoRawat.getText().trim().isEmpty() || noRm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih bayi dulu dari daftar."); return;
        }
        Object[] pilihan = {"Surat Keterangan Lahir 1", "Surat Keterangan Lahir 2", "Surat Keterangan Lahir 3"};
        Object dipilih = JOptionPane.showInputDialog(this,
            "Bentuk Surat Keterangan Lahir mana yang dipakai rumah sakit ini?",
            "Ambil SKL dari Jasper", JOptionPane.QUESTION_MESSAGE, null, pilihan, pilihan[0]);
        if (dipilih == null) return;
        String berkasJasper = dipilih.equals(pilihan[1]) ? "rptSKL2.jasper"
                            : dipilih.equals(pilihan[2]) ? "rptSKL3.jasper" : "rptSKL.jasper";
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            List<String> halaman = cetakSklJpeg(berkasJasper, noRm);
            if (halaman.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Jasper tidak menghasilkan halaman apa pun.");
                return;
            }
            TFileSKL.setText(String.join("|", halaman));
            JOptionPane.showMessageDialog(this,
                halaman.size() + " halaman SKL dibuat sebagai JPEG:\n\n" + String.join("\n", halaman),
                "Ambil SKL dari Jasper", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            System.out.println("Notif ambil SKL : " + e);
            JOptionPane.showMessageDialog(this, "Gagal membuat SKL: " + e.getMessage());
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    /** Isi jasper SKL untuk satu bayi, render tiap halamannya jadi berkas JPEG. */
    private List<String> cetakSklJpeg(String berkasJasper, String noRm) throws Exception {
        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("nomor", Sequel.cariIsi("select ifnull(no_skl,'') from pasien_bayi where no_rkm_medis=?", noRm));
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        param.put("logo2", Sequel.cariGambar("select setting.logo from setting"));

        // Query gabungan dari ketiga menu "Surat Keterangan Lahir" di DlgIKBBayi.
        // SKL 2 dan 3 memakai query yang sama; SKL 1 memakai query itu tanpa
        // pekerjaanpj dan no_ktp. Kolom lebih tidak mengganggu — JRResultSetDataSource
        // hanya membaca field yang dideklarasikan jasper-nya — jadi satu query
        // gabungan melayani ketiganya.
        String qry =
            "select pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, "
          + "pasien.tgl_lahir,pasien_bayi.jam_lahir, pasien.umur, "
          + "pasien.tgl_daftar,pasien.nm_ibu,pasien_bayi.umur_ibu,pasien.pekerjaanpj, "
          + "pasien_bayi.nama_ayah,pasien_bayi.umur_ayah,pasien.no_ktp,"
          + "concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat, "
          + "pasien_bayi.berat_badan,pasien_bayi.panjang_badan, pasien_bayi.lingkar_kepala, "
          + "pasien_bayi.proses_lahir,pasien_bayi.anakke, pasien_bayi.keterangan, "
          + "pasien_bayi.diagnosa,pasien_bayi.penyulit_kehamilan,pasien_bayi.ketuban,"
          + "pasien_bayi.lingkar_perut,pasien_bayi.lingkar_dada,pegawai.nama,"
          + "pasien_bayi.no_skl from pasien inner join pegawai inner join pasien_bayi "
          + "inner join kelurahan inner join kecamatan inner join kabupaten "
          + "on pasien.no_rkm_medis=pasien_bayi.no_rkm_medis and pasien_bayi.penolong=pegawai.nik "
          + "and pasien.kd_kel=kelurahan.kd_kel and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab "
          + "where pasien_bayi.no_rkm_medis=?";

        List<String> hasil = new ArrayList<>();
        File folder = new File("berkas_akta" + File.separator + TNoRawat.getText().replace("/", ""));
        if (!folder.isDirectory() && !folder.mkdirs()) {
            throw new IllegalStateException("tidak bisa membuat folder " + folder.getPath());
        }
        try (PreparedStatement ps = koneksi.prepareStatement(qry)) {
            ps.setString(1, noRm);
            try (ResultSet rs = ps.executeQuery()) {
                net.sf.jasperreports.engine.JasperPrint jp =
                    net.sf.jasperreports.engine.JasperFillManager.fillReport(
                        "./report/" + berkasJasper, param,
                        new net.sf.jasperreports.engine.JRResultSetDataSource(rs));
                int jml = jp.getPages().size();
                for (int i = 0; i < jml; i++) {
                    // zoom 2f: ukuran A4 pada ~150 dpi, cukup supaya teksnya terbaca
                    // jelas tanpa membuat base64-nya membengkak.
                    java.awt.Image gbr = net.sf.jasperreports.engine.JasperPrintManager
                            .printPageToImage(jp, i, 2f);
                    hasil.add(simpanJpeg(gbr, new File(folder, "skl-" + (i + 1) + ".jpg")));
                }
            }
        }
        return hasil;
    }

    /**
     * Tulis gambar jadi JPEG.
     *
     * Jasper mengembalikan gambar ber-alpha (ARGB). JPEG tidak mengenal alpha,
     * dan menulis ARGB langsung menghasilkan berkas yang warnanya kacau atau
     * tidak terbaca sama sekali — persis kendala "dokumen lampiran tidak
     * terbaca" yang paling sering dilaporkan. Jadi digambar ulang dulu di atas
     * kanvas putih tanpa alpha.
     */
    private String simpanJpeg(java.awt.Image gbr, File tujuan) throws Exception {
        int w = gbr.getWidth(null), h = gbr.getHeight(null);
        java.awt.image.BufferedImage rgb =
            new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g = rgb.createGraphics();
        g.setColor(java.awt.Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.drawImage(gbr, 0, 0, null);
        g.dispose();
        if (!javax.imageio.ImageIO.write(rgb, "jpg", tujuan)) {
            throw new IllegalStateException("tidak ada penulis JPEG di JVM ini");
        }
        return tujuan.getAbsolutePath();
    }

    /** Rangkai semua halaman jadi daftar attachment JSON. */
    private String lampiran(String isi, String judul) throws Exception {
        List<String> bagian = new ArrayList<>();
        List<String> path = daftarBerkas(isi);
        for (int i = 0; i < path.size(); i++) {
            String namaHalaman = path.size() == 1 ? judul : judul + " hal. " + (i + 1);
            bagian.add("{\"attachment\":{\"contentType\":\"" + tipeIsi(path.get(i))
                     + "\",\"data\":\"" + base64(path.get(i))
                     + "\",\"title\":\"" + esc(namaHalaman)
                     + "\",\"creation\":\"" + utcSekarang() + "\"}}");
        }
        return String.join(",", bagian);
    }
    /**
     * contentType lampiran. SELALU image/jpeg — juknis mewajibkannya
     * ("DocumentReference.content.attachment.contentType menggunakan
     * image/jpeg"). Berkas berekstensi lain ditolak lebih awal oleh
     * {@link #itemKurang()}, jadi tidak ada cabang PDF/PNG di sini.
     */
    private String tipeIsi(String path) { return "image/jpeg"; }

    private static boolean jpeg(String path) {
        String p = path.toLowerCase();
        return p.endsWith(".jpg") || p.endsWith(".jpeg");
    }
    private String esc(String s) { return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\""); }

    private String post(String resource, String json) throws Exception {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.add("Authorization", "Bearer " + api.TokenSatuSehat());
        HttpEntity<String> req = new HttpEntity<>(json, h);
        String hasil = api.getRest().exchange(link + "/" + resource, HttpMethod.POST, req, String.class).getBody();
        JsonNode root = mapper.readTree(hasil);
        // asText() tanpa argumen: classpath proyek memuat beberapa versi Jackson
        // dan yang tertua (2.2.3) belum punya asText(String). Node yang tidak ada
        // mengembalikan "" sendiri, jadi nilai bawaan tidak diperlukan.
        return root.path("id").asText();
    }

    /**
     * POST Task, lalu tuliskan kembali nomor transaksinya.
     *
     * Nomor transaksi HANYA muncul di badan balasan POST. SatuSehat tidak
     * menyimpannya ke resource Task: GET Task/{id} sesudahnya hanya memuat
     * identifier kita sendiri, riwayat versinya cuma satu, dan pencarian
     * Task?identifier=...transaction-number|<nomor> mengembalikan nol. Jadi
     * kalau tidak ditangkap saat itu juga, nomornya hilang — dan pemantau yang
     * membaca lewat GET tidak akan pernah melihatnya.
     *
     * Karena itu nomor tersebut langsung di-PUT balik ke Task yang sama
     * (PUT Task diizinkan, tidak seperti EpisodeOfCare).
     *
     * @return String[]{id task, nomor transaksi}; nomor "" kalau belum terbit.
     */
    private String[] postTask(String json) throws Exception {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.add("Authorization", "Bearer " + api.TokenSatuSehat());
        String hasil = api.getRest().exchange(link + "/Task", HttpMethod.POST,
            new HttpEntity<>(json, h), String.class).getBody();
        JsonNode root = mapper.readTree(hasil);
        String id = root.path("id").asText();
        String trx = "";
        for (JsonNode ident : root.path("identifier")) {
            if (SYS_NO_TRANSAKSI.equals(ident.path("system").asText())) {
                trx = ident.path("value").asText();
            }
        }
        if (!id.isEmpty() && !trx.isEmpty()) {
            try {
                String isi = mapper.readTree(json).toString();
                // Sisipkan id + identifier nomor transaksi lalu kirim sebagai PUT.
                String baru = isi.substring(0, isi.length() - 1)
                    + ",\"id\":\"" + esc(id) + "\"}";
                baru = baru.replace("\"identifier\":[",
                    "\"identifier\":[{\"system\":\"" + SYS_NO_TRANSAKSI + "\",\"value\":\"" + esc(trx) + "\"},");
                HttpHeaders h2 = new HttpHeaders();
                h2.setContentType(MediaType.APPLICATION_JSON);
                h2.add("Authorization", "Bearer " + api.TokenSatuSehat());
                api.getRest().exchange(link + "/Task/" + id, HttpMethod.PUT,
                    new HttpEntity<>(baru, h2), String.class);
            } catch (Exception e) {
                // Nomornya sudah kita simpan sendiri di database, jadi gagal
                // menuliskan balik tidak membatalkan pengiriman.
                System.out.println("Notif tulis balik no.transaksi : " + e);
            }
        }
        return new String[]{id, trx};
    }

    /**
     * RelatedPerson untuk ibu / ayah / pelapor / saksi.
     *
     * `address` dan `communication` ikut dikirim sejak rscb-43. Keduanya ada di
     * contoh payload resmi (koleksi Postman 05. Variabel Data Kelahiran Lainnya)
     * dan formulir SIAK pada lampiran Surat Edaran Ditjen Dukcapil
     * 400.8.2.2/10077/DUKCAPIL menampilkan Alamat + RT/RW + wilayah untuk
     * keempat orang itu. Tanpa alamat, operator Dukcapil harus mengetik ulang.
     *
     * RT/RW tidak dipisah karena di Khanza memang menyatu di teks alamat.
     * Ekstensi administrativeCode (kode Permendagri) belum dikirim — kode
     * wilayah Khanza adalah id auto-increment internal, bukan kode Permendagri.
     */
    private String jsonRelatedPerson(String kode, String tampil, String nik, String nama,
                                     String tglLahir, String hp, String email, String gender,
                                     String alamat, String kota) {
        StringBuilder b = new StringBuilder();
        b.append("{\"resourceType\":\"RelatedPerson\",")
         .append("\"meta\":{\"profile\":[\"https://fhir.kemkes.go.id/r4/StructureDefinition/RelatedPerson\"]},")
         .append("\"identifier\":[{\"use\":\"official\",\"system\":\"https://fhir.kemkes.go.id/id/nik\",\"value\":\"").append(esc(nik)).append("\"}],")
         .append("\"active\":true,")
         .append("\"relationship\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/v3-RoleCode\",")
         .append("\"code\":\"").append(kode).append("\",\"display\":\"").append(tampil).append("\"}],\"text\":\"").append(tampil).append("\"}],")
         .append("\"patient\":{\"reference\":\"Patient/").append(esc(TIhsBayi.getText())).append("\"},")
         .append("\"name\":[{\"use\":\"official\",\"text\":\"").append(esc(nama)).append("\"}]");
        if (gender != null) b.append(",\"gender\":\"").append(gender).append("\"");
        if (tglLahir != null && tglLahir.length() >= 10) b.append(",\"birthDate\":\"").append(tglLahir).append("\"");
        List<String> tel = new ArrayList<>();
        if (hp != null && !hp.trim().isEmpty())
            tel.add("{\"system\":\"phone\",\"value\":\"" + esc(hp.trim()) + "\",\"use\":\"mobile\"}");
        if (email != null && !email.trim().isEmpty())
            tel.add("{\"system\":\"email\",\"value\":\"" + esc(email.trim()) + "\",\"use\":\"home\"}");
        if (!tel.isEmpty()) b.append(",\"telecom\":[").append(String.join(",", tel)).append("]");

        if (alamat != null && !alamat.trim().isEmpty()) {
            b.append(",\"address\":[{\"use\":\"home\",\"line\":[\"").append(esc(alamat.trim())).append("\"]");
            if (kota != null && !kota.trim().isEmpty())
                b.append(",\"city\":\"").append(esc(kota.trim())).append("\"");
            b.append(",\"country\":\"ID\"}]");
        }
        b.append(",\"communication\":[{\"language\":{\"coding\":[{\"system\":\"urn:ietf:bcp:47\",")
         .append("\"code\":\"id-ID\",\"display\":\"Indonesian\"}],\"text\":\"Indonesian\"},\"preferred\":true}]");

        b.append("}");
        return b.toString();
    }

    private String jsonDocumentReference(String kode, String tampil, String path) throws Exception {
        return "{\"resourceType\":\"DocumentReference\",\"status\":\"current\","
             + "\"type\":{\"coding\":[{\"system\":\"http://terminology.kemkes.go.id\",\"code\":\"" + kode
             + "\",\"display\":\"" + tampil + "\"}]},"
             + "\"subject\":{\"reference\":\"Patient/" + esc(TIhsBayi.getText()) + "\"},"
             + "\"date\":\"" + utcSekarang() + "\","
             // custodian ada di contoh payload resmi: fasyankes yang menyimpan berkasnya.
             + "\"custodian\":{\"reference\":\"Organization/" + esc(orgId) + "\"},"
             // Satu halaman satu attachment: DocumentReference.content memang
             // berulang, jadi dokumen berhalaman banyak tidak perlu digabung
             // paksa jadi satu gambar.
             + "\"content\":[" + lampiran(path, tampil) + "]}";
    }

    private String jsonTask(String encounter) {
        String kini = utcSekarang();
        return "{\"resourceType\":\"Task\","
             // meta.tag creator ada di contoh payload resmi (item 07.03). Sistemnya
             // .../creator, kodenya kode fasyankes — BUKAN sinonim owner, karena
             // gateway menstempelnya dengan organisasi client yang menulis.
             + "\"meta\":{\"tag\":[{\"system\":\"http://sys-ids.kemkes.go.id/creator\",\"code\":\"" + esc(orgId)
             + "\",\"display\":\"" + esc(akses.getnamars()) + "\"}]},"
             + "\"identifier\":[{\"system\":\"http://sys-ids.kemkes.go.id/task/" + esc(orgId) + "\",\"value\":\"AKTA-"
             + esc(TNoRawat.getText().replace("/", "")) + "\"}],"
             // intent "order" mengikuti contoh payload resmi.
             + "\"status\":\"requested\",\"intent\":\"order\",\"priority\":\"routine\","
             + "\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"444561001\",\"display\":\"Birth certificate\"}]},"
             + "\"authoredOn\":\"" + kini + "\",\"lastModified\":\"" + kini + "\","
             + "\"requester\":{\"reference\":\"Organization/" + esc(orgId) + "\"},"
             + "\"owner\":{\"reference\":\"Organization/" + esc(orgId) + "\"},"
             + (encounter.isEmpty() ? "" : "\"encounter\":{\"reference\":\"Encounter/" + esc(encounter) + "\"},")
             + "\"for\":{\"reference\":\"Patient/" + esc(TIhsBayi.getText()) + "\"}}";
    }

    private void kirim() {
        if (TNoRawat.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih bayi dulu dari daftar."); return; }
        List<String> kurang = itemKurang();
        if (!kurang.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Belum bisa dikirim. " + kurang.size() + " item wajib belum lengkap:\n\n- " + String.join("\n- ", kurang),
                "Pusat Validasi", JOptionPane.WARNING_MESSAGE);
            return; }
        if (JOptionPane.showConfirmDialog(this,
                "Kirim permohonan akta kelahiran untuk " + TNamaBayi.getText() + " ke SatuSehat?\n\n"
                + "Data akan diteruskan ke SIAK Dukcapil dan tidak dapat ditarik kembali dari sini.",
                "Konfirmasi", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        simpan();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        BtnKirim.setEnabled(false);
        final String noRawat = TNoRawat.getText();
        executor.submit(() -> {
            String pesan;
            try { pesan = jalankanKirim(noRawat); }
            catch (Exception e) { pesan = "Gagal : " + e.getMessage(); System.out.println("Notif kirim akta : " + e); }
            final String hasil = pesan;
            SwingUtilities.invokeLater(() -> {
                setCursor(Cursor.getDefaultCursor());
                BtnKirim.setEnabled(true);
                LStatus.setText(hasil);
                tampil();
                tampilProgres();
            });
        });
    }

    /** Urutan kiriman tahap 5 lalu 6. Setiap resource dicatat supaya tidak dobel. */
    private String jalankanKirim(String noRawat) throws Exception {
        siapkanProgres(noRawat);
        String encounter = Sequel.cariIsi(
            "select ifnull(encounter_bayi,'') from satu_sehat_akta_kelahiran where no_rawat=?", noRawat);
        if (encounter == null) encounter = "";

        // Tahap 5 — keluarga & dokumen
        simpanResource(noRawat, "RelatedPersonIbu", post("RelatedPerson",
            jsonRelatedPerson("NMTH", "Natural Mother", TNikIbu.getText(), TNamaIbu.getText(),
                null, THpIbu.getText(), TEmailIbu.getText(), "female",
                TAlamatIbu.getText(), TKotaIbu.getText())));
        // Ayah memakai alamat ibu: keduanya satu Kartu Keluarga (no_kk tunggal).
        simpanResource(noRawat, "RelatedPersonAyah", post("RelatedPerson",
            jsonRelatedPerson("NFTH", "Natural Father", TNikAyah.getText(), TNamaAyah.getText(),
                tglIso(TglLahirAyah), THpAyah.getText(), null, "male",
                TAlamatIbu.getText(), TKotaIbu.getText())));
        simpanResource(noRawat, "RelatedPersonPelapor", post("RelatedPerson",
            jsonRelatedPerson("pelapor-kelahiran", "Pelapor Kelahiran", TNikPelapor.getText(), TNamaPelapor.getText(),
                tglIso(TglLahirPelapor), THpPelapor.getText(), TEmailPelapor.getText(), null,
                TAlamatPelapor.getText(), TKotaPelapor.getText())));
        if (TNikSaksi.getText().trim().matches("\\d{16}"))
            simpanResource(noRawat, "RelatedPersonSaksi", post("RelatedPerson",
                jsonRelatedPerson("saksi-kelahiran", "Saksi Kelahiran", TNikSaksi.getText(), TNamaSaksi.getText(),
                    tglIso(TglLahirSaksi), null, null, null,
                    TAlamatSaksi.getText(), TKotaSaksi.getText())));

        simpanResource(noRawat, "DocumentReferenceBukuNikah",
            post("DocumentReference", jsonDocumentReference("dokumen-buku-nikah", "Buku Nikah", TFileNikah.getText())));
        simpanResource(noRawat, "DocumentReferenceSKL",
            post("DocumentReference", jsonDocumentReference("surat-lahir", "Surat Keterangan Lahir", TFileSKL.getText())));
        simpanResource(noRawat, "DocumentReferenceSuratKuasa",
            post("DocumentReference", jsonDocumentReference("surat-kuasa", "Surat Kuasa", TFileKuasa.getText())));
        setProgres(noRawat, 1, "BERHASIL", "Pelaporan bayi ke Satu Sehat berhasil");

        // Tahap 6 — Task akta lahir
        String[] hasilTask = postTask(jsonTask(encounter));
        String taskId = hasilTask[0];
        if (taskId.isEmpty()) { setProgres(noRawat, 2, "GAGAL", "Task tidak mengembalikan id"); return "Task gagal."; }
        simpanResource(noRawat, "Task", taskId);
        setProgres(noRawat, 2, "BERHASIL", "Post Task berhasil");

        // Utamakan nomor dari balasan POST; ambilNoTransaksi() cuma cadangan
        // untuk Task lama yang nomornya sudah pernah tertulis di resource.
        String trx = hasilTask[1].isEmpty() ? ambilNoTransaksi(taskId) : hasilTask[1];
        Sequel.queryu2tf("update satu_sehat_akta_kelahiran set task_id=?, no_transaksi=?, status='Terkirim', tgl_kirim=now() where no_rawat=?",
            3, new String[]{taskId, trx, noRawat});
        if (trx.isEmpty()) {
            setProgres(noRawat, 3, "MENUNGGU PROSES", "Menunggu nomor transaksi dari SatuSehat");
            return "Task terkirim (" + taskId + "). Nomor transaksi belum terbit — cek lagi lewat tombol Progres.";
        }
        setProgres(noRawat, 3, "BERHASIL", "data sent successfully");
        setProgres(noRawat, 4, "MENUNGGU PROSES", "Permohonan diterima SIAK");
        return "Terkirim. No. Transaksi : " + trx;
    }

    /**
     * Tanyakan kemajuan di Dukcapil lalu perbarui tahap 4-9 di tabel progres.
     *
     *   GET {base}/v1/data-kelahiran-rme/status
     *       ?transaction_number=<nomor>&faskes_code=<Org ID>
     *
     * Dua hal yang perlu diingat:
     *   - `faskes_code` WAJIB; tanpanya dijawab "Query parameter faskes_code is
     *     required."
     *   - Endpoint ini ADA di staging maupun produksi, walau koleksi Postman
     *     memakukannya ke host produksi. Jadi base URL-nya ikut setelan yang
     *     sedang dipakai, bukan dipaku.
     *
     * Balasannya berisi track_list enam tahap, berurutan sama dengan urut 4..9
     * di tabel progres kita:
     *   1. Permohonan · 2. Proses Biodata · 3. Proses Pembuatan Akta
     *   4. Pengajuan TTE · 5. Verifikasi TTE · 6. Sertifikasi Akta Kelahiran
     */
    private void tarikStatusDukcapil(String noRawat, String trx) {
        if (trx == null || trx.trim().isEmpty()) return;
        try {
            // Base URL diambil dari URL FHIR yang sedang dipakai, dipotong di
            // "/fhir-r4" — supaya ikut berpindah kalau setelannya staging.
            String dasar = link;
            int potong = dasar.indexOf("/fhir-r4");
            if (potong > 0) dasar = dasar.substring(0, potong);
            String url = dasar + "/v1/data-kelahiran-rme/status"
                       + "?transaction_number=" + java.net.URLEncoder.encode(trx, "UTF-8")
                       + "&faskes_code=" + java.net.URLEncoder.encode(orgId, "UTF-8");
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            String badan = api.getRest().exchange(url, HttpMethod.GET,
                    new HttpEntity<>(h), String.class).getBody();
            JsonNode akar = mapper.readTree(badan);

            JsonNode daftar = akar.path("track_list");
            if (!daftar.isArray() || daftar.size() == 0) {
                // Balasan "transaction number ... not found" bentuknya {"message":...}.
                // Itu bukan galat: pengajuannya memang belum sampai ke SIAK.
                String pesan = akar.path("message").asText();
                setProgres(noRawat, 4, "MENUNGGU PROSES",
                        pesan.isEmpty() ? "Dukcapil belum punya catatan untuk nomor ini" : pesan);
                return;
            }
            int urut = 4;
            String urlCetak = "";
            for (JsonNode t : daftar) {
                if (urut > 9) break;
                String st  = t.path("status").asText();
                String tgl = t.path("date").asText();
                String ket = t.path("desc").asText();
                String status = st.equalsIgnoreCase("Selesai") ? "BERHASIL"
                              : st.toLowerCase().contains("gagal") || st.toLowerCase().contains("tolak") ? "GAGAL"
                              : "MENUNGGU PROSES";

                StringBuilder c = new StringBuilder(ket);
                if (!tgl.isEmpty()) c.append(" — ").append(tgl);
                // Lama proses tiap tahap dikirim SIAK; berguna untuk menakar
                // apakah suatu tahap memang lambat atau tersangkut.
                String durasi = t.path("durasi_proses").asText();
                if (!durasi.isEmpty()) c.append(" (").append(durasi).append(")");
                // ALASAN PENOLAKAN — ini yang paling dibutuhkan petugas, karena
                // menyebut persis berkas apa yang kurang. Contoh nyata:
                // "Dok yang di upload tidak lengkap silahkan untuk upload ktp
                //  kedua orang tua, kk orang tua, ktp saksi".
                String alasan = t.path("alasan_desc").asText();
                if (alasan.isEmpty()) alasan = t.path("alasan_penolakan").asText();
                if (!alasan.isEmpty()) c.append(" | DITOLAK: ").append(alasan);
                String tglTolak = t.path("tgl_penolakan").asText();
                if (!tglTolak.isEmpty()) c.append(" [").append(tglTolak).append("]");

                // Tahap 6 membawa tautan cetak aktanya di layanan Dukcapil.
                String u = t.path("url_cetak").asText();
                if (!u.isEmpty()) { urlCetak = u; c.append(" | Cetak: ").append(u); }

                String isi = c.toString();
                if (isi.length() > 255) isi = isi.substring(0, 252) + "...";
                setProgres(noRawat, urut, status, isi);
                urut++;
            }

            boolean selesai = daftar.size() >= 6
                    && "Selesai".equalsIgnoreCase(daftar.get(5).path("status").asText());
            boolean ditolak = false;
            for (JsonNode t : daftar) {
                if (t.path("status").asText().toLowerCase().contains("tolak")) { ditolak = true; break; }
            }
            if (selesai) {
                Sequel.queryu2tf("update satu_sehat_akta_kelahiran set status='Selesai' where no_rawat=?",
                        1, new String[]{noRawat});
            } else if (ditolak) {
                Sequel.queryu2tf("update satu_sehat_akta_kelahiran set status='Gagal' where no_rawat=?",
                        1, new String[]{noRawat});
            }
            if (!urlCetak.isEmpty()) {
                Sequel.queryu2tf("update satu_sehat_akta_kelahiran set url_cetak=? where no_rawat=?",
                        2, new String[]{urlCetak, noRawat});
            }
        } catch (Exception e) {
            // Gagal menanyakan bukan alasan menutup panelnya — yang tersimpan
            // lokal tetap ditampilkan.
            System.out.println("Notif cek status Dukcapil : " + e);
        }
    }

    /**
     * Nomor transaksi diterbitkan SatuSehat pada Task.identifier.
     *
     * Dikenali dari SISTEMNYA, yang persis
     * {@code http://sys-ids.kemkes.go.id/task/transaction-number} — bukan dari
     * awalan nilainya. Task kita sendiri dikirim dengan sistem
     * {@code .../task/{org_id}}; SatuSehat menambahkan identifier kedua setelah
     * pelaporannya diterima.
     *
     * Bentuk nilainya YYYYMMDD + delapan alfanumerik, misalnya 20260821LCYVSBG5.
     * Versi sebelumnya mencari nilai berawalan "TRX" sehingga nomor yang sah pun
     * tidak pernah terbaca.
     */
    private static final String SYS_NO_TRANSAKSI = "http://sys-ids.kemkes.go.id/task/transaction-number";

    private String ambilNoTransaksi(String taskId) {
        try {
            HttpHeaders h = new HttpHeaders();
            h.add("Authorization", "Bearer " + api.TokenSatuSehat());
            String body = api.getRest().exchange(link + "/Task/" + taskId, HttpMethod.GET,
                new HttpEntity<>(h), String.class).getBody();
            JsonNode ident = mapper.readTree(body).path("identifier");
            for (JsonNode id : ident) {
                if (SYS_NO_TRANSAKSI.equals(id.path("system").asText())) {
                    return id.path("value").asText();
                }
            }
            // Cadangan untuk contoh lama yang memang memakai awalan TRX.
            for (JsonNode id : ident) {
                String nilai = id.path("value").asText();
                if (nilai.startsWith("TRX")) return nilai;
            }
        } catch (Exception e) { System.out.println("Notif baca Task : " + e); }
        return "";
    }

    private void simpanResource(String noRawat, String jenis, String id) {
        if (id == null || id.isEmpty()) return;
        Sequel.queryu2tf("replace into satu_sehat_akta_kelahiran_resource (no_rawat,jenis,id_resource,waktu) values (?,?,?,now())",
            3, new String[]{noRawat, jenis, id});
    }

    // ── Panel progres 9 tahap ────────────────────────────────────────────────
    private void siapkanProgres(String noRawat) {
        for (int i = 0; i < TAHAP.length; i++)
            Sequel.queryu2tf("insert ignore into satu_sehat_akta_kelahiran_progres (no_rawat,urut,sumber,tahap,status) values (?,?,?,?,'BELUM')",
                4, new String[]{noRawat, String.valueOf(i + 1), TAHAP[i][0], TAHAP[i][1]});
    }
    private void setProgres(String noRawat, int urut, String status, String ket) {
        Sequel.queryu2tf("update satu_sehat_akta_kelahiran_progres set status=?, keterangan=?, waktu=now() where no_rawat=? and urut=?",
            4, new String[]{status, ket, noRawat, String.valueOf(urut)});
    }

    private void tampilProgres() {
        if (TNoRawat.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih bayi dulu dari daftar."); return; }
        String noRawat = TNoRawat.getText();
        String trx = Sequel.cariIsi("select ifnull(no_transaksi,'') from satu_sehat_akta_kelahiran where no_rawat=?", noRawat);
        // Tahap 4-9 milik Dukcapil: ditanyakan langsung sebelum panelnya digambar,
        // jadi yang terlihat keadaan terkini, bukan salinan lokal yang basi.
        tarikStatusDukcapil(noRawat, trx);

        JDialog d = new JDialog(this, "Progres Permohonan Akta Kelahiran", true);
        d.setSize(860, 620); d.setLocationRelativeTo(this);
        JPanel isi = new JPanel(); isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));
        isi.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        isi.setBackground(Color.WHITE);

        JLabel judul = new JLabel("NO. TRANSAKSI   " + (trx == null || trx.isEmpty() ? "(belum terbit)" : trx));
        judul.setFont(new java.awt.Font("Tahoma", 1, 16));
        judul.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        isi.add(judul);
        isi.add(Box.createVerticalStrut(10));

        int berhasil = 0, gagal = 0, belum = 0;
        List<Object[]> baris = new ArrayList<>();
        try (PreparedStatement ps = koneksi.prepareStatement(
            "select urut,sumber,tahap,status,ifnull(keterangan,'') keterangan,ifnull(waktu,'') waktu " +
            "from satu_sehat_akta_kelahiran_progres where no_rawat=? order by urut")) {
            ps.setString(1, noRawat);
            try (ResultSet s = ps.executeQuery()) {
                while (s.next()) {
                    baris.add(new Object[]{s.getString("sumber"), s.getString("tahap"),
                        s.getString("status"), s.getString("keterangan"), s.getString("waktu")});
                    String st = s.getString("status");
                    if ("BERHASIL".equals(st)) berhasil++;
                    else if ("GAGAL".equals(st)) gagal++;
                    else belum++;
                }
            }
        } catch (Exception e) { System.out.println("Notif progres : " + e); }
        if (baris.isEmpty()) { for (String[] t : TAHAP) { baris.add(new Object[]{t[0], t[1], "BELUM", "Belum sampai tahap ini", ""}); belum++; } }

        int total = baris.size();
        JProgressBar bar = new JProgressBar(0, total);
        bar.setValue(berhasil); bar.setStringPainted(true);
        bar.setString(berhasil + " dari " + total + " proses selesai  (" + (total == 0 ? 0 : berhasil * 100 / total) + "%)");
        bar.setForeground(new Color(22, 160, 92));
        bar.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
        isi.add(bar);
        isi.add(Box.createVerticalStrut(6));
        JLabel ring = new JLabel("Berhasil : " + berhasil + "     Gagal : " + gagal + "     Belum diproses : " + belum);
        ring.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        isi.add(ring);
        isi.add(Box.createVerticalStrut(12));

        String sumberKini = "";
        for (Object[] b : baris) {
            if (!sumberKini.equals(b[0])) {
                sumberKini = (String) b[0];
                JLabel head = new JLabel(sumberKini.equals("SATUSEHAT") ? "SATU SEHAT" : "DUKCAPIL");
                head.setFont(new java.awt.Font("Tahoma", 1, 12));
                head.setForeground(new Color(22, 120, 80));
                head.setBorder(BorderFactory.createEmptyBorder(8, 0, 4, 0));
                head.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
                isi.add(head);
            }
            isi.add(barisProgres((String) b[1], (String) b[2], (String) b[3], (String) b[4]));
        }

        JButton tutup = new JButton("Tutup");
        tutup.addActionListener(e -> d.dispose());
        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bawah.setBackground(Color.WHITE); bawah.add(tutup);

        JScrollPane sc = new JScrollPane(isi);
        sc.getVerticalScrollBar().setUnitIncrement(16);
        d.setLayout(new BorderLayout());
        d.add(sc, BorderLayout.CENTER);
        d.add(bawah, BorderLayout.PAGE_END);
        d.setVisible(true);
    }

    private JPanel barisProgres(String tahap, String status, String ket, String waktu) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(235, 235, 235)),
            BorderFactory.createEmptyBorder(6, 2, 6, 2)));
        row.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));

        Color warna;
        if ("BERHASIL".equals(status)) warna = new Color(22, 160, 92);
        else if ("GAGAL".equals(status)) warna = new Color(200, 45, 45);
        else if ("MENUNGGU PROSES".equals(status)) warna = new Color(35, 110, 200);
        else warna = new Color(150, 150, 150);

        JLabel kiri = new JLabel("<html><b>" + tahap + "</b><br><span style='color:#777;font-size:9px'>"
            + (waktu == null || waktu.isEmpty() ? "" : waktu + " &nbsp; ") + ket + "</span></html>");
        JLabel kanan = new JLabel(status);
        kanan.setForeground(warna);
        kanan.setFont(new java.awt.Font("Tahoma", 1, 11));
        kanan.setHorizontalAlignment(SwingConstants.RIGHT);
        kanan.setPreferredSize(new Dimension(150, 20));
        row.add(kiri, BorderLayout.CENTER);
        row.add(kanan, BorderLayout.EAST);
        return row;
    }

    // ── Dipanggil frmUtama ───────────────────────────────────────────────────
    public void isCek() {
        // Pakai hak akses yang sudah ada: gerbang pengiriman SatuSehat (dipakai
        // juga oleh SatuSehatKirimTTE & SatuSehatKirimLaporanPersalinan).
        BtnKirim.setEnabled(akses.getsatu_sehat_kirim_encounter());
        BtnKirimKelahiran.setEnabled(akses.getsatu_sehat_kirim_encounter());
        BtnSklJasper.setEnabled(akses.getsatu_sehat_kirim_encounter());
        BtnPersetujuan.setEnabled(akses.getsatu_sehat_kirim_encounter());
        BtnSimpan.setEnabled(akses.getsatu_sehat_kirim_encounter());
        tampil();
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            SatuSehatKirimAktaKelahiran d = new SatuSehatKirimAktaKelahiran(null, true);
            d.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosing(java.awt.event.WindowEvent e) { System.exit(0); } });
            d.setVisible(true);
        });
    }
}
