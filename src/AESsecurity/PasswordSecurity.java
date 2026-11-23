package AESsecurity;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Autentikasi BCrypt murni — tanpa fallback AES.
 *
 * <p>Prasyarat: jalankan {@code MigrasiBcrypt} dulu untuk mengisi
 * tabel {@code users}/{@code admins} dari tabel lama.</p>
 *
 * @author Salim Mulyana — ITSA BSSN hardening 2026
 */
public class PasswordSecurity {

    private static final int BCRYPT_ROUNDS = 12;

    public static String hashPassword(String plaintext) {
        return BCrypt.hashpw(plaintext, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    public static boolean verifyPassword(String plaintext, String bcryptHash) {
        if (bcryptHash == null || bcryptHash.isEmpty() || !bcryptHash.startsWith("$2")) {
            return false;
        }
        try {
            return BCrypt.checkpw(plaintext, bcryptHash);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Login user — cek BCrypt di tabel {@code users}.
     */
    public static boolean authenticateUser(Connection koneksi, String userId, String password) {
        try {
            PreparedStatement ps = koneksi.prepareStatement(
                    "SELECT password_hash FROM users WHERE username=? LIMIT 1");
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hash = rs.getString("password_hash");
                rs.close();
                ps.close();
                return verifyPassword(password, hash);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.err.println("PasswordSecurity.authenticateUser: " + e.getMessage());
        }
        return false;
    }

    /**
     * Login admin — cek BCrypt di tabel {@code admins}.
     */
    public static boolean authenticateAdmin(Connection koneksi, String userId, String password) {
        try {
            PreparedStatement ps = koneksi.prepareStatement(
                    "SELECT password_hash FROM admins WHERE username=? LIMIT 1");
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hash = rs.getString("password_hash");
                rs.close();
                ps.close();
                return verifyPassword(password, hash);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.err.println("PasswordSecurity.authenticateAdmin: " + e.getMessage());
        }
        return false;
    }
}
