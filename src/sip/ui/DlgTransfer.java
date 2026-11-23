package sip.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import sip.SipClient;

/** Input nomor tujuan blind transfer. */
public class DlgTransfer extends JDialog {
    public DlgTransfer(JDialog parent, SipClient sipClient) {
        super(parent, "Transfer Panggilan", true);
        setSize(new Dimension(340, 180));
        setLocationRelativeTo(parent);

        JLabel header = new JLabel("Transfer ke extension", SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 14f));
        header.setBorder(new EmptyBorder(12, 10, 6, 10));

        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.BOLD, 18));
        txt.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel mid = new JPanel(new BorderLayout());
        mid.setBorder(new EmptyBorder(4, 24, 10, 24));
        mid.add(txt, BorderLayout.CENTER);

        JButton ok = new JButton("Transfer");
        ok.setBackground(new Color(0x2563EB));
        ok.setForeground(Color.WHITE);
        ok.setFocusPainted(false);
        ok.addActionListener(e -> {
            String t = txt.getText().trim();
            if (!t.isEmpty()) {
                sipClient.blindTransfer(t);
                dispose();
            }
        });

        JButton cancel = new JButton("Batal");
        cancel.addActionListener(e -> dispose());

        JPanel bot = new JPanel(new GridLayout(1, 2, 6, 0));
        bot.setBorder(new EmptyBorder(0, 24, 12, 24));
        bot.add(cancel);
        bot.add(ok);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(mid, BorderLayout.CENTER);
        getContentPane().add(bot, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(ok);
    }
}
