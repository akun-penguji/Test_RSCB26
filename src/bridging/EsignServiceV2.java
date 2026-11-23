package bridging;

import fungsi.koneksiDB;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import javax.net.ssl.SSLContext;

public class EsignServiceV2 {
    private final ObjectMapper mapper = new ObjectMapper();
    private int lastStatusCode = 0;
    private String lastErrorMessage = "";
    private String lastRawResponse = "";

    public int getLastStatusCode() {
        return lastStatusCode;
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public String getLastRawResponse() {
        return lastRawResponse;
    }

    public static void catatLog(String noRawat, String nik, String nama, String message, String statusCode) {
        try {
            java.sql.Connection conn = koneksiDB.condb();
            if (conn == null) return;
            String sql = "INSERT INTO esign_log (no_rawat, nik, nama, message, status_code, remote_addr, user_agent, created_at) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
            try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, noRawat != null ? noRawat : "-");
                ps.setString(2, nik != null ? nik : "-");
                ps.setString(3, nama != null ? nama : "-");
                ps.setString(4, message != null ? message : "Gagal TTE");
                ps.setString(5, statusCode != null ? statusCode : "ERR");
                ps.setString(6, getClientIp());
                ps.setString(7, "SIMRS Khanza Desktop / BSrE Client");
                ps.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("Gagal mencatat esign_log: " + e.getMessage());
        }
    }

    private static String getClientIp() {
        try {
            return java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }

    public static class SignatureProperty {
        public String tampilan = "VISIBLE";
        public String linkQR = "";
        public boolean image = true;
        public int page = 1;
        public float originX = 0;
        public float originY = 0;
        public float pageWidth = 0;
        public float pageHeight = 0;
        public float width = 100;
        public float height = 75;
        public String location = "Jakarta";
        public String reason = "Dokumen Ini Telah Disetujui dengan Tanda Tangan Elektronik";
        public String imageBase64 = "";
    }

    public static BufferedImage getLogoPemda() {
        String[] possiblePaths = {
            "setting/logopemda.png",
            "report/logopemda.png",
            "/Users/salimmulyana/Downloads/logokabupatenbekasi.png",
            "dist/report/logopemda.png",
            "picture/logopemda.png"
        };
        for (String p : possiblePaths) {
            File f = new File(p);
            if (f.exists()) {
                try {
                    BufferedImage img = ImageIO.read(f);
                    if (img != null) return img;
                } catch (Exception ignored) {}
            }
        }
        try {
            java.io.InputStream is = EsignServiceV2.class.getResourceAsStream("/picture/logopemda.png");
            if (is != null) {
                BufferedImage img = ImageIO.read(is);
                if (img != null) return img;
            }
        } catch (Exception ignored) {}
        return null;
    }

    public String generateQRCodeBase64(String text, int width, int height) {
        return generateQRCodeWithLogoBase64(text, width, height, getLogoPemda());
    }

    public String generateQRCodeWithLogoBase64(String text, int width, int height, BufferedImage logoImage) {
        try {
            // Render at high resolution (min 400x400) for crispness when rendered
            int renderSize = Math.max(Math.max(width, height) * 4, 400);

            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, renderSize, renderSize, hints);

            int matrixWidth = bitMatrix.getWidth();
            int matrixHeight = bitMatrix.getHeight();

            BufferedImage qrImage = new BufferedImage(matrixWidth, matrixHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = qrImage.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            // Draw white background
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, matrixWidth, matrixHeight);

            // Draw QR modules
            g2.setColor(Color.BLACK);
            for (int x = 0; x < matrixWidth; x++) {
                for (int y = 0; y < matrixHeight; y++) {
                    if (bitMatrix.get(x, y)) {
                        g2.fillRect(x, y, 1, 1);
                    }
                }
            }

            // Overlay logo if available
            if (logoImage != null) {
                int logoWidth = (int) (matrixWidth * 0.22);
                int logoHeight = (int) (matrixHeight * 0.22);

                int origW = logoImage.getWidth();
                int origH = logoImage.getHeight();
                if (origW > 0 && origH > 0) {
                    if (origW > origH) {
                        logoHeight = (int) ((double) origH / origW * logoWidth);
                    } else {
                        logoWidth = (int) ((double) origW / origH * logoHeight);
                    }
                }

                int logoX = (matrixWidth - logoWidth) / 2;
                int logoY = (matrixHeight - logoHeight) / 2;

                int pad = Math.max(4, (int) (matrixWidth * 0.015));
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(logoX - pad, logoY - pad, logoWidth + (pad * 2), logoHeight + (pad * 2), 12, 12);
                g2.setColor(new Color(210, 210, 210));
                g2.drawRoundRect(logoX - pad, logoY - pad, logoWidth + (pad * 2), logoHeight + (pad * 2), 12, 12);

                g2.drawImage(logoImage, logoX, logoY, logoWidth, logoHeight, null);
            }
            g2.dispose();

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(pngData);
        } catch (Exception e) {
            System.err.println("Error generating QR Code with logo: " + e.getMessage());
            return "";
        }
    }

