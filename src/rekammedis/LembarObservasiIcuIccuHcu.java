/*
 * Form Observasi Rawat Intensif / ICU - Hemodynamic & Ventilator Monitoring
 * Based on intensive care observation form requirements
 * Modified for Hemodynamic Parameters: HR, T, RR, NIBP, IBP, CO, CVP, PAP
 * Added Ventilator Parameters: Complete ventilator monitoring system
 * By RS AL-MULK System
 */
package rekammedis;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariPetugas;
import kepegawaian.DlgCariPegawai;
import rekammedis.RMPelod2Score;
import rekammedis.RMRamsayScore;
import rekammedis.RMRASSScore;
import rekammedis.RMSOFAScore;
import rekammedis.RMAPACHEScore;

/**
 * Form Observasi Rawat Intensif (ICU/High Care Unit) - Complete Monitoring
 *
 * @author IT SIMRS
 */
public final class LembarObservasiIcuIccuHcu extends javax.swing.JDialog {

    private final DefaultTableModel tabModeObservasi, tabModeVentilator, tabModeIntakeOutput, tabModeMedikasi, tabModeCatatan,
            tabModeCatatanKeperawatan, tabModeBundleVAP, tabModeBundleCVC, tabModeBundleISK, tabModePemeriksaanPenunjang,
            tabModeGambar, tabModeCairanNutrisi, tabModeTindakan, tabModeMonitoringTambahan;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);
    private String TANGGALMUNDUR = "yes";

    public LembarObservasiIcuIccuHcu(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // Hide field tab Hemodinamik yang TIDAK ada di form fisik
        // (di-instantiate untuk back-compat & batasInput, tapi tidak ditampilkan di UI)
        jLabel4.setVisible(false); TNIBP.setVisible(false);
        jLabel5.setVisible(false); TIBP.setVisible(false);
        jLabel6.setVisible(false); TCO.setVisible(false);
        jLabel8.setVisible(false); TPAP.setVisible(false);
        jLabel10.setVisible(false); TGambaranVES.setVisible(false);
        jLabel13.setVisible(false); TEtCO2.setVisible(false);
        jLabelGCSTotal.setVisible(false); TGCSTotal.setVisible(false);
        jLabel15.setVisible(false); cmbPupilKanan.setVisible(false);
        jLabel16.setVisible(false); cmbPupilKiri.setVisible(false);
        jLabelSkalaNyeri.setVisible(false); cmbSkalaNyeri.setVisible(false);
        jLabelNilaiNyeri.setVisible(false); TNilaiNyeri.setVisible(false);
        jLabel18.setVisible(false); cmbResikoJatuh.setVisible(false);
        jLabel18b.setVisible(false); cmbKategoriResikoJatuh.setVisible(false);
        jLabel19.setVisible(false); TBredenScore.setVisible(false);
        jLabel11.setVisible(false);

        // Initialize table models with hemodynamic parameters
        tabModeObservasi = new DefaultTableModel(null, new Object[]{
            "P", "No.Rawat", "No.R.M.", "Nama Pasien", "Tgl.Observasi", "Jam",
            "BP Sys", "BP Dys", "MAP",
            "HR(/mnt)", "T(°C)", "RR(/mnt)", "CVP(cmH2O)",
            "Gambaran EKG", "SpO2(%)",
            "Kesadaran", "GCS E", "GCS V", "GCS M",
            "Refleks Pupil Ka", "Refleks Pupil Ki",
            "Motorik AKA", "Motorik AKI", "Motorik BKA", "Motorik BKI",
            "NIP", "Petugas"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }
        };
        tbObservasi.setModel(tabModeObservasi);

        // Alert Vital Sign: warna merah jika abnormal
        tbObservasi.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(java.awt.Color.WHITE);
                    c.setForeground(java.awt.Color.BLACK);
                    try {
                        String val = value == null ? "" : value.toString().trim();
                        if (!val.isEmpty()) {
                            double d = 0;
                            boolean isNum = false;
                            try { d = Double.parseDouble(val); isNum = true; } catch (Exception ex) {}
                            if (isNum) {
                                // Col 6=BP Sys, 9=HR, 10=Temp, 11=RR, 14=SpO2
                                if (column == 6 && (d < 90 || d > 140)) { c.setBackground(new java.awt.Color(255, 200, 200)); c.setForeground(java.awt.Color.RED); }
                                else if (column == 9 && (d < 60 || d > 120)) { c.setBackground(new java.awt.Color(255, 200, 200)); c.setForeground(java.awt.Color.RED); }
                                else if (column == 10 && (d < 36.0 || d > 38.5)) { c.setBackground(new java.awt.Color(255, 200, 200)); c.setForeground(java.awt.Color.RED); }
                                else if (column == 11 && (d < 12 || d > 25)) { c.setBackground(new java.awt.Color(255, 200, 200)); c.setForeground(java.awt.Color.RED); }
                                else if (column == 14 && d < 90) { c.setBackground(new java.awt.Color(255, 200, 200)); c.setForeground(java.awt.Color.RED); }
                            }
                        }
                    } catch (Exception ex) {}
                }
                return c;
            }
        });

        // Initialize Ventilator table model
        tabModeVentilator = new DefaultTableModel(null, new Object[]{
            "P", "No.Rawat", "No.R.M.", "Nama Pasien", "Tgl.Observasi", "Jam",
            "Tipe", "RR Setting", "RR Aktual", "I:E Ratio",
            "MV Setting", "MV Aktual", "Pressure Control", "Pressure Support", "PEEP",
            "FiO2/Flow(%)", "Peak(cmH2O)", "Plateau Pressure",
            "ETT/TC", "Diameter", "Kedalaman(cm)", "Tekanan Cuff",
            "NIP", "Petugas"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }
        };
        tbVentilator.setModel(tabModeVentilator);

        // KARDEX CAIRAN (Intake-Output spt lembar fisik):
        //  0=NoRawat,1=Tgl,2=No,3=Tipe(dropdown),4=NamaCairan,5=ParafPerawat(checkbox),
        //  6-29=Jam(07-06) berisi jumlah cairan (ml), 30=NIP, 31=Petugas
        //  + baris ringkasan otomatis di bawah (Total Intake/Output, Balance, Diuresis)
        //    ditandai dengan kolom No.Rawat kosong (read-only).
        tabModeIntakeOutput = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tgl.Obs", "No", "Tipe", "Nama Cairan", "Paraf Perawat",
            "07", "08", "09", "10", "11", "12", "13", "14",
            "15", "16", "17", "18", "19", "20", "21", "22", "23", "24",
            "01", "02", "03", "04", "05", "06",
            "NIP", "Petugas"
        }) {
            @Override
            public Class<?> getColumnClass(int colIndex) {
                return colIndex == 5 ? Boolean.class : Object.class;
            }
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                // Baris ringkasan (No.Rawat kosong) → read-only
                Object nr = getValueAt(rowIndex, 0);
                if (nr == null || nr.toString().isEmpty()) return false;
                // Tipe (3) + Paraf Perawat (5) + kolom jam (6-29, ketik angka ml)
                return colIndex == 3 || colIndex == 5 || (colIndex >= 6 && colIndex <= 29);
            }
        };
        tbIntakeOutput.setModel(tabModeIntakeOutput);

        // Dropdown Tipe pada kolom 3
        tbIntakeOutput.getColumnModel().getColumn(3).setCellEditor(
            new javax.swing.DefaultCellEditor(new javax.swing.JComboBox<>(TIPE_CAIRAN)));

        // Renderer angka rata-kanan untuk kolom jam cairan
        javax.swing.table.DefaultTableCellRenderer cairanJamRenderer = new javax.swing.table.DefaultTableCellRenderer();
        cairanJamRenderer.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        for (int col = 6; col <= 29; col++) {
            tbIntakeOutput.getColumnModel().getColumn(col).setCellRenderer(cairanJamRenderer);
        }

        // Listener: simpan saat sel jam (6-29), Paraf Perawat (5), atau Tipe (3) berubah
        tabModeIntakeOutput.addTableModelListener(new javax.swing.event.TableModelListener() {
            @Override
            public void tableChanged(javax.swing.event.TableModelEvent e) {
                if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int col = e.getColumn();
                    if (row < 0 || suppressTableEvent) return;
                    Object nr = tabModeIntakeOutput.getValueAt(row, 0);
                    if (nr == null || nr.toString().isEmpty()) return; // baris ringkasan
                    if (col >= 6 && col <= 29) {
                        simpanDetailJamCairan(row, col);
                        hitungRingkasanCairan();
                    } else if (col == 5) {
                        simpanParafPerawat(row);
                    } else if (col == 3) {
                        simpanTipeCairan(row);
                        hitungRingkasanCairan();
                    }
                }
            }
        });

        // Rebuild panel input Intake-Output: pilih Tipe + ketik Nama Cairan
        // (form flat lama diganti kardex cairan).
        panelGlassIntake.removeAll();
        panelGlassIntake.setLayout(null);
        panelGlassIntake.setPreferredSize(new Dimension(900, 46));
        javax.swing.JLabel lblTipeCairan = new javax.swing.JLabel("Tipe :");
        panelGlassIntake.add(lblTipeCairan);
        lblTipeCairan.setBounds(0, 8, 35, 23);
        cmbTipeCairan = new javax.swing.JComboBox<>(TIPE_CAIRAN);
        panelGlassIntake.add(cmbTipeCairan);
        cmbTipeCairan.setBounds(40, 8, 160, 23);
        javax.swing.JLabel lblNamaCairan = new javax.swing.JLabel("Nama Cairan :");
        panelGlassIntake.add(lblNamaCairan);
        lblNamaCairan.setBounds(210, 8, 80, 23);
        TNamaCairan = new widget.TextBox();
        TNamaCairan.setName("TNamaCairan");
        panelGlassIntake.add(TNamaCairan);
        TNamaCairan.setBounds(290, 8, 280, 23);
        javax.swing.JLabel lblCairanInfo = new javax.swing.JLabel(
            "<html><font size='1'>Pilih Tipe + ketik Nama lalu Simpan. Isi jumlah (ml) per jam di sel tabel. Baris ringkasan (Total Intake/Output, Balance, Diuresis) terisi otomatis. Centang 'Paraf Perawat' saat selesai.</font></html>");
        panelGlassIntake.add(lblCairanInfo);
        lblCairanInfo.setBounds(0, 30, 880, 14);
        PanelInputIntake.setPreferredSize(new Dimension(900, 70));
        panelGlassIntake.revalidate();
        panelGlassIntake.repaint();

        tabModeMonitoringTambahan = new DefaultTableModel(null, new Object[]{
            "P", "No.Rawat", "No.R.M.", "Nama Pasien", "Tgl.Observasi", "Jam",
            "Skala Sedasi", "Nilai Sedasi", "CRT (detik)", "Lingkar Perut (cm)",
            "Sekret Warna", "Sekret Konsistensi", "NIP", "Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return colIndex == 0;
            }
        };
        tbMonitoringTambahan.setModel(tabModeMonitoringTambahan);
        tbMonitoringTambahan.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (tbMonitoringTambahan.getSelectedRow() != -1) {
                    int r = tbMonitoringTambahan.getSelectedRow();
                    TNoRw.setText(tbMonitoringTambahan.getValueAt(r, 1).toString());
                    TNoRM.setText(tbMonitoringTambahan.getValueAt(r, 2).toString());
                    TPasien.setText(tbMonitoringTambahan.getValueAt(r, 3).toString());
                    Valid.SetTgl(Tanggal, tbMonitoringTambahan.getValueAt(r, 4).toString());
                    Jam.setSelectedItem(tbMonitoringTambahan.getValueAt(r, 5).toString().substring(0, 2));
                    Menit.setSelectedItem(tbMonitoringTambahan.getValueAt(r, 5).toString().substring(3, 5));
                    Detik.setSelectedItem(tbMonitoringTambahan.getValueAt(r, 5).toString().substring(6, 8));
                    cmbSkalaSedasi.setSelectedItem(tbMonitoringTambahan.getValueAt(r, 6).toString());
                    TNilaiSedasi.setText(tbMonitoringTambahan.getValueAt(r, 7).toString());
                    TCRT.setText(tbMonitoringTambahan.getValueAt(r, 8).toString());
                    TLingkarPerut.setText(tbMonitoringTambahan.getValueAt(r, 9).toString());
                    cmbSekretWarna.setSelectedItem(tbMonitoringTambahan.getValueAt(r, 10).toString());
                    cmbSekretKonsistensi.setSelectedItem(tbMonitoringTambahan.getValueAt(r, 11).toString());
                }
            }
        });

        // Kolom: 0=NoRawat, 1=Tgl, 2=No.Urut, 3=NamaObat, 4=Kekuatan, 5=BentukSediaan,
        // 6=AturanPakai, 7=Rute, 8=ParafDokter(checkbox), 9-32=Jam(07-06), 33=Keterangan, 34=NIP, 35=Petugas
        tabModeMedikasi = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tgl.Obs", "No", "Nama Obat", "Kekuatan", "Bentuk Sediaan",
            "Aturan Pakai", "Rute", "Paraf Dokter",
            "07", "08", "09", "10", "11", "12", "13", "14",
            "15", "16", "17", "18", "19", "20", "21", "22", "23", "24",
            "01", "02", "03", "04", "05", "06",
            "Keterangan", "NIP", "Petugas"
        }) {
            @Override
            public Class<?> getColumnClass(int colIndex) {
                // Kolom Paraf Dokter (8) dirender sebagai checkbox
                return colIndex == 8 ? Boolean.class : Object.class;
            }
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                // Paraf Dokter (8) + kolom jam (9-32) bisa diedit langsung
                return colIndex == 8 || (colIndex >= 9 && colIndex <= 32);
            }
        };
        tbMedikasi.setModel(tabModeMedikasi);

        // ═══════════════════════════════════════════════════════════════════════
        //  Tab Medikasi — workflow "centang cepat":
        //    klik kiri  : cycle "" → "○" (planned) → "✓" (diberikan) → ""
        //    klik kanan : popup menu untuk simbol khusus (T/K/ESO/TAP)
        //    color-code : ✓ hijau, ○ abu, T merah, K kuning, ESO oranye, TAP abu
        //    auto-isi   : saat tambah obat baru, jam terjadwal diisi "○" otomatis
        //                 berdasarkan Aturan Pakai (1x1, 2x1, 3x1, 4x1, 6x1, q8h)
        // ═══════════════════════════════════════════════════════════════════════

        // Kolom jam 10-33: klik kiri = toggle centang "✓" (diberikan) ⇄ kosong,
        // klik kanan = popup status khusus (○ terjadwal / T / K / ESO / TAP).
        final javax.swing.JPopupMenu jamPopup = new javax.swing.JPopupMenu();
        final String[] simbolPopup = {"Diberikan (centang)", "Terjadwal (o)", "Tolak (T)", "Tunda (K)", "Efek Samping (ESO)", "Tdk Ada Persediaan (TAP)", "Kosongkan"};
        final String[] simbolVal   = {"✓",            "○",            "T",        "K",       "ESO",              "TAP",                       ""};
        for (int i = 0; i < simbolPopup.length; i++) {
            final String val = simbolVal[i];
            javax.swing.JMenuItem mi = new javax.swing.JMenuItem(simbolPopup[i]);
            mi.addActionListener(e -> {
                int r = tbMedikasi.getSelectedRow(), c = tbMedikasi.getSelectedColumn();
                if (r >= 0 && c >= 9 && c <= 32) {
                    tabModeMedikasi.setValueAt(val, r, c);
                }
            });
            jamPopup.add(mi);
        }
        tbMedikasi.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent e) {
                int row = tbMedikasi.rowAtPoint(e.getPoint());
                int col = tbMedikasi.columnAtPoint(e.getPoint());
                if (row < 0 || col < 9 || col > 32) return;
                tbMedikasi.setRowSelectionInterval(row, row);
                tbMedikasi.setColumnSelectionInterval(col, col);

                if (javax.swing.SwingUtilities.isRightMouseButton(e) || e.isPopupTrigger()) {
                    jamPopup.show(tbMedikasi, e.getX(), e.getY());
                    return;
                }
                if (e.getClickCount() == 1 && javax.swing.SwingUtilities.isLeftMouseButton(e)) {
                    // Toggle centang: jika sudah "✓" (diberikan) → kosongkan,
                    // selain itu (kosong/○ terjadwal/status khusus) → "✓".
                    Object cur = tabModeMedikasi.getValueAt(row, col);
                    String s = cur == null ? "" : cur.toString().trim();
                    String next = s.equals("✓") ? "" : "✓";
                    tabModeMedikasi.setValueAt(next, row, col);
                    // Cegah masuk edit mode
                    if (tbMedikasi.isEditing()) tbMedikasi.getCellEditor().cancelCellEditing();
                }
            }
        });

        // Renderer kolom jam: status "✓" (diberikan) ditampilkan sebagai CHECKBOX
        // tercentang (hijau) — bukan teks ✓ yang sering tidak ter-render jadi kotak.
        // Status khusus (○/T/K/ESO/TAP) tetap teks berwarna. (overrides WarnaTable)
        final javax.swing.table.DefaultTableCellRenderer jamLabelRenderer = new javax.swing.table.DefaultTableCellRenderer();
        jamLabelRenderer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        final javax.swing.JCheckBox jamCheck = new javax.swing.JCheckBox();
        jamCheck.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jamCheck.setBorderPainted(false);
        jamCheck.setOpaque(true);
        javax.swing.table.TableCellRenderer jamRenderer = new javax.swing.table.TableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String s = value == null ? "" : value.toString().trim();
                if (s.equals("✓")) {
                    // CHECKBOX tercentang = obat diberikan
                    jamCheck.setSelected(true);
                    jamCheck.setBackground(isSelected ? table.getSelectionBackground() : new java.awt.Color(187, 247, 208));
                    return jamCheck;
                }
                java.awt.Component c = jamLabelRenderer.getTableCellRendererComponent(table, s, isSelected, hasFocus, row, column);
                java.awt.Color bg = java.awt.Color.WHITE, fg = java.awt.Color.BLACK;
                switch (s) {
                    case "○":  bg = new java.awt.Color(226, 232, 240); fg = new java.awt.Color(71, 85, 105); break;  // abu
                    case "T":  bg = new java.awt.Color(254, 202, 202); fg = new java.awt.Color(127, 29, 29); break;  // merah
                    case "K":  bg = new java.awt.Color(254, 240, 138); fg = new java.awt.Color(120, 53, 15); break;  // kuning
                    case "ESO":bg = new java.awt.Color(254, 215, 170); fg = new java.awt.Color(154, 52, 18); break;  // oranye
                    case "TAP":bg = new java.awt.Color(209, 213, 219); fg = new java.awt.Color(55, 65, 81);  break;  // abu gelap
                    default: break;
                }
                if (!isSelected) { c.setBackground(bg); c.setForeground(fg); }
                return c;
            }
        };
        for (int col = 9; col <= 32; col++) {
            tbMedikasi.getColumnModel().getColumn(col).setCellRenderer(jamRenderer);
        }

        // Listener: simpan saat cell jam (9-32) atau Paraf Dokter (8) berubah
        tabModeMedikasi.addTableModelListener(new javax.swing.event.TableModelListener() {
            @Override
            public void tableChanged(javax.swing.event.TableModelEvent e) {
                if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int col = e.getColumn();
                    if (row < 0 || suppressTableEvent) return;
                    if (col >= 9 && col <= 32) {
                        simpanDetailJam(row, col);
                    } else if (col == 8) {
                        simpanParafDokter(row);
                    }
                }
            }
        });

        tabModeCatatan = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tgl.Perawatan", "Jam", "Suhu Tubuh", "Tensi", "Nadi", "Respirasi",
            "Tinggi", "Berat", "SpO2", "Kesadaran", "Keluhan", "Pemeriksaan",
            "Alergi", "Penilaian", "RTL", "Instruksi", "Evaluasi", "NIP", "Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbCatatan.setModel(tabModeCatatan);

        // Implementasi Keperawatan table model — 30 tindakan + petugas
        tabModeCatatanKeperawatan = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tgl.Catat", "Jam",
            "1.Oral Hygiene", "2.Suctioning", "3.Cek ETT", "4.Kempes Cuff ETT", "5.Rawat ETT",
            "6.Auskultasi Napas", "7.Inhalasi", "8.Batuk Efektif", "9.Minum/Makan", "10.Cek NGT",
            "11.Alat Invasif", "12.CVP", "13.Transfusi", "14.Sampel Darah", "15.Luka Dekubitus",
            "16.Luka Lain", "17.Fisio Dada", "18.Memandikan", "19.BAB/BAK", "20.Kateter Urine",
            "21.Positioning", "22.Mika/Miki", "23.ROM", "24.Tepid Sponge", "25.EKG",
            "26.Edukasi", "27.Kolaborasi", "28.Lain-lain 1", "29.Lain-lain 2", "30.Lain-lain 3",
            "NIP", "Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbCatatanKeperawatan.setModel(tabModeCatatanKeperawatan);
        // Column widths Implementasi Keperawatan (35 col: 3 PK + 30 tindakan + NIP + Petugas)
        tbCatatanKeperawatan.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbCatatanKeperawatan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < 35; i++) {
            TableColumn c = tbCatatanKeperawatan.getColumnModel().getColumn(i);
            if (i == 0)              c.setPreferredWidth(105); // No.Rawat
            else if (i == 1)         c.setPreferredWidth(80);  // Tgl.Catat
            else if (i == 2)         c.setPreferredWidth(60);  // Jam
            else if (i >= 3 && i <= 32) c.setPreferredWidth(130); // 30 tindakan
            else if (i == 33)        c.setPreferredWidth(80);  // NIP
            else                     c.setPreferredWidth(150); // Petugas
        }
        tbCatatanKeperawatan.setDefaultRenderer(Object.class, new WarnaTable());

        // Bundle VAP table model
        tabModeBundleVAP = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tgl.Bundle", "Jam", "Head Of Bed Elevation", "Oral Care",
            "Peptic Ulcer Prophylaxis", "DVT Prophylaxis", "Sedation Vacation", "Weaning Assessment",
            "NIP", "Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbBundleVAP.setModel(tabModeBundleVAP);

        // Bundle CVC table model
        tabModeBundleCVC = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tgl.Bundle", "Jam", "Kebutuhan CVC", "Mencuci Tangan", "Suhu >38",
            "Nyeri", "Kemerahan", "Bengkak", "Pus", "Penggantian Monitoring", "Balutan Insersi",
            "Infus Set", "Tree Way", "Valve Konektor", "NIP", "Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbBundleCVC.setModel(tabModeBundleCVC);

        // Bundle ISK table model (replacing Bundle General)
        tabModeBundleISK = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tgl.Bundle", "Jam", "Pemasangan Sesuai Indikasi", "Teknik Aseptik",
            "Kateter Dibutuhkan", "Posisi Urin Bag", "Hand Hygiene", "Meatal Care",
            "Fiksasi Kateter", "Kosongkan Urin Bag", "Closed System", "NIP", "Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbBundleISK.setModel(tabModeBundleISK);

        // Pemeriksaan Penunjang table model
        tabModePemeriksaanPenunjang = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tgl.Pemeriksaan", "Jam", "Nama Pemeriksaan", "Hasil", "NIP", "Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbPemeriksaanPenunjang.setModel(tabModePemeriksaanPenunjang);

        // Tab Kebutuhan Nutrisi dan Cairan table model
        tabModeGambar = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tgl.Nutrisi", "Jam", "BB", "TB", "BMI", "Kebutuhan Kkal", "Pemberian %",
            "Protein", "Balance", "IWL", "Cairan Total", "NIP", "Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbGambar.setModel(tabModeGambar);

        // Tab Cairan dan Nutrisi table model
        tabModeCairanNutrisi = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tgl.Cairan", "Jam", "Masalah", "Rate Nutrisi", "Jenis Enteral", "Cairan Enteral",
            "Kalori Enteral", "Jenis Parenteral", "Cairan Parenteral", "Kalori Parenteral", "Total Cairan",
            "Total Kalori", "Total Protein", "NIP", "Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbCairanNutrisi.setModel(tabModeCairanNutrisi);

        // Tab Tindakan table model
        tabModeTindakan = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tgl.Tindakan", "Jam", "Tindakan", "Pemasangan ke", "Hari Ke", "Keterangan", "NIP", "Petugas"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbTindakan.setModel(tabModeTindakan);

        // Setup table properties
        setupTableProperties();

        // Input restrictions for hemodynamic parameters
        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        THR.setDocument(new batasInput((byte) 3).getKata(THR));
        TTemperature.setDocument(new batasInput((byte) 5).getKata(TTemperature));
        TRespirasi.setDocument(new batasInput((byte) 3).getKata(TRespirasi));
        TNIBP.setDocument(new batasInput((byte) 7).getKata(TNIBP));
        TIBP.setDocument(new batasInput((byte) 7).getKata(TIBP));
        TCO.setDocument(new batasInput((byte) 5).getKata(TCO));
        TCVP.setDocument(new batasInput((byte) 5).getKata(TCVP));
        TPAP.setDocument(new batasInput((byte) 7).getKata(TPAP));
        TSaturasi.setDocument(new batasInput((byte) 3).getKata(TSaturasi));
        TEtCO2.setDocument(new batasInput((byte) 3).getKata(TEtCO2));
        // Pupil ComboBoxes don't need document restrictions
        // GCS Limits
        TGCS_E.setDocument(new batasInput((byte) 1).getKata(TGCS_E));
        TGCS_V.setDocument(new batasInput((byte) 1).getKata(TGCS_V));
        TGCS_M.setDocument(new batasInput((byte) 1).getKata(TGCS_M));
        TNilaiNyeri.setDocument(new batasInput((byte) 2).getKata(TNilaiNyeri));
        TBredenScore.setDocument(new batasInput((byte) 2).getKata(TBredenScore));

        // Input restrictions for pemeriksaan_ranap parameters
        TSuhuTubuh.setDocument(new batasInput((byte) 5).getKata(TSuhuTubuh));
        TTensi.setDocument(new batasInput((byte) 8).getKata(TTensi));
        TNadi.setDocument(new batasInput((byte) 3).getKata(TNadi));
        // TRespirasi already has restrictions in hemodynamic section
        TTinggi.setDocument(new batasInput((byte) 5).getKata(TTinggi));
        TBerat.setDocument(new batasInput((byte) 5).getKata(TBerat));
        TSpO2.setDocument(new batasInput((byte) 3).getKata(TSpO2));
        // TGCS already has restrictions in hemodynamic section
        TAlergi.setDocument(new batasInput((byte) 50).getKata(TAlergi));

        // Input restrictions for Tab Kebutuhan Nutrisi dan Cairan
        TBB.setDocument(new batasInput((byte) 5).getKata(TBB));
        TTB.setDocument(new batasInput((byte) 5).getKata(TTB));
        TKebutuhanKkal.setDocument(new batasInput((byte) 4).getKata(TKebutuhanKkal));
        TKebutuhanDasar.setDocument(new batasInput((byte) 4).getKata(TKebutuhanDasar));
        TPersentasePemberian.setDocument(new batasInput((byte) 3).getKata(TPersentasePemberian));
        TObatObatan.setDocument(new batasInput((byte) 6).getKata(TObatObatan));
        TProtein.setDocument(new batasInput((byte) 4).getKata(TProtein));
        TRasioKH.setDocument(new batasInput((byte) 3).getKata(TRasioKH));
        TRasioLemak.setDocument(new batasInput((byte) 3).getKata(TRasioLemak));
        TBalance.setDocument(new batasInput((byte) 6).getKata(TBalance));

        // Add key listeners for automatic calculations
        TBB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateNutritionalNeeds();
            }
        });
        TTB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateNutritionalNeeds();
            }
        });
        TKebutuhanKkal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateNutritionalNeeds();
            }
        });
        TPersentasePemberian.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateNutritionalNeeds();
            }
        });
        TProtein.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateNutritionalNeeds();
            }
        });
        TObatObatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateNutritionalNeeds();
            }
        });
        TRasioKH.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateNutritionalNeeds();
            }
        });
        TRasioLemak.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateNutritionalNeeds();
            }
        });

        // Input restrictions for Tab Cairan dan Nutrisi
        TJenisEnteral.setDocument(new batasInput((byte) 100).getKata(TJenisEnteral));
        TCairanEnteral.setDocument(new batasInput((byte) 6).getKata(TCairanEnteral));
        TKaloriEnteral.setDocument(new batasInput((byte) 6).getKata(TKaloriEnteral));
        TKHEnteral.setDocument(new batasInput((byte) 6).getKata(TKHEnteral));
        TLemakEnteral.setDocument(new batasInput((byte) 6).getKata(TLemakEnteral));
        TProteinEnteral.setDocument(new batasInput((byte) 6).getKata(TProteinEnteral));
        TJenisParenteral.setDocument(new batasInput((byte) 100).getKata(TJenisParenteral));
        TCairanParenteral.setDocument(new batasInput((byte) 6).getKata(TCairanParenteral));
        TKaloriParenteral.setDocument(new batasInput((byte) 6).getKata(TKaloriParenteral));
        TKHParenteral.setDocument(new batasInput((byte) 6).getKata(TKHParenteral));
        TLemakParenteral.setDocument(new batasInput((byte) 6).getKata(TLemakParenteral));
        TProteinParenteral.setDocument(new batasInput((byte) 6).getKata(TProteinParenteral));

        // Add key listeners for automatic total calculations
        TCairanEnteral.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTotalNutrients();
            }
        });
        TKaloriEnteral.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTotalNutrients();
            }
        });
        TKHEnteral.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTotalNutrients();
            }
        });
        TLemakEnteral.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTotalNutrients();
            }
        });
        TProteinEnteral.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTotalNutrients();
            }
        });
        TCairanParenteral.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTotalNutrients();
            }
        });
        TKaloriParenteral.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTotalNutrients();
            }
        });
        TKHParenteral.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTotalNutrients();
            }
        });
        TLemakParenteral.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTotalNutrients();
            }
        });
        TProteinParenteral.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateTotalNutrients();
            }
        });

        // Input restrictions for Tab Tindakan
        TPemasanganKe.setDocument(new batasInput((byte) 100).getKata(TPemasanganKe));
        THariKe.setDocument(new batasInput((byte) 3).getKata(THariKe));
        TKeteranganTindakan.setDocument(new batasInput((byte) 200).getKata(TKeteranganTindakan));

        // Input restrictions for ventilator parameters
        TTipe.setDocument(new batasInput((byte) 50).getKata(TTipe));
        TRRSetting.setDocument(new batasInput((byte) 3).getKata(TRRSetting));
        TRRAktual.setDocument(new batasInput((byte) 3).getKata(TRRAktual));
        TIERatio.setDocument(new batasInput((byte) 10).getKata(TIERatio));
        TMVSetting.setDocument(new batasInput((byte) 5).getKata(TMVSetting));
        TMVAktual.setDocument(new batasInput((byte) 5).getKata(TMVAktual));
        TPressureControl.setDocument(new batasInput((byte) 3).getKata(TPressureControl));
        TPressureSupport.setDocument(new batasInput((byte) 3).getKata(TPressureSupport));
        TPEEP.setDocument(new batasInput((byte) 3).getKata(TPEEP));
        TFiO2Flow.setDocument(new batasInput((byte) 5).getKata(TFiO2Flow));
        TPeak.setDocument(new batasInput((byte) 3).getKata(TPeak));
        TPlateauPressure.setDocument(new batasInput((byte) 3).getKata(TPlateauPressure));
        TDiameter.setDocument(new batasInput((byte) 5).getKata(TDiameter));
        TKedalaman.setDocument(new batasInput((byte) 3).getKata(TKedalaman));
        TTekananCuff.setDocument(new batasInput((byte) 3).getKata(TTekananCuff));

        // Input restrictions for intake and output parameters
        TTransfusiICA.setDocument(new batasInput((byte) 10).getKata(TTransfusiICA));
        TTransfusiICM.setDocument(new batasInput((byte) 10).getKata(TTransfusiICM));
        TTransfusiIICA.setDocument(new batasInput((byte) 10).getKata(TTransfusiIICA));
        TTransfusiIICM.setDocument(new batasInput((byte) 10).getKata(TTransfusiIICM));
        TTransfusiIIICACM.setDocument(new batasInput((byte) 10).getKata(TTransfusiIIICACM));
        TJumlah1JamKumulatif.setDocument(new batasInput((byte) 10).getKata(TJumlah1JamKumulatif));
        TMakanOral.setDocument(new batasInput((byte) 10).getKata(TMakanOral));
        TEnteral.setDocument(new batasInput((byte) 10).getKata(TEnteral));
        TJumlahMakanKumulatif.setDocument(new batasInput((byte) 10).getKata(TJumlahMakanKumulatif));
        TParenteral.setDocument(new batasInput((byte) 10).getKata(TParenteral));
        TParenteralKumulatif.setDocument(new batasInput((byte) 10).getKata(TParenteralKumulatif));
        TKumulatif.setDocument(new batasInput((byte) 10).getKata(TKumulatif));
        TUrine.setDocument(new batasInput((byte) 10).getKata(TUrine));
        TNGT_OGT.setDocument(new batasInput((byte) 10).getKata(TNGT_OGT));
        TBAB.setDocument(new batasInput((byte) 10).getKata(TBAB));
        TDrainStoma1.setDocument(new batasInput((byte) 10).getKata(TDrainStoma1));
        TDrainStoma2.setDocument(new batasInput((byte) 10).getKata(TDrainStoma2));
        TDrainStoma3.setDocument(new batasInput((byte) 10).getKata(TDrainStoma3));
        TMuntah.setDocument(new batasInput((byte) 10).getKata(TMuntah));
        TIWAL.setDocument(new batasInput((byte) 10).getKata(TIWAL));

        // Auto-kalkulasi Intake-Output saat ketik
        java.awt.event.KeyAdapter intakeOutputCalc = new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent evt) { calculateIntakeOutput(); }
        };
        TTransfusiICA.addKeyListener(intakeOutputCalc);
        TTransfusiICM.addKeyListener(intakeOutputCalc);
        TTransfusiIICA.addKeyListener(intakeOutputCalc);
        TTransfusiIICM.addKeyListener(intakeOutputCalc);
        TTransfusiIIICACM.addKeyListener(intakeOutputCalc);
        TMakanOral.addKeyListener(intakeOutputCalc);
        TEnteral.addKeyListener(intakeOutputCalc);
        TParenteral.addKeyListener(intakeOutputCalc);
        TParenteralKumulatif.addKeyListener(intakeOutputCalc);
        TUrine.addKeyListener(intakeOutputCalc);
        TNGT_OGT.addKeyListener(intakeOutputCalc);
        TBAB.addKeyListener(intakeOutputCalc);
        TDrainStoma1.addKeyListener(intakeOutputCalc);
        TDrainStoma2.addKeyListener(intakeOutputCalc);
        TDrainStoma3.addKeyListener(intakeOutputCalc);
        TMuntah.addKeyListener(intakeOutputCalc);
        TIWAL.addKeyListener(intakeOutputCalc);
        TOutputKumulatif.setDocument(new batasInput((byte) 10).getKata(TOutputKumulatif));
        TKeseimbanganCairan.setDocument(new batasInput((byte) 10).getKata(TKeseimbanganCairan));

        // Input restrictions for medication parameters
        TKodeDokter.setDocument(new batasInput((byte) 10).getKata(TKodeDokter));
        TNamaDokter.setDocument(new batasInput((byte) 100).getKata(TNamaDokter));
        TNamaObat.setDocument(new batasInput((byte) 100).getKata(TNamaObat));
        TKekuatan.setDocument(new batasInput((byte) 20).getKata(TKekuatan));
        TBentukSediaan.setDocument(new batasInput((byte) 50).getKata(TBentukSediaan));
        TAturanPakai.setDocument(new batasInput((byte) 100).getKata(TAturanPakai));
        TRutePemberian.setDocument(new batasInput((byte) 20).getKata(TRutePemberian));
        TVerifikasiApoteker.setDocument(new batasInput((byte) 100).getKata(TVerifikasiApoteker));
        // TJam07 dan TJam08 dihapus - menggunakan komponen jam dari form utama

        // Setup listeners
        setupWindowListeners();

        // Initialize time
        jam();

        try {
            TANGGALMUNDUR = koneksiDB.TANGGALMUNDUR();
        } catch (Exception e) {
            TANGGALMUNDUR = "yes";
        }
    }

    private void setupTableProperties() {
        // Setup tabel Observasi with hemodynamic columns
        tbObservasi.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObservasi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // tabModeObservasi sekarang 27 kolom (P + NoRawat + RM + Nama + Tgl + Jam +
        // BPSys/BPDys/MAP + HR/T/RR/CVP + EKG + SpO2 + Kesadaran + GCS E/V/M +
        // Refleks Pupil Ka/Ki + Motorik AKA/AKI/BKA/BKI + NIP + Petugas)
        for (i = 0; i < 27; i++) {
            TableColumn column = tbObservasi.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(105);
            } else if (i == 2) {
                column.setPreferredWidth(70);
            } else if (i == 3) {
                column.setPreferredWidth(150);
            } else if (i == 4) {
                column.setPreferredWidth(80);
            } else if (i == 5) {
                column.setPreferredWidth(60);
            } else if (i >= 6 && i <= 8) { // BP Sys, BP Dys, MAP
                column.setPreferredWidth(60);
            } else if (i >= 9 && i <= 12) { // HR, T, RR, CVP
                column.setPreferredWidth(70);
            } else if (i == 13) { // Gambaran EKG
                column.setPreferredWidth(140);
            } else if (i == 14) { // SpO2
                column.setPreferredWidth(60);
            } else if (i == 15) { // Kesadaran
                column.setPreferredWidth(100);
            } else if (i >= 16 && i <= 18) { // GCS E, V, M
                column.setPreferredWidth(50);
            } else if (i >= 19 && i <= 20) { // Refleks Pupil Ka/Ki
                column.setPreferredWidth(100);
            } else if (i >= 21 && i <= 24) { // Motorik AKA/AKI/BKA/BKI
                column.setPreferredWidth(80);
            } else if (i == 25) { // NIP
                column.setPreferredWidth(80);
            } else if (i == 26) { // Petugas
                column.setPreferredWidth(150);
            }
        }
        tbObservasi.setDefaultRenderer(Object.class, new WarnaTable());

        // Setup ventilator table
        tbVentilator.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbVentilator.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 24; i++) {
            TableColumn column = tbVentilator.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setPreferredWidth(105);
            } else if (i == 2) {
                column.setPreferredWidth(70);
            } else if (i == 3) {
                column.setPreferredWidth(150);
            } else if (i == 4) {
                column.setPreferredWidth(80);
            } else if (i == 5) {
                column.setPreferredWidth(60);
            } else if (i >= 6 && i <= 21) { // Ventilator parameters
                column.setPreferredWidth(90);
            } else if (i == 22) { // NIP
                column.setPreferredWidth(80);
            } else if (i == 23) { // Petugas
                column.setPreferredWidth(150);
            }
        }
        tbVentilator.setDefaultRenderer(Object.class, new WarnaTable());

        // Setup other tables
        tbIntakeOutput.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbIntakeOutput.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbIntakeOutput.setDefaultRenderer(Object.class, new WarnaTable());

        tbMedikasi.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbMedikasi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbMedikasi.setDefaultRenderer(Object.class, new WarnaTable());

        tbCatatan.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbCatatan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbCatatan.setDefaultRenderer(Object.class, new WarnaTable());
    }

    private void setupWindowListeners() {
        petugas.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (petugas.getTable().getSelectedRow() != -1) {
                    String nip = petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString();
                    String nama = petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString();
                    NIPHeader.setText(nip);
                    NamaPetugasHeader.setText(nama);
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnPrintObservasi = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        TabICU = new javax.swing.JTabbedPane();

        // Tab Hemodynamic
        internalFrame2 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObservasi = new widget.Table();
        PanelInputObservasi = new javax.swing.JPanel();
        ChkInputObservasi = new widget.CekBox();
        scrollPaneObservasi = new widget.ScrollPane();
        panelGlassObservasi = new widget.panelisi();
        jLabel1 = new widget.Label();
        THR = new widget.TextBox();
        jLabel2 = new widget.Label();
        TTemperature = new widget.TextBox();
        jLabel3 = new widget.Label();
        TRespirasi = new widget.TextBox();
        jLabel4 = new widget.Label();
        TNIBP = new widget.TextBox();
        jLabel5 = new widget.Label();
        TIBP = new widget.TextBox();
        jLabel6 = new widget.Label();
        TCO = new widget.TextBox();
        jLabel7 = new widget.Label();
        TCVP = new widget.TextBox();
        jLabel8 = new widget.Label();
        TPAP = new widget.TextBox();
        jLabel9 = new widget.Label();
        TGambaranEKG = new widget.TextBox();
        jLabel10 = new widget.Label();
        TGambaranVES = new widget.TextBox();
        jLabel12 = new widget.Label();
        TSaturasi = new widget.TextBox();
        jLabel13 = new widget.Label();
        TEtCO2 = new widget.TextBox();
        jLabel14 = new widget.Label();
        // GCS Components
        TGCS_E = new widget.TextBox();
        TGCS_V = new widget.TextBox();
        TGCS_M = new widget.TextBox();
        TGCSTotal = new widget.TextBox();
        TGCSTotal.setEditable(false); // Read-only untuk total
        jLabelGCS_E = new widget.Label();
        jLabelGCS_V = new widget.Label();
        jLabelGCS_M = new widget.Label();
        jLabelGCSTotal = new widget.Label();

        jLabel15 = new widget.Label();
        cmbPupilKanan = new widget.ComboBox();
        jLabel16 = new widget.Label();
        cmbPupilKiri = new widget.ComboBox();

        // Pain Scale Components
        jLabelSkalaNyeri = new widget.Label();
        cmbSkalaNyeri = new widget.ComboBox();
        jLabelNilaiNyeri = new widget.Label();
        TNilaiNyeri = new widget.TextBox();

        // Risk of Fall Components
        jLabel18 = new widget.Label();
        cmbResikoJatuh = new widget.ComboBox();
        jLabel18b = new widget.Label();
        cmbKategoriResikoJatuh = new widget.ComboBox();
        jLabel19 = new widget.Label();
        TBredenScore = new widget.TextBox();
        jLabel11 = new widget.Label();
        NIPObservasi = new widget.TextBox();
        NamaPetugasObservasi = new widget.TextBox();
        btnPetugasObservasi = new widget.Button();

        // Tahap 1 — Field tambahan tab Hemodinamik (BP split, Kesadaran, Refleks Pupil, Motorik AKA/AKI/BKA/BKI)
        jLabelBPSys = new widget.Label();
        TBPSys = new widget.TextBox();
        jLabelBPDys = new widget.Label();
        TBPDys = new widget.TextBox();
        jLabelMAP = new widget.Label();
        TMAP = new widget.TextBox();
        jLabelKesadaranObs = new widget.Label();
        cmbKesadaranObs = new widget.ComboBox();
        jLabelRefleksPupilKanan = new widget.Label();
        cmbRefleksPupilKanan = new widget.ComboBox();
        jLabelRefleksPupilKiri = new widget.Label();
        cmbRefleksPupilKiri = new widget.ComboBox();
        jLabelMotorik = new widget.Label();
        jLabelMotorikAKA = new widget.Label();
        cmbMotorikAKA = new widget.ComboBox();
        jLabelMotorikAKI = new widget.Label();
        cmbMotorikAKI = new widget.ComboBox();
        jLabelMotorikBKA = new widget.Label();
        cmbMotorikBKA = new widget.ComboBox();
        jLabelMotorikBKI = new widget.Label();
        cmbMotorikBKI = new widget.ComboBox();

        // Tahap 2 — Tab Pemantauan Tambahan (Skala Sedasi, CRT, Lingkar Perut, Sekret)
        internalFrameMonitoringTambahan = new widget.InternalFrame();
        ScrollMonitoringTambahan = new widget.ScrollPane();
        tbMonitoringTambahan = new widget.Table();
        PanelInputMonitoringTambahan = new javax.swing.JPanel();
        ChkInputMonitoringTambahan = new widget.CekBox();
        scrollPaneMonitoringTambahan = new widget.ScrollPane();
        panelGlassMonitoringTambahan = new widget.panelisi();
        jLabelSkalaSedasi = new widget.Label();
        cmbSkalaSedasi = new widget.ComboBox();
        jLabelNilaiSedasi = new widget.Label();
        TNilaiSedasi = new widget.TextBox();
        jLabelCRT = new widget.Label();
        TCRT = new widget.TextBox();
        jLabelLingkarPerut = new widget.Label();
        TLingkarPerut = new widget.TextBox();
        jLabelSekretWarna = new widget.Label();
        cmbSekretWarna = new widget.ComboBox();
        jLabelSekretKonsistensi = new widget.Label();
        cmbSekretKonsistensi = new widget.ComboBox();

        // Tab Ventilator Components
        internalFrameVentilator = new widget.InternalFrame();
        ScrollVentilator = new widget.ScrollPane();
        tbVentilator = new widget.Table();
        PanelInputVentilator = new javax.swing.JPanel();
        ChkInputVentilator = new widget.CekBox();
        scrollPaneVentilator = new widget.ScrollPane();
        panelGlassVentilator = new widget.panelisi();
        jLabelVent1 = new widget.Label();
        TTipe = new widget.TextBox();
        jLabelVent3 = new widget.Label();
        TRRSetting = new widget.TextBox();
        jLabelVent4 = new widget.Label();
        TRRAktual = new widget.TextBox();
        jLabelVent5 = new widget.Label();
        TIERatio = new widget.TextBox();
        jLabelVent6 = new widget.Label();
        TMVSetting = new widget.TextBox();
        jLabelVent7 = new widget.Label();
        TMVAktual = new widget.TextBox();
        jLabelVent9 = new widget.Label();
        TPressureControl = new widget.TextBox();
        jLabelVent10 = new widget.Label();
        TPressureSupport = new widget.TextBox();
        jLabelVent11 = new widget.Label();
        TPEEP = new widget.TextBox();
        jLabelVent12 = new widget.Label();
        TFiO2Flow = new widget.TextBox();
        jLabelVent14 = new widget.Label();
        TPeak = new widget.TextBox();
        jLabelVent15 = new widget.Label();
        TPlateauPressure = new widget.TextBox();
        jLabelVent16 = new widget.Label();
        cmbETTTC = new widget.ComboBox();
        jLabelVent17 = new widget.Label();
        TDiameter = new widget.TextBox();
        jLabelVent18 = new widget.Label();
        TKedalaman = new widget.TextBox();
        jLabelVent19 = new widget.Label();
        TTekananCuff = new widget.TextBox();
        jLabelVent20 = new widget.Label();
        NIPVentilator = new widget.TextBox();
        NamaPetugasVentilator = new widget.TextBox();
        btnPetugasVentilator = new widget.Button();

        // Tab Respirasi — field tambahan: Trigger, Exp Tidal Volume, Inspirasi Time, ETCO2 Resp
        jLabelRespTrigger      = new widget.Label();
        TRespTrigger           = new widget.TextBox();
        jLabelRespExpTV        = new widget.Label();
        TRespExpTV             = new widget.TextBox();
        jLabelRespInspTime     = new widget.Label();
        TRespInspTime          = new widget.TextBox();
        jLabelRespETCO2        = new widget.Label();
        TRespETCO2             = new widget.TextBox();

        // Intake Output components
        internalFrame3 = new widget.InternalFrame();
        Scroll1 = new widget.ScrollPane();
        tbIntakeOutput = new widget.Table();
        PanelInputIntake = new javax.swing.JPanel();
        ChkInputIntake = new widget.CekBox();
        scrollPaneIntake = new widget.ScrollPane();
        panelGlassIntake = new widget.panelisi();
        jLabel20 = new widget.Label();
        TTransfusiICA = new widget.TextBox();
        jLabel21 = new widget.Label();
        TTransfusiICM = new widget.TextBox();
        jLabel22 = new widget.Label();
        TTransfusiIICA = new widget.TextBox();
        jLabel23 = new widget.Label();
        TTransfusiIICM = new widget.TextBox();
        jLabel24 = new widget.Label();
        TTransfusiIIICA = new widget.TextBox();
        TTransfusiIIICM = new widget.TextBox();
        TTransfusiIIICACM = new widget.TextBox();
        jLabel25 = new widget.Label();
        TJumlah1JamKumulatif = new widget.TextBox();
        jLabelIntakeHeader = new widget.Label();
        jLabelTransfusi1CA = new widget.Label();
        jLabelTransfusi1CM = new widget.Label();
        jLabelTransfusi2CA = new widget.Label();
        jLabelTransfusi2CM = new widget.Label();
        jLabelTransfusi3CA = new widget.Label();
        jLabelTransfusi3CM = new widget.Label();
        jLabelJumlah1JamKumulatif = new widget.Label();
        jLabelMakanOralCA = new widget.Label();
        TMakanOralCA = new widget.TextBox();
        jLabelMakanOralCM = new widget.Label();
        TMakanOralCM = new widget.TextBox();
        jLabelMakanEnteralCA = new widget.Label();
        TMakanEnteralCA = new widget.TextBox();
        jLabelMakanEnteralCM = new widget.Label();
        TMakanEnteralCM = new widget.TextBox();
        jLabelJumlahMakan1JamKumulatif = new widget.Label();
        jLabelJumlah1JamKumulatifCA = new widget.Label();
        TJumlah1JamKumulatifCA = new widget.TextBox();
        jLabelJumlah1JamKumulatifCM = new widget.Label();
        TJumlah1JamKumulatifCM = new widget.TextBox();
        jLabelJumlahMakan1JamKumulatifCA = new widget.Label();
        TJumlahMakanKumulatifCA = new widget.TextBox();
        jLabelJumlahMakan1JamKumulatifCM = new widget.Label();
        TJumlahMakanKumulatifCM = new widget.TextBox();
        jLabelParenteral = new widget.Label();
        jLabelJumlahParenteral1JamKumulatif = new widget.Label();
        jLabelTotalIntakeKumulatif = new widget.Label();

        // Initialize OUTPUT section components
        jLabelOutputHeader = new widget.Label();
        jLabelUrine = new widget.Label();
        TUrine = new widget.TextBox();
        jLabelNGT_OGT = new widget.Label();
        TNGT_OGT = new widget.TextBox();
        jLabelBAB = new widget.Label();
        TBAB = new widget.TextBox();
        jLabelDrainStomaI = new widget.Label();
        TDrainStoma1 = new widget.TextBox();
        jLabelDrainStomaII = new widget.Label();
        TDrainStoma2 = new widget.TextBox();
        jLabelDrainStomaIII = new widget.Label();
        TDrainStoma3 = new widget.TextBox();
        jLabelMuntah = new widget.Label();
        TMuntah = new widget.TextBox();
        jLabelIWL = new widget.Label();
        TIWAL = new widget.TextBox();
        jLabelJumlahTotalOutputKumulatif = new widget.Label();
        TOutputKumulatif = new widget.TextBox();
        jLabelKeseimbanganCairan = new widget.Label();
        TKeseimbanganCairan = new widget.TextBox();
        jLabel26 = new widget.Label();
        TMakanOral = new widget.TextBox();
        jLabel27 = new widget.Label();
        TEnteral = new widget.TextBox();
        jLabel28 = new widget.Label();
        TJumlahMakanKumulatif = new widget.TextBox();
        jLabel29 = new widget.Label();
        TParenteral = new widget.TextBox();
        jLabel30Par = new widget.Label();
        TParenteralKumulatif = new widget.TextBox();
        jLabel31Kum = new widget.Label();
        TKumulatif = new widget.TextBox();
        jLabel32Out = new widget.Label();
        jLabel33Urine = new widget.Label();
        TUrine = new widget.TextBox();
        jLabel34NGT = new widget.Label();
        TNGT_OGT = new widget.TextBox();
        jLabel35BAB = new widget.Label();
        TBAB = new widget.TextBox();
        jLabel36Drain1 = new widget.Label();
        TDrainStoma1 = new widget.TextBox();
        jLabel37Drain2 = new widget.Label();
        TDrainStoma2 = new widget.TextBox();
        jLabel38Drain3 = new widget.Label();
        TDrainStoma3 = new widget.TextBox();
        jLabel39Muntah = new widget.Label();
        TMuntah = new widget.TextBox();
        jLabel40IWAL = new widget.Label();
        TIWAL = new widget.TextBox();
        jLabel41OutKum = new widget.Label();
        TOutputKumulatif = new widget.TextBox();
        jLabel42Balance = new widget.Label();
        TKeseimbanganCairan = new widget.TextBox();
        jLabel30Int = new widget.Label();
        NIPIntake = new widget.TextBox();
        NamaPetugasIntake = new widget.TextBox();
        btnPetugasIntake = new widget.Button();

        // Jam textboxes for Intake Output (24 hours: 07-06)
        // Inisialisasi textbox jam dihapus - menggunakan komponen jam dari form utama

        // Catatan Pengobatan components
        internalFrame4 = new widget.InternalFrame();
        Scroll2 = new widget.ScrollPane();
        tbMedikasi = new widget.Table();
        PanelInputMedikasi = new javax.swing.JPanel();
        ChkInputMedikasi = new widget.CekBox();
        scrollPaneMedikasi = new widget.ScrollPane();
        panelGlassMedikasi = new widget.panelisi();
        jLabel30Med = new widget.Label();
        TKodeDokter = new widget.TextBox();
        jLabel31Med = new widget.Label();
        TNamaDokter = new widget.TextBox();
        jLabel32Med = new widget.Label();
        TNamaObat = new widget.TextBox();
        jLabelKetMed = new widget.Label();
        TKeterangan = new widget.TextBox();
        jLabel33Med = new widget.Label();
        TKekuatan = new widget.TextBox();
        jLabel34Med = new widget.Label();
        TBentukSediaan = new widget.TextBox();
        jLabel35Med = new widget.Label();
        TAturanPakai = new widget.TextBox();
        jLabel36Med = new widget.Label();
        TRutePemberian = new widget.TextBox();
        jLabel37Med = new widget.Label();
        TanggalMulai = new widget.Tanggal();
        jLabel38Med = new widget.Label();
        TanggalStop = new widget.Tanggal();
        jLabel39Med = new widget.Label();
        TVerifikasiApoteker = new widget.TextBox();
        jLabel40Med = new widget.Label();
        NIPMedikasi = new widget.TextBox();
        NamaPetugasMedikasi = new widget.TextBox();
        btnPetugasMedikasi = new widget.Button();

        // Catatan Keperawatan components
        internalFrameKeperawatan = new widget.InternalFrame();
        ScrollKeperawatan = new widget.ScrollPane();
        tbCatatanKeperawatan = new widget.Table();
        PanelInputKeperawatan = new javax.swing.JPanel();
        ChkInputKeperawatan = new widget.CekBox();
        scrollPaneCatatanKeperawatan = new widget.ScrollPane();
        panelGlassCatatanKeperawatan = new widget.panelisi();
        jLabelKep01 = new widget.Label(); TKep01 = new widget.TextBox();
        jLabelKep02 = new widget.Label(); TKep02 = new widget.TextBox();
        jLabelKep03 = new widget.Label(); TKep03 = new widget.TextBox();
        jLabelKep04 = new widget.Label(); TKep04 = new widget.TextBox();
        jLabelKep05 = new widget.Label(); TKep05 = new widget.TextBox();
        jLabelKep06 = new widget.Label(); TKep06 = new widget.TextBox();
        jLabelKep07 = new widget.Label(); TKep07 = new widget.TextBox();
        jLabelKep08 = new widget.Label(); TKep08 = new widget.TextBox();
        jLabelKep09 = new widget.Label(); TKep09 = new widget.TextBox();
        jLabelKep10 = new widget.Label(); TKep10 = new widget.TextBox();
        jLabelKep11 = new widget.Label(); TKep11 = new widget.TextBox();
        jLabelKep12 = new widget.Label(); TKep12 = new widget.TextBox();
        jLabelKep13 = new widget.Label(); TKep13 = new widget.TextBox();
        jLabelKep14 = new widget.Label(); TKep14 = new widget.TextBox();
        jLabelKep15 = new widget.Label(); TKep15 = new widget.TextBox();
        jLabelKep16 = new widget.Label(); TKep16 = new widget.TextBox();
        jLabelKep17 = new widget.Label(); TKep17 = new widget.TextBox();
        jLabelKep18 = new widget.Label(); TKep18 = new widget.TextBox();
        jLabelKep19 = new widget.Label(); TKep19 = new widget.TextBox();
        jLabelKep20 = new widget.Label(); TKep20 = new widget.TextBox();
        jLabelKep21 = new widget.Label(); TKep21 = new widget.TextBox();
        jLabelKep22 = new widget.Label(); TKep22 = new widget.TextBox();
        jLabelKep23 = new widget.Label(); TKep23 = new widget.TextBox();
        jLabelKep24 = new widget.Label(); TKep24 = new widget.TextBox();
        jLabelKep25 = new widget.Label(); TKep25 = new widget.TextBox();
        jLabelKep26 = new widget.Label(); TKep26 = new widget.TextBox();
        jLabelKep27 = new widget.Label(); TKep27 = new widget.TextBox();
        jLabelKep28 = new widget.Label(); TKep28 = new widget.TextBox();
        jLabelKep29 = new widget.Label(); TKep29 = new widget.TextBox();
        jLabelKep30 = new widget.Label(); TKep30 = new widget.TextBox();
        jLabelPetugasKeperawatan = new widget.Label();
        NIPKeperawatan = new widget.TextBox();
        NamaPetugasKeperawatan = new widget.TextBox();
        btnPetugasKeperawatan = new widget.Button();

        // Bundle Care components with sub tabs
        internalFrameBundleCare = new widget.InternalFrame();
        TabBundleCare = new javax.swing.JTabbedPane();

        // Bundle VAP sub tab components
        internalFrameVAP = new widget.InternalFrame();
        ScrollVAP = new widget.ScrollPane();
        tbBundleVAP = new widget.Table();
        PanelInputVAP = new javax.swing.JPanel();
        ChkInputVAP = new widget.CekBox();
        scrollPaneBundleVAP = new widget.ScrollPane();
        panelGlassBundleVAP = new widget.panelisi();

        // Bundle VAP specific fields
        jLabelHeadUpVAP = new widget.Label();
        cmbHeadUpPagiVAP = new widget.ComboBox();
        cmbHeadUpSoreVAP = new widget.ComboBox();
        jLabelOralHygieneVAP = new widget.Label();
        cmbOralHygienePagiVAP = new widget.ComboBox();
        cmbOralHygieneSoreVAP = new widget.ComboBox();
        jLabelEvaluasiSedasiVAP = new widget.Label();
        cmbEvaluasiSedasiVAP = new widget.ComboBox();
        jLabelProfilaksisDVTVAP = new widget.Label();
        cmbProfilaksisDVTVAP = new widget.ComboBox();
        jLabelProfilaksisPepticVAP = new widget.Label();
        cmbProfilaksisPepticVAP = new widget.ComboBox();
        jLabelPetugasVAP = new widget.Label();
        NIPBundleVAP = new widget.TextBox();
        NamaPetugasBundleVAP = new widget.TextBox();
        btnPetugasBundleVAP = new widget.Button();

        // Bundle CVC sub tab components
        internalFrameCVC = new widget.InternalFrame();
        ScrollCVC = new widget.ScrollPane();
        tbBundleCVC = new widget.Table();
        PanelInputCVC = new javax.swing.JPanel();
        ChkInputCVC = new widget.CekBox();
        scrollPaneBundleCVC = new widget.ScrollPane();
        panelGlassBundleCVC = new widget.panelisi();

        // Bundle CVC specific fields
        jLabelKebutuhanCVC = new widget.Label();
        cmbKebutuhanCVC = new widget.ComboBox();
        jLabelMencuciTanganCVC = new widget.Label();
        cmbMencuciTanganCVC = new widget.ComboBox();
        jLabelTempatInserseCVC = new widget.Label();
        jLabelSuhuCVC = new widget.Label();
        cmbSuhuCVC = new widget.ComboBox();
        jLabelNyeriCVC = new widget.Label();
        cmbNyeriCVC = new widget.ComboBox();
        jLabelKemerahanCVC = new widget.Label();
        cmbKemerahanCVC = new widget.ComboBox();
        jLabelBengkakCVC = new widget.Label();
        cmbBengkakCVC = new widget.ComboBox();
        jLabelPusCVC = new widget.Label();
        cmbPusCVC = new widget.ComboBox();
        jLabelPenggantianMonitoringCVC = new widget.Label();
        cmbPenggantianMonitoringCVC = new widget.ComboBox();
        jLabelBalutanInsersiCVC = new widget.Label();
        cmbBalutanInsersiCVC = new widget.ComboBox();
        jLabelPenggantianBerkala = new widget.Label();
        jLabelInfusSetCVC = new widget.Label();
        cmbInfusSetCVC = new widget.ComboBox();
        jLabelTreeWayCVC = new widget.Label();
        cmbTreeWayCVC = new widget.ComboBox();
        jLabelValveKonektorCVC = new widget.Label();
        cmbValveKonektorCVC = new widget.ComboBox();
        jLabelPetugasCVC = new widget.Label();
        NIPBundleCVC = new widget.TextBox();
        NamaPetugasBundleCVC = new widget.TextBox();
        btnPetugasBundleCVC = new widget.Button();

        // Bundle ISK sub tab (replacing Bundle General)
        internalFrameISK = new widget.InternalFrame();
        ScrollISK = new widget.ScrollPane();
        tbBundleISK = new widget.Table();
        PanelInputISK = new javax.swing.JPanel();
        ChkInputISK = new widget.CekBox();
        scrollPaneBundleISK = new widget.ScrollPane();
        panelGlassBundleISK = new widget.panelisi();

        // Bundle ISK specific fields
        jLabelPemasanganSesuaiISK = new widget.Label();
        cmbPemasanganSesuaiISK = new widget.ComboBox();
        jLabelTeknikAseptikISK = new widget.Label();
        cmbTeknikAseptikISK = new widget.ComboBox();
        jLabelKateterDibutuhkanISK = new widget.Label();
        cmbKateterDibutuhkanISK = new widget.ComboBox();
        jLabelPosisiUrinBagISK = new widget.Label();
        cmbPosisiUrinBagISK = new widget.ComboBox();
        jLabelHandHygieneISK = new widget.Label();
        cmbHandHygieneISK = new widget.ComboBox();
        jLabelMeatalCareISK = new widget.Label();
        cmbMeatalCareISK = new widget.ComboBox();
        jLabelFiksasiKateterISK = new widget.Label();
        cmbFiksasiKateterISK = new widget.ComboBox();
        jLabelKosongkanUrinBagISK = new widget.Label();
        cmbKosongkanUrinBagISK = new widget.ComboBox();
        jLabelClosedSystemISK = new widget.Label();
        cmbClosedSystemISK = new widget.ComboBox();
        jLabelPetugasISK = new widget.Label();
        NIPBundleISK = new widget.TextBox();
        NamaPetugasBundleISK = new widget.TextBox();
        btnPetugasBundleISK = new widget.Button();

        // Pemeriksaan Penunjang components
        internalFramePemeriksaanPenunjang = new widget.InternalFrame();
        ScrollPemeriksaanPenunjang = new widget.ScrollPane();
        tbPemeriksaanPenunjang = new widget.Table();
        PanelInputPemeriksaanPenunjang = new javax.swing.JPanel();
        ChkInputPemeriksaanPenunjang = new widget.CekBox();
        scrollPanePemeriksaanPenunjang = new widget.ScrollPane();
        panelGlassPemeriksaanPenunjang = new widget.panelisi();
        jLabelNamaPemeriksaan = new widget.Label();
        TNamaPemeriksaan = new widget.TextBox();
        jLabelHasilPemeriksaan = new widget.Label();
        scrollPaneHasilPemeriksaan = new javax.swing.JScrollPane();
        THasilPemeriksaan = new javax.swing.JTextArea();
        jLabelPetugasPemeriksaan = new widget.Label();
        NIPPemeriksaanPenunjang = new widget.TextBox();
        NamaPetugasPemeriksaanPenunjang = new widget.TextBox();
        btnPetugasPemeriksaanPenunjang = new widget.Button();

        // Tab Kebutuhan Nutrisi dan Cairan components
        internalFrameGambar = new widget.InternalFrame();
        ScrollGambar = new widget.ScrollPane();
        tbGambar = new widget.Table();
        PanelInputGambar = new javax.swing.JPanel();
        ChkInputGambar = new widget.CekBox();
        scrollPaneGambar = new widget.ScrollPane();
        panelGlassGambar = new widget.panelisi();

        // Nutrition and fluid calculation fields
        TBB = new widget.TextBox();
        TTB = new widget.TextBox();
        TBMI = new widget.TextBox();
        TKebutuhanKkal = new widget.TextBox();
        TKebutuhanKkal24 = new widget.TextBox();
        TKebutuhanDasar = new widget.TextBox();
        TPersentasePemberian = new widget.TextBox();
        TPemberianKkal = new widget.TextBox();
        TIWL = new widget.TextBox();
        TObatObatan = new widget.TextBox();
        TCairanTotal = new widget.TextBox();
        TProtein = new widget.TextBox();
        TProteinTotal = new widget.TextBox();
        TKaloriNonProtein = new widget.TextBox();
        TRasioKH = new widget.TextBox();
        TRasioLemak = new widget.TextBox();
        TRasioTotal = new widget.TextBox();
        TRasioKkal = new widget.TextBox();
        TBalance = new widget.TextBox();

        // Staff components
        jLabelPetugasGambar = new widget.Label();
        NIPGambar = new widget.TextBox();
        NamaPetugasGambar = new widget.TextBox();
        btnPetugasGambar = new widget.Button();

        // Tab Cairan dan Nutrisi components
        internalFrameCairanNutrisi = new widget.InternalFrame();
        ScrollCairanNutrisi = new widget.ScrollPane();
        tbCairanNutrisi = new widget.Table();
        PanelInputCairanNutrisi = new javax.swing.JPanel();
        ChkInputCairanNutrisi = new widget.CekBox();
        scrollPaneCairanNutrisi = new widget.ScrollPane();
        panelGlassCairanNutrisi = new widget.panelisi();

        // Masalah checkboxes
        ChkSSP = new widget.CekBox();
        ChkJantung = new widget.CekBox();
        ChkRespirasi = new widget.CekBox();
        ChkRenal = new widget.CekBox();
        ChkSaluranPencernaan = new widget.CekBox();
        ChkHepar = new widget.CekBox();
        ChkMetabolisme = new widget.CekBox();

        // Rate Nutrisi checkboxes
        ChkOral = new widget.CekBox();
        ChkEnteral = new widget.CekBox();
        ChkParenteral = new widget.CekBox();
        ChkKombinasi = new widget.CekBox();

        // Enteral fields
        TJenisEnteral = new widget.TextBox();
        TCairanEnteral = new widget.TextBox();
        TKaloriEnteral = new widget.TextBox();
        TKHEnteral = new widget.TextBox();
        TLemakEnteral = new widget.TextBox();
        TProteinEnteral = new widget.TextBox();

        // Parenteral fields
        TJenisParenteral = new widget.TextBox();
        TCairanParenteral = new widget.TextBox();
        TKaloriParenteral = new widget.TextBox();
        TKHParenteral = new widget.TextBox();
        TLemakParenteral = new widget.TextBox();
        TProteinParenteral = new widget.TextBox();

        // Total fields
        TTotalCairan = new widget.TextBox();
        TTotalKalori = new widget.TextBox();
        TTotalKH = new widget.TextBox();
        TTotalLemak = new widget.TextBox();
        TTotalProtein = new widget.TextBox();

        // Staff components
        jLabelPetugasCairanNutrisi = new widget.Label();
        NIPCairanNutrisi = new widget.TextBox();
        NamaPetugasCairanNutrisi = new widget.TextBox();
        btnPetugasCairanNutrisi = new widget.Button();

        // Tab Tindakan components
        internalFrameTindakan = new widget.InternalFrame();
        ScrollTindakan = new widget.ScrollPane();
        tbTindakan = new widget.Table();
        PanelInputTindakan = new javax.swing.JPanel();
        ChkInputTindakan = new widget.CekBox();
        scrollPaneTindakan = new widget.ScrollPane();
        panelGlassTindakan = new widget.panelisi();

        // Tindakan checkboxes
        ChkEndotrachealTube = new widget.CekBox();
        ChkTracheostomyTube = new widget.CekBox();
        ChkTubingVentilator = new widget.CekBox();
        ChkNasogastricTube = new widget.CekBox();
        ChkCentralVenousCatheter = new widget.CekBox();
        ChkPICC = new widget.CekBox();
        ChkCloseSuction = new widget.CekBox();
        ChkArteriLineCatheter = new widget.CekBox();
        ChkDowerCatheter = new widget.CekBox();
        ChkInfusPerifer = new widget.CekBox();

        // Tindakan fields
        TPemasanganKe = new widget.TextBox();
        THariKe = new widget.TextBox();
        TKeteranganTindakan = new widget.TextBox();

        // Staff components
        jLabelPetugasTindakan = new widget.Label();
        NIPTindakan = new widget.TextBox();
        NamaPetugasTindakan = new widget.TextBox();
        btnPetugasTindakan = new widget.Button();

        // Catatan Terintegrasi (pemeriksaan_ranap) components
        TSuhuTubuh = new widget.TextBox();
        TTensi = new widget.TextBox();
        TNadi = new widget.TextBox();
        // TRespirasi already initialized in hemodynamic section
        TTinggi = new widget.TextBox();
        TBerat = new widget.TextBox();
        TSpO2 = new widget.TextBox();
        // TGCS already initialized in hemodynamic section
        CmbKesadaran = new widget.ComboBox();
        TAlergi = new widget.TextBox();
        scrollPaneKeluhan = new javax.swing.JScrollPane();
        TKeluhan = new javax.swing.JTextArea();
        scrollPanePemeriksaanRanap = new javax.swing.JScrollPane();
        TPemeriksaanRanap = new javax.swing.JTextArea();
        scrollPanePenilaian = new javax.swing.JScrollPane();
        TPenilaian = new javax.swing.JTextArea();
        scrollPaneRTL = new javax.swing.JScrollPane();
        TRTL = new javax.swing.JTextArea();
        scrollPaneInstruksi = new javax.swing.JScrollPane();
        TInstruksi = new javax.swing.JTextArea();
        scrollPaneEvaluasi = new javax.swing.JScrollPane();
        TEvaluasi = new javax.swing.JTextArea();

        internalFrame5 = new widget.InternalFrame();
        Scroll3 = new widget.ScrollPane();
        tbCatatan = new widget.Table();
        PanelInputCatatan = new javax.swing.JPanel();
        ChkInputCatatan = new widget.CekBox();
        scrollPaneCatatan = new widget.ScrollPane();
        panelGlassCatatan = new widget.panelisi();
        jLabel40 = new widget.Label();
        scrollPane2 = new widget.ScrollPane();
        TCatatanISI = new widget.TextArea();
        jLabel41 = new widget.Label();
        NIPCatatan = new widget.TextBox();
        NamaPetugasCatatan = new widget.TextBox();
        btnPetugasCatatan = new widget.Button();
        jLabel42 = new widget.Label();
        cmbProfesi = new widget.ComboBox();

        // Form Input
        FormInput = new widget.PanelBiasa();
        jLabel50 = new widget.Label();
        TNoRw = new widget.TextBox();
        TNoRM = new widget.TextBox();
        TPasien = new widget.TextBox();
        jLabel51 = new widget.Label();
        Tanggal = new widget.Tanggal();
        Jam = new widget.ComboBox();
        Menit = new widget.ComboBox();
        Detik = new widget.ComboBox();
        ChkJln = new widget.CekBox();
        jLabel52 = new widget.Label();
        TglLahir = new widget.TextBox();
        jLabel53 = new widget.Label();
        Umur = new widget.TextBox();

        // Control Panel
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        BtnAll = new widget.Button();
        jLabel60 = new widget.Label();
        LCount = new widget.Label();
        BtnKeluar = new widget.Button();
        panelGlass10 = new widget.panelisi();
        jLabel61 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel62 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel63 = new widget.Label();
        TCariPasien = new widget.TextBox();
        btnPasien = new widget.Button();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel64 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();

        // Popup Menu
        jPopupMenu1.setName("jPopupMenu1");

        MnPrintObservasi.setBackground(new java.awt.Color(255, 255, 254));
        MnPrintObservasi.setFont(new java.awt.Font("Tahoma", 0, 11));
        MnPrintObservasi.setForeground(new java.awt.Color(50, 50, 50));
        MnPrintObservasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        MnPrintObservasi.setText("Cetak Lembar Observasi ICU");
        MnPrintObservasi.setName("MnPrintObservasi");
        MnPrintObservasi.setPreferredSize(new java.awt.Dimension(200, 26));
        MnPrintObservasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnPrintObservasiActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnPrintObservasi);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
                "::[ Lembar Observasi Rawat Intensif / ICU - Complete Monitoring ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 11),
                new java.awt.Color(50, 50, 50)));
        internalFrame1.setName("internalFrame1");
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        TabICU.setBackground(new java.awt.Color(254, 255, 254));
        TabICU.setForeground(new java.awt.Color(50, 50, 50));
        TabICU.setFont(new java.awt.Font("Tahoma", 0, 11));
        TabICU.setName("TabICU");

        // Setup Tab Hemodynamic
        internalFrame2.setBorder(null);
        internalFrame2.setName("internalFrame2");
        internalFrame2.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll");
        Scroll.setOpaque(true);

        tbObservasi.setToolTipText("Silahkan klik untuk memilih data");
        tbObservasi.setComponentPopupMenu(jPopupMenu1);
        tbObservasi.setName("tbObservasi");
        tbObservasi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObservasiMouseClicked(evt);
            }
        });
        Scroll.setViewportView(tbObservasi);
        internalFrame2.add(Scroll, java.awt.BorderLayout.CENTER);

        PanelInputObservasi.setName("PanelInputObservasi");
        PanelInputObservasi.setOpaque(false);
        PanelInputObservasi.setPreferredSize(new java.awt.Dimension(192, 320));
        PanelInputObservasi.setLayout(new java.awt.BorderLayout(1, 1));

        ChkInputObservasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInputObservasi.setText(".: Input Data Hemodinamik");
        ChkInputObservasi.setBorderPainted(true);
        ChkInputObservasi.setBorderPaintedFlat(true);
        ChkInputObservasi.setFocusable(false);
        ChkInputObservasi.setName("ChkInputObservasi");
        ChkInputObservasi.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInputObservasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputObservasiActionPerformed(evt);
            }
        });
        PanelInputObservasi.add(ChkInputObservasi, java.awt.BorderLayout.PAGE_END);

        scrollPaneObservasi.setName("scrollPaneObservasi");
        panelGlassObservasi.setName("panelGlassObservasi");
        panelGlassObservasi.setPreferredSize(new java.awt.Dimension(44, 175));
        panelGlassObservasi.setLayout(null);

        // ═══════════════════════════════════════════════════════════════════════
        //  Layout tab HEMODINAMIK — disusun 5 baris rapih tanpa gap kosong.
        //  Field hidden (NIBP, IBP, CO, PAP, VES, EtCO2, Pupil mm, Skala Nyeri,
        //  Resiko Jatuh, Breden) tetap di-add ke panel untuk back-compat, tapi
        //  setVisible(false) sudah di constructor. setBounds-nya tidak lagi
        //  mempengaruhi tampilan.
        //
        //  Row 1 (y=10):  HR  | T   | RR  | SpO2 | CVP
        //  Row 2 (y=40):  BP Sys | BP Dys | MAP | Kesadaran
        //  Row 3 (y=70):  GCS E | V | M | Total | Gambaran EKG
        //  Row 4 (y=100): Refleks Pupil Kanan | Refleks Pupil Kiri
        //  Row 5 (y=130): Motorik Ekstremitas: AKA | AKI | BKA | BKI
        // ═══════════════════════════════════════════════════════════════════════

        // ── Row 1 (y=10): HR | T | RR | SpO2 | CVP ─────────────────────────────
        jLabel1.setText("HR (/mnt) :");
        jLabel1.setName("jLabel1");
        panelGlassObservasi.add(jLabel1);
        jLabel1.setBounds(0, 10, 75, 23);
        THR.setName("THR");
        panelGlassObservasi.add(THR);
        THR.setBounds(80, 10, 55, 23);

        jLabel2.setText("T (°C) :");
        jLabel2.setName("jLabel2");
        panelGlassObservasi.add(jLabel2);
        jLabel2.setBounds(150, 10, 55, 23);
        TTemperature.setName("TTemperature");
        panelGlassObservasi.add(TTemperature);
        TTemperature.setBounds(210, 10, 55, 23);

        jLabel3.setText("RR (/mnt) :");
        jLabel3.setName("jLabel3");
        panelGlassObservasi.add(jLabel3);
        jLabel3.setBounds(280, 10, 65, 23);
        TRespirasi.setName("TRespirasi");
        panelGlassObservasi.add(TRespirasi);
        TRespirasi.setBounds(350, 10, 55, 23);

        jLabel4.setText("NIBP (mmHg) :");
        jLabel4.setName("jLabel4");
        panelGlassObservasi.add(jLabel4);
        jLabel4.setBounds(420, 10, 90, 23);
        TNIBP.setName("TNIBP");
        panelGlassObservasi.add(TNIBP);
        TNIBP.setBounds(515, 10, 80, 23);

        jLabel5.setText("IBP (mmHg) :");
        jLabel5.setName("jLabel5");
        panelGlassObservasi.add(jLabel5);
        jLabel5.setBounds(0, 40, 80, 23);
        TIBP.setName("TIBP");
        panelGlassObservasi.add(TIBP);
        TIBP.setBounds(85, 40, 80, 23);

        jLabel6.setText("CO (L/min) :");
        jLabel6.setName("jLabel6");
        panelGlassObservasi.add(jLabel6);
        jLabel6.setBounds(170, 40, 80, 23);
        TCO.setName("TCO");
        panelGlassObservasi.add(TCO);
        TCO.setBounds(255, 40, 60, 23);

        // CVP — pindah ke Row 1 (y=10) di kolom kanan, setelah SpO2
        jLabel7.setText("CVP (cmH2O) :");
        jLabel7.setName("jLabel7");
        panelGlassObservasi.add(jLabel7);
        jLabel7.setBounds(550, 10, 85, 23);
        TCVP.setName("TCVP");
        panelGlassObservasi.add(TCVP);
        TCVP.setBounds(640, 10, 60, 23);

        jLabel8.setText("PAP (mmHg) :");
        jLabel8.setName("jLabel8");
        panelGlassObservasi.add(jLabel8);
        jLabel8.setBounds(480, 40, 90, 23);
        TPAP.setName("TPAP");
        panelGlassObservasi.add(TPAP);
        TPAP.setBounds(575, 40, 80, 23);

        // Gambaran EKG — Row 3 (y=70), kanan setelah GCS Total
        jLabel9.setText("Gambaran EKG :");
        jLabel9.setName("jLabel9");
        panelGlassObservasi.add(jLabel9);
        jLabel9.setBounds(310, 70, 95, 23);
        TGambaranEKG.setName("TGambaranEKG");
        panelGlassObservasi.add(TGambaranEKG);
        TGambaranEKG.setBounds(410, 70, 180, 23);

        jLabel10.setText("Gambaran VES :");
        jLabel10.setName("jLabel10");
        panelGlassObservasi.add(jLabel10);
        jLabel10.setBounds(260, 70, 100, 23);
        TGambaranVES.setName("TGambaranVES");
        panelGlassObservasi.add(TGambaranVES);
        TGambaranVES.setBounds(365, 70, 150, 23);

        // SpO2 — pindah ke Row 1 (y=10), kolom 4 setelah RR
        jLabel12.setText("SpO2 (%) :");
        jLabel12.setName("jLabel12");
        panelGlassObservasi.add(jLabel12);
        jLabel12.setBounds(420, 10, 65, 23);
        TSaturasi.setName("TSaturasi");
        panelGlassObservasi.add(TSaturasi);
        TSaturasi.setBounds(490, 10, 55, 23);

        jLabel13.setText("EtCO2 (mmHg) :");
        jLabel13.setName("jLabel13");
        panelGlassObservasi.add(jLabel13);
        jLabel13.setBounds(0, 100, 100, 23);
        TEtCO2.setName("TEtCO2");
        panelGlassObservasi.add(TEtCO2);
        TEtCO2.setBounds(105, 100, 60, 23);

        // GCS — Row 3 (y=70), kiri-rapat: E | V | M | Total
        jLabelGCS_E.setText("GCS  E :");
        jLabelGCS_E.setName("jLabelGCS_E");
        panelGlassObservasi.add(jLabelGCS_E);
        jLabelGCS_E.setBounds(0, 70, 55, 23);
        TGCS_E.setName("TGCS_E");
        panelGlassObservasi.add(TGCS_E);
        TGCS_E.setBounds(60, 70, 30, 23);
        TGCS_E.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateGCSTotal();
            }
        });

        jLabelGCS_V.setText("V :");
        jLabelGCS_V.setName("jLabelGCS_V");
        panelGlassObservasi.add(jLabelGCS_V);
        jLabelGCS_V.setBounds(95, 70, 20, 23);
        TGCS_V.setName("TGCS_V");
        panelGlassObservasi.add(TGCS_V);
        TGCS_V.setBounds(120, 70, 30, 23);
        TGCS_V.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateGCSTotal();
            }
        });

        jLabelGCS_M.setText("M :");
        jLabelGCS_M.setName("jLabelGCS_M");
        panelGlassObservasi.add(jLabelGCS_M);
        jLabelGCS_M.setBounds(155, 70, 20, 23);
        TGCS_M.setName("TGCS_M");
        panelGlassObservasi.add(TGCS_M);
        TGCS_M.setBounds(180, 70, 30, 23);
        TGCS_M.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateGCSTotal();
            }
        });

        // GCS Total (hidden tapi tetap di-add) — Row 3 y=70
        jLabelGCSTotal.setText("Total :");
        jLabelGCSTotal.setName("jLabelGCSTotal");
        panelGlassObservasi.add(jLabelGCSTotal);
        jLabelGCSTotal.setBounds(220, 70, 45, 23);
        TGCSTotal.setName("TGCSTotal");
        panelGlassObservasi.add(TGCSTotal);
        TGCSTotal.setBounds(270, 70, 40, 23);

        jLabel15.setText("Pupil Kanan (mm) :");
        jLabel15.setName("jLabel15");
        panelGlassObservasi.add(jLabel15);
        jLabel15.setBounds(475, 100, 115, 23);
        cmbPupilKanan.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
        }));
        cmbPupilKanan.setName("cmbPupilKanan");
        panelGlassObservasi.add(cmbPupilKanan);
        cmbPupilKanan.setBounds(595, 100, 60, 23);

        jLabel16.setText("Pupil Kiri (mm) :");
        jLabel16.setName("jLabel16");
        panelGlassObservasi.add(jLabel16);
        jLabel16.setBounds(660, 100, 105, 23);
        cmbPupilKiri.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"
        }));
        cmbPupilKiri.setName("cmbPupilKiri");
        panelGlassObservasi.add(cmbPupilKiri);
        cmbPupilKiri.setBounds(770, 100, 60, 23);

        jLabelSkalaNyeri.setText("Skala Nyeri :");
        jLabelSkalaNyeri.setName("jLabelSkalaNyeri");
        panelGlassObservasi.add(jLabelSkalaNyeri);
        jLabelSkalaNyeri.setBounds(0, 130, 80, 23);
        cmbSkalaNyeri.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "CCPOT", "NRS", "NIPS", "FLACC"
        }));
        cmbSkalaNyeri.setName("cmbSkalaNyeri");
        panelGlassObservasi.add(cmbSkalaNyeri);
        cmbSkalaNyeri.setBounds(85, 130, 100, 23);

        jLabelNilaiNyeri.setText("Nilai :");
        jLabelNilaiNyeri.setName("jLabelNilaiNyeri");
        panelGlassObservasi.add(jLabelNilaiNyeri);
        jLabelNilaiNyeri.setBounds(190, 130, 40, 23);
        TNilaiNyeri.setName("TNilaiNyeri");
        panelGlassObservasi.add(TNilaiNyeri);
        TNilaiNyeri.setBounds(235, 130, 40, 23);

        jLabel18.setText("Resiko Jatuh :");
        jLabel18.setName("jLabel18");
        panelGlassObservasi.add(jLabel18);
        jLabel18.setBounds(280, 130, 85, 23);
        cmbResikoJatuh.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "Morse Fall Scale", "Humpty Dumpty Fall Scale", "Get Up and Go Test",
            "Johns Hopkins Fall Risk", "Ontario Modified Stratify", "Skala Jatuh Geriatri"
        }));
        cmbResikoJatuh.setName("cmbResikoJatuh");
        panelGlassObservasi.add(cmbResikoJatuh);
        cmbResikoJatuh.setBounds(370, 130, 180, 23);

        jLabel18b.setText("Kategori :");
        jLabel18b.setName("jLabel18b");
        panelGlassObservasi.add(jLabel18b);
        jLabel18b.setBounds(555, 130, 65, 23);
        cmbKategoriResikoJatuh.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "Rendah", "Sedang", "Tinggi"
        }));
        cmbKategoriResikoJatuh.setName("cmbKategoriResikoJatuh");
        panelGlassObservasi.add(cmbKategoriResikoJatuh);
        cmbKategoriResikoJatuh.setBounds(625, 130, 85, 23);

        jLabel19.setText("Breden Score :");
        jLabel19.setName("jLabel19");
        panelGlassObservasi.add(jLabel19);
        jLabel19.setBounds(0, 160, 90, 23);
        TBredenScore.setName("TBredenScore");
        panelGlassObservasi.add(TBredenScore);
        TBredenScore.setBounds(95, 160, 60, 23);

        jLabel11.setText("Petugas :");
        jLabel11.setName("jLabel11");
        panelGlassObservasi.add(jLabel11);
        jLabel11.setBounds(160, 160, 80, 23);
        // Petugas dipindah ke panel atas (NIPHeader)
        NIPObservasi.setVisible(false);
        NamaPetugasObservasi.setVisible(false);
        btnPetugasObservasi.setVisible(false);

        // ── Row 2 (y=40): BP Sys | BP Dys | MAP | Kesadaran ────────────────────
        jLabelBPSys.setText("BP Sys :");
        jLabelBPSys.setName("jLabelBPSys");
        panelGlassObservasi.add(jLabelBPSys);
        jLabelBPSys.setBounds(0, 40, 55, 23);
        TBPSys.setName("TBPSys");
        panelGlassObservasi.add(TBPSys);
        TBPSys.setBounds(60, 40, 55, 23);

        jLabelBPDys.setText("BP Dys :");
        jLabelBPDys.setName("jLabelBPDys");
        panelGlassObservasi.add(jLabelBPDys);
        jLabelBPDys.setBounds(125, 40, 55, 23);
        TBPDys.setName("TBPDys");
        panelGlassObservasi.add(TBPDys);
        TBPDys.setBounds(185, 40, 55, 23);

        jLabelMAP.setText("MAP :");
        jLabelMAP.setName("jLabelMAP");
        panelGlassObservasi.add(jLabelMAP);
        jLabelMAP.setBounds(250, 40, 40, 23);
        TMAP.setName("TMAP");
        panelGlassObservasi.add(TMAP);
        TMAP.setBounds(295, 40, 55, 23);

        jLabelKesadaranObs.setText("Kesadaran :");
        jLabelKesadaranObs.setName("jLabelKesadaranObs");
        panelGlassObservasi.add(jLabelKesadaranObs);
        jLabelKesadaranObs.setBounds(360, 40, 75, 23);
        cmbKesadaranObs.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "Composmentis", "Apatis", "Somnolen", "Sopor", "Coma"
        }));
        cmbKesadaranObs.setName("cmbKesadaranObs");
        panelGlassObservasi.add(cmbKesadaranObs);
        cmbKesadaranObs.setBounds(440, 40, 130, 23);

        // ── Row 4 (y=100): Refleks Pupil Kanan | Refleks Pupil Kiri ────────────
        jLabelRefleksPupilKanan.setText("Refleks Pupil Kanan :");
        jLabelRefleksPupilKanan.setName("jLabelRefleksPupilKanan");
        panelGlassObservasi.add(jLabelRefleksPupilKanan);
        jLabelRefleksPupilKanan.setBounds(0, 100, 130, 23);
        cmbRefleksPupilKanan.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "+", "-"
        }));
        cmbRefleksPupilKanan.setName("cmbRefleksPupilKanan");
        panelGlassObservasi.add(cmbRefleksPupilKanan);
        cmbRefleksPupilKanan.setBounds(135, 100, 50, 23);

        jLabelRefleksPupilKiri.setText("Refleks Pupil Kiri :");
        jLabelRefleksPupilKiri.setName("jLabelRefleksPupilKiri");
        panelGlassObservasi.add(jLabelRefleksPupilKiri);
        jLabelRefleksPupilKiri.setBounds(210, 100, 110, 23);
        cmbRefleksPupilKiri.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "+", "-"
        }));
        cmbRefleksPupilKiri.setName("cmbRefleksPupilKiri");
        panelGlassObservasi.add(cmbRefleksPupilKiri);
        cmbRefleksPupilKiri.setBounds(325, 100, 50, 23);

        // ── Row 5 (y=130): Motorik Ekstremitas: AKA | AKI | BKA | BKI ──────────
        jLabelMotorik.setText("Motorik Ekstremitas :");
        jLabelMotorik.setName("jLabelMotorik");
        panelGlassObservasi.add(jLabelMotorik);
        jLabelMotorik.setBounds(0, 130, 130, 23);

        jLabelMotorikAKA.setText("AKA :");
        jLabelMotorikAKA.setName("jLabelMotorikAKA");
        panelGlassObservasi.add(jLabelMotorikAKA);
        jLabelMotorikAKA.setBounds(135, 130, 35, 23);
        cmbMotorikAKA.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "0", "1", "2", "3", "4", "5"
        }));
        cmbMotorikAKA.setName("cmbMotorikAKA");
        panelGlassObservasi.add(cmbMotorikAKA);
        cmbMotorikAKA.setBounds(175, 130, 45, 23);

        jLabelMotorikAKI.setText("AKI :");
        jLabelMotorikAKI.setName("jLabelMotorikAKI");
        panelGlassObservasi.add(jLabelMotorikAKI);
        jLabelMotorikAKI.setBounds(235, 130, 30, 23);
        cmbMotorikAKI.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "0", "1", "2", "3", "4", "5"
        }));
        cmbMotorikAKI.setName("cmbMotorikAKI");
        panelGlassObservasi.add(cmbMotorikAKI);
        cmbMotorikAKI.setBounds(270, 130, 45, 23);

        jLabelMotorikBKA.setText("BKA :");
        jLabelMotorikBKA.setName("jLabelMotorikBKA");
        panelGlassObservasi.add(jLabelMotorikBKA);
        jLabelMotorikBKA.setBounds(330, 130, 35, 23);
        cmbMotorikBKA.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "0", "1", "2", "3", "4", "5"
        }));
        cmbMotorikBKA.setName("cmbMotorikBKA");
        panelGlassObservasi.add(cmbMotorikBKA);
        cmbMotorikBKA.setBounds(370, 130, 45, 23);

        jLabelMotorikBKI.setText("BKI :");
        jLabelMotorikBKI.setName("jLabelMotorikBKI");
        panelGlassObservasi.add(jLabelMotorikBKI);
        jLabelMotorikBKI.setBounds(430, 130, 30, 23);
        cmbMotorikBKI.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "0", "1", "2", "3", "4", "5"
        }));
        cmbMotorikBKI.setName("cmbMotorikBKI");
        panelGlassObservasi.add(cmbMotorikBKI);
        cmbMotorikBKI.setBounds(465, 130, 45, 23);
        // ===== Akhir Tahap 1 =====

        // Tombol Grafik Hemodinamik
        BtnGrafikHemo = new widget.Button();
        BtnGrafikHemo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png")));
        BtnGrafikHemo.setText("Grafik");
        BtnGrafikHemo.setToolTipText("Tampilkan Grafik Hemodinamik");
        BtnGrafikHemo.setFocusPainted(false);
        BtnGrafikHemo.setFont(new java.awt.Font("Tahoma", 0, 11));
        BtnGrafikHemo.setName("BtnGrafikHemo");
        BtnGrafikHemo.addActionListener(e -> tampilGrafikHemodinamik());
        panelGlassObservasi.add(BtnGrafikHemo);
        BtnGrafikHemo.setBounds(400, 160, 100, 23);

        scrollPaneObservasi.setViewportView(panelGlassObservasi);
        PanelInputObservasi.add(scrollPaneObservasi, java.awt.BorderLayout.PAGE_START);
        internalFrame2.add(PanelInputObservasi, java.awt.BorderLayout.PAGE_START);

        TabICU.addTab("Hemodinamik", internalFrame2);

        // ===== Tab Pemantauan Tambahan (Skala Sedasi, CRT, Lingkar Perut, Sekret) =====
        internalFrameMonitoringTambahan.setBorder(null);
        internalFrameMonitoringTambahan.setName("internalFrameMonitoringTambahan");
        internalFrameMonitoringTambahan.setLayout(new java.awt.BorderLayout(1, 1));

        ScrollMonitoringTambahan.setName("ScrollMonitoringTambahan");
        ScrollMonitoringTambahan.setOpaque(true);

        tbMonitoringTambahan.setToolTipText("Silahkan klik untuk memilih data");
        tbMonitoringTambahan.setName("tbMonitoringTambahan");
        ScrollMonitoringTambahan.setViewportView(tbMonitoringTambahan);
        internalFrameMonitoringTambahan.add(ScrollMonitoringTambahan, java.awt.BorderLayout.CENTER);

        PanelInputMonitoringTambahan.setName("PanelInputMonitoringTambahan");
        PanelInputMonitoringTambahan.setOpaque(false);
        PanelInputMonitoringTambahan.setPreferredSize(new java.awt.Dimension(192, 130));
        PanelInputMonitoringTambahan.setLayout(new java.awt.BorderLayout(1, 1));

        ChkInputMonitoringTambahan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInputMonitoringTambahan.setText(".: Input Pemantauan Tambahan");
        ChkInputMonitoringTambahan.setBorderPainted(true);
        ChkInputMonitoringTambahan.setBorderPaintedFlat(true);
        ChkInputMonitoringTambahan.setFocusable(false);
        ChkInputMonitoringTambahan.setName("ChkInputMonitoringTambahan");
        ChkInputMonitoringTambahan.setPreferredSize(new java.awt.Dimension(192, 20));
        PanelInputMonitoringTambahan.add(ChkInputMonitoringTambahan, java.awt.BorderLayout.PAGE_END);

        scrollPaneMonitoringTambahan.setName("scrollPaneMonitoringTambahan");
        panelGlassMonitoringTambahan.setName("panelGlassMonitoringTambahan");
        panelGlassMonitoringTambahan.setPreferredSize(new java.awt.Dimension(44, 110));
        panelGlassMonitoringTambahan.setLayout(null);

        // Row 1 (y=10): Skala Sedasi (jenis + nilai), CRT, Lingkar Perut
        jLabelSkalaSedasi.setText("Skala Sedasi :");
        jLabelSkalaSedasi.setName("jLabelSkalaSedasi");
        panelGlassMonitoringTambahan.add(jLabelSkalaSedasi);
        jLabelSkalaSedasi.setBounds(0, 10, 90, 23);
        cmbSkalaSedasi.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "RASS", "Ramsay", "SAS"
        }));
        cmbSkalaSedasi.setName("cmbSkalaSedasi");
        panelGlassMonitoringTambahan.add(cmbSkalaSedasi);
        cmbSkalaSedasi.setBounds(95, 10, 90, 23);

        jLabelNilaiSedasi.setText("Nilai :");
        jLabelNilaiSedasi.setName("jLabelNilaiSedasi");
        panelGlassMonitoringTambahan.add(jLabelNilaiSedasi);
        jLabelNilaiSedasi.setBounds(190, 10, 40, 23);
        TNilaiSedasi.setName("TNilaiSedasi");
        panelGlassMonitoringTambahan.add(TNilaiSedasi);
        TNilaiSedasi.setBounds(235, 10, 50, 23);

        jLabelCRT.setText("CRT (detik) :");
        jLabelCRT.setName("jLabelCRT");
        panelGlassMonitoringTambahan.add(jLabelCRT);
        jLabelCRT.setBounds(295, 10, 90, 23);
        TCRT.setName("TCRT");
        panelGlassMonitoringTambahan.add(TCRT);
        TCRT.setBounds(390, 10, 50, 23);

        jLabelLingkarPerut.setText("Lingkar Perut (cm) :");
        jLabelLingkarPerut.setName("jLabelLingkarPerut");
        panelGlassMonitoringTambahan.add(jLabelLingkarPerut);
        jLabelLingkarPerut.setBounds(450, 10, 130, 23);
        TLingkarPerut.setName("TLingkarPerut");
        panelGlassMonitoringTambahan.add(TLingkarPerut);
        TLingkarPerut.setBounds(585, 10, 60, 23);

        // Row 2 (y=40): Sekret Warna, Sekret Konsistensi
        jLabelSekretWarna.setText("Sekret Warna :");
        jLabelSekretWarna.setName("jLabelSekretWarna");
        panelGlassMonitoringTambahan.add(jLabelSekretWarna);
        jLabelSekretWarna.setBounds(0, 40, 100, 23);
        cmbSekretWarna.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "-", "Bening", "Putih", "Kuning", "Hijau", "Kemerahan", "Kecoklatan"
        }));
        cmbSekretWarna.setName("cmbSekretWarna");
        panelGlassMonitoringTambahan.add(cmbSekretWarna);
        cmbSekretWarna.setBounds(105, 40, 130, 23);

        jLabelSekretKonsistensi.setText("Sekret Konsistensi :");
        jLabelSekretKonsistensi.setName("jLabelSekretKonsistensi");
        panelGlassMonitoringTambahan.add(jLabelSekretKonsistensi);
        jLabelSekretKonsistensi.setBounds(245, 40, 130, 23);
        cmbSekretKonsistensi.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "-", "Encer", "Kental", "Mukoid", "Purulent", "Bercampur Darah"
        }));
        cmbSekretKonsistensi.setName("cmbSekretKonsistensi");
        panelGlassMonitoringTambahan.add(cmbSekretKonsistensi);
        cmbSekretKonsistensi.setBounds(380, 40, 130, 23);

        scrollPaneMonitoringTambahan.setViewportView(panelGlassMonitoringTambahan);
        PanelInputMonitoringTambahan.add(scrollPaneMonitoringTambahan, java.awt.BorderLayout.PAGE_START);
        internalFrameMonitoringTambahan.add(PanelInputMonitoringTambahan, java.awt.BorderLayout.PAGE_START);

        // Tab "Pemantauan Tambahan" addTab dipindah ke akhir (setelah Handover SBAR)
        // supaya tidak mengubah index tab existing (switch BtnSimpan/BtnHapus pakai
        // urutan lama). Setup panel-nya tetap di sini.

        // Setup Tab Ventilator
        internalFrameVentilator.setBorder(null);
        internalFrameVentilator.setName("internalFrameVentilator");
        internalFrameVentilator.setLayout(new java.awt.BorderLayout(1, 1));

        ScrollVentilator.setName("ScrollVentilator");
        tbVentilator.setName("tbVentilator");
        tbVentilator.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbVentilatorMouseClicked(evt);
            }
        });
        ScrollVentilator.setViewportView(tbVentilator);
        internalFrameVentilator.add(ScrollVentilator, java.awt.BorderLayout.CENTER);

        PanelInputVentilator.setName("PanelInputVentilator");
        PanelInputVentilator.setOpaque(false);
        PanelInputVentilator.setPreferredSize(new java.awt.Dimension(192, 260));
        PanelInputVentilator.setLayout(new java.awt.BorderLayout(1, 1));

        ChkInputVentilator.setText(".: Input Data Ventilator");
        ChkInputVentilator.setBorderPainted(true);
        ChkInputVentilator.setBorderPaintedFlat(true);
        ChkInputVentilator.setFocusable(false);
        ChkInputVentilator.setName("ChkInputVentilator");
        ChkInputVentilator.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInputVentilator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputVentilatorActionPerformed(evt);
            }
        });
        PanelInputVentilator.add(ChkInputVentilator, java.awt.BorderLayout.PAGE_END);

        scrollPaneVentilator.setName("scrollPaneVentilator");
        panelGlassVentilator.setName("panelGlassVentilator");
        panelGlassVentilator.setPreferredSize(new java.awt.Dimension(44, 200));
        panelGlassVentilator.setLayout(null);

        // ═══════════════════════════════════════════════════════════════════════
        //  Layout tab RESPIRASI — 20 field (gabung Ventilator + Pemantauan Tambahan)
        //  Row 1 (y=10):  Tipe Ventilasi | Respirasi Rate | Tidal Vol/PC | Minute Vol
        //  Row 2 (y=40):  PEEP | PS | Trigger | Exp Tidal Vol | Inspirasi Time
        //  Row 3 (y=70):  I:E | FiO2 LPM | ETCO2 | P Peak | Pressure Cuff
        //  Row 4 (y=100): Skala Nyeri (jenis+nilai) | Skala Sedasi (jenis+nilai)
        //  Row 5 (y=130): Capillary Refill Time | Lingkar Perut
        //  Row 6 (y=160): Warna Sekret | Konsistensi Sekret
        //
        //  Field yang DIBUANG dari UI (tetap di-instantiate, hidden):
        //  - RR Aktual, MV Aktual, Plateau Pressure, ETT/TC, Diameter, Kedalaman
        // ═══════════════════════════════════════════════════════════════════════

        // ── Row 1 (y=10) ────────────────────────────────────────────────────────
        jLabelVent1.setText("Tipe Ventilasi :");
        jLabelVent1.setName("jLabelVent1");
        panelGlassVentilator.add(jLabelVent1);
        jLabelVent1.setBounds(0, 10, 95, 23);
        TTipe.setName("TTipe");
        panelGlassVentilator.add(TTipe);
        TTipe.setBounds(100, 10, 130, 23);

        jLabelVent3.setText("Respirasi Rate :");
        jLabelVent3.setName("jLabelVent3");
        panelGlassVentilator.add(jLabelVent3);
        jLabelVent3.setBounds(240, 10, 95, 23);
        TRRSetting.setName("TRRSetting");
        panelGlassVentilator.add(TRRSetting);
        TRRSetting.setBounds(340, 10, 55, 23);

        jLabelVent9.setText("Tidal Volume / PC :");
        jLabelVent9.setName("jLabelVent9");
        panelGlassVentilator.add(jLabelVent9);
        jLabelVent9.setBounds(405, 10, 115, 23);
        TPressureControl.setName("TPressureControl");
        panelGlassVentilator.add(TPressureControl);
        TPressureControl.setBounds(525, 10, 70, 23);

        jLabelVent6.setText("Minute Volume :");
        jLabelVent6.setName("jLabelVent6");
        panelGlassVentilator.add(jLabelVent6);
        jLabelVent6.setBounds(605, 10, 95, 23);
        TMVSetting.setName("TMVSetting");
        panelGlassVentilator.add(TMVSetting);
        TMVSetting.setBounds(705, 10, 70, 23);

        // ── Row 2 (y=40): PEEP | PS | Trigger | Exp Tidal Vol | Inspirasi Time ─
        jLabelVent11.setText("PEEP :");
        jLabelVent11.setName("jLabelVent11");
        panelGlassVentilator.add(jLabelVent11);
        jLabelVent11.setBounds(0, 40, 50, 23);
        TPEEP.setName("TPEEP");
        panelGlassVentilator.add(TPEEP);
        TPEEP.setBounds(55, 40, 60, 23);

        jLabelVent10.setText("PS :");
        jLabelVent10.setName("jLabelVent10");
        panelGlassVentilator.add(jLabelVent10);
        jLabelVent10.setBounds(125, 40, 35, 23);
        TPressureSupport.setName("TPressureSupport");
        panelGlassVentilator.add(TPressureSupport);
        TPressureSupport.setBounds(165, 40, 60, 23);

        jLabelRespTrigger.setText("Trigger :");
        jLabelRespTrigger.setName("jLabelRespTrigger");
        panelGlassVentilator.add(jLabelRespTrigger);
        jLabelRespTrigger.setBounds(240, 40, 55, 23);
        TRespTrigger.setName("TRespTrigger");
        panelGlassVentilator.add(TRespTrigger);
        TRespTrigger.setBounds(300, 40, 60, 23);

        jLabelRespExpTV.setText("Exp Tidal Volume :");
        jLabelRespExpTV.setName("jLabelRespExpTV");
        panelGlassVentilator.add(jLabelRespExpTV);
        jLabelRespExpTV.setBounds(380, 40, 115, 23);
        TRespExpTV.setName("TRespExpTV");
        panelGlassVentilator.add(TRespExpTV);
        TRespExpTV.setBounds(500, 40, 60, 23);

        jLabelRespInspTime.setText("Inspirasi Time :");
        jLabelRespInspTime.setName("jLabelRespInspTime");
        panelGlassVentilator.add(jLabelRespInspTime);
        jLabelRespInspTime.setBounds(580, 40, 95, 23);
        TRespInspTime.setName("TRespInspTime");
        panelGlassVentilator.add(TRespInspTime);
        TRespInspTime.setBounds(680, 40, 60, 23);

        // ── Row 3 (y=70): I:E | FiO2 LPM | ETCO2 | P Peak | Pressure Cuff ──────
        jLabelVent5.setText("I:E :");
        jLabelVent5.setName("jLabelVent5");
        panelGlassVentilator.add(jLabelVent5);
        jLabelVent5.setBounds(0, 70, 30, 23);
        TIERatio.setName("TIERatio");
        panelGlassVentilator.add(TIERatio);
        TIERatio.setBounds(35, 70, 70, 23);

        jLabelVent12.setText("FiO2 LPM :");
        jLabelVent12.setName("jLabelVent12");
        panelGlassVentilator.add(jLabelVent12);
        jLabelVent12.setBounds(125, 70, 75, 23);
        TFiO2Flow.setName("TFiO2Flow");
        panelGlassVentilator.add(TFiO2Flow);
        TFiO2Flow.setBounds(205, 70, 60, 23);

        jLabelRespETCO2.setText("ETCO2 :");
        jLabelRespETCO2.setName("jLabelRespETCO2");
        panelGlassVentilator.add(jLabelRespETCO2);
        jLabelRespETCO2.setBounds(285, 70, 55, 23);
        TRespETCO2.setName("TRespETCO2");
        panelGlassVentilator.add(TRespETCO2);
        TRespETCO2.setBounds(345, 70, 60, 23);

        jLabelVent14.setText("P Peak :");
        jLabelVent14.setName("jLabelVent14");
        panelGlassVentilator.add(jLabelVent14);
        jLabelVent14.setBounds(425, 70, 55, 23);
        TPeak.setName("TPeak");
        panelGlassVentilator.add(TPeak);
        TPeak.setBounds(485, 70, 60, 23);

        jLabelVent19.setText("Pressure Cuff :");
        jLabelVent19.setName("jLabelVent19");
        panelGlassVentilator.add(jLabelVent19);
        jLabelVent19.setBounds(565, 70, 95, 23);
        TTekananCuff.setName("TTekananCuff");
        panelGlassVentilator.add(TTekananCuff);
        TTekananCuff.setBounds(665, 70, 60, 23);

        // ── Row 4 (y=100): Skala Nyeri | Skala Sedasi (pindah dari panel lain) ──
        jLabelSkalaNyeri.setText("Skala Nyeri :");
        jLabelSkalaNyeri.setVisible(true);
        panelGlassVentilator.add(jLabelSkalaNyeri);
        jLabelSkalaNyeri.setBounds(0, 100, 80, 23);
        cmbSkalaNyeri.setVisible(true);
        panelGlassVentilator.add(cmbSkalaNyeri);
        cmbSkalaNyeri.setBounds(85, 100, 100, 23);

        jLabelNilaiNyeri.setText("Nilai :");
        jLabelNilaiNyeri.setVisible(true);
        panelGlassVentilator.add(jLabelNilaiNyeri);
        jLabelNilaiNyeri.setBounds(195, 100, 40, 23);
        TNilaiNyeri.setVisible(true);
        panelGlassVentilator.add(TNilaiNyeri);
        TNilaiNyeri.setBounds(240, 100, 50, 23);

        jLabelSkalaSedasi.setText("Skala Sedasi :");
        panelGlassVentilator.add(jLabelSkalaSedasi);
        jLabelSkalaSedasi.setBounds(330, 100, 90, 23);
        panelGlassVentilator.add(cmbSkalaSedasi);
        cmbSkalaSedasi.setBounds(425, 100, 90, 23);

        jLabelNilaiSedasi.setText("Nilai :");
        panelGlassVentilator.add(jLabelNilaiSedasi);
        jLabelNilaiSedasi.setBounds(525, 100, 40, 23);
        panelGlassVentilator.add(TNilaiSedasi);
        TNilaiSedasi.setBounds(570, 100, 50, 23);

        // ── Row 5 (y=130): Capillary Refill Time | Lingkar Perut ────────────────
        jLabelCRT.setText("Capillary Refill Time (dtk) :");
        panelGlassVentilator.add(jLabelCRT);
        jLabelCRT.setBounds(0, 130, 170, 23);
        panelGlassVentilator.add(TCRT);
        TCRT.setBounds(175, 130, 60, 23);

        jLabelLingkarPerut.setText("Lingkar Perut (cm) :");
        panelGlassVentilator.add(jLabelLingkarPerut);
        jLabelLingkarPerut.setBounds(260, 130, 130, 23);
        panelGlassVentilator.add(TLingkarPerut);
        TLingkarPerut.setBounds(395, 130, 60, 23);

        // ── Row 6 (y=160): Warna Sekret | Konsistensi Sekret ────────────────────
        jLabelSekretWarna.setText("Warna Sekret :");
        panelGlassVentilator.add(jLabelSekretWarna);
        jLabelSekretWarna.setBounds(0, 160, 100, 23);
        panelGlassVentilator.add(cmbSekretWarna);
        cmbSekretWarna.setBounds(105, 160, 130, 23);

        jLabelSekretKonsistensi.setText("Konsistensi Sekret :");
        panelGlassVentilator.add(jLabelSekretKonsistensi);
        jLabelSekretKonsistensi.setBounds(260, 160, 130, 23);
        panelGlassVentilator.add(cmbSekretKonsistensi);
        cmbSekretKonsistensi.setBounds(395, 160, 130, 23);

        // ── Field yang TIDAK ditampilkan di tab Respirasi (back-compat) ────────
        jLabelVent4.setVisible(false);  TRRAktual.setVisible(false);
        jLabelVent7.setVisible(false);  TMVAktual.setVisible(false);
        jLabelVent15.setVisible(false); TPlateauPressure.setVisible(false);
        jLabelVent16.setVisible(false); cmbETTTC.setVisible(false);
        jLabelVent17.setVisible(false); TDiameter.setVisible(false);
        jLabelVent18.setVisible(false); TKedalaman.setVisible(false);
        cmbETTTC.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"ETT","Tracheostomy"}));

        jLabelVent20.setVisible(false);
        NIPVentilator.setVisible(false);
        NamaPetugasVentilator.setVisible(false);
        btnPetugasVentilator.setVisible(false);

        // Combo models untuk field yang dipindah (jaga-jaga kalau awalnya kosong)
        if (cmbSkalaSedasi.getModel().getSize() == 0) {
            cmbSkalaSedasi.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"RASS","Ramsay","SAS"}));
        }
        if (cmbSekretWarna.getModel().getSize() == 0) {
            cmbSekretWarna.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"-","Bening","Putih","Kuning","Hijau","Kemerahan","Kecoklatan"}));
        }
        if (cmbSekretKonsistensi.getModel().getSize() == 0) {
            cmbSekretKonsistensi.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"-","Encer","Kental","Mukoid","Purulent","Bercampur Darah"}));
        }
        if (cmbSkalaNyeri.getModel().getSize() == 0) {
            cmbSkalaNyeri.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"CCPOT","NRS","NIPS","FLACC"}));
        }

        scrollPaneVentilator.setViewportView(panelGlassVentilator);
        PanelInputVentilator.add(scrollPaneVentilator, java.awt.BorderLayout.PAGE_START);
        internalFrameVentilator.add(PanelInputVentilator, java.awt.BorderLayout.PAGE_START);

        TabICU.addTab("Respirasi", internalFrameVentilator);

        // Setup Tab Intake Output
        internalFrame3.setBorder(null);
        internalFrame3.setName("internalFrame3");
        internalFrame3.setLayout(new java.awt.BorderLayout(1, 1));
        Scroll1.setName("Scroll1");
        tbIntakeOutput.setName("tbIntakeOutput");
        tbIntakeOutput.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbIntakeOutputMouseClicked(evt);
            }
        });
        Scroll1.setViewportView(tbIntakeOutput);
        internalFrame3.add(Scroll1, java.awt.BorderLayout.CENTER);

        PanelInputIntake.setName("PanelInputIntake");
        PanelInputIntake.setOpaque(false);
        PanelInputIntake.setPreferredSize(new java.awt.Dimension(192, 350));
        PanelInputIntake.setLayout(new java.awt.BorderLayout(1, 1));
        ChkInputIntake.setText(".: Input Data Intake Output");
        ChkInputIntake.setBorderPainted(true);
        ChkInputIntake.setBorderPaintedFlat(true);
        ChkInputIntake.setFocusable(false);
        ChkInputIntake.setName("ChkInputIntake");
        ChkInputIntake.setSelected(false);
        ChkInputIntake.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInputIntake.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputIntakeActionPerformed(evt);
            }
        });
        PanelInputIntake.add(ChkInputIntake, java.awt.BorderLayout.PAGE_END);

        scrollPaneIntake.setName("scrollPaneIntake");
        panelGlassIntake.setName("panelGlassIntake");
        panelGlassIntake.setPreferredSize(new java.awt.Dimension(1200, 600));
        panelGlassIntake.setLayout(null);

        // Setup Tab Intake Output configuration
        internalFrame3.setBorder(null);
        internalFrame3.setName("internalFrame3");
        internalFrame3.setLayout(new java.awt.BorderLayout(1, 1));
        Scroll1.setName("Scroll1");
        tbIntakeOutput.setName("tbIntakeOutput");
        Scroll1.setViewportView(tbIntakeOutput);
        internalFrame3.add(Scroll1, java.awt.BorderLayout.CENTER);
        PanelInputIntake.setName("PanelInputIntake");
        PanelInputIntake.setOpaque(false);
        PanelInputIntake.setPreferredSize(new java.awt.Dimension(192, 350));
        PanelInputIntake.setLayout(new java.awt.BorderLayout(1, 1));
        ChkInputIntake.setText(".: Input Data Intake Output");
        ChkInputIntake.setBorderPainted(true);
        ChkInputIntake.setBorderPaintedFlat(true);
        ChkInputIntake.setFocusable(false);
        ChkInputIntake.setName("ChkInputIntake");
        ChkInputIntake.setPreferredSize(new java.awt.Dimension(192, 20));
        PanelInputIntake.add(ChkInputIntake, java.awt.BorderLayout.PAGE_END);
        scrollPaneIntake.setName("scrollPaneIntake");
        panelGlassIntake.setName("panelGlassIntake");
        panelGlassIntake.setPreferredSize(new java.awt.Dimension(44, 800));
        panelGlassIntake.setLayout(null);

        // INTAKE Section Header (kiri)
        jLabelIntakeHeader.setText("INTAKE");
        jLabelIntakeHeader.setName("jLabelIntakeHeader");
        jLabelIntakeHeader.setFont(new java.awt.Font("Tahoma", 1, 12));
        panelGlassIntake.add(jLabelIntakeHeader);
        jLabelIntakeHeader.setBounds(10, 10, 80, 23);

        // Transfusi 1 CA
        jLabelTransfusi1CA.setText("Transfusi 1 CA :");
        jLabelTransfusi1CA.setName("jLabelTransfusi1CA");
        panelGlassIntake.add(jLabelTransfusi1CA);
        jLabelTransfusi1CA.setBounds(10, 40, 100, 23);
        TTransfusiICA.setName("TTransfusiICA");
        panelGlassIntake.add(TTransfusiICA);
        TTransfusiICA.setBounds(115, 40, 90, 23);

        // Transfusi 1 CM
        jLabelTransfusi1CM.setText("CM :");
        jLabelTransfusi1CM.setName("jLabelTransfusi1CM");
        panelGlassIntake.add(jLabelTransfusi1CM);
        jLabelTransfusi1CM.setBounds(215, 40, 35, 23);
        TTransfusiICM.setName("TTransfusiICM");
        panelGlassIntake.add(TTransfusiICM);
        TTransfusiICM.setBounds(255, 40, 90, 23);

        // Transfusi 2 CA
        jLabelTransfusi2CA.setText("Transfusi 2 CA :");
        jLabelTransfusi2CA.setName("jLabelTransfusi2CA");
        panelGlassIntake.add(jLabelTransfusi2CA);
        jLabelTransfusi2CA.setBounds(10, 70, 100, 23);
        TTransfusiIICA.setName("TTransfusiIICA");
        panelGlassIntake.add(TTransfusiIICA);
        TTransfusiIICA.setBounds(115, 70, 90, 23);

        // Transfusi 2 CM
        jLabelTransfusi2CM.setText("CM :");
        jLabelTransfusi2CM.setName("jLabelTransfusi2CM");
        panelGlassIntake.add(jLabelTransfusi2CM);
        jLabelTransfusi2CM.setBounds(215, 70, 35, 23);
        TTransfusiIICM.setName("TTransfusiIICM");
        panelGlassIntake.add(TTransfusiIICM);
        TTransfusiIICM.setBounds(255, 70, 90, 23);

        // Transfusi 3 CA
        jLabelTransfusi3CA.setText("Transfusi 3 CA :");
        jLabelTransfusi3CA.setName("jLabelTransfusi3CA");
        panelGlassIntake.add(jLabelTransfusi3CA);
        jLabelTransfusi3CA.setBounds(10, 100, 100, 23);
        TTransfusiIIICA.setName("TTransfusiIIICA");
        panelGlassIntake.add(TTransfusiIIICA);
        TTransfusiIIICA.setBounds(115, 100, 90, 23);

        // Transfusi 3 CM
        jLabelTransfusi3CM.setText("CM :");
        jLabelTransfusi3CM.setName("jLabelTransfusi3CM");
        panelGlassIntake.add(jLabelTransfusi3CM);
        jLabelTransfusi3CM.setBounds(215, 100, 35, 23);
        TTransfusiIIICM.setName("TTransfusiIIICM");
        panelGlassIntake.add(TTransfusiIIICM);
        TTransfusiIIICM.setBounds(255, 100, 90, 23);

        // Jumlah Transfusi 1 jam kumulatif CA
        jLabelJumlah1JamKumulatifCA.setText("Jml Transfusi 1 jam kum. CA :");
        jLabelJumlah1JamKumulatifCA.setName("jLabelJumlah1JamKumulatifCA");
        panelGlassIntake.add(jLabelJumlah1JamKumulatifCA);
        jLabelJumlah1JamKumulatifCA.setBounds(10, 130, 175, 23);
        TJumlah1JamKumulatifCA.setName("TJumlah1JamKumulatifCA");
        panelGlassIntake.add(TJumlah1JamKumulatifCA);
        TJumlah1JamKumulatifCA.setBounds(190, 130, 75, 23);

        // Jumlah Transfusi 1 jam kumulatif CM
        jLabelJumlah1JamKumulatifCM.setText("CM :");
        jLabelJumlah1JamKumulatifCM.setName("jLabelJumlah1JamKumulatifCM");
        panelGlassIntake.add(jLabelJumlah1JamKumulatifCM);
        jLabelJumlah1JamKumulatifCM.setBounds(275, 130, 35, 23);
        TJumlah1JamKumulatifCM.setName("TJumlah1JamKumulatifCM");
        panelGlassIntake.add(TJumlah1JamKumulatifCM);
        TJumlah1JamKumulatifCM.setBounds(315, 130, 75, 23);

        // Makan Oral CA
        jLabelMakanOralCA.setText("Makan Oral CA :");
        jLabelMakanOralCA.setName("jLabelMakanOralCA");
        panelGlassIntake.add(jLabelMakanOralCA);
        jLabelMakanOralCA.setBounds(10, 160, 100, 23);
        TMakanOralCA.setName("TMakanOralCA");
        panelGlassIntake.add(TMakanOralCA);
        TMakanOralCA.setBounds(115, 160, 90, 23);

        // Makan Oral CM
        jLabelMakanOralCM.setText("CM :");
        jLabelMakanOralCM.setName("jLabelMakanOralCM");
        panelGlassIntake.add(jLabelMakanOralCM);
        jLabelMakanOralCM.setBounds(215, 160, 35, 23);
        TMakanOralCM.setName("TMakanOralCM");
        panelGlassIntake.add(TMakanOralCM);
        TMakanOralCM.setBounds(255, 160, 90, 23);

        // Makan Enteral CA
        jLabelMakanEnteralCA.setText("Makan Enteral CA :");
        jLabelMakanEnteralCA.setName("jLabelMakanEnteralCA");
        panelGlassIntake.add(jLabelMakanEnteralCA);
        jLabelMakanEnteralCA.setBounds(10, 190, 115, 23);
        TMakanEnteralCA.setName("TMakanEnteralCA");
        panelGlassIntake.add(TMakanEnteralCA);
        TMakanEnteralCA.setBounds(130, 190, 75, 23);

        // Makan Enteral CM
        jLabelMakanEnteralCM.setText("CM :");
        jLabelMakanEnteralCM.setName("jLabelMakanEnteralCM");
        panelGlassIntake.add(jLabelMakanEnteralCM);
        jLabelMakanEnteralCM.setBounds(215, 190, 35, 23);
        TMakanEnteralCM.setName("TMakanEnteralCM");
        panelGlassIntake.add(TMakanEnteralCM);
        TMakanEnteralCM.setBounds(255, 190, 90, 23);

        // Jumlah Makan 1 jam kumulatif CA
        jLabelJumlahMakan1JamKumulatifCA.setText("Jml Makan 1 jam kum. CA :");
        jLabelJumlahMakan1JamKumulatifCA.setName("jLabelJumlahMakan1JamKumulatifCA");
        panelGlassIntake.add(jLabelJumlahMakan1JamKumulatifCA);
        jLabelJumlahMakan1JamKumulatifCA.setBounds(10, 220, 165, 23);
        TJumlahMakanKumulatifCA.setName("TJumlahMakanKumulatifCA");
        panelGlassIntake.add(TJumlahMakanKumulatifCA);
        TJumlahMakanKumulatifCA.setBounds(180, 220, 75, 23);

        // Jumlah Makan 1 jam kumulatif CM
        jLabelJumlahMakan1JamKumulatifCM.setText("CM :");
        jLabelJumlahMakan1JamKumulatifCM.setName("jLabelJumlahMakan1JamKumulatifCM");
        panelGlassIntake.add(jLabelJumlahMakan1JamKumulatifCM);
        jLabelJumlahMakan1JamKumulatifCM.setBounds(265, 220, 35, 23);
        TJumlahMakanKumulatifCM.setName("TJumlahMakanKumulatifCM");
        panelGlassIntake.add(TJumlahMakanKumulatifCM);
        TJumlahMakanKumulatifCM.setBounds(305, 220, 75, 23);

        // Parenteral
        jLabelParenteral.setText("Parenteral :");
        jLabelParenteral.setName("jLabelParenteral");
        panelGlassIntake.add(jLabelParenteral);
        jLabelParenteral.setBounds(10, 250, 100, 23);
        TParenteral.setName("TParenteral");
        panelGlassIntake.add(TParenteral);
        TParenteral.setBounds(115, 250, 90, 23);

        // Jumlah Parenteral 1 jam kumulatif
        jLabelJumlahParenteral1JamKumulatif.setText("Jml Parenteral 1 jam kum. :");
        jLabelJumlahParenteral1JamKumulatif.setName("jLabelJumlahParenteral1JamKumulatif");
        panelGlassIntake.add(jLabelJumlahParenteral1JamKumulatif);
        jLabelJumlahParenteral1JamKumulatif.setBounds(10, 280, 175, 23);
        TParenteralKumulatif.setName("TParenteralKumulatif");
        panelGlassIntake.add(TParenteralKumulatif);
        TParenteralKumulatif.setBounds(190, 280, 90, 23);

        // Total Intake Kumulatif
        jLabelTotalIntakeKumulatif.setText("Total Intake Kumulatif :");
        jLabelTotalIntakeKumulatif.setName("jLabelTotalIntakeKumulatif");
        jLabelTotalIntakeKumulatif.setFont(new java.awt.Font("Tahoma", 1, 11));
        panelGlassIntake.add(jLabelTotalIntakeKumulatif);
        jLabelTotalIntakeKumulatif.setBounds(10, 310, 145, 23);
        TKumulatif.setName("TKumulatif");
        panelGlassIntake.add(TKumulatif);
        TKumulatif.setBounds(160, 310, 90, 23);

        // OUTPUT Section Header (kanan)
        jLabelOutputHeader.setText("OUTPUT");
        jLabelOutputHeader.setName("jLabelOutputHeader");
        jLabelOutputHeader.setFont(new java.awt.Font("Tahoma", 1, 12));
        panelGlassIntake.add(jLabelOutputHeader);
        jLabelOutputHeader.setBounds(410, 10, 80, 23);

        // Urine
        jLabelUrine.setText("Urine :");
        jLabelUrine.setName("jLabelUrine");
        panelGlassIntake.add(jLabelUrine);
        jLabelUrine.setBounds(410, 40, 100, 23);
        TUrine.setName("TUrine");
        panelGlassIntake.add(TUrine);
        TUrine.setBounds(515, 40, 90, 23);

        // NGT/OGT
        jLabelNGT_OGT.setText("NGT/OGT :");
        jLabelNGT_OGT.setName("jLabelNGT_OGT");
        panelGlassIntake.add(jLabelNGT_OGT);
        jLabelNGT_OGT.setBounds(410, 70, 100, 23);
        TNGT_OGT.setName("TNGT_OGT");
        panelGlassIntake.add(TNGT_OGT);
        TNGT_OGT.setBounds(515, 70, 90, 23);

        // BAB
        jLabelBAB.setText("BAB :");
        jLabelBAB.setName("jLabelBAB");
        panelGlassIntake.add(jLabelBAB);
        jLabelBAB.setBounds(410, 100, 100, 23);
        TBAB.setName("TBAB");
        panelGlassIntake.add(TBAB);
        TBAB.setBounds(515, 100, 90, 23);

        // Drain/Stoma I
        jLabelDrainStomaI.setText("Drain/Stoma I :");
        jLabelDrainStomaI.setName("jLabelDrainStomaI");
        panelGlassIntake.add(jLabelDrainStomaI);
        jLabelDrainStomaI.setBounds(410, 130, 100, 23);
        TDrainStoma1.setName("TDrainStoma1");
        panelGlassIntake.add(TDrainStoma1);
        TDrainStoma1.setBounds(515, 130, 90, 23);

        // Drain/Stoma II
        jLabelDrainStomaII.setText("Drain/Stoma II :");
        jLabelDrainStomaII.setName("jLabelDrainStomaII");
        panelGlassIntake.add(jLabelDrainStomaII);
        jLabelDrainStomaII.setBounds(410, 160, 100, 23);
        TDrainStoma2.setName("TDrainStoma2");
        panelGlassIntake.add(TDrainStoma2);
        TDrainStoma2.setBounds(515, 160, 90, 23);

        // Drain/Stoma III
        jLabelDrainStomaIII.setText("Drain/Stoma III :");
        jLabelDrainStomaIII.setName("jLabelDrainStomaIII");
        panelGlassIntake.add(jLabelDrainStomaIII);
        jLabelDrainStomaIII.setBounds(410, 190, 100, 23);
        TDrainStoma3.setName("TDrainStoma3");
        panelGlassIntake.add(TDrainStoma3);
        TDrainStoma3.setBounds(515, 190, 90, 23);

        // Muntah
        jLabelMuntah.setText("Muntah :");
        jLabelMuntah.setName("jLabelMuntah");
        panelGlassIntake.add(jLabelMuntah);
        jLabelMuntah.setBounds(410, 220, 100, 23);
        TMuntah.setName("TMuntah");
        panelGlassIntake.add(TMuntah);
        TMuntah.setBounds(515, 220, 90, 23);

        // IWL
        jLabelIWL.setText("IWL :");
        jLabelIWL.setName("jLabelIWL");
        panelGlassIntake.add(jLabelIWL);
        jLabelIWL.setBounds(410, 250, 100, 23);
        TIWAL.setName("TIWAL");
        panelGlassIntake.add(TIWAL);
        TIWAL.setBounds(515, 250, 90, 23);

        // Jumlah Total Output kumulatif
        jLabelJumlahTotalOutputKumulatif.setText("Jml Total Output kum. :");
        jLabelJumlahTotalOutputKumulatif.setName("jLabelJumlahTotalOutputKumulatif");
        jLabelJumlahTotalOutputKumulatif.setFont(new java.awt.Font("Tahoma", 1, 11));
        panelGlassIntake.add(jLabelJumlahTotalOutputKumulatif);
        jLabelJumlahTotalOutputKumulatif.setBounds(410, 280, 155, 23);
        TOutputKumulatif.setName("TOutputKumulatif");
        panelGlassIntake.add(TOutputKumulatif);
        TOutputKumulatif.setBounds(570, 280, 90, 23);

        // Keseimbangan Cairan
        jLabelKeseimbanganCairan.setText("Keseimbangan Cairan :");
        jLabelKeseimbanganCairan.setName("jLabelKeseimbanganCairan");
        jLabelKeseimbanganCairan.setFont(new java.awt.Font("Tahoma", 1, 11));
        panelGlassIntake.add(jLabelKeseimbanganCairan);
        jLabelKeseimbanganCairan.setBounds(410, 310, 155, 23);
        TKeseimbanganCairan.setName("TKeseimbanganCairan");
        panelGlassIntake.add(TKeseimbanganCairan);
        TKeseimbanganCairan.setBounds(570, 310, 90, 23);

        // Petugas (paling bawah)
        // Petugas dipindah ke panel atas (NIPHeader)
        jLabel30Int.setVisible(false);
        NIPIntake.setVisible(false);
        NamaPetugasIntake.setVisible(false);
        btnPetugasIntake.setVisible(false);

        // Tombol Grafik Balance Cairan
        BtnGrafikBalance = new widget.Button();
        BtnGrafikBalance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png")));
        BtnGrafikBalance.setText("Grafik Balance");
        BtnGrafikBalance.setToolTipText("Tampilkan Grafik Balance Cairan");
        BtnGrafikBalance.setFocusPainted(false);
        BtnGrafikBalance.addActionListener(e -> tampilGrafikBalanceCairan());
        panelGlassIntake.add(BtnGrafikBalance);
        BtnGrafikBalance.setBounds(10, 345, 130, 23);

        panelGlassIntake.setMinimumSize(new java.awt.Dimension(700, 400));
        scrollPaneIntake.setViewportView(panelGlassIntake);
        scrollPaneIntake.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneIntake.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPaneIntake.setPreferredSize(new java.awt.Dimension(800, 300));
        PanelInputIntake.add(scrollPaneIntake, java.awt.BorderLayout.PAGE_START);
        internalFrame3.add(PanelInputIntake, java.awt.BorderLayout.PAGE_START);
        TabICU.addTab("Intake - Output", internalFrame3);

        internalFrame4.setBorder(null);
        internalFrame4.setName("internalFrame4");
        internalFrame4.setLayout(new java.awt.BorderLayout(1, 1));
        Scroll2.setName("Scroll2");
        tbMedikasi.setName("tbMedikasi");
        tbMedikasi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbMedikasiMouseClicked(evt);
            }
        });
        Scroll2.setViewportView(tbMedikasi);
        internalFrame4.add(Scroll2, java.awt.BorderLayout.CENTER);
        PanelInputMedikasi.setName("PanelInputMedikasi");
        PanelInputMedikasi.setOpaque(false);
        PanelInputMedikasi.setPreferredSize(new java.awt.Dimension(192, 160));
        PanelInputMedikasi.setLayout(new java.awt.BorderLayout(1, 1));
        ChkInputMedikasi.setText(".: Input Catatan Pengobatan");
        ChkInputMedikasi.setBorderPainted(true);
        ChkInputMedikasi.setBorderPaintedFlat(true);
        ChkInputMedikasi.setFocusable(false);
        ChkInputMedikasi.setName("ChkInputMedikasi");
        ChkInputMedikasi.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInputMedikasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputMedikasiActionPerformed(evt);
            }
        });
        PanelInputMedikasi.add(ChkInputMedikasi, java.awt.BorderLayout.PAGE_END);
        scrollPaneMedikasi.setName("scrollPaneMedikasi");
        panelGlassMedikasi.setName("panelGlassMedikasi");
        panelGlassMedikasi.setPreferredSize(new java.awt.Dimension(44, 140));
        panelGlassMedikasi.setLayout(null);

        // Row 1: Nama Obat, Keterangan
        // (Input dokter dihapus — dokter mem-paraf via checkbox "Paraf Dokter" di tabel)
        jLabel32Med.setText("Nama Obat :");
        jLabel32Med.setName("jLabel32Med");
        panelGlassMedikasi.add(jLabel32Med);
        jLabel32Med.setBounds(0, 10, 75, 23);
        TNamaObat.setName("TNamaObat");
        panelGlassMedikasi.add(TNamaObat);
        TNamaObat.setBounds(80, 10, 300, 23);

        jLabelKetMed.setText("Keterangan :");
        jLabelKetMed.setName("jLabelKetMed");
        panelGlassMedikasi.add(jLabelKetMed);
        jLabelKetMed.setBounds(390, 10, 75, 23);
        TKeterangan.setName("TKeterangan");
        panelGlassMedikasi.add(TKeterangan);
        TKeterangan.setBounds(470, 10, 350, 23);

        // Row 2: Kekuatan, Bentuk Sediaan, Aturan Pakai, Rute Pemberian
        jLabel33Med.setText("Kekuatan :");
        jLabel33Med.setName("jLabel33Med");
        panelGlassMedikasi.add(jLabel33Med);
        jLabel33Med.setBounds(0, 40, 65, 23);
        TKekuatan.setName("TKekuatan");
        panelGlassMedikasi.add(TKekuatan);
        TKekuatan.setBounds(70, 40, 80, 23);

        jLabel34Med.setText("Sediaan :");
        jLabel34Med.setName("jLabel34Med");
        panelGlassMedikasi.add(jLabel34Med);
        jLabel34Med.setBounds(155, 40, 55, 23);
        TBentukSediaan.setName("TBentukSediaan");
        panelGlassMedikasi.add(TBentukSediaan);
        TBentukSediaan.setBounds(215, 40, 100, 23);

        jLabel35Med.setText("Aturan Pakai :");
        jLabel35Med.setName("jLabel35Med");
        panelGlassMedikasi.add(jLabel35Med);
        jLabel35Med.setBounds(320, 40, 80, 23);
        TAturanPakai.setName("TAturanPakai");
        panelGlassMedikasi.add(TAturanPakai);
        TAturanPakai.setBounds(405, 40, 150, 23);

        jLabel36Med.setText("Rute :");
        jLabel36Med.setName("jLabel36Med");
        panelGlassMedikasi.add(jLabel36Med);
        jLabel36Med.setBounds(560, 40, 35, 23);
        TRutePemberian.setName("TRutePemberian");
        panelGlassMedikasi.add(TRutePemberian);
        TRutePemberian.setBounds(600, 40, 100, 23);

        // Row 3: Keterangan simbol (Tgl Mulai/Stop & Verifikasi Apoteker dihapus —
        // tidak ada di lembar fisik)
        javax.swing.JLabel lblSimbol = new javax.swing.JLabel(
            "<html><font size='1'>Klik sel jam = tandai obat diberikan (muncul centang/checkbox) | Klik-kanan sel jam = status lain: Terjadwal(o), Tolak(T), Tunda(K), Efek Samping(ESO), Tdk Ada Persediaan(TAP) | Paraf Dokter dicentang oleh dokter</font></html>");
        panelGlassMedikasi.add(lblSimbol);
        lblSimbol.setBounds(0, 70, 820, 16);

        // Hidden fields
        jLabel39Med.setVisible(false);
        TVerifikasiApoteker.setVisible(false);
        jLabel40Med.setVisible(false);
        NIPMedikasi.setVisible(false);
        NamaPetugasMedikasi.setVisible(false);
        btnPetugasMedikasi.setVisible(false);

        scrollPaneMedikasi.setViewportView(panelGlassMedikasi);
        PanelInputMedikasi.add(scrollPaneMedikasi, java.awt.BorderLayout.PAGE_START);
        internalFrame4.add(PanelInputMedikasi, java.awt.BorderLayout.PAGE_START);
        // Disisipkan di index 2 (setelah Hemodinamik=0 & Respirasi=1, sebelum Intake-Output)
        TabICU.insertTab("Medikasi", null, internalFrame4, null, 2);

        // Setup Catatan Terintegrasi ada di bawah (sebelum TabICU.addTab)

        // Setup Tab Catatan Keperawatan
        internalFrameKeperawatan.setBorder(null);
        internalFrameKeperawatan.setName("internalFrameKeperawatan");
        internalFrameKeperawatan.setLayout(new java.awt.BorderLayout(1, 1));

        ScrollKeperawatan.setName("ScrollKeperawatan");
        tbCatatanKeperawatan.setName("tbCatatanKeperawatan");
        tbCatatanKeperawatan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbCatatanKeperawatanMouseClicked(evt);
            }
        });
        ScrollKeperawatan.setViewportView(tbCatatanKeperawatan);
        internalFrameKeperawatan.add(ScrollKeperawatan, java.awt.BorderLayout.CENTER);

        PanelInputKeperawatan.setName("PanelInputKeperawatan");
        PanelInputKeperawatan.setOpaque(false);
        PanelInputKeperawatan.setPreferredSize(new java.awt.Dimension(1170, 320));
        PanelInputKeperawatan.setLayout(new java.awt.BorderLayout(1, 1));
        ChkInputKeperawatan.setText(".: Input Implementasi Keperawatan");
        ChkInputKeperawatan.setBorderPainted(true);
        ChkInputKeperawatan.setBorderPaintedFlat(true);
        ChkInputKeperawatan.setFocusable(false);
        ChkInputKeperawatan.setName("ChkInputKeperawatan");
        ChkInputKeperawatan.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInputKeperawatan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputKeperawatanActionPerformed(evt);
            }
        });
        PanelInputKeperawatan.add(ChkInputKeperawatan, java.awt.BorderLayout.PAGE_END);

        scrollPaneCatatanKeperawatan.setName("scrollPaneCatatanKeperawatan");
        panelGlassCatatanKeperawatan.setName("panelGlassCatatanKeperawatan");
        // 3 kolom x 10 baris layout: setiap kolom = 380px (label 230 + tb 150),
        // baris height 26px. Total 10*26 + 20 padding = 290.
        panelGlassCatatanKeperawatan.setPreferredSize(new java.awt.Dimension(1170, 290));
        panelGlassCatatanKeperawatan.setLayout(null);

        // Helper inline: tiap item = label (no + nama, lebar 230) + textbox (lebar 150)
        // Posisi: col 0 di x=0, col 1 di x=390, col 2 di x=780. Row N di y=10 + N*26.

        // ── KOLOM 1 (item 1-10) ──
        jLabelKep01.setText("1. Oral Hygiene :");                              jLabelKep01.setName("jLabelKep01");
        panelGlassCatatanKeperawatan.add(jLabelKep01); jLabelKep01.setBounds(0, 10, 230, 23);
        TKep01.setName("TKep01"); panelGlassCatatanKeperawatan.add(TKep01); TKep01.setBounds(235, 10, 150, 23);

        jLabelKep02.setText("2. Suctioning :");                                jLabelKep02.setName("jLabelKep02");
        panelGlassCatatanKeperawatan.add(jLabelKep02); jLabelKep02.setBounds(0, 36, 230, 23);
        TKep02.setName("TKep02"); panelGlassCatatanKeperawatan.add(TKep02); TKep02.setBounds(235, 36, 150, 23);

        jLabelKep03.setText("3. Cek Kepatenan ETT/Trakheostomy :");           jLabelKep03.setName("jLabelKep03");
        panelGlassCatatanKeperawatan.add(jLabelKep03); jLabelKep03.setBounds(0, 62, 230, 23);
        TKep03.setName("TKep03"); panelGlassCatatanKeperawatan.add(TKep03); TKep03.setBounds(235, 62, 150, 23);

        jLabelKep04.setText("4. Kempes Cuff Rawat ETT/Trakeostomy :");        jLabelKep04.setName("jLabelKep04");
        panelGlassCatatanKeperawatan.add(jLabelKep04); jLabelKep04.setBounds(0, 88, 230, 23);
        TKep04.setName("TKep04"); panelGlassCatatanKeperawatan.add(TKep04); TKep04.setBounds(235, 88, 150, 23);

        jLabelKep05.setText("5. Perawatan ETT/Trakheostomy :");               jLabelKep05.setName("jLabelKep05");
        panelGlassCatatanKeperawatan.add(jLabelKep05); jLabelKep05.setBounds(0, 114, 230, 23);
        TKep05.setName("TKep05"); panelGlassCatatanKeperawatan.add(TKep05); TKep05.setBounds(235, 114, 150, 23);

        jLabelKep06.setText("6. Auskultasi Suara Napas :");                   jLabelKep06.setName("jLabelKep06");
        panelGlassCatatanKeperawatan.add(jLabelKep06); jLabelKep06.setBounds(0, 140, 230, 23);
        TKep06.setName("TKep06"); panelGlassCatatanKeperawatan.add(TKep06); TKep06.setBounds(235, 140, 150, 23);

        jLabelKep07.setText("7. Melakukan Inhalasi :");                       jLabelKep07.setName("jLabelKep07");
        panelGlassCatatanKeperawatan.add(jLabelKep07); jLabelKep07.setBounds(0, 166, 230, 23);
        TKep07.setName("TKep07"); panelGlassCatatanKeperawatan.add(TKep07); TKep07.setBounds(235, 166, 150, 23);

        jLabelKep08.setText("8. Mengajarkan Batuk Efektif :");                jLabelKep08.setName("jLabelKep08");
        panelGlassCatatanKeperawatan.add(jLabelKep08); jLabelKep08.setBounds(0, 192, 230, 23);
        TKep08.setName("TKep08"); panelGlassCatatanKeperawatan.add(TKep08); TKep08.setBounds(235, 192, 150, 23);

        jLabelKep09.setText("9. Pemberian Minum/Makan (Oral/NGT) :");         jLabelKep09.setName("jLabelKep09");
        panelGlassCatatanKeperawatan.add(jLabelKep09); jLabelKep09.setBounds(0, 218, 230, 23);
        TKep09.setName("TKep09"); panelGlassCatatanKeperawatan.add(TKep09); TKep09.setBounds(235, 218, 150, 23);

        jLabelKep10.setText("10. Cek Kepatenan NGT :");                       jLabelKep10.setName("jLabelKep10");
        panelGlassCatatanKeperawatan.add(jLabelKep10); jLabelKep10.setBounds(0, 244, 230, 23);
        TKep10.setName("TKep10"); panelGlassCatatanKeperawatan.add(TKep10); TKep10.setBounds(235, 244, 150, 23);

        // ── KOLOM 2 (item 11-20) ──
        jLabelKep11.setText("11. Pemasangan/Rawat Alat Invasif :");           jLabelKep11.setName("jLabelKep11");
        panelGlassCatatanKeperawatan.add(jLabelKep11); jLabelKep11.setBounds(390, 10, 230, 23);
        TKep11.setName("TKep11"); panelGlassCatatanKeperawatan.add(TKep11); TKep11.setBounds(625, 10, 150, 23);

        jLabelKep12.setText("12. Pengukuran CVP :");                          jLabelKep12.setName("jLabelKep12");
        panelGlassCatatanKeperawatan.add(jLabelKep12); jLabelKep12.setBounds(390, 36, 230, 23);
        TKep12.setName("TKep12"); panelGlassCatatanKeperawatan.add(TKep12); TKep12.setBounds(625, 36, 150, 23);

        jLabelKep13.setText("13. Observasi & Pemberian Transfusi :");         jLabelKep13.setName("jLabelKep13");
        panelGlassCatatanKeperawatan.add(jLabelKep13); jLabelKep13.setBounds(390, 62, 230, 23);
        TKep13.setName("TKep13"); panelGlassCatatanKeperawatan.add(TKep13); TKep13.setBounds(625, 62, 150, 23);

        jLabelKep14.setText("14. Sample Darah (Arteri/Vena) :");              jLabelKep14.setName("jLabelKep14");
        panelGlassCatatanKeperawatan.add(jLabelKep14); jLabelKep14.setBounds(390, 88, 230, 23);
        TKep14.setName("TKep14"); panelGlassCatatanKeperawatan.add(TKep14); TKep14.setBounds(625, 88, 150, 23);

        jLabelKep15.setText("15. Perawatan Luka Dekubitus :");                jLabelKep15.setName("jLabelKep15");
        panelGlassCatatanKeperawatan.add(jLabelKep15); jLabelKep15.setBounds(390, 114, 230, 23);
        TKep15.setName("TKep15"); panelGlassCatatanKeperawatan.add(TKep15); TKep15.setBounds(625, 114, 150, 23);

        jLabelKep16.setText("16. Perawatan Luka (Lain) :");                   jLabelKep16.setName("jLabelKep16");
        panelGlassCatatanKeperawatan.add(jLabelKep16); jLabelKep16.setBounds(390, 140, 230, 23);
        TKep16.setName("TKep16"); panelGlassCatatanKeperawatan.add(TKep16); TKep16.setBounds(625, 140, 150, 23);

        jLabelKep17.setText("17. Fisio Terapi Dada :");                       jLabelKep17.setName("jLabelKep17");
        panelGlassCatatanKeperawatan.add(jLabelKep17); jLabelKep17.setBounds(390, 166, 230, 23);
        TKep17.setName("TKep17"); panelGlassCatatanKeperawatan.add(TKep17); TKep17.setBounds(625, 166, 150, 23);

        jLabelKep18.setText("18. Memandikan Pasien :");                       jLabelKep18.setName("jLabelKep18");
        panelGlassCatatanKeperawatan.add(jLabelKep18); jLabelKep18.setBounds(390, 192, 230, 23);
        TKep18.setName("TKep18"); panelGlassCatatanKeperawatan.add(TKep18); TKep18.setBounds(625, 192, 150, 23);

        jLabelKep19.setText("19. Menolong BAB/BAK :");                        jLabelKep19.setName("jLabelKep19");
        panelGlassCatatanKeperawatan.add(jLabelKep19); jLabelKep19.setBounds(390, 218, 230, 23);
        TKep19.setName("TKep19"); panelGlassCatatanKeperawatan.add(TKep19); TKep19.setBounds(625, 218, 150, 23);

        jLabelKep20.setText("20. Perawatan Kateter Urine :");                 jLabelKep20.setName("jLabelKep20");
        panelGlassCatatanKeperawatan.add(jLabelKep20); jLabelKep20.setBounds(390, 244, 230, 23);
        TKep20.setName("TKep20"); panelGlassCatatanKeperawatan.add(TKep20); TKep20.setBounds(625, 244, 150, 23);

        // ── KOLOM 3 (item 21-30) ──
        jLabelKep21.setText("21. Positioning :");                             jLabelKep21.setName("jLabelKep21");
        panelGlassCatatanKeperawatan.add(jLabelKep21); jLabelKep21.setBounds(780, 10, 230, 23);
        TKep21.setName("TKep21"); panelGlassCatatanKeperawatan.add(TKep21); TKep21.setBounds(1015, 10, 150, 23);

        jLabelKep22.setText("22. Mobilisasi (Mika/Miki) :");                  jLabelKep22.setName("jLabelKep22");
        panelGlassCatatanKeperawatan.add(jLabelKep22); jLabelKep22.setBounds(780, 36, 230, 23);
        TKep22.setName("TKep22"); panelGlassCatatanKeperawatan.add(TKep22); TKep22.setBounds(1015, 36, 150, 23);

        jLabelKep23.setText("23. ROM Aktif/Pasif :");                         jLabelKep23.setName("jLabelKep23");
        panelGlassCatatanKeperawatan.add(jLabelKep23); jLabelKep23.setBounds(780, 62, 230, 23);
        TKep23.setName("TKep23"); panelGlassCatatanKeperawatan.add(TKep23); TKep23.setBounds(1015, 62, 150, 23);

        jLabelKep24.setText("24. Water Tepid Sponge :");                      jLabelKep24.setName("jLabelKep24");
        panelGlassCatatanKeperawatan.add(jLabelKep24); jLabelKep24.setBounds(780, 88, 230, 23);
        TKep24.setName("TKep24"); panelGlassCatatanKeperawatan.add(TKep24); TKep24.setBounds(1015, 88, 150, 23);

        jLabelKep25.setText("25. Pemeriksaan EKG :");                         jLabelKep25.setName("jLabelKep25");
        panelGlassCatatanKeperawatan.add(jLabelKep25); jLabelKep25.setBounds(780, 114, 230, 23);
        TKep25.setName("TKep25"); panelGlassCatatanKeperawatan.add(TKep25); TKep25.setBounds(1015, 114, 150, 23);

        jLabelKep26.setText("26. Edukasi :");                                 jLabelKep26.setName("jLabelKep26");
        panelGlassCatatanKeperawatan.add(jLabelKep26); jLabelKep26.setBounds(780, 140, 230, 23);
        TKep26.setName("TKep26"); panelGlassCatatanKeperawatan.add(TKep26); TKep26.setBounds(1015, 140, 150, 23);

        jLabelKep27.setText("27. Kolaborasi :");                              jLabelKep27.setName("jLabelKep27");
        panelGlassCatatanKeperawatan.add(jLabelKep27); jLabelKep27.setBounds(780, 166, 230, 23);
        TKep27.setName("TKep27"); panelGlassCatatanKeperawatan.add(TKep27); TKep27.setBounds(1015, 166, 150, 23);

        jLabelKep28.setText("28. Lain-lain 1 :");                             jLabelKep28.setName("jLabelKep28");
        panelGlassCatatanKeperawatan.add(jLabelKep28); jLabelKep28.setBounds(780, 192, 230, 23);
        TKep28.setName("TKep28"); panelGlassCatatanKeperawatan.add(TKep28); TKep28.setBounds(1015, 192, 150, 23);

        jLabelKep29.setText("29. Lain-lain 2 :");                             jLabelKep29.setName("jLabelKep29");
        panelGlassCatatanKeperawatan.add(jLabelKep29); jLabelKep29.setBounds(780, 218, 230, 23);
        TKep29.setName("TKep29"); panelGlassCatatanKeperawatan.add(TKep29); TKep29.setBounds(1015, 218, 150, 23);

        jLabelKep30.setText("30. Lain-lain 3 :");                             jLabelKep30.setName("jLabelKep30");
        panelGlassCatatanKeperawatan.add(jLabelKep30); jLabelKep30.setBounds(780, 244, 230, 23);
        TKep30.setName("TKep30"); panelGlassCatatanKeperawatan.add(TKep30); TKep30.setBounds(1015, 244, 150, 23);

        // Petugas
        // Petugas dipindah ke panel atas (NIPHeader)
        jLabelPetugasKeperawatan.setVisible(false);
        NIPKeperawatan.setVisible(false);
        NamaPetugasKeperawatan.setVisible(false);
        btnPetugasKeperawatan.setVisible(false);

        scrollPaneCatatanKeperawatan.setViewportView(panelGlassCatatanKeperawatan);
        PanelInputKeperawatan.add(scrollPaneCatatanKeperawatan, java.awt.BorderLayout.PAGE_START);
        internalFrameKeperawatan.add(PanelInputKeperawatan, java.awt.BorderLayout.PAGE_START);
        TabICU.addTab("Implementasi Keperawatan", internalFrameKeperawatan);

        // Setup Tab Bundle Care with sub tabs
        internalFrameBundleCare.setBorder(null);
        internalFrameBundleCare.setName("internalFrameBundleCare");
        internalFrameBundleCare.setLayout(new java.awt.BorderLayout(1, 1));

        TabBundleCare.setBackground(new java.awt.Color(254, 255, 254));
        TabBundleCare.setForeground(new java.awt.Color(50, 50, 50));
        TabBundleCare.setFont(new java.awt.Font("Tahoma", 0, 11));
        TabBundleCare.setName("TabBundleCare");

        // Sub Tab 1: Bundle VAP
        internalFrameVAP.setBorder(null);
        internalFrameVAP.setName("internalFrameVAP");
        internalFrameVAP.setLayout(new java.awt.BorderLayout(1, 1));

        ScrollVAP.setName("ScrollVAP");
        tbBundleVAP.setName("tbBundleVAP");
        tbBundleVAP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbBundleVAPMouseClicked(evt);
            }
        });
        ScrollVAP.setViewportView(tbBundleVAP);
        internalFrameVAP.add(ScrollVAP, java.awt.BorderLayout.CENTER);

        PanelInputVAP.setName("PanelInputVAP");
        PanelInputVAP.setOpaque(false);
        PanelInputVAP.setPreferredSize(new java.awt.Dimension(192, 120));
        PanelInputVAP.setLayout(new java.awt.BorderLayout(1, 1));
        ChkInputVAP.setText(".: Input Bundle VAP");
        ChkInputVAP.setBorderPainted(true);
        ChkInputVAP.setBorderPaintedFlat(true);
        ChkInputVAP.setFocusable(false);
        ChkInputVAP.setName("ChkInputVAP");
        ChkInputVAP.setPreferredSize(new java.awt.Dimension(192, 20));
        PanelInputVAP.add(ChkInputVAP, java.awt.BorderLayout.PAGE_END);

        scrollPaneBundleVAP.setName("scrollPaneBundleVAP");
        panelGlassBundleVAP.setName("panelGlassBundleVAP");
        panelGlassBundleVAP.setPreferredSize(new java.awt.Dimension(44, 200));
        panelGlassBundleVAP.setLayout(null);

        // Bundle VAP detailed content
        // Row 1: Head Up 30-45
        jLabelHeadUpVAP.setText("Head Up 30-45° :");
        jLabelHeadUpVAP.setName("jLabelHeadUpVAP");
        panelGlassBundleVAP.add(jLabelHeadUpVAP);
        jLabelHeadUpVAP.setBounds(0, 10, 120, 23);

        javax.swing.JLabel jLabelPagi1 = new javax.swing.JLabel("Pagi:");
        jLabelPagi1.setBounds(125, 10, 35, 23);
        panelGlassBundleVAP.add(jLabelPagi1);
        cmbHeadUpPagiVAP.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbHeadUpPagiVAP.setName("cmbHeadUpPagiVAP");
        cmbHeadUpPagiVAP.setBounds(165, 10, 60, 23);
        panelGlassBundleVAP.add(cmbHeadUpPagiVAP);

        javax.swing.JLabel jLabelSore1 = new javax.swing.JLabel("Sore:");
        jLabelSore1.setBounds(230, 10, 35, 23);
        panelGlassBundleVAP.add(jLabelSore1);
        cmbHeadUpSoreVAP.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbHeadUpSoreVAP.setName("cmbHeadUpSoreVAP");
        cmbHeadUpSoreVAP.setBounds(270, 10, 60, 23);
        panelGlassBundleVAP.add(cmbHeadUpSoreVAP);

        // Row 2: Oral Hygiene
        jLabelOralHygieneVAP.setText("Oral Hygiene :");
        jLabelOralHygieneVAP.setName("jLabelOralHygieneVAP");
        panelGlassBundleVAP.add(jLabelOralHygieneVAP);
        jLabelOralHygieneVAP.setBounds(0, 40, 100, 23);

        javax.swing.JLabel jLabelPagi2 = new javax.swing.JLabel("Pagi:");
        jLabelPagi2.setBounds(125, 40, 35, 23);
        panelGlassBundleVAP.add(jLabelPagi2);
        cmbOralHygienePagiVAP.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbOralHygienePagiVAP.setName("cmbOralHygienePagiVAP");
        cmbOralHygienePagiVAP.setBounds(165, 40, 60, 23);
        panelGlassBundleVAP.add(cmbOralHygienePagiVAP);

        javax.swing.JLabel jLabelSore2 = new javax.swing.JLabel("Sore:");
        jLabelSore2.setBounds(230, 40, 35, 23);
        panelGlassBundleVAP.add(jLabelSore2);
        cmbOralHygieneSoreVAP.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbOralHygieneSoreVAP.setName("cmbOralHygieneSoreVAP");
        cmbOralHygieneSoreVAP.setBounds(270, 40, 60, 23);
        panelGlassBundleVAP.add(cmbOralHygieneSoreVAP);

        // Row 3: Evaluasi sedasi
        jLabelEvaluasiSedasiVAP.setText("Evaluasi sedasi :");
        jLabelEvaluasiSedasiVAP.setName("jLabelEvaluasiSedasiVAP");
        panelGlassBundleVAP.add(jLabelEvaluasiSedasiVAP);
        jLabelEvaluasiSedasiVAP.setBounds(0, 70, 120, 23);

        javax.swing.JLabel jLabelPagi3 = new javax.swing.JLabel("Pagi:");
        jLabelPagi3.setBounds(125, 70, 35, 23);
        panelGlassBundleVAP.add(jLabelPagi3);
        cmbEvaluasiSedasiVAP.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbEvaluasiSedasiVAP.setName("cmbEvaluasiSedasiVAP");
        cmbEvaluasiSedasiVAP.setBounds(165, 70, 60, 23);
        panelGlassBundleVAP.add(cmbEvaluasiSedasiVAP);

        // Row 4: Profilaksis DVT
        jLabelProfilaksisDVTVAP.setText("Profilaksis DVT :");
        jLabelProfilaksisDVTVAP.setName("jLabelProfilaksisDVTVAP");
        panelGlassBundleVAP.add(jLabelProfilaksisDVTVAP);
        jLabelProfilaksisDVTVAP.setBounds(0, 100, 120, 23);

        javax.swing.JLabel jLabelPagi4 = new javax.swing.JLabel("Pagi:");
        jLabelPagi4.setBounds(125, 100, 35, 23);
        panelGlassBundleVAP.add(jLabelPagi4);
        cmbProfilaksisDVTVAP.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbProfilaksisDVTVAP.setName("cmbProfilaksisDVTVAP");
        cmbProfilaksisDVTVAP.setBounds(165, 100, 60, 23);
        panelGlassBundleVAP.add(cmbProfilaksisDVTVAP);

        // Row 5: Profilactic Peptic Ulcer
        jLabelProfilaksisPepticVAP.setText("Profilactic Peptic Ulcer :");
        jLabelProfilaksisPepticVAP.setName("jLabelProfilaksisPepticVAP");
        panelGlassBundleVAP.add(jLabelProfilaksisPepticVAP);
        jLabelProfilaksisPepticVAP.setBounds(0, 130, 160, 23);

        javax.swing.JLabel jLabelPagi5 = new javax.swing.JLabel("Pagi:");
        jLabelPagi5.setBounds(165, 130, 35, 23);
        panelGlassBundleVAP.add(jLabelPagi5);
        cmbProfilaksisPepticVAP.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbProfilaksisPepticVAP.setName("cmbProfilaksisPepticVAP");
        cmbProfilaksisPepticVAP.setBounds(205, 130, 60, 23);
        panelGlassBundleVAP.add(cmbProfilaksisPepticVAP);

        // Petugas dipindah ke panel atas (NIPHeader)
        jLabelPetugasVAP.setVisible(false);
        NIPBundleVAP.setVisible(false);
        NamaPetugasBundleVAP.setVisible(false);
        btnPetugasBundleVAP.setVisible(false);

        scrollPaneBundleVAP.setViewportView(panelGlassBundleVAP);
        PanelInputVAP.add(scrollPaneBundleVAP, java.awt.BorderLayout.PAGE_START);
        internalFrameVAP.add(PanelInputVAP, java.awt.BorderLayout.PAGE_START);
        TabBundleCare.addTab("BUNDLE VAP", internalFrameVAP);

        // Sub Tab 2: Bundle CVC/IV CATHETER/PICC
        internalFrameCVC.setBorder(null);
        internalFrameCVC.setName("internalFrameCVC");
        internalFrameCVC.setLayout(new java.awt.BorderLayout(1, 1));

        ScrollCVC.setName("ScrollCVC");
        tbBundleCVC.setName("tbBundleCVC");
        tbBundleCVC.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbBundleCVCMouseClicked(evt);
            }
        });
        ScrollCVC.setViewportView(tbBundleCVC);
        internalFrameCVC.add(ScrollCVC, java.awt.BorderLayout.CENTER);

        PanelInputCVC.setName("PanelInputCVC");
        PanelInputCVC.setOpaque(false);
        PanelInputCVC.setPreferredSize(new java.awt.Dimension(192, 120));
        PanelInputCVC.setLayout(new java.awt.BorderLayout(1, 1));
        ChkInputCVC.setText(".: Input Bundle CVC");
        ChkInputCVC.setBorderPainted(true);
        ChkInputCVC.setBorderPaintedFlat(true);
        ChkInputCVC.setFocusable(false);
        ChkInputCVC.setName("ChkInputCVC");
        ChkInputCVC.setPreferredSize(new java.awt.Dimension(192, 20));
        PanelInputCVC.add(ChkInputCVC, java.awt.BorderLayout.PAGE_END);

        scrollPaneBundleCVC.setName("scrollPaneBundleCVC");
        panelGlassBundleCVC.setName("panelGlassBundleCVC");
        panelGlassBundleCVC.setPreferredSize(new java.awt.Dimension(44, 300));
        panelGlassBundleCVC.setLayout(null);

        // Bundle CVC detailed content
        // Row 1: Kebutuhan Pengguna CVC
        jLabelKebutuhanCVC.setText("Kebutuhan Pengguna CVC :");
        jLabelKebutuhanCVC.setName("jLabelKebutuhanCVC");
        panelGlassBundleCVC.add(jLabelKebutuhanCVC);
        jLabelKebutuhanCVC.setBounds(0, 10, 160, 23);
        cmbKebutuhanCVC.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbKebutuhanCVC.setName("cmbKebutuhanCVC");
        cmbKebutuhanCVC.setBounds(165, 10, 60, 23);
        panelGlassBundleCVC.add(cmbKebutuhanCVC);

        // Row 2: Mencuci Tangan
        jLabelMencuciTanganCVC.setText("Mencuci Tangan :");
        jLabelMencuciTanganCVC.setName("jLabelMencuciTanganCVC");
        panelGlassBundleCVC.add(jLabelMencuciTanganCVC);
        jLabelMencuciTanganCVC.setBounds(0, 40, 120, 23);
        cmbMencuciTanganCVC.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbMencuciTanganCVC.setName("cmbMencuciTanganCVC");
        cmbMencuciTanganCVC.setBounds(125, 40, 60, 23);
        panelGlassBundleCVC.add(cmbMencuciTanganCVC);

        // Row 3: Tempat Inserse Kateter Header
        jLabelTempatInserseCVC.setText("Tempat Inserse Kateter :");
        jLabelTempatInserseCVC.setName("jLabelTempatInserseCVC");
        jLabelTempatInserseCVC.setFont(new java.awt.Font("Tahoma", 1, 11));
        panelGlassBundleCVC.add(jLabelTempatInserseCVC);
        jLabelTempatInserseCVC.setBounds(0, 70, 200, 23);

        // Row 4: Suhu > 38
        jLabelSuhuCVC.setText("* Suhu > 38° :");
        jLabelSuhuCVC.setName("jLabelSuhuCVC");
        panelGlassBundleCVC.add(jLabelSuhuCVC);
        jLabelSuhuCVC.setBounds(20, 100, 100, 23);
        cmbSuhuCVC.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbSuhuCVC.setName("cmbSuhuCVC");
        cmbSuhuCVC.setBounds(125, 100, 60, 23);
        panelGlassBundleCVC.add(cmbSuhuCVC);

        // Row 5: Nyeri
        jLabelNyeriCVC.setText("* Nyeri :");
        jLabelNyeriCVC.setName("jLabelNyeriCVC");
        panelGlassBundleCVC.add(jLabelNyeriCVC);
        jLabelNyeriCVC.setBounds(200, 100, 60, 23);
        cmbNyeriCVC.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbNyeriCVC.setName("cmbNyeriCVC");
        cmbNyeriCVC.setBounds(265, 100, 60, 23);
        panelGlassBundleCVC.add(cmbNyeriCVC);

        // Row 6: Kemerahan
        jLabelKemerahanCVC.setText("* Kemerahan :");
        jLabelKemerahanCVC.setName("jLabelKemerahanCVC");
        panelGlassBundleCVC.add(jLabelKemerahanCVC);
        jLabelKemerahanCVC.setBounds(20, 130, 100, 23);
        cmbKemerahanCVC.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbKemerahanCVC.setName("cmbKemerahanCVC");
        cmbKemerahanCVC.setBounds(125, 130, 60, 23);
        panelGlassBundleCVC.add(cmbKemerahanCVC);

        // Row 7: Bengkak
        jLabelBengkakCVC.setText("* Bengkak :");
        jLabelBengkakCVC.setName("jLabelBengkakCVC");
        panelGlassBundleCVC.add(jLabelBengkakCVC);
        jLabelBengkakCVC.setBounds(200, 130, 80, 23);
        cmbBengkakCVC.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbBengkakCVC.setName("cmbBengkakCVC");
        cmbBengkakCVC.setBounds(285, 130, 60, 23);
        panelGlassBundleCVC.add(cmbBengkakCVC);

        // Row 8: Pus
        jLabelPusCVC.setText("* Pus :");
        jLabelPusCVC.setName("jLabelPusCVC");
        panelGlassBundleCVC.add(jLabelPusCVC);
        jLabelPusCVC.setBounds(20, 160, 60, 23);
        cmbPusCVC.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbPusCVC.setName("cmbPusCVC");
        cmbPusCVC.setBounds(85, 160, 60, 23);
        panelGlassBundleCVC.add(cmbPusCVC);

        // Row 9: Penggantian dan Monitoring
        jLabelPenggantianMonitoringCVC.setText("Penggantian dan Monitoring :");
        jLabelPenggantianMonitoringCVC.setName("jLabelPenggantianMonitoringCVC");
        panelGlassBundleCVC.add(jLabelPenggantianMonitoringCVC);
        jLabelPenggantianMonitoringCVC.setBounds(0, 190, 180, 23);
        cmbPenggantianMonitoringCVC.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbPenggantianMonitoringCVC.setName("cmbPenggantianMonitoringCVC");
        cmbPenggantianMonitoringCVC.setBounds(185, 190, 60, 23);
        panelGlassBundleCVC.add(cmbPenggantianMonitoringCVC);

        // Row 10: Balutan Insersi
        jLabelBalutanInsersiCVC.setText("Balutan Insersi :");
        jLabelBalutanInsersiCVC.setName("jLabelBalutanInsersiCVC");
        panelGlassBundleCVC.add(jLabelBalutanInsersiCVC);
        jLabelBalutanInsersiCVC.setBounds(0, 220, 120, 23);
        cmbBalutanInsersiCVC.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbBalutanInsersiCVC.setName("cmbBalutanInsersiCVC");
        cmbBalutanInsersiCVC.setBounds(125, 220, 60, 23);
        panelGlassBundleCVC.add(cmbBalutanInsersiCVC);

        // Row 11: Penggantian berkala header
        jLabelPenggantianBerkala.setText("Penggantian berkala :");
        jLabelPenggantianBerkala.setName("jLabelPenggantianBerkala");
        jLabelPenggantianBerkala.setFont(new java.awt.Font("Tahoma", 1, 11));
        panelGlassBundleCVC.add(jLabelPenggantianBerkala);
        jLabelPenggantianBerkala.setBounds(200, 220, 150, 23);

        // Row 12: infus set, Tree Way, Valve Konektor
        jLabelInfusSetCVC.setText("* infus set :");
        jLabelInfusSetCVC.setName("jLabelInfusSetCVC");
        panelGlassBundleCVC.add(jLabelInfusSetCVC);
        jLabelInfusSetCVC.setBounds(20, 250, 80, 23);
        cmbInfusSetCVC.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbInfusSetCVC.setName("cmbInfusSetCVC");
        cmbInfusSetCVC.setBounds(105, 250, 60, 23);
        panelGlassBundleCVC.add(cmbInfusSetCVC);

        jLabelTreeWayCVC.setText("* Tree Way :");
        jLabelTreeWayCVC.setName("jLabelTreeWayCVC");
        panelGlassBundleCVC.add(jLabelTreeWayCVC);
        jLabelTreeWayCVC.setBounds(170, 250, 80, 23);
        cmbTreeWayCVC.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbTreeWayCVC.setName("cmbTreeWayCVC");
        cmbTreeWayCVC.setBounds(255, 250, 60, 23);
        panelGlassBundleCVC.add(cmbTreeWayCVC);

        jLabelValveKonektorCVC.setText("* Valve Konektor :");
        jLabelValveKonektorCVC.setName("jLabelValveKonektorCVC");
        panelGlassBundleCVC.add(jLabelValveKonektorCVC);
        jLabelValveKonektorCVC.setBounds(320, 250, 120, 23);
        cmbValveKonektorCVC.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbValveKonektorCVC.setName("cmbValveKonektorCVC");
        cmbValveKonektorCVC.setBounds(445, 250, 60, 23);
        panelGlassBundleCVC.add(cmbValveKonektorCVC);

        // Petugas dipindah ke panel atas (NIPHeader)
        jLabelPetugasCVC.setVisible(false);
        NIPBundleCVC.setVisible(false);
        NamaPetugasBundleCVC.setVisible(false);
        btnPetugasBundleCVC.setVisible(false);

        scrollPaneBundleCVC.setViewportView(panelGlassBundleCVC);
        PanelInputCVC.add(scrollPaneBundleCVC, java.awt.BorderLayout.PAGE_START);
        internalFrameCVC.add(PanelInputCVC, java.awt.BorderLayout.PAGE_START);
        TabBundleCare.addTab("BUNDLE CVC/IV CATHETER/PICC", internalFrameCVC);

        // Sub Tab 3: Bundle ISK (replacing Bundle General)
        internalFrameISK.setBorder(null);
        internalFrameISK.setName("internalFrameISK");
        internalFrameISK.setLayout(new java.awt.BorderLayout(1, 1));

        ScrollISK.setName("ScrollISK");
        tbBundleISK.setName("tbBundleISK");
        tbBundleISK.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbBundleISKMouseClicked(evt);
            }
        });
        ScrollISK.setViewportView(tbBundleISK);
        internalFrameISK.add(ScrollISK, java.awt.BorderLayout.CENTER);

        PanelInputISK.setName("PanelInputISK");
        PanelInputISK.setOpaque(false);
        PanelInputISK.setPreferredSize(new java.awt.Dimension(192, 280));
        PanelInputISK.setLayout(new java.awt.BorderLayout(1, 1));
        ChkInputISK.setText(".: Input Bundle ISK");
        ChkInputISK.setBorderPainted(true);
        ChkInputISK.setBorderPaintedFlat(true);
        ChkInputISK.setFocusable(false);
        ChkInputISK.setName("ChkInputISK");
        ChkInputISK.setPreferredSize(new java.awt.Dimension(192, 20));
        PanelInputISK.add(ChkInputISK, java.awt.BorderLayout.PAGE_END);

        scrollPaneBundleISK.setName("scrollPaneBundleISK");
        panelGlassBundleISK.setName("panelGlassBundleISK");
        panelGlassBundleISK.setPreferredSize(new java.awt.Dimension(44, 260));
        panelGlassBundleISK.setLayout(null);

        // Bundle ISK detailed content
        // Row 1: Pemasangan Sesuai Indikasi
        jLabelPemasanganSesuaiISK.setText("Pemasangan Sesuai Indikasi :");
        jLabelPemasanganSesuaiISK.setName("jLabelPemasanganSesuaiISK");
        panelGlassBundleISK.add(jLabelPemasanganSesuaiISK);
        jLabelPemasanganSesuaiISK.setBounds(0, 10, 180, 23);
        cmbPemasanganSesuaiISK.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbPemasanganSesuaiISK.setName("cmbPemasanganSesuaiISK");
        cmbPemasanganSesuaiISK.setBounds(185, 10, 60, 23);
        panelGlassBundleISK.add(cmbPemasanganSesuaiISK);

        // Row 2: Teknik Aseptik dan APT yang tepat
        jLabelTeknikAseptikISK.setText("Teknik Aseptik dan APT yang tepat :");
        jLabelTeknikAseptikISK.setName("jLabelTeknikAseptikISK");
        panelGlassBundleISK.add(jLabelTeknikAseptikISK);
        jLabelTeknikAseptikISK.setBounds(0, 40, 220, 23);
        cmbTeknikAseptikISK.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbTeknikAseptikISK.setName("cmbTeknikAseptikISK");
        cmbTeknikAseptikISK.setBounds(225, 40, 60, 23);
        panelGlassBundleISK.add(cmbTeknikAseptikISK);

        // Row 3: Apakah kateter masih dibutuhkan
        jLabelKateterDibutuhkanISK.setText("Apakah kateter masih dibutuhkan :");
        jLabelKateterDibutuhkanISK.setName("jLabelKateterDibutuhkanISK");
        panelGlassBundleISK.add(jLabelKateterDibutuhkanISK);
        jLabelKateterDibutuhkanISK.setBounds(0, 70, 220, 23);
        cmbKateterDibutuhkanISK.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbKateterDibutuhkanISK.setName("cmbKateterDibutuhkanISK");
        cmbKateterDibutuhkanISK.setBounds(225, 70, 60, 23);
        panelGlassBundleISK.add(cmbKateterDibutuhkanISK);

        // Row 4: Posisi Urin bag lebih rendah dari bladder dan tidak menyentuh tanah
        jLabelPosisiUrinBagISK.setText("Posisi Urin bag lebih rendah dari bladder");
        jLabelPosisiUrinBagISK.setName("jLabelPosisiUrinBagISK");
        panelGlassBundleISK.add(jLabelPosisiUrinBagISK);
        jLabelPosisiUrinBagISK.setBounds(0, 100, 250, 23);
        javax.swing.JLabel jLabelTidakMenyentuhTanah = new javax.swing.JLabel("dan tidak menyentuh tanah :");
        jLabelTidakMenyentuhTanah.setBounds(0, 120, 180, 23);
        panelGlassBundleISK.add(jLabelTidakMenyentuhTanah);
        cmbPosisiUrinBagISK.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbPosisiUrinBagISK.setName("cmbPosisiUrinBagISK");
        cmbPosisiUrinBagISK.setBounds(185, 120, 60, 23);
        panelGlassBundleISK.add(cmbPosisiUrinBagISK);

        // Row 5: Hand Hygiene dan gunakan glove saat mengganti kateter
        jLabelHandHygieneISK.setText("Hand Hygiene dan gunakan glove");
        jLabelHandHygieneISK.setName("jLabelHandHygieneISK");
        panelGlassBundleISK.add(jLabelHandHygieneISK);
        jLabelHandHygieneISK.setBounds(0, 150, 200, 23);
        javax.swing.JLabel jLabelSaatMengganti = new javax.swing.JLabel("saat mengganti kateter :");
        jLabelSaatMengganti.setBounds(0, 170, 140, 23);
        panelGlassBundleISK.add(jLabelSaatMengganti);
        cmbHandHygieneISK.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbHandHygieneISK.setName("cmbHandHygieneISK");
        cmbHandHygieneISK.setBounds(145, 170, 60, 23);
        panelGlassBundleISK.add(cmbHandHygieneISK);

        // Row 6: Meatal Care secara rutin
        jLabelMeatalCareISK.setText("Meatal Care secara rutin :");
        jLabelMeatalCareISK.setName("jLabelMeatalCareISK");
        panelGlassBundleISK.add(jLabelMeatalCareISK);
        jLabelMeatalCareISK.setBounds(250, 150, 160, 23);
        cmbMeatalCareISK.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbMeatalCareISK.setName("cmbMeatalCareISK");
        cmbMeatalCareISK.setBounds(415, 150, 60, 23);
        panelGlassBundleISK.add(cmbMeatalCareISK);

        // Row 7: Fiksasi kateter
        jLabelFiksasiKateterISK.setText("Fiksasi kateter :");
        jLabelFiksasiKateterISK.setName("jLabelFiksasiKateterISK");
        panelGlassBundleISK.add(jLabelFiksasiKateterISK);
        jLabelFiksasiKateterISK.setBounds(250, 180, 120, 23);
        cmbFiksasiKateterISK.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbFiksasiKateterISK.setName("cmbFiksasiKateterISK");
        cmbFiksasiKateterISK.setBounds(375, 180, 60, 23);
        panelGlassBundleISK.add(cmbFiksasiKateterISK);

        // Row 8: Kosongkan urin bag < 2/3 penuh
        jLabelKosongkanUrinBagISK.setText("Kosongkan urin bag < 2/3 penuh :");
        jLabelKosongkanUrinBagISK.setName("jLabelKosongkanUrinBagISK");
        panelGlassBundleISK.add(jLabelKosongkanUrinBagISK);
        jLabelKosongkanUrinBagISK.setBounds(0, 200, 200, 23);
        cmbKosongkanUrinBagISK.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbKosongkanUrinBagISK.setName("cmbKosongkanUrinBagISK");
        cmbKosongkanUrinBagISK.setBounds(205, 200, 60, 23);
        panelGlassBundleISK.add(cmbKosongkanUrinBagISK);

        // Row 9: Closed System
        jLabelClosedSystemISK.setText("Closed System :");
        jLabelClosedSystemISK.setName("jLabelClosedSystemISK");
        panelGlassBundleISK.add(jLabelClosedSystemISK);
        jLabelClosedSystemISK.setBounds(270, 200, 100, 23);
        cmbClosedSystemISK.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak"}));
        cmbClosedSystemISK.setName("cmbClosedSystemISK");
        cmbClosedSystemISK.setBounds(375, 200, 60, 23);
        panelGlassBundleISK.add(cmbClosedSystemISK);

        // Petugas dipindah ke panel atas (NIPHeader)
        jLabelPetugasISK.setVisible(false);
        NIPBundleISK.setVisible(false);
        NamaPetugasBundleISK.setVisible(false);
        btnPetugasBundleISK.setVisible(false);

        scrollPaneBundleISK.setViewportView(panelGlassBundleISK);
        PanelInputISK.add(scrollPaneBundleISK, java.awt.BorderLayout.PAGE_START);
        internalFrameISK.add(PanelInputISK, java.awt.BorderLayout.PAGE_START);
        TabBundleCare.addTab("BUNDLE ISK", internalFrameISK);

        internalFrameBundleCare.add(TabBundleCare, java.awt.BorderLayout.CENTER);
        // Tab BUNDLE CARE dihilangkan dari UI (sesuai permintaan user).
        // Komponen + tabel DB (observasi_icu_bundle_vap/cvc/isk) tetap dijaga
        // untuk back-compat. Untuk munculin lagi: uncomment baris addTab di bawah.
        // TabICU.addTab("BUNDLE CARE", internalFrameBundleCare);

        // Setup Tab Pemeriksaan Penunjang
        internalFramePemeriksaanPenunjang.setBorder(null);
        internalFramePemeriksaanPenunjang.setName("internalFramePemeriksaanPenunjang");
        internalFramePemeriksaanPenunjang.setLayout(new java.awt.BorderLayout(1, 1));

        ScrollPemeriksaanPenunjang.setName("ScrollPemeriksaanPenunjang");
        tbPemeriksaanPenunjang.setName("tbPemeriksaanPenunjang");
        tbPemeriksaanPenunjang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbPemeriksaanPenunjangMouseClicked(evt);
            }
        });
        ScrollPemeriksaanPenunjang.setViewportView(tbPemeriksaanPenunjang);
        internalFramePemeriksaanPenunjang.add(ScrollPemeriksaanPenunjang, java.awt.BorderLayout.CENTER);

        PanelInputPemeriksaanPenunjang.setName("PanelInputPemeriksaanPenunjang");
        PanelInputPemeriksaanPenunjang.setOpaque(false);
        PanelInputPemeriksaanPenunjang.setPreferredSize(new java.awt.Dimension(192, 140));
        PanelInputPemeriksaanPenunjang.setLayout(new java.awt.BorderLayout(1, 1));
        ChkInputPemeriksaanPenunjang.setText(".: Input Pemeriksaan Penunjang");
        ChkInputPemeriksaanPenunjang.setBorderPainted(true);
        ChkInputPemeriksaanPenunjang.setBorderPaintedFlat(true);
        ChkInputPemeriksaanPenunjang.setFocusable(false);
        ChkInputPemeriksaanPenunjang.setName("ChkInputPemeriksaanPenunjang");
        ChkInputPemeriksaanPenunjang.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInputPemeriksaanPenunjang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputPemeriksaanPenunjangActionPerformed(evt);
            }
        });
        PanelInputPemeriksaanPenunjang.add(ChkInputPemeriksaanPenunjang, java.awt.BorderLayout.PAGE_END);

        scrollPanePemeriksaanPenunjang.setName("scrollPanePemeriksaanPenunjang");
        panelGlassPemeriksaanPenunjang.setName("panelGlassPemeriksaanPenunjang");
        panelGlassPemeriksaanPenunjang.setPreferredSize(new java.awt.Dimension(44, 120));
        panelGlassPemeriksaanPenunjang.setLayout(null);

        // Pemeriksaan Penunjang content
        // Row 1: Nama Pemeriksaan
        jLabelNamaPemeriksaan.setText("Nama Pemeriksaan :");
        jLabelNamaPemeriksaan.setName("jLabelNamaPemeriksaan");
        panelGlassPemeriksaanPenunjang.add(jLabelNamaPemeriksaan);
        jLabelNamaPemeriksaan.setBounds(0, 10, 130, 23);
        TNamaPemeriksaan.setName("TNamaPemeriksaan");
        panelGlassPemeriksaanPenunjang.add(TNamaPemeriksaan);
        TNamaPemeriksaan.setBounds(135, 10, 300, 23);

        // Row 2: Hasil
        jLabelHasilPemeriksaan.setText("Hasil :");
        jLabelHasilPemeriksaan.setName("jLabelHasilPemeriksaan");
        panelGlassPemeriksaanPenunjang.add(jLabelHasilPemeriksaan);
        jLabelHasilPemeriksaan.setBounds(0, 40, 50, 23);
        scrollPaneHasilPemeriksaan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPaneHasilPemeriksaan.setName("scrollPaneHasilPemeriksaan");
        THasilPemeriksaan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        THasilPemeriksaan.setColumns(20);
        THasilPemeriksaan.setRows(3);
        THasilPemeriksaan.setName("THasilPemeriksaan");
        scrollPaneHasilPemeriksaan.setViewportView(THasilPemeriksaan);
        panelGlassPemeriksaanPenunjang.add(scrollPaneHasilPemeriksaan);
        scrollPaneHasilPemeriksaan.setBounds(55, 40, 400, 50);

        // Petugas dipindah ke panel atas (NIPHeader)
        jLabelPetugasPemeriksaan.setVisible(false);
        NIPPemeriksaanPenunjang.setVisible(false);
        NamaPetugasPemeriksaanPenunjang.setVisible(false);
        btnPetugasPemeriksaanPenunjang.setVisible(false);

        scrollPanePemeriksaanPenunjang.setViewportView(panelGlassPemeriksaanPenunjang);
        PanelInputPemeriksaanPenunjang.add(scrollPanePemeriksaanPenunjang, java.awt.BorderLayout.PAGE_START);
        internalFramePemeriksaanPenunjang.add(PanelInputPemeriksaanPenunjang, java.awt.BorderLayout.PAGE_START);
        // Tab "Pemeriksaan Penunjang" dihilangkan (permintaan user). Tabel DB tetap.
        // TabICU.addTab("Pemeriksaan Penunjang", internalFramePemeriksaanPenunjang);

        // Setup Tab Gambar (added before Catatan Terintegrasi)
        internalFrameGambar.setBorder(null);
        internalFrameGambar.setName("internalFrameGambar");
        internalFrameGambar.setLayout(new java.awt.BorderLayout(1, 1));

        ScrollGambar.setName("ScrollGambar");
        tbGambar.setName("tbGambar");
        tbGambar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbGambarMouseClicked(evt);
            }
        });
        ScrollGambar.setViewportView(tbGambar);
        internalFrameGambar.add(ScrollGambar, java.awt.BorderLayout.CENTER);

        PanelInputGambar.setName("PanelInputGambar");
        PanelInputGambar.setOpaque(false);
        PanelInputGambar.setPreferredSize(new java.awt.Dimension(192, 320));
        PanelInputGambar.setLayout(new java.awt.BorderLayout(1, 1));
        ChkInputGambar.setText(".: Input Kebutuhan Nutrisi dan Cairan");
        ChkInputGambar.setBorderPainted(true);
        ChkInputGambar.setBorderPaintedFlat(true);
        ChkInputGambar.setFocusable(false);
        ChkInputGambar.setName("ChkInputGambar");
        ChkInputGambar.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInputGambar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputGambarActionPerformed(evt);
            }
        });
        PanelInputGambar.add(ChkInputGambar, java.awt.BorderLayout.PAGE_END);

        scrollPaneGambar.setName("scrollPaneGambar");
        panelGlassGambar.setName("panelGlassGambar");
        panelGlassGambar.setPreferredSize(new java.awt.Dimension(44, 300));
        panelGlassGambar.setLayout(null);

        // Tab Kebutuhan Nutrisi dan Cairan content
        // Row 1: BB (Berat Badan), TB (Tinggi Badan), BMI
        javax.swing.JLabel jLabelBB = new javax.swing.JLabel("BB :");
        jLabelBB.setName("jLabelBB");
        panelGlassGambar.add(jLabelBB);
        jLabelBB.setBounds(0, 10, 30, 23);
        TBB = new widget.TextBox();
        TBB.setName("TBB");
        panelGlassGambar.add(TBB);
        TBB.setBounds(35, 10, 60, 23);
        javax.swing.JLabel jLabelKg = new javax.swing.JLabel("kg");
        jLabelKg.setBounds(100, 10, 20, 23);
        panelGlassGambar.add(jLabelKg);

        javax.swing.JLabel jLabelTB = new javax.swing.JLabel("TB :");
        jLabelTB.setName("jLabelTB");
        panelGlassGambar.add(jLabelTB);
        jLabelTB.setBounds(130, 10, 30, 23);
        TTB = new widget.TextBox();
        TTB.setName("TTB");
        panelGlassGambar.add(TTB);
        TTB.setBounds(165, 10, 60, 23);
        javax.swing.JLabel jLabelCm = new javax.swing.JLabel("cm");
        jLabelCm.setBounds(230, 10, 20, 23);
        panelGlassGambar.add(jLabelCm);

        javax.swing.JLabel jLabelBMI = new javax.swing.JLabel("BMI :");
        jLabelBMI.setName("jLabelBMI");
        panelGlassGambar.add(jLabelBMI);
        jLabelBMI.setBounds(260, 10, 35, 23);
        TBMI = new widget.TextBox();
        TBMI.setName("TBMI");
        TBMI.setEditable(false);
        panelGlassGambar.add(TBMI);
        TBMI.setBounds(300, 10, 80, 23);

        // Row 2: Nutrisi Section
        javax.swing.JLabel jLabelNutrisi = new javax.swing.JLabel("Nutrisi :");
        jLabelNutrisi.setName("jLabelNutrisi");
        jLabelNutrisi.setFont(new java.awt.Font("Tahoma", 1, 11));
        panelGlassGambar.add(jLabelNutrisi);
        jLabelNutrisi.setBounds(0, 40, 60, 23);

        // Row 3: Kebutuhan
        javax.swing.JLabel jLabelKebutuhan = new javax.swing.JLabel("- Kebutuhan :");
        jLabelKebutuhan.setName("jLabelKebutuhan");
        panelGlassGambar.add(jLabelKebutuhan);
        jLabelKebutuhan.setBounds(20, 70, 80, 23);
        TKebutuhanKkal = new widget.TextBox();
        TKebutuhanKkal.setName("TKebutuhanKkal");
        panelGlassGambar.add(TKebutuhanKkal);
        TKebutuhanKkal.setBounds(105, 70, 60, 23);
        javax.swing.JLabel jLabelKkalKgBB = new javax.swing.JLabel("Kkal/KgBB/24 jam =");
        jLabelKkalKgBB.setBounds(170, 70, 110, 23);
        panelGlassGambar.add(jLabelKkalKgBB);
        TKebutuhanKkal24 = new widget.TextBox();
        TKebutuhanKkal24.setName("TKebutuhanKkal24");
        TKebutuhanKkal24.setEditable(false);
        panelGlassGambar.add(TKebutuhanKkal24);
        TKebutuhanKkal24.setBounds(285, 70, 60, 23);
        javax.swing.JLabel jLabelKkal = new javax.swing.JLabel("kkal");
        jLabelKkal.setBounds(350, 70, 30, 23);
        panelGlassGambar.add(jLabelKkal);

        // Row 4: Kebutuhan dasar
        javax.swing.JLabel jLabelKebutuhanDasar = new javax.swing.JLabel("- Kebutuhan dasar :");
        jLabelKebutuhanDasar.setName("jLabelKebutuhanDasar");
        panelGlassGambar.add(jLabelKebutuhanDasar);
        jLabelKebutuhanDasar.setBounds(400, 40, 120, 23);
        TKebutuhanDasar = new widget.TextBox();
        TKebutuhanDasar.setName("TKebutuhanDasar");
        panelGlassGambar.add(TKebutuhanDasar);
        TKebutuhanDasar.setBounds(525, 40, 80, 23);
        javax.swing.JLabel jLabelCcKgBB24 = new javax.swing.JLabel("cc/KgBB/24 jam");
        jLabelCcKgBB24.setBounds(610, 40, 90, 23);
        panelGlassGambar.add(jLabelCcKgBB24);

        // Row 5: Pemberian
        javax.swing.JLabel jLabelPemberian = new javax.swing.JLabel("- Pemberian :");
        jLabelPemberian.setName("jLabelPemberian");
        panelGlassGambar.add(jLabelPemberian);
        jLabelPemberian.setBounds(20, 100, 80, 23);
        TPersentasePemberian = new widget.TextBox();
        TPersentasePemberian.setName("TPersentasePemberian");
        panelGlassGambar.add(TPersentasePemberian);
        TPersentasePemberian.setBounds(105, 100, 40, 23);
        javax.swing.JLabel jLabelPersen = new javax.swing.JLabel("% =");
        jLabelPersen.setBounds(150, 100, 25, 23);
        panelGlassGambar.add(jLabelPersen);
        TPemberianKkal = new widget.TextBox();
        TPemberianKkal.setName("TPemberianKkal");
        TPemberianKkal.setEditable(false);
        panelGlassGambar.add(TPemberianKkal);
        TPemberianKkal.setBounds(180, 100, 60, 23);
        javax.swing.JLabel jLabelKkal2 = new javax.swing.JLabel("kkal");
        jLabelKkal2.setBounds(245, 100, 30, 23);
        panelGlassGambar.add(jLabelKkal2);

        // Row 6: IWL
        javax.swing.JLabel jLabelIWL = new javax.swing.JLabel("- IWL : 10 cc/KgBB/24 jam =");
        jLabelIWL.setName("jLabelIWL");
        panelGlassGambar.add(jLabelIWL);
        jLabelIWL.setBounds(400, 70, 150, 23);
        TIWL = new widget.TextBox();
        TIWL.setName("TIWL");
        TIWL.setEditable(false);
        panelGlassGambar.add(TIWL);
        TIWL.setBounds(555, 70, 60, 23);
        javax.swing.JLabel jLabelCc24jam = new javax.swing.JLabel("cc/24 jam");
        jLabelCc24jam.setBounds(620, 70, 70, 23);
        panelGlassGambar.add(jLabelCc24jam);

        // Row 7: Komposisi
        javax.swing.JLabel jLabelKomposisi = new javax.swing.JLabel("Komposisi :");
        jLabelKomposisi.setName("jLabelKomposisi");
        jLabelKomposisi.setFont(new java.awt.Font("Tahoma", 1, 11));
        panelGlassGambar.add(jLabelKomposisi);
        jLabelKomposisi.setBounds(20, 130, 80, 23);

        // Row 8: Protein
        javax.swing.JLabel jLabelProtein = new javax.swing.JLabel("• Protein :");
        jLabelProtein.setName("jLabelProtein");
        panelGlassGambar.add(jLabelProtein);
        jLabelProtein.setBounds(40, 160, 60, 23);
        TProtein = new widget.TextBox();
        TProtein.setName("TProtein");
        panelGlassGambar.add(TProtein);
        TProtein.setBounds(105, 160, 50, 23);
        javax.swing.JLabel jLabelGrKgBBHari = new javax.swing.JLabel("gr/KgBB/hari");
        jLabelGrKgBBHari.setBounds(160, 160, 80, 23);
        panelGlassGambar.add(jLabelGrKgBBHari);
        javax.swing.JLabel jLabelProteinSama = new javax.swing.JLabel("=");
        jLabelProteinSama.setBounds(245, 160, 10, 23);
        panelGlassGambar.add(jLabelProteinSama);
        TProteinTotal = new widget.TextBox();
        TProteinTotal.setName("TProteinTotal");
        TProteinTotal.setEditable(false);
        panelGlassGambar.add(TProteinTotal);
        TProteinTotal.setBounds(260, 160, 50, 23);
        javax.swing.JLabel jLabelGram = new javax.swing.JLabel("gram");
        jLabelGram.setBounds(315, 160, 40, 23);
        panelGlassGambar.add(jLabelGram);

        // Row 9: Obat-obatan
        javax.swing.JLabel jLabelObatObatan = new javax.swing.JLabel("- Obat-obatan =");
        jLabelObatObatan.setName("jLabelObatObatan");
        panelGlassGambar.add(jLabelObatObatan);
        jLabelObatObatan.setBounds(400, 100, 100, 23);
        TObatObatan = new widget.TextBox();
        TObatObatan.setName("TObatObatan");
        panelGlassGambar.add(TObatObatan);
        TObatObatan.setBounds(505, 100, 80, 23);
        javax.swing.JLabel jLabelCc24jam2 = new javax.swing.JLabel("cc/24 jam");
        jLabelCc24jam2.setBounds(590, 100, 70, 23);
        panelGlassGambar.add(jLabelCc24jam2);

        // Row 10: Kalori non protein
        javax.swing.JLabel jLabelKaloriNonProtein = new javax.swing.JLabel("• Kalori non protein :");
        jLabelKaloriNonProtein.setName("jLabelKaloriNonProtein");
        panelGlassGambar.add(jLabelKaloriNonProtein);
        jLabelKaloriNonProtein.setBounds(40, 190, 120, 23);
        TKaloriNonProtein = new widget.TextBox();
        TKaloriNonProtein.setName("TKaloriNonProtein");
        TKaloriNonProtein.setEditable(false);
        panelGlassGambar.add(TKaloriNonProtein);
        TKaloriNonProtein.setBounds(165, 190, 60, 23);
        javax.swing.JLabel jLabelKkal3 = new javax.swing.JLabel("kkal");
        jLabelKkal3.setBounds(230, 190, 30, 23);
        panelGlassGambar.add(jLabelKkal3);

        // Row 11: Cairan total
        javax.swing.JLabel jLabelCairanTotal = new javax.swing.JLabel("- Cairan total =");
        jLabelCairanTotal.setName("jLabelCairanTotal");
        panelGlassGambar.add(jLabelCairanTotal);
        jLabelCairanTotal.setBounds(400, 130, 100, 23);
        TCairanTotal = new widget.TextBox();
        TCairanTotal.setName("TCairanTotal");
        TCairanTotal.setEditable(false);
        panelGlassGambar.add(TCairanTotal);
        TCairanTotal.setBounds(505, 130, 80, 23);
        javax.swing.JLabel jLabelCc24jam3 = new javax.swing.JLabel("cc/24 jam");
        jLabelCc24jam3.setBounds(590, 130, 70, 23);
        panelGlassGambar.add(jLabelCc24jam3);

        // Row 12: Rasio KH : Lemak
        javax.swing.JLabel jLabelRasioKH = new javax.swing.JLabel("• Rasio KH : Lemak =");
        jLabelRasioKH.setName("jLabelRasioKH");
        panelGlassGambar.add(jLabelRasioKH);
        jLabelRasioKH.setBounds(40, 220, 120, 23);
        TRasioKH = new widget.TextBox();
        TRasioKH.setName("TRasioKH");
        panelGlassGambar.add(TRasioKH);
        TRasioKH.setBounds(165, 220, 40, 23);
        javax.swing.JLabel jLabelPersen2 = new javax.swing.JLabel("% :");
        jLabelPersen2.setBounds(210, 220, 20, 23);
        panelGlassGambar.add(jLabelPersen2);
        TRasioLemak = new widget.TextBox();
        TRasioLemak.setName("TRasioLemak");
        panelGlassGambar.add(TRasioLemak);
        TRasioLemak.setBounds(235, 220, 40, 23);
        javax.swing.JLabel jLabelPersen3 = new javax.swing.JLabel("%");
        jLabelPersen3.setBounds(280, 220, 15, 23);
        panelGlassGambar.add(jLabelPersen3);
        javax.swing.JLabel jLabelSama2 = new javax.swing.JLabel("=");
        jLabelSama2.setBounds(300, 220, 10, 23);
        panelGlassGambar.add(jLabelSama2);
        TRasioTotal = new widget.TextBox();
        TRasioTotal.setName("TRasioTotal");
        TRasioTotal.setEditable(false);
        panelGlassGambar.add(TRasioTotal);
        TRasioTotal.setBounds(315, 220, 50, 23);
        javax.swing.JLabel jLabelKkal4 = new javax.swing.JLabel("Kkal :");
        jLabelKkal4.setBounds(370, 220, 35, 23);
        panelGlassGambar.add(jLabelKkal4);
        TRasioKkal = new widget.TextBox();
        TRasioKkal.setName("TRasioKkal");
        TRasioKkal.setEditable(false);
        panelGlassGambar.add(TRasioKkal);
        TRasioKkal.setBounds(410, 220, 50, 23);
        javax.swing.JLabel jLabelKkal5 = new javax.swing.JLabel("Kkal");
        jLabelKkal5.setBounds(465, 220, 30, 23);
        panelGlassGambar.add(jLabelKkal5);

        // Row 13: BALANCE
        javax.swing.JLabel jLabelBalance = new javax.swing.JLabel("BALANCE :");
        jLabelBalance.setName("jLabelBalance");
        jLabelBalance.setFont(new java.awt.Font("Tahoma", 1, 11));
        panelGlassGambar.add(jLabelBalance);
        jLabelBalance.setBounds(400, 160, 80, 23);
        TBalance = new widget.TextBox();
        TBalance.setName("TBalance");
        panelGlassGambar.add(TBalance);
        TBalance.setBounds(485, 160, 80, 23);
        javax.swing.JLabel jLabelCc24jam4 = new javax.swing.JLabel("cc / 24 jam");
        jLabelCc24jam4.setBounds(570, 160, 70, 23);
        panelGlassGambar.add(jLabelCc24jam4);

        javax.swing.JLabel jLabelPerhatianKebutuhan = new javax.swing.JLabel("(perhatian kebutuhan/kehilangan cairan)");
        jLabelPerhatianKebutuhan.setName("jLabelPerhatianKebutuhan");
        panelGlassGambar.add(jLabelPerhatianKebutuhan);
        jLabelPerhatianKebutuhan.setBounds(400, 185, 250, 23);

        // Petugas dipindah ke panel atas (NIPHeader)
        jLabelPetugasGambar.setVisible(false);
        NIPGambar.setVisible(false);
        NamaPetugasGambar.setVisible(false);
        btnPetugasGambar.setVisible(false);

        scrollPaneGambar.setViewportView(panelGlassGambar);
        PanelInputGambar.add(scrollPaneGambar, java.awt.BorderLayout.PAGE_START);
        internalFrameGambar.add(PanelInputGambar, java.awt.BorderLayout.PAGE_START);
        // Tab "Kebutuhan Nutrisi dan Cairan" dihilangkan dari UI (permintaan user).
        // Tabel DB observasi_icu_nutrisi_cairan tetap dijaga.
        // TabICU.addTab("Kebutuhan Nutrisi dan Cairan", internalFrameGambar);

        // Setup Tab Cairan dan Nutrisi (after Kebutuhan Nutrisi dan Cairan)
        internalFrameCairanNutrisi.setBorder(null);
        internalFrameCairanNutrisi.setName("internalFrameCairanNutrisi");
        internalFrameCairanNutrisi.setLayout(new java.awt.BorderLayout(1, 1));

        ScrollCairanNutrisi.setName("ScrollCairanNutrisi");
        tbCairanNutrisi.setName("tbCairanNutrisi");
        tbCairanNutrisi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbCairanNutrisiMouseClicked(evt);
            }
        });
        ScrollCairanNutrisi.setViewportView(tbCairanNutrisi);
        internalFrameCairanNutrisi.add(ScrollCairanNutrisi, java.awt.BorderLayout.CENTER);

        PanelInputCairanNutrisi.setName("PanelInputCairanNutrisi");
        PanelInputCairanNutrisi.setOpaque(false);
        PanelInputCairanNutrisi.setPreferredSize(new java.awt.Dimension(192, 400));
        PanelInputCairanNutrisi.setLayout(new java.awt.BorderLayout(1, 1));
        ChkInputCairanNutrisi.setText(".: Input Cairan dan Nutrisi");
        ChkInputCairanNutrisi.setBorderPainted(true);
        ChkInputCairanNutrisi.setBorderPaintedFlat(true);
        ChkInputCairanNutrisi.setFocusable(false);
        ChkInputCairanNutrisi.setName("ChkInputCairanNutrisi");
        ChkInputCairanNutrisi.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInputCairanNutrisi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputCairanNutrisiActionPerformed(evt);
            }
        });
        PanelInputCairanNutrisi.add(ChkInputCairanNutrisi, java.awt.BorderLayout.PAGE_END);

        scrollPaneCairanNutrisi.setName("scrollPaneCairanNutrisi");
        panelGlassCairanNutrisi.setName("panelGlassCairanNutrisi");
        panelGlassCairanNutrisi.setPreferredSize(new java.awt.Dimension(44, 360));
        panelGlassCairanNutrisi.setLayout(null);

        // Tab Cairan dan Nutrisi content
        // Row 1: Masalah (Multiple checkboxes)
        javax.swing.JLabel jLabelMasalah = new javax.swing.JLabel("Masalah :");
        jLabelMasalah.setName("jLabelMasalah");
        jLabelMasalah.setFont(new java.awt.Font("Tahoma", 1, 11));
        panelGlassCairanNutrisi.add(jLabelMasalah);
        jLabelMasalah.setBounds(0, 10, 70, 23);

        ChkSSP = new widget.CekBox();
        ChkSSP.setText("SSP");
        ChkSSP.setName("ChkSSP");
        panelGlassCairanNutrisi.add(ChkSSP);
        ChkSSP.setBounds(80, 10, 60, 23);

        ChkJantung = new widget.CekBox();
        ChkJantung.setText("Jantung");
        ChkJantung.setName("ChkJantung");
        panelGlassCairanNutrisi.add(ChkJantung);
        ChkJantung.setBounds(150, 10, 80, 23);

        ChkRespirasi = new widget.CekBox();
        ChkRespirasi.setText("Respirasi");
        ChkRespirasi.setName("ChkRespirasi");
        panelGlassCairanNutrisi.add(ChkRespirasi);
        ChkRespirasi.setBounds(240, 10, 90, 23);

        ChkRenal = new widget.CekBox();
        ChkRenal.setText("Renal");
        ChkRenal.setName("ChkRenal");
        panelGlassCairanNutrisi.add(ChkRenal);
        ChkRenal.setBounds(340, 10, 70, 23);

        ChkSaluranPencernaan = new widget.CekBox();
        ChkSaluranPencernaan.setText("Saluran Pencernaan");
        ChkSaluranPencernaan.setName("ChkSaluranPencernaan");
        panelGlassCairanNutrisi.add(ChkSaluranPencernaan);
        ChkSaluranPencernaan.setBounds(420, 10, 150, 23);

        ChkHepar = new widget.CekBox();
        ChkHepar.setText("Hepar");
        ChkHepar.setName("ChkHepar");
        panelGlassCairanNutrisi.add(ChkHepar);
        ChkHepar.setBounds(580, 10, 70, 23);

        ChkMetabolisme = new widget.CekBox();
        ChkMetabolisme.setText("Metabolisme");
        ChkMetabolisme.setName("ChkMetabolisme");
        panelGlassCairanNutrisi.add(ChkMetabolisme);
        ChkMetabolisme.setBounds(660, 10, 100, 23);

        // Row 2: Rate Nutrisi
        javax.swing.JLabel jLabelRateNutrisi = new javax.swing.JLabel("Rate Nutrisi :");
        jLabelRateNutrisi.setName("jLabelRateNutrisi");
        jLabelRateNutrisi.setFont(new java.awt.Font("Tahoma", 1, 11));
        panelGlassCairanNutrisi.add(jLabelRateNutrisi);
        jLabelRateNutrisi.setBounds(0, 40, 90, 23);

        ChkOral = new widget.CekBox();
        ChkOral.setText("Oral");
        ChkOral.setName("ChkOral");
        panelGlassCairanNutrisi.add(ChkOral);
        ChkOral.setBounds(100, 40, 60, 23);

        ChkEnteral = new widget.CekBox();
        ChkEnteral.setText("Enteral");
        ChkEnteral.setName("ChkEnteral");
        panelGlassCairanNutrisi.add(ChkEnteral);
        ChkEnteral.setBounds(170, 40, 80, 23);

        ChkParenteral = new widget.CekBox();
        ChkParenteral.setText("Parenteral");
        ChkParenteral.setName("ChkParenteral");
        panelGlassCairanNutrisi.add(ChkParenteral);
        ChkParenteral.setBounds(260, 40, 100, 23);

        ChkKombinasi = new widget.CekBox();
        ChkKombinasi.setText("Kombinasi");
        ChkKombinasi.setName("ChkKombinasi");
        panelGlassCairanNutrisi.add(ChkKombinasi);
        ChkKombinasi.setBounds(370, 40, 100, 23);

        // Row 3: Enteral Section
        javax.swing.JLabel jLabelEnteral = new javax.swing.JLabel("Enteral");
        jLabelEnteral.setName("jLabelEnteral");
        jLabelEnteral.setFont(new java.awt.Font("Tahoma", 1, 11));
        panelGlassCairanNutrisi.add(jLabelEnteral);
        jLabelEnteral.setBounds(0, 70, 60, 23);

        // Row 4: Enteral - Jenis
        javax.swing.JLabel jLabelJenisEnteral = new javax.swing.JLabel("Jenis :");
        jLabelJenisEnteral.setName("jLabelJenisEnteral");
        panelGlassCairanNutrisi.add(jLabelJenisEnteral);
        jLabelJenisEnteral.setBounds(20, 100, 50, 23);
        TJenisEnteral = new widget.TextBox();
        TJenisEnteral.setName("TJenisEnteral");
        panelGlassCairanNutrisi.add(TJenisEnteral);
        TJenisEnteral.setBounds(75, 100, 200, 23);

        // Row 5: Enteral - Cairan, Kalori, KH, Lemak, Protein
        javax.swing.JLabel jLabelCairanEnteral = new javax.swing.JLabel("Cairan (cc) :");
        jLabelCairanEnteral.setName("jLabelCairanEnteral");
        panelGlassCairanNutrisi.add(jLabelCairanEnteral);
        jLabelCairanEnteral.setBounds(20, 130, 80, 23);
        TCairanEnteral = new widget.TextBox();
        TCairanEnteral.setName("TCairanEnteral");
        panelGlassCairanNutrisi.add(TCairanEnteral);
        TCairanEnteral.setBounds(105, 130, 80, 23);

        javax.swing.JLabel jLabelKaloriEnteral = new javax.swing.JLabel("Kalori :");
        jLabelKaloriEnteral.setName("jLabelKaloriEnteral");
        panelGlassCairanNutrisi.add(jLabelKaloriEnteral);
        jLabelKaloriEnteral.setBounds(200, 130, 50, 23);
        TKaloriEnteral = new widget.TextBox();
        TKaloriEnteral.setName("TKaloriEnteral");
        panelGlassCairanNutrisi.add(TKaloriEnteral);
        TKaloriEnteral.setBounds(255, 130, 80, 23);

        javax.swing.JLabel jLabelKHEnteral = new javax.swing.JLabel("KH (kal) :");
        jLabelKHEnteral.setName("jLabelKHEnteral");
        panelGlassCairanNutrisi.add(jLabelKHEnteral);
        jLabelKHEnteral.setBounds(350, 130, 60, 23);
        TKHEnteral = new widget.TextBox();
        TKHEnteral.setName("TKHEnteral");
        panelGlassCairanNutrisi.add(TKHEnteral);
        TKHEnteral.setBounds(415, 130, 80, 23);

        javax.swing.JLabel jLabelLemakEnteral = new javax.swing.JLabel("Lemak (kal) :");
        jLabelLemakEnteral.setName("jLabelLemakEnteral");
        panelGlassCairanNutrisi.add(jLabelLemakEnteral);
        jLabelLemakEnteral.setBounds(510, 130, 80, 23);
        TLemakEnteral = new widget.TextBox();
        TLemakEnteral.setName("TLemakEnteral");
        panelGlassCairanNutrisi.add(TLemakEnteral);
        TLemakEnteral.setBounds(595, 130, 80, 23);

        javax.swing.JLabel jLabelProteinEnteral = new javax.swing.JLabel("Protein (gr) :");
        jLabelProteinEnteral.setName("jLabelProteinEnteral");
        panelGlassCairanNutrisi.add(jLabelProteinEnteral);
        jLabelProteinEnteral.setBounds(20, 160, 80, 23);
        TProteinEnteral = new widget.TextBox();
        TProteinEnteral.setName("TProteinEnteral");
        panelGlassCairanNutrisi.add(TProteinEnteral);
        TProteinEnteral.setBounds(105, 160, 80, 23);

        // Row 6: Parenteral Section
        javax.swing.JLabel jLabelParenteral = new javax.swing.JLabel("Parenteral");
        jLabelParenteral.setName("jLabelParenteral");
        jLabelParenteral.setFont(new java.awt.Font("Tahoma", 1, 11));
        panelGlassCairanNutrisi.add(jLabelParenteral);
        jLabelParenteral.setBounds(0, 190, 80, 23);

        // Row 7: Parenteral - Jenis
        javax.swing.JLabel jLabelJenisParenteral = new javax.swing.JLabel("Jenis :");
        jLabelJenisParenteral.setName("jLabelJenisParenteral");
        panelGlassCairanNutrisi.add(jLabelJenisParenteral);
        jLabelJenisParenteral.setBounds(20, 220, 50, 23);
        TJenisParenteral = new widget.TextBox();
        TJenisParenteral.setName("TJenisParenteral");
        panelGlassCairanNutrisi.add(TJenisParenteral);
        TJenisParenteral.setBounds(75, 220, 200, 23);

        // Row 8: Parenteral - Cairan, Kalori, KH, Lemak, Protein
        javax.swing.JLabel jLabelCairanParenteral = new javax.swing.JLabel("Cairan (cc) :");
        jLabelCairanParenteral.setName("jLabelCairanParenteral");
        panelGlassCairanNutrisi.add(jLabelCairanParenteral);
        jLabelCairanParenteral.setBounds(20, 250, 80, 23);
        TCairanParenteral = new widget.TextBox();
        TCairanParenteral.setName("TCairanParenteral");
        panelGlassCairanNutrisi.add(TCairanParenteral);
        TCairanParenteral.setBounds(105, 250, 80, 23);

        javax.swing.JLabel jLabelKaloriParenteral = new javax.swing.JLabel("Kalori :");
        jLabelKaloriParenteral.setName("jLabelKaloriParenteral");
        panelGlassCairanNutrisi.add(jLabelKaloriParenteral);
        jLabelKaloriParenteral.setBounds(200, 250, 50, 23);
        TKaloriParenteral = new widget.TextBox();
        TKaloriParenteral.setName("TKaloriParenteral");
        panelGlassCairanNutrisi.add(TKaloriParenteral);
        TKaloriParenteral.setBounds(255, 250, 80, 23);

        javax.swing.JLabel jLabelKHParenteral = new javax.swing.JLabel("KH (kal) :");
        jLabelKHParenteral.setName("jLabelKHParenteral");
        panelGlassCairanNutrisi.add(jLabelKHParenteral);
        jLabelKHParenteral.setBounds(350, 250, 60, 23);
        TKHParenteral = new widget.TextBox();
        TKHParenteral.setName("TKHParenteral");
        panelGlassCairanNutrisi.add(TKHParenteral);
        TKHParenteral.setBounds(415, 250, 80, 23);

        javax.swing.JLabel jLabelLemakParenteral = new javax.swing.JLabel("Lemak (kal) :");
        jLabelLemakParenteral.setName("jLabelLemakParenteral");
        panelGlassCairanNutrisi.add(jLabelLemakParenteral);
        jLabelLemakParenteral.setBounds(510, 250, 80, 23);
        TLemakParenteral = new widget.TextBox();
        TLemakParenteral.setName("TLemakParenteral");
        panelGlassCairanNutrisi.add(TLemakParenteral);
        TLemakParenteral.setBounds(595, 250, 80, 23);

        javax.swing.JLabel jLabelProteinParenteral = new javax.swing.JLabel("Protein (gr) :");
        jLabelProteinParenteral.setName("jLabelProteinParenteral");
        panelGlassCairanNutrisi.add(jLabelProteinParenteral);
        jLabelProteinParenteral.setBounds(20, 280, 80, 23);
        TProteinParenteral = new widget.TextBox();
        TProteinParenteral.setName("TProteinParenteral");
        panelGlassCairanNutrisi.add(TProteinParenteral);
        TProteinParenteral.setBounds(105, 280, 80, 23);

        // Row 9: Total Section (calculated fields)
        javax.swing.JLabel jLabelTotal = new javax.swing.JLabel("Total");
        jLabelTotal.setName("jLabelTotal");
        jLabelTotal.setFont(new java.awt.Font("Tahoma", 1, 11));
        panelGlassCairanNutrisi.add(jLabelTotal);
        jLabelTotal.setBounds(0, 310, 50, 23);

        javax.swing.JLabel jLabelTotalCairan = new javax.swing.JLabel("Cairan (cc) :");
        jLabelTotalCairan.setName("jLabelTotalCairan");
        panelGlassCairanNutrisi.add(jLabelTotalCairan);
        jLabelTotalCairan.setBounds(60, 310, 80, 23);
        TTotalCairan = new widget.TextBox();
        TTotalCairan.setEditable(false);
        TTotalCairan.setName("TTotalCairan");
        panelGlassCairanNutrisi.add(TTotalCairan);
        TTotalCairan.setBounds(145, 310, 80, 23);

        javax.swing.JLabel jLabelTotalKalori = new javax.swing.JLabel("Kalori :");
        jLabelTotalKalori.setName("jLabelTotalKalori");
        panelGlassCairanNutrisi.add(jLabelTotalKalori);
        jLabelTotalKalori.setBounds(240, 310, 50, 23);
        TTotalKalori = new widget.TextBox();
        TTotalKalori.setEditable(false);
        TTotalKalori.setName("TTotalKalori");
        panelGlassCairanNutrisi.add(TTotalKalori);
        TTotalKalori.setBounds(295, 310, 80, 23);

        javax.swing.JLabel jLabelTotalKH = new javax.swing.JLabel("KH (kal) :");
        jLabelTotalKH.setName("jLabelTotalKH");
        panelGlassCairanNutrisi.add(jLabelTotalKH);
        jLabelTotalKH.setBounds(390, 310, 60, 23);
        TTotalKH = new widget.TextBox();
        TTotalKH.setEditable(false);
        TTotalKH.setName("TTotalKH");
        panelGlassCairanNutrisi.add(TTotalKH);
        TTotalKH.setBounds(455, 310, 80, 23);

        javax.swing.JLabel jLabelTotalLemak = new javax.swing.JLabel("Lemak (kal) :");
        jLabelTotalLemak.setName("jLabelTotalLemak");
        panelGlassCairanNutrisi.add(jLabelTotalLemak);
        jLabelTotalLemak.setBounds(550, 310, 80, 23);
        TTotalLemak = new widget.TextBox();
        TTotalLemak.setEditable(false);
        TTotalLemak.setName("TTotalLemak");
        panelGlassCairanNutrisi.add(TTotalLemak);
        TTotalLemak.setBounds(635, 310, 80, 23);

        javax.swing.JLabel jLabelTotalProtein = new javax.swing.JLabel("Protein (gr) :");
        jLabelTotalProtein.setName("jLabelTotalProtein");
        panelGlassCairanNutrisi.add(jLabelTotalProtein);
        jLabelTotalProtein.setBounds(200, 340, 80, 23);
        TTotalProtein = new widget.TextBox();
        TTotalProtein.setEditable(false);
        TTotalProtein.setName("TTotalProtein");
        panelGlassCairanNutrisi.add(TTotalProtein);
        TTotalProtein.setBounds(285, 340, 80, 23);

        // Petugas dipindah ke panel atas (NIPHeader)
        jLabelPetugasCairanNutrisi.setVisible(false);
        NIPCairanNutrisi.setVisible(false);
        NamaPetugasCairanNutrisi.setVisible(false);
        btnPetugasCairanNutrisi.setVisible(false);

        scrollPaneCairanNutrisi.setViewportView(panelGlassCairanNutrisi);
        PanelInputCairanNutrisi.add(scrollPaneCairanNutrisi, java.awt.BorderLayout.PAGE_START);
        internalFrameCairanNutrisi.add(PanelInputCairanNutrisi, java.awt.BorderLayout.PAGE_START);
        // Tab "Cairan dan Nutrisi" dihilangkan dari UI (permintaan user).
        // Tabel DB icu_cairan_nutrisi tetap dijaga.
        // TabICU.addTab("Cairan dan Nutrisi", internalFrameCairanNutrisi);

        // Setup Tab Tindakan (after Cairan dan Nutrisi)
        internalFrameTindakan.setBorder(null);
        internalFrameTindakan.setName("internalFrameTindakan");
        internalFrameTindakan.setLayout(new java.awt.BorderLayout(1, 1));

        ScrollTindakan.setName("ScrollTindakan");
        tbTindakan.setName("tbTindakan");
        tbTindakan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbTindakanMouseClicked(evt);
            }
        });
        ScrollTindakan.setViewportView(tbTindakan);
        internalFrameTindakan.add(ScrollTindakan, java.awt.BorderLayout.CENTER);

        PanelInputTindakan.setName("PanelInputTindakan");
        PanelInputTindakan.setOpaque(false);
        PanelInputTindakan.setPreferredSize(new java.awt.Dimension(192, 200));
        PanelInputTindakan.setLayout(new java.awt.BorderLayout(1, 1));
        ChkInputTindakan.setText(".: Input Tindakan");
        ChkInputTindakan.setBorderPainted(true);
        ChkInputTindakan.setBorderPaintedFlat(true);
        ChkInputTindakan.setFocusable(false);
        ChkInputTindakan.setName("ChkInputTindakan");
        ChkInputTindakan.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInputTindakan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputTindakanActionPerformed(evt);
            }
        });
        PanelInputTindakan.add(ChkInputTindakan, java.awt.BorderLayout.PAGE_END);

        scrollPaneTindakan.setName("scrollPaneTindakan");
        panelGlassTindakan.setName("panelGlassTindakan");
        panelGlassTindakan.setPreferredSize(new java.awt.Dimension(44, 180));
        panelGlassTindakan.setLayout(null);

        // Tab Tindakan content
        // Row 1: Tindakan (Multiple checkboxes)
        javax.swing.JLabel jLabelTindakan = new javax.swing.JLabel("Tindakan :");
        jLabelTindakan.setName("jLabelTindakan");
        jLabelTindakan.setFont(new java.awt.Font("Tahoma", 1, 11));
        panelGlassTindakan.add(jLabelTindakan);
        jLabelTindakan.setBounds(0, 10, 70, 23);

        ChkEndotrachealTube = new widget.CekBox();
        ChkEndotrachealTube.setText("Endotracheal Tube");
        ChkEndotrachealTube.setName("ChkEndotrachealTube");
        panelGlassTindakan.add(ChkEndotrachealTube);
        ChkEndotrachealTube.setBounds(80, 10, 130, 23);

        ChkTracheostomyTube = new widget.CekBox();
        ChkTracheostomyTube.setText("Tracheostomy Tube");
        ChkTracheostomyTube.setName("ChkTracheostomyTube");
        panelGlassTindakan.add(ChkTracheostomyTube);
        ChkTracheostomyTube.setBounds(220, 10, 140, 23);

        ChkTubingVentilator = new widget.CekBox();
        ChkTubingVentilator.setText("Tubing Ventilator");
        ChkTubingVentilator.setName("ChkTubingVentilator");
        panelGlassTindakan.add(ChkTubingVentilator);
        ChkTubingVentilator.setBounds(370, 10, 120, 23);

        ChkNasogastricTube = new widget.CekBox();
        ChkNasogastricTube.setText("Nasogastric Tube");
        ChkNasogastricTube.setName("ChkNasogastricTube");
        panelGlassTindakan.add(ChkNasogastricTube);
        ChkNasogastricTube.setBounds(500, 10, 120, 23);

        ChkCentralVenousCatheter = new widget.CekBox();
        ChkCentralVenousCatheter.setText("Central Venous Catheter");
        ChkCentralVenousCatheter.setName("ChkCentralVenousCatheter");
        panelGlassTindakan.add(ChkCentralVenousCatheter);
        ChkCentralVenousCatheter.setBounds(630, 10, 160, 23);

        // Row 2: More Tindakan checkboxes
        ChkPICC = new widget.CekBox();
        ChkPICC.setText("PICC");
        ChkPICC.setName("ChkPICC");
        panelGlassTindakan.add(ChkPICC);
        ChkPICC.setBounds(80, 40, 60, 23);

        ChkCloseSuction = new widget.CekBox();
        ChkCloseSuction.setText("Close Suction");
        ChkCloseSuction.setName("ChkCloseSuction");
        panelGlassTindakan.add(ChkCloseSuction);
        ChkCloseSuction.setBounds(150, 40, 100, 23);

        ChkArteriLineCatheter = new widget.CekBox();
        ChkArteriLineCatheter.setText("Arteri Line Catheter");
        ChkArteriLineCatheter.setName("ChkArteriLineCatheter");
        panelGlassTindakan.add(ChkArteriLineCatheter);
        ChkArteriLineCatheter.setBounds(260, 40, 140, 23);

        ChkDowerCatheter = new widget.CekBox();
        ChkDowerCatheter.setText("Dower Catheter");
        ChkDowerCatheter.setName("ChkDowerCatheter");
        panelGlassTindakan.add(ChkDowerCatheter);
        ChkDowerCatheter.setBounds(410, 40, 110, 23);

        ChkInfusPerifer = new widget.CekBox();
        ChkInfusPerifer.setText("Infus Perifer");
        ChkInfusPerifer.setName("ChkInfusPerifer");
        panelGlassTindakan.add(ChkInfusPerifer);
        ChkInfusPerifer.setBounds(530, 40, 100, 23);

        // Row 3: Pemasangan ke
        javax.swing.JLabel jLabelPemasanganKe = new javax.swing.JLabel("Pemasangan ke :");
        jLabelPemasanganKe.setName("jLabelPemasanganKe");
        panelGlassTindakan.add(jLabelPemasanganKe);
        jLabelPemasanganKe.setBounds(0, 80, 100, 23);
        TPemasanganKe = new widget.TextBox();
        TPemasanganKe.setName("TPemasanganKe");
        panelGlassTindakan.add(TPemasanganKe);
        TPemasanganKe.setBounds(105, 80, 200, 23);

        // Row 4: Hari Ke
        javax.swing.JLabel jLabelHariKe = new javax.swing.JLabel("Hari Ke :");
        jLabelHariKe.setName("jLabelHariKe");
        panelGlassTindakan.add(jLabelHariKe);
        jLabelHariKe.setBounds(320, 80, 60, 23);
        THariKe = new widget.TextBox();
        THariKe.setName("THariKe");
        panelGlassTindakan.add(THariKe);
        THariKe.setBounds(385, 80, 80, 23);

        // Row 5: Keterangan (Optional)
        javax.swing.JLabel jLabelKeteranganTindakan = new javax.swing.JLabel("Keterangan :");
        jLabelKeteranganTindakan.setName("jLabelKeteranganTindakan");
        panelGlassTindakan.add(jLabelKeteranganTindakan);
        jLabelKeteranganTindakan.setBounds(0, 110, 80, 23);
        TKeteranganTindakan = new widget.TextBox();
        TKeteranganTindakan.setName("TKeteranganTindakan");
        panelGlassTindakan.add(TKeteranganTindakan);
        TKeteranganTindakan.setBounds(85, 110, 400, 23);

        // Petugas dipindah ke panel atas (NIPHeader)
        jLabelPetugasTindakan.setVisible(false);
        NIPTindakan.setVisible(false);
        NamaPetugasTindakan.setVisible(false);
        btnPetugasTindakan.setVisible(false);

        scrollPaneTindakan.setViewportView(panelGlassTindakan);
        PanelInputTindakan.add(scrollPaneTindakan, java.awt.BorderLayout.PAGE_START);
        internalFrameTindakan.add(PanelInputTindakan, java.awt.BorderLayout.PAGE_START);
        // Tab "Tindakan" dihilangkan (permintaan user). Tabel DB icu_tindakan tetap.
        // TabICU.addTab("Tindakan", internalFrameTindakan);

        // Setup Tab Catatan Terintegrasi (moved to last position with pemeriksaan_ranap fields)
        internalFrame5.setBorder(null);
        internalFrame5.setName("internalFrame5");
        internalFrame5.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll3.setName("Scroll3");
        tbCatatan.setName("tbCatatan");
        tbCatatan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbCatatanMouseClicked(evt);
            }
        });
        Scroll3.setViewportView(tbCatatan);
        internalFrame5.add(Scroll3, java.awt.BorderLayout.CENTER);

        PanelInputCatatan.setName("PanelInputCatatan");
        PanelInputCatatan.setOpaque(false);
        PanelInputCatatan.setPreferredSize(new java.awt.Dimension(192, 280));
        PanelInputCatatan.setLayout(new java.awt.BorderLayout(1, 1));
        ChkInputCatatan.setText(".: Input Catatan Terintegrasi");
        ChkInputCatatan.setBorderPainted(true);
        ChkInputCatatan.setBorderPaintedFlat(true);
        ChkInputCatatan.setFocusable(false);
        ChkInputCatatan.setName("ChkInputCatatan");
        ChkInputCatatan.setPreferredSize(new java.awt.Dimension(192, 20));
        PanelInputCatatan.add(ChkInputCatatan, java.awt.BorderLayout.PAGE_END);

        scrollPaneCatatan.setName("scrollPaneCatatan");
        panelGlassCatatan.removeAll();
        panelGlassCatatan.setName("panelGlassCatatan");
        panelGlassCatatan.setPreferredSize(new java.awt.Dimension(44, 280));
        panelGlassCatatan.setLayout(null);

        // Layout sama dengan DlgRawatInap tab Pemeriksaan (2 kolom)
        // === KOLOM KIRI ===
        // Row 1: Dilakukan (Kode Pegawai)
        javax.swing.JLabel jLabelDilakukan = new javax.swing.JLabel("Dilakukan :");
        panelGlassCatatan.add(jLabelDilakukan);
        jLabelDilakukan.setBounds(0, 10, 70, 23);
        KdPegCatatan = new widget.TextBox();
        KdPegCatatan.setName("KdPegCatatan");
        KdPegCatatan.setPreferredSize(new Dimension(130, 23));
        KdPegCatatan.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    NamaPegawaiCatatan.setText(Sequel.cariIsi("select nama from pegawai where nik=?", KdPegCatatan.getText()));
                }
            }
        });
        panelGlassCatatan.add(KdPegCatatan);
        KdPegCatatan.setBounds(73, 10, 130, 23);
        NamaPegawaiCatatan = new widget.TextBox();
        NamaPegawaiCatatan.setName("NamaPegawaiCatatan");
        NamaPegawaiCatatan.setEditable(false);
        panelGlassCatatan.add(NamaPegawaiCatatan);
        NamaPegawaiCatatan.setBounds(205, 10, 200, 23);
        widget.Button btnCariPegawaiCatatan = new widget.Button();
        btnCariPegawaiCatatan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnCariPegawaiCatatan.setToolTipText("Cari Pegawai");
        btnCariPegawaiCatatan.addActionListener(e -> {
            DlgCariPegawai dlgPeg = new DlgCariPegawai(null, true);
            dlgPeg.emptTeks();
            dlgPeg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            dlgPeg.setLocationRelativeTo(internalFrame1);
            dlgPeg.setVisible(true);
            if (dlgPeg.getTable().getSelectedRow() != -1) {
                KdPegCatatan.setText(dlgPeg.getTable().getValueAt(dlgPeg.getTable().getSelectedRow(), 0).toString());
                NamaPegawaiCatatan.setText(dlgPeg.getTable().getValueAt(dlgPeg.getTable().getSelectedRow(), 1).toString());
            }
        });
        panelGlassCatatan.add(btnCariPegawaiCatatan);
        btnCariPegawaiCatatan.setBounds(407, 10, 28, 23);

        // Row 2: Subjek (S)
        javax.swing.JLabel jLabelSubjek = new javax.swing.JLabel("Subjek :");
        panelGlassCatatan.add(jLabelSubjek);
        jLabelSubjek.setBounds(0, 40, 70, 23);
        scrollPaneKeluhan = new javax.swing.JScrollPane();
        scrollPaneKeluhan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        TKeluhan = new javax.swing.JTextArea();
        TKeluhan.setColumns(20);
        TKeluhan.setRows(5);
        TKeluhan.setLineWrap(true);
        TKeluhan.setWrapStyleWord(true);
        scrollPaneKeluhan.setViewportView(TKeluhan);
        panelGlassCatatan.add(scrollPaneKeluhan);
        scrollPaneKeluhan.setBounds(73, 40, 360, 38);

        // Row 3: Objek (O)
        javax.swing.JLabel jLabelObjek = new javax.swing.JLabel("Objek :");
        panelGlassCatatan.add(jLabelObjek);
        jLabelObjek.setBounds(0, 85, 70, 23);
        scrollPanePemeriksaanRanap = new javax.swing.JScrollPane();
        scrollPanePemeriksaanRanap.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        TPemeriksaanRanap = new javax.swing.JTextArea();
        TPemeriksaanRanap.setColumns(20);
        TPemeriksaanRanap.setRows(5);
        TPemeriksaanRanap.setLineWrap(true);
        TPemeriksaanRanap.setWrapStyleWord(true);
        scrollPanePemeriksaanRanap.setViewportView(TPemeriksaanRanap);
        panelGlassCatatan.add(scrollPanePemeriksaanRanap);
        scrollPanePemeriksaanRanap.setBounds(73, 85, 360, 38);

        // Row 4: Suhu, Tensi, Nadi, Respirasi
        javax.swing.JLabel jLabelSuhuTubuh = new javax.swing.JLabel("Suhu (°C) :");
        panelGlassCatatan.add(jLabelSuhuTubuh);
        jLabelSuhuTubuh.setBounds(0, 130, 70, 23);
        TSuhuTubuh = new widget.TextBox();
        panelGlassCatatan.add(TSuhuTubuh);
        TSuhuTubuh.setBounds(73, 130, 55, 23);
        javax.swing.JLabel jLabelTensi = new javax.swing.JLabel("Tensi :");
        panelGlassCatatan.add(jLabelTensi);
        jLabelTensi.setBounds(133, 130, 40, 23);
        TTensi = new widget.TextBox();
        panelGlassCatatan.add(TTensi);
        TTensi.setBounds(178, 130, 65, 23);
        javax.swing.JLabel jLabelNadi = new javax.swing.JLabel("Nadi :");
        panelGlassCatatan.add(jLabelNadi);
        jLabelNadi.setBounds(248, 130, 35, 23);
        TNadi = new widget.TextBox();
        panelGlassCatatan.add(TNadi);
        TNadi.setBounds(288, 130, 55, 23);
        javax.swing.JLabel jLabelRespirasi = new javax.swing.JLabel("RR :");
        panelGlassCatatan.add(jLabelRespirasi);
        jLabelRespirasi.setBounds(348, 130, 30, 23);
        TRespirasi = new widget.TextBox();
        panelGlassCatatan.add(TRespirasi);
        TRespirasi.setBounds(383, 130, 50, 23);

        // Row 5: TB, BB
        javax.swing.JLabel jLabelTinggi = new javax.swing.JLabel("TB (cm) :");
        panelGlassCatatan.add(jLabelTinggi);
        jLabelTinggi.setBounds(0, 160, 70, 23);
        TTinggi = new widget.TextBox();
        panelGlassCatatan.add(TTinggi);
        TTinggi.setBounds(73, 160, 55, 23);
        javax.swing.JLabel jLabelBerat = new javax.swing.JLabel("BB (kg) :");
        panelGlassCatatan.add(jLabelBerat);
        jLabelBerat.setBounds(133, 160, 55, 23);
        TBerat = new widget.TextBox();
        panelGlassCatatan.add(TBerat);
        TBerat.setBounds(193, 160, 55, 23);

        // Row 6: SpO2, GCS, Kesadaran
        javax.swing.JLabel jLabelSpO2 = new javax.swing.JLabel("SpO2 (%) :");
        panelGlassCatatan.add(jLabelSpO2);
        jLabelSpO2.setBounds(0, 190, 70, 23);
        TSpO2 = new widget.TextBox();
        panelGlassCatatan.add(TSpO2);
        TSpO2.setBounds(73, 190, 42, 23);
        javax.swing.JLabel jLabelKesadaran = new javax.swing.JLabel("Kesadaran :");
        panelGlassCatatan.add(jLabelKesadaran);
        jLabelKesadaran.setBounds(120, 190, 70, 23);
        CmbKesadaran = new widget.ComboBox();
        CmbKesadaran.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "Compos Mentis", "Somnolence", "Sopor", "Coma", "Apatis", "Delirium"
        }));
        panelGlassCatatan.add(CmbKesadaran);
        CmbKesadaran.setBounds(193, 190, 126, 23);

        // === KOLOM KANAN ===
        int rx = 450, rfx = 543, rw = 360;

        // Alergi
        javax.swing.JLabel jLabelAlergi = new javax.swing.JLabel("Alergi :");
        panelGlassCatatan.add(jLabelAlergi);
        jLabelAlergi.setBounds(rx, 10, 90, 23);
        TAlergi = new widget.TextBox();
        panelGlassCatatan.add(TAlergi);
        TAlergi.setBounds(rfx, 10, rw, 23);

        // Asesmen (A)
        javax.swing.JLabel jLabelPenilaian = new javax.swing.JLabel("Asesmen :");
        panelGlassCatatan.add(jLabelPenilaian);
        jLabelPenilaian.setBounds(rx, 40, 90, 23);
        scrollPanePenilaian = new javax.swing.JScrollPane();
        scrollPanePenilaian.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        TPenilaian = new javax.swing.JTextArea();
        TPenilaian.setColumns(20);
        TPenilaian.setRows(5);
        TPenilaian.setLineWrap(true);
        TPenilaian.setWrapStyleWord(true);
        scrollPanePenilaian.setViewportView(TPenilaian);
        panelGlassCatatan.add(scrollPanePenilaian);
        scrollPanePenilaian.setBounds(rfx, 40, rw, 38);

        // Plan (P)
        javax.swing.JLabel jLabelRTL = new javax.swing.JLabel("Plan :");
        panelGlassCatatan.add(jLabelRTL);
        jLabelRTL.setBounds(rx, 85, 90, 23);
        scrollPaneRTL = new javax.swing.JScrollPane();
        scrollPaneRTL.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        TRTL = new javax.swing.JTextArea();
        TRTL.setColumns(20);
        TRTL.setRows(5);
        TRTL.setLineWrap(true);
        TRTL.setWrapStyleWord(true);
        scrollPaneRTL.setViewportView(TRTL);
        panelGlassCatatan.add(scrollPaneRTL);
        scrollPaneRTL.setBounds(rfx, 85, rw, 47);

        // Instruksi (I)
        javax.swing.JLabel jLabelInstruksi = new javax.swing.JLabel("Instruksi :");
        panelGlassCatatan.add(jLabelInstruksi);
        jLabelInstruksi.setBounds(rx, 139, 90, 23);
        scrollPaneInstruksi = new javax.swing.JScrollPane();
        scrollPaneInstruksi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        TInstruksi = new javax.swing.JTextArea();
        TInstruksi.setColumns(20);
        TInstruksi.setRows(5);
        TInstruksi.setLineWrap(true);
        TInstruksi.setWrapStyleWord(true);
        scrollPaneInstruksi.setViewportView(TInstruksi);
        panelGlassCatatan.add(scrollPaneInstruksi);
        scrollPaneInstruksi.setBounds(rfx, 139, rw, 50);

        // Evaluasi (E)
        javax.swing.JLabel jLabelEvaluasi = new javax.swing.JLabel("Evaluasi :");
        panelGlassCatatan.add(jLabelEvaluasi);
        jLabelEvaluasi.setBounds(rx, 196, 90, 23);
        scrollPaneEvaluasi = new javax.swing.JScrollPane();
        scrollPaneEvaluasi.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        TEvaluasi = new javax.swing.JTextArea();
        TEvaluasi.setColumns(20);
        TEvaluasi.setRows(5);
        TEvaluasi.setLineWrap(true);
        TEvaluasi.setWrapStyleWord(true);
        scrollPaneEvaluasi.setViewportView(TEvaluasi);
        panelGlassCatatan.add(scrollPaneEvaluasi);
        scrollPaneEvaluasi.setBounds(rfx, 196, rw, 44);

        // Petugas dipindah ke panel atas (NIPHeader)
        NIPCatatan.setVisible(false);
        NamaPetugasCatatan.setVisible(false);
        btnPetugasCatatan.setVisible(false);

        scrollPaneCatatan.setViewportView(panelGlassCatatan);
        PanelInputCatatan.add(scrollPaneCatatan, java.awt.BorderLayout.PAGE_START);
        internalFrame5.add(PanelInputCatatan, java.awt.BorderLayout.PAGE_START);
        TabICU.addTab("Catatan Terintegrasi", internalFrame5);

        // Tab inserts (idx 5-10) — pakai insertTab agar landing di posisi tepat.
        // HARUS dipanggil sebelum setupTab* lain (yg pakai addTab → end).
        setupTabResume();           // idx 5
        setupTabMIT();              // idx 6
        setupTabFasthug();          // idx 7
        setupTabAlat();             // idx 8
        setupTabKonsultasi();       // idx 9
        setupTabRencana();          // idx 10
        setupTabCatatanKhusus();    // idx 11
        setupTabHasilLab();         // idx 12 (viewer)
        setupTabHasilRadiologi();   // idx 13 (viewer + gambar + expertise)
        setupTabNutri();            // idx 14

        // Tab Penanganan Dokter, Petugas, Dokter & Petugas
        // setupTabDailyChecklist() di-hilangkan — fungsi FASTHUG sudah pindah ke tab Fasthug baru.
        setupTabHandoverSBAR();
        setupTabPenangananDokter();
        setupTabPenangananPetugas();
        setupTabPenangananDokterPetugas();

        // ========== SIDEBAR MENU (seperti DlgRawatInap) ==========
        PanelAccor = new widget.PanelBiasa();
        PanelAccor.setBackground(new java.awt.Color(255, 255, 255));
        PanelAccor.setName("PanelAccor");
        PanelAccor.setPreferredSize(new java.awt.Dimension(205, 43));
        PanelAccor.setLayout(new java.awt.BorderLayout());

        ChkAccor = new widget.CekBox();
        ChkAccor.setBackground(new java.awt.Color(255, 250, 250));
        ChkAccor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(250, 255, 248)));
        ChkAccor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kanan.png")));
        ChkAccor.setFocusable(false);
        ChkAccor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkAccor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkAccor.setName("ChkAccor");
        ChkAccor.setPreferredSize(new java.awt.Dimension(15, 20));
        ChkAccor.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kanan.png")));
        ChkAccor.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kiri.png")));
        ChkAccor.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kiri.png")));
        ChkAccor.setSelected(true);
        ChkAccor.addActionListener(e -> isMenu());
        PanelAccor.add(ChkAccor, java.awt.BorderLayout.EAST);

        ScrollMenu = new widget.ScrollPane();
        ScrollMenu.setBorder(null);
        ScrollMenu.setName("ScrollMenu");
        ScrollMenu.setOpaque(true);

        FormMenu = new widget.PanelBiasa();
        FormMenu.setBackground(new java.awt.Color(255, 255, 255));
        FormMenu.setBorder(null);
        FormMenu.setName("FormMenu");
        FormMenu.setPreferredSize(new java.awt.Dimension(195, 700));
        FormMenu.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 1));

        // Menu buttons
        BtnRiwayat = createMenuButton("Riwayat Pasien");
        BtnRiwayat.addActionListener(e -> openRiwayatPasien());
        FormMenu.add(BtnRiwayat);

        BtnRiwayatSOAPIE = createMenuButton("Riwayat SOAPIE");
        BtnRiwayatSOAPIE.addActionListener(e -> openRiwayatSOAPIE());
        FormMenu.add(BtnRiwayatSOAPIE);

        BtnResepObat = createMenuButton("Input Resep");
        BtnResepObat.addActionListener(e -> openResepObat());
        FormMenu.add(BtnResepObat);

        BtnCopyResep = createMenuButton("Copy Resep");
        BtnCopyResep.addActionListener(e -> openCopyResep());
        FormMenu.add(BtnCopyResep);

        BtnPermintaanLab = createMenuButton("Permintaan Lab");
        BtnPermintaanLab.addActionListener(e -> openPermintaanLab());
        FormMenu.add(BtnPermintaanLab);

        BtnPermintaanRad = createMenuButton("Permintaan Radiologi");
        BtnPermintaanRad.addActionListener(e -> openPermintaanRad());
        FormMenu.add(BtnPermintaanRad);

        BtnDiagnosa = createMenuButton("Diagnosa");
        BtnDiagnosa.addActionListener(e -> openDiagnosa());
        FormMenu.add(BtnDiagnosa);

        BtnResume = createMenuButton("Resume Pasien");
        BtnResume.addActionListener(e -> openResume());
        FormMenu.add(BtnResume);

        BtnBerkasDigital = createMenuButton("Berkas Digital");
        BtnBerkasDigital.addActionListener(e -> openBerkasDigital());
        FormMenu.add(BtnBerkasDigital);

        BtnPermintaanKonsul = createMenuButton("Konsultasi Medik");
        BtnPermintaanKonsul.addActionListener(e -> openKonsultasiMedik());
        FormMenu.add(BtnPermintaanKonsul);

        BtnSkriningNutrisi = createMenuButton("Skrining Nutrisi");
        BtnSkriningNutrisi.addActionListener(e -> openSkriningNutrisi());
        FormMenu.add(BtnSkriningNutrisi);

        BtnPenilaianResikoJatuh = createMenuButton("Penilaian Resiko Jatuh");
        BtnPenilaianResikoJatuh.addActionListener(e -> openPenilaianResikoJatuh());
        FormMenu.add(BtnPenilaianResikoJatuh);

        BtnTransferRuang = createMenuButton("Transfer Antar Ruang");
        BtnTransferRuang.addActionListener(e -> openTransferRuang());
        FormMenu.add(BtnTransferRuang);

        BtnDashboardICU = createMenuButton("Dashboard ICU");
        BtnDashboardICU.addActionListener(e -> openDashboardICU());
        FormMenu.add(BtnDashboardICU);

        ScrollMenu.setViewportView(FormMenu);
        PanelAccor.add(ScrollMenu, java.awt.BorderLayout.CENTER);
        internalFrame1.add(PanelAccor, java.awt.BorderLayout.WEST);

        internalFrame1.add(TabICU, java.awt.BorderLayout.CENTER);

        // Form Input (simplified)
        FormInput.setName("FormInput");
        // Tinggi: 4 baris × 30 = 120 + margin 10 = 130 (4 row layout: 10/40/70/100)
        FormInput.setPreferredSize(new java.awt.Dimension(260, 130));
        FormInput.setLayout(null);

        jLabel50.setText("No.Rawat :");
        jLabel50.setName("jLabel50");
        FormInput.add(jLabel50);
        jLabel50.setBounds(0, 10, 70, 23);
        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw");
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(74, 10, 125, 23);
        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM");
        FormInput.add(TNoRM);
        TNoRM.setBounds(201, 10, 80, 23);
        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien");
        FormInput.add(TPasien);
        TPasien.setBounds(283, 10, 250, 23);

        jLabel51.setText("Tanggal :");
        jLabel51.setName("jLabel51");
        FormInput.add(jLabel51);
        jLabel51.setBounds(540, 10, 60, 23);
        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"04-09-2025"}));
        Tanggal.setDisplayFormat("dd-MM-yyyy");
        Tanggal.setName("Tanggal");
        Tanggal.setOpaque(false);
        FormInput.add(Tanggal);
        Tanggal.setBounds(603, 10, 90, 23);

        Jam.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
            "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23"
        }));
        Jam.setName("Jam");
        FormInput.add(Jam);
        Jam.setBounds(697, 10, 62, 23);

        Menit.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
            "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47",
            "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"
        }));
        Menit.setName("Menit");
        FormInput.add(Menit);
        Menit.setBounds(762, 10, 62, 23);

        Detik.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
            "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
            "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47",
            "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59"
        }));
        Detik.setName("Detik");
        FormInput.add(Detik);
        Detik.setBounds(827, 10, 62, 23);

        ChkJln.setBorder(null);
        ChkJln.setBorderPainted(true);
        ChkJln.setBorderPaintedFlat(true);
        ChkJln.setFont(new java.awt.Font("Tahoma", 1, 11));
        ChkJln.setSelected(false);
        ChkJln.setName("ChkJln");
        FormInput.add(ChkJln);
        ChkJln.setBounds(892, 10, 23, 23);

        jLabel52.setText("Tgl.Lahir :");
        jLabel52.setName("jLabel52");
        FormInput.add(jLabel52);
        jLabel52.setBounds(0, 40, 70, 23);
        TglLahir.setEditable(false);
        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir");
        FormInput.add(TglLahir);
        TglLahir.setBounds(74, 40, 100, 23);
        jLabel53.setText("Umur :");
        jLabel53.setName("jLabel53");
        FormInput.add(jLabel53);
        jLabel53.setBounds(180, 40, 50, 23);
        Umur.setEditable(false);
        Umur.setHighlighter(null);
        Umur.setName("Umur");
        FormInput.add(Umur);
        Umur.setBounds(235, 40, 100, 23);

        // Petugas di panel atas (shared semua tab)
        NIPHeader = new widget.TextBox();
        NamaPetugasHeader = new widget.TextBox();
        btnPetugasHeader = new widget.Button();

        jLabelPetugas = new widget.Label();
        jLabelPetugas.setText("Petugas :");
        jLabelPetugas.setName("jLabelPetugas");
        FormInput.add(jLabelPetugas);
        jLabelPetugas.setBounds(345, 40, 60, 23);
        NIPHeader.setEditable(false);
        NIPHeader.setName("NIPHeader");
        FormInput.add(NIPHeader);
        NIPHeader.setBounds(405, 40, 90, 23);
        NamaPetugasHeader.setEditable(false);
        NamaPetugasHeader.setName("NamaPetugasHeader");
        FormInput.add(NamaPetugasHeader);
        NamaPetugasHeader.setBounds(500, 40, 200, 23);
        btnPetugasHeader.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnPetugasHeader.setName("btnPetugasHeader");
        btnPetugasHeader.addActionListener(e -> btnPetugasHeaderActionPerformed());
        FormInput.add(btnPetugasHeader);
        btnPetugasHeader.setBounds(705, 40, 28, 23);

        // Score TextBoxes — di-instantiate utk back-compat dgn loadHeaderScores().
        // BB, TB, PELOD2, RAMSAY, RASS, APACHE TIDAK lagi di-add ke panel atas
        // (permintaan user). Hanya SOFA yg masih ditampilkan.
        TBeratBadanHeader = new widget.TextBox();
        TTinggiBadanHeader = new widget.TextBox();
        TPELOD2Header = new widget.TextBox();
        TRAMSAYHeader = new widget.TextBox();
        TRASSHeader = new widget.TextBox();
        TSOFAHeader = new widget.TextBox();
        TAPACHEHeader = new widget.TextBox();
        BtnPelod2 = new widget.Button();
        BtnRamsay = new widget.Button();
        BtnRass = new widget.Button();
        BtnSofa = new widget.Button();
        BtnApache = new widget.Button();

        // Row 3 (y=70): Tgl Masuk ICU | Hari Rawat | Hari Operasi | SOFA + btn
        int y3 = 70, bw = 18;
        TTglMasukIcuHeader = new widget.TextBox();
        THariRawatHeader = new widget.TextBox();
        THariOperasiHeader = new widget.TextBox();

        javax.swing.JLabel lTglMasukIcu = new javax.swing.JLabel("Tgl Masuk ICU :");
        lTglMasukIcu.setBounds(0, y3, 95, 23); FormInput.add(lTglMasukIcu);
        TTglMasukIcuHeader.setEditable(false);
        TTglMasukIcuHeader.setName("TTglMasukIcuHeader");
        TTglMasukIcuHeader.setBounds(98, y3, 110, 23); FormInput.add(TTglMasukIcuHeader);

        javax.swing.JLabel lHariRawat = new javax.swing.JLabel("Hari Rawat :");
        lHariRawat.setBounds(218, y3, 75, 23); FormInput.add(lHariRawat);
        THariRawatHeader.setEditable(false);
        THariRawatHeader.setName("THariRawatHeader");
        THariRawatHeader.setBounds(295, y3, 50, 23); FormInput.add(THariRawatHeader);

        javax.swing.JLabel lHariOp = new javax.swing.JLabel("Hari Operasi :");
        lHariOp.setBounds(355, y3, 85, 23); FormInput.add(lHariOp);
        THariOperasiHeader.setEditable(false);
        THariOperasiHeader.setName("THariOperasiHeader");
        THariOperasiHeader.setBounds(442, y3, 50, 23); FormInput.add(THariOperasiHeader);

        jLabelSofa = new widget.Label();
        jLabelSofa.setText("SOFA :");
        jLabelSofa.setName("jLabelSofa");
        FormInput.add(jLabelSofa);
        jLabelSofa.setBounds(505, y3, 40, 23);
        TSOFAHeader.setEditable(false);
        TSOFAHeader.setName("TSOFAHeader");
        FormInput.add(TSOFAHeader);
        TSOFAHeader.setBounds(545, y3, 35, 23);
        BtnSofa.setText("+");
        BtnSofa.setFont(new java.awt.Font("Tahoma", 1, 11));
        BtnSofa.setFocusPainted(false);
        BtnSofa.setMargin(new java.awt.Insets(0, 0, 0, 0));
        BtnSofa.setToolTipText("Buka Form SOFA Score");
        BtnSofa.addActionListener(e -> openScoreForm("SOFA"));
        FormInput.add(BtnSofa);
        BtnSofa.setBounds(582, y3, bw, 23);

        // Row 4 (y=100): DPJP | Diagnosa
        int y4 = 100;
        TDpjpHeader = new widget.TextBox();
        TDiagnosaHeader = new widget.TextBox();

        javax.swing.JLabel lDpjp = new javax.swing.JLabel("DPJP :");
        lDpjp.setBounds(0, y4, 50, 23); FormInput.add(lDpjp);
        TDpjpHeader.setEditable(false);
        TDpjpHeader.setName("TDpjpHeader");
        TDpjpHeader.setBounds(53, y4, 260, 23); FormInput.add(TDpjpHeader);

        javax.swing.JLabel lDiag = new javax.swing.JLabel("Diagnosa :");
        lDiag.setBounds(325, y4, 70, 23); FormInput.add(lDiag);
        TDiagnosaHeader.setEditable(false);
        TDiagnosaHeader.setName("TDiagnosaHeader");
        TDiagnosaHeader.setBounds(398, y4, 600, 23); FormInput.add(TDiagnosaHeader);

        internalFrame1.add(FormInput, java.awt.BorderLayout.PAGE_START);

        // Control Panel (simplified)
        jPanel3.setBackground(new java.awt.Color(215, 225, 215));
        jPanel3.setName("jPanel3");
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 140));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8");
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan");
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnSimpan);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Baru");
        BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setName("BtnBatal");
        BtnBatal.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBatalActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnBatal);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus");
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnHapus);

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnEdit.setMnemonic('G');
        BtnEdit.setText("Ganti");
        BtnEdit.setToolTipText("Alt+G");
        BtnEdit.setName("BtnEdit");
        BtnEdit.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEditActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnEdit);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setName("BtnPrint");
        BtnPrint.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrintActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnPrint);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setMnemonic('M');
        BtnAll.setText("Semua");
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll");
        BtnAll.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnAll);

        jLabel60.setText("Record :");
        jLabel60.setName("jLabel60");
        jLabel60.setPreferredSize(new java.awt.Dimension(95, 30));
        panelGlass8.add(jLabel60);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount");
        LCount.setPreferredSize(new java.awt.Dimension(87, 30));
        panelGlass8.add(LCount);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar");
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnKeluar);

        jPanel3.add(panelGlass8, java.awt.BorderLayout.PAGE_START);

        panelGlass10.setName("panelGlass10");
        panelGlass10.setPreferredSize(new java.awt.Dimension(44, 44));

        jLabel61.setText("Tgl.Observasi :");
        jLabel61.setName("jLabel61");
        jLabel61.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass10.add(jLabel61);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"04-09-2025"}));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1");
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass10.add(DTPCari1);

        jLabel62.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel62.setText("s.d.");
        jLabel62.setName("jLabel62");
        jLabel62.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass10.add(jLabel62);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"04-09-2025"}));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2");
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass10.add(DTPCari2);

        jLabel63.setText("No.RM :");
        jLabel63.setName("jLabel63");
        jLabel63.setPreferredSize(new java.awt.Dimension(55, 23));
        panelGlass10.add(jLabel63);

        TCariPasien.setName("TCariPasien");
        TCariPasien.setPreferredSize(new java.awt.Dimension(140, 23));
        panelGlass10.add(TCariPasien);

        btnPasien.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnPasien.setMnemonic('6');
        btnPasien.setToolTipText("Alt+6");
        btnPasien.setName("btnPasien");
        btnPasien.setPreferredSize(new java.awt.Dimension(28, 23));
        btnPasien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPasienActionPerformed(evt);
            }
        });
        panelGlass10.add(btnPasien);

        jSeparator1.setBackground(new java.awt.Color(220, 225, 215));
        jSeparator1.setForeground(new java.awt.Color(220, 225, 215));
        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator1.setName("jSeparator1");
        jSeparator1.setOpaque(true);
        jSeparator1.setPreferredSize(new java.awt.Dimension(1, 23));
        panelGlass10.add(jSeparator1);

        jLabel64.setText("Key Word :");
        jLabel64.setName("jLabel64");
        jLabel64.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass10.add(jLabel64);

        TCari.setName("TCari");
        TCari.setPreferredSize(new java.awt.Dimension(250, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass10.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setMnemonic('7');
        BtnCari.setToolTipText("Alt+7");
        BtnCari.setName("BtnCari");
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        panelGlass10.add(BtnCari);

        jPanel3.add(panelGlass10, java.awt.BorderLayout.CENTER);

        // Panel Singkatan (Daftar Singkatan)
        javax.swing.JPanel panelSingkatan = new javax.swing.JPanel();
        panelSingkatan.setBackground(new java.awt.Color(255, 255, 255));
        panelSingkatan.setPreferredSize(new java.awt.Dimension(44, 50));
        panelSingkatan.setLayout(null);
        java.awt.Font fontSingkatan = new java.awt.Font("Tahoma", 1, 9);

        // Row 1
        addSingkatan(panelSingkatan, "PIP = Peak Inspiratory Pressure", 5, 2, fontSingkatan);
        addSingkatan(panelSingkatan, "HR = Heart Rate", 230, 2, fontSingkatan);
        addSingkatan(panelSingkatan, "IBP = Invasive Blood Pressure", 380, 2, fontSingkatan);
        addSingkatan(panelSingkatan, "IWL = Insessible Water Loss", 600, 2, fontSingkatan);
        addSingkatan(panelSingkatan, "PEEP = Positive End Expiratory Pressure", 800, 2, fontSingkatan);
        addSingkatan(panelSingkatan, "PAP = Pulmonary Artery Pressure", 1060, 2, fontSingkatan);

        // Row 2
        addSingkatan(panelSingkatan, "IPL = Inspiratory Pressure Limit", 5, 15, fontSingkatan);
        addSingkatan(panelSingkatan, "T = Temperature", 230, 15, fontSingkatan);
        addSingkatan(panelSingkatan, "CO = Cardiac Output", 380, 15, fontSingkatan);
        addSingkatan(panelSingkatan, "I = Inspirasi : Ekspirasi Ratio", 600, 15, fontSingkatan);
        addSingkatan(panelSingkatan, "FiO2 = Fraksi Inspirasi Oksigen", 800, 15, fontSingkatan);

        // Row 3
        addSingkatan(panelSingkatan, "NIBP = Non Invasive Blood Pressure", 5, 28, fontSingkatan);
        addSingkatan(panelSingkatan, "RR = Respiratory Rate", 230, 28, fontSingkatan);
        addSingkatan(panelSingkatan, "CVP = Central Venous Pressure", 380, 28, fontSingkatan);
        addSingkatan(panelSingkatan, "TV = Tidal Volume", 600, 28, fontSingkatan);
        addSingkatan(panelSingkatan, "MV = Minute Volume", 800, 28, fontSingkatan);

        jPanel3.add(panelSingkatan, java.awt.BorderLayout.PAGE_END);
        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
        pack();
    }// </editor-fold>                        

    // Event Handler Methods
    private void tbObservasiMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabModeObservasi.getRowCount() != 0) {
            try {
                getDataObservasi();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }

    private void tbVentilatorMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabModeVentilator.getRowCount() != 0) {
            try {
                getDataVentilator();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }

    private void ChkInputObservasiActionPerformed(java.awt.event.ActionEvent evt) {
        isFormObservasi();
    }

    private void ChkInputVentilatorActionPerformed(java.awt.event.ActionEvent evt) {
        isFormVentilator();
    }

    private void ChkInputIntakeActionPerformed(java.awt.event.ActionEvent evt) {
        isFormIntake();
    }

    private void ChkInputMedikasiActionPerformed(java.awt.event.ActionEvent evt) {
        isFormMedikasi();
    }

    private void tbIntakeOutputMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabModeIntakeOutput.getRowCount() != 0) {
            try {
                getDataIntakeOutput();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }

    private void tbMedikasiMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabModeMedikasi.getRowCount() != 0) {
            try {
                getDataMedikasi();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }

    private void btnPetugasObservasiActionPerformed(java.awt.event.ActionEvent evt) {
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }

    private void btnPetugasVentilatorActionPerformed(java.awt.event.ActionEvent evt) {
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }

    private void btnPetugasIntakeActionPerformed(java.awt.event.ActionEvent evt) {
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }

    private void btnPetugasMedikasiActionPerformed(java.awt.event.ActionEvent evt) {
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }

    private void btnPetugasCatatanActionPerformed(java.awt.event.ActionEvent evt) {
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }

    private void MnPrintObservasiActionPerformed(java.awt.event.ActionEvent evt) {
        if (tbObservasi.getSelectedRow() > -1) {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));

            Valid.MyReportqry("rptICUHemodynamic.jasper", "report", "::[ Lembar Observasi ICU Hemodinamik ]::",
                    "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir,"
                    + "observasi_icu_hemodinamik.tgl_observasi,observasi_icu_hemodinamik.jam as jam_observasi,"
                    + "observasi_icu_hemodinamik.hr,observasi_icu_hemodinamik.suhu as temperature,observasi_icu_hemodinamik.rr,"
                    + "observasi_icu_hemodinamik.bp_sistol as nibp,'' as ibp,'' as co,"
                    + "observasi_icu_hemodinamik.cvp,'' as pap,observasi_icu_hemodinamik.nip,petugas.nama "
                    + "from observasi_icu_hemodinamik inner join reg_periksa on observasi_icu_hemodinamik.no_rawat=reg_periksa.no_rawat "
                    + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                    + "inner join petugas on observasi_icu_hemodinamik.nip=petugas.nip where observasi_icu_hemodinamik.no_rawat='"
                    + tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 1).toString() + "'", param);
        }
    }

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            isRawat();
        }
    }

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else {
            switch (TabICU.getSelectedIndex()) {
                case 0:  simpanObservasi(); break;
                case 1:  simpanVentilator(); break;
                case 2:  simpanMedikasi(); break;       // tab idx 2 = Medikasi
                case 3:  simpanIntakeOutput(); break;   // tab idx 3 = Intake-Output
                case 4:  simpanCatatanKeperawatan(); break;
                case 5:  simpanResume(); break;
                case 6:  simpanMIT(); break;
                case 7:  simpanFasthug(); break;
                case 8:  simpanAlat(); break;
                case 9:  simpanKonsultasi(); break;
                case 10: simpanRencana(); break;
                case 11: simpanCatKhusus(); break;
                // case 12: Hasil Lab — viewer (no save)
                // case 13: Hasil Radiologi — viewer (no save)
                case 14: simpanNutri(); break;
                case 15: simpanCatatan(); break;
                case 16: simpanSBAR(); break;
                case 17: simpanPenangananDr(); break;
                case 18: simpanPenangananPr(); break;
                case 19: simpanPenangananDrPr(); break;
                case 20: simpanMonitoringTambahan(); break;
                default: break;
            }
        }
    }

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {
        emptTeks();
    }

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {
        switch (TabICU.getSelectedIndex()) {
            case 0:  hapusObservasi(); break;
            case 1:  hapusVentilator(); break;
            case 2:  hapusMedikasi(); break;       // tab idx 2 = Medikasi
            case 3:  hapusIntakeOutput(); break;   // tab idx 3 = Intake-Output
            case 4:  hapusCatatanKeperawatan(); break;
            case 5:  hapusResume(); break;
            case 6:  hapusMIT(); break;
            case 7:  hapusFasthug(); break;
            case 8:  hapusAlat(); break;
            case 9:  hapusKonsultasi(); break;
            case 10: hapusRencana(); break;
            case 11: hapusCatKhusus(); break;
            // case 12,13: viewer (no delete)
            case 14: hapusNutri(); break;
            case 15: hapusCatatan(); break;
            case 16: hapusSBAR(); break;
            case 17: hapusPenangananDr(); break;
            case 18: hapusPenangananPr(); break;
            case 19: hapusPenangananDrPr(); break;
            case 20: hapusMonitoringTambahan(); break;
            default: break;
        }
    }

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {
        switch (TabICU.getSelectedIndex()) {
            case 0:  editObservasi(); break;
            case 1:  editVentilator(); break;
            case 2:  editMedikasi(); break;       // tab idx 2 = Medikasi
            case 3:  editIntakeOutput(); break;   // tab idx 3 = Intake-Output
            case 4:  editCatatanKeperawatan(); break;
            case 5:  editResume(); break;
            case 6:  editMIT(); break;
            case 7:  editFasthug(); break;
            case 8:  editAlat(); break;
            case 9:  editKonsultasi(); break;
            case 10: editRencana(); break;
            case 11: editCatKhusus(); break;
            // case 12,13: viewer (no edit)
            case 14: editNutri(); break;
            case 15: editCatatan(); break;
            case 20: editMonitoringTambahan(); break;
            default: break;
        }
    }

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (!TCari.getText().trim().equals("")) {
            BtnCariActionPerformed(evt);
        }
        printCurrentTab();
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {
        TCari.setText("");
        TCariPasien.setText("");
        BtnCariActionPerformed(null);
    }

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    private void btnPasienActionPerformed(java.awt.event.ActionEvent evt) {
        // Open patient search dialog - implementation needed
    }

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariActionPerformed(null);
        }
    }

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {
        switch (TabICU.getSelectedIndex()) {
            case 0:  tampilObservasi(); break;
            case 1:  tampilVentilator(); break;
            case 2:  tampilMedikasi(); break;       // tab idx 2 = Medikasi
            case 3:  tampilIntakeOutput(); break;   // tab idx 3 = Intake-Output
            case 4:  tampilCatatanKeperawatan(); break;
            case 5:  tampilResume(); break;
            case 6:  tampilMIT(); break;
            case 7:  tampilFasthug(); break;
            case 8:  tampilAlat(); break;
            case 9:  tampilKonsultasi(); break;
            case 10: tampilRencana(); break;
            case 11: tampilCatKhusus(); break;
            case 12: tampilHasilLab(); break;
            case 13: tampilHasilRadiologi(); break;
            case 14: tampilNutri(); break;
            case 15: tampilCatatan(); break;
            case 20: tampilMonitoringTambahan(); break;
            default: break;
        }
    }

    // Implementation methods
    public void emptTeks() {
        // Reset field untuk Tab Pemantauan Tambahan
        if (cmbSkalaSedasi != null) {
            if (cmbSkalaSedasi.getItemCount() > 0) cmbSkalaSedasi.setSelectedIndex(0);
            TNilaiSedasi.setText("");
            TCRT.setText("");
            TLingkarPerut.setText("");
            if (cmbSekretWarna.getItemCount() > 0) cmbSekretWarna.setSelectedIndex(0);
            if (cmbSekretKonsistensi.getItemCount() > 0) cmbSekretKonsistensi.setSelectedIndex(0);
        }
        // Reset field tambahan Tab Hemodinamik (Tahap 1)
        if (TBPSys != null) {
            TBPSys.setText("");
            TBPDys.setText("");
            TMAP.setText("");
            if (cmbKesadaranObs.getItemCount() > 0) cmbKesadaranObs.setSelectedIndex(0);
            if (cmbRefleksPupilKanan.getItemCount() > 0) cmbRefleksPupilKanan.setSelectedIndex(0);
            if (cmbRefleksPupilKiri.getItemCount() > 0) cmbRefleksPupilKiri.setSelectedIndex(0);
            if (cmbMotorikAKA.getItemCount() > 0) cmbMotorikAKA.setSelectedIndex(0);
            if (cmbMotorikAKI.getItemCount() > 0) cmbMotorikAKI.setSelectedIndex(0);
            if (cmbMotorikBKA.getItemCount() > 0) cmbMotorikBKA.setSelectedIndex(0);
            if (cmbMotorikBKI.getItemCount() > 0) cmbMotorikBKI.setSelectedIndex(0);
        }
        // Reset field untuk Tab Hemodinamik
        THR.setText("");
        TTemperature.setText("");
        TRespirasi.setText("");
        TNIBP.setText("");
        TIBP.setText("");
        TCO.setText("");
        TCVP.setText("");
        TPAP.setText("");
        TGambaranEKG.setText("");
        TGambaranVES.setText("");
        TSaturasi.setText("");
        TEtCO2.setText("");
        TGCS_E.setText("");
        TGCS_V.setText("");
        TGCS_M.setText("");
        TGCSTotal.setText("");
        if (cmbPupilKanan.getItemCount() > 0) { cmbPupilKanan.setSelectedIndex(0); }
        if (cmbPupilKiri.getItemCount() > 0) { cmbPupilKiri.setSelectedIndex(0); }
        if (cmbSkalaNyeri.getItemCount() > 0) { cmbSkalaNyeri.setSelectedIndex(0); }
        TNilaiNyeri.setText("");
        if (cmbResikoJatuh.getItemCount() > 0) { cmbResikoJatuh.setSelectedIndex(0); }
        if (cmbKategoriResikoJatuh.getItemCount() > 0) { cmbKategoriResikoJatuh.setSelectedIndex(0); }
        TBredenScore.setText("");
        //NIPObservasi cleared - now using NIPHeader
        //NamaPetugasObservasi cleared - now using NamaPetugasHeader

        // Reset field untuk Tab Ventilator
        TTipe.setText("");
        TRRSetting.setText("");
        TRRAktual.setText("");
        TIERatio.setText("");
        TMVSetting.setText("");
        TMVAktual.setText("");
        TPressureControl.setText("");
        TPressureSupport.setText("");
        TPEEP.setText("");
        TFiO2Flow.setText("");
        TPeak.setText("");
        TPlateauPressure.setText("");
        if (cmbETTTC.getItemCount() > 0) { cmbETTTC.setSelectedIndex(0); }
        TDiameter.setText("");
        TKedalaman.setText("");
        TTekananCuff.setText("");
        //NIPVentilator cleared - now using NIPHeader
        //NamaPetugasVentilator cleared - now using NamaPetugasHeader

        // Reset field untuk Tab Intake Output
        TTransfusiICA.setText("");
        TTransfusiICM.setText("");
        TTransfusiIICA.setText("");
        TTransfusiIICM.setText("");
        TTransfusiIIICACM.setText("");
        TJumlah1JamKumulatif.setText("");
        TMakanOral.setText("");
        TEnteral.setText("");
        TJumlahMakanKumulatif.setText("");
        TParenteral.setText("");
        TParenteralKumulatif.setText("");
        TKumulatif.setText("");
        TUrine.setText("");
        TNGT_OGT.setText("");
        TBAB.setText("");
        TDrainStoma1.setText("");
        TDrainStoma2.setText("");
        TDrainStoma3.setText("");
        TMuntah.setText("");
        TIWAL.setText("");
        TOutputKumulatif.setText("");
        TKeseimbanganCairan.setText("");
        //NIPIntake cleared - now using NIPHeader
        //NamaPetugasIntake cleared - now using NamaPetugasHeader

        // Reset field untuk Tab Catatan Pengobatan
        TKodeDokter.setText("");
        TNamaDokter.setText("");
        TNamaObat.setText("");
        TKekuatan.setText("");
        TBentukSediaan.setText("");
        TAturanPakai.setText("");
        TRutePemberian.setText("");
        TVerifikasiApoteker.setText("");
        // TJam07 dan TJam08 dihapus - menggunakan komponen jam dari form utama
        //NIPMedikasi cleared - now using NIPHeader
        //NamaPetugasMedikasi cleared - now using NamaPetugasHeader

        // Reset field untuk Tab Catatan
        TCatatanISI.setText("");
        if (cmbProfesi.getItemCount() > 0) { cmbProfesi.setSelectedIndex(0); }
        NIPCatatan.setText("");
        NamaPetugasCatatan.setText("");

        // Reset field untuk Tab Implementasi Keperawatan (30 tindakan)
        TKep01.setText(""); TKep02.setText(""); TKep03.setText(""); TKep04.setText(""); TKep05.setText("");
        TKep06.setText(""); TKep07.setText(""); TKep08.setText(""); TKep09.setText(""); TKep10.setText("");
        TKep11.setText(""); TKep12.setText(""); TKep13.setText(""); TKep14.setText(""); TKep15.setText("");
        TKep16.setText(""); TKep17.setText(""); TKep18.setText(""); TKep19.setText(""); TKep20.setText("");
        TKep21.setText(""); TKep22.setText(""); TKep23.setText(""); TKep24.setText(""); TKep25.setText("");
        TKep26.setText(""); TKep27.setText(""); TKep28.setText(""); TKep29.setText(""); TKep30.setText("");
        NIPKeperawatan.setText("");
        NamaPetugasKeperawatan.setText("");

        // Reset field untuk Tab Intake Output Jam
        // Reset textbox jam dihapus - menggunakan komponen jam dari form utama
        // Additional jam fields can be added here

        // Reset field untuk Tab Pemeriksaan Penunjang
        TNamaPemeriksaan.setText("");
        THasilPemeriksaan.setText("");
        NIPPemeriksaanPenunjang.setText("");
        NamaPetugasPemeriksaanPenunjang.setText("");

        // Reset field untuk Tab Kebutuhan Nutrisi dan Cairan
        TBB.setText("");
        TTB.setText("");
        TBMI.setText("");
        TKebutuhanKkal.setText("");
        TKebutuhanKkal24.setText("");
        TKebutuhanDasar.setText("");
        TPersentasePemberian.setText("");
        TPemberianKkal.setText("");
        TIWL.setText("");
        TObatObatan.setText("");
        TCairanTotal.setText("");
        TProtein.setText("");
        TProteinTotal.setText("");
        TKaloriNonProtein.setText("");
        TRasioKH.setText("");
        TRasioLemak.setText("");
        TRasioTotal.setText("");
        TRasioKkal.setText("");
        TBalance.setText("");
        NIPGambar.setText("");
        NamaPetugasGambar.setText("");

        // Reset field untuk Tab Cairan dan Nutrisi
        ChkSSP.setSelected(false);
        ChkJantung.setSelected(false);
        ChkRespirasi.setSelected(false);
        ChkRenal.setSelected(false);
        ChkSaluranPencernaan.setSelected(false);
        ChkHepar.setSelected(false);
        ChkMetabolisme.setSelected(false);
        ChkOral.setSelected(false);
        ChkEnteral.setSelected(false);
        ChkParenteral.setSelected(false);
        ChkKombinasi.setSelected(false);
        TJenisEnteral.setText("");
        TCairanEnteral.setText("");
        TKaloriEnteral.setText("");
        TKHEnteral.setText("");
        TLemakEnteral.setText("");
        TProteinEnteral.setText("");
        TJenisParenteral.setText("");
        TCairanParenteral.setText("");
        TKaloriParenteral.setText("");
        TKHParenteral.setText("");
        TLemakParenteral.setText("");
        TProteinParenteral.setText("");
        TTotalCairan.setText("");
        TTotalKalori.setText("");
        TTotalKH.setText("");
        TTotalLemak.setText("");
        TTotalProtein.setText("");
        NIPCairanNutrisi.setText("");
        NamaPetugasCairanNutrisi.setText("");

        // Reset field untuk Tab Tindakan
        ChkEndotrachealTube.setSelected(false);
        ChkTracheostomyTube.setSelected(false);
        ChkTubingVentilator.setSelected(false);
        ChkNasogastricTube.setSelected(false);
        ChkCentralVenousCatheter.setSelected(false);
        ChkPICC.setSelected(false);
        ChkCloseSuction.setSelected(false);
        ChkArteriLineCatheter.setSelected(false);
        ChkDowerCatheter.setSelected(false);
        ChkInfusPerifer.setSelected(false);
        TPemasanganKe.setText("");
        THariKe.setText("");
        TKeteranganTindakan.setText("");
        NIPTindakan.setText("");
        NamaPetugasTindakan.setText("");

        // Reset field untuk Tab Catatan Terintegrasi (pemeriksaan_ranap)
        TSuhuTubuh.setText("");
        TTensi.setText("");
        TNadi.setText("");
        // TRespirasi already reset in hemodynamic section
        TTinggi.setText("");
        TBerat.setText("");
        TSpO2.setText("");
        // TGCS already reset in hemodynamic section
        if (CmbKesadaran.getItemCount() > 0) { CmbKesadaran.setSelectedIndex(0); }
        TAlergi.setText("");
        TKeluhan.setText("");
        TPemeriksaanRanap.setText("");
        TPenilaian.setText("");
        TRTL.setText("");
        TInstruksi.setText("");
        TEvaluasi.setText("");
        NIPCatatan.setText("");
        NamaPetugasCatatan.setText("");

        // Reset tanggal dan waktu
        Tanggal.setDate(new Date());

        // Set jam ke jam sekarang tapi checkbox tidak tercentang
        Date now = Calendar.getInstance().getTime();
        String jam = String.format("%02d", now.getHours());
        String menit = String.format("%02d", now.getMinutes());
        String detik = String.format("%02d", now.getSeconds());
        Jam.setSelectedItem(jam);
        Menit.setSelectedItem(menit);
        Detik.setSelectedItem(detik);

        TNoRw.requestFocus();
    }

    private void isRawat() {
        try {
            ps = koneksi.prepareStatement(
                    "select reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.tgl_lahir,"
                    + "reg_periksa.tgl_registrasi,reg_periksa.umurdaftar,reg_periksa.sttsumur "
                    + "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                    + "where reg_periksa.no_rawat=?");
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    TPasien.setText(rs.getString("nm_pasien"));
                    TglLahir.setText(rs.getString("tgl_lahir"));
                    Umur.setText(rs.getString("umurdaftar") + " " + rs.getString("sttsumur"));
                    DTPCari1.setDate(rs.getDate("tgl_registrasi"));
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void jam() {
        ActionListener taskPerformer = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date now = Calendar.getInstance().getTime();
                if (ChkJln.isSelected()) {
                    String jam = String.format("%02d", now.getHours());
                    String menit = String.format("%02d", now.getMinutes());
                    String detik = String.format("%02d", now.getSeconds());

                    Jam.setSelectedItem(jam);
                    Menit.setSelectedItem(menit);
                    Detik.setSelectedItem(detik);
                }
            }
        };
        new Timer(1000, taskPerformer).start();
    }

    private void isFormObservasi() {
        if (ChkInputObservasi.isSelected()) {
            ChkInputObservasi.setVisible(false);
            PanelInputObservasi.setPreferredSize(new Dimension(WIDTH, 220));
            panelGlassObservasi.setVisible(true);
            ChkInputObservasi.setVisible(true);
        } else {
            ChkInputObservasi.setVisible(false);
            PanelInputObservasi.setPreferredSize(new Dimension(WIDTH, 20));
            panelGlassObservasi.setVisible(false);
            ChkInputObservasi.setVisible(true);
        }
    }

    private void isFormVentilator() {
        if (ChkInputVentilator.isSelected()) {
            ChkInputVentilator.setVisible(false);
            PanelInputVentilator.setPreferredSize(new Dimension(WIDTH, 260));
            panelGlassVentilator.setVisible(true);
            ChkInputVentilator.setVisible(true);
        } else {
            ChkInputVentilator.setVisible(false);
            PanelInputVentilator.setPreferredSize(new Dimension(WIDTH, 20));
            panelGlassVentilator.setVisible(false);
            ChkInputVentilator.setVisible(true);
        }
    }

    private void isFormIntake() {
        if (ChkInputIntake.isSelected()) {
            ChkInputIntake.setVisible(false);
            PanelInputIntake.setPreferredSize(new Dimension(WIDTH, 70));
            panelGlassIntake.setVisible(true);
            ChkInputIntake.setVisible(true);
        } else {
            ChkInputIntake.setVisible(false);
            PanelInputIntake.setPreferredSize(new Dimension(WIDTH, 20));
            panelGlassIntake.setVisible(false);
            ChkInputIntake.setVisible(true);
        }
    }

    private void isFormMedikasi() {
        if (ChkInputMedikasi.isSelected()) {
            ChkInputMedikasi.setVisible(false);
            PanelInputMedikasi.setPreferredSize(new Dimension(WIDTH, 180));
            panelGlassMedikasi.setVisible(true);
            ChkInputMedikasi.setVisible(true);
        } else {
            ChkInputMedikasi.setVisible(false);
            PanelInputMedikasi.setPreferredSize(new Dimension(WIDTH, 20));
            panelGlassMedikasi.setVisible(false);
            ChkInputMedikasi.setVisible(true);
        }
    }

    // Data retrieval methods
    private void getDataObservasi() {
        if (tbObservasi.getSelectedRow() != -1) {
            int r = tbObservasi.getSelectedRow();
            TNoRw.setText(tbObservasi.getValueAt(r, 1).toString());
            TNoRM.setText(tbObservasi.getValueAt(r, 2).toString());
            TPasien.setText(tbObservasi.getValueAt(r, 3).toString());

            TBPSys.setText(tbObservasi.getValueAt(r, 6).toString());
            TBPDys.setText(tbObservasi.getValueAt(r, 7).toString());
            TMAP.setText(tbObservasi.getValueAt(r, 8).toString());
            THR.setText(tbObservasi.getValueAt(r, 9).toString());
            TTemperature.setText(tbObservasi.getValueAt(r, 10).toString());
            TRespirasi.setText(tbObservasi.getValueAt(r, 11).toString());
            TCVP.setText(tbObservasi.getValueAt(r, 12).toString());
            TGambaranEKG.setText(tbObservasi.getValueAt(r, 13).toString());
            TSaturasi.setText(tbObservasi.getValueAt(r, 14).toString());
            cmbKesadaranObs.setSelectedItem(tbObservasi.getValueAt(r, 15).toString());
            TGCS_E.setText(tbObservasi.getValueAt(r, 16).toString());
            TGCS_V.setText(tbObservasi.getValueAt(r, 17).toString());
            TGCS_M.setText(tbObservasi.getValueAt(r, 18).toString());
            cmbRefleksPupilKanan.setSelectedItem(tbObservasi.getValueAt(r, 19).toString());
            cmbRefleksPupilKiri.setSelectedItem(tbObservasi.getValueAt(r, 20).toString());
            cmbMotorikAKA.setSelectedItem(tbObservasi.getValueAt(r, 21).toString());
            cmbMotorikAKI.setSelectedItem(tbObservasi.getValueAt(r, 22).toString());
            cmbMotorikBKA.setSelectedItem(tbObservasi.getValueAt(r, 23).toString());
            cmbMotorikBKI.setSelectedItem(tbObservasi.getValueAt(r, 24).toString());

            Jam.setSelectedItem(tbObservasi.getValueAt(r, 5).toString().substring(0, 2));
            Menit.setSelectedItem(tbObservasi.getValueAt(r, 5).toString().substring(3, 5));
            Detik.setSelectedItem(tbObservasi.getValueAt(r, 5).toString().substring(6, 8));
            Valid.SetTgl(Tanggal, tbObservasi.getValueAt(r, 4).toString());
        }
    }

    private void getDataVentilator() {
        if (tbVentilator.getSelectedRow() != -1) {
            TNoRw.setText(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 1).toString());
            TNoRM.setText(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 2).toString());
            TPasien.setText(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 3).toString());

            TTipe.setText(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 6).toString());
            TRRSetting.setText(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 7).toString());
            TRRAktual.setText(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 8).toString());
            TIERatio.setText(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 9).toString());
            TMVSetting.setText(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 10).toString());
            TMVAktual.setText(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 11).toString());
            TPressureControl.setText(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 12).toString());
            TPressureSupport.setText(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 13).toString());
            TPEEP.setText(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 14).toString());
            TFiO2Flow.setText(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 15).toString());
            TPeak.setText(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 16).toString());
            TPlateauPressure.setText(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 17).toString());
            cmbETTTC.setSelectedItem(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 18).toString());
            TDiameter.setText(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 19).toString());
            TKedalaman.setText(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 20).toString());
            TTekananCuff.setText(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 21).toString());
            //NIPVentilator - now using NIPHeader from panel atas
            NamaPetugasVentilator.setText(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 23).toString());

            Jam.setSelectedItem(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 5).toString().substring(0, 2));
            Menit.setSelectedItem(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 5).toString().substring(3, 5));
            Detik.setSelectedItem(tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 5).toString().substring(6, 8));
            Valid.SetTgl(Tanggal, tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 4).toString());
        }
    }

    private void getDataIntakeOutput() {
        if (tbIntakeOutput.getSelectedRow() != -1) {
            int r = tbIntakeOutput.getSelectedRow();
            // Kardex: 0=NoRawat,1=Tgl,2=No,3=Tipe,4=NamaCairan,5=Paraf,6-29=Jam,30=NIP,31=Petugas
            Object nr = tabModeIntakeOutput.getValueAt(r, 0);
            if (nr == null || nr.toString().isEmpty()) return; // baris ringkasan
            Object tipe = tabModeIntakeOutput.getValueAt(r, 3);
            if (tipe != null) cmbTipeCairan.setSelectedItem(tipe.toString());
            TNamaCairan.setText(tabModeIntakeOutput.getValueAt(r, 4) == null ? "" : tabModeIntakeOutput.getValueAt(r, 4).toString());
        }
    }

    private void getDataMedikasi() {
        if (tbMedikasi.getSelectedRow() != -1) {
            int r = tbMedikasi.getSelectedRow();
            // Kolom: 0=NoRawat, 1=Tgl, 2=NoUrut, 3=NamaObat, 4=Kekuatan, 5=BentukSediaan,
            // 6=AturanPakai, 7=Rute, 8=ParafDokter, 9-32=Jam, 33=Keterangan, 34=NIP, 35=Petugas
            TNamaObat.setText(tabModeMedikasi.getValueAt(r, 3).toString());
            TKekuatan.setText(tabModeMedikasi.getValueAt(r, 4).toString());
            TBentukSediaan.setText(tabModeMedikasi.getValueAt(r, 5).toString());
            TAturanPakai.setText(tabModeMedikasi.getValueAt(r, 6).toString());
            TRutePemberian.setText(tabModeMedikasi.getValueAt(r, 7).toString());
            TKeterangan.setText(tabModeMedikasi.getValueAt(r, 33) == null ? "" : tabModeMedikasi.getValueAt(r, 33).toString());
        }
    }

    // Database operation methods
    private void simpanObservasi() {
        if (NIPHeader.getText().trim().equals("")) {
            Valid.textKosong(NIPHeader, "Petugas");
        } else {
            if (Sequel.menyimpantf("observasi_icu_hemodinamik", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 23, new String[]{
                TNoRw.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + ""),
                Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                TBPSys.getText(), TBPDys.getText(), TMAP.getText(),
                THR.getText(), TTemperature.getText(), TRespirasi.getText(), TCVP.getText(),
                TGambaranEKG.getText(), TSaturasi.getText(),
                cmbKesadaranObs.getSelectedItem().toString(),
                TGCS_E.getText(), TGCS_V.getText(), TGCS_M.getText(),
                cmbRefleksPupilKanan.getSelectedItem().toString(), cmbRefleksPupilKiri.getSelectedItem().toString(),
                cmbMotorikAKA.getSelectedItem().toString(), cmbMotorikAKI.getSelectedItem().toString(),
                cmbMotorikBKA.getSelectedItem().toString(), cmbMotorikBKI.getSelectedItem().toString(),
                NIPHeader.getText()
            }) == true) {
                tabModeObservasi.addRow(new Object[]{
                    false, TNoRw.getText(), TNoRM.getText(), TPasien.getText(),
                    Valid.SetTgl(Tanggal.getSelectedItem() + ""),
                    Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                    TBPSys.getText(), TBPDys.getText(), TMAP.getText(),
                    THR.getText(), TTemperature.getText(), TRespirasi.getText(), TCVP.getText(),
                    TGambaranEKG.getText(), TSaturasi.getText(),
                    cmbKesadaranObs.getSelectedItem().toString(),
                    TGCS_E.getText(), TGCS_V.getText(), TGCS_M.getText(),
                    cmbRefleksPupilKanan.getSelectedItem().toString(), cmbRefleksPupilKiri.getSelectedItem().toString(),
                    cmbMotorikAKA.getSelectedItem().toString(), cmbMotorikAKI.getSelectedItem().toString(),
                    cmbMotorikBKA.getSelectedItem().toString(), cmbMotorikBKI.getSelectedItem().toString(),
                    NIPHeader.getText(), NamaPetugasHeader.getText()
                });
                LCount.setText("" + tabModeObservasi.getRowCount());
                emptTeks();
            }
        }
    }

    private void simpanMonitoringTambahan() {
        if (NIPHeader.getText().trim().equals("")) {
            Valid.textKosong(NIPHeader, "Petugas");
            return;
        }
        if (Sequel.menyimpantf("observasi_icu_pemantauan_tambahan", "?,?,?,?,?,?,?,?,?,?", "Data", 10, new String[]{
            TNoRw.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + ""),
            Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
            cmbSkalaSedasi.getSelectedItem().toString(), TNilaiSedasi.getText(),
            TCRT.getText(), TLingkarPerut.getText(),
            cmbSekretWarna.getSelectedItem().toString(), cmbSekretKonsistensi.getSelectedItem().toString(),
            NIPHeader.getText()
        }) == true) {
            tabModeMonitoringTambahan.addRow(new Object[]{
                false, TNoRw.getText(), TNoRM.getText(), TPasien.getText(),
                Valid.SetTgl(Tanggal.getSelectedItem() + ""),
                Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                cmbSkalaSedasi.getSelectedItem().toString(), TNilaiSedasi.getText(),
                TCRT.getText(), TLingkarPerut.getText(),
                cmbSekretWarna.getSelectedItem().toString(), cmbSekretKonsistensi.getSelectedItem().toString(),
                NIPHeader.getText(), NamaPetugasHeader.getText()
            });
            LCount.setText("" + tabModeMonitoringTambahan.getRowCount());
            emptTeks();
        }
    }

    private void tampilMonitoringTambahan() {
        Valid.tabelKosong(tabModeMonitoringTambahan);
        try {
            String sql = "select obs.no_rawat,p.no_rkm_medis,p.nm_pasien,obs.tgl_observasi,obs.jam,"
                    + "obs.jenis_skala_sedasi,obs.nilai_skala_sedasi,obs.crt,obs.lingkar_perut,"
                    + "obs.sekret_warna,obs.sekret_konsistensi,obs.nip,IFNULL(pet.nama,obs.nip) "
                    + "from observasi_icu_pemantauan_tambahan obs "
                    + "inner join reg_periksa r on obs.no_rawat=r.no_rawat "
                    + "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "
                    + "left join petugas pet on obs.nip=pet.nip "
                    + "where obs.tgl_observasi between ? and ? ";
            if (!TCari.getText().trim().equals("")) {
                sql += "and (obs.no_rawat like ? or p.no_rkm_medis like ? or p.nm_pasien like ?) ";
            }
            sql += "order by obs.tgl_observasi desc, obs.jam desc";

            ps = koneksi.prepareStatement(sql);
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
            if (!TCari.getText().trim().equals("")) {
                ps.setString(3, "%" + TCari.getText() + "%");
                ps.setString(4, "%" + TCari.getText() + "%");
                ps.setString(5, "%" + TCari.getText() + "%");
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeMonitoringTambahan.addRow(new Object[]{
                    false, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8),
                    rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13)
                });
            }
        } catch (Exception e) {
            System.out.println("Error tampilMonitoringTambahan: " + e);
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (ps != null) ps.close(); } catch (Exception e) {}
        }
        LCount.setText("" + tabModeMonitoringTambahan.getRowCount());
    }

    private void hapusMonitoringTambahan() {
        if (tbMonitoringTambahan.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from observasi_icu_pemantauan_tambahan where no_rawat=? and tgl_observasi=? and jam=?", 3, new String[]{
                tbMonitoringTambahan.getValueAt(tbMonitoringTambahan.getSelectedRow(), 1).toString(),
                tbMonitoringTambahan.getValueAt(tbMonitoringTambahan.getSelectedRow(), 4).toString(),
                tbMonitoringTambahan.getValueAt(tbMonitoringTambahan.getSelectedRow(), 5).toString()
            }) == true) {
                tabModeMonitoringTambahan.removeRow(tbMonitoringTambahan.getSelectedRow());
                LCount.setText("" + tabModeMonitoringTambahan.getRowCount());
                emptTeks();
            }
        }
    }

    private void editMonitoringTambahan() {
        if (tbMonitoringTambahan.getSelectedRow() <= -1) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang akan diedit terlebih dahulu!");
            return;
        }
        if (NIPHeader.getText().trim().equals("")) {
            Valid.textKosong(NIPHeader, "Petugas");
            return;
        }
        if (Sequel.mengedittf("observasi_icu_pemantauan_tambahan", "no_rawat=? and tgl_observasi=? and jam=?",
            "jenis_skala_sedasi=?,nilai_skala_sedasi=?,crt=?,lingkar_perut=?,sekret_warna=?,sekret_konsistensi=?,nip=?", 10, new String[]{
            cmbSkalaSedasi.getSelectedItem().toString(), TNilaiSedasi.getText(),
            TCRT.getText(), TLingkarPerut.getText(),
            cmbSekretWarna.getSelectedItem().toString(), cmbSekretKonsistensi.getSelectedItem().toString(),
            NIPHeader.getText(),
            tbMonitoringTambahan.getValueAt(tbMonitoringTambahan.getSelectedRow(), 1).toString(),
            tbMonitoringTambahan.getValueAt(tbMonitoringTambahan.getSelectedRow(), 4).toString(),
            tbMonitoringTambahan.getValueAt(tbMonitoringTambahan.getSelectedRow(), 5).toString()
        }) == true) {
            tampilMonitoringTambahan();
            emptTeks();
        }
    }

    private void simpanVentilator() {
        if (NIPHeader.getText().trim().equals("")) {
            Valid.textKosong(NIPHeader, "Petugas");
        } else {
            if (Sequel.menyimpantf("observasi_icu_ventilator", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 20, new String[]{
                TNoRw.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + ""),
                Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                TTipe.getText(), TRRSetting.getText(), TRRAktual.getText(),
                TIERatio.getText(), TMVSetting.getText(), TMVAktual.getText(),
                TPressureControl.getText(), TPressureSupport.getText(), TPEEP.getText(), TFiO2Flow.getText(),
                TPeak.getText(), TPlateauPressure.getText(), cmbETTTC.getSelectedItem().toString(),
                TDiameter.getText(), TKedalaman.getText(), TTekananCuff.getText(), NIPHeader.getText()
            }) == true) {
                tabModeVentilator.addRow(new Object[]{
                    false, TNoRw.getText(), TNoRM.getText(), TPasien.getText(),
                    Valid.SetTgl(Tanggal.getSelectedItem() + ""),
                    Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                    TTipe.getText(), TRRSetting.getText(), TRRAktual.getText(),
                    TIERatio.getText(), TMVSetting.getText(), TMVAktual.getText(),
                    TPressureControl.getText(), TPressureSupport.getText(), TPEEP.getText(), TFiO2Flow.getText(),
                    TPeak.getText(), TPlateauPressure.getText(), cmbETTTC.getSelectedItem().toString(),
                    TDiameter.getText(), TKedalaman.getText(), TTekananCuff.getText(),
                    NIPHeader.getText(), NamaPetugasHeader.getText()
                });
                LCount.setText("" + tabModeVentilator.getRowCount());
                emptTeks();
            }
        }
    }

    private void simpanIntakeOutput() {
        if (NIPHeader.getText().trim().equals("")) {
            Valid.textKosong(NIPHeader, "Petugas");
        } else if (TNamaCairan.getText().trim().equals("")) {
            Valid.textKosong(TNamaCairan, "Nama Cairan");
        } else {
            String tgl = Valid.SetTgl(Tanggal.getSelectedItem() + "");
            String maxUrut = Sequel.cariIsi("select IFNULL(MAX(no_urut),0) from observasi_icu_cairan where no_rawat=? and tgl_observasi='" + tgl + "'", TNoRw.getText());
            int noUrut = Integer.parseInt(maxUrut.isEmpty() ? "0" : maxUrut) + 1;

            // Kolom: no_rawat,tgl_observasi,no_urut,tipe,nama_cairan,
            //        [paraf_perawat='',nip_perawat='',tgl_paraf=NULL],nip,waktu_input=NULL
            if (Sequel.menyimpantf("observasi_icu_cairan",
                "?,?,?,?,?,'','',NULL,?,NULL", "Data", 6, new String[]{
                TNoRw.getText(), tgl, String.valueOf(noUrut),
                cmbTipeCairan.getSelectedItem().toString(), TNamaCairan.getText(), NIPHeader.getText()
            })) {
                tampilIntakeOutput();
                TNamaCairan.setText("");
            }
        }
    }

    private void simpanMedikasi() {
        if (NIPHeader.getText().trim().equals("")) {
            Valid.textKosong(NIPHeader, "Petugas");
        } else if (TNamaObat.getText().trim().equals("")) {
            Valid.textKosong(TNamaObat, "Nama Obat");
        } else {
            String tgl = Valid.SetTgl(Tanggal.getSelectedItem() + "");
            // Auto no_urut
            String maxUrut = Sequel.cariIsi("select IFNULL(MAX(no_urut),0) from observasi_icu_catatan_pengobatan where no_rawat=? and tgl_observasi='" + tgl + "'", TNoRw.getText());
            int noUrut = Integer.parseInt(maxUrut.isEmpty() ? "0" : maxUrut) + 1;

            // Kolom: no_rawat,tgl_observasi,no_urut,nama_obat,kekuatan,bentuk_sediaan,
            //        aturan_pakai,rute_pemberian,keterangan,[paraf_dokter='',kd_dokter='',
            //        nm_dokter='',tgl_paraf=NULL],nip,waktu_input=NULL
            if (Sequel.menyimpantf("observasi_icu_catatan_pengobatan",
                "?,?,?,?,?,?,?,?,?,'','','',NULL,?,NULL", "Data", 10, new String[]{
                TNoRw.getText(), tgl, String.valueOf(noUrut),
                TNamaObat.getText(), TKekuatan.getText(), TBentukSediaan.getText(),
                TAturanPakai.getText(), TRutePemberian.getText(), TKeterangan.getText(),
                NIPHeader.getText()
            })) {
                // Auto-schedule cell jam dari aturan pakai sebelum tampilMedikasi
                // sehingga row baru muncul dengan "○" planned di jam terjadwal.
                // Pakai prepared statement (queryu2) — konsisten dgn simpanDetailJam
                // dan ngga rawan SQL injection. Set nip_petugas = NIPHeader.
                String[] jamTerjadwal = parseJadwalJamMedikasi(TAturanPakai.getText());
                for (String jam : jamTerjadwal) {
                    Sequel.queryu2("insert into observasi_icu_catatan_pengobatan_detail "
                        + "(no_rawat,tgl_observasi,no_urut,jam,status,nip_petugas) "
                        + "values (?,?,?,?,?,?) on duplicate key update status=values(status)",
                        6, new String[]{TNoRw.getText(), tgl, String.valueOf(noUrut), jam, "○", NIPHeader.getText()});
                }
                tampilMedikasi();
                emptTeksMedikasi();
            }
        }
    }

    /**
     * Parse aturan pakai (mis. "3x1", "tiap 8 jam", "q6h", "PRN") menjadi
     * daftar jam pemberian (format "07","08",...,"06"). Default jam pertama 08:00.
     *
     * Mapping standar:
     *   1x1 → 08
     *   2x1 → 08, 20  (tiap 12 jam)
     *   3x1 → 08, 16, 24  (tiap 8 jam)
     *   4x1 → 06, 12, 18, 24  (tiap 6 jam)
     *   6x1 → 06, 10, 14, 18, 22, 02  (tiap 4 jam)
     *   PRN/K.P → tidak dijadwalkan, perawat tap manual
     */
    private String[] parseJadwalJamMedikasi(String aturan) {
        if (aturan == null) return new String[0];
        String a = aturan.toLowerCase().replaceAll("\\s+", "");
        if (a.contains("prn") || a.contains("k/p") || a.contains("kalauperlu")) {
            return new String[0];
        }
        // Coba match "Nx1" atau "qNh" untuk dapat frekuensi/interval
        int freq = 0;
        java.util.regex.Matcher m;
        m = java.util.regex.Pattern.compile("(\\d+)x\\d*").matcher(a);
        if (m.find()) { try { freq = Integer.parseInt(m.group(1)); } catch (Exception ex) {} }
        if (freq == 0) {
            m = java.util.regex.Pattern.compile("q(\\d+)h|tiap(\\d+)jam|setiap(\\d+)jam").matcher(a);
            if (m.find()) {
                String h = m.group(1) != null ? m.group(1) : (m.group(2) != null ? m.group(2) : m.group(3));
                try { int interval = Integer.parseInt(h); if (interval > 0) freq = 24 / interval; } catch (Exception ex) {}
            }
        }
        switch (freq) {
            case 1: return new String[]{"08"};
            case 2: return new String[]{"08","20"};
            case 3: return new String[]{"08","16","24"};
            case 4: return new String[]{"06","12","18","24"};
            case 6: return new String[]{"06","10","14","18","22","02"};
            case 8: return new String[]{"06","09","12","15","18","21","24","03"};
            case 12:return new String[]{"06","08","10","12","14","16","18","20","22","24","02","04"};
            default: return new String[0];
        }
    }

    // ===== AUTO-KALKULASI INTAKE-OUTPUT =====
    private void calculateIntakeOutput() {
        try {
            // === INTAKE ===
            // Jumlah 1 Jam Kumulatif = Transfusi I CA + I CM + II CA + II CM + III CA/CM
            double tICA = parseDoubleOrZero(TTransfusiICA.getText());
            double tICM = parseDoubleOrZero(TTransfusiICM.getText());
            double tIICA = parseDoubleOrZero(TTransfusiIICA.getText());
            double tIICM = parseDoubleOrZero(TTransfusiIICM.getText());
            double tIII = parseDoubleOrZero(TTransfusiIIICACM.getText());
            double jumlah1JamKum = tICA + tICM + tIICA + tIICM + tIII;
            TJumlah1JamKumulatif.setText(String.format("%.0f", jumlah1JamKum));

            // Jumlah Makan Kumulatif = Makan Oral + Enteral
            double oral = parseDoubleOrZero(TMakanOral.getText());
            double enteral = parseDoubleOrZero(TEnteral.getText());
            double jumlahMakanKum = oral + enteral;
            TJumlahMakanKumulatif.setText(String.format("%.0f", jumlahMakanKum));

            // Parenteral Kumulatif (manual atau sama dengan Parenteral)
            double parenteral = parseDoubleOrZero(TParenteral.getText());
            double parenteralKum = parseDoubleOrZero(TParenteralKumulatif.getText());
            if (parenteralKum == 0 && parenteral > 0) {
                parenteralKum = parenteral;
                TParenteralKumulatif.setText(String.format("%.0f", parenteralKum));
            }

            // Total Kumulatif Intake = Jumlah 1 Jam Kum + Jumlah Makan Kum + Parenteral Kum
            double totalIntake = jumlah1JamKum + jumlahMakanKum + parenteralKum;
            TKumulatif.setText(String.format("%.0f", totalIntake));

            // === OUTPUT ===
            double urine = parseDoubleOrZero(TUrine.getText());
            double ngtOgt = parseDoubleOrZero(TNGT_OGT.getText());
            double bab = parseDoubleOrZero(TBAB.getText());
            double drain1 = parseDoubleOrZero(TDrainStoma1.getText());
            double drain2 = parseDoubleOrZero(TDrainStoma2.getText());
            double drain3 = parseDoubleOrZero(TDrainStoma3.getText());
            double muntah = parseDoubleOrZero(TMuntah.getText());
            double iwal = parseDoubleOrZero(TIWAL.getText());
            double totalOutput = urine + ngtOgt + bab + drain1 + drain2 + drain3 + muntah + iwal;
            TOutputKumulatif.setText(String.format("%.0f", totalOutput));

            // === KESEIMBANGAN CAIRAN = Total Intake - Total Output ===
            double balance = totalIntake - totalOutput;
            TKeseimbanganCairan.setText(String.format("%.0f", balance));
        } catch (Exception e) {
            // silent
        }
    }

    private double parseDoubleOrZero(String text) {
        if (text == null || text.trim().isEmpty()) return 0;
        try { return Double.parseDouble(text.trim()); } catch (NumberFormatException e) { return 0; }
    }

    private void emptTeksMedikasi() {
        TNamaObat.setText("");
        TKekuatan.setText("");
        TBentukSediaan.setText("");
        TAturanPakai.setText("");
        TRutePemberian.setText("");
        TKeterangan.setText("");
    }

    private void simpanCatatan() {
        if (KdPegCatatan.getText().trim().equals("")) {
            Valid.textKosong(KdPegCatatan, "Pegawai");
        } else {
            if (Sequel.menyimpantf("pemeriksaan_ranap", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 20, new String[]{
                TNoRw.getText(),
                Valid.SetTgl(Tanggal.getSelectedItem() + ""),
                Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                TSuhuTubuh.getText(), TTensi.getText(), TNadi.getText(), TRespirasi.getText(),
                TTinggi.getText(), TBerat.getText(), TSpO2.getText(), "",
                CmbKesadaran.getSelectedItem().toString(),
                TKeluhan.getText(), TPemeriksaanRanap.getText(), TAlergi.getText(),
                TPenilaian.getText(), TRTL.getText(), TInstruksi.getText(), TEvaluasi.getText(),
                KdPegCatatan.getText()
            })) {
                tampilCatatan();
                emptTeksCatatan();
            }
        }
    }

    // Display methods
    private void tampilObservasi() {
        Valid.tabelKosong(tabModeObservasi);
        try {
            String sql = "select obs.no_rawat,p.no_rkm_medis,p.nm_pasien,obs.tgl_observasi,obs.jam,"
                    + "obs.bp_sistol,obs.bp_diastol,obs.map,"
                    + "obs.hr,obs.suhu,obs.rr,obs.cvp,"
                    + "obs.gambaran_ekg,obs.saturasi_o2,"
                    + "obs.kesadaran,obs.gcs_e,obs.gcs_v,obs.gcs_m,"
                    + "obs.refleks_pupil_kanan,obs.refleks_pupil_kiri,"
                    + "obs.motorik_aka,obs.motorik_aki,obs.motorik_bka,obs.motorik_bki,"
                    + "obs.nip,IFNULL(pet.nama,obs.nip) "
                    + "from observasi_icu_hemodinamik obs "
                    + "inner join reg_periksa r on obs.no_rawat=r.no_rawat "
                    + "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "
                    + "left join petugas pet on obs.nip=pet.nip "
                    + "where obs.tgl_observasi between ? and ? ";

            if (!TCari.getText().trim().equals("")) {
                sql += "and (obs.no_rawat like ? or p.no_rkm_medis like ? or p.nm_pasien like ?) ";
            }
            sql += "order by obs.tgl_observasi desc, obs.jam desc";

            ps = koneksi.prepareStatement(sql);
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));

            if (!TCari.getText().trim().equals("")) {
                ps.setString(3, "%" + TCari.getText() + "%");
                ps.setString(4, "%" + TCari.getText() + "%");
                ps.setString(5, "%" + TCari.getText() + "%");
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeObservasi.addRow(new Object[]{
                    false, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8),
                    rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12),
                    rs.getString(13), rs.getString(14), rs.getString(15), rs.getString(16),
                    rs.getString(17), rs.getString(18), rs.getString(19), rs.getString(20),
                    rs.getString(21), rs.getString(22), rs.getString(23), rs.getString(24),
                    rs.getString(25), rs.getString(26)
                });
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
            }
        }
        LCount.setText("" + tabModeObservasi.getRowCount());
    }

    private void tampilVentilator() {
        Valid.tabelKosong(tabModeVentilator);
        try {
            String sql = "select vent.no_rawat,p.no_rkm_medis,p.nm_pasien,vent.tgl_observasi,vent.jam_observasi,"
                    + "vent.tipe_ventilator,vent.rr_setting,vent.rr_aktual,vent.ie_ratio,"
                    + "vent.mv_setting,vent.mv_aktual,vent.pressure_control,vent.pressure_support,vent.peep,"
                    + "vent.fio2_flow,vent.peak_pressure,vent.plateau_pressure,vent.ett_tc,"
                    + "vent.diameter,vent.kedalaman,vent.tekanan_cuff,vent.nip,IFNULL(pet.nama,vent.nip) "
                    + "from observasi_icu_ventilator vent "
                    + "inner join reg_periksa r on vent.no_rawat=r.no_rawat "
                    + "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "
                    + "left join petugas pet on vent.nip=pet.nip "
                    + "where vent.tgl_observasi between ? and ? ";

            if (!TCari.getText().trim().equals("")) {
                sql += "and (vent.no_rawat like ? or p.no_rkm_medis like ? or p.nm_pasien like ?) ";
            }
            sql += "order by vent.tgl_observasi desc, vent.jam_observasi desc";

            ps = koneksi.prepareStatement(sql);
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));

            if (!TCari.getText().trim().equals("")) {
                ps.setString(3, "%" + TCari.getText() + "%");
                ps.setString(4, "%" + TCari.getText() + "%");
                ps.setString(5, "%" + TCari.getText() + "%");
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeVentilator.addRow(new Object[]{
                    false, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8),
                    rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12),
                    rs.getString(13), rs.getString(14), rs.getString(15), rs.getString(16),
                    rs.getString(17), rs.getString(18), rs.getString(19), rs.getString(20),
                    rs.getString(21), rs.getString(22), rs.getString(23)
                });
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
            }
        }
        LCount.setText("" + tabModeVentilator.getRowCount());
    }

    private void tampilIntakeOutput() {
        Valid.tabelKosong(tabModeIntakeOutput);
        try {
            String[] jamLabels = JAM_LABELS;
            ps = koneksi.prepareStatement(
                "select c.no_rawat,c.tgl_observasi,c.no_urut,c.tipe,c.nama_cairan,c.paraf_perawat,"
                + "c.nip,IFNULL(pet.nama,c.nip) "
                + "from observasi_icu_cairan c "
                + "left join petugas pet on c.nip=pet.nip "
                + "where c.no_rawat=? order by c.tgl_observasi,c.no_urut");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[32];
                row[0] = rs.getString(1);  // no_rawat
                row[1] = rs.getString(2);  // tgl_observasi
                row[2] = rs.getString(3);  // no_urut
                row[3] = rs.getString(4);  // tipe
                row[4] = rs.getString(5);  // nama_cairan
                row[5] = "Y".equalsIgnoreCase(rs.getString(6)); // paraf_perawat (Boolean)
                for (int j = 6; j <= 29; j++) row[j] = "";       // jam 07-06 default kosong
                row[30] = rs.getString(7); // nip
                row[31] = rs.getString(8); // petugas

                PreparedStatement psD = koneksi.prepareStatement(
                    "select jam, jumlah from observasi_icu_cairan_detail "
                    + "where no_rawat=? and tgl_observasi=? and no_urut=?");
                psD.setString(1, rs.getString(1));
                psD.setString(2, rs.getString(2));
                psD.setString(3, rs.getString(3));
                ResultSet rsD = psD.executeQuery();
                while (rsD.next()) {
                    String jam = rsD.getString(1);
                    String jumlah = rsD.getString(2);
                    for (int j = 0; j < jamLabels.length; j++) {
                        if (jamLabels[j].equals(jam)) { row[6 + j] = jumlah; break; }
                    }
                }
                rsD.close(); psD.close();

                tabModeIntakeOutput.addRow(row);
            }
            rs.close(); ps.close();
            hitungRingkasanCairan();
        } catch (Exception e) {
            System.out.println("Notifikasi tampilIntakeOutput: " + e);
        }
        LCount.setText("" + tabModeIntakeOutput.getRowCount());
    }

    private void tampilMedikasi() {
        Valid.tabelKosong(tabModeMedikasi);
        try {
            // Pakai konstanta JAM_LABELS (24 jam: 07..23,24,01..06) — match header
            // tabel & simpanDetailJam. Sebelumnya local array kurang 04/05/06.
            String[] jamLabels = JAM_LABELS;

            ps = koneksi.prepareStatement(
                "select m.no_rawat,m.tgl_observasi,m.no_urut,m.nama_obat,m.kekuatan,m.bentuk_sediaan,"
                + "m.aturan_pakai,m.rute_pemberian,m.paraf_dokter,m.keterangan,"
                + "m.nip,IFNULL(pet.nama,m.nip) "
                + "from observasi_icu_catatan_pengobatan m "
                + "left join petugas pet on m.nip=pet.nip "
                + "where m.no_rawat=? order by m.tgl_observasi,m.no_urut");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[36];
                row[0] = rs.getString(1);  // no_rawat
                row[1] = rs.getString(2);  // tgl_observasi
                row[2] = rs.getString(3);  // no_urut
                row[3] = rs.getString(4);  // nama_obat
                row[4] = rs.getString(5);  // kekuatan
                row[5] = rs.getString(6);  // bentuk_sediaan
                row[6] = rs.getString(7);  // aturan_pakai
                row[7] = rs.getString(8);  // rute
                row[8] = "Y".equalsIgnoreCase(rs.getString(9)); // paraf_dokter (checkbox Boolean)
                // Kolom jam 9-32 default kosong (07-06)
                for (int j = 9; j <= 32; j++) row[j] = "";
                row[33] = rs.getString(10); // keterangan (setelah jam)
                row[34] = rs.getString(11); // nip
                row[35] = rs.getString(12); // petugas

                // Query detail jam untuk baris ini
                PreparedStatement psD = koneksi.prepareStatement(
                    "select jam, status from observasi_icu_catatan_pengobatan_detail "
                    + "where no_rawat=? and tgl_observasi=? and no_urut=?");
                psD.setString(1, rs.getString(1));
                psD.setString(2, rs.getString(2));
                psD.setString(3, rs.getString(3));
                ResultSet rsD = psD.executeQuery();
                while (rsD.next()) {
                    String jam = rsD.getString(1);
                    String status = rsD.getString(2);
                    for (int j = 0; j < jamLabels.length; j++) {
                        if (jamLabels[j].equals(jam)) {
                            row[9 + j] = status;
                            break;
                        }
                    }
                }
                rsD.close(); psD.close();

                tabModeMedikasi.addRow(row);
            }
            rs.close(); ps.close();
        } catch (Exception e) {
            System.out.println("Notifikasi tampilMedikasi: " + e);
        }
        LCount.setText("" + tabModeMedikasi.getRowCount());
    }

    private void tampilCatatan() {
        Valid.tabelKosong(tabModeCatatan);
        try {
            ps = koneksi.prepareStatement(
                "select pr.no_rawat,pr.tgl_perawatan,pr.jam_rawat,pr.suhu_tubuh,pr.tensi,pr.nadi,pr.respirasi,"
                + "pr.tinggi,pr.berat,pr.spo2,pr.kesadaran,pr.keluhan,pr.pemeriksaan,"
                + "pr.alergi,pr.penilaian,pr.rtl,pr.instruksi,pr.evaluasi,pr.nip,IFNULL(pg.nama,pr.nip) "
                + "from pemeriksaan_ranap pr "
                + "left join pegawai pg on pr.nip=pg.nik "
                + "where pr.no_rawat=? order by pr.tgl_perawatan desc, pr.jam_rawat desc");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeCatatan.addRow(new Object[]{
                    rs.getString("no_rawat"), rs.getString("tgl_perawatan"), rs.getString("jam_rawat"),
                    rs.getString("suhu_tubuh"), rs.getString("tensi"), rs.getString("nadi"), rs.getString("respirasi"),
                    rs.getString("tinggi"), rs.getString("berat"), rs.getString("spo2"), rs.getString("kesadaran"),
                    rs.getString("keluhan"), rs.getString("pemeriksaan"), rs.getString("alergi"),
                    rs.getString("penilaian"), rs.getString("rtl"), rs.getString("instruksi"), rs.getString("evaluasi"),
                    rs.getString("nip"), rs.getString(20)
                });
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println("Notifikasi tampilCatatan: " + e);
        }
        LCount.setText("" + tabModeCatatan.getRowCount());
    }

    // Delete methods
    private void hapusObservasi() {
        if (tbObservasi.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from observasi_icu_hemodinamik where no_rawat=? and tgl_observasi=? and jam=?", 3, new String[]{
                tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 1).toString(),
                tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 4).toString(),
                tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 5).toString()
            }) == true) {
                tabModeObservasi.removeRow(tbObservasi.getSelectedRow());
                LCount.setText("" + tabModeObservasi.getRowCount());
                emptTeks();
            }
        }
    }

    private void hapusVentilator() {
        if (tbVentilator.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from observasi_icu_ventilator where no_rawat=? and tgl_observasi=? and jam_observasi=?", 3, new String[]{
                tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 1).toString(),
                tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 4).toString(),
                tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 5).toString()
            }) == true) {
                tabModeVentilator.removeRow(tbVentilator.getSelectedRow());
                LCount.setText("" + tabModeVentilator.getRowCount());
                emptTeks();
            }
        }
    }

    private void hapusIntakeOutput() {
        if (tbIntakeOutput.getSelectedRow() > -1) {
            int row = tbIntakeOutput.getSelectedRow();
            Object nrObj = tabModeIntakeOutput.getValueAt(row, 0);
            if (nrObj == null || nrObj.toString().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Baris ringkasan (Total/Balance/Diuresis) tidak bisa dihapus.");
                return;
            }
            String noRawat = nrObj.toString();
            String tglObs  = tabModeIntakeOutput.getValueAt(row, 1).toString();
            String noUrut  = tabModeIntakeOutput.getValueAt(row, 2).toString();

            // Validasi 1: blokir hapus bila Paraf Perawat sudah tercentang
            String paraf = Sequel.cariIsi("select paraf_perawat from observasi_icu_cairan "
                + "where no_rawat=? and tgl_observasi='" + tglObs + "' and no_urut='" + noUrut + "'", noRawat);
            if ("Y".equalsIgnoreCase(paraf)) {
                JOptionPane.showMessageDialog(this,
                    "Cairan ini sudah diparaf perawat.\n"
                    + "Perawat pembuat paraf harus membatalkan paraf dulu, baru bisa dihapus.");
                return;
            }
            // Validasi 2: blokir hapus bila sudah ada isian jumlah cairan di jam
            String adaIsi = Sequel.cariIsi("select count(*) from observasi_icu_cairan_detail "
                + "where no_rawat=? and tgl_observasi='" + tglObs + "' and no_urut='" + noUrut + "' and jumlah<>''", noRawat);
            if (!adaIsi.isEmpty() && Integer.parseInt(adaIsi) > 0) {
                JOptionPane.showMessageDialog(this,
                    "Cairan ini sudah memiliki isian jumlah (ml) di jam.\n"
                    + "Hapus dulu isian jamnya (oleh petugas pembuatnya), baru bisa dihapus.");
                return;
            }
            if (Sequel.queryu2tf("delete from observasi_icu_cairan where no_rawat=? and tgl_observasi=? and no_urut=?", 3, new String[]{
                noRawat, tglObs, noUrut
            })) {
                tampilIntakeOutput();
                TNamaCairan.setText("");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus terlebih dahulu");
        }
    }

    private void hapusMedikasi() {
        if (tbMedikasi.getSelectedRow() > -1) {
            int row = tbMedikasi.getSelectedRow();
            String noRawat = tabModeMedikasi.getValueAt(row, 0).toString();
            String tglObs  = tabModeMedikasi.getValueAt(row, 1).toString();
            String noUrut  = tabModeMedikasi.getValueAt(row, 2).toString();

            // Validasi 1: blokir hapus bila Paraf Dokter sudah tercentang.
            String parafDokter = Sequel.cariIsi(
                "select paraf_dokter from observasi_icu_catatan_pengobatan "
                + "where no_rawat=? and tgl_observasi='" + tglObs + "' and no_urut='" + noUrut + "'", noRawat);
            if ("Y".equalsIgnoreCase(parafDokter)) {
                JOptionPane.showMessageDialog(this,
                    "Obat ini sudah diparaf dokter.\n"
                    + "Dokter pembuat paraf harus membatalkan paraf dulu, baru obat bisa dihapus.");
                return;
            }

            // Validasi 2: blokir hapus bila obat sudah punya isian centang/status
            // pemberian (selain '○' terjadwal otomatis & kosong).
            String adaCentang = Sequel.cariIsi(
                "select count(*) from observasi_icu_catatan_pengobatan_detail "
                + "where no_rawat=? and tgl_observasi='" + tglObs + "' and no_urut='" + noUrut + "' "
                + "and status<>'' and status<>'○'", noRawat);
            if (!adaCentang.isEmpty() && Integer.parseInt(adaCentang) > 0) {
                JOptionPane.showMessageDialog(this,
                    "Obat ini sudah memiliki isian centang/status pemberian.\n"
                    + "Hapus dulu seluruh centang pada jamnya (oleh petugas pembuatnya), "
                    + "baru obat bisa dihapus.");
                return;
            }

            // CASCADE delete: detail (jam terjadwal) otomatis terhapus
            if (Sequel.queryu2tf("delete from observasi_icu_catatan_pengobatan where no_rawat=? and tgl_observasi=? and no_urut=?", 3, new String[]{
                noRawat, tglObs, noUrut
            })) {
                tampilMedikasi();
                emptTeksMedikasi();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus terlebih dahulu");
        }
    }

    private void hapusCatatan() {
        if (tbCatatan.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from pemeriksaan_ranap where no_rawat=? and tgl_perawatan=? and jam_rawat=?", 3, new String[]{
                tbCatatan.getValueAt(tbCatatan.getSelectedRow(), 0).toString(),
                tbCatatan.getValueAt(tbCatatan.getSelectedRow(), 1).toString(),
                tbCatatan.getValueAt(tbCatatan.getSelectedRow(), 2).toString()
            })) {
                tampilCatatan();
                emptTeksCatatan();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Pilih data yang akan dihapus terlebih dahulu..!!");
        }
    }

    // Edit methods
    private void editObservasi() {
        if (tbObservasi.getSelectedRow() > -1) {
            if (NIPHeader.getText().trim().equals("")) {
                Valid.textKosong(NIPHeader, "Petugas");
            } else {
                if (Sequel.mengedittf("observasi_icu_hemodinamik", "no_rawat=? and tgl_observasi=? and jam=?",
                    "bp_sistol=?,bp_diastol=?,map=?,hr=?,suhu=?,rr=?,cvp=?,gambaran_ekg=?,saturasi_o2=?,kesadaran=?,gcs_e=?,gcs_v=?,gcs_m=?,refleks_pupil_kanan=?,refleks_pupil_kiri=?,motorik_aka=?,motorik_aki=?,motorik_bka=?,motorik_bki=?,nip=?", 23, new String[]{
                    TBPSys.getText(), TBPDys.getText(), TMAP.getText(),
                    THR.getText(), TTemperature.getText(), TRespirasi.getText(), TCVP.getText(),
                    TGambaranEKG.getText(), TSaturasi.getText(),
                    cmbKesadaranObs.getSelectedItem().toString(),
                    TGCS_E.getText(), TGCS_V.getText(), TGCS_M.getText(),
                    cmbRefleksPupilKanan.getSelectedItem().toString(), cmbRefleksPupilKiri.getSelectedItem().toString(),
                    cmbMotorikAKA.getSelectedItem().toString(), cmbMotorikAKI.getSelectedItem().toString(),
                    cmbMotorikBKA.getSelectedItem().toString(), cmbMotorikBKI.getSelectedItem().toString(),
                    NIPHeader.getText(),
                    tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 1).toString(),
                    tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 4).toString(),
                    tbObservasi.getValueAt(tbObservasi.getSelectedRow(), 5).toString()
                }) == true) {
                    tampilObservasi();
                    emptTeks();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang akan diedit terlebih dahulu!");
        }
    }

    private void editVentilator() {
        if (tbVentilator.getSelectedRow() > -1) {
            if (NIPHeader.getText().trim().equals("")) {
                Valid.textKosong(NIPHeader, "Petugas");
            } else {
                if (Sequel.mengedittf("observasi_icu_ventilator", "no_rawat=? and tgl_observasi=? and jam_observasi=?", "tipe_ventilator=?,rr_setting=?,rr_aktual=?,ie_ratio=?,mv_setting=?,mv_aktual=?,pressure_control=?,pressure_support=?,peep=?,fio2_flow=?,peak_pressure=?,plateau_pressure=?,ett_tc=?,diameter=?,kedalaman=?,tekanan_cuff=?,nip=?", 20, new String[]{
                    TTipe.getText(), TRRSetting.getText(), TRRAktual.getText(),
                    TIERatio.getText(), TMVSetting.getText(), TMVAktual.getText(),
                    TPressureControl.getText(), TPressureSupport.getText(), TPEEP.getText(), TFiO2Flow.getText(),
                    TPeak.getText(), TPlateauPressure.getText(), cmbETTTC.getSelectedItem().toString(),
                    TDiameter.getText(), TKedalaman.getText(), TTekananCuff.getText(), NIPHeader.getText(),
                    tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 1).toString(),
                    tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 4).toString(),
                    tbVentilator.getValueAt(tbVentilator.getSelectedRow(), 5).toString()
                }) == true) {
                    tampilVentilator();
                    emptTeks();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang akan diedit terlebih dahulu!");
        }
    }

    private void editIntakeOutput() {
        if (tbIntakeOutput.getSelectedRow() > -1) {
            if (NIPHeader.getText().trim().equals("")) {
                Valid.textKosong(NIPHeader, "Petugas");
            } else if (TNamaCairan.getText().trim().equals("")) {
                Valid.textKosong(TNamaCairan, "Nama Cairan");
            } else {
                int r = tbIntakeOutput.getSelectedRow();
                Object nr = tabModeIntakeOutput.getValueAt(r, 0);
                if (nr == null || nr.toString().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Baris ringkasan tidak bisa diedit.");
                    return;
                }
                String noRawat = nr.toString();
                String tglObs  = tabModeIntakeOutput.getValueAt(r, 1).toString();
                String noUrut  = tabModeIntakeOutput.getValueAt(r, 2).toString();
                if (Sequel.mengedittf("observasi_icu_cairan",
                    "no_rawat=? and tgl_observasi=? and no_urut=?",
                    "tipe=?,nama_cairan=?,nip=?", 6, new String[]{
                    cmbTipeCairan.getSelectedItem().toString(), TNamaCairan.getText(), NIPHeader.getText(),
                    noRawat, tglObs, noUrut
                })) {
                    tampilIntakeOutput();
                    TNamaCairan.setText("");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Pilih data yang akan diedit terlebih dahulu..!!");
        }
    }

    private void editMedikasi() {
        if (tbMedikasi.getSelectedRow() > -1) {
            if (NIPHeader.getText().trim().equals("")) {
                Valid.textKosong(NIPHeader, "Petugas");
            } else {
                int r = tbMedikasi.getSelectedRow();
                String noRawat = tabModeMedikasi.getValueAt(r, 0).toString();
                String tglObs  = tabModeMedikasi.getValueAt(r, 1).toString();
                String noUrut  = tabModeMedikasi.getValueAt(r, 2).toString();
                String aturanLama = tabModeMedikasi.getValueAt(r, 6) == null ? "" : tabModeMedikasi.getValueAt(r, 6).toString();
                String aturanBaru = TAturanPakai.getText();

                if (Sequel.mengedittf("observasi_icu_catatan_pengobatan",
                    "no_rawat=? and tgl_observasi=? and no_urut=?",
                    "nama_obat=?,kekuatan=?,bentuk_sediaan=?,aturan_pakai=?,rute_pemberian=?,keterangan=?,nip=?", 10, new String[]{
                    TNamaObat.getText(), TKekuatan.getText(), TBentukSediaan.getText(),
                    aturanBaru, TRutePemberian.getText(), TKeterangan.getText(),
                    NIPHeader.getText(),
                    noRawat, tglObs, noUrut
                })) {
                    // Regenerate jam detail kalau aturan_pakai berubah: hapus row "○"
                    // (planned) lama, generate ulang dari aturan baru. Status non-○
                    // (✓ T K ESO TAP yg sudah diisi perawat) dijaga.
                    if (!aturanLama.equals(aturanBaru)) {
                        Sequel.queryu2("delete from observasi_icu_catatan_pengobatan_detail "
                            + "where no_rawat=? and tgl_observasi=? and no_urut=? and status='○'",
                            3, new String[]{noRawat, tglObs, noUrut});
                        for (String jam : parseJadwalJamMedikasi(aturanBaru)) {
                            Sequel.queryu2("insert into observasi_icu_catatan_pengobatan_detail "
                                + "(no_rawat,tgl_observasi,no_urut,jam,status,nip_petugas) "
                                + "values (?,?,?,?,?,?) on duplicate key update status=status",
                                6, new String[]{noRawat, tglObs, noUrut, jam, "○", NIPHeader.getText()});
                        }
                    }
                    tampilMedikasi();
                    emptTeksMedikasi();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Pilih data yang akan diedit terlebih dahulu..!!");
        }
    }

    private void editCatatan() {
        if (tbCatatan.getSelectedRow() > -1) {
            if (KdPegCatatan.getText().trim().equals("")) {
                Valid.textKosong(KdPegCatatan, "Pegawai");
            } else {
                if (Sequel.mengedittf("pemeriksaan_ranap", "no_rawat=? and tgl_perawatan=? and jam_rawat=?",
                    "suhu_tubuh=?,tensi=?,nadi=?,respirasi=?,tinggi=?,berat=?,spo2=?,kesadaran=?,"
                    + "keluhan=?,pemeriksaan=?,alergi=?,penilaian=?,rtl=?,instruksi=?,evaluasi=?,nip=?", 19, new String[]{
                    TSuhuTubuh.getText(), TTensi.getText(), TNadi.getText(), TRespirasi.getText(),
                    TTinggi.getText(), TBerat.getText(), TSpO2.getText(),
                    CmbKesadaran.getSelectedItem().toString(),
                    TKeluhan.getText(), TPemeriksaanRanap.getText(), TAlergi.getText(),
                    TPenilaian.getText(), TRTL.getText(), TInstruksi.getText(), TEvaluasi.getText(),
                    KdPegCatatan.getText(),
                    tbCatatan.getValueAt(tbCatatan.getSelectedRow(), 0).toString(),
                    tbCatatan.getValueAt(tbCatatan.getSelectedRow(), 1).toString(),
                    tbCatatan.getValueAt(tbCatatan.getSelectedRow(), 2).toString()
                })) {
                    tampilCatatan();
                    emptTeksCatatan();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Pilih data yang akan diedit terlebih dahulu..!!");
        }
    }

    private void printCurrentTab() {
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            // Data pasien
            String noRawat = TNoRw.getText();
            String noRM = TNoRM.getText();
            String namaPasien = TPasien.getText();
            String tglLahir = Sequel.cariIsi("select tgl_lahir from pasien where no_rkm_medis=?", noRM);
            String jk = Sequel.cariIsi("select jk from pasien where no_rkm_medis=?", noRM);
            String alamat = Sequel.cariIsi("select alamat from pasien where no_rkm_medis=?", noRM);
            String tglMasuk = Sequel.cariIsi("select tgl_registrasi from reg_periksa where no_rawat=?", noRawat);
            String diagnosa = Sequel.cariIsi("select diagnosa_awal from kamar_inap where no_rawat=? order by tgl_masuk desc limit 1", noRawat);
            String kamar = Sequel.cariIsi("select kd_kamar from kamar_inap where no_rawat=? and stts_pulang='-' order by tgl_masuk desc limit 1", noRawat);
            String dpjp = Sequel.cariIsi("select dokter.nm_dokter from dpjp_ranap inner join dokter on dpjp_ranap.kd_dokter=dokter.kd_dokter where dpjp_ranap.no_rawat=? limit 1", noRawat);

            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
            html.append("<title>Lembar Observasi Rawat Intensif</title>");
            html.append("<style>");
            html.append("@media print { @page { size: A4 landscape; margin: 10mm; } }");
            html.append("body { font-family: 'Tahoma', Arial, sans-serif; font-size: 10px; margin: 10px; color: #333; }");
            html.append(".header { text-align: center; border-bottom: 2px solid #333; padding-bottom: 5px; margin-bottom: 8px; }");
            html.append(".header h2 { margin: 0; font-size: 14px; }");
            html.append(".header h3 { margin: 2px 0; font-size: 12px; font-weight: normal; }");
            html.append(".header p { margin: 1px 0; font-size: 9px; }");
            html.append(".title { text-align: center; font-size: 12px; font-weight: bold; margin: 8px 0; text-decoration: underline; }");
            html.append(".info-table { width: 100%; border-collapse: collapse; margin-bottom: 8px; }");
            html.append(".info-table td { padding: 2px 5px; font-size: 10px; vertical-align: top; }");
            html.append(".info-table .lbl { width: 120px; font-weight: bold; }");
            html.append(".data-table { width: 100%; border-collapse: collapse; margin-bottom: 10px; }");
            html.append(".data-table th { background: #e8e8e8; border: 1px solid #999; padding: 3px 4px; font-size: 9px; text-align: center; }");
            html.append(".data-table td { border: 1px solid #999; padding: 2px 4px; font-size: 9px; text-align: center; }");
            html.append(".section-title { font-size: 11px; font-weight: bold; margin: 10px 0 5px 0; background: #f0f0f0; padding: 3px 8px; border-left: 3px solid #333; }");
            html.append(".tte-table { width: 100%; border: none; margin-top: 15px; }");
            html.append(".tte-table td { text-align: center; padding: 5px; font-size: 10px; border: none; vertical-align: top; }");
            html.append(".tte-table img { width: 90px; height: 90px; }");
            html.append("</style></head><body>");

            // Header RS
            html.append("<div class='header'>");
            html.append("<h2>").append(akses.getnamars()).append("</h2>");
            html.append("<p>").append(akses.getalamatrs()).append(", ").append(akses.getkabupatenrs()).append(", ").append(akses.getpropinsirs()).append("</p>");
            html.append("<p>").append(akses.getkontakrs()).append(" | E-mail: ").append(akses.getemailrs()).append("</p>");
            html.append("</div>");
            html.append("<div class='title'>LEMBAR OBSERVASI RAWAT INTENSIF (ICU/HCU)</div>");

            // Info Pasien
            html.append("<table class='info-table'>");
            html.append("<tr><td class='lbl'>No. Rawat</td><td>: ").append(noRawat).append("</td>");
            html.append("<td class='lbl'>Tgl. Masuk RS</td><td>: ").append(tglMasuk).append("</td></tr>");
            html.append("<tr><td class='lbl'>No. Rekam Medis</td><td>: ").append(noRM).append("</td>");
            html.append("<td class='lbl'>Kamar</td><td>: ").append(kamar).append("</td></tr>");
            html.append("<tr><td class='lbl'>Nama Pasien</td><td>: ").append(namaPasien).append("</td>");
            html.append("<td class='lbl'>DPJP</td><td>: ").append(dpjp).append("</td></tr>");
            html.append("<tr><td class='lbl'>Tgl. Lahir / JK</td><td>: ").append(tglLahir).append(" / ").append(jk).append("</td>");
            html.append("<td class='lbl'>Diagnosa</td><td>: ").append(diagnosa).append("</td></tr>");
            html.append("</table>");

            // ===== SKOR PENILAIAN (tampilkan jika ada data) =====
            {
                boolean adaSkor = false;
                StringBuilder skorHtml = new StringBuilder();

                // PELOD2
                ps = koneksi.prepareStatement("select tanggal,total_skor,interpretasi,IFNULL(pet.nama,p.nip) from pelod2_score p left join petugas pet on p.nip=pet.nip where p.no_rawat=? order by p.tanggal desc");
                ps.setString(1, noRawat);
                rs = ps.executeQuery();
                if (rs.isBeforeFirst()) {
                    adaSkor = true;
                    skorHtml.append("<p style='font-size:10px;font-weight:bold;margin:5px 0 3px 0;'>PELOD-2 Score</p>");
                    skorHtml.append("<table class='data-table'><tr><th>Tanggal</th><th>Total Skor</th><th>Interpretasi</th><th>Petugas</th></tr>");
                    while (rs.next()) {
                        skorHtml.append("<tr><td>").append(rs.getString(1)).append("</td><td>").append(rs.getString(2))
                            .append("</td><td>").append(rs.getString(3) == null ? "" : rs.getString(3))
                            .append("</td><td>").append(rs.getString(4) == null ? "" : rs.getString(4)).append("</td></tr>");
                    }
                    skorHtml.append("</table>");
                }
                rs.close(); ps.close();

                // RAMSAY
                ps = koneksi.prepareStatement("select tanggal,level,deskripsi,interpretasi,IFNULL(pet.nama,r.nip) from ramsay_score r left join petugas pet on r.nip=pet.nip where r.no_rawat=? order by r.tanggal desc");
                ps.setString(1, noRawat);
                rs = ps.executeQuery();
                if (rs.isBeforeFirst()) {
                    adaSkor = true;
                    skorHtml.append("<p style='font-size:10px;font-weight:bold;margin:5px 0 3px 0;'>Ramsay Sedation Scale</p>");
                    skorHtml.append("<table class='data-table'><tr><th>Tanggal</th><th>Level</th><th>Deskripsi</th><th>Interpretasi</th><th>Petugas</th></tr>");
                    while (rs.next()) {
                        skorHtml.append("<tr><td>").append(rs.getString(1)).append("</td><td>").append(rs.getString(2))
                            .append("</td><td style='text-align:left'>").append(rs.getString(3) == null ? "" : rs.getString(3))
                            .append("</td><td>").append(rs.getString(4) == null ? "" : rs.getString(4))
                            .append("</td><td>").append(rs.getString(5) == null ? "" : rs.getString(5)).append("</td></tr>");
                    }
                    skorHtml.append("</table>");
                }
                rs.close(); ps.close();

                // RASS
                ps = koneksi.prepareStatement("select tanggal,score,kategori,interpretasi,rekomendasi,IFNULL(pet.nama,r.nip) from rass_score r left join petugas pet on r.nip=pet.nip where r.no_rawat=? order by r.tanggal desc");
                ps.setString(1, noRawat);
                rs = ps.executeQuery();
                if (rs.isBeforeFirst()) {
                    adaSkor = true;
                    skorHtml.append("<p style='font-size:10px;font-weight:bold;margin:5px 0 3px 0;'>RASS (Richmond Agitation-Sedation Scale)</p>");
                    skorHtml.append("<table class='data-table'><tr><th>Tanggal</th><th>Score</th><th>Kategori</th><th>Interpretasi</th><th>Rekomendasi</th><th>Petugas</th></tr>");
                    while (rs.next()) {
                        skorHtml.append("<tr><td>").append(rs.getString(1)).append("</td><td>").append(rs.getString(2))
                            .append("</td><td>").append(rs.getString(3) == null ? "" : rs.getString(3))
                            .append("</td><td>").append(rs.getString(4) == null ? "" : rs.getString(4))
                            .append("</td><td style='text-align:left'>").append(rs.getString(5) == null ? "" : rs.getString(5))
                            .append("</td><td>").append(rs.getString(6) == null ? "" : rs.getString(6)).append("</td></tr>");
                    }
                    skorHtml.append("</table>");
                }
                rs.close(); ps.close();

                // SOFA
                ps = koneksi.prepareStatement("select tanggal,skor_respirasi,skor_kardio,skor_neuro,skor_hemato,skor_renal,skor_hepatik,total_sofa,kategori_risiko,estimasi_mortalitas,IFNULL(pet.nama,s.nip) from sofa_score s left join petugas pet on s.nip=pet.nip where s.no_rawat=? order by s.tanggal desc");
                ps.setString(1, noRawat);
                rs = ps.executeQuery();
                if (rs.isBeforeFirst()) {
                    adaSkor = true;
                    skorHtml.append("<p style='font-size:10px;font-weight:bold;margin:5px 0 3px 0;'>SOFA Score</p>");
                    skorHtml.append("<table class='data-table'><tr><th>Tanggal</th><th>Resp</th><th>Kardio</th><th>Neuro</th><th>Hemato</th><th>Renal</th><th>Hepatik</th><th>Total</th><th>Kategori Risiko</th><th>Est. Mortalitas</th><th>Petugas</th></tr>");
                    while (rs.next()) {
                        skorHtml.append("<tr>");
                        for (int c = 1; c <= 11; c++) skorHtml.append("<td>").append(rs.getString(c) == null ? "" : rs.getString(c)).append("</td>");
                        skorHtml.append("</tr>");
                    }
                    skorHtml.append("</table>");
                }
                rs.close(); ps.close();

                // APACHE II
                ps = koneksi.prepareStatement("select tanggal,total_aps,skor_umur,skor_chronic_health,total_apache,estimasi_mortalitas,kategori_risiko,IFNULL(pet.nama,a.nip) from apache_score a left join petugas pet on a.nip=pet.nip where a.no_rawat=? order by a.tanggal desc");
                ps.setString(1, noRawat);
                rs = ps.executeQuery();
                if (rs.isBeforeFirst()) {
                    adaSkor = true;
                    skorHtml.append("<p style='font-size:10px;font-weight:bold;margin:5px 0 3px 0;'>APACHE II Score</p>");
                    skorHtml.append("<table class='data-table'><tr><th>Tanggal</th><th>APS</th><th>Skor Umur</th><th>Chronic Health</th><th>Total APACHE</th><th>Est. Mortalitas</th><th>Kategori Risiko</th><th>Petugas</th></tr>");
                    while (rs.next()) {
                        skorHtml.append("<tr>");
                        for (int c = 1; c <= 8; c++) skorHtml.append("<td>").append(rs.getString(c) == null ? "" : rs.getString(c)).append("</td>");
                        skorHtml.append("</tr>");
                    }
                    skorHtml.append("</table>");
                }
                rs.close(); ps.close();

                if (adaSkor) {
                    html.append("<div class='section-title'>SKOR PENILAIAN</div>");
                    html.append(skorHtml);
                }
            }

            // ===== TAB HEMODINAMIK =====
            html.append("<div class='section-title'>A. MONITORING HEMODINAMIK</div>");
            html.append("<table class='data-table'>");
            html.append("<tr><th>Tanggal</th><th>Jam</th><th>BP</th><th>MAP</th><th>HR</th><th>T(°C)</th><th>RR</th><th>CVP</th><th>EKG</th><th>SpO2</th><th>Kesadaran</th><th>GCS</th><th>Refleks Pupil Ka/Ki</th><th>Motorik (AKA/AKI/BKA/BKI)</th><th>Petugas</th></tr>");
            ps = koneksi.prepareStatement("select obs.tgl_observasi,obs.jam,obs.bp_sistol,obs.bp_diastol,obs.map,obs.hr,obs.suhu,obs.rr,obs.cvp,obs.gambaran_ekg,obs.saturasi_o2,obs.kesadaran,obs.gcs_e,obs.gcs_v,obs.gcs_m,obs.refleks_pupil_kanan,obs.refleks_pupil_kiri,obs.motorik_aka,obs.motorik_aki,obs.motorik_bka,obs.motorik_bki,obs.nip,IFNULL(pet.nama,obs.nip) from observasi_icu_hemodinamik obs left join petugas pet on obs.nip=pet.nip where obs.no_rawat=? order by obs.tgl_observasi,obs.jam");
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            while (rs.next()) {
                String bpSys = rs.getString(3) == null ? "" : rs.getString(3);
                String bpDys = rs.getString(4) == null ? "" : rs.getString(4);
                String map_ = rs.getString(5) == null ? "" : rs.getString(5);
                String hr_ = rs.getString(6) == null ? "" : rs.getString(6);
                String suhu_ = rs.getString(7) == null ? "" : rs.getString(7);
                String rr_ = rs.getString(8) == null ? "" : rs.getString(8);
                String cvp_ = rs.getString(9) == null ? "" : rs.getString(9);
                String ekg_ = rs.getString(10) == null ? "" : rs.getString(10);
                String spo2_ = rs.getString(11) == null ? "" : rs.getString(11);
                String kes_ = rs.getString(12) == null ? "" : rs.getString(12);
                String gcsE = rs.getString(13) == null ? "" : rs.getString(13);
                String gcsV = rs.getString(14) == null ? "" : rs.getString(14);
                String gcsM = rs.getString(15) == null ? "" : rs.getString(15);
                String pupKa = rs.getString(16) == null ? "" : rs.getString(16);
                String pupKi = rs.getString(17) == null ? "" : rs.getString(17);
                String aka_ = rs.getString(18) == null ? "" : rs.getString(18);
                String aki_ = rs.getString(19) == null ? "" : rs.getString(19);
                String bka_ = rs.getString(20) == null ? "" : rs.getString(20);
                String bki_ = rs.getString(21) == null ? "" : rs.getString(21);
                String petugas_ = rs.getString(23) == null ? "" : rs.getString(23);
                html.append("<tr>");
                html.append("<td>").append(rs.getString(1)).append("</td>");
                html.append("<td>").append(rs.getString(2)).append("</td>");
                html.append("<td>").append(bpSys).append("/").append(bpDys).append("</td>");
                html.append("<td>").append(map_).append("</td>");
                html.append("<td>").append(hr_).append("</td>");
                html.append("<td>").append(suhu_).append("</td>");
                html.append("<td>").append(rr_).append("</td>");
                html.append("<td>").append(cvp_).append("</td>");
                html.append("<td style='font-size:8px'>").append(ekg_).append("</td>");
                html.append("<td>").append(spo2_).append("</td>");
                html.append("<td>").append(kes_).append("</td>");
                html.append("<td>").append(gcsE).append("/").append(gcsV).append("/").append(gcsM).append("</td>");
                html.append("<td>").append(pupKa).append("/").append(pupKi).append("</td>");
                html.append("<td>").append(aka_).append("/").append(aki_).append("/").append(bka_).append("/").append(bki_).append("</td>");
                html.append("<td style='font-size:8px'>").append(petugas_).append("</td>");
                html.append("</tr>");
            }
            rs.close(); ps.close();
            html.append("</table>");

            // ===== GRAFIK HEMODINAMIK (inline SVG) =====
            {
                java.util.List<String> gJam = new java.util.ArrayList<>();
                java.util.List<Double> gHR = new java.util.ArrayList<>();
                java.util.List<Double> gRR = new java.util.ArrayList<>();
                java.util.List<Double> gTemp = new java.util.ArrayList<>();
                java.util.List<Double> gSpO2 = new java.util.ArrayList<>();

                ps = koneksi.prepareStatement(
                    "select jam, IFNULL(hr,0), IFNULL(rr,0), IFNULL(suhu,0), IFNULL(saturasi_o2,0) "
                    + "from observasi_icu_hemodinamik where no_rawat=? order by tgl_observasi, jam");
                ps.setString(1, noRawat);
                rs = ps.executeQuery();
                while (rs.next()) {
                    gJam.add(rs.getString(1));
                    try { gHR.add(Double.parseDouble(rs.getString(2))); } catch (Exception ex) { gHR.add(0.0); }
                    try { gRR.add(Double.parseDouble(rs.getString(3))); } catch (Exception ex) { gRR.add(0.0); }
                    try { gTemp.add(Double.parseDouble(rs.getString(4))); } catch (Exception ex) { gTemp.add(0.0); }
                    try { gSpO2.add(Double.parseDouble(rs.getString(5))); } catch (Exception ex) { gSpO2.add(0.0); }
                }
                rs.close(); ps.close();

                int n = gJam.size();
                if (n >= 2) {
                    int svgW = 800, svgH = 300, left = 50, right = 20, top = 30, bottom = 50;
                    int gw = svgW - left - right, gh = svgH - top - bottom;
                    double maxVal = 200.0;

                    html.append("<div class='section-title'>GRAFIK HEMODINAMIK</div>");
                    html.append("<svg width='").append(svgW).append("' height='").append(svgH).append("' style='border:1px solid #ccc;background:#fff;margin-bottom:10px'>");

                    // Grid
                    for (int yi = 0; yi <= 10; yi++) {
                        int y = top + gh - (yi * gh / 10);
                        html.append("<line x1='").append(left).append("' y1='").append(y).append("' x2='").append(left + gw).append("' y2='").append(y).append("' stroke='#eee'/>");
                        html.append("<text x='5' y='").append(y + 4).append("' font-size='9' fill='#666'>").append(yi * 20).append("</text>");
                    }
                    // X labels
                    int step = Math.max(1, n / 12);
                    for (int xi = 0; xi < n; xi += step) {
                        int x = left + (xi * gw / (n - 1));
                        html.append("<text x='").append(x - 10).append("' y='").append(top + gh + 15).append("' font-size='8' fill='#666'>").append(gJam.get(xi)).append("</text>");
                    }

                    // Lines helper
                    String[][] series = {{"HR", "#ff0000"}, {"RR", "#0000ff"}, {"Temp", "#ff8c00"}, {"SpO2", "#009900"}};
                    java.util.List<?>[] dataArrays = {gHR, gRR, gTemp, gSpO2};
                    for (int s = 0; s < 4; s++) {
                        @SuppressWarnings("unchecked")
                        java.util.List<Double> data = (java.util.List<Double>) dataArrays[s];
                        StringBuilder points = new StringBuilder();
                        for (int xi = 0; xi < n; xi++) {
                            int x = left + (xi * gw / (n - 1));
                            int y = top + gh - (int) (data.get(xi) / maxVal * gh);
                            if (xi > 0) points.append(" ");
                            points.append(x).append(",").append(y);
                        }
                        html.append("<polyline points='").append(points).append("' fill='none' stroke='").append(series[s][1]).append("' stroke-width='2'/>");
                        // Dots
                        for (int xi = 0; xi < n; xi++) {
                            int x = left + (xi * gw / (n - 1));
                            int y = top + gh - (int) (data.get(xi) / maxVal * gh);
                            html.append("<circle cx='").append(x).append("' cy='").append(y).append("' r='3' fill='").append(series[s][1]).append("'/>");
                        }
                    }

                    // Legend
                    for (int s = 0; s < 4; s++) {
                        int lx = left + 10 + s * 100;
                        html.append("<rect x='").append(lx).append("' y='").append(top + gh + 30).append("' width='15' height='3' fill='").append(series[s][1]).append("'/>");
                        html.append("<text x='").append(lx + 20).append("' y='").append(top + gh + 35).append("' font-size='10' font-weight='bold' fill='").append(series[s][1]).append("'>").append(series[s][0]).append("</text>");
                    }

                    html.append("</svg>");
                }
            }

            // ===== TAB VENTILATOR =====
            html.append("<div class='section-title'>B. MONITORING VENTILATOR</div>");
            html.append("<table class='data-table'>");
            html.append("<tr><th>Tanggal</th><th>Jam</th><th>Tipe</th><th>RR Set</th><th>RR Akt</th><th>I:E</th><th>MV Set</th><th>MV Akt</th><th>PC</th><th>PS</th><th>PEEP</th><th>FiO2</th><th>Peak</th><th>Plateau</th><th>ETT/TC</th><th>Diameter</th><th>Kedlmn</th><th>Cuff</th><th>Petugas</th></tr>");
            ps = koneksi.prepareStatement("select vent.tgl_observasi,vent.jam_observasi,vent.tipe_ventilator,vent.rr_setting,vent.rr_aktual,vent.ie_ratio,vent.mv_setting,vent.mv_aktual,vent.pressure_control,vent.pressure_support,vent.peep,vent.fio2_flow,vent.peak_pressure,vent.plateau_pressure,vent.ett_tc,vent.diameter,vent.kedalaman,vent.tekanan_cuff,IFNULL(pet.nama,vent.nip) from observasi_icu_ventilator vent left join petugas pet on vent.nip=pet.nip where vent.no_rawat=? order by vent.tgl_observasi,vent.jam_observasi");
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            while (rs.next()) {
                html.append("<tr>");
                for (int c = 1; c <= 19; c++) html.append("<td>").append(rs.getString(c) == null ? "" : rs.getString(c)).append("</td>");
                html.append("</tr>");
            }
            rs.close(); ps.close();
            html.append("</table>");

            // ===== TAB INTAKE OUTPUT =====
            html.append("<div class='section-title'>C. INTAKE & OUTPUT</div>");
            html.append("<table class='data-table'>");
            html.append("<tr><th>Tanggal</th><th>Jam</th><th>Transfusi I CA</th><th>Transfusi I CM</th><th>Transfusi II CA</th><th>Transfusi II CM</th><th>Transfusi III</th><th>Jml 1Jam Kum</th><th>Oral</th><th>Enteral</th><th>Jml Makan Kum</th><th>Parenteral</th><th>Par. Kum</th><th>Kumulatif</th><th>Urine</th><th>NGT/OGT</th><th>BAB</th><th>Drain I</th><th>Drain II</th><th>Drain III</th><th>Muntah</th><th>IWL</th><th>Out Kum</th><th>Kes. Cairan</th><th>Petugas</th></tr>");
            ps = koneksi.prepareStatement("select io.tgl_observasi,io.jam_observasi,io.transfusi_i_ca,io.transfusi_i_cm,io.transfusi_ii_ca,io.transfusi_ii_cm,io.transfusi_iii_ca_cm,io.jumlah_1jam_kumulatif,io.makan_oral,io.enteral,io.jumlah_makan_kumulatif,io.parenteral,io.parenteral_kumulatif,io.kumulatif,io.urine,io.ngt_ogt,io.bab,io.drain_stoma_i,io.drain_stoma_ii,io.drain_stoma_iii,io.muntah,io.iwal,io.output_kumulatif,io.keseimbangan_cairan,IFNULL(pet.nama,io.nip) from observasi_icu_intake_output io left join petugas pet on io.nip=pet.nip where io.no_rawat=? order by io.tgl_observasi,io.jam_observasi");
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            while (rs.next()) {
                html.append("<tr>");
                for (int c = 1; c <= 25; c++) html.append("<td>").append(rs.getString(c) == null ? "" : rs.getString(c)).append("</td>");
                html.append("</tr>");
            }
            rs.close(); ps.close();
            html.append("</table>");

            // ===== TAB CATATAN KEPERAWATAN =====
            html.append("<div class='section-title'>D. CATATAN KEPERAWATAN</div>");
            html.append("<table class='data-table'>");
            html.append("<tr><th>Tanggal</th><th>Jam</th><th>Personal Hygiene</th><th>Oral Hygiene</th><th>Perineal Hygiene</th><th>Suction</th><th>Posisi/Mobilisasi</th><th>Postural Drainase</th><th>Nutrisi Enteral</th><th>Nebulasi</th><th>Wound Care</th><th>Mnj Risiko Jatuh</th><th>Petugas</th></tr>");
            ps = koneksi.prepareStatement("select ck.tgl_catat,ck.jam_catat,ck.personal_hygiene,ck.oral_hygiene,ck.perineal_hygiene,ck.suction,ck.posisi_mobilisasi,ck.postural_drainase,ck.nutrisi_enteral,ck.nebulasi,ck.wound_care,ck.manajemen_risiko_jatuh,IFNULL(pet.nama,ck.nip) from observasi_icu_catatan_keperawatan ck left join petugas pet on ck.nip=pet.nip where ck.no_rawat=? order by ck.tgl_catat,ck.jam_catat");
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            while (rs.next()) {
                html.append("<tr>");
                for (int c = 1; c <= 13; c++) html.append("<td>").append(rs.getString(c) == null ? "" : rs.getString(c)).append("</td>");
                html.append("</tr>");
            }
            rs.close(); ps.close();
            html.append("</table>");

            // ===== TAB BUNDLE CARE =====
            html.append("<div class='section-title'>E. BUNDLE CARE</div>");
            // VAP
            html.append("<p style='font-size:10px;font-weight:bold;margin:5px 0 3px 10px;'>E.1 Bundle VAP</p>");
            html.append("<table class='data-table'>");
            html.append("<tr><th>Tanggal</th><th>Jam</th><th>Head Elevation</th><th>Oral Care</th><th>Peptic Ulcer Proph.</th><th>DVT Proph.</th><th>Sedation Vacation</th><th>Weaning Assess.</th><th>Petugas</th></tr>");
            ps = koneksi.prepareStatement("select v.tgl_bundle,v.jam_bundle,v.head_of_bed_elevation,v.oral_care,v.peptic_ulcer_prophylaxis,v.dvt_prophylaxis,v.sedation_vacation,v.weaning_assessment,IFNULL(pet.nama,v.nip) from observasi_icu_bundle_vap v left join petugas pet on v.nip=pet.nip where v.no_rawat=? order by v.tgl_bundle,v.jam_bundle");
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            while (rs.next()) { html.append("<tr>"); for (int c = 1; c <= 9; c++) html.append("<td>").append(rs.getString(c) == null ? "" : rs.getString(c)).append("</td>"); html.append("</tr>"); }
            rs.close(); ps.close();
            html.append("</table>");
            // CVC
            html.append("<p style='font-size:10px;font-weight:bold;margin:5px 0 3px 10px;'>E.2 Bundle CVC</p>");
            html.append("<table class='data-table'>");
            html.append("<tr><th>Tanggal</th><th>Jam</th><th>Kebutuhan</th><th>Cuci Tangan</th><th>Suhu>38</th><th>Nyeri</th><th>Kemerahan</th><th>Bengkak</th><th>Pus</th><th>Penggantian</th><th>Balutan</th><th>Infus Set</th><th>Tree Way</th><th>Valve</th><th>Petugas</th></tr>");
            ps = koneksi.prepareStatement("select c.tgl_bundle,c.jam_bundle,c.kebutuhan_cvc,c.mencuci_tangan,c.suhu_lebih_38,c.nyeri,c.kemerahan,c.bengkak,c.pus,c.penggantian_monitoring,c.balutan_insersi,c.infus_set,c.tree_way,c.valve_konektor,IFNULL(pet.nama,c.nip) from observasi_icu_bundle_cvc c left join petugas pet on c.nip=pet.nip where c.no_rawat=? order by c.tgl_bundle,c.jam_bundle");
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            while (rs.next()) { html.append("<tr>"); for (int c = 1; c <= 15; c++) html.append("<td>").append(rs.getString(c) == null ? "" : rs.getString(c)).append("</td>"); html.append("</tr>"); }
            rs.close(); ps.close();
            html.append("</table>");
            // ISK
            html.append("<p style='font-size:10px;font-weight:bold;margin:5px 0 3px 10px;'>E.3 Bundle ISK</p>");
            html.append("<table class='data-table'>");
            html.append("<tr><th>Tanggal</th><th>Jam</th><th>Pemasangan</th><th>Tek. Aseptik</th><th>Kateter</th><th>Posisi Urin Bag</th><th>Hand Hygiene</th><th>Meatal Care</th><th>Fiksasi</th><th>Kosongkan</th><th>Closed System</th><th>Petugas</th></tr>");
            ps = koneksi.prepareStatement("select i.tgl_bundle,i.jam_bundle,i.pemasangan_sesuai_indikasi,i.teknik_aseptik,i.kateter_dibutuhkan,i.posisi_urin_bag,i.hand_hygiene,i.meatal_care,i.fiksasi_kateter,i.kosongkan_urin_bag,i.closed_system,IFNULL(pet.nama,i.nip) from observasi_icu_bundle_isk i left join petugas pet on i.nip=pet.nip where i.no_rawat=? order by i.tgl_bundle,i.jam_bundle");
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            while (rs.next()) { html.append("<tr>"); for (int c = 1; c <= 12; c++) html.append("<td>").append(rs.getString(c) == null ? "" : rs.getString(c)).append("</td>"); html.append("</tr>"); }
            rs.close(); ps.close();
            html.append("</table>");

            // ===== TAB CATATAN PENGOBATAN =====
            html.append("<div class='section-title' style='page-break-before:always;'>F. CATATAN PENGOBATAN</div>");
            html.append("<table class='data-table'>");
            html.append("<tr><th rowspan='2'>No</th><th rowspan='2'>Nama Obat</th><th rowspan='2'>Kekuatan</th><th rowspan='2'>Sediaan</th><th rowspan='2'>Aturan Pakai</th><th rowspan='2'>Rute</th><th rowspan='2'>Paraf Dokter</th><th colspan='24'>JAM</th><th rowspan='2'>Keterangan</th></tr>");
            html.append("<tr><th>07</th><th>08</th><th>09</th><th>10</th><th>11</th><th>12</th><th>13</th><th>14</th><th>15</th><th>16</th><th>17</th><th>18</th><th>19</th><th>20</th><th>21</th><th>22</th><th>23</th><th>24</th><th>01</th><th>02</th><th>03</th><th>04</th><th>05</th><th>06</th></tr>");
            {
                String[] jamCetak = {"07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","01","02","03","04","05","06"};
                ps = koneksi.prepareStatement(
                    "select m.no_urut,m.nama_obat,m.kekuatan,m.bentuk_sediaan,m.aturan_pakai,m.rute_pemberian,m.keterangan,"
                    + "CASE WHEN m.paraf_dokter='Y' THEN CONCAT(IFNULL(m.nm_dokter,''),' ',IFNULL(DATE_FORMAT(m.tgl_paraf,'%d-%m-%Y %H:%i'),'')) ELSE '' END,"
                    + "m.no_rawat,m.tgl_observasi "
                    + "from observasi_icu_catatan_pengobatan m "
                    + "where m.no_rawat=? order by m.tgl_observasi,m.no_urut");
                ps.setString(1, noRawat);
                rs = ps.executeQuery();
                while (rs.next()) {
                    html.append("<tr>");
                    // No urut
                    html.append("<td>").append(rs.getString(1) == null ? "" : rs.getString(1)).append("</td>");
                    // Nama Obat - Rute (kolom 2-6)
                    for (int c = 2; c <= 6; c++) {
                        html.append("<td style='font-size:8px;text-align:left'>").append(rs.getString(c) == null ? "" : rs.getString(c)).append("</td>");
                    }
                    // Paraf Dokter (kolom 8)
                    html.append("<td style='font-size:8px;text-align:left'>").append(rs.getString(8) == null ? "" : rs.getString(8)).append("</td>");
                    // Kolom jam: query detail
                    String nrOb = rs.getString(9);
                    String tgOb = rs.getString(10);
                    String noUr = rs.getString(1);
                    // Ambil semua detail jam untuk baris ini
                    java.util.HashMap<String, String> jamMap = new java.util.HashMap<>();
                    PreparedStatement psJam = koneksi.prepareStatement(
                        "select jam, status from observasi_icu_catatan_pengobatan_detail where no_rawat=? and tgl_observasi=? and no_urut=?");
                    psJam.setString(1, nrOb);
                    psJam.setString(2, tgOb);
                    psJam.setString(3, noUr);
                    ResultSet rsJam = psJam.executeQuery();
                    while (rsJam.next()) {
                        jamMap.put(rsJam.getString(1), rsJam.getString(2));
                    }
                    rsJam.close(); psJam.close();

                    for (String j : jamCetak) {
                        String st = jamMap.getOrDefault(j, "");
                        html.append("<td style='font-size:8px'>").append(st).append("</td>");
                    }
                    // Keterangan (setelah jam, kolom 7)
                    html.append("<td style='font-size:8px;text-align:left'>").append(rs.getString(7) == null ? "" : rs.getString(7)).append("</td>");
                    html.append("</tr>");
                }
                rs.close(); ps.close();
            }
            html.append("</table>");
            html.append("<p style='font-size:8px;margin:3px 0;'>Keterangan: ✓=Obat Diberikan | ○=Terjadwal | T=Tdk Diberikan | K=Ditunda | ESO=Efek Samping Obat | TAP=Tdk Ada Persediaan</p>");

            // ===== TAB CATATAN TERINTEGRASI (SOAP) =====
            html.append("<div class='section-title'>G. CATATAN TERINTEGRASI (SOAP)</div>");
            html.append("<table class='data-table'>");
            html.append("<tr><th>Tanggal</th><th>Jam</th><th>Suhu</th><th>Tensi</th><th>Nadi</th><th>RR</th><th>SpO2</th><th>Kesadaran</th><th>Subjek (S)</th><th>Objek (O)</th><th>Alergi</th><th>Asesmen (A)</th><th>Plan (P)</th><th>Instruksi</th><th>Evaluasi</th><th>Petugas</th></tr>");
            ps = koneksi.prepareStatement("select pr.tgl_perawatan,pr.jam_rawat,pr.suhu_tubuh,pr.tensi,pr.nadi,pr.respirasi,pr.spo2,pr.kesadaran,pr.keluhan,pr.pemeriksaan,pr.alergi,pr.penilaian,pr.rtl,pr.instruksi,pr.evaluasi,IFNULL(pg.nama,pr.nip) from pemeriksaan_ranap pr left join pegawai pg on pr.nip=pg.nik where pr.no_rawat=? order by pr.tgl_perawatan,pr.jam_rawat");
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            while (rs.next()) {
                html.append("<tr>");
                for (int c = 1; c <= 16; c++) {
                    String val = rs.getString(c);
                    html.append("<td style='text-align:left;font-size:8px'>").append(val == null ? "" : val).append("</td>");
                }
                html.append("</tr>");
            }
            rs.close(); ps.close();
            html.append("</table>");

            // ===== TTE (QR Code) =====
            html.append("<div class='section-title'>TANDA TANGAN ELEKTRONIK (TTE)</div>");
            html.append("<table class='tte-table'><tr>");

            // QR DPJP
            String kdDpjp = Sequel.cariIsi("select kd_dokter from dpjp_ranap where no_rawat=? limit 1", noRawat);
            String nmDpjp = Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", kdDpjp);
            if (!kdDpjp.isEmpty()) {
                try {
                    org.apache.commons.httpclient.HttpClient http = new org.apache.commons.httpclient.HttpClient();
                    org.apache.commons.httpclient.methods.GetMethod get = new org.apache.commons.httpclient.methods.GetMethod(
                        "http://" + fungsi.koneksiDB.HOSTHYBRIDWEB() + ":" + fungsi.koneksiDB.PORTWEB() + "/"
                        + fungsi.koneksiDB.HYBRIDWEB() + "/penggajian/generateqrcode.php?kodedokter=" + kdDpjp.replace(" ", "_").replace("/", "_"));
                    http.executeMethod(get);
                    html.append("<td><b>DPJP</b><br><img src='http://").append(fungsi.koneksiDB.HOSTHYBRIDWEB()).append(":")
                        .append(fungsi.koneksiDB.PORTWEB()).append("/").append(fungsi.koneksiDB.HYBRIDWEB())
                        .append("/penggajian/temp/").append(kdDpjp.replace("/", "-")).append(".png'/><br>").append(nmDpjp).append("</td>");
                } catch (Exception ex) {
                    html.append("<td><b>DPJP</b><br>").append(nmDpjp).append("<br>(").append(kdDpjp).append(")</td>");
                }
            }

            // QR Petugas yang terlibat (dari semua tab observasi)
            // Map: nip -> {nama, tipe} dimana tipe = "petugas" atau "pegawai"
            java.util.LinkedHashMap<String, String[]> petugasMap = new java.util.LinkedHashMap<>();

            // Petugas dari tabel ICU (nip → petugas)
            PreparedStatement psPtg = koneksi.prepareStatement(
                "select distinct obs.nip, IFNULL(pet.nama,obs.nip) from observasi_icu_hemodinamik obs left join petugas pet on obs.nip=pet.nip where obs.no_rawat=? "
                + "union select distinct vent.nip, IFNULL(pet.nama,vent.nip) from observasi_icu_ventilator vent left join petugas pet on vent.nip=pet.nip where vent.no_rawat=? "
                + "union select distinct ck.nip, IFNULL(pet.nama,ck.nip) from observasi_icu_catatan_keperawatan ck left join petugas pet on ck.nip=pet.nip where ck.no_rawat=? "
                + "union select distinct io.nip, IFNULL(pet.nama,io.nip) from observasi_icu_intake_output io left join petugas pet on io.nip=pet.nip where io.no_rawat=?");
            psPtg.setString(1, noRawat);
            psPtg.setString(2, noRawat);
            psPtg.setString(3, noRawat);
            psPtg.setString(4, noRawat);
            ResultSet rsPtg = psPtg.executeQuery();
            while (rsPtg.next()) {
                if (!rsPtg.getString(1).isEmpty()) {
                    petugasMap.put(rsPtg.getString(1), new String[]{rsPtg.getString(2), "petugas"});
                }
            }
            rsPtg.close(); psPtg.close();

            // Pegawai dari pemeriksaan_ranap (nik → pegawai)
            psPtg = koneksi.prepareStatement(
                "select distinct pr.nip, IFNULL(pg.nama,pr.nip) from pemeriksaan_ranap pr left join pegawai pg on pr.nip=pg.nik where pr.no_rawat=?");
            psPtg.setString(1, noRawat);
            rsPtg = psPtg.executeQuery();
            while (rsPtg.next()) {
                if (!rsPtg.getString(1).isEmpty() && !petugasMap.containsKey(rsPtg.getString(1))) {
                    petugasMap.put(rsPtg.getString(1), new String[]{rsPtg.getString(2), "pegawai"});
                }
            }
            rsPtg.close(); psPtg.close();

            String baseUrl = "http://" + fungsi.koneksiDB.HOSTHYBRIDWEB() + ":" + fungsi.koneksiDB.PORTWEB() + "/" + fungsi.koneksiDB.HYBRIDWEB();
            org.apache.commons.httpclient.HttpClient httpTte = new org.apache.commons.httpclient.HttpClient();

            for (Map.Entry<String, String[]> entry : petugasMap.entrySet()) {
                String nip = entry.getKey();
                String nama = entry.getValue()[0];
                String tipe = entry.getValue()[1];
                try {
                    // Generate QR: NIP di-encode untuk URL, filename ganti / → -
                    String nipSafe = nip.replace("/", "-");
                    String qrUrl = baseUrl + "/penggajian/generateqrcodepetugas.php?kodepetugas=" + nip.replace(" ", "_").replace("/", "_");
                    org.apache.commons.httpclient.methods.GetMethod get = new org.apache.commons.httpclient.methods.GetMethod(qrUrl);
                    httpTte.executeMethod(get);
                    html.append("<td><b>Petugas</b><br><img src='").append(baseUrl)
                        .append("/penggajian/temp/").append(nipSafe).append(".png'/><br>").append(nama).append("</td>");
                } catch (Exception ex) {
                    html.append("<td><b>Petugas</b><br>").append(nama).append("<br>(").append(nip).append(")</td>");
                }
            }
            html.append("</tr></table>");

            // Footer
            html.append("<p style='font-size:8px;text-align:right;margin-top:10px;color:#888;'>Dicetak pada: ").append(new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date())).append("</p>");
            html.append("</body></html>");

            // Write CSS file
            java.io.File cssFile = new java.io.File("icu_report.css");
            java.io.BufferedWriter bwCss = new java.io.BufferedWriter(new java.io.FileWriter(cssFile));
            bwCss.write(""); // CSS sudah inline
            bwCss.close();

            // Write HTML file
            java.io.File htmlFile = new java.io.File("LembarObservasiRawatIntensif.html");
            java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(htmlFile));
            bw.write(html.toString());
            bw.close();

            // Open in browser
            java.awt.Desktop.getDesktop().browse(htmlFile.toURI());

        } catch (Exception ex) {
            System.out.println("Notifikasi Cetak: " + ex);
            JOptionPane.showMessageDialog(null, "Gagal mencetak: " + ex.getMessage());
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void emptTeksCatatan() {
        TSuhuTubuh.setText("");
        TTensi.setText("");
        TNadi.setText("");
        TRespirasi.setText("");
        TTinggi.setText("");
        TBerat.setText("");
        TSpO2.setText("");
        if (CmbKesadaran.getItemCount() > 0) { CmbKesadaran.setSelectedIndex(0); }
        TKeluhan.setText("");
        TPemeriksaanRanap.setText("");
        TAlergi.setText("");
        TPenilaian.setText("");
        TRTL.setText("");
        TInstruksi.setText("");
        TEvaluasi.setText("");
        KdPegCatatan.setText("");
        NamaPegawaiCatatan.setText("");
    }

    // ========== TAB PENANGANAN DOKTER ==========

    private DefaultTableModel tabModeDr;
    private widget.Table tbRawatDr;
    private widget.TextBox KdDokPen, TDokterPen, TKdPrwDr, TNmPrwDr;

    private void setupTabPenangananDokter() {
        tabModeDr = new DefaultTableModel(null, new Object[]{
            "P", "No.Rawat", "No.R.M.", "Nama Pasien", "Perawatan/Tindakan", "Kode Dokter",
            "Dokter Yg Menangani", "Tgl.Rawat", "Jam Rawat", "Biaya", "Kode Prw",
            "Tarif Dokter", "KSO", "Jasa Sarana", "BHP", "Menejemen"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return c == 0; }
            Class[] types = new Class[]{Boolean.class, Object.class, Object.class, Object.class,
                Object.class, Object.class, Object.class, Object.class, Object.class, Double.class,
                Object.class, Object.class, Object.class, Object.class, Object.class, Object.class};
            @Override public Class getColumnClass(int c) { return types[c]; }
        };
        tbRawatDr = new widget.Table();
        tbRawatDr.setModel(tabModeDr);
        tbRawatDr.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] wDr = {20,105,70,150,200,80,150,65,50,80,80,80,60,70,60,70};
        for (int c = 0; c < wDr.length; c++) tbRawatDr.getColumnModel().getColumn(c).setPreferredWidth(wDr[c]);
        tbRawatDr.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbRawatDr.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (tbRawatDr.getSelectedRow() != -1) {
                    KdDokPen.setText(tbRawatDr.getValueAt(tbRawatDr.getSelectedRow(), 5).toString());
                    TDokterPen.setText(tbRawatDr.getValueAt(tbRawatDr.getSelectedRow(), 6).toString());
                    TKdPrwDr.setText(tbRawatDr.getValueAt(tbRawatDr.getSelectedRow(), 10).toString());
                    TNmPrwDr.setText(tbRawatDr.getValueAt(tbRawatDr.getSelectedRow(), 4).toString());
                }
            }
        });

        widget.InternalFrame ifDr = new widget.InternalFrame();
        ifDr.setBackground(new java.awt.Color(235, 255, 235));
        ifDr.setBorder(null);
        ifDr.setLayout(new java.awt.BorderLayout(1, 1));
        widget.ScrollPane scDr = new widget.ScrollPane();
        scDr.setViewportView(tbRawatDr);
        ifDr.add(scDr, java.awt.BorderLayout.CENTER);

        // Input panel - layout sama dengan DlgRawatInap
        widget.panelisi pnlDr = new widget.panelisi();
        pnlDr.setPreferredSize(new Dimension(44, 74));
        pnlDr.setLayout(null);

        KdDokPen = new widget.TextBox();
        TDokterPen = new widget.TextBox();
        TKdPrwDr = new widget.TextBox();
        TNmPrwDr = new widget.TextBox();

        // Row 1: Dokter
        widget.Label lblDokter = new widget.Label();
        lblDokter.setText("Dokter :");
        pnlDr.add(lblDokter);
        lblDokter.setBounds(0, 10, 105, 23);

        KdDokPen.setName("KdDokPen");
        KdDokPen.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    TDokterPen.setText(Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", KdDokPen.getText()));
                }
            }
        });
        pnlDr.add(KdDokPen);
        KdDokPen.setBounds(108, 10, 130, 23);

        TDokterPen.setEditable(false);
        TDokterPen.setName("TDokterPen");
        pnlDr.add(TDokterPen);
        TDokterPen.setBounds(240, 10, 606, 23);

        widget.Button btnSeekDokterDr = new widget.Button();
        btnSeekDokterDr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnSeekDokterDr.setToolTipText("Cari Dokter");
        btnSeekDokterDr.addActionListener(e -> {
            akses.setform("ICUObservationFormDr");
            kepegawaian.DlgCariDokter dlg = new kepegawaian.DlgCariDokter(null, true);
            dlg.emptTeks();
            dlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            dlg.setLocationRelativeTo(internalFrame1);
            dlg.setVisible(true);
            if (dlg.getTable().getSelectedRow() != -1) {
                KdDokPen.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 0).toString());
                TDokterPen.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 1).toString());
            }
        });
        pnlDr.add(btnSeekDokterDr);
        btnSeekDokterDr.setBounds(849, 10, 28, 23);

        // Row 2: Tindakan/Tagihan
        widget.Label lblTindakan = new widget.Label();
        lblTindakan.setText("Tindakan/Tagihan :");
        pnlDr.add(lblTindakan);
        lblTindakan.setBounds(0, 40, 105, 23);

        TKdPrwDr.setName("TKdPrwDr");
        TKdPrwDr.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    TNmPrwDr.setText(Sequel.cariIsi("select nm_perawatan from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwDr.getText()));
                }
            }
        });
        pnlDr.add(TKdPrwDr);
        TKdPrwDr.setBounds(108, 40, 100, 23);

        TNmPrwDr.setEditable(false);
        TNmPrwDr.setName("TNmPrwDr");
        pnlDr.add(TNmPrwDr);
        TNmPrwDr.setBounds(210, 40, 605, 23);

        widget.Button btnTindakanDr = new widget.Button();
        btnTindakanDr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnTindakanDr.setToolTipText("Cari Tindakan");
        btnTindakanDr.addActionListener(e -> {
            akses.setform("ICUObservationFormDr");
            keuangan.DlgCariPerawatanRanap dlg = new keuangan.DlgCariPerawatanRanap(null, true);
            dlg.emptTeks();
            dlg.isCek();
            dlg.setNoRm(TNoRw.getText(), "rawat_inap_dr", Tanggal.getDate(),
                Jam.getSelectedItem().toString(), Menit.getSelectedItem().toString(),
                Detik.getSelectedItem().toString(), true, TPasien.getText());
            dlg.tampil();
            dlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            dlg.setLocationRelativeTo(internalFrame1);
            dlg.setVisible(true);
            if (dlg.getTable().getSelectedRow() != -1) {
                TKdPrwDr.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 0).toString());
                TNmPrwDr.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 1).toString());
            }
        });
        pnlDr.add(btnTindakanDr);
        btnTindakanDr.setBounds(818, 40, 28, 23);

        widget.Button btnTindakanDr2 = new widget.Button();
        btnTindakanDr2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnTindakanDr2.setToolTipText("Cari Tindakan 2");
        btnTindakanDr2.addActionListener(e -> {
            akses.setform("ICUObservationFormDr");
            keuangan.DlgCariPerawatanRanap2 dlg = new keuangan.DlgCariPerawatanRanap2(null, true);
            dlg.isCek();
            dlg.setNoRm(TNoRw.getText(), "rawat_inap_dr", Tanggal.getDate(),
                Jam.getSelectedItem().toString(), Menit.getSelectedItem().toString(),
                Detik.getSelectedItem().toString(), true, TPasien.getText());
            dlg.tampil();
            dlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            dlg.setLocationRelativeTo(internalFrame1);
            dlg.setVisible(true);
            if (dlg.getTable().getSelectedRow() != -1) {
                TKdPrwDr.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 0).toString());
                TNmPrwDr.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 1).toString());
            }
        });
        pnlDr.add(btnTindakanDr2);
        btnTindakanDr2.setBounds(849, 40, 28, 23);

        ifDr.add(pnlDr, java.awt.BorderLayout.PAGE_START);
        TabICU.addTab("Penanganan Dokter", ifDr);
    }

    private void simpanPenangananDr() {
        if (KdDokPen.getText().trim().isEmpty()) { Valid.textKosong(KdDokPen, "Dokter"); return; }
        if (TKdPrwDr.getText().trim().isEmpty()) { Valid.textKosong(TKdPrwDr, "Tindakan"); return; }
        if (Sequel.cariRegistrasi(TNoRw.getText()) > 0) {
            JOptionPane.showMessageDialog(null, "Data billing sudah terverifikasi ..!!");
            return;
        }
        String tgl = Valid.SetTgl(Tanggal.getSelectedItem() + "");
        String jam = Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem();
        // Get tarif from jns_perawatan_inap
        String totalBiaya = Sequel.cariIsi("select total_byrdr from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwDr.getText());
        String material = Sequel.cariIsi("select material from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwDr.getText());
        String bhp = Sequel.cariIsi("select bhp from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwDr.getText());
        String tarifDr = Sequel.cariIsi("select tarif_tindakandr from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwDr.getText());
        String kso = Sequel.cariIsi("select kso from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwDr.getText());
        String menejemen = Sequel.cariIsi("select menejemen from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwDr.getText());

        if (Sequel.menyimpantf("rawat_inap_dr", "?,?,?,?,?,?,?,?,?,?,?", "Data", 11, new String[]{
            TNoRw.getText(), TKdPrwDr.getText(), KdDokPen.getText(), tgl, jam,
            material, bhp, tarifDr, kso, menejemen, totalBiaya
        })) {
            tampilPenangananDr();
            KdDokPen.setText(""); TDokterPen.setText(""); TKdPrwDr.setText(""); TNmPrwDr.setText("");
        }
    }

    private void hapusPenangananDr() {
        if (tbRawatDr.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih data yang mau dihapus..!!"); return; }
        if (Sequel.cariRegistrasi(tbRawatDr.getValueAt(tbRawatDr.getSelectedRow(), 1).toString()) > 0) {
            JOptionPane.showMessageDialog(null, "Data billing sudah terverifikasi ..!!"); return;
        }
        Sequel.queryu2("delete from rawat_inap_dr where no_rawat=? and kd_jenis_prw=? and kd_dokter=? and tgl_perawatan=? and jam_rawat=?", 5, new String[]{
            tbRawatDr.getValueAt(tbRawatDr.getSelectedRow(), 1).toString(),
            tbRawatDr.getValueAt(tbRawatDr.getSelectedRow(), 10).toString(),
            tbRawatDr.getValueAt(tbRawatDr.getSelectedRow(), 5).toString(),
            tbRawatDr.getValueAt(tbRawatDr.getSelectedRow(), 7).toString(),
            tbRawatDr.getValueAt(tbRawatDr.getSelectedRow(), 8).toString()
        });
        tampilPenangananDr();
    }

    private void tampilPenangananDr() {
        Valid.tabelKosong(tabModeDr);
        try {
            ps = koneksi.prepareStatement(
                "select rawat_inap_dr.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"
                + "concat(rawat_inap_dr.kd_jenis_prw,' ',jns_perawatan_inap.nm_perawatan),rawat_inap_dr.kd_dokter,dokter.nm_dokter,"
                + "rawat_inap_dr.tgl_perawatan,rawat_inap_dr.jam_rawat,rawat_inap_dr.biaya_rawat,rawat_inap_dr.kd_jenis_prw,"
                + "rawat_inap_dr.tarif_tindakandr,rawat_inap_dr.kso,rawat_inap_dr.material,rawat_inap_dr.bhp,rawat_inap_dr.menejemen "
                + "from pasien inner join reg_periksa on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                + "inner join rawat_inap_dr on rawat_inap_dr.no_rawat=reg_periksa.no_rawat "
                + "inner join jns_perawatan_inap on rawat_inap_dr.kd_jenis_prw=jns_perawatan_inap.kd_jenis_prw "
                + "inner join dokter on rawat_inap_dr.kd_dokter=dokter.kd_dokter "
                + "where rawat_inap_dr.no_rawat=? order by rawat_inap_dr.tgl_perawatan,rawat_inap_dr.jam_rawat");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeDr.addRow(new Object[]{false, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getDouble(9),
                    rs.getString("kd_jenis_prw"), rs.getString("tarif_tindakandr"), rs.getString("kso"),
                    rs.getString("material"), rs.getString("bhp"), rs.getString("menejemen")});
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilDr: " + e); }
    }

    // ========== TAB PENANGANAN PETUGAS ==========

    private DefaultTableModel tabModePr;
    private widget.Table tbRawatPr;
    private widget.TextBox KdPtgPen, TPetugasPen, TKdPrwPr, TNmPrwPr;

    private void setupTabPenangananPetugas() {
        tabModePr = new DefaultTableModel(null, new Object[]{
            "P", "No.Rawat", "No.R.M.", "Nama Pasien", "Perawatan/Tindakan", "NIP",
            "Petugas Yg Menangani", "Tgl.Rawat", "Jam Rawat", "Biaya", "Kode Prw",
            "Tarif Perawat", "KSO", "Jasa Sarana", "BHP", "Menejemen"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return c == 0; }
            Class[] types = new Class[]{Boolean.class, Object.class, Object.class, Object.class,
                Object.class, Object.class, Object.class, Object.class, Object.class, Double.class,
                Object.class, Object.class, Object.class, Object.class, Object.class, Object.class};
            @Override public Class getColumnClass(int c) { return types[c]; }
        };
        tbRawatPr = new widget.Table();
        tbRawatPr.setModel(tabModePr);
        tbRawatPr.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] wPr = {20,105,70,150,200,80,150,65,50,80,80,80,60,70,60,70};
        for (int c = 0; c < wPr.length; c++) tbRawatPr.getColumnModel().getColumn(c).setPreferredWidth(wPr[c]);
        tbRawatPr.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbRawatPr.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (tbRawatPr.getSelectedRow() != -1) {
                    KdPtgPen.setText(tbRawatPr.getValueAt(tbRawatPr.getSelectedRow(), 5).toString());
                    TPetugasPen.setText(tbRawatPr.getValueAt(tbRawatPr.getSelectedRow(), 6).toString());
                    TKdPrwPr.setText(tbRawatPr.getValueAt(tbRawatPr.getSelectedRow(), 10).toString());
                    TNmPrwPr.setText(tbRawatPr.getValueAt(tbRawatPr.getSelectedRow(), 4).toString());
                }
            }
        });

        widget.InternalFrame ifPr = new widget.InternalFrame();
        ifPr.setBackground(new java.awt.Color(235, 255, 235));
        ifPr.setBorder(null);
        ifPr.setLayout(new java.awt.BorderLayout(1, 1));
        widget.ScrollPane scPr = new widget.ScrollPane();
        scPr.setViewportView(tbRawatPr);
        ifPr.add(scPr, java.awt.BorderLayout.CENTER);

        // Input panel - layout sama dengan DlgRawatInap
        widget.panelisi pnlPr = new widget.panelisi();
        pnlPr.setPreferredSize(new Dimension(44, 74));
        pnlPr.setLayout(null);

        KdPtgPen = new widget.TextBox();
        TPetugasPen = new widget.TextBox();
        TKdPrwPr = new widget.TextBox();
        TNmPrwPr = new widget.TextBox();

        // Row 1: Petugas
        widget.Label lblPetugas = new widget.Label();
        lblPetugas.setText("Petugas :");
        pnlPr.add(lblPetugas);
        lblPetugas.setBounds(0, 10, 105, 23);

        KdPtgPen.setName("KdPtgPen");
        KdPtgPen.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    TPetugasPen.setText(Sequel.cariIsi("select nama from petugas where nip=?", KdPtgPen.getText()));
                }
            }
        });
        pnlPr.add(KdPtgPen);
        KdPtgPen.setBounds(108, 10, 130, 23);

        TPetugasPen.setEditable(false);
        TPetugasPen.setName("TPetugasPen");
        pnlPr.add(TPetugasPen);
        TPetugasPen.setBounds(240, 10, 606, 23);

        widget.Button btnSeekPetugas = new widget.Button();
        btnSeekPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnSeekPetugas.setToolTipText("Cari Petugas");
        btnSeekPetugas.addActionListener(e -> {
            DlgCariPetugas dlg = new DlgCariPetugas(null, true);
            dlg.emptTeks();
            dlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            dlg.setLocationRelativeTo(internalFrame1);
            dlg.setVisible(true);
            if (dlg.getTable().getSelectedRow() != -1) {
                KdPtgPen.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 0).toString());
                TPetugasPen.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 1).toString());
            }
        });
        pnlPr.add(btnSeekPetugas);
        btnSeekPetugas.setBounds(849, 10, 28, 23);

        // Row 2: Tindakan/Tagihan
        widget.Label lblTindakanPr = new widget.Label();
        lblTindakanPr.setText("Tindakan/Tagihan :");
        pnlPr.add(lblTindakanPr);
        lblTindakanPr.setBounds(0, 40, 105, 23);

        TKdPrwPr.setName("TKdPrwPr");
        TKdPrwPr.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    TNmPrwPr.setText(Sequel.cariIsi("select nm_perawatan from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwPr.getText()));
                }
            }
        });
        pnlPr.add(TKdPrwPr);
        TKdPrwPr.setBounds(108, 40, 100, 23);

        TNmPrwPr.setEditable(false);
        TNmPrwPr.setName("TNmPrwPr");
        pnlPr.add(TNmPrwPr);
        TNmPrwPr.setBounds(210, 40, 605, 23);

        widget.Button btnTindakanPr = new widget.Button();
        btnTindakanPr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnTindakanPr.setToolTipText("Cari Tindakan");
        btnTindakanPr.addActionListener(e -> {
            keuangan.DlgCariPerawatanRanap dlg = new keuangan.DlgCariPerawatanRanap(null, true);
            dlg.emptTeks();
            dlg.isCek();
            dlg.setNoRm(TNoRw.getText(), "rawat_inap_pr", Tanggal.getDate(),
                Jam.getSelectedItem().toString(), Menit.getSelectedItem().toString(),
                Detik.getSelectedItem().toString(), true, TPasien.getText());
            dlg.tampil();
            dlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            dlg.setLocationRelativeTo(internalFrame1);
            dlg.setVisible(true);
            if (dlg.getTable().getSelectedRow() != -1) {
                TKdPrwPr.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 0).toString());
                TNmPrwPr.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 1).toString());
            }
        });
        pnlPr.add(btnTindakanPr);
        btnTindakanPr.setBounds(818, 40, 28, 23);

        widget.Button btnTindakanPr2 = new widget.Button();
        btnTindakanPr2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnTindakanPr2.setToolTipText("Cari Tindakan 2");
        btnTindakanPr2.addActionListener(e -> {
            keuangan.DlgCariPerawatanRanap2 dlg = new keuangan.DlgCariPerawatanRanap2(null, true);
            dlg.isCek();
            dlg.setNoRm(TNoRw.getText(), "rawat_inap_pr", Tanggal.getDate(),
                Jam.getSelectedItem().toString(), Menit.getSelectedItem().toString(),
                Detik.getSelectedItem().toString(), true, TPasien.getText());
            dlg.tampil();
            dlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            dlg.setLocationRelativeTo(internalFrame1);
            dlg.setVisible(true);
            if (dlg.getTable().getSelectedRow() != -1) {
                TKdPrwPr.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 0).toString());
                TNmPrwPr.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 1).toString());
            }
        });
        pnlPr.add(btnTindakanPr2);
        btnTindakanPr2.setBounds(849, 40, 28, 23);

        ifPr.add(pnlPr, java.awt.BorderLayout.PAGE_START);
        TabICU.addTab("Penanganan Petugas", ifPr);
    }

    private void simpanPenangananPr() {
        if (KdPtgPen.getText().trim().isEmpty()) { Valid.textKosong(KdPtgPen, "Petugas"); return; }
        if (TKdPrwPr.getText().trim().isEmpty()) { Valid.textKosong(TKdPrwPr, "Tindakan"); return; }
        if (Sequel.cariRegistrasi(TNoRw.getText()) > 0) {
            JOptionPane.showMessageDialog(null, "Data billing sudah terverifikasi ..!!"); return;
        }
        String tgl = Valid.SetTgl(Tanggal.getSelectedItem() + "");
        String jam = Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem();
        String totalBiaya = Sequel.cariIsi("select total_byrpr from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwPr.getText());
        String material = Sequel.cariIsi("select material from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwPr.getText());
        String bhp = Sequel.cariIsi("select bhp from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwPr.getText());
        String tarifPr = Sequel.cariIsi("select tarif_tindakanpr from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwPr.getText());
        String kso = Sequel.cariIsi("select kso from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwPr.getText());
        String menejemen = Sequel.cariIsi("select menejemen from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwPr.getText());

        if (Sequel.menyimpantf("rawat_inap_pr", "?,?,?,?,?,?,?,?,?,?,?", "Data", 11, new String[]{
            TNoRw.getText(), TKdPrwPr.getText(), KdPtgPen.getText(), tgl, jam,
            material, bhp, tarifPr, kso, menejemen, totalBiaya
        })) {
            tampilPenangananPr();
            KdPtgPen.setText(""); TPetugasPen.setText(""); TKdPrwPr.setText(""); TNmPrwPr.setText("");
        }
    }

    private void hapusPenangananPr() {
        if (tbRawatPr.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih data yang mau dihapus..!!"); return; }
        if (Sequel.cariRegistrasi(tbRawatPr.getValueAt(tbRawatPr.getSelectedRow(), 1).toString()) > 0) {
            JOptionPane.showMessageDialog(null, "Data billing sudah terverifikasi ..!!"); return;
        }
        Sequel.queryu2("delete from rawat_inap_pr where no_rawat=? and kd_jenis_prw=? and nip=? and tgl_perawatan=? and jam_rawat=?", 5, new String[]{
            tbRawatPr.getValueAt(tbRawatPr.getSelectedRow(), 1).toString(),
            tbRawatPr.getValueAt(tbRawatPr.getSelectedRow(), 10).toString(),
            tbRawatPr.getValueAt(tbRawatPr.getSelectedRow(), 5).toString(),
            tbRawatPr.getValueAt(tbRawatPr.getSelectedRow(), 7).toString(),
            tbRawatPr.getValueAt(tbRawatPr.getSelectedRow(), 8).toString()
        });
        tampilPenangananPr();
    }

    private void tampilPenangananPr() {
        Valid.tabelKosong(tabModePr);
        try {
            ps = koneksi.prepareStatement(
                "select rawat_inap_pr.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"
                + "concat(rawat_inap_pr.kd_jenis_prw,' ',jns_perawatan_inap.nm_perawatan),rawat_inap_pr.nip,petugas.nama,"
                + "rawat_inap_pr.tgl_perawatan,rawat_inap_pr.jam_rawat,rawat_inap_pr.biaya_rawat,rawat_inap_pr.kd_jenis_prw,"
                + "rawat_inap_pr.tarif_tindakanpr,rawat_inap_pr.kso,rawat_inap_pr.material,rawat_inap_pr.bhp,rawat_inap_pr.menejemen "
                + "from pasien inner join reg_periksa on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                + "inner join rawat_inap_pr on rawat_inap_pr.no_rawat=reg_periksa.no_rawat "
                + "inner join jns_perawatan_inap on rawat_inap_pr.kd_jenis_prw=jns_perawatan_inap.kd_jenis_prw "
                + "inner join petugas on rawat_inap_pr.nip=petugas.nip "
                + "where rawat_inap_pr.no_rawat=? order by rawat_inap_pr.tgl_perawatan,rawat_inap_pr.jam_rawat");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModePr.addRow(new Object[]{false, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getDouble(9),
                    rs.getString("kd_jenis_prw"), rs.getString("tarif_tindakanpr"), rs.getString("kso"),
                    rs.getString("material"), rs.getString("bhp"), rs.getString("menejemen")});
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilPr: " + e); }
    }

    // ========== TAB PENANGANAN DOKTER & PETUGAS ==========

    private DefaultTableModel tabModeDrPr;
    private widget.Table tbRawatDrPr;
    private widget.TextBox KdDokDrPr, TDokterDrPr, KdPtgDrPr, TPetugasDrPr, TKdPrwDrPr, TNmPrwDrPr;

    private void setupTabPenangananDokterPetugas() {
        tabModeDrPr = new DefaultTableModel(null, new Object[]{
            "P", "No.Rawat", "No.R.M.", "Nama Pasien", "Perawatan/Tindakan", "Kode Dokter",
            "Dokter Yg Menangani", "NIP", "Petugas Yg Menangani", "Tgl.Rawat", "Jam Rawat",
            "Biaya", "Kode Prw", "Tarif Dokter", "Tarif Perawat", "KSO", "Jasa Sarana", "BHP", "Menejemen"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return c == 0; }
            Class[] types = new Class[]{Boolean.class, Object.class, Object.class, Object.class,
                Object.class, Object.class, Object.class, Object.class, Object.class, Object.class,
                Object.class, Double.class, Object.class, Object.class, Object.class, Object.class,
                Object.class, Object.class, Object.class};
            @Override public Class getColumnClass(int c) { return types[c]; }
        };
        tbRawatDrPr = new widget.Table();
        tbRawatDrPr.setModel(tabModeDrPr);
        tbRawatDrPr.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] wDrPr = {20,105,70,150,200,80,150,80,150,65,50,80,80,80,80,60,70,60,70};
        for (int c = 0; c < wDrPr.length; c++) tbRawatDrPr.getColumnModel().getColumn(c).setPreferredWidth(wDrPr[c]);
        tbRawatDrPr.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbRawatDrPr.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (tbRawatDrPr.getSelectedRow() != -1) {
                    KdDokDrPr.setText(tbRawatDrPr.getValueAt(tbRawatDrPr.getSelectedRow(), 5).toString());
                    TDokterDrPr.setText(tbRawatDrPr.getValueAt(tbRawatDrPr.getSelectedRow(), 6).toString());
                    KdPtgDrPr.setText(tbRawatDrPr.getValueAt(tbRawatDrPr.getSelectedRow(), 7).toString());
                    TPetugasDrPr.setText(tbRawatDrPr.getValueAt(tbRawatDrPr.getSelectedRow(), 8).toString());
                    TKdPrwDrPr.setText(tbRawatDrPr.getValueAt(tbRawatDrPr.getSelectedRow(), 12).toString());
                    TNmPrwDrPr.setText(tbRawatDrPr.getValueAt(tbRawatDrPr.getSelectedRow(), 4).toString());
                }
            }
        });

        widget.InternalFrame ifDrPr = new widget.InternalFrame();
        ifDrPr.setBackground(new java.awt.Color(235, 255, 235));
        ifDrPr.setBorder(null);
        ifDrPr.setLayout(new java.awt.BorderLayout(1, 1));
        widget.ScrollPane scDrPr = new widget.ScrollPane();
        scDrPr.setViewportView(tbRawatDrPr);
        ifDrPr.add(scDrPr, java.awt.BorderLayout.CENTER);

        // Input panel - layout sama dengan DlgRawatInap (3 rows)
        widget.panelisi pnlDrPr = new widget.panelisi();
        pnlDrPr.setPreferredSize(new Dimension(44, 104));
        pnlDrPr.setLayout(null);

        KdDokDrPr = new widget.TextBox();
        TDokterDrPr = new widget.TextBox();
        KdPtgDrPr = new widget.TextBox();
        TPetugasDrPr = new widget.TextBox();
        TKdPrwDrPr = new widget.TextBox();
        TNmPrwDrPr = new widget.TextBox();

        // Row 1: Dokter
        widget.Label lblDokterDrPr = new widget.Label();
        lblDokterDrPr.setText("Dokter :");
        pnlDrPr.add(lblDokterDrPr);
        lblDokterDrPr.setBounds(0, 10, 105, 23);

        KdDokDrPr.setName("KdDokDrPr");
        KdDokDrPr.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    TDokterDrPr.setText(Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", KdDokDrPr.getText()));
                }
            }
        });
        pnlDrPr.add(KdDokDrPr);
        KdDokDrPr.setBounds(108, 10, 130, 23);

        TDokterDrPr.setEditable(false);
        pnlDrPr.add(TDokterDrPr);
        TDokterDrPr.setBounds(240, 10, 606, 23);

        widget.Button btnSeekDokterDrPr = new widget.Button();
        btnSeekDokterDrPr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnSeekDokterDrPr.setToolTipText("Cari Dokter");
        btnSeekDokterDrPr.addActionListener(e -> {
            kepegawaian.DlgCariDokter dlg = new kepegawaian.DlgCariDokter(null, true);
            dlg.emptTeks();
            dlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            dlg.setLocationRelativeTo(internalFrame1);
            dlg.setVisible(true);
            if (dlg.getTable().getSelectedRow() != -1) {
                KdDokDrPr.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 0).toString());
                TDokterDrPr.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 1).toString());
            }
        });
        pnlDrPr.add(btnSeekDokterDrPr);
        btnSeekDokterDrPr.setBounds(849, 10, 28, 23);

        // Row 2: Petugas
        widget.Label lblPetugasDrPr = new widget.Label();
        lblPetugasDrPr.setText("Petugas :");
        pnlDrPr.add(lblPetugasDrPr);
        lblPetugasDrPr.setBounds(0, 40, 105, 23);

        KdPtgDrPr.setName("KdPtgDrPr");
        KdPtgDrPr.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    TPetugasDrPr.setText(Sequel.cariIsi("select nama from petugas where nip=?", KdPtgDrPr.getText()));
                }
            }
        });
        pnlDrPr.add(KdPtgDrPr);
        KdPtgDrPr.setBounds(108, 40, 130, 23);

        TPetugasDrPr.setEditable(false);
        pnlDrPr.add(TPetugasDrPr);
        TPetugasDrPr.setBounds(240, 40, 606, 23);

        widget.Button btnSeekPetugasDrPr = new widget.Button();
        btnSeekPetugasDrPr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnSeekPetugasDrPr.setToolTipText("Cari Petugas");
        btnSeekPetugasDrPr.addActionListener(e -> {
            DlgCariPetugas dlg = new DlgCariPetugas(null, true);
            dlg.emptTeks();
            dlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            dlg.setLocationRelativeTo(internalFrame1);
            dlg.setVisible(true);
            if (dlg.getTable().getSelectedRow() != -1) {
                KdPtgDrPr.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 0).toString());
                TPetugasDrPr.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 1).toString());
            }
        });
        pnlDrPr.add(btnSeekPetugasDrPr);
        btnSeekPetugasDrPr.setBounds(849, 40, 28, 23);

        // Row 3: Tindakan/Tagihan
        widget.Label lblTindakanDrPr = new widget.Label();
        lblTindakanDrPr.setText("Tindakan/Tagihan :");
        pnlDrPr.add(lblTindakanDrPr);
        lblTindakanDrPr.setBounds(0, 70, 105, 23);

        TKdPrwDrPr.setName("TKdPrwDrPr");
        TKdPrwDrPr.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    TNmPrwDrPr.setText(Sequel.cariIsi("select nm_perawatan from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwDrPr.getText()));
                }
            }
        });
        pnlDrPr.add(TKdPrwDrPr);
        TKdPrwDrPr.setBounds(108, 70, 100, 23);

        TNmPrwDrPr.setEditable(false);
        pnlDrPr.add(TNmPrwDrPr);
        TNmPrwDrPr.setBounds(210, 70, 605, 23);

        widget.Button btnTindakanDrPr = new widget.Button();
        btnTindakanDrPr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnTindakanDrPr.setToolTipText("Cari Tindakan");
        btnTindakanDrPr.addActionListener(e -> {
            keuangan.DlgCariPerawatanRanap dlg = new keuangan.DlgCariPerawatanRanap(null, true);
            dlg.emptTeks();
            dlg.isCek();
            dlg.setNoRm(TNoRw.getText(), "rawat_inap_drpr", Tanggal.getDate(),
                Jam.getSelectedItem().toString(), Menit.getSelectedItem().toString(),
                Detik.getSelectedItem().toString(), true, TPasien.getText());
            dlg.tampil();
            dlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            dlg.setLocationRelativeTo(internalFrame1);
            dlg.setVisible(true);
            if (dlg.getTable().getSelectedRow() != -1) {
                TKdPrwDrPr.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 0).toString());
                TNmPrwDrPr.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 1).toString());
            }
        });
        pnlDrPr.add(btnTindakanDrPr);
        btnTindakanDrPr.setBounds(818, 70, 28, 23);

        widget.Button btnTindakanDrPr2 = new widget.Button();
        btnTindakanDrPr2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnTindakanDrPr2.setToolTipText("Cari Tindakan 2");
        btnTindakanDrPr2.addActionListener(e -> {
            keuangan.DlgCariPerawatanRanap2 dlg = new keuangan.DlgCariPerawatanRanap2(null, true);
            dlg.isCek();
            dlg.setNoRm(TNoRw.getText(), "rawat_inap_drpr", Tanggal.getDate(),
                Jam.getSelectedItem().toString(), Menit.getSelectedItem().toString(),
                Detik.getSelectedItem().toString(), true, TPasien.getText());
            dlg.tampil();
            dlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            dlg.setLocationRelativeTo(internalFrame1);
            dlg.setVisible(true);
            if (dlg.getTable().getSelectedRow() != -1) {
                TKdPrwDrPr.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 0).toString());
                TNmPrwDrPr.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 1).toString());
            }
        });
        pnlDrPr.add(btnTindakanDrPr2);
        btnTindakanDrPr2.setBounds(849, 70, 28, 23);

        ifDrPr.add(pnlDrPr, java.awt.BorderLayout.PAGE_START);
        TabICU.addTab("Penanganan Dokter & Petugas", ifDrPr);
    }

    private void simpanPenangananDrPr() {
        if (KdDokDrPr.getText().trim().isEmpty()) { Valid.textKosong(KdDokDrPr, "Dokter"); return; }
        if (KdPtgDrPr.getText().trim().isEmpty()) { Valid.textKosong(KdPtgDrPr, "Petugas"); return; }
        if (TKdPrwDrPr.getText().trim().isEmpty()) { Valid.textKosong(TKdPrwDrPr, "Tindakan"); return; }
        if (Sequel.cariRegistrasi(TNoRw.getText()) > 0) {
            JOptionPane.showMessageDialog(null, "Data billing sudah terverifikasi ..!!"); return;
        }
        String tgl = Valid.SetTgl(Tanggal.getSelectedItem() + "");
        String jam = Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem();
        String totalBiaya = Sequel.cariIsi("select total_byrdrpr from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwDrPr.getText());
        String material = Sequel.cariIsi("select material from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwDrPr.getText());
        String bhp = Sequel.cariIsi("select bhp from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwDrPr.getText());
        String tarifDr = Sequel.cariIsi("select tarif_tindakandr from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwDrPr.getText());
        String tarifPr = Sequel.cariIsi("select tarif_tindakanpr from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwDrPr.getText());
        String kso = Sequel.cariIsi("select kso from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwDrPr.getText());
        String menejemen = Sequel.cariIsi("select menejemen from jns_perawatan_inap where kd_jenis_prw=?", TKdPrwDrPr.getText());

        if (Sequel.menyimpantf("rawat_inap_drpr", "?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 13, new String[]{
            TNoRw.getText(), TKdPrwDrPr.getText(), KdDokDrPr.getText(), KdPtgDrPr.getText(), tgl, jam,
            material, bhp, tarifDr, tarifPr, kso, menejemen, totalBiaya
        })) {
            tampilPenangananDrPr();
            KdDokDrPr.setText(""); TDokterDrPr.setText(""); KdPtgDrPr.setText(""); TPetugasDrPr.setText("");
            TKdPrwDrPr.setText(""); TNmPrwDrPr.setText("");
        }
    }

    private void hapusPenangananDrPr() {
        if (tbRawatDrPr.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih data yang mau dihapus..!!"); return; }
        if (Sequel.cariRegistrasi(tbRawatDrPr.getValueAt(tbRawatDrPr.getSelectedRow(), 1).toString()) > 0) {
            JOptionPane.showMessageDialog(null, "Data billing sudah terverifikasi ..!!"); return;
        }
        Sequel.queryu2("delete from rawat_inap_drpr where no_rawat=? and kd_jenis_prw=? and kd_dokter=? and nip=? and tgl_perawatan=? and jam_rawat=?", 6, new String[]{
            tbRawatDrPr.getValueAt(tbRawatDrPr.getSelectedRow(), 1).toString(),
            tbRawatDrPr.getValueAt(tbRawatDrPr.getSelectedRow(), 12).toString(),
            tbRawatDrPr.getValueAt(tbRawatDrPr.getSelectedRow(), 5).toString(),
            tbRawatDrPr.getValueAt(tbRawatDrPr.getSelectedRow(), 7).toString(),
            tbRawatDrPr.getValueAt(tbRawatDrPr.getSelectedRow(), 9).toString(),
            tbRawatDrPr.getValueAt(tbRawatDrPr.getSelectedRow(), 10).toString()
        });
        tampilPenangananDrPr();
    }

    private void tampilPenangananDrPr() {
        Valid.tabelKosong(tabModeDrPr);
        try {
            ps = koneksi.prepareStatement(
                "select rawat_inap_drpr.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"
                + "concat(rawat_inap_drpr.kd_jenis_prw,' ',jns_perawatan_inap.nm_perawatan),"
                + "rawat_inap_drpr.kd_dokter,dokter.nm_dokter,rawat_inap_drpr.nip,petugas.nama,"
                + "rawat_inap_drpr.tgl_perawatan,rawat_inap_drpr.jam_rawat,rawat_inap_drpr.biaya_rawat,"
                + "rawat_inap_drpr.kd_jenis_prw,rawat_inap_drpr.tarif_tindakandr,rawat_inap_drpr.tarif_tindakanpr,"
                + "rawat_inap_drpr.kso,rawat_inap_drpr.material,rawat_inap_drpr.bhp,rawat_inap_drpr.menejemen "
                + "from pasien inner join reg_periksa on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                + "inner join rawat_inap_drpr on rawat_inap_drpr.no_rawat=reg_periksa.no_rawat "
                + "inner join jns_perawatan_inap on rawat_inap_drpr.kd_jenis_prw=jns_perawatan_inap.kd_jenis_prw "
                + "inner join dokter on rawat_inap_drpr.kd_dokter=dokter.kd_dokter "
                + "inner join petugas on rawat_inap_drpr.nip=petugas.nip "
                + "where rawat_inap_drpr.no_rawat=? order by rawat_inap_drpr.tgl_perawatan,rawat_inap_drpr.jam_rawat");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeDrPr.addRow(new Object[]{false, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8),
                    rs.getString(9), rs.getString(10), rs.getDouble(11),
                    rs.getString("kd_jenis_prw"), rs.getString("tarif_tindakandr"), rs.getString("tarif_tindakanpr"),
                    rs.getString("kso"), rs.getString("material"), rs.getString("bhp"), rs.getString("menejemen")});
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilDrPr: " + e); }
    }

    // ========== GRAFIK BALANCE CAIRAN ==========

    private widget.Button BtnGrafikBalance;

    private void tampilGrafikBalanceCairan() {
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            java.util.List<String> listTgl = new java.util.ArrayList<>();
            java.util.List<Double> listIntake = new java.util.ArrayList<>();
            java.util.List<Double> listOutput = new java.util.ArrayList<>();
            java.util.List<Double> listBalance = new java.util.ArrayList<>();

            ps = koneksi.prepareStatement(
                "select tgl_observasi, SUM(IFNULL(kumulatif,0)) as total_intake, "
                + "SUM(IFNULL(output_kumulatif,0)) as total_output, "
                + "SUM(IFNULL(keseimbangan_cairan,0)) as balance "
                + "from observasi_icu_intake_output where no_rawat=? "
                + "group by tgl_observasi order by tgl_observasi");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                listTgl.add(rs.getString(1));
                listIntake.add(rs.getDouble(2));
                listOutput.add(rs.getDouble(3));
                listBalance.add(rs.getDouble(4));
            }
            rs.close(); ps.close();

            if (listTgl.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Belum ada data intake-output untuk pasien ini");
                this.setCursor(Cursor.getDefaultCursor());
                return;
            }

            javax.swing.JDialog dlg = new javax.swing.JDialog((java.awt.Frame) null, "Grafik Balance Cairan - " + TPasien.getText(), true);
            dlg.setSize(900, 500);
            dlg.setLocationRelativeTo(internalFrame1);

            javax.swing.JPanel panel = new javax.swing.JPanel() {
                @Override
                protected void paintComponent(java.awt.Graphics g) {
                    super.paintComponent(g);
                    java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
                    g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight(), left = 70, right = 20, top = 40, bottom = 70;
                    int gw = w - left - right, gh = h - top - bottom;
                    int n = listTgl.size();

                    g2.setColor(java.awt.Color.WHITE);
                    g2.fillRect(0, 0, w, h);
                    g2.setColor(java.awt.Color.BLACK);
                    g2.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 14));
                    g2.drawString("Grafik Balance Cairan (Intake vs Output)", w / 2 - 180, 25);

                    // Find max value
                    double maxVal = 100;
                    for (int i = 0; i < n; i++) {
                        maxVal = Math.max(maxVal, Math.abs(listIntake.get(i)));
                        maxVal = Math.max(maxVal, Math.abs(listOutput.get(i)));
                        maxVal = Math.max(maxVal, Math.abs(listBalance.get(i)));
                    }
                    maxVal = Math.ceil(maxVal / 100) * 100;
                    if (maxVal == 0) maxVal = 100;

                    // Grid
                    g2.setFont(new java.awt.Font("Tahoma", 0, 9));
                    int barWidth = Math.max(20, gw / (n * 4));
                    int midY = top + gh / 2;

                    // Zero line
                    g2.setColor(java.awt.Color.DARK_GRAY);
                    g2.drawLine(left, midY, left + gw, midY);
                    g2.drawString("0", left - 15, midY + 4);

                    // Scale lines
                    g2.setColor(new java.awt.Color(220, 220, 220));
                    for (int yi = 1; yi <= 4; yi++) {
                        int yUp = midY - (yi * gh / 8);
                        int yDown = midY + (yi * gh / 8);
                        g2.drawLine(left, yUp, left + gw, yUp);
                        g2.drawLine(left, yDown, left + gw, yDown);
                        g2.setColor(java.awt.Color.GRAY);
                        g2.drawString(String.format("%.0f", maxVal * yi / 4), 5, yUp + 4);
                        g2.drawString(String.format("%.0f", -maxVal * yi / 4), 5, yDown + 4);
                        g2.setColor(new java.awt.Color(220, 220, 220));
                    }

                    // Bars
                    for (int i = 0; i < n; i++) {
                        int cx = left + (i * gw / n) + gw / (n * 2);

                        // Intake (hijau)
                        int hIntake = (int) (listIntake.get(i) / maxVal * (gh / 2));
                        g2.setColor(new java.awt.Color(100, 200, 100));
                        g2.fillRect(cx - barWidth, midY - hIntake, barWidth, hIntake);

                        // Output (merah)
                        int hOutput = (int) (listOutput.get(i) / maxVal * (gh / 2));
                        g2.setColor(new java.awt.Color(220, 100, 100));
                        g2.fillRect(cx, midY, barWidth, hOutput);

                        // Balance line dot
                        int yBal = midY - (int) (listBalance.get(i) / maxVal * (gh / 2));
                        g2.setColor(java.awt.Color.BLUE);
                        g2.fillOval(cx - 4, yBal - 4, 8, 8);
                        if (i > 0) {
                            int prevCx = left + ((i - 1) * gw / n) + gw / (n * 2);
                            int prevYBal = midY - (int) (listBalance.get(i - 1) / maxVal * (gh / 2));
                            g2.setStroke(new java.awt.BasicStroke(2f));
                            g2.drawLine(prevCx, prevYBal, cx, yBal);
                        }

                        // X label
                        g2.setColor(java.awt.Color.DARK_GRAY);
                        g2.setFont(new java.awt.Font("Tahoma", 0, 9));
                        g2.drawString(listTgl.get(i), cx - barWidth, top + gh + 15);
                    }

                    // Legend
                    int lx = left + 10, ly = top + gh + 35;
                    g2.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 11));
                    g2.setColor(new java.awt.Color(100, 200, 100)); g2.fillRect(lx, ly, 15, 12); g2.drawString("Intake", lx + 20, ly + 11); lx += 100;
                    g2.setColor(new java.awt.Color(220, 100, 100)); g2.fillRect(lx, ly, 15, 12); g2.drawString("Output", lx + 20, ly + 11); lx += 100;
                    g2.setColor(java.awt.Color.BLUE); g2.fillOval(lx, ly + 2, 10, 10); g2.drawString("Balance", lx + 15, ly + 11);
                }
            };
            dlg.setContentPane(panel);
            dlg.setVisible(true);
        } catch (Exception ex) {
            System.out.println("Error grafik balance: " + ex);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    // ========== CATATAN PENGOBATAN DETAIL JAM ==========

    private static final String[] JAM_LABELS = {"07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","01","02","03","04","05","06"};

    // Tipe baris kardex cairan (Intake-Output). Prefix "Intake"/"Output" dipakai
    // untuk hitung Total Intake/Output & Balance; "Output - Urine" untuk Diuresis.
    private static final String[] TIPE_CAIRAN = {
        "Intake Parenteral", "Intake Enteral",
        "Output - Urine", "Output - NGT/OGT", "Output - BAB",
        "Output - Drain", "Output - Muntah", "Output - IWL"
    };

    // Guard agar setValueAt programatik (revert/refresh) di kolom Paraf Dokter
    // atau jam tidak memicu ulang listener simpan
    private boolean suppressTableEvent = false;

    private void simpanDetailJam(int row, int col) {
        if (NIPHeader.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Isi Kode Petugas di panel atas terlebih dahulu!");
            return;
        }
        try {
            String noRawat = tabModeMedikasi.getValueAt(row, 0).toString();
            String tglObs = tabModeMedikasi.getValueAt(row, 1).toString();
            String noUrut = tabModeMedikasi.getValueAt(row, 2).toString();
            String jam = JAM_LABELS[col - 9];
            Object val = tabModeMedikasi.getValueAt(row, col);
            String status = (val == null) ? "" : val.toString();

            // Ambil isian existing (status & petugas pembuat) untuk otorisasi
            String existingStatus = "", existingNip = "";
            ps = koneksi.prepareStatement("select status, nip_petugas from observasi_icu_catatan_pengobatan_detail "
                + "where no_rawat=? and tgl_observasi=? and no_urut=? and jam=?");
            ps.setString(1, noRawat); ps.setString(2, tglObs); ps.setString(3, noUrut); ps.setString(4, jam);
            rs = ps.executeQuery();
            if (rs.next()) { existingStatus = rs.getString(1); existingNip = rs.getString(2) == null ? "" : rs.getString(2); }
            rs.close(); ps.close();

            // Otorisasi: centang "✓" yang sudah ada hanya boleh diubah/dihapus oleh
            // petugas yang membuatnya. Petugas lain → tolak & kembalikan ke "✓".
            if ("✓".equals(existingStatus) && !existingNip.equals(NIPHeader.getText()) && !"✓".equals(status)) {
                String namaPetugas = Sequel.cariIsi("select nama from petugas where nip=?", existingNip);
                JOptionPane.showMessageDialog(null,
                    "Centang ini dibuat oleh petugas " + (namaPetugas.isEmpty() ? existingNip : namaPetugas) + ".\n"
                    + "Hanya petugas tersebut yang dapat menghapus/mengubahnya.");
                setCellSilently(tabModeMedikasi, row, col, "✓");
                return;
            }

            if (status.isEmpty()) {
                // Hapus detail jika dikosongkan
                Sequel.queryu2("delete from observasi_icu_catatan_pengobatan_detail where no_rawat=? and tgl_observasi=? and no_urut=? and jam=?",
                    4, new String[]{noRawat, tglObs, noUrut, jam});
            } else {
                // INSERT ON DUPLICATE KEY UPDATE
                Sequel.queryu2("insert into observasi_icu_catatan_pengobatan_detail (no_rawat,tgl_observasi,no_urut,jam,status,nip_petugas) "
                    + "values (?,?,?,?,?,?) on duplicate key update status=values(status),nip_petugas=values(nip_petugas),waktu_input=current_timestamp",
                    6, new String[]{noRawat, tglObs, noUrut, jam, status, NIPHeader.getText()});
            }
        } catch (Exception e) {
            System.out.println("Notifikasi simpanDetailJam: " + e);
        }
    }

    /**
     * Dipanggil saat kolom "Paraf Dokter" (checkbox, index 9) di-toggle.
     * Centang  → buka dialog cari dokter; dokter memilih dirinya, lalu
     *            tersimpan kd_dokter, nm_dokter, paraf_dokter='Y', tgl_paraf=NOW().
     * Un-check → paraf dibatalkan (kosongkan kd_dokter/nm_dokter/tgl_paraf).
     */
    private void simpanParafDokter(int row) {
        String noRawat = tabModeMedikasi.getValueAt(row, 0).toString();
        String tglObs  = tabModeMedikasi.getValueAt(row, 1).toString();
        String noUrut  = tabModeMedikasi.getValueAt(row, 2).toString();
        Object val = tabModeMedikasi.getValueAt(row, 8);
        boolean checked = (val instanceof Boolean) && (Boolean) val;

        if (checked) {
            // Dokter memilih dirinya sendiri
            kepegawaian.DlgCariDokter dlg = new kepegawaian.DlgCariDokter(null, true);
            dlg.emptTeks();
            dlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            dlg.setLocationRelativeTo(internalFrame1);
            dlg.setVisible(true);
            if (dlg.getTable().getSelectedRow() != -1) {
                String kdDokter = dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 0).toString();
                String nmDokter = dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 1).toString();
                if (Sequel.mengedittf("observasi_icu_catatan_pengobatan",
                    "no_rawat=? and tgl_observasi=? and no_urut=?",
                    "kd_dokter=?,nm_dokter=?,paraf_dokter='Y',tgl_paraf=NOW()", 5, new String[]{
                    kdDokter, nmDokter, noRawat, tglObs, noUrut
                })) {
                    tbMedikasi.setToolTipText("Paraf: " + nmDokter);
                } else {
                    revertParaf(row, false);
                }
            } else {
                // Batal pilih dokter → kembalikan checkbox ke kosong
                revertParaf(row, false);
            }
        } else {
            // Un-check → otorisasi: hanya dokter yang memberi paraf yang boleh menghapus.
            String storedKd = Sequel.cariIsi("select kd_dokter from observasi_icu_catatan_pengobatan "
                + "where no_rawat=? and tgl_observasi='" + tglObs + "' and no_urut='" + noUrut + "'", noRawat);
            kepegawaian.DlgCariDokter dlg = new kepegawaian.DlgCariDokter(null, true);
            dlg.emptTeks();
            dlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            dlg.setLocationRelativeTo(internalFrame1);
            dlg.setVisible(true);
            if (dlg.getTable().getSelectedRow() != -1) {
                String kd = dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 0).toString();
                if (kd.equals(storedKd)) {
                    Sequel.mengedittf("observasi_icu_catatan_pengobatan",
                        "no_rawat=? and tgl_observasi=? and no_urut=?",
                        "kd_dokter='',nm_dokter='',paraf_dokter='',tgl_paraf=NULL", 3, new String[]{
                        noRawat, tglObs, noUrut
                    });
                } else {
                    String nm = Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", storedKd);
                    JOptionPane.showMessageDialog(null,
                        "Paraf ini dibuat oleh dokter " + (nm.isEmpty() ? storedKd : nm) + ".\n"
                        + "Hanya dokter tersebut yang dapat menghapus paraf.");
                    setCellSilently(tabModeMedikasi, row, 8, true); // kembalikan tercentang
                }
            } else {
                // Batal pilih dokter → kembalikan tercentang
                setCellSilently(tabModeMedikasi, row, 8, true);
            }
        }
    }

    /** Set nilai cell tanpa memicu listener simpan (untuk revert/refresh programatik). */
    private void setCellSilently(DefaultTableModel model, int row, int col, Object value) {
        suppressTableEvent = true;
        model.setValueAt(value, row, col);
        suppressTableEvent = false;
    }

    /** Set nilai checkbox Paraf Dokter tanpa memicu listener simpanParafDokter. */
    private void revertParaf(int row, boolean value) {
        setCellSilently(tabModeMedikasi, row, 8, value);
    }

    // ========== KARDEX CAIRAN (Intake-Output) ==========

    /** Simpan jumlah cairan (ml) per (cairan, jam) saat sel jam (5-28) diubah. */
    private void simpanDetailJamCairan(int row, int col) {
        if (NIPHeader.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Isi Kode Petugas di panel atas terlebih dahulu!");
            return;
        }
        try {
            String noRawat = tabModeIntakeOutput.getValueAt(row, 0).toString();
            String tglObs = tabModeIntakeOutput.getValueAt(row, 1).toString();
            String noUrut = tabModeIntakeOutput.getValueAt(row, 2).toString();
            String jam = JAM_LABELS[col - 6];
            Object val = tabModeIntakeOutput.getValueAt(row, col);
            String jumlah = (val == null) ? "" : val.toString().trim();

            if (jumlah.isEmpty()) {
                Sequel.queryu2("delete from observasi_icu_cairan_detail where no_rawat=? and tgl_observasi=? and no_urut=? and jam=?",
                    4, new String[]{noRawat, tglObs, noUrut, jam});
            } else {
                Sequel.queryu2("insert into observasi_icu_cairan_detail (no_rawat,tgl_observasi,no_urut,jam,jumlah,nip_petugas) "
                    + "values (?,?,?,?,?,?) on duplicate key update jumlah=values(jumlah),nip_petugas=values(nip_petugas),waktu_input=current_timestamp",
                    6, new String[]{noRawat, tglObs, noUrut, jam, jumlah, NIPHeader.getText()});
            }
        } catch (Exception e) {
            System.out.println("Notifikasi simpanDetailJamCairan: " + e);
        }
    }

    /**
     * Dipanggil saat kolom "Paraf Perawat" (checkbox, index 4) di-toggle.
     * Centang  → rekam nip_perawat = petugas yang login (NIPHeader), tgl_paraf=NOW().
     * Un-check → hanya perawat pembuat paraf (nip_perawat) yang boleh menghapus.
     */
    private void simpanParafPerawat(int row) {
        String noRawat = tabModeIntakeOutput.getValueAt(row, 0).toString();
        String tglObs  = tabModeIntakeOutput.getValueAt(row, 1).toString();
        String noUrut  = tabModeIntakeOutput.getValueAt(row, 2).toString();
        Object val = tabModeIntakeOutput.getValueAt(row, 5);
        boolean checked = (val instanceof Boolean) && (Boolean) val;

        if (checked) {
            if (NIPHeader.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Isi Kode Petugas di panel atas terlebih dahulu!");
                setCellSilently(tabModeIntakeOutput, row, 5, false);
                return;
            }
            Sequel.mengedittf("observasi_icu_cairan",
                "no_rawat=? and tgl_observasi=? and no_urut=?",
                "nip_perawat=?,paraf_perawat='Y',tgl_paraf=NOW()", 4, new String[]{
                NIPHeader.getText(), noRawat, tglObs, noUrut
            });
        } else {
            // Un-check → otorisasi: hanya perawat pembuat paraf yang boleh menghapus
            String storedNip = Sequel.cariIsi("select nip_perawat from observasi_icu_cairan "
                + "where no_rawat=? and tgl_observasi='" + tglObs + "' and no_urut='" + noUrut + "'", noRawat);
            if (!storedNip.isEmpty() && !storedNip.equals(NIPHeader.getText())) {
                String nm = Sequel.cariIsi("select nama from petugas where nip=?", storedNip);
                JOptionPane.showMessageDialog(null,
                    "Paraf ini dibuat oleh perawat " + (nm.isEmpty() ? storedNip : nm) + ".\n"
                    + "Hanya perawat tersebut yang dapat menghapus paraf.");
                setCellSilently(tabModeIntakeOutput, row, 5, true);
                return;
            }
            Sequel.mengedittf("observasi_icu_cairan",
                "no_rawat=? and tgl_observasi=? and no_urut=?",
                "nip_perawat='',paraf_perawat='',tgl_paraf=NULL", 3, new String[]{
                noRawat, tglObs, noUrut
            });
        }
    }

    /** Simpan perubahan Tipe (dropdown kolom 3) ke master cairan. */
    private void simpanTipeCairan(int row) {
        String noRawat = tabModeIntakeOutput.getValueAt(row, 0).toString();
        String tglObs  = tabModeIntakeOutput.getValueAt(row, 1).toString();
        String noUrut  = tabModeIntakeOutput.getValueAt(row, 2).toString();
        Object tipe = tabModeIntakeOutput.getValueAt(row, 3);
        Sequel.mengedittf("observasi_icu_cairan",
            "no_rawat=? and tgl_observasi=? and no_urut=?",
            "tipe=?", 4, new String[]{
            tipe == null ? "" : tipe.toString(), noRawat, tglObs, noUrut
        });
    }

    /**
     * Hitung & tampilkan baris ringkasan otomatis (read-only) di bawah grid cairan:
     *   Total Intake/jam, Total Output/jam, Balance (kumulatif), Diuresis (ml/kgBB/jam).
     * Diuresis = urine(jam) ÷ BB, BB diambil dari tab Nutrisi (bb terakhir) pasien.
     */
    private void hitungRingkasanCairan() {
        suppressTableEvent = true;
        try {
            // Buang baris ringkasan lama (No.Rawat kosong)
            for (int i = tabModeIntakeOutput.getRowCount() - 1; i >= 0; i--) {
                Object nr = tabModeIntakeOutput.getValueAt(i, 0);
                if (nr == null || nr.toString().isEmpty()) tabModeIntakeOutput.removeRow(i);
            }
            int n = 24;
            double[] totIn = new double[n], totOut = new double[n], urine = new double[n];
            for (int r = 0; r < tabModeIntakeOutput.getRowCount(); r++) {
                Object nr = tabModeIntakeOutput.getValueAt(r, 0);
                if (nr == null || nr.toString().isEmpty()) continue;
                Object tipeObj = tabModeIntakeOutput.getValueAt(r, 3);
                String tipe = tipeObj == null ? "" : tipeObj.toString();
                for (int j = 0; j < n; j++) {
                    Object v = tabModeIntakeOutput.getValueAt(r, 6 + j);
                    double val = parseDoubleOrZero(v == null ? "" : v.toString());
                    if (tipe.startsWith("Intake")) totIn[j] += val;
                    else if (tipe.startsWith("Output")) totOut[j] += val;
                    if (tipe.equals("Output - Urine")) urine[j] += val;
                }
            }
            // BB dari tab Nutrisi (bb terakhir) untuk Diuresis
            double bb = parseDoubleOrZero(Sequel.cariIsi(
                "select bb from observasi_icu_nutrisi_cairan where no_rawat=? and bb<>'' "
                + "order by tgl_nutrisi desc, jam_nutrisi desc limit 1", TNoRw.getText()));

            Object[] rIn = barisRingkasan("TOTAL INTAKE (ml)");
            Object[] rOut = barisRingkasan("TOTAL OUTPUT (ml)");
            Object[] rBal = barisRingkasan("BALANCE (ml, kumulatif)");
            Object[] rDiu = barisRingkasan("DIURESIS (ml/kgBB/jam)");
            double balanceKum = 0;
            for (int j = 0; j < n; j++) {
                rIn[6 + j]  = fmtAngka(totIn[j]);
                rOut[6 + j] = fmtAngka(totOut[j]);
                balanceKum += totIn[j] - totOut[j];
                // Balance kumulatif hanya ditampilkan di jam yang ada aktivitas
                // (ada intake/output jam itu) — tidak terbawa ke jam kosong.
                boolean adaAktivitas = (totIn[j] != 0 || totOut[j] != 0);
                rBal[6 + j] = adaAktivitas
                    ? ((balanceKum == Math.floor(balanceKum)) ? String.valueOf((long) balanceKum) : String.valueOf(balanceKum))
                    : "";
                rDiu[6 + j] = (bb > 0 && urine[j] > 0) ? fmt2(urine[j] / bb) : "";
            }
            tabModeIntakeOutput.addRow(rIn);
            tabModeIntakeOutput.addRow(rOut);
            tabModeIntakeOutput.addRow(rBal);
            tabModeIntakeOutput.addRow(rDiu);
        } catch (Exception e) {
            System.out.println("Notifikasi hitungRingkasanCairan: " + e);
        } finally {
            suppressTableEvent = false;
        }
    }

    /** Baris ringkasan kosong dgn label di kolom Tipe; No.Rawat kosong = penanda read-only. */
    private Object[] barisRingkasan(String label) {
        Object[] row = new Object[32];
        for (int i = 0; i < 32; i++) row[i] = (i == 3) ? label : (i == 5 ? Boolean.FALSE : "");
        return row;
    }

    private String fmtAngka(double d) {
        if (d == 0) return "";
        return (d == Math.floor(d)) ? String.valueOf((long) d) : String.valueOf(d);
    }

    private String fmt2(double d) {
        return new java.math.BigDecimal(d).setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    // ========== GRAFIK HEMODINAMIK ==========

    private widget.Button BtnGrafikHemo;

    private void tampilGrafikHemodinamik() {
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            // Ambil data hemodinamik pasien ini
            java.util.List<String> listJam = new java.util.ArrayList<>();
            java.util.List<Double> listHR = new java.util.ArrayList<>();
            java.util.List<Double> listRR = new java.util.ArrayList<>();
            java.util.List<Double> listTemp = new java.util.ArrayList<>();
            java.util.List<Double> listSpO2 = new java.util.ArrayList<>();

            ps = koneksi.prepareStatement(
                "select jam,hr,rr,suhu,saturasi_o2 from observasi_icu_hemodinamik "
                + "where no_rawat=? order by tgl_observasi, jam");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                listJam.add(rs.getString(1));
                listHR.add(rs.getDouble(2));
                listRR.add(rs.getDouble(3));
                listTemp.add(rs.getDouble(4));
                listSpO2.add(rs.getDouble(5));
            }
            rs.close();
            ps.close();

            if (listJam.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Belum ada data hemodinamik untuk pasien ini");
                this.setCursor(Cursor.getDefaultCursor());
                return;
            }

            // Buat grafik dengan panel custom
            javax.swing.JDialog dlgGrafik = new javax.swing.JDialog((java.awt.Frame) null, "Grafik Hemodinamik - " + TPasien.getText(), true);
            dlgGrafik.setSize(900, 500);
            dlgGrafik.setLocationRelativeTo(internalFrame1);

            // Panel grafik custom
            javax.swing.JPanel panelGrafik = new javax.swing.JPanel() {
                @Override
                protected void paintComponent(java.awt.Graphics g) {
                    super.paintComponent(g);
                    java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
                    g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = getWidth(), h = getHeight();
                    int left = 60, right = 20, top = 40, bottom = 60;
                    int gw = w - left - right, gh = h - top - bottom;
                    int n = listJam.size();

                    // Background
                    g2.setColor(java.awt.Color.WHITE);
                    g2.fillRect(0, 0, w, h);

                    // Title
                    g2.setColor(java.awt.Color.BLACK);
                    g2.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 14));
                    g2.drawString("Grafik Hemodinamik Pasien", w / 2 - 120, 25);

                    // Grid & axes
                    g2.setColor(new java.awt.Color(220, 220, 220));
                    for (int yi = 0; yi <= 10; yi++) {
                        int y = top + gh - (yi * gh / 10);
                        g2.drawLine(left, y, left + gw, y);
                    }
                    g2.setColor(java.awt.Color.DARK_GRAY);
                    g2.drawLine(left, top, left, top + gh);
                    g2.drawLine(left, top + gh, left + gw, top + gh);

                    // Y-axis labels (0-200)
                    g2.setFont(new java.awt.Font("Tahoma", 0, 10));
                    for (int yi = 0; yi <= 10; yi++) {
                        int y = top + gh - (yi * gh / 10);
                        g2.drawString(String.valueOf(yi * 20), 20, y + 4);
                    }

                    // X-axis labels
                    if (n > 0) {
                        int step = Math.max(1, n / 15);
                        for (int xi = 0; xi < n; xi += step) {
                            int x = left + (xi * gw / Math.max(n - 1, 1));
                            g2.setColor(java.awt.Color.DARK_GRAY);
                            g2.drawString(listJam.get(xi), x - 15, top + gh + 20);
                        }
                    }

                    double maxVal = 200.0;

                    // Draw lines: HR (Red), RR (Blue), Temp (Orange), SpO2 (Green)
                    drawLine(g2, listHR, left, top, gw, gh, n, maxVal, java.awt.Color.RED);
                    drawLine(g2, listRR, left, top, gw, gh, n, maxVal, java.awt.Color.BLUE);
                    drawLine(g2, listTemp, left, top, gw, gh, n, maxVal, new java.awt.Color(255, 140, 0));
                    drawLine(g2, listSpO2, left, top, gw, gh, n, maxVal, new java.awt.Color(0, 150, 0));

                    // Legend
                    int lx = left + 10, ly = top + 10;
                    g2.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 11));
                    g2.setColor(java.awt.Color.RED); g2.fillRect(lx, ly, 15, 3); g2.drawString("HR", lx + 20, ly + 5); ly += 18;
                    g2.setColor(java.awt.Color.BLUE); g2.fillRect(lx, ly, 15, 3); g2.drawString("RR", lx + 20, ly + 5); ly += 18;
                    g2.setColor(new java.awt.Color(255, 140, 0)); g2.fillRect(lx, ly, 15, 3); g2.drawString("Temp", lx + 20, ly + 5); ly += 18;
                    g2.setColor(new java.awt.Color(0, 150, 0)); g2.fillRect(lx, ly, 15, 3); g2.drawString("SpO2", lx + 20, ly + 5);
                }

                private void drawLine(java.awt.Graphics2D g2, java.util.List<Double> data,
                        int left, int top, int gw, int gh, int n, double maxVal, java.awt.Color color) {
                    if (n < 2) return;
                    g2.setColor(color);
                    g2.setStroke(new java.awt.BasicStroke(2f));
                    for (int xi = 1; xi < n; xi++) {
                        int x1 = left + ((xi - 1) * gw / (n - 1));
                        int y1 = top + gh - (int) (data.get(xi - 1) / maxVal * gh);
                        int x2 = left + (xi * gw / (n - 1));
                        int y2 = top + gh - (int) (data.get(xi) / maxVal * gh);
                        g2.drawLine(x1, y1, x2, y2);
                        g2.fillOval(x1 - 3, y1 - 3, 6, 6);
                        if (xi == n - 1) g2.fillOval(x2 - 3, y2 - 3, 6, 6);
                    }
                }
            };

            dlgGrafik.setContentPane(panelGrafik);
            dlgGrafik.setVisible(true);

        } catch (Exception ex) {
            System.out.println("Error grafik: " + ex);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    // ========== RESUME SHIFT (Pagi / Siang / Malam) ==========
    //
    // Tab Resume disisipkan di index 5 (setelah Implementasi Keperawatan).
    // Input: combobox shift + textarea resume. Timestamp dari panel atas
    // (Tanggal + Jam/Menit/Detik di header form). PK gabungan dgn jam,
    // jadi bisa multiple entry per shift dlm sehari.

    private DefaultTableModel tabModeResume;
    private widget.Table tbResume;
    private widget.ComboBox cmbShiftResume;
    private javax.swing.JTextArea TResumeShift;

    private void setupTabResume() {
        tabModeResume = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tanggal", "Jam", "Shift", "Resume", "NIP", "Petugas"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbResume = new widget.Table();
        tbResume.setModel(tabModeResume);
        tbResume.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbResume.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { getDataResume(); }
        });
        // Column widths
        int[] resCols = {105, 80, 60, 70, 600, 80, 150};
        for (int i = 0; i < resCols.length; i++) {
            tbResume.getColumnModel().getColumn(i).setPreferredWidth(resCols[i]);
        }

        widget.InternalFrame ifRes = new widget.InternalFrame();
        ifRes.setBorder(null);
        ifRes.setLayout(new java.awt.BorderLayout(1, 1));
        widget.ScrollPane scRes = new widget.ScrollPane();
        scRes.setViewportView(tbResume);
        ifRes.add(scRes, java.awt.BorderLayout.CENTER);

        // Input panel: combobox shift + textarea
        widget.panelisi pnlRes = new widget.panelisi();
        pnlRes.setPreferredSize(new Dimension(44, 160));
        pnlRes.setLayout(null);

        javax.swing.JLabel lblShift = new javax.swing.JLabel("Shift :");
        lblShift.setBounds(0, 10, 50, 23); pnlRes.add(lblShift);
        cmbShiftResume = new widget.ComboBox();
        cmbShiftResume.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Pagi", "Siang", "Malam"}));
        cmbShiftResume.setBounds(55, 10, 120, 23); pnlRes.add(cmbShiftResume);

        javax.swing.JLabel lblResume = new javax.swing.JLabel("Resume :");
        lblResume.setBounds(0, 40, 60, 23); pnlRes.add(lblResume);
        TResumeShift = new javax.swing.JTextArea();
        TResumeShift.setLineWrap(true);
        TResumeShift.setWrapStyleWord(true);
        javax.swing.JScrollPane scrResume = new javax.swing.JScrollPane(TResumeShift);
        scrResume.setBounds(65, 40, 1100, 110); pnlRes.add(scrResume);

        ifRes.add(pnlRes, java.awt.BorderLayout.PAGE_START);

        // Insert di idx 5 (setelah Implementasi Keperawatan)
        TabICU.insertTab("Resume", null, ifRes, null, 5);
    }

    private void simpanResume() {
        if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); return; }
        String tgl = Valid.SetTgl(Tanggal.getSelectedItem() + "");
        String jam = Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem();
        if (Sequel.menyimpantf("observasi_icu_resume", "?,?,?,?,?,?", "Data", 6, new String[]{
            TNoRw.getText(), tgl, jam,
            cmbShiftResume.getSelectedItem().toString(),
            TResumeShift.getText(),
            NIPHeader.getText()
        })) { tampilResume(); emptResume(); }
    }

    private void tampilResume() {
        Valid.tabelKosong(tabModeResume);
        try {
            ps = koneksi.prepareStatement(
                "select r.no_rawat,r.tgl_resume,r.jam_resume,r.shift,r.resume,r.nip,IFNULL(pet.nama,r.nip) "
                + "from observasi_icu_resume r left join petugas pet on r.nip=pet.nip "
                + "where r.no_rawat=? order by r.tgl_resume desc, r.jam_resume desc");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeResume.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)});
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilResume: " + e); }
        LCount.setText("" + tabModeResume.getRowCount());
    }

    private void hapusResume() {
        if (tbResume.getSelectedRow() > -1) {
            int r = tbResume.getSelectedRow();
            if (Sequel.queryu2tf("delete from observasi_icu_resume where no_rawat=? and tgl_resume=? and jam_resume=?", 3, new String[]{
                tabModeResume.getValueAt(r, 0).toString(), tabModeResume.getValueAt(r, 1).toString(), tabModeResume.getValueAt(r, 2).toString()
            })) { tampilResume(); emptResume(); }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan dihapus..!!"); }
    }

    private void editResume() {
        if (tbResume.getSelectedRow() > -1) {
            if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); return; }
            int r = tbResume.getSelectedRow();
            if (Sequel.mengedittf("observasi_icu_resume", "no_rawat=? and tgl_resume=? and jam_resume=?",
                "shift=?,resume=?,nip=?", 6, new String[]{
                    cmbShiftResume.getSelectedItem().toString(), TResumeShift.getText(), NIPHeader.getText(),
                    tabModeResume.getValueAt(r, 0).toString(),
                    tabModeResume.getValueAt(r, 1).toString(),
                    tabModeResume.getValueAt(r, 2).toString()
                })) { tampilResume(); emptResume(); }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan diedit..!!"); }
    }

    private void getDataResume() {
        int r = tbResume.getSelectedRow();
        if (r < 0) return;
        Object shift = tabModeResume.getValueAt(r, 3);
        if (shift != null) cmbShiftResume.setSelectedItem(shift.toString());
        Object res = tabModeResume.getValueAt(r, 4);
        TResumeShift.setText(res == null ? "" : res.toString());
    }

    private void emptResume() {
        if (cmbShiftResume.getItemCount() > 0) cmbShiftResume.setSelectedIndex(0);
        TResumeShift.setText("");
    }

    // ========== MIT (Masalah / Instruksi / Tindakan) ==========
    //
    // Tab MIT disisipkan di index 6 (setelah Resume). 3 textarea bebas:
    // Masalah, Instruksi, Tindakan. Timestamp+petugas dari panel atas.

    private DefaultTableModel tabModeMIT;
    private widget.Table tbMIT;
    private javax.swing.JTextArea TMasalah, TInstruksiMIT, TTindakanMIT;

    private void setupTabMIT() {
        tabModeMIT = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tanggal", "Jam", "Masalah", "Instruksi", "Tindakan", "NIP", "Petugas"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbMIT = new widget.Table();
        tbMIT.setModel(tabModeMIT);
        tbMIT.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbMIT.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { getDataMIT(); }
        });
        int[] mitCols = {105, 80, 60, 350, 350, 350, 80, 150};
        for (int i = 0; i < mitCols.length; i++) {
            tbMIT.getColumnModel().getColumn(i).setPreferredWidth(mitCols[i]);
        }

        widget.InternalFrame ifMIT = new widget.InternalFrame();
        ifMIT.setBorder(null);
        ifMIT.setLayout(new java.awt.BorderLayout(1, 1));
        widget.ScrollPane scMIT = new widget.ScrollPane();
        scMIT.setViewportView(tbMIT);
        ifMIT.add(scMIT, java.awt.BorderLayout.CENTER);

        // Input panel: 3 textarea (Masalah/Instruksi/Tindakan) berdampingan
        widget.panelisi pnlMIT = new widget.panelisi();
        pnlMIT.setPreferredSize(new Dimension(44, 200));
        pnlMIT.setLayout(null);

        int paneW = 380, paneH = 180, gap = 10;
        javax.swing.JLabel l1 = new javax.swing.JLabel("Masalah :");
        l1.setBounds(0, 5, 80, 18); pnlMIT.add(l1);
        TMasalah = new javax.swing.JTextArea();
        TMasalah.setLineWrap(true); TMasalah.setWrapStyleWord(true);
        javax.swing.JScrollPane scM = new javax.swing.JScrollPane(TMasalah);
        scM.setBounds(0, 25, paneW, paneH); pnlMIT.add(scM);

        javax.swing.JLabel l2 = new javax.swing.JLabel("Instruksi :");
        l2.setBounds(paneW + gap, 5, 80, 18); pnlMIT.add(l2);
        TInstruksiMIT = new javax.swing.JTextArea();
        TInstruksiMIT.setLineWrap(true); TInstruksiMIT.setWrapStyleWord(true);
        javax.swing.JScrollPane scI = new javax.swing.JScrollPane(TInstruksiMIT);
        scI.setBounds(paneW + gap, 25, paneW, paneH); pnlMIT.add(scI);

        javax.swing.JLabel l3 = new javax.swing.JLabel("Tindakan :");
        l3.setBounds(2 * (paneW + gap), 5, 80, 18); pnlMIT.add(l3);
        TTindakanMIT = new javax.swing.JTextArea();
        TTindakanMIT.setLineWrap(true); TTindakanMIT.setWrapStyleWord(true);
        javax.swing.JScrollPane scT = new javax.swing.JScrollPane(TTindakanMIT);
        scT.setBounds(2 * (paneW + gap), 25, paneW, paneH); pnlMIT.add(scT);

        ifMIT.add(pnlMIT, java.awt.BorderLayout.PAGE_START);

        // Insert di idx 6 (setelah Resume yg di idx 5)
        TabICU.insertTab("M.I.T.", null, ifMIT, null, 6);
    }

    private void simpanMIT() {
        if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); return; }
        String tgl = Valid.SetTgl(Tanggal.getSelectedItem() + "");
        String jam = Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem();
        if (Sequel.menyimpantf("observasi_icu_mit", "?,?,?,?,?,?,?", "Data", 7, new String[]{
            TNoRw.getText(), tgl, jam,
            TMasalah.getText(), TInstruksiMIT.getText(), TTindakanMIT.getText(),
            NIPHeader.getText()
        })) { tampilMIT(); emptMIT(); }
    }

    private void tampilMIT() {
        Valid.tabelKosong(tabModeMIT);
        try {
            ps = koneksi.prepareStatement(
                "select m.no_rawat,m.tgl_mit,m.jam_mit,m.masalah,m.instruksi,m.tindakan,m.nip,IFNULL(pet.nama,m.nip) "
                + "from observasi_icu_mit m left join petugas pet on m.nip=pet.nip "
                + "where m.no_rawat=? order by m.tgl_mit desc, m.jam_mit desc");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeMIT.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)});
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilMIT: " + e); }
        LCount.setText("" + tabModeMIT.getRowCount());
    }

    private void hapusMIT() {
        if (tbMIT.getSelectedRow() > -1) {
            int r = tbMIT.getSelectedRow();
            if (Sequel.queryu2tf("delete from observasi_icu_mit where no_rawat=? and tgl_mit=? and jam_mit=?", 3, new String[]{
                tabModeMIT.getValueAt(r, 0).toString(), tabModeMIT.getValueAt(r, 1).toString(), tabModeMIT.getValueAt(r, 2).toString()
            })) { tampilMIT(); emptMIT(); }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan dihapus..!!"); }
    }

    private void editMIT() {
        if (tbMIT.getSelectedRow() > -1) {
            if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); return; }
            int r = tbMIT.getSelectedRow();
            if (Sequel.mengedittf("observasi_icu_mit", "no_rawat=? and tgl_mit=? and jam_mit=?",
                "masalah=?,instruksi=?,tindakan=?,nip=?", 7, new String[]{
                    TMasalah.getText(), TInstruksiMIT.getText(), TTindakanMIT.getText(), NIPHeader.getText(),
                    tabModeMIT.getValueAt(r, 0).toString(),
                    tabModeMIT.getValueAt(r, 1).toString(),
                    tabModeMIT.getValueAt(r, 2).toString()
                })) { tampilMIT(); emptMIT(); }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan diedit..!!"); }
    }

    private void getDataMIT() {
        int r = tbMIT.getSelectedRow();
        if (r < 0) return;
        Object m = tabModeMIT.getValueAt(r, 3);
        Object i = tabModeMIT.getValueAt(r, 4);
        Object t = tabModeMIT.getValueAt(r, 5);
        TMasalah.setText(m == null ? "" : m.toString());
        TInstruksiMIT.setText(i == null ? "" : i.toString());
        TTindakanMIT.setText(t == null ? "" : t.toString());
    }

    private void emptMIT() {
        TMasalah.setText("");
        TInstruksiMIT.setText("");
        TTindakanMIT.setText("");
    }

    // ========== FASTHUG (Feeding/Analgesia/Sedation/Thrombo/Head/Ulcer/Glucose) ==========
    //
    // Tab Fasthug disisipkan di index 7 (setelah MIT). 7 combobox Ya/Tidak/N/A
    // sesuai mnemonic FASTHUG. NB: tab "Daily Checklist" yg sudah ada di
    // ujung TabICU juga FASTHUG versi lengkap (+SBT, Line Review, Catatan).

    private DefaultTableModel tabModeFasthug;
    private widget.Table tbFasthug;
    private widget.ComboBox cmbFastFeeding, cmbFastAnalgesia, cmbFastSedation,
            cmbFastThrombo, cmbFastHead, cmbFastUlcer, cmbFastGlucose;

    private void setupTabFasthug() {
        tabModeFasthug = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tanggal", "Jam",
            "Feeding", "Analgesia", "Sedation", "Thrombo Prophy", "Head Elevation",
            "Ulcer Prophy", "Glucose Control", "NIP", "Petugas"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbFasthug = new widget.Table();
        tbFasthug.setModel(tabModeFasthug);
        tbFasthug.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbFasthug.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { getDataFasthug(); }
        });
        int[] fhCols = {105, 80, 60, 80, 80, 80, 110, 110, 110, 110, 80, 150};
        for (int i = 0; i < fhCols.length; i++) {
            tbFasthug.getColumnModel().getColumn(i).setPreferredWidth(fhCols[i]);
        }

        widget.InternalFrame ifFH = new widget.InternalFrame();
        ifFH.setBorder(null);
        ifFH.setLayout(new java.awt.BorderLayout(1, 1));
        widget.ScrollPane scFH = new widget.ScrollPane();
        scFH.setViewportView(tbFasthug);
        ifFH.add(scFH, java.awt.BorderLayout.CENTER);

        // Input panel: 7 label+combobox layout 4 kolom x 2 baris
        widget.panelisi pnlFH = new widget.panelisi();
        pnlFH.setPreferredSize(new Dimension(44, 90));
        pnlFH.setLayout(null);

        String[] labels = {"Feeding", "Analgesia", "Sedation",
            "Thrombo Prophy", "Head of Elevation", "Ulcer Prophy", "Glucose Control"};
        widget.ComboBox[] cmbs = new widget.ComboBox[7];
        for (int i = 0; i < 7; i++) {
            int col = i % 4, row = i / 4;
            javax.swing.JLabel lbl = new javax.swing.JLabel(labels[i] + " :");
            lbl.setBounds(col * 280, 10 + row * 35, 130, 23);
            pnlFH.add(lbl);
            cmbs[i] = new widget.ComboBox();
            cmbs[i].setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak", "N/A"}));
            cmbs[i].setBounds(col * 280 + 135, 10 + row * 35, 80, 23);
            pnlFH.add(cmbs[i]);
        }
        cmbFastFeeding   = cmbs[0]; cmbFastAnalgesia = cmbs[1]; cmbFastSedation = cmbs[2];
        cmbFastThrombo   = cmbs[3]; cmbFastHead      = cmbs[4]; cmbFastUlcer    = cmbs[5];
        cmbFastGlucose   = cmbs[6];

        ifFH.add(pnlFH, java.awt.BorderLayout.PAGE_START);

        // Insert di idx 7 (setelah MIT yg di idx 6)
        TabICU.insertTab("Fasthug", null, ifFH, null, 7);
    }

    private void simpanFasthug() {
        if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); return; }
        String tgl = Valid.SetTgl(Tanggal.getSelectedItem() + "");
        String jam = Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem();
        if (Sequel.menyimpantf("observasi_icu_fasthug", "?,?,?,?,?,?,?,?,?,?,?", "Data", 11, new String[]{
            TNoRw.getText(), tgl, jam,
            cmbFastFeeding.getSelectedItem().toString(),
            cmbFastAnalgesia.getSelectedItem().toString(),
            cmbFastSedation.getSelectedItem().toString(),
            cmbFastThrombo.getSelectedItem().toString(),
            cmbFastHead.getSelectedItem().toString(),
            cmbFastUlcer.getSelectedItem().toString(),
            cmbFastGlucose.getSelectedItem().toString(),
            NIPHeader.getText()
        })) { tampilFasthug(); emptFasthug(); }
    }

    private void tampilFasthug() {
        Valid.tabelKosong(tabModeFasthug);
        try {
            ps = koneksi.prepareStatement(
                "select f.no_rawat,f.tgl_fasthug,f.jam_fasthug,f.feeding,f.analgesia,f.sedation,"
                + "f.thrombo_prophy,f.head_elevation,f.ulcer_prophy,f.glucose_control,f.nip,IFNULL(pet.nama,f.nip) "
                + "from observasi_icu_fasthug f left join petugas pet on f.nip=pet.nip "
                + "where f.no_rawat=? order by f.tgl_fasthug desc, f.jam_fasthug desc");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[12];
                for (int i = 1; i <= 12; i++) row[i-1] = rs.getString(i);
                tabModeFasthug.addRow(row);
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilFasthug: " + e); }
        LCount.setText("" + tabModeFasthug.getRowCount());
    }

    private void hapusFasthug() {
        if (tbFasthug.getSelectedRow() > -1) {
            int r = tbFasthug.getSelectedRow();
            if (Sequel.queryu2tf("delete from observasi_icu_fasthug where no_rawat=? and tgl_fasthug=? and jam_fasthug=?", 3, new String[]{
                tabModeFasthug.getValueAt(r, 0).toString(), tabModeFasthug.getValueAt(r, 1).toString(), tabModeFasthug.getValueAt(r, 2).toString()
            })) { tampilFasthug(); emptFasthug(); }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan dihapus..!!"); }
    }

    private void editFasthug() {
        if (tbFasthug.getSelectedRow() > -1) {
            if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); return; }
            int r = tbFasthug.getSelectedRow();
            if (Sequel.mengedittf("observasi_icu_fasthug", "no_rawat=? and tgl_fasthug=? and jam_fasthug=?",
                "feeding=?,analgesia=?,sedation=?,thrombo_prophy=?,head_elevation=?,ulcer_prophy=?,glucose_control=?,nip=?", 11, new String[]{
                    cmbFastFeeding.getSelectedItem().toString(),
                    cmbFastAnalgesia.getSelectedItem().toString(),
                    cmbFastSedation.getSelectedItem().toString(),
                    cmbFastThrombo.getSelectedItem().toString(),
                    cmbFastHead.getSelectedItem().toString(),
                    cmbFastUlcer.getSelectedItem().toString(),
                    cmbFastGlucose.getSelectedItem().toString(),
                    NIPHeader.getText(),
                    tabModeFasthug.getValueAt(r, 0).toString(),
                    tabModeFasthug.getValueAt(r, 1).toString(),
                    tabModeFasthug.getValueAt(r, 2).toString()
                })) { tampilFasthug(); emptFasthug(); }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan diedit..!!"); }
    }

    private void getDataFasthug() {
        int r = tbFasthug.getSelectedRow();
        if (r < 0) return;
        widget.ComboBox[] cmbs = {cmbFastFeeding, cmbFastAnalgesia, cmbFastSedation,
                                  cmbFastThrombo, cmbFastHead, cmbFastUlcer, cmbFastGlucose};
        for (int i = 0; i < 7; i++) {
            Object v = tabModeFasthug.getValueAt(r, 3 + i);
            if (v != null) cmbs[i].setSelectedItem(v.toString());
        }
    }

    private void emptFasthug() {
        widget.ComboBox[] cmbs = {cmbFastFeeding, cmbFastAnalgesia, cmbFastSedation,
                                  cmbFastThrombo, cmbFastHead, cmbFastUlcer, cmbFastGlucose};
        for (widget.ComboBox c : cmbs) if (c.getItemCount() > 0) c.setSelectedIndex(0);
    }

    // ========== ALAT (7 alat invasif × 3 field: Lokasi / Tgl Pasang / Tgl Cabut) ==========

    private DefaultTableModel tabModeAlat;
    private widget.Table tbAlat;
    // 7 alat × 3 field = 21 textbox. Format tgl 'YYYY-MM-DD' (kosong = belum ada).
    private widget.TextBox[] tAlatLokasi = new widget.TextBox[7];
    private widget.TextBox[] tAlatPasang = new widget.TextBox[7];
    private widget.TextBox[] tAlatCabut  = new widget.TextBox[7];
    private static final String[] ALAT_NAMES = {
        "Perifer I", "Perifer II", "CVC", "Arteri Line", "Kateter Urin", "NGT", "Drain"
    };

    private void setupTabAlat() {
        // Table header: 3 cols PK + (7×3) = 24 cols + nip + petugas = 26
        Object[] alatHeaders = new Object[26];
        alatHeaders[0] = "No.Rawat"; alatHeaders[1] = "Tanggal"; alatHeaders[2] = "Jam";
        for (int i = 0; i < 7; i++) {
            alatHeaders[3 + i*3]     = ALAT_NAMES[i] + " - Lokasi";
            alatHeaders[3 + i*3 + 1] = ALAT_NAMES[i] + " - Pasang";
            alatHeaders[3 + i*3 + 2] = ALAT_NAMES[i] + " - Cabut";
        }
        alatHeaders[24] = "NIP"; alatHeaders[25] = "Petugas";
        tabModeAlat = new DefaultTableModel(null, alatHeaders) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbAlat = new widget.Table();
        tbAlat.setModel(tabModeAlat);
        tbAlat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbAlat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { getDataAlat(); }
        });
        tbAlat.getColumnModel().getColumn(0).setPreferredWidth(105);
        tbAlat.getColumnModel().getColumn(1).setPreferredWidth(80);
        tbAlat.getColumnModel().getColumn(2).setPreferredWidth(60);
        for (int i = 3; i <= 23; i++) tbAlat.getColumnModel().getColumn(i).setPreferredWidth(110);
        tbAlat.getColumnModel().getColumn(24).setPreferredWidth(80);
        tbAlat.getColumnModel().getColumn(25).setPreferredWidth(150);

        widget.InternalFrame ifAL = new widget.InternalFrame();
        ifAL.setBorder(null);
        ifAL.setLayout(new java.awt.BorderLayout(1, 1));
        widget.ScrollPane scAL = new widget.ScrollPane();
        scAL.setViewportView(tbAlat);
        ifAL.add(scAL, java.awt.BorderLayout.CENTER);

        // Input panel: 7 baris, masing-masing [label, lokasi, tgl pasang, tgl cabut]
        widget.panelisi pnlAL = new widget.panelisi();
        pnlAL.setPreferredSize(new Dimension(44, 240));
        pnlAL.setLayout(null);

        // Header row labels
        javax.swing.JLabel hLok = new javax.swing.JLabel("Lokasi");
        hLok.setBounds(135, 5, 200, 18); pnlAL.add(hLok);
        javax.swing.JLabel hPsg = new javax.swing.JLabel("Tgl Pasang (DD-MM-YYYY)");
        hPsg.setBounds(345, 5, 180, 18); pnlAL.add(hPsg);
        javax.swing.JLabel hCab = new javax.swing.JLabel("Tgl Cabut (DD-MM-YYYY)");
        hCab.setBounds(535, 5, 180, 18); pnlAL.add(hCab);

        for (int i = 0; i < 7; i++) {
            int y = 28 + i * 28;
            javax.swing.JLabel l = new javax.swing.JLabel((i + 1) + ". " + ALAT_NAMES[i] + " :");
            l.setBounds(0, y, 130, 23); pnlAL.add(l);
            tAlatLokasi[i] = new widget.TextBox(); tAlatLokasi[i].setBounds(135, y, 200, 23); pnlAL.add(tAlatLokasi[i]);
            tAlatPasang[i] = new widget.TextBox(); tAlatPasang[i].setBounds(345, y, 180, 23); pnlAL.add(tAlatPasang[i]);
            tAlatCabut[i]  = new widget.TextBox(); tAlatCabut[i].setBounds(535, y, 180, 23); pnlAL.add(tAlatCabut[i]);
        }

        ifAL.add(pnlAL, java.awt.BorderLayout.PAGE_START);

        // Insert di idx 8 (setelah Fasthug yg di idx 7)
        TabICU.insertTab("Alat", null, ifAL, null, 8);
    }

    private void simpanAlat() {
        if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); return; }
        String tgl = Valid.SetTgl(Tanggal.getSelectedItem() + "");
        String jam = Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem();
        // 3 (no_rawat,tgl,jam) + 21 (alat) + 1 (nip) = 25 placeholder
        String[] vals = new String[25];
        vals[0] = TNoRw.getText(); vals[1] = tgl; vals[2] = jam;
        for (int i = 0; i < 7; i++) {
            vals[3 + i*3]     = tAlatLokasi[i].getText();
            vals[3 + i*3 + 1] = tAlatPasang[i].getText();
            vals[3 + i*3 + 2] = tAlatCabut[i].getText();
        }
        vals[24] = NIPHeader.getText();
        StringBuilder ph = new StringBuilder("?");
        for (int i = 1; i < 25; i++) ph.append(",?");
        if (Sequel.menyimpantf("observasi_icu_alat", ph.toString(), "Data", 25, vals)) {
            tampilAlat(); emptAlat();
        }
    }

    private void tampilAlat() {
        Valid.tabelKosong(tabModeAlat);
        try {
            ps = koneksi.prepareStatement(
                "select a.no_rawat,a.tgl_alat,a.jam_alat,"
                + "a.perifer1_lokasi,a.perifer1_pasang,a.perifer1_cabut,"
                + "a.perifer2_lokasi,a.perifer2_pasang,a.perifer2_cabut,"
                + "a.cvc_lokasi,a.cvc_pasang,a.cvc_cabut,"
                + "a.arteri_lokasi,a.arteri_pasang,a.arteri_cabut,"
                + "a.kateter_lokasi,a.kateter_pasang,a.kateter_cabut,"
                + "a.ngt_lokasi,a.ngt_pasang,a.ngt_cabut,"
                + "a.drain_lokasi,a.drain_pasang,a.drain_cabut,"
                + "a.nip,IFNULL(pet.nama,a.nip) "
                + "from observasi_icu_alat a left join petugas pet on a.nip=pet.nip "
                + "where a.no_rawat=? order by a.tgl_alat desc, a.jam_alat desc");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[26];
                for (int i = 1; i <= 26; i++) row[i-1] = rs.getString(i);
                tabModeAlat.addRow(row);
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilAlat: " + e); }
        LCount.setText("" + tabModeAlat.getRowCount());
    }

    private void hapusAlat() {
        if (tbAlat.getSelectedRow() > -1) {
            int r = tbAlat.getSelectedRow();
            if (Sequel.queryu2tf("delete from observasi_icu_alat where no_rawat=? and tgl_alat=? and jam_alat=?", 3, new String[]{
                tabModeAlat.getValueAt(r, 0).toString(), tabModeAlat.getValueAt(r, 1).toString(), tabModeAlat.getValueAt(r, 2).toString()
            })) { tampilAlat(); emptAlat(); }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan dihapus..!!"); }
    }

    private void editAlat() {
        if (tbAlat.getSelectedRow() > -1) {
            if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); return; }
            int r = tbAlat.getSelectedRow();
            String setClause = "perifer1_lokasi=?,perifer1_pasang=?,perifer1_cabut=?,"
                + "perifer2_lokasi=?,perifer2_pasang=?,perifer2_cabut=?,"
                + "cvc_lokasi=?,cvc_pasang=?,cvc_cabut=?,"
                + "arteri_lokasi=?,arteri_pasang=?,arteri_cabut=?,"
                + "kateter_lokasi=?,kateter_pasang=?,kateter_cabut=?,"
                + "ngt_lokasi=?,ngt_pasang=?,ngt_cabut=?,"
                + "drain_lokasi=?,drain_pasang=?,drain_cabut=?,nip=?";
            String[] vals = new String[25];
            for (int i = 0; i < 7; i++) {
                vals[i*3]     = tAlatLokasi[i].getText();
                vals[i*3 + 1] = tAlatPasang[i].getText();
                vals[i*3 + 2] = tAlatCabut[i].getText();
            }
            vals[21] = NIPHeader.getText();
            vals[22] = tabModeAlat.getValueAt(r, 0).toString();
            vals[23] = tabModeAlat.getValueAt(r, 1).toString();
            vals[24] = tabModeAlat.getValueAt(r, 2).toString();
            if (Sequel.mengedittf("observasi_icu_alat", "no_rawat=? and tgl_alat=? and jam_alat=?", setClause, 25, vals)) {
                tampilAlat(); emptAlat();
            }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan diedit..!!"); }
    }

    private void getDataAlat() {
        int r = tbAlat.getSelectedRow();
        if (r < 0) return;
        for (int i = 0; i < 7; i++) {
            Object lok = tabModeAlat.getValueAt(r, 3 + i*3);
            Object pas = tabModeAlat.getValueAt(r, 3 + i*3 + 1);
            Object cab = tabModeAlat.getValueAt(r, 3 + i*3 + 2);
            tAlatLokasi[i].setText(lok == null ? "" : lok.toString());
            tAlatPasang[i].setText(pas == null ? "" : pas.toString());
            tAlatCabut[i].setText(cab == null ? "" : cab.toString());
        }
    }

    private void emptAlat() {
        for (int i = 0; i < 7; i++) {
            tAlatLokasi[i].setText("");
            tAlatPasang[i].setText("");
            tAlatCabut[i].setText("");
        }
    }

    // ========== CATATAN KONSULTASI (idx 9) ==========

    private DefaultTableModel tabModeKonsultasi;
    private widget.Table tbKonsultasi;
    private javax.swing.JTextArea TKonsultasi;

    private void setupTabKonsultasi() {
        tabModeKonsultasi = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tanggal", "Jam", "Konsultasi", "NIP", "Petugas"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbKonsultasi = new widget.Table();
        tbKonsultasi.setModel(tabModeKonsultasi);
        tbKonsultasi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbKonsultasi.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { getDataKonsultasi(); }
        });
        int[] kCols = {105, 80, 60, 700, 80, 150};
        for (int i = 0; i < kCols.length; i++) tbKonsultasi.getColumnModel().getColumn(i).setPreferredWidth(kCols[i]);

        widget.InternalFrame ifKO = new widget.InternalFrame();
        ifKO.setBorder(null);
        ifKO.setLayout(new java.awt.BorderLayout(1, 1));
        widget.ScrollPane scKO = new widget.ScrollPane();
        scKO.setViewportView(tbKonsultasi);
        ifKO.add(scKO, java.awt.BorderLayout.CENTER);

        widget.panelisi pnlKO = new widget.panelisi();
        pnlKO.setPreferredSize(new Dimension(44, 150));
        pnlKO.setLayout(null);

        javax.swing.JLabel l = new javax.swing.JLabel("Konsultasi :");
        l.setBounds(0, 10, 80, 23); pnlKO.add(l);
        TKonsultasi = new javax.swing.JTextArea();
        TKonsultasi.setLineWrap(true); TKonsultasi.setWrapStyleWord(true);
        javax.swing.JScrollPane scrKO = new javax.swing.JScrollPane(TKonsultasi);
        scrKO.setBounds(85, 10, 1080, 130); pnlKO.add(scrKO);

        ifKO.add(pnlKO, java.awt.BorderLayout.PAGE_START);
        TabICU.insertTab("Catatan Konsultasi", null, ifKO, null, 9);
    }

    private void simpanKonsultasi() {
        if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); return; }
        String tgl = Valid.SetTgl(Tanggal.getSelectedItem() + "");
        String jam = Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem();
        if (Sequel.menyimpantf("observasi_icu_konsultasi", "?,?,?,?,?", "Data", 5, new String[]{
            TNoRw.getText(), tgl, jam, TKonsultasi.getText(), NIPHeader.getText()
        })) { tampilKonsultasi(); emptKonsultasi(); }
    }

    private void tampilKonsultasi() {
        Valid.tabelKosong(tabModeKonsultasi);
        try {
            ps = koneksi.prepareStatement(
                "select k.no_rawat,k.tgl_konsultasi,k.jam_konsultasi,k.konsultasi,k.nip,IFNULL(pet.nama,k.nip) "
                + "from observasi_icu_konsultasi k left join petugas pet on k.nip=pet.nip "
                + "where k.no_rawat=? order by k.tgl_konsultasi desc, k.jam_konsultasi desc");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeKonsultasi.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6)});
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilKonsultasi: " + e); }
        LCount.setText("" + tabModeKonsultasi.getRowCount());
    }

    private void hapusKonsultasi() {
        if (tbKonsultasi.getSelectedRow() > -1) {
            int r = tbKonsultasi.getSelectedRow();
            if (Sequel.queryu2tf("delete from observasi_icu_konsultasi where no_rawat=? and tgl_konsultasi=? and jam_konsultasi=?", 3, new String[]{
                tabModeKonsultasi.getValueAt(r, 0).toString(), tabModeKonsultasi.getValueAt(r, 1).toString(), tabModeKonsultasi.getValueAt(r, 2).toString()
            })) { tampilKonsultasi(); emptKonsultasi(); }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan dihapus..!!"); }
    }

    private void editKonsultasi() {
        if (tbKonsultasi.getSelectedRow() > -1) {
            if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); return; }
            int r = tbKonsultasi.getSelectedRow();
            if (Sequel.mengedittf("observasi_icu_konsultasi", "no_rawat=? and tgl_konsultasi=? and jam_konsultasi=?",
                "konsultasi=?,nip=?", 5, new String[]{
                    TKonsultasi.getText(), NIPHeader.getText(),
                    tabModeKonsultasi.getValueAt(r, 0).toString(),
                    tabModeKonsultasi.getValueAt(r, 1).toString(),
                    tabModeKonsultasi.getValueAt(r, 2).toString()
                })) { tampilKonsultasi(); emptKonsultasi(); }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan diedit..!!"); }
    }

    private void getDataKonsultasi() {
        int r = tbKonsultasi.getSelectedRow();
        if (r < 0) return;
        Object v = tabModeKonsultasi.getValueAt(r, 3);
        TKonsultasi.setText(v == null ? "" : v.toString());
    }

    private void emptKonsultasi() { TKonsultasi.setText(""); }

    // ========== RENCANA PEMERIKSAAN (idx 10) ==========

    private DefaultTableModel tabModeRencana;
    private widget.Table tbRencana;
    private javax.swing.JTextArea TRencanaPeriksa;

    private void setupTabRencana() {
        tabModeRencana = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tanggal", "Jam", "Rencana Pemeriksaan", "NIP", "Petugas"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbRencana = new widget.Table();
        tbRencana.setModel(tabModeRencana);
        tbRencana.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbRencana.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { getDataRencana(); }
        });
        int[] rCols = {105, 80, 60, 700, 80, 150};
        for (int i = 0; i < rCols.length; i++) tbRencana.getColumnModel().getColumn(i).setPreferredWidth(rCols[i]);

        widget.InternalFrame ifRP = new widget.InternalFrame();
        ifRP.setBorder(null);
        ifRP.setLayout(new java.awt.BorderLayout(1, 1));
        widget.ScrollPane scRP = new widget.ScrollPane();
        scRP.setViewportView(tbRencana);
        ifRP.add(scRP, java.awt.BorderLayout.CENTER);

        widget.panelisi pnlRP = new widget.panelisi();
        pnlRP.setPreferredSize(new Dimension(44, 150));
        pnlRP.setLayout(null);

        javax.swing.JLabel l = new javax.swing.JLabel("Rencana :");
        l.setBounds(0, 10, 80, 23); pnlRP.add(l);
        TRencanaPeriksa = new javax.swing.JTextArea();
        TRencanaPeriksa.setLineWrap(true); TRencanaPeriksa.setWrapStyleWord(true);
        javax.swing.JScrollPane scrRP = new javax.swing.JScrollPane(TRencanaPeriksa);
        scrRP.setBounds(85, 10, 1080, 130); pnlRP.add(scrRP);

        ifRP.add(pnlRP, java.awt.BorderLayout.PAGE_START);
        TabICU.insertTab("Rencana Pemeriksaan", null, ifRP, null, 10);
    }

    private void simpanRencana() {
        if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); return; }
        String tgl = Valid.SetTgl(Tanggal.getSelectedItem() + "");
        String jam = Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem();
        if (Sequel.menyimpantf("observasi_icu_rencana_periksa", "?,?,?,?,?", "Data", 5, new String[]{
            TNoRw.getText(), tgl, jam, TRencanaPeriksa.getText(), NIPHeader.getText()
        })) { tampilRencana(); emptRencana(); }
    }

    private void tampilRencana() {
        Valid.tabelKosong(tabModeRencana);
        try {
            ps = koneksi.prepareStatement(
                "select r.no_rawat,r.tgl_rencana,r.jam_rencana,r.rencana_periksa,r.nip,IFNULL(pet.nama,r.nip) "
                + "from observasi_icu_rencana_periksa r left join petugas pet on r.nip=pet.nip "
                + "where r.no_rawat=? order by r.tgl_rencana desc, r.jam_rencana desc");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeRencana.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6)});
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilRencana: " + e); }
        LCount.setText("" + tabModeRencana.getRowCount());
    }

    private void hapusRencana() {
        if (tbRencana.getSelectedRow() > -1) {
            int r = tbRencana.getSelectedRow();
            if (Sequel.queryu2tf("delete from observasi_icu_rencana_periksa where no_rawat=? and tgl_rencana=? and jam_rencana=?", 3, new String[]{
                tabModeRencana.getValueAt(r, 0).toString(), tabModeRencana.getValueAt(r, 1).toString(), tabModeRencana.getValueAt(r, 2).toString()
            })) { tampilRencana(); emptRencana(); }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan dihapus..!!"); }
    }

    private void editRencana() {
        if (tbRencana.getSelectedRow() > -1) {
            if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); return; }
            int r = tbRencana.getSelectedRow();
            if (Sequel.mengedittf("observasi_icu_rencana_periksa", "no_rawat=? and tgl_rencana=? and jam_rencana=?",
                "rencana_periksa=?,nip=?", 5, new String[]{
                    TRencanaPeriksa.getText(), NIPHeader.getText(),
                    tabModeRencana.getValueAt(r, 0).toString(),
                    tabModeRencana.getValueAt(r, 1).toString(),
                    tabModeRencana.getValueAt(r, 2).toString()
                })) { tampilRencana(); emptRencana(); }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan diedit..!!"); }
    }

    private void getDataRencana() {
        int r = tbRencana.getSelectedRow();
        if (r < 0) return;
        Object v = tabModeRencana.getValueAt(r, 3);
        TRencanaPeriksa.setText(v == null ? "" : v.toString());
    }

    private void emptRencana() { TRencanaPeriksa.setText(""); }

    // ========== CATATAN KHUSUS (idx 11) ==========
    //
    // Field: BB, TB, Riwayat Alergi, Golongan Darah, Lainnya, PPJP (kd & nm dokter).
    // Timestamp + nip dari panel atas.

    private DefaultTableModel tabModeCatKhusus;
    private widget.Table tbCatKhusus;
    private widget.TextBox TKhBB, TKhTB, TKhGolDarah, TKhKdDokterPPJP, TKhNmDokterPPJP;
    private javax.swing.JTextArea TKhRiwayatAlergi, TKhLainnya;

    private void setupTabCatatanKhusus() {
        tabModeCatKhusus = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tanggal", "Jam", "BB", "TB", "Gol Darah",
            "Riwayat Alergi", "Lainnya", "Kd PPJP", "Nama PPJP", "NIP", "Petugas"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbCatKhusus = new widget.Table();
        tbCatKhusus.setModel(tabModeCatKhusus);
        tbCatKhusus.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbCatKhusus.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { getDataCatKhusus(); }
        });
        int[] ckhCols = {105, 80, 60, 60, 60, 70, 250, 250, 80, 150, 80, 150};
        for (int i = 0; i < ckhCols.length; i++) tbCatKhusus.getColumnModel().getColumn(i).setPreferredWidth(ckhCols[i]);

        widget.InternalFrame ifCK = new widget.InternalFrame();
        ifCK.setBorder(null);
        ifCK.setLayout(new java.awt.BorderLayout(1, 1));
        widget.ScrollPane scCK = new widget.ScrollPane();
        scCK.setViewportView(tbCatKhusus);
        ifCK.add(scCK, java.awt.BorderLayout.CENTER);

        widget.panelisi pnlCK = new widget.panelisi();
        pnlCK.setPreferredSize(new Dimension(44, 220));
        pnlCK.setLayout(null);

        // Row 1: BB, TB, Gol Darah (kecil-kecil sejajar)
        javax.swing.JLabel lBB = new javax.swing.JLabel("Berat Badan (kg) :");
        lBB.setBounds(0, 10, 110, 23); pnlCK.add(lBB);
        TKhBB = new widget.TextBox(); TKhBB.setBounds(115, 10, 80, 23); pnlCK.add(TKhBB);

        javax.swing.JLabel lTB = new javax.swing.JLabel("Tinggi Badan (cm) :");
        lTB.setBounds(210, 10, 120, 23); pnlCK.add(lTB);
        TKhTB = new widget.TextBox(); TKhTB.setBounds(335, 10, 80, 23); pnlCK.add(TKhTB);

        javax.swing.JLabel lGol = new javax.swing.JLabel("Golongan Darah :");
        lGol.setBounds(430, 10, 110, 23); pnlCK.add(lGol);
        TKhGolDarah = new widget.TextBox(); TKhGolDarah.setBounds(545, 10, 80, 23); pnlCK.add(TKhGolDarah);

        // Row 2: Riwayat Alergi (textarea)
        javax.swing.JLabel lAle = new javax.swing.JLabel("Riwayat Alergi :");
        lAle.setBounds(0, 40, 110, 23); pnlCK.add(lAle);
        TKhRiwayatAlergi = new javax.swing.JTextArea();
        TKhRiwayatAlergi.setLineWrap(true); TKhRiwayatAlergi.setWrapStyleWord(true);
        javax.swing.JScrollPane scAle = new javax.swing.JScrollPane(TKhRiwayatAlergi);
        scAle.setBounds(115, 40, 1050, 50); pnlCK.add(scAle);

        // Row 3: Lainnya (textarea)
        javax.swing.JLabel lLn = new javax.swing.JLabel("Lainnya :");
        lLn.setBounds(0, 95, 110, 23); pnlCK.add(lLn);
        TKhLainnya = new javax.swing.JTextArea();
        TKhLainnya.setLineWrap(true); TKhLainnya.setWrapStyleWord(true);
        javax.swing.JScrollPane scLn = new javax.swing.JScrollPane(TKhLainnya);
        scLn.setBounds(115, 95, 1050, 60); pnlCK.add(scLn);

        // Row 4: PPJP — kode dokter + tombol klip cari dokter + nama dokter
        javax.swing.JLabel lPPJP = new javax.swing.JLabel("PPJP ICU - Kode :");
        lPPJP.setBounds(0, 165, 110, 23); pnlCK.add(lPPJP);
        TKhKdDokterPPJP = new widget.TextBox(); TKhKdDokterPPJP.setBounds(115, 165, 100, 23); pnlCK.add(TKhKdDokterPPJP);

        widget.Button btnCariDokterPPJP = new widget.Button();
        btnCariDokterPPJP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnCariDokterPPJP.setToolTipText("Cari Dokter PPJP");
        btnCariDokterPPJP.addActionListener(e -> {
            kepegawaian.DlgCariDokter dlg = new kepegawaian.DlgCariDokter(null, true);
            dlg.emptTeks();
            dlg.setSize(LembarObservasiIcuIccuHcu.this.getWidth() - 80, LembarObservasiIcuIccuHcu.this.getHeight() - 80);
            dlg.setLocationRelativeTo(LembarObservasiIcuIccuHcu.this);
            dlg.setVisible(true);
            if (dlg.getTable().getSelectedRow() != -1) {
                TKhKdDokterPPJP.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 0).toString());
                TKhNmDokterPPJP.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 1).toString());
            }
        });
        pnlCK.add(btnCariDokterPPJP);
        btnCariDokterPPJP.setBounds(218, 165, 28, 23);

        javax.swing.JLabel lPPJPNm = new javax.swing.JLabel("Nama Dokter :");
        lPPJPNm.setBounds(252, 165, 100, 23); pnlCK.add(lPPJPNm);
        TKhNmDokterPPJP = new widget.TextBox(); TKhNmDokterPPJP.setBounds(355, 165, 400, 23); pnlCK.add(TKhNmDokterPPJP);

        ifCK.add(pnlCK, java.awt.BorderLayout.PAGE_START);
        TabICU.insertTab("Catatan Khusus", null, ifCK, null, 11);
    }

    private void simpanCatKhusus() {
        if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); return; }
        String tgl = Valid.SetTgl(Tanggal.getSelectedItem() + "");
        String jam = Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem();
        if (Sequel.menyimpantf("observasi_icu_catatan_khusus", "?,?,?,?,?,?,?,?,?,?,?", "Data", 11, new String[]{
            TNoRw.getText(), tgl, jam,
            TKhBB.getText(), TKhTB.getText(), TKhRiwayatAlergi.getText(), TKhGolDarah.getText(),
            TKhLainnya.getText(), TKhKdDokterPPJP.getText(), TKhNmDokterPPJP.getText(),
            NIPHeader.getText()
        })) { tampilCatKhusus(); emptCatKhusus(); }
    }

    private void tampilCatKhusus() {
        Valid.tabelKosong(tabModeCatKhusus);
        try {
            ps = koneksi.prepareStatement(
                "select c.no_rawat,c.tgl_catat,c.jam_catat,c.berat_badan,c.tinggi_badan,c.golongan_darah,"
                + "c.riwayat_alergi,c.lainnya,c.kd_dokter_ppjp,c.nama_dokter_ppjp,c.nip,IFNULL(pet.nama,c.nip) "
                + "from observasi_icu_catatan_khusus c left join petugas pet on c.nip=pet.nip "
                + "where c.no_rawat=? order by c.tgl_catat desc, c.jam_catat desc");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[12];
                for (int i = 1; i <= 12; i++) row[i-1] = rs.getString(i);
                tabModeCatKhusus.addRow(row);
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilCatKhusus: " + e); }
        LCount.setText("" + tabModeCatKhusus.getRowCount());
    }

    private void hapusCatKhusus() {
        if (tbCatKhusus.getSelectedRow() > -1) {
            int r = tbCatKhusus.getSelectedRow();
            if (Sequel.queryu2tf("delete from observasi_icu_catatan_khusus where no_rawat=? and tgl_catat=? and jam_catat=?", 3, new String[]{
                tabModeCatKhusus.getValueAt(r, 0).toString(), tabModeCatKhusus.getValueAt(r, 1).toString(), tabModeCatKhusus.getValueAt(r, 2).toString()
            })) { tampilCatKhusus(); emptCatKhusus(); }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan dihapus..!!"); }
    }

    private void editCatKhusus() {
        if (tbCatKhusus.getSelectedRow() > -1) {
            if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); return; }
            int r = tbCatKhusus.getSelectedRow();
            if (Sequel.mengedittf("observasi_icu_catatan_khusus", "no_rawat=? and tgl_catat=? and jam_catat=?",
                "berat_badan=?,tinggi_badan=?,riwayat_alergi=?,golongan_darah=?,lainnya=?,kd_dokter_ppjp=?,nama_dokter_ppjp=?,nip=?", 11, new String[]{
                    TKhBB.getText(), TKhTB.getText(), TKhRiwayatAlergi.getText(), TKhGolDarah.getText(),
                    TKhLainnya.getText(), TKhKdDokterPPJP.getText(), TKhNmDokterPPJP.getText(), NIPHeader.getText(),
                    tabModeCatKhusus.getValueAt(r, 0).toString(),
                    tabModeCatKhusus.getValueAt(r, 1).toString(),
                    tabModeCatKhusus.getValueAt(r, 2).toString()
                })) { tampilCatKhusus(); emptCatKhusus(); }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan diedit..!!"); }
    }

    private void getDataCatKhusus() {
        int r = tbCatKhusus.getSelectedRow();
        if (r < 0) return;
        Object[] vals = new Object[7];
        for (int i = 0; i < 7; i++) vals[i] = tabModeCatKhusus.getValueAt(r, 3 + i);
        TKhBB.setText(vals[0] == null ? "" : vals[0].toString());
        TKhTB.setText(vals[1] == null ? "" : vals[1].toString());
        TKhGolDarah.setText(vals[2] == null ? "" : vals[2].toString());
        TKhRiwayatAlergi.setText(vals[3] == null ? "" : vals[3].toString());
        TKhLainnya.setText(vals[4] == null ? "" : vals[4].toString());
        TKhKdDokterPPJP.setText(vals[5] == null ? "" : vals[5].toString());
        TKhNmDokterPPJP.setText(vals[6] == null ? "" : vals[6].toString());
    }

    private void emptCatKhusus() {
        TKhBB.setText(""); TKhTB.setText(""); TKhGolDarah.setText("");
        TKhRiwayatAlergi.setText(""); TKhLainnya.setText("");
        TKhKdDokterPPJP.setText(""); TKhNmDokterPPJP.setText("");
    }

    // ========== HASIL PEMERIKSAAN LAB (idx 12, viewer w/ date filter) ==========
    //
    // View-only tab. Query gabung periksa_lab + detail_periksa_lab +
    // template_laboratorium + jns_perawatan_lab. Filter: no_rawat (otomatis)
    // + range tgl_periksa (dipilih user di panel filter).
    // BtnSimpan/Hapus/Edit: NO-OP utk tab ini (cuma BtnCari yg refresh).

    private DefaultTableModel tabModeHasilLab;
    private widget.Table tbHasilLab;
    private widget.Tanggal DTPHasilLabDari, DTPHasilLabSampai;

    private void setupTabHasilLab() {
        tabModeHasilLab = new DefaultTableModel(null, new Object[]{
            "Tgl Periksa", "Jenis Pemeriksaan", "Item Pemeriksaan",
            "Nilai", "Satuan", "Nilai Rujukan", "Keterangan"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbHasilLab = new widget.Table();
        tbHasilLab.setModel(tabModeHasilLab);
        tbHasilLab.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] lCols = {90, 200, 250, 100, 70, 130, 250};
        for (int i = 0; i < lCols.length; i++) tbHasilLab.getColumnModel().getColumn(i).setPreferredWidth(lCols[i]);

        widget.InternalFrame ifHL = new widget.InternalFrame();
        ifHL.setBorder(null);
        ifHL.setLayout(new java.awt.BorderLayout(1, 1));
        widget.ScrollPane scHL = new widget.ScrollPane();
        scHL.setViewportView(tbHasilLab);
        ifHL.add(scHL, java.awt.BorderLayout.CENTER);

        // Filter panel: Dari Tanggal — Sampai Tanggal — Tombol Tampilkan
        widget.panelisi pnlHL = new widget.panelisi();
        pnlHL.setPreferredSize(new Dimension(44, 45));
        pnlHL.setLayout(null);

        javax.swing.JLabel lDari = new javax.swing.JLabel("Dari Tanggal :");
        lDari.setBounds(5, 10, 90, 23); pnlHL.add(lDari);
        DTPHasilLabDari = new widget.Tanggal();
        DTPHasilLabDari.setBounds(100, 10, 130, 23); pnlHL.add(DTPHasilLabDari);

        javax.swing.JLabel lSmp = new javax.swing.JLabel("s.d :");
        lSmp.setBounds(240, 10, 30, 23); pnlHL.add(lSmp);
        DTPHasilLabSampai = new widget.Tanggal();
        DTPHasilLabSampai.setBounds(275, 10, 130, 23); pnlHL.add(DTPHasilLabSampai);

        widget.Button btnTampilHL = new widget.Button();
        btnTampilHL.setText("Tampilkan");
        btnTampilHL.setMnemonic('T');
        btnTampilHL.setToolTipText("Filter hasil lab berdasarkan range tanggal");
        btnTampilHL.addActionListener(e -> tampilHasilLab());
        btnTampilHL.setBounds(415, 10, 110, 23); pnlHL.add(btnTampilHL);

        ifHL.add(pnlHL, java.awt.BorderLayout.PAGE_START);
        TabICU.insertTab("Hasil Pemeriksaan Lab", null, ifHL, null, 12);
    }

    private void tampilHasilLab() {
        Valid.tabelKosong(tabModeHasilLab);
        if (TNoRw.getText().trim().isEmpty()) return;
        try {
            ps = koneksi.prepareStatement(
                "SELECT DATE_FORMAT(pl.tgl_periksa,'%d-%m-%Y') AS tgl, "
                + "jpl.nm_perawatan, tl.Pemeriksaan, "
                + "dpl.nilai, tl.satuan, dpl.nilai_rujukan, dpl.keterangan "
                + "FROM periksa_lab pl "
                + "INNER JOIN detail_periksa_lab dpl ON pl.no_rawat=dpl.no_rawat "
                + "  AND pl.kd_jenis_prw=dpl.kd_jenis_prw "
                + "  AND pl.tgl_periksa=dpl.tgl_periksa AND pl.jam=dpl.jam "
                + "INNER JOIN jns_perawatan_lab jpl ON pl.kd_jenis_prw=jpl.kd_jenis_prw "
                + "INNER JOIN template_laboratorium tl ON dpl.id_template=tl.id_template "
                + "WHERE pl.no_rawat=? AND pl.tgl_periksa BETWEEN ? AND ? "
                + "ORDER BY pl.tgl_periksa DESC, pl.jam DESC, tl.urut, tl.Pemeriksaan");
            ps.setString(1, TNoRw.getText());
            ps.setString(2, Valid.SetTgl(DTPHasilLabDari.getSelectedItem() + ""));
            ps.setString(3, Valid.SetTgl(DTPHasilLabSampai.getSelectedItem() + ""));
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeHasilLab.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)
                });
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilHasilLab: " + e); }
        LCount.setText("" + tabModeHasilLab.getRowCount());
    }

    // ========== HASIL PEMERIKSAAN RADIOLOGI (idx 13, viewer + expertise + gambar) ==========
    //
    // Tables: periksa_radiologi (header) + hasil_radiologi (expertise text) +
    // gambar_radiologi (lokasi_gambar file path) + jns_perawatan_radiologi + dokter.
    // Click row → load expertise & gambar pertama ke panel bawah.
    // View-only — BtnSimpan/Hapus/Edit NO-OP.

    private DefaultTableModel tabModeHasilRad;
    private widget.Table tbHasilRad;
    private widget.Tanggal DTPHasilRadDari, DTPHasilRadSampai;
    private javax.swing.JTextArea TExpertiseRad;
    private javax.swing.JLabel lblGambarRad;

    private void setupTabHasilRadiologi() {
        // Catatan: "Jam" & "No.Rawat" disimpan sebagai kolom hidden (width 0)
        // — dipakai loadHasilRadDetail() utk lookup hasil_radiologi & gambar_radiologi.
        tabModeHasilRad = new DefaultTableModel(null, new Object[]{
            "Tgl Periksa", "Jenis Pemeriksaan", "Proyeksi", "Dokter Radiolog",
            "_Jam", "_NoRawat"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbHasilRad = new widget.Table();
        tbHasilRad.setModel(tabModeHasilRad);
        tbHasilRad.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] rCols = {90, 280, 110, 250, 0, 0};
        for (int i = 0; i < rCols.length; i++) {
            javax.swing.table.TableColumn col = tbHasilRad.getColumnModel().getColumn(i);
            col.setPreferredWidth(rCols[i]);
            if (rCols[i] == 0) { col.setMinWidth(0); col.setMaxWidth(0); }
        }
        tbHasilRad.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { loadHasilRadDetail(); }
        });

        widget.InternalFrame ifRD = new widget.InternalFrame();
        ifRD.setBorder(null);
        ifRD.setLayout(new java.awt.BorderLayout(1, 1));

        // Filter panel (atas)
        widget.panelisi pnlFilter = new widget.panelisi();
        pnlFilter.setPreferredSize(new Dimension(44, 40));
        pnlFilter.setLayout(null);

        javax.swing.JLabel lDari = new javax.swing.JLabel("Dari Tanggal :");
        lDari.setBounds(5, 10, 90, 23); pnlFilter.add(lDari);
        DTPHasilRadDari = new widget.Tanggal();
        DTPHasilRadDari.setBounds(100, 10, 130, 23); pnlFilter.add(DTPHasilRadDari);

        javax.swing.JLabel lSmp = new javax.swing.JLabel("s.d :");
        lSmp.setBounds(240, 10, 30, 23); pnlFilter.add(lSmp);
        DTPHasilRadSampai = new widget.Tanggal();
        DTPHasilRadSampai.setBounds(275, 10, 130, 23); pnlFilter.add(DTPHasilRadSampai);

        widget.Button btnTampilRD = new widget.Button();
        btnTampilRD.setText("Tampilkan");
        btnTampilRD.setMnemonic('T');
        btnTampilRD.setToolTipText("Filter hasil radiologi berdasarkan range tanggal");
        btnTampilRD.addActionListener(e -> tampilHasilRadiologi());
        btnTampilRD.setBounds(415, 10, 110, 23); pnlFilter.add(btnTampilRD);

        ifRD.add(pnlFilter, java.awt.BorderLayout.PAGE_START);

        // Split: table di atas, expertise+gambar di bawah
        widget.ScrollPane scTbl = new widget.ScrollPane();
        scTbl.setViewportView(tbHasilRad);

        // Bottom detail panel: Expertise (kiri 50%) + Gambar (kanan 50%)
        javax.swing.JPanel pnlDetail = new javax.swing.JPanel();
        pnlDetail.setLayout(new java.awt.GridLayout(1, 2, 5, 5));
        pnlDetail.setPreferredSize(new Dimension(44, 280));

        // Expertise
        javax.swing.JPanel pnlExp = new javax.swing.JPanel(new java.awt.BorderLayout());
        pnlExp.add(new javax.swing.JLabel("  Expertise / Hasil :"), java.awt.BorderLayout.NORTH);
        TExpertiseRad = new javax.swing.JTextArea();
        TExpertiseRad.setLineWrap(true); TExpertiseRad.setWrapStyleWord(true);
        TExpertiseRad.setEditable(false);
        pnlExp.add(new javax.swing.JScrollPane(TExpertiseRad), java.awt.BorderLayout.CENTER);
        pnlDetail.add(pnlExp);

        // Gambar
        javax.swing.JPanel pnlGmb = new javax.swing.JPanel(new java.awt.BorderLayout());
        pnlGmb.add(new javax.swing.JLabel("  Gambar :"), java.awt.BorderLayout.NORTH);
        lblGambarRad = new javax.swing.JLabel("(Belum ada gambar)", javax.swing.SwingConstants.CENTER);
        pnlGmb.add(new javax.swing.JScrollPane(lblGambarRad), java.awt.BorderLayout.CENTER);
        pnlDetail.add(pnlGmb);

        javax.swing.JSplitPane split = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT, scTbl, pnlDetail);
        split.setResizeWeight(0.5);
        split.setDividerLocation(220);
        ifRD.add(split, java.awt.BorderLayout.CENTER);

        TabICU.insertTab("Hasil Pemeriksaan Radiologi", null, ifRD, null, 13);
    }

    private void tampilHasilRadiologi() {
        Valid.tabelKosong(tabModeHasilRad);
        TExpertiseRad.setText("");
        lblGambarRad.setIcon(null);
        lblGambarRad.setText("(Belum ada gambar)");
        if (TNoRw.getText().trim().isEmpty()) return;
        try {
            ps = koneksi.prepareStatement(
                "SELECT DATE_FORMAT(pr.tgl_periksa,'%d-%m-%Y') AS tgl, "
                + "jpr.nm_perawatan, pr.proyeksi, IFNULL(dk.nm_dokter,pr.kd_dokter) AS dokter, "
                + "pr.jam, pr.no_rawat "
                + "FROM periksa_radiologi pr "
                + "INNER JOIN jns_perawatan_radiologi jpr ON pr.kd_jenis_prw=jpr.kd_jenis_prw "
                + "LEFT JOIN dokter dk ON pr.kd_dokter=dk.kd_dokter "
                + "WHERE pr.no_rawat=? AND pr.tgl_periksa BETWEEN ? AND ? "
                + "ORDER BY pr.tgl_periksa DESC, pr.jam DESC");
            ps.setString(1, TNoRw.getText());
            ps.setString(2, Valid.SetTgl(DTPHasilRadDari.getSelectedItem() + ""));
            ps.setString(3, Valid.SetTgl(DTPHasilRadSampai.getSelectedItem() + ""));
            rs = ps.executeQuery();
            while (rs.next()) {
                // Kolom 4 (_Jam) & 5 (_NoRawat) hidden — dipakai loadHasilRadDetail()
                tabModeHasilRad.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6)
                });
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilHasilRadiologi: " + e); }
        LCount.setText("" + tabModeHasilRad.getRowCount());
    }

    private void loadHasilRadDetail() {
        int r = tbHasilRad.getSelectedRow();
        if (r < 0) return;
        TExpertiseRad.setText("");
        lblGambarRad.setIcon(null);
        lblGambarRad.setText("(Loading...)");
        // Parse tgl di col 0 (format dd-MM-yyyy) jadi yyyy-MM-dd
        // jam (col 4) & no_rawat (col 5) = kolom hidden (lihat setupTabHasilRadiologi)
        String tglDisplay = tabModeHasilRad.getValueAt(r, 0).toString(); // dd-MM-yyyy
        String[] p = tglDisplay.split("-");
        String tglSql = p.length == 3 ? (p[2] + "-" + p[1] + "-" + p[0]) : tglDisplay;
        String jam = tabModeHasilRad.getValueAt(r, 4).toString();
        String noRawat = tabModeHasilRad.getValueAt(r, 5).toString();
        try {
            // Expertise dari hasil_radiologi
            ps = koneksi.prepareStatement(
                "SELECT hasil FROM hasil_radiologi WHERE no_rawat=? AND tgl_periksa=? AND jam=? LIMIT 1");
            ps.setString(1, noRawat); ps.setString(2, tglSql); ps.setString(3, jam);
            rs = ps.executeQuery();
            if (rs.next()) TExpertiseRad.setText(rs.getString(1) == null ? "" : rs.getString(1));
            else TExpertiseRad.setText("(Belum ada expertise)");
            rs.close(); ps.close();

            // Gambar pertama dari gambar_radiologi
            ps = koneksi.prepareStatement(
                "SELECT lokasi_gambar FROM gambar_radiologi WHERE no_rawat=? AND tgl_periksa=? AND jam=? LIMIT 1");
            ps.setString(1, noRawat); ps.setString(2, tglSql); ps.setString(3, jam);
            rs = ps.executeQuery();
            if (rs.next()) {
                String path = rs.getString(1);
                if (path != null && !path.trim().isEmpty()) {
                    try {
                        javax.swing.ImageIcon icon;
                        if (path.startsWith("http://") || path.startsWith("https://")) {
                            icon = new javax.swing.ImageIcon(new java.net.URL(path));
                        } else {
                            icon = new javax.swing.ImageIcon(path);
                        }
                        if (icon.getIconWidth() > 0) {
                            // Scale image to fit ~400x250
                            int maxW = 400, maxH = 250;
                            int w = icon.getIconWidth(), h = icon.getIconHeight();
                            double ratio = Math.min(1.0 * maxW / w, 1.0 * maxH / h);
                            if (ratio < 1.0) {
                                java.awt.Image img = icon.getImage().getScaledInstance(
                                    (int)(w * ratio), (int)(h * ratio), java.awt.Image.SCALE_SMOOTH);
                                icon = new javax.swing.ImageIcon(img);
                            }
                            lblGambarRad.setIcon(icon);
                            lblGambarRad.setText("");
                        } else {
                            lblGambarRad.setText("(Gambar tidak bisa di-load: " + path + ")");
                        }
                    } catch (Exception ie) {
                        lblGambarRad.setText("(Error load gambar: " + ie.getMessage() + ")");
                    }
                } else {
                    lblGambarRad.setText("(Lokasi gambar kosong)");
                }
            } else {
                lblGambarRad.setText("(Tidak ada gambar tersimpan)");
            }
            rs.close(); ps.close();
        } catch (Exception e) {
            System.out.println("Notifikasi loadHasilRadDetail: " + e);
        }
    }

    // ========== NUTRI (idx 14, textarea bebas) ==========

    private DefaultTableModel tabModeNutri;
    private widget.Table tbNutri;
    private javax.swing.JTextArea TNutri;

    private void setupTabNutri() {
        tabModeNutri = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tanggal", "Jam", "Nutri", "NIP", "Petugas"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbNutri = new widget.Table();
        tbNutri.setModel(tabModeNutri);
        tbNutri.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbNutri.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { getDataNutri(); }
        });
        int[] nCols = {105, 80, 60, 700, 80, 150};
        for (int i = 0; i < nCols.length; i++) tbNutri.getColumnModel().getColumn(i).setPreferredWidth(nCols[i]);

        widget.InternalFrame ifNT = new widget.InternalFrame();
        ifNT.setBorder(null);
        ifNT.setLayout(new java.awt.BorderLayout(1, 1));
        widget.ScrollPane scNT = new widget.ScrollPane();
        scNT.setViewportView(tbNutri);
        ifNT.add(scNT, java.awt.BorderLayout.CENTER);

        widget.panelisi pnlNT = new widget.panelisi();
        pnlNT.setPreferredSize(new Dimension(44, 150));
        pnlNT.setLayout(null);

        javax.swing.JLabel l = new javax.swing.JLabel("Nutri :");
        l.setBounds(0, 10, 60, 23); pnlNT.add(l);
        TNutri = new javax.swing.JTextArea();
        TNutri.setLineWrap(true); TNutri.setWrapStyleWord(true);
        javax.swing.JScrollPane scrNT = new javax.swing.JScrollPane(TNutri);
        scrNT.setBounds(65, 10, 1100, 130); pnlNT.add(scrNT);

        ifNT.add(pnlNT, java.awt.BorderLayout.PAGE_START);
        TabICU.insertTab("Nutri", null, ifNT, null, 14);
    }

    private void simpanNutri() {
        if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); return; }
        String tgl = Valid.SetTgl(Tanggal.getSelectedItem() + "");
        String jam = Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem();
        if (Sequel.menyimpantf("observasi_icu_nutri", "?,?,?,?,?", "Data", 5, new String[]{
            TNoRw.getText(), tgl, jam, TNutri.getText(), NIPHeader.getText()
        })) { tampilNutri(); emptNutri(); }
    }

    private void tampilNutri() {
        Valid.tabelKosong(tabModeNutri);
        try {
            ps = koneksi.prepareStatement(
                "select n.no_rawat,n.tgl_nutri,n.jam_nutri,n.nutri,n.nip,IFNULL(pet.nama,n.nip) "
                + "from observasi_icu_nutri n left join petugas pet on n.nip=pet.nip "
                + "where n.no_rawat=? order by n.tgl_nutri desc, n.jam_nutri desc");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeNutri.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6)});
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilNutri: " + e); }
        LCount.setText("" + tabModeNutri.getRowCount());
    }

    private void hapusNutri() {
        if (tbNutri.getSelectedRow() > -1) {
            int r = tbNutri.getSelectedRow();
            if (Sequel.queryu2tf("delete from observasi_icu_nutri where no_rawat=? and tgl_nutri=? and jam_nutri=?", 3, new String[]{
                tabModeNutri.getValueAt(r, 0).toString(), tabModeNutri.getValueAt(r, 1).toString(), tabModeNutri.getValueAt(r, 2).toString()
            })) { tampilNutri(); emptNutri(); }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan dihapus..!!"); }
    }

    private void editNutri() {
        if (tbNutri.getSelectedRow() > -1) {
            if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); return; }
            int r = tbNutri.getSelectedRow();
            if (Sequel.mengedittf("observasi_icu_nutri", "no_rawat=? and tgl_nutri=? and jam_nutri=?",
                "nutri=?,nip=?", 5, new String[]{
                    TNutri.getText(), NIPHeader.getText(),
                    tabModeNutri.getValueAt(r, 0).toString(),
                    tabModeNutri.getValueAt(r, 1).toString(),
                    tabModeNutri.getValueAt(r, 2).toString()
                })) { tampilNutri(); emptNutri(); }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan diedit..!!"); }
    }

    private void getDataNutri() {
        int r = tbNutri.getSelectedRow();
        if (r < 0) return;
        Object v = tabModeNutri.getValueAt(r, 3);
        TNutri.setText(v == null ? "" : v.toString());
    }

    private void emptNutri() { TNutri.setText(""); }

    // ========== DAILY ICU CHECKLIST (FASTHUG) ==========

    private DefaultTableModel tabModeChecklist;
    private widget.Table tbChecklist;

    private void setupTabDailyChecklist() {
        tabModeChecklist = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tanggal", "Feeding", "Analgesia", "Sedation Vacation",
            "Thromboembolism", "Head Elevation", "Ulcer Prevention", "Glucose Control",
            "SBT Assessment", "Line Review", "Catatan", "NIP", "Petugas"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbChecklist = new widget.Table();
        tbChecklist.setModel(tabModeChecklist);
        tbChecklist.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        widget.InternalFrame ifCL = new widget.InternalFrame();
        ifCL.setBorder(null);
        ifCL.setLayout(new java.awt.BorderLayout(1, 1));
        widget.ScrollPane scCL = new widget.ScrollPane();
        scCL.setViewportView(tbChecklist);
        ifCL.add(scCL, java.awt.BorderLayout.CENTER);

        // Input panel
        widget.panelisi pnlCL = new widget.panelisi();
        pnlCL.setPreferredSize(new Dimension(44, 110));
        pnlCL.setLayout(null);

        String[] fasthugLabels = {"Feeding", "Analgesia", "Sedation Vacation", "Thromboembolism",
            "Head Elevation 30°", "Ulcer Prevention", "Glucose Control", "SBT Assessment", "Line Review"};
        cmbChecklist = new widget.ComboBox[9];
        for (int i = 0; i < 9; i++) {
            int col = i % 3, row = i / 3;
            javax.swing.JLabel lbl = new javax.swing.JLabel(fasthugLabels[i] + " :");
            lbl.setBounds(col * 300, 10 + row * 28, 130, 23);
            pnlCL.add(lbl);
            cmbChecklist[i] = new widget.ComboBox();
            cmbChecklist[i].setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak", "N/A"}));
            cmbChecklist[i].setBounds(col * 300 + 135, 10 + row * 28, 60, 23);
            pnlCL.add(cmbChecklist[i]);
        }
        TCatatanChecklist = new widget.TextBox();
        javax.swing.JLabel lblCatCL = new javax.swing.JLabel("Catatan :");
        lblCatCL.setBounds(0, 95, 60, 23);
        pnlCL.add(lblCatCL);
        TCatatanChecklist.setBounds(65, 95, 600, 23);
        pnlCL.add(TCatatanChecklist);

        ifCL.add(pnlCL, java.awt.BorderLayout.PAGE_START);
        TabICU.addTab("Daily Checklist", ifCL);
    }

    private widget.ComboBox[] cmbChecklist;
    private widget.TextBox TCatatanChecklist;

    private void simpanChecklist() {
        if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); return; }
        String tgl = Valid.SetTgl(Tanggal.getSelectedItem() + "");
        if (Sequel.menyimpantf("icu_daily_checklist", "?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 13, new String[]{
            TNoRw.getText(), tgl,
            cmbChecklist[0].getSelectedItem().toString(), cmbChecklist[1].getSelectedItem().toString(),
            cmbChecklist[2].getSelectedItem().toString(), cmbChecklist[3].getSelectedItem().toString(),
            cmbChecklist[4].getSelectedItem().toString(), cmbChecklist[5].getSelectedItem().toString(),
            cmbChecklist[6].getSelectedItem().toString(), cmbChecklist[7].getSelectedItem().toString(),
            cmbChecklist[8].getSelectedItem().toString(), TCatatanChecklist.getText(),
            NIPHeader.getText()
        })) { tampilChecklist(); }
    }

    private void tampilChecklist() {
        Valid.tabelKosong(tabModeChecklist);
        try {
            ps = koneksi.prepareStatement("select c.no_rawat,c.tgl_checklist,c.feeding,c.analgesia,c.sedation_vacation,c.thromboembolism,c.head_elevation,c.ulcer_prevention,c.glucose_control,c.sbt_assessment,c.line_review,c.catatan,c.nip,IFNULL(pet.nama,c.nip) from icu_daily_checklist c left join petugas pet on c.nip=pet.nip where c.no_rawat=? order by c.tgl_checklist desc");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeChecklist.addRow(new Object[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14)});
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilChecklist: " + e); }
    }

    private void hapusChecklist() {
        if (tbChecklist.getSelectedRow() > -1) {
            int r = tbChecklist.getSelectedRow();
            if (Sequel.queryu2tf("delete from icu_daily_checklist where no_rawat=? and tgl_checklist=?", 2, new String[]{
                tabModeChecklist.getValueAt(r, 0).toString(), tabModeChecklist.getValueAt(r, 1).toString()
            })) { tampilChecklist(); }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan dihapus..!!"); }
    }

    // ========== HANDOVER SBAR ==========

    private DefaultTableModel tabModeSBAR;
    private widget.Table tbSBAR;
    private javax.swing.JTextArea TSituation, TBackground, TAssessment, TRecommendation;
    private widget.ComboBox CmbShiftDari, CmbShiftKe;

    private void setupTabHandoverSBAR() {
        tabModeSBAR = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "Tanggal", "Jam", "Shift Dari", "Shift Ke",
            "Situation", "Background", "Assessment", "Recommendation", "Petugas Dari", "Petugas Ke"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbSBAR = new widget.Table();
        tbSBAR.setModel(tabModeSBAR);
        tbSBAR.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        widget.InternalFrame ifSB = new widget.InternalFrame();
        ifSB.setBorder(null);
        ifSB.setLayout(new java.awt.BorderLayout(1, 1));
        widget.ScrollPane scSB = new widget.ScrollPane();
        scSB.setViewportView(tbSBAR);
        ifSB.add(scSB, java.awt.BorderLayout.CENTER);

        widget.panelisi pnlSB = new widget.panelisi();
        // 4 textarea (Situation/Background/Assessment/Recommend) tinggi 60 + shift row 30 + padding
        pnlSB.setPreferredSize(new Dimension(44, 290));
        pnlSB.setLayout(null);

        javax.swing.JLabel lbl1 = new javax.swing.JLabel("Shift Dari :");
        lbl1.setBounds(0, 10, 70, 23); pnlSB.add(lbl1);
        CmbShiftDari = new widget.ComboBox();
        CmbShiftDari.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Pagi", "Sore", "Malam"}));
        CmbShiftDari.setBounds(75, 10, 80, 23); pnlSB.add(CmbShiftDari);

        javax.swing.JLabel lbl2 = new javax.swing.JLabel("Shift Ke :");
        lbl2.setBounds(170, 10, 60, 23); pnlSB.add(lbl2);
        CmbShiftKe = new widget.ComboBox();
        CmbShiftKe.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Pagi", "Sore", "Malam"}));
        CmbShiftKe.setBounds(235, 10, 80, 23); pnlSB.add(CmbShiftKe);

        // 4 textarea masing-masing 60px tinggi, mulai y=40, gap 5
        int taY = 40, taH = 55, gap = 5, taW = 1080, lblW = 75;
        javax.swing.JLabel lblS = new javax.swing.JLabel("Situation :");
        lblS.setBounds(0, taY, lblW, 23); pnlSB.add(lblS);
        TSituation = new javax.swing.JTextArea();
        TSituation.setLineWrap(true); TSituation.setWrapStyleWord(true);
        javax.swing.JScrollPane scS = new javax.swing.JScrollPane(TSituation);
        scS.setBounds(lblW, taY, taW, taH); pnlSB.add(scS);

        javax.swing.JLabel lblB = new javax.swing.JLabel("Background :");
        lblB.setBounds(0, taY + (taH + gap), lblW, 23); pnlSB.add(lblB);
        TBackground = new javax.swing.JTextArea();
        TBackground.setLineWrap(true); TBackground.setWrapStyleWord(true);
        javax.swing.JScrollPane scB = new javax.swing.JScrollPane(TBackground);
        scB.setBounds(lblW, taY + (taH + gap), taW, taH); pnlSB.add(scB);

        javax.swing.JLabel lblA = new javax.swing.JLabel("Assessment :");
        lblA.setBounds(0, taY + 2*(taH + gap), lblW, 23); pnlSB.add(lblA);
        TAssessment = new javax.swing.JTextArea();
        TAssessment.setLineWrap(true); TAssessment.setWrapStyleWord(true);
        javax.swing.JScrollPane scA = new javax.swing.JScrollPane(TAssessment);
        scA.setBounds(lblW, taY + 2*(taH + gap), taW, taH); pnlSB.add(scA);

        javax.swing.JLabel lblR = new javax.swing.JLabel("Recommend :");
        lblR.setBounds(0, taY + 3*(taH + gap), lblW, 23); pnlSB.add(lblR);
        TRecommendation = new javax.swing.JTextArea();
        TRecommendation.setLineWrap(true); TRecommendation.setWrapStyleWord(true);
        javax.swing.JScrollPane scR = new javax.swing.JScrollPane(TRecommendation);
        scR.setBounds(lblW, taY + 3*(taH + gap), taW, taH); pnlSB.add(scR);

        ifSB.add(pnlSB, java.awt.BorderLayout.PAGE_START);
        TabICU.addTab("Handover SBAR", ifSB);

        // Pemantauan Tambahan TIDAK lagi ditampilkan sebagai tab terpisah —
        // semua field (Skala Sedasi, CRT, Lingkar Perut, Sekret) sudah dipindah
        // ke tab "Respirasi" untuk konsolidasi. Komponen tetap ada di memory
        // untuk back-compat (save/load tampilan tabel).
        // (Sebelumnya: TabICU.addTab("Pemantauan Tambahan", internalFrameMonitoringTambahan);)
    }

    private void simpanSBAR() {
        if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); return; }
        String tgl = Valid.SetTgl(Tanggal.getSelectedItem() + "");
        String jam = Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem();
        if (Sequel.menyimpantf("icu_handover_sbar", "?,?,?,?,?,?,?,?,?,?,?", "Data", 11, new String[]{
            TNoRw.getText(), tgl, jam,
            CmbShiftDari.getSelectedItem().toString(), CmbShiftKe.getSelectedItem().toString(),
            TSituation.getText(), TBackground.getText(), TAssessment.getText(), TRecommendation.getText(),
            NIPHeader.getText(), ""
        })) { tampilSBAR(); TSituation.setText(""); TBackground.setText(""); TAssessment.setText(""); TRecommendation.setText(""); }
    }

    private void tampilSBAR() {
        Valid.tabelKosong(tabModeSBAR);
        try {
            ps = koneksi.prepareStatement("select s.no_rawat,s.tgl_handover,s.jam_handover,s.shift_dari,s.shift_ke,s.situation,s.background,s.assessment,s.recommendation,IFNULL(p1.nama,s.nip_dari),IFNULL(p2.nama,s.nip_ke) from icu_handover_sbar s left join petugas p1 on s.nip_dari=p1.nip left join petugas p2 on s.nip_ke=p2.nip where s.no_rawat=? order by s.tgl_handover desc,s.jam_handover desc");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeSBAR.addRow(new Object[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),rs.getString(11)});
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilSBAR: " + e); }
    }

    private void hapusSBAR() {
        if (tbSBAR.getSelectedRow() > -1) {
            int r = tbSBAR.getSelectedRow();
            if (Sequel.queryu2tf("delete from icu_handover_sbar where no_rawat=? and tgl_handover=? and jam_handover=?", 3, new String[]{
                tabModeSBAR.getValueAt(r, 0).toString(), tabModeSBAR.getValueAt(r, 1).toString(), tabModeSBAR.getValueAt(r, 2).toString()
            })) { tampilSBAR(); }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan dihapus..!!"); }
    }

    // ========== SIDEBAR ==========

    private widget.PanelBiasa PanelAccor, FormMenu;
    private widget.CekBox ChkAccor;
    private widget.ScrollPane ScrollMenu;
    private widget.Button BtnRiwayat, BtnRiwayatSOAPIE, BtnResepObat, BtnCopyResep,
            BtnPermintaanLab, BtnPermintaanRad, BtnDiagnosa, BtnResume,
            BtnBerkasDigital, BtnPermintaanKonsul, BtnSkriningNutrisi,
            BtnPenilaianResikoJatuh, BtnTransferRuang, BtnDashboardICU;

    private widget.Button createMenuButton(String text) {
        widget.Button btn = new widget.Button();
        btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png")));
        btn.setText(text);
        btn.setFocusPainted(false);
        btn.setFont(new java.awt.Font("Tahoma", 0, 11));
        btn.setGlassColor(new java.awt.Color(255, 255, 255));
        btn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btn.setMargin(new java.awt.Insets(1, 1, 1, 1));
        btn.setPreferredSize(new java.awt.Dimension(190, 23));
        btn.setRoundRect(false);
        return btn;
    }

    private void isMenu() {
        if (ChkAccor.isSelected()) {
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(205, HEIGHT));
            FormMenu.setVisible(true);
            ChkAccor.setVisible(true);
        } else {
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(15, HEIGHT));
            FormMenu.setVisible(false);
            ChkAccor.setVisible(true);
        }
    }

    private void openRiwayatPasien() {
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        rekammedis.RMRiwayatPerawatan form = new rekammedis.RMRiwayatPerawatan(null, true);
        form.setNoRm(TNoRM.getText(), TPasien.getText());
        form.setSize(internalFrame1.getWidth(), internalFrame1.getHeight());
        form.setLocationRelativeTo(internalFrame1);
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void openRiwayatSOAPIE() {
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        rekammedis.RMRiwayatSOAPIE form = new rekammedis.RMRiwayatSOAPIE(null, true);
        form.setNoRm(TNoRM.getText(), TNoRw.getText());
        form.setSize(internalFrame1.getWidth(), internalFrame1.getHeight());
        form.setLocationRelativeTo(internalFrame1);
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void openResepObat() {
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        inventory.DlgPeresepanDokter form = new inventory.DlgPeresepanDokter(null, false);
        form.setSize(internalFrame1.getWidth(), internalFrame1.getHeight());
        form.setLocationRelativeTo(internalFrame1);
        form.setNoRm(TNoRw.getText(), Tanggal.getDate(),
            Jam.getSelectedItem().toString(), Menit.getSelectedItem().toString(),
            Detik.getSelectedItem().toString(), "", "", "ranap");
        form.isCek();
        form.tampilobat();
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void openCopyResep() {
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        inventory.DlgCopyResep form = new inventory.DlgCopyResep(null, false);
        form.isCek();
        form.setRM(TNoRw.getText(), TNoRM.getText(), "", "", "ranap");
        form.tampil();
        form.setSize(internalFrame1.getWidth(), internalFrame1.getHeight());
        form.setLocationRelativeTo(internalFrame1);
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void openPermintaanLab() {
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        permintaan.DlgPermintaanLaboratorium form = new permintaan.DlgPermintaanLaboratorium(null, false);
        form.setSize(internalFrame1.getWidth(), internalFrame1.getHeight());
        form.setLocationRelativeTo(internalFrame1);
        form.emptTeks();
        form.isCek();
        form.setNoRm(TNoRw.getText(), "Ranap");
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void openPermintaanRad() {
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        permintaan.DlgPermintaanRadiologi form = new permintaan.DlgPermintaanRadiologi(null, false);
        form.setSize(internalFrame1.getWidth(), internalFrame1.getHeight());
        form.setLocationRelativeTo(internalFrame1);
        form.emptTeks();
        form.isCek();
        form.setNoRm(TNoRw.getText(), "Ranap");
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void openDiagnosa() {
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        laporan.DlgDiagnosaPenyakit form = new laporan.DlgDiagnosaPenyakit(null, false);
        form.isCek();
        form.setSize(internalFrame1.getWidth(), internalFrame1.getHeight());
        form.setLocationRelativeTo(internalFrame1);
        form.setNoRm(TNoRw.getText(), Tanggal.getDate(), Tanggal.getDate(), "Ranap");
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void openResume() {
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        rekammedis.RMDataResumePasienRanap form = new rekammedis.RMDataResumePasienRanap(null, false);
        form.isCek();
        form.setSize(internalFrame1.getWidth(), internalFrame1.getHeight());
        form.setLocationRelativeTo(internalFrame1);
        form.setNoRm(TNoRw.getText(), Tanggal.getDate());
        form.tampil();
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void openBerkasDigital() {
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        laporan.DlgBerkasRawat form = new laporan.DlgBerkasRawat(null, true);
        form.setJudul("::[ Berkas Digital Perawatan ]::", "berkasrawat/pages");
        try {
            form.loadURL("http://" + fungsi.koneksiDB.HOSTHYBRIDWEB() + ":" + fungsi.koneksiDB.PORTWEB() + "/"
                + fungsi.koneksiDB.HYBRIDWEB() + "/berkasrawat/login2.php?act=login&usere="
                + fungsi.koneksiDB.USERHYBRIDWEB() + "&passwordte=" + fungsi.koneksiDB.PASHYBRIDWEB()
                + "&no_rawat=" + TNoRw.getText());
        } catch (Exception ex) {
            System.out.println("Notifikasi : " + ex);
        }
        form.setSize(internalFrame1.getWidth(), internalFrame1.getHeight());
        form.setLocationRelativeTo(internalFrame1);
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void openKonsultasiMedik() {
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        permintaan.DlgPermintaanKonsultasiMedik form = new permintaan.DlgPermintaanKonsultasiMedik(null, false);
        form.isCek();
        form.setSize(internalFrame1.getWidth(), internalFrame1.getHeight());
        form.setLocationRelativeTo(internalFrame1);
        form.setNoRm(TNoRw.getText(), TNoRM.getText(), TPasien.getText());
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void openSkriningNutrisi() {
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        rekammedis.RMSkriningNutrisiDewasa form = new rekammedis.RMSkriningNutrisiDewasa(null, false);
        form.isCek();
        form.setSize(internalFrame1.getWidth(), internalFrame1.getHeight());
        form.setLocationRelativeTo(internalFrame1);
        form.setNoRm(TNoRw.getText(), Tanggal.getDate());
        form.tampil();
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void openPenilaianResikoJatuh() {
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        rekammedis.RMPenilaianLanjutanRisikoJatuhDewasa form = new rekammedis.RMPenilaianLanjutanRisikoJatuhDewasa(null, false);
        form.isCek();
        form.setSize(internalFrame1.getWidth(), internalFrame1.getHeight());
        form.setLocationRelativeTo(internalFrame1);
        form.setNoRm(TNoRw.getText(), Tanggal.getDate());
        form.tampil();
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void openTransferRuang() {
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        rekammedis.RMTransferPasienAntarRuang form = new rekammedis.RMTransferPasienAntarRuang(null, false);
        form.isCek();
        form.setSize(internalFrame1.getWidth(), internalFrame1.getHeight());
        form.setLocationRelativeTo(internalFrame1);
        form.setNoRm(TNoRw.getText(), Tanggal.getDate());
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void openDashboardICU() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            javax.swing.JDialog dlg = new javax.swing.JDialog((java.awt.Frame) null, "Dashboard Ringkasan ICU/HCU", true);
            java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            dlg.setSize(screenSize.width, screenSize.height);
            dlg.setLocation(0, 0);

            String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
            StringBuilder html = new StringBuilder();
            html.append("<html><body style='font-family:Tahoma;font-size:11px;padding:10px;background:#f5f5f5'>");
            html.append("<h2 style='text-align:center;color:#333;margin:5px'>DASHBOARD MONITORING ICU/HCU</h2>");
            html.append("<p style='text-align:center;font-size:10px;color:#666'>").append(akses.getnamars()).append(" | ").append(new java.text.SimpleDateFormat("dd MMMM yyyy, HH:mm").format(new java.util.Date())).append(" WIB</p>");

            // ===== SUMMARY CARDS =====
            String jmlPasien = Sequel.cariIsi("select count(*) from kamar_inap ki inner join kamar k on ki.kd_kamar=k.kd_kamar inner join bangsal b on k.kd_bangsal=b.kd_bangsal where ki.stts_pulang='-' and (b.nm_bangsal like '%ICU%' or b.nm_bangsal like '%HCU%' or b.nm_bangsal like '%Intensif%')");
            String jmlVentilator = Sequel.cariIsi("select count(distinct no_rawat) from observasi_icu_ventilator where tgl_observasi='" + today + "'");
            String totalBed = Sequel.cariIsi("select count(*) from kamar k inner join bangsal b on k.kd_bangsal=b.kd_bangsal where (b.nm_bangsal like '%ICU%' or b.nm_bangsal like '%HCU%' or b.nm_bangsal like '%Intensif%') and k.statusdata='1'");
            String bedKosong = Sequel.cariIsi("select count(*) from kamar k inner join bangsal b on k.kd_bangsal=b.kd_bangsal where (b.nm_bangsal like '%ICU%' or b.nm_bangsal like '%HCU%' or b.nm_bangsal like '%Intensif%') and k.statusdata='1' and k.status='KOSONG'");
            String avgLOS = Sequel.cariIsi("select IFNULL(ROUND(AVG(DATEDIFF(CURDATE(),ki.tgl_masuk)+1),1),0) from kamar_inap ki inner join kamar k on ki.kd_kamar=k.kd_kamar inner join bangsal b on k.kd_bangsal=b.kd_bangsal where ki.stts_pulang='-' and (b.nm_bangsal like '%ICU%' or b.nm_bangsal like '%HCU%' or b.nm_bangsal like '%Intensif%')");

            html.append("<table width='100%' cellpadding='0' cellspacing='8' border='0'><tr>");
            html.append("<td width='20%' style='background:#4CAF50;color:white;padding:12px;text-align:center;'><b style='font-size:24px'>").append(jmlPasien).append("</b><br>Pasien Aktif</td>");
            html.append("<td width='20%' style='background:#2196F3;color:white;padding:12px;text-align:center;'><b style='font-size:24px'>").append(jmlVentilator).append("</b><br>On Ventilator</td>");
            html.append("<td width='20%' style='background:#FF9800;color:white;padding:12px;text-align:center;'><b style='font-size:24px'>").append(bedKosong).append("/").append(totalBed).append("</b><br>Bed Tersedia</td>");
            html.append("<td width='20%' style='background:#9C27B0;color:white;padding:12px;text-align:center;'><b style='font-size:24px'>").append(avgLOS).append("</b><br>Avg LOS (hari)</td>");

            // Occupancy rate
            int iBed = 0, iKosong = 0;
            try { iBed = Integer.parseInt(totalBed); iKosong = Integer.parseInt(bedKosong); } catch (Exception ex) {}
            int occupancy = iBed > 0 ? ((iBed - iKosong) * 100 / iBed) : 0;
            String occColor = occupancy > 85 ? "#F44336" : occupancy > 60 ? "#FF9800" : "#4CAF50";
            html.append("<td width='20%' style='background:").append(occColor).append(";color:white;padding:12px;text-align:center;'><b style='font-size:24px'>").append(occupancy).append("%</b><br>BOR ICU</td>");
            html.append("</tr></table>");

            // ===== PASIEN AKTIF + VITAL SIGN TERAKHIR =====
            html.append("<h3 style='background:#e0e0e0;padding:5px;border-left:4px solid #333;margin-top:10px'>Monitoring Pasien ICU Aktif</h3>");
            html.append("<table border='1' cellpadding='4' cellspacing='0' width='100%' style='font-size:9px;border-collapse:collapse'>");
            html.append("<tr style='background:#37474F;color:white'><th>No</th><th>Nama</th><th>Kamar</th><th>Hari</th><th>Diagnosa</th><th>HR</th><th>Temp</th><th>RR</th><th>NIBP</th><th>SpO2</th><th>GCS</th><th>Ventilator</th><th>Balance</th><th>Checklist</th><th>SBAR</th></tr>");
            ps = koneksi.prepareStatement(
                "select ki.no_rawat,p.nm_pasien,ki.kd_kamar,DATEDIFF(CURDATE(),ki.tgl_masuk)+1 as hari_ke,ki.diagnosa_awal "
                + "from kamar_inap ki inner join reg_periksa rp on ki.no_rawat=rp.no_rawat "
                + "inner join pasien p on rp.no_rkm_medis=p.no_rkm_medis "
                + "inner join kamar k on ki.kd_kamar=k.kd_kamar inner join bangsal b on k.kd_bangsal=b.kd_bangsal "
                + "where ki.stts_pulang='-' and (b.nm_bangsal like '%ICU%' or b.nm_bangsal like '%HCU%' or b.nm_bangsal like '%Intensif%') order by ki.tgl_masuk");
            rs = ps.executeQuery();
            int no = 1;
            while (rs.next()) {
                String nrw = rs.getString(1);
                int hariKe = rs.getInt(4);

                // Vital sign terakhir
                String lastHR = Sequel.cariIsi("select hr from observasi_icu_hemodinamik where no_rawat='" + nrw + "' order by tgl_observasi desc, jam desc limit 1");
                String lastTemp = Sequel.cariIsi("select suhu from observasi_icu_hemodinamik where no_rawat='" + nrw + "' order by tgl_observasi desc, jam desc limit 1");
                String lastRR = Sequel.cariIsi("select rr from observasi_icu_hemodinamik where no_rawat='" + nrw + "' order by tgl_observasi desc, jam desc limit 1");
                String lastNIBP = Sequel.cariIsi("select concat(bp_sistol,'/',bp_diastol) from observasi_icu_hemodinamik where no_rawat='" + nrw + "' order by tgl_observasi desc, jam desc limit 1");
                String lastSpO2 = Sequel.cariIsi("select saturasi_o2 from observasi_icu_hemodinamik where no_rawat='" + nrw + "' order by tgl_observasi desc, jam desc limit 1");
                String lastGCS = Sequel.cariIsi("select concat(gcs_e,'/',gcs_v,'/',gcs_m) from observasi_icu_hemodinamik where no_rawat='" + nrw + "' order by tgl_observasi desc, jam desc limit 1");
                String onVent = Sequel.cariIsi("select tipe_ventilator from observasi_icu_ventilator where no_rawat='" + nrw + "' order by tgl_observasi desc, jam_observasi desc limit 1");
                String lastBalance = Sequel.cariIsi("select keseimbangan_cairan from observasi_icu_intake_output where no_rawat='" + nrw + "' order by tgl_observasi desc, jam_observasi desc limit 1");
                String hasChecklist = Sequel.cariIsi("select count(*) from icu_daily_checklist where no_rawat='" + nrw + "' and tgl_checklist='" + today + "'");
                String hasSBAR = Sequel.cariIsi("select count(*) from icu_handover_sbar where no_rawat='" + nrw + "' and tgl_handover='" + today + "'");

                // Warna baris
                String rowStyle = "";
                if (hariKe > 7) rowStyle = " style='background:#FFF3E0'";

                // Alert vital sign
                String hrStyle = "", tempStyle = "", rrStyle = "", spo2Style = "", gcsStyle = "";
                try {
                    double dHR = lastHR.isEmpty() ? 0 : Double.parseDouble(lastHR);
                    if (dHR > 0 && (dHR < 60 || dHR > 120)) hrStyle = " style='background:#FFCDD2;color:red;font-weight:bold'";
                } catch (Exception ex) {}
                try {
                    double dTemp = lastTemp.isEmpty() ? 0 : Double.parseDouble(lastTemp);
                    if (dTemp > 0 && (dTemp < 36.0 || dTemp > 38.5)) tempStyle = " style='background:#FFCDD2;color:red;font-weight:bold'";
                } catch (Exception ex) {}
                try {
                    double dRR = lastRR.isEmpty() ? 0 : Double.parseDouble(lastRR);
                    if (dRR > 0 && (dRR < 12 || dRR > 25)) rrStyle = " style='background:#FFCDD2;color:red;font-weight:bold'";
                } catch (Exception ex) {}
                try {
                    double dSpO2 = lastSpO2.isEmpty() ? 0 : Double.parseDouble(lastSpO2);
                    if (dSpO2 > 0 && dSpO2 < 90) spo2Style = " style='background:#FFCDD2;color:red;font-weight:bold'";
                } catch (Exception ex) {}
                try {
                    double dGCS = lastGCS.isEmpty() ? 0 : Double.parseDouble(lastGCS);
                    if (dGCS > 0 && dGCS < 9) gcsStyle = " style='background:#FFE0B2;color:#E65100;font-weight:bold'";
                } catch (Exception ex) {}

                String checkIcon = "0".equals(hasChecklist) ? "<font color='red'>Belum</font>" : "<font color='green'>Done</font>";
                String sbarIcon = "0".equals(hasSBAR) ? "<font color='red'>Belum</font>" : "<font color='green'>Done ("+hasSBAR+")</font>";

                html.append("<tr").append(rowStyle).append(">");
                html.append("<td align='center'>").append(no++).append("</td>");
                html.append("<td><b>").append(rs.getString(2)).append("</b></td>");
                html.append("<td align='center'>").append(rs.getString(3)).append("</td>");
                html.append("<td align='center'><b>").append(hariKe).append("</b></td>");
                html.append("<td>").append(rs.getString(5) == null ? "" : rs.getString(5)).append("</td>");
                html.append("<td align='center'").append(hrStyle).append(">").append(lastHR).append("</td>");
                html.append("<td align='center'").append(tempStyle).append(">").append(lastTemp).append("</td>");
                html.append("<td align='center'").append(rrStyle).append(">").append(lastRR).append("</td>");
                html.append("<td align='center'>").append(lastNIBP).append("</td>");
                html.append("<td align='center'").append(spo2Style).append(">").append(lastSpO2).append("</td>");
                html.append("<td align='center'").append(gcsStyle).append(">").append(lastGCS).append("</td>");
                html.append("<td align='center'>").append(onVent.isEmpty() ? "-" : onVent).append("</td>");
                html.append("<td align='center'>").append(lastBalance.isEmpty() ? "-" : lastBalance).append("</td>");
                html.append("<td align='center'>").append(checkIcon).append("</td>");
                html.append("<td align='center'>").append(sbarIcon).append("</td>");
                html.append("</tr>");
            }
            rs.close(); ps.close();
            html.append("</table>");

            // ===== BED OCCUPANCY MAP =====
            html.append("<h3 style='background:#e0e0e0;padding:5px;border-left:4px solid #333;margin-top:10px'>Peta Bed ICU/HCU</h3>");
            html.append("<table cellpadding='8' cellspacing='5' border='0' style='font-size:9px'><tr>");
            ps = koneksi.prepareStatement(
                "select k.kd_kamar, k.status, b.nm_bangsal, "
                + "IFNULL((select p.nm_pasien from kamar_inap ki inner join reg_periksa rp on ki.no_rawat=rp.no_rawat inner join pasien p on rp.no_rkm_medis=p.no_rkm_medis where ki.kd_kamar=k.kd_kamar and ki.stts_pulang='-' limit 1),'') as pasien "
                + "from kamar k inner join bangsal b on k.kd_bangsal=b.kd_bangsal "
                + "where (b.nm_bangsal like '%ICU%' or b.nm_bangsal like '%HCU%' or b.nm_bangsal like '%Intensif%') and k.statusdata='1' order by k.kd_kamar");
            rs = ps.executeQuery();
            while (rs.next()) {
                String status = rs.getString(2);
                String bgColor = "KOSONG".equals(status) ? "#C8E6C9" : "#FFCDD2";
                String pasien = rs.getString(4);
                html.append("<td style='background:").append(bgColor).append(";border:2px solid #999;width:120px;text-align:center'>");
                html.append("<b>").append(rs.getString(1)).append("</b><br>");
                html.append("<font size='1'>").append(rs.getString(3)).append("</font><br>");
                html.append(pasien.isEmpty() ? "<font color='green'>KOSONG</font>" : "<font color='red'>" + pasien + "</font>");
                html.append("</td>");
            }
            rs.close(); ps.close();
            html.append("</tr></table>");

            // ===== STATISTIK AKTIVITAS =====
            html.append("<h3 style='background:#e0e0e0;padding:5px;border-left:4px solid #333;margin-top:10px'>Statistik Aktivitas Hari Ini</h3>");
            html.append("<table border='1' cellpadding='4' cellspacing='0' width='60%' style='font-size:10px;border-collapse:collapse'>");
            html.append("<tr style='background:#e8e8e8'><th>Aktivitas</th><th>Jumlah</th><th>Status</th></tr>");

            String[][] stats = {
                {"Observasi Hemodinamik", Sequel.cariIsi("select count(*) from observasi_icu_hemodinamik where tgl_observasi='" + today + "'")},
                {"Observasi Ventilator", Sequel.cariIsi("select count(*) from observasi_icu_ventilator where tgl_observasi='" + today + "'")},
                {"Intake-Output", Sequel.cariIsi("select count(*) from observasi_icu_intake_output where tgl_observasi='" + today + "'")},
                {"Implementasi Keperawatan", Sequel.cariIsi("select count(*) from observasi_icu_catatan_keperawatan where tgl_catat='" + today + "'")},
                {"Catatan Pengobatan", Sequel.cariIsi("select count(*) from observasi_icu_catatan_pengobatan where tgl_observasi='" + today + "'")},
                {"Pemeriksaan Penunjang", Sequel.cariIsi("select count(*) from observasi_icu_pemeriksaan_penunjang where tgl_pemeriksaan='" + today + "'")},
                {"Daily Checklist FASTHUG", Sequel.cariIsi("select count(*) from icu_daily_checklist where tgl_checklist='" + today + "'")},
                {"Handover SBAR", Sequel.cariIsi("select count(*) from icu_handover_sbar where tgl_handover='" + today + "'")},
            };
            for (String[] st : stats) {
                int jml = 0;
                try { jml = Integer.parseInt(st[1]); } catch (Exception ex) {}
                String statusIcon = jml > 0 ? "<font color='green'>&#10004;</font>" : "<font color='red'>&#10008;</font>";
                html.append("<tr><td>").append(st[0]).append("</td><td align='center'><b>").append(st[1]).append("</b></td><td align='center'>").append(statusIcon).append("</td></tr>");
            }
            html.append("</table>");

            // ===== GRAFIK PER PASIEN =====
            html.append("<h3 style='background:#e0e0e0;padding:5px;border-left:4px solid #333;margin-top:10px'>Grafik Hemodinamik & Balance Cairan</h3>");
            {
                // Query semua pasien ICU aktif
                PreparedStatement psGraf = koneksi.prepareStatement(
                    "select ki.no_rawat,p.nm_pasien from kamar_inap ki "
                    + "inner join reg_periksa rp on ki.no_rawat=rp.no_rawat "
                    + "inner join pasien p on rp.no_rkm_medis=p.no_rkm_medis "
                    + "inner join kamar k on ki.kd_kamar=k.kd_kamar inner join bangsal b on k.kd_bangsal=b.kd_bangsal "
                    + "where ki.stts_pulang='-' and (b.nm_bangsal like '%ICU%' or b.nm_bangsal like '%HCU%' or b.nm_bangsal like '%Intensif%') order by ki.tgl_masuk");
                ResultSet rsGraf = psGraf.executeQuery();
                while (rsGraf.next()) {
                    String nrwG = rsGraf.getString(1);
                    String nmG = rsGraf.getString(2);

                    // ---- Grafik Hemodinamik ----
                    java.util.List<String> gJam = new java.util.ArrayList<>();
                    java.util.List<Double> gHR = new java.util.ArrayList<>();
                    java.util.List<Double> gRR = new java.util.ArrayList<>();
                    java.util.List<Double> gTemp = new java.util.ArrayList<>();
                    java.util.List<Double> gSpO2 = new java.util.ArrayList<>();

                    PreparedStatement psH = koneksi.prepareStatement(
                        "select jam,IFNULL(hr,0),IFNULL(rr,0),IFNULL(suhu,0),IFNULL(saturasi_o2,0) "
                        + "from observasi_icu_hemodinamik where no_rawat=? order by tgl_observasi,jam");
                    psH.setString(1, nrwG);
                    ResultSet rsH = psH.executeQuery();
                    while (rsH.next()) {
                        gJam.add(rsH.getString(1));
                        try { gHR.add(Double.parseDouble(rsH.getString(2))); } catch (Exception ex) { gHR.add(0.0); }
                        try { gRR.add(Double.parseDouble(rsH.getString(3))); } catch (Exception ex) { gRR.add(0.0); }
                        try { gTemp.add(Double.parseDouble(rsH.getString(4))); } catch (Exception ex) { gTemp.add(0.0); }
                        try { gSpO2.add(Double.parseDouble(rsH.getString(5))); } catch (Exception ex) { gSpO2.add(0.0); }
                    }
                    rsH.close(); psH.close();

                    if (gJam.size() >= 2) {
                        int imgW = 450, imgH = 180, svgL = 40, svgR = 10, svgT = 25, svgB = 40;
                        int gw = imgW - svgL - svgR, gh = imgH - svgT - svgB;
                        double maxV = 200.0;
                        int n = gJam.size();
                        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(imgW, imgH, java.awt.image.BufferedImage.TYPE_INT_RGB);
                        java.awt.Graphics2D g2 = img.createGraphics();
                        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(java.awt.Color.WHITE); g2.fillRect(0, 0, imgW, imgH);
                        g2.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 10));
                        g2.setColor(java.awt.Color.DARK_GRAY);
                        g2.drawString("Hemodinamik - " + nmG, svgL, 14);
                        g2.setFont(new java.awt.Font("Tahoma", 0, 8));
                        for (int yi = 0; yi <= 5; yi++) {
                            int y = svgT + gh - (yi * gh / 5);
                            g2.setColor(new java.awt.Color(230, 230, 230)); g2.drawLine(svgL, y, svgL + gw, y);
                            g2.setColor(java.awt.Color.GRAY); g2.drawString(String.valueOf(yi * 40), 5, y + 4);
                        }
                        g2.setColor(java.awt.Color.DARK_GRAY);
                        g2.drawLine(svgL, svgT, svgL, svgT + gh); g2.drawLine(svgL, svgT + gh, svgL + gw, svgT + gh);
                        java.awt.Color[] colors = {java.awt.Color.RED, java.awt.Color.BLUE, new java.awt.Color(255, 140, 0), new java.awt.Color(0, 150, 0)};
                        String[] labels = {"HR", "RR", "Temp", "SpO2"};
                        java.util.List<?>[] da = {gHR, gRR, gTemp, gSpO2};
                        for (int s = 0; s < 4; s++) {
                            @SuppressWarnings("unchecked") java.util.List<Double> d = (java.util.List<Double>) da[s];
                            g2.setColor(colors[s]); g2.setStroke(new java.awt.BasicStroke(1.5f));
                            for (int xi = 1; xi < n; xi++) {
                                int x1 = svgL + ((xi - 1) * gw / (n - 1)), y1 = svgT + gh - (int) (d.get(xi - 1) / maxV * gh);
                                int x2 = svgL + (xi * gw / (n - 1)), y2 = svgT + gh - (int) (d.get(xi) / maxV * gh);
                                g2.drawLine(x1, y1, x2, y2);
                                g2.fillOval(x1 - 2, y1 - 2, 4, 4);
                                if (xi == n - 1) g2.fillOval(x2 - 2, y2 - 2, 4, 4);
                            }
                        }
                        g2.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 9));
                        for (int s = 0; s < 4; s++) {
                            int lx = svgL + 5 + s * 80;
                            g2.setColor(colors[s]); g2.fillRect(lx, svgT + gh + 20, 12, 3); g2.drawString(labels[s], lx + 16, svgT + gh + 25);
                        }
                        g2.dispose();
                        java.io.File fHemo = new java.io.File("dashboard_hemo_" + nrwG.replace("/", "_") + ".png");
                        javax.imageio.ImageIO.write(img, "png", fHemo);
                        html.append("<img src='file:").append(fHemo.getAbsolutePath()).append("' width='450' height='180'/>&nbsp;");
                    }

                    // ---- Grafik Balance Cairan ----
                    java.util.List<String> bTgl = new java.util.ArrayList<>();
                    java.util.List<Double> bIntake = new java.util.ArrayList<>();
                    java.util.List<Double> bOutput = new java.util.ArrayList<>();
                    java.util.List<Double> bBalance = new java.util.ArrayList<>();

                    PreparedStatement psB = koneksi.prepareStatement(
                        "select tgl_observasi,SUM(IFNULL(kumulatif,0)),SUM(IFNULL(output_kumulatif,0)),SUM(IFNULL(keseimbangan_cairan,0)) "
                        + "from observasi_icu_intake_output where no_rawat=? group by tgl_observasi order by tgl_observasi");
                    psB.setString(1, nrwG);
                    ResultSet rsB = psB.executeQuery();
                    while (rsB.next()) {
                        bTgl.add(rsB.getString(1));
                        bIntake.add(rsB.getDouble(2));
                        bOutput.add(rsB.getDouble(3));
                        bBalance.add(rsB.getDouble(4));
                    }
                    rsB.close(); psB.close();

                    if (bTgl.size() >= 1) {
                        int imgW = 450, imgH = 180, svgL = 50, svgR = 10, svgT = 25, svgB = 40;
                        int gw = imgW - svgL - svgR, gh = imgH - svgT - svgB;
                        int n = bTgl.size();
                        double maxV = 100;
                        for (int i2 = 0; i2 < n; i2++) {
                            maxV = Math.max(maxV, Math.abs(bIntake.get(i2)));
                            maxV = Math.max(maxV, Math.abs(bOutput.get(i2)));
                        }
                        maxV = Math.ceil(maxV / 100) * 100;
                        if (maxV == 0) maxV = 100;

                        java.awt.image.BufferedImage img2 = new java.awt.image.BufferedImage(imgW, imgH, java.awt.image.BufferedImage.TYPE_INT_RGB);
                        java.awt.Graphics2D g2 = img2.createGraphics();
                        g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(java.awt.Color.WHITE); g2.fillRect(0, 0, imgW, imgH);
                        g2.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 10));
                        g2.setColor(java.awt.Color.DARK_GRAY);
                        g2.drawString("Balance Cairan - " + nmG, svgL, 14);

                        int midY = svgT + gh / 2;
                        g2.setColor(java.awt.Color.DARK_GRAY);
                        g2.drawLine(svgL, midY, svgL + gw, midY);
                        g2.setFont(new java.awt.Font("Tahoma", 0, 8));
                        g2.drawString("0", svgL - 15, midY + 4);

                        int barW = Math.max(15, gw / (n * 4));
                        for (int i2 = 0; i2 < n; i2++) {
                            int cx = svgL + (n == 1 ? gw / 2 : (i2 * gw / (n - 1 > 0 ? n - 1 : 1)));
                            int hI = (int) (bIntake.get(i2) / maxV * (gh / 2));
                            g2.setColor(new java.awt.Color(100, 200, 100));
                            g2.fillRect(cx - barW, midY - hI, barW, hI);
                            int hO = (int) (bOutput.get(i2) / maxV * (gh / 2));
                            g2.setColor(new java.awt.Color(220, 100, 100));
                            g2.fillRect(cx, midY, barW, hO);
                            int yBal = midY - (int) (bBalance.get(i2) / maxV * (gh / 2));
                            g2.setColor(java.awt.Color.BLUE);
                            g2.fillOval(cx - 3, yBal - 3, 6, 6);
                            if (i2 > 0) {
                                int prevCx = svgL + ((i2 - 1) * gw / (n - 1 > 0 ? n - 1 : 1));
                                int prevYBal = midY - (int) (bBalance.get(i2 - 1) / maxV * (gh / 2));
                                g2.setStroke(new java.awt.BasicStroke(2f));
                                g2.drawLine(prevCx, prevYBal, cx, yBal);
                            }
                        }
                        g2.setFont(new java.awt.Font("Tahoma", java.awt.Font.BOLD, 8));
                        int lx = svgL + 5;
                        g2.setColor(new java.awt.Color(100, 200, 100)); g2.fillRect(lx, svgT + gh + 20, 12, 8); g2.drawString("Intake", lx + 16, svgT + gh + 28); lx += 70;
                        g2.setColor(new java.awt.Color(220, 100, 100)); g2.fillRect(lx, svgT + gh + 20, 12, 8); g2.drawString("Output", lx + 16, svgT + gh + 28); lx += 70;
                        g2.setColor(java.awt.Color.BLUE); g2.fillOval(lx, svgT + gh + 21, 8, 8); g2.drawString("Balance", lx + 12, svgT + gh + 28);
                        g2.dispose();
                        java.io.File fBal = new java.io.File("dashboard_balance_" + nrwG.replace("/", "_") + ".png");
                        javax.imageio.ImageIO.write(img2, "png", fBal);
                        html.append("<img src='file:").append(fBal.getAbsolutePath()).append("' width='450' height='180'/>");
                    }
                    html.append("<br>");
                }
                rsGraf.close(); psGraf.close();
            }

            // ===== ALERT & WARNING =====
            html.append("<h3 style='background:#FFCDD2;padding:5px;border-left:4px solid #F44336;margin-top:10px;color:#B71C1C'>Alert & Warning</h3>");
            html.append("<table width='100%' style='font-size:10px'>");
            // Pasien rawat > 7 hari
            String longStay = Sequel.cariIsi("select count(*) from kamar_inap ki inner join kamar k on ki.kd_kamar=k.kd_kamar inner join bangsal b on k.kd_bangsal=b.kd_bangsal where ki.stts_pulang='-' and (b.nm_bangsal like '%ICU%' or b.nm_bangsal like '%HCU%' or b.nm_bangsal like '%Intensif%') and DATEDIFF(CURDATE(),ki.tgl_masuk)+1 > 7");
            if (!"0".equals(longStay)) html.append("<tr><td>&#9888; <b>").append(longStay).append(" pasien</b> rawat ICU lebih dari 7 hari</td></tr>");
            // Checklist belum lengkap
            String blmChecklist = Sequel.cariIsi("select count(*) from kamar_inap ki inner join kamar k on ki.kd_kamar=k.kd_kamar inner join bangsal b on k.kd_bangsal=b.kd_bangsal where ki.stts_pulang='-' and (b.nm_bangsal like '%ICU%' or b.nm_bangsal like '%HCU%' or b.nm_bangsal like '%Intensif%') and ki.no_rawat not in (select no_rawat from icu_daily_checklist where tgl_checklist='" + today + "')");
            if (!"0".equals(blmChecklist)) html.append("<tr><td>&#9888; <b>").append(blmChecklist).append(" pasien</b> belum dilakukan Daily Checklist hari ini</td></tr>");
            // SBAR belum
            String blmSBAR = Sequel.cariIsi("select count(*) from kamar_inap ki inner join kamar k on ki.kd_kamar=k.kd_kamar inner join bangsal b on k.kd_bangsal=b.kd_bangsal where ki.stts_pulang='-' and (b.nm_bangsal like '%ICU%' or b.nm_bangsal like '%HCU%' or b.nm_bangsal like '%Intensif%') and ki.no_rawat not in (select no_rawat from icu_handover_sbar where tgl_handover='" + today + "')");
            if (!"0".equals(blmSBAR)) html.append("<tr><td>&#9888; <b>").append(blmSBAR).append(" pasien</b> belum ada Handover SBAR hari ini</td></tr>");
            // Observasi belum
            String blmObs = Sequel.cariIsi("select count(*) from kamar_inap ki inner join kamar k on ki.kd_kamar=k.kd_kamar inner join bangsal b on k.kd_bangsal=b.kd_bangsal where ki.stts_pulang='-' and (b.nm_bangsal like '%ICU%' or b.nm_bangsal like '%HCU%' or b.nm_bangsal like '%Intensif%') and ki.no_rawat not in (select no_rawat from observasi_icu_hemodinamik where tgl_observasi='" + today + "')");
            if (!"0".equals(blmObs)) html.append("<tr><td>&#9888; <b>").append(blmObs).append(" pasien</b> belum ada observasi hemodinamik hari ini</td></tr>");
            html.append("</table>");

            html.append("<p style='font-size:8px;color:#888;text-align:right;margin-top:10px'>*Merah=nilai abnormal | Oranye=rawat >7hari | Hijau=bed kosong</p>");
            html.append("</body></html>");

            javax.swing.JEditorPane editorPane = new javax.swing.JEditorPane();
            editorPane.setContentType("text/html");
            editorPane.setText(html.toString());
            editorPane.setEditable(false);
            editorPane.setCaretPosition(0);
            javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(editorPane);
            dlg.setContentPane(scrollPane);
            dlg.setVisible(true);
        } catch (Exception ex) {
            System.out.println("Error Dashboard ICU: " + ex);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    // ========== HELPER ==========

    private void addSingkatan(javax.swing.JPanel panel, String text, int x, int y, java.awt.Font font) {
        javax.swing.JLabel lbl = new javax.swing.JLabel(text);
        lbl.setFont(font);
        lbl.setBounds(x, y, text.length() * 6, 13);
        panel.add(lbl);
    }

    // ========== HEADER PETUGAS ==========

    private void btnPetugasHeaderActionPerformed() {
        akses.setform("ICUObservationForm");
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }

    // ========== HEADER SCORE METHODS ==========

    private void openScoreForm(String type) {
        if (TNoRw.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        switch (type) {
            case "PELOD2": {
                RMPelod2Score form = new RMPelod2Score(null, false);
                form.isCek();
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), TNoRM.getText(), TPasien.getText());
                form.tampil();
                break;
            }
            case "RAMSAY": {
                RMRamsayScore form = new RMRamsayScore(null, false);
                form.isCek();
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), TNoRM.getText(), TPasien.getText());
                form.tampil();
                break;
            }
            case "RASS": {
                RMRASSScore form = new RMRASSScore(null, false);
                form.isCek();
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), TNoRM.getText(), TPasien.getText());
                form.tampil();
                break;
            }
            case "SOFA": {
                RMSOFAScore form = new RMSOFAScore(null, false);
                form.isCek();
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), TNoRM.getText(), TPasien.getText());
                form.tampil();
                break;
            }
            case "APACHE": {
                RMAPACHEScore form = new RMAPACHEScore(null, false);
                form.isCek();
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), TNoRM.getText(), TPasien.getText());
                form.tampil();
                break;
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void loadHeaderScores() {
        try {
            String today = Valid.SetTgl(Tanggal.getSelectedItem() + "");

            // Berat badan & Tinggi badan dari pemeriksaan_ranap hari ini
            ps = koneksi.prepareStatement(
                    "SELECT berat, tinggi FROM pemeriksaan_ranap "
                    + "WHERE no_rawat=? AND tgl_perawatan=? "
                    + "ORDER BY tgl_perawatan DESC LIMIT 1");
            ps.setString(1, TNoRw.getText());
            ps.setString(2, today);
            rs = ps.executeQuery();
            if (rs.next()) {
                TBeratBadanHeader.setText(rs.getString("berat"));
                TTinggiBadanHeader.setText(rs.getString("tinggi"));
            } else {
                TBeratBadanHeader.setText("");
                TTinggiBadanHeader.setText("");
            }
            rs.close();
            ps.close();

            // PELOD 2 Score - latest
            TPELOD2Header.setText(Sequel.cariIsi("SELECT total_skor FROM pelod2_score WHERE no_rawat=? ORDER BY tanggal DESC LIMIT 1", TNoRw.getText()));

            // RAMSAY Score - latest
            TRAMSAYHeader.setText(Sequel.cariIsi("SELECT level FROM ramsay_score WHERE no_rawat=? ORDER BY tanggal DESC LIMIT 1", TNoRw.getText()));

            // RASS Score - latest
            TRASSHeader.setText(Sequel.cariIsi("SELECT score FROM rass_score WHERE no_rawat=? ORDER BY tanggal DESC LIMIT 1", TNoRw.getText()));

            // SOFA Score - latest
            TSOFAHeader.setText(Sequel.cariIsi("SELECT total_sofa FROM sofa_score WHERE no_rawat=? ORDER BY tanggal DESC LIMIT 1", TNoRw.getText()));

            // APACHE Score - latest
            TAPACHEHeader.setText(Sequel.cariIsi("SELECT total_apache FROM apache_score WHERE no_rawat=? ORDER BY tanggal DESC LIMIT 1", TNoRw.getText()));

            // ===== Header baru: Tgl Masuk ICU, Hari Rawat, Hari Operasi, DPJP, Diagnosa =====
            // Tgl masuk: prioritas kamar dgn bangsal mengandung ICU/HCU/Intensif; fallback ke
            // kamar_inap pertama. Hari Rawat = (today - tgl_masuk_ICU) + 1.
            String nrw = TNoRw.getText();
            ps = koneksi.prepareStatement(
                "SELECT ki.tgl_masuk, DATEDIFF(?, ki.tgl_masuk) + 1 AS hari_rawat "
                + "FROM kamar_inap ki "
                + "INNER JOIN kamar k ON ki.kd_kamar = k.kd_kamar "
                + "INNER JOIN bangsal b ON k.kd_bangsal = b.kd_bangsal "
                + "WHERE ki.no_rawat = ? AND (b.nm_bangsal LIKE '%ICU%' OR b.nm_bangsal LIKE '%HCU%' OR b.nm_bangsal LIKE '%Intensif%') "
                + "ORDER BY ki.tgl_masuk ASC LIMIT 1");
            ps.setString(1, today);
            ps.setString(2, nrw);
            rs = ps.executeQuery();
            if (rs.next()) {
                TTglMasukIcuHeader.setText(rs.getString(1));
                THariRawatHeader.setText(rs.getString(2));
            } else {
                // Fallback: pakai kamar_inap pertama apapun bangsalnya
                rs.close(); ps.close();
                ps = koneksi.prepareStatement(
                    "SELECT tgl_masuk, DATEDIFF(?, tgl_masuk) + 1 FROM kamar_inap "
                    + "WHERE no_rawat=? ORDER BY tgl_masuk ASC LIMIT 1");
                ps.setString(1, today); ps.setString(2, nrw);
                rs = ps.executeQuery();
                if (rs.next()) {
                    TTglMasukIcuHeader.setText(rs.getString(1));
                    THariRawatHeader.setText(rs.getString(2));
                } else {
                    TTglMasukIcuHeader.setText(""); THariRawatHeader.setText("");
                }
            }
            rs.close(); ps.close();

            // Hari Operasi = (today - tgl_operasi_pertama) + 1; "-" kalau tidak ada operasi
            ps = koneksi.prepareStatement(
                "SELECT DATEDIFF(?, DATE(MIN(tgl_operasi))) + 1 FROM operasi WHERE no_rawat=?");
            ps.setString(1, today); ps.setString(2, nrw);
            rs = ps.executeQuery();
            if (rs.next() && rs.getString(1) != null) {
                THariOperasiHeader.setText(rs.getString(1));
            } else {
                THariOperasiHeader.setText("-");
            }
            rs.close(); ps.close();

            // DPJP dari dpjp_ranap (bisa lebih dari 1 dokter — pakai GROUP_CONCAT)
            TDpjpHeader.setText(Sequel.cariIsi(
                "SELECT GROUP_CONCAT(d.nm_dokter SEPARATOR ', ') "
                + "FROM dpjp_ranap dr INNER JOIN dokter d ON dr.kd_dokter = d.kd_dokter "
                + "WHERE dr.no_rawat=?", nrw));

            // Diagnosa dari kamar_inap (latest entry)
            TDiagnosaHeader.setText(Sequel.cariIsi(
                "SELECT diagnosa_awal FROM kamar_inap WHERE no_rawat=? "
                + "ORDER BY tgl_masuk DESC LIMIT 1", nrw));

        } catch (Exception e) {
            System.out.println("Notifikasi loadHeaderScores : " + e);
        }
    }

    // Additional methods required by the caller script
    public void isCek() {
        BtnSimpan.setEnabled(akses.getcatatan_observasi_ranap());
        BtnHapus.setEnabled(akses.getcatatan_observasi_ranap());
        BtnEdit.setEnabled(akses.getcatatan_observasi_ranap());
        BtnPrint.setEnabled(akses.getcatatan_observasi_ranap());

        if (akses.getjml2() >= 1) {
            NIPHeader.setEditable(false);
            btnPetugasHeader.setEnabled(false);
            NIPHeader.setText(akses.getkode());
            NamaPetugasHeader.setText(petugas.tampil3(NIPHeader.getText()));
            if (NamaPetugasHeader.getText().equals("")) {
                NIPHeader.setText("");
                JOptionPane.showMessageDialog(null, "User login bukan petugas...!!");
            }
        }

        if (TANGGALMUNDUR.equals("no")) {
            if (!akses.getkode().equals("Admin Utama")) {
                Tanggal.setEditable(false);
                Tanggal.setEnabled(false);
            }
        }
    }

    public void setNoRm(String norawat, String norm, String nmpasien) {
        TNoRw.setText(norawat);
        TCari.setText(norawat);
        isRawat();

        if (!norm.equals("")) {
            TNoRM.setText(norm);
        }
        if (!nmpasien.equals("")) {
            TPasien.setText(nmpasien);
        }

        DTPCari2.setDate(new Date());
        loadHeaderScores();
    }

    public void tampil() {
        TabICU.setSelectedIndex(0);
        tampilObservasi();
        tampilVentilator();
        tampilIntakeOutput();
        tampilMedikasi();
        tampilCatatanKeperawatan();
        // Tab baru (Resume, MIT, Fasthug, Alat, Konsultasi, Rencana, Catatan Khusus, Nutri)
        // di-load lazy saat user klik tabnya (lewat BtnCari) — ngga perlu di-load semua di awal.
        // tampilBundleCare(), tampilPemeriksaanPenunjang(), tampilGambar(), tampilCairanNutrisi(),
        // tampilTindakan(), tampilChecklist() di-skip karena tab terkait dihapus / table model NULL.
        tampilCatatan();
        tampilSBAR();
        tampilPenangananDr();
        tampilPenangananPr();
        tampilPenangananDrPr();
    }

    public void setTampil() {
        TabICU.setSelectedIndex(0);
        tampilObservasi();
    }

    public void setData(String norawat, String norm, String nmpasien, String tgllahir, String jk, String umur) {
        TNoRw.setText(norawat);
        TNoRM.setText(norm);
        TPasien.setText(nmpasien);
        TglLahir.setText(tgllahir);
        Umur.setText(umur);

        DTPCari1.setDate(new Date());
        DTPCari2.setDate(new Date());

        loadHeaderScores();
        tampil();
    }

    /**
     * Main method for testing
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            LembarObservasiIcuIccuHcu dialog = new LembarObservasiIcuIccuHcu(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - Complete list with ventilator components
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInputObservasi;
    private widget.CekBox ChkInputVentilator;
    private widget.CekBox ChkInputIntake;
    private widget.CekBox ChkInputMedikasi;
    private widget.CekBox ChkInputCatatan;
    private widget.CekBox ChkJln;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.ComboBox Detik;
    private widget.PanelBiasa FormInput;
    private widget.ComboBox Jam;
    private widget.Label LCount;
    private javax.swing.JMenuItem MnPrintObservasi;
    private widget.ComboBox Menit;
    private widget.TextBox NIPObservasi;
    private widget.TextBox NIPVentilator;
    private widget.TextBox NIPIntake;
    private widget.TextBox NIPMedikasi;
    private widget.TextBox NIPCatatan;
    private widget.TextBox KdPegCatatan;
    private widget.TextBox NamaPegawaiCatatan;
    private widget.TextBox NamaPetugasObservasi;
    private widget.TextBox NamaPetugasVentilator;
    private widget.TextBox NamaPetugasIntake;
    private widget.TextBox NamaPetugasMedikasi;
    private widget.TextBox NamaPetugasCatatan;
    private javax.swing.JPanel PanelInputObservasi;
    private javax.swing.JPanel PanelInputVentilator;
    private javax.swing.JPanel PanelInputIntake;
    private javax.swing.JPanel PanelInputMedikasi;
    private javax.swing.JPanel PanelInputCatatan;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane ScrollVentilator;
    private widget.ScrollPane Scroll1;
    private widget.ScrollPane Scroll2;
    private widget.ScrollPane Scroll3;

    // Hemodynamic parameters text boxes
    private widget.TextBox THR;
    private widget.TextBox TTemperature;
    private widget.TextBox TRespirasi;
    private widget.TextBox TNIBP;
    private widget.TextBox TIBP;
    private widget.TextBox TCO;
    private widget.TextBox TCVP;
    private widget.TextBox TPAP;

    // Additional monitoring parameters
    private widget.TextBox TGambaranEKG;
    private widget.TextBox TGambaranVES;
    private widget.TextBox TSaturasi;
    private widget.TextBox TEtCO2;

    // GCS Components (3 separate textboxes)
    private widget.TextBox TGCS_E;
    private widget.TextBox TGCS_V;
    private widget.TextBox TGCS_M;
    private widget.TextBox TGCSTotal;
    private widget.Label jLabelGCS_E;
    private widget.Label jLabelGCS_V;
    private widget.Label jLabelGCS_M;
    private widget.Label jLabelGCSTotal;

    private widget.ComboBox cmbPupilKanan;
    private widget.ComboBox cmbPupilKiri;

    // Tahap 1 — Field tambahan tab Hemodinamik (BP split, Kesadaran, Refleks Pupil, Motorik AKA/AKI/BKA/BKI)
    private widget.TextBox TBPSys;
    private widget.TextBox TBPDys;
    private widget.TextBox TMAP;
    private widget.ComboBox cmbKesadaranObs;
    private widget.ComboBox cmbRefleksPupilKanan;
    private widget.ComboBox cmbRefleksPupilKiri;
    private widget.ComboBox cmbMotorikAKA;
    private widget.ComboBox cmbMotorikAKI;
    private widget.ComboBox cmbMotorikBKA;
    private widget.ComboBox cmbMotorikBKI;
    private widget.Label jLabelBPSys;
    private widget.Label jLabelBPDys;
    private widget.Label jLabelMAP;
    private widget.Label jLabelKesadaranObs;
    private widget.Label jLabelRefleksPupilKanan;
    private widget.Label jLabelRefleksPupilKiri;
    private widget.Label jLabelMotorik;
    private widget.Label jLabelMotorikAKA;
    private widget.Label jLabelMotorikAKI;
    private widget.Label jLabelMotorikBKA;
    private widget.Label jLabelMotorikBKI;

    // Tahap 2 — Tab Pemantauan Tambahan (Skala Sedasi, CRT, Lingkar Perut, Sekret)
    private widget.InternalFrame internalFrameMonitoringTambahan;
    private widget.ScrollPane ScrollMonitoringTambahan;
    private widget.Table tbMonitoringTambahan;
    private javax.swing.JPanel PanelInputMonitoringTambahan;
    private widget.CekBox ChkInputMonitoringTambahan;
    private widget.ScrollPane scrollPaneMonitoringTambahan;
    private widget.panelisi panelGlassMonitoringTambahan;
    private widget.Label jLabelSkalaSedasi;
    private widget.ComboBox cmbSkalaSedasi;
    private widget.Label jLabelNilaiSedasi;
    private widget.TextBox TNilaiSedasi;
    private widget.Label jLabelCRT;
    private widget.TextBox TCRT;
    private widget.Label jLabelLingkarPerut;
    private widget.TextBox TLingkarPerut;
    private widget.Label jLabelSekretWarna;
    private widget.ComboBox cmbSekretWarna;
    private widget.Label jLabelSekretKonsistensi;
    private widget.ComboBox cmbSekretKonsistensi;

    // Pain Scale Components
    private widget.Label jLabelSkalaNyeri;
    private widget.ComboBox cmbSkalaNyeri;
    private widget.Label jLabelNilaiNyeri;
    private widget.TextBox TNilaiNyeri;

    // Risk of Fall Component
    private widget.ComboBox cmbResikoJatuh;
    private widget.ComboBox cmbKategoriResikoJatuh;
    private widget.TextBox TBredenScore;
    private widget.Label jLabel18b;

    // Ventilator parameter textboxes
    private widget.TextBox TTipe;
    private widget.TextBox TRRSetting;
    private widget.TextBox TRRAktual;
    private widget.TextBox TIERatio;
    private widget.TextBox TMVSetting;
    private widget.TextBox TMVAktual;
    private widget.TextBox TPressureControl;
    private widget.TextBox TPressureSupport;
    private widget.TextBox TPEEP;
    private widget.TextBox TFiO2Flow;
    private widget.TextBox TPeak;
    private widget.TextBox TPlateauPressure;
    private widget.ComboBox cmbETTTC;
    private widget.TextBox TDiameter;
    private widget.TextBox TKedalaman;
    private widget.TextBox TTekananCuff;

    private widget.TextBox TCari;
    private widget.TextBox TCariPasien;
    private widget.TextArea TCatatanISI;
    private widget.TextBox TKodeDokter;
    private widget.TextBox TNamaDokter;
    private widget.TextBox TNamaObat;
    private widget.TextBox TNamaCairan;
    private javax.swing.JComboBox<String> cmbTipeCairan;
    private widget.TextBox TKekuatan;
    private widget.TextBox TBentukSediaan;
    private widget.TextBox TAturanPakai;
    private widget.TextBox TRutePemberian;
    private widget.TextBox TVerifikasiApoteker;
    // TJam07 dan TJam08 dihapus - menggunakan komponen jam dari form utama
    // Textbox jam dihapus - menggunakan komponen jam dari form utama
    private widget.Tanggal TanggalMulai;
    private widget.Tanggal TanggalStop;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;

    // Header Score fields
    private widget.TextBox TBeratBadanHeader;
    private widget.TextBox TTinggiBadanHeader;
    private widget.TextBox TPELOD2Header;
    private widget.TextBox TRAMSAYHeader;
    private widget.TextBox TRASSHeader;
    private widget.TextBox TSOFAHeader;
    private widget.TextBox TAPACHEHeader;
    private widget.Button BtnPelod2;
    private widget.Button BtnRamsay;
    private widget.Button BtnRass;
    private widget.Button BtnSofa;
    private widget.Button BtnApache;
    private widget.Label jLabelBB;
    private widget.Label jLabelTB;
    private widget.Label jLabelPelod2;
    private widget.Label jLabelRamsay;
    private widget.Label jLabelRass;
    private widget.Label jLabelSofa;
    private widget.Label jLabelApache;
    private widget.TextBox NIPHeader;
    private widget.TextBox NamaPetugasHeader;
    private widget.Button btnPetugasHeader;
    private widget.Label jLabelPetugas;
    // Header row 3 & 4 — ICU admission, lama rawat/operasi, DPJP, Diagnosa
    private widget.TextBox TTglMasukIcuHeader;
    private widget.TextBox THariRawatHeader;
    private widget.TextBox THariOperasiHeader;
    private widget.TextBox TDpjpHeader;
    private widget.TextBox TDiagnosaHeader;

    // New Intake parameters
    private widget.TextBox TTransfusiICA;
    private widget.TextBox TTransfusiICM;
    private widget.TextBox TTransfusiIICA;
    private widget.TextBox TTransfusiIICM;
    private widget.TextBox TTransfusiIIICA;
    private widget.TextBox TTransfusiIIICM;
    private widget.TextBox TTransfusiIIICACM;
    private widget.TextBox TJumlah1JamKumulatif;
    private widget.TextBox TJumlah1JamKumulatifCA;
    private widget.TextBox TJumlah1JamKumulatifCM;
    private widget.TextBox TMakanOral;
    private widget.TextBox TMakanOralCA;
    private widget.TextBox TMakanOralCM;
    private widget.TextBox TEnteral;
    private widget.TextBox TMakanEnteralCA;
    private widget.TextBox TMakanEnteralCM;
    private widget.TextBox TJumlahMakanKumulatif;
    private widget.TextBox TJumlahMakanKumulatifCA;
    private widget.TextBox TJumlahMakanKumulatifCM;
    private widget.TextBox TParenteral;
    private widget.TextBox TParenteralKumulatif;
    private widget.TextBox TKumulatif;

    // New Output parameters
    private widget.TextBox TUrine;
    private widget.TextBox TNGT_OGT;
    private widget.TextBox TBAB;
    private widget.TextBox TDrainStoma1;
    private widget.TextBox TDrainStoma2;
    private widget.TextBox TDrainStoma3;
    private widget.TextBox TMuntah;
    private widget.TextBox TIWAL;
    private widget.TextBox TOutputKumulatif;
    private widget.TextBox TKeseimbanganCairan;
    private javax.swing.JTabbedPane TabICU;
    private widget.Tanggal Tanggal;
    private widget.TextBox TglLahir;
    private widget.TextBox Umur;
    private widget.Button btnPasien;
    private widget.Button btnPetugasObservasi;
    private widget.Button btnPetugasVentilator;
    private widget.Button btnPetugasIntake;
    private widget.Button btnPetugasMedikasi;
    private widget.Button btnPetugasCatatan;
    private widget.ComboBox cmbProfesi;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame2;
    private widget.InternalFrame internalFrameVentilator;
    private widget.InternalFrame internalFrame3;
    private widget.InternalFrame internalFrame4;
    private widget.InternalFrame internalFrame5;
    private widget.Label jLabel1;
    private widget.Label jLabel2;
    private widget.Label jLabel3;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel17b;
    private widget.Label jLabel17c;
    private widget.Label jLabel17d;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel20;
    private widget.Label jLabel21;
    private widget.Label jLabel22;
    private widget.Label jLabel23;
    private widget.Label jLabel24;
    private widget.Label jLabel25;
    private widget.Label jLabel26;
    private widget.Label jLabel27;
    private widget.Label jLabel28;
    private widget.Label jLabel29;
    private widget.Label jLabel30Par;
    private widget.Label jLabel31Kum;
    private widget.Label jLabel32Out;
    private widget.Label jLabel33Urine;
    private widget.Label jLabel34NGT;
    private widget.Label jLabel35BAB;
    private widget.Label jLabel36Drain1;
    private widget.Label jLabel37Drain2;
    private widget.Label jLabel38Drain3;
    private widget.Label jLabel39Muntah;
    private widget.Label jLabel40IWAL;
    private widget.Label jLabel41OutKum;
    private widget.Label jLabel42Balance;
    private widget.Label jLabel30Int;
    private widget.Label jLabelIntakeHeader;
    private widget.Label jLabelTransfusi1CA;
    private widget.Label jLabelTransfusi1CM;
    private widget.Label jLabelTransfusi2CA;
    private widget.Label jLabelTransfusi2CM;
    private widget.Label jLabelTransfusi3CA;
    private widget.Label jLabelTransfusi3CM;
    private widget.Label jLabelJumlah1JamKumulatif;
    private widget.Label jLabelJumlah1JamKumulatifCA;
    private widget.Label jLabelJumlah1JamKumulatifCM;
    private widget.Label jLabelMakanOralCA;
    private widget.Label jLabelMakanOralCM;
    private widget.Label jLabelMakanEnteralCA;
    private widget.Label jLabelMakanEnteralCM;
    private widget.Label jLabelJumlahMakan1JamKumulatif;
    private widget.Label jLabelJumlahMakan1JamKumulatifCA;
    private widget.Label jLabelJumlahMakan1JamKumulatifCM;
    private widget.Label jLabelParenteral;
    private widget.Label jLabelJumlahParenteral1JamKumulatif;
    private widget.Label jLabelTotalIntakeKumulatif;
    private widget.Label jLabelOutputHeader;
    private widget.Label jLabelUrine;
    private widget.Label jLabelNGT_OGT;
    private widget.Label jLabelBAB;
    private widget.Label jLabelDrainStomaI;
    private widget.Label jLabelDrainStomaII;
    private widget.Label jLabelDrainStomaIII;
    private widget.Label jLabelMuntah;
    private widget.Label jLabelIWL;
    private widget.Label jLabelJumlahTotalOutputKumulatif;
    private widget.Label jLabelKeseimbanganCairan;
    private widget.Label jLabel30Med;
    private widget.Label jLabel31Med;
    private widget.Label jLabel32Med;
    private widget.Label jLabelKetMed;
    private widget.TextBox TKeterangan;
    private widget.Label jLabel33Med;
    private widget.Label jLabel34Med;
    private widget.Label jLabel35Med;
    private widget.Label jLabel36Med;
    private widget.Label jLabel37Med;
    private widget.Label jLabel38Med;
    private widget.Label jLabel39Med;
    private widget.Label jLabel40Med;
    private widget.Label jLabel30;
    private widget.Label jLabel31;
    private widget.Label jLabel32;
    private widget.Label jLabel33;
    private widget.Label jLabel34;
    private widget.Label jLabel35;
    private widget.Label jLabel40;
    private widget.Label jLabel41;
    private widget.Label jLabel42;
    private widget.Label jLabel50;
    private widget.Label jLabel51;
    private widget.Label jLabel52;
    private widget.Label jLabel53;
    private widget.Label jLabel60;
    private widget.Label jLabel61;
    private widget.Label jLabel62;
    private widget.Label jLabel63;
    private widget.Label jLabel64;

    // Ventilator labels
    private widget.Label jLabelVent1;
    private widget.Label jLabelVent2;
    private widget.Label jLabelVent3;
    private widget.Label jLabelVent4;
    private widget.Label jLabelVent5;
    private widget.Label jLabelVent6;
    private widget.Label jLabelVent7;
    private widget.Label jLabelVent8;
    private widget.Label jLabelVent9;
    private widget.Label jLabelVent10;
    private widget.Label jLabelVent11;
    private widget.Label jLabelVent12;
    private widget.Label jLabelVent13;
    private widget.Label jLabelVent14;
    private widget.Label jLabelVent15;
    private widget.Label jLabelVent16;
    private widget.Label jLabelVent17;
    private widget.Label jLabelVent18;
    private widget.Label jLabelVent19;
    private widget.Label jLabelVent20;

    // Tab Respirasi — field tambahan (Trigger, Exp Tidal Volume, Inspirasi Time, ETCO2)
    private widget.Label jLabelRespTrigger, jLabelRespExpTV, jLabelRespInspTime, jLabelRespETCO2;
    private widget.TextBox TRespTrigger, TRespExpTV, TRespInspTime, TRespETCO2;

    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JSeparator jSeparator1;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass10;
    private widget.panelisi panelGlassObservasi;
    private widget.panelisi panelGlassVentilator;
    private widget.panelisi panelGlassIntake;
    private widget.panelisi panelGlassMedikasi;
    private widget.panelisi panelGlassCatatan;
    private widget.ScrollPane scrollPane1;
    private widget.ScrollPane scrollPane2;
    private widget.ScrollPane scrollPaneObservasi;
    private widget.ScrollPane scrollPaneVentilator;
    private widget.ScrollPane scrollPaneIntake;
    private widget.ScrollPane scrollPaneMedikasi;
    private widget.ScrollPane scrollPaneCatatan;
    private widget.Table tbObservasi;
    private widget.Table tbVentilator;
    private widget.Table tbIntakeOutput;
    private widget.Table tbMedikasi;
    private widget.Table tbCatatan;
    private widget.Table tbCatatanKeperawatan;
    private widget.Table tbBundleVAP;
    private widget.Table tbBundleCVC;
    private widget.Table tbBundleISK;
    private widget.Table tbPemeriksaanPenunjang;

    // Catatan Keperawatan components
    private widget.InternalFrame internalFrameKeperawatan;
    private widget.ScrollPane ScrollKeperawatan;
    private javax.swing.JPanel PanelInputKeperawatan;
    private widget.CekBox ChkInputKeperawatan;
    private widget.ScrollPane scrollPaneCatatanKeperawatan;
    private widget.panelisi panelGlassCatatanKeperawatan;
    // 30 textbox tindakan keperawatan (form fisik RSCB).
    // Kolom DB: oral_hygiene, suctioning, cek_ett, ..., lain_lain_1..3.
    // Lihat tampilCatatanKeperawatan() / simpanCatatanKeperawatan() utk urutan.
    private widget.Label jLabelKep01; private widget.TextBox TKep01;
    private widget.Label jLabelKep02; private widget.TextBox TKep02;
    private widget.Label jLabelKep03; private widget.TextBox TKep03;
    private widget.Label jLabelKep04; private widget.TextBox TKep04;
    private widget.Label jLabelKep05; private widget.TextBox TKep05;
    private widget.Label jLabelKep06; private widget.TextBox TKep06;
    private widget.Label jLabelKep07; private widget.TextBox TKep07;
    private widget.Label jLabelKep08; private widget.TextBox TKep08;
    private widget.Label jLabelKep09; private widget.TextBox TKep09;
    private widget.Label jLabelKep10; private widget.TextBox TKep10;
    private widget.Label jLabelKep11; private widget.TextBox TKep11;
    private widget.Label jLabelKep12; private widget.TextBox TKep12;
    private widget.Label jLabelKep13; private widget.TextBox TKep13;
    private widget.Label jLabelKep14; private widget.TextBox TKep14;
    private widget.Label jLabelKep15; private widget.TextBox TKep15;
    private widget.Label jLabelKep16; private widget.TextBox TKep16;
    private widget.Label jLabelKep17; private widget.TextBox TKep17;
    private widget.Label jLabelKep18; private widget.TextBox TKep18;
    private widget.Label jLabelKep19; private widget.TextBox TKep19;
    private widget.Label jLabelKep20; private widget.TextBox TKep20;
    private widget.Label jLabelKep21; private widget.TextBox TKep21;
    private widget.Label jLabelKep22; private widget.TextBox TKep22;
    private widget.Label jLabelKep23; private widget.TextBox TKep23;
    private widget.Label jLabelKep24; private widget.TextBox TKep24;
    private widget.Label jLabelKep25; private widget.TextBox TKep25;
    private widget.Label jLabelKep26; private widget.TextBox TKep26;
    private widget.Label jLabelKep27; private widget.TextBox TKep27;
    private widget.Label jLabelKep28; private widget.TextBox TKep28;
    private widget.Label jLabelKep29; private widget.TextBox TKep29;
    private widget.Label jLabelKep30; private widget.TextBox TKep30;
    private widget.Label jLabelPetugasKeperawatan;
    private widget.TextBox NIPKeperawatan;
    private widget.TextBox NamaPetugasKeperawatan;
    private widget.Button btnPetugasKeperawatan;

    // Bundle Care components
    private widget.InternalFrame internalFrameBundleCare;
    private javax.swing.JTabbedPane TabBundleCare;

    // Bundle VAP components
    private widget.InternalFrame internalFrameVAP;
    private widget.ScrollPane ScrollVAP;
    private javax.swing.JPanel PanelInputVAP;
    private widget.CekBox ChkInputVAP;
    private widget.ScrollPane scrollPaneBundleVAP;
    private widget.panelisi panelGlassBundleVAP;
    private widget.Label jLabelHeadUpVAP;
    private widget.ComboBox cmbHeadUpPagiVAP;
    private widget.ComboBox cmbHeadUpSoreVAP;
    private widget.Label jLabelOralHygieneVAP;
    private widget.ComboBox cmbOralHygienePagiVAP;
    private widget.ComboBox cmbOralHygieneSoreVAP;
    private widget.Label jLabelEvaluasiSedasiVAP;
    private widget.ComboBox cmbEvaluasiSedasiVAP;
    private widget.Label jLabelProfilaksisDVTVAP;
    private widget.ComboBox cmbProfilaksisDVTVAP;
    private widget.Label jLabelProfilaksisPepticVAP;
    private widget.ComboBox cmbProfilaksisPepticVAP;
    private widget.Label jLabelPetugasVAP;
    private widget.TextBox NIPBundleVAP;
    private widget.TextBox NamaPetugasBundleVAP;
    private widget.Button btnPetugasBundleVAP;

    // Bundle CVC components
    private widget.InternalFrame internalFrameCVC;
    private widget.ScrollPane ScrollCVC;
    private javax.swing.JPanel PanelInputCVC;
    private widget.CekBox ChkInputCVC;
    private widget.ScrollPane scrollPaneBundleCVC;
    private widget.panelisi panelGlassBundleCVC;
    private widget.Label jLabelKebutuhanCVC;
    private widget.ComboBox cmbKebutuhanCVC;
    private widget.Label jLabelMencuciTanganCVC;
    private widget.ComboBox cmbMencuciTanganCVC;
    private widget.Label jLabelTempatInserseCVC;
    private widget.Label jLabelSuhuCVC;
    private widget.ComboBox cmbSuhuCVC;
    private widget.Label jLabelNyeriCVC;
    private widget.ComboBox cmbNyeriCVC;
    private widget.Label jLabelKemerahanCVC;
    private widget.ComboBox cmbKemerahanCVC;
    private widget.Label jLabelBengkakCVC;
    private widget.ComboBox cmbBengkakCVC;
    private widget.Label jLabelPusCVC;
    private widget.ComboBox cmbPusCVC;
    private widget.Label jLabelPenggantianMonitoringCVC;
    private widget.ComboBox cmbPenggantianMonitoringCVC;
    private widget.Label jLabelBalutanInsersiCVC;
    private widget.ComboBox cmbBalutanInsersiCVC;
    private widget.Label jLabelPenggantianBerkala;
    private widget.Label jLabelInfusSetCVC;
    private widget.ComboBox cmbInfusSetCVC;
    private widget.Label jLabelTreeWayCVC;
    private widget.ComboBox cmbTreeWayCVC;
    private widget.Label jLabelValveKonektorCVC;
    private widget.ComboBox cmbValveKonektorCVC;
    private widget.Label jLabelPetugasCVC;
    private widget.TextBox NIPBundleCVC;
    private widget.TextBox NamaPetugasBundleCVC;
    private widget.Button btnPetugasBundleCVC;

    // Bundle ISK components (replacing Bundle General)
    private widget.InternalFrame internalFrameISK;
    private widget.ScrollPane ScrollISK;
    private javax.swing.JPanel PanelInputISK;
    private widget.CekBox ChkInputISK;
    private widget.ScrollPane scrollPaneBundleISK;
    private widget.panelisi panelGlassBundleISK;
    private widget.Label jLabelPemasanganSesuaiISK;
    private widget.ComboBox cmbPemasanganSesuaiISK;
    private widget.Label jLabelTeknikAseptikISK;
    private widget.ComboBox cmbTeknikAseptikISK;
    private widget.Label jLabelKateterDibutuhkanISK;
    private widget.ComboBox cmbKateterDibutuhkanISK;
    private widget.Label jLabelPosisiUrinBagISK;
    private widget.ComboBox cmbPosisiUrinBagISK;
    private widget.Label jLabelHandHygieneISK;
    private widget.ComboBox cmbHandHygieneISK;
    private widget.Label jLabelMeatalCareISK;
    private widget.ComboBox cmbMeatalCareISK;
    private widget.Label jLabelFiksasiKateterISK;
    private widget.ComboBox cmbFiksasiKateterISK;
    private widget.Label jLabelKosongkanUrinBagISK;
    private widget.ComboBox cmbKosongkanUrinBagISK;
    private widget.Label jLabelClosedSystemISK;
    private widget.ComboBox cmbClosedSystemISK;
    private widget.Label jLabelPetugasISK;
    private widget.TextBox NIPBundleISK;
    private widget.TextBox NamaPetugasBundleISK;
    private widget.Button btnPetugasBundleISK;

    // Pemeriksaan Penunjang components
    private widget.InternalFrame internalFramePemeriksaanPenunjang;
    private widget.ScrollPane ScrollPemeriksaanPenunjang;
    private javax.swing.JPanel PanelInputPemeriksaanPenunjang;
    private widget.CekBox ChkInputPemeriksaanPenunjang;
    private widget.ScrollPane scrollPanePemeriksaanPenunjang;
    private widget.panelisi panelGlassPemeriksaanPenunjang;
    private widget.Label jLabelNamaPemeriksaan;
    private widget.TextBox TNamaPemeriksaan;
    private widget.Label jLabelHasilPemeriksaan;
    private javax.swing.JScrollPane scrollPaneHasilPemeriksaan;
    private javax.swing.JTextArea THasilPemeriksaan;
    private widget.Label jLabelPetugasPemeriksaan;
    private widget.TextBox NIPPemeriksaanPenunjang;
    private widget.TextBox NamaPetugasPemeriksaanPenunjang;
    private widget.Button btnPetugasPemeriksaanPenunjang;

    // Tab Kebutuhan Nutrisi dan Cairan components
    private widget.InternalFrame internalFrameGambar;
    private widget.ScrollPane ScrollGambar;
    private widget.Table tbGambar;
    private javax.swing.JPanel PanelInputGambar;
    private widget.CekBox ChkInputGambar;
    private widget.ScrollPane scrollPaneGambar;
    private widget.panelisi panelGlassGambar;

    // Nutrition and fluid calculation fields
    private widget.TextBox TBB;                    // Berat Badan
    private widget.TextBox TTB;                    // Tinggi Badan  
    private widget.TextBox TBMI;                   // BMI (calculated)
    private widget.TextBox TKebutuhanKkal;         // Kebutuhan Kkal/KgBB
    private widget.TextBox TKebutuhanKkal24;       // Total Kkal/24 jam (calculated)
    private widget.TextBox TKebutuhanDasar;        // Kebutuhan dasar cairan
    private widget.TextBox TPersentasePemberian;   // Persentase pemberian
    private widget.TextBox TPemberianKkal;         // Pemberian Kkal (calculated)
    private widget.TextBox TIWL;                   // IWL (calculated)
    private widget.TextBox TObatObatan;            // Obat-obatan cairan
    private widget.TextBox TCairanTotal;           // Cairan total (calculated)
    private widget.TextBox TProtein;               // Protein gr/KgBB/hari
    private widget.TextBox TProteinTotal;          // Protein total gram (calculated)
    private widget.TextBox TKaloriNonProtein;      // Kalori non protein (calculated)
    private widget.TextBox TRasioKH;               // Rasio KH persen
    private widget.TextBox TRasioLemak;            // Rasio Lemak persen
    private widget.TextBox TRasioTotal;            // Rasio total (calculated)
    private widget.TextBox TRasioKkal;             // Rasio Kkal (calculated)
    private widget.TextBox TBalance;               // Balance cairan

    // Staff fields (reused from previous implementation)
    private widget.Label jLabelPetugasGambar;
    private widget.TextBox NIPGambar;
    private widget.TextBox NamaPetugasGambar;
    private widget.Button btnPetugasGambar;

    // Tab Cairan dan Nutrisi components
    private widget.InternalFrame internalFrameCairanNutrisi;
    private widget.ScrollPane ScrollCairanNutrisi;
    private widget.Table tbCairanNutrisi;
    private javax.swing.JPanel PanelInputCairanNutrisi;
    private widget.CekBox ChkInputCairanNutrisi;
    private widget.ScrollPane scrollPaneCairanNutrisi;
    private widget.panelisi panelGlassCairanNutrisi;

    // Masalah checkboxes
    private widget.CekBox ChkSSP;
    private widget.CekBox ChkJantung;
    private widget.CekBox ChkRespirasi;
    private widget.CekBox ChkRenal;
    private widget.CekBox ChkSaluranPencernaan;
    private widget.CekBox ChkHepar;
    private widget.CekBox ChkMetabolisme;

    // Rate Nutrisi checkboxes
    private widget.CekBox ChkOral;
    private widget.CekBox ChkEnteral;
    private widget.CekBox ChkParenteral;
    private widget.CekBox ChkKombinasi;

    // Enteral fields
    private widget.TextBox TJenisEnteral;
    private widget.TextBox TCairanEnteral;
    private widget.TextBox TKaloriEnteral;
    private widget.TextBox TKHEnteral;
    private widget.TextBox TLemakEnteral;
    private widget.TextBox TProteinEnteral;

    // Parenteral fields
    private widget.TextBox TJenisParenteral;
    private widget.TextBox TCairanParenteral;
    private widget.TextBox TKaloriParenteral;
    private widget.TextBox TKHParenteral;
    private widget.TextBox TLemakParenteral;
    private widget.TextBox TProteinParenteral;

    // Total fields (calculated)
    private widget.TextBox TTotalCairan;
    private widget.TextBox TTotalKalori;
    private widget.TextBox TTotalKH;
    private widget.TextBox TTotalLemak;
    private widget.TextBox TTotalProtein;

    // Staff fields
    private widget.Label jLabelPetugasCairanNutrisi;
    private widget.TextBox NIPCairanNutrisi;
    private widget.TextBox NamaPetugasCairanNutrisi;
    private widget.Button btnPetugasCairanNutrisi;

    // Tab Tindakan components
    private widget.InternalFrame internalFrameTindakan;
    private widget.ScrollPane ScrollTindakan;
    private widget.Table tbTindakan;
    private javax.swing.JPanel PanelInputTindakan;
    private widget.CekBox ChkInputTindakan;
    private widget.ScrollPane scrollPaneTindakan;
    private widget.panelisi panelGlassTindakan;

    // Tindakan checkboxes
    private widget.CekBox ChkEndotrachealTube;
    private widget.CekBox ChkTracheostomyTube;
    private widget.CekBox ChkTubingVentilator;
    private widget.CekBox ChkNasogastricTube;
    private widget.CekBox ChkCentralVenousCatheter;
    private widget.CekBox ChkPICC;
    private widget.CekBox ChkCloseSuction;
    private widget.CekBox ChkArteriLineCatheter;
    private widget.CekBox ChkDowerCatheter;
    private widget.CekBox ChkInfusPerifer;

    // Tindakan fields
    private widget.TextBox TPemasanganKe;
    private widget.TextBox THariKe;
    private widget.TextBox TKeteranganTindakan;

    // Staff fields
    private widget.Label jLabelPetugasTindakan;
    private widget.TextBox NIPTindakan;
    private widget.TextBox NamaPetugasTindakan;
    private widget.Button btnPetugasTindakan;

    // Catatan Terintegrasi components (pemeriksaan_ranap fields - only new ones)
    private widget.TextBox TSuhuTubuh;
    private widget.TextBox TTensi;
    private widget.TextBox TNadi;
    // TRespirasi already declared in hemodynamic section
    private widget.TextBox TTinggi;
    private widget.TextBox TBerat;
    private widget.TextBox TSpO2;
    // TGCS already declared in hemodynamic section
    private widget.ComboBox CmbKesadaran;
    private widget.TextBox TAlergi;
    private javax.swing.JScrollPane scrollPaneKeluhan;
    private javax.swing.JTextArea TKeluhan;
    private javax.swing.JScrollPane scrollPanePemeriksaanRanap;
    private javax.swing.JTextArea TPemeriksaanRanap;
    private javax.swing.JScrollPane scrollPanePenilaian;
    private javax.swing.JTextArea TPenilaian;
    private javax.swing.JScrollPane scrollPaneRTL;
    private javax.swing.JTextArea TRTL;
    private javax.swing.JScrollPane scrollPaneInstruksi;
    private javax.swing.JTextArea TInstruksi;
    private javax.swing.JScrollPane scrollPaneEvaluasi;
    private javax.swing.JTextArea TEvaluasi;

    // End of variables declaration
    // New tab methods
    private void tampilCatatanKeperawatan() {
        Valid.tabelKosong(tabModeCatatanKeperawatan);
        try {
            ps = koneksi.prepareStatement(
                "select ck.no_rawat,ck.tgl_catat,ck.jam_catat,"
                + "ck.oral_hygiene,ck.suctioning,ck.cek_ett,ck.kempes_ett,ck.rawat_ett,"
                + "ck.ausk_napas,ck.inhalasi,ck.batuk_efektif,ck.minum_makan,ck.cek_ngt,"
                + "ck.alat_invasif,ck.cvp,ck.transfusi,ck.sampel_darah,ck.luka_dekubitus,"
                + "ck.luka_lain,ck.fisio_dada,ck.memandikan,ck.bab_bak,ck.kateter_urine,"
                + "ck.positioning,ck.mika_miki,ck.rom_aktif_pasif,ck.tepid_sponge,ck.pemeriksaan_ekg,"
                + "ck.edukasi,ck.kolaborasi,ck.lain_lain_1,ck.lain_lain_2,ck.lain_lain_3,"
                + "ck.nip,IFNULL(pet.nama,ck.nip) "
                + "from observasi_icu_catatan_keperawatan ck "
                + "left join petugas pet on ck.nip=pet.nip "
                + "where ck.no_rawat=? order by ck.tgl_catat desc, ck.jam_catat desc");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[35];
                for (int i = 1; i <= 35; i++) row[i-1] = rs.getString(i);
                tabModeCatatanKeperawatan.addRow(row);
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilKeperawatan: " + e); }
        LCount.setText("" + tabModeCatatanKeperawatan.getRowCount());
    }

    private void tampilBundleCare() {
        // VAP
        Valid.tabelKosong(tabModeBundleVAP);
        try {
            ps = koneksi.prepareStatement(
                "select v.no_rawat,v.tgl_bundle,v.jam_bundle,v.head_of_bed_elevation,v.oral_care,"
                + "v.peptic_ulcer_prophylaxis,v.dvt_prophylaxis,v.sedation_vacation,v.weaning_assessment,"
                + "v.nip,IFNULL(pet.nama,v.nip) from observasi_icu_bundle_vap v "
                + "left join petugas pet on v.nip=pet.nip where v.no_rawat=? order by v.tgl_bundle desc,v.jam_bundle desc");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeBundleVAP.addRow(new Object[]{rs.getString(1),rs.getString(2),rs.getString(3),
                    rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),
                    rs.getString(9),rs.getString(10),rs.getString(11)});
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilBundleVAP: " + e); }

        // CVC
        Valid.tabelKosong(tabModeBundleCVC);
        try {
            ps = koneksi.prepareStatement(
                "select c.no_rawat,c.tgl_bundle,c.jam_bundle,c.kebutuhan_cvc,c.mencuci_tangan,"
                + "c.suhu_lebih_38,c.nyeri,c.kemerahan,c.bengkak,c.pus,c.penggantian_monitoring,"
                + "c.balutan_insersi,c.infus_set,c.tree_way,c.valve_konektor,"
                + "c.nip,IFNULL(pet.nama,c.nip) from observasi_icu_bundle_cvc c "
                + "left join petugas pet on c.nip=pet.nip where c.no_rawat=? order by c.tgl_bundle desc,c.jam_bundle desc");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeBundleCVC.addRow(new Object[]{rs.getString(1),rs.getString(2),rs.getString(3),
                    rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),
                    rs.getString(9),rs.getString(10),rs.getString(11),rs.getString(12),rs.getString(13),
                    rs.getString(14),rs.getString(15),rs.getString(16),rs.getString(17)});
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilBundleCVC: " + e); }

        // ISK
        Valid.tabelKosong(tabModeBundleISK);
        try {
            ps = koneksi.prepareStatement(
                "select i.no_rawat,i.tgl_bundle,i.jam_bundle,i.pemasangan_sesuai_indikasi,i.teknik_aseptik,"
                + "i.kateter_dibutuhkan,i.posisi_urin_bag,i.hand_hygiene,i.meatal_care,i.fiksasi_kateter,"
                + "i.kosongkan_urin_bag,i.closed_system,"
                + "i.nip,IFNULL(pet.nama,i.nip) from observasi_icu_bundle_isk i "
                + "left join petugas pet on i.nip=pet.nip where i.no_rawat=? order by i.tgl_bundle desc,i.jam_bundle desc");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeBundleISK.addRow(new Object[]{rs.getString(1),rs.getString(2),rs.getString(3),
                    rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),
                    rs.getString(9),rs.getString(10),rs.getString(11),rs.getString(12),
                    rs.getString(13),rs.getString(14)});
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilBundleISK: " + e); }
    }

    private void simpanCatatanKeperawatan() {
        if (NIPHeader.getText().trim().equals("")) {
            Valid.textKosong(NIPHeader, "Petugas");
        } else {
            // 34 kolom = 3 PK/waktu + 30 tindakan + 1 nip
            if (Sequel.menyimpantf("observasi_icu_catatan_keperawatan",
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 34, new String[]{
                TNoRw.getText(),
                Valid.SetTgl(Tanggal.getSelectedItem() + ""),
                Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                TKep01.getText(), TKep02.getText(), TKep03.getText(), TKep04.getText(), TKep05.getText(),
                TKep06.getText(), TKep07.getText(), TKep08.getText(), TKep09.getText(), TKep10.getText(),
                TKep11.getText(), TKep12.getText(), TKep13.getText(), TKep14.getText(), TKep15.getText(),
                TKep16.getText(), TKep17.getText(), TKep18.getText(), TKep19.getText(), TKep20.getText(),
                TKep21.getText(), TKep22.getText(), TKep23.getText(), TKep24.getText(), TKep25.getText(),
                TKep26.getText(), TKep27.getText(), TKep28.getText(), TKep29.getText(), TKep30.getText(),
                NIPHeader.getText()
            })) {
                tampilCatatanKeperawatan();
                emptTeks();
            }
        }
    }

    private void simpanBundleCare() {
        if (NIPHeader.getText().trim().equals("")) {
            Valid.textKosong(NIPHeader, "Petugas");
            return;
        }
        String tgl = Valid.SetTgl(Tanggal.getSelectedItem() + "");
        String jam = Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem();
        int subTab = TabBundleCare.getSelectedIndex();
        switch (subTab) {
            case 0: // VAP
                if (Sequel.menyimpantf("observasi_icu_bundle_vap",
                    "?,?,?,?,?,?,?,?,?,?", "Data", 10, new String[]{
                    TNoRw.getText(), tgl, jam,
                    cmbHeadUpPagiVAP.getSelectedItem().toString(),
                    cmbOralHygienePagiVAP.getSelectedItem().toString(),
                    cmbProfilaksisPepticVAP.getSelectedItem().toString(),
                    cmbProfilaksisDVTVAP.getSelectedItem().toString(),
                    cmbEvaluasiSedasiVAP.getSelectedItem().toString(),
                    "Tidak",
                    NIPHeader.getText()
                })) {
                    tampilBundleCare();
                    emptTeks();
                }
                break;
            case 1: // CVC
                if (Sequel.menyimpantf("observasi_icu_bundle_cvc",
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 16, new String[]{
                    TNoRw.getText(), tgl, jam,
                    cmbKebutuhanCVC.getSelectedItem().toString(),
                    cmbMencuciTanganCVC.getSelectedItem().toString(),
                    cmbSuhuCVC.getSelectedItem().toString(),
                    cmbNyeriCVC.getSelectedItem().toString(),
                    cmbKemerahanCVC.getSelectedItem().toString(),
                    cmbBengkakCVC.getSelectedItem().toString(),
                    cmbPusCVC.getSelectedItem().toString(),
                    cmbPenggantianMonitoringCVC.getSelectedItem().toString(),
                    cmbBalutanInsersiCVC.getSelectedItem().toString(),
                    cmbInfusSetCVC.getSelectedItem().toString(),
                    cmbTreeWayCVC.getSelectedItem().toString(),
                    cmbValveKonektorCVC.getSelectedItem().toString(),
                    NIPHeader.getText()
                })) {
                    tampilBundleCare();
                    emptTeks();
                }
                break;
            case 2: // ISK
                if (Sequel.menyimpantf("observasi_icu_bundle_isk",
                    "?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 13, new String[]{
                    TNoRw.getText(), tgl, jam,
                    cmbPemasanganSesuaiISK.getSelectedItem().toString(),
                    cmbTeknikAseptikISK.getSelectedItem().toString(),
                    cmbKateterDibutuhkanISK.getSelectedItem().toString(),
                    cmbPosisiUrinBagISK.getSelectedItem().toString(),
                    cmbHandHygieneISK.getSelectedItem().toString(),
                    cmbMeatalCareISK.getSelectedItem().toString(),
                    cmbFiksasiKateterISK.getSelectedItem().toString(),
                    cmbKosongkanUrinBagISK.getSelectedItem().toString(),
                    cmbClosedSystemISK.getSelectedItem().toString(),
                    NIPHeader.getText()
                })) {
                    tampilBundleCare();
                    emptTeks();
                }
                break;
        }
    }

    private void hapusCatatanKeperawatan() {
        if (tbCatatanKeperawatan.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from observasi_icu_catatan_keperawatan where no_rawat=? and tgl_catat=? and jam_catat=?", 3, new String[]{
                tbCatatanKeperawatan.getValueAt(tbCatatanKeperawatan.getSelectedRow(), 0).toString(),
                tbCatatanKeperawatan.getValueAt(tbCatatanKeperawatan.getSelectedRow(), 1).toString(),
                tbCatatanKeperawatan.getValueAt(tbCatatanKeperawatan.getSelectedRow(), 2).toString()
            })) {
                tampilCatatanKeperawatan();
                emptTeks();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus terlebih dahulu");
        }
    }

    private void hapusBundleCare() {
        int subTab = TabBundleCare.getSelectedIndex();
        switch (subTab) {
            case 0: // VAP
                if (tbBundleVAP.getSelectedRow() > -1) {
                    if (Sequel.queryu2tf("delete from observasi_icu_bundle_vap where no_rawat=? and tgl_bundle=? and jam_bundle=?", 3, new String[]{
                        tbBundleVAP.getValueAt(tbBundleVAP.getSelectedRow(), 0).toString(),
                        tbBundleVAP.getValueAt(tbBundleVAP.getSelectedRow(), 1).toString(),
                        tbBundleVAP.getValueAt(tbBundleVAP.getSelectedRow(), 2).toString()
                    })) { tampilBundleCare(); emptTeks(); }
                } else { JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus terlebih dahulu"); }
                break;
            case 1: // CVC
                if (tbBundleCVC.getSelectedRow() > -1) {
                    if (Sequel.queryu2tf("delete from observasi_icu_bundle_cvc where no_rawat=? and tgl_bundle=? and jam_bundle=?", 3, new String[]{
                        tbBundleCVC.getValueAt(tbBundleCVC.getSelectedRow(), 0).toString(),
                        tbBundleCVC.getValueAt(tbBundleCVC.getSelectedRow(), 1).toString(),
                        tbBundleCVC.getValueAt(tbBundleCVC.getSelectedRow(), 2).toString()
                    })) { tampilBundleCare(); emptTeks(); }
                } else { JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus terlebih dahulu"); }
                break;
            case 2: // ISK
                if (tbBundleISK.getSelectedRow() > -1) {
                    if (Sequel.queryu2tf("delete from observasi_icu_bundle_isk where no_rawat=? and tgl_bundle=? and jam_bundle=?", 3, new String[]{
                        tbBundleISK.getValueAt(tbBundleISK.getSelectedRow(), 0).toString(),
                        tbBundleISK.getValueAt(tbBundleISK.getSelectedRow(), 1).toString(),
                        tbBundleISK.getValueAt(tbBundleISK.getSelectedRow(), 2).toString()
                    })) { tampilBundleCare(); emptTeks(); }
                } else { JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus terlebih dahulu"); }
                break;
        }
    }

    private void editCatatanKeperawatan() {
        if (tbCatatanKeperawatan.getSelectedRow() > -1) {
            if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); } else {
                int r = tbCatatanKeperawatan.getSelectedRow();
                // 30 SET kolom + 1 nip + 3 WHERE = 34 parameter
                if (Sequel.mengedittf("observasi_icu_catatan_keperawatan", "no_rawat=? and tgl_catat=? and jam_catat=?",
                    "oral_hygiene=?,suctioning=?,cek_ett=?,kempes_ett=?,rawat_ett=?,"
                    + "ausk_napas=?,inhalasi=?,batuk_efektif=?,minum_makan=?,cek_ngt=?,"
                    + "alat_invasif=?,cvp=?,transfusi=?,sampel_darah=?,luka_dekubitus=?,"
                    + "luka_lain=?,fisio_dada=?,memandikan=?,bab_bak=?,kateter_urine=?,"
                    + "positioning=?,mika_miki=?,rom_aktif_pasif=?,tepid_sponge=?,pemeriksaan_ekg=?,"
                    + "edukasi=?,kolaborasi=?,lain_lain_1=?,lain_lain_2=?,lain_lain_3=?,nip=?", 34, new String[]{
                    TKep01.getText(), TKep02.getText(), TKep03.getText(), TKep04.getText(), TKep05.getText(),
                    TKep06.getText(), TKep07.getText(), TKep08.getText(), TKep09.getText(), TKep10.getText(),
                    TKep11.getText(), TKep12.getText(), TKep13.getText(), TKep14.getText(), TKep15.getText(),
                    TKep16.getText(), TKep17.getText(), TKep18.getText(), TKep19.getText(), TKep20.getText(),
                    TKep21.getText(), TKep22.getText(), TKep23.getText(), TKep24.getText(), TKep25.getText(),
                    TKep26.getText(), TKep27.getText(), TKep28.getText(), TKep29.getText(), TKep30.getText(),
                    NIPHeader.getText(),
                    tbCatatanKeperawatan.getValueAt(r, 0).toString(),
                    tbCatatanKeperawatan.getValueAt(r, 1).toString(),
                    tbCatatanKeperawatan.getValueAt(r, 2).toString()
                })) { tampilCatatanKeperawatan(); emptTeks(); }
            }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan diedit terlebih dahulu..!!"); }
    }

    private void editBundleCare() {
        JOptionPane.showMessageDialog(this, "Gunakan tombol Simpan untuk menyimpan perubahan Bundle Care");
    }

    private void tampilPemeriksaanPenunjang() {
        Valid.tabelKosong(tabModePemeriksaanPenunjang);
        try {
            ps = koneksi.prepareStatement(
                "select pp.no_rawat,pp.tgl_pemeriksaan,pp.jam_pemeriksaan,pp.nama_pemeriksaan,"
                + "pp.hasil,pp.nip,IFNULL(pet.nama,pp.nip) from observasi_icu_pemeriksaan_penunjang pp "
                + "left join petugas pet on pp.nip=pet.nip "
                + "where pp.no_rawat=? order by pp.tgl_pemeriksaan desc, pp.jam_pemeriksaan desc");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModePemeriksaanPenunjang.addRow(new Object[]{
                    rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),
                    rs.getString(5),rs.getString(6),rs.getString(7)});
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilPemPenunjang: " + e); }
        LCount.setText("" + tabModePemeriksaanPenunjang.getRowCount());
    }

    private void simpanPemeriksaanPenunjang() {
        if (NIPHeader.getText().trim().equals("")) {
            Valid.textKosong(NIPHeader, "Petugas");
        } else if (TNamaPemeriksaan.getText().trim().equals("")) {
            Valid.textKosong(TNamaPemeriksaan, "Nama Pemeriksaan");
        } else {
            if (Sequel.menyimpantf("observasi_icu_pemeriksaan_penunjang",
                "NULL,?,?,?,?,?,?", "Data", 6, new String[]{
                TNoRw.getText(),
                Valid.SetTgl(Tanggal.getSelectedItem() + ""),
                Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                TNamaPemeriksaan.getText(), THasilPemeriksaan.getText(),
                NIPHeader.getText()
            })) {
                tampilPemeriksaanPenunjang();
                emptTeks();
            }
        }
    }

    private void hapusPemeriksaanPenunjang() {
        if (tbPemeriksaanPenunjang.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from observasi_icu_pemeriksaan_penunjang where no_rawat=? and tgl_pemeriksaan=? and jam_pemeriksaan=?", 3, new String[]{
                tbPemeriksaanPenunjang.getValueAt(tbPemeriksaanPenunjang.getSelectedRow(), 0).toString(),
                tbPemeriksaanPenunjang.getValueAt(tbPemeriksaanPenunjang.getSelectedRow(), 1).toString(),
                tbPemeriksaanPenunjang.getValueAt(tbPemeriksaanPenunjang.getSelectedRow(), 2).toString()
            })) {
                tampilPemeriksaanPenunjang();
                emptTeks();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus terlebih dahulu");
        }
    }

    private void editPemeriksaanPenunjang() {
        if (tbPemeriksaanPenunjang.getSelectedRow() > -1) {
            if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); } else {
                int r = tbPemeriksaanPenunjang.getSelectedRow();
                Sequel.queryu2("update observasi_icu_pemeriksaan_penunjang set nama_pemeriksaan=?,hasil=?,nip=? where no_rawat=? and tgl_pemeriksaan=? and jam_pemeriksaan=?", 6, new String[]{
                    TNamaPemeriksaan.getText(), THasilPemeriksaan.getText(), NIPHeader.getText(),
                    tbPemeriksaanPenunjang.getValueAt(r, 0).toString(),
                    tbPemeriksaanPenunjang.getValueAt(r, 1).toString(),
                    tbPemeriksaanPenunjang.getValueAt(r, 2).toString()
                });
                tampilPemeriksaanPenunjang(); emptTeks();
            }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan diedit terlebih dahulu..!!"); }
    }

    private void tampilGambar() {
        Valid.tabelKosong(tabModeGambar);
        try {
            ps = koneksi.prepareStatement(
                "select nc.no_rawat,nc.tgl_nutrisi,nc.jam_nutrisi,nc.bb,nc.tb,nc.bmi,"
                + "nc.kebutuhan_kkal,nc.pemberian_persen,nc.protein,nc.balance,nc.iwl,nc.cairan_total,"
                + "nc.nip,IFNULL(pet.nama,nc.nip) from observasi_icu_nutrisi_cairan nc "
                + "left join petugas pet on nc.nip=pet.nip "
                + "where nc.no_rawat=? order by nc.tgl_nutrisi desc, nc.jam_nutrisi desc");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeGambar.addRow(new Object[]{
                    rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),
                    rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),
                    rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14)});
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilGambar: " + e); }
        LCount.setText("" + tabModeGambar.getRowCount());
    }

    private void simpanGambar() {
        if (NIPHeader.getText().trim().equals("")) {
            Valid.textKosong(NIPHeader, "Petugas");
        } else if (TBB.getText().trim().equals("")) {
            Valid.textKosong(TBB, "Berat Badan");
        } else if (TTB.getText().trim().equals("")) {
            Valid.textKosong(TTB, "Tinggi Badan");
        } else {
            // Calculate BMI
            try {
                double bb = Double.parseDouble(TBB.getText());
                double tb = Double.parseDouble(TTB.getText());
                double bmi = bb / ((tb / 100) * (tb / 100));
                TBMI.setText(String.format("%.2f", bmi));

                // Calculate nutritional requirements
                calculateNutritionalNeeds();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "BB dan TB harus berupa angka yang valid");
                return;
            }

            if (Sequel.menyimpantf("observasi_icu_nutrisi_cairan",
                "?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 13, new String[]{
                TNoRw.getText(),
                Valid.SetTgl(Tanggal.getSelectedItem() + ""),
                Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                TBB.getText(), TTB.getText(), TBMI.getText(),
                TKebutuhanKkal.getText(), TPersentasePemberian.getText(), TProtein.getText(),
                "", TIWL.getText(), TCairanTotal.getText(),
                NIPHeader.getText()
            })) {
                tampilGambar();
                emptTeks();
            }
        }
    }

    private void calculateNutritionalNeeds() {
        try {
            double bb = Double.parseDouble(TBB.getText());

            // Calculate Kebutuhan Kkal/24 jam
            if (!TKebutuhanKkal.getText().trim().equals("")) {
                double kkalPerKg = Double.parseDouble(TKebutuhanKkal.getText());
                double totalKkal = kkalPerKg * bb;
                TKebutuhanKkal24.setText(String.format("%.0f", totalKkal));
            }

            // Calculate IWL (10 cc/KgBB/24 jam)
            double iwl = 10 * bb;
            TIWL.setText(String.format("%.0f", iwl));

            // Calculate Pemberian Kkal
            if (!TPersentasePemberian.getText().trim().equals("") && !TKebutuhanKkal24.getText().trim().equals("")) {
                double persen = Double.parseDouble(TPersentasePemberian.getText());
                double totalKkal = Double.parseDouble(TKebutuhanKkal24.getText());
                double pemberianKkal = (persen / 100) * totalKkal;
                TPemberianKkal.setText(String.format("%.0f", pemberianKkal));
            }

            // Calculate Protein total
            if (!TProtein.getText().trim().equals("")) {
                double proteinPerKg = Double.parseDouble(TProtein.getText());
                double totalProtein = proteinPerKg * bb;
                TProteinTotal.setText(String.format("%.0f", totalProtein));
            }

            // Calculate Kalori non protein
            if (!TPemberianKkal.getText().trim().equals("") && !TProteinTotal.getText().trim().equals("")) {
                double totalKkal = Double.parseDouble(TPemberianKkal.getText());
                double totalProtein = Double.parseDouble(TProteinTotal.getText());
                double kaloriProtein = totalProtein * 4; // 1 gram protein = 4 kkal
                double kaloriNonProtein = totalKkal - kaloriProtein;
                TKaloriNonProtein.setText(String.format("%.0f", kaloriNonProtein));
            }

            // Calculate Rasio total and Kkal
            if (!TRasioKH.getText().trim().equals("") && !TRasioLemak.getText().trim().equals("") && !TKaloriNonProtein.getText().trim().equals("")) {
                double rasioKH = Double.parseDouble(TRasioKH.getText());
                double rasioLemak = Double.parseDouble(TRasioLemak.getText());
                double kaloriNonProtein = Double.parseDouble(TKaloriNonProtein.getText());

                double totalRasio = rasioKH + rasioLemak;
                TRasioTotal.setText(String.format("%.0f", totalRasio));

                // Calculate actual Kkal for ratio
                TRasioKkal.setText(String.format("%.0f", kaloriNonProtein));
            }

            // Calculate Cairan Total
            if (!TIWL.getText().trim().equals("") && !TObatObatan.getText().trim().equals("")) {
                double iwlValue = Double.parseDouble(TIWL.getText());
                double obatValue = Double.parseDouble(TObatObatan.getText());
                double cairanTotal = iwlValue + obatValue;
                TCairanTotal.setText(String.format("%.0f", cairanTotal));
            }

        } catch (NumberFormatException e) {
            // Handle calculation errors silently
        }
    }

    private void hapusGambar() {
        if (tbGambar.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from observasi_icu_nutrisi_cairan where no_rawat=? and tgl_nutrisi=? and jam_nutrisi=?", 3, new String[]{
                tbGambar.getValueAt(tbGambar.getSelectedRow(), 0).toString(),
                tbGambar.getValueAt(tbGambar.getSelectedRow(), 1).toString(),
                tbGambar.getValueAt(tbGambar.getSelectedRow(), 2).toString()
            })) {
                tampilGambar();
                emptTeks();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus terlebih dahulu");
        }
    }

    private void editGambar() {
        if (tbGambar.getSelectedRow() > -1) {
            if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); } else {
                try {
                    double bb = Double.parseDouble(TBB.getText());
                    double tb = Double.parseDouble(TTB.getText());
                    double bmi = bb / ((tb / 100) * (tb / 100));
                    TBMI.setText(String.format("%.2f", bmi));
                    calculateNutritionalNeeds();
                } catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "BB dan TB harus angka valid"); return; }
                int r = tbGambar.getSelectedRow();
                if (Sequel.mengedittf("observasi_icu_nutrisi_cairan", "no_rawat=? and tgl_nutrisi=? and jam_nutrisi=?",
                    "bb=?,tb=?,bmi=?,kebutuhan_kkal=?,pemberian_persen=?,protein=?,balance=?,iwl=?,cairan_total=?,nip=?", 13, new String[]{
                    TBB.getText(), TTB.getText(), TBMI.getText(),
                    TKebutuhanKkal.getText(), TPersentasePemberian.getText(), TProtein.getText(),
                    "", TIWL.getText(), TCairanTotal.getText(), NIPHeader.getText(),
                    tbGambar.getValueAt(r, 0).toString(), tbGambar.getValueAt(r, 1).toString(), tbGambar.getValueAt(r, 2).toString()
                })) { tampilGambar(); emptTeks(); }
            }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan diedit terlebih dahulu..!!"); }
    }

    private void tbGambarMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabModeGambar.getRowCount() != 0) {
            try {
                getDataGambar();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }

    private void getDataGambar() {
        if (tbGambar.getSelectedRow() != -1) {
            int r = tbGambar.getSelectedRow();
            TBB.setText(tabModeGambar.getValueAt(r, 3) == null ? "" : tabModeGambar.getValueAt(r, 3).toString());
            TTB.setText(tabModeGambar.getValueAt(r, 4) == null ? "" : tabModeGambar.getValueAt(r, 4).toString());
            TBMI.setText(tabModeGambar.getValueAt(r, 5) == null ? "" : tabModeGambar.getValueAt(r, 5).toString());
            TKebutuhanKkal.setText(tabModeGambar.getValueAt(r, 6) == null ? "" : tabModeGambar.getValueAt(r, 6).toString());
            TPersentasePemberian.setText(tabModeGambar.getValueAt(r, 7) == null ? "" : tabModeGambar.getValueAt(r, 7).toString());
            TProtein.setText(tabModeGambar.getValueAt(r, 8) == null ? "" : tabModeGambar.getValueAt(r, 8).toString());
            TIWL.setText(tabModeGambar.getValueAt(r, 10) == null ? "" : tabModeGambar.getValueAt(r, 10).toString());
            TCairanTotal.setText(tabModeGambar.getValueAt(r, 11) == null ? "" : tabModeGambar.getValueAt(r, 11).toString());
        }
    }

    private void ChkInputGambarActionPerformed(java.awt.event.ActionEvent evt) {
        isFormGambar();
    }

    private void isFormGambar() {
        if (ChkInputGambar.isSelected()) {
            ChkInputGambar.setVisible(false);
            PanelInputGambar.setPreferredSize(new Dimension(WIDTH, 340));
            panelGlassGambar.setVisible(true);
            ChkInputGambar.setVisible(true);
        } else {
            ChkInputGambar.setVisible(false);
            PanelInputGambar.setPreferredSize(new Dimension(WIDTH, 20));
            panelGlassGambar.setVisible(false);
            ChkInputGambar.setVisible(true);
        }
    }

    private void btnPetugasGambarActionPerformed(java.awt.event.ActionEvent evt) {
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }

    private void tampilCairanNutrisi() {
        Valid.tabelKosong(tabModeCairanNutrisi);
        try {
            ps = koneksi.prepareStatement(
                "select cn.no_rawat,cn.tgl_cairan,cn.jam_cairan,"
                + "cn.jenis_enteral,cn.cairan_enteral,cn.kalori_enteral,cn.kh_enteral,cn.lemak_enteral,cn.protein_enteral,"
                + "cn.jenis_parenteral,cn.cairan_parenteral,cn.kalori_parenteral,cn.kh_parenteral,cn.lemak_parenteral,cn.protein_parenteral,"
                + "cn.total_cairan,cn.total_kalori,cn.total_kh,cn.total_lemak,cn.total_protein,"
                + "cn.nip_petugas,IFNULL(pet.nama,cn.nip_petugas) "
                + "from icu_cairan_nutrisi cn "
                + "left join petugas pet on cn.nip_petugas=pet.nip "
                + "where cn.no_rawat=? order by cn.tgl_cairan desc, cn.jam_cairan desc");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeCairanNutrisi.addRow(new Object[]{
                    rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),
                    rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9),rs.getString(10),
                    rs.getString(11),rs.getString(12),rs.getString(13),rs.getString(14),rs.getString(15),
                    rs.getString(16),rs.getString(17),rs.getString(18),rs.getString(19),rs.getString(20),
                    rs.getString(21),rs.getString(22)});
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilCairanNutrisi: " + e); }
        LCount.setText("" + tabModeCairanNutrisi.getRowCount());
    }

    private void simpanCairanNutrisi() {
        if (NIPHeader.getText().trim().equals("")) {
            Valid.textKosong(NIPHeader, "Petugas");
        } else {
            calculateTotalNutrients();
            if (Sequel.menyimpantf("icu_cairan_nutrisi",
                "NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),NOW()", "Data", 32, new String[]{
                TNoRw.getText(),
                Valid.SetTgl(Tanggal.getSelectedItem() + ""),
                Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                ChkSSP.isSelected() ? "1" : "0",
                ChkJantung.isSelected() ? "1" : "0",
                ChkRespirasi.isSelected() ? "1" : "0",
                ChkRenal.isSelected() ? "1" : "0",
                ChkSaluranPencernaan.isSelected() ? "1" : "0",
                ChkHepar.isSelected() ? "1" : "0",
                ChkMetabolisme.isSelected() ? "1" : "0",
                ChkOral.isSelected() ? "1" : "0",
                ChkEnteral.isSelected() ? "1" : "0",
                ChkParenteral.isSelected() ? "1" : "0",
                ChkKombinasi.isSelected() ? "1" : "0",
                TJenisEnteral.getText(),
                TCairanEnteral.getText().equals("") ? "0" : TCairanEnteral.getText(),
                TKaloriEnteral.getText().equals("") ? "0" : TKaloriEnteral.getText(),
                TKHEnteral.getText().equals("") ? "0" : TKHEnteral.getText(),
                TLemakEnteral.getText().equals("") ? "0" : TLemakEnteral.getText(),
                TProteinEnteral.getText().equals("") ? "0" : TProteinEnteral.getText(),
                TJenisParenteral.getText(),
                TCairanParenteral.getText().equals("") ? "0" : TCairanParenteral.getText(),
                TKaloriParenteral.getText().equals("") ? "0" : TKaloriParenteral.getText(),
                TKHParenteral.getText().equals("") ? "0" : TKHParenteral.getText(),
                TLemakParenteral.getText().equals("") ? "0" : TLemakParenteral.getText(),
                TProteinParenteral.getText().equals("") ? "0" : TProteinParenteral.getText(),
                TTotalCairan.getText().equals("") ? "0" : TTotalCairan.getText(),
                TTotalKalori.getText().equals("") ? "0" : TTotalKalori.getText(),
                TTotalKH.getText().equals("") ? "0" : TTotalKH.getText(),
                TTotalLemak.getText().equals("") ? "0" : TTotalLemak.getText(),
                TTotalProtein.getText().equals("") ? "0" : TTotalProtein.getText(),
                NIPHeader.getText()
            })) {
                tampilCairanNutrisi();
                emptTeks();
            }
        }
    }

    private void calculateTotalNutrients() {
        try {
            // Calculate total cairan
            double cairanEnteral = TCairanEnteral.getText().trim().equals("") ? 0 : Double.parseDouble(TCairanEnteral.getText());
            double cairanParenteral = TCairanParenteral.getText().trim().equals("") ? 0 : Double.parseDouble(TCairanParenteral.getText());
            double totalCairan = cairanEnteral + cairanParenteral;
            TTotalCairan.setText(String.format("%.0f", totalCairan));

            // Calculate total kalori
            double kaloriEnteral = TKaloriEnteral.getText().trim().equals("") ? 0 : Double.parseDouble(TKaloriEnteral.getText());
            double kaloriParenteral = TKaloriParenteral.getText().trim().equals("") ? 0 : Double.parseDouble(TKaloriParenteral.getText());
            double totalKalori = kaloriEnteral + kaloriParenteral;
            TTotalKalori.setText(String.format("%.0f", totalKalori));

            // Calculate total KH
            double khEnteral = TKHEnteral.getText().trim().equals("") ? 0 : Double.parseDouble(TKHEnteral.getText());
            double khParenteral = TKHParenteral.getText().trim().equals("") ? 0 : Double.parseDouble(TKHParenteral.getText());
            double totalKH = khEnteral + khParenteral;
            TTotalKH.setText(String.format("%.0f", totalKH));

            // Calculate total Lemak
            double lemakEnteral = TLemakEnteral.getText().trim().equals("") ? 0 : Double.parseDouble(TLemakEnteral.getText());
            double lemakParenteral = TLemakParenteral.getText().trim().equals("") ? 0 : Double.parseDouble(TLemakParenteral.getText());
            double totalLemak = lemakEnteral + lemakParenteral;
            TTotalLemak.setText(String.format("%.0f", totalLemak));

            // Calculate total Protein
            double proteinEnteral = TProteinEnteral.getText().trim().equals("") ? 0 : Double.parseDouble(TProteinEnteral.getText());
            double proteinParenteral = TProteinParenteral.getText().trim().equals("") ? 0 : Double.parseDouble(TProteinParenteral.getText());
            double totalProtein = proteinEnteral + proteinParenteral;
            TTotalProtein.setText(String.format("%.0f", totalProtein));

        } catch (NumberFormatException e) {
            // Handle calculation errors silently
        }
    }

    private void hapusCairanNutrisi() {
        if (tbCairanNutrisi.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from icu_cairan_nutrisi where no_rawat=? and tgl_cairan=? and jam_cairan=?", 3, new String[]{
                tbCairanNutrisi.getValueAt(tbCairanNutrisi.getSelectedRow(), 0).toString(),
                tbCairanNutrisi.getValueAt(tbCairanNutrisi.getSelectedRow(), 1).toString(),
                tbCairanNutrisi.getValueAt(tbCairanNutrisi.getSelectedRow(), 2).toString()
            })) {
                tampilCairanNutrisi();
                emptTeks();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus terlebih dahulu");
        }
    }

    private void editCairanNutrisi() {
        if (tbCairanNutrisi.getSelectedRow() > -1) {
            if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); } else {
                calculateTotalNutrients();
                int r = tbCairanNutrisi.getSelectedRow();
                Sequel.queryu2("update icu_cairan_nutrisi set masalah_ssp=?,masalah_jantung=?,masalah_respirasi=?,masalah_renal=?,"
                    + "masalah_saluran_pencernaan=?,masalah_hepar=?,masalah_metabolisme=?,rate_oral=?,rate_enteral=?,rate_parenteral=?,rate_kombinasi=?,"
                    + "jenis_enteral=?,cairan_enteral=?,kalori_enteral=?,kh_enteral=?,lemak_enteral=?,protein_enteral=?,"
                    + "jenis_parenteral=?,cairan_parenteral=?,kalori_parenteral=?,kh_parenteral=?,lemak_parenteral=?,protein_parenteral=?,"
                    + "total_cairan=?,total_kalori=?,total_kh=?,total_lemak=?,total_protein=?,nip_petugas=? "
                    + "where no_rawat=? and tgl_cairan=? and jam_cairan=?", 32, new String[]{
                    ChkSSP.isSelected()?"1":"0", ChkJantung.isSelected()?"1":"0", ChkRespirasi.isSelected()?"1":"0", ChkRenal.isSelected()?"1":"0",
                    ChkSaluranPencernaan.isSelected()?"1":"0", ChkHepar.isSelected()?"1":"0", ChkMetabolisme.isSelected()?"1":"0",
                    ChkOral.isSelected()?"1":"0", ChkEnteral.isSelected()?"1":"0", ChkParenteral.isSelected()?"1":"0", ChkKombinasi.isSelected()?"1":"0",
                    TJenisEnteral.getText(),
                    TCairanEnteral.getText().equals("")?"0":TCairanEnteral.getText(),
                    TKaloriEnteral.getText().equals("")?"0":TKaloriEnteral.getText(),
                    TKHEnteral.getText().equals("")?"0":TKHEnteral.getText(),
                    TLemakEnteral.getText().equals("")?"0":TLemakEnteral.getText(),
                    TProteinEnteral.getText().equals("")?"0":TProteinEnteral.getText(),
                    TJenisParenteral.getText(),
                    TCairanParenteral.getText().equals("")?"0":TCairanParenteral.getText(),
                    TKaloriParenteral.getText().equals("")?"0":TKaloriParenteral.getText(),
                    TKHParenteral.getText().equals("")?"0":TKHParenteral.getText(),
                    TLemakParenteral.getText().equals("")?"0":TLemakParenteral.getText(),
                    TProteinParenteral.getText().equals("")?"0":TProteinParenteral.getText(),
                    TTotalCairan.getText().equals("")?"0":TTotalCairan.getText(),
                    TTotalKalori.getText().equals("")?"0":TTotalKalori.getText(),
                    TTotalKH.getText().equals("")?"0":TTotalKH.getText(),
                    TTotalLemak.getText().equals("")?"0":TTotalLemak.getText(),
                    TTotalProtein.getText().equals("")?"0":TTotalProtein.getText(),
                    NIPHeader.getText(),
                    tbCairanNutrisi.getValueAt(r, 0).toString(),
                    tbCairanNutrisi.getValueAt(r, 1).toString(),
                    tbCairanNutrisi.getValueAt(r, 2).toString()
                });
                tampilCairanNutrisi(); emptTeks();
            }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan diedit terlebih dahulu..!!"); }
    }

    private void tbCairanNutrisiMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabModeCairanNutrisi.getRowCount() != 0) {
            try {
                getDataCairanNutrisi();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }

    private void getDataCairanNutrisi() {
        if (tbCairanNutrisi.getSelectedRow() != -1) {
            int r = tbCairanNutrisi.getSelectedRow();
            // Query detail dari DB karena tabel tidak simpan semua field checkbox
            try {
                ps = koneksi.prepareStatement("select * from icu_cairan_nutrisi where no_rawat=? and tgl_cairan=? and jam_cairan=?");
                ps.setString(1, tabModeCairanNutrisi.getValueAt(r, 0).toString());
                ps.setString(2, tabModeCairanNutrisi.getValueAt(r, 1).toString());
                ps.setString(3, tabModeCairanNutrisi.getValueAt(r, 2).toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    ChkSSP.setSelected(rs.getInt("masalah_ssp") == 1);
                    ChkJantung.setSelected(rs.getInt("masalah_jantung") == 1);
                    ChkRespirasi.setSelected(rs.getInt("masalah_respirasi") == 1);
                    ChkRenal.setSelected(rs.getInt("masalah_renal") == 1);
                    ChkSaluranPencernaan.setSelected(rs.getInt("masalah_saluran_pencernaan") == 1);
                    ChkHepar.setSelected(rs.getInt("masalah_hepar") == 1);
                    ChkMetabolisme.setSelected(rs.getInt("masalah_metabolisme") == 1);
                    ChkOral.setSelected(rs.getInt("rate_oral") == 1);
                    ChkEnteral.setSelected(rs.getInt("rate_enteral") == 1);
                    ChkParenteral.setSelected(rs.getInt("rate_parenteral") == 1);
                    ChkKombinasi.setSelected(rs.getInt("rate_kombinasi") == 1);
                    TJenisEnteral.setText(rs.getString("jenis_enteral") == null ? "" : rs.getString("jenis_enteral"));
                    TCairanEnteral.setText(rs.getString("cairan_enteral") == null ? "" : rs.getString("cairan_enteral"));
                    TKaloriEnteral.setText(rs.getString("kalori_enteral") == null ? "" : rs.getString("kalori_enteral"));
                    TKHEnteral.setText(rs.getString("kh_enteral") == null ? "" : rs.getString("kh_enteral"));
                    TLemakEnteral.setText(rs.getString("lemak_enteral") == null ? "" : rs.getString("lemak_enteral"));
                    TProteinEnteral.setText(rs.getString("protein_enteral") == null ? "" : rs.getString("protein_enteral"));
                    TJenisParenteral.setText(rs.getString("jenis_parenteral") == null ? "" : rs.getString("jenis_parenteral"));
                    TCairanParenteral.setText(rs.getString("cairan_parenteral") == null ? "" : rs.getString("cairan_parenteral"));
                    TKaloriParenteral.setText(rs.getString("kalori_parenteral") == null ? "" : rs.getString("kalori_parenteral"));
                    TKHParenteral.setText(rs.getString("kh_parenteral") == null ? "" : rs.getString("kh_parenteral"));
                    TLemakParenteral.setText(rs.getString("lemak_parenteral") == null ? "" : rs.getString("lemak_parenteral"));
                    TProteinParenteral.setText(rs.getString("protein_parenteral") == null ? "" : rs.getString("protein_parenteral"));
                    TTotalCairan.setText(rs.getString("total_cairan") == null ? "" : rs.getString("total_cairan"));
                    TTotalKalori.setText(rs.getString("total_kalori") == null ? "" : rs.getString("total_kalori"));
                    TTotalKH.setText(rs.getString("total_kh") == null ? "" : rs.getString("total_kh"));
                    TTotalLemak.setText(rs.getString("total_lemak") == null ? "" : rs.getString("total_lemak"));
                    TTotalProtein.setText(rs.getString("total_protein") == null ? "" : rs.getString("total_protein"));
                }
                rs.close(); ps.close();
            } catch (Exception e) { System.out.println("Notifikasi getDataCairanNutrisi: " + e); }
        }
    }

    private void ChkInputCairanNutrisiActionPerformed(java.awt.event.ActionEvent evt) {
        isFormCairanNutrisi();
    }

    private void isFormCairanNutrisi() {
        if (ChkInputCairanNutrisi.isSelected()) {
            ChkInputCairanNutrisi.setVisible(false);
            PanelInputCairanNutrisi.setPreferredSize(new Dimension(WIDTH, 420));
            panelGlassCairanNutrisi.setVisible(true);
            ChkInputCairanNutrisi.setVisible(true);
        } else {
            ChkInputCairanNutrisi.setVisible(false);
            PanelInputCairanNutrisi.setPreferredSize(new Dimension(WIDTH, 20));
            panelGlassCairanNutrisi.setVisible(false);
            ChkInputCairanNutrisi.setVisible(true);
        }
    }

    private void btnPetugasCairanNutrisiActionPerformed(java.awt.event.ActionEvent evt) {
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }

    private void tampilTindakan() {
        Valid.tabelKosong(tabModeTindakan);
        try {
            ps = koneksi.prepareStatement(
                "select t.no_rawat,t.tgl_tindakan,t.jam_tindakan,"
                + "t.endotracheal_tube,t.tracheostomy_tube,t.tubing_ventilator,t.nasogastric_tube,"
                + "t.central_venous_catheter,t.picc,t.close_suction,t.arteri_line_catheter,"
                + "t.dower_catheter,t.infus_perifer,t.pemasangan_ke,t.hari_ke,t.keterangan,"
                + "t.nip_petugas,IFNULL(pet.nama,t.nip_petugas) "
                + "from icu_tindakan t "
                + "left join petugas pet on t.nip_petugas=pet.nip "
                + "where t.no_rawat=? order by t.tgl_tindakan desc, t.jam_tindakan desc");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeTindakan.addRow(new Object[]{
                    rs.getString(1),rs.getString(2),rs.getString(3),
                    rs.getInt(4)==1?"Ya":"",rs.getInt(5)==1?"Ya":"",rs.getInt(6)==1?"Ya":"",
                    rs.getInt(7)==1?"Ya":"",rs.getInt(8)==1?"Ya":"",rs.getInt(9)==1?"Ya":"",
                    rs.getInt(10)==1?"Ya":"",rs.getInt(11)==1?"Ya":"",rs.getInt(12)==1?"Ya":"",
                    rs.getInt(13)==1?"Ya":"",rs.getString(14),rs.getString(15),rs.getString(16),
                    rs.getString(17),rs.getString(18)});
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notifikasi tampilTindakan: " + e); }
        LCount.setText("" + tabModeTindakan.getRowCount());
    }

    private void simpanTindakan() {
        if (NIPHeader.getText().trim().equals("")) {
            Valid.textKosong(NIPHeader, "Petugas");
        } else if (!hasSelectedTindakan()) {
            JOptionPane.showMessageDialog(this, "Pilih minimal satu tindakan");
        } else {
            if (Sequel.menyimpantf("icu_tindakan",
                "NULL,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),NOW()", "Data", 17, new String[]{
                TNoRw.getText(),
                Valid.SetTgl(Tanggal.getSelectedItem() + ""),
                Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                ChkEndotrachealTube.isSelected() ? "1" : "0",
                ChkTracheostomyTube.isSelected() ? "1" : "0",
                ChkTubingVentilator.isSelected() ? "1" : "0",
                ChkNasogastricTube.isSelected() ? "1" : "0",
                ChkCentralVenousCatheter.isSelected() ? "1" : "0",
                ChkPICC.isSelected() ? "1" : "0",
                ChkCloseSuction.isSelected() ? "1" : "0",
                ChkArteriLineCatheter.isSelected() ? "1" : "0",
                ChkDowerCatheter.isSelected() ? "1" : "0",
                ChkInfusPerifer.isSelected() ? "1" : "0",
                TPemasanganKe.getText(), THariKe.getText().equals("") ? "0" : THariKe.getText(),
                TKeteranganTindakan.getText(),
                NIPHeader.getText()
            })) {
                tampilTindakan();
                emptTeks();
            }
        }
    }

    private boolean hasSelectedTindakan() {
        return ChkEndotrachealTube.isSelected() || ChkTracheostomyTube.isSelected()
                || ChkTubingVentilator.isSelected() || ChkNasogastricTube.isSelected()
                || ChkCentralVenousCatheter.isSelected() || ChkPICC.isSelected()
                || ChkCloseSuction.isSelected() || ChkArteriLineCatheter.isSelected()
                || ChkDowerCatheter.isSelected() || ChkInfusPerifer.isSelected();
    }

    private String getSelectedTindakanString() {
        StringBuilder tindakan = new StringBuilder();

        if (ChkEndotrachealTube.isSelected()) {
            tindakan.append("Endotracheal Tube, ");
        }
        if (ChkTracheostomyTube.isSelected()) {
            tindakan.append("Tracheostomy Tube, ");
        }
        if (ChkTubingVentilator.isSelected()) {
            tindakan.append("Tubing Ventilator, ");
        }
        if (ChkNasogastricTube.isSelected()) {
            tindakan.append("Nasogastric Tube, ");
        }
        if (ChkCentralVenousCatheter.isSelected()) {
            tindakan.append("Central Venous Catheter, ");
        }
        if (ChkPICC.isSelected()) {
            tindakan.append("PICC, ");
        }
        if (ChkCloseSuction.isSelected()) {
            tindakan.append("Close Suction, ");
        }
        if (ChkArteriLineCatheter.isSelected()) {
            tindakan.append("Arteri Line Catheter, ");
        }
        if (ChkDowerCatheter.isSelected()) {
            tindakan.append("Dower Catheter, ");
        }
        if (ChkInfusPerifer.isSelected()) {
            tindakan.append("Infus Perifer, ");
        }

        // Remove trailing comma and space
        if (tindakan.length() > 0) {
            tindakan.setLength(tindakan.length() - 2);
        }

        return tindakan.toString();
    }

    private void hapusTindakan() {
        if (tbTindakan.getSelectedRow() > -1) {
            if (Sequel.queryu2tf("delete from icu_tindakan where no_rawat=? and tgl_tindakan=? and jam_tindakan=?", 3, new String[]{
                tbTindakan.getValueAt(tbTindakan.getSelectedRow(), 0).toString(),
                tbTindakan.getValueAt(tbTindakan.getSelectedRow(), 1).toString(),
                tbTindakan.getValueAt(tbTindakan.getSelectedRow(), 2).toString()
            })) {
                tampilTindakan();
                emptTeks();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus terlebih dahulu");
        }
    }

    private void editTindakan() {
        if (tbTindakan.getSelectedRow() > -1) {
            if (NIPHeader.getText().trim().equals("")) { Valid.textKosong(NIPHeader, "Petugas"); } else {
                int r = tbTindakan.getSelectedRow();
                Sequel.queryu2("update icu_tindakan set endotracheal_tube=?,tracheostomy_tube=?,tubing_ventilator=?,nasogastric_tube=?,"
                    + "central_venous_catheter=?,picc=?,close_suction=?,arteri_line_catheter=?,dower_catheter=?,infus_perifer=?,"
                    + "pemasangan_ke=?,hari_ke=?,keterangan=?,nip_petugas=? "
                    + "where no_rawat=? and tgl_tindakan=? and jam_tindakan=?", 17, new String[]{
                    ChkEndotrachealTube.isSelected()?"1":"0", ChkTracheostomyTube.isSelected()?"1":"0",
                    ChkTubingVentilator.isSelected()?"1":"0", ChkNasogastricTube.isSelected()?"1":"0",
                    ChkCentralVenousCatheter.isSelected()?"1":"0", ChkPICC.isSelected()?"1":"0",
                    ChkCloseSuction.isSelected()?"1":"0", ChkArteriLineCatheter.isSelected()?"1":"0",
                    ChkDowerCatheter.isSelected()?"1":"0", ChkInfusPerifer.isSelected()?"1":"0",
                    TPemasanganKe.getText(), THariKe.getText().equals("")?"0":THariKe.getText(),
                    TKeteranganTindakan.getText(), NIPHeader.getText(),
                    tbTindakan.getValueAt(r, 0).toString(), tbTindakan.getValueAt(r, 1).toString(), tbTindakan.getValueAt(r, 2).toString()
                });
                tampilTindakan(); emptTeks();
            }
        } else { JOptionPane.showMessageDialog(null, "Pilih data yang akan diedit terlebih dahulu..!!"); }
    }

    private void tbTindakanMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabModeTindakan.getRowCount() != 0) {
            try {
                getDataTindakan();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }

    private void getDataTindakan() {
        if (tbTindakan.getSelectedRow() != -1) {
            int r = tbTindakan.getSelectedRow();
            ChkEndotrachealTube.setSelected("Ya".equals(tabModeTindakan.getValueAt(r, 3)));
            ChkTracheostomyTube.setSelected("Ya".equals(tabModeTindakan.getValueAt(r, 4)));
            ChkTubingVentilator.setSelected("Ya".equals(tabModeTindakan.getValueAt(r, 5)));
            ChkNasogastricTube.setSelected("Ya".equals(tabModeTindakan.getValueAt(r, 6)));
            ChkCentralVenousCatheter.setSelected("Ya".equals(tabModeTindakan.getValueAt(r, 7)));
            ChkPICC.setSelected("Ya".equals(tabModeTindakan.getValueAt(r, 8)));
            ChkCloseSuction.setSelected("Ya".equals(tabModeTindakan.getValueAt(r, 9)));
            ChkArteriLineCatheter.setSelected("Ya".equals(tabModeTindakan.getValueAt(r, 10)));
            ChkDowerCatheter.setSelected("Ya".equals(tabModeTindakan.getValueAt(r, 11)));
            ChkInfusPerifer.setSelected("Ya".equals(tabModeTindakan.getValueAt(r, 12)));
            TPemasanganKe.setText(tabModeTindakan.getValueAt(r, 13) == null ? "" : tabModeTindakan.getValueAt(r, 13).toString());
            THariKe.setText(tabModeTindakan.getValueAt(r, 14) == null ? "" : tabModeTindakan.getValueAt(r, 14).toString());
            TKeteranganTindakan.setText(tabModeTindakan.getValueAt(r, 15) == null ? "" : tabModeTindakan.getValueAt(r, 15).toString());
        }
    }

    private void ChkInputTindakanActionPerformed(java.awt.event.ActionEvent evt) {
        isFormTindakan();
    }

    private void isFormTindakan() {
        if (ChkInputTindakan.isSelected()) {
            ChkInputTindakan.setVisible(false);
            PanelInputTindakan.setPreferredSize(new Dimension(WIDTH, 220));
            panelGlassTindakan.setVisible(true);
            ChkInputTindakan.setVisible(true);
        } else {
            ChkInputTindakan.setVisible(false);
            PanelInputTindakan.setPreferredSize(new Dimension(WIDTH, 20));
            panelGlassTindakan.setVisible(false);
            ChkInputTindakan.setVisible(true);
        }
    }

    private void btnPetugasTindakanActionPerformed(java.awt.event.ActionEvent evt) {
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }

    // Event handlers for new tabs
    private void ChkInputKeperawatanActionPerformed(java.awt.event.ActionEvent evt) {
        isFormKeperawatan();
    }

    private void tbCatatanKeperawatanMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabModeCatatanKeperawatan.getRowCount() != 0) {
            try {
                getDataCatatanKeperawatan();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }

    private void btnPetugasKeperawatanActionPerformed(java.awt.event.ActionEvent evt) {
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }

    private void tbBundleVAPMouseClicked(java.awt.event.MouseEvent evt) {
        // Handle Bundle VAP table click
    }

    private void tbBundleCVCMouseClicked(java.awt.event.MouseEvent evt) {
        // Handle Bundle CVC table click
    }

    private void tbBundleISKMouseClicked(java.awt.event.MouseEvent evt) {
        // Handle Bundle ISK table click
        if (tabModeBundleISK.getRowCount() != 0) {
            try {
                getDataBundleISK();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }

    private void tbPemeriksaanPenunjangMouseClicked(java.awt.event.MouseEvent evt) {
        // Handle Pemeriksaan Penunjang table click
        if (tabModePemeriksaanPenunjang.getRowCount() != 0) {
            try {
                getDataPemeriksaanPenunjang();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }

    private void btnPetugasBundleVAPActionPerformed(java.awt.event.ActionEvent evt) {
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }

    private void btnPetugasBundleCVCActionPerformed(java.awt.event.ActionEvent evt) {
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }

    private void btnPetugasBundleISKActionPerformed(java.awt.event.ActionEvent evt) {
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }

    private void btnPetugasPemeriksaanPenunjangActionPerformed(java.awt.event.ActionEvent evt) {
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }

    private void ChkInputPemeriksaanPenunjangActionPerformed(java.awt.event.ActionEvent evt) {
        isFormPemeriksaanPenunjang();
    }

    private void isFormPemeriksaanPenunjang() {
        if (ChkInputPemeriksaanPenunjang.isSelected()) {
            ChkInputPemeriksaanPenunjang.setVisible(false);
            PanelInputPemeriksaanPenunjang.setPreferredSize(new Dimension(WIDTH, 160));
            panelGlassPemeriksaanPenunjang.setVisible(true);
            ChkInputPemeriksaanPenunjang.setVisible(true);
        } else {
            ChkInputPemeriksaanPenunjang.setVisible(false);
            PanelInputPemeriksaanPenunjang.setPreferredSize(new Dimension(WIDTH, 20));
            panelGlassPemeriksaanPenunjang.setVisible(false);
            ChkInputPemeriksaanPenunjang.setVisible(true);
        }
    }

    private void getDataBundleISK() {
        if (tbBundleISK.getSelectedRow() != -1) {
            int r = tbBundleISK.getSelectedRow();
            if (cmbPemasanganSesuaiISK.getItemCount() > 0) cmbPemasanganSesuaiISK.setSelectedItem(tabModeBundleISK.getValueAt(r, 3));
            if (cmbTeknikAseptikISK.getItemCount() > 0) cmbTeknikAseptikISK.setSelectedItem(tabModeBundleISK.getValueAt(r, 4));
            if (cmbKateterDibutuhkanISK.getItemCount() > 0) cmbKateterDibutuhkanISK.setSelectedItem(tabModeBundleISK.getValueAt(r, 5));
            if (cmbPosisiUrinBagISK.getItemCount() > 0) cmbPosisiUrinBagISK.setSelectedItem(tabModeBundleISK.getValueAt(r, 6));
            if (cmbHandHygieneISK.getItemCount() > 0) cmbHandHygieneISK.setSelectedItem(tabModeBundleISK.getValueAt(r, 7));
            if (cmbMeatalCareISK.getItemCount() > 0) cmbMeatalCareISK.setSelectedItem(tabModeBundleISK.getValueAt(r, 8));
            if (cmbFiksasiKateterISK.getItemCount() > 0) cmbFiksasiKateterISK.setSelectedItem(tabModeBundleISK.getValueAt(r, 9));
            if (cmbKosongkanUrinBagISK.getItemCount() > 0) cmbKosongkanUrinBagISK.setSelectedItem(tabModeBundleISK.getValueAt(r, 10));
            if (cmbClosedSystemISK.getItemCount() > 0) cmbClosedSystemISK.setSelectedItem(tabModeBundleISK.getValueAt(r, 11));
        }
    }

    private void getDataPemeriksaanPenunjang() {
        if (tbPemeriksaanPenunjang.getSelectedRow() != -1) {
            int r = tbPemeriksaanPenunjang.getSelectedRow();
            TNamaPemeriksaan.setText(tabModePemeriksaanPenunjang.getValueAt(r, 3) == null ? "" : tabModePemeriksaanPenunjang.getValueAt(r, 3).toString());
            THasilPemeriksaan.setText(tabModePemeriksaanPenunjang.getValueAt(r, 4) == null ? "" : tabModePemeriksaanPenunjang.getValueAt(r, 4).toString());
        }
    }

    private void tbCatatanMouseClicked(java.awt.event.MouseEvent evt) {
        if (tabModeCatatan.getRowCount() != 0) {
            try {
                getDataCatatan();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }

    private void getDataCatatan() {
        if (tbCatatan.getSelectedRow() != -1) {
            int r = tbCatatan.getSelectedRow();
            TSuhuTubuh.setText(tbCatatan.getValueAt(r, 3).toString());
            TTensi.setText(tbCatatan.getValueAt(r, 4).toString());
            TNadi.setText(tbCatatan.getValueAt(r, 5).toString());
            TRespirasi.setText(tbCatatan.getValueAt(r, 6).toString());
            TTinggi.setText(tbCatatan.getValueAt(r, 7).toString());
            TBerat.setText(tbCatatan.getValueAt(r, 8).toString());
            TSpO2.setText(tbCatatan.getValueAt(r, 9).toString());
            TKeluhan.setText(tbCatatan.getValueAt(r, 11).toString());
            TPemeriksaanRanap.setText(tbCatatan.getValueAt(r, 12).toString());
            TAlergi.setText(tbCatatan.getValueAt(r, 13).toString());
            TPenilaian.setText(tbCatatan.getValueAt(r, 14).toString());
            TRTL.setText(tbCatatan.getValueAt(r, 15).toString());
            TInstruksi.setText(tbCatatan.getValueAt(r, 16).toString());
            TEvaluasi.setText(tbCatatan.getValueAt(r, 17).toString());
            KdPegCatatan.setText(tbCatatan.getValueAt(r, 18).toString());
            NamaPegawaiCatatan.setText(tbCatatan.getValueAt(r, 19).toString());
        }
    }

    private void isFormKeperawatan() {
        if (ChkInputKeperawatan.isSelected()) {
            ChkInputKeperawatan.setVisible(false);
            // 30 item layout 3 kol x 10 baris (panel 290) + checkbox 25 + padding
            PanelInputKeperawatan.setPreferredSize(new Dimension(WIDTH, 320));
            panelGlassCatatanKeperawatan.setVisible(true);
            ChkInputKeperawatan.setVisible(true);
        } else {
            ChkInputKeperawatan.setVisible(false);
            PanelInputKeperawatan.setPreferredSize(new Dimension(WIDTH, 20));
            panelGlassCatatanKeperawatan.setVisible(false);
            ChkInputKeperawatan.setVisible(true);
        }
    }

    private void getDataCatatanKeperawatan() {
        if (tbCatatanKeperawatan.getSelectedRow() != -1) {
            int r = tbCatatanKeperawatan.getSelectedRow();
            // Kolom 3-32 = 30 textbox tindakan (urutan match tampilCatatanKeperawatan)
            widget.TextBox[] boxes = {TKep01,TKep02,TKep03,TKep04,TKep05,TKep06,TKep07,TKep08,TKep09,TKep10,
                                      TKep11,TKep12,TKep13,TKep14,TKep15,TKep16,TKep17,TKep18,TKep19,TKep20,
                                      TKep21,TKep22,TKep23,TKep24,TKep25,TKep26,TKep27,TKep28,TKep29,TKep30};
            for (int i = 0; i < 30; i++) {
                Object v = tabModeCatatanKeperawatan.getValueAt(r, 3 + i);
                boxes[i].setText(v == null ? "" : v.toString());
            }
        }
    }

    private void calculateGCSTotal() {
        try {
            int e = TGCS_E.getText().isEmpty() ? 0 : Integer.parseInt(TGCS_E.getText());
            int v = TGCS_V.getText().isEmpty() ? 0 : Integer.parseInt(TGCS_V.getText());
            int m = TGCS_M.getText().isEmpty() ? 0 : Integer.parseInt(TGCS_M.getText());
            int total = e + v + m;
            TGCSTotal.setText(total > 0 ? String.valueOf(total) : "");
        } catch (NumberFormatException ex) {
            TGCSTotal.setText("");
        }
    }

}
