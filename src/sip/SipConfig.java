package sip;

import AESsecurity.EnkripsiAES;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Konfigurasi SIP gabungan dari 2 sumber:
 * <ul>
 *   <li><b>Global</b> dari {@code setting/database.xml} — server FreePBX + REST API + sync interval.
 *       Aman di-sync Nextcloud (sama di semua komputer).</li>
 *   <li><b>Per-komputer</b> dari tabel {@code sip_komputer} (keyed by MAC) — extension, user, pass,
 *       display name, auto-answer, ringtone volume. Tiap komputer unik.</li>
 * </ul>
 *
 * <p>Kalau row {@code sip_komputer} untuk komputer ini tidak ada, config SIP
 * dianggap disabled dan status bar akan nampilin "SIP Nonaktif". Admin mesti
 * register komputer via menu Settings → Kelola Komputer SIP.
 */
public final class SipConfig {
    private static final Logger LOG = Logger.getLogger(SipConfig.class.getName());

    // Global dari database.xml
    private final String  srvHost;
    private final int     srvPort;
    private final String  restUrl;
    private final String  restClientId;
    private final String  restClientSecret;
    private final int     kontakSyncIntervalMinutes;

    // Rocket.Chat
    private final String  rcUrl;
    private final String  rcAdminToken;
    private final String  rcAdminUserId;
    private final String  rcPasswordSecret;

    // Per-komputer dari tabel sip_komputer
    private final boolean enabled;
    private final String  num;
    private final String  displayName;
    private final String  user;
    private final String  pass;
    private final boolean autoAnswer;
    private final int     ringtoneVolume;

    // Auto-detect
    private final String  ip;

    private SipConfig(Properties prop, SipKomputer local) {
        this.srvHost          = readEncrypted(prop, "SIP_SRV_HOST", "");
        this.srvPort          = parseIntSafe(readEncrypted(prop, "SIP_SRV_PORT", "5060"), 5060);
        this.restUrl          = readEncrypted(prop, "SIP_REST_URL", "");
        this.restClientId     = readEncrypted(prop, "SIP_REST_CLIENT_ID", "");
        this.restClientSecret = readEncrypted(prop, "SIP_REST_CLIENT_SECRET", "");
        this.kontakSyncIntervalMinutes = readInt(prop, "SIP_KONTAK_SYNC_INTERVAL", 30);

        this.rcUrl            = readEncrypted(prop, "RC_URL", "");
        this.rcAdminToken     = readEncrypted(prop, "RC_ADMIN_TOKEN", "");
        this.rcAdminUserId    = readEncrypted(prop, "RC_ADMIN_USERID", "");
        this.rcPasswordSecret = readEncrypted(prop, "RC_PASSWORD_SECRET", "");

        if (local != null) {
            this.enabled        = local.aktif;
            this.num            = safe(local.extension);
            this.displayName    = safe(local.displayName);
            this.user           = safe(local.username);
            this.pass           = safe(local.password);
            this.autoAnswer     = local.autoAnswer;
            this.ringtoneVolume = local.ringtoneVolume > 0 ? local.ringtoneVolume : 80;
        } else {
            // Fallback backward compat: baca dari Properties kalau row DB belum ada
            this.enabled        = readBool(prop, "SIP_PHONE", false);
            this.num            = readPlain(prop, "SIP_NUM", "");
            this.displayName    = readPlain(prop, "SIP_DISPLAY_NAME", "");
            this.user           = readEncrypted(prop, "SIP_USER", "");
            this.pass           = readEncrypted(prop, "SIP_PASS", "");
            this.autoAnswer     = readBool(prop, "SIP_AUTO_ANSWER", false);
            this.ringtoneVolume = readInt(prop, "SIP_RINGTONE_VOLUME", 80);
        }

        // SIP_IP override (manual) → fallback auto-detect berdasar SIP_SRV_HOST.
        // Kalau target di range Tailscale (100.64.0.0/10) → pakai Tailscale IP, biar
        // source packet konsisten dengan tunnel. Else → pakai LAN IP seperti biasa.
        String ipProp = readPlain(prop, "SIP_IP", "");
        this.ip = !ipProp.isEmpty() ? ipProp : MachineIdentifier.pickLocalIpFor(srvHost);
    }

    /** Load dengan DB lookup ke {@code sip_komputer}. Fallback ke Properties. */
    public static SipConfig load(Properties prop) {
        SipKomputer local = null;
        try { local = new SipKomputerService().findLocal(); }
        catch (Exception e) { LOG.log(Level.WARNING, "Lookup sip_komputer gagal, fallback Properties", e); }
        return new SipConfig(prop != null ? prop : new Properties(), local);
    }

