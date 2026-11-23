package rekammedis;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import kepegawaian.DlgCariDokter;
import kepegawaian.DlgCariPetugas;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import widget.*;

public class RMKonversiAnestesiRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private Label LCount;

    private TextBox TxtRuangan, TxtJamAwal, TxtJamKonversi;
    private TextBox TxtObatLokal, TxtObatRegional, TxtObatEpidural, TxtObatTiva, TxtObatInhalasi, TxtObatAnalgetik;
    private CekBox ChkPerubahanRencana;
    private ComboBox CmbAnestesiAwal, CmbKonversiKe, CmbTehnikJalanNapas;
    private TextBox KdDokter, NmDokter, NIP, NamaPetugas;
    private Button BtnDokter, BtnPetugas;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private int i = 0;
    private DlgCariDokter dokterDlg = new DlgCariDokter(null, false);
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);

    private widget.Table tbObat;
    private DefaultTableModel tabMode;
    private ScrollPane Scroll;
    private CekBox ChkInput;
    private PanelBiasa FormInput;
    private ScrollPane ScrollInput;
    private JPanel PanelInput;
    public widget.editorpane LoadHTML;

    public RMKonversiAnestesiRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(900, 680);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Konversi Anestesi ]::",
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
        TNoRM = new TextBox(); TNoRM.setPreferredSize(new Dimension(100, 23)); TNoRM.setEditable(false);
        TPasien = new TextBox(); TPasien.setPreferredSize(new Dimension(200, 23)); TPasien.setEditable(false);
        panelAtas.add(lblNoRw); panelAtas.add(TNoRw); panelAtas.add(TNoRM); panelAtas.add(TPasien);

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        TCari = new TextBox();
        TCari.setDocument(new batasInput((int) 100).getKata(TCari));

        // TABLE
        Scroll = new ScrollPane();
        tbObat = new widget.Table();
        tabMode = new DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat", "No.R.M.", "Nama", "Tanggal", "Ruangan", "Anestesi Awal",
                          "Konversi Ke", "Kd.Dokter", "Dokter Anestesi", "NIP", "Petugas"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 11; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) column.setPreferredWidth(105);
            else if (i == 1) column.setPreferredWidth(65);
            else if (i == 2) column.setPreferredWidth(160);
            else if (i == 3) column.setPreferredWidth(122);
            else if (i == 4) column.setPreferredWidth(100);
            else if (i == 5) column.setPreferredWidth(120);
            else if (i == 6) column.setPreferredWidth(120);
            else if (i == 7) column.setPreferredWidth(80);
            else if (i == 8) column.setPreferredWidth(160);
            else if (i == 9) column.setPreferredWidth(80);
            else if (i == 10) column.setPreferredWidth(160);
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { if (tabMode.getRowCount() != 0) getData(); }
        });
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem mnCetak = new JMenuItem("Cetak Konversi Anestesi");
        mnCetak.setIcon(new ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetak.addActionListener(evt -> cetakKonversi());
        popupMenu.add(mnCetak);
        tbObat.setComponentPopupMenu(popupMenu);
        Scroll.setViewportView(tbObat);

        // FORM INPUT
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(880, 580));
        FormInput.setLayout(null);

        int y = 10;
        Label lblTgl = new Label(); lblTgl.setText("Tanggal :"); lblTgl.setBounds(0, y, 130, 23); FormInput.add(lblTgl);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setBounds(135, y, 130, 23); FormInput.add(TglAsuhan);

        y += 30;
        Label lblRuangan = new Label(); lblRuangan.setText("Ruangan :"); lblRuangan.setBounds(0, y, 130, 23); FormInput.add(lblRuangan);
        TxtRuangan = new TextBox(); TxtRuangan.setEditable(false); TxtRuangan.setBounds(135, y, 250, 23); FormInput.add(TxtRuangan);

        y += 30;
        Label lblPerubahanRencana = new Label(); lblPerubahanRencana.setText("Perubahan Rencana :"); lblPerubahanRencana.setBounds(0, y, 130, 23); FormInput.add(lblPerubahanRencana);
        ChkPerubahanRencana = new CekBox(); ChkPerubahanRencana.setText("Ya");
        ChkPerubahanRencana.setSelected(false);
        ChkPerubahanRencana.setBounds(135, y, 60, 23); FormInput.add(ChkPerubahanRencana);

        y += 30;
        Label lblAnestesiAwal = new Label(); lblAnestesiAwal.setText("Anestesi Awal :"); lblAnestesiAwal.setBounds(0, y, 130, 23); FormInput.add(lblAnestesiAwal);
        CmbAnestesiAwal = new ComboBox();
        CmbAnestesiAwal.setModel(new DefaultComboBoxModel(new String[]{"General Anestesi", "Regional Anestesi", "Sedasi", "Lokal Anestesi", "Epidural", "TIVA", "Lainnya"}));
        CmbAnestesiAwal.setBounds(135, y, 200, 23); FormInput.add(CmbAnestesiAwal);

        y += 30;
        Label lblJamAwal = new Label(); lblJamAwal.setText("Jam Awal :"); lblJamAwal.setBounds(0, y, 130, 23); FormInput.add(lblJamAwal);
        TxtJamAwal = new TextBox(); TxtJamAwal.setBounds(135, y, 80, 23); FormInput.add(TxtJamAwal);

        y += 30;
        Label lblKonversiKe = new Label(); lblKonversiKe.setText("Konversi Ke :"); lblKonversiKe.setBounds(0, y, 130, 23); FormInput.add(lblKonversiKe);
        CmbKonversiKe = new ComboBox();
        CmbKonversiKe.setModel(new DefaultComboBoxModel(new String[]{"General Anestesi", "Regional Anestesi", "Sedasi", "Lokal Anestesi", "Epidural", "TIVA", "Lainnya"}));
        CmbKonversiKe.setBounds(135, y, 200, 23); FormInput.add(CmbKonversiKe);

        y += 30;
        Label lblJamKonversi = new Label(); lblJamKonversi.setText("Jam Konversi :"); lblJamKonversi.setBounds(0, y, 130, 23); FormInput.add(lblJamKonversi);
        TxtJamKonversi = new TextBox(); TxtJamKonversi.setBounds(135, y, 80, 23); FormInput.add(TxtJamKonversi);

        y += 32;
        JLabel lblObatSection = new JLabel("Obat-obatan yang digunakan :");
        lblObatSection.setFont(new java.awt.Font("Tahoma", 1, 11)); lblObatSection.setBounds(0, y, 250, 20); FormInput.add(lblObatSection);

        y += 28;
        Label lblObatLokal = new Label(); lblObatLokal.setText("Obat Lokal :"); lblObatLokal.setBounds(0, y, 130, 23); FormInput.add(lblObatLokal);
        TxtObatLokal = new TextBox(); TxtObatLokal.setBounds(135, y, 400, 23); FormInput.add(TxtObatLokal);

        y += 30;
        Label lblObatRegional = new Label(); lblObatRegional.setText("Obat Regional :"); lblObatRegional.setBounds(0, y, 130, 23); FormInput.add(lblObatRegional);
        TxtObatRegional = new TextBox(); TxtObatRegional.setBounds(135, y, 400, 23); FormInput.add(TxtObatRegional);

        y += 30;
        Label lblObatEpidural = new Label(); lblObatEpidural.setText("Obat Epidural :"); lblObatEpidural.setBounds(0, y, 130, 23); FormInput.add(lblObatEpidural);
        TxtObatEpidural = new TextBox(); TxtObatEpidural.setBounds(135, y, 400, 23); FormInput.add(TxtObatEpidural);

        y += 30;
        Label lblObatTiva = new Label(); lblObatTiva.setText("Obat TIVA :"); lblObatTiva.setBounds(0, y, 130, 23); FormInput.add(lblObatTiva);
        TxtObatTiva = new TextBox(); TxtObatTiva.setBounds(135, y, 400, 23); FormInput.add(TxtObatTiva);

        y += 30;
        Label lblObatInhalasi = new Label(); lblObatInhalasi.setText("Obat Inhalasi :"); lblObatInhalasi.setBounds(0, y, 130, 23); FormInput.add(lblObatInhalasi);
        TxtObatInhalasi = new TextBox(); TxtObatInhalasi.setBounds(135, y, 400, 23); FormInput.add(TxtObatInhalasi);

        y += 30;
        Label lblObatAnalgetik = new Label(); lblObatAnalgetik.setText("Obat Analgetik :"); lblObatAnalgetik.setBounds(0, y, 130, 23); FormInput.add(lblObatAnalgetik);
        TxtObatAnalgetik = new TextBox(); TxtObatAnalgetik.setBounds(135, y, 400, 23); FormInput.add(TxtObatAnalgetik);

        y += 30;
        Label lblTehnikJalanNapas = new Label(); lblTehnikJalanNapas.setText("Tehnik Jalan Napas :"); lblTehnikJalanNapas.setBounds(0, y, 130, 23); FormInput.add(lblTehnikJalanNapas);
        CmbTehnikJalanNapas = new ComboBox();
        CmbTehnikJalanNapas.setModel(new DefaultComboBoxModel(new String[]{"Face Mask", "LMA", "ETT Oral", "ETT Nasal", "Tracheostomy", "Lainnya"}));
        CmbTehnikJalanNapas.setBounds(135, y, 200, 23); FormInput.add(CmbTehnikJalanNapas);

        y += 30;
        Label lblDokter = new Label(); lblDokter.setText("Dokter Anestesi :"); lblDokter.setBounds(0, y, 130, 23); FormInput.add(lblDokter);
        KdDokter = new TextBox(); KdDokter.setBounds(135, y, 80, 23); FormInput.add(KdDokter);
        KdDokter.setDocument(new batasInput((byte) 20).getKata(KdDokter));
        NmDokter = new TextBox(); NmDokter.setEditable(false); NmDokter.setBounds(217, y, 200, 23); FormInput.add(NmDokter);
        BtnDokter = new Button(); BtnDokter.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokter.setBounds(419, y, 28, 23); FormInput.add(BtnDokter);
        BtnDokter.addActionListener(evt -> {
            dokterDlg.emptTeks(); dokterDlg.isCek();
            dokterDlg.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20);
            dokterDlg.setLocationRelativeTo(internalFrame1); dokterDlg.setVisible(true);
        });
        dokterDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (dokterDlg.getTable().getSelectedRow() != -1) {
                    KdDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(), 0).toString());
                    NmDokter.setText(dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

        y += 30;
        Label lblPetugas = new Label(); lblPetugas.setText("Petugas :"); lblPetugas.setBounds(0, y, 130, 23); FormInput.add(lblPetugas);
        NIP = new TextBox(); NIP.setEditable(false); NIP.setBounds(135, y, 80, 23); FormInput.add(NIP);
        NIP.setDocument(new batasInput((byte) 20).getKata(NIP));
        NamaPetugas = new TextBox(); NamaPetugas.setEditable(false); NamaPetugas.setBounds(217, y, 200, 23); FormInput.add(NamaPetugas);
        BtnPetugas = new Button(); BtnPetugas.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPetugas.setBounds(419, y, 28, 23); FormInput.add(BtnPetugas);
        BtnPetugas.addActionListener(evt -> {
            petugasDlg.emptTeks(); petugasDlg.isCek();
            petugasDlg.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20);
            petugasDlg.setLocationRelativeTo(internalFrame1); petugasDlg.setVisible(true);
        });
        petugasDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (petugasDlg.getTable().getSelectedRow() != -1) {
                    NIP.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(), 0).toString());
                    NamaPetugas.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

        // Input toggle
        ChkInput = new CekBox();
        ChkInput.setIcon(new ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setMnemonic('1'); ChkInput.setText(".: Input Data"); ChkInput.setToolTipText("Alt+1");
        ChkInput.setBorderPainted(true); ChkInput.setBorderPaintedFlat(true); ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(SwingConstants.RIGHT);
        ChkInput.setPreferredSize(new Dimension(192, 20));
        ChkInput.setRolloverIcon(new ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setRolloverSelectedIcon(new ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setSelectedIcon(new ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setSelected(true);
        ChkInput.addActionListener(evt -> isForm());

        ScrollInput = new ScrollPane(); ScrollInput.setViewportView(FormInput);
        ScrollInput.setPreferredSize(new Dimension(880, 580));
        PanelInput = new JPanel(new BorderLayout());
        PanelInput.setOpaque(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 600));
        PanelInput.add(ScrollInput, BorderLayout.CENTER); PanelInput.add(ChkInput, BorderLayout.PAGE_END);

        // PANEL BAWAH
        panelBawah = new panelisi(); panelBawah.setPreferredSize(new Dimension(44, 54));
        panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));
        BtnSimpan = new Button(); BtnSimpan.setIcon(new ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setMnemonic('S'); BtnSimpan.setText("Simpan"); BtnSimpan.setPreferredSize(new Dimension(100, 30));
        BtnBatal = new Button(); BtnBatal.setIcon(new ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBatal.setMnemonic('B'); BtnBatal.setText("Baru"); BtnBatal.setPreferredSize(new Dimension(100, 30));
        BtnHapus = new Button(); BtnHapus.setIcon(new ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setMnemonic('H'); BtnHapus.setText("Hapus"); BtnHapus.setPreferredSize(new Dimension(100, 30));
        BtnEdit = new Button(); BtnEdit.setIcon(new ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnEdit.setMnemonic('G'); BtnEdit.setText("Ganti"); BtnEdit.setPreferredSize(new Dimension(100, 30));
        BtnPrint = new Button(); BtnPrint.setIcon(new ImageIcon(getClass().getResource("/picture/b_print.png")));
        BtnPrint.setMnemonic('T'); BtnPrint.setText("Cetak"); BtnPrint.setPreferredSize(new Dimension(100, 30));
        BtnKeluar = new Button(); BtnKeluar.setIcon(new ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K'); BtnKeluar.setText("Keluar"); BtnKeluar.setPreferredSize(new Dimension(100, 30));
        Label lblTglC = new Label(); lblTglC.setText("Tanggal :"); lblTglC.setPreferredSize(new Dimension(55, 23));
        DTPCari1 = new widget.Tanggal(); DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setPreferredSize(new Dimension(90, 23));
        Label lblSd = new Label(); lblSd.setText("s.d."); lblSd.setPreferredSize(new Dimension(25, 23));
        DTPCari2 = new widget.Tanggal(); DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setPreferredSize(new Dimension(90, 23));
        Label lblKey = new Label(); lblKey.setText("Key Word :"); lblKey.setPreferredSize(new Dimension(70, 23));
        TCari.setPreferredSize(new Dimension(150, 23));
        BtnCari = new Button(); BtnCari.setIcon(new ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnAll = new Button(); BtnAll.setIcon(new ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
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
            if (TNoRw.getText().trim().isEmpty()) { Valid.textKosong(TNoRw, "Pasien");
            } else if (KdDokter.getText().trim().isEmpty()) { Valid.textKosong(KdDokter, "Dokter Anestesi");
            } else if (NIP.getText().trim().isEmpty()) { Valid.textKosong(NIP, "Petugas");
            } else {
                if (Sequel.menyimpantf("konversi_anestesi_rscb", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 17, new String[]{
                    TNoRw.getText(),
                    Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19),
                    TxtRuangan.getText(),
                    ChkPerubahanRencana.isSelected() ? "Ya" : "Tidak",
                    CmbAnestesiAwal.getSelectedItem().toString(),
                    TxtJamAwal.getText(),
                    CmbKonversiKe.getSelectedItem().toString(),
                    TxtJamKonversi.getText(),
                    TxtObatLokal.getText(), TxtObatRegional.getText(), TxtObatEpidural.getText(),
                    TxtObatTiva.getText(), TxtObatInhalasi.getText(), TxtObatAnalgetik.getText(),
                    CmbTehnikJalanNapas.getSelectedItem().toString(),
                    KdDokter.getText(), NIP.getText()
                })) { tampil(); emptTeks(); }
            }
        });

        BtnHapus.addActionListener(evt -> {
            if (tbObat.getSelectedRow() != -1) {
                if (Sequel.queryu2tf("delete from konversi_anestesi_rscb where no_rawat=? and tanggal=?", 2, new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
                })) { tampil(); emptTeks(); }
            } else JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dihapus...!!!!");
        });

        BtnEdit.addActionListener(evt -> {
            if (TNoRw.getText().trim().isEmpty() || tbObat.getSelectedRow()==-1) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            } else {
                if (Sequel.queryu2tf("delete from konversi_anestesi_rscb where no_rawat=? and tanggal=?", 2, new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
                })) { BtnSimpan.doClick(); }
            }
        });

        BtnPrint.addActionListener(evt -> cetakKonversi());

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

    private void isForm() {
        if (ChkInput.isSelected()) { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 600)); FormInput.setVisible(true); ChkInput.setVisible(true);
        } else { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 20)); FormInput.setVisible(false); ChkInput.setVisible(true); }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select ka.no_rawat, r.no_rkm_medis, p.nm_pasien, ka.tanggal, ka.ruangan, "+
                "ka.anestesi_awal, ka.konversi_ke, ka.kd_dokter, d.nm_dokter, ka.nip, pt.nama as nm_petugas "+
                "from konversi_anestesi_rscb ka "+
                "inner join reg_periksa r on ka.no_rawat=r.no_rawat "+
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "+
                "inner join dokter d on ka.kd_dokter=d.kd_dokter "+
                "inner join petugas pt on ka.nip=pt.nip "+
                "where ka.tanggal between ? and ? and (ka.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) "+
                "order by ka.tanggal desc");
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
            ps.setString(3, "%"+TCari.getText().trim()+"%");
            ps.setString(4, "%"+TCari.getText().trim()+"%");
            ps.setString(5, "%"+TCari.getText().trim()+"%");
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                    rs.getString("tanggal"), rs.getString("ruangan"),
                    rs.getString("anestesi_awal"), rs.getString("konversi_ke"),
                    rs.getString("kd_dokter"), rs.getString("nm_dokter"),
                    rs.getString("nip"), rs.getString("nm_petugas")
                });
            }
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif Konversi Anestesi : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            try { TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString())); } catch (Exception e) {}
            try {
                ps = koneksi.prepareStatement("select * from konversi_anestesi_rscb where no_rawat=? and tanggal=?");
                ps.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                ps.setString(2, tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TxtRuangan.setText(rs.getString("ruangan"));
                    ChkPerubahanRencana.setSelected(rs.getString("perubahan_rencana").equals("Ya"));
                    CmbAnestesiAwal.setSelectedItem(rs.getString("anestesi_awal"));
                    TxtJamAwal.setText(rs.getString("jam_awal"));
                    CmbKonversiKe.setSelectedItem(rs.getString("konversi_ke"));
                    TxtJamKonversi.setText(rs.getString("jam_konversi"));
                    TxtObatLokal.setText(rs.getString("obat_lokal"));
                    TxtObatRegional.setText(rs.getString("obat_regional"));
                    TxtObatEpidural.setText(rs.getString("obat_epidural"));
                    TxtObatTiva.setText(rs.getString("obat_tiva"));
                    TxtObatInhalasi.setText(rs.getString("obat_inhalasi"));
                    TxtObatAnalgetik.setText(rs.getString("obat_analgetik"));
                    CmbTehnikJalanNapas.setSelectedItem(rs.getString("tehnik_jalan_napas"));
                    KdDokter.setText(rs.getString("kd_dokter"));
                    NmDokter.setText(Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", rs.getString("kd_dokter")));
                    NIP.setText(rs.getString("nip"));
                    NamaPetugas.setText(Sequel.cariIsi("select nama from petugas where nip=?", rs.getString("nip")));
                }
            } catch (Exception e) { System.out.println("Notif getData Konversi Anestesi : " + e);
            } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
        }
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date());
        ChkPerubahanRencana.setSelected(false);
        CmbAnestesiAwal.setSelectedIndex(0); TxtJamAwal.setText("");
        CmbKonversiKe.setSelectedIndex(0); TxtJamKonversi.setText("");
        TxtObatLokal.setText(""); TxtObatRegional.setText(""); TxtObatEpidural.setText("");
        TxtObatTiva.setText(""); TxtObatInhalasi.setText(""); TxtObatAnalgetik.setText("");
        CmbTehnikJalanNapas.setSelectedIndex(0);
        KdDokter.setText(""); NmDokter.setText("");
        ChkPerubahanRencana.requestFocus();
    }

    private void isiRuangan(String norwt) {
        String ruangan = Sequel.cariIsi("select b.nm_bangsal from kamar_inap ki inner join kamar k on ki.kd_kamar=k.kd_kamar inner join bangsal b on k.kd_bangsal=b.kd_bangsal where ki.no_rawat=? order by ki.tgl_masuk desc limit 1", norwt);
        if (ruangan.isEmpty()) {
            ruangan = Sequel.cariIsi("select pl.nm_poli from reg_periksa rp inner join poliklinik pl on rp.kd_poli=pl.kd_poli where rp.no_rawat=?", norwt);
        }
        TxtRuangan.setText(ruangan);
    }

    public void setNoRm(String norwt, Date tgl) {
        TNoRw.setText(norwt); TglAsuhan.setDate(tgl);
        TCari.setText(norwt); DTPCari2.setDate(tgl);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", norwt));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
        isiRuangan(norwt);
    }

    public void setNoRm(String norwt, String norm, String namapasien) {
        TNoRw.setText(norwt);
        TNoRM.setText(norm);
        TPasien.setText(namapasien);
        TCari.setText(norwt);
        isiRuangan(norwt);
    }

    private void cetakKonversi() {
        if (tbObat.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu..!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        String noRawat = tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString();
        String tanggalVal = tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString();

        String noRM = "", nmPasien = "", ruanganVal = "", tglLahirPasien = "", jkPasien = "";
        String perubahanRencana = "", anestesiAwal = "", jamAwal = "", konversiKe = "", jamKonversi = "";
        String obatLokal = "", obatRegional = "", obatEpidural = "", obatTiva = "", obatInhalasi = "", obatAnalgetik = "";
        String tehnikJalanNapas = "", kdDokterVal = "", nmDokterVal = "", nmPetugasVal = "";

        try {
            ps = koneksi.prepareStatement(
                "select ka.*, r.no_rkm_medis, p.nm_pasien, DATE_FORMAT(p.tgl_lahir,'%d-%m-%Y') as tgl_lahir_pasien, " +
                "if(p.jk='L','Laki-laki','Perempuan') as jk_pasien, d.nm_dokter, pt.nama as nm_petugas " +
                "from konversi_anestesi_rscb ka " +
                "inner join reg_periksa r on ka.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                "inner join dokter d on ka.kd_dokter=d.kd_dokter " +
                "inner join petugas pt on ka.nip=pt.nip " +
                "where ka.no_rawat=? and ka.tanggal=?");
            ps.setString(1, noRawat);
            ps.setString(2, tanggalVal);
            rs = ps.executeQuery();
            if (rs.next()) {
                noRM = rs.getString("no_rkm_medis");
                nmPasien = rs.getString("nm_pasien");
                tglLahirPasien = rs.getString("tgl_lahir_pasien") != null ? rs.getString("tgl_lahir_pasien") : "";
                jkPasien = rs.getString("jk_pasien");
                ruanganVal = rs.getString("ruangan") != null ? rs.getString("ruangan") : "";
                perubahanRencana = rs.getString("perubahan_rencana") != null ? rs.getString("perubahan_rencana") : "Tidak";
                anestesiAwal = rs.getString("anestesi_awal") != null ? rs.getString("anestesi_awal") : "";
                jamAwal = rs.getString("jam_awal") != null ? rs.getString("jam_awal") : "";
                konversiKe = rs.getString("konversi_ke") != null ? rs.getString("konversi_ke") : "";
                jamKonversi = rs.getString("jam_konversi") != null ? rs.getString("jam_konversi") : "";
                obatLokal = rs.getString("obat_lokal") != null ? rs.getString("obat_lokal") : "";
                obatRegional = rs.getString("obat_regional") != null ? rs.getString("obat_regional") : "";
                obatEpidural = rs.getString("obat_epidural") != null ? rs.getString("obat_epidural") : "";
                obatTiva = rs.getString("obat_tiva") != null ? rs.getString("obat_tiva") : "";
                obatInhalasi = rs.getString("obat_inhalasi") != null ? rs.getString("obat_inhalasi") : "";
                obatAnalgetik = rs.getString("obat_analgetik") != null ? rs.getString("obat_analgetik") : "";
                tehnikJalanNapas = rs.getString("tehnik_jalan_napas") != null ? rs.getString("tehnik_jalan_napas") : "";
                kdDokterVal = rs.getString("kd_dokter");
                nmDokterVal = rs.getString("nm_dokter");
                nmPetugasVal = rs.getString("nm_petugas");
            }
        } catch (Exception e) {
            System.out.println("Notif cetak konversi : " + e);
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {}
        }

        // QR Code dokter
        String qrUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB()
            + "/penggajian/generateqrcode.php?kodedokter=" + kdDokterVal.replace(" ", "_");
        String qrImgUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB()
            + "/penggajian/temp/" + kdDokterVal + ".png";
        try { new java.net.URL(qrUrl).openStream().close(); } catch (Exception e) {}

        // Helper checkbox
        String CK = "&#9745;"; // checked
        String UC = "&#9744;"; // unchecked

        // Build HTML sesuai PDF RM.IBS.11.006.2019
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
            .append("body{font-family:Tahoma;font-size:9px;margin:15px;color:#000;}")
            .append("table{border-collapse:collapse;}")
            .append("td,th{vertical-align:middle;}")
            .append(".hdr{width:100%;border-bottom:2px solid #000;padding-bottom:5px;margin-bottom:5px;}")
            .append(".rm-box{border:1px solid #000;padding:5px;text-align:center;font-weight:bold;font-size:9px;width:120px;}")
            .append(".title{text-align:center;font-weight:bold;font-size:12px;margin:5px 0;}")
            .append(".main{width:100%;border:1px solid #000;}")
            .append(".main td,.main th{border:1px solid #000;padding:4px 6px;font-size:8px;}")
            .append(".main th{background:#e8e8e8;font-weight:bold;text-align:center;}")
            .append(".cb{font-size:12px;}")
            .append(".ttd{width:100%;margin-top:30px;}")
            .append(".ttd td{text-align:center;vertical-align:top;padding:5px;font-size:9px;}")
            .append(".pi td{padding:1px 5px;font-size:8px;}")
            .append("@media print{body{margin:10px;}}")
            .append("</style></head><body>");

        // Header RS
        html.append("<table class='hdr'><tr>")
            .append("<td style='text-align:center;'>")
            .append("<div style='font-size:7px;'>DINAS KESEHATAN KABUPATEN BEKASI</div>")
            .append("<div style='font-size:10px;font-weight:bold;'>RUMAH SAKIT UMUM DAERAH (RSUD)</div>")
            .append("<div style='font-size:11px;font-weight:bold;letter-spacing:5px;'>").append(akses.getnamars().toUpperCase().replace("RSUD","").replace("RUMAH SAKIT UMUM DAERAH","").trim()).append("</div>")
            .append("<div style='font-size:7px;'>").append(akses.getalamatrs()).append("</div>")
            .append("<div style='font-size:7px;'>Email : ").append(akses.getemailrs()).append(" Telp : ").append(akses.getkontakrs()).append("</div>")
            .append("</td>")
            .append("<td class='rm-box'>RM.IBS.11.006.2019</td>")
            .append("</tr></table>");

        // Title + Patient Info
        html.append("<table style='width:100%;margin-bottom:5px;'><tr>")
            .append("<td style='font-size:12px;font-weight:bold;width:50%;'>KONVERSI ANESTESI</td>")
            .append("<td><table class='pi'>")
            .append("<tr><td><b>No.RM</b></td><td>: ").append(noRM).append("</td></tr>")
            .append("<tr><td><b>Nama</b></td><td>: ").append(nmPasien).append("</td></tr>")
            .append("<tr><td><b>Tanggal Lahir</b></td><td>: ").append(tglLahirPasien).append("</td></tr>")
            .append("<tr><td><b>Ruangan</b></td><td>: ").append(ruanganVal).append("</td></tr>")
            .append("<tr><td><b>Tanggal</b></td><td>: ").append(tanggalVal).append("</td></tr>")
            .append("</table></td></tr></table>");

        // === KONVERSI ANESTESI ===
        html.append("<table class='main'>");
        html.append("<tr><th colspan='6'>KONVERSI ANESTESI</th></tr>");

        // Perubahan Rencana
        html.append("<tr>")
            .append("<td colspan='2'><b>Perubahan Rencana Anestesi</b></td>")
            .append("<td colspan='2' class='cb'>").append(perubahanRencana.equalsIgnoreCase("Ya")?CK:UC).append(" Ya</td>")
            .append("<td colspan='2' class='cb'>").append(perubahanRencana.equalsIgnoreCase("Tidak")?CK:UC).append(" Tidak</td>")
            .append("</tr>");

        // Anestesi Awal
        String aa = anestesiAwal.toLowerCase();
        html.append("<tr>")
            .append("<td rowspan='3'><b>Anestesi Awal</b></td>")
            .append("<td class='cb'>").append(aa.contains("lokal")?CK:UC).append(" Lokal</td>")
            .append("<td class='cb'>").append(aa.contains("blok saraf")?CK:UC).append(" Blok Saraf Tepi</td>")
            .append("<td colspan='2' rowspan='3' style='text-align:center;'><b>WAKTU</b></td>")
            .append("<td rowspan='3'>Jam : ").append(jamAwal).append("</td>")
            .append("</tr>");
        html.append("<tr>")
            .append("<td class='cb'>").append(aa.contains("spinal")?CK:UC).append(" Spinal</td>")
            .append("<td class='cb'>").append(aa.contains("epidural")?CK:UC).append(" Epidural</td>")
            .append("</tr>");
        html.append("<tr>")
            .append("<td class='cb'>").append(aa.contains("general")?CK:UC).append(" General</td>")
            .append("<td class='cb'>").append(aa.contains("nla")?CK:UC).append(" NLA</td>")
            .append("</tr>");

        // Konversi Ke
        String kk = konversiKe.toLowerCase();
        html.append("<tr>")
            .append("<td rowspan='3'><b>Konversi Ke</b></td>")
            .append("<td class='cb'>").append(kk.contains("lokal")?CK:UC).append(" Lokal</td>")
            .append("<td class='cb'>").append(kk.contains("epidural")?CK:UC).append(" Epidural</td>")
            .append("<td colspan='2' rowspan='3'></td>")
            .append("<td rowspan='3'>Jam : ").append(jamKonversi).append("</td>")
            .append("</tr>");
        html.append("<tr>")
            .append("<td class='cb'>").append(kk.contains("nla")?CK:UC).append(" NLA</td>")
            .append("<td class='cb'>").append(kk.contains("general")?CK:UC).append(" General</td>")
            .append("</tr>");
        html.append("<tr>")
            .append("<td class='cb'>").append(kk.contains("spinal")?CK:UC).append(" Spinal</td>")
            .append("<td class='cb'>").append(kk.contains("blok saraf")?CK:UC).append(" Blok Saraf Tepi</td>")
            .append("</tr>");

        // === EVALUASI KONVERSI ANESTESI ===
        html.append("<tr><th colspan='6'>EVALUASI KONVERSI ANESTESI</th></tr>");

        String ol = obatLokal.toLowerCase();
        String or2 = obatRegional.toLowerCase();
        String oe = obatEpidural.toLowerCase();
        html.append("<tr>")
            .append("<td rowspan='3'><b>Obat</b></td>")
            .append("<td><b>Lokal :</b></td>")
            .append("<td colspan='2'><b>Regional :</b></td>")
            .append("<td colspan='2'><b>Epidural</b></td>")
            .append("</tr>");
        html.append("<tr>")
            .append("<td class='cb'>").append(ol.contains("lidocain")?CK:UC).append(" Lidocain</td>")
            .append("<td colspan='2' class='cb'>").append(or2.contains("bunascan")?CK:UC).append(" Bunascan</td>")
            .append("<td colspan='2' class='cb'>").append(oe.contains("vopicain")?CK:UC).append(" Vopicain</td>")
            .append("</tr>");
        html.append("<tr>")
            .append("<td class='cb'>").append(ol.contains("pehacain")?CK:UC).append(" Pehacain</td>")
            .append("<td colspan='2'>").append(obatRegional.replace("Bunascan","").replace(",","").trim()).append("</td>")
            .append("<td colspan='2'>").append(obatEpidural.replace("Vopicain","").replace(",","").trim()).append("</td>")
            .append("</tr>");

        // === OBAT GENERAL ===
        html.append("<tr><th colspan='6'>OBAT GENERAL</th></tr>");

        String ot = obatTiva.toLowerCase();
        String oi = obatInhalasi.toLowerCase();
        String oa = obatAnalgetik.toLowerCase();
        html.append("<tr>")
            .append("<td colspan='2'><b>TIVA</b></td>")
            .append("<td colspan='2'><b>Inhalasi / Balance</b></td>")
            .append("<td colspan='2'><b>Analgetik / Narkotik</b></td>")
            .append("</tr>");
        html.append("<tr>")
            .append("<td colspan='2' class='cb'>").append(ot.contains("midazolam")?CK:UC).append(" Midazolam</td>")
            .append("<td colspan='2' class='cb'>").append(oi.contains("halotan")?CK:UC).append(" Halotan</td>")
            .append("<td colspan='2' class='cb'>").append(oa.contains("fentanyl")?CK:UC).append(" Fentanyl</td>")
            .append("</tr>");
        html.append("<tr>")
            .append("<td colspan='2' class='cb'>").append(ot.contains("propofol")?CK:UC).append(" Propofol</td>")
            .append("<td colspan='2' class='cb'>").append(oi.contains("sevoflurane")?CK:UC).append(" Sevoflurane</td>")
            .append("<td colspan='2' class='cb'>").append(oa.contains("pethidine")?CK:UC).append(" Pethidine</td>")
            .append("</tr>");
        html.append("<tr>")
            .append("<td colspan='2' class='cb'>").append(ot.contains("ketamin")?CK:UC).append(" Ketamin</td>")
            .append("<td colspan='2' class='cb'>").append(oi.contains("isoflurane")?CK:UC).append(" Isoflurane</td>")
            .append("<td colspan='2' class='cb'>").append(oa.contains("ketorolac")?CK:UC).append(" Ketorolac</td>")
            .append("</tr>");
        html.append("<tr>")
            .append("<td colspan='2'>").append(obatTiva.replaceAll("(?i)(midazolam|propofol|ketamin)[,\\s]*","").trim()).append("</td>")
            .append("<td colspan='2'>").append(obatInhalasi.replaceAll("(?i)(halotan|sevoflurane|isoflurane)[,\\s]*","").trim()).append("</td>")
            .append("<td colspan='2'>").append(obatAnalgetik.replaceAll("(?i)(fentanyl|pethidine|ketorolac)[,\\s]*","").trim()).append("</td>")
            .append("</tr>");

        // Tehnik Jalan Napas
        String tj = tehnikJalanNapas.toLowerCase();
        html.append("<tr>")
            .append("<td colspan='2'><b>Tehnik Jalan Napas</b></td>")
            .append("<td class='cb'>").append(tj.contains("mask")?CK:UC).append(" MASK</td>")
            .append("<td class='cb'>").append(tj.contains("oral ett")?CK:UC).append(" Oral ETT</td>")
            .append("<td class='cb'>").append(tj.contains("lma")?CK:UC).append(" LMA</td>")
            .append("<td class='cb'>").append(tj.contains("nasal ett")?CK:UC).append(" Nasal ETT</td>")
            .append("</tr>");

        html.append("</table>");

        // Tanda tangan Dokter Anestesi (kanan)
        html.append("<table class='ttd'><tr>")
            .append("<td style='width:50%;'></td>")
            .append("<td style='width:50%;'>Dokter Anestesi<br><br>")
            .append("<img src='").append(qrImgUrl).append("' style='width:130px;height:130px;'/><br><br>")
            .append("( ").append(nmDokterVal).append(" )</td>")
            .append("</tr></table>");

        html.append("<script>window.onload=function(){window.print();}</script>")
            .append("</body></html>");

        // Simpan ke file HTML dan buka di browser
        try {
            java.io.File tmpFile = java.io.File.createTempFile("konversi_anestesi_", ".html");
            tmpFile.deleteOnExit();
            java.io.FileWriter fw = new java.io.FileWriter(tmpFile);
            fw.write(html.toString());
            fw.close();
            java.awt.Desktop.getDesktop().browse(tmpFile.toURI());
        } catch (Exception e) {
            System.out.println("Notif buka browser : " + e);
        }

        this.setCursor(Cursor.getDefaultCursor());
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getrujukan_keluar()); BtnHapus.setEnabled(akses.getrujukan_keluar());
        BtnEdit.setEnabled(akses.getrujukan_keluar()); BtnPrint.setEnabled(akses.getrujukan_keluar());
        if (akses.getjml2() >= 1) {
            NIP.setEditable(false);
            BtnPetugas.setEnabled(false);
            NIP.setText(akses.getkode());
            Sequel.cariIsi("select petugas.nama from petugas where petugas.nip=?", NamaPetugas, NIP.getText());
        }
    }
}