    private CloseableHttpClient createHttpClient() {
        try {
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

    /**
     * Ringkasan payload yang aman dicetak ke konsol.
     *
     * Payload asli TIDAK boleh dicetak apa adanya: isinya memuat passphrase BSrE
     * mentah, NIK penandatangan, dan seluruh dokumen dalam base64. Konsol Khanza
     * kerap ditangkap ke berkas log di workstation, sehingga mencetaknya sama
     * dengan menuliskan kredensial TTE ke disk.
     *
     * Yang disisakan hanya yang berguna saat menelusuri masalah: alamat tujuan,
     * jumlah & posisi tanda tangan, serta ukuran berkas.
     */
    private String ringkasPayload(ObjectNode root) {
        try {
            ObjectNode aman = root.deepCopy();
            aman.remove("passphrase");
            // asText() tanpa argumen, bukan asText("") — Jackson lama di classpath
            // Khanza belum punya overload berargumen. Perilakunya sama: path()
            // mengembalikan MissingNode yang asText()-nya sudah string kosong.
            aman.put("nik", samarkanNik(aman.path("nik").asText()));

            JsonNode fileNode = aman.get("file");
            if (fileNode != null && fileNode.isArray()) {
                ArrayNode ringkas = mapper.createArrayNode();
                for (JsonNode f : fileNode) {
                    ringkas.add("<dokumen base64, " + f.asText().length() + " karakter>");
                }
                aman.set("file", ringkas);
            }

            JsonNode props = aman.get("signatureProperties");
            if (props != null && props.isArray()) {
                for (JsonNode pn : props) {
                    if (pn instanceof ObjectNode) {
                        ObjectNode po = (ObjectNode) pn;
                        String img = po.path("imageBase64").asText();
                        if (!img.isEmpty()) {
                            po.put("imageBase64", "<QR base64, " + img.length() + " karakter>");
                        }
                    }
                }
            }
            return mapper.writeValueAsString(aman);
        } catch (Exception e) {
            return "<gagal meringkas payload: " + e.getMessage() + ">";
        }
    }

    /** NIK 16 digit -> 4 digit awal + bintang + 4 digit akhir. */
    private static String samarkanNik(String nik) {
        if (nik == null || nik.length() < 9) return "<disamarkan>";
        return nik.substring(0, 4) + "********" + nik.substring(nik.length() - 4);
    }

    /**
     * Call BSrE V2 Sign API
     */
    public boolean prosesTTE(File inputFile, File outputFile, String nik, String passphrase, List<SignatureProperty> properties) {
        try {
            ObjectNode root = mapper.createObjectNode();
            root.put("nik", nik);
            root.put("passphrase", passphrase);
            
            ArrayNode propsArray = mapper.createArrayNode();
            for (SignatureProperty prop : properties) {
                ObjectNode p = mapper.createObjectNode();
                
                // Field order matching schema strictly
                String finalImageBase64 = prop.imageBase64;
                if ((finalImageBase64 == null || finalImageBase64.trim().isEmpty()) && "VISIBLE".equalsIgnoreCase(prop.tampilan)) {
                    String qrText = prop.linkQR;
                    if (qrText == null || qrText.trim().isEmpty()) {
                        qrText = "https://" + koneksiDB.URLVERIFIKASITTE() + "/verify/?tte=" + nik;
                    }
                    int w = (int) prop.width;
                    int h = (int) prop.height;
                    if (w <= 0) w = 150;
                    if (h <= 0) h = 150;
                    finalImageBase64 = generateQRCodeBase64(qrText, w, h);
                }

                p.put("imageBase64", finalImageBase64 == null ? "" : finalImageBase64);
                p.put("tampilan", prop.tampilan);
                p.put("page", prop.page);
                
                float adjustedY = prop.originY;
                if (prop.height > 0) {
                    // Center the signature vertically on the tag baseline
                    adjustedY = prop.originY - (prop.height / 2);
                }

                float x = Math.round(prop.originX * 100.0f) / 100.0f;
                float y = Math.round(adjustedY * 100.0f) / 100.0f;

                p.put("originX", x);
                p.put("originY", y);
                
                p.put("width", prop.width);
                p.put("height", prop.height);
                p.put("location", prop.location == null || prop.location.equalsIgnoreCase("null") ? "" : prop.location);
                p.put("reason", prop.reason == null || prop.reason.equalsIgnoreCase("null") ? "" : prop.reason);
                p.put("contactInfo", "");
                
                propsArray.add(p);
            }
            root.set("signatureProperties", propsArray);

            ArrayNode fileArray = mapper.createArrayNode();
            String base64File = Base64.getEncoder().encodeToString(Files.readAllBytes(inputFile.toPath()));
            fileArray.add(base64File);
            root.set("file", fileArray);

            root.put("targetUrl", koneksiDB.URLAPIESIGNV2());
            String jsonPayload = mapper.writeValueAsString(root);

            try (CloseableHttpClient httpClient = createHttpClient()) {
                String url = koneksiDB.URLAKSESFILEESIGNV2();
                if (url == null || url.isEmpty()) {
                    url = koneksiDB.URLAPIESIGNV2();
                    if (url == null || url.isEmpty()) {
                        url = koneksiDB.URLAPIESIGN() + "/api/v2/sign/pdf";
                        if (url.contains("/api/sign/pdf")) {
                            url = url.replace("/api/sign/pdf", "/api/v2/sign/pdf");
                        }
                    }
                }
                
                if (url.contains("jadikanpdf") && !url.contains("/signpdfv2")) {
                } else if (!url.endsWith("/api/v2/sign/pdf") && !url.endsWith("/signpdfv2")) {
                    if (url.endsWith("/")) {
                        url += "signpdfv2";
                    } else {
                        url += "/signpdfv2";
                    }
                }
                
                System.out.println("DEBUG: TTE V2 Request URL: " + url);
                System.out.println("DEBUG: TTE V2 Payload (tersamar): " + ringkasPayload(root));
                
                HttpPost request = new HttpPost(url);
                request.addHeader("Content-Type", "application/json");
                
                String user = koneksiDB.USERNAMEAPIESIGNV2();
                String pass = koneksiDB.PASSAPIESIGNV2();
                if (user == null || user.isEmpty()) {
                    user = koneksiDB.USERNAMEAPIESIGN();
                    pass = koneksiDB.PASSAPIESIGN();
                }
                
                String auth = user + ":" + pass;
                String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
                request.addHeader("Authorization", "Basic " + encodedAuth);
                request.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));

                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    String resultJson = EntityUtils.toString(response.getEntity());
                    int statusCode = response.getCode();

                    // Strip UTF-8 BOM if present
                    if (resultJson != null && resultJson.startsWith("\uFEFF")) {
                        resultJson = resultJson.substring(1);
                    }

                    if (statusCode == 200 || statusCode == 201) {
                        JsonNode res = mapper.readTree(resultJson);
                        if (res.isArray() && res.size() > 0) {
                            String signedBase64 = res.get(0).asText();
                            byte[] signedBytes = Base64.getDecoder().decode(signedBase64);
                            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                                fos.write(signedBytes);
                            }
                            return true;
                        } else if (res.has("file") && res.get("file").isArray()) {
                            String signedBase64 = res.get("file").get(0).asText();
                            byte[] signedBytes = Base64.getDecoder().decode(signedBase64);
                            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                                fos.write(signedBytes);
                            }
                            return true;
                        }
                    } else {
                        this.lastStatusCode = statusCode;
                        this.lastRawResponse = resultJson;
                        String pesan = "";
                        try {
                            JsonNode errNode = mapper.readTree(resultJson);
                            if (errNode.has("error") && !errNode.get("error").isNull()) {
                                pesan = errNode.get("error").asText();
                            } else if (errNode.has("message") && !errNode.get("message").isNull()) {
                                pesan = errNode.get("message").asText();
                            } else if (errNode.has("pesan") && !errNode.get("pesan").isNull()) {
                                pesan = errNode.get("pesan").asText();
                            } else {
                                pesan = resultJson;
                            }
                        } catch (Exception ignored) {
                            pesan = resultJson != null && !resultJson.trim().isEmpty() ? resultJson : "Tidak ada respon pesan dari server BSrE";
                        }
                        this.lastErrorMessage = pesan;
                        System.err.println("BSrE V2 Error " + statusCode + ": " + pesan);
                        catatLog(null, nik, null, "Gagal melakukan penandatanganan dokumen. Pesan: " + pesan, String.valueOf(statusCode));
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            this.lastErrorMessage = e.getMessage() != null ? e.getMessage() : e.toString();
            System.err.println("BSrE V2 Exception: " + this.lastErrorMessage);
            catatLog(null, nik, null, "Gagal melakukan penandatanganan dokumen. Pesan: " + this.lastErrorMessage, "EXC");
            e.printStackTrace();
        }
        return false;
    }

