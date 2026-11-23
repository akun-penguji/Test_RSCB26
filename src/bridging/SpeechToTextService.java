package bridging;

import fungsi.koneksiDB;
import javax.swing.JButton;

/**
 * Speech-to-Text Service yang menggunakan pengaturan database.xml
 * untuk memilih antara Whisper dan Google Speech
 * Java 8 compatible version
 * @author YourName
 */
public class SpeechToTextService {
    
    // Speech service types
    public enum ServiceType {
        WHISPER("whisper"),
        GOOGLE_SPEECH("google");
        
        private final String configValue;
        
        ServiceType(String configValue) {
            this.configValue = configValue;
        }
        
        public String getConfigValue() {
            return configValue;
        }
        
        public static ServiceType fromConfig(String configValue) {
            if (configValue == null) return WHISPER; // Default
            
            for (ServiceType type : values()) {
                if (type.configValue.equalsIgnoreCase(configValue.trim())) {
                    return type;
                }
            }
            return WHISPER; // Default fallback
        }
    }
    
    private WhisperCustomServerService whisperService;
    private GoogleSpeechToTextService googleSpeechService;
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
     */
    public SpeechToTextService(JButton microphoneButton, Object parentDialog) {
        this.microphoneButton = microphoneButton;
        this.parentDialog = parentDialog;
        
        // Initialize services
        initializeServices();
        
        ServiceType currentService = getCurrentServiceFromConfig();
        // Speech-to-Text Service initialized
        // Current service: " + currentService + " (from database.xml)"
    }
    
