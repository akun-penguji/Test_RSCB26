package bridging;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import javax.sound.sampled.*;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Service untuk menangani recording audio dan transcription menggunakan Whisper
 * @author YourName
 */
public class WhisperService {
    
    // Constants untuk Whisper configuration
    private static final String WHISPER_PYTHON_PATH = "python3";
    private static final String WHISPER_MODEL = "base";
    private static final String WHISPER_LANGUAGE = "id";
    
    // Audio recording variables
    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private ByteArrayOutputStream audioBuffer;
    private Thread recordingThread;
    private boolean isRecording = false;
    
    // UI reference
    private JButton microphoneButton;
    private Object parentDialog;
    
    // Callback interface untuk hasil transcription
    public interface TranscriptionCallback {
        void onTranscriptionComplete(String transcription);
        void onTranscriptionError(String error);
    }
    
    private TranscriptionCallback callback;
    
    /**
     * Constructor
     * @param microphoneButton Button untuk recording
     * @param parentDialog Parent dialog untuk message boxes
     */
    public WhisperService(JButton microphoneButton, Object parentDialog) {
        this.microphoneButton = microphoneButton;
        this.parentDialog = parentDialog;
        initializeAudio();
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
            // Audio format: 16kHz, 16-bit, mono
            audioFormat = new AudioFormat(16000, 16, 1, true, false);
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            audioBuffer = new ByteArrayOutputStream();

            System.out.println("✅ Audio initialized");

        } catch (Exception e) {
            System.out.println("❌ Audio init error: " + e.getMessage());
            e.printStackTrace();
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
                microphoneButton.setText("⏹️ Stop");
                microphoneButton.setBackground(java.awt.Color.RED);
            });

            audioBuffer.reset();

            // Recording thread
            recordingThread = new Thread(() -> {
                byte[] buffer = new byte[4096];
                System.out.println("🔴 Recording... speak now!");

                while (isRecording) {
                    int bytesRead = targetDataLine.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        audioBuffer.write(buffer, 0, bytesRead);
                    }
                }

                // Save and process audio when recording stops
                saveAndProcessAudio();
            });

            recordingThread.start();

        } catch (Exception e) {
            System.out.println("❌ Recording error: " + e.getMessage());
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

        if (targetDataLine != null) {
            targetDataLine.stop();
            targetDataLine.close();
        }

        // Reset button will be done after processing
    }
    
    /**
     * Save audio to file and process with Whisper
     */
    private void saveAndProcessAudio() {
        try {
            // Generate filename dengan timestamp
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String filename = "recording_" + sdf.format(new Date()) + ".wav";
            File outputFile = new File(filename);

            // Convert audio buffer ke AudioInputStream
            byte[] audioData = audioBuffer.toByteArray();
            if (audioData.length == 0) {
                System.out.println("⚠️ No audio data!");
                SwingUtilities.invokeLater(() -> {
                    resetButton();
                    showWarningDialog("No Audio", "No audio data recorded!");
                });
                return;
            }

            ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
            AudioInputStream audioInputStream = new AudioInputStream(
                    bais, audioFormat, audioData.length / audioFormat.getFrameSize()
            );

            // Save ke file WAV
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outputFile);

            // Calculate duration
            double duration = (double) audioData.length / (audioFormat.getSampleRate() * audioFormat.getFrameSize());

            System.out.println("✅ Audio saved: " + filename);
            System.out.println("   Duration: " + String.format("%.2f", duration) + " seconds");
            System.out.println("   Size: " + (outputFile.length() / 1024) + " KB");

            // Show success message
            SwingUtilities.invokeLater(() -> {
                showInfoDialog("Audio Saved", 
                    "Audio saved: " + filename + "\nDuration: " + String.format("%.2f", duration) + " seconds");
            });

            // Process with Whisper
            processAudioWithWhisper(outputFile);

        } catch (Exception e) {
            System.out.println("❌ Save error: " + e.getMessage());
            e.printStackTrace();
            
            SwingUtilities.invokeLater(() -> {
                resetButton();
                showErrorDialog("Save Error", "Failed to save audio: " + e.getMessage());
            });
        }
    }
    
    /**
     * Process audio file with Whisper
     */
    private void processAudioWithWhisper(File audioFile) {
        // Update UI to show processing status
        SwingUtilities.invokeLater(() -> {
            microphoneButton.setText("🔄 Processing...");
            microphoneButton.setEnabled(false);
        });

        // Run Whisper in background thread
        CompletableFuture.runAsync(() -> {
            try {
                System.out.println("🤖 Starting Whisper transcription...");
                String transcription = callWhisper(audioFile);

                // Update UI with result
                SwingUtilities.invokeLater(() -> {
                    resetButton();

                    if (!transcription.trim().isEmpty()) {
                        System.out.println("✅ Transcription: " + transcription);

                        // Show result in dialog
                        showInfoDialog("Whisper Result", "Transcription Result:\n\n" + transcription);

                        // Call callback with result
                        if (callback != null) {
                            callback.onTranscriptionComplete(transcription);
                        }

                    } else {
                        System.out.println("⚠️ No transcription result");
                        showWarningDialog("Whisper Result", "No speech detected or transcription failed");
                        
                        if (callback != null) {
                            callback.onTranscriptionError("No speech detected");
                        }
                    }
                });

            } catch (Exception e) {
                System.out.println("❌ Whisper error: " + e.getMessage());
                e.printStackTrace();

                SwingUtilities.invokeLater(() -> {
                    resetButton();
                    showErrorDialog("Error", "Error processing audio:\n" + e.getMessage());
                    
                    if (callback != null) {
                        callback.onTranscriptionError(e.getMessage());
                    }
                });
            }
        });
    }
    
    /**
     * Call Whisper locally
     */
    private String callWhisper(File audioFile) throws Exception {
        System.out.println("🔄 Calling Whisper: " + audioFile.getAbsolutePath());

        // Build Whisper command dengan CPU device untuk avoid GPU memory error
        ProcessBuilder pb = new ProcessBuilder(
                WHISPER_PYTHON_PATH, "-m", "whisper",
                audioFile.getAbsolutePath(),
                "--model", WHISPER_MODEL,
                "--language", WHISPER_LANGUAGE,
                "--output_format", "txt",
                "--output_dir", System.getProperty("user.dir"), // Current directory
                "--device", "cpu", // Force CPU usage
                "--verbose", "False"
        );

        pb.redirectErrorStream(true);

        // Debug: Print command
        System.out.println("   Command: " + String.join(" ", pb.command()));

        Process process = pb.start();

        // Read process output
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                System.out.println("   Whisper: " + line);
            }
        }

        int exitCode = process.waitFor();
        System.out.println("   Exit code: " + exitCode);

        if (exitCode != 0) {
            throw new RuntimeException("Whisper failed with exit code: " + exitCode + "\n" + output.toString());
        }

        // Read transcription result
        String audioFileName = audioFile.getName().replaceFirst("[.][^.]+$", "");
        File txtFile = new File(audioFileName + ".txt");

        if (txtFile.exists()) {
            String transcription = new String(java.nio.file.Files.readAllBytes(txtFile.toPath()));
            txtFile.delete(); // Clean up
            return transcription.trim();
        }

        return "";
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
     * Check if Whisper is installed
     */
    public static boolean isWhisperInstalled() {
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "-m", "whisper", "--help");
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Test Whisper with existing audio file
     */
    public static void testWhisperWithFile(String audioFilePath) {
        try {
            System.out.println("🧪 Testing Whisper with: " + audioFilePath);
            
            ProcessBuilder pb = new ProcessBuilder(
                "python", "-m", "whisper", 
                audioFilePath,
                "--model", "base",
                "--language", "id",
                "--output_format", "txt"
            );
            
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            // Read output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("   " + line);
                }
            }
            
            int exitCode = process.waitFor();
            System.out.println("   Exit code: " + exitCode);
            
        } catch (Exception e) {
            System.out.println("❌ Test error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
}