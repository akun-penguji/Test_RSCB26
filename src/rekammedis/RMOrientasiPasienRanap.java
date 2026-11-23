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

/**
 * Form "Penerimaan & Orientasi Pasien Baru Rawat Inap" — RSUD Cabang Bungin (AKP 2 & 2d).
 * Data penerimaan pasien masuk ranap + checklist edukasi & orientasi ruangan.
 * Disimpan ke tabel orientasi_pasien_ranap_rscb per (no_rawat, tanggal).
 * Dibuka dari DlgKamarInap (Rawat Inap). Tabel dipakai bersama e-Nakes simrscb.
 */
public class RMOrientasiPasienRanap extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2, TglOrientasi;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private TextBox TRuang, TNamaPenerima, THubungan;
    private widget.ComboBox CmbPenerima;
    private TextBox TKdPetugas, TNmPetugas; private Button BtnPetugas;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);
    // Penerimaan
    private widget.ComboBox CmbCaraMasuk, CmbCaraDatang, CmbAlergi, CmbConsent;
    private TextBox TKondisiMasuk, TDiagnosaMasuk, TDpjp, TPjNama, TPjHubungan, TAlergiKet, TBarang;
    private TextArea TKeterangan;
    private Label LCount;

    // 16 item checklist orientasi
    private CekBox oPetugas, oJamVisite, oTempatTidur, oBel, oKamarMandi, oLemari, oListrik, oJamBesuk,
                   oMerokok, oBarang, oGelang, oJatuh, oCuciTangan, oEvakuasi, oHakKewajiban, oKomplain;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;

    private widget.Table tbData;
    private DefaultTableModel tabMode;
    private ScrollPane Scroll;
    private CekBox ChkInput;
    private PanelBiasa FormInput;
    private ScrollPane ScrollInput;
    private JPanel PanelInput;

    public RMOrientasiPasienRanap(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(960, 720);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Penerimaan & Orientasi Pasien Baru Rawat Inap ]::",
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
        TPasien = new TextBox(); TPasien.setPreferredSize(new Dimension(240, 23)); TPasien.setEditable(false);
        panelAtas.add(lblNoRw); panelAtas.add(TNoRw); panelAtas.add(TNoRM); panelAtas.add(TPasien);

        // TABLE
        Scroll = new ScrollPane();
        tbData = new widget.Table();
        tabMode = new DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","Ruang","Cara Masuk","Orientasi","Petugas"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbData.setModel(tabMode);
        tbData.setDefaultRenderer(Object.class, new fungsi.WarnaTable());
        tbData.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { if (tabMode.getRowCount() != 0) getData(); }
        });
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem mnCetak = new JMenuItem("Cetak Penerimaan & Orientasi Pasien");
        mnCetak.addActionListener(evt -> BtnPrint.doClick());
        popupMenu.add(mnCetak);
        tbData.setComponentPopupMenu(popupMenu);
        Scroll.setViewportView(tbData);

        // FORM INPUT
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(920, 820));
        FormInput.setLayout(null);

        int y = 10;
        JLabel lblHeader = new JLabel("FORMULIR PENERIMAAN & ORIENTASI PASIEN BARU RAWAT INAP");
        lblHeader.setFont(new java.awt.Font("Tahoma", 1, 13)); lblHeader.setBounds(10, y, 600, 20); FormInput.add(lblHeader);

        y += 28;
        FormInput.add(lbl("Tanggal / Jam :", 10, y, 120));
        TglOrientasi = new widget.Tanggal(); TglOrientasi.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglOrientasi.setBounds(140, y, 170, 23); FormInput.add(TglOrientasi);
        FormInput.add(lbl("Ruang Rawat :", 330, y, 100));
        TRuang = new TextBox(); TRuang.setBounds(432, y, 320, 23); FormInput.add(TRuang);

        // ===== PENERIMAAN =====
        y += 30; FormInput.add(sec("A. PENERIMAAN PASIEN", y)); y += 24;
        FormInput.add(lbl("Cara Masuk :", 10, y, 120));
        CmbCaraMasuk = combo(new String[]{"IGD", "Poliklinik", "Rujukan", "Datang Sendiri", "Pindahan Ruangan"});
        CmbCaraMasuk.setBounds(140, y, 170, 23); FormInput.add(CmbCaraMasuk);
        FormInput.add(lbl("Cara Datang :", 330, y, 100));
        CmbCaraDatang = combo(new String[]{"Jalan", "Kursi Roda", "Brankar"});
        CmbCaraDatang.setBounds(432, y, 160, 23); FormInput.add(CmbCaraDatang);
        y += 27;
        FormInput.add(lbl("Keadaan Umum :", 10, y, 120));
        TKondisiMasuk = new TextBox(); TKondisiMasuk.setBounds(140, y, 612, 23); FormInput.add(TKondisiMasuk);
        y += 27;
        FormInput.add(lbl("Diagnosa Masuk :", 10, y, 120));
        TDiagnosaMasuk = new TextBox(); TDiagnosaMasuk.setBounds(140, y, 612, 23); FormInput.add(TDiagnosaMasuk);
        y += 27;
        FormInput.add(lbl("DPJP :", 10, y, 120));
        TDpjp = new TextBox(); TDpjp.setBounds(140, y, 300, 23); FormInput.add(TDpjp);
        FormInput.add(lbl("General Consent :", 452, y, 100));
        CmbConsent = combo(new String[]{"Belum", "Sudah"});
        CmbConsent.setBounds(554, y, 100, 23); FormInput.add(CmbConsent);
        y += 27;
        FormInput.add(lbl("Penanggung Jawab :", 10, y, 120));
        TPjNama = new TextBox(); TPjNama.setBounds(140, y, 240, 23); FormInput.add(TPjNama);
        FormInput.add(lbl("Hubungan :", 392, y, 65));
        TPjHubungan = new TextBox(); TPjHubungan.setBounds(459, y, 195, 23); FormInput.add(TPjHubungan);
        y += 27;
        FormInput.add(lbl("Alergi :", 10, y, 120));
        CmbAlergi = combo(new String[]{"Tidak", "Ya"});
        CmbAlergi.setBounds(140, y, 90, 23); FormInput.add(CmbAlergi);
        FormInput.add(lbl("Sebutkan :", 240, y, 65));
        TAlergiKet = new TextBox(); TAlergiKet.setBounds(307, y, 200, 23); FormInput.add(TAlergiKet);
        y += 27;
        FormInput.add(lbl("Barang Bawaan :", 10, y, 120));
        TBarang = new TextBox(); TBarang.setBounds(140, y, 612, 23); FormInput.add(TBarang);

        // ===== ORIENTASI penerima =====
        y += 30; FormInput.add(sec("B. ORIENTASI — Diberikan Kepada", y)); y += 24;
        FormInput.add(lbl("Penerima :", 10, y, 120));
        CmbPenerima = combo(new String[]{"Pasien", "Keluarga"});
        CmbPenerima.setBounds(140, y, 120, 23); FormInput.add(CmbPenerima);
        FormInput.add(lbl("Nama :", 270, y, 45));
        TNamaPenerima = new TextBox(); TNamaPenerima.setBounds(317, y, 200, 23); FormInput.add(TNamaPenerima);
        FormInput.add(lbl("Hubungan :", 525, y, 70));
        THubungan = new TextBox(); THubungan.setBounds(597, y, 155, 23); FormInput.add(THubungan);
        y += 27;
        FormInput.add(lbl("Petugas/Perawat :", 10, y, 120));
        TKdPetugas = new TextBox(); TKdPetugas.setBounds(140, y, 100, 23); FormInput.add(TKdPetugas);
        BtnPetugas = new Button(); try { BtnPetugas.setIcon(new ImageIcon(getClass().getResource("/picture/190.png"))); } catch (Exception e) {}
        BtnPetugas.setBounds(242, y, 28, 23); FormInput.add(BtnPetugas);
        BtnPetugas.addActionListener(evt -> bukaPetugas());
        TNmPetugas = new TextBox(); TNmPetugas.setBounds(272, y, 300, 23); TNmPetugas.setEditable(false); FormInput.add(TNmPetugas);

        // Checklist
        y += 30; FormInput.add(sec("CHECKLIST ORIENTASI (beri tanda pada yang sudah dijelaskan)", y)); y += 24;
        oPetugas      = addCk("1. Perkenalan perawat penanggung jawab & DPJP", 20, y);
        oJamVisite    = addCk("2. Jam visite dokter", 470, y); y += 24;
        oTempatTidur  = addCk("3. Tempat tidur & pengaman (cara naik/turun)", 20, y);
        oBel          = addCk("4. Bel panggilan perawat (nurse call)", 470, y); y += 24;
        oKamarMandi   = addCk("5. Kamar mandi / toilet", 20, y);
        oLemari       = addCk("6. Lemari / tempat penyimpanan pasien", 470, y); y += 24;
        oListrik      = addCk("7. Lampu / AC / TV", 20, y);
        oJamBesuk     = addCk("8. Jam besuk & jumlah penunggu", 470, y); y += 24;
        oMerokok      = addCk("9. Larangan merokok di area RS", 20, y);
        oBarang       = addCk("10. Barang berharga (tanggung jawab pasien)", 470, y); y += 24;
        oGelang       = addCk("11. Gelang identitas pasien", 20, y);
        oJatuh        = addCk("12. Pencegahan risiko jatuh", 470, y); y += 24;
        oCuciTangan   = addCk("13. Cara & 5 momen cuci tangan (hand hygiene)", 20, y);
        oEvakuasi     = addCk("14. Jalur evakuasi & titik kumpul / APAR", 470, y); y += 24;
        oHakKewajiban = addCk("15. Hak & kewajiban pasien", 20, y);
        oKomplain     = addCk("16. Cara menyampaikan komplain / informasi", 470, y); y += 30;

        FormInput.add(lbl("Keterangan :", 10, y, 100));
        TKeterangan = new TextArea(); TKeterangan.setBounds(140, y, 612, 46); FormInput.add(TKeterangan); y += 52;

        // ASSEMBLY input toggle
        ChkInput = new CekBox();
        try {
            ChkInput.setIcon(new ImageIcon(getClass().getResource("/picture/143.png")));
            ChkInput.setSelectedIcon(new ImageIcon(getClass().getResource("/picture/145.png")));
        } catch (Exception e) {}
        ChkInput.setMnemonic('1'); ChkInput.setText(".: Input Data"); ChkInput.setToolTipText("Alt+1");
        ChkInput.setBorderPainted(true); ChkInput.setBorderPaintedFlat(true); ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(SwingConstants.LEFT); ChkInput.setHorizontalTextPosition(SwingConstants.RIGHT);
        ChkInput.setPreferredSize(new Dimension(192, 20)); ChkInput.setSelected(true);
        ChkInput.addActionListener(evt -> isForm());

        ScrollInput = new ScrollPane(); ScrollInput.setViewportView(FormInput);
        ScrollInput.setPreferredSize(new Dimension(920, 520));
        PanelInput = new JPanel(new BorderLayout());
        PanelInput.setOpaque(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 540));
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
                if (JOptionPane.showConfirmDialog(this, "Hapus data ini?", "Konfirmasi",
                        JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
                if (Sequel.queryu2tf("delete from orientasi_pasien_ranap_rscb where no_rawat=? and tanggal=?", 2, new String[]{
                    tbData.getValueAt(tbData.getSelectedRow(), 0).toString(),
                    tbData.getValueAt(tbData.getSelectedRow(), 3).toString()
                })) { tampil(); emptTeks(); }
            } else JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dihapus...!!!!");
        });
        BtnEdit.addActionListener(evt -> {
            if (TNoRw.getText().trim().equals("") || tbData.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            } else {
                if (Sequel.queryu2tf("delete from orientasi_pasien_ranap_rscb where no_rawat=? and tanggal=?", 2, new String[]{
                    tbData.getValueAt(tbData.getSelectedRow(), 0).toString(),
                    tbData.getValueAt(tbData.getSelectedRow(), 3).toString()
                })) { BtnSimpan.doClick(); }
            }
        });
        BtnPrint.addActionListener(evt -> cetak());
        petugas.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (petugas.getTable().getSelectedRow() != -1) {
                    TKdPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                    TNmPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                }
            }
        });

        // ASSEMBLY
        InternalFrame internalFrameData = new InternalFrame();
        internalFrameData.setLayout(new BorderLayout());
        internalFrameData.setBackground(new Color(255, 255, 255));
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

    private String yn(CekBox c) { return c.isSelected() ? "Ya" : "Tidak"; }
    private String cmb(widget.ComboBox c) { return c.getSelectedItem() == null ? "" : c.getSelectedItem().toString(); }

    private void bukaPetugas() {
        petugas.emptTeks(); petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }

    private void simpan() {
        if (TNoRw.getText().trim().equals("")) { Valid.textKosong(TNoRw, "Pasien"); return; }
        String tgl = Valid.SetTgl(TglOrientasi.getSelectedItem() + "") + " " + TglOrientasi.getSelectedItem().toString().substring(11, 19);
        // Urutan nilai HARUS = urutan kolom tabel orientasi_pasien_ranap_rscb (35 kolom).
        if (Sequel.menyimpantf("orientasi_pasien_ranap_rscb",
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 35, new String[]{
            TNoRw.getText(),
            tgl,
            TRuang.getText(),
            cmb(CmbCaraMasuk), cmb(CmbCaraDatang), TKondisiMasuk.getText(), TDiagnosaMasuk.getText(),
            TDpjp.getText(), TPjNama.getText(), TPjHubungan.getText(), cmb(CmbAlergi), TAlergiKet.getText(),
            cmb(CmbConsent), TBarang.getText(),
            cmb(CmbPenerima), TNamaPenerima.getText(), THubungan.getText(),
            yn(oPetugas), yn(oJamVisite), yn(oTempatTidur), yn(oBel), yn(oKamarMandi), yn(oLemari),
            yn(oListrik), yn(oJamBesuk), yn(oMerokok), yn(oBarang), yn(oGelang), yn(oJatuh),
            yn(oCuciTangan), yn(oEvakuasi), yn(oHakKewajiban), yn(oKomplain),
            TKeterangan.getText(),
            TKdPetugas.getText().trim().isEmpty() ? akses.getkode() : TKdPetugas.getText()
        })) { tampil(); emptTeks(); }
    }

    private int countYa(java.sql.ResultSet r) throws Exception {
        String[] cols = {"o_petugas","o_jam_visite","o_tempat_tidur","o_bel","o_kamar_mandi","o_lemari",
            "o_listrik","o_jam_besuk","o_merokok","o_barang","o_gelang","o_jatuh","o_cuci_tangan",
            "o_evakuasi","o_hak_kewajiban","o_komplain"};
        int n = 0; for (String c : cols) if ("Ya".equals(r.getString(c))) n++;
        return n;
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select o.*, r.no_rkm_medis, p.nm_pasien, IFNULL(pg.nama,'') nm_petugas " +
                "from orientasi_pasien_ranap_rscb o inner join reg_periksa r on o.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                "left join pegawai pg on o.nip=pg.nik " +
                "where o.tanggal between ? and ? and (o.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) " +
                "order by o.tanggal desc");
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
            ps.setString(3, "%" + TCari.getText().trim() + "%");
            ps.setString(4, "%" + TCari.getText().trim() + "%");
            ps.setString(5, "%" + TCari.getText().trim() + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                    rs.getString("tanggal"), rs.getString("ruang"), rs.getString("cara_masuk"),
                    countYa(rs) + "/16", rs.getString("nm_petugas")
                });
            }
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif Penerimaan/Orientasi Ranap : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void getData() {
        if (tbData.getSelectedRow() == -1) return;
        TNoRw.setText(tbData.getValueAt(tbData.getSelectedRow(), 0).toString());
        TNoRM.setText(tbData.getValueAt(tbData.getSelectedRow(), 1).toString());
        TPasien.setText(tbData.getValueAt(tbData.getSelectedRow(), 2).toString());
        try { TglOrientasi.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
            tbData.getValueAt(tbData.getSelectedRow(), 3).toString())); } catch (Exception e) {}
        try {
            ps = koneksi.prepareStatement("select * from orientasi_pasien_ranap_rscb where no_rawat=? and tanggal=?");
            ps.setString(1, tbData.getValueAt(tbData.getSelectedRow(), 0).toString());
            ps.setString(2, tbData.getValueAt(tbData.getSelectedRow(), 3).toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                TRuang.setText(rs.getString("ruang"));
                CmbCaraMasuk.setSelectedItem(rs.getString("cara_masuk"));
                CmbCaraDatang.setSelectedItem(rs.getString("cara_datang"));
                TKondisiMasuk.setText(rs.getString("kondisi_masuk"));
                TDiagnosaMasuk.setText(rs.getString("diagnosa_masuk"));
                TDpjp.setText(rs.getString("dpjp"));
                TPjNama.setText(rs.getString("pj_nama"));
                TPjHubungan.setText(rs.getString("pj_hubungan"));
                CmbAlergi.setSelectedItem(rs.getString("alergi"));
                TAlergiKet.setText(rs.getString("alergi_ket"));
                CmbConsent.setSelectedItem(rs.getString("general_consent"));
                TBarang.setText(rs.getString("barang_bawaan"));
                CmbPenerima.setSelectedItem(rs.getString("penerima"));
                TNamaPenerima.setText(rs.getString("nama_penerima"));
                THubungan.setText(rs.getString("hubungan"));
                oPetugas.setSelected("Ya".equals(rs.getString("o_petugas")));
                oJamVisite.setSelected("Ya".equals(rs.getString("o_jam_visite")));
                oTempatTidur.setSelected("Ya".equals(rs.getString("o_tempat_tidur")));
                oBel.setSelected("Ya".equals(rs.getString("o_bel")));
                oKamarMandi.setSelected("Ya".equals(rs.getString("o_kamar_mandi")));
                oLemari.setSelected("Ya".equals(rs.getString("o_lemari")));
                oListrik.setSelected("Ya".equals(rs.getString("o_listrik")));
                oJamBesuk.setSelected("Ya".equals(rs.getString("o_jam_besuk")));
                oMerokok.setSelected("Ya".equals(rs.getString("o_merokok")));
                oBarang.setSelected("Ya".equals(rs.getString("o_barang")));
                oGelang.setSelected("Ya".equals(rs.getString("o_gelang")));
                oJatuh.setSelected("Ya".equals(rs.getString("o_jatuh")));
                oCuciTangan.setSelected("Ya".equals(rs.getString("o_cuci_tangan")));
                oEvakuasi.setSelected("Ya".equals(rs.getString("o_evakuasi")));
                oHakKewajiban.setSelected("Ya".equals(rs.getString("o_hak_kewajiban")));
                oKomplain.setSelected("Ya".equals(rs.getString("o_komplain")));
                TKeterangan.setText(rs.getString("keterangan"));
                TKdPetugas.setText(rs.getString("nip"));
                TNmPetugas.setText(Sequel.cariIsi("select nama from pegawai where nik=?", rs.getString("nip")));
            }
        } catch (Exception e) { System.out.println("Notif getData Penerimaan/Orientasi : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void emptTeks() {
        TglOrientasi.setDate(new Date());
        TRuang.setText(""); TNamaPenerima.setText(""); THubungan.setText(""); TKeterangan.setText("");
        TKondisiMasuk.setText(""); TDiagnosaMasuk.setText(""); TDpjp.setText("");
        TPjNama.setText(""); TPjHubungan.setText(""); TAlergiKet.setText(""); TBarang.setText("");
        CmbPenerima.setSelectedIndex(0); CmbCaraMasuk.setSelectedIndex(0); CmbCaraDatang.setSelectedIndex(0);
        CmbAlergi.setSelectedIndex(0); CmbConsent.setSelectedIndex(0);
        TKdPetugas.setText(akses.getkode());
        TNmPetugas.setText(Sequel.cariIsi("select nama from pegawai where nik=?", akses.getkode()));
        for (CekBox c : new CekBox[]{oPetugas, oJamVisite, oTempatTidur, oBel, oKamarMandi, oLemari,
                oListrik, oJamBesuk, oMerokok, oBarang, oGelang, oJatuh, oCuciTangan, oEvakuasi, oHakKewajiban, oKomplain})
            c.setSelected(false);
    }

    public void setNoRm(String norwt, String norm, String nama) {
        TNoRw.setText(norwt); TNoRM.setText(norm); TPasien.setText(nama);
        emptTeks(); prefill(norwt);
    }

    public void setNoRm(String norwt) {
        TNoRw.setText(norwt);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", norwt));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
        emptTeks(); prefill(norwt);
    }

    private void prefill(String noRawat) {
        try {
            TNamaPenerima.setText(TPasien.getText()); TPjNama.setText(TPasien.getText());
            String kamar = Sequel.cariIsi(
                "select bangsal.nm_bangsal from kamar_inap inner join kamar on kamar_inap.kd_kamar=kamar.kd_kamar " +
                "inner join bangsal on kamar.kd_bangsal=bangsal.kd_bangsal where kamar_inap.no_rawat=? and kamar_inap.stts_pulang='-' limit 1", noRawat);
            if (kamar != null && !kamar.trim().equals("")) TRuang.setText(kamar);
            String dpjp = Sequel.cariIsi("select d.nm_dokter from reg_periksa r left join dokter d on r.kd_dokter=d.kd_dokter where r.no_rawat=?", noRawat);
            if (dpjp != null && !dpjp.trim().equals("")) TDpjp.setText(dpjp);
        } catch (Exception e) {}
    }

    public void isCek() {
        BtnSimpan.setEnabled(true); BtnHapus.setEnabled(true);
        BtnEdit.setEnabled(true); BtnPrint.setEnabled(true);
    }

    private void isForm() {
        if (ChkInput.isSelected()) { PanelInput.setPreferredSize(new Dimension(WIDTH, 540)); FormInput.setVisible(true); }
        else { PanelInput.setPreferredSize(new Dimension(WIDTH, 20)); FormInput.setVisible(false); }
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
        param.put("urlweb", "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB());
        Valid.MyReportqry("rptOrientasiPasienRanap.jasper", "report", "::[ Penerimaan & Orientasi Pasien Baru Rawat Inap ]::",
            "select o.*, r.no_rkm_medis, p.nm_pasien, p.tgl_lahir, p.jk, IFNULL(pg.nama,'') nm_petugas, " +
            "IFNULL(ob.foto_path,'') foto_path, IFNULL(ob.ttd_path,'') ttd_path " +
            "from orientasi_pasien_ranap_rscb o inner join reg_periksa r on o.no_rawat=r.no_rawat " +
            "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
            "left join pegawai pg on o.nip=pg.nik " +
            "left join orientasi_pasien_ranap_bukti ob on ob.no_rawat=o.no_rawat " +
            "where o.no_rawat='" + TNoRw.getText() + "' and o.tanggal='" +
            tbData.getValueAt(tbData.getSelectedRow(), 3).toString() + "'", param);
    }

    // ---- helper UI ----
    private Label lbl(String t, int x, int y, int w) { Label l = new Label(); l.setText(t); l.setBounds(x, y, w, 23); return l; }
    private JLabel sec(String t, int y) { JLabel l = new JLabel(t); l.setFont(new java.awt.Font("Tahoma", 1, 11)); l.setBounds(10, y, 700, 20); return l; }
    private widget.ComboBox combo(String[] items) {
        widget.ComboBox c = new widget.ComboBox(); c.setModel(new javax.swing.DefaultComboBoxModel(items)); return c;
    }
    private CekBox addCk(String t, int x, int y) {
        CekBox c = new CekBox(); c.setText(t); c.setBounds(x, y, 440, 22); c.setSelected(false); c.setOpaque(false);
        FormInput.add(c); return c;
    }
    private Button btn(String teks, char mn, String ikon) {
        Button b = new Button(); b.setText(teks); b.setMnemonic(mn); b.setPreferredSize(new Dimension(100, 30));
        try { b.setIcon(new ImageIcon(getClass().getResource(ikon))); } catch (Exception e) {}
        return b;
    }
}
