package sip.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractAction;
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
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import sip.CallLogEntry;
import sip.CallLogService;
import sip.FreePbxSyncService;
import sip.Kontak;
import sip.KontakService;
import sip.SipClient;
import sip.SipClientListener;
import sip.SipState;
import sip.SoundPlayer;

public class DlgPhone extends JDialog implements SipClientListener {
    private static final SimpleDateFormat TS_FMT = new SimpleDateFormat("dd MMM HH:mm", new Locale("id"));

    private static final Color COLOR_ACCENT    = new Color(0x2563EB);
    private static final Color COLOR_SUCCESS   = new Color(0x10B981);
    private static final Color COLOR_DANGER    = new Color(0xDC2626);
    private static final Color COLOR_WARNING   = new Color(0xF59E0B);
    private static final Color COLOR_TEXT_SUB  = new Color(0x6B7280);
    private static final Color COLOR_BG_SOFT   = new Color(0xF8FAFC);
    private static final Color COLOR_STATUS_BG = new Color(0xF1F5F9);

    private final SipClient sipClient;
    private final KontakService kontakService;
    private final CallLogService callLogService;
    private final SoundPlayer dialingSound = new SoundPlayer();

    private final JTextField txtCari = new JTextField();
    private final JTextField txtDial = new JTextField();
    private final DefaultListModel<Kontak> kontakModel = new DefaultListModel<>();
    private final JList<Kontak> kontakList = new JList<>(kontakModel);
    private final JPanel favPanel = new JPanel();

    private final DefaultTableModel logModel = new DefaultTableModel(
            new Object[]{"", "Waktu", "Ext", "Nama", "Status", "Durasi"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tbLog = new JTable(logModel);

    private final JLabel lblStatus = new JLabel(" ");
    private final JLabel lblTimer  = new JLabel(" ");
    private final JButton btnCall   = bigButton("Panggil", COLOR_SUCCESS);
    private final JButton btnHangup = bigButton("Tutup",   COLOR_DANGER);
    private final JButton btnMute   = new JButton("Mute");
    private final JButton btnHold   = new JButton("Hold");
    private final JButton btnXfer   = new JButton("Transfer");

    private volatile boolean inCall = false;
    private Timer callTimer;
    private long callStartMs;

    public DlgPhone(JFrame parent, SipClient sipClient,
                    KontakService kontakService, CallLogService callLogService) {
        super(parent, "Telepon", false);
        this.sipClient = sipClient;
        this.kontakService = kontakService;
        this.callLogService = callLogService;

        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        setSize(new Dimension(760, 620));
        setLocationRelativeTo(parent);

        buildUI();
        bindKeyboard();
        sipClient.addListener(this);
        onStateChanged(sipClient.getState(), null);
        reloadKontak();
    }

    // ===== UI =====

    private void buildUI() {
        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        getContentPane().add(buildTop(),    BorderLayout.NORTH);
        getContentPane().add(buildCenter(), BorderLayout.CENTER);
        getContentPane().add(buildStatus(), BorderLayout.SOUTH);
    }

    private JPanel buildTop() {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setBorder(new EmptyBorder(10, 12, 8, 12));
        p.setBackground(Color.WHITE);

        JLabel icon = new JLabel("  🔍 ");
        txtCari.putClientProperty("JTextField.placeholderText", "Cari kontak — nama, extension, atau unit...");
        txtCari.setPreferredSize(new Dimension(0, 34));
        txtCari.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { reloadKontak(); }
            @Override public void removeUpdate(DocumentEvent e)  { reloadKontak(); }
            @Override public void changedUpdate(DocumentEvent e) { reloadKontak(); }
        });

        JPanel searchWrap = new JPanel(new BorderLayout());
        searchWrap.setBackground(Color.WHITE);
        searchWrap.add(icon, BorderLayout.WEST);
        searchWrap.add(txtCari, BorderLayout.CENTER);

        JButton btnAdd = new JButton("+ Kontak");
        btnAdd.setToolTipText("Tambah kontak manual");
        btnAdd.setPreferredSize(new Dimension(90, 34));
        btnAdd.addActionListener(e -> openEditKontak(null));

        JButton btnSync = new JButton("Sync");
        btnSync.setPreferredSize(new Dimension(76, 34));
        btnSync.setToolTipText("Sync kontak dari FreePBX REST API");
        btnSync.addActionListener(e -> doSyncFreePbx(btnSync));

        JButton btnSetting = new JButton("⚙");
        btnSetting.setToolTipText("Setting telepon (butuh login admin)");
        btnSetting.setPreferredSize(new Dimension(40, 34));
        btnSetting.addActionListener(e -> {
            if (!requireAdminAccess()) return;
            new DlgSettingSip(this).setVisible(true);
        });

        JPanel rightBtns = new JPanel(new GridLayout(1, 3, 4, 0));
        rightBtns.setBackground(Color.WHITE);
        rightBtns.add(btnAdd);
        rightBtns.add(btnSync);
        rightBtns.add(btnSetting);

        p.add(searchWrap, BorderLayout.CENTER);
        p.add(rightBtns, BorderLayout.EAST);
        return p;
    }

