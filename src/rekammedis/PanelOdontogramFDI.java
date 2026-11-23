package rekammedis;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * Panel Odontogram Visual — Standar FDI / ISO 3950
 * Setiap gigi digambar dengan 5 permukaan (cross/diamond pattern standar).
 * Layout: RA atas, RB bawah, sulung di tengah.
 */
public class PanelOdontogramFDI extends JPanel {

    // FDI layout — urutan tampil kiri ke kanan di layar
    static final int[] UPPER = {18,17,16,15,14,13,12,11, 21,22,23,24,25,26,27,28};
    static final int[] LOWER = {48,47,46,45,44,43,42,41, 31,32,33,34,35,36,37,38};
    static final int[] PRI_U = {55,54,53,52,51, 61,62,63,64,65};
    static final int[] PRI_L = {85,84,83,82,81, 71,72,73,74,75};

    static final String[] CODES  = {"N","C","?","F","Cr","Br","X","M","I","RCT"};
    static final String[] LABELS = {
        "Normal","Karies","Suspect Karies","Tambalan/Filling","Crown/Mahkota",
        "Bridge/Pontik","Indikasi Ekstraksi","Missing/Pencabutan","Implant","Root Canal (RCT)"
    };
    static final Color[] COLORS = {
        new Color(255,255,255), // N   - putih
        new Color(255,150,150), // C   - merah
        new Color(255,220,80),  // ?   - kuning
        new Color(120,210,120), // F   - hijau
        new Color(130,185,255), // Cr  - biru muda
        new Color(195,160,255), // Br  - ungu
        new Color(255,175,80),  // X   - oranye
        new Color(180,180,180), // M   - abu-abu
        new Color(100,170,230), // I   - biru tua
        new Color(255,230,100), // RCT - kuning tua
    };

    // Dimensi gigi
    static final int BTN  = 40;  // permanen
    static final int PBTN = 32;  // sulung
    static final int GAP  =  3;  // jarak antar gigi
    static final int X0   = 12;  // margin kiri
    static final int XLBL = 30;  // lebar label RA/RB
    static final int MID  =  8;  // gap midline

    final Map<Integer,String> kondisiMap    = new HashMap<>();
    final Map<Integer,String> keteranganMap = new HashMap<>();
    private String kondisiAktif = "N";
    private JLabel lStatus;
    private JLabel lDMF;
    Runnable onChangeCallback;

    private OdontogramCanvas canvas;

