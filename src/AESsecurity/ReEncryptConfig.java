package AESsecurity;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import java.io.*;
import java.security.SecureRandom;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Tool untuk re-encrypt database.xml dengan kunci AES custom.
 *
 * Alur:
 * 1. Baca database.xml, decrypt semua value terenkripsi pakai kunci LAMA
 * 2. Re-encrypt pakai kunci BARU (random atau user-defined)
 * 3. Tulis database.xml baru + setting/aes.key baru
 *
 * Jalankan: java -cp lib/* AESsecurity.ReEncryptConfig [path/ke/setting]
 */
public class ReEncryptConfig {

    private static final String DEFAULT_KEY = "Bar12345Bar12345";
    private static final String DEFAULT_IV  = "sayangsamakhanza";

    public static void main(String[] args) {
        String settingDir = args.length > 0 ? args[0] : "setting";
        File dbXml = new File(settingDir, "database.xml");
        File aesKeyFile = new File(settingDir, "aes.key");

        if (!dbXml.exists()) {
            System.err.println("database.xml tidak ditemukan di: " + dbXml.getAbsolutePath());
            System.exit(1);
        }

        String oldKey = DEFAULT_KEY;
        String oldIv  = DEFAULT_IV;

        if (aesKeyFile.exists()) {
            try {
                Properties p = new Properties();
                p.load(new FileInputStream(aesKeyFile));
                String k = p.getProperty("AES_KEY");
                String v = p.getProperty("AES_IV");
                if (k != null && !k.isEmpty()) oldKey = k;
                if (v != null && !v.isEmpty()) oldIv = v;
                System.out.println("Kunci LAMA dimuat dari: " + aesKeyFile.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("Gagal baca aes.key, pakai default: " + e.getMessage());
            }
        } else {
            System.out.println("aes.key tidak ada, pakai kunci default (Bar12345Bar12345 / sayangsamakhanza)");
        }

        String newKey;
        String newIv;

        System.out.println("\nPilihan kunci BARU:");
        System.out.println("1. Generate random (recommended)");
        System.out.println("2. Input manual (harus tepat 16 karakter)");
        System.out.print("Pilih [1/2]: ");

        try (Scanner sc = new Scanner(System.in)) {
            String pilihan = sc.nextLine().trim();
            if ("2".equals(pilihan)) {
                System.out.print("AES_KEY baru (16 char): ");
                newKey = sc.nextLine().trim();
                System.out.print("AES_IV baru  (16 char): ");
                newIv = sc.nextLine().trim();
                if (newKey.length() != 16 || newIv.length() != 16) {
                    System.err.println("ERROR: Key dan IV harus tepat 16 karakter (AES-128).");
                    System.exit(1);
                }
            } else {
                newKey = generateRandom16();
                newIv  = generateRandom16();
                System.out.println("Key baru: " + newKey);
                System.out.println("IV  baru: " + newIv);
            }

            System.out.println("\n--- Proses Re-encrypt ---");
            System.out.println("Old key: " + oldKey);
            System.out.println("New key: " + newKey);

            String content = readFile(dbXml);

            int count = 0;
            StringBuilder result = new StringBuilder();
            int pos = 0;

            while (pos < content.length()) {
                int tagStart = content.indexOf("<entry key=\"", pos);
                if (tagStart == -1) {
                    result.append(content.substring(pos));
                    break;
                }

                result.append(content, pos, tagStart);

                int keyStart = tagStart + "<entry key=\"".length();
                int keyEnd = content.indexOf("\"", keyStart);
                String entryKey = content.substring(keyStart, keyEnd);

                int gtAfterKey = content.indexOf(">", keyEnd);
                boolean selfClosing = content.charAt(gtAfterKey - 1) == '/';

                if (selfClosing) {
                    int endPos = gtAfterKey + 1;
                    result.append(content, tagStart, endPos);
                    pos = endPos;
                    continue;
                }

                int valueStart = gtAfterKey + 1;
                int closeTag = content.indexOf("</entry>", valueStart);
                String value = content.substring(valueStart, closeTag);
                int endPos = closeTag + "</entry>".length();

                if (looksBase64(value)) {
                    String decrypted = decryptWith(value, oldKey, oldIv);
                    if (decrypted != null) {
                        String reEncrypted = encryptWith(decrypted, newKey, newIv);
                        if (reEncrypted != null) {
                            result.append(content, tagStart, valueStart);
                            result.append(reEncrypted);
                            result.append("</entry>");
                            count++;
                            System.out.println("  OK  " + entryKey + ": " + value.substring(0, Math.min(20, value.length())) + "... -> " + reEncrypted.substring(0, Math.min(20, reEncrypted.length())) + "...");
                            pos = endPos;
                            continue;
                        }
                    }
                    System.out.println("  SKIP " + entryKey + " (decrypt gagal, biarkan apa adanya)");
                }

                result.append(content, tagStart, endPos);
                pos = endPos;
            }

            File backup = new File(dbXml.getAbsolutePath() + ".bak." + System.currentTimeMillis());
            copyFile(dbXml, backup);
            System.out.println("\nBackup: " + backup.getAbsolutePath());

            writeFile(dbXml, result.toString());
            System.out.println("database.xml ditulis ulang.");

            Properties newProps = new Properties();
            newProps.setProperty("AES_KEY", newKey);
            newProps.setProperty("AES_IV", newIv);
            try (FileOutputStream fos = new FileOutputStream(aesKeyFile)) {
                newProps.store(fos, "AES key — generated by ReEncryptConfig " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
            }
            System.out.println("aes.key ditulis: " + aesKeyFile.getAbsolutePath());

            System.out.println("\n=== Selesai: " + count + " value di-reencrypt ===");
            System.out.println("PENTING: Salin aes.key ke SEMUA workstation yang menjalankan Khanza.");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Saringan kasar: hanya memastikan value berpeluang jadi Base64. Penentu
     * sebenarnya adalah percobaan dekripsi di pemanggil — pola saja tidak cukup.
     *
     * Jangan menolak value yang mengandung '/' atau '+': keduanya karakter sah
     * Base64, dan menolaknya pernah membuat PAS (password DB) terlewat.
     * Panjang tidak diwajibkan kelipatan 4 karena ada entri lama yang kurang
     * karakter padding, mis. GOWA_DEVICE_ID_SKM.
     */
    private static boolean looksBase64(String value) {
        if (value == null || value.length() < 22) return false;
        return value.matches("^[A-Za-z0-9+/]+={0,2}$");
    }

    private static String decryptWith(String encrypted, String key, String iv) {
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted.getBytes()));
            String result = new String(original, "UTF-8");
            if (result.isEmpty()) return null;
            for (char c : result.toCharArray()) {
                if (c < 0x20 && c != '\t' && c != '\n' && c != '\r') return null;
                if (c == 0xFFFD) return null;   // replacement char = bukan UTF-8 valid
            }
            return result;
        } catch (Exception ex) {
            return null;
        }
    }

    private static String encryptWith(String plaintext, String key, String iv) {
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plaintext.getBytes("UTF-8"));
            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            return null;
        }
    }

    private static String generateRandom16() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom rng = new SecureRandom();
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            sb.append(chars.charAt(rng.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private static String readFile(File f) throws IOException {
        byte[] data = new byte[(int) f.length()];
        try (FileInputStream fis = new FileInputStream(f)) { fis.read(data); }
        return new String(data, "UTF-8");
    }

    private static void writeFile(File f, String content) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(f)) {
            fos.write(content.getBytes("UTF-8"));
        }
    }

    private static void copyFile(File src, File dst) throws IOException {
        try (FileInputStream in = new FileInputStream(src);
             FileOutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
        }
    }
}