    private JComponent buildCenter() {
        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildLeft(), buildRight());
        sp.setBorder(null);
        sp.setDividerSize(6);
        sp.setContinuousLayout(true);
        sp.setResizeWeight(0.6);
        sp.setDividerLocation(450);
        return sp;
    }

    private JComponent buildLeft() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setBackground(Color.WHITE);
        wrap.setBorder(new EmptyBorder(0, 8, 0, 4));

        // Favorites bar di atas
        favPanel.setLayout(new BoxLayout(favPanel, BoxLayout.X_AXIS));
        favPanel.setBackground(Color.WHITE);
        favPanel.setBorder(new EmptyBorder(4, 6, 8, 6));
        JScrollPane favScroll = new JScrollPane(favPanel,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        favScroll.setBorder(null);
        favScroll.setPreferredSize(new Dimension(0, 78));
        wrap.add(favScroll, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Kontak",  buildKontakPane());
        tabs.addTab("Riwayat", buildRiwayatPane());
        tabs.addChangeListener(ev -> { if (tabs.getSelectedIndex() == 1) reloadCallLog(); });
        wrap.add(tabs, BorderLayout.CENTER);
        return wrap;
    }

    private JComponent buildKontakPane() {
        kontakList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        kontakList.setCellRenderer(new KontakCardRenderer());
        kontakList.setFixedCellHeight(56);
        kontakList.setBackground(Color.WHITE);
        kontakList.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e)  { maybeMenu(e); }
            @Override public void mouseReleased(MouseEvent e) { maybeMenu(e); }
            @Override public void mouseClicked(MouseEvent e) {
                if (e.isPopupTrigger()) return;
                int idx = kontakList.locationToIndex(e.getPoint());
                if (idx < 0) return;
                kontakList.setSelectedIndex(idx);
                Kontak k = kontakModel.getElementAt(idx);
                txtDial.setText(k.extension);
                if (e.getClickCount() == 2) doCall();
            }
            private void maybeMenu(MouseEvent e) {
                if (!e.isPopupTrigger()) return;
                int idx = kontakList.locationToIndex(e.getPoint());
                if (idx < 0) return;
                kontakList.setSelectedIndex(idx);
                showKontakContextMenu(e, kontakModel.getElementAt(idx));
            }
        });

        JScrollPane sp = new JScrollPane(kontakList);
        sp.setBorder(BorderFactory.createLineBorder(new Color(0xE5E7EB), 1));
        return sp;
    }

    private void showKontakContextMenu(MouseEvent e, Kontak k) {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem call = new JMenuItem("Panggil  " + k.extension);
        call.addActionListener(ev -> { txtDial.setText(k.extension); doCall(); });
        JMenuItem edit = new JMenuItem("Edit kontak");
        edit.addActionListener(ev -> openEditKontak(k));
        JMenuItem fav  = new JMenuItem(k.favorit ? "Hapus dari favorit" : "Tandai favorit");
        fav.addActionListener(ev -> {
            k.favorit = !k.favorit;
            kontakService.save(k);
            reloadKontak();
        });
        JMenuItem del = new JMenuItem("Hapus");
        del.addActionListener(ev -> {
            int r = JOptionPane.showConfirmDialog(this,
                    "Hapus kontak " + k.extension + " (" + k.nama + ")?",
                    "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION && kontakService.delete(k.extension)) reloadKontak();
        });
        menu.add(call);
        menu.addSeparator();
        menu.add(fav);
        menu.add(edit);
        menu.add(del);
        menu.show(e.getComponent(), e.getX(), e.getY());
    }

    private void openEditKontak(Kontak k) {
        DlgEditKontak d = new DlgEditKontak(this, kontakService, k);
        d.setVisible(true);
        if (d.isSaved()) reloadKontak();
    }

    private JComponent buildRiwayatPane() {
        tbLog.setRowHeight(32);
        tbLog.setFillsViewportHeight(true);
        tbLog.setShowGrid(false);
        tbLog.setIntercellSpacing(new Dimension(0, 0));
        JTableHeader h = tbLog.getTableHeader();
        h.setFont(h.getFont().deriveFont(Font.BOLD));

        tbLog.getColumnModel().getColumn(0).setMaxWidth(36);
        tbLog.getColumnModel().getColumn(0).setMinWidth(36);
        tbLog.getColumnModel().getColumn(1).setMaxWidth(110);
        tbLog.getColumnModel().getColumn(2).setMaxWidth(60);
        tbLog.getColumnModel().getColumn(5).setMaxWidth(70);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                String st = String.valueOf(t.getValueAt(row, 4));
                if (!sel) {
                    if ("missed".equals(st))      c.setForeground(COLOR_DANGER);
                    else if ("failed".equals(st)) c.setForeground(COLOR_DANGER);
                    else                          c.setForeground(Color.BLACK);
                }
                return c;
            }
        };
        tbLog.getColumnModel().getColumn(0).setCellRenderer(center);
        tbLog.getColumnModel().getColumn(1).setCellRenderer(center);
        tbLog.getColumnModel().getColumn(2).setCellRenderer(center);
        tbLog.getColumnModel().getColumn(5).setCellRenderer(center);

        tbLog.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tbLog.getSelectedRow();
                    if (row < 0) return;
                    String ext = String.valueOf(logModel.getValueAt(row, 2));
                    if (ext != null && !ext.isEmpty()) {
                        txtDial.setText(ext);
                        doCall();
                    }
                }
            }
        });

        JScrollPane sp = new JScrollPane(tbLog);
        sp.setBorder(BorderFactory.createLineBorder(new Color(0xE5E7EB), 1));
        return sp;
    }

    private JPanel buildRight() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(6, 6, 8, 10));
        p.setBackground(Color.WHITE);

        JLabel cap = new JLabel("Nomor");
        cap.setForeground(COLOR_TEXT_SUB);
        cap.setFont(cap.getFont().deriveFont(11f));
        cap.setAlignmentX(Component.LEFT_ALIGNMENT);
        cap.setBorder(new EmptyBorder(0, 4, 2, 0));
        p.add(cap);

        txtDial.setFont(new Font("Segoe UI", Font.BOLD, 26));
        txtDial.setHorizontalAlignment(SwingConstants.CENTER);
        txtDial.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        txtDial.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtDial.setBackground(COLOR_BG_SOFT);
        p.add(txtDial);

        p.add(Box.createVerticalStrut(10));
        p.add(buildDialpad());
        p.add(Box.createVerticalStrut(8));
        p.add(buildCallControls());
        p.add(Box.createVerticalStrut(6));
        p.add(buildInCallControls());

        return p;
    }

    private JPanel buildDialpad() {
        JPanel pad = new JPanel(new GridLayout(4, 3, 6, 6));
        pad.setAlignmentX(Component.LEFT_ALIGNMENT);
        pad.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));
        pad.setBackground(Color.WHITE);
        String[] keys = {"1","2","3","4","5","6","7","8","9","*","0","#"};
        String[] subs = {"", "ABC","DEF","GHI","JKL","MNO","PQRS","TUV","WXYZ","","+",""};
        for (int i = 0; i < keys.length; i++) pad.add(dialKey(keys[i], subs[i]));
        return pad;
    }

    private JButton dialKey(String digit, String sub) {
        JButton b = new JButton("<html><div style='text-align:center;'>"
                + "<span style='font-size:18pt;'>" + digit + "</span>"
                + (sub.isEmpty() ? "" : "<br><span style='font-size:8pt;color:#6B7280;'>" + sub + "</span>")
                + "</div></html>");
        b.setFocusPainted(false);
        b.setBackground(Color.WHITE);
        b.setBorder(BorderFactory.createLineBorder(new Color(0xE5E7EB), 1, true));
        b.setPreferredSize(new Dimension(72, 60));
        b.addActionListener(e -> {
            if (inCall) {
                sipClient.sendDtmf(digit.charAt(0));
            } else {
                txtDial.setText(txtDial.getText() + digit);
                txtDial.requestFocus();
            }
        });
        return b;
    }

    private JPanel buildCallControls() {
        JPanel row = new JPanel(new GridLayout(1, 2, 6, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        row.setBackground(Color.WHITE);
        btnCall.addActionListener(e -> doCall());
        btnHangup.addActionListener(e -> doHangup());
        row.add(btnCall);
        row.add(btnHangup);
        return row;
    }

    private JPanel buildInCallControls() {
        JPanel row = new JPanel(new GridLayout(1, 3, 6, 0));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        row.setBackground(Color.WHITE);
        styleSmallBtn(btnMute);  btnMute.addActionListener(e -> toggleMute());
        styleSmallBtn(btnHold);  btnHold.addActionListener(e -> toggleHold());
        styleSmallBtn(btnXfer);  btnXfer.addActionListener(e -> {
            if (!inCall) return;
            new DlgTransfer(this, sipClient).setVisible(true);
        });
        setInCallControlsEnabled(false);
        row.add(btnMute);
        row.add(btnHold);
        row.add(btnXfer);
        return row;
    }

    private static void styleSmallBtn(JButton b) {
        b.setFocusPainted(false);
        b.setFont(b.getFont().deriveFont(11f));
    }

    private void setInCallControlsEnabled(boolean on) {
        btnMute.setEnabled(on);
        btnHold.setEnabled(on);
        btnXfer.setEnabled(on);
        if (!on) {
            btnMute.setText("Mute");
            btnHold.setText("Hold");
        }
    }

    private void toggleMute() {
        boolean now = sipClient.toggleMute();
        btnMute.setText(now ? "Unmute" : "Mute");
        btnMute.setBackground(now ? COLOR_WARNING : null);
        btnMute.setForeground(now ? Color.WHITE : null);
    }

    private void toggleHold() {
        boolean now = sipClient.toggleHold();
        btnHold.setText(now ? "Resume" : "Hold");
        btnHold.setBackground(now ? COLOR_WARNING : null);
        btnHold.setForeground(now ? Color.WHITE : null);
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

    private JPanel buildStatus() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(COLOR_STATUS_BG);
        p.setBorder(new EmptyBorder(6, 12, 6, 12));
        lblStatus.setForeground(COLOR_TEXT_SUB);
        lblTimer.setForeground(COLOR_TEXT_SUB);
        lblTimer.setFont(new Font("Monospaced", Font.BOLD, 12));
        p.add(lblStatus, BorderLayout.WEST);
        p.add(lblTimer,  BorderLayout.EAST);
        return p;
    }

    // ===== keyboard =====

    private void bindKeyboard() {
        txtDial.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doCall();
            }
        });
        kontakList.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Kontak k = kontakList.getSelectedValue();
                    if (k != null) { txtDial.setText(k.extension); doCall(); }
                }
            }
        });
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "hangup");
        getRootPane().getActionMap().put("hangup", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { doHangup(); }
        });
    }

    // ===== actions =====

    private void doCall() {
        String ext = txtDial.getText().trim();
        if (ext.isEmpty()) {
            Kontak k = kontakList.getSelectedValue();
            if (k != null) { ext = k.extension; txtDial.setText(ext); }
            else return;
        }
        if (sipClient.getState() != SipState.REGISTERED) {
            JOptionPane.showMessageDialog(this,
                    "SIP belum terdaftar ke FreePBX. Cek status bar dan database.xml.",
                    "Tidak bisa panggil", JOptionPane.WARNING_MESSAGE);
            return;
        }
        sipClient.call(ext);
    }

    private void doHangup() {
        sipClient.hangup();
        dialingSound.stop();
    }

    private void reloadKontak() {
        String kw = txtCari.getText();
        List<Kontak> list = kontakService.list(kw);
        SwingUtilities.invokeLater(() -> {
            kontakModel.clear();
            for (Kontak k : list) kontakModel.addElement(k);
            refreshFavBar(list);
        });
    }

    private void refreshFavBar(List<Kontak> list) {
        favPanel.removeAll();
        boolean any = false;
        for (Kontak k : list) {
            if (!k.favorit) continue;
            any = true;
            favPanel.add(buildFavTile(k));
            favPanel.add(Box.createHorizontalStrut(6));
        }
        if (!any) {
            JLabel hint = new JLabel("Belum ada favorit — klik ★ di kontak untuk quick-dial");
            hint.setForeground(COLOR_TEXT_SUB);
            hint.setFont(hint.getFont().deriveFont(11f));
            favPanel.add(hint);
        }
        favPanel.revalidate();
        favPanel.repaint();
    }

    private JComponent buildFavTile(Kontak k) {
        AvatarLabel av = new AvatarLabel(k.nama, 44);
        av.setToolTipText(k.nama + " (" + k.extension + ")");
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBackground(Color.WHITE);
        col.setBorder(new EmptyBorder(0, 0, 0, 0));
        av.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel cap = new JLabel(k.extension, SwingConstants.CENTER);
        cap.setAlignmentX(Component.CENTER_ALIGNMENT);
        cap.setFont(cap.getFont().deriveFont(10f));
        cap.setForeground(COLOR_TEXT_SUB);
        col.add(av);
        col.add(cap);
        col.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                txtDial.setText(k.extension);
                if (e.getClickCount() == 2) doCall();
            }
        });
        av.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                txtDial.setText(k.extension);
                if (e.getClickCount() == 2) doCall();
            }
        });
        return col;
    }

    private void reloadCallLog() {
        String localExt = sipClient.getConfig().getNum();
        List<CallLogEntry> list = callLogService.recent(localExt, 100);
        SwingUtilities.invokeLater(() -> {
            logModel.setRowCount(0);
            for (CallLogEntry e : list) {
                logModel.addRow(new Object[]{
                        iconFor(e.arah, e.status),
                        e.tglMulai == null ? "" : TS_FMT.format(e.tglMulai),
                        e.extensionRemote,
                        e.namaRemote == null ? "" : e.namaRemote,
                        e.status,
                        formatDur(e.durasiDetik)
                });
            }
        });
    }

    private static String iconFor(String arah, String status) {
        if ("missed".equals(status))    return "✖";
        if ("failed".equals(status))    return "✗";
        if ("rejected".equals(status))  return "⊘";
        if ("in".equals(arah))          return "↙";
        if ("out".equals(arah))         return "↗";
        return "•";
    }

    private static String formatDur(int sec) {
        if (sec <= 0) return "-";
        int m = sec / 60, s = sec % 60;
        return String.format("%d:%02d", m, s);
    }

    private void doSyncFreePbx(JButton btn) {
        btn.setEnabled(false);
        btn.setText("...");
        lblStatus.setText("Sync FreePBX...");
        new Thread(() -> {
            int n = -1;
            try {
                n = new FreePbxSyncService(sipClient.getConfig(), kontakService).sync();
            } catch (Exception ignored) {}
            final int count = n;
            SwingUtilities.invokeLater(() -> {
                btn.setEnabled(true);
                btn.setText("Sync");
                if (count < 0) {
                    JOptionPane.showMessageDialog(this,
                            "Sync gagal.\nCek SIP_REST_URL / CLIENT_ID / CLIENT_SECRET di database.xml\ndan dokumentasi di docs/freepbx-rest-api.md",
                            "Sync Error", JOptionPane.ERROR_MESSAGE);
                    lblStatus.setText("Sync gagal");
                } else {
                    lblStatus.setText("Sync OK — " + count + " kontak diperbarui");
                    reloadKontak();
                }
            });
        }, "freepbx-sync").start();
    }

    // public API for frmUtama
    public void focusDialInput() {
        txtDial.requestFocus();
        txtDial.selectAll();
    }

    /**
     * Pastikan user sudah login dan punya hak admin sebelum akses setting.
     * Return true kalau boleh lanjut, false kalau harus dibatalkan.
     */
    private boolean requireAdminAccess() {
        String kode = fungsi.akses.getkode();
        if (kode == null || kode.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Silakan login terlebih dahulu untuk membuka setting telepon.",
                    "Login Diperlukan", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!fungsi.akses.getadmin()) {
            JOptionPane.showMessageDialog(this,
                    "User \"" + kode + "\" tidak memiliki hak akses admin.\n"
                  + "Setting telepon hanya bisa diubah oleh admin.\n"
                  + "Hubungi administrator sistem untuk bantuan.",
                    "Akses Ditolak", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    // ===== SipClientListener =====

    @Override
    public void onStateChanged(SipState newState, String message) {
        SwingUtilities.invokeLater(() -> {
            String s;
            Color c = COLOR_TEXT_SUB;
            switch (newState) {
                case REGISTERED: s = "● Online  ·  " + sipClient.getConfig().getNum(); c = COLOR_SUCCESS; break;
                case CONNECTING: s = "● Menghubungkan...";        c = COLOR_WARNING; break;
                case FAILED:     s = "● Error: " + (message == null ? "" : message); c = COLOR_DANGER; break;
                case DISABLED:   s = "○ SIP Nonaktif"; break;
                default:         s = "○ Offline"; break;
            }
            lblStatus.setText(s);
            lblStatus.setForeground(c);
        });
    }

    @Override
    public void onOutgoingCall(String remoteExtension) {
        SwingUtilities.invokeLater(() -> {
            inCall = true;
            setInCallControlsEnabled(true);
            lblStatus.setText("Memanggil " + remoteExtension + "...");
            lblStatus.setForeground(COLOR_WARNING);
            dialingSound.play(SoundPlayer.DIALING);
        });
    }

    @Override
    public void onOutgoingRinging(String remoteExtension) {
        SwingUtilities.invokeLater(() -> lblStatus.setText("Berdering di " + remoteExtension));
    }

    @Override
    public void onCallAnswered(String remoteExtension) {
        SwingUtilities.invokeLater(() -> {
            inCall = true;
            setInCallControlsEnabled(true);
            dialingSound.stop();
            lblStatus.setText("Terhubung: " + remoteExtension);
            lblStatus.setForeground(COLOR_SUCCESS);
            startTimer();
        });
    }

    @Override
    public void onCallEnded(String remoteExtension, CallEndReason reason) {
        SwingUtilities.invokeLater(() -> {
            inCall = false;
            setInCallControlsEnabled(false);
            dialingSound.stop();
            stopTimer();
            lblStatus.setText("Panggilan selesai (" + translateReason(reason) + ")");
            lblStatus.setForeground(COLOR_TEXT_SUB);
        });
    }

    private static String translateReason(CallEndReason r) {
        switch (r) {
            case ANSWERED:  return "dijawab";
            case MISSED:    return "tak terjawab";
            case REJECTED:  return "ditolak";
            case FAILED:    return "gagal";
            case CANCELLED: return "diputus";
            default: return String.valueOf(r);
        }
    }

    private void startTimer() {
        callStartMs = System.currentTimeMillis();
        if (callTimer != null) callTimer.stop();
        callTimer = new Timer(500, new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                long sec = (System.currentTimeMillis() - callStartMs) / 1000;
                lblTimer.setText(formatDur((int) sec));
            }
        });
        callTimer.start();
    }

    private void stopTimer() {
        if (callTimer != null) { callTimer.stop(); callTimer = null; }
        lblTimer.setText(" ");
    }

    // ===== Kontak card renderer =====

    private static class KontakCardRenderer extends JPanel implements ListCellRenderer<Kontak> {
        private final AvatarLabel avatar = new AvatarLabel("?", 40);
        private final JLabel lblNama = new JLabel();
        private final JLabel lblSub  = new JLabel();
        private final JLabel lblFav  = new JLabel();
        private final JPanel text    = new JPanel();

        KontakCardRenderer() {
            setLayout(new BorderLayout(10, 0));
            setBorder(new EmptyBorder(6, 10, 6, 10));
            text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
            text.setOpaque(false);
            lblNama.setFont(lblNama.getFont().deriveFont(Font.BOLD, 13f));
            lblSub.setFont(lblSub.getFont().deriveFont(11f));
            lblSub.setForeground(COLOR_TEXT_SUB);
            text.add(lblNama);
            text.add(lblSub);
            lblFav.setFont(lblFav.getFont().deriveFont(16f));
            lblFav.setForeground(new Color(0xF59E0B));
            add(avatar, BorderLayout.WEST);
            add(text,   BorderLayout.CENTER);
            add(lblFav, BorderLayout.EAST);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Kontak> list, Kontak k,
                                                      int index, boolean selected, boolean focus) {
            avatar.setName(k.nama);
            lblNama.setText(k.nama == null ? "" : k.nama);
            String sub = "ext " + k.extension;
            if (k.unit != null && !k.unit.isEmpty())    sub += "  ·  " + k.unit;
            if (k.lokasi != null && !k.lokasi.isEmpty()) sub += "  ·  " + k.lokasi;
            lblSub.setText(sub);
            lblFav.setText(k.favorit ? "★" : "");
            setBackground(selected ? new Color(0xE0E7FF) : Color.WHITE);
            setOpaque(true);
            return this;
        }
    }
}
