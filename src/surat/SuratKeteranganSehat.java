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
import java.awt.Desktop;
import java.io.File;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import bridging.EsignServiceV2;
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
public final class SuratKeteranganSehat extends javax.swing.JDialog {

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
    public SuratKeteranganSehat(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(628, 674);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Surat", "No.Rawat", "No.R.M.", "Nama Pasien", "Tanggal", "BB", "TB",
            "Tensi", "Nadi", "Respirasi", "Suhu", "Buta Warna", "Keperluan", "Kesimpulan","Kode Dokter","Nama Dokter"
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
            } else if (i == 13) {
                column.setPreferredWidth(80);
            }else if (i == 14) {
                column.setPreferredWidth(50);
            }else if (i == 15) {
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
        btnHistoriCetak.setToolTipText("Alt+H — Histori Cetak Surat Sehat");
        btnHistoriCetak.setPreferredSize(new java.awt.Dimension(140, 30));
        btnHistoriCetak.addActionListener((java.awt.event.ActionEvent evt) -> tampilHistoriCetakSuratSehat());
        panelGlass8.add(btnHistoriCetak);

        widget.Button btnAuditEditHapus = new widget.Button();
        btnAuditEditHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/clock.png")));
        btnAuditEditHapus.setMnemonic('A'); btnAuditEditHapus.setText("Audit Edit/Hapus");
        btnAuditEditHapus.setToolTipText("Alt+A — Audit Edit/Hapus Surat Sehat");
        btnAuditEditHapus.setPreferredSize(new java.awt.Dimension(160, 30));
        btnAuditEditHapus.addActionListener((java.awt.event.ActionEvent evt) -> tampilAuditSuratSehat());
        panelGlass8.add(btnAuditEditHapus);

        panelGlass8.revalidate(); panelGlass8.repaint();
    }

