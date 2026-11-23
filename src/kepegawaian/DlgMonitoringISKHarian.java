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
 * Lembar Monitoring Pencegahan Infeksi Saluran Kemih (ISK) Harian — per pasien.
 * Model HEADER/DETAIL:
 *   INSERSI  (monitoring_isk_insersi)     : diisi SEKALI saat pemasangan, key (no_rawat, tgl_pemasangan).
 *   MAINTENANCE (monitoring_isk_maintenance): diisi TIAP HARI, key (no_rawat, tgl_pemasangan, tanggal).
 * Satu set tombol CRUD dipakai bersama, menyesuaikan tab yang aktif.
 */
public final class DlgMonitoringISKHarian extends javax.swing.JDialog {

    private DefaultTableModel tabIns, tabMnt;
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
    private final widget.Tanggal TglPemasangan = new widget.Tanggal();
    private final widget.Tanggal TglMonitoring = new widget.Tanggal();
    private final widget.Tanggal DTPCari1 = new widget.Tanggal();
    private final widget.Tanggal DTPCari2 = new widget.Tanggal();
    private final widget.ComboBox iIndikasi = combo();
    private final widget.ComboBox iAPD = combo();
    private final widget.ComboBox iHH = combo();
    private final widget.ComboBox iSteril = combo();
    private final widget.ComboBox iBalon = combo();
    private final widget.ComboBox iFiksasi = combo();
    private final widget.ComboBox mHH = combo();
    private final widget.ComboBox mPemeliharaan = combo();
    private final widget.ComboBox mPelepasan = combo();
    private final widget.Table tbObat = new widget.Table();
    private final javax.swing.JTabbedPane tabInput = new javax.swing.JTabbedPane();
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

    /** true = tab Maintenance aktif, false = tab Insersi aktif. */
    private boolean isMaint() { return tabInput.getSelectedIndex() == 1; }

