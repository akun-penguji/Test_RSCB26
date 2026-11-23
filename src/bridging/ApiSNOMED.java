package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fungsi.koneksiDB;
import java.awt.Component;
import java.awt.Cursor;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * SNOMED CT Service - membaca dari Snowstorm Server (FHIR & Browser API)
 * dan menyinkronkan ke tabel snomed lokal.
 */
public class ApiSNOMED {

    private final ObjectMapper mapper;
    private final String baseUrl;
    private final HttpHeaders defaultHeaders;
    private static final String SNOMED_SYSTEM = "http://snomed.info/sct";
    private static final String FHIR_VS      = "http://snomed.info/sct?fhir_vs";
    private static final String BRANCH        = "MAIN";
    private Component parentComponent;

    public ApiSNOMED() {
        this.mapper     = new ObjectMapper();
        this.baseUrl    = koneksiDB.URLAPISNOWSTORM(); // baca dari database.xml
        this.defaultHeaders = new HttpHeaders();
        this.defaultHeaders.add("Accept",       "application/json");
        this.defaultHeaders.add("Content-Type", "application/json");
    }

    public ApiSNOMED(Component parentComponent) {
        this();
        this.parentComponent = parentComponent;
    }

    public void setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    public boolean isConfigured() {
        return baseUrl != null && !baseUrl.trim().isEmpty();
    }

