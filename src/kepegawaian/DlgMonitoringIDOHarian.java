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
 * Lembar Monitoring Pencegahan Infeksi Daerah Operasi (IDO) Harian (Ruang Rawat) — per pasien.
 * Model HEADER/DETAIL:
 *   PRE OPERATIF  (monitoring_ido_preop)  : diisi SEKALI sebelum operasi, key (no_rawat, tgl_operasi).
 *   POST OPERATIF (monitoring_ido_postop) : diisi TIAP HARI setelah operasi, key (no_rawat, tgl_operasi, tanggal).
 * Satu set tombol CRUD dipakai bersama, menyesuaikan tab yang aktif.
 */
public final class DlgMonitoringIDOHarian extends javax.swing.JDialog {

    private DefaultTableModel tabPre, tabPost;
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
    private final widget.Tanggal TglOperasi = new widget.Tanggal();
    private final widget.Tanggal TglMonitoring = new widget.Tanggal();
    private final widget.Tanggal DTPCari1 = new widget.Tanggal();
    private final widget.Tanggal DTPCari2 = new widget.Tanggal();
    private final widget.ComboBox pMandi = combo();
    private final widget.ComboBox pHH = combo();
    private final widget.ComboBox pCukur = combo();
    private final widget.ComboBox pSuhu = combo();
    private final widget.ComboBox pGula = combo();
    private final widget.ComboBox pAntibiotik = combo();
    private final widget.ComboBox qPerawatan = combo();
    private final widget.ComboBox qHH = combo();
    private final widget.ComboBox qPenutupan = combo();
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

    /** true = tab Post Operatif aktif, false = tab Pre Operatif aktif. */
    private boolean isPost() { return tabInput.getSelectedIndex() == 1; }

    public DlgMonitoringIDOHarian(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Lembar Monitoring Infeksi Daerah Operasi (IDO) Harian");
        buildUI();
        setSize(1080, 720);
        setLocationRelativeTo(parent);

        tabPre = new DefaultTableModel(null, new Object[]{
            "Tgl Operasi", "No.Rawat", "No.RM", "Pasien",
            "Pre-Mandi", "Pre-HH", "Pre-Cukur", "Pre-Suhu", "Pre-Gula", "Pre-Antibiotik",
            "Patuh(%)", "Petugas", "Kd.Petugas"
        }) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tabPost = new DefaultTableModel(null, new Object[]{
            "Tgl Monitoring", "Tgl Operasi", "No.Rawat", "No.RM", "Pasien",
            "Post-Perawatan", "Post-HH", "Post-Penutupan",
            "Patuh(%)", "Petugas", "Kd.Petugas"
        }) { @Override public boolean isCellEditable(int r, int c) { return false; } };

        tbObat.setModel(tabPre);
        tbObat.setPreferredScrollableViewportSize(new Dimension(1020, 360));
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
        tabInput.addChangeListener(e -> { tbObat.setModel(isPost() ? tabPost : tabPre); setLebarKolom(); tampil(); });
        tampil();
    }

