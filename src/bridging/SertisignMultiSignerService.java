/*
 * Service untuk integrasi Sertisign Multi-Signer API
 */
package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.koneksiDB;
import fungsi.sekuel;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

/**
 * SertisignMultiSignerService - Service untuk TTE Multi-Signer
 * 
 * @author RS Trimitra
 */
public class SertisignMultiSignerService {
    
    private final sekuel Sequel = new sekuel();
    private final Connection koneksi = koneksiDB.condb();
    private final ObjectMapper mapper = new ObjectMapper();
    
    /**
     * Get signers from berkas_digital_perawatan for a specific no_rawat
     * @param noRawat Nomor rawat pasien
     * @return List of SignerConfig
     */
    public List<SignerConfig> getSignersFromBerkas(String noRawat) {
        List<SignerConfig> signers = new ArrayList<>();
        
        try {
            // Query distinct nip from berkas_digital_perawatan
            // Exclude invalid values: NULL, empty string, dash (-)
            String sql = "SELECT DISTINCT nip FROM berkas_digital_perawatan " +
                        "WHERE no_rawat = ? " +
                        "AND nip IS NOT NULL " +
                        "AND nip != '' " +
                        "AND nip != '-'";
            
            Connection currentConn = koneksi;
            if (currentConn == null || currentConn.isClosed()) {
                System.out.println("DEBUG: Connection is NULL or CLOSED. Attempting to get new connection.");
                currentConn = koneksiDB.condb();
            }
            
            System.out.println("DEBUG: Connection status: " + (currentConn != null ? "ACTIVE" : "NULL"));
            
            PreparedStatement ps = currentConn.prepareStatement(sql);
            ps.setString(1, noRawat);
            ResultSet rs = ps.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                count++;
                String nip = rs.getString("nip");
                System.out.println("DEBUG: Row " + count + " raw nip from DB: '" + nip + "'");
                
                if (nip != null) nip = nip.trim();
                if (nip == null || nip.isEmpty() || nip.equals("-")) {
                    System.out.println("DEBUG: Skipping invalid nip: '" + nip + "'");
                    continue;
                }
                
                SignerConfig signer = getSignerInfo(nip);
                if (signer != null) {
                    System.out.println("DEBUG: getSignerInfo returned VALID signer for " + nip + ": " + signer.getSignerName());
                    if (signer.isValid()) {
                        signers.add(signer);
                    } else {
                        System.out.println("DEBUG: Signer found but NOT valid (empty ID/Email?): " + signer);
                    }
                } else {
                    System.out.println("DEBUG: getSignerInfo returned NULL for " + nip + ". Check dokter/petugas table!");
                }
            }
            System.out.println("DEBUG: getSignersFromBerkas COMPLETED. Found " + count + " rows. Total valid signers: " + signers.size());
            
            rs.close();
            ps.close();
            
        } catch (Exception e) {
            System.err.println("Error getting signers from berkas: " + e.getMessage());
            e.printStackTrace();
        }
        
