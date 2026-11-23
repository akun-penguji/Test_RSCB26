package sip.chat.rc;

import java.util.logging.Level;
import java.util.logging.Logger;
import sip.SipConfig;

/**
 * Per-user client RC: login pakai password HMAC-derived, simpan authToken +
 * userId di memory. Stateless re-login kalau auth expire.
 *
 * <p>Dipanggil sekali di startup SIMRS (background thread). Token di-cache
 * sampai shutdown. Tidak ada re-login otomatis kecuali di-trigger manual
 * (mis. dari {@link RocketChatService} kalau API balik 401).
 */
public final class RocketChatClient {
    private static final Logger LOG = Logger.getLogger(RocketChatClient.class.getName());

    private final SipConfig config;
    private final String    extension;

    private volatile String authToken;
    private volatile String userId;
    private volatile boolean connected = false;

    public RocketChatClient(SipConfig config, String extension) {
        this.config = config;
        this.extension = extension;
    }

    /** Login ke RC pakai username=extension + HMAC password. Return true kalau sukses. */
    public synchronized boolean connect() {
        if (!config.isRcEnabled()) {
            LOG.info("RC belum di-config, skip login");
            connected = false;
            return false;
        }
        if (extension == null || extension.isEmpty()) {
            LOG.warning("Extension kosong, tidak bisa login RC");
            connected = false;
            return false;
        }
        try {
            String pwd = RocketChatCreds.passwordFor(config.getRcPasswordSecret(), extension);
            String username = RocketChatCreds.usernameFor(extension);
            String[] creds = RocketChatHttp.login(config.getRcUrl(), username, pwd);
            if (creds == null) {
                LOG.warning("RC login gagal untuk ext " + extension);
                connected = false;
                return false;
            }
            this.authToken = creds[0];
            this.userId    = creds[1];
            this.connected = true;
            LOG.info("RC login sukses — user " + username + " userId=" + userId);
            return true;
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "RC connect error", e);
            connected = false;
            return false;
        }
    }

    /** Logout dari RC. */
    public synchronized void disconnect() {
        if (!connected || authToken == null) return;
        try {
            RocketChatHttp.userPost(config.getRcUrl(), "/api/v1/logout", "{}", authToken, userId);
        } catch (Exception ignored) {}
        authToken = null;
        userId = null;
        connected = false;
    }

    public boolean isConnected() { return connected; }
    public String  getAuthToken() { return authToken; }
    public String  getUserId()    { return userId; }
    public String  getUsername()  { return extension; }
    public String  getUrl()       { return config.getRcUrl(); }
    public SipConfig getConfig()  { return config; }
}
