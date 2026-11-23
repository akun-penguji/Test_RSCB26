package bridging;

import fungsi.koneksiDB;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;

public class EsignService {

    public static boolean prosesTTE(String filePath, String nik, String passphrase, String tagKoordinat, String linkQR, String location, String reason) {
        return prosesTTE(filePath, filePath, nik, passphrase, tagKoordinat, linkQR, location, reason);
    }

    public static boolean prosesTTE(String inputPath, String outputPath, String nik, String passphrase, String tagKoordinat, String linkQR, String location, String reason) {
        System.out.println("Memulai proses TTE input: " + inputPath + " -> output: " + outputPath);
        System.out.println("NIK: " + nik + ", Tag: " + tagKoordinat);

        try {
            // Bypass SSL checks
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) { }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) { }
                }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

            // 1. Siapkan JSON Payload
            ObjectMapper mapper = new ObjectMapper();
            com.fasterxml.jackson.databind.node.ObjectNode json = mapper.createObjectNode();
            
            String fileValue = "";
            if (inputPath.startsWith("http")) {
                fileValue = inputPath; // Middleware will download from this URL
            } else {
                File file = new File(inputPath);
                if (!file.exists()) {
                    System.out.println("File tidak ditemukan: " + inputPath);
                    return false;
                }
                fileValue = Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
            }
            
            json.put("file", fileValue);
            json.put("nik", nik);
            json.put("passphrase", passphrase);
            json.put("tampilan", "visible");
            json.put("image", "false"); 
            json.put("linkQR", linkQR); 
            json.put("width", "55");
            json.put("height", "55");
            json.put("tag_koordinat", tagKoordinat);
            json.put("location", location);
            json.put("reason", reason);

            String jsonString = mapper.writeValueAsString(json);

            // 2. Kirim ke Middleware PHP
            String urlString = koneksiDB.URLAKSESFILEESIGN(); 
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000); // 30 seconds connection timeout
            conn.setReadTimeout(60000);    // 60 seconds read timeout
            
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("username", koneksiDB.USERNAMEAPIESIGN());
            conn.setRequestProperty("password", koneksiDB.PASSAPIESIGN());
            conn.setRequestProperty("url", koneksiDB.URLAPIESIGN());

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 3. Terima & Proses Respon
            int responseCode = conn.getResponseCode();
            java.io.InputStream is = (responseCode >= 200 && responseCode < 300) ? conn.getInputStream() : conn.getErrorStream();
            if (is == null) is = conn.getInputStream(); // Fallback
            
            StringBuilder response = new StringBuilder();
            if (is != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }
            }

            if (responseCode == 200) {
                // Remove UTF-8 BOM if present
                String responseStr = response.toString();
                if (responseStr.startsWith("\uFEFF")) {
                    responseStr = responseStr.substring(1);
                }
                
                JsonNode resJson = mapper.readTree(responseStr);
                String signedBase64 = null;
                
                System.out.println("DEBUG: Response JSON keys: " + resJson.fieldNames());
                
                if (resJson.has("response")) {
                     signedBase64 = resJson.path("response").asText();
                     System.out.println("DEBUG: Found 'response' field, length: " + signedBase64.length());
                } else if (resJson.has("file")) {
                     signedBase64 = resJson.get("file").asText();
                     System.out.println("DEBUG: Found 'file' field, length: " + signedBase64.length());
                }
                
                if (signedBase64 != null && !signedBase64.isEmpty()) {
                    System.out.println("DEBUG: Base64 content length: " + signedBase64.length());
                    byte[] signedBytes = Base64.getDecoder().decode(signedBase64);
                    System.out.println("DEBUG: Decoded bytes length: " + signedBytes.length);
                    // Ensure parent directory exists
                    File outFile = new File(outputPath);
                    if (outFile.getParentFile() != null) {
                        outFile.getParentFile().mkdirs();
                    }
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        fos.write(signedBytes);
                    }
                    System.out.println("Sukses simpan signed file ke: " + outputPath);
                    return true;
                } else {
                    System.out.println("DEBUG: signedBase64 is null or empty!");
                    System.out.println("DEBUG: Full response: " + responseStr);
                }
            } else {
                System.out.println("Gagal koneksi ke server. Response Code: " + responseCode);
                System.out.println("Response Body: " + response.toString());
                try {
                    JsonNode resJson = mapper.readTree(response.toString());
                    if (resJson.has("metadata") && resJson.get("metadata").has("message")) {
                        System.out.println("Pesan Error: " + resJson.get("metadata").get("message").asText());
                    }
                } catch (Exception ex) {}
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception in prosesTTE: " + e.getMessage());
        }
        return false;
    }
}