        return signers;
    }
    
    /**
     * Get signer information from dokter or petugas table
     * @param nip NIP/Kode dokter atau petugas
     * @return SignerConfig or null if not found
     */
    private SignerConfig getSignerInfo(String nip) {
        if (nip == null) return null;
        nip = nip.trim(); // Robustness: trim the input NIP
        System.out.println("DEBUG: getSignerInfo for NIP: '" + nip + "'");
        try {
            Connection currentConn = koneksi;
            if (currentConn == null || currentConn.isClosed()) {
                currentConn = koneksiDB.condb();
            }
            
            // Try dokter table first
            String sql = "SELECT kd_dokter, nm_dokter, email, tag_signer " +
                        "FROM dokter WHERE TRIM(kd_dokter) = ?";
            PreparedStatement ps = currentConn.prepareStatement(sql);
            ps.setString(1, nip);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                SignerConfig signer = new SignerConfig();
                signer.setSignerType("dokter");
                signer.setSignerId(rs.getString("kd_dokter") != null ? rs.getString("kd_dokter").trim() : "");
                signer.setSignerName(rs.getString("nm_dokter") != null ? rs.getString("nm_dokter").trim() : "");
                signer.setEmail(rs.getString("email") != null ? rs.getString("email").trim() : "");
                signer.setTagSigner(rs.getString("tag_signer") != null ? rs.getString("tag_signer").trim() : "");
                System.out.println("DEBUG: getSignerInfo SUCCESS found in dokter table: " + signer.getSignerName() + " (ID: '" + signer.getSignerId() + "')");
                rs.close();
                ps.close();
                return signer;
            }
            rs.close();
            ps.close();
            
            // Try petugas table
            sql = "SELECT nip, nama, email, tag_signer FROM petugas WHERE TRIM(nip) = ?";
            ps = currentConn.prepareStatement(sql);
            ps.setString(1, nip);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                SignerConfig signer = new SignerConfig();
                signer.setSignerType("petugas");
                signer.setSignerId(rs.getString("nip") != null ? rs.getString("nip").trim() : "");
                signer.setSignerName(rs.getString("nama") != null ? rs.getString("nama").trim() : "");
                signer.setEmail(rs.getString("email") != null ? rs.getString("email").trim() : "");
                signer.setTagSigner(rs.getString("tag_signer") != null ? rs.getString("tag_signer").trim() : "");
                System.out.println("DEBUG: getSignerInfo SUCCESS found in petugas table: " + signer.getSignerName() + " (ID: '" + signer.getSignerId() + "')");
                rs.close();
                ps.close();
                return signer;
            }
            System.out.println("DEBUG: getSignerInfo FAILED. NIP '" + nip + "' NOT found in dokter or petugas table.");
            rs.close();
            ps.close();
            
        } catch (Exception e) {
            System.err.println("Error getting signer info: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Send PDF to Sertisign for multi-signer TTE
     * @param pdfFile File PDF yang akan di-TTE
     * @param signers List of signer configuration
     * @param qrWidth Width of QR code
     * @param qrHeight Height of QR code
     * @param rsName Nama rumah sakit
     * @param rsKabupaten Kabupaten rumah sakit
     * @return Transaction ID from Sertisign
     */
    public String sendMultiSignerRequest(
        File pdfFile,
        List<SignerConfig> signers,
        int qrWidth,
        int qrHeight,
        String rsName,
        String rsKabupaten
    ) throws Exception {
        
        if (signers == null || signers.isEmpty()) {
            throw new Exception("No signers provided");
        }
        
        // Validate all signers
        for (SignerConfig signer : signers) {
            if (!signer.isValid()) {
                throw new Exception("Invalid signer: " + signer.getSignerName() + 
                                  " (missing email or tag_signer)");
            }
        }
        
        CloseableHttpClient httpClient = HttpClients.createDefault();
        
        // Construct URL properly - URLAPISERTISIGN already contains full path to signing-qr
        // We need to replace the last segment with our multi-signing endpoint
        String baseUrl = koneksiDB.URLAPISERTISIGN();
        
        // Remove trailing slash if exists
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        
        // Replace the last path segment (signing-qr) with our endpoint
        // Expected: https://connect.sertisign.id/v2/poa/signing-qr -> https://connect.sertisign.id/v2/poa/multiple-signing-qr-multiple
        String fullUrl;
        if (baseUrl.endsWith("/signing-qr")) {
            fullUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/signing-qr")) + "/multiple-signing-qr-multiple";
        } else if (baseUrl.endsWith("/v2/poa")) {
            fullUrl = baseUrl + "/multiple-signing-qr-multiple";
        } else {
            // Fallback: just append
            fullUrl = baseUrl + "/v2/poa/multiple-signing-qr-multiple";
        }
        
        HttpPost post = new HttpPost(fullUrl);
        post.addHeader("apikey", koneksiDB.APIKEYSERTISIGN());
        post.addHeader("Accept", "application/json");
        
        // Build signer JSON array
        ArrayNode signersArray = mapper.createArrayNode();
        
        for (SignerConfig signer : signers) {
            // Generate QR value - format: "Dokumen ini ditanda tangani oleh Dokter/Petugas XXX di [nama RS]"
            String signerType = signer.getSignerType().equals("dokter") ? "Dokter" : "Petugas";
            String qrValue = "Dokumen ini ditanda tangani oleh " + signerType + " " + 
                           signer.getSignerName() + " di " + rsName;
            
            signer.setQrValue(qrValue);
            
            ObjectNode signerNode = mapper.createObjectNode();
            signerNode.put("identifier", signer.getEmail());
            signerNode.put("keyword", signer.getTagSigner());
            signerNode.put("qrValue", qrValue);
            signersArray.add(signerNode);
        }
        
        String signersJson = mapper.writeValueAsString(signersArray);
        
        // Debug logging after variables are created
        System.out.println("🔗 Sertisign API URL: " + fullUrl);
        System.out.println("📋 Signers JSON: " + signersJson);
        
        // Build multipart request - signer must be JSON string as per Postman collection
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create()
            .addBinaryBody("document", pdfFile, ContentType.APPLICATION_PDF, pdfFile.getName())
            .addTextBody("w", String.valueOf(qrWidth))
            .addTextBody("h", String.valueOf(qrHeight))
            .addTextBody("signer", signersJson);
        
        HttpEntity entity = entityBuilder.build();
        post.setEntity(entity);
        
        System.out.println("📤 Sending request to Sertisign...");
        CloseableHttpResponse response = httpClient.execute(post);
        
        try {
            System.out.println("📥 Response code: " + response.getCode());
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("📄 Response body: " + responseBody);
            
            if (response.getCode() == 200) {
                JsonNode root = mapper.readTree(responseBody);
                String transactionId = root.path("data").path("transaction_id").asText();
                
                if (transactionId == null || transactionId.isEmpty()) {
                    throw new Exception("Transaction ID not found in response");
                }
                
                return transactionId;
            } else {
                throw new Exception("Sertisign API Error " + response.getCode() + ": " + responseBody);
            }
        } finally {
            response.close();
            httpClient.close();
        }
    }
    
    /**
     * Download signed PDF from Sertisign
     * @param transactionId Transaction ID dari Sertisign
     * @param outputPath Path untuk menyimpan file
     * @param maxRetries Maximum retry attempts
     * @param retryDelay Delay between retries in milliseconds
     * @return true if download successful
     */
    public boolean downloadSignedPdf(String transactionId, String outputPath, 
                                     int maxRetries, int retryDelay) {
        
        String downloadUrl = koneksiDB.URLDOKUMENSERTISIGN() + "/" + transactionId + ".pdf";
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                System.out.println("Download attempt " + attempt + "/" + maxRetries + ": " + downloadUrl);
                
                URL url = new URL(downloadUrl);
                ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                FileOutputStream fos = new FileOutputStream(outputPath);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                fos.close();
                rbc.close();
                
                // Verify file exists and has content
                File downloadedFile = new File(outputPath);
                if (downloadedFile.exists() && downloadedFile.length() > 0) {
                    System.out.println("Download successful: " + outputPath + 
                                     " (" + downloadedFile.length() + " bytes)");
                    return true;
                }
                
                System.out.println("Downloaded file is empty, retrying...");
                Thread.sleep(retryDelay);
                
            } catch (Exception e) {
                System.err.println("Download attempt " + attempt + " failed: " + e.getMessage());
                
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        return false;
    }
    
    /**
     * Save TTE status to database
     * @param noRawat Nomor rawat
     * @param noSep Nomor SEP
     * @param transactionId Transaction ID dari Sertisign
     * @param documentName Nama dokumen
     * @param urlCallback URL callback
     * @param createdBy User yang membuat
     * @return true if successful
     */
    public boolean saveStatusTte(String noRawat, String noSep, String transactionId,
                                 String documentName, String urlCallback, String createdBy) {
        try {
            // Delete existing record for this no_rawat and jenis_berkas
            Sequel.queryu("DELETE FROM status_tte WHERE no_rawat = ? AND jenis_berkas = 'merged_berkas'", 
                         noRawat);
            
            // Insert new record using direct SQL
            String sql = "INSERT INTO status_tte (no_rawat, no_sep, transaction_id, document_name, " +
                        "jenis_berkas, status, url_callback, created_by) " +
                        "VALUES (?, ?, ?, ?, 'merged_berkas', 'PENDING', ?, ?)";
            
            PreparedStatement ps = koneksi.prepareStatement(sql);
            ps.setString(1, noRawat);
            ps.setString(2, noSep);
            ps.setString(3, transactionId);
            ps.setString(4, documentName);
            ps.setString(5, urlCallback);
            ps.setString(6, createdBy);
            int result = ps.executeUpdate();
            ps.close();
            return result > 0;
                           
        } catch (Exception e) {
            System.err.println("Error saving status TTE: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Save signers to database
     * @param transactionId Transaction ID
     * @param signers List of signers
     * @return true if successful
     */
    public boolean saveSigners(String transactionId, List<SignerConfig> signers) {
        try {
            for (SignerConfig signer : signers) {
                String sql = "INSERT INTO status_tte_signers (transaction_id, signer_type, " +
                           "signer_id, signer_name, signer_email, tag_signer, qr_value) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?)";
                
                PreparedStatement ps = koneksi.prepareStatement(sql);
                ps.setString(1, transactionId);
                ps.setString(2, signer.getSignerType());
                ps.setString(3, signer.getSignerId());
                ps.setString(4, signer.getSignerName());
                ps.setString(5, signer.getEmail());
                ps.setString(6, signer.getTagSigner());
                ps.setString(7, signer.getQrValue());
                ps.executeUpdate();
                ps.close();
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error saving signers: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update TTE status
     * @param transactionId Transaction ID
     * @param status New status
     * @param errorMessage Error message (if any)
     */
    public void updateStatus(String transactionId, String status, String errorMessage) {
        try {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Sequel.mengedit("status_tte", "transaction_id=?", 
                              "status=?, error_message=?, updated_at=NOW()", 3,
                              new String[]{status, errorMessage, transactionId});
            } else {
                Sequel.mengedit("status_tte", "transaction_id=?", 
                              "status=?, updated_at=NOW()", 2,
                              new String[]{status, transactionId});
            }
            
            if ("SIGNED".equals(status)) {
                Sequel.mengedit("status_tte", "transaction_id=?", 
                              "signed_at=NOW()", 1,
                              new String[]{transactionId});
            }
        } catch (Exception e) {
            System.err.println("Error updating status: " + e.getMessage());
        }
    }
}
