package rekammedis;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.toedter.calendar.JDateChooser;
import fungsi.WarnaTableMonitoringObat;
import fungsi.koneksiDB;
import fungsi.akses;

/**
 * OPTIMIZED VERSION - Performance Improvements: 1. Prevent double/triple query
 * execution 2. Background thread for database operations 3. Debounced text
 * search (500ms delay) 4. Query optimization (remove DATE() function) 5.
 * Loading state management 6. Connection health check UPDATED: Added ket1-8
 * status columns from pemberian_obat table
 */
public class MonitoringPemberianObatPasien extends JFrame {

    private final Connection koneksi;
    private JTable tableMonitoring;
    private DefaultTableModel modelMonitoring;
    private JComboBox<String> cmbValidator, cmbStatusFilter;
    private JDateChooser dateFromChooser, dateToChooser;
    private JButton btnValidasi, btnHapusValidasi, btnRefresh, btnCari;
    private JTextField txtFilter;
    private String nipUserLogin = "", namaUserLogin = "";
    private String currentFilter = "";

    private boolean isLoadingData = false;
    private Timer searchTimer;

    public MonitoringPemberianObatPasien() {
        this.koneksi = koneksiDB.condb();
        if (koneksi == null) {
            JOptionPane.showMessageDialog(this, "Koneksi Database Gagal!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        detectUserLogin();
        initComponents();
        loadInitialData();
    }

    public MonitoringPemberianObatPasien(Connection connection) {
        this.koneksi = connection;
        detectUserLogin();
        initComponents();
        loadInitialData();
    }

    public MonitoringPemberianObatPasien(Connection connection, String nipLogin) {
        this.koneksi = connection;
        this.nipUserLogin = nipLogin;
        initComponents();
        loadInitialData();
    }

    public MonitoringPemberianObatPasien(String noRawatFilter) {
        this.koneksi = koneksiDB.condb();
        if (koneksi == null) {
            JOptionPane.showMessageDialog(this, "Koneksi Database Gagal!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        detectUserLogin();
        initComponents();
        loadValidatorList();

        if (!nipUserLogin.isEmpty()) {
            for (int i = 0; i < cmbValidator.getItemCount(); i++) {
                if (cmbValidator.getItemAt(i).contains(nipUserLogin)) {
                    cmbValidator.setSelectedIndex(i);
                    break;
                }
            }
        }

        if (noRawatFilter != null && !noRawatFilter.trim().isEmpty()) {
            txtFilter.setText(noRawatFilter.trim());
            currentFilter = noRawatFilter.trim();
            SwingUtilities.invokeLater(this::applyFilter);
        } else {
            SwingUtilities.invokeLater(this::loadMonitoringData);
        }
    }

    private void detectUserLogin() {
        if (akses.getjml2() >= 1) {
            nipUserLogin = akses.getkode();
            try (PreparedStatement ps = koneksi.prepareStatement("SELECT nama FROM petugas WHERE nip=?")) {
                ps.setString(1, nipUserLogin);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    namaUserLogin = rs.getString("nama");
                } else {
                    nipUserLogin = "";
                    namaUserLogin = "";
                    JOptionPane.showMessageDialog(this, "User login bukan petugas!\nSilakan pilih validator secara manual.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException e) {
                System.err.println("Error get nama petugas: " + e.getMessage());
            }
        }
    }

    private void initComponents() {
        setTitle("Monitoring & Validasi Pemberian Obat Pasien");
        setSize(1600, 900);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(createMonitoringPanel());
    }

    private void loadInitialData() {
        loadValidatorList();
        if (!nipUserLogin.isEmpty()) {
            for (int i = 0; i < cmbValidator.getItemCount(); i++) {
                if (cmbValidator.getItemAt(i).contains(nipUserLogin)) {
                    cmbValidator.setSelectedIndex(i);
                    break;
                }
            }
        }
        SwingUtilities.invokeLater(this::loadMonitoringData);
    }

    private JPanel createMonitoringPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createTitledBorder("Monitoring & Validasi Pemberian Obat Pasien (8 Jam)"));

        JLabel lblHeader = new JLabel("<html><h3>Daftar Pemberian Obat yang Perlu Divalidasi</h3><p>Pastikan obat telah diberikan kepada pasien melalui konfirmasi manual</p></html>");
        lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(lblHeader, BorderLayout.NORTH);

        JPanel controlPanel = new JPanel(new BorderLayout(5, 5));
        controlPanel.add(createControlPanel1(), BorderLayout.NORTH);
        controlPanel.add(createControlPanel2(), BorderLayout.CENTER);
        headerPanel.add(controlPanel, BorderLayout.CENTER);
        headerPanel.add(createInfoPanel(), BorderLayout.SOUTH);
        return headerPanel;
    }

    private JPanel createControlPanel1() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        panel.add(new JLabel("Validator:"));
        cmbValidator = new JComboBox<>();
        cmbValidator.setPreferredSize(new Dimension(250, 25));
        panel.add(cmbValidator);

        btnValidasi = createButton("✓ Validasi", new Color(100, 200, 100), e -> validasiPemberianObat());
        panel.add(btnValidasi);

        btnHapusValidasi = createButton("✗ Hapus Validasi", new Color(220, 53, 69), e -> hapusValidasi());
        panel.add(btnHapusValidasi);

        panel.add(new JSeparator(SwingConstants.VERTICAL));

        panel.add(new JLabel("🔍 Filter:"));
        txtFilter = new JTextField(20);
        txtFilter.setToolTipText("Cari: No.Rawat, Nama Pasien, atau Nama Obat");

        searchTimer = new Timer(500, e -> applyFilter());
        searchTimer.setRepeats(false);
        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                if (searchTimer.isRunning()) {
                    searchTimer.restart();
                } else {
                    searchTimer.start();
                }
            }
        });
        panel.add(txtFilter);

