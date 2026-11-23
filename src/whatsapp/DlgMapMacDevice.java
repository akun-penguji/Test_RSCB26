package whatsapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.koneksiDB;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * Form mapping MAC PC ↔ device gowa. Style mengikuti DlgBangsal:
 *  - Top:    panelGlass8 — form input
 *  - Center: Scroll + Table — daftar mapping
 *  - Bottom: panelGlass5 — CRUD buttons + status
 *
 * <p>Tombol pakai widget.Button dgn icon 16x16, mnemonic, tooltip Alt+X, size 100x30.
 *
 * <p>Catatan koneksi: {@code koneksiDB.condb()} adalah <b>singleton</b>. JANGAN
 * tutup di try-with-resources — itu akan close koneksi global utk semua modul.
 * Hanya tutup PreparedStatement + ResultSet.
 */
public class DlgMapMacDevice extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;

    public DlgMapMacDevice(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        tabMode = new DefaultTableModel(null, new Object[]{
            "MAC Address", "Device ID", "Kd Bangsal", "Keterangan", "Created", "Updated"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        initComponents();

        tbMap.setModel(tabMode);
        tbMap.setPreferredScrollableViewportSize(new Dimension(700, 200));
        tbMap.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {140, 160, 90, 200, 130, 130};
        for (int i = 0; i < widths.length && i < tabMode.getColumnCount(); i++) {
            TableColumn c = tbMap.getColumnModel().getColumn(i);
            c.setPreferredWidth(widths[i]);
            c.setMinWidth(widths[i]);
        }
        tbMap.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        loadDeviceList();
        refreshData();
    }

    /** Layout manual mengikuti DlgBangsal: input top → table center → tombol bottom. */
    private void initComponents() {
        setTitle("Mapping MAC Address ↔ Device WA :: ");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(960, 600);
        setLocationRelativeTo(getParent());

        internalFrame1 = new widget.InternalFrame();
        internalFrame1.setBorder(null);
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        // ── PAGE_START: Form Input ────────────────────────────────────────────
        panelGlass8 = new widget.panelisi();
        panelGlass8.setBorder(javax.swing.BorderFactory.createTitledBorder("Form Input"));
        panelGlass8.setLayout(null);
        panelGlass8.setPreferredSize(new Dimension(900, 95));

        lblMac = new widget.Label();
        lblMac.setText("MAC Address :");
        lblMac.setHorizontalAlignment(widget.Label.RIGHT);
        lblMac.setBounds(20, 18, 100, 23);
        panelGlass8.add(lblMac);

        TMac = new widget.TextBox();
        TMac.setBounds(125, 18, 200, 23);
        TMac.setToolTipText("Format AA:BB:CC:DD:EE:FF");
        panelGlass8.add(TMac);

        BtnDetekMac = new widget.Button();
        BtnDetekMac.setText("Detek MAC");
        BtnDetekMac.setToolTipText("Ambil MAC NIC utama komputer ini");
        BtnDetekMac.setBounds(330, 18, 110, 23);
        BtnDetekMac.addActionListener(e -> doDetekMac());
        panelGlass8.add(BtnDetekMac);

        lblDev = new widget.Label();
        lblDev.setText("Device ID :");
        lblDev.setHorizontalAlignment(widget.Label.RIGHT);
        lblDev.setBounds(455, 18, 80, 23);
        panelGlass8.add(lblDev);

        cmbDevice = new widget.ComboBox();
        cmbDevice.setBounds(540, 18, 300, 23);
        cmbDevice.setToolTipText("List device gowa (auto-load dari /devices)");
        panelGlass8.add(cmbDevice);

        BtnRefreshDev = new widget.Button();
        BtnRefreshDev.setText("↻");
        BtnRefreshDev.setToolTipText("Reload daftar device dari gowa");
        BtnRefreshDev.setBounds(845, 18, 40, 23);
        BtnRefreshDev.addActionListener(e -> loadDeviceList());
        panelGlass8.add(BtnRefreshDev);

        lblBgsl = new widget.Label();
        lblBgsl.setText("Kd Bangsal :");
        lblBgsl.setHorizontalAlignment(widget.Label.RIGHT);
        lblBgsl.setBounds(20, 50, 100, 23);
        panelGlass8.add(lblBgsl);

        TBangsal = new widget.TextBox();
        TBangsal.setBounds(125, 50, 120, 23);
        TBangsal.setToolTipText("Opsional — referensi bangsal");
        panelGlass8.add(TBangsal);

        lblKet = new widget.Label();
        lblKet.setText("Keterangan :");
        lblKet.setHorizontalAlignment(widget.Label.RIGHT);
        lblKet.setBounds(250, 50, 85, 23);
        panelGlass8.add(lblKet);

        TKeterangan = new widget.TextBox();
        TKeterangan.setBounds(340, 50, 545, 23);
        TKeterangan.setToolTipText("Label deskriptif: NS1-PC-1, IGD-Triase, dll.");
        panelGlass8.add(TKeterangan);

        // ── CENTER: Tabel ────────────────────────────────────────────────────
        tbMap = new widget.Table();
        Scroll = new widget.ScrollPane();
        Scroll.setViewportView(tbMap);
        tbMap.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int r = tbMap.getSelectedRow();
            if (r < 0) return;
            TMac.setText(String.valueOf(tabMode.getValueAt(r, 0)));
            selectDeviceInCombo(String.valueOf(tabMode.getValueAt(r, 1)));
            TBangsal.setText(String.valueOf(tabMode.getValueAt(r, 2) == null ? "" : tabMode.getValueAt(r, 2)));
            TKeterangan.setText(String.valueOf(tabMode.getValueAt(r, 3) == null ? "" : tabMode.getValueAt(r, 3)));
        });

        // ── PAGE_END: CRUD buttons panel (style DlgBangsal) ───────────────────
        panelGlass5 = new widget.panelisi();
        panelGlass5.setPreferredSize(new Dimension(960, 55));
        panelGlass5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan = makeButton("Simpan", 'S', "Alt+S", "/picture/save-16x16.png");
        BtnSimpan.addActionListener(e -> simpanMapping());
        panelGlass5.add(BtnSimpan);

        BtnBaru = makeButton("Baru", 'B', "Alt+B", "/picture/add-file-16x16.png");
        BtnBaru.addActionListener(e -> resetForm());
        panelGlass5.add(BtnBaru);

        BtnHapus = makeButton("Hapus", 'H', "Alt+H", "/picture/delete-16x16.png");
        BtnHapus.addActionListener(e -> hapusMapping());
        panelGlass5.add(BtnHapus);

        BtnRefresh = makeButton("Refresh", 'R', "Alt+R", "/picture/refresh.png");
        BtnRefresh.addActionListener(e -> refreshData());
        panelGlass5.add(BtnRefresh);

        BtnKeluar = makeButton("Keluar", 'K', "Alt+K", "/picture/exit.png");
        BtnKeluar.addActionListener(e -> dispose());
        panelGlass5.add(BtnKeluar);

        lblStatus = new widget.Label();
        lblStatus.setText(" ");
        lblStatus.setForeground(new Color(0, 100, 0));
        lblStatus.setPreferredSize(new Dimension(380, 30));
        panelGlass5.add(lblStatus);

        // ── Assemble layout ───────────────────────────────────────────────────
        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_START);
        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);
        internalFrame1.add(panelGlass5, java.awt.BorderLayout.PAGE_END);

        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
    }

    /** Factory tombol style Khanza (mirip DlgBangsal): icon 16x16, mnemonic, tooltip,
     *  preferredSize 100x30 supaya konsisten dengan form CRUD lainnya. */
    private widget.Button makeButton(String text, char mnemonic, String tooltip, String iconPath) {
        widget.Button b = new widget.Button();
        try {
            java.net.URL url = getClass().getResource(iconPath);
            if (url != null) b.setIcon(new javax.swing.ImageIcon(url));
        } catch (Exception ignored) { /* icon opsional */ }
        b.setMnemonic(mnemonic);
        b.setText(text);
        b.setToolTipText(tooltip);
        b.setPreferredSize(new Dimension(100, 30));
        return b;
    }

    // ── Handler aksi ──────────────────────────────────────────────────────────

    private void doDetekMac() {
        String mac = koneksiDB.getLocalMacAddress();
        if (mac.isEmpty()) {
            setStatus("Gagal deteksi MAC — tidak ada NIC fisik aktif", Color.RED);
        } else {
            TMac.setText(mac);
            setStatus("MAC terdeteksi: " + mac, new Color(0, 100, 0));
        }
    }

    private void loadDeviceList() {
        cmbDevice.removeAllItems();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            String url = koneksiDB.URLWAGATEWAY();
            String user = koneksiDB.USERWAGATEWAY();
            String pass = koneksiDB.PASSWAGATEWAY();
            if (url == null || url.isEmpty()) {
                setStatus("URLWAGATEWAY belum di-set di database.xml", Color.RED);
                return;
            }
            String basic = "Basic " + Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());
            HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
            HttpResponse<String> resp = http.send(
                HttpRequest.newBuilder(URI.create(url.replaceAll("/$", "") + "/devices"))
                    .timeout(Duration.ofSeconds(8))
                    .header("Authorization", basic)
                    .GET().build(),
                HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() != 200) {
                setStatus("Gowa HTTP " + resp.statusCode(), Color.RED);
                return;
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(resp.body());
            JsonNode results = root.get("results");
            if (results == null || !results.isArray()) {
                setStatus("Format /devices tidak dikenali", Color.RED);
                return;
            }
            List<String[]> items = new ArrayList<>();
            for (JsonNode d : results) {
                JsonNode idNode = d.path("id");
                JsonNode nameNode = d.path("display_name");
                JsonNode stateNode = d.path("state");
                String id = idNode.isMissingNode() || idNode.isNull() ? "" : idNode.asText();
                String name = nameNode.isMissingNode() || nameNode.isNull() ? "" : nameNode.asText();
                String state = stateNode.isMissingNode() || stateNode.isNull() ? "" : stateNode.asText();
                if (id.isEmpty()) continue;
                String label = id + " — " + (name.isEmpty() ? "(no name)" : name) + " (" + state + ")";
                items.add(new String[]{id, label});
            }
            items.sort(Comparator.comparing(a -> a[0]));
            for (String[] it : items) cmbDevice.addItem(it[1]);
            setStatus("Loaded " + items.size() + " device dari gowa", new Color(0, 100, 0));
        } catch (Throwable ex) {
            setStatus("Gagal load device: " + ex, Color.RED);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void selectDeviceInCombo(String deviceId) {
        for (int i = 0; i < cmbDevice.getItemCount(); i++) {
            String item = String.valueOf(cmbDevice.getItemAt(i));
            if (item.equals(deviceId) || item.startsWith(deviceId + " ")) {
                cmbDevice.setSelectedIndex(i);
                return;
            }
        }
        cmbDevice.addItem(deviceId);
        cmbDevice.setSelectedItem(deviceId);
    }

    private String getSelectedDeviceId() {
        Object sel = cmbDevice.getSelectedItem();
        if (sel == null) return "";
        String s = sel.toString();
        int sp = s.indexOf(" — ");
        if (sp > 0) return s.substring(0, sp).trim();
        sp = s.indexOf(' ');
        if (sp > 0) return s.substring(0, sp).trim();
        return s.trim();
    }

    private void refreshData() {
        tabMode.setRowCount(0);
        // PENTING: condb() = singleton shared. JANGAN tutup Connection.
        Connection cx = koneksiDB.condb();
        try (PreparedStatement ps = cx.prepareStatement(
                "SELECT mac_address, device_id, IFNULL(kd_bangsal,'') AS kd_bangsal, " +
                "       IFNULL(keterangan,'') AS keterangan, " +
                "       IFNULL(created_at,'') AS created_at, IFNULL(updated_at,'') AS updated_at " +
                "FROM wa_mac_device_mapping ORDER BY device_id, mac_address");
             ResultSet rs = ps.executeQuery()) {
            int cnt = 0;
            while (rs.next()) {
                tabMode.addRow(new Object[]{
                    rs.getString("mac_address"),
                    rs.getString("device_id"),
                    rs.getString("kd_bangsal"),
                    rs.getString("keterangan"),
                    rs.getString("created_at"),
                    rs.getString("updated_at")
                });
                cnt++;
            }
            setStatus("Total " + cnt + " mapping", new Color(0, 100, 0));
        } catch (Throwable ex) {
            setStatus("Query gagal: " + ex, Color.RED);
        }
    }

    private void simpanMapping() {
        String mac = TMac.getText().trim().toUpperCase();
        String dev = getSelectedDeviceId();
        String ban = TBangsal.getText().trim();
        String ket = TKeterangan.getText().trim();

        if (mac.isEmpty()) {
            JOptionPane.showMessageDialog(this, "MAC Address wajib diisi. Klik [Detek MAC].", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!mac.matches("^[0-9A-F]{2}(:[0-9A-F]{2}){5}$")) {
            JOptionPane.showMessageDialog(this, "Format MAC salah. Pakai AA:BB:CC:DD:EE:FF (uppercase, dipisah ':').", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (dev.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih Device ID dari combobox. Klik [↻] kalau kosong.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Connection cx = koneksiDB.condb();
        try (PreparedStatement ps = cx.prepareStatement(
                "INSERT INTO wa_mac_device_mapping (mac_address, device_id, kd_bangsal, keterangan) " +
                "VALUES (?,?,?,?) " +
                "ON DUPLICATE KEY UPDATE device_id=VALUES(device_id), kd_bangsal=VALUES(kd_bangsal), " +
                "                       keterangan=VALUES(keterangan)")) {
            ps.setString(1, mac);
            ps.setString(2, dev);
            ps.setString(3, ban.isEmpty() ? null : ban);
            ps.setString(4, ket.isEmpty() ? null : ket);
            int n = ps.executeUpdate();
            setStatus("Saved (rows " + n + ") — " + mac + " → " + dev, new Color(0, 100, 0));
            refreshData();
        } catch (Exception ex) {
            setStatus("Save gagal: " + ex.getMessage(), Color.RED);
        }
    }

    private void hapusMapping() {
        String mac = TMac.getText().trim().toUpperCase();
        if (mac.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih baris di tabel atau isi MAC dulu", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Hapus mapping untuk MAC " + mac + " ?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        Connection cx = koneksiDB.condb();
        try (PreparedStatement ps = cx.prepareStatement(
                "DELETE FROM wa_mac_device_mapping WHERE mac_address=?")) {
            ps.setString(1, mac);
            int n = ps.executeUpdate();
            setStatus("Deleted " + n + " row", new Color(0, 100, 0));
            resetForm();
            refreshData();
        } catch (Exception ex) {
            setStatus("Delete gagal: " + ex.getMessage(), Color.RED);
        }
    }

    private void resetForm() {
        TMac.setText("");
        if (cmbDevice.getItemCount() > 0) cmbDevice.setSelectedIndex(-1);
        TBangsal.setText("");
        TKeterangan.setText("");
        tbMap.clearSelection();
        setStatus("Form direset", Color.GRAY);
    }

    private void setStatus(String msg, Color c) {
        lblStatus.setText(msg);
        lblStatus.setForeground(c);
        System.out.println("[DlgMapMacDevice] " + msg);
    }

    // ── Widget fields ─────────────────────────────────────────────────────────
    private widget.InternalFrame internalFrame1;
    private widget.ScrollPane Scroll;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass5;
    private widget.Table tbMap;
    private widget.Label lblMac, lblDev, lblBgsl, lblKet, lblStatus;
    private widget.TextBox TMac, TBangsal, TKeterangan;
    private widget.ComboBox cmbDevice;
    private widget.Button BtnDetekMac, BtnRefreshDev, BtnSimpan, BtnBaru, BtnHapus, BtnRefresh, BtnKeluar;
}
