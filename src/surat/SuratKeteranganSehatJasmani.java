/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * kontribusi dari dokter Salim Mulyana
 */
package surat;

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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import kepegawaian.DlgCariDokter;
import AESsecurity.EnkripsiAES;

/**
 *
 * @author salimmulyana
 */
public final class SuratKeteranganSehatJasmani extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private Connection koneksiwa;
    private DlgCariDokter dokter=new DlgCariDokter(null,false);
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private String tgl, finger = "", kodedokter = "", namadokter = "", notifwasurat = "", idgroupwasurat;

    /**
     * Creates new form DlgRujuk
     *
     * @param parent
     * @param modal
     */
    public SuratKeteranganSehatJasmani(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(628, 674);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Surat", "No.Rawat", "No.R.M.", "Nama Pasien", "Tanggal", "BB", "TB",
            "Tensi", "Nadi", "Respirasi", "Suhu", "Buta Warna", "Keperluan", "Keterangan", "Kesimpulan","Kode Dokter","Nama Dokter"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbObat.setModel(tabMode);

        //tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 16; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(105);
            } else if (i == 1) {
                column.setPreferredWidth(105);
            } else if (i == 2) {
                column.setPreferredWidth(70);
            } else if (i == 3) {
                column.setPreferredWidth(170);
            } else if (i == 4) {
                column.setPreferredWidth(65);
            } else if (i == 5) {
                column.setPreferredWidth(35);
            } else if (i == 6) {
                column.setPreferredWidth(35);
            } else if (i == 7) {
                column.setPreferredWidth(50);
            } else if (i == 8) {
                column.setPreferredWidth(35);
            } else if (i == 9) {
                column.setPreferredWidth(35);
            } else if (i == 10) {
                column.setPreferredWidth(35);
            } else if (i == 11) {
                column.setPreferredWidth(70);
            } else if (i == 12) {
                column.setPreferredWidth(150);
            }else if (i == 13) {
                column.setPreferredWidth(200);
            } else if (i == 14) {
                column.setPreferredWidth(80);
            }else if (i == 15) {
                column.setPreferredWidth(50);
            }else if (i == 16) {
                column.setPreferredWidth(150);
                        }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        NoSurat.setDocument(new batasInput((byte) 17).getKata(NoSurat));
        Bb.setDocument(new batasInput((byte) 3).getKata(Bb));
        Tb.setDocument(new batasInput((byte) 3).getKata(Tb));
        Tensi.setDocument(new batasInput((byte) 8).getKata(Tensi));
        Suhu.setDocument(new batasInput((byte) 4).getKata(Suhu));
        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));
        if (koneksiDB.CARICEPAT().equals("aktif")) {
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    if (TCari.getText().length() > 2) {
                        tampil();
                    }
                }
            });
        }
        
        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(dokter.getTable().getSelectedRow()!= -1){
                    KdDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                    NmDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                    KdDokter.requestFocus();
                }
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
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
        btnHistoriCetak.addActionListener((java.awt.event.ActionEvent evt) -> tampilHistoriCetakSuratSehatJasmani());
        panelGlass8.add(btnHistoriCetak);
        widget.Button btnAuditEditHapus = new widget.Button();
        btnAuditEditHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/clock.png")));
        btnAuditEditHapus.setMnemonic('A'); btnAuditEditHapus.setText("Audit Edit/Hapus");
        btnAuditEditHapus.setPreferredSize(new java.awt.Dimension(160, 30));
        btnAuditEditHapus.addActionListener((java.awt.event.ActionEvent evt) -> tampilAuditSuratSehatJasmani());
        panelGlass8.add(btnAuditEditHapus);
        panelGlass8.revalidate(); panelGlass8.repaint();
    }

    private void tampilHistoriCetakSuratSehatJasmani() {
        final javax.swing.JDialog dlg = new javax.swing.JDialog(this, "Histori Pencetakan Surat Sehat Jasmani", true);
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
            String sql = "select no_surat,no_rawat,jenis_surat,kd_user,tanggal,jam from log_cetak_surat_sehat_jasmani where no_surat like ? or no_rawat like ? or kd_user like ? order by tanggal desc, jam desc limit 500";
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

    private void tampilAuditSuratSehatJasmani() {
        final javax.swing.JDialog dlg = new javax.swing.JDialog(this, "Audit Edit & Hapus Surat Sehat Jasmani", true);
        dlg.setSize(1200, 500); dlg.setLocationRelativeTo(this); dlg.setLayout(new java.awt.BorderLayout(6, 6));
        final DefaultTableModel mdl = new DefaultTableModel(null, new Object[]{"Aksi","User","Tgl Aksi","Jam","No.Surat","No.Rawat","Tgl Surat","BB","TB","Tensi","Nadi","RR","Suhu","Buta Warna","Keperluan","Keterangan","Kesimpulan","Kd Dokter"}) { @Override public boolean isCellEditable(int r, int c){ return false; } };
        final JTable tbl = new JTable(mdl); tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {60,90,100,80,140,120,90,50,50,70,60,50,50,120,150,180,140,100};
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
            String sql = "select aksi,kd_user,tanggal_aksi,jam_aksi,no_surat,no_rawat,tanggalsurat,berat,tinggi,tensi,nadi,rr,suhu,butawarna,keperluan,keterangan,kesimpulan,kd_dokter from surat_keterangan_sehat_jasmani_audit where (no_surat like ? or no_rawat like ? or kd_user like ?) " + (aksiSel.equals("SEMUA") ? "" : "and aksi=? ") + "order by tanggal_aksi desc, jam_aksi desc limit 500";
            PreparedStatement p = null; ResultSet r = null;
            try { p = koneksiDB.condb().prepareStatement(sql); p.setString(1, key); p.setString(2, key); p.setString(3, key); if (!aksiSel.equals("SEMUA")) p.setString(4, aksiSel); r = p.executeQuery(); int n = 0; while (r.next()) { Object[] row = new Object[18]; for (int i = 0; i < 18; i++) row[i] = r.getString(i + 1); mdl.addRow(row); n++; } lblInfo.setText("  " + n + " baris");
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Gagal: " + ex.getMessage()); }
            finally { try { if (r != null) r.close(); } catch (Exception ignore) {} try { if (p != null) p.close(); } catch (Exception ignore) {} }
        };
        bRefresh.addActionListener(e -> loader.run()); fCari.addActionListener(e -> loader.run()); cbAksi.addActionListener(e -> loader.run());
        javax.swing.JButton bClose = new javax.swing.JButton("Tutup"); bClose.addActionListener(e -> dlg.dispose());
        javax.swing.JPanel bottom = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 5)); bottom.add(bClose);
        dlg.add(top, java.awt.BorderLayout.NORTH); dlg.add(new javax.swing.JScrollPane(tbl), java.awt.BorderLayout.CENTER); dlg.add(bottom, java.awt.BorderLayout.SOUTH);
        loader.run(); dlg.setVisible(true);
    }

    private void snapshotSuratSehatJasmaniAudit(String noSuratLama, String aksi) {
        PreparedStatement pSel = null, pIns = null; ResultSet rSel = null;
        try {
            pSel = koneksiDB.condb().prepareStatement("select no_surat,no_rawat,tanggalsurat,berat,tinggi,tensi,nadi,rr,suhu,butawarna,keperluan,keterangan,kesimpulan,kd_dokter from surat_keterangan_sehat_jasmani where no_surat=?");
            pSel.setString(1, noSuratLama); rSel = pSel.executeQuery();
            if (rSel.next()) {
                pIns = koneksiDB.condb().prepareStatement("insert into surat_keterangan_sehat_jasmani_audit(aksi,kd_user,tanggal_aksi,jam_aksi,no_surat,no_rawat,tanggalsurat,berat,tinggi,tensi,nadi,rr,suhu,butawarna,keperluan,keterangan,kesimpulan,kd_dokter) values(?,?,current_date(),current_time(),?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                pIns.setString(1, aksi); pIns.setString(2, akses.getkode());
                for (int k = 1; k <= 14; k++) pIns.setString(k + 2, rSel.getString(k));
                pIns.executeUpdate();
            }
        } catch (Exception ex) { System.out.println("snapshotSuratSehatJasmaniAudit gagal: " + ex); }
        finally { try { if (rSel != null) rSel.close(); } catch (Exception ignore) {} try { if (pSel != null) pSel.close(); } catch (Exception ignore) {} try { if (pIns != null) pIns.close(); } catch (Exception ignore) {} }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnCetakSuratSehat = new javax.swing.JMenuItem();
        MnCetakSuratSehat1 = new javax.swing.JMenuItem();
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
        jLabel13 = new widget.Label();
        Bb = new widget.TextBox();
        jLabel14 = new widget.Label();
        jLabel15 = new widget.Label();
        Keperluan = new widget.TextBox();
        jLabel17 = new widget.Label();
        jLabel18 = new widget.Label();
        Tensi = new widget.TextBox();
        jLabel20 = new widget.Label();
        jLabel22 = new widget.Label();
        Suhu = new widget.TextBox();
        jLabel23 = new widget.Label();
        jLabel5 = new widget.Label();
        Tb = new widget.TextBox();
        jLabel27 = new widget.Label();
        jLabel28 = new widget.Label();
        CmbButaWarna = new widget.ComboBox();
        CmbKesimpulan = new widget.ComboBox();
        TanggalSurat = new widget.Tanggal();
        jLabel16 = new widget.Label();
        jLabel24 = new widget.Label();
        TNadi = new widget.TextBox();
        TRespirasi = new widget.TextBox();
        jLabel25 = new widget.Label();
        label14 = new widget.Label();
        KdDokter = new widget.TextBox();
        NmDokter = new widget.TextBox();
        BtnDokter = new widget.Button();
        Keterangan = new widget.TextBox();
        jLabel8 = new widget.Label();
        ChkInput = new widget.CekBox();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnCetakSuratSehat.setBackground(new java.awt.Color(250, 250, 250));
        MnCetakSuratSehat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnCetakSuratSehat.setForeground(new java.awt.Color(50, 50, 50));
        MnCetakSuratSehat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnCetakSuratSehat.setText("Cetak Surat Sehat 1");
        MnCetakSuratSehat.setName("MnCetakSuratSehat"); // NOI18N
        MnCetakSuratSehat.setPreferredSize(new java.awt.Dimension(200, 26));
        MnCetakSuratSehat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnCetakSuratSehatActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnCetakSuratSehat);

        MnCetakSuratSehat1.setBackground(new java.awt.Color(250, 250, 250));
        MnCetakSuratSehat1.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnCetakSuratSehat1.setForeground(new java.awt.Color(50, 50, 50));
        MnCetakSuratSehat1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnCetakSuratSehat1.setText("Cetak Surat Sehat 2");
        MnCetakSuratSehat1.setName("MnCetakSuratSehat1"); // NOI18N
        MnCetakSuratSehat1.setPreferredSize(new java.awt.Dimension(200, 26));
        MnCetakSuratSehat1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnCetakSuratSehat1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnCetakSuratSehat1);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Surat Keterangan Sehat ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 200));

        tbObat.setAutoCreateRowSorter(true);
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
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "07-07-2025" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "07-07-2025" }));
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

        PanelInput.setMinimumSize(new java.awt.Dimension(84, 40));
        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 160));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 165));
        FormInput.setLayout(null);

        jLabel3.setText("Keperluan :");
        jLabel3.setName("jLabel3"); // NOI18N
        FormInput.add(jLabel3);
        jLabel3.setBounds(10, 70, 70, 23);

        NoSurat.setHighlighter(null);
        NoSurat.setName("NoSurat"); // NOI18N
        NoSurat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NoSuratActionPerformed(evt);
            }
        });
        NoSurat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoSuratKeyPressed(evt);
            }
        });
        FormInput.add(NoSurat);
        NoSurat.setBounds(810, 10, 400, 23);

        jLabel4.setText("No.Rawat :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 10, 75, 23);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(79, 10, 150, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        FormInput.add(TPasien);
        TPasien.setBounds(335, 10, 230, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        FormInput.add(TNoRM);
        TNoRM.setBounds(232, 10, 100, 23);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("Kg");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(140, 40, 20, 23);

        Bb.setName("Bb"); // NOI18N
        Bb.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BbKeyPressed(evt);
            }
        });
        FormInput.add(Bb);
        Bb.setBounds(80, 40, 50, 23);

        jLabel14.setText("Berat Badan :");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(0, 40, 80, 23);

        jLabel15.setText("Tinggi Badan :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(160, 40, 80, 23);

        Keperluan.setName("Keperluan"); // NOI18N
        Keperluan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeperluanKeyPressed(evt);
            }
        });
        FormInput.add(Keperluan);
        Keperluan.setBounds(90, 70, 400, 23);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Cm");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(300, 40, 20, 23);

        jLabel18.setText("Tensi :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(330, 40, 40, 23);

        Tensi.setName("Tensi"); // NOI18N
        Tensi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TensiKeyPressed(evt);
            }
        });
        FormInput.add(Tensi);
        Tensi.setBounds(380, 40, 70, 23);

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("mmHg");
        jLabel20.setName("jLabel20"); // NOI18N
        FormInput.add(jLabel20);
        jLabel20.setBounds(450, 40, 40, 23);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setText("°C");
        jLabel22.setName("jLabel22"); // NOI18N
        FormInput.add(jLabel22);
        jLabel22.setBounds(890, 40, 30, 23);

        Suhu.setName("Suhu"); // NOI18N
        Suhu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SuhuKeyPressed(evt);
            }
        });
        FormInput.add(Suhu);
        Suhu.setBounds(850, 40, 40, 23);

        jLabel23.setText("Suhu :");
        jLabel23.setName("jLabel23"); // NOI18N
        FormInput.add(jLabel23);
        jLabel23.setBounds(790, 40, 50, 23);

        jLabel5.setText("No. Surat :");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(730, 10, 75, 23);

        Tb.setName("Tb"); // NOI18N
        Tb.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TbKeyPressed(evt);
            }
        });
        FormInput.add(Tb);
        Tb.setBounds(240, 40, 55, 23);

        jLabel27.setText("Buta Warna :");
        jLabel27.setName("jLabel27"); // NOI18N
        FormInput.add(jLabel27);
        jLabel27.setBounds(930, 40, 83, 23);

        jLabel28.setText("Kesimpulan :");
        jLabel28.setName("jLabel28"); // NOI18N
        FormInput.add(jLabel28);
        jLabel28.setBounds(490, 70, 75, 23);

        CmbButaWarna.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak Buta Warna Parsial/Total", "Buta Warna Parsial", "Buta Warna Total" }));
        CmbButaWarna.setName("CmbButaWarna"); // NOI18N
        CmbButaWarna.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmbButaWarnaActionPerformed(evt);
            }
        });
        CmbButaWarna.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CmbButaWarnaKeyPressed(evt);
            }
        });
        FormInput.add(CmbButaWarna);
        CmbButaWarna.setBounds(1020, 40, 190, 23);

        CmbKesimpulan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sehat Jasmani", "Tidak Sehat Jasmani" }));
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
        CmbKesimpulan.setBounds(570, 70, 150, 23);

        TanggalSurat.setForeground(new java.awt.Color(50, 70, 50));
        TanggalSurat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "07-07-2025" }));
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
        TanggalSurat.setBounds(630, 10, 90, 23);

        jLabel16.setText("Tanggal :");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(566, 10, 60, 23);

        jLabel24.setText("Nadi (/menit) :");
        jLabel24.setName("jLabel24"); // NOI18N
        FormInput.add(jLabel24);
        jLabel24.setBounds(490, 40, 79, 23);

        TNadi.setFocusTraversalPolicyProvider(true);
        TNadi.setName("TNadi"); // NOI18N
        TNadi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TNadiActionPerformed(evt);
            }
        });
        TNadi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNadiKeyPressed(evt);
            }
        });
        FormInput.add(TNadi);
        TNadi.setBounds(570, 40, 55, 23);

        TRespirasi.setHighlighter(null);
        TRespirasi.setName("TRespirasi"); // NOI18N
        TRespirasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TRespirasiKeyPressed(evt);
            }
        });
        FormInput.add(TRespirasi);
        TRespirasi.setBounds(720, 40, 55, 23);

        jLabel25.setText("RR (/menit) :");
        jLabel25.setName("jLabel25"); // NOI18N
        FormInput.add(jLabel25);
        jLabel25.setBounds(630, 40, 90, 23);

        label14.setText("Dokter :");
        label14.setName("label14"); // NOI18N
        label14.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label14);
        label14.setBounds(710, 70, 70, 23);

        KdDokter.setEditable(false);
        KdDokter.setName("KdDokter"); // NOI18N
        KdDokter.setPreferredSize(new java.awt.Dimension(80, 23));
        KdDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdDokterKeyPressed(evt);
            }
        });
        FormInput.add(KdDokter);
        KdDokter.setBounds(790, 70, 90, 23);

        NmDokter.setEditable(false);
        NmDokter.setName("NmDokter"); // NOI18N
        NmDokter.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(NmDokter);
        NmDokter.setBounds(890, 70, 290, 23);

        BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokter.setMnemonic('2');
        BtnDokter.setToolTipText("Alt+2");
        BtnDokter.setName("BtnDokter"); // NOI18N
        BtnDokter.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokterActionPerformed(evt);
            }
        });
        BtnDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnDokterKeyPressed(evt);
            }
        });
        FormInput.add(BtnDokter);
        BtnDokter.setBounds(1180, 70, 28, 23);

        Keterangan.setName("Keterangan"); // NOI18N
        Keterangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KeteranganActionPerformed(evt);
            }
        });
        Keterangan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeteranganKeyPressed(evt);
            }
        });
        FormInput.add(Keterangan);
        Keterangan.setBounds(90, 100, 1120, 30);

        jLabel8.setText("Keterangan :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(10, 100, 70, 23);

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
        internalFrame1.getAccessibleContext().setAccessibleName("::[ Data Surat Keterangan Sehat Jasmani ]::");
        internalFrame1.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void NoSuratKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoSuratKeyPressed
        Valid.pindah(evt, TanggalSurat, Bb);
}//GEN-LAST:event_NoSuratKeyPressed

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            isRawat();
            isPsien();
        } else {
            Valid.pindah(evt, TCari, NoSurat);
        }
}//GEN-LAST:event_TNoRwKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (NoSurat.getText().trim().equals("")) {
            Valid.textKosong(NoSurat, "No.Surat");
        } else if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "pasien");
        } else if (Bb.getText().trim().equals("")) {
            Valid.textKosong(Bb, "Berat Badan");
        } else if (Tb.getText().trim().equals("")) {
            Valid.textKosong(Tb, "Tinggi Badan");
        } else if (Tensi.getText().trim().equals("")) {
            Valid.textKosong(Tensi, "Tensi");
        } else if (TNadi.getText().trim().equals("")) {
            Valid.textKosong(TNadi, "Nadi");
        } else if (TRespirasi.getText().trim().equals("")) {
            Valid.textKosong(TRespirasi, "Respirasi");
        } else if (Suhu.getText().trim().equals("")) {
            Valid.textKosong(Suhu, "Suhu");
        } else if (CmbButaWarna.getSelectedItem().equals("")) {
            Valid.textKosong(CmbButaWarna, "Buta Warna");
        } else if (Keperluan.getText().trim().equals("")) {
            Valid.textKosong(Keperluan, "Keperluan");
        } else if (Keterangan.getText().trim().equals("")) {
            Valid.textKosong(Keterangan, "Keterangan");        
        } else {
            if (Sequel.menyimpantf("surat_keterangan_sehat_jasmani", "?,?,?,?,?,?,?,?,?,?,?,?,?,?", "No.Surat", 14, new String[]{
                NoSurat.getText(), TNoRw.getText(), Valid.SetTgl(TanggalSurat.getSelectedItem() + ""), Bb.getText(), Tb.getText(), Tensi.getText(), TNadi.getText(), TRespirasi.getText(), Suhu.getText(), CmbButaWarna.getSelectedItem() + "",
                Keperluan.getText(), Keterangan.getText(), CmbKesimpulan.getSelectedItem() + "",KdDokter.getText()
            }) == true) {
                //NotifWa();
                tampil();
                emptTeks();
            }
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, CmbKesimpulan, BtnBatal);
        }
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
        ChkInput.setSelected(true);
        isForm();

}//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            emptTeks();
        } else {
            Valid.pindah(evt, BtnSimpan, BtnHapus);
        }
}//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if(tabMode.getRowCount()>0 && !NoSurat.getText().trim().equals("")){
            snapshotSuratSehatJasmaniAudit(NoSurat.getText(),"HAPUS");
        }
        Valid.hapusTable(tabMode, NoSurat, "surat_keterangan_sehat_jasmani", "no_surat");
        tampil();
        emptTeks();
}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnHapusActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if (NoSurat.getText().trim().equals("")) {
            Valid.textKosong(NoSurat, "No.Surat Sakit");
        } else if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "pasien");
        } else if (Bb.getText().trim().equals("")) {
            Valid.textKosong(Bb, "Berat Badan");
        } else if (Tb.getText().trim().equals("")) {
            Valid.textKosong(Tb, "Tinggi Badan");
        } else if (Tensi.getText().trim().equals("")) {
            Valid.textKosong(Tensi, "Tensi");
        } else if (TNadi.getText().trim().equals("")) {
            Valid.textKosong(TNadi, "Nadi");
        } else if (TRespirasi.getText().trim().equals("")) {
            Valid.textKosong(TRespirasi, "Respirasi");
        } else if (Suhu.getText().trim().equals("")) {
            Valid.textKosong(Suhu, "Suhu");
        } else if (CmbButaWarna.getSelectedItem().equals("")) {
            Valid.textKosong(CmbButaWarna, "Buta Warna");
        } else if (Keperluan.getText().trim().equals("")) {
            Valid.textKosong(Keperluan, "Keperluan");
        } else if (Keterangan.getText().trim().equals("")) {
            Valid.textKosong(Keterangan, "Keterangan");
        } else {
            if (tbObat.getSelectedRow() != -1) {
                snapshotSuratSehatJasmaniAudit(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),"EDIT");
                if (Sequel.mengedittf("surat_keterangan_sehat_jasmani", "no_surat=?", "no_rawat=?,tanggalsurat=?,berat=?,tinggi=?,tensi=?,nadi=?,rr=?,suhu=?,butawarna=?,keperluan=?,kesimpulan=?,kd_dokter=?,keterangan=?", 14, new String[]{
                    NoSurat.getText(), TNoRw.getText(), Valid.SetTgl(TanggalSurat.getSelectedItem() + ""), Bb.getText(), Tb.getText(), Tensi.getText(), TNadi.getText(), TRespirasi.getText(), Suhu.getText(), CmbButaWarna.getSelectedItem().toString(),
                    Keperluan.getText(), Keterangan.getText(), CmbKesimpulan.getSelectedItem().toString(),KdDokter.getText()
                }) == true) {
                    tampil();
                    emptTeks();
                }
            }
        }
}//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnEditActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
}//GEN-LAST:event_BtnEditKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            dispose();
        } else {
            Valid.pindah(evt, BtnEdit, TCari);
        }
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        } else if (tabMode.getRowCount() != 0) {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            tgl = " surat_keterangan_sehat_jasmani.tanggalsurat between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' ";
            if (TCari.getText().trim().equals("")) {
                Valid.MyReportqry("rptDataSuratKrpteteranganSehat.jasper", "report", "::[ Data Surat Keterangan Sehat ]::",
                        "select surat_keterangan_sehat_jasmani.no_surat,surat_keterangan_sehat_jasmani.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"
                        + "surat_keterangan_sehat_jasmani.tanggalsurat,surat_keterangan_sehat_jasmani.berat,surat_keterangan_sehat_jasmani.tinggi,surat_keterangan_sehat_jasmani.tensi,surat_keterangan_sehat_jasmani.nadi,surat_keterangan_sehat_jasmani.rr,surat_keterangan_sehat_jasmani.suhu,surat_keterangan_sehat_jasmani.butawarna, "
                        + "surat_keterangan_sehat_jasmani.keperluan,surat_keterangan_sehat_jasmani.keterangan,surat_keterangan_sehat_jasmani.kesimpulan from surat_keterangan_sehat_jasmani inner join reg_periksa on surat_keterangan_sehat_jasmani.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "where " + tgl + "order by surat_keterangan_sehat_jasmani.no_surat", param);
            } else {
                Valid.MyReportqry("rptDataSuratKeteranganSehatJamani.jasper", "report", "::[ Data Surat Keterangan Sehat ]::",
                        "select surat_keterangan_sehat_jasmani.no_surat,surat_keterangan_sehat_jasmani.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"
                        + "surat_keterangan_sehat_jasmani.tanggalawal,surat_keterangan_sehat_jasmani.tanggalakhir "
                        + "from surat_keterangan_sehat_jasmani inner join reg_periksa on surat_keterangan_sehat_jasmani.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "where " + tgl + "and no_surat like '%" + TCari.getText().trim() + "%' or "
                        + tgl + "and surat_keterangan_sehat_jasmani.no_rawat like '%" + TCari.getText().trim() + "%' or "
                        + tgl + "and reg_periksa.no_rkm_medis like '%" + TCari.getText().trim() + "%' or "
                        + tgl + "and pasien.nm_pasien like '%" + TCari.getText().trim() + "%' or "
                        + tgl + "and surat_keterangan_sehat_jasmani.tanggalakhir like '%" + TCari.getText().trim() + "%' "
                        + "order by surat_keterangan_sehat_jasmani.no_surat", param);
            }

        }
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPrintActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnEdit, BtnKeluar);
        }
}//GEN-LAST:event_BtnPrintKeyPressed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnCari.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            BtnKeluar.requestFocus();
        }
}//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
}//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnCariActionPerformed(null);
        } else {
            Valid.pindah(evt, TCari, BtnAll);
        }
}//GEN-LAST:event_BtnCariKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        tampil();
}//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            tampil();
            TCari.setText("");
        } else {
            Valid.pindah(evt, BtnCari, TPasien);
        }
}//GEN-LAST:event_BtnAllKeyPressed


    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if (tabMode.getRowCount() != 0) {
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
        if (tabMode.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }//GEN-LAST:event_tbObatKeyReleased

    private void MnCetakSuratSehatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnCetakSuratSehatActionPerformed
        if (TPasien.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Sequel.menyimpan("log_cetak_surat_sehat_jasmani","null,?,?,?,?,current_date(),current_time()",4,new String[]{
                NoSurat.getText(),TNoRw.getText(),"Surat Sehat Jasmani",akses.getkode()
            });
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            param.put("logo2", Sequel.cariGambar("select setting.logo2 from setting"));
            param.put("dokter",NmDokter.getText());
            param.put("sip",Sequel.cariIsi("select no_ijn_praktek from dokter where kd_dokter = '"+KdDokter.getText()+"'"));
            param.put("nip", KdDokter.getText());
            param.put("verifikasi","https://"+koneksiDB.URLVERIFIKASI()+"/verify/?id="+EnkripsiAES.encrypt("{'x': 'sksj', 't':'"+NoSurat.getText () +"'}"));
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", KdDokter.getText());
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + NmDokter.getText() + "\nID " + (finger.equals("") ? KdDokter.getText() : finger) + "\n" + Sequel.cariIsi("select DATE_FORMAT(reg_periksa.tgl_registrasi,'%d-%m-%Y') from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText()));
            Valid.MyReportqry("rptSuratKeteranganSehatJasmani.jasper", "report", "::[ Surat Keterangan Sehat Jasmani ]::",
                    " select surat_keterangan_sehat_jasmani.no_surat,DATE_FORMAT(surat_keterangan_sehat_jasmani.tanggalsurat,'%d-%m-%Y')as tanggalsurat,surat_keterangan_sehat_jasmani.berat,surat_keterangan_sehat_jasmani.tinggi,surat_keterangan_sehat_jasmani.tensi,surat_keterangan_sehat_jasmani.nadi,surat_keterangan_sehat_jasmani.rr,surat_keterangan_sehat_jasmani.suhu, "
                    + " surat_keterangan_sehat_jasmani.butawarna,surat_keterangan_sehat_jasmani.keperluan,surat_keterangan_sehat_jasmani.keterangan,surat_keterangan_sehat_jasmani.kesimpulan,dokter.nm_dokter,pasien.jk,reg_periksa.kd_dokter,"
                    + " pasien.nm_pasien,concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur)as umur,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat"
                    + " from surat_keterangan_sehat_jasmani inner join reg_periksa inner join pasien inner join dokter inner join kelurahan inner join kecamatan inner join kabupaten"
                    + " on reg_periksa.no_rkm_medis=pasien.no_rkm_medis and reg_periksa.kd_dokter=dokter.kd_dokter and pasien.kd_kel=kelurahan.kd_kel and "
                    + " pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab and reg_periksa.no_rawat=surat_keterangan_sehat_jasmani.no_rawat "
                    + " where reg_periksa.no_rawat='" + TNoRw.getText() + "' ", param);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_MnCetakSuratSehatActionPerformed

    private void TanggalSuratActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TanggalSuratActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TanggalSuratActionPerformed

    private void TanggalSuratKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalSuratKeyPressed
        Valid.pindah(evt, TCari, Keperluan);
    }//GEN-LAST:event_TanggalSuratKeyPressed

    private void MnCetakSuratSehat1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnCetakSuratSehat1ActionPerformed
        Sequel.menyimpan("log_cetak_surat_sehat_jasmani","null,?,?,?,?,current_date(),current_time()",4,new String[]{
            NoSurat.getText(),TNoRw.getText(),"Surat Sehat Jasmani (Cetak 1)",akses.getkode()
        });
        if (TPasien.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            kodedokter = Sequel.cariIsi("select reg_periksa.kd_dokter from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText());
            namadokter = Sequel.cariIsi("select dokter.nm_dokter from dokter where dokter.kd_dokter=?", kodedokter);
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", kodedokter);
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + namadokter + "\nID " + (finger.equals("") ? kodedokter : finger) + "\n" + Sequel.cariIsi("select DATE_FORMAT(reg_periksa.tgl_registrasi,'%d-%m-%Y') from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText()));
            Valid.MyReportqry("rptSuratKeteranganSehatJasmani2.jasper", "report", "::[ Surat Keterangan Sehat Jasmani ]::",
                    " select surat_keterangan_sehat_jasmani.no_surat,DATE_FORMAT(surat_keterangan_sehat_jasmani.tanggalsurat,'%d-%m-%Y')as tanggalsurat,surat_keterangan_sehat_jasmani.berat,surat_keterangan_sehat_jasmani.tinggi,surat_keterangan_sehat_jasmani.tensi,surat_keterangan_sehat_jasmani.nadi,surat_keterangan_sehat_jasmani.rr,surat_keterangan_sehat_jasmani.suhu, "
                    + " surat_keterangan_sehat_jasmani.butawarna,surat_keterangan_sehat_jasmani.keperluan,surat_keterangan_sehat_jasmani.keterangan,surat_keterangan_sehat_jasmani.kesimpulan,dokter.nm_dokter,pasien.jk,DATE_FORMAT(pasien.tgl_lahir,'%d-%m-%Y')as tgl_lahir,reg_periksa.kd_dokter, "
                    + " pasien.nm_pasien,concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur)as umur,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat"
                    + " from surat_keterangan_sehat_jasmani inner join reg_periksa inner join pasien inner join dokter inner join kelurahan inner join kecamatan inner join kabupaten"
                    + " on reg_periksa.no_rkm_medis=pasien.no_rkm_medis and reg_periksa.kd_dokter=dokter.kd_dokter and pasien.kd_kel=kelurahan.kd_kel and "
                    + " pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab and reg_periksa.no_rawat=surat_keterangan_sehat_jasmani.no_rawat "
                    + " where reg_periksa.no_rawat='" + TNoRw.getText() + "' ", param);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_MnCetakSuratSehat1ActionPerformed

    private void NoSuratActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NoSuratActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NoSuratActionPerformed

    private void BbKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BbKeyPressed
        Valid.pindah(evt, NoSurat, Tb);
    }//GEN-LAST:event_BbKeyPressed

    private void TbKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TbKeyPressed
        Valid.pindah(evt, Bb, Tensi);
    }//GEN-LAST:event_TbKeyPressed

    private void TensiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TensiKeyPressed
        Valid.pindah(evt, Tb, Suhu);
    }//GEN-LAST:event_TensiKeyPressed

    private void SuhuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SuhuKeyPressed
        Valid.pindah(evt, Tensi, CmbButaWarna);
    }//GEN-LAST:event_SuhuKeyPressed

    private void CmbButaWarnaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CmbButaWarnaKeyPressed
        Valid.pindah(evt, Suhu, Keperluan);
    }//GEN-LAST:event_CmbButaWarnaKeyPressed

    private void KeperluanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KeperluanKeyPressed
        Valid.pindah(evt, CmbButaWarna, CmbKesimpulan);
    }//GEN-LAST:event_KeperluanKeyPressed

    private void CmbKesimpulanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CmbKesimpulanKeyPressed
        Valid.pindah(evt, Keperluan, BtnSimpan);
    }//GEN-LAST:event_CmbKesimpulanKeyPressed

    private void TNadiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNadiKeyPressed
        Valid.pindah(evt, TRespirasi, TNadi);
    }//GEN-LAST:event_TNadiKeyPressed

    private void TRespirasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TRespirasiKeyPressed
        Valid.pindah(evt, TNadi, BtnSimpan);
    }//GEN-LAST:event_TRespirasiKeyPressed

    private void TNadiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TNadiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TNadiActionPerformed

    private void CmbKesimpulanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CmbKesimpulanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CmbKesimpulanActionPerformed

    private void KdDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdDokterKeyPressed

    }//GEN-LAST:event_KdDokterKeyPressed

    private void BtnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokterActionPerformed
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setAlwaysOnTop(false);
        dokter.setVisible(true);
    }//GEN-LAST:event_BtnDokterActionPerformed

    private void BtnDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnDokterKeyPressed
        //Valid.pindah(evt,Monitoring,BtnSimpan);
    }//GEN-LAST:event_BtnDokterKeyPressed

    private void CmbButaWarnaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CmbButaWarnaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CmbButaWarnaActionPerformed

    private void KeteranganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KeteranganKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_KeteranganKeyPressed

    private void KeteranganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KeteranganActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_KeteranganActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            SuratKeteranganSehatJasmani dialog = new SuratKeteranganSehatJasmani(new javax.swing.JFrame(), true);
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
    private widget.TextBox Bb;
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnDokter;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.ComboBox CmbButaWarna;
    private widget.ComboBox CmbKesimpulan;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.PanelBiasa FormInput;
    private widget.TextBox KdDokter;
    private widget.TextBox Keperluan;
    private widget.TextBox Keterangan;
    private widget.Label LCount;
    private javax.swing.JMenuItem MnCetakSuratSehat;
    private javax.swing.JMenuItem MnCetakSuratSehat1;
    private widget.TextBox NmDokter;
    private widget.TextBox NoSurat;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox Suhu;
    private widget.TextBox TCari;
    private widget.TextBox TNadi;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.TextBox TRespirasi;
    private widget.Tanggal TanggalSurat;
    private widget.TextBox Tb;
    private widget.TextBox Tensi;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel20;
    private widget.Label jLabel21;
    private widget.Label jLabel22;
    private widget.Label jLabel23;
    private widget.Label jLabel24;
    private widget.Label jLabel25;
    private widget.Label jLabel27;
    private widget.Label jLabel28;
    private widget.Label jLabel3;
    private widget.Label jLabel4;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.Label label14;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            tgl = " surat_keterangan_sehat_jasmani.tanggalsurat between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' ";
            if (TCari.getText().trim().equals("")) {
                ps = koneksi.prepareStatement(
                        "select surat_keterangan_sehat_jasmani.no_surat,surat_keterangan_sehat_jasmani.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"
                        + "surat_keterangan_sehat_jasmani.tanggalsurat,surat_keterangan_sehat_jasmani.berat,surat_keterangan_sehat_jasmani.tinggi,surat_keterangan_sehat_jasmani.tensi,surat_keterangan_sehat_jasmani.nadi,surat_keterangan_sehat_jasmani.rr,surat_keterangan_sehat_jasmani.suhu, "
                        + "surat_keterangan_sehat_jasmani.butawarna,surat_keterangan_sehat_jasmani.keperluan,surat_keterangan_sehat_jasmani.keterangan,surat_keterangan_sehat_jasmani.kesimpulan,surat_keterangan_sehat_jasmani.kd_dokter,dokter.nm_dokter from surat_keterangan_sehat_jasmani left join reg_periksa on surat_keterangan_sehat_jasmani.no_rawat=reg_periksa.no_rawat "
                        + "left join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis left join dokter on dokter.kd_dokter = surat_keterangan_sehat_jasmani.kd_dokter "
                        + "where " + tgl + "order by surat_keterangan_sehat_jasmani.no_surat");
            } else {
                ps = koneksi.prepareStatement(
                        "select surat_keterangan_sehat_jasmani.no_surat,surat_keterangan_sehat_jasmani.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"
                        + "surat_keterangan_sehat_jasmani.tanggalsurat,surat_keterangan_sehat_jasmani.berat,surat_keterangan_sehat_jasmani.tinggi,surat_keterangan_sehat_jasmani.tensi,surat_keterangan_sehat_jasmani.nadi,surat_keterangan_sehat_jasmani.rr,surat_keterangan_sehat_jasmani.suhu, "
                        + "surat_keterangan_sehat_jasmani.butawarna,surat_keterangan_sehat_jasmani.keperluan,surat_keterangan_sehat_jasmani.keterangan,surat_keterangan_sehat_jasmani.kesimpulan,surat_keterangan_sehat_jasmani.kd_dokter,dokter.nm_dokter from surat_keterangan_sehat_jasmani left join reg_periksa on surat_keterangan_sehat_jasmani.no_rawat=reg_periksa.no_rawat "
                        + "left join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis left join dokter on dokter.kd_dokter = surat_keterangan_sehat_jasmani.kd_dokter "
                        + "where " + tgl + "and no_surat like '%" + TCari.getText().trim() + "%' or "
                        + tgl + "and surat_keterangan_sehat_jasmani.no_rawat like '%" + TCari.getText().trim() + "%' or "
                        + tgl + "and reg_periksa.no_rkm_medis like '%" + TCari.getText().trim() + "%' or "
                        + tgl + "and pasien.nm_pasien like '%" + TCari.getText().trim() + "%' or "
                        + tgl + "and surat_keterangan_sehat_jasmani.tanggalsurat like '%" + TCari.getText().trim() + "%' "
                        + "order by surat_keterangan_sehat_jasmani.no_surat");
            }

            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6),
                        rs.getString(7), rs.getString(8), rs.getString(9),
                        rs.getString(10), rs.getString(11), rs.getString(12), 
                        rs.getString(13), rs.getString(14),rs.getString(15),
                        rs.getString(16),rs.getString(17)});
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        LCount.setText("" + tabMode.getRowCount());
    }

    public void emptTeks() {
        TNoRw.setText("");
        TNoRM.setText("");
        TPasien.setText("");
        NoSurat.setText("");
        Bb.setText("");
        Tb.setText("");
        Tensi.setText("");
        TNadi.setText("");
        TRespirasi.setText("");
        Suhu.setText("");
        Keperluan.setText("");
        Keterangan.setText("");
        TanggalSurat.setDate(new Date());
        CmbButaWarna.setSelectedItem("Tidak");
        CmbKesimpulan.setSelectedItem("Sehat");
                Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(surat_keterangan_sehat_jasmani.no_surat,3),signed)),0) from surat_keterangan_sehat_jasmani where surat_keterangan_sehat_jasmani.tanggalsurat='" + Valid.SetTgl(TanggalSurat.getSelectedItem() + "") + "' ",
                "SKSJ" + TanggalSurat.getSelectedItem().toString().substring(6, 10) + TanggalSurat.getSelectedItem().toString().substring(3, 5) + TanggalSurat.getSelectedItem().toString().substring(0, 2), 3, NoSurat);

        
        NoSurat.requestFocus();
    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            NoSurat.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
            Valid.SetTgl(TanggalSurat, tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
            Bb.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            Tb.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
            Tensi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString());
            TNadi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString());
            TRespirasi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString());
            Suhu.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString());
            CmbButaWarna.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString());
            Keperluan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString());
            Keterangan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 13).toString());
            CmbKesimpulan.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 14).toString());
            KdDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 15).toString());
            NmDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 16).toString());
        }
    }

    private void isRawat() {
        Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat='" + TNoRw.getText() + "' ", TNoRM);
    }

    private void isPsien() {
        Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis='" + TNoRM.getText() + "' ", TPasien);
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

    private void isForm() {
        if (ChkInput.isSelected() == true) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 160));
            FormInput.setVisible(true);
            ChkInput.setVisible(true);
        } else if (ChkInput.isSelected() == false) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 20));
            FormInput.setVisible(false);
            ChkInput.setVisible(true);
        }
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getsurat_keterangan_sehat());
        BtnHapus.setEnabled(akses.getsurat_keterangan_sehat());
        BtnEdit.setEnabled(akses.getsurat_keterangan_sehat());
        
        if(akses.getjml2()>=1){
            KdDokter.setEditable(false);
            BtnDokter.setEnabled(false);
            KdDokter.setText(akses.getkode());
            NmDokter.setText(dokter.tampil3(KdDokter.getText()));
            if(NmDokter.getText().equals("")){
                KdDokter.setText("");
                JOptionPane.showMessageDialog(null,"User login bukan Dokter...!!");
            }
        }    
    }

    private void NotifWa() {
        String petugas = Sequel.cariIsi("select nama from pegawai where nik = ?", akses.getkode());
        if (notifwasurat.equals("yes")) {
            koneksiwa = koneksiDBWa.condb();
            String tanggaljamkirim = Valid.SetTgl(DTPCari1.getSelectedItem() + "")
                    + " "
                    + new SimpleDateFormat("HH:mm:ss").format(new Date());
            
            String pesan = "Surat Keterangan Sehat:"
                    + "\n============================"
                    + "\nNama : " + TPasien.getText() + ""
                    + "\nNo RM : " + TNoRM.getText() + ""
                    + "\nNo Surat : " + NoSurat.getText() + ""
                    + "\nKeperluan : " + Keperluan.getText() + ""
                    + "\nTanggal/Jam : " + tanggaljamkirim + ""
                    + "\nDokter : " + NmDokter.getText() + ""
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
