package voip;

import javax.swing.*;
import java.awt.*;

public class VoIPOverlayTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("VoIP Overlay Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            
            // Add background content
            JPanel content = new JPanel();
            content.setBackground(Color.LIGHT_GRAY);
            content.add(new JLabel("Main Application Content"));
            frame.add(content);
            
            // Create VoIP overlay button (same as in frmUtama)
            JButton voipOverlayButton = new JButton("📞");
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
            
            // Add click action
            voipOverlayButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(frame, "VoIP Panel akan dibuka di sini!");
            });
            
            // Add to layered pane
            frame.getLayeredPane().add(voipOverlayButton, JLayeredPane.PALETTE_LAYER);
            
            frame.setVisible(true);
        });
    }
}