    public static class StatusResult {
        public int code = 0;
        public String status = "Belum Terdaftar";
        public String message = "-";
        public String raw = "";
    }

    public static class ResetResult {
        public boolean success = false;
        public String message = "";
    }

    private static final String DUMMY_PDF_BASE64 = 
        "JVBERi0xLjQKMSAwIG9iajw8L1R5cGUvQ2F0YWxvZy9QYWdlcyAyIDAgUj4+ZW5kb2JqCjIgMCBvYmo8PC9UeXBlL1BhZ2VzL0tpZHNbMyAwIFJdL0NvdW50IDE+PmVuZG9iagozIDAgb2JqPDwvVHlwZS9QYWdlL01lZGlhQm94WzAgMCA2MTIgNzkyXS9QYXJlbnQgMiAwIFI+PmVuZG9iagp4cmVmCjAgNAowMDAwMDAwMDAwIDY1NTM1IGYgCjAwMDAwMDAwMTAgMDAwMDAgbiAKMDAwMDAwMDA1MyAwMDAwMCBuIAowMDAwMDAwMTAyIDAwMDAwIG4gCnRyYWlsZXI8PC9TaXplIDQvUm9vdCAxIDAgUj4+CnN0YXJ0eHJlZgoxNzgKJSVFT0YK";

