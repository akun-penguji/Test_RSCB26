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
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import widget.Button;
import widget.ComboBox;
import widget.InternalFrame;
import widget.Label;
import widget.PanelBiasa;
import widget.ScrollPane;
import widget.TextBox;
import widget.panelisi;

/**
 * Asesmen Pasien Tahap Terminal (RM.KEP.08.002).
 * Pengganti form lama RMPenilaianPasienTerminal — dibangun ulang sesuai form fisik
 * (B1-B6 head-to-toe, tingkat pemahaman, nyeri, psikososial, spiritual).
 */
public class RMAsesmenPasienTerminal extends JDialog {

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

    // 1. Pemahaman
    private ComboBox cmbPemahaman;
    // 2.1 Respiratory
    private JCheckBox[] cbTipeNafas, cbSuaraNafas;
    private TextBox txtTipeNafasLain, txtFrekNafas, txtSputumWarna, txtSputumLain;
    private ComboBox cmbSputum;
    // 2.2 Blood
    private ComboBox cmbIrama, cmbPulsasi;
    private TextBox txtFrekNadi, txtTD, txtSuhu, txtCVLine, txtMAP, txtPerdarahan;
    private JCheckBox[] cbEkstremitas;
    // 2.3 Brain
    private TextBox txtGcsMata, txtGcsMotorik, txtGcsVerbal, txtKonjungtivaLain;
    private ComboBox cmbKesadaran, cmbKonjungtiva;
    private JCheckBox[] cbTik;
    // 2.4 Blader
    private ComboBox cmbAreaGenital, cmbCaraBak;
    private TextBox txtProduksiUrine, txtWarnaUrine;
    // 2.5 Bowel
    private JCheckBox[] cbNafsuMakan, cbMulut;
    private ComboBox cmbPorsiMakan, cmbEliminasiBab;
    private TextBox txtMinumCc, txtJenisCairan, txtBabFrek, txtBabLain;
    // 2.6 Muskuloskeletal
    private ComboBox cmbPergerakan, cmbOedema, cmbDekubitus;
    private TextBox txtKontraktur, txtFraktur, txtInfus, txtOedemaLokasi, txtDekubitusLokasi;
    private JCheckBox[] cbWarnaKulit;
    // 3. Nyeri
    private ComboBox cmbNyeri, cmbSkalaNyeri;
    private TextBox txtNyeriLokasi;
    // 4. Psikososial
    private ComboBox cmbPsikososial;
    // 5. Spiritual
    private ComboBox cmbSpPelayanan, cmbSpDidoakan, cmbSpBimbingan, cmbSpPendampingan;
    private TextBox txtSpPelayananOleh, txtSpDidoakanOleh, txtSpBimbinganOleh, txtSpPendampinganOleh;
    // Petugas
    private TextBox NIP, NmPetugas;
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);

    private widget.Table tbObat;
    private javax.swing.table.DefaultTableModel tabMode;
    private ScrollPane Scroll;

    private PanelBiasa FormInput;
    private ScrollPane ScrollInput;
    private javax.swing.JTabbedPane TabData;

    public RMAsesmenPasienTerminal(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(1000, 640);
        setLocationRelativeTo(null);
    }

    private JCheckBox cb(String txt) {
        JCheckBox c = new JCheckBox(txt);
        c.setOpaque(false);
        c.setFont(new Font("Tahoma", 0, 11));
        return c;
    }

    private ComboBox combo(String[] items) {
        ComboBox c = new ComboBox();
        c.setModel(new DefaultComboBoxModel(items));
        return c;
    }

    private Label lbl(String txt, int x, int y, int w) {
        Label l = new Label();
        l.setText(txt);
        l.setBounds(x, y, w, 23);
        return l;
    }

    private JLabel header(String txt, int y) {
        JLabel l = new JLabel(txt);
        l.setFont(new Font("Tahoma", 1, 11));
        l.setForeground(new Color(0, 70, 140));
        l.setBounds(10, y, 700, 20);
        return l;
    }

    private void initComponents() {
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Asesmen Pasien Tahap Terminal ]::",
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

        // TABLE
        Scroll = new ScrollPane();
        tbObat = new widget.Table();
        tabMode = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat", "No.R.M.", "Nama Pasien", "Tanggal", "Kesadaran", "Skala Nyeri", "Psikososial", "Petugas"}) {
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
                    try { getData(); } catch (Exception e) {}   // pilih + isi field, tetap di tab Data Input
                }
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent evt) {
                if (tabMode.getRowCount() == 0) return;
                if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                    try { getData(); } catch (Exception e) {}
                    TabData.setSelectedIndex(0);             // SPASI -> buka tab Input
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
        FormInput.setPreferredSize(new Dimension(960, 1180));
        FormInput.setLayout(null);
        int lx = 10, fx = 175, y = 8;

        Tgl = new widget.Tanggal(); Tgl.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        FormInput.add(lbl("Tanggal Asesmen :", lx, y, 160));
        Tgl.setBounds(fx, y, 140, 23); FormInput.add(Tgl);

        y += 28;
        FormInput.add(header("1. Tingkat Pemahaman Pasien/Keluarga", y));
        y += 22;
        cmbPemahaman = combo(new String[]{"",
            "Pasien/keluarga percaya bahwa pasien akan segera sembuh",
            "Keluarga mengetahui kondisi terminal & kadang menghindari pembicaraan tentang kematian",
            "Keluarga mengetahui proses kematian & tidak keberatan membicarakannya"});
        cmbPemahaman.setBounds(lx, y, 760, 23); FormInput.add(cmbPemahaman);

        // 2.1 RESPIRATORY
        y += 30; FormInput.add(header("2.1 Respiratory (Pernapasan)", y));
        y += 22;
        FormInput.add(lbl("Tipe Pernapasan :", lx, y, 160));
        cbTipeNafas = new JCheckBox[]{cb("Teratur"), cb("Tidak teratur"), cb("Batuk"), cb("Ventilasi mekanik"), cb("O2 T-Piece")};
        int cx = fx;
        for (JCheckBox c : cbTipeNafas) { int w = c.getPreferredSize().width; c.setBounds(cx, y, w, 23); FormInput.add(c); cx += w + 6; }
        y += 26;
        FormInput.add(lbl("Lain-lain :", lx, y, 160));
        txtTipeNafasLain = new TextBox(); txtTipeNafasLain.setBounds(fx, y, 220, 23); FormInput.add(txtTipeNafasLain);
        FormInput.add(lbl("Frek. Nafas (x/mnt) :", 410, y, 130));
        txtFrekNafas = new TextBox(); txtFrekNafas.setBounds(545, y, 60, 23); FormInput.add(txtFrekNafas);
        y += 26;
        FormInput.add(lbl("Suara Nafas :", lx, y, 160));
        cbSuaraNafas = new JCheckBox[]{cb("Ronkhi"), cb("Wheezing"), cb("Stridor"), cb("Crackles"), cb("Dll")};
        cx = fx;
        for (JCheckBox c : cbSuaraNafas) { int w = c.getPreferredSize().width; c.setBounds(cx, y, w, 23); FormInput.add(c); cx += w + 6; }
        y += 26;
        FormInput.add(lbl("Sputum :", lx, y, 160));
        cmbSputum = combo(new String[]{"", "Tidak ada", "Ada"});
        cmbSputum.setBounds(fx, y, 120, 23); FormInput.add(cmbSputum);
        FormInput.add(lbl("Warna :", 305, y, 50));
        txtSputumWarna = new TextBox(); txtSputumWarna.setBounds(355, y, 120, 23); FormInput.add(txtSputumWarna);
        FormInput.add(lbl("Lain-lain :", 485, y, 60));
        txtSputumLain = new TextBox(); txtSputumLain.setBounds(545, y, 160, 23); FormInput.add(txtSputumLain);

        // 2.2 BLOOD
        y += 30; FormInput.add(header("2.2 Kardiovaskuler (Blood)", y));
        y += 22;
        FormInput.add(lbl("Irama Jantung :", lx, y, 160));
        cmbIrama = combo(new String[]{"", "Reguler", "Ireguler"});
        cmbIrama.setBounds(fx, y, 120, 23); FormInput.add(cmbIrama);
        FormInput.add(lbl("Frek. Nadi (x/mnt) :", 305, y, 130));
        txtFrekNadi = new TextBox(); txtFrekNadi.setBounds(435, y, 60, 23); FormInput.add(txtFrekNadi);
        FormInput.add(lbl("TD (mmHg) :", 505, y, 80));
        txtTD = new TextBox(); txtTD.setBounds(585, y, 80, 23); FormInput.add(txtTD);
        y += 26;
        FormInput.add(lbl("Ekstremitas :", lx, y, 160));
        cbEkstremitas = new JCheckBox[]{cb("Hangat"), cb("Kering"), cb("Pucat"), cb("Merah"), cb("Dingin"), cb("Basah")};
        cx = fx;
        for (JCheckBox c : cbEkstremitas) { int w = c.getPreferredSize().width; c.setBounds(cx, y, w, 23); FormInput.add(c); cx += w + 6; }
        y += 26;
        FormInput.add(lbl("Suhu (°C) :", lx, y, 160));
        txtSuhu = new TextBox(); txtSuhu.setBounds(fx, y, 60, 23); FormInput.add(txtSuhu);
        FormInput.add(lbl("Pulsasi :", 250, y, 60));
        cmbPulsasi = combo(new String[]{"", "Sangat kuat", "Kuat teraba", "Lemah teraba", "Hilang timbul", "Tidak teraba"});
        cmbPulsasi.setBounds(310, y, 130, 23); FormInput.add(cmbPulsasi);
        FormInput.add(lbl("CV Line (cmH2O) :", 450, y, 110));
        txtCVLine = new TextBox(); txtCVLine.setBounds(560, y, 60, 23); FormInput.add(txtCVLine);
        FormInput.add(lbl("MAP (mmHg) :", 630, y, 90));
        txtMAP = new TextBox(); txtMAP.setBounds(720, y, 60, 23); FormInput.add(txtMAP);
        y += 26;
        FormInput.add(lbl("Perdarahan/Lokasi :", lx, y, 160));
        txtPerdarahan = new TextBox(); txtPerdarahan.setBounds(fx, y, 530, 23); FormInput.add(txtPerdarahan);

        // 2.3 BRAIN
        y += 30; FormInput.add(header("2.3 Persyarafan (Brain)", y));
        y += 22;
        FormInput.add(lbl("GCS - Mata :", lx, y, 160));
        txtGcsMata = new TextBox(); txtGcsMata.setBounds(fx, y, 40, 23); FormInput.add(txtGcsMata);
        FormInput.add(lbl("Motorik :", 235, y, 60));
        txtGcsMotorik = new TextBox(); txtGcsMotorik.setBounds(295, y, 40, 23); FormInput.add(txtGcsMotorik);
        FormInput.add(lbl("Verbal :", 355, y, 50));
        txtGcsVerbal = new TextBox(); txtGcsVerbal.setBounds(405, y, 40, 23); FormInput.add(txtGcsVerbal);
        y += 26;
        FormInput.add(lbl("Tingkat Kesadaran :", lx, y, 160));
        cmbKesadaran = combo(new String[]{"", "Compos mentis", "Apatis", "Somnolent", "Sopor", "Coma"});
        cmbKesadaran.setBounds(fx, y, 130, 23); FormInput.add(cmbKesadaran);
        FormInput.add(lbl("Tanda Peningkatan TIK :", 320, y, 150));
        cbTik = new JCheckBox[]{cb("Nyeri kepala"), cb("Muntah proyektil")};
        cx = 470;
        for (JCheckBox c : cbTik) { int w = c.getPreferredSize().width; c.setBounds(cx, y, w, 23); FormInput.add(c); cx += w + 6; }
        y += 26;
        FormInput.add(lbl("Konjungtiva :", lx, y, 160));
        cmbKonjungtiva = combo(new String[]{"", "Anemis", "Kemerahan", "Lain-lain"});
        cmbKonjungtiva.setBounds(fx, y, 120, 23); FormInput.add(cmbKonjungtiva);
        FormInput.add(lbl("Lain-lain :", 305, y, 60));
        txtKonjungtivaLain = new TextBox(); txtKonjungtivaLain.setBounds(365, y, 200, 23); FormInput.add(txtKonjungtivaLain);

        // 2.4 BLADER
        y += 30; FormInput.add(header("2.4 Perkemihan (Blader)", y));
        y += 22;
        FormInput.add(lbl("Area Genital :", lx, y, 160));
        cmbAreaGenital = combo(new String[]{"", "Bersih", "Kotor"});
        cmbAreaGenital.setBounds(fx, y, 120, 23); FormInput.add(cmbAreaGenital);
        FormInput.add(lbl("Cara BAK :", 305, y, 70));
        cmbCaraBak = combo(new String[]{"", "Spontan", "Dower Cateter"});
        cmbCaraBak.setBounds(375, y, 130, 23); FormInput.add(cmbCaraBak);
        y += 26;
        FormInput.add(lbl("Produksi Urine (cc/jam) :", lx, y, 160));
        txtProduksiUrine = new TextBox(); txtProduksiUrine.setBounds(fx, y, 70, 23); FormInput.add(txtProduksiUrine);
        FormInput.add(lbl("Warna :", 260, y, 50));
        txtWarnaUrine = new TextBox(); txtWarnaUrine.setBounds(310, y, 180, 23); FormInput.add(txtWarnaUrine);

        // 2.5 BOWEL
        y += 30; FormInput.add(header("2.5 Pencernaan (Bowel)", y));
        y += 22;
        FormInput.add(lbl("Nafsu Makan :", lx, y, 160));
        cbNafsuMakan = new JCheckBox[]{cb("Baik"), cb("Menurun"), cb("Mual"), cb("Muntah")};
        cx = fx;
        for (JCheckBox c : cbNafsuMakan) { int w = c.getPreferredSize().width; c.setBounds(cx, y, w, 23); FormInput.add(c); cx += w + 6; }
        y += 26;
        FormInput.add(lbl("Porsi Makan :", lx, y, 160));
        cmbPorsiMakan = combo(new String[]{"", "Habis", "Tidak Habis"});
        cmbPorsiMakan.setBounds(fx, y, 120, 23); FormInput.add(cmbPorsiMakan);
        FormInput.add(lbl("Minum (cc/hari) :", 305, y, 110));
        txtMinumCc = new TextBox(); txtMinumCc.setBounds(415, y, 60, 23); FormInput.add(txtMinumCc);
        FormInput.add(lbl("Jenis Cairan :", 485, y, 80));
        txtJenisCairan = new TextBox(); txtJenisCairan.setBounds(565, y, 150, 23); FormInput.add(txtJenisCairan);
        y += 26;
        FormInput.add(lbl("Mulut :", lx, y, 160));
        cbMulut = new JCheckBox[]{cb("Bersih"), cb("Kotor"), cb("Berbau")};
        cx = fx;
        for (JCheckBox c : cbMulut) { int w = c.getPreferredSize().width; c.setBounds(cx, y, w, 23); FormInput.add(c); cx += w + 6; }
        y += 26;
        FormInput.add(lbl("Eliminasi BAB :", lx, y, 160));
        cmbEliminasiBab = combo(new String[]{"", "Teratur", "Tidak normal", "Lain-lain"});
        cmbEliminasiBab.setBounds(fx, y, 120, 23); FormInput.add(cmbEliminasiBab);
        FormInput.add(lbl("Frek. (x/hari) :", 305, y, 90));
        txtBabFrek = new TextBox(); txtBabFrek.setBounds(395, y, 50, 23); FormInput.add(txtBabFrek);
        FormInput.add(lbl("Lain-lain :", 455, y, 60));
        txtBabLain = new TextBox(); txtBabLain.setBounds(515, y, 200, 23); FormInput.add(txtBabLain);

        // 2.6 MUSKULOSKELETAL
        y += 30; FormInput.add(header("2.6 Muskuloskeletal (Integumen)", y));
        y += 22;
        FormInput.add(lbl("Pergerakan Sendi :", lx, y, 160));
        cmbPergerakan = combo(new String[]{"", "Bebas", "Terbatas"});
        cmbPergerakan.setBounds(fx, y, 120, 23); FormInput.add(cmbPergerakan);
        FormInput.add(lbl("Kontraktur/Lokasi :", 305, y, 120));
        txtKontraktur = new TextBox(); txtKontraktur.setBounds(425, y, 130, 23); FormInput.add(txtKontraktur);
        y += 26;
        FormInput.add(lbl("Fraktur/Lokasi :", lx, y, 160));
        txtFraktur = new TextBox(); txtFraktur.setBounds(fx, y, 200, 23); FormInput.add(txtFraktur);
        FormInput.add(lbl("Terpasang Infus/Lokasi :", 390, y, 150));
        txtInfus = new TextBox(); txtInfus.setBounds(545, y, 170, 23); FormInput.add(txtInfus);
        y += 26;
        FormInput.add(lbl("Warna Kulit :", lx, y, 160));
        cbWarnaKulit = new JCheckBox[]{cb("Ikterik"), cb("Sianotik"), cb("Kemerahan"), cb("Pucat"), cb("Hiperpigmentasi")};
        cx = fx;
        for (JCheckBox c : cbWarnaKulit) { int w = c.getPreferredSize().width; c.setBounds(cx, y, w, 23); FormInput.add(c); cx += w + 6; }
        y += 26;
        FormInput.add(lbl("Oedema Tungkai :", lx, y, 160));
        cmbOedema = combo(new String[]{"", "Ada", "Tidak ada"});
        cmbOedema.setBounds(fx, y, 110, 23); FormInput.add(cmbOedema);
        FormInput.add(lbl("Lokasi :", 295, y, 50));
        txtOedemaLokasi = new TextBox(); txtOedemaLokasi.setBounds(345, y, 150, 23); FormInput.add(txtOedemaLokasi);
        y += 26;
        FormInput.add(lbl("Dekubitus :", lx, y, 160));
        cmbDekubitus = combo(new String[]{"", "Ada", "Tidak"});
        cmbDekubitus.setBounds(fx, y, 110, 23); FormInput.add(cmbDekubitus);
        FormInput.add(lbl("Lokasi :", 295, y, 50));
        txtDekubitusLokasi = new TextBox(); txtDekubitusLokasi.setBounds(345, y, 150, 23); FormInput.add(txtDekubitusLokasi);

        // 3. NYERI
        y += 30; FormInput.add(header("3. Nyeri", y));
        y += 22;
        FormInput.add(lbl("Nyeri :", lx, y, 160));
        cmbNyeri = combo(new String[]{"", "Tidak", "Ada"});
        cmbNyeri.setBounds(fx, y, 100, 23); FormInput.add(cmbNyeri);
        FormInput.add(lbl("Skala Nyeri :", 285, y, 80));
        cmbSkalaNyeri = combo(new String[]{"", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
        cmbSkalaNyeri.setBounds(365, y, 70, 23); FormInput.add(cmbSkalaNyeri);
        FormInput.add(lbl("Lokasi :", 445, y, 50));
        txtNyeriLokasi = new TextBox(); txtNyeriLokasi.setBounds(495, y, 220, 23); FormInput.add(txtNyeriLokasi);

        // 4. PSIKOSOSIAL
        y += 30; FormInput.add(header("4. Psikososial (Tahap Berduka)", y));
        y += 22;
        FormInput.add(lbl("Tahap :", lx, y, 160));
        cmbPsikososial = combo(new String[]{"", "Tahap Denial", "Tahap Anger", "Tahap Bargaining", "Tahap Depresi", "Tahap Acceptance"});
        cmbPsikososial.setBounds(fx, y, 200, 23); FormInput.add(cmbPsikososial);

        // 5. SPIRITUAL
        y += 30; FormInput.add(header("5. Spiritual", y));
        y += 22;
        FormInput.add(lbl("Perlu pelayanan spiritual :", lx, y, 170));
        cmbSpPelayanan = combo(new String[]{"", "Ya", "Tidak"});
        cmbSpPelayanan.setBounds(fx + 10, y, 90, 23); FormInput.add(cmbSpPelayanan);
        FormInput.add(lbl("Oleh :", 290, y, 40));
        txtSpPelayananOleh = new TextBox(); txtSpPelayananOleh.setBounds(330, y, 200, 23); FormInput.add(txtSpPelayananOleh);
        y += 26;
        FormInput.add(lbl("Perlu didoakan :", lx, y, 170));
        cmbSpDidoakan = combo(new String[]{"", "Ya", "Tidak"});
        cmbSpDidoakan.setBounds(fx + 10, y, 90, 23); FormInput.add(cmbSpDidoakan);
        FormInput.add(lbl("Oleh :", 290, y, 40));
        txtSpDidoakanOleh = new TextBox(); txtSpDidoakanOleh.setBounds(330, y, 200, 23); FormInput.add(txtSpDidoakanOleh);
        y += 26;
        FormInput.add(lbl("Perlu bimbingan rohani :", lx, y, 170));
        cmbSpBimbingan = combo(new String[]{"", "Ya", "Tidak"});
        cmbSpBimbingan.setBounds(fx + 10, y, 90, 23); FormInput.add(cmbSpBimbingan);
        FormInput.add(lbl("Oleh :", 290, y, 40));
        txtSpBimbinganOleh = new TextBox(); txtSpBimbinganOleh.setBounds(330, y, 200, 23); FormInput.add(txtSpBimbinganOleh);
        y += 26;
        FormInput.add(lbl("Perlu pendampingan rohani :", lx, y, 170));
        cmbSpPendampingan = combo(new String[]{"", "Ya", "Tidak"});
        cmbSpPendampingan.setBounds(fx + 10, y, 90, 23); FormInput.add(cmbSpPendampingan);
        FormInput.add(lbl("Oleh :", 290, y, 40));
        txtSpPendampinganOleh = new TextBox(); txtSpPendampinganOleh.setBounds(330, y, 200, 23); FormInput.add(txtSpPendampinganOleh);

        // PETUGAS
        y += 30; FormInput.add(header("Petugas Penilai (Perawat)", y));
        y += 22;
        FormInput.add(lbl("NIP :", lx, y, 160));
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

        // SCROLL FORM INPUT (dipasang sebagai tab "Input")
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

        // ACTIONS
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

        // ASSEMBLY: 2 tab -> "Input" (form) & "Data Input" (tabel pasien tersimpan)
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

    // ---- helper multi-select ----
    private String selJoin(JCheckBox[] boxes) {
        StringBuilder s = new StringBuilder();
        for (JCheckBox c : boxes) if (c.isSelected()) { if (s.length() > 0) s.append(", "); s.append(c.getText()); }
        return s.toString();
    }
    private void selSet(JCheckBox[] boxes, String v) {
        String val = v == null ? "" : v;
        for (JCheckBox c : boxes) c.setSelected(val.contains(c.getText()));
    }
    private String cv(ComboBox c) { return c.getSelectedItem() == null ? "" : c.getSelectedItem().toString(); }
    private String tglValue() {
        return Valid.SetTgl(Tgl.getSelectedItem() + "") + " " + Tgl.getSelectedItem().toString().substring(11, 19);
    }

    private String[] dataArray() {
        return new String[]{
            TNoRw.getText(), tglValue(),
            cv(cmbPemahaman),
            selJoin(cbTipeNafas), txtTipeNafasLain.getText(), txtFrekNafas.getText(),
            selJoin(cbSuaraNafas), cv(cmbSputum), txtSputumWarna.getText(), txtSputumLain.getText(),
            cv(cmbIrama), txtFrekNadi.getText(), txtTD.getText(), selJoin(cbEkstremitas), txtSuhu.getText(),
            cv(cmbPulsasi), txtCVLine.getText(), txtMAP.getText(), txtPerdarahan.getText(),
            txtGcsMata.getText(), txtGcsMotorik.getText(), txtGcsVerbal.getText(), cv(cmbKesadaran),
            selJoin(cbTik), cv(cmbKonjungtiva), txtKonjungtivaLain.getText(),
            cv(cmbAreaGenital), cv(cmbCaraBak), txtProduksiUrine.getText(), txtWarnaUrine.getText(),
            selJoin(cbNafsuMakan), cv(cmbPorsiMakan), txtMinumCc.getText(), txtJenisCairan.getText(),
            selJoin(cbMulut), cv(cmbEliminasiBab), txtBabFrek.getText(), txtBabLain.getText(),
            cv(cmbPergerakan), txtKontraktur.getText(), txtFraktur.getText(), txtInfus.getText(),
            selJoin(cbWarnaKulit), cv(cmbOedema), txtOedemaLokasi.getText(), cv(cmbDekubitus), txtDekubitusLokasi.getText(),
            cv(cmbNyeri), cv(cmbSkalaNyeri), txtNyeriLokasi.getText(),
            cv(cmbPsikososial),
            cv(cmbSpPelayanan), txtSpPelayananOleh.getText(), cv(cmbSpDidoakan), txtSpDidoakanOleh.getText(),
            cv(cmbSpBimbingan), txtSpBimbinganOleh.getText(), cv(cmbSpPendampingan), txtSpPendampinganOleh.getText(),
            NIP.getText()
        };
    }

    private void simpan() {
        if (TNoRw.getText().trim().equals("")) { Valid.textKosong(TNoRw, "Pasien"); return; }
        if (Sequel.menyimpantf("asesmen_pasien_terminal",
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?",
                "Asesmen Pasien Terminal", 60, dataArray())) {
            tampil(); emptTeks();
        }
    }

    private void hapus() {
        if (tbObat.getSelectedRow() != -1) {
            if (Sequel.queryu2tf("delete from asesmen_pasien_terminal where no_rawat=? and tanggal=?", 2, new String[]{
                tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
            })) { tampil(); emptTeks(); }
        } else { JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dihapus...!!!!"); }
    }

    private void edit() {
        if (TNoRw.getText().trim().equals("")) { Valid.textKosong(TNoRw, "Pasien"); return; }
        if (tbObat.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau diganti...!!!!"); return; }
        String update = "tingkat_pemahaman=?,tipe_pernapasan=?,tipe_pernapasan_lain=?,frekuensi_nafas=?,suara_nafas=?,"
            + "sputum=?,sputum_warna=?,sputum_lain=?,irama_jantung=?,frekuensi_nadi=?,td=?,ekstremitas=?,suhu=?,pulsasi=?,"
            + "cv_line=?,map=?,perdarahan_lokasi=?,gcs_mata=?,gcs_motorik=?,gcs_verbal=?,kesadaran=?,tanda_tik=?,konjungtiva=?,"
            + "konjungtiva_lain=?,area_genital=?,cara_bak=?,produksi_urine=?,warna_urine=?,nafsu_makan=?,porsi_makan=?,minum_cc=?,"
            + "jenis_cairan=?,mulut=?,eliminasi_bab=?,bab_frekuensi=?,bab_lain=?,pergerakan_sendi=?,kontraktur_lokasi=?,"
            + "fraktur_lokasi=?,infus_lokasi=?,warna_kulit=?,oedema_tungkai=?,oedema_lokasi=?,dekubitus=?,dekubitus_lokasi=?,"
            + "nyeri=?,skala_nyeri=?,nyeri_lokasi=?,psikososial=?,spiritual_pelayanan=?,spiritual_pelayanan_oleh=?,"
            + "spiritual_didoakan=?,spiritual_didoakan_oleh=?,spiritual_bimbingan=?,spiritual_bimbingan_oleh=?,"
            + "spiritual_pendampingan=?,spiritual_pendampingan_oleh=?,nip=?";
        String[] d = dataArray();
        // params: 58 kolom update (indeks 2..59 dari dataArray) + 2 acuan (no_rawat,tanggal asli)
        String[] a = new String[60];
        for (int i = 0; i < 58; i++) a[i] = d[i + 2];
        a[58] = tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString();
        a[59] = tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString();
        if (Sequel.mengedittf("asesmen_pasien_terminal", "no_rawat=? and tanggal=?", update, 60, a)) {
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
        Valid.MyReportqry("rptAsesmenPasienTerminal.jasper", "report", "::[ Asesmen Pasien Tahap Terminal ]::",
            "select a.*, p.nm_pasien, p.no_rkm_medis, p.jk, DATE_FORMAT(p.tgl_lahir,'%d-%m-%Y') as tgl_lahir, "
            + "concat(p.alamat) as alamat_pasien, dk.nm_dokter as dpjp, pg.nama as nama_petugas, "
            + "DATE_FORMAT(a.tanggal,'%d-%m-%Y') as tanggal_cetak, "
            + "ifnull((select bangsal.nm_bangsal from kamar_inap join kamar on kamar_inap.kd_kamar=kamar.kd_kamar join bangsal on kamar.kd_bangsal=bangsal.kd_bangsal where kamar_inap.no_rawat=a.no_rawat order by kamar_inap.tgl_masuk desc, kamar_inap.jam_masuk desc limit 1), poliklinik.nm_poli) as ruangan "
            + "from asesmen_pasien_terminal a "
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
                "select a.no_rawat, r.no_rkm_medis, p.nm_pasien, a.tanggal, a.kesadaran, a.skala_nyeri, a.psikososial, pg.nama "
                + "from asesmen_pasien_terminal a "
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
                        rs.getString("tanggal"), rs.getString("kesadaran"), rs.getString("skala_nyeri"),
                        rs.getString("psikososial"), rs.getString("nama")
                    });
                }
                LCount.setText("Record : " + tabMode.getRowCount());
            } catch (Exception e) { System.out.println("Notif Asesmen Terminal : " + e);
            } finally { if (rs != null) rs.close(); if (ps != null) ps.close(); }
        } catch (Exception e) { System.out.println("Notif Asesmen Terminal : " + e); }
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
            ps = koneksi.prepareStatement("select * from asesmen_pasien_terminal where no_rawat=? and tanggal=?");
            ps.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            ps.setString(2, tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                cmbPemahaman.setSelectedItem(rs.getString("tingkat_pemahaman"));
                selSet(cbTipeNafas, rs.getString("tipe_pernapasan"));
                txtTipeNafasLain.setText(rs.getString("tipe_pernapasan_lain"));
                txtFrekNafas.setText(rs.getString("frekuensi_nafas"));
                selSet(cbSuaraNafas, rs.getString("suara_nafas"));
                cmbSputum.setSelectedItem(rs.getString("sputum"));
                txtSputumWarna.setText(rs.getString("sputum_warna"));
                txtSputumLain.setText(rs.getString("sputum_lain"));
                cmbIrama.setSelectedItem(rs.getString("irama_jantung"));
                txtFrekNadi.setText(rs.getString("frekuensi_nadi"));
                txtTD.setText(rs.getString("td"));
                selSet(cbEkstremitas, rs.getString("ekstremitas"));
                txtSuhu.setText(rs.getString("suhu"));
                cmbPulsasi.setSelectedItem(rs.getString("pulsasi"));
                txtCVLine.setText(rs.getString("cv_line"));
                txtMAP.setText(rs.getString("map"));
                txtPerdarahan.setText(rs.getString("perdarahan_lokasi"));
                txtGcsMata.setText(rs.getString("gcs_mata"));
                txtGcsMotorik.setText(rs.getString("gcs_motorik"));
                txtGcsVerbal.setText(rs.getString("gcs_verbal"));
                cmbKesadaran.setSelectedItem(rs.getString("kesadaran"));
                selSet(cbTik, rs.getString("tanda_tik"));
                cmbKonjungtiva.setSelectedItem(rs.getString("konjungtiva"));
                txtKonjungtivaLain.setText(rs.getString("konjungtiva_lain"));
                cmbAreaGenital.setSelectedItem(rs.getString("area_genital"));
                cmbCaraBak.setSelectedItem(rs.getString("cara_bak"));
                txtProduksiUrine.setText(rs.getString("produksi_urine"));
                txtWarnaUrine.setText(rs.getString("warna_urine"));
                selSet(cbNafsuMakan, rs.getString("nafsu_makan"));
                cmbPorsiMakan.setSelectedItem(rs.getString("porsi_makan"));
                txtMinumCc.setText(rs.getString("minum_cc"));
                txtJenisCairan.setText(rs.getString("jenis_cairan"));
                selSet(cbMulut, rs.getString("mulut"));
                cmbEliminasiBab.setSelectedItem(rs.getString("eliminasi_bab"));
                txtBabFrek.setText(rs.getString("bab_frekuensi"));
                txtBabLain.setText(rs.getString("bab_lain"));
                cmbPergerakan.setSelectedItem(rs.getString("pergerakan_sendi"));
                txtKontraktur.setText(rs.getString("kontraktur_lokasi"));
                txtFraktur.setText(rs.getString("fraktur_lokasi"));
                txtInfus.setText(rs.getString("infus_lokasi"));
                selSet(cbWarnaKulit, rs.getString("warna_kulit"));
                cmbOedema.setSelectedItem(rs.getString("oedema_tungkai"));
                txtOedemaLokasi.setText(rs.getString("oedema_lokasi"));
                cmbDekubitus.setSelectedItem(rs.getString("dekubitus"));
                txtDekubitusLokasi.setText(rs.getString("dekubitus_lokasi"));
                cmbNyeri.setSelectedItem(rs.getString("nyeri"));
                cmbSkalaNyeri.setSelectedItem(rs.getString("skala_nyeri"));
                txtNyeriLokasi.setText(rs.getString("nyeri_lokasi"));
                cmbPsikososial.setSelectedItem(rs.getString("psikososial"));
                cmbSpPelayanan.setSelectedItem(rs.getString("spiritual_pelayanan"));
                txtSpPelayananOleh.setText(rs.getString("spiritual_pelayanan_oleh"));
                cmbSpDidoakan.setSelectedItem(rs.getString("spiritual_didoakan"));
                txtSpDidoakanOleh.setText(rs.getString("spiritual_didoakan_oleh"));
                cmbSpBimbingan.setSelectedItem(rs.getString("spiritual_bimbingan"));
                txtSpBimbinganOleh.setText(rs.getString("spiritual_bimbingan_oleh"));
                cmbSpPendampingan.setSelectedItem(rs.getString("spiritual_pendampingan"));
                txtSpPendampinganOleh.setText(rs.getString("spiritual_pendampingan_oleh"));
                NIP.setText(rs.getString("nip"));
                NmPetugas.setText(Sequel.cariIsi("select nama from petugas where nip=?", NIP.getText()));
            }
        } catch (Exception e) { System.out.println("Notif getData Asesmen Terminal : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void emptTeks() {
        Tgl.setDate(new Date());
        cmbPemahaman.setSelectedIndex(0);
        for (JCheckBox c : cbTipeNafas) c.setSelected(false);
        for (JCheckBox c : cbSuaraNafas) c.setSelected(false);
        for (JCheckBox c : cbEkstremitas) c.setSelected(false);
        for (JCheckBox c : cbTik) c.setSelected(false);
        for (JCheckBox c : cbNafsuMakan) c.setSelected(false);
        for (JCheckBox c : cbMulut) c.setSelected(false);
        for (JCheckBox c : cbWarnaKulit) c.setSelected(false);
        txtTipeNafasLain.setText(""); txtFrekNafas.setText(""); txtSputumWarna.setText(""); txtSputumLain.setText("");
        txtFrekNadi.setText(""); txtTD.setText(""); txtSuhu.setText(""); txtCVLine.setText(""); txtMAP.setText(""); txtPerdarahan.setText("");
        txtGcsMata.setText(""); txtGcsMotorik.setText(""); txtGcsVerbal.setText(""); txtKonjungtivaLain.setText("");
        txtProduksiUrine.setText(""); txtWarnaUrine.setText("");
        txtMinumCc.setText(""); txtJenisCairan.setText(""); txtBabFrek.setText(""); txtBabLain.setText("");
        txtKontraktur.setText(""); txtFraktur.setText(""); txtInfus.setText(""); txtOedemaLokasi.setText(""); txtDekubitusLokasi.setText("");
        txtNyeriLokasi.setText("");
        txtSpPelayananOleh.setText(""); txtSpDidoakanOleh.setText(""); txtSpBimbinganOleh.setText(""); txtSpPendampinganOleh.setText("");
        for (ComboBox c : new ComboBox[]{cmbSputum, cmbIrama, cmbPulsasi, cmbKesadaran, cmbKonjungtiva, cmbAreaGenital,
                cmbCaraBak, cmbPorsiMakan, cmbEliminasiBab, cmbPergerakan, cmbOedema, cmbDekubitus, cmbNyeri,
                cmbSkalaNyeri, cmbPsikososial, cmbSpPelayanan, cmbSpDidoakan, cmbSpBimbingan, cmbSpPendampingan}) {
            c.setSelectedIndex(0);
        }
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
            RMAsesmenPasienTerminal dialog = new RMAsesmenPasienTerminal(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosing(java.awt.event.WindowEvent e) { System.exit(0); }
            });
            dialog.setVisible(true);
        });
    }
}
