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
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import widget.*;

public class RMClinicalPathwayTemplate extends JDialog {

    private InternalFrame internalFrame1;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;

    // Master Template
    private widget.Table tbTemplate;
    private DefaultTableModel tabTemplate;
    private TextBox TNamaCP, TKodeICD, TKodeICD9, TTargetLOS;
    private Button BtnSimpanTP, BtnHapusTP, BtnEditTP;

    // Detail Item
    private widget.Table tbItem;
    private DefaultTableModel tabItem;
    private TextBox TUrutan, TNamaAktivitas, THariKe, TKeterangan;
    private ComboBox CmbKategori, CmbSubKategori;
    private Button BtnSimpanItem, BtnHapusItem, BtnEditItem;

    private int selectedTemplateId = -1;

    public RMClinicalPathwayTemplate(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(1000, 650);
        setLocationRelativeTo(null);
        tampilTemplate();
    }

    private void initComponents() {
        internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Master Template Clinical Pathway ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // === PANEL KIRI: Master Template ===
        JPanel panelKiri = new JPanel(new BorderLayout(2, 2));
        panelKiri.setPreferredSize(new Dimension(400, 600));

        JPanel inputTP = new JPanel(null);
        inputTP.setPreferredSize(new Dimension(400, 130));
        inputTP.setBorder(BorderFactory.createTitledBorder("Template CP"));

        Label l1 = new Label(); l1.setText("Nama CP :"); l1.setBounds(5, 18, 60, 23); inputTP.add(l1);
        TNamaCP = new TextBox(); TNamaCP.setBounds(70, 18, 320, 23); inputTP.add(TNamaCP);
        Label l2 = new Label(); l2.setText("Kode ICD :"); l2.setBounds(5, 43, 60, 23); inputTP.add(l2);
        TKodeICD = new TextBox(); TKodeICD.setBounds(70, 43, 80, 23); inputTP.add(TKodeICD);
        Label l2b = new Label(); l2b.setText("ICD-9 :"); l2b.setBounds(155, 43, 40, 23); inputTP.add(l2b);
        TKodeICD9 = new TextBox(); TKodeICD9.setBounds(200, 43, 80, 23); inputTP.add(TKodeICD9);
        Label l3 = new Label(); l3.setText("Target LOS :"); l3.setBounds(5, 68, 65, 23); inputTP.add(l3);
        TTargetLOS = new TextBox(); TTargetLOS.setBounds(70, 68, 40, 23); inputTP.add(TTargetLOS);
        JLabel l3u = new JLabel("hari"); l3u.setBounds(112, 68, 30, 23); inputTP.add(l3u);

        BtnSimpanTP = new Button(); BtnSimpanTP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpanTP.setText("Simpan"); BtnSimpanTP.setBounds(5, 98, 90, 25); inputTP.add(BtnSimpanTP);
        BtnEditTP = new Button(); BtnEditTP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnEditTP.setText("Ganti"); BtnEditTP.setBounds(100, 98, 80, 25); inputTP.add(BtnEditTP);
        BtnHapusTP = new Button(); BtnHapusTP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapusTP.setText("Hapus"); BtnHapusTP.setBounds(185, 98, 80, 25); inputTP.add(BtnHapusTP);
        Button BtnKeluar = new Button(); BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setText("Keluar"); BtnKeluar.setBounds(310, 98, 80, 25); inputTP.add(BtnKeluar);
        BtnKeluar.addActionListener(ev -> dispose());

        tabTemplate = new DefaultTableModel(null, new String[]{"ID", "Nama CP", "ICD-10", "ICD-9", "LOS", "Aktif"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbTemplate = new widget.Table(); tbTemplate.setModel(tabTemplate);
        tbTemplate.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < 6; i++) {
            int[] w = {30, 180, 50, 50, 35, 35};
            tbTemplate.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        }
        tbTemplate.setDefaultRenderer(Object.class, new WarnaTable());
        tbTemplate.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { getDataTemplate(); }
        });
        ScrollPane scrollTP = new ScrollPane(); scrollTP.setViewportView(tbTemplate);

        panelKiri.add(inputTP, BorderLayout.NORTH);
        panelKiri.add(scrollTP, BorderLayout.CENTER);

        // === PANEL KANAN: Detail Item ===
        JPanel panelKanan = new JPanel(new BorderLayout(2, 2));

