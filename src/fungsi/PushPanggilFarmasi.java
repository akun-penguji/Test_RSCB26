package fungsi;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Thin client pemanggilan antrian farmasi — memberi tahu backend Rust SIMRSCB
 * bahwa obat sebuah resep sudah diserahkan (tombol Penyerahan di
 * DlgDaftarPermintaanResep), supaya display TV farmasi memanggil nama pasien.
 *
 * Backend yang resolve nama pasien dari no_rawat dan broadcast via SSE ke
 * halaman /display/farmasi.
 *
 * Prinsip (sama dengan PushMobileJKN):
 *  - fire-and-forget: HTTP jalan di thread pool daemon, TIDAK memblok UI.
 *  - best-effort: error apa pun didiamkan; penyerahan operator tetap sukses.
 *
 * Config dibaca dari tabel simrscb_config (kunci mobilejkn_backend_url —
 * base URL backend SIMRSCB yang sama), di-cache sekali.
 */
public class PushPanggilFarmasi {

    private static final ExecutorService POOL = Executors.newFixedThreadPool(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "PushPanggilFarmasi");
            t.setDaemon(true); // jangan menahan JVM shutdown
            return t;
        }
    });

    private static final sekuel Sequel = new sekuel();
    private static String BASE_URL = null;
    private static boolean loaded = false;

    private PushPanggilFarmasi() {}

    /** Penyerahan obat: display memanggil nama pasien (TTS + panel utama). */
    public static void kirim(String noResep, String noRawat) {
        kirim(noResep, noRawat, "penyerahan");
    }

    /**
     * @param noResep nomor resep (wajib)
     * @param noRawat no_rawat pasien (boleh "", backend resolve dari no_resep)
     * @param jenis   "penyerahan" (panggil pasien) atau "validasi"
     *                (pasien masuk daftar sedang disiapkan di display)
     */
    public static void kirim(String noResep, String noRawat, String jenis) {
        if (noResep == null || noResep.trim().isEmpty()) return;

        ensureConfig(); // baca config di thread pemanggil, sekali
        if (BASE_URL == null || BASE_URL.isEmpty()) return; // belum dikonfigur → diam

        final String url = BASE_URL.replaceAll("/+$", "") + "/anjungan/farmasi/call";
        final String json = "{"
                + "\"no_resep\":" + q(noResep) + ","
                + "\"no_rawat\":" + q(noRawat == null ? "" : noRawat) + ","
                + "\"jenis\":" + q(jenis == null || jenis.trim().isEmpty() ? "penyerahan" : jenis)
                + "}";

        POOL.submit(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection c = null;
                try {
                    c = (HttpURLConnection) new URL(url).openConnection();
                    c.setRequestMethod("POST");
                    c.setConnectTimeout(2000);
                    c.setReadTimeout(5000);
                    c.setDoOutput(true);
                    c.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    OutputStream os = c.getOutputStream();
                    os.write(json.getBytes("UTF-8"));
                    os.flush();
                    os.close();
                    c.getResponseCode(); // pastikan request benar-benar terkirim
                } catch (Exception ignore) {
                    // best-effort: diamkan. Operator tidak boleh terganggu.
                } finally {
                    if (c != null) {
                        try { c.disconnect(); } catch (Exception ignore) {}
                    }
                }
            }
        });
    }

    private static synchronized void ensureConfig() {
        if (loaded) return;
        try {
            BASE_URL = Sequel.cariIsi("select nilai from simrscb_config where kunci=?", "mobilejkn_backend_url");
        } catch (Exception ignore) {
            // tabel belum ada / DB error → biarkan kosong, fitur non-aktif
        }
        loaded = true; // jangan query ulang tiap event
    }

    private static String q(String s) {
        if (s == null) return "\"\"";
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", " ").replace("\r", " ") + "\"";
    }
}
