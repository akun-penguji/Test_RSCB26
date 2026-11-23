/*
 * widget.StatusLokalis — overlay penanda status lokalis (bulatan hitam) di atas
 * gambar tubuh. Titik disimpan sebagai FRAKSI (0..1) relatif ukuran komponen,
 * sehingga konsisten di resolusi/ukuran berbeda maupun saat dicetak.
 * Pakai: klik-kiri = tambah titik, klik-kanan = hapus titik terdekat.
 * getData()/setData() untuk simpan/muat; renderPng() untuk cetak & riwayat.
 */
package widget;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class StatusLokalis extends JComponent {

    private final List<float[]> pts = new ArrayList<>(); // {fx, fy} fraksi 0..1
    private Image base;          // opsional; bila null hanya menggambar titik (overlay)
    private int radius = 6;
    private boolean editable = true;

    public StatusLokalis() {
        setOpaque(false);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!editable) {
                    return;
                }
                if (SwingUtilities.isRightMouseButton(e)) {
                    int idx = nearest(e.getX(), e.getY());
                    if (idx >= 0) {
                        pts.remove(idx);
                        repaint();
                    }
                } else {
                    float fx = e.getX() / (float) Math.max(1, getWidth());
                    float fy = e.getY() / (float) Math.max(1, getHeight());
                    pts.add(new float[]{fx, fy});
                    repaint();
                }
            }
        });
    }

    private int nearest(int x, int y) {
        int best = -1;
        double bd = 12 * 12;
        for (int i = 0; i < pts.size(); i++) {
            int px = (int) (pts.get(i)[0] * getWidth());
            int py = (int) (pts.get(i)[1] * getHeight());
            double d = (px - x) * (px - x) + (py - y) * (py - y);
            if (d < bd) {
                bd = d;
                best = i;
            }
        }
        return best;
    }

    public void setBaseImage(Image img) {
        this.base = img;
        repaint();
    }

    public void setEditable(boolean e) {
        this.editable = e;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (base != null) {
            g.drawImage(base, 0, 0, getWidth(), getHeight(), this);
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.BLACK);
        g2.setStroke(new java.awt.BasicStroke(2f));
        for (float[] p : pts) {
            int x = (int) (p[0] * getWidth());
            int y = (int) (p[1] * getHeight());
            g2.drawOval(x - radius, y - radius, radius * 2, radius * 2);
        }
        g2.dispose();
    }

    public String getData() {
        StringBuilder sb = new StringBuilder();
        for (float[] p : pts) {
            if (sb.length() > 0) {
                sb.append(";");
            }
            sb.append(round(p[0])).append(",").append(round(p[1]));
        }
        return sb.toString();
    }

    private String round(float f) {
        return String.valueOf(Math.round(f * 10000) / 10000.0);
    }

    public void setData(String s) {
        pts.clear();
        if (s != null && !s.trim().isEmpty()) {
            for (String pair : s.split(";")) {
                String[] xy = pair.split(",");
                if (xy.length == 2) {
                    try {
                        pts.add(new float[]{Float.parseFloat(xy[0].trim()), Float.parseFloat(xy[1].trim())});
                    } catch (Exception ex) {
                        // abaikan format rusak
                    }
                }
            }
        }
        repaint();
    }

    public void clearPoints() {
        pts.clear();
        repaint();
    }

    // Render (base image di-stretch ke w x h) + titik ke file PNG. Kembalikan path.
    public static String renderPng(String baseResource, String data, int w, int h) {
        try {
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, w, h);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            try {
                Image base = new ImageIcon(StatusLokalis.class.getResource(baseResource)).getImage();
                g.drawImage(base, 0, 0, w, h, null);
            } catch (Exception exImg) {
                // base tidak ada -> latar putih saja
            }
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.BLACK);
            g.setStroke(new java.awt.BasicStroke(2f));
            if (data != null && !data.trim().isEmpty()) {
                for (String pair : data.split(";")) {
                    String[] xy = pair.split(",");
                    if (xy.length == 2) {
                        try {
                            int x = (int) (Float.parseFloat(xy[0].trim()) * w);
                            int y = (int) (Float.parseFloat(xy[1].trim()) * h);
                            g.drawOval(x - 6, y - 6, 12, 12);
                        } catch (Exception ex) {
                            // abaikan
                        }
                    }
                }
            }
            g.dispose();
            File dir = new File("./tmpPDF");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File out = new File(dir, "lokalis_" + System.nanoTime() + ".png");
            ImageIO.write(img, "png", out);
            return out.getAbsolutePath();
        } catch (Exception e) {
            System.out.println("StatusLokalis.renderPng: " + e);
            return "";
        }
    }
}
