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
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import javax.sound.sampled.*;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Service untuk menangani recording audio dan transcription menggunakan Google
 * Speech-to-Text API Java 8 compatible version dengan HttpClient 5
 *
 * @author YourName
 */
public class GoogleSpeechToTextService {

    // Google Speech API Configuration
    private static String GOOGLE_API_KEY = null;
    private static final String GOOGLE_SPEECH_API_URL = "https://speech.googleapis.com/v1/speech:recognize";

    // Default parameters untuk Google Speech
    private static final String DEFAULT_LANGUAGE_CODE = "id-ID"; // Indonesian
    private static final String DEFAULT_MODEL = "latest_long"; // Model terbaru untuk audio panjang
    private static final int TIMEOUT_SECONDS = 60;
    private static final int SAMPLE_RATE = 16000; // Google merekomendasikan 16kHz

    // Audio recording variables
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private ByteArrayOutputStream audioBuffer;
    private Thread recordingThread;
    private boolean isRecording = false;

    // UI reference
    private JButton microphoneButton;
    private Object parentDialog;

    // HTTP Client 5
    private CloseableHttpClient httpClient;
    private Gson gson;

    // Callback interface untuk hasil transcription
    public interface TranscriptionCallback {

        void onTranscriptionComplete(String transcription);

        void onTranscriptionError(String error);
    }

    private TranscriptionCallback callback;

    /**
     * Constructor
     */
    public GoogleSpeechToTextService(JButton microphoneButton, Object parentDialog) {
        this.microphoneButton = microphoneButton;
        this.parentDialog = parentDialog;
        this.httpClient = HttpClients.createDefault();
        this.gson = new Gson();

        // Load configuration dari database.xml
        loadConfiguration();
        initializeAudio();
    }

    /**
     * Load konfigurasi Google Speech API dari database.xml (with AES
     * decryption)
     */
    private void loadConfiguration() {
        try {
            GOOGLE_API_KEY = koneksiDB.GOOGLESPEECHAPIKEY();

            if (GOOGLE_API_KEY == null || GOOGLE_API_KEY.trim().isEmpty()) {
                System.out.println("⚠️ WARNING: Google Speech API Key tidak ditemukan atau gagal didekripsi!");
                System.out.println("   Kemungkinan penyebab:");
                System.out.println("   • Entry GOOGLESPEECHAPIKEY tidak ada di database.xml");
                System.out.println("   • API Key tidak terenkripsi dengan benar");
                System.out.println("   • File database.xml tidak dapat diakses");
                System.out.println("   Silakan periksa konfigurasi enkripsi AES dan database.xml");
            } else {
                // Mask API key for security (show only first 10 chars)
                String maskedKey = GOOGLE_API_KEY.length() > 10
                        ? GOOGLE_API_KEY.substring(0, 10) + "***" : "***";
                // Google Speech API Key loaded and decrypted
                // Google Speech API URL configured
            }

        } catch (Exception e) {
            System.out.println("❌ Error loading Google Speech configuration: " + e.getMessage());
            System.out.println("   Ini mungkin disebabkan oleh:");
            System.out.println("   • Masalah dekripsi AES");
            System.out.println("   • File database.xml corrupt atau tidak dapat diakses");
            System.out.println("   • Konfigurasi enkripsi tidak sesuai");
            GOOGLE_API_KEY = "";
            e.printStackTrace();
        }
    }

    /**
     * Validasi apakah konfigurasi Google Speech sudah benar
     */
    public boolean isConfigurationValid() {
        return GOOGLE_API_KEY != null && !GOOGLE_API_KEY.trim().isEmpty();
    }

    /**
     * Set callback untuk hasil transcription
     */
    public void setTranscriptionCallback(TranscriptionCallback callback) {
        this.callback = callback;
    }

