/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * kontribusi dari dokter Salim Mulyana
 */

package surat;

import AESsecurity.EnkripsiAES;
import bridging.koneksiDBWa;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import bridging.EsignServiceV2;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JRResultSetDataSource;


/**
 * 
 * @author salimmulyana
 */
public final class SuratKeteranganSehatRohani extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private Connection koneksiwa;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i=0;
    private String tgl,finger="",kodedokter="",namadokter="",notifwasurat="",idgroupwasurat="";
    /** Creates new form DlgRujuk
     * @param parent
     * @param modal */
    public SuratKeteranganSehatRohani(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);
        setSize(628,674);
        
        tabMode=new DefaultTableModel(null,new Object[]{
            "No.Surat","No.Rawat","No.R.M","Nama Pasien","Tanggal","Keperluan","Kesimpulan"
        }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbObat.setModel(tabMode);

        //tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 7; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(105);
            }else if(i==1){
                column.setPreferredWidth(105);
            }else if(i==2){
                column.setPreferredWidth(70);
            }else if(i==3){
                column.setPreferredWidth(170);
            }else if(i==4){
                column.setPreferredWidth(90);
            }else if(i==5){
                column.setPreferredWidth(90);
            }else if(i==6){
                column.setPreferredWidth(90);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());
        
        NoSurat.setDocument(new batasInput((byte)17).getKata(NoSurat));
        TNoRw.setDocument(new batasInput((byte)17).getKata(TNoRw));        
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));           
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
        ChkInput.setSelected(false);
        isForm();
        try {
            notifwasurat = koneksiDB.NOTIFWASURAT();
            idgroupwasurat = koneksiDB.IDGROUPWASURAT();
        } catch (Exception e) {
            notifwasurat = koneksiDB.NOTIFWASURAT();
            idgroupwasurat = "no";
        }

        widget.Button btnHistoriCetak = new widget.Button();
        btnHistoriCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/clock.png")));
        btnHistoriCetak.setMnemonic('H'); btnHistoriCetak.setText("Histori Cetak");
        btnHistoriCetak.setPreferredSize(new java.awt.Dimension(140, 30));
        btnHistoriCetak.addActionListener((java.awt.event.ActionEvent evt) -> tampilHistoriCetakSuratSehatRohani());
        panelGlass8.add(btnHistoriCetak);
        widget.Button btnAuditEditHapus = new widget.Button();
        btnAuditEditHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/clock.png")));
        btnAuditEditHapus.setMnemonic('A'); btnAuditEditHapus.setText("Audit Edit/Hapus");
        btnAuditEditHapus.setPreferredSize(new java.awt.Dimension(160, 30));
        btnAuditEditHapus.addActionListener((java.awt.event.ActionEvent evt) -> tampilAuditSuratSehatRohani());
        panelGlass8.add(btnAuditEditHapus);
        panelGlass8.revalidate(); panelGlass8.repaint();
    }

    private void tampilHistoriCetakSuratSehatRohani() {
        final javax.swing.JDialog dlg = new javax.swing.JDialog(this, "Histori Pencetakan Surat Sehat Rohani", true);
        dlg.setSize(820, 480); dlg.setLocationRelativeTo(this); dlg.setLayout(new java.awt.BorderLayout(6, 6));
        final DefaultTableModel mdl = new DefaultTableModel(null, new Object[]{"No.Surat","No.Rawat","Jenis","Kode User","Tanggal","Jam"}) { @Override public boolean isCellEditable(int r, int c){ return false; } };
        final JTable tbl = new JTable(mdl); tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {150,120,140,120,100,90};
        for (int i = 0; i < widths.length; i++) tbl.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        javax.swing.JPanel top = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 5));
        top.add(new javax.swing.JLabel("Cari:"));
        final javax.swing.JTextField fCari = new javax.swing.JTextField(20); fCari.setText(TNoRw.getText()); top.add(fCari);
        final javax.swing.JButton bRefresh = new javax.swing.JButton("Refresh"); top.add(bRefresh);
        final javax.swing.JLabel lblInfo = new javax.swing.JLabel(""); top.add(lblInfo);
        Runnable loader = () -> {
            mdl.setRowCount(0);
            String key = "%" + fCari.getText().trim() + "%";
            String sql = "select no_surat,no_rawat,jenis_surat,kd_user,tanggal,jam from log_cetak_surat_sehat_rohani where no_surat like ? or no_rawat like ? or kd_user like ? order by tanggal desc, jam desc limit 500";
            PreparedStatement p = null; ResultSet r = null;
            try { p = koneksiDB.condb().prepareStatement(sql); p.setString(1, key); p.setString(2, key); p.setString(3, key); r = p.executeQuery(); int n = 0; while (r.next()) { mdl.addRow(new Object[]{r.getString(1),r.getString(2),r.getString(3),r.getString(4),r.getString(5),r.getString(6)}); n++; } lblInfo.setText("  " + n + " baris");
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Gagal: " + ex.getMessage()); }
            finally { try { if (r != null) r.close(); } catch (Exception ignore) {} try { if (p != null) p.close(); } catch (Exception ignore) {} }
        };
        bRefresh.addActionListener(e -> loader.run()); fCari.addActionListener(e -> loader.run());
        javax.swing.JButton bClose = new javax.swing.JButton("Tutup"); bClose.addActionListener(e -> dlg.dispose());
        javax.swing.JPanel bottom = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 5)); bottom.add(bClose);
        dlg.add(top, java.awt.BorderLayout.NORTH); dlg.add(new javax.swing.JScrollPane(tbl), java.awt.BorderLayout.CENTER); dlg.add(bottom, java.awt.BorderLayout.SOUTH);
        loader.run(); dlg.setVisible(true);
    }

    private void tampilAuditSuratSehatRohani() {
        final javax.swing.JDialog dlg = new javax.swing.JDialog(this, "Audit Edit & Hapus Surat Sehat Rohani", true);
        dlg.setSize(960, 480); dlg.setLocationRelativeTo(this); dlg.setLayout(new java.awt.BorderLayout(6, 6));
        final DefaultTableModel mdl = new DefaultTableModel(null, new Object[]{"Aksi","User","Tgl Aksi","Jam","No.Surat","No.Rawat","Tgl Surat","Keperluan","Kesimpulan"}) { @Override public boolean isCellEditable(int r, int c){ return false; } };
        final JTable tbl = new JTable(mdl); tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {60,90,100,80,140,120,90,160,140};
        for (int i = 0; i < widths.length; i++) tbl.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        javax.swing.JPanel top = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 5));
        top.add(new javax.swing.JLabel("Cari:"));
        final javax.swing.JTextField fCari = new javax.swing.JTextField(18); fCari.setText(TNoRw.getText()); top.add(fCari);
        top.add(new javax.swing.JLabel("  Aksi:"));
        final javax.swing.JComboBox<String> cbAksi = new javax.swing.JComboBox<>(new String[]{"SEMUA","EDIT","HAPUS"}); top.add(cbAksi);
        final javax.swing.JButton bRefresh = new javax.swing.JButton("Refresh"); top.add(bRefresh);
        final javax.swing.JLabel lblInfo = new javax.swing.JLabel(""); top.add(lblInfo);
        Runnable loader = () -> {
            mdl.setRowCount(0);
            String key = "%" + fCari.getText().trim() + "%";
            String aksiSel = cbAksi.getSelectedItem().toString();
            String sql = "select aksi,kd_user,tanggal_aksi,jam_aksi,no_surat,no_rawat,tanggalsurat,keperluan,kesimpulan from surat_keterangan_sehat_rohani_audit where (no_surat like ? or no_rawat like ? or kd_user like ?) " + (aksiSel.equals("SEMUA") ? "" : "and aksi=? ") + "order by tanggal_aksi desc, jam_aksi desc limit 500";
            PreparedStatement p = null; ResultSet r = null;
            try { p = koneksiDB.condb().prepareStatement(sql); p.setString(1, key); p.setString(2, key); p.setString(3, key); if (!aksiSel.equals("SEMUA")) p.setString(4, aksiSel); r = p.executeQuery(); int n = 0; while (r.next()) { Object[] row = new Object[9]; for (int i = 0; i < 9; i++) row[i] = r.getString(i + 1); mdl.addRow(row); n++; } lblInfo.setText("  " + n + " baris");
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Gagal: " + ex.getMessage()); }
            finally { try { if (r != null) r.close(); } catch (Exception ignore) {} try { if (p != null) p.close(); } catch (Exception ignore) {} }
        };
        bRefresh.addActionListener(e -> loader.run()); fCari.addActionListener(e -> loader.run()); cbAksi.addActionListener(e -> loader.run());
        javax.swing.JButton bClose = new javax.swing.JButton("Tutup"); bClose.addActionListener(e -> dlg.dispose());
        javax.swing.JPanel bottom = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 5)); bottom.add(bClose);
        dlg.add(top, java.awt.BorderLayout.NORTH); dlg.add(new javax.swing.JScrollPane(tbl), java.awt.BorderLayout.CENTER); dlg.add(bottom, java.awt.BorderLayout.SOUTH);
        loader.run(); dlg.setVisible(true);
    }

    private void snapshotSuratSehatRohaniAudit(String noSuratLama, String aksi) {
        PreparedStatement pSel = null, pIns = null; ResultSet rSel = null;
        try {
            pSel = koneksiDB.condb().prepareStatement("select no_surat,no_rawat,tanggalsurat,keperluan,kesimpulan from surat_keterangan_sehat_rohani where no_surat=?");
            pSel.setString(1, noSuratLama); rSel = pSel.executeQuery();
            if (rSel.next()) {
                pIns = koneksiDB.condb().prepareStatement("insert into surat_keterangan_sehat_rohani_audit(aksi,kd_user,tanggal_aksi,jam_aksi,no_surat,no_rawat,tanggalsurat,keperluan,kesimpulan) values(?,?,current_date(),current_time(),?,?,?,?,?)");
                pIns.setString(1, aksi); pIns.setString(2, akses.getkode());
                for (int k = 1; k <= 5; k++) pIns.setString(k + 2, rSel.getString(k));
                pIns.executeUpdate();
            }
        } catch (Exception ex) { System.out.println("snapshotSuratSehatRohaniAudit gagal: " + ex); }
        finally { try { if (rSel != null) rSel.close(); } catch (Exception ignore) {} try { if (pSel != null) pSel.close(); } catch (Exception ignore) {} try { if (pIns != null) pIns.close(); } catch (Exception ignore) {} }
    }
        
        

    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnCetakSuratKeteranganSehatRohani = new javax.swing.JMenuItem();
        MnTteSuratSehatRohani = new javax.swing.JMenuItem();
        MnBukaTteSuratSehatRohani = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        BtnAll = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel19 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        PanelInput = new javax.swing.JPanel();
        FormInput = new widget.PanelBiasa();
        jLabel3 = new widget.Label();
        NoSurat = new widget.TextBox();
        jLabel4 = new widget.Label();
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        TNoRM = new widget.TextBox();
        jLabel17 = new widget.Label();
        TanggalSurat = new widget.Tanggal();
        Keperluan = new widget.TextBox();
        jLabel5 = new widget.Label();
        jLabel28 = new widget.Label();
        CmbKesimpulan = new widget.ComboBox();
        ChkInput = new widget.CekBox();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnCetakSuratKeteranganSehatRohani.setBackground(new java.awt.Color(250, 250, 250));
        MnCetakSuratKeteranganSehatRohani.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnCetakSuratKeteranganSehatRohani.setForeground(new java.awt.Color(50, 50, 50));
        MnCetakSuratKeteranganSehatRohani.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnCetakSuratKeteranganSehatRohani.setText("Cetak Surat Keterangan Sehat Rohani");
        MnCetakSuratKeteranganSehatRohani.setToolTipText("");
        MnCetakSuratKeteranganSehatRohani.setName("MnCetakSuratKeteranganSehatRohani"); // NOI18N
        MnCetakSuratKeteranganSehatRohani.setPreferredSize(new java.awt.Dimension(200, 26));
        MnCetakSuratKeteranganSehatRohani.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnCetakSuratKeteranganSehatRohaniActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnCetakSuratKeteranganSehatRohani);

        MnTteSuratSehatRohani.setBackground(new java.awt.Color(250, 250, 250));
        MnTteSuratSehatRohani.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnTteSuratSehatRohani.setForeground(new java.awt.Color(50, 50, 50));
        MnTteSuratSehatRohani.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnTteSuratSehatRohani.setText("Tanda Tangan Elektronik (TTE BSrE)");
        MnTteSuratSehatRohani.setName("MnTteSuratSehatRohani"); // NOI18N
        MnTteSuratSehatRohani.setPreferredSize(new java.awt.Dimension(220, 26));
        MnTteSuratSehatRohani.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnTteSuratSehatRohaniActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnTteSuratSehatRohani);

        MnBukaTteSuratSehatRohani.setBackground(new java.awt.Color(250, 250, 250));
        MnBukaTteSuratSehatRohani.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnBukaTteSuratSehatRohani.setForeground(new java.awt.Color(50, 50, 50));
        MnBukaTteSuratSehatRohani.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnBukaTteSuratSehatRohani.setText("Buka Dokumen TTE BSrE");
        MnBukaTteSuratSehatRohani.setName("MnBukaTteSuratSehatRohani"); // NOI18N
        MnBukaTteSuratSehatRohani.setPreferredSize(new java.awt.Dimension(220, 26));
        MnBukaTteSuratSehatRohani.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnBukaTteSuratSehatRohaniActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnBukaTteSuratSehatRohani);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Surat Keterangan Sehat Rohani ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 200));

        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat.setComponentPopupMenu(jPopupMenu1);
        tbObat.setName("tbObat"); // NOI18N
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbObatKeyReleased(evt);
            }
        });
        Scroll.setViewportView(tbObat);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });
        BtnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSimpanKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnSimpan);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); // NOI18N
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Baru");
        BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setName("BtnBatal"); // NOI18N
        BtnBatal.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBatalActionPerformed(evt);
            }
        });
        BtnBatal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnBatalKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnBatal);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus"); // NOI18N
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusActionPerformed(evt);
            }
        });
        BtnHapus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnHapusKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnHapus);

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        BtnEdit.setMnemonic('G');
        BtnEdit.setText("Ganti");
        BtnEdit.setToolTipText("Alt+G");
        BtnEdit.setName("BtnEdit"); // NOI18N
        BtnEdit.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEditActionPerformed(evt);
            }
        });
        BtnEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnEditKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnEdit);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setName("BtnPrint"); // NOI18N
        BtnPrint.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrintActionPerformed(evt);
            }
        });
        BtnPrint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnPrintKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnPrint);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setText("Semua");
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllActionPerformed(evt);
            }
        });
        BtnAll.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnAllKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnAll);

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
        panelGlass8.add(BtnKeluar);

        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel19.setText("Tgl. Surat :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(67, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "04-01-2025" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari1);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel21);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "04-01-2025" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(205, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

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
        panelGlass9.add(BtnCari);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(65, 23));
        panelGlass9.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass9.add(LCount);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 96));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 165));
        FormInput.setLayout(null);

        jLabel3.setText("No. Surat Sakit :");
        jLabel3.setName("jLabel3"); // NOI18N
        FormInput.add(jLabel3);
        jLabel3.setBounds(0, 40, 95, 23);

        NoSurat.setHighlighter(null);
        NoSurat.setName("NoSurat"); // NOI18N
        NoSurat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoSuratKeyPressed(evt);
            }
        });
        FormInput.add(NoSurat);
        NoSurat.setBounds(99, 40, 141, 23);

        jLabel4.setText("No.Rawat :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 10, 95, 23);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(99, 10, 141, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        TPasien.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TPasienKeyPressed(evt);
            }
        });
        FormInput.add(TPasien);
        TPasien.setBounds(355, 10, 365, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        TNoRM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRMKeyPressed(evt);
            }
        });
        FormInput.add(TNoRM);
        TNoRM.setBounds(242, 10, 111, 23);

        jLabel17.setText("Tanggal :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(720, 10, 60, 23);

        TanggalSurat.setForeground(new java.awt.Color(50, 70, 50));
        TanggalSurat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "04-01-2025" }));
        TanggalSurat.setDisplayFormat("dd-MM-yyyy");
        TanggalSurat.setName("TanggalSurat"); // NOI18N
        TanggalSurat.setOpaque(false);
        TanggalSurat.setPreferredSize(new java.awt.Dimension(141, 18));
        TanggalSurat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TanggalSuratActionPerformed(evt);
            }
        });
        TanggalSurat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalSuratKeyPressed(evt);
            }
        });
        FormInput.add(TanggalSurat);
        TanggalSurat.setBounds(790, 10, 90, 23);

        Keperluan.setName("Keperluan"); // NOI18N
        Keperluan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeperluanKeyPressed(evt);
            }
        });
        FormInput.add(Keperluan);
        Keperluan.setBounds(330, 40, 260, 23);

        jLabel5.setText("Keperluan :");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(250, 40, 70, 23);

        jLabel28.setText("Kesimpulan :");
        jLabel28.setName("jLabel28"); // NOI18N
        FormInput.add(jLabel28);
        jLabel28.setBounds(610, 40, 75, 23);

        CmbKesimpulan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sehat Rohani", "Tidak Sehat Rohani" }));
        CmbKesimpulan.setName("CmbKesimpulan"); // NOI18N
        CmbKesimpulan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmbKesimpulanActionPerformed(evt);
            }
        });
        CmbKesimpulan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CmbKesimpulanKeyPressed(evt);
            }
        });
        FormInput.add(CmbKesimpulan);
        CmbKesimpulan.setBounds(690, 40, 190, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

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
        internalFrame1.getAccessibleContext().setAccessibleName("::[ Surat Keterangan Sehat Rohani ]::");
        internalFrame1.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void NoSuratKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoSuratKeyPressed
       Valid.pindah(evt,TCari,TanggalSurat);
}//GEN-LAST:event_NoSuratKeyPressed

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            isRawat();
            isPsien();
        }else{            
            Valid.pindah(evt,TCari,CmbKesimpulan);
        }
}//GEN-LAST:event_TNoRwKeyPressed

    private void TPasienKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TPasienKeyPressed
        Valid.pindah(evt,TCari,BtnSimpan);
}//GEN-LAST:event_TPasienKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(NoSurat.getText().trim().equals("")){
            Valid.textKosong(NoSurat,"No.Surat");
        }else if(TNoRw.getText().trim().equals("")||TPasien.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"pasien");
        }else if(Keperluan.getText().trim().equals("")){
            Valid.textKosong(Keperluan,"Keperluan");
        }else{
            if(Sequel.menyimpantf("surat_keterangan_sehat_rohani","?,?,?,?,?","No.Surat Sakit",5,new String[]{
                    NoSurat.getText(),TNoRw.getText(),Valid.SetTgl(TanggalSurat.getSelectedItem()+""),Keperluan.getText(),CmbKesimpulan.getSelectedItem()+""
                })==true){
                NotifWa();
                tampil();
                emptTeks();
            }
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
            Valid.pindah(evt,CmbKesimpulan,BtnBatal);
        }
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
        ChkInput.setSelected(true);
        isForm(); 
        
}//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            emptTeks();
        }else{Valid.pindah(evt, BtnSimpan, BtnHapus);}
}//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if(tabMode.getRowCount()>0 && !NoSurat.getText().trim().equals("")){
            snapshotSuratSehatRohaniAudit(NoSurat.getText(),"HAPUS");
        }
        Valid.hapusTable(tabMode,NoSurat,"surat_keterangan_sehat_rohani","no_surat");
        tampil();
        emptTeks();
}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnHapusActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if(NoSurat.getText().trim().equals("")){
            Valid.textKosong(NoSurat,"No.Surat");      
        }else if(TNoRw.getText().trim().equals("")||TPasien.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"pasien");    
        }else{    
            if(tbObat.getSelectedRow()!= -1){
                snapshotSuratSehatRohaniAudit(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),"EDIT");
                if(Sequel.mengedittf("surat_keterangan_sehat_rohani","no_surat=?","no_surat=?,no_rawat=?,tanggalsurat=?,keperluan=?,kesimpulan=?",6,new String[]{
                    NoSurat.getText(),TNoRw.getText(),Valid.SetTgl(TanggalSurat.getSelectedItem()+""),Keperluan.getText(),CmbKesimpulan.getSelectedItem().toString(),tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()
                })==true){
                    tampil();
                    emptTeks();
                }
            }
        }
}//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnEditActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
}//GEN-LAST:event_BtnEditKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{Valid.pindah(evt,BtnEdit,TCari);}
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        }else if(tabMode.getRowCount()!=0){
            Map<String, Object> param = new HashMap<>(); 
                param.put("namars",akses.getnamars());
                param.put("alamatrs",akses.getalamatrs());
                param.put("kotars",akses.getkabupatenrs());
                param.put("propinsirs",akses.getpropinsirs());
                param.put("kontakrs",akses.getkontakrs());
                param.put("emailrs",akses.getemailrs());   
                param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
            tgl=" suratsakit.tanggalawal between '"+Valid.SetTgl(DTPCari1.getSelectedItem()+"")+"' and '"+Valid.SetTgl(DTPCari2.getSelectedItem()+"")+"' ";
            if(TCari.getText().trim().equals("")){
                Valid.MyReportqry("rptSuratKeteranganSehatRohani.jasper","report","::[ Data Surat Sehat Rohani ]::",
                     "select suratsakit.no_surat,suratsakit.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"+
                     "suratsakit.tanggalawal,suratsakit.tanggalakhir,suratsakit.lamasakit "+                  
                     "from suratsakit inner join reg_periksa on suratsakit.no_rawat=reg_periksa.no_rawat "+
                     "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                     "where "+tgl+"order by suratsakit.no_surat",param);
            }else{
                Valid.MyReportqry("rptSuratKeteranganSehatRohani.jasper","report","::[ Data Surat Sakit Pasien ]::",
                     "select suratsakit.no_surat,suratsakit.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"+
                     "surat_keterangan_sehat_rohani.tanggalsurat,surat_keterangan_sehat_rohani.keperluan,surat_keterangan_sehat_rohani.kesimpulan "+                  
                     "from suratsakit inner join reg_periksa on suratsakit.no_rawat=reg_periksa.no_rawat "+
                     "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                     "where "+tgl+"and no_surat like '%"+TCari.getText().trim()+"%' or "+
                     tgl+"and surat_keterangan_sehat_rohani.no_rawat like '%"+TCari.getText().trim()+"%' or "+
                     tgl+"and reg_periksa.no_rkm_medis like '%"+TCari.getText().trim()+"%' or "+
                     tgl+"and pasien.nm_pasien like '%"+TCari.getText().trim()+"%' or "+
                     tgl+"and ssurat_keterangan_sehat_rohani.tanggalakhir like '%"+TCari.getText().trim()+"%' "+
                     "order by surat_keterangan_sehat_rohani.no_surat",param);
            }
            
        }
        this.setCursor(Cursor.getDefaultCursor());        
}//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnPrintActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnEdit, BtnKeluar);
        }
}//GEN-LAST:event_BtnPrintKeyPressed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            BtnKeluar.requestFocus();
        }
}//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
}//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt, TCari, BtnAll);
        }
}//GEN-LAST:event_BtnCariKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        tampil();
}//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            tampil();
            TCari.setText("");
        }else{
            Valid.pindah(evt, BtnCari, TPasien);
        }
}//GEN-LAST:event_BtnAllKeyPressed
   
                                  
    private void TNoRMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRMKeyPressed
       
}//GEN-LAST:event_TNoRMKeyPressed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if(tabMode.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
}//GEN-LAST:event_tbObatMouseClicked

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
       isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void tbObatKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyReleased
        if(tabMode.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }//GEN-LAST:event_tbObatKeyReleased

    private void MnCetakSuratKeteranganSehatRohaniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnCetakSuratKeteranganSehatRohaniActionPerformed
       if(TPasien.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Silahkan anda pilih dulu pasien...!!!");
        }else{
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                Sequel.menyimpan("log_cetak_surat_sehat_rohani","null,?,?,?,?,current_date(),current_time()",4,new String[]{
                    NoSurat.getText(),TNoRw.getText(),"Surat Sehat Rohani",akses.getkode()
                });
                Map<String, Object> param = new HashMap<>();
                param.put("namars",akses.getnamars());
                param.put("alamatrs",akses.getalamatrs());
                param.put("kotars",akses.getkabupatenrs());
                param.put("propinsirs",akses.getpropinsirs());
                param.put("kontakrs",akses.getkontakrs());
                param.put("emailrs",akses.getemailrs());  
                param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
                kodedokter=Sequel.cariIsi("select reg_periksa.kd_dokter from reg_periksa where reg_periksa.no_rawat=?",TNoRw.getText());
                namadokter=Sequel.cariIsi("select dokter.nm_dokter from dokter where dokter.kd_dokter=?",kodedokter);
                finger=Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",kodedokter);
                param.put("finger","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+namadokter+"\nID "+(finger.equals("")?kodedokter:finger)+"\n"+Sequel.cariIsi("select DATE_FORMAT(reg_periksa.tgl_registrasi,'%d-%m-%Y') from reg_periksa where reg_periksa.no_rawat=?",TNoRw.getText()));  
                param.put("verifikasi","https://"+koneksiDB.URLVERIFIKASI()+"/verify/?id="+EnkripsiAES.encrypt("{'x': 'skr', 't':'"+NoSurat.getText () +"'}"));
                Valid.MyReportqry("rptSuratKeteranganSehatRohani.jasper","report","::[ Surat Keterangan Sehat Rohani ]::",
                              " select surat_keterangan_sehat_rohani.no_surat,DATE_FORMAT(surat_keterangan_sehat_rohani.tanggalsurat,'%d-%m-%Y')as tanggalsurat, "+
                              " surat_keterangan_sehat_rohani.keperluan,surat_keterangan_sehat_rohani.kesimpulan,dokter.nm_dokter,pasien.jk,reg_periksa.kd_dokter," +
                              " pasien.nm_pasien,concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur)as umur,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat" +
                              " from surat_keterangan_sehat_rohani inner join reg_periksa inner join pasien inner join dokter inner join kelurahan inner join kecamatan inner join kabupaten" +
                              " on reg_periksa.no_rkm_medis=pasien.no_rkm_medis and reg_periksa.kd_dokter=dokter.kd_dokter and pasien.kd_kel=kelurahan.kd_kel and "+
                              " pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab and reg_periksa.no_rawat=surat_keterangan_sehat_rohani.no_rawat "+
                              " where reg_periksa.no_rawat='"+TNoRw.getText()+"' ",param);
                this.setCursor(Cursor.getDefaultCursor());  
       }
    }//GEN-LAST:event_MnCetakSuratKeteranganSehatRohaniActionPerformed

    private void TanggalSuratActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TanggalSuratActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TanggalSuratActionPerformed

    private void TanggalSuratKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalSuratKeyPressed
        Valid.pindah(evt,TCari,Keperluan);
    }//GEN-LAST:event_TanggalSuratKeyPressed

    private void KeperluanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KeperluanKeyPressed
        Valid.pindah(evt,Keperluan,CmbKesimpulan);
    }//GEN-LAST:event_KeperluanKeyPressed

    private void CmbKesimpulanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CmbKesimpulanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CmbKesimpulanActionPerformed

    private void CmbKesimpulanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CmbKesimpulanKeyPressed
        Valid.pindah(evt,Keperluan,BtnSimpan);
    }//GEN-LAST:event_CmbKesimpulanKeyPressed

    // ===== TTE BSrE Surat Keterangan Sehat Rohani & Simpan ke DlgBerkasRawat =====
    private void MnTteSuratSehatRohaniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnTteSuratSehatRohaniActionPerformed
        int row = tbObat.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, silakan pilih dulu data surat keterangan sehat rohani di tabel...!!!");
            return;
        }

        String noSurat = tbObat.getValueAt(row, 0).toString();
        String noRawat = tbObat.getValueAt(row, 1).toString();
        String noRM = tbObat.getValueAt(row, 2).toString();
        String namaPasien = tbObat.getValueAt(row, 3).toString();

        String kdDokter = Sequel.cariIsi("select reg_periksa.kd_dokter from reg_periksa where reg_periksa.no_rawat=?", noRawat);
        String nmDokter = Sequel.cariIsi("select dokter.nm_dokter from dokter where dokter.kd_dokter=?", kdDokter);

        String userLogin = akses.getkode();
        boolean isDokter = userLogin.equalsIgnoreCase(kdDokter);
        boolean isAdmin = akses.getadmin();

        if (!isDokter && !isAdmin) {
            JOptionPane.showMessageDialog(null, 
                "Otorisasi Ditolak!\n" +
                "Dokumen ini diterbitkan oleh: " + nmDokter + " (" + kdDokter + ")\n" +
                "Hanya dokter yang bersangkutan yang berhak melakukan TTE BSrE.", 
                "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (isAdmin && !isDokter) {
            int confirm = JOptionPane.showConfirmDialog(null, 
                "Perhatian: Anda login sebagai Administrator/Super Admin (" + userLogin + "), bukan dokter pemeriksa (" + nmDokter + ").\n" +
                "Apakah Anda ingin melanjutkan proses penandatanganan menggunakan akun/NIK dokter bersangkutan untuk pengujian?", 
                "Konfirmasi Mode Admin", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        String nikDokter = Sequel.cariIsi("select no_ktp from pegawai where nik=?", kdDokter);
        if (nikDokter == null || nikDokter.trim().isEmpty() || nikDokter.equals("-")) {
            nikDokter = Sequel.cariIsi("select no_ktp from petugas where nip=?", kdDokter);
        }
        if (nikDokter == null || nikDokter.trim().isEmpty() || nikDokter.equals("-")) {
            JOptionPane.showMessageDialog(null, 
                "NIK Dokter tidak ditemukan pada database pegawai/petugas!\n" +
                "Pastikan field NIK/No. KTP dokter terisi dengan benar untuk integrasi BSrE.", 
                "NIK Dokter Kosong", JOptionPane.WARNING_MESSAGE);
            return;
        }

        javax.swing.JPasswordField pwdField = new javax.swing.JPasswordField(20);
        pwdField.setTransferHandler(null);
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 1, 5, 5));
        panel.add(new javax.swing.JLabel("Penandatangan: " + nmDokter));
        panel.add(new javax.swing.JLabel("NIK Dokter    : " + (nikDokter.length() > 6 ? nikDokter.substring(0, 6) + "******" : nikDokter)));
        panel.add(new javax.swing.JLabel("Dokumen       : Surat Keterangan Sehat Rohani (" + noSurat + ")"));
        panel.add(new javax.swing.JLabel("Masukkan Passphrase TTE BSrE:"));
        panel.add(pwdField);

        int option = JOptionPane.showConfirmDialog(null, panel, "Otorisasi TTE BSrE", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option != JOptionPane.OK_OPTION) {
            return;
        }

        String passphrase = new String(pwdField.getPassword()).trim();
        if (passphrase.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Passphrase tidak boleh kosong!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cleanNoRawat = noRawat.replaceAll("[^a-zA-Z0-9]", "_");
        String cleanNoSurat = noSurat.replaceAll("[^a-zA-Z0-9]", "_");
        String checkSignedFileName = "SuratSehatRohani_" + cleanNoRawat + "_" + cleanNoSurat + ".pdf";
        if (Sequel.cariInteger("select count(*) from berkas_digital_perawatan where no_rawat=? and kode='068'", noRawat) > 0) {
            int reSign = JOptionPane.showConfirmDialog(null,
                "Surat Keterangan Sehat Rohani ini telah memiliki berkas TTE BSrE.\n" +
                "Apakah Anda ingin melakukan tanda tangan ulang (re-sign)?",
                "Konfirmasi TTE Ulang", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (reSign != JOptionPane.YES_OPTION) {
                return;
            }
        }

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            param.put("logo2", Sequel.cariGambar("select setting.logo from setting"));

            try {
                File fLogoPemda = new File("setting/logopemda.png");
                if (fLogoPemda.exists()) {
                    param.put("logo2", new FileInputStream(fLogoPemda));
                }
            } catch (Exception ignore) {}

            try {
                File fLogoBsre = new File("dist/report/logobsre.png");
                if (!fLogoBsre.exists()) fLogoBsre = new File("report/logobsre.png");
                if (fLogoBsre.exists()) {
                    param.put("logobsre", new FileInputStream(fLogoBsre));
                }
            } catch (Exception ignore) {}

            String sip = Sequel.cariIsi("select no_ijn_praktek from dokter where kd_dokter=?", kdDokter);
            param.put("sip", (sip == null || sip.isEmpty() || sip.equals("-")) ? "-" : sip);
            param.put("nip", (kdDokter == null || kdDokter.isEmpty() || kdDokter.equals("-")) ? "-" : kdDokter);
            String urlVerifikasiTTE = "https://" + koneksiDB.URLVERIFIKASITTE() + "/verify/?id=" + EnkripsiAES.encrypt("{'x': 'sehatrohani', 't':'" + noSurat + "'}");
            param.put("verifikasi", urlVerifikasiTTE);
            param.put("finger", "#");

            String sqlQuery = "select surat_keterangan_sehat_rohani.no_surat,DATE_FORMAT(surat_keterangan_sehat_rohani.tanggalsurat,'%d-%m-%Y')as tanggalsurat, "
                    + " surat_keterangan_sehat_rohani.keperluan,surat_keterangan_sehat_rohani.kesimpulan,dokter.nm_dokter,pasien.jk, "
                    + " pasien.nm_pasien,concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur)as umur, "
                    + " IF(kelurahan.nm_kel IS NOT NULL, concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab), pasien.alamat) as alamat, "
                    + " reg_periksa.no_rawat,reg_periksa.no_rkm_medis,DATE_FORMAT(pasien.tgl_lahir,'%d-%m-%Y') as tgl_lahir,pasien.pekerjaan "
                    + " from surat_keterangan_sehat_rohani "
                    + " inner join reg_periksa on reg_periksa.no_rawat=surat_keterangan_sehat_rohani.no_rawat "
                    + " inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                    + " inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter "
                    + " left join kelurahan on pasien.kd_kel=kelurahan.kd_kel "
                    + " left join kecamatan on pasien.kd_kec=kecamatan.kd_kec "
                    + " left join kabupaten on pasien.kd_kab=kabupaten.kd_kab "
                    + " where reg_periksa.no_rawat='" + noRawat + "' ";

            File dirTmp = new File("./tmpPDF");
            if (!dirTmp.exists()) {
                dirTmp.mkdirs();
            }

            String rawPdfPath = "./tmpPDF/SuratSehatRohani_raw_" + cleanNoRawat + ".pdf";
            String signedFileName = checkSignedFileName;
            String signedPdfPath = "./tmpPDF/" + signedFileName;

            // Generate Raw PDF dengan JasperReports
            try (PreparedStatement pst = koneksi.prepareStatement(sqlQuery);
                 ResultSet rst = pst.executeQuery()) {
                JRResultSetDataSource ds = new JRResultSetDataSource(rst);
                String jasperFile = "./report/rptSuratKeteranganSehatRohaniTTE.jasper";
                if (!new File(jasperFile).exists()) {
                    jasperFile = "report/rptSuratKeteranganSehatRohaniTTE.jasper";
                }
                if (!new File(jasperFile).exists()) {
                    jasperFile = "./dist/report/rptSuratKeteranganSehatRohaniTTE.jasper";
                }
                JasperPrint jp = JasperFillManager.fillReport(jasperFile, param, ds);
                JasperExportManager.exportReportToPdfFile(jp, rawPdfPath);
            }

            File rawFile = new File(rawPdfPath);
            if (!rawFile.exists() || rawFile.length() == 0) {
                JOptionPane.showMessageDialog(null, "Gagal meng-generate dokumen PDF Surat Keterangan Sehat Rohani!");
                this.setCursor(Cursor.getDefaultCursor());
                return;
            }

            File signedFile = new File(signedPdfPath);
            if (signedFile.exists()) {
                signedFile.delete();
            }

            // Eksekusi TTE BSrE V2
            EsignServiceV2 esign = new EsignServiceV2();
            List<EsignServiceV2.SignatureProperty> props = new ArrayList<>();
            EsignServiceV2.SignatureProperty sp = new EsignServiceV2.SignatureProperty();
            sp.tampilan = "VISIBLE";

            bridging.PDFTextLocationHelper.TextLocation loc = null;
            try {
                loc = new bridging.PDFTextLocationHelper().findText(rawFile, "#");
            } catch (Exception exLoc) {
                System.out.println("Gagal cari tag #: " + exLoc.getMessage());
            }
            if (loc != null) {
                sp.page = loc.page;
                float qrSize = 85.0f;
                sp.originX = loc.x + (loc.width / 2) - (qrSize / 2);
                sp.originY = loc.y;
                sp.pageWidth = loc.pageWidth;
                sp.pageHeight = loc.pageHeight;
                sp.width = qrSize;
                sp.height = qrSize;
            } else {
                sp.page = 1;
                sp.originX = 380.0f;
                sp.originY = 140.0f;
                sp.width = 85.0f;
                sp.height = 85.0f;
            }

            sp.location = akses.getkabupatenrs();
            sp.reason = "Dokumen Surat Keterangan Sehat Rohani telah disetujui dan ditandatangani secara elektronik";
            sp.linkQR = urlVerifikasiTTE;
            sp.imageBase64 = esign.generateQRCodeBase64(sp.linkQR, 400, 400);
            props.add(sp);

            boolean suksesTte = esign.prosesTTE(rawFile, signedFile, nikDokter, passphrase, props);
            if (!suksesTte || !signedFile.exists()) {
                String errMsg = esign.getLastErrorMessage() != null && !esign.getLastErrorMessage().isEmpty() 
                                ? esign.getLastErrorMessage() : "Penandatanganan gagal";
                int errCode = esign.getLastStatusCode();
                EsignServiceV2.catatLog(noRawat, nikDokter, nmDokter, "Gagal TTE Surat Sehat Rohani (" + noSurat + "): " + errMsg, String.valueOf(errCode));
                JOptionPane.showMessageDialog(null, 
                    "Penandatanganan Elektronik (TTE) Gagal!\n" +
                    "Status Code: " + errCode + "\n" +
                    "Pesan BSrE: " + errMsg, 
                    "TTE BSrE Gagal", JOptionPane.ERROR_MESSAGE);
                this.setCursor(Cursor.getDefaultCursor());
                return;
            }

            // Simpan ke Berkas Digital Perawatan
            simpanKeBerkasRawat(signedFile, signedFileName, noRawat);

            // Catat log sukses
            EsignServiceV2.catatLog(noRawat, nikDokter, nmDokter, "Sukses TTE Surat Sehat Rohani (" + noSurat + ") - File: " + signedFileName, "200");

            // Notifikasi berhasil
            JOptionPane.showMessageDialog(null, 
                "Surat Keterangan Sehat Rohani BERHASIL ditandatangani secara elektronik (BSrE)!\n" +
                "File : " + signedFileName + " (" + signedFile.length() + " bytes)\n" +
                "Penandatangan : " + nmDokter + "\n" +
                "Status : Dokumen resmi tersimpan ke Berkas Digital Perawatan (DlgBerkasRawat).", 
                "TTE BSrE Sukses", JOptionPane.INFORMATION_MESSAGE);

            // Bersihkan file raw
            if (rawFile.exists()) {
                rawFile.delete();
            }
            tampil();
        } catch (Exception e) {
            System.out.println("Error TTE Surat Sehat Rohani: " + e.getMessage());
            e.printStackTrace();
            EsignServiceV2.catatLog(noRawat, nikDokter, nmDokter, "Kesalahan Sistem TTE Surat Sehat Rohani (" + noSurat + "): " + e.getMessage(), "EXC");
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan sistem saat TTE: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_MnTteSuratSehatRohaniActionPerformed

    private void simpanKeBerkasRawat(File signedFile, String fileName, String noRawat) {
        try {
            // Upload ke web server hybrid jika tersedia
            try {
                byte[] data = org.apache.commons.io.FileUtils.readFileToByteArray(signedFile);
                org.apache.http.client.HttpClient httpClient = new org.apache.http.impl.client.DefaultHttpClient();
                org.apache.http.client.methods.HttpPost postReq = new org.apache.http.client.methods.HttpPost(
                    "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/unggah.php?doc=berkasrawat/pages/upload/");
                postReq.addHeader("X-Upload-Token", fungsi.koneksiDB.TOKENUNGGAH());
                org.apache.http.entity.mime.MultipartEntity reqEntity = new org.apache.http.entity.mime.MultipartEntity();
                reqEntity.addPart("file", new org.apache.http.entity.mime.content.ByteArrayBody(data, fileName));
                postReq.setEntity(reqEntity);
                httpClient.execute(postReq);
            } catch (Exception exUpload) {
                System.out.println("Upload berkas rawat via HTTP: " + exUpload.getMessage());
            }

            // Master berkas digital kode 068 untuk Surat Keterangan Sehat Rohani
            Sequel.queryu("INSERT IGNORE INTO master_berkas_digital (kode, nama) VALUES ('068', 'SURAT KETERANGAN SEHAT ROHANI')");

            String lokasiFile = "pages/upload/" + fileName;
            if (Sequel.cariInteger("SELECT COUNT(*) FROM berkas_digital_perawatan WHERE no_rawat=? AND kode=?", noRawat, "068") > 0) {
                Sequel.mengedittf("berkas_digital_perawatan", "no_rawat=? AND kode=?", "lokasi_file=?, nip=?", 4,
                    new String[]{lokasiFile, akses.getkode(), noRawat, "068"});
            } else {
                Sequel.menyimpantf("berkas_digital_perawatan", "?,?,?,?", "Surat Sehat Rohani TTE", 4,
                    new String[]{noRawat, "068", lokasiFile, akses.getkode()});
            }
        } catch (Exception e) {
            System.out.println("Gagal simpan ke berkas_digital_perawatan: " + e.getMessage());
        }
    }

    private void bukaDokumenTte(String noRawat, String noSurat, String signedFileName, String berkasTte) {
        try {
            File fLocal = new File("./tmpPDF/" + signedFileName);
            if (fLocal.exists()) {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                    Desktop.getDesktop().open(fLocal);
                    return;
                }
            }

            if (berkasTte != null && !berkasTte.trim().isEmpty()) {
                File fServer = new File(berkasTte);
                if (fServer.exists()) {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                        Desktop.getDesktop().open(fServer);
                        return;
                    }
                }
                File fWeb = new File("./webapps/berkasrawat/" + berkasTte);
                if (fWeb.exists()) {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                        Desktop.getDesktop().open(fWeb);
                        return;
                    }
                }

                String httpUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/berkasrawat/" + berkasTte;
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new java.net.URI(httpUrl));
                    return;
                }
            }

            JOptionPane.showMessageDialog(null, 
                "Dokumen PDF hasil TTE belum tersedia pada penyimpanan lokal maupun server.\n" +
                "Silakan lakukan penandatanganan (TTE) terlebih dahulu.",
                "Informasi", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Gagal membuka dokumen TTE: " + ex.getMessage());
        }
    }

    private void MnBukaTteSuratSehatRohaniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnBukaTteSuratSehatRohaniActionPerformed
        int row = tbObat.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, silakan pilih dulu data surat keterangan sehat rohani di tabel...!!!");
            return;
        }

        String noSurat = tbObat.getValueAt(row, 0).toString();
        String noRawat = tbObat.getValueAt(row, 1).toString();
        String cleanNoRawat = noRawat.replaceAll("[^a-zA-Z0-9]", "_");
        String cleanNoSurat = noSurat.replaceAll("[^a-zA-Z0-9]", "_");
        String signedFileName = "SuratSehatRohani_" + cleanNoRawat + "_" + cleanNoSurat + ".pdf";

        String berkasTte = Sequel.cariIsi("select lokasi_file from berkas_digital_perawatan where no_rawat=? and kode='068'", noRawat);
        bukaDokumenTte(noRawat, noSurat, signedFileName, berkasTte);
    }//GEN-LAST:event_MnBukaTteSuratSehatRohaniActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            SuratKeteranganSehatRohani dialog = new SuratKeteranganSehatRohani(new javax.swing.JFrame(), true);
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
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.ComboBox CmbKesimpulan;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.PanelBiasa FormInput;
    private widget.TextBox Keperluan;
    private widget.Label LCount;
    private javax.swing.JMenuItem MnCetakSuratKeteranganSehatRohani;
    private javax.swing.JMenuItem MnTteSuratSehatRohani;
    private javax.swing.JMenuItem MnBukaTteSuratSehatRohani;
    private widget.TextBox NoSurat;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Tanggal TanggalSurat;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel17;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel28;
    private widget.Label jLabel3;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try{
            tgl=" surat_keterangan_sehat_rohani.tanggalsurat between '"+Valid.SetTgl(DTPCari1.getSelectedItem()+"")+"' and '"+Valid.SetTgl(DTPCari2.getSelectedItem()+"")+"' ";
            if(TCari.getText().trim().equals("")){
                ps=koneksi.prepareStatement(
                     "select surat_keterangan_sehat_rohani.no_surat,surat_keterangan_sehat_rohani.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"+
                     "surat_keterangan_sehat_rohani.tanggalsurat, "+                  
                     "surat_keterangan_sehat_rohani.keperluan,surat_keterangan_sehat_rohani.kesimpulan from surat_keterangan_sehat_rohani left join reg_periksa on surat_keterangan_sehat_rohani.no_rawat=reg_periksa.no_rawat "+
                     "left join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                     "where "+tgl+"order by surat_keterangan_sehat_rohani.no_surat");
            }else{
                ps=koneksi.prepareStatement(
                     "select surat_keterangan_sehat_rohani.no_surat,surat_keterangan_sehat_rohani.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"+
                     "surat_keterangan_sehat_rohani.tanggalsurat, "+                  
                     "surat_keterangan_sehat_rohani.keperluan,surat_keterangan_sehat_rohani.kesimpulan from surat_keterangan_sehat_rohani left join reg_periksa on surat_keterangan_sehat_rohani.no_rawat=reg_periksa.no_rawat "+
                     "left join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                     "where "+tgl+"and no_surat like '%"+TCari.getText().trim()+"%' or "+
                     tgl+"and surat_keterangan_sehat_rohani.no_rawat like '%"+TCari.getText().trim()+"%' or "+
                     tgl+"and reg_periksa.no_rkm_medis like '%"+TCari.getText().trim()+"%' or "+
                     tgl+"and pasien.nm_pasien like '%"+TCari.getText().trim()+"%' or "+
                     tgl+"and surat_keterangan_sehat_rohani.tanggalsurat like '%"+TCari.getText().trim()+"%' "+
                     "order by surat_keterangan_sehat_rohani.no_surat");
            }
                
            try {
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new String[]{
                        rs.getString(1),rs.getString(2),rs.getString(3),
                        rs.getString(4),rs.getString(5),rs.getString(6),
                        rs.getString(7)
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
        LCount.setText(""+tabMode.getRowCount());
    }

    public void emptTeks() {
        TNoRw.setText("");
        TNoRM.setText("");
        TPasien.setText("");
        NoSurat.setText("");
        Keperluan.setText("");
        TanggalSurat.setDate(new Date());
        Keperluan.setText("");
        CmbKesimpulan.setSelectedItem("Sehat");
        Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(surat_keterangan_sehat_rohani.no_surat,3),signed)),0) from surat_keterangan_sehat_rohani where surat_keterangan_sehat_rohani.tanggalsurat='"+Valid.SetTgl(TanggalSurat.getSelectedItem()+"")+"' ",
                "SKSR"+TanggalSurat.getSelectedItem().toString().substring(6,10)+TanggalSurat.getSelectedItem().toString().substring(3,5)+TanggalSurat.getSelectedItem().toString().substring(0,2),3,NoSurat);
        NoSurat.requestFocus();
    }

 
    private void getData() {
        if(tbObat.getSelectedRow()!= -1){
            NoSurat.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            Valid.SetTgl(TanggalSurat,tbObat.getValueAt(tbObat.getSelectedRow(),4).toString());
            Keperluan.setText(tbObat.getValueAt(tbObat.getSelectedRow(),5).toString());
            CmbKesimpulan.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString());           
            
        }
    }

    private void isRawat() {
         Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat='"+TNoRw.getText()+"' ",TNoRM);
    }

    private void isPsien() {
        Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis='"+TNoRM.getText()+"' ",TPasien);
    }
    
    public void setNoRm(String norwt, Date tgl1, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        DTPCari1.setDate(tgl1);
        DTPCari2.setDate(tgl2);
        isRawat();
        isPsien(); 
        ChkInput.setSelected(true);
        isForm();
    }
    private void isForm(){
        if(ChkInput.isSelected()==true){
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH,96));
            FormInput.setVisible(true);      
            ChkInput.setVisible(true);
        }else if(ChkInput.isSelected()==false){           
            ChkInput.setVisible(false);            
            PanelInput.setPreferredSize(new Dimension(WIDTH,20));
            FormInput.setVisible(false);      
            ChkInput.setVisible(true);
        }
    }
       
    
    public void isCek(){
        BtnSimpan.setEnabled(akses.getsurat_keterangan_sehat());
        BtnHapus.setEnabled(akses.getsurat_keterangan_sehat());
        BtnEdit.setEnabled(akses.getsurat_keterangan_sehat());
    }
    
    private void NotifWa() {
        String petugas = Sequel.cariIsi("select nama from pegawai where nik = ?", akses.getkode());
        if (notifwasurat.equals("yes")) {
            koneksiwa = koneksiDBWa.condb();
            String tanggaljamkirim = Valid.SetTgl(DTPCari1.getSelectedItem() + "")
                    + " "
                    + new SimpleDateFormat("HH:mm:ss").format(new Date());
            
            String pesan = "Surat Keterangan Sehat Rohani:"
                    + "\n============================"
                    + "\nNama : " + TPasien.getText() + ""
                    + "\nNo RM : " + TNoRM.getText() + ""
                    + "\nNo Surat : " + NoSurat.getText() + ""
                    + "\nKeperluan : " + Keperluan.getText() + ""
                    + "\nTanggal/Jam : " + tanggaljamkirim + ""
                    + "\nKode Petugas : " + akses.getkode() + ""
                    + "\nPetugas : " + petugas + ""
                    + "\n============================";
            
            try {
                String sql = "INSERT INTO wa_outbox (NOMOR, NOWA, PESAN, TANGGAL_JAM, STATUS, SOURCE, SENDER, SUCCESS, RESPONSE, REQUEST, TYPE, FILE) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                ps = koneksiwa.prepareStatement(sql);
                ps.setLong(1, 0);  // NOMOR, jika AUTO_INCREMENT, bisa diubah atau dihilangkan sesuai kebutuhan
                ps.setString(2, idgroupwasurat);  // NOWA
                ps.setString(3, pesan);          // PESAN
                ps.setString(4, tanggaljamkirim);  // TANGGAL_JAM
                ps.setString(5, "ANTRIAN");                 // STATUS
                ps.setString(6, "KHANZA");                  // SOURCE
                ps.setString(7, "NODEJS");                  // SENDER
                ps.setString(8, "");                        // SUCCESS
                ps.setString(9, "");                        // RESPONSE
                ps.setString(10, "");                       // REQUEST
                ps.setString(11, "TEXT");                   // TYPE
                ps.setString(12, "");                       // FILE

                ps.executeUpdate();
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
        }
    }
}



