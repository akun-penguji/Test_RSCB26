package bridging;

import com.google.gson.*;
import fungsi.koneksiDB;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import java.io.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import javax.swing.SwingUtilities;

/**
 * Service untuk menangani SOAP parsing menggunakan Google Gemini API Updated
 * untuk menggunakan SOAPResult dari bridging package
 *
 * @author YourName
 */
public class GeminiService {

    // Gemini API Configuration - sekarang diambil dari database.xml
    private static String GEMINI_API_KEY = null;
    private static String GEMINI_API_BASE_URL = null;

    private static final String[] GEMINI_MODELS = {
        "gemini-1.5-flash",
        "gemini-1.5-pro",
        "gemini-pro"
    };

    private int currentModelIndex = 0;
    private Gson gson;
    private CloseableHttpClient httpClient;

    // Callback interface untuk hasil SOAP parsing - UPDATED to use bridging.SOAPResult
    public interface SOAPCallback {

        void onSOAPParsingComplete(bridging.SOAPResult soapResult);

        void onSOAPParsingError(String error);

        String getVitalSignsFromForm(); // Method untuk ambil vital signs dari form
    }

    private SOAPCallback callback;

    /**
     * Constructor - Load configuration dari database.xml
     */
    public GeminiService() {
        this.gson = new Gson();
        this.httpClient = HttpClients.createDefault();

        // Load configuration dari database.xml
        loadConfiguration();

        // Gemini service initialized with database configuration
    }

    /**
     * Load konfigurasi Gemini dari database.xml
     */
    private void loadConfiguration() {
        try {
            GEMINI_API_KEY = koneksiDB.GEMINIAPIKEY();
            GEMINI_API_BASE_URL = koneksiDB.GEMINIAPIURL();

            if (GEMINI_API_KEY == null || GEMINI_API_KEY.trim().isEmpty()) {
                // // System.out.println("⚠️ WARNING: Gemini API Key tidak ditemukan di database.xml!");
                // // System.out.println("   Silakan tambahkan entry GEMINIAPIKEY di database.xml");
            } else {
                // Gemini API Key loaded from database.xml
                // Gemini API URL: " + GEMINI_API_BASE_URL
            }

        } catch (Exception e) {
            // // System.out.println("❌ Error loading Gemini configuration: " + e.getMessage());
            // Fallback ke default jika ada error
            GEMINI_API_KEY = "";
            GEMINI_API_BASE_URL = "https://generativelanguage.googleapis.com/v1/models";
        }
    }

    /**
     * Reload configuration dari database.xml (jika ada perubahan)
     */
    public void reloadConfiguration() {
        // // System.out.println("🔄 Reloading Gemini configuration from database.xml...");
        loadConfiguration();
    }

    /**
     * Validasi apakah konfigurasi Gemini sudah benar
     */
    public boolean isConfigurationValid() {
        return GEMINI_API_KEY != null
                && !GEMINI_API_KEY.trim().isEmpty()
                && GEMINI_API_BASE_URL != null
                && !GEMINI_API_BASE_URL.trim().isEmpty();
    }

    /**
     * Set callback untuk hasil SOAP parsing
     */
    public void setSOAPCallback(SOAPCallback callback) {
        this.callback = callback;
    }

    /**
     * Parse Gemini response and create bridging.SOAPResult UPDATED to return
     * bridging.SOAPResult
     */
    private bridging.SOAPResult parseGeminiResponse(String responseBody) throws Exception {
        try {
            // Parsing Gemini response...

            JsonObject response = gson.fromJson(responseBody, JsonObject.class);

            if (response.has("candidates") && response.getAsJsonArray("candidates").size() > 0) {
                JsonObject candidate = response.getAsJsonArray("candidates").get(0).getAsJsonObject();

                if (candidate.has("content")) {
                    JsonObject content = candidate.getAsJsonObject("content");

                    if (content.has("parts") && content.getAsJsonArray("parts").size() > 0) {
                        JsonArray parts = content.getAsJsonArray("parts");
                        String text = parts.get(0).getAsJsonObject().get("text").getAsString();

                        // // System.out.println("📝 Extracted text from Gemini:");
                        // // System.out.println(text);

                        // Extract dan parse JSON - UPDATED to use bridging.SOAPResult
                        bridging.SOAPResult result = extractSOAPFromText(text);

                        // Validasi result
                        if (result != null && !result.isEmpty()) {
                            // Successfully parsed SOAP from Gemini response
                            return result;
                        } else {
                            // // System.out.println("⚠️ Empty SOAP result, trying fallback...");
                        }
                    }
                }
            }

            // // System.out.println("⚠️ No valid content found, trying direct text extraction...");
            return extractSOAPFromText(responseBody);

        } catch (Exception e) {
            // // System.out.println("❌ Error parsing Gemini response: " + e.getMessage());
            e.printStackTrace();
            // // System.out.println("🔧 Trying fallback parsing...");
            return extractSOAPFromText(responseBody);
        }
    }