        JPanel inputItem = new JPanel(null);
        inputItem.setPreferredSize(new Dimension(580, 150));
        inputItem.setBorder(BorderFactory.createTitledBorder("Detail Item Kegiatan"));

        Label li1 = new Label(); li1.setText("Urutan :"); li1.setBounds(5, 18, 50, 23); inputItem.add(li1);
        TUrutan = new TextBox(); TUrutan.setBounds(90, 18, 40, 23); inputItem.add(TUrutan);
        Label li2 = new Label(); li2.setText("Kategori :"); li2.setBounds(5, 43, 80, 23); inputItem.add(li2);
        CmbKategori = new ComboBox(); CmbKategori.setEditable(true); CmbKategori.setBounds(90, 43, 220, 23); inputItem.add(CmbKategori);
        CmbKategori.setModel(new DefaultComboBoxModel(new String[]{
            "1. ASESMEN AWAL","2. LABORATORIUM","3. RADIOLOGI/IMAGING","4. KONSULTASI",
            "5. ASESMEN LANJUTAN","6. DIAGNOSIS","7. DISCHARGE PLANNING","8. EDUKASI TERINTEGRASI",
            "9. TERAPI MEDIKAMENTOSA","10. TLI","11. MONITORING & EVALUASI",
            "12. MOBILISASI/REHABILITASI","13. OUTCOME/HASIL","14. KRITERIA PULANG","15. RENCANA PULANG"
        }));
        Label li3 = new Label(); li3.setText("Sub Kategori :"); li3.setBounds(5, 68, 80, 23); inputItem.add(li3);
        CmbSubKategori = new ComboBox(); CmbSubKategori.setEditable(true); CmbSubKategori.setBounds(90, 68, 220, 23); inputItem.add(CmbSubKategori);
        CmbSubKategori.setModel(new DefaultComboBoxModel(new String[]{
            "","a. ASESMEN AWAL MEDIS","b. ASESMEN AWAL KEPERAWATAN","a. ASESMEN MEDIS","b. ASESMEN KEPERAWATAN",
            "c. ASESMEN GIZI","d. ASESMEN FARMASI","a. DIAGNOSIS MEDIS","b. DIAGNOSIS KEPERAWATAN","c. DIAGNOSIS GIZI",
            "a. EDUKASI/INFORMASI MEDIS","b. EDUKASI & KONSELING GIZI","c. EDUKASI KEPERAWATAN","d. EDUKASI FARMASI",
            "a. INJEKSI","b. CAIRAN INFUS","c. OBAT ORAL","a. TLI MEDIS","b. TLI KEPERAWATAN","c. TLI GIZI","d. TLI FARMASI",
            "a. DOKTER DPJP","b. KEPERAWATAN","c. GIZI","d. FARMASI","a. MEDIS","b. KEPERAWATAN","c. FISIOTERAPI"
        }));
        Label li4 = new Label(); li4.setText("Aktivitas :"); li4.setBounds(315, 18, 55, 23); inputItem.add(li4);
        TNamaAktivitas = new TextBox(); TNamaAktivitas.setBounds(375, 18, 200, 23); inputItem.add(TNamaAktivitas);
        Label li5 = new Label(); li5.setText("Hari ke :"); li5.setBounds(315, 43, 55, 23); inputItem.add(li5);
        THariKe = new TextBox(); THariKe.setBounds(375, 43, 100, 23); inputItem.add(THariKe);
        JLabel li5h = new JLabel("mis: 1,2,3 atau 1,2,3,4,5,6,7"); li5h.setFont(new Font("Tahoma",0,8)); li5h.setBounds(480, 43, 150, 23); inputItem.add(li5h);
        Label li6 = new Label(); li6.setText("Keterangan :"); li6.setBounds(315, 68, 60, 23); inputItem.add(li6);
        TKeterangan = new TextBox(); TKeterangan.setBounds(375, 68, 200, 23); inputItem.add(TKeterangan);

        BtnSimpanItem = new Button(); BtnSimpanItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpanItem.setText("Simpan"); BtnSimpanItem.setBounds(5, 118, 90, 25); inputItem.add(BtnSimpanItem);
        BtnEditItem = new Button(); BtnEditItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png")));
        BtnEditItem.setText("Ganti"); BtnEditItem.setBounds(100, 118, 80, 25); inputItem.add(BtnEditItem);
        BtnHapusItem = new Button(); BtnHapusItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapusItem.setText("Hapus"); BtnHapusItem.setBounds(185, 118, 80, 25); inputItem.add(BtnHapusItem);

