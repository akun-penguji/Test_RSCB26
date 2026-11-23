package rekammedis;

import fungsi.koneksiDB;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.print.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

/**
 * Form Grafik Partograf WHO
 * Simbol penandaan sesuai standar WHO:
 *  - DJJ           : titik (•) dihubungkan garis, skala 80-200 bpm
 *  - Pembukaan     : X, di-plot mulai di garis waspada, skala 0-10 cm
 *  - Penurunan     : O (lingkaran), skala 5/5 (atas) → 0/5 (bawah)
 *  - Garis Waspada : diagonal 4cm jam-0 → 10cm jam-6 (1cm/jam)
 *  - Garis Bertindak: paralel, geser +4 jam ke kanan
 *  - TD            : panah vertikal (↕), sistole=atas, diastole=bawah, skala 60-180
 *  - Nadi          : titik (•), skala 60-180
 *  - Kontraksi     : kotak dengan isian sesuai durasi:
 *                    <20dt=titik-titik, 20-40dt=diagonal, >40dt=penuh
 *  - Suhu          : titik (•), skala 35-40°C
 */
public class DlgPartografGrafik extends JDialog implements Printable {

    private Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps, ps2;
    private ResultSet rs, rs2;

    // Data pasien (public agar bisa di-set dari luar)
    public String noRawat, nmPasien, noRM, umur, g_, p_, a_, ketubanPecah, mulesSejak, tglPartograf;

    // Data observasi terurut
    private List<ObsData> obsList = new ArrayList<>();

    // Panel grafik yang bisa di-scroll dan di-print
    private PartografPanel grafikPanel;
    private JScrollPane scrollPane;

    // ========================== DATA MODEL ==========================
    static class ObsData {
        double jamKe; // jam dari mulai observasi pertama
        String waktu; // label waktu HH:mm
        int djj;
        String airKetuban;  // I/C/M/B
        String penyusupan;  // 0/+/++/+++
        int pembukaan;      // 0-10 cm
        int penurunan;      // 0-5 (5=belum engaged, 0=fully)
        int kontraksiJml;   // 1-5
        String kontraksiDurasi; // "<20" / "20-40" / ">40"
        int sistole, diastole, nadi;
        double suhu;
        String urineVol, urineProt, urineAseton;
    }

