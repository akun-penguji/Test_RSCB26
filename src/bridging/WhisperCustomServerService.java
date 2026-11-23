package bridging;

import fungsi.koneksiDB;
import java.io.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import javax.sound.sampled.*;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.time.Duration;

/**
 * Service untuk menangani recording audio dan transcription menggunakan Custom
 * Whisper Server
 *
 * @author YourName
 */
public class WhisperCustomServerService {

    // Server configuration - Ganti dengan URL server Anda
    private static final String WHISPER_SERVER_URL = koneksiDB.WHISPERSERVERURL();
    private static final String TRANSCRIBE_ENDPOINT = "/transcribe";
    private static final String HEALTH_ENDPOINT = "/health";

    // Default parameters
    private static final String DEFAULT_MODEL = "medium"; // Upgrade to medium for better accuracy
    private static final String DEFAULT_LANGUAGE = "id";
    private static final int TIMEOUT_SECONDS = 60;

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

    // Callback interface untuk hasil transcription
    public interface TranscriptionCallback {

        void onTranscriptionComplete(String transcription);

        void onTranscriptionError(String error);
    }

    private TranscriptionCallback callback;

    /**
     * Constructor
     */
    public WhisperCustomServerService(JButton microphoneButton, Object parentDialog) {
        this.microphoneButton = microphoneButton;
        this.parentDialog = parentDialog;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        initializeAudio();
    }

    /**
     * Constructor dengan custom server URL
     */
    public WhisperCustomServerService(JButton microphoneButton, Object parentDialog, String serverUrl) {
        this(microphoneButton, parentDialog);
        // Update server URL jika diperlukan
    }

    /**
     * Set callback untuk hasil transcription
     */
    public void setTranscriptionCallback(TranscriptionCallback callback) {
        this.callback = callback;
    }

