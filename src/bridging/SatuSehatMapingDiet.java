/*
 * Form mapping jenis diet (tabel diet) -> kode SNOMED untuk NutritionOrder SatuSehat.
 * Dibuat dengan gaya yang sama seperti form mapping lain (Mapping KPTL):
 * internalFrame + panel input toggle (.: Input Data) + toolbar Simpan/Baru/Hapus.
 * Picker SNOMED via simrskhanza.DlgCariSnomed. Hasil: satu_sehat_mapping_diet.
 */
package bridging;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public final class SatuSehatMapingDiet extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private validasi Valid=new validasi();
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i=0;

    public SatuSehatMapingDiet(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);
        setSize(900,600);

        tabMode=new DefaultTableModel(null,new Object[]{
                "Kd.Diet","Nama Diet (lokal)","SNOMED Code","SNOMED Display"
            }){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] w={80,360,130,330};
        for (i=0; i<w.length; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            column.setPreferredWidth(w[i]);
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
        FormInput.setVisible(false);

        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override public void insertUpdate(DocumentEvent e) { if(TCari.getText().length()>2) tampil(); }
                @Override public void removeUpdate(DocumentEvent e) { if(TCari.getText().length()>2) tampil(); }
                @Override public void changedUpdate(DocumentEvent e) { if(TCari.getText().length()>2) tampil(); }
            });
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBaru = new widget.Button();
        BtnHapus = new widget.Button();
        BtnAll = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        PanelInput = new javax.swing.JPanel();
        ChkInput = new widget.CekBox();
        FormInput = new widget.PanelBiasa();
        jLabel1 = new widget.Label();
        TKdDiet = new widget.TextBox();
        jLabel3 = new widget.Label();
        TNama = new widget.TextBox();
        jLabel4 = new widget.Label();
        TCode = new widget.TextBox();
        BtnPilih = new widget.Button();
        jLabel5 = new widget.Label();
        TDisplay = new widget.TextBox();
        BtnPilihDiet = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Mapping Diet ke SNOMED (NutritionOrder Satu Sehat) ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbObat.setAutoCreateRowSorter(true);
        tbObat.setName("tbObat"); // NOI18N
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        Scroll.setViewportView(tbObat);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setPreferredSize(new java.awt.Dimension(100, 80));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 23));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnSimpan);

        BtnBaru.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); // NOI18N
        BtnBaru.setMnemonic('B');
        BtnBaru.setText("Baru");
        BtnBaru.setToolTipText("Alt+B");
        BtnBaru.setName("BtnBaru"); // NOI18N
        BtnBaru.setPreferredSize(new java.awt.Dimension(100, 23));
        BtnBaru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBaruActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnBaru);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus"); // NOI18N
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 23));
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnHapus);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setText("Semua");
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(100, 23));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnAll);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 23));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnKeluar);

        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel6.setText("Cari Diet :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(280, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

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
        panelGlass9.add(BtnCari);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(55, 23));
        panelGlass9.add(jLabel7);

        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(LCount);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(660, 160));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setMnemonic('I');
        ChkInput.setText(".: Input Data");
        ChkInput.setToolTipText("Alt+I");
        ChkInput.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput.setName("ChkInput"); // NOI18N
        ChkInput.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputActionPerformed(evt);
            }
        });
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 135));
        FormInput.setLayout(null);

        jLabel1.setText("Kd.Diet :");
        jLabel1.setName("jLabel1"); // NOI18N
        FormInput.add(jLabel1);
        jLabel1.setBounds(0, 10, 95, 23);

        TKdDiet.setEditable(false);
        TKdDiet.setName("TKdDiet"); // NOI18N
        FormInput.add(TKdDiet);
        TKdDiet.setBounds(99, 10, 120, 23);

        BtnPilihDiet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/attachment.png"))); // NOI18N
        BtnPilihDiet.setToolTipText("Cari/pilih diet dari master (DlgCariDiet)");
        BtnPilihDiet.setName("BtnPilihDiet"); // NOI18N
        BtnPilihDiet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPilihDietActionPerformed(evt);
            }
        });
        FormInput.add(BtnPilihDiet);
        BtnPilihDiet.setBounds(222, 10, 28, 23);

        jLabel3.setText("Nama Diet :");
        jLabel3.setName("jLabel3"); // NOI18N
        FormInput.add(jLabel3);
        jLabel3.setBounds(0, 40, 95, 23);

        TNama.setEditable(false);
        TNama.setName("TNama"); // NOI18N
        FormInput.add(TNama);
        TNama.setBounds(99, 40, 625, 23);

        jLabel4.setText("SNOMED Code :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 70, 95, 23);

        TCode.setEditable(false);
        TCode.setName("TCode"); // NOI18N
        FormInput.add(TCode);
        TCode.setBounds(99, 70, 120, 23);

        BtnPilih.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnPilih.setMnemonic('2');
        BtnPilih.setText("Cari SNOMED");
        BtnPilih.setToolTipText("Alt+2");
        BtnPilih.setName("BtnPilih"); // NOI18N
        BtnPilih.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPilihActionPerformed(evt);
            }
        });
        FormInput.add(BtnPilih);
        BtnPilih.setBounds(222, 70, 120, 23);

        jLabel5.setText("SNOMED Display :");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(0, 100, 95, 23);

        TDisplay.setEditable(false);
        TDisplay.setName("TDisplay"); // NOI18N
        FormInput.add(TDisplay);
        TDisplay.setBounds(99, 100, 625, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {
        tampil();
    }

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {
        int r = tbObat.getSelectedRow();
        if(r<0) return;
        TKdDiet.setText(tabMode.getValueAt(r,0).toString());
        TNama.setText(tabMode.getValueAt(r,1).toString());
        TCode.setText(tabMode.getValueAt(r,2).toString());
        TDisplay.setText(tabMode.getValueAt(r,3).toString());
        if(!FormInput.isVisible()){
            ChkInput.setSelected(true);
            FormInput.setVisible(true);
        }
    }

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {
        FormInput.setVisible(ChkInput.isSelected());
    }

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {
        tampil();
    }

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {
        TCari.setText("");
        tampil();
    }

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) tampil();
    }

    private void BtnBaruActionPerformed(java.awt.event.ActionEvent evt) {
        TKdDiet.setText("");
        TNama.setText("");
        TCode.setText("");
        TDisplay.setText("");
    }

    /** Pilih kode SNOMED via DlgCariSnomed (kolom: 0=system,1=code,2=display). */
    private void BtnPilihActionPerformed(java.awt.event.ActionEvent evt) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        simrskhanza.DlgCariSnomed dlg = new simrskhanza.DlgCariSnomed(null, true);
        dlg.setSize(internalFrame1.getWidth()-30, 320);
        dlg.setLocationRelativeTo(internalFrame1);
        dlg.setVisible(true);
        try {
            JTable t = dlg.getTable();
            int r = t.getSelectedRow();
            if(r>=0){
                Object c1=t.getValueAt(r,1), c2=t.getValueAt(r,2);
                TCode.setText(c1==null?"":c1.toString());
                TDisplay.setText(c2==null?"":c2.toString());
            }
        } catch(Exception e){ System.out.println("Notif cari SNOMED : "+e); }
        this.setCursor(Cursor.getDefaultCursor());
    }

    /** Pilih diet dari master via DlgCariDiet (kolom 0=kd_diet, 1=nama_diet).
     *  Isi TKdDiet+TNama, lalu muat mapping SNOMED yang sudah ada (jika ada). */
    private void BtnPilihDietActionPerformed(java.awt.event.ActionEvent evt) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        simrskhanza.DlgCariDiet dlg = new simrskhanza.DlgCariDiet(null, true);
        dlg.setSize(internalFrame1.getWidth()-30, 320);
        dlg.setLocationRelativeTo(internalFrame1);
        dlg.setVisible(true);
        try {
            JTable t = dlg.getTable();
            int r = t.getSelectedRow();
            if(r>=0){
                String kd = t.getValueAt(r,0)==null?"":t.getValueAt(r,0).toString();
                String nm = t.getValueAt(r,1)==null?"":t.getValueAt(r,1).toString();
                TKdDiet.setText(kd);
                TNama.setText(nm);
                TCode.setText(""); TDisplay.setText("");
                PreparedStatement p = koneksi.prepareStatement(
                    "select snomed_code, snomed_display from satu_sehat_mapping_diet where kd_diet=?");
                p.setString(1, kd);
                ResultSet rr = p.executeQuery();
                if(rr.next()){
                    TCode.setText(rr.getString(1)==null?"":rr.getString(1));
                    TDisplay.setText(rr.getString(2)==null?"":rr.getString(2));
                }
                rr.close(); p.close();
                if(!FormInput.isVisible()){ ChkInput.setSelected(true); FormInput.setVisible(true); }
            }
        } catch(Exception e){ System.out.println("Notif cari diet : "+e); }
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
        String kd = TKdDiet.getText().trim();
        String code = TCode.getText().trim();
        String disp = TDisplay.getText().trim();
        if(kd.equals("")){ Valid.textKosong(TKdDiet,"Kode Diet (pilih baris tabel)"); return; }
        if(code.equals("")){ Valid.textKosong(TCode,"SNOMED Code (tombol Cari SNOMED)"); return; }
        try {
            ps = koneksi.prepareStatement("replace into satu_sehat_mapping_diet (kd_diet,snomed_code,snomed_display) values (?,?,?)");
            ps.setString(1,kd);
            ps.setString(2,code);
            ps.setString(3,disp);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Gagal simpan : "+e);
            return;
        }
        tampil();
    }

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {
        String kd = TKdDiet.getText().trim();
        if(kd.equals("")){ Valid.textKosong(TKdDiet,"Kode Diet (pilih baris tabel)"); return; }
        try {
            ps = koneksi.prepareStatement("delete from satu_sehat_mapping_diet where kd_diet=?");
            ps.setString(1,kd);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Gagal hapus : "+e);
            return;
        }
        TCode.setText("");
        TDisplay.setText("");
        tampil();
    }

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    public void tampil() {
        try {
            while(tabMode.getRowCount()>0) tabMode.removeRow(0);
            String c = "%"+TCari.getText().trim()+"%";
            String sql =
                "select d.kd_diet, d.nama_diet, "+
                "ifnull(m.snomed_code,'') snomed_code, ifnull(m.snomed_display,'') snomed_display "+
                "from diet d "+
                "left join satu_sehat_mapping_diet m on m.kd_diet=d.kd_diet "+
                "where (d.kd_diet like ? or d.nama_diet like ? or ifnull(m.snomed_code,'') like ? or ifnull(m.snomed_display,'') like ?) "+
                "order by d.kd_diet";
            ps = koneksi.prepareStatement(sql);
            ps.setString(1,c); ps.setString(2,c); ps.setString(3,c); ps.setString(4,c);
            rs = ps.executeQuery();
            while(rs.next()){
                tabMode.addRow(new Object[]{
                    rs.getString("kd_diet"), rs.getString("nama_diet"),
                    rs.getString("snomed_code"), rs.getString("snomed_display")
                });
            }
        } catch (Exception e) {
            System.out.println("Notif tampil mapping diet : "+e);
        } finally {
            try { if(rs!=null) rs.close(); if(ps!=null) ps.close(); } catch(Exception x){}
        }
        LCount.setText(""+tabMode.getRowCount());
    }

    public void isCek() {
        boolean bisa = akses.getsatu_sehat_kirim_diet();
        BtnSimpan.setEnabled(bisa);
        BtnHapus.setEnabled(bisa);
    }

    public JTable getTable(){ return tbObat; }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            SatuSehatMapingDiet d = new SatuSehatMapingDiet(new javax.swing.JFrame(), true);
            d.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosing(java.awt.event.WindowEvent e) { System.exit(0); }
            });
            d.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnAll;
    private widget.Button BtnBaru;
    private widget.Button BtnCari;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPilih;
    private widget.Button BtnPilihDiet;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.PanelBiasa FormInput;
    private widget.Label LCount;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TCode;
    private widget.TextBox TDisplay;
    private widget.TextBox TKdDiet;
    private widget.TextBox TNama;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel1;
    private widget.Label jLabel3;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private javax.swing.JPanel jPanel3;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables
}
