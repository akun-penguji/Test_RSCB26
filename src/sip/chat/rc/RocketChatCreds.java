package sip.chat.rc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Generate deterministic credential Rocket.Chat untuk tiap extension.
 *
 * <p>Password tidak disimpan di DB — di-compute on-demand via
 * HMAC-SHA256(secret, "rc-pwd:" + extension) lalu di-base64 dan dipotong.
 * Semua SIMRS client dengan {@code RC_PASSWORD_SECRET} yang sama akan
 * menghasilkan password yang sama untuk extension yang sama.
 *
 * <p>Rotasi secret: ganti {@code RC_PASSWORD_SECRET} di database.xml,
 * lalu re-provision semua user RC (delete + create ulang lewat admin API).
 */
public final class RocketChatCreds {
    private RocketChatCreds() {}

    /**
     * Hitung password RC untuk extension tertentu.
     *
     * @param secret    RC_PASSWORD_SECRET dari database.xml
     * @param extension nomor extension SIP (mis. "201")
     * @return password base64-url-safe panjang 24 char
     */
    public static String passwordFor(String secret, String extension) {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("RC_PASSWORD_SECRET belum di-set di database.xml");
        }
        if (extension == null || extension.isEmpty()) {
            throw new IllegalArgumentException("extension kosong");
        }
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(("rc-pwd:" + extension).getBytes(StandardCharsets.UTF_8));
            String b64 = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
            // Ambil 24 char — cukup kuat (≈ 144 bit entropy), di bawah limit RC default
            return b64.substring(0, 24);
        } catch (Exception e) {
            throw new RuntimeException("Gagal compute HMAC password", e);
        }
    }

    /** Username RC = extension. Bisa di-prefix kalau mau ("ext201"). */
    public static String usernameFor(String extension) {
        return extension;   // 1:1 mapping
    }

    /** Email placeholder per extension — butuh valid-looking format tapi tidak dipakai login. */
    public static String emailFor(String extension) {
        return "ext" + extension + "@simrs.local";
    }
}
