/*
 * Registrasi Kanker SatuSehat — Fase 1 (INPUT & simpan lokal).
 * Belum mengirim ke SatuSehat (itu Fase 2). Lihat docs/registrasi-kanker-satusehat.md
 *
 * Struktur:
 *   - Worklist (atas): pasien dengan diagnosa ICD-10 kode C, filter tanggal/status/keyword.
 *     Pilih baris -> muat konteks pasien + data registrasi (bila sudah ada).
 *   - Form (tengah): diagnosis (ICD-10 + ICD-O topografi/morfologi + behavior/grade/lateralitas),
 *     staging TNM, registry (status episode + care manager), outcome.
 *   - Subtabel terapi (bawah): bedah/kemo/radiasi dst (registrasi_kanker_terapi).
 *
 * Simpan menulis ke registrasi_kanker (+ registrasi_kanker_terapi). Hand-built (tanpa .form).
 */
package bridging;

import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import kepegawaian.DlgCariPetugas;

public final class DlgRegistrasiKanker extends javax.swing.JDialog {

    private final sekuel Sequel = new sekuel();
    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();

    // Konteks pasien terpilih
    private String noRawat = "", noRkm = "", namaPasien = "", noKtp = "";
    private long idRegistrasi = 0; // 0 = belum tersimpan

    // ---- combo values (sinkron dgn ENUM di SQL) ----
    private static final String[] BEHAVIOR   = {"", "0 - Jinak", "1 - Uncertain", "2 - In situ", "3 - Malignant primer", "6 - Metastasis"};
    private static final String[] GRADE      = {"", "G1", "G2", "G3", "G4", "GX"};
    private static final String[] LATERAL    = {"", "kiri", "kanan", "bilateral", "tengah", "tidak diketahui"};
    private static final String[] DASAR      = {"", "klinis", "imaging", "sitologi", "histopatologi", "marker", "operasi", "lainnya"};
    private static final String[] STAGE_TYPE = {"", "klinis", "patologis"};
    private static final String[] STAGE_GRP  = {"", "0", "I", "II", "III", "IV", "IA", "IB", "IIA", "IIB", "IIIA", "IIIB", "IIIC", "IVA", "IVB"};
    private static final String[] STATUS_EOC  = {"waitlist", "active", "onhold", "finished", "cancelled"};
    private static final String[] VITAL      = {"", "hidup", "meninggal", "tidak diketahui"};
    private static final String[] T_JENIS    = {"bedah", "kemoterapi", "radioterapi", "hormonal", "imunoterapi", "targeted", "paliatif", "lainnya"};
    private static final String[] T_STATUS   = {"rencana", "berlangsung", "selesai", "dibatalkan"};
    private static final String[] T_RESPON   = {"", "CR", "PR", "SD", "PD", "tidak dinilai"};

    // Header
    private widget.InternalFrame internalFrame1;
    private widget.Label lblPasien;

    // Worklist
    private widget.Tanggal DTPCari1, DTPCari2;
    private widget.ComboBox cmbFilter;
    private widget.TextBox TCari;
    private widget.Button BtnRefresh;
    private widget.Table tbWork;
    private DefaultTableModel workMode;

    // Form input
    private widget.Tanggal DTPReg, DTPDiag, DTPStaging, DTPOutcome;
    private widget.TextBox txtIcd10Kd, txtIcd10Nm;
    private widget.TextBox txtTopoKd, txtTopoNm, txtMorfKd, txtMorfNm;
    private widget.ComboBox cmbBehavior, cmbGrade, cmbLateral, cmbDasar, cmbStageType, cmbStageGrp, cmbStatus, cmbVital;
    private widget.TextBox txtT, txtN, txtM, txtNoPa, txtSebab;
    private widget.TextBox txtCmNik, txtCmNama;
    private widget.TextArea txtKeterangan;

    // Terapi
    private widget.Table tbTerapi;
    private DefaultTableModel terapiMode;
    private widget.ComboBox cmbTJenis, cmbTStatus, cmbTRespon;
    private widget.TextBox txtTKode, txtTDesk;
    private widget.Tanggal DTPTMulai, DTPTSelesai;
    private javax.swing.JCheckBox chkTSelesai;

    // Buttons
    private widget.Button BtnSimpan, BtnKirim, BtnBaru, BtnHapus, BtnTutup;

    private DlgCariPetugas dlgPetugas;

    public DlgRegistrasiKanker(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle(":: Registrasi Kanker (SatuSehat) ::");
        java.awt.Dimension scr = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(scr.width, scr.height - 40);
        setLocation(0, 0);
        buildUI();
        emptyFields();
        tampilWorklist();
    }

    public void isCek() { /* no-op, konsistensi pattern */ }

    /**
     * Entry point saat dibuka dari dialog konteks pasien (DlgReg / DlgKamarInap /
     * DlgKasirRalan). Set konteks pasien, autofill ICD-10 kode C, dan muat
     * registrasi existing bila sudah ada.
     */
    public void setPasien(String noRawat, String noRkm, String nama) {
        this.noRawat = nz(noRawat).trim();
        this.noRkm = nz(noRkm).trim();
        this.namaPasien = nz(nama).trim();
        this.noKtp = lookupNoKtp(this.noRkm);
        this.idRegistrasi = 0;
        emptyFields();
        keepPatientHeader();
        autofillIcd10();
        long id = cariIdRegistrasi(this.noRawat);
        if (id > 0) muatRegistrasi(id);
        tampilWorklist();
    }

