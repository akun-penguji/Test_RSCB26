package rekammedis;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
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
import javax.swing.table.TableColumn;
import widget.*;

/**
 * Hitung Kassa & Instrumen Bedah (RM IBS) — pasangan desktop untuk form e-Nakes
 * "Hitung Kassa" (tabel hitung_kassa_instrumen_rscb). Satu tabel dengan e-Nakes,
 * jadi data yang diinput di web tampil di sini dan sebaliknya.
 */
public class RMHitungKassaInstrumen extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private Label LCount;

    private TextBox TxtKassaInsisi, TxtKassaTutup, TxtInstrumenInsisi, TxtInstrumenTutup, TxtJarumInsisi, TxtJarumTutup;
    private CekBox ChkSesuai;
    private widget.TextArea TxtTindakan;
    private TextBox TxtPerawatInstrumen, TxtPerawatSirkuler;
    private TextBox NIP, NamaPetugas;
    private Button BtnPetugas;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private int i = 0;
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);

    private widget.Table tbData;
    private DefaultTableModel tabMode;
    private ScrollPane Scroll;
    private CekBox ChkInput;
    private PanelBiasa FormInput;
    private ScrollPane ScrollInput;
    private JPanel PanelInput;
    public widget.editorpane LoadHTML;

    public RMHitungKassaInstrumen(java.awt.Frame parent, boolean modal) {
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
            "::[ Hitung Kassa & Instrumen Bedah ]::",
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
            new String[]{"No.Rawat", "No.R.M.", "Nama", "Tanggal",
                          "Kassa Insisi", "Kassa Tutup", "Instrmn Insisi", "Instrmn Tutup",
                          "Jarum Insisi", "Jarum Tutup", "Sesuai?",
                          "Perawat Instrumen", "Perawat Sirkuler", "NIP", "Petugas"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbData.setModel(tabMode);
        tbData.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] w = {105, 65, 150, 122, 80, 80, 90, 90, 80, 80, 60, 140, 140, 70, 150};
        for (i = 0; i < w.length; i++) {
            TableColumn column = tbData.getColumnModel().getColumn(i);
            column.setPreferredWidth(w[i]);
        }
        tbData.setDefaultRenderer(Object.class, new WarnaTable());

        tbData.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { if (tabMode.getRowCount() != 0) getData(); }
        });
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem mnCetak = new JMenuItem("Cetak Hitung Kassa & Instrumen");
        mnCetak.setIcon(new ImageIcon(getClass().getResource("/picture/b_print.png")));
        mnCetak.addActionListener(evt -> cetakHitungKassa());
        popupMenu.add(mnCetak);
        tbData.setComponentPopupMenu(popupMenu);
        Scroll.setViewportView(tbData);

        // FORM INPUT
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(880, 480));
        FormInput.setLayout(null);

        int y = 10;
        Label lblTgl = new Label(); lblTgl.setText("Tanggal :"); lblTgl.setBounds(0, y, 160, 23); FormInput.add(lblTgl);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setBounds(165, y, 150, 23); FormInput.add(TglAsuhan);

        y += 32;
        JLabel lblHdr = new JLabel("Jumlah (Sebelum Insisi / Sebelum Tutup Luka) :");
        lblHdr.setFont(new java.awt.Font("Tahoma", 1, 11)); lblHdr.setBounds(0, y, 400, 20); FormInput.add(lblHdr);

        y += 26;
        JLabel lblKolInsisi = new JLabel("Sebelum Insisi"); lblKolInsisi.setBounds(165, y, 120, 20); FormInput.add(lblKolInsisi);
        JLabel lblKolTutup = new JLabel("Sebelum Tutup"); lblKolTutup.setBounds(300, y, 120, 20); FormInput.add(lblKolTutup);

        y += 24;
        Label lblKassa = new Label(); lblKassa.setText("Kassa :"); lblKassa.setBounds(0, y, 160, 23); FormInput.add(lblKassa);
        TxtKassaInsisi = new TextBox(); TxtKassaInsisi.setBounds(165, y, 120, 23); FormInput.add(TxtKassaInsisi);
        TxtKassaTutup = new TextBox(); TxtKassaTutup.setBounds(300, y, 120, 23); FormInput.add(TxtKassaTutup);

        y += 30;
        Label lblInstrumen = new Label(); lblInstrumen.setText("Instrumen :"); lblInstrumen.setBounds(0, y, 160, 23); FormInput.add(lblInstrumen);
        TxtInstrumenInsisi = new TextBox(); TxtInstrumenInsisi.setBounds(165, y, 120, 23); FormInput.add(TxtInstrumenInsisi);
        TxtInstrumenTutup = new TextBox(); TxtInstrumenTutup.setBounds(300, y, 120, 23); FormInput.add(TxtInstrumenTutup);

        y += 30;
        Label lblJarum = new Label(); lblJarum.setText("Jarum :"); lblJarum.setBounds(0, y, 160, 23); FormInput.add(lblJarum);
        TxtJarumInsisi = new TextBox(); TxtJarumInsisi.setBounds(165, y, 120, 23); FormInput.add(TxtJarumInsisi);
        TxtJarumTutup = new TextBox(); TxtJarumTutup.setBounds(300, y, 120, 23); FormInput.add(TxtJarumTutup);

        y += 32;
        Label lblSesuai = new Label(); lblSesuai.setText("Jumlah Sesuai :"); lblSesuai.setBounds(0, y, 160, 23); FormInput.add(lblSesuai);
        ChkSesuai = new CekBox(); ChkSesuai.setText("Ya (Sesuai)"); ChkSesuai.setSelected(true);
        ChkSesuai.setBounds(165, y, 120, 23); FormInput.add(ChkSesuai);

        y += 30;
        Label lblTindakan = new Label(); lblTindakan.setText("Tindakan bila tidak sesuai :"); lblTindakan.setBounds(0, y, 160, 23); FormInput.add(lblTindakan);
        TxtTindakan = new widget.TextArea();
        JScrollPane spTindakan = new JScrollPane(TxtTindakan);
        spTindakan.setBounds(165, y, 500, 50); FormInput.add(spTindakan);

        y += 58;
        Label lblPerawatInst = new Label(); lblPerawatInst.setText("Perawat Instrumen :"); lblPerawatInst.setBounds(0, y, 160, 23); FormInput.add(lblPerawatInst);
        TxtPerawatInstrumen = new TextBox(); TxtPerawatInstrumen.setBounds(165, y, 300, 23); FormInput.add(TxtPerawatInstrumen);
        TxtPerawatInstrumen.setDocument(new batasInput((int) 100).getKata(TxtPerawatInstrumen));

        y += 30;
        Label lblPerawatSirk = new Label(); lblPerawatSirk.setText("Perawat Sirkuler :"); lblPerawatSirk.setBounds(0, y, 160, 23); FormInput.add(lblPerawatSirk);
        TxtPerawatSirkuler = new TextBox(); TxtPerawatSirkuler.setBounds(165, y, 300, 23); FormInput.add(TxtPerawatSirkuler);
        TxtPerawatSirkuler.setDocument(new batasInput((int) 100).getKata(TxtPerawatSirkuler));

        y += 30;
        Label lblPetugas = new Label(); lblPetugas.setText("Petugas :"); lblPetugas.setBounds(0, y, 160, 23); FormInput.add(lblPetugas);
        NIP = new TextBox(); NIP.setEditable(false); NIP.setBounds(165, y, 80, 23); FormInput.add(NIP);
        NIP.setDocument(new batasInput((byte) 20).getKata(NIP));
        NamaPetugas = new TextBox(); NamaPetugas.setEditable(false); NamaPetugas.setBounds(247, y, 200, 23); FormInput.add(NamaPetugas);
        BtnPetugas = new Button(); BtnPetugas.setIcon(new ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPetugas.setBounds(449, y, 28, 23); FormInput.add(BtnPetugas);
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
        ScrollInput.setPreferredSize(new Dimension(880, 480));
        PanelInput = new JPanel(new BorderLayout());
        PanelInput.setOpaque(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 500));
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
            } else if (NIP.getText().trim().isEmpty()) { Valid.textKosong(NIP, "Petugas");
            } else {
                if (Sequel.menyimpantf("hitung_kassa_instrumen_rscb", "?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 13, new String[]{
                    TNoRw.getText(),
                    Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19),
                    num(TxtKassaInsisi), num(TxtKassaTutup),
                    num(TxtInstrumenInsisi), num(TxtInstrumenTutup),
                    num(TxtJarumInsisi), num(TxtJarumTutup),
                    ChkSesuai.isSelected() ? "Ya" : "Tidak",
                    TxtTindakan.getText(),
                    TxtPerawatInstrumen.getText(), TxtPerawatSirkuler.getText(),
                    NIP.getText()
                })) { tampil(); emptTeks(); }
            }
        });

        BtnHapus.addActionListener(evt -> {
            if (tbData.getSelectedRow() != -1) {
                if (Sequel.queryu2tf("delete from hitung_kassa_instrumen_rscb where no_rawat=? and tanggal=?", 2, new String[]{
                    tbData.getValueAt(tbData.getSelectedRow(), 0).toString(),
                    tbData.getValueAt(tbData.getSelectedRow(), 3).toString()
                })) { tampil(); emptTeks(); }
            } else JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dihapus...!!!!");
        });

        BtnEdit.addActionListener(evt -> {
            if (TNoRw.getText().trim().isEmpty() || tbData.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            } else {
                if (Sequel.queryu2tf("delete from hitung_kassa_instrumen_rscb where no_rawat=? and tanggal=?", 2, new String[]{
                    tbData.getValueAt(tbData.getSelectedRow(), 0).toString(),
                    tbData.getValueAt(tbData.getSelectedRow(), 3).toString()
                })) { BtnSimpan.doClick(); }
            }
        });

        BtnPrint.addActionListener(evt -> cetakHitungKassa());

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

    private String num(widget.TextBox t) {
        String v = t.getText().trim();
        return v.matches("\\d+") ? v : "0";
    }

    private void isForm() {
        if (ChkInput.isSelected()) { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 500)); FormInput.setVisible(true); ChkInput.setVisible(true);
        } else { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 20)); FormInput.setVisible(false); ChkInput.setVisible(true); }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select hk.no_rawat, r.no_rkm_medis, p.nm_pasien, hk.tanggal, "+
                "hk.kassa_sebelum_insisi, hk.kassa_sebelum_tutup, hk.instrumen_sebelum_insisi, hk.instrumen_sebelum_tutup, "+
                "hk.jarum_sebelum_insisi, hk.jarum_sebelum_tutup, hk.jumlah_sesuai, "+
                "hk.perawat_instrumen, hk.perawat_sirkuler, hk.nip, ifnull(pt.nama,'') as nm_petugas "+
                "from hitung_kassa_instrumen_rscb hk "+
                "inner join reg_periksa r on hk.no_rawat=r.no_rawat "+
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "+
                "left join petugas pt on hk.nip=pt.nip "+
                "where hk.tanggal between ? and ? and (hk.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) "+
                "order by hk.tanggal desc");
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
            ps.setString(3, "%"+TCari.getText().trim()+"%");
            ps.setString(4, "%"+TCari.getText().trim()+"%");
            ps.setString(5, "%"+TCari.getText().trim()+"%");
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                    rs.getString("tanggal"),
                    rs.getString("kassa_sebelum_insisi"), rs.getString("kassa_sebelum_tutup"),
                    rs.getString("instrumen_sebelum_insisi"), rs.getString("instrumen_sebelum_tutup"),
                    rs.getString("jarum_sebelum_insisi"), rs.getString("jarum_sebelum_tutup"),
                    rs.getString("jumlah_sesuai"),
                    rs.getString("perawat_instrumen"), rs.getString("perawat_sirkuler"),
                    rs.getString("nip"), rs.getString("nm_petugas")
                });
            }
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif Hitung Kassa : " + e);
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
                ps = koneksi.prepareStatement("select * from hitung_kassa_instrumen_rscb where no_rawat=? and tanggal=?");
                ps.setString(1, tbData.getValueAt(tbData.getSelectedRow(), 0).toString());
                ps.setString(2, tbData.getValueAt(tbData.getSelectedRow(), 3).toString());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TxtKassaInsisi.setText(rs.getString("kassa_sebelum_insisi"));
                    TxtKassaTutup.setText(rs.getString("kassa_sebelum_tutup"));
                    TxtInstrumenInsisi.setText(rs.getString("instrumen_sebelum_insisi"));
                    TxtInstrumenTutup.setText(rs.getString("instrumen_sebelum_tutup"));
                    TxtJarumInsisi.setText(rs.getString("jarum_sebelum_insisi"));
                    TxtJarumTutup.setText(rs.getString("jarum_sebelum_tutup"));
                    ChkSesuai.setSelected(!"Tidak".equalsIgnoreCase(rs.getString("jumlah_sesuai")));
                    TxtTindakan.setText(rs.getString("tindakan_bila_tidak_sesuai") == null ? "" : rs.getString("tindakan_bila_tidak_sesuai"));
                    TxtPerawatInstrumen.setText(rs.getString("perawat_instrumen") == null ? "" : rs.getString("perawat_instrumen"));
                    TxtPerawatSirkuler.setText(rs.getString("perawat_sirkuler") == null ? "" : rs.getString("perawat_sirkuler"));
                    NIP.setText(rs.getString("nip") == null ? "" : rs.getString("nip"));
                    NamaPetugas.setText(Sequel.cariIsi("select nama from petugas where nip=?", NIP.getText()));
                }
            } catch (Exception e) { System.out.println("Notif getData Hitung Kassa : " + e);
            } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
        }
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date());
        TxtKassaInsisi.setText(""); TxtKassaTutup.setText("");
        TxtInstrumenInsisi.setText(""); TxtInstrumenTutup.setText("");
        TxtJarumInsisi.setText(""); TxtJarumTutup.setText("");
        ChkSesuai.setSelected(true);
        TxtTindakan.setText("");
        TxtPerawatInstrumen.setText(""); TxtPerawatSirkuler.setText("");
        TxtKassaInsisi.requestFocus();
    }

    public void setNoRm(String norwt, Date tgl) {
        TNoRw.setText(norwt); TglAsuhan.setDate(tgl);
        TCari.setText(norwt); DTPCari2.setDate(tgl);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", norwt));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
    }

    public void setNoRm(String norwt, String norm, String namapasien) {
        TNoRw.setText(norwt);
        TNoRM.setText(norm);
        TPasien.setText(namapasien);
        TCari.setText(norwt);
    }

    private void cetakHitungKassa() {
        if (tbData.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu..!!!");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String noRawat = tbData.getValueAt(tbData.getSelectedRow(), 0).toString();
        String tanggalVal = tbData.getValueAt(tbData.getSelectedRow(), 3).toString();
        String noRM = tbData.getValueAt(tbData.getSelectedRow(), 1).toString();
        String nmPasien = tbData.getValueAt(tbData.getSelectedRow(), 2).toString();

        String kaI = "", kaT = "", inI = "", inT = "", jaI = "", jaT = "", sesuai = "", tindakan = "", pInst = "", pSirk = "", nmPetugas = "";
        try {
            ps = koneksi.prepareStatement(
                "select hk.*, ifnull(pt.nama,'') as nm_petugas from hitung_kassa_instrumen_rscb hk "+
                "left join petugas pt on hk.nip=pt.nip where hk.no_rawat=? and hk.tanggal=?");
            ps.setString(1, noRawat); ps.setString(2, tanggalVal);
            rs = ps.executeQuery();
            if (rs.next()) {
                kaI = s(rs.getString("kassa_sebelum_insisi")); kaT = s(rs.getString("kassa_sebelum_tutup"));
                inI = s(rs.getString("instrumen_sebelum_insisi")); inT = s(rs.getString("instrumen_sebelum_tutup"));
                jaI = s(rs.getString("jarum_sebelum_insisi")); jaT = s(rs.getString("jarum_sebelum_tutup"));
                sesuai = s(rs.getString("jumlah_sesuai")); tindakan = s(rs.getString("tindakan_bila_tidak_sesuai"));
                pInst = s(rs.getString("perawat_instrumen")); pSirk = s(rs.getString("perawat_sirkuler"));
                nmPetugas = s(rs.getString("nm_petugas"));
            }
        } catch (Exception e) { System.out.println("Notif cetak hitung kassa : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }

        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>")
            .append("body{font-family:Tahoma;font-size:9px;margin:15px;color:#000;}")
            .append("table{border-collapse:collapse;}")
            .append(".hdr{width:100%;border-bottom:2px solid #000;padding-bottom:5px;margin-bottom:5px;}")
            .append(".rm-box{border:1px solid #000;padding:5px;text-align:center;font-weight:bold;font-size:9px;width:120px;}")
            .append(".main{width:100%;border:1px solid #000;}")
            .append(".main td,.main th{border:1px solid #000;padding:4px 6px;font-size:8px;}")
            .append(".main th{background:#e8e8e8;font-weight:bold;text-align:center;}")
            .append(".pi td{padding:1px 5px;font-size:8px;}")
            .append(".ttd{width:100%;margin-top:30px;}")
            .append(".ttd td{text-align:center;vertical-align:top;padding:5px;font-size:9px;}")
            .append("</style></head><body>");
        html.append("<table class='hdr'><tr><td style='text-align:center;'>")
            .append("<div style='font-size:7px;'>DINAS KESEHATAN KABUPATEN BEKASI</div>")
            .append("<div style='font-size:10px;font-weight:bold;'>RUMAH SAKIT UMUM DAERAH (RSUD)</div>")
            .append("<div style='font-size:11px;font-weight:bold;letter-spacing:5px;'>").append(akses.getnamars().toUpperCase().replace("RSUD","").replace("RUMAH SAKIT UMUM DAERAH","").trim()).append("</div>")
            .append("<div style='font-size:7px;'>").append(akses.getalamatrs()).append("</div>")
            .append("</td><td class='rm-box'>RM.IBS Hitung Kassa</td></tr></table>");
        html.append("<table style='width:100%;margin-bottom:5px;'><tr>")
            .append("<td style='font-size:12px;font-weight:bold;width:50%;'>SURGICAL SAFETY - HITUNG KASSA &amp; INSTRUMEN</td>")
            .append("<td><table class='pi'>")
            .append("<tr><td><b>No.RM</b></td><td>: ").append(noRM).append("</td></tr>")
            .append("<tr><td><b>Nama</b></td><td>: ").append(nmPasien).append("</td></tr>")
            .append("<tr><td><b>Tanggal</b></td><td>: ").append(tanggalVal).append("</td></tr>")
            .append("</table></td></tr></table>");
        html.append("<table class='main'>")
            .append("<tr><th>Item</th><th>Sebelum Insisi</th><th>Sebelum Tutup Luka</th></tr>")
            .append("<tr><td><b>Kassa</b></td><td style='text-align:center;'>").append(kaI).append("</td><td style='text-align:center;'>").append(kaT).append("</td></tr>")
            .append("<tr><td><b>Instrumen</b></td><td style='text-align:center;'>").append(inI).append("</td><td style='text-align:center;'>").append(inT).append("</td></tr>")
            .append("<tr><td><b>Jarum</b></td><td style='text-align:center;'>").append(jaI).append("</td><td style='text-align:center;'>").append(jaT).append("</td></tr>")
            .append("<tr><td><b>Jumlah Sesuai</b></td><td colspan='2'>").append(sesuai).append("</td></tr>")
            .append("<tr><td><b>Tindakan bila tidak sesuai</b></td><td colspan='2'>").append(tindakan).append("</td></tr>")
            .append("</table>");
        html.append("<table class='ttd'><tr>")
            .append("<td style='width:33%;'>Perawat Instrumen<br><br><br>( ").append(pInst).append(" )</td>")
            .append("<td style='width:33%;'>Perawat Sirkuler<br><br><br>( ").append(pSirk).append(" )</td>")
            .append("<td style='width:33%;'>Petugas<br><br><br>( ").append(nmPetugas).append(" )</td>")
            .append("</tr></table>");
        html.append("<script>window.onload=function(){window.print();}</script></body></html>");

        try {
            java.io.File tmpFile = java.io.File.createTempFile("hitung_kassa_", ".html");
            tmpFile.deleteOnExit();
            java.io.FileWriter fw = new java.io.FileWriter(tmpFile);
            fw.write(html.toString());
            fw.close();
            java.awt.Desktop.getDesktop().browse(tmpFile.toURI());
        } catch (Exception e) { System.out.println("Notif buka browser : " + e); }
        this.setCursor(Cursor.getDefaultCursor());
    }

    private String s(String v) { return v == null ? "" : v; }

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
