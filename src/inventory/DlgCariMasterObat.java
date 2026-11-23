package inventory;

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
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
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import widget.*;

public class DlgCariMasterObat extends JDialog {

    private widget.Table tbObat;
    private DefaultTableModel tabMode;
    private TextBox TCari;
    private ComboBox CmbJenis, CmbKategori;
    private widget.Label LCount;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private javax.swing.Timer searchTimer;
    private static final int LIMIT_TAMPIL = 200;

    public DlgCariMasterObat(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(850, 500);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        InternalFrame internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Cari Master Obat / Barang Medis ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // Panel atas - filter
        panelisi panelAtas = new panelisi();
        panelAtas.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panelAtas.setPreferredSize(new Dimension(850, 35));

        widget.Label lblCari = new widget.Label(); lblCari.setText("Cari Obat :"); lblCari.setPreferredSize(new Dimension(60, 23));
        TCari = new TextBox(); TCari.setPreferredSize(new Dimension(200, 23));
        TCari.setToolTipText("Ketik langsung untuk mencari (bisa beberapa kata), panah bawah untuk pindah ke tabel");
        // Live search: cari otomatis 300ms setelah user berhenti mengetik
        searchTimer = new javax.swing.Timer(300, ev -> tampil());
        searchTimer.setRepeats(false);
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyReleased(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) { searchTimer.stop(); tampil(); return; }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) { pindahKeTabel(); return; }
                if (e.isActionKey() || e.getKeyCode() == KeyEvent.VK_SHIFT || e.getKeyCode() == KeyEvent.VK_CONTROL) return;
                searchTimer.restart();
            }
        });

        widget.Label lblJenis = new widget.Label(); lblJenis.setText("Jenis :"); lblJenis.setPreferredSize(new Dimension(35, 23));
        CmbJenis = new ComboBox(); CmbJenis.setPreferredSize(new Dimension(130, 23));
        loadJenis();
        CmbJenis.addItemListener(ev -> { if (ev.getStateChange() == java.awt.event.ItemEvent.SELECTED) tampil(); });

        widget.Label lblKat = new widget.Label(); lblKat.setText("Kategori :"); lblKat.setPreferredSize(new Dimension(50, 23));
        CmbKategori = new ComboBox(); CmbKategori.setPreferredSize(new Dimension(100, 23));
        loadKategori();
        CmbKategori.addItemListener(ev -> { if (ev.getStateChange() == java.awt.event.ItemEvent.SELECTED) tampil(); });

        Button BtnCari = new Button();
        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnCari.addActionListener(ev -> tampil());

        Button BtnAll = new Button();
        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setPreferredSize(new Dimension(28, 23));
        BtnAll.addActionListener(ev -> { TCari.setText(""); tampil(); });

        LCount = new widget.Label(); LCount.setText("Record : 0"); LCount.setPreferredSize(new Dimension(100, 23));

        panelAtas.add(lblCari); panelAtas.add(TCari);
        panelAtas.add(lblJenis); panelAtas.add(CmbJenis);
        panelAtas.add(lblKat); panelAtas.add(CmbKategori);
        panelAtas.add(BtnCari); panelAtas.add(BtnAll); panelAtas.add(LCount);

        // Tabel
        ScrollPane scroll = new ScrollPane();
        tbObat = new widget.Table();
        tabMode = new DefaultTableModel(null,
            new String[]{"Kode", "Nama Obat/Barang", "Satuan Kecil", "Satuan Besar", "Isi", "Jenis", "Kategori", "Golongan", "Letak"}
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbObat.setModel(tabMode);
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] cw = {80, 250, 60, 60, 35, 100, 80, 80, 100};
        for (int i = 0; i < cw.length; i++) {
            tbObat.getColumnModel().getColumn(i).setPreferredWidth(cw[i]);
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        // Double click untuk pilih
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) dispose();
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ENTER) dispose();
            }
        });

        scroll.setViewportView(tbObat);

        // Panel bawah
        panelisi panelBawah = new panelisi();
        panelBawah.setPreferredSize(new Dimension(44, 40));
        panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

        Button BtnPilih = new Button();
        BtnPilih.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnPilih.setText("Pilih"); BtnPilih.setPreferredSize(new Dimension(80, 30));
        BtnPilih.addActionListener(ev -> dispose());

        Button BtnKeluar = new Button();
        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setText("Keluar"); BtnKeluar.setPreferredSize(new Dimension(80, 30));
        BtnKeluar.addActionListener(ev -> { tbObat.clearSelection(); dispose(); });

        panelBawah.add(BtnPilih); panelBawah.add(BtnKeluar);

        internalFrame1.add(panelAtas, BorderLayout.PAGE_START);
        internalFrame1.add(scroll, BorderLayout.CENTER);
        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);
        setLayout(new BorderLayout()); add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); setUndecorated(true);

        // Kursor langsung siap di kotak cari setiap dialog dibuka
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override public void componentShown(java.awt.event.ComponentEvent e) {
                SwingUtilities.invokeLater(() -> TCari.requestFocusInWindow());
            }
        });
    }

    private void pindahKeTabel() {
        if (tabMode.getRowCount() > 0) {
            tbObat.requestFocusInWindow();
            tbObat.setRowSelectionInterval(0, 0);
            tbObat.scrollRectToVisible(tbObat.getCellRect(0, 0, true));
        }
    }

    private void loadJenis() {
        try {
            java.util.List<String> items = new java.util.ArrayList<>();
            items.add("Semua");
            ps = koneksi.prepareStatement("select kdjns, nama from jenis order by nama");
            rs = ps.executeQuery();
            while (rs.next()) items.add(rs.getString("kdjns") + " - " + rs.getString("nama"));
            CmbJenis.setModel(new DefaultComboBoxModel(items.toArray(new String[0])));
        } catch (Exception e) { System.out.println("Notif loadJenis: " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    private void loadKategori() {
        try {
            java.util.List<String> items = new java.util.ArrayList<>();
            items.add("Semua");
            ps = koneksi.prepareStatement("select kode, nama from kategori_barang order by nama");
            rs = ps.executeQuery();
            while (rs.next()) items.add(rs.getString("kode") + " - " + rs.getString("nama"));
            CmbKategori.setModel(new DefaultComboBoxModel(items.toArray(new String[0])));
        } catch (Exception e) { System.out.println("Notif loadKategori: " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        String filterJenis = "";
        if (CmbJenis.getSelectedIndex() > 0) {
            String kdjns = CmbJenis.getSelectedItem().toString().split(" - ")[0].trim();
            filterJenis = " and db.kdjns='" + kdjns + "'";
        }
        String filterKategori = "";
        if (CmbKategori.getSelectedIndex() > 0) {
            String kdkat = CmbKategori.getSelectedItem().toString().split(" - ")[0].trim();
            filterKategori = " and db.kode_kategori='" + kdkat + "'";
        }
        // Kata kunci dipecah per kata, semua kata harus cocok (AND).
        // Token berupa angka dilewati supaya "ceftriaxone 1 gr" tetap ketemu.
        StringBuilder filterKw = new StringBuilder();
        java.util.List<String> paramKw = new java.util.ArrayList<>();
        for (String token : TCari.getText().trim().split("\\s+")) {
            if (token.isEmpty() || token.matches("\\d+")) continue;
            filterKw.append(" and (db.kode_brng like ? or db.nama_brng like ?)");
            paramKw.add("%" + token + "%");
            paramKw.add("%" + token + "%");
        }
        try {
            ps = koneksi.prepareStatement(
                "select db.kode_brng, db.nama_brng, ks.satuan as sat_kecil, ksb.satuan as sat_besar, db.isi, " +
                "ifnull(j.nama,'') as jenis, ifnull(kb.nama,'') as kategori, ifnull(gb.nama,'') as golongan, db.letak_barang " +
                "from databarang db " +
                "left join kodesatuan ks on db.kode_sat=ks.kode_sat " +
                "left join kodesatuan ksb on db.kode_satbesar=ksb.kode_sat " +
                "left join jenis j on db.kdjns=j.kdjns " +
                "left join kategori_barang kb on db.kode_kategori=kb.kode " +
                "left join golongan_barang gb on db.kode_golongan=gb.kode " +
                "where db.status='1' " +
                filterKw + filterJenis + filterKategori +
                " order by db.nama_brng limit " + LIMIT_TAMPIL);
            for (int i = 0; i < paramKw.size(); i++) ps.setString(i + 1, paramKw.get(i));
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString("kode_brng"), rs.getString("nama_brng"),
                    rs.getString("sat_kecil"), rs.getString("sat_besar"),
                    rs.getString("isi"),
                    rs.getString("jenis"), rs.getString("kategori"), rs.getString("golongan"),
                    rs.getString("letak_barang")
                });
            }
            LCount.setText(tabMode.getRowCount() >= LIMIT_TAMPIL
                ? "Record : " + LIMIT_TAMPIL + "+ (persempit)"
                : "Record : " + tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif tampilMasterObat: " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void emptTeks() {
        TCari.setText("");
        Valid.tabelKosong(tabMode);
    }

    public widget.Table getTable() {
        return tbObat;
    }

    public void isCek() {}
}