    public static SipConfig load(Properties prop, SipKomputer local) {
        return new SipConfig(prop != null ? prop : new Properties(), local);
    }

    public static SipConfig loadFromFile(String path) {
        Properties p = new Properties();
        try (FileInputStream fis = new FileInputStream(path)) { p.loadFromXML(fis); }
        catch (Exception e) { LOG.log(Level.WARNING, "Gagal load " + path, e); }
        return load(p);
    }

    // ===== Readers =====

    private static String safe(String s) { return s == null ? "" : s; }

    private static String readPlain(Properties prop, String key, String def) {
        String raw = prop.getProperty(key);
        if (raw == null) return def;
        raw = raw.trim();
        return raw.isEmpty() ? def : raw;
    }

    /**
     * Baca value yang mungkin ter-encrypt AES (dev boleh plain).
     *
     * <p>Strategi: kalau bentuk-nya base64 (panjang ≥16, kelipatan 4, char valid),
     * coba decrypt. Kalau hasilnya null (BadPadding) atau bukan printable-ASCII
     * → fallback ke raw (plain text).
     *
     * <p>Sebelumnya pakai heuristic "minimal ada `+`/`/`/`=`" tapi ternyata
     * encrypted output kadang tidak punya marker (tergantung input bytes &
     * IV/key) → false negative → token ter-enkripsi ke-treat sebagai plain.
     */
    private static String readEncrypted(Properties prop, String key, String def) {
        String raw = readPlain(prop, key, null);
        if (raw == null) return def;
        if (looksLikeAesB64(raw)) {
            try {
                String decoded = EnkripsiAES.decrypt(raw);
                if (decoded != null && !decoded.isEmpty() && isPrintableAscii(decoded)) return decoded;
            } catch (Exception ignored) { /* fall through ke plain */ }
        }
        return raw;
    }

    /**
     * Bentuk base64: panjang ≥16, kelipatan 4, semua char {@code A-Za-z0-9+/=}.
     */
    private static boolean looksLikeAesB64(String s) {
        if (s == null || s.length() < 16 || s.length() % 4 != 0) return false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            boolean alpha = (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9');
            boolean marker = c == '+' || c == '/' || c == '=';
            if (!alpha && !marker) return false;
        }
        return true;
    }

    /** Decrypted plaintext valid kalau semua karakter printable ASCII (0x20..0x7E). */
    private static boolean isPrintableAscii(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c < 0x20 || c > 0x7E) return false;
        }
        return true;
    }

    private static int readInt(Properties prop, String key, int def) {
        return parseIntSafe(readPlain(prop, key, null), def);
    }

    private static int parseIntSafe(String s, int def) {
        if (s == null) return def;
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return def; }
    }

    private static boolean readBool(Properties prop, String key, boolean def) {
        String s = readPlain(prop, key, null);
        if (s == null) return def;
        s = s.toLowerCase();
        return s.equals("aktif") || s.equals("true") || s.equals("yes") || s.equals("1") || s.equals("on");
    }

    // ===== Getters =====

    public boolean isEnabled()             { return enabled; }
    public String  getNum()                { return num; }
    public String  getDisplayName()        { return displayName; }
    public String  getIp()                 { return ip; }
    public String  getUser()               { return user; }
    public String  getPass()               { return pass; }
    public String  getSrvHost()            { return srvHost; }
    public int     getSrvPort()            { return srvPort; }
    public boolean isAutoAnswer()          { return autoAnswer; }
    public int     getRingtoneVolume()     { return ringtoneVolume; }
    public String  getRestUrl()            { return restUrl; }
    public String  getRestClientId()       { return restClientId; }
    public String  getRestClientSecret()   { return restClientSecret; }
    public int     getKontakSyncIntervalMinutes() { return kontakSyncIntervalMinutes; }

    public String  getRcUrl()              { return rcUrl; }
    public String  getRcAdminToken()       { return rcAdminToken; }
    public String  getRcAdminUserId()      { return rcAdminUserId; }
    public String  getRcPasswordSecret()   { return rcPasswordSecret; }
    public boolean isRcEnabled() {
        return !rcUrl.isEmpty() && !rcAdminToken.isEmpty() && !rcPasswordSecret.isEmpty();
    }

    public boolean isValid() {
        return enabled && !srvHost.isEmpty() && !num.isEmpty();
    }

    public String summary() {
        return String.format("SIP[enabled=%s host=%s:%d num=%s displayName=%s ip=%s]",
                enabled, srvHost, srvPort, num, displayName, ip);
    }
}
