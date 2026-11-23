/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * kontribusi dari dokter Salim Mulyana
 */
package surat;

import AESsecurity.EnkripsiAES;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
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
public final class SuratSakit extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private String tgl, finger = "", kodedokter = "", namadokter = "";

    /**
     * Creates new form DlgRujuk
     *
     * @param parent
     * @param modal
     */
    public SuratSakit(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(628, 674);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Surat Sakit", "No.Rawat", "No.R.M.", "Nama Pasien", "Dari Tanggal", "Sampai Tanggal", "Lama Sakit", "Status TTE"
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

        for (i = 0; i < 8; i++) {
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
                column.setPreferredWidth(90);
            } else if (i == 5) {
                column.setPreferredWidth(90);
            } else if (i == 6) {
                column.setPreferredWidth(90);
            } else if (i == 7) {
                column.setPreferredWidth(95);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        NoSurat.setDocument(new batasInput((byte) 17).getKata(NoSurat));
        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        LamaSakit.setDocument(new batasInput((byte) 20).getKata(LamaSakit));
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
        ChkInput.setSelected(false);
        isForm();

        widget.Button btnHistoriCetakSS = new widget.Button();
        btnHistoriCetakSS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/clock.png")));
        btnHistoriCetakSS.setMnemonic('H'); btnHistoriCetakSS.setText("Histori Cetak");
        btnHistoriCetakSS.setPreferredSize(new java.awt.Dimension(140, 30));
        btnHistoriCetakSS.addActionListener((java.awt.event.ActionEvent evt) -> tampilHistoriCetakSS());
        panelGlass8.add(btnHistoriCetakSS);
        widget.Button btnAuditEHSS = new widget.Button();
        btnAuditEHSS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/clock.png")));
        btnAuditEHSS.setMnemonic('A'); btnAuditEHSS.setText("Audit Edit/Hapus");
        btnAuditEHSS.setPreferredSize(new java.awt.Dimension(160, 30));
        btnAuditEHSS.addActionListener((java.awt.event.ActionEvent evt) -> tampilAuditSS());
        panelGlass8.add(btnAuditEHSS);
        panelGlass8.revalidate(); panelGlass8.repaint();
    }

    private void tampilHistoriCetakSS() {
        final javax.swing.JDialog dlg = new javax.swing.JDialog(this, "Histori Pencetakan Surat Sakit", true);
        dlg.setSize(820, 480); dlg.setLocationRelativeTo(this); dlg.setLayout(new java.awt.BorderLayout(6, 6));
        final DefaultTableModel mdl = new DefaultTableModel(null, new Object[]{"No.Surat","No.Rawat","Jenis","User","Tanggal","Jam"}) { @Override public boolean isCellEditable(int r, int c){ return false; } };
        final JTable tbl = new JTable(mdl); tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {150,120,160,120,100,90};
        for (int i = 0; i < widths.length; i++) tbl.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        javax.swing.JPanel top = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 5));
        top.add(new javax.swing.JLabel("Cari:"));
        final javax.swing.JTextField fCari = new javax.swing.JTextField(20); fCari.setText(TNoRw.getText()); top.add(fCari);
        final javax.swing.JButton bRefresh = new javax.swing.JButton("Refresh"); top.add(bRefresh);
        final javax.swing.JLabel lblInfo = new javax.swing.JLabel(""); top.add(lblInfo);
        Runnable loader = () -> {
            mdl.setRowCount(0);
            String key = "%" + fCari.getText().trim() + "%";
            String sql = "select no_surat,no_rawat,jenis_surat,kd_user,tanggal,jam from log_cetak_suratsakit where no_surat like ? or no_rawat like ? or kd_user like ? order by tanggal desc, jam desc limit 500";
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

    private void tampilAuditSS() {
        final javax.swing.JDialog dlg = new javax.swing.JDialog(this, "Audit Edit & Hapus Surat Sakit", true);
        dlg.setSize(900, 480); dlg.setLocationRelativeTo(this); dlg.setLayout(new java.awt.BorderLayout(6, 6));
        final DefaultTableModel mdl = new DefaultTableModel(null, new Object[]{"Aksi","User","Tgl Aksi","Jam","No.Surat","No.Rawat","Tgl Awal","Tgl Akhir","Lama"}) { @Override public boolean isCellEditable(int r, int c){ return false; } };
        final JTable tbl = new JTable(mdl); tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {60,90,100,80,140,120,90,90,70};
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
            String sql = "select aksi,kd_user,tanggal_aksi,jam_aksi,no_surat,no_rawat,tanggalawal,tanggalakhir,lamasakit from suratsakit_audit where (no_surat like ? or coalesce(no_rawat,'') like ? or kd_user like ?) " + (aksiSel.equals("SEMUA") ? "" : "and aksi=? ") + "order by tanggal_aksi desc, jam_aksi desc limit 500";
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

    private void snapshotSSAudit(String noSuratLama, String aksi) {
        PreparedStatement pSel = null, pIns = null; ResultSet rSel = null;
        try {
            pSel = koneksiDB.condb().prepareStatement("select no_surat,no_rawat,tanggalawal,tanggalakhir,lamasakit from suratsakit where no_surat=?");
            pSel.setString(1, noSuratLama); rSel = pSel.executeQuery();
            if (rSel.next()) {
                pIns = koneksiDB.condb().prepareStatement("insert into suratsakit_audit(aksi,kd_user,tanggal_aksi,jam_aksi,no_surat,no_rawat,tanggalawal,tanggalakhir,lamasakit) values(?,?,current_date(),current_time(),?,?,?,?,?)");
                pIns.setString(1, aksi); pIns.setString(2, akses.getkode());
                for (int k = 1; k <= 5; k++) pIns.setString(k + 2, rSel.getString(k));
                pIns.executeUpdate();
            }
        } catch (Exception ex) { System.out.println("snapshotSSAudit gagal: " + ex); }
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
        MnCetakSuratSakit = new javax.swing.JMenuItem();
        MnKirimWASakit = new javax.swing.JMenuItem();
        MnTteSuratSakit = new javax.swing.JMenuItem();
        MnBukaTteSuratSakit = new javax.swing.JMenuItem();
        MnCetakSuratSakit1 = new javax.swing.JMenuItem();
        MnCetakSuratSakit4 = new javax.swing.JMenuItem();
        MnCetakSuratSakit5 = new javax.swing.JMenuItem();
        MnCetakSuratSakit2 = new javax.swing.JMenuItem();
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
        TanggalAkhir = new widget.Tanggal();
        TNoRM = new widget.TextBox();
        LamaSakit = new widget.TextBox();
        jLabel16 = new widget.Label();
        jLabel18 = new widget.Label();
        TanggalAwal = new widget.Tanggal();
        jLabel13 = new widget.Label();
        ChkInput = new widget.CekBox();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnCetakSuratSakit.setBackground(new java.awt.Color(250, 250, 250));
        MnCetakSuratSakit.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnCetakSuratSakit.setForeground(new java.awt.Color(50, 50, 50));
        MnCetakSuratSakit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnCetakSuratSakit.setText("Cetak Surat Sakit 1");
        MnCetakSuratSakit.setName("MnCetakSuratSakit"); // NOI18N
        MnCetakSuratSakit.setPreferredSize(new java.awt.Dimension(200, 26));
        MnCetakSuratSakit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnCetakSuratSakitActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnCetakSuratSakit);

        MnKirimWASakit.setBackground(new java.awt.Color(250, 250, 250));
        MnKirimWASakit.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnKirimWASakit.setForeground(new java.awt.Color(50, 50, 50));
        MnKirimWASakit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnKirimWASakit.setText("Kirim WA Surat Sakit 1");
        MnKirimWASakit.setName("MnKirimWASakit"); // NOI18N
        MnKirimWASakit.setPreferredSize(new java.awt.Dimension(200, 26));
        MnKirimWASakit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnKirimWASakitActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnKirimWASakit);

        MnTteSuratSakit.setBackground(new java.awt.Color(250, 250, 250));
        MnTteSuratSakit.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnTteSuratSakit.setForeground(new java.awt.Color(50, 50, 50));
        MnTteSuratSakit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnTteSuratSakit.setText("Tanda Tangan Elektronik (TTE) BSrE");
        MnTteSuratSakit.setName("MnTteSuratSakit"); // NOI18N
        MnTteSuratSakit.setPreferredSize(new java.awt.Dimension(220, 26));
        MnTteSuratSakit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnTteSuratSakitActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnTteSuratSakit);

        MnBukaTteSuratSakit.setBackground(new java.awt.Color(250, 250, 250));
        MnBukaTteSuratSakit.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnBukaTteSuratSakit.setForeground(new java.awt.Color(50, 50, 50));
        MnBukaTteSuratSakit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnBukaTteSuratSakit.setText("Buka Dokumen TTE BSrE");
        MnBukaTteSuratSakit.setName("MnBukaTteSuratSakit"); // NOI18N
        MnBukaTteSuratSakit.setPreferredSize(new java.awt.Dimension(220, 26));
        MnBukaTteSuratSakit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnBukaTteSuratSakitActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnBukaTteSuratSakit);

        MnCetakSuratSakit1.setBackground(new java.awt.Color(250, 250, 250));
        MnCetakSuratSakit1.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnCetakSuratSakit1.setForeground(new java.awt.Color(50, 50, 50));
        MnCetakSuratSakit1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnCetakSuratSakit1.setText("Cetak Surat Sakit 2");
        MnCetakSuratSakit1.setName("MnCetakSuratSakit1"); // NOI18N
        MnCetakSuratSakit1.setPreferredSize(new java.awt.Dimension(200, 26));
        MnCetakSuratSakit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnCetakSuratSakit1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnCetakSuratSakit1);

        MnCetakSuratSakit4.setBackground(new java.awt.Color(250, 250, 250));
        MnCetakSuratSakit4.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnCetakSuratSakit4.setForeground(new java.awt.Color(50, 50, 50));
        MnCetakSuratSakit4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnCetakSuratSakit4.setText("Cetak Surat Sakit 3");
        MnCetakSuratSakit4.setName("MnCetakSuratSakit4"); // NOI18N
        MnCetakSuratSakit4.setPreferredSize(new java.awt.Dimension(200, 26));
        MnCetakSuratSakit4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnCetakSuratSakit4ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnCetakSuratSakit4);

        MnCetakSuratSakit5.setBackground(new java.awt.Color(250, 250, 250));
        MnCetakSuratSakit5.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnCetakSuratSakit5.setForeground(new java.awt.Color(50, 50, 50));
        MnCetakSuratSakit5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnCetakSuratSakit5.setText("Cetak Surat Sakit 4");
        MnCetakSuratSakit5.setName("MnCetakSuratSakit5"); // NOI18N
        MnCetakSuratSakit5.setPreferredSize(new java.awt.Dimension(200, 26));
        MnCetakSuratSakit5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnCetakSuratSakit5ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnCetakSuratSakit5);

        MnCetakSuratSakit2.setBackground(new java.awt.Color(250, 250, 250));
        MnCetakSuratSakit2.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnCetakSuratSakit2.setForeground(new java.awt.Color(50, 50, 50));
        MnCetakSuratSakit2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnCetakSuratSakit2.setText("Cetak Surat Sakit Pihak Ke-2");
        MnCetakSuratSakit2.setName("MnCetakSuratSakit2"); // NOI18N
        MnCetakSuratSakit2.setPreferredSize(new java.awt.Dimension(200, 26));
        MnCetakSuratSakit2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnCetakSuratSakit2ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnCetakSuratSakit2);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Surat Keterangan Sakit ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
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
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "20-01-2022" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "20-01-2022" }));
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

        TanggalAkhir.setForeground(new java.awt.Color(50, 70, 50));
        TanggalAkhir.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "20-01-2022" }));
        TanggalAkhir.setDisplayFormat("dd-MM-yyyy");
        TanggalAkhir.setName("TanggalAkhir"); // NOI18N
        TanggalAkhir.setOpaque(false);
        TanggalAkhir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TanggalAkhirActionPerformed(evt);
            }
        });
        TanggalAkhir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalAkhirKeyPressed(evt);
            }
        });
        FormInput.add(TanggalAkhir);
        TanggalAkhir.setBounds(447, 40, 90, 23);

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

        LamaSakit.setHighlighter(null);
        LamaSakit.setName("LamaSakit"); // NOI18N
        LamaSakit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LamaSakitActionPerformed(evt);
            }
        });
        LamaSakit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LamaSakitKeyPressed(evt);
            }
        });
        FormInput.add(LamaSakit);
        LamaSakit.setBounds(614, 40, 106, 23);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("s.d.");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(417, 40, 27, 23);

        jLabel18.setText("Lama Sakit :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(540, 40, 70, 23);

        TanggalAwal.setForeground(new java.awt.Color(50, 70, 50));
        TanggalAwal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "20-01-2022" }));
        TanggalAwal.setDisplayFormat("dd-MM-yyyy");
        TanggalAwal.setName("TanggalAwal"); // NOI18N
        TanggalAwal.setOpaque(false);
        TanggalAwal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TanggalAwalActionPerformed(evt);
            }
        });
        TanggalAwal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalAwalKeyPressed(evt);
            }
        });
        FormInput.add(TanggalAwal);
        TanggalAwal.setBounds(324, 40, 90, 23);

        jLabel13.setText("Dari Tanggal :");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(240, 40, 80, 23);

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
        Valid.pindah(evt, TCari, TanggalAwal);
}//GEN-LAST:event_NoSuratKeyPressed

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            isRawat();
            isPsien();
        } else {
            Valid.pindah(evt, TCari, LamaSakit);
        }
}//GEN-LAST:event_TNoRwKeyPressed

    private void TPasienKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TPasienKeyPressed
        Valid.pindah(evt, TCari, BtnSimpan);
}//GEN-LAST:event_TPasienKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (NoSurat.getText().trim().equals("")) {
            Valid.textKosong(NoSurat, "No.Surat Sakit");
        } else if (LamaSakit.getText().trim().equals("")) {
            Valid.textKosong(LamaSakit, "lama sakit");
        } else if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "pasien");
        } else {
            if (Sequel.menyimpantf("suratsakit", "?,?,?,?,?", "No.Surat Sakit", 5, new String[]{
                NoSurat.getText(), TNoRw.getText(), Valid.SetTgl(TanggalAwal.getSelectedItem() + ""), Valid.SetTgl(TanggalAkhir.getSelectedItem() + ""), LamaSakit.getText()
            }) == true) {
                tampil();
                emptTeks();
            }
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, LamaSakit, BtnBatal);
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
            snapshotSSAudit(NoSurat.getText(),"HAPUS");
        }
        Valid.hapusTable(tabMode, NoSurat, "suratsakit", "no_surat");
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
        } else {
            if (tbObat.getSelectedRow() != -1) {
                snapshotSSAudit(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),"EDIT");
                if (Sequel.mengedittf("suratsakit", "no_surat=?", "no_surat=?,no_rawat=?,tanggalawal=?,tanggalakhir=?,lamasakit=?", 6, new String[]{
                    NoSurat.getText(), TNoRw.getText(), Valid.SetTgl(TanggalAwal.getSelectedItem() + ""), Valid.SetTgl(TanggalAkhir.getSelectedItem() + ""), LamaSakit.getText(), tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
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
            tgl = " suratsakit.tanggalawal between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' ";
            if (TCari.getText().trim().equals("")) {
                Valid.MyReportqry("rptDataSuratSakit.jasper", "report", "::[ Data Surat Sakit Pasien ]::",
                        "select suratsakit.no_surat,suratsakit.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"
                        + "suratsakit.tanggalawal,suratsakit.tanggalakhir,suratsakit.lamasakit "
                        + "from suratsakit inner join reg_periksa on suratsakit.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "where " + tgl + "order by suratsakit.no_surat", param);
            } else {
                Valid.MyReportqry("rptDataSuratSakit.jasper", "report", "::[ Data Surat Sakit Pasien ]::",
                        "select suratsakit.no_surat,suratsakit.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"
                        + "suratsakit.tanggalawal,suratsakit.tanggalakhir,suratsakit.lamasakit "
                        + "from suratsakit inner join reg_periksa on suratsakit.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "where " + tgl + "and no_surat like '%" + TCari.getText().trim() + "%' or "
                        + tgl + "and suratsakit.no_rawat like '%" + TCari.getText().trim() + "%' or "
                        + tgl + "and reg_periksa.no_rkm_medis like '%" + TCari.getText().trim() + "%' or "
                        + tgl + "and pasien.nm_pasien like '%" + TCari.getText().trim() + "%' or "
                        + tgl + "and suratsakit.tanggalawal like '%" + TCari.getText().trim() + "%' or "
                        + tgl + "and suratsakit.tanggalakhir like '%" + TCari.getText().trim() + "%' "
                        + "order by suratsakit.no_surat", param);
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


    private void TanggalAkhirKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalAkhirKeyPressed
        Valid.pindah(evt, TanggalAwal, LamaSakit);
}//GEN-LAST:event_TanggalAkhirKeyPressed

    private void TNoRMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRMKeyPressed

}//GEN-LAST:event_TNoRMKeyPressed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
}//GEN-LAST:event_tbObatMouseClicked

    private void LamaSakitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LamaSakitKeyPressed
        Valid.pindah(evt, TanggalAkhir, BtnSimpan);
    }//GEN-LAST:event_LamaSakitKeyPressed

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

    private void LamaSakitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LamaSakitActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_LamaSakitActionPerformed

    private void TanggalAkhirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TanggalAkhirActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TanggalAkhirActionPerformed

    private void TanggalAwalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TanggalAwalActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TanggalAwalActionPerformed

    private void TanggalAwalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalAwalKeyPressed
        Valid.pindah(evt, NoSurat, TanggalAkhir);
    }//GEN-LAST:event_TanggalAwalKeyPressed

    private void MnCetakSuratSakitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnCetakSuratSakitActionPerformed
        if (TPasien.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Sequel.menyimpan("log_cetak_suratsakit","null,?,?,?,?,current_date(),current_time()",4,new String[]{
                NoSurat.getText(),TNoRw.getText(),"Surat Sakit",akses.getkode()
            });
            Map<String, Object> param = new HashMap<>();
            param.put("hari", LamaSakit.getText());
            param.put("TanggalAwal", TanggalAwal.getSelectedItem().toString());
            param.put("TanggalAkhir", TanggalAkhir.getSelectedItem().toString());
            param.put("nosakit", NoSurat.getText());
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            kodedokter = Sequel.cariIsi("select reg_periksa.kd_dokter from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText());
            namadokter = Sequel.cariIsi("select dokter.nm_dokter from dokter where dokter.kd_dokter=?", kodedokter);
            param.put("sip", Sequel.cariIsi("select no_ijn_praktek from dokter where kd_dokter = '" + kodedokter + "'"));
            param.put("nip", kodedokter);
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", kodedokter);
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + namadokter + "\nID " + (finger.equals("") ? kodedokter : finger) + "\n" + Sequel.cariIsi("select DATE_FORMAT(reg_periksa.tgl_registrasi,'%d-%m-%Y') from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText()));
            param.put("penyakit", Sequel.cariIsi("select concat(diagnosa_pasien.kd_penyakit,' ',penyakit.nm_penyakit) from diagnosa_pasien inner join reg_periksa inner join penyakit "
                    + "on diagnosa_pasien.no_rawat=reg_periksa.no_rawat and diagnosa_pasien.kd_penyakit=penyakit.kd_penyakit "
                    + "where diagnosa_pasien.no_rawat=? and diagnosa_pasien.prioritas='1'", TNoRw.getText()));
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            param.put("logo2", Sequel.cariGambar("select setting.logo2 from setting"));
            param.put("verifikasi", "https://" + koneksiDB.URLVERIFIKASI() + "/verify/?id=" + EnkripsiAES.encrypt("{'x': 'sakit', 't':'" + NoSurat.getText() + "'}"));
            Valid.MyReportqry("rptSuratSakit5.jasper", "report", "::[ Surat Sakit ]::",
                    "select DATE_FORMAT(reg_periksa.tgl_registrasi,'%d-%m-%Y')as tgl_registrasi,perusahaan_pasien.nama_perusahaan,reg_periksa.no_rawat,dokter.nm_dokter,pasien.keluarga,pasien.namakeluarga,pasien.tgl_lahir,pasien.jk,"
                    + " pasien.nm_pasien,pasien.jk,concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur)as umur,pasien.pekerjaan,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat"
                    + " from reg_periksa inner join pasien inner join dokter inner join kelurahan inner join perusahaan_pasien inner join kecamatan inner join kabupaten"
                    + " on reg_periksa.no_rkm_medis=pasien.no_rkm_medis and reg_periksa.kd_dokter=dokter.kd_dokter and pasien.kd_kel=kelurahan.kd_kel "
                    + "and pasien.perusahaan_pasien=perusahaan_pasien.kode_perusahaan and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab "
                    + "where reg_periksa.no_rawat='" + TNoRw.getText() + "' ", param);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_MnCetakSuratSakitActionPerformed

    // ===== Kirim Surat Sakit 1 (rptSuratSakit5, sudah ber-QR dokter) ke WhatsApp =====
    private void MnKirimWASakitActionPerformed(java.awt.event.ActionEvent evt) {
        if (TPasien.getText().trim().equals("") || TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, silakan pilih dulu pasien...!!!");
            return;
        }
        String noRawat = TNoRw.getText();
        String noRM = Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat=?", noRawat);
        String namaPasien = TPasien.getText();
        String noHp = Sequel.cariIsi("select pasien.no_tlp from pasien where pasien.no_rkm_medis=?", noRM);
        String tglLahir = Sequel.cariIsi("select pasien.tgl_lahir from pasien where pasien.no_rkm_medis=?", noRM);

        // Generate PDF (param & query SAMA dg 'Cetak Surat Sakit 1' — rptSuratSakit5 sudah punya QR dokter dari $P{finger})
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Map<String, Object> param = new HashMap<>();
        param.put("hari", LamaSakit.getText());
        param.put("TanggalAwal", TanggalAwal.getSelectedItem().toString());
        param.put("TanggalAkhir", TanggalAkhir.getSelectedItem().toString());
        param.put("nosakit", NoSurat.getText());
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        kodedokter = Sequel.cariIsi("select reg_periksa.kd_dokter from reg_periksa where reg_periksa.no_rawat=?", noRawat);
        namadokter = Sequel.cariIsi("select dokter.nm_dokter from dokter where dokter.kd_dokter=?", kodedokter);
        param.put("sip", Sequel.cariIsi("select no_ijn_praktek from dokter where kd_dokter = '" + kodedokter + "'"));
        param.put("nip", kodedokter);
        finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", kodedokter);
        param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + namadokter + "\nID " + (finger.equals("") ? kodedokter : finger) + "\n" + Sequel.cariIsi("select DATE_FORMAT(reg_periksa.tgl_registrasi,'%d-%m-%Y') from reg_periksa where reg_periksa.no_rawat=?", noRawat));
        param.put("penyakit", Sequel.cariIsi("select concat(diagnosa_pasien.kd_penyakit,' ',penyakit.nm_penyakit) from diagnosa_pasien inner join reg_periksa inner join penyakit "
                + "on diagnosa_pasien.no_rawat=reg_periksa.no_rawat and diagnosa_pasien.kd_penyakit=penyakit.kd_penyakit "
                + "where diagnosa_pasien.no_rawat=? and diagnosa_pasien.prioritas='1'", noRawat));
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        param.put("logo2", Sequel.cariGambar("select setting.logo2 from setting"));
        param.put("verifikasi", "https://" + koneksiDB.URLVERIFIKASI() + "/verify/?id=" + EnkripsiAES.encrypt("{'x': 'sakit', 't':'" + NoSurat.getText() + "'}"));
        Valid.MyReportPDF2Qry("rptSuratSakit5.jasper", "report", "::[ Surat Sakit ]::",
                "select DATE_FORMAT(reg_periksa.tgl_registrasi,'%d-%m-%Y')as tgl_registrasi,perusahaan_pasien.nama_perusahaan,reg_periksa.no_rawat,dokter.nm_dokter,pasien.keluarga,pasien.namakeluarga,pasien.tgl_lahir,pasien.jk,"
                + " pasien.nm_pasien,pasien.jk,concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur)as umur,pasien.pekerjaan,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat"
                + " from reg_periksa inner join pasien inner join dokter inner join kelurahan inner join perusahaan_pasien inner join kecamatan inner join kabupaten"
                + " on reg_periksa.no_rkm_medis=pasien.no_rkm_medis and reg_periksa.kd_dokter=dokter.kd_dokter and pasien.kd_kel=kelurahan.kd_kel "
                + "and pasien.perusahaan_pasien=perusahaan_pasien.kode_perusahaan and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab "
                + "where reg_periksa.no_rawat='" + noRawat + "' ", param);
        this.setCursor(Cursor.getDefaultCursor());

        File pdfAsli = new File("./report/rptSuratSakit5.pdf");
        if (!pdfAsli.exists()) {
            JOptionPane.showMessageDialog(null, "File PDF Surat Sakit tidak ditemukan / gagal dibuat.");
            return;
        }
        kirimPdfKeWa(pdfAsli, noRM, namaPasien, noHp, tglLahir, "SURAT KETERANGAN SAKIT", "SuratSakit_" + noRM + ".pdf");
    }

    // ===== TTE BSrE Surat Sakit & Simpan ke DlgBerkasRawat =====
    private void MnTteSuratSakitActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tbObat.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, silakan pilih dulu data surat sakit di tabel...!!!");
            return;
        }

        String noSurat = tbObat.getValueAt(row, 0).toString();
        String noRawat = tbObat.getValueAt(row, 1).toString();
        String noRM = tbObat.getValueAt(row, 2).toString();
        String namaPasien = tbObat.getValueAt(row, 3).toString();
        String tglAwal = tbObat.getValueAt(row, 4).toString();
        String tglAkhir = tbObat.getValueAt(row, 5).toString();
        String lamaSakit = tbObat.getValueAt(row, 6).toString();

        String cleanNoRawatCheck = noRawat.replaceAll("[^a-zA-Z0-9]", "_");
        String cleanNoSuratCheck = noSurat.replaceAll("[^a-zA-Z0-9]", "_");
        String checkSignedFileName = "SuratSakit_" + cleanNoRawatCheck + "_" + cleanNoSuratCheck + ".pdf";
        File checkLocalSignedFile = new File("./tmpPDF/" + checkSignedFileName);
        String checkBerkasTte = Sequel.cariIsi(
            "select lokasi_file from berkas_digital_perawatan where no_rawat='" + noRawat + "' and kode='064' and (lokasi_file like '%" + cleanNoSuratCheck + "%' or lokasi_file like '%" + cleanNoRawatCheck + "%') limit 1"
        );
        if (checkBerkasTte.isEmpty()) {
            checkBerkasTte = Sequel.cariIsi(
                "select lokasi_file from berkas_digital_perawatan where no_rawat='" + noRawat + "' and kode='064' limit 1"
            );
        }

        if (checkLocalSignedFile.exists() || !checkBerkasTte.isEmpty()) {
            Object[] options = {"Buka Dokumen TTE", "TTE Ulang", "Batal"};
            int pilihan = JOptionPane.showOptionDialog(null,
                "Surat Keterangan Sakit No. " + noSurat + " (" + namaPasien + ")\n" +
                "Status: SUDAH DITANDATANGANI SECARA ELEKTRONIK (TTE BSrE).\n\n" +
                "Apakah Anda ingin membuka dokumen yang sudah di-TTE atau melakukan TTE ulang?",
                "Konfirmasi TTE Surat Sakit",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );

            if (pilihan == 0) {
                bukaDokumenTte(noRawat, noSurat, checkSignedFileName, checkBerkasTte);
                return;
            } else if (pilihan == 1) {
                // TTE Ulang dipilih, lanjutkan ke proses penandatanganan ulang
            } else {
                return;
            }
        }

        String kdDokter = Sequel.cariIsi("select reg_periksa.kd_dokter from reg_periksa where reg_periksa.no_rawat=?", noRawat);
        String nmDokter = Sequel.cariIsi("select dokter.nm_dokter from dokter where dokter.kd_dokter=?", kdDokter);
        // Validasi Otorisasi Dokter yang bersangkutan
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

        // Dialog Passphrase Aman (Poin 3 & 4 Checklist UKS BSrE)
        javax.swing.JPasswordField txtPass = new javax.swing.JPasswordField(20);
        txtPass.putClientProperty("JPasswordField.cutCopyAllowed", false);
        javax.swing.JPanel pnlTte = new javax.swing.JPanel(new java.awt.GridLayout(0, 1, 3, 3));
        pnlTte.add(new javax.swing.JLabel("Tanda Tangan Elektronik (TTE) BSrE - Surat Keterangan Sakit"));
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
            // 1. Parameter Laporan
            Map<String, Object> param = new HashMap<>();
            param.put("hari", lamaSakit);
            param.put("TanggalAwal", tglAwal);
            param.put("TanggalAkhir", tglAkhir);
            param.put("nosakit", noSurat);
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("sip", Sequel.cariIsi("select no_ijn_praktek from dokter where kd_dokter = '" + kdDokter + "'"));
            param.put("nip", kdDokter);
            param.put("penyakit", Sequel.cariIsi("select concat(diagnosa_pasien.kd_penyakit,' ',penyakit.nm_penyakit) from diagnosa_pasien inner join reg_periksa inner join penyakit "
                    + "on diagnosa_pasien.no_rawat=reg_periksa.no_rawat and diagnosa_pasien.kd_penyakit=penyakit.kd_penyakit "
                    + "where diagnosa_pasien.no_rawat=? and diagnosa_pasien.prioritas='1'", noRawat));
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            param.put("logo2", Sequel.cariGambar("select setting.logo2 from setting"));
            param.put("logobsre", Sequel.cariGambar("select gambar.bsre from gambar"));
            String urlVerifikasiTTE = "https://" + koneksiDB.URLVERIFIKASITTE() + "/verify/?id=" + EnkripsiAES.encrypt("{'x': 'sakit', 't':'" + noSurat + "'}");
            param.put("verifikasi", urlVerifikasiTTE);
            param.put("finger", "#");

            String sqlQuery = "select DATE_FORMAT(reg_periksa.tgl_registrasi,'%d-%m-%Y')as tgl_registrasi,IFNULL(perusahaan_pasien.nama_perusahaan,'-') as nama_perusahaan,reg_periksa.no_rawat,dokter.nm_dokter,pasien.keluarga,pasien.namakeluarga,pasien.tgl_lahir,pasien.jk,"
                    + " pasien.nm_pasien,concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur)as umur,pasien.pekerjaan,"
                    + " IF(kelurahan.nm_kel IS NOT NULL, concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab), pasien.alamat) as alamat"
                    + " from reg_periksa "
                    + " inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                    + " inner join dokter on reg_periksa.kd_dokter=dokter.kd_dokter "
                    + " left join kelurahan on pasien.kd_kel=kelurahan.kd_kel "
                    + " left join perusahaan_pasien on pasien.perusahaan_pasien=perusahaan_pasien.kode_perusahaan "
                    + " left join kecamatan on pasien.kd_kec=kecamatan.kd_kec "
                    + " left join kabupaten on pasien.kd_kab=kabupaten.kd_kab "
                    + " where reg_periksa.no_rawat='" + noRawat + "' ";

            File dirTmp = new File("./tmpPDF");
            if (!dirTmp.exists()) {
                dirTmp.mkdirs();
            }

            String cleanNoRawat = noRawat.replaceAll("[^a-zA-Z0-9]", "_");
            String cleanNoSurat = noSurat.replaceAll("[^a-zA-Z0-9]", "_");
            String rawPdfPath = "./tmpPDF/SuratSakit_raw_" + cleanNoRawat + ".pdf";
            String signedFileName = "SuratSakit_" + cleanNoRawat + "_" + cleanNoSurat + ".pdf";
            String signedPdfPath = "./tmpPDF/" + signedFileName;

            // Generate Raw PDF with JasperReports
            try (PreparedStatement pst = koneksi.prepareStatement(sqlQuery);
                 ResultSet rst = pst.executeQuery()) {
                JRResultSetDataSource ds = new JRResultSetDataSource(rst);
                String jasperFile = "./report/rptSuratSakitTTE.jasper";
                if (!new File(jasperFile).exists()) {
                    jasperFile = "report/rptSuratSakitTTE.jasper";
                }
                if (!new File(jasperFile).exists()) {
                    jasperFile = "./dist/report/rptSuratSakitTTE.jasper";
                }
                if (!new File(jasperFile).exists()) {
                    jasperFile = "./report/rptSuratSakit5.jasper";
                }
                JasperPrint jp = JasperFillManager.fillReport(jasperFile, param, ds);
                JasperExportManager.exportReportToPdfFile(jp, rawPdfPath);
            }

            File rawFile = new File(rawPdfPath);
            if (!rawFile.exists() || rawFile.length() == 0) {
                JOptionPane.showMessageDialog(null, "Gagal meng-generate dokumen PDF Surat Sakit!");
                this.setCursor(Cursor.getDefaultCursor());
                return;
            }

            File signedFile = new File(signedPdfPath);
            if (signedFile.exists()) {
                signedFile.delete();
            }

            // 2. Eksekusi TTE BSrE V2
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
                sp.originX = 417.5f;
                sp.originY = 360.0f;
                sp.width = 85.0f;
                sp.height = 85.0f;
            }

            sp.location = akses.getkabupatenrs();
            sp.reason = "Dokumen Surat Keterangan Sakit telah disetujui dan ditandatangani secara elektronik";
            sp.linkQR = urlVerifikasiTTE;
            sp.imageBase64 = esign.generateQRCodeBase64(sp.linkQR, 400, 400);
            props.add(sp);

            boolean suksesTte = esign.prosesTTE(rawFile, signedFile, nikDokter, passphrase, props);
            if (!suksesTte || !signedFile.exists()) {
                // UKS BSrE Poin 8 (Notifikasi gagal informatif)
                String errMsg = esign.getLastErrorMessage() != null && !esign.getLastErrorMessage().isEmpty() 
                                ? esign.getLastErrorMessage() : "Penandatanganan gagal";
                int errCode = esign.getLastStatusCode();
                EsignServiceV2.catatLog(noRawat, nikDokter, nmDokter, "Gagal TTE Surat Sakit (" + noSurat + "): " + errMsg, String.valueOf(errCode));
                JOptionPane.showMessageDialog(null, 
                    "Penandatanganan Elektronik (TTE) Gagal!\n" +
                    "Status Code: " + errCode + "\n" +
                    "Pesan BSrE: " + errMsg, 
                    "TTE BSrE Gagal", JOptionPane.ERROR_MESSAGE);
                this.setCursor(Cursor.getDefaultCursor());
                return;
            }

            // 3. Simpan dan Upload ke Berkas Digital Perawatan (DlgBerkasRawat)
            simpanKeBerkasRawat(signedFile, signedFileName, noRawat);

            // UKS BSrE Poin 9 (Perekaman log keberhasilan TTE)
            EsignServiceV2.catatLog(noRawat, nikDokter, nmDokter, "Sukses TTE Surat Sakit (" + noSurat + ") - File: " + signedFileName, "200");

            // UKS BSrE Poin 7 (Notifikasi berhasil)
            JOptionPane.showMessageDialog(null, 
                "Surat Keterangan Sakit BERHASIL ditandatangani secara elektronik (BSrE)!\n" +
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
            System.out.println("Error TTE Surat Sakit: " + e.getMessage());
            e.printStackTrace();
            EsignServiceV2.catatLog(noRawat, nikDokter, nmDokter, "Kesalahan Sistem TTE Surat Sakit (" + noSurat + "): " + e.getMessage(), "EXC");
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan sistem saat TTE: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            this.setCursor(Cursor.getDefaultCursor());
        }
    }

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

            // Pastikan master_berkas_digital memiliki kode 064 untuk Surat Sakit
            Sequel.queryu("INSERT IGNORE INTO master_berkas_digital (kode, nama) VALUES ('064', 'SURAT KETERANGAN SAKIT')");

            String lokasiFile = "pages/upload/" + fileName;
            if (Sequel.cariInteger("SELECT COUNT(*) FROM berkas_digital_perawatan WHERE no_rawat=? AND kode=?", noRawat, "064") > 0) {
                Sequel.mengedittf("berkas_digital_perawatan", "no_rawat=? AND kode=?", "lokasi_file=?, nip=?", 4,
                    new String[]{lokasiFile, akses.getkode(), noRawat, "064"});
            } else {
                Sequel.menyimpantf("berkas_digital_perawatan", "?,?,?,?", "Surat Sakit TTE", 4,
                    new String[]{noRawat, "064", lokasiFile, akses.getkode()});
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

    private void MnBukaTteSuratSakitActionPerformed(java.awt.event.ActionEvent evt) {
        int row = tbObat.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, silakan pilih dulu data surat sakit di tabel...!!!");
            return;
        }

        String noSurat = tbObat.getValueAt(row, 0).toString();
        String noRawat = tbObat.getValueAt(row, 1).toString();
        String cleanNoRawat = noRawat.replaceAll("[^a-zA-Z0-9]", "_");
        String cleanNoSurat = noSurat.replaceAll("[^a-zA-Z0-9]", "_");
        String signedFileName = "SuratSakit_" + cleanNoRawat + "_" + cleanNoSurat + ".pdf";
        String berkasTte = Sequel.cariIsi(
            "select lokasi_file from berkas_digital_perawatan where no_rawat='" + noRawat + "' and kode='064' and (lokasi_file like '%" + cleanNoSurat + "%' or lokasi_file like '%" + cleanNoRawat + "%') limit 1"
        );
        if (berkasTte.isEmpty()) {
            berkasTte = Sequel.cariIsi(
                "select lokasi_file from berkas_digital_perawatan where no_rawat='" + noRawat + "' and kode='064' limit 1"
            );
        }
        bukaDokumenTte(noRawat, noSurat, signedFileName, berkasTte);
    }

    /** Konfirmasi No.WA (editable) → enkripsi (password tgl lahir) → kirim GOWA /send/file (device MAC→global). */
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
            try ( CloseableHttpClient httpClient = HttpClients.createDefault()) {
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
                try ( CloseableHttpResponse resp = httpClient.execute(up)) {
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

    private void MnCetakSuratSakit2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnCetakSuratSakit2ActionPerformed
        if (TPasien.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Sequel.menyimpan("log_cetak_suratsakit","null,?,?,?,?,current_date(),current_time()",4,new String[]{
                NoSurat.getText(),TNoRw.getText(),"Surat Sakit (Cetak 2)",akses.getkode()
            });
            Map<String, Object> param = new HashMap<>();
            param.put("hari", LamaSakit.getText());
            param.put("TanggalAwal", TanggalAwal.getSelectedItem().toString());
            param.put("TanggalAkhir", TanggalAkhir.getSelectedItem().toString());
            param.put("nosakit", NoSurat.getText());
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("penyakit", Sequel.cariIsi("select concat(diagnosa_pasien.kd_penyakit,' ',penyakit.nm_penyakit) from diagnosa_pasien inner join reg_periksa inner join penyakit "
                    + "on diagnosa_pasien.no_rawat=reg_periksa.no_rawat and diagnosa_pasien.kd_penyakit=penyakit.kd_penyakit "
                    + "where diagnosa_pasien.no_rawat=? and diagnosa_pasien.prioritas='1'", TNoRw.getText()));
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            kodedokter = Sequel.cariIsi("select reg_periksa.kd_dokter from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText());
            namadokter = Sequel.cariIsi("select dokter.nm_dokter from dokter where dokter.kd_dokter=?", kodedokter);
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", kodedokter);
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + namadokter + "\nID " + (finger.equals("") ? kodedokter : finger) + "\n" + Sequel.cariIsi("select DATE_FORMAT(reg_periksa.tgl_registrasi,'%d-%m-%Y') from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText()));
            param.put("verifikasi", "https://" + koneksiDB.URLVERIFIKASI() + "/verify/?id=" + EnkripsiAES.encrypt("{'x': 'sakit', 't':'" + NoSurat.getText() + "'}"));
            Valid.MyReportqry("rptSuratSakit7.jasper", "report", "::[ Surat Sakit ]::",
                    "select pasien.namakeluarga,pasien.pekerjaanpj,pasien.alamatpj,reg_periksa.no_rawat,dokter.nm_dokter,pasien.keluarga,pasien.namakeluarga,pasien.tgl_lahir,pasien.jk,"
                    + " DATE_FORMAT(reg_periksa.tgl_registrasi,'%d-%m-%Y')as tgl_registrasi,pasien.nm_pasien,concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur)as umur,pasien.pekerjaan,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat"
                    + " from reg_periksa inner join pasien inner join dokter inner join kelurahan inner join kecamatan inner join kabupaten"
                    + " on reg_periksa.no_rkm_medis=pasien.no_rkm_medis and reg_periksa.kd_dokter=dokter.kd_dokter and pasien.kd_kel=kelurahan.kd_kel "
                    + "and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab "
                    + "where reg_periksa.no_rawat='" + TNoRw.getText() + "' ", param);
            this.setCursor(Cursor.getDefaultCursor());

        }
    }//GEN-LAST:event_MnCetakSuratSakit2ActionPerformed

    private void MnCetakSuratSakit1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnCetakSuratSakit1ActionPerformed
        if (TPasien.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Sequel.menyimpan("log_cetak_suratsakit","null,?,?,?,?,current_date(),current_time()",4,new String[]{
                NoSurat.getText(),TNoRw.getText(),"Surat Sakit (Cetak 1)",akses.getkode()
            });
            Map<String, Object> param = new HashMap<>();
            param.put("hari", LamaSakit.getText());
            param.put("TanggalAwal", TanggalAwal.getSelectedItem().toString());
            param.put("TanggalAkhir", TanggalAkhir.getSelectedItem().toString());
            param.put("nosakit", NoSurat.getText());
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
            param.put("verifikasi", "https://" + koneksiDB.URLVERIFIKASI() + "/verify/?id=" + EnkripsiAES.encrypt("{'x': 'sakit', 't':'" + NoSurat.getText() + "'}"));
            Valid.MyReportqry("rptSuratSakit.jasper", "report", "::[ Surat Sakit ]::",
                    "select reg_periksa.no_rawat,dokter.nm_dokter,pasien.keluarga,pasien.namakeluarga,pasien.tgl_lahir,pasien.jk,"
                    + " pasien.nm_pasien,pasien.jk,concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur)as umur,pasien.pekerjaan,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat"
                    + " from reg_periksa inner join pasien inner join dokter inner join kelurahan inner join kecamatan inner join kabupaten"
                    + " on reg_periksa.no_rkm_medis=pasien.no_rkm_medis and reg_periksa.kd_dokter=dokter.kd_dokter and pasien.kd_kel=kelurahan.kd_kel "
                    + "and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab "
                    + "where reg_periksa.no_rawat='" + TNoRw.getText() + "' ", param);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_MnCetakSuratSakit1ActionPerformed

    private void MnCetakSuratSakit4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnCetakSuratSakit4ActionPerformed
        if (TPasien.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Sequel.menyimpan("log_cetak_suratsakit","null,?,?,?,?,current_date(),current_time()",4,new String[]{
                NoSurat.getText(),TNoRw.getText(),"Surat Sakit (Cetak 4)",akses.getkode()
            });
            Map<String, Object> param = new HashMap<>();
            param.put("hari", LamaSakit.getText());
            param.put("TanggalAwal", TanggalAwal.getSelectedItem().toString());
            param.put("TanggalAkhir", TanggalAkhir.getSelectedItem().toString());
            param.put("nosakit", NoSurat.getText());
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
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + namadokter + "no_ijin_praktek" + "\nID " + (finger.equals("") ? kodedokter : finger) + "\n" + Sequel.cariIsi("select DATE_FORMAT(reg_periksa.tgl_registrasi,'%d-%m-%Y') from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText()));
            Valid.MyReportqry("rptSuratSakit3.jasper", "report", "::[ Surat Sakit ]::",
                    "select reg_periksa.no_rawat,dokter.nm_dokter,pasien.keluarga,pasien.namakeluarga,pasien.tgl_lahir,DATE_FORMAT(reg_periksa.tgl_registrasi,'%d-%m-%Y')as tgl_registrasi,"
                    + " pasien.nm_pasien,pasien.jk,concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur)as umur,pasien.pekerjaan,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat"
                    + " from reg_periksa inner join pasien inner join dokter inner join kelurahan inner join kecamatan inner join kabupaten"
                    + " on reg_periksa.no_rkm_medis=pasien.no_rkm_medis and reg_periksa.kd_dokter=dokter.kd_dokter and pasien.kd_kel=kelurahan.kd_kel "
                    + " and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab "
                    + " where reg_periksa.no_rawat='" + TNoRw.getText() + "' ", param);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_MnCetakSuratSakit4ActionPerformed

    private void MnCetakSuratSakit5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnCetakSuratSakit5ActionPerformed
        if (TPasien.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> param = new HashMap<>();
            param.put("hari", LamaSakit.getText());
            param.put("TanggalAwal", TanggalAwal.getSelectedItem().toString());
            param.put("TanggalAkhir", TanggalAkhir.getSelectedItem().toString());
            param.put("nosakit", NoSurat.getText());
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("penyakit", Sequel.cariIsi("select concat(diagnosa_pasien.kd_penyakit,' ',penyakit.nm_penyakit) from diagnosa_pasien inner join reg_periksa inner join penyakit "
                    + "on diagnosa_pasien.no_rawat=reg_periksa.no_rawat and diagnosa_pasien.kd_penyakit=penyakit.kd_penyakit "
                    + "where diagnosa_pasien.no_rawat=? and diagnosa_pasien.prioritas='1'", TNoRw.getText()));
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            kodedokter = Sequel.cariIsi("select reg_periksa.kd_dokter from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText());
            namadokter = Sequel.cariIsi("select dokter.nm_dokter from dokter where dokter.kd_dokter=?", kodedokter);
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", kodedokter);
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + namadokter + "\nID " + (finger.equals("") ? kodedokter : finger) + "\n" + Sequel.cariIsi("select DATE_FORMAT(reg_periksa.tgl_registrasi,'%d-%m-%Y') from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText()));
            param.put("verifikasi", "https://" + koneksiDB.URLVERIFIKASI() + "/verify/?id=" + EnkripsiAES.encrypt("{'x': 'sakit', 't':'" + NoSurat.getText() + "'}"));
            Valid.MyReportqry("rptSuratSakit4.jasper", "report", "::[ Surat Sakit ]::",
                    "select reg_periksa.no_rawat,dokter.nm_dokter,pasien.keluarga,pasien.namakeluarga,pasien.tgl_lahir,pasien.jk,DATE_FORMAT(reg_periksa.tgl_registrasi,'%d-%m-%Y')as tgl_registrasi,"
                    + " pasien.nm_pasien,concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur)as umur,pasien.pekerjaan,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat"
                    + " from reg_periksa inner join pasien inner join dokter inner join kelurahan inner join kecamatan inner join kabupaten"
                    + " on reg_periksa.no_rkm_medis=pasien.no_rkm_medis and reg_periksa.kd_dokter=dokter.kd_dokter and pasien.kd_kel=kelurahan.kd_kel "
                    + " and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab "
                    + "where reg_periksa.no_rawat='" + TNoRw.getText() + "' ", param);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_MnCetakSuratSakit5ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            SuratSakit dialog = new SuratSakit(new javax.swing.JFrame(), true);
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
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.PanelBiasa FormInput;
    private widget.Label LCount;
    private widget.TextBox LamaSakit;
    private javax.swing.JMenuItem MnCetakSuratSakit;
    private javax.swing.JMenuItem MnKirimWASakit;
    private javax.swing.JMenuItem MnTteSuratSakit;
    private javax.swing.JMenuItem MnCetakSuratSakit1;
    private javax.swing.JMenuItem MnCetakSuratSakit2;
    private javax.swing.JMenuItem MnCetakSuratSakit4;
    private javax.swing.JMenuItem MnCetakSuratSakit5;
    private widget.TextBox NoSurat;
    private javax.swing.JPanel PanelInput;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Tanggal TanggalAkhir;
    private widget.Tanggal TanggalAwal;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel13;
    private widget.Label jLabel16;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel3;
    private widget.Label jLabel4;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JMenuItem MnBukaTteSuratSakit;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            tgl = " suratsakit.tanggalawal between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' ";
            String sqlSelect = "select suratsakit.no_surat,suratsakit.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,"
                    + "suratsakit.tanggalawal,suratsakit.tanggalakhir,suratsakit.lamasakit,"
                    + "ifnull((select berkas_digital_perawatan.lokasi_file from berkas_digital_perawatan "
                    + "where berkas_digital_perawatan.no_rawat=suratsakit.no_rawat and berkas_digital_perawatan.kode='064' limit 1),'') as file_tte "
                    + "from suratsakit inner join reg_periksa on suratsakit.no_rawat=reg_periksa.no_rawat "
                    + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis ";
            if (TCari.getText().trim().equals("")) {
                ps = koneksi.prepareStatement(sqlSelect + "where " + tgl + "order by suratsakit.no_surat");
            } else {
                ps = koneksi.prepareStatement(
                        sqlSelect + "where " + tgl + "and (no_surat like ? or suratsakit.no_rawat like ? or reg_periksa.no_rkm_medis like ? or pasien.nm_pasien like ? or suratsakit.tanggalawal like ? or suratsakit.tanggalakhir like ?) "
                        + "order by suratsakit.no_surat");
                String q = "%" + TCari.getText().trim() + "%";
                for (int k = 1; k <= 6; k++) ps.setString(k, q);
            }

            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    String fileTte = rs.getString(8);
                    String statusTte = (fileTte != null && !fileTte.trim().isEmpty()) ? "Sudah TTE" : "Belum TTE";
                    tabMode.addRow(new String[]{
                        rs.getString(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getString(6),
                        rs.getString(7), statusTte
                    });
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
        LamaSakit.setText("1 (Satu)");
        TanggalAwal.setDate(new Date());
        TanggalAkhir.setDate(new Date());
        Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(suratsakit.no_surat,3),signed)),0) from suratsakit where suratsakit.tanggalawal='" + Valid.SetTgl(TanggalAwal.getSelectedItem() + "") + "' ",
                "SKS" + TanggalAwal.getSelectedItem().toString().substring(6, 10) + TanggalAwal.getSelectedItem().toString().substring(3, 5) + TanggalAwal.getSelectedItem().toString().substring(0, 2), 3, NoSurat);
        NoSurat.requestFocus();
    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            NoSurat.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
            Valid.SetTgl(TanggalAwal, tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
            Valid.SetTgl(TanggalAkhir, tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            LamaSakit.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());

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
            PanelInput.setPreferredSize(new Dimension(WIDTH, 96));
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
        BtnSimpan.setEnabled(akses.getsurat_sakit());
        BtnHapus.setEnabled(akses.getsurat_sakit());
        BtnEdit.setEnabled(akses.getsurat_sakit());
    }
}
