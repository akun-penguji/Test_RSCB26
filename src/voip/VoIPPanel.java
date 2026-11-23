package voip;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import widget.Button;
import widget.TextBox;

public class VoIPPanel extends JPanel implements SIPClient.SIPEventListener {
    private SIPClient sipClient;
    private JLabel statusLabel;
    private TextBox phoneNumberField;
    private Button callButton;
    private Button hangupButton;
    private Button registerButton;
    private JTextArea logArea;
    private boolean inCall = false;
    
    public VoIPPanel() {
        initComponents();
        sipClient = SIPClient.getInstance();
        sipClient.addEventListener(this);
        
        SwingUtilities.invokeLater(() -> {
            registerToServer();
        });
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 400));
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        statusLabel = new JLabel("Status: Disconnected");
        statusLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        statusLabel.setForeground(Color.RED);
        statusPanel.add(new JLabel("VoIP "));
        statusPanel.add(statusLabel);
        
        JPanel dialPanel = new JPanel();
        dialPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        dialPanel.add(new JLabel("Nomor Telepon:"), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        phoneNumberField = new TextBox();
        phoneNumberField.setPreferredSize(new Dimension(200, 23));
        phoneNumberField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !inCall) {
                    makeCall();
                }
            }
        });
        dialPanel.add(phoneNumberField, gbc);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        
        callButton = new Button();
        callButton.setText("Call");
        callButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/call16.png")));
        callButton.setPreferredSize(new Dimension(100, 23));
        callButton.addActionListener(e -> makeCall());
        
        hangupButton = new Button();
        hangupButton.setText("Hangup");
        hangupButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        hangupButton.setPreferredSize(new Dimension(100, 23));
        hangupButton.setEnabled(false);
        hangupButton.addActionListener(e -> hangup());
        
        registerButton = new Button();
        registerButton.setText("Register");
        registerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        registerButton.setPreferredSize(new Dimension(100, 23));
        registerButton.addActionListener(e -> registerToServer());
        
        buttonPanel.add(callButton);
        buttonPanel.add(hangupButton);
        buttonPanel.add(registerButton);
        
        topPanel.add(statusPanel, BorderLayout.NORTH);
        topPanel.add(dialPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Log Aktivitas"));
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void makeCall() {
        String phoneNumber = phoneNumberField.getText().trim();
        if (phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan nomor telepon terlebih dahulu!");
            return;
        }
        
        if (!sipClient.isRegistered()) {
            JOptionPane.showMessageDialog(this, "Belum terhubung ke server VoIP. Silakan register terlebih dahulu!");
            return;
        }
        
        sipClient.makeCall(phoneNumber);
        inCall = true;
        updateButtonStates();
    }
    
    private void hangup() {
        sipClient.hangUp();
        inCall = false;
        updateButtonStates();
    }
    
    private void registerToServer() {
        addLog("Mencoba koneksi ke server VoIP...");
        sipClient.register();
    }
    
    private void updateButtonStates() {
        callButton.setEnabled(!inCall && sipClient.isRegistered());
        hangupButton.setEnabled(inCall);
        phoneNumberField.setEnabled(!inCall);
    }
    
    private void addLog(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
    
    @Override
    public void onSIPEvent(String message) {
        addLog(message);
        
        SwingUtilities.invokeLater(() -> {
            if (message.contains("Registration successful")) {
                statusLabel.setText("Status: Connected");
                statusLabel.setForeground(new Color(0, 128, 0));
                updateButtonStates();
            } else if (message.contains("Registration failed")) {
                statusLabel.setText("Status: Disconnected");
                statusLabel.setForeground(Color.RED);
                updateButtonStates();
            } else if (message.contains("Call connected")) {
                inCall = true;
                updateButtonStates();
            } else if (message.contains("Call ended") || message.contains("Call terminated")) {
                inCall = false;
                updateButtonStates();
            } else if (message.contains("Incoming call")) {
                int result = JOptionPane.showConfirmDialog(this, 
                    message + "\nTerima panggilan?", 
                    "Panggilan Masuk", 
                    JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    inCall = true;
                    updateButtonStates();
                }
            }
        });
    }
    
    public void shutdown() {
        if (sipClient != null) {
            sipClient.shutdown();
        }
    }
}