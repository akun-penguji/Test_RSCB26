package fungsi;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Properties;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Integritas berkas cache data master (./cache/*.iyem).
 *
 * LATAR BELAKANG
 * Berkas cache disimpan sebagai JSON biasa dengan izin tulis longgar, dan
 * validitasnya selama ini ditentukan semata-mata oleh usia berkas. Siapa pun
 * yang bisa menulis ke direktori cache dapat mengubah daftar dokter, petugas,
 * pegawai, atau bangsal yang ditampilkan aplikasi — tanpa menyentuh basis data
 * sama sekali. Temuan ITSA BSSN menyebutnya "Master Data Cache Tampering via
 * Missing Integrity Validation".
 *
 * CARA KERJANYA
 * Saat aplikasi menulis cache, ia juga menulis tanda tangan HMAC-SHA256 ke
 * berkas pendamping berekstensi ".sig". Saat membaca, tanda tangan diperiksa
 * lebih dulu; bila tidak cocok atau tidak ada, cache diabaikan dan data diambil
 * langsung dari basis data.
 *
 * Kenapa HMAC, bukan hash biasa: hash polos yang disimpan di sebelah berkasnya
 * bisa ikut dihitung ulang oleh penyerang. HMAC memerlukan kunci yang tidak ada
 * di direktori cache, sehingga tanda tangan tidak bisa dipalsukan tanpa
 * menguasai berkas kunci.
 *
 * Kenapa bukan sekadar membandingkan isi cache dengan nilai tersimpan: isi
 * cache memang berubah secara sah. ./cache/dokter.iyem, misalnya, ditulis
 * berdasarkan hari kerja dan poliklinik yang sedang dipilih, sehingga dua
 * pembacaan yang sah bisa menghasilkan isi yang berbeda.
 *
 * KUNCI
 * Diturunkan dari AES_KEY yang sudah dipakai instalasi (system property, lalu
 * environment, lalu setting/aes.key) melalui SHA-256 dengan pembeda konteks,
 * supaya satu rahasia yang didistribusikan tidak dipakai mentah untuk dua
 * keperluan berbeda.
 *
 * PERILAKU BILA KUNCI TIDAK ADA
 * Verifikasi dilewati dan cache diperlakukan seperti sebelumnya. Instalasi yang
 * belum menerima setting/aes.key tetap berjalan normal, hanya tanpa
 * perlindungan ini. Mematikan aplikasi karena berkas kunci belum terdistribusi
 * akan lebih merugikan daripada kerentanan yang ditutup.
 */
public class CacheAman {

    private static final String PEMBEDA = "khanza-cache-integrity|";
    private static byte[] kunci;
    private static boolean kunciDimuat = false;

    /** Kunci HMAC, atau null bila tidak ada kunci AES di instalasi ini. */
    private static synchronized byte[] kunci() {
        if (kunciDimuat) return kunci;
        kunciDimuat = true;
        try {
            String aes = System.getProperty("AES_KEY");
            if (aes == null || aes.isEmpty()) aes = System.getenv("AES_KEY");
            if (aes == null || aes.isEmpty()) {
                File f = new File("setting/aes.key");
                if (f.exists()) {
                    Properties p = new Properties();
                    try (FileInputStream fis = new FileInputStream(f)) { p.load(fis); }
                    aes = p.getProperty("AES_KEY");
                }
            }
            if (aes == null || aes.isEmpty()) { kunci = null; return null; }
            kunci = MessageDigest.getInstance("SHA-256")
                    .digest((PEMBEDA + aes).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            kunci = null;
        }
        return kunci;
    }

    private static File berkasSidik(String path) {
        return new File(path + ".sig");
    }

    /**
     * Peringatkan sekali per berkas per proses.
     *
     * CariDokter saja dipanggil dari ratusan tempat; mencetak pada setiap
     * pemanggilan akan menenggelamkan konsol dan justru menyembunyikan
     * peringatan lain yang penting.
     */
    private static final java.util.Set<String> SUDAH_DIPERINGATKAN =
            java.util.Collections.synchronizedSet(new java.util.HashSet<String>());

    private static void peringatkanSekali(String path, String sebab) {
        if (SUDAH_DIPERINGATKAN.add(path)) {
            System.err.println("Notifikasi : cache " + path + " " + sebab
                    + ", data diambil langsung dari basis data.");
        }
    }

    private static String hitung(File berkas, byte[] k) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(k, "HmacSHA256"));
        byte[] tanda = mac.doFinal(Files.readAllBytes(berkas.toPath()));
        StringBuilder sb = new StringBuilder(tanda.length * 2);
        for (byte b : tanda) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    /**
     * Tulis tanda tangan untuk berkas cache. Panggil SETELAH penulisan cache
     * selesai dan alirannya ditutup; memanggil sebelum itu akan menandatangani
     * berkas yang belum lengkap.
     */
    public static void tulisSidik(String path) {
        byte[] k = kunci();
        if (k == null) return;
        try {
            File berkas = new File(path);
            if (!berkas.exists()) return;
            Files.write(berkasSidik(path).toPath(),
                    hitung(berkas, k).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.err.println("Notifikasi : gagal menulis sidik cache " + path + " — " + e.getMessage());
        }
    }

    /**
     * Apakah cache boleh dipercaya?
     *
     * Mengembalikan true bila tidak ada kunci (perilaku lama dipertahankan),
     * atau bila tanda tangannya cocok. Mengembalikan false bila berkas cache
     * tidak ada, tanda tangannya tidak ada, atau tidak cocok — pemanggil harus
     * mengambil data dari basis data.
     */
    public static boolean sahih(String path) {
        byte[] k = kunci();
        if (k == null) return true;
        try {
            File berkas = new File(path);
            File sidik = berkasSidik(path);
            if (!berkas.exists()) return false;
            if (!sidik.exists()) {
                // Belum bertanda tangan: bisa karena instalasi baru saja
                // diperbarui dan cache belum pernah ditulis ulang. Diperlakukan
                // tidak tepercaya — aman, dan sembuh sendiri begitu cache
                // disegarkan. Jalankan fungsi.CacheAman sekali saat pemasangan
                // untuk menghindari fallback basis data berkepanjangan.
                peringatkanSekali(path, "belum bertanda tangan");
                return false;
            }
            String tersimpan = new String(Files.readAllBytes(sidik.toPath()),
                    StandardCharsets.UTF_8).trim();
            String sekarang = hitung(berkas, k);
            // Perbandingan waktu-tetap: pembandingan biasa membocorkan posisi
            // ketidakcocokan lewat selisih waktu.
            if (tersimpan.length() != sekarang.length()) return false;
            int beda = 0;
            for (int i = 0; i < tersimpan.length(); i++) {
                beda |= tersimpan.charAt(i) ^ sekarang.charAt(i);
            }
            if (beda != 0) {
                peringatkanSekali(path, "tidak lolos pemeriksaan integritas");
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Buang cache yang tidak bertanda tangan sah. Dipanggil sekali saat
     * aplikasi mulai.
     *
     * Sengaja MENGHAPUS, bukan menandatangani. Menandatangani otomatis berarti
     * mengesahkan apa pun yang kebetulan ada di direktori cache saat itu —
     * termasuk berkas yang sudah terlanjur diubah sebelum pembaruan dipasang.
     * Cache hanyalah salinan data basis data, jadi membuangnya tidak
     * menghilangkan apa pun: aplikasi menuliskannya kembali saat dibutuhkan,
     * lengkap dengan tanda tangannya.
     *
     * Setelah pembaruan pertama, seluruh cache lama terhapus sekali, lalu
     * terbentuk ulang sendiri selama pemakaian normal. Startup berikutnya tidak
     * menghapus apa-apa karena semuanya sudah bertanda tangan.
     *
     * Bila instalasi belum punya kunci AES, tidak ada yang dihapus.
     */
    public static int bersihkanYangTidakTepercaya(String direktori) {
        if (kunci() == null) return 0;
        File dir = new File(direktori);
        File[] isi = dir.listFiles((d, n) -> n.endsWith(".iyem"));
        if (isi == null) return 0;
        int dibuang = 0;
        for (File f : isi) {
            if (!sahih(f.getPath())) {
                if (f.delete()) {
                    new File(f.getPath() + ".sig").delete();
                    dibuang++;
                }
            }
        }
        if (dibuang > 0) {
            System.out.println("Notifikasi : " + dibuang + " berkas cache dibuang karena tidak "
                    + "bertanda tangan sah; akan dibentuk ulang dari basis data saat dibutuhkan.");
        }
        return dibuang;
    }

    /** Dipanggil dari titik mulai aplikasi. Tidak pernah melempar. */
    public static void siapkanSaatMulai() {
        try {
            bersihkanYangTidakTepercaya("./cache");
        } catch (Exception e) {
            System.err.println("Notifikasi : gagal memeriksa integritas cache — " + e.getMessage());
        }
    }

    /**
     * Tandatangani seluruh cache yang ada sekarang.
     *
     * HANYA untuk dijalankan manual pada mesin yang isi cache-nya memang
     * dipercaya — misalnya saat menyiapkan citra instalasi baru. Jangan
     * dijalankan untuk "memperbaiki" peringatan integritas di mesin produksi;
     * melakukannya justru mengesahkan berkas yang mungkin sudah diubah.
     */
    public static int tandatanganiSemua(String direktori) {
        File dir = new File(direktori);
        File[] isi = dir.listFiles((d, n) -> n.endsWith(".iyem"));
        if (isi == null) return 0;
        int n = 0;
        for (File f : isi) { tulisSidik(f.getPath()); n++; }
        return n;
    }

    public static void main(String[] args) {
        String dir = args.length > 0 ? args[0] : "./cache";
        if (kunci() == null) {
            System.out.println("Kunci AES tidak ditemukan (setting/aes.key / env AES_KEY). "
                    + "Tanda tangan tidak dibuat.");
            return;
        }
        System.out.println("Ditandatangani: " + tandatanganiSemua(dir) + " berkas cache di " + dir);
    }
}
