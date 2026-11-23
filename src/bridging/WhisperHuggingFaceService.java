package bridging;

import java.io.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.Base64;
import javax.sound.sampled.*;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.time.Duration;

/**
 * Service untuk menangani recording audio dan transcription menggunakan Hugging
 * Face API
 *
 * @author YourName
 */
public class WhisperHuggingFaceService {

    // Hugging Face API Configuration
    private static final String HF_API_URL = "https://api-inference.huggingface.co/models/openai/whisper-large-v3";
    private static final String HF_API_TOKEN = "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"; // Ganti dengan token Anda
    private static final String HF_WHOAMI_URL = "https://huggingface.co/api/whoami-v2";
    private static final int TIMEOUT_SECONDS = 60;
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 2000;

    // Audio recording variables
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private ByteArrayOutputStream audioBuffer;
    private Thread recordingThread;
    private boolean isRecording = false;

    // UI reference
    private JButton microphoneButton;
    private Object parentDialog;

    // HTTP Client
    private HttpClient httpClient;

    // Available models
    public enum WhisperModel {
        WHISPER_TINY("openai/whisper-tiny"),
        WHISPER_BASE("openai/whisper-base"),
        WHISPER_SMALL("openai/whisper-small"),
        WHISPER_MEDIUM("openai/whisper-medium"),
        WHISPER_LARGE_V1("openai/whisper-large"),
        WHISPER_LARGE_V2("openai/whisper-large-v2"),
        WHISPER_LARGE_V3("openai/whisper-large-v3");

        private final String modelId;

        WhisperModel(String modelId) {
            this.modelId = modelId;
        }

        public String getModelId() {
            return modelId;
        }

        public String getApiUrl() {
            return "https://api-inference.huggingface.co/models/" + modelId;
        }
    }

    private WhisperModel selectedModel;

    // Callback interface untuk hasil transcription
    public interface TranscriptionCallback {

        void onTranscriptionComplete(String transcription);

        void onTranscriptionError(String error);

        void onModelLoading(String message);
    }

    private TranscriptionCallback callback;

    /**
     * Constructor dengan model default
     */
    public WhisperHuggingFaceService(JButton microphoneButton, Object parentDialog) {
        this(microphoneButton, parentDialog, WhisperModel.WHISPER_LARGE_V3);
    }

    /**
     * Constructor dengan model custom
     */
    public WhisperHuggingFaceService(JButton microphoneButton, Object parentDialog, WhisperModel model) {
        this.microphoneButton = microphoneButton;
        this.parentDialog = parentDialog;
        this.selectedModel = model;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        initializeAudio();
    }

    /**
     * Set callback untuk hasil transcription
     */
    public void setTranscriptionCallback(TranscriptionCallback callback) {
        this.callback = callback;
    }

    /**
     * Set model yang akan digunakan
     */
    public void setModel(WhisperModel model) {
        this.selectedModel = model;
        System.out.println("🔄 Model changed to: " + model.getModelId());
    }

