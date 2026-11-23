package voip;

import javax.swing.*;

public class VoIPRealTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Ignore
            }
            
            JFrame frame = new JFrame("VoIP Real Test - SIMRS");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            VoIPPanelReal voipPanel = new VoIPPanelReal();
            frame.add(voipPanel);
            
            frame.setSize(400, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            
            // Add window closing event to shutdown VoIP
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    voipPanel.shutdown();
                    System.exit(0);
                }
            });
            
            // Show connection info
            JOptionPane.showMessageDialog(frame, 
                "VoIP Real Test dimulai!\n\n" +
                "Server: 192.168.88.119:5061\n" +
                "Extension: 119\n" +
                "User/Pass: 119\n\n" +
                "Panel akan otomatis mencoba register ke server PBX.\n" +
                "Pastikan server PBX dapat diakses.",
                "VoIP Real Test", 
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
}