    public PanelOdontogramFDI() {
        setLayout(new BorderLayout(0, 4));
        setBackground(new Color(250,253,250));
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180,200,180)),
            "Odontogram  ·  Standar FDI / ISO 3950  ·  Permenkes No.24 Tahun 2022",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", Font.BOLD, 10), new Color(30,30,130)));
        add(buildToolbar(), BorderLayout.NORTH);
        canvas = new OdontogramCanvas();
        add(canvas, BorderLayout.CENTER);
    }

    public void setOnChangeCallback(Runnable r) { this.onChangeCallback = r; }

    static Color colorFor(String k) {
        for (int i=0;i<CODES.length;i++) if (CODES[i].equals(k)) return COLORS[i];
        return Color.WHITE;
    }
    static String labelFor(String k) {
        for (int i=0;i<CODES.length;i++) if (CODES[i].equals(k)) return LABELS[i];
        return "Normal";
    }

    // ---- Toolbar kondisi ----
    private JPanel buildToolbar() {
        JPanel bar = new JPanel(null);
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(10, 48));

        JLabel lTool = new JLabel("Kondisi Aktif:");
        lTool.setFont(new Font("Tahoma", Font.BOLD, 10));
        lTool.setBounds(X0, 4, 90, 20);
        bar.add(lTool);

        lStatus = new JLabel("  [N] Normal  ");
        lStatus.setFont(new Font("Tahoma", Font.BOLD, 10));
        lStatus.setOpaque(true);
        lStatus.setBackground(Color.WHITE);
        lStatus.setBorder(BorderFactory.createLineBorder(new Color(150,150,150)));

        ButtonGroup bg = new ButtonGroup();
        int tx = X0 + 96, ty = 4;
        for (int i=0; i<CODES.length; i++) {
            final String kode = CODES[i];
            final Color  col  = COLORS[i];
            JToggleButton tb  = new JToggleButton(kode);
            tb.setBackground(col);
            tb.setOpaque(true);
            tb.setFocusPainted(false);
            tb.setFont(new Font("Tahoma", Font.BOLD, 9));
            tb.setMargin(new Insets(0,2,0,2));
            int tw = kode.length()==1 ? 38 : kode.length()==2 ? 46 : 54;
            tb.setBounds(tx, ty, tw, 20);
            tb.setToolTipText(kode+" = "+LABELS[i]);
            tb.addActionListener(ev -> {
                kondisiAktif = kode;
                lStatus.setText("  ["+kode+"] "+labelFor(kode)+"  ");
                lStatus.setBackground(col);
            });
            bg.add(tb);
            if (i==0) tb.setSelected(true);
            bar.add(tb);
            tx += tw + 2;
            if (tx > 670 && i < CODES.length-1) { tx = X0+96; ty += 24; }
        }
        lStatus.setBounds(tx+4, 4, 220, 20);
        bar.add(lStatus);

        // DMF-T
        lDMF = new JLabel("DMF-T :  D=0  M=0  F=0  →  Total = 0");
        lDMF.setFont(new Font("Tahoma", Font.BOLD, 10));
        lDMF.setForeground(new Color(160,0,0));
        lDMF.setBounds(X0, 26, 500, 18);
        bar.add(lDMF);

        return bar;
    }

    // ---- Posisi X ----
    int permX(int idx) {
        int xGap = (idx>=8) ? MID : 0;
        return X0 + XLBL + idx*(BTN+GAP) + xGap;
    }
    int priX(int idx) {
        int half=5;
        int permHalfW = 8*(BTN+GAP);
        int priHalfW  = half*(PBTN+GAP);
        int coff = (permHalfW - priHalfW)/2;
        if (idx<half) return X0+XLBL+coff+idx*(PBTN+GAP);
        return X0+XLBL+permHalfW+MID+coff+(idx-half)*(PBTN+GAP);
    }

    // ---- Inner canvas dengan custom painting ----
    class OdontogramCanvas extends JComponent {
        // Bounds per gigi untuk hit-detection
        private final Map<Integer,Rectangle> toothBounds = new LinkedHashMap<>();

        // Y coordinates for each row
        final int yNumU  = 2;
        final int yUpper = yNumU + 12;
        final int yPriNumU, yPriU, yMid, yPriL, yPriNumL, yLower, yNumL, yLegend;

        OdontogramCanvas() {
            yPriNumU = yUpper + BTN + 4;
            yPriU    = yPriNumU + 11;
            yMid     = yPriU + PBTN + 2;
            yPriL    = yMid + 20;
            yPriNumL = yPriL + PBTN + 2;
            yLower   = yPriNumL + 11;
            yNumL    = yLower + BTN + 2;
            yLegend  = yNumL + 14;

            int totalH = yLegend + 32;
            // Total canvas width: permX(15)+BTN+legendW+margin
            int totalW = permX(15) + BTN + 210;
            setPreferredSize(new Dimension(totalW, totalH));

            // Build tooth bounds
            for (int i=0; i<UPPER.length; i++)
                toothBounds.put(UPPER[i], new Rectangle(permX(i), yUpper, BTN, BTN));
            for (int i=0; i<LOWER.length; i++)
                toothBounds.put(LOWER[i], new Rectangle(permX(i), yLower, BTN, BTN));
            for (int i=0; i<PRI_U.length; i++)
                toothBounds.put(PRI_U[i], new Rectangle(priX(i), yPriU, PBTN, PBTN));
            for (int i=0; i<PRI_L.length; i++)
                toothBounds.put(PRI_L[i], new Rectangle(priX(i), yPriL, PBTN, PBTN));

            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    Integer num = toothAt(e.getX(), e.getY());
                    if (num == null) return;
                    if (SwingUtilities.isRightMouseButton(e)) showContextMenu(e, num);
                    else applyCondition(num, kondisiAktif);
                }
            });
            setToolTipText(""); // enable tooltip
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseMoved(MouseEvent e) {
                    Integer num = toothAt(e.getX(), e.getY());
                    if (num == null) { setToolTipText(null); return; }
                    String k   = kondisiMap.getOrDefault(num, "N");
                    String ket = keteranganMap.getOrDefault(num, "");
                    setToolTipText("<html><b>Gigi "+num+"</b><br>"+k+" = "+labelFor(k)
                        +(ket.isEmpty()?"":"<br>Ket: "+ket)+"</html>");
                }
            });
        }

        private Integer toothAt(int mx, int my) {
            for (Map.Entry<Integer,Rectangle> e : toothBounds.entrySet())
                if (e.getValue().contains(mx, my)) return e.getKey();
            return null;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Font fNum  = new Font("Tahoma", Font.PLAIN, 8);
            Font fLbl  = new Font("Tahoma", Font.BOLD, 9);
            Font fCode = new Font("Tahoma", Font.BOLD, 7);
            Font fLeg  = new Font("Tahoma", Font.PLAIN, 8);

            // ---- Label RA / RB ----
            g2.setFont(new Font("Tahoma", Font.BOLD, 9));
            g2.setColor(new Color(0,0,150));
            drawCenter(g2, "RA", X0, yUpper, XLBL-2, BTN);
            drawCenter(g2, "RB", X0, yLower, XLBL-2, BTN);
            g2.setFont(new Font("Tahoma", Font.PLAIN, 8));
            drawCenter(g2, "RA:", X0, yPriU, XLBL-2, PBTN);
            drawCenter(g2, "RB:", X0, yPriL, XLBL-2, PBTN);

            // ---- Nomor gigi RA atas ----
            g2.setFont(fNum);
            g2.setColor(new Color(60,60,60));
            for (int i=0;i<UPPER.length;i++) {
                drawCenter(g2, String.valueOf(UPPER[i]), permX(i), yNumU, BTN, 10);
            }
            // ---- Baris UPPER ----
            for (int i=0;i<UPPER.length;i++) drawTooth(g2, permX(i), yUpper, BTN, UPPER[i], fCode);

            // ---- Midline markers ----
            g2.setFont(new Font("Tahoma", Font.BOLD, 14));
            g2.setColor(new Color(40,40,40));
            // ▼ antara 11 dan 21 (upper)
            int midX = permX(7)+BTN + (permX(8)-permX(7)-BTN)/2 + MID/2;
            g2.drawString("▼", midX-6, yUpper - 1);
            // ▲ antara 41 dan 31 (lower)
            g2.drawString("▲", midX-6, yLower + BTN + 12);

            // ---- Nomor gigi sulung RA ----
            g2.setFont(fNum);
            g2.setColor(new Color(130,90,0));
            for (int i=0;i<PRI_U.length;i++) drawCenter(g2, String.valueOf(PRI_U[i]), priX(i), yPriNumU, PBTN, 10);

            // ---- Baris PRI_U ----
            for (int i=0;i<PRI_U.length;i++) drawTooth(g2, priX(i), yPriU, PBTN, PRI_U[i], fCode);

            // ---- Midline garis tengah ----
            g2.setFont(new Font("Tahoma", Font.PLAIN, 7));
            g2.setColor(new Color(80,80,180));
            int rowW = permX(15)+BTN - permX(0);
            g2.drawLine(permX(0), yMid+9, permX(0)+rowW, yMid+9);
            // ▼ di tengah
            g2.setFont(new Font("Tahoma", Font.BOLD, 14));
            g2.setColor(new Color(40,40,40));
            g2.drawString("▼", midX-6, yMid+9);

            // ---- Baris PRI_L ----
            for (int i=0;i<PRI_L.length;i++) drawTooth(g2, priX(i), yPriL, PBTN, PRI_L[i], fCode);

            // ---- Nomor gigi sulung RB ----
            g2.setFont(fNum);
            g2.setColor(new Color(130,90,0));
            for (int i=0;i<PRI_L.length;i++) drawCenter(g2, String.valueOf(PRI_L[i]), priX(i), yPriNumL, PBTN, 10);

            // ---- Baris LOWER ----
            for (int i=0;i<LOWER.length;i++) drawTooth(g2, permX(i), yLower, BTN, LOWER[i], fCode);

            // ---- Nomor gigi RB bawah ----
            g2.setFont(fNum);
            g2.setColor(new Color(60,60,60));
            for (int i=0;i<LOWER.length;i++) drawCenter(g2, String.valueOf(LOWER[i]), permX(i), yNumL, BTN, 10);

            // ---- Legend kanan ----
            int lx = permX(15) + BTN + 10;
            int ly = yNumU;
            g2.setFont(new Font("Tahoma", Font.BOLD, 9));
            g2.setColor(new Color(30,30,130));
            g2.drawString("Keterangan Warna :", lx, ly+10);
            g2.drawLine(lx, ly+13, lx+185, ly+13);
            ly += 17;
            for (int i=0; i<CODES.length; i++) {
                g2.setColor(COLORS[i]);
                g2.fillRect(lx, ly, 20, 14);
                g2.setColor(new Color(100,100,100));
                g2.drawRect(lx, ly, 20, 14);
                g2.setFont(new Font("Tahoma", Font.BOLD, 8));
                g2.setColor(Color.BLACK);
                drawCenter(g2, CODES[i], lx, ly, 20, 14);
                g2.setFont(fLeg);
                g2.setColor(new Color(40,40,40));
                g2.drawString("= "+LABELS[i], lx+24, ly+11);
                ly += 18;
            }
            // Cara input
            ly += 4;
            g2.setFont(new Font("Tahoma", Font.BOLD, 9));
            g2.setColor(new Color(30,30,130));
            g2.drawString("Cara Input :", lx, ly+10);
            g2.drawLine(lx, ly+13, lx+185, ly+13);
            ly += 17;
            String[] tips = {
                "· Pilih kode kondisi di toolbar atas",
                "· Klik kiri gigi → kondisi aktif",
                "· Klik kanan gigi → pilih kondisi",
                "  atau tambah keterangan",
                "· Gigi Normal (N) tidak disimpan",
                "· DMF-T dihitung otomatis",
                "  dari gigi permanen (11-48)",
            };
            g2.setFont(fLeg);
            g2.setColor(new Color(50,50,50));
            for (String t : tips) { g2.drawString(t, lx, ly+10); ly += 13; }

            g2.dispose();
        }

        /** Gambar satu gigi dengan pola 5 permukaan standar FDI */
        private void drawTooth(Graphics2D g2, int x, int y, int s, int num, Font fCode) {
            String k   = kondisiMap.getOrDefault(num, "N");
            Color  col = colorFor(k);

            int s3  = s/3;
            int ix  = x + s3;
            int iy  = y + s3;
            int ix2 = x + 2*s3;
            int iy2 = y + 2*s3;

            if ("M".equals(k)) {
                // Missing: seluruh kotak diarsir abu
                g2.setColor(new Color(180,180,180));
                g2.fillRect(x, y, s, s);
                g2.setColor(new Color(100,100,100));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRect(x, y, s, s);
                g2.drawLine(x, y, x+s, y+s);
                g2.drawLine(x+s, y, x, y+s);
                g2.setStroke(new BasicStroke(1f));
            } else {
                // 5 zona: top, bottom, left, right, center
                // Top (bukal/labial)
                g2.setColor(col);
                g2.fillPolygon(new int[]{x,x+s,ix2,ix}, new int[]{y,y,iy,iy}, 4);
                // Bottom (lingual/palatal)
                g2.fillPolygon(new int[]{x,x+s,ix2,ix}, new int[]{y+s,y+s,iy2,iy2}, 4);
                // Left (mesial/distal)
                g2.fillPolygon(new int[]{x,x,ix,ix}, new int[]{y,y+s,iy2,iy}, 4);
                // Right (distal/mesial)
                g2.fillPolygon(new int[]{x+s,x+s,ix2,ix2}, new int[]{y,y+s,iy2,iy}, 4);
                // Center (oklusal/insisal)
                g2.fillRect(ix, iy, ix2-ix, iy2-iy);

                // Border luar
                g2.setColor(new Color(80,80,80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRect(x, y, s, s);
                // Border dalam (inner rect)
                g2.drawRect(ix, iy, ix2-ix, iy2-iy);
                // Diagonal lines (sudut luar ke sudut dalam)
                g2.drawLine(x, y, ix, iy);
                g2.drawLine(x+s, y, ix2, iy);
                g2.drawLine(x, y+s, ix, iy2);
                g2.drawLine(x+s, y+s, ix2, iy2);

                // Kode kondisi di tengah (jika bukan Normal)
                if (!"N".equals(k)) {
                    g2.setFont(fCode);
                    g2.setColor(new Color(30,30,30));
                    FontMetrics fm = g2.getFontMetrics();
                    int tw = fm.stringWidth(k);
                    int th = fm.getAscent();
                    g2.drawString(k, x+(s-tw)/2, y+(s+th)/2-1);
                }
            }
        }

        /** Gambar teks di tengah kotak */
        private void drawCenter(Graphics2D g2, String txt, int x, int y, int w, int h) {
            FontMetrics fm = g2.getFontMetrics();
            int tx = x + (w - fm.stringWidth(txt))/2;
            int ty = y + (h + fm.getAscent() - fm.getDescent())/2;
            g2.drawString(txt, tx, ty);
        }
    }

    // ---- Apply kondisi ----
    private void applyCondition(int num, String kode) {
        kondisiMap.put(num, kode);
        canvas.repaint();
        updateDMFT();
        if (onChangeCallback != null) onChangeCallback.run();
    }

    // ---- Context menu klik kanan ----
    private void showContextMenu(MouseEvent e, int num) {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem hdr = new JMenuItem("Gigi "+num+" — pilih kondisi:");
        hdr.setFont(new Font("Tahoma", Font.BOLD, 10));
        hdr.setEnabled(false);
        popup.add(hdr);
        popup.addSeparator();
        for (int i=0; i<CODES.length; i++) {
            final String kode = CODES[i];
            String curr = kondisiMap.getOrDefault(num, "N");
            JMenuItem mi = new JMenuItem("  "+kode+"  —  "+LABELS[i]+(kode.equals(curr)?"  ✓":""));
            mi.setBackground(COLORS[i]);
            mi.setOpaque(true);
            mi.setFont(new Font("Tahoma", Font.PLAIN, 10));
            mi.addActionListener(ev -> applyCondition(num, kode));
            popup.add(mi);
        }
        popup.addSeparator();
        JMenuItem miKet = new JMenuItem("  Tambah/Edit Keterangan...");
        miKet.addActionListener(ev -> {
            String ket = (String)JOptionPane.showInputDialog(
                this, "Keterangan untuk Gigi "+num+":", "Keterangan Gigi",
                JOptionPane.PLAIN_MESSAGE, null, null, keteranganMap.getOrDefault(num,""));
            if (ket!=null) { keteranganMap.put(num, ket); canvas.repaint(); }
        });
        popup.add(miKet);
        JMenuItem miReset = new JMenuItem("  Reset ke Normal (N)");
        miReset.addActionListener(ev -> applyCondition(num, "N"));
        popup.add(miReset);
        popup.show(canvas, e.getX(), e.getY());
    }

    // ---- Update DMF-T ----
    private void updateDMFT() {
        int d=0,m=0,f=0;
        for (Map.Entry<Integer,String> e : kondisiMap.entrySet()) {
            if (e.getKey()<50) {
                String k = e.getValue();
                if ("C".equals(k)||"?".equals(k)) d++;
                else if ("M".equals(k)) m++;
                else if ("F".equals(k)||"Cr".equals(k)||"RCT".equals(k)) f++;
            }
        }
        if (lDMF!=null)
            lDMF.setText("DMF-T :  D="+d+"  M="+m+"  F="+f+"  →  Total = "+(d+m+f));
    }

    // ---- Public API ----
    public void loadData(Map<Integer,String> kondisi, Map<Integer,String> keterangan) {
        kondisiMap.clear(); keteranganMap.clear();
        if (kondisi    != null) kondisiMap.putAll(kondisi);
        if (keterangan != null) keteranganMap.putAll(keterangan);
        canvas.repaint(); updateDMFT();
    }

    public void resetAll() {
        kondisiMap.clear(); keteranganMap.clear();
        canvas.repaint();
        if (lDMF!=null) lDMF.setText("DMF-T :  D=0  M=0  F=0  →  Total = 0");
    }

    public Map<Integer,String> getKondisiMap()    { return Collections.unmodifiableMap(kondisiMap); }
    public Map<Integer,String> getKeteranganMap() { return Collections.unmodifiableMap(keteranganMap); }

    public int[] getDMFT() {
        int d=0,m=0,f=0;
        for (Map.Entry<Integer,String> e : kondisiMap.entrySet()) {
            if (e.getKey()<50) {
                String k=e.getValue();
                if ("C".equals(k)||"?".equals(k)) d++;
                else if ("M".equals(k)) m++;
                else if ("F".equals(k)||"Cr".equals(k)||"RCT".equals(k)) f++;
            }
        }
        return new int[]{d,m,f};
    }

    /** Render panel ke PNG InputStream untuk JasperReports */
    public java.io.InputStream renderToStream() {
        try {
            java.awt.Dimension ps = canvas.getPreferredSize();
            canvas.setSize(ps);
            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                ps.width, ps.height, java.awt.image.BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, ps.width, ps.height);
            canvas.paint(g2);
            g2.dispose();
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            javax.imageio.ImageIO.write(img, "png", baos);
            return new java.io.ByteArrayInputStream(baos.toByteArray());
        } catch (Exception e) {
            System.out.println("Notif renderToStream: "+e);
            return null;
        }
    }
}
