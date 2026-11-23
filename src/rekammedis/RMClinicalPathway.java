package rekammedis;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import fungsi.WarnaTable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariPetugas;
import widget.*;

public class RMClinicalPathway extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnKeluar, BtnCari, BtnAll, BtnPrint, BtnPetugas;
    private widget.Tanggal DTPCari1, DTPCari2;
    private TextBox TNoRw, TNoRM, TPasien, TCari;
    private TextBox TBB, TTB;
    private TextBox KdPetugas, NmPetugas;
    private TextBox TJK, TTglLahir, TDiagnosaMasuk, TPenyakitUtama, TPenyakitPenyerta, TTindakan;
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);
    private ComboBox CmbTemplate;
    private Label LCount;

    private java.util.List<CekBox[]> chkGrid = new ArrayList<>();
    private java.util.List<Integer> itemIds = new ArrayList<>();
    private java.util.List<String> itemKategori = new ArrayList<>();
    private java.util.List<String> itemAktivitas = new ArrayList<>();
    private java.util.List<String> itemHariKe = new ArrayList<>();
    private PanelBiasa FormInput;
    private ScrollPane ScrollInput;
    private JPanel PanelInput;
    private CekBox ChkInput;

    private widget.Table tbData;
    private DefaultTableModel tabMode;
    private ScrollPane Scroll;

    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    public widget.editorpane LoadHTML;

    private int selectedTemplateId = -1;
    private int selectedEksekusiId = -1;

    public RMClinicalPathway(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Clinical Pathway ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        panelisi panelAtas = new panelisi();
        panelAtas.setLayout(null);
        panelAtas.setPreferredSize(new Dimension(1200, 80));

        int x, y0 = 3, y1 = 28, y2 = 53, h = 23;

        // === BARIS 1 ===
        x = 5;
        Label lblNoRw = new Label(); lblNoRw.setText("No.Rawat :"); lblNoRw.setBounds(x, y0, 60, h); panelAtas.add(lblNoRw);
        x += 62; TNoRw = new TextBox(); TNoRw.setBounds(x, y0, 125, h); panelAtas.add(TNoRw);
        x += 127; TNoRM = new TextBox(); TNoRM.setBounds(x, y0, 70, h); panelAtas.add(TNoRM);
        x += 72; TPasien = new TextBox(); TPasien.setBounds(x, y0, 150, h); panelAtas.add(TPasien);
        x += 155; Label lblTpl = new Label(); lblTpl.setText("CP :"); lblTpl.setBounds(x, y0, 25, h); panelAtas.add(lblTpl);
        x += 27; CmbTemplate = new ComboBox(); CmbTemplate.setBounds(x, y0, 200, h); panelAtas.add(CmbTemplate);
        loadTemplateList();
        CmbTemplate.addItemListener(ev -> { if (ev.getStateChange() == java.awt.event.ItemEvent.SELECTED) loadTemplateForm(); });
        x += 202; Button BtnMasterCP = new Button(); BtnMasterCP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnMasterCP.setBounds(x, y0, 28, h); BtnMasterCP.setToolTipText("Master Template CP"); panelAtas.add(BtnMasterCP);
        BtnMasterCP.addActionListener(ev -> {
            RMClinicalPathwayTemplate dlg = new RMClinicalPathwayTemplate(null, false);
            dlg.setSize(internalFrame1.getWidth()-20, internalFrame1.getHeight()-20);
            dlg.setLocationRelativeTo(internalFrame1); dlg.setVisible(true);
            dlg.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosed(java.awt.event.WindowEvent e) { loadTemplateList(); if (CmbTemplate.getItemCount()>0) { CmbTemplate.setSelectedIndex(0); loadTemplateForm(); } }
            });
        });
        x += 33; Label lblBB = new Label(); lblBB.setText("BB:"); lblBB.setBounds(x, y0, 22, h); panelAtas.add(lblBB);
        x += 23; TBB = new TextBox(); TBB.setBounds(x, y0, 35, h); panelAtas.add(TBB);
        x += 38; Label lblTB = new Label(); lblTB.setText("TB:"); lblTB.setBounds(x, y0, 22, h); panelAtas.add(lblTB);
        x += 23; TTB = new TextBox(); TTB.setBounds(x, y0, 35, h); panelAtas.add(TTB);
        x += 42; Label lblPetugas = new Label(); lblPetugas.setText("Petugas:"); lblPetugas.setBounds(x, y0, 50, h); panelAtas.add(lblPetugas);
        x += 52; KdPetugas = new TextBox(); KdPetugas.setBounds(x, y0, 70, h); panelAtas.add(KdPetugas);
        x += 72; NmPetugas = new TextBox(); NmPetugas.setBounds(x, y0, 130, h); panelAtas.add(NmPetugas);
        x += 132; BtnPetugas = new Button(); BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPetugas.setBounds(x, y0, 28, h); panelAtas.add(BtnPetugas);
        BtnPetugas.addActionListener(ev -> {
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

        // === BARIS 2 ===
        x = 5;
        Label lblJK = new Label(); lblJK.setText("J.Kelamin :"); lblJK.setBounds(x, y1, 60, h); panelAtas.add(lblJK);
        x += 62; TJK = new TextBox(); TJK.setEditable(false); TJK.setBounds(x, y1, 80, h); panelAtas.add(TJK);
        x += 85; Label lblTglLahir = new Label(); lblTglLahir.setText("Tgl Lahir :"); lblTglLahir.setBounds(x, y1, 55, h); panelAtas.add(lblTglLahir);
        x += 57; TTglLahir = new TextBox(); TTglLahir.setEditable(false); TTglLahir.setBounds(x, y1, 80, h); panelAtas.add(TTglLahir);
        x += 85; Label lblDiagMasuk = new Label(); lblDiagMasuk.setText("Diagnosa Masuk :"); lblDiagMasuk.setBounds(x, y1, 95, h); panelAtas.add(lblDiagMasuk);
        x += 97; TDiagnosaMasuk = new TextBox(); TDiagnosaMasuk.setEditable(false); TDiagnosaMasuk.setBounds(x, y1, 250, h); panelAtas.add(TDiagnosaMasuk);
        x += 255; Label lblPU = new Label(); lblPU.setText("Penyakit Utama :"); lblPU.setBounds(x, y1, 90, h); panelAtas.add(lblPU);
        x += 92; TPenyakitUtama = new TextBox(); TPenyakitUtama.setEditable(false); TPenyakitUtama.setBounds(x, y1, 300, h); panelAtas.add(TPenyakitUtama);

        // === BARIS 3 ===
        x = 5;
        Label lblPP = new Label(); lblPP.setText("Penyerta :"); lblPP.setBounds(x, y2, 55, h); panelAtas.add(lblPP);
        x += 57; TPenyakitPenyerta = new TextBox(); TPenyakitPenyerta.setEditable(false); TPenyakitPenyerta.setBounds(x, y2, 400, h); panelAtas.add(TPenyakitPenyerta);
        x += 405; Label lblTind = new Label(); lblTind.setText("Tindakan :"); lblTind.setBounds(x, y2, 55, h); panelAtas.add(lblTind);
        x += 57; TTindakan = new TextBox(); TTindakan.setEditable(false); TTindakan.setBounds(x, y2, 400, h); panelAtas.add(TTindakan);

        FormInput = new PanelBiasa();
        FormInput.setLayout(null);

        ChkInput = new CekBox();
        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setText(".: Input Data"); ChkInput.setSelected(true);
        ChkInput.setBorderPainted(true); ChkInput.setBorderPaintedFlat(true); ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(SwingConstants.LEFT); ChkInput.setPreferredSize(new Dimension(192, 20));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.addActionListener(ev -> isForm());

        ScrollInput = new ScrollPane(); ScrollInput.setViewportView(FormInput);
        PanelInput = new JPanel(new BorderLayout()); PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new Dimension(WIDTH, 450));
        PanelInput.add(ScrollInput, BorderLayout.CENTER); PanelInput.add(ChkInput, BorderLayout.PAGE_END);

        Scroll = new ScrollPane();
        tbData = new widget.Table();
        tabMode = new DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat","No.R.M.","Nama","CP","Tgl Mulai","Status","LOS","Kepatuhan"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbData.setModel(tabMode);
        tbData.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < 8; i++) {
            TableColumn col = tbData.getColumnModel().getColumn(i);
            int[] w = {105, 70, 150, 150, 80, 60, 40, 70};
            col.setPreferredWidth(w[i]);
        }
        tbData.setDefaultRenderer(Object.class, new WarnaTable());
        tbData.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { if (tabMode.getRowCount() != 0) getData(); }
        });

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem mnCetak = new JMenuItem();
        mnCetak.setBackground(new Color(255,255,254));
        mnCetak.setFont(new Font("Tahoma",0,11));
        mnCetak.setForeground(new Color(50,50,50));
        mnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        mnCetak.setText("Cetak Clinical Pathway");
        mnCetak.setPreferredSize(new Dimension(220,26));
        mnCetak.addActionListener(ev -> cetakCP());
        popupMenu.add(mnCetak);

        JMenuItem mnHapus = new JMenuItem();
        mnHapus.setBackground(new Color(255,255,254));
        mnHapus.setFont(new Font("Tahoma",0,11));
        mnHapus.setForeground(new Color(50,50,50));
        mnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        mnHapus.setText("Hapus Clinical Pathway");
        mnHapus.setPreferredSize(new Dimension(220,26));
        mnHapus.addActionListener(ev -> hapusCP());
        popupMenu.add(mnHapus);

        tbData.setComponentPopupMenu(popupMenu);
        Scroll.setViewportView(tbData);

        panelBawah = new panelisi(); panelBawah.setPreferredSize(new Dimension(44, 54));
        panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 9));
        BtnSimpan = new Button(); BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setText("Simpan"); BtnSimpan.setPreferredSize(new Dimension(100, 30));
        BtnBatal = new Button(); BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBatal.setText("Baru"); BtnBatal.setPreferredSize(new Dimension(100, 30));
        BtnPrint = new Button(); BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        BtnPrint.setText("Cetak"); BtnPrint.setPreferredSize(new Dimension(100, 30));
        BtnKeluar = new Button(); BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setText("Keluar"); BtnKeluar.setPreferredSize(new Dimension(100, 30));
        Label lc = new Label(); lc.setText("Tanggal :"); lc.setPreferredSize(new Dimension(55, 23));
        DTPCari1 = new widget.Tanggal(); DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setPreferredSize(new Dimension(90, 23));
        Label sd = new Label(); sd.setText("s.d."); sd.setPreferredSize(new Dimension(25, 23));
        DTPCari2 = new widget.Tanggal(); DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setPreferredSize(new Dimension(90, 23));
        Label lk = new Label(); lk.setText("Key Word :"); lk.setPreferredSize(new Dimension(70, 23));
        TCari = new TextBox(); TCari.setPreferredSize(new Dimension(150, 23));
        BtnCari = new Button(); BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnAll = new Button(); BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); BtnAll.setPreferredSize(new Dimension(28, 23));
        LCount = new Label(); LCount.setText("Record : 0"); LCount.setPreferredSize(new Dimension(100, 23));
        Button BtnAutoDetect = new Button(); BtnAutoDetect.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAutoDetect.setText("Auto Deteksi"); BtnAutoDetect.setPreferredSize(new Dimension(120, 30));
        BtnAutoDetect.addActionListener(ev -> autoDetect());
        panelBawah.add(BtnSimpan); panelBawah.add(BtnBatal); panelBawah.add(BtnAutoDetect); panelBawah.add(BtnPrint); panelBawah.add(BtnKeluar);
        panelBawah.add(lc); panelBawah.add(DTPCari1); panelBawah.add(sd); panelBawah.add(DTPCari2);
        panelBawah.add(lk); panelBawah.add(TCari); panelBawah.add(BtnCari); panelBawah.add(BtnAll); panelBawah.add(LCount);

        BtnKeluar.addActionListener(ev -> dispose());
        BtnBatal.addActionListener(ev -> emptTeks());
        BtnCari.addActionListener(ev -> tampil());
        BtnAll.addActionListener(ev -> { TCari.setText(""); tampil(); });
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) tampil(); }
        });
        BtnSimpan.addActionListener(ev -> simpan());
        BtnPrint.addActionListener(ev -> cetakRekap());

        InternalFrame ifd = new InternalFrame(); ifd.setLayout(new BorderLayout()); ifd.setBackground(Color.WHITE);
        ifd.add(PanelInput, BorderLayout.NORTH); ifd.add(Scroll, BorderLayout.CENTER);
        internalFrame1.add(panelAtas, BorderLayout.PAGE_START);
        internalFrame1.add(ifd, BorderLayout.CENTER);
        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);
        setLayout(new BorderLayout()); add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); setUndecorated(true);

        if (CmbTemplate.getItemCount() > 0) {
            CmbTemplate.setSelectedIndex(0);
            loadTemplateForm();
        }
    }

    private void loadTemplateList() {
        try {
            ps = koneksi.prepareStatement("select id_template, nama_cp from cp_template where aktif=1 order by nama_cp");
            rs = ps.executeQuery();
            java.util.List<String> items = new ArrayList<>();
            while (rs.next()) items.add(rs.getInt("id_template") + " - " + rs.getString("nama_cp"));
            CmbTemplate.setModel(new javax.swing.DefaultComboBoxModel(items.toArray(new String[0])));
        } catch (Exception e) { System.out.println("Notif loadTemplate: " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    private void loadTemplateForm() {
        FormInput.removeAll();
        chkGrid.clear(); itemIds.clear(); itemKategori.clear(); itemAktivitas.clear(); itemHariKe.clear();

        String sel = CmbTemplate.getSelectedItem().toString();
        selectedTemplateId = Integer.parseInt(sel.split(" - ")[0].trim());

        int y = 5;
        JLabel lblTitle = new JLabel("CLINICAL PATHWAY");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 14)); lblTitle.setBounds(10, y, 300, 20); FormInput.add(lblTitle);

        try {
            ps = koneksi.prepareStatement("select nama_cp, kode_icd10 from cp_template where id_template=?");
            ps.setInt(1, selectedTemplateId);
            rs = ps.executeQuery();
            if (rs.next()) {
                JLabel lblNama = new JLabel(rs.getString("nama_cp") + " (ICD: " + rs.getString("kode_icd10") + ")");
                lblNama.setFont(new Font("Tahoma", Font.BOLD, 11)); lblNama.setBounds(200, y, 500, 20); FormInput.add(lblNama);
            }
            rs.close(); ps.close();
        } catch (Exception e) { System.out.println(e); }

        y += 30;
        int xStart = 500, cellW = 30;
        // Headers
        JLabel hKeg = new JLabel("KEGIATAN"); hKeg.setFont(new Font("Tahoma", Font.BOLD, 11)); hKeg.setBounds(5, y, 150, 30); FormInput.add(hKeg);
        JLabel hUr = new JLabel("URAIAN KEGIATAN"); hUr.setFont(new Font("Tahoma", Font.BOLD, 11)); hUr.setBounds(160, y, 250, 30); FormInput.add(hUr);
        JLabel hHP = new JLabel("HARI PENYAKIT"); hHP.setFont(new Font("Tahoma", Font.BOLD, 9)); hHP.setBounds(xStart, y, 220, 14); FormInput.add(hHP);
        for (int d = 1; d <= 7; d++) {
            JLabel lbl = new JLabel(String.valueOf(d)); lbl.setFont(new Font("Tahoma", Font.BOLD, 10));
            lbl.setHorizontalAlignment(SwingConstants.CENTER); lbl.setBounds(xStart + (d-1) * cellW, y + 14, cellW, 16); FormInput.add(lbl);
        }
        JLabel hKet = new JLabel("KETERANGAN"); hKet.setFont(new Font("Tahoma", Font.BOLD, 10));
        hKet.setBounds(xStart + 7 * cellW + 8, y, 200, 30); FormInput.add(hKet);
        y += 34;

        String prevKat = "", prevSub = "";
        try {
            ps = koneksi.prepareStatement("select * from cp_template_item where id_template=? order by urutan");
            ps.setInt(1, selectedTemplateId);
            rs = ps.executeQuery();
            while (rs.next()) {
                int idItem = rs.getInt("id_item");
                String kat = rs.getString("kategori");
                String sub = rs.getString("sub_kategori");
                String akt = rs.getString("nama_aktivitas");
                String hariKe = rs.getString("hari_ke");
                String ket = rs.getString("keterangan");

                if (!kat.equals(prevKat)) {
                    JLabel lblK = new JLabel(" " + kat);
                    lblK.setFont(new Font("Tahoma", Font.BOLD, 10)); lblK.setOpaque(true);
                    lblK.setBackground(new Color(210, 210, 210));
                    lblK.setBounds(5, y, xStart + 7 * cellW + 250, 20); FormInput.add(lblK);
                    y += 22; prevKat = kat; prevSub = "";
                }
                if (sub != null && !sub.equals(prevSub)) {
                    JLabel lblS = new JLabel("   " + sub);
                    lblS.setFont(new Font("Tahoma", Font.ITALIC, 9));
                    lblS.setBounds(5, y, 480, 16); FormInput.add(lblS);
                    y += 18; prevSub = sub;
                }

                if (!akt.isEmpty()) {
                    JLabel lblA = new JLabel("<html>" + akt + "</html>");
                    lblA.setFont(new Font("Tahoma", 0, 10)); lblA.setBounds(160, y, 330, 24); FormInput.add(lblA);
                }

                CekBox[] days = new CekBox[7];
                java.util.Set<Integer> aktif = parseHariKe(hariKe);
                for (int d = 0; d < 7; d++) {
                    days[d] = new CekBox();
                    days[d].setBounds(xStart + d * cellW, y, cellW, 24);
                    if (aktif.contains(d + 1)) {
                        days[d].setOpaque(true); days[d].setBackground(new Color(255, 100, 100));
                    } else {
                        days[d].setEnabled(false);
                    }
                    FormInput.add(days[d]);
                }

                if (ket != null && !ket.isEmpty()) {
                    JLabel lblKt = new JLabel(ket); lblKt.setFont(new Font("Tahoma", 0, 9));
                    lblKt.setBounds(xStart + 7 * cellW + 8, y, 250, 24); FormInput.add(lblKt);
                }

                chkGrid.add(days); itemIds.add(idItem);
                itemKategori.add(kat); itemAktivitas.add(akt); itemHariKe.add(hariKe);
                y += 28;
            }
        } catch (Exception e) { System.out.println("Notif loadForm: " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }

        // Legend
        y += 14;
        JLabel lgRed = new JLabel("  "); lgRed.setOpaque(true); lgRed.setBackground(new Color(255, 100, 100)); lgRed.setBounds(10, y, 20, 16); FormInput.add(lgRed);
        JLabel lgRedT = new JLabel("Yang harus dilakukan"); lgRedT.setFont(new Font("Tahoma", 0, 10)); lgRedT.setBounds(35, y, 200, 16); FormInput.add(lgRedT);
        JLabel lgCheck = new JLabel("\u2713 Bila sudah dilakukan"); lgCheck.setFont(new Font("Tahoma", 0, 10)); lgCheck.setBounds(10, y + 18, 250, 16); FormInput.add(lgCheck);

        FormInput.setPreferredSize(new Dimension(1050, y + 50));
        FormInput.revalidate(); FormInput.repaint();
        ScrollInput.setViewportView(FormInput);
        ScrollInput.revalidate(); ScrollInput.repaint();
    }

    private java.util.Set<Integer> parseHariKe(String h) {
        java.util.Set<Integer> s = new java.util.HashSet<>();
        if (h == null || h.isEmpty()) return s;
        for (String v : h.split(",")) { try { s.add(Integer.parseInt(v.trim())); } catch (Exception e) {} }
        return s;
    }

    private void simpan() {
        if (TNoRw.getText().trim().isEmpty()) { Valid.textKosong(TNoRw, "Pasien"); return; }
        if (selectedTemplateId < 0) { JOptionPane.showMessageDialog(null, "Pilih template CP"); return; }
        try {
            String tglMulai = Valid.SetTgl(DTPCari1.getSelectedItem() + "");
            if (selectedEksekusiId > 0) {
                Sequel.queryu("delete from cp_eksekusi_item where id_eksekusi=" + selectedEksekusiId);
            } else {
                ps = koneksi.prepareStatement(
                    "insert into cp_eksekusi (no_rawat, id_template, tgl_mulai, status, dibuat_oleh) values (?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, TNoRw.getText()); ps.setInt(2, selectedTemplateId);
                ps.setString(3, tglMulai); ps.setString(4, "aktif"); ps.setString(5, akses.getkode());
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) selectedEksekusiId = keys.getInt(1);
                keys.close(); ps.close();
            }
            int saved = 0;
            String now = Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " " + new java.text.SimpleDateFormat("HH:mm:ss").format(new Date());
            for (int i = 0; i < chkGrid.size(); i++) {
                CekBox[] days = chkGrid.get(i);
                for (int d = 0; d < 7; d++) {
                    if (days[d].isEnabled()) {
                        String st = days[d].isSelected() ? "done" : "pending";
                        Sequel.menyimpantf("cp_eksekusi_item", "NULL,?,?,?,?,?,?,?", "Data", 7, new String[]{
                            String.valueOf(selectedEksekusiId), String.valueOf(itemIds.get(i)),
                            String.valueOf(d + 1), st, akses.getkode(), days[d].isSelected() ? now : null, ""
                        });
                        if (days[d].isSelected()) saved++;
                    }
                }
            }
            JOptionPane.showMessageDialog(null, "Clinical Pathway tersimpan. " + saved + " item done.");
            tampil();
        } catch (Exception e) { System.out.println("Notif simpanCP: " + e); }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select e.id_eksekusi, e.no_rawat, r.no_rkm_medis, p.nm_pasien, t.nama_cp, e.tgl_mulai, e.status, e.los_aktual, " +
                "(select count(*) from cp_eksekusi_item ei where ei.id_eksekusi=e.id_eksekusi and ei.status='done') as done_count, " +
                "(select count(*) from cp_eksekusi_item ei where ei.id_eksekusi=e.id_eksekusi) as total_count " +
                "from cp_eksekusi e inner join cp_template t on e.id_template=t.id_template " +
                "inner join reg_periksa r on e.no_rawat=r.no_rawat inner join pasien p on r.no_rkm_medis=p.no_rkm_medis " +
                "where e.tgl_mulai between ? and ? and (e.no_rawat like ? or r.no_rkm_medis like ? or p.nm_pasien like ?) " +
                "order by e.tgl_mulai desc");
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
            ps.setString(3, "%" + TCari.getText().trim() + "%");
            ps.setString(4, "%" + TCari.getText().trim() + "%");
            ps.setString(5, "%" + TCari.getText().trim() + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                int done = rs.getInt("done_count"), total = rs.getInt("total_count");
                String kepatuhan = total > 0 ? String.format("%.0f%%", (done * 100.0 / total)) : "-";
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                    rs.getString("nama_cp"), rs.getString("tgl_mulai"), rs.getString("status"),
                    rs.getString("los_aktual") == null ? "-" : rs.getString("los_aktual"), kepatuhan
                });
            }
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif tampilCP: " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void getData() {
        if (tbData.getSelectedRow() == -1) return;
        TNoRw.setText(tbData.getValueAt(tbData.getSelectedRow(), 0).toString());
        TNoRM.setText(tbData.getValueAt(tbData.getSelectedRow(), 1).toString());
        TPasien.setText(tbData.getValueAt(tbData.getSelectedRow(), 2).toString());
        try {
            ps = koneksi.prepareStatement("select id_eksekusi, id_template from cp_eksekusi where no_rawat=? order by tgl_mulai desc limit 1");
            ps.setString(1, TNoRw.getText());
            rs = ps.executeQuery();
            if (rs.next()) {
                selectedEksekusiId = rs.getInt("id_eksekusi");
                int tid = rs.getInt("id_template");
                for (int i = 0; i < CmbTemplate.getItemCount(); i++) {
                    if (CmbTemplate.getItemAt(i).toString().startsWith(tid + " -")) { CmbTemplate.setSelectedIndex(i); break; }
                }
                loadEksekusiData();
            }
        } catch (Exception e) { System.out.println("Notif getDataCP: " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    private void loadEksekusiData() {
        if (selectedEksekusiId <= 0) return;
        for (CekBox[] days : chkGrid) for (CekBox cb : days) cb.setSelected(false);
        try {
            ps = koneksi.prepareStatement("select id_item, hari_ke, status from cp_eksekusi_item where id_eksekusi=?");
            ps.setInt(1, selectedEksekusiId);
            rs = ps.executeQuery();
            while (rs.next()) {
                int idItem = rs.getInt("id_item"), hk = rs.getInt("hari_ke");
                for (int i = 0; i < itemIds.size(); i++) {
                    if (itemIds.get(i) == idItem && hk >= 1 && hk <= 7) {
                        chkGrid.get(i)[hk - 1].setSelected("done".equals(rs.getString("status")));
                        break;
                    }
                }
            }
        } catch (Exception e) { System.out.println("Notif loadEksekusi: " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    private void isForm() {
        if (ChkInput.isSelected()) {
            ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 450));
            FormInput.setVisible(true); ChkInput.setVisible(true);
        } else {
            ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 20));
            FormInput.setVisible(false); ChkInput.setVisible(true);
        }
    }

    private void cetakCP() {
        if (tbData.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih data dulu...!!!!"); return; }
        String noRawat = tbData.getValueAt(tbData.getSelectedRow(), 0).toString();
        String noRM = tbData.getValueAt(tbData.getSelectedRow(), 1).toString();
        String nmPasien = tbData.getValueAt(tbData.getSelectedRow(), 2).toString();
        String namaCP = tbData.getValueAt(tbData.getSelectedRow(), 3).toString();
        String tglMulai = tbData.getValueAt(tbData.getSelectedRow(), 4).toString();
        String kepatuhan = tbData.getValueAt(tbData.getSelectedRow(), 7).toString();

        // Find eksekusi id & template id
        int eksId = 0, tplId = 0;
        try {
            PreparedStatement ps2 = koneksi.prepareStatement("select id_eksekusi, id_template from cp_eksekusi where no_rawat=? order by tgl_mulai desc limit 1");
            ps2.setString(1, noRawat); ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) { eksId = rs2.getInt("id_eksekusi"); tplId = rs2.getInt("id_template"); }
            rs2.close(); ps2.close();
        } catch (Exception e) { System.out.println(e); }

        String kodeIcd = Sequel.cariIsi("select kode_icd10 from cp_template where id_template=" + tplId);
        String targetLos = Sequel.cariIsi("select target_los_hari from cp_template where id_template=" + tplId);
        String jk = Sequel.cariIsi("select if(jk='L','Laki-Laki','Perempuan') from pasien where no_rkm_medis=?", noRM);
        String tglLahir = Sequel.cariIsi("select tgl_lahir from pasien where no_rkm_medis=?", noRM);

        java.util.Map<String, Object> param = new java.util.HashMap<>();
        param.put("namars", akses.getnamars()); param.put("alamatrs", akses.getalamatrs());
        param.put("propinsirs", akses.getpropinsirs()); param.put("kabupatenrs", akses.getkabupatenrs());
        param.put("kontakrs", akses.getkontakrs()); param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        param.put("nama_cp", namaCP); param.put("kode_icd", kodeIcd);
        param.put("no_rawat", noRawat); param.put("no_rkm_medis", noRM);
        param.put("nm_pasien", nmPasien); param.put("jk", jk);
        param.put("tgl_lahir", tglLahir); param.put("tgl_masuk", tglMulai);
        param.put("tgl_keluar", ""); param.put("kepatuhan", kepatuhan);
        param.put("target_los", targetLos);

        Valid.MyReportqry("rptClinicalPathway.jasper", "report", "::[ Clinical Pathway ]::",
            "select ti.kategori, ti.sub_kategori, ti.nama_aktivitas, ti.keterangan, " +
            "max(case when ei.hari_ke=1 then if(ei.status='done','\u2713','') else '' end) as h1, " +
            "max(case when ei.hari_ke=2 then if(ei.status='done','\u2713','') else '' end) as h2, " +
            "max(case when ei.hari_ke=3 then if(ei.status='done','\u2713','') else '' end) as h3, " +
            "max(case when ei.hari_ke=4 then if(ei.status='done','\u2713','') else '' end) as h4, " +
            "max(case when ei.hari_ke=5 then if(ei.status='done','\u2713','') else '' end) as h5, " +
            "max(case when ei.hari_ke=6 then if(ei.status='done','\u2713','') else '' end) as h6, " +
            "max(case when ei.hari_ke=7 then if(ei.status='done','\u2713','') else '' end) as h7 " +
            "from cp_template_item ti left join cp_eksekusi_item ei on ti.id_item=ei.id_item and ei.id_eksekusi=" + eksId + " " +
            "where ti.id_template=" + tplId + " group by ti.id_item order by ti.urutan", param);
    }

    private void cetakRekap() {
        if (tabMode.getRowCount() == 0) { JOptionPane.showMessageDialog(null, "Tidak ada data...!!!!"); return; }
        Sequel.queryu("delete from temporary where temp37='" + akses.getalamatip() + "'");
        for (int r = 0; r < tabMode.getRowCount(); r++) {
            Sequel.menyimpantf("temporary",
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?",
                "Rekap Clinical Pathway", 38, new String[]{
                String.valueOf(r + 1),
                tabMode.getValueAt(r, 0).toString(), tabMode.getValueAt(r, 1).toString(),
                tabMode.getValueAt(r, 2).toString(), tabMode.getValueAt(r, 3).toString(),
                tabMode.getValueAt(r, 4).toString(), tabMode.getValueAt(r, 5).toString(),
                tabMode.getValueAt(r, 6).toString(), tabMode.getValueAt(r, 7).toString(),
                "","","","","","","","","","","","","","","","","","","","","","","","","","","","",
                akses.getalamatip()
            });
        }
        java.util.Map<String, Object> param = new java.util.HashMap<>();
        param.put("namars", akses.getnamars()); param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs()); param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs()); param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        Valid.MyReportqry("rptRekapClinicalPathway.jasper", "report", "::[ Rekap Clinical Pathway ]::",
            "select * from temporary where temporary.temp37='" + akses.getalamatip() + "' order by temporary.no", param);
    }

    private void hapusCP() {
        if (tbData.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih data dulu...!!!!"); return; }
        String noRawat = tbData.getValueAt(tbData.getSelectedRow(), 0).toString();
        int ok = JOptionPane.showConfirmDialog(null, "Hapus Clinical Pathway untuk pasien " + noRawat + "?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement ps2 = koneksi.prepareStatement("select id_eksekusi from cp_eksekusi where no_rawat=? order by tgl_mulai desc limit 1");
                ps2.setString(1, noRawat);
                ResultSet rs2 = ps2.executeQuery();
                if (rs2.next()) {
                    int eksId = rs2.getInt("id_eksekusi");
                    Sequel.queryu("delete from cp_eksekusi_item where id_eksekusi=" + eksId);
                    Sequel.queryu("delete from cp_eksekusi where id_eksekusi=" + eksId);
                }
                rs2.close(); ps2.close();
                tampil(); emptTeks();
            } catch (Exception e) { System.out.println("Notif hapusCP: " + e); }
        }
    }

    private void autoDetect() {
        if (TNoRw.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(null, "Pilih pasien dulu...!!!!"); return; }
        String noRawat = TNoRw.getText();
        int detected = 0;

        // Ambil tgl_masuk kamar_inap untuk hitung hari ke
        String tglMasukStr = Sequel.cariIsi("select tgl_masuk from kamar_inap where no_rawat=? order by tgl_masuk asc limit 1", noRawat);
        java.time.LocalDate tglMasuk = null;
        try { tglMasuk = java.time.LocalDate.parse(tglMasukStr); } catch (Exception e) {}

        for (int i = 0; i < chkGrid.size(); i++) {
            String kat = itemKategori.get(i).toLowerCase();
            String akt = itemAktivitas.get(i).toLowerCase();
            boolean found = false;

            try {
                // 1. LABORATORIUM — cek periksa_lab
                if (kat.contains("laboratorium") || kat.contains("pemeriksaan penunjang")) {
                    if (akt.contains("darah") || akt.contains("hb") || akt.contains("leukosit") || akt.contains("trombosit") ||
                        akt.contains("gds") || akt.contains("gula darah") || akt.contains("kimia") || akt.contains("elektrolit") ||
                        akt.contains("ureum") || akt.contains("widal") || akt.contains("tubex") || akt.contains("feses") ||
                        akt.contains("sputum") || akt.contains("led") || akt.contains("ns1") || akt.contains("hitung jenis") ||
                        akt.contains("profil lipid") || akt.contains("proteinuria") || akt.contains("urin")) {
                        ps = koneksi.prepareStatement("select tgl_periksa from periksa_lab where no_rawat=? group by tgl_periksa order by tgl_periksa");
                        ps.setString(1, noRawat); rs = ps.executeQuery();
                        while (rs.next() && tglMasuk != null) {
                            java.time.LocalDate tglLab = rs.getDate("tgl_periksa").toLocalDate();
                            int hariKe = (int) java.time.temporal.ChronoUnit.DAYS.between(tglMasuk, tglLab) + 1;
                            if (hariKe >= 1 && hariKe <= 7 && chkGrid.get(i)[hariKe-1].isEnabled()) {
                                chkGrid.get(i)[hariKe-1].setSelected(true); found = true;
                            }
                        }
                        rs.close(); ps.close();
                    }
                }

                // 2. RADIOLOGI — cek periksa_radiologi
                if (kat.contains("radiologi") || kat.contains("imaging")) {
                    ps = koneksi.prepareStatement("select tgl_periksa from periksa_radiologi where no_rawat=? group by tgl_periksa order by tgl_periksa");
                    ps.setString(1, noRawat); rs = ps.executeQuery();
                    while (rs.next() && tglMasuk != null) {
                        java.time.LocalDate tglRad = rs.getDate("tgl_periksa").toLocalDate();
                        int hariKe = (int) java.time.temporal.ChronoUnit.DAYS.between(tglMasuk, tglRad) + 1;
                        if (hariKe >= 1 && hariKe <= 7 && chkGrid.get(i)[hariKe-1].isEnabled()) {
                            chkGrid.get(i)[hariKe-1].setSelected(true); found = true;
                        }
                    }
                    rs.close(); ps.close();
                }

                // 3. ASESMEN MEDIS (visite dokter) — cek rawat_inap_dr / pemeriksaan_ranap
                if (akt.contains("dokter dpjp") || akt.contains("dokter igd") || akt.contains("visite") || akt.contains("pemeriksaan dpjp") ||
                    (kat.contains("asesmen") && kat.contains("medis") && (akt.contains("dokter") || akt.contains("dr")))) {
                    ps = koneksi.prepareStatement("select tgl_perawatan from pemeriksaan_ranap where no_rawat=? group by tgl_perawatan order by tgl_perawatan");
                    ps.setString(1, noRawat); rs = ps.executeQuery();
                    while (rs.next() && tglMasuk != null) {
                        java.time.LocalDate tglVisite = rs.getDate("tgl_perawatan").toLocalDate();
                        int hariKe = (int) java.time.temporal.ChronoUnit.DAYS.between(tglMasuk, tglVisite) + 1;
                        if (hariKe >= 1 && hariKe <= 7 && chkGrid.get(i)[hariKe-1].isEnabled()) {
                            chkGrid.get(i)[hariKe-1].setSelected(true); found = true;
                        }
                    }
                    rs.close(); ps.close();
                }

                // 4. ASESMEN KEPERAWATAN — cek rawat_inap_pr
                if (akt.contains("perawat") && (kat.contains("asesmen") || kat.contains("keperawatan"))) {
                    ps = koneksi.prepareStatement("select tgl_perawatan from rawat_inap_pr where no_rawat=? group by tgl_perawatan order by tgl_perawatan");
                    ps.setString(1, noRawat); rs = ps.executeQuery();
                    while (rs.next() && tglMasuk != null) {
                        java.time.LocalDate tglPr = rs.getDate("tgl_perawatan").toLocalDate();
                        int hariKe = (int) java.time.temporal.ChronoUnit.DAYS.between(tglMasuk, tglPr) + 1;
                        if (hariKe >= 1 && hariKe <= 7 && chkGrid.get(i)[hariKe-1].isEnabled()) {
                            chkGrid.get(i)[hariKe-1].setSelected(true); found = true;
                        }
                    }
                    rs.close(); ps.close();
                }

                // 5. TERAPI / OBAT — cek detail_pemberian_obat
                if (kat.contains("terapi") || kat.contains("medikamentosa")) {
                    ps = koneksi.prepareStatement("select tgl_perawatan from detail_pemberian_obat where no_rawat=? group by tgl_perawatan order by tgl_perawatan");
                    ps.setString(1, noRawat); rs = ps.executeQuery();
                    while (rs.next() && tglMasuk != null) {
                        java.time.LocalDate tglObat = rs.getDate("tgl_perawatan").toLocalDate();
                        int hariKe = (int) java.time.temporal.ChronoUnit.DAYS.between(tglMasuk, tglObat) + 1;
                        if (hariKe >= 1 && hariKe <= 7 && chkGrid.get(i)[hariKe-1].isEnabled()) {
                            chkGrid.get(i)[hariKe-1].setSelected(true); found = true;
                        }
                    }
                    rs.close(); ps.close();
                }

                // 6. KONSELING FARMASI — cek konseling_farmasi
                if (kat.contains("farmasi") && (akt.contains("konseling") || akt.contains("rekonsiliasi") || akt.contains("telaah"))) {
                    ps = koneksi.prepareStatement("select tanggal from konseling_farmasi where no_rawat=? group by tanggal order by tanggal");
                    ps.setString(1, noRawat); rs = ps.executeQuery();
                    while (rs.next() && tglMasuk != null) {
                        java.time.LocalDate tglFarm = rs.getDate("tanggal").toLocalDate();
                        int hariKe = (int) java.time.temporal.ChronoUnit.DAYS.between(tglMasuk, tglFarm) + 1;
                        if (hariKe >= 1 && hariKe <= 7 && chkGrid.get(i)[hariKe-1].isEnabled()) {
                            chkGrid.get(i)[hariKe-1].setSelected(true); found = true;
                        }
                    }
                    rs.close(); ps.close();
                }

                if (found) detected++;
            } catch (Exception e) {
                System.out.println("AutoDetect item " + i + ": " + e);
            }
        }
        JOptionPane.showMessageDialog(null, "Auto deteksi selesai.\n" + detected + " item terdeteksi dari data rekam medis.");
    }

    public void emptTeks() {
        selectedEksekusiId = -1;
        TBB.setText(""); TTB.setText("");
        KdPetugas.setText(""); NmPetugas.setText("");
        for (CekBox[] days : chkGrid) for (CekBox cb : days) cb.setSelected(false);
    }

    public void setNoRm(String norwt, Date tgl) {
        TNoRw.setText(norwt);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", norwt));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
        // Viewer data
        TJK.setText(Sequel.cariIsi("select if(jk='L','Laki-Laki','Perempuan') from pasien where no_rkm_medis=?", TNoRM.getText()));
        TTglLahir.setText(Sequel.cariIsi("select tgl_lahir from pasien where no_rkm_medis=?", TNoRM.getText()));
        TDiagnosaMasuk.setText(Sequel.cariIsi("select diagnosa_awal from kamar_inap where no_rawat=? order by tgl_masuk desc limit 1", norwt));
        // Penyakit utama (prioritas 1)
        TPenyakitUtama.setText(Sequel.cariIsi("select concat(dp.kd_penyakit,' - ',p.nm_penyakit) from diagnosa_pasien dp inner join penyakit p on dp.kd_penyakit=p.kd_penyakit where dp.no_rawat=? and dp.prioritas=1 limit 1", norwt));
        // Penyakit penyerta (prioritas > 1)
        try {
            ps = koneksi.prepareStatement("select concat(dp.kd_penyakit,' - ',p.nm_penyakit) from diagnosa_pasien dp inner join penyakit p on dp.kd_penyakit=p.kd_penyakit where dp.no_rawat=? and dp.prioritas>1 order by dp.prioritas");
            ps.setString(1, norwt);
            rs = ps.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) { if (sb.length() > 0) sb.append(", "); sb.append(rs.getString(1)); }
            TPenyakitPenyerta.setText(sb.toString());
        } catch (Exception e) { System.out.println(e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
        // Tindakan
        try {
            ps = koneksi.prepareStatement("select concat(pp.kode,' - ',icd.deskripsi_panjang) from prosedur_pasien pp left join icd9 icd on pp.kode=icd.kode where pp.no_rawat=? order by pp.prioritas");
            ps.setString(1, norwt);
            rs = ps.executeQuery();
            StringBuilder sb = new StringBuilder();
            while (rs.next()) { if (sb.length() > 0) sb.append(", "); sb.append(rs.getString(1)); }
            TTindakan.setText(sb.toString());
        } catch (Exception e) {
            TTindakan.setText(Sequel.cariIsi("select concat(kode,' ') from prosedur_pasien where no_rawat=? limit 1", norwt));
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void isCek() {}
}
