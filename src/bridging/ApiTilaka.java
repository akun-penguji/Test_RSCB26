package bridging;

import fungsi.koneksiDB;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import javax.imageio.ImageIO;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;

public class ApiTilaka {
    
    // Configuration is now pulled from koneksiDB
    
    // Auth Credentials/URL will be fetched from koneksiDB at runtime
    // KEYCLOAK_TOKEN_URL will be constructed or fetched
    
    private ObjectMapper mapper = new ObjectMapper();

    public ApiTilaka() {
    }

    private CloseableHttpClient createHttpClient() {
        try {
            // Trust all certificates for dev/sandbox
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial(TrustAllStrategy.INSTANCE)
                    .build();
            
            SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                    .setSslContext(sslContext)
                    .build();
            
            PoolingHttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(sslSocketFactory)
                    .build();

            return HttpClients.custom()
                    .setConnectionManager(cm)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return HttpClients.createDefault();
        }
    }

    public String getAccessToken() {
        try (CloseableHttpClient client = createHttpClient()) {
            // Replicating logic from RMDataResumePasienRanap's getTilakaAccessToken which seemed to use client_credentials
            // The config URLAUTHTILAKA is 'https://sb-api.tilaka.id' which suggests a proxy or simplified auth endpoint.
            // The Postman URL 'https://dev-keycloak.tilaka.id/auth/realms/...' causes 404 on 'sb-api.tilaka.id'.
            // So we revert to the simple '/auth' path found in the legacy code.
            
            String authUrl = koneksiDB.URLAUTHTILAKA() + "/auth";
            
            HttpPost httpPost = new HttpPost(authUrl);
            
            List<org.apache.hc.core5.http.NameValuePair> params = new ArrayList<>();
            params.add(new org.apache.hc.core5.http.message.BasicNameValuePair("client_id", koneksiDB.CLIENTIDTILAKA()));
            params.add(new org.apache.hc.core5.http.message.BasicNameValuePair("client_secret", koneksiDB.CLIENTSECRETTILAKA()));
            params.add(new org.apache.hc.core5.http.message.BasicNameValuePair("grant_type", "client_credentials"));
            params.add(new org.apache.hc.core5.http.message.BasicNameValuePair("scope", "email")); 
            
            httpPost.setEntity(new org.apache.hc.client5.http.entity.UrlEncodedFormEntity(params));

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("Tilaka Auth Response: " + responseBody);
                
                if (response.getCode() == 200) {
                    JsonNode rootNode = mapper.readTree(responseBody);
                    if (rootNode.has("access_token")) {
                        return rootNode.get("access_token").asText();
                    } else if (rootNode.has("auth_token")) {
                        return rootNode.get("auth_token").asText();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String uploadFile(String filePath) {
        String token = getAccessToken();
        if (token == null) {
            System.err.println("Failed to get access token for upload");
            return null;
        }

        try (CloseableHttpClient client = createHttpClient()) {
            HttpPost httpPost = new HttpPost(koneksiDB.URLAPITILAKA() + "/api/v1/upload");
            httpPost.setHeader("Authorization", "Bearer " + token);

            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("File not found: " + filePath);
                return null;
            }

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", file, ContentType.APPLICATION_PDF, file.getName());
            
            HttpEntity multipart = builder.build();
            httpPost.setEntity(multipart);

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("Tilaka Upload Response: " + responseBody);
                
                if (response.getCode() == 200) {
                    JsonNode rootNode = mapper.readTree(responseBody);
                    // Assuming response contains filename or ID needed for signing
                    // Adjust based on actual API response structure
                    // For now returning full response or specific field if known
                    if (rootNode.has("filename")) {
                        return rootNode.get("filename").asText();
                    }
                    // Fallback, return file name if success but structure unknown
                    return file.getName(); 
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // request_id, user_identifier, email, filename, coordinate_x, coordinate_y, page_number, etc.
    public String requestQuickSign(String requestId, String userIdentifier, String email, String filename, 
                                    int width, int height, double coordX, double coordY, int pageNumber, String templateNo) {
        List<Map<String, Object>> signers = new ArrayList<>();
        Map<String, Object> signer = new HashMap<>();
        signer.put("user_identifier", userIdentifier);
        signer.put("email", email);
        signer.put("coordinate_x", coordX);
        signer.put("coordinate_y", coordY);
        signer.put("page_number", pageNumber);
        signer.put("width", width);
        signer.put("height", height);
        signers.add(signer);
        
        return requestQuickSignMultiple(requestId, filename, templateNo, signers);
    }

    public String requestQuickSignMultiple(String requestId, String filename, String templateNo, List<Map<String, Object>> signers) {
        String token = getAccessToken();
        if (token == null) return null;

        try (CloseableHttpClient client = createHttpClient()) {
            HttpPost httpPost = new HttpPost(koneksiDB.URLAPITILAKA() + "/api/v1/requestquicksign");
            httpPost.setHeader("Authorization", "Bearer " + token);
            httpPost.setHeader("Content-Type", "application/json");

            Map<String, Object> payload = new java.util.LinkedHashMap<>();
            payload.put("request_id", requestId);
            
            Map<String, Map<String, Object>> uniqueSigners = new java.util.LinkedHashMap<>();
        List<Map<String, Object>> pdfSignatures = new ArrayList<>();

        for (Map<String, Object> s : signers) {
            String userIdentifier = s.get("user_identifier").toString().trim();
            String email = s.get("email").toString();

            // Add to unique signers if not present
            if (!uniqueSigners.containsKey(userIdentifier)) {
                Map<String, Object> topSig = new java.util.LinkedHashMap<>();
                topSig.put("user_identifier", userIdentifier);
                topSig.put("email", email);
                
                String sigImage = s.getOrDefault("signature_image", "").toString();
                if (sigImage.isEmpty()) {
                    sigImage = generateSignatureImage(userIdentifier);
                }
                topSig.put("signature_image", sigImage);
                uniqueSigners.put(userIdentifier, topSig);
            }

            // PDF level signature (always add as a spot)
            Map<String, Object> pdfSig = new java.util.LinkedHashMap<>();
            pdfSig.put("user_identifier", userIdentifier);
            pdfSig.put("reason", "Signing Medical Record");
            pdfSig.put("location", "Yogyakarta");
            pdfSig.put("width", s.getOrDefault("width", 152));
            pdfSig.put("height", s.getOrDefault("height", 80));
            pdfSig.put("coordinate_x", s.get("coordinate_x"));
            pdfSig.put("coordinate_y", s.get("coordinate_y"));
            pdfSig.put("page_number", s.get("page_number"));
            pdfSignatures.add(pdfSig);
        }

        // Assign sequences to unique signers and build the list
        List<Map<String, Object>> topSignatures = new ArrayList<>();
        int seq = 1;
        for (Map<String, Object> ts : uniqueSigners.values()) {
            ts.put("sequence", seq++);
            topSignatures.add(ts);
        }

        payload.put("signatures", topSignatures);
            
            List<Map<String, Object>> pdfList = new ArrayList<>();
            Map<String, Object> pdfDTO = new java.util.LinkedHashMap<>();
            pdfDTO.put("filename", filename);
            pdfDTO.put("template_no", templateNo);
            pdfDTO.put("signatures", pdfSignatures);
            pdfList.add(pdfDTO);
            
            payload.put("list_pdf", pdfList);
            
            String jsonPayload = mapper.writeValueAsString(payload);
            System.out.println("DEBUG: Quick Sign Multiple Payload: " + jsonPayload);
            httpPost.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("Tilaka Quick Sign Response: " + responseBody);
                if (response.getCode() == 200) {
                    return responseBody;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String checkSignStatus(String requestId) {
        String token = getAccessToken();
        if (token == null) return "ERROR";

        try (CloseableHttpClient client = createHttpClient()) {
            HttpPost httpPost = new HttpPost(koneksiDB.URLAPITILAKA() + "/api/v1/checksignstatus");
            httpPost.setHeader("Authorization", "Bearer " + token);
            httpPost.setHeader("Content-Type", "application/json");

            Map<String, String> payload = new HashMap<>();
            payload.put("request_id", requestId);
            
            String jsonPayload = mapper.writeValueAsString(payload);
            httpPost.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("Tilaka Check Status Response: " + responseBody);
                if (responseBody.contains("presigned_url")) {
                    System.out.println("Success: presigned_url found in status response.");
                } else {
                    System.out.println("Warning: presigned_url NOT found in status response.");
                }
                return responseBody;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR";
    }
    
    public String getPresignedUrl(String filename) {
        // This is a GET request without auth? Postman says "Getpresignurl" method GET, url param filename.
        // Postman does NOT show auth for this specific request in the collection provided (it has empty auth in description or just 'No Auth' implied if not inherited). 
        // Wait, the collection has "auth": {"type": "bearer"} for others, but for "Getpresignurl" it says "request": { "auth": ... } is missing or empty in snippet?
        // Ah, snippet for Getpresignurl: "request": { "method": "GET", ... "auth": null/missing inside request object? }
        // The prompt JSON snippet for Getpresignurl DOES NOT have an "auth" block inside "request". 
        // But usually these need auth. I'll assume no auth for now based on snippet, or try adding it if it fails.
        // Actually, usually checking status or getting url needs auth.
        
        try (CloseableHttpClient client = createHttpClient()) {
            HttpGet httpGet = new HttpGet(koneksiDB.URLAPITILAKA() + "/api/v1/getpresignurl?filename=" + filename);
            
            String token = getAccessToken();
            if (token != null) {
                httpGet.setHeader("Authorization", "Bearer " + token);
            }

            try (CloseableHttpResponse response = client.execute(httpGet)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("Tilaka Get Presigned URL Response: " + responseBody);
                
                if (response.getCode() == 200) {
                     JsonNode rootNode = mapper.readTree(responseBody);
                     // Assuming structure has url
                     if (rootNode.has("url")) return rootNode.get("url").asText();
                     // Or maybe presigned_url
                     if (rootNode.has("presigned_url")) return rootNode.get("presigned_url").asText();
                     
                     return responseBody; // Return full body if unsure
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean downloadFromUrl(String url, String destinationPath) {
        System.out.println("Attempting to download from: " + url);
        try (CloseableHttpClient client = createHttpClient()) {
            HttpGet httpGet = new HttpGet(url);
            // Presigned URLs usually don't need auth headers as the signature is in the query params
            
            try (CloseableHttpResponse response = client.execute(httpGet)) {
                int statusCode = response.getCode();
                System.out.println("Download response status: " + statusCode);
                
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        try (InputStream inputStream = entity.getContent();
                             FileOutputStream outputStream = new FileOutputStream(destinationPath)) {
                            
                            byte[] buffer = new byte[8192];
                            int bytesRead;
                            long totalBytesRead = 0;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                                totalBytesRead += bytesRead;
                            }
                            System.out.println("File downloaded successfully (" + totalBytesRead + " bytes) to: " + destinationPath);
                            return true;
                        }
                    } else {
                        System.out.println("Download failed: Response entity is null");
                    }
                } else {
                    String reason = EntityUtils.toString(response.getEntity());
                    System.out.println("Failed to download file. Status: " + statusCode + ", Reason: " + reason);
                }
            }
        } catch (Exception e) {
            System.out.println("Error downloading file: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public String generateSignatureImage(String text, String kd_dokter) {
        try {
            int width = 240;
            int height = 240;
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bufferedImage.createGraphics();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

            g2d.setColor(new Color(255, 255, 255, 0)); // Transparent background
            g2d.fillRect(0, 0, width, height);

            int xOffset = 10;
            
            // Try to fetch QR code if kd_dokter is provided
            if (kd_dokter != null && !kd_dokter.isEmpty()) {
                try {
                    String qrUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/penggajian/generateqrcode.php?kodedokter=" + kd_dokter.replace(" ", "_");
                    String imageUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/penggajian/temp/" + kd_dokter + ".png";
                    
                    try (CloseableHttpClient client = createHttpClient()) {
                        // 1. Trigger generation
                        HttpGet getGen = new HttpGet(qrUrl);
                        client.execute(getGen, response -> null);
                        
                        // 2. Fetch image
                        HttpGet getImg = new HttpGet(imageUrl);
                        client.execute(getImg, response -> {
                            if (response.getCode() == 200) {
                                try (InputStream is = response.getEntity().getContent()) {
                                    BufferedImage qrImg = ImageIO.read(is);
                                    if (qrImg != null) {
                                        g2d.drawImage(qrImg, 10, 10, 220, 220, null);
                                    }
                                }
                            }
                            return null;
                        });
                    }
                    xOffset = 240; 
                } catch (Exception qre) {
                    System.out.println("Could not fetch QR code for " + kd_dokter + ": " + qre.getMessage());
                }
            }

            // Text drawing removed as per user request
            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkYAAAAAYAAjCB0C8AAAAASUVORK5CYII=";
        }
    }

    public String generateSignatureImage(String text) {
        return generateSignatureImage(text, "");
    }
}