    /**
     * Extract SOAP dari text response UPDATED to return bridging.SOAPResult
     */
    private bridging.SOAPResult extractSOAPFromText(String text) {
        // Extracting SOAP from text...

        try {
            String cleanText = text;

            // 1. Extract from markdown code block if exists
            if (text.contains("```json")) {
                try {
                    int jsonStart = text.indexOf("```json") + 7;
                    int jsonEnd = text.indexOf("```", jsonStart);
                    if (jsonEnd != -1) {
                        cleanText = text.substring(jsonStart, jsonEnd).trim();
                        // // System.out.println("📋 Extracted from markdown: " + cleanText.substring(0, Math.min(200, cleanText.length())) + "...");
                    }
                } catch (Exception e) {
                    // // System.out.println("⚠️ Error extracting from markdown: " + e.getMessage());
                }
            }

            // 2. Find JSON object in text
            int jsonStart = cleanText.indexOf("{");
            int jsonEnd = findMatchingCloseBrace(cleanText, jsonStart);

            if (jsonStart != -1 && jsonEnd != -1) {
                String rawJsonStr = cleanText.substring(jsonStart, jsonEnd + 1);
                // Raw JSON string (first 300 chars)

                String jsonStr = cleanJsonString(rawJsonStr);
                // Cleaned JSON string (first 300 chars)

                try {
                    // Attempting JsonParser parsing...

                    com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
                    com.google.gson.JsonElement element = parser.parse(jsonStr);

                    if (element != null && element.isJsonObject()) {
                        JsonObject soapJson = element.getAsJsonObject();
                        // JsonParser succeeded!

                        // Extract values using bridging.SOAPResult constructor
                        String subjective = getJsonValueSafe(soapJson, "subjective", "Tidak disebutkan");
                        String objective = getJsonValueSafe(soapJson, "objective", "Tidak disebutkan");
                        String assessment = getJsonValueSafe(soapJson, "assessment", "Tidak disebutkan");
                        String plan = getJsonValueSafe(soapJson, "plan", "Tidak disebutkan");
                        String intervention = getJsonValueSafe(soapJson, "intervention", "Tidak disebutkan");
                        String evaluation = getJsonValueSafe(soapJson, "evaluation", "Tidak disebutkan");

                        // Extracted values via JsonParser:
                        // // System.out.println("   S: " + subjective);
                        // // System.out.println("   O: " + objective);
                        // // System.out.println("   A: " + assessment);

                        // Create bridging.SOAPResult object
                        return new bridging.SOAPResult(subjective, objective, assessment, plan, intervention, evaluation);
                    } else {
                        // // System.out.println("❌ JsonParser returned null or non-object");
                        return parseJSONManually(jsonStr);
                    }

                } catch (Exception e) {
                    // // System.out.println("❌ JsonParser error: " + e.getMessage());
                    e.printStackTrace();

                    // Fallback to manual parsing
                    // // System.out.println("🔧 Trying manual parsing...");
                    return parseJSONManually(jsonStr);
                }
            }

            return createDefaultSOAPResult();

        } catch (Exception e) {
            // // System.out.println("❌ Error in extractSOAPFromText: " + e.getMessage());
            e.printStackTrace();
            return createDefaultSOAPResult();
        }
    }

    /**
     * Create default SOAP result - UPDATED to use bridging.SOAPResult
     */
    private bridging.SOAPResult createDefaultSOAPResult() {
        return new bridging.SOAPResult(
                "Tidak dapat mengextract keluhan dari transcription",
                "Tidak dapat mengextract pemeriksaan dari transcription",
                "Tidak dapat mengextract assessment dari transcription",
                "Tidak dapat mengextract plan dari transcription",
                "Tidak dapat mengextract intervention dari transcription",
                "Tidak dapat mengextract evaluation dari transcription"
        );
    }

    /**
     * Parse JSON manually - UPDATED to use bridging.SOAPResult
     */
    private bridging.SOAPResult parseJSONManually(String jsonStr) {
        // // System.out.println("🔧 Attempting manual JSON parsing...");

        try {
            String subjective = extractValueManually(jsonStr, "subjective");
            String objective = extractValueManually(jsonStr, "objective");
            String assessment = extractValueManually(jsonStr, "assessment");
            String plan = extractValueManually(jsonStr, "plan");
            String intervention = extractValueManually(jsonStr, "intervention");
            String evaluation = extractValueManually(jsonStr, "evaluation");

            if (!subjective.equals("Tidak disebutkan") || !objective.equals("Tidak disebutkan")) {
                // Manual parsing successful:
                // // System.out.println("   S: " + subjective.substring(0, Math.min(50, subjective.length())) + "...");
                // // System.out.println("   O: " + objective.substring(0, Math.min(50, objective.length())) + "...");

                return new bridging.SOAPResult(subjective, objective, assessment, plan, intervention, evaluation);
            } else {
                // // System.out.println("⚠️ Manual parsing produced empty results");
                return createDefaultSOAPResult();
            }

        } catch (Exception e) {
            // // System.out.println("❌ Manual parsing failed: " + e.getMessage());
            return createDefaultSOAPResult();
        }
    }

