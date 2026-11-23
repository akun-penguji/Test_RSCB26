package voip;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VoIPOutgoingDialog extends JDialog {
    private String calledNumber;
    private PeersSIPClient sipClient;
    private JLabel statusLabel;
    private boolean callConnected = false;
    
    public VoIPOutgoingDialog(Frame parent, String number, PeersSIPClient client) {
        super(parent, "📞 Memanggil", true);
        this.calledNumber = number;
        this.sipClient = client;
        
        initComponents();
        setupDialog();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(45, 45, 45));
        
        // Header Panel - Style like RSDH
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(33, 37, 41));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("📞 Outgoing Call");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Center Panel - Called Info with RSDH style
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        centerPanel.setBackground(new Color(45, 45, 45));
        
        // Called Avatar - Modern style
        JPanel avatarPanel = new JPanel();
        avatarPanel.setLayout(new BorderLayout());
        avatarPanel.setBackground(new Color(45, 45, 45));
        avatarPanel.setPreferredSize(new Dimension(80, 80));
        avatarPanel.setMaximumSize(new Dimension(80, 80));
        avatarPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(52, 144, 220), 3),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel avatarIcon = new JLabel("📞");
        avatarIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        avatarIcon.setForeground(Color.WHITE);
        avatarIcon.setHorizontalAlignment(SwingConstants.CENTER);
        avatarPanel.add(avatarIcon, BorderLayout.CENTER);
        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(avatarPanel);
        
        centerPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Called Number - Modern style
        JLabel numberLabel = new JLabel(calledNumber);
        numberLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        numberLabel.setForeground(Color.WHITE);
        numberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(numberLabel);
        
        centerPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        // Extension info
        JLabel extLabel = new JLabel("Extension: " + calledNumber);
        extLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        extLabel.setForeground(new Color(180, 180, 180));
        extLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(extLabel);
        
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Status Label - Modern style
        statusLabel = new JLabel("📳 Connecting...");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        statusLabel.setForeground(new Color(52, 144, 220));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(statusLabel);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Button Panel - RSDH style
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(45, 45, 45));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        // Hangup Button - Modern circular style
        JButton hangupButton = new JButton("📵");
        hangupButton.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        hangupButton.setPreferredSize(new Dimension(60, 60));
        hangupButton.setBackground(new Color(220, 53, 69));
        hangupButton.setForeground(Color.WHITE);
        hangupButton.setFocusPainted(false);
        hangupButton.setBorderPainted(false);
        hangupButton.setOpaque(true);
        hangupButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Make button circular
        hangupButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 40, 55), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        hangupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hangupCall();
            }
        });
        
        // Add hover effect
        hangupButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                hangupButton.setBackground(new Color(200, 35, 51));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                hangupButton.setBackground(new Color(220, 53, 69));
            }
        });
        
        buttonPanel.add(hangupButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupDialog() {
        setSize(350, 400);
        setResizable(false);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        // RSDH style - modern dark theme
        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(52, 144, 220), 2));
        
        // Make dialog always on top
        setAlwaysOnTop(true);
        toFront();
        
        // Auto-close after 60 seconds if no answer
        Timer autoCloseTimer = new Timer(60000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isDisplayable() && !callConnected) {
                    hangupCall();
                }
            }
        });
        autoCloseTimer.setRepeats(false);
        autoCloseTimer.start();
    }
    
    public void updateStatus(String status) {
        SwingUtilities.invokeLater(() -> {
            if (status.contains("connected") || status.contains("answered")) {
                callConnected = true;
                statusLabel.setText("📞 Call Connected");
                statusLabel.setForeground(new Color(40, 167, 69));
            } else if (status.contains("ringing")) {
                statusLabel.setText("📳 Ringing...");
                statusLabel.setForeground(new Color(255, 193, 7));
                
                // Add blinking effect for ringing
                Timer blinkTimer = new Timer(500, new ActionListener() {
                    boolean visible = true;
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        statusLabel.setVisible(visible);
                        visible = !visible;
                    }
                });
                blinkTimer.start();
                
                // Stop blinking after call is answered or failed
                Timer stopBlinkTimer = new Timer(10000, e -> blinkTimer.stop());
                stopBlinkTimer.setRepeats(false);
                stopBlinkTimer.start();
                
            } else if (status.contains("busy")) {
                statusLabel.setText("📵 Line Busy");
                statusLabel.setForeground(new Color(220, 53, 69));
                autoCloseOnError();
            } else if (status.contains("failed") || status.contains("error")) {
                statusLabel.setText("❌ Call Failed");
                statusLabel.setForeground(new Color(220, 53, 69));
                autoCloseOnError();
            } else {
                statusLabel.setText(status);
                statusLabel.setForeground(new Color(52, 144, 220));
            }
        });
    }
    
    private void autoCloseOnError() {
        Timer closeTimer = new Timer(3000, e -> dispose());
        closeTimer.setRepeats(false);
        closeTimer.start();
    }
    
    private void hangupCall() {
        if (sipClient != null) {
            sipClient.hangUp();
        }
        dispose();
    }
    
    public boolean isCallConnected() {
        return callConnected;
    }
}