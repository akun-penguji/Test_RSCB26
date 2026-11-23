package fungsi;

/**
 * Pusat informasi versi aplikasi Khanza fork RSUD Cabangbungin.
 *
 * <p>Saat menambah fitur / fix, naikkan {@link #NOMOR} dan tambahkan entry
 * baru di paling ATAS string {@link #FITUR} (format: lihat contoh di bawah).
 *
 * <p>Konvensi versi: {@code yyyy.MM.dd-rscb} — tanggal release.
 * Kalau ada beberapa release di hari yang sama, tambahkan suffix
 * {@code -2}, {@code -3}, dst.
 */
public class Versi {

    /** Nomor versi aplikasi. UBAH SETIAP RELEASE. */
    public static final String NOMOR = "2026.10.10-rscb-45";

    /** Nama brand aplikasi (tampil di login / about). */
    public static final String NAMA = "SIMRS Khanza — RSUD Cabangbungin";

    /** Pengembang / fork maintainer. */
    public static final String DEVELOPER = "Dokter Tekno (Salim Mulyana) · Fork dari Khanza.Soft Media";

    /**
     * Riwayat fitur per versi. Format markdown-style sederhana.
     * Tambahkan entry baru di PALING ATAS supaya versi terbaru di urutan pertama.
     */
    public static final String FITUR =
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.10.10-rscb-45  (2026-10-10)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Keamanan Login (frmUtama):\n" +
        "   - Visual Image Captcha: 5 karakter acak dengan distorsi grafis,\n" +
        "     rotasi, dan noise garis/titik (100% native Java AWT, offline).\n" +
        "   - Lock Out Login Temporer: proteksi penguncian otomatis selama\n" +
        "     60 detik setelah 3 kali gagal percobaan login (salah password / captcha).\n" +
        " • Booking Operasi (DlgBookingOperasi):\n" +
        "   - Perapihan menu sidebar terstruktur per alur pelayanan OK.\n" +
        "   - Sembunyikan form generic yang kosong & bukan tabel RSCB.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.10.05-rscb-44  (2026-10-05)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Laporan RL 3.19 PPRA (DlgRL319PPRA):\n" +
        "   - RL 3.19.2: Pasien Rawat Inap dipetakan otomatis per spesialis\n" +
        "     (Penyakit Dalam, Bedah, Anak, Kandungan, ICU, Paru, Lain-lain)\n" +
        "     berdasarkan ruang ICU dan DPJP spesialis ranap.\n" +
        "   - RL 3.19.3 (Kuantitas DDD): ditambahkan dropdown filter spesialis\n" +
        "     sehingga penyebut (hari rawat/kunjungan), pembilang DDD,\n" +
        "     dan cakupan dapat dilihat per spesialis maupun total RS.\n" +
        "   - Rawat Jalan RL 3.19.2 & RL 3.19.3: filter eksklusi poli IGD\n" +
        "     saat jenis perawatan Rawat Jalan dipilih (IGD terpisah).\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.10.03-rscb-43  (2026-10-03)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Akta Kelahiran SATUSEHAT — alamat keluarga ikut dikirim:\n" +
        "   - RelatedPerson sekarang membawa `address` dan `communication`\n" +
        "     untuk ibu, ayah, pelapor dan saksi. Keduanya ada di contoh\n" +
        "     payload resmi dan formulir SIAK pada lampiran Surat Edaran\n" +
        "     Ditjen Dukcapil 400.8.2.2/10077/DUKCAPIL (7 Sep 2026)\n" +
        "     menampilkan Alamat + RT/RW + wilayah untuk keempatnya.\n" +
        "     Tanpa itu operator Dukcapil harus mengetik ulang.\n" +
        "   - Form Kirim Akta: 3 isian alamat baru. Alamat ibu TERISI\n" +
        "     OTOMATIS dari rekam medis ibu (lewat bayi_ibu_mapping),\n" +
        "     lengkap dengan kelurahan & kecamatan. Ayah memakai alamat\n" +
        "     ibu karena satu Kartu Keluarga. Pelapor & saksi defaultnya\n" +
        "     menyalin alamat ibu dan tinggal ditimpa kalau beda rumah.\n" +
        "     Yang sudah pernah disimpan tidak ditimpa isian otomatis.\n" +
        "   - Diuji ke staging: POST RelatedPerson balas 201 dan kedua\n" +
        "     bidang tersimpan utuh.\n" +
        "   - Butuh SQL sql/akta_kelahiran_alamat.sql (6 kolom baru).\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.10.03-rscb-42  (2026-10-03)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Nilai Kritis Lab — perbaikan alarm palsu (laporan petugas lab):\n" +
        "   - Trombosit 357,000 ditandai kritis padahal normal. Sebabnya\n" +
        "     NilaiRujukanResolver.parseNum membaca koma sebagai pemisah\n" +
        "     desimal, jadi \"357,000\" dibaca 357 — di bawah ambang 20.000.\n" +
        "     Alat lab RSCB memakai koma sebagai pemisah RIBUAN dan titik\n" +
        "     sebagai desimal, jadi aturan bacanya dibalik dan ditambahi\n" +
        "     dukungan tanda pembanding (\"< 20000\", \"≥150\").\n" +
        "     Akibat bug ini 31 dari 32 catatan audit 03-10-2026 palsu.\n" +
        "   - Ambang kritis diselaraskan ke daftar resmi yang ditempel di\n" +
        "     laboratorium (SQL rscb-nilai_kritis_kertas_putih.sql):\n" +
        "     Trombosit atas 1.000.000 → 800.000, Lekosit bawah 2.000 →\n" +
        "     1.000, Natrium bawah 120 → 115, Glukosa dewasa 45/500 →\n" +
        "     40/400, Glukosa neonatus atas 300 → 325, APTT 70 → 150.\n" +
        "     Ditambah kelompok umur Neonatus/Bayi/Balita/Anak/Remaja yang\n" +
        "     sebelumnya tidak punya ambang sama sekali.\n" +
        "     Hematokrit, Kreatinin, Ureum, PT, pCO2 dan pO2 dicabut karena\n" +
        "     tidak tercantum di daftar resmi RS.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.10.02-rscb-41  (2026-10-02)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Modul Data Kelahiran SATUSEHAT (akta kelahiran) — tahap 2 s.d. 6:\n" +
        "   - SatuSehatDataKelahiranHelper (baru): mesin bersama tahap 2-4.\n" +
        "     Encounter ibu, Patient bayi via NIK IBU, EpisodeOfCare Neonate,\n" +
        "     Encounter bayi, 8 Observation. Semua data ditarik OTOMATIS dari\n" +
        "     pasien_bayi/pasien/bayi_ibu_mapping/kamar_inap — tidak ada yang\n" +
        "     diketik ulang petugas.\n" +
        "   - DlgIKBBayi: popup \"Kirim Data Kelahiran Satu Sehat\".\n" +
        "   - SatuSehatKirimAktaKelahiran (baru): daftar kerja dari pasien_bayi,\n" +
        "     isian keluarga & berkas, tahap 5-6, panel progres 9 tahap.\n" +
        "   - Tombol Progres kini MENANYAKAN status ke Dukcapil\n" +
        "     (GET /v1/data-kelahiran-rme/status), bukan sekadar baca lokal.\n" +
        "   - SKL diambil langsung dari Jasper jadi JPEG (pilih bentuk 1/2/3),\n" +
        "     tidak perlu dicetak lalu dipindai.\n" +
        "   - rptFormulirPersetujuanKelahiran (baru): lembar persetujuan\n" +
        "     pemohon, terisi data yang benar-benar akan dikirim.\n" +
        "   - Lampiran: PDF diterima lalu diubah sendiri jadi JPEG per halaman;\n" +
        "     beberapa halaman boleh dipilih sekaligus dan urutannya dirapikan.\n" +
        "   - Penanda \"sudah terkirim\" = ADANYA ID, jadi aman ditekan berulang.\n" +
        "   Aturan SatuSehat yang tidak tertulis di dokumentasi dan ditemukan\n" +
        "   dari galat: Observation.issued wajib; Encounter.location wajib;\n" +
        "   penolong persalinan subjeknya IBU; EpisodeOfCare.managingOrganization\n" +
        "   wajib (dan PUT-nya ditolak, harus benar sejak POST); Task wajib bawa\n" +
        "   meta.tag creator atau nomor transaksi TIDAK PERNAH terbit; nomor\n" +
        "   transaksi hanya ada di balasan POST dan harus ditulis balik sendiri.\n" +
        " • Permintaan Resep Pulang: tab RACIKAN (baru).\n" +
        "   - Tabel header racikan + detail obat, tambah/hapus baris, metode\n" +
        "     racik, hitung kandungan ragam angka maupun persen.\n" +
        "   - Kotak Cari mengikuti tab yang sedang dibuka; baris racikan pertama\n" +
        "     disiapkan sendiri supaya daftar obat tidak diam tanpa sebab.\n" +
        "   - Simpan: angka kosong dikirim 0 (STRICT mode menolak string kosong\n" +
        "     di kolom DOUBLE dan membatalkan seluruh simpan), tab racikan ikut\n" +
        "     dibersihkan, dan muncul konfirmasi nomor permintaan.\n" +
        "   - Tabel baru: permintaan_resep_pulang_racikan + _detail.\n" +
        " • Input Resep Pulang: tab RACIKAN (baru) + penagihannya.\n" +
        "   Obat penyusun racikan ditulis ke resep_pulang yang sama dengan obat\n" +
        "   biasa, jadi stok, Trackobat, penagihan dan akuntansi ikut jalan.\n" +
        "   Obat yang sudah ditulis dari tab Obat dilewati supaya tidak bentrok\n" +
        "   kunci dan tidak tertagih dua kali. Isi tab dimuat begitu diklik.\n" +
        " • Catatan Keperawatan Rawat Jalan: tombol \"Ambil Pemeriksaan\" —\n" +
        "   tarik TTV & keluhan dari pemeriksaan_ralan lewat fungsi.SoapieRalan.\n" +
        "   Baris perawat diutamakan, cadangannya baris dokter. TTV ditimpa,\n" +
        "   Uraian TIDAK ditimpa — ketikan petugas tidak boleh hilang.\n" +
        " • Perbaikan: SatuSehatKirimEpisodeOfCare memakai kode tipe \"uncoded\"\n" +
        "   yang tidak ada di CodeSystem, sehingga layar itu SELALU 400.\n" +
        "   Diperbaiki jadi ANC sesuai sumbernya (tabel data_anc).\n" +
        " • Lampiran DocumentReference: beberapa halaman dalam SATU resource,\n" +
        "   dan JFileChooser diurutkan sendiri (urutan klik bukan urutan halaman).\n" +
        " v2026.09.24-rscb-40  (2026-09-24)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • DlgCariPeriksaLab: filter 'Hanya Nilai Kritis' (checkbox merah) di\n" +
        "   tab Item Pemeriksaan — menampilkan hanya hasil yang tercatat di\n" +
        "   audit_nilai_kritis (PAP 3), spt di rsud-tualang.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.24-rscb-39  (2026-09-24)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Nilai Kritis Lab Tahap 3 — worklist & laporan PAP 3:\n" +
        "   - DlgDaftarNilaiKritis (baru): monitoring audit_nilai_kritis dgn\n" +
        "     filter tanggal + status, tandai PENDING merah, tindak lanjut\n" +
        "     pelaporan TBaK, tandai 'Tidak Perlu', cetak rekap.\n" +
        "   - Menu 'Nilai Kritis Lab' di frmUtama (grup Laboratorium, akses\n" +
        "     periksa_lab).\n" +
        "   - Jasper rptLaporanNilaiKritis (rekap audit + pelaporan) untuk\n" +
        "     bukti surveior.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.24-rscb-38  (2026-09-24)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Nilai Kritis Lab Tahap 2 (KARS PAP 3) — deteksi otomatis saat\n" +
        "   input hasil di DlgPeriksaLaboratorium: tiap hasil numerik\n" +
        "   dibandingkan thresholds kritis (NilaiRujukanResolver per jk+umur).\n" +
        "   Yang kritis di-log ke audit_nilai_kritis (PENDING) + peringatan\n" +
        "   pop-up, lalu tawarkan pelaporan TBaK.\n" +
        " • DlgLaporKritis (baru): form pelaporan TBaK (penerima, tipe,\n" +
        "   metode komunikasi, read-back, instruksi, kondisi) — simpan ke\n" +
        "   lapor_kritis_rm & update status audit jadi TERNOTIFIKASI.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.24-rscb-37  (2026-09-24)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • CPPT/SOAP Ranap (DlgRawatInap) & Ralan (DlgRawatJalan): field Alergi\n" +
        "   diubah dari textbox bebas menjadi combobox 'Tidak Ada' / 'Ada'.\n" +
        "   Saat 'Ada' dipilih, muncul textbox rincian alergen (default 'Tidak\n" +
        "   Ada' agar tak pernah kosong — temuan survey akreditasi). Selaras\n" +
        "   dengan e-Nakes/e-Dokter web.\n" +
        " • Cari Permintaan Lab & Radiologi: kolom Prioritas menampilkan 'Cito'\n" +
        "   berwarna merah tebal agar mudah terlihat.\n" +
        " • Sinkron jasper rptPeriksaRadiologi (1/2/3) ke KhanzaAPMRSJ/report\n" +
        "   (Indikasi Klinis dari rscb-36 sebelumnya belum ikut ter-copy).\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.23-rscb-27  (2026-09-23)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Cetakan Persetujuan Umum: perbaikan layout (blok foto/tanda tangan\n" +
        "   Float agar tak overlap teks) & pilihan PELEPASAN INFORMASI\n" +
        "   (memberikan/tidak memberikan) kini sesuai isian form (kolom baru\n" +
        "   pelepasan_informasi). Query +pelepasan_informasi.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.23-rscb-26  (2026-09-23)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Cetakan Persetujuan Umum (jasper rptSuratPersetujuanUmum) disesuaikan\n" +
        "   dgn form PDF resmi RM.KEP.08.004: +seksi 7 PELAYANAN ROHANI (agama +\n" +
        "   pilihan bersedia/tidak), checklist Nilai&Keyakinan & Privasi kini Ya/Tidak\n" +
        "   penuh. Query +pasien.agama +pelayanan_rohani. Kolom baru pelayanan_rohani\n" +
        "   di surat_persetujuan_umum_tambahan (diisi via web). QR e-sign jadi teks.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.23-rscb-25  (2026-09-23)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Sign Out: tambah pengisian JUMLAH JARUM (kolom baru jumlah_jarum)\n" +
        "   di e-Nakes maupun Java, sejajar kassa/instrumen.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.23-rscb-24  (2026-09-23)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • 5 form periop diselaraskan ke tabel e-Nakes (form Java punya field\n" +
        "   lebih -> INSERT gagal). Field ekstra dipangkas ikut tabel:\n" +
        "   - Edukasi Anestesi (15->12): buang hubungan + tgl_lahir/jk terpisah\n" +
        "     (pakai tgl_lahir_jk gabungan), perbaiki tampil/getData/cetak.\n" +
        "   - SIA General (25->23) & SIA Regional (26->23): buang field ekstra.\n" +
        "   - Monitoring Fisiologis Anestesi (15->14): buang kd_dokter.\n" +
        "   - Asesmen Pra Anestesi: placeholder INSERT 60->62 '?' (typo, N=62).\n" +
        "   Semua kini bisa simpan & data e-Nakes tampil di desktop.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.23-rscb-23  (2026-09-23)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Status Anestesi (RMStatusAnestesi): perbaiki 2 sub-tab yang GAGAL\n" +
        "   simpan — Monitoring & Catatan. Placeholder INSERT masih menyertakan\n" +
        "   kolom id/timestamp (null,...,null,null) padahal tabel sudah di-slim\n" +
        "   (catatan_anestesi_monitoring 21 kolom, catatan_anestesi_catatan 10)\n" +
        "   -> INSERT kelebihan 3 posisi -> 1136. Placeholder disamakan jumlah\n" +
        "   kolom. 12 sub-tab lain diverifikasi sudah selaras.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.23-rscb-22  (2026-09-23)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Sign Out (Sebelum Menutup Luka) DITULIS ULANG agar SATU skema\n" +
        "   dengan form e-Nakes (tabel signout_sebelum_menutup_luka 14 kolom):\n" +
        "   simpan dari desktop kini berfungsi (dulu INSERT 12 kolom -> gagal,\n" +
        "   kurang jumlah_kasa/jumlah_instrumen), + field Jumlah Kasa/Instrumen\n" +
        "   & cetak. Data e-Nakes & desktop saling tampil.\n" +
        " • Sign In (Sebelum Anestesi) diverifikasi SUDAH selaras (13 kolom) —\n" +
        "   tidak ada perubahan.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.23-rscb-21  (2026-09-23)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Time Out (Sebelum Insisi) DITULIS ULANG agar SATU skema dengan\n" +
        "   form e-Nakes (tabel timeout_sebelum_insisi 24 kolom): simpan dari\n" +
        "   Khanza desktop kini berfungsi (dulu INSERT 16 kolom -> gagal), &\n" +
        "   cetak diperbaiki. Data dari e-Nakes & desktop saling tampil.\n" +
        " • Ditambah pengisian JUMLAH KASSA / INSTRUMEN / JARUM di Time Out\n" +
        "   (kolom baru jumlah_jarum) — di e-Nakes maupun Java.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.23-rscb-20  (2026-09-23)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Form desktop baru: Hitung Kassa & Instrumen Bedah\n" +
        "   (RMHitungKassaInstrumen, tabel hitung_kassa_instrumen_rscb) —\n" +
        "   satu tabel dgn form e-Nakes shg data saling tampil. Diwiring ke\n" +
        "   menu IBS di DlgKamarInap, DlgKasirRalan, DlgIGD, DlgReg.\n" +
        " • Laporan Operasi: e-Nakes kini menulis ke laporan_operasi_casemix\n" +
        "   (tabel RMLaporanOperasi) — perubahan sisi web, agar data operasi\n" +
        "   dari e-Nakes tampil di Khanza desktop.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.23-rscb-19  (2026-09-23)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Prioritas CITO/RUTIN diperluas:\n" +
        "   - DlgPermintaanRadiologi: checkbox CITO (tabel companion\n" +
        "     permintaan_radiologi_prioritas, TIDAK mengubah skema\n" +
        "     permintaan_radiologi shg insert posisional aman).\n" +
        "   - Kolom 'Prioritas' di DlgCariPermintaanLab &\n" +
        "     DlgCariPermintaanRadiologi (LEFT JOIN companion, default Rutin).\n" +
        "   - Perbaikan: kolom Indikasi Klinis kini terisi di worklist\n" +
        "     ranap-detail lab & radiologi (sebelumnya kosong).\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.18-rscb-18  (2026-09-23)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Permintaan Laboratorium: checkbox CITO di DlgPermintaanLaboratorium.\n" +
        "   Ditandai ke tabel companion permintaan_lab_prioritas (Cito/Rutin)\n" +
        "   tanpa mengubah skema permintaan_lab. Dashboard waktu tunggu lab\n" +
        "   (reguler vs cito) di simrscb.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.18-rscb-17  (2026-09-23)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Nilai Rujukan (normal) & Nilai Kritis lab (port dari rsud-tualang):\n" +
        "   tabel master_kelompok_umur, template_lab_nilai_rujukan (+kritis),\n" +
        "   audit_nilai_kritis, lapor_kritis_rm. NilaiRujukanResolver +\n" +
        "   NilaiKritisAudit + DlgKelompokUmur. Panel 'Nilai Rujukan per\n" +
        "   Kelompok Umur' di DlgTemplateLaboratorium (UI widget Khanza).\n" +
        "   (Tahap lanjut: deteksi kritis saat input hasil + lapor kritis.)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.18-rscb-16  (2026-09-23)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Permintaan Radiologi: field INDIKASI KLINIS terpisah (wajib) di\n" +
        "   DlgPermintaanRadiologi + kolom Indikasi Klinis di tabel\n" +
        "   DlgCariPermintaanRadiologi. Insert dinaikkan 12->13 kolom.\n" +
        "   WAJIB: jalankan ALTER 'ADD COLUMN indikasi_klinis' ke\n" +
        "   permintaan_radiologi BERSAMAAN dgn distribusi JAR ini (semua\n" +
        "   klien), krn insert posisional (lihat catatan rilis).\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.18-rscb-15  (2026-09-21)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Hak & Kewajiban Pasien diperbarui ke regulasi terbaru:\n" +
        "   UU No. 17 Tahun 2023 (Pasal 276 hak & 277 kewajiban) jo.\n" +
        "   PP No. 28 Tahun 2024 — di form Java, jasper cetak, & web.\n" +
        " • Menu 'Perencanaan Pemulangan' ditambahkan di DlgReg, DlgIGD,\n" +
        "   dan DlgKasirRalan (buka RMPerencanaanPemulangan pasien terpilih).\n" +
        " • 'Cetak Resep Awal' (DlgDaftarPermintaanResep) kini memakai format\n" +
        "   rptResepTelaah (resep + Pengkajian + Telaah Obat), sama seperti\n" +
        "   di DlgResepObat.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.18-rscb-14  (2026-09-21)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • MPP Pengaturan Alur Pasien (AKP 2.1): 8 kolom baru di\n" +
        "   mpp_evaluasi (koordinasi TT, percepatan discharge, penempatan\n" +
        "   transit, koordinasi transfer, pemantauan penumpukan, koordinasi\n" +
        "   penunjang, hambatan/tindak lanjut alur) — diisi via e-Nakes tab\n" +
        "   'MPP — Evaluasi'. Insert Java FormA dinaikkan 10→18 kolom\n" +
        "   (default aman). Dashboard 'Pengelolaan Alur Pasien' di web\n" +
        "   (ketersediaan TT/BOR/boarding IGD, read-only).\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.18-rscb-13  (2026-09-21)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Surat Pengantar Pulang kini menampilkan TANDA TANGAN PASIEN\n" +
        "   (di-capture di e-Nakes tab 'TTD Pengantar Pulang', disimpan di\n" +
        "   webapps + tabel surat_pengantar_pulang_ttd). Tampil di cetak &\n" +
        "   kirim WA. Param ttdurl via HOSTHYBRIDWEB.\n" +        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.18-rscb-12  (2026-09-21)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Cetak Penerimaan & Orientasi Ranap kini menampilkan BUKTI\n" +
        "   penerimaan: foto pasien/keluarga + tanda tangan penerima\n" +
        "   (di-capture di e-Nakes web, disimpan di webapps + tabel\n" +
        "   orientasi_pasien_ranap_bukti). URL via HOSTHYBRIDWEB.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.18-rscb-11  (2026-09-21)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Form Penerimaan & Orientasi Ranap: field Kode + Nama Petugas\n" +
        "   dengan tombol cari (DlgCariPetugas) — nip tersimpan = petugas\n" +
        "   terpilih (default user login).\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.18-rscb-10  (2026-09-21)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Label menu di DlgKamarInap disamakan → 'Penerimaan & Orientasi\n" +
        "   Pasien Baru Rawat Inap' (sesuai isi form gabungan).\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.18-rscb-9  (2026-09-21)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Form Orientasi digabung jadi 'Penerimaan & Orientasi Pasien\n" +
        "   Baru Rawat Inap' — tambah section Penerimaan (cara masuk/datang,\n" +
        "   keadaan umum, diagnosa masuk, DPJP, penanggung jawab, alergi,\n" +
        "   general consent, barang bawaan) + 16 checklist orientasi.\n" +
        "   Tabel orientasi_pasien_ranap_rscb → 35 kolom, jasper diperbarui.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.18-rscb-8  (2026-09-21)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Form baru 'Orientasi Pasien Baru Rawat Inap' (AKP 2d) —\n" +
        "   RMOrientasiPasienRanap, 16 checklist orientasi + TTD + cetak\n" +
        "   (rptOrientasiPasienRanap). Menu di DlgKamarInap. Tabel bersama\n" +
        "   orientasi_pasien_ranap_rscb (dipakai juga oleh e-Nakes simrscb).\n" +
        " • FIX: hapus duplikat menu 'Askep SDKI/SLKI/SIKI' di DlgKamarInap\n" +
        "   (rscb-7 keliru menambah, padahal sudah ada).\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.18-rscb-7  (2026-09-21)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Menu 'Askep SDKI/SLKI/SIKI' (RMAskep3S) ditambah di DlgKamarInap\n" +
        "   (rawat inap) — sebelumnya hanya di DlgIGD & menu utama. Mendukung\n" +
        "   AKP 2c: rencana asuhan (SIKI) + hasil yang diharapkan (luaran SLKI,\n" +
        "   skala & waktu target) diisi saat ranap.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.18-rscb-6  (2026-09-21)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Menu 'Skrining Rawat Inap' (AKP 1.2a: penetapan pelayanan\n" +
        "   preventif/kuratif/paliatif/rehabilitatif/intensif) ditambah di\n" +
        "   DlgReg & DlgKamarInap (sebelumnya sudah ada di DlgIGD &\n" +
        "   DlgKasirRalan).\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.18-rscb-5  (2026-09-20)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • FIX lanjutan NPE: di RMPenilaianAwalMedisRalanKandungan tombol\n" +
        "   BtnAmbilTriase dipakai (setText) SEBELUM di-instansiasi (urutan\n" +
        "   setText mendahului setIcon) → NPE saat form dibuka via DlgIGD.\n" +
        "   Instansiasi dipindah ke atas bersama komponen lain. Sudah dicek\n" +
        "   31 form berTriase/SOAPIE, semua urutan new<pemakaian.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.18-rscb-4  (2026-09-18)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Cetak Formulir SRQ-29 disesuaikan dgn form fisik resmi:\n" +
        "   judul 'Self-Reporting Questionnare-29', blok identitas\n" +
        "   (Tgl Pemeriksaan/NIK/Nama/JK/Tempat,Tgl Lahir/No.RM),\n" +
        "   paragraf Petunjuk lengkap, kotak Penilaian | Petugas Pemeriksa\n" +
        "   dengan QR code TTD verifikasi (seperti report lain).\n" +
        "   Query cetak +no_ktp,+tmp_lahir,+nama verifikator.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.18-rscb-3  (2026-09-18)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • FIX NPE: tombol 'Ambil data Triase' & 'Ambil SOAPIE' tak pernah\n" +
        "   di-instansiasi (new) di 7 form Penilaian Awal Medis → form gagal\n" +
        "   dibuka (NullPointerException di initComponents). Termasuk form ICU\n" +
        "   (penyebab 'assesmen medis ICU belum bisa kebuka').\n" +
        "   Form terdampak: RanapDewasa, RanapICU, RanapAnak, RanapJantung,\n" +
        "   RanapKandungan, RanapPsikiatrik, RalanKandungan.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.18-rscb-2  (2026-09-18)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Status Anestesi: sub-tab 'Monitoring' (TTV) di Blokade Regional.\n" +
        " • Menu 'Monitoring Anestesi Diluar Kamar Operasi' (RM.IBS.11.011)\n" +
        "   ditambah di RM Operasi DlgKasirRalan, DlgKamarInap, DlgIGD.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.18-rscb-1  (2026-09-18)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Skrining Kesehatan Jiwa SRQ-29 (RMSkriningSRQ29): 29 pertanyaan\n" +
        "   Ya/Tidak + skor otomatis (SRQ-20 cutoff>=6 / zat / psikotik / PTSD).\n" +
        "   Input pasien via e-Pasien tampil status 'Belum Verifikasi', petugas\n" +
        "   memverifikasi di Java. Menu di DlgKasirRalan + cetak jasper.\n" +
        " • Pengkajian Keperawatan ICU: skala nyeri +CPOT +BPS.\n" +
        " • Status Anestesi (Keadaan Selama Operasi): opsi Airway +'Nasal kanul'.\n" +
        " • SOAP/CPPT Ranap: Pengkajian Keperawatan ICU kini membuka simpan SOAP\n" +
        "   (perawat ICU tak perlu isi Awal Keperawatan Umum lagi).\n" +
        " • PPRA RL 3.19.2: opsi 'IGD' di Jenis Perawatan + IGD dipisah (via\n" +
        "   kolom 'sebutkan' portal); perbaikan pemetaan Rawat Inap/Jalan.\n" +
        " • Auto-logout idle default 3 jam (IDLETIMEOUTMENIT=180 di database.xml).\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.16-rscb-3  (2026-09-16)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Awal Medis Ranap & IGD Kebidanan: tombol 'Ambil data Triase'\n" +
        "   (TTV + keluhan dari data_triase_igd) & 'Ambil SOAPIE' (isian\n" +
        "   dokter dari pemeriksaan Ralan/IGD) — agar TTV tak ditulis ulang.\n" +
        "   Ranap Dewasa/Kandungan/ICU/Psikiatri/Anak/Jantung + IGD Kebidanan\n" +
        "   (RMPenilaianAwalMedisRalanKandungan).\n" +
        " • Menu baru 'Daftar Singkatan' di panel atas frmUtama: viewer +\n" +
        "   pencarian singkatan/simbol RM (tabel master_singkatan_rscb, sesuai\n" +
        "   SK Direktur No.400.7.31/112/RSUD-CB/2026). Baris 'dilarang' merah.\n" +
        " • Toolbar panel atas frmUtama: tombol 'Lap RL' & 'Lapor IT' dihapus.\n" +
        " • Form Skrining Rawat Inap (RM.KEP.08.074.2026) di DlgIGD &\n" +
        "   DlgKasirRalan (skrining_rawat_inap_rscb).\n" +
        " • Pra Induksi (RMAsesmenAwalPraInduksiRscb): Dokter & Penata Anestesi\n" +
        "   dipilih via DlgCariPegawai (FK kd_dokter -> pegawai.nik).\n" +
        " • Anestesi Sedasi (RMCatatanSedasiAnestesiRscb): jam Monitoring &\n" +
        "   Monitoring Pasca kini bisa diedit.\n" +
        " • Asesmen Kep Ralan (Ralan/Geriatri/Psikiatri): default Kualitas\n" +
        "   nyeri dikosongkan (bukan 'Seperti Tertusuk').\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.11-rscb-2  (2026-09-14)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Konsul: tombol konsul diarahkan ke form 'Permintaan Konsultasi\n" +
        "   Medik' (tabel konsultasi_medik/jawaban_konsultasi_medik) di\n" +
        "   DlgRawatInap, DlgKasirRalan & DlgKasirRalan01 (samakan dgn\n" +
        "   DlgRawatJalan). Form lama (KonsultasiInternal/KonsultasiJawab/\n" +
        "   LembarKonsul) yg pakai tabel konsultasi_lembar/konsultasi_jawab\n" +
        "   (tidak ada di DB) dilepas -> jawaban konsul tak lagi 'hilang'.\n" +
        "   Notifikasi login pesanKonsul() ke dokter dikonsuli kini jalan.\n" +
        " • Surat Kontrol BPJS: tidak boleh dibuat utk tgl kontrol hari ini/\n" +
        "   sebelumnya (BPJSSuratKontrol); anjungan APM (DlgCekSKDPKontrol)\n" +
        "   tolak surat kontrol yg dibuat di hari yg sama dgn jadwal.\n" +
        " • Booking Operasi: sembunyikan form pra-anestesi lama yg tak dipakai\n" +
        "   ('Penilaian Pre Anestesi' & 'Asesmen Pra Anastesi/ Sedasi');\n" +
        "   sisakan 'Penilaian Pre Anestesi RSCB' (asesmen_pra_anestesi_rscb,\n" +
        "   sinkron dgn tab Asesmen Pra Anestesi di E-Dokter).\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.11-rscb  (2026-09-11)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • E-Klaim INA-CBG (INACBGDataIDRG): perbaikan tarif RS milyaran —\n" +
        "   nilai tarif berdesimal (mis. 4294967.295) yang masuk TextBox\n" +
        "   digit-only membengkak ~1000x. Kini semua 15 field tarif\n" +
        "   dibulatkan ke rupiah bulat (helper bulat()) sebelum diisi.\n" +
        " • E-Klaim: pilih SEP sesuai jenis kunjungan — klaim ranap kini\n" +
        "   memakai SEP ranap (jnspelayanan 1), bukan SEP ralan, saat satu\n" +
        "   no_rawat punya 2 SEP (IGD ralan + ranap).\n" +
        " • Surat Kontrol BPJS (BPJSSuratKontrol): tidak boleh dibuat untuk\n" +
        "   tanggal kontrol hari ini/sebelumnya — harus tanggal setelah ini.\n" +
        " • Anjungan APM Pendaftaran BPJS Kontrol (DlgCekSKDPKontrol): tolak\n" +
        "   surat kontrol yang dibuat di hari yang sama dgn jadwal kontrol\n" +
        "   (tgl_surat >= tgl_rencana) — surat harus dibuat sebelumnya.\n" +
        " • Kriteria Masuk ICU (RM.KEP.08.052.2022): Prioritas 1-4 (2022) +\n" +
        "   Kriteria Fisiologis (lama); skor EWS/qSofa/APACHE dihapus; jasper\n" +
        "   diperbarui.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.03-rscb  (2026-09-03)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Fix EDIT Transfer Antar Ruang (RMTransferInternalRs): tombol\n" +
        "   ganti gagal 'No value specified for parameter 52' — jumlah\n" +
        "   param 51->53 (51 kolom SET + 2 WHERE no_rawat & tanggal_masuk).\n" +
        " • Transfer: kolom obat_infus/injeksi/oral dilebarkan ke TEXT\n" +
        "   (dulu varchar 100/255) supaya daftar obat panjang tidak terpotong.\n" +
        " • Permintaan Lab (permintaan usul surveyor): tambah field INDIKASI\n" +
        "   KLINIS terpisah dari Diagnosa Klinis, di DlgPermintaanLaboratorium\n" +
        "   (lab/PA/MB) + reprint DlgCariPermintaanLab + cetakan formulir.\n" +
        "   Kolom indikasi_klinis ditambahkan ke permintaan_lab/labpa/labmb.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.01-rscb-2  (2026-09-01)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Asesmen Pra Bedah: perbaikan EDIT — klik baris memuat semua\n" +
        "   field (dulu rusak krn bentrok ps/rs dgn booking operasi).\n" +
        " • PHQ-9: Kesimpulan tidak lagi otomatis; diisi manual oleh petugas.\n" +
        " • PRMRJ: hanya kunjungan dokter SPESIALIS (kecuali Umum/-),\n" +
        "   kecualikan poli penunjang (Lab/Radiologi/Apotik); hitung DPJP\n" +
        "   ikut aturan ini.\n" +
        " • Catatan Keperawatan: SpO2 (Ranap); Ralan disamakan (full TTV).\n" +
        " • Menu Check List Kriteria Masuk/Keluar HCU/ICU/NICU/PICU di\n" +
        "   DlgKamarInap, DlgIGD & DlgKasirRalan.\n" +
        " • Transfer Antar Ruang: SEMUA modul (frmUtama, Rawat Jalan/Inap,\n" +
        "   IGD, Kasir Ralan, Booking Operasi) disamakan ke RMTransferInternalRs;\n" +
        "   hapus menu ganda & form lama RMTransferPasienAntarRuang tak dipakai lagi.\n" +
        " • Fix Jam Validasi resep salah (variabel now stale) di DlgResepObat.\n" +
        " • Monitor alarm apotek (Daftar Permintaan Resep) cegah auto-logout.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.09.01-rscb  (2026-09-01)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Profil Ringkas Medis RJ: menu di frmUtama (alat review\n" +
        "   pasien rawat jalan dengan >=3 DPJP), hak akses tindakan_ralan.\n" +
        " • Catatan Keperawatan: tambah SpO2 (Ranap); Ralan disamakan\n" +
        "   persis Ranap (Tensi/Nadi/Respirasi/Suhu/SpO2).\n" +
        " • Menu Transfer Antar Ruang & Transfer Pasien Keluar RS di\n" +
        "   DlgIGD & DlgKasirRalan.\n" +
        " • Check List Kriteria Masuk/Keluar NICU & PICU di DlgKamarInap;\n" +
        "   submenu 'RM HCU, ICU, NICU & PICU' (8 item) di DlgIGD &\n" +
        "   DlgKasirRalan.\n" +
        " • Monitor alarm apotek (Daftar Permintaan Resep) tidak lagi\n" +
        "   kena auto-logout idle 10 menit selama form aktif.\n" +
        " • Rapikan paket: RMChecklistKriteriaKeluarICU -> rekammedis.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.08.29-rscb  (2026-08-29)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Batas 2x24 jam ubah/hapus data RM (PMK 24/2022): hak akses\n" +
        "   custom 'edit_lewat_batas_waktu' via tombol Akses+ di DlgUser\n" +
        "   (tabel simrscb_user_akses); 64 form RM pakai akses.boleh-\n" +
        "   LewatBatasWaktu(). Admin Utama tetap bisa melewati.\n" +
        " • Tombol 'Ambil SOAPIE' di 23 form Penilaian Awal Medis &\n" +
        "   Keperawatan Ralan + versi IGD: tarik SOAPIE + TTV dari\n" +
        "   pemeriksaan_ralan (dokter/perawat) ke form.\n" +
        " • Ranap: wajib Penilaian Awal (medis/keperawatan) sebelum\n" +
        "   simpan pemeriksaan_ranap (SOAP).\n" +
        " • Form Monitoring Peralatan Single Use yang di Reuse (menu\n" +
        "   DlgBookingOperasi + e-Nakes) & menu Awal Kebidanan di IGD.\n" +
        " • Anestesi Sedasi RSCB: menu DlgBookingOperasi + fix simpan.\n" +
        " • Asuhan Gizi Ranap: fix gagal simpan (kolom TEXT), label\n" +
        "   validasi, Implementasi bisa Enter, pisah Monitoring &\n" +
        "   Evaluasi.\n" +
        " • Penandaan Area Operasi: marking klik (lingkaran) sinkron\n" +
        "   dengan e-Nakes.\n" +
        " • e-Nakes: Askep KB dipisah Pra/Intra/Post-Op; tab Monitoring\n" +
        "   Alat Single Use; login selalu ke /enakes. e-Dokter: form\n" +
        "   Asesmen Pra Anestesi/Sedasi.\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.08.28-rscb  (2026-08-28)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Status Lokalis: penanda lokasi (LINGKARAN garis hitam,\n" +
        "   resolution-independent) bisa diklik pada 15 form penilaian\n" +
        "   awal medis (IGD, IGD Spesialis, IGD Psikiatri, Ralan\n" +
        "   Dewasa/Anak/Bedah/Orthopedi/THT/Kulit-Kelamin/Paru/Mata,\n" +
        "   Ranap Dewasa/Anak, Fisioterapi, Korban Kekerasan). Titik\n" +
        "   muncul juga di Riwayat Perawatan & hasil cetak.\n" +
        " • Checklist Keselamatan Operasi (Sign In sebelum anestesi):\n" +
        "   tambah item 'Peralatan khusus/implan (+jenis)' — disimpan\n" +
        "   di tabel samping (tak mengubah insert lama).\n" +
        " • Penandaan Lokasi Operasi: menu ditambah di sidebar kiri\n" +
        "   DlgBookingOperasi; gambar penanda dilengkapi ODONTOGRAM\n" +
        "   GIGI (FDI) sesuai RM.KEP.08.038; cetak diberi kolom TTD\n" +
        "   Pasien/Keluarga di samping e-sign Operator Bedah.\n" +
        " • DlgBookingOperasi: menu sidebar kiri kini bisa di-scroll.\n" +
        " • Build: perbaikan gagal build 'archive contains more than\n" +
        "   65535 entries' — Zip64 diaktifkan (proyek 67rb+ entri).\n" +
        " • DB: jalankan sql/status_lokalis.sql (kolom status_lokalis\n" +
        "   per tabel) SEBELUM memakai JAR baru.\n" +
        "\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.08.13-rscb  (2026-08-13)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Tiket antrian farmasi thermal 80mm: tercetak otomatis saat\n" +
        "   dokter menyimpan resep rawat jalan (kecuali IGD). Nomor\n" +
        "   antrian = 4 digit terakhir no.resep; memuat Nama, No.Rawat,\n" +
        "   No.RM, Tgl Lahir, Poli, Dokter. Diatur di database.xml\n" +
        "   (TIKETANTRIANFARMASI / PRINTERTIKETFARMASI / ...PREVIEW).\n" +
        " • Riwayat Perawatan: menu sidebar S.B.A.R & T.B.A.K Rawat Inap\n" +
        "   beserta status validasinya (validator, tanggal, jam).\n" +
        " • TTE Satu Sehat: chaining signature Residen -> DPJP Supervisi\n" +
        "   (RME Rawat Inap); PractitionerRole eligibility (skema\n" +
        "   Pengecekan SSM) + mapping spesialis; dialog Buat TTE\n" +
        "   menampilkan JSON request/response (copy-able), keterangan\n" +
        "   UAT, dan tombol Simpan JSON untuk bukti UAT.\n" +
        " • Keamanan: auto-logout saat idle (default 10 menit).\n" +
        " • Radiologi: QR hasil radiologi mengunduh PDF (sama seperti WA);\n" +
        "   teks ekspertise di e-Pasien dirapikan.\n" +
        " • APM/SEP: anjungan APMSEPDuaPrinter (antrean berprefix, task\n" +
        "   admisi, cetak seragam), cetak label RM di jalur pembuat SEP,\n" +
        "   perbaikan tujuanKunj, jumlah cetak & printer tujuan, serta\n" +
        "   SEP kontrol pasca ranap (ppkRujukan dipasangkan dengan\n" +
        "   asalRujukan); waktu pembuatan SEP dicatat di tabel terpisah.\n" +
        " • SIRS: modul laporan SIRS RL 6.3 diport dari SIMRSKhanza-\n" +
        "   Trimitra; tombol laporan RL lama disembunyikan.\n" +
        " • Farmasi/Inventory: pencarian obat jadi live search; filter\n" +
        "   Rawat Jalan/Inap/ICU/IGD di Daftar Pengguna Obat.\n" +
        " • Fix: Kardex DPJP dibaca dari dpjp_ranap & Tgl Stop opsional;\n" +
        "   RM2D diagnosa awal/tindakan tidak lagi tertutup kotak dan\n" +
        "   Ringkasan Riwayat MK terisi otomatis; batas input Tindakan\n" +
        "   Operasi 50 -> 5000 karakter; foto/TTD web muncul di form\n" +
        "   Surat (folder distinct anti-tabrakan).\n" +
        "\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.07.27-rscb  (2026-07-27)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • PRMRJ (Profil Ringkas Medis Rawat Jalan): urutan kunjungan\n" +
        "   dibalik — Kunjungan ke-1 = kunjungan terlama, terbaru di\n" +
        "   paling bawah. Filter '5 Terakhir' tetap ambil 5 kunjungan\n" +
        "   terbaru, penomoran mulai dari 1.\n" +
        " • DlgKamarInap: menu Bridging > Rencana Kontrol BPJS dihapus\n" +
        "   (tidak dipakai dari Rawat Inap).\n" +
        " • Bundel Satu Sehat — dokumen RME entry-based (resource\n" +
        "   terstruktur, bukan teks naratif):\n" +
        "   - Resume Medis Ranap (Obat/Lab/Chief Complaint/Fisik/Alergi).\n" +
        "   - Laporan Persalinan sesuai Modul INC + preview data bayi.\n" +
        "   - Laporan Operasi (Allergies/Complications/Procedure Findings).\n" +
        "   - Laporan Tindakan (Allergies/Procedure Findings/Procedures).\n" +
        "   - Laporan Anestesi (Pre-eval/Procedure/Monitoring/Post).\n" +
        "   - Penilaian Awal Medis IGD & Transfer Internal RS.\n" +
        " • Triase IGD lengkap sesuai playbook: Penilaian ABCD, Procedure\n" +
        "   'Triage' (SNOMED 225390008), Sarana Transportasi Kedatangan\n" +
        "   (LOINC 74286-6), rujukan & CTAS; author = petugas triase.\n" +
        " • Encounter: kirim classHistory (histori jenis rawat), diagnosis\n" +
        "   utama vs sekunder dibedakan, period tak lagi terbalik,\n" +
        "   filter pakai stts. Procedure: performed[x] adaptif, kategori\n" +
        "   tidak ditebak dari 2 digit ICD-9, alasan 400 ditampilkan.\n" +
        " • Modul Registrasi SatuSehat: Kanker, Jantung, Stroke,\n" +
        "   Uronefrologi (skema DB, orkestrator, form + worklist + menu);\n" +
        "   helper EpisodeOfCare siap registry waitlist.\n" +
        " • Dokumen DICOM: upload PDF/gambar SIMRS -> konversi go-dcm ->\n" +
        "   STOW langsung ke Satu Sehat; daftar Berkas Rawat dengan\n" +
        "   tombol kirim & status per berkas, AccessionNumber unik.\n" +
        " • TTE Satu Sehat: QR hitung mundur + pantau webhook (auto-tutup),\n" +
        "   author per-dokumen, sinkron praktisi, filter+warna status,\n" +
        "   retry Gagal, preview dokumen, cetak bukti, 'Buka Lokasi di\n" +
        "   Peta'; aksi per-baris pindah ke klik-kanan. Webhook: persist +\n" +
        "   idempotency, /healthz + metrik, dashboard bertema Kemkes.\n" +
        " • Diagnosa & Prosedur: nomor Urut melanjutkan saat simpan satu-\n" +
        "   satu dan dapat diedit langsung di tabel.\n" +
        " • Antrian & Mobile JKN: prefix antrean A/B/C, kode penjab BPJS\n" +
        "   dibaca dari DB, perbaikan task admisi & validasi\n" +
        "   pemeriksaan_ralan di semua menu kirim task.\n" +
        "\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.07.12-rscb  (2026-07-12)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Asesmen Menjelang Akhir Kehidupan (Terminal)/Paliatif —\n" +
        "   form baru RMAsesmenAkhirKehidupanTerminal:\n" +
        "   - Skala penilaian 7 gejala (0-10), Total 0-70, kesimpulan\n" +
        "     paket asuhan OTOMATIS (Ringan/Sedang/Berat); pengecualian:\n" +
        "     1 gejala >7 langsung Berat.\n" +
        "   - Skor keparahan masalah (nyeri/gejala lain/psiko-spiritual/\n" +
        "     keluarga) + paket rencana otomatis; orientasi & keprihatinan\n" +
        "     spiritual; status psikososial; kebutuhan bantuan pelayanan\n" +
        "     (Full Code/DNR/Comfort Care Only).\n" +
        "   - Tabel asesmen_akhir_kehidupan_terminal; menu di DlgRawatInap,\n" +
        "     DlgRawatJalan, DlgReg; cetak jasper.\n" +
        "\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.07.06-rscb  (2026-07-06)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Kirim PDF ke WhatsApp pasien (GOWA /send/file, PDF terproteksi\n" +
        "   password = tgl lahir, device WA per-MAC PC -> global):\n" +
        "   - Resume Ranap (klik-kanan tabel), Surat Pengantar Pulang,\n" +
        "     Surat Keterangan Rawat Inap (2 form), Surat Sakit 1.\n" +
        "   - Report varian QR dokter: Resume, Surat Ket. Rawat Inap,\n" +
        "     rptSuratSakit2; rptSuratSakit5 sudah ber-QR.\n" +
        " • Mapping MAC PC <-> device WA (whatsapp.DlgMapMacDevice) +\n" +
        "   menu di frmUtama; DlgBangsal + kolom No.Telp/Device ID.\n" +
        "   koneksiDB.getLocalMacAddress / GOWADEVICEID_BYMAC.\n" +
        " • Apotek BPJS: form Input Resep Obat + Permintaan Iterasi\n" +
        "   (3 tombol input bertahap: Simpan Resep / Obat Non Racikan /\n" +
        "   Obat Racikan). Tombol Obat kini juga membuat piutang obat +\n" +
        "   potong stok gudang + jurnal (idempoten per nota, jika\n" +
        "   JADIKANPIUTANGAPOTEKBPJS=yes).\n" +
        " • Lama Waktu Pelayanan Mobile JKN: rekap rata-rata + distribusi\n" +
        "   durasi total di tabel; Task 1&2 dari antripendaftaran,\n" +
        "   Task 6 (validasi resep) & 7 (penyerahan) dari resep_obat.\n" +
        " • Menu Skrining Rawat Jalan di DlgReg, DlgIGD, DlgKasirRalan.\n" +
        " • RMSKriningRawatJalan: gambar referensi skala nyeri (nyeri.png).\n" +
        " • Fix: waktu peresepan tak lagi tertimpa saat validasi resep;\n" +
        "   query tampil() beberapa form Surat (INNER->LEFT JOIN);\n" +
        "   NPE JADIKANPIUTANGAPOTEKBPJS (null-guard, baca plain).\n" +
        "\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.06.21-rscb  (2026-06-21)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Tema warna: aksen diganti ke hijau (0,158,96) di menu bar,\n" +
        "   toolbar, separator, ProgressBar (selaras SIMRS-Khanza).\n" +
        "   Highlight baris terpilih tabel jadi hijau muda.\n" +
        " • PanelDiagnosa: master Diagnosa (ICD-10) & Prosedur (ICD-9)\n" +
        "   kini auto-load begitu form dibuka (tak perlu ketik dulu).\n" +
        "\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.06.16-rscb  (2026-06-16)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Menu SIRS Online (RS Online Yankes) di Bridging: Referensi\n" +
        "   Kamar, Maping Kamar, & Update Kamar SIRS Online (3 form).\n" +
        "   Tampil jika user punya akses 'Referensi Kamar Aplicare'.\n" +
        " • Tema warna: ganti aksen pink/magenta -> navy (#2C3E50) di\n" +
        "   menu bar, toolbar, separator, ProgressBar.\n" +
        " • Permukaan grid/panel dilembutkan dari putih murni -> off-white\n" +
        "   (240,242,245) agar tidak silau; field input tetap putih.\n" +
        " • Build: nama file jar dist diganti -> rscb2026.jar.\n" +
        "\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.06.15-rscb  (2026-06-15)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Asesmen Pasien Tahap Terminal (RM.KEP.08.002): ganti form lama\n" +
        "   Penilaian -> class baru RMAsesmenPasienTerminal.\n" +
        "   - Tabel granular asesmen_pasien_terminal (B1-B6, pemahaman,\n" +
        "     nyeri, psikososial, spiritual) + jasper rptAsesmenPasienTerminal\n" +
        "     (kop, identitas, QR TTD perawat).\n" +
        "   - Layout 2 tab (Input & Data Input); tombol cari petugas\n" +
        "     (DlgCariPetugas); menu klik-kanan Cetak Jasper.\n" +
        " • Pemakaian Obat per Depo: tombol widget.Button, Cetak Jasper &\n" +
        "   CSV, total Qty (form & jasper).\n" +
        " • Tracking: trackersql kini catat ID login asli (userlogin) + IP,\n" +
        "   bisa bedakan antar admin utama.\n" +
        " • DlgAdmin: dukung multi admin utama (validasi single-admin dibuang).\n" +
        "\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " v2026.06.12-rscb  (2026-06-12)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Pengenalan sistem versioning terpusat (kelas fungsi.Versi);\n" +
        "   versi aplikasi kini tampil di title bar utama.\n" +
        " • Formulir Permintaan Pelayanan Kerohanian (RM.KEP.08.009):\n" +
        "   - Tabel pelayanan_kerohanian dibuat.\n" +
        "   - Jasper rptPermintaanPelayananKerohanian disesuaikan dgn\n" +
        "     form fisik (blok pemohon, agama/kepercayaan, blok pasien,\n" +
        "     TTD Perawat & Pasien/keluarga).\n" +
        "   - No.Permintaan otomatis format yyyy/PK/{romawi}/{urut},\n" +
        "     fix bug nomor selalu '002' (duplikat) & prefix salah.\n" +
        "\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " Sebelum sistem versi (riwayat ringkas dari git)\n" +
        "═══════════════════════════════════════════════════════════\n" +
        " • Analisis VEN-ABC obat + pemakaian per depo.\n" +
        " • Bridging Rujukan Pasien SatuSehat (RJ/Ranap/IGD/Masuk).\n" +
        " • Bridging Triase IGD & Kirim Bundel SatuSehat.\n";

    /** Judul title bar utama saat aplikasi belum memuat nama instansi. */
    public static String judulAplikasi() {
        return "::[ Khanza SIMKES — RSUD Cabangbungin · v" + NOMOR + " ]::";
    }

    /** Judul title bar setelah nama instansi termuat dari tabel setting. */
    public static String judulInstansi(String namaInstansi) {
        return "SIM " + namaInstansi + "  ·  v" + NOMOR;
    }

    /** Label versi ringkas untuk footer login / about. */
    public static String labelKecil() {
        return "v" + NOMOR;
    }

    /** Teks "Tentang Aplikasi" lengkap beserta riwayat versi. */
    public static String tentangAplikasi() {
        return NAMA + "\n" +
               "Versi: " + NOMOR + "\n" +
               "Dikembangkan oleh: " + DEVELOPER + "\n\n" +
               "RIWAYAT VERSI:\n" + FITUR;
    }
}
