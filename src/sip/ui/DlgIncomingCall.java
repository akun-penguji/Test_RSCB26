package sip.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import sip.Kontak;
import sip.KontakService;
import sip.SipClient;
import sip.SipClientListener;
import sip.SipState;
import sip.SoundPlayer;

/** Popup always-on-top saat panggilan masuk — avatar besar + Answer/End. */
public class DlgIncomingCall extends JDialog implements SipClientListener {
    private static final Color COLOR_SUCCESS = new Color(0x10B981);
    private static final Color COLOR_DANGER  = new Color(0xDC2626);
    private static final Color COLOR_SUB     = new Color(0x6B7280);

    private final SipClient sipClient;
    private final KontakService kontakService;
    private final SoundPlayer ringSound = new SoundPlayer();

    private final AvatarLabel avatar  = new AvatarLabel("?", 84);
    private final JLabel lblCaller    = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel lblSub       = new JLabel(" ", SwingConstants.CENTER);
    private final JLabel lblIncoming  = new JLabel("Panggilan Masuk", SwingConstants.CENTER);

    private Timer pulseTimer;
    private int pulseStep = 0;

    public DlgIncomingCall(JFrame parent, SipClient sipClient, KontakService kontakService) {
        super(parent, "Panggilan Masuk", false);
        this.sipClient = sipClient;
        this.kontakService = kontakService;

        setAlwaysOnTop(true);
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setUndecorated(false);
        setSize(new Dimension(380, 320));
        setLocationRelativeTo(parent);

        buildUI();
        sipClient.addListener(this);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(new EmptyBorder(14, 18, 14, 18));

        JPanel top = new JPanel();
        top.setLayout(new javax.swing.BoxLayout(top, javax.swing.BoxLayout.Y_AXIS));
        top.setOpaque(false);

        lblIncoming.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        lblIncoming.setFont(lblIncoming.getFont().deriveFont(12f));
        lblIncoming.setForeground(COLOR_SUB);
        top.add(lblIncoming);
        top.add(javax.swing.Box.createVerticalStrut(8));

        JPanel avatarWrap = new JPanel();
        avatarWrap.setOpaque(false);
        avatarWrap.add(avatar);
        avatarWrap.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        top.add(avatarWrap);
        top.add(javax.swing.Box.createVerticalStrut(10));

        lblCaller.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        lblCaller.setFont(new Font("Segoe UI", Font.BOLD, 18));
        top.add(lblCaller);

        lblSub.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        lblSub.setFont(lblSub.getFont().deriveFont(12f));
        lblSub.setForeground(COLOR_SUB);
        top.add(lblSub);

        root.add(top, BorderLayout.CENTER);

        JButton btnAnswer = bigButton("Angkat", COLOR_SUCCESS);
        btnAnswer.addActionListener(e -> {
            sipClient.answer();
            setVisible(false);
        });

        JButton btnReject = bigButton("Tolak", COLOR_DANGER);
        btnReject.addActionListener(e -> {
            sipClient.reject();
            setVisible(false);
        });

        JPanel btns = new JPanel(new GridLayout(1, 2, 10, 0));
        btns.setOpaque(false);
        btns.setBorder(new EmptyBorder(12, 0, 0, 0));
        btns.add(btnAnswer);
        btns.add(btnReject);
        root.add(btns, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private static JButton bigButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setPreferredSize(new Dimension(0, 50));
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        b.putClientProperty("JButton.buttonType", "roundRect");
        return b;
    }

    @Override
    public void onIncomingCall(String remoteExtension, String displayName) {
        String nama = remoteExtension;
        String unit = "";
        Kontak k = kontakService.find(remoteExtension);
        if (k != null) {
            if (k.nama != null && !k.nama.isEmpty()) nama = k.nama;
            if (k.unit != null && !k.unit.isEmpty()) unit = k.unit;
        }
        final String callerName = nama;
        final String sub = "Ext. " + remoteExtension
                + (unit.isEmpty() ? "" : "  ·  " + unit);
        SwingUtilities.invokeLater(() -> {
            avatar.setName(callerName);
            lblCaller.setText(callerName);
            lblSub.setText(sub);
            if (!isVisible()) setVisible(true);
            toFront();
            startPulse();
            ringSound.play(SoundPlayer.RINGING);
        });
    }

    @Override
    public void onCallAnswered(String remoteExtension) {
        SwingUtilities.invokeLater(this::dismiss);
    }

    @Override
    public void onCallEnded(String remoteExtension, CallEndReason reason) {
        SwingUtilities.invokeLater(this::dismiss);
    }

    @Override
    public void onStateChanged(SipState newState, String message) {
        if (newState == SipState.OFFLINE || newState == SipState.STOPPING) {
            SwingUtilities.invokeLater(this::dismiss);
        }
    }

    private void dismiss() {
        stopPulse();
        ringSound.stop();
        setVisible(false);
    }

    private void startPulse() {
        stopPulse();
        pulseStep = 0;
        pulseTimer = new Timer(450, e -> {
            pulseStep++;
            lblIncoming.setText(pulseStep % 2 == 0 ? "Panggilan Masuk" : "Panggilan Masuk ·");
        });
        pulseTimer.start();
    }

    private void stopPulse() {
        if (pulseTimer != null) {
            pulseTimer.stop();
            pulseTimer = null;
        }
    }
}
