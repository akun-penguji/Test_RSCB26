package kepegawaian;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Audit Bundles Pencegahan Infeksi Daerah Operasi (IDO) — PERI OPERATIF (Kamar Operasi / OK).
 * Form FLAT: diisi saat operasi, per pasien per tanggal. Tabel: monitoring_ido_ok (key no_rawat+tanggal).
 */
public final class DlgAuditBundleIDOPeriOp extends javax.swing.JDialog {

    private DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;

    private simrskhanza.DlgCariPasien pasien = new simrskhanza.DlgCariPasien(null, false);
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);

    private final widget.TextBox TNoRw = new widget.TextBox();
    private final widget.TextBox TNoRM = new widget.TextBox();
    private final widget.TextBox TPasien = new widget.TextBox();
    private final widget.TextBox KdPetugas = new widget.TextBox();
    private final widget.TextBox NmPetugas = new widget.TextBox();
    private final widget.TextBox TCari = new widget.TextBox();
    private final widget.Tanggal Tanggal = new widget.Tanggal();
    private final widget.Tanggal DTPCari1 = new widget.Tanggal();
    private final widget.Tanggal DTPCari2 = new widget.Tanggal();
    private final widget.ComboBox cHH = combo();
    private final widget.ComboBox cAPD = combo();
    private final widget.ComboBox cAntiseptik = combo();
    private final widget.ComboBox cSuhu = combo();
    private final widget.ComboBox cNormo = combo();
    private final widget.Table tbObat = new widget.Table();
    private final widget.Button BtnSimpan = new widget.Button();
    private final widget.Button BtnEdit = new widget.Button();
    private final widget.Button BtnHapus = new widget.Button();
    private final widget.Button BtnBatal = new widget.Button();
    private final widget.Button BtnPrint = new widget.Button();
    private final widget.Button BtnKeluar = new widget.Button();
    private final widget.Button btnPasien = new widget.Button();
    private final widget.Button btnPetugas = new widget.Button();
    private final widget.Button BtnCari = new widget.Button();

    private static widget.ComboBox combo() {
        widget.ComboBox c = new widget.ComboBox();
        c.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ya", "Tidak", "NA"}));
        return c;
    }

    public DlgAuditBundleIDOPeriOp(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Audit Bundles IDO — Peri Operatif (Kamar Operasi)");
        buildUI();
        setSize(1000, 680);
        setLocationRelativeTo(parent);

        tabMode = new DefaultTableModel(null, new Object[]{
            "Tanggal", "No.Rawat", "No.RM", "Pasien",
            "Kebersihan Tangan", "APD", "Antiseptik Kulit", "Suhu/Kelembaban Ruang", "Normothermia",
            "Patuh(%)", "Petugas", "Kd.Petugas"
        }) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(960, 360));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());
        int[] w = {120, 110, 80, 150, 110, 50, 100, 130, 90, 60, 130, 0};
        for (i = 0; i < w.length && i < tbObat.getColumnModel().getColumnCount(); i++) {
            tbObat.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        }
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { getData(); }
        });

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        TCari.setDocument(new batasInput((int) 100).getKata(TCari));

        pasien.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                if (pasien.getTable().getSelectedRow() != -1) {
                    TNoRw.setText(pasien.getTable().getValueAt(pasien.getTable().getSelectedRow(), 0).toString());
                    isRawat();
                }
                TNoRw.requestFocus();
            }
        });
        petugas.addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                if (petugas.getTable().getSelectedRow() != -1) {
                    KdPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                    NmPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                }
            }
        });
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN || evt.getKeyCode() == KeyEvent.VK_ENTER) { isRawat(); }
            }
        });
        tampil();
    }

    private JPanel row(String label, java.awt.Component... comps) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        if (label != null) { JLabel l = new JLabel(label); l.setPreferredSize(new Dimension(120, 23)); p.add(l); }
        for (java.awt.Component c : comps) p.add(c);
        return p;
    }

    private JPanel indRow(String label, widget.ComboBox cb) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 3));
        p.setBackground(new java.awt.Color(255, 255, 254));
        JLabel l = new JLabel(label);
        l.setPreferredSize(new Dimension(590, 23));
        l.setFont(new java.awt.Font("Tahoma", 0, 11));
        cb.setPreferredSize(new Dimension(110, 23));
        p.add(l); p.add(cb);
        return p;
    }

    private void icon(widget.Button b, String png, char mnem, String text) {
        try { b.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/" + png))); } catch (Exception e) {}
        b.setMnemonic(mnem); b.setText(text);
    }

    private static String fmt(java.util.Date d, String pat) {
        return d == null ? "" : new java.text.SimpleDateFormat(pat).format(d);
    }

    private void buildUI() {
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(BorderFactory.createTitledBorder("Audit Peri Operatif IDO (Ya / Tidak / NA) — diisi saat operasi"));

        TNoRw.setPreferredSize(new Dimension(150, 23));
        TNoRM.setPreferredSize(new Dimension(110, 23)); TNoRM.setEditable(false);
        TPasien.setPreferredSize(new Dimension(220, 23)); TPasien.setEditable(false);
        btnPasien.setText("Cari Pasien"); btnPasien.addActionListener(e -> pasien.setVisible(true));
        top.add(row("No.Rawat :", TNoRw, btnPasien, new JLabel(" No.RM:"), TNoRM, new JLabel(" Pasien:"), TPasien));

        Tanggal.setPreferredSize(new Dimension(170, 23));
        top.add(row("Tgl Operasi :", Tanggal));

        JPanel ind = new JPanel();
        ind.setBackground(new java.awt.Color(255, 255, 254));
        ind.setLayout(new BoxLayout(ind, BoxLayout.Y_AXIS));
        ind.add(indRow("1. Petugas melakukan kebersihan tangan", cHH));
        ind.add(indRow("2. Petugas menggunakan APD sesuai pajanan", cAPD));
        ind.add(indRow("3. Antiseptik permukaan kulit (alkohol 70% / iodine tincture 2% / chlorhexidine 2-4%)", cAntiseptik));
        ind.add(indRow("4. Temperatur ruang 19-24°C dan kelembaban 40-60%", cSuhu));
        ind.add(indRow("5. Pertahankan suhu tubuh pasien (normothermia)", cNormo));
        top.add(ind);

        KdPetugas.setPreferredSize(new Dimension(90, 23)); KdPetugas.setEditable(false);
        NmPetugas.setPreferredSize(new Dimension(200, 23)); NmPetugas.setEditable(false);
        btnPetugas.setText("Cari Petugas"); btnPetugas.addActionListener(e -> petugas.setVisible(true));
        top.add(row("Petugas :", KdPetugas, NmPetugas, btnPetugas));

        icon(BtnSimpan, "save-16x16.png", 'S', "Simpan"); BtnSimpan.addActionListener(e -> simpan());
        icon(BtnEdit, "inventaris.png", 'G', "Ganti"); BtnEdit.addActionListener(e -> edit());
        icon(BtnHapus, "stop_f2.png", 'H', "Hapus"); BtnHapus.addActionListener(e -> hapus());
        icon(BtnBatal, "Cancel-2-16x16.png", 'B', "Baru"); BtnBatal.addActionListener(e -> emptTeks());
        icon(BtnPrint, "b_print.png", 'T', "Cetak"); BtnPrint.addActionListener(e -> cetak());
        icon(BtnKeluar, "exit.png", 'K', "Keluar"); BtnKeluar.addActionListener(e -> dispose());

        DTPCari1.setPreferredSize(new Dimension(160, 23));
        DTPCari2.setPreferredSize(new Dimension(160, 23));
        TCari.setPreferredSize(new Dimension(180, 23));
        BtnCari.setText("Tampil"); BtnCari.addActionListener(e -> tampil());
        JPanel filter = row("Filter Tanggal :", DTPCari1, new JLabel(" s/d "), DTPCari2,
                new JLabel(" Cari:"), TCari, BtnCari);

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
        tombol.add(BtnSimpan); tombol.add(BtnEdit); tombol.add(BtnHapus);
        tombol.add(BtnBatal); tombol.add(BtnPrint); tombol.add(BtnKeluar);

        JPanel south = new JPanel(new BorderLayout(2, 2));
        south.add(filter, BorderLayout.PAGE_START);
        south.add(tombol, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout(2, 2));
        getContentPane().add(top, BorderLayout.PAGE_START);
        getContentPane().add(new JScrollPane(tbObat), BorderLayout.CENTER);
        getContentPane().add(south, BorderLayout.PAGE_END);
    }

    private void isRawat() {
        if (TNoRw.getText().trim().isEmpty()) return;
        try {
            ps = koneksi.prepareStatement(
                "select reg_periksa.no_rkm_medis,pasien.nm_pasien from reg_periksa "
              + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis where reg_periksa.no_rawat=?");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            if (rs.next()) { TNoRM.setText(rs.getString("no_rkm_medis")); TPasien.setText(rs.getString("nm_pasien")); }
            else { TNoRM.setText(""); TPasien.setText(""); JOptionPane.showMessageDialog(null, "No.Rawat tidak ditemukan."); }
        } catch (Exception e) { System.out.println("Notif isRawat : " + e);
        } finally { try { if (rs != null) rs.close(); } catch (Exception e) {} try { if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    private void simpan() {
        if (TNoRw.getText().trim().isEmpty()) { Valid.textKosong(TNoRw, "No.Rawat"); return; }
        if (KdPetugas.getText().trim().isEmpty()) { Valid.textKosong(KdPetugas, "Petugas"); return; }
        if (Tanggal.getDate() == null) { JOptionPane.showMessageDialog(null, "Isi Tgl Operasi dulu."); return; }
        String tgl = fmt(Tanggal.getDate(), "yyyy-MM-dd") + " " + fmt(new java.util.Date(), "HH:mm:ss");
        boolean ok = Sequel.queryu2tf("replace into monitoring_ido_ok "
            + "(no_rawat,tanggal,peri_hh,peri_apd,peri_antiseptik,peri_suhu_ruang,peri_normothermia,kd_petugas) "
            + "values(?,?,?,?,?,?,?,?)", 8, new String[]{
                TNoRw.getText(), tgl,
                cHH.getSelectedItem().toString(), cAPD.getSelectedItem().toString(), cAntiseptik.getSelectedItem().toString(),
                cSuhu.getSelectedItem().toString(), cNormo.getSelectedItem().toString(), KdPetugas.getText()});
        if (ok) { tampil(); emptTeks(); }
        else JOptionPane.showMessageDialog(null, "Gagal menyimpan..!!");
    }

    private void edit() {
        if (tbObat.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih baris yang mau diubah."); return; }
        // tanggal baris dipakai sebagai key edit (bukan tanggal picker)
        String tgl = v(0);
        boolean ok = Sequel.queryu2tf("replace into monitoring_ido_ok "
            + "(no_rawat,tanggal,peri_hh,peri_apd,peri_antiseptik,peri_suhu_ruang,peri_normothermia,kd_petugas) "
            + "values(?,?,?,?,?,?,?,?)", 8, new String[]{
                v(1), tgl,
                cHH.getSelectedItem().toString(), cAPD.getSelectedItem().toString(), cAntiseptik.getSelectedItem().toString(),
                cSuhu.getSelectedItem().toString(), cNormo.getSelectedItem().toString(), KdPetugas.getText()});
        if (ok) { tampil(); emptTeks(); }
        else JOptionPane.showMessageDialog(null, "Gagal mengubah..!!");
    }

    private void hapus() {
        if (tbObat.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih baris yang mau dihapus."); return; }
        if (Sequel.queryu2tf("delete from monitoring_ido_ok where no_rawat=? and tanggal=?", 2,
                new String[]{v(1), v(0)}) == true) { tampil(); emptTeks(); }
        else JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
    }

    /** Dipanggil DlgKamarInap: set pasien terpilih (no_rawat) lalu lookup data pasien. */
    public void setNoRm(String noRawat, java.util.Date tgl) {
        TNoRw.setText(noRawat);
        isRawat();
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            String q = "select m.tanggal,m.no_rawat,rp.no_rkm_medis,pas.nm_pasien,"
                + "m.peri_hh,m.peri_apd,m.peri_antiseptik,m.peri_suhu_ruang,m.peri_normothermia,"
                + "m.kd_petugas,pg.nama as nm_petugas "
                + "from monitoring_ido_ok m "
                + "inner join reg_periksa rp on m.no_rawat=rp.no_rawat "
                + "inner join pasien pas on rp.no_rkm_medis=pas.no_rkm_medis "
                + "left join petugas pg on m.kd_petugas=pg.nip "
                + "where m.tanggal between ? and ? "
                + (TCari.getText().trim().isEmpty() ? "" : "and (m.no_rawat like ? or pas.nm_pasien like ?) ")
                + "order by m.tanggal desc";
            ps = koneksi.prepareStatement(q);
            ps.setString(1, fmt(DTPCari1.getDate(), "yyyy-MM-dd") + " 00:00:00");
            ps.setString(2, fmt(DTPCari2.getDate(), "yyyy-MM-dd") + " 23:59:59");
            if (!TCari.getText().trim().isEmpty()) { ps.setString(3, "%" + TCari.getText() + "%"); ps.setString(4, "%" + TCari.getText() + "%"); }
            rs = ps.executeQuery();
            while (rs.next()) {
                int ya = 0, applicable = 0;
                String[] ind = {"peri_hh","peri_apd","peri_antiseptik","peri_suhu_ruang","peri_normothermia"};
                for (String c : ind) {
                    String val = rs.getString(c);
                    if (val == null || val.equalsIgnoreCase("NA")) continue;
                    applicable++;
                    if (val.equalsIgnoreCase("Ya")) ya++;
                }
                String pct = applicable == 0 ? "-" : Math.round(((double) ya / applicable) * 100) + " %";
                tabMode.addRow(new String[]{
                    rs.getString("tanggal"), rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                    nz(rs.getString("peri_hh")), nz(rs.getString("peri_apd")), nz(rs.getString("peri_antiseptik")),
                    nz(rs.getString("peri_suhu_ruang")), nz(rs.getString("peri_normothermia")),
                    pct, nz(rs.getString("nm_petugas")), nz(rs.getString("kd_petugas"))
                });
            }
        } catch (Exception e) { System.out.println("Notif tampil : " + e);
        } finally { try { if (rs != null) rs.close(); } catch (Exception e) {} try { if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    private static String nz(String s) { return s == null ? "" : s; }

    private void getData() {
        int r = tbObat.getSelectedRow();
        if (r == -1) return;
        Tanggal.setDate(parseDate(v(0)));
        TNoRw.setText(v(1)); TNoRM.setText(v(2)); TPasien.setText(v(3));
        setCb(cHH, v(4)); setCb(cAPD, v(5)); setCb(cAntiseptik, v(6)); setCb(cSuhu, v(7)); setCb(cNormo, v(8));
        NmPetugas.setText(v(10)); KdPetugas.setText(v(11));
    }

    private static java.util.Date parseDate(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        for (String pat : new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"}) {
            try { return new java.text.SimpleDateFormat(pat).parse(s); } catch (Exception e) {}
        }
        return null;
    }

    private String v(int c) { int r = tbObat.getSelectedRow(); Object o = (r == -1) ? null : tbObat.getValueAt(r, c); return o == null ? "" : o.toString(); }
    private void setCb(widget.ComboBox cb, String val) { if (val != null && !val.isEmpty()) cb.setSelectedItem(val); }

    public void emptTeks() {
        TNoRw.setText(""); TNoRM.setText(""); TPasien.setText("");
        KdPetugas.setText(akses.getkode());
        Sequel.cariIsi("select nama from petugas where nip=?", NmPetugas, akses.getkode());
        for (widget.ComboBox cb : new widget.ComboBox[]{cHH, cAPD, cAntiseptik, cSuhu, cNormo})
            cb.setSelectedIndex(0);
        if (tbObat.getRowCount() > 0) tbObat.clearSelection();
    }

    /** Dipanggil dari menu (frmUtama). Gate akses pakai izin audit bundle IDO. */
    public void isCek() {
        boolean a = akses.getaudit_bundle_ido();
        BtnSimpan.setEnabled(a); BtnEdit.setEnabled(a); BtnHapus.setEnabled(a); BtnPrint.setEnabled(a);
    }

    private void cetak() {
        if (TNoRw.getText().trim().isEmpty()) { Valid.textKosong(TNoRw, "No.Rawat"); return; }
        try {
            java.util.Map param = new java.util.HashMap();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            Valid.MyReportqry("rptAuditBundleIDOPeriOp.jasper", "report",
                "::[ Audit Bundles IDO - Peri Operatif ]::",
                "select m.no_rawat,m.tanggal,rp.no_rkm_medis,pasien.nm_pasien,pasien.tgl_lahir,"
              + "m.peri_hh,m.peri_apd,m.peri_antiseptik,m.peri_suhu_ruang,m.peri_normothermia,"
              + "petugas.nama as nm_petugas "
              + "from monitoring_ido_ok m "
              + "inner join reg_periksa rp on m.no_rawat=rp.no_rawat "
              + "inner join pasien on rp.no_rkm_medis=pasien.no_rkm_medis "
              + "left join petugas on m.kd_petugas=petugas.nip "
              + "where m.no_rawat='" + TNoRw.getText() + "' order by m.tanggal", param);
        } catch (Exception e) { System.out.println("Notif cetak : " + e); }
    }
}
