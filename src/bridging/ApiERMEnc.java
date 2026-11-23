package bridging;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

/**
 * ApiERMEnc - wrapper untuk:
 * 1) GZIP -> Base64
 * 2) Menggunakan ApiBPJSEnc.generateKey(...) untuk mendapat SecretKeySpec + IvParameterSpec
 * 3) Memanggil ApiBPJSEnc.encrypt(...) untuk AES/CBC/PKCS5Padding + Base64
 *
 * Compatible dengan ApiBPJSEnc.decrypt(...)
 */
public class ApiERMEnc {

    private final ApiERM apiERM; // dipakai bila ingin ambil Consid/Key/KodeRS dari koneksiDB lewat ApiERM

    public ApiERMEnc() {
        this.apiERM = new ApiERM();
    }

    // === 1) GZIP -> BASE64 ===
    public String gzipToBase64(String input) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(input.getBytes(StandardCharsets.UTF_8));
        gzip.close();
        byte[] gz = bos.toByteArray();
        return Base64.getEncoder().encodeToString(gz);
    }

    // === 2) Encrypt (menggunakan ApiBPJSEnc) ===
    // combinedKeyString harus = consid + secretKey + kodeFaskes (sama persis dengan decrypt)
    public String encryptUsingApiBPJSEnc(String combinedKeyString, String plainBase64) throws Exception {
        // generateKey -> menghasilkan SecretKeySpec + IvParameterSpec (ApiBPJSAesKeySpec)
        ApiBPJSAesKeySpec spec = ApiBPJSEnc.generateKey(combinedKeyString);

        // ApiBPJSEnc.encrypt expects (String cipherText, SecretKeySpec key, IvParameterSpec iv)
        // Here plainBase64 is the data to be encrypted (already base64 of gzipped JSON)
        String encryptedBase64 = ApiBPJSEnc.encrypt(plainBase64, spec.getKey(), spec.getIv());
        return encryptedBase64;
    }

    // === 3) Full pipeline: GZIP -> Base64 -> Encrypt -> Base64 ===
    // consid, secretKeyParam, kodeFaskes must be provided (or you can call with apiERM values)
    public String processData(String consid, String secretKeyParam, String kodeFaskes, String rawJson) throws Exception {
        // step 1: gzip -> base64
        String gzipBase64 = gzipToBase64(rawJson);

        // step 2: build combined key string (sama seperti decrypt)
        String combinedKey = consid + secretKeyParam + kodeFaskes;

        // step 3: encrypt the gzipBase64 using ApiBPJSEnc (AES/CBC/PKCS5Padding) => base64 cipher
        String finalEncrypted = encryptUsingApiBPJSEnc(combinedKey, gzipBase64);

        return finalEncrypted;
    }
}