    /**
     * Memeriksa status sertifikat akun TTE di BSrE secara presisi
     */
    public StatusResult checkStatus(String nik) {
        StatusResult res = new StatusResult();
        if (nik == null || nik.trim().length() != 16 || !nik.matches("^[0-9]{16}$")) {
            res.code = 400;
            res.status = "Belum Terdaftar";
            res.message = "NIK KTP tidak valid (harus 16 digit angka)";
            return res;
        }

        try {
            ObjectNode root = mapper.createObjectNode();
            root.put("nik", nik);
            root.put("passphrase", "check_status_bsre_probe");

            ArrayNode propsArray = mapper.createArrayNode();
            ObjectNode p = mapper.createObjectNode();
            p.put("tampilan", "INVISIBLE");
            p.put("location", "Jakarta");
            p.put("reason", "Cek Status Akun TTE");
            propsArray.add(p);
            root.set("signatureProperties", propsArray);

            ArrayNode fileArray = mapper.createArrayNode();
            fileArray.add(DUMMY_PDF_BASE64);
            root.set("file", fileArray);

            root.put("targetUrl", koneksiDB.URLAPIESIGNV2());
            String jsonPayload = mapper.writeValueAsString(root);

            try (CloseableHttpClient httpClient = createHttpClient()) {
                String url = koneksiDB.URLAKSESFILEESIGNV2();
                if (url == null || url.isEmpty()) {
                    url = koneksiDB.URLAPIESIGNV2();
                }

                HttpPost request = new HttpPost(url);
                request.addHeader("Content-Type", "application/json");

                String user = koneksiDB.USERNAMEAPIESIGNV2();
                String pass = koneksiDB.PASSAPIESIGNV2();
                if (user == null || user.isEmpty()) {
                    user = koneksiDB.USERNAMEAPIESIGN();
                    pass = koneksiDB.PASSAPIESIGN();
                }

                String auth = user + ":" + pass;
                String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
                request.addHeader("Authorization", "Basic " + encodedAuth);
                request.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));

                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    String resultJson = EntityUtils.toString(response.getEntity());
                    int httpCode = response.getCode();

                    if (resultJson != null && resultJson.startsWith("\uFEFF")) {
                        resultJson = resultJson.substring(1);
                    }
                    res.raw = resultJson;

                    if (httpCode == 200 || httpCode == 201) {
                        res.code = 200;
                        res.status = "AKTIF";
                        res.message = "Sertifikat TTE aktif dan terdaftar di BSrE";
                        return res;
                    }

                    int bsreStatusCode = 0;
                    String bsreError = "";
                    try {
                        JsonNode node = mapper.readTree(resultJson);
                        if (node.has("status_code")) {
                            bsreStatusCode = node.get("status_code").asInt();
                        }
                        if (node.has("error")) {
                            bsreError = node.get("error").asText();
                        } else if (node.has("message")) {
                            bsreError = node.get("message").asText();
                        }
                    } catch (Exception ignored) {
                        bsreError = resultJson;
                    }

                    res.code = bsreStatusCode > 0 ? bsreStatusCode : httpCode;
                    res.message = bsreError != null && !bsreError.isEmpty() ? bsreError : "Respon: HTTP " + httpCode;

                    if (bsreStatusCode == 2031 || bsreError.toLowerCase().contains("passphrase anda salah")) {
                        res.status = "AKTIF";
                        res.message = "Sertifikat TTE aktif dan terdaftar di BSrE";
                    } else if (bsreStatusCode == 2011 || bsreError.toLowerCase().contains("tidak terdaftar")) {
                        res.status = "BELUM TERDAFTAR";
                        res.message = "Pengguna belum terdaftar di BSrE";
                    } else if (bsreStatusCode == 2021 || bsreError.toLowerCase().contains("kedaluwarsa") || bsreError.toLowerCase().contains("expired")) {
                        res.status = "KEDALUWARSA";
                        res.message = "Sertifikat TTE telah kedaluwarsa";
                    } else if (bsreStatusCode == 2022 || bsreError.toLowerCase().contains("dicabut") || bsreError.toLowerCase().contains("revoked")) {
                        res.status = "DICABUT";
                        res.message = "Sertifikat TTE telah dicabut";
                    } else if (bsreStatusCode == 2041 || bsreError.toLowerCase().contains("terblokir") || bsreError.toLowerCase().contains("blocked")) {
                        res.status = "TERBLOKIR";
                        res.message = "Akun TTE terblokir karena salah passphrase";
                    } else {
                        res.status = "TIDAK AKTIF";
                    }
                }
            }
        } catch (Exception e) {
            res.code = 500;
            res.status = "ERROR";
            res.message = "Koneksi ke server TTE gagal: " + e.getMessage();
        }
        return res;
    }

    /**
     * Meminta link reset passphrase akun BSrE ke email pemilik NIK
     */
    public ResetResult requestResetPassphrase(String nik) {
        ResetResult res = new ResetResult();
        if (nik == null || nik.trim().length() != 16 || !nik.matches("^[0-9]{16}$")) {
            res.success = false;
            res.message = "NIK KTP tidak valid (harus 16 digit angka)";
            return res;
        }

        try {
            ObjectNode root = mapper.createObjectNode();
            root.put("nik", nik);
            root.put("passphrase", "dummy");

            ArrayNode propsArray = mapper.createArrayNode();
            ObjectNode p = mapper.createObjectNode();
            p.put("tampilan", "INVISIBLE");
            propsArray.add(p);
            root.set("signatureProperties", propsArray);

            ArrayNode fileArray = mapper.createArrayNode();
            fileArray.add(DUMMY_PDF_BASE64);
            root.set("file", fileArray);

            // Forward ke endpoint forget passphrase BSrE
            String bsreHost = koneksiDB.URLAPIESIGNV2();
            if (bsreHost != null && bsreHost.contains("/api/")) {
                bsreHost = bsreHost.substring(0, bsreHost.indexOf("/api/"));
            } else {
                bsreHost = "http://10.5.55.79";
            }
            root.put("targetUrl", bsreHost + "/api/user/passphrase/forget/" + nik);

            String jsonPayload = mapper.writeValueAsString(root);

            try (CloseableHttpClient httpClient = createHttpClient()) {
                String url = koneksiDB.URLAKSESFILEESIGNV2();
                if (url == null || url.isEmpty()) {
                    url = koneksiDB.URLAPIESIGNV2();
                }

                HttpPost request = new HttpPost(url);
                request.addHeader("Content-Type", "application/json");

                String user = koneksiDB.USERNAMEAPIESIGNV2();
                String pass = koneksiDB.PASSAPIESIGNV2();
                if (user == null || user.isEmpty()) {
                    user = koneksiDB.USERNAMEAPIESIGN();
                    pass = koneksiDB.PASSAPIESIGN();
                }

                String auth = user + ":" + pass;
                String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
                request.addHeader("Authorization", "Basic " + encodedAuth);
                request.setEntity(new StringEntity(jsonPayload, ContentType.APPLICATION_JSON));

                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    String resultJson = EntityUtils.toString(response.getEntity());
                    int httpCode = response.getCode();

                    if (resultJson != null && resultJson.startsWith("\uFEFF")) {
                        resultJson = resultJson.substring(1);
                    }

                    if (httpCode == 200) {
                        try {
                            JsonNode node = mapper.readTree(resultJson);
                            if (node.has("message")) {
                                res.message = node.get("message").asText();
                            } else {
                                res.message = "Kirim link reset passphrase berhasil. Silahkan periksa email yang terdaftar di BSrE.";
                            }
                            res.success = node.has("success") ? node.get("success").asBoolean() : true;
                        } catch (Exception ex) {
                            res.success = true;
                            res.message = "Kirim link reset passphrase berhasil. Silahkan periksa email terdaftar.";
                        }
                    } else {
                        String errMsg = "";
                        try {
                            JsonNode node = mapper.readTree(resultJson);
                            if (node.has("message")) {
                                errMsg = node.get("message").asText();
                            } else if (node.has("error")) {
                                errMsg = node.get("error").asText();
                            }
                        } catch (Exception ignored) {
                            errMsg = resultJson;
                        }
                        res.success = false;
                        res.message = "Gagal memproses reset passphrase: " + (errMsg.isEmpty() ? "HTTP " + httpCode : errMsg);
                    }
                }
            }
        } catch (Exception e) {
            res.success = false;
            res.message = "Koneksi ke server TTE gagal: " + e.getMessage();
        }
        return res;
    }
}
