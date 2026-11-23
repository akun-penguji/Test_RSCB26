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
import widget.*;

public class RMSkalaMorse extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private TextBox KdPetugas, NmPetugas;
    private Button BtnPetugas;
    private Label LCount, LTotalNilai, LLevelResiko;

    // Skoring radio buttons
    private JRadioButton RdJatuhYa, RdJatuhTidak;
    private JRadioButton RdDiagnosaTidak, RdDiagnosaYa;
    private JRadioButton RdAlatTidakAda, RdAlatKruk, RdAlatPerabot;
    private JRadioButton RdInfusTidak, RdInfusYa;
    private JRadioButton RdGayaNormal, RdGayaLemah, RdGayaTerganggu;
    private JRadioButton RdMentalSadar, RdMentalLupa;
    private ComboBox CmbSkoring;

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

    public RMSkalaMorse(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void hitungTotal() {
        int total = 0;
        if (RdJatuhYa.isSelected()) total += 25;
        if (RdDiagnosaYa.isSelected()) total += 15;
        if (RdAlatKruk.isSelected()) total += 15;
        else if (RdAlatPerabot.isSelected()) total += 30;
        if (RdInfusYa.isSelected()) total += 20;
        if (RdGayaLemah.isSelected()) total += 10;
        else if (RdGayaTerganggu.isSelected()) total += 20;
        if (RdMentalLupa.isSelected()) total += 15;
        LTotalNilai.setText("Total Nilai : " + total);
        if (total <= 24) LLevelResiko.setText("Tidak beresiko (0-24)");
        else if (total <= 50) LLevelResiko.setText("Resiko rendah (25-50)");
        else LLevelResiko.setText("Risiko tinggi (>51)");
    }

    private int getScore(JRadioButton... buttons) {
        // returns the value based on which is selected
        return 0; // placeholder, actual values set in save
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Skala Morse ]::",
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
            new String[]{"No.Rawat", "No.R.M.", "Nama", "Tanggal", "Skoring", "Riwayat Jatuh", "Diagnosa", "Alat Bantu", "Infus", "Gaya Jalan", "Mental", "Total", "Level", "Petugas"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbObat.setModel(tabMode);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { if (tabMode.getRowCount() != 0) getData(); }
        });
        javax.swing.JPopupMenu popupMenu = new javax.swing.JPopupMenu();
        javax.swing.JMenuItem mnCetak = new javax.swing.JMenuItem("Cetak Skala Morse");
        mnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetak.addActionListener(evt -> BtnPrint.doClick());
        popupMenu.add(mnCetak);
        tbObat.setComponentPopupMenu(popupMenu);

        Scroll.setViewportView(tbObat);

        // FORM INPUT
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(880, 520));
        FormInput.setLayout(null);

        java.awt.event.ActionListener recalc = e -> hitungTotal();

        int y = 10;
        JLabel lblHeader = new JLabel("SKALA MORSE");
        lblHeader.setFont(new java.awt.Font("Tahoma", 1, 13)); lblHeader.setBounds(10, y, 200, 20); FormInput.add(lblHeader);
        JLabel lblKode = new JLabel("RM.KEP.08.031.2019");
        lblKode.setFont(new java.awt.Font("Tahoma", 0, 8)); lblKode.setBounds(700, y, 150, 20); FormInput.add(lblKode);

        y += 32;
        Label lblTgl = new Label(); lblTgl.setText("Tanggal :"); lblTgl.setBounds(10, y, 80, 23); FormInput.add(lblTgl);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setBounds(95, y, 130, 23); FormInput.add(TglAsuhan);
        Label lblSkoring = new Label(); lblSkoring.setText("Skoring :"); lblSkoring.setBounds(260, y, 60, 23); FormInput.add(lblSkoring);
        CmbSkoring = new ComboBox();
        CmbSkoring.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"1 - Saat Masuk", "2", "3"}));
        CmbSkoring.setBounds(325, y, 130, 23); FormInput.add(CmbSkoring);

        // === FAKTOR RESIKO ===
        y += 38;
        JLabel lblFR = new JLabel("Faktor Resiko");
        lblFR.setFont(new java.awt.Font("Tahoma", 1, 11)); lblFR.setBounds(10, y, 200, 20); FormInput.add(lblFR);
        JLabel lblSkala = new JLabel("SKALA");
        lblSkala.setFont(new java.awt.Font("Tahoma", 1, 11)); lblSkala.setBounds(250, y, 80, 20); FormInput.add(lblSkala);

        // 1. Riwayat Jatuh
        y += 32;
        JLabel lbl1 = new JLabel("<html><b>Riwayat Jatuh</b>, Yang Baru<br>Atau Dalam 3 Bulan Terakhir</html>");
        lbl1.setBounds(10, y, 220, 38); FormInput.add(lbl1);
        RdJatuhYa = new JRadioButton("Iya (25)"); RdJatuhYa.setBounds(250, y, 100, 20); RdJatuhYa.setOpaque(false); RdJatuhYa.addActionListener(recalc); FormInput.add(RdJatuhYa);
        RdJatuhTidak = new JRadioButton("Tidak (0)"); RdJatuhTidak.setBounds(250, y+20, 100, 20); RdJatuhTidak.setOpaque(false); RdJatuhTidak.setSelected(true); RdJatuhTidak.addActionListener(recalc); FormInput.add(RdJatuhTidak);
        ButtonGroup bg1 = new ButtonGroup(); bg1.add(RdJatuhYa); bg1.add(RdJatuhTidak);

        // 2. Diagnosa Sekunder
        y += 48;
        JLabel lbl2 = new JLabel("<html><b>Memiliki Diagnosa Sekunder</b>,<br>Atau Diagnosa Medis >1</html>");
        lbl2.setBounds(10, y, 220, 38); FormInput.add(lbl2);
        RdDiagnosaTidak = new JRadioButton("Tidak (0)"); RdDiagnosaTidak.setBounds(250, y, 100, 20); RdDiagnosaTidak.setOpaque(false); RdDiagnosaTidak.setSelected(true); RdDiagnosaTidak.addActionListener(recalc); FormInput.add(RdDiagnosaTidak);
        RdDiagnosaYa = new JRadioButton("Iya (15)"); RdDiagnosaYa.setBounds(250, y+20, 100, 20); RdDiagnosaYa.setOpaque(false); RdDiagnosaYa.addActionListener(recalc); FormInput.add(RdDiagnosaYa);
        ButtonGroup bg2 = new ButtonGroup(); bg2.add(RdDiagnosaTidak); bg2.add(RdDiagnosaYa);

        // 3. Alat Bantu
        y += 48;
        JLabel lbl3 = new JLabel("<html><b>Menggunakan Alat-Alat</b><br><b>Bantu</b></html>");
        lbl3.setBounds(10, y, 220, 38); FormInput.add(lbl3);
        RdAlatTidakAda = new JRadioButton("Tidak Ada / Kursi Roda / Bedrest (0)"); RdAlatTidakAda.setBounds(250, y, 280, 20); RdAlatTidakAda.setOpaque(false); RdAlatTidakAda.setSelected(true); RdAlatTidakAda.addActionListener(recalc); FormInput.add(RdAlatTidakAda);
        RdAlatKruk = new JRadioButton("Kruk / Tongkat (15)"); RdAlatKruk.setBounds(250, y+20, 200, 20); RdAlatKruk.setOpaque(false); RdAlatKruk.addActionListener(recalc); FormInput.add(RdAlatKruk);
        RdAlatPerabot = new JRadioButton("Berpegangan Pada Perabot (30)"); RdAlatPerabot.setBounds(250, y+40, 250, 20); RdAlatPerabot.setOpaque(false); RdAlatPerabot.addActionListener(recalc); FormInput.add(RdAlatPerabot);
        ButtonGroup bg3 = new ButtonGroup(); bg3.add(RdAlatTidakAda); bg3.add(RdAlatKruk); bg3.add(RdAlatPerabot);

        // 4. Infus/Heparin
        y += 68;
        JLabel lbl4 = new JLabel("<html><b>Menggunakan Infus,</b><br><b>Heparin/Pengencer Darah</b></html>");
        lbl4.setBounds(10, y, 220, 38); FormInput.add(lbl4);
        RdInfusTidak = new JRadioButton("Tidak (0)"); RdInfusTidak.setBounds(250, y, 100, 20); RdInfusTidak.setOpaque(false); RdInfusTidak.setSelected(true); RdInfusTidak.addActionListener(recalc); FormInput.add(RdInfusTidak);
        RdInfusYa = new JRadioButton("Iya (20)"); RdInfusYa.setBounds(250, y+20, 100, 20); RdInfusYa.setOpaque(false); RdInfusYa.addActionListener(recalc); FormInput.add(RdInfusYa);
        ButtonGroup bg4 = new ButtonGroup(); bg4.add(RdInfusTidak); bg4.add(RdInfusYa);

        // 5. Gaya Berjalan
        y += 48;
        JLabel lbl5 = new JLabel("<html><b>Gaya Berjalan/Berpindah</b></html>");
        lbl5.setBounds(10, y, 220, 20); FormInput.add(lbl5);
        RdGayaNormal = new JRadioButton("Normal / Tirah Baring / Imobilisasi (0)"); RdGayaNormal.setBounds(250, y, 300, 20); RdGayaNormal.setOpaque(false); RdGayaNormal.setSelected(true); RdGayaNormal.addActionListener(recalc); FormInput.add(RdGayaNormal);
        RdGayaLemah = new JRadioButton("Lemah (10)"); RdGayaLemah.setBounds(250, y+20, 150, 20); RdGayaLemah.setOpaque(false); RdGayaLemah.addActionListener(recalc); FormInput.add(RdGayaLemah);
        RdGayaTerganggu = new JRadioButton("Terganggu (20)"); RdGayaTerganggu.setBounds(250, y+40, 150, 20); RdGayaTerganggu.setOpaque(false); RdGayaTerganggu.addActionListener(recalc); FormInput.add(RdGayaTerganggu);
        ButtonGroup bg5 = new ButtonGroup(); bg5.add(RdGayaNormal); bg5.add(RdGayaLemah); bg5.add(RdGayaTerganggu);

        // 6. Status Mental
        y += 68;
        JLabel lbl6 = new JLabel("<html><b>Status Mental</b></html>");
        lbl6.setBounds(10, y, 220, 20); FormInput.add(lbl6);
        RdMentalSadar = new JRadioButton("Menyadari Kemampuan (0)"); RdMentalSadar.setBounds(250, y, 220, 20); RdMentalSadar.setOpaque(false); RdMentalSadar.setSelected(true); RdMentalSadar.addActionListener(recalc); FormInput.add(RdMentalSadar);
        RdMentalLupa = new JRadioButton("Lupa / Pelupa (15)"); RdMentalLupa.setBounds(250, y+20, 180, 20); RdMentalLupa.setOpaque(false); RdMentalLupa.addActionListener(recalc); FormInput.add(RdMentalLupa);
        ButtonGroup bg6 = new ButtonGroup(); bg6.add(RdMentalSadar); bg6.add(RdMentalLupa);

        // Total & Level
        y += 50;
        LTotalNilai = new Label(); LTotalNilai.setText("Total Nilai : 0");
        LTotalNilai.setFont(new java.awt.Font("Tahoma", 1, 12)); LTotalNilai.setBounds(10, y, 250, 23); FormInput.add(LTotalNilai);
        LLevelResiko = new Label(); LLevelResiko.setText("Tidak beresiko (0-24)");
        LLevelResiko.setFont(new java.awt.Font("Tahoma", 1, 12)); LLevelResiko.setBounds(280, y, 300, 23);
        LLevelResiko.setForeground(new Color(0, 128, 0)); FormInput.add(LLevelResiko);

        // Petugas
        y += 38;
        Label lblPetugas = new Label(); lblPetugas.setText("Petugas :"); lblPetugas.setBounds(10, y, 65, 23); FormInput.add(lblPetugas);
        KdPetugas = new TextBox(); KdPetugas.setBounds(80, y, 80, 23); FormInput.add(KdPetugas);
        NmPetugas = new TextBox(); NmPetugas.setBounds(162, y, 165, 23); FormInput.add(NmPetugas);
        BtnPetugas = new Button(); BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPetugas.setBounds(329, y, 28, 23); FormInput.add(BtnPetugas);
        BtnPetugas.addActionListener(evt -> {
            petugasDlg.emptTeks(); petugasDlg.isCek();
            petugasDlg.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20);
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
        ScrollInput.setPreferredSize(new Dimension(880, 430));
        PanelInput = new JPanel(new BorderLayout());
        PanelInput.setOpaque(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 450));
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
            } else if (KdPetugas.getText().trim().equals("")) { Valid.textKosong(KdPetugas, "Petugas");
            } else {
                String skoring = CmbSkoring.getSelectedItem().toString().substring(0, 1);
                int jatuh = RdJatuhYa.isSelected() ? 25 : 0;
                int diagnosa = RdDiagnosaYa.isSelected() ? 15 : 0;
                int alat = RdAlatPerabot.isSelected() ? 30 : (RdAlatKruk.isSelected() ? 15 : 0);
                int infus = RdInfusYa.isSelected() ? 20 : 0;
                int gaya = RdGayaTerganggu.isSelected() ? 20 : (RdGayaLemah.isSelected() ? 10 : 0);
                int mental = RdMentalLupa.isSelected() ? 15 : 0;
                int total = jatuh + diagnosa + alat + infus + gaya + mental;
                String level = total <= 24 ? "Tidak beresiko" : (total <= 50 ? "Resiko rendah" : "Risiko tinggi");

                if (Sequel.menyimpantf("skala_morse", "?,?,?,?,?,?,?,?,?,?,?,?", "Data", 12, new String[]{
                    TNoRw.getText(), Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19),
                    skoring, String.valueOf(jatuh), String.valueOf(diagnosa), String.valueOf(alat),
                    String.valueOf(infus), String.valueOf(gaya), String.valueOf(mental),
                    String.valueOf(total), level, KdPetugas.getText()
                })) { tampil(); emptTeks(); }
            }
        });

        BtnHapus.addActionListener(evt -> {
            if (tbObat.getSelectedRow() != -1) {
                if (Sequel.queryu2tf("delete from skala_morse where no_rawat=? and tanggal=? and skoring=?", 3, new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString()
                })) { tampil(); emptTeks(); }
            } else JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dihapus...!!!!");
        });

        BtnEdit.addActionListener(evt -> {
            if (TNoRw.getText().trim().equals("") || tbObat.getSelectedRow()==-1) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            } else {
                if (Sequel.queryu2tf("delete from skala_morse where no_rawat=? and tanggal=? and skoring=?", 3, new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString(),
                    tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString()
                })) { BtnSimpan.doClick(); }
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
                Valid.MyReportqry("rptSkalaMorse.jasper", "report", "::[ Skala Morse ]::",
                    "select s.*, r.no_rkm_medis, p.nm_pasien, p.tgl_lahir, p.no_ktp, pt.nama as nm_petugas " +
                    "from skala_morse s inner join reg_periksa r on s.no_rawat=r.no_rawat " +
                    "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis left join petugas pt on s.nip=pt.nip " +
                    "where s.no_rawat='" + TNoRw.getText() + "'", param);
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
        if (ChkInput.isSelected()) { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 450)); FormInput.setVisible(true); ChkInput.setVisible(true);
        } else { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 20)); FormInput.setVisible(false); ChkInput.setVisible(true); }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select s.no_rawat, r.no_rkm_medis, p.nm_pasien, s.tanggal, s.skoring, " +
                "s.riwayat_jatuh, s.diagnosa_sekunder, s.alat_bantu, s.infus_heparin, s.gaya_berjalan, " +
                "s.status_mental, s.total_nilai, s.level_resiko, pt.nama as nm_petugas " +
                "from skala_morse s inner join reg_periksa r on s.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis left join petugas pt on s.nip=pt.nip " +
                "where s.tanggal between ? and ? and (s.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) " +
                "order by s.tanggal desc, s.skoring");
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
            ps.setString(3, "%"+TCari.getText().trim()+"%");
            ps.setString(4, "%"+TCari.getText().trim()+"%");
            ps.setString(5, "%"+TCari.getText().trim()+"%");
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                    rs.getString("tanggal"), rs.getString("skoring"),
                    rs.getString("riwayat_jatuh"), rs.getString("diagnosa_sekunder"), rs.getString("alat_bantu"),
                    rs.getString("infus_heparin"), rs.getString("gaya_berjalan"), rs.getString("status_mental"),
                    rs.getString("total_nilai"), rs.getString("level_resiko"), rs.getString("nm_petugas")
                });
            }
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif Skala Morse : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            try { TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString())); } catch (Exception e) {}
            String sk = tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString();
            CmbSkoring.setSelectedIndex(sk.equals("2")?1:(sk.equals("3")?2:0));
            int jatuh = Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            if (jatuh==25) RdJatuhYa.setSelected(true); else RdJatuhTidak.setSelected(true);
            int diag = Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
            if (diag==15) RdDiagnosaYa.setSelected(true); else RdDiagnosaTidak.setSelected(true);
            int alat = Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString());
            if (alat==30) RdAlatPerabot.setSelected(true); else if (alat==15) RdAlatKruk.setSelected(true); else RdAlatTidakAda.setSelected(true);
            int inf = Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString());
            if (inf==20) RdInfusYa.setSelected(true); else RdInfusTidak.setSelected(true);
            int gaya = Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString());
            if (gaya==20) RdGayaTerganggu.setSelected(true); else if (gaya==10) RdGayaLemah.setSelected(true); else RdGayaNormal.setSelected(true);
            int mental = Integer.parseInt(tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString());
            if (mental==15) RdMentalLupa.setSelected(true); else RdMentalSadar.setSelected(true);
            hitungTotal();
            KdPetugas.setText(Sequel.cariIsi("select nip from skala_morse where no_rawat='"+
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()+"' and tanggal='"+
                tbObat.getValueAt(tbObat.getSelectedRow(),3).toString()+"' and skoring='"+sk+"'"));
            NmPetugas.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 13).toString());
        }
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date()); CmbSkoring.setSelectedIndex(0);
        RdJatuhTidak.setSelected(true); RdDiagnosaTidak.setSelected(true);
        RdAlatTidakAda.setSelected(true); RdInfusTidak.setSelected(true);
        RdGayaNormal.setSelected(true); RdMentalSadar.setSelected(true);
        KdPetugas.setText(""); NmPetugas.setText("");
        hitungTotal();
    }

    public void setNoRm(String norwt, String norm, String nama) { TNoRw.setText(norwt); TNoRM.setText(norm); TPasien.setText(nama); }

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