    // Helper methods remain the same...
    private String getJsonValueSafe(JsonObject json, String key, String defaultValue) {
        try {
            if (json.has(key) && json.get(key) != null && !json.get(key).isJsonNull()) {
                String value = json.get(key).getAsString().trim();
                // Found key
                return value;
            }

            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                if (entry.getKey().equals(key)) {
                    JsonElement element = entry.getValue();
                    if (element != null && !element.isJsonNull()) {
                        String value = element.getAsString().trim();
                        // Found key via manual search
                        return value;
                    }
                }
            }

            // // System.out.println("⚠️ Key '" + key + "' not found in JSON");
            return defaultValue;

        } catch (Exception e) {
            // // System.out.println("❌ Error extracting '" + key + "': " + e.getMessage());
            return defaultValue;
        }
    }

    private String createSOAPPrompt(String transcription) {
        // Ambil vital signs dari form melalui callback
        String vitalSigns = "";
        if (callback != null) {
            vitalSigns = callback.getVitalSignsFromForm();
        }

        String prompt = "Kamu adalah asisten medis yang ahli dalam dokumentasi SOAPI-E. "
                + "Tolong parse catatan medis berikut ini ke dalam format SOAPI-E dalam bahasa Indonesia.\n\n"
                + "Catatan medis: \"" + transcription + "\"\n\n";

        // Tambahkan vital signs jika ada
        if (!vitalSigns.trim().isEmpty()) {
            prompt += "Vital signs dari form: " + vitalSigns + "\n\n";
        }

        prompt += "INSTRUKSI PENTING:\n"
                + "1. Bagi informasi ke dalam 6 kategori SOAPI-E\n"
                + "2. Gunakan bahasa Indonesia yang baik dan benar\n"
                + "3. Untuk bagian Objective: SERTAKAN semua vital signs yang disebutkan dalam transcript\n"
                + "4. Format vital signs dengan jelas: TD 120/80 mmHg, Nadi 85 x/menit, Suhu 36.8°C, dst\n"
                + "5. Pisahkan vital signs dan pemeriksaan fisik dengan koma atau titik koma\n"
                + "6. Jika tidak ada informasi untuk kategori tertentu, tulis \"Tidak disebutkan\"\n"
                + "7. Berikan response dalam format JSON berikut:\n\n"
                + "{\n"
                + "  \"subjective\": \"keluhan dan riwayat pasien yang disampaikan\",\n"
                + "  \"objective\": \"VITAL SIGNS dan hasil pemeriksaan fisik. Contoh: TD 120/80 mmHg, Nadi 85 x/menit, Suhu 36.8°C, RR 20 x/menit, SpO2 98%, TB 165 cm, BB 70 kg, GCS 15. Pemeriksaan fisik: mata konjungtiva tidak anemis, thoraks simetris, dll\",\n"
                + "  \"assessment\": \"diagnosis dan penilaian klinis\",\n"
                + "  \"plan\": \"rencana pengobatan dan tindakan medis\",\n"
                + "  \"intervention\": \"intervensi dan prosedur yang dilakukan\",\n"
                + "  \"evaluation\": \"evaluasi hasil dan monitoring\"\n"
                + "}\n\n"
                + "PENTING untuk Objective:\n"
                + "- SELALU masukkan semua vital signs yang disebutkan dalam transcript\n"
                + "- Format: TD [systolic]/[diastolic] mmHg, Nadi [nilai] x/menit, Suhu [nilai]°C\n"
                + "- Contoh yang BENAR: \"TD 150/90 mmHg, Nadi 95 x/menit, Suhu 38.7°C, RR 24 x/menit, SpO2 96%, TB 165 cm, BB 70 kg, GCS 15. Pemeriksaan fisik: mata merah, thoraks ronki basah halus\"\n"
                + "- Jangan lupa satuan yang tepat (mmHg, °C, %, cm, kg)\n\n"
                + "Hanya berikan response JSON, tidak perlu penjelasan tambahan.";

        return prompt;
    }

    /**
     * Test dengan sample transcript yang mengandung vital signs
     */
    public void testVitalSignsPrompt() {
        String sampleTranscript = "Pasien datang dengan keluhan demam batuk pilek, pemeriksaan keadaan umum tampak sakit sedang, tekanan darah 150 per 90 mmHg, nadi 95 kali per menit, suhu 38 koma 7 derajat celsius, respirasi 24 kali per menit, saturasi oksigen 96 persen, tinggi badan 165 cm, berat badan 70 kg, GCS 15, mata merah, assessment ISPA, plan rawat jalan antibiotik";

        String prompt = createSOAPPrompt(sampleTranscript);

        // TESTING VITAL SIGNS PROMPT
        // Sample transcript logged
        // Generated prompt length logged  
        // Prompt preview logged
        // END TEST
    }

    // Add other helper methods as needed...
    private int findMatchingCloseBrace(String text, int startIndex) {
        if (startIndex == -1 || startIndex >= text.length() || text.charAt(startIndex) != '{') {
            return -1;
        }

        int braceCount = 0;
        boolean inString = false;
        boolean escaped = false;

        for (int i = startIndex; i < text.length(); i++) {
            char c = text.charAt(i);

            if (escaped) {
                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            if (c == '"' && !escaped) {
                inString = !inString;
                continue;
            }

            if (!inString) {
                if (c == '{') {
                    braceCount++;
                } else if (c == '}') {
                    braceCount--;
                    if (braceCount == 0) {
                        return i;
                    }
                }
            }
        }

        return -1;
    }

    private String cleanJsonString(String jsonStr) {
        try {
            String cleaned = jsonStr.trim();

            if (cleaned.contains("\\\"")) {
                cleaned = cleaned.replace("\\\"", "\"");
            }

            if (cleaned.contains("\\n")) {
                cleaned = cleaned.replace("\\n", " ");
            }

            if (cleaned.contains("\\t")) {
                cleaned = cleaned.replace("\\t", " ");
            }

            if (cleaned.contains("  ")) {
                cleaned = cleaned.replaceAll("\\s+", " ");
            }

            if (!cleaned.startsWith("{")) {
                cleaned = "{" + cleaned;
            }
            if (!cleaned.endsWith("}")) {
                cleaned = cleaned + "}";
            }

            return cleaned;

        } catch (Exception e) {
            // // System.out.println("❌ Error cleaning JSON: " + e.getMessage());
            return jsonStr;
        }
    }

    private String extractValueManually(String jsonStr, String key) {
        try {
            String pattern = "\"" + key + "\"\\s*:\\s*\"";
            int keyIndex = jsonStr.indexOf(pattern);

            if (keyIndex == -1) {
                return "Tidak disebutkan";
            }

            int valueStart = jsonStr.indexOf("\"", keyIndex + pattern.length() - 1);
            int valueEnd = findValueEnd(jsonStr, valueStart);

            if (valueStart == -1 || valueEnd == -1) {
                return "Tidak disebutkan";
            }

            String value = jsonStr.substring(valueStart + 1, valueEnd);
            value = value.replace("\\\"", "\"");
            value = value.replace("\\n", " ");
            value = value.replace("\\t", " ");
            value = value.trim();

            if (value.isEmpty()) {
                return "Tidak disebutkan";
            }

            return value;

        } catch (Exception e) {
            // // System.out.println("⚠️ Error extracting " + key + ": " + e.getMessage());
            return "Tidak disebutkan";
        }
    }

    private int findValueEnd(String jsonStr, int startIndex) {
        if (startIndex == -1 || startIndex >= jsonStr.length()) {
            return -1;
        }

        boolean escaped = false;
        int braceCount = 0;

        for (int i = startIndex + 1; i < jsonStr.length(); i++) {
            char c = jsonStr.charAt(i);

            if (escaped) {
                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            if (c == '{' || c == '[') {
                braceCount++;
            } else if (c == '}' || c == ']') {
                braceCount--;
            } else if (c == '"' && braceCount == 0) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Close resources
     */
    public void close() {
        try {
            if (httpClient != null) {
                httpClient.close();
                // // System.out.println("✅ HttpClient closed");
            }
        } catch (Exception e) {
            // // System.out.println("⚠️ Error closing HttpClient: " + e.getMessage());
        }
    }

    /**
     * PERBAIKAN: HttpClient compatibility fix untuk GeminiService.java Ganti
     * method sendToGeminiAPI() dengan versi yang kompatibel
     */
    private String sendToGeminiAPI(String prompt) throws Exception {
        Exception lastException = null;

        // Add overall timeout untuk seluruh proses
        long startTime = System.currentTimeMillis();
        final long OVERALL_TIMEOUT = 60000; // 60 seconds max

        for (int i = 0; i < GEMINI_MODELS.length; i++) {
            // Check overall timeout
            if (System.currentTimeMillis() - startTime > OVERALL_TIMEOUT) {
                throw new RuntimeException("Overall timeout exceeded: " + OVERALL_TIMEOUT / 1000 + " seconds");
            }

            // CREATE SIMPLE HTTPCLIENT dengan timeout config
            try ( CloseableHttpClient tempHttpClient = HttpClients.createDefault()) {
                try {
                    String modelName = GEMINI_MODELS[currentModelIndex];
                    String apiUrl = GEMINI_API_BASE_URL + "/" + modelName + ":generateContent?key=" + GEMINI_API_KEY;

                    // // System.out.println("🤖 Trying Gemini model: " + modelName);
                    // // System.out.println("⏱️ Timeout: 30 seconds");

                    // Build request body
                    JsonObject requestBody = new JsonObject();

                    JsonArray contents = new JsonArray();
                    JsonObject content = new JsonObject();
                    JsonArray parts = new JsonArray();
                    JsonObject part = new JsonObject();

                    part.addProperty("text", prompt);
                    parts.add(part);
                    content.add("parts", parts);
                    contents.add(content);
                    requestBody.add("contents", contents);

                    // Add generation config
                    JsonObject generationConfig = new JsonObject();
                    generationConfig.addProperty("temperature", 0.1); // Lower temperature for consistent output
                    generationConfig.addProperty("maxOutputTokens", 1000);
                    generationConfig.addProperty("topP", 0.8);
                    requestBody.add("generationConfig", generationConfig);

                    // Create HTTP POST request dengan timeout yang compatible
                    HttpPost httpPost = new HttpPost(apiUrl);
                    httpPost.setHeader("Content-Type", "application/json");

                    // PERBAIKAN: Set timeout menggunakan RequestConfig yang compatible
                    org.apache.hc.client5.http.config.RequestConfig requestConfig
                            = org.apache.hc.client5.http.config.RequestConfig.custom()
                                    .setConnectTimeout(15000, java.util.concurrent.TimeUnit.MILLISECONDS)
                                    .setResponseTimeout(30000, java.util.concurrent.TimeUnit.MILLISECONDS)
                                    .build();
                    httpPost.setConfig(requestConfig);

                    // Set request body
                    String jsonString = requestBody.toString();
                    // // System.out.println("📤 Request length: " + jsonString.length() + " characters");

                    StringEntity entity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
                    httpPost.setEntity(entity);

                    // Execute request dengan timeout handling
                    // // System.out.println("📤 Sending request to Gemini...");
                    try ( CloseableHttpResponse response = tempHttpClient.execute(httpPost)) {
                        int statusCode = response.getCode();
                        HttpEntity responseEntity = response.getEntity();
                        String responseBody = EntityUtils.toString(responseEntity);

                        // // System.out.println("📨 Gemini response status: " + statusCode);
                        // // System.out.println("📨 Response length: " + responseBody.length() + " characters");

                        if (statusCode >= 200 && statusCode < 300) {
                            // // System.out.println("✅ Gemini response received successfully with model: " + modelName);
                            // Response preview
                            return responseBody;

                        } else {
                            lastException = new RuntimeException("Model " + modelName + " failed: " + statusCode + " - " + responseBody);
                            // // System.out.println("❌ " + lastException.getMessage());

                            // Specific error handling
                            if (statusCode == 401) {
                                throw new RuntimeException("Gemini API Key invalid or expired");
                            } else if (statusCode == 403) {
                                throw new RuntimeException("Gemini API access denied - check billing or API enabled");
                            } else if (statusCode == 429) {
                                // // System.out.println("⏳ Rate limit hit, waiting 5 seconds...");
                                try {
                                    Thread.sleep(5000);
                                } catch (InterruptedException ie) {
                                    Thread.currentThread().interrupt();
                                }
                            }

                            // Try next model
                            currentModelIndex = (currentModelIndex + 1) % GEMINI_MODELS.length;
                            continue;
                        }
                    }

                } catch (java.net.SocketTimeoutException e) {
                    lastException = new RuntimeException("Timeout connecting to Gemini model " + GEMINI_MODELS[currentModelIndex]);
                    // // System.out.println("⏰ " + lastException.getMessage());
                    currentModelIndex = (currentModelIndex + 1) % GEMINI_MODELS.length;
                    continue;

                } catch (java.net.ConnectException e) {
                    lastException = new RuntimeException("Cannot connect to Gemini API: " + e.getMessage());
                    // // System.out.println("🌐 " + lastException.getMessage());
                    currentModelIndex = (currentModelIndex + 1) % GEMINI_MODELS.length;
                    continue;

                } catch (Exception e) {
                    lastException = e;
                    // // System.out.println("❌ Model " + GEMINI_MODELS[currentModelIndex] + " error: " + e.getMessage());
                    e.printStackTrace();
                    currentModelIndex = (currentModelIndex + 1) % GEMINI_MODELS.length;
                }
            } catch (Exception httpClientEx) {
                // // System.out.println("❌ HttpClient creation failed: " + httpClientEx.getMessage());
                lastException = httpClientEx;
            }
        }

        // Jika semua model gagal
        throw new RuntimeException("All Gemini models failed after "
                + (System.currentTimeMillis() - startTime) / 1000 + " seconds. Last error: "
                + (lastException != null ? lastException.getMessage() : "Unknown error"));
    }

    /**
     * PERBAIKAN: Test Gemini connection dengan HttpClient yang compatible
     */
    public boolean testGeminiConnection() {
        try {
            // // System.out.println("🧪 Testing Gemini API connection...");

            if (!isConfigurationValid()) {
                // // System.out.println("❌ Gemini configuration invalid");
                return false;
            }

            // Simple test prompt
            String testPrompt = "Test connection. Please respond with 'OK'.";

            // Simple HttpClient untuk test
            try ( CloseableHttpClient testClient = HttpClients.createDefault()) {

                String modelName = GEMINI_MODELS[0]; // Test dengan model pertama
                String apiUrl = GEMINI_API_BASE_URL + "/" + modelName + ":generateContent?key=" + GEMINI_API_KEY;

                JsonObject requestBody = new JsonObject();
                JsonArray contents = new JsonArray();
                JsonObject content = new JsonObject();
                JsonArray parts = new JsonArray();
                JsonObject part = new JsonObject();

                part.addProperty("text", testPrompt);
                parts.add(part);
                content.add("parts", parts);
                contents.add(content);
                requestBody.add("contents", contents);

                JsonObject generationConfig = new JsonObject();
                generationConfig.addProperty("temperature", 0.1);
                generationConfig.addProperty("maxOutputTokens", 50);
                requestBody.add("generationConfig", generationConfig);

                HttpPost httpPost = new HttpPost(apiUrl);
                httpPost.setHeader("Content-Type", "application/json");

                // PERBAIKAN: Set timeout yang compatible
                org.apache.hc.client5.http.config.RequestConfig requestConfig
                        = org.apache.hc.client5.http.config.RequestConfig.custom()
                                .setConnectTimeout(10000, java.util.concurrent.TimeUnit.MILLISECONDS)
                                .setResponseTimeout(15000, java.util.concurrent.TimeUnit.MILLISECONDS)
                                .build();
                httpPost.setConfig(requestConfig);

                String jsonString = requestBody.toString();
                StringEntity entity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
                httpPost.setEntity(entity);

                // // System.out.println("📤 Sending test request to Gemini...");
                try ( CloseableHttpResponse response = testClient.execute(httpPost)) {
                    int statusCode = response.getCode();
                    // // System.out.println("📨 Test response status: " + statusCode);

                    if (statusCode >= 200 && statusCode < 300) {
                        String responseBody = EntityUtils.toString(response.getEntity());
                        // // System.out.println("✅ Gemini API connection test SUCCESS");
                        // Test response logged
                        return true;
                    } else {
                        String errorBody = EntityUtils.toString(response.getEntity());
                        // // System.out.println("❌ Gemini API test failed with status: " + statusCode);
                        // // System.out.println("❌ Error response: " + errorBody);
                        return false;
                    }
                }
            }

        } catch (java.net.SocketTimeoutException e) {
            // // System.out.println("❌ Gemini connection test timeout: " + e.getMessage());
            return false;
        } catch (java.net.ConnectException e) {
            // // System.out.println("❌ Cannot connect to Gemini API: " + e.getMessage());
            return false;
        } catch (Exception e) {
            // // System.out.println("❌ Gemini connection test failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * VERSI SEDERHANA: processTranscriptionWithGemini dengan better logging
     */
    public void processTranscriptionWithGemini(String transcription) {
        // // System.out.println("🤖 Starting Gemini processing...");
        // // System.out.println("📝 Transcription: " + transcription.substring(0, Math.min(100, transcription.length())) + "...");

        // Validasi konfigurasi sebelum proses
        if (!isConfigurationValid()) {
            String errorMsg = "Konfigurasi Gemini tidak valid. Periksa GEMINIAPIKEY di database.xml";
            // // System.out.println("❌ " + errorMsg);

            SwingUtilities.invokeLater(() -> {
                if (callback != null) {
                    callback.onSOAPParsingError(errorMsg);
                }
            });
            return;
        }

        // Test connection first untuk avoid infinite loading
        // // System.out.println("🔍 Testing Gemini connection before processing...");
        if (!testGeminiConnection()) {
            // // System.out.println("❌ Gemini connection test failed");
            SwingUtilities.invokeLater(() -> {
                if (callback != null) {
                    callback.onSOAPParsingError("Gemini API connection failed. Please check:\n\n"
                            + "• API key configuration\n"
                            + "• Internet connection\n"
                            + "• Gemini API billing status\n"
                            + "• API key permissions");
                }
            });
            return;
        }

        // // System.out.println("✅ Gemini connection OK, proceeding with SOAP processing...");

        CompletableFuture.runAsync(() -> {
            try {
                // // System.out.println("📤 Creating SOAP prompt...");

                // Create prompt untuk SOAP parsing
                String prompt = createSOAPPrompt(transcription);
                // // System.out.println("📤 Prompt length: " + prompt.length() + " characters");

                // // System.out.println("📤 Sending to Gemini API...");
                // Send to Gemini API dengan timeout handling
                String response = sendToGeminiAPI(prompt);
                // // System.out.println("📨 Received Gemini response, parsing...");

                // Parse response
                bridging.SOAPResult soapResult = parseGeminiResponse(response);
                // // System.out.println("✅ SOAP parsing completed successfully");

                // Update UI dengan hasil SOAP
                SwingUtilities.invokeLater(() -> {
                    if (callback != null) {
                        callback.onSOAPParsingComplete(soapResult);
                    }
                });

            } catch (Exception e) {
                // // System.out.println("❌ Error processing with Gemini: " + e.getMessage());
                e.printStackTrace();

                SwingUtilities.invokeLater(() -> {
                    if (callback != null) {
                        String errorMsg = "Gemini processing failed: " + e.getMessage();

                        // Provide helpful error messages
                        if (e.getMessage().contains("timeout") || e.getMessage().contains("Timeout")) {
                            errorMsg += "\n\n💡 Network is slow. Try again or check internet connection.";
                        } else if (e.getMessage().contains("401") || e.getMessage().contains("invalid")) {
                            errorMsg += "\n\n💡 Check your Gemini API key in database.xml";
                        } else if (e.getMessage().contains("403") || e.getMessage().contains("denied")) {
                            errorMsg += "\n\n💡 Check Gemini API billing and permissions in Google Cloud Console.";
                        } else if (e.getMessage().contains("429")) {
                            errorMsg += "\n\n💡 Rate limit exceeded. Wait a moment and try again.";
                        }

                        callback.onSOAPParsingError(errorMsg);
                    }
                });
            }
        });
    }

    /**
     * Create prompt untuk chat biasa
     */
    private String createChatPrompt(String message) {
        return "Kamu adalah asisten AI yang ramah dan membantu. "
                + "Jawab pertanyaan berikut dengan bahasa Indonesia yang baik dan sopan. "
                + "Berikan jawaban yang informatif namun tidak terlalu panjang.\n\n"
                + "Pertanyaan: " + message + "\n\n"
                + "Jawab dengan bahasa Indonesia:";
    }

    /**
     * Extract response text dari Gemini API response untuk chat
     */
    private String extractChatResponse(String responseBody) {
        try {
            JsonObject response = gson.fromJson(responseBody, JsonObject.class);

            if (response.has("candidates") && response.getAsJsonArray("candidates").size() > 0) {
                JsonObject candidate = response.getAsJsonArray("candidates").get(0).getAsJsonObject();

                if (candidate.has("content")) {
                    JsonObject content = candidate.getAsJsonObject("content");

                    if (content.has("parts") && content.getAsJsonArray("parts").size() > 0) {
                        JsonArray parts = content.getAsJsonArray("parts");
                        String text = parts.get(0).getAsJsonObject().get("text").getAsString();

                        // Clean up response
                        text = text.trim();
                        if (text.isEmpty()) {
                            return "Maaf, saya tidak dapat memberikan jawaban untuk pertanyaan tersebut.";
                        }

                        return text;
                    }
                }
            }

            // Fallback jika struktur response berbeda
            return "Maaf, saya tidak dapat memproses permintaan Anda saat ini.";

        } catch (Exception e) {
            // // System.out.println("❌ Error parsing chat response: " + e.getMessage());
            return "Maaf, terjadi kesalahan dalam memproses jawaban.";
        }
    }

    /**
     * Test method untuk chat functionality
     */
    public void testChatFunctionality() {
        try {
            // // System.out.println("🧪 Testing chat functionality...");

            String testMessage = "Halo, apa kabar?";
            String response = generateResponse(testMessage);

            // // System.out.println("✅ Test message: " + testMessage);
            // // System.out.println("✅ Test response: " + response);
            // // System.out.println("✅ Chat functionality test completed");

        } catch (Exception e) {
            // // System.out.println("❌ Chat functionality test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * REPLACE method generateResponse dan extractSimpleChatResponse di
     * GeminiService.java Versi yang sudah diperbaiki untuk kompatibilitas Gson
     */
    /**
     * Generate response untuk chat biasa
     *
     * @param message Pesan dari user
     * @return Response dari Gemini
     * @throws Exception jika terjadi error
     */
    public String generateResponse(String message) throws Exception {
        if (message == null || message.trim().isEmpty()) {
            return "Maaf, pesan tidak boleh kosong.";
        }

        // Validasi konfigurasi
        if (!isConfigurationValid()) {
            throw new Exception("Konfigurasi Gemini tidak valid. Periksa GEMINIAPIKEY di database.xml");
        }

        try {
            // // System.out.println("🤖 Generating simple chat response for: " + message.substring(0, Math.min(50, message.length())) + "...");

            // Create simple chat prompt
            String chatPrompt = "Kamu adalah asisten AI yang ramah dan membantu. "
                    + "Jawab pertanyaan berikut dengan bahasa Indonesia yang baik dan sopan. "
                    + "Berikan jawaban yang informatif namun tidak terlalu panjang.\n\n"
                    + "Pertanyaan: " + message + "\n\n"
                    + "Jawab dengan bahasa Indonesia:";

            // Send to Gemini API menggunakan method yang sudah ada
            String response = sendToGeminiAPI(chatPrompt);

            // // System.out.println("📨 Raw Gemini response: " + response.substring(0, Math.min(200, response.length())) + "...");

            // Extract response text
            String chatResponse = extractSimpleChatResponse(response);

            // // System.out.println("✅ Extracted chat response: " + chatResponse.substring(0, Math.min(100, chatResponse.length())) + "...");
            return chatResponse;

        } catch (Exception e) {
            // // System.out.println("❌ Error generating chat response: " + e.getMessage());
            e.printStackTrace();

            // Return user-friendly error messages
            if (e.getMessage().contains("timeout") || e.getMessage().contains("Timeout")) {
                return "Maaf, koneksi sedang lambat. Silakan coba lagi.";
            } else if (e.getMessage().contains("401") || e.getMessage().contains("invalid")) {
                return "Maaf, ada masalah dengan konfigurasi API. Hubungi administrator.";
            } else if (e.getMessage().contains("429")) {
                return "Maaf, terlalu banyak permintaan. Tunggu sebentar dan coba lagi.";
            } else {
                return "Maaf, terjadi kesalahan: " + e.getMessage();
            }
        }
    }

    /**
     * Extract response text dari Gemini API response untuk chat sederhana Versi
     * yang diperbaiki untuk kompatibilitas Gson
     */
    private String extractSimpleChatResponse(String responseBody) {
        try {
            // // System.out.println("🔍 Extracting simple chat response...");
            // // System.out.println("📊 Response body length: " + responseBody.length());

            // Parse JSON response
            JsonObject response = gson.fromJson(responseBody, JsonObject.class);

            // Navigate through the JSON structure step by step
            if (response.has("candidates")) {
                JsonArray candidates = response.getAsJsonArray("candidates");
                // // System.out.println("✅ Found candidates array with " + candidates.size() + " items");

                if (candidates.size() > 0) {
                    JsonObject candidate = candidates.get(0).getAsJsonObject();
                    // // System.out.println("✅ Got first candidate");

                    if (candidate.has("content")) {
                        JsonObject content = candidate.getAsJsonObject("content");
                        // // System.out.println("✅ Found content object");

                        if (content.has("parts")) {
                            JsonArray parts = content.getAsJsonArray("parts");
                            // // System.out.println("✅ Found parts array with " + parts.size() + " items");

                            if (parts.size() > 0) {
                                JsonObject firstPart = parts.get(0).getAsJsonObject();

                                if (firstPart.has("text")) {
                                    String text = firstPart.get("text").getAsString();
                                    // // System.out.println("✅ Successfully extracted text: " + text.substring(0, Math.min(100, text.length())) + "...");

                                    // Clean up response
                                    text = text.trim();
                                    if (!text.isEmpty()) {
                                        return text;
                                    }
                                } else {
                                    // // System.out.println("❌ No 'text' field in first part");
                                    // Print available fields using entrySet()
                                    // Available fields logged
                                }
                            } else {
                                // // System.out.println("❌ Parts array is empty");
                            }
                        } else {
                            // // System.out.println("❌ No 'parts' field in content");
                            // Print available fields
                            // Available fields in content logged
                        }
                    } else {
                        // // System.out.println("❌ No 'content' field in candidate");
                        // Print available fields
                        // Available fields in candidate logged
                    }
                } else {
                    // // System.out.println("❌ Candidates array is empty");
                }
            } else {
                // // System.out.println("❌ No 'candidates' field in response");
                // Print available fields
                // Available fields in response logged
            }

            // Fallback: cari text pattern secara manual
            // // System.out.println("🔧 Trying manual text extraction as fallback...");
            return extractTextManually(responseBody);

        } catch (com.google.gson.JsonSyntaxException e) {
            // // System.out.println("❌ JSON parsing error: " + e.getMessage());
            // // System.out.println("🔧 Trying manual extraction for malformed JSON...");
            return extractTextManually(responseBody);

        } catch (Exception e) {
            // // System.out.println("❌ Error extracting chat response: " + e.getMessage());
            e.printStackTrace();
            return "Maaf, terjadi kesalahan dalam memproses respons dari Gemini.";
        }
    }

    /**
     * Manual text extraction sebagai fallback
     */
    private String extractTextManually(String responseBody) {
        try {
            // // System.out.println("🔧 Manual text extraction started");

            // Pattern 1: Cari "text": "..."
            String searchPattern = "\"text\":";
            int textIndex = responseBody.indexOf(searchPattern);

            if (textIndex != -1) {
                // // System.out.println("✅ Found text pattern at index: " + textIndex);

                // Cari quote pembuka setelah "text":
                int quoteStart = responseBody.indexOf("\"", textIndex + searchPattern.length());
                if (quoteStart != -1) {
                    // Cari quote penutup
                    int quoteEnd = findClosingQuote(responseBody, quoteStart + 1);

                    if (quoteEnd != -1) {
                        String extractedText = responseBody.substring(quoteStart + 1, quoteEnd);

                        // Unescape JSON string
                        extractedText = extractedText.replace("\\\"", "\"");
                        extractedText = extractedText.replace("\\n", "\n");
                        extractedText = extractedText.replace("\\\\", "\\");
                        extractedText = extractedText.trim();

                        if (!extractedText.isEmpty()) {
                            // // System.out.println("✅ Manual extraction successful: " + extractedText.substring(0, Math.min(50, extractedText.length())) + "...");
                            return extractedText;
                        }
                    }
                }
            }

            // // System.out.println("❌ Manual extraction failed - no recognizable text pattern found");
            // // System.out.println("📄 Response sample: " + responseBody.substring(0, Math.min(300, responseBody.length())) + "...");
            return "Maaf, tidak dapat mengekstrak respons dari Gemini. Response: " + responseBody.substring(0, Math.min(100, responseBody.length())) + "...";

        } catch (Exception e) {
            // // System.out.println("❌ Manual extraction error: " + e.getMessage());
            return "Maaf, terjadi kesalahan dalam ekstraksi manual: " + e.getMessage();
        }
    }

    /**
     * Helper method untuk mencari quote penutup
     */
    private int findClosingQuote(String text, int startIndex) {
        boolean escaped = false;

        for (int i = startIndex; i < text.length(); i++) {
            char c = text.charAt(i);

            if (escaped) {
                escaped = false;
                continue;
            }

            if (c == '\\') {
                escaped = true;
                continue;
            }

            if (c == '"') {
                return i;
            }
        }

        return -1;
    }

}
