package simrskhanza;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;

/**
 * Toast (top-right) with:
 * - Left large bell icon
 * - Title "Pemberitahuan" (bold) on first line
 * - Message on second line (supports plain text or HTML; \n -> <br> for plain)
 * - "Lihat Konsul" button at bottom-right
 * - Thin progress bar (8px) at bottom
 * - Flat (no shadow), blue theme
 * - WAV sound loading from /suara/notifikasi.wav or relative fallback
 */
public final class Toast {
    private Toast() {}

    // Single instance to avoid stacking
    private static JWindow win;
    private static javax.swing.Timer progressTimer;
    private static long endsAtMs;

    // Positioning
    private static final int RIGHT_MARGIN_PX = 10;
    private static final int TOP_MARGIN_PX   = 100; // ~3 cm

    // Colors (flat blue)
    private static final Color BLUE_BG   = new Color(33, 150, 243);
    private static final Color BLUE_DARK = new Color(25, 118, 210);
    private static final Color WHITE     = Color.WHITE;
    
    // helper: pilih font pertama yang tersedia
    private static Font uiFont(int style, float size) {
        String[] families = { "Segoe UI", "Inter", "Roboto", "Noto Sans", "Ubuntu", "Tahoma", "SansSerif" };
        for (String f : families) {
            Font test = new Font(f, style, Math.round(size));
            if (test.getFamily().equals(f) || !"Dialog".equals(test.getFamily())) {
                // asumsi tersedia; sebagian JRE map ke family yang benar
                return test.deriveFont(style, size);
            }
        }
        return UIManager.getFont("Label.font").deriveFont(style, size);
    }

    // rekomendasi ukuran:
    private static final Font TITLE_FONT = uiFont(Font.BOLD,   16f);
    private static final Font MSG_FONT   = uiFont(Font.PLAIN,  13.5f);
    private static final Font BTN_FONT   = uiFont(Font.BOLD,   12.5f);

    // Public APIs
    public static void show(Window owner, String msg, int millis, Runnable onClick) {
        showInternal(owner, "Pemberitahuan", msg, millis <= 0 ? 12000 : millis, onClick, "notifikasi.wav");
    }
    public static void showInfo(Window owner, String msg, int millis, Runnable onClick, String wavName) {
        showInternal(owner, "Pemberitahuan", msg, millis <= 0 ? 12000 : millis, onClick, wavName);
    }
    public static void showWithTitle(Window owner, String title, String msg, int millis, Runnable onClick, String wavName) {
        showInternal(owner, title, msg, millis <= 0 ? 12000 : millis, onClick, wavName);
    }

    private static void showInternal(Window owner, String title, String msg, int millis, Runnable onClick, String wavName) {
        SwingUtilities.invokeLater(() -> buildAndShow(owner, title, msg, millis, onClick, wavName));
    }

    private static void buildAndShow(Window owner, String title, String msg, int millis, Runnable onClick, String wavName) {
        // dispose previous
        if (progressTimer != null) { progressTimer.stop(); progressTimer = null; }
        if (win != null) { win.dispose(); win = null; }

        win = new JWindow(owner);
        win.setAlwaysOnTop(true);
        win.setBackground(new Color(0,0,0,0));

        // Root
        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);

