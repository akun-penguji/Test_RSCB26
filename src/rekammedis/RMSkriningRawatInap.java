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
 * Form "Skrining Rawat Inap" (RM.KEP.08.074.2026) — RSUD Cabang Bungin.
 * Diisi Dokter Poliklinik / Dokter Jaga IGD saat memutuskan rawat inap.
 * Disimpan ke tabel skrining_rawat_inap_rscb per (no_rawat, tanggal).
 * Dibuka dari DlgIGD & DlgKasirRalan. Gaya UI mengikuti Khanza.
 */
public class RMSkriningRawatInap extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan, TglMasuk;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private Label LCount;

    private TextBox TJamMasuk, TDiagnosis, TIndikasi, TRuang;
    private TextBox KdDpjpU, NmDpjpU; private Button BtnDpjpU;
    // DPJP 2..5 (rawat bersama): indeks 2..5 dipakai
    private TextBox[] KdDpjp = new TextBox[6];
    private TextBox[] NmDpjp = new TextBox[6];
    private Button[]  BtnDpjp = new Button[6];
    private widget.Tanggal[] TglDpjp = new widget.Tanggal[6];
    private CekBox ChkPreventif, ChkKuratif, ChkIntensif, ChkPaliatif, ChkRehab;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariDokter dokterDlg = new DlgCariDokter(null, false);
    private int dpjpTarget = 0; // 0 = utama, 2..5 = DPJP kedua dst

    private widget.Table tbData;
    private DefaultTableModel tabMode;
    private ScrollPane Scroll;
    private CekBox ChkInput;
    private PanelBiasa FormInput;
    private ScrollPane ScrollInput;
    private JPanel PanelInput;

    public RMSkriningRawatInap(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(950, 700);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Skrining Rawat Inap ]::",
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
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","Tgl Masuk","Diagnosis","DPJP Utama","Skrining"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbData.setModel(tabMode);
        tbData.setDefaultRenderer(Object.class, new fungsi.WarnaTable());
        tbData.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) { if (tabMode.getRowCount() != 0) getData(); }
        });
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem mnCetak = new JMenuItem("Cetak Skrining Rawat Inap");
        mnCetak.addActionListener(evt -> BtnPrint.doClick());
        popupMenu.add(mnCetak);
        tbData.setComponentPopupMenu(popupMenu);
        Scroll.setViewportView(tbData);

        // FORM INPUT
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(920, 620));
        FormInput.setLayout(null);

        int y = 10;
        JLabel lblHeader = new JLabel("FORM SKRINING RAWAT INAP");
        lblHeader.setFont(new java.awt.Font("Tahoma", 1, 13)); lblHeader.setBounds(10, y, 400, 20); FormInput.add(lblHeader);

        y += 28;
        FormInput.add(lbl("Tanggal (input) :", 10, y, 130));
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setBounds(150, y, 160, 23); FormInput.add(TglAsuhan);

        y += 30; FormInput.add(sec("DATA MASUK", y)); y += 25;
        FormInput.add(lbl("Tgl Masuk RS :", 10, y, 130));
        TglMasuk = new widget.Tanggal(); TglMasuk.setDisplayFormat("dd-MM-yyyy"); TglMasuk.setBounds(150, y, 130, 23); FormInput.add(TglMasuk);
        FormInput.add(lbl("Jam Masuk RS :", 320, y, 100));
        TJamMasuk = new TextBox(); TJamMasuk.setBounds(422, y, 90, 23); FormInput.add(TJamMasuk);
        y += 27;
        FormInput.add(lbl("Diagnosis :", 10, y, 130));
        TDiagnosis = new TextBox(); TDiagnosis.setBounds(150, y, 620, 23); FormInput.add(TDiagnosis);
        y += 27;
        FormInput.add(lbl("Indikasi Rawat Inap :", 10, y, 130));
        TIndikasi = new TextBox(); TIndikasi.setBounds(150, y, 620, 23); FormInput.add(TIndikasi);
        y += 27;
        FormInput.add(lbl("Ruang Rawat :", 10, y, 130));
        TRuang = new TextBox(); TRuang.setBounds(150, y, 400, 23); FormInput.add(TRuang);

        y += 30; FormInput.add(sec("DPJP", y)); y += 25;
        FormInput.add(lbl("DPJP Utama :", 10, y, 130));
        KdDpjpU = new TextBox(); KdDpjpU.setBounds(150, y, 90, 23); FormInput.add(KdDpjpU);
        NmDpjpU = new TextBox(); NmDpjpU.setBounds(242, y, 300, 23); NmDpjpU.setEditable(false); FormInput.add(NmDpjpU);
        BtnDpjpU = new Button(); try { BtnDpjpU.setIcon(new ImageIcon(getClass().getResource("/picture/190.png"))); } catch (Exception e) {}
        BtnDpjpU.setBounds(544, y, 28, 23); FormInput.add(BtnDpjpU);
        BtnDpjpU.addActionListener(evt -> bukaDokter(0));

        // DPJP 2..5 + tanggal (rawat bersama)
        for (int i = 2; i <= 5; i++) {
            y += 27;
            final int idx = i;
            FormInput.add(lbl("DPJP " + i + " :", 10, y, 130));
            KdDpjp[i] = new TextBox(); KdDpjp[i].setBounds(150, y, 90, 23); FormInput.add(KdDpjp[i]);
            NmDpjp[i] = new TextBox(); NmDpjp[i].setBounds(242, y, 250, 23); NmDpjp[i].setEditable(false); FormInput.add(NmDpjp[i]);
            BtnDpjp[i] = new Button(); try { BtnDpjp[i].setIcon(new ImageIcon(getClass().getResource("/picture/190.png"))); } catch (Exception e) {}
            BtnDpjp[i].setBounds(494, y, 28, 23); FormInput.add(BtnDpjp[i]);
            BtnDpjp[i].addActionListener(evt -> bukaDokter(idx));
            FormInput.add(lbl("Tgl :", 532, y, 32));
            TglDpjp[i] = new widget.Tanggal(); TglDpjp[i].setDisplayFormat("dd-MM-yyyy"); TglDpjp[i].setBounds(566, y, 120, 23); FormInput.add(TglDpjp[i]);
        }

        y += 30; FormInput.add(sec("SKRINING KEBUTUHAN PELAYANAN RAWAT INAP", y)); y += 25;
        ChkPreventif = ck("Preventif", 10, y, 120); FormInput.add(ChkPreventif);
        ChkKuratif = ck("Kuratif", 140, y, 120); FormInput.add(ChkKuratif);
        ChkIntensif = ck("Intensif", 270, y, 120); FormInput.add(ChkIntensif);
        y += 25;
        ChkPaliatif = ck("Paliatif", 10, y, 120); FormInput.add(ChkPaliatif);
        ChkRehab = ck("Rehabilitatif", 140, y, 150); FormInput.add(ChkRehab);

        // DlgCariDokter listener (target-based)
        dokterDlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosed(java.awt.event.WindowEvent e) {
                if (dokterDlg.getTable().getSelectedRow() != -1) {
                    String kd = dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(), 0).toString();
                    String nm = dokterDlg.getTable().getValueAt(dokterDlg.getTable().getSelectedRow(), 1).toString();
                    if (dpjpTarget == 0) { KdDpjpU.setText(kd); NmDpjpU.setText(nm); }
                    else if (dpjpTarget >= 2 && dpjpTarget <= 5) { KdDpjp[dpjpTarget].setText(kd); NmDpjp[dpjpTarget].setText(nm); }
                }
            }
        });

        // Input toggle
        ChkInput = new CekBox();
        try {
            ChkInput.setIcon(new ImageIcon(getClass().getResource("/picture/143.png")));
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
                if (JOptionPane.showConfirmDialog(this, "Hapus skrining rawat inap ini?", "Konfirmasi",
                        JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
                if (Sequel.queryu2tf("delete from skrining_rawat_inap_rscb where no_rawat=? and tanggal=?", 2, new String[]{
                    tbData.getValueAt(tbData.getSelectedRow(), 0).toString(),
                    tbData.getValueAt(tbData.getSelectedRow(), 3).toString()
                })) { tampil(); emptTeks(); }
            } else JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau dihapus...!!!!");
        });
        BtnEdit.addActionListener(evt -> {
            if (TNoRw.getText().trim().equals("") || tbData.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            } else {
                if (Sequel.queryu2tf("delete from skrining_rawat_inap_rscb where no_rawat=? and tanggal=?", 2, new String[]{
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
        internalFrameData.add(PanelInput, BorderLayout.CENTER);
        Scroll.setPreferredSize(new Dimension(920, 130));
        internalFrameData.add(Scroll, BorderLayout.SOUTH);
        internalFrame1.add(panelAtas, BorderLayout.PAGE_START);
        internalFrame1.add(internalFrameData, BorderLayout.CENTER);
        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);
        setLayout(new BorderLayout());
        add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setUndecorated(true);
    }

    private void bukaDokter(int target) {
        dpjpTarget = target;
        dokterDlg.emptTeks(); dokterDlg.isCek();
        dokterDlg.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokterDlg.setLocationRelativeTo(internalFrame1);
        dokterDlg.setVisible(true);
    }

    private String yn(CekBox c) { return c.isSelected() ? "Ya" : "Tidak"; }
    private String tglDpjp(int i) {
        return KdDpjp[i].getText().trim().equals("") ? "" : Valid.SetTgl(TglDpjp[i].getSelectedItem() + "");
    }

    private void simpan() {
        if (TNoRw.getText().trim().equals("")) { Valid.textKosong(TNoRw, "Pasien"); return; }
        if (KdDpjpU.getText().trim().equals("")) { Valid.textKosong(KdDpjpU, "DPJP Utama"); return; }
        String tgl = Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + " " + TglAsuhan.getSelectedItem().toString().substring(11, 19);
        // Urutan nilai HARUS = urutan kolom tabel skrining_rawat_inap_rscb (22 kolom).
        if (Sequel.menyimpantf("skrining_rawat_inap_rscb",
            "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 22, new String[]{
            TNoRw.getText(),
            tgl,
            Valid.SetTgl(TglMasuk.getSelectedItem() + ""),
            TJamMasuk.getText(),
            TDiagnosis.getText(),
            TIndikasi.getText(),
            TRuang.getText(),
            KdDpjpU.getText(),
            KdDpjp[2].getText(), tglDpjp(2),
            KdDpjp[3].getText(), tglDpjp(3),
            KdDpjp[4].getText(), tglDpjp(4),
            KdDpjp[5].getText(), tglDpjp(5),
            yn(ChkPreventif), yn(ChkKuratif), yn(ChkIntensif), yn(ChkPaliatif), yn(ChkRehab),
            akses.getkode()
        })) { tampil(); emptTeks(); }
    }

    private String skriningStr(java.sql.ResultSet r) throws Exception {
        StringBuilder sb = new StringBuilder();
        if ("Ya".equals(r.getString("preventif"))) sb.append("Preventif, ");
        if ("Ya".equals(r.getString("kuratif"))) sb.append("Kuratif, ");
        if ("Ya".equals(r.getString("intensif"))) sb.append("Intensif, ");
        if ("Ya".equals(r.getString("paliatif"))) sb.append("Paliatif, ");
        if ("Ya".equals(r.getString("rehabilitatif"))) sb.append("Rehabilitatif, ");
        String s = sb.toString();
        return s.isEmpty() ? "-" : s.substring(0, s.length() - 2);
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select s.no_rawat, r.no_rkm_medis, p.nm_pasien, s.tanggal, s.tgl_masuk_rs, s.diagnosis, " +
                "IFNULL(d.nm_dokter,'') nm_dpjp, s.preventif, s.kuratif, s.intensif, s.paliatif, s.rehabilitatif " +
                "from skrining_rawat_inap_rscb s inner join reg_periksa r on s.no_rawat=r.no_rawat " +
                "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                "left join dokter d on s.dpjp_utama=d.kd_dokter " +
                "where s.tanggal between ? and ? and (s.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) " +
                "order by s.tanggal desc");
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
            ps.setString(3, "%" + TCari.getText().trim() + "%");
            ps.setString(4, "%" + TCari.getText().trim() + "%");
            ps.setString(5, "%" + TCari.getText().trim() + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                    rs.getString("tanggal"), rs.getString("tgl_masuk_rs"), rs.getString("diagnosis"),
                    rs.getString("nm_dpjp"), skriningStr(rs)
                });
            }
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif Skrining Rawat Inap : " + e);
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
            ps = koneksi.prepareStatement("select * from skrining_rawat_inap_rscb where no_rawat=? and tanggal=?");
            ps.setString(1, tbData.getValueAt(tbData.getSelectedRow(), 0).toString());
            ps.setString(2, tbData.getValueAt(tbData.getSelectedRow(), 3).toString());
            rs = ps.executeQuery();
            if (rs.next()) {
                try { TglMasuk.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(rs.getString("tgl_masuk_rs"))); } catch (Exception e) {}
                TJamMasuk.setText(rs.getString("jam_masuk_rs"));
                TDiagnosis.setText(rs.getString("diagnosis"));
                TIndikasi.setText(rs.getString("indikasi"));
                TRuang.setText(rs.getString("ruang_rawat"));
                KdDpjpU.setText(rs.getString("dpjp_utama"));
                NmDpjpU.setText(Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", rs.getString("dpjp_utama")));
                for (int i = 2; i <= 5; i++) {
                    String kd = rs.getString("dpjp" + i);
                    KdDpjp[i].setText(kd);
                    NmDpjp[i].setText(kd == null || kd.trim().equals("") ? "" : Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", kd));
                    String td = rs.getString("tgl_dpjp" + i);
                    if (td != null && !td.trim().equals("")) { try { TglDpjp[i].setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(td)); } catch (Exception e) {} }
                }
                ChkPreventif.setSelected("Ya".equals(rs.getString("preventif")));
                ChkKuratif.setSelected("Ya".equals(rs.getString("kuratif")));
                ChkIntensif.setSelected("Ya".equals(rs.getString("intensif")));
                ChkPaliatif.setSelected("Ya".equals(rs.getString("paliatif")));
                ChkRehab.setSelected("Ya".equals(rs.getString("rehabilitatif")));
            }
        } catch (Exception e) { System.out.println("Notif getData Skrining Rawat Inap : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date()); TglMasuk.setDate(new Date());
        TJamMasuk.setText(""); TDiagnosis.setText(""); TIndikasi.setText(""); TRuang.setText("");
        KdDpjpU.setText(""); NmDpjpU.setText("");
        for (int i = 2; i <= 5; i++) { KdDpjp[i].setText(""); NmDpjp[i].setText(""); TglDpjp[i].setDate(new Date()); }
        ChkPreventif.setSelected(false); ChkKuratif.setSelected(false); ChkIntensif.setSelected(false);
        ChkPaliatif.setSelected(false); ChkRehab.setSelected(false);
    }

    public void setNoRm(String norwt, String norm, String nama) {
        TNoRw.setText(norwt); TNoRM.setText(norm); TPasien.setText(nama);
        emptTeks();
        prefill(norwt);
    }

    public void setNoRm(String norwt) {
        TNoRw.setText(norwt);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", norwt));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
        emptTeks();
        prefill(norwt);
    }

    private void prefill(String noRawat) {
        try {
            ps = koneksi.prepareStatement("select tgl_registrasi, jam_reg, kd_dokter from reg_periksa where no_rawat=?");
            ps.setString(1, noRawat);
            rs = ps.executeQuery();
            if (rs.next()) {
                try { TglMasuk.setDate(rs.getDate("tgl_registrasi")); } catch (Exception e) {}
                if (rs.getString("jam_reg") != null && rs.getString("jam_reg").length() >= 5) TJamMasuk.setText(rs.getString("jam_reg").substring(0, 5));
                String kd = rs.getString("kd_dokter");
                if (kd != null && !kd.trim().equals("")) { KdDpjpU.setText(kd); NmDpjpU.setText(Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", kd)); }
            }
        } catch (Exception e) { System.out.println("Notif prefill Skrining : " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getrujukan_keluar()); BtnHapus.setEnabled(akses.getrujukan_keluar());
        BtnEdit.setEnabled(akses.getrujukan_keluar()); BtnPrint.setEnabled(akses.getrujukan_keluar());
    }

    private void isForm() {
        if (ChkInput.isSelected()) { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 540)); FormInput.setVisible(true); ChkInput.setVisible(true);
        } else { ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 20)); FormInput.setVisible(false); ChkInput.setVisible(true); }
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
        Valid.MyReportqry("rptSkriningRawatInap.jasper", "report", "::[ Skrining Rawat Inap ]::",
            "select s.*, r.no_rkm_medis, p.nm_pasien, p.tgl_lahir, p.jk, " +
            "IFNULL(du.nm_dokter,'') nm_dpjp_utama, IFNULL(d2.nm_dokter,'') nm_dpjp2, IFNULL(d3.nm_dokter,'') nm_dpjp3, " +
            "IFNULL(d4.nm_dokter,'') nm_dpjp4, IFNULL(d5.nm_dokter,'') nm_dpjp5, IFNULL(pg.nama,'') nm_petugas " +
            "from skrining_rawat_inap_rscb s inner join reg_periksa r on s.no_rawat=r.no_rawat " +
            "inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
            "left join dokter du on s.dpjp_utama=du.kd_dokter " +
            "left join dokter d2 on s.dpjp2=d2.kd_dokter left join dokter d3 on s.dpjp3=d3.kd_dokter " +
            "left join dokter d4 on s.dpjp4=d4.kd_dokter left join dokter d5 on s.dpjp5=d5.kd_dokter " +
            "left join pegawai pg on s.nip=pg.nik " +
            "where s.no_rawat='" + TNoRw.getText() + "' and s.tanggal='" +
            tbData.getValueAt(tbData.getSelectedRow(), 3).toString() + "'", param);
    }

    // ---- helper UI ----
    private Label lbl(String t, int x, int y, int w) { Label l = new Label(); l.setText(t); l.setBounds(x, y, w, 23); return l; }
    private JLabel sec(String t, int y) { JLabel l = new JLabel(t); l.setFont(new java.awt.Font("Tahoma", 1, 11)); l.setBounds(10, y, 500, 20); return l; }
    private CekBox ck(String t, int x, int y, int w) { CekBox c = new CekBox(); c.setText(t); c.setBounds(x, y, w, 23); c.setSelected(false); c.setOpaque(false); return c; }
    private Button btn(String teks, char mn, String ikon) {
        Button b = new Button(); b.setText(teks); b.setMnemonic(mn); b.setPreferredSize(new Dimension(100, 30));
        try { b.setIcon(new ImageIcon(getClass().getResource(ikon))); } catch (Exception e) {}
        return b;
    }
}
