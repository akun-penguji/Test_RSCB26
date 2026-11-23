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
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import widget.*;

/**
 * Sign Out (Sebelum Menutup Luka) — WHO Surgical Safety Checklist fase Sign Out.
 * Ditulis ulang agar SATU skema dengan form e-Nakes (tabel signout_sebelum_menutup_luka,
 * 14 kolom termasuk jumlah_kasa/jumlah_instrumen) sehingga data yang diinput di web tampil
 * di Khanza desktop & sebaliknya. INSERT positional mengikuti urutan fisik kolom.
 */
public class RMSignOutSebelumMenutupLuka extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private Label LCount;

    private TextBox TxtTindakan, TxtPerhatian, TxtJumlahKasa, TxtJumlahInstrumen, TxtJumlahJarum;
    private CekBox ChkVerbalTindakan, ChkVerbalKasa, ChkVerbalInstrumen, ChkVerbalAlatTajam, ChkSpesimen;
    private TextBox KdDokterBedah, NmDokterBedah, KdDokterAnestesi, NmDokterAnestesi, NIP, NamaPetugas;
    private Button BtnDokterBedah, BtnDokterAnestesi, BtnPetugas;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private int i = 0;
    private DlgCariDokter dokterBedahDlg = new DlgCariDokter(null, false);
    private DlgCariDokter dokterAnestesiDlg = new DlgCariDokter(null, false);
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);

    private widget.Table tbData;
    private DefaultTableModel tabMode;
    private ScrollPane Scroll;
    private CekBox ChkInput;
    private PanelBiasa FormInput;
    private ScrollPane ScrollInput;
    private JPanel PanelInput;
    public widget.editorpane LoadHTML;

    public RMSignOutSebelumMenutupLuka(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(920, 680);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Sign Out (Sebelum Menutup Luka) ]::",
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
        tbData = new widget.Table();
        tabMode = new DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat", "No.R.M.", "Nama", "Tanggal", "Tindakan",
                          "Kd.Dokter Bedah", "Dokter Bedah", "Kd.Dokter Anestesi", "Dokter Anestesi",
                          "NIP", "Petugas OK"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbData.setModel(tabMode);
        tbData.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] w = {105, 65, 150, 122, 150, 90, 150, 100, 150, 70, 150};
        for (i = 0; i < w.length; i++) tbData.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        tbData.setDefaultRenderer(Object.class, new WarnaTable());

        tbData.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { if (tabMode.getRowCount() != 0) getData(); }
        });
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem mnCetak = new JMenuItem("Cetak Sign Out");
        mnCetak.setIcon(new ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetak.addActionListener(evt -> cetakSignOut());
        popupMenu.add(mnCetak);
        tbData.setComponentPopupMenu(popupMenu);
        Scroll.setViewportView(tbData);

        // FORM INPUT
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(900, 460));
        FormInput.setLayout(null);

        int y = 10;
        Label lblTgl = new Label(); lblTgl.setText("Tanggal :"); lblTgl.setBounds(0, y, 200, 23); FormInput.add(lblTgl);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setBounds(205, y, 150, 23); FormInput.add(TglAsuhan);

        y += 30;
        Label lblTindakan = new Label(); lblTindakan.setText("Tindakan / Operasi :"); lblTindakan.setBounds(0, y, 200, 23); FormInput.add(lblTindakan);
        TxtTindakan = new TextBox(); TxtTindakan.setBounds(205, y, 400, 23); FormInput.add(TxtTindakan);
        TxtTindakan.setDocument(new batasInput((int) 100).getKata(TxtTindakan));

        y += 30;
        Label lblDokterBedah = new Label(); lblDokterBedah.setText("Dokter Bedah :"); lblDokterBedah.setBounds(0, y, 200, 23); FormInput.add(lblDokterBedah);
        KdDokterBedah = new TextBox(); KdDokterBedah.setBounds(205, y, 80, 23); FormInput.add(KdDokterBedah);
        KdDokterBedah.setDocument(new batasInput((byte) 20).getKata(KdDokterBedah));
        NmDokterBedah = new TextBox(); NmDokterBedah.setEditable(false); NmDokterBedah.setBounds(287, y, 250, 23); FormInput.add(NmDokterBedah);
        BtnDokterBedah = new Button(); BtnDokterBedah.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokterBedah.setBounds(539, y, 28, 23); FormInput.add(BtnDokterBedah);
        BtnDokterBedah.addActionListener(evt -> {
            dokterBedahDlg.emptTeks(); dokterBedahDlg.isCek();
            dokterBedahDlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            dokterBedahDlg.setLocationRelativeTo(internalFrame1); dokterBedahDlg.setVisible(true);
        });
        dokterBedahDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (dokterBedahDlg.getTable().getSelectedRow() != -1) {
                    KdDokterBedah.setText(dokterBedahDlg.getTable().getValueAt(dokterBedahDlg.getTable().getSelectedRow(), 0).toString());
                    NmDokterBedah.setText(dokterBedahDlg.getTable().getValueAt(dokterBedahDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

        y += 30;
        Label lblDokterAnestesi = new Label(); lblDokterAnestesi.setText("Dokter Anestesi :"); lblDokterAnestesi.setBounds(0, y, 200, 23); FormInput.add(lblDokterAnestesi);
        KdDokterAnestesi = new TextBox(); KdDokterAnestesi.setBounds(205, y, 80, 23); FormInput.add(KdDokterAnestesi);
        KdDokterAnestesi.setDocument(new batasInput((byte) 20).getKata(KdDokterAnestesi));
        NmDokterAnestesi = new TextBox(); NmDokterAnestesi.setEditable(false); NmDokterAnestesi.setBounds(287, y, 250, 23); FormInput.add(NmDokterAnestesi);
        BtnDokterAnestesi = new Button(); BtnDokterAnestesi.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokterAnestesi.setBounds(539, y, 28, 23); FormInput.add(BtnDokterAnestesi);
        BtnDokterAnestesi.addActionListener(evt -> {
            dokterAnestesiDlg.emptTeks(); dokterAnestesiDlg.isCek();
            dokterAnestesiDlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            dokterAnestesiDlg.setLocationRelativeTo(internalFrame1); dokterAnestesiDlg.setVisible(true);
        });
        dokterAnestesiDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (dokterAnestesiDlg.getTable().getSelectedRow() != -1) {
                    KdDokterAnestesi.setText(dokterAnestesiDlg.getTable().getValueAt(dokterAnestesiDlg.getTable().getSelectedRow(), 0).toString());
                    NmDokterAnestesi.setText(dokterAnestesiDlg.getTable().getValueAt(dokterAnestesiDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

        y += 32;
        JLabel lblHdr = new JLabel("Checklist Sign Out (centang = Ya) :");
        lblHdr.setFont(new java.awt.Font("Tahoma", 1, 11)); lblHdr.setBounds(0, y, 400, 20); FormInput.add(lblHdr);

        y += 26;
        ChkVerbalTindakan = addChk("Konfirmasi nama tindakan", y); y += 24;
        ChkVerbalKasa = addChk("Kelengkapan jumlah kasa", y);
        Label lblJKasa = new Label(); lblJKasa.setText("Jumlah kasa :"); lblJKasa.setBounds(430, y, 90, 22); FormInput.add(lblJKasa);
        TxtJumlahKasa = new TextBox(); TxtJumlahKasa.setBounds(520, y, 90, 22); FormInput.add(TxtJumlahKasa);
        TxtJumlahKasa.setDocument(new batasInput((int) 20).getKata(TxtJumlahKasa));
        y += 26;
        ChkVerbalInstrumen = addChk("Kelengkapan instrumen", y);
        Label lblJInstr = new Label(); lblJInstr.setText("Jumlah instrumen :"); lblJInstr.setBounds(430, y, 90, 22); FormInput.add(lblJInstr);
        TxtJumlahInstrumen = new TextBox(); TxtJumlahInstrumen.setBounds(520, y, 90, 22); FormInput.add(TxtJumlahInstrumen);
        TxtJumlahInstrumen.setDocument(new batasInput((int) 20).getKata(TxtJumlahInstrumen));
        y += 26;
        ChkVerbalAlatTajam = addChk("Kelengkapan alat tajam / jarum", y);
        Label lblJJarum = new Label(); lblJJarum.setText("Jumlah jarum :"); lblJJarum.setBounds(430, y, 90, 22); FormInput.add(lblJJarum);
        TxtJumlahJarum = new TextBox(); TxtJumlahJarum.setBounds(520, y, 90, 22); FormInput.add(TxtJumlahJarum);
        TxtJumlahJarum.setDocument(new batasInput((int) 20).getKata(TxtJumlahJarum));
        y += 26;
        ChkSpesimen = addChk("Pelabelan spesimen (nama pasien)", y); y += 30;

        Label lblPerhatian = new Label(); lblPerhatian.setText("Perhatian utama fase pemulihan :"); lblPerhatian.setBounds(0, y, 200, 23); FormInput.add(lblPerhatian);
        TxtPerhatian = new TextBox(); TxtPerhatian.setBounds(205, y, 400, 23); FormInput.add(TxtPerhatian);
        TxtPerhatian.setDocument(new batasInput((int) 200).getKata(TxtPerhatian));

        y += 32;
        Label lblPetugas = new Label(); lblPetugas.setText("Perawat OK (Petugas) :"); lblPetugas.setBounds(0, y, 200, 23); FormInput.add(lblPetugas);
        NIP = new TextBox(); NIP.setEditable(false); NIP.setBounds(205, y, 80, 23); FormInput.add(NIP);
        NIP.setDocument(new batasInput((byte) 20).getKata(NIP));
        NamaPetugas = new TextBox(); NamaPetugas.setEditable(false); NamaPetugas.setBounds(287, y, 250, 23); FormInput.add(NamaPetugas);
        BtnPetugas = new Button(); BtnPetugas.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPetugas.setBounds(539, y, 28, 23); FormInput.add(BtnPetugas);
        BtnPetugas.addActionListener(evt -> {
            petugasDlg.emptTeks(); petugasDlg.isCek();
            petugasDlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
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
        ScrollInput.setPreferredSize(new Dimension(900, 340));
        PanelInput = new JPanel(new BorderLayout());
        PanelInput.setOpaque(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 360));
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
            } else if (KdDokterBedah.getText().trim().isEmpty()) { Valid.textKosong(KdDokterBedah, "Dokter Bedah");
            } else if (KdDokterAnestesi.getText().trim().isEmpty()) { Valid.textKosong(KdDokterAnestesi, "Dokter Anestesi");
            } else if (NIP.getText().trim().isEmpty()) { Valid.textKosong(NIP, "Petugas OK");
            } else {
                if (Sequel.menyimpantf("signout_sebelum_menutup_luka", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 15, new String[]{
                    TNoRw.getText(),
                    Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19),
                    TxtTindakan.getText(),
                    KdDokterBedah.getText(), KdDokterAnestesi.getText(),
                    yn(ChkVerbalTindakan), yn(ChkVerbalKasa), TxtJumlahKasa.getText(),
                    yn(ChkVerbalInstrumen), TxtJumlahInstrumen.getText(), TxtJumlahJarum.getText(),
                    yn(ChkVerbalAlatTajam), yn(ChkSpesimen),
                    TxtPerhatian.getText(), NIP.getText()
                })) { tampil(); emptTeks(); }
            }
        });

        BtnHapus.addActionListener(evt -> {
            if (tbData.getSelectedRow() != -1) {
                if (Sequel.queryu2tf("delete from signout_sebelum_menutup_luka where no_rawat=? and tanggal=?", 2, new String[]{
                    tbData.getValueAt(tbData.getSelectedRow(), 0).toString(),
                    tbData.getValueAt(tbData.getSelectedRow(), 3).toString()
                })) { tampil(); emptTeks(); }
            } else JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dihapus...!!!!");
        });

        BtnEdit.addActionListener(evt -> {
            if (TNoRw.getText().trim().isEmpty() || tbData.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            } else {
                if (Sequel.queryu2tf("delete from signout_sebelum_menutup_luka where no_rawat=? and tanggal=?", 2, new String[]{
                    tbData.getValueAt(tbData.getSelectedRow(), 0).toString(),
                    tbData.getValueAt(tbData.getSelectedRow(), 3).toString()
                })) { BtnSimpan.doClick(); }
            }
        });

        BtnPrint.addActionListener(evt -> cetakSignOut());

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

    private CekBox addChk(String text, int y) {
        CekBox c = new CekBox(); c.setText(text); c.setSelected(false);
        c.setBounds(205, y, 220, 22); FormInput.add(c);
        return c;
    }

    private String yn(CekBox c) { return c.isSelected() ? "Ya" : "Tidak"; }
    private String s(String v) { return v == null ? "" : v; }

    private void isForm() {
        if (ChkInput.isSelected()) { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 360)); FormInput.setVisible(true); ChkInput.setVisible(true);
        } else { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 20)); FormInput.setVisible(false); ChkInput.setVisible(true); }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select t.no_rawat, r.no_rkm_medis, p.nm_pasien, t.tanggal, t.tindakan, "+
                "t.kd_dokter_bedah, ifnull(db.nm_dokter,'') as dokterbedah, "+
                "t.kd_dokter_anestesi, ifnull(da.nm_dokter,'') as dokteranestesi, "+
                "t.nip_perawat_ok, ifnull(pt.nama,'') as nm_petugas "+
                "from signout_sebelum_menutup_luka t "+
                "inner join reg_periksa r on t.no_rawat=r.no_rawat "+
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "+
                "left join dokter db on db.kd_dokter=t.kd_dokter_bedah "+
                "left join dokter da on da.kd_dokter=t.kd_dokter_anestesi "+
                "left join petugas pt on pt.nip=t.nip_perawat_ok "+
                "where t.tanggal between ? and ? and (t.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) "+
                "order by t.tanggal desc");
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
            ps.setString(3, "%"+TCari.getText().trim()+"%");
            ps.setString(4, "%"+TCari.getText().trim()+"%");
            ps.setString(5, "%"+TCari.getText().trim()+"%");
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                    rs.getString("tanggal"), rs.getString("tindakan"),
                    rs.getString("kd_dokter_bedah"), rs.getString("dokterbedah"),
                    rs.getString("kd_dokter_anestesi"), rs.getString("dokteranestesi"),
                    rs.getString("nip_perawat_ok"), rs.getString("nm_petugas")
                });
            }
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif Sign Out : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void getData() {
        if (tbData.getSelectedRow() != -1) {
            TNoRw.setText(tbData.getValueAt(tbData.getSelectedRow(), 0).toString());
            TNoRM.setText(tbData.getValueAt(tbData.getSelectedRow(), 1).toString());
            TPasien.setText(tbData.getValueAt(tbData.getSelectedRow(), 2).toString());
            try { TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                tbData.getValueAt(tbData.getSelectedRow(), 3).toString())); } catch (Exception e) {}
            try {
                ps = koneksi.prepareStatement("select * from signout_sebelum_menutup_luka where no_rawat=? and tanggal=?");
                ps.setString(1, tbData.getValueAt(tbData.getSelectedRow(), 0).toString());
                ps.setString(2, tbData.getValueAt(tbData.getSelectedRow(), 3).toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TxtTindakan.setText(s(rs.getString("tindakan")));
                    KdDokterBedah.setText(s(rs.getString("kd_dokter_bedah")));
                    NmDokterBedah.setText(Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", KdDokterBedah.getText()));
                    KdDokterAnestesi.setText(s(rs.getString("kd_dokter_anestesi")));
                    NmDokterAnestesi.setText(Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", KdDokterAnestesi.getText()));
                    ChkVerbalTindakan.setSelected("Ya".equalsIgnoreCase(rs.getString("verbal_tindakan")));
                    ChkVerbalKasa.setSelected("Ya".equalsIgnoreCase(rs.getString("verbal_kelengkapan_kasa")));
                    TxtJumlahKasa.setText(s(rs.getString("jumlah_kasa")));
                    ChkVerbalInstrumen.setSelected("Ya".equalsIgnoreCase(rs.getString("verbal_instrumen")));
                    TxtJumlahInstrumen.setText(s(rs.getString("jumlah_instrumen")));
                    TxtJumlahJarum.setText(s(rs.getString("jumlah_jarum")));
                    ChkVerbalAlatTajam.setSelected("Ya".equalsIgnoreCase(rs.getString("verbal_alat_tajam")));
                    ChkSpesimen.setSelected("Ya".equalsIgnoreCase(rs.getString("kelengkapan_specimen_label")));
                    TxtPerhatian.setText(s(rs.getString("perhatian_utama_fase_pemulihan")));
                    NIP.setText(s(rs.getString("nip_perawat_ok")));
                    NamaPetugas.setText(Sequel.cariIsi("select nama from petugas where nip=?", NIP.getText()));
                }
            } catch (Exception e) { System.out.println("Notif getData Sign Out : " + e);
            } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
        }
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date());
        TxtTindakan.setText("");
        KdDokterBedah.setText(""); NmDokterBedah.setText("");
        KdDokterAnestesi.setText(""); NmDokterAnestesi.setText("");
        ChkVerbalTindakan.setSelected(false); ChkVerbalKasa.setSelected(false);
        ChkVerbalInstrumen.setSelected(false); ChkVerbalAlatTajam.setSelected(false);
        ChkSpesimen.setSelected(false);
        TxtJumlahKasa.setText(""); TxtJumlahInstrumen.setText(""); TxtJumlahJarum.setText(""); TxtPerhatian.setText("");
        TxtTindakan.requestFocus();
    }

    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt); TglAsuhan.setDate(tgl2);
        TCari.setText(norwt); DTPCari2.setDate(tgl2);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", norwt));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
    }

    public void setNoRm(String norwt, Date tgl2, String KodeDokter, String NamaDokter, String Operasi) {
        setNoRm(norwt, tgl2);
        KdDokterBedah.setText(s(KodeDokter));
        NmDokterBedah.setText(s(NamaDokter));
        TxtTindakan.setText(s(Operasi));
    }

    private void cetakSignOut() {
        if (tbData.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu..!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String noRawat = tbData.getValueAt(tbData.getSelectedRow(), 0).toString();
        String tanggalVal = tbData.getValueAt(tbData.getSelectedRow(), 3).toString();

        String kdBedah = "", kdAnestesi = "", nipPerawat = "", nmBedah = "", nmAnestesi = "", nmPetugas = "";
        try {
            ps = koneksi.prepareStatement(
                "select t.kd_dokter_bedah, t.kd_dokter_anestesi, t.nip_perawat_ok, "
                + "ifnull(db.nm_dokter,'') db, ifnull(da.nm_dokter,'') da, ifnull(pt.nama,'') pt "
                + "from signout_sebelum_menutup_luka t "
                + "left join dokter db on db.kd_dokter=t.kd_dokter_bedah "
                + "left join dokter da on da.kd_dokter=t.kd_dokter_anestesi "
                + "left join petugas pt on pt.nip=t.nip_perawat_ok "
                + "where t.no_rawat=? and t.tanggal=?");
            ps.setString(1, noRawat); ps.setString(2, tanggalVal);
            rs = ps.executeQuery();
            if (rs.next()) {
                kdBedah = s(rs.getString(1)); kdAnestesi = s(rs.getString(2)); nipPerawat = s(rs.getString(3));
                nmBedah = s(rs.getString(4)); nmAnestesi = s(rs.getString(5)); nmPetugas = s(rs.getString(6));
            }
        } catch (Exception e) { System.out.println("Notif cetak sign out : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }

        String base = "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh ";
        java.util.Map<String, Object> param = new java.util.HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        param.put("logo2", Sequel.cariGambar("select setting.logo2 from setting"));
        param.put("finger",  nmBedah.isEmpty()    ? "-" : base + nmBedah    + "\nID " + kdBedah    + "\n" + tanggalVal);
        param.put("finger2", nmAnestesi.isEmpty() ? "-" : base + nmAnestesi + "\nID " + kdAnestesi + "\n" + tanggalVal);
        param.put("finger3", nmPetugas.isEmpty()  ? "-" : base + nmPetugas  + "\nID " + nipPerawat + "\n" + tanggalVal);

        Valid.MyReportqry("rptSignOutSebelumMenutupLuka.jasper", "report", "::[ Formulir Sign Out Sebelum Menutup Luka ]::",
            "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,signout_sebelum_menutup_luka.tanggal,signout_sebelum_menutup_luka.tindakan,"
            + "signout_sebelum_menutup_luka.kd_dokter_bedah,ifnull(db.nm_dokter,'') as dokterbedah,signout_sebelum_menutup_luka.kd_dokter_anestesi,ifnull(da.nm_dokter,'') as dokteranestesi,"
            + "signout_sebelum_menutup_luka.verbal_tindakan,signout_sebelum_menutup_luka.verbal_kelengkapan_kasa,signout_sebelum_menutup_luka.jumlah_kasa,signout_sebelum_menutup_luka.verbal_instrumen,"
            + "signout_sebelum_menutup_luka.jumlah_instrumen,signout_sebelum_menutup_luka.jumlah_jarum,signout_sebelum_menutup_luka.verbal_alat_tajam,signout_sebelum_menutup_luka.kelengkapan_specimen_label,"
            + "signout_sebelum_menutup_luka.perhatian_utama_fase_pemulihan,signout_sebelum_menutup_luka.nip_perawat_ok,ifnull(pt.nama,'') as nama "
            + "from signout_sebelum_menutup_luka "
            + "inner join reg_periksa on signout_sebelum_menutup_luka.no_rawat=reg_periksa.no_rawat "
            + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
            + "left join dokter db on db.kd_dokter=signout_sebelum_menutup_luka.kd_dokter_bedah "
            + "left join dokter da on da.kd_dokter=signout_sebelum_menutup_luka.kd_dokter_anestesi "
            + "left join petugas pt on pt.nip=signout_sebelum_menutup_luka.nip_perawat_ok "
            + "where signout_sebelum_menutup_luka.no_rawat='" + noRawat + "' and signout_sebelum_menutup_luka.tanggal='" + tanggalVal + "'", param);
        this.setCursor(Cursor.getDefaultCursor());
    }

    private String rowCk(String CK, String UC, String val, String label) {
        boolean ya = "Ya".equalsIgnoreCase(val);
        return "<tr><td style='width:30px;font-size:12px;text-align:center;'>" + (ya ? CK : UC) + "</td><td>" + label + "</td></tr>";
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
