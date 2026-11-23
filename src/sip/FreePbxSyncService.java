package sip;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sync kontak dari FreePBX REST API (OAuth2 + GraphQL) ke tabel {@code sip_kontak}.
 *
 * <p>Kredensial diambil dari {@link SipConfig} — user harus set di
 * {@code setting/database.xml}:
 * <ul>
 *   <li>{@code SIP_REST_URL} mis. {@code http://192.168.88.105/admin/api/api}</li>
 *   <li>{@code SIP_REST_CLIENT_ID} — dari aplikasi OAuth2 yang dibuat di FreePBX</li>
 *   <li>{@code SIP_REST_CLIENT_SECRET}</li>
 * </ul>
 *
 * <p>Strategi upsert: kontak yang ada di FreePBX di-save dengan
 * {@code sumber='freepbx_sync'}. Kontak lama dengan {@code sumber='freepbx_sync'}
 * yang tidak lagi ada di FreePBX di-set {@code aktif=0} (soft delete).
 * Kontak manual ({@code sumber='manual'}) tidak disentuh.
 */
public final class FreePbxSyncService {
    private static final Logger LOG = Logger.getLogger(FreePbxSyncService.class.getName());

    private static final String GRAPHQL_QUERY =
            "{ fetchAllExtensions { status message extension { extensionId user { name extension } } } }";

    private final SipConfig config;
    private final KontakService kontakService;

    public FreePbxSyncService(SipConfig config, KontakService kontakService) {
        this.config = config;
        this.kontakService = kontakService;
    }

    /** Jalankan sync. Return jumlah kontak yang ter-upsert, atau -1 kalau error. */
    public int sync() {
        if (config.getRestUrl().isEmpty()
                || config.getRestClientId().isEmpty()
                || config.getRestClientSecret().isEmpty()) {
            LOG.warning("FreePBX REST belum di-config (SIP_REST_URL / CLIENT_ID / CLIENT_SECRET)");
            return -1;
        }
        try {
            String token = fetchToken();
            if (token == null) return -1;
            List<Kontak> remote = fetchExtensions(token);
            return upsert(remote);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "FreePBX sync error", e);
            return -1;
        }
    }

    // ===== OAuth2 token =====

    private String fetchToken() throws IOException {
        String url = config.getRestUrl() + "/token";
        // Tanpa param "scope" — app M2M dengan "Allowed Scopes" kosong akan di-issue
        // token dengan default full scopes. Kalau di FreePBX scope di-restrict,
        // token request harus minta scope yang valid.
        String body = "grant_type=client_credentials"
                + "&client_id="     + URLEncoder.encode(config.getRestClientId(),     "UTF-8")
                + "&client_secret=" + URLEncoder.encode(config.getRestClientSecret(), "UTF-8");
        String resp = httpPost(url, "application/x-www-form-urlencoded", body, null);
        if (resp == null) return null;
        Matcher m = Pattern.compile("\"access_token\"\\s*:\\s*\"([^\"]+)\"").matcher(resp);
        if (m.find()) return m.group(1);
        LOG.warning("Tidak bisa ambil access_token dari response: " + resp);
        return null;
    }

    // ===== GraphQL extensions =====

    private List<Kontak> fetchExtensions(String token) throws IOException {
        String url = config.getRestUrl() + "/gql";
        String body = "{\"query\":\"" + GRAPHQL_QUERY.replace("\"", "\\\"") + "\"}";
        String resp = httpPost(url, "application/json", body, token);
        return parseExtensions(resp);
    }

    static List<Kontak> parseExtensions(String json) {
        List<Kontak> out = new ArrayList<>();
        if (json == null) return out;
        // minimal JSON parsing: cari setiap blok "extension":{ ... }
        Pattern blockP = Pattern.compile(
                "\"extensionId\"\\s*:\\s*\"?([^,\"}]+)\"?.*?"
              + "\"name\"\\s*:\\s*\"([^\"]*)\".*?"
              + "\"extension\"\\s*:\\s*\"?([^,\"}]+)\"?",
                Pattern.DOTALL);
        Matcher m = blockP.matcher(json);
        while (m.find()) {
            Kontak k = new Kontak();
            k.extension = m.group(1).trim();
            k.nama      = m.group(2).trim();
            k.unit      = "";
            k.aktif     = true;
            k.sumber    = "freepbx_sync";
            out.add(k);
        }
        return out;
    }

    // ===== Upsert DB =====

    private int upsert(List<Kontak> remote) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        int n = 0;
        java.util.Set<String> remoteExts = new java.util.HashSet<>();
        for (Kontak k : remote) {
            remoteExts.add(k.extension);
            Kontak existing = kontakService.find(k.extension);
            if (existing != null && "manual".equals(existing.sumber)) {
                // Kontak manual jangan ditimpa — cukup tandai aktif
                continue;
            }
            Kontak saveTo = existing != null ? existing : k;
            saveTo.extension = k.extension;
            saveTo.nama      = k.nama;
            saveTo.unit      = existing != null && existing.unit != null ? existing.unit : "";
            saveTo.aktif     = true;
            saveTo.sumber    = "freepbx_sync";
            saveTo.terakhirSync = now;
            if (kontakService.save(saveTo)) n++;
        }
        // Soft-delete kontak freepbx_sync yang hilang
        for (Kontak k : kontakService.list(null)) {
            if ("freepbx_sync".equals(k.sumber) && !remoteExts.contains(k.extension)) {
                k.aktif = false;
                kontakService.save(k);
            }
        }
        LOG.info("FreePBX sync: " + n + " kontak ter-upsert dari " + remote.size() + " di server");
        return n;
    }

    // ===== HTTP helper =====

    private static String httpPost(String url, String contentType, String body, String bearer)
            throws IOException {
        HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
        c.setRequestMethod("POST");
        c.setConnectTimeout(8000);
        c.setReadTimeout(15000);
        c.setDoOutput(true);
        c.setRequestProperty("Content-Type", contentType);
        c.setRequestProperty("Accept", "application/json");
        if (bearer != null) c.setRequestProperty("Authorization", "Bearer " + bearer);
        try (OutputStream os = c.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
        }
        int code = c.getResponseCode();
        java.io.InputStream is = code >= 400 ? c.getErrorStream() : c.getInputStream();
        if (is == null) return null;
        try (java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream()) {
            byte[] buf = new byte[4096]; int n;
            while ((n = is.read(buf)) != -1) bos.write(buf, 0, n);
            String resp = bos.toString(StandardCharsets.UTF_8.name());
            if (code >= 400) {
                LOG.warning("HTTP " + code + " dari " + url + ": " + resp);
                return null;
            }
            return resp;
        }
    }
}
