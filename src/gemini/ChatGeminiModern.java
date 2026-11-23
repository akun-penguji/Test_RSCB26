/*
 * Modern ChatGemini UI - Java Swing Upgrade
 * By Mas Elkhanza - Enhanced Version
 */
package gemini;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import bridging.GeminiService;

public final class ChatGeminiModern extends javax.swing.JDialog {

    private String reply = "";
    private GeminiService geminiService;
    private Timer typingTimer;
    private boolean isTyping = false;

    // Modern Color Scheme
    private static final Color PRIMARY_BLUE = new Color(66, 133, 244);
    private static final Color PRIMARY_GREEN = new Color(52, 168, 83);
    private static final Color PRIMARY_RED = new Color(234, 67, 53);
    private static final Color BACKGROUND_GRAY = new Color(248, 249, 250);
    private static final Color MESSAGE_USER = new Color(66, 133, 244);
    private static final Color MESSAGE_BOT = new Color(255, 255, 255);
    private static final Color BORDER_LIGHT = new Color(224, 224, 224);
    private static final Color TEXT_DARK = new Color(32, 33, 36);
    private static final Color TEXT_LIGHT = new Color(95, 99, 104);

    // Component declarations
    private JPanel mainPanel;
    private JPanel headerPanel;
    private JPanel inputPanel;
    private JScrollPane chatScrollPane;
    private JScrollPane inputScrollPane;
    private JTextPane chatArea;
    private JTextArea inputArea;
    private JButton sendButton;
    private JLabel titleLabel;
    private JLabel statusLabel;
    private JLabel statusIndicator;

    public ChatGeminiModern(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initModernComponents();
        setupModernStyling();
        initializeGeminiService();

        setTitle("Gemini Chat - AI Assistant");
        setLocationRelativeTo(parent);
        addWelcomeMessage();
    }

