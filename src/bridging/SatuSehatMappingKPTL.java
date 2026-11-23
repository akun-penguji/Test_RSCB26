/*
 * Form mapping jenis perawatan (jns_perawatan = ralan, jns_perawatan_inap =
 * ranap) -> kode KPTL untuk pengiriman ChargeItem SatuSehat.
 * Master KPTL: satu_sehat_kptl_master (picker DlgCariKPTL).
 * Hasil mapping: satu_sehat_mapping_kptl (PK kd_jenis_prw).
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

public final class SatuSehatMappingKPTL extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private validasi Valid=new validasi();
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i=0;

    public SatuSehatMappingKPTL(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);
        setSize(900,640);

        tabMode=new DefaultTableModel(null,new Object[]{
                "Kd.Jenis","Nama Perawatan","Sumber","Kode KPTL","Display KPTL"
            }){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] w={90,360,70,110,320};
        for (i=0; i<w.length; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            column.setPreferredWidth(w[i]);
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TKodeKPTL.setDocument(new batasInput((byte)40).getKata(TKodeKPTL));
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
        BtnAutoMap = new widget.Button();
        BtnAll = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabelS = new widget.Label();
        Cmbsumber = new widget.ComboBox();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        PanelInput = new javax.swing.JPanel();
        ChkInput = new widget.CekBox();
        FormInput = new widget.PanelBiasa();
        jLabel1 = new widget.Label();
        TKdJenis = new widget.TextBox();
        btnJnsRalan = new widget.Button();
        btnJnsRanap = new widget.Button();
        jLabel2 = new widget.Label();
        TSumber = new widget.TextBox();
        jLabel3 = new widget.Label();
        TNama = new widget.TextBox();
        jLabel4 = new widget.Label();
        TKodeKPTL = new widget.TextBox();
        BtnPilih = new widget.Button();
        jLabel5 = new widget.Label();
        TDisplay = new widget.TextBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Mapping Jenis Perawatan ke Kode KPTL (ChargeItem Satu Sehat) ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
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

        BtnAutoMap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnAutoMap.setMnemonic('A');
        BtnAutoMap.setText("Auto Map");
        BtnAutoMap.setToolTipText("Alt+A (cocokkan nama tindakan dengan display KPTL persis)");
        BtnAutoMap.setName("BtnAutoMap"); // NOI18N
        BtnAutoMap.setPreferredSize(new java.awt.Dimension(110, 23));
        BtnAutoMap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAutoMapActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnAutoMap);

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

        jLabel6.setText("Cari Tindakan :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(85, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(260, 23));
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

        jLabelS.setText("Sumber :");
        jLabelS.setName("jLabelS"); // NOI18N
        jLabelS.setPreferredSize(new java.awt.Dimension(55, 23));
        panelGlass9.add(jLabelS);

        Cmbsumber.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semua", "Ralan", "Ranap", "Lab", "Radiologi" }));
        Cmbsumber.setName("Cmbsumber"); // NOI18N
        Cmbsumber.setPreferredSize(new java.awt.Dimension(90, 23));
        Cmbsumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        panelGlass9.add(Cmbsumber);

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
        PanelInput.setPreferredSize(new java.awt.Dimension(660, 125));
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
        FormInput.setPreferredSize(new java.awt.Dimension(100, 100));
        FormInput.setLayout(null);

        jLabel1.setText("Kd.Jenis Prw :");
        jLabel1.setName("jLabel1"); // NOI18N
        FormInput.add(jLabel1);
        jLabel1.setBounds(0, 10, 95, 23);

        TKdJenis.setEditable(false);
        TKdJenis.setName("TKdJenis"); // NOI18N
        FormInput.add(TKdJenis);
        TKdJenis.setBounds(99, 10, 120, 23);

        btnJnsRalan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/attachment.png"))); // NOI18N
        btnJnsRalan.setToolTipText("Master Jenis Perawatan Ralan");
        btnJnsRalan.setName("btnJnsRalan"); // NOI18N
        btnJnsRalan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJnsRalanActionPerformed(evt);
            }
        });
        FormInput.add(btnJnsRalan);
        btnJnsRalan.setBounds(221, 10, 28, 23);

        btnJnsRanap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/attachment.png"))); // NOI18N
        btnJnsRanap.setToolTipText("Master Jenis Perawatan Ranap");
        btnJnsRanap.setName("btnJnsRanap"); // NOI18N
        btnJnsRanap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnJnsRanapActionPerformed(evt);
            }
        });
        FormInput.add(btnJnsRanap);
        btnJnsRanap.setBounds(251, 10, 28, 23);

        jLabel2.setText("Sumber :");
        jLabel2.setName("jLabel2"); // NOI18N
        FormInput.add(jLabel2);
        jLabel2.setBounds(290, 10, 55, 23);

        TSumber.setEditable(false);
        TSumber.setName("TSumber"); // NOI18N
        FormInput.add(TSumber);
        TSumber.setBounds(345, 10, 90, 23);

        jLabel3.setText("Nama Perawatan :");
        jLabel3.setName("jLabel3"); // NOI18N
        FormInput.add(jLabel3);
        jLabel3.setBounds(0, 40, 95, 23);

        TNama.setEditable(false);
        TNama.setName("TNama"); // NOI18N
        FormInput.add(TNama);
        TNama.setBounds(99, 40, 625, 23);

        jLabel4.setText("Kode KPTL :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 70, 95, 23);

        TKodeKPTL.setEditable(false);
        TKodeKPTL.setName("TKodeKPTL"); // NOI18N
        FormInput.add(TKodeKPTL);
        TKodeKPTL.setBounds(99, 70, 120, 23);

        BtnPilih.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnPilih.setMnemonic('2');
        BtnPilih.setText("Pilih KPTL");
        BtnPilih.setToolTipText("Alt+2");
        BtnPilih.setName("BtnPilih"); // NOI18N
        BtnPilih.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPilihActionPerformed(evt);
            }
        });
        FormInput.add(BtnPilih);
        BtnPilih.setBounds(222, 70, 110, 23);

        jLabel5.setText("Display KPTL :");
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
        TKdJenis.setText(tabMode.getValueAt(r,0).toString());
        TNama.setText(tabMode.getValueAt(r,1).toString());
        TSumber.setText(tabMode.getValueAt(r,2).toString());
        TKodeKPTL.setText(tabMode.getValueAt(r,3).toString());
        TDisplay.setText(tabMode.getValueAt(r,4).toString());
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
        Cmbsumber.setSelectedItem("Semua");
        tampil();
    }

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {
        if(evt.getKeyCode()==KeyEvent.VK_ENTER) tampil();
    }

    private void BtnBaruActionPerformed(java.awt.event.ActionEvent evt) {
        TKdJenis.setText("");
        TSumber.setText("");
        TNama.setText("");
        TKodeKPTL.setText("");
        TDisplay.setText("");
    }

    private void BtnPilihActionPerformed(java.awt.event.ActionEvent evt) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        DlgCariKPTL d = new DlgCariKPTL(null, true);
        d.setSize(internalFrame1.getWidth()-30, 320);
        d.setLocationRelativeTo(internalFrame1);
        d.setVisible(true);
        if(d.isSukses()){
            TKodeKPTL.setText(d.getKodePilih());
            TDisplay.setText(d.getDisplayPilih());
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {
        if(TKdJenis.getText().trim().equals("")){
            Valid.textKosong(TKdJenis,"Kd.Jenis Perawatan (pilih baris tabel)");
            return;
        }
        if(TKodeKPTL.getText().trim().equals("")){
            Valid.textKosong(TKodeKPTL,"Kode KPTL (tombol Pilih KPTL)");
            return;
        }
        try {
            ps = koneksi.prepareStatement("replace into satu_sehat_mapping_kptl (kd_jenis_prw,kode_kptl,display) values (?,?,?)");
            ps.setString(1,TKdJenis.getText().trim());
            ps.setString(2,TKodeKPTL.getText().trim());
            ps.setString(3,TDisplay.getText().trim());
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Gagal simpan : "+e);
            return;
        }
        tampil();
    }

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {
        if(TKdJenis.getText().trim().equals("")){
            Valid.textKosong(TKdJenis,"Kd.Jenis Perawatan (pilih baris tabel)");
            return;
        }
        try {
            ps = koneksi.prepareStatement("delete from satu_sehat_mapping_kptl where kd_jenis_prw=?");
            ps.setString(1,TKdJenis.getText().trim());
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,"Gagal hapus : "+e);
            return;
        }
        TKodeKPTL.setText("");
        TDisplay.setText("");
        tampil();
    }

    /** Auto map: cocokkan nm_perawatan (ralan+ranap) dengan display KPTL persis. */
    private void BtnAutoMapActionPerformed(java.awt.event.ActionEvent evt) {
        int n = JOptionPane.showConfirmDialog(this,
                "Cocokkan otomatis nama tindakan dengan display KPTL yang sama persis?\n"+
                "Mapping yang sudah ada tidak ditimpa.","Auto Map KPTL",JOptionPane.YES_NO_OPTION);
        if(n!=JOptionPane.YES_OPTION) return;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        int total=0;
        String[] sumber = {"jns_perawatan","jns_perawatan_inap","jns_perawatan_lab","jns_perawatan_radiologi"};
        for(String tbl : sumber){
            String sql =
                "insert ignore into satu_sehat_mapping_kptl (kd_jenis_prw, kode_kptl, display) "+
                "select t.kd_jenis_prw, t.kode_kptl, t.display from ("+
                "  select jp.kd_jenis_prw, m.kode_kptl, m.display, "+
                "         row_number() over (partition by jp.kd_jenis_prw order by length(m.kode_kptl), m.kode_kptl) rn "+
                "  from "+tbl+" jp "+
                "  join satu_sehat_kptl_master m on m.status='Aktif' and lower(trim(m.display))=lower(trim(jp.nm_perawatan)) "+
                "  where jp.status='1'"+
                ") t where t.rn=1";
            try {
                ps = koneksi.prepareStatement(sql);
                total += ps.executeUpdate();
                ps.close();
            } catch (Exception e) {
                System.out.println("Notif auto map ("+tbl+") : "+e);
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
        JOptionPane.showMessageDialog(this,"Auto map selesai. Mapping baru: "+total);
        tampil();
    }

    private void btnJnsRalanActionPerformed(java.awt.event.ActionEvent evt) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        keuangan.DlgJnsPerawatanRalan form = new keuangan.DlgJnsPerawatanRalan(null, false);
        form.emptTeks();
        form.isCek();
        form.setSize(internalFrame1.getWidth()-30, internalFrame1.getHeight()-60);
        form.setLocationRelativeTo(internalFrame1);
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
        tampil();
    }

    private void btnJnsRanapActionPerformed(java.awt.event.ActionEvent evt) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        keuangan.DlgJnsPerawatanRanap form = new keuangan.DlgJnsPerawatanRanap(null, false);
        form.emptTeks();
        form.isCek();
        form.setSize(internalFrame1.getWidth()-30, internalFrame1.getHeight()-60);
        form.setLocationRelativeTo(internalFrame1);
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
        tampil();
    }

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    public void tampil() {
        try {
            while(tabMode.getRowCount()>0) tabMode.removeRow(0);
            String c = "%"+TCari.getText().trim()+"%";
            String sumber = String.valueOf(Cmbsumber.getSelectedItem());
            String sql =
                "select * from ("+
                "  select jp.kd_jenis_prw, jp.nm_perawatan, 'Ralan' sumber, "+
                "         ifnull(m.kode_kptl,'') kode_kptl, ifnull(m.display,'') display "+
                "  from jns_perawatan jp left join satu_sehat_mapping_kptl m on m.kd_jenis_prw=jp.kd_jenis_prw "+
                "  where jp.status='1' "+
                "  union all "+
                "  select ji.kd_jenis_prw, ji.nm_perawatan, 'Ranap' sumber, "+
                "         ifnull(m.kode_kptl,''), ifnull(m.display,'') "+
                "  from jns_perawatan_inap ji left join satu_sehat_mapping_kptl m on m.kd_jenis_prw=ji.kd_jenis_prw "+
                "  where ji.status='1' "+
                // Lab & radiologi juga ditagihkan lewat ChargeItem: kirimBillingBundle
                // meng-inner-join periksa_lab/periksa_radiologi ke tabel mapping ini,
                // jadi yang belum dipetakan tidak pernah masuk klaim.
                "  union all "+
                "  select jl.kd_jenis_prw, jl.nm_perawatan, 'Lab' sumber, "+
                "         ifnull(m.kode_kptl,''), ifnull(m.display,'') "+
                "  from jns_perawatan_lab jl left join satu_sehat_mapping_kptl m on m.kd_jenis_prw=jl.kd_jenis_prw "+
                "  where jl.status='1' "+
                "  union all "+
                "  select jr.kd_jenis_prw, jr.nm_perawatan, 'Radiologi' sumber, "+
                "         ifnull(m.kode_kptl,''), ifnull(m.display,'') "+
                "  from jns_perawatan_radiologi jr left join satu_sehat_mapping_kptl m on m.kd_jenis_prw=jr.kd_jenis_prw "+
                "  where jr.status='1' "+
                ") x where (x.kd_jenis_prw like ? or x.nm_perawatan like ?) "+
                (sumber.equals("Semua")?"":"and x.sumber=? ")+
                "order by x.nm_perawatan, x.sumber limit 5000";
            ps = koneksi.prepareStatement(sql);
            ps.setString(1,c);
            ps.setString(2,c);
            if(!sumber.equals("Semua")) ps.setString(3,sumber);
            rs = ps.executeQuery();
            while(rs.next()){
                tabMode.addRow(new Object[]{
                    rs.getString("kd_jenis_prw"), rs.getString("nm_perawatan"), rs.getString("sumber"),
                    rs.getString("kode_kptl"), rs.getString("display")
                });
            }
        } catch (Exception e) {
            System.out.println("Notif tampil mapping KPTL : "+e);
        } finally {
            try { if(rs!=null) rs.close(); if(ps!=null) ps.close(); } catch(Exception x){}
        }
        LCount.setText(""+tabMode.getRowCount());
    }

    public void isCek() {
        boolean bisa = akses.getsatu_sehat_kirim_encounter();
        BtnSimpan.setEnabled(bisa);
        BtnHapus.setEnabled(bisa);
        BtnAutoMap.setEnabled(bisa);
    }

    public JTable getTable(){ return tbObat; }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            SatuSehatMappingKPTL d = new SatuSehatMappingKPTL(new javax.swing.JFrame(), true);
            d.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosing(java.awt.event.WindowEvent e) { System.exit(0); }
            });
            d.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnAll;
    private widget.Button BtnAutoMap;
    private widget.Button BtnBaru;
    private widget.Button BtnCari;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPilih;
    private widget.Button BtnSimpan;
    private widget.Button btnJnsRalan;
    private widget.Button btnJnsRanap;
    private widget.CekBox ChkInput;
    private widget.ComboBox Cmbsumber;
    private widget.PanelBiasa FormInput;
    private widget.Label LCount;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TDisplay;
    private widget.TextBox TKdJenis;
    private widget.TextBox TKodeKPTL;
    private widget.TextBox TNama;
    private widget.TextBox TSumber;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel1;
    private widget.Label jLabel2;
    private widget.Label jLabel3;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabelS;
    private javax.swing.JPanel jPanel3;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables
}
