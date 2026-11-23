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
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import widget.*;

/**
 * Penilaian Risiko Jatuh Pasien Geriatri
 * Ontario Modified Stratify - Sydney Scoring
 * RM.KEP.08.033.2019
 */
public class RMResikoJatuhGeriatriOms extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2, TglSkoring;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private Label LCount;
    private widget.Table tbData;
    private DefaultTableModel tabMode;
    private ScrollPane Scroll, ScrollInput;
    private CekBox ChkInput;
    private PanelBiasa FormInput;
    private JPanel PanelInput;

    // Jenis Skoring
    private ComboBox CmbJenisSkoring;

    // 1. Riwayat Jatuh
    private ComboBox CmbRiwayatRS, CmbRiwayat2Bln;
    private JLabel LSkorRiwayat;

    // 2. Status Mental
    private ComboBox CmbDelirium;
    private JLabel LSkorMental;

    // 3. Penglihatan
    private ComboBox CmbPenglihatanKacamata, CmbPenglihatanBuram, CmbPenglihatanPenyakit;
    private JLabel LSkorPenglihatan;

    // 4. Berkemih
    private ComboBox CmbBerkemihUrgensi, CmbBerkemihInkontinensia;
    private JLabel LSkorBerkemih;

    // 5. Transfer
    private ComboBox CmbTransfer;
    private JLabel LSkorTransfer;

    // 6. Mobilitas
    private ComboBox CmbMobilitas;
    private JLabel LSkorMobilitas;

    // Total
    private JLabel LTotalSkor, LLevelResiko;

    // Petugas
    private TextBox NIP, NamaPetugas;
    private Button BtnPetugas;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);

    public RMResikoJatuhGeriatriOms(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        java.awt.Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width, screen.height);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Penilaian Risiko Jatuh Geriatri - Ontario Modified Stratify (Sydney Scoring) - RM.KEP.08.033.2019 ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // === TOP PANEL ===
        panelisi panelAtas = new panelisi();
        panelAtas.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 4));
        panelAtas.setPreferredSize(new Dimension(1024, 33));
        Label lblNoRw = new Label(); lblNoRw.setText("No.Rawat :");
        TNoRw = new TextBox(); TNoRw.setPreferredSize(new Dimension(130, 23));
        TNoRM = new TextBox(); TNoRM.setPreferredSize(new Dimension(100, 23));
        TPasien = new TextBox(); TPasien.setPreferredSize(new Dimension(220, 23));
        panelAtas.add(lblNoRw); panelAtas.add(TNoRw); panelAtas.add(TNoRM); panelAtas.add(TPasien);

        // === LIST TABLE ===
        Scroll = new ScrollPane();
        tbData = new widget.Table();
        tabMode = new DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat","No.R.M.","Nama","Jenis Skoring","Tanggal",
                         "Riwayat","Mental","Penglihatan","Berkemih","Transfer","Mobilitas",
                         "Total Skor","Level Risiko","Petugas"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbData.setModel(tabMode);
        // Popup menu klik kanan pada tabel
        JPopupMenu popupTable = new JPopupMenu();
        JMenuItem mnuCetak = new JMenuItem("Cetak / Print");
        mnuCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnuCetak.addActionListener(e -> cetakReport());
        popupTable.add(mnuCetak);

        tbData.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (tabMode.getRowCount() != 0) getData();
            }
            @Override public void mousePressed(java.awt.event.MouseEvent e) {
                int row = tbData.rowAtPoint(e.getPoint());
                if (row >= 0 && row < tabMode.getRowCount()) tbData.setRowSelectionInterval(row, row);
                if (javax.swing.SwingUtilities.isRightMouseButton(e) && tabMode.getRowCount() != 0) {
                    getData();
                    popupTable.show(tbData, e.getX(), e.getY());
                }
            }
        });
        Scroll.setViewportView(tbData);

        // === FORM INPUT ===
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(960, 700));
        FormInput.setLayout(null);

        // Kolom: x1=pertanyaan, x2=jawaban combobox, x3=skor, x4=keterangan
        int x1 = 10, x2 = 315, x3 = 435, x4 = 508, x5 = 660;
        int cw = 110; // combobox width
        int ch = 26;  // combobox height
        int lh = 24;  // label height
        int y = 10;

        // Title
        JLabel hTitle = new JLabel("PENILAIAN RISIKO JATUH PASIEN GERIATRI  —  Ontario Modified Stratify - Sydney Scoring");
        hTitle.setFont(new Font("Tahoma", Font.BOLD, 13));
        hTitle.setBounds(x1, y, 750, lh); FormInput.add(hTitle);
        JLabel hCode = new JLabel("RM.KEP.08.033.2019");
        hCode.setFont(new Font("Tahoma", 0, 11));
        hCode.setBounds(800, y, 150, lh); FormInput.add(hCode);

        // Jenis Skoring & Tanggal
        y += 32;
        Label ljenis = new Label(); ljenis.setText("Jenis Skoring :"); ljenis.setBounds(x1, y, 110, ch); FormInput.add(ljenis);
        CmbJenisSkoring = new ComboBox();
        CmbJenisSkoring.setModel(new DefaultComboBoxModel(new String[]{"Saat Masuk", "Skoring 2", "Skoring 3"}));
        CmbJenisSkoring.setBounds(124, y, 130, ch); FormInput.add(CmbJenisSkoring);
        Label ltgl = new Label(); ltgl.setText("Tanggal :"); ltgl.setBounds(264, y, 70, ch); FormInput.add(ltgl);
        TglSkoring = new widget.Tanggal(); TglSkoring.setDisplayFormat("dd-MM-yyyy");
        TglSkoring.setBounds(336, y, 130, ch); FormInput.add(TglSkoring);

        // Column headers
        y += 34;
        JLabel hParam = new JLabel("Parameter & Pertanyaan");
        hParam.setFont(new Font("Tahoma", Font.BOLD, 14)); hParam.setForeground(new Color(0, 0, 120));
        hParam.setBounds(x1, y, x2 - x1 - 5, lh); FormInput.add(hParam);
        JLabel hJawaban = new JLabel("Jawaban");
        hJawaban.setFont(new Font("Tahoma", Font.BOLD, 14)); hJawaban.setForeground(new Color(0, 0, 120));
        hJawaban.setBounds(x2, y, cw, lh); FormInput.add(hJawaban);
        JLabel hSkor = new JLabel("SKOR");
        hSkor.setFont(new Font("Tahoma", Font.BOLD, 14)); hSkor.setForeground(new Color(0, 0, 120));
        hSkor.setBounds(x3, y, x4 - x3 - 5, lh); FormInput.add(hSkor);
        JLabel hKeterangan = new JLabel("Keterangan Skor");
        hKeterangan.setFont(new Font("Tahoma", Font.BOLD, 14)); hKeterangan.setForeground(new Color(0, 0, 120));
        hKeterangan.setBounds(x4, y, 430, lh); FormInput.add(hKeterangan);

        y += 20;

        // ================================================================
        // PARAMETER 1: RIWAYAT JATUH (0 / 6)
        // ================================================================
        y += 8;
        JLabel h1 = new JLabel("1. Riwayat Jatuh");
        h1.setFont(new Font("Tahoma", Font.BOLD, 14)); h1.setForeground(new Color(80, 0, 0));
        h1.setBounds(x1, y, x2 - x1 - 5, lh); FormInput.add(h1);
        LSkorRiwayat = buatLabelSkor("0", 14);
        LSkorRiwayat.setBounds(x3, y, x4 - x3 - 5, 56); FormInput.add(LSkorRiwayat);
        JLabel acuan1 = new JLabel("Ya (salah satu) = 6   |   Tidak keduanya = 0");
        acuan1.setFont(new Font("Tahoma", 0, 11)); acuan1.setBounds(x4, y, 430, lh); FormInput.add(acuan1);

        y += 28;
        JLabel q1a = new JLabel("  Datang ke RS karena jatuh?");
        q1a.setFont(new Font("Tahoma", 0, 12)); q1a.setBounds(x1, y, x2 - x1 - 5, lh); FormInput.add(q1a);
        CmbRiwayatRS = new ComboBox();
        CmbRiwayatRS.setModel(new DefaultComboBoxModel(new String[]{"Tidak", "Ya"}));
        CmbRiwayatRS.setBounds(x2, y, cw, ch); FormInput.add(CmbRiwayatRS);
        CmbRiwayatRS.addActionListener(e -> hitungSemua());

        y += 28;
        JLabel q1b = new JLabel("  Jatuh dalam 2 bulan terakhir?");
        q1b.setFont(new Font("Tahoma", 0, 12)); q1b.setBounds(x1, y, x2 - x1 - 5, lh); FormInput.add(q1b);
        CmbRiwayat2Bln = new ComboBox();
        CmbRiwayat2Bln.setModel(new DefaultComboBoxModel(new String[]{"Tidak", "Ya"}));
        CmbRiwayat2Bln.setBounds(x2, y, cw, ch); FormInput.add(CmbRiwayat2Bln);
        CmbRiwayat2Bln.addActionListener(e -> hitungSemua());

        y += 16;

        // ================================================================
        // PARAMETER 2: STATUS MENTAL (0 / 14)
        // ================================================================
        y += 8;
        JLabel h2 = new JLabel("2. Status Mental");
        h2.setFont(new Font("Tahoma", Font.BOLD, 14)); h2.setForeground(new Color(80, 0, 0));
        h2.setBounds(x1, y, x2 - x1 - 5, lh); FormInput.add(h2);
        LSkorMental = buatLabelSkor("0", 14);
        LSkorMental.setBounds(x3, y, x4 - x3 - 5, 28); FormInput.add(LSkorMental);
        JLabel acuan2 = new JLabel("Delirium = 14   |   Tidak = 0");
        acuan2.setFont(new Font("Tahoma", 0, 11)); acuan2.setBounds(x4, y, 430, lh); FormInput.add(acuan2);

        y += 28;
        JLabel q2 = new JLabel("  Delirium / kebingungan akut?");
        q2.setFont(new Font("Tahoma", 0, 12)); q2.setBounds(x1, y, x2 - x1 - 5, lh); FormInput.add(q2);
        CmbDelirium = new ComboBox();
        CmbDelirium.setModel(new DefaultComboBoxModel(new String[]{"Tidak", "Ya"}));
        CmbDelirium.setBounds(x2, y, cw, ch); FormInput.add(CmbDelirium);
        CmbDelirium.addActionListener(e -> hitungSemua());
        JLabel q2info = new JLabel("(tidak sadar posisi, gelisah, kognitif terganggu)");
        q2info.setFont(new Font("Tahoma", Font.ITALIC, 11)); q2info.setForeground(new Color(90,90,90));
        q2info.setBounds(x2 + cw + 5, y, 370, lh); FormInput.add(q2info);

        y += 16;

        // ================================================================
        // PARAMETER 3: PENGLIHATAN (0 / 1)
        // ================================================================
        y += 8;
        JLabel h3 = new JLabel("3. Penglihatan");
        h3.setFont(new Font("Tahoma", Font.BOLD, 14)); h3.setForeground(new Color(80, 0, 0));
        h3.setBounds(x1, y, x2 - x1 - 5, lh); FormInput.add(h3);
        LSkorPenglihatan = buatLabelSkor("0", 14);
        LSkorPenglihatan.setBounds(x3, y, x4 - x3 - 5, 84); FormInput.add(LSkorPenglihatan);
        JLabel acuan3 = new JLabel("Ya (salah satu) = 1   |   Tidak ketiganya = 0");
        acuan3.setFont(new Font("Tahoma", 0, 11)); acuan3.setBounds(x4, y, 430, lh); FormInput.add(acuan3);

        y += 28;
        JLabel q3a = new JLabel("  Memerlukan kacamata / lensa kontak?");
        q3a.setFont(new Font("Tahoma", 0, 12)); q3a.setBounds(x1, y, x2 - x1 - 5, lh); FormInput.add(q3a);
        CmbPenglihatanKacamata = new ComboBox();
        CmbPenglihatanKacamata.setModel(new DefaultComboBoxModel(new String[]{"Tidak", "Ya"}));
        CmbPenglihatanKacamata.setBounds(x2, y, cw, ch); FormInput.add(CmbPenglihatanKacamata);
        CmbPenglihatanKacamata.addActionListener(e -> hitungSemua());

        y += 28;
        JLabel q3b = new JLabel("  Mengeluh penglihatan buram?");
        q3b.setFont(new Font("Tahoma", 0, 12)); q3b.setBounds(x1, y, x2 - x1 - 5, lh); FormInput.add(q3b);
        CmbPenglihatanBuram = new ComboBox();
        CmbPenglihatanBuram.setModel(new DefaultComboBoxModel(new String[]{"Tidak", "Ya"}));
        CmbPenglihatanBuram.setBounds(x2, y, cw, ch); FormInput.add(CmbPenglihatanBuram);
        CmbPenglihatanBuram.addActionListener(e -> hitungSemua());

        y += 28;
        JLabel q3c = new JLabel("  Memiliki glaukoma / katarak / degenerasi makular?");
        q3c.setFont(new Font("Tahoma", 0, 12)); q3c.setBounds(x1, y, x2 - x1 - 5, lh); FormInput.add(q3c);
        CmbPenglihatanPenyakit = new ComboBox();
        CmbPenglihatanPenyakit.setModel(new DefaultComboBoxModel(new String[]{"Tidak", "Ya"}));
        CmbPenglihatanPenyakit.setBounds(x2, y, cw, ch); FormInput.add(CmbPenglihatanPenyakit);
        CmbPenglihatanPenyakit.addActionListener(e -> hitungSemua());

        y += 16;

        // ================================================================
        // PARAMETER 4: BERKEMIH (0 / 2)
        // ================================================================
        y += 8;
        JLabel h4 = new JLabel("4. Berkemih");
        h4.setFont(new Font("Tahoma", Font.BOLD, 14)); h4.setForeground(new Color(80, 0, 0));
        h4.setBounds(x1, y, x2 - x1 - 5, lh); FormInput.add(h4);
        LSkorBerkemih = buatLabelSkor("0", 14);
        LSkorBerkemih.setBounds(x3, y, x4 - x3 - 5, 56); FormInput.add(LSkorBerkemih);
        JLabel acuan4 = new JLabel("Ya (salah satu) = 2   |   Tidak keduanya = 0");
        acuan4.setFont(new Font("Tahoma", 0, 11)); acuan4.setBounds(x4, y, 430, lh); FormInput.add(acuan4);

        y += 28;
        JLabel q4a = new JLabel("  Urgensi berkemih (tidak dapat menahan)?");
        q4a.setFont(new Font("Tahoma", 0, 12)); q4a.setBounds(x1, y, x2 - x1 - 5, lh); FormInput.add(q4a);
        CmbBerkemihUrgensi = new ComboBox();
        CmbBerkemihUrgensi.setModel(new DefaultComboBoxModel(new String[]{"Tidak", "Ya"}));
        CmbBerkemihUrgensi.setBounds(x2, y, cw, ch); FormInput.add(CmbBerkemihUrgensi);
        CmbBerkemihUrgensi.addActionListener(e -> hitungSemua());

        y += 28;
        JLabel q4b = new JLabel("  Inkontinensia urin?");
        q4b.setFont(new Font("Tahoma", 0, 12)); q4b.setBounds(x1, y, x2 - x1 - 5, lh); FormInput.add(q4b);
        CmbBerkemihInkontinensia = new ComboBox();
        CmbBerkemihInkontinensia.setModel(new DefaultComboBoxModel(new String[]{"Tidak", "Ya"}));
        CmbBerkemihInkontinensia.setBounds(x2, y, cw, ch); FormInput.add(CmbBerkemihInkontinensia);
        CmbBerkemihInkontinensia.addActionListener(e -> hitungSemua());

        y += 16;

        // ================================================================
        // PARAMETER 5: TRANSFER (0-3)
        // ================================================================
        y += 8;
        JLabel h5 = new JLabel("5. Transfer (pindah dari tempat tidur ke kursi)");
        h5.setFont(new Font("Tahoma", Font.BOLD, 14)); h5.setForeground(new Color(80, 0, 0));
        h5.setBounds(x1, y, x2 + cw, lh); FormInput.add(h5);
        LSkorTransfer = buatLabelSkor("0", 14);
        LSkorTransfer.setBounds(x3, y, x4 - x3 - 5, ch); FormInput.add(LSkorTransfer);
        JLabel acuan5 = new JLabel("0=Mandiri  |  1=Alat bantu  |  2=Bantuan 1 org  |  3=Tidak mampu/≥2 org");
        acuan5.setFont(new Font("Tahoma", 0, 11)); acuan5.setBounds(x4, y, 430, lh); FormInput.add(acuan5);

        y += 30;
        CmbTransfer = new ComboBox();
        CmbTransfer.setModel(new DefaultComboBoxModel(new String[]{
            "0 - Mandiri (tidak perlu alat bantu/bantuan orang lain)",
            "1 - Menggunakan alat bantu berjalan",
            "2 - Perlu bantuan/pengawasan 1 orang",
            "3 - Tidak mampu / perlu bantuan ≥2 orang"
        }));
        CmbTransfer.setBounds(x1, y, 560, ch); FormInput.add(CmbTransfer);
        CmbTransfer.addActionListener(e -> hitungSemua());

        y += 16;

        // ================================================================
        // PARAMETER 6: MOBILITAS (0-4)
        // ================================================================
        y += 8;
        JLabel h6 = new JLabel("6. Mobilitas / Kemampuan Berjalan");
        h6.setFont(new Font("Tahoma", Font.BOLD, 14)); h6.setForeground(new Color(80, 0, 0));
        h6.setBounds(x1, y, x2 + cw, lh); FormInput.add(h6);
        LSkorMobilitas = buatLabelSkor("0", 14);
        LSkorMobilitas.setBounds(x3, y, x4 - x3 - 5, ch); FormInput.add(LSkorMobilitas);
        JLabel acuan6 = new JLabel("0=Mandiri  |  1=Lambat mandiri  |  2=Alat bantu  |  3=Bantuan orang  |  4=Imobilisasi");
        acuan6.setFont(new Font("Tahoma", 0, 11)); acuan6.setBounds(x4, y, 430, lh); FormInput.add(acuan6);

        y += 30;
        CmbMobilitas = new ComboBox();
        CmbMobilitas.setModel(new DefaultComboBoxModel(new String[]{
            "0 - Mandiri / normal (tanpa masalah)",
            "1 - Berjalan lambat namun mandiri",
            "2 - Membutuhkan alat bantu berjalan (tripod/walker)",
            "3 - Membutuhkan bantuan/pengawasan orang lain",
            "4 - Imobilisasi / bedrest"
        }));
        CmbMobilitas.setBounds(x1, y, 560, ch); FormInput.add(CmbMobilitas);
        CmbMobilitas.addActionListener(e -> hitungSemua());

        y += 24;

        // ================================================================
        // TOTAL SKOR & LEVEL RISIKO
        // ================================================================
        y += 12;
        JLabel lTotalLabel = new JLabel("TOTAL SKOR :");
        lTotalLabel.setFont(new Font("Tahoma", Font.BOLD, 14)); lTotalLabel.setBounds(x1, y, 150, 32); FormInput.add(lTotalLabel);
        LTotalSkor = new JLabel("0");
        LTotalSkor.setFont(new Font("Tahoma", Font.BOLD, 20));
        LTotalSkor.setOpaque(true); LTotalSkor.setBackground(new Color(220, 255, 220));
        LTotalSkor.setForeground(new Color(0, 80, 0));
        LTotalSkor.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 0)));
        LTotalSkor.setHorizontalAlignment(SwingConstants.CENTER);
        LTotalSkor.setBounds(162, y, 60, 32); FormInput.add(LTotalSkor);

        JLabel lLevelLabel = new JLabel("Level Risiko :");
        lLevelLabel.setFont(new Font("Tahoma", Font.BOLD, 14)); lLevelLabel.setBounds(234, y, 115, 32); FormInput.add(lLevelLabel);
        LLevelResiko = new JLabel(" — ");
        LLevelResiko.setFont(new Font("Tahoma", Font.BOLD, 14));
        LLevelResiko.setOpaque(true); LLevelResiko.setBackground(new Color(220, 255, 220));
        LLevelResiko.setForeground(new Color(0, 100, 0));
        LLevelResiko.setBorder(BorderFactory.createLineBorder(new Color(0, 150, 0)));
        LLevelResiko.setHorizontalAlignment(SwingConstants.CENTER);
        LLevelResiko.setBounds(352, y, 220, 32); FormInput.add(LLevelResiko);

        // Keterangan level risiko
        y += 38;
        JLabel hLevel = new JLabel("Keterangan :  0-5 = Risiko Rendah   |   6-16 = Risiko Sedang   |   17-30 = Risiko Tinggi");
        hLevel.setFont(new Font("Tahoma", Font.BOLD, 11)); hLevel.setForeground(new Color(60, 60, 60));
        hLevel.setBounds(x1, y, 620, 22); FormInput.add(hLevel);

        // Skor max referensi
        JLabel hMaxRef = new JLabel("(Maks: Riwayat=6, Mental=14, Penglihatan=1, Berkemih=2, Transfer+Mobilitas=7  →  Total maks=30)");
        hMaxRef.setFont(new Font("Tahoma", 0, 10)); hMaxRef.setForeground(new Color(100, 100, 100));
        hMaxRef.setBounds(x1, y + 18, 720, 18); FormInput.add(hMaxRef);

        // ================================================================
        // PETUGAS
        // ================================================================
        y += 44;
        Label lnip = new Label(); lnip.setText("Paraf & Nama Petugas :"); lnip.setBounds(x1, y, 165, ch); FormInput.add(lnip);
        NIP = new TextBox(); NIP.setBounds(178, y, 90, ch); FormInput.add(NIP);
        NamaPetugas = new TextBox(); NamaPetugas.setBounds(272, y, 240, ch); FormInput.add(NamaPetugas);
        BtnPetugas = new Button();
        BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPetugas.setBounds(516, y, 28, ch); FormInput.add(BtnPetugas);
        BtnPetugas.addActionListener(ev -> {
            petugasDlg.emptTeks(); petugasDlg.isCek();
            petugasDlg.setSize(800, 500); petugasDlg.setLocationRelativeTo(null);
            petugasDlg.setVisible(true);
        });
        petugasDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (petugasDlg.getTable().getSelectedRow() != -1) {
                    NIP.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(), 0).toString());
                    NamaPetugas.setText(petugasDlg.getTable().getValueAt(petugasDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

        // ================================================================
        // ChkInput toggle & layout assembly
        // ================================================================
        ChkInput = new CekBox();
        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setText(".: Input Data"); ChkInput.setSelected(true); ChkInput.setBorderPainted(true);
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.addActionListener(ev -> isForm());

        ScrollInput = new ScrollPane(); ScrollInput.setViewportView(FormInput);
        ScrollInput.setPreferredSize(new Dimension(960, 520));
        ScrollInput.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        ScrollInput.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        PanelInput = new JPanel(new BorderLayout()); PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new Dimension(WIDTH, 540));
        PanelInput.add(ScrollInput, BorderLayout.CENTER); PanelInput.add(ChkInput, BorderLayout.PAGE_END);

        // === BOTTOM BUTTONS ===
        panelBawah = new panelisi(); panelBawah.setPreferredSize(new Dimension(44, 54));
        panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));
        BtnSimpan = new Button(); BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); BtnSimpan.setText("Simpan"); BtnSimpan.setPreferredSize(new Dimension(100, 30));
        BtnBatal = new Button(); BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); BtnBatal.setText("Baru"); BtnBatal.setPreferredSize(new Dimension(100, 30));
        BtnHapus = new Button(); BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); BtnHapus.setText("Hapus"); BtnHapus.setPreferredSize(new Dimension(100, 30));
        BtnEdit = new Button(); BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); BtnEdit.setText("Ganti"); BtnEdit.setPreferredSize(new Dimension(100, 30));
        BtnPrint = new Button(); BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); BtnPrint.setText("Cetak"); BtnPrint.setPreferredSize(new Dimension(100, 30));
        BtnKeluar = new Button(); BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); BtnKeluar.setText("Keluar"); BtnKeluar.setPreferredSize(new Dimension(100, 30));
        Label lc = new Label(); lc.setText("Tanggal :"); lc.setPreferredSize(new Dimension(55, 23));
        DTPCari1 = new widget.Tanggal(); DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setPreferredSize(new Dimension(90, 23));
        Label sd = new Label(); sd.setText("s.d."); sd.setPreferredSize(new Dimension(25, 23));
        DTPCari2 = new widget.Tanggal(); DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setPreferredSize(new Dimension(90, 23));
        Label lk = new Label(); lk.setText("Key Word :"); lk.setPreferredSize(new Dimension(70, 23));
        TCari = new TextBox(); TCari.setPreferredSize(new Dimension(150, 23));
        BtnCari = new Button(); BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnAll = new Button(); BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); BtnAll.setPreferredSize(new Dimension(28, 23));
        LCount = new Label(); LCount.setText("Record : 0"); LCount.setPreferredSize(new Dimension(100, 23));

        panelBawah.add(BtnSimpan); panelBawah.add(BtnBatal); panelBawah.add(BtnHapus);
        panelBawah.add(BtnEdit); panelBawah.add(BtnPrint); panelBawah.add(BtnKeluar);
        panelBawah.add(lc); panelBawah.add(DTPCari1); panelBawah.add(sd); panelBawah.add(DTPCari2);
        panelBawah.add(lk); panelBawah.add(TCari); panelBawah.add(BtnCari); panelBawah.add(BtnAll);
        panelBawah.add(LCount);

        // Button actions
        BtnKeluar.addActionListener(ev -> dispose());
        BtnBatal.addActionListener(ev -> emptTeks());
        BtnCari.addActionListener(ev -> tampil());
        BtnAll.addActionListener(ev -> { TCari.setText(""); tampil(); });
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) tampil();
            }
        });

        BtnSimpan.addActionListener(ev -> {
            if (TNoRw.getText().trim().isEmpty()) { Valid.textKosong(TNoRw, "Pasien"); return; }
            if (NIP.getText().trim().isEmpty()) { Valid.textKosong(NIP, "Petugas"); return; }
            String tgl = Valid.SetTgl(TglSkoring.getSelectedItem() + "");
            String jenis = CmbJenisSkoring.getSelectedItem().toString();
            int sRiwayat = hitungSkorRiwayat();
            int sMental = hitungSkorMental();
            int sPenglihatan = hitungSkorPenglihatan();
            int sBerkemih = hitungSkorBerkemih();
            int transfer = CmbTransfer.getSelectedIndex();
            int mobilitas = CmbMobilitas.getSelectedIndex();
            int total = sRiwayat + sMental + sPenglihatan + sBerkemih + transfer + mobilitas;
            String level = hitungLevel(total);
            if (Sequel.menyimpantf("resiko_jatuh_geriatri_oms",
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?",
                "Data", 23, new String[]{
                    null, TNoRw.getText(), jenis, tgl,
                    CmbRiwayatRS.getSelectedItem().toString(),
                    CmbRiwayat2Bln.getSelectedItem().toString(),
                    String.valueOf(sRiwayat),
                    CmbDelirium.getSelectedItem().toString(),
                    String.valueOf(sMental),
                    CmbPenglihatanKacamata.getSelectedItem().toString(),
                    CmbPenglihatanBuram.getSelectedItem().toString(),
                    CmbPenglihatanPenyakit.getSelectedItem().toString(),
                    String.valueOf(sPenglihatan),
                    CmbBerkemihUrgensi.getSelectedItem().toString(),
                    CmbBerkemihInkontinensia.getSelectedItem().toString(),
                    String.valueOf(sBerkemih),
                    String.valueOf(transfer), String.valueOf(transfer),
                    String.valueOf(mobilitas), String.valueOf(mobilitas),
                    String.valueOf(total), level, NIP.getText()
                })) { tampil(); emptTeks(); }
        });

        BtnHapus.addActionListener(ev -> {
            if (tbData.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih data dulu...!!!!"); return; }
            int row = tbData.getSelectedRow();
            String nr = tabMode.getValueAt(row, 0).toString();
            String jenis = tabMode.getValueAt(row, 3).toString();
            if (JOptionPane.showConfirmDialog(null, "Hapus data risiko jatuh '" + jenis + "'?",
                    "Konfirmasi", JOptionPane.YES_NO_OPTION) == 0) {
                Sequel.queryu2tf("delete from resiko_jatuh_geriatri_oms where no_rawat=? and jenis_skoring=?",
                    2, new String[]{nr, jenis});
                tampil(); emptTeks();
            }
        });

        BtnEdit.addActionListener(ev -> {
            if (TNoRw.getText().trim().isEmpty() || tbData.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Pilih data dulu...!!!!");
                return;
            }
            int row = tbData.getSelectedRow();
            String nr = tabMode.getValueAt(row, 0).toString();
            String jenis = tabMode.getValueAt(row, 3).toString();
            Sequel.queryu2tf("delete from resiko_jatuh_geriatri_oms where no_rawat=? and jenis_skoring=?",
                2, new String[]{nr, jenis});
            BtnSimpan.doClick();
        });

        BtnPrint.addActionListener(ev -> cetakReport());

        // Final layout
        InternalFrame ifd = new InternalFrame(); ifd.setLayout(new BorderLayout()); ifd.setBackground(Color.WHITE);
        ifd.add(PanelInput, BorderLayout.NORTH); ifd.add(Scroll, BorderLayout.CENTER);
        internalFrame1.add(panelAtas, BorderLayout.PAGE_START);
        internalFrame1.add(ifd, BorderLayout.CENTER);
        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);
        setLayout(new BorderLayout()); add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); setUndecorated(true);

        hitungSemua();
    }

    // ============================
    //  LOGIKA SKOR
    // ============================

    private int hitungSkorRiwayat() {
        boolean a = CmbRiwayatRS.getSelectedItem().toString().equals("Ya");
        boolean b = CmbRiwayat2Bln.getSelectedItem().toString().equals("Ya");
        return (a || b) ? 6 : 0;
    }

    private int hitungSkorMental() {
        return CmbDelirium.getSelectedItem().toString().equals("Ya") ? 14 : 0;
    }

    private int hitungSkorPenglihatan() {
        boolean a = CmbPenglihatanKacamata.getSelectedItem().toString().equals("Ya");
        boolean b = CmbPenglihatanBuram.getSelectedItem().toString().equals("Ya");
        boolean c = CmbPenglihatanPenyakit.getSelectedItem().toString().equals("Ya");
        return (a || b || c) ? 1 : 0;
    }

    private int hitungSkorBerkemih() {
        boolean a = CmbBerkemihUrgensi.getSelectedItem().toString().equals("Ya");
        boolean b = CmbBerkemihInkontinensia.getSelectedItem().toString().equals("Ya");
        return (a || b) ? 2 : 0;
    }

    private String hitungLevel(int total) {
        if (total >= 17) return "Risiko Tinggi";
        if (total >= 6)  return "Risiko Sedang";
        return "Risiko Rendah";
    }

    private int hitungSemua() {
        int sRiwayat     = hitungSkorRiwayat();
        int sMental      = hitungSkorMental();
        int sPenglihatan = hitungSkorPenglihatan();
        int sBerkemih    = hitungSkorBerkemih();
        int sTransfer    = CmbTransfer.getSelectedIndex();
        int sMobilitas   = CmbMobilitas.getSelectedIndex();

        setSkorLabel(LSkorRiwayat, sRiwayat, 6, 14);
        setSkorLabel(LSkorMental, sMental, 14, 14);
        setSkorLabel(LSkorPenglihatan, sPenglihatan, 1, 14);
        setSkorLabel(LSkorBerkemih, sBerkemih, 2, 14);
        setSkorLabel(LSkorTransfer, sTransfer, 3, 14);
        setSkorLabel(LSkorMobilitas, sMobilitas, 4, 14);

        int total = sRiwayat + sMental + sPenglihatan + sBerkemih + sTransfer + sMobilitas;
        LTotalSkor.setText(String.valueOf(total));

        String level = hitungLevel(total);
        LLevelResiko.setText("  " + level + "  ");

        if (total >= 17) {
            LTotalSkor.setBackground(new Color(255, 80, 80)); LTotalSkor.setForeground(Color.WHITE);
            LLevelResiko.setBackground(new Color(255, 80, 80)); LLevelResiko.setForeground(Color.WHITE);
        } else if (total >= 6) {
            LTotalSkor.setBackground(new Color(255, 200, 80)); LTotalSkor.setForeground(Color.BLACK);
            LLevelResiko.setBackground(new Color(255, 200, 80)); LLevelResiko.setForeground(Color.BLACK);
        } else {
            LTotalSkor.setBackground(new Color(200, 255, 200)); LTotalSkor.setForeground(new Color(0, 80, 0));
            LLevelResiko.setBackground(new Color(200, 255, 200)); LLevelResiko.setForeground(new Color(0, 80, 0));
        }
        return total;
    }

    /** Buat JLabel skor dengan ukuran berbeda */
    private void cetakReport() {
        if (TNoRw.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(null, "Pilih data dulu...!!!!"); return; }
        java.util.Map<String, Object> p = new java.util.HashMap<>();
        p.put("namars", akses.getnamars()); p.put("alamatrs", akses.getalamatrs());
        p.put("kotars", akses.getkabupatenrs()); p.put("propinsirs", akses.getpropinsirs());
        p.put("kontakrs", akses.getkontakrs()); p.put("emailrs", akses.getemailrs());
        p.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        p.put("no_rawat", TNoRw.getText());
        Valid.MyReportqry("rptRisikoJatuhGeriatriOms.jasper", "report",
            "::[ Risiko Jatuh Geriatri - Ontario Modified Stratify ]::",
            "select r.no_rawat,rp.no_rkm_medis,p.nm_pasien,p.tgl_lahir,p.jk," +
            "r.jenis_skoring,r.tanggal," +
            "r.riwayat_jatuh_rs,r.riwayat_jatuh_2bln,r.skor_riwayat," +
            "r.status_delirium,r.skor_mental," +
            "r.penglihatan_kacamata,r.penglihatan_buram,r.penglihatan_penyakit,r.skor_penglihatan," +
            "r.berkemih_urgensi,r.berkemih_inkontinensia,r.skor_berkemih," +
            "r.transfer,r.skor_transfer,r.mobilitas,r.skor_mobilitas," +
            "r.total_skor,r.level_resiko,pt.nama as nm_petugas " +
            "from resiko_jatuh_geriatri_oms r " +
            "inner join reg_periksa rp on r.no_rawat=rp.no_rawat " +
            "inner join pasien p on rp.no_rkm_medis=p.no_rkm_medis " +
            "left join petugas pt on r.nip=pt.nip " +
            "where r.no_rawat='" + TNoRw.getText() + "' order by r.id", p);
    }

    private JLabel buatLabelSkor(String val, int fontSize) {
        JLabel lbl = new JLabel(val, SwingConstants.CENTER);
        lbl.setFont(new Font("Tahoma", Font.BOLD, fontSize));
        lbl.setOpaque(true); lbl.setBackground(new Color(200, 255, 200));
        lbl.setForeground(new Color(0, 80, 0));
        lbl.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 0)));
        return lbl;
    }

    private void setSkorLabel(JLabel lbl, int skor, int maxSkor, int fontSize) {
        lbl.setText(String.valueOf(skor));
        if (skor == 0) {
            lbl.setBackground(new Color(200, 255, 200)); lbl.setForeground(new Color(0, 80, 0));
            lbl.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 0)));
        } else if (skor < maxSkor) {
            lbl.setBackground(new Color(255, 200, 80)); lbl.setForeground(Color.BLACK);
            lbl.setBorder(BorderFactory.createLineBorder(new Color(180, 120, 0)));
        } else {
            lbl.setBackground(new Color(255, 80, 80)); lbl.setForeground(Color.WHITE);
            lbl.setBorder(BorderFactory.createLineBorder(new Color(180, 0, 0)));
        }
    }

    private void isForm() {
        if (ChkInput.isSelected()) {
            ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 540));
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
                "select r.no_rawat, rp.no_rkm_medis, p.nm_pasien, r.jenis_skoring, r.tanggal," +
                "r.skor_riwayat, r.skor_mental, r.skor_penglihatan, r.skor_berkemih," +
                "r.transfer, r.mobilitas, r.total_skor, r.level_resiko," +
                "pt.nama as nm_petugas " +
                "from resiko_jatuh_geriatri_oms r " +
                "inner join reg_periksa rp on r.no_rawat=rp.no_rawat " +
                "inner join pasien p on rp.no_rkm_medis=p.no_rkm_medis " +
                "left join petugas pt on r.nip=pt.nip " +
                "where r.tanggal between ? and ? " +
                "and (r.no_rawat like ? or rp.no_rkm_medis like ? or p.nm_pasien like ?) " +
                "order by r.tanggal desc, r.id desc");
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
            ps.setString(3, "%" + TCari.getText().trim() + "%");
            ps.setString(4, "%" + TCari.getText().trim() + "%");
            ps.setString(5, "%" + TCari.getText().trim() + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5),
                    rs.getString(6), rs.getString(7), rs.getString(8),
                    rs.getString(9), rs.getString(10), rs.getString(11),
                    rs.getString(12), rs.getString(13), rs.getString(14)
                });
            }
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif tampil RisikoJatuhGeriatri: " + e); }
        finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void getData() {
        if (tbData.getSelectedRow() == -1) return;
        int row = tbData.getSelectedRow();
        TNoRw.setText(tabMode.getValueAt(row, 0).toString());
        TNoRM.setText(tabMode.getValueAt(row, 1).toString());
        TPasien.setText(tabMode.getValueAt(row, 2).toString());

        try {
            ps = koneksi.prepareStatement(
                "select * from resiko_jatuh_geriatri_oms r " +
                "left join petugas pt on r.nip=pt.nip " +
                "where r.no_rawat=? and r.jenis_skoring=?");
            ps.setString(1, tabMode.getValueAt(row, 0).toString());
            ps.setString(2, tabMode.getValueAt(row, 3).toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                try { TglSkoring.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("tanggal"))); } catch (Exception ex) {}
                if (rs.getString("jenis_skoring") != null) CmbJenisSkoring.setSelectedItem(rs.getString("jenis_skoring"));
                setCombo(CmbRiwayatRS, rs.getString("riwayat_jatuh_rs"));
                setCombo(CmbRiwayat2Bln, rs.getString("riwayat_jatuh_2bln"));
                setCombo(CmbDelirium, rs.getString("status_delirium"));
                setCombo(CmbPenglihatanKacamata, rs.getString("penglihatan_kacamata"));
                setCombo(CmbPenglihatanBuram, rs.getString("penglihatan_buram"));
                setCombo(CmbPenglihatanPenyakit, rs.getString("penglihatan_penyakit"));
                setCombo(CmbBerkemihUrgensi, rs.getString("berkemih_urgensi"));
                setCombo(CmbBerkemihInkontinensia, rs.getString("berkemih_inkontinensia"));
                int transferVal = rs.getInt("transfer"); if (transferVal >= 0 && transferVal <= 3) CmbTransfer.setSelectedIndex(transferVal);
                int mobilitasVal = rs.getInt("mobilitas"); if (mobilitasVal >= 0 && mobilitasVal <= 4) CmbMobilitas.setSelectedIndex(mobilitasVal);
                NIP.setText(rs.getString("nip") == null ? "" : rs.getString("nip"));
                NamaPetugas.setText(rs.getString("nama") == null ? "" : rs.getString("nama"));
                hitungSemua();
            }
        } catch (Exception e) { System.out.println("Notif getData RisikoJatuhGeriatri: " + e); }
        finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    private void setCombo(ComboBox cmb, String val) {
        if (val != null) cmb.setSelectedItem(val);
    }

    public void emptTeks() {
        TglSkoring.setDate(new Date());
        CmbJenisSkoring.setSelectedIndex(0);
        CmbRiwayatRS.setSelectedIndex(0); CmbRiwayat2Bln.setSelectedIndex(0);
        CmbDelirium.setSelectedIndex(0);
        CmbPenglihatanKacamata.setSelectedIndex(0); CmbPenglihatanBuram.setSelectedIndex(0);
        CmbPenglihatanPenyakit.setSelectedIndex(0);
        CmbBerkemihUrgensi.setSelectedIndex(0); CmbBerkemihInkontinensia.setSelectedIndex(0);
        CmbTransfer.setSelectedIndex(0); CmbMobilitas.setSelectedIndex(0);
        NIP.setText(""); NamaPetugas.setText("");
        hitungSemua();
    }

    public void setNoRm(String n, String m, String p) {
        TNoRw.setText(n); TNoRM.setText(m); TPasien.setText(p);
    }

    public void setNoRm(String n, Date t) {
        TNoRw.setText(n);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", n));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
        if (t != null) TglSkoring.setDate(t);
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getrujukan_keluar());
        BtnHapus.setEnabled(akses.getrujukan_keluar());
        BtnEdit.setEnabled(akses.getrujukan_keluar());
        BtnPrint.setEnabled(akses.getrujukan_keluar());
    }
}
