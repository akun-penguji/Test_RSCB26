package voip;

import javax.swing.*;
import java.awt.*;

public class VoIPOverlayAnimationDemo {
    private static JButton voipOverlayButton;
    private static Timer voipBlinkTimer;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("VoIP Overlay Animation Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            
            // Add background content
            JPanel content = new JPanel();
            content.setBackground(Color.LIGHT_GRAY);
            content.add(new JLabel("SIMRS Main Content"));
            frame.add(content);
            
            // Create VoIP overlay button
            voipOverlayButton = new JButton("📞");
            voipOverlayButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            voipOverlayButton.setSize(50, 50);
            voipOverlayButton.setToolTipText("VoIP - Telepon (Klik untuk buka panel)");
            voipOverlayButton.setBorder(BorderFactory.createRaisedBevelBorder());
            voipOverlayButton.setBackground(new Color(34, 139, 34));
            voipOverlayButton.setForeground(Color.WHITE);
            voipOverlayButton.setOpaque(true);
            voipOverlayButton.setFocusPainted(false);
            
            // Position at bottom right
            int x = frame.getWidth() - 80;
            int y = frame.getHeight() - 120;
            voipOverlayButton.setLocation(x, y);
            
            // Setup blink timer for incoming calls
            voipBlinkTimer = new Timer(500, e -> {
                if (voipOverlayButton != null) {
                    Color currentBg = voipOverlayButton.getBackground();
                    if (currentBg.equals(Color.RED)) {
                        voipOverlayButton.setBackground(new Color(255, 165, 0)); // Orange
                        voipOverlayButton.setText("📳");
                    } else {
                        voipOverlayButton.setBackground(Color.RED);
                        voipOverlayButton.setText("📞");
                    }
                    voipOverlayButton.repaint();
                }
            });
            
            // Add click action
            voipOverlayButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(frame, "VoIP Panel dibuka!");
                stopIncomingCallAnimation();
            });
            
            // Add to layered pane
            frame.getLayeredPane().add(voipOverlayButton, JLayeredPane.PALETTE_LAYER);
            
            // Add demo buttons
            JPanel demoPanel = new JPanel();
            demoPanel.setLayout(new FlowLayout());
            
            JButton btnStartAnimation = new JButton("Simulate Incoming Call");
            btnStartAnimation.addActionListener(e -> startIncomingCallAnimation());
            
            JButton btnStopAnimation = new JButton("Stop Animation");
            btnStopAnimation.addActionListener(e -> stopIncomingCallAnimation());
            
            JButton btnSetConnected = new JButton("Set Connected");
            btnSetConnected.addActionListener(e -> setOverlayStatus("connected"));
            
            JButton btnSetDisconnected = new JButton("Set Disconnected");
            btnSetDisconnected.addActionListener(e -> setOverlayStatus("disconnected"));
            
            JButton btnSetCalling = new JButton("Set Calling");
            btnSetCalling.addActionListener(e -> setOverlayStatus("calling"));
            
            JButton btnSetInCall = new JButton("Set In Call");
            btnSetInCall.addActionListener(e -> setOverlayStatus("in_call"));
            
            demoPanel.add(btnStartAnimation);
            demoPanel.add(btnStopAnimation);
            demoPanel.add(btnSetConnected);
            demoPanel.add(btnSetDisconnected);
            demoPanel.add(btnSetCalling);
            demoPanel.add(btnSetInCall);
            
            frame.add(demoPanel, BorderLayout.SOUTH);
            frame.setVisible(true);
        });
    }
    
    private static void startIncomingCallAnimation() {
        if (voipOverlayButton != null && voipBlinkTimer != null) {
            voipOverlayButton.setToolTipText("📞 PANGGILAN MASUK! Klik untuk jawab");
            voipBlinkTimer.start();
        }
    }
    
    private static void stopIncomingCallAnimation() {
        if (voipOverlayButton != null && voipBlinkTimer != null) {
            voipBlinkTimer.stop();
            voipOverlayButton.setBackground(new Color(34, 139, 34)); // Back to green
            voipOverlayButton.setText("📞");
            voipOverlayButton.setToolTipText("VoIP - Telepon (Klik untuk buka panel)");
            voipOverlayButton.repaint();
        }
    }
    
    private static void setOverlayStatus(String status) {
        if (voipOverlayButton != null) {
            switch (status.toLowerCase()) {
                case "connected":
                    voipOverlayButton.setBackground(new Color(34, 139, 34)); // Green
                    voipOverlayButton.setText("📞");
                    voipOverlayButton.setToolTipText("VoIP - Connected");
                    break;
                case "disconnected":
                    voipOverlayButton.setBackground(Color.GRAY);
                    voipOverlayButton.setText("📞");
                    voipOverlayButton.setToolTipText("VoIP - Disconnected");
                    break;
                case "calling":
                    voipOverlayButton.setBackground(new Color(255, 165, 0)); // Orange
                    voipOverlayButton.setText("📞");
                    voipOverlayButton.setToolTipText("VoIP - Calling...");
                    break;
                case "in_call":
                    voipOverlayButton.setBackground(new Color(0, 100, 200)); // Blue
                    voipOverlayButton.setText("📞");
                    voipOverlayButton.setToolTipText("VoIP - In Call");
                    break;
            }
            voipOverlayButton.repaint();
        }
    }
}