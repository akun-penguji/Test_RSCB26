package sip.chat.rc;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sip.SipConfig;

/**
 * Wrapper admin API Rocket.Chat untuk provisioning user per-extension.
 *
 * <p>Dipanggil dari {@code DlgEditKomputer.save()} saat admin register komputer baru —
 * RC user otomatis dibuat (atau update kalau sudah ada). Username RC = extension,
 * password = HMAC dari {@code RC_PASSWORD_SECRET}.
 *
 * <p>Endpoint yang dipakai:
 * <ul>
 *   <li>{@code POST /api/v1/users.create}</li>
 *   <li>{@code POST /api/v1/users.update}</li>
 *   <li>{@code GET  /api/v1/users.info?username=...}</li>
 *   <li>{@code POST /api/v1/users.delete}</li>
 * </ul>
 */
public final class RocketChatAdmin {
    private static final Logger LOG = Logger.getLogger(RocketChatAdmin.class.getName());

    private final SipConfig config;

    public RocketChatAdmin(SipConfig config) {
        this.config = config;
    }

    /**
     * Pastikan user RC untuk extension tertentu ada dengan password HMAC current.
     * Return RC user {@code _id} atau null kalau gagal.
     *
     * <p>Kalau user belum ada → create. Kalau sudah ada → update password saja
     * (supaya match HMAC terbaru, penting saat rotasi secret).
     */
    public String ensureUser(String extension, String displayName) {
        if (!config.isRcEnabled()) {
            LOG.warning("RC belum di-config (RC_URL/TOKEN/SECRET). Skip provisioning.");
            return null;
        }
        String username = RocketChatCreds.usernameFor(extension);
        String password = RocketChatCreds.passwordFor(config.getRcPasswordSecret(), extension);
        String email    = RocketChatCreds.emailFor(extension);
        String name     = (displayName == null || displayName.isEmpty()) ? ("SIMRS ext " + extension) : displayName;

        try {
            // 1. Cek user existing
            String lookupJson = httpGet("/api/v1/users.info?username=" + RocketChatHttp.urlEncode(username));
            String userId = RocketChatHttp.extractJson(lookupJson, "\"user\":\\{[^}]*\"_id\":\"([^\"]+)\"");
            if (userId != null) {
                // Update password biar match HMAC terbaru
                String body = "{\"userId\":\"" + userId + "\",\"data\":{"
                        + "\"password\":\""    + RocketChatHttp.jsonEscape(password) + "\","
                        + "\"name\":\""        + RocketChatHttp.jsonEscape(name)     + "\","
                        + "\"active\":true"
                        + "}}";
                String resp = httpPost("/api/v1/users.update", body);
                boolean ok = resp != null && resp.contains("\"success\":true");
                LOG.info("RC user '" + username + "' di-update (ok=" + ok + ")");
                return ok ? userId : null;
            }

            // 2. Create baru
            String body = "{"
                    + "\"username\":\""    + RocketChatHttp.jsonEscape(username) + "\","
                    + "\"email\":\""       + RocketChatHttp.jsonEscape(email)    + "\","
                    + "\"password\":\""    + RocketChatHttp.jsonEscape(password) + "\","
                    + "\"name\":\""        + RocketChatHttp.jsonEscape(name)     + "\","
                    + "\"roles\":[\"user\"],"
                    + "\"verified\":true,"
                    + "\"requirePasswordChange\":false,"
                    + "\"sendWelcomeEmail\":false"
                    + "}";
            String resp = httpPost("/api/v1/users.create", body);
            String newId = RocketChatHttp.extractJson(resp, "\"_id\":\"([^\"]+)\"");
            LOG.info("RC user '" + username + "' di-create (_id=" + newId + ")");
            return newId;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "ensureUser gagal untuk ext " + extension, e);
            return null;
        }
    }

    /** Hapus user RC by _id. Return true kalau sukses. */
    public boolean deleteUser(String rcUserId) {
        if (rcUserId == null || rcUserId.isEmpty()) return false;
        try {
            String resp = httpPost("/api/v1/users.delete", "{\"userId\":\"" + rcUserId + "\"}");
            return resp != null && resp.contains("\"success\":true");
        } catch (Exception e) {
            LOG.log(Level.WARNING, "deleteUser gagal", e);
            return false;
        }
    }

    /** Test apakah admin token masih valid. Return true = valid. */
    public boolean testAdminToken() {
        try {
            String resp = httpGet("/api/v1/me");
            return resp != null && resp.contains("\"success\":true");
        } catch (Exception e) {
            return false;
        }
    }

    // ===== HTTP helpers (pakai admin token + userId) =====

    private String httpGet(String path) throws IOException {
        return RocketChatHttp.adminGet(config, path);
    }

    private String httpPost(String path, String body) throws IOException {
        return RocketChatHttp.adminPost(config, path, body);
    }
}
