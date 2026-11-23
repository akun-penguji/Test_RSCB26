package inventory;

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

public class DlgMasterInteraksiObat extends JDialog {

    private widget.Table tbData;
    private DefaultTableModel tabMode;
    private TextBox TKomposisi1, TKomposisi2, TDeskripsi, TRekomendasi, TMekanisme, TOnset, TCari;
    private ComboBox CmbTingkat, CmbStatus;
    private widget.Label LCount;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;

    public DlgMasterInteraksiObat(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setSize(1000, 600);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        InternalFrame internalFrame1 = new InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(240, 245, 235)),
            "::[ Master Interaksi Obat ]::",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // Panel input
        JPanel panelInput = new JPanel(new BorderLayout(1, 1));
        panelInput.setOpaque(false);
        panelInput.setPreferredSize(new Dimension(192, 145));

        CekBox chkInput = new CekBox();
        chkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png")));
        chkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png")));
        chkInput.setText(".: Input Data");
        chkInput.setSelected(true);
        chkInput.setBorderPainted(true); chkInput.setBorderPaintedFlat(true); chkInput.setFocusable(false);
        chkInput.setPreferredSize(new Dimension(192, 20));
        ScrollPane scrollInput = new ScrollPane();
        panelisi panelGlass = new panelisi();
        panelGlass.setPreferredSize(new Dimension(44, 120));
        panelGlass.setLayout(null);

        int h = 23, y = 5;
        widget.Label lbl1 = new widget.Label(); lbl1.setText("Komposisi/Obat 1 :"); lbl1.setBounds(0, y, 105, h); panelGlass.add(lbl1);
        TKomposisi1 = new TextBox(); TKomposisi1.setBounds(108, y, 200, h); panelGlass.add(TKomposisi1);
        widget.Label lbl2 = new widget.Label(); lbl2.setText("Komposisi/Obat 2 :"); lbl2.setBounds(315, y, 105, h); panelGlass.add(lbl2);
        TKomposisi2 = new TextBox(); TKomposisi2.setBounds(423, y, 200, h); panelGlass.add(TKomposisi2);
        widget.Label lbl3 = new widget.Label(); lbl3.setText("Tingkat :"); lbl3.setBounds(630, y, 48, h); panelGlass.add(lbl3);
        CmbTingkat = new ComboBox(); CmbTingkat.setModel(new DefaultComboBoxModel(new String[]{"RINGAN","SEDANG","BERAT","KONTRAINDIKASI"}));
        CmbTingkat.setBounds(680, y, 120, h); panelGlass.add(CmbTingkat);

        y += 28;
        widget.Label lbl4 = new widget.Label(); lbl4.setText("Deskripsi :"); lbl4.setBounds(0, y, 60, h); panelGlass.add(lbl4);
        TDeskripsi = new TextBox(); TDeskripsi.setBounds(108, y, 500, h); panelGlass.add(TDeskripsi);
        widget.Label lbl5 = new widget.Label(); lbl5.setText("Onset :"); lbl5.setBounds(615, y, 38, h); panelGlass.add(lbl5);
        TOnset = new TextBox(); TOnset.setBounds(655, y, 80, h); panelGlass.add(TOnset);
        widget.Label lbl8 = new widget.Label(); lbl8.setText("Status :"); lbl8.setBounds(740, y, 38, h); panelGlass.add(lbl8);
        CmbStatus = new ComboBox(); CmbStatus.setModel(new DefaultComboBoxModel(new String[]{"AKTIF","NONAKTIF"}));
        CmbStatus.setBounds(780, y, 85, h); panelGlass.add(CmbStatus);

        y += 28;
        widget.Label lbl6 = new widget.Label(); lbl6.setText("Rekomendasi :"); lbl6.setBounds(0, y, 80, h); panelGlass.add(lbl6);
        TRekomendasi = new TextBox(); TRekomendasi.setBounds(108, y, 350, h); panelGlass.add(TRekomendasi);
        widget.Label lbl7 = new widget.Label(); lbl7.setText("Mekanisme :"); lbl7.setBounds(465, y, 70, h); panelGlass.add(lbl7);
        TMekanisme = new TextBox(); TMekanisme.setBounds(538, y, 327, h); panelGlass.add(TMekanisme);