    /**
     * Initialize audio system
     */
    private void initializeAudio() {
        try {
            // Audio format: 16kHz, 16-bit, mono (optimal untuk Whisper)
            audioFormat = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            audioBuffer = new ByteArrayOutputStream();

            System.out.println("✅ Audio system initialized");

        } catch (Exception e) {
            System.out.println("❌ Audio initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Check Hugging Face API availability
     */
    /**
     * Check Hugging Face API availability
     */
    public boolean checkApiAvailability() {
        try {
            System.out.println("🔍 Checking Hugging Face API availability...");

            // Test dengan POST request kosong untuk trigger model loading
            String jsonPayload = "{\"inputs\": \"\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(selectedModel.getApiUrl()))
                    .header("Authorization", "Bearer " + HF_API_TOKEN)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(15))
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("📨 Model availability code: " + response.statusCode());
            System.out.println("📨 Model availability response: " + response.body());

            if (response.statusCode() == 200) {
                System.out.println("✅ Model is ready");
                return true;
            } else if (response.statusCode() == 503) {
                System.out.println("⏳ Model is loading, please wait...");
                return false; // Model loading
            } else if (response.statusCode() == 400) {
                System.out.println("✅ Model is available (empty input error expected)");
                return true; // Model available, just empty input
            } else {
                System.out.println("⚠️ Model check returned: " + response.statusCode());
                return false;
            }

        } catch (Exception e) {
            System.out.println("❌ Model availability check failed: " + e.getMessage());
            return false;
        }
    }

    // Tambahkan method untuk debug token
    public void debugApiToken() {
        String token = HF_API_TOKEN;
        System.out.println("🔍 Debug API Token:");
        System.out.println("   Token format: " + (token.startsWith("hf_") ? "✅ Valid format" : "❌ Invalid format"));
        System.out.println("   Token length: " + token.length());
        System.out.println("   Token preview: " + token.substring(0, Math.min(10, token.length())) + "...");

        // Test dengan endpoint sederhana
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://huggingface.co/api/whoami-v2"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("   Whoami test: " + response.statusCode());

            if (response.statusCode() == 200) {
                System.out.println("   ✅ Token is valid");
            } else {
                System.out.println("   ❌ Token might be invalid");
            }

        } catch (Exception e) {
            System.out.println("   ❌ Token test failed: " + e.getMessage());
        }
    }

    /**
     * Start recording audio
     */
    public void startRecording() {
        try {
            System.out.println("🎤 Starting recording...");

            targetDataLine.open(audioFormat);
            targetDataLine.start();
            isRecording = true;

            // Update button UI
            SwingUtilities.invokeLater(() -> {
                microphoneButton.setText("⏹️ Stop Recording");
                microphoneButton.setBackground(java.awt.Color.RED);
            });

            audioBuffer.reset();

            // Recording thread
            recordingThread = new Thread(() -> {
                byte[] buffer = new byte[4096];
                System.out.println("🔴 Recording started... speak now!");

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
            });

            recordingThread.start();

        } catch (Exception e) {
            System.out.println("❌ Failed to start recording: " + e.getMessage());
            e.printStackTrace();

            SwingUtilities.invokeLater(() -> {
                resetButton();
                showErrorDialog("Recording Error", "Failed to start recording: " + e.getMessage());
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
                SwingUtilities.invokeLater(() -> {
                    resetButton();
                    showWarningDialog("No Audio", "No audio data was recorded. Please try again.");
                });
                return;
            }

            // Calculate duration
            double duration = (double) audioData.length / (audioFormat.getSampleRate() * audioFormat.getFrameSize());
            System.out.println("📊 Audio duration: " + String.format("%.2f", duration) + " seconds");
            System.out.println("📊 Audio size: " + (audioData.length / 1024) + " KB");

            if (duration < 0.5) {
                System.out.println("⚠️ Audio too short!");
                SwingUtilities.invokeLater(() -> {
                    resetButton();
                    showWarningDialog("Audio Too Short", "Recording too short. Please record at least 0.5 seconds.");
                });
                return;
            }

            // Send to Hugging Face API
            sendToHuggingFaceAPI(audioData);

        } catch (Exception e) {
            System.out.println("❌ Error processing audio: " + e.getMessage());
            e.printStackTrace();

            SwingUtilities.invokeLater(() -> {
                resetButton();
                showErrorDialog("Processing Error", "Error processing audio: " + e.getMessage());
            });
        }
    }

    /**
     * Send audio to Hugging Face API
     */
    private void sendToHuggingFaceAPI(byte[] audioData) {
        // Update UI to show processing
        SwingUtilities.invokeLater(() -> {
            microphoneButton.setText("🔄 Processing...");
            microphoneButton.setEnabled(false);
        });

        // Process in background
        CompletableFuture.runAsync(() -> {
            try {
                System.out.println("🤖 Sending to Hugging Face API...");
                System.out.println("🎯 Using model: " + selectedModel.getModelId());

                String transcription = callHuggingFaceAPI(audioData);

                // Update UI with result
                SwingUtilities.invokeLater(() -> {
                    resetButton();

                    if (transcription != null && !transcription.trim().isEmpty()) {
                        System.out.println("✅ Transcription result: " + transcription);

                        // Show result dialog
                        showInfoDialog("Transcription Result",
                                "Speech to text result:\n\n" + transcription);

                        // Call callback
                        if (callback != null) {
                            callback.onTranscriptionComplete(transcription.trim());
                        }

                    } else {
                        System.out.println("⚠️ Empty transcription result");
                        showWarningDialog("No Speech Detected",
                                "No speech was detected in the audio. Please try speaking more clearly.");

                        if (callback != null) {
                            callback.onTranscriptionError("No speech detected");
                        }
                    }
                });

            } catch (Exception e) {
                System.out.println("❌ API processing error: " + e.getMessage());
                e.printStackTrace();

                SwingUtilities.invokeLater(() -> {
                    resetButton();
                    showErrorDialog("API Error",
                            "Error communicating with Hugging Face API:\n\n" + e.getMessage());

                    if (callback != null) {
                        callback.onTranscriptionError("API error: " + e.getMessage());
                    }
                });
            }
        });
    }

    /**
     * Call Hugging Face API with retry logic
     */
    private String callHuggingFaceAPI(byte[] audioData) throws Exception {
        System.out.println("📡 Calling Hugging Face API...");

        Exception lastException = null;

        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                // Convert audio to base64
                String audioBase64 = Base64.getEncoder().encodeToString(audioData);

                // Create JSON payload
                String jsonPayload = String.format(
                        "{\"inputs\": \"%s\"}",
                        audioBase64
                );

                // Build HTTP request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(selectedModel.getApiUrl()))
                        .header("Authorization", "Bearer " + HF_API_TOKEN)
                        .header("Content-Type", "application/json")
                        .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                        .build();

                // Send request
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("📨 API response code: " + response.statusCode());
                System.out.println("📨 API response: " + response.body());

                if (response.statusCode() == 200) {
                    // Parse successful response
                    String transcription = parseSuccessResponse(response.body());
                    return transcription;

                } else if (response.statusCode() == 503) {
                    // Model is loading
                    String message = "Model is loading, please wait...";
                    System.out.println("⏳ " + message);

                    if (callback != null) {
                        SwingUtilities.invokeLater(() -> callback.onModelLoading(message));
                    }

                    // Wait and retry
                    Thread.sleep(RETRY_DELAY_MS * attempt);
                    continue;

                } else if (response.statusCode() == 429) {
                    // Rate limit exceeded
                    System.out.println("⚠️ Rate limit exceeded, waiting...");
                    Thread.sleep(RETRY_DELAY_MS * attempt);
                    continue;

                } else {
                    // Other error
                    String errorMsg = parseErrorResponse(response.body());
                    throw new RuntimeException("API error " + response.statusCode() + ": " + errorMsg);
                }

            } catch (Exception e) {
                lastException = e;
                System.out.println("❌ Attempt " + attempt + " failed: " + e.getMessage());

                if (attempt < MAX_RETRIES) {
                    System.out.println("🔄 Retrying in " + (RETRY_DELAY_MS * attempt / 1000) + " seconds...");
                    Thread.sleep(RETRY_DELAY_MS * attempt);
                }
            }
        }

        throw new RuntimeException("API call failed after " + MAX_RETRIES + " attempts", lastException);
    }

