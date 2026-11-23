/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgPenyakit.java
 *
 * Created on May 23, 2010, 12:57:16 AM
 */

package keuangan;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author dosen
 */
public final class DlgTemplateLaboratorium extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean ceksukses = false;

    // ===== Nilai Rujukan Panel =====
    // Kolom tbNilaiRujukan: [Kelompok][JK][Min][Max][Text][Min.Kritis][Max.Kritis][Kondisi][ID hidden]
    private static final int COL_NR_KELOMPOK    = 0;
    private static final int COL_NR_JK          = 1;
    private static final int COL_NR_MIN         = 2;
    private static final int COL_NR_MAX         = 3;
    private static final int COL_NR_TEXT        = 4;
    private static final int COL_NR_KRITIS_MIN  = 5;
    private static final int COL_NR_KRITIS_MAX  = 6;
    private static final int COL_NR_KONDISI     = 7;
    private static final int COL_NR_ID          = 8;
    private widget.Table tbNilaiRujukan;
    private DefaultTableModel mdlNilaiRujukan;
    private JLabel lblRujukanHeader;
    private int selectedIdTemplate = -1;
    /** Creates new form DlgPenyakit
     * @param parent
     * @param modal */
    public DlgTemplateLaboratorium(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(10,2);
        setSize(656,250);

        Object[] row={"Pemeriksaan","Satuan","N.Rujukan L.D.","N.Rujukan L.A.","N.Rujukan P.D.",
                      "N.Rujukan P.A.","J.S. Rmh Skt","Paket BHP","J.M. Perujuk","J.M. Dokter",
                      "J.M. Laborat","K.S.O.","Menejemen","Biaya Item","",""};
        tabMode=new DefaultTableModel(null,row){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = true;
                if (colIndex==13) {
                    a=false;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,
                java.lang.Object.class,java.lang.Object.class,java.lang.Double.class,java.lang.Double.class,
                java.lang.Double.class,java.lang.Double.class,java.lang.Double.class,java.lang.Double.class, 
                java.lang.Double.class,java.lang.Double.class,java.lang.Object.class,java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbKamar.setModel(tabMode);
        //tbPenyakit.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbPenyakit.getBackground()));
        tbKamar.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbKamar.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < 16; i++) {
            TableColumn column = tbKamar.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(180);
            }else if((i==14)||(i==15)){
                column.setMinWidth(0);
                column.setMaxWidth(0);
            }else {
                column.setPreferredWidth(80);
            }
        }
        tbKamar.setDefaultRenderer(Object.class, new WarnaTable());
        KdPeriksa.setDocument(new batasInput((byte)100).getKata(KdPeriksa));

        initNilaiRujukanPanel();
        setSize(1100, 700);
        this.setLocation(10, 2);
    }

    /**
     * Panel untuk edit nilai rujukan per kelompok umur × JK, muncul di SOUTH.
     * Baris baru terisi otomatis saat user pilih row pemeriksaan di tbKamar.
     */
    private void initNilaiRujukanPanel() {
        mdlNilaiRujukan = new DefaultTableModel(null, new Object[]{
            "Kelompok Umur", "JK", "Min", "Max", "Text/Nilai",
            "Min.Kritis", "Max.Kritis", "Kondisi", "ID"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return true; }
        };
        tbNilaiRujukan = new widget.Table();
        tbNilaiRujukan.setModel(mdlNilaiRujukan);
        tbNilaiRujukan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbNilaiRujukan.setRowHeight(24);
        tbNilaiRujukan.setDefaultRenderer(Object.class, new WarnaTable());

        // Combo untuk Kelompok Umur — load dari master
        JComboBox<String> cbKelompok = new JComboBox<>();
        for (NilaiRujukanResolver.Kelompok k : NilaiRujukanResolver.listKelompok(koneksi)) {
            cbKelompok.addItem(k.kode + " - " + k.nama);
        }
        tbNilaiRujukan.getColumnModel().getColumn(COL_NR_KELOMPOK).setCellEditor(new DefaultCellEditor(cbKelompok));
        tbNilaiRujukan.getColumnModel().getColumn(COL_NR_KELOMPOK).setPreferredWidth(150);

        // Combo JK: L, P, L/P
        JComboBox<String> cbJk = new JComboBox<>(new String[]{"L", "P", "L/P"});
        tbNilaiRujukan.getColumnModel().getColumn(COL_NR_JK).setCellEditor(new DefaultCellEditor(cbJk));
        tbNilaiRujukan.getColumnModel().getColumn(COL_NR_JK).setPreferredWidth(50);

        tbNilaiRujukan.getColumnModel().getColumn(COL_NR_MIN).setPreferredWidth(70);
        tbNilaiRujukan.getColumnModel().getColumn(COL_NR_MAX).setPreferredWidth(70);
        tbNilaiRujukan.getColumnModel().getColumn(COL_NR_TEXT).setPreferredWidth(150);
        tbNilaiRujukan.getColumnModel().getColumn(COL_NR_KRITIS_MIN).setPreferredWidth(80);
        tbNilaiRujukan.getColumnModel().getColumn(COL_NR_KRITIS_MAX).setPreferredWidth(80);
        tbNilaiRujukan.getColumnModel().getColumn(COL_NR_KONDISI).setPreferredWidth(100);
        // Hidden ID column
        TableColumn idCol = tbNilaiRujukan.getColumnModel().getColumn(COL_NR_ID);
        idCol.setMinWidth(0); idCol.setMaxWidth(0); idCol.setPreferredWidth(0);

        // Header + toolbar
        JPanel panelHeader = new JPanel(new BorderLayout(8, 0));
        lblRujukanHeader = new JLabel("Nilai Rujukan — pilih pemeriksaan di tabel atas dulu");
        lblRujukanHeader.setFont(lblRujukanHeader.getFont().deriveFont(java.awt.Font.BOLD, 12f));
        lblRujukanHeader.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));
        panelHeader.add(lblRujukanHeader, BorderLayout.WEST);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 2));
        toolbar.setOpaque(false);
        widget.Button btnMaster    = new widget.Button(); btnMaster.setText("Master Kelompok");
        widget.Button btnTambah    = new widget.Button(); btnTambah.setText("+ Range");
        widget.Button btnQuickFill = new widget.Button(); btnQuickFill.setText("Quick Fill 16");
        widget.Button btnLegacy    = new widget.Button(); btnLegacy.setText("Isi dari Data Lama");
        widget.Button btnCopy      = new widget.Button(); btnCopy.setText("Copy JK lawan");
        widget.Button btnHapus     = new widget.Button(); btnHapus.setText("Hapus Row");
        widget.Button btnSimpan    = new widget.Button(); btnSimpan.setText("Simpan Rujukan");
        for (widget.Button b : new widget.Button[]{btnMaster,btnTambah,btnQuickFill,btnLegacy,btnCopy,btnHapus,btnSimpan}) {
            b.setFont(new java.awt.Font("Tahoma", java.awt.Font.PLAIN, 11));
        }
        btnLegacy.setToolTipText("Isi dari kolom nilai rujukan lama (L.D / L.A / P.D / P.A) di tabel atas");
        btnMaster.addActionListener(e -> openMasterKelompok());
        btnTambah.addActionListener(e -> addEmptyRow());
        btnQuickFill.addActionListener(e -> quickFill());
        btnLegacy.addActionListener(e -> prefillFromLegacy(true));
        btnCopy.addActionListener(e -> copyRowOppositeJk());
        btnHapus.addActionListener(e -> deleteSelectedRow());
        btnSimpan.addActionListener(e -> saveNilaiRujukan());
        toolbar.add(btnMaster); toolbar.add(btnTambah); toolbar.add(btnQuickFill);
        toolbar.add(btnLegacy);
        toolbar.add(btnCopy);   toolbar.add(btnHapus);  toolbar.add(btnSimpan);
        panelHeader.add(toolbar, BorderLayout.EAST);

        JPanel panelRujukan = new JPanel(new BorderLayout());
        panelRujukan.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235), 1),
                " Nilai Rujukan per Kelompok Umur ",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", java.awt.Font.BOLD, 11),
                new java.awt.Color(50, 50, 50)));
        panelRujukan.add(panelHeader, BorderLayout.NORTH);
        panelRujukan.add(new JScrollPane(tbNilaiRujukan), BorderLayout.CENTER);
        panelRujukan.setPreferredSize(new Dimension(1080, 340));

        getContentPane().add(panelRujukan, BorderLayout.SOUTH);

        // Listener: auto-load nilai rujukan saat user pilih row di tabel pemeriksaan
        tbKamar.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = tbKamar.getSelectedRow();
            if (row < 0) return;
            Object idObj = tbKamar.getValueAt(row, 14);
            String id   = idObj == null ? "" : idObj.toString();
            String nama = tbKamar.getValueAt(row, 0) == null ? "" : tbKamar.getValueAt(row, 0).toString();
            loadNilaiRujukan(id, nama);
        });
    }

    /** Ambil semua row nilai rujukan untuk id_template tertentu, isi ke tbNilaiRujukan. */
    private void loadNilaiRujukan(String idTemplateStr, String namaPemeriksaan) {
        mdlNilaiRujukan.setRowCount(0);
        try { selectedIdTemplate = Integer.parseInt(idTemplateStr.trim()); }
        catch (NumberFormatException nfe) { selectedIdTemplate = -1; }
        lblRujukanHeader.setText("Nilai Rujukan: " + namaPemeriksaan);
        if (selectedIdTemplate < 0) return;
        try (PreparedStatement p = koneksi.prepareStatement(
                "SELECT n.id_nilai_rujukan, n.kd_kelompok_umur, m.nama, n.jk, n.nilai_min, n.nilai_max, " +
                "n.nilai_text, n.kritis_min, n.kritis_max, n.kondisi " +
                "FROM template_lab_nilai_rujukan n " +
                "LEFT JOIN master_kelompok_umur m ON m.kd_kelompok_umur = n.kd_kelompok_umur " +
                "WHERE n.id_template=? ORDER BY m.urut, n.jk")) {
            p.setInt(1, selectedIdTemplate);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) {
                    String kodeNama = r.getString("kd_kelompok_umur") + " - " + r.getString("nama");
                    String jkDb = r.getString("jk");
                    String jkDisplay = (jkDb == null || jkDb.isEmpty()) ? "L/P" : jkDb;
                    mdlNilaiRujukan.addRow(new Object[]{
                        kodeNama, jkDisplay,
                        nz(r.getString("nilai_min")), nz(r.getString("nilai_max")),
                        nz(r.getString("nilai_text")),
                        nz(r.getString("kritis_min")), nz(r.getString("kritis_max")),
                        nz(r.getString("kondisi")),
                        r.getInt("id_nilai_rujukan")
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("loadNilaiRujukan: " + e);
        }
        // Belum ada nilai rujukan tersimpan → prefill otomatis dari kolom legacy
        // (L.D / L.A / P.D / P.A) supaya tidak perlu input ulang. Belum disimpan
        // sampai user klik "Simpan Rujukan".
        if (mdlNilaiRujukan.getRowCount() == 0) {
            prefillFromLegacy(false);
        }
    }

    /**
     * Isi tabel nilai rujukan dari 4 kolom legacy di template_laboratorium
     * (nilai_rujukan_ld/la/pd/pa) berdasarkan row pemeriksaan yang dipilih.
     * Legacy hanya punya pembagian Dewasa vs Anak × L/P, jadi dipetakan ke
     * kelompok umur "Dewasa" dan "Anak" (default DWS / ANK). Kolom kritis
     * dibiarkan kosong karena tidak ada di data lama.
     *
     * @param fromButton true kalau dipicu tombol (tampilkan pesan & konfirmasi);
     *                   false kalau auto-prefill saat tabel masih kosong.
     */
    private void prefillFromLegacy(boolean fromButton) {
        if (selectedIdTemplate < 0) {
            if (fromButton) JOptionPane.showMessageDialog(this, "Pilih pemeriksaan di tabel atas dulu.");
            return;
        }
        int row = tbKamar.getSelectedRow();
        if (row < 0) return;
        // Kolom: 2=L.D, 3=L.A, 4=P.D, 5=P.A
        String ld = str(tbKamar.getValueAt(row, 2));
        String la = str(tbKamar.getValueAt(row, 3));
        String pd = str(tbKamar.getValueAt(row, 4));
        String pa = str(tbKamar.getValueAt(row, 5));
        if (ld.isEmpty() && la.isEmpty() && pd.isEmpty() && pa.isEmpty()) {
            if (fromButton) JOptionPane.showMessageDialog(this,
                "Tidak ada nilai rujukan lama (L.D / L.A / P.D / P.A) pada pemeriksaan ini.");
            return;
        }
        List<NilaiRujukanResolver.Kelompok> ks = NilaiRujukanResolver.listKelompok(koneksi);
        NilaiRujukanResolver.Kelompok dewasaK = pickKelompok(ks, true);
        NilaiRujukanResolver.Kelompok anakK   = pickKelompok(ks, false);
        if (dewasaK == null && anakK == null) {
            if (fromButton) JOptionPane.showMessageDialog(this,
                "Master kelompok umur masih kosong. Klik \"Master Kelompok\" dulu.");
            return;
        }
        if (fromButton && mdlNilaiRujukan.getRowCount() > 0) {
            int ans = JOptionPane.showConfirmDialog(this,
                "Tabel sudah ada isi. Tambahkan baris dari data lama?",
                "Isi dari Data Lama", JOptionPane.YES_NO_OPTION);
            if (ans != JOptionPane.YES_OPTION) return;
        }
        int before = mdlNilaiRujukan.getRowCount();
        addLegacyRows(dewasaK, ld, pd);
        addLegacyRows(anakK, la, pa);
        int added = mdlNilaiRujukan.getRowCount() - before;
        if (added > 0) {
            lblRujukanHeader.setText(lblRujukanHeader.getText() + "  (dari data lama — belum disimpan)");
        }
        if (fromButton) {
            JOptionPane.showMessageDialog(this, added > 0
                ? added + " baris diisi dari data lama. Cek lalu klik \"Simpan Rujukan\"."
                : "Tidak ada baris yang bisa diisi.");
        }
    }

    /** Pola "min - max" (toleransi koma desimal & berbagai jenis tanda hubung). */
    private static final java.util.regex.Pattern RANGE_PAT =
        java.util.regex.Pattern.compile("^\\s*(-?\\d+(?:[.,]\\d+)?)\\s*[-–—]\\s*(-?\\d+(?:[.,]\\d+)?)\\s*$");

    /**
     * Tambah baris untuk satu kelompok dengan nilai L dan P. Kalau L==P diisi
     * jadi satu baris "L/P". Nilai berformat range "a - b" dipecah ke Min/Max,
     * selain itu masuk ke kolom Text/Nilai.
     */
    private void addLegacyRows(NilaiRujukanResolver.Kelompok k, String valL, String valP) {
        if (k == null) return;
        String kodeNama = k.kode + " - " + k.nama;
        boolean hasL = !valL.isEmpty(), hasP = !valP.isEmpty();
        if (hasL && hasP && valL.equals(valP)) {
            addOneLegacyRow(kodeNama, "L/P", valL);
        } else {
            if (hasL) addOneLegacyRow(kodeNama, "L", valL);
            if (hasP) addOneLegacyRow(kodeNama, "P", valP);
        }
    }

    private void addOneLegacyRow(String kodeNama, String jk, String val) {
        String min = "", max = "", text = "";
        java.util.regex.Matcher m = RANGE_PAT.matcher(val);
        if (m.matches()) { min = m.group(1); max = m.group(2); }
        else             { text = val; }
        mdlNilaiRujukan.addRow(new Object[]{kodeNama, jk, min, max, text, "", "", "", 0});
    }

    /**
     * Pilih kelompok representatif untuk migrasi legacy. Prefer kode persis
     * ("DWS" untuk dewasa, "ANK" untuk anak); kalau tidak ada ambil kelompok
     * pertama yang klasifikasinya cocok.
     */
    private NilaiRujukanResolver.Kelompok pickKelompok(
            List<NilaiRujukanResolver.Kelompok> ks, boolean dewasa) {
        String prefer = dewasa ? "DWS" : "ANK";
        NilaiRujukanResolver.Kelompok firstMatch = null;
        for (NilaiRujukanResolver.Kelompok k : ks) {
            if (prefer.equalsIgnoreCase(k.kode)) return k;
            if (NilaiRujukanResolver.isDewasa(k.kode) == dewasa && firstMatch == null) {
                firstMatch = k;
            }
        }
        return firstMatch;
    }

    private void addEmptyRow() {
        if (selectedIdTemplate < 0) {
            JOptionPane.showMessageDialog(this, "Pilih pemeriksaan di tabel atas dulu.");
            return;
        }
        List<NilaiRujukanResolver.Kelompok> ks = NilaiRujukanResolver.listKelompok(koneksi);
        String defaultKelompok = ks.isEmpty() ? "" : ks.get(0).kode + " - " + ks.get(0).nama;
        mdlNilaiRujukan.addRow(new Object[]{defaultKelompok, "L", "", "", "", "", "", "", 0});
    }

    /** Generate 16 row: 8 kelompok umur × 2 JK (L, P) — kosong siap diisi. */
    private void quickFill() {
        if (selectedIdTemplate < 0) {
            JOptionPane.showMessageDialog(this, "Pilih pemeriksaan di tabel atas dulu.");
            return;
        }
        if (mdlNilaiRujukan.getRowCount() > 0) {
            int ans = JOptionPane.showConfirmDialog(this,
                "Tabel sudah ada isi. Tambahkan 16 row baru di bawah?",
                "Quick Fill", JOptionPane.YES_NO_OPTION);
            if (ans != JOptionPane.YES_OPTION) return;
        }
        for (NilaiRujukanResolver.Kelompok k : NilaiRujukanResolver.listKelompok(koneksi)) {
            String kodeNama = k.kode + " - " + k.nama;
            mdlNilaiRujukan.addRow(new Object[]{kodeNama, "L", "", "", "", "", "", "", 0});
            mdlNilaiRujukan.addRow(new Object[]{kodeNama, "P", "", "", "", "", "", "", 0});
        }
    }

    /** Duplikasi row terseleksi dengan JK berlawanan. */
    private void copyRowOppositeJk() {
        int row = tbNilaiRujukan.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Pilih row yang mau di-copy.");
            return;
        }
        String jk = str(mdlNilaiRujukan.getValueAt(row, COL_NR_JK));
        String oppJk = "L".equals(jk) ? "P" : ("P".equals(jk) ? "L" : "L/P");
        mdlNilaiRujukan.addRow(new Object[]{
            mdlNilaiRujukan.getValueAt(row, COL_NR_KELOMPOK),
            oppJk,
            mdlNilaiRujukan.getValueAt(row, COL_NR_MIN),
            mdlNilaiRujukan.getValueAt(row, COL_NR_MAX),
            mdlNilaiRujukan.getValueAt(row, COL_NR_TEXT),
            mdlNilaiRujukan.getValueAt(row, COL_NR_KRITIS_MIN),
            mdlNilaiRujukan.getValueAt(row, COL_NR_KRITIS_MAX),
            mdlNilaiRujukan.getValueAt(row, COL_NR_KONDISI),
            0   // new row, id=0 → insert on save
        });
    }

    private void deleteSelectedRow() {
        int row = tbNilaiRujukan.getSelectedRow();
        if (row < 0) return;
        if (tbNilaiRujukan.isEditing()) tbNilaiRujukan.getCellEditor().stopCellEditing();
        mdlNilaiRujukan.removeRow(row);
    }

    /** Simpan: delete all existing + batch insert. Transaction-safe. */
    private void saveNilaiRujukan() {
        if (selectedIdTemplate < 0) {
            JOptionPane.showMessageDialog(this, "Pilih pemeriksaan di tabel atas dulu.");
            return;
        }
        if (tbNilaiRujukan.isEditing()) tbNilaiRujukan.getCellEditor().stopCellEditing();

        try {
            koneksi.setAutoCommit(false);
            try (PreparedStatement del = koneksi.prepareStatement(
                    "DELETE FROM template_lab_nilai_rujukan WHERE id_template=?")) {
                del.setInt(1, selectedIdTemplate);
                del.executeUpdate();
            }
            try (PreparedStatement ins = koneksi.prepareStatement(
                    "INSERT INTO template_lab_nilai_rujukan " +
                    "(id_template, kd_kelompok_umur, jk, kondisi, nilai_min, nilai_max, nilai_text, " +
                    "kritis_min, kritis_max, urut) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?)")) {
                int urut = 1;
                for (int i = 0; i < mdlNilaiRujukan.getRowCount(); i++) {
                    String kelompok = str(mdlNilaiRujukan.getValueAt(i, COL_NR_KELOMPOK));
                    if (kelompok.isEmpty()) continue;
                    // Split "NEO - Neonatus" → ambil "NEO"
                    String kode = kelompok.contains(" - ") ? kelompok.split(" - ")[0] : kelompok;
                    String jk   = str(mdlNilaiRujukan.getValueAt(i, COL_NR_JK));
                    String jkDb = "L/P".equals(jk) ? "" : jk;
                    String min  = str(mdlNilaiRujukan.getValueAt(i, COL_NR_MIN));
                    String max  = str(mdlNilaiRujukan.getValueAt(i, COL_NR_MAX));
                    String text = str(mdlNilaiRujukan.getValueAt(i, COL_NR_TEXT));
                    String kMin = str(mdlNilaiRujukan.getValueAt(i, COL_NR_KRITIS_MIN));
                    String kMax = str(mdlNilaiRujukan.getValueAt(i, COL_NR_KRITIS_MAX));
                    String kond = str(mdlNilaiRujukan.getValueAt(i, COL_NR_KONDISI));
                    // Skip row kosong (tidak ada normal range dan tidak ada kritis threshold)
                    if (min.isEmpty() && max.isEmpty() && text.isEmpty() && kMin.isEmpty() && kMax.isEmpty()) continue;
                    ins.setInt(1, selectedIdTemplate);
                    ins.setString(2, kode);
                    ins.setString(3, jkDb);
                    ins.setString(4, kond);
                    ins.setString(5, min.isEmpty() ? null : min);
                    ins.setString(6, max.isEmpty() ? null : max);
                    ins.setString(7, text.isEmpty() ? null : text);
                    ins.setString(8, kMin.isEmpty() ? null : kMin);
                    ins.setString(9, kMax.isEmpty() ? null : kMax);
                    ins.setInt(10, urut++);
                    ins.addBatch();
                }
                ins.executeBatch();
            }
            koneksi.commit();
            koneksi.setAutoCommit(true);
            JOptionPane.showMessageDialog(this, "Nilai rujukan berhasil disimpan.");
            // Reload untuk refresh ID dari row baru
            String idStr = String.valueOf(selectedIdTemplate);
            String nama  = lblRujukanHeader.getText().replaceFirst("^Nilai Rujukan: ", "");
            loadNilaiRujukan(idStr, nama);
        } catch (Exception e) {
            try { koneksi.rollback(); koneksi.setAutoCommit(true); } catch (Exception ex) {}
            JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String nz(String s) { return s == null ? "" : s; }
    private static String str(Object o) { return o == null ? "" : o.toString().trim(); }

    private void openMasterKelompok() {
        DlgKelompokUmur d = new DlgKelompokUmur(
            (java.awt.Frame) SwingUtilities.getWindowAncestor(this), true);
        d.setVisible(true);
        // Setelah master ditutup, refresh combo editor "Kelompok Umur".
        JComboBox<String> cb = new JComboBox<>();
        for (NilaiRujukanResolver.Kelompok k : NilaiRujukanResolver.listKelompok(koneksi)) {
            cb.addItem(k.kode + " - " + k.nama);
        }
        tbNilaiRujukan.getColumnModel().getColumn(COL_NR_KELOMPOK).setCellEditor(new DefaultCellEditor(cb));
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbKamar = new widget.Table();
        panelisi3 = new widget.panelisi();
        label9 = new widget.Label();
        KdPeriksa = new widget.TextBox();
        NmPeriksa = new widget.TextBox();
        BtnTambah = new widget.Button();
        BtnSimpan = new widget.Button();
        BtnHapus = new widget.Button();
        jLabel10 = new widget.Label();
        BtnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Template Hasil Pemeriksaan Laboratorium ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50,50,50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbKamar.setName("tbKamar"); // NOI18N
        tbKamar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbKamarMouseClicked(evt);
            }
        });
        tbKamar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbKamarKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbKamar);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        panelisi3.setName("panelisi3"); // NOI18N
        panelisi3.setPreferredSize(new java.awt.Dimension(100, 43));
        panelisi3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 9));

        label9.setText("Pemeriksaan :");
        label9.setName("label9"); // NOI18N
        label9.setPreferredSize(new java.awt.Dimension(82, 23));
        panelisi3.add(label9);

        KdPeriksa.setName("KdPeriksa"); // NOI18N
        KdPeriksa.setPreferredSize(new java.awt.Dimension(100, 23));
        panelisi3.add(KdPeriksa);

        NmPeriksa.setName("NmPeriksa"); // NOI18N
        NmPeriksa.setPreferredSize(new java.awt.Dimension(330, 23));
        panelisi3.add(NmPeriksa);

        BtnTambah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        BtnTambah.setMnemonic('3');
        BtnTambah.setToolTipText("Alt+3");
        BtnTambah.setName("BtnTambah"); // NOI18N
        BtnTambah.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnTambah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTambahActionPerformed(evt);
            }
        });
        panelisi3.add(BtnTambah);

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });
        panelisi3.add(BtnSimpan);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus.setMnemonic('H');
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus"); // NOI18N
        BtnHapus.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusActionPerformed(evt);
            }
        });
        panelisi3.add(BtnHapus);

        jLabel10.setName("jLabel10"); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(jLabel10);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('4');
        BtnKeluar.setToolTipText("Alt+4");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        panelisi3.add(BtnKeluar);

        internalFrame1.add(panelisi3, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnTambahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTambahActionPerformed
        if(KdPeriksa.getText().equals("")||NmPeriksa.getText().equals("")){
            Valid.textKosong(KdPeriksa,"Pemeriksaan");
        }else{
            if(tbKamar.getSelectedRow()>-1){                
                tabMode.insertRow(tbKamar.getSelectedRow(),new Object[]{"","","","","","",0,0,0,0,0,0,0,0,""});
            }else{
                tabMode.addRow(new Object[]{"","","","","","",0,0,0,0,0,0,0,0,"",""});
                tbKamar.requestFocus();
            }
            urut();
        }
    }//GEN-LAST:event_BtnTambahActionPerformed