        scrollInput.setViewportView(panelGlass);
        panelInput.add(scrollInput, BorderLayout.CENTER);
        chkInput.addActionListener(ev -> {
            if (chkInput.isSelected()) {
                chkInput.setVisible(false); panelInput.setPreferredSize(new Dimension(192, 145));
                scrollInput.setVisible(true); chkInput.setVisible(true);
            } else {
                chkInput.setVisible(false); panelInput.setPreferredSize(new Dimension(192, 20));
                scrollInput.setVisible(false); chkInput.setVisible(true);
            }
        });
        panelInput.add(chkInput, BorderLayout.PAGE_END);

        // Tabel
        ScrollPane scroll = new ScrollPane();
        tbData = new widget.Table();
        tabMode = new DefaultTableModel(null,
            new String[]{"No", "Komposisi/Obat 1", "Komposisi/Obat 2", "Tingkat", "Deskripsi Interaksi", "Rekomendasi", "Mekanisme", "Onset", "Status"}
        ) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tbData.setModel(tabMode);
        tbData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] cw = {0, 130, 130, 95, 200, 150, 150, 50, 65};
        for (int i = 0; i < cw.length; i++) {
            TableColumn col = tbData.getColumnModel().getColumn(i);
            col.setPreferredWidth(cw[i]);
            if (i == 0) { col.setMinWidth(0); col.setMaxWidth(0); col.setWidth(0); }
        }
        tbData.setDefaultRenderer(Object.class, new WarnaTable() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                java.awt.Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                if (!s && r < t.getRowCount()) {
                    String tingkat = t.getValueAt(r, 3).toString();
                    if ("KONTRAINDIKASI".equals(tingkat)) comp.setBackground(new Color(255, 180, 180));
                    else if ("BERAT".equals(tingkat)) comp.setBackground(new Color(255, 220, 180));
                    else if ("SEDANG".equals(tingkat)) comp.setBackground(new Color(255, 255, 200));
                    else comp.setBackground(new Color(220, 255, 220));
                }
                return comp;
            }
        });
        tbData.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { getData(); }
        });
        scroll.setViewportView(tbData);

        // Panel bawah
        panelisi panelBawah = new panelisi();
        panelBawah.setPreferredSize(new Dimension(44, 44));
        panelBawah.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 7));

        Button BtnSimpan = new Button(); BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png")));
        BtnSimpan.setText("Simpan"); BtnSimpan.setPreferredSize(new Dimension(100, 30));
        BtnSimpan.addActionListener(ev -> simpan());
        Button BtnHapus = new Button(); BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setText("Hapus"); BtnHapus.setPreferredSize(new Dimension(80, 30));
        BtnHapus.addActionListener(ev -> hapus());
        Button BtnEdit = new Button(); BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/1360485642_edit-notes.png")));
        BtnEdit.setText("Ganti"); BtnEdit.setPreferredSize(new Dimension(80, 30));
        BtnEdit.addActionListener(ev -> ganti());
        Button BtnBaru = new Button(); BtnBaru.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png")));
        BtnBaru.setText("Baru"); BtnBaru.setPreferredSize(new Dimension(80, 30));
        BtnBaru.addActionListener(ev -> emptTeks());
        Button BtnKeluar = new Button(); BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setText("Keluar"); BtnKeluar.setPreferredSize(new Dimension(80, 30));
        BtnKeluar.addActionListener(ev -> dispose());

        widget.Label lblCari = new widget.Label(); lblCari.setText("Cari :"); lblCari.setPreferredSize(new Dimension(28, 23));
        TCari = new TextBox(); TCari.setPreferredSize(new Dimension(150, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_ENTER) tampil(); }
        });
        Button BtnCari = new Button(); BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setPreferredSize(new Dimension(28, 23));
        BtnCari.addActionListener(ev -> tampil());
        LCount = new widget.Label(); LCount.setText("Record : 0"); LCount.setPreferredSize(new Dimension(100, 23));

        panelBawah.add(BtnSimpan); panelBawah.add(BtnHapus); panelBawah.add(BtnEdit); panelBawah.add(BtnBaru); panelBawah.add(BtnKeluar);
        panelBawah.add(lblCari); panelBawah.add(TCari); panelBawah.add(BtnCari); panelBawah.add(LCount);

        internalFrame1.add(panelInput, BorderLayout.PAGE_START);
        internalFrame1.add(scroll, BorderLayout.CENTER);
        internalFrame1.add(panelBawah, BorderLayout.PAGE_END);
        setLayout(new BorderLayout()); add(internalFrame1, BorderLayout.CENTER);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); setUndecorated(true);
    }

    private void simpan() {
        if (TKomposisi1.getText().trim().isEmpty() || TKomposisi2.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Komposisi 1 dan 2 harus diisi"); return;
        }
        Sequel.menyimpantf("interaksi_obat",
            "NULL,?,?,?,?,?,?,?,?,NOW()", "Data", 8, new String[]{
            TKomposisi1.getText(), TKomposisi2.getText(),
            CmbTingkat.getSelectedItem().toString(), TDeskripsi.getText(),
            TRekomendasi.getText(), TMekanisme.getText(), TOnset.getText(),
            CmbStatus.getSelectedItem().toString()
        });
        emptTeks(); tampil();
    }

    private void ganti() {
        if (tbData.getSelectedRow() == -1) return;
        String id = tabMode.getValueAt(tbData.getSelectedRow(), 0).toString();
        Sequel.mengedittf("interaksi_obat",
            "komposisi1=?,komposisi2=?,tingkat_interaksi=?,deskripsi_interaksi=?,rekomendasi=?,mekanisme=?,onset=?,status=?",
            "id=?", 9, new String[]{
            TKomposisi1.getText(), TKomposisi2.getText(),
            CmbTingkat.getSelectedItem().toString(), TDeskripsi.getText(),
            TRekomendasi.getText(), TMekanisme.getText(), TOnset.getText(),
            CmbStatus.getSelectedItem().toString(), id
        });
        emptTeks(); tampil();
    }

    private void hapus() {
        if (tbData.getSelectedRow() == -1) return;
        int ok = JOptionPane.showConfirmDialog(null, "Hapus data interaksi ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            String id = tabMode.getValueAt(tbData.getSelectedRow(), 0).toString();
            Sequel.queryu("delete from interaksi_obat where id=" + id);
            emptTeks(); tampil();
        }
    }

    private void getData() {
        if (tbData.getSelectedRow() == -1) return;
        int r = tbData.getSelectedRow();
        TKomposisi1.setText(tabMode.getValueAt(r, 1).toString());
        TKomposisi2.setText(tabMode.getValueAt(r, 2).toString());
        String tingkat = tabMode.getValueAt(r, 3).toString();
        for (int i = 0; i < CmbTingkat.getItemCount(); i++) {
            if (CmbTingkat.getItemAt(i).toString().equals(tingkat)) { CmbTingkat.setSelectedIndex(i); break; }
        }
        TDeskripsi.setText(tabMode.getValueAt(r, 4) == null ? "" : tabMode.getValueAt(r, 4).toString());
        TRekomendasi.setText(tabMode.getValueAt(r, 5) == null ? "" : tabMode.getValueAt(r, 5).toString());
        TMekanisme.setText(tabMode.getValueAt(r, 6) == null ? "" : tabMode.getValueAt(r, 6).toString());
        TOnset.setText(tabMode.getValueAt(r, 7) == null ? "" : tabMode.getValueAt(r, 7).toString());
        String status = tabMode.getValueAt(r, 8).toString();
        for (int i = 0; i < CmbStatus.getItemCount(); i++) {
            if (CmbStatus.getItemAt(i).toString().equals(status)) { CmbStatus.setSelectedIndex(i); break; }
        }
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                "select id, komposisi1, komposisi2, tingkat_interaksi, deskripsi_interaksi, rekomendasi, mekanisme, onset, status " +
                "from interaksi_obat where komposisi1 like ? or komposisi2 like ? or deskripsi_interaksi like ? order by tingkat_interaksi desc, komposisi1");
            ps.setString(1, "%" + TCari.getText().trim() + "%");
            ps.setString(2, "%" + TCari.getText().trim() + "%");
            ps.setString(3, "%" + TCari.getText().trim() + "%");
            rs = ps.executeQuery();
            while (rs.next()) {
                tabMode.addRow(new String[]{
                    rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9)
                });
            }
            LCount.setText("Record : " + tabMode.getRowCount());
        } catch (Exception e) { System.out.println("Notif tampil interaksi: " + e);
        } finally { try { if (rs != null) rs.close(); if (ps != null) ps.close(); } catch (Exception e) {} }
    }

    public void emptTeks() {
        TKomposisi1.setText(""); TKomposisi2.setText(""); TDeskripsi.setText("");
        TRekomendasi.setText(""); TMekanisme.setText(""); TOnset.setText("");
    }

    public void isCek() {}
}