    /**
     * Parse successful API response
     */
    private String parseSuccessResponse(String responseBody) {
        try {
            // Hugging Face API bisa return format berbeda:
            // 1. {"text": "transcription result"}
            // 2. [{"text": "transcription result"}]
            // 3. "transcription result"

            responseBody = responseBody.trim();

            // Case 1: Object dengan field text
            if (responseBody.startsWith("{") && responseBody.contains("\"text\"")) {
                int textStart = responseBody.indexOf("\"text\":");
                if (textStart != -1) {
                    int quoteStart = responseBody.indexOf("\"", textStart + 7);
                    if (quoteStart != -1) {
                        int quoteEnd = responseBody.indexOf("\"", quoteStart + 1);
                        if (quoteEnd != -1) {
                            return responseBody.substring(quoteStart + 1, quoteEnd);
                        }
                    }
                }
            }

            // Case 2: Array dengan object
            if (responseBody.startsWith("[") && responseBody.contains("\"text\"")) {
                int textStart = responseBody.indexOf("\"text\":");
                if (textStart != -1) {
                    int quoteStart = responseBody.indexOf("\"", textStart + 7);
                    if (quoteStart != -1) {
                        int quoteEnd = responseBody.indexOf("\"", quoteStart + 1);
                        if (quoteEnd != -1) {
                            return responseBody.substring(quoteStart + 1, quoteEnd);
                        }
                    }
                }
            }

            // Case 3: Direct string
            if (responseBody.startsWith("\"") && responseBody.endsWith("\"")) {
                return responseBody.substring(1, responseBody.length() - 1);
            }

            // Fallback: return as-is
            return responseBody;

        } catch (Exception e) {
            System.out.println("⚠️ Error parsing response: " + e.getMessage());
            return responseBody; // Return raw response if parsing fails
        }
    }