    // ========================== KONSTRUKTOR ==========================
    public DlgPartografGrafik(Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Grafik Partograf");
        // Fullscreen relative to parent frame
        if (parent != null) {
            setBounds(parent.getBounds());
        } else {
            java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            setBounds(0, 0, screen.width, screen.height);
        }
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 3));
        toolbar.setBackground(new Color(52, 73, 94));

        JButton btnPrint = new JButton("Cetak");
        btnPrint.setIcon(UIManager.getIcon("FileView.fileIcon"));
        btnPrint.addActionListener(e -> doCetak());
        toolbar.add(btnPrint);

        JButton btnClose = new JButton("Tutup");
        btnClose.addActionListener(e -> dispose());
        toolbar.add(btnClose);

        add(toolbar, BorderLayout.NORTH);

        // Grafik panel
        grafikPanel = new PartografPanel();
        scrollPane = new JScrollPane(grafikPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
    }

    // ========================== SET DATA ==========================
    public void setData(String noRawat) {
        this.noRawat = noRawat;
        obsList.clear();
        try {
            // Info pasien dari partograf
            ps = koneksi.prepareStatement(
                "SELECT DATE_FORMAT(pt.tgl_jam,'%d/%m/%Y %H:%i'), pt.g, pt.p, pt.a, " +
                "DATE_FORMAT(pt.mules_sejak,'%d/%m/%Y %H:%i'), pt.ketuban_pecah, " +
                "pa.nm_pasien, pa.no_rkm_medis, " +
                "TIMESTAMPDIFF(YEAR, pa.tgl_lahir, CURDATE()) " +
                "FROM partograf pt " +
                "JOIN reg_periksa rp ON pt.no_rawat=rp.no_rawat " +
                "JOIN pasien pa ON rp.no_rkm_medis=pa.no_rkm_medis " +
                "WHERE pt.no_rawat=? AND pt.status='aktif' ORDER BY pt.tgl_jam DESC LIMIT 1");
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            if (rs.next()) {
                tglPartograf = safe(rs.getString(1));
                g_ = safe(rs.getString(2)); p_ = safe(rs.getString(3)); a_ = safe(rs.getString(4));
                mulesSejak = safe(rs.getString(5)); ketubanPecah = safe(rs.getString(6));
                nmPasien = safe(rs.getString(7)); noRM = safe(rs.getString(8));
                umur = safe(rs.getString(9)) + " th";
            }
            rs.close(); ps.close();

            // Data observasi
            ps2 = koneksi.prepareStatement(
                "SELECT " +
                "  TIMESTAMPDIFF(MINUTE, MIN(tgl_jam) OVER(), tgl_jam)/60.0, " +
                "  DATE_FORMAT(tgl_jam,'%H:%i'), djj, air_ketuban, penyusupan, " +
                "  IFNULL(pembukaan,0), IFNULL(penurunan,0), " +
                "  IFNULL(kontraksi_jml,0), kontraksi_durasi, " +
                "  IFNULL(sistole,0), IFNULL(diastole,0), IFNULL(nadi,0), " +
                "  IFNULL(suhu,0), urine_vol, urine_prot, urine_aseton " +
                "FROM partograf_observasi " +
                "WHERE no_rawat=? AND status='aktif' ORDER BY tgl_jam");
            ps2.setString(1, noRawat);
            rs2 = ps2.executeQuery();
            while (rs2.next()) {
                ObsData d = new ObsData();
                d.jamKe         = rs2.getDouble(1);
                d.waktu         = safe(rs2.getString(2));
                d.djj           = rs2.getInt(3);
                d.airKetuban    = safe(rs2.getString(4));
                d.penyusupan    = safe(rs2.getString(5));
                d.pembukaan     = rs2.getInt(6);
                try { d.penurunan = Integer.parseInt(rs2.getString(7)); } catch (Exception ex) { d.penurunan = 0; }
                d.kontraksiJml  = rs2.getInt(8);
                d.kontraksiDurasi = safe(rs2.getString(9));
                d.sistole       = rs2.getInt(10);
                d.diastole      = rs2.getInt(11);
                d.nadi          = rs2.getInt(12);
                try { d.suhu = Double.parseDouble(rs2.getString(13)); } catch (Exception ex) { d.suhu = 0; }
                d.urineVol      = safe(rs2.getString(14));
                d.urineProt     = safe(rs2.getString(15));
                d.urineAseton   = safe(rs2.getString(16));
                obsList.add(d);
            }
            rs2.close(); ps2.close();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error memuat data: " + ex.getMessage());
        }
        grafikPanel.repaint();
        grafikPanel.revalidate();
    }

    private String safe(String s) { return s == null ? "" : s; }

    public java.awt.image.BufferedImage getSnapshot() {
        java.awt.Dimension d = grafikPanel.getPreferredSize();
        // Set ukuran panel secara eksplisit agar layout benar
        grafikPanel.setSize(d);
        grafikPanel.doLayout();

        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
            d.width, d.height, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
        // Fill background putih dulu
        g2.setColor(java.awt.Color.WHITE);
        g2.fillRect(0, 0, d.width, d.height);
        // Paint grafik
        grafikPanel.paint(g2);
        g2.dispose();
        return image;
    }

    // ========================== CETAK ==========================
    private void doCetak() {
        PrinterJob pj = PrinterJob.getPrinterJob();
        PageFormat pf = pj.defaultPage();
        pf.setOrientation(PageFormat.LANDSCAPE);
        pj.setPrintable(this, pf);
        if (pj.printDialog()) {
            try { pj.print(); }
            catch (PrinterException ex) { JOptionPane.showMessageDialog(this, "Gagal cetak: " + ex.getMessage()); }
        }
    }

    @Override
    public int print(Graphics g2, PageFormat pf, int page) throws PrinterException {
        if (page > 0) return NO_SUCH_PAGE;
        Graphics2D g = (Graphics2D) g2;
        g.translate(pf.getImageableX(), pf.getImageableY());
        double sx = pf.getImageableWidth()  / grafikPanel.getPreferredSize().getWidth();
        double sy = pf.getImageableHeight() / grafikPanel.getPreferredSize().getHeight();
        double scale = Math.min(sx, sy);
        g.scale(scale, scale);
        grafikPanel.printAll(g);
        return PAGE_EXISTS;
    }

    // ========================== PANEL GRAFIK ==========================
    class PartografPanel extends JPanel {

        // Layout constants
        static final int MARGIN_LEFT   = 150;
        static final int MARGIN_TOP    = 90;
        static final int MARGIN_RIGHT  = 180;
        static final int COL_W         = 60;  // lebar per kolom (per jam)
        static final int MAX_HOURS     = 24;

        // Section heights
        static final int H_DJJ         = 250; // DJJ: 80-200 = 120 unit
        static final int H_KETUBAN     = 40;  // Air Ketuban + Penyusupan
        static final int H_PEMBUKAAN   = 350; // Pembukaan 0-10 cm
        static final int H_KONTRAKSI   = 100; // Kontraksi 1-5 x 5 baris
        static final int H_OKSITOSIN   = 40;  // Oksitosin
        static final int H_TD          = 250; // TD+Nadi 60-180
        static final int H_SUHU        = 60;  // Suhu 35-40
        static final int H_URIN        = 90;  // Urin 3 baris
        static final int ROW_H         = 20;  // per baris di section kecil

        int totalWidth, totalHeight;
        int yDJJ, yKetuban, yPembukaan, yKontraksi, yOksitosin, yTD, ySuhu, yUrin;

        PartografPanel() {
            setBackground(Color.WHITE);
        }

        @Override
        public Dimension getPreferredSize() {
            computeSections();
            return new Dimension(totalWidth + MARGIN_LEFT + MARGIN_RIGHT + 10, totalHeight + MARGIN_TOP + 40);
        }

        void computeSections() {
            totalWidth = COL_W * MAX_HOURS;
            yDJJ        = MARGIN_TOP;
            yKetuban    = yDJJ + H_DJJ + 5;
            yPembukaan  = yKetuban + H_KETUBAN + 5;
            yKontraksi  = yPembukaan + H_PEMBUKAAN + 5;
            yOksitosin  = yKontraksi + H_KONTRAKSI + 5;
            yTD         = yOksitosin + H_OKSITOSIN + 5;
            ySuhu       = yTD + H_TD + 5;
            yUrin       = ySuhu + H_SUHU + 5;
            totalHeight = yUrin + H_URIN + 20;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            super.paintComponent(g0);
            Graphics2D g = (Graphics2D) g0;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            computeSections();

            drawHeader(g);
            drawDJJ(g);
            drawKetuban(g);
            drawPembukaan(g);
            drawKontraksi(g);
            drawOksitosin(g);
            drawTD(g);
            drawSuhu(g);
            drawUrin(g);
            drawTimeAxis(g);
        }

        // ---- Helpers ----
        int xOf(double jamKe) {
            return MARGIN_LEFT + (int)(jamKe * COL_W);
        }

        void drawSectionBorder(Graphics2D g, int x, int y, int w, int h) {
            g.setColor(new Color(180,180,180));
            g.drawRect(x, y, w, h);
        }

        void drawGrid(Graphics2D g, int x, int y, int w, int h, int rowCount, int colCount) {
            g.setColor(new Color(200,200,200));
            // vertical lines (jam)
            for (int c = 0; c <= colCount; c++) {
                int xc = x + c * (w / colCount);
                g.drawLine(xc, y, xc, y+h);
            }
            // horizontal lines
            for (int r = 0; r <= rowCount; r++) {
                int yr = y + r * (h / rowCount);
                g.drawLine(x, yr, x+w, yr);
            }
        }

        void drawLabel(Graphics2D g, String text, int x, int y, int w, int h) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 9));
            FontMetrics fm = g.getFontMetrics();
            // word wrap kiri
            int lineH = fm.getHeight();
            String[] words = text.split("\\n");
            int startY = y + (h - words.length * lineH) / 2 + fm.getAscent();
            for (String line : words) {
                g.drawString(line, x + w - fm.stringWidth(line) - 2, startY);
                startY += lineH;
            }
        }

        void drawLabelLeft(Graphics2D g, String text, int x, int y, int maxW, boolean bold) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", bold ? Font.BOLD : Font.PLAIN, 9));
            g.drawString(text, x, y);
        }

        // ---- HEADER ----
        void drawHeader(Graphics2D g) {
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            FontMetrics fm = g.getFontMetrics();
            String title = "PARTOGRAF";
            g.drawString(title, MARGIN_LEFT + (totalWidth - fm.stringWidth(title)) / 2, 20);

            g.setFont(new Font("Arial", Font.PLAIN, 11));
            int y = 34;
            g.drawString("No. Register : " + noRawat, MARGIN_LEFT, y);
            g.drawString("Nama Ibu : " + nmPasien, MARGIN_LEFT + 220, y);
            g.drawString("Umur : " + umur, MARGIN_LEFT + 550, y);
            g.drawString("G: " + DlgPartografGrafik.this.g_ + "  P: " + DlgPartografGrafik.this.p_ + "  A: " + DlgPartografGrafik.this.a_, MARGIN_LEFT + 680, y);
            y += 16;
            g.drawString("No. RM : " + noRM, MARGIN_LEFT, y);
            g.drawString("Tanggal : " + tglPartograf, MARGIN_LEFT + 220, y);
            g.drawString("Mulas sejak pukul : " + mulesSejak, MARGIN_LEFT + 400, y);
            y += 16;
            g.drawString("Ketuban Pecah : " + ketubanPecah, MARGIN_LEFT, y);
        }

        // Access outer class fields g_, p_, a_ directly as inner class can see them

        // ---- DJJ (80-200 bpm) ----
        // Simbol: titik (•) per observasi, dihubungkan garis lurus
        // Batas normal: 120-160 bpm (shading kuning)
        void drawDJJ(Graphics2D g) {
            int x = MARGIN_LEFT, y = yDJJ, w = totalWidth, h = H_DJJ;
            int minV = 80, maxV = 200;

            // Label kiri
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.drawString("Denyut", x - 80, y + 12);
            g.drawString("Jantung", x - 80, y + 26);
            g.drawString("Janin", x - 80, y + 40);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            g.drawString("(/menit)", x - 75, y + 54);

            // Grid background
            drawSectionBorder(g, x, y, w, h);

            // Zona normal DJJ 120-160 (warna hijau muda)
            int y120 = y + (int)((maxV - 120) / (double)(maxV - minV) * h);
            int y160 = y + (int)((maxV - 160) / (double)(maxV - minV) * h);
            g.setColor(new Color(200, 240, 200, 80));
            g.fillRect(x+1, y160, w-1, y120-y160);

            // Grid horizontal: tiap 10 bpm
            g.setFont(new Font("Arial", Font.PLAIN, 9));
            for (int v = minV; v <= maxV; v += 10) {
                int yv = y + (int)((maxV - v) / (double)(maxV - minV) * h);
                g.setColor(v == 120 || v == 160 ? new Color(0,150,0) : new Color(220,220,220));
                g.setStroke(v == 120 || v == 160 ? new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{4,2}, 0) : new BasicStroke(0.5f));
                g.drawLine(x, yv, x+w, yv);
                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(1f));
                String lbl = String.valueOf(v);
                g.drawString(lbl, x - g.getFontMetrics().stringWidth(lbl) - 4, yv + 4);
            }

            // Grid vertical (tiap jam)
            g.setStroke(new BasicStroke(0.5f));
            for (int hr = 0; hr <= MAX_HOURS; hr++) {
                int xh = x + hr * COL_W;
                g.setColor(new Color(220,220,220));
                g.drawLine(xh, y, xh, y+h);
            }
            g.setStroke(new BasicStroke(1f));

            if (obsList.isEmpty()) return;

            // Plot data: titik + garis
            int[] xPts = new int[obsList.size()];
            int[] yPts = new int[obsList.size()];
            for (int i = 0; i < obsList.size(); i++) {
                ObsData d = obsList.get(i);
                if (d.djj <= 0) continue;
                xPts[i] = xOf(d.jamKe);
                yPts[i] = y + (int)((maxV - d.djj) / (double)(maxV - minV) * h);
            }
            // Garis penghubung
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(1.2f));
            for (int i = 1; i < obsList.size(); i++) {
                if (obsList.get(i).djj > 0 && obsList.get(i-1).djj > 0) {
                    g.drawLine(xPts[i-1], yPts[i-1], xPts[i], yPts[i]);
                }
            }
            // Titik (•)
            for (int i = 0; i < obsList.size(); i++) {
                ObsData d = obsList.get(i);
                if (d.djj <= 0) continue;
                int xd = xPts[i], yd = yPts[i];
                boolean abnormal = d.djj < 120 || d.djj > 160;
                g.setColor(abnormal ? Color.RED : Color.BLACK);
                g.fillOval(xd-4, yd-4, 8, 8);
                // Label nilai
                g.setFont(new Font("Arial", Font.PLAIN, 9));
                g.drawString(String.valueOf(d.djj), xd+5, yd-3);
            }
            g.setStroke(new BasicStroke(1f));
        }

        // ---- AIR KETUBAN & PENYUSUPAN ----
        // Kode: I=Intact, C=Clear, M=Meconium, B=Blood
        void drawKetuban(Graphics2D g) {
            int x = MARGIN_LEFT, y = yKetuban, w = totalWidth;
            int rowH = H_KETUBAN / 2;

            // Labels
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 10));
            g.drawString("Air Ketuban", x - 100, y + rowH/2 + 4);
            g.drawString("Penyusupan", x - 97, y + rowH + rowH/2 + 4);

            // Borders
            g.drawRect(x, y, w, H_KETUBAN);
            g.drawLine(x, y+rowH, x+w, y+rowH);

            // Grid vertical
            g.setColor(new Color(220,220,220));
            g.setStroke(new BasicStroke(0.5f));
            for (int hr = 1; hr < MAX_HOURS; hr++) {
                int xh = x + hr * COL_W;
                g.drawLine(xh, y, xh, y+H_KETUBAN);
            }
            g.setStroke(new BasicStroke(1f));

            // Plot data (geser ke tengah kolom)
            for (ObsData d : obsList) {
                int xd = xOf(d.jamKe) + COL_W/4;
                // Air Ketuban - kode huruf dengan warna sesuai kondisi
                String ak = d.airKetuban.isEmpty() ? "" : d.airKetuban.substring(0,1).toUpperCase();
                Color akColor = "M".equals(ak) ? new Color(120,60,0) :
                                "B".equals(ak) ? Color.RED :
                                "C".equals(ak) ? new Color(0,100,200) : Color.BLACK;
                g.setColor(akColor);
                g.setFont(new Font("Arial", Font.BOLD, 10));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(ak, xd - fm.stringWidth(ak)/2, y + rowH/2 + 4);

                // Penyusupan
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 10));
                fm = g.getFontMetrics();
                g.drawString(d.penyusupan, xd - fm.stringWidth(d.penyusupan)/2, y + rowH + rowH/2 + 4);
            }
        }

        // ---- PEMBUKAAN + PENURUNAN ----
        // Pembukaan: X, skala 0-10 cm
        // Penurunan: O, skala 5/5 (atas) → 0/5 (bawah) -- sumbu Y terbalik vs pembukaan
        // Garis Waspada: 4cm jam-0 → 10cm jam-6 (1 cm/jam)
        // Garis Bertindak: paralel, geser +4 jam
        void drawPembukaan(Graphics2D g) {
            int x = MARGIN_LEFT, y = yPembukaan, w = totalWidth, h = H_PEMBUKAAN;
            int minV = 0, maxV = 10;

            // Labels
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.drawString("Pembukaan", x - 110, y + h/3);
            g.drawString("serviks (cm)", x - 110, y + h/3 + 14);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            g.drawString("bertanda X", x - 105, y + h/3 + 28);

            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.drawString("Penurunan", x - 110, y + h*2/3);
            g.drawString("kepala", x - 90, y + h*2/3 + 14);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            g.drawString("bertanda O", x - 105, y + h*2/3 + 28);

            // Border
            g.setColor(Color.BLACK);
            drawSectionBorder(g, x, y, w, h);

            // Grid horizontal tiap 1 cm pembukaan
            g.setFont(new Font("Arial", Font.PLAIN, 9));
            for (int v = minV; v <= maxV; v++) {
                int yv = y + (int)((maxV - v) / (double)(maxV - minV) * h);
                g.setColor(new Color(220,220,220));
                g.setStroke(new BasicStroke(0.5f));
                g.drawLine(x, yv, x+w, yv);
                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(1f));
                g.drawString(String.valueOf(v), x - 16, yv + 4);
            }

            // Grid vertical
            g.setStroke(new BasicStroke(0.5f));
            g.setColor(new Color(220,220,220));
            for (int hr = 0; hr <= MAX_HOURS; hr++) {
                int xh = x + hr * COL_W;
                g.drawLine(xh, y, xh, y+h);
            }
            g.setStroke(new BasicStroke(1f));

            // === GARIS WASPADA ===
            // Berdasarkan WHO: titik pertama PEMBUKAAN yang tercatat (minimal 4cm) di-plot mulai di garis waspada.
            // Di sini kita gambar garis waspada mulai dari jam ke-n dimana pembukaan >= 4cm.
            double waspadaStartHour = -1;
            for (ObsData d : obsList) {
                if (d.pembukaan >= 4) {
                    waspadaStartHour = d.jamKe;
                    break;
                }
            }
            
            if (waspadaStartHour != -1) {
                int xWaspadaStart = x + (int)(waspadaStartHour * COL_W);
                int xWaspadaEnd   = x + (int)((waspadaStartHour + 6) * COL_W); // 6 jam ke 10cm
                int yWaspadaStart = y + (int)((maxV - 4) / (double)(maxV - minV) * h);
                int yWaspadaEnd   = y + (int)((maxV - 10) / (double)(maxV - minV) * h);

                // Label WASPADA
                g.setColor(new Color(200,100,0));
                g.setStroke(new BasicStroke(2f));
                g.drawLine(xWaspadaStart, yWaspadaStart, xWaspadaEnd, yWaspadaEnd);
                g.setFont(new Font("Arial", Font.BOLD, 11));
                g.drawString("WASPADA", xWaspadaStart + 10, (yWaspadaStart + yWaspadaEnd)/2 - 5);

                // === GARIS BERTINDAK === (geser +4 jam dari waspada)
                int xBertindakStart = xWaspadaStart + 4 * COL_W;
                int xBertindakEnd   = xBertindakStart + 6 * COL_W;
                g.setColor(new Color(180,0,0));
                g.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{6,3}, 0));
                g.drawLine(xBertindakStart, yWaspadaStart, xBertindakEnd, yWaspadaEnd);
                g.drawString("BERTINDAK", xBertindakStart + 5, (yWaspadaStart + yWaspadaEnd)/2 - 5);
                g.setStroke(new BasicStroke(1f));
            }

            if (obsList.isEmpty()) return;

            // Plot Pembukaan (X) + garis penghubung
            g.setColor(new Color(200,0,0));
            g.setStroke(new BasicStroke(1.2f));
            for (int i = 1; i < obsList.size(); i++) {
                ObsData d0 = obsList.get(i-1), d1 = obsList.get(i);
                if (d0.pembukaan > 0 && d1.pembukaan > 0) {
                    int x0 = xOf(d0.jamKe), y0 = y + (int)((maxV - d0.pembukaan) / (double)(maxV-minV) * h);
                    int x1 = xOf(d1.jamKe), y1 = y + (int)((maxV - d1.pembukaan) / (double)(maxV-minV) * h);
                    g.drawLine(x0, y0, x1, y1);
                }
            }
            for (ObsData d : obsList) {
                if (d.pembukaan <= 0) continue;
                int xd = xOf(d.jamKe);
                int yd = y + (int)((maxV - d.pembukaan) / (double)(maxV-minV) * h);
                drawX(g, xd, yd, new Color(180,0,0));
            }
            g.setStroke(new BasicStroke(1f));

            // Plot Penurunan (O) - 5=atas (5/5), 0=bawah (0/5)
            // Di grafik: nilai 5 = posisi y paling bawah (seperti di partograf standar, penurunan semakin ke bawah semakin maju)
            // Namun dalam implementasi digital: kita reverse: 5=atas, 0=bawah pada sumbu kiri penurunan
            g.setColor(new Color(0,0,180));
            g.setStroke(new BasicStroke(1.2f));
            for (int i = 1; i < obsList.size(); i++) {
                ObsData d0 = obsList.get(i-1), d1 = obsList.get(i);
                int x0 = xOf(d0.jamKe), y0 = y + (int)((5 - d0.penurunan) / 5.0 * h);
                int x1 = xOf(d1.jamKe), y1 = y + (int)((5 - d1.penurunan) / 5.0 * h);
                g.drawLine(x0, y0, x1, y1);
            }
            for (ObsData d : obsList) {
                int xd = xOf(d.jamKe);
                int yd = y + (int)((5 - d.penurunan) / 5.0 * h);
                drawO(g, xd, yd, new Color(0,0,180));
            }
            // Label skala penurunan (kanan)
            g.setColor(new Color(0,0,180));
            g.setFont(new Font("Arial", Font.BOLD, 9));
            for (int pn = 0; pn <= 5; pn++) {
                int yd = y + (int)((5 - pn) / 5.0 * h);
                g.drawString(pn+"/5", x + w + 5, yd + 4);
            }
            g.setStroke(new BasicStroke(1f));
        }

        void drawX(Graphics2D g, int cx, int cy, Color color) {
            int r = 5;
            g.setColor(color);
            g.setStroke(new BasicStroke(2f));
            g.drawLine(cx-r, cy-r, cx+r, cy+r);
            g.drawLine(cx+r, cy-r, cx-r, cy+r);
            g.setStroke(new BasicStroke(1f));
        }

        void drawO(Graphics2D g, int cx, int cy, Color color) {
            int r = 4;
            g.setColor(color);
            g.setStroke(new BasicStroke(1.8f));
            g.drawOval(cx-r, cy-r, r*2, r*2);
            g.setStroke(new BasicStroke(1f));
        }

        // ---- KONTRAKSI ----
        // 5 baris = 1-5 kontraksi, tiap kontraksi 1 kotak
        // Durasi: <20s = kotak titik-titik, 20-40s = diagonal, >40s = penuh
        void drawKontraksi(Graphics2D g) {
            int x = MARGIN_LEFT, y = yKontraksi, w = totalWidth;
            int rows = 5;
            int rowH = H_KONTRAKSI / rows;

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.drawString("Kontraksi", x - 100, y + H_KONTRAKSI/2 - 6);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            g.drawString("/10 menit", x - 97, y + H_KONTRAKSI/2 + 8);

            // Border + grid
            g.drawRect(x, y, w, H_KONTRAKSI);
            for (int r = 1; r < rows; r++) {
                g.setColor(new Color(200,200,200));
                g.drawLine(x, y+r*rowH, x+w, y+r*rowH);
            }
            // Label sisi kiri (1–5)
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 9));
            for (int r = 0; r < rows; r++) {
                g.drawString(String.valueOf(rows-r), x-14, y + r*rowH + rowH/2 + 3);
            }

            // Grid kolom
            g.setColor(new Color(220,220,220));
            g.setStroke(new BasicStroke(0.5f));
            for (int hr = 1; hr < MAX_HOURS; hr++) {
                int xh = x + hr * COL_W;
                g.drawLine(xh, y, xh, y+H_KONTRAKSI);
            }
            g.setStroke(new BasicStroke(1f));

            // Plot kontraksi (nempel setelah garis)
            for (ObsData d : obsList) {
                if (d.kontraksiJml <= 0) continue;
                int xd = xOf(d.jamKe);
                int boxW = COL_W - 4;
                int jml = Math.min(d.kontraksiJml, 5);
                for (int b = 0; b < jml; b++) {
                    int row = rows - 1 - b; // baris dari bawah
                    int bx = xd + 2;
                    int by = y + row * rowH + 2;
                    int bh = rowH - 4;
                    drawKontraksiBox(g, bx, by, boxW, bh, d.kontraksiDurasi);
                }
            }
        }

        void drawKontraksiBox(Graphics2D g, int bx, int by, int bw, int bh, String durasi) {
            if ("<20".equals(durasi) || durasi == null || durasi.isEmpty()) {
                // Kotak titik-titik (belum efektif)
                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(0.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{2,2}, 0));
                g.drawRect(bx, by, bw, bh);
                g.setStroke(new BasicStroke(1f));
                // Isi titik-titik
                g.setColor(new Color(180,180,180));
                for (int px = bx+3; px < bx+bw-2; px += 4) {
                    for (int py = by+3; py < by+bh-2; py += 4) {
                        g.fillOval(px, py, 2, 2);
                    }
                }
            } else if (durasi.contains("20") || durasi.equals("20-40")) {
                // Diagonal garis
                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(1f));
                g.drawRect(bx, by, bw, bh);
                // Garis diagonal
                int step = 5;
                for (int s = 0; s < bw + bh; s += step) {
                    int x1 = bx + s, y1 = by;
                    int x2 = bx, y2 = by + s;
                    if (x1 > bx+bw) { y1 += (x1 - (bx+bw)); x1 = bx+bw; }
                    if (y2 > by+bh) { x2 += (y2 - (by+bh)); y2 = by+bh; }
                    if (x2 <= bx+bw && y1 <= by+bh) g.drawLine(x1, y1, x2, y2);
                }
            } else {
                // Penuh (>40 det)
                g.setColor(Color.BLACK);
                g.fillRect(bx, by, bw, bh);
                g.drawRect(bx, by, bw, bh);
            }
        }

        // ---- OKSITOSIN ----
        void drawOksitosin(Graphics2D g) {
            int x = MARGIN_LEFT, y = yOksitosin, w = totalWidth;
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 10));
            g.drawString("Oksitosin U/L", x - 110, y + 12);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            g.drawString("Tetes/menit", x - 100, y + 26);
            g.drawRect(x, y, w, H_OKSITOSIN);
            // Grid
            g.setColor(new Color(220,220,220));
            g.setStroke(new BasicStroke(0.5f));
            for (int hr = 1; hr < MAX_HOURS; hr++) {
                g.drawLine(x + hr*COL_W, y, x + hr*COL_W, y+H_OKSITOSIN);
            }
            g.setStroke(new BasicStroke(1f));
            g.setColor(Color.BLACK);
            g.drawLine(x, y+H_OKSITOSIN/2, x+w, y+H_OKSITOSIN/2);
            // Oksitosin: data tersedia di model (oksitosin_unit, oksitosin_tetes) namun belum diplot
        }

        // ---- TD/NADI ----
        // TD: panah vertikal ↕ (atas=sistole, bawah=diastole)
        // Nadi: titik (•), dihubungkan garis putus-putus
        void drawTD(Graphics2D g) {
            int x = MARGIN_LEFT, y = yTD, w = totalWidth, h = H_TD;
            int minV = 60, maxV = 180;

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.drawString("Tekanan", x-90, y+h/3);
            g.drawString("Darah", x-80, y+h/3+14);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            g.drawString("↕ TD", x-70, y+h/3+28);
            g.drawString("• Nadi", x-75, y+h/3+42);

            drawSectionBorder(g, x, y, w, h);

            // Zona normal TD
            int y120 = y + (int)((maxV - 120) / (double)(maxV-minV) * h);
            int y80  = y + (int)((maxV - 80)  / (double)(maxV-minV) * h);
            g.setColor(new Color(200,220,255,60));
            g.fillRect(x+1, y120, w-1, y80-y120);

            // Grid horizontal
            g.setFont(new Font("Arial", Font.PLAIN, 9));
            for (int v = minV; v <= maxV; v += 10) {
                int yv = y + (int)((maxV - v) / (double)(maxV-minV) * h);
                g.setColor(new Color(220,220,220));
                g.setStroke(new BasicStroke(0.5f));
                g.drawLine(x, yv, x+w, yv);
                g.setColor(Color.BLACK);
                g.setStroke(new BasicStroke(1f));
                g.drawString(String.valueOf(v), x-20, yv+4);
            }

            // Grid vertikal
            g.setColor(new Color(220,220,220));
            g.setStroke(new BasicStroke(0.5f));
            for (int hr = 1; hr < MAX_HOURS; hr++) {
                g.drawLine(x+hr*COL_W, y, x+hr*COL_W, y+h);
            }
            g.setStroke(new BasicStroke(1f));

            if (obsList.isEmpty()) return;

            // Plot nadi: titik dihubungkan garis
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{4,2}, 0));
            for (int i = 1; i < obsList.size(); i++) {
                ObsData d0 = obsList.get(i-1), d1 = obsList.get(i);
                if (d0.nadi > 0 && d1.nadi > 0) {
                    int x0 = xOf(d0.jamKe), y0 = y + (int)((maxV-d0.nadi)/(double)(maxV-minV)*h);
                    int x1 = xOf(d1.jamKe), y1 = y + (int)((maxV-d1.nadi)/(double)(maxV-minV)*h);
                    g.drawLine(x0, y0, x1, y1);
                }
            }
            g.setStroke(new BasicStroke(1f));

            for (ObsData d : obsList) {
                int xd = xOf(d.jamKe);
                // Nadi: titik (•)
                if (d.nadi > 0) {
                    int yd = y + (int)((maxV-d.nadi)/(double)(maxV-minV)*h);
                    boolean abnormal = d.nadi < 60 || d.nadi > 100;
                    g.setColor(abnormal ? Color.RED : Color.BLACK);
                    g.fillOval(xd-3, yd-3, 6, 6);
                }
                // TD: panah vertikal ↕
                if (d.sistole > 0 && d.diastole > 0) {
                    int yS = y + (int)((maxV-d.sistole) /(double)(maxV-minV)*h);
                    int yD = y + (int)((maxV-d.diastole)/(double)(maxV-minV)*h);
                    g.setColor(new Color(0,0,180));
                    g.setStroke(new BasicStroke(2f));
                    // Garis vertikal sistole→diastole
                    g.drawLine(xd, yS, xd, yD);
                    // Kepala panah atas (sistole)
                    g.fillPolygon(new int[]{xd-4, xd, xd+4}, new int[]{yS+6, yS, yS+6}, 3);
                    // Kepala panah bawah (diastole)
                    g.fillPolygon(new int[]{xd-4, xd, xd+4}, new int[]{yD-6, yD, yD-6}, 3);
                    g.setStroke(new BasicStroke(1f));
                }
            }
        }

        // ---- SUHU ----
        void drawSuhu(Graphics2D g) {
            int x = MARGIN_LEFT, y = ySuhu, w = totalWidth, h = H_SUHU;
            int vTicks = 5;
            double minV = 35.0, maxV = 40.0;

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.drawString("Suhu °C", x-80, y+h/2+3);

            drawSectionBorder(g, x, y, w, h);
            g.setFont(new Font("Arial", Font.PLAIN, 9));
            for (int t = 0; t <= vTicks; t++) {
                double v = minV + t;
                int yv = y + (int)((maxV - v) / (maxV - minV) * h);
                g.setColor(new Color(220,220,220));
                g.setStroke(new BasicStroke(0.5f));
                g.drawLine(x, yv, x+w, yv);
                g.setColor(v == 37.5 ? new Color(0,150,0) : Color.BLACK);
                g.setStroke(new BasicStroke(1f));
                g.drawString(String.format("%.0f", v), x-22, yv+4);
            }
            g.setColor(new Color(220,220,220));
            g.setStroke(new BasicStroke(0.5f));
            for (int hr = 1; hr < MAX_HOURS; hr++) g.drawLine(x+hr*COL_W, y, x+hr*COL_W, y+h);
            g.setStroke(new BasicStroke(1f));

            // Plot suhu: titik + garis
            g.setColor(new Color(200,0,0));
            g.setStroke(new BasicStroke(1.2f));
            for (int i = 1; i < obsList.size(); i++) {
                ObsData d0 = obsList.get(i-1), d1 = obsList.get(i);
                if (d0.suhu > 0 && d1.suhu > 0) {
                    int x0=xOf(d0.jamKe), y0=y+(int)((maxV-d0.suhu)/(maxV-minV)*h);
                    int x1=xOf(d1.jamKe), y1=y+(int)((maxV-d1.suhu)/(maxV-minV)*h);
                    g.drawLine(x0, y0, x1, y1);
                }
            }
            for (ObsData d : obsList) {
                if (d.suhu <= 0) continue;
                int xd = xOf(d.jamKe);
                int yd = y + (int)((maxV - d.suhu)/(maxV-minV)*h);
                boolean hi = d.suhu >= 37.5;
                g.setColor(hi ? Color.RED : new Color(200,0,0));
                g.fillOval(xd-3, yd-3, 6, 6);
            }
            g.setStroke(new BasicStroke(1f));
        }

        // ---- URIN ----
        void drawUrin(Graphics2D g) {
            int x = MARGIN_LEFT, y = yUrin, w = totalWidth;
            int rowH = H_URIN / 3;
            String[] labels = {"Volume", "Protein", "Aseton"};

            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.drawString("Urin", x-140, y+H_URIN/2);

            g.drawRect(x, y, w, H_URIN);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            for (int r = 0; r < 3; r++) {
                g.drawLine(x, y+r*rowH, x+w, y+r*rowH);
                g.drawString(labels[r], x-100, y+r*rowH+rowH/2+4);
            }
            // Grid
            g.setColor(new Color(220,220,220));
            g.setStroke(new BasicStroke(0.5f));
            for (int hr = 1; hr < MAX_HOURS; hr++) g.drawLine(x+hr*COL_W, y, x+hr*COL_W, y+H_URIN);
            g.setStroke(new BasicStroke(1f));

            // Plot urin
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 9));
            for (ObsData d : obsList) {
                int xd = xOf(d.jamKe);
                FontMetrics fm = g.getFontMetrics();
                if (!d.urineVol.isEmpty())   g.drawString(d.urineVol,   xd - fm.stringWidth(d.urineVol)/2,   y+     rowH/2+3);
                if (!d.urineProt.isEmpty())  g.drawString(d.urineProt,  xd - fm.stringWidth(d.urineProt)/2,  y+rowH+rowH/2+3);
                if (!d.urineAseton.isEmpty())g.drawString(d.urineAseton,xd - fm.stringWidth(d.urineAseton)/2,y+2*rowH+rowH/2+3);
            }
        }

        // ---- SUMBU WAKTU (X) di bawah / atas setiap section ----
        void drawTimeAxis(Graphics2D g) {
            int x = MARGIN_LEFT;
            int yAxis = yDJJ - 14;
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 10));
            g.drawString("Jam →", x - 55, yAxis);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            for (int hr = 0; hr <= MAX_HOURS; hr++) {
                int xh = x + hr * COL_W;
                g.drawLine(xh, yAxis+5, xh, yAxis+10);
                String lbl = String.valueOf(hr);
                g.drawString(lbl, xh - g.getFontMetrics().stringWidth(lbl)/2, yAxis+2);
            }
            // Waktu label dari data observasi
            g.setFont(new Font("Arial", Font.PLAIN, 8));
            for (ObsData d : obsList) {
                int xh = xOf(d.jamKe);
                g.drawString(d.waktu, xh - 10, yUrin + H_URIN + 14);
            }

            // Legend (di kanan luar grafik)
            int lx = x + totalWidth + 30, ly = yPembukaan + 20;
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 10));
            g.drawString("Keterangan:", lx, ly);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            ly += 16;
            drawX(g, lx+6, ly, new Color(180,0,0));
            g.setColor(new Color(180,0,0));
            g.drawString(" X = Pembukaan serviks", lx+16, ly+4);
            ly += 16;
            drawO(g, lx+6, ly, new Color(0,0,180));
            g.setColor(new Color(0,0,180));
            g.drawString(" O = Penurunan kepala", lx+16, ly+4);
            ly += 16;
            g.setColor(Color.BLACK);
            g.fillOval(lx+3, ly-3, 7, 7);
            g.drawString(" • = DJJ / Nadi", lx+16, ly+4);
            ly += 16;
            g.setColor(new Color(0,0,180));
            g.drawString(" ↕ = Tekanan Darah", lx+16, ly+4);
        }
    }
}
