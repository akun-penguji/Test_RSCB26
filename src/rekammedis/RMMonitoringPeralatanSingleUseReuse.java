package rekammedis;

import fungsi.akses;
import fungsi.koneksiDB;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Form "Monitoring Peralatan Single Use yang di Reuse" (Penata Anestesi).
 * Tiap baris = satu alat (Nama Alat / Ukuran / Kode Warna). Disimpan ke tabel
 * monitoring_peralatan_single_use_reuse per no_rawat. Nama penata anestesi
 * otomatis dari user login. Dibuka dari DlgBookingOperasi. Gaya UI + tombol CRUD
 * mengikuti form rekam medis Khanza (InternalFrame titled-border, FormInput
 * null-layout, tabel WarnaTable, tombol Simpan/Edit/Hapus/Batal/Cetak/Keluar).
 */
public final class RMMonitoringPeralatanSingleUseReuse extends javax.swing.JDialog {

    private final fungsi.sekuel Sequel = new fungsi.sekuel();
    private final fungsi.validasi Valid = new fungsi.validasi();
    private final java.sql.Connection koneksi = koneksiDB.condb();
    private final java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final DefaultTableModel tabMode;
    private final kepegawaian.DlgCariPetugas petugas = new kepegawaian.DlgCariPetugas(null, false);
    private String idEdit = "";

    public RMMonitoringPeralatanSingleUseReuse(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        tabMode = new DefaultTableModel(null, new Object[]{
            "ID", "No.", "Nama Alat", "Ukuran", "Kode Warna"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        initComponents();
        tbData.setModel(tabMode);
        tbData.setDefaultRenderer(Object.class, new fungsi.WarnaTable());
        tbData.getColumnModel().getColumn(0).setMinWidth(0);
        tbData.getColumnModel().getColumn(0).setMaxWidth(0);
        tbData.getColumnModel().getColumn(0).setPreferredWidth(0);
        tbData.getColumnModel().getColumn(1).setPreferredWidth(40);
        tbData.getColumnModel().getColumn(2).setPreferredWidth(360);
        tbData.getColumnModel().getColumn(3).setPreferredWidth(130);
        tbData.getColumnModel().getColumn(4).setPreferredWidth(130);
        tbData.setRowHeight(23);
    }

    // ================= API dipanggil DlgBookingOperasi =================
    public void isCek() {
        // Kendali akses mengikuti tombol pemanggil; form bebas diakses.
    }

    public void setNoRm(String norawat, Date tgl) {
        TNoRw.setText(norawat);
        String nm = Sequel.cariIsi("select pasien.nm_pasien from reg_periksa "
                + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                + "where reg_periksa.no_rawat=?", norawat);
        String rm = Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat=?", norawat);
        TPasien.setText(nm == null ? "" : nm);
        TNoRM.setText(rm == null ? "" : rm);
        // Tanggal monitoring = saat ini (bisa diubah manual), pola Khanza.
        Tgl.setDate(new Date());
        KdPetugas.setText(akses.getkode());
        String nmPtg = Sequel.cariIsi("select nama from petugas where nip=?", akses.getkode());
        NmPetugas.setText(nmPtg == null ? "" : nmPtg);
    }

    public void emptTeks() {
        TNamaAlat.setText("");
        TUkuran.setText("");
        TKodeWarna.setText("");
        idEdit = "";
    }

    /** Muat data alat untuk no_rawat yang sedang dibuka. */
    public void tampil() {
        tabMode.setRowCount(0);
        try {
            java.sql.PreparedStatement ps = koneksi.prepareStatement(
                "select id_msur, ifnull(nama_alat,''), ifnull(ukuran,''), ifnull(kode_warna,'') "
                + "from monitoring_peralatan_single_use_reuse where no_rawat=? order by id_msur");
            ps.setString(1, TNoRw.getText());
            java.sql.ResultSet rs = ps.executeQuery();
            int no = 0;
            while (rs.next()) {
                no++;
                tabMode.addRow(new Object[]{ rs.getString(1), no, rs.getString(2), rs.getString(3), rs.getString(4) });
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println("RMMonitoringPeralatanSingleUseReuse.tampil : " + e);
        }
    }

    /** Klik baris tabel → muat ke input untuk diedit. */
    private void getData() {
        int r = tbData.getSelectedRow();
        if (r == -1) return;
        idEdit = tabMode.getValueAt(r, 0).toString();
        TNamaAlat.setText(tabMode.getValueAt(r, 2).toString());
        TUkuran.setText(tabMode.getValueAt(r, 3).toString());
        TKodeWarna.setText(tabMode.getValueAt(r, 4).toString());
    }

    private void simpan() {
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Silahkan pilih data pasien terlebih dahulu..!!");
            return;
        }
        if (TNamaAlat.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Nama Alat masih kosong..!!");
            TNamaAlat.requestFocus();
            return;
        }
        String tgl = Tgl.getDate() == null ? fmt.format(new Date()) : fmt.format(Tgl.getDate());
        if (Sequel.queryu2tf("insert into monitoring_peralatan_single_use_reuse "
                + "(no_rawat,tanggal,nama_alat,ukuran,kode_warna,nip) values (?,?,?,?,?,?)", 6, new String[]{
                    TNoRw.getText(), tgl, TNamaAlat.getText(), TUkuran.getText(), TKodeWarna.getText(), akses.getkode()
                }) == true) {
            emptTeks();
            tampil();
            TNamaAlat.requestFocus();
        }
    }

    private void ganti() {
        if (idEdit.equals("")) {
            JOptionPane.showMessageDialog(null, "Silahkan pilih baris pada tabel yang akan diubah..!!");
            return;
        }
        if (TNamaAlat.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Nama Alat masih kosong..!!");
            TNamaAlat.requestFocus();
            return;
        }
        if (Sequel.queryu2tf("update monitoring_peralatan_single_use_reuse set nama_alat=?, ukuran=?, kode_warna=? "
                + "where id_msur=?", 4, new String[]{
                    TNamaAlat.getText(), TUkuran.getText(), TKodeWarna.getText(), idEdit
                }) == true) {
            emptTeks();
            tampil();
        }
    }

    private void hapus() {
        int r = tbData.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(null, "Silahkan pilih data pada tabel terlebih dahulu..!!");
            return;
        }
        if (JOptionPane.showConfirmDialog(this, "Hapus alat \"" + tabMode.getValueAt(r, 2) + "\" ?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        if (Sequel.queryu2tf("delete from monitoring_peralatan_single_use_reuse where id_msur=?", 1,
                new String[]{ tabMode.getValueAt(r, 0).toString() }) == true) {
            emptTeks();
            tampil();
        }
    }

    private void cetak() {
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Belum ada data pasien..!!");
            return;
        }
        HashMap<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kabupatenrs", akses.getkabupatenrs());
        param.put("namapenata", NmPetugas.getText());
        Valid.MyReportqry("rptMonitoringPeralatanSingleUseReuse.jasper", "report",
            "::[ Monitoring Peralatan Single Use yang di Reuse ]::",
            "select m.id_msur,reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,m.tanggal,"
            + "m.nama_alat,m.ukuran,m.kode_warna,ifnull(petugas.nama,m.nip) as namapenata "
            + "from monitoring_peralatan_single_use_reuse m "
            + "inner join reg_periksa on m.no_rawat=reg_periksa.no_rawat "
            + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
            + "left join petugas on petugas.nip=m.nip "
            + "where m.no_rawat='" + TNoRw.getText() + "' order by m.id_msur", param);
    }

