package rekammedis;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import kepegawaian.DlgCariDokter;
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
import widget.*;

public class RMLembarKonsultasiDPJPRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private ComboBox CmbJenisKonsul;
    private TextBox TxtLainnya;
    private TextBox TxtNoKonsul;
    private TextBox KdDokterKonsul, NmDokterKonsul;
    private Button BtnDokterKonsul;
    private Label LCount;

    // Detail section - DPJP input
    private widget.Table tbDetail;
    private DefaultTableModel tabModeDetail;
    private ScrollPane ScrollDetail;
    private widget.Tanggal TglKonsulDetail;
    private TextArea TxtKonsulDPJP;
    private TextBox TxtKdDokterDPJP, TxtNmDokterDPJP;
    private Button BtnDokterDPJP;

    // WindowInput - Jawaban Konsultan (popup terpisah)
    private JDialog WindowInput;
    private TextArea TxtKonsulDisplay;   // readonly - menampilkan isi konsultasi
    private TextArea TxtJawabanInput;
    private TextBox TxtKdDokterKonsulInput, TxtNmDokterKonsulInput;
    private Button BtnDokterKonsulInput, BtnSimpanJawaban, BtnTutupJawaban;
    private widget.Tanggal TglJawabanInput;
    private DlgCariDokter dokterKonsulInputDlg = new DlgCariDokter(null, false);

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariDokter dokterKonsulDlg = new DlgCariDokter(null, false);
    private DlgCariDokter dokterDPJPDlg = new DlgCariDokter(null, false);

    private CekBox ChkInput;
    private PanelBiasa FormInput;
    private ScrollPane ScrollInput;
    private JPanel PanelInput;
    public widget.editorpane LoadHTML;

    public RMLembarKonsultasiDPJPRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initTables();
        initComponents();
        emptTeks();
        setSize(950, 700);
        setLocationRelativeTo(null);
    }

    private void initTables() {
        try {
            koneksi.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS konsultasi_dpjp_rscb (" +
                "no_konsul varchar(20) NOT NULL PRIMARY KEY," +
                "no_rawat varchar(20) NULL,tanggal varchar(30) NULL," +
                "jenis_konsul varchar(20) NULL,lainnya varchar(100) NULL," +
                "nip varchar(20) NULL,tgl_konsul varchar(30) NULL," +
                "konsul_dpjp text NULL,kd_dokter_dpjp varchar(20) NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
            koneksi.createStatement().execute(
                "CREATE TABLE IF NOT EXISTS jawaban_konsultasi_dpjp_rscb (" +
                "no_konsul varchar(20) NOT NULL PRIMARY KEY," +
                "tgl_jawaban varchar(30) NULL," +
                "jawaban_konsultan text NULL,kd_dokter_konsultan varchar(20) NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8");
        } catch (Exception e) { System.out.println("Init tables konsultasi: " + e); }
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Lembar Konsultasi DPJP ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // ========== PANEL ATAS ==========
        panelisi panelAtas = new panelisi();
        panelAtas.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 4));
        panelAtas.setPreferredSize(new Dimension(1024, 33));
        Label lblNoRw = new Label(); lblNoRw.setText("No.Rawat :");
        TNoRw = new TextBox(); TNoRw.setPreferredSize(new Dimension(130, 23));
        TNoRM = new TextBox(); TNoRM.setPreferredSize(new Dimension(100, 23));
        TPasien = new TextBox(); TPasien.setPreferredSize(new Dimension(200, 23));
        panelAtas.add(lblNoRw); panelAtas.add(TNoRw); panelAtas.add(TNoRM); panelAtas.add(TPasien);

        // ========== FORM INPUT ==========
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(920, 315));
        FormInput.setLayout(null);

        int y = 10;
        JLabel lblHeader = new JLabel("LEMBAR KONSULTASI DPJP");
        lblHeader.setFont(new java.awt.Font("Tahoma", 1, 13)); lblHeader.setBounds(10, y, 350, 20); FormInput.add(lblHeader);

        Label lblNoKonsul = new Label(); lblNoKonsul.setText("No. Konsultasi :"); lblNoKonsul.setBounds(370, y, 110, 20); FormInput.add(lblNoKonsul);
        TxtNoKonsul = new TextBox(); TxtNoKonsul.setEditable(false); TxtNoKonsul.setBackground(new Color(235, 245, 255));
        TxtNoKonsul.setBounds(480, y, 150, 20); FormInput.add(TxtNoKonsul);

        y += 32;
        Label lblTgl = new Label(); lblTgl.setText("Tanggal :"); lblTgl.setBounds(10, y, 80, 23); FormInput.add(lblTgl);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setBounds(95, y, 130, 23); FormInput.add(TglAsuhan);

        y += 32;
        Label lblJenisKonsul = new Label(); lblJenisKonsul.setText("Jenis Konsul :"); lblJenisKonsul.setBounds(10, y, 90, 23); FormInput.add(lblJenisKonsul);
        CmbJenisKonsul = new ComboBox();
        CmbJenisKonsul.setModel(new DefaultComboBoxModel(new String[]{"Biasa","Cito","Lainnya"}));
        CmbJenisKonsul.setBounds(105, y, 120, 23); FormInput.add(CmbJenisKonsul);
        CmbJenisKonsul.addActionListener(evt -> {
            TxtLainnya.setEnabled("Lainnya".equals(CmbJenisKonsul.getSelectedItem().toString()));
            if (!"Lainnya".equals(CmbJenisKonsul.getSelectedItem().toString())) TxtLainnya.setText("");
        });

        Label lblLainnya = new Label(); lblLainnya.setText("Lainnya :"); lblLainnya.setBounds(235, y, 60, 23); FormInput.add(lblLainnya);
        TxtLainnya = new TextBox(); TxtLainnya.setBounds(300, y, 200, 23); TxtLainnya.setEnabled(false); FormInput.add(TxtLainnya);

        y += 32;
        Label lblPetugas = new Label(); lblPetugas.setText("Dokter :"); lblPetugas.setBounds(10, y, 80, 23); FormInput.add(lblPetugas);
        KdDokterKonsul = new TextBox(); KdDokterKonsul.setBounds(95, y, 80, 23); FormInput.add(KdDokterKonsul);
        NmDokterKonsul = new TextBox(); NmDokterKonsul.setBounds(177, y, 180, 23); FormInput.add(NmDokterKonsul);
        BtnDokterKonsul = new Button(); BtnDokterKonsul.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokterKonsul.setBounds(359, y, 28, 23); FormInput.add(BtnDokterKonsul);
        BtnDokterKonsul.addActionListener(evt -> {
            dokterKonsulDlg.emptTeks(); dokterKonsulDlg.isCek();
            dokterKonsulDlg.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20);
            dokterKonsulDlg.setLocationRelativeTo(internalFrame1); dokterKonsulDlg.setVisible(true);
        });
        dokterKonsulDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (dokterKonsulDlg.getTable().getSelectedRow() != -1) {
                    KdDokterKonsul.setText(dokterKonsulDlg.getTable().getValueAt(dokterKonsulDlg.getTable().getSelectedRow(), 0).toString());
                    NmDokterKonsul.setText(dokterKonsulDlg.getTable().getValueAt(dokterKonsulDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

        // ===== SEKSI INPUT KONSULTASI DPJP =====
        y += 40;
        JLabel lblSeksiKonsul = new JLabel("Isi Konsultasi (oleh DPJP) :");
        lblSeksiKonsul.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSeksiKonsul.setForeground(new Color(0, 80, 160));
        lblSeksiKonsul.setBounds(10, y, 300, 20); FormInput.add(lblSeksiKonsul);

        y += 28;
        Label lblTglKonsul = new Label(); lblTglKonsul.setText("Tgl Konsultasi :"); lblTglKonsul.setBounds(10, y, 100, 23); FormInput.add(lblTglKonsul);
        TglKonsulDetail = new widget.Tanggal(); TglKonsulDetail.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglKonsulDetail.setBounds(115, y, 130, 23); FormInput.add(TglKonsulDetail);

        y += 28;
        Label lblKonsulDPJP = new Label(); lblKonsulDPJP.setText("Isi Konsultasi :"); lblKonsulDPJP.setBounds(10, y, 100, 23); FormInput.add(lblKonsulDPJP);
        TxtKonsulDPJP = new TextArea();
        JScrollPane scrKonsulDPJP = new JScrollPane(TxtKonsulDPJP);
        scrKonsulDPJP.setBounds(115, y, 600, 60); FormInput.add(scrKonsulDPJP);

        y += 65;
        Label lblDokterDPJP = new Label(); lblDokterDPJP.setText("Dokter DPJP :"); lblDokterDPJP.setBounds(10, y, 100, 23); FormInput.add(lblDokterDPJP);
        TxtKdDokterDPJP = new TextBox(); TxtKdDokterDPJP.setBounds(115, y, 80, 23); FormInput.add(TxtKdDokterDPJP);
        TxtNmDokterDPJP = new TextBox(); TxtNmDokterDPJP.setBounds(197, y, 200, 23); FormInput.add(TxtNmDokterDPJP);
        BtnDokterDPJP = new Button(); BtnDokterDPJP.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokterDPJP.setBounds(399, y, 28, 23); FormInput.add(BtnDokterDPJP);
        BtnDokterDPJP.addActionListener(evt -> {
            dokterDPJPDlg.emptTeks(); dokterDPJPDlg.isCek();
            dokterDPJPDlg.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20);
            dokterDPJPDlg.setLocationRelativeTo(internalFrame1); dokterDPJPDlg.setVisible(true);
        });
        dokterDPJPDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (dokterDPJPDlg.getTable().getSelectedRow() != -1) {
                    TxtKdDokterDPJP.setText(dokterDPJPDlg.getTable().getValueAt(dokterDPJPDlg.getTable().getSelectedRow(), 0).toString());
                    TxtNmDokterDPJP.setText(dokterDPJPDlg.getTable().getValueAt(dokterDPJPDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

        // === TABEL DETAIL ===
        // Kolom 0-4: diisi DPJP; kolom 5-6: diisi Konsultan via WindowInput
        ScrollDetail = new ScrollPane();
        tbDetail = new widget.Table();
        tabModeDetail = new DefaultTableModel(new Object[][]{},
            new String[]{"No.Konsul","No.Rawat","Nm Pasien","Tanggal","Jenis Konsul","Tgl Konsultasi","Isi Konsultasi DPJP","Nm Dokter DPJP","Jawaban Konsultan","Nm Dokter Konsultan","Tgl Jawaban","Kd Dokter DPJP","Kd Dokter Konsultan"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbDetail.setModel(tabModeDetail);
        // Sembunyikan kolom kd_dokter (index 11, 12)
        tbDetail.getColumnModel().getColumn(11).setMinWidth(0); tbDetail.getColumnModel().getColumn(11).setMaxWidth(0); tbDetail.getColumnModel().getColumn(11).setPreferredWidth(0);
        tbDetail.getColumnModel().getColumn(12).setMinWidth(0); tbDetail.getColumnModel().getColumn(12).setMaxWidth(0); tbDetail.getColumnModel().getColumn(12).setPreferredWidth(0);

        // Right-click popup on tbDetail -> Input Jawaban Konsultan
        JPopupMenu popupDetail = new JPopupMenu();
        JMenuItem mnJawaban = new JMenuItem("Input Jawaban Konsultan");
        mnJawaban.setIcon(new ImageIcon(getClass().getResource("/picture/item.png")));
        mnJawaban.addActionListener(evt -> bukaWindowJawaban());
        popupDetail.add(mnJawaban);
        tbDetail.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (tabModeDetail.getRowCount() != 0) getData();
            }
            @Override public void mousePressed(java.awt.event.MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    int row = tbDetail.rowAtPoint(evt.getPoint());
                    if (row >= 0) tbDetail.setRowSelectionInterval(row, row);
                    popupDetail.show(tbDetail, evt.getX(), evt.getY());
                }
            }
            @Override public void mouseReleased(java.awt.event.MouseEvent evt) {
                if (evt.isPopupTrigger()) {
                    int row = tbDetail.rowAtPoint(evt.getPoint());
                    if (row >= 0) tbDetail.setRowSelectionInterval(row, row);
                    popupDetail.show(tbDetail, evt.getX(), evt.getY());
                }
            }
        });

        ScrollDetail.setViewportView(tbDetail);


        // ========== WINDOW INPUT - JAWABAN KONSULTAN ==========
        WindowInput = new JDialog(this, "Jawaban Konsultan", false);
        WindowInput.setUndecorated(true);
        WindowInput.setSize(700, 310);
        WindowInput.setLocationRelativeTo(this);
        WindowInput.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        InternalFrame frameJawaban = new InternalFrame();
        frameJawaban.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(230, 235, 225)),
            "::[ Jawaban Konsultan ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11), new Color(50, 70, 40)));
        frameJawaban.setLayout(null);

        JLabel lblKonsulDLabel = new JLabel("Isi Konsultasi DPJP :");
        lblKonsulDLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); lblKonsulDLabel.setForeground(new Color(0,80,160));
        lblKonsulDLabel.setBounds(10, 15, 160, 20); frameJawaban.add(lblKonsulDLabel);

        TxtKonsulDisplay = new TextArea();
        TxtKonsulDisplay.setEditable(false);
        TxtKonsulDisplay.setBackground(new Color(245, 245, 245));
        JScrollPane scrDisplay = new JScrollPane(TxtKonsulDisplay);
        scrDisplay.setBounds(10, 38, 660, 60); frameJawaban.add(scrDisplay);

        JLabel lblTglJawaban = new JLabel("Tgl Jawaban :");
        lblTglJawaban.setBounds(10, 108, 100, 23); frameJawaban.add(lblTglJawaban);
        TglJawabanInput = new widget.Tanggal(); TglJawabanInput.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglJawabanInput.setBounds(115, 108, 130, 23); frameJawaban.add(TglJawabanInput);

        JLabel lblJawabanInput = new JLabel("Jawaban :");
        lblJawabanInput.setBounds(10, 138, 100, 23); frameJawaban.add(lblJawabanInput);
        TxtJawabanInput = new TextArea();
        JScrollPane scrJawabanInput = new JScrollPane(TxtJawabanInput);
        scrJawabanInput.setBounds(115, 138, 555, 60); frameJawaban.add(scrJawabanInput);

        JLabel lblDokterKonsulInput = new JLabel("Dokter Konsultan :");
        lblDokterKonsulInput.setBounds(10, 208, 120, 23); frameJawaban.add(lblDokterKonsulInput);
        TxtKdDokterKonsulInput = new TextBox(); TxtKdDokterKonsulInput.setBounds(135, 208, 80, 23); frameJawaban.add(TxtKdDokterKonsulInput);
        TxtNmDokterKonsulInput = new TextBox(); TxtNmDokterKonsulInput.setBounds(217, 208, 200, 23); frameJawaban.add(TxtNmDokterKonsulInput);
        BtnDokterKonsulInput = new Button(); BtnDokterKonsulInput.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokterKonsulInput.setBounds(419, 208, 28, 23); frameJawaban.add(BtnDokterKonsulInput);
        BtnDokterKonsulInput.addActionListener(evt -> {
            dokterKonsulInputDlg.emptTeks(); dokterKonsulInputDlg.isCek();
            dokterKonsulInputDlg.setSize(700, 500);
            dokterKonsulInputDlg.setLocationRelativeTo(WindowInput); dokterKonsulInputDlg.setVisible(true);
        });
        dokterKonsulInputDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (dokterKonsulInputDlg.getTable().getSelectedRow() != -1) {
                    TxtKdDokterKonsulInput.setText(dokterKonsulInputDlg.getTable().getValueAt(dokterKonsulInputDlg.getTable().getSelectedRow(), 0).toString());
                    TxtNmDokterKonsulInput.setText(dokterKonsulInputDlg.getTable().getValueAt(dokterKonsulInputDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

        BtnSimpanJawaban = new Button();
        BtnSimpanJawaban.setIcon(new ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpanJawaban.setText("Simpan Jawaban"); BtnSimpanJawaban.setBounds(10, 248, 140, 30); frameJawaban.add(BtnSimpanJawaban);
        BtnTutupJawaban = new Button();
        BtnTutupJawaban.setIcon(new ImageIcon(getClass().getResource("/picture/cross.png")));
        BtnTutupJawaban.setText("Tutup"); BtnTutupJawaban.setBounds(158, 248, 100, 30); frameJawaban.add(BtnTutupJawaban);
        BtnTutupJawaban.addActionListener(evt -> WindowInput.dispose());

        BtnSimpanJawaban.addActionListener(evt -> {
            if (tbDetail.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih baris konsultasi terlebih dahulu...");
                return;
            }
            if (TxtKdDokterKonsulInput.getText().trim().isEmpty()) {
                Valid.textKosong(TxtKdDokterKonsulInput, "Dokter Konsultan");
                return;
            }
            if (TxtJawabanInput.getText().trim().isEmpty()) {
                Valid.textKosong(TxtJawabanInput, "Jawaban");
                return;
            }
            int jawRow = tbDetail.getSelectedRow();
            String noKonsulJ = tabModeDetail.getValueAt(jawRow, 0).toString();
            String tglJawaban = Valid.SetTgl(TglJawabanInput.getSelectedItem()+"")+" "+TglJawabanInput.getSelectedItem().toString().substring(11,19);
            // Cek apakah jawaban sudah ada
            String cekJawaban = Sequel.cariIsi("select no_konsul from jawaban_konsultasi_dpjp_rscb where no_konsul='"+noKonsulJ+"'");
            boolean saved;
            if (cekJawaban == null || cekJawaban.isEmpty()) {
                saved = Sequel.menyimpantf("jawaban_konsultasi_dpjp_rscb", "?,?,?,?", "Jawaban", 4,
                    new String[]{noKonsulJ, tglJawaban, TxtJawabanInput.getText(), TxtKdDokterKonsulInput.getText()});
            } else {
                saved = Sequel.queryu2tf(
                    "update jawaban_konsultasi_dpjp_rscb set tgl_jawaban=?,jawaban_konsultan=?,kd_dokter_konsultan=? where no_konsul=?",
                    4, new String[]{tglJawaban, TxtJawabanInput.getText(), TxtKdDokterKonsulInput.getText(), noKonsulJ});
            }
            if (saved) {
                // Update tabel lokal
                tabModeDetail.setValueAt(TxtJawabanInput.getText(), jawRow, 8);
                tabModeDetail.setValueAt(TxtKdDokterKonsulInput.getText(), jawRow, 12);
                tabModeDetail.setValueAt(TxtNmDokterKonsulInput.getText(), jawRow, 9);
                JOptionPane.showMessageDialog(null, "Jawaban berhasil disimpan.");
                WindowInput.dispose();
            }
        });

        WindowInput.getContentPane().setLayout(new BorderLayout());
        WindowInput.getContentPane().add(frameJawaban, BorderLayout.CENTER);

        // ========== INPUT TOGGLE ==========
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
        ScrollInput.setPreferredSize(new Dimension(920, 325));
        PanelInput = new JPanel(new BorderLayout());
        PanelInput.setOpaque(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 345));
        PanelInput.add(ScrollInput, BorderLayout.CENTER); PanelInput.add(ChkInput, BorderLayout.PAGE_END);

        // ========== PANEL BAWAH ==========
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
        TCari = new TextBox(); TCari.setPreferredSize(new Dimension(150, 23));
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

        // ========== ACTIONS ==========
        BtnKeluar.addActionListener(evt -> dispose());
        BtnBatal.addActionListener(evt -> emptTeks());
        BtnCari.addActionListener(evt -> tampil());
        BtnAll.addActionListener(evt -> { TCari.setText(""); tampil(); });
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) { if (evt.getKeyCode() == KeyEvent.VK_ENTER) tampil(); }
        });

        BtnSimpan.addActionListener(evt -> {
            if (TNoRw.getText().trim().isEmpty()) {
                Valid.textKosong(TNoRw, "Pasien");
            } else if (KdDokterKonsul.getText().trim().isEmpty()) {
                Valid.textKosong(KdDokterKonsul, "Dokter");
            } else {
                String tanggal   = Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19);
                String tglKonsul = Valid.SetTgl(TglKonsulDetail.getSelectedItem()+"")+" "+TglKonsulDetail.getSelectedItem().toString().substring(11,19);
                String noKonsul  = TxtNoKonsul.getText().trim();
                if (noKonsul.isEmpty()) { JOptionPane.showMessageDialog(null, "No. Konsultasi kosong!"); return; }
                if (Sequel.queryu2tf(
                    "insert into konsultasi_dpjp_rscb(no_konsul,no_rawat,tanggal,jenis_konsul,lainnya,nip,tgl_konsul,konsul_dpjp,kd_dokter_dpjp) values(?,?,?,?,?,?,?,?,?)",
                    9, new String[]{noKonsul, TNoRw.getText(), tanggal,
                        CmbJenisKonsul.getSelectedItem().toString(), TxtLainnya.getText(),
                        KdDokterKonsul.getText(), tglKonsul,
                        TxtKonsulDPJP.getText(), TxtKdDokterDPJP.getText()})) {
                    tampil(); emptTeks();
                }
            }
        });

        BtnHapus.addActionListener(evt -> {
            if (tbDetail.getSelectedRow() != -1) {
                String noKonsul = tabModeDetail.getValueAt(tbDetail.getSelectedRow(), 0).toString();
                Sequel.queryu2tf("delete from jawaban_konsultasi_dpjp_rscb where no_konsul=?", 1, new String[]{noKonsul});
                if (Sequel.queryu2tf("delete from konsultasi_dpjp_rscb where no_konsul=?", 1, new String[]{noKonsul})) {
                    tampil(); emptTeks();
                }
            } else JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dihapus...!!!!");
        });

        BtnEdit.addActionListener(evt -> {
            if (TNoRw.getText().trim().isEmpty() || tbDetail.getSelectedRow()==-1) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            } else {
                String noKonsul = tabModeDetail.getValueAt(tbDetail.getSelectedRow(), 0).toString();
                String tanggal  = Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19);
                String tglKonsul = Valid.SetTgl(TglKonsulDetail.getSelectedItem()+"")+" "+TglKonsulDetail.getSelectedItem().toString().substring(11,19);
                if (Sequel.queryu2tf(
                    "update konsultasi_dpjp_rscb set no_rawat=?,tanggal=?,jenis_konsul=?,lainnya=?,nip=?,tgl_konsul=?,konsul_dpjp=?,kd_dokter_dpjp=? where no_konsul=?",
                    9, new String[]{TNoRw.getText(), tanggal, CmbJenisKonsul.getSelectedItem().toString(),
                        TxtLainnya.getText(), KdDokterKonsul.getText(), tglKonsul,
                        TxtKonsulDPJP.getText(), TxtKdDokterDPJP.getText(), noKonsul})) {
                    tampil(); emptTeks();
                }
            }
        });

        BtnPrint.addActionListener(evt -> {
            if (tbDetail.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu..!!!");
            } else {
                String noKonsulPrint = tabModeDetail.getValueAt(tbDetail.getSelectedRow(), 0).toString();
                String noRawatPrint  = tabModeDetail.getValueAt(tbDetail.getSelectedRow(), 1).toString();
                Map<String, Object> param = new HashMap<>();
                param.put("namars", akses.getnamars()); param.put("alamatrs", akses.getalamatrs());
                param.put("propinsirs", akses.getpropinsirs()); param.put("kabupatenrs", akses.getkabupatenrs());
                param.put("kontakrs", akses.getkontakrs()); param.put("emailrs", akses.getemailrs());
                param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
                Valid.MyReportqry("rptLembarKonsultasiDPJPRscb.jasper", "report", "::[ Lembar Konsultasi DPJP ]::",
                    "select k.no_konsul, k.no_rawat, k.tanggal, k.jenis_konsul, k.lainnya, " +
                    "r.no_rkm_medis, p.nm_pasien, dk0.nm_dokter as nm_petugas, k.nip as kd_dokter_header, " +
                    "k.tgl_konsul, k.konsul_dpjp, k.kd_dokter_dpjp, dk1.nm_dokter as nm_dokter_dpjp, " +
                    "j.jawaban_konsultan, j.kd_dokter_konsultan, dk2.nm_dokter as nm_dokter_konsultan, j.tgl_jawaban " +
                    "from konsultasi_dpjp_rscb k " +
                    "inner join reg_periksa r on k.no_rawat=r.no_rawat " +
                    "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                    "left join dokter dk0 on k.nip=dk0.kd_dokter " +
                    "left join jawaban_konsultasi_dpjp_rscb j on k.no_konsul=j.no_konsul " +
                    "left join dokter dk1 on k.kd_dokter_dpjp=dk1.kd_dokter " +
                    "left join dokter dk2 on j.kd_dokter_konsultan=dk2.kd_dokter " +
                    "where k.no_rawat='" + noRawatPrint + "' order by k.tanggal", param);
            }
        });

        // ========== ASSEMBLY ==========
        InternalFrame internalFrameData = new InternalFrame();
        internalFrameData.setLayout(new BorderLayout());
        internalFrameData.setBackground(new Color(255, 255, 255));
        internalFrameData.add(PanelInput, BorderLayout.NORTH);
        internalFrameData.add(ScrollDetail, BorderLayout.CENTER);
        internalFrame1.add(panelAtas, BorderLayout.PAGE_START);
        internalFrame1.add(internalFrameData, BorderLayout.CENTER);
        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);
        setLayout(new BorderLayout());
        add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
    }

    /** Buka WindowInput untuk mengisi jawaban konsultan */
    private void bukaWindowJawaban() {
        int selRow = tbDetail.getSelectedRow();
        if (selRow == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih baris konsultasi yang akan dijawab...");
            return;
        }
        // Tampilkan isi konsultasi DPJP (col 6)
        TxtKonsulDisplay.setText(tabModeDetail.getValueAt(selRow, 6) == null ? "" : tabModeDetail.getValueAt(selRow, 6).toString());
        // Pre-fill jawaban jika sudah ada
        TxtJawabanInput.setText(tabModeDetail.getValueAt(selRow, 8) == null ? "" : tabModeDetail.getValueAt(selRow, 8).toString());
        TxtKdDokterKonsulInput.setText(tabModeDetail.getValueAt(selRow, 12) == null ? "" : tabModeDetail.getValueAt(selRow, 12).toString());
        TxtNmDokterKonsulInput.setText(tabModeDetail.getValueAt(selRow, 9) == null ? "" : tabModeDetail.getValueAt(selRow, 9).toString());
        TglJawabanInput.setDate(new Date());
        WindowInput.setSize(700, 310);
        WindowInput.setLocationRelativeTo(this);
        WindowInput.setVisible(true);
    }

    private void isForm() {
        if (ChkInput.isSelected()) {
            ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 345));
            FormInput.setVisible(true); ChkInput.setVisible(true);
        } else {
            ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 20));
            FormInput.setVisible(false); ChkInput.setVisible(true);
        }
    }

    public void tampil() {
        Valid.tabelKosong(tabModeDetail);
        try {
            ps = koneksi.prepareStatement(
                "select k.no_konsul, k.no_rawat, p.nm_pasien, k.tanggal, k.jenis_konsul, " +
                "k.tgl_konsul, k.konsul_dpjp, dk1.nm_dokter as nm_dokter_dpjp, " +
                "j.jawaban_konsultan, dk2.nm_dokter as nm_dokter_konsultan, j.tgl_jawaban, " +
                "k.kd_dokter_dpjp, j.kd_dokter_konsultan " +
                "from konsultasi_dpjp_rscb k " +
                "inner join reg_periksa r on k.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                "left join jawaban_konsultasi_dpjp_rscb j on k.no_konsul=j.no_konsul " +
                "left join dokter dk1 on k.kd_dokter_dpjp=dk1.kd_dokter " +
                "left join dokter dk2 on j.kd_dokter_konsultan=dk2.kd_dokter " +
                "where DATE(k.tanggal) between ? and ? and (k.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) " +
                "order by k.tanggal desc");
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem()+""));
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem()+""));
            ps.setString(3, "%"+TCari.getText().trim()+"%");
            ps.setString(4, "%"+TCari.getText().trim()+"%");
            ps.setString(5, "%"+TCari.getText().trim()+"%");
            rs = ps.executeQuery();
            while (rs.next()) {
                tabModeDetail.addRow(new Object[]{
                    rs.getString("no_konsul"), rs.getString("no_rawat"), rs.getString("nm_pasien"),
                    rs.getString("tanggal"), rs.getString("jenis_konsul"),
                    rs.getString("tgl_konsul"), rs.getString("konsul_dpjp"), rs.getString("nm_dokter_dpjp"),
                    rs.getString("jawaban_konsultan"), rs.getString("nm_dokter_konsultan"),
                    rs.getString("tgl_jawaban"), rs.getString("kd_dokter_dpjp"),
                    rs.getString("kd_dokter_konsultan")
                });
            }
            LCount.setText("Record : " + tabModeDetail.getRowCount());
        } catch (Exception e) { System.out.println("Notif Lembar Konsultasi DPJP : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void getData() {
        int row = tbDetail.getSelectedRow();
        if (row == -1) return;
        String noKonsul = tabModeDetail.getValueAt(row, 0).toString();
        String noRawat  = tabModeDetail.getValueAt(row, 1).toString();
        String tgl      = tabModeDetail.getValueAt(row, 3).toString();
        TxtNoKonsul.setText(noKonsul);
        TNoRw.setText(noRawat);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat='" + noRawat + "'"));
        TPasien.setText(tabModeDetail.getValueAt(row, 2) == null ? "" : tabModeDetail.getValueAt(row, 2).toString());
        try { TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tgl)); } catch (Exception e) {}
        String jenisKonsul = tabModeDetail.getValueAt(row, 4) == null ? "" : tabModeDetail.getValueAt(row, 4).toString();
        CmbJenisKonsul.setSelectedItem(jenisKonsul);
        TxtLainnya.setText(Sequel.cariIsi("select lainnya from konsultasi_dpjp_rscb where no_konsul='"+noKonsul+"'"));
        TxtLainnya.setEnabled("Lainnya".equals(jenisKonsul));
        KdDokterKonsul.setText(Sequel.cariIsi("select nip from konsultasi_dpjp_rscb where no_konsul='"+noKonsul+"'"));
        NmDokterKonsul.setText(Sequel.cariIsi("select dk.nm_dokter from dokter dk inner join konsultasi_dpjp_rscb k on k.nip=dk.kd_dokter where k.no_konsul='"+noKonsul+"'"));
        String tglKonsul = tabModeDetail.getValueAt(row, 5) == null ? "" : tabModeDetail.getValueAt(row, 5).toString();
        if (!tglKonsul.isEmpty()) try { TglKonsulDetail.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(tglKonsul)); } catch (Exception e) {}
        TxtKonsulDPJP.setText(tabModeDetail.getValueAt(row, 6) == null ? "" : tabModeDetail.getValueAt(row, 6).toString());
        TxtKdDokterDPJP.setText(tabModeDetail.getValueAt(row, 11) == null ? "" : tabModeDetail.getValueAt(row, 11).toString());
        TxtNmDokterDPJP.setText(tabModeDetail.getValueAt(row, 7) == null ? "" : tabModeDetail.getValueAt(row, 7).toString());
    }

    public void emptTeks() {
        String tglHari = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
        String prefix  = "KONS-" + tglHari + "-";
        String lastNo  = Sequel.cariIsi("select no_konsul from konsultasi_dpjp_rscb where no_konsul like '"+prefix+"%' order by no_konsul desc limit 1");
        int seq = 1;
        if (lastNo != null && !lastNo.isEmpty()) try { seq = Integer.parseInt(lastNo.replace(prefix,"")) + 1; } catch(Exception ex){}
        TxtNoKonsul.setText(prefix + String.format("%04d", seq));
        TglAsuhan.setDate(new Date()); TglKonsulDetail.setDate(new Date());
        CmbJenisKonsul.setSelectedIndex(0); TxtLainnya.setText(""); TxtLainnya.setEnabled(false);
        KdDokterKonsul.setText(""); NmDokterKonsul.setText("");
        TxtKonsulDPJP.setText(""); TxtKdDokterDPJP.setText(""); TxtNmDokterDPJP.setText("");
        Valid.tabelKosong(tabModeDetail);
    }

    public void setNoRm(String norwt, String norm, String nmpas) {
        TNoRw.setText(norwt); TNoRM.setText(norm); TPasien.setText(nmpas);
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
