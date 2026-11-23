/*
 * Enkripsi/dekripsi AES-128 CBC untuk konfigurasi (database.xml, dll).
 *
 * HARDENING ITSA BSSN (Okt 2026):
 * - Kunci dan IV sekarang dimuat dari file eksternal `setting/aes.key`
 * - Jika file tidak ada, gunakan nilai bawaan (backward-compatible)
 * - RENCANA JANGKA PANJANG: Migrasi ke AES-256-GCM dengan kunci per-instalasi
 */
package AESsecurity;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author khanzamedia (modified: ITSA hardening 2026)
 */
public class EnkripsiAES {

    private static String key;
    private static String initVector;

    static {
        // Muat kunci dari file eksternal, fallback ke default
        key = loadKeyComponent("AES_KEY", "Bar12345Bar12345");
        initVector = loadKeyComponent("AES_IV", "sayangsamakhanza");
    }

    /**
     * Muat komponen kunci dari:
     * 1. System property (java -DAES_KEY=xxx)
     * 2. Environment variable (AES_KEY=xxx)
     * 3. File setting/aes.key (property file)
     * 4. Nilai default (backward compatible)
     */
    private static String loadKeyComponent(String name, String defaultValue) {
        // 1. System property
        String val = System.getProperty(name);
        if (val != null && !val.isEmpty()) return val;

        // 2. Environment variable
        val = System.getenv(name);
        if (val != null && !val.isEmpty()) return val;

        // 3. File setting/aes.key
        try {
            File keyFile = new File("setting/aes.key");
            if (keyFile.exists()) {
                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream(keyFile)) {
                    props.load(fis);
                }
                val = props.getProperty(name);
                if (val != null && !val.isEmpty()) return val;
            }
        } catch (Exception e) {
            // File tidak bisa dibaca — lanjut ke default
        }

        // 4. Default (backward compatible)
        return defaultValue;
    }

    public static String decrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted.getBytes()));

            return new String(original);
        } catch (Exception ex) {
            System.err.println("EnkripsiAES.decrypt error: " + ex.getMessage());
        }

        return null;
    }

    public static String encrypt(String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            System.err.println("EnkripsiAES.encrypt error: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Periksa apakah masih menggunakan kunci default (untuk audit).
     */
    public static boolean isUsingDefaultKey() {
        return "Bar12345Bar12345".equals(key) && "sayangsamakhanza".equals(initVector);
    }
}