    private void autofillIcd10() {
        if (noRawat.isEmpty()) return;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select dp.kd_penyakit, ifnull(pk.nm_penyakit,'') nm from diagnosa_pasien dp "
              + "left join penyakit pk on pk.kd_penyakit = dp.kd_penyakit "
              + "where dp.no_rawat=? and dp.kd_penyakit like 'C%' order by dp.kd_penyakit limit 1")) {
            ps.setString(1, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { txtIcd10Kd.setText(nz(rs.getString(1))); txtIcd10Nm.setText(nz(rs.getString(2))); }
            }
        } catch (Exception ex) { System.out.println("Notif autofillIcd10 : " + ex); }
    }

    private long cariIdRegistrasi(String noRwt) {
        if (noRwt == null || noRwt.isEmpty()) return 0;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select id_registrasi from registrasi_kanker where no_rawat=? order by id_registrasi desc limit 1")) {
            ps.setString(1, noRwt);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getLong(1) : 0; }
        } catch (Exception ex) { return 0; }
    }

    private String lookupNoKtp(String noRm) {
        if (noRm == null || noRm.isEmpty()) return "";
        try (PreparedStatement ps = koneksi.prepareStatement("select no_ktp from pasien where no_rkm_medis=? limit 1")) {
            ps.setString(1, noRm);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? nz(rs.getString(1)) : ""; }
        } catch (Exception ex) { return ""; }
    }

    // ============================================================ UI
    private void buildUI() {
        internalFrame1 = new widget.InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(230, 220, 235)),
                "::[ Registrasi Kanker Satu Sehat ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(0, 4));

        internalFrame1.add(buildWorklist(), BorderLayout.NORTH);

        JScrollPane center = new JScrollPane(buildForm());
        center.getVerticalScrollBar().setUnitIncrement(16);
        center.setBorder(null);
        internalFrame1.add(center, BorderLayout.CENTER);

        widget.panelisi south = new widget.panelisi();
        south.setLayout(new BorderLayout(0, 2));
        south.add(buildTerapi(), BorderLayout.CENTER);
        south.add(buildButtonBar(), BorderLayout.SOUTH);
        internalFrame1.add(south, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(internalFrame1, BorderLayout.CENTER);

        dlgPetugas = new DlgCariPetugas(null, true);
        dlgPetugas.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                JTable t = dlgPetugas.getTable();
                if (t.getSelectedRow() != -1) {
                    txtCmNik.setText(t.getValueAt(t.getSelectedRow(), 0).toString());
                    txtCmNama.setText(t.getValueAt(t.getSelectedRow(), 1).toString());
                }
            }
        });
    }

    private widget.panelisi buildWorklist() {
        widget.panelisi p = new widget.panelisi();
        p.setLayout(new BorderLayout(0, 2));
        p.setPreferredSize(new Dimension(880, 230));

        lblPasien = new widget.Label();
        lblPasien.setText("Pasien: -");
        lblPasien.setFont(new Font("Tahoma", Font.BOLD, 12));
        lblPasien.setBorder(BorderFactory.createEmptyBorder(4, 8, 0, 0));
        p.add(lblPasien, BorderLayout.NORTH);

        widget.panelisi bar = new widget.panelisi();
        bar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 6));
        widget.Label l1 = new widget.Label(); l1.setText("Tgl:"); bar.add(l1);
        DTPCari1 = new widget.Tanggal(); DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setPreferredSize(new Dimension(120, 23)); bar.add(DTPCari1);
        widget.Label l2 = new widget.Label(); l2.setText("s/d"); bar.add(l2);
        DTPCari2 = new widget.Tanggal(); DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setPreferredSize(new Dimension(120, 23)); bar.add(DTPCari2);
        widget.Label l3 = new widget.Label(); l3.setText("Status:"); bar.add(l3);
        cmbFilter = new widget.ComboBox();
        cmbFilter.setModel(new DefaultComboBoxModel<>(new String[]{"Semua", "Belum diregistrasi", "Sudah diregistrasi"}));
        cmbFilter.setPreferredSize(new Dimension(150, 23)); bar.add(cmbFilter);
        widget.Label l4 = new widget.Label(); l4.setText("Cari:"); bar.add(l4);
        TCari = new widget.TextBox(); TCari.setPreferredSize(new Dimension(180, 23)); bar.add(TCari);
        BtnRefresh = new widget.Button(); BtnRefresh.setText("Tampilkan"); BtnRefresh.setPreferredSize(new Dimension(100, 23));
        BtnRefresh.addActionListener(e -> tampilWorklist()); bar.add(BtnRefresh);
        p.add(bar, BorderLayout.CENTER);

        workMode = new DefaultTableModel(null, new String[]{
                "No.Rawat", "No.RM", "Nama", "NIK", "ICD-10", "Diagnosa", "Tgl Registrasi", "Status Registrasi", "id_reg"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbWork = new widget.Table();
        tbWork.setModel(workMode);
        tbWork.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] w = {130, 90, 200, 120, 70, 240, 110, 130, 0};
        for (int i = 0; i < w.length; i++) tbWork.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        tbWork.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) pilihWorklist(); });
        widget.ScrollPane sc = new widget.ScrollPane();
        sc.setViewportView(tbWork);
        sc.setPreferredSize(new Dimension(880, 140));
        p.add(sc, BorderLayout.SOUTH);
        return p;
    }

    private widget.panelisi buildForm() {
        widget.panelisi p = new widget.panelisi();
        p.setLayout(null);
        p.setPreferredSize(new Dimension(940, 360));
        p.setBorder(BorderFactory.createTitledBorder(":: Data Onkologi ::"));
        Font f = new Font("Tahoma", 0, 11);
        int LX = 14, FX = 150, RX = 500, FX2 = 620, RH = 27;
        int y = 24;

        // Tgl registrasi / diagnosis
        addLabel(p, "Tgl Registrasi :", LX, y, 130, 22, f);
        DTPReg = mkTgl(); DTPReg.setBounds(FX, y, 120, 23); p.add(DTPReg);
        addLabel(p, "Tgl Diagnosis :", RX, y, 110, 22, f);
        DTPDiag = mkTgl(); DTPDiag.setBounds(FX2, y, 120, 23); p.add(DTPDiag);
        y += RH;

        // ICD-10
        addLabel(p, "Diagnosa ICD-10 :", LX, y, 130, 22, f);
        txtIcd10Kd = new widget.TextBox(); txtIcd10Kd.setBounds(FX, y, 80, 23); txtIcd10Kd.setEditable(false); p.add(txtIcd10Kd);
        txtIcd10Nm = new widget.TextBox(); txtIcd10Nm.setBounds(FX + 85, y, 620, 23); txtIcd10Nm.setEditable(false); p.add(txtIcd10Nm);
        y += RH;

        // Topografi ICD-O
        addLabel(p, "Topografi ICD-O :", LX, y, 130, 22, f);
        txtTopoKd = new widget.TextBox(); txtTopoKd.setBounds(FX, y, 80, 23); p.add(txtTopoKd);
        txtTopoNm = new widget.TextBox(); txtTopoNm.setBounds(FX + 85, y, 560, 23); txtTopoNm.setEditable(false); p.add(txtTopoNm);
        widget.Button bTopo = new widget.Button(); bTopo.setText("..."); bTopo.setBounds(FX + 648, y, 30, 23);
        bTopo.addActionListener(e -> cariICDO(DlgCariICDO.MODE_TOPOGRAFI)); p.add(bTopo);
        y += RH;

        // Morfologi ICD-O
        addLabel(p, "Morfologi ICD-O :", LX, y, 130, 22, f);
        txtMorfKd = new widget.TextBox(); txtMorfKd.setBounds(FX, y, 80, 23); p.add(txtMorfKd);
        txtMorfNm = new widget.TextBox(); txtMorfNm.setBounds(FX + 85, y, 560, 23); txtMorfNm.setEditable(false); p.add(txtMorfNm);
        widget.Button bMorf = new widget.Button(); bMorf.setText("..."); bMorf.setBounds(FX + 648, y, 30, 23);
        bMorf.addActionListener(e -> cariICDO(DlgCariICDO.MODE_MORFOLOGI)); p.add(bMorf);
        y += RH;

        // Behavior / Grade
        addLabel(p, "Behavior :", LX, y, 130, 22, f);
        cmbBehavior = mkCombo(BEHAVIOR); cmbBehavior.setBounds(FX, y, 200, 23); p.add(cmbBehavior);
        addLabel(p, "Grade :", RX, y, 110, 22, f);
        cmbGrade = mkCombo(GRADE); cmbGrade.setBounds(FX2, y, 120, 23); p.add(cmbGrade);
        y += RH;

        // Lateralitas / Dasar diagnosis
        addLabel(p, "Lateralitas :", LX, y, 130, 22, f);
        cmbLateral = mkCombo(LATERAL); cmbLateral.setBounds(FX, y, 200, 23); p.add(cmbLateral);
        addLabel(p, "Dasar Diagnosis :", RX, y, 110, 22, f);
        cmbDasar = mkCombo(DASAR); cmbDasar.setBounds(FX2, y, 200, 23); p.add(cmbDasar);
        y += RH;

        // No PA
        addLabel(p, "No. PA :", LX, y, 130, 22, f);
        txtNoPa = new widget.TextBox(); txtNoPa.setDocument(new batasInput((byte) 20).getKata(txtNoPa)); txtNoPa.setBounds(FX, y, 200, 23); p.add(txtNoPa);
        y += RH;

        // Staging: type / group
        addLabel(p, "Tipe Staging :", LX, y, 130, 22, f);
        cmbStageType = mkCombo(STAGE_TYPE); cmbStageType.setBounds(FX, y, 200, 23); p.add(cmbStageType);
        addLabel(p, "Stage Group :", RX, y, 110, 22, f);
        cmbStageGrp = mkCombo(STAGE_GRP); cmbStageGrp.setBounds(FX2, y, 120, 23); p.add(cmbStageGrp);
        y += RH;

        // TNM + tgl staging
        addLabel(p, "TNM (T / N / M) :", LX, y, 130, 22, f);
        txtT = new widget.TextBox(); txtT.setDocument(new batasInput((byte) 6).getKata(txtT)); txtT.setBounds(FX, y, 60, 23); p.add(txtT);
        txtN = new widget.TextBox(); txtN.setDocument(new batasInput((byte) 6).getKata(txtN)); txtN.setBounds(FX + 65, y, 60, 23); p.add(txtN);
        txtM = new widget.TextBox(); txtM.setDocument(new batasInput((byte) 6).getKata(txtM)); txtM.setBounds(FX + 130, y, 60, 23); p.add(txtM);
        addLabel(p, "Tgl Staging :", RX, y, 110, 22, f);
        DTPStaging = mkTgl(); DTPStaging.setBounds(FX2, y, 120, 23); p.add(DTPStaging);
        y += RH;

        // Status episode / Care manager
        addLabel(p, "Status Episode :", LX, y, 130, 22, f);
        cmbStatus = mkCombo(STATUS_EOC); cmbStatus.setBounds(FX, y, 200, 23); p.add(cmbStatus);
        addLabel(p, "Care Manager :", RX, y, 110, 22, f);
        txtCmNik = new widget.TextBox(); txtCmNik.setDocument(new batasInput((byte) 20).getKata(txtCmNik)); txtCmNik.setBounds(FX2, y, 100, 23); p.add(txtCmNik);
        txtCmNama = new widget.TextBox(); txtCmNama.setEditable(false); txtCmNama.setBounds(FX2 + 105, y, 150, 23); p.add(txtCmNama);
        widget.Button bCm = new widget.Button(); bCm.setText("..."); bCm.setBounds(FX2 + 258, y, 30, 23);
        bCm.addActionListener(e -> cariPetugas()); p.add(bCm);
        y += RH;

        // Outcome
        addLabel(p, "Status Vital :", LX, y, 130, 22, f);
        cmbVital = mkCombo(VITAL); cmbVital.setBounds(FX, y, 200, 23); p.add(cmbVital);
        addLabel(p, "Tgl Outcome :", RX, y, 110, 22, f);
        DTPOutcome = mkTgl(); DTPOutcome.setBounds(FX2, y, 120, 23); p.add(DTPOutcome);
        y += RH;

        addLabel(p, "Sebab Kematian :", LX, y, 130, 22, f);
        txtSebab = new widget.TextBox(); txtSebab.setDocument(new batasInput((byte) 150).getKata(txtSebab)); txtSebab.setBounds(FX, y, 545, 23); p.add(txtSebab);
        y += RH;

        // Keterangan
        addLabel(p, "Keterangan :", LX, y, 130, 22, f);
        txtKeterangan = new widget.TextArea(); txtKeterangan.setRows(2);
        JScrollPane spK = new JScrollPane(txtKeterangan);
        spK.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        spK.setBounds(FX, y, 545, 46); p.add(spK);
        y += 52;

        p.setPreferredSize(new Dimension(940, y + 10));
        return p;
    }

    private widget.panelisi buildTerapi() {
        widget.panelisi wrap = new widget.panelisi();
        wrap.setLayout(new BorderLayout(0, 2));
        wrap.setPreferredSize(new Dimension(880, 200));
        wrap.setBorder(BorderFactory.createTitledBorder(":: Terapi ::"));

        // Mini-form input terapi
        widget.panelisi bar = new widget.panelisi();
        bar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 4));
        widget.Label lj = new widget.Label(); lj.setText("Jenis:"); bar.add(lj);
        cmbTJenis = mkCombo(T_JENIS); cmbTJenis.setPreferredSize(new Dimension(120, 23)); bar.add(cmbTJenis);
        widget.Label lk = new widget.Label(); lk.setText("Kode:"); bar.add(lk);
        txtTKode = new widget.TextBox(); txtTKode.setPreferredSize(new Dimension(70, 23)); bar.add(txtTKode);
        widget.Label ld = new widget.Label(); ld.setText("Deskripsi:"); bar.add(ld);
        txtTDesk = new widget.TextBox(); txtTDesk.setPreferredSize(new Dimension(180, 23)); bar.add(txtTDesk);
        widget.Label lm = new widget.Label(); lm.setText("Mulai:"); bar.add(lm);
        DTPTMulai = mkTgl(); DTPTMulai.setPreferredSize(new Dimension(110, 23)); bar.add(DTPTMulai);
        chkTSelesai = new javax.swing.JCheckBox("Selesai:"); bar.add(chkTSelesai);
        DTPTSelesai = mkTgl(); DTPTSelesai.setPreferredSize(new Dimension(110, 23)); DTPTSelesai.setEnabled(false); bar.add(DTPTSelesai);
        chkTSelesai.addActionListener(e -> DTPTSelesai.setEnabled(chkTSelesai.isSelected()));
        widget.Label ls = new widget.Label(); ls.setText("Status:"); bar.add(ls);
        cmbTStatus = mkCombo(T_STATUS); cmbTStatus.setPreferredSize(new Dimension(100, 23)); bar.add(cmbTStatus);
        widget.Label lr = new widget.Label(); lr.setText("Respon:"); bar.add(lr);
        cmbTRespon = mkCombo(T_RESPON); cmbTRespon.setPreferredSize(new Dimension(100, 23)); bar.add(cmbTRespon);
        widget.Button bAdd = new widget.Button(); bAdd.setText("Tambah"); bAdd.addActionListener(e -> tambahTerapi()); bar.add(bAdd);
        widget.Button bDel = new widget.Button(); bDel.setText("Hapus Baris"); bDel.addActionListener(e -> hapusTerapiRow()); bar.add(bDel);
        wrap.add(bar, BorderLayout.NORTH);

        terapiMode = new DefaultTableModel(null, new String[]{
                "Jenis", "Kode", "Deskripsi", "Mulai", "Selesai", "Status", "Respon", "id_terapi"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbTerapi = new widget.Table();
        tbTerapi.setModel(terapiMode);
        tbTerapi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] tw = {110, 80, 240, 90, 90, 100, 90, 0};
        for (int i = 0; i < tw.length; i++) tbTerapi.getColumnModel().getColumn(i).setPreferredWidth(tw[i]);
        widget.ScrollPane sc = new widget.ScrollPane();
        sc.setViewportView(tbTerapi);
        wrap.add(sc, BorderLayout.CENTER);
        return wrap;
    }

    private widget.panelisi buildButtonBar() {
        widget.panelisi bar = new widget.panelisi();
        bar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 4));
        BtnSimpan = mkButton("Simpan", "Alt+S - Simpan registrasi (lokal)", 'S', "/picture/save-16x16.png", e -> simpan());
        BtnKirim  = mkButton("Kirim ke SatuSehat", "Alt+K - Kirim EpisodeOfCare/Condition/Procedure ke SatuSehat", 'K', "/picture/save-16x16.png", e -> kirimSatuSehat());
        BtnBaru   = mkButton("Baru",   "Alt+B - Kosongkan form",            'B', "/picture/Cancel-2-16x16.png", e -> { idRegistrasi = 0; emptyFields(); keepPatientHeader(); });
        BtnHapus  = mkButton("Hapus",  "Alt+H - Hapus registrasi terpilih", 'H', "/picture/stop_f2.png", e -> hapus());
        BtnTutup  = mkButton("Tutup",  "Alt+T - Tutup",                     'T', "/picture/exit.png", e -> dispose());
        bar.add(BtnSimpan); bar.add(BtnKirim); bar.add(BtnBaru); bar.add(BtnHapus); bar.add(BtnTutup);
        return bar;
    }

    // ============================================================ helpers UI
    private void addLabel(JPanel parent, String text, int x, int y, int w, int h, Font f) {
        widget.Label lbl = new widget.Label(); lbl.setText(text); lbl.setFont(f); lbl.setBounds(x, y, w, h); parent.add(lbl);
    }
    private widget.ComboBox mkCombo(String[] vals) {
        widget.ComboBox c = new widget.ComboBox(); c.setModel(new DefaultComboBoxModel<>(vals)); return c;
    }
    private widget.Tanggal mkTgl() {
        widget.Tanggal t = new widget.Tanggal(); t.setDisplayFormat("dd-MM-yyyy"); return t;
    }
    private widget.Button mkButton(String label, String tip, char mn, String icon, java.awt.event.ActionListener act) {
        widget.Button b = new widget.Button(); b.setText(label); b.setToolTipText(tip); b.setMnemonic(mn); b.setFont(new Font("Tahoma", 0, 11));
        if (icon != null) { try { b.setIcon(new javax.swing.ImageIcon(getClass().getResource(icon))); } catch (Exception ex) { } }
        b.addActionListener(act); return b;
    }

    private void cariPetugas() {
        dlgPetugas.emptTeks(); dlgPetugas.isCek();
        dlgPetugas.setSize(internalFrame1.getWidth() - 40, internalFrame1.getHeight() - 40);
        dlgPetugas.setLocationRelativeTo(internalFrame1);
        dlgPetugas.setVisible(true);
    }

    private void cariICDO(int mode) {
        final DlgCariICDO dlg = new DlgCariICDO(null, true, mode);
        dlg.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                JTable t = dlg.getTable();
                if (t.getSelectedRow() != -1) {
                    String kode = String.valueOf(t.getValueAt(t.getSelectedRow(), 0));
                    String nama = String.valueOf(t.getValueAt(t.getSelectedRow(), 1));
                    String beh  = String.valueOf(t.getValueAt(t.getSelectedRow(), 2));
                    if (mode == DlgCariICDO.MODE_TOPOGRAFI) {
                        txtTopoKd.setText(kode); txtTopoNm.setText(nama);
                    } else {
                        txtMorfKd.setText(kode); txtMorfNm.setText(nama);
                        if (beh != null && !beh.isEmpty() && !"null".equals(beh)) selectCombo(cmbBehavior, beh);
                    }
                }
            }
        });
        dlg.isCek();
        dlg.setLocationRelativeTo(internalFrame1);
        dlg.setVisible(true);
    }

    // ============================================================ worklist
    private void tampilWorklist() {
        Valid.tabelKosong(workMode);
        String d1 = Valid.SetTgl(DTPCari1.getSelectedItem() + "");
        String d2 = Valid.SetTgl(DTPCari2.getSelectedItem() + "");
        String filter = String.valueOf(cmbFilter.getSelectedItem());
        String kw = TCari.getText().trim();

        StringBuilder sql = new StringBuilder(
                "select dp.no_rawat, rp.no_rkm_medis, p.nm_pasien, ifnull(p.no_ktp,'') no_ktp, "
              + "min(dp.kd_penyakit) kd, ifnull(pk.nm_penyakit,'') diag, rp.tgl_registrasi, "
              + "ifnull(rk.status,'') stat, ifnull(rk.id_registrasi,0) id_reg "
              + "from diagnosa_pasien dp "
              + "join reg_periksa rp on rp.no_rawat = dp.no_rawat "
              + "join pasien p on p.no_rkm_medis = rp.no_rkm_medis "
              + "left join penyakit pk on pk.kd_penyakit = dp.kd_penyakit "
              + "left join registrasi_kanker rk on rk.no_rawat = dp.no_rawat "
              + "where dp.kd_penyakit like 'C%' and rp.tgl_registrasi between ? and ? ");
        if ("Belum diregistrasi".equals(filter)) sql.append("and rk.id_registrasi is null ");
        else if ("Sudah diregistrasi".equals(filter)) sql.append("and rk.id_registrasi is not null ");
        if (!kw.isEmpty()) sql.append("and (p.nm_pasien like ? or rp.no_rkm_medis like ? or dp.no_rawat like ?) ");
        sql.append("group by dp.no_rawat order by rp.tgl_registrasi desc, dp.no_rawat desc limit 500");

        try (PreparedStatement ps = koneksi.prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setString(idx++, d1);
            ps.setString(idx++, d2);
            if (!kw.isEmpty()) {
                ps.setString(idx++, "%" + kw + "%");
                ps.setString(idx++, "%" + kw + "%");
                ps.setString(idx++, "%" + kw + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    workMode.addRow(new Object[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("no_ktp"), rs.getString("kd"), rs.getString("diag"),
                        rs.getString("tgl_registrasi"), rs.getString("stat"), rs.getString("id_reg")
                    });
                }
            }
        } catch (Exception ex) {
            System.out.println("Notif tampilWorklist kanker : " + ex);
        }
    }

    private void pilihWorklist() {
        int r = tbWork.getSelectedRow();
        if (r < 0) return;
        noRawat    = str(workMode.getValueAt(r, 0));
        noRkm      = str(workMode.getValueAt(r, 1));
        namaPasien = str(workMode.getValueAt(r, 2));
        noKtp      = str(workMode.getValueAt(r, 3));
        String icd = str(workMode.getValueAt(r, 4));
        String diag = str(workMode.getValueAt(r, 5));
        long idReg = parseLong(str(workMode.getValueAt(r, 8)));

        emptyFields();
        keepPatientHeader();
        txtIcd10Kd.setText(icd);
        txtIcd10Nm.setText(diag);

        if (idReg > 0) muatRegistrasi(idReg);
    }

    private void keepPatientHeader() {
        lblPasien.setText("Pasien: " + namaPasien + "  |  No.RM: " + noRkm + "  |  No.Rawat: " + noRawat
                + (noKtp.isEmpty() ? "  |  (NIK kosong)" : ""));
    }

    // ============================================================ load/save
    private void muatRegistrasi(long idReg) {
        try (PreparedStatement ps = koneksi.prepareStatement("select * from registrasi_kanker where id_registrasi=?")) {
            ps.setLong(1, idReg);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idRegistrasi = idReg;
                    setTgl(DTPReg, rs.getString("tgl_registrasi"));
                    setTgl(DTPDiag, rs.getString("tgl_diagnosis"));
                    txtIcd10Kd.setText(nz(rs.getString("kd_penyakit")));
                    txtTopoKd.setText(nz(rs.getString("icdo_topografi")));
                    txtTopoNm.setText(nz(rs.getString("topografi_nama")));
                    txtMorfKd.setText(nz(rs.getString("icdo_morfologi")));
                    txtMorfNm.setText(nz(rs.getString("morfologi_nama")));
                    selectByCode(cmbBehavior, nz(rs.getString("behavior")));
                    selectCombo(cmbGrade, nz(rs.getString("grade")));
                    selectCombo(cmbLateral, nz(rs.getString("lateralitas")));
                    selectCombo(cmbDasar, nz(rs.getString("dasar_diagnosis")));
                    txtNoPa.setText(nz(rs.getString("no_pa")));
                    selectCombo(cmbStageType, nz(rs.getString("stage_type")));
                    selectCombo(cmbStageGrp, nz(rs.getString("stage_group")));
                    txtT.setText(nz(rs.getString("tnm_t")));
                    txtN.setText(nz(rs.getString("tnm_n")));
                    txtM.setText(nz(rs.getString("tnm_m")));
                    setTgl(DTPStaging, rs.getString("tgl_staging"));
                    selectCombo(cmbStatus, nz(rs.getString("status")));
                    txtCmNik.setText(nz(rs.getString("kd_dokter")));
                    txtCmNama.setText(lookupNamaPegawai(nz(rs.getString("kd_dokter"))));
                    selectCombo(cmbVital, nz(rs.getString("status_vital")));
                    setTgl(DTPOutcome, rs.getString("tgl_outcome"));
                    txtSebab.setText(nz(rs.getString("sebab_kematian")));
                    txtKeterangan.setText(nz(rs.getString("keterangan")));
                }
            }
        } catch (Exception ex) {
            System.out.println("Notif muatRegistrasi : " + ex);
        }
        muatTerapi(idReg);
    }

    private void muatTerapi(long idReg) {
        Valid.tabelKosong(terapiMode);
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select id_terapi, jenis, ifnull(kode_tindakan,'') kode, ifnull(deskripsi,'') deskripsi, "
              + "ifnull(tgl_mulai,'') mulai, ifnull(tgl_selesai,'') selesai, status, ifnull(respon,'') respon "
              + "from registrasi_kanker_terapi where id_registrasi=? order by id_terapi")) {
            ps.setLong(1, idReg);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    terapiMode.addRow(new Object[]{
                        rs.getString("jenis"), rs.getString("kode"), rs.getString("deskripsi"),
                        rs.getString("mulai"), rs.getString("selesai"), rs.getString("status"),
                        rs.getString("respon"), rs.getString("id_terapi")
                    });
                }
            }
        } catch (Exception ex) {
            System.out.println("Notif muatTerapi : " + ex);
        }
    }

    private void tambahTerapi() {
        String mulai = Valid.SetTgl(DTPTMulai.getSelectedItem() + "");
        String selesai = chkTSelesai.isSelected() ? Valid.SetTgl(DTPTSelesai.getSelectedItem() + "") : "";
        terapiMode.addRow(new Object[]{
            String.valueOf(cmbTJenis.getSelectedItem()),
            txtTKode.getText().trim(),
            txtTDesk.getText().trim(),
            mulai, selesai,
            String.valueOf(cmbTStatus.getSelectedItem()),
            String.valueOf(cmbTRespon.getSelectedItem()),
            "0"
        });
        txtTKode.setText(""); txtTDesk.setText(""); chkTSelesai.setSelected(false); DTPTSelesai.setEnabled(false);
    }

    private void hapusTerapiRow() {
        int r = tbTerapi.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Pilih baris terapi dulu."); return; }
        terapiMode.removeRow(r);
    }

    private void simpan() {
        if (noRawat.isEmpty() || noRkm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pilih pasien dulu dari worklist di atas.");
            return;
        }
        String tglReg = Valid.SetTgl(DTPReg.getSelectedItem() + "");
        try {
            if (idRegistrasi == 0) {
                String sql = "insert into registrasi_kanker (no_rkm_medis,no_rawat,tgl_registrasi,tgl_diagnosis,"
                        + "kd_penyakit,icdo_topografi,topografi_nama,icdo_morfologi,morfologi_nama,behavior,grade,"
                        + "lateralitas,dasar_diagnosis,no_pa,stage_type,tnm_t,tnm_n,tnm_m,stage_group,tgl_staging,"
                        + "status,kd_dokter,status_vital,tgl_outcome,sebab_kematian,keterangan) "
                        + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                try (PreparedStatement ps = koneksi.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    bindRegistrasi(ps, tglReg);
                    ps.executeUpdate();
                    try (ResultSet gk = ps.getGeneratedKeys()) { if (gk.next()) idRegistrasi = gk.getLong(1); }
                }
            } else {
                String sql = "update registrasi_kanker set tgl_registrasi=?,tgl_diagnosis=?,kd_penyakit=?,"
                        + "icdo_topografi=?,topografi_nama=?,icdo_morfologi=?,morfologi_nama=?,behavior=?,grade=?,"
                        + "lateralitas=?,dasar_diagnosis=?,no_pa=?,stage_type=?,tnm_t=?,tnm_n=?,tnm_m=?,stage_group=?,"
                        + "tgl_staging=?,status=?,kd_dokter=?,status_vital=?,tgl_outcome=?,sebab_kematian=?,keterangan=? "
                        + "where id_registrasi=?";
                try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
                    int i = bindRegistrasiUpdate(ps, tglReg);
                    ps.setLong(i, idRegistrasi);
                    ps.executeUpdate();
                }
            }
            simpanTerapi();
            JOptionPane.showMessageDialog(this, "Registrasi kanker tersimpan (ID: " + idRegistrasi + ").");
            tampilWorklist();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal simpan: " + ex.getMessage());
            System.out.println("Notif simpan kanker : " + ex);
        }
    }

    /** Isi 26 param insert (kolom no_rkm_medis..keterangan). */
    private void bindRegistrasi(PreparedStatement ps, String tglReg) throws Exception {
        ps.setString(1, noRkm);
        ps.setString(2, noRawat);
        ps.setString(3, tglReg);
        setDateOrNull(ps, 4, DTPDiag);
        setStrOrNull(ps, 5, txtIcd10Kd.getText());
        setStrOrNull(ps, 6, txtTopoKd.getText());
        setStrOrNull(ps, 7, txtTopoNm.getText());
        setStrOrNull(ps, 8, txtMorfKd.getText());
        setStrOrNull(ps, 9, txtMorfNm.getText());
        setStrOrNull(ps, 10, behaviorCode());
        setComboOrNull(ps, 11, cmbGrade);
        setComboOrNull(ps, 12, cmbLateral);
        setComboOrNull(ps, 13, cmbDasar);
        setStrOrNull(ps, 14, txtNoPa.getText());
        setComboOrNull(ps, 15, cmbStageType);
        setStrOrNull(ps, 16, txtT.getText());
        setStrOrNull(ps, 17, txtN.getText());
        setStrOrNull(ps, 18, txtM.getText());
        setComboOrNull(ps, 19, cmbStageGrp);
        setDateOrNull(ps, 20, DTPStaging);
        ps.setString(21, String.valueOf(cmbStatus.getSelectedItem()));
        setStrOrNull(ps, 22, txtCmNik.getText());
        setComboOrNull(ps, 23, cmbVital);
        setDateOrNull(ps, 24, DTPOutcome);
        setStrOrNull(ps, 25, txtSebab.getText());
        setStrOrNull(ps, 26, txtKeterangan.getText());
    }

    /** Sama seperti insert tapi mulai param 1 = tgl_registrasi (tanpa no_rkm/no_rawat). Return index param berikutnya. */
    private int bindRegistrasiUpdate(PreparedStatement ps, String tglReg) throws Exception {
        ps.setString(1, tglReg);
        setDateOrNull(ps, 2, DTPDiag);
        setStrOrNull(ps, 3, txtIcd10Kd.getText());
        setStrOrNull(ps, 4, txtTopoKd.getText());
        setStrOrNull(ps, 5, txtTopoNm.getText());
        setStrOrNull(ps, 6, txtMorfKd.getText());
        setStrOrNull(ps, 7, txtMorfNm.getText());
        setStrOrNull(ps, 8, behaviorCode());
        setComboOrNull(ps, 9, cmbGrade);
        setComboOrNull(ps, 10, cmbLateral);
        setComboOrNull(ps, 11, cmbDasar);
        setStrOrNull(ps, 12, txtNoPa.getText());
        setComboOrNull(ps, 13, cmbStageType);
        setStrOrNull(ps, 14, txtT.getText());
        setStrOrNull(ps, 15, txtN.getText());
        setStrOrNull(ps, 16, txtM.getText());
        setComboOrNull(ps, 17, cmbStageGrp);
        setDateOrNull(ps, 18, DTPStaging);
        ps.setString(19, String.valueOf(cmbStatus.getSelectedItem()));
        setStrOrNull(ps, 20, txtCmNik.getText());
        setComboOrNull(ps, 21, cmbVital);
        setDateOrNull(ps, 22, DTPOutcome);
        setStrOrNull(ps, 23, txtSebab.getText());
        setStrOrNull(ps, 24, txtKeterangan.getText());
        return 25;
    }

    private void simpanTerapi() throws Exception {
        try (PreparedStatement del = koneksi.prepareStatement("delete from registrasi_kanker_terapi where id_registrasi=?")) {
            del.setLong(1, idRegistrasi); del.executeUpdate();
        }
        String ins = "insert into registrasi_kanker_terapi (id_registrasi,jenis,kode_tindakan,deskripsi,tgl_mulai,tgl_selesai,status,respon,kd_dokter) values (?,?,?,?,?,?,?,?,?)";
        for (int r = 0; r < terapiMode.getRowCount(); r++) {
            try (PreparedStatement ps = koneksi.prepareStatement(ins)) {
                ps.setLong(1, idRegistrasi);
                ps.setString(2, str(terapiMode.getValueAt(r, 0)));
                setStrOrNull(ps, 3, str(terapiMode.getValueAt(r, 1)));
                setStrOrNull(ps, 4, str(terapiMode.getValueAt(r, 2)));
                setStrOrNull(ps, 5, str(terapiMode.getValueAt(r, 3)));
                setStrOrNull(ps, 6, str(terapiMode.getValueAt(r, 4)));
                ps.setString(7, str(terapiMode.getValueAt(r, 5)));
                setStrOrNull(ps, 8, str(terapiMode.getValueAt(r, 6)));
                setStrOrNull(ps, 9, txtCmNik.getText());
                ps.executeUpdate();
            }
        }
    }

    private void kirimSatuSehat() {
        if (!fungsi.akses.getregistrasi_kanker()) {
            JOptionPane.showMessageDialog(this, "Anda tidak memiliki hak akses untuk Registrasi Kanker.");
            return;
        }
        if (idRegistrasi == 0) {
            JOptionPane.showMessageDialog(this, "Simpan registrasi dulu sebelum dikirim ke SatuSehat.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
                "Kirim registrasi kanker ini ke SatuSehat?\n(Encounter kunjungan harus sudah terkirim lebih dulu.)",
                "Konfirmasi Kirim", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        final long id = idRegistrasi;
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        BtnKirim.setEnabled(false);
        new Thread(() -> {
            final String hasil = SatuSehatKirimKanker.kirim(id);
            javax.swing.SwingUtilities.invokeLater(() -> {
                setCursor(java.awt.Cursor.getDefaultCursor());
                BtnKirim.setEnabled(true);
                javax.swing.JTextArea ta = new javax.swing.JTextArea(hasil, 16, 60);
                ta.setEditable(false);
                JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Hasil Kirim SatuSehat", JOptionPane.INFORMATION_MESSAGE);
                tampilWorklist();
            });
        }, "kirim-kanker-satusehat").start();
    }

    private void hapus() {
        if (idRegistrasi == 0) { JOptionPane.showMessageDialog(this, "Belum ada registrasi tersimpan untuk pasien ini."); return; }
        int ok = JOptionPane.showConfirmDialog(this, "Hapus registrasi kanker ini beserta data terapinya?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            try (PreparedStatement p1 = koneksi.prepareStatement("delete from registrasi_kanker_terapi where id_registrasi=?")) {
                p1.setLong(1, idRegistrasi); p1.executeUpdate();
            }
            try (PreparedStatement p2 = koneksi.prepareStatement("delete from registrasi_kanker where id_registrasi=?")) {
                p2.setLong(1, idRegistrasi); p2.executeUpdate();
            }
            idRegistrasi = 0;
            emptyFields(); keepPatientHeader();
            tampilWorklist();
            JOptionPane.showMessageDialog(this, "Registrasi dihapus.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal hapus: " + ex.getMessage());
        }
    }

    // ============================================================ util
    private void emptyFields() {
        String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        setTgl(DTPReg, today); setTgl(DTPDiag, today); setTgl(DTPStaging, today); setTgl(DTPOutcome, today);
        txtIcd10Kd.setText(""); txtIcd10Nm.setText("");
        txtTopoKd.setText(""); txtTopoNm.setText(""); txtMorfKd.setText(""); txtMorfNm.setText("");
        cmbBehavior.setSelectedIndex(0); cmbGrade.setSelectedIndex(0); cmbLateral.setSelectedIndex(0);
        cmbDasar.setSelectedIndex(0); cmbStageType.setSelectedIndex(0); cmbStageGrp.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0); cmbVital.setSelectedIndex(0);
        txtT.setText(""); txtN.setText(""); txtM.setText(""); txtNoPa.setText(""); txtSebab.setText("");
        txtKeterangan.setText("");
        String kd = akses.getkode();
        String nm = lookupNamaPegawai(kd);
        if (!nm.isEmpty()) { txtCmNik.setText(kd); txtCmNama.setText(nm); } else { txtCmNik.setText(""); txtCmNama.setText(""); }
        Valid.tabelKosong(terapiMode);
    }

    private String behaviorCode() {
        String s = String.valueOf(cmbBehavior.getSelectedItem());
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, 1); // "3 - Malignant primer" -> "3"
    }

    private void selectByCode(widget.ComboBox cmb, String code) {
        if (code == null || code.isEmpty()) { cmb.setSelectedIndex(0); return; }
        for (int i = 0; i < cmb.getItemCount(); i++) {
            String it = String.valueOf(cmb.getItemAt(i));
            if (!it.isEmpty() && it.substring(0, 1).equals(code)) { cmb.setSelectedIndex(i); return; }
        }
    }
    private void selectCombo(widget.ComboBox cmb, String val) {
        if (val == null) { cmb.setSelectedIndex(0); return; }
        for (int i = 0; i < cmb.getItemCount(); i++) {
            if (String.valueOf(cmb.getItemAt(i)).equals(val)) { cmb.setSelectedIndex(i); return; }
        }
        cmb.setSelectedIndex(0);
    }

    private void setTgl(widget.Tanggal dtp, String ymd) {
        if (ymd == null || ymd.isEmpty() || ymd.startsWith("0000") || "null".equals(ymd)) return;
        Valid.SetTgl(dtp, ymd.length() >= 10 ? ymd.substring(0, 10) : ymd);
    }
    private void setStrOrNull(PreparedStatement ps, int idx, String v) throws Exception {
        if (v == null || v.trim().isEmpty()) ps.setNull(idx, java.sql.Types.VARCHAR); else ps.setString(idx, v.trim());
    }
    private void setComboOrNull(PreparedStatement ps, int idx, widget.ComboBox cmb) throws Exception {
        String v = String.valueOf(cmb.getSelectedItem());
        if (v == null || v.trim().isEmpty()) ps.setNull(idx, java.sql.Types.VARCHAR); else ps.setString(idx, v.trim());
    }
    private void setDateOrNull(PreparedStatement ps, int idx, widget.Tanggal dtp) throws Exception {
        String d = Valid.SetTgl(dtp.getSelectedItem() + "");
        if (d == null || d.isEmpty() || d.startsWith("0000")) ps.setNull(idx, java.sql.Types.DATE); else ps.setString(idx, d);
    }

    private String lookupNamaPegawai(String nik) {
        if (nik == null || nik.isEmpty()) return "";
        try (PreparedStatement ps = koneksi.prepareStatement("select nama from pegawai where nik=? limit 1")) {
            ps.setString(1, nik);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? nz(rs.getString(1)) : ""; }
        } catch (Exception ex) { return ""; }
    }

    private static String str(Object o) { return o == null ? "" : o.toString(); }
    private static String nz(String s) { return s == null ? "" : s; }
    private static long parseLong(String s) { try { return Long.parseLong(s.trim()); } catch (Exception e) { return 0; } }
}