    /**
     * Parse error response
     */
    private String parseErrorResponse(String responseBody) {
        try {
            // Try to extract error message from JSON
            if (responseBody.contains("\"error\"")) {
                int errorStart = responseBody.indexOf("\"error\":");
                if (errorStart != -1) {
                    int quoteStart = responseBody.indexOf("\"", errorStart + 8);
                    if (quoteStart != -1) {
                        int quoteEnd = responseBody.indexOf("\"", quoteStart + 1);
                        if (quoteEnd != -1) {
                            return responseBody.substring(quoteStart + 1, quoteEnd);
                        }
                    }
                }
            }

            return responseBody;

        } catch (Exception e) {
            return responseBody;
        }
    }

    /**
     * Test API connection with current token
     */
    /**
     * Test API connection with current token
     */
    public boolean testApiConnection() {
        try {
            System.out.println("🧪 Testing API connection...");

            // Test dengan endpoint yang lebih reliable
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://huggingface.co/api/whoami-v2"))
                    .header("Authorization", "Bearer " + HF_API_TOKEN)
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("📨 API test response code: " + response.statusCode());
            System.out.println("📨 API test response: " + response.body());

            if (response.statusCode() == 200) {
                System.out.println("✅ API connection successful");
                return true;
            } else if (response.statusCode() == 401) {
                System.out.println("❌ Invalid API token");
                return false;
            } else {
                System.out.println("⚠️ API test returned: " + response.statusCode());
                // Untuk testing, anggap berhasil jika bukan 401
                return response.statusCode() != 401;
            }

        } catch (Exception e) {
            System.out.println("❌ API test failed: " + e.getMessage());
            return false;
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
     * Show info dialog
     */
    private void showInfoDialog(String title, String message) {
        JOptionPane.showMessageDialog((java.awt.Component) parentDialog, message, title,
                JOptionPane.INFORMATION_MESSAGE);
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
     * Get current model
     */
    public WhisperModel getCurrentModel() {
        return selectedModel;
    }

    /**
     * Get API token (for debugging)
     */
    public String getApiToken() {
        return HF_API_TOKEN.substring(0, 8) + "..."; // Only show first 8 chars
    }

    /**
     * Static method to validate API token format
     */
    public static boolean isValidTokenFormat(String token) {
        return token != null && token.startsWith("hf_") && token.length() > 10;
    }
}