        btnCari = new JButton("Tampil Data");
        btnCari.setBackground(new Color(70, 130, 180));
        btnCari.setForeground(Color.WHITE);
        btnCari.setFont(new Font("Arial", Font.BOLD, 12));
        btnCari.addActionListener(e -> applyFilter());
        panel.add(btnCari);

        panel.add(new JSeparator(SwingConstants.VERTICAL));

        panel.add(new JLabel("Status:"));
        cmbStatusFilter = new JComboBox<>(new String[]{"Semua Data", "Belum Divalidasi", "Belum Lengkap", "Sudah Divalidasi"});

        cmbStatusFilter.addActionListener(e -> {
            if (!isLoadingData) {
                applyFilter();
            }
        });
        panel.add(cmbStatusFilter);

        btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> resetFilter());
        panel.add(btnRefresh);

        return panel;
    }

    private JPanel createControlPanel2() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        dateFromChooser = new JDateChooser();
        dateFromChooser.setPreferredSize(new Dimension(130, 25));
        dateFromChooser.setDateFormatString("dd-MM-yyyy");
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, -7);
        dateFromChooser.setDate(cal.getTime());

        dateToChooser = new JDateChooser();
        dateToChooser.setPreferredSize(new Dimension(130, 25));
        dateToChooser.setDateFormatString("dd-MM-yyyy");
        dateToChooser.setDate(new Date());

        panel.add(new JLabel("📅 Periode:"));
        panel.add(new JLabel("Dari:"));
        panel.add(dateFromChooser);
        panel.add(new JLabel("Sampai:"));
        panel.add(dateToChooser);

        JButton btnFilter = new JButton("Tampil Data");
        btnFilter.setBackground(new Color(70, 130, 180));
        btnFilter.setForeground(Color.WHITE);
        btnFilter.setFont(new Font("Arial", Font.BOLD, 12));
        btnFilter.addActionListener(e -> filterPeriodeData());
        panel.add(btnFilter);

        return panel;
    }

    private JButton createButton(String text, Color bg, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.addActionListener(listener);
        return btn;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Informasi"));
        panel.add(new JLabel("🟡 Kuning = Belum Divalidasi"));
        panel.add(new JLabel("🟠 Orange = Belum Lengkap (Ada obat yang sudah diberikan)"));
        panel.add(new JLabel("🟢 Hijau = Sudah Divalidasi | 💗 Pink = Jam sudah diberikan (Klik kanan pada kolom jam)"));
        return panel;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {"No Rawat", "No RM", "NAMA PASIEN", "TGL. PEMBERIAN", "NAMA OBAT", "DOSIS",
            "PEMBERIAN 1 ", "PEMBERIAN 2", "PEMBERIAN 3", "PEMBERIAN 4", "PEMBERIAN 5", "PEMBERIAN 6", "PEMBERIAN 7", "PEMBERIAN 8",
            "PETUGAS 1", "PETUGAS 2", "PETUGAS 3", "PETUGAS 4", "PETUGAS 5", "PETUGAS 6", "PETUGAS 7", "PETUGAS 8",
            "KAMAR / RUANG", "STATUS VALIDASI", "VALIDATOR", "WAKTU VALIDASI",
            "status_jam1", "status_jam2", "status_jam3", "status_jam4", "status_jam5", "status_jam6", "status_jam7", "status_jam8"};

        modelMonitoring = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableMonitoring = new JTable(modelMonitoring);
        tableMonitoring.setRowHeight(30);
        tableMonitoring.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableMonitoring.setDefaultRenderer(Object.class, new WarnaTableMonitoringObat());
        tableMonitoring.getTableHeader().setPreferredSize(new Dimension(0, 35));

        // Set header teks di tengah dan berwarna
        javax.swing.table.DefaultTableCellRenderer headerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        headerRenderer.setBackground(new Color(70, 130, 180));
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Arial", Font.BOLD, 12));

        for (int i = 0; i < tableMonitoring.getColumnModel().getColumnCount(); i++) {
            tableMonitoring.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
        // ========================================

        int[] widths = {130, 50, 200, 100, 300, 200,
            120, 120, 120, 120, 120, 120, 120, 120, // <<< Lebar Kolom Jam Pemberian 1 - 8
            90, 90, 90, 90, 90, 90, 90, 90, // <<< Lebar Kolom Petugas 1 - 8
            150, 100, 100, 150,
            0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < widths.length && i < tableMonitoring.getColumnCount(); i++) {
            tableMonitoring.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            if (i >= 26) {
                tableMonitoring.getColumnModel().getColumn(i).setMinWidth(0);
                tableMonitoring.getColumnModel().getColumn(i).setMaxWidth(0);
            }
        }

        addCellPopupMenu();

        JScrollPane scroll = new JScrollPane(tableMonitoring);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        return scroll;
    }

    private void addCellPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuSudahDiberikan = new JMenuItem("✓ Sudah Diberikan");
        JMenuItem menuBelumDiberikan = new JMenuItem("✗ Belum Diberikan");

        menuSudahDiberikan.addActionListener(e -> updateStatusPemberian("sudah"));
        menuBelumDiberikan.addActionListener(e -> updateStatusPemberian("belum"));

        popupMenu.add(menuSudahDiberikan);
        popupMenu.add(menuBelumDiberikan);

        tableMonitoring.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showPopup(e);
                }
            }

            private void showPopup(MouseEvent e) {
                int row = tableMonitoring.rowAtPoint(e.getPoint());
                int col = tableMonitoring.columnAtPoint(e.getPoint());

                if (row >= 0 && col >= 6 && col <= 13) {
                    String statusValidasi = tableMonitoring.getValueAt(row, 23).toString();
                    if (!statusValidasi.equals("Sudah")) {
                        tableMonitoring.setRowSelectionInterval(row, row);
                        tableMonitoring.setColumnSelectionInterval(col, col);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });
    }

    private void updateStatusPemberian(String status) {
        int row = tableMonitoring.getSelectedRow();
        int col = tableMonitoring.getSelectedColumn();

        if (row == -1 || col < 6 || col > 13) {
            return;
        }

        String noRawat = tableMonitoring.getValueAt(row, 0).toString();
        String tglPemberian = tableMonitoring.getValueAt(row, 3).toString();
        String namaObat = tableMonitoring.getValueAt(row, 4).toString();
        int jamKe = col - 5;

        try {
            String waktuSimpan = "";
            try (PreparedStatement ps = koneksi.prepareStatement(
                    "SELECT waktu_simpan FROM pemberian_obat "
                    + "WHERE no_rawat=? AND tgl_pemberian >= ? AND tgl_pemberian < DATE_ADD(?, INTERVAL 1 DAY) "
                    + "AND nama_obat=? LIMIT 1")) {
                ps.setString(1, noRawat);
                ps.setString(2, tglPemberian + " 00:00:00");
                ps.setString(3, tglPemberian);
                ps.setString(4, namaObat);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    waktuSimpan = rs.getString("waktu_simpan");
                }
            }

            if (waktuSimpan.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Data tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (PreparedStatement psCheck = koneksi.prepareStatement(
                    "SELECT COUNT(*) as jml FROM pelaksana_pemberian_obat WHERE no_rawat=? AND waktu_simpan_obat=?")) {
                psCheck.setString(1, noRawat);
                psCheck.setString(2, waktuSimpan);
                ResultSet rsCheck = psCheck.executeQuery();
                if (rsCheck.next() && rsCheck.getInt("jml") == 0) {
                    JOptionPane.showMessageDialog(this, "Data pelaksana pemberian obat belum ada!\nSilakan isi data pelaksana terlebih dahulu.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            String updateQuery = "UPDATE pelaksana_pemberian_obat SET status_pemberian_jam" + jamKe + "=? WHERE no_rawat=? AND waktu_simpan_obat=?";
            try (PreparedStatement ps = koneksi.prepareStatement(updateQuery)) {
                ps.setString(1, status);
                ps.setString(2, noRawat);
                ps.setString(3, waktuSimpan);
                int result = ps.executeUpdate();

                if (result > 0) {
                    updateStatusValidasiOtomatis(noRawat, waktuSimpan);
                    applyFilter();
                    JOptionPane.showMessageDialog(this, "Status pemberian jam " + jamKe + " berhasil diupdate menjadi: " + status.toUpperCase(), "Sukses", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateStatusValidasiOtomatis(String noRawat, String waktuSimpan) {
        try {
            String query = "SELECT status_pemberian_jam1, status_pemberian_jam2, status_pemberian_jam3, status_pemberian_jam4, "
                    + "status_pemberian_jam5, status_pemberian_jam6, status_pemberian_jam7, status_pemberian_jam8, status_validasi "
                    + "FROM pelaksana_pemberian_obat WHERE no_rawat=? AND waktu_simpan_obat=?";

            try (PreparedStatement ps = koneksi.prepareStatement(query)) {
                ps.setString(1, noRawat);
                ps.setString(2, waktuSimpan);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String currentStatus = rs.getString("status_validasi");
                    if ("Sudah".equals(currentStatus)) {
                        return;
                    }

                    boolean adaYangSudah = false;
                    for (int i = 1; i <= 8; i++) {
                        if ("sudah".equals(rs.getString("status_pemberian_jam" + i))) {
                            adaYangSudah = true;
                            break;
                        }
                    }

                    if (adaYangSudah) {
                        String updateStatus = "UPDATE pelaksana_pemberian_obat SET status_validasi='Belum Lengkap' "
                                + "WHERE no_rawat=? AND waktu_simpan_obat=? AND status_validasi='Belum'";
                        try (PreparedStatement psUpdate = koneksi.prepareStatement(updateStatus)) {
                            psUpdate.setString(1, noRawat);
                            psUpdate.setString(2, waktuSimpan);
                            psUpdate.executeUpdate();
                        }
                    } else {
                        String updateStatus = "UPDATE pelaksana_pemberian_obat SET status_validasi='Belum' "
                                + "WHERE no_rawat=? AND waktu_simpan_obat=? AND status_validasi='Belum Lengkap'";
                        try (PreparedStatement psUpdate = koneksi.prepareStatement(updateStatus)) {
                            psUpdate.setString(1, noRawat);
                            psUpdate.setString(2, waktuSimpan);
                            psUpdate.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadMonitoringData() {
        loadDataWithQuery(buildDefaultQuery(), null);
    }

    private void applyFilter() {
        currentFilter = txtFilter.getText().trim();
        String statusFilter = cmbStatusFilter.getSelectedItem().toString();

        StringBuilder query = new StringBuilder(buildBaseQuery());
        query.append("WHERE 1=1 ");

        boolean hasTextFilter = !currentFilter.isEmpty();
        if (hasTextFilter) {
            query.append("AND (p.no_rawat LIKE ? OR pas.nm_pasien LIKE ? OR p.nama_obat LIKE ?) ");
        }

        if (statusFilter.equals("Belum Divalidasi")) {
            query.append("AND pel.status_validasi = 'Belum' ");
        } else if (statusFilter.equals("Belum Lengkap")) {
            query.append("AND pel.status_validasi = 'Belum Lengkap' ");
        } else if (statusFilter.equals("Sudah Divalidasi")) {
            query.append("AND pel.status_validasi = 'Sudah' ");
        }

        query.append("ORDER BY p.tgl_pemberian DESC LIMIT 200");

        if (hasTextFilter) {
            String searchPattern = "%" + currentFilter + "%";
            loadDataWithQuery(query.toString(), new String[]{searchPattern, searchPattern, searchPattern});
        } else {
            loadDataWithQuery(query.toString(), null);
        }
    }

    private void resetFilter() {
        txtFilter.setText("");
        currentFilter = "";
        cmbStatusFilter.setSelectedIndex(0);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.DAY_OF_MONTH, -7);
        dateFromChooser.setDate(cal.getTime());
        dateToChooser.setDate(new Date());
        loadMonitoringData();
    }

    private void filterPeriodeData() {
        if (dateFromChooser.getDate() == null || dateToChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Pilih tanggal periode terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (dateFromChooser.getDate().after(dateToChooser.getDate())) {
            JOptionPane.showMessageDialog(this, "Tanggal 'Dari' tidak boleh lebih besar dari 'Sampai'!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String tglDari = sdf.format(dateFromChooser.getDate());
        String tglSampai = sdf.format(dateToChooser.getDate());
        String statusFilter = cmbStatusFilter.getSelectedItem().toString();

        StringBuilder query = new StringBuilder(buildBaseQuery());
        query.append("WHERE p.tgl_pemberian BETWEEN ? AND ? ");

        if (statusFilter.equals("Belum Divalidasi")) {
            query.append("AND pel.status_validasi = 'Belum' ");
        } else if (statusFilter.equals("Belum Lengkap")) {
            query.append("AND pel.status_validasi = 'Belum Lengkap' ");
        } else if (statusFilter.equals("Sudah Divalidasi")) {
            query.append("AND pel.status_validasi = 'Sudah' ");
        }

        query.append("ORDER BY p.tgl_pemberian DESC, p.waktu_simpan DESC");

        loadDataWithQuery(query.toString(), new String[]{tglDari, tglSampai});
    }

    private String buildBaseQuery() {
        return "SELECT p.no_rawat, rp.no_rkm_medis, pas.nm_pasien, p.tgl_pemberian, p.nama_obat, "
                + "CONCAT(p.dosis, ' (', COALESCE(p.cara_pemberian, '-'), ')') AS dosis, "
                + "p.jadwal_pemberian AS jam1, p.jadwal_pemberian2 AS jam2, p.jadwal_pemberian3 AS jam3, p.jadwal_pemberian4 AS jam4, "
                + "p.jadwal_pemberian5 AS jam5, p.jadwal_pemberian6 AS jam6, p.jadwal_pemberian7 AS jam7, p.jadwal_pemberian8 AS jam8, "
                + "p.ket1, p.ket2, p.ket3, p.ket4, p.ket5, p.ket6, p.ket7, p.ket8, "
                + "pe1.nama AS petugas_jam1, pe2.nama AS petugas_jam2, pe3.nama AS petugas_jam3, pe4.nama AS petugas_jam4, "
                + "pe5.nama AS petugas_jam5, pe6.nama AS petugas_jam6, pe7.nama AS petugas_jam7, pe8.nama AS petugas_jam8, p.nm_unit, "
                + "COALESCE(pel.status_validasi, 'Belum') AS status_validasi, "
                + "COALESCE(pel.nip_validator, '-') AS nip_validator, "
                + "COALESCE(pv.nama, '-') AS nama_validator, COALESCE(pel.waktu_validasi, '-') AS waktu_validasi, "
                + "COALESCE(pel.status_pemberian_jam1, 'belum') AS status_jam1, "
                + "COALESCE(pel.status_pemberian_jam2, 'belum') AS status_jam2, "
                + "COALESCE(pel.status_pemberian_jam3, 'belum') AS status_jam3, "
                + "COALESCE(pel.status_pemberian_jam4, 'belum') AS status_jam4, "
                + "COALESCE(pel.status_pemberian_jam5, 'belum') AS status_jam5, "
                + "COALESCE(pel.status_pemberian_jam6, 'belum') AS status_jam6, "
                + "COALESCE(pel.status_pemberian_jam7, 'belum') AS status_jam7, "
                + "COALESCE(pel.status_pemberian_jam8, 'belum') AS status_jam8 "
                + "FROM pemberian_obat p "
                + "LEFT JOIN pelaksana_pemberian_obat pel ON p.no_rawat = pel.no_rawat AND p.waktu_simpan = pel.waktu_simpan_obat "
                + "LEFT JOIN reg_periksa rp ON p.no_rawat = rp.no_rawat LEFT JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                + "LEFT JOIN petugas pe1 ON pe1.nip = pel.nip_petugas_jam1 LEFT JOIN petugas pe2 ON pe2.nip = pel.nip_petugas_jam2 "
                + "LEFT JOIN petugas pe3 ON pe3.nip = pel.nip_petugas_jam3 LEFT JOIN petugas pe4 ON pe4.nip = pel.nip_petugas_jam4 "
                + "LEFT JOIN petugas pe5 ON pe5.nip = pel.nip_petugas_jam5 LEFT JOIN petugas pe6 ON pe6.nip = pel.nip_petugas_jam6 "
                + "LEFT JOIN petugas pe7 ON pe7.nip = pel.nip_petugas_jam7 LEFT JOIN petugas pe8 ON pe8.nip = pel.nip_petugas_jam8 "
                + "LEFT JOIN petugas pv ON pv.nip = pel.nip_validator ";
    }

    private String buildDefaultQuery() {
        return buildBaseQuery() + "WHERE p.tgl_pemberian >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) ORDER BY p.tgl_pemberian DESC, p.waktu_simpan DESC LIMIT 200";
    }

    private void loadDataWithQuery(String query, String[] params) {
        if (koneksi == null || isLoadingData) {
            return;
        }

        checkConnectionHealth();
        isLoadingData = true;

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        btnCari.setEnabled(false);
        btnRefresh.setEnabled(false);
        btnValidasi.setEnabled(false);
        btnHapusValidasi.setEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private final java.util.List<Object[]> rows = new java.util.ArrayList<>();
            private String errorMessage = null;

            @Override
            protected Void doInBackground() throws Exception {
                try (PreparedStatement ps = koneksi.prepareStatement(query)) {
                    if (params != null) {
                        for (int i = 0; i < params.length; i++) {
                            ps.setString(i + 1, params[i]);
                        }
                    }

                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        // Format jam dengan status ket
                        String jam1 = formatJamWithKet(rs.getString("jam1"), rs.getString("ket1"));
                        String jam2 = formatJamWithKet(rs.getString("jam2"), rs.getString("ket2"));
                        String jam3 = formatJamWithKet(rs.getString("jam3"), rs.getString("ket3"));
                        String jam4 = formatJamWithKet(rs.getString("jam4"), rs.getString("ket4"));
                        String jam5 = formatJamWithKet(rs.getString("jam5"), rs.getString("ket5"));
                        String jam6 = formatJamWithKet(rs.getString("jam6"), rs.getString("ket6"));
                        String jam7 = formatJamWithKet(rs.getString("jam7"), rs.getString("ket7"));
                        String jam8 = formatJamWithKet(rs.getString("jam8"), rs.getString("ket8"));

                        Object[] row = {
                            rs.getString("no_rawat"),
                            rs.getString("no_rkm_medis"),
                            rs.getString("nm_pasien"),
                            rs.getString("tgl_pemberian"),
                            rs.getString("nama_obat"),
                            rs.getString("dosis"),
                            jam1, jam2, jam3, jam4, jam5, jam6, jam7, jam8,
                            formatNullable(rs.getString("petugas_jam1")),
                            formatNullable(rs.getString("petugas_jam2")),
                            formatNullable(rs.getString("petugas_jam3")),
                            formatNullable(rs.getString("petugas_jam4")),
                            formatNullable(rs.getString("petugas_jam5")),
                            formatNullable(rs.getString("petugas_jam6")),
                            formatNullable(rs.getString("petugas_jam7")),
                            formatNullable(rs.getString("petugas_jam8")),
                            rs.getString("nm_unit"),
                            rs.getString("status_validasi"),
                            rs.getString("nama_validator"),
                            rs.getString("waktu_validasi"),
                            rs.getString("status_jam1"),
                            rs.getString("status_jam2"),
                            rs.getString("status_jam3"),
                            rs.getString("status_jam4"),
                            rs.getString("status_jam5"),
                            rs.getString("status_jam6"),
                            rs.getString("status_jam7"),
                            rs.getString("status_jam8")
                        };
                        rows.add(row);
                    }
                } catch (SQLException e) {
                    errorMessage = e.getMessage();
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                modelMonitoring.setRowCount(0);

                if (errorMessage != null) {
                    JOptionPane.showMessageDialog(
                            MonitoringPemberianObatPasien.this,
                            "Error loading data: " + errorMessage,
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                } else {
                    for (Object[] row : rows) {
                        modelMonitoring.addRow(row);
                    }
                }

                setCursor(Cursor.getDefaultCursor());
                btnCari.setEnabled(true);
                btnRefresh.setEnabled(true);
                btnValidasi.setEnabled(true);
                btnHapusValidasi.setEnabled(true);
                isLoadingData = false;
            }
        };

        worker.execute();
    }

    private void checkConnectionHealth() {
        try {
            if (koneksi == null || koneksi.isClosed()) {
                JOptionPane.showMessageDialog(this,
                        "Koneksi database terputus!\n\nSilakan restart aplikasi.",
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
                dispose();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void validasiPemberianObat() {
        int row = tableMonitoring.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan divalidasi dari tabel!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String selectedValidator = cmbValidator.getSelectedItem().toString();
        if (selectedValidator.contains("──────") || selectedValidator.contains("Pilih")) {
            JOptionPane.showMessageDialog(this, "Pilih Validator yang valid!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String noRawat = tableMonitoring.getValueAt(row, 0).toString();
        String tglPemberian = tableMonitoring.getValueAt(row, 3).toString();
        String namaObat = tableMonitoring.getValueAt(row, 4).toString();

        StringBuilder infoJam = new StringBuilder();
        for (int i = 6; i <= 13; i++) {
            String jam = tableMonitoring.getValueAt(row, i).toString();
            if (!jam.equals("-")) {
                infoJam.append("Jam ").append(i - 5).append(": ").append(jam).append("\n");
            }
        }

        String nipValidator = selectedValidator.contains("(Login)") ? nipUserLogin : selectedValidator.split(" - ")[0];
        int confirm = JOptionPane.showConfirmDialog(this, String.format("Validasi Pemberian Obat:\n\nNo. Rawat: %s\nTgl: %s\nObat: %s\n\nJadwal Pemberian:\n%s\nValidator: %s\n\nApakah Anda yakin telah mengonfirmasi bahwa obat telah diberikan kepada pasien?",
                noRawat, tglPemberian, namaObat, infoJam, selectedValidator), "Konfirmasi Validasi", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            executeValidasi(noRawat, tglPemberian, namaObat, nipValidator, selectedValidator);
        }
    }

    private void executeValidasi(String noRawat, String tglPemberian, String namaObat, String nipValidator, String validatorName) {
        try {
            String waktuSimpanObat = "";
            try (PreparedStatement psGet = koneksi.prepareStatement(
                    "SELECT waktu_simpan FROM pemberian_obat "
                    + "WHERE no_rawat = ? AND tgl_pemberian >= ? AND tgl_pemberian < DATE_ADD(?, INTERVAL 1 DAY) "
                    + "AND nama_obat = ? LIMIT 1")) {
                psGet.setString(1, noRawat);
                psGet.setString(2, tglPemberian + " 00:00:00");
                psGet.setString(3, tglPemberian);
                psGet.setString(4, namaObat);
                ResultSet rsGet = psGet.executeQuery();
                if (rsGet.next()) {
                    waktuSimpanObat = rsGet.getString("waktu_simpan");
                }
            }

            if (waktuSimpanObat.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Data pemberian obat tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (PreparedStatement ps = koneksi.prepareStatement(
                    "UPDATE pelaksana_pemberian_obat "
                    + "SET status_validasi = 'Sudah', nip_validator = ?, waktu_validasi = NOW() "
                    + "WHERE no_rawat = ? AND tgl_pemberian >= ? AND tgl_pemberian < DATE_ADD(?, INTERVAL 1 DAY) "
                    + "AND nama_obat = ? AND waktu_simpan_obat = ?")) {
                ps.setString(1, nipValidator);
                ps.setString(2, noRawat);
                ps.setString(3, tglPemberian + " 00:00:00");
                ps.setString(4, tglPemberian);
                ps.setString(5, namaObat);
                ps.setString(6, waktuSimpanObat);
                int result = ps.executeUpdate();

                if (result > 0) {
                    JOptionPane.showMessageDialog(this,
                            "✓ Validasi Berhasil!\n\nData pemberian obat telah divalidasi oleh:\n" + validatorName,
                            "Sukses",
                            JOptionPane.INFORMATION_MESSAGE);
                    applyFilter();
                    tableMonitoring.clearSelection();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Gagal validasi. Data pelaksana untuk obat ini belum ada!\n\nPastikan petugas pelaksana sudah diisi terlebih dahulu.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error validasi: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void hapusValidasi() {
        int row = tableMonitoring.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus validasinya dari tabel!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String statusValidasi = tableMonitoring.getValueAt(row, 23).toString();
        if (!statusValidasi.equalsIgnoreCase("Sudah")) {
            JOptionPane.showMessageDialog(this, "Data yang dipilih belum divalidasi!\n\nHanya data yang sudah divalidasi yang dapat dihapus validasinya.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String noRawat = tableMonitoring.getValueAt(row, 0).toString();
        String tglPemberian = tableMonitoring.getValueAt(row, 3).toString();
        String namaObat = tableMonitoring.getValueAt(row, 4).toString();
        String validator = tableMonitoring.getValueAt(row, 24).toString();

        int confirm = JOptionPane.showConfirmDialog(this, String.format("HAPUS VALIDASI Pemberian Obat:\n\nNo. Rawat: %s\nTgl: %s\nObat: %s\n\nValidator Sebelumnya: %s\n\nData akan kembali menjadi status 'Belum Divalidasi' (kuning).\nApakah Anda yakin ingin menghapus validasi ini?",
                noRawat, tglPemberian, namaObat, validator), "Konfirmasi Hapus Validasi", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            executeHapusValidasi(noRawat, tglPemberian, namaObat);
        }
    }

    private void executeHapusValidasi(String noRawat, String tglPemberian, String namaObat) {
        try {
            String waktuSimpanObat = "";
            try (PreparedStatement psGet = koneksi.prepareStatement(
                    "SELECT waktu_simpan FROM pemberian_obat "
                    + "WHERE no_rawat = ? AND tgl_pemberian >= ? AND tgl_pemberian < DATE_ADD(?, INTERVAL 1 DAY) "
                    + "AND nama_obat = ? LIMIT 1")) {
                psGet.setString(1, noRawat);
                psGet.setString(2, tglPemberian + " 00:00:00");
                psGet.setString(3, tglPemberian);
                psGet.setString(4, namaObat);
                ResultSet rsGet = psGet.executeQuery();
                if (rsGet.next()) {
                    waktuSimpanObat = rsGet.getString("waktu_simpan");
                }
            }

            if (waktuSimpanObat.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Data pemberian obat tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (PreparedStatement ps = koneksi.prepareStatement(
                    "UPDATE pelaksana_pemberian_obat "
                    + "SET status_validasi = 'Belum', nip_validator = NULL, waktu_validasi = NULL "
                    + "WHERE no_rawat = ? AND tgl_pemberian >= ? AND tgl_pemberian < DATE_ADD(?, INTERVAL 1 DAY) "
                    + "AND nama_obat = ? AND waktu_simpan_obat = ?")) {
                ps.setString(1, noRawat);
                ps.setString(2, tglPemberian + " 00:00:00");
                ps.setString(3, tglPemberian);
                ps.setString(4, namaObat);
                ps.setString(5, waktuSimpanObat);
                int result = ps.executeUpdate();

                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "✓ Hapus Validasi Berhasil!\n\nStatus data telah dikembalikan menjadi 'Belum Divalidasi'.\nData akan ditampilkan dengan warna kuning.", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    applyFilter();
                    tableMonitoring.clearSelection();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus validasi!\n\nData tidak ditemukan atau sudah dihapus.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error hapus validasi: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadValidatorList() {
        if (koneksi == null) {
            return;
        }
        try (Statement stmt = koneksi.createStatement(); ResultSet rs = stmt.executeQuery("SELECT nip, nama FROM petugas ORDER BY nama")) {
            cmbValidator.removeAllItems();
            cmbValidator.addItem("-- Pilih Validator --");
            while (rs.next()) {
                cmbValidator.addItem(rs.getString("nip") + " - " + rs.getString("nama"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading validator: " + e.getMessage());
        }
    }

    private String formatJam(String jam) {
        return (jam == null || jam.isEmpty()) ? "-" : (jam.length() >= 5 ? jam.substring(0, 5) : jam);
    }

    private String formatJamWithKet(String jam, String ket) {
        String formattedJam = formatJam(jam);
        if (formattedJam.equals("-")) {
            return "-";
        }

        String formattedKet = formatNullable(ket);
        if (formattedKet.equals("-") || formattedKet.isEmpty()) {
            return formattedJam;
        }

        return formattedJam + " (" + formattedKet + ")";
    }

    private String formatNullable(String value) {
        return (value == null || value.isEmpty()) ? "-" : value;
    }

    public void setNoRawatFilter(String noRawat) {
        if (noRawat != null && !noRawat.trim().isEmpty()) {
            txtFilter.setText(noRawat.trim());
            currentFilter = noRawat.trim();
            applyFilter();
        }
    }

    @Override
    public void dispose() {
        if (searchTimer != null && searchTimer.isRunning()) {
            searchTimer.stop();
        }
        super.dispose();
    }

    public static void showForm(Connection conn) {
        SwingUtilities.invokeLater(() -> new MonitoringPemberianObatPasien(conn).setVisible(true));
    }

    public static void showForm() {
        SwingUtilities.invokeLater(() -> new MonitoringPemberianObatPasien().setVisible(true));
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            try {
                new MonitoringPemberianObatPasien().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
