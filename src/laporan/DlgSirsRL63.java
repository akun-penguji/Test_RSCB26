package laporan;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import laporan.sirs.SirsRLDef;
import laporan.sirs.SirsRLRegistry;
import laporan.sirs.SirsResult;

/**
 * Dialog terpusat Laporan SIRS RL 6.3 (JUKNIS Kemenkes 2025) versi Khanza.
 *
 * Data-driven: memilih salah satu dari 30 formulir RL dari registry, lalu
 * menampilkan hasil realtime dari DB Khanza (rentang Tgl1..Tgl2). Tiap form
 * mendeskripsikan kolomnya sendiri (mendukung form matriks). Tombol Cetak
 * (Jasper generik, maksimal 12 kolom) dan Ekspor Excel (semua kolom).
 *
 * Sejalan dengan versi web simrs-dt-trimitra (controller Sirs\Rl*).
 */
public final class DlgSirsRL63 extends javax.swing.JDialog {

    private final validasi Valid = new validasi();
    private final sekuel Sequel = new sekuel();
    private DefaultTableModel tabMode;
    private List<SirsRLDef> daftar;
    private SirsResult hasil;

    public DlgSirsRL63(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(1000, 680);
        setLocationRelativeTo(null);

        daftar = SirsRLRegistry.all();
        for (SirsRLDef d : daftar) {
            cmbForm.addItem(d.toString());
        }
        initTableDefault();

        // Default rentang: awal bulan berjalan s.d. hari ini (kebanyakan form bulanan).
        java.util.Calendar cal = java.util.Calendar.getInstance();
        Tgl2.setDate(cal.getTime());
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        Tgl1.setDate(cal.getTime());
    }

    /**
     * Buka langsung terkunci ke satu formulir (dipakai tombol menu per-form di
     * frmUtama). Pemilih dinonaktifkan; data langsung ditampilkan.
     */
    public DlgSirsRL63(java.awt.Frame parent, boolean modal, String kode) {
        this(parent, modal);
        for (int i = 0; i < daftar.size(); i++) {
            if (daftar.get(i).kode.equals(kode)) {
                cmbForm.setSelectedIndex(i);
                break;
            }
        }
        cmbForm.setEnabled(false);
        SirsRLDef d = daftar.get(cmbForm.getSelectedIndex());
        setTitle("Laporan SIRS RL " + d.kode + " — " + d.judul);
        tampil();
    }

