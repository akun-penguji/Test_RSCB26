package bridging;

import javax.swing.*;
import java.awt.*;
import java.awt.Dialog.ModalityType;

/**
 * A simple utility to display progress logs in a non-blocking dialog.
 */
public class ProcessLog extends JDialog {
    private JTextArea textArea;
    private JProgressBar progressBar;

    public ProcessLog(Window parent, String title) {
        super(parent, title, ModalityType.MODELESS);
        initComponents();
    }

    private void initComponents() {
        textArea = new JTextArea(15, 50);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);

        setLayout(new BorderLayout(10, 10));
        add(progressBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dispose());
        btnPanel.add(btnClose);
        add(btnPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getOwner());
    }

    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            textArea.append(message + "\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
            System.out.println("[ProcessLog] " + message);
        });
    }

    public void setIndeterminate(boolean indeterminate) {
        SwingUtilities.invokeLater(() -> progressBar.setIndeterminate(indeterminate));
    }

    public void setProgress(int value) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setIndeterminate(false);
            progressBar.setValue(value);
        });
    }
}
