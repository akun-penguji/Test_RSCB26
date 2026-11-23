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
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import widget.Button;
import widget.InternalFrame;
import widget.Label;
import widget.TextBox;
import widget.CekBox;
import widget.PanelBiasa;
import widget.ScrollPane;
import widget.panelisi;

public class RMAsesmenRisikoJatuhGetUpGo extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan;
    private TextBox TCari, TNoRw, TNoRM, TPasien, TRuangan, TNamaKeluarga;
    private TextBox KdPetugas, NmPetugas;
    private Button BtnPetugas;
    private Label LCount;

    private CekBox ChkBerjalanYa, ChkBerjalanTidak, ChkMenopangYa, ChkMenopangTidak;
    private JRadioButton RdTidakBerisiko, RdRisikoRendah, RdRisikoTinggi;
    private CekBox ChkTindakanYa, ChkTindakanTidak;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);

    private widget.Table tbObat;
    private javax.swing.table.DefaultTableModel tabMode;
    private ScrollPane Scroll;
    private CekBox ChkInput;
    private PanelBiasa FormInput;
    private ScrollPane ScrollInput;
    private JPanel PanelInput;
    public widget.editorpane LoadHTML;

    public RMAsesmenRisikoJatuhGetUpGo(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Asesmen Risiko Jatuh Get Up And Go ]::",
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
        tabMode = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat", "No.R.M.", "Nama Pasien", "Tanggal", "Ruangan", "Cara Berjalan", "Menopang", "Hasil", "Tindakan", "Keluarga", "Petugas"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbObat.setModel(tabMode);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { if (tabMode.getRowCount() != 0) getData(); }
        });
        javax.swing.JPopupMenu popupMenu = new javax.swing.JPopupMenu();
        javax.swing.JMenuItem mnCetak = new javax.swing.JMenuItem("Cetak Get Up And Go");
        mnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetak.addActionListener(evt -> BtnPrint.doClick());
        popupMenu.add(mnCetak);
        tbObat.setComponentPopupMenu(popupMenu);
        Scroll.setViewportView(tbObat);

        // FORM INPUT
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(880, 620));
        FormInput.setLayout(null);

        int y = 10;
        JLabel lblHeader = new JLabel("ASESMEN RISIKO JATUH GET UP AND GO");
        lblHeader.setFont(new java.awt.Font("Tahoma", 1, 12)); lblHeader.setBounds(10, y, 400, 20); FormInput.add(lblHeader);
        JLabel lblKode = new JLabel("RM.KEP.08.035.2019");
        lblKode.setFont(new java.awt.Font("Tahoma", 0, 8)); lblKode.setBounds(700, y, 150, 20); FormInput.add(lblKode);

        y += 32;
        Label lblTgl = new Label(); lblTgl.setText("Tanggal :"); lblTgl.setBounds(10, y, 80, 23); FormInput.add(lblTgl);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setBounds(95, y, 130, 23); FormInput.add(TglAsuhan);
        Label lblRuangan = new Label(); lblRuangan.setText("Ruangan :"); lblRuangan.setBounds(250, y, 65, 23); FormInput.add(lblRuangan);
        TRuangan = new TextBox(); TRuangan.setBounds(320, y, 200, 23); FormInput.add(TRuangan);

        // === SECTION 1: Penilaian/Pengkajian ===
        y += 40;
        JLabel lblSec1 = new JLabel("Penilaian / Pengkajian");
        lblSec1.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec1.setBounds(10, y, 300, 20); FormInput.add(lblSec1);

        y += 28;
        JLabel lbl1 = new JLabel("<html><b>1.</b> Cara berjalan pasien (salah satu atau lebih):<br>" +
            "&nbsp;&nbsp;&nbsp;a. Tidak seimbang / sempoyongan / limbung<br>" +
            "&nbsp;&nbsp;&nbsp;b. Jalan dengan menggunakan alat bantu (kruk, tripot, kursi roda, orang lain)</html>");
        lbl1.setBounds(10, y, 550, 55); FormInput.add(lbl1);
        JLabel lblYa1 = new JLabel("Ya"); lblYa1.setBounds(620, y, 30, 23); FormInput.add(lblYa1);
        JLabel lblTdk1 = new JLabel("Tidak"); lblTdk1.setBounds(680, y, 40, 23); FormInput.add(lblTdk1);
        ChkBerjalanYa = new CekBox(); ChkBerjalanYa.setBounds(625, y+20, 23, 23); FormInput.add(ChkBerjalanYa);
        ChkBerjalanTidak = new CekBox(); ChkBerjalanTidak.setBounds(690, y+20, 23, 23); FormInput.add(ChkBerjalanTidak);

        y += 65;
        JLabel lbl2 = new JLabel("<html><b>2.</b> Menopang saat akan duduk / tampak memegang pinggiran kursi atau<br>" +
            "&nbsp;&nbsp;&nbsp;meja / benda lain sebagai penopang saat akan duduk.</html>");
        lbl2.setBounds(10, y, 550, 38); FormInput.add(lbl2);
        ChkMenopangYa = new CekBox(); ChkMenopangYa.setBounds(625, y+8, 23, 23); FormInput.add(ChkMenopangYa);
        ChkMenopangTidak = new CekBox(); ChkMenopangTidak.setBounds(690, y+8, 23, 23); FormInput.add(ChkMenopangTidak);

        // === SECTION 2: Hasil Penilaian ===
        y += 55;
        JLabel lblSec2 = new JLabel("Hasil Penilaian / Pengkajian");
        lblSec2.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec2.setBounds(10, y, 300, 20); FormInput.add(lblSec2);

        y += 28;
        RdTidakBerisiko = new JRadioButton("1. Tidak berisiko - Tidak ditemukan 1 dan 2");
        RdTidakBerisiko.setBounds(10, y, 400, 23); RdTidakBerisiko.setOpaque(false); FormInput.add(RdTidakBerisiko);
        y += 28;
        RdRisikoRendah = new JRadioButton("2. Risiko rendah - Ditemukan salah satu dari 1 / 2");
        RdRisikoRendah.setBounds(10, y, 400, 23); RdRisikoRendah.setOpaque(false); FormInput.add(RdRisikoRendah);
        y += 28;
        RdRisikoTinggi = new JRadioButton("3. Risiko tinggi - Ditemukan 1 dan 2");
        RdRisikoTinggi.setBounds(10, y, 400, 23); RdRisikoTinggi.setOpaque(false); FormInput.add(RdRisikoTinggi);

        ButtonGroup bgHasil = new ButtonGroup();
        bgHasil.add(RdTidakBerisiko); bgHasil.add(RdRisikoRendah); bgHasil.add(RdRisikoTinggi);
        RdTidakBerisiko.setSelected(true);

        // Auto-select hasil based on penilaian
        java.awt.event.ActionListener autoHasil = evt -> {
            boolean b1 = ChkBerjalanYa.isSelected();
            boolean b2 = ChkMenopangYa.isSelected();
            if (b1 && b2) RdRisikoTinggi.setSelected(true);
            else if (b1 || b2) RdRisikoRendah.setSelected(true);
            else RdTidakBerisiko.setSelected(true);
        };
        ChkBerjalanYa.addActionListener(e -> { ChkBerjalanTidak.setSelected(!ChkBerjalanYa.isSelected()); autoHasil.actionPerformed(null); });
        ChkBerjalanTidak.addActionListener(e -> { ChkBerjalanYa.setSelected(!ChkBerjalanTidak.isSelected()); autoHasil.actionPerformed(null); });
        ChkMenopangYa.addActionListener(e -> { ChkMenopangTidak.setSelected(!ChkMenopangYa.isSelected()); autoHasil.actionPerformed(null); });
        ChkMenopangTidak.addActionListener(e -> { ChkMenopangYa.setSelected(!ChkMenopangTidak.isSelected()); autoHasil.actionPerformed(null); });

        // === SECTION 3: Tindakan ===
        y += 38;
        JLabel lblSec3 = new JLabel("Hasil Tindakan");
        lblSec3.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSec3.setBounds(10, y, 300, 20); FormInput.add(lblSec3);

        y += 25;
        JLabel lblTindakan = new JLabel("<html>Tindakan sesuai hasil penilaian dilaksanakan?</html>");
        lblTindakan.setBounds(10, y, 350, 23); FormInput.add(lblTindakan);
        ChkTindakanYa = new CekBox(); ChkTindakanYa.setText("Ya"); ChkTindakanYa.setBounds(370, y, 55, 23); FormInput.add(ChkTindakanYa);
        ChkTindakanTidak = new CekBox(); ChkTindakanTidak.setText("Tidak"); ChkTindakanTidak.setBounds(430, y, 70, 23); FormInput.add(ChkTindakanTidak);
        ChkTindakanYa.addActionListener(e -> ChkTindakanTidak.setSelected(!ChkTindakanYa.isSelected()));
        ChkTindakanTidak.addActionListener(e -> ChkTindakanYa.setSelected(!ChkTindakanTidak.isSelected()));

        // TTD
        y += 38;
        Label lblKeluarga = new Label(); lblKeluarga.setText("Nama Pasien/Keluarga :"); lblKeluarga.setBounds(10, y, 140, 23); FormInput.add(lblKeluarga);
        TNamaKeluarga = new TextBox(); TNamaKeluarga.setBounds(155, y, 200, 23); FormInput.add(TNamaKeluarga);

        y += 32;
        Label lblPetugas = new Label(); lblPetugas.setText("Petugas :"); lblPetugas.setBounds(10, y, 65, 23); FormInput.add(lblPetugas);
        KdPetugas = new TextBox(); KdPetugas.setBounds(80, y, 80, 23); FormInput.add(KdPetugas);
        NmPetugas = new TextBox(); NmPetugas.setBounds(162, y, 165, 23); FormInput.add(NmPetugas);
        BtnPetugas = new Button(); BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPetugas.setBounds(329, y, 28, 23); FormInput.add(BtnPetugas);
        BtnPetugas.addActionListener(evt -> {
            petugasDlg.emptTeks(); petugasDlg.isCek();
            petugasDlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            petugasDlg.setLocationRelativeTo(internalFrame1); petugasDlg.setVisible(true);
        });
        petugasDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (petugasDlg.getTable().getSelectedRow() != -1) {
                    KdPetugas.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(), 0).toString());
                    NmPetugas.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

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
        ScrollInput.setPreferredSize(new Dimension(880, 420));

        PanelInput = new JPanel(new BorderLayout());
        PanelInput.setOpaque(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 440));
        PanelInput.add(ScrollInput, BorderLayout.CENTER); PanelInput.add(ChkInput, BorderLayout.PAGE_END);

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

        Label lblTglC = new Label(); lblTglC.setText("Tanggal :"); lblTglC.setPreferredSize(new Dimension(55, 23));
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
            } else if (KdPetugas.getText().trim().equals("")) { Valid.textKosong(KdPetugas, "Petugas");
            } else {
                String hasil = RdTidakBerisiko.isSelected() ? "Tidak berisiko" : (RdRisikoRendah.isSelected() ? "Risiko rendah" : "Risiko tinggi");
                if (Sequel.menyimpantf("asesmen_risiko_jatuh_getupgo", "?,?,?,?,?,?,?,?,?", "Data", 9, new String[]{
                    TNoRw.getText(), Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + " " + TglAsuhan.getSelectedItem().toString().substring(11, 19),
                    TRuangan.getText(), ChkBerjalanYa.isSelected() ? "Ya" : "Tidak", ChkMenopangYa.isSelected() ? "Ya" : "Tidak",
                    hasil, ChkTindakanYa.isSelected() ? "Ya" : "Tidak", TNamaKeluarga.getText(), KdPetugas.getText()
                })) { tampil(); emptTeks(); }
            }
        });

        BtnHapus.addActionListener(evt -> {
            if (tbObat.getSelectedRow() != -1) {
                if (Sequel.queryu2tf("delete from asesmen_risiko_jatuh_getupgo where no_rawat=? and tanggal=?", 2, new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
                })) { tampil(); emptTeks(); }
            } else JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dihapus...!!!!");
        });

        BtnEdit.addActionListener(evt -> {
            if (TNoRw.getText().trim().equals("")) { Valid.textKosong(TNoRw, "Pasien");
            } else if (tbObat.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau diganti...!!!!");
            } else {
                String hasil = RdTidakBerisiko.isSelected() ? "Tidak berisiko" : (RdRisikoRendah.isSelected() ? "Risiko rendah" : "Risiko tinggi");
                if (Sequel.mengedittf("asesmen_risiko_jatuh_getupgo", "no_rawat=? and tanggal=?",
                    "ruangan=?,cara_berjalan=?,menopang=?,hasil_penilaian=?,tindakan_ya=?,nama_keluarga=?,nip=?", 9, new String[]{
                    TRuangan.getText(), ChkBerjalanYa.isSelected() ? "Ya" : "Tidak", ChkMenopangYa.isSelected() ? "Ya" : "Tidak",
                    hasil, ChkTindakanYa.isSelected() ? "Ya" : "Tidak", TNamaKeluarga.getText(), KdPetugas.getText(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()
                })) { tampil(); emptTeks(); }
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
                Valid.MyReportqry("rptAsesmenRisikoJatuhGetUpGo.jasper", "report", "::[ Asesmen Risiko Jatuh Get Up And Go ]::",
                    "select a.*, r.no_rkm_medis, p.nm_pasien, p.tgl_lahir, p.no_ktp, pt.nama as nm_petugas " +
                    "from asesmen_risiko_jatuh_getupgo a inner join reg_periksa r on a.no_rawat=r.no_rawat " +
                    "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis left join petugas pt on a.nip=pt.nip " +
                    "where a.no_rawat='" + TNoRw.getText() + "' and a.tanggal='" +
                    Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + " " + TglAsuhan.getSelectedItem().toString().substring(11, 19) + "'", param);
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

    private void isForm() {
        if (ChkInput.isSelected()) {
            ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 440));
            FormInput.setVisible(true); ChkInput.setVisible(true);
        } else {
            ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 20));
            FormInput.setVisible(false); ChkInput.setVisible(true);
        }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select a.no_rawat, r.no_rkm_medis, p.nm_pasien, a.tanggal, a.ruangan, a.cara_berjalan, a.menopang, " +
                "a.hasil_penilaian, a.tindakan_ya, a.nama_keluarga, pt.nama as nm_petugas " +
                "from asesmen_risiko_jatuh_getupgo a inner join reg_periksa r on a.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis left join petugas pt on a.nip=pt.nip " +
                "where a.tanggal between ? and ? and (a.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) " +
                "order by a.tanggal desc");
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
            ps.setString(3, "%" + TCari.getText().trim() + "%");
            ps.setString(4, "%" + TCari.getText().trim() + "%");
            ps.setString(5, "%" + TCari.getText().trim() + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                    rs.getString("tanggal"), rs.getString("ruangan"), rs.getString("cara_berjalan"),
                    rs.getString("menopang"), rs.getString("hasil_penilaian"), rs.getString("tindakan_ya"),
                    rs.getString("nama_keluarga"), rs.getString("nm_petugas")
                });
            }
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif Get Up Go : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            try { TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString())); } catch (Exception e) {}
            TRuangan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
            ChkBerjalanYa.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString().equals("Ya"));
            ChkBerjalanTidak.setSelected(!ChkBerjalanYa.isSelected());
            ChkMenopangYa.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString().equals("Ya"));
            ChkMenopangTidak.setSelected(!ChkMenopangYa.isSelected());
            String hasil = tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString();
            if (hasil.equals("Risiko tinggi")) RdRisikoTinggi.setSelected(true);
            else if (hasil.equals("Risiko rendah")) RdRisikoRendah.setSelected(true);
            else RdTidakBerisiko.setSelected(true);
            ChkTindakanYa.setSelected(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString().equals("Ya"));
            ChkTindakanTidak.setSelected(!ChkTindakanYa.isSelected());
            TNamaKeluarga.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString());
            KdPetugas.setText(Sequel.cariIsi("select nip from asesmen_risiko_jatuh_getupgo where no_rawat='" +
                tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "' and tanggal='" +
                tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString() + "'"));
            NmPetugas.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString());
        }
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date()); TRuangan.setText("");
        ChkBerjalanYa.setSelected(false); ChkBerjalanTidak.setSelected(true);
        ChkMenopangYa.setSelected(false); ChkMenopangTidak.setSelected(true);
        RdTidakBerisiko.setSelected(true);
        ChkTindakanYa.setSelected(false); ChkTindakanTidak.setSelected(false);
        TNamaKeluarga.setText(""); KdPetugas.setText(""); NmPetugas.setText("");
    }

    public void setNoRm(String norwt, String norm, String namapasien) {
        TNoRw.setText(norwt); TNoRM.setText(norm); TPasien.setText(namapasien);
    }

    public void setNoRm(String norwt, Date tgl) {
        TNoRw.setText(norwt); TglAsuhan.setDate(tgl);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", norwt));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
        TRuangan.setText(Sequel.cariIsi("select nm_bangsal from bangsal inner join kamar on bangsal.kd_bangsal=kamar.kd_bangsal " +
            "inner join kamar_inap on kamar.kd_kamar=kamar_inap.kd_kamar where kamar_inap.no_rawat=? " +
            "order by kamar_inap.tgl_masuk desc limit 1", norwt));
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getrujukan_keluar());
        BtnHapus.setEnabled(akses.getrujukan_keluar());
        BtnEdit.setEnabled(akses.getrujukan_keluar());
        BtnPrint.setEnabled(akses.getrujukan_keluar());
    }
}