    // =========================================================
    // PENCARIAN LEWAT SNOWSTORM BROWSER API
    // Endpoint: GET /browser/{branch}/concepts?term=...&limit=...&offset=...
    // =========================================================
    public SnomedSearchResult searchBrowser(String keyword, int limit, int offset) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return createErrorResult("Keyword tidak boleh kosong");
        }
        if (!isConfigured()) {
            return createErrorResult("URL Snowstorm belum dikonfigurasi di database.xml (URLAPISNOWSTORM)");
        }
        setCursor(true);
        try {
            String url = baseUrl + "/browser/" + BRANCH + "/concepts"
                    + "?term="   + URLEncoder.encode(keyword.trim(), StandardCharsets.UTF_8)
                    + "&limit="  + limit
                    + "&offset=" + offset
                    + "&activeFilter=true";

            System.out.println("Snowstorm Browser API: " + url);

            HttpEntity<String> req = new HttpEntity<>(defaultHeaders);
            ResponseEntity<String> resp = getRest().exchange(url, HttpMethod.GET, req, String.class);

            if (resp.getStatusCode().value() >= 200 && resp.getStatusCode().value() < 300
                    && resp.getBody() != null) {
                return parseBrowserResponse(resp.getBody(), keyword, limit, offset);
            } else {
                return createErrorResult("Server Snowstorm error: " + resp.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error Snowstorm Browser API: " + e.getMessage());
            return createErrorResult("Gagal koneksi ke Snowstorm: " + e.getMessage());
        } finally {
            setCursor(false);
        }
    }

    // =========================================================
    // PENCARIAN LEWAT SNOWSTORM FHIR API
    // Endpoint: GET /fhir/ValueSet/$expand?url=...&filter=...&count=...
    // =========================================================
    public SnomedSearchResult searchFhir(String keyword, int limit, int offset) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return createErrorResult("Keyword tidak boleh kosong");
        }
        if (!isConfigured()) {
            return createErrorResult("URL Snowstorm belum dikonfigurasi di database.xml (URLAPISNOWSTORM)");
        }
        setCursor(true);
        try {
            String url = baseUrl + "/fhir/ValueSet/$expand"
                    + "?url="    + URLEncoder.encode(FHIR_VS, StandardCharsets.UTF_8)
                    + "&filter=" + URLEncoder.encode(keyword.trim(), StandardCharsets.UTF_8)
                    + "&count="  + limit
                    + "&offset=" + offset;

            System.out.println("Snowstorm FHIR API: " + url);

            HttpHeaders fhirHeaders = new HttpHeaders();
            fhirHeaders.add("Accept",       "application/fhir+json");
            fhirHeaders.add("Content-Type", "application/fhir+json");

            HttpEntity<String> req = new HttpEntity<>(fhirHeaders);
            ResponseEntity<String> resp = getRest().exchange(url, HttpMethod.GET, req, String.class);

            if (resp.getStatusCode().value() >= 200 && resp.getStatusCode().value() < 300
                    && resp.getBody() != null) {
                return parseFhirResponse(resp.getBody(), keyword, limit, offset);
            } else {
                return createErrorResult("FHIR server error: " + resp.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error Snowstorm FHIR API: " + e.getMessage());
            return createErrorResult("Gagal koneksi FHIR: " + e.getMessage());
        } finally {
            setCursor(false);
        }
    }

    // =========================================================
    // SINKRONISASI KE TABEL LOKAL snomed
    // INSERT IGNORE INTO snomed (system, code, display) VALUES (?,?,?)
    // =========================================================
    public SyncResult syncToLocalDB(String keyword, int limit, Connection koneksi) {
        SyncResult result = new SyncResult();

        if (!isConfigured()) {
            result.setSuccess(false);
            result.setMessage("URL Snowstorm belum dikonfigurasi di database.xml (URLAPISNOWSTORM)");
            JOptionPane.showMessageDialog(null, result.getMessage());
            return result;
        }

        setCursor(true);
        try {
            // Coba Browser API dulu, fallback ke FHIR
            SnomedSearchResult searchResult = searchBrowser(keyword, limit, 0);
            if (!searchResult.isSuccess() || searchResult.getTotalResults() == 0) {
                System.out.println("Browser API tidak berhasil, coba FHIR API...");
                searchResult = searchFhir(keyword, limit, 0);
            }

            if (!searchResult.isSuccess()) {
                result.setSuccess(false);
                result.setMessage(searchResult.getErrorMessage());
                JOptionPane.showMessageDialog(null, "Gagal ambil data dari Snowstorm:\n" + searchResult.getErrorMessage());
                return result;
            }

            if (searchResult.getTotalResults() == 0) {
                result.setSuccess(true);
                result.setMessage("Tidak ada hasil ditemukan untuk keyword: " + keyword);
                result.setSavedCount(0);
                return result;
            }

            // Simpan ke tabel snomed lokal
            int saved = 0;
            int skipped = 0;
            PreparedStatement ps = null;
            try {
                ps = koneksi.prepareStatement(
                    "INSERT IGNORE INTO snomed (system, code, display) VALUES (?,?,?)");

                for (JsonNode concept : searchResult.getConcepts()) {
                    String system  = concept.has("system")  ? concept.get("system").asText()  : SNOMED_SYSTEM;
                    String code    = concept.has("code")    ? concept.get("code").asText()    : "";
                    String display = concept.has("display") ? concept.get("display").asText() : "";

                    if (code.isEmpty()) { skipped++; continue; }

                    ps.setString(1, system);
                    ps.setString(2, code);
                    ps.setString(3, display);
                    ps.addBatch();
                    saved++;
                }

                if (saved > 0) {
                    int[] counts = ps.executeBatch();
                    int inserted = 0;
                    for (int c : counts) {
                        if (c > 0) inserted++;
                    }
                    result.setSavedCount(inserted);
                    result.setMessage("Berhasil menyimpan " + inserted + " data SNOMED baru ke lokal"
                            + (skipped > 0 ? " (" + skipped + " dilewati)" : ""));
                } else {
                    result.setSavedCount(0);
                    result.setMessage("Tidak ada data valid untuk disimpan");
                }
                result.setSuccess(true);

            } catch (Exception e) {
                System.err.println("Error sync ke DB lokal: " + e.getMessage());
                result.setSuccess(false);
                result.setMessage("Gagal menyimpan ke database lokal: " + e.getMessage());
                JOptionPane.showMessageDialog(null, result.getMessage());
            } finally {
                if (ps != null) try { ps.close(); } catch (Exception ex) {}
            }

        } finally {
            setCursor(false);
        }
        return result;
    }

    // =========================================================
    // PARSE BROWSER API RESPONSE
    // =========================================================
    private SnomedSearchResult parseBrowserResponse(String json, String keyword, int limit, int offset) {
        try {
            JsonNode root = mapper.readTree(json);
            SnomedSearchResult result = new SnomedSearchResult();
            result.setSuccess(true);
            result.setKeyword(keyword);
            result.setLimit(limit);
            result.setOffset(offset);

            if (!root.has("items")) {
                result.setTotalResults(0);
                return result;
            }

            JsonNode items = root.get("items");
            result.setTotalResults(root.has("total") ? root.get("total").asInt() : items.size());

            ArrayNode concepts = mapper.createArrayNode();
            for (JsonNode item : items) {
                ObjectNode node = mapper.createObjectNode();
                node.put("code",    item.has("conceptId") ? item.get("conceptId").asText() : "");
                node.put("system",  SNOMED_SYSTEM);
                // preferredTerm > pt.term > fsn.term
                String display = "";
                if (item.has("pt") && item.get("pt").has("term")) {
                    display = item.get("pt").get("term").asText();
                } else if (item.has("fsn") && item.get("fsn").has("term")) {
                    display = item.get("fsn").get("term").asText();
                }
                node.put("display", display);
                concepts.add(node);
            }
            result.setConcepts(concepts);
            return result;

        } catch (Exception e) {
            System.err.println("Error parse Browser response: " + e.getMessage());
            return createErrorResult("Gagal parse respons Snowstorm Browser API");
        }
    }

    // =========================================================
    // PARSE FHIR VALUESET EXPANSION RESPONSE
    // =========================================================
    private SnomedSearchResult parseFhirResponse(String json, String keyword, int limit, int offset) {
        try {
            JsonNode root = mapper.readTree(json);
            SnomedSearchResult result = new SnomedSearchResult();
            result.setSuccess(true);
            result.setKeyword(keyword);
            result.setLimit(limit);
            result.setOffset(offset);

            if (!root.has("expansion") || !root.get("expansion").has("contains")) {
                result.setTotalResults(0);
                return result;
            }

            JsonNode contains = root.get("expansion").get("contains");
            int total = root.get("expansion").has("total")
                    ? root.get("expansion").get("total").asInt() : contains.size();
            result.setTotalResults(total);

            ArrayNode concepts = mapper.createArrayNode();
            for (JsonNode c : contains) {
                ObjectNode node = mapper.createObjectNode();
                node.put("code",    c.has("code")    ? c.get("code").asText()    : "");
                node.put("system",  c.has("system")  ? c.get("system").asText()  : SNOMED_SYSTEM);
                node.put("display", c.has("display") ? c.get("display").asText() : "");
                concepts.add(node);
            }
            result.setConcepts(concepts);
            return result;

        } catch (Exception e) {
            System.err.println("Error parse FHIR response: " + e.getMessage());
            return createErrorResult("Gagal parse respons FHIR");
        }
    }

    private SnomedSearchResult createErrorResult(String msg) {
        SnomedSearchResult r = new SnomedSearchResult();
        r.setSuccess(false);
        r.setErrorMessage(msg);
        r.setTotalResults(0);
        r.setConcepts(mapper.createArrayNode());
        return r;
    }

    private void setCursor(boolean wait) {
        if (parentComponent != null) {
            parentComponent.setCursor(wait
                    ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                    : Cursor.getDefaultCursor());
        }
    }

    private RestTemplate getRest() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("SSL");
        TrustManager[] trustManagers = {
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkServerTrusted(X509Certificate[] c, String a) throws CertificateException {}
                public void checkClientTrusted(X509Certificate[] c, String a) throws CertificateException {}
            }
        };
        sslContext.init(null, trustManagers, new SecureRandom());
        SSLSocketFactory sslFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        Scheme scheme = new Scheme("https", 443, sslFactory);
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(scheme);
        return new RestTemplate(factory);
    }

    // =========================================================
    // INNER CLASS: SnomedSearchResult
    // =========================================================
    public static class SnomedSearchResult {
        private boolean   success;
        private String    errorMessage;
        private String    keyword;
        private int       totalResults;
        private int       limit;
        private int       offset;
        private ArrayNode concepts;

        public boolean   isSuccess()        { return success; }
        public void      setSuccess(boolean v)      { this.success = v; }
        public String    getErrorMessage()  { return errorMessage; }
        public void      setErrorMessage(String v)  { this.errorMessage = v; }
        public String    getKeyword()       { return keyword; }
        public void      setKeyword(String v)       { this.keyword = v; }
        public int       getTotalResults()  { return totalResults; }
        public void      setTotalResults(int v)     { this.totalResults = v; }
        public int       getLimit()         { return limit; }
        public void      setLimit(int v)            { this.limit = v; }
        public int       getOffset()        { return offset; }
        public void      setOffset(int v)           { this.offset = v; }
        public ArrayNode getConcepts()      { return concepts; }
        public void      setConcepts(ArrayNode v)   { this.concepts = v; }
    }

    // =========================================================
    // IMPORT RF2 EXTENDED MAP FILE KE TABEL LOKAL
    // Format: der2_iisssccRefset_ExtendedMapSnapshot_*.txt
    // Kolom (tab-delimited): id effectiveTime active moduleId refsetId
    //   referencedComponentId mapGroup mapPriority mapRule mapAdvice
    //   mapTarget correlationId mapCategoryId
    //
    // tableName: "snomedct_icd10_rf2" atau "snomedct_icd9_rf2"
    // Hanya insert record: active=1, mapCategoryId=447637006 (properly classified)
    // =========================================================
    public SyncResult importRF2MapFile(java.io.File file, Connection koneksi, String tableName) {
        SyncResult result = new SyncResult();

        // Buat tabel jika belum ada
        try {
            koneksi.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS `" + tableName + "` ("
                + "  id              VARCHAR(36)  NOT NULL,"
                + "  effective_time  VARCHAR(8)   DEFAULT NULL,"
                + "  active          TINYINT(1)   DEFAULT 1,"
                + "  snomed_code     VARCHAR(20)  NOT NULL,"
                + "  map_group       INT          NOT NULL DEFAULT 1,"
                + "  map_priority    INT          NOT NULL DEFAULT 1,"
                + "  map_rule        VARCHAR(500) DEFAULT NULL,"
                + "  map_advice      TEXT         DEFAULT NULL,"
                + "  icd_code        VARCHAR(20)  DEFAULT NULL,"
                + "  map_category_id BIGINT       DEFAULT NULL,"
                + "  PRIMARY KEY (id),"
                + "  KEY idx_snomed (snomed_code, active)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Gagal buat tabel " + tableName + ": " + e.getMessage());
            return result;
        }

        int inserted = 0, skipped = 0, batchCount = 0;
        final int BATCH_SIZE = 500;
        final long CATEGORY_PROPER = 447637006L; // properly classified

        try (java.io.BufferedReader br = new java.io.BufferedReader(
                new java.io.InputStreamReader(
                    new java.io.FileInputStream(file),
                    java.nio.charset.StandardCharsets.UTF_8))) {

            String line = br.readLine(); // skip header
            if (line == null) {
                result.setSuccess(false);
                result.setMessage("File kosong atau bukan format RF2.");
                return result;
            }

            PreparedStatement ps = koneksi.prepareStatement(
                "INSERT IGNORE INTO `" + tableName + "` "
                + "(id, effective_time, active, snomed_code, map_group, map_priority, "
                + " map_rule, map_advice, icd_code, map_category_id) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?)");

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] f = line.split("\t", -1);
                if (f.length < 13) { skipped++; continue; }

                // Hanya record aktif
                if (!"1".equals(f[2].trim())) { skipped++; continue; }

                // Hanya mapCategoryId = 447637006 (properly classified)
                String catStr = f[12].trim();
                if (catStr.isEmpty()) { skipped++; continue; }
                try {
                    if (Long.parseLong(catStr) != CATEGORY_PROPER) { skipped++; continue; }
                } catch (NumberFormatException nfe) { skipped++; continue; }

                String icdCode = f[10].trim(); // mapTarget (boleh kosong = NO MAP)

                try {
                    ps.setString(1, f[0].trim());              // id
                    ps.setString(2, f[1].trim());              // effectiveTime
                    ps.setInt(3, 1);                           // active
                    ps.setString(4, f[5].trim());              // snomed_code (referencedComponentId)
                    ps.setInt(5, Integer.parseInt(f[6].trim())); // mapGroup
                    ps.setInt(6, Integer.parseInt(f[7].trim())); // mapPriority
                    ps.setString(7, f[8].trim());              // mapRule
                    ps.setString(8, f[9].trim());              // mapAdvice
                    ps.setString(9, icdCode.isEmpty() ? null : icdCode); // icd_code
                    ps.setLong(10, CATEGORY_PROPER);           // map_category_id
                    ps.addBatch();
                    batchCount++;
                    inserted++;
                } catch (Exception ex) {
                    skipped++;
                    continue;
                }

                if (batchCount >= BATCH_SIZE) {
                    ps.executeBatch();
                    batchCount = 0;
                }
            }

            if (batchCount > 0) ps.executeBatch();
            ps.close();

            result.setSuccess(true);
            result.setSavedCount(inserted);
            result.setMessage("Import RF2 selesai ke tabel '" + tableName + "'.\n"
                + "Berhasil: " + inserted + " record\n"
                + "Dilewati (inactive/kategori lain/duplikat): " + skipped + " record");

        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Gagal import RF2: " + e.getMessage());
        }
        return result;
    }

    // =========================================================
    // INNER CLASS: SyncResult
    // =========================================================
    public static class SyncResult {
        private boolean success;
        private String  message;
        private int     savedCount;

        public boolean isSuccess()           { return success; }
        public void    setSuccess(boolean v) { this.success = v; }
        public String  getMessage()          { return message; }
        public void    setMessage(String v)  { this.message = v; }
        public int     getSavedCount()       { return savedCount; }
        public void    setSavedCount(int v)  { this.savedCount = v; }
    }
}
