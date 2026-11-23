package rekammedis;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import fungsi.WarnaTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import inventory.DlgCariMasterObat;
import kepegawaian.DlgCariDokter;
import kepegawaian.DlgCariPetugas;
import widget.*;

public class RMInstruksiMedisFarmaRscb extends JDialog {

    private InternalFrame internalFrame1;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private TextBox TRuangRawat, TDokter, TAlergi, TJK, TTglLahir;
    private Label LCount;
    private widget.Tanggal DTPCari1, DTPCari2;

    // Input fields
    private TextBox KdPetugas, NmPetugas;
    private TextBox TKodeDokter, TNamaDokter, TNamaObat, TKekuatan, TBentukSediaan, TAturanPakai, TRutePemberian, TKodeAB;
    private ComboBox CmbSection, CmbFilterSection;
    private CekBox chkHighAlert, chkPRN, ChkStop;
    private widget.Tanggal TglMulai, TglStop;

    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;

    private widget.Table tbMedikasi;
    private DefaultTableModel tabModeMedikasi;
    private ScrollPane Scroll2;
    private JPanel PanelInputMedikasi;
    private CekBox ChkInputMedikasi;
    private ScrollPane scrollPaneMedikasi;
    private panelisi panelGlassMedikasi;
    public widget.editorpane LoadHTML;

    private static final String[] JAM_LABELS = {"07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","01","02","03","04","05","06"};

    public RMInstruksiMedisFarmaRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(1200, 700);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Kardex Obat - Instruksi Medis Farmakologis ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // === PANEL ATAS ===
        panelisi panelAtas = new panelisi();
        panelAtas.setLayout(null);
        panelAtas.setPreferredSize(new Dimension(1200, 55));

        int x = 5, y0 = 3, y1 = 28, h = 23;
        Label lblNoRw = new Label(); lblNoRw.setText("No.Rawat :"); lblNoRw.setBounds(x, y0, 58, h); panelAtas.add(lblNoRw);
        x += 60; TNoRw = new TextBox(); TNoRw.setBounds(x, y0, 120, h); panelAtas.add(TNoRw);
        x += 122; TNoRM = new TextBox(); TNoRM.setBounds(x, y0, 65, h); panelAtas.add(TNoRM);
        x += 67; TPasien = new TextBox(); TPasien.setBounds(x, y0, 140, h); panelAtas.add(TPasien);
        x += 145; Label lblJK = new Label(); lblJK.setText("JK:"); lblJK.setBounds(x, y0, 18, h); panelAtas.add(lblJK);
        x += 19; TJK = new TextBox(); TJK.setEditable(false); TJK.setBounds(x, y0, 65, h); panelAtas.add(TJK);
        x += 68; Label lblTL = new Label(); lblTL.setText("Lahir:"); lblTL.setBounds(x, y0, 30, h); panelAtas.add(lblTL);
        x += 32; TTglLahir = new TextBox(); TTglLahir.setEditable(false); TTglLahir.setBounds(x, y0, 75, h); panelAtas.add(TTglLahir);
        x += 78; Label lblAl = new Label(); lblAl.setText("Alergi:"); lblAl.setBounds(x, y0, 36, h); panelAtas.add(lblAl);
        x += 38; TAlergi = new TextBox(); TAlergi.setEditable(false); TAlergi.setBounds(x, y0, 200, h); panelAtas.add(TAlergi);

