/*
 * RMSkriningASQ — Skrining Pasien Risiko Bunuh Diri (Ask Suicide-Screening Questions / ASQ)
 * Form RM.103. UI/UX mengikuti form Khanza (RMSkriningPHQ9): InternalFrame berjudul,
 * form input di atas, tabel di tengah, panel bawah pencarian + tombol CRUD, toggle ChkInput.
 * 5 pertanyaan Ya/Tidak (Q4 & Q5 disertai deskripsi). Interpretasi:
 *   - Q1..Q4 semua "Tidak"            -> Skrining Negatif
 *   - ada "Ya" (Q1..Q4) & Q5 "Tidak"  -> Positif Non-Akut (evaluasi kesehatan jiwa lanjut)
 *   - Q5 "Ya"                          -> Positif AKUT (evaluasi keselamatan segera)
 * Hak akses memakai akses.getpenilaian_tambahan_bunuh_diri(). Simpan ke tabel skrining_asq.
 */
package rekammedis;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariPetugas;

public final class RMSkriningASQ extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);
    private String TANGGALMUNDUR = "yes";
    private String finger = "";

    private static final String[] YATIDAK = {"Tidak", "Ya"};
    private static final String[] ASQ_PERTANYAAN = {
        "1. Dalam beberapa minggu terakhir, apakah Anda berharap untuk mengakhiri hidup?",
        "2. Dalam beberapa minggu terakhir, apakah Anda merasa keluarga Anda lebih baik bila Anda mengakhiri hidup?",
        "3. Dalam beberapa minggu terakhir, apakah Anda ada keinginan bunuh diri?",
        "4. Apakah Anda pernah mencoba bunuh diri? (bila iya: bagaimana & kapan)",
        "5. Apakah Anda merasa ingin bunuh diri saat ini? (bila iya, deskripsikan)"
    };

    // Komponen (widget Khanza)
    private widget.InternalFrame internalFrame1;
    private widget.ScrollPane Scroll, scrollInput;
    private widget.Table tbObat;
    private widget.panelisi panelTombol, panelCari;
    private widget.PanelBiasa FormInput;
    private javax.swing.JPanel PanelInput;
    private widget.TextBox TNoRw, TNoRM, TPasien, TglLahir, Jk, KdPetugas, NmPetugas, Kesimpulan, TCari, TanggalRegistrasi, Ket4, Ket5;
    private widget.Tanggal Tanggal, DTPCari1, DTPCari2;
    private widget.ComboBox Jam, Menit, Detik;
    private widget.ComboBox[] ASQ = new widget.ComboBox[5];
    private widget.CekBox ChkKejadian, ChkInput;
    private widget.Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll, btnPetugas;
    private widget.Label LCount;
    private javax.swing.JPopupMenu popupCetak;
    private javax.swing.JMenuItem MnCetakFormulir;

    public RMSkriningASQ(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Skrining Risiko Bunuh Diri :: ASQ (Ask Suicide-Screening Questions)");

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "J.K.", "Kode Petugas", "Nama Petugas", "Tanggal",
            "ASQ1", "ASQ2", "ASQ3", "ASQ4", "Ket.ASQ4", "ASQ5", "Ket.ASQ5", "Kesimpulan"
        }) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        initComponents();
        setSize(628, 674);
        setLocation(8, 1);

        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(widget.Table.AUTO_RESIZE_OFF);
        int[] lebar = {105, 70, 150, 65, 35, 80, 150, 115, 55, 55, 55, 55, 180, 55, 180, 260};
        for (i = 0; i < lebar.length; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            column.setPreferredWidth(lebar[i]);
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        KdPetugas.setDocument(new batasInput((byte) 20).getKata(KdPetugas));
        Kesimpulan.setDocument(new batasInput((int) 150).getKata(Kesimpulan));
        Ket4.setDocument(new batasInput((int) 255).getKata(Ket4));
        Ket5.setDocument(new batasInput((int) 255).getKata(Ket5));
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
        try { TANGGALMUNDUR = koneksiDB.TANGGALMUNDUR(); } catch (Exception e) { TANGGALMUNDUR = "yes"; }
        emptTeks();
        jam();
    }

    private widget.Label label(String txt) {
        widget.Label l = new widget.Label();
        l.setText(txt);
        l.setFont(new java.awt.Font("Tahoma", 1, 11));
        l.setForeground(new java.awt.Color(50, 70, 50));
        return l;
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

        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
            "::[ Skrining Pasien Risiko Bunuh Diri - ASQ (Ask Suicide-Screening Questions) ]::",
            TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));
        internalFrame1.setVisible(true);
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        // ══ (1) FORM INPUT — di ATAS (PAGE_START), toggle ChkInput ══
        PanelInput.setLayout(new java.awt.BorderLayout());
        ChkInput.setText(".: Input Data");
        ChkInput.setFont(new java.awt.Font("Tahoma", 1, 11));
        try {
            ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
            ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        } catch (Exception e) { /* ikon opsional */ }
        ChkInput.addActionListener(e -> isForm());
        buildFormInput();
        scrollInput.setViewportView(FormInput);
        scrollInput.setPreferredSize(new Dimension(WIDTH, 360));
        PanelInput.add(scrollInput, java.awt.BorderLayout.CENTER);
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);
        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        // ══ (2) TABLE UI — di TENGAH + klik-kanan cetak ══
        popupCetak = new javax.swing.JPopupMenu();
        MnCetakFormulir = new javax.swing.JMenuItem("Cetak Formulir ASQ");
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

        // ══ (3) PANEL BAWAH — pencarian (atas) + tombol CRUD (bawah) ══
        javax.swing.JPanel jPanel3 = new javax.swing.JPanel(new java.awt.BorderLayout());

        panelCari.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 4));
        DTPCari1 = new widget.Tanggal(); DTPCari2 = new widget.Tanggal();
        DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setDate(new Date()); DTPCari2.setDate(new Date());
        TCari = new widget.TextBox(); TCari.setPreferredSize(new Dimension(180, 25));
        BtnCari = new widget.Button(); BtnCari.setText("Cari"); BtnCari.setMnemonic('3');
        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnAll = new widget.Button(); BtnAll.setText("Semua"); BtnAll.setMnemonic('M');
        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
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
        BtnHapus = tombol("Hapus", 'H', "/picture/stop_f2.png");
        BtnEdit = tombol("Ganti", 'G', "/picture/inventaris.png");
        BtnPrint = tombol("Cetak", 'T', "/picture/b_print.png");
        BtnKeluar = tombol("Keluar", 'K', "/picture/exit.png");
        panelTombol.add(BtnSimpan); panelTombol.add(BtnBatal); panelTombol.add(BtnEdit);
        panelTombol.add(BtnHapus); panelTombol.add(BtnPrint); panelTombol.add(BtnKeluar);
        jPanel3.add(panelTombol, java.awt.BorderLayout.CENTER);

        BtnSimpan.addActionListener(this::BtnSimpanActionPerformed);
        BtnBatal.addActionListener(e -> { emptTeks(); ChkInput.setSelected(true); isForm(); });
        BtnEdit.addActionListener(this::BtnEditActionPerformed);
        BtnHapus.addActionListener(this::BtnHapusActionPerformed);
        BtnPrint.addActionListener(this::cetakFormulir);
        BtnKeluar.addActionListener(e -> { petugas.dispose(); dispose(); });

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);
    }

    private widget.Button tombol(String txt, char mn, String ikon) {
        widget.Button b = new widget.Button();
        b.setText(txt);
        b.setMnemonic(mn);
        try { b.setIcon(new javax.swing.ImageIcon(getClass().getResource(ikon))); } catch (Exception e) { }
        return b;
    }

    private void buildFormInput() {
        FormInput.setBackground(new java.awt.Color(250, 255, 245));
        FormInput.setBorder(null);
        FormInput.setLayout(null);

        TNoRw = new widget.TextBox(); TNoRM = new widget.TextBox();
        TPasien = new widget.TextBox(); TglLahir = new widget.TextBox(); Jk = new widget.TextBox();
        KdPetugas = new widget.TextBox(); NmPetugas = new widget.TextBox();
        TanggalRegistrasi = new widget.TextBox();
        Kesimpulan = new widget.TextBox(); Kesimpulan.setEditable(false);
        Kesimpulan.setFont(new java.awt.Font("Tahoma", 1, 11));
        Ket4 = new widget.TextBox(); Ket5 = new widget.TextBox();
        Tanggal = new widget.Tanggal(); Tanggal.setDisplayFormat("dd-MM-yyyy"); Tanggal.setDate(new Date());
        ChkKejadian = new widget.CekBox(); ChkKejadian.setText("Waktu Sekarang"); ChkKejadian.setSelected(true);
        btnPetugas = new widget.Button();
        try { btnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); } catch (Exception e) {}
        btnPetugas.addActionListener(e -> { petugas.emptTeks(); petugas.isCek(); petugas.setVisible(true); });
        Jam = comboAngka(24); Menit = comboAngka(60); Detik = comboAngka(60);

        TNoRM.setEditable(false); TPasien.setEditable(false); TglLahir.setEditable(false); Jk.setEditable(false);
        NmPetugas.setEditable(false);

        int y = 8;
        put(label("No.Rawat"), 10, y, 90, 25); put(TNoRw, 105, y, 160, 25);
        put(label("No.RM"), 300, y, 100, 25); put(TNoRM, 405, y, 170, 25); y += 28;
        put(label("Nama Pasien"), 10, y, 90, 25); put(TPasien, 105, y, 160, 25);
        put(label("Tgl.Lahir/JK"), 300, y, 100, 25); put(TglLahir, 405, y, 110, 25); put(Jk, 520, y, 55, 25); y += 28;
        put(label("Tanggal"), 10, y, 90, 25); put(Tanggal, 105, y, 160, 25);
        put(Jam, 300, y, 55, 25); put(Menit, 358, y, 55, 25); put(Detik, 416, y, 55, 25);
        put(ChkKejadian, 475, y, 120, 25); y += 28;
        put(label("Petugas"), 10, y, 90, 25); put(KdPetugas, 105, y, 150, 25); put(btnPetugas, 258, y, 32, 25);
        put(NmPetugas, 300, y, 275, 25); y += 32;

        widget.Label judul = label("Asesmen Risiko Bunuh Diri (ASQ)");
        judul.setForeground(new java.awt.Color(170, 0, 0));
        put(judul, 10, y, 585, 20); y += 24;

        for (i = 0; i < 5; i++) {
            widget.Label q = new widget.Label();
            q.setText("<html>" + ASQ_PERTANYAAN[i] + "</html>");
            q.setFont(new java.awt.Font("Tahoma", 0, 11));
            put(q, 10, y, 425, 32);
            ASQ[i] = new widget.ComboBox();
            for (String o : YATIDAK) ASQ[i].addItem(o);
            ASQ[i].addActionListener(this::onJawab);
            put(ASQ[i], 440, y + 4, 95, 25);
            y += 34;
            if (i == 3) { put(label("Ket. Q4"), 20, y, 70, 22); put(Ket4, 95, y, 480, 22); y += 26; }
            if (i == 4) { put(label("Ket. Q5"), 20, y, 70, 22); put(Ket5, 95, y, 480, 22); y += 26; }
        }

        widget.Label note = new widget.Label();
        note.setText("<html><i>Catatan: bila Q1–Q4 ada satu jawaban \"Ya\", lanjutkan ke Q5.</i></html>");
        note.setFont(new java.awt.Font("Tahoma", 2, 10));
        put(note, 10, y, 585, 18); y += 22;

        put(label("Kesimpulan"), 10, y, 80, 25); put(Kesimpulan, 95, y, 480, 25); y += 30;
        FormInput.setPreferredSize(new Dimension(600, y + 10));
    }

    private void put(java.awt.Component c, int x, int y, int w, int h) {
        c.setBounds(x, y, w, h);
        FormInput.add(c);
    }

    private widget.ComboBox comboAngka(int n) {
        widget.ComboBox cb = new widget.ComboBox();
        for (int k = 0; k < n; k++) cb.addItem((k <= 9 ? "0" : "") + k);
        return cb;
    }

    private void onJawab(ActionEvent e) { hitungKesimpulan(); }

    private void pilihBaris(java.awt.event.MouseEvent evt) {
        if (evt.isPopupTrigger()) {
            int r = tbObat.rowAtPoint(evt.getPoint());
            if (r >= 0) { tbObat.setRowSelectionInterval(r, r); getData(); }
        }
    }

    private void cetakFormulir(ActionEvent evt) {
        if (tbObat.getSelectedRow() > -1) {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs()
                + "\nDitandatangani secara elektronik oleh " + tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString()
                + "\nID " + (finger.equals("") ? tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString() : finger)
                + "\n" + tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString());
            Valid.MyReportqry("rptSkriningASQ.jasper", "report", "::[ Formulir Skrining Risiko Bunuh Diri (ASQ) ]::",
                "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.tgl_lahir,pasien.jk,"
                + "skrining_asq.nip,petugas.nama,skrining_asq.tanggal,reg_periksa.umurdaftar,reg_periksa.sttsumur,"
                + "skrining_asq.asq1,skrining_asq.asq2,skrining_asq.asq3,skrining_asq.asq4,skrining_asq.asq4_deskripsi,"
                + "skrining_asq.asq5,skrining_asq.asq5_deskripsi,skrining_asq.kesimpulan from skrining_asq "
                + "inner join reg_periksa on skrining_asq.no_rawat=reg_periksa.no_rawat "
                + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                + "inner join petugas on skrining_asq.nip=petugas.nip "
                + "where reg_periksa.no_rawat='" + tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "' "
                + "and skrining_asq.tanggal='" + tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString() + "'", param);
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
        }
    }

    private String tgl(widget.Tanggal t) {
        try { return new SimpleDateFormat("yyyy-MM-dd").format(t.getDate()); }
        catch (Exception e) { return new SimpleDateFormat("yyyy-MM-dd").format(new Date()); }
    }

    private String waktu() {
        return tgl(Tanggal) + " " + Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem();
    }

    private String asq(int n) { return ASQ[n].getSelectedItem().toString(); }

    private void BtnSimpanActionPerformed(ActionEvent evt) {
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "pasien");
        } else if (KdPetugas.getText().trim().equals("") || NmPetugas.getText().trim().equals("")) {
            Valid.textKosong(KdPetugas, "Petugas");
        } else {
            if (akses.getkode().equals("Admin Utama")) {
                simpan();
            } else {
                if (TanggalRegistrasi.getText().equals("")) {
                    TanggalRegistrasi.setText(Sequel.cariIsi("select concat(reg_periksa.tgl_registrasi,' ',reg_periksa.jam_reg) from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText()));
                }
                if (Sequel.cekTanggalRegistrasi(TanggalRegistrasi.getText(), waktu()) == true) {
                    simpan();
                }
            }
        }
    }

    private void BtnEditActionPerformed(ActionEvent evt) {
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "pasien");
        } else if (KdPetugas.getText().trim().equals("") || NmPetugas.getText().trim().equals("")) {
            Valid.textKosong(KdPetugas, "Petugas");
        } else if (tbObat.getSelectedRow() > -1) {
            if (akses.bolehLewatBatasWaktu()) {
                ganti();
            } else if (KdPetugas.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString())) {
                if (Sequel.cekTanggal48jam(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString(), Sequel.ambiltanggalsekarang()) == true) {
                    ganti();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Hanya bisa diganti oleh petugas yang bersangkutan..!!");
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
        }
    }

    private void BtnHapusActionPerformed(ActionEvent evt) {
        if (tbObat.getSelectedRow() > -1) {
            if (akses.bolehLewatBatasWaktu()) {
                hapus();
            } else if (KdPetugas.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString())) {
                if (Sequel.cekTanggal48jam(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString(), Sequel.ambiltanggalsekarang()) == true) {
                    hapus();
                }
            } else {
                JOptionPane.showMessageDialog(null, "Hanya bisa dihapus oleh petugas yang bersangkutan..!!");
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
        }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            String q =
                "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.tgl_lahir,pasien.jk,skrining_asq.nip,petugas.nama,skrining_asq.tanggal,"
                + "skrining_asq.asq1,skrining_asq.asq2,skrining_asq.asq3,skrining_asq.asq4,skrining_asq.asq4_deskripsi,skrining_asq.asq5,skrining_asq.asq5_deskripsi,skrining_asq.kesimpulan "
                + "from skrining_asq inner join reg_periksa on skrining_asq.no_rawat=reg_periksa.no_rawat "
                + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis inner join petugas on skrining_asq.nip=petugas.nip ";
            if (TCari.getText().trim().equals("")) {
                ps = koneksi.prepareStatement(q + "where skrining_asq.tanggal between ? and ? order by skrining_asq.tanggal");
            } else {
                ps = koneksi.prepareStatement(q + "where skrining_asq.tanggal between ? and ? and (reg_periksa.no_rawat like ? or pasien.no_rkm_medis like ? or "
                    + "pasien.nm_pasien like ? or skrining_asq.nip like ? or petugas.nama like ?) order by skrining_asq.tanggal");
            }
            try {
                ps.setString(1, tgl(DTPCari1) + " 00:00:00");
                ps.setString(2, tgl(DTPCari2) + " 23:59:59");
                if (!TCari.getText().trim().equals("")) {
                    for (int k = 3; k <= 7; k++) ps.setString(k, "%" + TCari.getText() + "%");
                }
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"), rs.getString("tgl_lahir"), rs.getString("jk"),
                        rs.getString("nip"), rs.getString("nama"), rs.getString("tanggal"),
                        rs.getString("asq1"), rs.getString("asq2"), rs.getString("asq3"), rs.getString("asq4"), rs.getString("asq4_deskripsi"),
                        rs.getString("asq5"), rs.getString("asq5_deskripsi"), rs.getString("kesimpulan")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        LCount.setText("" + tabMode.getRowCount());
    }

    public void emptTeks() {
        for (i = 0; i < 5; i++) ASQ[i].setSelectedIndex(0);
        Ket4.setText(""); Ket5.setText("");
        Tanggal.setDate(new Date());
        hitungKesimpulan();
    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            int r = tbObat.getSelectedRow();
            TNoRw.setText(tbObat.getValueAt(r, 0).toString());
            TNoRM.setText(tbObat.getValueAt(r, 1).toString());
            TPasien.setText(tbObat.getValueAt(r, 2).toString());
            TglLahir.setText(tbObat.getValueAt(r, 3).toString());
            Jk.setText(tbObat.getValueAt(r, 4).toString());
            String t = tbObat.getValueAt(r, 7).toString();
            try {
                Jam.setSelectedItem(t.substring(11, 13));
                Menit.setSelectedItem(t.substring(14, 16));
                Detik.setSelectedItem(t.substring(17, 19));
            } catch (Exception e) { }
            ASQ[0].setSelectedItem(tbObat.getValueAt(r, 8).toString());
            ASQ[1].setSelectedItem(tbObat.getValueAt(r, 9).toString());
            ASQ[2].setSelectedItem(tbObat.getValueAt(r, 10).toString());
            ASQ[3].setSelectedItem(tbObat.getValueAt(r, 11).toString());
            Ket4.setText(tbObat.getValueAt(r, 12).toString());
            ASQ[4].setSelectedItem(tbObat.getValueAt(r, 13).toString());
            Ket5.setText(tbObat.getValueAt(r, 14).toString());
            Kesimpulan.setText(tbObat.getValueAt(r, 15).toString());
            Valid.SetTgl(Tanggal, t);
        }
    }

    private void isRawat() {
        try {
            ps = koneksi.prepareStatement(
                "select reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir,reg_periksa.tgl_registrasi,reg_periksa.jam_reg "
                + "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis where reg_periksa.no_rawat=?");
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    TPasien.setText(rs.getString("nm_pasien"));
                    Jk.setText(rs.getString("jk"));
                    TglLahir.setText(rs.getString("tgl_lahir"));
                    TanggalRegistrasi.setText(rs.getString("tgl_registrasi") + " " + rs.getString("jam_reg"));
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
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
        BtnSimpan.setEnabled(akses.getpenilaian_tambahan_bunuh_diri());
        BtnHapus.setEnabled(akses.getpenilaian_tambahan_bunuh_diri());
        BtnEdit.setEnabled(akses.getpenilaian_tambahan_bunuh_diri());
        BtnPrint.setEnabled(akses.getpenilaian_tambahan_bunuh_diri());
        if (akses.getjml2() >= 1) {
            KdPetugas.setEditable(false);
            btnPetugas.setEnabled(false);
            KdPetugas.setText(akses.getkode());
            NmPetugas.setText(petugas.tampil3(KdPetugas.getText()));
            if (NmPetugas.getText().equals("")) {
                KdPetugas.setText("");
                JOptionPane.showMessageDialog(null, "User login bukan petugas...!!");
            }
        }
        if (TANGGALMUNDUR.equals("no") && !akses.getkode().equals("Admin Utama")) {
            Tanggal.setEditable(false);
            Tanggal.setEnabled(false);
            ChkKejadian.setEnabled(false);
            Jam.setEnabled(false);
            Menit.setEnabled(false);
            Detik.setEnabled(false);
        }
    }

    private void jam() {
        ActionListener taskPerformer = new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                if (ChkKejadian.isSelected()) {
                    Calendar c = Calendar.getInstance();
                    int jm = c.get(Calendar.HOUR_OF_DAY), mn = c.get(Calendar.MINUTE), dt = c.get(Calendar.SECOND);
                    Jam.setSelectedItem((jm <= 9 ? "0" : "") + jm);
                    Menit.setSelectedItem((mn <= 9 ? "0" : "") + mn);
                    Detik.setSelectedItem((dt <= 9 ? "0" : "") + dt);
                }
            }
        };
        new javax.swing.Timer(1000, taskPerformer).start();
    }

    private void isForm() {
        boolean tampil = ChkInput.isSelected();
        scrollInput.setVisible(tampil);
        PanelInput.setPreferredSize(new Dimension(WIDTH, tampil ? 430 : 26));
        PanelInput.revalidate();
        if (internalFrame1 != null) { internalFrame1.revalidate(); internalFrame1.repaint(); }
    }

    private void ganti() {
        if (Sequel.mengedittf("skrining_asq", "no_rawat=?",
            "no_rawat=?,tanggal=?,nip=?,asq1=?,asq2=?,asq3=?,asq4=?,asq4_deskripsi=?,asq5=?,asq5_deskripsi=?,kesimpulan=?", 12,
            new String[]{
                TNoRw.getText(), waktu(), KdPetugas.getText(),
                asq(0), asq(1), asq(2), asq(3), Ket4.getText(), asq(4), Ket5.getText(), Kesimpulan.getText(),
                tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
            }) == true) {
            int r = tbObat.getSelectedRow();
            tbObat.setValueAt(TNoRw.getText(), r, 0);
            tbObat.setValueAt(TNoRM.getText(), r, 1);
            tbObat.setValueAt(TPasien.getText(), r, 2);
            tbObat.setValueAt(TglLahir.getText(), r, 3);
            tbObat.setValueAt(Jk.getText(), r, 4);
            tbObat.setValueAt(KdPetugas.getText(), r, 5);
            tbObat.setValueAt(NmPetugas.getText(), r, 6);
            tbObat.setValueAt(waktu(), r, 7);
            tbObat.setValueAt(asq(0), r, 8);
            tbObat.setValueAt(asq(1), r, 9);
            tbObat.setValueAt(asq(2), r, 10);
            tbObat.setValueAt(asq(3), r, 11);
            tbObat.setValueAt(Ket4.getText(), r, 12);
            tbObat.setValueAt(asq(4), r, 13);
            tbObat.setValueAt(Ket5.getText(), r, 14);
            tbObat.setValueAt(Kesimpulan.getText(), r, 15);
            emptTeks();
        }
    }

    private void hapus() {
        if (Sequel.queryu2tf("delete from skrining_asq where no_rawat=?", 1, new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
        }) == true) {
            tabMode.removeRow(tbObat.getSelectedRow());
            LCount.setText("" + tabMode.getRowCount());
            emptTeks();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
        }
    }

    private void simpan() {
        if (Sequel.menyimpantf("skrining_asq", "?,?,?,?,?,?,?,?,?,?,?", "Data", 11, new String[]{
            TNoRw.getText(), waktu(), KdPetugas.getText(),
            asq(0), asq(1), asq(2), asq(3), Ket4.getText(), asq(4), Ket5.getText(), Kesimpulan.getText()
        }) == true) {
            tabMode.addRow(new String[]{
                TNoRw.getText(), TNoRM.getText(), TPasien.getText(), TglLahir.getText(), Jk.getText(), KdPetugas.getText(), NmPetugas.getText(), waktu(),
                asq(0), asq(1), asq(2), asq(3), Ket4.getText(), asq(4), Ket5.getText(), Kesimpulan.getText()
            });
            LCount.setText("" + tabMode.getRowCount());
            emptTeks();
        }
    }

    private void hitungKesimpulan() {
        boolean positif = asq(0).equals("Ya") || asq(1).equals("Ya") || asq(2).equals("Ya") || asq(3).equals("Ya");
        String k;
        if (!positif) {
            k = "Skrining Negatif (tidak ada risiko bunuh diri saat ini)";
        } else if (asq(4).equals("Ya")) {
            k = "Positif AKUT - evaluasi keselamatan segera, jangan tinggalkan pasien sendiri";
        } else {
            k = "Positif Non-Akut - perlu evaluasi kesehatan jiwa lebih lanjut";
        }
        Kesimpulan.setText(k);
    }
}
