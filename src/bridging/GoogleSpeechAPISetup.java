package bridging;

import fungsi.koneksiDB;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Utility untuk setup dan enkripsi Google Speech API Key
 * Updated to use Apache HttpClient 5 and Java 8 compatible
 * @author YourName
 */
public class GoogleSpeechAPISetup {
    
    /**
     * Show dialog untuk input dan enkripsi Google Speech API Key
     */
    public static void showAPIKeySetupDialog(Component parent) {
        JDialog setupDialog = new JDialog();
        setupDialog.setTitle("Google Speech API Key Setup");
        setupDialog.setModal(true);
        setupDialog.setSize(550, 450);
        setupDialog.setLocationRelativeTo(parent);
        setupDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header
        JLabel headerLabel = new JLabel("Google Speech-to-Text API Key Setup");
        headerLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Current status
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel statusPanel = createStatusPanel();
        contentPanel.add(statusPanel, gbc);
        
        // API Key input
        gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel keyLabel = new JLabel("API Key:");
        keyLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        contentPanel.add(keyLabel, gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        JPasswordField keyField = new JPasswordField(30);
        keyField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        contentPanel.add(keyField, gbc);
        
        // Show checkbox
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        JCheckBox showCheckBox = new JCheckBox("Show API Key");
        showCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (showCheckBox.isSelected()) {
                    keyField.setEchoChar((char) 0);
                } else {
                    keyField.setEchoChar('*');
                }
            }
        });
        contentPanel.add(showCheckBox, gbc);
        
        // Instructions
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH; 
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        JPanel instructionsPanel = createInstructionsPanel();
        contentPanel.add(instructionsPanel, gbc);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton testButton = new JButton("Test API Key");
        testButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String apiKey = new String(keyField.getPassword()).trim();
                if (apiKey.isEmpty()) {
                    JOptionPane.showMessageDialog(setupDialog, 
                        "Please enter an API key first.", "Input Required", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                testAPIKey(apiKey, setupDialog);
            }
        });
        
        JButton saveButton = new JButton("Save & Encrypt");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String apiKey = new String(keyField.getPassword()).trim();
                if (apiKey.isEmpty()) {
                    JOptionPane.showMessageDialog(setupDialog, 
                        "Please enter an API key first.", "Input Required", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (saveEncryptedAPIKey(apiKey, setupDialog)) {
                    setupDialog.dispose();
                }
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setupDialog.dispose();
            }
        });
        
        buttonPanel.add(testButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        setupDialog.add(mainPanel);
        setupDialog.setVisible(true);
    }
    
    /**
     * Create status panel
     */
    private static JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Current Status"));
        
        JTextArea statusArea = new JTextArea(3, 40);
        statusArea.setEditable(false);
        statusArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        
        StringBuilder status = new StringBuilder();
        try {
            String speechService = koneksiDB.SPEECHSERVICE();
            status.append("Speech Service: ").append(speechService).append("\n");
            
            boolean apiKeyConfigured = isAPIKeyConfigured();
            status.append("API Key: ").append(apiKeyConfigured ? "Configured" : "Not configured").append("\n");
            
            if (apiKeyConfigured) {
                status.append("Preview: ").append(getMaskedAPIKey());
            } else {
                status.append("Status: Ready for setup");
            }
        } catch (Exception e) {
            status.append("Error: ").append(e.getMessage());
        }
        
        statusArea.setText(status.toString());
        panel.add(new JScrollPane(statusArea), BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create instructions panel
     */
    private static JPanel createInstructionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Instructions"));
        
        JTextArea instructionsArea = new JTextArea();
        instructionsArea.setText(
                "How to get Google Speech API Key:\n\n" +
                "1. Go to Google Cloud Console (console.cloud.google.com)\n" +
                "2. Create or select a project\n" +
                "3. Enable Cloud Speech-to-Text API\n" +
                "4. Go to 'APIs & Services' -> 'Credentials'\n" +
                "5. Click 'Create Credentials' -> 'API Key'\n" +
                "6. Copy the API key and paste above\n\n" +
                "Note: Billing must be enabled (free tier available)\n" +
                "The API key will be encrypted before storage."
        );
        
        instructionsArea.setEditable(false);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        
        JScrollPane scrollPane = new JScrollPane(instructionsArea);
        scrollPane.setPreferredSize(new Dimension(500, 150));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Test API key
     */
    private static void testAPIKey(String apiKey, Component parent) {
        JDialog progressDialog = new JDialog();
        progressDialog.setTitle("Testing API Key");
        progressDialog.setModal(true);
        progressDialog.setSize(300, 100);
        progressDialog.setLocationRelativeTo(parent);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Testing connection...", SwingConstants.CENTER), BorderLayout.CENTER);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        panel.add(progressBar, BorderLayout.SOUTH);
        
        progressDialog.add(panel);
        progressDialog.setVisible(true);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return testGoogleSpeechAPIKey(apiKey);
            }
            
            @Override
            protected void done() {
                progressDialog.dispose();
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(parent,
                                "API Key test successful!\n\nThe key is valid and ready to use.",
                                "Test Result", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(parent,
                                "API Key test failed!\n\nPossible issues:\n" +
                                "• Invalid API key\n• API not enabled\n• Network issues\n• Billing not enabled",
                                "Test Result", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(parent,
                            "Test error: " + e.getMessage(),
                            "Test Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Test Google Speech API key using HttpClient 5
     */
    private static boolean testGoogleSpeechAPIKey(String apiKey) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String testJson = "{\n" +
                "    \"config\": {\n" +
                "        \"encoding\": \"LINEAR16\",\n" +
                "        \"sampleRateHertz\": 16000,\n" +
                "        \"languageCode\": \"id-ID\"\n" +
                "    },\n" +
                "    \"audio\": {\n" +
                "        \"content\": \"\"\n" +
                "    }\n" +
                "}";
            
            HttpPost httpPost = new HttpPost(
                "https://speech.googleapis.com/v1/speech:recognize?key=" + apiKey);
            httpPost.setHeader("Content-Type", "application/json");
            
            StringEntity entity = new StringEntity(testJson, ContentType.APPLICATION_JSON);
            httpPost.setEntity(entity);
            
            try (CloseableHttpResponse response = client.execute(httpPost)) {
                int statusCode = response.getCode();
                
                // 400 = valid key but empty audio, 401/403 = invalid key
                if (statusCode == 400) {
                    return true;
                } else if (statusCode == 401 || statusCode == 403) {
                    return false;
                } else {
                    return statusCode != 401 && statusCode != 403;
                }
            }
            
        } catch (Exception e) {
            System.out.println("API test error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Save encrypted API key
     */
    private static boolean saveEncryptedAPIKey(String apiKey, Component parent) {
        try {
            boolean success = koneksiDB.setGOOGLESPEECHAPIKEY(apiKey);
            
            if (success) {
                JOptionPane.showMessageDialog(parent,
                        "Configuration saved successfully!\n\n" +
                        "The API key has been encrypted and stored.\n" +
                        "Google Speech-to-Text is now ready to use.\n\n" +
                        "To activate: Set SPEECHSERVICE=google in database.xml",
                        "Save Complete", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(parent,
                        "Failed to save configuration!\n\n" +
                        "Please check file permissions and try again.",
                        "Save Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Save error: " + e.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    /**
     * Check if API key is configured
     */
    public static boolean isAPIKeyConfigured() {
        try {
            String apiKey = koneksiDB.GOOGLESPEECHAPIKEY();
            return apiKey != null && !apiKey.trim().isEmpty() && apiKey.length() > 10;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get masked API key for display
     */
    public static String getMaskedAPIKey() {
        try {
            String apiKey = koneksiDB.GOOGLESPEECHAPIKEY();
            if (apiKey == null || apiKey.trim().isEmpty()) {
                return "Not configured";
            }
            
            if (apiKey.length() > 10) {
                return apiKey.substring(0, 6) + "*****" + apiKey.substring(apiKey.length() - 4);
            } else {
                return "*****";
            }
        } catch (Exception e) {
            return "Error loading";
        }
    }
    
    /**
     * Quick setup for testing
     */
    public static void quickSetup() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                showAPIKeySetupDialog(null);
            }
        });
    }
}