    // ================= UI (hand-coded, gaya Khanza) =================
    private void initComponents() {
        internalFrame1 = new widget.InternalFrame();
        FormInput = new widget.PanelBiasa();
        TNoRw = new widget.TextBox();
        TNoRM = new widget.TextBox();
        TPasien = new widget.TextBox();
        Tgl = new widget.Tanggal();
        KdPetugas = new widget.TextBox();
        NmPetugas = new widget.TextBox();
        TNamaAlat = new widget.TextBox();
        TUkuran = new widget.TextBox();
        TKodeWarna = new widget.TextBox();
        BtnPetugas = new widget.Button();
        tbData = new widget.Table();
        jScrollPane1 = new widget.ScrollPane();
        panelInput = new widget.panelisi();
        panelGlass = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnEdit = new widget.Button();
        BtnHapus = new widget.Button();
        BtnBatal = new widget.Button();
        BtnCetak = new widget.Button();
        BtnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("::[ Monitoring Peralatan Single Use yang di Reuse ]::");
        getContentPane().setLayout(new BorderLayout());

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Monitoring Peralatan Single Use yang di Reuse ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout());

        // ---- Header pasien (null-layout ala Khanza) ----
        FormInput.setBackground(new Color(255, 255, 255));
        FormInput.setPreferredSize(new Dimension(890, 90));
        FormInput.setLayout(null);

        addLbl("No.Rawat :", 15, 14, 70);
        TNoRw.setEditable(false);
        FormInput.add(TNoRw); TNoRw.setBounds(90, 14, 150, 23);
        addLbl("No.RM :", 255, 14, 55);
        TNoRM.setEditable(false);
        FormInput.add(TNoRM); TNoRM.setBounds(312, 14, 110, 23);
        addLbl("Nama Pasien :", 435, 14, 85);
        TPasien.setEditable(false);
        FormInput.add(TPasien); TPasien.setBounds(520, 14, 350, 23);

