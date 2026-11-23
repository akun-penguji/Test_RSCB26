package rekammedis;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;
import fungsi.koneksiDB;
import fungsi.akses;

public class MonitoringPemberianObatView extends JFrame {

    private final Connection koneksi;
    private JTable tableMonitoring;
    private DefaultTableModel modelMonitoring;
    private JComboBox<String> cmbPeriodeFilter, cmbStatusFilter;
    private JButton btnRefresh, btnCari;
    private JTextField txtFilter;
    private JLabel lblNoRawat, lblNoRM, lblNamaPasien;
    private String currentFilter = "";
    private String fixedNoRawatFilter = "";
    private boolean isLoadingData = false;
    private Timer searchTimer;

    public MonitoringPemberianObatView() {
        this.koneksi = koneksiDB.condb();
        if (koneksi == null) {
            JOptionPane.showMessageDialog(this, "Koneksi Database Gagal!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        initComponents();
        loadInitialData();
    }

    public MonitoringPemberianObatView(Connection connection) {
        this.koneksi = connection;
        initComponents();
        loadInitialData();
    }

    public MonitoringPemberianObatView(String noRawatFilter) {
        this.koneksi = koneksiDB.condb();
        if (koneksi == null) {
            JOptionPane.showMessageDialog(this, "Koneksi Database Gagal!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        initComponents();
        SwingUtilities.invokeLater(this::loadMonitoringData);
    }

    private void initComponents() {
        setTitle("Monitoring Pemberian Obat Pasien (View Only)");
        setSize(1600, 900);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(createMonitoringPanel());
    }

    private void loadInitialData() {
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
        headerPanel.setBorder(BorderFactory.createTitledBorder("Monitoring Jadwal Pemberian Obat Pasien"));
        headerPanel.add(createControlPanel(), BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel filterMainPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel filterPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel1.add(new JLabel("🔍 Filter:"));

        txtFilter = new JTextField(20);
        txtFilter.setToolTipText("Cari:");
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
        filterPanel1.add(txtFilter);

        btnCari = new JButton("Tampil Data");
        btnCari.setBackground(new Color(70, 130, 180));
        btnCari.setForeground(Color.WHITE);
        btnCari.setFont(new Font("Arial", Font.BOLD, 11));
        btnCari.addActionListener(e -> applyFilter());
        filterPanel1.add(btnCari);

        filterPanel1.add(new JSeparator(SwingConstants.VERTICAL));
        filterPanel1.add(new JLabel("📅 Periode:"));

        cmbPeriodeFilter = new JComboBox<>(new String[]{"Hari Ini", "7 Hari Terakhir", "30 Hari Terakhir"});
        cmbPeriodeFilter.setSelectedIndex(0);
        cmbPeriodeFilter.addActionListener(e -> {
            if (!isLoadingData) {
                applyFilter();
            }
        });
        filterPanel1.add(cmbPeriodeFilter);

        filterPanel1.add(new JSeparator(SwingConstants.VERTICAL));
        filterPanel1.add(new JLabel("Status:"));

        cmbStatusFilter = new JComboBox<>(new String[]{"Semua Data", "Belum Divalidasi", "Belum Lengkap", "Sudah Divalidasi"});
        cmbStatusFilter.addActionListener(e -> {
            if (!isLoadingData) {
                applyFilter();
            }
        });
        filterPanel1.add(cmbStatusFilter);

        JPanel filterPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        btnRefresh = new JButton("🔄 Refresh");
        btnRefresh.setBackground(new Color(100, 200, 100));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("Arial", Font.BOLD, 11));
        btnRefresh.addActionListener(e -> resetFilter());
        filterPanel2.add(btnRefresh);

        filterPanel2.add(new JSeparator(SwingConstants.VERTICAL));

        JButton btnBukaFormObat = new JButton("Input Jadwal Obat");
        btnBukaFormObat.setBackground(new Color(255, 140, 0));
        btnBukaFormObat.setForeground(Color.WHITE);
        btnBukaFormObat.setFont(new Font("Arial", Font.BOLD, 11));
        btnBukaFormObat.setToolTipText("Buka form input jadwal pemberian obat untuk pasien yang dipilih");
        btnBukaFormObat.addActionListener(e -> bukaFormPemberianObat());
        filterPanel2.add(btnBukaFormObat);

        filterPanel2.add(new JSeparator(SwingConstants.VERTICAL));

        JButton btnSummary = new JButton("📊 Summary");
        btnSummary.setBackground(new Color(106, 90, 205));
        btnSummary.setForeground(Color.WHITE);
        btnSummary.setFont(new Font("Arial", Font.BOLD, 11));
        btnSummary.setToolTipText("Tampilkan ringkasan pemberian obat pasien");
        btnSummary.addActionListener(e -> tampilkanSummary());
        filterPanel2.add(btnSummary);

        filterMainPanel.add(filterPanel1);
        filterMainPanel.add(filterPanel2);

        panel.add(filterMainPanel, BorderLayout.NORTH);
        panel.add(createInfoPasienPanel(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInfoPasienPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Informasi Pasien"));

        lblNoRawat = new JLabel("No. Rawat: -");
        lblNoRawat.setFont(new Font("Arial", Font.BOLD, 13));
        lblNoRawat.setForeground(new Color(70, 130, 180));
        panel.add(lblNoRawat);

        panel.add(new JSeparator(SwingConstants.VERTICAL));

        lblNoRM = new JLabel("No. RM: -");
        lblNoRM.setFont(new Font("Arial", Font.BOLD, 13));
        lblNoRM.setForeground(new Color(70, 130, 180));
        panel.add(lblNoRM);

        panel.add(new JSeparator(SwingConstants.VERTICAL));

        lblNamaPasien = new JLabel("Nama Pasien: -");
        lblNamaPasien.setFont(new Font("Arial", Font.BOLD, 13));
        lblNamaPasien.setForeground(new Color(70, 130, 180));
        panel.add(lblNamaPasien);

        return panel;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {"Tgl Pemberian", "Nama Obat", "Dosis",
            "Jam 1", "Jam 2", "Jam 3", "Jam 4", "Jam 5", "Jam 6", "Jam 7", "Jam 8",
            "Petugas 1", "Petugas 2", "Petugas 3", "Petugas 4", "Petugas 5", "Petugas 6", "Petugas 7", "Petugas 8",
            "Unit", "Status Validasi", "Validator", "Waktu Validasi",
            "status_jam1", "status_jam2", "status_jam3", "status_jam4", "status_jam5", "status_jam6", "status_jam7", "status_jam8",
            "no_rawat", "no_rm", "nm_pasien", "waktu_simpan"};

        modelMonitoring = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableMonitoring = new JTable(modelMonitoring);
        tableMonitoring.setRowHeight(30);
        tableMonitoring.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        tableMonitoring.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = tableMonitoring.rowAtPoint(e.getPoint());
                    int col = tableMonitoring.columnAtPoint(e.getPoint());

                    if (row >= 0 && col >= 3 && col <= 10) {
                        tableMonitoring.setRowSelectionInterval(row, row);
                        showStatusPopupMenu(e.getX(), e.getY(), row, col);
                    }
                }
            }
        });

        tableMonitoring.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (column == 1) {
                    setHorizontalAlignment(SwingConstants.LEFT);
                } else {
                    setHorizontalAlignment(SwingConstants.CENTER);
                }

                if (!isSelected) {
                    String statusValidasi = table.getValueAt(row, 20).toString();
                    boolean adaJamSudahDiberikan = false;

                    for (int i = 23; i <= 30; i++) {
                        if ("sudah".equalsIgnoreCase(table.getValueAt(row, i).toString())) {
                            adaJamSudahDiberikan = true;
                            break;
                        }
                    }

                    if ("Sudah".equalsIgnoreCase(statusValidasi)) {
                        c.setBackground(new Color(144, 238, 144));
                    } else if ("Belum Lengkap".equalsIgnoreCase(statusValidasi)) {
                        c.setBackground(new Color(255, 200, 124));
                    } else if (adaJamSudahDiberikan && "Belum".equalsIgnoreCase(statusValidasi)) {
                        c.setBackground(new Color(255, 200, 124));
                    } else {
                        c.setBackground(new Color(255, 255, 153));
                    }

                    if (column >= 3 && column <= 10) {
                        int jamIndex = column - 3;
                        String statusJam = table.getValueAt(row, 23 + jamIndex).toString();
                        if ("sudah".equalsIgnoreCase(statusJam)) {
                            c.setBackground(new Color(144, 238, 144));
                        }
                    }

                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(new Color(184, 207, 229));
                    c.setForeground(Color.BLACK);
                }

                return c;
            }
        });

