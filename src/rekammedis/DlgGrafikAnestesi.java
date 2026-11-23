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
import javax.swing.table.DefaultTableModel;

/**
 * Grafik Monitoring Anestesi / Sedasi
 *
 * Simbol standar chart anestesi:
 *  × Anesthesia event
 *  ⊙ Operation event
 *  >< B/P Cuff Pressure (NIBP sistol-diastol)
 *  ⊢⊣ Arterial Line Pressure
 *  ▶ Mean Arterial Pressure (MAP)
 *  • Pulse (nadi)
 *  ○ Spontaneous Respiration
 *  ⊗ Assisted Respiration
 *  → Controlled Respiration
 */
public class DlgGrafikAnestesi extends JDialog implements Printable {

    private Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;

    // Patient info
    private String noRawat = "", nmPasien = "";

    // UI
    private ChartPanel chartPanel;
    private JScrollPane chartScroll;
    private JLabel lblPasien;

    // Data model
    static class MonitorData {
        String waktu;       // HH:mm
        int menit;          // offset menit dari titik awal
        int sistolNIBP;
        int diastolNIBP;
        int sistolAline;
        int diastolAline;
        int map;
        int nadi;
        String respMode;    // spont / assisted / controlled / -
        int respRate;
        String eventType;   // anesthesia / operation / -
        String keterangan;
    }

    private List<MonitorData> dataList = new ArrayList<>();

