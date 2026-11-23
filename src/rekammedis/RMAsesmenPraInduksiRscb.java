package rekammedis;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
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

/**
 * Form "Asesmen Pra Induksi" (RM.IBS.11.012.2026) — RSUD Cabang Bungin.
 * Terpisah dari Asesmen Pra Anestesi (PAB 4: pra-anestesi & pra-induksi dicatat
 * terpisah). Disimpan ke tabel asesmen_pra_induksi_rscb per (no_rawat, tanggal).
 * Dibuka dari DlgBookingOperasi. Gaya UI + tombol CRUD mengikuti Khanza
 * (InternalFrame titled-border, FormInput null-layout, tabel WarnaTable).
 * nip = petugas login (owner). Menyatu dgn form web e-Dokter (tabel sama).
 */
public class RMAsesmenPraInduksiRscb extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private Label LCount;

    private TextBox TDiagnosa, TTindakanBedah;
    private TextBox KdAsisten, NmAsisten, KdPenata, NmPenata;
    private Button BtnAsisten, BtnPenata;
    private TextBox KdDokterBedah, NmDokterBedah, KdDokter, NmDokter;
    private Button BtnDokterBedah, BtnDokter;
    private kepegawaian.DlgCariPetugas petugasAsisten = new kepegawaian.DlgCariPetugas(null, false);
    private kepegawaian.DlgCariPetugas petugasPenata = new kepegawaian.DlgCariPetugas(null, false);
    private CekBox ChkAlergi; private TextBox TAlergiKet;
    private CekBox ChkKesulitan; private TextBox TKesulitanKet;
    private CekBox ChkResiko, ChkSediaDarah; private TextBox TSediaJenis, TSediaMl;
    private ComboBox CmbMallampati, CmbBukaMulut; private CekBox ChkLeher;
    private ComboBox CmbASA;
    private TextBox TRencanaGA, TRencanaRA;
    private ComboBox CmbPuasa; private TextBox TPuasaTerakhir;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariDokter dokterBedahDlg = new DlgCariDokter(null, false);
    // Dokter/Penata Anestesi: bisa dokter ATAU penata anestesi -> cari via pegawai
    // (kolom kd_dokter kini menyimpan NIK pegawai, FK ke tabel pegawai).
    private kepegawaian.DlgCariPegawai pegawaiAnesDlg = new kepegawaian.DlgCariPegawai(null, false);

    private widget.Table tbData;
    private DefaultTableModel tabMode;
    private ScrollPane Scroll;
    private CekBox ChkInput;
    private PanelBiasa FormInput;
    private ScrollPane ScrollInput;
    private JPanel PanelInput;

    public RMAsesmenPraInduksiRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(950, 700);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Asesmen Pra Induksi ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // PANEL ATAS
        panelisi panelAtas = new panelisi();
        panelAtas.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 4));
        panelAtas.setPreferredSize(new Dimension(1024, 33));
        Label lblNoRw = new Label(); lblNoRw.setText("No.Rawat :");
        TNoRw = new TextBox(); TNoRw.setPreferredSize(new Dimension(130, 23)); TNoRw.setEditable(false);
        TNoRM = new TextBox(); TNoRM.setPreferredSize(new Dimension(100, 23)); TNoRM.setEditable(false);
        TPasien = new TextBox(); TPasien.setPreferredSize(new Dimension(220, 23)); TPasien.setEditable(false);
        panelAtas.add(lblNoRw); panelAtas.add(TNoRw); panelAtas.add(TNoRM); panelAtas.add(TPasien);

        // TABLE
        Scroll = new ScrollPane();
        tbData = new widget.Table();
        tabMode = new DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","Diagnosis","ASA","Dokter Anestesi"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbData.setModel(tabMode);
        tbData.setDefaultRenderer(Object.class, new fungsi.WarnaTable());
        tbData.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { if (tabMode.getRowCount() != 0) getData(); }
        });
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem mnCetak = new JMenuItem("Cetak Asesmen Pra Induksi");
        mnCetak.addActionListener(evt -> BtnPrint.doClick());
        popupMenu.add(mnCetak);
        tbData.setComponentPopupMenu(popupMenu);
        Scroll.setViewportView(tbData);

        // FORM INPUT (null-layout ala Khanza)
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(920, 720));
        FormInput.setLayout(null);

        int y = 10;
        JLabel lblHeader = new JLabel("ASESMEN PRA INDUKSI");
        lblHeader.setFont(new java.awt.Font("Tahoma", 1, 13)); lblHeader.setBounds(10, y, 400, 20); FormInput.add(lblHeader);

        // === IDENTITAS TINDAKAN ===
        y += 28;
        FormInput.add(sec("IDENTITAS TINDAKAN", y));
        y += 25;
        FormInput.add(lbl("Tanggal :", 10, y, 130));
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setBounds(140, y, 150, 23); FormInput.add(TglAsuhan);
        y += 27;
        FormInput.add(lbl("Diagnosis Prabedah :", 10, y, 130));
        TDiagnosa = new TextBox(); TDiagnosa.setBounds(140, y, 400, 23); FormInput.add(TDiagnosa);
        y += 27;
        FormInput.add(lbl("Tindakan Bedah :", 10, y, 130));
        TTindakanBedah = new TextBox(); TTindakanBedah.setBounds(140, y, 400, 23); FormInput.add(TTindakanBedah);
        y += 27;
        FormInput.add(lbl("Dokter Bedah :", 10, y, 130));
        KdDokterBedah = new TextBox(); KdDokterBedah.setBounds(140, y, 80, 23); FormInput.add(KdDokterBedah);
        NmDokterBedah = new TextBox(); NmDokterBedah.setBounds(222, y, 250, 23); NmDokterBedah.setEditable(false); FormInput.add(NmDokterBedah);
        BtnDokterBedah = new Button(); try { BtnDokterBedah.setIcon(new ImageIcon(getClass().getResource("/picture/190.png"))); } catch (Exception e) {}
        BtnDokterBedah.setBounds(474, y, 28, 23); FormInput.add(BtnDokterBedah);
        BtnDokterBedah.addActionListener(evt -> {
            dokterBedahDlg.emptTeks(); dokterBedahDlg.isCek();
            dokterBedahDlg.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20);
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
        y += 27;
        FormInput.add(lbl("Asisten Bedah :", 10, y, 130));
        KdAsisten = new TextBox(); KdAsisten.setBounds(140, y, 80, 23); KdAsisten.setEditable(false); FormInput.add(KdAsisten);
        NmAsisten = new TextBox(); NmAsisten.setBounds(222, y, 250, 23); NmAsisten.setEditable(false); FormInput.add(NmAsisten);
        BtnAsisten = new Button(); BtnAsisten.setToolTipText("Cari Petugas (Asisten Bedah)");
        try { BtnAsisten.setIcon(new ImageIcon(getClass().getResource("/picture/attachment.png"))); } catch (Exception e) {}
        BtnAsisten.setBounds(474, y, 28, 23); FormInput.add(BtnAsisten);
        BtnAsisten.addActionListener(evt -> { petugasAsisten.setLocationRelativeTo(null); petugasAsisten.setVisible(true); });
        petugasAsisten.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (petugasAsisten.getTable().getSelectedRow() != -1) {
                    KdAsisten.setText(petugasAsisten.getTable().getValueAt(petugasAsisten.getTable().getSelectedRow(), 0).toString());
                    NmAsisten.setText(petugasAsisten.getTable().getValueAt(petugasAsisten.getTable().getSelectedRow(), 1).toString());
                }
            }
        });
        y += 27;
        FormInput.add(lbl("Dokter/Penata Anestesi :", 10, y, 130));
        KdDokter = new TextBox(); KdDokter.setBounds(140, y, 80, 23); FormInput.add(KdDokter);
        NmDokter = new TextBox(); NmDokter.setBounds(222, y, 250, 23); NmDokter.setEditable(false); FormInput.add(NmDokter);
        BtnDokter = new Button(); try { BtnDokter.setIcon(new ImageIcon(getClass().getResource("/picture/190.png"))); } catch (Exception e) {}
        BtnDokter.setBounds(474, y, 28, 23); FormInput.add(BtnDokter);
        BtnDokter.addActionListener(evt -> {
            pegawaiAnesDlg.emptTeks();
            pegawaiAnesDlg.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20);
            pegawaiAnesDlg.setLocationRelativeTo(internalFrame1); pegawaiAnesDlg.setVisible(true);
        });
        pegawaiAnesDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (pegawaiAnesDlg.getTable().getSelectedRow() != -1) {
                    KdDokter.setText(pegawaiAnesDlg.getTable().getValueAt(pegawaiAnesDlg.getTable().getSelectedRow(), 0).toString());
                    NmDokter.setText(pegawaiAnesDlg.getTable().getValueAt(pegawaiAnesDlg.getTable().getSelectedRow(), 1).toString());
                }
            }
        });
        y += 27;
        FormInput.add(lbl("Penata Anestesi :", 10, y, 130));
        KdPenata = new TextBox(); KdPenata.setBounds(140, y, 80, 23); KdPenata.setEditable(false); FormInput.add(KdPenata);
        NmPenata = new TextBox(); NmPenata.setBounds(222, y, 250, 23); NmPenata.setEditable(false); FormInput.add(NmPenata);
        BtnPenata = new Button(); BtnPenata.setToolTipText("Cari Petugas (Penata Anestesi)");
        try { BtnPenata.setIcon(new ImageIcon(getClass().getResource("/picture/attachment.png"))); } catch (Exception e) {}
        BtnPenata.setBounds(474, y, 28, 23); FormInput.add(BtnPenata);
        BtnPenata.addActionListener(evt -> { petugasPenata.setLocationRelativeTo(null); petugasPenata.setVisible(true); });
        petugasPenata.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (petugasPenata.getTable().getSelectedRow() != -1) {
                    KdPenata.setText(petugasPenata.getTable().getValueAt(petugasPenata.getTable().getSelectedRow(), 0).toString());
                    NmPenata.setText(petugasPenata.getTable().getValueAt(petugasPenata.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

        // === 1. RIWAYAT ALERGI ===
        y += 33;
        FormInput.add(sec("1. RIWAYAT ALERGI", y));
        y += 25;
        ChkAlergi = ck("Ada riwayat alergi", 10, y, 160); FormInput.add(ChkAlergi);
        FormInput.add(lbl("Sebutkan :", 180, y, 65));
        TAlergiKet = new TextBox(); TAlergiKet.setBounds(247, y, 300, 23); FormInput.add(TAlergiKet);

        // === 2. KESULITAN JALAN NAFAS ===
        y += 33;
        FormInput.add(sec("2. KEMUNGKINAN KESULITAN JALAN NAFAS", y));
        y += 25;
        ChkKesulitan = ck("Ada kemungkinan", 10, y, 160); FormInput.add(ChkKesulitan);
        FormInput.add(lbl("Antisipasi :", 180, y, 70));
        TKesulitanKet = new TextBox(); TKesulitanKet.setBounds(252, y, 295, 23); FormInput.add(TKesulitanKet);

        // === 3. RESIKO PERDARAHAN ===
        y += 33;
        FormInput.add(sec("3. RESIKO PERDARAHAN > 500 ml", y));
        y += 25;
        ChkResiko = ck("Resiko perdarahan > 500 ml", 10, y, 200); FormInput.add(ChkResiko);
        y += 25;
        ChkSediaDarah = ck("Sedia darah", 10, y, 110); FormInput.add(ChkSediaDarah);
        FormInput.add(lbl("Jenis :", 130, y, 45));
        TSediaJenis = new TextBox(); TSediaJenis.setBounds(177, y, 150, 23); FormInput.add(TSediaJenis);
        FormInput.add(lbl("Jumlah (ml) :", 337, y, 80));
        TSediaMl = new TextBox(); TSediaMl.setBounds(419, y, 100, 23); FormInput.add(TSediaMl);

        // === 4. PENILAIAN JALAN NAFAS ===
        y += 33;
        FormInput.add(sec("4. PENILAIAN JALAN NAFAS", y));
        y += 25;
        FormInput.add(lbl("Mallampati :", 10, y, 130));
        CmbMallampati = new ComboBox(); CmbMallampati.setModel(new DefaultComboBoxModel(new String[]{"","1","2","3","4"}));
        CmbMallampati.setBounds(140, y, 80, 23); FormInput.add(CmbMallampati);
        FormInput.add(lbl("Buka Mulut :", 240, y, 80));
        CmbBukaMulut = new ComboBox(); CmbBukaMulut.setModel(new DefaultComboBoxModel(new String[]{"","> 3 jari","< 3 jari"}));
        CmbBukaMulut.setBounds(322, y, 120, 23); FormInput.add(CmbBukaMulut);
        y += 25;
        ChkLeher = ck("Pergerakan leher terbatas", 10, y, 200); FormInput.add(ChkLeher);

        // === 5. STATUS FISIK ASA ===
        y += 33;
        FormInput.add(sec("5. STATUS FISIK ASA", y));
        y += 25;
        FormInput.add(lbl("ASA :", 10, y, 130));
        CmbASA = new ComboBox(); CmbASA.setModel(new DefaultComboBoxModel(new String[]{"","1","2","3","4","5","E"}));
        CmbASA.setBounds(140, y, 80, 23); FormInput.add(CmbASA);

        // === 6. RENCANA ANESTESI ===
        y += 33;
        FormInput.add(sec("6. RENCANA ANESTESI", y));
        y += 25;
        FormInput.add(lbl("General Anestesi :", 10, y, 130));
        TRencanaGA = new TextBox(); TRencanaGA.setBounds(140, y, 400, 23); FormInput.add(TRencanaGA);
        y += 27;
        FormInput.add(lbl("Regional Anestesi :", 10, y, 130));
        TRencanaRA = new TextBox(); TRencanaRA.setBounds(140, y, 400, 23); FormInput.add(TRencanaRA);

        // === 7. PUASA ===
        y += 33;
        FormInput.add(sec("7. PUASA", y));
        y += 25;
        FormInput.add(lbl("Status Puasa :", 10, y, 130));
        CmbPuasa = new ComboBox(); CmbPuasa.setModel(new DefaultComboBoxModel(new String[]{"","Belum","Sudah"}));
        CmbPuasa.setBounds(140, y, 120, 23); FormInput.add(CmbPuasa);
        FormInput.add(lbl("Makan/minum terakhir :", 280, y, 140));
        TPuasaTerakhir = new TextBox(); TPuasaTerakhir.setBounds(422, y, 180, 23); FormInput.add(TPuasaTerakhir);

        // Input toggle
        ChkInput = new CekBox();
        try {
            ChkInput.setIcon(new ImageIcon(getClass().getResource("/picture/143.png")));
            ChkInput.setRolloverIcon(new ImageIcon(getClass().getResource("/picture/143.png")));
            ChkInput.setRolloverSelectedIcon(new ImageIcon(getClass().getResource("/picture/145.png")));
            ChkInput.setSelectedIcon(new ImageIcon(getClass().getResource("/picture/145.png")));
        } catch (Exception e) {}
        ChkInput.setMnemonic('1'); ChkInput.setText(".: Input Data"); ChkInput.setToolTipText("Alt+1");
        ChkInput.setBorderPainted(true); ChkInput.setBorderPaintedFlat(true); ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(SwingConstants.RIGHT);
        ChkInput.setPreferredSize(new Dimension(192, 20));
        ChkInput.setSelected(true);
        ChkInput.addActionListener(evt -> isForm());

        ScrollInput = new ScrollPane(); ScrollInput.setViewportView(FormInput);
        ScrollInput.setPreferredSize(new Dimension(920, 620));
        PanelInput = new JPanel(new BorderLayout());
        PanelInput.setOpaque(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 640));
        PanelInput.add(ScrollInput, BorderLayout.CENTER); PanelInput.add(ChkInput, BorderLayout.PAGE_END);

        // PANEL BAWAH
        panelBawah = new panelisi(); panelBawah.setPreferredSize(new Dimension(44, 54));
        panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));
        BtnSimpan = btn("Simpan", 'S', "/picture/save-16x16.png");
        BtnBatal  = btn("Baru",   'B', "/picture/Cancel-2-16x16.png");
        BtnHapus  = btn("Hapus",  'H', "/picture/stop_f2.png");
        BtnEdit   = btn("Ganti",  'G', "/picture/inventaris.png");
        BtnPrint  = btn("Cetak",  'T', "/picture/b_print.png");
        BtnKeluar = btn("Keluar", 'K', "/picture/exit.png");
        Label lblTglC = new Label(); lblTglC.setText("Tanggal :"); lblTglC.setPreferredSize(new Dimension(55, 23));
        DTPCari1 = new widget.Tanggal(); DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setPreferredSize(new Dimension(90, 23));
        Label lblSd = new Label(); lblSd.setText("s.d."); lblSd.setPreferredSize(new Dimension(25, 23));
        DTPCari2 = new widget.Tanggal(); DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setPreferredSize(new Dimension(90, 23));
        Label lblKey = new Label(); lblKey.setText("Key Word :"); lblKey.setPreferredSize(new Dimension(70, 23));
        TCari = new TextBox(); TCari.setPreferredSize(new Dimension(150, 23));
        BtnCari = new Button(); try { BtnCari.setIcon(new ImageIcon(getClass().getResource("/picture/accept.png"))); } catch (Exception e) {}
        BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnAll = new Button(); try { BtnAll.setIcon(new ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); } catch (Exception e) {}
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
        BtnSimpan.addActionListener(evt -> simpan());
        BtnHapus.addActionListener(evt -> {
            if (tbData.getSelectedRow() != -1) {
                if (JOptionPane.showConfirmDialog(this, "Hapus asesmen pra induksi ini?", "Konfirmasi",
                        JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
                if (Sequel.queryu2tf("delete from asesmen_pra_induksi_rscb where no_rawat=? and tanggal=?", 2, new String[]{
                    tbData.getValueAt(tbData.getSelectedRow(), 0).toString(),
                    tbData.getValueAt(tbData.getSelectedRow(), 3).toString()
                })) { tampil(); emptTeks(); }
            } else JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dihapus...!!!!");
        });
        BtnEdit.addActionListener(evt -> {
            if (TNoRw.getText().trim().equals("") || tbData.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            } else {
                if (Sequel.queryu2tf("delete from asesmen_pra_induksi_rscb where no_rawat=? and tanggal=?", 2, new String[]{
                    tbData.getValueAt(tbData.getSelectedRow(), 0).toString(),
                    tbData.getValueAt(tbData.getSelectedRow(), 3).toString()
                })) { BtnSimpan.doClick(); }
            }
        });
        BtnPrint.addActionListener(evt -> cetak());

        // ASSEMBLY
        InternalFrame internalFrameData = new InternalFrame();
        internalFrameData.setLayout(new BorderLayout());
        internalFrameData.setBackground(new Color(255, 255, 255));
        // Form mengisi seluruh area (CENTER, scroll penuh); daftar record jadi
        // strip kecil di bawah (SOUTH) — supaya semua field kelihatan/terscroll.
        internalFrameData.add(PanelInput, BorderLayout.CENTER);
        Scroll.setPreferredSize(new Dimension(920, 120));
        internalFrameData.add(Scroll, BorderLayout.SOUTH);
        internalFrame1.add(panelAtas, BorderLayout.PAGE_START);
        internalFrame1.add(internalFrameData, BorderLayout.CENTER);
        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);
        setLayout(new BorderLayout());
        add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
    }

    private void simpan() {
        if (TNoRw.getText().trim().equals("")) { Valid.textKosong(TNoRw, "Pasien"); return; }
        if (KdDokter.getText().trim().equals("")) { Valid.textKosong(KdDokter, "Dokter Anestesi"); return; }
        String tgl = Valid.SetTgl(TglAsuhan.getSelectedItem()+"")+" "+TglAsuhan.getSelectedItem().toString().substring(11,19);
        // Urutan nilai HARUS = urutan kolom tabel asesmen_pra_induksi_rscb (25 kolom).
        if (Sequel.menyimpantf("asesmen_pra_induksi_rscb",
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 25, new String[]{
            TNoRw.getText(),
            tgl,
            KdDokter.getText(),
            KdDokterBedah.getText(),
            akses.getkode(),
            TDiagnosa.getText(),
            TTindakanBedah.getText(),
            NmAsisten.getText(),
            NmPenata.getText(),
            ChkAlergi.isSelected() ? "Ya" : "Tidak",
            TAlergiKet.getText(),
            ChkKesulitan.isSelected() ? "Ya" : "Tidak",
            TKesulitanKet.getText(),
            ChkResiko.isSelected() ? "Ya" : "Tidak",
            ChkSediaDarah.isSelected() ? "Ya" : "Tidak",
            TSediaJenis.getText(),
            TSediaMl.getText(),
            CmbMallampati.getSelectedItem().toString(),
            CmbBukaMulut.getSelectedItem().toString(),
            ChkLeher.isSelected() ? "Ya" : "Tidak",
            CmbASA.getSelectedItem().toString(),
            TRencanaGA.getText(),
            TRencanaRA.getText(),
            CmbPuasa.getSelectedItem().toString(),
            TPuasaTerakhir.getText()
        })) { tampil(); emptTeks(); }
    }

    private void cetak() {
        if (TNoRw.getText().trim().equals("") || tbData.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu..!!!");
            return;
        }
        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars()); param.put("alamatrs", akses.getalamatrs());
        param.put("propinsirs", akses.getpropinsirs()); param.put("kabupatenrs", akses.getkabupatenrs());
        param.put("kontakrs", akses.getkontakrs()); param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        Valid.MyReportqry("rptAsesmenPraInduksiRscb.jasper", "report", "::[ Asesmen Pra Induksi ]::",
            "select s.*, r.no_rkm_medis, p.nm_pasien, p.tgl_lahir, p.jk, "+
            "db.nm_dokter as nm_dokter_bedah, da.nama as nm_dokter_anestesi "+
            "from asesmen_pra_induksi_rscb s inner join reg_periksa r on s.no_rawat=r.no_rawat "+
            "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "+
            "left join dokter db on s.kd_dokter_bedah=db.kd_dokter "+
            "left join pegawai da on s.kd_dokter=da.nik "+
            "where s.no_rawat='"+TNoRw.getText()+"' and s.tanggal='"+
            tbData.getValueAt(tbData.getSelectedRow(), 3).toString()+"'", param);
    }

    private void isForm() {
        if (ChkInput.isSelected()) { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 640)); FormInput.setVisible(true); ChkInput.setVisible(true);
        } else { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 20)); FormInput.setVisible(false); ChkInput.setVisible(true); }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select s.no_rawat, r.no_rkm_medis, p.nm_pasien, s.tanggal, s.diagnosa_prabedah, "+
                "s.asa, da.nama as nm_dokter_anestesi "+
                "from asesmen_pra_induksi_rscb s inner join reg_periksa r on s.no_rawat=r.no_rawat "+
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis "+
                "left join pegawai da on s.kd_dokter=da.nik "+
                "where s.tanggal between ? and ? and (s.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) "+
                "order by s.tanggal desc");
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
            ps.setString(3, "%"+TCari.getText().trim()+"%");
            ps.setString(4, "%"+TCari.getText().trim()+"%");
            ps.setString(5, "%"+TCari.getText().trim()+"%");
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                    rs.getString("tanggal"), rs.getString("diagnosa_prabedah"),
                    rs.getString("asa"), rs.getString("nm_dokter_anestesi")
                });
            }
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif Asesmen Pra Induksi : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void getData() {
        if (tbData.getSelectedRow() == -1) return;
        TNoRw.setText(tbData.getValueAt(tbData.getSelectedRow(), 0).toString());
        TNoRM.setText(tbData.getValueAt(tbData.getSelectedRow(), 1).toString());
        TPasien.setText(tbData.getValueAt(tbData.getSelectedRow(), 2).toString());
        try { TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
            tbData.getValueAt(tbData.getSelectedRow(), 3).toString())); } catch (Exception e) {}
        try {
            ps = koneksi.prepareStatement("select * from asesmen_pra_induksi_rscb where no_rawat=? and tanggal=?");
            ps.setString(1, tbData.getValueAt(tbData.getSelectedRow(), 0).toString());
            ps.setString(2, tbData.getValueAt(tbData.getSelectedRow(), 3).toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                KdDokter.setText(rs.getString("kd_dokter"));
                NmDokter.setText(Sequel.cariIsi("select nama from pegawai where nik=?", rs.getString("kd_dokter")));
                KdDokterBedah.setText(rs.getString("kd_dokter_bedah"));
                NmDokterBedah.setText(Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", rs.getString("kd_dokter_bedah")));
                TDiagnosa.setText(rs.getString("diagnosa_prabedah"));
                TTindakanBedah.setText(rs.getString("tindakan_bedah"));
                NmAsisten.setText(rs.getString("asisten_bedah"));
                KdAsisten.setText(Sequel.cariIsi("select nip from petugas where nama=? limit 1", rs.getString("asisten_bedah")));
                NmPenata.setText(rs.getString("penata_anestesi"));
                KdPenata.setText(Sequel.cariIsi("select nip from petugas where nama=? limit 1", rs.getString("penata_anestesi")));
                ChkAlergi.setSelected("Ya".equals(rs.getString("alergi")));
                TAlergiKet.setText(rs.getString("alergi_ket"));
                ChkKesulitan.setSelected("Ya".equals(rs.getString("kesulitan_jalan_nafas")));
                TKesulitanKet.setText(rs.getString("kesulitan_jalan_nafas_ket"));
                ChkResiko.setSelected("Ya".equals(rs.getString("resiko_perdarahan")));
                ChkSediaDarah.setSelected("Ya".equals(rs.getString("sedia_darah")));
                TSediaJenis.setText(rs.getString("sedia_darah_jenis"));
                TSediaMl.setText(rs.getString("sedia_darah_ml"));
                CmbMallampati.setSelectedItem(rs.getString("mallampati"));
                CmbBukaMulut.setSelectedItem(rs.getString("buka_mulut"));
                ChkLeher.setSelected("Ya".equals(rs.getString("pergerakan_leher")));
                CmbASA.setSelectedItem(rs.getString("asa"));
                TRencanaGA.setText(rs.getString("rencana_ga"));
                TRencanaRA.setText(rs.getString("rencana_ra"));
                CmbPuasa.setSelectedItem(rs.getString("puasa"));
                TPuasaTerakhir.setText(rs.getString("puasa_terakhir"));
            }
        } catch (Exception e) { System.out.println("Notif getData Asesmen Pra Induksi : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date());
        TDiagnosa.setText(""); TTindakanBedah.setText("");
        KdAsisten.setText(""); NmAsisten.setText(""); KdPenata.setText(""); NmPenata.setText("");
        KdDokterBedah.setText(""); NmDokterBedah.setText(""); KdDokter.setText(""); NmDokter.setText("");
        ChkAlergi.setSelected(false); TAlergiKet.setText("");
        ChkKesulitan.setSelected(false); TKesulitanKet.setText("");
        ChkResiko.setSelected(false); ChkSediaDarah.setSelected(false); TSediaJenis.setText(""); TSediaMl.setText("");
        CmbMallampati.setSelectedIndex(0); CmbBukaMulut.setSelectedIndex(0); ChkLeher.setSelected(false);
        CmbASA.setSelectedIndex(0);
        TRencanaGA.setText(""); TRencanaRA.setText("");
        CmbPuasa.setSelectedIndex(0); TPuasaTerakhir.setText("");
    }

    public void setNoRm(String norwt, Date tgl) {
        TNoRw.setText(norwt); TglAsuhan.setDate(tgl == null ? new Date() : tgl);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", norwt));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
        loadFromBookingOperasi(norwt);
    }

    public void setNoRm(String norwt, String norm, String nama) {
        TNoRw.setText(norwt); TNoRM.setText(norm); TPasien.setText(nama);
        TglAsuhan.setDate(new Date());
        loadFromBookingOperasi(norwt);
    }

    private void loadFromBookingOperasi(String noRawat) {
        try {
            ps = koneksi.prepareStatement(
                "select po.nm_perawatan, bo.kd_dokter " +
                "from booking_operasi bo " +
                "left join paket_operasi po on bo.kode_paket=po.kode_paket " +
                "where bo.no_rawat=? order by bo.tanggal desc limit 1");
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getString("nm_perawatan") != null && TTindakanBedah.getText().trim().equals(""))
                    TTindakanBedah.setText(rs.getString("nm_perawatan"));
                String kdBedah = rs.getString("kd_dokter");
                if (kdBedah != null && !kdBedah.trim().equals("") && KdDokterBedah.getText().trim().equals("")) {
                    KdDokterBedah.setText(kdBedah);
                    NmDokterBedah.setText(Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", kdBedah));
                }
            }
        } catch (Exception e) { System.out.println("Notif loadFromBookingOperasi PraInduksi : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getrujukan_keluar()); BtnHapus.setEnabled(akses.getrujukan_keluar());
        BtnEdit.setEnabled(akses.getrujukan_keluar()); BtnPrint.setEnabled(akses.getrujukan_keluar());
    }

    // ---- helper UI ----
    private Label lbl(String t, int x, int y, int w) {
        Label l = new Label(); l.setText(t); l.setBounds(x, y, w, 23); return l;
    }
    private JLabel sec(String t, int y) {
        JLabel l = new JLabel(t); l.setFont(new java.awt.Font("Tahoma", 1, 11)); l.setBounds(10, y, 400, 20); return l;
    }
    private CekBox ck(String t, int x, int y, int w) {
        CekBox c = new CekBox(); c.setText(t); c.setBounds(x, y, w, 23); c.setSelected(false); c.setOpaque(false); return c;
    }
    private Button btn(String teks, char mn, String ikon) {
        Button b = new Button(); b.setText(teks); b.setMnemonic(mn); b.setPreferredSize(new Dimension(100, 30));
        try { b.setIcon(new ImageIcon(getClass().getResource(ikon))); } catch (Exception e) {}
        return b;
    }
}
