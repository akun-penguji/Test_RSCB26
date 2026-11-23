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

public class RMEdukasiAnestesiRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private Label LCount;

    private TextBox TxtRuangan, TxtNamaPenandatangan, TxtAlamat, TxtNoTelp;
    private widget.Tanggal DTPTglLahir;
    private ComboBox CmbJK;
    private TextBox TxtDiagnosa, TxtRencanaTindakan;
    private ComboBox CmbHubungan, CmbJenisAnestesia;
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
    private CekBox ChkInput, ChkPhoto;
    private PanelBiasa FormInput, FormPhoto;
    private ScrollPane ScrollInput;
    private JPanel PanelInput, PanelPhoto;
    public widget.editorpane LoadHTML, LoadHTML2;
    private Button BtnAmbil, BtnRefreshPhoto;
    private String lokasifile = "", signaturefile = "";

    public RMEdukasiAnestesiRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(900, 650);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        LoadHTML2 = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Edukasi Anestesi ]::",
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
            new String[]{"No.Rawat", "No.R.M.", "Nama", "Tanggal", "Ruangan", "Hubungan", "Diagnosa",
                          "Jenis Anestesia", "Kd.Dokter", "Dokter Anestesi", "NIP", "Petugas"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 12; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) column.setPreferredWidth(105);
            else if (i == 1) column.setPreferredWidth(65);
            else if (i == 2) column.setPreferredWidth(160);
            else if (i == 3) column.setPreferredWidth(122);
            else if (i == 4) column.setPreferredWidth(100);
            else if (i == 5) column.setPreferredWidth(100);
            else if (i == 6) column.setPreferredWidth(150);
            else if (i == 7) column.setPreferredWidth(120);
            else if (i == 8) column.setPreferredWidth(80);
            else if (i == 9) column.setPreferredWidth(160);
            else if (i == 10) column.setPreferredWidth(80);
            else if (i == 11) column.setPreferredWidth(160);
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (tabMode.getRowCount() != 0) {
                    getData();
                    if (FormPhoto.isVisible()) panggilPhoto();
                }
            }
        });
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem mnCetakBukti = new JMenuItem("Cetak Edukasi Anestesi");
        mnCetakBukti.setIcon(new ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetakBukti.addActionListener(evt -> cetakDenganBukti());
        popupMenu.add(mnCetakBukti);

        tbObat.setComponentPopupMenu(popupMenu);
        Scroll.setViewportView(tbObat);

        // FORM INPUT
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(880, 480));
        FormInput.setLayout(null);

        int y = 10;
        Label lblTgl = new Label(); lblTgl.setText("Tanggal :"); lblTgl.setBounds(0, y, 120, 23); FormInput.add(lblTgl);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setBounds(125, y, 130, 23); FormInput.add(TglAsuhan);

        y += 30;
        Label lblRuangan = new Label(); lblRuangan.setText("Ruangan :"); lblRuangan.setBounds(0, y, 120, 23); FormInput.add(lblRuangan);
        TxtRuangan = new TextBox(); TxtRuangan.setEditable(false); TxtRuangan.setBounds(125, y, 250, 23); FormInput.add(TxtRuangan);

        y += 30;
        Label lblNamaPenandatangan = new Label(); lblNamaPenandatangan.setText("Nama Penandatangan :"); lblNamaPenandatangan.setBounds(0, y, 120, 23); FormInput.add(lblNamaPenandatangan);
        TxtNamaPenandatangan = new TextBox(); TxtNamaPenandatangan.setBounds(125, y, 300, 23); FormInput.add(TxtNamaPenandatangan);

        y += 30;
        Label lblHubungan = new Label(); lblHubungan.setText("Hubungan :"); lblHubungan.setBounds(0, y, 120, 23); FormInput.add(lblHubungan);
        CmbHubungan = new ComboBox();
        CmbHubungan.setModel(new DefaultComboBoxModel(new String[]{"Saya Sendiri", "Istri", "Suami", "Anak", "Ayah", "Ibu", "Saudara", "Teman"}));
        CmbHubungan.setBounds(125, y, 200, 23); FormInput.add(CmbHubungan);

        y += 30;
        Label lblTglLahir = new Label(); lblTglLahir.setText("Tanggal Lahir :"); lblTglLahir.setBounds(0, y, 120, 23); FormInput.add(lblTglLahir);
        DTPTglLahir = new widget.Tanggal(); DTPTglLahir.setDisplayFormat("dd-MM-yyyy");
        DTPTglLahir.setBounds(125, y, 130, 23); FormInput.add(DTPTglLahir);

        y += 30;
        Label lblJK = new Label(); lblJK.setText("Jenis Kelamin :"); lblJK.setBounds(0, y, 120, 23); FormInput.add(lblJK);
        CmbJK = new ComboBox();
        CmbJK.setModel(new DefaultComboBoxModel(new String[]{"Laki-laki", "Perempuan"}));
        CmbJK.setBounds(125, y, 150, 23); FormInput.add(CmbJK);

        y += 30;
        Label lblAlamat = new Label(); lblAlamat.setText("Alamat :"); lblAlamat.setBounds(0, y, 120, 23); FormInput.add(lblAlamat);
        TxtAlamat = new TextBox(); TxtAlamat.setBounds(125, y, 400, 23); FormInput.add(TxtAlamat);

        y += 30;
        Label lblNoTelp = new Label(); lblNoTelp.setText("No. Telp :"); lblNoTelp.setBounds(0, y, 120, 23); FormInput.add(lblNoTelp);
        TxtNoTelp = new TextBox(); TxtNoTelp.setBounds(125, y, 150, 23); FormInput.add(TxtNoTelp);

        y += 30;
        Label lblDiagnosa = new Label(); lblDiagnosa.setText("Diagnosa :"); lblDiagnosa.setBounds(0, y, 120, 23); FormInput.add(lblDiagnosa);
        TxtDiagnosa = new TextBox(); TxtDiagnosa.setBounds(125, y, 400, 23); FormInput.add(TxtDiagnosa);

        y += 30;
        Label lblRencanaTindakan = new Label(); lblRencanaTindakan.setText("Rencana Tindakan :"); lblRencanaTindakan.setBounds(0, y, 120, 23); FormInput.add(lblRencanaTindakan);
        TxtRencanaTindakan = new TextBox(); TxtRencanaTindakan.setBounds(125, y, 400, 23); FormInput.add(TxtRencanaTindakan);

        y += 30;
        Label lblJenisAnestesia = new Label(); lblJenisAnestesia.setText("Jenis Anestesia :"); lblJenisAnestesia.setBounds(0, y, 120, 23); FormInput.add(lblJenisAnestesia);
        CmbJenisAnestesia = new ComboBox();
        CmbJenisAnestesia.setModel(new DefaultComboBoxModel(new String[]{"General Anestesi", "Regional Anestesi", "Sedasi", "Lainnya"}));
        CmbJenisAnestesia.setBounds(125, y, 200, 23); FormInput.add(CmbJenisAnestesia);

        y += 30;
        Label lblDokter = new Label(); lblDokter.setText("Dokter Anestesi :"); lblDokter.setBounds(0, y, 120, 23); FormInput.add(lblDokter);
        KdDokter = new TextBox(); KdDokter.setBounds(125, y, 80, 23); FormInput.add(KdDokter);
        KdDokter.setDocument(new batasInput((byte) 20).getKata(KdDokter));
        NmDokter = new TextBox(); NmDokter.setEditable(false); NmDokter.setBounds(207, y, 200, 23); FormInput.add(NmDokter);
        BtnDokter = new Button(); BtnDokter.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokter.setBounds(409, y, 28, 23); FormInput.add(BtnDokter);
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
        Label lblPetugas = new Label(); lblPetugas.setText("Petugas :"); lblPetugas.setBounds(0, y, 120, 23); FormInput.add(lblPetugas);
        NIP = new TextBox(); NIP.setEditable(false); NIP.setBounds(125, y, 80, 23); FormInput.add(NIP);
        NIP.setDocument(new batasInput((byte) 20).getKata(NIP));
        NamaPetugas = new TextBox(); NamaPetugas.setEditable(false); NamaPetugas.setBounds(207, y, 200, 23); FormInput.add(NamaPetugas);
        BtnPetugas = new Button(); BtnPetugas.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPetugas.setBounds(409, y, 28, 23); FormInput.add(BtnPetugas);
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
        ScrollInput.setPreferredSize(new Dimension(880, 430));
        PanelInput = new JPanel(new BorderLayout());
        PanelInput.setOpaque(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 510));
        PanelInput.add(ScrollInput, BorderLayout.CENTER); PanelInput.add(ChkInput, BorderLayout.PAGE_END);

        // PANEL PHOTO (EAST)
        PanelPhoto = new JPanel(new BorderLayout());
        PanelPhoto.setOpaque(false);
        PanelPhoto.setPreferredSize(new Dimension(15, HEIGHT));

        ChkPhoto = new CekBox();
        ChkPhoto.setIcon(new ImageIcon(getClass().getResource("/picture/143.png")));
        ChkPhoto.setMnemonic('2'); ChkPhoto.setText(".: Bukti Edukasi"); ChkPhoto.setToolTipText("Alt+2");
        ChkPhoto.setBorderPainted(true); ChkPhoto.setBorderPaintedFlat(true); ChkPhoto.setFocusable(false);
        ChkPhoto.setHorizontalAlignment(SwingConstants.LEFT);
        ChkPhoto.setHorizontalTextPosition(SwingConstants.RIGHT);
        ChkPhoto.setPreferredSize(new Dimension(15, 20));
        ChkPhoto.setRolloverIcon(new ImageIcon(getClass().getResource("/picture/143.png")));
        ChkPhoto.setRolloverSelectedIcon(new ImageIcon(getClass().getResource("/picture/145.png")));
        ChkPhoto.setSelectedIcon(new ImageIcon(getClass().getResource("/picture/145.png")));
        ChkPhoto.addActionListener(evt -> {
            if (tbObat.getSelectedRow() != -1) {
                isPhoto(); panggilPhoto();
            } else {
                ChkPhoto.setSelected(false);
                JOptionPane.showMessageDialog(null, "Silahkan pilih data terlebih dahulu..!!!");
            }
        });

        FormPhoto = new PanelBiasa();
        FormPhoto.setBackground(new Color(255, 255, 255));
        FormPhoto.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1),
            " Bukti Edukasi Anestesi : ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        FormPhoto.setPreferredSize(new Dimension(115, 73));
        FormPhoto.setLayout(new BorderLayout());

        PanelBiasa panelBtnPhoto = new PanelBiasa();
        panelBtnPhoto.setBackground(new Color(255, 255, 255));
        panelBtnPhoto.setPreferredSize(new Dimension(115, 40));
        panelBtnPhoto.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 9));

        BtnAmbil = new Button();
        BtnAmbil.setIcon(new ImageIcon(getClass().getResource("/picture/item.png")));
        BtnAmbil.setText("Ambil");
        BtnAmbil.setPreferredSize(new Dimension(100, 25));
        BtnAmbil.addActionListener(evt -> {
            if (tbObat.getSelectedRow() != -1) {
                Sequel.queryu("delete from antriedukasianestesi");
                Sequel.queryu("insert into antriedukasianestesi values('" +
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "','" +
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString() + "')");
                JOptionPane.showMessageDialog(null, "Data sudah dikirim ke form web. Silakan buka halaman Edukasi Anestesi di browser.");
            } else {
                JOptionPane.showMessageDialog(null, "Silahkan pilih data terlebih dahulu..!!!");
            }
        });

        BtnRefreshPhoto = new Button();
        BtnRefreshPhoto.setIcon(new ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnRefreshPhoto.setText("Refresh");
        BtnRefreshPhoto.setPreferredSize(new Dimension(100, 25));
        BtnRefreshPhoto.addActionListener(evt -> {
            if (tbObat.getSelectedRow() != -1) {
                panggilPhoto();
            } else {
                JOptionPane.showMessageDialog(null, "Silahkan pilih data terlebih dahulu..!!!");
            }
        });

        panelBtnPhoto.add(BtnAmbil);
        panelBtnPhoto.add(BtnRefreshPhoto);
        FormPhoto.add(panelBtnPhoto, BorderLayout.PAGE_END);

        ScrollPane scrollPhoto = new ScrollPane();
        scrollPhoto.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));
        scrollPhoto.setPreferredSize(new Dimension(200, 200));
        LoadHTML2.setBorder(null);
        scrollPhoto.setViewportView(LoadHTML2);
        FormPhoto.add(scrollPhoto, BorderLayout.CENTER);
        FormPhoto.setVisible(false);

        PanelPhoto.add(ChkPhoto, BorderLayout.WEST);
        PanelPhoto.add(FormPhoto, BorderLayout.CENTER);

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
                if (Sequel.menyimpantf("edukasi_anestesi_rscb", "?,?,?,?,?,?,?,?,?,?,?,?", "Data", 12, new String[]{
                    TNoRw.getText(),
                    Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19),
                    TxtRuangan.getText(), TxtNamaPenandatangan.getText(),
                    Valid.SetTgl(DTPTglLahir.getSelectedItem()+"")+"/"+CmbJK.getSelectedItem().toString(),
                    TxtAlamat.getText(), TxtNoTelp.getText(),
                    TxtDiagnosa.getText(), TxtRencanaTindakan.getText(),
                    CmbJenisAnestesia.getSelectedItem().toString(),
                    KdDokter.getText(), NIP.getText()
                })) { tampil(); emptTeks(); }
            }
        });

        BtnHapus.addActionListener(evt -> {
            if (tbObat.getSelectedRow() != -1) {
                if (Sequel.queryu2tf("delete from edukasi_anestesi_rscb where no_rawat=? and tanggal=?", 2, new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
                })) { tampil(); emptTeks(); }
            } else JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dihapus...!!!!");
        });

        BtnEdit.addActionListener(evt -> {
            if (TNoRw.getText().trim().isEmpty() || tbObat.getSelectedRow()==-1) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            } else {
                if (Sequel.queryu2tf("delete from edukasi_anestesi_rscb where no_rawat=? and tanggal=?", 2, new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
                })) { BtnSimpan.doClick(); }
            }
        });

        BtnPrint.addActionListener(evt -> cetakDenganBukti());

        // ASSEMBLY
        InternalFrame internalFrameData = new InternalFrame();
        internalFrameData.setLayout(new BorderLayout());
        internalFrameData.setBackground(new Color(255, 255, 255));
        internalFrameData.add(PanelInput, BorderLayout.NORTH);
        internalFrameData.add(Scroll, BorderLayout.CENTER);
        internalFrame1.add(panelAtas, BorderLayout.PAGE_START);
        internalFrame1.add(internalFrameData, BorderLayout.CENTER);
        internalFrame1.add(PanelPhoto, BorderLayout.EAST);
        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);
        setLayout(new BorderLayout());
        add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
    }

    private void isForm() {
        if (ChkInput.isSelected()) { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 510)); FormInput.setVisible(true); ChkInput.setVisible(true);
        } else { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 20)); FormInput.setVisible(false); ChkInput.setVisible(true); }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select ea.no_rawat, r.no_rkm_medis, p.nm_pasien, ea.tanggal, ea.ruangan, ea.tgl_lahir_jk, ea.diagnosa, "+
                "ea.jenis_anestesia, ea.kd_dokter, d.nm_dokter, ea.nip, pt.nama as nm_petugas "+
                "from edukasi_anestesi_rscb ea "+
                "inner join reg_periksa r on ea.no_rawat=r.no_rawat "+
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "+
                "inner join dokter d on ea.kd_dokter=d.kd_dokter "+
                "inner join petugas pt on ea.nip=pt.nip "+
                "where ea.tanggal between ? and ? and (ea.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) "+
                "order by ea.tanggal desc");
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
            ps.setString(3, "%"+TCari.getText().trim()+"%");
            ps.setString(4, "%"+TCari.getText().trim()+"%");
            ps.setString(5, "%"+TCari.getText().trim()+"%");
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                    rs.getString("tanggal"), rs.getString("ruangan"), rs.getString("tgl_lahir_jk"), rs.getString("diagnosa"),
                    rs.getString("jenis_anestesia"), rs.getString("kd_dokter"), rs.getString("nm_dokter"),
                    rs.getString("nip"), rs.getString("nm_petugas")
                });
            }
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif Edukasi Anestesi : " + e);
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
                ps = koneksi.prepareStatement("select * from edukasi_anestesi_rscb where no_rawat=? and tanggal=?");
                ps.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                ps.setString(2, tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TxtRuangan.setText(rs.getString("ruangan"));
                    TxtNamaPenandatangan.setText(rs.getString("nama_penandatangan"));
                    try {
                        String tlj = rs.getString("tgl_lahir_jk");
                        if (tlj != null && tlj.contains("/")) {
                            String[] pj = tlj.split("/", 2);
                            if (!pj[0].isEmpty()) DTPTglLahir.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(pj[0]));
                            if (pj.length > 1 && !pj[1].isEmpty()) CmbJK.setSelectedItem(pj[1]);
                        }
                    } catch (Exception ex) {}
                    TxtAlamat.setText(rs.getString("alamat"));
                    TxtNoTelp.setText(rs.getString("no_telp"));
                    TxtDiagnosa.setText(rs.getString("diagnosa"));
                    TxtRencanaTindakan.setText(rs.getString("rencana_tindakan"));
                    CmbJenisAnestesia.setSelectedItem(rs.getString("jenis_anestesia"));
                    KdDokter.setText(rs.getString("kd_dokter"));
                    NmDokter.setText(Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", rs.getString("kd_dokter")));
                    NIP.setText(rs.getString("nip"));
                    NamaPetugas.setText(Sequel.cariIsi("select nama from petugas where nip=?", rs.getString("nip")));
                }
            } catch (Exception e) { System.out.println("Notif getData Edukasi Anestesi : " + e);
            } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
        }
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date());
        TxtNamaPenandatangan.setText(""); CmbHubungan.setSelectedIndex(0);
        DTPTglLahir.setDate(new Date()); CmbJK.setSelectedIndex(0);
        TxtAlamat.setText(""); TxtNoTelp.setText("");
        TxtDiagnosa.setText(""); TxtRencanaTindakan.setText("");
        CmbJenisAnestesia.setSelectedIndex(0);
        KdDokter.setText(""); NmDokter.setText("");
        TxtNamaPenandatangan.requestFocus();
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

    private void isPhoto() {
        if (ChkPhoto.isSelected()) {
            ChkPhoto.setVisible(false);
            PanelPhoto.setPreferredSize(new Dimension(480, HEIGHT));
            FormPhoto.setVisible(true);
            ChkPhoto.setVisible(true);
        } else {
            ChkPhoto.setVisible(false);
            PanelPhoto.setPreferredSize(new Dimension(15, HEIGHT));
            FormPhoto.setVisible(false);
            ChkPhoto.setVisible(true);
        }
    }

    private void cetakDenganBukti() {
        if (tbObat.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu..!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        String noRawat = tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString();
        String tanggal = tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString();

        // Ambil data dari DB
        String noRM = "", nmPasien = "", ruangan = "", namaPenandatangan = "", hubungan = "";
        String tglLahirPj = "", jkPj = "", alamatPj = "", noTelpPj = "";
        String diagnosaVal = "", rencanaTindakanVal = "", jenisAnestesiaVal = "";
        String nmDokterVal = "", nmPetugasVal = "", tglLahirPasien = "", jkPasien = "";
        String kdDokterVal = "";
        try {
            ps = koneksi.prepareStatement(
                "select ea.*, r.no_rkm_medis, p.nm_pasien, DATE_FORMAT(p.tgl_lahir,'%d-%m-%Y') as tgl_lahir_pasien, " +
                "if(p.jk='L','Laki-laki','Perempuan') as jk_pasien, d.nm_dokter, pt.nama as nm_petugas " +
                "from edukasi_anestesi_rscb ea " +
                "inner join reg_periksa r on ea.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                "inner join dokter d on ea.kd_dokter=d.kd_dokter " +
                "inner join petugas pt on ea.nip=pt.nip " +
                "where ea.no_rawat=? and ea.tanggal=?");
            ps.setString(1, noRawat);
            ps.setString(2, tanggal);
            rs = ps.executeQuery();
            if (rs.next()) {
                noRM = rs.getString("no_rkm_medis");
                nmPasien = rs.getString("nm_pasien");
                tglLahirPasien = rs.getString("tgl_lahir_pasien") != null ? rs.getString("tgl_lahir_pasien") : "";
                jkPasien = rs.getString("jk_pasien");
                ruangan = rs.getString("ruangan") != null ? rs.getString("ruangan") : "";
                namaPenandatangan = rs.getString("nama_penandatangan") != null ? rs.getString("nama_penandatangan") : "";
                hubungan = "";
                String tljc = rs.getString("tgl_lahir_jk");
                if (tljc != null && tljc.contains("/")) { String[] pjc = tljc.split("/",2); tglLahirPj = pjc[0]; jkPj = pjc.length>1?pjc[1]:""; }
                else { tglLahirPj = tljc != null ? tljc : ""; jkPj = ""; }
                alamatPj = rs.getString("alamat") != null ? rs.getString("alamat") : "";
                noTelpPj = rs.getString("no_telp") != null ? rs.getString("no_telp") : "";
                diagnosaVal = rs.getString("diagnosa") != null ? rs.getString("diagnosa") : "";
                rencanaTindakanVal = rs.getString("rencana_tindakan") != null ? rs.getString("rencana_tindakan") : "";
                jenisAnestesiaVal = rs.getString("jenis_anestesia") != null ? rs.getString("jenis_anestesia") : "";
                kdDokterVal = rs.getString("kd_dokter");
                nmDokterVal = rs.getString("nm_dokter");
                nmPetugasVal = rs.getString("nm_petugas");
            }
        } catch (Exception e) {
            System.out.println("Notif cetak bukti : " + e);
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {}
        }

        // Ambil photo & signature URL
        String photoUrl = "";
        String signatureUrl = "";
        try {
            ps = koneksi.prepareStatement("select photo from edukasi_anestesi_rscb_photo where no_rawat=? and tanggal=?");
            ps.setString(1, noRawat); ps.setString(2, tanggal);
            rs = ps.executeQuery();
            if (rs.next() && rs.getString("photo") != null) {
                photoUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/edukasianestesi/" + rs.getString("photo");
            }
            rs.close(); ps.close();

            ps = koneksi.prepareStatement("select signature_path from edukasi_anestesi_rscb_signature where no_rawat=? and tanggal=?");
            ps.setString(1, noRawat); ps.setString(2, tanggal);
            rs = ps.executeQuery();
            if (rs.next() && rs.getString("signature_path") != null) {
                signatureUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/edukasianestesi/" + rs.getString("signature_path");
            }
            rs.close(); ps.close();
        } catch (Exception e) {
            System.out.println("Notif cetak bukti photo : " + e);
        }

        // Build HTML
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
            .append("body{font-family:Tahoma;font-size:9px;margin:20px;}")
            .append("h3,h4{margin:2px 0;}")
            .append("table.header{width:100%;border-bottom:2px solid #000;padding-bottom:5px;margin-bottom:10px;}")
            .append("table.info{width:100%;margin-bottom:5px;}")
            .append("table.info td{padding:2px 5px;vertical-align:top;}")
            .append("table.info td.label{width:120px;font-weight:bold;}")
            .append(".title{text-align:center;font-weight:bold;font-size:11px;margin:10px 0;}")
            .append(".edukasi{text-align:justify;line-height:1.6;}")
            .append(".edukasi b{font-size:9px;}")
            .append(".ttd-table{width:100%;margin-top:15px;}")
            .append(".ttd-table td{width:50%;text-align:center;vertical-align:top;padding:5px;}")
            .append("img.photo{max-width:250px;max-height:200px;}")
            .append("img.sig{max-width:200px;max-height:80px;}")
            .append("</style></head><body>");

        // Header RS
        html.append("<table class='header'><tr>")
            .append("<td style='text-align:center;'>")
            .append("<div style='font-size:8px;'>DINAS KESEHATAN KABUPATEN BEKASI</div>")
            .append("<div style='font-size:11px;font-weight:bold;'>").append(akses.getnamars()).append("</div>")
            .append("<div style='font-size:7px;'>").append(akses.getalamatrs()).append("</div>")
            .append("<div style='font-size:7px;'>Email : ").append(akses.getemailrs()).append(" Telp : ").append(akses.getkontakrs()).append("</div>")
            .append("</td>")
            .append("<td style='width:100px;text-align:center;font-size:8px;font-weight:bold;border:1px solid #000;vertical-align:middle;'>RM.IBS.11.003</td>")
            .append("</tr></table>");

        // Title
        html.append("<div class='title'>PEMBERIAN INFORMASI ANESTESI / SEDASI</div>");

        // Info Pasien
        html.append("<table class='info'>")
            .append("<tr><td class='label'>No.RM</td><td>: ").append(noRM).append("</td></tr>")
            .append("<tr><td class='label'>Nama</td><td>: ").append(nmPasien).append("</td></tr>")
            .append("<tr><td class='label'>Tanggal Lahir</td><td>: ").append(tglLahirPasien).append(" (").append(jkPasien).append(")</td></tr>")
            .append("<tr><td class='label'>Ruangan</td><td>: ").append(ruangan).append("</td></tr>")
            .append("<tr><td class='label'>Tanggal</td><td>: ").append(tanggal).append("</td></tr>")
            .append("</table>");

        html.append("<hr>");
        html.append("<div class='title'>INFORMASI / EDUKASI</div>");

        // Isi edukasi lengkap sesuai PDF
        html.append("<div class='edukasi'>")
            .append("<b>A. Persiapan</b><br>")
            .append("Untuk tindakan operasi atau invasif atau tindakan medis lainnya, pasien harus dilakukan pembiusan. ")
            .append("Pembiusan bisa dilakukan dengan cara anestesi umum/total atau anestesi regional (spinal/epidural) atau sedasi.<br>")
            .append("Untuk semua tindakan anestesi / sedasi harus dilakukan persiapan sebagai berikut :<br>")
            .append("1. Untuk operasi terencana atau invasif atau tindakan medis lainnya pasien harus puasa, puasa ini sangat penting ditaati oleh pasien karena lambung pasien harus kosong untuk menghindari keluarnya isi lambung ke rongga mulut pada waktu dibius dan sisa lambung ini bisa masuk ke dalam jalan nafas dan menyebabkan sumbatan jalan nafas yang fatal.<br>")
            .append("<table border='1' cellpadding='3' cellspacing='0' style='margin:5px 20px;font-size:8px;'>")
            .append("<tr><th>Umur</th><th>Puasa makan/susu (Jam)</th></tr>")
            .append("<tr><td>&lt;6 bulan</td><td>4</td></tr>")
            .append("<tr><td>6-36 bulan</td><td>6</td></tr>")
            .append("<tr><td>&gt;36 bulan</td><td>8</td></tr>")
            .append("<tr><td>Dewasa</td><td>8</td></tr></table>")
            .append("2. Pemeriksaan laboratorium tertentu sesuai indikasi dan keperluan.<br>")
            .append("3. Pemeriksaan oleh dokter anestesi sebelum operasi.<br>")
            .append("4. Semua make-up (lipstick, pewarna kuku, dsb) harus dibersihkan agar bisa dilihat warna kulit untuk dimonitor selama pembiusan.<br>")
            .append("5. Perhiasan dan gigi palsu harus dilepas.<br>")
            .append("6. Pasien menandatangani lembaran persetujuan tindakan anestesi.<br><br>")

            .append("<b>B. Jenis-jenis Anestesi dan Sedasi</b><br><br>")

            .append("<b>1. ANESTESI UMUM (AU) / GENERAL ANESTHESIA</b><br>")
            .append("Anestesi Umum adalah teknik pembiusan dengan bius total dimana pasien tidak sadar, tidak dapat dirangsang dan tidak merasakan sakit. Obat bius untuk AU berupa obat yang disuntikkan kedalam pembuluh darah atau zat anestesi yang dapat dihirup/dihisap, terutama pada bayi/anak. Lama kerja obat disesuaikan dengan lama operasi. Sesuai dengan kebutuhan operasi dan kondisi pasien, teknik ini akan mempengaruhi kemampuan untuk mempertahankan patensi jalan nafas, terjadi depresi fungsi pernafasan spontan atau depresi fungsi otot. Sehingga pasien sering memerlukan pemasangan alat pernafasan untuk mempertahankan patensi jalan napas dan pemberian nafas bantu.<br>")
            .append("<b>KELEBIHAN TEKNIK AU :</b> Dari awal pembiusan pasien sudah tidak sadar, tidak merasakan nyeri, teknik dan lama pembiusan bisa disesuaikan dengan lama operasi.<br>")
            .append("<b>KEKURANGAN TEKNIK AU :</b> Pasca bedah pasien harus sadar penuh sebelum bisa diberi minum. Obat bius yang diberikan dapat memiliki efek keseluruh tubuh termasuk ke aliran pembuluh janin dalam kandungan.<br>")
            .append("<b>KOMPLIKASI/EFEK SAMPING :</b> Efek samping pasca bedah berupa mual/muntah, menggigil, pusing, mengantuk, sakit tenggorokan yang bisa diatasi dengan obat-obatan. Beresiko pada pasien yang tidak puasa, bisa terjadi aspirasi yaitu masuknya isi lambung ke jalan nafas/paru. Kesulitan pemasangan alat/pipa pernafasan yang tidak terduga sebelumnya. Alergi/hipersensitif terhadap obat (sangat jarang), mulai derajat ringan hingga berat/fatal.<br><br>")

            .append("<b>2. ANESTESI SPINAL / EPIDURAL</b><br>")
            .append("Anestesia spinal/epidural adalah pembiusan yang hanya meliputi daerah perut ke bawah (perut sampai ujung kaki) dengan pasien tetap sadar tanpa merasakan nyeri. Bila pasien menginginkan untuk tidur maka dokter dapat memberi obat tidur/penenang melalui suntikan. Obat bius yang dipakai adalah obat bius lokal (Anestesi Lokal) dan bisa ditambah dengan obat lain yang bisa menambah kekuatan obat maupun menambah lama kerja obat bius lokal.<br>")
            .append("<b>KELEBIHAN :</b> Jumlah obat yang diberikan sedikit sekali. Obat bius tidak masuk ke dalam sirkulasi ari-ari/rahim sehingga baik untuk operasi besar. Obat bius tidak mempengaruhi organ lain dalam tubuh. Bisa ditambahkan obat penghilang rasa sakit yang bisa bertahan hingga 24 jam pasca bedah. Bila tidak mual/muntah pasca bedah bisa langsung minum. Lebih aman untuk pasien yang tidak puasa/operasi darurat.<br>")
            .append("<b>KELEMAHAN :</b> Pasca bedah harus berbaring, tidak boleh duduk/bangun selama 6 jam.<br>")
            .append("<b>KOMPLIKASI/EFEK SAMPING :</b> Mual/muntah, gatal-gatal terutama di daerah wajah. Sakit kepala dibagian depan atau belakang kepala pada hari ke-2/ke-3. Kesulitan buang air kecil. Alergi/hipersensitif terhadap obat. Gangguan pernafasan ringan hingga berat. Kelumpuhan atau kesemutan/rasa baal ditungkai yang bersifat sementara. Untuk epidural bisa terjadi kejang bila obat masuk kedalam pembuluh darah.<br><br>")

            .append("<b>3. BLOK PERIFER</b><br>")
            .append("Blok Perifer adalah teknik pembiusan yang hanya melibatkan sebagian tubuh saja (misalnya lengan atas atau bawah, tangan, tungkai, kaki dan sebagainya). Teknik ini dilakukan dengan menyuntikkan obat bius lokal didaerah sekitar saraf yang mensyarafi bagian tubuh yang akan dioperasi. Efek bius berlangsung antara 2-4 jam tergantung jenis obat yang dipakai.<br>")
            .append("<b>KOMPLIKASI/EFEK SAMPING :</b> Rasa kesemutan dan atau gangguan bergerak (motorik) yang berkepanjangan tetapi bersifat sementara. Pendarahan dibawah kulit (hematom). Tertusuknya lapisan paru. Pembiusan yang tidak komplit. Reaksi alergi atau hipersensitif yang ringan hingga berat (fatal). Kejang bila obat masuk ke dalam pembuluh darah.<br><br>")

            .append("<b>4. SEDASI</b><br>")
            .append("<b>a. Sedasi Ringan</b> - Teknik pembiusan dengan penyuntikkan obat yang dapat menyebabkan pasien mengantuk, tetapi masih memiliki respon normal terhadap rangsangan verbal dan tetap dapat mempertahankan patensi dari jalan nafasnya.<br>")
            .append("<b>b. Sedasi Sedang</b> - Teknik pembiusan dengan penyuntikkan obat yang dapat menyebabkan pasien mengantuk, tetapi masih memiliki respon terhadap rangsangan verbal. Pada sedasi moderat terjadi perubahan ringan dari respon pernafasan.<br>")
            .append("<b>c. Sedasi Dalam</b> - Teknik pembiusan dengan penyuntikkan obat yang dapat menyebabkan pasien mengantuk, tidur, serta tidak mudah dibangunkan. Respon pernafasan sudah mulai terganggu.<br>")
            .append("<b>KELEBIHAN :</b> Obat diberikan secara bertahap. Selama tindakan pasien dalam keadaan mengantuk dan tidur. Obat yang diberikan dapat memiliki efek amnesia.<br>")
            .append("<b>KELEMAHAN :</b> Pasca sedasi pasien harus sadar penuh sebelum bisa diberi minum. Sampai 24 jam pasca sedasi pasien tidak diperbolehkan mengendarai mobil, mengoperasikan mesin dan menandatangani dokumen penting yang bersifat legal.<br>")
            .append("<b>KOMPLIKASI :</b> Sedasi ringan ataupun moderat bisa bergeser menjadi sedasi dalam. Efek samping pasca sedasi berupa mual/muntah, menggigil, pusing, mengantuk. Alergi/hipersensitif terhadap obat. Beresiko pada pasien yang tidak puasa (aspirasi). Pada sedasi dalam terdapat kemungkinan pemasangan alat atau pipa pernafasan.<br>")
            .append("</div>");

        html.append("<hr>");

        // Pernyataan + data penandatangan
        html.append("<div class='edukasi'>")
            .append("<b>Saya yang bertanda tangan di bawah ini telah membaca atau dibacakan keterangan diatas dan telah dijelaskan terkait dengan prosedur anestesia dan sedasi yang akan dilakukan terhadap diri saya sendiri/istri/suami/anak/ayah/ibu *)</b>")
            .append("</div><br>");

        html.append("<table class='info'>")
            .append("<tr><td class='label'>Nama</td><td>: ").append(namaPenandatangan).append("</td></tr>")
            .append("<tr><td class='label'>Hubungan</td><td>: ").append(hubungan).append("</td></tr>")
            .append("<tr><td class='label'>Tanggal Lahir</td><td>: ").append(tglLahirPj).append(" ").append(jkPj).append("</td></tr>")
            .append("<tr><td class='label'>Alamat</td><td>: ").append(alamatPj).append("</td></tr>")
            .append("<tr><td class='label'>No. Telp</td><td>: ").append(noTelpPj).append("</td></tr>")
            .append("<tr><td class='label'>No. Rekam Medik</td><td>: ").append(noRM).append("</td></tr>")
            .append("<tr><td class='label'>Diagnosa</td><td>: ").append(diagnosaVal).append("</td></tr>")
            .append("<tr><td class='label'>Rencana Tindakan</td><td>: ").append(rencanaTindakanVal).append("</td></tr>")
            .append("<tr><td class='label'>Jenis Anestesia</td><td>: ").append(jenisAnestesiaVal).append("</td></tr>")
            .append("</table>");

        // Tanda tangan
        String qrUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB()
            + "/penggajian/generateqrcode.php?kodedokter=" + kdDokterVal.replace(" ", "_");
        String qrImgUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB()
            + "/penggajian/temp/" + kdDokterVal + ".png";

        // Generate QR dulu via HTTP call
        try {
            new java.net.URL(qrUrl).openStream().close();
        } catch (Exception e) {}

        html.append("<table class='ttd-table'><tr>")
            .append("<td>Dokter yang menjelaskan<br>")
            .append("<img src='").append(qrImgUrl).append("' style='width:130px;height:130px;'/><br>")
            .append("( ").append(nmDokterVal).append(" )</td>")
            .append("<td>Pihak yang dijelaskan<br>");
        if (!photoUrl.isEmpty()) {
            html.append("<img class='photo' src='").append(photoUrl).append("'/><br>");
        }
        if (!signatureUrl.isEmpty()) {
            html.append("<img class='sig' src='").append(signatureUrl).append("'/><br>");
        }
        html.append("( ").append(namaPenandatangan).append(" )</td>")
            .append("</tr></table>");

        html.append("<script>window.onload=function(){window.print();}</script>")
            .append("</body></html>");

        // Simpan ke file HTML temp lalu buka di browser
        try {
            java.io.File tmpFile = java.io.File.createTempFile("edukasi_anestesi_", ".html");
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

    private void panggilPhoto() {
        if (FormPhoto.isVisible()) {
            lokasifile = "";
            signaturefile = "";
            try {
                ps = koneksi.prepareStatement("select photo from edukasi_anestesi_rscb_photo where no_rawat=? and tanggal=?");
                try {
                    ps.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                    ps.setString(2, tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        lokasifile = rs.getString("photo");
                    }
                } finally {
                    if (rs != null) rs.close();
                    if (ps != null) ps.close();
                }

                ps = koneksi.prepareStatement("select signature_path from edukasi_anestesi_rscb_signature where no_rawat=? and tanggal=?");
                try {
                    ps.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                    ps.setString(2, tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        signaturefile = rs.getString("signature_path");
                    }
                } finally {
                    if (rs != null) rs.close();
                    if (ps != null) ps.close();
                }

                if (lokasifile.equals("") && signaturefile.equals("")) {
                    LoadHTML2.setText("<html><body><center><br><br><font face='tahoma' size='2' color='#434343'>Kosong</font></center></body></html>");
                } else {
                    StringBuilder html = new StringBuilder();
                    html.append("<html><body><center>");
                    if (!lokasifile.equals("")) {
                        html.append("<br><font face='tahoma' size='2' color='#434343'>Foto:</font><br>");
                        html.append("<img src='http://").append(koneksiDB.HOSTHYBRIDWEB()).append(":").append(koneksiDB.PORTWEB())
                            .append("/").append(koneksiDB.HYBRIDWEB()).append("/edukasianestesi/").append(lokasifile)
                            .append("' alt='photo' width='450' height='337'/><br>");
                    }
                    if (!signaturefile.equals("")) {
                        html.append("<br><font face='tahoma' size='2' color='#434343'>Tanda Tangan:</font><br>");
                        html.append("<img src='http://").append(koneksiDB.HOSTHYBRIDWEB()).append(":").append(koneksiDB.PORTWEB())
                            .append("/").append(koneksiDB.HYBRIDWEB()).append("/edukasianestesi/").append(signaturefile)
                            .append("' alt='signature' width='450' height='225'/><br>");
                    }
                    html.append("</center></body></html>");
                    LoadHTML2.setText(html.toString());
                }
            } catch (Exception e) {
                System.out.println("Notif panggilPhoto Edukasi Anestesi : " + e);
            }
        }
    }
}
