package bridging;

import bridging.ApiApotekBPJS;
import bridging.ApotekBPJSDaftarPelayananObat2;
import bridging.BPJSDataSEP;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.swing.JOptionPane;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author dosen
 */
public final class ApotekBPJSDaftarPelayananObat3 extends javax.swing.JDialog {
    private final DefaultTableModel tabMode, tabMode2;
    private validasi Valid = new validasi();
    private sekuel Sequel = new sekuel();
    private int i = 0, y = 0, reply = 0;
    private ApiApotekBPJS api = new ApiApotekBPJS();
    private String URL = "", link = "", utc = "", requestJson = "", notif = "";
    private HttpHeaders headers;
    private HttpEntity requestEntity;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode nameNode;
    private JsonNode response;
    private Connection koneksi = koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;

    /**
     * Creates new form DlgKamar
     * 
     * @param parent
     * @param modal
     */
    public ApotekBPJSDaftarPelayananObat3(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        kosongkanKeteranganResep();

        this.setLocation(10, 2);
        setSize(628, 674);

        tabMode = new DefaultTableModel(null, new String[] {
                "No.SEP Apotek", // 0
                "No.SEP Kunjungan", // 1
                "No.Resep", // 2
                "", // 3
                "Jenis Resep", // 4
                "No.Kartu", // 5
                "Nama Peserta", // 6
                "Kode Jenis", // 7
                "Jenis Obat", // 8
                "Tgl.Pelayanan", // 9
                "Iterasi", // 10
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbResep.setModel(tabMode);

        tbResep.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbResep.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < tabMode.getColumnCount(); i++) {
            TableColumn column = tbResep.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(130);
            } else if (i == 1) {
                column.setPreferredWidth(130);
            } else if (i == 2) {
                column.setPreferredWidth(60);
            } else if (i == 3) {
                column.setPreferredWidth(80);
            } else if (i == 4) {
                column.setPreferredWidth(160);
            } else if (i == 5) {
                column.setPreferredWidth(100);
            } else if (i == 6) {
                column.setPreferredWidth(180);
            } else if (i == 7) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
                // column.setPreferredWidth(65);
            } else if (i == 8) {
                column.setPreferredWidth(150);
            } else if (i == 9) {
                column.setPreferredWidth(100);
            } else if (i == 10) {
                column.setPreferredWidth(140);
            }
        }
        tbResep.setDefaultRenderer(Object.class, new WarnaTable());