    private void tampilHistoriCetakSuratSehat() {
        final javax.swing.JDialog dlg = new javax.swing.JDialog(this, "Histori Pencetakan Surat Sehat", true);
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
            String sql = "select no_surat,no_rawat,jenis_surat,kd_user,tanggal,jam from log_cetak_surat_keterangan_sehat where no_surat like ? or no_rawat like ? or kd_user like ? order by tanggal desc, jam desc limit 500";
            PreparedStatement p = null; ResultSet r = null;
            try {
                p = koneksiDB.condb().prepareStatement(sql);
                p.setString(1, key); p.setString(2, key); p.setString(3, key);
                r = p.executeQuery(); int n = 0;
                while (r.next()) { mdl.addRow(new Object[]{r.getString(1),r.getString(2),r.getString(3),r.getString(4),r.getString(5),r.getString(6)}); n++; }
                lblInfo.setText("  " + n + " baris");
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Gagal: " + ex.getMessage()); }
            finally { try { if (r != null) r.close(); } catch (Exception ignore) {} try { if (p != null) p.close(); } catch (Exception ignore) {} }
        };
        bRefresh.addActionListener(e -> loader.run()); fCari.addActionListener(e -> loader.run());
        javax.swing.JButton bClose = new javax.swing.JButton("Tutup"); bClose.addActionListener(e -> dlg.dispose());
        javax.swing.JPanel bottom = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 5)); bottom.add(bClose);
        dlg.add(top, java.awt.BorderLayout.NORTH); dlg.add(new javax.swing.JScrollPane(tbl), java.awt.BorderLayout.CENTER); dlg.add(bottom, java.awt.BorderLayout.SOUTH);
        loader.run(); dlg.setVisible(true);
    }

    private void tampilAuditSuratSehat() {
        final javax.swing.JDialog dlg = new javax.swing.JDialog(this, "Audit Edit & Hapus Surat Sehat", true);
        dlg.setSize(1200, 500); dlg.setLocationRelativeTo(this); dlg.setLayout(new java.awt.BorderLayout(6, 6));
        final DefaultTableModel mdl = new DefaultTableModel(null, new Object[]{"Aksi","Kode User","Tanggal Aksi","Jam Aksi","No.Surat","No.Rawat","Tgl Surat","BB","TB","Tensi","Nadi","RR","Suhu","Buta Warna","Keperluan","Kesimpulan","Kd Dokter"}) { @Override public boolean isCellEditable(int r, int c){ return false; } };
        final JTable tbl = new JTable(mdl); tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {60,100,100,80,140,120,90,50,50,70,60,50,50,80,150,140,100};
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
            String sql = "select aksi,kd_user,tanggal_aksi,jam_aksi,no_surat,no_rawat,tanggalsurat,berat,tinggi,tensi,nadi,rr,suhu,butawarna,keperluan,kesimpulan,kd_dokter from surat_keterangan_sehat_audit where (no_surat like ? or no_rawat like ? or kd_user like ?) " + (aksiSel.equals("SEMUA") ? "" : "and aksi=? ") + "order by tanggal_aksi desc, jam_aksi desc limit 500";
            PreparedStatement p = null; ResultSet r = null;
            try {
                p = koneksiDB.condb().prepareStatement(sql);
                p.setString(1, key); p.setString(2, key); p.setString(3, key);
                if (!aksiSel.equals("SEMUA")) p.setString(4, aksiSel);
                r = p.executeQuery(); int n = 0;
                while (r.next()) { Object[] row = new Object[17]; for (int i = 0; i < 17; i++) row[i] = r.getString(i + 1); mdl.addRow(row); n++; }
                lblInfo.setText("  " + n + " baris");
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Gagal: " + ex.getMessage()); }
            finally { try { if (r != null) r.close(); } catch (Exception ignore) {} try { if (p != null) p.close(); } catch (Exception ignore) {} }
        };
        bRefresh.addActionListener(e -> loader.run()); fCari.addActionListener(e -> loader.run()); cbAksi.addActionListener(e -> loader.run());
        javax.swing.JButton bClose = new javax.swing.JButton("Tutup"); bClose.addActionListener(e -> dlg.dispose());
        javax.swing.JPanel bottom = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 5)); bottom.add(bClose);
        dlg.add(top, java.awt.BorderLayout.NORTH); dlg.add(new javax.swing.JScrollPane(tbl), java.awt.BorderLayout.CENTER); dlg.add(bottom, java.awt.BorderLayout.SOUTH);
        loader.run(); dlg.setVisible(true);
    }

    private void snapshotSuratSehatAudit(String noSuratLama, String aksi) {
        PreparedStatement pSel = null, pIns = null; ResultSet rSel = null;
        try {
            pSel = koneksiDB.condb().prepareStatement("select no_surat,no_rawat,tanggalsurat,berat,tinggi,tensi,nadi,rr,suhu,butawarna,keperluan,kesimpulan,kd_dokter from surat_keterangan_sehat where no_surat=?");
            pSel.setString(1, noSuratLama); rSel = pSel.executeQuery();
            if (rSel.next()) {
                pIns = koneksiDB.condb().prepareStatement("insert into surat_keterangan_sehat_audit(aksi,kd_user,tanggal_aksi,jam_aksi,no_surat,no_rawat,tanggalsurat,berat,tinggi,tensi,nadi,rr,suhu,butawarna,keperluan,kesimpulan,kd_dokter) values(?,?,current_date(),current_time(),?,?,?,?,?,?,?,?,?,?,?,?,?)");
                pIns.setString(1, aksi); pIns.setString(2, akses.getkode());
                for (int k = 1; k <= 13; k++) pIns.setString(k + 2, rSel.getString(k));
                pIns.executeUpdate();
            }
        } catch (Exception ex) { System.out.println("snapshotSuratSehatAudit gagal: " + ex); }
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
        MnTteSuratSehat = new javax.swing.JMenuItem();
        MnBukaTteSuratSehat = new javax.swing.JMenuItem();
        MnKirimWaSuratSehat = new javax.swing.JMenuItem();
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

        MnTteSuratSehat.setBackground(new java.awt.Color(250, 250, 250));
        MnTteSuratSehat.setFont(new java.awt.Font("Tahoma", 0, 11));
        MnTteSuratSehat.setForeground(new java.awt.Color(50, 50, 50));
        MnTteSuratSehat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        MnTteSuratSehat.setText("Tanda Tangan Elektronik (TTE BSrE)");
        MnTteSuratSehat.setName("MnTteSuratSehat");
        MnTteSuratSehat.setPreferredSize(new java.awt.Dimension(220, 26));
        MnTteSuratSehat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnTteSuratSehatActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnTteSuratSehat);

        MnBukaTteSuratSehat.setBackground(new java.awt.Color(250, 250, 250));
        MnBukaTteSuratSehat.setFont(new java.awt.Font("Tahoma", 0, 11));
        MnBukaTteSuratSehat.setForeground(new java.awt.Color(50, 50, 50));
        MnBukaTteSuratSehat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        MnBukaTteSuratSehat.setText("Buka Dokumen TTE Terverifikasi");
        MnBukaTteSuratSehat.setName("MnBukaTteSuratSehat");
        MnBukaTteSuratSehat.setPreferredSize(new java.awt.Dimension(220, 26));
        MnBukaTteSuratSehat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnBukaTteSuratSehatActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnBukaTteSuratSehat);

        MnKirimWaSuratSehat.setBackground(new java.awt.Color(250, 250, 250));
        MnKirimWaSuratSehat.setFont(new java.awt.Font("Tahoma", 0, 11));
        MnKirimWaSuratSehat.setForeground(new java.awt.Color(50, 50, 50));
        MnKirimWaSuratSehat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        MnKirimWaSuratSehat.setText("Kirim Surat Sehat ke WhatsApp");
        MnKirimWaSuratSehat.setName("MnKirimWaSuratSehat");
        MnKirimWaSuratSehat.setPreferredSize(new java.awt.Dimension(220, 26));
        MnKirimWaSuratSehat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnKirimWaSuratSehatActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnKirimWaSuratSehat);

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
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "05-01-2025" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "05-01-2025" }));
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
        jLabel3.setBounds(180, 70, 70, 23);

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
        NoSurat.setBounds(810, 10, 150, 23);

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
        Keperluan.setBounds(263, 70, 260, 23);

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
        jLabel27.setBounds(0, 70, 83, 23);

        jLabel28.setText("Kesimpulan :");
        jLabel28.setName("jLabel28"); // NOI18N
        FormInput.add(jLabel28);
        jLabel28.setBounds(540, 70, 75, 23);

        CmbButaWarna.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        CmbButaWarna.setName("CmbButaWarna"); // NOI18N
        CmbButaWarna.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CmbButaWarnaKeyPressed(evt);
            }
        });
        FormInput.add(CmbButaWarna);
        CmbButaWarna.setBounds(90, 70, 80, 23);

        CmbKesimpulan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Sehat", "Sehat Jasmani dan Rohani", "Tidak Sehat" }));
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
        CmbKesimpulan.setBounds(620, 70, 80, 23);

        TanggalSurat.setForeground(new java.awt.Color(50, 70, 50));
        TanggalSurat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "05-01-2025" }));
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
        label14.setBounds(10, 100, 70, 23);

        KdDokter.setEditable(false);
        KdDokter.setName("KdDokter"); // NOI18N
        KdDokter.setPreferredSize(new java.awt.Dimension(80, 23));
        KdDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdDokterKeyPressed(evt);
            }
        });
        FormInput.add(KdDokter);
        KdDokter.setBounds(90, 100, 90, 23);

        NmDokter.setEditable(false);
        NmDokter.setName("NmDokter"); // NOI18N
        NmDokter.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(NmDokter);
        NmDokter.setBounds(190, 100, 290, 23);

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
        BtnDokter.setBounds(480, 100, 28, 23);

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

        } else {
            if (Sequel.menyimpantf("surat_keterangan_sehat", "?,?,?,?,?,?,?,?,?,?,?,?,?", "No.Surat", 13, new String[]{
                NoSurat.getText(), TNoRw.getText(), Valid.SetTgl(TanggalSurat.getSelectedItem() + ""), Bb.getText(), Tb.getText(), Tensi.getText(), TNadi.getText(), TRespirasi.getText(), Suhu.getText(), CmbButaWarna.getSelectedItem() + "",
                Keperluan.getText(), CmbKesimpulan.getSelectedItem() + "",KdDokter.getText()
            }) == true) {
                NotifWa();
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
            snapshotSuratSehatAudit(NoSurat.getText(),"HAPUS");
        }
        Valid.hapusTable(tabMode, NoSurat, "surat_keterangan_sehat", "no_surat");
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
        } else {
            if (tbObat.getSelectedRow() != -1) {
                snapshotSuratSehatAudit(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),"EDIT");
                if (Sequel.mengedittf("surat_keterangan_sehat", "no_surat=?", "no_rawat=?,tanggalsurat=?,berat=?,tinggi=?,tensi=?,nadi=?,rr=?,suhu=?,butawarna=?,keperluan=?,kesimpulan=?,kd_dokter=?", 13, new String[]{
                    NoSurat.getText(), TNoRw.getText(), Valid.SetTgl(TanggalSurat.getSelectedItem() + ""), Bb.getText(), Tb.getText(), Tensi.getText(), TNadi.getText(), TRespirasi.getText(), Suhu.getText(), CmbButaWarna.getSelectedItem().toString(),
                    Keperluan.getText(), CmbKesimpulan.getSelectedItem().toString(),KdDokter.getText()
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
            tgl = " surat_keterangan_sehat.tanggalsurat between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' ";
            if (TCari.getText().trim().equals("")) {
                Valid.MyReportqry("rptDataSuratKrpteteranganSehat.jasper", "report", "::[ Data Surat Keterangan Sehat ]::",
                        "select surat_keterangan_sehat.no_surat,surat_keterangan_sehat.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"
                        + "surat_keterangan_sehat.tanggalsurat,surat_keterangan_sehat.berat,surat_keterangan_sehat.tinggi,surat_keterangan_sehat.tensi,surat_keterangan_sehat.nadi,surat_keterangan_sehat.rr,surat_keterangan_sehat.suhu,surat_keterangan_sehat.butawarna, "
                        + "surat_keterangan_sehat.keperluan,surat_keterangan_sehat.kesimpulan from surat_keterangan_sehat inner join reg_periksa on surat_keterangan_sehat.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "where " + tgl + "order by surat_keterangan_sehat.no_surat", param);
            } else {
                Valid.MyReportqry("rptDataSuratKeteranganSehat.jasper", "report", "::[ Data Surat Keterangan Sehat ]::",
                        "select surat_keterangan_sehat.no_surat,surat_keterangan_sehat.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"
                        + "surat_keterangan_sehat.tanggalawal,surat_keterangan_sehat.tanggalakhir "
                        + "from surat_keterangan_sehat inner join reg_periksa on surat_keterangan_sehat.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "where " + tgl + "and no_surat like '%" + TCari.getText().trim() + "%' or "
                        + tgl + "and surat_keterangan_sehat.no_rawat like '%" + TCari.getText().trim() + "%' or "
                        + tgl + "and reg_periksa.no_rkm_medis like '%" + TCari.getText().trim() + "%' or "
                        + tgl + "and pasien.nm_pasien like '%" + TCari.getText().trim() + "%' or "
                        + tgl + "and surat_keterangan_sehat.tanggalakhir like '%" + TCari.getText().trim() + "%' "
                        + "order by surat_keterangan_sehat.no_surat", param);
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
            Sequel.menyimpan("log_cetak_surat_keterangan_sehat","null,?,?,?,?,current_date(),current_time()",4,new String[]{
                NoSurat.getText(),TNoRw.getText(),"Surat Sehat",akses.getkode()
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
            param.put("verifikasi","https://"+koneksiDB.URLVERIFIKASI()+"/verify/?id="+EnkripsiAES.encrypt("{'x': 'skd', 't':'"+NoSurat.getText () +"'}"));
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", KdDokter.getText());
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + NmDokter.getText() + "\nID " + (finger.equals("") ? KdDokter.getText() : finger) + "\n" + Sequel.cariIsi("select DATE_FORMAT(reg_periksa.tgl_registrasi,'%d-%m-%Y') from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText()));
            Valid.MyReportqry("rptSuratKeteranganSehat.jasper", "report", "::[ Surat Keterangan Sehat ]::",
                    " select surat_keterangan_sehat.no_surat,DATE_FORMAT(surat_keterangan_sehat.tanggalsurat,'%d-%m-%Y')as tanggalsurat,surat_keterangan_sehat.berat,surat_keterangan_sehat.tinggi,surat_keterangan_sehat.tensi,surat_keterangan_sehat.nadi,surat_keterangan_sehat.rr,surat_keterangan_sehat.suhu, "
                    + " surat_keterangan_sehat.butawarna,surat_keterangan_sehat.keperluan,surat_keterangan_sehat.kesimpulan,dokter.nm_dokter,pasien.jk,reg_periksa.kd_dokter,"
                    + " pasien.nm_pasien,concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur)as umur,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat"
                    + " from surat_keterangan_sehat inner join reg_periksa inner join pasien inner join dokter inner join kelurahan inner join kecamatan inner join kabupaten"
                    + " on reg_periksa.no_rkm_medis=pasien.no_rkm_medis and reg_periksa.kd_dokter=dokter.kd_dokter and pasien.kd_kel=kelurahan.kd_kel and "
                    + " pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab and reg_periksa.no_rawat=surat_keterangan_sehat.no_rawat "
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
        if (TPasien.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Sequel.menyimpan("log_cetak_surat_keterangan_sehat","null,?,?,?,?,current_date(),current_time()",4,new String[]{
                NoSurat.getText(),TNoRw.getText(),"Surat Sehat (Cetak 1)",akses.getkode()
            });
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
            Valid.MyReportqry("rptSuratKeteranganSehat2.jasper", "report", "::[ Surat Keterangan Sehat ]::",
                    " select surat_keterangan_sehat.no_surat,DATE_FORMAT(surat_keterangan_sehat.tanggalsurat,'%d-%m-%Y')as tanggalsurat,surat_keterangan_sehat.berat,surat_keterangan_sehat.tinggi,surat_keterangan_sehat.tensi,surat_keterangan_sehat.nadi,surat_keterangan_sehat.rr,surat_keterangan_sehat.suhu, "
                    + " surat_keterangan_sehat.butawarna,surat_keterangan_sehat.keperluan,surat_keterangan_sehat.kesimpulan,dokter.nm_dokter,pasien.jk,DATE_FORMAT(pasien.tgl_lahir,'%d-%m-%Y')as tgl_lahir,reg_periksa.kd_dokter, "
                    + " pasien.nm_pasien,concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur)as umur,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat"
                    + " from surat_keterangan_sehat inner join reg_periksa inner join pasien inner join dokter inner join kelurahan inner join kecamatan inner join kabupaten"
                    + " on reg_periksa.no_rkm_medis=pasien.no_rkm_medis and reg_periksa.kd_dokter=dokter.kd_dokter and pasien.kd_kel=kelurahan.kd_kel and "
                    + " pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab and reg_periksa.no_rawat=surat_keterangan_sehat.no_rawat "
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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            SuratKeteranganSehat dialog = new SuratKeteranganSehat(new javax.swing.JFrame(), true);
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
    private widget.Label LCount;
    private javax.swing.JMenuItem MnCetakSuratSehat;
    private javax.swing.JMenuItem MnCetakSuratSehat1;
    private javax.swing.JMenuItem MnTteSuratSehat;
    private javax.swing.JMenuItem MnBukaTteSuratSehat;
    private javax.swing.JMenuItem MnKirimWaSuratSehat;
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
            tgl = " surat_keterangan_sehat.tanggalsurat between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' ";
            if (TCari.getText().trim().equals("")) {
                ps = koneksi.prepareStatement(
                        "select surat_keterangan_sehat.no_surat,surat_keterangan_sehat.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"
                        + "surat_keterangan_sehat.tanggalsurat,surat_keterangan_sehat.berat,surat_keterangan_sehat.tinggi,surat_keterangan_sehat.tensi,surat_keterangan_sehat.nadi,surat_keterangan_sehat.rr,surat_keterangan_sehat.suhu, "
                        + "surat_keterangan_sehat.butawarna,surat_keterangan_sehat.keperluan,surat_keterangan_sehat.kesimpulan,surat_keterangan_sehat.kd_dokter,dokter.nm_dokter from surat_keterangan_sehat inner join reg_periksa on surat_keterangan_sehat.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis inner join dokter on dokter.kd_dokter = surat_keterangan_sehat.kd_dokter "
                        + "where " + tgl + "order by surat_keterangan_sehat.no_surat");
            } else {
                ps = koneksi.prepareStatement(
                        "select surat_keterangan_sehat.no_surat,surat_keterangan_sehat.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"
                        + "surat_keterangan_sehat.tanggalsurat,surat_keterangan_sehat.berat,surat_keterangan_sehat.tinggi,surat_keterangan_sehat.tensi,surat_keterangan_sehat.nadi,surat_keterangan_sehat.rr,surat_keterangan_sehat.suhu, "
                        + "surat_keterangan_sehat.butawarna,surat_keterangan_sehat.keperluan,surat_keterangan_sehat.kesimpulan,surat_keterangan_sehat.kd_dokter,dokter.nm_dokter from surat_keterangan_sehat inner join reg_periksa on surat_keterangan_sehat.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis inner join dokter on dokter.kd_dokter = surat_keterangan_sehat.kd_dokter "
                        + "where " + tgl + "and no_surat like '%" + TCari.getText().trim() + "%' or "
                        + tgl + "and surat_keterangan_sehat.no_rawat like '%" + TCari.getText().trim() + "%' or "
                        + tgl + "and reg_periksa.no_rkm_medis like '%" + TCari.getText().trim() + "%' or "
                        + tgl + "and pasien.nm_pasien like '%" + TCari.getText().trim() + "%' or "
                        + tgl + "and surat_keterangan_sehat.tanggalsurat like '%" + TCari.getText().trim() + "%' "
                        + "order by surat_keterangan_sehat.no_surat");
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
                        rs.getString(16)});
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
        TanggalSurat.setDate(new Date());
        CmbButaWarna.setSelectedItem("Tidak");
        CmbKesimpulan.setSelectedItem("Sehat");
        Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(surat_keterangan_sehat.no_surat,3),signed)),0) from surat_keterangan_sehat where surat_keterangan_sehat.tanggalsurat='" + Valid.SetTgl(TanggalSurat.getSelectedItem() + "") + "' ",
                "SKD" + TanggalSurat.getSelectedItem().toString().substring(6, 10) + TanggalSurat.getSelectedItem().toString().substring(3, 5) + TanggalSurat.getSelectedItem().toString().substring(0, 2), 3, NoSurat);

        
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
            CmbKesimpulan.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 13).toString());
            KdDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 14).toString());
            NmDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 15).toString());
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

    private void MnTteSuratSehatActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tbObat.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, silakan pilih dulu data surat keterangan sehat di tabel...!!!");
            return;
        }

        String noSurat = tbObat.getValueAt(row, 0).toString();
        String noRawat = tbObat.getValueAt(row, 1).toString();
        String noRM = tbObat.getValueAt(row, 2).toString();
        String namaPasien = tbObat.getValueAt(row, 3).toString();
        String kdDokter = tbObat.getValueAt(row, 14).toString();
        String nmDokter = tbObat.getValueAt(row, 15).toString();

        if (kdDokter == null || kdDokter.trim().isEmpty()) {
            kdDokter = Sequel.cariIsi("select surat_keterangan_sehat.kd_dokter from surat_keterangan_sehat where surat_keterangan_sehat.no_surat=?", noSurat);
        }
        if (nmDokter == null || nmDokter.trim().isEmpty()) {
            nmDokter = Sequel.cariIsi("select dokter.nm_dokter from dokter where dokter.kd_dokter=?", kdDokter);
        }

        String checkFile = "SuratSehat_" + noRawat.replaceAll("[^a-zA-Z0-9]", "_") + "_" + noSurat.replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";
        int hitungBerkas = Sequel.cariInteger("select count(*) from berkas_digital_perawatan where no_rawat='" + noRawat + "' and kode='065' and (lokasi_file like '%" + noSurat.replaceAll("[^a-zA-Z0-9]", "_") + "%' or lokasi_file like '%" + noRawat.replaceAll("[^a-zA-Z0-9]", "_") + "%')");
        File fLocalCheck = new File("./tmpPDF/" + checkFile);

        if (hitungBerkas > 0 || fLocalCheck.exists()) {
            int optSudah = JOptionPane.showConfirmDialog(null, 
                "Surat Keterangan Sehat ini SUDAH PERNAH ditandatangani secara elektronik (BSrE)!\n" +
                "No. Surat : " + noSurat + "\n" +
                "Pasien    : " + namaPasien + " (RM: " + noRM + ")\n\n" +
                "Pilihan:\n" +
                "- Klik YES untuk membuka/melihat dokumen PDF yang sudah bertanda tangan.\n" +
                "- Klik NO untuk menandatangani ulang dokumen (Timpa tanda tangan lama).\n" +
                "- Klik CANCEL untuk membatalkan.", 
                "Dokumen Sudah Bertanda Tangan", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (optSudah == JOptionPane.YES_OPTION) {
                bukaDokumenTte(noRawat, noSurat, checkFile, "");
                return;
            } else if (optSudah != JOptionPane.NO_OPTION) {
                return;
            }
        }

        // Validasi Otorisasi Login Dokter DPJP
        String userLogin = akses.getkode();
        if (userLogin == null || !userLogin.equalsIgnoreCase(kdDokter)) {
            if (akses.getadmin()) {
                int optAdmin = JOptionPane.showConfirmDialog(null, 
                    "Peringatan Otorisasi TTE (Mode Administrator):\n\n" +
                    "Dokter DPJP / Pembuat Surat : " + nmDokter + " (" + kdDokter + ")\n" +
                    "Akun Login Saat Ini         : Administrator (" + userLogin + ")\n\n" +
                    "Sesuai regulasi BSrE, TTE wajib dilakukan oleh Dokter yang bersangkutan.\n" +
                    "Apakah Anda ingin melanjutkan penandatanganan ini untuk pengujian?", 
                    "Validasi Otorisasi Login Dokter", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (optAdmin != JOptionPane.YES_OPTION) {
                    return;
                }
            } else {
                String nmUserLogin = Sequel.cariIsi("select pegawai.nama from pegawai where pegawai.nik=?", userLogin);
                JOptionPane.showMessageDialog(null, 
                    "Akses Ditolak!\n" +
                    "Tanda Tangan Elektronik (TTE) hanya dapat dilakukan oleh Dokter yang bersangkutan.\n\n" +
                    "Dokter DPJP / Pembuat Surat : " + nmDokter + " (" + kdDokter + ")\n" +
                    "Akun Login Saat Ini         : " + (nmUserLogin.isEmpty() ? userLogin : nmUserLogin) + " (" + userLogin + ")\n\n" +
                    "Silakan login menggunakan akun dokter yang bersangkutan untuk melakukan TTE dokumen ini.", 
                    "Validasi Otorisasi TTE BSrE", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String nikDokter = Sequel.cariIsi("select pegawai.no_ktp from pegawai where pegawai.nik=?", kdDokter);
        if (nikDokter == null || nikDokter.trim().isEmpty()) {
            nikDokter = Sequel.cariIsi("select pegawai.no_ktp from pegawai inner join dokter on pegawai.nik=dokter.kd_dokter where dokter.kd_dokter=?", kdDokter);
        }

        if (nikDokter == null || nikDokter.trim().isEmpty()) {
            nikDokter = JOptionPane.showInputDialog(null, 
                "NIK Penandatangan belum terdaftar di data Kepegawaian untuk dokter:\n" + nmDokter + "\nSilakan masukkan NIK BSrE dokter:", 
                "Input NIK BSrE", JOptionPane.QUESTION_MESSAGE);
            if (nikDokter == null || nikDokter.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "TTE dibatalkan karena NIK dokter tidak diisi.");
                return;
            }
        }

        String maskedNik = nikDokter;
        if (nikDokter != null && nikDokter.length() >= 12) {
            maskedNik = nikDokter.substring(0, 4) + "********" + nikDokter.substring(nikDokter.length() - 4);
        } else if (nikDokter != null && nikDokter.length() > 6) {
            maskedNik = nikDokter.substring(0, 2) + "****" + nikDokter.substring(nikDokter.length() - 2);
        }

        javax.swing.JPasswordField txtPass = new javax.swing.JPasswordField(20);
        txtPass.putClientProperty("JPasswordField.cutCopyAllowed", false);
        javax.swing.JPanel pnlTte = new javax.swing.JPanel(new java.awt.GridLayout(0, 1, 3, 3));
        pnlTte.add(new javax.swing.JLabel("Tanda Tangan Elektronik (TTE) BSrE - Surat Keterangan Sehat"));
        pnlTte.add(new javax.swing.JLabel("Pasien : " + namaPasien + " (RM: " + noRM + ")"));
        pnlTte.add(new javax.swing.JLabel("No. Rawat : " + noRawat + " | No. Surat: " + noSurat));
        pnlTte.add(new javax.swing.JLabel("Penandatangan : " + nmDokter + " (NIK: " + maskedNik + ")"));
        pnlTte.add(new javax.swing.JLabel("Masukkan Passphrase BSrE:"));
        pnlTte.add(txtPass);

        int opt = JOptionPane.showConfirmDialog(null, pnlTte, "Otentikasi Passphrase TTE BSrE", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opt != JOptionPane.OK_OPTION) {
            return;
        }

        String passphrase = new String(txtPass.getPassword());
        if (passphrase.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Passphrase tidak boleh kosong...!");
            return;
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
            param.put("sip", Sequel.cariIsi("select no_ijn_praktek from dokter where kd_dokter = '" + kdDokter + "'"));
            param.put("nip", kdDokter);
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            param.put("logo2", Sequel.cariGambar("select setting.logo2 from setting"));
            param.put("logobsre", Sequel.cariGambar("select gambar.bsre from gambar"));
            String urlVerifikasiTTE = "https://" + koneksiDB.URLVERIFIKASITTE() + "/verify/?id=" + EnkripsiAES.encrypt("{'x': 'sehat', 't':'" + noSurat + "'}");
            param.put("finger", "#");

            String sqlQuery = "select surat_keterangan_sehat.no_surat,DATE_FORMAT(surat_keterangan_sehat.tanggalsurat,'%d-%m-%Y')as tanggalsurat,surat_keterangan_sehat.berat,surat_keterangan_sehat.tinggi,surat_keterangan_sehat.tensi,surat_keterangan_sehat.nadi,surat_keterangan_sehat.rr,surat_keterangan_sehat.suhu,"
                    + " surat_keterangan_sehat.butawarna,surat_keterangan_sehat.keperluan,surat_keterangan_sehat.kesimpulan,dokter.nm_dokter,pasien.jk,reg_periksa.kd_dokter,"
                    + " pasien.nm_pasien,concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur)as umur,"
                    + " IF(kelurahan.nm_kel IS NOT NULL, concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab), pasien.alamat) as alamat"
                    + " from surat_keterangan_sehat "
                    + " inner join reg_periksa on reg_periksa.no_rawat=surat_keterangan_sehat.no_rawat"
                    + " inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis"
                    + " inner join dokter on surat_keterangan_sehat.kd_dokter=dokter.kd_dokter"
                    + " left join kelurahan on pasien.kd_kel=kelurahan.kd_kel"
                    + " left join kecamatan on pasien.kd_kec=kecamatan.kd_kec"
                    + " left join kabupaten on pasien.kd_kab=kabupaten.kd_kab"
                    + " where surat_keterangan_sehat.no_surat='" + noSurat + "'";

            File dirTmp = new File("./tmpPDF");
            if (!dirTmp.exists()) {
                dirTmp.mkdirs();
            }

            String cleanNoRawat = noRawat.replaceAll("[^a-zA-Z0-9]", "_");
            String cleanNoSurat = noSurat.replaceAll("[^a-zA-Z0-9]", "_");
            String rawPdfPath = "./tmpPDF/SuratSehat_raw_" + cleanNoRawat + ".pdf";
            String signedFileName = "SuratSehat_" + cleanNoRawat + "_" + cleanNoSurat + ".pdf";
            String signedPdfPath = "./tmpPDF/" + signedFileName;

            try (PreparedStatement pst = koneksi.prepareStatement(sqlQuery);
                 ResultSet rst = pst.executeQuery()) {
                JRResultSetDataSource ds = new JRResultSetDataSource(rst);
                String jasperFile = "./report/rptSuratKeteranganSehatTTE.jasper";
                if (!new File(jasperFile).exists()) {
                    jasperFile = "report/rptSuratKeteranganSehatTTE.jasper";
                }
                if (!new File(jasperFile).exists()) {
                    jasperFile = "./dist/report/rptSuratKeteranganSehatTTE.jasper";
                }
                JasperPrint jp = JasperFillManager.fillReport(jasperFile, param, ds);
                JasperExportManager.exportReportToPdfFile(jp, rawPdfPath);
            }

            File rawFile = new File(rawPdfPath);
            if (!rawFile.exists() || rawFile.length() == 0) {
                JOptionPane.showMessageDialog(null, "Gagal meng-generate dokumen PDF Surat Keterangan Sehat!");
                this.setCursor(Cursor.getDefaultCursor());
                return;
            }

            File signedFile = new File(signedPdfPath);
            if (signedFile.exists()) {
                signedFile.delete();
            }

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
                sp.originX = 410.0f;
                sp.originY = 572.0f;
                sp.width = 85.0f;
                sp.height = 85.0f;
            }

            sp.location = akses.getkabupatenrs();
            sp.reason = "Dokumen Surat Keterangan Sehat telah disetujui dan ditandatangani secara elektronik";
            sp.linkQR = urlVerifikasiTTE;
            sp.imageBase64 = esign.generateQRCodeBase64(sp.linkQR, 400, 400);
            props.add(sp);

            boolean suksesTte = esign.prosesTTE(rawFile, signedFile, nikDokter, passphrase, props);
            if (!suksesTte || !signedFile.exists()) {
                String errMsg = esign.getLastErrorMessage() != null && !esign.getLastErrorMessage().isEmpty() 
                                ? esign.getLastErrorMessage() : "Penandatanganan gagal";
                int errCode = esign.getLastStatusCode();
                EsignServiceV2.catatLog(noRawat, nikDokter, nmDokter, "Gagal TTE Surat Sehat (" + noSurat + "): " + errMsg, String.valueOf(errCode));
                JOptionPane.showMessageDialog(null, 
                    "Penandatanganan Elektronik (TTE) Gagal!\n" +
                    "Status Code: " + errCode + "\n" +
                    "Pesan BSrE: " + errMsg, 
                    "TTE BSrE Gagal", JOptionPane.ERROR_MESSAGE);
                this.setCursor(Cursor.getDefaultCursor());
                return;
            }

            simpanKeBerkasRawat(signedFile, signedFileName, noRawat);
            EsignServiceV2.catatLog(noRawat, nikDokter, nmDokter, "Sukses TTE Surat Sehat (" + noSurat + ") - File: " + signedFileName, "200");

            JOptionPane.showMessageDialog(null, 
                "Surat Keterangan Sehat BERHASIL ditandatangani secara elektronik (BSrE)!\n" +
                "File : " + signedFileName + " (" + signedFile.length() + " bytes)\n" +
                "Penandatangan : " + nmDokter + "\n" +
                "Status : Dokumen resmi tersimpan ke Berkas Digital Perawatan (DlgBerkasRawat).", 
                "TTE BSrE Sukses", JOptionPane.INFORMATION_MESSAGE);

            if (rawFile.exists()) {
                rawFile.delete();
            }
            tampil();
        } catch (Exception e) {
            System.out.println("Error TTE Surat Sehat: " + e.getMessage());
            e.printStackTrace();
            EsignServiceV2.catatLog(noRawat, nikDokter, nmDokter, "Kesalahan Sistem TTE Surat Sehat (" + noSurat + "): " + e.getMessage(), "EXC");
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan sistem saat TTE: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            this.setCursor(Cursor.getDefaultCursor());
        }
    }

    private void MnBukaTteSuratSehatActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tbObat.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, silakan pilih dulu data surat sehat di tabel...!!!");
            return;
        }

        String noSurat = tbObat.getValueAt(row, 0).toString();
        String noRawat = tbObat.getValueAt(row, 1).toString();
        String cleanNoRawat = noRawat.replaceAll("[^a-zA-Z0-9]", "_");
        String cleanNoSurat = noSurat.replaceAll("[^a-zA-Z0-9]", "_");
        String signedFileName = "SuratSehat_" + cleanNoRawat + "_" + cleanNoSurat + ".pdf";
        String berkasTte = Sequel.cariIsi(
            "select lokasi_file from berkas_digital_perawatan where no_rawat='" + noRawat + "' and kode='065' and (lokasi_file like '%" + cleanNoSurat + "%' or lokasi_file like '%" + cleanNoRawat + "%') limit 1"
        );
        if (berkasTte.isEmpty()) {
            berkasTte = Sequel.cariIsi(
                "select lokasi_file from berkas_digital_perawatan where no_rawat='" + noRawat + "' and kode='065' limit 1"
            );
        }
        bukaDokumenTte(noRawat, noSurat, signedFileName, berkasTte);
    }

    private void MnKirimWaSuratSehatActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tbObat.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, silakan pilih dulu data surat sehat di tabel...!!!");
            return;
        }

        String noSurat = tbObat.getValueAt(row, 0).toString();
        String noRawat = tbObat.getValueAt(row, 1).toString();
        String noRM = tbObat.getValueAt(row, 2).toString();
        String namaPasien = tbObat.getValueAt(row, 3).toString();

        String cleanNoRawat = noRawat.replaceAll("[^a-zA-Z0-9]", "_");
        String cleanNoSurat = noSurat.replaceAll("[^a-zA-Z0-9]", "_");
        String signedFileName = "SuratSehat_" + cleanNoRawat + "_" + cleanNoSurat + ".pdf";
        File fLocal = new File("./tmpPDF/" + signedFileName);

        if (!fLocal.exists()) {
            JOptionPane.showMessageDialog(null, 
                "Dokumen PDF TTE Surat Keterangan Sehat belum tersedia di sistem.\n" +
                "Silakan lakukan Tanda Tangan Elektronik (TTE BSrE) terlebih dahulu sebelum mengirim via WhatsApp.", 
                "File Belum Tersedia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String noHp = Sequel.cariIsi("select no_tlp from pasien where no_rkm_medis='" + noRM + "'");
        String tglLahir = Sequel.cariIsi("select DATE_FORMAT(tgl_lahir,'%d-%m-%Y') from pasien where no_rkm_medis='" + noRM + "'");

        kirimPdfKeWa(fLocal, noRM, namaPasien, noHp, tglLahir, "Surat Keterangan Sehat", signedFileName);
    }

    private void simpanKeBerkasRawat(File signedFile, String fileName, String noRawat) {
        try {
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

            // Pastikan master_berkas_digital memiliki kode 065 untuk Surat Sehat
            Sequel.queryu("INSERT IGNORE INTO master_berkas_digital (kode, nama) VALUES ('065', 'SURAT KETERANGAN DOKTER / SEHAT')");

            String lokasiFile = "pages/upload/" + fileName;
            if (Sequel.cariInteger("SELECT COUNT(*) FROM berkas_digital_perawatan WHERE no_rawat=? AND kode=?", noRawat, "065") > 0) {
                Sequel.mengedittf("berkas_digital_perawatan", "no_rawat=? AND kode=?", "lokasi_file=?, nip=?", 4,
                    new String[]{lokasiFile, akses.getkode(), noRawat, "065"});
            } else {
                Sequel.menyimpantf("berkas_digital_perawatan", "?,?,?,?", "Surat Sehat TTE", 4,
                    new String[]{noRawat, "065", lokasiFile, akses.getkode()});
            }
        } catch (Exception e) {
            System.out.println("Error simpanKeBerkasRawat: " + e.getMessage());
        }
    }

    private void bukaDokumenTte(String noRawat, String noSurat, String signedFileName, String berkasTte) {
        File fLocal = new File("./tmpPDF/" + signedFileName);
        if (fLocal.exists()) {
            try {
                Desktop.getDesktop().open(fLocal);
                return;
            } catch (Exception e) {
                System.out.println("Gagal membuka PDF lokal: " + e.getMessage());
            }
        }

        if (berkasTte != null && !berkasTte.trim().isEmpty()) {
            try {
                String urlWeb = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/berkasrawat/" + berkasTte;
                Desktop.getDesktop().browse(new java.net.URI(urlWeb));
                return;
            } catch (Exception e) {
                System.out.println("Gagal membuka browser berkas rawat: " + e.getMessage());
            }
        }

        JOptionPane.showMessageDialog(null, 
            "Dokumen PDF TTE belum ditemukan di penyimpanan lokal maupun berkas digital.\n" +
            "Silakan lakukan penandatanganan elektronik (TTE) terlebih dahulu.", 
            "Dokumen Belum Ada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void kirimPdfKeWa(File pdfAsli, String noRM, String namaPasien, String noHp, String tglLahir, String judulDok, String fileName) {
        JTextField fieldNoHp = new JTextField(noHp, 20);
        JCheckBox checkUpdate = new JCheckBox("Update No. Telp di Data Pasien");
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(0, 1));
        panel.add(new javax.swing.JLabel("Kirim " + judulDok + " ke WhatsApp pasien:"));
        panel.add(new javax.swing.JLabel("Pasien : " + namaPasien));
        panel.add(new javax.swing.JLabel("Nomor WhatsApp :"));
        panel.add(fieldNoHp);
        panel.add(checkUpdate);
        int result = JOptionPane.showConfirmDialog(null, panel, "Konfirmasi Pengiriman WhatsApp", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }
        noHp = fieldNoHp.getText().trim();
        if (noHp.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nomor HP tidak boleh kosong!");
            return;
        }
        if (checkUpdate.isSelected()) {
            Sequel.mengedit("pasien", "no_rkm_medis='" + noRM + "'", "no_tlp='" + noHp + "'");
        }
        if (noHp.startsWith("0")) {
            noHp = "62" + noHp.substring(1);
        } else if (!noHp.startsWith("62")) {
            noHp = "62" + noHp;
        }
        if (!noHp.endsWith("@s.whatsapp.net")) {
            noHp = noHp + "@s.whatsapp.net";
        }
        String password = "";
        if (tglLahir != null && !tglLahir.isEmpty()) {
            try {
                String[] parts = tglLahir.split("-");
                if (parts.length == 3) {
                    password = parts[2] + parts[1] + parts[0];
                }
            } catch (Exception e) {
                System.out.println("Error format tanggal lahir: " + e.getMessage());
            }
        }
        if (password.isEmpty()) {
            password = "12345678";
        }
        try {
            String encPath = encryptPDF(pdfAsli.getAbsolutePath(), password);
            File encFile = new File(encPath);
            String waUrl = koneksiDB.URLWAGATEWAY() + "/send/file";
            String deviceId = koneksiDB.GOWADEVICEID_BYMAC();
            if (deviceId == null || deviceId.isEmpty()) {
                deviceId = koneksiDB.GOWADEVICEID();
            }
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost up = new HttpPost(waUrl);
                String authGw = koneksiDB.USERWAGATEWAY() + ":" + koneksiDB.PASSWAGATEWAY();
                byte[] enc = Base64.getEncoder().encode(authGw.getBytes(StandardCharsets.UTF_8));
                up.setHeader("Authorization", "Basic " + new String(enc));
                if (!deviceId.isEmpty()) {
                    up.setHeader("X-Device-Id", deviceId);
                }
                MultipartEntityBuilder b = MultipartEntityBuilder.create();
                b.setCharset(StandardCharsets.UTF_8);
                b.setMode(HttpMultipartMode.EXTENDED);
                b.addTextBody("phone", noHp, ContentType.create("text/plain", StandardCharsets.UTF_8));
                b.addBinaryBody("file", encFile, ContentType.APPLICATION_PDF, fileName);
                String caption = "*" + judulDok + "*\n"
                        + "🏥 *" + akses.getnamars() + "*\n\n"
                        + "Yth. Bapak/Ibu/Saudara/i *" + namaPasien + "*,\n\n"
                        + "Terlampir dokumen Anda.\n\n"
                        + "*Keamanan Dokumen:*\n"
                        + "PDF ini *TERPROTEKSI PASSWORD*.\n"
                        + "📌 *Password: Tanggal Lahir (ddmmyyyy)*\n"
                        + "Contoh: lahir 17 Agustus 1945 → *17081945*.\n\n"
                        + "---\n"
                        + "📞 " + akses.getkontakrs() + " | " + akses.getnamars();
                b.addTextBody("caption", caption, ContentType.create("text/plain", StandardCharsets.UTF_8));
                up.setEntity(b.build());
                try (CloseableHttpResponse resp = httpClient.execute(up)) {
                    String rstr = EntityUtils.toString(resp.getEntity());
                    System.out.println("WA Gateway Response: " + rstr);
                    if (resp.getCode() == 200) {
                        JOptionPane.showMessageDialog(null, judulDok + " berhasil dikirim ke WhatsApp pasien (" + namaPasien + ").");
                    } else {
                        JOptionPane.showMessageDialog(null, "Gagal mengirim ke WhatsApp. Kode: " + resp.getCode() + "\n" + rstr);
                    }
                }
            }
            if (encFile.exists() && !encFile.getAbsolutePath().equals(pdfAsli.getAbsolutePath())) {
                encFile.delete();
            }
        } catch (Exception e) {
            System.out.println("Error Kirim WA: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat mengirim WA: " + e.getMessage());
        }
    }

    private String encryptPDF(String inputPath, String password) {
        String outputPath = inputPath.replace(".pdf", "_encrypted.pdf");
        try {
            PdfReader reader = new PdfReader(inputPath);
            PdfStamper stamper = new PdfStamper(reader, new java.io.FileOutputStream(outputPath));
            stamper.setEncryption(password.getBytes(), password.getBytes(),
                    PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY, PdfWriter.ENCRYPTION_AES_128);
            stamper.close();
            reader.close();
            return outputPath;
        } catch (Exception e) {
            System.out.println("Error encrypting PDF: " + e.getMessage());
            return inputPath;
        }
    }

}
