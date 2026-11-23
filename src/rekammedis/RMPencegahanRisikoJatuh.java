package rekammedis;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import fungsi.WarnaTable;
import kepegawaian.DlgCariPetugas;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import widget.*;

public class RMPencegahanRisikoJatuh extends JDialog {

    private InternalFrame internalFrame1;
    private panelisi panelBawah;
    private Button BtnSimpan, BtnBatal, BtnHapus, BtnEdit, BtnPrint, BtnKeluar, BtnCari, BtnAll, BtnPetugas;
    private widget.Tanggal DTPCari1, DTPCari2, TglAsuhan;
    private TextBox TNoRw, TNoRM, TPasien, TCari, KdPetugas, NmPetugas;
    private Label LCount;

    private CekBox[] chkTindakan = new CekBox[10];
    private PanelBiasa FormInput;
    private ScrollPane ScrollInput, Scroll;
    private JPanel PanelInput;
    private CekBox ChkInput;

    private widget.Table tbData;
    private DefaultTableModel tabMode;

    private java.sql.Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private java.sql.PreparedStatement ps;
    private java.sql.ResultSet rs;
    private DlgCariPetugas petugasDlg = new DlgCariPetugas(null, false);
    public widget.editorpane LoadHTML;

    private JPopupMenu jPopupMenu1;
    private JMenuItem MnCetak;

    private static final String[] TINDAKAN = {
        "1. Pasangkan/pantau klip kuning pada gelang identitas pasien",
        "2. Observasi keselamatan lingkungan : biarkan pintu terbuka, pasang pengaman tempat tidur, posisi tempat tidur rendah, rem berfungsi baik, pencahayaan yang cukup dikamar",
        "3. Komunikasikan resiko jatuh pada pasien & keluarga",
        "4. Anjurkan pasien untuk menggunakan alas kaki yang tidak licin",
        "5. Gunakan alat bantu berjalan",
        "6. Pastikan selalu ada pendamping",
        "7. Lakukan tindakan pencegahan risiko jatuh rendah/sedang",
        "8. Pasangkan segitiga resiko jatuh pada tempat tidur pasien/tiang infus pasien",
        "9. Jaga lantai kamar mandi selalu kering",
        "10. Jangan tinggalkan pasien sendiri, dimanapun pasien berada"
    };

    private static final String[] TINDAKAN_SHORT = {
        "Pasangkan klip kuning",
        "Observasi keselamatan lingkungan",
        "Komunikasikan resiko jatuh",
        "Anjurkan alas kaki tidak licin",
        "Gunakan alat bantu berjalan",
        "Pastikan ada pendamping",
        "Pencegahan risiko jatuh rendah/sedang",
        "Pasangkan segitiga resiko jatuh",
        "Jaga lantai kamar mandi kering",
        "Jangan tinggalkan pasien sendiri"
    };

