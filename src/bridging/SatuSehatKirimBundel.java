/*
  Kirim Bundel SatuSehat — 3 tab: Rawat Jalan, IGD, Rawat Inap.
  Tiap tab: kiri = tabel kunjungan, kanan = detail pasien + tombol per-resource + Kirim Bundel.
  Klik row tabel -> detail tampil di panel kanan.
  By Mas Elkhanza
 */
package bridging;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.html.HTMLEditorKit;

public final class SatuSehatKirimBundel extends javax.swing.JDialog {

    private final String[] KAT = {"Rawat Jalan", "IGD", "Rawat Inap"};
    private final DefaultTableModel[] models = new DefaultTableModel[3];
    private final widget.Table[] tables = new widget.Table[3];
    private final widget.Label[] counts = new widget.Label[3];
    private final String[] selNoRawat = {"", "", ""};
    /** Berkas PDF/gambar yang dipilih petugas untuk dikonversi jadi DICOM, per tab. */
    // detail labels per tab: [tab][field] field: 0 norawat 1 pasien 2 nik 3 poli 4 tgl 5 enc 6 diag 7 proc
    private final widget.Label[][] det = new widget.Label[3][9];
    private final widget.editorpane[] logPane = new widget.editorpane[3];
    private final widget.editorpane[] statusKirim = new widget.editorpane[3];
    private final widget.Button[] btnEnc = new widget.Button[3];
    private final widget.Button[] btnCond = new widget.Button[3];
    private final widget.Button[] btnProc = new widget.Button[3];
    private final widget.Button[] btnBundel = new widget.Button[3];
    private final widget.Button[] btnSEP = new widget.Button[3];
    private final widget.Button[] btnKirimNow = new widget.Button[3];
    private final widget.Button[] btnUpdateNow = new widget.Button[3];
    private final String[] pendingType = {"", "", ""};
    private final String[] pendingLabel = {"", "", ""};
    // Semua tombol yang BERAKIBAT KIRIM, per tab — dipakai isCek() supaya hak akses
    // menjaga seluruh tombol, bukan hanya Encounter/Condition/Procedure/Bundel.
    @SuppressWarnings("unchecked")
    private final java.util.ArrayList<widget.Button>[] btnSemua = new java.util.ArrayList[3];
    /** Cermin akses.getsatu_sehat_kirim_encounter(); dicek ulang saat aksi berjalan. */
    private boolean izinKirim = true;

    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps; private ResultSet rs;
    private int i = 0;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean busy = false;
    private java.awt.Frame parent;