    public DlgMonitoringISKHarian(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Lembar Monitoring Infeksi Saluran Kemih (ISK) Harian");
        buildUI();
        setSize(1060, 720);
        setLocationRelativeTo(parent);

        tabIns = new DefaultTableModel(null, new Object[]{
            "Tgl Pasang", "No.Rawat", "No.RM", "Pasien",
            "I-Indikasi", "I-APD", "I-HH", "I-Steril", "I-Balon", "I-Fiksasi",
            "Patuh(%)", "Petugas", "Kd.Petugas"
        }) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tabMnt = new DefaultTableModel(null, new Object[]{
            "Tgl Monitoring", "Tgl Pasang", "No.Rawat", "No.RM", "Pasien",
            "M-HH", "M-Pemeliharaan", "M-Pelepasan",
            "Patuh(%)", "Petugas", "Kd.Petugas"
        }) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        tbObat.setModel(tabIns);
        tbObat.setPreferredScrollableViewportSize(new Dimension(1000, 360));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) { getData(); }
        });
        setLebarKolom();

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
        tabInput.addChangeListener(e -> { tbObat.setModel(isMaint() ? tabMnt : tabIns); setLebarKolom(); tampil(); });
        tampil();
    }

    private void setLebarKolom() {
        int[] w = isMaint()
            ? new int[]{120, 80, 110, 80, 150, 50, 110, 90, 60, 130, 0}
            : new int[]{80, 110, 80, 150, 70, 50, 50, 55, 55, 60, 60, 130, 0};
        for (i = 0; i < w.length && i < tbObat.getColumnModel().getColumnCount(); i++) {
            tbObat.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        }
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
        l.setPreferredSize(new Dimension(560, 23));
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
        top.setBorder(BorderFactory.createTitledBorder("Data Pasien & Bundle ISK (Ya / Tidak / NA)"));

        TNoRw.setPreferredSize(new Dimension(150, 23));
        TNoRM.setPreferredSize(new Dimension(110, 23)); TNoRM.setEditable(false);
        TPasien.setPreferredSize(new Dimension(220, 23)); TPasien.setEditable(false);
        btnPasien.setText("Cari Pasien"); btnPasien.addActionListener(e -> pasien.setVisible(true));
        top.add(row("No.Rawat :", TNoRw, btnPasien, new JLabel(" No.RM:"), TNoRM, new JLabel(" Pasien:"), TPasien));

        TglPemasangan.setPreferredSize(new Dimension(170, 23));
        top.add(row("Tgl Pemasangan Kateter :", TglPemasangan,
                new JLabel("  (kunci data Insersi; dipakai juga oleh Maintenance)")));

        tabInput.setFont(new java.awt.Font("Tahoma", 0, 11));
        tabInput.setBackground(new java.awt.Color(255, 255, 254));

        JPanel tabInsP = new JPanel();
        tabInsP.setBackground(new java.awt.Color(255, 255, 254));
        tabInsP.setLayout(new BoxLayout(tabInsP, BoxLayout.Y_AXIS));
        tabInsP.add(indRow("1. Pemasangan sesuai indikasi", iIndikasi));
        tabInsP.add(indRow("2. Petugas menggunakan APD sesuai indikasi dan jenis paparan", iAPD));
        tabInsP.add(indRow("3. Petugas melakukan kebersihan tangan", iHH));
        tabInsP.add(indRow("4. Pemasangan menggunakan teknik steril", iSteril));
        tabInsP.add(indRow("5. Pengisian balon sesuai 10ml menggunakan air steril/aquabidest", iBalon));
        tabInsP.add(indRow("6. Fiksasi kateter dengan plester", iFiksasi));

        JPanel tabMntP = new JPanel();
        tabMntP.setBackground(new java.awt.Color(255, 255, 254));
        tabMntP.setLayout(new BoxLayout(tabMntP, BoxLayout.Y_AXIS));
        TglMonitoring.setPreferredSize(new Dimension(170, 23));
        tabMntP.add(row("Tgl Monitoring :", TglMonitoring, new JLabel("  (tanggal harian)")));
        tabMntP.add(indRow("1. Petugas melakukan kebersihan tangan", mHH));
        tabMntP.add(indRow("2. Pemeliharaan kateter dilakukan secara rutin (pengosongan & kebersihan urine bag)", mPemeliharaan));
        tabMntP.add(indRow("3. Pelepasan kateter dilakukan segera jika tidak ada indikasi", mPelepasan));

        tabInput.addTab("  Insersi (Saat Pemasangan)  ", tabInsP);
        tabInput.addTab("  Maintenance (Isi tiap hari)  ", tabMntP);
        top.add(tabInput);

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

    // ----------------------------------------------------------------- SIMPAN
    private void simpan() {
        if (TNoRw.getText().trim().isEmpty()) { Valid.textKosong(TNoRw, "No.Rawat"); return; }
        if (KdPetugas.getText().trim().isEmpty()) { Valid.textKosong(KdPetugas, "Petugas"); return; }
        if (TglPemasangan.getDate() == null) { JOptionPane.showMessageDialog(null, "Isi Tgl Pemasangan Kateter dulu."); return; }
        String tglPsg = fmt(TglPemasangan.getDate(), "yyyy-MM-dd");
        boolean ok;
        if (isMaint()) {
            if (TglMonitoring.getDate() == null) { JOptionPane.showMessageDialog(null, "Isi Tgl Monitoring (harian) dulu."); return; }
            String tgl = fmt(TglMonitoring.getDate(), "yyyy-MM-dd") + " " + fmt(new java.util.Date(), "HH:mm:ss");
            ok = Sequel.queryu2tf("replace into monitoring_isk_maintenance "
                + "(no_rawat,tgl_pemasangan,tanggal,main_hh,main_pemeliharaan,main_pelepasan,kd_petugas) "
                + "values(?,?,?,?,?,?,?)", 7, new String[]{
                    TNoRw.getText(), tglPsg, tgl,
                    mHH.getSelectedItem().toString(), mPemeliharaan.getSelectedItem().toString(),
                    mPelepasan.getSelectedItem().toString(), KdPetugas.getText()});
        } else {
            String waktu = fmt(new java.util.Date(), "yyyy-MM-dd HH:mm:ss");
            ok = Sequel.queryu2tf("replace into monitoring_isk_insersi "
                + "(no_rawat,tgl_pemasangan,waktu_input,ins_indikasi,ins_apd,ins_hh,ins_steril,ins_balon,ins_fiksasi,kd_petugas) "
                + "values(?,?,?,?,?,?,?,?,?,?)", 10, new String[]{
                    TNoRw.getText(), tglPsg, waktu,
                    iIndikasi.getSelectedItem().toString(), iAPD.getSelectedItem().toString(), iHH.getSelectedItem().toString(),
                    iSteril.getSelectedItem().toString(), iBalon.getSelectedItem().toString(), iFiksasi.getSelectedItem().toString(),
                    KdPetugas.getText()});
        }
        if (ok) { tampil(); emptTeks(); }
        else JOptionPane.showMessageDialog(null, "Gagal menyimpan..!!");
    }

    // ------------------------------------------------------------------- EDIT
    private void edit() {
        if (tbObat.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih baris yang mau diubah."); return; }
        simpan();
    }

    // ------------------------------------------------------------------ HAPUS
    private void hapus() {
        int r = tbObat.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(null, "Pilih baris yang mau dihapus."); return; }
        boolean ok;
        if (isMaint()) {
            // kolom: 0 Tgl Monitoring, 1 Tgl Pasang, 2 No.Rawat
            ok = Sequel.queryu2tf("delete from monitoring_isk_maintenance where no_rawat=? and tgl_pemasangan=? and tanggal=?",
                3, new String[]{v(2), v(1), v(0)});
        } else {
            // kolom: 0 Tgl Pasang, 1 No.Rawat
            ok = Sequel.queryu2tf("delete from monitoring_isk_insersi where no_rawat=? and tgl_pemasangan=?",
                2, new String[]{v(1), v(0)});
        }
        if (ok) { tampil(); emptTeks(); }
        else JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
    }

    /** Dipanggil DlgKamarInap: set pasien terpilih (no_rawat) lalu lookup data pasien. */
    public void setNoRm(String noRawat, java.util.Date tgl) {
        TNoRw.setText(noRawat);
        isRawat();
    }

    // ----------------------------------------------------------------- TAMPIL
    public void tampil() {
        if (isMaint()) tampilMaint(); else tampilInsersi();
    }

    private void tampilInsersi() {
        Valid.tabelKosong(tabIns);
        try {
            String q = "select m.tgl_pemasangan,m.no_rawat,rp.no_rkm_medis,pas.nm_pasien,"
                + "m.ins_indikasi,m.ins_apd,m.ins_hh,m.ins_steril,m.ins_balon,m.ins_fiksasi,"
                + "m.kd_petugas,pg.nama as nm_petugas "
                + "from monitoring_isk_insersi m "
                + "inner join reg_periksa rp on m.no_rawat=rp.no_rawat "
                + "inner join pasien pas on rp.no_rkm_medis=pas.no_rkm_medis "
                + "left join petugas pg on m.kd_petugas=pg.nip "
                + "where m.tgl_pemasangan between ? and ? "
                + (TCari.getText().trim().isEmpty() ? "" : "and (m.no_rawat like ? or pas.nm_pasien like ?) ")
                + "order by m.tgl_pemasangan desc";
            ps = koneksi.prepareStatement(q);
            ps.setString(1, fmt(DTPCari1.getDate(), "yyyy-MM-dd"));
            ps.setString(2, fmt(DTPCari2.getDate(), "yyyy-MM-dd"));
            if (!TCari.getText().trim().isEmpty()) { ps.setString(3, "%" + TCari.getText() + "%"); ps.setString(4, "%" + TCari.getText() + "%"); }
            rs = ps.executeQuery();
            while (rs.next()) {
                String pct = patuh(rs, new String[]{"ins_indikasi","ins_apd","ins_hh","ins_steril","ins_balon","ins_fiksasi"});
                tabIns.addRow(new String[]{
                    nz(rs.getString("tgl_pemasangan")), rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                    nz(rs.getString("ins_indikasi")), nz(rs.getString("ins_apd")), nz(rs.getString("ins_hh")),
                    nz(rs.getString("ins_steril")), nz(rs.getString("ins_balon")), nz(rs.getString("ins_fiksasi")),
                    pct, nz(rs.getString("nm_petugas")), nz(rs.getString("kd_petugas"))
                });
            }
        } catch (Exception e) { System.out.println("Notif tampilInsersi : " + e);
        } finally { try { if (rs != null) rs.close(); } catch (Exception e) {} try { if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    private void tampilMaint() {
        Valid.tabelKosong(tabMnt);
        try {
            String q = "select m.tanggal,m.tgl_pemasangan,m.no_rawat,rp.no_rkm_medis,pas.nm_pasien,"
                + "m.main_hh,m.main_pemeliharaan,m.main_pelepasan,"
                + "m.kd_petugas,pg.nama as nm_petugas "
                + "from monitoring_isk_maintenance m "
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
                String pct = patuh(rs, new String[]{"main_hh","main_pemeliharaan","main_pelepasan"});
                tabMnt.addRow(new String[]{
                    nz(rs.getString("tanggal")), nz(rs.getString("tgl_pemasangan")), rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                    nz(rs.getString("main_hh")), nz(rs.getString("main_pemeliharaan")), nz(rs.getString("main_pelepasan")),
                    pct, nz(rs.getString("nm_petugas")), nz(rs.getString("kd_petugas"))
                });
            }
        } catch (Exception e) { System.out.println("Notif tampilMaint : " + e);
        } finally { try { if (rs != null) rs.close(); } catch (Exception e) {} try { if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    private static String patuh(ResultSet rs, String[] cols) throws Exception {
        int ya = 0, applicable = 0;
        for (String c : cols) {
            String v = rs.getString(c);
            if (v == null || v.equalsIgnoreCase("NA")) continue;
            applicable++;
            if (v.equalsIgnoreCase("Ya")) ya++;
        }
        return applicable == 0 ? "-" : Math.round(((double) ya / applicable) * 100) + " %";
    }

    private static String nz(String s) { return s == null ? "" : s; }

    // --------------------------------------------------------- GETDATA (klik)
    private void getData() {
        int r = tbObat.getSelectedRow();
        if (r == -1) return;
        if (isMaint()) {
            // 0 Tgl Monitoring, 1 Tgl Pasang, 2 No.Rawat, 3 No.RM, 4 Pasien, 5-7 ind, 9 nm, 10 kd
            TglMonitoring.setDate(parseDate(v(0)));
            TglPemasangan.setDate(parseDate(v(1)));
            TNoRw.setText(v(2)); TNoRM.setText(v(3)); TPasien.setText(v(4));
            setCb(mHH, v(5)); setCb(mPemeliharaan, v(6)); setCb(mPelepasan, v(7));
            NmPetugas.setText(v(9)); KdPetugas.setText(v(10));
        } else {
            // 0 Tgl Pasang, 1 No.Rawat, 2 No.RM, 3 Pasien, 4-9 ind, 11 nm, 12 kd
            TglPemasangan.setDate(parseDate(v(0)));
            TNoRw.setText(v(1)); TNoRM.setText(v(2)); TPasien.setText(v(3));
            setCb(iIndikasi, v(4)); setCb(iAPD, v(5)); setCb(iHH, v(6));
            setCb(iSteril, v(7)); setCb(iBalon, v(8)); setCb(iFiksasi, v(9));
            NmPetugas.setText(v(11)); KdPetugas.setText(v(12));
        }
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
        // Petugas default = user yang login (boleh diganti via tombol Cari Petugas)
        KdPetugas.setText(akses.getkode());
        Sequel.cariIsi("select nama from petugas where nip=?", NmPetugas, akses.getkode());
        for (widget.ComboBox cb : new widget.ComboBox[]{iIndikasi, iAPD, iHH, iSteril, iBalon, iFiksasi, mHH, mPemeliharaan, mPelepasan})
            cb.setSelectedIndex(0);
        if (tbObat.getRowCount() > 0) tbObat.clearSelection();
    }

    /** Dipanggil dari menu (frmUtama). Gate akses pakai izin audit bundle ISK. */
    public void isCek() {
        boolean a = akses.getaudit_bundle_isk();
        BtnSimpan.setEnabled(a); BtnEdit.setEnabled(a); BtnHapus.setEnabled(a); BtnPrint.setEnabled(a);
    }

    // ------------------------------------------------------------------ CETAK
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
            // Cetak lembar harian: header insersi + baris maintenance tiap hari (per pemasangan).
            Valid.MyReportqry("rptMonitoringISKHarian.jasper", "report",
                "::[ Monitoring Infeksi Saluran Kemih (ISK) Harian ]::",
                "select i.no_rawat,i.tgl_pemasangan,rp.no_rkm_medis,pasien.nm_pasien,pasien.tgl_lahir,"
              + "i.ins_indikasi,i.ins_apd,i.ins_hh,i.ins_steril,i.ins_balon,i.ins_fiksasi,"
              + "d.tanggal,d.main_hh,d.main_pemeliharaan,d.main_pelepasan,"
              + "pg.nama as nm_petugas "
              + "from monitoring_isk_insersi i "
              + "inner join reg_periksa rp on i.no_rawat=rp.no_rawat "
              + "inner join pasien on rp.no_rkm_medis=pasien.no_rkm_medis "
              + "left join monitoring_isk_maintenance d on d.no_rawat=i.no_rawat and d.tgl_pemasangan=i.tgl_pemasangan "
              + "left join petugas pg on d.kd_petugas=pg.nip "
              + "where i.no_rawat='" + TNoRw.getText() + "' order by i.tgl_pemasangan, d.tanggal", param);
        } catch (Exception e) { System.out.println("Notif cetak : " + e); }
    }
}