    /**
     * Initialize audio system
     */
    private void initializeAudio() {
        try {
            // Audio format yang lebih baik: 22kHz, 16-bit, mono
            audioFormat = new AudioFormat(22050, 16, 1, true, false);

            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            audioBuffer = new ByteArrayOutputStream();

            System.out.println("✅ Audio system initialized with improved quality (22kHz)");

        } catch (Exception e) {
            System.out.println("❌ Audio initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Check server connection
     */
    public boolean checkServerConnection() {
        try {
            System.out.println("🔍 Checking server connection...");

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(WHISPER_SERVER_URL + HEALTH_ENDPOINT))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("✅ Server is available");
                return true;
            } else {
                System.out.println("⚠️ Server responded with code: " + response.statusCode());
                return false;
            }

        } catch (Exception e) {
            System.out.println("❌ Server connection failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Start recording audio
     */
    public void startRecording() {
        // Check server first
        if (!checkServerConnection()) {
            showErrorDialog("Server Error",
                    "Cannot connect to Whisper server at " + WHISPER_SERVER_URL
                    + "\n\nPlease make sure the server is running.");
            return;
        }

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

            // Create temporary WAV file
            File tempFile = createTempWavFile(audioData);

            // Send to server for transcription
            sendToWhisperServer(tempFile);

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
     * Create temporary WAV file from audio data
     */
    private File createTempWavFile(byte[] audioData) throws Exception {
        // Generate unique filename
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String filename = "whisper_audio_" + sdf.format(new Date()) + ".wav";
        File tempFile = new File(System.getProperty("java.io.tmpdir"), filename);

        // Convert audio data to AudioInputStream
        ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
        AudioInputStream audioInputStream = new AudioInputStream(
                bais, audioFormat, audioData.length / audioFormat.getFrameSize()
        );

        // Write to WAV file
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, tempFile);

        System.out.println("📁 Temp file created: " + tempFile.getAbsolutePath());
        return tempFile;
    }

    /**
     * Send audio file to Whisper server
     */
    /**
     * Send audio file to Whisper server - FIXED VERSION
     */
    private void sendToWhisperServer(File audioFile) {
        // Update UI to show processing
        SwingUtilities.invokeLater(() -> {
            microphoneButton.setText("🔄 Processing...");
            microphoneButton.setEnabled(false);
        });

        // Process in background
        CompletableFuture.runAsync(() -> {
            try {
                System.out.println("🤖 Sending to Whisper server...");
                String rawTranscription = callWhisperServer(audioFile);

                // Clean up temp file
                if (audioFile.exists()) {
                    audioFile.delete();
                    System.out.println("🗑️ Temp file deleted: " + audioFile.getName());
                }

                // Clean and validate transcription result
                final String cleanedTranscription = cleanTranscriptionResult(rawTranscription);

                // Update UI with result
                SwingUtilities.invokeLater(() -> {
                    resetButton();

                    if (cleanedTranscription != null && !cleanedTranscription.trim().isEmpty()) {
                        System.out.println("✅ Transcription result: " + cleanedTranscription);

                        // Analyze transcription quality
                        TranscriptionQuality quality = analyzeTranscriptionQuality(cleanedTranscription);

                        // Log quality analysis
                        System.out.println("📊 Transcription quality: " + quality.getQualityLevel());
                        System.out.println("📊 Word count: " + quality.getWordCount());
                        System.out.println("📊 Character count: " + quality.getCharCount());
                        System.out.println("📊 Medical words ratio: " + String.format("%.2f", quality.getMedicalWordsRatio()));

                        // Show transcription result dialog with quality info
                        showTranscriptionResultWithQuality(cleanedTranscription, quality);

                    } else {
                        System.out.println("⚠️ Empty transcription result");
                        showWarningDialog("No Speech Detected",
                                "No speech was detected in the audio. Please try speaking more clearly.\n\n"
                                + "Tips for better transcription:\n"
                                + "• Speak clearly and not too fast\n"
                                + "• Use a good microphone\n"
                                + "• Record in a quiet environment\n"
                                + "• Try using medical terminology");

                        if (callback != null) {
                            callback.onTranscriptionError("No speech detected");
                        }
                    }
                });

            } catch (Exception e) {
                System.out.println("❌ Server processing error: " + e.getMessage());
                e.printStackTrace();

                // Clean up temp file on error
                if (audioFile.exists()) {
                    audioFile.delete();
                }

                SwingUtilities.invokeLater(() -> {
                    resetButton();

                    String errorMessage = "Error communicating with Whisper server:\n\n" + e.getMessage();

                    // Provide helpful error messages based on error type
                    if (e.getMessage().contains("Connection refused")) {
                        errorMessage += "\n\n💡 Possible solutions:\n"
                                + "• Make sure Whisper server is running\n"
                                + "• Check server URL: " + WHISPER_SERVER_URL + "\n"
                                + "• Verify network connectivity";
                    } else if (e.getMessage().contains("timeout")) {
                        errorMessage += "\n\n💡 Audio processing timed out:\n"
                                + "• Try recording shorter audio clips\n"
                                + "• Check server performance\n"
                                + "• Verify audio file is not corrupted";
                    } else if (e.getMessage().contains("400")) {
                        errorMessage += "\n\n💡 Bad request - check audio format:\n"
                                + "• Audio should be WAV format\n"
                                + "• Sample rate: 16-22kHz recommended\n"
                                + "• Mono channel preferred";
                    }

                    showErrorDialog("Server Error", errorMessage);

                    if (callback != null) {
                        callback.onTranscriptionError("Server error: " + e.getMessage());
                    }
                });
            }
        });
    }

    /**
     * Call Whisper server API
     */
    private String callWhisperServer(File audioFile) throws Exception {
        System.out.println("📡 Calling Whisper server API...");

        // Read audio file
        byte[] audioBytes = Files.readAllBytes(audioFile.toPath());

        // Create multipart form data
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        byte[] multipartBody = createMultipartBody(audioBytes, audioFile.getName(), boundary);

        // Build HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(WHISPER_SERVER_URL + TRANSCRIBE_ENDPOINT))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .timeout(Duration.ofSeconds(TIMEOUT_SECONDS))
                .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody))
                .build();

        // Send request
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("📨 Server response code: " + response.statusCode());

        if (response.statusCode() == 200) {
            String responseBody = response.body();
            System.out.println("📨 Server response: " + responseBody);

            // Extract transcription from JSON response
            String transcription = extractTranscriptionFromResponse(responseBody);
            return transcription;

        } else {
            String errorBody = response.body();
            throw new RuntimeException("Server returned error " + response.statusCode() + ": " + errorBody);
        }
    }

    /**
     * Create multipart form data dengan parameter yang ditingkatkan
     */
    private byte[] createMultipartBody(byte[] audioBytes, String filename, String boundary) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(baos, "UTF-8"));

        // Audio file part
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"audio\"; filename=\"").append(filename).append("\"\r\n");
        writer.append("Content-Type: audio/wav\r\n");
        writer.append("\r\n");
        writer.flush();

        baos.write(audioBytes);

        writer.append("\r\n");