        addLbl("Tanggal :", 15, 48, 70);
        Tgl.setDate(new Date()); // default = saat ini (pola Khanza)
        FormInput.add(Tgl); Tgl.setBounds(90, 48, 160, 23);
        addLbl("Penata Anestesi :", 255, 48, 105);
        KdPetugas.setEditable(false);
        FormInput.add(KdPetugas); KdPetugas.setBounds(362, 48, 80, 23);
        NmPetugas.setEditable(false);
        FormInput.add(NmPetugas); NmPetugas.setBounds(446, 48, 386, 23);
        BtnPetugas.setToolTipText("Cari Petugas (Penata Anestesi)");
        try { BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/attachment.png"))); } catch (Exception e) {}
        BtnPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { petugas.setLocationRelativeTo(null); petugas.setVisible(true); }
        });
        FormInput.add(BtnPetugas); BtnPetugas.setBounds(836, 48, 32, 23);
        petugas.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (petugas.getTable().getSelectedRow() != -1) {
                    KdPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                    NmPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

        internalFrame1.add(FormInput, BorderLayout.NORTH);

        // ---- Tabel ----
        tbData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { getData(); }
        });
        jScrollPane1.setViewportView(tbData);
        internalFrame1.add(jScrollPane1, BorderLayout.CENTER);

        // ---- Input alat ----
        panelInput.setBackground(new Color(255, 255, 255));
        panelInput.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 7));
        panelInput.add(lbl("Nama Alat :"));
        TNamaAlat.setPreferredSize(new Dimension(330, 23));
        panelInput.add(TNamaAlat);
        panelInput.add(lbl("Ukuran :"));
        TUkuran.setPreferredSize(new Dimension(110, 23));
        panelInput.add(TUkuran);
        panelInput.add(lbl("Kode Warna :"));
        TKodeWarna.setPreferredSize(new Dimension(110, 23));
        panelInput.add(TKodeWarna);

        // ---- Tombol CRUD ala Khanza ----
        panelGlass.setBackground(new Color(255, 255, 255));
        panelGlass.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 7));
        tombol(BtnSimpan, "Simpan", 'S', "/picture/save-16x16.png", (e) -> simpan());
        tombol(BtnEdit,   "Edit",   'E', "/picture/edit.png",        (e) -> ganti());
        tombol(BtnHapus,  "Hapus",  'H', "/picture/stop_f2.png",     (e) -> hapus());
        tombol(BtnBatal,  "Batal",  'B', "/picture/cancel.png",      (e) -> emptTeks());
        tombol(BtnCetak,  "Cetak",  'C', "/picture/print.png",       (e) -> cetak());
        tombol(BtnKeluar, "Keluar", 'K', "/picture/exit.png",        (e) -> dispose());
        panelGlass.add(BtnSimpan);
        panelGlass.add(BtnEdit);
        panelGlass.add(BtnHapus);
        panelGlass.add(BtnBatal);
        panelGlass.add(BtnCetak);
        panelGlass.add(BtnKeluar);

        widget.PanelBiasa bawah = new widget.PanelBiasa();
        bawah.setBackground(new Color(255, 255, 255));
        bawah.setLayout(new BorderLayout());
        bawah.add(panelInput, BorderLayout.NORTH);
        bawah.add(panelGlass, BorderLayout.SOUTH);
        internalFrame1.add(bawah, BorderLayout.SOUTH);

        getContentPane().add(internalFrame1, BorderLayout.CENTER);
        setSize(900, 560);
        setLocationRelativeTo(null);
    }

    private void tombol(widget.Button b, String teks, char mn, String ikon, java.util.function.Consumer<java.awt.event.ActionEvent> aksi) {
        b.setText(teks);
        b.setMnemonic(mn);
        b.setPreferredSize(new Dimension(100, 30));
        try { b.setIcon(new javax.swing.ImageIcon(getClass().getResource(ikon))); } catch (Exception e) {}
        b.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) { aksi.accept(evt); }
        });
    }

    private void addLbl(String t, int x, int y, int w) {
        widget.Label l = lbl(t);
        FormInput.add(l);
        l.setBounds(x, y, w, 23);
    }

    private widget.Label lbl(String t) {
        widget.Label l = new widget.Label();
        l.setText(t);
        l.setFont(new Font("Tahoma", 0, 11));
        l.setForeground(new Color(50, 50, 50));
        return l;
    }

    private widget.InternalFrame internalFrame1;
    private widget.PanelBiasa FormInput;
    private widget.ScrollPane jScrollPane1;
    private widget.Table tbData;
    private widget.panelisi panelInput, panelGlass;
    private widget.TextBox TNoRw, TNoRM, TPasien, KdPetugas, NmPetugas, TNamaAlat, TUkuran, TKodeWarna;
    private widget.Tanggal Tgl;
    private widget.Button BtnSimpan, BtnEdit, BtnHapus, BtnBatal, BtnCetak, BtnKeluar, BtnPetugas;
}
