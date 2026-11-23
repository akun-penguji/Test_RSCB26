package AESsecurity;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

/**
 * One-time migration: baca semua user/admin dari tabel lama (AES),
 * hash password dengan BCrypt, simpan ke tabel users/admins.
 *
 * Jalankan SEKALI sebelum deploy Khanza/simrscb versi tanpa fallback AES.
 *
 * Usage: java -cp "lib/*" AESsecurity.MigrasiBcrypt jdbc:mysql://HOST:3306/DB user pass
 */
public class MigrasiBcrypt {

    private static final int BCRYPT_ROUNDS = 12;

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Usage: java -cp \"lib/*\" AESsecurity.MigrasiBcrypt <jdbc-url> <db-user> <db-pass>");
            System.out.println("Example: java -cp \"lib/*\" AESsecurity.MigrasiBcrypt jdbc:mysql://192.168.8.5:3306/sikcb10102023 client DBSIRS1234");
            return;
        }
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection(args[0], args[1], args[2]);
        System.out.println("Connected to " + args[0]);

        migrateUsers(con);
        migrateAdmins(con);

        con.close();
        System.out.println("\nSelesai. Semua password sudah di-hash BCrypt.");
    }

    private static void migrateUsers(Connection con) throws Exception {
        System.out.println("\n=== Migrasi tabel user → users ===");
        PreparedStatement read = con.prepareStatement(
                "SELECT CONVERT(AES_DECRYPT(id_user,'nur') USING utf8) AS username, " +
                "       CONVERT(AES_DECRYPT(password,'windi') USING utf8) AS plainpass " +
                "FROM user");
        PreparedStatement update = con.prepareStatement(
                "UPDATE users SET password_hash = ?, updated_at = NOW() WHERE username = ?");
        PreparedStatement insert = con.prepareStatement(
                "INSERT INTO users (nik, username, password, password_hash) VALUES (?, ?, ?, ?)");

        ResultSet rs = read.executeQuery();
        int ok = 0, skip = 0, inserted = 0;
        while (rs.next()) {
            String username = rs.getString("username");
            String plainpass = rs.getString("plainpass");
            if (username == null || username.isEmpty() || plainpass == null || plainpass.isEmpty()) {
                skip++;
                continue;
            }
            String hash = BCrypt.hashpw(plainpass, BCrypt.gensalt(BCRYPT_ROUNDS));
            // Coba UPDATE dulu (user sudah ada di tabel users)
            update.setString(1, hash);
            update.setString(2, username);
            int affected = update.executeUpdate();
            if (affected == 0) {
                // User belum ada — INSERT baru
                insert.setString(1, username);
                insert.setString(2, username);
                insert.setString(3, plainpass.substring(0, Math.min(plainpass.length(), 50)));
                insert.setString(4, hash);
                insert.executeUpdate();
                inserted++;
            }
            ok++;
            if (ok % 20 == 0) System.out.printf("  ... %d user di-hash%n", ok);
        }
        rs.close();
        read.close();
        update.close();
        insert.close();
        System.out.printf("  Selesai: %d user di-hash (%d baru di-insert), %d di-skip (null/kosong)%n", ok, inserted, skip);
    }

    private static void migrateAdmins(Connection con) throws Exception {
        System.out.println("\n=== Migrasi tabel admin → admins ===");
        PreparedStatement read = con.prepareStatement(
                "SELECT CONVERT(AES_DECRYPT(usere,'nur') USING utf8) AS username, " +
                "       CONVERT(AES_DECRYPT(passworde,'windi') USING utf8) AS plainpass " +
                "FROM admin");
        PreparedStatement upsert = con.prepareStatement(
                "INSERT INTO admins (username, password_hash) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE password_hash = VALUES(password_hash), updated_at = NOW()");

        ResultSet rs = read.executeQuery();
        int ok = 0;
        while (rs.next()) {
            String username = rs.getString("username");
            String plainpass = rs.getString("plainpass");
            if (username == null || username.isEmpty() || plainpass == null || plainpass.isEmpty()) continue;
            String hash = BCrypt.hashpw(plainpass, BCrypt.gensalt(BCRYPT_ROUNDS));
            upsert.setString(1, username);
            upsert.setString(2, hash);
            upsert.executeUpdate();
            ok++;
        }
        rs.close();
        read.close();
        upsert.close();
        System.out.printf("  Selesai: %d admin di-hash%n", ok);
    }
}
