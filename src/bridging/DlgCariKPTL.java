/*
 * Dialog cari / master KPTL (Kode Pembiayaan Tindakan dan Layanan Kesehatan
 * Nasional). Menampilkan isi satu_sehat_kptl_master. Dipakai sebagai picker
 * pada form Mapping KPTL dan bisa dibuka sendiri sebagai master viewer.
 */
package bridging;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.validasi;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public final class DlgCariKPTL extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private validasi Valid=new validasi();
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private int z=0;
    /** kode & display terpilih saat dialog ditutup via double click / space. */
    private String kodePilih="";
    private String displayPilih="";
    private boolean sukses=false;

    public DlgCariKPTL(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(10,2);
        setSize(720,260);

        Object[] row={"Kode KPTL","Display","Base Code","Nama Tindakan","Status"};
        tabMode=new DefaultTableModel(null,row){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbKamar.setModel(tabMode);
        tbKamar.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbKamar.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] w={120,300,170,300,60};
        for (z=0; z<w.length; z++) {
            TableColumn column = tbKamar.getColumnModel().getColumn(z);
            column.setPreferredWidth(w[z]);
        }
        tbKamar.setDefaultRenderer(Object.class, new WarnaTable());
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbKamar = new widget.Table();
        panelisi3 = new widget.panelisi();
        label9 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        labelTampil = new widget.Label();
        Cmbtampil = new widget.ComboBox();
        label10 = new widget.Label();
        LCount = new widget.Label();
        BtnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Master KPTL - Kode Pembiayaan Tindakan & Layanan ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbKamar.setAutoCreateRowSorter(true);
        tbKamar.setName("tbKamar"); // NOI18N
        tbKamar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbKamarMouseClicked(evt);
            }
        });
        tbKamar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbKamarKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbKamar);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        panelisi3.setName("panelisi3"); // NOI18N
        panelisi3.setPreferredSize(new java.awt.Dimension(100, 43));
        panelisi3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 9));

        label9.setText("Key Word :");
        label9.setName("label9"); // NOI18N
        label9.setPreferredSize(new java.awt.Dimension(68, 23));
        panelisi3.add(label9);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(360, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelisi3.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('1');
        BtnCari.setToolTipText("Alt+1");
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        BtnCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariKeyPressed(evt);
            }
        });
        panelisi3.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('2');
        BtnAll.setToolTipText("Alt+2");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllActionPerformed(evt);
            }
        });
        panelisi3.add(BtnAll);

        labelTampil.setText("Tampil :");
        labelTampil.setName("labelTampil"); // NOI18N
        labelTampil.setPreferredSize(new java.awt.Dimension(48, 23));
        panelisi3.add(labelTampil);

        Cmbtampil.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "100", "300", "500", "1000", "Semua" }));
        Cmbtampil.setName("Cmbtampil"); // NOI18N
        Cmbtampil.setPreferredSize(new java.awt.Dimension(90, 23));
        Cmbtampil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmbtampilActionPerformed(evt);
            }
        });
        panelisi3.add(Cmbtampil);

        label10.setText("Record :");
        label10.setName("label10"); // NOI18N
        label10.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(label10);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(LCount);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('3');
        BtnKeluar.setToolTipText("Alt+3");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        panelisi3.add(BtnKeluar);

        internalFrame1.add(panelisi3, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tbKamarMouseClicked(java.awt.event.MouseEvent evt) {
        if(tabMode.getRowCount()!=0 && evt.getClickCount()==2){
            pilihBaris();
        }
    }

    private void tbKamarKeyPressed(java.awt.event.KeyEvent evt) {
        if(tabMode.getRowCount()!=0){
            if(evt.getKeyCode()==KeyEvent.VK_SPACE){
                pilihBaris();
            }else if(evt.getKeyCode()==KeyEvent.VK_SHIFT){
                TCari.setText("");
                TCari.requestFocus();
            }
        }
    }

    private void pilihBaris(){
        int r = tbKamar.getSelectedRow();
        if(r<0) return;
        kodePilih = tabMode.getValueAt(r,0).toString();
        displayPilih = tabMode.getValueAt(r,1).toString();
        sukses = true;
        dispose();
    }

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    private void formWindowOpened(java.awt.event.WindowEvent evt) {
        tampil();
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override public void insertUpdate(DocumentEvent e) { if(TCari.getText().length()>2) tampil(); }
                @Override public void removeUpdate(DocumentEvent e) { if(TCari.getText().length()>2) tampil(); }
                @Override public void changedUpdate(DocumentEvent e) { if(TCari.getText().length()>2) tampil(); }
            });
        }
    }

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {
        tampil();
    }

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt, TCari, BtnAll);
        }
    }

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {
        TCari.setText("");
        tampil();
    }

    private void CmbtampilActionPerformed(java.awt.event.ActionEvent evt) {
        tampil();
    }

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            tampil();
        }
    }

    /** Limit baris sesuai pilihan combobox; default 100 supaya ringan. */
    private int limitTampil(){
        String v = Cmbtampil==null?"100":String.valueOf(Cmbtampil.getSelectedItem());
        if(v.equalsIgnoreCase("Semua")) return 20000;
        try { return Integer.parseInt(v.trim()); } catch(Exception e){ return 100; }
    }

    public void tampil() {
        try {
            while(tabMode.getRowCount()>0) tabMode.removeRow(0);
            String c = "%"+TCari.getText().trim()+"%";
            String sql =
                "select kode_kptl, display, base_code, nama_tindakan, status " +
                "from satu_sehat_kptl_master " +
                "where (kode_kptl like ? or display like ? or nama_tindakan like ? or base_code like ?) " +
                "order by display limit "+limitTampil();
            ps = koneksi.prepareStatement(sql);
            ps.setString(1,c); ps.setString(2,c); ps.setString(3,c); ps.setString(4,c);
            rs = ps.executeQuery();
            while(rs.next()){
                tabMode.addRow(new Object[]{
                    rs.getString("kode_kptl"), rs.getString("display"),
                    rs.getString("base_code"), rs.getString("nama_tindakan"), rs.getString("status")
                });
            }
        } catch (Exception e) {
            System.out.println("Notif cari KPTL : "+e);
        } finally {
            try { if(rs!=null) rs.close(); if(ps!=null) ps.close(); } catch(Exception x){}
        }
        LCount.setText(""+tabMode.getRowCount());
    }

    public boolean isSukses(){ return sukses; }
    public String getKodePilih(){ return kodePilih; }
    public String getDisplayPilih(){ return displayPilih; }
    public JTable getTable(){ return tbKamar; }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnAll;
    private widget.Button BtnCari;
    private widget.Button BtnKeluar;
    private widget.ComboBox Cmbtampil;
    private widget.Label labelTampil;
    private widget.Label LCount;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.InternalFrame internalFrame1;
    private widget.Label label10;
    private widget.Label label9;
    private widget.panelisi panelisi3;
    private widget.Table tbKamar;
    // End of variables declaration//GEN-END:variables
}
