/*
 * DlgBerkasRawat2.java
 * Pure Java Swing implementation of DlgBerkasRawat
 * Redesigned to match web interface layout
 * Created on 11 Feb 2026
 */

package laporan;

import bridging.EsignService;
import bridging.SynologyUploader;
import bridging.SertisignMultiSignerService;
import bridging.SignerConfig;
import bridging.UploadPDFTte;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;

/**
 * Pure Java Swing implementation of Berkas Rawat dialog
 * Displays patient list with berkas on the right side
 * 
 * @author perpustakaan
 */
public class DlgBerkasRawat2 extends JDialog {
    
    // UI Components - Search Panel
    private JTextField txtDateFrom;
    private JTextField txtDateTo;
    private JTextField txtKeyword;
    private JComboBox<String> cmbStatus;
    private JButton btnSearch;
    private JButton btnRefresh;
    
    // UI Components - Patient List
    private JPanel pnlPatientList;
    private JScrollPane scrollPatientList;
    private JLabel lblStatus;
    private JComboBox<String> cmbCaraBayar;
    
    // Data
    private List<PatientWithBerkas> patients;
    private final Connection koneksi = koneksiDB.condb();
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    
    /**
     * Data model for patient with berkas
     */
    static class PatientWithBerkas {
        String noRawat;
        String noRM;
        String namaPasien;
        String alamat;
        String tglRegistrasi;
        String jamReg;
        String nmPoli;
        String nmDokter;
        String statusLanjut;
        String pngJawab;
        List<BerkasItem> berkasList;
        
        public PatientWithBerkas() {
            berkasList = new ArrayList<>();
        }
    }
    
    /**
     * Data model for berkas item
     */
    static class BerkasItem {
        String kode;
        String namaBerkas;
        String lokasiFile;
        String nip;
        boolean selected;
    }
    
    /**
     * Constructor
     */
    public DlgBerkasRawat2(Frame parent, boolean modal) {
        super(parent, modal);
        patients = new ArrayList<>();
        initComponents();
        loadInitialData();
    }
    