private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
    if(KdPeriksa.getText().equals("")||NmPeriksa.getText().equals("")){
        Valid.textKosong(KdPeriksa,"Pemeriksaan");
    }else{
        int reply = JOptionPane.showConfirmDialog(rootPane,"Eeiiiiiits, udah bener belum data yang mau disimpan..??","Konfirmasi",JOptionPane.YES_NO_OPTION);
        if(reply == JOptionPane.YES_OPTION) {
             int row=tbKamar.getRowCount();
             if(row>-1){
                 for(int i=0;i<row;i++){  
                     //System.out.println(tbKamar.getValueAt(i,0).toString());
                    if(tbKamar.getValueAt(i,14).toString().equals("")){
                         Sequel.menyimpan("template_laboratorium","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?","Id Hasil Periksa",17,new String[]{
                               KdPeriksa.getText(),"0",tbKamar.getValueAt(i,0).toString(),tbKamar.getValueAt(i,1).toString(),
                               tbKamar.getValueAt(i,2).toString(),tbKamar.getValueAt(i,3).toString(),tbKamar.getValueAt(i,4).toString(),
                               tbKamar.getValueAt(i,5).toString(),tbKamar.getValueAt(i,6).toString(),tbKamar.getValueAt(i,7).toString(),
                               tbKamar.getValueAt(i,8).toString(),tbKamar.getValueAt(i,9).toString(),tbKamar.getValueAt(i,10).toString(),
                               tbKamar.getValueAt(i,11).toString(),tbKamar.getValueAt(i,12).toString(),tbKamar.getValueAt(i,13).toString(),
                               tbKamar.getValueAt(i,15).toString()       
                         });
                    }else if(!tbKamar.getValueAt(i,14).toString().equals("")){
                         Sequel.mengedit("template_laboratorium","id_template=?","Pemeriksaan=?,satuan=?,nilai_rujukan_ld=?,nilai_rujukan_la=?,"+
                               "nilai_rujukan_pd=?,nilai_rujukan_pa=?,bagian_rs=?,bhp=?,bagian_perujuk=?,bagian_dokter=?,bagian_laborat=?,kso=?,menejemen=?,biaya_item=?,urut=?",16,new String[]{
                               tbKamar.getValueAt(i,0).toString(),tbKamar.getValueAt(i,1).toString(),tbKamar.getValueAt(i,2).toString(),tbKamar.getValueAt(i,3).toString(),
                               tbKamar.getValueAt(i,4).toString(),tbKamar.getValueAt(i,5).toString(),tbKamar.getValueAt(i,6).toString(),tbKamar.getValueAt(i,7).toString(),
                               tbKamar.getValueAt(i,8).toString(),tbKamar.getValueAt(i,9).toString(),tbKamar.getValueAt(i,10).toString(),tbKamar.getValueAt(i,11).toString(),
                               tbKamar.getValueAt(i,12).toString(),tbKamar.getValueAt(i,13).toString(),tbKamar.getValueAt(i,15).toString(),tbKamar.getValueAt(i,14).toString()
                         } );   
                    }
                 }
                 tampil();
             }             
        }
    }        
}//GEN-LAST:event_BtnSimpanActionPerformed

