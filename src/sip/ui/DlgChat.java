package sip.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import sip.Kontak;
import sip.KontakService;
import sip.chat.rc.RcConversation;
import sip.chat.rc.RcMessage;
import sip.chat.rc.RocketChatClient;
import sip.chat.rc.RocketChatService;

/**
 * Jendela chat 1-on-1 antar extension, backend Rocket.Chat.
 * Polling 5s saat DlgChat terbuka (active) + 20s saat background.
 */
public class DlgChat extends JDialog {
    private static final SimpleDateFormat TIME_FMT = new SimpleDateFormat("HH:mm", new Locale("id"));
    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("id"));

    private static final Color COLOR_ACCENT     = new Color(0x2563EB);
    private static final Color COLOR_BUBBLE_ME  = new Color(0xDCF8C6);
    private static final Color COLOR_BUBBLE_THEM= new Color(0xF1F5F9);
    private static final Color COLOR_TEXT_SUB   = new Color(0x6B7280);
    private static final Color COLOR_STATUS_BG  = new Color(0xF8FAFC);

    private final RocketChatClient  rcClient;
    private final RocketChatService rcService;
    private final KontakService     kontakService;
    private final String            myExt;

    private final ScheduledExecutorService poller = Executors.newScheduledThreadPool(2, r -> {
        Thread t = new Thread(r, "rc-chat-poll"); t.setDaemon(true); return t;
    });
    private ScheduledFuture<?> activePoll;
    private ScheduledFuture<?> convPoll;
    private String currentPeer;
    private String currentRoomId;

    private final DefaultListModel<RcConversation> convModel = new DefaultListModel<>();
    private final JList<RcConversation> convList = new JList<>(convModel);

    private final DefaultListModel<RcMessage> msgModel = new DefaultListModel<>();
    private final JList<RcMessage> msgList = new JList<>(msgModel);

    private final JLabel      lblHeader    = new JLabel(" ");
    private final JLabel      lblHeaderSub = new JLabel(" ");
    private final JLabel      lblStatus    = new JLabel("Belum login ke Rocket.Chat");
    private final JTextArea   txtInput     = new JTextArea();
    private final JTextField  txtNoRawat   = new JTextField(14);

    public DlgChat(JFrame parent, RocketChatClient rcClient,
                   RocketChatService rcService, KontakService kontakService) {
        super(parent, "Chat", false);
        this.rcClient      = rcClient;
        this.rcService     = rcService;
        this.kontakService = kontakService;
        this.myExt         = rcClient.getUsername();

        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setSize(new Dimension(900, 640));
        setLocationRelativeTo(parent);

        buildUI();
        reloadConversations();
        startConvPoll();   // auto-refresh conversation list selama dialog visible

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e)   {
                stopActivePoll();
                stopConvPoll();
            }
            @Override public void windowIconified(java.awt.event.WindowEvent e) {
                stopActivePoll();
                stopConvPoll();
            }
            @Override public void windowDeiconified(java.awt.event.WindowEvent e) {
                if (currentPeer != null) startActivePoll();
                startConvPoll();
            }
        });
        // setVisible() juga jadi trigger start/stop poll
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentShown(java.awt.event.ComponentEvent e) {
                startConvPoll();
                if (currentPeer != null) startActivePoll();
            }
            @Override public void componentHidden(java.awt.event.ComponentEvent e) {
                stopActivePoll();
                stopConvPoll();
            }
        });
    }

    // ===== UI =====

    private void buildUI() {
        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildLeft(), buildRight());
        sp.setBorder(null);
        sp.setDividerLocation(260);
        sp.setDividerSize(6);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sp, BorderLayout.CENTER);
        getContentPane().add(buildStatus(), BorderLayout.SOUTH);
    }

    private JComponent buildLeft() {
        convList.setCellRenderer(new ConvRenderer());
        convList.setFixedCellHeight(62);
        convList.setBackground(Color.WHITE);
        convList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            RcConversation c = convList.getSelectedValue();
            if (c != null) openConversation(c.peerUsername, c.roomId);
        });

        JPanel top = new JPanel(new BorderLayout(6, 0));
        top.setBorder(new EmptyBorder(10, 12, 8, 12));
        JLabel title = new JLabel("Percakapan");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 13f));
        top.add(title, BorderLayout.WEST);

        JButton btnNew = new JButton("+ Chat baru");
        btnNew.setFocusPainted(false);
        btnNew.addActionListener(e -> startNewChat());
        top.add(btnNew, BorderLayout.EAST);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(Color.WHITE);
        wrap.add(top, BorderLayout.NORTH);
        wrap.add(new JScrollPane(convList), BorderLayout.CENTER);
        return wrap;
    }

    private JComponent buildRight() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(Color.WHITE);
        wrap.add(buildHeader(),   BorderLayout.NORTH);
        wrap.add(buildMessages(), BorderLayout.CENTER);
        wrap.add(buildComposer(), BorderLayout.SOUTH);
        return wrap;
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(12, 14, 10, 14));
        p.setBackground(COLOR_STATUS_BG);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHeaderSub.setFont(lblHeaderSub.getFont().deriveFont(11f));
        lblHeaderSub.setForeground(COLOR_TEXT_SUB);
        p.add(lblHeader);
        p.add(Box.createVerticalStrut(2));
        p.add(lblHeaderSub);
        return p;
    }

    private JComponent buildMessages() {
        msgList.setCellRenderer(new BubbleRenderer());
        msgList.setBackground(Color.WHITE);
        JScrollPane sp = new JScrollPane(msgList);
        sp.setBorder(null);
        return sp;
    }

    private JPanel buildComposer() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(8, 10, 10, 10));
        p.setBackground(Color.WHITE);

        JPanel meta = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        meta.setBackground(Color.WHITE);
        meta.add(new JLabel("🏥 No. Rawat:"));
        txtNoRawat.setToolTipText("Opsional — attach no_rawat pasien ke pesan");
        meta.add(txtNoRawat);
        p.add(meta);

        txtInput.setLineWrap(true);
        txtInput.setWrapStyleWord(true);
        txtInput.setRows(3);
        txtInput.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE5E7EB), 1),
                new EmptyBorder(6, 8, 6, 8)));
        txtInput.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume();
                    doSend();
                }
            }
        });
        JScrollPane inputScroll = new JScrollPane(txtInput);
        inputScroll.setPreferredSize(new Dimension(0, 70));
        inputScroll.setBorder(null);

        JButton btnEmoji = new JButton("😀");
        btnEmoji.setFont(DlgEmojiPicker.EMOJI_FONT.deriveFont(16f));
        btnEmoji.setToolTipText("Sisipkan emoji");
        btnEmoji.setFocusPainted(false);
        btnEmoji.setPreferredSize(new Dimension(42, 36));
        btnEmoji.addActionListener(e -> DlgEmojiPicker.show(this, emoji -> {
            int pos = txtInput.getCaretPosition();
            txtInput.insert(emoji, pos);
            txtInput.requestFocus();
            txtInput.setCaretPosition(pos + emoji.length());
        }));

        JButton btnSend = new JButton("Kirim");
        btnSend.setBackground(COLOR_ACCENT);
        btnSend.setForeground(Color.WHITE);
        btnSend.setFocusPainted(false);
        btnSend.setPreferredSize(new Dimension(80, 36));
        btnSend.addActionListener(e -> doSend());

        // Text input harus pakai font teks biasa — jangan EMOJI_FONT karena
        // Noto Color Emoji di Linux emoji-only, tidak ada glyph Latin.
        txtInput.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));

        JPanel rightBtns = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 4, 0));
        rightBtns.setBackground(Color.WHITE);
        rightBtns.add(btnEmoji);
        rightBtns.add(btnSend);

        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(new EmptyBorder(6, 0, 0, 0));
        row.add(inputScroll, BorderLayout.CENTER);
        row.add(rightBtns, BorderLayout.EAST);
        p.add(row);
        return p;
    }

    private JPanel buildStatus() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COLOR_STATUS_BG);
        p.setBorder(new EmptyBorder(6, 12, 6, 12));
        lblStatus.setForeground(COLOR_TEXT_SUB);
        p.add(lblStatus, BorderLayout.WEST);
        return p;
    }

    // ===== actions =====

    private void doSend() {
        if (currentPeer == null) {
            JOptionPane.showMessageDialog(this,
                    "Pilih percakapan dulu (kiri) atau klik '+ Chat baru'.",
                    "Belum ada penerima", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        final String body = txtInput.getText().trim();
        final String nr   = txtNoRawat.getText().trim();
        if (body.isEmpty() && nr.isEmpty()) return;

        txtInput.setText("");
        new Thread(() -> {
            boolean ok = rcService.sendDirect(currentPeer, body, nr.isEmpty() ? null : nr);
            SwingUtilities.invokeLater(() -> {
                if (ok) {
                    refreshCurrentMessages();
                    reloadConversations();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal kirim pesan.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    txtInput.setText(body);
                }
            });
        }, "rc-send").start();
    }

    private void startNewChat() {
        List<Kontak> list = kontakService.list(null);
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Belum ada kontak. Sync dulu dari Telepon → Sync.",
                    "Kontak kosong", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // Exclude diri sendiri
        list.removeIf(k -> k.extension.equals(myExt));
        Kontak[] arr = list.toArray(new Kontak[0]);
        if (arr.length == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada kontak lain untuk diajak chat.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Kontak selected = (Kontak) JOptionPane.showInputDialog(this,
                "Pilih lawan bicara:",
                "Chat Baru", JOptionPane.PLAIN_MESSAGE, null, arr, arr[0]);
        if (selected != null) openConversation(selected.extension, null);
    }

    /** Public API: buka conversation dengan extension tertentu (dipanggil dari frmUtama tray). */
    public void openConversationPublic(String peerExt) {
        if (peerExt == null || peerExt.isEmpty()) return;
        SwingUtilities.invokeLater(() -> {
            // Pilih di list kalau ada, supaya selected state match
            for (int i = 0; i < convModel.size(); i++) {
                if (peerExt.equals(convModel.get(i).peerUsername)) {
                    convList.setSelectedIndex(i);
                    break;
                }
            }
            openConversation(peerExt, null);
        });
    }

    private void openConversation(String peerExt, String roomId) {
        if (peerExt == null) return;
        currentPeer = peerExt;
        currentRoomId = roomId;

        Kontak k = kontakService.find(peerExt);
        String nama = (k != null && k.nama != null && !k.nama.isEmpty()) ? k.nama : peerExt;
        String sub  = "ext " + peerExt + (k != null && k.unit != null && !k.unit.isEmpty() ? "  ·  " + k.unit : "");
        lblHeader.setText(nama);
        lblHeaderSub.setText(sub);

        refreshCurrentMessages();
        startActivePoll();
    }

    private void refreshCurrentMessages() {
        if (currentPeer == null) return;
        final String peer = currentPeer;
        new Thread(() -> {
            List<RcMessage> msgs = rcService.listMessages(peer, 200);
            if (msgs == null) return;   // rate-limit / error — jangan clear bubble existing
            if (currentRoomId == null && !msgs.isEmpty()) currentRoomId = msgs.get(0).roomId;
            SwingUtilities.invokeLater(() -> {
                if (!peer.equals(currentPeer)) return;
                msgModel.clear();
                for (RcMessage m : msgs) msgModel.addElement(m);
                scrollToBottom();
                if (currentRoomId != null) rcService.markRead(currentRoomId);
            });
        }, "rc-msg-load").start();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            int n = msgModel.size();
            if (n > 0) msgList.ensureIndexIsVisible(n - 1);
        });
    }

    private void reloadConversations() {
        new Thread(() -> {
            List<RcConversation> list = rcService.listConversations();
            for (RcConversation c : list) {
                Kontak k = kontakService.find(c.peerUsername);
                if (k != null && k.nama != null) c.peerDisplay = k.nama;
            }
            SwingUtilities.invokeLater(() -> {
                convModel.clear();
                for (RcConversation c : list) convModel.addElement(c);
                updateStatus();
            });
        }, "rc-conv-load").start();
    }

    private void updateStatus() {
        int total = 0;
        for (int i = 0; i < convModel.size(); i++) total += convModel.get(i).unread;
        String base = rcClient.isConnected() ? "● Terhubung RC (" + myExt + ")" : "○ RC belum login";
        lblStatus.setText(base + (total > 0 ? " · " + total + " unread" : ""));
        lblStatus.setForeground(rcClient.isConnected() ? new Color(0x10B981) : COLOR_TEXT_SUB);
    }

    // ===== Polling =====

    private void startActivePoll() {
        stopActivePoll();
        if (currentPeer == null) return;
        final String peer = currentPeer;
        activePoll = poller.scheduleWithFixedDelay(() -> {
            try {
                if (!peer.equals(currentPeer)) return;
                List<RcMessage> msgs = rcService.listMessages(peer, 50);
                if (msgs == null) return;   // rate-limit — skip iteration ini
                if (currentRoomId == null && !msgs.isEmpty()) currentRoomId = msgs.get(0).roomId;
                SwingUtilities.invokeLater(() -> {
                    if (!peer.equals(currentPeer)) return;
                    boolean changed = msgs.size() != msgModel.size();
                    if (!changed && !msgs.isEmpty()) {
                        String lastNewId = msgs.get(msgs.size() - 1).id;
                        String lastOldId = msgModel.get(msgModel.size() - 1).id;
                        changed = !lastNewId.equals(lastOldId);
                    }
                    if (changed) {
                        msgModel.clear();
                        for (RcMessage m : msgs) msgModel.addElement(m);
                        scrollToBottom();
                        if (currentRoomId != null) rcService.markRead(currentRoomId);
                        reloadConversations();
                    }
                });
            } catch (Exception ignored) {}
        }, 8, 8, TimeUnit.SECONDS);   // 5s → 8s, kurangi request
    }

    private void stopActivePoll() {
        if (activePoll != null) { activePoll.cancel(false); activePoll = null; }
    }

    /**
     * Background poll untuk refresh conversation list (kiri) selama DlgChat visible.
     * Interval 12 detik — setengah dari active poll biar unread badge update relatif responsif
     * tanpa bikin load berlebihan.
     */
    private void startConvPoll() {
        if (convPoll != null && !convPoll.isCancelled()) return;
        convPoll = poller.scheduleWithFixedDelay(() -> {
            try { reloadConversations(); } catch (Exception ignored) {}
        }, 12, 12, TimeUnit.SECONDS);
    }

    private void stopConvPoll() {
        if (convPoll != null) { convPoll.cancel(false); convPoll = null; }
    }

    public void shutdown() {
        stopActivePoll();
        stopConvPoll();
        try { poller.shutdownNow(); } catch (Exception ignored) {}
    }

    // ===== Renderers =====

    private class ConvRenderer extends JPanel implements ListCellRenderer<RcConversation> {
        private final AvatarLabel avatar = new AvatarLabel("?", 40);
        private final JLabel lblName = new JLabel();
        private final JLabel lblLast = new JLabel();
        private final JLabel lblTime = new JLabel();
        private final JLabel lblBadge = new JLabel();

        ConvRenderer() {
            setLayout(new BorderLayout(8, 0));
            setBorder(new EmptyBorder(6, 10, 6, 10));
            JPanel text = new JPanel();
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            text.setOpaque(false);
            lblName.setFont(lblName.getFont().deriveFont(Font.BOLD, 13f));
            lblLast.setFont(lblLast.getFont().deriveFont(11f));
            lblLast.setForeground(COLOR_TEXT_SUB);
            text.add(lblName);
            text.add(lblLast);

            JPanel right = new JPanel();
            right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
            right.setOpaque(false);
            lblTime.setFont(lblTime.getFont().deriveFont(10f));
            lblTime.setForeground(COLOR_TEXT_SUB);
            lblTime.setAlignmentX(Component.RIGHT_ALIGNMENT);
            lblBadge.setOpaque(true);
            lblBadge.setBackground(new Color(0xDC2626));
            lblBadge.setForeground(Color.WHITE);
            lblBadge.setFont(lblBadge.getFont().deriveFont(Font.BOLD, 10f));
            lblBadge.setHorizontalAlignment(JLabel.CENTER);
            lblBadge.setBorder(new EmptyBorder(1, 6, 1, 6));
            lblBadge.setAlignmentX(Component.RIGHT_ALIGNMENT);
            right.add(lblTime);
            right.add(Box.createVerticalStrut(4));
            right.add(lblBadge);

            add(avatar, BorderLayout.WEST);
            add(text,   BorderLayout.CENTER);
            add(right,  BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends RcConversation> list,
                                                      RcConversation c,
                                                      int index, boolean selected, boolean focus) {
            String nama = c.peerDisplay != null ? c.peerDisplay : c.peerUsername;
            avatar.setName(nama);
            lblName.setText(nama);
            String last = c.lastBody == null ? "" : c.lastBody;
            if (last.length() > 40) last = last.substring(0, 40) + "…";
            lblLast.setText(last.isEmpty() ? "ext " + c.peerUsername : last);
            lblTime.setText(c.lastTgl == null ? "" : TIME_FMT.format(c.lastTgl));
            if (c.unread > 0) { lblBadge.setText(String.valueOf(c.unread)); lblBadge.setVisible(true); }
            else              { lblBadge.setVisible(false); }
            setBackground(selected ? new Color(0xE0E7FF) : Color.WHITE);
            setOpaque(true);
            return this;
        }
    }

    private class BubbleRenderer extends JPanel implements ListCellRenderer<RcMessage> {
        @Override
        public Component getListCellRendererComponent(JList<? extends RcMessage> list,
                                                      RcMessage m,
                                                      int index, boolean selected, boolean focus) {
            removeAll();
            boolean mine = m.fromMe(myExt);
            setLayout(new FlowLayout(mine ? FlowLayout.RIGHT : FlowLayout.LEFT, 6, 4));
            setBackground(Color.WHITE);
            setOpaque(true);

            JPanel bubble = new JPanel();
            bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
            bubble.setBackground(mine ? COLOR_BUBBLE_ME : COLOR_BUBBLE_THEM);
            bubble.setBorder(new EmptyBorder(8, 12, 8, 12));

            if (m.noRawat != null && !m.noRawat.isEmpty()) {
                JLabel nr = new JLabel("🏥 " + m.noRawat);
                nr.setFont(nr.getFont().deriveFont(Font.BOLD, 10f));
                nr.setForeground(COLOR_ACCENT);
                bubble.add(nr);
                bubble.add(Box.createVerticalStrut(4));
            }

            if (m.body != null && !m.body.isEmpty()) {
                JTextArea t = new JTextArea(m.body);
                t.setEditable(false);
                t.setLineWrap(true);
                t.setWrapStyleWord(true);
                t.setOpaque(false);
                t.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
                t.setBorder(null);
                t.setColumns(30);
                bubble.add(t);
            }

            JLabel ts = new JLabel(m.tglKirim == null ? "" : DATE_FMT.format(m.tglKirim));
            ts.setFont(ts.getFont().deriveFont(9f));
            ts.setForeground(COLOR_TEXT_SUB);
            ts.setAlignmentX(Component.RIGHT_ALIGNMENT);
            bubble.add(Box.createVerticalStrut(4));
            bubble.add(ts);

            add(bubble);
            return this;
        }
    }
}