        tableMonitoring.setGridColor(new Color(200, 200, 200));
        tableMonitoring.setShowGrid(true);

        javax.swing.table.DefaultTableCellRenderer headerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        headerRenderer.setBackground(new Color(70, 130, 180));
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font("Arial", Font.BOLD, 12));

        for (int i = 0; i < tableMonitoring.getColumnModel().getColumnCount(); i++) {
            tableMonitoring.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        tableMonitoring.getTableHeader().setPreferredSize(new Dimension(0, 35));

        int[] widths = {100, 200, 200, 120, 120, 120, 120, 120, 120, 120, 120,
            90, 90, 90, 90, 90, 90, 90, 90, 150, 100, 100, 150,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        for (int i = 0; i < widths.length && i < tableMonitoring.getColumnCount(); i++) {
            tableMonitoring.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            if (i >= 23) {
                tableMonitoring.getColumnModel().getColumn(i).setMinWidth(0);
                tableMonitoring.getColumnModel().getColumn(i).setMaxWidth(0);
            }
        }

        tableMonitoring.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateInfoPasienLabel();
                updateFilterFromSelection();
            }
        });

        JScrollPane scroll = new JScrollPane(tableMonitoring);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        return scroll;
    }

    private void showStatusPopupMenu(int x, int y, int row, int col) {
        int jamIndex = col - 3;
        String jamLabel = "Jam " + (jamIndex + 1);
        String currentStatus = tableMonitoring.getValueAt(row, 23 + jamIndex).toString();
        String jamPemberian = tableMonitoring.getValueAt(row, col).toString();

        if (jamPemberian == null || jamPemberian.trim().isEmpty() || jamPemberian.equals("-")) {
            JOptionPane.showMessageDialog(this,
                    "Jam pemberian " + jamLabel + " belum diisi!\nTidak dapat mengupdate status pemberian.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int petugasColIndex = 11 + jamIndex;
        String namaPetugas = tableMonitoring.getValueAt(row, petugasColIndex).toString();

        if (namaPetugas == null || namaPetugas.trim().isEmpty() || namaPetugas.equals("-")) {
            JOptionPane.showMessageDialog(this,
                    "Petugas pelaksana untuk " + jamLabel + " belum diisi!\nSilakan isi data petugas pelaksana terlebih dahulu melalui form pemberian obat.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPopupMenu popup = new JPopupMenu();
        popup.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        JLabel lblTitle = new JLabel("  Update Status " + jamLabel + "  ");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setOpaque(true);
        lblTitle.setBackground(new Color(70, 130, 180));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        popup.add(lblTitle);
        popup.addSeparator();

        JLabel lblJam = new JLabel("  Waktu: " + jamPemberian + "  ");
        lblJam.setFont(new Font("Arial", Font.PLAIN, 12));
        lblJam.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        popup.add(lblJam);

        JLabel lblPetugas = new JLabel("  Petugas: " + namaPetugas + "  ");
        lblPetugas.setFont(new Font("Arial", Font.PLAIN, 12));
        lblPetugas.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        popup.add(lblPetugas);

        JLabel lblCurrent = new JLabel("  Status Saat Ini: " + currentStatus.toUpperCase() + "  ");
        lblCurrent.setFont(new Font("Arial", Font.BOLD, 12));
        lblCurrent.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        popup.add(lblCurrent);
        popup.addSeparator();

        JMenu menuSudah = new JMenu("✓ SUDAH");
        menuSudah.setFont(new Font("Arial", Font.BOLD, 13));
        menuSudah.setOpaque(true);
        menuSudah.setBackground(new Color(144, 238, 144));

        JMenuItem itemSudahLanjut = new JMenuItem("LANJUT");
        itemSudahLanjut.setFont(new Font("Arial", Font.PLAIN, 12));
        itemSudahLanjut.addActionListener(e -> updateStatusPemberianDenganKet(row, jamIndex, "sudah", "LANJUT"));
        menuSudah.add(itemSudahLanjut);

        JMenuItem itemSudahSelesai = new JMenuItem("SELESAI");
        itemSudahSelesai.setFont(new Font("Arial", Font.PLAIN, 12));
        itemSudahSelesai.addActionListener(e -> updateStatusPemberianDenganKet(row, jamIndex, "sudah", "SELESAI"));
        menuSudah.add(itemSudahSelesai);

        JMenuItem itemSudahDihentikan = new JMenuItem("DIHENTIKAN");
        itemSudahDihentikan.setFont(new Font("Arial", Font.PLAIN, 12));
        itemSudahDihentikan.addActionListener(e -> updateStatusPemberianDenganKet(row, jamIndex, "sudah", "DIHENTIKAN"));
        menuSudah.add(itemSudahDihentikan);

        JMenuItem itemSudahEkstra = new JMenuItem("EKSTRA");
        itemSudahEkstra.setFont(new Font("Arial", Font.PLAIN, 12));
        itemSudahEkstra.addActionListener(e -> updateStatusPemberianDenganKet(row, jamIndex, "sudah", "EKSTRA"));
        menuSudah.add(itemSudahEkstra);

        menuSudah.addSeparator();

        JMenuItem itemSudahCustom = new JMenuItem("Input Keterangan Lainnya...");
        itemSudahCustom.setFont(new Font("Arial", Font.ITALIC, 12));
        itemSudahCustom.addActionListener(e -> {
            String keteranganCustom = JOptionPane.showInputDialog(this,
                    "Masukkan keterangan untuk status SUDAH:",
                    "Input Keterangan Custom",
                    JOptionPane.PLAIN_MESSAGE);

            if (keteranganCustom != null && !keteranganCustom.trim().isEmpty()) {
                updateStatusPemberianDenganKet(row, jamIndex, "sudah", keteranganCustom.trim().toUpperCase());
            }
        });
        menuSudah.add(itemSudahCustom);

        popup.add(menuSudah);

        JMenuItem itemBelum = new JMenuItem("✗ BELUM");
        itemBelum.setFont(new Font("Arial", Font.BOLD, 13));
        itemBelum.setBackground(new Color(255, 200, 124));
        itemBelum.addActionListener(e -> updateStatusPemberianDenganKet(row, jamIndex, "belum", ""));
        popup.add(itemBelum);

        popup.show(tableMonitoring, x, y);
    }

    private void updateStatusPemberianDenganKet(int row, int jamIndex, String status, String keterangan) {
        String noRawat = tableMonitoring.getValueAt(row, 31).toString();
        String waktuSimpan = tableMonitoring.getValueAt(row, 34).toString();
        String namaObat = tableMonitoring.getValueAt(row, 1).toString();
        String tglPemberian = tableMonitoring.getValueAt(row, 0).toString();
        String jamLabel = "Jam " + (jamIndex + 1);

        String statusText = status.equalsIgnoreCase("sudah")
                ? "SUDAH" + (keterangan.isEmpty() ? "" : " - " + keterangan) : "BELUM";

        int confirm = JOptionPane.showConfirmDialog(this,
                "Update status pemberian obat " + jamLabel + " menjadi:\n'" + statusText + "'?",
                "Konfirmasi Update", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            String checkSql = "SELECT COUNT(*) as jml FROM pelaksana_pemberian_obat "
                    + "WHERE no_rawat = ? AND waktu_simpan_obat = ?";
            PreparedStatement psCheck = koneksi.prepareStatement(checkSql);
            psCheck.setString(1, noRawat);
            psCheck.setString(2, waktuSimpan);
            ResultSet rs = psCheck.executeQuery();

            boolean recordExists = false;
            if (rs.next()) {
                recordExists = rs.getInt("jml") > 0;
            }
            rs.close();
            psCheck.close();

            String sql;
            PreparedStatement ps;

            if (recordExists) {
                String columnName = "status_pemberian_jam" + (jamIndex + 1);
                sql = "UPDATE pelaksana_pemberian_obat SET " + columnName + " = ? "
                        + "WHERE no_rawat = ? AND waktu_simpan_obat = ?";
                ps = koneksi.prepareStatement(sql);
                ps.setString(1, status);
                ps.setString(2, noRawat);
                ps.setString(3, waktuSimpan);
            } else {
                sql = "INSERT INTO pelaksana_pemberian_obat (no_rawat, waktu_simpan_obat, nama_obat, "
                        + "status_pemberian_jam1, status_pemberian_jam2, status_pemberian_jam3, "
                        + "status_pemberian_jam4, status_pemberian_jam5, status_pemberian_jam6, "
                        + "status_pemberian_jam7, status_pemberian_jam8, status_validasi) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'Belum')";
                ps = koneksi.prepareStatement(sql);
                ps.setString(1, noRawat);
                ps.setString(2, waktuSimpan);
                ps.setString(3, namaObat);

                for (int i = 0; i < 8; i++) {
                    ps.setString(4 + i, (i == jamIndex) ? status : "belum");
                }
            }

            int result = ps.executeUpdate();
            ps.close();

            if (result > 0 && !keterangan.isEmpty()) {
                String ketColumn = "ket" + (jamIndex + 1);
                String updateKetSql = "UPDATE pemberian_obat SET " + ketColumn + " = ? "
                        + "WHERE no_rawat = ? AND tgl_pemberian = ? AND waktu_simpan = ?";
                PreparedStatement psKet = koneksi.prepareStatement(updateKetSql);
                psKet.setString(1, keterangan);
                psKet.setString(2, noRawat);
                psKet.setString(3, tglPemberian);
                psKet.setString(4, waktuSimpan);
                psKet.executeUpdate();
                psKet.close();
            }

            if (result > 0) {
                updateStatusValidasi(noRawat, waktuSimpan);

                int statusColumnIndex = 23 + jamIndex;
                tableMonitoring.setValueAt(status, row, statusColumnIndex);

                if (!keterangan.isEmpty()) {
                    String jamValue = tableMonitoring.getValueAt(row, 3 + jamIndex).toString();
                    if (jamValue.contains("(")) {
                        jamValue = jamValue.substring(0, jamValue.indexOf("(")).trim();
                    }
                    tableMonitoring.setValueAt(jamValue + " (" + keterangan + ")", row, 3 + jamIndex);
                }

                tableMonitoring.clearSelection();
                tableMonitoring.repaint(tableMonitoring.getCellRect(row, 0, true));

                JOptionPane.showMessageDialog(this,
                        "Status pemberian obat " + jamLabel + " berhasil diupdate!",
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal mengupdate status pemberian obat!",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error database: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatusValidasi(String noRawat, String waktuSimpan) throws SQLException {
        String checkSql = "SELECT status_pemberian_jam1, status_pemberian_jam2, status_pemberian_jam3, "
                + "status_pemberian_jam4, status_pemberian_jam5, status_pemberian_jam6, "
                + "status_pemberian_jam7, status_pemberian_jam8 "
                + "FROM pelaksana_pemberian_obat WHERE no_rawat = ? AND waktu_simpan_obat = ?";

        PreparedStatement ps = koneksi.prepareStatement(checkSql);
        ps.setString(1, noRawat);
        ps.setString(2, waktuSimpan);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            int countSudah = 0;
            int countBelum = 0;

            for (int i = 1; i <= 8; i++) {
                String status = rs.getString("status_pemberian_jam" + i);
                if ("sudah".equalsIgnoreCase(status)) {
                    countSudah++;
                } else if ("belum".equalsIgnoreCase(status)) {
                    countBelum++;
                }
            }

            String newStatusValidasi = (countSudah > 0 && countBelum > 0) ? "Belum Lengkap"
                    : (countSudah > 0 && countBelum == 0) ? "Belum Lengkap" : "Belum";

            String updateSql = "UPDATE pelaksana_pemberian_obat SET status_validasi = ? "
                    + "WHERE no_rawat = ? AND waktu_simpan_obat = ?";
            PreparedStatement psUpdate = koneksi.prepareStatement(updateSql);
            psUpdate.setString(1, newStatusValidasi);
            psUpdate.setString(2, noRawat);
            psUpdate.setString(3, waktuSimpan);
            psUpdate.executeUpdate();
            psUpdate.close();
        }

        rs.close();
        ps.close();
    }

    private void updateInfoPasienLabel() {
        int row = tableMonitoring.getSelectedRow();
        if (row >= 0) {
            lblNoRawat.setText("No. Rawat: " + tableMonitoring.getValueAt(row, 31).toString());
            lblNoRM.setText("No. RM: " + tableMonitoring.getValueAt(row, 32).toString());
            lblNamaPasien.setText("Nama Pasien: " + tableMonitoring.getValueAt(row, 33).toString());
        } else {
            lblNoRawat.setText("No. Rawat: -");
            lblNoRM.setText("No. RM: -");
            lblNamaPasien.setText("Nama Pasien: -");
        }
    }

    private void updateFilterFromSelection() {
        int row = tableMonitoring.getSelectedRow();
        if (row >= 0 && !isLoadingData) {
            String noRawat = tableMonitoring.getValueAt(row, 31).toString();
            txtFilter.setText(noRawat);
            currentFilter = noRawat;
        }
    }

    private void loadMonitoringData() {
        loadDataWithQuery(buildDefaultQuery(), null);
    }

    private void applyFilter() {
        if (!fixedNoRawatFilter.isEmpty()) {
            currentFilter = fixedNoRawatFilter;
        } else {
            currentFilter = txtFilter.getText().trim();
        }

        String statusFilter = cmbStatusFilter.getSelectedItem().toString();
        String periodeFilter = cmbPeriodeFilter.getSelectedItem().toString();

        StringBuilder query = new StringBuilder(buildBaseQuery());
        query.append("WHERE 1=1 ");

        if (periodeFilter.equals("Hari Ini")) {
            query.append("AND p.tgl_pemberian >= CURDATE() AND p.tgl_pemberian < DATE_ADD(CURDATE(), INTERVAL 1 DAY) ");
        } else if (periodeFilter.equals("7 Hari Terakhir")) {
            query.append("AND p.tgl_pemberian >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) ");
        } else if (periodeFilter.equals("30 Hari Terakhir")) {
            query.append("AND p.tgl_pemberian >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) ");
        }

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

        query.append("ORDER BY p.tgl_pemberian DESC, p.waktu_simpan DESC LIMIT 500");

        if (hasTextFilter) {
            String searchPattern = "%" + currentFilter + "%";
            loadDataWithQuery(query.toString(), new String[]{searchPattern, searchPattern, searchPattern});
        } else {
            loadDataWithQuery(query.toString(), null);
        }
    }

    private void resetFilter() {
        fixedNoRawatFilter = "";
        txtFilter.setText("");
        txtFilter.setEditable(true);
        txtFilter.setBackground(Color.WHITE);
        currentFilter = "";
        cmbStatusFilter.setSelectedIndex(0);

        lblNoRawat.setText("No. Rawat: -");
        lblNoRM.setText("No. RM: -");
        lblNamaPasien.setText("Nama Pasien: -");

        applyFilter();
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
                + "COALESCE(pel.status_pemberian_jam8, 'belum') AS status_jam8, "
                + "p.waktu_simpan "
                + "FROM pemberian_obat p "
                + "LEFT JOIN pelaksana_pemberian_obat pel ON p.no_rawat = pel.no_rawat AND p.waktu_simpan = pel.waktu_simpan_obat "
                + "LEFT JOIN reg_periksa rp ON p.no_rawat = rp.no_rawat "
                + "LEFT JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                + "LEFT JOIN petugas pe1 ON pe1.nip = pel.nip_petugas_jam1 "
                + "LEFT JOIN petugas pe2 ON pe2.nip = pel.nip_petugas_jam2 "
                + "LEFT JOIN petugas pe3 ON pe3.nip = pel.nip_petugas_jam3 "
                + "LEFT JOIN petugas pe4 ON pe4.nip = pel.nip_petugas_jam4 "
                + "LEFT JOIN petugas pe5 ON pe5.nip = pel.nip_petugas_jam5 "
                + "LEFT JOIN petugas pe6 ON pe6.nip = pel.nip_petugas_jam6 "
                + "LEFT JOIN petugas pe7 ON pe7.nip = pel.nip_petugas_jam7 "
                + "LEFT JOIN petugas pe8 ON pe8.nip = pel.nip_petugas_jam8 "
                + "LEFT JOIN petugas pv ON pv.nip = pel.nip_validator ";
    }

    private String buildDefaultQuery() {
        return buildBaseQuery() + "WHERE p.tgl_pemberian >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) "
                + "ORDER BY p.tgl_pemberian DESC, p.waktu_simpan DESC LIMIT 500";
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

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private final java.util.List<Object[]> rows = new java.util.ArrayList<>();
            private String errorMessage = null;

            @Override
            protected Void doInBackground() {
                try (PreparedStatement ps = koneksi.prepareStatement(query)) {
                    if (params != null) {
                        for (int i = 0; i < params.length; i++) {
                            ps.setString(i + 1, params[i]);
                        }
                    }

                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        Object[] row = {
                            rs.getString("tgl_pemberian"),
                            rs.getString("nama_obat"),
                            rs.getString("dosis"),
                            formatJamWithKet(rs.getString("jam1"), rs.getString("ket1")),
                            formatJamWithKet(rs.getString("jam2"), rs.getString("ket2")),
                            formatJamWithKet(rs.getString("jam3"), rs.getString("ket3")),
                            formatJamWithKet(rs.getString("jam4"), rs.getString("ket4")),
                            formatJamWithKet(rs.getString("jam5"), rs.getString("ket5")),
                            formatJamWithKet(rs.getString("jam6"), rs.getString("ket6")),
                            formatJamWithKet(rs.getString("jam7"), rs.getString("ket7")),
                            formatJamWithKet(rs.getString("jam8"), rs.getString("ket8")),
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
                            rs.getString("status_jam8"),
                            rs.getString("no_rawat"),
                            rs.getString("no_rkm_medis"),
                            rs.getString("nm_pasien"),
                            rs.getString("waktu_simpan")
                        };
                        rows.add(row);
                    }
                } catch (SQLException e) {
                    errorMessage = e.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                modelMonitoring.setRowCount(0);

                if (errorMessage != null) {
                    JOptionPane.showMessageDialog(MonitoringPemberianObatView.this,
                            "Error loading data: " + errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
                    resetInfoPasienLabels();
                } else {
                    for (Object[] row : rows) {
                        modelMonitoring.addRow(row);
                    }

                    if (modelMonitoring.getRowCount() > 0 && !currentFilter.isEmpty()) {
                        SwingUtilities.invokeLater(() -> {
                            tableMonitoring.setRowSelectionInterval(0, 0);
                            updateInfoPasienLabel();
                        });
                    } else {
                        resetInfoPasienLabels();
                    }
                }

                setCursor(Cursor.getDefaultCursor());
                btnCari.setEnabled(true);
                btnRefresh.setEnabled(true);
                isLoadingData = false;
            }
        };

        worker.execute();
    }

    private void checkConnectionHealth() {
        try {
            if (koneksi == null || koneksi.isClosed()) {
                JOptionPane.showMessageDialog(this,
                        "Koneksi database terputus!\nSilakan restart aplikasi.",
                        "Connection Error", JOptionPane.ERROR_MESSAGE);
                dispose();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void resetInfoPasienLabels() {
        lblNoRawat.setText("No. Rawat: -");
        lblNoRM.setText("No. RM: -");
        lblNamaPasien.setText("Nama Pasien: -");
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
            fixedNoRawatFilter = noRawat.trim();
            txtFilter.setText(fixedNoRawatFilter);
            txtFilter.setEditable(true);
            txtFilter.setBackground(Color.WHITE);
            currentFilter = fixedNoRawatFilter;
            loadInfoPasien(fixedNoRawatFilter);
            applyFilter();
        }
    }

    private void loadInfoPasien(String noRawat) {
        try (PreparedStatement ps = koneksi.prepareStatement(
                "SELECT rp.no_rkm_medis, pas.nm_pasien FROM reg_periksa rp "
                + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                + "WHERE rp.no_rawat = ? LIMIT 1")) {
            ps.setString(1, noRawat);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                lblNoRawat.setText("No. Rawat: " + noRawat);
                lblNoRM.setText("No. RM: " + rs.getString("no_rkm_medis"));
                lblNamaPasien.setText("Nama Pasien: " + rs.getString("nm_pasien"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setInfoPasien(String noRawat, String noRM, String namaPasien) {
        if (noRawat != null && !noRawat.isEmpty()) {
            lblNoRawat.setText("No. Rawat: " + noRawat);
        }
        if (noRM != null && !noRM.isEmpty()) {
            lblNoRM.setText("No. RM: " + noRM);
        }
        if (namaPasien != null && !namaPasien.isEmpty()) {
            lblNamaPasien.setText("Nama Pasien: " + namaPasien);
        }
    }

    private void tampilkanSummary() {
        String noRawat = "";
        String noRM = "";
        String namaPasien = "";

        if (modelMonitoring.getRowCount() > 0 && tableMonitoring.getSelectedRow() >= 0) {
            int row = tableMonitoring.getSelectedRow();
            noRawat = tableMonitoring.getValueAt(row, 31).toString();
            noRM = tableMonitoring.getValueAt(row, 32).toString();
            namaPasien = tableMonitoring.getValueAt(row, 33).toString();
        } else if (!lblNoRawat.getText().equals("No. Rawat: -")) {
            noRawat = lblNoRawat.getText().replace("No. Rawat: ", "").trim();
            noRM = lblNoRM.getText().replace("No. RM: ", "").trim();
            namaPasien = lblNamaPasien.getText().replace("Nama Pasien: ", "").trim();
        } else if (!fixedNoRawatFilter.isEmpty()) {
            noRawat = fixedNoRawatFilter;
            try (PreparedStatement ps = koneksi.prepareStatement(
                    "SELECT rp.no_rkm_medis, pas.nm_pasien FROM reg_periksa rp "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "WHERE rp.no_rawat = ? LIMIT 1")) {
                ps.setString(1, noRawat);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    noRM = rs.getString("no_rkm_medis");
                    namaPasien = rs.getString("nm_pasien");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (noRawat.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Tidak ada data pasien yang dipilih!\n\nSilakan pilih pasien dari tabel atau gunakan filter nomor rawat.",
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String query = "SELECT p.nama_obat, p.dosis, p.cara_pemberian, "
                    + "SUM(CASE WHEN p.jadwal_pemberian IS NOT NULL AND p.jadwal_pemberian != '' THEN 1 ELSE 0 END + "
                    + "    CASE WHEN p.jadwal_pemberian2 IS NOT NULL AND p.jadwal_pemberian2 != '' THEN 1 ELSE 0 END + "
                    + "    CASE WHEN p.jadwal_pemberian3 IS NOT NULL AND p.jadwal_pemberian3 != '' THEN 1 ELSE 0 END + "
                    + "    CASE WHEN p.jadwal_pemberian4 IS NOT NULL AND p.jadwal_pemberian4 != '' THEN 1 ELSE 0 END + "
                    + "    CASE WHEN p.jadwal_pemberian5 IS NOT NULL AND p.jadwal_pemberian5 != '' THEN 1 ELSE 0 END + "
                    + "    CASE WHEN p.jadwal_pemberian6 IS NOT NULL AND p.jadwal_pemberian6 != '' THEN 1 ELSE 0 END + "
                    + "    CASE WHEN p.jadwal_pemberian7 IS NOT NULL AND p.jadwal_pemberian7 != '' THEN 1 ELSE 0 END + "
                    + "    CASE WHEN p.jadwal_pemberian8 IS NOT NULL AND p.jadwal_pemberian8 != '' THEN 1 ELSE 0 END) AS total_jadwal, "
                    + "SUM(CASE WHEN COALESCE(pel.status_pemberian_jam1, 'belum') = 'sudah' THEN 1 ELSE 0 END + "
                    + "    CASE WHEN COALESCE(pel.status_pemberian_jam2, 'belum') = 'sudah' THEN 1 ELSE 0 END + "
                    + "    CASE WHEN COALESCE(pel.status_pemberian_jam3, 'belum') = 'sudah' THEN 1 ELSE 0 END + "
                    + "    CASE WHEN COALESCE(pel.status_pemberian_jam4, 'belum') = 'sudah' THEN 1 ELSE 0 END + "
                    + "    CASE WHEN COALESCE(pel.status_pemberian_jam5, 'belum') = 'sudah' THEN 1 ELSE 0 END + "
                    + "    CASE WHEN COALESCE(pel.status_pemberian_jam6, 'belum') = 'sudah' THEN 1 ELSE 0 END + "
                    + "    CASE WHEN COALESCE(pel.status_pemberian_jam7, 'belum') = 'sudah' THEN 1 ELSE 0 END + "
                    + "    CASE WHEN COALESCE(pel.status_pemberian_jam8, 'belum') = 'sudah' THEN 1 ELSE 0 END) AS total_sudah_diberi "
                    + "FROM pemberian_obat p "
                    + "LEFT JOIN pelaksana_pemberian_obat pel ON p.no_rawat = pel.no_rawat AND p.waktu_simpan = pel.waktu_simpan_obat "
                    + "WHERE p.no_rawat = ? "
                    + "GROUP BY p.nama_obat, p.dosis, p.cara_pemberian "
                    + "ORDER BY p.nama_obat";

            PreparedStatement ps = koneksi.prepareStatement(query);
            ps.setString(1, noRawat);
            ResultSet rs = ps.executeQuery();

            StringBuilder summary = new StringBuilder();
            summary.append("═══════════════════════════════════════════════════════════\n");
            summary.append("             RINGKASAN PEMBERIAN OBAT PASIEN\n");
            summary.append("═══════════════════════════════════════════════════════════\n\n");
            summary.append("No. Rawat      : ").append(noRawat).append("\n");
            summary.append("No. RM         : ").append(noRM).append("\n");
            summary.append("Nama Pasien    : ").append(namaPasien).append("\n");
            summary.append("═══════════════════════════════════════════════════════════\n\n");

            int nomorObat = 1;
            while (rs.next()) {
                String namaObat = rs.getString("nama_obat");
                String dosis = rs.getString("dosis");
                String caraPemberian = rs.getString("cara_pemberian");
                int totalJadwal = rs.getInt("total_jadwal");
                int totalSudahDiberi = rs.getInt("total_sudah_diberi");

                summary.append(nomorObat).append(". ").append(namaObat).append("\n");
                summary.append("   Dosis              : ").append(dosis != null ? dosis : "-").append("\n");
                summary.append("   Cara Pemberian     : ").append(caraPemberian != null ? caraPemberian : "-").append("\n");
                summary.append("   Terjadwal          : ").append(totalJadwal).append(" kali\n");
                summary.append("   Sudah Diberikan    : ").append(totalSudahDiberi).append(" kali");

                if (totalJadwal > 0) {
                    int persentase = (totalSudahDiberi * 100) / totalJadwal;
                    summary.append(" (").append(persentase).append("%)");
                }
                summary.append("\n\n");
                nomorObat++;
            }

            if (nomorObat == 1) {
                summary.append("Tidak ada data pemberian obat untuk pasien ini.\n\n");
            }

            summary.append("═══════════════════════════════════════════════════════════\n");

            rs.close();
            ps.close();

            JTextArea textArea = new JTextArea(summary.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setCaretPosition(0);

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 450));

            JOptionPane.showMessageDialog(this, scrollPane,
                    "Summary Pemberian Obat - " + namaPasien, JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error mengambil data summary: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void bukaFormPemberianObat() {
        String noRawatTerpilih = "";
        String noRMTerpilih = "";
        String namaPasienTerpilih = "";
        String unitTerpilih = "";

        if (modelMonitoring.getRowCount() > 0) {
            int row = tableMonitoring.getSelectedRow();
            if (row >= 0) {
                noRawatTerpilih = tableMonitoring.getValueAt(row, 31).toString();
                noRMTerpilih = tableMonitoring.getValueAt(row, 32).toString();
                namaPasienTerpilih = tableMonitoring.getValueAt(row, 33).toString();
                unitTerpilih = tableMonitoring.getValueAt(row, 19).toString();
            }
        }

        if (noRawatTerpilih.isEmpty() && !lblNoRawat.getText().equals("No. Rawat: -")) {
            noRawatTerpilih = lblNoRawat.getText().replace("No. Rawat: ", "").trim();
            noRMTerpilih = lblNoRM.getText().replace("No. RM: ", "").trim();
            namaPasienTerpilih = lblNamaPasien.getText().replace("Nama Pasien: ", "").trim();
        }

        if (noRawatTerpilih.isEmpty() && !fixedNoRawatFilter.isEmpty()) {
            noRawatTerpilih = fixedNoRawatFilter;
        }

        if (!noRawatTerpilih.isEmpty() && (noRMTerpilih.isEmpty() || namaPasienTerpilih.isEmpty())) {
            try (PreparedStatement ps = koneksi.prepareStatement(
                    "SELECT rp.no_rkm_medis, pas.nm_pasien FROM reg_periksa rp "
                    + "INNER JOIN pasien pas ON rp.no_rkm_medis = pas.no_rkm_medis "
                    + "WHERE rp.no_rawat = ? LIMIT 1")) {
                ps.setString(1, noRawatTerpilih);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    noRMTerpilih = rs.getString("no_rkm_medis");
                    namaPasienTerpilih = rs.getString("nm_pasien");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        String noRawatFinal = noRawatTerpilih;

        if (!noRawatTerpilih.isEmpty()) {
            try (PreparedStatement ps = koneksi.prepareStatement(
                    "SELECT ranap_gabung.no_rawat2 FROM ranap_gabung WHERE ranap_gabung.no_rawat2=?")) {
                ps.setString(1, noRawatTerpilih);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    noRawatFinal = rs.getString("no_rawat2");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            rekammedis.DlgPemberianObatPasien beriObat = new rekammedis.DlgPemberianObatPasien(null, false);
            akses.setform("MonitoringPemberianObatView");
            beriObat.emptTeks();
            beriObat.isCek();

            if (!noRawatFinal.isEmpty()) {
                beriObat.setData(noRawatFinal, noRMTerpilih, namaPasienTerpilih, "ranap", unitTerpilih);
            }

            beriObat.setSize(this.getWidth() - 40, this.getHeight() - 40);
            beriObat.setLocationRelativeTo(this);
            beriObat.setAlwaysOnTop(false);
            beriObat.setVisible(true);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error membuka form: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            this.setCursor(Cursor.getDefaultCursor());
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
        SwingUtilities.invokeLater(() -> new MonitoringPemberianObatView(conn).setVisible(true));
    }

    public static void showForm() {
        SwingUtilities.invokeLater(() -> new MonitoringPemberianObatView().setVisible(true));
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            try {
                new MonitoringPemberianObatView().setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