private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
   try{
       Sequel.meghapus("template_laboratorium","id_template",tbKamar.getValueAt(tbKamar.getSelectedRow(),14).toString());
       tabMode.removeRow(tbKamar.getSelectedRow());
       urut();
   }catch(Exception ex){
       JOptionPane.showMessageDialog(null,"Pilih dulu data yang mau dihapus..!!");
   }
}//GEN-LAST:event_BtnHapusActionPerformed

    private void tbKamarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbKamarKeyPressed
        if(tbKamar.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_RIGHT)||(evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }            
        }
    }//GEN-LAST:event_tbKamarKeyPressed

    private void tbKamarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbKamarMouseClicked
        if(tbKamar.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            } 
        }
    }//GEN-LAST:event_tbKamarMouseClicked

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgTemplateLaboratorium dialog = new DlgTemplateLaboratorium(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnSimpan;
    private widget.Button BtnTambah;
    public widget.TextBox KdPeriksa;
    public widget.TextBox NmPeriksa;
    private widget.ScrollPane Scroll;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label label9;
    private widget.panelisi panelisi3;
    private widget.Table tbKamar;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        runBackground(() ->LoadData());
    }
    
    private void LoadData() {
        Valid.tabelKosong(tabMode);
        try{            
            ps=koneksi.prepareStatement(
                    "select template_laboratorium.id_template,template_laboratorium.Pemeriksaan,template_laboratorium.satuan,template_laboratorium.nilai_rujukan_ld,template_laboratorium.nilai_rujukan_la,template_laboratorium.nilai_rujukan_pd,"+
                    "template_laboratorium.nilai_rujukan_pa,template_laboratorium.bagian_rs,template_laboratorium.bhp,template_laboratorium.bagian_perujuk,template_laboratorium.bagian_dokter,template_laboratorium.bagian_laborat,template_laboratorium.kso,template_laboratorium.menejemen,"+
                    "template_laboratorium.biaya_item,template_laboratorium.urut from template_laboratorium where template_laboratorium.kd_jenis_prw=? order by template_laboratorium.urut");
            try {
                ps.setString(1,KdPeriksa.getText());
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        rs.getString(2),rs.getString(3),rs.getString(4),
                        rs.getString(5),rs.getString(6),rs.getString(7),
                        rs.getDouble(8),rs.getDouble(9),rs.getDouble(10),
                        rs.getDouble(11),rs.getDouble(12),rs.getDouble(13),
                        rs.getDouble(14),rs.getDouble(15),rs.getString(1),""
                    });
                }
                urut();
            } catch (Exception e) {
                System.out.println(e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
    }

    private void getData() {
        int row=tbKamar.getSelectedRow();
        if(row!= -1){
            if((!tbKamar.getValueAt(row,6).toString().equals(""))
                    &&(!tbKamar.getValueAt(row,7).toString().equals(""))
                    &&(!tbKamar.getValueAt(row,8).toString().equals(""))
                    &&(!tbKamar.getValueAt(row,9).toString().equals(""))
                    &&(!tbKamar.getValueAt(row,10).toString().equals(""))
                    &&(!tbKamar.getValueAt(row,11).toString().equals(""))
                    &&(!tbKamar.getValueAt(row,12).toString().equals(""))){
               tbKamar.setValueAt(Valid.SetAngka(tbKamar.getValueAt(row,6).toString())+
                       Valid.SetAngka(tbKamar.getValueAt(row,7).toString())+
                       Valid.SetAngka(tbKamar.getValueAt(row,8).toString())+
                       Valid.SetAngka(tbKamar.getValueAt(row,9).toString())+
                       Valid.SetAngka(tbKamar.getValueAt(row,10).toString())+
                       Valid.SetAngka(tbKamar.getValueAt(row,11).toString())+
                       Valid.SetAngka(tbKamar.getValueAt(row,12).toString()), row,13); 
            }
        }
    } 
    
    public JTextField getTextField(){
        return KdPeriksa;
    }

    public JButton getButton(){
        return BtnKeluar;
    }
    
    public void urut(){
        if(tbKamar.getRowCount()>-1){
            for(int i=0;i<tbKamar.getRowCount();i++){  
                  tbKamar.setValueAt(i, i,15);
            }
        }
    }
    
    private void runBackground(Runnable task) {
        if (ceksukses) return;
        if (executor.isShutdown() || executor.isTerminated()) return;
        if (!isDisplayable()) return;

        ceksukses = true;
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            executor.submit(() -> {
                try {
                    task.run();
                } finally {
                    ceksukses = false;
                    SwingUtilities.invokeLater(() -> {
                        if (isDisplayable()) {
                            setCursor(Cursor.getDefaultCursor());
                        }
                    });
                }
            });
        } catch (RejectedExecutionException ex) {
            ceksukses = false;
        }
    }
    
    @Override
    public void dispose() {
        executor.shutdownNow();
        super.dispose();
    }
}
