package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.koneksiDB;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ApiSatuSehat {
    private String key,clientid,urlauth,token;
    private long millis;
    private SSLContext sslContext;
    private SSLSocketFactory sslFactory;
    private Scheme scheme;
    private HttpComponentsClientHttpRequestFactory factory;
    private ApiBPJSAesKeySpec mykey;
    private HttpHeaders header ;
    private JsonNode root;
    private HttpEntity requestEntity;
    private ObjectMapper mapper = new ObjectMapper();
    private static String cachedAccessToken = null;
    private static long tokenExpiresAtMillis = 0L;
    private static final long REFRESH_BUFFER_SECONDS = 60L;
    private static final long DEFAULT_TOKEN_TTL_SECONDS = 3600L;

    public ApiSatuSehat(){
        try {
            key = koneksiDB.SECRETKEYSATUSEHAT();
            clientid = koneksiDB.CLIENTIDSATUSEHAT();
            urlauth = koneksiDB.URLAUTHSATUSEHAT();
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
        }
    }

    public String TokenSatuSehat(){
        try {    
            header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            requestEntity = new HttpEntity("client_id="+clientid+"&client_secret="+key,header);
            root = mapper.readTree(getRest().exchange(urlauth+"/accesstoken?grant_type=client_credentials", HttpMethod.POST, requestEntity, String.class).getBody());
            token=root.path("access_token").asText();
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
        }
        return token;
    }
        
    public long GetUTCdatetimeAsString(){    
        millis = System.currentTimeMillis();   
        return millis/1000;
    }
    
    public String Decrypt(String data,String utc)throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        System.out.println(data);
        mykey = ApiBPJSEnc.generateKey(clientid+key+utc);
        data=ApiBPJSEnc.decrypt(data, mykey.getKey(), mykey.getIv());
        data=ApiBPJSLZString.decompressFromEncodedURIComponent(data);
        System.out.println(data);
        return data;
    }
    
    public RestTemplate getRest() throws NoSuchAlgorithmException, KeyManagementException {
        sslContext = SSLContext.getInstance("TLSv1.2");
        TrustManager[] trustManagers= {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {return null;}
                public void checkServerTrusted(X509Certificate[] arg0, String arg1)throws CertificateException {}
                public void checkClientTrusted(X509Certificate[] arg0, String arg1)throws CertificateException {}
            }
        };
        sslContext.init(null,trustManagers , new SecureRandom());
        sslFactory=new SSLSocketFactory(sslContext,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        scheme=new Scheme("https",443,sslFactory);
        factory=new HttpComponentsClientHttpRequestFactory();
        factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(scheme);
        return new RestTemplate(factory);
    }
    
    /**
     * Mengirim laporan NIK yang tidak terdaftar ke Satu Sehat
     * Menggunakan pendekatan validasi terlebih dahulu, kemudian pelaporan
     * @param noRawat
     * @param nikPasien
     * @param namaPasien
     * @param nikPraktisi
     * @param namaPraktisi
     * @param jenisLaporan
     * @param keterangan
     * @return Response dari API
     */
    public String kirimLaporanNIK(String noRawat, String nikPasien, String namaPasien, 
                                  String nikPraktisi, String namaPraktisi, 
                                  String jenisLaporan, String keterangan) {
        StringBuilder hasil = new StringBuilder();
        
        try {
            // Siapkan header dengan token
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer " + TokenSatuSehat());
            
            // 1. Validasi NIK Pasien terlebih dahulu
            if (nikPasien != null && !nikPasien.isEmpty()) {
                String validatePatient = validateNIKPatient(nikPasien, headers);
                hasil.append("Validasi NIK Pasien: ").append(validatePatient).append("\n");
            }
            
            // 2. Validasi NIK Praktisi
            if (nikPraktisi != null && !nikPraktisi.isEmpty()) {
                String validatePractitioner = validateNIKPractitioner(nikPraktisi, headers);
                hasil.append("Validasi NIK Praktisi: ").append(validatePractitioner).append("\n");
            }
            
            // 3. Buat laporan ke sistem internal (karena endpoint khusus pelaporan mungkin belum tersedia)
            String internalReport = createInternalNIKReport(noRawat, nikPasien, namaPasien, 
                                                           nikPraktisi, namaPraktisi, 
                                                           jenisLaporan, keterangan);
            hasil.append("Laporan Internal: ").append(internalReport);
            
            System.out.println("Proses pelaporan NIK selesai");
            System.out.println("Hasil: " + hasil.toString());
            
            return hasil.toString();
            
        } catch (Exception e) {
            System.out.println("Error mengirim laporan NIK: " + e.getMessage());
            e.printStackTrace();
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Validasi NIK Patient menggunakan endpoint Patient search
     */
    private String validateNIKPatient(String nik, HttpHeaders headers) {
        try {
            String url = koneksiDB.URLFHIRSATUSEHAT() + "/Patient?identifier=https://fhir.kemkes.go.id/id/nik|" + nik;
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            
            String response = getRest().exchange(url, HttpMethod.GET, requestEntity, String.class).getBody();
            
            // Parse response untuk cek apakah NIK ditemukan
            if (response.contains("\"total\":0") || response.contains("\"total\": 0")) {
                return "NIK " + nik + " TIDAK DITEMUKAN - Perlu pelaporan";
            } else {
                return "NIK " + nik + " DITEMUKAN - Sudah terdaftar";
            }
            
        } catch (Exception e) {
            return "Error validasi NIK Patient: " + e.getMessage();
        }
    }
    
    /**
     * Validasi NIK Practitioner menggunakan endpoint Practitioner search
     */
    private String validateNIKPractitioner(String nik, HttpHeaders headers) {
        try {
            String url = koneksiDB.URLFHIRSATUSEHAT() + "/Practitioner?identifier=https://fhir.kemkes.go.id/id/nik|" + nik;
            HttpEntity<String> requestEntity = new HttpEntity<>(headers);
            
            String response = getRest().exchange(url, HttpMethod.GET, requestEntity, String.class).getBody();
            
            // Parse response untuk cek apakah NIK ditemukan
            if (response.contains("\"total\":0") || response.contains("\"total\": 0")) {
                return "NIK " + nik + " TIDAK DITEMUKAN - Perlu pelaporan";
            } else {
                return "NIK " + nik + " DITEMUKAN - Sudah terdaftar";
            }
            
        } catch (Exception e) {
            return "Error validasi NIK Practitioner: " + e.getMessage();
        }
    }
    
    /**
     * Membuat laporan internal untuk NIK yang tidak terdaftar
     * (Menunggu endpoint resmi dari Satu Sehat)
     */
    private String createInternalNIKReport(String noRawat, String nikPasien, String namaPasien,
                                          String nikPraktisi, String namaPraktisi,
                                          String jenisLaporan, String keterangan) {
        // Untuk sementara, buat log report yang bisa dikirim via email atau sistem lain
        String reportId = "RPT-" + System.currentTimeMillis();
        
        StringBuilder report = new StringBuilder();
        report.append("=== LAPORAN NIK TIDAK TERDAFTAR ===\n");
        report.append("ID Laporan: ").append(reportId).append("\n");
        report.append("Timestamp: ").append(GetUTCdatetimeAsString()).append("\n");
        report.append("No. Rawat: ").append(noRawat).append("\n");
        report.append("Organisasi: ").append(koneksiDB.IDSATUSEHAT()).append("\n");
        report.append("\nData Pasien:\n");
        report.append("- NIK: ").append(nikPasien).append("\n");
        report.append("- Nama: ").append(namaPasien).append("\n");
        report.append("\nData Praktisi:\n");
        report.append("- NIK: ").append(nikPraktisi).append("\n");
        report.append("- Nama: ").append(namaPraktisi).append("\n");
        report.append("\nJenis: ").append(jenisLaporan).append("\n");
        report.append("Keterangan: ").append(keterangan).append("\n");
        
        System.out.println("Laporan internal berhasil dibuat dengan ID: " + reportId);

        return "Berhasil dibuat dengan ID: " + reportId + " - Silakan lanjutkan ke portal manual untuk submit resmi";
    }

    // ====================================================================
    //  METHOD untuk RUJUKAN SATUSEHAT (FHIR R4) - Playbook Rujukan v5.1
    //  Method existing di atas tidak diubah - backward compatible.
    // ====================================================================

    /**
     * Token getter dengan caching. Wrapper di sekitar TokenSatuSehat() existing.
     * Kalau cached token masih valid, return langsung tanpa hit OAuth.
     */
    public String getValidToken() {
        long nowMillis = System.currentTimeMillis();
        long bufferMillis = REFRESH_BUFFER_SECONDS * 1000L;
        if (cachedAccessToken != null
                && !cachedAccessToken.isEmpty()
                && nowMillis + bufferMillis < tokenExpiresAtMillis) {
            return cachedAccessToken;
        }
        // cache expired/null -> panggil method existing untuk request baru
        String fresh = TokenSatuSehat();
        if (fresh != null && !fresh.isEmpty()) {
            cachedAccessToken = fresh;
            // default 1 jam (SATUSEHAT umumnya kasih expires_in 3600)
            tokenExpiresAtMillis = System.currentTimeMillis() + (DEFAULT_TOKEN_TTL_SECONDS * 1000L);
        }
        return fresh;
    }

    /**
     * Force refresh token. Bersihkan cache dan request baru.
     * Berguna kalau dapat error 401 dari FHIR endpoint.
     */
    public String forceRefreshToken() {
        cachedAccessToken = null;
        tokenExpiresAtMillis = 0L;
        return getValidToken();
    }

    /**
     * UTC datetime saat ini dalam format ISO-8601 dengan offset +00:00.
     * Contoh: "2026-05-04T03:35:00+00:00"
     * Sesuai Playbook v5.1 hal. 41 (semua datetime SATUSEHAT pakai UTC+00).
     */
    public String getUtcDatetimeNow() {
        ZonedDateTime nowUtc = ZonedDateTime.now(ZoneOffset.UTC);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx");
        return nowUtc.format(fmt);
    }

    /**
     * Membangun HttpHeaders standar untuk endpoint FHIR SATUSEHAT.
     * Bearer token + Content-Type application/json.
     */
    public HttpHeaders buildAuthHeaders() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.add("Authorization", "Bearer " + getValidToken());
        return h;
    }

    /** Base URL FHIR SATUSEHAT, e.g. "https://api-satusehat-stg.kemkes.go.id/fhir-r4/v1". */
    public String getBaseUrl() {
        try {
            return koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception ex) {
            System.out.println("Notifikasi : " + ex);
            return "";
        }
    }

    /** Organization ID Fasyankes Perujuk (UUID dari registrasi SATUSEHAT). */
    public String getOrgIdPerujuk() {
        try {
            return koneksiDB.IDSATUSEHAT();
        } catch (Exception ex) {
            System.out.println("Notifikasi : " + ex);
            return "";
        }
    }

    /** ObjectMapper Jackson (shared instance). */
    public ObjectMapper getMapper() {
        return mapper;
    }

    // ===== FHIR Reference builders =====

    public String orgRef(String orgId) {
        return "Organization/" + safe(orgId);
    }

    public String patientRef(String patientIhsId) {
        return "Patient/" + safe(patientIhsId);
    }

    public String practitionerRef(String practitionerIhsId) {
        return "Practitioner/" + safe(practitionerIhsId);
    }

    public String encounterRef(String encounterId) {
        return "Encounter/" + safe(encounterId);
    }

    public String taskRef(String taskId) {
        return "Task/" + safe(taskId);
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

}