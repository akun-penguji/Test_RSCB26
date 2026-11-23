package sip.chat.rc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sip.SipConfig;

/** Helper HTTP client untuk Rocket.Chat REST API. */
final class RocketChatHttp {
    private RocketChatHttp() {}

    static String adminGet(SipConfig cfg, String path) throws IOException {
        return request(cfg.getRcUrl() + path, "GET", null, cfg.getRcAdminToken(), cfg.getRcAdminUserId());
    }

    static String adminPost(SipConfig cfg, String path, String body) throws IOException {
        return request(cfg.getRcUrl() + path, "POST", body, cfg.getRcAdminToken(), cfg.getRcAdminUserId());
    }

    static String userGet(String rcUrl, String path, String authToken, String userId) throws IOException {
        return request(rcUrl + path, "GET", null, authToken, userId);
    }

    static String userPost(String rcUrl, String path, String body, String authToken, String userId) throws IOException {
        return request(rcUrl + path, "POST", body, authToken, userId);
    }

    /** Login REST: POST /api/v1/login. Return authToken atau null. */
    static String[] login(String rcUrl, String username, String password) throws IOException {
        String body = "{\"user\":\"" + jsonEscape(username) + "\",\"password\":\"" + jsonEscape(password) + "\"}";
        String resp = request(rcUrl + "/api/v1/login", "POST", body, null, null);
        String token  = extractJson(resp, "\"authToken\":\"([^\"]+)\"");
        String userId = extractJson(resp, "\"userId\":\"([^\"]+)\"");
        return (token != null && userId != null) ? new String[]{token, userId} : null;
    }

    // ===== low-level =====

    private static String request(String fullUrl, String method, String body, String authToken, String userId)
            throws IOException {
        HttpURLConnection c = (HttpURLConnection) new URL(fullUrl).openConnection();
        c.setRequestMethod(method);
        c.setConnectTimeout(8000);
        c.setReadTimeout(15000);
        c.setRequestProperty("Accept", "application/json");
        if (authToken != null && userId != null) {
            c.setRequestProperty("X-Auth-Token", authToken);
            c.setRequestProperty("X-User-Id",    userId);
        }
        if (body != null) {
            c.setDoOutput(true);
            c.setRequestProperty("Content-Type", "application/json");
            try (OutputStream os = c.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }
        }
        int code = c.getResponseCode();
        InputStream is = code >= 400 ? c.getErrorStream() : c.getInputStream();
        if (is == null) return null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[4096]; int n;
            while ((n = is.read(buf)) != -1) bos.write(buf, 0, n);
            return bos.toString(StandardCharsets.UTF_8.name());
        }
    }

    static String extractJson(String json, String regex) {
        if (json == null) return null;
        Matcher m = Pattern.compile(regex).matcher(json);
        return m.find() ? m.group(1) : null;
    }

    static String urlEncode(String s) {
        try { return URLEncoder.encode(s, "UTF-8"); }
        catch (Exception e) { return s; }
    }

    static String jsonEscape(String s) {
        if (s == null) return "";
        StringBuilder b = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  b.append("\\\"");  break;
                case '\\': b.append("\\\\");  break;
                case '\n': b.append("\\n");   break;
                case '\r': b.append("\\r");   break;
                case '\t': b.append("\\t");   break;
                default:
                    if (c < 0x20) b.append(String.format("\\u%04x", (int) c));
                    else b.append(c);
            }
        }
        return b.toString();
    }
}