    private void setLebarKolom() {
        int[] w = isPost()
            ? new int[]{120, 80, 110, 80, 150, 100, 50, 100, 60, 130, 0}
            : new int[]{80, 110, 80, 150, 70, 50, 70, 60, 55, 85, 60, 130, 0};
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
        top.setBorder(BorderFactory.createTitledBorder("Data Pasien & Bundle IDO (Ya / Tidak / NA)"));

        TNoRw.setPreferredSize(new Dimension(150, 23));
        TNoRM.setPreferredSize(new Dimension(110, 23)); TNoRM.setEditable(false);
        TPasien.setPreferredSize(new Dimension(220, 23)); TPasien.setEditable(false);
        btnPasien.setText("Cari Pasien"); btnPasien.addActionListener(e -> pasien.setVisible(true));
        top.add(row("No.Rawat :", TNoRw, btnPasien, new JLabel(" No.RM:"), TNoRM, new JLabel(" Pasien:"), TPasien));

        TglOperasi.setPreferredSize(new Dimension(170, 23));
        top.add(row("Tgl Operasi :", TglOperasi,
                new JLabel("  (kunci data Pre Operatif; dipakai juga oleh Post Operatif)")));

        tabInput.setFont(new java.awt.Font("Tahoma", 0, 11));
        tabInput.setBackground(new java.awt.Color(255, 255, 254));

        JPanel tabPreP = new JPanel();
        tabPreP.setBackground(new java.awt.Color(255, 255, 254));
        tabPreP.setLayout(new BoxLayout(tabPreP, BoxLayout.Y_AXIS));
        tabPreP.add(indRow("1. Mandi pre operatif dengan sabun antiseptik pagi/malam hari", pMandi));
        tabPreP.add(indRow("2. Petugas melakukan kebersihan tangan", pHH));
        tabPreP.add(indRow("3. Tidak dilakukan pencukuran rambut, kecuali mengganggu area operasi", pCukur));
        tabPreP.add(indRow("4. Suhu tubuh pasien < 38°C", pSuhu));
        tabPreP.add(indRow("5. Kadar gula darah < 200 mg/dl", pGula));
        tabPreP.add(indRow("6. Pemberian antibiotik profilaksis 1 jam sebelum insisi", pAntibiotik));

        JPanel tabPostP = new JPanel();
        tabPostP.setBackground(new java.awt.Color(255, 255, 254));
        tabPostP.setLayout(new BoxLayout(tabPostP, BoxLayout.Y_AXIS));
        TglMonitoring.setPreferredSize(new Dimension(170, 23));
        tabPostP.add(row("Tgl Monitoring :", TglMonitoring, new JLabel("  (tanggal harian)")));
        tabPostP.add(indRow("1. Perawatan luka dilakukan dengan teknik steril/aseptik", qPerawatan));
        tabPostP.add(indRow("2. Petugas melakukan kebersihan tangan", qHH));
        tabPostP.add(indRow("3. Penutupan luka dilakukan selama 48 jam pasca operasi (kecuali rembes/kotor)", qPenutupan));

        tabInput.addTab("  Pre Operatif (Sebelum Operasi)  ", tabPreP);
        tabInput.addTab("  Post Operatif (Isi tiap hari)  ", tabPostP);
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
        if (TglOperasi.getDate() == null) { JOptionPane.showMessageDialog(null, "Isi Tgl Operasi dulu."); return; }
        String tglOp = fmt(TglOperasi.getDate(), "yyyy-MM-dd");
        boolean ok;
        if (isPost()) {
            if (TglMonitoring.getDate() == null) { JOptionPane.showMessageDialog(null, "Isi Tgl Monitoring (harian) dulu."); return; }
            String tgl = fmt(TglMonitoring.getDate(), "yyyy-MM-dd") + " " + fmt(new java.util.Date(), "HH:mm:ss");
            ok = Sequel.queryu2tf("replace into monitoring_ido_postop "
                + "(no_rawat,tgl_operasi,tanggal,post_perawatan,post_hh,post_penutupan,kd_petugas) "
                + "values(?,?,?,?,?,?,?)", 7, new String[]{
                    TNoRw.getText(), tglOp, tgl,
                    qPerawatan.getSelectedItem().toString(), qHH.getSelectedItem().toString(),
                    qPenutupan.getSelectedItem().toString(), KdPetugas.getText()});
        } else {
            String waktu = fmt(new java.util.Date(), "yyyy-MM-dd HH:mm:ss");
            ok = Sequel.queryu2tf("replace into monitoring_ido_preop "
                + "(no_rawat,tgl_operasi,waktu_input,pre_mandi,pre_hh,pre_cukur,pre_suhu,pre_gula,pre_antibiotik,kd_petugas) "
                + "values(?,?,?,?,?,?,?,?,?,?)", 10, new String[]{
                    TNoRw.getText(), tglOp, waktu,
                    pMandi.getSelectedItem().toString(), pHH.getSelectedItem().toString(), pCukur.getSelectedItem().toString(),
                    pSuhu.getSelectedItem().toString(), pGula.getSelectedItem().toString(), pAntibiotik.getSelectedItem().toString(),
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
        if (isPost()) {
            ok = Sequel.queryu2tf("delete from monitoring_ido_postop where no_rawat=? and tgl_operasi=? and tanggal=?",
                3, new String[]{v(2), v(1), v(0)});
        } else {
            ok = Sequel.queryu2tf("delete from monitoring_ido_preop where no_rawat=? and tgl_operasi=?",
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
        if (isPost()) tampilPost(); else tampilPre();
    }

    private void tampilPre() {
        Valid.tabelKosong(tabPre);
        try {
            String q = "select m.tgl_operasi,m.no_rawat,rp.no_rkm_medis,pas.nm_pasien,"
                + "m.pre_mandi,m.pre_hh,m.pre_cukur,m.pre_suhu,m.pre_gula,m.pre_antibiotik,"
                + "m.kd_petugas,pg.nama as nm_petugas "
                + "from monitoring_ido_preop m "
                + "inner join reg_periksa rp on m.no_rawat=rp.no_rawat "
                + "inner join pasien pas on rp.no_rkm_medis=pas.no_rkm_medis "
                + "left join petugas pg on m.kd_petugas=pg.nip "
                + "where m.tgl_operasi between ? and ? "
                + (TCari.getText().trim().isEmpty() ? "" : "and (m.no_rawat like ? or pas.nm_pasien like ?) ")
                + "order by m.tgl_operasi desc";
            ps = koneksi.prepareStatement(q);
            ps.setString(1, fmt(DTPCari1.getDate(), "yyyy-MM-dd"));
            ps.setString(2, fmt(DTPCari2.getDate(), "yyyy-MM-dd"));
            if (!TCari.getText().trim().isEmpty()) { ps.setString(3, "%" + TCari.getText() + "%"); ps.setString(4, "%" + TCari.getText() + "%"); }
            rs = ps.executeQuery();
            while (rs.next()) {
                String pct = patuh(rs, new String[]{"pre_mandi","pre_hh","pre_cukur","pre_suhu","pre_gula","pre_antibiotik"});
                tabPre.addRow(new String[]{
                    nz(rs.getString("tgl_operasi")), rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                    nz(rs.getString("pre_mandi")), nz(rs.getString("pre_hh")), nz(rs.getString("pre_cukur")),
                    nz(rs.getString("pre_suhu")), nz(rs.getString("pre_gula")), nz(rs.getString("pre_antibiotik")),
                    pct, nz(rs.getString("nm_petugas")), nz(rs.getString("kd_petugas"))
                });
            }
        } catch (Exception e) { System.out.println("Notif tampilPre : " + e);
        } finally { try { if (rs != null) rs.close(); } catch (Exception e) {} try { if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    private void tampilPost() {
        Valid.tabelKosong(tabPost);
        try {
            String q = "select m.tanggal,m.tgl_operasi,m.no_rawat,rp.no_rkm_medis,pas.nm_pasien,"
                + "m.post_perawatan,m.post_hh,m.post_penutupan,"
                + "m.kd_petugas,pg.nama as nm_petugas "
                + "from monitoring_ido_postop m "
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
                String pct = patuh(rs, new String[]{"post_perawatan","post_hh","post_penutupan"});
                tabPost.addRow(new String[]{
                    nz(rs.getString("tanggal")), nz(rs.getString("tgl_operasi")), rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                    nz(rs.getString("post_perawatan")), nz(rs.getString("post_hh")), nz(rs.getString("post_penutupan")),
                    pct, nz(rs.getString("nm_petugas")), nz(rs.getString("kd_petugas"))
                });
            }
        } catch (Exception e) { System.out.println("Notif tampilPost : " + e);
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
        if (isPost()) {
            // 0 Tgl Monitoring,1 Tgl Operasi,2 No.Rawat,3 No.RM,4 Pasien,5-7 ind,9 nm,10 kd
            TglMonitoring.setDate(parseDate(v(0)));
            TglOperasi.setDate(parseDate(v(1)));
            TNoRw.setText(v(2)); TNoRM.setText(v(3)); TPasien.setText(v(4));
            setCb(qPerawatan, v(5)); setCb(qHH, v(6)); setCb(qPenutupan, v(7));
            NmPetugas.setText(v(9)); KdPetugas.setText(v(10));
        } else {
            // 0 Tgl Operasi,1 No.Rawat,2 No.RM,3 Pasien,4-9 ind,11 nm,12 kd
            TglOperasi.setDate(parseDate(v(0)));
            TNoRw.setText(v(1)); TNoRM.setText(v(2)); TPasien.setText(v(3));
            setCb(pMandi, v(4)); setCb(pHH, v(5)); setCb(pCukur, v(6));
            setCb(pSuhu, v(7)); setCb(pGula, v(8)); setCb(pAntibiotik, v(9));
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
        for (widget.ComboBox cb : new widget.ComboBox[]{pMandi, pHH, pCukur, pSuhu, pGula, pAntibiotik, qPerawatan, qHH, qPenutupan})
            cb.setSelectedIndex(0);
        if (tbObat.getRowCount() > 0) tbObat.clearSelection();
    }

    /** Dipanggil dari menu (frmUtama). Gate akses pakai izin audit bundle IDO. */
    public void isCek() {
        boolean a = akses.getaudit_bundle_ido();
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
            Valid.MyReportqry("rptMonitoringIDOHarian.jasper", "report",
                "::[ Monitoring Infeksi Daerah Operasi (IDO) Harian ]::",
                "select i.no_rawat,i.tgl_operasi,rp.no_rkm_medis,pasien.nm_pasien,pasien.tgl_lahir,"
              + "i.pre_mandi,i.pre_hh,i.pre_cukur,i.pre_suhu,i.pre_gula,i.pre_antibiotik,"
              + "d.tanggal,d.post_perawatan,d.post_hh,d.post_penutupan,"
              + "pg.nama as nm_petugas "
              + "from monitoring_ido_preop i "
              + "inner join reg_periksa rp on i.no_rawat=rp.no_rawat "
              + "inner join pasien on rp.no_rkm_medis=pasien.no_rkm_medis "
              + "left join monitoring_ido_postop d on d.no_rawat=i.no_rawat and d.tgl_operasi=i.tgl_operasi "
              + "left join petugas pg on d.kd_petugas=pg.nip "
              + "where i.no_rawat='" + TNoRw.getText() + "' order by i.tgl_operasi, d.tanggal", param);
        } catch (Exception e) { System.out.println("Notif cetak : " + e); }
    }
}
