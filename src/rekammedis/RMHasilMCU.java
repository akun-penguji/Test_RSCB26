/*
 * Kontribusi dari RSUD Cabang Bungin, Bekasi
 */
package rekammedis;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.apache.hc.core5.http.HttpEntity;
import bridging.UploadPDFTte;
import bridging.ViewPDFNextcloud;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariDokter;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.Timeout;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.core5.http.ContentType;

/**
 *
 * @author RSUD Cabang Bungin
 */
public final class RMHasilMCU extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private DlgCariDokter dokter = new DlgCariDokter(null, false);
    private StringBuilder htmlContent;
    private String finger = "", kodeberkas = "", json = "";
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;

    /**
     * Creates new form RMHasilMCU
     *
     * @param parent
     * @param modal
     */
    public RMHasilMCU(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "J.K.", "Kode Dokter", "Nama Dokter", "Tanggal", 
            "Riwayat Penyakit Sekarang", "Riwayat Penyakit Dahulu", "Riwayat Penyakit Keluarga", "Riwayat Kebiasaan",
            "Tanda Vital", "Berat/Tinggi Badan", "Kepala", "Mulut", "Leher", "THT", "Thorax", "Abdomen", "Ekstremitas",
            "Hematologi", "Urin", "Kimia Darah", "Radiologi", "EKG", "Kesimpulan", "Saran"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 28; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(105);
            } else if (i == 1) {
                column.setPreferredWidth(70);
            } else if (i == 2) {
                column.setPreferredWidth(150);
            } else if (i == 3) {
                column.setPreferredWidth(65);
            } else if (i == 4) {
                column.setPreferredWidth(55);
            } else if (i == 5) {
                column.setPreferredWidth(80);
            } else if (i == 6) {
                column.setPreferredWidth(150);
            } else if (i == 7) {
                column.setPreferredWidth(115);
            } else if (i == 8) {
                column.setPreferredWidth(200);
            } else if (i == 9) {
                column.setPreferredWidth(200);
            } else if (i == 10) {
                column.setPreferredWidth(200);
            } else if (i == 11) {
                column.setPreferredWidth(200);
            } else if (i == 12) {
                column.setPreferredWidth(150);
            } else if (i == 13) {
                column.setPreferredWidth(120);
            } else {
                column.setPreferredWidth(150);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        KdDokter.setDocument(new batasInput((byte) 20).getKata(KdDokter));
        RiwayatPenyakitSekarang.setDocument(new batasInput((int) 2000).getKata(RiwayatPenyakitSekarang));
        RiwayatPenyakitDahulu.setDocument(new batasInput((int) 2000).getKata(RiwayatPenyakitDahulu));
        RiwayatPenyakitKeluarga.setDocument(new batasInput((int) 2000).getKata(RiwayatPenyakitKeluarga));
        RiwayatKebiasaan.setDocument(new batasInput((int) 2000).getKata(RiwayatKebiasaan));
        TandaVital.setDocument(new batasInput((int) 2000).getKata(TandaVital));
        BeratTinggi.setDocument(new batasInput((int) 2000).getKata(BeratTinggi));
        KeadaanUmumKepala.setDocument(new batasInput((int) 2000).getKata(KeadaanUmumKepala));
        KeadaanUmumMulut.setDocument(new batasInput((int) 2000).getKata(KeadaanUmumMulut));
        KeadaanUmumLeher.setDocument(new batasInput((int) 2000).getKata(KeadaanUmumLeher));
        KeadaanUmumTHT.setDocument(new batasInput((int) 2000).getKata(KeadaanUmumTHT));
        KeadaanUmumThorax.setDocument(new batasInput((int) 2000).getKata(KeadaanUmumThorax));
        KeadaanUmumAbdomen.setDocument(new batasInput((int) 2000).getKata(KeadaanUmumAbdomen));
        KeadaanUmumEkstremitas.setDocument(new batasInput((int) 2000).getKata(KeadaanUmumEkstremitas));
        PemeriksaanHematologi.setDocument(new batasInput((int) 2000).getKata(PemeriksaanHematologi));
        PemeriksaanUrin.setDocument(new batasInput((int) 2000).getKata(PemeriksaanUrin));
        PemeriksaanKimiaDarah.setDocument(new batasInput((int) 2000).getKata(PemeriksaanKimiaDarah));
        PemeriksaanRadiologi.setDocument(new batasInput((int) 2000).getKata(PemeriksaanRadiologi));
        PemeriksaanEKG.setDocument(new batasInput((int) 2000).getKata(PemeriksaanEKG));
        Kesimpulan.setDocument(new batasInput((int) 2000).getKata(Kesimpulan));
        Saran.setDocument(new batasInput((int) 2000).getKata(Saran));

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
                if (dokter.getTable().getSelectedRow() != -1) {
                    KdDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                    NmDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                }
                KdDokter.requestFocus();
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
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LoadHTML = new widget.editorpane();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnLaporanHasilMCU = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        PanelInput = new widget.PanelBiasa();
        TLabel = new widget.Label();
        TNoRw = new widget.TextBox();
        LblNoRanapan = new widget.Label();
        TNoRM = new widget.TextBox();
        LblNama = new widget.Label();
        TPasien = new widget.TextBox();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        jLabel10 = new widget.Label();
        Jk = new widget.TextBox();
        TCari = new widget.TextBox();
        Scroll = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        jLabel4 = new widget.Label();
        TglAsuhan = new widget.Tanggal();
        jLabel99 = new widget.Label();
        KdDokter = new widget.TextBox();
        NmDokter = new widget.TextBox();
        BtnDokter = new widget.Button();
        jLabel50 = new widget.Label();
        jLabel51 = new widget.Label();
        scrollPane1 = new widget.ScrollPane();
        RiwayatPenyakitSekarang = new widget.TextArea();
        jLabel52 = new widget.Label();
        scrollPane2 = new widget.ScrollPane();
        RiwayatPenyakitDahulu = new widget.TextArea();
        jLabel53 = new widget.Label();
        scrollPane3 = new widget.ScrollPane();
        RiwayatPenyakitKeluarga = new widget.TextArea();
        jLabel54 = new widget.Label();
        scrollPane4 = new widget.ScrollPane();
        RiwayatKebiasaan = new widget.TextArea();
        jLabel60 = new widget.Label();
        jLabel61 = new widget.Label();
        scrollPane5 = new widget.ScrollPane();
        TandaVital = new widget.TextArea();
        jLabel62 = new widget.Label();
        scrollPane6 = new widget.ScrollPane();
        BeratTinggi = new widget.TextArea();
        jLabel63 = new widget.Label();
        jLabel64 = new widget.Label();
        scrollPane7 = new widget.ScrollPane();
        KeadaanUmumKepala = new widget.TextArea();
        jLabel65 = new widget.Label();
        scrollPane8 = new widget.ScrollPane();
        KeadaanUmumMulut = new widget.TextArea();
        jLabel66 = new widget.Label();
        scrollPane9 = new widget.ScrollPane();
        KeadaanUmumLeher = new widget.TextArea();
        jLabel67 = new widget.Label();
        scrollPane10 = new widget.ScrollPane();
        KeadaanUmumTHT = new widget.TextArea();
        jLabel68 = new widget.Label();
        scrollPane11 = new widget.ScrollPane();
        KeadaanUmumThorax = new widget.TextArea();
        jLabel69 = new widget.Label();
        scrollPane12 = new widget.ScrollPane();
        KeadaanUmumAbdomen = new widget.TextArea();
        jLabel72 = new widget.Label();
        scrollPane13 = new widget.ScrollPane();
        KeadaanUmumEkstremitas = new widget.TextArea();
        jLabel73 = new widget.Label();
        jLabel74 = new widget.Label();
        jLabel75 = new widget.Label();
        scrollPane14 = new widget.ScrollPane();
        PemeriksaanHematologi = new widget.TextArea();
        jLabel76 = new widget.Label();
        scrollPane15 = new widget.ScrollPane();
        PemeriksaanUrin = new widget.TextArea();
        jLabel77 = new widget.Label();
        scrollPane16 = new widget.ScrollPane();
        PemeriksaanKimiaDarah = new widget.TextArea();
        jLabel78 = new widget.Label();
        scrollPane17 = new widget.ScrollPane();
        PemeriksaanRadiologi = new widget.TextArea();
        jLabel79 = new widget.Label();
        scrollPane18 = new widget.ScrollPane();
        PemeriksaanEKG = new widget.TextArea();
        jLabel70 = new widget.Label();
        scrollPane20 = new widget.ScrollPane();
        Kesimpulan = new widget.TextArea();
        jLabel71 = new widget.Label();
        scrollPane21 = new widget.ScrollPane();
        Saran = new widget.TextArea();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBaru = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        BtnAll = new widget.Button();
        BtnKeluar = new widget.Button();
        Scroll1 = new widget.ScrollPane();
        tbObat = new widget.Table();

        LoadHTML.setBorder(null);
        LoadHTML.setName("LoadHTML"); // NOI18N

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnLaporanHasilMCU.setBackground(new java.awt.Color(255, 255, 254));
        MnLaporanHasilMCU.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnLaporanHasilMCU.setForeground(new java.awt.Color(50, 50, 50));
        MnLaporanHasilMCU.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnLaporanHasilMCU.setText("Laporan Hasil MCU");
        MnLaporanHasilMCU.setName("MnLaporanHasilMCU"); // NOI18N
        MnLaporanHasilMCU.setPreferredSize(new java.awt.Dimension(220, 26));
        MnLaporanHasilMCU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnLaporanHasilMCUActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnLaporanHasilMCU);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(null);
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setWarnaAtas(new java.awt.Color(246, 244, 241));
        internalFrame1.setWarnaBawah(new java.awt.Color(212, 208, 205));

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 556));
        PanelInput.setLayout(null);

        TLabel.setText("No.Rawat :");
        TLabel.setName("TLabel"); // NOI18N
        TLabel.setPreferredSize(new java.awt.Dimension(70, 23));
        PanelInput.add(TLabel);
        TLabel.setBounds(0, 10, 75, 23);

        TNoRw.setEditable(false);
        TNoRw.setForeground(new java.awt.Color(0, 0, 0));
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.setPreferredSize(new java.awt.Dimension(207, 23));
        PanelInput.add(TNoRw);
        TNoRw.setBounds(79, 10, 112, 23);

        LblNoRanapan.setText("No.RM :");
        LblNoRanapan.setName("LblNoRanapan"); // NOI18N
        LblNoRanapan.setPreferredSize(new java.awt.Dimension(70, 23));
        PanelInput.add(LblNoRanapan);
        LblNoRanapan.setBounds(0, 40, 75, 23);

        TNoRM.setEditable(false);
        TNoRM.setForeground(new java.awt.Color(0, 0, 0));
        TNoRM.setName("TNoRM"); // NOI18N
        TNoRM.setPreferredSize(new java.awt.Dimension(80, 23));
        PanelInput.add(TNoRM);
        TNoRM.setBounds(79, 40, 112, 23);

        LblNama.setText("Nama :");
        LblNama.setName("LblNama"); // NOI18N
        LblNama.setPreferredSize(new java.awt.Dimension(70, 23));
        PanelInput.add(LblNama);
        LblNama.setBounds(0, 70, 75, 23);

        TPasien.setEditable(false);
        TPasien.setForeground(new java.awt.Color(0, 0, 0));
        TPasien.setName("TPasien"); // NOI18N
        TPasien.setPreferredSize(new java.awt.Dimension(207, 23));
        PanelInput.add(TPasien);
        TPasien.setBounds(79, 70, 112, 23);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8"); // NOI18N
        jLabel8.setPreferredSize(new java.awt.Dimension(70, 23));
        PanelInput.add(jLabel8);
        jLabel8.setBounds(0, 100, 75, 23);

        TglLahir.setEditable(false);
        TglLahir.setForeground(new java.awt.Color(0, 0, 0));
        TglLahir.setName("TglLahir"); // NOI18N
        TglLahir.setPreferredSize(new java.awt.Dimension(80, 23));
        PanelInput.add(TglLahir);
        TglLahir.setBounds(79, 100, 112, 23);

        jLabel10.setText("J.K. :");
        jLabel10.setName("jLabel10"); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(70, 23));
        PanelInput.add(jLabel10);
        jLabel10.setBounds(0, 130, 75, 23);

        Jk.setEditable(false);
        Jk.setForeground(new java.awt.Color(0, 0, 0));
        Jk.setName("Jk"); // NOI18N
        Jk.setPreferredSize(new java.awt.Dimension(80, 23));
        PanelInput.add(Jk);
        Jk.setBounds(79, 130, 112, 23);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.WEST);

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 200));

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(870, 1500));
        FormInput.setLayout(null);

        jLabel4.setText("Tanggal :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 12, 70, 23);

        TglAsuhan.setForeground(new java.awt.Color(50, 50, 50));
        TglAsuhan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "02-11-2025 02:41:42" }));
        TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setName("TglAsuhan"); // NOI18N
        TglAsuhan.setOpaque(false);
        TglAsuhan.setPreferredSize(new java.awt.Dimension(90, 23));
        FormInput.add(TglAsuhan);
        TglAsuhan.setBounds(74, 12, 130, 23);

        jLabel99.setText("Dokter :");
        jLabel99.setName("jLabel99"); // NOI18N
        FormInput.add(jLabel99);
        jLabel99.setBounds(215, 12, 50, 23);

        KdDokter.setForeground(new java.awt.Color(50, 50, 50));
        KdDokter.setName("KdDokter"); // NOI18N
        KdDokter.setPreferredSize(new java.awt.Dimension(80, 23));
        KdDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdDokterKeyPressed(evt);
            }
        });
        FormInput.add(KdDokter);
        KdDokter.setBounds(269, 12, 80, 23);

        NmDokter.setEditable(false);
        NmDokter.setName("NmDokter"); // NOI18N
        NmDokter.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(NmDokter);
        NmDokter.setBounds(381, 12, 180, 23);

        BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokter.setMnemonic('2');
        BtnDokter.setToolTipText("ALt+2");
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
        BtnDokter.setBounds(564, 12, 28, 23);

        jLabel50.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel50.setText("I. RIWAYAT KESEHATAN");
        jLabel50.setName("jLabel50"); // NOI18N
        FormInput.add(jLabel50);
        jLabel50.setBounds(0, 50, 200, 23);

        jLabel51.setText("Riwayat Penyakit Sekarang :");
        jLabel51.setName("jLabel51"); // NOI18N
        FormInput.add(jLabel51);
        jLabel51.setBounds(0, 75, 170, 23);

        scrollPane1.setName("scrollPane1"); // NOI18N

        RiwayatPenyakitSekarang.setBorder(null);
        RiwayatPenyakitSekarang.setColumns(20);
        RiwayatPenyakitSekarang.setRows(5);
        RiwayatPenyakitSekarang.setName("RiwayatPenyakitSekarang"); // NOI18N
        scrollPane1.setViewportView(RiwayatPenyakitSekarang);

        FormInput.add(scrollPane1);
        scrollPane1.setBounds(174, 75, 400, 35);

        jLabel52.setText("Riwayat Penyakit Dahulu :");
        jLabel52.setName("jLabel52"); // NOI18N
        FormInput.add(jLabel52);
        jLabel52.setBounds(0, 115, 170, 23);

        scrollPane2.setName("scrollPane2"); // NOI18N

        RiwayatPenyakitDahulu.setBorder(null);
        RiwayatPenyakitDahulu.setColumns(20);
        RiwayatPenyakitDahulu.setRows(5);
        RiwayatPenyakitDahulu.setName("RiwayatPenyakitDahulu"); // NOI18N
        scrollPane2.setViewportView(RiwayatPenyakitDahulu);

        FormInput.add(scrollPane2);
        scrollPane2.setBounds(174, 115, 400, 35);

        jLabel53.setText("Riwayat Penyakit Keluarga :");
        jLabel53.setName("jLabel53"); // NOI18N
        FormInput.add(jLabel53);
        jLabel53.setBounds(0, 155, 170, 23);

        scrollPane3.setName("scrollPane3"); // NOI18N

        RiwayatPenyakitKeluarga.setBorder(null);
        RiwayatPenyakitKeluarga.setColumns(20);
        RiwayatPenyakitKeluarga.setRows(5);
        RiwayatPenyakitKeluarga.setName("RiwayatPenyakitKeluarga"); // NOI18N
        scrollPane3.setViewportView(RiwayatPenyakitKeluarga);

        FormInput.add(scrollPane3);
        scrollPane3.setBounds(174, 155, 400, 35);

        jLabel54.setText("Riwayat Kebiasaan :");
        jLabel54.setName("jLabel54"); // NOI18N
        FormInput.add(jLabel54);
        jLabel54.setBounds(0, 195, 170, 23);

        scrollPane4.setName("scrollPane4"); // NOI18N

        RiwayatKebiasaan.setBorder(null);
        RiwayatKebiasaan.setColumns(20);
        RiwayatKebiasaan.setRows(5);
        RiwayatKebiasaan.setName("RiwayatKebiasaan"); // NOI18N
        scrollPane4.setViewportView(RiwayatKebiasaan);

        FormInput.add(scrollPane4);
        scrollPane4.setBounds(174, 195, 400, 35);

        jLabel60.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel60.setText("II. PEMERIKSAAN FISIK");
        jLabel60.setName("jLabel60"); // NOI18N
        FormInput.add(jLabel60);
        jLabel60.setBounds(0, 240, 200, 23);

        jLabel61.setText("Tanda-tanda Vital :");
        jLabel61.setName("jLabel61"); // NOI18N
        FormInput.add(jLabel61);
        jLabel61.setBounds(0, 265, 170, 23);

        scrollPane5.setName("scrollPane5"); // NOI18N

        TandaVital.setBorder(null);
        TandaVital.setColumns(20);
        TandaVital.setRows(5);
        TandaVital.setName("TandaVital"); // NOI18N
        scrollPane5.setViewportView(TandaVital);

        FormInput.add(scrollPane5);
        scrollPane5.setBounds(174, 265, 400, 35);

        jLabel62.setText("Berat Badan/Tinggi Badan :");
        jLabel62.setName("jLabel62"); // NOI18N
        FormInput.add(jLabel62);
        jLabel62.setBounds(0, 305, 170, 23);

        scrollPane6.setName("scrollPane6"); // NOI18N

        BeratTinggi.setBorder(null);
        BeratTinggi.setColumns(20);
        BeratTinggi.setRows(5);
        BeratTinggi.setName("BeratTinggi"); // NOI18N
        scrollPane6.setViewportView(BeratTinggi);

        FormInput.add(scrollPane6);
        scrollPane6.setBounds(174, 305, 400, 35);

        jLabel63.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel63.setText("3. Keadaan Umum");
        jLabel63.setName("jLabel63"); // NOI18N
        FormInput.add(jLabel63);
        jLabel63.setBounds(0, 345, 200, 23);

        jLabel64.setText("Kepala :");
        jLabel64.setName("jLabel64"); // NOI18N
        FormInput.add(jLabel64);
        jLabel64.setBounds(0, 370, 170, 23);

        scrollPane7.setName("scrollPane7"); // NOI18N

        KeadaanUmumKepala.setBorder(null);
        KeadaanUmumKepala.setColumns(20);
        KeadaanUmumKepala.setRows(5);
        KeadaanUmumKepala.setName("KeadaanUmumKepala"); // NOI18N
        scrollPane7.setViewportView(KeadaanUmumKepala);

        FormInput.add(scrollPane7);
        scrollPane7.setBounds(174, 370, 400, 35);

        jLabel65.setText("Mulut :");
        jLabel65.setName("jLabel65"); // NOI18N
        FormInput.add(jLabel65);
        jLabel65.setBounds(0, 410, 170, 23);

        scrollPane8.setName("scrollPane8"); // NOI18N

        KeadaanUmumMulut.setBorder(null);
        KeadaanUmumMulut.setColumns(20);
        KeadaanUmumMulut.setRows(5);
        KeadaanUmumMulut.setName("KeadaanUmumMulut"); // NOI18N
        scrollPane8.setViewportView(KeadaanUmumMulut);

        FormInput.add(scrollPane8);
        scrollPane8.setBounds(174, 410, 400, 35);

        jLabel66.setText("Leher :");
        jLabel66.setName("jLabel66"); // NOI18N
        FormInput.add(jLabel66);
        jLabel66.setBounds(0, 450, 170, 23);

        scrollPane9.setName("scrollPane9"); // NOI18N

        KeadaanUmumLeher.setBorder(null);
        KeadaanUmumLeher.setColumns(20);
        KeadaanUmumLeher.setRows(5);
        KeadaanUmumLeher.setName("KeadaanUmumLeher"); // NOI18N
        scrollPane9.setViewportView(KeadaanUmumLeher);

        FormInput.add(scrollPane9);
        scrollPane9.setBounds(174, 450, 400, 35);

        jLabel67.setText("THT :");
        jLabel67.setName("jLabel67"); // NOI18N
        FormInput.add(jLabel67);
        jLabel67.setBounds(0, 490, 170, 23);

        scrollPane10.setName("scrollPane10"); // NOI18N

        KeadaanUmumTHT.setBorder(null);
        KeadaanUmumTHT.setColumns(20);
        KeadaanUmumTHT.setRows(5);
        KeadaanUmumTHT.setName("KeadaanUmumTHT"); // NOI18N
        scrollPane10.setViewportView(KeadaanUmumTHT);

        FormInput.add(scrollPane10);
        scrollPane10.setBounds(174, 490, 400, 35);

        jLabel68.setText("Thorax :");
        jLabel68.setName("jLabel68"); // NOI18N
        FormInput.add(jLabel68);
        jLabel68.setBounds(0, 530, 170, 23);

        scrollPane11.setName("scrollPane11"); // NOI18N

        KeadaanUmumThorax.setBorder(null);
        KeadaanUmumThorax.setColumns(20);
        KeadaanUmumThorax.setRows(5);
        KeadaanUmumThorax.setName("KeadaanUmumThorax"); // NOI18N
        scrollPane11.setViewportView(KeadaanUmumThorax);

        FormInput.add(scrollPane11);
        scrollPane11.setBounds(174, 530, 400, 35);

        jLabel69.setText("Abdomen :");
        jLabel69.setName("jLabel69"); // NOI18N
        FormInput.add(jLabel69);
        jLabel69.setBounds(0, 570, 170, 23);

        scrollPane12.setName("scrollPane12"); // NOI18N

        KeadaanUmumAbdomen.setBorder(null);
        KeadaanUmumAbdomen.setColumns(20);
        KeadaanUmumAbdomen.setRows(5);
        KeadaanUmumAbdomen.setName("KeadaanUmumAbdomen"); // NOI18N
        scrollPane12.setViewportView(KeadaanUmumAbdomen);

        FormInput.add(scrollPane12);
        scrollPane12.setBounds(174, 570, 400, 35);

        jLabel72.setText("Ekstremitas :");
        jLabel72.setName("jLabel72"); // NOI18N
        FormInput.add(jLabel72);
        jLabel72.setBounds(0, 610, 170, 23);

        scrollPane13.setName("scrollPane13"); // NOI18N

        KeadaanUmumEkstremitas.setBorder(null);
        KeadaanUmumEkstremitas.setColumns(20);
        KeadaanUmumEkstremitas.setRows(5);
        KeadaanUmumEkstremitas.setName("KeadaanUmumEkstremitas"); // NOI18N
        scrollPane13.setViewportView(KeadaanUmumEkstremitas);

        FormInput.add(scrollPane13);
        scrollPane13.setBounds(174, 610, 400, 35);

        jLabel73.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel73.setText("III. PEMERIKSAAN PENUNJANG");
        jLabel73.setName("jLabel73"); // NOI18N
        FormInput.add(jLabel73);
        jLabel73.setBounds(0, 655, 200, 23);

        jLabel74.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel74.setText("1. Laboratorium");
        jLabel74.setName("jLabel74"); // NOI18N
        FormInput.add(jLabel74);
        jLabel74.setBounds(0, 680, 200, 23);

        jLabel75.setText("Hematologi :");
        jLabel75.setName("jLabel75"); // NOI18N
        FormInput.add(jLabel75);
        jLabel75.setBounds(0, 705, 170, 23);

        scrollPane14.setName("scrollPane14"); // NOI18N

        PemeriksaanHematologi.setBorder(null);
        PemeriksaanHematologi.setColumns(20);
        PemeriksaanHematologi.setRows(5);
        PemeriksaanHematologi.setName("PemeriksaanHematologi"); // NOI18N
        scrollPane14.setViewportView(PemeriksaanHematologi);

        FormInput.add(scrollPane14);
        scrollPane14.setBounds(174, 705, 400, 35);

        jLabel76.setText("Urin :");
        jLabel76.setName("jLabel76"); // NOI18N
        FormInput.add(jLabel76);
        jLabel76.setBounds(0, 745, 170, 23);

        scrollPane15.setName("scrollPane15"); // NOI18N

        PemeriksaanUrin.setBorder(null);
        PemeriksaanUrin.setColumns(20);
        PemeriksaanUrin.setRows(5);
        PemeriksaanUrin.setName("PemeriksaanUrin"); // NOI18N
        scrollPane15.setViewportView(PemeriksaanUrin);

        FormInput.add(scrollPane15);
        scrollPane15.setBounds(174, 745, 400, 35);

        jLabel77.setText("Kimia Darah :");
        jLabel77.setName("jLabel77"); // NOI18N
        FormInput.add(jLabel77);
        jLabel77.setBounds(0, 785, 170, 23);

        scrollPane16.setName("scrollPane16"); // NOI18N

        PemeriksaanKimiaDarah.setBorder(null);
        PemeriksaanKimiaDarah.setColumns(20);
        PemeriksaanKimiaDarah.setRows(5);
        PemeriksaanKimiaDarah.setName("PemeriksaanKimiaDarah"); // NOI18N
        scrollPane16.setViewportView(PemeriksaanKimiaDarah);

        FormInput.add(scrollPane16);
        scrollPane16.setBounds(174, 785, 400, 35);

        jLabel78.setText("Radiologi (Thorax PA) :");
        jLabel78.setName("jLabel78"); // NOI18N
        FormInput.add(jLabel78);
        jLabel78.setBounds(0, 825, 170, 23);

        scrollPane17.setName("scrollPane17"); // NOI18N

        PemeriksaanRadiologi.setBorder(null);
        PemeriksaanRadiologi.setColumns(20);
        PemeriksaanRadiologi.setRows(5);
        PemeriksaanRadiologi.setName("PemeriksaanRadiologi"); // NOI18N
        scrollPane17.setViewportView(PemeriksaanRadiologi);

        FormInput.add(scrollPane17);
        scrollPane17.setBounds(174, 825, 400, 35);

        jLabel79.setText("Elektrokardiografi :");
        jLabel79.setName("jLabel79"); // NOI18N
        FormInput.add(jLabel79);
        jLabel79.setBounds(0, 865, 170, 23);

        scrollPane18.setName("scrollPane18"); // NOI18N

        PemeriksaanEKG.setBorder(null);
        PemeriksaanEKG.setColumns(20);
        PemeriksaanEKG.setRows(5);
        PemeriksaanEKG.setName("PemeriksaanEKG"); // NOI18N
        scrollPane18.setViewportView(PemeriksaanEKG);

        FormInput.add(scrollPane18);
        scrollPane18.setBounds(174, 865, 400, 35);

        jLabel70.setText("Kesimpulan :");
        jLabel70.setName("jLabel70"); // NOI18N
        FormInput.add(jLabel70);
        jLabel70.setBounds(0, 905, 170, 23);

        scrollPane20.setName("scrollPane20"); // NOI18N

        Kesimpulan.setBorder(null);
        Kesimpulan.setColumns(20);
        Kesimpulan.setRows(5);
        Kesimpulan.setName("Kesimpulan"); // NOI18N
        scrollPane20.setViewportView(Kesimpulan);

        FormInput.add(scrollPane20);
        scrollPane20.setBounds(174, 905, 400, 35);

        jLabel71.setText("Saran :");
        jLabel71.setName("jLabel71"); // NOI18N
        FormInput.add(jLabel71);
        jLabel71.setBounds(0, 945, 170, 23);

        scrollPane21.setName("scrollPane21"); // NOI18N

        Saran.setBorder(null);
        Saran.setColumns(20);
        Saran.setRows(5);
        Saran.setName("Saran"); // NOI18N
        scrollPane21.setViewportView(Saran);

        FormInput.add(scrollPane21);
        scrollPane21.setBounds(174, 945, 400, 35);

        Scroll.setViewportView(FormInput);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 54));
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

        BtnBaru.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/New.png"))); // NOI18N
        BtnBaru.setMnemonic('B');
        BtnBaru.setText("Baru");
        BtnBaru.setToolTipText("Alt+B");
        BtnBaru.setName("BtnBaru"); // NOI18N
        BtnBaru.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBaru.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBaruActionPerformed(evt);
            }
        });
        BtnBaru.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnBaruKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnBaru);

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

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Print.png"))); // NOI18N
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

        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        Scroll1.setName("Scroll1"); // NOI18N
        Scroll1.setOpaque(true);

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
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbObatKeyPressed(evt);
            }
        });
        Scroll1.setViewportView(tbObat);

        getContentPane().add(Scroll1, java.awt.BorderLayout.PAGE_START);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(180, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void KdDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdDokterKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            NmDokter.setText(dokter.tampil3(KdDokter.getText()));
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            BtnDokterActionPerformed(null);
        } else {
            Valid.pindah(evt, TglAsuhan, BtnDokter);
        }
    }//GEN-LAST:event_KdDokterKeyPressed

    private void BtnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokterActionPerformed
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setAlwaysOnTop(false);
        dokter.setVisible(true);
    }//GEN-LAST:event_BtnDokterActionPerformed

    private void BtnDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnDokterKeyPressed
        Valid.pindah(evt, KdDokter, RiwayatPenyakitSekarang);
    }//GEN-LAST:event_BtnDokterKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else if (KdDokter.getText().trim().equals("")) {
            Valid.textKosong(KdDokter, "Dokter");
        } else {
            if (Sequel.menyimpantf("hasil_mcu", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "No.Rawat", 25, new String[]{
                TNoRw.getText(), Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + " " + TglAsuhan.getSelectedItem().toString().substring(11, 19), KdDokter.getText(),
                RiwayatPenyakitSekarang.getText(), RiwayatPenyakitDahulu.getText(), RiwayatPenyakitKeluarga.getText(), RiwayatKebiasaan.getText(),
                TandaVital.getText(), BeratTinggi.getText(), KeadaanUmumKepala.getText(), KeadaanUmumMulut.getText(), KeadaanUmumLeher.getText(),
                KeadaanUmumTHT.getText(), KeadaanUmumThorax.getText(), KeadaanUmumAbdomen.getText(), KeadaanUmumEkstremitas.getText(),
                PemeriksaanHematologi.getText(), PemeriksaanUrin.getText(), PemeriksaanKimiaDarah.getText(), PemeriksaanRadiologi.getText(),
                PemeriksaanEKG.getText(), Kesimpulan.getText(), Saran.getText()
            }) == true) {
                tabMode.addRow(new String[]{
                    TNoRw.getText(), TNoRM.getText(), TPasien.getText(), TglLahir.getText(), Jk.getText(), KdDokter.getText(), NmDokter.getText(),
                    Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + " " + TglAsuhan.getSelectedItem().toString().substring(11, 19),
                    RiwayatPenyakitSekarang.getText(), RiwayatPenyakitDahulu.getText(), RiwayatPenyakitKeluarga.getText(), RiwayatKebiasaan.getText(),
                    TandaVital.getText(), BeratTinggi.getText(), KeadaanUmumKepala.getText(), KeadaanUmumMulut.getText(), KeadaanUmumLeher.getText(),
                    KeadaanUmumTHT.getText(), KeadaanUmumThorax.getText(), KeadaanUmumAbdomen.getText(), KeadaanUmumEkstremitas.getText(),
                    PemeriksaanHematologi.getText(), PemeriksaanUrin.getText(), PemeriksaanKimiaDarah.getText(), PemeriksaanRadiologi.getText(),
                    PemeriksaanEKG.getText(), Kesimpulan.getText(), Saran.getText()
                });
                emptTeks();
                LCount.setText("" + tabMode.getRowCount());
            }
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnKeluar, BtnBaru);
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBaruActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBaruActionPerformed
        emptTeks();
    }//GEN-LAST:event_BtnBaruActionPerformed

    private void BtnBaruKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBaruKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            emptTeks();
        } else {
            Valid.pindah(evt, BtnSimpan, BtnHapus);
        }
    }//GEN-LAST:event_BtnBaruKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if (tbObat.getSelectedRow() > -1) {
            if (akses.getpenilaian_mcu() == true) {
                hapus();
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
        }
    }//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnHapusActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnBaru, BtnEdit);
        }
    }//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if (TNoRw.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "No.Rawat");
        } else if (KdDokter.getText().trim().equals("")) {
            Valid.textKosong(KdDokter, "Dokter");
        } else {
            if (tbObat.getSelectedRow() > -1) {
                if (akses.getpenilaian_mcu() == true) {
                    ganti();
                }
            } else {
                JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
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
        dokter.dispose();
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnKeluarActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnEdit, TCari);
        }
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (tbObat.getSelectedRow() > -1) {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            param.put("logo1", Sequel.cariGambar("select setting.logo1 from setting"));
            param.put("logo2", Sequel.cariGambar("select setting.logo2 from setting"));
            finger = Sequel.cariIsi("select sha1(concat(ifnull(pasien.no_ktp,''),ifnull(pasien.nm_pasien,''),ifnull(date_format(pasien.tgl_lahir,'%d-%m-%Y'),''),ifnull(pasien.jk,''),ifnull(pasien.nm_ibu,''),ifnull(hasil_mcu.tanggal,''),ifnull(hasil_mcu.kd_dokter,''),ifnull(hasil_mcu.riwayat_sekarang,''),ifnull(hasil_mcu.riwayat_dahulu,''),ifnull(hasil_mcu.riwayat_keluarga,''))) as finger from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis inner join hasil_mcu on reg_periksa.no_rawat=hasil_mcu.no_rawat where hasil_mcu.no_rawat=?", TNoRw.getText());
            param.put("finger", finger);
            // Generate QR Code text for doctor info
            String qrText = "Dr. " + tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString() + 
                           " | Kode: " + tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString() +
                           " | MCU: " + TNoRw.getText();
            param.put("qrcode", generateQRCodeImage(qrText));
            param.put("no_rawat", TNoRw.getText());
            Valid.MyReportqry("rptHasilMCU.jasper", "report", "::[ Laporan Hasil MCU ]::",
                    "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tmp_lahir,pasien.tgl_lahir,pasien.gol_darah,pasien.stts_nikah,"
                    + "pasien.agama,pasien.tgl_daftar,pasien.no_tlp,pasien.umur,pasien.pnd,pasien.keluarga,pasien.namakeluarga,penjab.png_jawab,"
                    + "hasil_mcu.tanggal,hasil_mcu.kd_dokter,dokter.nm_dokter,hasil_mcu.riwayat_sekarang,hasil_mcu.riwayat_dahulu,hasil_mcu.riwayat_keluarga,"
                    + "hasil_mcu.riwayat_kebiasaan,hasil_mcu.tanda_vital,hasil_mcu.bb_tb,hasil_mcu.kepala,hasil_mcu.mulut,hasil_mcu.leher,hasil_mcu.tht,"
                    + "hasil_mcu.thorax,hasil_mcu.abdomen,hasil_mcu.ekstremitas,hasil_mcu.hematologi,hasil_mcu.urin,hasil_mcu.kimia_darah,hasil_mcu.radiologi,"
                    + "hasil_mcu.ekg,hasil_mcu.kesimpulan,hasil_mcu.saran from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                    + "inner join hasil_mcu on reg_periksa.no_rawat=hasil_mcu.no_rawat inner join dokter on hasil_mcu.kd_dokter=dokter.kd_dokter "
                    + "inner join penjab on reg_periksa.kd_pj=penjab.kd_pj where hasil_mcu.no_rawat='" + TNoRw.getText() + "'", param);
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

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException ex) {
            }
        }
    }//GEN-LAST:event_tbObatMouseClicked

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyPressed
        if (tabMode.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                try {
                    getData();
                } catch (java.lang.NullPointerException ex) {
                }
            }
        }
    }//GEN-LAST:event_tbObatKeyPressed

    private void MnLaporanHasilMCUActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnLaporanHasilMCUActionPerformed
        if (tbObat.getSelectedRow() > -1) {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            finger = Sequel.cariIsi("select sha1(concat(ifnull(pasien.no_ktp,''),ifnull(pasien.nm_pasien,''),ifnull(date_format(pasien.tgl_lahir,'%d-%m-%Y'),''),ifnull(pasien.jk,''),ifnull(pasien.nm_ibu,''),ifnull(hasil_mcu.tanggal,''),ifnull(hasil_mcu.kd_dokter,''),ifnull(hasil_mcu.riwayat_sekarang,''),ifnull(hasil_mcu.riwayat_dahulu,''),ifnull(hasil_mcu.riwayat_keluarga,''))) as finger from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis inner join hasil_mcu on reg_periksa.no_rawat=hasil_mcu.no_rawat where hasil_mcu.no_rawat=?", TNoRw.getText());
            param.put("finger", finger);
            param.put("no_rawat", TNoRw.getText());
            Valid.MyReportqry("rptHasilMCU.jasper", "report", "::[ Laporan Hasil MCU ]::",
                    "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tmp_lahir,pasien.tgl_lahir,pasien.gol_darah,pasien.stts_nikah,"
                    + "pasien.agama,pasien.tgl_daftar,pasien.no_tlp,pasien.umur,pasien.pnd,pasien.keluarga,pasien.namakeluarga,penjab.png_jawab,"
                    + "hasil_mcu.tanggal,hasil_mcu.kd_dokter,dokter.nm_dokter,hasil_mcu.riwayat_sekarang,hasil_mcu.riwayat_dahulu,hasil_mcu.riwayat_keluarga,"
                    + "hasil_mcu.riwayat_kebiasaan,hasil_mcu.tanda_vital,hasil_mcu.bb_tb,hasil_mcu.kepala,hasil_mcu.mulut,hasil_mcu.leher,hasil_mcu.tht,"
                    + "hasil_mcu.thorax,hasil_mcu.abdomen,hasil_mcu.ekstremitas,hasil_mcu.hematologi,hasil_mcu.urin,hasil_mcu.kimia_darah,hasil_mcu.radiologi,"
                    + "hasil_mcu.ekg,hasil_mcu.kesimpulan,hasil_mcu.saran from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                    + "inner join hasil_mcu on reg_periksa.no_rawat=hasil_mcu.no_rawat inner join dokter on hasil_mcu.kd_dokter=dokter.kd_dokter "
                    + "inner join penjab on reg_periksa.kd_pj=penjab.kd_pj where hasil_mcu.no_rawat='" + TNoRw.getText() + "'", param);
        }
    }//GEN-LAST:event_MnLaporanHasilMCUActionPerformed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        tampil();
        emptTeks();
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            TCari.setText("");
            tampil();
            emptTeks();
        } else {
            Valid.pindah(evt, BtnPrint, BtnKeluar);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            tampil();
        }
    }//GEN-LAST:event_TCariKeyPressed

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", TNoRM, TNoRw.getText());
            Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", TPasien, TNoRM.getText());
            Sequel.cariIsi("select tgl_lahir from pasien where no_rkm_medis=?", TglLahir, TNoRM.getText());
            Sequel.cariIsi("select jk from pasien where no_rkm_medis=?", Jk, TNoRM.getText());
        } else {
            Valid.pindah(evt, TCari, TglAsuhan);
        }
    }//GEN-LAST:event_TNoRwKeyPressed

    private void TglAsuhanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TglAsuhanKeyPressed
        Valid.pindah(evt, TNoRw, KdDokter);
    }//GEN-LAST:event_TglAsuhanKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampil();
    }//GEN-LAST:event_formWindowOpened

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMHasilMCU dialog = new RMHasilMCU(new javax.swing.JFrame(), true);
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
    private widget.TextArea BeratTinggi;
    private widget.Button BtnAll;
    private widget.Button BtnBaru;
    private widget.Button BtnDokter;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.PanelBiasa FormInput;
    private widget.TextBox Jk;
    private widget.TextBox KdDokter;
    private widget.TextArea KeadaanUmumAbdomen;
    private widget.TextArea KeadaanUmumEkstremitas;
    private widget.TextArea KeadaanUmumKepala;
    private widget.TextArea KeadaanUmumLeher;
    private widget.TextArea KeadaanUmumMulut;
    private widget.TextArea KeadaanUmumTHT;
    private widget.TextArea KeadaanUmumThorax;
    private widget.TextArea Kesimpulan;
    private widget.Label LblNama;
    private widget.Label LblNoRanapan;
    private widget.editorpane LoadHTML;
    private javax.swing.JMenuItem MnLaporanHasilMCU;
    private widget.TextBox NmDokter;
    private widget.PanelBiasa PanelInput;
    private widget.TextArea PemeriksaanEKG;
    private widget.TextArea PemeriksaanHematologi;
    private widget.TextArea PemeriksaanKimiaDarah;
    private widget.TextArea PemeriksaanRadiologi;
    private widget.TextArea PemeriksaanUrin;
    private widget.TextArea RiwayatKebiasaan;
    private widget.TextArea RiwayatPenyakitDahulu;
    private widget.TextArea RiwayatPenyakitKeluarga;
    private widget.TextArea RiwayatPenyakitSekarang;
    private widget.TextArea Saran;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll1;
    private widget.Label TLabel;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.TextArea TandaVital;
    private widget.TextBox TglLahir;
    private widget.Tanggal TglAsuhan;
    private widget.Label jLabel10;
    private widget.Label jLabel4;
    private widget.Label jLabel50;
    private widget.Label jLabel51;
    private widget.Label jLabel52;
    private widget.Label jLabel53;
    private widget.Label jLabel54;
    private widget.Label jLabel60;
    private widget.Label jLabel61;
    private widget.Label jLabel62;
    private widget.Label jLabel63;
    private widget.Label jLabel64;
    private widget.Label jLabel65;
    private widget.Label jLabel66;
    private widget.Label jLabel67;
    private widget.Label jLabel68;
    private widget.Label jLabel69;
    private widget.Label jLabel70;
    private widget.Label jLabel71;
    private widget.Label jLabel72;
    private widget.Label jLabel73;
    private widget.Label jLabel74;
    private widget.Label jLabel75;
    private widget.Label jLabel76;
    private widget.Label jLabel77;
    private widget.Label jLabel78;
    private widget.Label jLabel79;
    private widget.Label jLabel8;
    private widget.Label jLabel99;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.InternalFrame internalFrame1;
    private widget.panelisi panelGlass8;
    private widget.ScrollPane scrollPane1;
    private widget.ScrollPane scrollPane10;
    private widget.ScrollPane scrollPane11;
    private widget.ScrollPane scrollPane12;
    private widget.ScrollPane scrollPane13;
    private widget.ScrollPane scrollPane14;
    private widget.ScrollPane scrollPane15;
    private widget.ScrollPane scrollPane16;
    private widget.ScrollPane scrollPane17;
    private widget.ScrollPane scrollPane18;
    private widget.ScrollPane scrollPane2;
    private widget.ScrollPane scrollPane20;
    private widget.ScrollPane scrollPane21;
    private widget.ScrollPane scrollPane3;
    private widget.ScrollPane scrollPane4;
    private widget.ScrollPane scrollPane5;
    private widget.ScrollPane scrollPane6;
    private widget.ScrollPane scrollPane7;
    private widget.ScrollPane scrollPane8;
    private widget.ScrollPane scrollPane9;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            if (TCari.getText().trim().equals("")) {
                ps = koneksi.prepareStatement(
                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.tgl_lahir,pasien.jk,"
                        + "hasil_mcu.kd_dokter,dokter.nm_dokter,hasil_mcu.tanggal,hasil_mcu.riwayat_sekarang,hasil_mcu.riwayat_dahulu,"
                        + "hasil_mcu.riwayat_keluarga,hasil_mcu.riwayat_kebiasaan,hasil_mcu.tanda_vital,hasil_mcu.bb_tb,hasil_mcu.kepala,"
                        + "hasil_mcu.mulut,hasil_mcu.leher,hasil_mcu.tht,hasil_mcu.thorax,hasil_mcu.abdomen,hasil_mcu.ekstremitas,"
                        + "hasil_mcu.hematologi,hasil_mcu.urin,hasil_mcu.kimia_darah,hasil_mcu.radiologi,hasil_mcu.ekg,"
                        + "hasil_mcu.kesimpulan,hasil_mcu.saran from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "inner join hasil_mcu on reg_periksa.no_rawat=hasil_mcu.no_rawat inner join dokter on hasil_mcu.kd_dokter=dokter.kd_dokter "
                        + "where hasil_mcu.tanggal between ? and ? order by hasil_mcu.tanggal");
            } else {
                ps = koneksi.prepareStatement(
                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.tgl_lahir,pasien.jk,"
                        + "hasil_mcu.kd_dokter,dokter.nm_dokter,hasil_mcu.tanggal,hasil_mcu.riwayat_sekarang,hasil_mcu.riwayat_dahulu,"
                        + "hasil_mcu.riwayat_keluarga,hasil_mcu.riwayat_kebiasaan,hasil_mcu.tanda_vital,hasil_mcu.bb_tb,hasil_mcu.kepala,"
                        + "hasil_mcu.mulut,hasil_mcu.leher,hasil_mcu.tht,hasil_mcu.thorax,hasil_mcu.abdomen,hasil_mcu.ekstremitas,"
                        + "hasil_mcu.hematologi,hasil_mcu.urin,hasil_mcu.kimia_darah,hasil_mcu.radiologi,hasil_mcu.ekg,"
                        + "hasil_mcu.kesimpulan,hasil_mcu.saran from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "inner join hasil_mcu on reg_periksa.no_rawat=hasil_mcu.no_rawat inner join dokter on hasil_mcu.kd_dokter=dokter.kd_dokter "
                        + "where hasil_mcu.tanggal between ? and ? and (reg_periksa.no_rawat like ? or pasien.no_rkm_medis like ? "
                        + "or pasien.nm_pasien like ? or dokter.nm_dokter like ?) order by hasil_mcu.tanggal");
            }

            try {
                if (TCari.getText().trim().equals("")) {
                    ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                    ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                } else {
                    ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                    ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                    ps.setString(3, "%" + TCari.getText() + "%");
                    ps.setString(4, "%" + TCari.getText() + "%");
                    ps.setString(5, "%" + TCari.getText() + "%");
                    ps.setString(6, "%" + TCari.getText() + "%");
                }
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"), rs.getString("tgl_lahir"), rs.getString("jk"),
                        rs.getString("kd_dokter"), rs.getString("nm_dokter"), rs.getString("tanggal"), rs.getString("riwayat_sekarang"),
                        rs.getString("riwayat_dahulu"), rs.getString("riwayat_keluarga"), rs.getString("riwayat_kebiasaan"), rs.getString("tanda_vital"),
                        rs.getString("bb_tb"), rs.getString("kepala"), rs.getString("mulut"), rs.getString("leher"), rs.getString("tht"),
                        rs.getString("thorax"), rs.getString("abdomen"), rs.getString("ekstremitas"), rs.getString("hematologi"),
                        rs.getString("urin"), rs.getString("kimia_darah"), rs.getString("radiologi"), rs.getString("ekg"),
                        rs.getString("kesimpulan"), rs.getString("saran")
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
        RiwayatPenyakitSekarang.setText("");
        RiwayatPenyakitDahulu.setText("");
        RiwayatPenyakitKeluarga.setText("");
        RiwayatKebiasaan.setText("");
        TandaVital.setText("");
        BeratTinggi.setText("");
        KeadaanUmumKepala.setText("");
        KeadaanUmumMulut.setText("");
        KeadaanUmumLeher.setText("");
        KeadaanUmumTHT.setText("");
        KeadaanUmumThorax.setText("");
        KeadaanUmumAbdomen.setText("");
        KeadaanUmumEkstremitas.setText("");
        PemeriksaanHematologi.setText("");
        PemeriksaanUrin.setText("");
        PemeriksaanKimiaDarah.setText("");
        PemeriksaanRadiologi.setText("");
        PemeriksaanEKG.setText("");
        Kesimpulan.setText("");
        Saran.setText("");
        TglAsuhan.setDate(new Date());
        TabRawat.setSelectedIndex(1);
        RiwayatPenyakitSekarang.requestFocus();
    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            TglLahir.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
            Jk.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
            KdDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            NmDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
            Valid.SetTgl2(TglAsuhan, tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString());
            RiwayatPenyakitSekarang.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString());
            RiwayatPenyakitDahulu.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString());
            RiwayatPenyakitKeluarga.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString());
            RiwayatKebiasaan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString());
            TandaVital.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString());
            BeratTinggi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 13).toString());
            KeadaanUmumKepala.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 14).toString());
            KeadaanUmumMulut.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 15).toString());
            KeadaanUmumLeher.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 16).toString());
            KeadaanUmumTHT.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 17).toString());
            KeadaanUmumThorax.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 18).toString());
            KeadaanUmumAbdomen.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 19).toString());
            KeadaanUmumEkstremitas.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 20).toString());
            PemeriksaanHematologi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 21).toString());
            PemeriksaanUrin.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 22).toString());
            PemeriksaanKimiaDarah.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 23).toString());
            PemeriksaanRadiologi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 24).toString());
            PemeriksaanEKG.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 25).toString());
            Kesimpulan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 26).toString());
            Saran.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 27).toString());
        }
    }

    private void hapus() {
        if (Sequel.queryu2tf("delete from hasil_mcu where no_rawat=?", 1, new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
        }) == true) {
            tabMode.removeRow(tbObat.getSelectedRow());
            LCount.setText("" + tabMode.getRowCount());
            emptTeks();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
        }
    }

    private void ganti() {
        if (Sequel.mengedittf("hasil_mcu", "no_rawat=?", "no_rawat=?,tanggal=?,kd_dokter=?,riwayat_sekarang=?,riwayat_dahulu=?,"
                + "riwayat_keluarga=?,riwayat_kebiasaan=?,tanda_vital=?,bb_tb=?,kepala=?,mulut=?,leher=?,tht=?,thorax=?,abdomen=?,"
                + "ekstremitas=?,hematologi=?,urin=?,kimia_darah=?,radiologi=?,ekg=?,kesimpulan=?,saran=?", 24, new String[]{
            TNoRw.getText(), Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + " " + TglAsuhan.getSelectedItem().toString().substring(11, 19), KdDokter.getText(),
            RiwayatPenyakitSekarang.getText(), RiwayatPenyakitDahulu.getText(), RiwayatPenyakitKeluarga.getText(), RiwayatKebiasaan.getText(),
            TandaVital.getText(), BeratTinggi.getText(), KeadaanUmumKepala.getText(), KeadaanUmumMulut.getText(), KeadaanUmumLeher.getText(),
            KeadaanUmumTHT.getText(), KeadaanUmumThorax.getText(), KeadaanUmumAbdomen.getText(), KeadaanUmumEkstremitas.getText(),
            PemeriksaanHematologi.getText(), PemeriksaanUrin.getText(), PemeriksaanKimiaDarah.getText(), PemeriksaanRadiologi.getText(),
            PemeriksaanEKG.getText(), Kesimpulan.getText(), Saran.getText(), tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
        }) == true) {

            tabMode.setValueAt(TNoRw.getText(), tbObat.getSelectedRow(), 0);
            tabMode.setValueAt(TNoRM.getText(), tbObat.getSelectedRow(), 1);
            tabMode.setValueAt(TPasien.getText(), tbObat.getSelectedRow(), 2);
            tabMode.setValueAt(TglLahir.getText(), tbObat.getSelectedRow(), 3);
            tabMode.setValueAt(Jk.getText(), tbObat.getSelectedRow(), 4);
            tabMode.setValueAt(KdDokter.getText(), tbObat.getSelectedRow(), 5);
            tabMode.setValueAt(NmDokter.getText(), tbObat.getSelectedRow(), 6);
            tabMode.setValueAt(Valid.SetTgl(TglAsuhan.getSelectedItem() + "") + " " + TglAsuhan.getSelectedItem().toString().substring(11, 19), tbObat.getSelectedRow(), 7);
            tabMode.setValueAt(RiwayatPenyakitSekarang.getText(), tbObat.getSelectedRow(), 8);
            tabMode.setValueAt(RiwayatPenyakitDahulu.getText(), tbObat.getSelectedRow(), 9);
            tabMode.setValueAt(RiwayatPenyakitKeluarga.getText(), tbObat.getSelectedRow(), 10);
            tabMode.setValueAt(RiwayatKebiasaan.getText(), tbObat.getSelectedRow(), 11);
            tabMode.setValueAt(TandaVital.getText(), tbObat.getSelectedRow(), 12);
            tabMode.setValueAt(BeratTinggi.getText(), tbObat.getSelectedRow(), 13);
            tabMode.setValueAt(KeadaanUmumKepala.getText(), tbObat.getSelectedRow(), 14);
            tabMode.setValueAt(KeadaanUmumMulut.getText(), tbObat.getSelectedRow(), 15);
            tabMode.setValueAt(KeadaanUmumLeher.getText(), tbObat.getSelectedRow(), 16);
            tabMode.setValueAt(KeadaanUmumTHT.getText(), tbObat.getSelectedRow(), 17);
            tabMode.setValueAt(KeadaanUmumThorax.getText(), tbObat.getSelectedRow(), 18);
            tabMode.setValueAt(KeadaanUmumAbdomen.getText(), tbObat.getSelectedRow(), 19);
            tabMode.setValueAt(KeadaanUmumEkstremitas.getText(), tbObat.getSelectedRow(), 20);
            tabMode.setValueAt(PemeriksaanHematologi.getText(), tbObat.getSelectedRow(), 21);
            tabMode.setValueAt(PemeriksaanUrin.getText(), tbObat.getSelectedRow(), 22);
            tabMode.setValueAt(PemeriksaanKimiaDarah.getText(), tbObat.getSelectedRow(), 23);
            tabMode.setValueAt(PemeriksaanRadiologi.getText(), tbObat.getSelectedRow(), 24);
            tabMode.setValueAt(PemeriksaanEKG.getText(), tbObat.getSelectedRow(), 25);
            tabMode.setValueAt(Kesimpulan.getText(), tbObat.getSelectedRow(), 26);
            tabMode.setValueAt(Saran.getText(), tbObat.getSelectedRow(), 27);

            emptTeks();
        }
    }

    public void isRawat() {
        try {
            ps = koneksi.prepareStatement(
                    "select reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir,reg_periksa.tgl_registrasi "
                    + "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                    + "where reg_periksa.no_rawat=?");
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    DTPCari1.setDate(rs.getDate("tgl_registrasi"));
                    TPasien.setText(rs.getString("nm_pasien"));
                    Jk.setText(rs.getString("jk"));
                    TglLahir.setText(rs.getString("tgl_lahir"));
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
            System.out.println("Notif : " + e);
        }
    }

    public void setNoRm(String norwt, Date tgl1, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        DTPCari1.setDate(tgl1);
        DTPCari2.setDate(tgl2);
        isRawat();
        ChkInput.setSelected(true);
        isForm();
    }

    private void isForm() {
        if (ChkInput.isSelected() == true) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 128));
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
        BtnSimpan.setEnabled(akses.getpenilaian_mcu());
        BtnHapus.setEnabled(akses.getpenilaian_mcu());
        BtnEdit.setEnabled(akses.getpenilaian_mcu());
        if (akses.getjml2() >= 1) {
            KdDokter.setEditable(false);
            BtnDokter.setEnabled(false);
            KdDokter.setText(akses.getkode());
            NmDokter.setText(dokter.tampil3(KdDokter.getText()));
            if (akses.getadmin() == true) {
                KdDokter.setEditable(true);
                BtnDokter.setEnabled(true);
            }
        }
    }

    public void setTampil() {
        TabRawat.setSelectedIndex(1);
        tampil();
    }

    /**
     * Generate QR Code image for doctor information
     */
    private ByteArrayInputStream generateQRCodeImage(String text) {
        try {
            // For now, return empty stream as placeholder
            // In future, this can be implemented with ZXing library or HTTP service
            return new ByteArrayInputStream(new byte[0]);
        } catch (Exception e) {
            System.out.println("Error generating QR Code: " + e.getMessage());
            return new ByteArrayInputStream(new byte[0]);
        }
    }

    // Additional missing variables from the form
    private widget.TextBox TCari;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.Label LCount;
    private widget.CekBox ChkInput;
    private javax.swing.JTabbedPane TabRawat;
}