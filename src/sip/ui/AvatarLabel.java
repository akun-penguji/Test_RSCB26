package sip.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;

/**
 * Lingkaran kecil berisi inisial nama, warna otomatis dari hash nama.
 * Dipakai di list kontak dan popup incoming call.
 */
public class AvatarLabel extends JComponent {

    private static final Color[] PALETTE = {
        new Color(0x3B82F6), new Color(0x10B981), new Color(0xF59E0B),
        new Color(0xEF4444), new Color(0x8B5CF6), new Color(0xEC4899),
        new Color(0x06B6D4), new Color(0x84CC16), new Color(0xF97316),
        new Color(0x6366F1)
    };

    private String name = "";
    private int    diameter;

    public AvatarLabel(String name, int diameter) {
        this.diameter = diameter;
        setName(name);
        setPreferredSize(new Dimension(diameter, diameter));
        setMinimumSize(new Dimension(diameter, diameter));
    }

    @Override
    public void setName(String n) {
        this.name = n == null ? "" : n;
        repaint();
    }

    public void setDiameter(int d) {
        this.diameter = d;
        setPreferredSize(new Dimension(d, d));
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int d = Math.min(diameter, Math.min(w, h));
        int x = (w - d) / 2, y = (h - d) / 2;

        Color bg = colorFor(name);
        g2.setColor(bg);
        g2.fillOval(x, y, d, d);

        String initial = initialOf(name);
        g2.setColor(Color.WHITE);
        int fontSize = (int) (d * 0.45);
        g2.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        FontMetrics fm = g2.getFontMetrics();
        int tw = fm.stringWidth(initial);
        int tx = x + (d - tw) / 2;
        int ty = y + (d - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(initial, tx, ty);

        g2.dispose();
    }

    private static String initialOf(String name) {
        if (name == null || name.trim().isEmpty()) return "?";
        String trimmed = name.trim();
        char c = Character.toUpperCase(trimmed.charAt(0));
        return Character.isLetterOrDigit(c) ? String.valueOf(c) : "?";
    }

    private static Color colorFor(String name) {
        if (name == null || name.isEmpty()) return PALETTE[0];
        int h = 0;
        for (int i = 0; i < name.length(); i++) h = 31 * h + name.charAt(i);
        return PALETTE[Math.floorMod(h, PALETTE.length)];
    }
}
