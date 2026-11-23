/*
 * Dialog pencarian referensi ICD-O-3 (topografi / morfologi) untuk modul
 * Registrasi Kanker SatuSehat. Hand-built (tanpa NetBeans .form).
 *
 * Mode:
 *   MODE_TOPOGRAFI -> tabel ref_icdo_topografi (kolom: Kode, Nama)
 *   MODE_MORFOLOGI -> tabel ref_icdo_morfologi (kolom: Kode, Nama, Behavior)
 *
 * Pola pemakaian (lihat DlgRegistrasiKanker):
 *   DlgCariICDO dlg = new DlgCariICDO(null, true, DlgCariICDO.MODE_MORFOLOGI);
 *   dlg.addWindowListener(... windowClosed -> baca getTable().getSelectedRow() ...);
 *   dlg.isCek(); dlg.setVisible(true);
 *
 * Kolom tabel (selalu 3, konsisten untuk caller): 0=Kode, 1=Nama, 2=Behavior.
 */
package bridging;

import fungsi.WarnaTable;
import fungsi.koneksiDB;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public final class DlgCariICDO extends javax.swing.JDialog {

    public static final int MODE_TOPOGRAFI = 0;
    public static final int MODE_MORFOLOGI = 1;

    private final validasi Valid = new validasi();
    private final Connection koneksi = koneksiDB.condb();
    private final int mode;

    private widget.InternalFrame internalFrame1;
    private widget.TextBox TCari;
    private widget.Button BtnCari, BtnKeluar;
    private widget.ComboBox cmbHlm;
    private widget.Label LCount;
    private widget.Table tbData;
    private DefaultTableModel tabMode;

    public DlgCariICDO(java.awt.Frame parent, boolean modal, int mode) {
        super(parent, modal);
        this.mode = mode;
        setTitle(mode == MODE_TOPOGRAFI
                ? ":: Cari Topografi ICD-O ::"
                : ":: Cari Morfologi ICD-O ::");
        buildUI();
        tampil();
        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new DocumentListener() {
                @Override public void insertUpdate(DocumentEvent e) { if (TCari.getText().length() > 1) tampil(); }
                @Override public void removeUpdate(DocumentEvent e) { if (TCari.getText().length() > 1) tampil(); }
                @Override public void changedUpdate(DocumentEvent e) { if (TCari.getText().length() > 1) tampil(); }
            });
        }
    }

    private void buildUI() {
        setSize(560, 460);
        internalFrame1 = new widget.InternalFrame();
        internalFrame1.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(240, 245, 235)),
                mode == MODE_TOPOGRAFI ? "::[ Referensi Topografi ICD-O ]::" : "::[ Referensi Morfologi ICD-O ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Tahoma", 0, 11), new Color(50, 50, 50)));
        internalFrame1.setLayout(new BorderLayout(1, 1));

        // Bar cari
        widget.panelisi bar = new widget.panelisi();
        bar.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 8));
        bar.setPreferredSize(new Dimension(100, 40));
        widget.Label l1 = new widget.Label(); l1.setText("Key Word :"); l1.setPreferredSize(new Dimension(60, 23));
        bar.add(l1);
        TCari = new widget.TextBox(); TCari.setPreferredSize(new Dimension(200, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override public void keyPressed(java.awt.event.KeyEvent e) { tampil(); }
        });
        bar.add(TCari);
        BtnCari = new widget.Button(); BtnCari.setText("Cari"); BtnCari.setPreferredSize(new Dimension(70, 23));
        BtnCari.addActionListener(e -> tampil());
        bar.add(BtnCari);
        widget.Label l2 = new widget.Label(); l2.setText("Record :"); l2.setPreferredSize(new Dimension(55, 23));
        bar.add(l2);
        LCount = new widget.Label(); LCount.setText("0"); LCount.setPreferredSize(new Dimension(50, 23));
        bar.add(LCount);
        widget.Label l3 = new widget.Label(); l3.setText("Limit :"); l3.setPreferredSize(new Dimension(40, 23));
        bar.add(l3);
        cmbHlm = new widget.ComboBox();
        cmbHlm.setModel(new DefaultComboBoxModel<>(new String[]{"100", "200", "500", "Semua"}));
        cmbHlm.setPreferredSize(new Dimension(80, 23));
        cmbHlm.addActionListener(e -> tampil());
        bar.add(cmbHlm);
        BtnKeluar = new widget.Button(); BtnKeluar.setText("Keluar"); BtnKeluar.setPreferredSize(new Dimension(80, 23));
        BtnKeluar.addActionListener(e -> dispose());
        bar.add(BtnKeluar);
        internalFrame1.add(bar, BorderLayout.PAGE_END);

        // Tabel
        tabMode = new DefaultTableModel(null, new String[]{"Kode", "Nama", "Behavior"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tbData = new widget.Table();
        tbData.setModel(tabMode);
        tbData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tbData.getColumnModel().getColumn(0).setPreferredWidth(90);
        tbData.getColumnModel().getColumn(1).setPreferredWidth(360);
        tbData.getColumnModel().getColumn(2).setPreferredWidth(70);
        tbData.setDefaultRenderer(Object.class, new WarnaTable());
        tbData.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && tbData.getSelectedRow() != -1) dispose();
            }
        });
        widget.ScrollPane scroll = new widget.ScrollPane();
        scroll.setViewportView(tbData);
        internalFrame1.add(scroll, BorderLayout.CENTER);

        getContentPane().add(internalFrame1, BorderLayout.CENTER);
    }

    private void tampil() {
        Valid.tabelKosong(tabMode);
        String limitStr = cmbHlm.getSelectedItem().toString();
        String limitClause = "Semua".equalsIgnoreCase(limitStr) ? "" : " LIMIT " + limitStr;
        String kw = TCari.getText().trim();
        String sql;
        if (mode == MODE_TOPOGRAFI) {
            sql = kw.isEmpty()
                ? "select kode, nama, '' as behavior from ref_icdo_topografi order by kode asc" + limitClause
                : "select kode, nama, '' as behavior from ref_icdo_topografi where kode like ? or nama like ? order by kode asc" + limitClause;
        } else {
            sql = kw.isEmpty()
                ? "select kode, nama, ifnull(behavior,'') as behavior from ref_icdo_morfologi order by kode asc" + limitClause
                : "select kode, nama, ifnull(behavior,'') as behavior from ref_icdo_morfologi where kode like ? or nama like ? order by kode asc" + limitClause;
        }
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            if (!kw.isEmpty()) {
                ps.setString(1, "%" + kw + "%");
                ps.setString(2, "%" + kw + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tabMode.addRow(new Object[]{
                        rs.getString("kode"), rs.getString("nama"), rs.getString("behavior")
                    });
                }
            }
            LCount.setText("" + tabMode.getRowCount());
        } catch (Exception e) {
            System.out.println("Notif tampil ICD-O : " + e);
        }
    }

    /** Konsisten dengan dialog cari lain di Khanza. */
    public JTable getTable() { return tbData; }

    public void isCek() { TCari.requestFocus(); }
}