        tabItem = new DefaultTableModel(null, new String[]{"ID","Urutan","Kategori","Sub Kategori","Aktivitas","Hari Ke","Keterangan"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbItem = new widget.Table(); tbItem.setModel(tabItem);
        tbItem.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < 7; i++) {
            int[] w = {30, 40, 120, 120, 200, 80, 120};
            tbItem.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        }
        tbItem.setDefaultRenderer(Object.class, new WarnaTable());
        tbItem.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { getDataItem(); }
        });
        ScrollPane scrollItem = new ScrollPane(); scrollItem.setViewportView(tbItem);

        panelKanan.add(inputItem, BorderLayout.NORTH);
        panelKanan.add(scrollItem, BorderLayout.CENTER);

        // === ACTIONS ===
        BtnSimpanTP.addActionListener(ev -> simpanTemplate());
        BtnEditTP.addActionListener(ev -> editTemplate());
        BtnHapusTP.addActionListener(ev -> hapusTemplate());
        BtnSimpanItem.addActionListener(ev -> simpanItem());
        BtnEditItem.addActionListener(ev -> editItem());
        BtnHapusItem.addActionListener(ev -> hapusItem());

        // === ASSEMBLY ===
        internalFrame1.add(panelKiri, BorderLayout.WEST);
        internalFrame1.add(panelKanan, BorderLayout.CENTER);
        setLayout(new BorderLayout()); add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); setUndecorated(true);
    }

    private void tampilTemplate() {
        Valid.tabelKosong(tabTemplate);
        try {
            ps = koneksi.prepareStatement("select * from cp_template order by nama_cp");
            rs = ps.executeQuery();
            while (rs.next()) {
                tabTemplate.addRow(new String[]{
                    rs.getString("id_template"), rs.getString("nama_cp"), rs.getString("kode_icd10"),
                    rs.getString("kode_icd9cm"), rs.getString("target_los_hari"),
                    rs.getInt("aktif") == 1 ? "Ya" : "Tidak"
                });
            }
        } catch (Exception e) { System.out.println("Notif tampilTemplate: " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    private void tampilItem() {
        Valid.tabelKosong(tabItem);
        if (selectedTemplateId <= 0) return;
        try {
            ps = koneksi.prepareStatement("select * from cp_template_item where id_template=? order by urutan");
            ps.setInt(1, selectedTemplateId); rs = ps.executeQuery();
            while (rs.next()) {
                tabItem.addRow(new String[]{
                    rs.getString("id_item"), rs.getString("urutan"), rs.getString("kategori"),
                    rs.getString("sub_kategori"), rs.getString("nama_aktivitas"),
                    rs.getString("hari_ke"), rs.getString("keterangan")
                });
            }
        } catch (Exception e) { System.out.println("Notif tampilItem: " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    private void getDataTemplate() {
        if (tbTemplate.getSelectedRow() != -1) {
            selectedTemplateId = Integer.parseInt(tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 0).toString());
            TNamaCP.setText(tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 1).toString());
            TKodeICD.setText(tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 2).toString());
            TKodeICD9.setText(tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 3) == null ? "" : tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 3).toString());
            TTargetLOS.setText(tbTemplate.getValueAt(tbTemplate.getSelectedRow(), 4).toString());
            tampilItem();
        }
    }

    private void getDataItem() {
        if (tbItem.getSelectedRow() != -1) {
            TUrutan.setText(tbItem.getValueAt(tbItem.getSelectedRow(), 1).toString());
            CmbKategori.setSelectedItem(tbItem.getValueAt(tbItem.getSelectedRow(), 2).toString());
            CmbSubKategori.setSelectedItem(tbItem.getValueAt(tbItem.getSelectedRow(), 3) == null ? "" : tbItem.getValueAt(tbItem.getSelectedRow(), 3).toString());
            TNamaAktivitas.setText(tbItem.getValueAt(tbItem.getSelectedRow(), 4).toString());
            THariKe.setText(tbItem.getValueAt(tbItem.getSelectedRow(), 5).toString());
            TKeterangan.setText(tbItem.getValueAt(tbItem.getSelectedRow(), 6) == null ? "" : tbItem.getValueAt(tbItem.getSelectedRow(), 6).toString());
        }
    }

    private void simpanTemplate() {
        if (TNamaCP.getText().trim().isEmpty()) { Valid.textKosong(TNamaCP, "Nama CP"); return; }
        if (TKodeICD.getText().trim().isEmpty()) { Valid.textKosong(TKodeICD, "Kode ICD"); return; }
        Sequel.menyimpantf("cp_template",
            "NULL,?,?,?,?,1,?,CURDATE(),NULL,''", "Nama CP", 4, new String[]{
            TNamaCP.getText(), TKodeICD.getText(), TKodeICD9.getText(),
            TTargetLOS.getText().isEmpty() ? "7" : TTargetLOS.getText()
        });
        tampilTemplate();
        TNamaCP.setText(""); TKodeICD.setText(""); TKodeICD9.setText(""); TTargetLOS.setText("");
    }

    private void editTemplate() {
        if (selectedTemplateId <= 0 || tbTemplate.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih template dulu"); return; }
        Sequel.queryu("update cp_template set nama_cp='" + TNamaCP.getText().replace("'","''") +
            "', kode_icd10='" + TKodeICD.getText() +
            "', kode_icd9cm='" + TKodeICD9.getText() +
            "', target_los_hari=" + (TTargetLOS.getText().isEmpty() ? "7" : TTargetLOS.getText()) +
            " where id_template=" + selectedTemplateId);
        tampilTemplate();
    }

    private void hapusTemplate() {
        if (selectedTemplateId <= 0 || tbTemplate.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih template dulu"); return; }
        int ok = JOptionPane.showConfirmDialog(null, "Hapus template '" + TNamaCP.getText() + "' beserta semua item?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            Sequel.queryu("delete from cp_template_item where id_template=" + selectedTemplateId);
            Sequel.queryu("delete from cp_template where id_template=" + selectedTemplateId);
            selectedTemplateId = -1;
            tampilTemplate(); Valid.tabelKosong(tabItem);
            TNamaCP.setText(""); TKodeICD.setText(""); TKodeICD9.setText(""); TTargetLOS.setText("");
        }
    }

    private void simpanItem() {
        if (selectedTemplateId <= 0) { JOptionPane.showMessageDialog(null, "Pilih template dulu di tabel kiri"); return; }
        if (TNamaAktivitas.getText().trim().isEmpty() && CmbKategori.getSelectedItem().toString().trim().isEmpty()) { JOptionPane.showMessageDialog(null, "Isi kategori dan aktivitas"); return; }
        String sub = CmbSubKategori.getSelectedItem().toString();
        Sequel.menyimpantf("cp_template_item",
            "NULL,?,?,?,?,?,?,?", "Item", 7, new String[]{
            String.valueOf(selectedTemplateId),
            TUrutan.getText().isEmpty() ? "0" : TUrutan.getText(),
            CmbKategori.getSelectedItem().toString(),
            sub.isEmpty() ? null : sub,
            TNamaAktivitas.getText(),
            THariKe.getText().isEmpty() ? "1" : THariKe.getText(),
            TKeterangan.getText()
        });
        tampilItem();
        TUrutan.setText(""); TNamaAktivitas.setText(""); THariKe.setText(""); TKeterangan.setText("");
    }

    private void editItem() {
        if (tbItem.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih item dulu"); return; }
        String idItem = tbItem.getValueAt(tbItem.getSelectedRow(), 0).toString();
        String sub = CmbSubKategori.getSelectedItem().toString();
        Sequel.queryu("update cp_template_item set urutan=" + (TUrutan.getText().isEmpty() ? "0" : TUrutan.getText()) +
            ", kategori='" + CmbKategori.getSelectedItem().toString().replace("'","''") +
            "', sub_kategori=" + (sub.isEmpty() ? "NULL" : "'" + sub.replace("'","''") + "'") +
            ", nama_aktivitas='" + TNamaAktivitas.getText().replace("'","''") +
            "', hari_ke='" + (THariKe.getText().isEmpty() ? "1" : THariKe.getText()) +
            "', keterangan='" + TKeterangan.getText().replace("'","''") +
            "' where id_item=" + idItem);
        tampilItem();
    }

    private void hapusItem() {
        if (tbItem.getSelectedRow() == -1) { JOptionPane.showMessageDialog(null, "Pilih item dulu"); return; }
        String idItem = tbItem.getValueAt(tbItem.getSelectedRow(), 0).toString();
        Sequel.queryu("delete from cp_template_item where id_item=" + idItem);
        tampilItem();
        TUrutan.setText(""); TNamaAktivitas.setText(""); THariKe.setText(""); TKeterangan.setText("");
    }
}
