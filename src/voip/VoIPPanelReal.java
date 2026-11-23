package voip;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;

public class VoIPPanelReal extends JPanel implements PeersSIPClient.SIPEventListener {
    private PeersSIPClient sipClient;
    private JLabel statusLabel;
    private JTextField phoneNumberField;
    private JButton callButton;
    private JButton hangupButton;
    private JTextArea logArea;
    private DefaultTableModel phonebookModel;
    private JTable phonebookTable;
    
    private boolean inCall = false;
    private boolean incomingCall = false;
    
    public VoIPPanelReal() {
        initComponents();
        sipClient = PeersSIPClient.getInstance();
        sipClient.addEventListener(this);
        loadPhonebook();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 500));
        setBackground(new Color(45, 45, 45));
        
        // Header Panel - RSDH Style
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(33, 37, 41));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("📞 VoIP Phone - SIMRS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        
        statusLabel = new JLabel("Connecting...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(52, 144, 220));
        
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(new Color(33, 37, 41));
        titlePanel.add(titleLabel);
        
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setBackground(new Color(33, 37, 41));
        statusPanel.add(statusLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(statusPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main Content Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(45, 45, 45));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Dial Panel - RSDH Style
        JPanel dialPanel = new JPanel();
        dialPanel.setLayout(new BoxLayout(dialPanel, BoxLayout.Y_AXIS));
        dialPanel.setBackground(new Color(52, 58, 64));
        dialPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 144, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel dialLabel = new JLabel("📞 Quick Dial");
        dialLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dialLabel.setForeground(Color.WHITE);
        dialLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dialPanel.add(dialLabel);
        
        dialPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Phone number input
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        inputPanel.setBackground(new Color(52, 58, 64));
        
        phoneNumberField = new JTextField(15);
        phoneNumberField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        phoneNumberField.setBackground(new Color(73, 80, 87));
        phoneNumberField.setForeground(Color.WHITE);
        phoneNumberField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(108, 117, 125), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        phoneNumberField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !inCall && !incomingCall) {
                    makeCall();
                }
            }
        });
        
        inputPanel.add(phoneNumberField);
        dialPanel.add(inputPanel);
        
        dialPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Call buttons - RSDH Style
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(new Color(52, 58, 64));
        
        callButton = createModernButton("📞", new Color(40, 167, 69), "Call");
        callButton.addActionListener(e -> makeCall());
        
        hangupButton = createModernButton("📵", new Color(220, 53, 69), "Hangup");
        hangupButton.addActionListener(e -> hangup());
        hangupButton.setEnabled(false);
        
        buttonPanel.add(callButton);
        buttonPanel.add(hangupButton);
        dialPanel.add(buttonPanel);
        
        mainPanel.add(dialPanel, BorderLayout.NORTH);
        
        // Split Panel for Phonebook and Log
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setBackground(new Color(45, 45, 45));
        splitPane.setBorder(null);
        splitPane.setDividerLocation(200);
        
        // Phonebook Panel
        JPanel phonebookPanel = createPhonebookPanel();
        splitPane.setTopComponent(phonebookPanel);
        
        // Log Panel
        JPanel logPanel = createLogPanel();
        splitPane.setBottomComponent(logPanel);
        
        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JButton createModernButton(String icon, Color color, String tooltip) {
        JButton button = new JButton(icon);
        button.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(50, 50));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);
        
        // Hover effect
        Color originalColor = color;
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });
        
        return button;
    }
    
    private JPanel createPhonebookPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 58, 64));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 144, 220), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel("📖 Extensions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Phonebook table
        String[] columns = {"Name", "Extension"};
        phonebookModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        phonebookTable = new JTable(phonebookModel);
        phonebookTable.setBackground(new Color(73, 80, 87));
        phonebookTable.setForeground(Color.WHITE);
        phonebookTable.setSelectionBackground(new Color(52, 144, 220));
        phonebookTable.setSelectionForeground(Color.WHITE);
        phonebookTable.setGridColor(new Color(108, 117, 125));
        phonebookTable.getTableHeader().setBackground(new Color(33, 37, 41));
        phonebookTable.getTableHeader().setForeground(Color.WHITE);
        
        phonebookTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = phonebookTable.getSelectedRow();
                    if (row >= 0) {
                        String extension = (String) phonebookModel.getValueAt(row, 1);
                        phoneNumberField.setText(extension);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(phonebookTable);
        scrollPane.setBackground(new Color(52, 58, 64));
        scrollPane.getViewport().setBackground(new Color(73, 80, 87));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 58, 64));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 144, 220), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel titleLabel = new JLabel("📋 Activity Log");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        logArea = new JTextArea(6, 50);
        logArea.setEditable(false);
        logArea.setBackground(new Color(33, 37, 41));
        logArea.setForeground(new Color(248, 249, 250));
        logArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBackground(new Color(52, 58, 64));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadPhonebook() {
        // Add some default extensions
        phonebookModel.addRow(new Object[]{"Reception", "100"});
        phonebookModel.addRow(new Object[]{"Emergency", "101"});
        phonebookModel.addRow(new Object[]{"Pharmacy", "102"});
        phonebookModel.addRow(new Object[]{"Laboratory", "103"});
        phonebookModel.addRow(new Object[]{"Radiology", "104"});
        phonebookModel.addRow(new Object[]{"ICU", "105"});
        phonebookModel.addRow(new Object[]{"Director", "108"});
        phonebookModel.addRow(new Object[]{"IT Support", "119"});
    }
    
    private void makeCall() {
        String phoneNumber = phoneNumberField.getText().trim();
        if (phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a phone number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!sipClient.isRegistered()) {
            JOptionPane.showMessageDialog(this, "Not connected to VoIP server!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        sipClient.makeCall(phoneNumber);
        updateButtonStates();
    }
    
    private void hangup() {
        sipClient.hangUp();
        inCall = false;
        incomingCall = false;
        updateButtonStates();
    }
    
    private void updateButtonStates() {
        callButton.setEnabled(!inCall && !incomingCall && sipClient.isRegistered());
        hangupButton.setEnabled(inCall || incomingCall);
        phoneNumberField.setEnabled(!inCall && !incomingCall);
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
                statusLabel.setText("Connected");
                statusLabel.setForeground(new Color(40, 167, 69));
                updateButtonStates();
                
            } else if (message.contains("Registration failed")) {
                statusLabel.setText("Disconnected");
                statusLabel.setForeground(new Color(220, 53, 69));
                updateButtonStates();
                
            } else if (message.equals("INCOMING_CALL_START")) {
                incomingCall = true;
                updateButtonStates();
                
            } else if (message.equals("INCOMING_CALL_STOP")) {
                incomingCall = false;
                updateButtonStates();
                
            } else if (message.contains("connected") || message.contains("answered")) {
                inCall = true;
                incomingCall = false;
                updateButtonStates();
                
            } else if (message.contains("ended") || message.contains("terminated")) {
                inCall = false;
                incomingCall = false;
                updateButtonStates();
                
            } else if (message.contains("Calling")) {
                statusLabel.setText("Calling...");
                statusLabel.setForeground(new Color(255, 193, 7));
                
            } else if (message.contains("Ringing")) {
                statusLabel.setText("Ringing...");
                statusLabel.setForeground(new Color(255, 193, 7));
            }
        });
    }
    
    public void shutdown() {
        if (sipClient != null) {
            sipClient.shutdown();
        }
    }
}