    public RMPencegahanRisikoJatuh(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(900, 600);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Pelaksanaan Pencegahan Resiko Jatuh Pasien ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // PANEL ATAS - Info Pasien
        panelisi panelAtas = new panelisi();
        panelAtas.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 4));
        panelAtas.setPreferredSize(new Dimension(1024, 33));
        Label lblNoRw = new Label(); lblNoRw.setText("No.Rawat :");
        TNoRw = new TextBox(); TNoRw.setPreferredSize(new Dimension(130, 23));
        TNoRM = new TextBox(); TNoRM.setPreferredSize(new Dimension(100, 23));
        TPasien = new TextBox(); TPasien.setPreferredSize(new Dimension(200, 23));
        panelAtas.add(lblNoRw); panelAtas.add(TNoRw); panelAtas.add(TNoRM); panelAtas.add(TPasien);

        // FORM INPUT - DatePicker + 10 Checkbox + Petugas
        FormInput = new PanelBiasa();
        FormInput.setPreferredSize(new Dimension(880, 380));
        FormInput.setLayout(null);

        int y = 10;
        JLabel h = new JLabel("PELAKSANAAN PENCEGAHAN RESIKO JATUH PASIEN");
        h.setFont(new java.awt.Font("Tahoma", 1, 12)); h.setBounds(10, y, 400, 20); FormInput.add(h);
        JLabel hk = new JLabel("RM.KEP.08.032.2019");
        hk.setFont(new java.awt.Font("Tahoma", 0, 8)); hk.setBounds(700, y, 150, 20); FormInput.add(hk);

        y += 30;
        Label lt = new Label(); lt.setText("Tanggal :"); lt.setBounds(10, y, 80, 23); FormInput.add(lt);
        TglAsuhan = new widget.Tanggal(); TglAsuhan.setDisplayFormat("dd-MM-yyyy");
        TglAsuhan.setBounds(95, y, 130, 23); FormInput.add(TglAsuhan);

        // Resiko Jatuh rendah/sedang
        y += 35;
        JLabel lblRR = new JLabel("Resiko Jatuh rendah/sedang");
        lblRR.setFont(new java.awt.Font("Tahoma", 1, 10)); lblRR.setBounds(10, y, 250, 18); FormInput.add(lblRR);

        y += 20;
        for (int t = 0; t < 6; t++) {
            chkTindakan[t] = new CekBox();
            chkTindakan[t].setText(TINDAKAN[t]);
            chkTindakan[t].setFont(new java.awt.Font("Tahoma", 0, 9));
            chkTindakan[t].setOpaque(false);
            chkTindakan[t].setBounds(20, y, 700, 18);
            FormInput.add(chkTindakan[t]);
            y += 20;
        }

        // Resiko Jatuh Tinggi
        y += 5;
        JLabel lblRT = new JLabel("Resiko Jatuh Tinggi");
        lblRT.setFont(new java.awt.Font("Tahoma", 1, 10)); lblRT.setBounds(10, y, 250, 18); FormInput.add(lblRT);

        y += 20;
        for (int t = 6; t < 10; t++) {
            chkTindakan[t] = new CekBox();
            chkTindakan[t].setText(TINDAKAN[t]);
            chkTindakan[t].setFont(new java.awt.Font("Tahoma", 0, 9));
            chkTindakan[t].setOpaque(false);
            chkTindakan[t].setBounds(20, y, 700, 18);
            FormInput.add(chkTindakan[t]);
            y += 20;
        }

        // Petugas
        y += 10;
        Label lp = new Label(); lp.setText("Petugas :"); lp.setBounds(10, y, 65, 23); FormInput.add(lp);
        KdPetugas = new TextBox(); KdPetugas.setBounds(80, y, 80, 23); FormInput.add(KdPetugas);
        NmPetugas = new TextBox(); NmPetugas.setBounds(162, y, 165, 23); FormInput.add(NmPetugas);
        BtnPetugas = new Button(); BtnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png")));
        BtnPetugas.setBounds(329, y, 28, 23); FormInput.add(BtnPetugas);
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

        ChkInput = new CekBox(); ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setText(".: Input Data"); ChkInput.setSelected(true); ChkInput.setBorderPainted(true); ChkInput.setBorderPaintedFlat(true); ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(SwingConstants.LEFT); ChkInput.setPreferredSize(new Dimension(192, 20));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        ChkInput.addActionListener(ev -> isForm());

        ScrollInput = new ScrollPane(); ScrollInput.setViewportView(FormInput); ScrollInput.setPreferredSize(new Dimension(880, 380));
        PanelInput = new JPanel(new BorderLayout()); PanelInput.setOpaque(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 400));
        PanelInput.add(ScrollInput, BorderLayout.CENTER); PanelInput.add(ChkInput, BorderLayout.PAGE_END);

        // TABLE
        Scroll = new ScrollPane();
        tbData = new widget.Table();
        tabMode = new DefaultTableModel(new Object[][]{},
            new String[]{"No.Rawat","No.R.M.","Nama","Tanggal","No","Tindakan","Petugas"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbData.setModel(tabMode);
        tbData.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 7; i++) {
            TableColumn column = tbData.getColumnModel().getColumn(i);
            if (i == 0) column.setPreferredWidth(105);
            else if (i == 1) column.setPreferredWidth(70);
            else if (i == 2) column.setPreferredWidth(150);
            else if (i == 3) column.setPreferredWidth(80);
            else if (i == 4) column.setPreferredWidth(25);
            else if (i == 5) column.setPreferredWidth(280);
            else if (i == 6) column.setPreferredWidth(150);
        }
        tbData.setDefaultRenderer(Object.class, new WarnaTable());

        tbData.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (tabMode.getRowCount() != 0) getData();
            }
        });

        jPopupMenu1 = new JPopupMenu();
        MnCetak = new JMenuItem();
        MnCetak.setBackground(new Color(255, 255, 254));
        MnCetak.setFont(new java.awt.Font("Tahoma", 0, 11));
        MnCetak.setForeground(new Color(50, 50, 50));
        MnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        MnCetak.setText("Cetak");
        MnCetak.setPreferredSize(new Dimension(220, 26));
        MnCetak.addActionListener(ev -> cetakReport());
        jPopupMenu1.add(MnCetak);
        tbData.setComponentPopupMenu(jPopupMenu1);

        Scroll.setViewportView(tbData);

        // BOTTOM
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
        Label lc = new Label(); lc.setText("Tanggal :"); lc.setPreferredSize(new Dimension(55, 23));
        DTPCari1 = new widget.Tanggal(); DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setPreferredSize(new Dimension(90, 23));
        Label sd = new Label(); sd.setText("s.d."); sd.setPreferredSize(new Dimension(25, 23));
        DTPCari2 = new widget.Tanggal(); DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setPreferredSize(new Dimension(90, 23));
        Label lk = new Label(); lk.setText("Key Word :"); lk.setPreferredSize(new Dimension(70, 23));
        TCari = new TextBox(); TCari.setPreferredSize(new Dimension(150, 23));
        BtnCari = new Button(); BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnAll = new Button(); BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); BtnAll.setPreferredSize(new Dimension(28, 23));
        LCount = new Label(); LCount.setText("Record : 0"); LCount.setPreferredSize(new Dimension(100, 23));

        panelBawah.add(BtnSimpan); panelBawah.add(BtnBatal); panelBawah.add(BtnHapus); panelBawah.add(BtnEdit);
        panelBawah.add(BtnPrint); panelBawah.add(BtnKeluar);
        panelBawah.add(lc); panelBawah.add(DTPCari1); panelBawah.add(sd); panelBawah.add(DTPCari2);
        panelBawah.add(lk); panelBawah.add(TCari); panelBawah.add(BtnCari); panelBawah.add(BtnAll); panelBawah.add(LCount);

        // ACTIONS
        BtnKeluar.addActionListener(ev -> dispose());
        BtnBatal.addActionListener(ev -> emptTeks());
        BtnCari.addActionListener(ev -> tampil());
        BtnAll.addActionListener(ev -> { TCari.setText(""); tampil(); });
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) tampil(); }
        });

        BtnSimpan.addActionListener(ev -> {
            if (TNoRw.getText().trim().equals("")) { Valid.textKosong(TNoRw, "Pasien"); return; }
            if (KdPetugas.getText().trim().equals("")) { Valid.textKosong(KdPetugas, "Petugas"); return; }
            String tgl = Valid.SetTgl(TglAsuhan.getSelectedItem() + "");
            int saved = 0;
            for (int t = 0; t < 10; t++) {
                if (chkTindakan[t].isSelected()) {
                    if (Sequel.menyimpantf("pencegahan_risiko_jatuh", "?,?,?,?", "Data", 4, new String[]{
                        TNoRw.getText(), tgl, String.valueOf(t + 1), KdPetugas.getText()
                    })) saved++;
                }
            }
            if (saved > 0) {
                JOptionPane.showMessageDialog(null, saved + " tindakan tersimpan.");
                tampil(); emptTeks();
            }
        });

        BtnHapus.addActionListener(ev -> {
            if (tbData.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih data dulu...!!!!"); return; }
            if (Sequel.queryu2tf("delete from pencegahan_risiko_jatuh where no_rawat=? and tanggal=? and no_tindakan=?", 3, new String[]{
                tbData.getValueAt(tbData.getSelectedRow(), 0).toString(),
                tbData.getValueAt(tbData.getSelectedRow(), 3).toString(),
                tbData.getValueAt(tbData.getSelectedRow(), 4).toString()
            })) { tampil(); emptTeks(); }
        });

        BtnEdit.addActionListener(ev -> {
            if (TNoRw.getText().trim().equals("") || tbData.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Pilih data dulu...!!!!"); return;
            }
            if (Sequel.queryu2tf("delete from pencegahan_risiko_jatuh where no_rawat=? and tanggal=? and no_tindakan=?", 3, new String[]{
                tbData.getValueAt(tbData.getSelectedRow(), 0).toString(),
                tbData.getValueAt(tbData.getSelectedRow(), 3).toString(),
                tbData.getValueAt(tbData.getSelectedRow(), 4).toString()
            })) {
                String tgl = Valid.SetTgl(TglAsuhan.getSelectedItem() + "");
                // Re-save all checked
                for (int t = 0; t < 10; t++) {
                    if (chkTindakan[t].isSelected()) {
                        Sequel.menyimpantf("pencegahan_risiko_jatuh", "?,?,?,?", "Data", 4, new String[]{
                            TNoRw.getText(), tgl, String.valueOf(t + 1), KdPetugas.getText()
                        });
                    }
                }
                tampil(); emptTeks();
            }
        });

        BtnPrint.addActionListener(ev -> cetakReport());

        // ASSEMBLY
        InternalFrame ifd = new InternalFrame(); ifd.setLayout(new BorderLayout()); ifd.setBackground(new Color(255, 255, 255));
        ifd.add(PanelInput, BorderLayout.NORTH); ifd.add(Scroll, BorderLayout.CENTER);
        internalFrame1.add(panelAtas, BorderLayout.PAGE_START); internalFrame1.add(ifd, BorderLayout.CENTER); internalFrame1.add(panelBawah, BorderLayout.PAGE_END);
        setLayout(new BorderLayout()); add(internalFrame1, BorderLayout.CENTER); setDefaultCloseOperation(DISPOSE_ON_CLOSE); setUndecorated(true);
    }

    private void isForm() {
        if (ChkInput.isSelected()) {
            ChkInput.setVisible(false); PanelInput.setPreferredSize(new Dimension(WIDTH, 400));
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
                "select p.no_rawat,r.no_rkm_medis,pa.nm_pasien,p.tanggal,p.no_tindakan,pt.nama as nm_petugas " +
                "from pencegahan_risiko_jatuh p " +
                "inner join reg_periksa r on p.no_rawat=r.no_rawat " +
                "inner join pasien pa on r.no_rkm_medis=pa.no_rkm_medis " +
                "left join petugas pt on p.nip=pt.nip " +
                "where p.tanggal between ? and ? and (p.no_rawat like ? or r.no_rkm_medis like ? or pa.nm_pasien like ?) " +
                "order by p.tanggal,p.no_tindakan");
            ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
            ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
            ps.setString(3, "%" + TCari.getText().trim() + "%");
            ps.setString(4, "%" + TCari.getText().trim() + "%");
            ps.setString(5, "%" + TCari.getText().trim() + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                int noTindakan = rs.getInt("no_tindakan");
                String nmTindakan = (noTindakan >= 1 && noTindakan <= 10) ? TINDAKAN_SHORT[noTindakan - 1] : "-";
                tabMode.addRow(new String[]{
                    rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                    rs.getString("tanggal"), String.valueOf(noTindakan), nmTindakan, rs.getString("nm_petugas")
                });
            }
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) {
            System.out.println("Notif Pencegahan Risiko Jatuh : " + e);
        } finally {
            try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {}
        }
    }

    // Alias for backward compatibility
    public void loadData() { tampil(); }

    public void getData() {
        if (tbData.getSelectedRow() != -1) {
            TNoRw.setText(tbData.getValueAt(tbData.getSelectedRow(), 0).toString());
            TNoRM.setText(tbData.getValueAt(tbData.getSelectedRow(), 1).toString());
            TPasien.setText(tbData.getValueAt(tbData.getSelectedRow(), 2).toString());
            try {
                TglAsuhan.setDate(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(tbData.getValueAt(tbData.getSelectedRow(), 3).toString()));
            } catch (Exception e) {}
            // Load all tindakan for this no_rawat + tanggal
            for (int t = 0; t < 10; t++) chkTindakan[t].setSelected(false);
            try {
                ps = koneksi.prepareStatement("select no_tindakan,nip from pencegahan_risiko_jatuh where no_rawat=? and tanggal=?");
                ps.setString(1, tbData.getValueAt(tbData.getSelectedRow(), 0).toString());
                ps.setString(2, tbData.getValueAt(tbData.getSelectedRow(), 3).toString());
                rs = ps.executeQuery();
                while (rs.next()) {
                    int t = rs.getInt("no_tindakan") - 1;
                    if (t >= 0 && t < 10) chkTindakan[t].setSelected(true);
                    KdPetugas.setText(rs.getString("nip"));
                }
                NmPetugas.setText(Sequel.cariIsi("select nama from petugas where nip=?", KdPetugas.getText()));
            } catch (Exception e) {
                System.out.println("Notif getData : " + e);
            } finally {
                try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {}
            }
        }
    }

    private void cetakReport() {
        if (TNoRw.getText().trim().equals("")) { JOptionPane.showMessageDialog(null, "Pilih data dulu...!!!!"); return; }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Map<String, Object> param = new HashMap<>();
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kabupatenrs", akses.getkabupatenrs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        Valid.MyReportqry("rptPencegahanRisikoJatuh.jasper", "report", "::[ Pencegahan Resiko Jatuh ]::",
            "select p.no_rawat,r.no_rkm_medis,pa.nm_pasien,pa.tgl_lahir,pa.no_ktp,p.tanggal,p.no_tindakan,pt.nama as nm_petugas " +
            "from pencegahan_risiko_jatuh p " +
            "inner join reg_periksa r on p.no_rawat=r.no_rawat " +
            "inner join pasien pa on r.no_rkm_medis=pa.no_rkm_medis " +
            "left join petugas pt on p.nip=pt.nip " +
            "where p.no_rawat='" + TNoRw.getText() + "' order by p.tanggal,p.no_tindakan", param);
        this.setCursor(Cursor.getDefaultCursor());
    }

    public void emptTeks() {
        TglAsuhan.setDate(new Date());
        for (int t = 0; t < 10; t++) chkTindakan[t].setSelected(false);
        KdPetugas.setText(""); NmPetugas.setText("");
    }

    public void setNoRm(String norwt, String norm, String nama) {
        TNoRw.setText(norwt); TNoRM.setText(norm); TPasien.setText(nama);
    }

    public void setNoRm(String norwt, Date tgl) {
        TNoRw.setText(norwt);
        TNoRM.setText(Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", norwt));
        TPasien.setText(Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TNoRM.getText()));
        if (tgl != null) TglAsuhan.setDate(tgl);
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getrujukan_keluar());
        BtnHapus.setEnabled(akses.getrujukan_keluar());
        BtnEdit.setEnabled(akses.getrujukan_keluar());
        BtnPrint.setEnabled(akses.getrujukan_keluar());
    }
}
