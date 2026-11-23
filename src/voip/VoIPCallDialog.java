package voip;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VoIPCallDialog extends JDialog {
    private String callerNumber;
    private String callerName;
    private boolean answered = false;
    private PeersSIPClient sipClient;
    
    public VoIPCallDialog(Frame parent, String caller, String number, PeersSIPClient client) {
        super(parent, "📞 Panggilan Masuk", true);
        this.callerNumber = number;
        this.callerName = caller;
        this.sipClient = client;
        
        initComponents();
        setupDialog();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 240, 240));
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout());
        headerPanel.setBackground(new Color(51, 122, 183));
        
        JLabel titleLabel = new JLabel("📞 PANGGILAN MASUK");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Center Panel - Caller Info
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerPanel.setBackground(Color.WHITE);
        
        // Caller Avatar (placeholder)
        JLabel avatarLabel = new JLabel("👤");
        avatarLabel.setFont(new Font("Arial", Font.PLAIN, 48));
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(avatarLabel);
        
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Caller Name
        JLabel nameLabel = new JLabel(callerName != null && !callerName.isEmpty() ? callerName : "Unknown Caller");
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(nameLabel);
        
        // Caller Number
        JLabel numberLabel = new JLabel(callerNumber);
        numberLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        numberLabel.setForeground(Color.GRAY);
        numberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(numberLabel);
        
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Status Label
        JLabel statusLabel = new JLabel("📳 Calling...");
        statusLabel.setFont(new Font("Tahoma", Font.ITALIC, 12));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(statusLabel);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(Color.WHITE);
        
        // Answer Button
        JButton answerButton = new JButton("✅ Terima");
        answerButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        answerButton.setPreferredSize(new Dimension(120, 45));
        answerButton.setBackground(new Color(92, 184, 92));
        answerButton.setForeground(Color.WHITE);
        answerButton.setFocusPainted(false);
        answerButton.setBorder(BorderFactory.createRaisedBevelBorder());
        answerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                answerCall();
            }
        });
        
        // Reject Button
        JButton rejectButton = new JButton("❌ Tolak");
        rejectButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        rejectButton.setPreferredSize(new Dimension(120, 45));
        rejectButton.setBackground(new Color(217, 83, 79));
        rejectButton.setForeground(Color.WHITE);
        rejectButton.setFocusPainted(false);
        rejectButton.setBorder(BorderFactory.createRaisedBevelBorder());
        rejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rejectCall();
            }
        });
        
        buttonPanel.add(answerButton);
        buttonPanel.add(rejectButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupDialog() {
        setSize(300, 350);
        setResizable(false);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        // Make dialog always on top and focused
        setAlwaysOnTop(true);
        toFront();
        requestFocus();
        
        // Auto-reject after 30 seconds
        Timer autoRejectTimer = new Timer(30000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isDisplayable()) {
                    rejectCall();
                }
            }
        });
        autoRejectTimer.setRepeats(false);
        autoRejectTimer.start();
    }
    
    private void answerCall() {
        answered = true;
        if (sipClient != null) {
            sipClient.answerCall();
        }
        dispose();
    }
    
    private void rejectCall() {
        answered = false;
        if (sipClient != null) {
            sipClient.hangUp();
        }
        dispose();
    }
    
    public boolean isAnswered() {
        return answered;
    }
}