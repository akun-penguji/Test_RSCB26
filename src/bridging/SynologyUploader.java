package bridging;

import fungsi.koneksiDB;
import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.Timeout;

/**
 * Synology File Station API uploader
 * Supports login, upload, logout with local/online URL fallback
 */
public class SynologyUploader {

    private String baseUrl;
    private String sid;
    private String resolvedFolder;
    private CloseableHttpClient httpClient;

    public SynologyUploader() {
        try {
            // Trust all SSL certs (Synology self-signed)
            SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial((chain, authType) -> true)
                .build();

            RequestConfig reqConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(5))
                .setResponseTimeout(Timeout.ofSeconds(30))
                .build();

            httpClient = HttpClients.custom()
                .setDefaultRequestConfig(reqConfig)
                .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(SSLConnectionSocketFactoryBuilder.create()
                        .setSslContext(sslContext)
                        .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                        .build())
                    .build())
                .build();
        } catch (Exception e) {
            System.out.println("SynologyUploader init error: " + e.getMessage());
            httpClient = HttpClients.createDefault();
        }
    }

    /**
     * Login ke Synology. Coba local dulu, fallback ke online.
     */
    public boolean login() {
        String urlLocal = koneksiDB.SYNOLOGYURLLOCAL();
        String urlOnline = koneksiDB.SYNOLOGYURLONLINE();
        String user = koneksiDB.SYNOLOGYUSER();
        String pass = koneksiDB.SYNOLOGYPASS();

        // Coba local dulu
        if (!urlLocal.isEmpty()) {
            System.out.println("Synology: trying local URL...");
            if (tryLogin(urlLocal, user, pass)) {
                System.out.println("Synology: connected via local");
                return true;
            }
        }

        // Fallback ke online
        if (!urlOnline.isEmpty()) {
            System.out.println("Synology: trying online URL...");
            if (tryLogin(urlOnline, user, pass)) {
                System.out.println("Synology: connected via online");
                return true;
            }
        }

        System.out.println("Synology: login failed");
        return false;
    }

    private boolean tryLogin(String url, String user, String pass) {
        try {
            String loginUrl = url + "/webapi/auth.cgi?api=SYNO.API.Auth&version=3&method=login"
                + "&account=" + URLEncoder.encode(user, "UTF-8")
                + "&passwd=" + URLEncoder.encode(pass, "UTF-8")
                + "&session=FileStation&format=sid";

            HttpGet request = new HttpGet(loginUrl);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String body = EntityUtils.toString(response.getEntity());
                System.out.println("Synology login response: " + body);

                if (body.contains("\"success\":true") || body.contains("\"success\": true")) {
                    // Extract sid
                    int sidIdx = body.indexOf("\"sid\"");
                    if (sidIdx >= 0) {
                        int start = body.indexOf("\"", sidIdx + 6) + 1;
                        int end = body.indexOf("\"", start);
                        sid = body.substring(start, end);
                        baseUrl = url;
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Synology login error (" + url + "): " + e.getMessage());
        }
        return false;
    }

    /**
     * Upload file ke Synology File Station
     * @param file file PDF yang akan diupload
     * @param fileName nama file tujuan (misal: 0301R0010426V000026.pdf)
     * @return true jika sukses
     */
    /**
     * Upload file ke Synology File Station
     * @param file file PDF yang akan diupload
     * @param fileName nama file tujuan (misal: 0301R0010426V000026.pdf)
     * @param tglRegistrasi tanggal registrasi pasien (format yyyy-MM-dd) untuk subfolder
     * @return true jika sukses
     */
    public boolean upload(File file, String fileName, String tglRegistrasi) {
        if (sid == null || baseUrl == null) {
            System.out.println("Synology: not logged in");
            return false;
        }

        // Resolve base folder path
        if (resolvedFolder == null) {
            resolvedFolder = resolveFolder();
        }
        if (resolvedFolder == null || resolvedFolder.isEmpty()) {
            System.out.println("Synology: folder path could not be resolved");
            return false;
        }

        // Build subfolder: BERKAS RAWAT JALAN/2026/04/10
        String uploadFolder = resolvedFolder;
        if (tglRegistrasi != null && tglRegistrasi.length() >= 10) {
            String tahun = tglRegistrasi.substring(0, 4);
            String bulan = tglRegistrasi.substring(5, 7);
            String tanggal = tglRegistrasi.substring(8, 10);
            uploadFolder = resolvedFolder + "/" + tahun + "/" + bulan + "/" + tanggal;
        }
        System.out.println("Synology upload folder: " + uploadFolder);

        try {
            String uploadUrl = baseUrl + "/webapi/entry.cgi?api=SYNO.FileStation.Upload&version=2&method=upload&_sid=" + sid;

            HttpPost post = new HttpPost(uploadUrl);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.LEGACY);
            builder.addTextBody("path", uploadFolder, ContentType.TEXT_PLAIN);
            builder.addTextBody("create_parents", "true", ContentType.TEXT_PLAIN);
            builder.addTextBody("overwrite", "true", ContentType.TEXT_PLAIN);
            builder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, fileName);
            post.setEntity(builder.build());

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                String body = EntityUtils.toString(response.getEntity());
                System.out.println("Synology upload response: " + body);
                return body.contains("\"success\":true") || body.contains("\"success\": true");
            }
        } catch (Exception e) {
            System.out.println("Synology upload error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Resolve folder path — coba beberapa variasi path
     */
    private String resolveFolder() {
        String configFolder = koneksiDB.SYNOLOGYFOLDER();
        if (configFolder.isEmpty()) configFolder = "/team-folders/CASEMIX/BERKAS RAWAT JALAN";

        // Coba beberapa variasi path
        String[] candidates = {
            configFolder,
            "/team-folders/CASEMIX/BERKAS RAWAT JALAN",
            "/CASEMIX/BERKAS RAWAT JALAN",
            "/homes/casemix/BERKAS RAWAT JALAN"
        };

        for (String path : candidates) {
            try {
                String listUrl = baseUrl + "/webapi/entry.cgi?api=SYNO.FileStation.List&version=2&method=list"
                    + "&folder_path=" + URLEncoder.encode(path, "UTF-8")
                    + "&_sid=" + sid;
                HttpGet req = new HttpGet(listUrl);
                try (CloseableHttpResponse resp = httpClient.execute(req)) {
                    String body = EntityUtils.toString(resp.getEntity());
                    System.out.println("Synology folder check [" + path + "]: " + (body.contains("\"success\":true") ? "OK" : "NOT FOUND"));
                    if (body.contains("\"success\":true")) {
                        return path;
                    }
                }
            } catch (Exception e) {
                System.out.println("Synology folder check error [" + path + "]: " + e.getMessage());
            }
        }
        System.out.println("Synology: no valid folder path found, using config: " + configFolder);
        return configFolder;
    }

    /**
     * Logout dari Synology
     */
    public void logout() {
        if (sid == null || baseUrl == null) return;
        try {
            String logoutUrl = baseUrl + "/webapi/auth.cgi?api=SYNO.API.Auth&version=1&method=logout&session=FileStation&_sid=" + sid;
            HttpGet request = new HttpGet(logoutUrl);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                EntityUtils.consume(response.getEntity());
            }
            System.out.println("Synology: logged out");
        } catch (Exception e) {
            System.out.println("Synology logout error: " + e.getMessage());
        } finally {
            sid = null;
            baseUrl = null;
        }
    }

    /**
     * Close HTTP client
     */
    public void close() {
        logout();
        try { if (httpClient != null) httpClient.close(); } catch (Exception e) {}
    }
}
