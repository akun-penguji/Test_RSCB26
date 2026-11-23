package rekammedis;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import kepegawaian.DlgCariPetugas;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import widget.Button;
import widget.ComboBox;
import widget.InternalFrame;
import widget.Label;
import widget.PanelBiasa;
import widget.ScrollPane;
import widget.TextBox;
import widget.panelisi;

/**
 * Asesmen Menjelang Akhir Kehidupan (Terminal) / Paliatif.
 * Skoring gejala (7 gejala 0-10, total 0-70) + rekomendasi paket asuhan otomatis,
 * skor keparahan masalah, orientasi & keprihatinan spiritual, status psikososial,
 * dan kebutuhan bantuan pelayanan. Tabel: asesmen_akhir_kehidupan_terminal.
 */
public class RMAsesmenAkhirKehidupanTerminal extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2;
    private TextBox TCari;
    private Label LCount;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;

    private TextBox TNoRw, TNoRM, TPasien;
    private widget.Tanggal Tgl;

    // A. Skala Penilaian Gejala
    private ComboBox[] cmbGejala;               // 7 gejala skor 0-10
    private Label LTotalSkor, LKategori;
    private TextBox txtPaket;
    // B. Skor Keparahan Masalah
    private ComboBox cmbBNyeri, cmbBGejala, cmbBPsiko, cmbBKeluarga;
    private TextBox txtBNyeriPaket, txtBGejalaPaket, txtBPsikoPaket, txtBKeluargaPaket;
    // C. Orientasi Spiritual
    private ComboBox cmbSpiritualButuh, cmbKepercayaan;
    // D. Keprihatinan Spiritual
    private ComboBox cmbKeprihatinan;
    // E. Status Psikososial
    private ComboBox cmbHubungan, cmbKelayakan, cmbPerawatanRumah, cmbReaksi;
    // F. Kebutuhan Bantuan Pelayanan
    private ComboBox cmbPihak, cmbAlternatif, cmbRisiko, cmbPotensi, cmbKeputusan;
    // Petugas
    private TextBox NIP, NmPetugas;
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);

    private widget.Table tbObat;
    private javax.swing.table.DefaultTableModel tabMode;
    private ScrollPane Scroll;

    private PanelBiasa FormInput;
    private ScrollPane ScrollInput;
    private javax.swing.JTabbedPane TabData;

    private final String[] GEJALA = {"Kesulitan tidur", "Kesulitan makan", "Mual", "Gangguan usus",
        "Kesulitan bernafas", "Kelelahan", "Skala nyeri"};

    public RMAsesmenAkhirKehidupanTerminal(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(1000, 640);
        setLocationRelativeTo(null);
    }

    // ---- helpers ----
    private ComboBox combo(String[] items) {
        ComboBox c = new ComboBox();
        c.setModel(new DefaultComboBoxModel(items));
        return c;
    }
    private ComboBox comboSkor() {
        return combo(new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
    }
    private Label lbl(String txt, int x, int y, int w) {
        Label l = new Label(); l.setText(txt); l.setBounds(x, y, w, 23); return l;
    }
    private JLabel header(String txt, int y) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Tahoma", 1, 11));
        l.setForeground(new Color(0, 70, 140));
        l.setBounds(10, y, 820, 20);
        return l;
    }
    private String cv(ComboBox c) { return c.getSelectedItem() == null ? "" : c.getSelectedItem().toString(); }
    private String tglValue() {
        return Valid.SetTgl(Tgl.getSelectedItem() + "") + " " + Tgl.getSelectedItem().toString().substring(11, 19);
    }

    private void initComponents() {
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Asesmen Menjelang Akhir Kehidupan (Terminal) ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // PANEL ATAS
        panelisi panelAtas = new panelisi();
        panelAtas.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 4));
        panelAtas.setPreferredSize(new Dimension(1024, 33));
        Label lblNoRw = new Label(); lblNoRw.setText("No.Rawat :");
        TNoRw = new TextBox(); TNoRw.setEditable(false); TNoRw.setPreferredSize(new Dimension(130, 23));
        TNoRM = new TextBox(); TNoRM.setEditable(false); TNoRM.setPreferredSize(new Dimension(100, 23));
        TPasien = new TextBox(); TPasien.setEditable(false); TPasien.setPreferredSize(new Dimension(220, 23));
        panelAtas.add(lblNoRw); panelAtas.add(TNoRw); panelAtas.add(TNoRM); panelAtas.add(TPasien);

        // TABLE (Data Input)
        Scroll = new ScrollPane();
        tbObat = new widget.Table();
        tabMode = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat", "No.R.M.", "Nama Pasien", "Tanggal", "Total Skor", "Kategori", "Keputusan", "Petugas"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbObat.setModel(tabMode);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mousePressed(java.awt.event.MouseEvent evt) {
                if (javax.swing.SwingUtilities.isRightMouseButton(evt) || evt.isPopupTrigger()) {
                    int r = tbObat.rowAtPoint(evt.getPoint());
                    if (r >= 0) { tbObat.setRowSelectionInterval(r, r); try { getData(); } catch (Exception e) {} }
                }
            }
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (javax.swing.SwingUtilities.isLeftMouseButton(evt) && tabMode.getRowCount() != 0) {
                    try { getData(); } catch (Exception e) {}
                }
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent evt) {
                if (tabMode.getRowCount() == 0) return;
                if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                    try { getData(); } catch (Exception e) {}
                    TabData.setSelectedIndex(0);
                } else if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN || evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    try { getData(); } catch (Exception e) {}
                }
            }
        });
        javax.swing.JPopupMenu popupMenu = new javax.swing.JPopupMenu();
        javax.swing.JMenuItem mnCetak = new javax.swing.JMenuItem("Cetak Jasper");
        mnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetak.addActionListener(evt -> cetak());
        popupMenu.add(mnCetak);
        tbObat.setComponentPopupMenu(popupMenu);
        Scroll.setViewportView(tbObat);

        // FORM INPUT (absolute layout)
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(960, 1050));
        FormInput.setLayout(null);
        int lx = 10, fx = 200, y = 8;

        Tgl = new widget.Tanggal(); Tgl.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        FormInput.add(lbl("Tanggal Asesmen :", lx, y, 170));
        Tgl.setBounds(fx, y, 150, 23); FormInput.add(Tgl);

        // A. SKALA PENILAIAN GEJALA
        y += 30; FormInput.add(header("A. Skala Penilaian Gejala (skor 0-10; 0=Tidak Mengganggu, 10=Sangat Mengganggu)", y));
        cmbGejala = new ComboBox[GEJALA.length];
        for (int i = 0; i < GEJALA.length; i++) {
            y += 25;
            FormInput.add(lbl((i + 1) + ". " + GEJALA[i] + " :", lx, y, 190));
            cmbGejala[i] = comboSkor();
            cmbGejala[i].setBounds(fx, y, 60, 23);
            cmbGejala[i].addActionListener(evt -> hitungSkorA());
            FormInput.add(cmbGejala[i]);
        }
        y += 28;
        FormInput.add(lbl("Total Skor (0-70) :", lx, y, 190));
        LTotalSkor = new Label(); LTotalSkor.setText("0"); LTotalSkor.setFont(new Font("Tahoma", 1, 12));
        LTotalSkor.setBounds(fx, y, 60, 23); FormInput.add(LTotalSkor);
        FormInput.add(lbl("Kategori :", fx + 80, y, 60));
        LKategori = new Label(); LKategori.setText("Ringan / Terkontrol"); LKategori.setFont(new Font("Tahoma", 1, 12));
        LKategori.setForeground(new Color(0, 120, 0));
        LKategori.setBounds(fx + 145, y, 250, 23); FormInput.add(LKategori);
        y += 26;
        FormInput.add(lbl("Kesimpulan Tata Laksana :", lx, y, 190));
        txtPaket = new TextBox(); txtPaket.setEditable(false);
        txtPaket.setText("PAKET ASUHAN PALIATIF STANDAR DAN MONITORING BERKALA");
        txtPaket.setBounds(fx, y, 620, 23); FormInput.add(txtPaket);

        // B. SKOR KEPARAHAN MASALAH
        y += 32; FormInput.add(header("B. Skor Keparahan Masalah (24 jam) — pilih kondisi, paket rencana otomatis", y));
        y += 25;
        FormInput.add(lbl("Nyeri :", lx, y, 190));
        cmbBNyeri = combo(new String[]{"", "Tidak Nyeri", "Nyeri Ringan", "Nyeri Sedang", "Nyeri Berat / Hebat"});
        cmbBNyeri.setBounds(fx, y, 260, 23); FormInput.add(cmbBNyeri);
        txtBNyeriPaket = new TextBox(); txtBNyeriPaket.setEditable(false); txtBNyeriPaket.setBounds(fx + 270, y, 350, 23); FormInput.add(txtBNyeriPaket);
        cmbBNyeri.addActionListener(evt -> txtBNyeriPaket.setText(paketNyeri(cv(cmbBNyeri))));
        y += 25;
        FormInput.add(lbl("Gejala Lain :", lx, y, 190));
        cmbBGejala = combo(new String[]{"",
            "Sesak napas berat / Gasping",
            "Suara napas berisik/ngorok (Death Rattle)",
            "Mual muntah berat / tidak dapat makan",
            "Gelisah ekstrem/meracau/ketakutan (Delirium)",
            "Konstipasi berat / tidak BAB >3 hari"});
        cmbBGejala.setBounds(fx, y, 260, 23); FormInput.add(cmbBGejala);
        txtBGejalaPaket = new TextBox(); txtBGejalaPaket.setEditable(false); txtBGejalaPaket.setBounds(fx + 270, y, 350, 23); FormInput.add(txtBGejalaPaket);
        cmbBGejala.addActionListener(evt -> txtBGejalaPaket.setText(paketGejala(cv(cmbBGejala))));
        y += 25;
        FormInput.add(lbl("Psikologis / Spiritual :", lx, y, 190));
        cmbBPsiko = combo(new String[]{"",
            "Kooperatif, tenang, menerima (Stabil)",
            "Cemas, sedih mendalam, menarik diri (Distres Ringan-Sedang)",
            "Denial, marah, depresi berat, histeris (Krisis Psikologis)"});
        cmbBPsiko.setBounds(fx, y, 260, 23); FormInput.add(cmbBPsiko);
        txtBPsikoPaket = new TextBox(); txtBPsikoPaket.setEditable(false); txtBPsikoPaket.setBounds(fx + 270, y, 350, 23); FormInput.add(txtBPsikoPaket);
        cmbBPsiko.addActionListener(evt -> txtBPsikoPaket.setText(paketPsiko(cv(cmbBPsiko))));
        y += 25;
        FormInput.add(lbl("Keluarga / Perawat :", lx, y, 190));
        cmbBKeluarga = combo(new String[]{"",
            "Memahami, suportif, terlibat",
            "Kelelahan fisik/mental, cemas, konflik internal",
            "Duka cita mendalam, histeris, menolak melepas (Krisis Koping)"});
        cmbBKeluarga.setBounds(fx, y, 260, 23); FormInput.add(cmbBKeluarga);
        txtBKeluargaPaket = new TextBox(); txtBKeluargaPaket.setEditable(false); txtBKeluargaPaket.setBounds(fx + 270, y, 350, 23); FormInput.add(txtBKeluargaPaket);
        cmbBKeluarga.addActionListener(evt -> txtBKeluargaPaket.setText(paketKeluarga(cv(cmbBKeluarga))));

        // C. ORIENTASI SPIRITUAL
        y += 32; FormInput.add(header("C. Orientasi Spiritual Pasien dan Keluarga", y));
        y += 25;
        FormInput.add(lbl("Butuh pelayanan spiritual/rohani :", lx, y, 190));
        cmbSpiritualButuh = combo(new String[]{"", "Ya", "Tidak", "Belum dapat dinilai / Pasien tidak sadar"});
        cmbSpiritualButuh.setBounds(fx, y, 300, 23); FormInput.add(cmbSpiritualButuh);
        y += 25;
        FormInput.add(lbl("Jenis kepercayaan :", lx, y, 190));
        cmbKepercayaan = combo(new String[]{"", "Islam", "Kristen (Protestan)", "Katolik", "Hindu", "Buddha", "Khonghucu / Lainnya"});
        cmbKepercayaan.setBounds(fx, y, 300, 23); FormInput.add(cmbKepercayaan);

        // D. KEPRIHATINAN SPIRITUAL
        y += 32; FormInput.add(header("D. Keprihatinan Spiritual Pasien dan Keluarganya", y));
        y += 25;
        FormInput.add(lbl("Putus asa/menderita/bersalah :", lx, y, 190));
        cmbKeprihatinan = combo(new String[]{"", "Tidak Ada", "Ya, Ringan - Sedang", "Ya, Berat / Distress Spiritual"});
        cmbKeprihatinan.setBounds(fx, y, 300, 23); FormInput.add(cmbKeprihatinan);

        // E. STATUS PSIKOSOSIAL
        y += 32; FormInput.add(header("E. Status Psikososial Pasien/Keluarga", y));
        y += 25;
        FormInput.add(lbl("Hubungan dengan keluarga :", lx, y, 190));
        cmbHubungan = combo(new String[]{"", "Harmonis / Baik", "Ada Konflik Ringan", "Disharmonis / Konflik Berat"});
        cmbHubungan.setBounds(fx, y, 300, 23); FormInput.add(cmbHubungan);
        y += 25;
        FormInput.add(lbl("Kelayakan tempat tinggal :", lx, y, 190));
        cmbKelayakan = combo(new String[]{"", "Mendukung Perawatan", "Ada Hambatan Budaya/Adat", "Kendala Geografis / Lingkungan"});
        cmbKelayakan.setBounds(fx, y, 300, 23); FormInput.add(cmbKelayakan);
        y += 25;
        FormInput.add(lbl("Perawatan pasien di rumah :", lx, y, 190));
        cmbPerawatanRumah = combo(new String[]{"", "Siap & Mampu", "Siap dengan Bantuan", "Tidak Siap / Tidak Mampu"});
        cmbPerawatanRumah.setBounds(fx, y, 300, 23); FormInput.add(cmbPerawatanRumah);
        y += 25;
        FormInput.add(lbl("Reaksi terhadap penyakit :", lx, y, 190));
        cmbReaksi = combo(new String[]{"", "Penerimaan (Acceptance)", "Penolakan / Marah (Denial / Anger)", "Tawar-menawar / Depresi (Bargaining / Depression)"});
        cmbReaksi.setBounds(fx, y, 300, 23); FormInput.add(cmbReaksi);

        // F. KEBUTUHAN BANTUAN PELAYANAN
        y += 32; FormInput.add(header("F. Kebutuhan Bantuan Pelayanan / Penundaan Pelayanan", y));
        y += 25;
        FormInput.add(lbl("Pihak yang dihubungi :", lx, y, 190));
        cmbPihak = combo(new String[]{"", "Suami / Istri", "Anak Kandung", "Orang Tua", "Wali / Keluarga Terdekat Lainnya"});
        cmbPihak.setBounds(fx, y, 300, 23); FormInput.add(cmbPihak);
        y += 25;
        FormInput.add(lbl("Kebutuhan alternatif pelayanan :", lx, y, 190));
        cmbAlternatif = combo(new String[]{"", "Tidak Ada", "Alih Rawat ke Ruang Intensif", "Pindah ke Fasilitas Palliative Care / Hospis Khusus", "Pulang untuk Perawatan Rumah"});
        cmbAlternatif.setBounds(fx, y, 300, 23); FormInput.add(cmbAlternatif);
        y += 25;
        FormInput.add(lbl("Faktor risiko keluarga :", lx, y, 190));
        cmbRisiko = combo(new String[]{"", "Risiko Rendah", "Risiko Sedang", "Risiko Tinggi"});
        cmbRisiko.setBounds(fx, y, 300, 23); FormInput.add(cmbRisiko);
        y += 25;
        FormInput.add(lbl("Potensi reaksi patologis :", lx, y, 190));
        cmbPotensi = combo(new String[]{"", "Antisipasi Reaksi Normal", "Antisipasi Reaksi Histeris / Denial Berat", "Antisipasi Reaksi Agresif / Menyalahkan Tenaga Medis"});
        cmbPotensi.setBounds(fx, y, 300, 23); FormInput.add(cmbPotensi);
        y += 25;
        FormInput.add(lbl("Pengambilan keputusan asuhan :", lx, y, 190));
        cmbKeputusan = combo(new String[]{"", "Full Code", "DNR", "Comfort Care Only"});
        cmbKeputusan.setBounds(fx, y, 300, 23); FormInput.add(cmbKeputusan);

        // PETUGAS
        y += 32; FormInput.add(header("Petugas Penilai (Perawat)", y));
        y += 25;
        FormInput.add(lbl("NIP :", lx, y, 190));
        NIP = new TextBox(); NIP.setBounds(fx, y, 150, 23); FormInput.add(NIP);
        Button BtnPetugas = new Button();
        BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPetugas.setToolTipText("Cari Petugas");
        BtnPetugas.setBounds(fx + 152, y, 28, 23); FormInput.add(BtnPetugas);
        NmPetugas = new TextBox(); NmPetugas.setEditable(false); NmPetugas.setBounds(fx + 184, y, 250, 23); FormInput.add(NmPetugas);
        NIP.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent evt) {
                NmPetugas.setText(Sequel.cariIsi("select nama from petugas where nip=?", NIP.getText()));
            }
        });
        BtnPetugas.addActionListener(evt -> {
            petugasDlg.emptTeks(); petugasDlg.isCek();
            petugasDlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            petugasDlg.setLocationRelativeTo(internalFrame1);
            petugasDlg.setVisible(true);
        });
        petugasDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (petugasDlg.getTable().getSelectedRow() != -1) {
                    NIP.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(), 0).toString());
                    NmPetugas.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

        ScrollInput = new ScrollPane();
        ScrollInput.setViewportView(FormInput);
        ScrollInput.setPreferredSize(new Dimension(960, 300));

        // PANEL BAWAH
        panelBawah = new panelisi();
        panelBawah.setPreferredSize(new Dimension(44, 54));
        panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));
        BtnSimpan = new Button(); BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setMnemonic('S'); BtnSimpan.setText("Simpan"); BtnSimpan.setToolTipText("Alt+S"); BtnSimpan.setPreferredSize(new Dimension(100, 30));
        BtnBatal = new Button(); BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBatal.setMnemonic('B'); BtnBatal.setText("Baru"); BtnBatal.setToolTipText("Alt+B"); BtnBatal.setPreferredSize(new Dimension(100, 30));
        BtnHapus = new Button(); BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setMnemonic('H'); BtnHapus.setText("Hapus"); BtnHapus.setToolTipText("Alt+H"); BtnHapus.setPreferredSize(new Dimension(100, 30));
        BtnEdit = new Button(); BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnEdit.setMnemonic('G'); BtnEdit.setText("Ganti"); BtnEdit.setToolTipText("Alt+G"); BtnEdit.setPreferredSize(new Dimension(100, 30));
        BtnPrint = new Button(); BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        BtnPrint.setMnemonic('T'); BtnPrint.setText("Cetak"); BtnPrint.setToolTipText("Alt+T"); BtnPrint.setPreferredSize(new Dimension(100, 30));
        BtnKeluar = new Button(); BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K'); BtnKeluar.setText("Keluar"); BtnKeluar.setToolTipText("Alt+K"); BtnKeluar.setPreferredSize(new Dimension(100, 30));

        Label lblTglCari = new Label(); lblTglCari.setText("Tanggal :"); lblTglCari.setPreferredSize(new Dimension(55, 23));
        DTPCari1 = new widget.Tanggal(); DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setPreferredSize(new Dimension(90, 23));
        Label lblSd = new Label(); lblSd.setText("s.d."); lblSd.setPreferredSize(new Dimension(25, 23));
        DTPCari2 = new widget.Tanggal(); DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setPreferredSize(new Dimension(90, 23));
        Label lblKey = new Label(); lblKey.setText("Key Word :"); lblKey.setPreferredSize(new Dimension(70, 23));
        TCari = new TextBox(); TCari.setPreferredSize(new Dimension(150, 23));
        BtnCari = new Button(); BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setMnemonic('4'); BtnCari.setToolTipText("Alt+4"); BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnAll = new Button(); BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setMnemonic('M'); BtnAll.setToolTipText("Alt+M"); BtnAll.setPreferredSize(new Dimension(28, 23));
        LCount = new Label(); LCount.setText("Record : 0"); LCount.setPreferredSize(new Dimension(100, 23));

        panelBawah.add(BtnSimpan); panelBawah.add(BtnBatal); panelBawah.add(BtnHapus);
        panelBawah.add(BtnEdit); panelBawah.add(BtnPrint); panelBawah.add(BtnKeluar);
        panelBawah.add(lblTglCari); panelBawah.add(DTPCari1); panelBawah.add(lblSd);
        panelBawah.add(DTPCari2); panelBawah.add(lblKey); panelBawah.add(TCari);
        panelBawah.add(BtnCari); panelBawah.add(BtnAll); panelBawah.add(LCount);

        BtnKeluar.addActionListener(evt -> dispose());
        BtnBatal.addActionListener(evt -> emptTeks());
        BtnCari.addActionListener(evt -> tampil());
        BtnAll.addActionListener(evt -> { TCari.setText(""); tampil(); });
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) { if (evt.getKeyCode() == KeyEvent.VK_ENTER) tampil(); }
        });
        BtnSimpan.addActionListener(evt -> simpan());
        BtnHapus.addActionListener(evt -> hapus());
        BtnEdit.addActionListener(evt -> edit());
        BtnPrint.addActionListener(evt -> cetak());

        TabData = new javax.swing.JTabbedPane();
        TabData.setFont(new Font("Tahoma", 0, 11));
        TabData.addTab("Input", ScrollInput);
        TabData.addTab("Data Input", Scroll);

        internalFrame1.add(panelAtas, BorderLayout.PAGE_START);
        internalFrame1.add(TabData, BorderLayout.CENTER);
        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);

        setLayout(new BorderLayout());
        add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
    }

    // ---- Skoring bagian A ----
    private void hitungSkorA() {
        int total = 0; boolean adaBerat = false;
        for (ComboBox c : cmbGejala) {
            int v = 0;
            try { v = Integer.parseInt(cv(c)); } catch (Exception e) { v = 0; }
            total += v;
            if (v > 7) adaBerat = true;   // 1 gejala >7 -> otomatis Berat (pengecualian)
        }
        String kat, paket; Color warna;
        if (adaBerat || total >= 29) {
            kat = "Berat / Distress"; warna = new Color(190, 0, 0);
            paket = "PAKET ASUHAN KRISIS AKHIR HAYAT (KOLABORASI MEDIS AKUT & PENYESUAIAN STATUS DNR)";
        } else if (total >= 15) {
            kat = "Sedang"; warna = new Color(200, 120, 0);
            paket = "PAKET ASUHAN PALIATIF INTERMEDIET (ESKALASI MONITORING DAN KONSULTASI)";
        } else {
            kat = "Ringan / Terkontrol"; warna = new Color(0, 120, 0);
            paket = "PAKET ASUHAN PALIATIF STANDAR DAN MONITORING BERKALA";
        }
        LTotalSkor.setText(String.valueOf(total));
        LKategori.setText(kat); LKategori.setForeground(warna);
        txtPaket.setText(paket);
    }

    // ---- Mapping paket bagian B ----
    private String paketNyeri(String v) {
        if (v == null) return "";
        if (v.equals("Nyeri Sedang")) return "Paket Eskalasi Analgetik & Manajemen Nyeri Intermediet";
        if (v.equals("Nyeri Berat / Hebat")) return "Paket Krisis Nyeri Akhir Hayat (Titrasi Opioid Kontinuan)";
        if (v.equals("Tidak Nyeri") || v.equals("Nyeri Ringan")) return "Paket Manajemen Kenyamanan & Terapi Analgetik Dasar";
        return "";
    }
    private String paketGejala(String v) {
        if (v == null) return "";
        if (v.startsWith("Sesak")) return "Paket Manajemen Distress Pernapasan Akhir Hayat";
        if (v.startsWith("Suara napas")) return "Paket Manajemen Sekresi Jalan Napas Pasien Terminal";
        if (v.startsWith("Mual")) return "Paket Manajemen Kenyamanan Gastrointestinal";
        if (v.startsWith("Gelisah")) return "Paket Manajemen Delirium & Sedasi Paliatif Ringan";
        if (v.startsWith("Konstipasi")) return "Paket Eliminasi & Manajemen Retensi Usus Paliatif";
        return "";
    }
    private String paketPsiko(String v) {
        if (v == null) return "";
        if (v.startsWith("Kooperatif")) return "Paket Pemeliharaan Stabilitas Psikologis & Edukasi Koping";
        if (v.startsWith("Cemas")) return "Paket Dukungan Emosional & Konseling Paliatif Intermediet";
        if (v.startsWith("Denial")) return "Paket Intervensi Krisis Psikologis Pasien Terminal";
        return "";
    }
    private String paketKeluarga(String v) {
        if (v == null) return "";
        if (v.startsWith("Memahami")) return "Paket Edukasi Koping & Pemberdayaan Keluarga (Comfort Care)";
        if (v.startsWith("Kelelahan")) return "Paket Respite Care & Konseling Keputusan Medis";
        if (v.startsWith("Duka cita")) return "Paket Manajemen Krisis Duka Cita & Dukungan Berduka";
        return "";
    }

    private String[] dataArray() {
        return new String[]{
            TNoRw.getText(), tglValue(),
            cv(cmbGejala[0]), cv(cmbGejala[1]), cv(cmbGejala[2]), cv(cmbGejala[3]),
            cv(cmbGejala[4]), cv(cmbGejala[5]), cv(cmbGejala[6]),
            LTotalSkor.getText(), LKategori.getText(), txtPaket.getText(),
            cv(cmbBNyeri), txtBNyeriPaket.getText(),
            cv(cmbBGejala), txtBGejalaPaket.getText(),
            cv(cmbBPsiko), txtBPsikoPaket.getText(),
            cv(cmbBKeluarga), txtBKeluargaPaket.getText(),
            cv(cmbSpiritualButuh), cv(cmbKepercayaan),
            cv(cmbKeprihatinan),
            cv(cmbHubungan), cv(cmbKelayakan), cv(cmbPerawatanRumah), cv(cmbReaksi),
            cv(cmbPihak), cv(cmbAlternatif), cv(cmbRisiko), cv(cmbPotensi), cv(cmbKeputusan),
            NIP.getText()
        };
    }

    private void simpan() {
        if (TNoRw.getText().trim().equals("")) { Valid.textKosong(TNoRw, "Pasien"); return; }
        if (Sequel.menyimpantf("asesmen_akhir_kehidupan_terminal",
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?",
                "Asesmen Akhir Kehidupan Terminal", 33, dataArray())) {
            tampil(); emptTeks();
        }
    }

    private void hapus() {
        if (TNoRw.getText().trim().equals("")) { Valid.textKosong(TNoRw, "Pasien"); return; }
        if (Sequel.queryu2tf("delete from asesmen_akhir_kehidupan_terminal where no_rawat=? and tanggal=?", 2, new String[]{
                TNoRw.getText(), tglValue()})) {
            tampil(); emptTeks();
        }
    }

    private void edit() {
        if (TNoRw.getText().trim().equals("")) { Valid.textKosong(TNoRw, "Pasien"); return; }
        if (tbObat.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau diganti...!!!!"); return; }
        String update = "skor_tidur=?,skor_makan=?,skor_mual=?,skor_usus=?,skor_nafas=?,skor_lelah=?,skor_nyeri=?,"
            + "total_skor=?,kategori_gejala=?,paket_asuhan=?,b_nyeri=?,b_nyeri_paket=?,b_gejala_lain=?,b_gejala_lain_paket=?,"
            + "b_psiko_spiritual=?,b_psiko_spiritual_paket=?,b_keluarga=?,b_keluarga_paket=?,spiritual_butuh=?,jenis_kepercayaan=?,"
            + "keprihatinan_spiritual=?,hubungan_keluarga=?,kelayakan_tinggal=?,perawatan_rumah=?,reaksi_penyakit=?,"
            + "pihak_dihubungi=?,kebutuhan_alternatif=?,faktor_risiko_keluarga=?,potensi_reaksi_patologis=?,keputusan_asuhan=?,nip=?";
        String[] d = dataArray();
        // params: 31 kolom update (indeks 2..32 dari dataArray) + 2 acuan (no_rawat,tanggal asli).
        String[] a = new String[33];
        for (int i = 0; i < 31; i++) a[i] = d[i + 2];
        a[31] = tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString();
        a[32] = tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString();
        if (Sequel.mengedittf("asesmen_akhir_kehidupan_terminal", "no_rawat=? and tanggal=?", update, 33, a)) {
            tampil(); emptTeks();
        }
    }

    private void cetak() {
        if (tbObat.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu..!!!");
            return;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        Valid.MyReportqry("rptAsesmenAkhirKehidupanTerminal.jasper", "report", "::[ Asesmen Menjelang Akhir Kehidupan (Terminal) ]::",
            "select a.*, p.nm_pasien, p.no_rkm_medis, p.jk, DATE_FORMAT(p.tgl_lahir,'%d-%m-%Y') as tgl_lahir, "
            + "concat(p.alamat) as alamat_pasien, dk.nm_dokter as dpjp, pg.nama as nama_petugas, "
            + "DATE_FORMAT(a.tanggal,'%d-%m-%Y %H:%i') as tanggal_cetak, "
            + "ifnull((select bangsal.nm_bangsal from kamar_inap join kamar on kamar_inap.kd_kamar=kamar.kd_kamar join bangsal on kamar.kd_bangsal=bangsal.kd_bangsal where kamar_inap.no_rawat=a.no_rawat order by kamar_inap.tgl_masuk desc, kamar_inap.jam_masuk desc limit 1), poliklinik.nm_poli) as ruangan "
            + "from asesmen_akhir_kehidupan_terminal a "
            + "inner join reg_periksa r on a.no_rawat=r.no_rawat "
            + "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "
            + "left join dokter dk on r.kd_dokter=dk.kd_dokter "
            + "left join poliklinik on r.kd_poli=poliklinik.kd_poli "
            + "left join petugas pg on a.nip=pg.nip "
            + "where a.no_rawat='" + tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "' "
            + "and a.tanggal='" + tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString() + "'", param);
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select a.no_rawat, r.no_rkm_medis, p.nm_pasien, a.tanggal, a.total_skor, a.kategori_gejala, a.keputusan_asuhan, pg.nama "
                + "from asesmen_akhir_kehidupan_terminal a "
                + "inner join reg_periksa r on a.no_rawat=r.no_rawat "
                + "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "
                + "left join petugas pg on a.nip=pg.nip "
                + "where a.tanggal between ? and ? and (a.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) "
                + "order by a.tanggal desc");
            try {
                ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                ps.setString(3, "%" + TCari.getText().trim() + "%");
                ps.setString(4, "%" + TCari.getText().trim() + "%");
                ps.setString(5, "%" + TCari.getText().trim() + "%");
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("tanggal"), rs.getString("total_skor"), rs.getString("kategori_gejala"),
                        rs.getString("keputusan_asuhan"), rs.getString("nama")
                    });
                }
                LCount.setText("Record : " + tabMode.getRowCount());
            } catch (Exception e) { System.out.println("Notif Asesmen Akhir Hidup : " + e);
            } finally { if (rs != null) rs.close(); if (ps != null) ps.close(); }
        } catch (Exception e) { System.out.println("Notif Asesmen Akhir Hidup : " + e); }
    }

    public void getData() {
        if (tbObat.getSelectedRow() == -1) return;
        TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
        TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
        TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
        try {
            Tgl.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()));
        } catch (Exception e) {}
        try {
            ps = koneksi.prepareStatement("select * from asesmen_akhir_kehidupan_terminal where no_rawat=? and tanggal=?");
            ps.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            ps.setString(2, tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                String[] keys = {"skor_tidur", "skor_makan", "skor_mual", "skor_usus", "skor_nafas", "skor_lelah", "skor_nyeri"};
                for (int i = 0; i < cmbGejala.length; i++) cmbGejala[i].setSelectedItem(nz(rs.getString(keys[i]), "0"));
                hitungSkorA();
                cmbBNyeri.setSelectedItem(rs.getString("b_nyeri")); txtBNyeriPaket.setText(rs.getString("b_nyeri_paket"));
                cmbBGejala.setSelectedItem(rs.getString("b_gejala_lain")); txtBGejalaPaket.setText(rs.getString("b_gejala_lain_paket"));
                cmbBPsiko.setSelectedItem(rs.getString("b_psiko_spiritual")); txtBPsikoPaket.setText(rs.getString("b_psiko_spiritual_paket"));
                cmbBKeluarga.setSelectedItem(rs.getString("b_keluarga")); txtBKeluargaPaket.setText(rs.getString("b_keluarga_paket"));
                cmbSpiritualButuh.setSelectedItem(rs.getString("spiritual_butuh"));
                cmbKepercayaan.setSelectedItem(rs.getString("jenis_kepercayaan"));
                cmbKeprihatinan.setSelectedItem(rs.getString("keprihatinan_spiritual"));
                cmbHubungan.setSelectedItem(rs.getString("hubungan_keluarga"));
                cmbKelayakan.setSelectedItem(rs.getString("kelayakan_tinggal"));
                cmbPerawatanRumah.setSelectedItem(rs.getString("perawatan_rumah"));
                cmbReaksi.setSelectedItem(rs.getString("reaksi_penyakit"));
                cmbPihak.setSelectedItem(rs.getString("pihak_dihubungi"));
                cmbAlternatif.setSelectedItem(rs.getString("kebutuhan_alternatif"));
                cmbRisiko.setSelectedItem(rs.getString("faktor_risiko_keluarga"));
                cmbPotensi.setSelectedItem(rs.getString("potensi_reaksi_patologis"));
                cmbKeputusan.setSelectedItem(rs.getString("keputusan_asuhan"));
                NIP.setText(rs.getString("nip"));
                NmPetugas.setText(Sequel.cariIsi("select nama from petugas where nip=?", NIP.getText()));
            }
        } catch (Exception e) { System.out.println("Notif getData Asesmen Akhir Hidup : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    private String nz(String v, String def) { return (v == null || v.trim().equals("")) ? def : v; }

    public void emptTeks() {
        Tgl.setDate(new Date());
        for (ComboBox c : cmbGejala) c.setSelectedItem("0");
        hitungSkorA();
        for (ComboBox c : new ComboBox[]{cmbBNyeri, cmbBGejala, cmbBPsiko, cmbBKeluarga, cmbSpiritualButuh, cmbKepercayaan,
                cmbKeprihatinan, cmbHubungan, cmbKelayakan, cmbPerawatanRumah, cmbReaksi, cmbPihak, cmbAlternatif,
                cmbRisiko, cmbPotensi, cmbKeputusan}) {
            c.setSelectedIndex(0);
        }
        txtBNyeriPaket.setText(""); txtBGejalaPaket.setText(""); txtBPsikoPaket.setText(""); txtBKeluargaPaket.setText("");
        NIP.setText(akses.getkode());
        NmPetugas.setText(akses.getnamauser());
    }

    public void setNoRm(String norwt, Date tgl) {
        TNoRw.setText(norwt);
        if (tgl != null) Tgl.setDate(tgl);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", norwt));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
        NIP.setText(akses.getkode());
        NmPetugas.setText(akses.getnamauser());
        DTPCari1.setDate(new Date());
        DTPCari2.setDate(new Date());
        TCari.setText(norwt);
    }

    public void setNoRm(String norwt) { setNoRm(norwt, new Date()); }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getpenilaian_pasien_terminal());
        BtnHapus.setEnabled(akses.getpenilaian_pasien_terminal());
        BtnEdit.setEnabled(akses.getpenilaian_pasien_terminal());
        BtnPrint.setEnabled(akses.getpenilaian_pasien_terminal());
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            RMAsesmenAkhirKehidupanTerminal dialog = new RMAsesmenAkhirKehidupanTerminal(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosing(java.awt.event.WindowEvent e) { System.exit(0); }
            });
            dialog.setVisible(true);
        });
    }
}
