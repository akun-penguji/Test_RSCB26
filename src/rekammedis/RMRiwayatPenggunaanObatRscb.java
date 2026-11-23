package rekammedis;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import kepegawaian.DlgCariPetugas;
import kepegawaian.DlgCariDokter;
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

public class RMRiwayatPenggunaanObatRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private TextBox KdApoteker, NmApoteker, KdDokter, NmDokter;
    private TextBox TSumberInformasi, TTelpApoteker;
    private Button BtnApoteker, BtnDokter;
    private Label LCount;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);
    private DlgCariDokter dokterDlg = new DlgCariDokter(null, false);

    private widget.Table tbObat;
    private DefaultTableModel tabMode;
    private ScrollPane Scroll;
    private CekBox ChkInput;
    private PanelBiasa FormInput;
    private ScrollPane ScrollInput;
    private JPanel PanelInput;
    public widget.editorpane LoadHTML;

    // Two detail tables: Obat Resep and Obat Non Resep
    private JTable tbResep, tbNonResep;
    private DefaultTableModel tabModeResep, tabModeNonResep;

    private final String[] detailColumns = {"No","Nama Generik","Nama Dagang","Regimen","Sumber Obat","Tgl Mulai","Tgl Stop","Jmlh Tersisa","Status Admisi","Status Pulang"};

    public RMRiwayatPenggunaanObatRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(950, 700);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Riwayat Penggunaan Obat ]::",
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
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","DPJP","Sumber Informasi","Apoteker","Telp Apoteker"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbObat.setModel(tabMode);
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { if (tabMode.getRowCount() != 0) getData(); }
        });
        Scroll.setViewportView(tbObat);

        // FORM INPUT
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(920, 700));
        FormInput.setLayout(null);

        int y = 10;
        JLabel lblHeader = new JLabel("RIWAYAT PENGGUNAAN OBAT");
        lblHeader.setFont(new java.awt.Font("Tahoma", 1, 13)); lblHeader.setBounds(10, y, 350, 20); FormInput.add(lblHeader);
        JLabel lblKode = new JLabel("RM.FAR.03.002.2019");
        lblKode.setFont(new java.awt.Font("Tahoma", 0, 8)); lblKode.setBounds(750, y, 150, 20); FormInput.add(lblKode);

        y += 32;
        Label lblTgl = new Label(); lblTgl.setText("Tanggal :"); lblTgl.setBounds(10, y, 80, 23); FormInput.add(lblTgl);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setBounds(95, y, 130, 23); FormInput.add(TglAsuhan);

        y += 32;
        Label lblDPJP = new Label(); lblDPJP.setText("DPJP :"); lblDPJP.setBounds(10, y, 80, 23); FormInput.add(lblDPJP);
        KdDokter = new TextBox(); KdDokter.setBounds(95, y, 80, 23); FormInput.add(KdDokter);
        NmDokter = new TextBox(); NmDokter.setBounds(177, y, 180, 23); FormInput.add(NmDokter);
        BtnDokter = new Button(); BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnDokter.setBounds(359, y, 28, 23); FormInput.add(BtnDokter);
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

        y += 32;
        Label lblSumber = new Label(); lblSumber.setText("Sumber Info :"); lblSumber.setBounds(10, y, 80, 23); FormInput.add(lblSumber);
        TSumberInformasi = new TextBox(); TSumberInformasi.setBounds(95, y, 200, 23); FormInput.add(TSumberInformasi);

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
                    TTelpApoteker.setText(Sequel.cariIsi("select no_telp from petugas where nip=?", KdApoteker.getText()));
                }
            }
        });

        y += 32;
        Label lblTelp = new Label(); lblTelp.setText("Telp Apoteker :"); lblTelp.setBounds(10, y, 85, 23); FormInput.add(lblTelp);
        TTelpApoteker = new TextBox(); TTelpApoteker.setEditable(false); TTelpApoteker.setBounds(95, y, 150, 23); FormInput.add(TTelpApoteker);

        // Obat Resep table
        y += 32;
        JLabel lblResep = new JLabel("Obat Resep :");
        lblResep.setFont(new java.awt.Font("Tahoma", 1, 11)); lblResep.setBounds(10, y, 200, 20); FormInput.add(lblResep);
        Button BtnTambahResep = new Button(); BtnTambahResep.setText("+Row"); BtnTambahResep.setBounds(120, y, 70, 20);
        BtnTambahResep.addActionListener(evt -> tabModeResep.addRow(new Object[]{"","","","","","","","","",""}));
        FormInput.add(BtnTambahResep);
        Button BtnHapusResep = new Button(); BtnHapusResep.setText("-Row"); BtnHapusResep.setBounds(195, y, 70, 20);
        BtnHapusResep.addActionListener(evt -> { if (tbResep.getSelectedRow() != -1) tabModeResep.removeRow(tbResep.getSelectedRow()); });
        FormInput.add(BtnHapusResep);

        y += 22;
        tabModeResep = new DefaultTableModel(new Object[][]{{"","","","","","","","","",""}}, detailColumns);
        tbResep = new JTable(tabModeResep);
        JScrollPane scrollResep = new JScrollPane(tbResep);
        scrollResep.setBounds(10, y, 890, 130); FormInput.add(scrollResep);

        // Obat Non Resep table
        y += 140;
        JLabel lblNonResep = new JLabel("Obat Non Resep :");
        lblNonResep.setFont(new java.awt.Font("Tahoma", 1, 11)); lblNonResep.setBounds(10, y, 200, 20); FormInput.add(lblNonResep);
        Button BtnTambahNonResep = new Button(); BtnTambahNonResep.setText("+Row"); BtnTambahNonResep.setBounds(150, y, 70, 20);
        BtnTambahNonResep.addActionListener(evt -> tabModeNonResep.addRow(new Object[]{"","","","","","","","","",""}));
        FormInput.add(BtnTambahNonResep);
        Button BtnHapusNonResep = new Button(); BtnHapusNonResep.setText("-Row"); BtnHapusNonResep.setBounds(225, y, 70, 20);
        BtnHapusNonResep.addActionListener(evt -> { if (tbNonResep.getSelectedRow() != -1) tabModeNonResep.removeRow(tbNonResep.getSelectedRow()); });
        FormInput.add(BtnHapusNonResep);

        y += 22;
        tabModeNonResep = new DefaultTableModel(new Object[][]{{"","","","","","","","","",""}}, detailColumns);
        tbNonResep = new JTable(tabModeNonResep);
        JScrollPane scrollNonResep = new JScrollPane(tbNonResep);
        scrollNonResep.setBounds(10, y, 890, 130); FormInput.add(scrollNonResep);

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
        ScrollInput.setPreferredSize(new Dimension(920, 550));
        PanelInput = new JPanel(new BorderLayout());
        PanelInput.setOpaque(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 570));
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
            } else if (KdDokter.getText().trim().equals("")) { Valid.textKosong(KdDokter, "DPJP");
            } else {
                if (tbResep.isEditing()) tbResep.getCellEditor().stopCellEditing();
                if (tbNonResep.isEditing()) tbNonResep.getCellEditor().stopCellEditing();
                String tanggal = Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19);
                if (Sequel.menyimpantf("riwayat_penggunaan_obat_rscb", "?,?,?,?,?,?", "Data", 6, new String[]{
                    TNoRw.getText(), tanggal, KdDokter.getText(), TSumberInformasi.getText(),
                    KdApoteker.getText(), TTelpApoteker.getText()
                })) {
                    saveDetail(tabModeResep, TNoRw.getText(), tanggal, "Resep");
                    saveDetail(tabModeNonResep, TNoRw.getText(), tanggal, "Non Resep");
                    tampil(); emptTeks();
                }
            }
        });

        BtnHapus.addActionListener(evt -> {
            if (tbObat.getSelectedRow() != -1) {
                String noRawat = tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString();
                String tgl = tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString();
                Sequel.queryu2tf("delete from riwayat_penggunaan_obat_rscb_detail where no_rawat=? and tanggal=?", 2, new String[]{noRawat, tgl});
                if (Sequel.queryu2tf("delete from riwayat_penggunaan_obat_rscb where no_rawat=? and tanggal=?", 2, new String[]{noRawat, tgl})) {
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
                Sequel.queryu2tf("delete from riwayat_penggunaan_obat_rscb_detail where no_rawat=? and tanggal=?", 2, new String[]{noRawat, tgl});
                if (Sequel.queryu2tf("delete from riwayat_penggunaan_obat_rscb where no_rawat=? and tanggal=?", 2, new String[]{noRawat, tgl})) {
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
                String nipApt = KdApoteker.getText().isEmpty() ? Sequel.cariIsi("select nip_apoteker from riwayat_penggunaan_obat_rscb where no_rawat=?", TNoRw.getText()) : KdApoteker.getText();
                String finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", nipApt);
                param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + NmApoteker.getText() + "\nID " + (finger.isEmpty() ? nipApt : finger));
                String tglCetak = tbObat.getSelectedRow()>-1 ? tbObat.getValueAt(tbObat.getSelectedRow(),3).toString() : "";
                Valid.MyReportqry("rptRiwayatPenggunaanObatRscb.jasper", "report", "::[ Riwayat Penggunaan Obat ]::",
                    "select dt.jenis,dt.no_urut,dt.nama_generik,dt.nama_dagang,dt.regimen,dt.sumber_obat,dt.tgl_mulai,dt.tgl_stop,dt.jmlh_obat_tersisa,dt.status_admisi,dt.status_pulang," +
                    "h.no_rawat,h.tanggal,h.sumber_informasi,h.telp_apoteker,r.no_rkm_medis,p.nm_pasien,p.tgl_lahir,dk.nm_dokter,pt.nama as nm_apoteker " +
                    "from riwayat_penggunaan_obat_rscb_detail dt " +
                    "inner join riwayat_penggunaan_obat_rscb h on dt.no_rawat=h.no_rawat and dt.tanggal=h.tanggal " +
                    "inner join reg_periksa r on h.no_rawat=r.no_rawat " +
                    "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                    "left join dokter dk on h.kd_dokter=dk.kd_dokter " +
                    "left join petugas pt on h.nip_apoteker=pt.nip " +
                    "where h.no_rawat='" + TNoRw.getText() + "'" +
                    (tglCetak.isEmpty() ? "" : " and h.tanggal='" + tglCetak + "'") +
                    " order by dt.jenis,dt.no_urut", param);
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

    private void saveDetail(DefaultTableModel model, String noRawat, String tanggal, String jenis) {
        for (int i = 0; i < model.getRowCount(); i++) {
            String namaGenerik = model.getValueAt(i, 1).toString();
            if (!namaGenerik.trim().equals("")) {
                int noUrut;
                try { noUrut = Integer.parseInt(model.getValueAt(i, 0).toString()); } catch (Exception e) { noUrut = i + 1; }
                Sequel.menyimpantf("riwayat_penggunaan_obat_rscb_detail", "?,?,?,?,?,?,?,?,?,?,?,?,?", "Detail", 13, new String[]{
                    noRawat, tanggal, jenis, String.valueOf(noUrut), namaGenerik,
                    model.getValueAt(i, 2).toString(), model.getValueAt(i, 3).toString(),
                    model.getValueAt(i, 4).toString(), model.getValueAt(i, 5).toString(),
                    model.getValueAt(i, 6).toString(), model.getValueAt(i, 7).toString(),
                    model.getValueAt(i, 8).toString(), model.getValueAt(i, 9).toString()
                });
            }
        }
    }

    private void isForm() {
        if (ChkInput.isSelected()) { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 570)); FormInput.setVisible(true); ChkInput.setVisible(true);
        } else { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 20)); FormInput.setVisible(false); ChkInput.setVisible(true); }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select h.no_rawat, r.no_rkm_medis, p.nm_pasien, h.tanggal, d.nm_dokter, h.sumber_informasi, " +
                "pt.nama as nm_petugas, h.telp_apoteker " +
                "from riwayat_penggunaan_obat_rscb h inner join reg_periksa r on h.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis left join dokter d on h.kd_dokter=d.kd_dokter " +
                "left join petugas pt on h.nip_apoteker=pt.nip " +
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
                    rs.getString("tanggal"), rs.getString("nm_dokter"), rs.getString("sumber_informasi"),
                    rs.getString("nm_petugas"), rs.getString("telp_apoteker")
                });
            }
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif Riwayat Penggunaan Obat : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            try { TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString())); } catch (Exception e) {}
            KdDokter.setText(Sequel.cariIsi("select kd_dokter from riwayat_penggunaan_obat_rscb where no_rawat='"+
                tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()+"' and tanggal='"+tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()+"'"));
            NmDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
            TSumberInformasi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            KdApoteker.setText(Sequel.cariIsi("select nip_apoteker from riwayat_penggunaan_obat_rscb where no_rawat='"+
                tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()+"' and tanggal='"+tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString()+"'"));
            NmApoteker.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
            TTelpApoteker.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString());
            loadDetail();
        }
    }

    private void loadDetail() {
        Valid.tabelKosong(tabModeResep);
        Valid.tabelKosong(tabModeNonResep);
        try {
            java.sql.PreparedStatement ps2 = koneksi.prepareStatement(
                "select * from riwayat_penggunaan_obat_rscb_detail where no_rawat=? and tanggal=? order by jenis, no_urut");
            ps2.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            ps2.setString(2, tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
            java.sql.ResultSet rs2 = ps2.executeQuery();
            while (rs2.next()) {
                String status = rs2.getString("status");
                String statusAdmisi = "", statusPulang = "";
                if (status != null && status.contains("|")) {
                    String[] parts = status.split("\\|", -1);
                    statusAdmisi = parts[0]; statusPulang = parts.length > 1 ? parts[1] : "";
                }
                Object[] row = new Object[]{
                    rs2.getString("no_urut"), rs2.getString("nama_generik"), rs2.getString("nama_dagang"),
                    rs2.getString("regimen"), rs2.getString("sumber_obat"), rs2.getString("tgl_mulai"),
                    rs2.getString("tgl_stop"), rs2.getString("jmlh_tersisa"), statusAdmisi, statusPulang
                };
                if ("Resep".equals(rs2.getString("jenis"))) tabModeResep.addRow(row);
                else tabModeNonResep.addRow(row);
            }
            rs2.close(); ps2.close();
        } catch (Exception e) { System.out.println("Notif Detail : " + e); }
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date()); KdDokter.setText(""); NmDokter.setText("");
        TSumberInformasi.setText(""); KdApoteker.setText(""); NmApoteker.setText(""); TTelpApoteker.setText("");
        Valid.tabelKosong(tabModeResep); tabModeResep.addRow(new Object[]{"","","","","","","","","",""});
        Valid.tabelKosong(tabModeNonResep); tabModeNonResep.addRow(new Object[]{"","","","","","","","","",""});
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