        tabMode2 = new DefaultTableModel(null, new String[] {
                "Kode Obat", // 0
                "Kode Obat BPJS", // 1
                "Nama Obat", // 2
                "Nama Obat BPJS", // 3
                "Signa 1", // 4
                "Signa 2", // 5
                "Sedia", // 6
                "Perm. Racikan", // 7
                "Jml Hari", // 8
                "Jml Obat", // 9
                "No Racikan", // 10
                "Catatan" // 11
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbRincianObat.setModel(tabMode2);

        tbRincianObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbRincianObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < tabMode2.getColumnCount(); i++) {
            TableColumn column = tbRincianObat.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(90);
            } else if (i == 1) {
                column.setPreferredWidth(150);
            } else if (i == 2) {
                column.setPreferredWidth(90);
            } else if (i == 3) {
                column.setPreferredWidth(150);
            } else if (i == 4) {
                column.setPreferredWidth(70);
            } else if (i == 5) {
                column.setPreferredWidth(70);
            } else if (i == 6) {
                column.setPreferredWidth(70);
            } else if (i == 7) {
                column.setPreferredWidth(70);
            } else if (i == 8) {
                column.setPreferredWidth(70);
            } else if (i == 9) {
                column.setPreferredWidth(70);
            } else if (i == 10) {
                column.setPreferredWidth(70);
            } else if (i == 11) {
                column.setPreferredWidth(90);
            }
        }
        tbResep.setDefaultRenderer(Object.class, new WarnaTable());

        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));

        try {
            link = koneksiDB.URLAPIAPOTEKBPJS();
        } catch (Exception e) {
            System.out.println("E : " + e);
        }

    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupResep = new javax.swing.JPopupMenu();
        mnHapusResep = new javax.swing.JMenuItem();
        mnLihatIterasi = new javax.swing.JMenuItem();
        popupObat = new javax.swing.JPopupMenu();
        mnHapusObat = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        panelGlass6 = new widget.panelisi();
        BtnKeluar = new widget.Button();
        panelBiasa1 = new widget.PanelBiasa();
        panelBiasa4 = new widget.PanelBiasa();
        Scroll = new widget.ScrollPane();
        tbResep = new widget.Table();
        panelBiasa5 = new widget.PanelBiasa();
        jLabel22 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel16 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        panelBiasa2 = new widget.PanelBiasa();
        Scroll1 = new widget.ScrollPane();
        tbRincianObat = new widget.Table();
        panelBiasa3 = new widget.PanelBiasa();
        jLabel1 = new javax.swing.JLabel();
        labelNoApotek = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        labelNoSEP = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        labelTglPelayanan = new javax.swing.JLabel();
        labelJenisResep = new javax.swing.JLabel();
        labelNoResep = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        labelIterasi = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        BtnCopyNoSEP = new widget.Button();
        BtnCopyNoApotek = new widget.Button();
        labelNoResepTerkirim = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        BtnCopyNoResepTerkirim = new widget.Button();
        BtnCopyNoResep = new widget.Button();
        panelBiasa6 = new widget.PanelBiasa();
        jLabel14 = new widget.Label();
        LCount1 = new widget.Label();

        popupResep.setName("popupResep"); // NOI18N

        mnHapusResep.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        mnHapusResep.setText("Hapus Resep");
        mnHapusResep.setName("mnHapusResep"); // NOI18N
        mnHapusResep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnHapusResepActionPerformed(evt);
            }
        });
        popupResep.add(mnHapusResep);

        mnLihatIterasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        mnLihatIterasi.setText("Lihat Iterasi");
        mnLihatIterasi.setName("mnLihatIterasi"); // NOI18N
        mnLihatIterasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnLihatIterasiActionPerformed(evt);
            }
        });
        popupResep.add(mnLihatIterasi);

        popupObat.setName("popupObat"); // NOI18N

        mnHapusObat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        mnHapusObat.setText("Hapus Obat");
        mnHapusObat.setName("mnHapusObat"); // NOI18N
        mnHapusObat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnHapusObatActionPerformed(evt);
            }
        });
        popupObat.add(mnHapusObat);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(null);
        setIconImages(null);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
                "::[ Daftar Pelayanan Obat Apotek BPJS (Integrasi) ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Segoe UI", 0, 12), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass6.setName("panelGlass6"); // NOI18N
        panelGlass6.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 6));

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
        panelGlass6.add(BtnKeluar);

        internalFrame1.add(panelGlass6, java.awt.BorderLayout.PAGE_END);

        panelBiasa1.setName("panelBiasa1"); // NOI18N
        panelBiasa1.setLayout(new java.awt.BorderLayout());

        panelBiasa4.setName("panelBiasa4"); // NOI18N
        panelBiasa4.setLayout(new java.awt.BorderLayout());

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbResep.setAutoCreateRowSorter(true);
        tbResep.setComponentPopupMenu(popupResep);
        tbResep.setName("tbResep"); // NOI18N
        tbResep.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbResepMouseClicked(evt);
            }
        });
        tbResep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbResepKeyReleased(evt);
            }
        });
        Scroll.setViewportView(tbResep);

        panelBiasa4.add(Scroll, java.awt.BorderLayout.CENTER);

        panelBiasa5.setName("panelBiasa5"); // NOI18N
        panelBiasa5.setPreferredSize(new java.awt.Dimension(12, 44));
        panelBiasa5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 6));

        jLabel22.setText("Tanggal :");
        jLabel22.setName("jLabel22"); // NOI18N
        jLabel22.setPreferredSize(new java.awt.Dimension(60, 16));
        panelBiasa5.add(jLabel22);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10-10-2025" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelBiasa5.add(DTPCari1);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelBiasa5.add(jLabel21);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10-10-2025" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelBiasa5.add(DTPCari2);

        jLabel16.setText("Keyword :");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setPreferredSize(new java.awt.Dimension(70, 23));
        panelBiasa5.add(jLabel16);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(240, 23));
        TCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TCariActionPerformed(evt);
            }
        });
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelBiasa5.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setText("Cari");
        BtnCari.setToolTipText("");
        BtnCari.setName("BtnCari"); // NOI18N
        BtnCari.setPreferredSize(new java.awt.Dimension(70, 30));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        panelBiasa5.add(BtnCari);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(65, 23));
        panelBiasa5.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(50, 23));
        panelBiasa5.add(LCount);

        panelBiasa4.add(panelBiasa5, java.awt.BorderLayout.PAGE_END);

        panelBiasa1.add(panelBiasa4, java.awt.BorderLayout.CENTER);

        panelBiasa2.setName("panelBiasa2"); // NOI18N
        panelBiasa2.setPreferredSize(new java.awt.Dimension(700, 700));
        panelBiasa2.setLayout(new java.awt.BorderLayout());

        Scroll1.setName("Scroll1"); // NOI18N
        Scroll1.setOpaque(true);

        tbRincianObat.setAutoCreateRowSorter(true);
        tbRincianObat.setComponentPopupMenu(popupObat);
        tbRincianObat.setName("tbRincianObat"); // NOI18N
        Scroll1.setViewportView(tbRincianObat);

        panelBiasa2.add(Scroll1, java.awt.BorderLayout.CENTER);

        panelBiasa3.setName("panelBiasa3"); // NOI18N
        panelBiasa3.setPreferredSize(new java.awt.Dimension(12, 165));
        panelBiasa3.setLayout(null);

        jLabel1.setText("Jenis Resep");
        jLabel1.setName("jLabel1"); // NOI18N
        panelBiasa3.add(jLabel1);
        jLabel1.setBounds(20, 90, 90, 16);

        labelNoApotek.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelNoApotek.setText("-");
        labelNoApotek.setName("labelNoApotek"); // NOI18N
        panelBiasa3.add(labelNoApotek);
        labelNoApotek.setBounds(140, 10, 150, 16);

        jLabel3.setText("Tgl Pelayanan");
        jLabel3.setName("jLabel3"); // NOI18N
        panelBiasa3.add(jLabel3);
        jLabel3.setBounds(20, 50, 90, 16);

        jLabel4.setText("No Resep");
        jLabel4.setName("jLabel4"); // NOI18N
        panelBiasa3.add(jLabel4);
        jLabel4.setBounds(20, 70, 90, 16);

        jLabel5.setText(":");
        jLabel5.setName("jLabel5"); // NOI18N
        panelBiasa3.add(jLabel5);
        jLabel5.setBounds(120, 90, 10, 16);

        jLabel6.setText(":");
        jLabel6.setName("jLabel6"); // NOI18N
        panelBiasa3.add(jLabel6);
        jLabel6.setBounds(120, 10, 10, 16);

        jLabel8.setText(":");
        jLabel8.setName("jLabel8"); // NOI18N
        panelBiasa3.add(jLabel8);
        jLabel8.setBounds(120, 50, 10, 16);

        jLabel9.setText(":");
        jLabel9.setName("jLabel9"); // NOI18N
        panelBiasa3.add(jLabel9);
        jLabel9.setBounds(340, 70, 10, 16);

        jLabel10.setText("No Apotek");
        jLabel10.setName("jLabel10"); // NOI18N
        panelBiasa3.add(jLabel10);
        jLabel10.setBounds(20, 10, 90, 16);

        labelNoSEP.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelNoSEP.setText("-");
        labelNoSEP.setName("labelNoSEP"); // NOI18N
        panelBiasa3.add(labelNoSEP);
        labelNoSEP.setBounds(140, 30, 150, 16);

        jLabel11.setText(":");
        jLabel11.setName("jLabel11"); // NOI18N
        panelBiasa3.add(jLabel11);
        jLabel11.setBounds(120, 30, 10, 16);

        jLabel12.setText("No SEP");
        jLabel12.setName("jLabel12"); // NOI18N
        panelBiasa3.add(jLabel12);
        jLabel12.setBounds(20, 30, 90, 16);

        labelTglPelayanan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelTglPelayanan.setText("-");
        labelTglPelayanan.setName("labelTglPelayanan"); // NOI18N
        panelBiasa3.add(labelTglPelayanan);
        labelTglPelayanan.setBounds(140, 50, 150, 16);

        labelJenisResep.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelJenisResep.setText("-");
        labelJenisResep.setName("labelJenisResep"); // NOI18N
        panelBiasa3.add(labelJenisResep);
        labelJenisResep.setBounds(140, 90, 240, 16);

        labelNoResep.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelNoResep.setText("-");
        labelNoResep.setName("labelNoResep"); // NOI18N
        panelBiasa3.add(labelNoResep);
        labelNoResep.setBounds(140, 70, 100, 16);

        jLabel2.setText("Iterasi");
        jLabel2.setName("jLabel2"); // NOI18N
        panelBiasa3.add(jLabel2);
        jLabel2.setBounds(20, 110, 90, 16);

        jLabel13.setText(":");
        jLabel13.setName("jLabel13"); // NOI18N
        panelBiasa3.add(jLabel13);
        jLabel13.setBounds(120, 110, 10, 16);

        labelIterasi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelIterasi.setText("-");
        labelIterasi.setName("labelIterasi"); // NOI18N
        panelBiasa3.add(labelIterasi);
        labelIterasi.setBounds(140, 110, 240, 16);

        jLabel15.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel15.setText("(Silahkan pilih resep yang akan ditampilkan rincian obatnya pada tabel sebelah kiri)");
        jLabel15.setName("jLabel15"); // NOI18N
        panelBiasa3.add(jLabel15);
        jLabel15.setBounds(20, 140, 460, 16);

        BtnCopyNoSEP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/copy.png"))); // NOI18N
        BtnCopyNoSEP.setMnemonic('6');
        BtnCopyNoSEP.setToolTipText("Salin No. Rawat");
        BtnCopyNoSEP.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        BtnCopyNoSEP.setIconTextGap(0);
        BtnCopyNoSEP.setName("BtnCopyNoSEP"); // NOI18N
        BtnCopyNoSEP.setPreferredSize(new java.awt.Dimension(16, 16));
        BtnCopyNoSEP.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCopyNoSEP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCopyNoSEPActionPerformed(evt);
            }
        });
        BtnCopyNoSEP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCopyNoSEPKeyPressed(evt);
            }
        });
        panelBiasa3.add(BtnCopyNoSEP);
        BtnCopyNoSEP.setBounds(300, 30, 16, 16);

        BtnCopyNoApotek.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/copy.png"))); // NOI18N
        BtnCopyNoApotek.setMnemonic('6');
        BtnCopyNoApotek.setToolTipText("Salin No. Rawat");
        BtnCopyNoApotek.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        BtnCopyNoApotek.setIconTextGap(0);
        BtnCopyNoApotek.setName("BtnCopyNoApotek"); // NOI18N
        BtnCopyNoApotek.setPreferredSize(new java.awt.Dimension(16, 16));
        BtnCopyNoApotek.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCopyNoApotek.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCopyNoApotekActionPerformed(evt);
            }
        });
        BtnCopyNoApotek.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCopyNoApotekKeyPressed(evt);
            }
        });
        panelBiasa3.add(BtnCopyNoApotek);
        BtnCopyNoApotek.setBounds(300, 10, 16, 16);

        labelNoResepTerkirim.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        labelNoResepTerkirim.setText("-");
        labelNoResepTerkirim.setName("labelNoResepTerkirim"); // NOI18N
        panelBiasa3.add(labelNoResepTerkirim);
        labelNoResepTerkirim.setBounds(350, 70, 50, 16);

        jLabel18.setText("Terkirim");
        jLabel18.setName("jLabel18"); // NOI18N
        panelBiasa3.add(jLabel18);
        jLabel18.setBounds(280, 70, 70, 16);

        jLabel19.setText(":");
        jLabel19.setName("jLabel19"); // NOI18N
        panelBiasa3.add(jLabel19);
        jLabel19.setBounds(120, 70, 10, 16);

        BtnCopyNoResepTerkirim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/copy.png"))); // NOI18N
        BtnCopyNoResepTerkirim.setMnemonic('6');
        BtnCopyNoResepTerkirim.setToolTipText("Salin No. Rawat");
        BtnCopyNoResepTerkirim.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        BtnCopyNoResepTerkirim.setIconTextGap(0);
        BtnCopyNoResepTerkirim.setName("BtnCopyNoResepTerkirim"); // NOI18N
        BtnCopyNoResepTerkirim.setPreferredSize(new java.awt.Dimension(16, 16));
        BtnCopyNoResepTerkirim.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCopyNoResepTerkirim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCopyNoResepTerkirimActionPerformed(evt);
            }
        });
        BtnCopyNoResepTerkirim.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCopyNoResepTerkirimKeyPressed(evt);
            }
        });
        panelBiasa3.add(BtnCopyNoResepTerkirim);
        BtnCopyNoResepTerkirim.setBounds(400, 70, 16, 16);

        BtnCopyNoResep.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/copy.png"))); // NOI18N
        BtnCopyNoResep.setMnemonic('6');
        BtnCopyNoResep.setToolTipText("Salin No. Rawat");
        BtnCopyNoResep.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        BtnCopyNoResep.setIconTextGap(0);
        BtnCopyNoResep.setName("BtnCopyNoResep"); // NOI18N
        BtnCopyNoResep.setPreferredSize(new java.awt.Dimension(16, 16));
        BtnCopyNoResep.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCopyNoResep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCopyNoResepActionPerformed(evt);
            }
        });
        BtnCopyNoResep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCopyNoResepKeyPressed(evt);
            }
        });
        panelBiasa3.add(BtnCopyNoResep);
        BtnCopyNoResep.setBounds(240, 70, 16, 16);

        panelBiasa2.add(panelBiasa3, java.awt.BorderLayout.PAGE_START);

        panelBiasa6.setName("panelBiasa6"); // NOI18N
        panelBiasa6.setPreferredSize(new java.awt.Dimension(12, 44));
        panelBiasa6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel14.setText("Record :");
        jLabel14.setName("jLabel14"); // NOI18N
        jLabel14.setPreferredSize(new java.awt.Dimension(65, 23));
        panelBiasa6.add(jLabel14);

        LCount1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount1.setText("0");
        LCount1.setName("LCount1"); // NOI18N
        LCount1.setPreferredSize(new java.awt.Dimension(50, 23));
        panelBiasa6.add(LCount1);

        panelBiasa2.add(panelBiasa6, java.awt.BorderLayout.PAGE_END);

        panelBiasa1.add(panelBiasa2, java.awt.BorderLayout.LINE_END);

        internalFrame1.add(panelBiasa1, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }// GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_BtnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            dispose();
        }
    }// GEN-LAST:event_BtnKeluarKeyPressed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_TCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampil();
        }
    }// GEN-LAST:event_TCariKeyPressed

    private void TCariActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_TCariActionPerformed
        // TODO add your handling code here:
    }// GEN-LAST:event_TCariActionPerformed

    private void mnHapusResepActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mnHapusResepActionPerformed
        if (tbResep.getSelectedRow() != -1) {
            String noApotek = tbResep.getValueAt(tbResep.getSelectedRow(), 0).toString();
            String noSep = tbResep.getValueAt(tbResep.getSelectedRow(), 1).toString();
            String noResep = tbResep.getValueAt(tbResep.getSelectedRow(), 2).toString();
            String iterasi = tbResep.getValueAt(tbResep.getSelectedRow(), 10).toString().substring(0, 1);
            Boolean sukses = false;
            if (!iterasi.equals("0")) {
                if (Sequel.cariInteger("select count(*) from bridging_apotek_bpjs where no_sep=? and iterasi='0'",
                        noSep) > 0) {
                    sukses = false;
                } else {
                    sukses = true;
                }
            } else {
                sukses = true;
            }

            if (sukses) {
                reply = JOptionPane.showConfirmDialog(rootPane,
                        "Apakah anda yakin akan menghapus No. Apotek " + noApotek
                                + " dan semua obat yang terdapat di dalamnya?",
                        "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    try {
                        ps = koneksi.prepareStatement(
                                "select * from bridging_apotek_bpjs_obat where no_apotek='" + noApotek + "'");
                        rs = ps.executeQuery();
                        while (rs.next()) {
                            if (bodyWithDeleteRequest(noApotek, noResep, rs.getString("kd_obat_bpjs"),
                                    rs.getString("no_racikan"))) {
                                Sequel.meghapus("bridging_apotek_bpjs_obat", "no_apotek", "kd_obat_bpjs", noApotek,
                                        rs.getString("kd_obat_bpjs"));
                                JOptionPane.showMessageDialog(null, "Berhasil menghapus obat "
                                        + rs.getString("nm_obat_bpjs") + " dengan No. Apotek " + noApotek + ".");
                            }
                        }

                        if (Sequel
                                .cariIsi("select no_apotek from bridging_apotek_bpjs_obat where no_apotek=?", noApotek)
                                .isEmpty()) {
                            if (bodyWithDeleteRequestResep(noApotek, noSep, noResep)) {
                                Sequel.meghapus("bridging_apotek_bpjs", "no_apotek", noApotek);
                                JOptionPane.showMessageDialog(null, "Berhasil menghapus No. Apotek " + noApotek
                                        + " dan seluruh obat yang tersimpan.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Tidak dapat menghapus No. Apotek " + noApotek
                                    + " karena masih terdapat beberapa obat di dalamnya.");
                        }

                        kosongkanKeteranganResep();
                        tampil();
                    } catch (Exception ex) {
                        System.out.println("Notifikasi Bridging : " + ex);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null,
                        "Tidak dapat menghapus resep. \nResep ini teridentifikasi sebagai Resep Induk (memiliki iterasi). \nTerdapat nomor apotek dengan Nomor SEP Kunjungan yang sama.\nSilahkan hapus resep yang terkait terlebih dahulu.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Silahkan pilih dulu data yang mau dihapus..!!");
        }
    }// GEN-LAST:event_mnHapusResepActionPerformed

    private void tbResepMouseClicked(java.awt.event.MouseEvent evt) {// GEN-FIRST:event_tbResepMouseClicked
        if (tabMode.getRowCount() != 0) {
            getDataResep();
        }
    }// GEN-LAST:event_tbResepMouseClicked

    private void tbResepKeyReleased(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_tbResepKeyReleased
        if (tabMode.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP)
                    || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                getDataResep();
            }
        }
    }// GEN-LAST:event_tbResepKeyReleased

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_BtnCariActionPerformed
        tampil();
        kosongkanKeteranganResep();
    }// GEN-LAST:event_BtnCariActionPerformed

    private void kosongkanKeteranganResep() {
        labelNoApotek.setText("-");
        labelNoSEP.setText("-");
        labelTglPelayanan.setText("-");
        labelNoResep.setText("-");
        labelNoResepTerkirim.setText("-");
        labelJenisResep.setText("-");
        labelIterasi.setText("-");

        LCount1.setText("0");

        BtnCopyNoApotek.setVisible(false);
        BtnCopyNoSEP.setVisible(false);
        BtnCopyNoResep.setVisible(false);
        BtnCopyNoResepTerkirim.setVisible(false);
    }

    private void mnHapusObatActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mnHapusObatActionPerformed
        if (tbRincianObat.getSelectedRow() != -1) {
            String kodeObat = tbRincianObat.getValueAt(tbRincianObat.getSelectedRow(), 1).toString();
            String namaObat = tbRincianObat.getValueAt(tbRincianObat.getSelectedRow(), 2).toString();
            String noApotek = labelNoApotek.getText();
            String tipeObat = tbRincianObat.getValueAt(tbRincianObat.getSelectedRow(), 10).toString();
            String noResep = labelNoResepTerkirim.getText();

            reply = JOptionPane.showConfirmDialog(rootPane,
                    "Apakah anda yakin akan menghapus obat " + namaObat + " dengan No. Apotek " + noApotek + "?",
                    "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (reply == JOptionPane.YES_OPTION) {
                try {
                    if (bodyWithDeleteRequestPerobat(noApotek, noResep, kodeObat, tipeObat)) {
                        Sequel.meghapus("bridging_apotek_bpjs_obat", "no_apotek", "kd_obat_bpjs", noApotek, kodeObat);
                        JOptionPane.showMessageDialog(null,
                                "Berhasil menghapus obat " + namaObat + " dengan No. Apotek " + noApotek + ".");
                    }

                    if (Sequel.cariInteger("select count(*) from bridging_apotek_bpjs_obat where no_apotek=?",
                            noApotek) == 0) {
                        JOptionPane.showMessageDialog(null,
                                "Seluruh obat dari resep yang anda pilih telah habis. Resep ini diidentifikasi sebagai Resep Kosong.");
                        Sequel.mengedit("bridging_apotek_bpjs", "no_apotek='" + noApotek + "'", "jenis_resep='Kosong'");
                    }
                    tampil2();
                } catch (Exception ex) {
                    Logger.getLogger(ApotekBPJSDaftarPelayananObat2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Silahkan pilih dulu data yang mau dihapus..!!");
        }
    }// GEN-LAST:event_mnHapusObatActionPerformed

    private void BtnCopyNoSEPActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_BtnCopyNoSEPActionPerformed
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(labelNoSEP.getText()), null);
    }// GEN-LAST:event_BtnCopyNoSEPActionPerformed

    private void BtnCopyNoSEPKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_BtnCopyNoSEPKeyPressed
        // TODO add your handling code here:
    }// GEN-LAST:event_BtnCopyNoSEPKeyPressed

    private void BtnCopyNoApotekActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_BtnCopyNoApotekActionPerformed
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(labelNoApotek.getText()), null);
    }// GEN-LAST:event_BtnCopyNoApotekActionPerformed

    private void BtnCopyNoApotekKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_BtnCopyNoApotekKeyPressed
        // TODO add your handling code here:
    }// GEN-LAST:event_BtnCopyNoApotekKeyPressed

    private void BtnCopyNoResepTerkirimActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_BtnCopyNoResepTerkirimActionPerformed
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(labelNoResepTerkirim.getText()), null);
    }// GEN-LAST:event_BtnCopyNoResepTerkirimActionPerformed

    private void BtnCopyNoResepTerkirimKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_BtnCopyNoResepTerkirimKeyPressed
        // TODO add your handling code here:
    }// GEN-LAST:event_BtnCopyNoResepTerkirimKeyPressed

    private void BtnCopyNoResepActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_BtnCopyNoResepActionPerformed
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                new StringSelection(labelNoResep.getText()), null);
    }// GEN-LAST:event_BtnCopyNoResepActionPerformed

    private void BtnCopyNoResepKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_BtnCopyNoResepKeyPressed
        // TODO add your handling code here:
    }// GEN-LAST:event_BtnCopyNoResepKeyPressed

    private void mnLihatIterasiActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mnLihatIterasiActionPerformed
        if (!tbResep.getValueAt(tbResep.getSelectedRow(), 10).toString().substring(0, 1).equals("0")) {
            Valid.SetTgl(DTPCari1, tbResep.getValueAt(tbResep.getSelectedRow(), 9).toString());
            Valid.SetTgl(DTPCari2, new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            TCari.setText(tbResep.getValueAt(tbResep.getSelectedRow(), 1).toString());
            tampil();
        } else {
            JOptionPane.showMessageDialog(null,
                    "Resep yang dipilih bukan resep iterasi. Otomatis mencari resep induk...");

            String tglPelayanan = Sequel.cariIsi(
                    "select ifnull(tgl_pelayanan,'') from bridging_apotek_bpjs where no_sep=?",
                    tbResep.getValueAt(tbResep.getSelectedRow(), 1).toString());
            if (tglPelayanan.equals("")) {
                JOptionPane.showMessageDialog(null,
                        "Tidak dapat menemukan resep induk. Resep ini bukan resep iterasi.");
            } else {
                Valid.SetTgl(DTPCari1, tglPelayanan);
                Valid.SetTgl(DTPCari2, tbResep.getValueAt(tbResep.getSelectedRow(), 9).toString());
                TCari.setText(tbResep.getValueAt(tbResep.getSelectedRow(), 1).toString());
                tampil();
            }
        }
    }// GEN-LAST:event_mnLihatIterasiActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            ApotekBPJSDaftarPelayananObat2 dialog = new ApotekBPJSDaftarPelayananObat2(new javax.swing.JFrame(), true);
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
    private widget.Button BtnCopyNoApotek;
    private widget.Button BtnCopyNoResep;
    private widget.Button BtnCopyNoResepTerkirim;
    private widget.Button BtnCopyNoSEP;
    private widget.Button BtnKeluar;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.Label LCount;
    private widget.Label LCount1;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll1;
    private widget.TextBox TCari;
    private widget.InternalFrame internalFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private widget.Label jLabel14;
    private javax.swing.JLabel jLabel15;
    private widget.Label jLabel16;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private widget.Label jLabel21;
    private widget.Label jLabel22;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private widget.Label jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel labelIterasi;
    private javax.swing.JLabel labelJenisResep;
    private javax.swing.JLabel labelNoApotek;
    private javax.swing.JLabel labelNoResep;
    private javax.swing.JLabel labelNoResepTerkirim;
    private javax.swing.JLabel labelNoSEP;
    private javax.swing.JLabel labelTglPelayanan;
    private javax.swing.JMenuItem mnHapusObat;
    private javax.swing.JMenuItem mnHapusResep;
    private javax.swing.JMenuItem mnLihatIterasi;
    private widget.PanelBiasa panelBiasa1;
    private widget.PanelBiasa panelBiasa2;
    private widget.PanelBiasa panelBiasa3;
    private widget.PanelBiasa panelBiasa4;
    private widget.PanelBiasa panelBiasa5;
    private widget.PanelBiasa panelBiasa6;
    private widget.panelisi panelGlass6;
    private javax.swing.JPopupMenu popupObat;
    private javax.swing.JPopupMenu popupResep;
    private widget.Table tbResep;
    private widget.Table tbRincianObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        Valid.tabelKosong(tabMode2);
        kosongkanKeteranganResep();

        try {
            String sql = "SELECT bridging_apotek_bpjs.no_apotek, bridging_apotek_bpjs.no_sep, bridging_apotek_bpjs.no_resep,bridging_apotek_bpjs.no_resep_terkirim,bridging_apotek_bpjs.jenis_resep, bridging_sep.no_kartu,bridging_sep.nama_pasien,bridging_apotek_bpjs.status_lanjut, "
                    +
                    "bridging_apotek_bpjs.jenis_obat,bridging_apotek_bpjs.tgl_pelayanan,bridging_apotek_bpjs.iterasi, bridging_apotek_bpjs.tgl_resep FROM bridging_apotek_bpjs JOIN bridging_sep ON "
                    +
                    "bridging_apotek_bpjs.no_sep=bridging_sep.no_sep WHERE bridging_apotek_bpjs.tgl_pelayanan BETWEEN ? and ? "
                    +
                    (TCari.getText().trim().equals("") ? ""
                            : "and (bridging_apotek_bpjs.no_apotek like ? or bridging_sep.no_sep like ? or bridging_sep.nama_pasien like ? or bridging_sep.no_kartu like ? or bridging_apotek_bpjs.no_resep like ?) ");

            ps = koneksi.prepareStatement(sql);
            try {
                ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
                ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
                if (!TCari.getText().trim().equals("")) {
                    ps.setString(3, "%" + TCari.getText().trim() + "%");
                    ps.setString(4, "%" + TCari.getText().trim() + "%");
                    ps.setString(5, "%" + TCari.getText().trim() + "%");
                    ps.setString(6, "%" + TCari.getText().trim() + "%");
                    ps.setString(7, "%" + TCari.getText().trim() + "%");
                }
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[] {
                            rs.getString("no_apotek"), rs.getString("no_sep"), rs.getString("no_resep_terkirim"),
                            rs.getString("no_resep"),
                            rs.getString("jenis_resep") + " (" + rs.getString("status_lanjut") + ")",
                            rs.getString("no_kartu"), rs.getString("nama_pasien"), rs.getString("jenis_obat"),
                            (rs.getString("jenis_obat").equals("1") ? "Obat PRB"
                                    : (rs.getString("jenis_obat").equals("2") ? "Obat Kronis Belum Stabil"
                                            : "Obat Kronis Stabil")),
                            rs.getString("tgl_pelayanan"),
                            rs.getString("iterasi") + ". " + (rs.getInt("iterasi") > 0 ? "Iterasi" : "Tanpa Iterasi")
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

    public void tampil2() {
        Valid.tabelKosong(tabMode2);

        try {
            ps = koneksi.prepareStatement(
                    "select * from bridging_apotek_bpjs_obat where no_apotek=?");
            try {
                ps.setString(1, labelNoApotek.getText());
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode2.addRow(new String[] {
                            rs.getString("kd_obat"),
                            rs.getString("kd_obat_bpjs"),
                            rs.getString("nm_obat"),
                            rs.getString("nm_obat_bpjs"),
                            rs.getString("signa1"),
                            rs.getString("signa2"),
                            rs.getString("sedia"),
                            rs.getString("perm_racikan"),
                            rs.getString("jho"),
                            rs.getString("jml_obat"),
                            rs.getString("no_racikan"),
                            rs.getString("catatan")
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
            System.out.println("Notifikasi Resep : " + e);
        }
        LCount1.setText("" + tabMode2.getRowCount());
    }

    private void getDataResep() {
        labelNoApotek.setText(tbResep.getValueAt(tbResep.getSelectedRow(), 0).toString());
        labelNoSEP.setText(tbResep.getValueAt(tbResep.getSelectedRow(), 1).toString());
        labelTglPelayanan.setText(tbResep.getValueAt(tbResep.getSelectedRow(), 9).toString());
        labelJenisResep.setText(tbResep.getValueAt(tbResep.getSelectedRow(), 4).toString());
        labelIterasi.setText(tbResep.getValueAt(tbResep.getSelectedRow(), 10).toString());
        labelNoResep.setText(tbResep.getValueAt(tbResep.getSelectedRow(), 3).toString());
        labelNoResepTerkirim.setText(tbResep.getValueAt(tbResep.getSelectedRow(), 2).toString());

        BtnCopyNoSEP.setVisible(true);
        BtnCopyNoApotek.setVisible(true);
        BtnCopyNoResep.setVisible(true);
        BtnCopyNoResepTerkirim.setVisible(true);

        tampil2();
    }

    public JTable getTable() {
        return tbResep;
    }

    public static class HttpEntityEnclosingDeleteRequest extends HttpEntityEnclosingRequestBase {
        public HttpEntityEnclosingDeleteRequest(final URI uri) {
            super();
            setURI(uri);
        }

        @Override
        public String getMethod() {
            return "DELETE";
        }
    }

    @Test
    public boolean bodyWithDeleteRequest(String noSjp, String noResep, String kodeObat, String tipeObat)
            throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        javax.net.ssl.TrustManager[] trustManagers = {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    }

                    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    }
                }
        };
        sslContext.init(null, trustManagers, new SecureRandom());
        SSLSocketFactory sslFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        Scheme scheme = new Scheme("https", 443, sslFactory);

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory() {
            @Override
            protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
                if (HttpMethod.DELETE == httpMethod) {
                    return new HttpEntityEnclosingDeleteRequest(uri);
                }
                return super.createHttpUriRequest(httpMethod, uri);
            }
        };
        factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(scheme);
        restTemplate.setRequestFactory(factory);

        Boolean result = false;
        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIAPOTEKBPJS());
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIAPOTEKBPJS());
            URL = link + "/pelayanan/obat/hapus";
            System.out.println("URL : " + URL);
            requestJson = "{ " +
                    "\"nosepapotek\":\"" + noSjp + "\"," +
                    "\"noresep\":\"" + noResep + "\"," +
                    "\"kodeobat\":\"" + kodeObat + "\"," +
                    "\"tipeobat\":\"" + tipeObat + "\"}  ";
            System.out.println("respone : " + requestJson);
            requestEntity = new HttpEntity(requestJson, headers);
            root = mapper
                    .readTree(restTemplate.exchange(URL, HttpMethod.DELETE, requestEntity, String.class).getBody());
            nameNode = root.path("metaData");
            System.out.println("code : " + nameNode.path("code").asText());
            System.out.println("message : " + nameNode.path("message").asText());
            if (nameNode.path("code").asText().equals("200")) {
                result = true;
            } else {
                result = false;
                JOptionPane.showMessageDialog(null, nameNode.path("message").asText());
            }
        } catch (Exception e) {
            result = false;
            System.out.println("Notif : " + e);
            if (e.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
            }
        }

        return result;
    }

    @Test
    public boolean bodyWithDeleteRequestPerobat(String noSjp, String noResep, String kodeObat, String tipeObat)
            throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        javax.net.ssl.TrustManager[] trustManagers = {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    }

                    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    }
                }
        };
        sslContext.init(null, trustManagers, new SecureRandom());
        SSLSocketFactory sslFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        Scheme scheme = new Scheme("https", 443, sslFactory);

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory() {
            @Override
            protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
                if (HttpMethod.DELETE == httpMethod) {
                    return new HttpEntityEnclosingDeleteRequest(uri);
                }
                return super.createHttpUriRequest(httpMethod, uri);
            }
        };
        factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(scheme);
        restTemplate.setRequestFactory(factory);

        Boolean result = false;
        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIAPOTEKBPJS());
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIAPOTEKBPJS());
            URL = link + "/pelayanan/obat/hapus";
            System.out.println("URL : " + URL);
            requestJson = "{ " +
                    "\"nosepapotek\":\"" + noSjp + "\"," +
                    "\"noresep\":\"" + noResep + "\"," +
                    "\"kodeobat\":\"" + kodeObat + "\"," +
                    "\"tipeobat\":\"" + tipeObat + "\"}  ";
            System.out.println("respone : " + requestJson);
            requestEntity = new HttpEntity(requestJson, headers);
            root = mapper
                    .readTree(restTemplate.exchange(URL, HttpMethod.DELETE, requestEntity, String.class).getBody());
            nameNode = root.path("metaData");
            System.out.println("code : " + nameNode.path("code").asText());
            System.out.println("message : " + nameNode.path("message").asText());
            if (nameNode.path("code").asText().equals("200")) {
                result = true;
            } else {
                result = false;
                JOptionPane.showMessageDialog(null, nameNode.path("message").asText());
            }
        } catch (Exception e) {
            result = false;
            System.out.println("Notif : " + e);
            if (e.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
            }
        }

        return result;
    }

    @Test
    public boolean bodyWithDeleteRequestResep(String noSjp, String noSep, String noResep) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        javax.net.ssl.TrustManager[] trustManagers = {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    }

                    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    }
                }
        };
        sslContext.init(null, trustManagers, new SecureRandom());
        SSLSocketFactory sslFactory = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        Scheme scheme = new Scheme("https", 443, sslFactory);

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory() {
            @Override
            protected HttpUriRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
                if (HttpMethod.DELETE == httpMethod) {
                    return new BPJSDataSEP.HttpEntityEnclosingDeleteRequest(uri);
                }
                return super.createHttpUriRequest(httpMethod, uri);
            }
        };
        factory.getHttpClient().getConnectionManager().getSchemeRegistry().register(scheme);
        restTemplate.setRequestFactory(factory);

        Boolean result = false;
        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.add("X-Cons-ID", koneksiDB.CONSIDAPIAPOTEKBPJS());
            utc = String.valueOf(api.GetUTCdatetimeAsString());
            headers.add("X-Timestamp", utc);
            headers.add("X-Signature", api.getHmac(utc));
            headers.add("user_key", koneksiDB.USERKEYAPIAPOTEKBPJS());
            URL = link + "/hapusresep";
            requestJson = "{ " +
                    "\"nosjp\":\"" + noSjp + "\"," +
                    "\"refasalsjp\":\"" + noSep + "\"," +
                    "\"noresep\":\"" + noResep + "\"}  ";
            requestEntity = new HttpEntity(requestJson, headers);
            root = mapper
                    .readTree(restTemplate.exchange(URL, HttpMethod.DELETE, requestEntity, String.class).getBody());
            nameNode = root.path("metaData");
            System.out.println("code : " + nameNode.path("code").asText());
            System.out.println("message : " + nameNode.path("message").asText());
            if (nameNode.path("code").asText().equals("200")) {
                result = true;
            } else {
                result = false;
                JOptionPane.showMessageDialog(null, nameNode.path("message").asText());
            }
        } catch (Exception e) {
            result = false;
            System.out.println("Notif : " + e);
            if (e.toString().contains("UnknownHostException")) {
                JOptionPane.showMessageDialog(null, "Koneksi ke server BPJS terputus...!");
            }
        }
        return result;
    }

    public void setPencarian(String cari) {
        TCari.setText(cari);
        tampil();
    }
}
