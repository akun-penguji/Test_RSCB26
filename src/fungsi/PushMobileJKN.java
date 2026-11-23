package fungsi;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Thin client Mobile JKN — memberi tahu backend Rust SIMRSCB bahwa sebuah event
 * task-id terjadi. Backend yang mengurus resolve kodebooking, HMAC, kirim ke
 * BPJS /antrean/updatewaktu, idempotency, dan log.
 *
 * Prinsip:
 *  - fire-and-forget: HTTP jalan di thread pool daemon, TIDAK memblok UI.
 *  - best-effort: error apa pun didiamkan; simpan operator tetap sukses.
 *  - tanpa state: kirim no_rawat + taskid + waktu; backend yang memutuskan.
 *
 * Config (URL backend + push key) dibaca dari tabel simrscb_config sekali,
 * pada thread pemanggil (aman terhadap koneksi DB Khanza), lalu di-cache.
 */
public class PushMobileJKN {

    private static final ExecutorService POOL = Executors.newFixedThreadPool(2, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "PushMobileJKN");
            t.setDaemon(true); // jangan menahan JVM shutdown
            return t;
        }
    });

    private static final sekuel Sequel = new sekuel();
    private static String BASE_URL = null;
    private static String PUSH_KEY = null;
    private static boolean loaded = false;

    private PushMobileJKN() {}

    /** Bentuk ringkas: waktu boleh "" (backend pakai now). */
    public static void kirim(String noRawat, String taskid, String waktu) {
        kirim(noRawat, taskid, waktu, "", "", "");
    }

    /**
     * @param noRawat no_rawat pasien (wajib)
     * @param taskid  "1".."7" atau "99" (wajib)
     * @param waktu   datetime WIB "yyyy-MM-dd HH:mm:ss" ATAU unix-ms; "" = now di backend
     * @param nama    nama pasien (opsional, untuk log)
     * @param poli    nama poli (opsional, untuk log)
     * @param tglReg  tgl registrasi (opsional, untuk log)
     */
    public static void kirim(String noRawat, String taskid, String waktu,
                             String nama, String poli, String tglReg) {
        if (noRawat == null || noRawat.trim().isEmpty()) return;
        if (taskid == null || taskid.trim().isEmpty()) return;

        ensureConfig(); // baca config di thread pemanggil, sekali
        if (BASE_URL == null || BASE_URL.isEmpty()) return; // belum dikonfigur → diam

        final String url = BASE_URL.replaceAll("/+$", "") + "/mobilejkn/event";
        final String key = PUSH_KEY;
        final String json = "{"
                + "\"no_rawat\":"       + q(noRawat) + ","
                + "\"taskid\":"         + q(taskid)  + ","
                + "\"waktu\":"          + q(waktu)   + ","
                + "\"nama\":"           + q(nama)    + ","
                + "\"poli\":"           + q(poli)    + ","
                + "\"tgl_registrasi\":" + q(tglReg)  + ","
                + "\"source\":\"khanza\"}";

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
                    if (key != null && !key.isEmpty()) {
                        c.setRequestProperty("x-push-key", key);
                    }
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
            PUSH_KEY = Sequel.cariIsi("select nilai from simrscb_config where kunci=?", "mobilejkn_push_key");
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
