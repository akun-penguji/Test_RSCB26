/*
 * RMSkriningSRQ29 — Skrining Kesehatan Jiwa (Self-Reporting Questionnaire / SRQ-29).
 * 29 pertanyaan Ya/Tidak. Skoring: item 1-20 (cutoff >=6 = GME), item 21 (zat),
 * item 22-24 (psikotik), item 25-29 (PTSD). Data menyatu dengan e-Pasien (tabel
 * skrining_srq29). Input pasien via e-Pasien berstatus "Belum Verifikasi";
 * petugas memverifikasi di sini (tombol Verifikasi). Programatik (tanpa .form).
 * Hak akses reuse akses.getpenilaian_tambahan_bunuh_diri(). Cetak Jasper.
 */
package rekammedis;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariPetugas;

public final class RMSkriningSRQ29 extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);

    private static final String[] YATIDAK = {"", "Ya", "Tidak"};
    private static final String[] SRQ = {
        "Apakah Anda sering merasa sakit kepala?","Apakah Anda kehilangan nafsu makan?","Apakah tidur Anda tidak nyenyak?",
        "Apakah Anda mudah merasa takut?","Apakah Anda merasa cemas, tegang, atau khawatir?","Apakah tangan Anda gemetar?",
        "Apakah Anda mengalami gangguan pencernaan?","Apakah Anda merasa sulit berpikir jernih?","Apakah Anda merasa tidak bahagia?",
        "Apakah Anda lebih sering menangis?","Apakah Anda merasa sulit untuk menikmati aktivitas sehari-hari?","Apakah Anda mengalami kesulitan untuk mengambil keputusan?",
        "Apakah aktivitas/tugas sehari-hari Anda terbengkalai?","Apakah Anda merasa tidak mampu berperan dalam kehidupan ini?","Apakah Anda kehilangan minat terhadap banyak hal?",
        "Apakah Anda merasa tidak berharga?","Apakah Anda mempunyai pikiran untuk mengakhiri hidup Anda?","Apakah Anda merasa lelah sepanjang waktu?",
        "Apakah Anda merasa tidak enak di perut?","Apakah Anda mudah lelah?","Apakah Anda minum alkohol lebih banyak dari biasanya atau apakah Anda menggunakan narkoba?",
        "Apakah Anda yakin bahwa seseorang mencoba mencelakai Anda dengan cara tertentu?","Apakah ada yang mengganggu atau hal yang tidak biasa dalam pikiran Anda?","Apakah Anda pernah mendengar suara tanpa tahu sumbernya atau yang orang lain tidak dapat mendengar?",
        "Apakah Anda mengalami mimpi yang mengganggu tentang suatu bencana/musibah atau adakah saat-saat Anda seolah mengalami kembali kejadian bencana itu?","Apakah Anda menghindari kegiatan, tempat, orang atau pikiran yang mengingatkan Anda akan bencana tersebut?","Apakah minat Anda terhadap teman dan kegiatan yang biasa Anda lakukan berkurang?",
        "Apakah Anda merasa sangat terganggu jika berada dalam situasi yang mengingatkan Anda akan bencana atau jika Anda berpikir tentang bencana itu?","Apakah Anda kesulitan memahami atau mengekspresikan perasaan Anda?"
    };

    private widget.InternalFrame internalFrame1;
    private widget.ScrollPane Scroll, scrollInput;
    private widget.Table tbObat;
    private widget.panelisi panelTombol, panelCari;
    private widget.PanelBiasa FormInput;
    private javax.swing.JPanel PanelInput;
    private widget.TextBox TNoRw, TNoRM, TPasien, TglLahir, Jk, KdPetugas, NmPetugas, Kesimpulan, TCari;
    private widget.Tanggal DTPCari1, DTPCari2;
    private widget.ComboBox[] Q = new widget.ComboBox[29];
    private widget.Label lblSkor, lblStatus;
    private widget.CekBox ChkInput;
    private widget.Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnVerif, BtnPrint, BtnKeluar, BtnCari, BtnAll, btnPetugas;
    private widget.Label LCount;
    private javax.swing.JPopupMenu popupCetak;
    private javax.swing.JMenuItem MnCetakFormulir;

    public RMSkriningSRQ29(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Skrining Kesehatan Jiwa :: SRQ-29 (Self-Reporting Questionnaire)");
        tabMode = new DefaultTableModel(null, new Object[]{
            "ID", "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "J.K.", "Tanggal",
            "Skor 1-20", "Zat(21)", "Psikotik(22-24)", "PTSD(25-29)", "Status", "Verifikator", "Kesimpulan"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        initComponents();
        setSize(650, 700);
        setLocation(8, 1);

        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(widget.Table.AUTO_RESIZE_OFF);
        int[] lebar = {120, 105, 70, 150, 65, 35, 115, 60, 55, 90, 90, 90, 150, 260};
        for (i = 0; i < lebar.length; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            column.setPreferredWidth(lebar[i]);
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        KdPetugas.setDocument(new batasInput((byte) 20).getKata(KdPetugas));
        TCari.setDocument(new batasInput((int) 100).getKata(TCari));

        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override public void insertUpdate(DocumentEvent e) { if (TCari.getText().length() > 2) tampil(); }
                @Override public void removeUpdate(DocumentEvent e) { if (TCari.getText().length() > 2) tampil(); }
                @Override public void changedUpdate(DocumentEvent e) { if (TCari.getText().length() > 2) tampil(); }
            });
        }

        petugas.addWindowListener(new WindowListener() {
            @Override public void windowOpened(WindowEvent e) {}
            @Override public void windowClosing(WindowEvent e) {}
            @Override public void windowClosed(WindowEvent e) {
                if (petugas.getTable().getSelectedRow() != -1) {
                    KdPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                    NmPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                }
                KdPetugas.requestFocus();
            }
            @Override public void windowIconified(WindowEvent e) {}
            @Override public void windowDeiconified(WindowEvent e) {}
            @Override public void windowActivated(WindowEvent e) {}
            @Override public void windowDeactivated(WindowEvent e) {}
        });
        ChkInput.setSelected(false);
        isForm();
        emptTeks();
    }

    private widget.Label label(String txt) {
        widget.Label l = new widget.Label();
        l.setText(txt);
        l.setFont(new java.awt.Font("Tahoma", 1, 11));
        l.setForeground(new java.awt.Color(50, 70, 50));
        return l;
    }

    private widget.Button tombol(String txt, char mn, String ikon) {
        widget.Button b = new widget.Button();
        b.setText(txt); b.setMnemonic(mn);
        try { b.setIcon(new javax.swing.ImageIcon(getClass().getResource(ikon))); } catch (Exception e) { }
        return b;
    }

    private void initComponents() {
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        panelCari = new widget.panelisi();
        panelTombol = new widget.panelisi();
        PanelInput = new javax.swing.JPanel();
        scrollInput = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        ChkInput = new widget.CekBox();

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
            "::[ Skrining Kesehatan Jiwa - SRQ-29 (Self-Reporting Questionnaire) ]::",
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));
        internalFrame1.setVisible(true);
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        PanelInput.setLayout(new java.awt.BorderLayout());
        ChkInput.setText(".: Input Data");
        ChkInput.setFont(new java.awt.Font("Tahoma", 1, 11));
        try {
            ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
            ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        } catch (Exception e) { }
        ChkInput.addActionListener(e -> isForm());
        buildFormInput();
        scrollInput.setViewportView(FormInput);
        scrollInput.setPreferredSize(new Dimension(WIDTH, 360));
        PanelInput.add(scrollInput, java.awt.BorderLayout.CENTER);
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);
        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        popupCetak = new javax.swing.JPopupMenu();
        MnCetakFormulir = new javax.swing.JMenuItem("Cetak Formulir SRQ-29");
        try { MnCetakFormulir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); } catch (Exception e) {}
        MnCetakFormulir.addActionListener(this::cetakFormulir);
        popupCetak.add(MnCetakFormulir);
        tbObat.setComponentPopupMenu(popupCetak);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { getData(); }
            @Override public void mousePressed(java.awt.event.MouseEvent evt) { pilihBaris(evt); }
            @Override public void mouseReleased(java.awt.event.MouseEvent evt) { pilihBaris(evt); }
        });
        Scroll.setViewportView(tbObat);
        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        javax.swing.JPanel jPanel3 = new javax.swing.JPanel(new java.awt.BorderLayout());
        panelCari.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 4));
        DTPCari1 = new widget.Tanggal(); DTPCari2 = new widget.Tanggal();
        DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setDate(new Date()); DTPCari2.setDate(new Date());
        TCari = new widget.TextBox(); TCari.setPreferredSize(new Dimension(180, 25));
        BtnCari = new widget.Button(); BtnCari.setText("Cari"); BtnCari.setMnemonic('3');
        try { BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); } catch (Exception e) {}
        BtnAll = new widget.Button(); BtnAll.setText("Semua"); BtnAll.setMnemonic('M');
        try { BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); } catch (Exception e) {}
        LCount = new widget.Label(); LCount.setText("0");
        panelCari.add(label("Tanggal")); panelCari.add(DTPCari1);
        panelCari.add(label("s/d")); panelCari.add(DTPCari2);
        panelCari.add(TCari); panelCari.add(BtnCari); panelCari.add(BtnAll);
        panelCari.add(label("Jumlah :")); panelCari.add(LCount);
        jPanel3.add(panelCari, java.awt.BorderLayout.PAGE_START);

        BtnCari.addActionListener(e -> tampil());
        BtnAll.addActionListener(e -> { TCari.setText(""); tampil(); });
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(KeyEvent evt) { if (evt.getKeyCode() == KeyEvent.VK_ENTER) tampil(); }
        });

        panelTombol.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 4, 4));
        BtnSimpan = tombol("Simpan", 'S', "/picture/save-16x16.png");
        BtnBatal = tombol("Baru", 'B', "/picture/Cancel-2-16x16.png");
        BtnEdit = tombol("Ganti", 'G', "/picture/inventaris.png");
        BtnHapus = tombol("Hapus", 'H', "/picture/stop_f2.png");
        BtnVerif = tombol("Verifikasi", 'V', "/picture/accept.png");
        BtnPrint = tombol("Cetak", 'T', "/picture/b_print.png");
        BtnKeluar = tombol("Keluar", 'K', "/picture/exit.png");
        panelTombol.add(BtnSimpan); panelTombol.add(BtnBatal); panelTombol.add(BtnEdit);
        panelTombol.add(BtnHapus); panelTombol.add(BtnVerif); panelTombol.add(BtnPrint); panelTombol.add(BtnKeluar);
        jPanel3.add(panelTombol, java.awt.BorderLayout.CENTER);

        BtnSimpan.addActionListener(this::BtnSimpanActionPerformed);
        BtnBatal.addActionListener(e -> { emptTeks(); ChkInput.setSelected(true); isForm(); });
        BtnEdit.addActionListener(this::BtnEditActionPerformed);
        BtnHapus.addActionListener(this::BtnHapusActionPerformed);
        BtnVerif.addActionListener(this::BtnVerifActionPerformed);
        BtnPrint.addActionListener(this::cetakFormulir);
        BtnKeluar.addActionListener(e -> { petugas.dispose(); dispose(); });

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);
    }

    private void buildFormInput() {
        FormInput.setBackground(new java.awt.Color(250, 255, 245));
        FormInput.setBorder(null);
        FormInput.setLayout(null);

        TNoRw = new widget.TextBox(); TNoRM = new widget.TextBox();
        TPasien = new widget.TextBox(); TglLahir = new widget.TextBox(); Jk = new widget.TextBox();
        KdPetugas = new widget.TextBox(); NmPetugas = new widget.TextBox();
        Kesimpulan = new widget.TextBox(); Kesimpulan.setEditable(false);
        Kesimpulan.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblSkor = new widget.Label(); lblSkor.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblStatus = new widget.Label(); lblStatus.setFont(new java.awt.Font("Tahoma", 1, 11));
        lblStatus.setForeground(new java.awt.Color(170, 0, 0));
        btnPetugas = new widget.Button();
        try { btnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); } catch (Exception e) {}
        btnPetugas.addActionListener(e -> { petugas.emptTeks(); petugas.isCek(); petugas.setVisible(true); });

        TNoRM.setEditable(false); TPasien.setEditable(false); TglLahir.setEditable(false); Jk.setEditable(false);
        NmPetugas.setEditable(false);

        int y = 8;
        put(label("No.Rawat"), 10, y, 90, 25); put(TNoRw, 105, y, 160, 25);
        put(label("No.RM"), 300, y, 100, 25); put(TNoRM, 405, y, 170, 25); y += 28;
        put(label("Nama Pasien"), 10, y, 90, 25); put(TPasien, 105, y, 160, 25);
        put(label("Tgl.Lahir/JK"), 300, y, 100, 25); put(TglLahir, 405, y, 110, 25); put(Jk, 520, y, 55, 25); y += 28;
        put(label("Petugas Verifikasi"), 10, y, 110, 25); put(KdPetugas, 122, y, 133, 25); put(btnPetugas, 258, y, 32, 25);
        put(NmPetugas, 300, y, 275, 25); y += 30;
        put(lblStatus, 10, y, 585, 20); y += 24;

        widget.Label judul = label("Kuesioner SRQ-29 (kondisi 30 hari terakhir)");
        judul.setForeground(new java.awt.Color(170, 0, 0));
        put(judul, 10, y, 585, 20); y += 24;

        for (i = 0; i < 29; i++) {
            widget.Label q = new widget.Label();
            q.setText("<html>" + (i + 1) + ". " + SRQ[i] + "</html>");
            q.setFont(new java.awt.Font("Tahoma", 0, 11));
            put(q, 10, y, 460, 30);
            Q[i] = new widget.ComboBox();
            for (String o : YATIDAK) Q[i].addItem(o);
            Q[i].addActionListener(this::onJawab);
            put(Q[i], 475, y + 2, 95, 25);
            y += 32;
        }
        put(label("Skor"), 10, y, 60, 25); put(lblSkor, 75, y, 500, 25); y += 28;
        put(label("Kesimpulan"), 10, y, 80, 25); put(Kesimpulan, 95, y, 480, 25); y += 30;
        FormInput.setPreferredSize(new Dimension(600, y + 10));
    }

    private void put(java.awt.Component c, int x, int y, int w, int h) { c.setBounds(x, y, w, h); FormInput.add(c); }

    private void onJawab(ActionEvent e) { hitung(); }

    private String jw(int n) { return Q[n].getSelectedItem() == null ? "" : Q[n].getSelectedItem().toString(); }

    private void pilihBaris(java.awt.event.MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            int r = tbObat.rowAtPoint(evt.getPoint());
            if (r >= 0) { tbObat.setRowSelectionInterval(r, r); getData(); }
        }
    }

    // Skoring SRQ-29
    private int skor20; private String adaZat; private int skorPsi; private int skorPtsd;
    private void hitung() {
        skor20 = 0; for (i = 0; i < 20; i++) if (jw(i).equals("Ya")) skor20++;
        adaZat = jw(20).equals("Ya") ? "Ya" : (jw(20).equals("Tidak") ? "Tidak" : "");
        skorPsi = 0; for (i = 21; i <= 23; i++) if (jw(i).equals("Ya")) skorPsi++;
        skorPtsd = 0; for (i = 24; i <= 28; i++) if (jw(i).equals("Ya")) skorPtsd++;
        StringBuilder k = new StringBuilder();
        k.append(skor20 >= 6 ? "Indikasi gangguan mental emosional (skor " + skor20 + "/20)" : "Tidak ada indikasi GME (skor " + skor20 + "/20)");
        if (adaZat.equals("Ya")) k.append("; indikasi penggunaan zat");
        if (skorPsi >= 1) k.append("; indikasi psikotik (" + skorPsi + "/3)");
        if (skorPtsd >= 1) k.append("; indikasi PTSD (" + skorPtsd + "/5)");
        Kesimpulan.setText(k.toString());
        lblSkor.setText("SRQ-20: " + skor20 + "/20 | Zat(21): " + (adaZat.equals("") ? "-" : adaZat) + " | Psikotik(22-24): " + skorPsi + "/3 | PTSD(25-29): " + skorPtsd + "/5");
    }

    private String tgl(widget.Tanggal t) {
        try { return new SimpleDateFormat("yyyy-MM-dd").format(t.getDate()); }
        catch (Exception e) { return new SimpleDateFormat("yyyy-MM-dd").format(new Date()); }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            String q =
                "select s.id,s.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.tgl_lahir,pasien.jk,s.tanggal,"
                + "s.skor_srq20,s.ada_zat,s.skor_psikotik,s.skor_ptsd,s.status_verifikasi,ifnull(pg.nama,'') verifikator,s.kesimpulan "
                + "from skrining_srq29 s inner join reg_periksa on s.no_rawat=reg_periksa.no_rawat "
                + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                + "left join pegawai pg on pg.nik=s.nip_verifikasi ";
            if (TCari.getText().trim().equals("")) {
                ps = koneksi.prepareStatement(q + "where s.tanggal between ? and ? order by s.tanggal desc");
            } else {
                ps = koneksi.prepareStatement(q + "where s.tanggal between ? and ? and (s.no_rawat like ? or pasien.no_rkm_medis like ? or pasien.nm_pasien like ?) order by s.tanggal desc");
            }
            try {
                ps.setString(1, tgl(DTPCari1) + " 00:00:00");
                ps.setString(2, tgl(DTPCari2) + " 23:59:59");
                if (!TCari.getText().trim().equals("")) { for (int k = 3; k <= 5; k++) ps.setString(k, "%" + TCari.getText() + "%"); }
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString("id"), rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("tgl_lahir"), rs.getString("jk"), rs.getString("tanggal"),
                        rs.getString("skor_srq20"), rs.getString("ada_zat"), rs.getString("skor_psikotik"), rs.getString("skor_ptsd"),
                        rs.getString("status_verifikasi"), rs.getString("verifikator"), rs.getString("kesimpulan")
                    });
                }
            } catch (Exception e) { System.out.println("Notif : " + e); }
            finally { if (rs != null) rs.close(); if (ps != null) ps.close(); }
        } catch (Exception e) { System.out.println("Notifikasi : " + e); }
        LCount.setText("" + tabMode.getRowCount());
    }

    public void emptTeks() {
        for (i = 0; i < 29; i++) Q[i].setSelectedIndex(0);
        lblStatus.setText(" ");
        hitung();
    }

    private void getData() {
        if (tbObat.getSelectedRow() == -1) return;
        int r = tbObat.getSelectedRow();
        TNoRw.setText(tbObat.getValueAt(r, 1).toString());
        TNoRM.setText(tbObat.getValueAt(r, 2).toString());
        TPasien.setText(tbObat.getValueAt(r, 3).toString());
        TglLahir.setText(tbObat.getValueAt(r, 4).toString());
        Jk.setText(tbObat.getValueAt(r, 5).toString());
        lblStatus.setText("Status: " + tbObat.getValueAt(r, 11).toString()
            + (tbObat.getValueAt(r, 12).toString().equals("") ? "" : " oleh " + tbObat.getValueAt(r, 12).toString()));
        // Muat 29 jawaban dari DB berdasarkan id
        try {
            ps = koneksi.prepareStatement("select * from skrining_srq29 where id=?");
            ps.setString(1, tbObat.getValueAt(r, 0).toString());
            rs = ps.executeQuery();
            if (rs.next()) { for (i = 0; i < 29; i++) Q[i].setSelectedItem(rs.getString("p" + (i + 1))); }
        } catch (Exception e) { System.out.println("Notif : " + e); }
        finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
        hitung();
    }

    private void isRawat() {
        try {
            ps = koneksi.prepareStatement(
                "select reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir "
                + "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis where reg_periksa.no_rawat=?");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            if (rs.next()) {
                TNoRM.setText(rs.getString("no_rkm_medis"));
                TPasien.setText(rs.getString("nm_pasien"));
                Jk.setText(rs.getString("jk"));
                TglLahir.setText(rs.getString("tgl_lahir"));
            }
        } catch (Exception e) { System.out.println("Notif : " + e); }
        finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        if (tgl2 != null) DTPCari2.setDate(tgl2);
        isRawat();
        ChkInput.setSelected(true);
        isForm();
    }

    public void isCek() {
        boolean b = akses.getpenilaian_tambahan_bunuh_diri();
        BtnSimpan.setEnabled(b); BtnHapus.setEnabled(b); BtnEdit.setEnabled(b); BtnVerif.setEnabled(b); BtnPrint.setEnabled(b);
        if (akses.getjml2() >= 1) {
            KdPetugas.setEditable(false); btnPetugas.setEnabled(false);
            KdPetugas.setText(akses.getkode());
            NmPetugas.setText(petugas.tampil3(KdPetugas.getText()));
            if (NmPetugas.getText().equals("")) { KdPetugas.setText(""); JOptionPane.showMessageDialog(null, "User login bukan petugas...!!"); }
        }
    }

    private void isForm() {
        boolean tampil = ChkInput.isSelected();
        scrollInput.setVisible(tampil);
        PanelInput.setPreferredSize(new Dimension(WIDTH, tampil ? 430 : 26));
        PanelInput.revalidate();
        if (internalFrame1 != null) { internalFrame1.revalidate(); internalFrame1.repaint(); }
    }

    private boolean semuaTerisi() {
        for (i = 0; i < 29; i++) if (jw(i).equals("")) return false;
        return true;
    }

    private void BtnSimpanActionPerformed(ActionEvent evt) {
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) { Valid.textKosong(TNoRw, "pasien"); return; }
        if (KdPetugas.getText().trim().equals("") || NmPetugas.getText().trim().equals("")) { Valid.textKosong(KdPetugas, "Petugas"); return; }
        if (!semuaTerisi()) { JOptionPane.showMessageDialog(null, "Lengkapi dulu 29 pertanyaan..!!"); return; }
        hitung();
        String id = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        try {
            StringBuilder cols = new StringBuilder("id,no_rkm_medis,no_rawat,tanggal");
            StringBuilder ph = new StringBuilder("?,?,?,now()");
            for (i = 1; i <= 29; i++) { cols.append(",p").append(i); ph.append(",?"); }
            cols.append(",skor_srq20,ada_zat,skor_psikotik,skor_ptsd,kesimpulan,status_verifikasi,nip_verifikasi,tgl_verifikasi");
            ph.append(",?,?,?,?,?,'Terverifikasi',?,now()");
            ps = koneksi.prepareStatement("insert into skrining_srq29 (" + cols + ") values (" + ph + ")");
            int idx = 1;
            ps.setString(idx++, id); ps.setString(idx++, TNoRM.getText()); ps.setString(idx++, TNoRw.getText());
            for (i = 0; i < 29; i++) ps.setString(idx++, jw(i));
            ps.setInt(idx++, skor20); ps.setString(idx++, adaZat); ps.setInt(idx++, skorPsi); ps.setInt(idx++, skorPtsd);
            ps.setString(idx++, Kesimpulan.getText()); ps.setString(idx++, KdPetugas.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data tersimpan & terverifikasi.");
            emptTeks(); tampil();
        } catch (Exception e) { JOptionPane.showMessageDialog(null, "Gagal menyimpan: " + e); }
        finally { try { if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    private void BtnEditActionPerformed(ActionEvent evt) {
        if (tbObat.getSelectedRow() == -1) { JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data dulu..!!"); return; }
        if (!semuaTerisi()) { JOptionPane.showMessageDialog(null, "Lengkapi dulu 29 pertanyaan..!!"); return; }
        hitung();
        try {
            StringBuilder set = new StringBuilder();
            for (i = 1; i <= 29; i++) set.append("p").append(i).append("=?,");
            set.append("skor_srq20=?,ada_zat=?,skor_psikotik=?,skor_ptsd=?,kesimpulan=?");
            ps = koneksi.prepareStatement("update skrining_srq29 set " + set + " where id=?");
            int idx = 1;
            for (i = 0; i < 29; i++) ps.setString(idx++, jw(i));
            ps.setInt(idx++, skor20); ps.setString(idx++, adaZat); ps.setInt(idx++, skorPsi); ps.setInt(idx++, skorPtsd);
            ps.setString(idx++, Kesimpulan.getText());
            ps.setString(idx++, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data diubah.");
            tampil();
        } catch (Exception e) { JOptionPane.showMessageDialog(null, "Gagal mengubah: " + e); }
        finally { try { if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    private void BtnHapusActionPerformed(ActionEvent evt) {
        if (tbObat.getSelectedRow() == -1) { JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data dulu..!!"); return; }
        if (Sequel.queryu2tf("delete from skrining_srq29 where id=?", 1, new String[]{ tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() }) == true) {
            tabMode.removeRow(tbObat.getSelectedRow());
            LCount.setText("" + tabMode.getRowCount());
            emptTeks();
        } else { JOptionPane.showMessageDialog(null, "Gagal menghapus..!!"); }
    }

    private void BtnVerifActionPerformed(ActionEvent evt) {
        if (tbObat.getSelectedRow() == -1) { JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data dulu..!!"); return; }
        if (KdPetugas.getText().trim().equals("") || NmPetugas.getText().trim().equals("")) { Valid.textKosong(KdPetugas, "Petugas"); return; }
        try {
            ps = koneksi.prepareStatement("update skrining_srq29 set status_verifikasi='Terverifikasi', nip_verifikasi=?, tgl_verifikasi=now() where id=?");
            ps.setString(1, KdPetugas.getText());
            ps.setString(2, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data terverifikasi.");
            tampil();
        } catch (Exception e) { JOptionPane.showMessageDialog(null, "Gagal verifikasi: " + e); }
        finally { try { if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    private void cetakFormulir(ActionEvent evt) {
        if (tbObat.getSelectedRow() <= -1) { JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data dulu..!!"); return; }
        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        Valid.MyReportqry("rptSkriningSRQ29.jasper", "report", "::[ Formulir Skrining Kesehatan Jiwa (SRQ-29) ]::",
            "select pasien.no_rkm_medis,pasien.nm_pasien,pasien.tgl_lahir,pasien.jk,pasien.no_ktp,pasien.tmp_lahir,pg.nama as nm_verifikator,s.* "
            + "from skrining_srq29 s inner join reg_periksa on s.no_rawat=reg_periksa.no_rawat "
            + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
            + "left join pegawai pg on pg.nik=s.nip_verifikasi "
            + "where s.id='" + tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "'", param);
    }
}