    /**
     * Initialize UI components
     */
    private void initComponents() {
        setTitle("Berkas Digital Perawatan");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5));
        
        // Search Panel (Top)
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);
        
        // Patient List Panel (Center)
        pnlPatientList = new JPanel();
        pnlPatientList.setLayout(new BoxLayout(pnlPatientList, BoxLayout.Y_AXIS));
        pnlPatientList.setBackground(Color.WHITE);
        
        scrollPatientList = new JScrollPane(pnlPatientList);
        scrollPatientList.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPatientList.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPatientList, BorderLayout.CENTER);
        
        // Status Panel (Bottom)
        JPanel statusPanel = new JPanel(new BorderLayout());
        lblStatus = new JLabel("Status: Ready");
        lblStatus.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.add(lblStatus, BorderLayout.WEST);
        
        // Filter Cara Bayar
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        filterPanel.add(new JLabel("Cara Bayar :"));
        cmbCaraBayar = new JComboBox<>(new String[]{"Semua"});
        cmbCaraBayar.setPreferredSize(new Dimension(200, 25));
        loadCaraBayar();
        // Default BPJS
        for (int i = 0; i < cmbCaraBayar.getItemCount(); i++) {
            if (cmbCaraBayar.getItemAt(i).toUpperCase().contains("BPJS")) {
                cmbCaraBayar.setSelectedIndex(i); break;
            }
        }
        filterPanel.add(cmbCaraBayar);

        JButton btnBatchLocal = new JButton("Gabung PDF Batch Local");
        btnBatchLocal.addActionListener(e -> gabungPDFBatchLocal());
        filterPanel.add(btnBatchLocal);

        JButton btnUploadSynology = new JButton("Upload Synology");
        btnUploadSynology.addActionListener(e -> uploadSynologyBatch());
        filterPanel.add(btnUploadSynology);

        JButton btnClose = new JButton("Tutup");
        btnClose.addActionListener(e -> dispose());
        filterPanel.add(btnClose);

        statusPanel.add(filterPanel, BorderLayout.EAST);
        
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create search panel
     */
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Pencarian"));
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        
        // Date From
        panel.add(new JLabel("Dari Tanggal:"));
        txtDateFrom = new JTextField(10);
        txtDateFrom.setText(new SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date()));
        panel.add(txtDateFrom);
        
        // Date To
        panel.add(new JLabel("s.d."));
        txtDateTo = new JTextField(10);
        txtDateTo.setText(new SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date()));
        panel.add(txtDateTo);
        
        // Keyword
        panel.add(new JLabel("Keyword:"));
        txtKeyword = new JTextField(20);
        panel.add(txtKeyword);
        
        // Status
        panel.add(new JLabel("Status:"));
        cmbStatus = new JComboBox<>(new String[]{"Semua", "Ralan", "Ranap"});
        panel.add(cmbStatus);
        
        // Buttons
        btnSearch = new JButton("Cari");
        btnSearch.addActionListener(e -> loadPatients());
        panel.add(btnSearch);
        
        btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> {
            txtKeyword.setText("");
            loadPatients();
        });
        panel.add(btnRefresh);
        
        JButton btnBatchSertisign = new JButton("Batch TTE Sertisign");
        btnBatchSertisign.addActionListener(e -> batchSertisign());
        panel.add(btnBatchSertisign);
        
        return panel;
    }
    
    /**
     * Load initial data (today's patients)
     */
    private void loadInitialData() {
        loadPatients();
    }
    
    /**
     * Load patients from database
     */
    private void loadPatients() {
        lblStatus.setText("Status: Loading data...");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                patients.clear();
                
                String dateFrom = txtDateFrom.getText().trim();
                String dateTo = txtDateTo.getText().trim();
                String keyword = txtKeyword.getText().trim();
                String status = cmbStatus.getSelectedItem().toString();
                String caraBayar = cmbCaraBayar.getSelectedItem().toString();
                
                // Build query
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT reg_periksa.no_rawat, reg_periksa.tgl_registrasi, reg_periksa.jam_reg, ");
                sql.append("reg_periksa.no_rkm_medis, pasien.nm_pasien, reg_periksa.almt_pj, ");
                sql.append("poliklinik.nm_poli, dokter.nm_dokter, reg_periksa.status_lanjut, penjab.png_jawab ");
                sql.append("FROM reg_periksa ");
                sql.append("INNER JOIN dokter ON reg_periksa.kd_dokter=dokter.kd_dokter ");
                sql.append("INNER JOIN pasien ON reg_periksa.no_rkm_medis=pasien.no_rkm_medis ");
                sql.append("INNER JOIN poliklinik ON reg_periksa.kd_poli=poliklinik.kd_poli ");
                sql.append("INNER JOIN penjab ON reg_periksa.kd_pj=penjab.kd_pj ");
                sql.append("WHERE reg_periksa.tgl_registrasi BETWEEN ? AND ? ");
                
                if (!status.equals("Semua")) {
                    sql.append("AND reg_periksa.status_lanjut = ? ");
                }

                if (!caraBayar.equals("Semua")) {
                    sql.append("AND penjab.png_jawab = ? ");
                }

                if (!keyword.isEmpty()) {
                    sql.append("AND (reg_periksa.no_rawat LIKE ? OR ");
                    sql.append("reg_periksa.no_rkm_medis LIKE ? OR ");
                    sql.append("pasien.nm_pasien LIKE ? OR ");
                    sql.append("dokter.nm_dokter LIKE ? OR ");
                    sql.append("poliklinik.nm_poli LIKE ?) ");
                }
                
                sql.append("ORDER BY reg_periksa.tgl_registrasi DESC, reg_periksa.jam_reg DESC ");
                sql.append("LIMIT 100");
                
                try (PreparedStatement ps = koneksi.prepareStatement(sql.toString())) {
                    int paramIndex = 1;
                    ps.setString(paramIndex++, Valid.SetTgl(dateFrom));
                    ps.setString(paramIndex++, Valid.SetTgl(dateTo));
                    
                    if (!status.equals("Semua")) {
                        ps.setString(paramIndex++, status);
                    }

                    if (!caraBayar.equals("Semua")) {
                        ps.setString(paramIndex++, caraBayar);
                    }

                    if (!keyword.isEmpty()) {
                        String likeKeyword = "%" + keyword + "%";
                        ps.setString(paramIndex++, likeKeyword);
                        ps.setString(paramIndex++, likeKeyword);
                        ps.setString(paramIndex++, likeKeyword);
                        ps.setString(paramIndex++, likeKeyword);
                        ps.setString(paramIndex++, likeKeyword);
                    }
                    
                    ResultSet rs = ps.executeQuery();
                    
                    while (rs.next()) {
                        PatientWithBerkas patient = new PatientWithBerkas();
                        patient.noRawat = rs.getString("no_rawat");
                        patient.noRM = rs.getString("no_rkm_medis");
                        patient.namaPasien = rs.getString("nm_pasien");
                        patient.alamat = rs.getString("almt_pj");
                        patient.tglRegistrasi = rs.getString("tgl_registrasi");
                        patient.jamReg = rs.getString("jam_reg");
                        patient.nmPoli = rs.getString("nm_poli");
                        patient.nmDokter = rs.getString("nm_dokter");
                        patient.statusLanjut = rs.getString("status_lanjut");
                        patient.pngJawab = rs.getString("png_jawab");
                        
                        // Load berkas for this patient
                        loadBerkasForPatient(patient);
                        
                        patients.add(patient);
                    }
                    
                    rs.close();
                } catch (Exception e) {
                    System.err.println("Error loading patients: " + e.getMessage());
                    e.printStackTrace();
                }
                
                return null;
            }
            
            @Override
            protected void done() {
                refreshDisplay();
                lblStatus.setText("Status: " + patients.size() + " pasien ditemukan");
                setCursor(Cursor.getDefaultCursor());
            }
        };
        
        worker.execute();
    }
    
    /**
     * Load berkas for a patient
     */
    private void loadBerkasForPatient(PatientWithBerkas patient) {
        String sql = "SELECT berkas_digital_perawatan.kode, master_berkas_digital.nama, " +
                    "berkas_digital_perawatan.lokasi_file, berkas_digital_perawatan.nip " +
                    "FROM berkas_digital_perawatan " +
                    "INNER JOIN master_berkas_digital ON berkas_digital_perawatan.kode=master_berkas_digital.kode " +
                    "WHERE berkas_digital_perawatan.no_rawat=? " +
                    "ORDER BY berkas_digital_perawatan.kode ASC";
        
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, patient.noRawat);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                BerkasItem berkas = new BerkasItem();
                berkas.kode = rs.getString("kode");
                berkas.namaBerkas = rs.getString("nama");
                berkas.lokasiFile = rs.getString("lokasi_file");
                berkas.nip = rs.getString("nip");
                berkas.selected = true; // Auto-select all berkas
                patient.berkasList.add(berkas);
            }
            
            rs.close();
        } catch (Exception e) {
            System.err.println("Error loading berkas for " + patient.noRawat + ": " + e.getMessage());
        }
    }
    
    /**
     * Refresh display with current patient data
     */
    private void refreshDisplay() {
        pnlPatientList.removeAll();
        
        for (PatientWithBerkas patient : patients) {
            JPanel patientPanel = createPatientPanel(patient);
            pnlPatientList.add(patientPanel);
            pnlPatientList.add(Box.createVerticalStrut(5));
        }
        
        pnlPatientList.revalidate();
        pnlPatientList.repaint();
    }
    
    /**
     * Create panel for one patient
     */
    private JPanel createPatientPanel(PatientWithBerkas patient) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(new Color(255, 250, 250));
        
        // Main content panel (horizontal layout)
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 5, 0, 5);
        gbc.gridy = 0;
        
        // Column 1: Actions (10%)
        gbc.gridx = 0;
        gbc.weightx = 0.10;
        contentPanel.add(createActionsPanel(patient), gbc);
        
        // Column 2: Patient Data (30%)
        gbc.gridx = 1;
        gbc.weightx = 0.30;
        contentPanel.add(createPatientDataPanel(patient), gbc);
        
        // Column 3: Registration (25%)
        gbc.gridx = 2;
        gbc.weightx = 0.25;
        contentPanel.add(createRegistrationPanel(patient), gbc);
        
        // Column 4: Berkas (35%)
        gbc.gridx = 3;
        gbc.weightx = 0.35;
        contentPanel.add(createBerkasPanel(patient), gbc);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create actions panel for patient
     */
    private JPanel createActionsPanel(PatientWithBerkas patient) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        JButton btnGabungSimple = new JButton("Gabung PDF");
        btnGabungSimple.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGabungSimple.setMaximumSize(new Dimension(150, 30));
        btnGabungSimple.addActionListener(e -> gabungPDFSimple(patient));
        
        JButton btnGabungEstafet = new JButton("TTE BSRE");
        btnGabungEstafet.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGabungEstafet.setMaximumSize(new Dimension(150, 30));
        btnGabungEstafet.addActionListener(e -> gabungPDFEstafet(patient));
        
        JButton btnGabungSertisign = new JButton("TTE Sertisign");
        btnGabungSertisign.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGabungSertisign.setMaximumSize(new Dimension(150, 30));
        btnGabungSertisign.addActionListener(e -> gabungPDFSertisign(patient));
        
        
        
        JButton btnUploadSynSingle = new JButton("Upload Synology");
        btnUploadSynSingle.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnUploadSynSingle.setMaximumSize(new Dimension(150, 30));
        btnUploadSynSingle.addActionListener(e -> uploadSynologySingle(patient));

        panel.add(btnGabungSimple);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btnUploadSynSingle);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btnGabungEstafet);
        panel.add(Box.createVerticalStrut(5));
        panel.add(btnGabungSertisign);

        return panel;
    }
    
    /**
     * Create patient data panel
     */
    private JPanel createPatientDataPanel(PatientWithBerkas patient) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Data Pasien"));
        
        panel.add(createInfoLabel("No Rawat", patient.noRawat));
        panel.add(createInfoLabel("No RM", patient.noRM));
        panel.add(createInfoLabel("Nama", patient.namaPasien));
        panel.add(createInfoLabel("Alamat", patient.alamat));
        
        return panel;
    }
    
    /**
     * Create registration panel
     */
    private JPanel createRegistrationPanel(PatientWithBerkas patient) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Registrasi"));
        
        panel.add(createInfoLabel("Tanggal", patient.tglRegistrasi + " " + patient.jamReg));
        panel.add(createInfoLabel("Poliklinik", patient.nmPoli));
        panel.add(createInfoLabel("Dokter", patient.nmDokter));
        panel.add(createInfoLabel("Status", patient.statusLanjut + " (" + patient.pngJawab + ")"));
        
        return panel;
    }
    
    /**
     * Create berkas panel
     */
    private JPanel createBerkasPanel(PatientWithBerkas patient) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Berkas Digital (" + patient.berkasList.size() + ")"));
        
        JPanel berkasListPanel = new JPanel();
        berkasListPanel.setLayout(new BoxLayout(berkasListPanel, BoxLayout.Y_AXIS));
        berkasListPanel.setOpaque(false);
        
        if (patient.berkasList.isEmpty()) {
            berkasListPanel.add(new JLabel("Tidak ada berkas"));
        } else {
            int no = 1;
            for (BerkasItem berkas : patient.berkasList) {
                JPanel berkasItemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
                berkasItemPanel.setOpaque(false);
                
                JCheckBox chk = new JCheckBox();
                chk.setSelected(berkas.selected);
                chk.addActionListener(e -> berkas.selected = chk.isSelected());
                
                JLabel lblNo = new JLabel(no + ".");
                JButton btnView = new JButton(berkas.namaBerkas);
                btnView.setBorderPainted(false);
                btnView.setContentAreaFilled(false);
                btnView.setForeground(Color.BLUE);
                btnView.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnView.addActionListener(e -> viewBerkas(berkas));
                
                berkasItemPanel.add(chk);
                berkasItemPanel.add(lblNo);
                berkasItemPanel.add(btnView);
                
                JButton btnDelete = new JButton();
                btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/delete-16x16.png")));
                btnDelete.setPreferredSize(new Dimension(20, 20));
                btnDelete.setBorderPainted(false);
                btnDelete.setContentAreaFilled(false);
                btnDelete.setToolTipText("Hapus berkas ini");
                btnDelete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnDelete.addActionListener(e -> deleteBerkas(patient, berkas));
                berkasItemPanel.add(btnDelete);
                
                berkasListPanel.add(berkasItemPanel);
                no++;
            }
            
            // Select All checkbox
            JPanel selectAllPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            selectAllPanel.setOpaque(false);
            JCheckBox chkSelectAll = new JCheckBox("Pilih Semua");
            chkSelectAll.addActionListener(e -> {
                boolean selected = chkSelectAll.isSelected();
                for (BerkasItem berkas : patient.berkasList) {
                    berkas.selected = selected;
                }
                refreshDisplay();
            });
            selectAllPanel.add(chkSelectAll);
            berkasListPanel.add(selectAllPanel);
        }
        
        JScrollPane scroll = new JScrollPane(berkasListPanel);
        scroll.setPreferredSize(new Dimension(300, 120));
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Create info label
     */
    private JPanel createInfoLabel(String label, String value) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        panel.setOpaque(false);
        
        JLabel lblLabel = new JLabel(label + ":");
        lblLabel.setFont(lblLabel.getFont().deriveFont(Font.BOLD));
        lblLabel.setPreferredSize(new Dimension(80, 20));
        
        JLabel lblValue = new JLabel(value != null ? value : "-");
        
        panel.add(lblLabel);
        panel.add(lblValue);
        
        return panel;
    }
    
    // ========== ACTION METHODS ==========
    
    /**
     * Delete berkas from database
     */
    private void deleteBerkas(PatientWithBerkas patient, BerkasItem berkas) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Yakin ingin menghapus berkas '" + berkas.namaBerkas + "'?", 
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "DELETE FROM berkas_digital_perawatan WHERE no_rawat=? AND kode=? AND lokasi_file=?";
                PreparedStatement ps = koneksi.prepareStatement(sql);
                ps.setString(1, patient.noRawat);
                ps.setString(2, berkas.kode);
                ps.setString(3, berkas.lokasiFile);
                
                int result = ps.executeUpdate();
                ps.close();
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "Berkas berhasil dihapus.");
                    // Remove from list and refresh
                    patient.berkasList.remove(berkas);
                    refreshDisplay();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus berkas. Data mungkin sudah tidak ada.");
                }
            } catch (Exception e) {
                System.err.println("Error deleting berkas: " + e.getMessage());
                JOptionPane.showMessageDialog(this, "Gagal menghapus berkas: " + e.getMessage());
            }
        }
    }

    /**
     * View berkas PDF
     */
    private void viewBerkas(BerkasItem berkas) {
        try {
            String cleanLokasi = berkas.lokasiFile;
            
            // Remove berkasrawat/ prefix if exists
            if (cleanLokasi.startsWith("berkasrawat/")) {
                cleanLokasi = cleanLokasi.substring("berkasrawat/".length());
            }
            
            // Remove leading slash if exists
            if (cleanLokasi.startsWith("/")) {
                cleanLokasi = cleanLokasi.substring(1);
            }
            
            // Build URL - lokasi file already contains pages/upload/ path
            String url = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + 
                        "/" + koneksiDB.HYBRIDWEB() + "/berkasrawat/" + cleanLokasi;
            
            Valid.panggilUrl2(url);
        } catch (Exception e) {
            System.err.println("Error viewing berkas: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Gagal membuka berkas: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Gabung PDF Simple
     */
    private void gabungPDFSimple(PatientWithBerkas patient) {
        List<BerkasItem> selected = getSelectedBerkas(patient);
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih berkas yang akan digabung!", 
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        lblStatus.setText("Status: Menggabungkan PDF untuk " + patient.namaPasien + "...");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        try {
            File mergedFile = mergePDFs(selected, patient.noRawat);
            if (mergedFile != null && mergedFile.exists()) {
                lblStatus.setText("Status: PDF berhasil digabung");
                Valid.panggilUrl2(mergedFile.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "PDF berhasil digabung!\nFile: " + mergedFile.getName(), 
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            lblStatus.setText("Status: Gagal menggabungkan PDF");
            JOptionPane.showMessageDialog(this, "Gagal menggabungkan PDF:\n" + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
    
    /**
     * Load daftar cara bayar dari tabel penjab
     */
    private void loadCaraBayar() {
        try (PreparedStatement ps = koneksi.prepareStatement("SELECT kd_pj, png_jawab FROM penjab ORDER BY png_jawab");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cmbCaraBayar.addItem(rs.getString("png_jawab"));
            }
        } catch (Exception e) {
            System.out.println("Notif loadCaraBayar : " + e);
        }
    }

    /**
     * Gabung PDF Batch Local - gabung semua berkas per pasien, simpan ke folder BerkasDigitalPerawatan
     * Nama file = no_sep.pdf
     */
    private void gabungPDFBatchLocal() {
        if (patients == null || patients.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data pasien. Silakan cari data terlebih dahulu.",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String userHome = System.getProperty("user.home");
        File folder = new File(userHome, "BerkasDigitalPerawatan");
        if (!folder.exists()) folder.mkdirs();

        int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Gabung PDF batch untuk " + patients.size() + " pasien?\nFile akan disimpan di:\n" + folder.getAbsolutePath(),
            "Konfirmasi Batch", JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) return;

        // Progress dialog
        JDialog dlgProgress = new JDialog(this, "Proses Batch", false);
        dlgProgress.setLayout(new BorderLayout(10, 10));
        dlgProgress.setSize(450, 130);
        dlgProgress.setLocationRelativeTo(this);

        JProgressBar progressBar = new JProgressBar(0, patients.size());
        progressBar.setStringPainted(true);
        progressBar.setString("0/" + patients.size());
        progressBar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JLabel lblProgress = new JLabel("Memulai...", SwingConstants.CENTER);
        lblProgress.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));

        dlgProgress.add(lblProgress, BorderLayout.NORTH);
        dlgProgress.add(progressBar, BorderLayout.CENTER);
        dlgProgress.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        final File folderFinal = folder;

        SwingWorker<int[], Void> worker = new SwingWorker<int[], Void>() {
            @Override
            protected int[] doInBackground() {
                int sukses = 0, gagal = 0, skip = 0;

                for (int p = 0; p < patients.size(); p++) {
                    PatientWithBerkas patient = patients.get(p);
                    final int idx = p + 1;
                    final String nama = patient.namaPasien;
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setValue(idx);
                        progressBar.setString(idx + "/" + patients.size());
                        lblProgress.setText(idx + ". " + nama);
                    });

                    List<BerkasItem> allBerkas = patient.berkasList;
                    if (allBerkas == null || allBerkas.isEmpty()) { skip++; continue; }

                    String noSep = Sequel.cariIsi("SELECT no_sep FROM bridging_sep WHERE no_rawat=? LIMIT 1", patient.noRawat);
                    if (noSep == null || noSep.trim().isEmpty()) noSep = patient.noRawat.replace("/", "_");

                    File outputFile = new File(folderFinal, noSep + ".pdf");

                    try {
                        PDFMergerUtility merger = new PDFMergerUtility();
                        List<File> tempFiles = new ArrayList<>();
                        int fileCount = 0;

                        for (BerkasItem item : allBerkas) {
                            try {
                                String cleanLokasi = item.lokasiFile;
                                if (cleanLokasi.startsWith("berkasrawat/")) cleanLokasi = cleanLokasi.substring("berkasrawat/".length());
                                if (cleanLokasi.startsWith("/")) cleanLokasi = cleanLokasi.substring(1);

                                String path = "/" + koneksiDB.HYBRIDWEB() + "/berkasrawat/" + cleanLokasi;
                                File tempFile = File.createTempFile("batch_src_", ".pdf");
                                tempFile.deleteOnExit();

                                try (InputStream is = getStreamFromUrl(koneksiDB.HOSTHYBRIDWEB(),
                                        Integer.parseInt(koneksiDB.PORTWEB()), path);
                                     FileOutputStream fos = new FileOutputStream(tempFile)) {
                                    byte[] buffer = new byte[8192];
                                    int bytesRead;
                                    while ((bytesRead = is.read(buffer)) != -1) fos.write(buffer, 0, bytesRead);
                                }
                                merger.addSource(tempFile);
                                tempFiles.add(tempFile);
                                fileCount++;
                            } catch (Exception e) {
                                System.out.println("Skip berkas: " + item.namaBerkas + " - " + e.getMessage());
                            }
                        }

                        if (fileCount > 0) {
                            merger.setDestinationFileName(outputFile.getAbsolutePath());
                            merger.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());
                            sukses++;
                        } else { skip++; }

                        for (File f : tempFiles) f.delete();
                    } catch (Exception e) {
                        System.err.println("Gagal batch " + patient.noRawat + ": " + e.getMessage());
                        gagal++;
                    }
                }
                return new int[]{sukses, gagal, skip};
            }

            @Override
            protected void done() {
                dlgProgress.dispose();
                try {
                    int[] result = get();
                    lblStatus.setText("Status: Batch selesai. Sukses: " + result[0] + ", Gagal: " + result[1] + ", Skip: " + result[2]);
                    JOptionPane.showMessageDialog(DlgBerkasRawat2.this,
                        "Batch selesai!\n\nSukses: " + result[0] + "\nGagal: " + result[1] + "\nSkip (kosong): " + result[2] +
                        "\n\nFolder: " + folderFinal.getAbsolutePath(),
                        "Hasil Batch", JOptionPane.INFORMATION_MESSAGE);
                    Desktop.getDesktop().open(folderFinal);
                } catch (Exception e) {
                    System.out.println("Notif batch done : " + e.getMessage());
                }
            }
        };

        worker.execute();
        dlgProgress.setVisible(true);
    }

    /**
     * Upload single pasien ke Synology NAS
     */
    private void uploadSynologySingle(PatientWithBerkas patient) {
        List<BerkasItem> selected = getSelectedBerkas(patient);
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih berkas yang akan diupload!",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        lblStatus.setText("Status: Upload ke Synology untuk " + patient.namaPasien + "...");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            File mergedFile = mergePDFs(selected, patient.noRawat);
            if (mergedFile != null && mergedFile.exists()) {
                String noSep = Sequel.cariIsi("SELECT no_sep FROM bridging_sep WHERE no_rawat=? LIMIT 1", patient.noRawat);
                if (noSep == null || noSep.trim().isEmpty()) noSep = patient.noRawat.replace("/", "_");
                String fileName = noSep + ".pdf";

                SynologyUploader synology = new SynologyUploader();
                try {
                    if (synology.login()) {
                        if (synology.upload(mergedFile, fileName, patient.tglRegistrasi)) {
                            lblStatus.setText("Status: Upload Synology berhasil");
                            JOptionPane.showMessageDialog(this, "Upload ke Synology berhasil!\nFile: " + fileName,
                                "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            lblStatus.setText("Status: Upload Synology gagal");
                            JOptionPane.showMessageDialog(this, "Gagal upload ke Synology.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        lblStatus.setText("Status: Gagal login Synology");
                        JOptionPane.showMessageDialog(this, "Gagal login ke Synology NAS.\nPeriksa koneksi dan credential.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } finally {
                    synology.close();
                    mergedFile.delete();
                }
            }
        } catch (Exception e) {
            System.err.println("Error upload single: " + e.getMessage());
            lblStatus.setText("Status: Gagal upload Synology");
            JOptionPane.showMessageDialog(this, "Gagal upload:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * Upload batch ke Synology NAS
     * Gabung PDF per pasien lalu upload ke folder BERKAS RAWAT JALAN
     */
    private void uploadSynologyBatch() {
        if (patients == null || patients.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data pasien. Silakan cari data terlebih dahulu.",
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this,
            "Upload " + patients.size() + " pasien ke Synology NAS?",
            "Konfirmasi Upload", JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) return;

        // Progress dialog
        JDialog dlgProgress = new JDialog(this, "Upload Synology", false);
        dlgProgress.setLayout(new BorderLayout(10, 10));
        dlgProgress.setSize(450, 130);
        dlgProgress.setLocationRelativeTo(this);

        JProgressBar progressBar = new JProgressBar(0, patients.size());
        progressBar.setStringPainted(true);
        progressBar.setString("0/" + patients.size());
        progressBar.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JLabel lblProgress = new JLabel("Login ke Synology...", SwingConstants.CENTER);
        lblProgress.setBorder(BorderFactory.createEmptyBorder(10, 15, 0, 15));

        dlgProgress.add(lblProgress, BorderLayout.NORTH);
        dlgProgress.add(progressBar, BorderLayout.CENTER);
        dlgProgress.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        SwingWorker<int[], Void> worker = new SwingWorker<int[], Void>() {
            @Override
            protected int[] doInBackground() {
                int sukses = 0, gagal = 0, skip = 0;

                SynologyUploader synology = new SynologyUploader();
                try {
                    if (!synology.login()) {
                        SwingUtilities.invokeLater(() -> {
                            dlgProgress.dispose();
                            JOptionPane.showMessageDialog(DlgBerkasRawat2.this,
                                "Gagal login ke Synology NAS.\nPeriksa koneksi jaringan dan credential.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        });
                        return new int[]{0, 0, 0};
                    }

                    for (int p = 0; p < patients.size(); p++) {
                        PatientWithBerkas patient = patients.get(p);
                        final int idx = p + 1;
                        final String nama = patient.namaPasien;
                        SwingUtilities.invokeLater(() -> {
                            progressBar.setValue(idx);
                            progressBar.setString(idx + "/" + patients.size());
                            lblProgress.setText(idx + ". " + nama);
                        });

                        List<BerkasItem> allBerkas = patient.berkasList;
                        if (allBerkas == null || allBerkas.isEmpty()) { skip++; continue; }

                        // Nama file = no_sep
                        String noSep = Sequel.cariIsi("SELECT no_sep FROM bridging_sep WHERE no_rawat=? LIMIT 1", patient.noRawat);
                        if (noSep == null || noSep.trim().isEmpty()) noSep = patient.noRawat.replace("/", "_");
                        String fileName = noSep + ".pdf";

                        try {
                            // Gabung PDF ke temp file
                            PDFMergerUtility merger = new PDFMergerUtility();
                            List<File> tempFiles = new ArrayList<>();
                            int fileCount = 0;

                            for (BerkasItem item : allBerkas) {
                                try {
                                    String cleanLokasi = item.lokasiFile;
                                    if (cleanLokasi.startsWith("berkasrawat/")) cleanLokasi = cleanLokasi.substring("berkasrawat/".length());
                                    if (cleanLokasi.startsWith("/")) cleanLokasi = cleanLokasi.substring(1);

                                    String path = "/" + koneksiDB.HYBRIDWEB() + "/berkasrawat/" + cleanLokasi;
                                    File tempFile = File.createTempFile("syn_src_", ".pdf");
                                    tempFile.deleteOnExit();

                                    try (InputStream is = getStreamFromUrl(koneksiDB.HOSTHYBRIDWEB(),
                                            Integer.parseInt(koneksiDB.PORTWEB()), path);
                                         FileOutputStream fos = new FileOutputStream(tempFile)) {
                                        byte[] buffer = new byte[8192];
                                        int bytesRead;
                                        while ((bytesRead = is.read(buffer)) != -1) fos.write(buffer, 0, bytesRead);
                                    }
                                    merger.addSource(tempFile);
                                    tempFiles.add(tempFile);
                                    fileCount++;
                                } catch (Exception e) {
                                    System.out.println("Skip: " + item.namaBerkas + " - " + e.getMessage());
                                }
                            }

                            if (fileCount > 0) {
                                File mergedFile = File.createTempFile("syn_merged_", ".pdf");
                                mergedFile.deleteOnExit();
                                merger.setDestinationFileName(mergedFile.getAbsolutePath());
                                merger.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());

                                // Upload ke Synology
                                if (synology.upload(mergedFile, fileName, patient.tglRegistrasi)) {
                                    sukses++;
                                } else {
                                    gagal++;
                                }
                                mergedFile.delete();
                            } else {
                                skip++;
                            }

                            for (File f : tempFiles) f.delete();
                        } catch (Exception e) {
                            System.err.println("Gagal upload " + patient.noRawat + ": " + e.getMessage());
                            gagal++;
                        }
                    }
                } finally {
                    synology.close();
                }
                return new int[]{sukses, gagal, skip};
            }

            @Override
            protected void done() {
                dlgProgress.dispose();
                try {
                    int[] result = get();
                    if (result[0] + result[1] + result[2] > 0) {
                        lblStatus.setText("Status: Upload Synology selesai. Sukses: " + result[0] + ", Gagal: " + result[1] + ", Skip: " + result[2]);
                        JOptionPane.showMessageDialog(DlgBerkasRawat2.this,
                            "Upload Synology selesai!\n\nSukses: " + result[0] + "\nGagal: " + result[1] + "\nSkip (kosong): " + result[2],
                            "Hasil Upload", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (Exception e) {
                    System.out.println("Notif upload done : " + e.getMessage());
                }
            }
        };

        worker.execute();
        dlgProgress.setVisible(true);
    }

    /**
     * Gabung PDF + Upload ke Berkas Klaim (TTE dilakukan di DlgBerkasKlaim)
     */
    private void gabungPDFEstafet(PatientWithBerkas patient) {
        List<BerkasItem> selected = getSelectedBerkas(patient);
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih berkas yang akan digabung!", 
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create progress dialog
        JDialog progressDialog = new JDialog(this, "Progress", true);
        progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        progressDialog.setSize(400, 150);
        progressDialog.setLocationRelativeTo(this);
        progressDialog.setLayout(new BorderLayout(10, 10));
        
        JLabel lblProgress = new JLabel("Memproses...", SwingConstants.CENTER);
        lblProgress.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        progressDialog.add(lblProgress, BorderLayout.CENTER);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        progressDialog.add(progressBar, BorderLayout.SOUTH);
        
        // Process in background
        SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {
            private String errorMessage = null;
            private String fileName = null;
            
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    // Step 1: Merge PDFs
                    publish("Menggabungkan PDF...");
                    File mergedFile = mergePDFs(selected, patient.noRawat);
                    if (mergedFile == null || !mergedFile.exists()) {
                        errorMessage = "Gagal menggabungkan PDF";
                        return false;
                    }
                    
                    // Step 2: Upload to berkasklaim
                    publish("Mengupload ke berkas klaim...");
                    String timestamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new java.util.Date());
                    
                    // Count distinct signers for naming convention DRAFT_..._X-0.pdf
                    String signerCount = Sequel.cariIsi("SELECT count(DISTINCT nip) FROM berkas_digital_perawatan WHERE no_rawat=? AND nip IS NOT NULL AND nip!='' AND nip!='-'", patient.noRawat);
                    if (signerCount.equals("") || signerCount.equals("0")) signerCount = "1";
                    
                    fileName = "DRAFT_" + patient.noRawat.replace("/", "_") + "_" + timestamp + "_" + signerCount + "-0.pdf";
                    
                    boolean uploaded = uploadToBerkasKlaim(mergedFile, fileName, patient.noRawat);
                    
                    if (!uploaded) {
                        errorMessage = "Gagal upload ke berkas klaim";
                        return false;
                    }
                    
                    publish("Selesai!");
                    return true;
                    
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    e.printStackTrace();
                    return false;
                }
            }
            
            @Override
            protected void process(java.util.List<String> chunks) {
                if (!chunks.isEmpty()) {
                    lblProgress.setText(chunks.get(chunks.size() - 1));
                }
            }
            
            @Override
            protected void done() {
                progressDialog.dispose();
                
                try {
                    Boolean success = get();
                    if (success) {
                        lblStatus.setText("Status: Berhasil upload ke berkas klaim");
                        JOptionPane.showMessageDialog(DlgBerkasRawat2.this, 
                            "Berkas berhasil digabung dan diupload!\n\n" + 
                            "File: " + fileName + "\n" +
                            "Lokasi: berkasklaim/\n\n" +
                            "Silakan lakukan TTE di menu Berkas Klaim", 
                            "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        lblStatus.setText("Status: Gagal upload ke berkas klaim");
                        JOptionPane.showMessageDialog(DlgBerkasRawat2.this, 
                            "Gagal upload ke berkas klaim:\n" + errorMessage, 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
        progressDialog.setVisible(true);
    }
    
    
    private boolean uploadToBerkasKlaim(File file, String fileName, String noRawat) {
        try {
            String uploadUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + 
                              "/" + koneksiDB.HYBRIDWEB() + "/berkasklaim/unggah.php";
            
            // System.out.println("Uploading to: " + uploadUrl);
            // System.out.println("File: " + fileName + " (" + file.length() + " bytes)");
            
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost uploadFile = new HttpPost(uploadUrl);
            
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.EXTENDED);
            builder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, fileName);
            builder.addTextBody("no_rawat", noRawat, ContentType.TEXT_PLAIN);
            
            HttpEntity multipart = builder.build();
            uploadFile.setEntity(multipart);
            
            try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
                int statusCode = response.getCode();
                
                if (statusCode == 200) {
                    // Success, no need for logs
                    // Save to database
                    String sql = "INSERT INTO berkas_klaim (no_rawat, lokasi_file, tgl_upload) " +
                               "VALUES (?, ?, NOW()) " +
                               "ON DUPLICATE KEY UPDATE lokasi_file=?, tgl_upload=NOW()";
                    PreparedStatement ps = koneksi.prepareStatement(sql);
                    ps.setString(1, noRawat);
                    ps.setString(2, fileName);
                    ps.setString(3, fileName);
                    ps.executeUpdate();
                    ps.close();
                    
                    return true;
                } else {
                    System.err.println("Upload failed with status code: " + statusCode);
                }
            }
            httpClient.close();
            
            return false;
            
        } catch (Exception e) {
            System.err.println("Error uploading to berkasklaim: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gabung PDF + TTE Sertisign
     */
    private void gabungPDFSertisign(PatientWithBerkas patient) {
        List<BerkasItem> selected = getSelectedBerkas(patient);
        if (selected.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Silakan pilih berkas yang akan digabung!", 
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Get Signers
        SertisignMultiSignerService sertisignService = new SertisignMultiSignerService();
        List<SignerConfig> signers = sertisignService.getSignersFromBerkas(patient.noRawat);
        
        if (signers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ditemukan dokter/petugas penanda tangan yang memiliki Email dan Tag TTE.", 
                "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirm
        StringBuilder sb = new StringBuilder("Kirim permintaan TTE Sertisign ke penanda tangan berikut?\n\n");
        for (SignerConfig s : signers) {
            sb.append("- ").append(s.getSignerName()).append(" (").append(s.getEmail()).append(")\n");
        }
        int confirm = JOptionPane.showConfirmDialog(this, sb.toString(), "Konfirmasi TTE Sertisign", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Progress Dialog
        JDialog progressDialog = new JDialog(this, "Progress", true);
        progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        progressDialog.setSize(400, 150);
        progressDialog.setLocationRelativeTo(this);
        progressDialog.setLayout(new BorderLayout(10, 10));
        
        JLabel lblProgress = new JLabel("Memproses Multi-Signer TTE...", SwingConstants.CENTER);
        lblProgress.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        progressDialog.add(lblProgress, BorderLayout.CENTER);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        progressDialog.add(progressBar, BorderLayout.SOUTH);

        SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {
            String errorMessage = "";
            String transactionId = "";

            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    publish("Menggabungkan PDF...");
                    File mergedFile = mergePDFs(selected, patient.noRawat);
                    if (mergedFile == null || !mergedFile.exists()) {
                        errorMessage = "Gagal menggabungkan PDF";
                        return false;
                    }

                    publish("Mengirim permintaan ke Sertisign...");
                    transactionId = sertisignService.sendMultiSignerRequest(
                        mergedFile, 
                        signers, 
                        100, 40, // Default QR size
                        akses.getnamars(), 
                        akses.getkabupatenrs()
                    );

                    if (transactionId != null && !transactionId.isEmpty()) {
                        publish("Menyimpan status transaksi...");
                        String sep = Sequel.cariIsi("SELECT no_sep FROM bridging_sep WHERE no_rawat=?", patient.noRawat);
                        if (sep == null || sep.trim().isEmpty()) sep = patient.noRawat.replace("/", "");
                        
                        sertisignService.saveStatusTte(patient.noRawat, sep, transactionId, mergedFile.getName(), "", akses.getkode());
                        sertisignService.saveSigners(transactionId, signers);

                        publish("⏳ Menunggu tanda tangan selesai...");
                        // Use No SEP as filename
                        String signedPdfFilename = sep + "_SIGNED.pdf";
                        File signedFile = new File(signedPdfFilename);
                        
                        // Retry for a limited time (5 minutes: 30 attempts with 10s delay)
                        boolean downloadSuccess = sertisignService.downloadSignedPdf(
                            transactionId,
                            signedFile.getAbsolutePath(),
                            30, // 30 attempts
                            10000 // 10 seconds delay
                        );

                        if (downloadSuccess) {
                            publish("☁️ Upload ke Nextcloud...");
                            sertisignService.updateStatus(transactionId, "SIGNED", null);
                            if (uploadToNextcloud(signedFile, patient.noRawat)) {
                                publish("✅ Berhasil diunggah ke Nextcloud");
                                return true;
                            } else {
                                errorMessage = "TTE Berhasil tapi gagal upload ke Nextcloud";
                                return false;
                            }
                        } else {
                            // If not signed yet, it's still okay (asynchronous)
                            publish("⏳ Permintaan terkirim (Menunggu tanda tangan)");
                            return true;
                        }
                    } else {
                        errorMessage = "Gagal mendapatkan Transaction ID dari Sertisign";
                        return false;
                    }
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void process(List<String> chunks) {
                lblProgress.setText(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                progressDialog.dispose();
                try {
                    if (get()) {
                        String msg = "Permintaan TTE Sertisign berhasil dikirim!\nTransaction ID: " + transactionId;
                        if (lblProgress.getText().contains("Nextcloud")) {
                            msg += "\n\nDokumen SUDAH ditandatangani dan berhasil diupload ke Nextcloud.";
                        } else {
                            msg += "\n\nSilakan instruksikan penanda tangan untuk cek Email/Aplikasi Sertisign.\n" +
                                   "Dokumen akan otomatis ke Nextcloud setelah TTE selesai (Cek Status nanti).";
                        }
                        JOptionPane.showMessageDialog(DlgBerkasRawat2.this, msg, "Informasi", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(DlgBerkasRawat2.this, "Gagal: " + errorMessage, 
                            "Kesalahan", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
        progressDialog.setVisible(true);
    }
    
    /**
     * Batch TTE Sertisign for all patients with selected berkas
     */
    private void batchSertisign() {
        if (patients == null || patients.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada data pasien yang nampil.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<PatientWithBerkas> targetPatients = new ArrayList<>();
        for (PatientWithBerkas p : patients) {
            if (!getSelectedBerkas(p).isEmpty()) {
                targetPatients.add(p);
            }
        }

        if (targetPatients.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tidak ada pasien dengan berkas terpilih untuk diproses batch.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Akan memproses TTE Sertisign untuk " + targetPatients.size() + " pasien terpilih.\nLanjutkan?", 
            "Konfirmasi Batch TTE Sertisign", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;

        // Process sequentially to avoid API rate limits and for better feedback
        // We use a recursively called process method or a worker that handles the list
        processBatchSertisign(targetPatients, 0);
    }

    private void processBatchSertisign(List<PatientWithBerkas> targetPatients, int index) {
        if (index >= targetPatients.size()) {
            JOptionPane.showMessageDialog(this, "Proses Batch TTE Sertisign Selesai!", "Selesai", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        PatientWithBerkas patient = targetPatients.get(index);
        
        // Progress Dialog specifically for batch item
        JDialog progressDialog = new JDialog(this, "Batch Progress (" + (index + 1) + "/" + targetPatients.size() + ")", true);
        progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        progressDialog.setSize(400, 150);
        progressDialog.setLocationRelativeTo(this);
        progressDialog.setLayout(new BorderLayout(10, 10));
        
        JLabel lblProgress = new JLabel("Memproses: " + patient.namaPasien, SwingConstants.CENTER);
        lblProgress.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        progressDialog.add(lblProgress, BorderLayout.CENTER);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        progressDialog.add(progressBar, BorderLayout.SOUTH);

        SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {
            String errorMessage = "";
            SertisignMultiSignerService sertisignService = new SertisignMultiSignerService();

            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    List<BerkasItem> selected = getSelectedBerkas(patient);
                    List<SignerConfig> signers = sertisignService.getSignersFromBerkas(patient.noRawat);
                    
                    if (signers.isEmpty()) {
                        errorMessage = "Tidak ditemukan penanda tangan dengan Email/Tag TTE.";
                        return false;
                    }

                    publish("Menggabungkan PDF...");
                    File mergedFile = mergePDFs(selected, patient.noRawat);
                    
                    publish("Mengirim permintaan...");
                    String transactionId = sertisignService.sendMultiSignerRequest(
                        mergedFile, signers, 100, 40, akses.getnamars(), akses.getkabupatenrs()
                    );

                    if (transactionId != null && !transactionId.isEmpty()) {
                        publish("Simpan status...");
                        String sep = Sequel.cariIsi("SELECT no_sep FROM bridging_sep WHERE no_rawat=?", patient.noRawat);
                        if (sep == null || sep.trim().isEmpty()) sep = patient.noRawat.replace("/", "");
                        
                        sertisignService.saveStatusTte(patient.noRawat, sep, transactionId, mergedFile.getName(), "", akses.getkode());
                        sertisignService.saveSigners(transactionId, signers);
                        
                        // We don't poll for 5 minutes in batch, just send the request
                        // OR we could poll shorter. For batch, let's just send and move on
                        // to keep it moving. Status can be checked later.
                        publish("Berhasil dikirim.");
                        return true;
                    } else {
                        errorMessage = "Gagal mendapatkan Transaction ID.";
                        return false;
                    }
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    return false;
                }
            }

            @Override
            protected void process(List<String> chunks) {
                lblProgress.setText(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                progressDialog.dispose();
                try {
                    if (!get()) {
                        int skip = JOptionPane.showConfirmDialog(DlgBerkasRawat2.this, 
                            "Gagal memproses " + patient.namaPasien + ": " + errorMessage + "\nLanjutkan ke pasien berikutnya?", 
                            "Kesalahan Batch", JOptionPane.YES_NO_OPTION);
                        if (skip != JOptionPane.YES_OPTION) return;
                    }
                } catch (Exception e) {}
                
                // Process next patient
                processBatchSertisign(targetPatients, index + 1);
            }
        };

        worker.execute();
        progressDialog.setVisible(true);
    }

    /**
     * Upload signed PDF to Nextcloud
     */
    private boolean uploadToNextcloud(java.io.File signedFile, String noRawat) {
        try {
            String sep = Sequel.cariIsi("SELECT no_sep FROM bridging_sep WHERE no_rawat = ? LIMIT 1", noRawat);
            if(sep == null || sep.isEmpty()){
                sep = noRawat.replace("/", ""); 
            }
            
            String tanggal = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
            String tanggaljam = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            
            UploadPDFTte uploader = new UploadPDFTte(
                signedFile.getAbsolutePath(),
                sep,
                tanggal,
                noRawat,
                akses.getkode(),
                tanggaljam,
                "INA-CBG"
            );
            
            return uploader.upload();
        } catch (Exception e) {
            System.err.println("Error upload to Nextcloud: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get selected berkas for patient
     */
    private List<BerkasItem> getSelectedBerkas(PatientWithBerkas patient) {
        List<BerkasItem> selected = new ArrayList<>();
        for (BerkasItem berkas : patient.berkasList) {
            if (berkas.selected) {
                selected.add(berkas);
            }
        }
        return selected;
    }
    
    /**
     * Merge PDFs
     */
    private File mergePDFs(List<BerkasItem> items, String noRawat) throws Exception {
        System.out.println("--- mergePDFs() START ---");
        System.out.println("Merging " + items.size() + " files for " + noRawat);
        
        PDFMergerUtility merger = new PDFMergerUtility();
        List<File> sourceFiles = new java.util.ArrayList<>();
        int successCount = 0;
        int failCount = 0;
        
        for (BerkasItem item : items) {
            try {
                String cleanLokasi = item.lokasiFile;
                if (cleanLokasi.startsWith("berkasrawat/")) {
                    cleanLokasi = cleanLokasi.substring("berkasrawat/".length());
                }
                if (cleanLokasi.startsWith("/")) {
                    cleanLokasi = cleanLokasi.substring(1);
                }
                
                System.out.println("Downloading for merge: " + item.namaBerkas + " (" + cleanLokasi + ")");
                String path = "/" + koneksiDB.HYBRIDWEB() + "/berkasrawat/" + cleanLokasi;
                
                // Download to local temp file first to avoid network stream issues during merge
                File tempFile = File.createTempFile("merge_src_", ".pdf");
                tempFile.deleteOnExit();
                
                try (InputStream is = getStreamFromUrl(koneksiDB.HOSTHYBRIDWEB(), 
                        Integer.parseInt(koneksiDB.PORTWEB()), path);
                     FileOutputStream fos = new FileOutputStream(tempFile)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
                
                merger.addSource(tempFile);
                sourceFiles.add(tempFile);
                successCount++;
            } catch (Exception e) {
                System.out.println("⚠️ SKIP: " + item.namaBerkas + " - " + e.getMessage());
                failCount++;
            }
        }
        
        System.out.println("Merge Download Summary: " + successCount + " success, " + failCount + " failed");
        
        if (successCount == 0) {
            throw new Exception("Tidak ada berkas PDF yang valid untuk digabungkan");
        }
        
        File outputFile = File.createTempFile("merged_" + noRawat.replace("/", "_") + "_", ".pdf");
        outputFile.deleteOnExit();
        
        System.out.println("Starting PDFBox merge to: " + outputFile.getAbsolutePath());
        merger.setDestinationFileName(outputFile.getAbsolutePath());
        // Use temp file memory management for large merges
        merger.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());
        
        System.out.println("Merge successful: " + outputFile.getAbsolutePath());
        System.out.println("--- mergePDFs() END ---");
        
        // Clean up source temp files
        for (File f : sourceFiles) {
            f.delete();
        }
        
        return outputFile;
    }
    
    /**
     * Get input stream from URL (with SSL bypass)
     */
    private InputStream getStreamFromUrl(String host, int port, String path) throws Exception {
        // SSL bypass for self-signed certificates
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(X509Certificate[] certs, String authType) {}
            }
        };
        
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        
        HostnameVerifier allHostsValid = (hostname, session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        
        URI uri = new URI("http", null, host, port, path, null, null);
        URL url = uri.toURL();
        return url.openStream();
    }
    
    /**
     * Main method for testing
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            DlgBerkasRawat2 dialog = new DlgBerkasRawat2(new javax.swing.JFrame(), true);
            dialog.setVisible(true);
        });
    }
}
