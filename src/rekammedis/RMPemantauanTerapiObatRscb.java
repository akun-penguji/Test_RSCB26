package rekammedis;

import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import kepegawaian.DlgCariPetugas;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import widget.*;

/**
 * Pemantauan Terapi Obat (PTO) — formulir apoteker sesuai form fisik RM:
 * deskripsi pasien, keluhan utama, riwayat penyakit sekarang/terdahulu/keluarga/
 * sosial, riwayat penggunaan obat, hasil pemeriksaan penunjang, penggunaan obat
 * saat ini, dan SOAP.
 *
 * Memakai tabel YANG SAMA dengan e-Farmasi (web) supaya data desktop &amp; HP
 * saling nyambung:
 *   pemantauan_terapi_obat_rscb  (1 baris = 1 lembar, PK no_rawat+tanggal)
 *   pto_riwayat_obat_rscb / pto_penunjang_rscb / pto_obat_saat_ini_rscb (detail)
 *
 * Tombol "Tarik Data Pasien / Resep / Hasil Lab" mengambil data yang sudah ada
 * di Khanza supaya apoteker tidak mengetik ulang (sama dengan /pto/awal di web).
 */
public class RMPemantauanTerapiObatRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnPrint, BtnKeluar, BtnCari, BtnAll, BtnPetugas, BtnTarik;
    private widget.Tanggal DTPCari1, DTPCari2, TglPantau, TglMasuk;
    private TextBox TNoRw, TNoRM, TPasien, TCari, TRuangan, TDpjp, TBb, TTb, TAlergi, NIP, NamaPetugas;
    private widget.TextArea TaKeluhan, TaRwSekarang, TaRwTerdahulu, TaRwKeluarga, TaRwSosial, TaS, TaO, TaA, TaP;
    private Label LCount;
    private TabPane TabForm;

    private widget.Table tbData, tbRiwayat, tbPenunjang, tbObat;
    private DefaultTableModel tabMode, tabRiwayat, tabPenunjang, tabObat;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);
    /** Tanggal baris yang sedang diedit; kosong = lembar baru. */
    private String origTanggal = "";

    public RMPemantauanTerapiObatRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(1000, 740);
        setLocationRelativeTo(null);
    }

    // ── Pembantu tata letak ──────────────────────────────────────────────────
    private Label mkLbl(String t) { Label l = new Label(); l.setText(t); return l; }
    private Button mkBtn(String t, char m, String ic, int w) {
        Button b = new Button(); b.setIcon(new ImageIcon(getClass().getResource(ic)));
        b.setMnemonic(m); b.setText(t); b.setPreferredSize(new Dimension(w, 30)); return b;
    }
    private ScrollPane wrap(PanelBiasa p, int h) {
        p.setPreferredSize(new Dimension(950, h)); p.setLayout(null);
        ScrollPane s = new ScrollPane(); s.setViewportView(p); return s;
    }
    /** TextArea berlabel di panel absolut; mengembalikan y berikutnya. */
    private int addArea(PanelBiasa p, int y, String label, widget.TextArea ta, int h) {
        Label l = mkLbl(label); l.setBounds(10, y, 200, 20); p.add(l);
        ScrollPane sc = new ScrollPane(); sc.setViewportView(ta);
        sc.setBounds(10, y + 20, 920, h); p.add(sc);
        return y + 24 + h;
    }
    private widget.Table mkTable(DefaultTableModel m, int[] lebar) {
        widget.Table t = new widget.Table(); t.setModel(m);
        for (int i = 0; i < lebar.length; i++) t.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        t.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        return t;
    }
    private DefaultTableModel mkModel(String[] kolom) {
        return new DefaultTableModel(new Object[][]{}, kolom) {
            @Override public boolean isCellEditable(int r, int c) { return true; } };
    }
    /** Panel tabel detail + tombol tambah/hapus baris (+ tombol tarik opsional). */
    private JPanel panelTabel(widget.Table tb, DefaultTableModel model, int kolom, Button tarik) {
        JPanel p = new JPanel(new BorderLayout(2, 2));
        ScrollPane sc = new ScrollPane(); sc.setViewportView(tb);
        panelisi bar = new panelisi();
        bar.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        bar.setPreferredSize(new Dimension(900, 36));
        Button add = mkBtn("Tambah Baris", 'T', "/picture/edit.png", 130);
        Button del = mkBtn("Hapus Baris", 'R', "/picture/stop_f2.png", 130);
        add.addActionListener(e -> model.addRow(new String[kolom]));
        del.addActionListener(e -> { if (tb.getSelectedRow() != -1) model.removeRow(tb.getSelectedRow()); });
        bar.add(add); bar.add(del);
        if (tarik != null) bar.add(tarik);
        p.add(bar, BorderLayout.PAGE_START);
        p.add(sc, BorderLayout.CENTER);
        return p;
    }

    private void initComponents() {
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Pemantauan Terapi Obat (PTO) ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // PANEL ATAS — identitas pasien
        panelisi panelAtas = new panelisi();
        panelAtas.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 4));
        panelAtas.setPreferredSize(new Dimension(1024, 33));
        TNoRw = new TextBox(); TNoRw.setPreferredSize(new Dimension(130, 23)); TNoRw.setEditable(false);
        TNoRM = new TextBox(); TNoRM.setPreferredSize(new Dimension(100, 23)); TNoRM.setEditable(false);
        TPasien = new TextBox(); TPasien.setPreferredSize(new Dimension(240, 23)); TPasien.setEditable(false);
        panelAtas.add(mkLbl("No.Rawat :")); panelAtas.add(TNoRw); panelAtas.add(TNoRM); panelAtas.add(TPasien);

        // ── TAB 1 : Deskripsi Pasien & Anamnesis ──
        PanelBiasa p1 = new PanelBiasa(); int y = 12;
        TglPantau = new widget.Tanggal(); TglPantau.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        Label l0 = mkLbl("Tgl Pemantauan :"); l0.setBounds(10, y, 110, 23); p1.add(l0);
        TglPantau.setBounds(123, y, 155, 23); p1.add(TglPantau);
        Label l0b = mkLbl("Ruangan :"); l0b.setBounds(290, y, 70, 23); p1.add(l0b);
        TRuangan = new TextBox(); TRuangan.setBounds(362, y, 180, 23); p1.add(TRuangan);
        BtnTarik = mkBtn("Tarik Data Pasien", 'D', "/picture/reload.png", 160);
        BtnTarik.setBounds(560, y - 3, 170, 28); p1.add(BtnTarik);

        y += 28;
        Label l1 = mkLbl("Tgl Masuk :"); l1.setBounds(10, y, 110, 23); p1.add(l1);
        TglMasuk = new widget.Tanggal(); TglMasuk.setDisplayFormat("dd-MM-yyyy");
        TglMasuk.setBounds(123, y, 155, 23); p1.add(TglMasuk);
        Label l2 = mkLbl("DPJP :"); l2.setBounds(290, y, 70, 23); p1.add(l2);
        TDpjp = new TextBox(); TDpjp.setBounds(362, y, 300, 23); p1.add(TDpjp);

        y += 28;
        Label l3 = mkLbl("BB (kg) :"); l3.setBounds(10, y, 110, 23); p1.add(l3);
        TBb = new TextBox(); TBb.setBounds(123, y, 70, 23); p1.add(TBb);
        Label l4 = mkLbl("TB (cm) :"); l4.setBounds(200, y, 60, 23); p1.add(l4);
        TTb = new TextBox(); TTb.setBounds(262, y, 70, 23); p1.add(TTb);
        Label l5 = mkLbl("Alergi Obat :"); l5.setBounds(345, y, 80, 23); p1.add(l5);
        TAlergi = new TextBox(); TAlergi.setBounds(427, y, 300, 23); p1.add(TAlergi);

        y += 28;
        Label l6 = mkLbl("Apoteker :"); l6.setBounds(10, y, 110, 23); p1.add(l6);
        NIP = new TextBox(); NIP.setBounds(123, y, 90, 23); p1.add(NIP);
        NamaPetugas = new TextBox(); NamaPetugas.setBounds(215, y, 250, 23); NamaPetugas.setEditable(false); p1.add(NamaPetugas);
        BtnPetugas = new Button(); BtnPetugas.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPetugas.setBounds(467, y, 28, 23); p1.add(BtnPetugas);

        y += 32;
        y = addArea(p1, y, "Keluhan Utama", TaKeluhan = new widget.TextArea(), 50);
        y = addArea(p1, y, "Riwayat Penyakit Sekarang", TaRwSekarang = new widget.TextArea(), 70);
        y = addArea(p1, y, "Riwayat Penyakit Terdahulu", TaRwTerdahulu = new widget.TextArea(), 50);
        y = addArea(p1, y, "Riwayat Keluarga", TaRwKeluarga = new widget.TextArea(), 45);
        y = addArea(p1, y, "Riwayat Sosial", TaRwSosial = new widget.TextArea(), 45);
        ScrollPane sc1 = wrap(p1, y + 20);

        // ── TAB 2 : Riwayat Penggunaan Obat ──
        tabRiwayat = mkModel(new String[]{"Nama Obat", "Rejimen Dosis", "Rute", "Waktu Mulai"});
        tbRiwayat = mkTable(tabRiwayat, new int[]{300, 160, 120, 160});
        JPanel p2 = panelTabel(tbRiwayat, tabRiwayat, 4, null);

        // ── TAB 3 : Hasil Pemeriksaan Penunjang ──
        tabPenunjang = mkModel(new String[]{"Tgl Pemeriksaan", "Nama Pemeriksaan", "Hasil", "Satuan", "Nilai Rujukan"});
        tbPenunjang = mkTable(tabPenunjang, new int[]{110, 260, 100, 90, 160});
        Button btnLab = mkBtn("Tarik Hasil Lab", 'L', "/picture/reload.png", 150);
        btnLab.addActionListener(e -> tarikHasilLab());
        JPanel p3 = panelTabel(tbPenunjang, tabPenunjang, 5, btnLab);

        // ── TAB 4 : Penggunaan Obat Saat Ini ──
        tabObat = mkModel(new String[]{"Tgl Mulai Terapi", "Nama Obat", "Rejimen", "Indikasi"});
        tbObat = mkTable(tabObat, new int[]{110, 300, 160, 240});
        Button btnResep = mkBtn("Tarik Resep", 'P', "/picture/reload.png", 130);
        btnResep.addActionListener(e -> tarikResep());
        JPanel p4 = panelTabel(tbObat, tabObat, 4, btnResep);

        // ── TAB 5 : SOAP ──
        PanelBiasa p5 = new PanelBiasa(); y = 12;
        y = addArea(p5, y, "S — Subjektif", TaS = new widget.TextArea(), 55);
        y = addArea(p5, y, "O — Objektif", TaO = new widget.TextArea(), 110);
        y = addArea(p5, y, "A — Assessment (DRP / interaksi obat)", TaA = new widget.TextArea(), 110);
        y = addArea(p5, y, "P — Plan (rencana pemantauan)", TaP = new widget.TextArea(), 90);
        ScrollPane sc5 = wrap(p5, y + 20);

        TabForm = new TabPane();
        TabForm.addTab("Deskripsi & Anamnesis", sc1);
        TabForm.addTab("Riwayat Penggunaan Obat", p2);
        TabForm.addTab("Hasil Penunjang", p3);
        TabForm.addTab("Obat Saat Ini", p4);
        TabForm.addTab("SOAP", sc5);

        // TABEL daftar lembar
        tabMode = new DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat", "No.R.M.", "Nama Pasien", "Tanggal", "Ruangan", "Apoteker", "Keluhan Utama"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbData = new widget.Table(); tbData.setModel(tabMode);
        tbData.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { if (tabMode.getRowCount() != 0) getData(); } });
        ScrollPane scrollData = new ScrollPane(); scrollData.setViewportView(tbData);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, TabForm, scrollData);
        split.setResizeWeight(0.66); split.setDividerLocation(450);

        // PANEL BAWAH
        panelBawah = new panelisi(); panelBawah.setPreferredSize(new Dimension(44, 54));
        panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));
        BtnSimpan = mkBtn("Simpan", 'S', "/picture/save-16x16.png", 95);
        BtnBatal = mkBtn("Baru", 'B', "/picture/Cancel-2-16x16.png", 90);
        BtnHapus = mkBtn("Hapus", 'H', "/picture/stop_f2.png", 90);
        BtnPrint = mkBtn("Cetak", 'C', "/picture/b_print.png", 90);
        BtnKeluar = mkBtn("Keluar", 'K', "/picture/exit.png", 90);
        DTPCari1 = new widget.Tanggal(); DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setPreferredSize(new Dimension(90, 23));
        DTPCari2 = new widget.Tanggal(); DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setPreferredSize(new Dimension(90, 23));
        Label lblTglC = mkLbl("Tanggal :"); lblTglC.setPreferredSize(new Dimension(55, 23));
        Label lblSd = mkLbl("s.d."); lblSd.setPreferredSize(new Dimension(25, 23));
        Label lblKey = mkLbl("Key Word :"); lblKey.setPreferredSize(new Dimension(65, 23));
        TCari = new TextBox(); TCari.setPreferredSize(new Dimension(140, 23));
        BtnCari = new Button(); BtnCari.setIcon(new ImageIcon(getClass().getResource("/picture/accept.png"))); BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnAll = new Button(); BtnAll.setIcon(new ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); BtnAll.setPreferredSize(new Dimension(28, 23));
        LCount = new Label(); LCount.setText("Record : 0"); LCount.setPreferredSize(new Dimension(95, 23));
        panelBawah.add(BtnSimpan); panelBawah.add(BtnBatal); panelBawah.add(BtnHapus);
        panelBawah.add(BtnPrint); panelBawah.add(BtnKeluar);
        panelBawah.add(lblTglC); panelBawah.add(DTPCari1); panelBawah.add(lblSd); panelBawah.add(DTPCari2);
        panelBawah.add(lblKey); panelBawah.add(TCari); panelBawah.add(BtnCari); panelBawah.add(BtnAll); panelBawah.add(LCount);

        BtnKeluar.addActionListener(evt -> dispose());
        BtnBatal.addActionListener(evt -> emptTeks());
        BtnCari.addActionListener(evt -> tampil());
        BtnAll.addActionListener(evt -> { TCari.setText(""); tampil(); });
        BtnSimpan.addActionListener(evt -> simpan());
        BtnHapus.addActionListener(evt -> hapus());
        BtnPrint.addActionListener(evt -> cetak());
        BtnTarik.addActionListener(evt -> tarikDataPasien());
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) { if (evt.getKeyCode() == KeyEvent.VK_ENTER) tampil(); } });
        BtnPetugas.addActionListener(evt -> {
            petugas.emptTeks(); petugas.isCek();
            petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            petugas.setLocationRelativeTo(internalFrame1); petugas.setVisible(true); });
        petugas.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (petugas.getTable().getSelectedRow() == -1) return;
                NIP.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                NamaPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
            } });

        internalFrame1.add(panelAtas, BorderLayout.PAGE_START);
        internalFrame1.add(split, BorderLayout.CENTER);
        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);
        setLayout(new BorderLayout());
        add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
    }

    // ── Pembantu nilai ───────────────────────────────────────────────────────
    private String s(String v) { return v == null ? "" : v; }
    private String cell(DefaultTableModel m, int r, int c) {
        Object o = m.getValueAt(r, c); return o == null ? "" : o.toString().trim();
    }
    private String dtPantau() {
        String v = TglPantau.getSelectedItem().toString();
        return Valid.SetTgl(v) + " " + (v.length() >= 19 ? v.substring(11, 19) : "00:00:00");
    }
    /** "" / format apa pun → yyyy-MM-dd, atau null bila kosong / tak terbaca. */
    private String normTgl(String v) {
        if (v == null || v.trim().isEmpty()) return null;
        String t = v.trim();
        if (t.length() > 10) t = t.substring(0, 10);
        if (t.matches("\\d{4}-\\d{2}-\\d{2}")) return t;
        if (t.matches("\\d{2}-\\d{2}-\\d{4}")) return t.substring(6) + "-" + t.substring(3, 5) + "-" + t.substring(0, 2);
        return null;
    }
    private void setTgl(PreparedStatement p, int i, String v) throws Exception {
        String t = normTgl(v);
        if (t == null) p.setNull(i, Types.DATE); else p.setString(i, t);
    }

    // ── Tarik data yang sudah ada di Khanza ──────────────────────────────────
    private void tarikDataPasien() {
        if (TNoRw.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(null, "Maaf, pilih pasien dulu..!!"); return; }
        String norw = TNoRw.getText();
        // Ruangan & tgl masuk: bangsal terakhir (ranap); kalau ralan pakai poliklinik.
        try {
            ps = koneksi.prepareStatement(
                "select ifnull(b.nm_bangsal,'') as ruangan, cast(ki.tgl_masuk as char) as tgl_masuk " +
                "from kamar_inap ki inner join kamar k on k.kd_kamar=ki.kd_kamar " +
                "left join bangsal b on b.kd_bangsal=k.kd_bangsal where ki.no_rawat=? " +
                "order by ki.tgl_masuk desc, ki.jam_masuk desc limit 1");
            ps.setString(1, norw);
            rs = ps.executeQuery();
            if (rs.next()) {
                TRuangan.setText(s(rs.getString("ruangan")));
                Date d = new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("tgl_masuk"));
                TglMasuk.setDate(d);
            } else {
                TRuangan.setText(s(Sequel.cariIsi(
                    "select ifnull(pk.nm_poli,'') from reg_periksa r left join poliklinik pk on pk.kd_poli=r.kd_poli where r.no_rawat=?", norw)));
                String tglReg = Sequel.cariIsi("select cast(tgl_registrasi as char) from reg_periksa where no_rawat=?", norw);
                if (tglReg != null && tglReg.length() >= 10) TglMasuk.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(tglReg.substring(0, 10)));
            }
        } catch (Exception e) { System.out.println("Notif tarik ruangan PTO : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }

        TDpjp.setText(s(Sequel.cariIsi(
            "select ifnull(d.nm_dokter,'') from reg_periksa r left join dokter d on d.kd_dokter=r.kd_dokter where r.no_rawat=?", norw)));

        // BB / TB / alergi dari pemeriksaan terakhir (ranap dulu, lalu ralan).
        try {
            ps = koneksi.prepareStatement(
                "select ifnull(berat,'') as berat, ifnull(tinggi,'') as tinggi, ifnull(alergi,'') as alergi from ( " +
                "  select berat,tinggi,alergi,tgl_perawatan,jam_rawat from pemeriksaan_ranap where no_rawat=? " +
                "  union all " +
                "  select berat,tinggi,alergi,tgl_perawatan,jam_rawat from pemeriksaan_ralan where no_rawat=? " +
                ") x order by tgl_perawatan desc, jam_rawat desc limit 1");
            ps.setString(1, norw); ps.setString(2, norw);
            rs = ps.executeQuery();
            if (rs.next()) { TBb.setText(s(rs.getString("berat"))); TTb.setText(s(rs.getString("tinggi"))); TAlergi.setText(s(rs.getString("alergi"))); }
        } catch (Exception e) { System.out.println("Notif tarik TTV PTO : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }

        tarikResep(); tarikHasilLab();
    }

    private void tarikResep() {
        if (TNoRw.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(null, "Maaf, pilih pasien dulu..!!"); return; }
        Valid.tabelKosong(tabObat);
        try {
            ps = koneksi.prepareStatement(
                "select cast(ro.tgl_peresepan as char) as tgl, ifnull(db.nama_brng,rd.kode_brng) as nama_obat, " +
                "ifnull(rd.aturan_pakai,'') as rejimen from resep_obat ro " +
                "inner join resep_dokter rd on rd.no_resep=ro.no_resep " +
                "left join databarang db on db.kode_brng=rd.kode_brng " +
                "where ro.no_rawat=? order by ro.tgl_peresepan desc, ro.jam_peresepan desc limit 40");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) tabObat.addRow(new String[]{ s(rs.getString("tgl")), s(rs.getString("nama_obat")), s(rs.getString("rejimen")), "" });
        } catch (Exception e) { System.out.println("Notif tarik resep PTO : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    private void tarikHasilLab() {
        if (TNoRw.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(null, "Maaf, pilih pasien dulu..!!"); return; }
        Valid.tabelKosong(tabPenunjang);
        try {
            ps = koneksi.prepareStatement(
                "select cast(dpl.tgl_periksa as char) as tgl, ifnull(tl.Pemeriksaan,'') as nama, ifnull(dpl.nilai,'') as hasil, " +
                "ifnull(tl.satuan,'') as satuan, ifnull(dpl.nilai_rujukan,'') as rujukan from detail_periksa_lab dpl " +
                "left join template_laboratorium tl on tl.id_template=dpl.id_template " +
                "where dpl.no_rawat=? and ifnull(dpl.nilai,'')<>'' order by dpl.tgl_periksa desc, dpl.jam desc limit 40");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) tabPenunjang.addRow(new String[]{
                s(rs.getString("tgl")), s(rs.getString("nama")), s(rs.getString("hasil")), s(rs.getString("satuan")), s(rs.getString("rujukan")) });
        } catch (Exception e) { System.out.println("Notif tarik lab PTO : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    // ── Simpan / hapus ───────────────────────────────────────────────────────
    private void hapusLembar(String norw, String tgl) {
        String[] detail = {"pto_riwayat_obat_rscb", "pto_penunjang_rscb", "pto_obat_saat_ini_rscb"};
        Sequel.queryu2tf("delete from pemantauan_terapi_obat_rscb where no_rawat=? and tanggal=?", 2, new String[]{norw, tgl});
        for (String t : detail) Sequel.queryu2tf("delete from " + t + " where no_rawat=? and tanggal=?", 2, new String[]{norw, tgl});
    }

    private void simpan() {
        if (TNoRw.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(null, "Maaf, pilih pasien dulu..!!"); return; }
        if (NIP.getText().trim().isEmpty()) { Valid.textKosong(NIP, "Apoteker"); return; }
        String norw = TNoRw.getText(), tgl = dtPantau();
        // Edit in-place: kalau jam pemantauan diubah, baris lama dibuang dulu.
        if (!origTanggal.isEmpty() && !origTanggal.equals(tgl)) hapusLembar(norw, origTanggal);
        try {
            ps = koneksi.prepareStatement(
                "replace into pemantauan_terapi_obat_rscb (no_rawat, tanggal, ruangan, tgl_masuk, dpjp, bb, tb, " +
                "alergi_obat, apoteker, keluhan_utama, riwayat_penyakit_sekarang, riwayat_penyakit_terdahulu, " +
                "riwayat_keluarga, riwayat_sosial, soap_s, soap_o, soap_a, soap_p, nip) " +
                "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, norw);
            ps.setString(2, tgl);
            ps.setString(3, TRuangan.getText());
            ps.setString(4, Valid.SetTgl(TglMasuk.getSelectedItem() + ""));
            ps.setString(5, TDpjp.getText());
            ps.setString(6, TBb.getText());
            ps.setString(7, TTb.getText());
            ps.setString(8, TAlergi.getText());
            ps.setString(9, NamaPetugas.getText());
            ps.setString(10, TaKeluhan.getText());
            ps.setString(11, TaRwSekarang.getText());
            ps.setString(12, TaRwTerdahulu.getText());
            ps.setString(13, TaRwKeluarga.getText());
            ps.setString(14, TaRwSosial.getText());
            ps.setString(15, TaS.getText());
            ps.setString(16, TaO.getText());
            ps.setString(17, TaA.getText());
            ps.setString(18, TaP.getText());
            ps.setString(19, NIP.getText());
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("Notif simpan PTO : " + e);
            JOptionPane.showMessageDialog(null, "Maaf, gagal menyimpan PTO..!!\n" + e);
            return;
        } finally { try { if (ps != null) ps.close(); } catch (Exception e) {} }

        // Tabel detail ditulis ulang penuh (baris tanpa isi utama dibuang).
        for (String t : new String[]{"pto_riwayat_obat_rscb", "pto_penunjang_rscb", "pto_obat_saat_ini_rscb"})
            Sequel.queryu2tf("delete from " + t + " where no_rawat=? and tanggal=?", 2, new String[]{norw, tgl});
        try {
            int urut = 0;
            ps = koneksi.prepareStatement("insert into pto_riwayat_obat_rscb (no_rawat,tanggal,urut,nama_obat,rejimen_dosis,rute,waktu_mulai) values (?,?,?,?,?,?,?)");
            for (int i = 0; i < tabRiwayat.getRowCount(); i++) {
                if (cell(tabRiwayat, i, 0).isEmpty()) continue;
                urut++;
                ps.setString(1, norw); ps.setString(2, tgl); ps.setInt(3, urut);
                ps.setString(4, cell(tabRiwayat, i, 0)); ps.setString(5, cell(tabRiwayat, i, 1));
                ps.setString(6, cell(tabRiwayat, i, 2)); ps.setString(7, cell(tabRiwayat, i, 3));
                ps.executeUpdate();
            }
            ps.close();

            urut = 0;
            ps = koneksi.prepareStatement("insert into pto_penunjang_rscb (no_rawat,tanggal,urut,tgl_pemeriksaan,nama_pemeriksaan,hasil,satuan,nilai_rujukan) values (?,?,?,?,?,?,?,?)");
            for (int i = 0; i < tabPenunjang.getRowCount(); i++) {
                if (cell(tabPenunjang, i, 1).isEmpty()) continue;
                urut++;
                ps.setString(1, norw); ps.setString(2, tgl); ps.setInt(3, urut);
                setTgl(ps, 4, cell(tabPenunjang, i, 0));
                ps.setString(5, cell(tabPenunjang, i, 1)); ps.setString(6, cell(tabPenunjang, i, 2));
                ps.setString(7, cell(tabPenunjang, i, 3)); ps.setString(8, cell(tabPenunjang, i, 4));
                ps.executeUpdate();
            }
            ps.close();

            urut = 0;
            ps = koneksi.prepareStatement("insert into pto_obat_saat_ini_rscb (no_rawat,tanggal,urut,tgl_mulai_terapi,nama_obat,rejimen,indikasi) values (?,?,?,?,?,?,?)");
            for (int i = 0; i < tabObat.getRowCount(); i++) {
                if (cell(tabObat, i, 1).isEmpty()) continue;
                urut++;
                ps.setString(1, norw); ps.setString(2, tgl); ps.setInt(3, urut);
                setTgl(ps, 4, cell(tabObat, i, 0));
                ps.setString(5, cell(tabObat, i, 1)); ps.setString(6, cell(tabObat, i, 2)); ps.setString(7, cell(tabObat, i, 3));
                ps.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("Notif simpan detail PTO : " + e);
            JOptionPane.showMessageDialog(null, "Maaf, lembar tersimpan tapi tabel detail gagal..!!\n" + e);
        } finally { try { if (ps != null) ps.close(); } catch (Exception e) {} }

        JOptionPane.showMessageDialog(null, "Data PTO berhasil disimpan..");
        origTanggal = tgl;
        tampil();
    }

    private void hapus() {
        if (tbData.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Maaf, pilih data yang mau dihapus..!!"); return; }
        String norw = tbData.getValueAt(tbData.getSelectedRow(), 0).toString();
        String tgl = tbData.getValueAt(tbData.getSelectedRow(), 3).toString();
        String pemilik = Sequel.cariIsi("select ifnull(nip,'') from pemantauan_terapi_obat_rscb where tanggal='" + tgl + "' and no_rawat=?", norw);
        if (akses.getjml2() >= 1 && pemilik != null && !pemilik.isEmpty() && !pemilik.equals(akses.getkode())) {
            JOptionPane.showMessageDialog(null, "Maaf, lembar ini milik apoteker lain..!!"); return;
        }
        if (JOptionPane.showConfirmDialog(null, "Hapus lembar PTO ini beserta tabel detailnya?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        hapusLembar(norw, tgl);
        emptTeks(); tampil();
    }

    // ── Tampil / getData ─────────────────────────────────────────────────────
    public void tampil() {
        if (tabMode == null) return;
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select t.no_rawat, r.no_rkm_medis, p.nm_pasien, cast(t.tanggal as char) as tanggal, " +
                "ifnull(t.ruangan,'') as ruangan, " +
                // nip bisa berasal dari desktop (tabel petugas) atau e-Farmasi web
                // (tabel pegawai) — dicoba keduanya, lalu nama yang tersimpan.
                "ifnull(pt.nama, ifnull(pg.nama, ifnull(t.apoteker,''))) as apoteker, " +
                "ifnull(t.keluhan_utama,'') as keluhan_utama " +
                "from pemantauan_terapi_obat_rscb t " +
                "inner join reg_periksa r on t.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                "left join petugas pt on t.nip=pt.nip " +
                "left join pegawai pg on t.nip=pg.nik " +
                "where t.tanggal between ? and ? and (t.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) " +
                "order by t.tanggal desc");
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
            for (int i = 3; i <= 5; i++) ps.setString(i, "%" + TCari.getText().trim() + "%");
            rs = ps.executeQuery();
            while (rs.next()) tabMode.addRow(new String[]{
                rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                rs.getString("tanggal"), rs.getString("ruangan"), rs.getString("apoteker"),
                rs.getString("keluhan_utama") });
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif tampil PTO : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void getData() {
        if (tbData.getSelectedRow() == -1) return;
        String norw = tbData.getValueAt(tbData.getSelectedRow(), 0).toString();
        String tgl = tbData.getValueAt(tbData.getSelectedRow(), 3).toString();
        TNoRw.setText(norw);
        TNoRM.setText(tbData.getValueAt(tbData.getSelectedRow(), 1).toString());
        TPasien.setText(tbData.getValueAt(tbData.getSelectedRow(), 2).toString());
        origTanggal = tgl;
        try {
            ps = koneksi.prepareStatement("select * from pemantauan_terapi_obat_rscb where no_rawat=? and tanggal=?");
            ps.setString(1, norw); ps.setString(2, tgl);
            rs = ps.executeQuery();
            if (rs.next()) {
                TglPantau.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tgl));
                TRuangan.setText(s(rs.getString("ruangan")));
                String tm = rs.getString("tgl_masuk");
                if (tm != null && tm.length() >= 10) TglMasuk.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(tm.substring(0, 10)));
                TDpjp.setText(s(rs.getString("dpjp")));
                TBb.setText(s(rs.getString("bb"))); TTb.setText(s(rs.getString("tb")));
                TAlergi.setText(s(rs.getString("alergi_obat")));
                NamaPetugas.setText(s(rs.getString("apoteker")));
                NIP.setText(s(rs.getString("nip")));
                TaKeluhan.setText(s(rs.getString("keluhan_utama")));
                TaRwSekarang.setText(s(rs.getString("riwayat_penyakit_sekarang")));
                TaRwTerdahulu.setText(s(rs.getString("riwayat_penyakit_terdahulu")));
                TaRwKeluarga.setText(s(rs.getString("riwayat_keluarga")));
                TaRwSosial.setText(s(rs.getString("riwayat_sosial")));
                TaS.setText(s(rs.getString("soap_s"))); TaO.setText(s(rs.getString("soap_o")));
                TaA.setText(s(rs.getString("soap_a"))); TaP.setText(s(rs.getString("soap_p")));
            }
        } catch (Exception e) { System.out.println("Notif getData PTO : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }

        isiDetail("pto_riwayat_obat_rscb", tabRiwayat, new String[]{"nama_obat", "rejimen_dosis", "rute", "waktu_mulai"}, norw, tgl);
        isiDetail("pto_penunjang_rscb", tabPenunjang, new String[]{"tgl_pemeriksaan", "nama_pemeriksaan", "hasil", "satuan", "nilai_rujukan"}, norw, tgl);
        isiDetail("pto_obat_saat_ini_rscb", tabObat, new String[]{"tgl_mulai_terapi", "nama_obat", "rejimen", "indikasi"}, norw, tgl);
    }

    private void isiDetail(String tabel, DefaultTableModel model, String[] kolom, String norw, String tgl) {
        Valid.tabelKosong(model);
        try {
            ps = koneksi.prepareStatement("select * from " + tabel + " where no_rawat=? and tanggal=? order by urut");
            ps.setString(1, norw); ps.setString(2, tgl);
            rs = ps.executeQuery();
            while (rs.next()) {
                String[] baris = new String[kolom.length];
                for (int i = 0; i < kolom.length; i++) baris[i] = s(rs.getString(kolom[i]));
                model.addRow(baris);
            }
        } catch (Exception e) { System.out.println("Notif detail PTO (" + tabel + ") : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    // ── Cetak (HTML, langsung dialog print browser) ──────────────────────────
    private String esc(String v) {
        return s(v).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
    }
    private void cetak() {
        if (tbData.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Maaf, pilih lembar yang mau dicetak..!!"); return; }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String norw = tbData.getValueAt(tbData.getSelectedRow(), 0).toString();
        String tgl = tbData.getValueAt(tbData.getSelectedRow(), 3).toString();
        String noRM = tbData.getValueAt(tbData.getSelectedRow(), 1).toString();
        String nmPasien = tbData.getValueAt(tbData.getSelectedRow(), 2).toString();
        getData();

        String tglLahir = s(Sequel.cariIsi("select date_format(tgl_lahir,'%d-%m-%Y') from pasien where no_rkm_medis=?", noRM));
        String jk = s(Sequel.cariIsi("select if(jk='L','Laki-laki','Perempuan') from pasien where no_rkm_medis=?", noRM));

        StringBuilder h = new StringBuilder();
        h.append("<html><head><meta charset='utf-8'><style>")
         .append("body{font-family:Tahoma;font-size:9px;margin:14px;color:#000;}")
         .append("table{border-collapse:collapse;width:100%;}")
         .append(".hdr{border-bottom:2px solid #000;padding-bottom:5px;margin-bottom:5px;}")
         .append(".judul{text-align:center;font-size:12px;font-weight:bold;background:#e8e8e8;border:1px solid #000;padding:3px 0;margin-bottom:6px;}")
         .append(".g td,.g th{border:1px solid #333;padding:3px 5px;font-size:8.5px;vertical-align:top;}")
         .append(".g th{background:#f0f0dc;text-align:center;font-weight:bold;}")
         .append(".sub{font-weight:bold;margin:6px 0 2px;}")
         .append(".ttd{margin-top:22px;text-align:right;}")
         .append("</style></head><body>");
        h.append("<table class='hdr'><tr><td style='text-align:center;'>")
         .append("<div style='font-size:7px;'>DINAS KESEHATAN KABUPATEN BEKASI</div>")
         .append("<div style='font-size:11px;font-weight:bold;'>").append(esc(akses.getnamars())).append("</div>")
         .append("<div style='font-size:7px;'>").append(esc(akses.getalamatrs())).append("</div>")
         .append("</td></tr></table>");
        h.append("<div class='judul'>PEMANTAUAN TERAPI OBAT</div>");

        h.append("<div class='sub'>Deskripsi Pasien</div><table class='g'>")
         .append("<tr><td style='width:16%'>No. RM</td><td style='width:34%'>").append(esc(noRM))
         .append("</td><td style='width:16%'>Tgl Lahir</td><td>").append(esc(tglLahir)).append("</td></tr>")
         .append("<tr><td>Nama Pasien</td><td>").append(esc(nmPasien))
         .append("</td><td>Jenis Kelamin</td><td>").append(esc(jk)).append("</td></tr>")
         .append("<tr><td>Ruangan</td><td>").append(esc(TRuangan.getText()))
         .append("</td><td>BB / TB</td><td>").append(esc(TBb.getText())).append(" kg / ").append(esc(TTb.getText())).append(" cm</td></tr>")
         .append("<tr><td>Tgl Masuk</td><td>").append(esc(TglMasuk.getSelectedItem() + ""))
         .append("</td><td>Alergi Obat</td><td>").append(esc(TAlergi.getText())).append("</td></tr>")
         .append("<tr><td>DPJP</td><td>").append(esc(TDpjp.getText()))
         .append("</td><td>Apoteker</td><td>").append(esc(NamaPetugas.getText())).append("</td></tr>")
         .append("<tr><td>Tanggal Pemantauan</td><td>").append(esc(tgl))
         .append("</td><td>No. Rawat</td><td>").append(esc(norw)).append("</td></tr></table>");

        String[][] anam = {
            {"Keluhan Utama", TaKeluhan.getText()},
            {"Riwayat Penyakit Sekarang", TaRwSekarang.getText()},
            {"Riwayat Penyakit Terdahulu", TaRwTerdahulu.getText()},
            {"Riwayat Keluarga", TaRwKeluarga.getText()},
            {"Riwayat Sosial", TaRwSosial.getText()} };
        for (String[] a : anam)
            h.append("<table class='g' style='margin-top:4px;'><tr><td style='background:#f7f7f0;font-weight:bold;'>")
             .append(a[0]).append("</td></tr><tr><td style='height:28px;'>").append(esc(a[1])).append("</td></tr></table>");

        h.append("<div class='sub'>Riwayat Penggunaan Obat</div><table class='g'>")
         .append("<tr><th>Nama Obat</th><th style='width:20%'>Rejimen Dosis</th><th style='width:15%'>Rute</th><th style='width:20%'>Waktu Mulai</th></tr>");
        if (tabRiwayat.getRowCount() == 0) h.append("<tr><td>&nbsp;</td><td></td><td></td><td></td></tr>");
        for (int i = 0; i < tabRiwayat.getRowCount(); i++)
            h.append("<tr><td>").append(esc(cell(tabRiwayat, i, 0))).append("</td><td>").append(esc(cell(tabRiwayat, i, 1)))
             .append("</td><td>").append(esc(cell(tabRiwayat, i, 2))).append("</td><td>").append(esc(cell(tabRiwayat, i, 3))).append("</td></tr>");
        h.append("</table>");

        h.append("<div class='sub'>Hasil Pemeriksaan Penunjang</div><table class='g'>")
         .append("<tr><th style='width:13%'>Tgl Pemeriksaan</th><th>Nama Pemeriksaan</th><th style='width:14%'>Hasil</th><th style='width:12%'>Satuan</th><th style='width:20%'>Nilai Rujukan</th></tr>");
        if (tabPenunjang.getRowCount() == 0) h.append("<tr><td>&nbsp;</td><td></td><td></td><td></td><td></td></tr>");
        for (int i = 0; i < tabPenunjang.getRowCount(); i++)
            h.append("<tr><td>").append(esc(cell(tabPenunjang, i, 0))).append("</td><td>").append(esc(cell(tabPenunjang, i, 1)))
             .append("</td><td>").append(esc(cell(tabPenunjang, i, 2))).append("</td><td>").append(esc(cell(tabPenunjang, i, 3)))
             .append("</td><td>").append(esc(cell(tabPenunjang, i, 4))).append("</td></tr>");
        h.append("</table>");

        h.append("<div class='sub'>Penggunaan Obat Saat Ini</div><table class='g'>")
         .append("<tr><th style='width:5%'>No</th><th style='width:13%'>Tgl Mulai Terapi</th><th>Nama Obat</th><th style='width:22%'>Rejimen</th><th style='width:24%'>Indikasi</th></tr>");
        if (tabObat.getRowCount() == 0) h.append("<tr><td>&nbsp;</td><td></td><td></td><td></td><td></td></tr>");
        for (int i = 0; i < tabObat.getRowCount(); i++)
            h.append("<tr><td style='text-align:center;'>").append(i + 1).append("</td><td>").append(esc(cell(tabObat, i, 0)))
             .append("</td><td>").append(esc(cell(tabObat, i, 1))).append("</td><td>").append(esc(cell(tabObat, i, 2)))
             .append("</td><td>").append(esc(cell(tabObat, i, 3))).append("</td></tr>");
        h.append("</table>");

        h.append("<div class='sub'>SOAP</div><table class='g'>");
        String[][] soap = {{"S", TaS.getText()}, {"O", TaO.getText()}, {"A", TaA.getText()}, {"P", TaP.getText()}};
        for (String[] x : soap)
            h.append("<tr><td style='width:22px;text-align:center;font-weight:bold;'>").append(x[0])
             .append("</td><td style='height:38px;'>").append(esc(x[1])).append("</td></tr>");
        h.append("</table>");

        h.append("<div class='ttd'>Apoteker<br><br><br>( ").append(esc(NamaPetugas.getText())).append(" )</div>");
        h.append("<script>window.onload=function(){window.print();}</script></body></html>");

        try {
            java.io.File tmp = java.io.File.createTempFile("pto_", ".html");
            tmp.deleteOnExit();
            java.io.Writer w = new java.io.OutputStreamWriter(new java.io.FileOutputStream(tmp), "UTF-8");
            w.write(h.toString()); w.close();
            java.awt.Desktop.getDesktop().browse(tmp.toURI());
        } catch (Exception e) { System.out.println("Notif cetak PTO : " + e); }
        this.setCursor(Cursor.getDefaultCursor());
    }

    // ── Reset / setter dari dialog pemanggil ─────────────────────────────────
    public void emptTeks() {
        origTanggal = "";
        TglPantau.setDate(new Date()); TglMasuk.setDate(new Date());
        for (TextBox t : new TextBox[]{TRuangan, TDpjp, TBb, TTb, TAlergi}) t.setText("");
        for (widget.TextArea t : new widget.TextArea[]{TaKeluhan, TaRwSekarang, TaRwTerdahulu, TaRwKeluarga, TaRwSosial, TaS, TaO, TaA, TaP}) t.setText("");
        Valid.tabelKosong(tabRiwayat); Valid.tabelKosong(tabPenunjang); Valid.tabelKosong(tabObat);
        for (int i = 0; i < 3; i++) tabRiwayat.addRow(new String[4]);
        TabForm.setSelectedIndex(0);
    }

    public void setNoRm(String norwt, Date tgl) {
        TNoRw.setText(norwt); TglPantau.setDate(new Date());
        TCari.setText(norwt); if (tgl != null) DTPCari2.setDate(tgl);
        TNoRM.setText(s(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", norwt)));
        TPasien.setText(s(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText())));
        tarikDataPasien();
    }

    public void setNoRm(String norwt, String norm, String namapasien) {
        TNoRw.setText(norwt); TNoRM.setText(norm); TPasien.setText(namapasien);
        TCari.setText(norwt);
        tarikDataPasien();
    }

    public void setNoRm(String norwt) {
        setNoRm(norwt, (Date) null);
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getrekonsiliasi_obat());
        BtnHapus.setEnabled(akses.getrekonsiliasi_obat());
        if (akses.getjml2() >= 1) {
            NIP.setEditable(false);
            BtnPetugas.setEnabled(false);
            NIP.setText(akses.getkode());
            // Petugas farmasi belum tentu terdaftar di tabel petugas — pakai pegawai
            // sebagai cadangan supaya nama apoteker tetap terisi.
            Sequel.cariIsi("select ifnull(pt.nama, ifnull(pg.nama,'')) from (select ? as k) x " +
                "left join petugas pt on pt.nip=x.k left join pegawai pg on pg.nik=x.k", NamaPetugas, NIP.getText());
        }
    }
}
