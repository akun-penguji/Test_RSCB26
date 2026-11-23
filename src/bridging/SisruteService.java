/*
 * SisruteService.java
 *
 * Helper service untuk endpoint Sisrute (Rujukan SATUSEHAT) BPJS Kesehatan.
 * Mengikuti pattern ApiBPJS / ApiSatuSehat existing di SIMRS Khanza.
 *
 * Endpoint yang di-handle:
 *   1. POST /Rujukan/GetKriteriaRujukan
 *   2. POST /Sisrute/GetFaskesRujukan
 *   3. POST /Sisrute/postKunjungan        (Insert Rujukan)
 *   4. DELETE /Rujukan/Delete
 *
 * Header autentikasi (sama dengan VClaim):
 *   X-Cons-ID, X-Timestamp, X-Signature, user_key
 *
 * Catatan:
 *   - Cons-ID, Secret Key, user_key reuse dari ApiBPJS (VClaim).
 *   - Base URL Sisrute di-derive otomatis dari URLAPIBPJS (ganti /vclaim-rest -> /sisrute-rest).
 *   - Tidak perlu konfigurasi tambahan di database.xml atau tabel setting.
 *   - Response Sisrute diasumsikan plain JSON (tidak encrypted seperti VClaim).
 *
 * @author dibuat untuk SIMRS Khanza - Bridging Sisrute SATUSEHAT
 */

