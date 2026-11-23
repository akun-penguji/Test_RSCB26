/*
 * BPJSRujukanSatuSehat.java
 *
 * Form Rujukan Keluar terintegrasi BPJS + Satu Sehat (Sisrute).
 * Dipanggil dari BPJSDataSEP setelah user memilih SEP rawat jalan.
 *
 * Flow:
 *   1. setDataSEP(...) dipanggil parent → form auto-isi data
 *   2. User cek/lengkapi data, lalu klik [Cek Kriteria]
 *      → POST /Rujukan/GetKriteriaRujukan
 *      → Tabel kriteria muncul, user isi jawaban (boolean/text)
 *   3. User klik [Cari Faskes] → POST /Sisrute/GetFaskesRujukan
 *      → Tabel faskes muncul, user pilih satu RS tujuan
 *   4. User klik [Kirim Rujukan] → POST /Sisrute/postKunjungan
 *      → Simpan ke 3 tabel:
 *         - bridging_rujukan_bpjs       (existing)
 *         - bridging_rujukan_satusehat  (BARU)
 *         - bridging_kriteria_rujukan_satusehat (BARU)
 *
 * @author SIMRS Khanza Bridging Sisrute
 */

package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public final class BPJSRujukanSatuSehat extends javax.swing.JDialog {

    // ===== Helper Khanza =====
    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();
    private final ObjectMapper mapper = new ObjectMapper();

    // ===== Service =====
    private final SisruteService sisrute = new SisruteService();

    // ===== State data dari parent (BPJSDataSEP) =====
    private String noSep = "";
    private String noRawat = "";
    private String noRm = "";
    private String namaPasien = "";
    private String kdPenyakit = "";        // ICD-10 (mis. I10)
    private String nmPenyakit = "";
    private String kdPoliBpjs = "";        // kode poli/spesialis BPJS (mis. INT)
    private String nmPoli = "";
    private String kdDokterRs = "";        // kd_dokter Khanza
    private String user = "";

    // ===== State data Satu Sehat =====
    private String kodeFaskesSatuSehat = "";  // dari koneksiDB.IDSATUSEHAT()
    private String idPasienSatuSehat = "";    // IHS pasien
    private String kdDokterSatuSehat = "";    // IHS dokter (dari mapping)
    private String encounterRef = "";         // dari satu_sehat_encounter
    private String kodeSpesialis = "";        // dari maping_poli_bpjs

    // ===== State faskes tujuan terpilih =====
    private String selectedKdppkTujuan = "";
    private String selectedNmppkTujuan = "";

    // ===== State diagnosa & poli RUJUKAN (boleh beda dari diagnosa kunjungan) =====
    private String kdPenyakitRujuk = "";   // ICD-10 untuk rujukan (default = kdPenyakit kunjungan)
    private String nmPenyakitRujuk = "";
    private String kdPoliRujuk = "";       // kode poli BPJS untuk rujukan (default = kdPoliBpjs kunjungan)
    private String nmPoliRujuk = "";

    // ===== Popup picker BPJS (reuse existing popup) =====
    private final BPJSCekReferensiPenyakit popupPenyakit = new BPJSCekReferensiPenyakit(null, true);
    private final BPJSCekReferensiPoli     popupPoli     = new BPJSCekReferensiPoli(null, true);

    // ===== Helper cari IHS pasien & dokter =====
    private final SatuSehatCekNIK cekViaSatuSehat = new SatuSehatCekNIK();

    // ===== Tabel models =====
    private final DefaultTableModel modelKriteria;
    private final DefaultTableModel modelFaskes;

    // ===== UI components =====
    private widget.InternalFrame frameMain;
    private widget.PanelBiasa panelData;
    private widget.PanelBiasa panelTombol;

    // Data SEP (read-only display)
    private widget.TextBox tNoSep, tNoRawat, tPasien;
    private widget.TextBox tEncounter, tIdPasienIhs, tIdDokterIhs;

    // Editable: Encounter button + diagnosa/poli rujukan
    private widget.Button btnBuatEncounter;
    private widget.TextBox tKdDiagnosaRujuk, tNmDiagnosaRujuk;
    private widget.TextBox tKdPoliRujuk, tNmPoliRujuk;
    private widget.Button btnPilihDiagnosa, btnPilihPoli;

    // Input
    private widget.Tanggal dtTglRujukan;
    private widget.Tanggal dtTglRencana;
    private widget.ComboBox cbJnsPelayanan;
    private widget.ComboBox cbTipeRujukan;
    private widget.ComboBox cbProvinsi;
    private widget.TextArea taCatatan;
    private widget.TextArea taKeterangan;

    // Tombol aksi
    private widget.Button btnCekKriteria;
    private widget.Button btnCariFaskes;
    private widget.Button btnKirim;
    private widget.Button btnHapus;
    private widget.Button btnTutup;

    // Tabel kriteria & faskes
    private widget.Table tblKriteria;
    private widget.Table tblFaskes;
    private widget.ScrollPane scrollKriteria;
    private widget.ScrollPane scrollFaskes;

    // Status label
    private widget.Label lblStatus;

    // Daftar kode-nama provinsi (preset 5 wilayah jejaring umum, bisa diperluas)
    private static final String[][] PROVINSI = {
        {"31", "DKI Jakarta"},
        {"32", "Jawa Barat"},
        {"33", "Jawa Tengah"},
        {"34", "Daerah Istimewa Yogyakarta"},
        {"35", "Jawa Timur"},
        {"36", "Banten"},
        {"73", "Sulawesi Selatan"},
    };

    public BPJSRujukanSatuSehat(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        // Init table models
        modelKriteria = new DefaultTableModel(null, new Object[]{
                "linkId", "Pertanyaan Kriteria", "Tipe", "Jawaban (boolean/text)"
        }) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 3;
            }
        };

        modelFaskes = new DefaultTableModel(null, new Object[]{
                "Pilih", "Kode SatuSehat", "Kode PPK", "Nama RS", "Kelas",
                "Kab/Kota", "Jarak (m)", "% Beban"
        }) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 0;
            }

            @Override
            public Class<?> getColumnClass(int col) {
                return col == 0 ? Boolean.class : String.class;
            }
        };

        initComponents();
        setSize(1100, 720);
        setLocationRelativeTo(parent);

        // Ambil user yang login
        try {
            user = akses.getkode().replace(" ", "").substring(0, 9);
        } catch (Exception e) {
            user = akses.getkode();
        }

        // Ambil kode faskes Satu Sehat
        try {
            kodeFaskesSatuSehat = koneksiDB.IDSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Gagal ambil IDSATUSEHAT : " + e);
        }

        // Setup window listener popup picker - copy data ke field saat dialog ditutup
        setupPopupListeners();
    }

    /**
     * Setup callback saat user pilih item di popup BPJSCekReferensiPenyakit & Poli.
     * Pattern reuse dari BPJSRujukanKeluar existing.
     */
    private void setupPopupListeners() {
        popupPenyakit.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override public void windowClosed(WindowEvent e) {
                if (popupPenyakit.getTable().getSelectedRow() != -1) {
                    int row = popupPenyakit.getTable().getSelectedRow();
                    kdPenyakitRujuk = popupPenyakit.getTable().getValueAt(row, 1).toString();
                    nmPenyakitRujuk = popupPenyakit.getTable().getValueAt(row, 2).toString();
                    tKdDiagnosaRujuk.setText(kdPenyakitRujuk);
                    tNmDiagnosaRujuk.setText(nmPenyakitRujuk);
                }
            }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });

        popupPoli.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override public void windowClosed(WindowEvent e) {
                if (popupPoli.getTable().getSelectedRow() != -1) {
                    int row = popupPoli.getTable().getSelectedRow();
                    kdPoliRujuk = popupPoli.getTable().getValueAt(row, 1).toString();
                    nmPoliRujuk = popupPoli.getTable().getValueAt(row, 2).toString();
                    tKdPoliRujuk.setText(kdPoliRujuk);
                    tNmPoliRujuk.setText(nmPoliRujuk);
                }
            }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });
    }

    // =================================================================
    //  initComponents - dibangun manual (tidak pakai NetBeans Form Editor)
    //  supaya gampang di-port dan tidak butuh .form file.
    // =================================================================
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        frameMain = new widget.InternalFrame();
        frameMain.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
                "::[ Rujukan Keluar Sisrute (BPJS + Satu Sehat) ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 11),
                new java.awt.Color(50, 50, 50)));
        frameMain.setLayout(new BorderLayout(2, 2));

        // ==================== PANEL DATA (atas) ====================
        panelData = new widget.PanelBiasa();
        panelData.setLayout(null);
        panelData.setPreferredSize(new Dimension(1100, 280));

        int x1 = 10, x2 = 110, w1 = 95, w2 = 380, h = 23, gap = 28;
        int y = 8;

        // Row 1: No.SEP | Encounter (+ tombol Buat Encounter)
        addLabel("No.SEP :",   x1, y, w1, h);
        tNoSep = roField(x2, y, w2, h);
        addLabel("Encounter :", x2 + w2 + 20, y, w1, h);
        tEncounter = roField(x2 + w2 + 20 + w1, y, w2 - 180, h);
        // Tombol Buat Encounter - di sebelah kanan field encounter
        btnBuatEncounter = new widget.Button();
        btnBuatEncounter.setText("Buat Encounter");
        btnBuatEncounter.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/picture/save-16x16.png")));
        btnBuatEncounter.setBounds(x2 + w2 + 20 + w1 + (w2 - 180) + 5, y, 130, h);
        btnBuatEncounter.addActionListener(e -> doBuatEncounter());
        panelData.add(btnBuatEncounter);
        y += gap;

        // Row 2: No.Rawat | IHS Pasien
        addLabel("No.Rawat :", x1, y, w1, h);
        tNoRawat = roField(x2, y, w2, h);
        addLabel("IHS Pasien :", x2 + w2 + 20, y, w1, h);
        tIdPasienIhs = roField(x2 + w2 + 20 + w1, y, w2 - 60, h);
        y += gap;

        // Row 3: Pasien | IHS Dokter
        addLabel("Pasien :", x1, y, w1, h);
        tPasien = roField(x2, y, w2, h);
        addLabel("IHS Dokter :", x2 + w2 + 20, y, w1, h);
        tIdDokterIhs = roField(x2 + w2 + 20 + w1, y, w2 - 60, h);
        y += gap;

        // Row 4: Diagnosa Rujuk (KdPenyakit + NmPenyakit + tombol Pilih)
        addLabel("Diagnosa Rujuk :", x1, y, w1, h);
        tKdDiagnosaRujuk = new widget.TextBox();
        tKdDiagnosaRujuk.setBounds(x2, y, 80, h);
        tKdDiagnosaRujuk.setEditable(false);
        panelData.add(tKdDiagnosaRujuk);

        tNmDiagnosaRujuk = new widget.TextBox();
        tNmDiagnosaRujuk.setBounds(x2 + 85, y, w2 - 85 - 70, h);
        tNmDiagnosaRujuk.setEditable(false);
        panelData.add(tNmDiagnosaRujuk);

        btnPilihDiagnosa = new widget.Button();
        btnPilihDiagnosa.setText("Pilih");
        btnPilihDiagnosa.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/picture/Search-16x16.png")));
        btnPilihDiagnosa.setBounds(x2 + w2 - 65, y, 65, h);
        btnPilihDiagnosa.addActionListener(e -> doPilihDiagnosa());
        panelData.add(btnPilihDiagnosa);

        // Poli Rujuk di kolom kanan
        addLabel("Poli Rujuk :", x2 + w2 + 20, y, w1, h);
        tKdPoliRujuk = new widget.TextBox();
        tKdPoliRujuk.setBounds(x2 + w2 + 20 + w1, y, 80, h);
        tKdPoliRujuk.setEditable(false);
        panelData.add(tKdPoliRujuk);

        tNmPoliRujuk = new widget.TextBox();
        tNmPoliRujuk.setBounds(x2 + w2 + 20 + w1 + 85, y, w2 - 85 - 130, h);
        tNmPoliRujuk.setEditable(false);
        panelData.add(tNmPoliRujuk);

        btnPilihPoli = new widget.Button();
        btnPilihPoli.setText("Pilih");
        btnPilihPoli.setIcon(new javax.swing.ImageIcon(
                getClass().getResource("/picture/Search-16x16.png")));
        btnPilihPoli.setBounds(x2 + w2 + 20 + w1 + w2 - 125, y, 65, h);
        btnPilihPoli.addActionListener(e -> doPilihPoli());
        panelData.add(btnPilihPoli);
        y += gap;

        addLabel("Tgl.Rujukan :", x1, y, w1, h);
        dtTglRujukan = new widget.Tanggal();
        dtTglRujukan.setBounds(x2, y, 110, h);
        dtTglRujukan.setDisplayFormat("dd-MM-yyyy");
        dtTglRujukan.setDate(new Date());
        panelData.add(dtTglRujukan);

        addLabel("Tgl.Rencana :", x2 + 130, y, w1, h);
        dtTglRencana = new widget.Tanggal();
        dtTglRencana.setBounds(x2 + 130 + w1, y, 110, h);
        dtTglRencana.setDisplayFormat("dd-MM-yyyy");
        dtTglRencana.setDate(new Date());
        panelData.add(dtTglRencana);

        addLabel("Jns.Pelayanan :", x2 + w2 + 20, y, w1, h);
        cbJnsPelayanan = new widget.ComboBox();
        cbJnsPelayanan.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[]{"1. Rawat Inap", "2. Rawat Jalan"}));
        cbJnsPelayanan.setSelectedIndex(1);
        cbJnsPelayanan.setBounds(x2 + w2 + 20 + w1, y, 130, h);
        panelData.add(cbJnsPelayanan);
        y += gap;

        addLabel("Tipe Rujukan :", x1, y, w1, h);
        cbTipeRujukan = new widget.ComboBox();
        cbTipeRujukan.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[]{"0. Penuh", "1. Partial", "2. Rujuk Balik"}));
        cbTipeRujukan.setBounds(x2, y, 130, h);
        panelData.add(cbTipeRujukan);

        addLabel("Provinsi :", x2 + 150, y, w1, h);
        cbProvinsi = new widget.ComboBox();
        String[] provItems = new String[PROVINSI.length];
        for (int i = 0; i < PROVINSI.length; i++) {
            provItems[i] = PROVINSI[i][0] + " - " + PROVINSI[i][1];
        }
        cbProvinsi.setModel(new javax.swing.DefaultComboBoxModel<>(provItems));
        cbProvinsi.setBounds(x2 + 150 + w1, y, 200, h);
        panelData.add(cbProvinsi);
        y += gap;

        addLabel("Catatan :", x1, y, w1, h);
        taCatatan = new widget.TextArea();
        widget.ScrollPane sp1 = new widget.ScrollPane();
        sp1.setViewportView(taCatatan);
        sp1.setBounds(x2, y, 380, 50);
        panelData.add(sp1);

        addLabel("Keterangan :", x2 + 400, y, w1, h);
        taKeterangan = new widget.TextArea();
        widget.ScrollPane sp2 = new widget.ScrollPane();
        sp2.setViewportView(taKeterangan);
        sp2.setBounds(x2 + 400 + w1, y, 380, 50);
        panelData.add(sp2);
        y += 60;

        lblStatus = new widget.Label();
        lblStatus.setBounds(x1, y, 1080, h);
        lblStatus.setForeground(new java.awt.Color(0, 100, 0));
        lblStatus.setText("Status: siap. Klik [Cek Kriteria] untuk mulai.");
        panelData.add(lblStatus);

        frameMain.add(panelData, BorderLayout.PAGE_START);

        // ==================== TENGAH: 2 TABEL (kriteria + faskes) ====================
        widget.PanelBiasa panelTengah = new widget.PanelBiasa();
        panelTengah.setLayout(new java.awt.GridLayout(2, 1, 2, 2));

        // ---- Tabel Kriteria ----
        widget.InternalFrame frameKriteria = new widget.InternalFrame();
        frameKriteria.setBorder(BorderFactory.createTitledBorder("Kriteria Rujukan (isi jawaban di kolom paling kanan)"));
        frameKriteria.setLayout(new BorderLayout(1, 1));

        tblKriteria = new widget.Table();
        tblKriteria.setModel(modelKriteria);
        tblKriteria.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumn[] colsKriteria = {
                tblKriteria.getColumnModel().getColumn(0),
                tblKriteria.getColumnModel().getColumn(1),
                tblKriteria.getColumnModel().getColumn(2),
                tblKriteria.getColumnModel().getColumn(3)
        };
        colsKriteria[0].setPreferredWidth(70);
        colsKriteria[1].setPreferredWidth(400);
        colsKriteria[2].setPreferredWidth(80);
        colsKriteria[3].setPreferredWidth(400);
        tblKriteria.setDefaultRenderer(Object.class, new WarnaTable());

        scrollKriteria = new widget.ScrollPane();
        scrollKriteria.setViewportView(tblKriteria);
        frameKriteria.add(scrollKriteria, BorderLayout.CENTER);
        panelTengah.add(frameKriteria);

        // ---- Tabel Faskes ----
        widget.InternalFrame frameFaskes = new widget.InternalFrame();
        frameFaskes.setBorder(BorderFactory.createTitledBorder("Faskes Tujuan (centang salah satu)"));
        frameFaskes.setLayout(new BorderLayout(1, 1));

        tblFaskes = new widget.Table();
        tblFaskes.setModel(modelFaskes);
        tblFaskes.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widthsFaskes = {50, 130, 90, 320, 60, 150, 90, 80};
        for (int i = 0; i < widthsFaskes.length; i++) {
            tblFaskes.getColumnModel().getColumn(i).setPreferredWidth(widthsFaskes[i]);
        }
        tblFaskes.setDefaultRenderer(Object.class, new WarnaTable());

        // Kalau user centang baris, uncheck baris lain (single-select)
        modelFaskes.addTableModelListener(e -> {
            if (e.getColumn() == 0 && e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                Object val = modelFaskes.getValueAt(row, 0);
                if (Boolean.TRUE.equals(val)) {
                    for (int i = 0; i < modelFaskes.getRowCount(); i++) {
                        if (i != row) {
                            modelFaskes.setValueAt(Boolean.FALSE, i, 0);
                        }
                    }
                    selectedKdppkTujuan = String.valueOf(modelFaskes.getValueAt(row, 1));
                    selectedNmppkTujuan = String.valueOf(modelFaskes.getValueAt(row, 3));
                    setStatus("Faskes tujuan dipilih: " + selectedNmppkTujuan, false);
                } else {
                    selectedKdppkTujuan = "";
                    selectedNmppkTujuan = "";
                }
            }
        });

        scrollFaskes = new widget.ScrollPane();
        scrollFaskes.setViewportView(tblFaskes);
        frameFaskes.add(scrollFaskes, BorderLayout.CENTER);
        panelTengah.add(frameFaskes);

        frameMain.add(panelTengah, BorderLayout.CENTER);

        // ==================== PANEL TOMBOL ====================
        panelTombol = new widget.PanelBiasa();
        panelTombol.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));
        panelTombol.setPreferredSize(new Dimension(1100, 50));

        btnCekKriteria = mkBtn("Cek Kriteria", "/picture/Search-16x16.png");
        btnCekKriteria.addActionListener(e -> doCekKriteria());

        btnCariFaskes = mkBtn("Cari Faskes", "/picture/Search-16x16.png");
        btnCariFaskes.addActionListener(e -> doCariFaskes());

        btnKirim = mkBtn("Kirim Rujukan", "/picture/save-16x16.png");
        btnKirim.addActionListener(e -> doKirimRujukan());

        btnHapus = mkBtn("Hapus Rujukan", "/picture/stop_f2.png");
        btnHapus.addActionListener(e -> doHapusRujukan());

        btnTutup = mkBtn("Tutup", "/picture/exit.png");
        btnTutup.addActionListener(e -> dispose());

        panelTombol.add(btnCekKriteria);
        panelTombol.add(btnCariFaskes);
        panelTombol.add(btnKirim);
        panelTombol.add(btnHapus);
        panelTombol.add(btnTutup);

        frameMain.add(panelTombol, BorderLayout.PAGE_END);

        getContentPane().add(frameMain, BorderLayout.CENTER);
        pack();
    }

    private void addLabel(String text, int x, int y, int w, int h) {
        widget.Label l = new widget.Label();
        l.setText(text);
        l.setBounds(x, y, w, h);
        panelData.add(l);
    }

    private widget.TextBox roField(int x, int y, int w, int h) {
        widget.TextBox t = new widget.TextBox();
        t.setEditable(false);
        t.setBackground(new java.awt.Color(245, 250, 240));
        t.setBounds(x, y, w, h);
        panelData.add(t);
        return t;
    }

    private widget.Button mkBtn(String text, String icon) {
        widget.Button b = new widget.Button();
        b.setText(text);
        try {
            b.setIcon(new javax.swing.ImageIcon(getClass().getResource(icon)));
        } catch (Exception ignore) {}
        b.setPreferredSize(new Dimension(140, 30));
        b.setGlassColor(new java.awt.Color(255, 255, 255));
        return b;
    }

    private void setStatus(String msg, boolean error) {
        lblStatus.setText("Status: " + msg);
        lblStatus.setForeground(error ? new java.awt.Color(180, 0, 0) : new java.awt.Color(0, 100, 0));
        System.out.println("[BPJSRujukanSatuSehat] " + msg);
    }

    // =================================================================
    //  PUBLIC API - dipanggil oleh BPJSDataSEP setelah pilih SEP
    // =================================================================

    /**
     * Set data SEP dan terkait dari row tbDataSEP yang dipilih.
     * Kolom yang dipakai (sesuai struktur tbDataSEP di BPJSDataSEP.java):
     *   0 No.SEP, 1 No.Rawat, 2 No.RM, 3 Nama Pasien,
     *   13 Kode Diagnosa (ICD-10), 14 Nama Diagnosa,
     *   15 Kode Poli BPJS, 16 Nama Poli, 44 Kd DPJP
     */
    public void setDataSEP(String noSep, String noRawat, String noRm, String namaPasien,
                           String kdPenyakit, String nmPenyakit,
                           String kdPoli, String nmPoli, String kdDokterRs) {
        this.noSep = safe(noSep);
        this.noRawat = safe(noRawat);
        this.noRm = safe(noRm);
        this.namaPasien = safe(namaPasien);
        this.kdPenyakit = safe(kdPenyakit);
        this.nmPenyakit = safe(nmPenyakit);
        this.kdPoliBpjs = safe(kdPoli);
        this.nmPoli = safe(nmPoli);
        this.kdDokterRs = safe(kdDokterRs);

        // Tampilkan ke field
        tNoSep.setText(this.noSep);
        tNoRawat.setText(this.noRawat);
        tPasien.setText(this.noRm + " - " + this.namaPasien);

        // Default: diagnosa & poli rujukan = diagnosa & poli kunjungan (user bisa ganti)
        this.kdPenyakitRujuk = this.kdPenyakit;
        this.nmPenyakitRujuk = this.nmPenyakit;
        this.kdPoliRujuk = this.kdPoliBpjs;
        this.nmPoliRujuk = this.nmPoli;
        tKdDiagnosaRujuk.setText(this.kdPenyakitRujuk);
        tNmDiagnosaRujuk.setText(this.nmPenyakitRujuk);
        tKdPoliRujuk.setText(this.kdPoliRujuk);
        tNmPoliRujuk.setText(this.nmPoliRujuk);

        // Lookup data Satu Sehat
        loadDataSatuSehat();
    }

    private void loadDataSatuSehat() {
        // 1. Encounter Satu Sehat (dari satu_sehat_encounter)
        encounterRef = Sequel.cariIsi(
                "select id_encounter from satu_sehat_encounter where no_rawat=?",
                noRawat);
        tEncounter.setText(encounterRef);

        // 2. IHS Pasien (lookup dari NIK pasien)
        try {
            String noKtpPasien = Sequel.cariIsi(
                    "select pasien.no_ktp from pasien where pasien.no_rkm_medis=?",
                    noRm);
            if (noKtpPasien != null && !noKtpPasien.isEmpty()) {
                idPasienSatuSehat = cekViaSatuSehat.tampilIDPasien(noKtpPasien);
            }
        } catch (Exception e) {
            System.out.println("Gagal ambil IHS Pasien: " + e);
        }
        tIdPasienIhs.setText(idPasienSatuSehat);

        // 3. IHS Dokter (lookup dari NIK pegawai/dokter)
        try {
            String noKtpDokter = Sequel.cariIsi(
                    "select pegawai.no_ktp from pegawai where pegawai.nik=?",
                    kdDokterRs);
            if (noKtpDokter != null && !noKtpDokter.isEmpty()) {
                kdDokterSatuSehat = cekViaSatuSehat.tampilIDParktisi(noKtpDokter);
            }
        } catch (Exception e) {
            System.out.println("Gagal ambil IHS Dokter: " + e);
        }
        tIdDokterIhs.setText(kdDokterSatuSehat);

        // 4. Kode Spesialis untuk GetFaskesRujukan = kd_poli_bpjs
        //    (sesuai diskusi: pakai kode poli BPJS sebagai kodeSpesialis Sisrute)
        kodeSpesialis = kdPoliBpjs;

        // Validasi
        StringBuilder warn = new StringBuilder();
        if (encounterRef.isEmpty()) {
            warn.append("Encounter Satu Sehat belum ada untuk no_rawat ini. ");
        }
        if (idPasienSatuSehat.isEmpty()) {
            warn.append("IHS Pasien tidak ditemukan. ");
        }
        if (kdDokterSatuSehat.isEmpty()) {
            warn.append("IHS Dokter tidak ditemukan. ");
        }

        if (warn.length() > 0) {
            setStatus("PERINGATAN: " + warn.toString(), true);
            JOptionPane.showMessageDialog(this,
                    "Data Satu Sehat belum lengkap:\n" + warn.toString()
                            + "\nLengkapi data tersebut sebelum mengirim rujukan.",
                    "Data Tidak Lengkap", JOptionPane.WARNING_MESSAGE);
        } else {
            setStatus("Data Satu Sehat lengkap. Klik [Cek Kriteria] untuk mulai.", false);
        }
    }

    // =================================================================
    //  ACTION: PILIH DIAGNOSA RUJUKAN (popup BPJSCekReferensiPenyakit)
    // =================================================================
    private void doPilihDiagnosa() {
        popupPenyakit.setSize(900, 500);
        popupPenyakit.setLocationRelativeTo(this);
        popupPenyakit.setVisible(true);
        // hasil dipilih -> handler windowClosed di setupPopupListeners()
    }

    // =================================================================
    //  ACTION: PILIH POLI RUJUKAN (popup BPJSCekReferensiPoli)
    // =================================================================
    private void doPilihPoli() {
        popupPoli.setSize(900, 500);
        popupPoli.setLocationRelativeTo(this);
        popupPoli.setVisible(true);
        // hasil dipilih -> handler windowClosed di setupPopupListeners()
    }

    // =================================================================
    //  ACTION: BUAT ENCOUNTER BARU
    //  Langsung POST minimal Encounter ke Satu Sehat (tanpa buka form lain).
    //  Encounter akan disimpan ke satu_sehat_encounter dan field tEncounter
    //  langsung terisi.
    // =================================================================
    private void doBuatEncounter() {
        // Validasi prerequisite
        if (noRawat == null || noRawat.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No.Rawat belum ada. Pastikan SEP terpilih.");
            return;
        }
        if (idPasienSatuSehat == null || idPasienSatuSehat.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "IHS Pasien belum ada. Cek mapping NIK pasien ke Satu Sehat.");
            return;
        }
        if (kdDokterSatuSehat == null || kdDokterSatuSehat.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "IHS Dokter belum ada. Cek mapping NIK dokter ke Satu Sehat.");
            return;
        }

        // Cek lagi ke DB apakah encounter sudah ada (in case di-create di session lain)
        String existing = Sequel.cariIsi(
                "select id_encounter from satu_sehat_encounter where no_rawat=?",
                noRawat);
        if (existing != null && !existing.isEmpty()) {
            encounterRef = existing;
            tEncounter.setText(existing);
            JOptionPane.showMessageDialog(this,
                    "Encounter sudah ada untuk no_rawat ini:\n" + existing);
            return;
        }

        int conf = JOptionPane.showConfirmDialog(this,
                "Akan kirim Encounter baru ke Satu Sehat dengan data:\n\n"
                + "  Pasien    : " + namaPasien + "\n"
                + "  IHS       : " + idPasienSatuSehat + "\n"
                + "  Dokter    : " + kdDokterSatuSehat + "\n"
                + "  No.Rawat  : " + noRawat + "\n"
                + "  Periode   : " + new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "\n\n"
                + "Lanjutkan?",
                "Konfirmasi Buat Encounter",
                JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        setStatus("Mengirim Encounter ke Satu Sehat...", false);
        try {
            String idEncounter = postEncounterMinimal();
            if (idEncounter != null && !idEncounter.isEmpty()) {
                // Simpan dengan nama kolom EKSPLISIT. Versi positional ("?,?")
                // mengasumsikan satu_sehat_encounter tepat 2 kolom, padahal skemanya
                // berbeda antar deployment (sebagian punya id_episodeofcare_local) —
                // gagal dengan "Column count doesn't match value count" dan id
                // Encounter yang sudah terbuat di SatuSehat jadi hilang.
                Sequel.queryu2tf(
                        "insert into satu_sehat_encounter (no_rawat,id_encounter) values (?,?) "
                      + "on duplicate key update id_encounter=values(id_encounter)",
                        2, new String[]{noRawat, idEncounter});
                encounterRef = idEncounter;
                tEncounter.setText(idEncounter);
                setStatus("Encounter berhasil dibuat: " + idEncounter, false);
                JOptionPane.showMessageDialog(this,
                        "Encounter berhasil dibuat:\n" + idEncounter);
            } else {
                setStatus("Gagal buat Encounter (response kosong).", true);
            }
        } catch (Exception ex) {
            setStatus("Error: " + ex.getMessage(), true);
            JOptionPane.showMessageDialog(this,
                    "Gagal buat Encounter:\n" + ex.getMessage());
            ex.printStackTrace();
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * POST minimal Encounter ke Satu Sehat (tanpa diagnosis/treatment detail).
     * Cukup untuk dipakai di rujukan rajal.
     * @return id Encounter yang baru di-create, atau null kalau gagal
     */
    private String postEncounterMinimal() throws Exception {
        ApiSatuSehat ihs = new ApiSatuSehat();
        String orgId = ihs.getOrgIdPerujuk();
        String now   = ihs.getUtcDatetimeNow();
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date());

        // Build minimal Encounter JSON (status: in-progress, class: ambulatory)
        StringBuilder sb = new StringBuilder();
        sb.append("{")
          .append("\"resourceType\":\"Encounter\",")
          .append("\"status\":\"in-progress\",")
          .append("\"class\":{")
              .append("\"system\":\"http://terminology.hl7.org/CodeSystem/v3-ActCode\",")
              .append("\"code\":\"AMB\",")
              .append("\"display\":\"ambulatory\"")
          .append("},")
          .append("\"subject\":{")
              .append("\"reference\":\"Patient/").append(idPasienSatuSehat).append("\",")
              .append("\"display\":\"").append(escapeJson(namaPasien)).append("\"")
          .append("},")
          .append("\"participant\":[{")
              .append("\"type\":[{\"coding\":[{")
                  .append("\"system\":\"http://terminology.hl7.org/CodeSystem/v3-ParticipationType\",")
                  .append("\"code\":\"ATND\",")
                  .append("\"display\":\"attender\"")
              .append("}]}],")
              .append("\"individual\":{")
                  .append("\"reference\":\"Practitioner/").append(kdDokterSatuSehat).append("\"")
              .append("}")
          .append("}],")
          .append("\"period\":{")
              .append("\"start\":\"").append(today).append("T00:00:00+07:00\"")
          .append("},")
          .append("\"serviceProvider\":{")
              .append("\"reference\":\"Organization/").append(orgId).append("\"")
          .append("},")
          .append("\"identifier\":[{")
              .append("\"system\":\"http://sys-ids.kemkes.go.id/encounter/").append(orgId).append("\",")
              .append("\"value\":\"").append(noRawat).append("\"")
          .append("}]")
          .append("}");

        String body = sb.toString();
        String baseUrl = ihs.getBaseUrl();

        org.springframework.http.HttpHeaders h = ihs.buildAuthHeaders();
        h.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        org.springframework.http.HttpEntity<String> req = new org.springframework.http.HttpEntity<>(body, h);

        System.out.println("==== POST Encounter ====");
        System.out.println("URL : " + baseUrl + "/Encounter");
        System.out.println("Body: " + body);

        String json = ihs.getRest()
                .exchange(baseUrl + "/Encounter",
                          org.springframework.http.HttpMethod.POST,
                          req, String.class)
                .getBody();
        System.out.println("Response: " + json);

        JsonNode root = mapper.readTree(json);
        return root.path("id").asText();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r");
    }

    // =================================================================
    //  ACTION 1: CEK KRITERIA RUJUKAN
    //  POST /Rujukan/GetKriteriaRujukan
    // =================================================================
    private void doCekKriteria() {
        if (!validateBeforeApi()) return;

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        setStatus("Memanggil GetKriteriaRujukan...", false);
        try {
            JsonNode resp = sisrute.getKriteriaRujukan(
                    kodeFaskesSatuSehat, kdPenyakitRujuk, encounterRef);

            if (!SisruteService.isOk(resp)) {
                JOptionPane.showMessageDialog(this,
                        "Gagal: " + SisruteService.getMessage(resp),
                        "Sisrute", JOptionPane.ERROR_MESSAGE);
                setStatus("Gagal: " + SisruteService.getMessage(resp), true);
                return;
            }

            // Isi tabel kriteria
            modelKriteria.setRowCount(0);
            JsonNode arr = resp.path("response").path("kriteriaRujukan");
            if (arr.isArray()) {
                for (JsonNode k : arr) {
                    modelKriteria.addRow(new Object[]{
                            k.path("linkId").asText(),
                            k.path("text").asText(),
                            k.path("type").asText(),
                            ""  // user isi
                    });
                }
            }

            setStatus("Berhasil. Isi jawaban kriteria, lalu klik [Cari Faskes].", false);
            JOptionPane.showMessageDialog(this,
                    "Kriteria rujukan berhasil diambil (" + modelKriteria.getRowCount()
                            + " item).\nIsi jawaban di kolom paling kanan.\n\n"
                            + "Catatan format jawaban:\n"
                            + "- type=boolean → ketik: true / false\n"
                            + "- type=text    → ketik kode ICD-9-CM (mis. 01.24)",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            handleApiError(ex);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    // =================================================================
    //  ACTION 2: CARI FASKES TUJUAN
    //  POST /Sisrute/GetFaskesRujukan
    // =================================================================
    private void doCariFaskes() {
        if (!validateBeforeApi()) return;

        if (modelKriteria.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Klik [Cek Kriteria] dulu untuk mengambil daftar kriteria.",
                    "Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validasi semua jawaban kriteria sudah diisi
        for (int i = 0; i < modelKriteria.getRowCount(); i++) {
            String jwb = String.valueOf(modelKriteria.getValueAt(i, 3)).trim();
            if (jwb.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Jawaban kriteria belum lengkap (baris " + (i + 1) + ").",
                        "Info", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Build JSON kriteria item
        String kriteriaJson = buildKriteriaJsonItem();
        if (kriteriaJson == null) return;  // user error sudah ditampilkan

        // Ambil provinsi terpilih
        int idxProv = cbProvinsi.getSelectedIndex();
        String kdProv = PROVINSI[idxProv][0];
        String nmProv = PROVINSI[idxProv][1];

        // Tanggal rencana
        String tglRencana = Valid.SetTgl(dtTglRencana.getSelectedItem() + "");

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        setStatus("Memanggil GetFaskesRujukan...", false);
        try {
            JsonNode resp = sisrute.getFaskesRujukan(
                    kodeFaskesSatuSehat, kdPenyakitRujuk, kdPoliRujuk,
                    tglRencana, kriteriaJson,
                    kdProv, nmProv, "", "",
                    encounterRef);

            if (!SisruteService.isOk(resp)) {
                JOptionPane.showMessageDialog(this,
                        "Gagal: " + SisruteService.getMessage(resp),
                        "Sisrute", JOptionPane.ERROR_MESSAGE);
                setStatus("Gagal: " + SisruteService.getMessage(resp), true);
                return;
            }

            modelFaskes.setRowCount(0);
            selectedKdppkTujuan = "";
            selectedNmppkTujuan = "";

            JsonNode list = resp.path("response").path("list");
            int count = resp.path("response").path("count").asInt(0);
            if (list.isArray()) {
                for (JsonNode f : list) {
                    modelFaskes.addRow(new Object[]{
                            Boolean.FALSE,
                            f.path("kodeFaskesSatuSehat").asText(),
                            f.path("kdppk").asText(),
                            f.path("nmppk").asText(),
                            f.path("kelas").asText(),
                            f.path("nmkc").asText(),
                            f.path("distance").asText(),
                            f.path("persentase").asText()
                    });
                }
            }

            setStatus("Ditemukan " + count + " faskes. Centang salah satu.", false);

        } catch (Exception ex) {
            handleApiError(ex);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    /** Build kriteriaRujukan.item JSON dari isi tabel. */
    private String buildKriteriaJsonItem() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < modelKriteria.getRowCount(); i++) {
            String linkId = String.valueOf(modelKriteria.getValueAt(i, 0));
            String text = String.valueOf(modelKriteria.getValueAt(i, 1));
            String type = String.valueOf(modelKriteria.getValueAt(i, 2));
            String jwb = String.valueOf(modelKriteria.getValueAt(i, 3)).trim();

            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"linkId\":\"").append(linkId).append("\",")
              .append("\"text\":\"").append(escape(text)).append("\",")
              .append("\"answer\":[{");

            if ("boolean".equalsIgnoreCase(type)) {
                if (!"true".equalsIgnoreCase(jwb) && !"false".equalsIgnoreCase(jwb)) {
                    JOptionPane.showMessageDialog(this,
                            "Baris " + (i + 1) + ": jawaban tipe boolean harus 'true' atau 'false'.",
                            "Format Salah", JOptionPane.WARNING_MESSAGE);
                    return null;
                }
                sb.append("\"valueBoolean\":").append(jwb.toLowerCase());
            } else {
                sb.append("\"valueString\":\"").append(escape(jwb)).append("\"");
            }

            sb.append("}]}");
        }
        sb.append("]");
        return sb.toString();
    }

    // =================================================================
    //  ACTION 3: KIRIM RUJUKAN
    //  POST /Sisrute/postKunjungan
    // =================================================================
    private void doKirimRujukan() {
        if (!validateBeforeApi()) return;

        if (selectedKdppkTujuan.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Pilih dulu faskes tujuan dengan mencentang salah satu baris.",
                    "Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (modelKriteria.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Klik [Cek Kriteria] dulu, dan lengkapi jawabannya.",
                    "Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String kriteriaJson = buildKriteriaJsonItem();
        if (kriteriaJson == null) return;

        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Kirim rujukan untuk pasien " + namaPasien + "\nke " + selectedNmppkTujuan + " ?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) return;

        SisruteService.InsertRujukanRequest req = new SisruteService.InsertRujukanRequest();
        req.noSep = noSep;
        req.tglRujukan = Valid.SetTgl(dtTglRujukan.getSelectedItem() + "");
        req.tglRencanaKunjungan = Valid.SetTgl(dtTglRencana.getSelectedItem() + "");
        req.ppkDirujuk = selectedKdppkTujuan;  // pakai kode dari hasil GetFaskesRujukan
        req.jnsPelayanan = cbJnsPelayanan.getSelectedItem().toString().substring(0, 1);
        req.catatan = taCatatan.getText();
        req.diagRujukan = kdPenyakitRujuk;
        req.tipeRujukan = cbTipeRujukan.getSelectedItem().toString().substring(0, 1);
        req.poliRujukan = kdPoliRujuk;
        req.user = user;

        req.kodeFaskesSatuSehat = kodeFaskesSatuSehat;
        req.idPasienSatuSehat = idPasienSatuSehat;
        req.kdppkSatuSehatTujuanRujukan = selectedKdppkTujuan;
        req.kdDokterSatuSehat = kdDokterSatuSehat;
        req.encounterReference = encounterRef;
        req.patientInstruction = "Rujukan ke " + selectedNmppkTujuan;
        req.kriteriaJsonItem = kriteriaJson;
        req.keteranganRujukan = taKeterangan.getText().isEmpty()
                ? "Rujukan ke " + selectedNmppkTujuan
                : taKeterangan.getText();

        int idxProv = cbProvinsi.getSelectedIndex();
        req.kodePropinsi = PROVINSI[idxProv][0];
        req.namaPropinsi = PROVINSI[idxProv][1];
        req.kodeKabupaten = "";
        req.namaKabupaten = "";

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        setStatus("Mengirim rujukan...", false);
        try {
            JsonNode resp = sisrute.insertRujukan(req);

            if (!SisruteService.isOk(resp)) {
                JOptionPane.showMessageDialog(this,
                        "Gagal kirim: " + SisruteService.getMessage(resp),
                        "Sisrute", JOptionPane.ERROR_MESSAGE);
                setStatus("Gagal: " + SisruteService.getMessage(resp), true);
                return;
            }

            // Ambil noRujukan dari response
            JsonNode rujuk = resp.path("response").path("rujukan");
            String noRujukan = rujuk.path("noRujukan").asText();
            String noRujukanSatuSehat = rujuk.path("noRujukanSatuSehat").asText();
            String serviceRequestId = rujuk.path("serviceRequestId").asText();

            if (noRujukan == null || noRujukan.isEmpty()) {
                noRujukan = "RJK" + System.currentTimeMillis();  // fallback
            }

            // Simpan ke 3 tabel
            saveToDb(req, noRujukan, noRujukanSatuSehat, serviceRequestId);

            JOptionPane.showMessageDialog(this,
                    "Rujukan berhasil dikirim.\nNo Rujukan: " + noRujukan,
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            setStatus("Berhasil. No Rujukan: " + noRujukan, false);

        } catch (Exception ex) {
            handleApiError(ex);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void saveToDb(SisruteService.InsertRujukanRequest req,
                          String noRujukan,
                          String noRujukanSatuSehat,
                          String serviceRequestId) {
        try {
            // 1. bridging_rujukan_bpjs
            Sequel.menyimpantf2("bridging_rujukan_bpjs",
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?",
                    "No.Rujukan", 14, new String[]{
                            req.noSep, req.tglRujukan, req.tglRencanaKunjungan,
                            req.ppkDirujuk, selectedNmppkTujuan,
                            req.jnsPelayanan, req.catatan,
                            req.diagRujukan, nmPenyakit,
                            cbTipeRujukan.getSelectedItem().toString(),
                            req.poliRujukan, nmPoli,
                            noRujukan, user
                    });

            // 2. bridging_rujukan_satusehat
            Sequel.menyimpan("bridging_rujukan_satusehat",
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now()",
                    "No.Rujukan SatuSehat", 16, new String[]{
                            noRujukan,
                            noRujukanSatuSehat,
                            serviceRequestId,
                            req.kodeFaskesSatuSehat,
                            req.idPasienSatuSehat,
                            req.kdppkSatuSehatTujuanRujukan,
                            selectedNmppkTujuan,
                            req.kdDokterSatuSehat,
                            req.encounterReference,
                            req.patientInstruction,
                            req.keteranganRujukan,
                            req.kodePropinsi,
                            req.namaPropinsi,
                            req.kodeKabupaten,
                            req.namaKabupaten,
                            kdPoliRujuk
                    });

            // 3. bridging_kriteria_rujukan_satusehat
            for (int i = 0; i < modelKriteria.getRowCount(); i++) {
                String linkId = String.valueOf(modelKriteria.getValueAt(i, 0));
                String text   = String.valueOf(modelKriteria.getValueAt(i, 1));
                String tipe   = String.valueOf(modelKriteria.getValueAt(i, 2));
                String jwb    = String.valueOf(modelKriteria.getValueAt(i, 3)).trim();

                String valBool = "";
                String valStr = "";
                if ("boolean".equalsIgnoreCase(tipe)) {
                    valBool = "true".equalsIgnoreCase(jwb) ? "1" : "0";
                } else {
                    valStr = jwb;
                }

                Sequel.menyimpan("bridging_kriteria_rujukan_satusehat",
                        "?,?,?,?,?,?",
                        "Kriteria", 6, new String[]{
                                noRujukan, linkId, text, tipe, valBool, valStr
                        });
            }
        } catch (Exception ex) {
            System.out.println("Gagal simpan DB: " + ex);
            JOptionPane.showMessageDialog(this,
                    "Rujukan terkirim ke BPJS, tapi gagal simpan ke DB lokal:\n" + ex,
                    "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    }

    // =================================================================
    //  ACTION 4: HAPUS RUJUKAN
    //  DELETE /Rujukan/Delete
    // =================================================================
    private void doHapusRujukan() {
        // Cari rujukan terakhir untuk no_sep ini
        String noRujukan = Sequel.cariIsi(
                "select b.no_rujukan from bridging_rujukan_bpjs b "
                + "where b.no_sep=? order by b.tglRujukan desc limit 1", noSep);

        if (noRujukan == null || noRujukan.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Tidak ada rujukan tersimpan untuk SEP " + noSep,
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Hapus rujukan No. " + noRujukan + " ?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirmasi != JOptionPane.YES_OPTION) return;

        // Ambil data tambahan dari bridging_rujukan_satusehat
        String kdppkTujuan = "", patientInstr = "", keteranganRujuk = "";
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select kdppk_satusehat_tujuan, patient_instruction, keterangan_rujukan "
                + "from bridging_rujukan_satusehat where no_rujukan=?")) {
            ps.setString(1, noRujukan);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    kdppkTujuan = safe(rs.getString(1));
                    patientInstr = safe(rs.getString(2));
                    keteranganRujuk = safe(rs.getString(3));
                }
            }
        } catch (Exception e) {
            System.out.println("Gagal ambil data rujukan satusehat: " + e);
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        setStatus("Menghapus rujukan...", false);
        try {
            JsonNode resp = sisrute.deleteRujukan(
                    noRujukan, user,
                    kodeFaskesSatuSehat, idPasienSatuSehat,
                    kdppkTujuan, kdDokterSatuSehat,
                    encounterRef, patientInstr, keteranganRujuk);

            if (!SisruteService.isOk(resp)) {
                JOptionPane.showMessageDialog(this,
                        "Gagal hapus: " + SisruteService.getMessage(resp),
                        "Sisrute", JOptionPane.ERROR_MESSAGE);
                setStatus("Gagal: " + SisruteService.getMessage(resp), true);
                return;
            }

            // Hapus dari DB lokal
            Sequel.queryu2("delete from bridging_kriteria_rujukan_satusehat where no_rujukan=?",
                    1, new String[]{noRujukan});
            Sequel.queryu2("delete from bridging_rujukan_satusehat where no_rujukan=?",
                    1, new String[]{noRujukan});
            Sequel.meghapus("bridging_rujukan_bpjs", "no_rujukan", noRujukan);

            JOptionPane.showMessageDialog(this,
                    "Rujukan " + noRujukan + " berhasil dihapus.",
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
            setStatus("Berhasil hapus rujukan " + noRujukan, false);

        } catch (Exception ex) {
            handleApiError(ex);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    // =================================================================
    //  HELPERS
    // =================================================================

    private boolean validateBeforeApi() {
        if (noSep.isEmpty() || noRawat.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Data SEP belum di-set. Tutup form dan pilih SEP terlebih dahulu.",
                    "Info", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (encounterRef.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Encounter Satu Sehat belum ada untuk no_rawat ini.\n"
                    + "Silakan kirim Encounter dulu via form 'Satu Sehat Kirim Encounter'.",
                    "Data Tidak Lengkap", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (idPasienSatuSehat.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "IHS Pasien tidak ditemukan. Pastikan pasien sudah punya IHS di Satu Sehat.",
                    "Data Tidak Lengkap", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (kdDokterSatuSehat.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "IHS Dokter tidak ditemukan. Pastikan dokter sudah punya IHS di Satu Sehat.",
                    "Data Tidak Lengkap", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (kdPenyakitRujuk == null || kdPenyakitRujuk.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Diagnosa Rujuk belum dipilih. Klik tombol [Pilih] di sebelah Diagnosa Rujuk.",
                    "Info", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (kdPoliRujuk == null || kdPoliRujuk.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Poli Rujuk belum dipilih. Klik tombol [Pilih] di sebelah Poli Rujuk.",
                    "Info", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void handleApiError(Exception ex) {
        System.out.println("API error: " + ex);
        ex.printStackTrace();
        String msg = ex.toString();
        if (msg.contains("UnknownHostException")) {
            JOptionPane.showMessageDialog(this,
                    "Koneksi ke server BPJS Sisrute terputus.",
                    "Koneksi", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error: " + msg, "Error", JOptionPane.ERROR_MESSAGE);
        }
        setStatus("Error: " + msg, true);
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", " ");
    }

    /** Untuk standalone test. */
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            BPJSRujukanSatuSehat dlg = new BPJSRujukanSatuSehat(new javax.swing.JFrame(), true);
            // Contoh data dummy untuk tes UI
            dlg.setDataSEP(
                    "1001R0120126V000010", "2026/03/03/000123", "000123", "Budi Santoso",
                    "I10", "Essential (primary) hypertension",
                    "INT", "Penyakit Dalam",
                    "DR001"
            );
            dlg.setVisible(true);
        });
    }
}