    private void initModernComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        createHeaderPanel();
        createChatArea();
        createInputArea();
        setupInputAreaBehavior();

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(chatScrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        getContentPane().add(mainPanel);
        setSize(900, 700);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void createHeaderPanel() {
        headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(PRIMARY_BLUE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);

        JLabel logoLabel = new JLabel("✨");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        titleLabel = new JLabel("Gemini Chat");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        statusLabel = new JLabel("AI Assistant Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(255, 255, 255, 180));

        statusIndicator = new JLabel("●");
        statusIndicator.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusIndicator.setForeground(PRIMARY_GREEN);

        titlePanel.add(titleLabel);
        titlePanel.add(statusLabel);

        leftPanel.add(logoLabel);
        leftPanel.add(titlePanel);
        leftPanel.add(Box.createHorizontalStrut(10));
        leftPanel.add(statusIndicator);

        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(new Color(255, 255, 255, 50));
        closeButton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> dispose());

        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setBackground(new Color(255, 255, 255, 100));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setBackground(new Color(255, 255, 255, 50));
            }
        });

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(closeButton, BorderLayout.EAST);
    }

    private void createChatArea() {
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setBackground(BACKGROUND_GRAY);
        chatArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setBorder(null);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        chatScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JScrollBar scrollBar = chatScrollPane.getVerticalScrollBar();
        scrollBar.setUI(new ModernScrollBarUI());
        scrollBar.setPreferredSize(new Dimension(8, 0));
    }

    private void createInputArea() {
        inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_LIGHT),
                BorderFactory.createEmptyBorder(20, 25, 25, 25)
        ));

        JPanel inputContainer = new JPanel(new BorderLayout(15, 0));
        inputContainer.setOpaque(false);

        inputArea = new JTextArea(2, 30);
        inputArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        inputArea.setBackground(BACKGROUND_GRAY);
        inputArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);

        inputScrollPane = new JScrollPane(inputArea);
        inputScrollPane.setBorder(null);
        inputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        inputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        inputScrollPane.setBackground(BACKGROUND_GRAY);
        inputScrollPane.setBorder(new RoundBorder(25));

        sendButton = new JButton("➤");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sendButton.setForeground(Color.WHITE);
        sendButton.setBackground(PRIMARY_BLUE);
        sendButton.setPreferredSize(new Dimension(55, 55));
        sendButton.setBorder(null);
        sendButton.setFocusPainted(false);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.addActionListener(this::sendButtonActionPerformed);

        sendButton.setBorder(new RoundBorder(27));

        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                sendButton.setBackground(PRIMARY_BLUE.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                sendButton.setBackground(PRIMARY_BLUE);
            }
        });

        JLabel hintLabel = new JLabel("💡 Press Enter to send • Ctrl+Enter for new line");
        hintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hintLabel.setForeground(TEXT_LIGHT);
        hintLabel.setBorder(BorderFactory.createEmptyBorder(8, 20, 0, 0));

        inputContainer.add(inputScrollPane, BorderLayout.CENTER);
        inputContainer.add(sendButton, BorderLayout.EAST);

        inputPanel.add(inputContainer, BorderLayout.CENTER);
        inputPanel.add(hintLabel, BorderLayout.SOUTH);
    }

    private void setupInputAreaBehavior() {
        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!e.isShiftDown() && !e.isControlDown()) {
                        e.consume();
                        sendButtonActionPerformed(null);
                    } else if (e.isControlDown()) {
                        inputArea.append("\n");
                    }
                }
            }
        });

        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                adjustInputHeight();
            }
        });

        inputArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                inputArea.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_BLUE, 2),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                inputArea.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_LIGHT, 2),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)
                ));
            }
        });
    }

    private void adjustInputHeight() {
        SwingUtilities.invokeLater(() -> {
            int lines = inputArea.getLineCount();
            int maxLines = 4;
            int minLines = 1;

            lines = Math.max(minLines, Math.min(maxLines, lines));
            int lineHeight = inputArea.getFontMetrics(inputArea.getFont()).getHeight();
            int height = lines * lineHeight + 30;

            inputArea.setRows(lines);
            inputScrollPane.setPreferredSize(new Dimension(
                    inputScrollPane.getPreferredSize().width,
                    height
            ));

            inputPanel.revalidate();
        });
    }

    private void setupModernStyling() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        System.out.println("Modern styling applied (text rendering only)");
    }

    private void initializeGeminiService() {
        try {
            geminiService = new GeminiService();

            if (geminiService.testGeminiConnection()) {
                System.out.println("✅ Gemini service ready");
                statusLabel.setText("AI Assistant Ready");
                statusIndicator.setForeground(PRIMARY_GREEN);
            } else {
                System.out.println("⚠️ Gemini connection failed");
                statusLabel.setText("Connection Failed");
                statusIndicator.setForeground(PRIMARY_RED);
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to initialize Gemini service: " + e.getMessage());
            geminiService = null;
            statusLabel.setText("Service Unavailable");
            statusIndicator.setForeground(PRIMARY_RED);
        }
    }

    private void addWelcomeMessage() {
        SwingUtilities.invokeLater(() -> {
            String welcomeText = "👋 Welcome to Gemini Chat!\n\n"
                    + "I'm your AI assistant, ready to help with any questions or tasks. "
                    + "Start a conversation by typing your message below.\n\n"
                    + "💡 Tip: Press Enter to send, Ctrl+Enter for new line";

            addMessageToChat("Gemini", welcomeText, false);
        });
    }

    private void sendButtonActionPerformed(ActionEvent evt) {
        String message = inputArea.getText().trim();

        if (message.isEmpty()) {
            return;
        }

        addMessageToChat("You", message, true);
        inputArea.setText("");
        showTypingIndicator();
        setInputEnabled(false);

        new Thread(() -> {
            try {
                String response;

                if (message.toLowerCase().contains("exit")) {
                    response = "Goodbye! Chat session ended.";

                    SwingUtilities.invokeLater(() -> {
                        hideTypingIndicator();
                        addMessageToChat("Gemini", response, false);

                        Timer exitTimer = new Timer(2000, e -> dispose());
                        exitTimer.setRepeats(false);
                        exitTimer.start();
                    });
                    return;
                }

                if (geminiService == null) {
                    response = "Sorry, Gemini service is not available. Please check configuration.";
                } else {
                    response = geminiService.generateResponse(message);
                }

                SwingUtilities.invokeLater(() -> {
                    hideTypingIndicator();
                    addMessageToChat("Gemini", response, false);
                    setInputEnabled(true);
                    inputArea.requestFocus();
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    hideTypingIndicator();
                    addMessageToChat("Gemini", "Sorry, an error occurred: " + e.getMessage(), false);
                    setInputEnabled(true);
                    inputArea.requestFocus();
                });
                System.out.println("Error: " + e.getMessage());
            }
        }).start();
    }

    private void addMessageToChat(String sender, String message, boolean isUser) {
        SwingUtilities.invokeLater(() -> {
            try {
                StyledDocument doc = chatArea.getStyledDocument();

                Style senderStyle = chatArea.addStyle("Sender", null);
                Style messageStyle = chatArea.addStyle("Message", null);
                Style timestampStyle = chatArea.addStyle("Timestamp", null);

                if (isUser) {
                    StyleConstants.setForeground(senderStyle, MESSAGE_USER);
                    StyleConstants.setBackground(messageStyle, new Color(66, 133, 244, 20));
                } else {
                    StyleConstants.setForeground(senderStyle, PRIMARY_GREEN);
                    StyleConstants.setBackground(messageStyle, Color.WHITE);
                }

                StyleConstants.setBold(senderStyle, true);
                StyleConstants.setFontSize(senderStyle, 14);
                StyleConstants.setFontSize(messageStyle, 14);
                StyleConstants.setFontSize(timestampStyle, 12);
                StyleConstants.setForeground(timestampStyle, TEXT_LIGHT);

                if (doc.getLength() > 0) {
                    doc.insertString(doc.getLength(), "\n\n", null);
                }

                String avatar = isUser ? "👤 " : "🤖 ";
                doc.insertString(doc.getLength(), avatar + sender + "\n", senderStyle);

                doc.insertString(doc.getLength(), message, messageStyle);

                String timestamp = java.time.LocalTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("HH:mm")
                );
                doc.insertString(doc.getLength(), "\n" + timestamp, timestampStyle);

                chatArea.setCaretPosition(doc.getLength());

            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    private void showTypingIndicator() {
        isTyping = true;
        statusLabel.setText("Gemini is typing...");

        typingTimer = new Timer(500, new ActionListener() {
            private int dots = 0;

            public void actionPerformed(ActionEvent e) {
                if (!isTyping) {
                    ((Timer) e.getSource()).stop();
                    return;
                }
                dots = (dots + 1) % 4;
                String dotString = ".".repeat(dots);
                statusLabel.setText("Gemini is typing" + dotString);
            }
        });
        typingTimer.start();
    }

    private void hideTypingIndicator() {
        isTyping = false;
        if (typingTimer != null) {
            typingTimer.stop();
        }
        statusLabel.setText("AI Assistant Ready");
    }

    private void setInputEnabled(boolean enabled) {
        inputArea.setEnabled(enabled);
        sendButton.setEnabled(enabled);

        if (enabled) {
            sendButton.setBackground(PRIMARY_BLUE);
        } else {
            sendButton.setBackground(Color.GRAY);
        }
    }

    @Override
    public void dispose() {
        if (geminiService != null) {
            geminiService.close();
        }
        super.dispose();
    }

    private static class RoundBorder extends AbstractBorder {

        private final int radius;

        public RoundBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BORDER_LIGHT);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }
    }

    private static class ModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {

        @Override
        protected void configureScrollBarColors() {
            this.thumbColor = new Color(200, 200, 200);
            this.trackColor = Color.WHITE;
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroButton();
        }

        private JButton createZeroButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }
    }
}
