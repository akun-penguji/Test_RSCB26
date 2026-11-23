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

public class RMRekonsObatTransferRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private TextBox KdApoteker, NmApoteker;
    private JRadioButton RdAlergiYa, RdAlergiTidak;
    private TextBox TAlergiKet;
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

    private JTable tbDetail;
    private DefaultTableModel tabModeDetail;
    private JScrollPane scrollDetail;

    public RMRekonsObatTransferRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(900, 650);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Rekonsiliasi Obat Saat Transfer ]::",
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
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","Alergi","Ket Alergi","Apoteker"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbObat.setModel(tabMode);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { if (tabMode.getRowCount() != 0) getData(); }
        });
        Scroll.setViewportView(tbObat);

        // FORM INPUT
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(880, 520));
        FormInput.setLayout(null);

        int y = 10;
        JLabel lblHeader = new JLabel("REKONSILIASI OBAT SAAT TRANSFER");
        lblHeader.setFont(new java.awt.Font("Tahoma", 1, 13)); lblHeader.setBounds(10, y, 400, 20); FormInput.add(lblHeader);
        JLabel lblKode = new JLabel("RM.FAR.03.015.2019");
        lblKode.setFont(new java.awt.Font("Tahoma", 0, 8)); lblKode.setBounds(700, y, 150, 20); FormInput.add(lblKode);

        y += 32;
        Label lblTgl = new Label(); lblTgl.setText("Tanggal :"); lblTgl.setBounds(10, y, 80, 23); FormInput.add(lblTgl);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setBounds(95, y, 130, 23); FormInput.add(TglAsuhan);

        y += 32;
        Label lblAlergi = new Label(); lblAlergi.setText("Alergi :"); lblAlergi.setBounds(10, y, 80, 23); FormInput.add(lblAlergi);
        RdAlergiYa = new JRadioButton("Ya"); RdAlergiYa.setBounds(95, y, 60, 23); RdAlergiYa.setOpaque(false); FormInput.add(RdAlergiYa);
        RdAlergiTidak = new JRadioButton("Tidak"); RdAlergiTidak.setBounds(160, y, 70, 23); RdAlergiTidak.setOpaque(false); RdAlergiTidak.setSelected(true); FormInput.add(RdAlergiTidak);
        ButtonGroup bgAlergi = new ButtonGroup(); bgAlergi.add(RdAlergiYa); bgAlergi.add(RdAlergiTidak);
        Label lblKetAlergi = new Label(); lblKetAlergi.setText("Ket :"); lblKetAlergi.setBounds(240, y, 30, 23); FormInput.add(lblKetAlergi);
        TAlergiKet = new TextBox(); TAlergiKet.setBounds(275, y, 200, 23); FormInput.add(TAlergiKet);

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

        // Detail table
        y += 32;
        JLabel lblDetail = new JLabel("Detail Obat :");
        lblDetail.setFont(new java.awt.Font("Tahoma", 1, 11)); lblDetail.setBounds(10, y, 200, 20); FormInput.add(lblDetail);
        Button BtnTambahRow = new Button(); BtnTambahRow.setText("+Row"); BtnTambahRow.setBounds(120, y, 70, 20);
        BtnTambahRow.addActionListener(evt -> tabModeDetail.addRow(new Object[]{"","","","","","",""}));
        FormInput.add(BtnTambahRow);
        Button BtnHapusRow = new Button(); BtnHapusRow.setText("-Row"); BtnHapusRow.setBounds(195, y, 70, 20);
        BtnHapusRow.addActionListener(evt -> { if (tbDetail.getSelectedRow() != -1) tabModeDetail.removeRow(tbDetail.getSelectedRow()); });
        FormInput.add(BtnHapusRow);

        y += 22;
        tabModeDetail = new DefaultTableModel(new Object[][]{{"","","","","","",""}},
            new String[]{"No","Nama Obat","Dosis","Frekuensi","Cara Pemberian","Tindak Lanjut","Perubahan Aturan Pakai"});
        tbDetail = new JTable(tabModeDetail);
        // ComboBox for Tindak Lanjut column
        JComboBox<String> cmbTindakLanjut = new JComboBox<>(new String[]{"Lanjut Aturan Pakai Sama","Berubah","Stop"});
        tbDetail.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(cmbTindakLanjut));
        scrollDetail = new JScrollPane(tbDetail);
        scrollDetail.setBounds(10, y, 850, 180); FormInput.add(scrollDetail);

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
            } else if (KdApoteker.getText().trim().equals("")) { Valid.textKosong(KdApoteker, "Apoteker");
            } else {
                if (tbDetail.isEditing()) tbDetail.getCellEditor().stopCellEditing();
                String tanggal = Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19);
                String alergi = RdAlergiYa.isSelected() ? "Ya" : "Tidak";
                if (Sequel.menyimpantf("rekonsiliasi_obat_transfer_rscb", "?,?,?,?,?", "Data", 5, new String[]{
                    TNoRw.getText(), tanggal, alergi, TAlergiKet.getText(), KdApoteker.getText()
                })) {
                    int noUrut = 1;
                    for (int i = 0; i < tabModeDetail.getRowCount(); i++) {
                        String namaObat = tabModeDetail.getValueAt(i, 1).toString();
                        if (!namaObat.trim().equals("")) {
                            Sequel.menyimpantf("rekonsiliasi_obat_transfer_rscb_detail", "?,?,?,?,?,?,?,?,?", "Detail", 9, new String[]{
                                TNoRw.getText(), tanggal, String.valueOf(noUrut), namaObat,
                                tabModeDetail.getValueAt(i, 2).toString(), tabModeDetail.getValueAt(i, 3).toString(),
                                tabModeDetail.getValueAt(i, 4).toString(), tabModeDetail.getValueAt(i, 5).toString(),
                                tabModeDetail.getValueAt(i, 6).toString()
                            });
                            noUrut++;
                        }
                    }
                    tampil(); emptTeks();
                }
            }
        });

        BtnHapus.addActionListener(evt -> {
            if (tbObat.getSelectedRow() != -1) {
                String noRawat = tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString();
                String tgl = tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString();
                Sequel.queryu2tf("delete from rekonsiliasi_obat_transfer_rscb_detail where no_rawat=? and tanggal=?", 2, new String[]{noRawat, tgl});
                if (Sequel.queryu2tf("delete from rekonsiliasi_obat_transfer_rscb where no_rawat=? and tanggal=?", 2, new String[]{noRawat, tgl})) {
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
                Sequel.queryu2tf("delete from rekonsiliasi_obat_transfer_rscb_detail where no_rawat=? and tanggal=?", 2, new String[]{noRawat, tgl});
                if (Sequel.queryu2tf("delete from rekonsiliasi_obat_transfer_rscb where no_rawat=? and tanggal=?", 2, new String[]{noRawat, tgl})) {
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
                String nipApt = Sequel.cariIsi("select nip_apoteker from rekonsiliasi_obat_transfer_rscb where no_rawat=?", TNoRw.getText());
                String finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", nipApt);
                param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + NmApoteker.getText() + "\nID " + (finger.isEmpty() ? nipApt : finger));
                String tglCetak = tbObat.getSelectedRow()>-1 ? tbObat.getValueAt(tbObat.getSelectedRow(),3).toString() : "";
                Valid.MyReportqry("rptRekonsObatTransferRscb.jasper", "report", "::[ Rekonsiliasi Obat Transfer ]::",
                    "select d.no_urut, d.nama_obat, d.dosis, d.frekuensi, d.cara_pemberian, d.tindak_lanjut, d.perubahan_aturan, " +
                    "h.no_rawat, h.tanggal, h.alergi, h.alergi_ket, r.no_rkm_medis, p.nm_pasien, p.tgl_lahir, pt.nama as nm_apoteker " +
                    "from rekonsiliasi_obat_transfer_rscb_detail d " +
                    "inner join rekonsiliasi_obat_transfer_rscb h on d.no_rawat=h.no_rawat and d.tanggal=h.tanggal " +
                    "inner join reg_periksa r on h.no_rawat=r.no_rawat " +
                    "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                    "left join petugas pt on h.nip_apoteker=pt.nip " +
                    "where h.no_rawat='" + TNoRw.getText() + "'" +
                    (tglCetak.isEmpty() ? "" : " and h.tanggal='" + tglCetak + "'") +
                    " order by d.no_urut", param);
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
                "select h.no_rawat, r.no_rkm_medis, p.nm_pasien, h.tanggal, h.alergi, h.alergi_ket, pt.nama as nm_petugas " +
                "from rekonsiliasi_obat_transfer_rscb h inner join reg_periksa r on h.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis left join petugas pt on h.nip_apoteker=pt.nip " +
                "where h.tanggal between ? and ? and (h.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) " +
                "order by h.tanggal desc");
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
            ps.setString(3, "%"+TCari.getText().trim()+"%");
            ps.setString(4, "%"+TCari.getText().trim()+"%");
            ps.setString(5, "%"+TCari.getText().trim()+"%");
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                    rs.getString("tanggal"), rs.getString("alergi"), rs.getString("alergi_ket"), rs.getString("nm_petugas")
                });
            }
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif Rekons Obat Transfer : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            try { TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString())); } catch (Exception e) {}
            if (tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString().equals("Ya")) RdAlergiYa.setSelected(true);
            else RdAlergiTidak.setSelected(true);
            TAlergiKet.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            KdApoteker.setText(Sequel.cariIsi("select nip from rekonsiliasi_obat_transfer_rscb where no_rawat='"+
                tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()+"' and tanggal='"+tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()+"'"));
            NmApoteker.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
            // Load detail
            Valid.tabelKosong(tabModeDetail);
            try {
                java.sql.PreparedStatement ps2 = koneksi.prepareStatement(
                    "select * from rekonsiliasi_obat_transfer_rscb_detail where no_rawat=? and tanggal=? order by no_urut");
                ps2.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                ps2.setString(2, tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
                java.sql.ResultSet rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    tabModeDetail.addRow(new Object[]{
                        rs2.getString("no_urut"), rs2.getString("nama_obat"), rs2.getString("dosis"),
                        rs2.getString("frekuensi"), rs2.getString("cara_pemberian"),
                        rs2.getString("tindak_lanjut"), rs2.getString("perubahan_aturan_pakai")
                    });
                }
                rs2.close(); ps2.close();
            } catch (Exception e) { System.out.println("Notif Detail : " + e); }
        }
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date()); RdAlergiTidak.setSelected(true); TAlergiKet.setText("");
        KdApoteker.setText(""); NmApoteker.setText("");
        Valid.tabelKosong(tabModeDetail);
        tabModeDetail.addRow(new Object[]{"","","","","","",""});
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