    private void initTableDefault() {

        tabMode = new DefaultTableModel(null, new String[]{"No.", "Keterangan"}) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tbData.setModel(tabMode);
        tbData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbData.setDefaultRenderer(Object.class, new WarnaTable());
        tbData.setPreferredScrollableViewportSize(new Dimension(700, 500));
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbData = new widget.Table();
        labForm = new widget.Label();
        cmbForm = new widget.ComboBox();
        labTgl = new widget.Label();
        Tgl1 = new widget.Tanggal();
        labSd = new widget.Label();
        Tgl2 = new widget.Tanggal();
        BtnTampil = new widget.Button();
        panelBawah = new widget.panelisi();
        BtnCetak = new widget.Button();
        BtnExcel = new widget.Button();
        BtnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Laporan SIRS RL 6.3");
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
                "::[ Laporan SIRS RL 6.3 — JUKNIS Kemenkes 2025 ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        Scroll.setViewportView(tbData);
        internalFrame1.add(Scroll, BorderLayout.CENTER);

        lblCatatan = new widget.Label();
        lblCatatan.setText(" ");
        lblCatatan.setPreferredSize(new Dimension(200, 20));
        internalFrame1.add(lblCatatan, BorderLayout.PAGE_START);

        // --- panel bawah (toolbar glass) : pemilih form + rentang tanggal + aksi ---
        panelBawah.setPreferredSize(new Dimension(55, 55));
        panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));

        labForm.setText("Formulir :");
        labForm.setPreferredSize(new Dimension(58, 23));
        panelBawah.add(labForm);

        cmbForm.setPreferredSize(new Dimension(360, 23));
        panelBawah.add(cmbForm);

        labTgl.setText("Tanggal :");
        labTgl.setPreferredSize(new Dimension(55, 23));
        panelBawah.add(labTgl);

        Tgl1.setDisplayFormat("dd-MM-yyyy");
        Tgl1.setPreferredSize(new Dimension(95, 23));
        panelBawah.add(Tgl1);

        labSd.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labSd.setText("s.d.");
        labSd.setPreferredSize(new Dimension(25, 23));
        panelBawah.add(labSd);

        Tgl2.setDisplayFormat("dd-MM-yyyy");
        Tgl2.setPreferredSize(new Dimension(95, 23));
        panelBawah.add(Tgl2);

        BtnTampil.setIcon(ikon("/picture/accept.png"));
        BtnTampil.setMnemonic('1');
        BtnTampil.setText("Tampilkan");
        BtnTampil.setToolTipText("Alt+1");
        BtnTampil.setPreferredSize(new Dimension(110, 30));
        BtnTampil.addActionListener(evt -> tampil());
        panelBawah.add(BtnTampil);

        BtnCetak.setIcon(ikon("/picture/b_print.png"));
        BtnCetak.setMnemonic('2');
        BtnCetak.setText("Cetak");
        BtnCetak.setToolTipText("Alt+2");
        BtnCetak.setPreferredSize(new Dimension(100, 30));
        BtnCetak.addActionListener(evt -> cetak());
        panelBawah.add(BtnCetak);

        BtnExcel.setIcon(ikon("/picture/export-excel.png"));
        BtnExcel.setMnemonic('3');
        BtnExcel.setText("Excel");
        BtnExcel.setToolTipText("Alt+3");
        BtnExcel.setPreferredSize(new Dimension(100, 30));
        BtnExcel.addActionListener(evt -> excel());
        panelBawah.add(BtnExcel);

        BtnKirimSirs = new widget.Button();
        BtnKirimSirs.setIcon(ikon("/picture/accept.png"));
        BtnKirimSirs.setMnemonic('4');
        BtnKirimSirs.setText("Kirim SIRS");
        BtnKirimSirs.setToolTipText("Kirim Laporan ke SIRS Online Kemenkes (Port 8080)");
        BtnKirimSirs.setPreferredSize(new Dimension(115, 30));
        BtnKirimSirs.addActionListener(evt -> kirimSirs());
        panelBawah.add(BtnKirimSirs);

        BtnKeluar.setIcon(ikon("/picture/exit.png"));
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setPreferredSize(new Dimension(100, 30));
        BtnKeluar.addActionListener(evt -> dispose());
        panelBawah.add(BtnKeluar);

        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, BorderLayout.CENTER);
        pack();
    }

    /** Jalankan builder form terpilih dan render ke tabel. */
    private void tampil() {
        int idx = cmbForm.getSelectedIndex();
        if (idx < 0 || idx >= daftar.size()) return;
        SirsRLDef def = daftar.get(idx);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            String tgl1 = Valid.SetTgl(Tgl1.getSelectedItem() + "");
            String tgl2 = Valid.SetTgl(Tgl2.getSelectedItem() + "");
            hasil = def.builder.build(koneksiDB.condb(), tgl1, tgl2);

            tabMode = new DefaultTableModel(null, hasil.kolom) {
                @Override
                public boolean isCellEditable(int r, int c) { return false; }
            };
            tbData.setModel(tabMode);
            for (Object[] row : hasil.rows) {
                tabMode.addRow(row);
            }
            aturLebarKolom();
            tbData.setDefaultRenderer(Object.class, new WarnaTable());
            lblCatatan.setText(hasil.catatan == null || hasil.catatan.isEmpty() ? " " : "  " + hasil.catatan);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat RL " + def.kode + " :\n" + e);
            System.out.println("DlgSirsRL63 RL " + def.kode + " : " + e);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void aturLebarKolom() {
        int n = tbData.getColumnCount();
        for (int i = 0; i < n; i++) {
            TableColumn col = tbData.getColumnModel().getColumn(i);
            if (i == 0) col.setPreferredWidth(35);
            else if (i == 1) col.setPreferredWidth(n <= 8 ? 320 : 200);
            else col.setPreferredWidth(90);
        }
    }

    /**
     * Cetak lewat jasper yang disusun on the fly (SirsRLReport) supaya lebar
     * kolomnya menyesuaikan formulir yang dipilih — tidak ada kolom kosong dan
     * kolom uraian tidak terlipat.
     */
    private void cetak() {
        if (hasil == null || tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data untuk dicetak. Klik Tampilkan dulu.");
            return;
        }
        if (hasil.kolom.length > 16) {
            JOptionPane.showMessageDialog(this, "Formulir ini berkolom banyak (" + hasil.kolom.length
                    + "). Gunakan tombol Excel untuk cetak/ekspor.");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            SirsRLDef def = daftar.get(cmbForm.getSelectedIndex());
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));

            SirsRLReport.cetak("RL " + def.kode + " \u2014 " + def.judul,
                    Tgl1.getSelectedItem() + " s.d. " + Tgl2.getSelectedItem(),
                    Tgl2.getDate(), hasil.kolom, tabMode, param);
        } catch (Exception e) {
            System.out.println("Notif cetak SIRS RL : " + e);
            JOptionPane.showMessageDialog(this, "Gagal mencetak :\n" + e);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    /** Ekspor seluruh kolom & baris ke berkas .xls (jxl). */
    private void excel() {
        if (hasil == null || tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data untuk diekspor. Klik Tampilkan dulu.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        SirsRLDef def = daftar.get(cmbForm.getSelectedIndex());
        fc.setSelectedFile(new File("RL_" + def.kode.replace(".", "_") + ".xls"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            File f = fc.getSelectedFile();
            if (!f.getName().toLowerCase().endsWith(".xls")) {
                f = new File(f.getParentFile(), f.getName() + ".xls");
            }
            WritableWorkbook wb = Workbook.createWorkbook(f);
            WritableSheet sh = wb.createSheet("RL " + def.kode, 0);
            int nc = hasil.kolom.length;
            for (int c = 0; c < nc; c++) {
                sh.addCell(new Label(c, 0, hasil.kolom[c]));
            }
            for (int r = 0; r < tabMode.getRowCount(); r++) {
                for (int c = 0; c < nc; c++) {
                    Object v = tabMode.getValueAt(r, c);
                    sh.addCell(new Label(c, r + 1, v == null ? "" : v.toString()));
                }
            }
            wb.write();
            wb.close();
            JOptionPane.showMessageDialog(this, "Tersimpan: " + f.getAbsolutePath());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal ekspor Excel :\n" + e);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    /** ImageIcon aman-null: kembalikan null bila resource tak ada (hindari NPE). */
    private javax.swing.ImageIcon ikon(String path) {
        java.net.URL u = getClass().getResource(path);
        return u != null ? new javax.swing.ImageIcon(u) : null;
    }

    /**
     * Kirim data formulir aktif ke SIRS Online 6.3 Kemenkes RI via backend simrscb
     */
    private void kirimSirs() {
        int idx = cmbForm.getSelectedIndex();
        if (idx < 0 || idx >= daftar.size()) {
            JOptionPane.showMessageDialog(this, "Pilih formulir SIRS terlebih dahulu.");
            return;
        }
        if (hasil == null || tabMode == null || tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data untuk dikirim. Klik \"Tampilkan\" terlebih dahulu.");
            return;
        }

        SirsRLDef def = daftar.get(idx);
        String formKode = def.kode;
        SirsResult dataKirim = hasil;

        String tgl1 = Valid.SetTgl(Tgl1.getSelectedItem() + "");
        String tgl2 = Valid.SetTgl(Tgl2.getSelectedItem() + "");
        int tahun = 2026;
        try {
            tahun = Integer.parseInt(tgl1.substring(0, 4));
        } catch (Exception ignored) {}

        String tahunBulan = tgl1.length() >= 7 ? tgl1.substring(0, 7) + "-01" : tgl1;

        if (def.kode.equals("4.2") || def.kode.equals("4.3")) {
            int konf = JOptionPane.showConfirmDialog(this,
                "Laporan RL " + def.kode + " (" + def.judul + ") dihitung otomatis oleh SIRS Kemenkes dari data RL 4.1 (Kompilasi Morbiditas Pasien Rawat Inap).\n\n"
                + "Apakah Anda ingin langsung memproses dan mengirimkan data RL 4.1 untuk periode " + tahunBulan + " ke Kemenkes agar data 10 Besar ini terisi?",
                "Konfirmasi Integrasi SIRS Online", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (konf != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                dataKirim = new laporan.sirs.Rl41().build(koneksiDB.condb(), tgl1, tgl2);
                formKode = "4.1";
            } catch (Exception e) {
                this.setCursor(Cursor.getDefaultCursor());
                JOptionPane.showMessageDialog(this, "Gagal memproses data RL 4.1:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if (def.kode.equals("5.2") || def.kode.equals("5.3")) {
            int konf = JOptionPane.showConfirmDialog(this,
                "Laporan RL " + def.kode + " (" + def.judul + ") dihitung otomatis oleh SIRS Kemenkes dari data RL 5.1 (Kompilasi Morbiditas Pasien Rawat Jalan).\n\n"
                + "Apakah Anda ingin langsung memproses dan mengirimkan data RL 5.1 untuk periode " + tahunBulan + " ke Kemenkes agar data 10 Besar ini terisi?",
                "Konfirmasi Integrasi SIRS Online", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (konf != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                dataKirim = new laporan.sirs.Rl51().build(koneksiDB.condb(), tgl1, tgl2);
                formKode = "5.1";
            } catch (Exception e) {
                this.setCursor(Cursor.getDefaultCursor());
                JOptionPane.showMessageDialog(this, "Gagal memproses data RL 5.1:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            int konfirmasi = JOptionPane.showConfirmDialog(this,
                "Kirim laporan RL " + def.kode + " (" + def.judul + ")?\n"
                + "Periode: " + tahunBulan + " (" + tabMode.getRowCount() + " baris data)\n"
                + "Data akan divalidasi dengan JUKNIS SIRS 6.3 dan dikirim ke server Kemenkes RI.",
                "Konfirmasi Pengiriman SIRS Online",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (konfirmasi != JOptionPane.YES_OPTION) {
                return;
            }
        }

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            bridging.Sirs6BotBridge.BridgeResult res = bridging.Sirs6BotBridge.kirimLaporan(formKode, tahun, tahunBulan, dataKirim);
            if (res.ok) {
                JOptionPane.showMessageDialog(this,
                    "Berhasil!\n" + res.message + "\n\nRespons Kemenkes:\n" + res.detail,
                    "SIRS Online 6.3 - Sukses", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    res.message + "\n\nKeterangan:\n" + res.detail,
                    "SIRS Online 6.3 - Perhatian", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Terjadi kesalahan saat mengirim laporan:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            this.setCursor(Cursor.getDefaultCursor());
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgSirsRL63 d = new DlgSirsRL63(new javax.swing.JFrame(), true);
            d.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) { System.exit(0); }
            });
            d.setVisible(true);
        });
    }

    private widget.Button BtnCetak;
    private widget.Button BtnExcel;
    private widget.Button BtnKirimSirs;
    private widget.Button BtnKeluar;
    private widget.Button BtnTampil;
    private widget.ScrollPane Scroll;
    private widget.Tanggal Tgl1;
    private widget.Tanggal Tgl2;
    private widget.ComboBox cmbForm;
    private widget.InternalFrame internalFrame1;
    private widget.Label lblCatatan;
    private widget.Label labForm;
    private widget.Label labSd;
    private widget.Label labTgl;
    private widget.panelisi panelBawah;
    private widget.Table tbData;
}