package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.koneksiDB;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class SisruteService {

    // ==== Konfigurasi (dari koneksiDB) ====
    private String baseUrl;       // contoh: https://apijkn.bpjs-kesehatan.go.id/sisrute-rest
    private String userKey;       // user_key khusus Sisrute
    private String consId;
    private String secretKey;

    // ==== Helper (reuse signature dari ApiBPJS) ====
    private final ApiBPJS api = new ApiBPJS();
    private final ObjectMapper mapper = new ObjectMapper();

    // ==== Workspace request ====
    private HttpHeaders headers;
    private HttpEntity<String> requestEntity;
    private JsonNode root;
    private String utc;
    private String responseBody;

    public SisruteService() {
        try {
            baseUrl   = deriveSisruteUrlFromBpjsUrl(koneksiDB.URLAPIBPJS());
            userKey   = koneksiDB.USERKEYAPIBPJS();   // reuse user_key VClaim BPJS
            consId    = koneksiDB.CONSIDAPIBPJS();
            secretKey = koneksiDB.SECRETKEYAPIBPJS();
        } catch (Exception ex) {
            System.out.println("SisruteService init error : " + ex);
        }
    }

    /**
     * Derive URL Sisrute dari URL VClaim BPJS yang sudah ada di koneksiDB.
     * Pattern URL BPJS:
     *   VClaim:  https://apijkn.bpjs-kesehatan.go.id/vclaim-rest
     *   Sisrute: https://apijkn.bpjs-kesehatan.go.id/sisrute-rest
     *
     * Jadi cukup ganti '/vclaim-rest' jadi '/sisrute-rest', atau '/vclaim' jadi '/sisrute'.
     * Untuk environment dev: /vclaim-rest -> /vclaim-sisrute-rest (kadang Kemkes pakai pattern ini)
     */
    private String deriveSisruteUrlFromBpjsUrl(String urlBpjs) {
        if (urlBpjs == null || urlBpjs.isEmpty()) return "";
        String url = urlBpjs.trim();
        if (url.endsWith("/")) url = url.substring(0, url.length() - 1);

        // Coba pola umum:
        if (url.contains("/vclaim-rest")) {
            return url.replace("/vclaim-rest", "/sisrute-rest");
        }
        if (url.contains("/vclaim/")) {
            return url.replace("/vclaim/", "/sisrute/");
        }
        if (url.endsWith("/vclaim")) {
            return url.substring(0, url.length() - "/vclaim".length()) + "/sisrute-rest";
        }
        // Kalau URL sudah sisrute-rest, pakai apa adanya
        if (url.contains("sisrute")) {
            return url;
        }
        // Fallback: tambahkan /sisrute-rest di akhir base
        // (hilangkan path terakhir, ganti dengan sisrute-rest)
        int lastSlash = url.lastIndexOf('/');
        if (lastSlash > "https://".length()) {
            return url.substring(0, lastSlash) + "/sisrute-rest";
        }
        return url + "/sisrute-rest";
    }

    // =================================================================
    //  HEADER & HTTP CLIENT
    // =================================================================

    /** Bangun header standar Sisrute: Cons-ID, Timestamp, Signature, user_key. */
    private HttpHeaders buildHeaders() {
        utc = String.valueOf(api.GetUTCdatetimeAsString());
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.add("X-Cons-ID", consId);
        h.add("X-Timestamp", utc);
        h.add("X-Signature", api.getHmac(utc));
        h.add("user_key", userKey);
        return h;
    }

    /**
     * RestTemplate yang support DELETE dengan body
     * (DELETE standar Spring tidak boleh punya body, padahal Sisrute butuh).
     */
    private RestTemplate getRestWithDeleteBody() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        TrustManager[] trustManagers = {
            new X509TrustManager() {
                @Override public X509Certificate[] getAcceptedIssuers() { return null; }
                @Override public void checkServerTrusted(X509Certificate[] a, String b) throws CertificateException {}
                @Override public void checkClientTrusted(X509Certificate[] a, String b) throws CertificateException {}
            }
        };
        sslContext.init(null, trustManagers, new SecureRandom());
        SSLSocketFactory sslFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        Scheme scheme = new Scheme("https", 443, sslFactory);

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory() {
            @Override
            protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
                if (HttpMethod.DELETE == httpMethod) {
                    return new HttpEntityEnclosingDeleteRequest(uri);
                }
                return super.createHttpUriRequest(httpMethod, uri);
            }
        };
        factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(scheme);
        RestTemplate rt = new RestTemplate();
        rt.setRequestFactory(factory);
        return rt;
    }

    /** Custom request class untuk DELETE-with-body. */
    public static class HttpEntityEnclosingDeleteRequest extends HttpEntityEnclosingRequestBase {
        public HttpEntityEnclosingDeleteRequest(final URI uri) {
            super();
            setURI(uri);
        }
        @Override
        public String getMethod() {
            return "DELETE";
        }
    }

    // =================================================================
    //  ENDPOINT 1 : GET KRITERIA RUJUKAN
    //  POST /Rujukan/GetKriteriaRujukan
    // =================================================================

    /**
     * Ambil daftar kriteria rujukan yang harus dijawab user.
     *
     * @param kodeFaskesSatuSehat kode faskes asal (Satu Sehat)
     * @param kodeDiagnosa        ICD-10 (contoh: "I10")
     * @param encounterReference  reference encounter aktif, contoh: "Encounter/9f476605-..."
     *                            Boleh kirim UUID saja, akan auto-prefix.
     * @return JsonNode response body. Cek metaData.code == 200 untuk sukses.
     *         Field response.kriteriaRujukan[] berisi pertanyaan untuk dialog.
     *         Field response.jejaringWilayahRujukan[] berisi daftar provinsi.
     * @throws Exception kalau gagal koneksi / parsing
     */
    public JsonNode getKriteriaRujukan(String kodeFaskesSatuSehat,
                                       String kodeDiagnosa,
                                       String encounterReference) throws Exception {
        String url = baseUrl + "/Rujukan/GetKriteriaRujukan";
        String encounterRef = normalizeEncounterRef(encounterReference);

        String body = "{"
                + "\"kodeFaskesSatuSehat\":\"" + safe(kodeFaskesSatuSehat) + "\","
                + "\"kodeDiagnosa\":\"" + safe(kodeDiagnosa) + "\","
                + "\"encounter\":{"
                +   "\"reference\":\"" + encounterRef + "\""
                + "}"
                + "}";

        return doPost(url, body);
    }

    // =================================================================
    //  ENDPOINT 2 : GET FASKES RUJUKAN
    //  POST /Sisrute/GetFaskesRujukan
    // =================================================================

    /**
     * Cari daftar faskes tujuan rujukan.
     *
     * @param kodeFaskesSatuSehat   kode faskes asal Satu Sehat
     * @param kodeDiagnosa          ICD-10
     * @param kodeSpesialis         kode sub-spesialis (3 digit, contoh: "095")
     * @param tglRencanaKunjungan   format: "yyyy-MM-dd" (atau "dd-MM-yyyy" sesuai docs)
     * @param kriteriaJsonItem      JSON string PERSIS sesuai format docs untuk
     *                              field "kriteriaRujukan.item"
     *                              Contoh:
     *                              [{"linkId":"3216","text":"Terapi/Pengobatan",
     *                                "answer":[{"valueBoolean":false}]}, ...]
     * @param kodePropinsi          kode provinsi jejaring tujuan (contoh: "31")
     * @param namaPropinsi          nama provinsi (contoh: "DKI Jakarta")
     * @param kodeKabupaten         kode kabupaten (boleh "")
     * @param namaKabupaten         nama kabupaten (boleh "")
     * @param encounterReference    reference encounter aktif
     */
    public JsonNode getFaskesRujukan(String kodeFaskesSatuSehat,
                                     String kodeDiagnosa,
                                     String kodeSpesialis,
                                     String tglRencanaKunjungan,
                                     String kriteriaJsonItem,
                                     String kodePropinsi,
                                     String namaPropinsi,
                                     String kodeKabupaten,
                                     String namaKabupaten,
                                     String encounterReference) throws Exception {
        String url = baseUrl + "/Sisrute/GetFaskesRujukan";
        String encounterRef = normalizeEncounterRef(encounterReference);

        String body = "{"
                + "\"kodeFaskesSatuSehat\":\"" + safe(kodeFaskesSatuSehat) + "\","
                + "\"kodeDiagnosa\":\"" + safe(kodeDiagnosa) + "\","
                + "\"kodeSpesialis\":\"" + safe(kodeSpesialis) + "\","
                + "\"tglRencanaKunjungan\":\"" + safe(tglRencanaKunjungan) + "\","
                + "\"kriteriaRujukan\":{"
                +   "\"item\":" + (kriteriaJsonItem == null || kriteriaJsonItem.trim().isEmpty()
                                    ? "[]" : kriteriaJsonItem)
                + "},"
                + "\"codeJejaringWilayah\":{"
                +   "\"kodePropinsi\":\""  + safe(kodePropinsi)  + "\","
                +   "\"namaPropinsi\":\""  + safe(namaPropinsi)  + "\","
                +   "\"kodeKabupaten\":\"" + safe(kodeKabupaten) + "\","
                +   "\"namaKabupaten\":\"" + safe(namaKabupaten) + "\""
                + "},"
                + "\"encounter\":{"
                +   "\"reference\":\"" + encounterRef + "\""
                + "}"
                + "}";

        return doPost(url, body);
    }

    // =================================================================
    //  ENDPOINT 3 : INSERT RUJUKAN (POST KUNJUNGAN)
    //  POST /Sisrute/postKunjungan
    // =================================================================

    /**
     * Kirim data rujukan lengkap ke Sisrute.
     * Karena field-nya banyak, dibungkus dalam class InsertRujukanRequest.
     */
    public JsonNode insertRujukan(InsertRujukanRequest r) throws Exception {
        String url = baseUrl + "/Sisrute/postKunjungan";

        String body = "{"
            + "\"request\":{"
            +   "\"t_rujukan\":{"
            +     "\"noSep\":\""              + safe(r.noSep) + "\","
            +     "\"tglRujukan\":\""         + safe(r.tglRujukan) + "\","
            +     "\"tglRencanaKunjungan\":\""+ safe(r.tglRencanaKunjungan) + "\","
            +     "\"ppkDirujuk\":\""         + safe(r.ppkDirujuk) + "\","
            +     "\"jnsPelayanan\":\""       + safe(r.jnsPelayanan) + "\","
            +     "\"catatan\":\""            + escape(r.catatan) + "\","
            +     "\"diagRujukan\":\""        + safe(r.diagRujukan) + "\","
            +     "\"tipeRujukan\":\""        + safe(r.tipeRujukan) + "\","
            +     "\"poliRujukan\":\""        + safe(r.poliRujukan) + "\","
            +     "\"user\":\""               + safe(r.user) + "\","
            +     "\"satuSehatRujukan\":{"
            +       "\"kodeFaskesSatuSehat\":\""           + safe(r.kodeFaskesSatuSehat) + "\","
            +       "\"idPasienSatuSehat\":\""             + safe(r.idPasienSatuSehat) + "\","
            +       "\"kdppkSatuSehatTujuanRujukan\":\""   + safe(r.kdppkSatuSehatTujuanRujukan) + "\","
            +       "\"kdDokterSatuSehat\":\""             + safe(r.kdDokterSatuSehat) + "\","
            +       "\"encounter\":{"
            +         "\"reference\":\""                   + normalizeEncounterRef(r.encounterReference) + "\""
            +       "},"
            +       "\"patientInstruction\":\""            + escape(r.patientInstruction) + "\","
            +       "\"kriteriaRujukan\":{"
            +         "\"item\":" + (r.kriteriaJsonItem == null || r.kriteriaJsonItem.trim().isEmpty()
                                       ? "[]" : r.kriteriaJsonItem)
            +       "},"
            +       "\"keteranganRujukan\":\""             + escape(r.keteranganRujukan) + "\","
            +       "\"codeJejaringWilayah\":{"
            +         "\"kodePropinsi\":\""  + safe(r.kodePropinsi)  + "\","
            +         "\"namaPropinsi\":\""  + safe(r.namaPropinsi)  + "\","
            +         "\"kodeKabupaten\":\"" + safe(r.kodeKabupaten) + "\","
            +         "\"namaKabupaten\":\"" + safe(r.namaKabupaten) + "\""
            +       "}"
            +     "}"
            +   "}"
            + "}"
            + "}";

        return doPost(url, body);
    }

    /** DTO untuk Insert Rujukan supaya parameter tidak terlalu panjang. */
    public static class InsertRujukanRequest {
        // Data rujukan utama (sudah ada di Khanza)
        public String noSep;
        public String tglRujukan;            // yyyy-MM-dd
        public String tglRencanaKunjungan;   // yyyy-MM-dd
        public String ppkDirujuk;            // kode faskes BPJS
        public String jnsPelayanan;          // 1=Ranap, 2=Ralan
        public String catatan;
        public String diagRujukan;           // ICD-10
        public String tipeRujukan;           // 0=Penuh, 1=Partial, 2=Rujuk Balik
        public String poliRujukan;           // kode poli BPJS
        public String user;

        // Data Satu Sehat
        public String kodeFaskesSatuSehat;
        public String idPasienSatuSehat;
        public String kdppkSatuSehatTujuanRujukan;
        public String kdDokterSatuSehat;
        public String encounterReference;
        public String patientInstruction;
        public String kriteriaJsonItem;      // JSON array string
        public String keteranganRujukan;

        // Wilayah jejaring
        public String kodePropinsi;
        public String namaPropinsi;
        public String kodeKabupaten;
        public String namaKabupaten;
    }

    // =================================================================
    //  ENDPOINT 4 : DELETE RUJUKAN
    //  DELETE /Rujukan/Delete
    // =================================================================

    /**
     * Hapus rujukan yang sudah dikirim ke Sisrute.
     */
    public JsonNode deleteRujukan(String noRujukan,
                                  String user,
                                  String kodeFaskesSatuSehat,
                                  String idPasienSatuSehat,
                                  String kdppkSatuSehatTujuanRujukan,
                                  String kdDokterSatuSehat,
                                  String encounterReference,
                                  String patientInstruction,
                                  String keteranganRujukan) throws Exception {
        String url = baseUrl + "/Rujukan/Delete";
        String encounterRef = normalizeEncounterRef(encounterReference);

        String body = "{"
                + "\"request\":{"
                +   "\"t_rujukan\":{"
                +     "\"noRujukan\":\"" + safe(noRujukan) + "\","
                +     "\"user\":\""      + safe(user) + "\","
                +     "\"satuSehatRujukan\":{"
                +       "\"kodeFaskesSatuSehat\":\""         + safe(kodeFaskesSatuSehat) + "\","
                +       "\"idPasienSatuSehat\":\""           + safe(idPasienSatuSehat) + "\","
                +       "\"kdppkSatuSehatTujuanRujukan\":\"" + safe(kdppkSatuSehatTujuanRujukan) + "\","
                +       "\"kdDokterSatuSehat\":\""           + safe(kdDokterSatuSehat) + "\","
                +       "\"encounter\":{"
                +         "\"reference\":\"" + encounterRef + "\""
                +       "},"
                +       "\"patientInstruction\":\"" + escape(patientInstruction) + "\","
                +       "\"keteranganRujukan\":\""  + escape(keteranganRujukan)  + "\""
                +     "}"
                +   "}"
                + "}"
                + "}";

        return doDelete(url, body);
    }

    // =================================================================
    //  HTTP CALLERS
    // =================================================================

    private JsonNode doPost(String url, String body) throws Exception {
        headers = buildHeaders();
        requestEntity = new HttpEntity<>(body, headers);

        System.out.println("==== Sisrute POST ====");
        System.out.println("URL : " + url);
        System.out.println("Body: " + body);

        responseBody = api.getRest()
                .exchange(url, HttpMethod.POST, requestEntity, String.class)
                .getBody();

        System.out.println("Response: " + responseBody);
        root = mapper.readTree(responseBody);
        return root;
    }

    private JsonNode doDelete(String url, String body) throws Exception {
        headers = buildHeaders();
        HttpEntity<String> req = new HttpEntity<>(body, headers);

        System.out.println("==== Sisrute DELETE ====");
        System.out.println("URL : " + url);
        System.out.println("Body: " + body);

        responseBody = getRestWithDeleteBody()
                .exchange(url, HttpMethod.DELETE, req, String.class)
                .getBody();

        System.out.println("Response: " + responseBody);
        root = mapper.readTree(responseBody);
        return root;
    }

    // =================================================================
    //  UTILITY
    // =================================================================

    /** Tambahkan prefix "Encounter/" jika user kirim UUID polos. */
    private String normalizeEncounterRef(String ref) {
        if (ref == null) return "";
        String t = ref.trim();
        if (t.isEmpty()) return "";
        if (t.toLowerCase().startsWith("encounter/")) return t;
        return "Encounter/" + t;
    }

    /** Null-safe trim. */
    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    /** Escape karakter khusus JSON untuk konten teks bebas. */
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("\t", " ");
    }

    /** Helper cepat untuk cek code metadata. */
    public static boolean isOk(JsonNode response) {
        if (response == null) return false;
        JsonNode meta = response.path("metaData");
        return "200".equals(meta.path("code").asText());
    }

    /** Helper cepat untuk ambil pesan metadata. */
    public static String getMessage(JsonNode response) {
        if (response == null) return "Response kosong";
        return response.path("metaData").path("message").asText();
    }

    // =================================================================
    //  MAIN - untuk tes manual
    // =================================================================

    /**
     * Tes manual. Pastikan koneksiDB sudah punya method (semua reuse dari VClaim):
     *   - URLAPIBPJS()
     *   - CONSIDAPIBPJS()
     *   - SECRETKEYAPIBPJS()
     *   - USERKEYAPIBPJS()
     *
     * URL Sisrute di-derive otomatis dari URLAPIBPJS (tidak perlu konfig tambahan).
     *
     * Cara pakai:
     *   1. Aktifkan blok tes yang diinginkan (uncomment).
     *   2. Run as Java Application.
     */
    public static void main(String[] args) {
        SisruteService svc = new SisruteService();

        try {
            // ===== TES 1: GET KRITERIA RUJUKAN =====
            System.out.println("\n>>> TES GetKriteriaRujukan");
            JsonNode r1 = svc.getKriteriaRujukan(
                    "100005239",                                       // kodeFaskesSatuSehat
                    "I10",                                             // kodeDiagnosa
                    "Encounter/9f476605-d133-4cc6-bcac-626e2ecacded"   // encounter ref
            );
            System.out.println("OK? " + isOk(r1) + " | Msg: " + getMessage(r1));

            // ===== TES 2: GET FASKES RUJUKAN =====
            // Aktifkan setelah TES 1 sukses dan punya kriteria yang bisa dijawab.
            /*
            System.out.println("\n>>> TES GetFaskesRujukan");
            String kriteriaItem =
                "[" +
                "  {\"linkId\":\"3216\",\"text\":\"Terapy/Pengobatan\",\"answer\":[{\"valueBoolean\":false}]}," +
                "  {\"linkId\":\"3215\",\"text\":\"Tindakan Medis\",   \"answer\":[{\"valueString\":\"01.24\"}]}," +
                "  {\"linkId\":\"3214\",\"text\":\"Upaya Diagnosis\",  \"answer\":[{\"valueBoolean\":false}]}" +
                "]";
            JsonNode r2 = svc.getFaskesRujukan(
                    "100010951", "I10", "095",
                    "2026-03-01",
                    kriteriaItem,
                    "31", "DKI Jakarta", "", "",
                    "Encounter/9f476605-d133-4cc6-bcac-626e2ecacded"
            );
            System.out.println("OK? " + isOk(r2) + " | Msg: " + getMessage(r2));
            System.out.println("Jumlah faskes: " + r2.path("response").path("count").asInt());
            */

            // ===== TES 3: INSERT RUJUKAN =====
            /*
            System.out.println("\n>>> TES insertRujukan");
            InsertRujukanRequest req = new InsertRujukanRequest();
            req.noSep = "1001R0120126V000010";
            req.tglRujukan = "2026-03-03";
            req.tglRencanaKunjungan = "2026-03-03";
            req.ppkDirujuk = "0903R004";
            req.jnsPelayanan = "2";
            req.catatan = "tolong untuk dirujuk";
            req.diagRujukan = "I10";
            req.tipeRujukan = "0";
            req.poliRujukan = "005";
            req.user = "tester";
            req.kodeFaskesSatuSehat = "100010939";
            req.idPasienSatuSehat = "P20395452616";
            req.kdppkSatuSehatTujuanRujukan = "100025612";
            req.kdDokterSatuSehat = "10009880728";
            req.encounterReference = "897d7713-77cd-492e-bd1e-1fd8d7c2b33d";
            req.patientInstruction = "Rujukan ke RSUP HASAN SADIKIN";
            req.kriteriaJsonItem = kriteriaItem;
            req.keteranganRujukan = "Rujukan ke RSUP HASAN SADIKIN";
            req.kodePropinsi = "31";
            req.namaPropinsi = "DKI Jakarta";
            req.kodeKabupaten = "";
            req.namaKabupaten = "";
            JsonNode r3 = svc.insertRujukan(req);
            System.out.println("OK? " + isOk(r3) + " | Msg: " + getMessage(r3));
            System.out.println("noRujukan: " + r3.path("response").path("rujukan").path("noRujukan").asText());
            */

            // ===== TES 4: DELETE RUJUKAN =====
            /*
            System.out.println("\n>>> TES deleteRujukan");
            JsonNode r4 = svc.deleteRujukan(
                    "1001R0120326B000017", "tester",
                    "100010939", "P20395452616", "100025548", "10009880728",
                    "897d7713-77cd-492e-bd1e-1fd8d7c2b33d",
                    "Rujukan ke RSUP HASAN SADIKIN",
                    "Rujukan ke RSUP HASAN SADIKIN"
            );
            System.out.println("OK? " + isOk(r4) + " | Msg: " + getMessage(r4));
            */

        } catch (Exception ex) {
            System.out.println("Tes error: " + ex);
            ex.printStackTrace();
        }
    }
}