    /**
     * Initialize audio system untuk Google Speech (16kHz recommended)
     */
    private void initializeAudio() {
        try {
            // Audio format optimized untuk Google Speech: 16kHz, 16-bit, mono
            audioFormat = new AudioFormat(SAMPLE_RATE, 16, 1, true, false);

            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            audioBuffer = new ByteArrayOutputStream();

            // Audio system initialized for Google Speech (16kHz)

        } catch (Exception e) {
            System.out.println("❌ Audio initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Check Google Speech API connection using HttpClient 5
     */
    public boolean checkAPIConnection() {
        if (!isConfigurationValid()) {
            System.out.println("❌ Google Speech API configuration invalid");
            return false;
        }

        try {
            // Checking Google Speech API connection with HttpClient 5...

            // Test dengan request kosong untuk validasi API key
            JsonObject testRequest = new JsonObject();
            JsonObject config = new JsonObject();
            config.addProperty("encoding", "LINEAR16");
            config.addProperty("sampleRateHertz", SAMPLE_RATE);
            config.addProperty("languageCode", DEFAULT_LANGUAGE_CODE);
            testRequest.add("config", config);

            JsonObject audio = new JsonObject();
            audio.addProperty("content", ""); // Empty content untuk test
            testRequest.add("audio", audio);

            // Create HTTP POST request using HttpClient 5
            HttpPost httpPost = new HttpPost(GOOGLE_SPEECH_API_URL + "?key=" + GOOGLE_API_KEY);
            httpPost.setHeader("Content-Type", "application/json");

            // Set request body
            String jsonString = testRequest.toString();
            StringEntity entity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);

            // Execute request
            try ( CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getCode();

                // Google Speech API akan return error 400 untuk empty content, tapi itu berarti API key valid
                if (statusCode == 400) {
                    // Google Speech API is accessible (API key valid)
                    return true;
                } else if (statusCode == 401 || statusCode == 403) {
                    System.out.println("❌ Google Speech API authentication failed");
                    return false;
                } else {
                    System.out.println("⚠️ Google Speech API responded with code: " + statusCode);
                    return true; // Assume it's working if not auth error
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Google Speech API connection failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Start recording audio
     */
    public void startRecording() {
        // Check API first
        if (!checkAPIConnection()) {
            showErrorDialog("API Error",
                    "Cannot connect to Google Speech API\n\n"
                    + "Please check your API key configuration in database.xml");
            return;
        }

        try {
            System.out.println("🎤 Starting recording for Google Speech...");

            targetDataLine.open(audioFormat);
            targetDataLine.start();
            isRecording = true;

            // Update button UI
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    microphoneButton.setText("⏹️ Stop Recording");
                    microphoneButton.setBackground(java.awt.Color.RED);
                }
            });

            audioBuffer.reset();

            // Recording thread
            recordingThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] buffer = new byte[4096];
                    System.out.println("🔴 Recording started for Google Speech... speak now!");

                    while (isRecording) {
                        try {
                            int bytesRead = targetDataLine.read(buffer, 0, buffer.length);
                            if (bytesRead > 0) {
                                audioBuffer.write(buffer, 0, bytesRead);
                            }
                        } catch (Exception e) {
                            System.out.println("❌ Recording error: " + e.getMessage());
                            break;
                        }
                    }

                    // Process audio when recording stops
                    processRecordedAudio();
                }
            });

            recordingThread.start();

        } catch (Exception e) {
            System.out.println("❌ Failed to start recording: " + e.getMessage());
            e.printStackTrace();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    resetButton();
                    showErrorDialog("Recording Error", "Failed to start recording: " + e.getMessage());
                }
            });
        }
    }

    /**
     * Stop recording audio
     */
    public void stopRecording() {
        System.out.println("⏹️ Stopping recording...");
        isRecording = false;

        if (targetDataLine != null && targetDataLine.isOpen()) {
            targetDataLine.stop();
            targetDataLine.close();
        }
    }

    /**
     * Process recorded audio
     */
    private void processRecordedAudio() {
        try {
            byte[] audioData = audioBuffer.toByteArray();

            if (audioData.length == 0) {
                System.out.println("⚠️ No audio data recorded!");
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        resetButton();
                        showWarningDialog("No Audio", "No audio data was recorded. Please try again.");
                    }
                });
                return;
            }

            // Calculate duration
            double duration = (double) audioData.length / (audioFormat.getSampleRate() * audioFormat.getFrameSize());
            // Audio duration: " + String.format("%.2f", duration) + " seconds
            // Audio size: " + (audioData.length / 1024) + " KB

            if (duration < 0.5) {
                System.out.println("⚠️ Audio too short!");
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        resetButton();
                        showWarningDialog("Audio Too Short", "Recording too short. Please record at least 0.5 seconds.");
                    }
                });
                return;
            }

            // Send to Google Speech API
            sendToGoogleSpeechAPI(audioData);

        } catch (Exception e) {
            System.out.println("❌ Error processing audio: " + e.getMessage());
            e.printStackTrace();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    resetButton();
                    showErrorDialog("Processing Error", "Error processing audio: " + e.getMessage());
                }
            });
        }
    }

    /**
     * Send audio data to Google Speech API
     */
    private void sendToGoogleSpeechAPI(byte[] audioData) {
        // Update UI to show processing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                microphoneButton.setText("🔄 Processing...");
                microphoneButton.setEnabled(false);
            }
        });
        // PERBAIKAN: Update method showTranscriptionResult() di GoogleSpeechToTextService.java
        // atau pastikan callback dipanggil dengan benar

        /**
         * PERBAIKAN 1: Pastikan callback dipanggil setelah transcript berhasil
         * Update di method sendToGoogleSpeechAPI() bagian akhir
         */

        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("🤖 Sending to Google Speech API...");
                    long startTime = System.currentTimeMillis();
                    String transcription = callGoogleSpeechAPI(audioData);
                    long processingTime = System.currentTimeMillis() - startTime;

                    // Clean and validate transcription result
                    final String cleanedTranscription = cleanTranscriptionResult(transcription);

                    // Log statistics
                    logTranscriptionStats(cleanedTranscription, processingTime);

                    // Update UI with result
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            resetButton();

                            if (cleanedTranscription != null && !cleanedTranscription.trim().isEmpty()) {
                                // Google Speech transcription result: " + cleanedTranscription

                                // PENTING: Langsung panggil callback tanpa dialog
                                if (callback != null) {
                                    System.out.println("📞 Calling transcription callback...");
                                    callback.onTranscriptionComplete(cleanedTranscription);
                                } else {
                                    System.out.println("❌ Callback is null!");

                                    // Fallback: show dialog jika callback null
                                    showTranscriptionResult(cleanedTranscription);
                                }

                            } else {
                                System.out.println("⚠️ Empty transcription result from Google Speech");
                                showWarningDialog("No Speech Detected",
                                        "No speech was detected in the audio. Please try speaking more clearly.");

                                if (callback != null) {
                                    callback.onTranscriptionError("No speech detected");
                                }
                            }
                        }
                    });

                } catch (Exception e) {
                    System.out.println("❌ Google Speech API error: " + e.getMessage());
                    e.printStackTrace();

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            resetButton();
                            showErrorDialog("Google Speech API Error", "Error: " + e.getMessage());

                            if (callback != null) {
                                callback.onTranscriptionError("Google Speech API error: " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * PERBAIKAN: Call Google Speech API dengan improved error handling Ganti
     * method callGoogleSpeechAPI() di GoogleSpeechToTextService.java
     */
    private String callGoogleSpeechAPI(byte[] audioData) throws Exception {
        System.out.println("📡 Calling Google Speech-to-Text API with HttpClient 5...");

        // Encode audio data to base64
        String base64Audio = Base64.getEncoder().encodeToString(audioData);

        // Build request JSON
        JsonObject request = new JsonObject();

        // Audio configuration
        JsonObject audio = new JsonObject();
        audio.addProperty("content", base64Audio);
        request.add("audio", audio);

        // Recognition configuration
        JsonObject config = new JsonObject();
        config.addProperty("encoding", "LINEAR16");
        config.addProperty("sampleRateHertz", SAMPLE_RATE);
        config.addProperty("languageCode", DEFAULT_LANGUAGE_CODE);
        config.addProperty("model", DEFAULT_MODEL);

        // Enhanced configuration for medical transcription
        config.addProperty("useEnhanced", true);
        config.addProperty("enableAutomaticPunctuation", true);
        config.addProperty("enableSpokenPunctuation", false);
        config.addProperty("enableSpokenEmojis", false);

        // Alternative language codes for better recognition
        JsonArray alternativeLanguageCodes = new JsonArray();
        JsonPrimitive englishCode = new JsonPrimitive("en-US");
        alternativeLanguageCodes.add(englishCode); // Fallback to English for medical terms
        config.add("alternativeLanguageCodes", alternativeLanguageCodes);

        request.add("config", config);

        // Create HTTP POST request using HttpClient 5
        HttpPost httpPost = new HttpPost(GOOGLE_SPEECH_API_URL + "?key=" + GOOGLE_API_KEY);
        httpPost.setHeader("Content-Type", "application/json");

        // Set request body
        String jsonString = request.toString();
        StringEntity entity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);

        // Execute request dengan timeout
        try ( CloseableHttpResponse response = httpClient.execute(httpPost)) {
            int statusCode = response.getCode();
            HttpEntity responseEntity = response.getEntity();
            String responseBody = EntityUtils.toString(responseEntity);

            System.out.println("📨 Google Speech API response code: " + statusCode);
            System.out.println("📨 Google Speech API response: " + responseBody);

            if (statusCode >= 200 && statusCode < 300) {
                // DEBUG: Parse response step by step
                debugGoogleResponseParsing(responseBody);

                // Extract transcription from response
                String transcription = extractTranscriptionFromGoogleResponse(responseBody);

                if (transcription != null && !transcription.trim().isEmpty()) {
                    // Successfully got transcription: " + transcription
                } else {
                    System.out.println("⚠️ Transcription is empty after parsing");
                }

                return transcription;

            } else {
                throw new RuntimeException("Google Speech API returned error " + statusCode + ": " + responseBody);
            }
        }
    }

    /**
     * TAMBAHKAN method debug sederhana ini di GoogleSpeechToTextService.java
     * Jika ingin tetap mempertahankan debug call
     */
    private void debugGoogleResponseParsing(String responseBody) {
        System.out.println("🐛 === SIMPLE RESPONSE DEBUG ===");
        try {
            System.out.println("📄 Response length: " + responseBody.length());
            // Contains 'results': " + responseBody.contains("\"results\"")
            // Contains 'alternatives': " + responseBody.contains("\"alternatives\"")
            // Contains 'transcript': " + responseBody.contains("\"transcript\"")

            // Show first 200 chars of response for quick check
            String preview = responseBody.length() > 200
                    ? responseBody.substring(0, 200) + "..." : responseBody;
            System.out.println("📄 Response preview: " + preview);

        } catch (Exception e) {
            System.out.println("⚠️ Debug error: " + e.getMessage());
        }
        System.out.println("🐛 === END DEBUG ===");
    }

    private String extractTranscriptionFromGoogleResponse(String responseBody) {
        // Extracting transcript from Google response (multiple parts)...

        try {
            // Method 1: Regex untuk mengambil SEMUA transcript parts
            StringBuilder fullTranscript = new StringBuilder();
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                    "\"transcript\"\\s*:\\s*\"([^\"]*)\""
            );
            java.util.regex.Matcher matcher = pattern.matcher(responseBody);

            double bestConfidence = 0.0;
            int transcriptCount = 0;

            while (matcher.find()) {
                String transcript = matcher.group(1);
                if (transcript != null) {
                    // Clean up leading/trailing spaces and add to full transcript
                    String cleanedTranscript = transcript.trim();
                    if (!cleanedTranscript.isEmpty()) {
                        if (fullTranscript.length() > 0) {
                            // Add space if previous part doesn't end with space and current doesn't start with space
                            if (!fullTranscript.toString().endsWith(" ") && !cleanedTranscript.startsWith(" ")) {
                                fullTranscript.append(" ");
                            }
                        }
                        fullTranscript.append(cleanedTranscript);
                        transcriptCount++;
                    }
                }
            }

            // Extract highest confidence score
            java.util.regex.Pattern confPattern = java.util.regex.Pattern.compile(
                    "\"confidence\"\\s*:\\s*([0-9.]+)"
            );
            java.util.regex.Matcher confMatcher = confPattern.matcher(responseBody);

            while (confMatcher.find()) {
                try {
                    double confidence = Double.parseDouble(confMatcher.group(1));
                    if (confidence > bestConfidence) {
                        bestConfidence = confidence;
                    }
                } catch (Exception e) {
                    // Ignore confidence parsing errors
                }
            }

            String finalTranscript = fullTranscript.toString().trim();

            if (!finalTranscript.isEmpty()) {
                // Multi-part regex extraction SUCCESS
                // Transcript parts found: " + transcriptCount
                // Best confidence: " + String.format("%.2f", bestConfidence * 100) + "%"
                System.out.println("📝 Full transcript: " + finalTranscript);
                return finalTranscript;
            }

            // Method 2: Fallback - Gson parsing untuk multiple results
            try {
                System.out.println("🔄 Trying Gson method for multiple results...");

                JsonObject response = gson.fromJson(responseBody, JsonObject.class);
                if (response.has("results")) {
                    JsonArray results = response.getAsJsonArray("results");
                    StringBuilder gsonTranscript = new StringBuilder();
                    double gsonBestConfidence = 0.0;

                    // Found " + results.size() + " result groups"

                    for (int i = 0; i < results.size(); i++) {
                        JsonObject result = results.get(i).getAsJsonObject();
                        if (result.has("alternatives")) {
                            JsonArray alternatives = result.getAsJsonArray("alternatives");
                            if (alternatives.size() > 0) {
                                JsonObject topAlternative = alternatives.get(0).getAsJsonObject();
                                if (topAlternative.has("transcript")) {
                                    String partTranscript = topAlternative.get("transcript").getAsString();
                                    if (partTranscript != null) {
                                        String cleanedPart = partTranscript.trim();
                                        if (!cleanedPart.isEmpty()) {
                                            if (gsonTranscript.length() > 0
                                                    && !gsonTranscript.toString().endsWith(" ")
                                                    && !cleanedPart.startsWith(" ")) {
                                                gsonTranscript.append(" ");
                                            }
                                            gsonTranscript.append(cleanedPart);
                                        }
                                    }

                                    // Get confidence
                                    if (topAlternative.has("confidence")) {
                                        try {
                                            double confidence = topAlternative.get("confidence").getAsDouble();
                                            if (confidence > gsonBestConfidence) {
                                                gsonBestConfidence = confidence;
                                            }
                                        } catch (Exception e) {
                                            // Ignore confidence errors
                                        }
                                    }
                                }
                            }
                        }
                    }

                    String gsonResult = gsonTranscript.toString().trim();
                    if (!gsonResult.isEmpty()) {
                        // Gson multi-result extraction SUCCESS
                        // Best confidence: " + String.format("%.2f", gsonBestConfidence * 100) + "%"
                        System.out.println("📝 Full transcript: " + gsonResult);
                        return gsonResult;
                    }
                }
            } catch (Exception gsonEx) {
                System.out.println("⚠️ Gson method failed: " + gsonEx.getMessage());
            }

            System.out.println("❌ No transcript found with any method");
            return "";

        } catch (Exception e) {
            System.out.println("❌ Error in transcript extraction: " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    /**
     * TAMBAHAN: Method untuk debug multiple results yang lebih detail
     */
    private void debugMultipleResults(String responseBody) {
        System.out.println("🐛 === MULTIPLE RESULTS DEBUG ===");

        try {
            JsonObject response = gson.fromJson(responseBody, JsonObject.class);
            if (response.has("results")) {
                JsonArray results = response.getAsJsonArray("results");
                // Total result groups: " + results.size()

                for (int i = 0; i < results.size(); i++) {
                    JsonObject result = results.get(i).getAsJsonObject();
                    // Result Group " + (i + 1) + ":"

                    if (result.has("alternatives")) {
                        JsonArray alternatives = result.getAsJsonArray("alternatives");
                        System.out.println("   Alternatives: " + alternatives.size());

                        for (int j = 0; j < alternatives.size(); j++) {
                            JsonObject alt = alternatives.get(j).getAsJsonObject();
                            if (alt.has("transcript")) {
                                String transcript = alt.get("transcript").getAsString();
                                System.out.println("   📝 Transcript[" + j + "]: '" + transcript + "'");
                            }
                            if (alt.has("confidence")) {
                                double confidence = alt.get("confidence").getAsDouble();
                                // Confidence[" + j + "]: " + String.format("%.2f", confidence * 100) + "%"
                            }
                        }
                    }

                    if (result.has("resultEndTime")) {
                        String endTime = result.get("resultEndTime").getAsString();
                        System.out.println("   ⏱️ End Time: " + endTime);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Debug failed: " + e.getMessage());
        }

        System.out.println("🐛 === END MULTIPLE RESULTS DEBUG ===");
    }

    private String extractTranscriptSimple(String responseBody) {
        try {
            System.out.println("🔧 Using simple regex to extract transcript...");

            // Simple regex untuk ambil transcript pertama
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                    "\"transcript\"\\s*:\\s*\"([^\"]*)\""
            );
            java.util.regex.Matcher matcher = pattern.matcher(responseBody);

            if (matcher.find()) {
                String transcript = matcher.group(1);
                // Simple regex extraction successful: " + transcript

                // Extract confidence too if available
                java.util.regex.Pattern confPattern = java.util.regex.Pattern.compile(
                        "\"confidence\"\\s*:\\s*([0-9.]+)"
                );
                java.util.regex.Matcher confMatcher = confPattern.matcher(responseBody);
                if (confMatcher.find()) {
                    try {
                        double confidence = Double.parseDouble(confMatcher.group(1));
                        // Confidence: " + String.format("%.2f", confidence * 100) + "%"
                    } catch (NumberFormatException e) {
                        System.out.println("⚠️ Could not parse confidence");
                    }
                }

                return transcript;
            } else {
                System.out.println("❌ No transcript found with simple regex");
                return "";
            }

        } catch (Exception e) {
            System.out.println("❌ Simple extraction failed: " + e.getMessage());
            return "";
        }
    }

    /**
     * ULTRA SIMPLE VERSION: Jika semua method di atas masih error Ganti
     * extractTranscriptionFromGoogleResponse() dengan method ini
     */
    private String extractTranscriptionFromGoogleResponseUltraSimple(String responseBody) {
        // Using ultra simple transcript extraction...

        try {
            // Method 1: Direct regex (most reliable)
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                    "\"transcript\"\\s*:\\s*\"([^\"]*)\""
            );
            java.util.regex.Matcher matcher = pattern.matcher(responseBody);

            if (matcher.find()) {
                String transcript = matcher.group(1);
                if (transcript != null && !transcript.trim().isEmpty()) {
                    // Transcript found: " + transcript
                    return transcript;
                }
            }

            // Method 2: Try basic Gson without keySet debugging
            try {
                JsonObject response = gson.fromJson(responseBody, JsonObject.class);
                if (response.has("results")) {
                    JsonArray results = response.getAsJsonArray("results");
                    if (results.size() > 0) {
                        JsonObject firstResult = results.get(0).getAsJsonObject();
                        if (firstResult.has("alternatives")) {
                            JsonArray alternatives = firstResult.getAsJsonArray("alternatives");
                            if (alternatives.size() > 0) {
                                JsonObject topAlternative = alternatives.get(0).getAsJsonObject();
                                if (topAlternative.has("transcript")) {
                                    String transcript = topAlternative.get("transcript").getAsString();
                                    if (transcript != null && !transcript.trim().isEmpty()) {
                                        // Gson extraction successful: " + transcript
                                        return transcript;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception gsonEx) {
                System.out.println("⚠️ Gson method failed: " + gsonEx.getMessage());
            }

            System.out.println("❌ No transcript found");
            return "";

        } catch (Exception e) {
            System.out.println("❌ Ultra simple extraction failed: " + e.getMessage());
            return "";
        }
    }

    /**
     * Clean and validate transcription result
     */
    private String cleanTranscriptionResult(String rawTranscription) {
        if (rawTranscription == null || rawTranscription.trim().isEmpty()) {
            return rawTranscription;
        }

        String cleaned = rawTranscription.trim();

        // Log hasil asli untuk debugging
        // Raw Google Speech transcription: " + cleaned

        // Basic cleaning
        // Remove multiple spaces
        cleaned = cleaned.replaceAll("\\s+", " ");

        // Fix common punctuation issues
        cleaned = cleaned.replaceAll("\\s+([.,!?])", "$1");

        // Capitalize first letter
        if (cleaned.length() > 0) {
            cleaned = Character.toUpperCase(cleaned.charAt(0)) + cleaned.substring(1);
        }

        System.out.println("🧹 Cleaned Google Speech transcription: " + cleaned);

        return cleaned;
    }

    /**
     * Log transcription statistics
     */
    private void logTranscriptionStats(String transcription, long processingTimeMs) {
        if (transcription == null || transcription.trim().isEmpty()) {
            // Transcription stats: Empty result
            return;
        }

        String[] words = transcription.trim().split("\\s+");
        int wordCount = words.length;
        int charCount = transcription.length();

        // Transcription Statistics:
        System.out.println("   • Processing Time: " + processingTimeMs + " ms");
        System.out.println("   • Word Count: " + wordCount);
        System.out.println("   • Character Count: " + charCount);
        System.out.println("   • Average Word Length: "
                + (wordCount > 0 ? String.format("%.1f", (double) charCount / wordCount) : "0") + " chars");

        // Check for potential medical terms
        int medicalTerms = 0;
        String[] commonMedicalWords = {
            "pasien", "keluhan", "sakit", "nyeri", "demam", "panas", "dingin",
            "kepala", "perut", "dada", "batuk", "pilek", "mual", "muntah",
            "tekanan", "darah", "nadi", "suhu", "pemeriksaan", "diagnosis"
        };

        String lowerTranscription = transcription.toLowerCase();
        for (String medWord : commonMedicalWords) {
            if (lowerTranscription.contains(medWord)) {
                medicalTerms++;
            }
        }

        if (medicalTerms > 0) {
            System.out.println("   • Medical Terms Detected: " + medicalTerms);
        }
    }

    
    /**
     * Reset button to initial state
     */
    private void resetButton() {
        microphoneButton.setText("🎤 Record");
        microphoneButton.setBackground(null);
        microphoneButton.setEnabled(true);
    }

    /**
     * Show warning dialog
     */
    private void showWarningDialog(String title, String message) {
        JOptionPane.showMessageDialog((java.awt.Component) parentDialog, message, title,
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Show error dialog
     */
    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog((java.awt.Component) parentDialog, message, title,
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Check if currently recording
     */
    public boolean isRecording() {
        return isRecording;
    }

    /**
     * Get API URL
     */
    public String getAPIUrl() {
        return GOOGLE_SPEECH_API_URL;
    }

    /**
     * Test Google Speech API connection dengan dekripsi AES dan HttpClient 5
     */
    public static void testGoogleSpeechConnection() {
        System.out.println("🧪 Testing Google Speech-to-Text API connection with AES decryption and HttpClient 5...");

        try {
            String apiKey = koneksiDB.GOOGLESPEECHAPIKEY();

            if (apiKey == null || apiKey.trim().isEmpty()) {
                System.out.println("❌ GOOGLESPEECHAPIKEY tidak ditemukan, kosong, atau gagal didekripsi!");
                System.out.println("   Langkah troubleshooting:");
                System.out.println("   1. Pastikan entry GOOGLESPEECHAPIKEY ada di database.xml");
                System.out.println("   2. Pastikan API key sudah dienkripsi dengan EnkripsiAES.encrypt()");
                System.out.println("   3. Pastikan EnkripsiAES dapat mendekripsi dengan benar");
                System.out.println("   4. Periksa akses file ke setting/database.xml");
                return;
            }

            // Mask API key for security logging
            String maskedKey = apiKey.length() > 10
                    ? apiKey.substring(0, 10) + "***" + apiKey.substring(apiKey.length() - 4) : "***";
            // Google Speech API Key decrypted: " + maskedKey
            // Google Speech API URL: " + GOOGLE_SPEECH_API_URL

            // Test API key validity menggunakan HttpClient 5
            // Testing API key validity with HttpClient 5...

            try ( CloseableHttpClient testClient = HttpClients.createDefault()) {
                // Test dengan empty audio (will fail but validates API key)
                JsonObject testRequest = new JsonObject();
                JsonObject config = new JsonObject();
                config.addProperty("encoding", "LINEAR16");
                config.addProperty("sampleRateHertz", 16000);
                config.addProperty("languageCode", "id-ID");
                testRequest.add("config", config);

                JsonObject audio = new JsonObject();
                audio.addProperty("content", "");
                testRequest.add("audio", audio);

                HttpPost httpPost = new HttpPost(GOOGLE_SPEECH_API_URL + "?key=" + apiKey);
                httpPost.setHeader("Content-Type", "application/json");

                String jsonString = testRequest.toString();
                StringEntity entity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
                httpPost.setEntity(entity);

                try ( CloseableHttpResponse response = testClient.execute(httpPost)) {
                    int statusCode = response.getCode();

                    if (statusCode == 400) {
                        // API key is valid (received expected 400 for empty audio)
                        // HttpClient 5 communication successful
                    } else if (statusCode == 401 || statusCode == 403) {
                        System.out.println("❌ API key authentication failed (status: " + statusCode + ")");
                    } else {
                        System.out.println("⚠️ Unexpected response code: " + statusCode);
                        System.out.println("   API key may still be valid");
                    }
                }
            }

            // Configuration test completed successfully with HttpClient 5

        } catch (Exception e) {
            System.out.println("❌ Error testing Google Speech configuration: " + e.getMessage());
            System.out.println("   Possible causes:");
            System.out.println("   • EnkripsiAES decryption failed");
            System.out.println("   • Database configuration issues");
            System.out.println("   • File access permissions");
            System.out.println("   • Network connectivity issues");
            e.printStackTrace();
        }
    }

    /**
     * Close resources - HttpClient 5 version
     */
    public void close() {
        try {
            if (httpClient != null) {
                httpClient.close();
                // HttpClient 5 closed
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error closing HttpClient 5: " + e.getMessage());
        }
    }

    private void showTranscriptionResult(String transcriptionText) {
        // Create dialog untuk menampilkan hasil transcription
        javax.swing.JDialog transcriptionDialog;

        // Handle different parent types
        if (parentDialog instanceof java.awt.Frame) {
            transcriptionDialog = new javax.swing.JDialog((java.awt.Frame) parentDialog, "Google Speech Transcription Result", true);
        } else if (parentDialog instanceof java.awt.Dialog) {
            transcriptionDialog = new javax.swing.JDialog((java.awt.Dialog) parentDialog, "Google Speech Transcription Result", true);
        } else {
            transcriptionDialog = new javax.swing.JDialog();
            transcriptionDialog.setTitle("Google Speech Transcription Result");
            transcriptionDialog.setModal(true);
        }

        transcriptionDialog.setLayout(new java.awt.BorderLayout());
        transcriptionDialog.setSize(700, 400);
        transcriptionDialog.setLocationRelativeTo((java.awt.Component) parentDialog);
        transcriptionDialog.setDefaultCloseOperation(javax.swing.JDialog.DISPOSE_ON_CLOSE);

        // Main panel
        javax.swing.JPanel mainPanel = new javax.swing.JPanel(new java.awt.BorderLayout(10, 10));
        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        javax.swing.JLabel titleLabel = new javax.swing.JLabel("🎙️ Google Speech-to-Text Result");
        titleLabel.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mainPanel.add(titleLabel, java.awt.BorderLayout.NORTH);

        // Text area untuk transcription
        javax.swing.JTextArea transcriptionArea = new javax.swing.JTextArea(transcriptionText);
        transcriptionArea.setLineWrap(true);
        transcriptionArea.setWrapStyleWord(true);
        transcriptionArea.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.PLAIN, 14));
        transcriptionArea.setMargin(new java.awt.Insets(10, 10, 10, 10));
        transcriptionArea.setEditable(true);
        transcriptionArea.setCaretPosition(0);

        // Scroll pane
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(transcriptionArea);
        scrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        mainPanel.add(scrollPane, java.awt.BorderLayout.CENTER);

        // Button panel
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new java.awt.FlowLayout());

        // Process to SOAP button
        javax.swing.JButton processButton = new javax.swing.JButton("🤖 Process to SOAP Notes");
        processButton.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 12));
        processButton.setPreferredSize(new java.awt.Dimension(200, 35));
        processButton.setBackground(new java.awt.Color(40, 167, 69)); // Green
        processButton.setForeground(java.awt.Color.WHITE);
        processButton.setFocusPainted(false);
        processButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                String editedText = transcriptionArea.getText().trim();
                if (!editedText.isEmpty()) {
                    transcriptionDialog.dispose();

                    // PENTING: Panggil callback dengan edited text
                    System.out.println("📞 Calling callback from dialog with edited text...");
                    if (callback != null) {
                        callback.onTranscriptionComplete(editedText);
                    } else {
                        System.out.println("❌ Callback is null in dialog!");
                    }
                } else {
                    javax.swing.JOptionPane.showMessageDialog(transcriptionDialog,
                            "Transcription text cannot be empty!",
                            "Warning",
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        // Skip Dialog - Langsung process
        javax.swing.JButton skipButton = new javax.swing.JButton("⚡ Skip Dialog & Process");
        skipButton.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 12));
        skipButton.setPreferredSize(new java.awt.Dimension(200, 35));
        skipButton.setBackground(new java.awt.Color(255, 193, 7)); // Yellow
        skipButton.setForeground(java.awt.Color.BLACK);
        skipButton.setFocusPainted(false);
        skipButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                transcriptionDialog.dispose();

                // Langsung process tanpa editing
                System.out.println("⚡ Skipping dialog, direct processing...");
                if (callback != null) {
                    callback.onTranscriptionComplete(transcriptionText);
                } else {
                    System.out.println("❌ Callback is null in skip action!");
                }
            }
        });

        // Cancel button
        javax.swing.JButton cancelButton = new javax.swing.JButton("❌ Cancel");
        cancelButton.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.PLAIN, 12));
        cancelButton.setPreferredSize(new java.awt.Dimension(100, 35));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                transcriptionDialog.dispose();
            }
        });

        // Add buttons to panel
        buttonPanel.add(processButton);
        buttonPanel.add(skipButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, java.awt.BorderLayout.SOUTH);
        transcriptionDialog.add(mainPanel);

        // Auto-select skip button untuk convenience
        skipButton.requestFocusInWindow();

        // Show dialog
        transcriptionDialog.setVisible(true);
    }
}
