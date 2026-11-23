/*
  Dilarang keras menggandakan/mengcopy/menyebarkan/membajak/mendecompile 
  Software ini dalam bentuk apapun tanpa seijin pembuat software
  (Khanza.Soft Media). Bagi yang sengaja membajak softaware ini ta
  npa ijin, kami sumpahi sial 1000 turunan, miskin sampai 500 turu
  nan. Selalu mendapat kecelakaan sampai 400 turunan. Anak pertama
  nya cacat tidak punya kaki sampai 300 turunan. Susah cari jodoh
  sampai umur 50 tahun sampai 200 turunan. Ya Alloh maafkan kami 
  karena telah berdoa buruk, semua ini kami lakukan karena kami ti
  dak pernah rela karya kami dibajak tanpa ijin.
 */

package simrskhanza;

import fungsi.WarnaTable;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;



public class DlgCariSnomed extends javax.swing.JDialog {

    private DefaultTableModel tabMode;
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps;
    private int i = 0;
    private ResultSet rs;
    private javax.swing.JComboBox<String> cmbHistory;
    private javax.swing.JComboBox<String> cmbFilterSystem;
    


    public DlgCariSnomed(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        tabMode = new DefaultTableModel(null,new Object[]{
            "System","Code","Display"
        }) {
            Class[] types = new Class[]{
                java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbObat.setModel(tabMode);

        tbObat.setPreferredScrollableViewportSize(new Dimension(800, 800));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 3; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(150);
            } else if (i == 1) {
                column.setPreferredWidth(100);
            } else if (i == 2) {
                column.setPreferredWidth(300);
            } 
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        widget.Button BtnDariSnowstorm = new widget.Button();
        BtnDariSnowstorm.setText("Dari Snowstorm");
        java.net.URL iconSnowstorm = getClass().getResource("/picture/refresh.png");
        if (iconSnowstorm != null) BtnDariSnowstorm.setIcon(new javax.swing.ImageIcon(iconSnowstorm));
        BtnDariSnowstorm.setPreferredSize(new java.awt.Dimension(150, 30));
        BtnDariSnowstorm.addActionListener(evt -> {
            bridging.ApiSNOMED api = new bridging.ApiSNOMED(this);
            if (!api.isConfigured()) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "URL Snowstorm belum dikonfigurasi di database.xml (URLAPISNOWSTORM)");
                return;
            }
            String kw = TCari.getText().trim();
            if (kw.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this,
                        "Isi keyword terlebih dahulu sebelum sinkronisasi dari Snowstorm.");
                TCari.requestFocus();
                return;
            }
            BtnDariSnowstorm.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            new SwingWorker<bridging.ApiSNOMED.SyncResult, Void>() {
                @Override
                protected bridging.ApiSNOMED.SyncResult doInBackground() {
                    return api.syncToLocalDB(kw, 100, koneksi);
                }
                @Override
                protected void done() {
                    try {
                        bridging.ApiSNOMED.SyncResult res = get();
                        if (res.isSuccess()) {
                            javax.swing.JOptionPane.showMessageDialog(DlgCariSnomed.this, res.getMessage());
                            tampil();
                        } else {
                            javax.swing.JOptionPane.showMessageDialog(DlgCariSnomed.this,
                                    "Gagal: " + res.getMessage());
                        }
                    } catch (Exception ex) {
                        javax.swing.JOptionPane.showMessageDialog(DlgCariSnomed.this,
                                "Error: " + ex.getMessage());
                    } finally {
                        BtnDariSnowstorm.setEnabled(true);
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            }.execute();
        });
        panelisi2.add(BtnDariSnowstorm);

        // History pencarian (10 keyword terakhir)
        widget.Label lblHistory = new widget.Label();
        lblHistory.setText("History:");
        lblHistory.setPreferredSize(new java.awt.Dimension(52, 23));
        cmbHistory = new javax.swing.JComboBox<>();
        cmbHistory.setPreferredSize(new java.awt.Dimension(160, 23));
        cmbHistory.addItem("-- Pilih history --");
        cmbHistory.addActionListener(ev -> {
            if (cmbHistory.getSelectedIndex() > 0) {
                TCari.setText(cmbHistory.getSelectedItem().toString());
                tampil();
            }
        });
        panelisi2.add(lblHistory);
        panelisi2.add(cmbHistory);

        // Filter System
        widget.Label lblSys = new widget.Label();
        lblSys.setText("System:");
        lblSys.setPreferredSize(new java.awt.Dimension(50, 23));
        cmbFilterSystem = new javax.swing.JComboBox<>(new String[]{"Semua", "http://snomed.info/sct", "Custom"});
        cmbFilterSystem.setPreferredSize(new java.awt.Dimension(165, 23));
        cmbFilterSystem.addActionListener(ev -> tampil());
        panelisi2.add(lblSys);
        panelisi2.add(cmbFilterSystem);

        // Tombol Salin Kode
        widget.Button BtnSalinKode = new widget.Button();
        BtnSalinKode.setText("Salin Kode");
        java.net.URL iconSalin = getClass().getResource("/picture/editcopy.png");
        if (iconSalin != null) BtnSalinKode.setIcon(new javax.swing.ImageIcon(iconSalin));
        BtnSalinKode.setPreferredSize(new java.awt.Dimension(110, 30));
        BtnSalinKode.addActionListener(ev -> salinKode());
        panelisi2.add(BtnSalinKode);

        // Tombol Export CSV
        widget.Button BtnExportCSV = new widget.Button();
        BtnExportCSV.setText("Export CSV");
        java.net.URL iconExport = getClass().getResource("/picture/Export.png");
        if (iconExport != null) BtnExportCSV.setIcon(new javax.swing.ImageIcon(iconExport));
        BtnExportCSV.setPreferredSize(new java.awt.Dimension(110, 30));
        BtnExportCSV.addActionListener(ev -> exportCSV());
        panelisi2.add(BtnExportCSV);

        // Tambah history saat BtnCari diklik atau Enter di TCari
        BtnCari.addActionListener(ev -> tambahHistory(TCari.getText().trim()));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    tambahHistory(TCari.getText().trim());
                }
            }
        });

        isForm();

        
        
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
            });
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        jPanel2 = new javax.swing.JPanel();
        panelisi2 = new widget.panelisi();
        label9 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        label10 = new widget.Label();
        LCount = new widget.Label();
        jLabel6 = new widget.Label();
        cmbHlm = new widget.ComboBox();
        BtnKeluar = new widget.Button();
        scrollPane1 = new widget.ScrollPane();
        tbObat = new widget.Table();
        PanelInput = new javax.swing.JPanel();
        ChkInput = new widget.CekBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Cari Snomed-CT]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setOpaque(false);
        jPanel2.setPreferredSize(new java.awt.Dimension(816, 100));
        jPanel2.setLayout(new java.awt.BorderLayout(1, 1));

        panelisi2.setBackground(new java.awt.Color(255, 150, 255));
        panelisi2.setName("panelisi2"); // NOI18N
        panelisi2.setPreferredSize(new java.awt.Dimension(100, 44));
        panelisi2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 9));

        label9.setText("Key Word :");
        label9.setName("label9"); // NOI18N
        label9.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi2.add(label9);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(200, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelisi2.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('3');
        BtnCari.setToolTipText("Alt+3");
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
        panelisi2.add(BtnCari);

        label10.setText("Record :");
        label10.setName("label10"); // NOI18N
        label10.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi2.add(label10);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi2.add(LCount);

        jLabel6.setText("Limit Data :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi2.add(jLabel6);

        cmbHlm.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "100", "200", "300", "400", "500", "1000", "Semua" }));
        cmbHlm.setName("cmbHlm"); // NOI18N
        cmbHlm.setPreferredSize(new java.awt.Dimension(90, 23));
        panelisi2.add(cmbHlm);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar"); // NOI18N
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarActionPerformed(evt);
            }
        });
        BtnKeluar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnKeluarKeyPressed(evt);
            }
        });
        panelisi2.add(BtnKeluar);

        jPanel2.add(panelisi2, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel2, java.awt.BorderLayout.PAGE_END);

        scrollPane1.setName("scrollPane1"); // NOI18N
        scrollPane1.setOpaque(true);

        tbObat.setAutoCreateRowSorter(true);
        tbObat.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat.setName("tbObat"); // NOI18N
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbObatKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbObatKeyReleased(evt);
            }
        });
        scrollPane1.setViewportView(tbObat);

        internalFrame1.add(scrollPane1, java.awt.BorderLayout.CENTER);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(660, 338));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setMnemonic('I');
        ChkInput.setText(".: Input Data");
        ChkInput.setToolTipText("Alt+I");
        ChkInput.setBorderPainted(true);
        ChkInput.setBorderPaintedFlat(true);
        ChkInput.setFocusable(false);
        ChkInput.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ChkInput.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ChkInput.setName("ChkInput"); // NOI18N
        ChkInput.setPreferredSize(new java.awt.Dimension(192, 20));
        ChkInput.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/145.png"))); // NOI18N
        ChkInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkInputActionPerformed(evt);
            }
        });
        PanelInput.add(ChkInput, java.awt.BorderLayout.PAGE_END);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
    tampil();
}//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
    tampil();

}//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
    tampil();
}//GEN-LAST:event_BtnCariKeyPressed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked

}//GEN-LAST:event_tbObatMouseClicked

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyPressed
        if (tabMode.getRowCount() != 0) {
            if (evt.getKeyCode() == KeyEvent.VK_SHIFT) {
                TCari.setText("");
                TCari.requestFocus();
            }
        }
}//GEN-LAST:event_tbObatKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
      tampil();
    }//GEN-LAST:event_formWindowOpened

    private void tbObatKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyReleased
        
    }//GEN-LAST:event_tbObatKeyReleased

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
       dispose();
    }//GEN-LAST:event_BtnKeluarKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgCariSnomed dialog = new DlgCariSnomed(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnCari;
    private widget.Button BtnKeluar;
    private widget.CekBox ChkInput;
    private widget.Label LCount;
    private javax.swing.JPanel PanelInput;
    private widget.TextBox TCari;
    private widget.ComboBox cmbHlm;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel6;
    private javax.swing.JPanel jPanel2;
    private widget.Label label10;
    private widget.Label label9;
    private widget.panelisi panelisi2;
    private widget.ScrollPane scrollPane1;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        Valid.tabelKosong(tabMode);
        String keyword   = TCari.getText().trim();
        String sysFilter = cmbFilterSystem != null ? (String) cmbFilterSystem.getSelectedItem() : "Semua";
        String limitStr  = cmbHlm.getSelectedItem().toString();

        // Build WHERE clause
        StringBuilder where = new StringBuilder(" WHERE 1=1");
        if (!keyword.isEmpty())                           where.append(" AND display LIKE ?");
        if ("http://snomed.info/sct".equals(sysFilter))  where.append(" AND system='http://snomed.info/sct'");
        else if ("Custom".equals(sysFilter))              where.append(" AND system!='http://snomed.info/sct'");

        String limitClause = "Semua".equalsIgnoreCase(limitStr) ? "" : " LIMIT " + limitStr;

        int total = 0;
        try {
            // Query total
            PreparedStatement psCnt = koneksi.prepareStatement("SELECT COUNT(*) FROM snomed" + where);
            int p = 1;
            if (!keyword.isEmpty()) psCnt.setString(p++, "%" + keyword + "%");
            ResultSet rsCnt = psCnt.executeQuery();
            if (rsCnt.next()) total = rsCnt.getInt(1);
            rsCnt.close(); psCnt.close();

            // Query data
            ps = koneksi.prepareStatement("SELECT * FROM snomed" + where + " ORDER BY display ASC" + limitClause);
            p = 1;
            if (!keyword.isEmpty()) ps.setString(p++, "%" + keyword + "%");
            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new Object[]{rs.getString("system"), rs.getString("code"), rs.getString("display")});
                }
            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
            } finally {
                if (rs  != null) rs.close();
                if (ps  != null) ps.close();
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }

        int shown = tabMode.getRowCount();
        LCount.setText(shown < total ? shown + " dari " + total : "" + shown);
    }

    

    public JTable getTable() {
        return tbObat;
    }

    

    private void isForm() {
        if (ChkInput.isSelected() == true) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 338));
            ChkInput.setVisible(true);
        } else if (ChkInput.isSelected() == false) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 20));
            //FormInput.setVisible(false);
            ChkInput.setVisible(true);
        }
    }

    
            
    private void tambahHistory(String kw) {
        if (kw == null || kw.isEmpty()) return;
        // Hapus jika sudah ada (hindari duplikat)
        for (int idx = 1; idx < cmbHistory.getItemCount(); idx++) {
            if (kw.equals(cmbHistory.getItemAt(idx))) {
                cmbHistory.removeItemAt(idx);
                break;
            }
        }
        cmbHistory.insertItemAt(kw, 1); // selalu di urutan pertama setelah placeholder
        // Batasi 10 item (1 placeholder + 10 keyword)
        while (cmbHistory.getItemCount() > 11) {
            cmbHistory.removeItemAt(cmbHistory.getItemCount() - 1);
        }
        cmbHistory.setSelectedIndex(0);
    }

    private void salinKode() {
        int row = tbObat.getSelectedRow();
        if (row < 0) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Pilih baris terlebih dahulu.", "Info", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Object kode = tbObat.getValueAt(row, 1);
        if (kode == null) return;
        java.awt.datatransfer.StringSelection sel =
            new java.awt.datatransfer.StringSelection(kode.toString());
        java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);
        javax.swing.JOptionPane.showMessageDialog(this,
            "Kode \"" + kode + "\" berhasil disalin ke clipboard.",
            "Disalin", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportCSV() {
        if (tabMode.getRowCount() == 0) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Tidak ada data untuk di-export.", "Info", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        fc.setSelectedFile(new java.io.File("snomed_export_"
            + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".csv"));
        if (fc.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter pw = new java.io.PrintWriter(
                    new java.io.OutputStreamWriter(
                        new java.io.FileOutputStream(fc.getSelectedFile()),
                        java.nio.charset.StandardCharsets.UTF_8))) {
                pw.print('\uFEFF'); // BOM untuk Excel
                pw.println("\"System\",\"Code\",\"Display\"");
                for (int r = 0; r < tbObat.getRowCount(); r++) {
                    String system  = tbObat.getValueAt(r, 0) != null ? tbObat.getValueAt(r, 0).toString().replace("\"", "\"\"") : "";
                    String code    = tbObat.getValueAt(r, 1) != null ? tbObat.getValueAt(r, 1).toString().replace("\"", "\"\"") : "";
                    String display = tbObat.getValueAt(r, 2) != null ? tbObat.getValueAt(r, 2).toString().replace("\"", "\"\"") : "";
                    pw.println("\"" + system + "\",\"" + code + "\",\"" + display + "\"");
                }
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Export berhasil!\n" + fc.getSelectedFile().getAbsolutePath(),
                    "Sukses", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Gagal export: " + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void isCek() {
        TCari.requestFocus();
        
    }
    
    

}
