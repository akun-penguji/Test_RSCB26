package bridging;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.util.Base64;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import fungsi.koneksiDB;
import javax.swing.JOptionPane;

public class ApiMirthConnect {
    private String baseUrl;
    private String username;
    private String password;
    private Connection koneksi = koneksiDB.condb();
    
    public ApiMirthConnect() {
        this.baseUrl = "http://192.168.88.133:8080";  // Changed to HTTP port 8080 for Mirth 4.4.1
        this.username = "admin";
        this.password = "rosmayanti";
        initializeSSL();
    }
    
    // Method untuk test different endpoints
    public boolean testConnection() {
        try {
            String[] testEndpoints = {
                "/api/channels",
                "/api/server/info", 
                "/api/system/info",
                "/webstart/client.jnlp"
            };
            
            for (String endpoint : testEndpoints) {
                System.out.println("Testing endpoint: " + baseUrl + endpoint);
                URL url = new URL(baseUrl + endpoint);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                
                String auth = username + ":" + password;
                byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
                String authHeader = "Basic " + new String(encodedAuth);
                connection.setRequestProperty("Authorization", authHeader);
                
                connection.setConnectTimeout(10000);
                int responseCode = connection.getResponseCode();
                System.out.println("Response: " + responseCode + " for " + endpoint);
                
                if (responseCode == 200) {
                    System.out.println("Successfully connected to: " + endpoint);
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    private void initializeSSL() {
        try {
            // Create a trust manager that accepts all certificates
            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };
            
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            
            // Create hostname verifier that accepts all hostnames
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            
        } catch (Exception e) {
            System.err.println("Error initializing SSL: " + e.getMessage());
        }
    }
    
    public boolean kirimPermintaanLab(String noPermintaan) {
        try {
            // Get lab request data from database
            String jsonData = getLabRequestData(noPermintaan);
            if (jsonData == null) {
                JOptionPane.showMessageDialog(null, "Data permintaan lab tidak ditemukan!");
                return false;
            }
            
            // Try different endpoints until one works (optimized for Mirth 4.4.1)
            String[] endpoints = {
                "/lab",  // Direct channel endpoint (Mirth 4.4.1 standard)
                "/channels/lab",
                "/lab-channel",
                "/api/channels/lab-channel/messages",  // REST API fallback
                "/api/messages"
            };
            
            for (String endpoint : endpoints) {
                System.out.println("Trying endpoint: " + baseUrl + endpoint);
                if (sendToEndpoint(endpoint, jsonData, noPermintaan)) {
                    return true;
                }
            }
            
            // If all endpoints fail, show error
            JOptionPane.showMessageDialog(null, "Tidak dapat mengirim ke Mirth Connect. Periksa konfigurasi channel.");
            updateMirthStatus(noPermintaan, "FAILED", "All endpoints failed");
            return false;
            
        } catch (Exception e) {
            System.err.println("Error sending data to Mirth Connect: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error mengirim data: " + e.getMessage());
            try {
                updateMirthStatus(noPermintaan, "FAILED", "EXCEPTION: " + e.getMessage());
            } catch (Exception updateEx) {
                System.err.println("Error updating status: " + updateEx.getMessage());
            }
            return false;
        }
    }
    
    private boolean sendToEndpoint(String endpoint, String jsonData, String noPermintaan) {
        try {
            String fullUrl = baseUrl + endpoint;
            
            URL url = new URL(fullUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            // Set request method and headers
            connection.setRequestMethod("POST");
            
            // Try different content types for different endpoints
            if (endpoint.startsWith("/api/")) {
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
            } else {
                // For direct channel endpoints, try plain text or XML
                connection.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");
                connection.setRequestProperty("Accept", "*/*");
            }
            
            connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            
            // Set basic authentication
            String auth = username + ":" + password;
            byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
            String authHeader = "Basic " + new String(encodedAuth);
            connection.setRequestProperty("Authorization", authHeader);
            
            // Enable output and send data
            connection.setDoOutput(true);
            connection.setConnectTimeout(15000); // 15 seconds - shorter for testing multiple endpoints
            connection.setReadTimeout(30000); // 30 seconds
            
            try (OutputStream outputStream = connection.getOutputStream()) {
                byte[] input = jsonData.getBytes("utf-8");
                outputStream.write(input, 0, input.length);
            }
            
            // Get response
            int responseCode = connection.getResponseCode();
            
            if (responseCode >= 200 && responseCode < 300) {
                System.out.println("Successfully sent to: " + endpoint + " (Response: " + responseCode + ")");
                JOptionPane.showMessageDialog(null, "Data permintaan lab berhasil dikirim ke Mirth Connect!");
                updateMirthStatus(noPermintaan, "SENT", "SUCCESS - Endpoint: " + endpoint + " Response: " + responseCode);
                return true;
            } else {
                // Read error response
                String errorResponse = "";
                try (BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream()))) {
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        errorResponse += line;
                    }
                } catch (Exception e) {
                    errorResponse = "Unable to read error response";
                }
                
                System.err.println("Failed endpoint " + endpoint + ". Status: " + responseCode + ", Error: " + errorResponse);
                return false; // Try next endpoint
            }
            
        } catch (Exception e) {
            System.err.println("Error with endpoint " + endpoint + ": " + e.getMessage());
            return false; // Try next endpoint
        }
    }
    
    private String getLabRequestData(String noPermintaan) {
        try {
            String sql = "SELECT p.no_rawat, p.tgl_permintaan, p.jam_permintaan, " +
                        "p.informasi_tambahan, p.diagnosa_klinis, p.dokter_perujuk, " +
                        "r.no_rkm_medis, ps.nm_pasien, ps.jk, ps.tmp_lahir, ps.tgl_lahir, " +
                        "jp.nm_perawatan " +
                        "FROM permintaan_lab p " +
                        "JOIN reg_periksa r ON p.no_rawat = r.no_rawat " +
                        "JOIN pasien ps ON r.no_rkm_medis = ps.no_rkm_medis " +
                        "JOIN permintaan_pemeriksaan_lab ppl ON p.noorder = ppl.noorder " +
                        "JOIN jns_perawatan_lab jp ON ppl.kd_jenis_prw = jp.kd_jenis_prw " +
                        "WHERE p.noorder = ? " +
                        "ORDER BY ppl.kd_jenis_prw";
            
            PreparedStatement ps = koneksi.prepareStatement(sql);
            ps.setString(1, noPermintaan);
            ResultSet rs = ps.executeQuery();
            
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");
            jsonBuilder.append("\"messageType\":\"LAB_REQUEST\",");
            jsonBuilder.append("\"noPermintaan\":\"").append(escapeJson(noPermintaan)).append("\",");
            
            boolean hasData = false;
            StringBuilder pemeriksaanBuilder = new StringBuilder();
            pemeriksaanBuilder.append("\"daftarPemeriksaan\":[");
            
            boolean firstPemeriksaan = true;
            while (rs.next()) {
                if (!hasData) {
                    // Patient and request info (set only once)
                    jsonBuilder.append("\"noRawat\":\"").append(escapeJson(rs.getString("no_rawat"))).append("\",");
                    jsonBuilder.append("\"noRM\":\"").append(escapeJson(rs.getString("no_rkm_medis"))).append("\",");
                    jsonBuilder.append("\"namaPasien\":\"").append(escapeJson(rs.getString("nm_pasien"))).append("\",");
                    jsonBuilder.append("\"jenisKelamin\":\"").append(escapeJson(rs.getString("jk"))).append("\",");
                    jsonBuilder.append("\"tempatLahir\":\"").append(escapeJson(rs.getString("tmp_lahir"))).append("\",");
                    jsonBuilder.append("\"tanggalLahir\":\"").append(escapeJson(rs.getString("tgl_lahir"))).append("\",");
                    jsonBuilder.append("\"tanggalPermintaan\":\"").append(escapeJson(rs.getString("tgl_permintaan"))).append("\",");
                    jsonBuilder.append("\"jamPermintaan\":\"").append(escapeJson(rs.getString("jam_permintaan"))).append("\",");
                    jsonBuilder.append("\"dokterPerujuk\":\"").append(escapeJson(rs.getString("dokter_perujuk"))).append("\",");
                    jsonBuilder.append("\"informasiTambahan\":\"").append(escapeJson(rs.getString("informasi_tambahan"))).append("\",");
                    jsonBuilder.append("\"diagnosisKlinis\":\"").append(escapeJson(rs.getString("diagnosa_klinis"))).append("\",");
                    jsonBuilder.append("\"timestamp\":").append(System.currentTimeMillis()).append(",");
                    hasData = true;
                }
                
                // Add each examination to array
                if (!firstPemeriksaan) {
                    pemeriksaanBuilder.append(",");
                }
                pemeriksaanBuilder.append("{");
                pemeriksaanBuilder.append("\"namaPemeriksaan\":\"").append(escapeJson(rs.getString("nm_perawatan"))).append("\"");
                pemeriksaanBuilder.append("}");
                firstPemeriksaan = false;
            }
            
            if (!hasData) {
                rs.close();
                ps.close();
                return null;
            }
            
            pemeriksaanBuilder.append("]");
            jsonBuilder.append(pemeriksaanBuilder.toString());
            jsonBuilder.append("}");
            
            rs.close();
            ps.close();
            
            return jsonBuilder.toString();
            
        } catch (Exception e) {
            System.err.println("Error getting lab request data: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\b", "\\b")
                   .replace("\f", "\\f")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    private void updateMirthStatus(String noPermintaan, String status, String message) {
        try {
            // First check if record exists
            String checkSql = "SELECT noorder FROM permintaan_lab_mirth WHERE noorder = ?";
            PreparedStatement checkPs = koneksi.prepareStatement(checkSql);
            checkPs.setString(1, noPermintaan);
            ResultSet checkRs = checkPs.executeQuery();
            
            if (checkRs.next()) {
                // Update existing record
                String updateSql = "UPDATE permintaan_lab_mirth SET status = ?, message = ?, " +
                                  "last_update = NOW() WHERE noorder = ?";
                PreparedStatement updatePs = koneksi.prepareStatement(updateSql);
                updatePs.setString(1, status);
                updatePs.setString(2, message);
                updatePs.setString(3, noPermintaan);
                updatePs.executeUpdate();
                updatePs.close();
            } else {
                // Insert new record
                String insertSql = "INSERT INTO permintaan_lab_mirth (noorder, status, message, " +
                                  "created_at, last_update) VALUES (?, ?, ?, NOW(), NOW())";
                PreparedStatement insertPs = koneksi.prepareStatement(insertSql);
                insertPs.setString(1, noPermintaan);
                insertPs.setString(2, status);
                insertPs.setString(3, message);
                insertPs.executeUpdate();
                insertPs.close();
            }
            
            checkRs.close();
            checkPs.close();
            
        } catch (Exception e) {
            System.err.println("Error updating Mirth status: " + e.getMessage());
        }
    }
}