        // Card (no shadow)
        JPanel card = new JPanel(new BorderLayout(12, 8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(BLUE_BG);
                g2.fillRoundRect(0, 0, w, h-8, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(12, 16, 8, 16));
        root.add(card, BorderLayout.CENTER);

        // LEFT: big bell icon (separated block)
        JComponent bell = new BellIcon(64);
        JPanel leftWrap = new JPanel(new GridBagLayout());
        leftWrap.setOpaque(false);
        leftWrap.add(bell); // center vertically
        leftWrap.setBorder(new EmptyBorder(0, 2, 0, 2));
        card.add(leftWrap, BorderLayout.WEST);

        // CENTER: text area (title + message)
        JPanel textArea = new JPanel();
        textArea.setOpaque(false);
        textArea.setLayout(new BoxLayout(textArea, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(title != null ? title : "Pemberitahuan");
        lblTitle.setForeground(WHITE);
        lblTitle.setFont(TITLE_FONT);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblMsg = new JLabel(toHtml(msg));
        lblMsg.setForeground(WHITE);
        lblMsg.setFont(MSG_FONT);
        lblMsg.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblMsg.setBorder(new EmptyBorder(2,0,0,0));

        textArea.add(lblTitle);
        textArea.add(lblMsg);

        card.add(textArea, BorderLayout.CENTER);

        // RIGHT: close button
        JLabel close = new JLabel("✕");
        close.setForeground(new Color(255,255,255,210));
        close.setFont(new Font("SansSerif", Font.BOLD, 14));
        close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        close.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { closeNow(); }
            @Override public void mouseEntered(MouseEvent e) { close.setForeground(WHITE); }
            @Override public void mouseExited(MouseEvent e)  { close.setForeground(new Color(255,255,255,210)); }
        });
        JPanel right = new JPanel(new BorderLayout());
        right.setOpaque(false);
        right.add(close, BorderLayout.NORTH);
        card.add(right, BorderLayout.EAST);

        // BOTTOM: actions (right aligned)
        JButton btn = new JButton("Lihat Dokumen");
        styleOutlineButton(btn);
        btn.setFont(BTN_FONT);
        if (onClick != null) {
            btn.addActionListener(e -> { onClick.run(); closeNow(); });
        } else {
            btn.setVisible(false);
        }
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        actions.setOpaque(false);
        actions.add(btn);
        card.add(actions, BorderLayout.SOUTH);

        // Progress strip (~2mm ≈ 8px)
        ProgressStrip strip = new ProgressStrip(8);
        strip.setOpaque(false);
        root.add(strip, BorderLayout.SOUTH);

        win.setContentPane(root);

        // click empty area to trigger onClick as well
        if (onClick != null) {
            MouseAdapter go = new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { onClick.run(); closeNow(); }
            };
            textArea.addMouseListener(go);
            lblMsg.addMouseListener(go);
        }

        win.pack();

        // Limit width to avoid over-stretch; message wrapper already has max-width
        Dimension pref = win.getPreferredSize();
        int maxW = 420;
        if (pref.width > maxW) {
            win.setSize(new Dimension(maxW, pref.height));
        }

        // position: top-right
        Rectangle b = owner != null
                ? owner.getGraphicsConfiguration().getBounds()
                : GraphicsEnvironment.getLocalGraphicsEnvironment()
                   .getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        int x = b.x + b.width - win.getWidth() - RIGHT_MARGIN_PX;
        int y = b.y + TOP_MARGIN_PX;
        win.setLocation(x, y);

        // start progress
        endsAtMs = System.currentTimeMillis() + Math.max(1000, millis);
        progressTimer = new javax.swing.Timer(30, e -> {
            long now = System.currentTimeMillis();
            float pct = 1f - Math.max(0, endsAtMs - now) / (float) Math.max(1, millis);
            strip.setProgress(Math.min(1f, Math.max(0f, pct)));
            if (now >= endsAtMs) closeNow();
        });
        progressTimer.start();

        win.setVisible(true);

        // play sound
        playSoundAsync(wavName);
    }

    private static void closeNow() {
        if (progressTimer != null) { progressTimer.stop(); progressTimer = null; }
        if (win != null) { win.setVisible(false); win.dispose(); win = null; }
    }

    // Convert plain text to HTML (supports \n). If string already starts with <html, keep as-is.
    private static String toHtml(String s){
        if (s == null) return "<html></html>";
        String trim = s.trim().toLowerCase();
        if (trim.startsWith("<html")) return s; // already HTML
        s = s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
        s = s.replace("\n","<br/>");
        return "<html><div style='max-width:360px;'>" + s + "</div></html>";
    }

    private static void styleOutlineButton(JButton b) {
        b.setForeground(WHITE);
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255,255,255,200), 1, true),
                new EmptyBorder(6,12,6,12)
        ));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                b.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(WHITE, 1, true),
                        new EmptyBorder(6,12,6,12)
                ));
            }
            @Override public void mouseExited(MouseEvent e) {
                b.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(255,255,255,200), 1, true),
                        new EmptyBorder(6,12,6,12)
                ));
            }
        });
    }