    public SatuSehatKirimBundel(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.parent = parent;
        initComponents();
        this.setLocation(10, 2);
        setSize(1000, 720);
        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));
        cmbStatus.addActionListener(e -> runBackground(this::tampilSemua));
    }

    private widget.Label mkLabel(String txt, boolean bold) {
        widget.Label l = new widget.Label(); l.setText(txt);
        l.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        if (bold) l.setFont(new Font("Tahoma", 1, 11));
        return l;
    }

    /** Tombol yang membuka form sender khusus (kategori-spesifik). */
    /** Tombol Lab: memicu preview lab kunjungan; pengiriman multi-resource dibahas kemudian. */
    private widget.Button mkLabBtn(int t, Color glass) {
        widget.Button b = new widget.Button();
        b.setText("Lab");
        try { b.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/1290990_doctor_drug_healthcare_medical_medicine_icon.png"))); } catch (Exception ignored) {}
        b.setGlassColor(glass);
        b.addActionListener(e -> siapkan(t, "LAB", "Lab"));
        return b;
    }

    private widget.Button mkFormBtn(String text, String iconPath, Color glass, Runnable action) {
        widget.Button b = new widget.Button();
        b.setText(text);
        try { b.setIcon(new javax.swing.ImageIcon(getClass().getResource(iconPath))); } catch (Exception ignored) {}
        b.setGlassColor(glass);
        b.addActionListener(e -> action.run());
        return b;
    }

    /** Tombol placeholder — aksinya akan diatur berikutnya. */
    private widget.Button mkStubBtn(int tab, String text, String iconPath, Color glass) {
        return mkFormBtn(text, iconPath, glass, () -> SwingUtilities.invokeLater(() -> logPane[tab].setText(
                "<html><body style='font:11px tahoma'><b>" + text + "</b><br><br>"
              + "<i>Tombol sudah disiapkan. Aksi pengiriman ke SatuSehat akan diatur berikutnya.</i></body></html>")));
    }

    /** Buka form sender sebagai child dialog. */
    private void showForm(javax.swing.JDialog a) {
        a.setSize(this.getWidth() - 40, this.getHeight() - 40);
        a.setLocationRelativeTo(this);
        a.setVisible(true);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        internalFrame1 = new widget.InternalFrame();
        panelAtas = new widget.panelisi();
        jLabel1 = new widget.Label(); DTPCari1 = new widget.Tanggal();
        jLabel2 = new widget.Label(); DTPCari2 = new widget.Tanggal();
        jLabel3 = new widget.Label(); TCari = new widget.TextBox(); BtnCari = new widget.Button();
        jLabel4 = new widget.Label(); cmbStatus = new widget.ComboBox();
        TabKategori = new javax.swing.JTabbedPane();
        panelBawah = new widget.panelisi(); BtnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true); setResizable(false);

        internalFrame1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(240, 245, 235)),
                "::[ Kirim Bundel SatuSehat (Rawat Jalan / IGD / Rawat Inap) ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // ---- filter atas ----
        panelAtas.setPreferredSize(new Dimension(44, 44));
        panelAtas.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));
        jLabel1.setText("Tgl.Registrasi :"); jLabel1.setPreferredSize(new Dimension(85, 23)); panelAtas.add(jLabel1);
        DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setPreferredSize(new Dimension(95, 23)); panelAtas.add(DTPCari1);
        jLabel2.setText("s.d."); jLabel2.setPreferredSize(new Dimension(24, 23)); panelAtas.add(jLabel2);
        DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setPreferredSize(new Dimension(95, 23)); panelAtas.add(DTPCari2);
        jLabel3.setText("Key Word :"); jLabel3.setPreferredSize(new Dimension(65, 23)); panelAtas.add(jLabel3);
        TCari.setPreferredSize(new Dimension(180, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent evt) { if (evt.getKeyCode() == KeyEvent.VK_ENTER) runBackground(() -> tampilSemua()); }
        });
        panelAtas.add(TCari);
        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnCari.addActionListener(e -> runBackground(this::tampilSemua)); panelAtas.add(BtnCari);
        jLabel4.setText("Status :"); jLabel4.setPreferredSize(new Dimension(45, 23)); panelAtas.add(jLabel4);
        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Semua", "Encounter siap", "Belum ada Encounter"}));
        panelAtas.add(cmbStatus);
        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setText("Keluar"); BtnKeluar.setPreferredSize(new Dimension(100, 26));
        BtnKeluar.addActionListener(e -> dispose()); panelAtas.add(BtnKeluar);

        for (int t = 0; t < 3; t++) TabKategori.addTab(KAT[t], buildTab(t));
        internalFrame1.add(TabKategori, BorderLayout.CENTER);

        // ---- filter tanggal + aksi di BAWAH ----
        internalFrame1.add(panelAtas, BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, BorderLayout.CENTER);
        pack();
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent e) { runBackground(() -> tampilSemua()); }
        });
    }

    /** Bangun 1 tab: split kiri(tabel) - kanan(detail + tombol). */
    private JPanel buildTab(final int t) {
        models[t] = new DefaultTableModel(null, new String[]{
                "P", "No.Rawat", "No.RM", "Nama Pasien", "NIK", "Poli", "Tgl Registrasi", "Encounter", "Diag", "Pros", "No.SEP"}) {
            @Override public boolean isCellEditable(int r, int c) { return c == 0; }
            Class[] types = {Boolean.class, String.class, String.class, String.class, String.class,
                    String.class, String.class, String.class, String.class, String.class, String.class};
            @Override public Class getColumnClass(int c) { return types[c]; }
        };
        tables[t] = new widget.Table();
        tables[t].setModel(models[t]);
        tables[t].setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] lebar = {22, 110, 70, 160, 110, 120, 110, 70, 45, 45, 150};
        for (i = 0; i < lebar.length; i++) tables[t].getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        tables[t].setDefaultRenderer(Object.class, new WarnaTable());
        tables[t].getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) loadDetail(t); });
        widget.ScrollPane scKiri = new widget.ScrollPane(); scKiri.setViewportView(tables[t]);

        // ---- KOLOM TENGAH: detail + tombol (rata kiri) ----
        JPanel detPanel = new JPanel(new GridLayout(9, 2, 3, 4));
        detPanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
        String[] capt = {"No.SEP", "No.Rawat", "Pasien", "NIK", "Poli", "Tgl Registrasi", "Encounter", "Diagnosa", "Prosedur"};
        for (int f = 0; f < 9; f++) {
            detPanel.add(mkLabel(capt[f] + " :", true));
            det[t][f] = new widget.Label(); det[t][f].setText("-");
            det[t][f].setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            detPanel.add(det[t][f]);
        }

        // Panel status kirim: menampilkan resource yang SUDAH terkirim beserta ID-nya,
        // supaya petugas tidak perlu menekan tombol untuk mengetahui apa yang sudah dikirim.
        statusKirim[t] = new widget.editorpane();
        statusKirim[t].setContentType("text/html");
        statusKirim[t].setEditable(false);
        statusKirim[t].setText(statusKirimHtml(""));
        widget.ScrollPane scStatus = new widget.ScrollPane();
        scStatus.setViewportView(statusKirim[t]);
        scStatus.setPreferredSize(new Dimension(320, 150));
        scStatus.setBorder(javax.swing.BorderFactory.createTitledBorder("Status Kirim SatuSehat"));
        Color biru = new Color(41, 128, 185), ungu = new Color(142, 68, 173), hijau = new Color(0, 153, 76);
        // ---- tombol COMMON (per-kunjungan via helper) ----
        btnSEP[t] = new widget.Button(); btnSEP[t].setText("SEP");
        btnSEP[t].setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/bpjs.png")));
        btnSEP[t].setGlassColor(biru); btnSEP[t].addActionListener(e -> { pendingType[t]=""; btnKirimNow[t].setEnabled(false); btnUpdateNow[t].setEnabled(false); previewSEP(t); });
        btnEnc[t] = new widget.Button(); btnEnc[t].setText("Encounter");
        btnEnc[t].setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/1485357890_hospital.png")));
        btnEnc[t].setGlassColor(biru); btnEnc[t].addActionListener(e -> siapkan(t, "ENC", "Encounter"));
        btnCond[t] = new widget.Button(); btnCond[t].setText("Condition");
        btnCond[t].setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/1360815295_medical_case.png")));
        btnCond[t].setGlassColor(biru); btnCond[t].addActionListener(e -> siapkan(t, "COND", "Condition"));
        btnProc[t] = new widget.Button(); btnProc[t].setText("Procedure");
        btnProc[t].setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/1491582070_advantage_diagram.png")));
        btnProc[t].setGlassColor(biru); btnProc[t].addActionListener(e -> siapkan(t, "PROC", "Procedure"));
        btnBundel[t] = new widget.Button(); btnBundel[t].setText("Bundel (Semua)");
        btnBundel[t].setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/307356_box_brown_cardboard_package_icon.png")));
        btnBundel[t].setGlassColor(hijau); btnBundel[t].addActionListener(e -> siapkan(t, "BUNDEL", "Bundel (Semua)"));

        // ---- tombol Billing (ChargeItem+Account+Invoice transaction Bundle) ----
        widget.Button bBill = new widget.Button(); bBill.setText("Billing");
        bBill.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/2942642_credit_money_online_pay_payments_icon.png")));
        bBill.setGlassColor(hijau); bBill.addActionListener(e -> siapkan(t, "BILLING", "Billing"));

        // ---- tombol KATEGORI-SPESIFIK (buka form sender terkait) ----
        java.util.List<widget.Button> bsl = new java.util.ArrayList<>();
        // Urutan mengikuti alur pengiriman yang sebenarnya:
        // SEP -> Encounter -> Condition -> Triase -> SPRI, baru sisanya.
        // Encounter butuh Condition sudah ada (Encounter.diagnosis), Triase dan SPRI
        // sama-sama merujuk Encounter, dan SPRI dibuat setelah triase memutuskan
        // pasien perlu dirawat inap.
        bsl.add(btnSEP[t]); bsl.add(btnEnc[t]); bsl.add(btnCond[t]); bsl.add(btnProc[t]);
        // Triase ada di IGD dan Rawat Inap: kunjungan IGD yang dirawat inap muncul di
        // kedua tab, dan data triase-nya tetap milik kunjungan yang sama. Poli rawat
        // jalan tidak punya data_triase_igd, jadi tab 0 dikecualikan.
        if (t == 1 || t == 2) {
            widget.Button bTri = new widget.Button(); bTri.setText("Triase IGD");
            bTri.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/1360815295_medical_case.png")));
            bTri.setGlassColor(ungu); bTri.addActionListener(e -> siapkan(t, "TRIASE", "Triase IGD"));
            bsl.add(bTri);
            // Penilaian Awal Medis IGD (penilaian_medis_igd) -> resource diskret +
            // Composition Emergency medicine Note (34111-5). Muncul di IGD & Rawat Inap.
            widget.Button bPenIGD = new widget.Button(); bPenIGD.setText("Penilaian Medis IGD");
            bPenIGD.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/1360485642_edit-notes.png")));
            bPenIGD.setGlassColor(ungu); bPenIGD.addActionListener(e -> siapkan(t, "PENILAIANIGD", "Penilaian Medis IGD"));
            bsl.add(bPenIGD);
        }
        if (t != 2) {
            // SPRI dibuat dari kunjungan poli/IGD yang memerintahkan rawat inap,
            // bukan dari kunjungan rawat inapnya sendiri, jadi tidak ada di tab Ranap.
            widget.Button bSpri = new widget.Button(); bSpri.setText("SPRI");
            bSpri.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/1360485894_add-notes.png")));
            bSpri.setGlassColor(ungu); bSpri.addActionListener(e -> siapkan(t, "SPRI", "SPRI"));
            bsl.add(bSpri);
        }
        // Resume Medis (Composition) setelah SPRI. Rawat jalan/IGD memakai sender
        // rawat jalan (resume_pasien); rawat inap memakai sender ranap
        // (resume_pasien_ranap).
        widget.Button bResume = new widget.Button(); bResume.setText("Resume Medis");
        bResume.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/1360485642_edit-notes.png")));
        bResume.setGlassColor(ungu); bResume.addActionListener(e -> siapkan(t, "RESUME", "Resume Medis"));
        bsl.add(bResume);
        // Laporan Operasi (Composition) -- bisa dari kunjungan RJ (ODC), IGD, atau Ranap.
        widget.Button bOperasi = new widget.Button(); bOperasi.setText("Laporan Operasi");
        bOperasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/1360815295_medical_case.png")));
        bOperasi.setGlassColor(ungu); bOperasi.addActionListener(e -> siapkan(t, "OPERASI", "Laporan Operasi"));
        bsl.add(bOperasi);
        widget.Button bTindakan = new widget.Button(); bTindakan.setText("Laporan Tindakan");
        bTindakan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/1360815295_medical_case.png")));
        bTindakan.setGlassColor(ungu); bTindakan.addActionListener(e -> siapkan(t, "TINDAKAN", "Laporan Tindakan"));
        bsl.add(bTindakan);
        widget.Button bAnestesi = new widget.Button(); bAnestesi.setText("Laporan Anestesi");
        bAnestesi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/1360815295_medical_case.png")));
        bAnestesi.setGlassColor(ungu); bAnestesi.addActionListener(e -> siapkan(t, "ANESTESI", "Laporan Anestesi"));
        bsl.add(bAnestesi);
        widget.Button bPersalinan = new widget.Button(); bPersalinan.setText("Laporan Persalinan");
        bPersalinan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/1360815295_medical_case.png")));
        bPersalinan.setGlassColor(ungu); bPersalinan.addActionListener(e -> siapkan(t, "PERSALINAN", "Laporan Persalinan"));
        bsl.add(bPersalinan);
        if (t == 2) {
            // Transfer Internal RS (transfer_internal_rs) -> Composition Transfer summary
            // note (18761-7) + Observation vital sebelum/sesudah. Khusus tab Rawat Inap.
            widget.Button bTransfer = new widget.Button(); bTransfer.setText("Transfer Internal");
            bTransfer.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/1360485642_edit-notes.png")));
            bTransfer.setGlassColor(ungu); bTransfer.addActionListener(e -> siapkan(t, "TRANSFER", "Transfer Internal"));
            bsl.add(bTransfer);
        }
        // Alkes (Device family): Device/DeviceRequest/Dispense/UseStatement/SupplyRequest/Delivery.
        // Alur pengiriman 6 resource masih akan dibahas -- tombol dipasang lebih dulu.
        widget.Button bAlkes = new widget.Button(); bAlkes.setText("Alkes");
        bAlkes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/1491582070_advantage_diagram.png")));
        bAlkes.setGlassColor(ungu); bAlkes.addActionListener(e -> siapkan(t, "ALKES", "Alkes"));
        bsl.add(bAlkes);
        widget.Button bObat = new widget.Button(); bObat.setText("Obat");
        bObat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/1290990_doctor_drug_healthcare_medical_medicine_icon.png")));
        bObat.setGlassColor(ungu); bObat.addActionListener(e -> siapkan(t, "OBAT", "Obat"));
        bsl.add(bObat);
        if (t == 0) { // Rawat Jalan
            // Obat Kronis (>7 hari, JKN) -- MedicationRequest + MedicationDispense, rawat jalan saja.
            widget.Button bKronis = new widget.Button(); bKronis.setText("Obat Kronis");
            bKronis.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/1290990_doctor_drug_healthcare_medical_medicine_icon.png")));
            bKronis.setGlassColor(ungu); bKronis.addActionListener(e -> siapkan(t, "KRONIS", "Obat Kronis"));
            bsl.add(bKronis);
            bsl.add(mkLabBtn(t, ungu));
            bsl.add(mkFormBtn("Radiologi", "/picture/1491582070_advantage_diagram.png", ungu, () -> siapkan(t, "RADIOLOGI", "Radiologi")));
            bsl.add(mkStubBtn(t, "USG", "/picture/1360815295_medical_case.png", ungu));
            bsl.add(mkStubBtn(t, "SOAPIE", "/picture/1360485894_add-notes.png", ungu));
            bsl.add(mkStubBtn(t, "Endoskopi", "/picture/1485357890_hospital.png", ungu));
        } else if (t == 1) { // IGD
            bsl.add(mkFormBtn("Observation TTV", "/picture/1290990_doctor_drug_healthcare_medical_medicine_icon.png", ungu,
                    () -> { bridging.SatuSehatKirimObservationTTV a = new bridging.SatuSehatKirimObservationTTV(parent, false); a.isCek(); showForm(a); }));
            bsl.add(mkLabBtn(t, ungu));
            bsl.add(mkFormBtn("Radiologi", "/picture/1491582070_advantage_diagram.png", ungu, () -> siapkan(t, "RADIOLOGI", "Radiologi")));
            bsl.add(mkStubBtn(t, "Awal Medis IGD", "/picture/1360485894_add-notes.png", ungu));
        } else { // Rawat Inap
            bsl.add(mkLabBtn(t, ungu));
            bsl.add(mkFormBtn("Radiologi", "/picture/1491582070_advantage_diagram.png", ungu, () -> siapkan(t, "RADIOLOGI", "Radiologi")));
            bsl.add(mkStubBtn(t, "Transfer Internal", "/picture/1485357890_hospital.png", ungu));
        }
        // Dokumen/citra bebas (PDF, JPG, PNG) -> DICOM lewat layanan RIS, lalu
        // diteruskan ke DICOM Router. Berbeda dari tombol lain: perlu memilih
        // berkas dulu, jadi klik tombol langsung membuka penjelajah berkas.
        bsl.add(mkFormBtn("Dokumen DICOM", "/picture/1360485894_add-notes.png", ungu,
                () -> bukaDaftarBerkasDicom(t)));
        bsl.add(btnBundel[t]);
        // Billing paling bawah: tagihan disusun setelah seluruh resource klinis
        // terkirim, jadi urutan tombol mengikuti urutan kerja petugas.
        bsl.add(bBill);
        widget.Button[] bs = bsl.toArray(new widget.Button[0]);
        // tombol-tombol (kolom kiri dalam grup) — rata kiri
        JPanel btnBox = new JPanel();
        btnBox.setLayout(new BoxLayout(btnBox, BoxLayout.Y_AXIS));
        for (widget.Button b : bs) {
            b.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
            b.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
            b.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
            b.setIconTextGap(10);
            b.setFont(new Font("Tahoma", 1, 11));
            b.setForeground(Color.BLACK);
            b.setPreferredSize(new Dimension(210, 34));
            b.setMaximumSize(new Dimension(210, 34));
            b.setMinimumSize(new Dimension(210, 34));
            btnBox.add(b); btnBox.add(javax.swing.Box.createVerticalStrut(5));
        }
        // Bungkus daftar tombol dengan scroll vertikal supaya tetap terjangkau saat
        // jumlah tombol bertambah (Laporan Operasi/Tindakan/Anestesi/Persalinan/dst).
        JPanel btnBoxWrap = new JPanel(new BorderLayout());
        btnBoxWrap.add(btnBox, BorderLayout.NORTH);
        javax.swing.JScrollPane btnScroll = new javax.swing.JScrollPane(btnBoxWrap,
                javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        btnScroll.setBorder(null);
        btnScroll.getVerticalScrollBar().setUnitIncrement(16);
        JPanel btnWrap = new JPanel(new BorderLayout());
        btnWrap.add(btnScroll, BorderLayout.CENTER);

        // preview / data (kolom kanan dalam grup) — sejajar dengan tombol
        logPane[t] = new widget.editorpane();
        logPane[t].setEditable(false);
        logPane[t].setEditorKit(new HTMLEditorKit());
        logPane[t].setText("<html><body style='font:11px tahoma'><i>Klik SEP atau kirim resource — datanya tampil di sini.</i></body></html>");
        widget.ScrollPane scLog = new widget.ScrollPane(); scLog.setViewportView(logPane[t]);
        JPanel dataKanan = new JPanel(new BorderLayout());
        dataKanan.add(scLog, BorderLayout.CENTER);

        // Panel aksi di bawah preview: Kirim (mengirim resource yang sedang di-preview)
        // dan Edit (membuka form untuk melengkapi Triase/SPRI). Keduanya nonaktif
        // sampai sebuah tombol resource ditekan dan preview-nya tampil.
        btnKirimNow[t] = new widget.Button(); btnKirimNow[t].setText("Kirim");
        btnKirimNow[t].setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/34.png")));
        btnKirimNow[t].setGlassColor(new Color(0, 153, 76)); btnKirimNow[t].setEnabled(false);
        final int tt = t;
        btnKirimNow[t].addActionListener(e -> { if (!pendingType[tt].isEmpty()) aksiKirim(tt, pendingType[tt]); });
        // Update: PUT/kirim-ulang resource yang sudah terkirim dengan data terbaru.
        btnUpdateNow[t] = new widget.Button(); btnUpdateNow[t].setText("Update");
        btnUpdateNow[t].setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/refresh.png")));
        btnUpdateNow[t].setGlassColor(new Color(0, 102, 204)); btnUpdateNow[t].setEnabled(false);
        btnUpdateNow[t].addActionListener(e -> { if (!pendingType[tt].isEmpty()) aksiUpdate(tt, pendingType[tt]); });
        JPanel aksiBawah = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 6, 4));
        aksiBawah.add(btnKirimNow[t]);
        aksiBawah.add(btnUpdateNow[t]);
        dataKanan.add(aksiBawah, BorderLayout.SOUTH);

        // Daftar tombol untuk penjagaan hak akses. SEP tidak masuk: hanya menampilkan
        // data SEP dari DB, tidak mengirim apa pun ke SatuSehat.
        btnSemua[t] = new java.util.ArrayList<>(bsl);
        btnSemua[t].remove(btnSEP[t]);
        btnSemua[t].add(btnKirimNow[t]);
        btnSemua[t].add(btnUpdateNow[t]);

        // Panel kanan: detail + status DIPINDAH ke atas preview saja, supaya kolom
        // tombol dokumen bisa MEMBENTANG PENUH dari atas ke bawah (scrollbar minimal).
        JPanel utara = new JPanel(new BorderLayout(2, 4));
        utara.add(detPanel, BorderLayout.NORTH);
        utara.add(scStatus, BorderLayout.CENTER);
        JPanel sisiKanan = new JPanel(new BorderLayout(2, 6));
        sisiKanan.add(utara, BorderLayout.NORTH);
        sisiKanan.add(dataKanan, BorderLayout.CENTER);

        // tombol dokumen (kolom penuh, atas–bawah) | (detail+status di atas preview)
        javax.swing.JSplitPane innerSplit = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT, btnWrap, sisiKanan);
        innerSplit.setResizeWeight(0.0); innerSplit.setDividerLocation(240);
        innerSplit.setOneTouchExpandable(true);

        // GRUP "Detail Kunjungan" membungkus seluruh panel kanan
        JPanel grup = new JPanel(new BorderLayout(2, 6));
        grup.setBorder(BorderFactory.createTitledBorder("Detail Kunjungan"));
        grup.add(innerSplit, BorderLayout.CENTER);

        javax.swing.JSplitPane split = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT, scKiri, grup);
        split.setResizeWeight(0.40); split.setDividerLocation(400);
        // tombol panah di pembatas: klik utk full-kan tabel kiri / panel kanan
        split.setOneTouchExpandable(true);

        JPanel tab = new JPanel(new BorderLayout(1, 1));
        tab.add(split, BorderLayout.CENTER);
        // footer kecil: record count
        widget.panelisi pc = new widget.panelisi();
        pc.setPreferredSize(new Dimension(44, 26));
        pc.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 4));
        widget.Label lc = new widget.Label(); lc.setText("Record :"); lc.setPreferredSize(new Dimension(50, 18)); pc.add(lc);
        counts[t] = new widget.Label(); counts[t].setText("0"); counts[t].setPreferredSize(new Dimension(60, 18)); pc.add(counts[t]);
        tab.add(pc, BorderLayout.PAGE_END);
        return tab;
    }

    private void loadDetail(int t) {
        int r = tables[t].getSelectedRow();
        if (r < 0) return;
        selNoRawat[t] = models[t].getValueAt(r, 1).toString();
        String sep = models[t].getValueAt(r, 10) == null ? "" : models[t].getValueAt(r, 10).toString();
        det[t][0].setText(sep.isEmpty() ? "-" : sep);
        det[t][1].setText(models[t].getValueAt(r, 1).toString());
        det[t][2].setText(models[t].getValueAt(r, 3).toString());
        det[t][3].setText(models[t].getValueAt(r, 4).toString());
        det[t][4].setText(models[t].getValueAt(r, 5).toString());
        det[t][5].setText(models[t].getValueAt(r, 6).toString());
        det[t][6].setText(models[t].getValueAt(r, 7).toString());
        det[t][7].setText(models[t].getValueAt(r, 8).toString() + " diagnosa");
        det[t][8].setText(models[t].getValueAt(r, 9).toString() + " prosedur");
        statusKirim[t].setText(statusKirimHtml(selNoRawat[t]));
        statusKirim[t].setCaretPosition(0);
        // Ganti baris -> batalkan arm sebelumnya supaya Kirim tidak mengirim untuk
        // konteks yang tidak lagi ditampilkan. Petugas menekan tombol resource lagi.
        pendingType[t] = "";
        btnKirimNow[t].setEnabled(false); btnKirimNow[t].setText("Kirim");
        btnUpdateNow[t].setEnabled(false); btnUpdateNow[t].setText("Update");
    }

    /**
     * Membangun ringkasan HTML resource yang sudah terkirim untuk satu kunjungan,
     * dibaca langsung dari tabel pelacak satu_sehat_*. Menampilkan centang beserta
     * ID (dipotong) sehingga petugas tahu apa yang sudah dikirim tanpa menekan
     * tombol. Baris yang belum terkirim ditandai jelas.
     */
    private String statusKirimHtml(String noRawat) {
        StringBuilder h = new StringBuilder("<html><body style='font:11px tahoma;margin:2px'>");
        if (noRawat == null || noRawat.isEmpty()) {
            return h.append("<i>Pilih kunjungan untuk melihat status.</i></body></html>").toString();
        }
        h.append("<table style='font:11px tahoma' cellpadding='1'>");
        h.append(barisStatus("Encounter", cariId("select id_encounter from satu_sehat_encounter where no_rawat=?", noRawat)));
        h.append(barisStatusJumlah("Condition", "satu_sehat_condition", "id_condition", noRawat));
        h.append(barisStatusJumlah("Procedure", "satu_sehat_procedure", "id_procedure", noRawat));
        h.append(barisStatus("Triase IGD", cariId("select id_composition from satu_sehat_triase_igd where no_rawat=?", noRawat)));
        h.append(barisStatus("SPRI", cariId("select id_servicerequest from satu_sehat_spri where no_rawat=?", noRawat)));
        String idRme = cariId("select id_composition from satu_sehat_rme_rawat_inap where no_rawat=?", noRawat);
        if (idRme.isEmpty()) idRme = cariId("select id_composition from satu_sehat_rme_rawat_jalan where no_rawat=?", noRawat);
        h.append(barisStatus("Resume Medis", idRme));
        h.append(barisStatusJumlah("Laporan Operasi", "satu_sehat_laporan_operasi", "id_composition", noRawat));
        h.append(barisStatusJumlah("Laporan Tindakan", "satu_sehat_laporan_tindakan", "id_composition", noRawat));
        h.append(barisStatusJumlah("Laporan Anestesi", "satu_sehat_resume_anestesi", "id_composition", noRawat));
        h.append(barisStatusJumlah("Laporan Persalinan", "satu_sehat_laporan_persalinan", "id_composition", noRawat));
        h.append(barisStatusObatKronis(noRawat));
        // Keluarga Alkes: 6 resource, dulu hanya DeviceRequest yang ditampilkan.
        h.append(barisStatusJumlah("Alkes · DeviceRequest", "satu_sehat_devicerequest", "id_devicerequest", noRawat));
        h.append(barisStatusJumlah("Alkes · DeviceDispense", "satu_sehat_devicedispense", "id_devicedispense", noRawat));
        h.append(barisStatusJumlah("Alkes · DeviceUseStatement", "satu_sehat_deviceusestatement", "id_deviceusestatement", noRawat));
        h.append(barisStatusJumlah("Alkes · SupplyRequest", "satu_sehat_supplyrequest", "id_supplyrequest", noRawat));
        h.append(barisStatusJumlah("Alkes · SupplyDelivery", "satu_sehat_supplydelivery", "id_supplydelivery", noRawat));
        h.append(barisStatus("Billing (Account)", cariId("select id_account from satu_sehat_account where no_rawat=?", noRawat)));
        h.append("</table></body></html>");
        return h.toString();
    }

    /** Satu baris status: hijau + ID bila terkirim, abu "belum" bila kosong. */
    private String barisStatus(String label, String id) {
        if (id == null || id.isEmpty()) {
            return "<tr><td><b>" + label + "</b></td><td style='color:#999'>&#10007; belum</td></tr>";
        }
        String pendek = id.length() > 13 ? id.substring(0, 13) + "\u2026" : id;
        return "<tr><td><b>" + label + "</b></td><td style='color:#090'>&#10003; " + pendek + "</td></tr>";
    }

    /** Baris status untuk resource banyak-baris (Condition/Procedure): berapa yang terisi dari total. */
    private String barisStatusJumlah(String label, String tabel, String kolomId, String noRawat) {
        int total = 0, terkirim = 0;
        try (java.sql.PreparedStatement ps = koneksi.prepareStatement(
                "select count(*) total, sum(ifnull(" + kolomId + ",'')<>'') terkirim from " + tabel + " where no_rawat=?")) {
            ps.setString(1, noRawat);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { total = rs.getInt("total"); terkirim = rs.getInt("terkirim"); }
            }
        } catch (Exception e) { System.out.println("Notif status " + tabel + " : " + e); }
        if (total == 0) {
            return "<tr><td><b>" + label + "</b></td><td style='color:#999'>&#10007; belum ada data</td></tr>";
        }
        String warna = terkirim == total ? "#090" : (terkirim > 0 ? "#c80" : "#999");
        String tanda = terkirim > 0 ? "&#10003;" : "&#10007;";
        return "<tr><td><b>" + label + "</b></td><td style='color:" + warna + "'>" + tanda + " "
                + terkirim + "/" + total + " terkirim</td></tr>";
    }

    /** Ambil satu ID dari tabel pelacak; string kosong bila belum terkirim. */
    private String cariId(String sql, String noRawat) {
        try (java.sql.PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, noRawat);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { String v = rs.getString(1); return v == null ? "" : v.trim(); }
            }
        } catch (Exception e) { System.out.println("Notif status kirim : " + e); }
        return "";
    }

    /** Hitung baris (count) untuk preview data terstruktur. */
    private int hitung(String sql, String noRawat) {
        try (java.sql.PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, noRawat);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { System.out.println("Notif hitung : " + e); }
        return 0;
    }

    private String kategoriSql(int tab) {
        switch (tab) {
            case 1:  return " and rp.kd_poli='IGDK' ";
            case 2:  return " and rp.status_lanjut='Ranap' ";
            default: return " and rp.status_lanjut='Ralan' and rp.kd_poli<>'IGDK' ";
        }
    }

    private void tampilSemua() { for (int t = 0; t < 3; t++) tampil(t); }

    private void tampil(int tab) {
        Valid.tabelKosong(models[tab]);
        String stat = cmbStatus.getSelectedItem().toString();
        try {
            String sql =
                "select rp.no_rawat, rp.no_rkm_medis, p.nm_pasien, ifnull(p.no_ktp,'') no_ktp, "
              + "ifnull(pol.nm_poli,rp.kd_poli) nm_poli, rp.kd_poli, ifnull(rp.status_lanjut,'') status_lanjut, "
              + "rp.tgl_registrasi, ifnull(se.id_encounter,'') id_encounter, "
              + "ifnull(bs.no_sep,'') no_sep, "
              + "(select count(*) from diagnosa_pasien dp where dp.no_rawat=rp.no_rawat) jml_diag, "
              + "(select count(*) from prosedur_pasien pp where pp.no_rawat=rp.no_rawat) jml_proc "
              + "from reg_periksa rp join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
              + "left join poliklinik pol on pol.kd_poli=rp.kd_poli "
              + "left join satu_sehat_encounter se on se.no_rawat=rp.no_rawat "
              + "left join bridging_sep bs on bs.no_rawat=rp.no_rawat "
              + "where rp.tgl_registrasi between ? and ? " + kategoriSql(tab)
              + (stat.equals("Encounter siap") ? "and ifnull(se.id_encounter,'')<>'' " : "")
              + (stat.equals("Belum ada Encounter") ? "and ifnull(se.id_encounter,'')='' " : "")
              + (TCari.getText().isEmpty() ? "" : "and (rp.no_rawat like ? or rp.no_rkm_medis like ? or p.nm_pasien like ?) ")
              + "order by rp.tgl_registrasi, rp.no_rawat";
            ps = koneksi.prepareStatement(sql);
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
            if (!TCari.getText().isEmpty()) {
                ps.setString(3, "%" + TCari.getText() + "%"); ps.setString(4, "%" + TCari.getText() + "%"); ps.setString(5, "%" + TCari.getText() + "%");
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                boolean enc = !rs.getString("id_encounter").isEmpty();
                // Kunjungan IGD yang dirawat inap memenuhi filter tab IGD sekaligus tab
                // Rawat Inap, jadi memang tampil dua kali. Ditandai supaya petugas sadar
                // itu kunjungan yang sama, bukan dua kunjungan berbeda.
                String poli = rs.getString("nm_poli");
                if ("IGDK".equalsIgnoreCase(rs.getString("kd_poli")) && "Ranap".equals(rs.getString("status_lanjut"))) {
                    poli = poli + " [lanjut Ranap]";
                }
                // ASCII saja: glyph ✓/✗/→ tidak ada di font tabel (Tahoma) dan
                // muncul sebagai kotak. Panel status HTML tetap boleh pakai entity.
                models[tab].addRow(new Object[]{
                        false, rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("no_ktp"), poli, rs.getString("tgl_registrasi"),
                        enc ? "siap" : "belum", rs.getString("jml_diag"), rs.getString("jml_proc"), rs.getString("no_sep")});
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notif tampil bundel : " + e); }
        counts[tab].setText("" + models[tab].getRowCount());
    }

    /** type: COND | PROC | BUNDEL */
    private void aksiKirim(final int tab, final String type) {
        final String noRawat = selNoRawat[tab];
        if (noRawat == null || noRawat.isEmpty()) {
            logPane[tab].setText("<html><body style='font:11px tahoma'><font color='red'>Pilih dulu kunjungan di tabel.</font></body></html>");
            return;
        }
        if (tolakTanpaIzin(tab)) return;
        SwingUtilities.invokeLater(() -> logPane[tab].setText(
                "<html><body style='font:11px tahoma'><i>Memproses " + type + " untuk " + noRawat + " ... (JSON akan tampil di sini)</i></body></html>"));
        runBackground(() -> {
            StringBuilder log = new StringBuilder("<html><body style='font:11px tahoma'>");
            log.append("<b>").append(noRawat).append("</b><br>");
            int n;
            switch (type) {
                case "ENC":    log.append("<u>Encounter</u><br>"); n = SatuSehatBundelHelper.kirimEncounter(noRawat, log); break;
                case "COND":   log.append("<u>Condition</u><br>"); n = SatuSehatBundelHelper.kirimCondition(noRawat, log); break;
                case "PROC":   log.append("<u>Procedure</u><br>"); n = SatuSehatBundelHelper.kirimProcedure(noRawat, log); break;
                case "TRIASE": log.append("<u>Triase IGD</u> (multi-resource: Observation/Condition/Composition)<br>");
                               n = kirimTriase(noRawat, log); break;
                case "PENILAIANIGD": log.append("<u>Penilaian Medis IGD</u> (Observation/Condition + Composition)<br>");
                               n = kirimPenilaianIGD(tab, noRawat, log, false); break;
                case "TRANSFER": log.append("<u>Transfer Internal RS</u> (Observation vital + Composition)<br>");
                               n = kirimTransfer(noRawat, log, false); break;
                case "SPRI":   log.append("<u>SPRI</u> (ServiceRequest: perintah rawat inap)<br>");
                               n = kirimSpri(noRawat, log, false); break;
                case "RESUME": log.append("<u>Resume Medis</u> (Composition)<br>");
                               n = kirimResume(tab, noRawat, log, false); break;
                case "OPERASI":log.append("<u>Laporan Operasi</u> (Composition)<br>");
                               n = kirimOperasi(noRawat, log, false); break;
                case "TINDAKAN":log.append("<u>Laporan Tindakan</u> (Composition)<br>");
                               n = kirimTindakan(noRawat, log, false); break;
                case "ANESTESI":log.append("<u>Laporan Anestesi</u> (Composition)<br>");
                               n = kirimAnestesi(noRawat, log, false); break;
                case "PERSALINAN":log.append("<u>Laporan Persalinan</u> (Composition)<br>");
                               n = kirimPersalinan(noRawat, log, false); break;
                case "KRONIS":log.append("<u>Obat Kronis</u> (MedicationRequest + MedicationDispense)<br>");
                               n = kirimObatKronis(noRawat, log); break;
                case "ALKES":log.append("<u>Alkes</u> (Device family)<br>");
                               n = kirimAlkes(noRawat, log); break;
                case "LAB":log.append("<u>Lab</u> (ServiceRequest/Specimen/Observation/DiagnosticReport)<br>");
                               n = kirimLab(noRawat, log); break;
                case "RADIOLOGI":log.append("<u>Radiologi</u> (ServiceRequest/Specimen/Observation/DiagnosticReport)<br>");
                               n = kirimRadiologi(noRawat, log); break;
                case "OBAT":log.append("<u>Obat</u> (MedicationRequest/Dispense/Administration/Statement)<br>");
                               n = kirimObat(noRawat, log); break;
                case "BILLING":log.append("<u>Billing</u> (transaction Bundle: ChargeItem+Account+Invoice + PATCH Encounter)<br>");
                               n = SatuSehatBundelHelper.kirimBillingBundle(noRawat, log); break;
                default:       n = SatuSehatBundelHelper.kirimBundel(noRawat, log); break;
            }
            // n=0 bisa berarti "sudah lengkap" ATAU "gagal" — jangan disamarkan jadi
            // angka telanjang yang terbaca seperti kegagalan.
            log.append(n > 0
                    ? "<hr><b>" + n + " resource terkirim.</b></body></html>"
                    : "<hr><b>Tidak ada resource baru yang terkirim.</b> Lihat keterangan per baris di atas.</body></html>");
            final String html = log.toString();
            SwingUtilities.invokeLater(() -> {
                logPane[tab].setText(html);
                tampil(tab);
                statusKirim[tab].setText(statusKirimHtml(noRawat));
                statusKirim[tab].setCaretPosition(0);
            });
        });
    }

    /** Jenis single-resource yang mendukung Update (PUT). Multi-resource (Triase/Lab/
     *  Alkes/Kronis/Billing/Encounter/Condition/Procedure) belum -- update-nya perlu
     *  PUT tiap sub-resource (desain terpisah). */
    private static final java.util.Set<String> UPDATE_DIDUKUNG = new java.util.HashSet<>(java.util.Arrays.asList(
            "OPERASI", "TINDAKAN", "ANESTESI", "PERSALINAN", "RESUME", "SPRI", "ALKES",
            "ENC", "COND", "PROC", "TRIASE", "PENILAIANIGD", "TRANSFER"));

    /**
     * Update (PUT/kirim-ulang) resource yang SUDAH terkirim dengan data terbaru.
     *
     * Menyalurkan ke wrapper kirimXxx(...,true) yang meneruskan ke
     * sender.updateSatuNoRawat() -> PUT /Resource/{id} dengan body terbaru.
     */
    private void aksiUpdate(final int tab, final String type) {
        final String noRawat = selNoRawat[tab];
        if (noRawat == null || noRawat.isEmpty()) {
            logPane[tab].setText("<html><body style='font:11px tahoma'><font color='red'>Pilih dulu kunjungan di tabel.</font></body></html>");
            return;
        }
        if (tolakTanpaIzin(tab)) return;
        String jenisTte = jenisTteUntuk(type);
        if (jenisTte != null) {
            String sudah = dokumenSudahTte(noRawat, jenisTte);
            if (!sudah.isEmpty()) {
                SwingUtilities.invokeLater(() -> logPane[tab].setText(
                        "<html><body style='font:11px tahoma'><b>Update " + type + " dibatalkan</b><br><br>"
                      + "<font color='#c00'>Dokumen ini sudah ditandatangani elektronik (TTE):</font>"
                      + "<ul style='margin:2px 0 0 14px;padding:0'>" + sudah + "</ul>"
                      + "<i>Sejak Panduan Teknis TTE v1.2, tanda tangan mengikat versi dokumen tertentu "
                      + "(<code>Composition/{id}/_history/{versionId}</code>). Memperbarui dokumen sekarang "
                      + "menaikkan versinya, sehingga tanda tangan yang ada menunjuk versi lama dan "
                      + "dokumen berjalan menjadi tidak bertanda tangan.</i><br><br>"
                      + "Bila isinya memang harus diubah, batalkan/ulangi TTE dokumen tersebut lebih dulu "
                      + "lewat menu <b>Tanda Tangan Elektronik</b>.</body></html>"));
                return;
            }
        }
        if (!UPDATE_DIDUKUNG.contains(type)) {
            SwingUtilities.invokeLater(() -> logPane[tab].setText(
                    "<html><body style='font:11px tahoma'><b>Update " + type + "</b><br><br>"
                  + "<i>Update belum tersedia untuk jenis ini (multi-resource: perlu PUT tiap "
                  + "sub-resource). Baru Resume/SPRI/Operasi/Tindakan/Anestesi/Persalinan.</i></body></html>"));
            return;
        }
        SwingUtilities.invokeLater(() -> logPane[tab].setText(
                "<html><body style='font:11px tahoma'><i>Memperbarui " + type + " untuk " + noRawat + " ...</i></body></html>"));
        runBackground(() -> {
            StringBuilder log = new StringBuilder("<html><body style='font:11px tahoma'>");
            log.append("<b>").append(noRawat).append("</b><br><u>Update ").append(type).append("</u> (PUT)<br>");
            int n;
            switch (type) {
                case "OPERASI":    n = kirimOperasi(noRawat, log, true); break;
                case "TINDAKAN":   n = kirimTindakan(noRawat, log, true); break;
                case "ANESTESI":   n = kirimAnestesi(noRawat, log, true); break;
                case "PERSALINAN": n = kirimPersalinan(noRawat, log, true); break;
                case "RESUME":     n = kirimResume(tab, noRawat, log, true); break;
                case "SPRI":       n = kirimSpri(noRawat, log, true); break;
                case "ALKES":      n = SatuSehatAlkes.updateSatuNoRawat(noRawat, log); break;
                case "ENC":        n = SatuSehatBundelHelper.updateEncounter(noRawat, log); break;
                case "COND":       n = SatuSehatBundelHelper.updateCondition(noRawat, log); break;
                case "PROC":       n = SatuSehatBundelHelper.updateProcedure(noRawat, log); break;
                case "TRIASE":     n = updateTriase(noRawat, log); break;
                case "PENILAIANIGD": n = kirimPenilaianIGD(tab, noRawat, log, true); break;
                case "TRANSFER":   n = kirimTransfer(noRawat, log, true); break;
                default:           n = 0;
            }
            log.append("<hr><b>").append(n).append(" resource diperbarui.</b></body></html>");
            final String html = log.toString();
            SwingUtilities.invokeLater(() -> {
                logPane[tab].setText(html);
                tampil(tab);
                statusKirim[tab].setText(statusKirimHtml(noRawat));
                statusKirim[tab].setCaretPosition(0);
            });
        });
    }

    /**
     * Hak akses menjaga SELURUH tombol yang berakibat kirim, bukan hanya
     * Encounter/Condition/Procedure/Bundel. Sebelumnya Triase, SPRI, Resume, 4 Laporan,
     * Obat Kronis, Billing, serta tombol Kirim/Update lolos tanpa pemeriksaan sehingga
     * petugas tanpa izin tetap bisa mengirim ke SatuSehat.
     */
    /**
     * Jenis dokumen di satu_sehat_tte untuk sebuah tombol Bundel; null bila tombol
     * itu bukan dokumen yang bisa di-TTE (Encounter/Condition/Procedure/Triase/Alkes).
     * RESUME memetakan ke dua jenis sekaligus (ranap dan rawat jalan).
     */
    private static String jenisTteUntuk(String type) {
        switch (type) {
            case "RESUME":     return "'RME Rawat Inap','RME Rawat Jalan'";
            case "SPRI":       return "'SPRI'";
            case "OPERASI":    return "'Laporan Operasi'";
            case "TINDAKAN":   return "'Laporan Tindakan'";
            case "ANESTESI":   return "'Laporan Anestesi'";
            case "PERSALINAN": return "'Laporan Persalinan'";
            default:           return null;
        }
    }

    /** Daftar &lt;li&gt; dokumen berstatus Signed; string kosong bila tidak ada. */
    private String dokumenSudahTte(String noRawat, String jenisIn) {
        StringBuilder s = new StringBuilder();
        try (java.sql.PreparedStatement p = koneksi.prepareStatement(
                "select jenis, id_composition, ifnull(tgl_tte,'') tgl from satu_sehat_tte "
              + "where no_rawat=? and jenis in (" + jenisIn + ") and status='Signed'")) {
            p.setString(1, noRawat);
            try (java.sql.ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    s.append("<li>").append(r.getString("jenis"))
                     .append(" <font color='#999'>").append(r.getString("id_composition")).append("</font>");
                    String t = r.getString("tgl");
                    if (!t.isEmpty()) s.append(" — ditandatangani ").append(t);
                    s.append("</li>");
                }
            }
        } catch (Exception e) { System.out.println("Notif cek TTE : " + e); }
        return s.toString();
    }

    public void isCek() {
        izinKirim = akses.getsatu_sehat_kirim_encounter();
        for (int t = 0; t < 3; t++) {
            if (btnSemua[t] == null) continue;
            for (widget.Button b : btnSemua[t]) b.setEnabled(izinKirim);
        }
        if (!izinKirim) {
            for (int t = 0; t < 3; t++) {
                logPane[t].setText("<html><body style='font:11px tahoma'><font color='#c00'>"
                        + "Anda tidak punya hak akses kirim SatuSehat. Tombol pengiriman dinonaktifkan.</font></body></html>");
            }
        }
    }

    /** Penjaga terakhir sebelum benar-benar mengirim — tombol bisa saja ter-enable ulang. */
    private boolean tolakTanpaIzin(int tab) {
        if (izinKirim) return false;
        SwingUtilities.invokeLater(() -> logPane[tab].setText(
                "<html><body style='font:11px tahoma'><font color='#c00'>Ditolak: tidak punya hak akses kirim SatuSehat.</font></body></html>"));
        return true;
    }

    /**
     * Status rawat inap ditentukan dari data kunjungan (reg_periksa.status_lanjut),
     * BUKAN dari tab yang sedang dibuka. Kunjungan IGD yang dirawat inap muncul di
     * tab IGD sekaligus tab Rawat Inap; dulu Resume-nya membaca resume_pasien di tab
     * IGD dan resume_pasien_ranap di tab Ranap — hasilnya beda tergantung tab.
     */
    private boolean kunjunganRanap(String noRawat) {
        try (java.sql.PreparedStatement p = koneksi.prepareStatement("select status_lanjut from reg_periksa where no_rawat=?")) {
            p.setString(1, noRawat);
            try (java.sql.ResultSet r = p.executeQuery()) {
                if (r.next()) return "Ranap".equals(r.getString(1));
            }
        } catch (Exception e) { System.out.println("Notif kunjunganRanap : " + e); }
        return false;
    }

    /** Kirim (update=false) atau Update/PUT (update=true) Laporan Operasi via sender aslinya. */
    private int kirimOperasi(String noRawat, StringBuilder log, boolean update) {
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.PrintStream orig = System.out;
        int n = 0;
        try {
            java.io.PrintStream tee = new java.io.PrintStream(new java.io.OutputStream() {
                @Override public void write(int b) { orig.write(b); buf.write(b); }
                @Override public void write(byte[] b, int o, int l) { orig.write(b, o, l); buf.write(b, o, l); }
            }, true);
            System.setOut(tee);
            bridging.SatuSehatKirimLaporanOperasi f = new bridging.SatuSehatKirimLaporanOperasi(parent, false);
            n = update ? f.updateSatuNoRawat(noRawat) : f.kirimSatuNoRawat(noRawat); f.dispose();
        } catch (Exception e) {
            log.append("&nbsp;&nbsp;<font color='red'>\u2717 " + e + "</font><br>");
        } finally {
            System.setOut(orig);
        }
        for (String line : buf.toString().split("\\r?\\n")) {
            String l = line.trim();
            if (l.startsWith("URL :") || l.startsWith("Request JSON :") || l.startsWith("Result JSON :")) {
                log.append("<div style='background:#f6f6f6;border:1px solid #e0e0e0;padding:2px;margin:2px 0;font-family:monospace;font-size:9px;color:#444'>")
                   .append(l.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")).append("</div>");
            }
        }
        log.append(n > 0
            ? "&nbsp;&nbsp;<font color='green'>\u2713 " + n + " Laporan Operasi terkirim.</font><br>"
            : "&nbsp;&nbsp;<font color='orange'>\u2022 Laporan Operasi tidak terkirim \u2014 belum ada laporan operasi, Encounter belum dikirim, atau sudah pernah terkirim.</font><br>");
        return n;
    }

    /**
     * Kirim rantai Lab Patologi Klinik untuk satu kunjungan, berurutan sesuai
     * dependensi referensi: ServiceRequest -&gt; Specimen -&gt; Observation -&gt;
     * DiagnosticReport. Tiap tahap memerlukan id tahap sebelumnya, jadi urutannya
     * tidak boleh ditukar dan tahap yang gagal menghentikan sisanya.
     *
     * Tiap sender idempoten (baris yang sudah punya id dilewati), jadi tombol ini
     * aman ditekan berulang \u2014 mis. setelah hasil lab baru diinput.
     */
    private int kirimLab(String noRawat, StringBuilder log) {
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.PrintStream orig = System.out;
        int total = 0;
        try {
            java.io.PrintStream tee = new java.io.PrintStream(new java.io.OutputStream() {
                @Override public void write(int b) { orig.write(b); buf.write(b); }
                @Override public void write(byte[] b, int o, int l) { orig.write(b, o, l); buf.write(b, o, l); }
            }, true);
            System.setOut(tee);

            bridging.SatuSehatKirimServiceRequestLabPK sr = new bridging.SatuSehatKirimServiceRequestLabPK(parent, false);
            int nSr = sr.kirimSatuNoRawat(noRawat); sr.dispose();
            log.append(barisLab("ServiceRequest", nSr));
            total += nSr;

            bridging.SatuSehatKirimSpecimenLabPK sp = new bridging.SatuSehatKirimSpecimenLabPK(parent, false);
            int nSp = sp.kirimSatuNoRawat(noRawat); sp.dispose();
            log.append(barisLab("Specimen", nSp));
            total += nSp;

            bridging.SatuSehatKirimObservationLabPK ob = new bridging.SatuSehatKirimObservationLabPK(parent, false);
            int nOb = ob.kirimSatuNoRawat(noRawat); ob.dispose();
            log.append(barisLab("Observation (hasil)", nOb));
            total += nOb;

            bridging.SatuSehatKirimDiagnosticReportLabPK dr = new bridging.SatuSehatKirimDiagnosticReportLabPK(parent, false);
            int nDr = dr.kirimSatuNoRawat(noRawat); dr.dispose();
            log.append(barisLab("DiagnosticReport (expertise)", nDr));
            total += nDr;

            // Mikrobiologi: order-nya di permintaan_labmb (tabel terpisah), hasil dan
            // kesan menumpang tabel yang sama dengan PK. Rantainya identik.
            int nMb = 0;
            bridging.SatuSehatKirimServiceRequestLabMB srm = new bridging.SatuSehatKirimServiceRequestLabMB(parent, false);
            nMb += srm.kirimSatuNoRawat(noRawat); srm.dispose();
            bridging.SatuSehatKirimSpecimenLabMB spm = new bridging.SatuSehatKirimSpecimenLabMB(parent, false);
            nMb += spm.kirimSatuNoRawat(noRawat); spm.dispose();
            bridging.SatuSehatKirimObservationLabMB obm = new bridging.SatuSehatKirimObservationLabMB(parent, false);
            nMb += obm.kirimSatuNoRawat(noRawat); obm.dispose();
            bridging.SatuSehatKirimDiagnosticReportLabMB drm = new bridging.SatuSehatKirimDiagnosticReportLabMB(parent, false);
            nMb += drm.kirimSatuNoRawat(noRawat); drm.dispose();
            log.append(barisLab("Mikrobiologi (4 resource)", nMb));
            total += nMb;
        } catch (Exception e) {
            log.append("&nbsp;&nbsp;<font color='red'>\u2717 ").append(e).append("</font><br>");
        } finally {
            System.setOut(orig);
        }
        for (String line : buf.toString().split("\\r?\\n")) {
            String l = line.trim();
            if (l.startsWith("URL :") || l.startsWith("Request JSON :") || l.startsWith("Result JSON :")) {
                log.append("<div style='background:#f6f6f6;border:1px solid #e0e0e0;padding:2px;margin:2px 0;font-family:monospace;font-size:9px;color:#444'>")
                   .append(l.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")).append("</div>");
            }
        }
        if (total == 0) {
            log.append("&nbsp;&nbsp;<font color='orange'>\u2022 Tidak ada resource Lab baru \u2014 belum ada permintaan lab, "
                     + "pemeriksaan belum dipetakan LOINC (satu_sehat_mapping_lab), hasil/kesan belum diinput, "
                     + "atau semuanya sudah terkirim.</font><br>");
        }
        return total;
    }

    /**
     * Kirim rantai Radiologi untuk satu kunjungan, berurutan sesuai dependensi:
     * ServiceRequest -&gt; Specimen -&gt; Observation -&gt; DiagnosticReport.
     * Idempoten seperti Lab.
     */
    private int kirimRadiologi(String noRawat, StringBuilder log) {
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.PrintStream orig = System.out;
        int total = 0;
        try {
            java.io.PrintStream tee = new java.io.PrintStream(new java.io.OutputStream() {
                @Override public void write(int b) { orig.write(b); buf.write(b); }
                @Override public void write(byte[] b, int o, int l) { orig.write(b, o, l); buf.write(b, o, l); }
            }, true);
            System.setOut(tee);

            bridging.SatuSehatKirimServiceRequestRadiologi sr = new bridging.SatuSehatKirimServiceRequestRadiologi(parent, false);
            int nSr = sr.kirimSatuNoRawat(noRawat); sr.dispose();
            log.append(barisLab("ServiceRequest", nSr)); total += nSr;

            // Specimen SENGAJA dilewati. SatuSehatKirimSpecimenRadiologi.BtnKirim
            // dimatikan dari dalam ("Specimen tidak diperlukan untuk radiologi pada
            // SatuSehat versi sekarang, gunakan ImagingStudy + Observation +
            // DiagnosticReport") dan memunculkan dialog modal — kalau dipanggil dari
            // Bundel, petugas akan disodori popup tiap kali menekan Kirim.
            // Observation & DiagnosticReport radiologi mem-LEFT JOIN specimen, jadi
            // rantainya tetap utuh tanpa langkah ini.

            bridging.SatuSehatKirimObservationRadiologi ob = new bridging.SatuSehatKirimObservationRadiologi(parent, false);
            int nOb = ob.kirimSatuNoRawat(noRawat); ob.dispose();
            log.append(barisLab("Observation (hasil)", nOb)); total += nOb;

            bridging.SatuSehatKirimDiagnosticReportRadiologi dr = new bridging.SatuSehatKirimDiagnosticReportRadiologi(parent, false);
            int nDr = dr.kirimSatuNoRawat(noRawat); dr.dispose();
            log.append(barisLab("DiagnosticReport (expertise)", nDr)); total += nDr;
        } catch (Exception e) {
            log.append("&nbsp;&nbsp;<font color='red'>\u2717 ").append(e).append("</font><br>");
        } finally {
            System.setOut(orig);
        }
        for (String line : buf.toString().split("\\r?\\n")) {
            String l = line.trim();
            if (l.startsWith("URL :") || l.startsWith("Request JSON :") || l.startsWith("Result JSON :")) {
                log.append("<div style='background:#f6f6f6;border:1px solid #e0e0e0;padding:2px;margin:2px 0;font-family:monospace;font-size:9px;color:#444'>")
                   .append(l.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")).append("</div>");
            }
        }
        log.append("&nbsp;&nbsp;<i>Specimen tidak dikirim \u2014 tidak diperlukan untuk radiologi. "
                 + "ImagingStudy belum ada sender-nya.</i><br>");
        if (total == 0) {
            log.append("&nbsp;&nbsp;<font color='orange'>\u2022 Tidak ada resource Radiologi baru \u2014 belum ada permintaan radiologi, "
                     + "pemeriksaan belum dipetakan LOINC (satu_sehat_mapping_radiologi), hasil belum diinput, "
                     + "atau semuanya sudah terkirim.</font><br>");
        }
        return total;
    }

    /** Preview Radiologi: pemeriksaan kunjungan + status tiap tahap rantai. */
    private void previewRadiologi(final int tab) {
        final String noRawat = selNoRawat[tab];
        if (noRawat == null || noRawat.isEmpty()) {
            logPane[tab].setText("<html><body style='font:11px tahoma'><font color='red'>Pilih dulu kunjungan di tabel.</font></body></html>");
            return;
        }
        runBackground(() -> {
            StringBuilder b = new StringBuilder("<html><body style='font:11px tahoma;color:#222'>");
            b.append("<div style='text-align:center'><b>PREVIEW RADIOLOGI</b><br>").append(noRawat).append("</div><hr>");
            b.append("<i>Menekan Kirim mengirim 4 resource per pemeriksaan: ServiceRequest, Specimen, "
                   + "Observation (hasil), DiagnosticReport (expertise).</i><hr>");
            try {
                b.append("<table cellpadding='2' style='font:11px tahoma'>");
                int jml = 0;
                try (PreparedStatement p = koneksi.prepareStatement(
                        "select j.nm_perawatan, pr.noorder, ifnull(m.code,'') loinc, "
                      + "ifnull(sr.id_servicerequest,'') sr, ifnull(sp.id_specimen,'') sp, "
                      + "ifnull(ob.id_observation,'') ob, ifnull(dr.id_diagnosticreport,'') dr "
                      + "from permintaan_radiologi pr "
                      + "join permintaan_pemeriksaan_radiologi d on d.noorder=pr.noorder "
                      + "join jns_perawatan_radiologi j on j.kd_jenis_prw=d.kd_jenis_prw "
                      + "left join satu_sehat_mapping_radiologi m on m.kd_jenis_prw=d.kd_jenis_prw "
                      + "left join satu_sehat_servicerequest_radiologi sr on sr.noorder=d.noorder and sr.kd_jenis_prw=d.kd_jenis_prw "
                      + "left join satu_sehat_specimen_radiologi sp on sp.noorder=d.noorder and sp.kd_jenis_prw=d.kd_jenis_prw "
                      + "left join satu_sehat_observation_radiologi ob on ob.noorder=d.noorder and ob.kd_jenis_prw=d.kd_jenis_prw "
                      + "left join satu_sehat_diagnosticreport_radiologi dr on dr.noorder=d.noorder and dr.kd_jenis_prw=d.kd_jenis_prw "
                      + "where pr.no_rawat=?")) {
                    p.setString(1, noRawat);
                    try (ResultSet r = p.executeQuery()) {
                        while (r.next()) {
                            jml++;
                            String loinc = r.getString("loinc");
                            b.append("<tr><td><b>").append(r.getString("nm_perawatan")).append("</b><br>")
                             .append("<font color='#999'>").append(r.getString("noorder")).append("</font></td><td>")
                             .append(loinc.isEmpty() ? "<font color='#c00'>LOINC belum dipetakan</font>"
                                                     : "<font color='#999'>LOINC " + loinc + "</font>")
                             .append("<br>").append(tandaTahap("SR", r.getString("sr")))
                             .append(" ").append(tandaTahap("Spec", r.getString("sp")))
                             .append(" ").append(tandaTahap("Obs", r.getString("ob")))
                             .append(" ").append(tandaTahap("Report", r.getString("dr")))
                             .append("</td></tr>");
                        }
                    }
                }
                if (jml == 0) b.append("<tr><td colspan='2'><i>Belum ada permintaan radiologi pada kunjungan ini.</i></td></tr>");
                b.append("</table>");
            } catch (Exception e) {
                b.append("<font color='red'>Error: ").append(e).append("</font>");
            }
            b.append("</body></html>");
            final String h = b.toString();
            SwingUtilities.invokeLater(() -> logPane[tab].setText(h));
        });
    }

    private String tandaTahap(String nama, String id) {
        return (id == null || id.isEmpty())
            ? "<font color='#999'>&#10007; " + nama + "</font>"
            : "<font color='#090'>&#10003; " + nama + "</font>";
    }

    /**
     * Kirim keluarga Obat untuk satu kunjungan.
     *
     * MedicationRequest (peresepan dokter) dan MedicationStatement (riwayat pakai)
     * bersumber dari resep_obat/resep_dokter. MedicationDispense (penyerahan
     * farmasi) dan MedicationAdministration (pemberian ke pasien) bersumber dari
     * detail_pemberian_obat, dan KEDUANYA mensyaratkan depo farmasi bangsal sudah
     * dipetakan ke Location (satu_sehat_mapping_lokasi_depo_farmasi) serta aturan
     * pakai terisi — kalau belum, keduanya wajar menghasilkan 0.
     *
     * Medication (master per barang) TIDAK dikirim di sini; itu sekali-seumur per
     * kode barang lewat menu "Kirim Medication". Obat yang belum punya Medication
     * dilaporkan supaya jelas kenapa dilewati.
     */
    private int kirimObat(String noRawat, StringBuilder log) {
        int belumMedication = 0;
        try (PreparedStatement p = koneksi.prepareStatement(
                "select count(*) n from ("
              + "  select rd.kode_brng from resep_obat r join resep_dokter rd on rd.no_resep=r.no_resep where r.no_rawat=? "
              + "  union select d.kode_brng from detail_pemberian_obat d where d.no_rawat=?"
              + ") x join databarang db on db.kode_brng=x.kode_brng "
              // alkes punya resource Device, bukan Medication -- jangan ikut dihitung
              + "join jenis j on j.kdjns=db.kdjns and not " + SatuSehatAlkes.filter("j") + " "
              + "left join satu_sehat_medication sm on sm.kode_brng=x.kode_brng "
              + "where ifnull(sm.id_medication,'')=''")) {
            p.setString(1, noRawat); p.setString(2, noRawat);
            try (ResultSet r = p.executeQuery()) { if (r.next()) belumMedication = r.getInt("n"); }
        } catch (Exception e) { System.out.println("Notif cek medication : " + e); }

        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.PrintStream orig = System.out;
        int total = 0;
        try {
            java.io.PrintStream tee = new java.io.PrintStream(new java.io.OutputStream() {
                @Override public void write(int b) { orig.write(b); buf.write(b); }
                @Override public void write(byte[] b, int o, int l) { orig.write(b, o, l); buf.write(b, o, l); }
            }, true);
            System.setOut(tee);

            bridging.SatuSehatKirimMedicationRequest mr = new bridging.SatuSehatKirimMedicationRequest(parent, false);
            int n1 = mr.kirimSatuNoRawat(noRawat); mr.dispose();
            log.append(barisLab("MedicationRequest (peresepan)", n1)); total += n1;

            bridging.SatuSehatKirimMedicationDispense md = new bridging.SatuSehatKirimMedicationDispense(parent, false);
            int n2 = md.kirimSatuNoRawat(noRawat); md.dispose();
            log.append(barisLab("MedicationDispense (penyerahan)", n2)); total += n2;

            bridging.SatuSehatKirimMedicationAdministration ma = new bridging.SatuSehatKirimMedicationAdministration(parent, false);
            int n3 = ma.kirimSatuNoRawat(noRawat); ma.dispose();
            log.append(barisLab("MedicationAdministration (pemberian)", n3)); total += n3;

            bridging.SatuSehatKirimMedicationStatement ms = new bridging.SatuSehatKirimMedicationStatement(parent, false);
            int n4 = ms.kirimSatuNoRawat(noRawat); ms.dispose();
            log.append(barisLab("MedicationStatement (riwayat)", n4)); total += n4;
        } catch (Exception e) {
            log.append("&nbsp;&nbsp;<font color='red'>\u2717 ").append(e).append("</font><br>");
        } finally {
            System.setOut(orig);
        }
        for (String line : buf.toString().split("\\r?\\n")) {
            String l = line.trim();
            if (l.startsWith("URL :") || l.startsWith("Request JSON :") || l.startsWith("Result JSON :")) {
                log.append("<div style='background:#f6f6f6;border:1px solid #e0e0e0;padding:2px;margin:2px 0;font-family:monospace;font-size:9px;color:#444'>")
                   .append(l.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")).append("</div>");
            }
        }
        if (belumMedication > 0) {
            log.append("&nbsp;&nbsp;<font color='#c00'>\u2022 ").append(belumMedication)
               .append(" obat belum punya resource Medication \u2014 kirim dulu lewat menu <b>Kirim Medication</b>.</font><br>");
        }
        if (total == 0) {
            log.append("&nbsp;&nbsp;<font color='orange'>\u2022 Tidak ada resource Obat baru \u2014 belum ada resep/pemberian obat, "
                     + "KFA belum dipetakan, depo farmasi belum dipetakan ke Location, atau semuanya sudah terkirim.</font><br>");
        }
        return total;
    }

    /** Preview Obat: resep dan pemberian obat kunjungan + status tiap resource. */
    private void previewObat(final int tab) {
        final String noRawat = selNoRawat[tab];
        if (noRawat == null || noRawat.isEmpty()) {
            logPane[tab].setText("<html><body style='font:11px tahoma'><font color='red'>Pilih dulu kunjungan di tabel.</font></body></html>");
            return;
        }
        runBackground(() -> {
            StringBuilder b = new StringBuilder("<html><body style='font:11px tahoma;color:#222'>");
            b.append("<div style='text-align:center'><b>PREVIEW OBAT</b><br>").append(noRawat).append("</div><hr>");
            b.append("<i>MedicationRequest &amp; Statement dari resep; Dispense &amp; Administration dari "
                   + "pemberian obat (butuh depo farmasi termapping ke Location).</i><hr>");
            try {
                b.append("<table cellpadding='2' style='font:11px tahoma'>");
                int jml = 0;
                try (PreparedStatement p = koneksi.prepareStatement(
                        "select x.kode_brng, ifnull(db.nama_brng,'') nama, ifnull(m.obat_code,'') kfa, "
                      + "ifnull(sm.id_medication,'') id_medication, x.sumber "
                      + "from ( select rd.kode_brng, 'resep' sumber from resep_obat r "
                      + "         join resep_dokter rd on rd.no_resep=r.no_resep where r.no_rawat=? "
                      + "       union select d.kode_brng, 'pemberian' from detail_pemberian_obat d where d.no_rawat=? ) x "
                      + "join databarang db on db.kode_brng=x.kode_brng "
                      + "join jenis j on j.kdjns=db.kdjns and not " + SatuSehatAlkes.filter("j") + " "
                      + "left join satu_sehat_mapping_obat m on m.kode_brng=x.kode_brng "
                      + "left join satu_sehat_medication sm on sm.kode_brng=x.kode_brng "
                      + "order by x.sumber, db.nama_brng")) {
                    p.setString(1, noRawat); p.setString(2, noRawat);
                    try (ResultSet r = p.executeQuery()) {
                        while (r.next()) {
                            jml++;
                            String kfa = r.getString("kfa");
                            b.append("<tr><td><b>").append(r.getString("nama")).append("</b><br><font color='#999'>")
                             .append(r.getString("sumber")).append("</font></td><td>")
                             .append(kfa.isEmpty() ? "<font color='#c00'>KFA belum dipetakan</font>"
                                                   : "<font color='#999'>KFA " + kfa + "</font>")
                             .append("<br>").append(tandaTahap("Medication", r.getString("id_medication")))
                             .append("</td></tr>");
                        }
                    }
                }
                if (jml == 0) b.append("<tr><td colspan='2'><i>Tidak ada resep maupun pemberian obat pada kunjungan ini.</i></td></tr>");
                b.append("</table>");
            } catch (Exception e) {
                b.append("<font color='red'>Error: ").append(e).append("</font>");
            }
            b.append("</body></html>");
            final String h = b.toString();
            SwingUtilities.invokeLater(() -> logPane[tab].setText(h));
        });
    }

    private String barisLab(String nama, int n) {
        return n > 0
            ? "&nbsp;&nbsp;<font color='green'>\u2713 " + nama + " : " + n + " terkirim</font><br>"
            : "&nbsp;&nbsp;<font color='#999'>\u2022 " + nama + " : tidak ada yang baru</font><br>";
    }

    /**
     * Kirim keluarga Alkes (Device, DeviceRequest, DeviceDispense sbg extension,
     * DeviceUseStatement, SupplyRequest, SupplyDelivery) lewat SatuSehatAlkes.
     * Idempoten \u2014 resource yang sudah punya id di tabel tracking dilewati.
     */
    private int kirimAlkes(String noRawat, StringBuilder log) {
        return SatuSehatAlkes.kirimSatuNoRawat(noRawat, log);
    }

    /** Kirim Obat Kronis (MedicationRequest+Dispense) via sender aslinya. */
    private int kirimObatKronis(String noRawat, StringBuilder log) {
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.PrintStream orig = System.out;
        int n = 0;
        try {
            java.io.PrintStream tee = new java.io.PrintStream(new java.io.OutputStream() {
                @Override public void write(int b) { orig.write(b); buf.write(b); }
                @Override public void write(byte[] b, int o, int l) { orig.write(b, o, l); buf.write(b, o, l); }
            }, true);
            System.setOut(tee);
            bridging.SatuSehatKirimObatKronis f = new bridging.SatuSehatKirimObatKronis(parent, false);
            n = f.kirimSatuNoRawat(noRawat); f.dispose();
        } catch (Exception e) {
            log.append("&nbsp;&nbsp;<font color='red'>\u2717 " + e + "</font><br>");
        } finally {
            System.setOut(orig);
        }
        for (String line : buf.toString().split("\\r?\\n")) {
            String l = line.trim();
            if (l.startsWith("URL :") || l.startsWith("Request JSON :") || l.startsWith("Result JSON :")) {
                log.append("<div style='background:#f6f6f6;border:1px solid #e0e0e0;padding:2px;margin:2px 0;font-family:monospace;font-size:9px;color:#444'>")
                   .append(l.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")).append("</div>");
            }
        }
        log.append(n > 0
            ? "&nbsp;&nbsp;<font color='green'>\u2713 " + n + " obat kronis terkirim.</font><br>"
            : "&nbsp;&nbsp;<font color='orange'>\u2022 Obat kronis tidak terkirim \u2014 belum ada resep apotek kronis pasien, KFA belum dipetakan, Encounter belum dikirim, atau sudah pernah terkirim.</font><br>");
        return n;
    }

    /** Kirim (update=false) atau Update/PUT (update=true) Laporan Persalinan via sender aslinya. */
    private int kirimPersalinan(String noRawat, StringBuilder log, boolean update) {
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.PrintStream orig = System.out;
        int n = 0;
        try {
            java.io.PrintStream tee = new java.io.PrintStream(new java.io.OutputStream() {
                @Override public void write(int b) { orig.write(b); buf.write(b); }
                @Override public void write(byte[] b, int o, int l) { orig.write(b, o, l); buf.write(b, o, l); }
            }, true);
            System.setOut(tee);
            bridging.SatuSehatKirimLaporanPersalinan f = new bridging.SatuSehatKirimLaporanPersalinan(parent, false);
            n = update ? f.updateSatuNoRawat(noRawat) : f.kirimSatuNoRawat(noRawat); f.dispose();
        } catch (Exception e) {
            log.append("&nbsp;&nbsp;<font color='red'>\u2717 " + e + "</font><br>");
        } finally {
            System.setOut(orig);
        }
        for (String line : buf.toString().split("\\r?\\n")) {
            String l = line.trim();
            if (l.startsWith("URL :") || l.startsWith("Request JSON :") || l.startsWith("Result JSON :")) {
                log.append("<div style='background:#f6f6f6;border:1px solid #e0e0e0;padding:2px;margin:2px 0;font-family:monospace;font-size:9px;color:#444'>")
                   .append(l.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")).append("</div>");
            }
        }
        log.append(n > 0
            ? "&nbsp;&nbsp;<font color='green'>\u2713 " + n + " Laporan Persalinan terkirim.</font><br>"
            : "&nbsp;&nbsp;<font color='orange'>\u2022 Laporan Persalinan tidak terkirim \u2014 belum ada laporan persalinan, Encounter belum dikirim, atau sudah pernah terkirim.</font><br>");
        return n;
    }

    /** Kirim (update=false) atau Update/PUT (update=true) Laporan Anestesi via sender aslinya. */
    private int kirimAnestesi(String noRawat, StringBuilder log, boolean update) {
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.PrintStream orig = System.out;
        int n = 0;
        try {
            java.io.PrintStream tee = new java.io.PrintStream(new java.io.OutputStream() {
                @Override public void write(int b) { orig.write(b); buf.write(b); }
                @Override public void write(byte[] b, int o, int l) { orig.write(b, o, l); buf.write(b, o, l); }
            }, true);
            System.setOut(tee);
            bridging.SatuSehatKirimResumeAnestesi f = new bridging.SatuSehatKirimResumeAnestesi(parent, false);
            n = update ? f.updateSatuNoRawat(noRawat) : f.kirimSatuNoRawat(noRawat); f.dispose();
        } catch (Exception e) {
            log.append("&nbsp;&nbsp;<font color='red'>\u2717 " + e + "</font><br>");
        } finally {
            System.setOut(orig);
        }
        for (String line : buf.toString().split("\\r?\\n")) {
            String l = line.trim();
            if (l.startsWith("URL :") || l.startsWith("Request JSON :") || l.startsWith("Result JSON :")) {
                log.append("<div style='background:#f6f6f6;border:1px solid #e0e0e0;padding:2px;margin:2px 0;font-family:monospace;font-size:9px;color:#444'>")
                   .append(l.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")).append("</div>");
            }
        }
        log.append(n > 0
            ? "&nbsp;&nbsp;<font color='green'>\u2713 " + n + " Laporan Anestesi terkirim.</font><br>"
            : "&nbsp;&nbsp;<font color='orange'>\u2022 Laporan Anestesi tidak terkirim \u2014 belum ada laporan anestesi, Encounter belum dikirim, atau sudah pernah terkirim.</font><br>");
        return n;
    }

    /** Kirim (update=false) atau Update/PUT (update=true) Laporan Tindakan via sender aslinya. */
    private int kirimTindakan(String noRawat, StringBuilder log, boolean update) {
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.PrintStream orig = System.out;
        int n = 0;
        try {
            java.io.PrintStream tee = new java.io.PrintStream(new java.io.OutputStream() {
                @Override public void write(int b) { orig.write(b); buf.write(b); }
                @Override public void write(byte[] b, int o, int l) { orig.write(b, o, l); buf.write(b, o, l); }
            }, true);
            System.setOut(tee);
            bridging.SatuSehatKirimLaporanTindakan f = new bridging.SatuSehatKirimLaporanTindakan(parent, false);
            n = update ? f.updateSatuNoRawat(noRawat) : f.kirimSatuNoRawat(noRawat); f.dispose();
        } catch (Exception e) {
            log.append("&nbsp;&nbsp;<font color='red'>\u2717 " + e + "</font><br>");
        } finally {
            System.setOut(orig);
        }
        for (String line : buf.toString().split("\\r?\\n")) {
            String l = line.trim();
            if (l.startsWith("URL :") || l.startsWith("Request JSON :") || l.startsWith("Result JSON :")) {
                log.append("<div style='background:#f6f6f6;border:1px solid #e0e0e0;padding:2px;margin:2px 0;font-family:monospace;font-size:9px;color:#444'>")
                   .append(l.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")).append("</div>");
            }
        }
        log.append(n > 0
            ? "&nbsp;&nbsp;<font color='green'>\u2713 " + n + " Laporan Tindakan terkirim.</font><br>"
            : "&nbsp;&nbsp;<font color='orange'>\u2022 Laporan Tindakan tidak terkirim \u2014 belum ada laporan tindakan, Encounter belum dikirim, atau sudah pernah terkirim.</font><br>");
        return n;
    }

    /** Kirim (update=false) atau Update/PUT (update=true) Resume Medis.
     *  Ranap/ralan ditentukan dari reg_periksa.status_lanjut kunjungannya, bukan dari
     *  tab — kunjungan IGD yang dirawat inap tampil di tab IGD dan tab Ranap sekaligus,
     *  dan dulu hasilnya beda tergantung tab mana yang dipakai. */
    private int kirimResume(int tab, String noRawat, StringBuilder log, boolean update) {
        final boolean ranap = kunjunganRanap(noRawat);
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.PrintStream orig = System.out;
        int n = 0;
        try {
            java.io.PrintStream tee = new java.io.PrintStream(new java.io.OutputStream() {
                @Override public void write(int b) { orig.write(b); buf.write(b); }
                @Override public void write(byte[] b, int o, int l) { orig.write(b, o, l); buf.write(b, o, l); }
            }, true);
            System.setOut(tee);
            if (ranap) {
                bridging.SatuSehatKirimRMERawatInap f = new bridging.SatuSehatKirimRMERawatInap(parent, false);
                n = update ? f.updateSatuNoRawat(noRawat) : f.kirimSatuNoRawat(noRawat); f.dispose();
            } else {
                bridging.SatuSehatKirimRMERawatJalan f = new bridging.SatuSehatKirimRMERawatJalan(parent, false);
                n = update ? f.updateSatuNoRawat(noRawat) : f.kirimSatuNoRawat(noRawat); f.dispose();
            }
        } catch (Exception e) {
            log.append("&nbsp;&nbsp;<font color='red'>\u2717 " + e + "</font><br>");
        } finally {
            System.setOut(orig);
        }
        for (String line : buf.toString().split("\\r?\\n")) {
            String l = line.trim();
            if (l.startsWith("URL :") || l.startsWith("Request JSON :") || l.startsWith("Result JSON :")) {
                log.append("<div style='background:#f6f6f6;border:1px solid #e0e0e0;padding:2px;margin:2px 0;font-family:monospace;font-size:9px;color:#444'>")
                   .append(l.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")).append("</div>");
            }
        }
        log.append(n > 0
            ? "&nbsp;&nbsp;<font color='green'>\u2713 Resume Medis terkirim.</font><br>"
            : "&nbsp;&nbsp;<font color='orange'>\u2022 Resume tidak terkirim \u2014 belum ada resume, Encounter belum dikirim, atau sudah pernah terkirim.</font><br>");
        return n;
    }

    /** Kirim (update=false) / Update (true) Penilaian Awal Medis IGD: resource diskret
     *  (Condition keluhan/diagnosis, Observation tanda vital & fisik) + Composition
     *  Emergency medicine Note (34111-5). Sumber: penilaian_medis_igd. */
    private int kirimPenilaianIGD(int tab, String noRawat, StringBuilder log, boolean update) {
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.PrintStream orig = System.out;
        int n = 0;
        try {
            java.io.PrintStream tee = new java.io.PrintStream(new java.io.OutputStream() {
                @Override public void write(int b) { orig.write(b); buf.write(b); }
                @Override public void write(byte[] b, int o, int l) { orig.write(b, o, l); buf.write(b, o, l); }
            }, true);
            System.setOut(tee);
            bridging.SatuSehatKirimPenilaianMedisIGD f = new bridging.SatuSehatKirimPenilaianMedisIGD();
            n = update ? f.updateSatuNoRawat(noRawat) : f.kirimSatuNoRawat(noRawat);
        } catch (Exception e) {
            log.append("&nbsp;&nbsp;<font color='red'>\u2717 " + e + "</font><br>");
        } finally {
            System.setOut(orig);
        }
        for (String line : buf.toString().split("\\r?\\n")) {
            String l = line.trim();
            if (l.startsWith("URL :") || l.startsWith("Request JSON :") || l.startsWith("Result JSON :")) {
                log.append("<div style='background:#f6f6f6;border:1px solid #e0e0e0;padding:2px;margin:2px 0;font-family:monospace;font-size:9px;color:#444'>")
                   .append(l.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")).append("</div>");
            }
        }
        log.append(n > 0
            ? "&nbsp;&nbsp;<font color='green'>\u2713 Penilaian Medis IGD terkirim.</font><br>"
            : "&nbsp;&nbsp;<font color='orange'>\u2022 Penilaian Medis IGD tidak terkirim \u2014 belum ada data, Encounter belum dikirim, atau sudah pernah terkirim.</font><br>");
        return n;
    }

    /** Kirim (update=false) / Update (true) Transfer Internal RS: Observation tanda vital
     *  sebelum/sesudah transfer + Composition Transfer summary note (18761-7).
     *  Sumber: transfer_internal_rs. */
    private int kirimTransfer(String noRawat, StringBuilder log, boolean update) {
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.PrintStream orig = System.out;
        int n = 0;
        try {
            java.io.PrintStream tee = new java.io.PrintStream(new java.io.OutputStream() {
                @Override public void write(int b) { orig.write(b); buf.write(b); }
                @Override public void write(byte[] b, int o, int l) { orig.write(b, o, l); buf.write(b, o, l); }
            }, true);
            System.setOut(tee);
            bridging.SatuSehatKirimTransferInternal f = new bridging.SatuSehatKirimTransferInternal();
            n = update ? f.updateSatuNoRawat(noRawat) : f.kirimSatuNoRawat(noRawat);
        } catch (Exception e) {
            log.append("&nbsp;&nbsp;<font color='red'>\u2717 " + e + "</font><br>");
        } finally {
            System.setOut(orig);
        }
        for (String line : buf.toString().split("\\r?\\n")) {
            String l = line.trim();
            if (l.startsWith("URL :") || l.startsWith("Request JSON :") || l.startsWith("Result JSON :")) {
                log.append("<div style='background:#f6f6f6;border:1px solid #e0e0e0;padding:2px;margin:2px 0;font-family:monospace;font-size:9px;color:#444'>")
                   .append(l.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")).append("</div>");
            }
        }
        log.append(n > 0
            ? "&nbsp;&nbsp;<font color='green'>\u2713 Transfer Internal terkirim.</font><br>"
            : "&nbsp;&nbsp;<font color='orange'>\u2022 Transfer Internal tidak terkirim \u2014 belum ada data, Encounter belum dikirim, atau sudah pernah terkirim.</font><br>");
        return n;
    }

    /** Kirim (update=false) atau Update/PUT (update=true) SPRI; tangkap JSON dari console utk ditampilkan. */
    private int kirimSpri(String noRawat, StringBuilder log, boolean update) {
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.PrintStream orig = System.out;
        int n = 0;
        try {
            java.io.PrintStream tee = new java.io.PrintStream(new java.io.OutputStream() {
                @Override public void write(int b) { orig.write(b); buf.write(b); }
                @Override public void write(byte[] b, int o, int l) { orig.write(b, o, l); buf.write(b, o, l); }
            }, true);
            System.setOut(tee);
            bridging.SatuSehatKirimSPRI sp = new bridging.SatuSehatKirimSPRI(parent, false);
            n = update ? sp.updateSatuNoRawat(noRawat) : sp.kirimSatuNoRawat(noRawat);
            sp.dispose();
        } catch (Exception e) {
            log.append("&nbsp;&nbsp;<font color='red'>\u2717 " + e + "</font><br>");
        } finally {
            System.setOut(orig);
        }
        for (String line : buf.toString().split("\\r?\\n")) {
            String l = line.trim();
            if (l.startsWith("URL :") || l.startsWith("Request JSON :") || l.startsWith("Result JSON :")) {
                log.append("<div style='background:#f6f6f6;border:1px solid #e0e0e0;padding:2px;margin:2px 0;font-family:monospace;font-size:9px;color:#444'>")
                   .append(l.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")).append("</div>");
            }
        }
        if (n > 0) {
            log.append("&nbsp;&nbsp;<font color='green'>\u2713 SPRI terkirim.</font><br>");
        } else {
            log.append("&nbsp;&nbsp;<font color='orange'>\u2022 SPRI tidak terkirim \u2014 belum ada permintaan ranap, "
                     + "Encounter belum dikirim, atau sudah pernah terkirim.</font><br>");
        }
        return n;
    }

    /** Kirim Triase IGD (multi-resource) via sender aslinya; tangkap JSON dari console utk ditampilkan. */
    private int kirimTriase(String noRawat, StringBuilder log) {
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.PrintStream orig = System.out;
        int n = 0;
        try {
            java.io.PrintStream tee = new java.io.PrintStream(new java.io.OutputStream() {
                @Override public void write(int b) { orig.write(b); buf.write(b); }
                @Override public void write(byte[] b, int o, int l) { orig.write(b, o, l); buf.write(b, o, l); }
            }, true);
            System.setOut(tee);
            bridging.SatuSehatKirimTriaseIGD tr = new bridging.SatuSehatKirimTriaseIGD(parent, false);
            n = tr.kirimSatuNoRawat(noRawat);
            tr.dispose();
        } catch (Exception e) {
            log.append("&nbsp;&nbsp;<font color='red'>✗ " + e + "</font><br>");
        } finally {
            System.setOut(orig);
        }
        for (String line : buf.toString().split("\\r?\\n")) {
            String l = line.trim();
            if (l.startsWith("URL :") || l.startsWith("Request JSON :") || l.startsWith("Result JSON :")) {
                log.append("<div style='background:#f6f6f6;border:1px solid #e0e0e0;padding:2px;margin:2px 0;font-family:monospace;font-size:9px;color:#444'>")
                   .append(l.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")).append("</div>");
            }
        }
        log.append("&nbsp;&nbsp;<font color='green'>✓ ").append(n).append(" resource Triase terkirim/terisi.</font><br>");
        return n;
    }

    /** Update Triase IGD (PUT tiap sub-resource) via sender aslinya. */
    private int updateTriase(String noRawat, StringBuilder log) {
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        java.io.PrintStream orig = System.out;
        int n = 0;
        try {
            java.io.PrintStream tee = new java.io.PrintStream(new java.io.OutputStream() {
                @Override public void write(int b) { orig.write(b); buf.write(b); }
                @Override public void write(byte[] b, int o, int l) { orig.write(b, o, l); buf.write(b, o, l); }
            }, true);
            System.setOut(tee);
            bridging.SatuSehatKirimTriaseIGD tr = new bridging.SatuSehatKirimTriaseIGD(parent, false);
            n = tr.updateSatuNoRawat(noRawat);
            tr.dispose();
        } catch (Exception e) {
            log.append("&nbsp;&nbsp;<font color='red'>\u2717 " + e + "</font><br>");
        } finally {
            System.setOut(orig);
        }
        for (String line : buf.toString().split("\\r?\\n")) {
            String l = line.trim();
            if (l.startsWith("URL :") || l.startsWith("Request JSON :") || l.startsWith("Result JSON :")) {
                log.append("<div style='background:#f6f6f6;border:1px solid #e0e0e0;padding:2px;margin:2px 0;font-family:monospace;font-size:9px;color:#444'>")
                   .append(l.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")).append("</div>");
            }
        }
        log.append(n > 0
            ? "&nbsp;&nbsp;<font color='green'>\u2713 " + n + " resource Triase diperbarui.</font><br>"
            : "&nbsp;&nbsp;<font color='orange'>\u2022 Tidak ada resource Triase yang diperbarui \u2014 belum ada data triase atau belum pernah dikirim.</font><br>");
        return n;
    }

    /** Preview SEP (HTML mirip cetakan) dari bridging_sep berdasarkan kunjungan terpilih. */
    /**
     * Menyiapkan aksi kirim: menampilkan preview resource yang dipilih dan
     * mengaktifkan tombol Kirim (dan Edit untuk Triase/SPRI) di panel bawah.
     * Pengiriman baru terjadi setelah petugas menekan Kirim -- memberi kesempatan
     * meninjau isian lebih dulu.
     */
    private void siapkan(int tab, String type, String label) {
        if (selNoRawat[tab] == null || selNoRawat[tab].isEmpty()) {
            logPane[tab].setText("<html><body style='font:11px tahoma'><font color='red'>Pilih dulu kunjungan di tabel.</font></body></html>");
            return;
        }
        pendingType[tab] = type;
        pendingLabel[tab] = label;
        btnKirimNow[tab].setEnabled(izinKirim);
        btnKirimNow[tab].setText("Kirim " + label);
        // Update hanya untuk jenis single-resource yang punya jalur PUT. Dulu tombolnya
        // selalu aktif lalu menolak sendiri saat ditekan.
        boolean bisaUpdate = izinKirim && UPDATE_DIDUKUNG.contains(type);
        btnUpdateNow[tab].setEnabled(bisaUpdate);
        btnUpdateNow[tab].setText(bisaUpdate ? "Update " + label : "Update");
        btnUpdateNow[tab].setToolTipText(bisaUpdate ? "Kirim ulang (PUT) " + label + " dengan data terbaru"
                : "Update belum tersedia untuk " + label + " (multi-resource, perlu PUT tiap sub-resource)");
        switch (type) {
            case "TRIASE": previewTriase(tab); break;
            case "SPRI":   previewSPRI(tab);   break;
            case "RESUME": previewResume(tab); break;
            case "OPERASI": previewOperasi(tab); break;
            case "TINDAKAN": previewTindakan(tab); break;
            case "ANESTESI": previewAnestesi(tab); break;
            case "PERSALINAN": previewPersalinan(tab); break;
            case "KRONIS": previewObatKronis(tab); break;
            case "ALKES": previewAlkes(tab); break;
            case "LAB": previewLab(tab); break;
            case "RADIOLOGI": previewRadiologi(tab); break;
            case "OBAT": previewObat(tab); break;
            case "PENILAIANIGD": previewPenilaianIGD(tab); break;
            case "TRANSFER": previewTransfer(tab); break;
            default:       previewUmum(tab, type, label); break;
        }
    }

    /**
     * Preview ringkas untuk resource tanpa pembangun payload khusus
     * (Encounter/Condition/Procedure/Billing/Bundel): menampilkan jumlah data dan
     * prasyarat Encounter, lalu mengarahkan menekan Kirim.
     */
    private void previewUmum(final int tab, final String type, final String label) {
        final String noRawat = selNoRawat[tab];
        runBackground(() -> {
            StringBuilder b = new StringBuilder("<html><body style='font:11px tahoma;color:#222'>");
            b.append("<div style='text-align:center'><b>PREVIEW ").append(label.toUpperCase()).append("</b><br>").append(noRawat).append("</div><hr>");
            java.util.List<String> kurang = new java.util.ArrayList<>();
            try {
                String idEnc = cariId("select id_encounter from satu_sehat_encounter where no_rawat=?", noRawat);
                b.append("<table cellpadding='2' style='font:11px tahoma'>");
                if (type.equals("ENC")) {
                    // Tekan Kirim = POST bila belum ada, PUT status finished bila sudah ada.
                    // Syarat finished ditampilkan apa adanya supaya tidak menjanjikan hal
                    // yang tidak terjadi (dulu tertulis "akan memperbaruinya" padahal
                    // pengirimannya berhenti begitu saja).
                    boolean ranap = kunjunganRanap(noRawat);
                    b.append(pvRow("Status Encounter", idEnc, false));
                    if (!idEnc.isEmpty()) {
                        String selesai = cariId(ranap
                            ? "select concat(tgl_keluar,' ',jam_keluar) from kamar_inap where no_rawat=? "
                              + "and tgl_keluar is not null and tgl_keluar<>'0000-00-00' and ifnull(stts_pulang,'')<>'Pindah Kamar' "
                              + "order by tgl_keluar desc, jam_keluar desc limit 1"
                            : "select concat(tgl_perawatan,' ',jam_rawat) from pemeriksaan_ralan where no_rawat=? "
                              + "order by tgl_perawatan desc, jam_rawat desc limit 1", noRawat);
                        String idCond = cariId("select id_condition from satu_sehat_condition where no_rawat=? and ifnull(id_condition,'')<>'' limit 1", noRawat);
                        b.append(pvRow("Waktu selesai (period.end)", selesai, true));
                        if (selesai.isEmpty()) kurang.add(ranap ? "Pasien belum dipulangkan (kamar_inap.tgl_keluar kosong)"
                                                               : "Belum ada pemeriksaan sebagai penanda selesai pelayanan");
                        b.append(pvRow("Condition terkirim (diagnosis)", idCond, true));
                        if (idCond.isEmpty()) kurang.add("Encounter.diagnosis wajib untuk status finished — kirim Condition dulu");
                    }
                    b.append("</table><hr>");
                    if (idEnc.isEmpty()) {
                        b.append("Encounter belum dikirim. Tekan <b>Kirim</b> untuk membuatnya (status awal <b>")
                         .append(ranap ? "in-progress" : "arrived").append("</b>).");
                    } else if (kurang.isEmpty()) {
                        b.append("<font color='#090'><b>&#10003; Siap dituntaskan.</b></font> Menekan <b>Kirim</b> akan "
                               + "meng-<i>update</i> Encounter ke status <b>finished</b>.");
                    } else {
                        b.append("<font color='#090'>Encounter sudah dikirim.</font> Belum bisa dituntaskan ke <b>finished</b>:")
                         .append("<ul style='margin:2px 0 0 14px;padding:0'>");
                        for (String k : kurang) b.append("<li>").append(k).append("</li>");
                        b.append("</ul>");
                    }
                } else {
                    // resource yang mensyaratkan Encounter
                    b.append(pvRow("Encounter", idEnc, true));
                    if (idEnc.isEmpty()) kurang.add("Encounter belum dikirim (kirim lebih dulu)");
                    if (type.equals("COND") || type.equals("PROC")) {
                        String tabel = type.equals("COND") ? "diagnosa_pasien" : "prosedur_pasien";
                        int jml = 0;
                        try (java.sql.PreparedStatement p = koneksi.prepareStatement("select count(*) n from " + tabel + " where no_rawat=?")) {
                            p.setString(1, noRawat);
                            java.sql.ResultSet r = p.executeQuery();
                            if (r.next()) jml = r.getInt("n");
                            r.close();
                        }
                        b.append(pvRow("Jumlah " + label, jml + " item", true));
                        if (jml == 0) kurang.add("Belum ada " + label.toLowerCase() + " pada kunjungan ini");
                    }
                    b.append("</table><hr>");
                    if (kurang.isEmpty()) b.append("<font color='#090'><b>&#10003; Siap dikirim.</b></font> Tekan <b>Kirim</b>.");
                    else {
                        b.append("<font color='#c00'><b>&#10007; Belum lengkap:</b></font><ul style='margin:2px 0 0 14px;padding:0'>");
                        for (String k : kurang) b.append("<li>").append(k).append("</li>");
                        b.append("</ul>");
                    }
                }
            } catch (Exception e) {
                b.append("<font color='red'>Error: ").append(e).append("</font>");
            }
            b.append("</body></html>");
            final String h = b.toString();
            SwingUtilities.invokeLater(() -> logPane[tab].setText(h));
        });
    }

    /** Baris preview: label + nilai; nilai kosong ditandai merah "(belum diisi)". */
    // ==================================================== DICOM (dokumen/gambar)

    /**
     * Membuka daftar berkas yang sudah diunggah ke Berkas Rawat (tabel
     * berkas_digital_perawatan) untuk kunjungan terpilih. Tiap berkas diberi
     * tombol "Kirim DICOM" sendiri; AccessionNumber dibuat UNIK per dokumen
     * ({no_rawat tanpa '/'} + "D" + urutan), jadi tiap berkas jadi ImagingStudy
     * tersendiri di SatuSehat.
     */
    private void bukaDaftarBerkasDicom(int tab) {
        final String noRawat = selNoRawat[tab];
        if (noRawat == null || noRawat.isEmpty()) {
            logPane[tab].setText("<html><body style='font:11px tahoma'><font color='red'>Pilih dulu kunjungan di tabel.</font></body></html>");
            return;
        }
        java.util.List<String[]> berkas = new java.util.ArrayList<>();   // {nama, lokasi}
        try (PreparedStatement p = koneksi.prepareStatement(
                "SELECT master_berkas_digital.nama, berkas_digital_perawatan.lokasi_file, berkas_digital_perawatan.kode "
              + "FROM berkas_digital_perawatan INNER JOIN master_berkas_digital "
              + "ON berkas_digital_perawatan.kode=master_berkas_digital.kode "
              + "WHERE berkas_digital_perawatan.no_rawat=? "
              + "ORDER BY berkas_digital_perawatan.kode ASC, berkas_digital_perawatan.lokasi_file ASC")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) berkas.add(new String[]{ nz(r.getString(1)), nz(r.getString(2)), nz(r.getString(3)) });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal membaca berkas rawat: " + e.getMessage());
            return;
        }
        if (berkas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Belum ada berkas pada kunjungan ini.\n"
                    + "Unggah dulu lewat menu Berkas Rawat.", "Dokumen DICOM", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String encTmp = "";
        try { encTmp = metaDicom(noRawat).get("encounter_id"); } catch (Exception ignore) {}
        final boolean encAda = encTmp != null && !encTmp.isEmpty();
        final String noDigit = noRawat.replace("/", "");

        // Berkas yang SUDAH terkirim (dari penjejak satu_sehat_dicom_berkas).
        final java.util.Set<String> sudahKirim = new java.util.HashSet<>();
        try (PreparedStatement p = koneksi.prepareStatement(
                "select accession from satu_sehat_dicom_berkas where no_rawat=?")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) { while (r.next()) sudahKirim.add(nz(r.getString(1))); }
        } catch (Exception ignore) {}

        // Area log proses (dibagi semua baris).
        final javax.swing.JTextArea log = new javax.swing.JTextArea(7, 60);
        log.setEditable(false);
        log.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 11));

        JPanel isi = new JPanel();
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));
        // AccessionNumber = {no_rawat tanpa '/'} + kode berkas + "-" + hash lokasi_file.
        // Satu kunjungan bisa punya >1 berkas kode sama (PK no_rawat+kode+lokasi_file);
        // hash lokasi_file mengunci accession ke berkasnya sendiri sehingga tidak
        // pernah bergeser walau ada berkas lain dihapus/disisipkan.
        for (String[] b : berkas) {
            final String nama = b[0], lokasi = b[1], kode = b[2];
            final String acsn = noDigit + kode + "-" + hashPendek(lokasi);
            String e = lokasi.contains(".") ? lokasi.substring(lokasi.lastIndexOf('.') + 1).toLowerCase() : "";
            final boolean didukung = e.equals("pdf") || e.equals("jpg") || e.equals("jpeg") || e.equals("png");
            final boolean terkirim = sudahKirim.contains(acsn);

            JPanel row = new JPanel(new BorderLayout(6, 2));
            row.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xDD, 0xDD, 0xDD)),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            javax.swing.JLabel info = new javax.swing.JLabel("<html><b>" + escHtmlDicom(nama) + "</b>"
                    + "<br><font color='#777'>" + escHtmlDicom(lokasi) + "</font>"
                    + "<br><font color='#068'>ACSN: " + acsn + (didukung ? "" : "  &mdash; tipe tak didukung") + "</font></html>");
            info.setPreferredSize(new Dimension(370, 48));
            row.add(info, BorderLayout.CENTER);

            javax.swing.JLabel status = new javax.swing.JLabel(terkirim ? "● Sudah terkirim" : "○ Belum");
            status.setForeground(terkirim ? new Color(0x2E, 0x7D, 0x32) : new Color(0x99, 0x99, 0x99));
            status.setPreferredSize(new Dimension(140, 20));
            widget.Button bLihat = new widget.Button(); bLihat.setText("Lihat");
            bLihat.addActionListener(ev -> bukaBrowserBerkas(lokasi));
            widget.Button bKirim = new widget.Button(); bKirim.setText(terkirim ? "Kirim ulang" : "Kirim DICOM");
            bKirim.setEnabled(didukung && izinKirim && encAda);
            if (!encAda) bKirim.setToolTipText("Encounter belum dikirim ke SatuSehat");
            bKirim.addActionListener(ev -> { bKirim.setEnabled(false);
                    kirimSatuBerkasDicom(noRawat, lokasi, nama, acsn, status, bKirim, log); });

            JPanel aksi = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 4, 0));
            aksi.add(status); aksi.add(bLihat); aksi.add(bKirim);
            row.add(aksi, BorderLayout.EAST);
            isi.add(row);
        }
        javax.swing.JScrollPane sc = new javax.swing.JScrollPane(isi);
        sc.setPreferredSize(new Dimension(760, Math.min(360, 40 + berkas.size() * 62)));
        javax.swing.JScrollPane scLog = new javax.swing.JScrollPane(log);
        scLog.setBorder(BorderFactory.createTitledBorder("Proses pengiriman"));
        scLog.setPreferredSize(new Dimension(760, 150));
        JPanel wadah = new JPanel(new BorderLayout(4, 6));
        wadah.add(sc, BorderLayout.CENTER);
        wadah.add(scLog, BorderLayout.SOUTH);
        JOptionPane.showMessageDialog(this, wadah,
                "Kirim Berkas Rawat ke DICOM — " + noRawat + (encAda ? "" : "  (Encounter belum dikirim!)"),
                JOptionPane.PLAIN_MESSAGE);
    }

    /** Tulis satu baris ke area log proses (di EDT), auto-scroll ke bawah. */
    private void logProses(javax.swing.JTextArea log, String s) {
        final String baris = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()) + "  " + s + "\n";
        SwingUtilities.invokeLater(() -> { log.append(baris); log.setCaretPosition(log.getDocument().getLength()); });
    }

    /** URL HTTP berkas rawat, sama seperti DlgBerkasRawat2.viewBerkas. */
    private String urlBerkas(String lokasi) {
        String clean = lokasi;
        if (clean.startsWith("berkasrawat/")) clean = clean.substring("berkasrawat/".length());
        if (clean.startsWith("/")) clean = clean.substring(1);
        return "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB()
                + "/" + koneksiDB.HYBRIDWEB() + "/berkasrawat/" + clean;
    }

    /** Buka berkas di browser. */
    private void bukaBrowserBerkas(String lokasi) {
        try { java.awt.Desktop.getDesktop().browse(java.net.URI.create(urlBerkas(lokasi))); }
        catch (Exception e) { JOptionPane.showMessageDialog(this, "Gagal membuka berkas: " + e.getMessage()); }
    }

    /** Unduh berkas rawat ke berkas sementara ber-ekstensi asli. */
    private java.io.File unduhBerkas(String url, String ext) throws Exception {
        java.net.HttpURLConnection c = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        c.setConnectTimeout(20000); c.setReadTimeout(60000);
        int kode = c.getResponseCode();
        if (kode < 200 || kode >= 300) throw new Exception("unduh berkas HTTP " + kode);
        java.io.File tmp = java.io.File.createTempFile("berkasdcm_", "." + (ext.isEmpty() ? "bin" : ext));
        try (java.io.InputStream in = c.getInputStream()) {
            java.nio.file.Files.copy(in, tmp.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        return tmp;
    }

    /**
     * Unduh satu berkas rawat lalu kirim ke RIS sebagai DICOM (accession unik).
     * Tiap langkah ditulis ke area {@code log}; bila tuntas, dicatat ke penjejak
     * satu_sehat_dicom_berkas dan status baris jadi "Sudah terkirim".
     */
    private void kirimSatuBerkasDicom(String noRawat, String lokasi, String nama, String acsn,
                                      javax.swing.JLabel status, widget.Button btn, javax.swing.JTextArea log) {
        final String base = koneksiDB.URLDICOMRIS().trim();
        if (base.isEmpty()) {
            status.setText("URLDICOMRIS kosong"); status.setForeground(Color.RED); btn.setEnabled(true);
            logProses(log, "✗ URLDICOMRIS belum diisi di setting/database.xml"); return;
        }
        status.setText("mengirim..."); status.setForeground(new Color(0x33, 0x66, 0x99));
        final String ext = lokasi.contains(".") ? lokasi.substring(lokasi.lastIndexOf('.') + 1).toLowerCase() : "";
        logProses(log, "── " + nama + "  [" + acsn + "]");
        runBackground(() -> {
            String hasil; boolean ok = false;
            java.io.File tmp = null;
            try {
                logProses(log, "   unduh berkas dari web rawat...");
                tmp = unduhBerkas(urlBerkas(lokasi), ext);
                logProses(log, "   unduh OK (" + (tmp.length() / 1024) + " KB) — konversi DICOM + kirim ke RIS...");
                java.util.Map<String, String> m = metaDicom(noRawat);
                m.put("accession_number", acsn);                 // unik per dokumen
                m.put("document_title", nama);
                m.put("study_description", "Berkas Rawat: " + nama);
                m.put("send_to_satusehat", "true");
                String url = base.replaceAll("/+$", "") + "/api/v1/tools/document-to-dicom";
                String resp = postMultipart(url, koneksiDB.USERDICOMRIS(), koneksiDB.PASSDICOMRIS(), tmp, m);

                JsonNode root = new ObjectMapper().readTree(resp);
                String orth = root.path("orthanc").path("Status").asText();
                JsonNode ss = root.path("satusehat");
                String isId = ss.path("imaging_study").path("id").asText();
                String stow = ss.path("stow").path("status").asText();
                boolean sent = ss.path("sent").asBoolean(false);
                String err  = ss.path("error").asText();
                if (!orth.isEmpty()) logProses(log, "   Orthanc: " + orth);
                if (!isId.isEmpty()) logProses(log, "   ImagingStudy dibuat: " + isId);
                if (!stow.isEmpty()) logProses(log, "   STOW SatuSehat: " + stow);
                if (!err.isEmpty())  logProses(log, "   Catatan SatuSehat: " + err);

                ok = sent && !isId.isEmpty();
                if (ok) {
                    try (PreparedStatement ps = koneksi.prepareStatement(
                            "replace into satu_sehat_dicom_berkas (no_rawat,accession,lokasi_file,jenis_berkas,"
                          + "id_imagingstudy,study_instance_uid,sop_instance_uid,tgl_kirim) values (?,?,?,?,?,?,?,now())")) {
                        ps.setString(1, noRawat); ps.setString(2, acsn); ps.setString(3, lokasi); ps.setString(4, nama);
                        ps.setString(5, isId);
                        ps.setString(6, root.path("study_instance_uid").asText());
                        ps.setString(7, root.path("sop_instance_uid").asText());
                        ps.executeUpdate();
                    }
                    logProses(log, "   ✓ SELESAI — tersimpan sebagai ImagingStudy & tercatat.");
                    hasil = "● Sudah terkirim";
                } else {
                    logProses(log, "   ✗ Belum tuntas.");
                    hasil = "gagal (lihat proses)";
                }
            } catch (Exception e) {
                hasil = "gagal";
                logProses(log, "   ✗ GAGAL: " + e.getMessage());
            } finally { if (tmp != null) tmp.delete(); }
            final String h = hasil; final boolean okF = ok;
            SwingUtilities.invokeLater(() -> {
                status.setText(h);
                status.setForeground(okF ? new Color(0x2E, 0x7D, 0x32) : Color.RED);
                btn.setText("Kirim ulang");
                btn.setEnabled(true);      // boleh kirim ulang (sukses maupun gagal)
            });
        });
    }

    /**
     * Metadata DICOM dirakit dari kunjungan terpilih.
     *
     * AccessionNumber memakai no_rawat TANPA garis miring: bentuk aslinya 17
     * karakter, melebihi batas VR SH DICOM yang 16; dibuang garis miringnya
     * menjadi 14 karakter dan tetap bisa dikembalikan ke bentuk semula.
     */
    private java.util.Map<String, String> metaDicom(String noRawat) throws Exception {
        java.util.Map<String, String> m = new java.util.LinkedHashMap<>();
        m.put("patient_id", ""); m.put("patient_name", ""); m.put("birth_date", "");
        m.put("sex", ""); m.put("encounter_id", ""); m.put("study_date", "");
        try (PreparedStatement p = koneksi.prepareStatement(
                "select pasien.no_rkm_medis, pasien.nm_pasien, pasien.tgl_lahir, pasien.jk, "
              + "ifnull(sse.id_encounter,'') as id_encounter, date(reg_periksa.tgl_registrasi) as tgl "
              + "from reg_periksa inner join pasien on pasien.no_rkm_medis=reg_periksa.no_rkm_medis "
              + "left join satu_sehat_encounter sse on sse.no_rawat=reg_periksa.no_rawat "
              + "where reg_periksa.no_rawat=?")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    m.put("patient_id", nz(r.getString("no_rkm_medis")));
                    m.put("patient_name", nz(r.getString("nm_pasien")));
                    m.put("birth_date", nz(r.getString("tgl_lahir")));
                    m.put("sex", "P".equalsIgnoreCase(nz(r.getString("jk"))) ? "F" : "M");
                    m.put("encounter_id", nz(r.getString("id_encounter")));
                    m.put("study_date", nz(r.getString("tgl")));
                }
            }
        }
        m.put("accession_number", noRawat.replace("/", ""));
        return m;
    }

    private static String nz(String s) { return s == null ? "" : s; }

    /**
     * 6 heksadesimal dari SHA-256 lokasi_file -- pembeda AccessionNumber yang
     * terkunci per berkas (tak bergeser walau daftar berkas berubah). Cukup untuk
     * membedakan berkas dalam satu (no_rawat, kode); peluang tabrakan sangat kecil.
     */
    private static String hashPendek(String s) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] h = md.digest((s == null ? "" : s).getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 3; i++) sb.append(String.format("%02x", h[i]));
            return sb.toString();
        } catch (Exception e) {
            return Integer.toHexString((s == null ? "" : s).hashCode() & 0xFFFFFF);
        }
    }


    private static String escHtmlDicom(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    /**
     * POST multipart/form-data sederhana memakai HttpURLConnection supaya tidak
     * menambah pustaka baru. Basic Auth dipakai bila pengguna/sandi terisi.
     */
    private String postMultipart(String url, String user, String pass,
                                 java.io.File file, java.util.Map<String, String> fields) throws Exception {
        String batas = "----KhanzaDicom" + System.currentTimeMillis();
        java.net.HttpURLConnection c = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        c.setRequestMethod("POST");
        c.setDoOutput(true);
        c.setConnectTimeout(20000);
        c.setReadTimeout(120000);
        c.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + batas);
        if (user != null && !user.trim().isEmpty()) {
            String kredensial = java.util.Base64.getEncoder().encodeToString(
                    (user + ":" + (pass == null ? "" : pass)).getBytes("UTF-8"));
            c.setRequestProperty("Authorization", "Basic " + kredensial);
        }
        try (java.io.DataOutputStream out = new java.io.DataOutputStream(c.getOutputStream())) {
            for (java.util.Map.Entry<String, String> e : fields.entrySet()) {
                out.writeBytes("--" + batas + "\r\n");
                out.writeBytes("Content-Disposition: form-data; name=\"" + e.getKey() + "\"\r\n\r\n");
                out.write((e.getValue() == null ? "" : e.getValue()).getBytes("UTF-8"));
                out.writeBytes("\r\n");
            }
            out.writeBytes("--" + batas + "\r\n");
            out.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"\r\n");
            out.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
            java.nio.file.Files.copy(file.toPath(), out);
            out.writeBytes("\r\n--" + batas + "--\r\n");
        }
        int kode = c.getResponseCode();
        java.io.InputStream is = (kode >= 200 && kode < 300) ? c.getInputStream() : c.getErrorStream();
        StringBuilder sb = new StringBuilder();
        if (is != null) {
            try (java.io.BufferedReader br = new java.io.BufferedReader(
                    new java.io.InputStreamReader(is, "UTF-8"))) {
                String ln; while ((ln = br.readLine()) != null) sb.append(ln).append("\n");
            }
        }
        if (kode < 200 || kode >= 300) throw new Exception("HTTP " + kode + " : " + sb);
        return sb.toString();
    }

    private String pvRow(String label, String nilai, boolean wajib) {
        boolean kosong = nilai == null || nilai.trim().isEmpty() || nilai.trim().equals("-");
        String isi = kosong
                ? "<font color='" + (wajib ? "#c00" : "#c80") + "'>(belum " + (wajib ? "diisi" : "ada") + ")</font>"
                : nilai;
        return "<tr><td valign='top'><b>" + label + "</b></td><td valign='top'>" + isi + "</td></tr>";
    }

    /**
     * Preview isian Triase IGD sebelum dikirim, lengkap dengan cek kelengkapan.
     * Menampilkan apa yang akan masuk payload dan menandai bagian yang belum
     * lengkap,
     * sebelum benar-benar mengirim.
     */
    private void previewTriase(final int tab) {
        final String noRawat = selNoRawat[tab];
        if (noRawat == null || noRawat.isEmpty()) {
            logPane[tab].setText("<html><body style='font:11px tahoma'><font color='red'>Pilih dulu kunjungan di tabel.</font></body></html>");
            return;
        }
        runBackground(() -> {
            StringBuilder b = new StringBuilder("<html><body style='font:11px tahoma;color:#222'>");
            b.append("<div style='text-align:center'><b>PREVIEW TRIASE IGD</b><br>").append(noRawat).append("</div><hr>");
            java.util.List<String> kurang = new java.util.ArrayList<>();
            try {
                String idEnc = cariId("select id_encounter from satu_sehat_encounter where no_rawat=?", noRawat);
                if (idEnc.isEmpty()) kurang.add("Encounter belum dikirim (wajib, kirim lebih dulu)");

                b.append("<table cellpadding='2' style='font:11px tahoma'>");
                b.append(pvRow("Encounter", idEnc.isEmpty() ? "" : idEnc, true));

                // vital + kedatangan dari data_triase_igd
                try (PreparedStatement p = koneksi.prepareStatement(
                        "select cara_masuk, tekanan_darah, nadi, pernapasan, suhu, saturasi_o2, nyeri from data_triase_igd where no_rawat=?")) {
                    p.setString(1, noRawat);
                    ResultSet r = p.executeQuery();
                    if (r.next()) {
                        b.append(pvRow("Cara Masuk", r.getString("cara_masuk"), false));
                        b.append(pvRow("Tekanan Darah", r.getString("tekanan_darah"), false));
                        b.append(pvRow("Nadi", r.getString("nadi"), false));
                        b.append(pvRow("Pernapasan", r.getString("pernapasan"), false));
                        b.append(pvRow("Suhu", r.getString("suhu"), false));
                        b.append(pvRow("SpO2", r.getString("saturasi_o2"), false));
                        b.append(pvRow("Nyeri", r.getString("nyeri"), false));
                    } else {
                        b.append(pvRow("Vital Sign", "", true));
                        kurang.add("Data triase (vital sign) belum diisi");
                    }
                    r.close();
                }

                // keluhan/plan dari primer atau sekunder
                String keluhan = cariId("select keluhan_utama from data_triase_igdprimer where no_rawat=?", noRawat);
                String jenis = "Primer";
                if (keluhan.isEmpty()) {
                    keluhan = cariId("select anamnesa_singkat from data_triase_igdsekunder where no_rawat=?", noRawat);
                    jenis = "Sekunder";
                }
                b.append(pvRow("Jenis Triase", keluhan.isEmpty() ? "" : jenis, false));
                b.append(pvRow("Keluhan/Anamnesa", keluhan, true));
                if (keluhan.isEmpty()) kurang.add("Keluhan utama / anamnesa belum diisi");

                // ABCD dari rantai detail_skala: gabungan lima skala
                StringBuilder abcd = new StringBuilder();
                StringBuilder union = new StringBuilder();
                for (int sk = 1; sk <= 5; sk++) {
                    if (sk > 1) union.append(" union all ");
                    union.append("select m.kode_pemeriksaan, m.pengkajian_skala").append(sk).append(" temuan ")
                         .append("from data_triase_igddetail_skala").append(sk).append(" d join master_triase_skala").append(sk)
                         .append(" m on m.kode_skala").append(sk).append("=d.kode_skala").append(sk).append(" where d.no_rawat=?");
                }
                try (PreparedStatement p = koneksi.prepareStatement(
                        "select pp.nama_pemeriksaan, group_concat(t.temuan order by t.temuan separator ', ') teks from ("
                      + union + ") t join master_triase_pemeriksaan pp on pp.kode_pemeriksaan=t.kode_pemeriksaan "
                      + "group by pp.kode_pemeriksaan, pp.nama_pemeriksaan")) {
                    for (int sk = 1; sk <= 5; sk++) p.setString(sk, noRawat);
                    ResultSet r = p.executeQuery();
                    while (r.next()) {
                        if (abcd.length() > 0) abcd.append("<br>");
                        abcd.append("&bull; ").append(r.getString("nama_pemeriksaan")).append(": ").append(r.getString("teks"));
                    }
                    r.close();
                }
                b.append(pvRow("Penilaian ABCDE", abcd.toString(), false));
                if (abcd.length() == 0) kurang.add("Penilaian ABCDE (centang Pemeriksaan) belum diisi");

                b.append("</table>");
            } catch (Exception e) {
                b.append("<font color='red'>Error: ").append(e).append("</font>");
            }

            b.append("<hr>");
            if (kurang.isEmpty()) {
                b.append("<font color='#090'><b>&#10003; Siap dikirim.</b></font>");
            } else {
                b.append("<font color='#c00'><b>&#10007; Belum lengkap:</b></font><ul style='margin:2px 0 0 14px;padding:0'>");
                for (String k : kurang) b.append("<li>").append(k).append("</li>");
                b.append("</ul>");
            }
            b.append("</body></html>");
            final String h = b.toString();
            SwingUtilities.invokeLater(() -> logPane[tab].setText(h));
        });
    }

    /** Preview Lab: daftar pemeriksaan lab kunjungan (periksa_lab). Pengiriman dibahas kemudian. */
    private void previewLab(final int tab) {
        final String noRawat = selNoRawat[tab];
        if (noRawat == null || noRawat.isEmpty()) {
            logPane[tab].setText("<html><body style='font:11px tahoma'><font color='red'>Pilih dulu kunjungan di tabel.</font></body></html>");
            return;
        }
        runBackground(() -> {
            StringBuilder b = new StringBuilder("<html><body style='font:11px tahoma;color:#222'>");
            b.append("<div style='text-align:center'><b>PREVIEW LAB</b><br>").append(noRawat).append("</div><hr>");
            b.append("<font color='#c80'>Alur pengiriman Lab (ServiceRequest/Specimen/Observation/DiagnosticReport, "
                   + "PK & MB) belum diaktifkan di Bundel \u2014 menunggu pembahasan.</font><hr>");
            try {
                b.append("<table cellpadding='2' style='font:11px tahoma'>");
                int jml = 0;
                try (PreparedStatement p = koneksi.prepareStatement(
                        "select pl.tgl_periksa, ifnull(jpl.nm_perawatan,pl.kd_jenis_prw) nm "
                      + "from periksa_lab pl left join jns_perawatan_lab jpl on jpl.kd_jenis_prw=pl.kd_jenis_prw "
                      + "where pl.no_rawat=? group by pl.tgl_periksa, pl.kd_jenis_prw, jpl.nm_perawatan order by pl.tgl_periksa")) {
                    p.setString(1, noRawat);
                    try (ResultSet r = p.executeQuery()) {
                        while (r.next()) {
                            jml++;
                            b.append("<tr><td><b>").append(r.getString("nm")).append("</b></td><td style='color:#999'>")
                             .append(r.getString("tgl_periksa")).append("</td></tr>");
                        }
                    }
                }
                if (jml == 0) b.append("<tr><td colspan='2'><i>Tidak ada pemeriksaan lab pada kunjungan ini.</i></td></tr>");
                b.append("</table>");
            } catch (Exception e) {
                b.append("<font color='red'>Error: ").append(e).append("</font>");
            }
            b.append("</body></html>");
            final String h = b.toString();
            SwingUtilities.invokeLater(() -> logPane[tab].setText(h));
        });
    }

    /** Preview Alkes: daftar alkes (databarang jenis alkes) yang dipakai pasien pada kunjungan. */
    private void previewAlkes(final int tab) {
        final String noRawat = selNoRawat[tab];
        if (noRawat == null || noRawat.isEmpty()) {
            logPane[tab].setText("<html><body style='font:11px tahoma'><font color='red'>Pilih dulu kunjungan di tabel.</font></body></html>");
            return;
        }
        runBackground(() -> {
            StringBuilder b = new StringBuilder("<html><body style='font:11px tahoma;color:#222'>");
            b.append("<div style='text-align:center'><b>PREVIEW ALKES</b><br>").append(noRawat).append("</div><hr>");
            b.append("<i>Menekan Kirim mengirim 6 resource per alkes: Device (bila belum), DeviceRequest, "
                   + "DeviceDispense (extension pada DeviceRequest), DeviceUseStatement, SupplyRequest, "
                   + "SupplyDelivery.</i><hr>");
            try {
                b.append("<table cellpadding='2' style='font:11px tahoma'>");
                int jml = 0, tanpaDevice = 0;
                try (PreparedStatement p = koneksi.prepareStatement(
                        "select dpo.kode_brng, ifnull(db.nama_brng,'') nama, sum(dpo.jml) jml, "
                      + "ifnull(ssd.id_device,'') id_device "
                      + "from detail_pemberian_obat dpo "
                      + "join databarang db on db.kode_brng=dpo.kode_brng "
                      + "join jenis j on j.kdjns=db.kdjns and ("+SatuSehatAlkes.filter("j")+") "
                      + "left join satu_sehat_device ssd on ssd.kode_brng=dpo.kode_brng "
                      + "where dpo.no_rawat=? group by dpo.kode_brng, db.nama_brng, ssd.id_device order by db.nama_brng")) {
                    p.setString(1, noRawat);
                    try (ResultSet r = p.executeQuery()) {
                        while (r.next()) {
                            jml++;
                            boolean adaDevice = !r.getString("id_device").isEmpty();
                            if (!adaDevice) tanpaDevice++;
                            b.append("<tr><td><b>").append(r.getString("nama")).append("</b> x").append(r.getString("jml"))
                             .append("</td><td>").append(adaDevice ? "<font color='#090'>Device termapping</font>"
                                     : "<font color='#c00'>Device belum dikirim/dimapping</font>").append("</td></tr>");
                        }
                    }
                }
                if (jml == 0) b.append("<tr><td colspan='2'><i>Tidak ada alkes pada kunjungan ini.</i></td></tr>");
                b.append("</table>");
                if (tanpaDevice > 0) b.append("<br><font color='#c00'>").append(tanpaDevice)
                        .append(" alkes belum punya Device SatuSehat (kirim Device dulu di menu Alkes).</font>");
            } catch (Exception e) {
                b.append("<font color='red'>Error: ").append(e).append("</font>");
            }
            b.append("</body></html>");
            final String h = b.toString();
            SwingUtilities.invokeLater(() -> logPane[tab].setText(h));
        });
    }

    /** Baris status obat kronis: hitung berapa item terkirim dari resep apotek pasien kunjungan ini. */
    private String barisStatusObatKronis(String noRawat) {
        int total = 0, terkirim = 0;
        try (PreparedStatement ps = koneksi.prepareStatement(
                "select count(*) total, sum(ifnull(ssok.id_medicationdispense,'')<>'') terkirim "
              + "from bridging_resep_apotek_bpjs h "
              + "join bridging_resep_apotek_bpjs_nonracikan d on d.no_sep_apotek=h.no_sep_apotek "
              + "join pasien p on p.no_peserta=h.no_kartu "
              + "join reg_periksa rpx on rpx.no_rkm_medis=p.no_rkm_medis and rpx.no_rawat=? "
              + "left join satu_sehat_obat_kronis ssok on ssok.no_sep_apotek=h.no_sep_apotek and ssok.kode_brng=d.kode_brng")) {
            ps.setString(1, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) { total = rs.getInt("total"); terkirim = rs.getInt("terkirim"); }
            }
        } catch (Exception e) { System.out.println("Notif status obat kronis : " + e); }
        if (total == 0) return "<tr><td><b>Obat Kronis</b></td><td style='color:#999'>&#10007; belum ada</td></tr>";
        String warna = terkirim == total ? "#090" : (terkirim > 0 ? "#c80" : "#999");
        String tanda = terkirim > 0 ? "&#10003;" : "&#10007;";
        return "<tr><td><b>Obat Kronis</b></td><td style='color:" + warna + "'>" + tanda + " " + terkirim + "/" + total + " terkirim</td></tr>";
    }

    /**
     * Preview Obat Kronis sebelum dikirim. Resep apotek BPJS terkait pasien
     * kunjungan (no_kartu), bukan no_rawat langsung.
     */
    private void previewObatKronis(final int tab) {
        final String noRawat = selNoRawat[tab];
        if (noRawat == null || noRawat.isEmpty()) {
            logPane[tab].setText("<html><body style='font:11px tahoma'><font color='red'>Pilih dulu kunjungan di tabel.</font></body></html>");
            return;
        }
        runBackground(() -> {
            StringBuilder b = new StringBuilder("<html><body style='font:11px tahoma;color:#222'>");
            b.append("<div style='text-align:center'><b>PREVIEW OBAT KRONIS (>7 hari, JKN)</b><br>").append(noRawat).append("</div><hr>");
            java.util.List<String> kurang = new java.util.ArrayList<>();
            try {
                String idEnc = cariId("select id_encounter from satu_sehat_encounter where no_rawat=?", noRawat);
                if (idEnc.isEmpty()) kurang.add("Encounter belum dikirim (wajib, kirim lebih dulu)");
                b.append("<table cellpadding='2' style='font:11px tahoma'>");
                b.append(pvRow("Encounter", idEnc, true));
                int jml = 0, tanpaKfa = 0;
                try (PreparedStatement p = koneksi.prepareStatement(
                        "select h.no_sep_apotek, d.kode_brng, ifnull(db.nama_brng,'') nama, ifnull(m.obat_code,'') kfa, "
                      + "d.jml_obat, ifnull(ssok.id_medicationdispense,'') terkirim "
                      + "from bridging_resep_apotek_bpjs h "
                      + "join bridging_resep_apotek_bpjs_nonracikan d on d.no_sep_apotek=h.no_sep_apotek "
                      + "join pasien p on p.no_peserta=h.no_kartu "
                      + "join reg_periksa rpx on rpx.no_rkm_medis=p.no_rkm_medis and rpx.no_rawat=? "
                      + "left join databarang db on db.kode_brng=d.kode_brng "
                      + "left join satu_sehat_mapping_obat m on m.kode_brng=d.kode_brng "
                      + "left join satu_sehat_obat_kronis ssok on ssok.no_sep_apotek=h.no_sep_apotek and ssok.kode_brng=d.kode_brng "
                      + "order by h.tgl_pelayanan")) {
                    p.setString(1, noRawat);
                    try (ResultSet r = p.executeQuery()) {
                        while (r.next()) {
                            jml++;
                            String kfa = r.getString("kfa");
                            boolean sudah = !r.getString("terkirim").isEmpty();
                            String ket = r.getString("nama") + " x" + r.getString("jml_obat")
                                    + (kfa.isEmpty() ? " <font color='#c00'>(KFA belum dipetakan)</font>" : " <font color='#999'>[KFA " + kfa + "]</font>")
                                    + (sudah ? " <font color='#090'>&#10003; terkirim</font>" : "");
                            if (kfa.isEmpty()) tanpaKfa++;
                            b.append("<tr><td><b>").append(r.getString("no_sep_apotek")).append("</b></td><td>").append(ket).append("</td></tr>");
                        }
                    }
                }
                if (jml == 0) { b.append(pvRow("Obat Kronis", "", true)); kurang.add("Belum ada resep apotek kronis untuk pasien kunjungan ini"); }
                else if (tanpaKfa > 0) kurang.add(tanpaKfa + " obat belum punya pemetaan KFA (tidak akan terkirim)");
                b.append("</table>");
            } catch (Exception e) {
                b.append("<font color='red'>Error: ").append(e).append("</font>");
            }
            b.append("<hr>");
            if (kurang.isEmpty()) b.append("<font color='#090'><b>&#10003; Siap dikirim.</b></font> Tekan <b>Kirim</b>.");
            else {
                b.append("<font color='#c00'><b>&#10007; Perhatian:</b></font><ul style='margin:2px 0 0 14px;padding:0'>");
                for (String k : kurang) b.append("<li>").append(k).append("</li>");
                b.append("</ul>");
            }
            b.append("</body></html>");
            final String h = b.toString();
            SwingUtilities.invokeLater(() -> logPane[tab].setText(h));
        });
    }

    /**
     * Preview Laporan Persalinan (Composition) sebelum dikirim. Membaca
     * catatan_persalinan. Satu kunjungan bisa punya beberapa record (per mulai).
     */
    private void previewPersalinan(final int tab) {
        final String noRawat = selNoRawat[tab];
        if (noRawat == null || noRawat.isEmpty()) {
            logPane[tab].setText("<html><body style='font:11px tahoma'><font color='red'>Pilih dulu kunjungan di tabel.</font></body></html>");
            return;
        }
        runBackground(() -> {
            StringBuilder b = new StringBuilder("<html><body style='font:11px tahoma;color:#222'>");
            b.append("<div style='text-align:center'><b>PREVIEW LAPORAN PERSALINAN</b><br>").append(noRawat).append("</div><hr>");
            java.util.List<String> kurang = new java.util.ArrayList<>();
            try {
                String idEnc = cariId("select id_encounter from satu_sehat_encounter where no_rawat=?", noRawat);
                if (idEnc.isEmpty()) kurang.add("Encounter belum dikirim (wajib, kirim lebih dulu)");
                b.append("<table cellpadding='2' style='font:11px tahoma'>");
                b.append(pvRow("Encounter", idEnc, true));
                int jml = 0;
                try (PreparedStatement p = koneksi.prepareStatement(
                        "select mulai, perineum, anak, status_lahir, apgar_score, bb, pb, "
                      + "darah_keluar_jumlah, placenta, pengobatan, kd_dokter from catatan_persalinan where no_rawat=? order by mulai")) {
                    p.setString(1, noRawat);
                    try (ResultSet r = p.executeQuery()) {
                        while (r.next()) {
                            jml++;
                            b.append("<tr><td colspan='2'><hr style='border:0;border-top:1px dashed #ccc'></td></tr>");
                            b.append(pvRow("Waktu", r.getString("mulai"), false));
                            b.append(pvRow("Perineum", r.getString("perineum"), false));
                            b.append(pvRow("Bayi", (r.getString("anak")+", "+r.getString("status_lahir")+", BB "+r.getString("bb")+"g, PB "+r.getString("pb")+"cm").trim(), true));
                            b.append(pvRow("APGAR", r.getString("apgar_score"), false));
                            b.append(pvRow("Plasenta", r.getString("placenta"), false));
                            b.append(pvRow("Perdarahan total", r.getString("darah_keluar_jumlah"), false));
                            b.append(pvRow("Pengobatan", r.getString("pengobatan"), false));
                            b.append(pvRow("Penolong (kd_dokter)", r.getString("kd_dokter"), true));
                        }
                    }
                }
                if (jml == 0) { b.append(pvRow("Laporan Persalinan", "", true)); kurang.add("Belum ada catatan persalinan (isi di RMPartograf/RMCatatanPersalinan)"); }
                b.append("</table>");
                if (jml > 1) b.append("<i>").append(jml).append(" record persalinan akan dikirim untuk kunjungan ini.</i><br>");

                // Info resource terstruktur (entry-based, Modul INC) yang akan dibuat/di-link.
                if (jml > 0) {
                    String kala = cariId("select concat_ws('/',waktu_persalinan_kala_1,waktu_persalinan_kala_2,waktu_persalinan_kala_3) from catatan_persalinan where no_rawat=? order by mulai limit 1", noRawat);
                    String ihsBayi = cariId("select ifnull(ihs_bayi,'') from satu_sehat_bayi_baru_lahir where no_rawat=?", noRawat);
                    int nApgar = hitung("select count(*) from penilaian_bayi_baru_lahir where no_rawat=? and (ifnull(n1,'')<>'' or ifnull(n5,'')<>'' or ifnull(n10,'')<>'')", noRawat);
                    b.append("<div style='margin-top:6px'><b>Resource terstruktur (entry) yang akan dikirim:</b></div>");
                    b.append("<table cellpadding='2' style='font:11px tahoma'>");
                    b.append(pvRow("Pelayanan Persalinan", "Observation: Keadaan Ibu, Penolong, Cara, Kala I/II/III"+(kala.isEmpty()?"":" ("+kala+" mnt)"), false));
                    b.append(pvRow("Tindakan Persalinan", "Procedure (cara persalinan)", false));
                    b.append(pvRow("Data Bayi Baru Lahir", ihsBayi.isEmpty()
                        ? "dilewati — bayi belum punya IHS (kirim 'Bayi Baru Lahir' dulu)"
                        : ("BB/PB + APGAR"+(nApgar>0?" ("+nApgar+" data)":" (APGAR belum diisi)")), ihsBayi.isEmpty()));
                    b.append("</table>");
                    if (ihsBayi.isEmpty()) kurang.add("Data bayi (IHS) belum ada — section Data Bayi Baru Lahir akan dilewati");
                }
            } catch (Exception e) {
                b.append("<font color='red'>Error: ").append(e).append("</font>");
            }
            b.append("<hr>");
            if (kurang.isEmpty()) b.append("<font color='#090'><b>&#10003; Siap dikirim.</b></font> Tekan <b>Kirim</b>.");
            else {
                b.append("<font color='#c00'><b>&#10007; Belum lengkap:</b></font><ul style='margin:2px 0 0 14px;padding:0'>");
                for (String k : kurang) b.append("<li>").append(k).append("</li>");
                b.append("</ul>");
            }
            b.append("</body></html>");
            final String h = b.toString();
            SwingUtilities.invokeLater(() -> logPane[tab].setText(h));
        });
    }

    /**
     * Preview Transfer Internal RS (transfer_internal_rs) sebelum dikirim:
     * ruang asal/tujuan, DPJP, tanda vital sebelum/sesudah, resource yang dikirim.
     */
    private void previewTransfer(final int tab) {
        final String noRawat = selNoRawat[tab];
        if (noRawat == null || noRawat.isEmpty()) {
            logPane[tab].setText("<html><body style='font:11px tahoma'><font color='red'>Pilih dulu kunjungan di tabel.</font></body></html>");
            return;
        }
        runBackground(() -> {
            StringBuilder b = new StringBuilder("<html><body style='font:11px tahoma;color:#222'>");
            b.append("<div style='text-align:center'><b>PREVIEW TRANSFER INTERNAL RS</b><br>").append(noRawat).append("</div><hr>");
            java.util.List<String> kurang = new java.util.ArrayList<>();
            try {
                String idEnc = cariId("select id_encounter from satu_sehat_encounter where no_rawat=?", noRawat);
                if (idEnc.isEmpty()) kurang.add("Encounter belum dikirim (wajib, kirim lebih dulu)");
                b.append("<table cellpadding='2' style='font:11px tahoma'>");
                b.append(pvRow("Encounter", idEnc, true));
                int jml = 0;
                try (PreparedStatement p = koneksi.prepareStatement(
                        "select ti.tanggal_masuk, ti.tanggal_pindah, ifnull(ba.nm_bangsal,ti.asal_ruang) asal, "
                      + "ifnull(bs.nm_bangsal,ti.ruang_selanjutnya) tujuan, ifnull(dok.nm_dokter,'') dpjp, ti.derajat, "
                      + "ti.td_sebelum_transfer, ti.nadi_sebelum_transfer, ti.td_sesudah_transfer, ti.nadi_sesudah_transfer "
                      + "from transfer_internal_rs ti left join bangsal ba on ba.kd_bangsal=ti.asal_ruang "
                      + "left join bangsal bs on bs.kd_bangsal=ti.ruang_selanjutnya left join dokter dok on dok.kd_dokter=ti.dpjp "
                      + "where ti.no_rawat=? order by ti.tanggal_masuk")) {
                    p.setString(1, noRawat);
                    try (ResultSet r = p.executeQuery()) {
                        while (r.next()) {
                            jml++;
                            b.append("<tr><td colspan='2'><hr style='border:0;border-top:1px dashed #ccc'></td></tr>");
                            b.append(pvRow("Pindah", r.getString("asal") + " → " + r.getString("tujuan"), true));
                            b.append(pvRow("DPJP", r.getString("dpjp"), false));
                            b.append(pvRow("Derajat", r.getString("derajat"), false));
                            b.append(pvRow("Vital sebelum", "TD " + r.getString("td_sebelum_transfer") + ", N " + r.getString("nadi_sebelum_transfer"), false));
                            b.append(pvRow("Vital sesudah", "TD " + r.getString("td_sesudah_transfer") + ", N " + r.getString("nadi_sesudah_transfer"), false));
                        }
                    }
                }
                if (jml == 0) { b.append(pvRow("Transfer Internal", "", true)); kurang.add("Belum ada data transfer (isi di RMTransferInternalRs)"); }
                b.append("</table>");
                if (jml > 0) {
                    b.append("<div style='margin-top:6px'><b>Resource yang akan dikirim:</b></div>");
                    b.append("<table cellpadding='2' style='font:11px tahoma'>");
                    b.append(pvRow("Tanda Vital", "Observation TD/Nadi/Suhu/RR/SpO2 (sebelum & sesudah)", false));
                    b.append(pvRow("Dokumen", "Composition Transfer summary note (18761-7)", false));
                    b.append("</table>");
                    b.append("<i>Catatan: perpindahan ruang juga otomatis di Encounter.location.</i><br>");
                }
                if (jml > 1) b.append("<i>").append(jml).append(" record transfer akan dikirim.</i><br>");
            } catch (Exception e) {
                b.append("<font color='red'>Error: ").append(e).append("</font>");
            }
            b.append("<hr>");
            if (kurang.isEmpty()) b.append("<font color='#090'><b>&#10003; Siap dikirim.</b></font> Tekan <b>Kirim</b>.");
            else {
                b.append("<font color='#c00'><b>&#10007; Belum lengkap:</b></font><ul style='margin:2px 0 0 14px;padding:0'>");
                for (String k : kurang) b.append("<li>").append(k).append("</li>");
                b.append("</ul>");
            }
            b.append("</body></html>");
            final String h = b.toString();
            SwingUtilities.invokeLater(() -> logPane[tab].setText(h));
        });
    }

    /**
     * Preview Penilaian Awal Medis IGD (penilaian_medis_igd) sebelum dikirim:
     * anamnesis, keluhan utama, tanda vital, diagnosis, dan resource diskret yang
     * akan dibuat + Composition Emergency medicine Note.
     */
    private void previewPenilaianIGD(final int tab) {
        final String noRawat = selNoRawat[tab];
        if (noRawat == null || noRawat.isEmpty()) {
            logPane[tab].setText("<html><body style='font:11px tahoma'><font color='red'>Pilih dulu kunjungan di tabel.</font></body></html>");
            return;
        }
        runBackground(() -> {
            StringBuilder b = new StringBuilder("<html><body style='font:11px tahoma;color:#222'>");
            b.append("<div style='text-align:center'><b>PREVIEW PENILAIAN MEDIS IGD</b><br>").append(noRawat).append("</div><hr>");
            java.util.List<String> kurang = new java.util.ArrayList<>();
            try {
                String idEnc = cariId("select id_encounter from satu_sehat_encounter where no_rawat=?", noRawat);
                if (idEnc.isEmpty()) kurang.add("Encounter belum dikirim (wajib, kirim lebih dulu)");
                b.append("<table cellpadding='2' style='font:11px tahoma'>");
                b.append(pvRow("Encounter", idEnc, true));
                boolean ada = false;
                try (PreparedStatement p = koneksi.prepareStatement(
                        "select tanggal, keluhan_utama, td, nadi, rr, suhu, spo, gcs, bb, tb, diagnosis, tata "
                      + "from penilaian_medis_igd where no_rawat=? order by tanggal desc limit 1")) {
                    p.setString(1, noRawat);
                    try (ResultSet r = p.executeQuery()) {
                        if (r.next()) {
                            ada = true;
                            b.append(pvRow("Waktu", r.getString("tanggal"), false));
                            b.append(pvRow("Keluhan Utama", r.getString("keluhan_utama"), true));
                            if ((r.getString("keluhan_utama")==null || r.getString("keluhan_utama").trim().isEmpty())) kurang.add("Keluhan utama belum diisi");
                            b.append(pvRow("Tanda Vital", "TD " + r.getString("td") + ", N " + r.getString("nadi") + ", RR " + r.getString("rr")
                                    + ", S " + r.getString("suhu") + ", SpO2 " + r.getString("spo") + ", GCS " + r.getString("gcs"), false));
                            b.append(pvRow("BB / TB", r.getString("bb") + " kg / " + r.getString("tb") + " cm", false));
                            b.append(pvRow("Diagnosis", r.getString("diagnosis"), true));
                            b.append(pvRow("Tatalaksana", r.getString("tata"), false));
                        }
                    }
                }
                if (!ada) { b.append(pvRow("Penilaian Medis IGD", "", true)); kurang.add("Belum ada penilaian medis IGD (isi di RMPenilaianAwalMedisIGD)"); }
                b.append("</table>");
                if (ada) {
                    b.append("<div style='margin-top:6px'><b>Resource yang akan dikirim:</b></div>");
                    b.append("<table cellpadding='2' style='font:11px tahoma'>");
                    b.append(pvRow("Chief Complaint", "Condition (SNOMED 404684003)", false));
                    b.append(pvRow("Vital Signs", "Observation TD/Nadi/RR/Suhu/SpO2/GCS/BB/TB", false));
                    b.append(pvRow("Physical Exam", "Observation (LOINC 29545-1)", false));
                    b.append(pvRow("Diagnosis", "Condition", false));
                    b.append(pvRow("Dokumen", "Composition Emergency medicine Note (34111-5)", false));
                    b.append("</table>");
                }
            } catch (Exception e) {
                b.append("<font color='red'>Error: ").append(e).append("</font>");
            }
            b.append("<hr>");
            if (kurang.isEmpty()) b.append("<font color='#090'><b>&#10003; Siap dikirim.</b></font> Tekan <b>Kirim</b>.");
            else {
                b.append("<font color='#c00'><b>&#10007; Belum lengkap:</b></font><ul style='margin:2px 0 0 14px;padding:0'>");
                for (String k : kurang) b.append("<li>").append(k).append("</li>");
                b.append("</ul>");
            }
            b.append("</body></html>");
            final String h = b.toString();
            SwingUtilities.invokeLater(() -> logPane[tab].setText(h));
        });
    }

    /**
     * Preview Laporan Anestesi (Composition) sebelum dikirim. Membaca
     * laporan_anestesi. Satu kunjungan bisa punya beberapa record (per mulai).
     */
    private void previewAnestesi(final int tab) {
        final String noRawat = selNoRawat[tab];
        if (noRawat == null || noRawat.isEmpty()) {
            logPane[tab].setText("<html><body style='font:11px tahoma'><font color='red'>Pilih dulu kunjungan di tabel.</font></body></html>");
            return;
        }
        runBackground(() -> {
            StringBuilder b = new StringBuilder("<html><body style='font:11px tahoma;color:#222'>");
            b.append("<div style='text-align:center'><b>PREVIEW LAPORAN ANESTESI</b><br>").append(noRawat).append("</div><hr>");
            java.util.List<String> kurang = new java.util.ArrayList<>();
            try {
                String idEnc = cariId("select id_encounter from satu_sehat_encounter where no_rawat=?", noRawat);
                if (idEnc.isEmpty()) kurang.add("Encounter belum dikirim (wajib, kirim lebih dulu)");
                b.append("<table cellpadding='2' style='font:11px tahoma'>");
                b.append(pvRow("Encounter", idEnc, true));
                int jml = 0;
                try (PreparedStatement p = koneksi.prepareStatement(
                        "select mulai, tindakan_operasi, diagnosa_preop, diagnosa_postop, status_asa, "
                      + "jenis_anestesi_lokasi, jenis_anestesi_regional, premedikasi, dokter_anestesi "
                      + "from laporan_anestesi where no_rawat=? order by mulai")) {
                    p.setString(1, noRawat);
                    try (ResultSet r = p.executeQuery()) {
                        while (r.next()) {
                            jml++;
                            b.append("<tr><td colspan='2'><hr style='border:0;border-top:1px dashed #ccc'></td></tr>");
                            b.append(pvRow("Mulai", r.getString("mulai"), false));
                            b.append(pvRow("Tindakan Operasi", r.getString("tindakan_operasi"), true));
                            b.append(pvRow("Dx Pra Operasi", r.getString("diagnosa_preop"), false));
                            b.append(pvRow("Dx Pasca Operasi", r.getString("diagnosa_postop"), false));
                            b.append(pvRow("Status ASA", r.getString("status_asa"), false));
                            b.append(pvRow("Regional/Lokasi", (r.getString("jenis_anestesi_regional")+" "+r.getString("jenis_anestesi_lokasi")).trim(), false));
                            b.append(pvRow("Premedikasi", r.getString("premedikasi"), false));
                            b.append(pvRow("Dokter Anestesi", r.getString("dokter_anestesi"), true));
                        }
                    }
                }
                if (jml == 0) { b.append(pvRow("Laporan Anestesi", "", true)); kurang.add("Belum ada laporan anestesi (isi di RMLaporanAnastesi)"); }
                b.append("</table>");
                if (jml > 1) b.append("<i>").append(jml).append(" record anestesi akan dikirim untuk kunjungan ini.</i><br>");
            } catch (Exception e) {
                b.append("<font color='red'>Error: ").append(e).append("</font>");
            }
            b.append("<hr>");
            if (kurang.isEmpty()) b.append("<font color='#090'><b>&#10003; Siap dikirim.</b></font> Tekan <b>Kirim</b>.");
            else {
                b.append("<font color='#c00'><b>&#10007; Belum lengkap:</b></font><ul style='margin:2px 0 0 14px;padding:0'>");
                for (String k : kurang) b.append("<li>").append(k).append("</li>");
                b.append("</ul>");
            }
            b.append("</body></html>");
            final String h = b.toString();
            SwingUtilities.invokeLater(() -> logPane[tab].setText(h));
        });
    }

    /**
     * Preview Laporan Tindakan (Composition) sebelum dikirim. Membaca
     * laporan_tindakan (tabel yang diisi RMLaporanTindakan). Satu kunjungan bisa
     * punya lebih dari satu tindakan (per tanggal).
     */
    private void previewTindakan(final int tab) {
        final String noRawat = selNoRawat[tab];
        if (noRawat == null || noRawat.isEmpty()) {
            logPane[tab].setText("<html><body style='font:11px tahoma'><font color='red'>Pilih dulu kunjungan di tabel.</font></body></html>");
            return;
        }
        runBackground(() -> {
            StringBuilder b = new StringBuilder("<html><body style='font:11px tahoma;color:#222'>");
            b.append("<div style='text-align:center'><b>PREVIEW LAPORAN TINDAKAN</b><br>").append(noRawat).append("</div><hr>");
            java.util.List<String> kurang = new java.util.ArrayList<>();
            try {
                String idEnc = cariId("select id_encounter from satu_sehat_encounter where no_rawat=?", noRawat);
                if (idEnc.isEmpty()) kurang.add("Encounter belum dikirim (wajib, kirim lebih dulu)");
                b.append("<table cellpadding='2' style='font:11px tahoma'>");
                b.append(pvRow("Encounter", idEnc, true));
                int jml = 0;
                try (PreparedStatement p = koneksi.prepareStatement(
                        "select lt.tanggal, lt.kd_dokter, ifnull(d.nm_dokter, ifnull(pg.nama,'')) as dokter, "
                      + "lt.diagnosa_pra_tindakan, lt.tindakan_medik, lt.uraian, lt.hasil, "
                      + "lt.diagnosa_pasca_tindakan, lt.kesimpulan from laporan_tindakan lt "
                      + "left join dokter d on d.kd_dokter=lt.kd_dokter "
                      + "left join pegawai pg on pg.nik=lt.kd_dokter "
                      + "where lt.no_rawat=? order by lt.tanggal")) {
                    p.setString(1, noRawat);
                    try (ResultSet r = p.executeQuery()) {
                        while (r.next()) {
                            jml++;
                            b.append("<tr><td colspan='2'><hr style='border:0;border-top:1px dashed #ccc'></td></tr>");
                            b.append(pvRow("Tindakan tgl", r.getString("tanggal"), false));
                            b.append(pvRow("Dokter", r.getString("dokter") + " (" + r.getString("kd_dokter") + ")", true));
                            b.append(pvRow("Indikasi (Dx Pra)", r.getString("diagnosa_pra_tindakan"), true));
                            b.append(pvRow("Tindakan Medik", r.getString("tindakan_medik"), true));
                            b.append(pvRow("Uraian", r.getString("uraian"), false));
                            b.append(pvRow("Hasil", r.getString("hasil"), false));
                            b.append(pvRow("Dx Pasca", r.getString("diagnosa_pasca_tindakan"), false));
                            b.append(pvRow("Kesimpulan", r.getString("kesimpulan"), false));
                        }
                    }
                }
                if (jml == 0) { b.append(pvRow("Laporan Tindakan", "", true)); kurang.add("Belum ada laporan tindakan (isi di RMLaporanTindakan)"); }
                b.append("</table>");
                if (jml > 1) b.append("<i>").append(jml).append(" tindakan akan dikirim untuk kunjungan ini.</i><br>");
            } catch (Exception e) {
                b.append("<font color='red'>Error: ").append(e).append("</font>");
            }
            b.append("<hr>");
            if (kurang.isEmpty()) b.append("<font color='#090'><b>&#10003; Siap dikirim.</b></font> Tekan <b>Kirim</b>.");
            else {
                b.append("<font color='#c00'><b>&#10007; Belum lengkap:</b></font><ul style='margin:2px 0 0 14px;padding:0'>");
                for (String k : kurang) b.append("<li>").append(k).append("</li>");
                b.append("</ul>");
            }
            b.append("</body></html>");
            final String h = b.toString();
            SwingUtilities.invokeLater(() -> logPane[tab].setText(h));
        });
    }

    /**
     * Preview Laporan Operasi (Composition) sebelum dikirim. Membaca
     * laporan_operasi_casemix (tabel yang benar-benar diisi RMLaporanOperasi).
     * Satu kunjungan bisa punya lebih dari satu operasi (per tanggal).
     */
    private void previewOperasi(final int tab) {
        final String noRawat = selNoRawat[tab];
        if (noRawat == null || noRawat.isEmpty()) {
            logPane[tab].setText("<html><body style='font:11px tahoma'><font color='red'>Pilih dulu kunjungan di tabel.</font></body></html>");
            return;
        }
        runBackground(() -> {
            StringBuilder b = new StringBuilder("<html><body style='font:11px tahoma;color:#222'>");
            b.append("<div style='text-align:center'><b>PREVIEW LAPORAN OPERASI</b><br>").append(noRawat).append("</div><hr>");
            java.util.List<String> kurang = new java.util.ArrayList<>();
            try {
                String idEnc = cariId("select id_encounter from satu_sehat_encounter where no_rawat=?", noRawat);
                if (idEnc.isEmpty()) kurang.add("Encounter belum dikirim (wajib, kirim lebih dulu)");
                b.append("<table cellpadding='2' style='font:11px tahoma'>");
                b.append(pvRow("Encounter", idEnc, true));
                int jml = 0;
                try (PreparedStatement p = koneksi.prepareStatement(
                        "select loc.tanggal, loc.kddokterbedah, ifnull(d.nm_dokter, ifnull(pg.nama,'')) as dokter, "
                      + "loc.diagnosaprabedah, loc.diagnosapascabedah, loc.tindakan, loc.uraian, loc.komplikasi, loc.perdarahan, "
                      + "loc.jenisanestesi, loc.dikirim, loc.dikirimket from laporan_operasi_casemix loc "
                      + "left join dokter d on d.kd_dokter=loc.kddokterbedah "
                      + "left join pegawai pg on pg.nik=loc.kddokterbedah "
                      + "where loc.no_rawat=? order by loc.tanggal")) {
                    p.setString(1, noRawat);
                    try (ResultSet r = p.executeQuery()) {
                        while (r.next()) {
                            jml++;
                            b.append("<tr><td colspan='2'><hr style='border:0;border-top:1px dashed #ccc'></td></tr>");
                            b.append(pvRow("Operasi tgl", r.getString("tanggal"), false));
                            b.append(pvRow("Dokter Bedah", r.getString("dokter") + " (" + r.getString("kddokterbedah") + ")", true));
                            b.append(pvRow("Dx Prabedah", r.getString("diagnosaprabedah"), true));
                            b.append(pvRow("Dx Pascabedah", r.getString("diagnosapascabedah"), false));
                            b.append(pvRow("Tindakan", r.getString("tindakan"), false));
                            b.append(pvRow("Uraian Pembedahan", r.getString("uraian"), false));
                            b.append(pvRow("Anestesi", r.getString("jenisanestesi"), false));
                            b.append(pvRow("Perdarahan", r.getString("perdarahan"), false));
                            b.append(pvRow("Komplikasi", r.getString("komplikasi"), false));
                            b.append(pvRow("Spesimen", ("Ya".equalsIgnoreCase(r.getString("dikirim")) ? r.getString("dikirimket") : "Tidak dikirim"), false));
                        }
                    }
                }
                if (jml == 0) { b.append(pvRow("Laporan Operasi", "", true)); kurang.add("Belum ada laporan operasi (isi di RMLaporanOperasi)"); }
                b.append("</table>");
                if (jml > 1) b.append("<i>").append(jml).append(" operasi akan dikirim untuk kunjungan ini.</i><br>");
            } catch (Exception e) {
                b.append("<font color='red'>Error: ").append(e).append("</font>");
            }
            b.append("<hr>");
            if (kurang.isEmpty()) b.append("<font color='#090'><b>&#10003; Siap dikirim.</b></font> Tekan <b>Kirim</b>.");
            else {
                b.append("<font color='#c00'><b>&#10007; Belum lengkap:</b></font><ul style='margin:2px 0 0 14px;padding:0'>");
                for (String k : kurang) b.append("<li>").append(k).append("</li>");
                b.append("</ul>");
            }
            b.append("</body></html>");
            final String h = b.toString();
            SwingUtilities.invokeLater(() -> logPane[tab].setText(h));
        });
    }

    /**
     * Preview isian Resume Medis (Composition) sebelum dikirim. Tab Rawat Inap
     * membaca resume_pasien_ranap, tab lain membaca resume_pasien. Menandai bagian
     * yang belum lengkap supaya bisa diperbaiki lewat form resume sebelum kirim.
     */
    private void previewResume(final int tab) {
        final String noRawat = selNoRawat[tab];
        if (noRawat == null || noRawat.isEmpty()) {
            logPane[tab].setText("<html><body style='font:11px tahoma'><font color='red'>Pilih dulu kunjungan di tabel.</font></body></html>");
            return;
        }
        final boolean ranap = kunjunganRanap(noRawat);
        runBackground(() -> {
            StringBuilder b = new StringBuilder("<html><body style='font:11px tahoma;color:#222'>");
            b.append("<div style='text-align:center'><b>PREVIEW RESUME MEDIS ").append(ranap ? "RAWAT INAP" : "RAWAT JALAN")
             .append("</b><br>").append(noRawat).append("</div><hr>");
            java.util.List<String> kurang = new java.util.ArrayList<>();
            try {
                String idEnc = cariId("select id_encounter from satu_sehat_encounter where no_rawat=?", noRawat);
                if (idEnc.isEmpty()) kurang.add("Encounter belum dikirim (wajib, kirim lebih dulu)");
                b.append("<table cellpadding='2' style='font:11px tahoma'>");
                b.append(pvRow("Encounter", idEnc, true));

                String tabel = ranap ? "resume_pasien_ranap" : "resume_pasien";
                String sql = ranap
                    ? "select keluhan_utama, diagnosa_awal, pemeriksaan_fisik, diagnosa_utama, kd_diagnosa_utama, "
                      + "jalannya_penyakit, pemeriksaan_penunjang, hasil_laborat, obat_pulang, keadaan, cara_keluar "
                      + "from resume_pasien_ranap where no_rawat=?"
                    : "select keluhan_utama, diagnosa_utama, kd_diagnosa_utama, prosedur_utama, "
                      + "jalannya_penyakit, pemeriksaan_penunjang, hasil_laborat, obat_pulang, kondisi_pulang "
                      + "from resume_pasien where no_rawat=?";
                boolean ada = false;
                try (PreparedStatement p = koneksi.prepareStatement(sql)) {
                    p.setString(1, noRawat);
                    ResultSet r = p.executeQuery();
                    if (r.next()) {
                        ada = true;
                        String diagU = r.getString("diagnosa_utama");
                        String kdU = r.getString("kd_diagnosa_utama");
                        String diagTeks = (kdU==null||kdU.trim().isEmpty()?"":kdU+" - ") + (diagU==null?"":diagU);
                        if (ranap) b.append(pvRow("Keluhan Utama", r.getString("keluhan_utama"), false));
                        else       b.append(pvRow("Keluhan Utama", r.getString("keluhan_utama"), false));
                        if (ranap) b.append(pvRow("Diagnosa Awal", r.getString("diagnosa_awal"), false));
                        if (ranap) b.append(pvRow("Pemeriksaan Fisik", r.getString("pemeriksaan_fisik"), false));
                        b.append(pvRow("Diagnosa Utama", diagTeks, true));
                        if (diagTeks.trim().isEmpty()) kurang.add("Diagnosa utama belum diisi");
                        if (!ranap) b.append(pvRow("Prosedur Utama", r.getString("prosedur_utama"), false));
                        b.append(pvRow("Perjalanan Penyakit", r.getString("jalannya_penyakit"), false));
                        b.append(pvRow("Pemeriksaan Penunjang", r.getString("pemeriksaan_penunjang"), false));
                        b.append(pvRow("Hasil Laboratorium", r.getString("hasil_laborat"), false));
                        b.append(pvRow("Obat Pulang", r.getString("obat_pulang"), false));
                        if (ranap) {
                            b.append(pvRow("Keadaan Pulang", r.getString("keadaan"), false));
                            b.append(pvRow("Cara Keluar", r.getString("cara_keluar"), false));
                        } else {
                            b.append(pvRow("Kondisi Pulang", r.getString("kondisi_pulang"), false));
                        }
                    }
                    r.close();
                }
                if (!ada) {
                    b.append(pvRow("Resume", "", true));
                    kurang.add("Resume medis belum diisi di form " + (ranap ? "RMDataResumePasienRanap" : "RMDataResumePasien"));
                }
                b.append("</table>");

                // Data terstruktur (entry Composition) yang di-link/ dibuat otomatis oleh
                // sender: Obat -> MedicationRequest, Lab -> DiagnosticReport, Alergi ->
                // AllergyIntolerance. Chief Complaint (Condition) & Pemeriksaan Fisik
                // (Observation) selalu dibuat saat kirim. Ranap saja.
                if (ranap) {
                    int nObat = hitung("select count(distinct sm.id_medicationrequest) from satu_sehat_medicationrequest sm join resep_obat ro on ro.no_resep=sm.no_resep where ro.no_rawat=? and ifnull(sm.id_medicationrequest,'')<>''", noRawat);
                    int nLab  = hitung("select count(distinct dl.id_diagnosticreport) from satu_sehat_diagnosticreport_lab dl join permintaan_lab pl on pl.noorder=dl.noorder where pl.no_rawat=? and ifnull(dl.id_diagnosticreport,'')<>''", noRawat);
                    int nAlg  = hitung("select count(*) from satu_sehat_allergy_intolerance where no_rawat=? and ifnull(id_allergy_intolerance,'')<>''", noRawat);
                    b.append("<div style='margin-top:6px'><b>Data terstruktur (entry) yang akan di-link:</b></div>");
                    b.append("<table cellpadding='2' style='font:11px tahoma'>");
                    b.append(pvRow("Obat (MedicationRequest)", nObat>0? nObat+" resep" : "", false));
                    b.append(pvRow("Lab (DiagnosticReport)",  nLab>0?  nLab+" hasil"  : "", false));
                    b.append(pvRow("Alergi (AllergyIntolerance)", nAlg>0? nAlg+" data" : "belum ada (isi tab Data Alergi)", false));
                    b.append(pvRow("Keluhan Utama (Condition)", "dibuat saat kirim", false));
                    b.append(pvRow("Pemeriksaan Fisik (Observation)", "dibuat saat kirim", false));
                    b.append("</table>");
                }
            } catch (Exception e) {
                b.append("<font color='red'>Error: ").append(e).append("</font>");
            }
            b.append("<hr>");
            if (kurang.isEmpty()) b.append("<font color='#090'><b>&#10003; Siap dikirim.</b></font> Tekan <b>Kirim</b>.");
            else {
                b.append("<font color='#c00'><b>&#10007; Belum lengkap:</b></font><ul style='margin:2px 0 0 14px;padding:0'>");
                for (String k : kurang) b.append("<li>").append(k).append("</li>");
                b.append("</ul>");
            }
            b.append("</body></html>");
            final String h = b.toString();
            SwingUtilities.invokeLater(() -> logPane[tab].setText(h));
        });
    }

    /**
     * Preview isian SPRI (ServiceRequest perintah rawat inap) sebelum dikirim,
     * dengan cek kelengkapan. Bagian yang belum lengkap bisa diperbaiki lewat
     *DlgPermintaanRanap.
     */
    private void previewSPRI(final int tab) {
        final String noRawat = selNoRawat[tab];
        if (noRawat == null || noRawat.isEmpty()) {
            logPane[tab].setText("<html><body style='font:11px tahoma'><font color='red'>Pilih dulu kunjungan di tabel.</font></body></html>");
            return;
        }
        runBackground(() -> {
            StringBuilder b = new StringBuilder("<html><body style='font:11px tahoma;color:#222'>");
            b.append("<div style='text-align:center'><b>PREVIEW SPRI</b><br>").append(noRawat).append("</div><hr>");
            java.util.List<String> kurang = new java.util.ArrayList<>();
            try {
                String idEnc = cariId("select id_encounter from satu_sehat_encounter where no_rawat=?", noRawat);
                if (idEnc.isEmpty()) kurang.add("Encounter belum dikirim (wajib, kirim lebih dulu)");

                b.append("<table cellpadding='2' style='font:11px tahoma'>");
                b.append(pvRow("Encounter", idEnc, true));

                try (PreparedStatement p = koneksi.prepareStatement(
                        "select pr.diagnosa, pr.rencana_tindakan, pr.kd_dokter_dpjp, pr.tanggal, "
                      + "ifnull(dd.nm_dokter,'') nm_dpjp, ifnull(dr.nm_dokter,'') nm_perujuk "
                      + "from permintaan_ranap pr "
                      + "left join dokter dd on dd.kd_dokter=pr.kd_dokter_dpjp "
                      + "left join reg_periksa rp on rp.no_rawat=pr.no_rawat "
                      + "left join dokter dr on dr.kd_dokter=rp.kd_dokter "
                      + "where pr.no_rawat=?")) {
                    p.setString(1, noRawat);
                    ResultSet r = p.executeQuery();
                    if (r.next()) {
                        String diag = r.getString("diagnosa");
                        b.append(pvRow("Diagnosa masuk", diag, true));
                        if (diag == null || diag.trim().isEmpty() || diag.trim().equals("- - -")) kurang.add("Diagnosa masuk belum diisi");
                        b.append(pvRow("Dokter perujuk (requester)", r.getString("nm_perujuk"), true));
                        b.append(pvRow("DPJP tujuan (performer)", r.getString("nm_dpjp"), false));
                        if (r.getString("kd_dokter_dpjp") == null || r.getString("kd_dokter_dpjp").trim().isEmpty())
                            kurang.add("DPJP tujuan belum dipilih (opsional, dikirim sebagai performer)");
                        b.append(pvRow("Rencana tindakan (orderDetail)", r.getString("rencana_tindakan"), false));
                        if (r.getString("rencana_tindakan") == null || r.getString("rencana_tindakan").trim().isEmpty())
                            kurang.add("Rencana tindakan belum diisi (opsional, jadi orderDetail)");
                    } else {
                        b.append(pvRow("Permintaan Ranap", "", true));
                        kurang.add("Permintaan rawat inap belum dibuat");
                    }
                    r.close();
                }
                String idCond = cariId("select id_condition from satu_sehat_condition where no_rawat=? and ifnull(id_condition,'')<>'' limit 1", noRawat);
                b.append(pvRow("reasonReference (Condition)", idCond.isEmpty() ? "" : idCond, false));
                b.append("</table>");
            } catch (Exception e) {
                b.append("<font color='red'>Error: ").append(e).append("</font>");
            }

            b.append("<hr>");
            boolean adaWajibKurang = kurang.stream().anyMatch(k -> k.contains("wajib") || k.contains("Diagnosa") || k.contains("belum dibuat"));
            if (!adaWajibKurang) {
                b.append("<font color='#090'><b>&#10003; Siap dikirim.</b></font>");
                if (!kurang.isEmpty()) {
                    b.append("<br><font color='#c80'>Catatan opsional yang belum lengkap:</font><ul style='margin:2px 0 0 14px;padding:0'>");
                    for (String k : kurang) b.append("<li>").append(k).append("</li>");
                    b.append("</ul>");
                }
            } else {
                b.append("<font color='#c00'><b>&#10007; Belum lengkap:</b></font><ul style='margin:2px 0 0 14px;padding:0'>");
                for (String k : kurang) b.append("<li>").append(k).append("</li>");
                b.append("</ul>");
            }
            b.append("</body></html>");
            final String h = b.toString();
            SwingUtilities.invokeLater(() -> logPane[tab].setText(h));
        });
    }

    private void previewSEP(final int tab) {
        final String noRawat = selNoRawat[tab];
        if (noRawat == null || noRawat.isEmpty()) {
            logPane[tab].setText("<html><body style='font:11px tahoma'><font color='red'>Pilih dulu kunjungan di tabel.</font></body></html>");
            return;
        }
        runBackground(() -> {
            String html;
            try (PreparedStatement p = koneksi.prepareStatement(
                    "select ifnull(no_sep,'') no_sep, ifnull(tglsep,'') tglsep, ifnull(no_kartu,'') no_kartu, ifnull(nama_pasien,'') nama_pasien, "
                  + "ifnull(tanggal_lahir,'') tanggal_lahir, ifnull(jkel,'') jkel, ifnull(nomr,'') nomr, ifnull(peserta,'') peserta, "
                  + "ifnull(jnspelayanan,'') jnspelayanan, ifnull(klsrawat,'') klsrawat, ifnull(nmpolitujuan,'') nmpolitujuan, "
                  + "ifnull(nmdiagnosaawal,'') diag, ifnull(diagawal,'') kddiag, ifnull(nmppkpelayanan,'') faskes, ifnull(nmppkrujukan,'') perujuk, "
                  + "ifnull(no_rujukan,'') no_rujukan, ifnull(nmdpdjp,'') dpjp, ifnull(catatan,'') catatan, ifnull(cob,'') cob, ifnull(lakalantas,'') laka "
                  + "from bridging_sep where no_rawat=?")) {
                p.setString(1, noRawat);
                ResultSet r = p.executeQuery();
                if (r.next()) {
                    String jp = "1".equals(r.getString("jnspelayanan")) ? "Rawat Inap" : "2".equals(r.getString("jnspelayanan")) ? "Rawat Jalan" : r.getString("jnspelayanan");
                    String jk = "L".equalsIgnoreCase(r.getString("jkel")) ? "Laki-laki" : "P".equalsIgnoreCase(r.getString("jkel")) ? "Perempuan" : r.getString("jkel");
                    StringBuilder b = new StringBuilder();
                    b.append("<html><body style='font:11px tahoma;color:#222'>");
                    b.append("<div style='text-align:center'><b>").append(akses.getnamars()).append("</b><br>")
                     .append(akses.getalamatrs()).append("<br><b>SURAT ELIGIBILITAS PESERTA</b></div><hr>");
                    b.append("<table width='100%' cellpadding='2' style='font:11px tahoma'>");
                    b.append(sepRow("No. SEP", r.getString("no_sep")));
                    b.append(sepRow("Tgl. SEP", r.getString("tglsep")));
                    b.append(sepRow("No. Kartu", r.getString("no_kartu")));
                    b.append(sepRow("Nama Peserta", r.getString("nama_pasien")));
                    b.append(sepRow("Tgl. Lahir", r.getString("tanggal_lahir")));
                    b.append(sepRow("Jns Kelamin", jk));
                    b.append(sepRow("No. RM", r.getString("nomr")));
                    b.append(sepRow("Jns. Peserta", r.getString("peserta")));
                    b.append(sepRow("Jns. Rawat", jp));
                    b.append(sepRow("Poli/Tujuan", r.getString("nmpolitujuan")));
                    b.append(sepRow("Kelas Rawat", r.getString("klsrawat")));
                    b.append(sepRow("Diagnosa Awal", r.getString("kddiag") + " - " + r.getString("diag")));
                    b.append(sepRow("Faskes", r.getString("faskes")));
                    b.append(sepRow("Perujuk", r.getString("perujuk") + (r.getString("no_rujukan").isEmpty() ? "" : " (" + r.getString("no_rujukan") + ")")));
                    b.append(sepRow("DPJP", r.getString("dpjp")));
                    b.append(sepRow("COB", "Y".equalsIgnoreCase(r.getString("cob")) ? "Ya" : "Tidak"));
                    b.append(sepRow("Laka Lantas", "0".equals(r.getString("laka")) || r.getString("laka").isEmpty() ? "Tidak" : "Ya"));
                    b.append(sepRow("Catatan", r.getString("catatan")));
                    b.append("</table></body></html>");
                    html = b.toString();
                } else html = "<html><body style='font:11px tahoma'><font color='red'>Data SEP tidak ditemukan untuk kunjungan ini.</font></body></html>";
                r.close();
            } catch (Exception e) {
                html = "<html><body style='font:11px tahoma'><font color='red'>Error: " + e + "</font></body></html>";
            }
            final String h = html;
            SwingUtilities.invokeLater(() -> logPane[tab].setText(h));
        });
    }

    private static String sepRow(String k, String v) {
        if (v == null) v = "";
        return "<tr><td valign='top' width='32%'><b>" + k + "</b></td><td valign='top'>: " + v.replace("<", "&lt;") + "</td></tr>";
    }

    private void runBackground(Runnable task) {
        if (busy || executor.isShutdown() || !isDisplayable()) return;
        busy = true; setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            executor.submit(() -> { try { task.run(); } finally { busy = false; SwingUtilities.invokeLater(() -> { if (isDisplayable()) setCursor(Cursor.getDefaultCursor()); }); } });
        } catch (RejectedExecutionException ex) { busy = false; }
    }

    @Override public void dispose() { executor.shutdownNow(); super.dispose(); }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            SatuSehatKirimBundel d = new SatuSehatKirimBundel(new javax.swing.JFrame(), true);
            d.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosing(java.awt.event.WindowEvent e) { System.exit(0); }
            });
            d.setVisible(true);
        });
    }

    private widget.InternalFrame internalFrame1;
    private widget.panelisi panelAtas, panelBawah;
    private widget.Label jLabel1, jLabel2, jLabel3, jLabel4;
    private widget.Tanggal DTPCari1, DTPCari2;
    private widget.TextBox TCari;
    private widget.Button BtnCari, BtnKeluar;
    private widget.ComboBox cmbStatus;
    private javax.swing.JTabbedPane TabKategori;
}