        // Model parameter - gunakan model yang lebih baik untuk bahasa Indonesia
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"model\"\r\n");
        writer.append("\r\n");
        writer.append(DEFAULT_MODEL); // medium untuk akurasi lebih baik
        writer.append("\r\n");

        // Language parameter - explicit Indonesian
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"language\"\r\n");
        writer.append("\r\n");
        writer.append(DEFAULT_LANGUAGE); // Indonesian
        writer.append("\r\n");

        // Temperature untuk consistency
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"temperature\"\r\n");
        writer.append("\r\n");
        writer.append("0.0"); // 0 untuk deterministic output
        writer.append("\r\n");

        // Initial prompt untuk konteks medis
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"initial_prompt\"\r\n");
        writer.append("\r\n");
        writer.append("Pasien datang dengan keluhan medis. Transkripsi dalam bahasa Indonesia yang jelas:");
        writer.append("\r\n");

        // Word timestamps untuk debug
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"word_timestamps\"\r\n");
        writer.append("\r\n");
        writer.append("false");
        writer.append("\r\n");

        // End boundary
        writer.append("--").append(boundary).append("--\r\n");
        writer.flush();
        writer.close();

        return baos.toByteArray();
    }

    /**
     * Extract transcription text from server response
     */
    private String extractTranscriptionFromResponse(String responseBody) {
        try {
            // Simple JSON parsing untuk mengambil field "text"
            responseBody = responseBody.trim();

            // Cari field "text"
            int textStart = responseBody.indexOf("\"text\":");
            if (textStart != -1) {
                // Cari opening quote setelah ":"
                int quoteStart = responseBody.indexOf("\"", textStart + 7);
                if (quoteStart != -1) {
                    // Cari closing quote
                    int quoteEnd = responseBody.indexOf("\"", quoteStart + 1);
                    if (quoteEnd != -1) {
                        String text = responseBody.substring(quoteStart + 1, quoteEnd);
                        // Unescape JSON string
                        text = text.replace("\\\"", "\"")
                                .replace("\\n", "\n")
                                .replace("\\r", "\r")
                                .replace("\\t", "\t")
                                .replace("\\\\", "\\");
                        return text;
                    }
                }
            }

            // Jika format tidak sesuai, coba ambil seluruh response sebagai text
            if (responseBody.startsWith("\"") && responseBody.endsWith("\"")) {
                return responseBody.substring(1, responseBody.length() - 1);
            }

            // Fallback: return response as-is
            return responseBody;

        } catch (Exception e) {
            System.out.println("⚠️ Error parsing server response: " + e.getMessage());
            return responseBody; // Return raw response if parsing fails
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
        System.out.println("🔍 Raw transcription: " + cleaned);

        // Basic cleaning
        // Remove multiple spaces
        cleaned = cleaned.replaceAll("\\s+", " ");

        // Fix common punctuation issues
        cleaned = cleaned.replaceAll("\\s+([.,!?])", "$1");

        // Capitalize first letter
        if (cleaned.length() > 0) {
            cleaned = Character.toUpperCase(cleaned.charAt(0)) + cleaned.substring(1);
        }

        System.out.println("🧹 Cleaned transcription: " + cleaned);

        return cleaned;
    }

    /**
     * Analyze transcription quality
     */
    private TranscriptionQuality analyzeTranscriptionQuality(String transcription) {
        TranscriptionQuality quality = new TranscriptionQuality();

        if (transcription == null || transcription.trim().isEmpty()) {
            quality.setQualityLevel("EMPTY");
            return quality;
        }

        String[] words = transcription.toLowerCase().split("\\s+");
        quality.setWordCount(words.length);
        quality.setCharCount(transcription.length());

        // Check for medical words
        int medicalWords = 0;
        String[] commonMedicalWords = {
            "pasien", "keluhan", "sakit", "nyeri", "demam", "panas", "dingin",
            "kepala", "perut", "dada", "batuk", "pilek", "mual", "muntah",
            "tekanan", "darah", "nadi", "suhu", "pemeriksaan", "diagnosis",
            "terapi", "obat", "tablet", "kapsul", "mg", "gram", "hari",
            "pagi", "siang", "malam", "sesudah", "sebelum", "makan",
            "dokter", "rumah", "sakit", "klinik", "puskesmas"
        };

        for (String word : words) {
            for (String medWord : commonMedicalWords) {
                if (word.contains(medWord) || medWord.contains(word)) {
                    medicalWords++;
                    break;
                }
            }
        }

        double medicalRatio = words.length > 0 ? (double) medicalWords / words.length : 0;
        quality.setMedicalWordsRatio(medicalRatio);

        // Determine quality level
        if (words.length < 3) {
            quality.setQualityLevel("VERY_LOW");
        } else if (words.length < 8 || medicalRatio < 0.1) {
            quality.setQualityLevel("LOW");
        } else if (words.length < 15 || medicalRatio < 0.2) {
            quality.setQualityLevel("MEDIUM");
        } else if (medicalRatio >= 0.3) {
            quality.setQualityLevel("HIGH");
        } else {
            quality.setQualityLevel("GOOD");
        }

        return quality;
    }

    /**
     * Show transcription result with quality information
     */
    private void showTranscriptionResultWithQuality(String transcription, TranscriptionQuality quality) {
        showTranscriptionResult(transcription, quality);
    }

    /**
     * Show transcription result dialog with quality info - COMPLETE VERSION
     */
    private void showTranscriptionResult(String transcriptionText, TranscriptionQuality quality) {
        // Reset button
        resetButton();

        // Create dialog untuk menampilkan hasil transcription
        javax.swing.JDialog transcriptionDialog;

        // Handle different parent types
        if (parentDialog instanceof java.awt.Frame) {
            transcriptionDialog = new javax.swing.JDialog((java.awt.Frame) parentDialog, "Transcription Result", true);
        } else if (parentDialog instanceof java.awt.Dialog) {
            transcriptionDialog = new javax.swing.JDialog((java.awt.Dialog) parentDialog, "Transcription Result", true);
        } else {
            transcriptionDialog = new javax.swing.JDialog();
            transcriptionDialog.setTitle("Transcription Result");
            transcriptionDialog.setModal(true);
            transcriptionDialog.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        }

        transcriptionDialog.setLayout(new java.awt.BorderLayout());
        transcriptionDialog.setSize(900, 700);
        transcriptionDialog.setLocationRelativeTo((java.awt.Component) parentDialog);
        transcriptionDialog.setDefaultCloseOperation(javax.swing.JDialog.DISPOSE_ON_CLOSE);

        // Main panel dengan padding
        javax.swing.JPanel mainPanel = new javax.swing.JPanel(new java.awt.BorderLayout(10, 10));
        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header panel dengan quality info
        javax.swing.JPanel headerPanel = new javax.swing.JPanel(new java.awt.BorderLayout());

        // Title
        javax.swing.JLabel titleLabel = new javax.swing.JLabel("🎙️ Voice Transcription Result");
        titleLabel.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        headerPanel.add(titleLabel, java.awt.BorderLayout.NORTH);

        // Quality info panel
        javax.swing.JPanel qualityPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));

        // Quality status
        javax.swing.JLabel qualityLabel = new javax.swing.JLabel(quality.getQualityMessage());
        qualityLabel.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 12));
        qualityLabel.setForeground(quality.getQualityColor());
        qualityPanel.add(qualityLabel);

        // Quality details
        javax.swing.JLabel detailsLabel = new javax.swing.JLabel(
                String.format("(%d words, %d chars, %.0f%% medical terms)",
                        quality.getWordCount(),
                        quality.getCharCount(),
                        quality.getMedicalWordsRatio() * 100));
        detailsLabel.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.PLAIN, 10));
        detailsLabel.setForeground(java.awt.Color.GRAY);
        qualityPanel.add(detailsLabel);

        headerPanel.add(qualityPanel, java.awt.BorderLayout.CENTER);

        // Info label
        javax.swing.JLabel infoLabel = new javax.swing.JLabel("Review and edit the transcription below before processing:");
        infoLabel.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.PLAIN, 11));
        infoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        infoLabel.setForeground(java.awt.Color.GRAY);
        headerPanel.add(infoLabel, java.awt.BorderLayout.SOUTH);

        mainPanel.add(headerPanel, java.awt.BorderLayout.NORTH);

        // Text area panel dengan border
        javax.swing.JPanel textPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
        String borderTitle = "Transcription Text";

        // Add quality indicator to border title
        if (quality.getQualityLevel().equals("HIGH")) {
            borderTitle += " ✅";
        } else if (quality.getQualityLevel().equals("LOW") || quality.getQualityLevel().equals("VERY_LOW")) {
            borderTitle += " ⚠️";
        }

        textPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createEtchedBorder(),
                borderTitle,
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 12)
        ));

        // Text area untuk transcription
        javax.swing.JTextArea transcriptionArea = new javax.swing.JTextArea(transcriptionText);
        transcriptionArea.setLineWrap(true);
        transcriptionArea.setWrapStyleWord(true);
        transcriptionArea.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.PLAIN, 14));
        transcriptionArea.setMargin(new java.awt.Insets(10, 10, 10, 10));
        transcriptionArea.setEditable(true);
        transcriptionArea.setCaretPosition(0);

        // Set background color based on quality
        java.awt.Color backgroundColor;
        if (quality.getQualityLevel().equals("HIGH") || quality.getQualityLevel().equals("GOOD")) {
            backgroundColor = new java.awt.Color(248, 255, 248); // Light green
        } else if (quality.getQualityLevel().equals("LOW") || quality.getQualityLevel().equals("VERY_LOW")) {
            backgroundColor = new java.awt.Color(255, 248, 248); // Light red
        } else {
            backgroundColor = new java.awt.Color(255, 252, 248); // Light orange
        }

        transcriptionArea.setBackground(backgroundColor);
        transcriptionArea.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
                javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Scroll pane
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(transcriptionArea);
        scrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new java.awt.Dimension(850, 400));

        textPanel.add(scrollPane, java.awt.BorderLayout.CENTER);

        // Character count label
        javax.swing.JLabel charCountLabel = new javax.swing.JLabel();
        charCountLabel.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.PLAIN, 11));
        charCountLabel.setForeground(java.awt.Color.GRAY);
        charCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

        // Update character count
        Runnable updateCharCount = () -> {
            int charCount = transcriptionArea.getText().length();
            int wordCount = transcriptionArea.getText().trim().isEmpty() ? 0
                    : transcriptionArea.getText().trim().split("\\s+").length;
            charCountLabel.setText(String.format("Characters: %d | Words: %d", charCount, wordCount));
        };

        updateCharCount.run();

        transcriptionArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateCharCount.run();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateCharCount.run();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateCharCount.run();
            }
        });

        textPanel.add(charCountLabel, java.awt.BorderLayout.SOUTH);
        mainPanel.add(textPanel, java.awt.BorderLayout.CENTER);

        // Button panel
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 10));

        // Process to SOAP button - with warning for low quality
        javax.swing.JButton processButton = new javax.swing.JButton("🤖 Process to SOAP Notes");
        processButton.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 12));
        processButton.setPreferredSize(new java.awt.Dimension(200, 35));

        // Button color based on quality
        if (quality.getQualityLevel().equals("VERY_LOW") || quality.getQualityLevel().equals("LOW")) {
            processButton.setBackground(new java.awt.Color(255, 140, 0)); // Orange warning
            processButton.setText("⚠️ Process with Caution");
        } else {
            processButton.setBackground(new java.awt.Color(40, 167, 69)); // Green
        }

        processButton.setForeground(java.awt.Color.WHITE);
        processButton.setFocusPainted(false);
        processButton.addActionListener(e -> {
            String editedText = transcriptionArea.getText().trim();
            if (!editedText.isEmpty()) {

                // Show warning for low quality transcription
                if (quality.getQualityLevel().equals("VERY_LOW") || quality.getQualityLevel().equals("LOW")) {
                    int confirm = javax.swing.JOptionPane.showConfirmDialog(transcriptionDialog,
                            "This transcription has low quality indicators.\n"
                            + "It may contain errors that could affect SOAP note accuracy.\n\n"
                            + "Do you want to proceed anyway?",
                            "Quality Warning",
                            javax.swing.JOptionPane.YES_NO_OPTION,
                            javax.swing.JOptionPane.WARNING_MESSAGE);

                    if (confirm != javax.swing.JOptionPane.YES_OPTION) {
                        return; // User cancelled
                    }
                }

                transcriptionDialog.dispose();
                // Call callback dengan teks yang sudah diedit
                if (callback != null) {
                    callback.onTranscriptionComplete(editedText);
                }
            } else {
                javax.swing.JOptionPane.showMessageDialog(transcriptionDialog,
                        "Transcription text cannot be empty!",
                        "Warning",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
            }
        });

        // Re-record button for low quality
        javax.swing.JButton reRecordButton = null;
        if (quality.getQualityLevel().equals("VERY_LOW") || quality.getQualityLevel().equals("LOW")) {
            reRecordButton = new javax.swing.JButton("🎤 Re-record");
            reRecordButton.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.PLAIN, 12));
            reRecordButton.setPreferredSize(new java.awt.Dimension(120, 35));
            reRecordButton.setBackground(new java.awt.Color(108, 117, 125));
            reRecordButton.setForeground(java.awt.Color.WHITE);
            reRecordButton.addActionListener(e -> {
                transcriptionDialog.dispose();
                // Start new recording
                startRecording();
            });
        }

        // Copy to clipboard button
        javax.swing.JButton copyButton = new javax.swing.JButton("📋 Copy Text");
        copyButton.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.PLAIN, 12));
        copyButton.setPreferredSize(new java.awt.Dimension(120, 35));
        copyButton.addActionListener(e -> {
            java.awt.datatransfer.StringSelection stringSelection
                    = new java.awt.datatransfer.StringSelection(transcriptionArea.getText());
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(stringSelection, null);

            // Show brief feedback
            copyButton.setText("✅ Copied!");
            javax.swing.Timer timer = new javax.swing.Timer(1500, evt -> copyButton.setText("📋 Copy Text"));
            timer.setRepeats(false);
            timer.start();
        });

        // Cancel button
        javax.swing.JButton cancelButton = new javax.swing.JButton("❌ Cancel");
        cancelButton.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.PLAIN, 12));
        cancelButton.setPreferredSize(new java.awt.Dimension(100, 35));
        cancelButton.addActionListener(e -> transcriptionDialog.dispose());

        // Add buttons to panel
        buttonPanel.add(processButton);
        if (reRecordButton != null) {
            buttonPanel.add(reRecordButton);
        }
        buttonPanel.add(copyButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, java.awt.BorderLayout.SOUTH);

        // Add main panel to dialog
        transcriptionDialog.add(mainPanel, java.awt.BorderLayout.CENTER);

        // Keyboard shortcuts
        transcriptionDialog.getRootPane().registerKeyboardAction(
                e -> transcriptionDialog.dispose(),
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        transcriptionDialog.getRootPane().registerKeyboardAction(
                e -> processButton.doClick(),
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER,
                        java.awt.event.InputEvent.CTRL_DOWN_MASK),
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // Show dialog
        transcriptionDialog.setVisible(true);

        // Focus ke text area untuk langsung bisa edit
        transcriptionArea.requestFocusInWindow();
    }

    /**
     * Show transcription result dialog - BACKWARD COMPATIBILITY
     */
    private void showTranscriptionResult(String transcriptionText) {
        // Analyze quality for backward compatibility
        TranscriptionQuality quality = analyzeTranscriptionQuality(transcriptionText);
        showTranscriptionResult(transcriptionText, quality);
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
     * Get server URL
     */
    public String getServerUrl() {
        return WHISPER_SERVER_URL;
    }

    /**
     * Inner class for transcription quality analysis
     */
    private static class TranscriptionQuality {

        private String qualityLevel;
        private int wordCount;
        private int charCount;
        private double medicalWordsRatio;

        public TranscriptionQuality() {
        }

        // Getters and setters
        public String getQualityLevel() {
            return qualityLevel;
        }

        public void setQualityLevel(String qualityLevel) {
            this.qualityLevel = qualityLevel;
        }

        public int getWordCount() {
            return wordCount;
        }

        public void setWordCount(int wordCount) {
            this.wordCount = wordCount;
        }

        public int getCharCount() {
            return charCount;
        }

        public void setCharCount(int charCount) {
            this.charCount = charCount;
        }

        public double getMedicalWordsRatio() {
            return medicalWordsRatio;
        }

        public void setMedicalWordsRatio(double medicalWordsRatio) {
            this.medicalWordsRatio = medicalWordsRatio;
        }

        public String getQualityMessage() {
            switch (qualityLevel) {
                case "VERY_LOW":
                    return "⚠️ Very short transcription - consider re-recording";
                case "LOW":
                    return "⚠️ Low quality - please verify accuracy carefully";
                case "MEDIUM":
                    return "✅ Medium quality - review before processing";
                case "GOOD":
                    return "✅ Good quality transcription";
                case "HIGH":
                    return "✅ High quality medical transcription";
                default:
                    return "✅ Transcription completed";
            }
        }

        public java.awt.Color getQualityColor() {
            switch (qualityLevel) {
                case "VERY_LOW":
                case "LOW":
                    return java.awt.Color.RED;
                case "MEDIUM":
                    return java.awt.Color.ORANGE;
                case "GOOD":
                case "HIGH":
                    return new java.awt.Color(0, 128, 0);
                default:
                    return java.awt.Color.BLACK;
            }
        }
    }
}