        x = 5;
        Label lblR = new Label(); lblR.setText("Ruang:"); lblR.setBounds(x, y1, 35, h); panelAtas.add(lblR);
        x += 37; TRuangRawat = new TextBox(); TRuangRawat.setEditable(false); TRuangRawat.setBounds(x, y1, 110, h); panelAtas.add(TRuangRawat);
        x += 115; Label lblD = new Label(); lblD.setText("DPJP:"); lblD.setBounds(x, y1, 30, h); panelAtas.add(lblD);
        x += 32; TDokter = new TextBox(); TDokter.setEditable(false); TDokter.setBounds(x, y1, 150, h); panelAtas.add(TDokter);
        x += 158; Label lblPtgs = new Label(); lblPtgs.setText("Petugas:"); lblPtgs.setBounds(x, y1, 48, h); panelAtas.add(lblPtgs);
        x += 50; KdPetugas = new TextBox(); KdPetugas.setBounds(x, y1, 70, h); panelAtas.add(KdPetugas);
        x += 72; NmPetugas = new TextBox(); NmPetugas.setEditable(false); NmPetugas.setBounds(x, y1, 150, h); panelAtas.add(NmPetugas);
        x += 152;
        Button btnCariPetugas = new Button();
        btnCariPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnCariPetugas.setBounds(x, y1, 28, h); panelAtas.add(btnCariPetugas);
        DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);
        btnCariPetugas.addActionListener(ev -> {
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

        // === INPUT PANEL (style ICU) ===
        InternalFrame internalFrame4 = new InternalFrame();
        internalFrame4.setBorder(null);
        internalFrame4.setLayout(new BorderLayout(1, 1));

        Scroll2 = new ScrollPane();
        tbMedikasi = new widget.Table();

        // Kolom: No.Rawat, Tgl.Obs, No, Dokter, NamaObat, Kekuatan, BentukSediaan, AturanPakai, Rute, KodeAB, Section, TglMulai, TglStop, Verifikasi, jam07..jam06(24)
        String[] colNames = new String[38];
        colNames[0] = "No.Rawat"; colNames[1] = "Tgl.Obs"; colNames[2] = "No";
        colNames[3] = "Dokter"; colNames[4] = "Nama Obat"; colNames[5] = "Kekuatan";
        colNames[6] = "Bentuk Sedi..."; colNames[7] = "Aturan Pakai"; colNames[8] = "Rute";
        colNames[9] = "Kode AB"; colNames[10] = "Section"; colNames[11] = "Tgl Mulai";
        colNames[12] = "Tgl Stop"; colNames[13] = "Verifikasi";
        for (int j = 0; j < JAM_LABELS.length; j++) colNames[14 + j] = JAM_LABELS[j];

        tabModeMedikasi = new DefaultTableModel(null, colNames) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbMedikasi.setModel(tabModeMedikasi);
        tbMedikasi.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] cw = {105, 75, 25, 100, 130, 55, 60, 80, 45, 35, 65, 72, 72, 80};
        for (int i = 0; i < 14; i++) {
            tbMedikasi.getColumnModel().getColumn(i).setPreferredWidth(cw[i]);
        }
        for (int j = 14; j < 38; j++) {
            tbMedikasi.getColumnModel().getColumn(j).setPreferredWidth(55);
        }
        tbMedikasi.setRowHeight(36);

        tbMedikasi.setDefaultRenderer(Object.class, new WarnaTable() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                if (!s && r < t.getRowCount()) {
                    String verif = t.getValueAt(r, 13) == null ? "" : t.getValueAt(r, 13).toString();
                    if (c >= 14 && c < 38) {
                        String val = v == null ? "" : v.toString();
                        if (comp instanceof JLabel) {
                            JLabel lbl2 = (JLabel) comp;
                            lbl2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                            lbl2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
                            if (!val.isEmpty()) {
                                // Ambil detail jam dari value format "status|jam_aktual|ket"
                                String[] parts = val.split("\\|", -1);
                                String st = parts[0];
                                String jamAktual = parts.length > 1 ? parts[1] : "";
                                String ket = parts.length > 2 ? parts[2] : "";
                                String simbol = "o".equals(st) ? "V" : "T".equals(st) ? "X" : st;
                                String baris2 = !jamAktual.isEmpty() ? jamAktual : (!ket.isEmpty() ? ket : "");
                                lbl2.setText("<html><center><b>" + simbol + "</b><br><font size='1'>" + baris2 + "</font></center></html>");
                            } else {
                                lbl2.setText("");
                            }
                        }
                        String st = val.isEmpty() ? "" : val.split("\\|")[0];
                        if ("o".equals(st)) comp.setBackground(new Color(200, 255, 200));
                        else if ("T".equals(st)) comp.setBackground(new Color(255, 200, 200));
                        else if ("K".equals(st)) comp.setBackground(new Color(255, 255, 150));
                        else if ("ESO".equals(st)) comp.setBackground(new Color(255, 180, 255));
                        else if ("TAP".equals(st)) comp.setBackground(new Color(220, 220, 255));
                        else comp.setBackground(Color.WHITE);
                    } else if (!verif.isEmpty()) {
                        comp.setBackground(new Color(230, 255, 230));
                    }
                }
                return comp;
            }
        });

        tbMedikasi.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tbMedikasi.getSelectedRow();
                int col = tbMedikasi.getSelectedColumn();
                if (row > -1 && col < 14) getDataMedikasi();
                // Single click pada kolom jam -> toggle centang
                if (row > -1 && col >= 14 && col < 38) {
                    toggleJamStatus(row, col);
                }
            }
        });

        // Popup menu
        JPopupMenu popup = new JPopupMenu();
        JMenuItem mnVerif = new JMenuItem("Verifikasi Apoteker");
        mnVerif.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        mnVerif.addActionListener(ev -> verifikasiApoteker());
        popup.add(mnVerif);
        popup.addSeparator();
        JMenuItem mnKeterangan = new JMenuItem("Lihat/Tambah Keterangan Jam");
        mnKeterangan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        mnKeterangan.addActionListener(ev -> lihatKeteranganJam());
        popup.add(mnKeterangan);
        JMenuItem mnRiwayat = new JMenuItem("Riwayat Pemberian (Audit Log)");
        mnRiwayat.addActionListener(ev -> lihatAuditLog());
        popup.add(mnRiwayat);
        JMenuItem mnBukti = new JMenuItem("Lihat Bukti Pemberian (Foto/TTD)");
        mnBukti.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        mnBukti.addActionListener(ev -> lihatBukti(false));
        popup.add(mnBukti);
        JMenuItem mnCetakBukti = new JMenuItem("Cetak Bukti Pemberian (Foto/TTD)");
        mnCetakBukti.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetakBukti.addActionListener(ev -> lihatBukti(true));
        popup.add(mnCetakBukti);
        JMenuItem mnLabel = new JMenuItem("Cetak Label Obat");
        mnLabel.addActionListener(ev -> cetakLabel());
        popup.add(mnLabel);
        JMenuItem mnGrafik = new JMenuItem("Grafik Kepatuhan Pemberian");
        mnGrafik.addActionListener(ev -> grafikKepatuhan());
        popup.add(mnGrafik);
        popup.addSeparator();
        JMenuItem mnHapus = new JMenuItem("Hapus Data Obat");
        mnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        mnHapus.addActionListener(ev -> hapusData());
        popup.add(mnHapus);
        tbMedikasi.setComponentPopupMenu(popup);

        Scroll2.setViewportView(tbMedikasi);
        internalFrame4.add(Scroll2, BorderLayout.CENTER);

        // Input panel
        PanelInputMedikasi = new JPanel(new BorderLayout(1, 1));
        PanelInputMedikasi.setOpaque(false);
        PanelInputMedikasi.setPreferredSize(new Dimension(192, 160));

        ChkInputMedikasi = new CekBox();
        ChkInputMedikasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInputMedikasi.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInputMedikasi.setText(".: Input Catatan Pengobatan");
        ChkInputMedikasi.setSelected(true);
        ChkInputMedikasi.setBorderPainted(true); ChkInputMedikasi.setBorderPaintedFlat(true);
        ChkInputMedikasi.setFocusable(false);
        ChkInputMedikasi.setPreferredSize(new Dimension(192, 20));
        ChkInputMedikasi.addActionListener(ev -> isFormMedikasi());
        PanelInputMedikasi.add(ChkInputMedikasi, BorderLayout.PAGE_END);

        scrollPaneMedikasi = new ScrollPane();
        panelGlassMedikasi = new panelisi();
        panelGlassMedikasi.setPreferredSize(new Dimension(44, 130));
        panelGlassMedikasi.setLayout(null);

        // Row 1: Kode Dokter, Nama Dokter, Nama Obat
        Label lbl1 = new Label(); lbl1.setText("Kode Dokter :"); lbl1.setBounds(0, 10, 80, h); panelGlassMedikasi.add(lbl1);
        TKodeDokter = new TextBox(); TKodeDokter.setBounds(85, 10, 80, h); panelGlassMedikasi.add(TKodeDokter);
        TKodeDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    TNamaDokter.setText(Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", TKodeDokter.getText()));
                }
            }
        });
        Button btnCariDok = new Button();
        btnCariDok.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnCariDok.setBounds(167, 10, 28, h);
        btnCariDok.addActionListener(ev -> {
            DlgCariDokter dlg = new DlgCariDokter(null, true);
            dlg.emptTeks(); dlg.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20);
            dlg.setLocationRelativeTo(internalFrame1); dlg.setVisible(true);
            if (dlg.getTable().getSelectedRow() != -1) {
                TKodeDokter.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 0).toString());
                TNamaDokter.setText(dlg.getTable().getValueAt(dlg.getTable().getSelectedRow(), 1).toString());
            }
        });
        panelGlassMedikasi.add(btnCariDok);

        Label lbl2 = new Label(); lbl2.setText("Nama Dokter :"); lbl2.setBounds(200, 10, 80, h); panelGlassMedikasi.add(lbl2);
        TNamaDokter = new TextBox(); TNamaDokter.setEditable(false); TNamaDokter.setBounds(285, 10, 170, h); panelGlassMedikasi.add(TNamaDokter);

        Label lbl3 = new Label(); lbl3.setText("Nama Obat :"); lbl3.setBounds(465, 10, 70, h); panelGlassMedikasi.add(lbl3);
        TNamaObat = new TextBox(); TNamaObat.setBounds(540, 10, 270, h); panelGlassMedikasi.add(TNamaObat);
        Button btnCariObat = new Button();
        btnCariObat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        btnCariObat.setToolTipText("Cari Obat");
        btnCariObat.setBounds(812, 10, 28, h);
        DlgCariMasterObat obatDlg = new DlgCariMasterObat(null, true);
        btnCariObat.addActionListener(ev -> {
            obatDlg.emptTeks(); obatDlg.isCek(); obatDlg.tampil();
            obatDlg.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20);
            obatDlg.setLocationRelativeTo(internalFrame1); obatDlg.setVisible(true);
            if (obatDlg.getTable().getSelectedRow() != -1) {
                int r = obatDlg.getTable().getSelectedRow();
                TNamaObat.setText(obatDlg.getTable().getValueAt(r, 1).toString()); // Nama Obat
                TKekuatan.setText(""); // user isi manual
                String satuan = obatDlg.getTable().getValueAt(r, 2) == null ? "" : obatDlg.getTable().getValueAt(r, 2).toString();
                TBentukSediaan.setText(satuan);
                // Auto-detect rute dan section dari kategori/jenis
                String jenis = obatDlg.getTable().getValueAt(r, 5) == null ? "" : obatDlg.getTable().getValueAt(r, 5).toString().toLowerCase();
                String namaObat = obatDlg.getTable().getValueAt(r, 1).toString().toLowerCase();
                if (namaObat.contains("inj") || namaObat.contains(" iv") || namaObat.contains("amp") || namaObat.contains("vial")) {
                    TRutePemberian.setText("IV/IM");
                    CmbSection.setSelectedIndex(0); // Parenteral
                } else if (namaObat.contains("inf") || namaObat.contains(" rl") || namaObat.contains("nacl") || namaObat.contains("nebul") || namaObat.contains("salep") || namaObat.contains("cream")) {
                    TRutePemberian.setText("Infus/Topikal");
                    CmbSection.setSelectedIndex(2); // Lain-lain
                } else {
                    TRutePemberian.setText("Oral");
                    CmbSection.setSelectedIndex(1); // Oral
                }
                // Auto-detect high-alert
                if (namaObat.contains("insulin") || namaObat.contains("heparin") || namaObat.contains("morfin") || namaObat.contains("kcl") ||
                    namaObat.contains("warfarin") || namaObat.contains("digoxin") || namaObat.contains("adrenalin") || namaObat.contains("epinefrin")) {
                    chkHighAlert.setSelected(true);
                } else {
                    chkHighAlert.setSelected(false);
                }
            }
        });
        panelGlassMedikasi.add(btnCariObat);

        // Row 2: Kekuatan, Sediaan, Aturan Pakai, Rute
        Label lbl4 = new Label(); lbl4.setText("Kekuatan :"); lbl4.setBounds(0, 40, 60, h); panelGlassMedikasi.add(lbl4);
        TKekuatan = new TextBox(); TKekuatan.setBounds(65, 40, 80, h); panelGlassMedikasi.add(TKekuatan);
        Label lbl5 = new Label(); lbl5.setText("Sediaan :"); lbl5.setBounds(150, 40, 55, h); panelGlassMedikasi.add(lbl5);
        TBentukSediaan = new TextBox(); TBentukSediaan.setBounds(208, 40, 100, h); panelGlassMedikasi.add(TBentukSediaan);
        Label lbl6 = new Label(); lbl6.setText("Aturan Pakai :"); lbl6.setBounds(315, 40, 80, h); panelGlassMedikasi.add(lbl6);
        TAturanPakai = new TextBox(); TAturanPakai.setBounds(398, 40, 130, h); panelGlassMedikasi.add(TAturanPakai);
        Label lbl7 = new Label(); lbl7.setText("Rute :"); lbl7.setBounds(535, 40, 30, h); panelGlassMedikasi.add(lbl7);
        TRutePemberian = new TextBox(); TRutePemberian.setBounds(568, 40, 60, h); panelGlassMedikasi.add(TRutePemberian);
        Label lbl10 = new Label(); lbl10.setText("Kode AB :"); lbl10.setBounds(635, 40, 50, h); panelGlassMedikasi.add(lbl10);
        TKodeAB = new TextBox(); TKodeAB.setToolTipText("pro=profilaksis, emp=empiris, def=definitif"); TKodeAB.setBounds(688, 40, 40, h); panelGlassMedikasi.add(TKodeAB);
        Label lbl11 = new Label(); lbl11.setText("Section :"); lbl11.setBounds(735, 40, 48, h); panelGlassMedikasi.add(lbl11);
        CmbSection = new ComboBox(); CmbSection.setModel(new DefaultComboBoxModel(new String[]{"Parenteral","Oral","Lain-lain"}));
        CmbSection.setBounds(786, 40, 90, h); panelGlassMedikasi.add(CmbSection);

        // Row 3: Tgl Mulai, Tgl Stop, Verifikasi Apoteker
        Label lbl8 = new Label(); lbl8.setText("Tgl Mulai :"); lbl8.setBounds(0, 70, 60, h); panelGlassMedikasi.add(lbl8);
        TglMulai = new widget.Tanggal(); TglMulai.setDisplayFormat("dd-MM-yyyy"); TglMulai.setBounds(65, 70, 100, h); panelGlassMedikasi.add(TglMulai);
        // Tgl Stop hanya diisi kalau obat memang dihentikan. Selama pasien masih
        // dirawat dan obat masih jalan, biarkan tidak dicentang -> tersimpan NULL.
        ChkStop = new CekBox(); ChkStop.setText("Tgl Stop :");
        ChkStop.setToolTipText("Centang hanya bila obat dihentikan. Kalau belum dihentikan, biarkan kosong.");
        ChkStop.setBounds(172, 70, 76, h); panelGlassMedikasi.add(ChkStop);
        TglStop = new widget.Tanggal(); TglStop.setDisplayFormat("dd-MM-yyyy");
        TglStop.setBounds(250, 70, 95, h); TglStop.setEnabled(false); panelGlassMedikasi.add(TglStop);
        ChkStop.addActionListener(ev -> {
            TglStop.setEnabled(ChkStop.isSelected());
            if (ChkStop.isSelected()) TglStop.setDate(new java.util.Date());
        });

        Button btnVerif = new Button();
        btnVerif.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        btnVerif.setText("Verifikasi Apoteker");
        btnVerif.setBounds(352, 70, 140, h);
        btnVerif.addActionListener(ev -> verifikasiApoteker());
        panelGlassMedikasi.add(btnVerif);

        // Row 3 cont: High Alert, PRN, Legend
        CekBox chkHighAlert = new CekBox(); chkHighAlert.setText("High-Alert");
        chkHighAlert.setFont(new Font("Tahoma", Font.BOLD, 10));
        chkHighAlert.setForeground(new Color(200, 0, 0));
        chkHighAlert.setBounds(500, 70, 90, h); panelGlassMedikasi.add(chkHighAlert);

        CekBox chkPRN = new CekBox(); chkPRN.setText("PRN");
        chkPRN.setFont(new Font("Tahoma", Font.BOLD, 10));
        chkPRN.setForeground(new Color(0, 0, 180));
        chkPRN.setBounds(595, 70, 55, h); panelGlassMedikasi.add(chkPRN);

        // Simpan referensi untuk akses di simpan()
        this.chkHighAlert = chkHighAlert;
        this.chkPRN = chkPRN;

        Label lblLegend = new Label();
        lblLegend.setText("o=Sudah Diberikan | T=Tdk Diberikan | K=Ditunda | ESO=Efek Samping | TAP=Tdk Ada Persediaan | Dbl-click kolom jam");
        lblLegend.setFont(new Font("Tahoma", Font.ITALIC, 9));
        lblLegend.setBounds(0, 97, 900, 16); panelGlassMedikasi.add(lblLegend);

        scrollPaneMedikasi.setViewportView(panelGlassMedikasi);
        PanelInputMedikasi.add(scrollPaneMedikasi, BorderLayout.CENTER);
        internalFrame4.add(PanelInputMedikasi, BorderLayout.PAGE_START);

        // === PANEL BAWAH ===
        panelisi panelBawah = new panelisi(); panelBawah.setPreferredSize(new Dimension(44, 44));
        panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 7));

        Button BtnSimpan = new Button(); BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setText("Simpan"); BtnSimpan.setPreferredSize(new Dimension(100, 30));
        BtnSimpan.addActionListener(ev -> simpan());
        Button BtnBaru = new Button(); BtnBaru.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBaru.setText("Baru"); BtnBaru.setPreferredSize(new Dimension(80, 30));
        BtnBaru.addActionListener(ev -> emptTeks());
        Button BtnHapus = new Button(); BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setText("Hapus"); BtnHapus.setPreferredSize(new Dimension(80, 30));
        BtnHapus.addActionListener(ev -> hapusData());
        Button BtnGanti = new Button(); BtnGanti.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/1360485642_edit-notes.png")));
        BtnGanti.setText("Ganti"); BtnGanti.setPreferredSize(new Dimension(80, 30));
        BtnGanti.addActionListener(ev -> ganti());
        Button BtnPrint = new Button(); BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        BtnPrint.setText("Cetak"); BtnPrint.setPreferredSize(new Dimension(80, 30));
        BtnPrint.addActionListener(ev -> cetakKardek());
        Button BtnAll = new Button(); BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setText("Semua"); BtnAll.setPreferredSize(new Dimension(80, 30));
        BtnAll.addActionListener(ev -> { TCari.setText(""); tampil(); });
        Button BtnKeluar = new Button(); BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setText("Keluar"); BtnKeluar.setPreferredSize(new Dimension(80, 30));
        BtnKeluar.addActionListener(ev -> dispose());

        LCount = new Label(); LCount.setText("Record : 0"); LCount.setPreferredSize(new Dimension(80, 23));
        DTPCari1 = new widget.Tanggal(); DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setPreferredSize(new Dimension(90, 23));
        Label sd = new Label(); sd.setText("s.d."); sd.setPreferredSize(new Dimension(22, 23));
        DTPCari2 = new widget.Tanggal(); DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setPreferredSize(new Dimension(90, 23));
        Label lbl = new Label(); lbl.setText("Key Word :"); lbl.setPreferredSize(new Dimension(55, 23));
        TCari = new TextBox(); TCari.setPreferredSize(new Dimension(100, 23));
        Button BtnCari = new Button(); BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnCari.addActionListener(ev -> tampil());

        Button BtnTarikResep = new Button(); BtnTarikResep.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnTarikResep.setText("Tarik Resep"); BtnTarikResep.setPreferredSize(new Dimension(105, 30));
        BtnTarikResep.setToolTipText("Ambil obat dari resep dokter hari ini");
        BtnTarikResep.addActionListener(ev -> tarikResep());

        Button BtnCopyBesok = new Button(); BtnCopyBesok.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        BtnCopyBesok.setText("Copy Besok"); BtnCopyBesok.setPreferredSize(new Dimension(100, 30));
        BtnCopyBesok.setToolTipText("Copy obat aktif ke tanggal besok");
        BtnCopyBesok.addActionListener(ev -> copyKeBesok());
        Button BtnDashboard = new Button(); BtnDashboard.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png")));
        BtnDashboard.setText("Dashboard"); BtnDashboard.setPreferredSize(new Dimension(100, 30));
        BtnDashboard.setToolTipText("Ringkasan pemberian obat per shift");
        BtnDashboard.addActionListener(ev -> dashboardShift());

        // Filter Section
        Label lblFilter = new Label(); lblFilter.setText("Filter:"); lblFilter.setPreferredSize(new Dimension(30, 23));
        CmbFilterSection = new ComboBox();
        CmbFilterSection.setModel(new DefaultComboBoxModel(new String[]{"Semua","Parenteral","Oral","Lain-lain"}));
        CmbFilterSection.setPreferredSize(new Dimension(80, 23));
        CmbFilterSection.addItemListener(ev -> { if (ev.getStateChange() == java.awt.event.ItemEvent.SELECTED) tampil(); });

        panelBawah.add(BtnSimpan); panelBawah.add(BtnBaru); panelBawah.add(BtnHapus);
        panelBawah.add(BtnGanti); panelBawah.add(BtnTarikResep); panelBawah.add(BtnCopyBesok); panelBawah.add(BtnDashboard);
        panelBawah.add(BtnPrint); panelBawah.add(BtnAll); panelBawah.add(BtnKeluar);
        panelBawah.add(LCount);
        panelBawah.add(DTPCari1); panelBawah.add(sd); panelBawah.add(DTPCari2);
        panelBawah.add(lblFilter); panelBawah.add(CmbFilterSection);
        panelBawah.add(lbl); panelBawah.add(TCari); panelBawah.add(BtnCari);

        // === ASSEMBLY ===
        internalFrame1.add(panelAtas, BorderLayout.PAGE_START);
        internalFrame1.add(internalFrame4, BorderLayout.CENTER);
        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);
        setLayout(new BorderLayout()); add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); setUndecorated(true);
    }

    private void isFormMedikasi() {
        if (ChkInputMedikasi.isSelected()) {
            ChkInputMedikasi.setVisible(false);
            PanelInputMedikasi.setPreferredSize(new Dimension(192, 160));
            scrollPaneMedikasi.setVisible(true);
            ChkInputMedikasi.setVisible(true);
        } else {
            ChkInputMedikasi.setVisible(false);
            PanelInputMedikasi.setPreferredSize(new Dimension(192, 20));
            scrollPaneMedikasi.setVisible(false);
            ChkInputMedikasi.setVisible(true);
        }
    }

    private void simpan() {
        if (TNoRw.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(null, "Pilih pasien dulu"); return; }
        if (TNamaObat.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(null, "Nama obat harus diisi"); return; }

        // Cek alergi
        if (cekAlergiObat(TNamaObat.getText())) return;
        // Cek interaksi
        cekInteraksiObat(TNamaObat.getText());

        String tgl = Valid.SetTgl(DTPCari1.getSelectedItem() + "");
        String maxUrut = Sequel.cariIsi("select IFNULL(MAX(no_urut),0) from kardek_obat_rscb where no_rawat=? and tgl_observasi='" + tgl + "'", TNoRw.getText());
        int noUrut = Integer.parseInt(maxUrut) + 1;

        Sequel.queryu2("insert into kardek_obat_rscb (no_rawat,tgl_observasi,no_urut,kd_dokter,nm_dokter,"
                + "nama_obat,kekuatan,bentuk_sediaan,aturan_pakai,rute_pemberian,kode_ab,tgl_mulai,nip,section,high_alert,is_prn) "
                + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 16, new String[]{
                TNoRw.getText(), tgl, String.valueOf(noUrut),
                TKodeDokter.getText(), TNamaDokter.getText(),
                TNamaObat.getText(), TKekuatan.getText(), TBentukSediaan.getText(),
                TAturanPakai.getText(), TRutePemberian.getText(),
                TKodeAB.getText(),
                Valid.SetTgl(TglMulai.getSelectedItem() + ""),
                akses.getkode(),
                CmbSection.getSelectedItem().toString(),
                chkHighAlert.isSelected() ? "Ya" : "Tidak",
                chkPRN.isSelected() ? "Ya" : "Tidak"
            });
        {
            emptTeksInput();
            tampil();
        }
    }

    private void tarikResep() {
        if (TNoRw.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(null, "Pilih pasien dulu"); return; }
        this.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        String tgl = Valid.SetTgl(DTPCari1.getSelectedItem() + "");
        int totalObat = 0;
        try {
            // Ambil resep hari ini yang belum ada di kardek
            ps = koneksi.prepareStatement(
                "select ro.no_resep, ro.kd_dokter, d.nm_dokter, rd.kode_brng, db.nama_brng, rd.jml, rd.aturan_pakai, ro.tgl_peresepan " +
                "from resep_obat ro " +
                "inner join resep_dokter rd on ro.no_resep=rd.no_resep " +
                "inner join databarang db on rd.kode_brng=db.kode_brng " +
                "inner join dokter d on ro.kd_dokter=d.kd_dokter " +
                "where ro.no_rawat=? and ro.tgl_peresepan=? and ro.status='ranap' " +
                "and not exists (select 1 from kardek_obat_rscb k where k.no_rawat=ro.no_rawat " +
                "  and k.nama_obat=db.nama_brng and k.tgl_observasi=ro.tgl_peresepan) " +
                "order by db.nama_brng");
            ps.setString(1, TNoRw.getText());
            ps.setString(2, tgl);
            rs = ps.executeQuery();
            while (rs.next()) {
                String namaObat = rs.getString("nama_brng");
                String aturan = rs.getString("aturan_pakai");
                String kdDokter = rs.getString("kd_dokter");
                String nmDokter = rs.getString("nm_dokter");
                String jumlah = String.valueOf(rs.getInt("jml"));

                // Tentukan section & rute otomatis
                String nl = namaObat.toLowerCase();
                String section, rute;
                if (nl.contains("inj") || nl.contains(" iv") || nl.contains("amp") || nl.contains("vial") || nl.contains("drip")) {
                    section = "Parenteral"; rute = "IV/IM";
                } else if (nl.contains("inf") || nl.contains(" rl") || nl.contains("nacl") || nl.contains("kaen") || nl.contains("nebul") || nl.contains("salep") || nl.contains("cream") || nl.contains("tetes")) {
                    section = "Lain-lain"; rute = "Infus/Topikal";
                } else {
                    section = "Oral"; rute = "Oral";
                }

                // Auto-detect high-alert
                String highAlert = "Tidak";
                if (nl.contains("insulin") || nl.contains("heparin") || nl.contains("morfin") || nl.contains("kcl") ||
                    nl.contains("kalium") || nl.contains("warfarin") || nl.contains("digoxin") || nl.contains("adrenalin") ||
                    nl.contains("epinefrin") || nl.contains("norepinefrin") || nl.contains("dopamin") || nl.contains("dobutamin")) {
                    highAlert = "Ya";
                }

                // Hitung no_urut
                String maxUrut = Sequel.cariIsi("select IFNULL(MAX(no_urut),0) from kardek_obat_rscb where no_rawat=? and tgl_observasi='" + tgl + "'", TNoRw.getText());
                int noUrut = Integer.parseInt(maxUrut) + 1;

                Sequel.queryu2("insert into kardek_obat_rscb (no_rawat,tgl_observasi,no_urut,kd_dokter,nm_dokter,"
                    + "nama_obat,kekuatan,bentuk_sediaan,aturan_pakai,rute_pemberian,kode_ab,tgl_mulai,nip,section,high_alert,is_prn) "
                    + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 16, new String[]{
                    TNoRw.getText(), tgl, String.valueOf(noUrut),
                    kdDokter, nmDokter, namaObat, jumlah, "", aturan, rute, "", tgl,
                    akses.getkode(), section, highAlert, "Tidak"
                });
                totalObat++;
            }
        } catch (Exception e) {
            System.out.println("Notif tarikResep: " + e);
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {}
        }
        this.setCursor(java.awt.Cursor.getDefaultCursor());
        if (totalObat > 0) {
            JOptionPane.showMessageDialog(null, totalObat + " obat berhasil ditarik dari resep hari ini.");
        } else {
            JOptionPane.showMessageDialog(null, "Tidak ada resep baru untuk hari ini, atau semua sudah ada di kardek.");
        }
        tampil();
    }

    private void ganti() {
        if (tbMedikasi.getSelectedRow() == -1) return;
        if (TNamaObat.getText().trim().isEmpty()) return;

        String noRw = tabModeMedikasi.getValueAt(tbMedikasi.getSelectedRow(), 0).toString();
        String tglObs = tabModeMedikasi.getValueAt(tbMedikasi.getSelectedRow(), 1).toString();
        String noUrut = tabModeMedikasi.getValueAt(tbMedikasi.getSelectedRow(), 2).toString();

        if (Sequel.mengedittf("kardek_obat_rscb",
                "kd_dokter=?,nm_dokter=?,nama_obat=?,kekuatan=?,bentuk_sediaan=?,aturan_pakai=?,rute_pemberian=?,kode_ab=?,tgl_mulai=?,tgl_stop=?,section=?",
                "no_rawat=? and tgl_observasi=? and no_urut=?", 14, new String[]{
                TKodeDokter.getText(), TNamaDokter.getText(),
                TNamaObat.getText(), TKekuatan.getText(), TBentukSediaan.getText(),
                TAturanPakai.getText(), TRutePemberian.getText(), TKodeAB.getText(),
                Valid.SetTgl(TglMulai.getSelectedItem() + ""),
                ChkStop.isSelected() ? Valid.SetTgl(TglStop.getSelectedItem() + "") : null,
                CmbSection.getSelectedItem().toString(),
                noRw, tglObs, noUrut
            })) {
            emptTeksInput();
            tampil();
        }
    }

    private void hapusData() {
        if (tbMedikasi.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih data dulu"); return; }
        int ok = JOptionPane.showConfirmDialog(null, "Hapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            String noRw = tabModeMedikasi.getValueAt(tbMedikasi.getSelectedRow(), 0).toString();
            String tglObs = tabModeMedikasi.getValueAt(tbMedikasi.getSelectedRow(), 1).toString();
            String noUrut = tabModeMedikasi.getValueAt(tbMedikasi.getSelectedRow(), 2).toString();
            Sequel.queryu2tf("delete from kardek_obat_rscb where no_rawat=? and tgl_observasi=? and no_urut=?", 3, new String[]{noRw, tglObs, noUrut});
            emptTeksInput();
            tampil();
        }
    }

    private void verifikasiApoteker() {
        if (tbMedikasi.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih obat dulu"); return; }
        // Cek hak akses: hanya admin atau apoteker (punya akses jawaban_pio_apoteker)
        if (!akses.getjawaban_pio_apoteker()) {
            JOptionPane.showMessageDialog(null, "Anda tidak memiliki hak akses verifikasi apoteker!\nHanya user dengan akses Apoteker yang bisa verifikasi.");
            return;
        }
        String noRw = tabModeMedikasi.getValueAt(tbMedikasi.getSelectedRow(), 0).toString();
        String tglObs = tabModeMedikasi.getValueAt(tbMedikasi.getSelectedRow(), 1).toString();
        String noUrut = tabModeMedikasi.getValueAt(tbMedikasi.getSelectedRow(), 2).toString();

        Sequel.queryu2("update kardek_obat_rscb set nip_apoteker=?, tgl_verifikasi=NOW() where no_rawat=? and tgl_observasi=? and no_urut=?",
            4, new String[]{akses.getkode(), noRw, tglObs, noUrut});
        String nmApoteker = Sequel.cariIsi("select nama from petugas where nip=?", akses.getkode());
        JOptionPane.showMessageDialog(null, "Terverifikasi oleh: " + nmApoteker);
        tampil();
    }

    // Lihat/tambah keterangan per jam pada baris yang dipilih
    private void lihatKeteranganJam() {
        if (tbMedikasi.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih obat dulu"); return; }
        int row = tbMedikasi.getSelectedRow();
        int col = tbMedikasi.getSelectedColumn();
        String noRw = tabModeMedikasi.getValueAt(row, 0).toString();
        String tglObs = tabModeMedikasi.getValueAt(row, 1).toString();
        String noUrut = tabModeMedikasi.getValueAt(row, 2).toString();
        String namaObat = tabModeMedikasi.getValueAt(row, 4).toString();

        // Jika klik di kolom jam, tampilkan detail jam tersebut
        if (col >= 14 && col < 38) {
            String jam = JAM_LABELS[col - 14];
            String status = Sequel.cariIsi("select status from kardek_obat_rscb_detail where no_rawat='" + noRw +
                "' and tgl_observasi='" + tglObs + "' and no_urut=" + noUrut + " and jam='" + jam + "' and is_corrected='Tidak'");
            String alasan = Sequel.cariIsi("select alasan from kardek_obat_rscb_detail where no_rawat='" + noRw +
                "' and tgl_observasi='" + tglObs + "' and no_urut=" + noUrut + " and jam='" + jam + "' and is_corrected='Tidak'");
            String catatan = Sequel.cariIsi("select catatan_klinis from kardek_obat_rscb_detail where no_rawat='" + noRw +
                "' and tgl_observasi='" + tglObs + "' and no_urut=" + noUrut + " and jam='" + jam + "' and is_corrected='Tidak'");
            String petugas = Sequel.cariIsi("select concat(ifnull(p.nama,d.nip_petugas),' (',d.nip_petugas,')') from kardek_obat_rscb_detail d left join petugas p on d.nip_petugas=p.nip where d.no_rawat='" + noRw +
                "' and d.tgl_observasi='" + tglObs + "' and d.no_urut=" + noUrut + " and d.jam='" + jam + "' and d.is_corrected='Tidak'");
            String waktu = Sequel.cariIsi("select waktu_aktual from kardek_obat_rscb_detail where no_rawat='" + noRw +
                "' and tgl_observasi='" + tglObs + "' and no_urut=" + noUrut + " and jam='" + jam + "' and is_corrected='Tidak'");
            String penerima = Sequel.cariIsi("select concat(nama_penerima,' (',hubungan_penerima,')') from kardek_obat_rscb_detail where no_rawat='" + noRw +
                "' and tgl_observasi='" + tglObs + "' and no_urut=" + noUrut + " and jam='" + jam + "' and is_corrected='Tidak'");

            if (status == null || status.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Jam " + jam + ":00 - " + namaObat + "\n\nBelum ada data pemberian.\nKlik kolom jam untuk mengisi.");
                return;
            }

            String statusLabel = "o".equals(status) ? "Diberikan" : "T".equals(status) ? "Tidak Diberikan" : "K".equals(status) ? "Ditunda" : status;

            // Tampilkan info + opsi edit keterangan
            JTextArea taCatatan = new JTextArea(catatan == null ? "" : catatan, 3, 30);
            taCatatan.setLineWrap(true);
            Object[] fields = {
                "Obat: " + namaObat,
                "Jam: " + jam + ":00 | Status: " + statusLabel,
                "Petugas: " + (petugas == null ? "-" : petugas),
                "Waktu Aktual: " + (waktu == null ? "-" : waktu),
                "Penerima: " + (penerima == null ? "-" : penerima),
                "Alasan: " + (alasan == null || alasan.isEmpty() ? "-" : alasan),
                " ", "Catatan Klinis (bisa diedit):",
                new javax.swing.JScrollPane(taCatatan)
            };
            int ok = JOptionPane.showConfirmDialog(null, fields, "Detail Pemberian Jam " + jam + ":00", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
            if (ok == JOptionPane.OK_OPTION && !taCatatan.getText().equals(catatan == null ? "" : catatan)) {
                Sequel.queryu2("update kardek_obat_rscb_detail set catatan_klinis=? where no_rawat=? and tgl_observasi=? and no_urut=? and jam=? and is_corrected='Tidak'",
                    5, new String[]{taCatatan.getText(), noRw, tglObs, noUrut, jam});
            }
        } else {
            // Klik di kolom non-jam: tampilkan semua detail jam obat ini
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("Obat: ").append(namaObat).append("\n\n");
                sb.append(String.format("%-5s %-12s %-8s %-20s %-25s %s\n", "Jam", "Status", "Waktu", "Petugas", "Penerima", "Keterangan"));
                sb.append("─".repeat(100)).append("\n");
                PreparedStatement psD = koneksi.prepareStatement(
                    "select d.jam, d.status, d.alasan, d.catatan_klinis, d.waktu_aktual, d.nip_petugas, " +
                    "ifnull(p.nama,d.nip_petugas) as nm_petugas, d.nama_penerima, d.hubungan_penerima " +
                    "from kardek_obat_rscb_detail d left join petugas p on d.nip_petugas=p.nip " +
                    "where d.no_rawat=? and d.tgl_observasi=? and d.no_urut=? and d.is_corrected='Tidak' order by d.jam");
                psD.setString(1, noRw); psD.setString(2, tglObs); psD.setString(3, noUrut);
                ResultSet rsD = psD.executeQuery();
                while (rsD.next()) {
                    String st = rsD.getString("status");
                    String stLabel = "o".equals(st) ? "\u2713" : "T".equals(st) ? "\u2717" : st;
                    String wkt = rsD.getString("waktu_aktual");
                    sb.append(String.format("%-5s %-12s %-8s %-20s %-25s %s\n",
                        rsD.getString("jam") + ":00", stLabel,
                        wkt == null ? "-" : wkt.substring(11, 16),
                        rsD.getString("nm_petugas"),
                        (rsD.getString("nama_penerima") == null ? "" : rsD.getString("nama_penerima")),
                        (rsD.getString("catatan_klinis") == null ? "" : rsD.getString("catatan_klinis")) +
                        (rsD.getString("alasan") == null || rsD.getString("alasan").isEmpty() ? "" : " [" + rsD.getString("alasan") + "]")
                    ));
                }
                rsD.close(); psD.close();
                JTextArea ta = new JTextArea(sb.toString());
                ta.setFont(new Font("Monospaced", Font.PLAIN, 11));
                ta.setEditable(false);
                JOptionPane.showMessageDialog(null, new javax.swing.JScrollPane(ta), "Detail Pemberian Obat", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) { System.out.println("Notif keterangan: " + e); }
        }
    }

    // Audit log — riwayat semua perubahan termasuk koreksi
    private void lihatAuditLog() {
        if (tbMedikasi.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih obat dulu"); return; }
        int row = tbMedikasi.getSelectedRow();
        String noRw = tabModeMedikasi.getValueAt(row, 0).toString();
        String tglObs = tabModeMedikasi.getValueAt(row, 1).toString();
        String noUrut = tabModeMedikasi.getValueAt(row, 2).toString();
        String namaObat = tabModeMedikasi.getValueAt(row, 4).toString();
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("AUDIT LOG - ").append(namaObat).append("\n\n");
            sb.append(String.format("%-5s %-8s %-8s %-20s %-20s %-10s %-20s\n", "Jam", "Status", "Koreksi", "Petugas", "Waktu Aktual", "Alasan", "Koreksi Oleh"));
            sb.append("─".repeat(110)).append("\n");
            PreparedStatement psA = koneksi.prepareStatement(
                "select d.jam, d.status, d.is_corrected, d.nip_petugas, ifnull(p.nama,d.nip_petugas) as nm, " +
                "d.waktu_aktual, d.alasan, d.correction_reason, d.corrected_by, ifnull(p2.nama,d.corrected_by) as nm_corrector " +
                "from kardek_obat_rscb_detail d left join petugas p on d.nip_petugas=p.nip " +
                "left join petugas p2 on d.corrected_by=p2.nip " +
                "where d.no_rawat=? and d.tgl_observasi=? and d.no_urut=? order by d.jam, d.waktu_input");
            psA.setString(1, noRw); psA.setString(2, tglObs); psA.setString(3, noUrut);
            ResultSet rsA = psA.executeQuery();
            while (rsA.next()) {
                String corrected = "Ya".equals(rsA.getString("is_corrected")) ? "[KOREKSI]" : "";
                sb.append(String.format("%-5s %-8s %-8s %-20s %-20s %-10s %-20s\n",
                    rsA.getString("jam") + ":00", rsA.getString("status"), corrected,
                    rsA.getString("nm"),
                    rsA.getString("waktu_aktual") == null ? "-" : rsA.getString("waktu_aktual"),
                    rsA.getString("alasan") == null ? "" : rsA.getString("alasan"),
                    rsA.getString("nm_corrector") == null ? "" : rsA.getString("nm_corrector") +
                        (rsA.getString("correction_reason") == null ? "" : ": " + rsA.getString("correction_reason"))
                ));
            }
            rsA.close(); psA.close();
            JTextArea ta = new JTextArea(sb.toString());
            ta.setFont(new Font("Monospaced", Font.PLAIN, 11));
            ta.setEditable(false);
            ta.setRows(20); ta.setColumns(80);
            JOptionPane.showMessageDialog(null, new javax.swing.JScrollPane(ta), "Audit Log Pemberian Obat", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) { System.out.println("Notif audit: " + e); }
    }

    // Single click kolom jam -> pilih status pemberian (eMAR compliant)
    private void toggleJamStatus(int row, int col) {
        if (row < 0 || col < 14 || col >= 38) return;
        String noRw = tabModeMedikasi.getValueAt(row, 0).toString();
        String tglObs = tabModeMedikasi.getValueAt(row, 1).toString();
        String noUrut = tabModeMedikasi.getValueAt(row, 2).toString();
        String jam = JAM_LABELS[col - 14];
        String namaObat = tabModeMedikasi.getValueAt(row, 4).toString();
        String rawVal = tabModeMedikasi.getValueAt(row, col) == null ? "" : tabModeMedikasi.getValueAt(row, col).toString();
        String currentVal = rawVal.contains("|") ? rawVal.split("\\|")[0] : rawVal;

        // 1. IMMUTABILITY — jika sudah diisi, tidak boleh dihapus, hanya koreksi
        if (!currentVal.isEmpty()) {
            String nipLama = Sequel.cariIsi("select nip_petugas from kardek_obat_rscb_detail where no_rawat='" + noRw +
                "' and tgl_observasi='" + tglObs + "' and no_urut=" + noUrut + " and jam='" + jam + "' and is_corrected='Tidak'");
            String nmLama = Sequel.cariIsi("select nama from petugas where nip=?", nipLama);
            int konfirm = JOptionPane.showConfirmDialog(null,
                "Slot ini sudah diisi oleh: " + nmLama + " (" + nipLama + ")\nStatus: " + currentVal +
                "\n\nApakah ingin KOREKSI? (data lama tetap tersimpan untuk audit)",
                "Koreksi Data", JOptionPane.YES_NO_OPTION);
            if (konfirm != JOptionPane.YES_OPTION) return;

            String alasanKoreksi = JOptionPane.showInputDialog(null, "Alasan koreksi (wajib diisi):");
            if (alasanKoreksi == null || alasanKoreksi.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Alasan koreksi wajib diisi!");
                return;
            }
            // Tandai entri lama sebagai corrected
            Sequel.queryu2("update kardek_obat_rscb_detail set is_corrected='Ya', corrected_by=?, correction_reason=? " +
                "where no_rawat=? and tgl_observasi=? and no_urut=? and jam=? and is_corrected='Tidak'",
                6, new String[]{akses.getkode(), alasanKoreksi, noRw, tglObs, noUrut, jam});
        }

        // 2. WINDOW WAKTU — validasi 30 menit sebelum s.d 60 menit setelah jadwal
        try {
            int jamJadwal = Integer.parseInt(jam);
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            java.time.LocalDate tglJadwal = java.time.LocalDate.parse(tglObs);
            java.time.LocalDateTime jadwal = tglJadwal.atTime(jamJadwal, 0);
            java.time.LocalDateTime windowBuka = jadwal.minusMinutes(30);
            java.time.LocalDateTime windowTutup = jadwal.plusMinutes(60);

            String statusWaktu = "";
            if (now.isBefore(windowBuka)) {
                int terlalu = JOptionPane.showConfirmDialog(null,
                    "Peringatan: Terlalu awal!\nJadwal: " + jam + ":00, Sekarang: " + now.toLocalTime().toString().substring(0,5) +
                    "\nWindow buka: " + windowBuka.toLocalTime().toString().substring(0,5) +
                    "\n\nLanjutkan?", "Peringatan Waktu", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (terlalu != JOptionPane.YES_OPTION) return;
                statusWaktu = " [AWAL]";
            } else if (now.isAfter(windowTutup)) {
                int terlambat = JOptionPane.showConfirmDialog(null,
                    "Peringatan: Terlambat!\nJadwal: " + jam + ":00, Sekarang: " + now.toLocalTime().toString().substring(0,5) +
                    "\nWindow tutup: " + windowTutup.toLocalTime().toString().substring(0,5) +
                    "\n\nLanjutkan?", "Peringatan Waktu", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (terlambat != JOptionPane.YES_OPTION) return;
                statusWaktu = " [TERLAMBAT]";
            }
        } catch (Exception e) { /* skip time validation if parse fails */ }

        // 3. Dialog pilih status
        String[] options = {"o  (Diberikan)", "T  (Tidak Diberikan)", "K  (Ditunda)", "ESO (Efek Samping)", "TAP (Tdk Ada Persediaan)"};
        int pilihan = JOptionPane.showOptionDialog(null,
            "Jam " + jam + ":00 - " + namaObat + "\nPetugas: " + akses.getkode() + "\nPilih status pemberian:",
            "Status Pemberian Obat", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);
        if (pilihan < 0) return;

        String[] statusCodes = {"o", "T", "K", "ESO", "TAP"};
        String newVal = statusCodes[pilihan];
        String alasan = "";
        String catatanKlinis = "";
        String namaPenerima = "";
        String hubungan = "";
        String nipDoubleCheck = "";

        // 4. Jika diberikan (o)
        if ("o".equals(newVal)) {
            JTextField fPenerima = new JTextField();
            JTextField fHubungan = new JTextField("Pasien");
            JTextField fCatatan = new JTextField();
            Object[] fields = {"Nama Penerima (Pasien/Keluarga):", fPenerima, "Hubungan:", fHubungan, "Catatan Klinis (kondisi pasien):", fCatatan};
            int ok = JOptionPane.showConfirmDialog(null, fields, "Konfirmasi Pemberian Obat", JOptionPane.OK_CANCEL_OPTION);
            if (ok != JOptionPane.OK_OPTION) return;
            namaPenerima = fPenerima.getText();
            hubungan = fHubungan.getText();
            catatanKlinis = fCatatan.getText();

            // 5. HIGH-ALERT — double check oleh perawat ke-2
            String highAlert = Sequel.cariIsi("select high_alert from kardek_obat_rscb where no_rawat='" + noRw +
                "' and tgl_observasi='" + tglObs + "' and no_urut=" + noUrut);
            if ("Ya".equals(highAlert)) {
                JOptionPane.showMessageDialog(null,
                    "OBAT HIGH-ALERT!\nDiperlukan konfirmasi perawat ke-2.\nMinta perawat lain untuk memasukkan NIP-nya.",
                    "Double Check High-Alert", JOptionPane.WARNING_MESSAGE);
                String nipKedua = JOptionPane.showInputDialog(null, "NIP Perawat ke-2 (harus berbeda dengan " + akses.getkode() + "):");
                if (nipKedua == null || nipKedua.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Double check wajib untuk obat high-alert!");
                    return;
                }
                if (nipKedua.equals(akses.getkode())) {
                    JOptionPane.showMessageDialog(null, "Perawat ke-2 harus berbeda dengan perawat pemberi!");
                    return;
                }
                String nmKedua = Sequel.cariIsi("select nama from petugas where nip=?", nipKedua);
                if (nmKedua == null || nmKedua.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "NIP tidak ditemukan di database!");
                    return;
                }
                int konfKedua = JOptionPane.showConfirmDialog(null,
                    "Konfirmasi double check oleh:\n" + nmKedua + " (" + nipKedua + ")\n\nSudah benar?",
                    "Konfirmasi Perawat ke-2", JOptionPane.YES_NO_OPTION);
                if (konfKedua != JOptionPane.YES_OPTION) return;
                nipDoubleCheck = nipKedua;
            }
        }

        // 6. Jika tidak diberikan/ditunda, alasan WAJIB
        if ("T".equals(newVal) || "K".equals(newVal) || "ESO".equals(newVal) || "TAP".equals(newVal)) {
            alasan = JOptionPane.showInputDialog(null, "Alasan " + options[pilihan] + " (wajib diisi):");
            if (alasan == null || alasan.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Alasan wajib diisi jika obat tidak diberikan!");
                return;
            }
        }

        // 7. INSERT dengan waktu aktual (bukan waktu jadwal)
        String waktuAktual = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        Sequel.queryu2("insert into kardek_obat_rscb_detail "
            + "(no_rawat,tgl_observasi,no_urut,jam,status,alasan,catatan_klinis,nip_petugas,nama_penerima,hubungan_penerima,nip_double_check,waktu_aktual) "
            + "values (?,?,?,?,?,?,?,?,?,?,?,?)",
            12, new String[]{noRw, tglObs, noUrut, jam, newVal, alasan, catatanKlinis, akses.getkode(), namaPenerima, hubungan, nipDoubleCheck, waktuAktual});

        // Format cell: status|jam_aktual|keterangan
        String jamAktualShort = waktuAktual.substring(11, 16);
        String ketShort = !catatanKlinis.isEmpty() ? (catatanKlinis.length() > 6 ? catatanKlinis.substring(0, 6) : catatanKlinis)
                        : (!alasan.isEmpty() ? (alasan.length() > 6 ? alasan.substring(0, 6) : alasan) : jamAktualShort);
        tabModeMedikasi.setValueAt(newVal + "|" + ketShort + "|", row, col);

        // Info bar
        String nmPetugas = Sequel.cariIsi("select nama from petugas where nip=?", akses.getkode());
        String info = nmPetugas + " | " + waktuAktual + " | Jam " + jam + ":00";
        if (!nipDoubleCheck.isEmpty()) info += " | DC: " + Sequel.cariIsi("select nama from petugas where nip=?", nipDoubleCheck);
        LCount.setText(info);
        tbMedikasi.repaint();
    }

    private void getDataMedikasi() {
        if (tbMedikasi.getSelectedRow() < 0) return;
        int r = tbMedikasi.getSelectedRow();
        String kdDokter = Sequel.cariIsi("select kd_dokter from kardek_obat_rscb where no_rawat=? and tgl_observasi='" +
            tabModeMedikasi.getValueAt(r, 1).toString() + "' and no_urut='" + tabModeMedikasi.getValueAt(r, 2).toString() + "'",
            tabModeMedikasi.getValueAt(r, 0).toString());
        TKodeDokter.setText(kdDokter);
        TNamaDokter.setText(tabModeMedikasi.getValueAt(r, 3) == null ? "" : tabModeMedikasi.getValueAt(r, 3).toString());
        TNamaObat.setText(tabModeMedikasi.getValueAt(r, 4) == null ? "" : tabModeMedikasi.getValueAt(r, 4).toString());
        TKekuatan.setText(tabModeMedikasi.getValueAt(r, 5) == null ? "" : tabModeMedikasi.getValueAt(r, 5).toString());
        TBentukSediaan.setText(tabModeMedikasi.getValueAt(r, 6) == null ? "" : tabModeMedikasi.getValueAt(r, 6).toString());
        TAturanPakai.setText(tabModeMedikasi.getValueAt(r, 7) == null ? "" : tabModeMedikasi.getValueAt(r, 7).toString());
        TRutePemberian.setText(tabModeMedikasi.getValueAt(r, 8) == null ? "" : tabModeMedikasi.getValueAt(r, 8).toString());
        TKodeAB.setText(tabModeMedikasi.getValueAt(r, 9) == null ? "" : tabModeMedikasi.getValueAt(r, 9).toString());
        // Tgl Stop: kosong/null berarti obat masih berjalan
        String tglStop = tabModeMedikasi.getValueAt(r, 12) == null ? "" : tabModeMedikasi.getValueAt(r, 12).toString();
        boolean adaStop = !tglStop.isEmpty() && !"null".equalsIgnoreCase(tglStop) && !tglStop.startsWith("0000");
        ChkStop.setSelected(adaStop);
        TglStop.setEnabled(adaStop);
        if (adaStop) {
            try { TglStop.setDate(java.sql.Date.valueOf(tglStop)); } catch (Exception e) { /* format tak terduga, abaikan */ }
        }
        String tglMulai = tabModeMedikasi.getValueAt(r, 11) == null ? "" : tabModeMedikasi.getValueAt(r, 11).toString();
        if (!tglMulai.isEmpty() && !tglMulai.startsWith("0000")) {
            try { TglMulai.setDate(java.sql.Date.valueOf(tglMulai)); } catch (Exception e) { /* format tak terduga, abaikan */ }
        }
        String section = tabModeMedikasi.getValueAt(r, 10) == null ? "" : tabModeMedikasi.getValueAt(r, 10).toString();
        for (int i = 0; i < CmbSection.getItemCount(); i++) {
            if (CmbSection.getItemAt(i).toString().equals(section)) { CmbSection.setSelectedIndex(i); break; }
        }
    }

    public void tampil() {
        Valid.tabelKosong(tabModeMedikasi);
        if (TNoRw.getText().trim().isEmpty()) return;
        try {
            ps = koneksi.prepareStatement(
                "select m.no_rawat,m.tgl_observasi,m.no_urut,m.nm_dokter,m.nama_obat,m.kekuatan,m.bentuk_sediaan,"
                + "m.aturan_pakai,m.rute_pemberian,m.kode_ab,m.section,m.tgl_mulai,m.tgl_stop,"
                + "CASE WHEN m.nip_apoteker='' OR m.nip_apoteker IS NULL THEN '' "
                + "ELSE CONCAT(IFNULL(pa.nama,m.nip_apoteker),' ',DATE_FORMAT(m.tgl_verifikasi,'%d-%m-%Y %H:%i')) END "
                + "from kardek_obat_rscb m "
                + "left join petugas pa on m.nip_apoteker=pa.nip "
                + "where m.no_rawat=? "
                + (TCari.getText().trim().isEmpty() ? "" : "and m.nama_obat like '%" + TCari.getText().trim() + "%' ")
                + (CmbFilterSection != null && CmbFilterSection.getSelectedIndex() > 0 ? "and m.section='" + CmbFilterSection.getSelectedItem().toString() + "' " : "")
                + "order by m.section,m.tgl_observasi,m.no_urut");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[38];
                row[0] = rs.getString(1);  // no_rawat
                row[1] = rs.getString(2);  // tgl_observasi
                row[2] = rs.getString(3);  // no_urut
                row[3] = rs.getString(4);  // dokter
                row[4] = rs.getString(5);  // nama_obat
                row[5] = rs.getString(6);  // kekuatan
                row[6] = rs.getString(7);  // bentuk_sediaan
                row[7] = rs.getString(8);  // aturan_pakai
                row[8] = rs.getString(9);  // rute
                row[9] = rs.getString(10); // kode_ab
                row[10] = rs.getString(11); // section
                row[11] = rs.getString(12); // tgl_mulai
                row[12] = rs.getString(13); // tgl_stop
                row[13] = rs.getString(14); // verifikasi
                for (int j = 14; j < 38; j++) row[j] = "";

                // Load detail jam: format "status|jam_aktual|keterangan"
                PreparedStatement psD = koneksi.prepareStatement(
                    "select jam, status, waktu_aktual, catatan_klinis, alasan from kardek_obat_rscb_detail where no_rawat=? and tgl_observasi=? and no_urut=? and is_corrected='Tidak'");
                psD.setString(1, rs.getString(1));
                psD.setString(2, rs.getString(2));
                psD.setString(3, rs.getString(3));
                ResultSet rsD = psD.executeQuery();
                while (rsD.next()) {
                    String jam = rsD.getString(1);
                    String status = rsD.getString(2);
                    String waktuAkt = rsD.getString(3);
                    String catKlinis = rsD.getString(4);
                    String alsn = rsD.getString(5);
                    // Baris 2: jam aktual atau keterangan/alasan
                    String baris2 = "";
                    if (waktuAkt != null && waktuAkt.length() >= 16) baris2 = waktuAkt.substring(11, 16);
                    if (catKlinis != null && !catKlinis.isEmpty()) baris2 = catKlinis.length() > 6 ? catKlinis.substring(0, 6) : catKlinis;
                    if ((alsn != null && !alsn.isEmpty()) && baris2.isEmpty()) baris2 = alsn.length() > 6 ? alsn.substring(0, 6) : alsn;
                    String cellVal = status + "|" + baris2 + "|";
                    for (int j = 0; j < JAM_LABELS.length; j++) {
                        if (JAM_LABELS[j].equals(jam)) {
                            row[14 + j] = cellVal;
                            break;
                        }
                    }
                }
                rsD.close(); psD.close();
                tabModeMedikasi.addRow(row);
            }
            rs.close(); ps.close();
        } catch (Exception e) {
            System.out.println("Notif tampilMedikasi: " + e);
        }
        LCount.setText("Record : " + tabModeMedikasi.getRowCount());
    }

    private void cetakKardek() {
        if (TNoRw.getText().trim().isEmpty()) return;
        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars()); param.put("alamatrs", akses.getalamatrs());
        param.put("propinsirs", akses.getpropinsirs()); param.put("kabupatenrs", akses.getkabupatenrs());
        param.put("kontakrs", akses.getkontakrs()); param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        Valid.MyReportqry("rptInstruksiMedisFarmaRscb.jasper", "report", "::[ Kardex Obat ]::",
            "select m.no_rawat, m.tgl_observasi, m.no_urut, m.nm_dokter, m.nama_obat, m.kekuatan, "
            + "m.bentuk_sediaan, m.aturan_pakai, m.rute_pemberian, m.tgl_mulai, m.tgl_stop, "
            + "CASE WHEN m.nip_apoteker='' THEN '' ELSE IFNULL(pa.nama,m.nip_apoteker) END as verifikasi, "
            + "r.no_rkm_medis, p.nm_pasien, p.tgl_lahir, if(p.jk='L','Laki-Laki','Perempuan') as jk "
            + "from kardek_obat_rscb m "
            + "inner join reg_periksa r on m.no_rawat=r.no_rawat "
            + "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "
            + "left join petugas pa on m.nip_apoteker=pa.nip "
            + "where m.no_rawat='" + TNoRw.getText() + "' "
            + "order by m.tgl_observasi, m.no_urut", param);
    }

    // === FITUR 7: COPY KE BESOK ===
    private void copyKeBesok() {
        if (TNoRw.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(null, "Pilih pasien dulu"); return; }
        String tglHari = Valid.SetTgl(DTPCari1.getSelectedItem() + "");
        java.time.LocalDate besok = java.time.LocalDate.parse(tglHari).plusDays(1);
        int total = 0;
        try {
            // Ambil obat aktif (belum di-stop) hari ini
            ps = koneksi.prepareStatement(
                "select * from kardek_obat_rscb where no_rawat=? and tgl_observasi=? and (tgl_stop is null or tgl_stop >= ?)");
            ps.setString(1, TNoRw.getText()); ps.setString(2, tglHari); ps.setString(3, besok.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                // Cek belum ada di besok
                String cek = Sequel.cariIsi("select no_urut from kardek_obat_rscb where no_rawat='" + TNoRw.getText() +
                    "' and tgl_observasi='" + besok + "' and nama_obat='" + rs.getString("nama_obat").replace("'","''") + "'");
                if (cek == null || cek.isEmpty()) {
                    String maxUrut = Sequel.cariIsi("select IFNULL(MAX(no_urut),0) from kardek_obat_rscb where no_rawat='" + TNoRw.getText() + "' and tgl_observasi='" + besok + "'");
                    int noUrut = Integer.parseInt(maxUrut) + 1;
                    Sequel.queryu2("insert into kardek_obat_rscb (no_rawat,tgl_observasi,no_urut,kd_dokter,nm_dokter,nama_obat,kekuatan,bentuk_sediaan,aturan_pakai,rute_pemberian,kode_ab,tgl_mulai,nip,section,high_alert,is_prn) "
                        + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", 16, new String[]{
                        TNoRw.getText(), besok.toString(), String.valueOf(noUrut),
                        rs.getString("kd_dokter"), rs.getString("nm_dokter"), rs.getString("nama_obat"),
                        rs.getString("kekuatan"), rs.getString("bentuk_sediaan"), rs.getString("aturan_pakai"),
                        rs.getString("rute_pemberian"), rs.getString("kode_ab"), rs.getString("tgl_mulai"),
                        akses.getkode(), rs.getString("section"), rs.getString("high_alert"), rs.getString("is_prn")
                    });
                    total++;
                }
            }
        } catch (Exception e) { System.out.println("Notif copyBesok: " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
        JOptionPane.showMessageDialog(null, total + " obat aktif di-copy ke tanggal " + besok);
    }

    // === FITUR 1: DASHBOARD RINGKASAN SHIFT ===
    private void dashboardShift() {
        if (TNoRw.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(null, "Pilih pasien dulu"); return; }
        String tgl = Valid.SetTgl(DTPCari1.getSelectedItem() + "");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("DASHBOARD PEMBERIAN OBAT\n");
            sb.append("Pasien: ").append(TPasien.getText()).append(" | Tanggal: ").append(tgl).append("\n");
            sb.append("═".repeat(70)).append("\n\n");

            String[][] shifts = {{"Pagi (07-14)", "07','08','09','10','11','12','13"}, {"Siang (14-22)", "14','15','16','17','18','19','20','21"}, {"Malam (22-06)", "22','23','24','01','02','03','04','05','06"}};
            for (String[] shift : shifts) {
                String totalObat = Sequel.cariIsi("select count(distinct no_urut) from kardek_obat_rscb where no_rawat='" + TNoRw.getText() + "' and tgl_observasi='" + tgl + "'");
                String diberikan = Sequel.cariIsi("select count(*) from kardek_obat_rscb_detail where no_rawat='" + TNoRw.getText() + "' and tgl_observasi='" + tgl + "' and status='o' and is_corrected='Tidak' and jam in ('" + shift[1] + "')");
                String tidakDiberikan = Sequel.cariIsi("select count(*) from kardek_obat_rscb_detail where no_rawat='" + TNoRw.getText() + "' and tgl_observasi='" + tgl + "' and status!='o' and status!='' and is_corrected='Tidak' and jam in ('" + shift[1] + "')");

                sb.append("SHIFT ").append(shift[0]).append("\n");
                sb.append("  Total Obat   : ").append(totalObat).append("\n");
                sb.append("  Diberikan    : ").append(diberikan).append(" ✓\n");
                sb.append("  Tidak/Tunda  : ").append(tidakDiberikan).append(" ✗\n");
                int total = Integer.parseInt(diberikan == null ? "0" : diberikan) + Integer.parseInt(tidakDiberikan == null ? "0" : tidakDiberikan);
                int dib = Integer.parseInt(diberikan == null ? "0" : diberikan);
                String persen = total > 0 ? String.format("%.0f%%", (dib * 100.0 / total)) : "-";
                sb.append("  Kepatuhan    : ").append(persen).append("\n\n");
            }

            // Obat high-alert status
            sb.append("─".repeat(70)).append("\n");
            sb.append("OBAT HIGH-ALERT:\n");
            PreparedStatement psH = koneksi.prepareStatement(
                "select nama_obat, nm_dokter from kardek_obat_rscb where no_rawat=? and tgl_observasi=? and high_alert='Ya'");
            psH.setString(1, TNoRw.getText()); psH.setString(2, tgl);
            ResultSet rsH = psH.executeQuery();
            int ha = 0;
            while (rsH.next()) {
                sb.append("  ⚠ ").append(rsH.getString("nama_obat")).append(" (DPJP: ").append(rsH.getString("nm_dokter")).append(")\n");
                ha++;
            }
            if (ha == 0) sb.append("  Tidak ada obat high-alert\n");
            rsH.close(); psH.close();

            JTextArea ta = new JTextArea(sb.toString());
            ta.setFont(new Font("Monospaced", Font.PLAIN, 12)); ta.setEditable(false);
            ta.setRows(22); ta.setColumns(60);
            JOptionPane.showMessageDialog(null, new javax.swing.JScrollPane(ta), "Dashboard Pemberian Obat", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) { System.out.println("Notif dashboard: " + e); }
    }

    // === FITUR 6: CETAK LABEL OBAT ===
    private void cetakLabel() {
        if (tbMedikasi.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih obat dulu"); return; }

        // Masukkan data obat yang dipilih ke temporary_resep
        Sequel.queryu("delete from temporary_resep where temp37='" + akses.getalamatip() + "'");
        int no = 1;
        for (int r = 0; r < tabModeMedikasi.getRowCount(); r++) {
            if (tbMedikasi.isRowSelected(r)) {
                String namaObat = tabModeMedikasi.getValueAt(r, 4) == null ? "" : tabModeMedikasi.getValueAt(r, 4).toString();
                String kekuatan = tabModeMedikasi.getValueAt(r, 5) == null ? "" : tabModeMedikasi.getValueAt(r, 5).toString();
                String aturan = tabModeMedikasi.getValueAt(r, 7) == null ? "" : tabModeMedikasi.getValueAt(r, 7).toString();
                String rute = tabModeMedikasi.getValueAt(r, 8) == null ? "" : tabModeMedikasi.getValueAt(r, 8).toString();
                String section = tabModeMedikasi.getValueAt(r, 10) == null ? "" : tabModeMedikasi.getValueAt(r, 10).toString();

                Sequel.menyimpantf("temporary_resep",
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?",
                    "Label", 38, new String[]{
                    String.valueOf(no), namaObat, kekuatan, aturan, rute, section, "", "", "", "",
                    "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
                    akses.getalamatip()
                });
                no++;
            }
        }

        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("emailrs", akses.getemailrs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("tanggal", Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
        param.put("norawat", TNoRw.getText());
        param.put("pasien", TPasien.getText());
        param.put("norm", TNoRM.getText());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        Valid.MyReportqry("rptLabelDaftarObat.jasper", "report", "::[ Label Obat ]::",
            "select * from temporary_resep where temporary_resep.temp37='" + akses.getalamatip() + "' order by temporary_resep.no", param);
    }

    // === FITUR 5: GRAFIK KEPATUHAN ===
    private void grafikKepatuhan() {
        if (TNoRw.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(null, "Pilih pasien dulu"); return; }
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("GRAFIK KEPATUHAN PEMBERIAN OBAT\n");
            sb.append("Pasien: ").append(TPasien.getText()).append("\n");
            sb.append("═".repeat(60)).append("\n\n");
            sb.append(String.format("%-12s %-8s %-8s %-8s %-10s\n", "Tanggal", "Total", "Given", "Skip", "Kepatuhan"));
            sb.append("─".repeat(60)).append("\n");

            PreparedStatement psG = koneksi.prepareStatement(
                "select m.tgl_observasi, " +
                "count(distinct m.no_urut) as total_obat, " +
                "sum(case when d.status='o' then 1 else 0 end) as given, " +
                "sum(case when d.status in ('T','K','ESO','TAP') then 1 else 0 end) as skipped " +
                "from kardek_obat_rscb m left join kardek_obat_rscb_detail d on m.no_rawat=d.no_rawat and m.tgl_observasi=d.tgl_observasi and m.no_urut=d.no_urut and d.is_corrected='Tidak' " +
                "where m.no_rawat=? group by m.tgl_observasi order by m.tgl_observasi");
            psG.setString(1, TNoRw.getText());
            ResultSet rsG = psG.executeQuery();
            while (rsG.next()) {
                int given = rsG.getInt("given");
                int skipped = rsG.getInt("skipped");
                int total = given + skipped;
                String persen = total > 0 ? String.format("%.0f%%", (given * 100.0 / total)) : "-";
                // Bar chart visual
                int barLen = total > 0 ? (given * 30 / total) : 0;
                String bar = "█".repeat(barLen) + "░".repeat(30 - barLen);
                sb.append(String.format("%-12s %-8d %-8d %-8d %-10s %s\n",
                    rsG.getString("tgl_observasi"), rsG.getInt("total_obat"), given, skipped, persen, bar));
            }
            rsG.close(); psG.close();

            JTextArea ta = new JTextArea(sb.toString());
            ta.setFont(new Font("Monospaced", Font.PLAIN, 12)); ta.setEditable(false);
            ta.setRows(18); ta.setColumns(70);
            JOptionPane.showMessageDialog(null, new javax.swing.JScrollPane(ta), "Grafik Kepatuhan", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) { System.out.println("Notif grafik: " + e); }
    }

    // === FITUR 8: CEK ALERGI SAAT TARIK RESEP (sudah di tarikResep, tambahan alert) ===
    private boolean cekAlergiObat(String namaObat) {
        if (TNoRw.getText().trim().isEmpty()) return false;
        try {
            ps = koneksi.prepareStatement(
                "select allergy_code from alergi_pasien where no_rawat=? and lower(allergy_code) like ?");
            ps.setString(1, TNoRw.getText());
            ps.setString(2, "%" + namaObat.toLowerCase() + "%");
            rs = ps.executeQuery();
            if (rs.next()) {
                int ok = JOptionPane.showConfirmDialog(null,
                    "⚠ PERINGATAN ALERGI!\n\nPasien memiliki riwayat alergi terhadap:\n" + rs.getString("allergy_code") +
                    "\n\nObat yang akan diberikan: " + namaObat +
                    "\n\nApakah tetap lanjutkan?",
                    "ALERT ALERGI", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                rs.close(); ps.close();
                return ok != JOptionPane.YES_OPTION; // true = blocked
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println("Notif cekAlergi: " + e); }
        return false;
    }

    // === FITUR 2: CEK INTERAKSI OBAT ===
    private void cekInteraksiObat(String namaObatBaru) {
        if (TNoRw.getText().trim().isEmpty()) return;
        try {
            // Ambil semua obat aktif pasien ini
            ps = koneksi.prepareStatement(
                "select distinct nama_obat from kardek_obat_rscb where no_rawat=? and (tgl_stop is null or tgl_stop >= CURDATE()) and nama_obat != ?");
            ps.setString(1, TNoRw.getText());
            ps.setString(2, namaObatBaru);
            rs = ps.executeQuery();
            java.util.List<String> obatAktif = new java.util.ArrayList<>();
            while (rs.next()) obatAktif.add(rs.getString("nama_obat"));
            rs.close(); ps.close();

            // Cek setiap obat aktif vs obat baru di tabel interaksi_obat
            StringBuilder hasil = new StringBuilder();
            for (String obatLama : obatAktif) {
                PreparedStatement psI = koneksi.prepareStatement(
                    "select komposisi1, komposisi2, tingkat_interaksi, deskripsi_interaksi, rekomendasi " +
                    "from interaksi_obat where status='AKTIF' and " +
                    "((lower(komposisi1) like ? and lower(komposisi2) like ?) or (lower(komposisi1) like ? and lower(komposisi2) like ?))");
                psI.setString(1, "%" + namaObatBaru.toLowerCase() + "%");
                psI.setString(2, "%" + obatLama.toLowerCase() + "%");
                psI.setString(3, "%" + obatLama.toLowerCase() + "%");
                psI.setString(4, "%" + namaObatBaru.toLowerCase() + "%");
                ResultSet rsI = psI.executeQuery();
                while (rsI.next()) {
                    String tingkat = rsI.getString("tingkat_interaksi");
                    hasil.append("[").append(tingkat).append("] ")
                        .append(rsI.getString("komposisi1")).append(" <-> ").append(rsI.getString("komposisi2"))
                        .append("\n  ").append(rsI.getString("deskripsi_interaksi"))
                        .append("\n  Rekomendasi: ").append(rsI.getString("rekomendasi") == null ? "-" : rsI.getString("rekomendasi"))
                        .append("\n\n");
                }
                rsI.close(); psI.close();
            }
            if (hasil.length() > 0) {
                JOptionPane.showMessageDialog(null,
                    "POTENSI INTERAKSI OBAT TERDETEKSI!\n\n" + hasil.toString(),
                    "Alert Interaksi Obat", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) { System.out.println("Notif interaksi: " + e); }
    }

    // Lihat/cetak bukti pemberian (foto + tanda tangan) dari e-Nakes untuk sel jam terpilih
    private void lihatBukti(boolean cetak) {
        int row = tbMedikasi.getSelectedRow();
        int col = tbMedikasi.getSelectedColumn();
        if (row < 0) { JOptionPane.showMessageDialog(null, "Pilih baris obat & sel jam dulu."); return; }
        if (col < 14 || col >= 38) { JOptionPane.showMessageDialog(null, "Klik pada sel JAM (kolom pemberian) yang ingin dilihat buktinya."); return; }
        String jam = JAM_LABELS[col - 14];
        String noRw = tabModeMedikasi.getValueAt(row, 0).toString();
        String tglObs = tabModeMedikasi.getValueAt(row, 1).toString();
        String noUrut = tabModeMedikasi.getValueAt(row, 2).toString();
        String where = "where no_rawat='" + noRw + "' and tgl_observasi='" + tglObs + "' and no_urut=" + noUrut + " and jam='" + jam + "'";
        String photo = Sequel.cariIsi("select ifnull(photo,'') from kardek_obat_rscb_detail " + where);
        String ttd = Sequel.cariIsi("select ifnull(tandatangan,'') from kardek_obat_rscb_detail " + where);
        if ((photo == null || photo.isEmpty()) && (ttd == null || ttd.isEmpty())) {
            JOptionPane.showMessageDialog(null, "Belum ada bukti foto/tanda tangan untuk jam " + jam + ":00.");
            return;
        }
        String base = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/pemberianobat/";
        StringBuilder h = new StringBuilder("<html><body style='font-family:tahoma'><center>");
        h.append("<b>Bukti Pemberian Obat &mdash; Jam ").append(jam).append(":00</b><br>");
        h.append(tabModeMedikasi.getValueAt(row, 4)).append("<br><br>");
        if (photo != null && !photo.isEmpty()) h.append("Foto pemberian:<br><img src='").append(base).append(photo).append("' width='360'/><br><br>");
        if (ttd != null && !ttd.isEmpty()) h.append("Tanda tangan penerima:<br><img src='").append(base).append(ttd).append("' width='360'/>");
        h.append("</center></body></html>");
        javax.swing.JEditorPane pane = new javax.swing.JEditorPane("text/html", h.toString());
        pane.setEditable(false);
        if (cetak) {
            try { pane.print(); } catch (Exception e) { JOptionPane.showMessageDialog(null, "Notif cetak: " + e); }
        } else {
            javax.swing.JScrollPane sc = new javax.swing.JScrollPane(pane);
            sc.setPreferredSize(new java.awt.Dimension(430, 560));
            JOptionPane.showMessageDialog(null, sc, "Bukti Pemberian Obat", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void emptTeks() {
        emptTeksInput();
    }

    private void emptTeksInput() {
        TKodeDokter.setText(""); TNamaDokter.setText("");
        TNamaObat.setText(""); TKekuatan.setText("");
        TBentukSediaan.setText(""); TAturanPakai.setText("");
        TRutePemberian.setText(""); TKodeAB.setText("");
        ChkStop.setSelected(false); TglStop.setEnabled(false);
        chkHighAlert.setSelected(false); chkPRN.setSelected(false);
    }

    public void setNoRm(String norwt, Date tgl) {
        TNoRw.setText(norwt);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", norwt));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
        TJK.setText(Sequel.cariIsi("select if(jk='L','Laki-Laki','Perempuan') from pasien where no_rkm_medis=?", TNoRM.getText()));
        TTglLahir.setText(Sequel.cariIsi("select tgl_lahir from pasien where no_rkm_medis=?", TNoRM.getText()));
        TAlergi.setText(Sequel.cariIsi("select ifnull(group_concat(allergy_code separator ', '),'Tidak ada') from alergi_pasien where no_rawat=?", TNoRw.getText()));
        TRuangRawat.setText(Sequel.cariIsi("select b.nm_bangsal from kamar_inap ki inner join kamar k on ki.kd_kamar=k.kd_kamar inner join bangsal b on k.kd_bangsal=b.kd_bangsal where ki.no_rawat=? order by ki.tgl_masuk desc limit 1", norwt));
        // DPJP ranap diambil dari dpjp_ranap (bisa lebih dari 1 dokter),
        // bukan reg_periksa.kd_dokter yang cuma dokter saat pendaftaran
        String dpjp = Sequel.cariIsi("select group_concat(distinct d.nm_dokter separator ', ') "
            + "from dpjp_ranap dr inner join dokter d on dr.kd_dokter=d.kd_dokter where dr.no_rawat=?", norwt);
        if (dpjp == null || dpjp.trim().isEmpty()) {
            dpjp = Sequel.cariIsi("select d.nm_dokter from reg_periksa r inner join dokter d on r.kd_dokter=d.kd_dokter where r.no_rawat=?", norwt);
        }
        TDokter.setText(dpjp);
        TDokter.setToolTipText(dpjp);
        tampil();
    }

    public void isCek() {}
}