    /**
     * Initialize both speech services
     */
    private void initializeServices() {
        try {
            // Initialize Whisper service
            whisperService = new WhisperCustomServerService(microphoneButton, parentDialog);
            whisperService.setTranscriptionCallback(new WhisperCallbackAdapter());
            
            // Initialize Google Speech service
            googleSpeechService = new GoogleSpeechToTextService(microphoneButton, parentDialog);
            googleSpeechService.setTranscriptionCallback(new GoogleSpeechCallbackAdapter());
            
            // Both speech services initialized successfully
            
        } catch (Exception e) {
            System.out.println("❌ Error initializing speech services: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get current service type from database.xml configuration
     */
    private ServiceType getCurrentServiceFromConfig() {
        try {
            String configValue = koneksiDB.SPEECHSERVICE();
            ServiceType serviceType = ServiceType.fromConfig(configValue);
            
            // Speech service config: '" + configValue + "' -> " + serviceType
            return serviceType;
            
        } catch (Exception e) {
            System.out.println("⚠️ Error reading speech service config, using default (Whisper): " + e.getMessage());
            return ServiceType.WHISPER;
        }
    }
    
    /**
     * Set callback untuk hasil transcription
     */
    public void setTranscriptionCallback(TranscriptionCallback callback) {
        this.callback = callback;
    }
    
    /**
     * Start recording dengan service yang dikonfigurasi
     */
    public void startRecording() {
        ServiceType serviceType = getCurrentServiceFromConfig();
        
        System.out.println("🎤 Starting recording with: " + serviceType);
        
        try {
            switch (serviceType) {
                case WHISPER:
                    if (whisperService != null) {
                        whisperService.startRecording();
                    } else {
                        showError("Whisper service not available");
                    }
                    break;
                    
                case GOOGLE_SPEECH:
                    if (googleSpeechService != null) {
                        googleSpeechService.startRecording();
                    } else {
                        showError("Google Speech service not available");
                    }
                    break;
                    
                default:
                    showError("Unknown speech service configured: " + serviceType);
                    break;
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error starting recording: " + e.getMessage());
            showError("Failed to start recording: " + e.getMessage());
        }
    }
    
    /**
     * Stop recording
     */
    public void stopRecording() {
        ServiceType serviceType = getCurrentServiceFromConfig();
        
        System.out.println("⏹️ Stopping recording with: " + serviceType);
        
        try {
            switch (serviceType) {
                case WHISPER:
                    if (whisperService != null) {
                        whisperService.stopRecording();
                    }
                    break;
                    
                case GOOGLE_SPEECH:
                    if (googleSpeechService != null) {
                        googleSpeechService.stopRecording();
                    }
                    break;
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error stopping recording: " + e.getMessage());
        }
    }
    
    /**
     * Check if currently recording
     */
    public boolean isRecording() {
        ServiceType serviceType = getCurrentServiceFromConfig();
        
        switch (serviceType) {
            case WHISPER:
                return whisperService != null && whisperService.isRecording();
                
            case GOOGLE_SPEECH:
                return googleSpeechService != null && googleSpeechService.isRecording();
                
            default:
                return false;
        }
    }
    
    /**
     * Check current service connection
     */
    public boolean checkCurrentServiceConnection() {
        ServiceType serviceType = getCurrentServiceFromConfig();
        
        // Checking connection for: " + serviceType
        
        try {
            switch (serviceType) {
                case WHISPER:
                    return whisperService != null && whisperService.checkServerConnection();
                    
                case GOOGLE_SPEECH:
                    return googleSpeechService != null && googleSpeechService.checkAPIConnection();
                    
                default:
                    return false;
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error checking connection: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get current service URL/endpoint
     */
    public String getCurrentServiceUrl() {
        ServiceType serviceType = getCurrentServiceFromConfig();
        
        switch (serviceType) {
            case WHISPER:
                return whisperService != null ? whisperService.getServerUrl() : "Not configured";
                
            case GOOGLE_SPEECH:
                return googleSpeechService != null ? googleSpeechService.getAPIUrl() : "Not configured";
                
            default:
                return "Unknown service: " + serviceType;
        }
    }
    
    /**
     * Get current service display name
     */
    public String getCurrentServiceDisplayName() {
        ServiceType serviceType = getCurrentServiceFromConfig();
        
        switch (serviceType) {
            case WHISPER:
                return "Whisper Custom Server";
            case GOOGLE_SPEECH:
                return "Google Speech-to-Text";
            default:
                return "Unknown Service";
        }
    }
    
    /**
     * Get service status info
     */
    public String getServiceStatusInfo() {
        StringBuilder status = new StringBuilder();
        
        // Current service from config
        ServiceType currentService = getCurrentServiceFromConfig();
        status.append("Current Service: ").append(getCurrentServiceDisplayName()).append("\n");
        status.append("📋 Config Value: ").append(currentService.getConfigValue()).append("\n\n");
        
        // Check Whisper
        boolean whisperAvailable = whisperService != null && whisperService.checkServerConnection();
        status.append("Whisper: ").append(whisperAvailable ? "Available" : "Unavailable").append("\n");
        
        // Check Google Speech
        boolean googleAvailable = googleSpeechService != null && googleSpeechService.checkAPIConnection();
        status.append("Google Speech: ").append(googleAvailable ? "Available" : "Unavailable").append("\n");
        
        // Current service status
        boolean currentAvailable = checkCurrentServiceConnection();
        status.append("\nCurrent Service Status: ").append(currentAvailable ? "Ready" : "Not Ready");
        
        return status.toString();
    }
    
    /**
     * Reload configuration (useful after config changes)
     */
    public void reloadConfiguration() {
        System.out.println("🔄 Reloading speech service configuration...");
        
        // Reload Google Speech configuration if it's a Google service
        if (googleSpeechService != null) {
            try {
                // Create new instance to reload config
                googleSpeechService.close();
                googleSpeechService = new GoogleSpeechToTextService(microphoneButton, parentDialog);
                googleSpeechService.setTranscriptionCallback(new GoogleSpeechCallbackAdapter());
            } catch (Exception e) {
                System.out.println("⚠️ Error reloading Google Speech service: " + e.getMessage());
            }
        }
        
        ServiceType currentService = getCurrentServiceFromConfig();
        // Configuration reloaded, current service: " + currentService
    }
    
    /**
     * Update button display based on current service
     */
    public void updateButtonDisplay() {
        if (microphoneButton != null) {
            ServiceType serviceType = getCurrentServiceFromConfig();
            String icon = serviceType == ServiceType.GOOGLE_SPEECH ? "🎙️" : "🤖";
            
            if (isRecording()) {
                microphoneButton.setText("⏹️ Stop Recording");
                microphoneButton.setBackground(java.awt.Color.RED);
            } else {
                microphoneButton.setText(icon + " Record");
                microphoneButton.setBackground(null);
            }
            
            String tooltip = "Speech-to-Text (" + getCurrentServiceDisplayName() + ")";
            microphoneButton.setToolTipText(tooltip);
        }
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        if (callback != null) {
            callback.onTranscriptionError(message);
        } else {
            System.out.println("❌ " + message);
        }
    }
    
    /**
     * Close all services
     */
    public void closeServices() {
        try {
            if (whisperService != null) {
                // WhisperCustomServerService doesn't have close method in the provided code
                whisperService = null;
            }
            
            if (googleSpeechService != null) {
                googleSpeechService.close();
                googleSpeechService = null;
            }
            
            // All speech services closed
            
        } catch (Exception e) {
            System.out.println("⚠️ Error closing services: " + e.getMessage());
        }
    }
    
    /**
     * Callback adapter untuk Whisper service
     */
    private class WhisperCallbackAdapter implements WhisperCustomServerService.TranscriptionCallback {
        @Override
        public void onTranscriptionComplete(String transcription) {
            // Whisper transcription completed
            if (callback != null) {
                callback.onTranscriptionComplete(transcription);
            }
        }
        
        @Override
        public void onTranscriptionError(String error) {
            System.out.println("❌ Whisper transcription error: " + error);
            if (callback != null) {
                callback.onTranscriptionError("Whisper error: " + error);
            }
        }
    }
    
    /**
     * Callback adapter untuk Google Speech service
     */
    private class GoogleSpeechCallbackAdapter implements GoogleSpeechToTextService.TranscriptionCallback {
        @Override
        public void onTranscriptionComplete(String transcription) {
            // Google Speech transcription completed
            if (callback != null) {
                callback.onTranscriptionComplete(transcription);
            }
        }
        
        @Override
        public void onTranscriptionError(String error) {
            System.out.println("❌ Google Speech transcription error: " + error);
            if (callback != null) {
                callback.onTranscriptionError("Google Speech error: " + error);
            }
        }
    }
    
    /**
     * Test both services
     */
    public static void testAllServices() {
        System.out.println("🧪 Testing all speech services...");
        
        // Test Whisper - uncomment jika ada method test di WhisperCustomServerService
        // try {
        //     WhisperCustomServerService.testConnection();
        // } catch (Exception e) {
        //     System.out.println("❌ Whisper test failed: " + e.getMessage());
        // }
        
        // Test Google Speech
        try {
            GoogleSpeechToTextService.testGoogleSpeechConnection();
        } catch (Exception e) {
            System.out.println("❌ Google Speech test failed: " + e.getMessage());
        }
        
        System.out.println("🧪 Service testing completed");
    }
    
    /**
     * Get configuration help text
     */
    public static String getConfigurationHelp() {
        return "Speech Service Configuration (database.xml):\n\n" +
               "Add this entry to database.xml:\n" +
               "<entry key=\"SPEECHSERVICE\">whisper</entry>\n" +
               "or\n" +
               "<entry key=\"SPEECHSERVICE\">google</entry>\n\n" +
               "Values:\n" +
               "• whisper = Use Whisper Custom Server\n" +
               "• google = Use Google Speech-to-Text\n\n" +
               "Default: whisper (if not specified)\n\n" +
               "For Google Speech, also need:\n" +
               "<entry key=\"GOOGLESPEECHAPIKEY\">ENCRYPTED_API_KEY</entry>";
    }
}