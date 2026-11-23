package bridging;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Service selector untuk memilih antara Whisper Custom Server dan Google Speech-to-Text
 * @author YourName
 */
public class SpeechToTextServiceSelector {
    
    public enum SpeechService {
        WHISPER_CUSTOM("Whisper Custom Server", "🤖"),
        GOOGLE_SPEECH("Google Speech-to-Text", "🎙️");
        
        private final String displayName;
        private final String icon;
        
        SpeechService(String displayName, String icon) {
            this.displayName = displayName;
            this.icon = icon;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getIcon() {
            return icon;
        }
        
        @Override
        public String toString() {
            return icon + " " + displayName;
        }
    }
    
    private static SpeechService selectedService = SpeechService.WHISPER_CUSTOM; // Default
    private static JDialog configDialog;
    
    /**
     * Get currently selected service
     */
    public static SpeechService getSelectedService() {
        return selectedService;
    }
    
    /**
     * Set selected service
     */
    public static void setSelectedService(SpeechService service) {
        selectedService = service;
        System.out.println("✅ Speech service changed to: " + service.getDisplayName());
    }
    
    /**
     * Show service selection dialog
     */
    public static void showServiceSelectionDialog(Component parent) {
        if (configDialog != null && configDialog.isVisible()) {
            configDialog.toFront();
            return;
        }
        
        // Create dialog
        if (parent instanceof Frame) {
            configDialog = new JDialog((Frame) parent, "Speech-to-Text Service Selection", true);
        } else if (parent instanceof Dialog) {
            configDialog = new JDialog((Dialog) parent, "Speech-to-Text Service Selection", true);
        } else {
            configDialog = new JDialog();
            configDialog.setTitle("Speech-to-Text Service Selection");
            configDialog.setModal(true);
        }
        
        configDialog.setLayout(new BorderLayout());
        configDialog.setSize(500, 400);
        configDialog.setLocationRelativeTo(parent);
        configDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel headerLabel = new JLabel("Choose Speech-to-Text Service");
        headerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Service selection panel
        JPanel selectionPanel = createServiceSelectionPanel();
        mainPanel.add(selectionPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        configDialog.add(mainPanel);
        configDialog.setVisible(true);
    }
    
    /**
     * Create service selection panel with radio buttons and descriptions
     */
    private static JPanel createServiceSelectionPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Available Services"));
        
        ButtonGroup serviceGroup = new ButtonGroup();
        
        // Whisper Custom Server option
        JPanel whisperPanel = createServiceOptionPanel(
            SpeechService.WHISPER_CUSTOM,
            "• Local Whisper server required\n" +
            "• High accuracy for medical terms\n" +
            "• Supports Indonesian language\n" +
            "• No API key required\n" +
            "• Offline capability",
            serviceGroup
        );
        
        // Google Speech-to-Text option
        JPanel googlePanel = createServiceOptionPanel(
            SpeechService.GOOGLE_SPEECH,
            "• Cloud-based Google service\n" +
            "• Excellent accuracy and speed\n" +
            "• Advanced language models\n" +
            "• Requires Google API key\n" +
            "• Internet connection required",
            serviceGroup
        );
        
        panel.add(whisperPanel);
        panel.add(googlePanel);
        
        return panel;
    }
    
    /**
     * Create individual service option panel
     */
    private static JPanel createServiceOptionPanel(SpeechService service, String description, ButtonGroup group) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.setBackground(Color.WHITE);
        
        // Radio button dengan service name
        JRadioButton radioButton = new JRadioButton(service.toString());
        radioButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        radioButton.setSelected(service == selectedService);
        radioButton.setBackground(Color.WHITE);
        
        radioButton.addActionListener(e -> {
            if (radioButton.isSelected()) {
                selectedService = service;
                updateServiceStatus();
            }
        });
        
        group.add(radioButton);
        
        // Description text
        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        descArea.setEditable(false);
        descArea.setBackground(Color.WHITE);
        descArea.setBorder(BorderFactory.createEmptyBorder(5, 25, 5, 5));
        
