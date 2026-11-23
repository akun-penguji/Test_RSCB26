/*
 * RMSkriningSKILAS — Skrining Geriatri / Skrining Lansia Sederhana (SKILAS), Kemenkes.
 * UI/UX mengikuti RMSkriningPHQ9 (widget.InternalFrame, toolbar, widget.Table,
 * toggle ChkInput). Enam domain kapasitas intrinsik; tiap "tanda" = satu kotak
 * hasil yang dicentang. Aturan Kemenkes: jika ada >=1 kotak tercentang ->
 * RUJUK PUSKESMAS.
 * Hak akses memakai akses.getskrining_instrumen_srq() (tanpa hak akses baru).
 * Disimpan ke tabel skrining_skilas (no_rawat, tanggal, nip, 12 tanda,
 * jumlah_tanda, kesimpulan).
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

public final class RMSkriningSKILAS extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);
    private String TANGGALMUNDUR = "yes";

    // 12 tanda dalam 6 domain. Urutan kolom DB = urutan di sini (index 0..11).
    private static final String[] TANDA = {
        "Salah pada salah satu pertanyaan orientasi (tanggal / tempat)",
        "Tidak dapat mengulang ketiga kata (bunga, pintu, nasi)",
        "Tidak dapat berdiri dari kursi 5x tanpa tangan dalam 14 detik",
        "BB berkurang >3 kg dalam 3 bulan / pakaian menjadi longgar",
        "Hilang nafsu makan atau kesulitan makan (tersedak / pakai sonde)",
        "Lingkar lengan atas (LiLA) < 21 cm",
        "Ada masalah pada mata (sulit lihat/baca, penyakit mata, DM, hipertensi)",
        "Tes MELIHAT gagal (kemungkinan gangguan penglihatan berat hingga buta)",
        "Tidak mendengar bisikan saat Tes BISIK",
        "Tes BISIK tidak dapat dilakukan (rujuk Puskesmas)",
        "Merasa sedih, tertekan, atau putus asa (2 minggu terakhir)",
        "Sedikit minat atau kesenangan dalam melakukan sesuatu"
    };
    // Judul domain yang muncul SEBELUM index tanda tertentu.
    private static final String[][] DOMAIN = {
        {"0", "1. PENURUNAN KOGNITIF"},
        {"2", "2. KETERBATASAN MOBILISASI"},
        {"3", "3. MALNUTRISI"},
        {"6", "4. GANGGUAN PENGLIHATAN"},
        {"8", "5. GANGGUAN PENDENGARAN"},
        {"10", "6. GEJALA DEPRESI"}
    };

    private widget.InternalFrame internalFrame1;
    private widget.ScrollPane Scroll, scrollInput;
    private widget.Table tbObat;
    private widget.panelisi panelTombol, panelCari;
    private widget.PanelBiasa FormInput;
    private javax.swing.JPanel PanelInput;
    private widget.TextBox TNoRw, TNoRM, TPasien, TglLahir, Jk, KdPetugas, NmPetugas, Kesimpulan, TotalNilai, TCari, TanggalRegistrasi;
    private widget.Tanggal Tanggal, DTPCari1, DTPCari2;
    private widget.ComboBox Jam, Menit, Detik;
    private widget.CekBox[] CHK = new widget.CekBox[12];
    private widget.CekBox ChkKejadian, ChkInput;
    private widget.Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll, btnPetugas;
    private widget.Label LCount;
    private javax.swing.JPopupMenu popupCetak;
    private javax.swing.JMenuItem MnCetakFormulir;
    private String finger = "";

    public RMSkriningSKILAS(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        setTitle("Skrining Geriatri :: Skrining Lansia Sederhana (SKILAS)");

        Object[] kolom = new Object[8 + 12 + 2];
        String[] head = {"No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "J.K.", "Kode Petugas", "Nama Petugas", "Tanggal"};
        for (int k = 0; k < 8; k++) kolom[k] = head[k];
        for (int k = 0; k < 12; k++) kolom[8 + k] = "T" + (k + 1);
        kolom[20] = "Jml"; kolom[21] = "Kesimpulan";
        tabMode = new DefaultTableModel(null, kolom) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        initComponents();
        setSize(628, 674);
        setLocation(8, 1);

        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(widget.Table.AUTO_RESIZE_OFF);
        int[] lebar = {105, 70, 150, 65, 35, 80, 150, 115, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 35, 250};
        for (i = 0; i < lebar.length; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            column.setPreferredWidth(lebar[i]);
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        KdPetugas.setDocument(new batasInput((byte) 20).getKata(KdPetugas));
        Kesimpulan.setDocument(new batasInput((int) 100).getKata(Kesimpulan));
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
            "::[ Skrining Geriatri - Skrining Lansia Sederhana (SKILAS) ]::",
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
        } catch (Exception e) { /* ikon opsional */ }
        ChkInput.addActionListener(e -> isForm());
        buildFormInput();
        scrollInput.setViewportView(FormInput);
        scrollInput.setPreferredSize(new Dimension(WIDTH, 430));
        PanelInput.add(scrollInput, java.awt.BorderLayout.CENTER);
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);
        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        popupCetak = new javax.swing.JPopupMenu();
        MnCetakFormulir = new javax.swing.JMenuItem("Cetak Formulir SKILAS");
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
        try { b.setIcon(new javax.swing.ImageIcon(getClass().getResource(ikon))); } catch (Exception e) { /* ikon opsional */ }
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
        TotalNilai = new widget.TextBox(); TotalNilai.setText("0"); TotalNilai.setEditable(false);
        TotalNilai.setFont(new java.awt.Font("Tahoma", 1, 12));
        Kesimpulan = new widget.TextBox();
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
        put(ChkKejadian, 475, y, 120, 25); y += 30;

        widget.Label judul = label("Centang tiap kotak HASIL yang terpenuhi. Jika ada >=1 centang -> RUJUK PUSKESMAS.");
        judul.setForeground(new java.awt.Color(170, 0, 0));
        put(judul, 10, y, 585, 20); y += 24;

        for (i = 0; i < 12; i++) {
            // Judul domain sebelum tanda tertentu.
            for (String[] d : DOMAIN) {
                if (Integer.parseInt(d[0]) == i) {
                    widget.Label dl = label(d[1]);
                    dl.setForeground(new java.awt.Color(0, 90, 60));
                    put(dl, 10, y, 585, 18); y += 20;
                }
            }
            CHK[i] = new widget.CekBox();
            CHK[i].setText("<html>" + TANDA[i] + "</html>");
            CHK[i].setFont(new java.awt.Font("Tahoma", 0, 11));
            CHK[i].addActionListener(this::onJawab);
            put(CHK[i], 24, y, 571, 20); y += 24;
        }

        y += 4;
        put(label("Jumlah Tanda"), 10, y, 95, 25); put(TotalNilai, 110, y, 50, 25);
        put(label("Kesimpulan"), 170, y, 75, 25); put(Kesimpulan, 250, y, 335, 25); y += 30;
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

    private void onJawab(ActionEvent e) { isTotal(); }

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
            Valid.MyReportqry("rptSkriningSKILAS.jasper", "report", "::[ Formulir Skrining Lansia Sederhana (SKILAS) ]::",
                "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.tgl_lahir,pasien.jk,"
                + "skrining_skilas.nip,petugas.nama,skrining_skilas.tanggal,reg_periksa.umurdaftar,reg_periksa.sttsumur,"
                + "skrining_skilas.kognitif_orientasi,skrining_skilas.kognitif_ulang_kata,skrining_skilas.mobilisasi_tidak_mampu,"
                + "skrining_skilas.malnutrisi_bb,skrining_skilas.malnutrisi_makan,skrining_skilas.malnutrisi_lila,"
                + "skrining_skilas.penglihatan_masalah,skrining_skilas.penglihatan_tes,"
                + "skrining_skilas.pendengaran_tidak_dengar,skrining_skilas.pendengaran_tak_bisa_tes,"
                + "skrining_skilas.depresi_sedih,skrining_skilas.depresi_minat,"
                + "skrining_skilas.jumlah_tanda,skrining_skilas.kesimpulan from skrining_skilas "
                + "inner join reg_periksa on skrining_skilas.no_rawat=reg_periksa.no_rawat "
                + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                + "inner join petugas on skrining_skilas.nip=petugas.nip "
                + "where reg_periksa.no_rawat='" + tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "' "
                + "and skrining_skilas.tanggal='" + tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString() + "'", param);
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
        }
    }

    private void isForm() {
        boolean tampil = ChkInput.isSelected();
        scrollInput.setVisible(tampil);
        PanelInput.setPreferredSize(new Dimension(WIDTH, tampil ? 456 : 26));
        PanelInput.revalidate();
        if (internalFrame1 != null) { internalFrame1.revalidate(); internalFrame1.repaint(); }
    }

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

    private String tgl(widget.Tanggal t) {
        try { return new SimpleDateFormat("yyyy-MM-dd").format(t.getDate()); }
        catch (Exception e) { return new SimpleDateFormat("yyyy-MM-dd").format(new Date()); }
    }

    private String waktu() {
        return tgl(Tanggal) + " " + Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem();
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            String q =
                "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.tgl_lahir,pasien.jk,skrining_skilas.nip,petugas.nama,skrining_skilas.tanggal,"
                + "skrining_skilas.kognitif_orientasi,skrining_skilas.kognitif_ulang_kata,skrining_skilas.mobilisasi_tidak_mampu,"
                + "skrining_skilas.malnutrisi_bb,skrining_skilas.malnutrisi_makan,skrining_skilas.malnutrisi_lila,"
                + "skrining_skilas.penglihatan_masalah,skrining_skilas.penglihatan_tes,"
                + "skrining_skilas.pendengaran_tidak_dengar,skrining_skilas.pendengaran_tak_bisa_tes,"
                + "skrining_skilas.depresi_sedih,skrining_skilas.depresi_minat,"
                + "skrining_skilas.jumlah_tanda,skrining_skilas.kesimpulan from skrining_skilas "
                + "inner join reg_periksa on skrining_skilas.no_rawat=reg_periksa.no_rawat "
                + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis inner join petugas on skrining_skilas.nip=petugas.nip ";
            if (TCari.getText().trim().equals("")) {
                ps = koneksi.prepareStatement(q + "where skrining_skilas.tanggal between ? and ? order by skrining_skilas.tanggal");
            } else {
                ps = koneksi.prepareStatement(q + "where skrining_skilas.tanggal between ? and ? and (reg_periksa.no_rawat like ? or pasien.no_rkm_medis like ? or "
                    + "pasien.nm_pasien like ? or skrining_skilas.nip like ? or petugas.nama like ?) order by skrining_skilas.tanggal");
            }
            try {
                ps.setString(1, tgl(DTPCari1) + " 00:00:00");
                ps.setString(2, tgl(DTPCari2) + " 23:59:59");
                if (!TCari.getText().trim().equals("")) {
                    for (int k = 3; k <= 7; k++) ps.setString(k, "%" + TCari.getText() + "%");
                }
                rs = ps.executeQuery();
                while (rs.next()) {
                    String[] baris = new String[22];
                    baris[0] = rs.getString("no_rawat"); baris[1] = rs.getString("no_rkm_medis");
                    baris[2] = rs.getString("nm_pasien"); baris[3] = rs.getString("tgl_lahir");
                    baris[4] = rs.getString("jk"); baris[5] = rs.getString("nip");
                    baris[6] = rs.getString("nama"); baris[7] = rs.getString("tanggal");
                    for (int k = 0; k < 12; k++) baris[8 + k] = rs.getString(9 + k);
                    baris[20] = rs.getString("jumlah_tanda"); baris[21] = rs.getString("kesimpulan");
                    tabMode.addRow(baris);
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
        for (i = 0; i < 12; i++) CHK[i].setSelected(false);
        TotalNilai.setText("0");
        Kesimpulan.setText("");
        Tanggal.setDate(new Date());
    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            int r = tbObat.getSelectedRow();
            TNoRw.setText(tbObat.getValueAt(r, 0).toString());
            TNoRM.setText(tbObat.getValueAt(r, 1).toString());
            TPasien.setText(tbObat.getValueAt(r, 2).toString());
            TglLahir.setText(tbObat.getValueAt(r, 3).toString());
            Jk.setText(tbObat.getValueAt(r, 4).toString());
            String tgl = tbObat.getValueAt(r, 7).toString();
            try {
                Jam.setSelectedItem(tgl.substring(11, 13));
                Menit.setSelectedItem(tgl.substring(14, 16));
                Detik.setSelectedItem(tgl.substring(17, 19));
            } catch (Exception e) { /* abaikan */ }
            for (i = 0; i < 12; i++) {
                CHK[i].setSelected("1".equals(("" + tbObat.getValueAt(r, 8 + i)).trim()));
            }
            TotalNilai.setText(tbObat.getValueAt(r, 20).toString());
            Kesimpulan.setText(tbObat.getValueAt(r, 21).toString());
            Valid.SetTgl(Tanggal, tgl);
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
        BtnSimpan.setEnabled(akses.getskrining_instrumen_srq());
        BtnHapus.setEnabled(akses.getskrining_instrumen_srq());
        BtnEdit.setEnabled(akses.getskrining_instrumen_srq());
        BtnPrint.setEnabled(akses.getskrining_instrumen_srq());
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

    private void ganti() {
        if (Sequel.mengedittf("skrining_skilas", "no_rawat=?",
            "no_rawat=?,tanggal=?,nip=?,kognitif_orientasi=?,kognitif_ulang_kata=?,mobilisasi_tidak_mampu=?,"
            + "malnutrisi_bb=?,malnutrisi_makan=?,malnutrisi_lila=?,penglihatan_masalah=?,penglihatan_tes=?,"
            + "pendengaran_tidak_dengar=?,pendengaran_tak_bisa_tes=?,depresi_sedih=?,depresi_minat=?,"
            + "jumlah_tanda=?,kesimpulan=?", 18,
            new String[]{
                TNoRw.getText(), waktu(), KdPetugas.getText(),
                v(0), v(1), v(2), v(3), v(4), v(5), v(6), v(7), v(8), v(9), v(10), v(11),
                TotalNilai.getText(), Kesimpulan.getText(),
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
            for (i = 0; i < 12; i++) tbObat.setValueAt(v(i), r, 8 + i);
            tbObat.setValueAt(TotalNilai.getText(), r, 20);
            tbObat.setValueAt(Kesimpulan.getText(), r, 21);
            emptTeks();
        }
    }

    private void hapus() {
        if (Sequel.queryu2tf("delete from skrining_skilas where no_rawat=?", 1, new String[]{
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
        if (Sequel.menyimpantf("skrining_skilas", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 17, new String[]{
            TNoRw.getText(), waktu(), KdPetugas.getText(),
            v(0), v(1), v(2), v(3), v(4), v(5), v(6), v(7), v(8), v(9), v(10), v(11),
            TotalNilai.getText(), Kesimpulan.getText()
        }) == true) {
            String[] baris = new String[22];
            baris[0] = TNoRw.getText(); baris[1] = TNoRM.getText(); baris[2] = TPasien.getText();
            baris[3] = TglLahir.getText(); baris[4] = Jk.getText(); baris[5] = KdPetugas.getText();
            baris[6] = NmPetugas.getText(); baris[7] = waktu();
            for (int k = 0; k < 12; k++) baris[8 + k] = v(k);
            baris[20] = TotalNilai.getText(); baris[21] = Kesimpulan.getText();
            tabMode.addRow(baris);
            LCount.setText("" + tabMode.getRowCount());
            emptTeks();
        }
    }

    private String v(int n) { return CHK[n].isSelected() ? "1" : "0"; }

    private void isTotal() {
        int total = 0;
        for (i = 0; i < 12; i++) if (CHK[i].isSelected()) total++;
        TotalNilai.setText("" + total);
        // Auto-isi kesimpulan sesuai aturan Kemenkes; petugas masih boleh menimpa.
        Kesimpulan.setText(total >= 1
            ? "RUJUK PUSKESMAS (ada " + total + " tanda penurunan kapasitas intrinsik)"
            : "Tidak ada tanda — tidak perlu rujuk");
    }
}
