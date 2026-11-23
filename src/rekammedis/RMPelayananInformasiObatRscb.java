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
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import widget.*;

public class RMPelayananInformasiObatRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan, TglJawab;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private TextBox KdApoteker, NmApoteker;
    private TextBox TNamaPenanya, TUmur, TBerat;
    private ComboBox CmbMetode, CmbStatus, CmbJK, CmbKehamilan, CmbMenyusui, CmbPenyampaian, CmbMetodeJawab;
    private TextArea TUraianPertanyaan, TJawaban, TReferensi;
    private CekBox ChkIdentifikasi, ChkEfekSamping, ChkHarga, ChkPenggunaanTerapeutik, ChkCaraPemakaian;
    private CekBox ChkKetersediaan, ChkInteraksi, ChkStabilitas, ChkKontraIndikasi, ChkLainLain, ChkDosis;
    private Button BtnApoteker;
    private Label LCount;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);

    private widget.Table tbObat;
    private DefaultTableModel tabMode;
    private ScrollPane Scroll;
    private CekBox ChkInput;
    private PanelBiasa FormInput;
    private ScrollPane ScrollInput;
    private JPanel PanelInput;
    public widget.editorpane LoadHTML;

    public RMPelayananInformasiObatRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(950, 700);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Pelayanan Informasi Obat ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // PANEL ATAS
        panelisi panelAtas = new panelisi();
        panelAtas.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 4));
        panelAtas.setPreferredSize(new Dimension(1024, 33));
        Label lblNoRw = new Label(); lblNoRw.setText("No.Rawat :");
        TNoRw = new TextBox(); TNoRw.setPreferredSize(new Dimension(130, 23));
        TNoRM = new TextBox(); TNoRM.setPreferredSize(new Dimension(100, 23));
        TPasien = new TextBox(); TPasien.setPreferredSize(new Dimension(200, 23));
        panelAtas.add(lblNoRw); panelAtas.add(TNoRw); panelAtas.add(TNoRM); panelAtas.add(TPasien);

        // TABLE
        Scroll = new ScrollPane();
        tbObat = new widget.Table();
        tabMode = new DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","Waktu","Metode","Penanya","Status","Umur","Berat","JK","Kehamilan","Menyusui","Penyampaian","Apoteker"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbObat.setModel(tabMode);
        JPopupMenu popupTable = new JPopupMenu();
        JMenuItem menuCetak = new JMenuItem("Cetak");
        menuCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        menuCetak.addActionListener(evt -> BtnPrint.doClick());
        popupTable.add(menuCetak);
        tbObat.setComponentPopupMenu(popupTable);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { if (tabMode.getRowCount() != 0) getData(); }
            @Override public void mousePressed(java.awt.event.MouseEvent evt) {
                if (evt.isPopupTrigger()) { int row = tbObat.rowAtPoint(evt.getPoint()); if (row >= 0) { tbObat.setRowSelectionInterval(row, row); getData(); } }
            }
            @Override public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (evt.isPopupTrigger()) { int row = tbObat.rowAtPoint(evt.getPoint()); if (row >= 0) { tbObat.setRowSelectionInterval(row, row); getData(); } }
            }
        });
        Scroll.setViewportView(tbObat);

        // FORM INPUT
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(920, 820));
        FormInput.setLayout(null);

        int y = 10;
        JLabel lblHeader = new JLabel("PELAYANAN INFORMASI OBAT");
        lblHeader.setFont(new java.awt.Font("Tahoma", 1, 13)); lblHeader.setBounds(10, y, 350, 20); FormInput.add(lblHeader);
        JLabel lblKode = new JLabel("RM.FAR.03.007.2019");
        lblKode.setFont(new java.awt.Font("Tahoma", 0, 8)); lblKode.setBounds(750, y, 150, 20); FormInput.add(lblKode);

        y += 32;
        Label lblTgl = new Label(); lblTgl.setText("Tanggal :"); lblTgl.setBounds(10, y, 80, 23); FormInput.add(lblTgl);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setBounds(95, y, 185, 23); FormInput.add(TglAsuhan);

        y += 32;
        Label lblMetode = new Label(); lblMetode.setText("Metode :"); lblMetode.setBounds(10, y, 80, 23); FormInput.add(lblMetode);
        CmbMetode = new ComboBox(); CmbMetode.setModel(new DefaultComboBoxModel(new String[]{"Lisan","Tertulis","Telephone"}));
        CmbMetode.setBounds(95, y, 120, 23); FormInput.add(CmbMetode);

        y += 32;
        Label lblPenanya = new Label(); lblPenanya.setText("Penanya :"); lblPenanya.setBounds(10, y, 80, 23); FormInput.add(lblPenanya);
        TNamaPenanya = new TextBox(); TNamaPenanya.setBounds(95, y, 200, 23); FormInput.add(TNamaPenanya);
        Label lblStatusP = new Label(); lblStatusP.setText("Status :"); lblStatusP.setBounds(310, y, 50, 23); FormInput.add(lblStatusP);
        CmbStatus = new ComboBox(); CmbStatus.setModel(new DefaultComboBoxModel(new String[]{"Pasien","Keluarga","Petugas Kesehatan"}));
        CmbStatus.setBounds(365, y, 150, 23); FormInput.add(CmbStatus);

        y += 32;
        Label lblUmur = new Label(); lblUmur.setText("Umur :"); lblUmur.setBounds(10, y, 80, 23); FormInput.add(lblUmur);
        TUmur = new TextBox(); TUmur.setBounds(95, y, 60, 23); FormInput.add(TUmur);
        Label lblBerat = new Label(); lblBerat.setText("Berat :"); lblBerat.setBounds(165, y, 40, 23); FormInput.add(lblBerat);
        TBerat = new TextBox(); TBerat.setBounds(210, y, 60, 23); FormInput.add(TBerat);
        Label lblJK = new Label(); lblJK.setText("JK :"); lblJK.setBounds(280, y, 30, 23); FormInput.add(lblJK);
        CmbJK = new ComboBox(); CmbJK.setModel(new DefaultComboBoxModel(new String[]{"L","P"}));
        CmbJK.setBounds(310, y, 50, 23); FormInput.add(CmbJK);
        Label lblHamil = new Label(); lblHamil.setText("Hamil :"); lblHamil.setBounds(370, y, 45, 23); FormInput.add(lblHamil);
        CmbKehamilan = new ComboBox(); CmbKehamilan.setModel(new DefaultComboBoxModel(new String[]{"Tidak","Ya"}));
        CmbKehamilan.setBounds(420, y, 70, 23); FormInput.add(CmbKehamilan);
        Label lblMenyusui = new Label(); lblMenyusui.setText("Menyusui :"); lblMenyusui.setBounds(500, y, 65, 23); FormInput.add(lblMenyusui);
        CmbMenyusui = new ComboBox(); CmbMenyusui.setModel(new DefaultComboBoxModel(new String[]{"Tidak","Ya"}));
        CmbMenyusui.setBounds(570, y, 70, 23); FormInput.add(CmbMenyusui);

        y += 32;
        Label lblUraian = new Label(); lblUraian.setText("Pertanyaan :"); lblUraian.setBounds(10, y, 80, 23); FormInput.add(lblUraian);
        TUraianPertanyaan = new TextArea(); TUraianPertanyaan.setBounds(95, y, 400, 50);
        TUraianPertanyaan.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        FormInput.add(TUraianPertanyaan);

        y += 60;
        Label lblJenis = new Label(); lblJenis.setText("Jenis :"); lblJenis.setBounds(10, y, 80, 23); FormInput.add(lblJenis);
        ChkIdentifikasi = new CekBox(); ChkIdentifikasi.setText("Identifikasi"); ChkIdentifikasi.setBounds(95, y, 110, 20); ChkIdentifikasi.setOpaque(false); FormInput.add(ChkIdentifikasi);
        ChkEfekSamping = new CekBox(); ChkEfekSamping.setText("Efek Samping"); ChkEfekSamping.setBounds(210, y, 120, 20); ChkEfekSamping.setOpaque(false); FormInput.add(ChkEfekSamping);
        ChkHarga = new CekBox(); ChkHarga.setText("Harga"); ChkHarga.setBounds(335, y, 70, 20); ChkHarga.setOpaque(false); FormInput.add(ChkHarga);
        ChkPenggunaanTerapeutik = new CekBox(); ChkPenggunaanTerapeutik.setText("Peng. Terapeutik"); ChkPenggunaanTerapeutik.setBounds(410, y, 140, 20); ChkPenggunaanTerapeutik.setOpaque(false); FormInput.add(ChkPenggunaanTerapeutik);
        ChkCaraPemakaian = new CekBox(); ChkCaraPemakaian.setText("Cara Pemakaian"); ChkCaraPemakaian.setBounds(555, y, 130, 20); ChkCaraPemakaian.setOpaque(false); FormInput.add(ChkCaraPemakaian);
        ChkDosis = new CekBox(); ChkDosis.setText("Dosis"); ChkDosis.setBounds(690, y, 70, 20); ChkDosis.setOpaque(false); FormInput.add(ChkDosis);

        y += 22;
        ChkKetersediaan = new CekBox(); ChkKetersediaan.setText("Ketersediaan"); ChkKetersediaan.setBounds(95, y, 110, 20); ChkKetersediaan.setOpaque(false); FormInput.add(ChkKetersediaan);
        ChkInteraksi = new CekBox(); ChkInteraksi.setText("Interaksi"); ChkInteraksi.setBounds(210, y, 100, 20); ChkInteraksi.setOpaque(false); FormInput.add(ChkInteraksi);
        ChkStabilitas = new CekBox(); ChkStabilitas.setText("Stabilitas"); ChkStabilitas.setBounds(315, y, 90, 20); ChkStabilitas.setOpaque(false); FormInput.add(ChkStabilitas);
        ChkKontraIndikasi = new CekBox(); ChkKontraIndikasi.setText("Kontra Indikasi"); ChkKontraIndikasi.setBounds(410, y, 130, 20); ChkKontraIndikasi.setOpaque(false); FormInput.add(ChkKontraIndikasi);
        ChkLainLain = new CekBox(); ChkLainLain.setText("Lain-lain"); ChkLainLain.setBounds(545, y, 90, 20); ChkLainLain.setOpaque(false); FormInput.add(ChkLainLain);

        y += 32;
        Label lblJawaban = new Label(); lblJawaban.setText("Jawaban :"); lblJawaban.setBounds(10, y, 80, 23); FormInput.add(lblJawaban);
        TJawaban = new TextArea(); TJawaban.setBounds(95, y, 400, 50);
        TJawaban.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        FormInput.add(TJawaban);

        y += 60;
        Label lblRef = new Label(); lblRef.setText("Referensi :"); lblRef.setBounds(10, y, 80, 23); FormInput.add(lblRef);
        TReferensi = new TextArea(); TReferensi.setBounds(95, y, 400, 50);
        TReferensi.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        FormInput.add(TReferensi);

        y += 60;
        Label lblPenyampaian = new Label(); lblPenyampaian.setText("Penyampaian :"); lblPenyampaian.setBounds(10, y, 80, 23); FormInput.add(lblPenyampaian);
        CmbPenyampaian = new ComboBox(); CmbPenyampaian.setModel(new DefaultComboBoxModel(new String[]{"Segera","24 Jam","> 24 Jam"}));
        CmbPenyampaian.setBounds(95, y, 100, 23); FormInput.add(CmbPenyampaian);

        y += 32;
        Label lblApoteker = new Label(); lblApoteker.setText("Apoteker :"); lblApoteker.setBounds(10, y, 80, 23); FormInput.add(lblApoteker);
        KdApoteker = new TextBox(); KdApoteker.setBounds(95, y, 80, 23); FormInput.add(KdApoteker);
        NmApoteker = new TextBox(); NmApoteker.setBounds(177, y, 180, 23); FormInput.add(NmApoteker);
        BtnApoteker = new Button(); BtnApoteker.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnApoteker.setBounds(359, y, 28, 23); FormInput.add(BtnApoteker);
        BtnApoteker.addActionListener(evt -> {
            petugasDlg.emptTeks(); petugasDlg.isCek();
            petugasDlg.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20);
            petugasDlg.setLocationRelativeTo(internalFrame1); petugasDlg.setVisible(true);
        });
        petugasDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (petugasDlg.getTable().getSelectedRow() != -1) {
                    KdApoteker.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(), 0).toString());
                    NmApoteker.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

        y += 32;
        Label lblTglJawab = new Label(); lblTglJawab.setText("Tgl Jawab :"); lblTglJawab.setBounds(10, y, 80, 23); FormInput.add(lblTglJawab);
        TglJawab = new widget.Tanggal(); TglJawab.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglJawab.setBounds(95, y, 185, 23); FormInput.add(TglJawab);
        Label lblMetodeJawab = new Label(); lblMetodeJawab.setText("Metode :"); lblMetodeJawab.setBounds(380, y, 55, 23); FormInput.add(lblMetodeJawab);
        CmbMetodeJawab = new ComboBox(); CmbMetodeJawab.setModel(new DefaultComboBoxModel(new String[]{"Lisan","Tertulis","Telephone"}));
        CmbMetodeJawab.setBounds(440, y, 120, 23); FormInput.add(CmbMetodeJawab);

        // Input toggle
        ChkInput = new CekBox();
        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setMnemonic('1'); ChkInput.setText(".: Input Data"); ChkInput.setToolTipText("Alt+1");
        ChkInput.setBorderPainted(true); ChkInput.setBorderPaintedFlat(true); ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput.setPreferredSize(new Dimension(192, 20));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setSelected(true);
        ChkInput.addActionListener(evt -> isForm());

        ScrollInput = new ScrollPane(); ScrollInput.setViewportView(FormInput);
        ScrollInput.setPreferredSize(new Dimension(920, 550));
        PanelInput = new JPanel(new BorderLayout());
        PanelInput.setOpaque(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 570));
        PanelInput.add(ScrollInput, BorderLayout.CENTER); PanelInput.add(ChkInput, BorderLayout.PAGE_END);

        // PANEL BAWAH
        panelBawah = new panelisi(); panelBawah.setPreferredSize(new Dimension(44, 54));
        panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));
        BtnSimpan = new Button(); BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setMnemonic('S'); BtnSimpan.setText("Simpan"); BtnSimpan.setPreferredSize(new Dimension(100, 30));
        BtnBatal = new Button(); BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBatal.setMnemonic('B'); BtnBatal.setText("Baru"); BtnBatal.setPreferredSize(new Dimension(100, 30));
        BtnHapus = new Button(); BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setMnemonic('H'); BtnHapus.setText("Hapus"); BtnHapus.setPreferredSize(new Dimension(100, 30));
        BtnEdit = new Button(); BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnEdit.setMnemonic('G'); BtnEdit.setText("Ganti"); BtnEdit.setPreferredSize(new Dimension(100, 30));
        BtnPrint = new Button(); BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        BtnPrint.setMnemonic('T'); BtnPrint.setText("Cetak"); BtnPrint.setPreferredSize(new Dimension(100, 30));
        BtnKeluar = new Button(); BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K'); BtnKeluar.setText("Keluar"); BtnKeluar.setPreferredSize(new Dimension(100, 30));
        Label lblTglC = new Label(); lblTglC.setText("Tanggal :"); lblTglC.setPreferredSize(new Dimension(55, 23));
        DTPCari1 = new widget.Tanggal(); DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setPreferredSize(new Dimension(90, 23));
        Label lblSd = new Label(); lblSd.setText("s.d."); lblSd.setPreferredSize(new Dimension(25, 23));
        DTPCari2 = new widget.Tanggal(); DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setPreferredSize(new Dimension(90, 23));
        Label lblKey = new Label(); lblKey.setText("Key Word :"); lblKey.setPreferredSize(new Dimension(70, 23));
        TCari = new TextBox(); TCari.setPreferredSize(new Dimension(150, 23));
        BtnCari = new Button(); BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnAll = new Button(); BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setPreferredSize(new Dimension(28, 23));
        LCount = new Label(); LCount.setText("Record : 0"); LCount.setPreferredSize(new Dimension(100, 23));
        panelBawah.add(BtnSimpan); panelBawah.add(BtnBatal); panelBawah.add(BtnHapus);
        panelBawah.add(BtnEdit); panelBawah.add(BtnPrint); panelBawah.add(BtnKeluar);
        panelBawah.add(lblTglC); panelBawah.add(DTPCari1); panelBawah.add(lblSd);
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

        BtnSimpan.addActionListener(evt -> {
            if (TNoRw.getText().trim().equals("")) { Valid.textKosong(TNoRw, "Pasien");
            } else if (KdApoteker.getText().trim().equals("")) { Valid.textKosong(KdApoteker, "Apoteker");
            } else {
                String tanggal = Valid.SetTglJam(TglAsuhan.getSelectedItem()+"");
                String jenisPertanyaan = getJenisPertanyaan();
                String tglJawab = Valid.SetTglJam(TglJawab.getSelectedItem()+"");
                String waktuJawab = (TglJawab.getSelectedItem()+"").length()>11?(TglJawab.getSelectedItem()+"").substring(11):"00:00:00";
                if (Sequel.menyimpantf("pelayanan_informasi_obat_rscb",
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 22, new String[]{
                    TNoRw.getText(), tanggal, (TglAsuhan.getSelectedItem()+"").length()>11?(TglAsuhan.getSelectedItem()+"").substring(11):"00:00:00",
                    CmbMetode.getSelectedItem().toString(), TNamaPenanya.getText(),
                    CmbStatus.getSelectedItem().toString(), TUmur.getText(), TBerat.getText(),
                    CmbJK.getSelectedItem().toString(), CmbKehamilan.getSelectedItem().toString(),
                    "", "",
                    CmbMenyusui.getSelectedItem().toString(), TUraianPertanyaan.getText(),
                    jenisPertanyaan, TJawaban.getText(), TReferensi.getText(),
                    CmbPenyampaian.getSelectedItem().toString(), KdApoteker.getText(),
                    tglJawab, waktuJawab, CmbMetodeJawab.getSelectedItem().toString()
                })) { tampil(); emptTeks(); }
            }
        });

        BtnHapus.addActionListener(evt -> {
            if (tbObat.getSelectedRow() != -1) {
                String noRawat = tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString();
                String tgl = tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString();
                if (Sequel.queryu2tf("delete from pelayanan_informasi_obat_rscb where no_rawat=? and tanggal=?", 2, new String[]{noRawat, tgl})) {
                    tampil(); emptTeks();
                }
            } else JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dihapus...!!!!");
        });

        BtnEdit.addActionListener(evt -> {
            if (TNoRw.getText().trim().equals("") || tbObat.getSelectedRow()==-1) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            } else {
                String noRawat = tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString();
                String tgl = tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString();
                if (Sequel.queryu2tf("delete from pelayanan_informasi_obat_rscb where no_rawat=? and tanggal=?", 2, new String[]{noRawat, tgl})) {
                    BtnSimpan.doClick();
                }
            }
        });

        BtnPrint.addActionListener(evt -> {
            if (TNoRw.getText().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu..!!!");
            } else {
                Map<String, Object> param = new HashMap<>();
                param.put("namars", akses.getnamars()); param.put("alamatrs", akses.getalamatrs());
                param.put("propinsirs", akses.getpropinsirs()); param.put("kabupatenrs", akses.getkabupatenrs());
                param.put("kontakrs", akses.getkontakrs()); param.put("emailrs", akses.getemailrs());
                param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
                String nipApoteker = KdApoteker.getText();
                String finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", nipApoteker);
                param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + NmApoteker.getText() + "\nID " + (finger == null || finger.isEmpty() ? nipApoteker : finger));
                Valid.MyReportqry("rptPelayananInformasiObatRscb.jasper", "report", "::[ Pelayanan Informasi Obat ]::",
                    "select h.no_rawat, h.tanggal, h.waktu, h.metode, h.nama_penanya, h.status_penanya, " +
                    "h.umur, h.berat, h.jenis_kelamin as jk, h.kehamilan, h.menyusui, " +
                    "h.uraian_pertanyaan, h.jenis_pertanyaan, h.jawaban, h.referensi, h.penyampaian, " +
                    "h.nip_apoteker as nip, " +
                    "CONCAT(IFNULL(h.tgl_jawab,''),'|',IFNULL(h.waktu_jawab,''),'|',IFNULL(h.metode_jawab,'')) as jawab_info, " +
                    "r.no_rkm_medis, p.nm_pasien, pt.nama as nm_petugas " +
                    "from pelayanan_informasi_obat_rscb h inner join reg_periksa r on h.no_rawat=r.no_rawat " +
                    "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis left join petugas pt on h.nip_apoteker=pt.nip " +
                    "where h.no_rawat='" + TNoRw.getText() + "'", param);
            }
        });

        // ASSEMBLY
        InternalFrame internalFrameData = new InternalFrame();
        internalFrameData.setLayout(new BorderLayout());
        internalFrameData.setBackground(new Color(255, 255, 255));
        internalFrameData.add(PanelInput, BorderLayout.NORTH);
        internalFrameData.add(Scroll, BorderLayout.CENTER);
        internalFrame1.add(panelAtas, BorderLayout.PAGE_START);
        internalFrame1.add(internalFrameData, BorderLayout.CENTER);
        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);
        setLayout(new BorderLayout());
        add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
    }

    private String getJenisPertanyaan() {
        StringBuilder sb = new StringBuilder();
        if (ChkIdentifikasi.isSelected()) sb.append("Identifikasi,");
        if (ChkEfekSamping.isSelected()) sb.append("Efek Samping,");
        if (ChkHarga.isSelected()) sb.append("Harga,");
        if (ChkPenggunaanTerapeutik.isSelected()) sb.append("Penggunaan Terapeutik,");
        if (ChkCaraPemakaian.isSelected()) sb.append("Cara Pemakaian,");
        if (ChkKetersediaan.isSelected()) sb.append("Ketersediaan,");
        if (ChkInteraksi.isSelected()) sb.append("Interaksi,");
        if (ChkStabilitas.isSelected()) sb.append("Stabilitas,");
        if (ChkKontraIndikasi.isSelected()) sb.append("Kontra Indikasi,");
        if (ChkLainLain.isSelected()) sb.append("Lain-lain,");
        if (ChkDosis.isSelected()) sb.append("Dosis,");
        return sb.length() > 0 ? sb.substring(0, sb.length()-1) : "";
    }

    private void setJenisPertanyaan(String jenis) {
        ChkIdentifikasi.setSelected(jenis.contains("Identifikasi"));
        ChkEfekSamping.setSelected(jenis.contains("Efek Samping"));
        ChkHarga.setSelected(jenis.contains("Harga"));
        ChkPenggunaanTerapeutik.setSelected(jenis.contains("Penggunaan Terapeutik"));
        ChkCaraPemakaian.setSelected(jenis.contains("Cara Pemakaian"));
        ChkKetersediaan.setSelected(jenis.contains("Ketersediaan"));
        ChkInteraksi.setSelected(jenis.contains("Interaksi"));
        ChkStabilitas.setSelected(jenis.contains("Stabilitas"));
        ChkKontraIndikasi.setSelected(jenis.contains("Kontra Indikasi"));
        ChkLainLain.setSelected(jenis.contains("Lain-lain"));
        ChkDosis.setSelected(jenis.contains("Dosis"));
    }

    private void isForm() {
        if (ChkInput.isSelected()) { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 570)); FormInput.setVisible(true); ChkInput.setVisible(true);
        } else { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 20)); FormInput.setVisible(false); ChkInput.setVisible(true); }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select h.no_rawat, r.no_rkm_medis, p.nm_pasien, h.tanggal, h.waktu, h.metode, h.nama_penanya, " +
                "h.status_penanya, h.umur, h.berat, h.jenis_kelamin as jk, h.kehamilan, h.menyusui, h.penyampaian, pt.nama as nm_petugas " +
                "from pelayanan_informasi_obat_rscb h inner join reg_periksa r on h.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis left join petugas pt on h.nip_apoteker=pt.nip " +
                "where DATE(h.tanggal) between ? and ? and (h.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) " +
                "order by h.tanggal desc");
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem()+""));
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem()+""));
            ps.setString(3, "%"+TCari.getText().trim()+"%");
            ps.setString(4, "%"+TCari.getText().trim()+"%");
            ps.setString(5, "%"+TCari.getText().trim()+"%");
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                    rs.getString("tanggal"), rs.getString("waktu"), rs.getString("metode"),
                    rs.getString("nama_penanya"), rs.getString("status_penanya"), rs.getString("umur"),
                    rs.getString("berat"), rs.getString("jk"), rs.getString("kehamilan"),
                    rs.getString("menyusui"), rs.getString("penyampaian"), rs.getString("nm_petugas")
                });
            }
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif Pelayanan Informasi Obat : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            try {
                String tglStr = tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString();
                try { TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tglStr)); }
                catch (Exception e) { TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(tglStr)); }
            } catch (Exception e) {}
            CmbMetode.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            TNamaPenanya.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
            CmbStatus.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString());
            TUmur.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString());
            TBerat.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString());
            CmbJK.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString());
            CmbKehamilan.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString());
            CmbMenyusui.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString());
            CmbPenyampaian.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 13).toString());
            String noRawat = tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString();
            String tgl = tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString();
            TUraianPertanyaan.setText(Sequel.cariIsi("select uraian_pertanyaan from pelayanan_informasi_obat_rscb where no_rawat='"+noRawat+"' and tanggal='"+tgl+"'"));
            setJenisPertanyaan(Sequel.cariIsi("select jenis_pertanyaan from pelayanan_informasi_obat_rscb where no_rawat='"+noRawat+"' and tanggal='"+tgl+"'"));
            TJawaban.setText(Sequel.cariIsi("select jawaban from pelayanan_informasi_obat_rscb where no_rawat='"+noRawat+"' and tanggal='"+tgl+"'"));
            TReferensi.setText(Sequel.cariIsi("select referensi from pelayanan_informasi_obat_rscb where no_rawat='"+noRawat+"' and tanggal='"+tgl+"'"));
            KdApoteker.setText(Sequel.cariIsi("select nip_apoteker from pelayanan_informasi_obat_rscb where no_rawat='"+noRawat+"' and tanggal='"+tgl+"'"));
            NmApoteker.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 14).toString());
            String tglJawabStr = Sequel.cariIsi("select tgl_jawab from pelayanan_informasi_obat_rscb where no_rawat='"+noRawat+"' and tanggal='"+tgl+"'");
            String wktuJawabStr = Sequel.cariIsi("select waktu_jawab from pelayanan_informasi_obat_rscb where no_rawat='"+noRawat+"' and tanggal='"+tgl+"'");
            String metodeJawabStr = Sequel.cariIsi("select metode_jawab from pelayanan_informasi_obat_rscb where no_rawat='"+noRawat+"' and tanggal='"+tgl+"'");
            if (tglJawabStr != null && !tglJawabStr.isEmpty()) {
                try {
                    String tglJawabFull = tglJawabStr + (wktuJawabStr != null && !wktuJawabStr.isEmpty() ? " " + wktuJawabStr : "");
                    try { TglJawab.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tglJawabFull)); }
                    catch (Exception e) { TglJawab.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(tglJawabStr)); }
                } catch (Exception e) {}
            }
            if (metodeJawabStr != null && !metodeJawabStr.isEmpty()) CmbMetodeJawab.setSelectedItem(metodeJawabStr);
        }
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date()); CmbMetode.setSelectedIndex(0);
        TNamaPenanya.setText(""); CmbStatus.setSelectedIndex(0); TUmur.setText(""); TBerat.setText("");
        CmbJK.setSelectedIndex(0); CmbKehamilan.setSelectedIndex(0); CmbMenyusui.setSelectedIndex(0);
        TUraianPertanyaan.setText(""); TJawaban.setText(""); TReferensi.setText("");
        CmbPenyampaian.setSelectedIndex(0); KdApoteker.setText(""); NmApoteker.setText("");
        TglJawab.setDate(new Date()); CmbMetodeJawab.setSelectedIndex(0);
        ChkIdentifikasi.setSelected(false); ChkEfekSamping.setSelected(false); ChkHarga.setSelected(false);
        ChkPenggunaanTerapeutik.setSelected(false); ChkCaraPemakaian.setSelected(false); ChkKetersediaan.setSelected(false);
        ChkInteraksi.setSelected(false); ChkStabilitas.setSelected(false); ChkKontraIndikasi.setSelected(false);
        ChkLainLain.setSelected(false); ChkDosis.setSelected(false);
    }

    public void setNoRm(String norwt, Date tgl) {
        TNoRw.setText(norwt); TglAsuhan.setDate(tgl);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", norwt));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getrujukan_keluar()); BtnHapus.setEnabled(akses.getrujukan_keluar());
        BtnEdit.setEnabled(akses.getrujukan_keluar()); BtnPrint.setEnabled(akses.getrujukan_keluar());
    }
}