        // Status label
        JLabel statusLabel = new JLabel();
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 11));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 25, 5, 5));
        
        // Check service availability
        boolean isAvailable = checkServiceAvailability(service);
        if (isAvailable) {
            statusLabel.setText("✅ Service available");
            statusLabel.setForeground(new Color(0, 128, 0));
        } else {
            statusLabel.setText("⚠️ Configuration required");
            statusLabel.setForeground(Color.ORANGE);
            if (service == selectedService) {
                // If current selected service is not available, switch to available one
                for (SpeechService altService : SpeechService.values()) {
                    if (checkServiceAvailability(altService)) {
                        selectedService = altService;
                        radioButton.setSelected(false);
                        break;
                    }
                }
            }
        }
        
        panel.add(radioButton, BorderLayout.NORTH);
        panel.add(descArea, BorderLayout.CENTER);
        panel.add(statusLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Check if service is properly configured and available
     */
    private static boolean checkServiceAvailability(SpeechService service) {
        try {
            switch (service) {
                case WHISPER_CUSTOM:
                    // Check if Whisper server URL is configured
                    String whisperUrl = fungsi.koneksiDB.WHISPERSERVERURL();
                    return whisperUrl != null && !whisperUrl.trim().isEmpty();
                    
                case GOOGLE_SPEECH:
                    // Check if Google Speech API key is configured
                    String googleApiKey = fungsi.koneksiDB.GOOGLESPEECHAPIKEY();
                    return googleApiKey != null && !googleApiKey.trim().isEmpty();
                    
                default:
                    return false;
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error checking service availability: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update service status in UI
     */
    private static void updateServiceStatus() {
        // This method can be called to refresh status if needed
        System.out.println("📊 Service status updated - Selected: " + selectedService.getDisplayName());
    }
    
    /**
     * Create button panel
     */
    private static JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        // Test connection button
        JButton testButton = new JButton("🔍 Test Connection");
        testButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        testButton.setPreferredSize(new Dimension(150, 35));
        testButton.addActionListener(e -> testSelectedService());
        
        // Save and close button
        JButton saveButton = new JButton("✅ Apply Selection");
        saveButton.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        saveButton.setPreferredSize(new Dimension(150, 35));
        saveButton.setBackground(new Color(40, 167, 69));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> {
            saveServiceSelection();
            configDialog.dispose();
        });
        
        // Cancel button
        JButton cancelButton = new JButton("❌ Cancel");
        cancelButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.addActionListener(e -> configDialog.dispose());
        
        panel.add(testButton);
        panel.add(saveButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    /**
     * Test selected service connection
     */
    private static void testSelectedService() {
        System.out.println("🧪 Testing connection for: " + selectedService.getDisplayName());
        
        SwingUtilities.invokeLater(() -> {
            JDialog progressDialog = createProgressDialog();
            progressDialog.setVisible(true);
            
            // Test in background
            SwingWorker<Boolean, Void> testWorker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    switch (selectedService) {
                        case WHISPER_CUSTOM:
                            return testWhisperConnection();
                        case GOOGLE_SPEECH:
                            return testGoogleSpeechConnection();
                        default:
                            return false;
                    }
                }
                
                @Override
                protected void done() {
                    progressDialog.dispose();
                    
                    try {
                        boolean success = get();
                        
                        if (success) {
                            JOptionPane.showMessageDialog(configDialog,
                                "✅ Connection successful!\n\n" +
                                selectedService.getDisplayName() + " is working properly.",
                                "Connection Test",
                                JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(configDialog,
                                "❌ Connection failed!\n\n" +
                                "Please check the configuration for " + selectedService.getDisplayName() + ".",
                                "Connection Test",
                                JOptionPane.ERROR_MESSAGE);
                        }
                        
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(configDialog,
                            "❌ Test error: " + e.getMessage(),
                            "Connection Test",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            
            testWorker.execute();
        });
    }
    
    /**
     * Create progress dialog for testing
     */
    private static JDialog createProgressDialog() {
        JDialog progressDialog = new JDialog(configDialog, "Testing Connection", true);
        progressDialog.setSize(300, 120);
        progressDialog.setLocationRelativeTo(configDialog);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel messageLabel = new JLabel("Testing " + selectedService.getDisplayName() + "...");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);
        
        progressDialog.add(panel);
        progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        return progressDialog;
    }
    
    /**
     * Test Whisper connection
     */
    private static boolean testWhisperConnection() {
        try {
            // Create temporary WhisperCustomServerService untuk test
            JButton dummyButton = new JButton();
            WhisperCustomServerService testService = new WhisperCustomServerService(dummyButton, configDialog);
            boolean result = testService.checkServerConnection();
            testService = null; // Cleanup
            return result;
        } catch (Exception e) {
            System.out.println("❌ Whisper test error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Test Google Speech connection
     */
    private static boolean testGoogleSpeechConnection() {
        try {
            // Create temporary GoogleSpeechToTextService untuk test
            JButton dummyButton = new JButton();
            GoogleSpeechToTextService testService = new GoogleSpeechToTextService(dummyButton, configDialog);
            boolean result = testService.checkAPIConnection();
            testService.close();
            return result;
        } catch (Exception e) {
            System.out.println("❌ Google Speech test error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Save service selection
     */
    private static void saveServiceSelection() {
        System.out.println("💾 Saving service selection: " + selectedService.getDisplayName());
        
        // Optionally save to preferences or config file
        // For now, just keep in memory
        
        JOptionPane.showMessageDialog(configDialog,
            "Service selection saved!\n\n" +
            "Now using: " + selectedService.getDisplayName(),
            "Settings Saved",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Get quick service selection button for toolbar
     */
    public static JButton createQuickSelectorButton(Component parent) {
        JButton selectorButton = new JButton(selectedService.getIcon());
        selectorButton.setToolTipText("Current: " + selectedService.getDisplayName() + " (Click to change)");
        selectorButton.setPreferredSize(new Dimension(30, 30));
        selectorButton.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        
        selectorButton.addActionListener(e -> showServiceSelectionDialog(parent));
        
        return selectorButton;
    }
    
    /**
     * Create service info label
     */
    public static JLabel createServiceInfoLabel() {
        JLabel infoLabel = new JLabel(selectedService.getIcon() + " " + selectedService.getDisplayName());
        infoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        infoLabel.setForeground(Color.GRAY);
        return infoLabel;
    }
}