    // ====== KONSTRUKTOR ======
    public DlgGrafikAnestesi(Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Grafik Monitoring Anestesi");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        // Full screen
        java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, screen.width, screen.height);
        initUI();
    }

    public void setNoRawat(String noRawat, String nmPasien) {
        this.noRawat = noRawat;
        this.nmPasien = nmPasien;
        lblPasien.setText("  No. Rawat: " + noRawat + "    Pasien: " + nmPasien);
        if (noRawat != null && !noRawat.trim().isEmpty()) {
            loadFromDB();
        } else {
            loadDummyData();
        }
        chartPanel.repaint();
    }

    // ====== INIT UI ======
    private void initUI() {
        setLayout(new BorderLayout());

        // --- Toolbar ---
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        toolbar.setBackground(new Color(45, 62, 80));
        toolbar.setPreferredSize(new Dimension(1340, 36));

        JButton btnCetak = makeBtn("Cetak", new Color(39, 174, 96));
        JButton btnRefresh = makeBtn("Refresh", new Color(41, 128, 185));
        JButton btnTutup = makeBtn("Tutup", new Color(192, 57, 43));

        btnCetak.addActionListener(e -> doPrint());
        btnRefresh.addActionListener(e -> {
            if (noRawat != null && !noRawat.trim().isEmpty()) loadFromDB();
            else loadDummyData();
            chartPanel.repaint();
        });
        btnTutup.addActionListener(e -> dispose());

        toolbar.add(btnCetak);
        toolbar.add(btnRefresh);
        toolbar.add(btnTutup);

        // --- Patient info bar ---
        JPanel infoBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 3));
        infoBar.setBackground(new Color(236, 240, 241));
        infoBar.setPreferredSize(new Dimension(1340, 26));
        lblPasien = new JLabel("  No. Rawat: -    Pasien: -");
        lblPasien.setFont(new Font("Tahoma", Font.BOLD, 11));
        infoBar.add(lblPasien);

        // --- Chart + Legend ---
        chartPanel = new ChartPanel();
        chartPanel.setPreferredSize(new Dimension(1140, 600));
        chartScroll = new JScrollPane(chartPanel,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        LegendPanel legend = new LegendPanel();
        legend.setPreferredSize(new Dimension(180, 600));

        JPanel center = new JPanel(new BorderLayout(2, 0));
        center.add(chartScroll, BorderLayout.CENTER);
        center.add(legend, BorderLayout.EAST);

        JPanel north = new JPanel(new BorderLayout());
        north.add(toolbar, BorderLayout.NORTH);
        north.add(infoBar, BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);

        // Load dummy data to show chart shape
        loadDummyData();
    }

    private JButton makeBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(new Font("Tahoma", Font.BOLD, 11));
        b.setForeground(Color.WHITE);
        b.setBackground(bg);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(90, 26));
        return b;
    }

    // ====== LOAD DATA ======
    private void loadDummyData() {
        dataList.clear();
        // Dummy: typical anesthesia monitoring every 5 minutes for ~2.5 hours
        // Format: waktu, menit, sistolNIBP, diastolNIBP, sistolAline, diastolAline, map, nadi, respMode, respRate, event, ket
        int[][] vals = {
            // min, sNIBP, dNIBP, sAline, dAline, map, nadi, resp_rate
            {  0, 130, 80,   0,   0, 97, 78, 16},
            {  5, 128, 78,   0,   0, 95, 76, 14},
            { 10, 120, 75, 122,  76, 91, 72, 12},
            { 15, 115, 72, 118,  74, 89, 70,  0},   // intubasi — controlled resp start
            { 20, 110, 70, 112,  70, 84, 68,  0},
            { 25, 108, 68, 110,  68, 82, 66,  0},
            { 30, 105, 65, 108,  66, 80, 64,  0},
            { 35, 108, 68, 110,  68, 82, 66,  0},
            { 40, 112, 70, 114,  70, 85, 68,  0},
            { 45, 118, 72, 120,  74, 89, 70,  0},
            { 50, 122, 75, 124,  76, 92, 72,  0},
            { 55, 125, 78, 126,  78, 94, 74,  0},
            { 60, 120, 75, 122,  76, 91, 72,  0},
            { 65, 118, 73, 120,  74, 89, 70,  0},
            { 70, 115, 70, 116,  72, 87, 68,  0},
            { 75, 112, 68, 114,  70, 85, 66,  0},
            { 80, 110, 66, 112,  68, 83, 65,  0},
            { 85, 115, 70, 116,  72, 86, 68,  0},
            { 90, 120, 74, 122,  76, 91, 70, 12},   // anestesi dilepas — spont resp
            { 95, 125, 78, 126,  78, 94, 74, 14},
            {100, 128, 80,   0,   0, 96, 76, 16},
            {105, 126, 78,   0,   0, 94, 74, 16},
            {110, 122, 76,   0,   0, 91, 72, 15},
            {115, 118, 74,   0,   0, 89, 70, 14},
            {120, 115, 72,   0,   0, 86, 68, 14},
            {125, 118, 74,   0,   0, 89, 70, 15},
            {130, 122, 76,   0,   0, 91, 72, 16},
            {135, 124, 78,   0,   0, 93, 74, 16},
            {140, 126, 80,   0,   0, 95, 76, 15},
        };

        String[] respModes = {
            "spont","spont","assisted","controlled","controlled","controlled",
            "controlled","controlled","controlled","controlled","controlled",
            "controlled","controlled","controlled","controlled","controlled",
            "controlled","controlled","spont","spont","spont","spont",
            "spont","spont","spont","spont","spont","spont","spont"
        };
        String[] events = {
            "anesthesia","","","operation","","","","","","","",
            "","","","","","","","","","anesthesia","","","","","","","",""
        };

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);

        for (int i = 0; i < vals.length; i++) {
            MonitorData d = new MonitorData();
            cal.set(Calendar.MINUTE, vals[i][0]);
            d.waktu = String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY),
                                                 cal.get(Calendar.MINUTE));
            d.menit = vals[i][0];
            d.sistolNIBP = vals[i][1];
            d.diastolNIBP = vals[i][2];
            d.sistolAline = vals[i][3];
            d.diastolAline = vals[i][4];
            d.map = vals[i][5];
            d.nadi = vals[i][6];
            d.respRate = vals[i][7];
            d.respMode = respModes[i];
            d.eventType = events[i];
            d.keterangan = "";
            dataList.add(d);
        }
        if (chartPanel != null) chartPanel.repaint();
    }

    private void loadFromDB() {
        dataList.clear();
        try {
            String sql = "SELECT waktu,sistol_nibp,diastol_nibp,sistol_aline,diastol_aline," +
                         "map_val,nadi,resp_mode,resp_rate,event_type,keterangan " +
                         "FROM rm_sedasi_monitoring " +
                         "WHERE no_rawat=? ORDER BY waktu ASC";
            ps = koneksi.prepareStatement(sql);
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            int menit0 = -1;
            while (rs.next()) {
                MonitorData d = new MonitorData();
                d.waktu = rs.getString("waktu");
                // parse HH:MM to minutes
                String[] parts = d.waktu.split(":");
                int hh = Integer.parseInt(parts[0]);
                int mm = Integer.parseInt(parts[1]);
                int totalMin = hh * 60 + mm;
                if (menit0 < 0) menit0 = totalMin;
                d.menit = totalMin - menit0;
                d.sistolNIBP = rs.getInt("sistol_nibp");
                d.diastolNIBP = rs.getInt("diastol_nibp");
                d.sistolAline = rs.getInt("sistol_aline");
                d.diastolAline = rs.getInt("diastol_aline");
                d.map = rs.getInt("map_val");
                d.nadi = rs.getInt("nadi");
                d.respMode = rs.getString("resp_mode");
                d.respRate = rs.getInt("resp_rate");
                d.eventType = rs.getString("event_type");
                d.keterangan = rs.getString("keterangan");
                dataList.add(d);
            }
        } catch (Exception e) {
            System.out.println("loadFromDB grafik: " + e);
            loadDummyData(); // fallback to dummy if table not yet created
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {}
        }
        if (dataList.isEmpty()) loadDummyData();
    }

    // ====== CHART PANEL ======
    class ChartPanel extends JPanel {

        // Chart geometry
        static final int MARGIN_LEFT   = 55;
        static final int MARGIN_RIGHT  = 20;
        static final int MARGIN_TOP    = 40;
        static final int MARGIN_BOTTOM = 55;

        static final int Y_MAX = 300;
        static final int Y_MIN = 0;
        static final int Y_STEP = 20;       // grid lines every 20 units
        static final int X_INTERVAL = 5;    // minutes per column

        ChartPanel() {
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (dataList.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Determine X range
            int minMin = dataList.get(0).menit;
            int maxMin = dataList.get(dataList.size() - 1).menit;
            // Extend a bit
            int totalMin = Math.max(maxMin - minMin + X_INTERVAL, 60);

            int chartW = getWidth() - MARGIN_LEFT - MARGIN_RIGHT;
            int chartH = getHeight() - MARGIN_TOP - MARGIN_BOTTOM;

            // Compute preferred width if data extends beyond visible area
            int neededW = MARGIN_LEFT + MARGIN_RIGHT + (totalMin / X_INTERVAL + 2) * 30;
            if (getPreferredSize().width != Math.max(neededW, 1100)) {
                setPreferredSize(new Dimension(Math.max(neededW, 1100), getHeight()));
                revalidate();
            }

            double xScale = (double) chartW / totalMin;    // px per minute
            double yScale = (double) chartH / (Y_MAX - Y_MIN); // px per unit

            // Helper lambdas
            // toX: converts minute offset to pixel x
            // toY: converts value to pixel y (inverted)

            // === Draw grid ===
            g2.setColor(new Color(220, 220, 220));
            g2.setStroke(new BasicStroke(0.5f));
            // Horizontal grid lines
            for (int v = Y_MIN; v <= Y_MAX; v += Y_STEP) {
                int py = MARGIN_TOP + (int) ((Y_MAX - v) * yScale);
                g2.drawLine(MARGIN_LEFT, py, MARGIN_LEFT + chartW, py);
            }
            // Vertical grid lines every 5 minutes
            for (int m = 0; m <= totalMin; m += X_INTERVAL) {
                int px = MARGIN_LEFT + (int) (m * xScale);
                g2.drawLine(px, MARGIN_TOP, px, MARGIN_TOP + chartH);
            }

            // === Draw axes ===
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(MARGIN_LEFT, MARGIN_TOP, MARGIN_LEFT, MARGIN_TOP + chartH);
            g2.drawLine(MARGIN_LEFT, MARGIN_TOP + chartH, MARGIN_LEFT + chartW, MARGIN_TOP + chartH);

            // === Y axis labels ===
            g2.setFont(new Font("Tahoma", Font.PLAIN, 10));
            g2.setColor(Color.DARK_GRAY);
            for (int v = Y_MIN; v <= Y_MAX; v += Y_STEP) {
                int py = MARGIN_TOP + (int) ((Y_MAX - v) * yScale);
                g2.drawString(String.valueOf(v), 5, py + 4);
                // small tick
                g2.drawLine(MARGIN_LEFT - 3, py, MARGIN_LEFT, py);
            }

            // === X axis labels (time) ===
            g2.setFont(new Font("Tahoma", Font.PLAIN, 9));
            for (MonitorData d : dataList) {
                int m = d.menit - minMin;
                int px = MARGIN_LEFT + (int) (m * xScale);
                // tick
                g2.setColor(Color.DARK_GRAY);
                g2.drawLine(px, MARGIN_TOP + chartH, px, MARGIN_TOP + chartH + 4);
                // label - every other point to avoid crowding
                g2.setColor(Color.DARK_GRAY);
                int idx = dataList.indexOf(d);
                if (idx % 2 == 0) {
                    int sw = g2.getFontMetrics().stringWidth(d.waktu);
                    g2.drawString(d.waktu, px - sw/2, MARGIN_TOP + chartH + 15);
                }
            }

            // === Draw event markers at top ===
            drawEvents(g2, minMin, xScale, chartH);

            // === Draw NIBP (systole/diastole cuff: >< symbol pair) ===
            drawNIBP(g2, minMin, xScale, yScale);

            // === Draw Arterial Line (systole/diastole: ⊢⊣) ===
            drawAline(g2, minMin, xScale, yScale);

            // === Draw MAP (▶ filled triangle points, connected) ===
            drawMAP(g2, minMin, xScale, yScale);

            // === Draw Pulse (• filled circle, connected) ===
            drawPulse(g2, minMin, xScale, yScale);

            // === Draw Respiration (symbols at resp rate Y) ===
            drawResp(g2, minMin, xScale, yScale);

            // === Y axis title ===
            g2.setFont(new Font("Tahoma", Font.BOLD, 11));
            g2.setColor(Color.DARK_GRAY);
            AffineTransform origTx = g2.getTransform();
            g2.translate(12, MARGIN_TOP + chartH / 2);
            g2.rotate(-Math.PI / 2);
            g2.drawString("Tekanan Darah / Nadi", -60, 0);
            g2.setTransform(origTx);

            g2.dispose();
        }

        private int toX(int menit, int minMin, double xScale) {
            return MARGIN_LEFT + (int) ((menit - minMin) * xScale);
        }

        private int toY(int value, double yScale) {
            int v = Math.max(Y_MIN, Math.min(Y_MAX, value));
            return MARGIN_TOP + (int) ((Y_MAX - v) * yScale);
        }

        private void drawEvents(Graphics2D g2, int minMin, double xScale, int chartH) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            for (MonitorData d : dataList) {
                if (d.eventType == null || d.eventType.isEmpty() || d.eventType.equals("-")) continue;
                int px = toX(d.menit, minMin, xScale);
                if ("anesthesia".equalsIgnoreCase(d.eventType)) {
                    // × Anesthesia
                    g2.setColor(new Color(142, 68, 173));
                    g2.drawString("\u00D7", px - 5, MARGIN_TOP - 8);
                    // vertical dashed line
                    g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        10f, new float[]{4f, 4f}, 0f));
                    g2.drawLine(px, MARGIN_TOP, px, MARGIN_TOP + chartH);
                    g2.setStroke(new BasicStroke(1.5f));
                } else if ("operation".equalsIgnoreCase(d.eventType)) {
                    // ⊙ Operation
                    g2.setColor(new Color(211, 84, 0));
                    int r = 7;
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawOval(px - r, MARGIN_TOP - 20, r*2, r*2);
                    g2.fillOval(px - 2, MARGIN_TOP - 15, 4, 4);
                    // vertical dashed line
                    g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        10f, new float[]{4f, 4f}, 0f));
                    g2.drawLine(px, MARGIN_TOP, px, MARGIN_TOP + chartH);
                    g2.setStroke(new BasicStroke(1.5f));
                }
            }
        }

        private void drawNIBP(Graphics2D g2, int minMin, double xScale, double yScale) {
            g2.setColor(new Color(52, 73, 94));
            g2.setStroke(new BasicStroke(1.5f));
            // Connect mid-points (MAP equivalent of NIBP) with thin line
            List<Point> mids = new ArrayList<>();
            for (MonitorData d : dataList) {
                if (d.sistolNIBP <= 0) continue;
                mids.add(new Point(toX(d.menit, minMin, xScale),
                    toY((d.sistolNIBP + d.diastolNIBP) / 2, yScale)));
            }
            for (int i = 1; i < mids.size(); i++) {
                g2.setColor(new Color(52, 73, 94, 80));
                g2.drawLine(mids.get(i-1).x, mids.get(i-1).y, mids.get(i).x, mids.get(i).y);
            }
            // Draw >< symbol at each point
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            for (MonitorData d : dataList) {
                if (d.sistolNIBP <= 0) continue;
                int px = toX(d.menit, minMin, xScale);
                int pySis = toY(d.sistolNIBP, yScale);
                int pyDia = toY(d.diastolNIBP, yScale);
                g2.setColor(new Color(52, 73, 94));
                // vertical bar connecting sistol to diastol
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(px, pySis, px, pyDia);
                // > at systole
                drawGtLt(g2, px, pySis, true);
                // < at diastole
                drawGtLt(g2, px, pyDia, false);
                g2.setStroke(new BasicStroke(1.5f));
            }
        }

        /** Draw > (pointing right) or < (pointing left) as cuff marker */
        private void drawGtLt(Graphics2D g2, int cx, int cy, boolean gt) {
            int sz = 5;
            if (gt) {
                // > shape
                int[] xs = {cx - sz, cx + sz, cx - sz};
                int[] ys = {cy - sz, cy,       cy + sz};
                g2.drawPolyline(xs, ys, 3);
            } else {
                // < shape
                int[] xs = {cx + sz, cx - sz, cx + sz};
                int[] ys = {cy - sz, cy,       cy + sz};
                g2.drawPolyline(xs, ys, 3);
            }
        }

        private void drawAline(Graphics2D g2, int minMin, double xScale, double yScale) {
            g2.setColor(new Color(41, 128, 185));
            g2.setStroke(new BasicStroke(1.5f));
            for (MonitorData d : dataList) {
                if (d.sistolAline <= 0) continue;
                int px = toX(d.menit, minMin, xScale);
                int pySis = toY(d.sistolAline, yScale);
                int pyDia = toY(d.diastolAline, yScale);
                // vertical line
                g2.drawLine(px, pySis, px, pyDia);
                // ⊢ at top (sistol)
                g2.drawLine(px, pySis, px + 6, pySis);
                // ⊣ at bottom (diastol)
                g2.drawLine(px - 6, pyDia, px, pyDia);
            }
        }

        private void drawMAP(Graphics2D g2, int minMin, double xScale, double yScale) {
            Color mapColor = new Color(39, 174, 96);
            g2.setColor(mapColor);
            g2.setStroke(new BasicStroke(1.5f));
            // Connect with line
            MonitorData prev = null;
            for (MonitorData d : dataList) {
                if (d.map <= 0) continue;
                if (prev != null) {
                    g2.setColor(new Color(39, 174, 96, 100));
                    g2.drawLine(toX(prev.menit, minMin, xScale), toY(prev.map, yScale),
                                toX(d.menit, minMin, xScale),    toY(d.map, yScale));
                }
                prev = d;
            }
            // Draw ▶ symbol
            for (MonitorData d : dataList) {
                if (d.map <= 0) continue;
                int px = toX(d.menit, minMin, xScale);
                int py = toY(d.map, yScale);
                g2.setColor(mapColor);
                int[] xs = {px - 5, px + 6, px - 5};
                int[] ys = {py - 5, py,      py + 5};
                g2.fillPolygon(xs, ys, 3);
            }
        }

        private void drawPulse(Graphics2D g2, int minMin, double xScale, double yScale) {
            Color pulseColor = new Color(192, 57, 43);
            g2.setColor(pulseColor);
            g2.setStroke(new BasicStroke(1.5f));
            // Connect with line
            MonitorData prev = null;
            for (MonitorData d : dataList) {
                if (d.nadi <= 0) continue;
                if (prev != null) {
                    g2.setColor(new Color(192, 57, 43, 100));
                    g2.drawLine(toX(prev.menit, minMin, xScale), toY(prev.nadi, yScale),
                                toX(d.menit, minMin, xScale),    toY(d.nadi, yScale));
                }
                prev = d;
            }
            // Draw • filled circle
            for (MonitorData d : dataList) {
                if (d.nadi <= 0) continue;
                int px = toX(d.menit, minMin, xScale);
                int py = toY(d.nadi, yScale);
                g2.setColor(pulseColor);
                g2.fillOval(px - 4, py - 4, 8, 8);
            }
        }

        private void drawResp(Graphics2D g2, int minMin, double xScale, double yScale) {
            g2.setStroke(new BasicStroke(1.5f));
            for (MonitorData d : dataList) {
                if (d.respMode == null || d.respMode.equals("-")) continue;
                int px = toX(d.menit, minMin, xScale);
                int rate = d.respRate > 0 ? d.respRate : 14;
                // Show resp rate on Y-axis (scale by 3x to make visible)
                int py = toY(rate * 3, yScale);

                switch (d.respMode.toLowerCase()) {
                    case "spont":
                        // ○ open circle
                        g2.setColor(new Color(155, 89, 182));
                        g2.drawOval(px - 5, py - 5, 10, 10);
                        break;
                    case "assisted":
                        // ⊗ circle with X
                        g2.setColor(new Color(230, 126, 34));
                        g2.drawOval(px - 5, py - 5, 10, 10);
                        g2.drawLine(px - 3, py - 3, px + 3, py + 3);
                        g2.drawLine(px + 3, py - 3, px - 3, py + 3);
                        break;
                    case "controlled":
                        // → arrow
                        g2.setColor(new Color(26, 188, 156));
                        g2.drawLine(px - 6, py, px + 4, py);
                        int[] axs = {px + 4, px + 4, px + 9};
                        int[] ays = {py - 3, py + 3, py};
                        g2.fillPolygon(axs, ays, 3);
                        break;
                }
            }
        }
    }

    // ====== LEGEND PANEL ======
    class LegendPanel extends JPanel {
        LegendPanel() {
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Font titleFont = new Font("Tahoma", Font.BOLD, 11);
            Font itemFont  = new Font("Tahoma", Font.PLAIN, 10);
            Font symFont   = new Font("SansSerif", Font.BOLD, 13);

            g2.setFont(titleFont);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString("SYMBOLS :", 5, 22);

            int y = 45;
            int lineH = 24;

            // × Anesthesia
            g2.setFont(symFont); g2.setColor(new Color(142, 68, 173));
            g2.drawString("\u00D7", 5, y);
            g2.setFont(itemFont); g2.setColor(Color.DARK_GRAY);
            g2.drawString("Anesthesia", 22, y);
            y += lineH;

            // ⊙ Operation
            g2.setColor(new Color(211, 84, 0));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(5, y - 11, 12, 12);
            g2.fillOval(9, y - 7, 4, 4);
            g2.setFont(itemFont); g2.setColor(Color.DARK_GRAY);
            g2.drawString("Operation", 22, y);
            y += lineH;

            // >< B/P Cuff
            g2.setColor(new Color(52, 73, 94));
            g2.setFont(symFont);
            g2.drawString("><", 3, y);
            g2.setFont(itemFont); g2.setColor(Color.DARK_GRAY);
            g2.drawString("B/P Cuff Pressure", 22, y);
            y += lineH;

            // ⊢⊣ Arterial Line
            g2.setColor(new Color(41, 128, 185));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(5, y - 5, 10, y - 5);
            g2.drawLine(10, y - 10, 10, y);
            g2.drawLine(18, y - 10, 18, y);
            g2.drawLine(18, y - 5, 22, y - 5);
            g2.setFont(itemFont); g2.setColor(Color.DARK_GRAY);
            g2.drawString("Arterial Line", 28, y);
            y += lineH;

            // ▶ MAP
            g2.setColor(new Color(39, 174, 96));
            int[] txs = {5, 16, 5}; int[] tys = {y - 11, y - 5, y};
            g2.fillPolygon(txs, tys, 3);
            g2.setFont(itemFont); g2.setColor(Color.DARK_GRAY);
            g2.drawString("Mean Arterial Pressure", 22, y);
            y += lineH;

            // • Pulse
            g2.setColor(new Color(192, 57, 43));
            g2.fillOval(7, y - 8, 9, 9);
            g2.setFont(itemFont); g2.setColor(Color.DARK_GRAY);
            g2.drawString("Pulse", 22, y);
            y += lineH;

            // ○ Spont Resp
            g2.setColor(new Color(155, 89, 182));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(5, y - 10, 11, 11);
            g2.setFont(itemFont); g2.setColor(Color.DARK_GRAY);
            g2.drawString("Spont Resp.", 22, y);
            y += lineH;

            // ⊗ Assisted Resp
            g2.setColor(new Color(230, 126, 34));
            g2.drawOval(5, y - 10, 11, 11);
            g2.drawLine(7, y - 8, 13, y - 1);
            g2.drawLine(13, y - 8, 7, y - 1);
            g2.setFont(itemFont); g2.setColor(Color.DARK_GRAY);
            g2.drawString("Assisted Resp.", 22, y);
            y += lineH;

            // → Controlled Resp
            g2.setColor(new Color(26, 188, 156));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawLine(3, y - 5, 14, y - 5);
            int[] axs2 = {14, 14, 20}; int[] ays2 = {y - 9, y - 1, y - 5};
            g2.fillPolygon(axs2, ays2, 3);
            g2.setFont(itemFont); g2.setColor(Color.DARK_GRAY);
            g2.drawString("Controlled Resp.", 26, y);
            y += lineH + 10;

            // Divider
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawLine(0, y, 170, y);
            y += 15;

            // Scale note
            g2.setFont(new Font("Tahoma", Font.ITALIC, 9));
            g2.setColor(Color.GRAY);
            g2.drawString("Skala Y: mmHg / bpm", 5, y);
            y += 14;
            g2.drawString("Respirasi: rate \u00D73", 5, y);

            g2.dispose();
        }
    }

    // ====== PRINT ======
    private void doPrint() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        if (job.printDialog()) {
            try { job.print(); } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "Gagal mencetak: " + e.getMessage());
            }
        }
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
        if (pageIndex > 0) return NO_SUCH_PAGE;
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(pf.getImageableX(), pf.getImageableY());
        double sx = pf.getImageableWidth()  / chartPanel.getPreferredSize().getWidth();
        double sy = pf.getImageableHeight() / chartPanel.getPreferredSize().getHeight();
        double scale = Math.min(sx, sy);
        g2.scale(scale, scale);
        chartPanel.paint(g2);
        return PAGE_EXISTS;
    }

    /** Render chart to BufferedImage for embedding in HTML reports. */
    public java.awt.image.BufferedImage getChartImage(int w, int h) {
        chartPanel.setPreferredSize(new Dimension(w, h));
        chartPanel.setSize(w, h);
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        chartPanel.paint(g2);
        g2.dispose();
        return img;
    }
}