/** HiDPI bell icon: cari @3x/@2x/normal/notification; kalau tak ketemu → vector */
private static final class BellIcon extends JComponent {
    private final int size;
    private final Image img;

    BellIcon(int size) {
        this.size = size;
        setPreferredSize(new Dimension(size + 6, size + 6));
        setOpaque(false);
        this.img = loadLargest();
    }

    private static Image loadLargest() {
        String[] candidates = {
            "/picture/bell-toast.png",   // rekomendasi: 64px atau 96px
            "/picture/bell@3x.png",
            "/picture/bell@2x.png",
            "/picture/bell.png",
            "/picture/notifikasi_64.png"
        };
        Image best = null;
        int bestW = -1;
        for (String path : candidates) {
            try {
                java.net.URL u = Toast.class.getResource(path);
                if (u != null) {
                    ImageIcon ic = new ImageIcon(u);
                    int w = ic.getIconWidth();
                    if (w > bestW) { bestW = w; best = ic.getImage(); }
                }
            } catch (Exception ignore) {}
        }
        if (bestW > 0) System.out.println("[TOAST] bell image selected width=" + bestW + "px");
        return best;
    }

    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (img != null) {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                 RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.drawImage(img, 0, 0, size, size, null);
        } else {
            // fallback vector — selalu crisp, tapi bentuk sederhana
            g2.setColor(new Color(255,255,255,230));
            int w = size, h = size, x = 0, y = 0;
            g2.fillRoundRect(x + w/6, y + h/4, (int)(w*0.66), (int)(h*0.55), w/3, w/3);
            g2.fillOval(x + w/2 - w/12, y + (int)(h*0.72), w/6, w/6);
            g2.fillOval(x + w/2 - w/8,  y + h/6 - 2,         w/4, w/6);
        }
        g2.dispose();
    }
}

    /** thin progress strip at the bottom of the toast */
    private static final class ProgressStrip extends JComponent {
        private float progress = 0f; // 0..1
        private final int thickness;
        ProgressStrip(int thickness){ this.thickness = Math.max(6, thickness); }
        void setProgress(float p){ this.progress = p; repaint(); }
        @Override public Dimension getPreferredSize() { return new Dimension(320, thickness); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setColor(new Color(255,255,255,90));
            g2.fillRoundRect(8, 1, w-16, h-2, 8, 8);
            g2.setColor(WHITE);
            int filled = Math.max(0, Math.min(w-16, Math.round((w-16) * progress)));
            g2.fillRoundRect(8, 1, filled, h-2, 8, 8);
            g2.dispose();
        }
    }

    // ---- SOUND ---- //
    private static void playSoundAsync(String name) {
        new Thread(() -> {
            try (AudioInputStream ais = openSound(name)) {
                if (ais == null) { System.out.println("Sound not found: " + name); return; }
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
            } catch (Exception ex) {
                System.out.println("Toast sound error: " + ex.getMessage());
            }
        }, "ToastSound").start();
    }

    private static AudioInputStream openSound(String fileName) {
        // kandidat nama (handle variasi)
        String[] names = new String[]{ fileName, "notifikasi.wav", "notify.wav", "notification.wav" };

        // 1) classpath: /suara/<name>
        for (String nm : names) {
            try {
                URL u = Toast.class.getResource("/suara/" + nm);
                if (u != null) {
                    System.out.println("[TOAST] sound from CLASSPATH: " + u);
                    return AudioSystem.getAudioInputStream(u);
                }
            } catch (Exception ignore) {}
        }

        // 2) relative: suara/<name> or Khanza/suara/<name>
        String wd = System.getProperty("user.dir");
        File[] dirs = new File[]{
            new File("suara"),
            new File("Khanza/suara"),
            new File(new File(wd).getParentFile(), "suara")
        };
        for (File dir : dirs) {
            for (String nm : names) {
                try {
                    File f = new File(dir, nm);
                    if (f.exists()) {
                        System.out.println("[TOAST] sound from FILE: " + f.getAbsolutePath());
                        return AudioSystem.getAudioInputStream(f);
                    }
                } catch (Exception ignore) {}
            }
        }
        System.out.println("[TOAST] Sound not found in classpath or relative paths. wd=" + wd);
        return null;
    }
}
