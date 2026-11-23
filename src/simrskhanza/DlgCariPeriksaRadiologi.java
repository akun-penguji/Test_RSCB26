package simrskhanza;

import bridging.ApiOrthanc;
import bridging.ApiSatuSehat;
import bridging.SatuSehatCekNIK;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import bridging.OrthancDICOM;
import bridging.OrthancViewerHybridSplitRad;
import bridging.UploadPDFTte;
import bridging.ViewPDFNextcloud;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kepegawaian.DlgCariPetugas;
import keuangan.Jurnal;
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
import java.awt.event.KeyListener;
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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.HyperlinkEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import kepegawaian.DlgCariDokter;
import laporan.DlgBerkasRawat;
import org.apache.commons.io.FileUtils;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.entity.mime.ByteArrayBody;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
// HttpClient 5 imports
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.util.Timeout;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

// Standard Java imports
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.nio.charset.StandardCharsets;
import javax.swing.SwingUtilities;
import javax.swing.JProgressBar;
import javax.swing.JDialog;
import rekammedis.MasterCariTemplateHasilRadiologi;
import rekammedis.RMRiwayatPerawatan;

public class DlgCariPeriksaRadiologi extends javax.swing.JDialog {

    private final DefaultTableModel tabMode, tabModeDicom;
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private Jurnal jur = new Jurnal();
    private Connection koneksi = koneksiDB.condb();
    private DlgCariPasien member = new DlgCariPasien(null, false);
    private DlgCariDokter dokter = new DlgCariDokter(null, false);
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);
    private MasterCariTemplateHasilRadiologi templatehasil = new MasterCariTemplateHasilRadiologi(null, false);
    private OrthancViewerHybridSplitRad dicomViewer = new OrthancViewerHybridSplitRad(null, false);
    private boolean sukses = false;
    private int i;
    private StringBuilder htmlContent;
    private PreparedStatement ps, ps2, ps3, ps4, ps5, psrekening;
    private ResultSet rs, rs2, rs3, rs5, rsrekening;
    private String kamar, namakamar, pemeriksaan = "", pilihan = "", status = "", finger = "";
    private double ttl = 0, item = 0;
    private double ttljmdokter = 0, ttljmpetugas = 0, ttlkso = 0, ttlpendapatan = 0, ttlbhp = 0, ttljasasarana = 0, ttljmperujuk = 0, ttlmenejemen = 0;
    ;
    private String FileName = "", kodeberkas = "", kdpetugas = "", kdpenjab = "", Suspen_Piutang_Radiologi_Ranap = "", Radiologi_Ranap = "", Beban_Jasa_Medik_Dokter_Radiologi_Ranap = "", Utang_Jasa_Medik_Dokter_Radiologi_Ranap = "",
            Beban_Jasa_Medik_Petugas_Radiologi_Ranap = "", Utang_Jasa_Medik_Petugas_Radiologi_Ranap = "", Beban_Kso_Radiologi_Ranap = "", Utang_Kso_Radiologi_Ranap = "",
            HPP_Persediaan_Radiologi_Rawat_Inap = "", Persediaan_BHP_Radiologi_Rawat_Inap = "", Beban_Jasa_Sarana_Radiologi_Ranap = "", Utang_Jasa_Sarana_Radiologi_Ranap = "",
            Beban_Jasa_Perujuk_Radiologi_Ranap = "", Utang_Jasa_Perujuk_Radiologi_Ranap = "", Beban_Jasa_Menejemen_Radiologi_Ranap = "", Utang_Jasa_Menejemen_Radiologi_Ranap = "",
            Suspen_Piutang_Radiologi_Ralan = "", Radiologi_Ralan = "", Beban_Jasa_Medik_Dokter_Radiologi_Ralan = "", Utang_Jasa_Medik_Dokter_Radiologi_Ralan = "",
            Beban_Jasa_Medik_Petugas_Radiologi_Ralan = "", Utang_Jasa_Medik_Petugas_Radiologi_Ralan = "", Beban_Kso_Radiologi_Ralan = "", Utang_Kso_Radiologi_Ralan = "",
            HPP_Persediaan_Radiologi_Rawat_Jalan = "", Persediaan_BHP_Radiologi_Rawat_Jalan = "", Beban_Jasa_Sarana_Radiologi_Ralan = "", Utang_Jasa_Sarana_Radiologi_Ralan = "",
            Beban_Jasa_Perujuk_Radiologi_Ralan = "", Utang_Jasa_Perujuk_Radiologi_Ralan = "", Beban_Jasa_Menejemen_Radiologi_Ralan = "", Utang_Jasa_Menejemen_Radiologi_Ralan = "", json = "";
    private ObjectMapper mapper = new ObjectMapper();
    private ApiSatuSehat apiSatuSehat = new ApiSatuSehat();
    private SatuSehatCekNIK cekViaSatuSehat = new SatuSehatCekNIK();
    private String linkSatuSehat = "";
    private javax.swing.JMenuItem MnKirimEncounter, MnKirimEncounterBatch;
    private javax.swing.JMenuItem MnKirimServiceRequestRadiologi, MnKirimServiceRequestRadiologiBatch;
    private javax.swing.JMenuItem MnKirimSemua, MnKirimSemuaBatch;
    private JsonNode root;
    private String MWL_API_URL = "";
    private String MWL_API_USERNAME = "";
    private String MWL_API_PASSWORD = "";

    /**
     * Creates new form DlgProgramStudi
     *
     * @param parent
     * @param modal
     */
    public DlgCariPeriksaRadiologi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        WindowPhrase.setSize(320, 100);
        initMenuKirimSatuSehat();

        // Tombol "QR Hasil" ditambahkan programatik ke FormPass2 (FlowLayout),
        // menyatu dengan tombol WA. Menampilkan QR link hasil radiologi untuk
        // dipindai pasien (buka via EPasien tanpa login, gate tanggal lahir).
        widget.Button BtnQRHasil = new widget.Button();
        BtnQRHasil.setText("QR Hasil");
        BtnQRHasil.setMnemonic('Q');
        BtnQRHasil.setToolTipText("Buat QR link hasil radiologi untuk pasien (Alt+Q)");
        BtnQRHasil.setName("BtnQRHasil");
        BtnQRHasil.setPreferredSize(new java.awt.Dimension(140, 30));
        BtnQRHasil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tampilQRHasilRadiologi();
            }
        });
        FormPass2.add(BtnQRHasil);
        FormPass2.revalidate();

        Object[] row = {"No.Rawat", "Pasien", "Petugas", "Tgl.Periksa", "Jam Periksa", "Dokter Perujuk", "Penanggung Jawab", "No. Telp", "Status WA", "Modality", "No.Order"};
        tabMode = new DefaultTableModel(null, row) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbDokter.setModel(tabMode);

        tbDokter.setPreferredScrollableViewportSize(new Dimension(800, 800));
        tbDokter.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 11; i++) {
            TableColumn column = tbDokter.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(110);
            } else if (i == 1) {
                column.setPreferredWidth(350);
            } else if (i == 2) {
                column.setPreferredWidth(150);
            } else if (i == 3) {
                column.setPreferredWidth(200);
            } else if (i == 4) {
                column.setPreferredWidth(80);
            } else if (i == 5) {
                column.setPreferredWidth(170);
            } else if (i == 6) {
                column.setPreferredWidth(170);
            } else if (i == 7) {
                column.setPreferredWidth(100);
            } else if (i == 8) {
                column.setPreferredWidth(80);
            } else if (i == 9) {
                column.setPreferredWidth(80);
            } else if (i == 10) {
                column.setPreferredWidth(140);
            }
        }
        tbDokter.setDefaultRenderer(Object.class, new WarnaTable() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    try {
                        Object statusWa = table.getValueAt(row, 8);
                        if (statusWa != null && "Terkirim".equals(statusWa.toString())) {
                            component.setBackground(new java.awt.Color(198, 239, 206));
                        }
                    } catch (Exception e) {
                    }
                }
                return component;
            }
        });

        tabModeDicom = new DefaultTableModel(null, new Object[]{
            "UUID Pasien", "ID Studies", "ID Series"}) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbListDicom.setModel(tabModeDicom);
        tbListDicom.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbListDicom.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 3; i++) {
            TableColumn column = tbListDicom.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(100);
            } else if (i == 1) {
                column.setPreferredWidth(310);
            } else if (i == 2) {
                column.setPreferredWidth(310);
            }
        }
        tbListDicom.setDefaultRenderer(Object.class, new WarnaTable());

        NoRawat.setDocument(new batasInput((byte) 17).getKata(NoRawat));
        kdmem.setDocument(new batasInput((byte) 8).getKata(kdmem));
        kdptg.setDocument(new batasInput((byte) 25).getKata(kdptg));
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
        member.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (akses.getform().equals("DlgCariPeriksaRadiologi")) {
                    if (member.getTable().getSelectedRow() != -1) {
                        kdmem.setText(member.getTable().getValueAt(member.getTable().getSelectedRow(), 0).toString());
                        nmmem.setText(member.getTable().getValueAt(member.getTable().getSelectedRow(), 1).toString());
                    }
                    kdmem.requestFocus();
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        member.getTable().addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (akses.getform().equals("DlgCariPeriksaRadiologi")) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        member.dispose();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

        petugas.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (akses.getform().equals("DlgCariPeriksaRadiologi")) {
                    if (petugas.getTable().getSelectedRow() != -1) {
                        if (pilihan.equals("ubahpetugas")) {
                            KdPtgUbah.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                            NmPtgUbah.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                            KdPtgUbah.requestFocus();
                        } else if (pilihan.equals("caripetugas")) {
                            kdptg.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                            nmptg.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                            kdptg.requestFocus();
                        }
                    }

                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (akses.getform().equals("DlgCariPeriksaRadiologi")) {
                    if (dokter.getTable().getSelectedRow() != -1) {
                        if (pilihan.equals("perujuk")) {
                            KodePerujuk.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                            NmPerujuk.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                            KodePerujuk.requestFocus();
                        } else if (pilihan.equals("penjab")) {
                            KodePj.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                            NmDokterPj.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                            KodePj.requestFocus();
                        }
                    }
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        templatehasil.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (akses.getform().equals("DlgCariPeriksaRadiologi")) {
                    if (templatehasil.getTable().getSelectedRow() != -1) {
                        HasilPeriksa.setText(templatehasil.getTable().getValueAt(templatehasil.getTable().getSelectedRow(), 2).toString());
                    }
                    HasilPeriksa.requestFocus();
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });

        try {
            psrekening = koneksi.prepareStatement(
                    "select set_akun_ranap.Suspen_Piutang_Radiologi_Ranap,set_akun_ranap.Radiologi_Ranap,set_akun_ranap.Beban_Jasa_Medik_Dokter_Radiologi_Ranap,"
                    + "set_akun_ranap.Utang_Jasa_Medik_Dokter_Radiologi_Ranap,set_akun_ranap.Beban_Jasa_Medik_Petugas_Radiologi_Ranap,set_akun_ranap.Utang_Jasa_Medik_Petugas_Radiologi_Ranap,"
                    + "set_akun_ranap.Beban_Kso_Radiologi_Ranap,set_akun_ranap.Utang_Kso_Radiologi_Ranap,set_akun_ranap.HPP_Persediaan_Radiologi_Rawat_Inap,"
                    + "set_akun_ranap.Persediaan_BHP_Radiologi_Rawat_Inap,set_akun_ranap.Beban_Jasa_Sarana_Radiologi_Ranap,set_akun_ranap.Utang_Jasa_Sarana_Radiologi_Ranap,"
                    + "set_akun_ranap.Beban_Jasa_Perujuk_Radiologi_Ranap,set_akun_ranap.Utang_Jasa_Perujuk_Radiologi_Ranap,set_akun_ranap.Beban_Jasa_Menejemen_Radiologi_Ranap,"
                    + "set_akun_ranap.Utang_Jasa_Menejemen_Radiologi_Ranap from set_akun_ranap");
            try {
                rsrekening = psrekening.executeQuery();
                while (rsrekening.next()) {
                    Suspen_Piutang_Radiologi_Ranap = rsrekening.getString("Suspen_Piutang_Radiologi_Ranap");
                    Radiologi_Ranap = rsrekening.getString("Radiologi_Ranap");
                    Beban_Jasa_Medik_Dokter_Radiologi_Ranap = rsrekening.getString("Beban_Jasa_Medik_Dokter_Radiologi_Ranap");
                    Utang_Jasa_Medik_Dokter_Radiologi_Ranap = rsrekening.getString("Utang_Jasa_Medik_Dokter_Radiologi_Ranap");
                    Beban_Jasa_Medik_Petugas_Radiologi_Ranap = rsrekening.getString("Beban_Jasa_Medik_Petugas_Radiologi_Ranap");
                    Utang_Jasa_Medik_Petugas_Radiologi_Ranap = rsrekening.getString("Utang_Jasa_Medik_Petugas_Radiologi_Ranap");
                    Beban_Kso_Radiologi_Ranap = rsrekening.getString("Beban_Kso_Radiologi_Ranap");
                    Utang_Kso_Radiologi_Ranap = rsrekening.getString("Utang_Kso_Radiologi_Ranap");
                    HPP_Persediaan_Radiologi_Rawat_Inap = rsrekening.getString("HPP_Persediaan_Radiologi_Rawat_Inap");
                    Persediaan_BHP_Radiologi_Rawat_Inap = rsrekening.getString("Persediaan_BHP_Radiologi_Rawat_Inap");
                    Beban_Jasa_Sarana_Radiologi_Ranap = rsrekening.getString("Beban_Jasa_Sarana_Radiologi_Ranap");
                    Utang_Jasa_Sarana_Radiologi_Ranap = rsrekening.getString("Utang_Jasa_Sarana_Radiologi_Ranap");
                    Beban_Jasa_Perujuk_Radiologi_Ranap = rsrekening.getString("Beban_Jasa_Perujuk_Radiologi_Ranap");
                    Utang_Jasa_Perujuk_Radiologi_Ranap = rsrekening.getString("Utang_Jasa_Perujuk_Radiologi_Ranap");
                    Beban_Jasa_Menejemen_Radiologi_Ranap = rsrekening.getString("Beban_Jasa_Menejemen_Radiologi_Ranap");
                    Utang_Jasa_Menejemen_Radiologi_Ranap = rsrekening.getString("Utang_Jasa_Menejemen_Radiologi_Ranap");
                }
            } catch (Exception e) {
                System.out.println("Notif Rekening : " + e);
            } finally {
                if (rsrekening != null) {
                    rsrekening.close();
                }
                if (psrekening != null) {
                    psrekening.close();
                }
            }

            psrekening = koneksi.prepareStatement(
                    "select set_akun_ralan.Suspen_Piutang_Radiologi_Ralan,set_akun_ralan.Radiologi_Ralan,set_akun_ralan.Beban_Jasa_Medik_Dokter_Radiologi_Ralan,"
                    + "set_akun_ralan.Utang_Jasa_Medik_Dokter_Radiologi_Ralan,set_akun_ralan.Beban_Jasa_Medik_Petugas_Radiologi_Ralan,set_akun_ralan.Utang_Jasa_Medik_Petugas_Radiologi_Ralan,"
                    + "set_akun_ralan.Beban_Kso_Radiologi_Ralan,set_akun_ralan.Utang_Kso_Radiologi_Ralan,set_akun_ralan.HPP_Persediaan_Radiologi_Rawat_Jalan,"
                    + "set_akun_ralan.Persediaan_BHP_Radiologi_Rawat_Jalan,set_akun_ralan.Beban_Jasa_Sarana_Radiologi_Ralan,set_akun_ralan.Utang_Jasa_Sarana_Radiologi_Ralan,"
                    + "set_akun_ralan.Beban_Jasa_Perujuk_Radiologi_Ralan,set_akun_ralan.Utang_Jasa_Perujuk_Radiologi_Ralan,set_akun_ralan.Beban_Jasa_Menejemen_Radiologi_Ralan,"
                    + "set_akun_ralan.Utang_Jasa_Menejemen_Radiologi_Ralan from set_akun_ralan");
            try {
                rsrekening = psrekening.executeQuery();
                while (rsrekening.next()) {
                    Suspen_Piutang_Radiologi_Ralan = rsrekening.getString("Suspen_Piutang_Radiologi_Ralan");
                    Radiologi_Ralan = rsrekening.getString("Radiologi_Ralan");
                    Beban_Jasa_Medik_Dokter_Radiologi_Ralan = rsrekening.getString("Beban_Jasa_Medik_Dokter_Radiologi_Ralan");
                    Utang_Jasa_Medik_Dokter_Radiologi_Ralan = rsrekening.getString("Utang_Jasa_Medik_Dokter_Radiologi_Ralan");
                    Beban_Jasa_Medik_Petugas_Radiologi_Ralan = rsrekening.getString("Beban_Jasa_Medik_Petugas_Radiologi_Ralan");
                    Utang_Jasa_Medik_Petugas_Radiologi_Ralan = rsrekening.getString("Utang_Jasa_Medik_Petugas_Radiologi_Ralan");
                    Beban_Kso_Radiologi_Ralan = rsrekening.getString("Beban_Kso_Radiologi_Ralan");
                    Utang_Kso_Radiologi_Ralan = rsrekening.getString("Utang_Kso_Radiologi_Ralan");
                    HPP_Persediaan_Radiologi_Rawat_Jalan = rsrekening.getString("HPP_Persediaan_Radiologi_Rawat_Jalan");
                    Persediaan_BHP_Radiologi_Rawat_Jalan = rsrekening.getString("Persediaan_BHP_Radiologi_Rawat_Jalan");
                    Beban_Jasa_Sarana_Radiologi_Ralan = rsrekening.getString("Beban_Jasa_Sarana_Radiologi_Ralan");
                    Utang_Jasa_Sarana_Radiologi_Ralan = rsrekening.getString("Utang_Jasa_Sarana_Radiologi_Ralan");
                    Beban_Jasa_Perujuk_Radiologi_Ralan = rsrekening.getString("Beban_Jasa_Perujuk_Radiologi_Ralan");
                    Utang_Jasa_Perujuk_Radiologi_Ralan = rsrekening.getString("Utang_Jasa_Perujuk_Radiologi_Ralan");
                    Beban_Jasa_Menejemen_Radiologi_Ralan = rsrekening.getString("Beban_Jasa_Menejemen_Radiologi_Ralan");
                    Utang_Jasa_Menejemen_Radiologi_Ralan = rsrekening.getString("Utang_Jasa_Menejemen_Radiologi_Ralan");
                }
            } catch (Exception e) {
                System.out.println("Notif Rekening : " + e);
            } finally {
                if (rsrekening != null) {
                    rsrekening.close();
                }
                if (psrekening != null) {
                    psrekening.close();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        HTMLEditorKit kit = new HTMLEditorKit();
        LoadHTML.setEditable(true);
        LoadHTML.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule(
                ".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"
                + ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}"
                + ".isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"
                + ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"
                + ".isi5 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#AA0000;}"
                + ".isi6 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#FF0000;}"
                + ".isi7 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#C8C800;}"
                + ".isi8 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#00AA00;}"
                + ".isi9 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#969696;}"
        );
        Document doc = kit.createDefaultDocument();
        LoadHTML.setDocument(doc);
        LoadHTML.setEditable(false);
        LoadHTML.addHyperlinkListener(e -> {
            if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(e.getURL().toURI());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        ChkAccor.setSelected(false);
        isPhoto();

        try {
            String urlBase = koneksiDB.URLORTHANC();
            String port = koneksiDB.PORTAPIORTHANC();
            if (urlBase == null || urlBase.trim().isEmpty()) {
                urlBase = "http://100.89.218.55";
            }
            // Default port 5001 sesuai endpoint image-to-dicom MWL API yang aktif.
            // Port 5000 (Orthanc native DICOM-web) tidak punya endpoint ini.
            if (port == null || port.trim().isEmpty()) {
                port = "5001";
            }
            MWL_API_URL = urlBase + ":" + port;
            MWL_API_USERNAME = koneksiDB.USERAPIORTHANC();
            MWL_API_PASSWORD = koneksiDB.PASSAPIORTHANC();
            if (MWL_API_USERNAME == null || MWL_API_USERNAME.trim().isEmpty()) {
                MWL_API_USERNAME = "admin";
            }
            if (MWL_API_PASSWORD == null || MWL_API_PASSWORD.trim().isEmpty()) {
                MWL_API_PASSWORD = "admin123";
            }
        } catch (Exception e) {
            System.out.println("Error loading MWL Config: " + e);
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

        Kd2 = new widget.TextBox();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        ppDicomImage = new javax.swing.JMenuItem();
        MnCetakNota = new javax.swing.JMenuItem();
        MnUbahDokterPetugas = new javax.swing.JMenuItem();
        ppBerkasDigital = new javax.swing.JMenuItem();
        ppRiwayat = new javax.swing.JMenuItem();
        UploadPDF = new javax.swing.JMenuItem();
        PDFExpertiseGambarRadiologi = new javax.swing.JMenuItem();
        TagSignerDokter = new javax.swing.JMenuItem();
        TagSignerDokterBatch = new javax.swing.JMenuItem();
        MnTte = new javax.swing.JMenuItem();
        Penjab = new widget.TextBox();
        Jk = new widget.TextBox();
        Umur = new widget.TextBox();
        Alamat = new widget.TextBox();
        NoRM = new widget.TextBox();
        WindowGantiDokterParamedis = new javax.swing.JDialog();
        internalFrame5 = new widget.InternalFrame();
        FormInput = new widget.panelisi();
        BtnSimpan4 = new widget.Button();
        BtnCloseIn4 = new widget.Button();
        jLabel7 = new widget.Label();
        KodePj = new widget.TextBox();
        NmDokterPj = new widget.TextBox();
        btnDokterPj = new widget.Button();
        btnDokter = new widget.Button();
        NmPerujuk = new widget.TextBox();
        KodePerujuk = new widget.TextBox();
        jLabel9 = new widget.Label();
        jLabel12 = new widget.Label();
        KdPtgUbah = new widget.TextBox();
        NmPtgUbah = new widget.TextBox();
        btnPetugas1 = new widget.Button();
        Petugas = new widget.TextBox();
        WindowPhrase = new javax.swing.JDialog();
        internalFrame8 = new widget.InternalFrame();
        jLabel45 = new widget.Label();
        panelisi5 = new widget.panelisi();
        BtnClosePhrase = new widget.Button();
        BtnSimpanTandaTangan = new widget.Button();
        jLabel46 = new widget.Label();
        Phrase = new widget.TextBox();
        internalFrame1 = new widget.InternalFrame();
        scrollPane1 = new widget.ScrollPane();
        tbDokter = new widget.Table();
        panelisi3 = new widget.panelisi();
        label15 = new widget.Label();
        NoRawat = new widget.TextBox();
        label11 = new widget.Label();
        Tgl1 = new widget.Tanggal();
        label16 = new widget.Label();
        label13 = new widget.Label();
        kdmem = new widget.TextBox();
        kdptg = new widget.TextBox();
        nmmem = new widget.TextBox();
        nmptg = new widget.TextBox();
        btnPasien = new widget.Button();
        btnPetugas = new widget.Button();
        label18 = new widget.Label();
        Tgl2 = new widget.Tanggal();
        panelisi1 = new widget.panelisi();
        label10 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        label9 = new widget.Label();
        BtnHapus = new widget.Button();
        BtnAll = new widget.Button();
        BtnPrint = new widget.Button();
        BtnKeluar = new widget.Button();
        BtnConvertKirimOrthancBatch = new widget.Button();
        PanelAccor = new widget.PanelBiasa();
        ChkAccor = new widget.CekBox();
        TabData = new javax.swing.JTabbedPane();
        FormPhoto = new widget.PanelBiasa();
        FormPass2 = new widget.PanelBiasa();
        btnAmbilPhoto = new widget.Button();
        BtnRefreshPhoto = new widget.Button();
        btnKirimWa = new widget.Button();
        btnKirimWaDokterPJ = new widget.Button();
        btnKirimWaDokterPengirim = new widget.Button();
        BtnConvertKirimOrthanc = new widget.Button();
        Scroll4 = new widget.ScrollPane();
        LoadHTML = new widget.editorpane();
        FormHasilRadiologi = new widget.PanelBiasa();
        Scroll3 = new widget.ScrollPane();
        HasilPeriksa = new widget.TextArea();
        panelGlass6 = new widget.panelisi();
        btnAmbilPhoto1 = new widget.Button();
        BtnSimpan = new widget.Button();
        BtnPrint1 = new widget.Button();
        FormOrthan = new widget.PanelBiasa();
        Scroll5 = new widget.ScrollPane();
        tbListDicom = new widget.Table();
        panelGlass7 = new widget.panelisi();
        btnDicom = new widget.Button();
        PanelDataDicari = new widget.panelisi();
        label17 = new widget.Label();
        NoRawatDicari = new widget.Label();
        label19 = new widget.Label();
        TglDicari = new widget.Label();
        JamDicari = new widget.Label();
        label20 = new widget.Label();
        PasienDicari = new widget.Label();

        Kd2.setName("Kd2"); // NOI18N
        Kd2.setPreferredSize(new java.awt.Dimension(207, 23));

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        ppDicomImage.setBackground(new java.awt.Color(255, 255, 254));
        ppDicomImage.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppDicomImage.setForeground(new java.awt.Color(50, 50, 50));
        ppDicomImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppDicomImage.setText("Tampilkan Hasil Pemeriksaan + DICOM");
        ppDicomImage.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppDicomImage.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppDicomImage.setName("ppDicomImage"); // NOI18N
        ppDicomImage.setPreferredSize(new java.awt.Dimension(220, 26));
        ppDicomImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppDicomImageBtnPrintActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppDicomImage);

        MnCetakNota.setBackground(new java.awt.Color(255, 255, 254));
        MnCetakNota.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnCetakNota.setForeground(new java.awt.Color(50, 50, 50));
        MnCetakNota.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnCetakNota.setText("Cetak Nota Radiologi");
        MnCetakNota.setName("MnCetakNota"); // NOI18N
        MnCetakNota.setPreferredSize(new java.awt.Dimension(220, 28));
        MnCetakNota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnCetakNotaActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnCetakNota);

        MnUbahDokterPetugas.setBackground(new java.awt.Color(255, 255, 254));
        MnUbahDokterPetugas.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnUbahDokterPetugas.setForeground(new java.awt.Color(50, 50, 50));
        MnUbahDokterPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnUbahDokterPetugas.setText("Ubah P.J.Lab, Perujuk & Petugas");
        MnUbahDokterPetugas.setName("MnUbahDokterPetugas"); // NOI18N
        MnUbahDokterPetugas.setPreferredSize(new java.awt.Dimension(220, 28));
        MnUbahDokterPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnUbahDokterPetugasActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnUbahDokterPetugas);

        ppBerkasDigital.setBackground(new java.awt.Color(255, 255, 254));
        ppBerkasDigital.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppBerkasDigital.setForeground(new java.awt.Color(50, 50, 50));
        ppBerkasDigital.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppBerkasDigital.setText("Berkas Digital Perawatan");
        ppBerkasDigital.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppBerkasDigital.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppBerkasDigital.setName("ppBerkasDigital"); // NOI18N
        ppBerkasDigital.setPreferredSize(new java.awt.Dimension(220, 28));
        ppBerkasDigital.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppBerkasDigitalBtnPrintActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppBerkasDigital);

        ppRiwayat.setBackground(new java.awt.Color(255, 255, 254));
        ppRiwayat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppRiwayat.setForeground(new java.awt.Color(50, 50, 50));
        ppRiwayat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppRiwayat.setText("Riwayat Perawatan");
        ppRiwayat.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppRiwayat.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppRiwayat.setName("ppRiwayat"); // NOI18N
        ppRiwayat.setPreferredSize(new java.awt.Dimension(220, 28));
        ppRiwayat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppRiwayatBtnPrintActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppRiwayat);

        UploadPDF.setBackground(new java.awt.Color(255, 255, 254));
        UploadPDF.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        UploadPDF.setForeground(new java.awt.Color(50, 50, 50));
        UploadPDF.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        UploadPDF.setText("Upload PDF ke Berkas Digital");
        UploadPDF.setName("UploadPDF"); // NOI18N
        UploadPDF.setPreferredSize(new java.awt.Dimension(220, 26));
        UploadPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UploadPDFActionPerformed(evt);
            }
        });
        jPopupMenu1.add(UploadPDF);

        PDFExpertiseGambarRadiologi.setBackground(new java.awt.Color(255, 255, 254));
        PDFExpertiseGambarRadiologi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        PDFExpertiseGambarRadiologi.setForeground(new java.awt.Color(50, 50, 50));
        PDFExpertiseGambarRadiologi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        PDFExpertiseGambarRadiologi.setText("PDF Expertise Gambar Radiologi");
        PDFExpertiseGambarRadiologi.setName("PDFExpertiseGambarRadiologi"); // NOI18N
        PDFExpertiseGambarRadiologi.setPreferredSize(new java.awt.Dimension(220, 26));
        PDFExpertiseGambarRadiologi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PDFExpertiseGambarRadiologiActionPerformed(evt);
            }
        });
        jPopupMenu1.add(PDFExpertiseGambarRadiologi);

        TagSignerDokter.setBackground(new java.awt.Color(255, 255, 254));
        TagSignerDokter.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TagSignerDokter.setForeground(new java.awt.Color(50, 50, 50));
        TagSignerDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        TagSignerDokter.setText("Tag Signer Dokter");
        TagSignerDokter.setName("TagSignerDokter"); // NOI18N
        TagSignerDokter.setPreferredSize(new java.awt.Dimension(220, 26));
        TagSignerDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TagSignerDokterActionPerformed(evt);
            }
        });
        jPopupMenu1.add(TagSignerDokter);

        TagSignerDokterBatch.setBackground(new java.awt.Color(255, 255, 254));
        TagSignerDokterBatch.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TagSignerDokterBatch.setForeground(new java.awt.Color(50, 50, 50));
        TagSignerDokterBatch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        TagSignerDokterBatch.setText("Tag Signer Dokter Batch");
        TagSignerDokterBatch.setName("TagSignerDokterBatch"); // NOI18N
        TagSignerDokterBatch.setPreferredSize(new java.awt.Dimension(220, 26));
        TagSignerDokterBatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TagSignerDokterBatchActionPerformed(evt);
            }
        });
        jPopupMenu1.add(TagSignerDokterBatch);

        MnTte.setBackground(new java.awt.Color(255, 255, 254));
        MnTte.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnTte.setForeground(new java.awt.Color(50, 50, 50));
        MnTte.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnTte.setText("Tanda Tangan Elektronik (TTE) BSrE");
        MnTte.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnTte.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnTte.setName("MnTte"); // NOI18N
        MnTte.setPreferredSize(new java.awt.Dimension(250, 26));
        MnTte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnTteBtnPrintActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnTte);

        Penjab.setEditable(false);
        Penjab.setFocusTraversalPolicyProvider(true);
        Penjab.setName("Penjab"); // NOI18N
        Penjab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PenjabKeyPressed(evt);
            }
        });

        Jk.setEditable(false);
        Jk.setFocusTraversalPolicyProvider(true);
        Jk.setName("Jk"); // NOI18N

        Umur.setEditable(false);
        Umur.setHighlighter(null);
        Umur.setName("Umur"); // NOI18N

        Alamat.setEditable(false);
        Alamat.setHighlighter(null);
        Alamat.setName("Alamat"); // NOI18N

        NoRM.setName("NoRM"); // NOI18N
        NoRM.setPreferredSize(new java.awt.Dimension(207, 23));

        WindowGantiDokterParamedis.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        WindowGantiDokterParamedis.setName("WindowGantiDokterParamedis"); // NOI18N
        WindowGantiDokterParamedis.setUndecorated(true);
        WindowGantiDokterParamedis.setResizable(false);

        internalFrame5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)), "::[ Ubah P.J.Rad, Perujuk & Petugas ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame5.setName("internalFrame5"); // NOI18N
        internalFrame5.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(89, 434));
        FormInput.setLayout(null);

        BtnSimpan4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan4.setMnemonic('S');
        BtnSimpan4.setText("Simpan");
        BtnSimpan4.setToolTipText("Alt+S");
        BtnSimpan4.setName("BtnSimpan4"); // NOI18N
        BtnSimpan4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpan4ActionPerformed(evt);
            }
        });
        FormInput.add(BtnSimpan4);
        BtnSimpan4.setBounds(470, 15, 100, 30);

        BtnCloseIn4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/cross.png"))); // NOI18N
        BtnCloseIn4.setMnemonic('U');
        BtnCloseIn4.setText("Tutup");
        BtnCloseIn4.setToolTipText("Alt+U");
        BtnCloseIn4.setName("BtnCloseIn4"); // NOI18N
        BtnCloseIn4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCloseIn4ActionPerformed(evt);
            }
        });
        FormInput.add(BtnCloseIn4);
        BtnCloseIn4.setBounds(470, 60, 100, 30);

        jLabel7.setText("Dokter P.J. :");
        jLabel7.setName("jLabel7"); // NOI18N
        FormInput.add(jLabel7);
        jLabel7.setBounds(0, 12, 92, 23);

        KodePj.setEditable(false);
        KodePj.setName("KodePj"); // NOI18N
        FormInput.add(KodePj);
        KodePj.setBounds(95, 12, 113, 23);

        NmDokterPj.setEditable(false);
        NmDokterPj.setHighlighter(null);
        NmDokterPj.setName("NmDokterPj"); // NOI18N
        FormInput.add(NmDokterPj);
        NmDokterPj.setBounds(210, 12, 208, 23);

        btnDokterPj.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnDokterPj.setMnemonic('4');
        btnDokterPj.setToolTipText("ALt+4");
        btnDokterPj.setName("btnDokterPj"); // NOI18N
        btnDokterPj.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDokterPjActionPerformed(evt);
            }
        });
        FormInput.add(btnDokterPj);
        btnDokterPj.setBounds(420, 12, 28, 23);

        btnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnDokter.setMnemonic('4');
        btnDokter.setToolTipText("ALt+4");
        btnDokter.setName("btnDokter"); // NOI18N
        btnDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDokterActionPerformed(evt);
            }
        });
        FormInput.add(btnDokter);
        btnDokter.setBounds(420, 42, 28, 23);

        NmPerujuk.setEditable(false);
        NmPerujuk.setHighlighter(null);
        NmPerujuk.setName("NmPerujuk"); // NOI18N
        FormInput.add(NmPerujuk);
        NmPerujuk.setBounds(210, 42, 208, 23);

        KodePerujuk.setName("KodePerujuk"); // NOI18N
        KodePerujuk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodePerujukKeyPressed(evt);
            }
        });
        FormInput.add(KodePerujuk);
        KodePerujuk.setBounds(95, 42, 113, 23);

        jLabel9.setText("Dokter Perujuk :");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(0, 42, 92, 23);

        jLabel12.setText("Petugas :");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(0, 72, 92, 23);

        KdPtgUbah.setName("KdPtgUbah"); // NOI18N
        KdPtgUbah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdPtgUbahKeyPressed(evt);
            }
        });
        FormInput.add(KdPtgUbah);
        KdPtgUbah.setBounds(95, 72, 113, 23);

        NmPtgUbah.setEditable(false);
        NmPtgUbah.setName("NmPtgUbah"); // NOI18N
        FormInput.add(NmPtgUbah);
        NmPtgUbah.setBounds(210, 72, 208, 23);

        btnPetugas1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPetugas1.setMnemonic('2');
        btnPetugas1.setToolTipText("Alt+2");
        btnPetugas1.setName("btnPetugas1"); // NOI18N
        btnPetugas1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPetugas1ActionPerformed(evt);
            }
        });
        FormInput.add(btnPetugas1);
        btnPetugas1.setBounds(420, 72, 28, 23);

        internalFrame5.add(FormInput, java.awt.BorderLayout.CENTER);

        WindowGantiDokterParamedis.getContentPane().add(internalFrame5, java.awt.BorderLayout.CENTER);

        Petugas.setEditable(false);
        Petugas.setHighlighter(null);
        Petugas.setName("Petugas"); // NOI18N

        WindowPhrase.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        WindowPhrase.setModal(true);
        WindowPhrase.setName("WindowPhrase"); // NOI18N
        WindowPhrase.setUndecorated(true);
        WindowPhrase.setResizable(false);

        internalFrame8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ E-Sign / Tanda Tangan Elektronik ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame8.setName("internalFrame8"); // NOI18N
        internalFrame8.setLayout(new java.awt.BorderLayout());

        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel45.setText("%");
        jLabel45.setName("jLabel45"); // NOI18N
        internalFrame8.add(jLabel45, java.awt.BorderLayout.CENTER);

        panelisi5.setName("panelisi5"); // NOI18N
        panelisi5.setPreferredSize(new java.awt.Dimension(100, 44));
        panelisi5.setLayout(null);

        BtnClosePhrase.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/cross.png"))); // NOI18N
        BtnClosePhrase.setMnemonic('U');
        BtnClosePhrase.setText("Batal");
        BtnClosePhrase.setToolTipText("Alt+U");
        BtnClosePhrase.setName("BtnClosePhrase"); // NOI18N
        BtnClosePhrase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnClosePhraseActionPerformed(evt);
            }
        });
        panelisi5.add(BtnClosePhrase);
        BtnClosePhrase.setBounds(200, 40, 100, 30);

        BtnSimpanTandaTangan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpanTandaTangan.setMnemonic('S');
        BtnSimpanTandaTangan.setText("Simpan");
        BtnSimpanTandaTangan.setToolTipText("Alt+S");
        BtnSimpanTandaTangan.setName("BtnSimpanTandaTangan"); // NOI18N
        BtnSimpanTandaTangan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanTandaTanganActionPerformed(evt);
            }
        });
        panelisi5.add(BtnSimpanTandaTangan);
        BtnSimpanTandaTangan.setBounds(10, 40, 100, 30);

        jLabel46.setText("Masukkan Passphrase :");
        jLabel46.setName("jLabel46"); // NOI18N
        panelisi5.add(jLabel46);
        jLabel46.setBounds(0, 10, 130, 23);

        Phrase.setToolTipText("Silahkan masukan passphrase");
        Phrase.setHighlighter(null);
        Phrase.setName("Phrase"); // NOI18N
        panelisi5.add(Phrase);
        Phrase.setBounds(134, 10, 160, 23);

        internalFrame8.add(panelisi5, java.awt.BorderLayout.CENTER);

        WindowPhrase.getContentPane().add(internalFrame8, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Pemeriksaan Radiologi ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        scrollPane1.setName("scrollPane1"); // NOI18N
        scrollPane1.setOpaque(true);

        tbDokter.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        tbDokter.setComponentPopupMenu(jPopupMenu1);
        tbDokter.setName("tbDokter"); // NOI18N
        tbDokter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbDokterMouseClicked(evt);
            }
        });
        tbDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbDokterKeyPressed(evt);
            }
        });
        scrollPane1.setViewportView(tbDokter);

        internalFrame1.add(scrollPane1, java.awt.BorderLayout.CENTER);

        panelisi3.setName("panelisi3"); // NOI18N
        panelisi3.setPreferredSize(new java.awt.Dimension(100, 73));
        panelisi3.setLayout(null);

        label15.setText("No.Rawat :");
        label15.setName("label15"); // NOI18N
        label15.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(label15);
        label15.setBounds(0, 10, 75, 23);

        NoRawat.setName("NoRawat"); // NOI18N
        NoRawat.setPreferredSize(new java.awt.Dimension(207, 23));
        NoRawat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoRawatKeyPressed(evt);
            }
        });
        panelisi3.add(NoRawat);
        NoRawat.setBounds(79, 10, 226, 23);

        label11.setText("Tanggal :");
        label11.setName("label11"); // NOI18N
        label11.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(label11);
        label11.setBounds(0, 40, 75, 23);

        Tgl1.setDisplayFormat("dd-MM-yyyy");
        Tgl1.setName("Tgl1"); // NOI18N
        Tgl1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Tgl1KeyPressed(evt);
            }
        });
        panelisi3.add(Tgl1);
        Tgl1.setBounds(79, 40, 100, 23);

        label16.setText("Pasien :");
        label16.setName("label16"); // NOI18N
        label16.setPreferredSize(new java.awt.Dimension(60, 23));
        panelisi3.add(label16);
        label16.setBounds(385, 10, 60, 23);

        label13.setText("Petugas :");
        label13.setName("label13"); // NOI18N
        label13.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(label13);
        label13.setBounds(385, 40, 60, 23);

        kdmem.setName("kdmem"); // NOI18N
        kdmem.setPreferredSize(new java.awt.Dimension(80, 23));
        kdmem.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdmemKeyPressed(evt);
            }
        });
        panelisi3.add(kdmem);
        kdmem.setBounds(449, 10, 80, 23);

        kdptg.setName("kdptg"); // NOI18N
        kdptg.setPreferredSize(new java.awt.Dimension(80, 23));
        kdptg.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdptgKeyPressed(evt);
            }
        });
        panelisi3.add(kdptg);
        kdptg.setBounds(449, 40, 80, 23);

        nmmem.setEditable(false);
        nmmem.setName("nmmem"); // NOI18N
        nmmem.setPreferredSize(new java.awt.Dimension(207, 23));
        panelisi3.add(nmmem);
        nmmem.setBounds(531, 10, 240, 23);

        nmptg.setEditable(false);
        nmptg.setName("nmptg"); // NOI18N
        nmptg.setPreferredSize(new java.awt.Dimension(207, 23));
        panelisi3.add(nmptg);
        nmptg.setBounds(531, 40, 240, 23);

        btnPasien.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPasien.setMnemonic('1');
        btnPasien.setToolTipText("Alt+1");
        btnPasien.setName("btnPasien"); // NOI18N
        btnPasien.setPreferredSize(new java.awt.Dimension(28, 23));
        btnPasien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPasienActionPerformed(evt);
            }
        });
        panelisi3.add(btnPasien);
        btnPasien.setBounds(774, 10, 28, 23);

        btnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPetugas.setMnemonic('2');
        btnPetugas.setToolTipText("Alt+2");
        btnPetugas.setName("btnPetugas"); // NOI18N
        btnPetugas.setPreferredSize(new java.awt.Dimension(28, 23));
        btnPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPetugasActionPerformed(evt);
            }
        });
        panelisi3.add(btnPetugas);
        btnPetugas.setBounds(774, 40, 28, 23);

        label18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label18.setText("s.d.");
        label18.setName("label18"); // NOI18N
        label18.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi3.add(label18);
        label18.setBounds(178, 40, 30, 23);

        Tgl2.setDisplayFormat("dd-MM-yyyy");
        Tgl2.setName("Tgl2"); // NOI18N
        Tgl2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Tgl2KeyPressed(evt);
            }
        });
        panelisi3.add(Tgl2);
        Tgl2.setBounds(205, 40, 100, 23);

        internalFrame1.add(panelisi3, java.awt.BorderLayout.PAGE_START);

        panelisi1.setName("panelisi1"); // NOI18N
        panelisi1.setPreferredSize(new java.awt.Dimension(100, 56));
        panelisi1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        label10.setText("Key Word :");
        label10.setName("label10"); // NOI18N
        label10.setPreferredSize(new java.awt.Dimension(70, 23));
        panelisi1.add(label10);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(170, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelisi1.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('5');
        BtnCari.setToolTipText("Alt+5");
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
        panelisi1.add(BtnCari);

        label9.setName("label9"); // NOI18N
        label9.setPreferredSize(new java.awt.Dimension(100, 30));
        panelisi1.add(label9);

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
        panelisi1.add(BtnHapus);

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
        panelisi1.add(BtnAll);

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
        panelisi1.add(BtnPrint);

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
        panelisi1.add(BtnKeluar);

        BtnConvertKirimOrthancBatch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/swap.png"))); // NOI18N
        BtnConvertKirimOrthancBatch.setMnemonic('T');
        BtnConvertKirimOrthancBatch.setText("Kirim Orthanc Batch");
        BtnConvertKirimOrthancBatch.setToolTipText("Alt+T");
        BtnConvertKirimOrthancBatch.setName("BtnConvertKirimOrthancBatch"); // NOI18N
        BtnConvertKirimOrthancBatch.setPreferredSize(new java.awt.Dimension(200, 30));
        BtnConvertKirimOrthancBatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnConvertKirimOrthancBatchActionPerformed(evt);
            }
        });
        BtnConvertKirimOrthancBatch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnConvertKirimOrthancBatchKeyPressed(evt);
            }
        });
        panelisi1.add(BtnConvertKirimOrthancBatch);

        internalFrame1.add(panelisi1, java.awt.BorderLayout.PAGE_END);

        PanelAccor.setBackground(new java.awt.Color(255, 255, 255));
        PanelAccor.setName("PanelAccor"); // NOI18N
        PanelAccor.setPreferredSize(new java.awt.Dimension(445, 43));
        PanelAccor.setLayout(new java.awt.BorderLayout(1, 1));

        ChkAccor.setBackground(new java.awt.Color(255, 250, 250));
        ChkAccor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kiri.png"))); // NOI18N
        ChkAccor.setSelected(true);
        ChkAccor.setFocusable(false);
        ChkAccor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkAccor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkAccor.setName("ChkAccor"); // NOI18N
        ChkAccor.setPreferredSize(new java.awt.Dimension(15, 20));
        ChkAccor.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kiri.png"))); // NOI18N
        ChkAccor.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kanan.png"))); // NOI18N
        ChkAccor.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kanan.png"))); // NOI18N
        ChkAccor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkAccorActionPerformed(evt);
            }
        });
        PanelAccor.add(ChkAccor, java.awt.BorderLayout.WEST);

        TabData.setBackground(new java.awt.Color(254, 255, 254));
        TabData.setForeground(new java.awt.Color(50, 50, 50));
        TabData.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabData.setName("TabData"); // NOI18N
        TabData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TabDataMouseClicked(evt);
            }
        });

        FormPhoto.setBackground(new java.awt.Color(255, 255, 255));
        FormPhoto.setBorder(null);
        FormPhoto.setName("FormPhoto"); // NOI18N
        FormPhoto.setPreferredSize(new java.awt.Dimension(400, 700));
        FormPhoto.setLayout(new java.awt.BorderLayout());

        FormPass2.setBackground(new java.awt.Color(255, 255, 255));
        FormPass2.setBorder(null);
        FormPass2.setName("FormPass2"); // NOI18N
        FormPass2.setPreferredSize(new java.awt.Dimension(115, 80));

        btnAmbilPhoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        btnAmbilPhoto.setMnemonic('U');
        btnAmbilPhoto.setText("Ambil");
        btnAmbilPhoto.setToolTipText("Alt+U");
        btnAmbilPhoto.setName("btnAmbilPhoto"); // NOI18N
        btnAmbilPhoto.setPreferredSize(new java.awt.Dimension(100, 30));
        btnAmbilPhoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAmbilPhotoActionPerformed(evt);
            }
        });
        FormPass2.add(btnAmbilPhoto);

        BtnRefreshPhoto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/refresh.png"))); // NOI18N
        BtnRefreshPhoto.setMnemonic('U');
        BtnRefreshPhoto.setText("Refresh");
        BtnRefreshPhoto.setToolTipText("Alt+U");
        BtnRefreshPhoto.setName("BtnRefreshPhoto"); // NOI18N
        BtnRefreshPhoto.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnRefreshPhoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnRefreshPhotoActionPerformed(evt);
            }
        });
        FormPass2.add(BtnRefreshPhoto);

        btnKirimWa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/wa.png"))); // NOI18N
        btnKirimWa.setMnemonic('U');
        btnKirimWa.setText("Kirim WA Pasien");
        btnKirimWa.setToolTipText("Alt+U");
        btnKirimWa.setName("btnKirimWa"); // NOI18N
        btnKirimWa.setPreferredSize(new java.awt.Dimension(200, 30));
        btnKirimWa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKirimWaActionPerformed(evt);
            }
        });
        FormPass2.add(btnKirimWa);

        btnKirimWaDokterPJ.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/wa.png"))); // NOI18N
        btnKirimWaDokterPJ.setText("Kirim WA Dokter PJ Radiologi");
        btnKirimWaDokterPJ.setName("btnKirimWaDokterPJ"); // NOI18N
        btnKirimWaDokterPJ.setPreferredSize(new java.awt.Dimension(220, 30));
        btnKirimWaDokterPJ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKirimWaDokterPJActionPerformed(evt);
            }
        });
        FormPass2.add(btnKirimWaDokterPJ);

        btnKirimWaDokterPengirim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/wa.png"))); // NOI18N
        btnKirimWaDokterPengirim.setText("Kirim WA Dokter Pengirim");
        btnKirimWaDokterPengirim.setName("btnKirimWaDokterPengirim"); // NOI18N
        btnKirimWaDokterPengirim.setPreferredSize(new java.awt.Dimension(220, 30));
        btnKirimWaDokterPengirim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKirimWaDokterPengirimActionPerformed(evt);
            }
        });
        FormPass2.add(btnKirimWaDokterPengirim);

        BtnConvertKirimOrthanc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/swap.png"))); // NOI18N
        BtnConvertKirimOrthanc.setMnemonic('T');
        BtnConvertKirimOrthanc.setText("Kirim Orthanc");
        BtnConvertKirimOrthanc.setToolTipText("Alt+T");
        BtnConvertKirimOrthanc.setName("BtnConvertKirimOrthanc"); // NOI18N
        BtnConvertKirimOrthanc.setPreferredSize(new java.awt.Dimension(200, 30));
        BtnConvertKirimOrthanc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnConvertKirimOrthancActionPerformed(evt);
            }
        });
        BtnConvertKirimOrthanc.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnConvertKirimOrthancKeyPressed(evt);
            }
        });
        FormPass2.add(BtnConvertKirimOrthanc);

        FormPhoto.add(FormPass2, java.awt.BorderLayout.PAGE_END);

        Scroll4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        Scroll4.setName("Scroll4"); // NOI18N
        Scroll4.setOpaque(true);
        Scroll4.setPreferredSize(new java.awt.Dimension(400, 400));

        LoadHTML.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        LoadHTML.setName("LoadHTML"); // NOI18N
        Scroll4.setViewportView(LoadHTML);

        FormPhoto.add(Scroll4, java.awt.BorderLayout.CENTER);

        TabData.addTab("Photo Radiologi", FormPhoto);

        FormHasilRadiologi.setBackground(new java.awt.Color(255, 255, 255));
        FormHasilRadiologi.setBorder(null);
        FormHasilRadiologi.setName("FormHasilRadiologi"); // NOI18N
        FormHasilRadiologi.setPreferredSize(new java.awt.Dimension(115, 73));
        FormHasilRadiologi.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        Scroll3.setName("Scroll3"); // NOI18N
        Scroll3.setOpaque(true);

        HasilPeriksa.setBorder(null);
        HasilPeriksa.setColumns(25);
        HasilPeriksa.setRows(40);
        HasilPeriksa.setName("HasilPeriksa"); // NOI18N
        Scroll3.setViewportView(HasilPeriksa);

        FormHasilRadiologi.add(Scroll3, java.awt.BorderLayout.CENTER);

        panelGlass6.setBorder(null);
        panelGlass6.setName("panelGlass6"); // NOI18N
        panelGlass6.setPreferredSize(new java.awt.Dimension(115, 40));

        btnAmbilPhoto1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnAmbilPhoto1.setMnemonic('U');
        btnAmbilPhoto1.setText("Template");
        btnAmbilPhoto1.setToolTipText("Alt+U");
        btnAmbilPhoto1.setName("btnAmbilPhoto1"); // NOI18N
        btnAmbilPhoto1.setPreferredSize(new java.awt.Dimension(105, 30));
        btnAmbilPhoto1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAmbilPhoto1ActionPerformed(evt);
            }
        });
        panelGlass6.add(btnAmbilPhoto1);

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('U');
        BtnSimpan.setText("Update");
        BtnSimpan.setToolTipText("Alt+U");
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
        panelGlass6.add(BtnSimpan);

        BtnPrint1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
        BtnPrint1.setMnemonic('T');
        BtnPrint1.setText("Cetak");
        BtnPrint1.setToolTipText("Alt+T");
        BtnPrint1.setName("BtnPrint1"); // NOI18N
        BtnPrint1.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnPrint1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrint1ActionPerformed(evt);
            }
        });
        BtnPrint1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnPrint1KeyPressed(evt);
            }
        });
        panelGlass6.add(BtnPrint1);

        FormHasilRadiologi.add(panelGlass6, java.awt.BorderLayout.PAGE_END);

        TabData.addTab("Hasil Bacaan Radiologi", FormHasilRadiologi);

        FormOrthan.setBackground(new java.awt.Color(255, 255, 255));
        FormOrthan.setBorder(null);
        FormOrthan.setName("FormOrthan"); // NOI18N
        FormOrthan.setPreferredSize(new java.awt.Dimension(115, 73));
        FormOrthan.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        Scroll5.setName("Scroll5"); // NOI18N
        Scroll5.setOpaque(true);

        tbListDicom.setName("tbListDicom"); // NOI18N
        tbListDicom.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbListDicomMouseClicked(evt);
            }
        });
        tbListDicom.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbListDicomKeyPressed(evt);
            }
        });
        Scroll5.setViewportView(tbListDicom);

        FormOrthan.add(Scroll5, java.awt.BorderLayout.CENTER);

        panelGlass7.setBorder(null);
        panelGlass7.setName("panelGlass7"); // NOI18N
        panelGlass7.setPreferredSize(new java.awt.Dimension(115, 40));

        btnDicom.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        btnDicom.setMnemonic('T');
        btnDicom.setText("Tampilkan DICOM");
        btnDicom.setToolTipText("Alt+T");
        btnDicom.setName("btnDicom"); // NOI18N
        btnDicom.setPreferredSize(new java.awt.Dimension(150, 30));
        btnDicom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDicomActionPerformed(evt);
            }
        });
        panelGlass7.add(btnDicom);

        FormOrthan.add(panelGlass7, java.awt.BorderLayout.PAGE_END);

        TabData.addTab("Integrasi Orthanc", FormOrthan);

        PanelAccor.add(TabData, java.awt.BorderLayout.CENTER);

        PanelDataDicari.setBackground(new java.awt.Color(255, 250, 250));
        PanelDataDicari.setBorder(null);
        PanelDataDicari.setName("PanelDataDicari"); // NOI18N
        PanelDataDicari.setPreferredSize(new java.awt.Dimension(100, 44));
        PanelDataDicari.setWarnaAtas(new java.awt.Color(255, 250, 250));
        PanelDataDicari.setWarnaBawah(new java.awt.Color(255, 250, 250));
        PanelDataDicari.setLayout(null);

        label17.setText("No.Rawat :");
        label17.setName("label17"); // NOI18N
        label17.setPreferredSize(new java.awt.Dimension(65, 23));
        PanelDataDicari.add(label17);
        label17.setBounds(0, 10, 74, 23);

        NoRawatDicari.setBackground(new java.awt.Color(255, 255, 255));
        NoRawatDicari.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        NoRawatDicari.setName("NoRawatDicari"); // NOI18N
        NoRawatDicari.setPreferredSize(new java.awt.Dimension(105, 23));
        PanelDataDicari.add(NoRawatDicari);
        NoRawatDicari.setBounds(78, 10, 105, 23);

        label19.setText("Tanggal :");
        label19.setName("label19"); // NOI18N
        label19.setPreferredSize(new java.awt.Dimension(53, 23));
        PanelDataDicari.add(label19);
        label19.setBounds(185, 10, 55, 23);

        TglDicari.setBackground(new java.awt.Color(255, 255, 255));
        TglDicari.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        TglDicari.setName("TglDicari"); // NOI18N
        TglDicari.setPreferredSize(new java.awt.Dimension(65, 23));
        PanelDataDicari.add(TglDicari);
        TglDicari.setBounds(244, 10, 65, 23);

        JamDicari.setBackground(new java.awt.Color(255, 255, 255));
        JamDicari.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        JamDicari.setName("JamDicari"); // NOI18N
        JamDicari.setPreferredSize(new java.awt.Dimension(52, 23));
        PanelDataDicari.add(JamDicari);
        JamDicari.setBounds(311, 10, 52, 23);

        label20.setText("Pasien :");
        label20.setName("label20"); // NOI18N
        label20.setPreferredSize(new java.awt.Dimension(50, 23));
        PanelDataDicari.add(label20);
        label20.setBounds(368, 10, 51, 23);

        PasienDicari.setBackground(new java.awt.Color(255, 255, 255));
        PasienDicari.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        PasienDicari.setName("PasienDicari"); // NOI18N
        PasienDicari.setPreferredSize(new java.awt.Dimension(470, 23));
        PanelDataDicari.add(PasienDicari);
        PasienDicari.setBounds(423, 10, 270, 23);

        PanelAccor.add(PanelDataDicari, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(PanelAccor, java.awt.BorderLayout.EAST);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
/*
private void KdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TKdKeyPressed
    Valid.pindah(evt,BtnCari,Nm);
}//GEN-LAST:event_TKdKeyPressed
*/

    private void btnPasienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPasienActionPerformed
        akses.setform("DlgCariPeriksaRadiologi");
        member.emptTeks();
        member.isCek();
        member.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        member.setLocationRelativeTo(internalFrame1);
        member.setAlwaysOnTop(false);
        member.setVisible(true);
    }//GEN-LAST:event_btnPasienActionPerformed

    private void btnPetugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPetugasActionPerformed
        akses.setform("DlgCariPeriksaRadiologi");
        pilihan = "caripetugas";
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setAlwaysOnTop(false);
        petugas.setVisible(true);
    }//GEN-LAST:event_btnPetugasActionPerformed

    private void Tgl1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Tgl1KeyPressed
        Valid.pindah(evt, kdmem, Tgl2);
    }//GEN-LAST:event_Tgl1KeyPressed

    private void kdmemKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdmemKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=?", nmmem, kdmem.getText());
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=?", nmmem, kdmem.getText());
            NoRawat.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            btnPasienActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=?", nmmem, kdmem.getText());
            Tgl1.requestFocus();
        }
    }//GEN-LAST:event_kdmemKeyPressed

    private void NoRawatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoRawatKeyPressed
        Valid.pindah(evt, BtnKeluar, kdptg);
    }//GEN-LAST:event_NoRawatKeyPressed

    private void kdptgKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdptgKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            nmptg.setText(petugas.tampil3(kdptg.getText()));
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            nmptg.setText(petugas.tampil3(kdptg.getText()));
            Tgl2.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            btnPetugasActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            nmptg.setText(petugas.tampil3(kdptg.getText()));
            NoRawat.requestFocus();
        }
    }//GEN-LAST:event_kdptgKeyPressed

    private void Tgl2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Tgl2KeyPressed
        Valid.pindah(evt, Tgl1, kdptg);
    }//GEN-LAST:event_Tgl2KeyPressed

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
        NoRawat.setText("");
        kdmem.setText("");
        nmmem.setText("");
        kdptg.setText("");
        nmptg.setText("");
        tampil();
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnAllActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnHapus, BtnKeluar);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        BtnCariActionPerformed(evt);
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            TCari.requestFocus();
        } else if (tabMode.getRowCount() != 0) {

            Sequel.queryu("delete from temporary_radiologi");
            int row = tabMode.getRowCount();
            for (i = 0; i < row; i++) {
                Sequel.menyimpan("temporary_radiologi", "'" + i + "','"
                        + tabMode.getValueAt(i, 0).toString() + "','"
                        + tabMode.getValueAt(i, 1).toString() + "','"
                        + tabMode.getValueAt(i, 2).toString() + "','"
                        + tabMode.getValueAt(i, 3).toString() + "','"
                        + tabMode.getValueAt(i, 4).toString() + "','"
                        + tabMode.getValueAt(i, 5).toString() + "','"
                        + tabMode.getValueAt(i, 6).toString() + "','','','','','','','','','','','','','','','','','','','','','','','','','','','','','',''", "Periksa Radiologi");
            }

            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            Valid.MyReport("rptDataRadiologi.jasper", "report", "::[ Data Pemeriksaan Radiologi ]::", param);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPrintActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnAll, BtnAll);
        }
    }//GEN-LAST:event_BtnPrintKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        petugas.dispose();
        member.dispose();
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            dispose();
        } else {
            Valid.pindah(evt, BtnPrint, NoRawat);
        }
    }//GEN-LAST:event_BtnKeluarKeyPressed

private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
    if (tabMode.getRowCount() == 0) {
        JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
        TCari.requestFocus();
    } else if (tbDokter.getSelectedRow() <= -1) {
        JOptionPane.showMessageDialog(null, "Maaf, Silahkan pilih data..!!");
    } else {
        if (Kd2.getText().equals("") || Petugas.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu...!!!!");
        } else {
            if (Sequel.cariRegistrasi(tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString()) > 0) {
                JOptionPane.showMessageDialog(rootPane, "Data billing sudah terverifikasi, data tidak boleh dihapus.\nSilahkan hubungi bagian kasir/keuangan ..!!");
                TCari.requestFocus();
            } else {
                int reply = JOptionPane.showConfirmDialog(rootPane, "Eeiiiiiits, udah bener belum data yang mau dihapus..??", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.YES_OPTION) {
                    try {
                        Sequel.AutoComitFalse();
                        sukses = true;
                        status = "";
                        ttljmdokter = 0;
                        ttljmpetugas = 0;
                        ttlkso = 0;
                        ttlpendapatan = 0;
                        ttlbhp = 0;
                        ttljasasarana = 0;
                        ttljmperujuk = 0;
                        ttlmenejemen = 0;
                        ttljmdokter = Sequel.cariIsiAngka("select sum(periksa_radiologi.tarif_tindakan_dokter) from periksa_radiologi where periksa_radiologi.no_rawat='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 0)
                                + "' and tgl_periksa='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 3)
                                + "' and jam='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 4) + "'");
                        ttljmpetugas = Sequel.cariIsiAngka("select sum(periksa_radiologi.tarif_tindakan_petugas) from periksa_radiologi where periksa_radiologi.no_rawat='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 0)
                                + "' and tgl_periksa='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 3)
                                + "' and jam='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 4) + "'");
                        ttlkso = Sequel.cariIsiAngka("select sum(periksa_radiologi.kso) from periksa_radiologi where periksa_radiologi.no_rawat='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 0)
                                + "' and tgl_periksa='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 3)
                                + "' and jam='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 4) + "'");
                        ttlbhp = Sequel.cariIsiAngka("select sum(periksa_radiologi.bhp) from periksa_radiologi where periksa_radiologi.no_rawat='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 0)
                                + "' and tgl_periksa='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 3)
                                + "' and jam='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 4) + "'");
                        ttlpendapatan = Sequel.cariIsiAngka("select sum(periksa_radiologi.biaya) from periksa_radiologi where periksa_radiologi.no_rawat='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 0)
                                + "' and tgl_periksa='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 3)
                                + "' and jam='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 4) + "'");
                        ttljasasarana = Sequel.cariIsiAngka("select sum(periksa_radiologi.bagian_rs) from periksa_radiologi where periksa_radiologi.no_rawat='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 0)
                                + "' and tgl_periksa='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 3)
                                + "' and jam='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 4) + "'");
                        ttljmperujuk = Sequel.cariIsiAngka("select sum(periksa_radiologi.tarif_perujuk) from periksa_radiologi where periksa_radiologi.no_rawat='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 0)
                                + "' and tgl_periksa='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 3)
                                + "' and jam='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 4) + "'");
                        ttlmenejemen = Sequel.cariIsiAngka("select sum(periksa_radiologi.menejemen) from periksa_radiologi where periksa_radiologi.no_rawat='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 0)
                                + "' and tgl_periksa='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 3)
                                + "' and jam='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 4) + "'");

                        status = Sequel.cariIsi("select periksa_radiologi.status from periksa_radiologi where periksa_radiologi.no_rawat='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 0)
                                + "' and tgl_periksa='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 3)
                                + "' and jam='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 4) + "'");

                        if (Sequel.queryu2tf("delete from periksa_radiologi where periksa_radiologi.no_rawat=? and tgl_periksa=? and jam=?", 3, new String[]{
                            tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString(), tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString(), tbDokter.getValueAt(tbDokter.getSelectedRow(), 4).toString()
                        }) == true) {
                            Sequel.queryu2("delete from hasil_radiologi where no_rawat=? and tgl_periksa=? and jam=?", 3, new String[]{
                                tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString(), tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString(), tbDokter.getValueAt(tbDokter.getSelectedRow(), 4).toString()
                            });
                            Sequel.queryu2("delete from gambar_radiologi where no_rawat=? and tgl_periksa=? and jam=?", 3, new String[]{
                                tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString(), tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString(), tbDokter.getValueAt(tbDokter.getSelectedRow(), 4).toString()
                            });

                            ttlbhp = ttlbhp + Sequel.cariIsiAngka("select sum(beri_bhp_radiologi.total) from beri_bhp_radiologi where no_rawat='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 0)
                                    + "' and tgl_periksa='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 3)
                                    + "' and jam='" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 4) + "'");
                            if (Sequel.queryu2tf("delete from beri_bhp_radiologi where no_rawat=? and tgl_periksa=? and jam=?", 3, new String[]{
                                tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString(), tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString(), tbDokter.getValueAt(tbDokter.getSelectedRow(), 4).toString()
                            }) == false) {
                                sukses = false;
                            }
                        } else {
                            sukses = false;
                        }

                        if (sukses == true) {
                            if (status.equals("Ranap")) {
                                Sequel.queryu("delete from tampjurnal");
                                if (ttlpendapatan > 0) {
                                    Sequel.menyimpan("tampjurnal", "'" + Suspen_Piutang_Radiologi_Ranap + "','Suspen Piutang Radiologi Ranap','0','" + ttlpendapatan + "'", "kredit=kredit+'" + (ttlpendapatan) + "'", "kd_rek='" + Suspen_Piutang_Radiologi_Ranap + "'");
                                    Sequel.menyimpan("tampjurnal", "'" + Radiologi_Ranap + "','Pendapatan Radiologi Rawat Inap','" + ttlpendapatan + "','0'", "debet=debet+'" + (ttlpendapatan) + "'", "kd_rek='" + Radiologi_Ranap + "'");
                                }
                                if (ttljmdokter > 0) {
                                    Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Medik_Dokter_Radiologi_Ranap + "','Beban Jasa Medik Dokter Radiologi Ranap','0','" + ttljmdokter + "'", "kredit=kredit+'" + (ttljmdokter) + "'", "kd_rek='" + Beban_Jasa_Medik_Dokter_Radiologi_Ranap + "'");
                                    Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Medik_Dokter_Radiologi_Ranap + "','Utang Jasa Medik Dokter Radiologi Ranap','" + ttljmdokter + "','0'", "debet=debet+'" + (ttljmdokter) + "'", "kd_rek='" + Utang_Jasa_Medik_Dokter_Radiologi_Ranap + "'");
                                }
                                if (ttljmpetugas > 0) {
                                    Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Medik_Petugas_Radiologi_Ranap + "','Beban Jasa Medik Petugas Radiologi Ranap','0','" + ttljmpetugas + "'", "kredit=kredit+'" + (ttljmpetugas) + "'", "kd_rek='" + Beban_Jasa_Medik_Petugas_Radiologi_Ranap + "'");
                                    Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Medik_Petugas_Radiologi_Ranap + "','Utang Jasa Medik Petugas Radiologi Ranap','" + ttljmpetugas + "','0'", "debet=debet+'" + (ttljmpetugas) + "'", "kd_rek='" + Utang_Jasa_Medik_Petugas_Radiologi_Ranap + "'");
                                }
                                if (ttlbhp > 0) {
                                    Sequel.menyimpan("tampjurnal", "'" + HPP_Persediaan_Radiologi_Rawat_Inap + "','HPP Persediaan Radiologi Rawat Inap','0','" + ttlbhp + "'", "kredit=kredit+'" + (ttlbhp) + "'", "kd_rek='" + HPP_Persediaan_Radiologi_Rawat_Inap + "'");
                                    Sequel.menyimpan("tampjurnal", "'" + Persediaan_BHP_Radiologi_Rawat_Inap + "','Persediaan BHP Radiologi Rawat Inap','" + ttlbhp + "','0'", "debet=debet+'" + (ttlbhp) + "'", "kd_rek='" + Persediaan_BHP_Radiologi_Rawat_Inap + "'");
                                }
                                if (ttlkso > 0) {
                                    Sequel.menyimpan("tampjurnal", "'" + Beban_Kso_Radiologi_Ranap + "','HPP Persediaan Radiologi Rawat Inap','0','" + ttlkso + "'", "kredit=kredit+'" + (ttlkso) + "'", "kd_rek='" + Beban_Kso_Radiologi_Ranap + "'");
                                    Sequel.menyimpan("tampjurnal", "'" + Utang_Kso_Radiologi_Ranap + "','Persediaan BHP Radiologi Rawat Inap','" + ttlkso + "','0'", "debet=debet+'" + (ttlkso) + "'", "kd_rek='" + Utang_Kso_Radiologi_Ranap + "'");
                                }
                                if (ttljasasarana > 0) {
                                    Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Sarana_Radiologi_Ranap + "','Beban Jasa Sarana Radiologi Ranap','0','" + ttljasasarana + "'", "kredit=kredit+'" + (ttljasasarana) + "'", "kd_rek='" + Beban_Jasa_Sarana_Radiologi_Ranap + "'");
                                    Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Sarana_Radiologi_Ranap + "','Utang Jasa Sarana Radiologi Ranap','" + ttljasasarana + "','0'", "debet=debet+'" + (ttljasasarana) + "'", "kd_rek='" + Utang_Jasa_Sarana_Radiologi_Ranap + "'");
                                }
                                if (ttljmperujuk > 0) {
                                    Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Perujuk_Radiologi_Ranap + "','Beban Jasa Perujuk Radiologi Ranap','0','" + ttljmperujuk + "'", "kredit=kredit+'" + (ttljmperujuk) + "'", "kd_rek='" + Beban_Jasa_Perujuk_Radiologi_Ranap + "'");
                                    Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Perujuk_Radiologi_Ranap + "','Utang Jasa Perujuk Radiologi Ranap','" + ttljmperujuk + "','0'", "debet=debet+'" + (ttljmperujuk) + "'", "kd_rek='" + Utang_Jasa_Perujuk_Radiologi_Ranap + "'");
                                }
                                if (ttlmenejemen > 0) {
                                    Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Menejemen_Radiologi_Ranap + "','Beban Jasa Menejemen Radiologi Ranap','0','" + ttlmenejemen + "'", "kredit=kredit+'" + (ttlmenejemen) + "'", "kd_rek='" + Beban_Jasa_Menejemen_Radiologi_Ranap + "'");
                                    Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Menejemen_Radiologi_Ranap + "','Utang Jasa Menejemen Radiologi Ranap','" + ttlmenejemen + "','0'", "debet=debet+'" + (ttlmenejemen) + "'", "kd_rek='" + Utang_Jasa_Menejemen_Radiologi_Ranap + "'");
                                }
                                sukses = jur.simpanJurnal(tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString(), "U", "PEMBATALAN PEMERIKSAAN RADIOLOGI RAWAT INAP PASIEN " + tbDokter.getValueAt(tbDokter.getSelectedRow(), 1).toString() + " OLEH " + akses.getkode());
                            } else if (status.equals("Ralan")) {
                                Sequel.queryu("delete from tampjurnal");
                                if (ttlpendapatan > 0) {
                                    Sequel.menyimpan("tampjurnal", "'" + Suspen_Piutang_Radiologi_Ralan + "','Suspen Piutang Radiologi Ralan','0','" + ttlpendapatan + "'", "kredit=kredit+'" + (ttlpendapatan) + "'", "kd_rek='" + Suspen_Piutang_Radiologi_Ralan + "'");
                                    Sequel.menyimpan("tampjurnal", "'" + Radiologi_Ralan + "','Pendapatan Radiologi Rawat Inap','" + ttlpendapatan + "','0'", "debet=debet+'" + (ttlpendapatan) + "'", "kd_rek='" + Radiologi_Ralan + "'");
                                }
                                if (ttljmdokter > 0) {
                                    Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Medik_Dokter_Radiologi_Ralan + "','Beban Jasa Medik Dokter Radiologi Ralan','0','" + ttljmdokter + "'", "kredit=kredit+'" + (ttljmdokter) + "'", "kd_rek='" + Beban_Jasa_Medik_Dokter_Radiologi_Ralan + "'");
                                    Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Medik_Dokter_Radiologi_Ralan + "','Utang Jasa Medik Dokter Radiologi Ralan','" + ttljmdokter + "','0'", "debet=debet+'" + (ttljmdokter) + "'", "kd_rek='" + Utang_Jasa_Medik_Dokter_Radiologi_Ralan + "'");
                                }
                                if (ttljmpetugas > 0) {
                                    Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Medik_Petugas_Radiologi_Ralan + "','Beban Jasa Medik Petugas Radiologi Ralan','0','" + ttljmpetugas + "'", "kredit=kredit+'" + (ttljmpetugas) + "'", "kd_rek='" + Beban_Jasa_Medik_Petugas_Radiologi_Ralan + "'");
                                    Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Medik_Petugas_Radiologi_Ralan + "','Utang Jasa Medik Petugas Radiologi Ralan','" + ttljmpetugas + "','0'", "debet=debet+'" + (ttljmpetugas) + "'", "kd_rek='" + Utang_Jasa_Medik_Petugas_Radiologi_Ralan + "'");
                                }
                                if (ttlbhp > 0) {
                                    Sequel.menyimpan("tampjurnal", "'" + HPP_Persediaan_Radiologi_Rawat_Jalan + "','HPP Persediaan Radiologi Rawat Jalan','0','" + ttlbhp + "'", "kredit=kredit+'" + (ttlbhp) + "'", "kd_rek='" + HPP_Persediaan_Radiologi_Rawat_Jalan + "'");
                                    Sequel.menyimpan("tampjurnal", "'" + Persediaan_BHP_Radiologi_Rawat_Jalan + "','Persediaan BHP Radiologi Rawat Jalan','" + ttlbhp + "','0'", "debet=debet+'" + (ttlbhp) + "'", "kd_rek='" + Persediaan_BHP_Radiologi_Rawat_Jalan + "'");
                                }
                                if (ttlkso > 0) {
                                    Sequel.menyimpan("tampjurnal", "'" + Beban_Kso_Radiologi_Ralan + "','HPP Persediaan Radiologi Rawat Inap','0','" + ttlkso + "'", "kredit=kredit+'" + (ttlkso) + "'", "kd_rek='" + Beban_Kso_Radiologi_Ralan + "'");
                                    Sequel.menyimpan("tampjurnal", "'" + Utang_Kso_Radiologi_Ralan + "','Persediaan BHP Radiologi Rawat Inap','" + ttlkso + "','0'", "debet=debet+'" + (ttlkso) + "'", "kd_rek='" + Utang_Kso_Radiologi_Ralan + "'");
                                }
                                if (ttljasasarana > 0) {
                                    Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Sarana_Radiologi_Ralan + "','Beban Jasa Sarana Radiologi Ralan','0','" + ttljasasarana + "'", "kredit=kredit+'" + (ttljasasarana) + "'", "kd_rek='" + Beban_Jasa_Sarana_Radiologi_Ralan + "'");
                                    Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Sarana_Radiologi_Ralan + "','Utang Jasa Sarana Radiologi Ralan','" + ttljasasarana + "','0'", "debet=debet+'" + (ttljasasarana) + "'", "kd_rek='" + Utang_Jasa_Sarana_Radiologi_Ralan + "'");
                                }
                                if (ttljmperujuk > 0) {
                                    Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Perujuk_Radiologi_Ralan + "','Beban Jasa Perujuk Radiologi Ralan','0','" + ttljmperujuk + "'", "kredit=kredit+'" + (ttljmperujuk) + "'", "kd_rek='" + Beban_Jasa_Perujuk_Radiologi_Ralan + "'");
                                    Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Perujuk_Radiologi_Ralan + "','Utang Jasa Perujuk Radiologi Ralan','" + ttljmperujuk + "','0'", "debet=debet+'" + (ttljmperujuk) + "'", "kd_rek='" + Utang_Jasa_Perujuk_Radiologi_Ralan + "'");
                                }
                                if (ttlmenejemen > 0) {
                                    Sequel.menyimpan("tampjurnal", "'" + Beban_Jasa_Menejemen_Radiologi_Ralan + "','Beban Jasa Menejemen Radiologi Ralan','0','" + ttlmenejemen + "'", "kredit=kredit+'" + (ttlmenejemen) + "'", "kd_rek='" + Beban_Jasa_Menejemen_Radiologi_Ralan + "'");
                                    Sequel.menyimpan("tampjurnal", "'" + Utang_Jasa_Menejemen_Radiologi_Ralan + "','Utang Jasa Menejemen Radiologi Ralan','" + ttlmenejemen + "','0'", "debet=debet+'" + (ttlmenejemen) + "'", "kd_rek='" + Utang_Jasa_Menejemen_Radiologi_Ralan + "'");
                                }
                                sukses = jur.simpanJurnal(tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString(), "U", "PEMBATALAN PEMERIKSAAN RADIOLOGI RAWAT JALAN PASIEN " + tbDokter.getValueAt(tbDokter.getSelectedRow(), 1).toString() + " OLEH " + akses.getkode());
                            }
                        }

                        if (sukses == true) {
                            Sequel.Commit();
                            tampil();
                        } else {
                            JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat pemrosesan data, transaksi dibatalkan.\nPeriksa kembali data sebelum melanjutkan menyimpan..!!");
                            Sequel.RollBack();
                        }
                        Sequel.AutoComitTrue();
                    } catch (Exception e) {
                        System.out.println("Notifikasi : " + e);
                        JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih terlebih dulu data yang mau anda hapus...\n Klik data pada table untuk memilih data...!!!!");
                    }
                }
            }
        }
    }
}//GEN-LAST:event_BtnHapusActionPerformed

private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
        BtnHapusActionPerformed(null);
    } else {
        Valid.pindah(evt, TCari, BtnAll);
    }
}//GEN-LAST:event_BtnHapusKeyPressed

private void tbDokterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbDokterMouseClicked
    if (tabMode.getRowCount() != 0) {
        try {
            getData();
            isPhoto();
            panggilPhoto();
            tampilOrthanc();
        } catch (java.lang.NullPointerException e) {
        }
    }
}//GEN-LAST:event_tbDokterMouseClicked

private void tbDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbDokterKeyPressed
    if (tabMode.getRowCount() != 0) {
        if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }
}//GEN-LAST:event_tbDokterKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampil();
    }//GEN-LAST:event_formWindowOpened

    private void MnCetakNotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnCetakNotaActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
            TCari.requestFocus();
        } else if (tbDokter.getSelectedRow() <= -1) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan pilih data..!!");
        } else {
            if (Kd2.getText().equals("") || Petugas.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu...!!!!");
            } else {
                try {
                    ps4 = koneksi.prepareStatement(
                            "select periksa_radiologi.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.umur,petugas.nama,periksa_radiologi.tgl_periksa,periksa_radiologi.jam,"
                            + "periksa_radiologi.dokter_perujuk,periksa_radiologi.kd_dokter,periksa_radiologi.tgl_periksa,periksa_radiologi.jam,pasien.alamat,dokter.nm_dokter from periksa_radiologi inner join reg_periksa inner join pasien inner join petugas  inner join dokter "
                            + "on periksa_radiologi.no_rawat=reg_periksa.no_rawat and reg_periksa.no_rkm_medis=pasien.no_rkm_medis and periksa_radiologi.nip=petugas.nip and periksa_radiologi.kd_dokter=dokter.kd_dokter where "
                            + "periksa_radiologi.tgl_periksa=? and periksa_radiologi.jam=? and periksa_radiologi.no_rawat=? group by concat(periksa_radiologi.no_rawat,periksa_radiologi.tgl_periksa,periksa_radiologi.jam)");
                    try {
                        ps4.setString(1, tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString());
                        ps4.setString(2, tbDokter.getValueAt(tbDokter.getSelectedRow(), 4).toString());
                        ps4.setString(3, tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString());
                        rs = ps4.executeQuery();
                        while (rs.next()) {
                            Sequel.queryu("delete from temporary_radiologi");
                            koneksi.setAutoCommit(false);
                            ps2 = koneksi.prepareStatement(
                                    "select jns_perawatan_radiologi.kd_jenis_prw,jns_perawatan_radiologi.nm_perawatan,periksa_radiologi.biaya from periksa_radiologi inner join jns_perawatan_radiologi "
                                    + "on periksa_radiologi.kd_jenis_prw=jns_perawatan_radiologi.kd_jenis_prw where periksa_radiologi.no_rawat=? and periksa_radiologi.tgl_periksa=? "
                                    + "and periksa_radiologi.jam=?");
                            try {
                                ps2.setString(1, rs.getString("no_rawat"));
                                ps2.setString(2, rs.getString("tgl_periksa"));
                                ps2.setString(3, rs.getString("jam"));
                                rs2 = ps2.executeQuery();
                                ttl = 0;
                                i = 0;
                                while (rs2.next()) {
                                    item = rs2.getDouble("biaya");
                                    ttl = ttl + item;
                                    Sequel.menyimpan("temporary_radiologi", "'" + i + "','" + rs2.getString("nm_perawatan") + "','" + item + "','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','',''", "Transaksi Biaya Lab");
                                    i++;
                                }
                            } catch (Exception e) {
                                System.out.println("simrskhanza.DlgCariPeriksaRadiologi.MnCetakNotaActionPerformed() ps2 : " + e);
                            } finally {
                                if (rs2 != null) {
                                    rs2.close();
                                }
                                if (ps2 != null) {
                                    ps2.close();
                                }
                            }
                            i++;
                            Sequel.menyimpan("temporary_radiologi", "'" + i + "','Total Biaya Pemeriksaan Radiologi','" + ttl + "','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','',''", "Transaksi Biaya Lab");
                            Valid.panggilUrl("billing/LaporanBiayaRadiologi.php?norm=" + rs.getString("no_rkm_medis") + "&pasien=" + rs.getString("nm_pasien").replaceAll(" ", "_")
                                    + "&tanggal=" + rs.getString("tgl_periksa") + "&jam=" + rs.getString("jam") + "&pjlab=" + rs.getString("nm_dokter").replaceAll(" ", "_")
                                    + "&petugas=" + rs.getString("nama").replaceAll(" ", "_") + "&kasir=" + Sequel.cariIsi("select pegawai.nama from pegawai where pegawai.nik=?", akses.getkode())
                                    + "&usere=" + koneksiDB.USERHYBRIDWEB() + "&passwordte=" + koneksiDB.PASHYBRIDWEB());
                            koneksi.setAutoCommit(true);
                        }
                    } catch (Exception e) {
                        System.out.println("simrskhanza.DlgCariPeriksaRadiologi.MnCetakNotaActionPerformed() ps4 : " + e);
                    } finally {
                        if (rs != null) {
                            rs.close();
                        }
                        if (ps4 != null) {
                            ps4.close();
                        }
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_MnCetakNotaActionPerformed

    private void BtnPrint1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrint1ActionPerformed
        if (Kd2.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu...!!!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            pemeriksaan = "";
            try {
                ps2 = koneksi.prepareStatement(
                        "select jns_perawatan_radiologi.kd_jenis_prw,jns_perawatan_radiologi.nm_perawatan,periksa_radiologi.biaya,"
                        + "periksa_radiologi.kd_dokter,periksa_radiologi.nip,periksa_radiologi.proyeksi,periksa_radiologi.kV,periksa_radiologi.mAS,periksa_radiologi.FFD,"
                        + "periksa_radiologi.BSF,periksa_radiologi.inak,periksa_radiologi.jml_penyinaran,periksa_radiologi.dosis from periksa_radiologi inner join jns_perawatan_radiologi "
                        + "on periksa_radiologi.kd_jenis_prw=jns_perawatan_radiologi.kd_jenis_prw where periksa_radiologi.no_rawat=? and periksa_radiologi.tgl_periksa=? "
                        + "and periksa_radiologi.jam=?");
                try {
                    ps2.setString(1, NoRawatDicari.getText());
                    ps2.setString(2, TglDicari.getText());
                    ps2.setString(3, JamDicari.getText());
                    rs2 = ps2.executeQuery();
                    while (rs2.next()) {
                        pemeriksaan = rs2.getString("nm_perawatan") + ", " + pemeriksaan;
                        kdpenjab = rs2.getString("kd_dokter");
                        kdpetugas = rs2.getString("nip");
                    }
                } catch (Exception e) {
                    System.out.println("simrskhanza.DlgCariPeriksaRadiologi.BtnPrint1ActionPerformed() ps2 : " + e);
                } finally {
                    if (rs2 != null) {
                        rs2.close();
                    }
                    if (ps2 != null) {
                        ps2.close();
                    }
                }

            } catch (Exception e) {
                System.out.println("Notifikasi Pemeriksaan : " + e);
            }
            Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat=? ", NoRM, Kd2.getText());
            Sequel.cariIsi("select pasien.jk from pasien where pasien.no_rkm_medis=? ", Jk, NoRM.getText());
            Sequel.cariIsi("select pasien.umur from pasien where pasien.no_rkm_medis=?", Umur, NoRM.getText());
            Sequel.cariIsi("select concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat from pasien inner join kelurahan inner join kecamatan inner join kabupaten on pasien.kd_kel=kelurahan.kd_kel and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab where no_rkm_medis=? ", Alamat, NoRM.getText());

            kamar = Sequel.cariIsi("select ifnull(kamar_inap.kd_kamar,'') from kamar_inap where kamar_inap.no_rawat='" + Kd2.getText() + "' order by kamar_inap.tgl_masuk desc limit 1");
            if (!kamar.equals("")) {
                namakamar = kamar + ", " + Sequel.cariIsi("select bangsal.nm_bangsal from bangsal inner join kamar on bangsal.kd_bangsal=kamar.kd_bangsal "
                        + " where kamar.kd_kamar='" + kamar + "' ");
                kamar = "Kamar";
            } else if (kamar.equals("")) {
                kamar = "Poli";
                namakamar = Sequel.cariIsi("select poliklinik.nm_poli from poliklinik inner join reg_periksa on poliklinik.kd_poli=reg_periksa.kd_poli "
                        + "where reg_periksa.no_rawat='" + Kd2.getText() + "'");
            }
            Map<String, Object> param = new HashMap<>();
            param.put("noperiksa", Kd2.getText());
            param.put("norm", NoRM.getText());
            param.put("namapasien", Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=? ", NoRM.getText()));
            param.put("jkel", Jk.getText());
            param.put("umur", Umur.getText());
            param.put("lahir", Sequel.cariIsi("select DATE_FORMAT(pasien.tgl_lahir,'%d-%m-%Y') from pasien where pasien.no_rkm_medis=? ", NoRM.getText()));
            param.put("pengirim", tbDokter.getValueAt(tbDokter.getSelectedRow(), 5).toString());
            param.put("tanggal", Valid.SetTgl3(tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString()));
            param.put("penjab", tbDokter.getValueAt(tbDokter.getSelectedRow(), 6).toString());
            param.put("petugas", tbDokter.getValueAt(tbDokter.getSelectedRow(), 2).toString());
            param.put("alamat", Alamat.getText());
            param.put("kamar", kamar);
            param.put("namakamar", namakamar);
            param.put("pemeriksaan", pemeriksaan);
            param.put("jam", tbDokter.getValueAt(tbDokter.getSelectedRow(), 4).toString());
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("hasil", HasilPeriksa.getText());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", kdpenjab);
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbDokter.getValueAt(tbDokter.getSelectedRow(), 6).toString() + "\nID " + (finger.equals("") ? kdpenjab : finger) + "\n" + Valid.SetTgl3(tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString()));
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", kdpetugas);
            param.put("finger2", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbDokter.getValueAt(tbDokter.getSelectedRow(), 2).toString() + "\nID " + (finger.equals("") ? kdpetugas : finger) + "\n" + Valid.SetTgl3(tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString()));

            pilihan = (String) JOptionPane.showInputDialog(null, "Silahkan pilih hasil pemeriksaan..!", "Hasil Pemeriksaan", JOptionPane.QUESTION_MESSAGE, null, new Object[]{"Model 1", "Model 2", "Model 3", "PDF Model 1", "PDF Model 2", "PDF Model 3"}, "Model 1");
            switch (pilihan) {
                case "Model 1":
                    Valid.MyReport("rptPeriksaRadiologi.jasper", "report", "::[ Pemeriksaan Radiologi ]::", param);
                    break;
                case "Model 2":
                    Valid.MyReport("rptPeriksaRadiologi2.jasper", "report", "::[ Pemeriksaan Radiologi ]::", param);
                    break;
                case "Model 3":
                    Valid.MyReport("rptPeriksaRadiologi3.jasper", "report", "::[ Pemeriksaan Radiologi ]::", param);
                    break;
                case "PDF Model 1":
                    Valid.MyReportPDF("rptPeriksaRadiologi.jasper", "report", "::[ Pemeriksaan Radiologi ]::", param);
                    break;
                case "PDF Model 2":
                    Valid.MyReportPDF("rptPeriksaRadiologi2.jasper", "report", "::[ Pemeriksaan Radiologi ]::", param);
                    break;
                case "PDF Model 3":
                    Valid.MyReportPDF("rptPeriksaRadiologi3.jasper", "report", "::[ Pemeriksaan Radiologi ]::", param);
                    break;
            }

            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_BtnPrint1ActionPerformed

    private void BtnPrint1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrint1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPrintActionPerformed(null);
        } else {
            //  Valid.pindah(evt, BtnBatal,BtnCari);
        }
    }//GEN-LAST:event_BtnPrint1KeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (Kd2.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu...!!!!");
        } else {
            if (HasilPeriksa.getText().equals("")) {
                Sequel.queryu2("delete from hasil_radiologi where no_rawat=? and tgl_periksa=? and jam=?", 3, new String[]{
                    NoRawatDicari.getText(), TglDicari.getText(), JamDicari.getText()
                });
            } else {
                Sequel.queryu2("delete from hasil_radiologi where no_rawat=? and tgl_periksa=? and jam=?", 3, new String[]{
                    NoRawatDicari.getText(), TglDicari.getText(), JamDicari.getText()
                });
                Sequel.menyimpan("hasil_radiologi", "?,?,?,?", "Hasil Pemeriksaan", 4, new String[]{
                    NoRawatDicari.getText(), TglDicari.getText(), JamDicari.getText(), HasilPeriksa.getText()
                });
            }

            JOptionPane.showMessageDialog(null, "Proses update selesai...!!!!");
        }
    }//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        }
    }//GEN-LAST:event_BtnSimpanKeyPressed

    private void PenjabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PenjabKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_PenjabKeyPressed

    private void BtnSimpan4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpan4ActionPerformed
        if (tbDokter.getSelectedRow() > -1) {
            if (!tbDokter.getValueAt(tbDokter.getSelectedRow(), 1).toString().equals("")) {
                Sequel.queryu2("update periksa_radiologi set nip=?,dokter_perujuk=?,kd_dokter=? where no_rawat=? and tgl_periksa=? and jam=?", 6, new String[]{
                    KdPtgUbah.getText(), KodePerujuk.getText(), KodePj.getText(), tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString(),
                    tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString(), tbDokter.getValueAt(tbDokter.getSelectedRow(), 4).toString()
                });
                tampil();
                dokter.dispose();
                petugas.dispose();
                WindowGantiDokterParamedis.dispose();
            }
        }
    }//GEN-LAST:event_BtnSimpan4ActionPerformed

    private void BtnCloseIn4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCloseIn4ActionPerformed
        dokter.dispose();
        petugas.dispose();
        WindowGantiDokterParamedis.dispose();
    }//GEN-LAST:event_BtnCloseIn4ActionPerformed

    private void btnDokterPjActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDokterPjActionPerformed
        pilihan = "penjab";
        akses.setform("DlgCariPeriksaRadiologi");
        dokter.emptTeks();
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    }//GEN-LAST:event_btnDokterPjActionPerformed

    private void btnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDokterActionPerformed
        pilihan = "perujuk";
        akses.setform("DlgCariPeriksaRadiologi");
        dokter.emptTeks();
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    }//GEN-LAST:event_btnDokterActionPerformed

    private void KodePerujukKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodePerujukKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            NmPerujuk.setText(dokter.tampil3(KodePerujuk.getText()));
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            btnDokterActionPerformed(null);
        } else {
            Valid.pindah(evt, KdPtgUbah, BtnSimpan4);
        }
    }//GEN-LAST:event_KodePerujukKeyPressed

    private void KdPtgUbahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdPtgUbahKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            NmPtgUbah.setText(petugas.tampil3(KdPtgUbah.getText()));
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            btnPetugasActionPerformed(null);
        } else {
            Valid.pindah(evt, KodePj, KodePerujuk);
        }
    }//GEN-LAST:event_KdPtgUbahKeyPressed

    private void btnPetugas1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPetugas1ActionPerformed
        akses.setform("DlgCariPeriksaRadiologi");
        pilihan = "ubahpetugas";
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setAlwaysOnTop(false);
        petugas.setVisible(true);
    }//GEN-LAST:event_btnPetugas1ActionPerformed

    private void MnUbahDokterPetugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnUbahDokterPetugasActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
            TCari.requestFocus();
        } else if (tbDokter.getSelectedRow() <= -1) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan pilih data..!!");
        } else {
            if (Kd2.getText().equals("") || Petugas.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu...!!!!");
            } else {
                if (Sequel.cariRegistrasi(tbDokter.getValueAt(tbDokter.getSelectedRow(), 1).toString()) > 0) {
                    JOptionPane.showMessageDialog(rootPane, "Data billing sudah terverifikasi, data tidak boleh diubah.\nSilahkan hubungi bagian kasir/keuangan ..!!");
                    TCari.requestFocus();
                } else {
                    panggilMedis();
                }
            }
        }
    }//GEN-LAST:event_MnUbahDokterPetugasActionPerformed

    private void ppBerkasDigitalBtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppBerkasDigitalBtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
            TCari.requestFocus();
        } else if (tbDokter.getSelectedRow() <= -1) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan pilih data..!!");
        } else {
            if (Kd2.getText().equals("") || Petugas.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu...!!!!");
            } else {
                DlgBerkasRawat berkas = new DlgBerkasRawat(null, true);
                berkas.setJudul("::[ Berkas Digital Perawatan ]::", "berkasrawat/pages");
                try {
                    if (akses.gethapus_berkas_digital_perawatan() == true) {
                        berkas.loadURL("http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/" + "berkasrawat/login2.php?act=login&usere=" + koneksiDB.USERHYBRIDWEB() + "&passwordte=" + koneksiDB.PASHYBRIDWEB() + "&no_rawat=" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString());
                    } else {
                        berkas.loadURL("http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/" + "berkasrawat/login2nonhapus.php?act=login&usere=" + koneksiDB.USERHYBRIDWEB() + "&passwordte=" + koneksiDB.PASHYBRIDWEB() + "&no_rawat=" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString());
                    }
                } catch (Exception ex) {
                    System.out.println("Notifikasi : " + ex);
                }

                berkas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                berkas.setLocationRelativeTo(internalFrame1);
                berkas.setVisible(true);
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_ppBerkasDigitalBtnPrintActionPerformed

    private void ChkAccorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkAccorActionPerformed
        if (tbDokter.getSelectedRow() != -1) {
            isPhoto();
            panggilPhoto();
        } else {
            ChkAccor.setSelected(false);
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau ditampilkan...!!!!");
        }
    }//GEN-LAST:event_ChkAccorActionPerformed

    private void btnAmbilPhotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAmbilPhotoActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
            TCari.requestFocus();
        } else if (tbDokter.getSelectedRow() <= -1) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan pilih data..!!");
        } else {
            if (Kd2.getText().equals("") || Petugas.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu...!!!!");
            } else {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                Valid.panggilUrl("radiologi/login.php?act=login&usere=" + koneksiDB.USERHYBRIDWEB() + "&passwordte=" + koneksiDB.PASHYBRIDWEB() + "&no_rawat=" + NoRawatDicari.getText() + "&tanggal=" + TglDicari.getText() + "&jam=" + JamDicari.getText());
                this.setCursor(Cursor.getDefaultCursor());
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_btnAmbilPhotoActionPerformed

    /** Upload hasil download DICOM ke SIMRS web. Dipanggil sebagai callback dari OrthancDICOM viewer. */
    public void uploadHasilDicomKeWeb(int modelRow) {
        if (modelRow < 0) {
            JOptionPane.showMessageDialog(null, "Tidak ada baris pemeriksaan dipilih.");
            return;
        }
        Object v0 = tabMode.getValueAt(modelRow, 0);
        String noRawat = v0 == null ? "" : v0.toString().trim();
        if (noRawat.isEmpty() || noRawat.startsWith(">>")) {
            JOptionPane.showMessageDialog(null, "Pilih baris pemeriksaan utama, bukan baris detail/total.");
            return;
        }

        // Cari file matching prefix di ./gambarradiologi/
        java.io.File dir = new java.io.File("./gambarradiologi");
        if (!dir.exists() || !dir.isDirectory()) {
            JOptionPane.showMessageDialog(null,
                    "Folder ./gambarradiologi/ tidak ditemukan.\nDownload DICOM dulu via tombol 'Tampilkan DICOM'.");
            return;
        }
        final String prefix = buildNamaFilePrefix(modelRow);
        java.io.File[] candidates = dir.listFiles((d, name) -> {
            if (!name.startsWith(prefix)) return false;
            String low = name.toLowerCase();
            // Skip DCM (file DICOM raw, tidak bisa display di web sebagai img)
            return low.endsWith(".jpg") || low.endsWith(".jpeg")
                || low.endsWith(".png") || low.endsWith(".bmp");
        });
        if (candidates == null || candidates.length == 0) {
            JOptionPane.showMessageDialog(null,
                    "Tidak ada file hasil download untuk pasien ini.\n"
                  + "Prefix dicari: " + prefix + "*\n"
                  + "Download DICOM dulu (PNG/JPG/BMP) via tombol 'Tampilkan DICOM'.");
            return;
        }

        int reply = JOptionPane.showConfirmDialog(this,
                "Ditemukan " + candidates.length + " file hasil download di ./gambarradiologi/.\n"
              + "Auto-upload semua ke SIMRS web (form gambar_radiologi)?\n\n"
              + "Catatan: PNG/BMP akan auto-convert ke JPG (hanya JPG yang diterima form).",
                "Upload Hasil DICOM", JOptionPane.YES_NO_OPTION);
        if (reply != JOptionPane.YES_OPTION) return;

        final java.io.File[] files = candidates;
        final String tglPeriksa = String.valueOf(tabMode.getValueAt(modelRow, 3));
        final String jamPeriksa = String.valueOf(tabMode.getValueAt(modelRow, 4));
        final String noRawatFinal = noRawat;

        final javax.swing.ProgressMonitor monitor = new javax.swing.ProgressMonitor(
                this, "Upload ke SIMRS web...", "Login...", 0, files.length + 1);
        monitor.setMillisToDecideToPopup(0);
        monitor.setMillisToPopup(0);
        monitor.setProgress(0);

        javax.swing.SwingWorker<int[], String> worker = new javax.swing.SwingWorker<int[], String>() {
            @Override
            protected int[] doInBackground() {
                int success = 0, fail = 0, converted = 0;
                java.net.CookieManager cm = new java.net.CookieManager(null, java.net.CookiePolicy.ACCEPT_ALL);
                java.net.CookieHandler.setDefault(cm);

                String baseUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB();
                // 1. Login → set session cookie
                try {
                    String loginUrl = baseUrl + "/radiologi/login.php?act=login"
                            + "&usere=" + java.net.URLEncoder.encode(koneksiDB.USERHYBRIDWEB(), "UTF-8")
                            + "&passwordte=" + java.net.URLEncoder.encode(koneksiDB.PASHYBRIDWEB(), "UTF-8");
                    java.net.HttpURLConnection lc = (java.net.HttpURLConnection) new java.net.URL(loginUrl).openConnection();
                    lc.setRequestMethod("GET");
                    lc.setInstanceFollowRedirects(true);
                    lc.connect();
                    int code = lc.getResponseCode();
                    System.out.println("Login HTTP code: " + code);
                    lc.getInputStream().close();
                } catch (Exception e) {
                    System.out.println("Login gagal: " + e);
                    return new int[]{0, files.length, 0};
                }
                publish("Login OK, upload " + files.length + " file...");
                setProgress(1);

                // 2. Upload tiap file
                for (int i = 0; i < files.length; i++) {
                    if (isCancelled() || monitor.isCanceled()) break;
                    java.io.File src = files[i];
                    publish("(" + (i + 1) + "/" + files.length + ") " + src.getName());
                    java.io.File toUpload = src;
                    try {
                        String low = src.getName().toLowerCase();
                        if (!low.endsWith(".jpg") && !low.endsWith(".jpeg")) {
                            toUpload = convertToJpg(src);
                            converted++;
                        }
                        boolean ok = postUploadMultipart(baseUrl, noRawatFinal, tglPeriksa, jamPeriksa, toUpload);
                        if (ok) success++; else fail++;
                    } catch (Exception e) {
                        System.out.println("Upload gagal " + src.getName() + ": " + e);
                        fail++;
                    }
                    setProgress(i + 2);
                }
                return new int[]{success, fail, converted};
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                monitor.setNote(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                monitor.close();
                try {
                    int[] r = get();
                    String headline = isCancelled() || monitor.isCanceled()
                            ? "Upload DIBATALKAN"
                            : "Upload Selesai!";
                    JOptionPane.showMessageDialog(null, headline + "\n\n"
                        + "Berhasil: " + r[0] + " file\n"
                        + "Gagal:    " + r[1] + " file\n"
                        + "Auto-converted ke JPG: " + r[2] + " file");
                    panggilPhoto(); // refresh tabel daftar gambar
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,
                            "Upload error: " + ex.getMessage(),
                            "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    /** POST multipart ke endpoint upload PHP (radiologi/index.php?act=List).
     *  Field: no_rawat, tanggal, jam, action, BtnSimpan, gambar=<file>. */
    private boolean postUploadMultipart(String baseUrl, String noRawat, String tanggal, String jam, java.io.File jpg) throws Exception {
        String url = baseUrl + "/radiologi/index.php?act=List"
                + "&no_rawat=" + java.net.URLEncoder.encode(noRawat, "UTF-8")
                + "&tanggal="  + java.net.URLEncoder.encode(tanggal, "UTF-8")
                + "&jam="      + java.net.URLEncoder.encode(jam, "UTF-8");

        String boundary = "RscbBoundary" + System.currentTimeMillis();
        String LF = "\r\n";
        java.io.ByteArrayOutputStream body = new java.io.ByteArrayOutputStream();
        java.io.PrintWriter w = new java.io.PrintWriter(new java.io.OutputStreamWriter(body, "UTF-8"), true);
        addMultipartField(w, boundary, "no_rawat", noRawat, LF);
        addMultipartField(w, boundary, "tanggal", tanggal, LF);
        addMultipartField(w, boundary, "jam", jam, LF);
        addMultipartField(w, boundary, "action", "List", LF);
        addMultipartField(w, boundary, "BtnSimpan", "Simpan", LF);

        // file part
        w.append("--").append(boundary).append(LF);
        w.append("Content-Disposition: form-data; name=\"gambar\"; filename=\"")
                .append(jpg.getName()).append("\"").append(LF);
        w.append("Content-Type: image/jpeg").append(LF).append(LF);
        w.flush();
        body.write(java.nio.file.Files.readAllBytes(jpg.toPath()));
        w.append(LF).append("--").append(boundary).append("--").append(LF);
        w.close();

        java.net.HttpURLConnection up = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
        up.setRequestMethod("POST");
        up.setDoOutput(true);
        up.setUseCaches(false);
        up.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        byte[] bytes = body.toByteArray();
        up.setFixedLengthStreamingMode(bytes.length);
        try (java.io.OutputStream os = up.getOutputStream()) {
            os.write(bytes);
        }
        int code = up.getResponseCode();
        System.out.println("Upload " + jpg.getName() + " → HTTP " + code);
        try (java.io.InputStream is = code >= 400 ? up.getErrorStream() : up.getInputStream()) {
            if (is != null) {
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is));
                String line; int n = 0;
                while ((line = br.readLine()) != null && n++ < 5) System.out.println("  " + line);
            }
        }
        up.disconnect();
        return code == 200;
    }

    /** Convert PNG/BMP → JPG dgn ImageIO, simpan di temp dir. White background utk PNG transparan. */
    private java.io.File convertToJpg(java.io.File src) throws Exception {
        java.awt.image.BufferedImage in = javax.imageio.ImageIO.read(src);
        if (in == null) throw new Exception("Tidak bisa baca image: " + src.getName());
        // Flatten ke RGB dgn background putih
        java.awt.image.BufferedImage rgb = new java.awt.image.BufferedImage(
                in.getWidth(), in.getHeight(), java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g = rgb.createGraphics();
        g.setColor(java.awt.Color.WHITE);
        g.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
        g.drawImage(in, 0, 0, null);
        g.dispose();
        String base = src.getName();
        int dot = base.lastIndexOf('.');
        if (dot > 0) base = base.substring(0, dot);
        java.io.File jpg = new java.io.File(src.getParent(), base + ".jpg");
        javax.imageio.ImageIO.write(rgb, "jpg", jpg);
        return jpg;
    }

    private void BtnRefreshPhotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnRefreshPhotoActionPerformed
        panggilPhoto();
    }//GEN-LAST:event_BtnRefreshPhotoActionPerformed

    private void btnAmbilPhoto1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAmbilPhoto1ActionPerformed
        akses.setform("DlgCariPeriksaRadiologi");
        templatehasil.emptTeks();
        templatehasil.isCek();
        templatehasil.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        templatehasil.setLocationRelativeTo(internalFrame1);
        templatehasil.setVisible(true);
    }//GEN-LAST:event_btnAmbilPhoto1ActionPerformed

    private void btnDicomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDicomActionPerformed
        if (tabModeDicom.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
            TCari.requestFocus();
        } else {
            if (tbListDicom.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                int dokterRow = tbDokter.getSelectedRow();
                int dokterModelRow = dokterRow >= 0 ? tbDokter.convertRowIndexToModel(dokterRow) : -1;
                int dicomRow = tbListDicom.getSelectedRow();
                String series = tbListDicom.getValueAt(dicomRow, 2).toString();

                // Build filename prefix: namaPasien_noRM_tglPeriksa_noRawat (lihat buildNamaFilePrefix)
                String namaFileBase = buildNamaFilePrefix(dokterModelRow);

                OrthancDICOM orthan = new OrthancDICOM(null, false);
                String judulDokter = dokterRow >= 0 ? tbDokter.getValueAt(dokterRow, 1).toString() : "(unknown)";
                orthan.setJudul("::[ DICOM Orthanc Pasien " + judulDokter + ", Series " + series + " ]::",
                        namaFileBase, series);
                // Tombol "Upload ke SIMRS" muncul di viewer DICOM, callback ke method ini.
                final int finalModelRow = dokterModelRow;
                orthan.setUploadCallback(() -> uploadHasilDicomKeWeb(finalModelRow));
                try {
                    System.out.println("URL : " + koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/web-viewer/app/viewer.html?series=" + series);
                    orthan.loadURL(koneksiDB.URLORTHANC() + ":" + koneksiDB.PORTORTHANC() + "/web-viewer/app/viewer.html?series=" + series);
                } catch (Exception ex) {
                    System.out.println("Notifikasi : " + ex);
                }
                orthan.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                orthan.setLocationRelativeTo(internalFrame1);
                orthan.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, Silahkan pilih data..!!");
            }
        }
    }//GEN-LAST:event_btnDicomActionPerformed

    /** Build prefix nama file dari row pasien: namaPasien_noRM_tglPeriksa_noRawat (sudah ter-sanitize). */
    private String buildNamaFilePrefix(int modelRow) {
        if (modelRow < 0) return "DICOM";
        try {
            String pasienInfo = String.valueOf(tabMode.getValueAt(modelRow, 1));
            String tglPeriksa = String.valueOf(tabMode.getValueAt(modelRow, 3));
            String noRawat    = String.valueOf(tabMode.getValueAt(modelRow, 0));
            int sp1 = pasienInfo.indexOf(' ');
            String noRM = sp1 > 0 ? pasienInfo.substring(0, sp1).trim() : "";
            String rest = sp1 > 0 ? pasienInfo.substring(sp1 + 1) : pasienInfo;
            int parenIdx = rest.indexOf(" (");
            String namaPasien = parenIdx > 0 ? rest.substring(0, parenIdx).trim() : rest.trim();
            return sanitizeNamaFile(namaPasien)
                    + "_" + sanitizeNamaFile(noRM)
                    + "_" + sanitizeNamaFile(tglPeriksa)
                    + "_" + sanitizeNamaFile(noRawat);
        } catch (Exception e) {
            return "DICOM";
        }
    }

    /** Sanitize string jadi aman dipakai sebagai nama file. Hapus char illegal di Linux/Windows,
     *  ganti spasi/slash/whitespace ke underscore, trim panjang max 60 char. */
    private static String sanitizeNamaFile(String s) {
        if (s == null) return "";
        String t = s.trim();
        // Hapus char yang invalid di filesystem Windows/Linux
        t = t.replaceAll("[\\\\/:*?\"<>|]", "");
        // Ganti whitespace berturut-turut jadi 1 underscore
        t = t.replaceAll("\\s+", "_");
        // Hapus titik di awal (hidden file Linux)
        while (t.startsWith(".")) t = t.substring(1);
        // Truncate biar tidak overflow path length
        if (t.length() > 60) t = t.substring(0, 60);
        return t;
    }

    private void TabDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabDataMouseClicked
        tampilOrthanc();
    }//GEN-LAST:event_TabDataMouseClicked

    private void tbListDicomMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbListDicomMouseClicked
        if (tabModeDicom.getRowCount() != 0) {
            if (evt.getClickCount() == 2) {
                btnDicomActionPerformed(null);
            }
        }
    }//GEN-LAST:event_tbListDicomMouseClicked

    private void tbListDicomKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbListDicomKeyPressed
        if (tabModeDicom.getRowCount() != 0) {
            if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                btnDicomActionPerformed(null);
            }
        }
    }//GEN-LAST:event_tbListDicomKeyPressed

    private void ppRiwayatBtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppRiwayatBtnPrintActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
            TCari.requestFocus();
        } else if (tbDokter.getSelectedRow() <= -1) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan pilih data..!!");
        } else {
            if (Kd2.getText().equals("") || Petugas.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu...!!!!");
            } else {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMRiwayatPerawatan resume = new RMRiwayatPerawatan(null, true);
                pilihan = Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat=?", Kd2.getText());
                resume.setNoRm(pilihan, tbDokter.getValueAt(tbDokter.getSelectedRow(), 1).toString().replaceAll(pilihan + " ", ""));
                resume.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                resume.setLocationRelativeTo(internalFrame1);
                resume.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            }
        }
    }//GEN-LAST:event_ppRiwayatBtnPrintActionPerformed

    private void UploadPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UploadPDFActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
            TCari.requestFocus();
        } else if (tbDokter.getSelectedRow() <= -1) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan pilih data..!!");
        } else {
            if (Kd2.getText().equals("") || Petugas.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu...!!!!");
            } else {
                int selectedRow = tbDokter.getSelectedRow();
                String noRawat = tbDokter.getValueAt(selectedRow, 0).toString();
                String namaPasien = tbDokter.getValueAt(selectedRow, 1).toString();
                
                String fileNameWithoutExt = "Radiologi_" + noRawat.replaceAll("/", "") + "_" + namaPasien.replaceAll("[^a-zA-Z0-9]", "");
                
                try {
                    CreatePDF(fileNameWithoutExt);
                    
                    File pdfFile = new File("tmpPDF/" + fileNameWithoutExt + ".pdf");
                    if (!pdfFile.exists() || pdfFile.length() == 0) {
                        JOptionPane.showMessageDialog(null, "Gagal membuat PDF. File tidak ditemukan atau kosong.");
                        return;
                    }
                    
                    System.out.println("PDF created successfully. Size: " + pdfFile.length() + " bytes");
                    
                    UploadPDF(fileNameWithoutExt, "berkasrawat/pages/upload/", noRawat);
                    
                    if (pdfFile.exists()) {
                        pdfFile.delete();
                        System.out.println("Temporary PDF deleted: " + fileNameWithoutExt + ".pdf");
                    }
                    
                    ppBerkasDigitalBtnPrintActionPerformed(evt);
                    
                } catch (Exception e) {
                    System.out.println("Error in UploadPDFActionPerformed: " + e);
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan: " + e.getMessage());
                }
            }
        }
    }//GEN-LAST:event_UploadPDFActionPerformed

    private void ppDicomImageBtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppDicomImageBtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
            TCari.requestFocus();
        } else if (tbDokter.getSelectedRow() <= -1) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan pilih data..!!");
        } else {
            if (Kd2.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data yang mau ditampilkan...!!!!");
            } else {
                try {
                    String noRekammedis = tbDokter.getValueAt(tbDokter.getSelectedRow(), 1).toString().split(" ")[0];
                    dicomViewer.setPasien(tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString(), tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString(), tbDokter.getValueAt(tbDokter.getSelectedRow(), 4).toString(), true);
                    dicomViewer.tampilDicomServer(tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString().replaceAll("-", ""), tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString().replaceAll("-", ""), noRekammedis, tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString());
                    dicomViewer.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                    dicomViewer.setLocationRelativeTo(internalFrame1);
                    dicomViewer.setVisible(true);
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_ppDicomImageBtnPrintActionPerformed

    private void BtnClosePhraseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnClosePhraseActionPerformed
        Phrase.setText("");
        WindowPhrase.dispose();
    }//GEN-LAST:event_BtnClosePhraseActionPerformed

    private void BtnSimpanTandaTanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanTandaTanganActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        if (Kd2.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu...!!!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            pemeriksaan = "";
            try {
                ps2 = koneksi.prepareStatement(
                        "select jns_perawatan_radiologi.kd_jenis_prw,jns_perawatan_radiologi.nm_perawatan,periksa_radiologi.biaya,"
                        + "periksa_radiologi.kd_dokter,periksa_radiologi.nip,periksa_radiologi.proyeksi,periksa_radiologi.kV,periksa_radiologi.mAS,periksa_radiologi.FFD,"
                        + "periksa_radiologi.BSF,periksa_radiologi.inak,periksa_radiologi.jml_penyinaran,periksa_radiologi.dosis from periksa_radiologi inner join jns_perawatan_radiologi "
                        + "on periksa_radiologi.kd_jenis_prw=jns_perawatan_radiologi.kd_jenis_prw where periksa_radiologi.no_rawat=? and periksa_radiologi.tgl_periksa=? "
                        + "and periksa_radiologi.jam=?");
                try {
                    ps2.setString(1, NoRawatDicari.getText());
                    ps2.setString(2, TglDicari.getText());
                    ps2.setString(3, JamDicari.getText());
                    rs2 = ps2.executeQuery();
                    while (rs2.next()) {
                        pemeriksaan = rs2.getString("nm_perawatan") + ", " + pemeriksaan;
                        kdpenjab = rs2.getString("kd_dokter");
                        kdpetugas = rs2.getString("nip");
                    }
                } catch (Exception e) {
                    System.out.println("simrskhanza.DlgCariPeriksaRadiologi.BtnPrint1ActionPerformed() ps2 : " + e);
                } finally {
                    if (rs2 != null) {
                        rs2.close();
                    }
                    if (ps2 != null) {
                        ps2.close();
                    }
                }

            } catch (Exception e) {
                System.out.println("Notifikasi Pemeriksaan : " + e);
            }
            Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat=? ", NoRM, Kd2.getText());
            Sequel.cariIsi("select pasien.jk from pasien where pasien.no_rkm_medis=? ", Jk, NoRM.getText());
            Sequel.cariIsi("select pasien.umur from pasien where pasien.no_rkm_medis=?", Umur, NoRM.getText());
            Sequel.cariIsi("select concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat from pasien inner join kelurahan inner join kecamatan inner join kabupaten on pasien.kd_kel=kelurahan.kd_kel and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab where no_rkm_medis=? ", Alamat, NoRM.getText());

            kamar = Sequel.cariIsi("select ifnull(kamar_inap.kd_kamar,'') from kamar_inap where kamar_inap.no_rawat='" + Kd2.getText() + "' order by kamar_inap.tgl_masuk desc limit 1");
            if (!kamar.equals("")) {
                namakamar = kamar + ", " + Sequel.cariIsi("select bangsal.nm_bangsal from bangsal inner join kamar on bangsal.kd_bangsal=kamar.kd_bangsal "
                        + " where kamar.kd_kamar='" + kamar + "' ");
                kamar = "Kamar";
            } else if (kamar.equals("")) {
                kamar = "Poli";
                namakamar = Sequel.cariIsi("select poliklinik.nm_poli from poliklinik inner join reg_periksa on poliklinik.kd_poli=reg_periksa.kd_poli "
                        + "where reg_periksa.no_rawat='" + Kd2.getText() + "'");
            }
            Map<String, Object> param = new HashMap<>();
            param.put("noperiksa", Kd2.getText());
            param.put("norm", NoRM.getText());
            param.put("namapasien", Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=? ", NoRM.getText()));
            param.put("jkel", Jk.getText());
            param.put("umur", Umur.getText());
            param.put("lahir", Sequel.cariIsi("select DATE_FORMAT(pasien.tgl_lahir,'%d-%m-%Y') from pasien where pasien.no_rkm_medis=? ", NoRM.getText()));
            param.put("pengirim", tbDokter.getValueAt(tbDokter.getSelectedRow(), 5).toString());
            param.put("tanggal", Valid.SetTgl3(tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString()));
            param.put("penjab", tbDokter.getValueAt(tbDokter.getSelectedRow(), 6).toString());
            param.put("petugas", tbDokter.getValueAt(tbDokter.getSelectedRow(), 2).toString());
            param.put("alamat", Alamat.getText());
            param.put("kamar", kamar);
            param.put("namakamar", namakamar);
            param.put("pemeriksaan", pemeriksaan);
            param.put("jam", tbDokter.getValueAt(tbDokter.getSelectedRow(), 4).toString());
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("hasil", HasilPeriksa.getText());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            param.put("logo2", Sequel.cariGambar("select setting.logo2 from setting"));
            param.put("logobsre", Sequel.cariGambar("select gambar.bsre from gambar"));
            param.put("finger", "#");
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", kdpenjab);
            //    param.put("finger","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+tbDokter.getValueAt(tbDokter.getSelectedRow(),6).toString()+"\nID "+(finger.equals("")?kdpenjab:finger)+"\n"+Valid.SetTgl3(tbDokter.getValueAt(tbDokter.getSelectedRow(),3).toString()));  
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", kdpetugas);
            param.put("finger2", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbDokter.getValueAt(tbDokter.getSelectedRow(), 2).toString() + "\nID " + (finger.equals("") ? kdpetugas : finger) + "\n" + Valid.SetTgl3(tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString()));
            Valid.MyReportPDF2("rptPeriksaRadiologiTte.jasper", "report", "::[ Pemeriksaan Radiologi ]::", param);
            this.setCursor(Cursor.getDefaultCursor());
        }

        // GUNAKAN FILE PDF YANG BENARTte.pdf
        File f = new File("./report/rptPeriksaRadiologiTte.pdf");

        // ========== ENHANCED VALIDASI NO_SEP DAN TANGGAL ==========
        String no_sep = "";
        String tanggal = "";
        String tanggal_registrasi = "";
        boolean lanjutkanProses = true;
        boolean usedRegistrationDate = false;
        boolean usedNoRawatAsFallback = false;
        String norawat = tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString();

        try {
            // Query gabungan untuk mendapatkan data SEP dan registrasi sekaligus
            String sqlQuery = "SELECT bs.no_sep, bs.tglsep, rp.tgl_registrasi "
                    + "FROM reg_periksa rp "
                    + "LEFT JOIN bridging_sep bs ON bs.no_rawat = rp.no_rawat "
                    + "WHERE rp.no_rawat = ?";

            try ( PreparedStatement psSep = koneksi.prepareStatement(sqlQuery)) {
                psSep.setString(1, norawat);
                try ( ResultSet rsSep = psSep.executeQuery()) {
                    if (rsSep.next()) {
                        no_sep = rsSep.getString("no_sep");
                        tanggal = rsSep.getString("tglsep");
                        tanggal_registrasi = rsSep.getString("tgl_registrasi");

                        System.out.println("Data ditemukan - SEP: " + no_sep + ", Tanggal SEP: " + tanggal + ", Tanggal Registrasi: " + tanggal_registrasi);
                    } else {
                        System.out.println("Warning: No registration data found for no_rawat: " + norawat);
                    }
                }
            }

            // Cek jika no_sep null atau kosong
            if (no_sep == null || no_sep.trim().isEmpty()) {
                System.out.println("Warning: No SEP found for no_rawat: " + norawat);

                // Tampilkan dialog konfirmasi ke user dengan informasi tanggal
                String tanggalInfo = "";
                if (tanggal_registrasi != null && !tanggal_registrasi.trim().isEmpty()) {
                    tanggalInfo = "\n• Tanggal registrasi tersedia: " + tanggal_registrasi;
                } else {
                    tanggalInfo = "\n• Akan menggunakan tanggal sistem";
                }

                int pilihan = JOptionPane.showConfirmDialog(
                        null,
                        "Nomor SEP tidak ditemukan untuk No. Rawat: " + norawat + "\n\n"
                        + "Apakah Anda ingin melanjutkan proses TTE?\n\n"
                        + "Jika Ya, sistem akan:\n"
                        + "• Menggunakan No. Rawat sebagai pengganti No. SEP"
                        + tanggalInfo,
                        "Konfirmasi - SEP Tidak Ditemukan",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (pilihan == JOptionPane.YES_OPTION) {
                    // User memilih lanjutkan, gunakan no_rawat sebagai pengganti dan hilangkan slash
                    no_sep = norawat.trim().replaceAll("/", "");
                    usedNoRawatAsFallback = true;

                    // Tentukan tanggal yang akan digunakan
                    if (tanggal_registrasi != null && !tanggal_registrasi.trim().isEmpty()) {
                        tanggal = tanggal_registrasi;
                        usedRegistrationDate = true;
                        System.out.println("Menggunakan tanggal registrasi: " + tanggal);
                    } else {
                        tanggal = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        System.out.println("Menggunakan tanggal sistem: " + tanggal);
                    }

                    String dateSource = usedRegistrationDate ? "tanggal registrasi" : "tanggal sistem";
                    JOptionPane.showMessageDialog(
                            null,
                            "Proses akan dilanjutkan dengan:\n"
                            + "• No. SEP pengganti: " + no_sep + "\n"
                            + "• Tanggal: " + tanggal + " (" + dateSource + ")",
                            "Informasi",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                } else {
                    // User memilih batal
                    lanjutkanProses = false;
                    JOptionPane.showMessageDialog(
                            null,
                            "Proses TTE dibatalkan oleh user.",
                            "Proses Dibatalkan",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            } else {
                System.out.println("No SEP ditemukan: " + no_sep + ", Tanggal: " + tanggal);

                // Jika ada SEP tapi tanggal kosong, gunakan tanggal registrasi sebagai fallback
                if (tanggal == null || tanggal.trim().isEmpty()) {
                    if (tanggal_registrasi != null && !tanggal_registrasi.trim().isEmpty()) {
                        tanggal = tanggal_registrasi;
                        usedRegistrationDate = true;
                        System.out.println("Tanggal SEP kosong, menggunakan tanggal registrasi: " + tanggal);
                    } else {
                        tanggal = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        System.out.println("Tanggal SEP dan registrasi kosong, menggunakan tanggal sistem: " + tanggal);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error saat mengambil no_sep: " + e);

            // Jika ada error, coba ambil tanggal registrasi sebagai fallback
            try {
                tanggal_registrasi = Sequel.cariIsi("select tgl_registrasi from reg_periksa where no_rawat = ?", norawat);
            } catch (Exception ex) {
                System.out.println("Error mengambil tanggal registrasi: " + ex);
            }

            // Dialog error dengan informasi fallback
            String dateInfo = "";
            if (tanggal_registrasi != null && !tanggal_registrasi.trim().isEmpty()) {
                dateInfo = "\n• Tanggal registrasi tersedia sebagai fallback: " + tanggal_registrasi;
            } else {
                dateInfo = "\n• Akan menggunakan tanggal sistem sebagai fallback";
            }

            int pilihan = JOptionPane.showConfirmDialog(
                    null,
                    "Terjadi error saat mengambil No. SEP: " + e.getMessage() + "\n\n"
                    + "Apakah Anda ingin melanjutkan proses TTE?\n\n"
                    + "Jika Ya, sistem akan:\n"
                    + "• Menggunakan No. Rawat sebagai pengganti No. SEP"
                    + dateInfo,
                    "Error - Gagal Mengambil SEP",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE
            );

            if (pilihan == JOptionPane.YES_OPTION) {
                no_sep = norawat.trim().replaceAll("/", "");
                usedNoRawatAsFallback = true;

                // Tentukan tanggal fallback
                if (tanggal_registrasi != null && !tanggal_registrasi.trim().isEmpty()) {
                    tanggal = tanggal_registrasi;
                    usedRegistrationDate = true;
                } else {
                    tanggal = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }
            } else {
                lanjutkanProses = false;
            }
        }

        // ========== LANJUTKAN PROSES JIKA USER SETUJU ==========
        if (lanjutkanProses) {
            // Solusi 1: SSL Context yang lebih lengkap dengan trust all certificates
            try {
                // Buat SSL context yang menerima semua certificate
                SSLContext sslContext = SSLContexts.custom()
                        .loadTrustMaterial(null, new TrustStrategy() {
                            @Override
                            public boolean isTrusted(X509Certificate[] chain, String authType)
                                    throws CertificateException {
                                return true; // Trust semua certificate
                            }
                        })
                        .build();

                // Buat hostname verifier yang menerima semua hostname
                HostnameVerifier hostnameVerifier = new NoopHostnameVerifier();

                // Konfigurasi SSL Connection Socket Factory
                SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                        sslContext,
                        hostnameVerifier
                );

                // Konfigurasi HTTP Client dengan SSL yang sudah dikustomisasi
                CloseableHttpClient httpClient = HttpClients.custom()
                        .setConnectionManager(
                                PoolingHttpClientConnectionManagerBuilder.create()
                                        .setSSLSocketFactory(sslSocketFactory)
                                        .build()
                        )
                        .setDefaultRequestConfig(
                                RequestConfig.custom()
                                        .setConnectTimeout(Timeout.ofSeconds(30))
                                        .setResponseTimeout(Timeout.ofSeconds(30))
                                        .build()
                        )
                        .build();

                // Setup HTTP POST request
                HttpPost post = new HttpPost(koneksiDB.URLAKSESFILEESIGN());
                post.setHeader("Content-Type", "application/json");
                post.addHeader("username", koneksiDB.USERNAMEAPIESIGN());
                post.addHeader("password", koneksiDB.PASSAPIESIGN());
                post.addHeader("url", koneksiDB.URLAPIESIGN());

                // Read file content
                byte[] fileContent = Files.readAllBytes(f.toPath());

                // Create JSON payload
                String json = "{"
                        + "\"file\":\"" + Base64.getEncoder().encodeToString(fileContent) + "\","
                        + "\"nik\":\"" + Sequel.cariIsi("select pegawai.no_ktp from pegawai where pegawai.nik=?", akses.getkode()) + "\","
                        + "\"passphrase\":\"" + Phrase.getText() + "\","
                        + "\"tampilan\":\"visible\","
                        + "\"image\":\"false\","
                        + "\"linkQR\":\"Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + " dan ditandatangani secara elektronik oleh " + Sequel.cariIsi("select pegawai.nama from pegawai where pegawai.nik=?", akses.getkode()) + " ID " + akses.getkode() + " Tanggal " + tanggal + "\","
                        + "\"width\":\"55\","
                        + "\"height\":\"55\","
                        + "\"tag_koordinat\":\"#\""
                        + "}";

                System.out.println("URL Akses file :" + koneksiDB.URLAKSESFILEESIGN());
                System.out.println("JSON Dikirim :" + json);
                System.out.println("No SEP yang digunakan: " + no_sep);
                System.out.println("Tanggal yang digunakan: " + tanggal + (usedRegistrationDate ? " (dari registrasi)" : usedNoRawatAsFallback ? " (sistem/fallback)" : " (dari SEP)"));

                //No.Rawat","Pasien","Petugas","Tgl.Periksa","Jam Periksa","Dokter Perujuk","Penanggung Jawab
                // Set request entity
                post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

                // Execute request
                try ( CloseableHttpResponse response = httpClient.execute(post)) {
                    System.out.println("Response Status : " + response.getCode());
                    json = EntityUtils.toString(response.getEntity());
                    root = mapper.readTree(json);
                    String fileName = "LabPk"
                            + tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString()
                                    .replaceAll("[^a-zA-Z0-9]", "") + "_"
                            + tbDokter.getValueAt(tbDokter.getSelectedRow(), 1).toString()
                                    .replaceAll("[^a-zA-Z0-9]", "") + "_"
                            + tbDokter.getValueAt(tbDokter.getSelectedRow(), 2).toString()
                                    .replaceAll("[^a-zA-Z0-9]", "") + ".pdf";
                    String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                    if (response.getCode() == 200) {
                        try ( FileOutputStream fos = new FileOutputStream(new File(fileName))) {
                            byte[] fileBytes = Base64.getDecoder().decode(root.path("response").asText());
                            fos.write(fileBytes);

                            // Tampilkan notifikasi SEBELUM WindowPhrase ditutup
                            JOptionPane.showMessageDialog(null, "Proses tanda tangan berhasil...");
                            WindowPhrase.dispose();

                            String kode = Sequel.cariIsi("select kode from master_berkas_digital where nama like 'resume'");
                            //                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            //                            String tanggalSekarang = sdf.format(new Date());
                            String tanggalSekarang = tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString() + " " + tbDokter.getValueAt(tbDokter.getSelectedRow(), 4).toString();

                            // Upload file dan ambil URL - dengan tanggal yang sudah ditentukan
                            UploadPDFTte uploader = new UploadPDFTte(fileName, no_sep, tanggal, tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString(), kode, tanggalSekarang, "INA-CBG");
                            boolean uploadResult = uploader.upload();

                            // Ambil URL dari uploader
                            String lokasiFile = "";
                            if (uploadResult && uploader.isUploadSuccess()) {
                                lokasiFile = uploader.getUploadedUrl();
                                System.out.println("File berhasil diupload ke: " + lokasiFile);
                            } else {
                                lokasiFile = ""; // fallback ke lokasi default jika upload gagal
                                System.out.println("Upload gagal, menggunakan lokasi default: " + lokasiFile);
                            }

                            // Enhanced status description
                            String statusKeterangan = buildStatusDescription(usedNoRawatAsFallback, usedRegistrationDate, "Sukses");
                            String keteranganText = buildDetailedDescription(no_sep, tanggal, usedRegistrationDate, usedNoRawatAsFallback, uploadResult, "TTE: Berhasil");

                            String sql = "INSERT INTO status_tte (no_rawat, nik, tanggal, nama_berkas, status_code, lokasi_file, status, keterangan) VALUES (?,?,?,?,?,?,?,?)";
                            try ( PreparedStatement pst = koneksi.prepareStatement(sql)) {
                                pst.setString(1, tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString().trim());
                                pst.setString(2, Sequel.cariIsi("select pegawai.no_ktp from pegawai where pegawai.nik=?", akses.getkode()));
                                pst.setString(3, now);
                                pst.setString(4, fileName);
                                pst.setString(5, response.getCode() + "");
                                pst.setString(6, lokasiFile);
                                pst.setString(7, statusKeterangan);
                                pst.setString(8, keteranganText);
                                pst.executeUpdate();
                                System.out.println("Status TTE berhasil disimpan dengan lokasi file: " + lokasiFile);
                            } catch (SQLException ex) {
                                System.out.println("Gagal menyimpan status TTE: " + ex);
                            }

                            //    Desktop.getDesktop().browse(new File("ResumeRI" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString().trim().replaceAll("/", "") + "_" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 1).toString().trim().replaceAll(" ", "") + "_" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 2).toString().trim().replaceAll(" ", "") + ".pdf").toURI());
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, "Gagal mengkonversi base64 ke file...");
                            System.out.println("Notif : " + e);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Code : " + root.path("metadata").path("code").asText() + " Pesan : " + root.path("metadata").path("message").asText());

                        // Enhanced status for failure
                        String statusKeterangan = buildStatusDescription(usedNoRawatAsFallback, usedRegistrationDate, "Gagal");
                        String keteranganText = buildDetailedDescription(no_sep, tanggal, usedRegistrationDate, usedNoRawatAsFallback, false, "TTE: Gagal - " + root.path("metadata").path("message").asText());

                        String sql = "INSERT INTO status_tte (no_rawat, nik, tanggal, nama_berkas, status_code, lokasi_file, status, keterangan) VALUES (?, ?, ?,?,?,?,?,?)";
                        try ( PreparedStatement pst = koneksi.prepareStatement(sql)) {
                            pst.setString(1, tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString().trim());
                            pst.setString(2, Sequel.cariIsi("select pegawai.no_ktp from pegawai where pegawai.nik=?", akses.getkode()));
                            pst.setString(3, now);
                            pst.setString(4, fileName);
                            pst.setString(5, response.getCode() + "");
                            pst.setString(6, ""); // Path kosong karena TTE gagal
                            pst.setString(7, statusKeterangan);
                            pst.setString(8, keteranganText);
                            pst.executeUpdate();
                            System.out.println("Status TTE gagal disimpan dengan path kosong");
                        } catch (SQLException ex) {
                            System.out.println("Gagal menyimpan status TTE: " + ex);
                        }
                    }
                } catch (IOException a) {
                    System.out.println("Notifikasi IO: " + a);
                } finally {
                    // Close HTTP client
                    try {
                        httpClient.close();
                    } catch (IOException e) {
                        System.out.println("Error closing HTTP client: " + e);
                    }
                }
            } catch (Exception e) {
                System.out.println("Notifikasi SSL: " + e);

                // Enhanced SSL error logging
                String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String fileName = "ResumeRI" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString().trim().replaceAll("/", "") + "_" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 1).toString().trim().replaceAll(" ", "") + "_" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 2).toString().trim().replaceAll(" ", "") + ".pdf";
                String keteranganText = buildDetailedDescription(no_sep, tanggal, usedRegistrationDate, usedNoRawatAsFallback, false, "SSL Error: " + e.getMessage());

                String sql = "INSERT INTO status_tte (no_rawat, nik, tanggal, nama_berkas, status_code, lokasi_file, status, keterangan) VALUES (?, ?, ?,?,?,?,?,?)";
                try ( PreparedStatement pst = koneksi.prepareStatement(sql)) {
                    pst.setString(1, tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString().trim());
                    pst.setString(2, Sequel.cariIsi("select pegawai.no_ktp from pegawai where pegawai.nik=?", akses.getkode()));
                    pst.setString(3, now);
                    pst.setString(4, fileName);
                    pst.setString(5, "0"); // Error code
                    pst.setString(6, ""); // Path kosong karena SSL error
                    pst.setString(7, "Error SSL");
                    pst.setString(8, keteranganText);
                    pst.executeUpdate();
                    System.out.println("Status TTE error SSL disimpan dengan path kosong");
                } catch (SQLException ex) {
                    System.out.println("Gagal menyimpan status TTE error: " + ex);
                }
            }
        } else {
            // User memilih untuk tidak melanjutkan proses
            System.out.println("Proses TTE dibatalkan karena No SEP tidak ditemukan dan user memilih untuk tidak melanjutkan.");

            // Enhanced cancellation logging
            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String fileName = "ResumeRI" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString().trim().replaceAll("/", "") + "_" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 1).toString().trim().replaceAll(" ", "") + "_" + tbDokter.getValueAt(tbDokter.getSelectedRow(), 2).toString().trim().replaceAll(" ", "") + ".pdf";

            String fallbackSep = tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString().trim().replaceAll("/", "");
            String fallbackDate = (tanggal_registrasi != null && !tanggal_registrasi.trim().isEmpty()) ? tanggal_registrasi : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            String keteranganText = String.format(
                    "Proses dibatalkan user - No SEP tidak ditemukan untuk No Rawat: %s | No SEP pengganti: %s | Tanggal fallback: %s (%s)",
                    tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString(),
                    fallbackSep,
                    fallbackDate,
                    (tanggal_registrasi != null && !tanggal_registrasi.trim().isEmpty()) ? "registrasi" : "sistem"
            );

            String sql = "INSERT INTO status_tte (no_rawat, nik, tanggal, nama_berkas, status_code, lokasi_file, status, keterangan) VALUES (?, ?, ?,?,?,?,?,?)";
            try ( PreparedStatement pst = koneksi.prepareStatement(sql)) {
                pst.setString(1, tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString().trim());
                pst.setString(2, Sequel.cariIsi("select pegawai.no_ktp from pegawai where pegawai.nik=?", akses.getkode()));
                pst.setString(3, now);
                pst.setString(4, fileName);
                pst.setString(5, "0"); // Status dibatalkan
                pst.setString(6, "-");
                pst.setString(7, "Dibatalkan");
                pst.setString(8, keteranganText);
                pst.executeUpdate();
                System.out.println("Status TTE dibatalkan disimpan dengan keterangan: " + keteranganText);
            } catch (SQLException ex) {
                System.out.println("Gagal menyimpan status TTE dibatalkan: " + ex);
            }
        }
    }//GEN-LAST:event_BtnSimpanTandaTanganActionPerformed

    private void MnTteBtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnTteBtnPrintActionPerformed
        //"No.Rawat", "Pasien", "Petugas", "Tgl.Periksa", "Jam Periksa", "Dokter Perujuk", "Penanggung Jawab", "No Telp"

        String noRawat = tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString();
        String tanggal = tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString(); // Format: yyyy-MM-dd
        String jam = tbDokter.getValueAt(tbDokter.getSelectedRow(), 4).toString();     // Format: HH:mm:ss
        String tanggalLengkap = tanggal + " " + jam;
        //   String tanggalLengkap = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String kode = Sequel.cariIsi("select kode from master_berkas_digital where nama like 'lab'");

        if (Sequel.cariInteger("SELECT COUNT(no_rawat) FROM berkas_tte WHERE no_rawat='" + noRawat + "' AND tanggal='" + tanggalLengkap + "' AND kode='" + kode + "'") > 0) {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Dokumen sudah di-TTE. Apakah Anda ingin membukanya?",
                    "Konfirmasi",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                String pathPDF = Sequel.cariIsi("select file_path from berkas_tte WHERE no_rawat='" + noRawat + "' AND tanggal='" + tanggalLengkap + "' AND kode='" + kode + "'");

                ViewPDFNextcloud viewer = new ViewPDFNextcloud();
                JPanel viewerPanel = viewer.getViewerPanel(pathPDF);

                if (viewerPanel != null) {
                    Window parentWindow = SwingUtilities.getWindowAncestor(internalFrame1);
                    JDialog dialog = new JDialog(parentWindow, "Lihat Hasil Lab PK", Dialog.ModalityType.APPLICATION_MODAL);
                    dialog.getContentPane().add(viewerPanel);
                    dialog.setSize(800, 600);
                    dialog.setLocationRelativeTo(internalFrame1);
                    dialog.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Gagal membuka dokumen.");
                }
            }
        } else {
            WindowPhrase.setAlwaysOnTop(false);
            WindowPhrase.setLocationRelativeTo(internalFrame1);
            WindowPhrase.setVisible(true);
        }
    }//GEN-LAST:event_MnTteBtnPrintActionPerformed

    private void TagSignerDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TagSignerDokterActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
            TCari.requestFocus();
        } else if (tbDokter.getSelectedRow() <= -1) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan pilih data..!!");
        } else {
            if (Kd2.getText().equals("") || Petugas.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu...!!!!");
            } else {
                int selectedRow = tbDokter.getSelectedRow();
                String noRawat = tbDokter.getValueAt(selectedRow, 0).toString();
                String namaPasien = tbDokter.getValueAt(selectedRow, 1).toString();
                
                boolean success = processSingleRadTagSign(noRawat, namaPasien, selectedRow, false);
                
                if (success) {
                    JOptionPane.showMessageDialog(null, "PDF berhasil dibuat dan diupload!");
                } else {
                    JOptionPane.showMessageDialog(null, "Gagal membuat PDF. Periksa console untuk detail error.");
                }
            }
        }
    }//GEN-LAST:event_TagSignerDokterActionPerformed

    private void TagSignerDokterBatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TagSignerDokterBatchActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, tidak ada data untuk diproses...!!!!");
            TCari.requestFocus();
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(null, 
            "Proses batch akan membuat PDF untuk semua " + tabMode.getRowCount() + " data radiologi.\nLanjutkan?",
            "Konfirmasi Batch", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        int successCount = 0;
        int failCount = 0;
        
        for (int i = 0; i < tabMode.getRowCount(); i++) {
            String noRawat = tbDokter.getValueAt(i, 0).toString();
            String namaPasien = tbDokter.getValueAt(i, 1).toString();
            
            boolean success = processSingleRadTagSign(noRawat, namaPasien, i, true);
            
            if (success) {
                successCount++;
            } else {
                failCount++;
            }
        }
        
        JOptionPane.showMessageDialog(null, 
            "Proses batch selesai!\n" +
            "Berhasil: " + successCount + "\n" +
            "Gagal: " + failCount);
    }//GEN-LAST:event_TagSignerDokterBatchActionPerformed

    private void PDFExpertiseGambarRadiologiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PDFExpertiseGambarRadiologiActionPerformed
        if (tbDokter.getSelectedRow() != -1) {
            if ((!Kd2.getText().equals("")) && (!Petugas.getText().equals(""))) {
                String FileName = "HasilRadiologiGambar-" + NoRawatDicari.getText().replaceAll("/", "-") + "-" + TglDicari.getText().replaceAll("-", "") + "-" + JamDicari.getText().replaceAll(":", "");
                CreatePDFGambar(FileName);
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu...!!!!");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu...!!!!");
        }
    }//GEN-LAST:event_PDFExpertiseGambarRadiologiActionPerformed

    private void btnKirimWaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKirimWaActionPerformed
        if (tbDokter.getSelectedRow() != -1) {
            String FileName = "Gambar_Radiologi-" + NoRawatDicari.getText().replaceAll("/", "-") + "-" + TglDicari.getText().replaceAll("-", "") + "-" + JamDicari.getText().replaceAll(":", "");
            CreatePDFGambar(FileName);

            String noRM = Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat=?", NoRawatDicari.getText());
            String noHp = Sequel.cariIsi("select pasien.no_tlp from pasien where pasien.no_rkm_medis=?", noRM);
            String tglLahir = Sequel.cariIsi("select pasien.tgl_lahir from pasien where pasien.no_rkm_medis=?", noRM);
            String namaPasien = Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=?", noRM);

            // Dialog konfirmasi nomor HP
            JTextField fieldNoHp = new JTextField(noHp, 20);
            JCheckBox checkUpdate = new JCheckBox("Update No. Telp di Data Pasien");
            JPanel panel = new JPanel(new java.awt.GridLayout(0, 1));
            panel.add(new JLabel("Nomor WhatsApp Pasien:"));
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
                tampil();
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
                        password = parts[2] + parts[1] + parts[0]; // ddmmyyyy
                    }
                } catch (Exception e) {
                    System.out.println("Error format tanggal lahir: " + e.getMessage());
                }
            }

            if (password.isEmpty()) {
                password = "12345678"; // default password
            }

            try {
                File file = new File("tmpPDF/" + FileName + ".pdf");
                if (!file.exists()) {
                    JOptionPane.showMessageDialog(null, "File PDF tidak ditemukan di tmpPDF/.");
                    return;
                }

                // Encrypt PDF
                String encryptedFilePath = encryptPDF(file.getAbsolutePath(), password);
                File encryptedFile = new File(encryptedFilePath);

                String waUrl = koneksiDB.URLWAGATEWAY() + "/send/file";
                String waUser = koneksiDB.USERWAGATEWAY();
                String waPass = koneksiDB.PASSWAGATEWAY();
                String deviceId = koneksiDB.GOWADEVICEIDRADIOLOGI();

                try ( CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    HttpPost uploadFile = new HttpPost(waUrl);

                    // Basic Auth
                    String auth = waUser + ":" + waPass;
                    byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
                    String authHeader = "Basic " + new String(encodedAuth);
                    uploadFile.setHeader("Authorization", authHeader);
                    
                    if (!deviceId.isEmpty()) {
                        uploadFile.setHeader("X-Device-Id", deviceId);
                    }

                    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                    builder.setCharset(StandardCharsets.UTF_8);
                    builder.setMode(HttpMultipartMode.EXTENDED);
                    builder.addTextBody("phone", noHp, ContentType.create("text/plain", StandardCharsets.UTF_8));
                    builder.addBinaryBody(
                            "file",
                            encryptedFile,
                            ContentType.APPLICATION_PDF,
                            encryptedFile.getName()
                    );
                    
                    String caption = "*PEMBERITAHUAN HASIL PEMERIKSAAN RADIOLOGI*\n"
                            + "🏥 *" + akses.getnamars() + "*\n\n"
                            + "Yth. Bapak/Ibu/Saudara/i *" + namaPasien + "*,\n\n"
                            + "Terima kasih telah mempercayakan layanan kesehatan Anda kepada kami. Terlampir bersama pesan ini adalah hasil pemeriksaan Radiologi Anda:\n\n"
                            + "*Detail Pemeriksaan:*\n"
                            + "👤 Nama Pasien: " + namaPasien + "\n"
                            + "📅 Tanggal Periksa: " + TglDicari.getText() + "\n\n"
                            + "*Keamanan Dokumen:*\n"
                            + "Untuk melindungi privasi data medis Anda, dokumen PDF ini telah *TERPROTEKSI PASSWORD*. \n\n"
                            + "Silakan gunakan format berikut untuk membuka dokumen:\n"
                            + "📌 *Password: Tanggal Lahir (ddmmyyyy)*\n"
                            + "Contoh: Jika lahir 17 Agustus 1945, maka password adalah *17081945*.\n\n"
                            + "Semoga Bapak/Ibu sehat selalu.\n\n"
                            + "---\n"
                            + "📞 " + akses.getkontakrs() + " | " + akses.getnamars();
                    
                    builder.addTextBody("caption", caption, ContentType.create("text/plain", StandardCharsets.UTF_8));

                    HttpEntity multipart = builder.build();
                    uploadFile.setEntity(multipart);

                    try ( CloseableHttpResponse response = httpClient.execute(uploadFile)) {
                        String responseString = EntityUtils.toString(response.getEntity());
                        System.out.println("WhatsApp Gateway Response: " + responseString);

                        String statusKirim = (response.getCode() == 200) ? "Terkirim" : "Gagal";
                        String tglKirim = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
                        String noHpClean = noHp.replace("@s.whatsapp.net", "");
                        Sequel.queryu("insert into wa_radiologi_status values('"
                                + NoRawatDicari.getText() + "','"
                                + TglDicari.getText() + "','"
                                + JamDicari.getText() + "','"
                                + statusKirim + "','"
                                + tglKirim + "','"
                                + noHpClean + "') "
                                + "on duplicate key update status_kirim='" + statusKirim
                                + "', tgl_kirim='" + tglKirim
                                + "', no_hp='" + noHpClean + "'");

                        if (response.getCode() == 200) {
                            JOptionPane.showMessageDialog(null, "Berhasil mengirim laporan ke nomor: " + noHp + "\nPassword PDF: " + password);
                        } else {
                            JOptionPane.showMessageDialog(null, "Gagal mengirim WhatsApp. Kode: " + response.getCode() + "\nRespon: " + responseString);
                        }
                        tampil();
                    }
                }
            } catch (Exception e) {
                System.out.println("Error Kirim WA: " + e);
                String tglKirim = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
                Sequel.queryu("insert into wa_radiologi_status values('"
                        + NoRawatDicari.getText() + "','"
                        + TglDicari.getText() + "','"
                        + JamDicari.getText() + "','Gagal','"
                        + tglKirim + "','') "
                        + "on duplicate key update status_kirim='Gagal', tgl_kirim='" + tglKirim + "'");
                tampil();
                JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat mengirim WhatsApp: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu...!!!!");
        }
    }//GEN-LAST:event_btnKirimWaActionPerformed

    private void btnKirimWaDokterPJActionPerformed(java.awt.event.ActionEvent evt) {
        if (tbDokter.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu...!!!!");
            return;
        }
        String kdDokter = Sequel.cariIsi("select kd_dokter from periksa_radiologi where no_rawat='"
                + NoRawatDicari.getText() + "' and tgl_periksa='" + TglDicari.getText()
                + "' and jam='" + JamDicari.getText() + "' limit 1");
        kirimWaKeDokter(kdDokter, "Dokter PJ Radiologi");
    }

    private void btnKirimWaDokterPengirimActionPerformed(java.awt.event.ActionEvent evt) {
        if (tbDokter.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu...!!!!");
            return;
        }
        String kdDokter = Sequel.cariIsi("select dokter_perujuk from periksa_radiologi where no_rawat='"
                + NoRawatDicari.getText() + "' and tgl_periksa='" + TglDicari.getText()
                + "' and jam='" + JamDicari.getText() + "' limit 1");
        kirimWaKeDokter(kdDokter, "Dokter Pengirim");
    }

    private void kirimWaKeDokter(String kdDokter, String labelJenis) {
        if (kdDokter == null || kdDokter.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, labelJenis + " tidak ditemukan untuk pemeriksaan ini.");
            return;
        }

        String noRawat = NoRawatDicari.getText();
        String tglPeriksa = TglDicari.getText();
        String jamPeriksa = JamDicari.getText();

        String namaDokter = Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", kdDokter);
        String noHp = Sequel.cariIsi("select no_telp from dokter where kd_dokter=?", kdDokter);

        String noRM = Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", noRawat);
        String namaPasien = Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", noRM);

        String diagnosaKlinis = Sequel.cariIsi(
                "select diagnosa_klinis from permintaan_radiologi where no_rawat='" + noRawat
                + "' and tgl_permintaan<='" + tglPeriksa
                + "' order by tgl_permintaan desc, jam_permintaan desc limit 1");
        String informasiTambahan = Sequel.cariIsi(
                "select informasi_tambahan from permintaan_radiologi where no_rawat='" + noRawat
                + "' and tgl_permintaan<='" + tglPeriksa
                + "' order by tgl_permintaan desc, jam_permintaan desc limit 1");

        JTextField fieldNoHp = new JTextField(noHp == null ? "" : noHp, 20);
        JCheckBox checkUpdate = new JCheckBox("Update No. Telp di Data Dokter");
        JPanel panel = new JPanel(new java.awt.GridLayout(0, 1));
        panel.add(new JLabel(labelJenis + ": " + (namaDokter == null ? "" : namaDokter) + " (" + kdDokter + ")"));
        panel.add(new JLabel("Nomor WhatsApp:"));
        panel.add(fieldNoHp);
        panel.add(checkUpdate);

        int result = JOptionPane.showConfirmDialog(null, panel,
                "Konfirmasi Pengiriman WhatsApp ke " + labelJenis,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        noHp = fieldNoHp.getText().trim();
        if (noHp.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Nomor HP tidak boleh kosong!");
            return;
        }

        if (checkUpdate.isSelected()) {
            Sequel.mengedit("dokter", "kd_dokter='" + kdDokter + "'", "no_telp='" + noHp + "'");
        }

        if (noHp.startsWith("0")) {
            noHp = "62" + noHp.substring(1);
        } else if (!noHp.startsWith("62")) {
            noHp = "62" + noHp;
        }
        if (!noHp.endsWith("@s.whatsapp.net")) {
            noHp = noHp + "@s.whatsapp.net";
        }

        java.util.List<String> lokasiList = new java.util.ArrayList<>();
        try (java.sql.PreparedStatement psImg = koneksi.prepareStatement(
                "select lokasi_gambar from gambar_radiologi where no_rawat=? and tgl_periksa=? and jam=?")) {
            psImg.setString(1, noRawat);
            psImg.setString(2, tglPeriksa);
            psImg.setString(3, jamPeriksa);
            try (java.sql.ResultSet rsImg = psImg.executeQuery()) {
                while (rsImg.next()) {
                    lokasiList.add(rsImg.getString("lokasi_gambar"));
                }
            }
        } catch (Exception e) {
            System.out.println("Error baca gambar_radiologi: " + e);
        }

        if (lokasiList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tidak ada gambar radiologi untuk pemeriksaan ini.");
            return;
        }

        String diag = (diagnosaKlinis == null || diagnosaKlinis.trim().isEmpty()) ? "-" : diagnosaKlinis;
        String info = (informasiTambahan == null || informasiTambahan.trim().isEmpty()) ? "-" : informasiTambahan;

        String pesanInti;
        if ("Dokter PJ Radiologi".equalsIgnoreCase(labelJenis)) {
            pesanInti = "Mohon perkenan dan kesediaan Bapak/Ibu Dokter untuk berkenan melakukan pembacaan "
                    + "serta validasi hasil pemeriksaan radiologi pasien sesuai prosedur yang berlaku.";
        } else {
            pesanInti = "Bersama pesan ini, kami sampaikan hasil pemeriksaan radiologi pasien yang telah "
                    + "Bapak/Ibu Dokter rujukkan, sebagai bahan tindak lanjut sesuai kebutuhan klinis.";
        }

        String caption = "*PEMBERITAHUAN HASIL PEMERIKSAAN RADIOLOGI*\n"
                + "🏥 *" + akses.getnamars() + "*\n\n"
                + "Yth. *" + (namaDokter == null ? "" : namaDokter) + "*\n"
                + "(" + labelJenis + ")\n\n"
                + "Dengan hormat,\n\n"
                + pesanInti + "\n\n"
                + "*Identitas Pasien:*\n"
                + "👤 Nama Pasien: " + namaPasien + "\n"
                + "🆔 No. RM: " + noRM + "\n"
                + "📅 Tanggal Periksa: " + tglPeriksa + " " + jamPeriksa + "\n\n"
                + "*Diagnosa & Klinis (dari permintaan):*\n"
                + "📝 Diagnosa Klinis: " + diag + "\n"
                + "📋 Informasi Tambahan: " + info + "\n\n"
                + "Atas perhatian dan kerjasamanya, kami sampaikan terima kasih.\n\n"
                + "Hormat kami,\n"
                + "🏥 *" + akses.getnamars() + "*\n"
                + "📞 " + akses.getkontakrs();

        String waUrl = koneksiDB.URLWAGATEWAY() + "/send/image";
        String waUser = koneksiDB.USERWAGATEWAY();
        String waPass = koneksiDB.PASSWAGATEWAY();
        String deviceId = koneksiDB.GOWADEVICEIDRADIOLOGI();
        String urlBase = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB()
                + "/" + koneksiDB.HYBRIDWEB() + "/radiologi/";

        int berhasil = 0, gagal = 0;
        String lastResponse = "";
        int lastCode = 0;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            for (int i = 0; i < lokasiList.size(); i++) {
                String lokasi = lokasiList.get(i);
                String imgUrl = urlBase + lokasi;

                byte[] imgBytes;
                try (java.io.InputStream is = new java.net.URL(imgUrl).openStream()) {
                    imgBytes = is.readAllBytes();
                } catch (Exception ex) {
                    System.out.println("Gagal download gambar " + imgUrl + ": " + ex);
                    gagal++;
                    continue;
                }

                String filename = lokasi.contains("/") ? lokasi.substring(lokasi.lastIndexOf('/') + 1) : lokasi;
                String lower = filename.toLowerCase();
                ContentType ct = ContentType.IMAGE_JPEG;
                if (lower.endsWith(".png")) {
                    ct = ContentType.IMAGE_PNG;
                } else if (lower.endsWith(".gif")) {
                    ct = ContentType.IMAGE_GIF;
                }

                HttpPost post = new HttpPost(waUrl);
                String auth = waUser + ":" + waPass;
                byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
                post.setHeader("Authorization", "Basic " + new String(encodedAuth));
                if (deviceId != null && !deviceId.isEmpty()) {
                    post.setHeader("X-Device-Id", deviceId);
                }

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setCharset(StandardCharsets.UTF_8);
                builder.setMode(HttpMultipartMode.EXTENDED);
                builder.addTextBody("phone", noHp, ContentType.create("text/plain", StandardCharsets.UTF_8));
                builder.addBinaryBody("image", imgBytes, ct, filename);
                if (i == 0) {
                    builder.addTextBody("caption", caption, ContentType.create("text/plain", StandardCharsets.UTF_8));
                }

                HttpEntity multipart = builder.build();
                post.setEntity(multipart);

                try (CloseableHttpResponse response = httpClient.execute(post)) {
                    String responseString = EntityUtils.toString(response.getEntity());
                    System.out.println("WA Gateway Response (" + labelJenis + ") img " + (i + 1)
                            + "/" + lokasiList.size() + ": " + responseString);
                    lastResponse = responseString;
                    lastCode = response.getCode();
                    if (response.getCode() == 200) {
                        berhasil++;
                    } else {
                        gagal++;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error Kirim WA " + labelJenis + ": " + e);
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat mengirim WhatsApp: " + e.getMessage());
            return;
        }

        if (berhasil > 0 && gagal == 0) {
            JOptionPane.showMessageDialog(null, "Berhasil mengirim " + berhasil + " gambar ke "
                    + labelJenis + " (" + namaDokter + ") nomor: " + noHp);
        } else if (berhasil > 0) {
            JOptionPane.showMessageDialog(null, "Sebagian terkirim: " + berhasil + " berhasil, "
                    + gagal + " gagal.\nResponse terakhir kode " + lastCode + ":\n" + lastResponse);
        } else {
            JOptionPane.showMessageDialog(null, "Gagal mengirim semua gambar (" + gagal
                    + " gagal).\nResponse terakhir kode " + lastCode + ":\n" + lastResponse);
        }
    }

    private void BtnConvertKirimOrthancActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnConvertKirimOrthancActionPerformed
        int viewRow = tbDokter.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(null, "Silahkan pilih data pemeriksaan terlebih dahulu");
            return;
        }
        // Convert ke model index biar konsisten dgn kirimDataOrthanc yang baca tabMode
        int modelRow = tbDokter.convertRowIndexToModel(viewRow);

        // Tolak baris detail/header/total (no_rawat kosong atau diawali ">>")
        Object v0 = tabMode.getValueAt(modelRow, 0);
        String noRawat = v0 == null ? "" : v0.toString().trim();
        if (noRawat.isEmpty() || noRawat.startsWith(">>")) {
            JOptionPane.showMessageDialog(null,
                    "Pilih baris pemeriksaan (yang ada No.Rawat),\nbukan baris detail tindakan / BHP / total.");
            return;
        }

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        int[] result = kirimDataOrthanc(modelRow);
        this.setCursor(Cursor.getDefaultCursor());

        int sukses = result[0], gagal = result[1];
        int sudahAda = result.length > 2 ? result[2] : 0;
        StringBuilder msg = new StringBuilder();
        if (sukses > 0) msg.append("Berhasil mengirim ").append(sukses).append(" gambar ke Orthanc.");
        if (gagal > 0)   msg.append((msg.length() > 0 ? "\n" : "")).append("Gagal: ").append(gagal).append(" gambar.");
        if (sudahAda > 0) msg.append((msg.length() > 0 ? "\n" : "")).append("Sudah pernah ter-upload (di-skip): ").append(sudahAda).append(" gambar.");
        if (msg.length() == 0) msg.append("Tidak ada gambar radiologi ditemukan untuk pemeriksaan ini.");
        JOptionPane.showMessageDialog(null, msg.toString());
    }//GEN-LAST:event_BtnConvertKirimOrthancActionPerformed

    private void BtnConvertKirimOrthancKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnConvertKirimOrthancKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnConvertKirimOrthancKeyPressed

    private void BtnConvertKirimOrthancBatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnConvertKirimOrthancBatchActionPerformed
        int count = tabMode.getRowCount();
        if (count == 0) {
            JOptionPane.showMessageDialog(null, "Tidak ada data pasien yang ditampilkan.");
            return;
        }

        // Filter cuma baris pasien (skip header detail / BHP / total).
        final java.util.List<Integer> pasienRows = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            Object v0 = tabMode.getValueAt(i, 0);
            if (v0 == null) continue;
            String noRawat = v0.toString().trim();
            if (noRawat.isEmpty() || noRawat.startsWith(">>")) continue;
            pasienRows.add(i);
        }

        if (pasienRows.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tidak ada baris pemeriksaan untuk diproses.");
            return;
        }

        int reply = JOptionPane.showConfirmDialog(rootPane,
                "Apakah anda yakin ingin mengirim " + pasienRows.size() + " data pasien ke Orthanc?\n"
                + "Proses berjalan di background — tombol Cancel di progress bar bisa dipakai untuk membatalkan.",
                "Konfirmasi Batch Upload", JOptionPane.YES_NO_OPTION);
        if (reply != JOptionPane.YES_OPTION) return;

        // ProgressMonitor: built-in dialog with cancel button + progress bar
        final javax.swing.ProgressMonitor monitor = new javax.swing.ProgressMonitor(
                this, "Mengirim data ke Orthanc...", "Mempersiapkan...", 0, pasienRows.size());
        monitor.setMillisToDecideToPopup(0);
        monitor.setMillisToPopup(0);
        monitor.setProgress(0);

        // SwingWorker: doInBackground jalan di thread terpisah, EDT tidak freeze
        javax.swing.SwingWorker<int[], BatchProgress> worker = new javax.swing.SwingWorker<int[], BatchProgress>() {
            @Override
            protected int[] doInBackground() {
                int totalSuccess = 0, totalFail = 0, totalAlready = 0;
                int patientsProcessed = 0, patientsSkipped = 0;
                int idx = 0;
                for (int modelRow : pasienRows) {
                    if (isCancelled() || monitor.isCanceled()) break;
                    idx++;
                    String labelPasien = "row " + (modelRow + 1);
                    try {
                        Object pasienInfo = tabMode.getValueAt(modelRow, 1);
                        if (pasienInfo != null) labelPasien = pasienInfo.toString();
                    } catch (Exception ignored) {}
                    publish(new BatchProgress(idx, pasienRows.size(), labelPasien));

                    try {
                        int[] result = kirimDataOrthanc(modelRow);
                        int rSucc = result[0], rFail = result[1];
                        int rAlready = result.length > 2 ? result[2] : 0;
                        totalSuccess += rSucc;
                        totalFail    += rFail;
                        totalAlready += rAlready;
                        if (rSucc > 0 || rFail > 0) {
                            patientsProcessed++;
                        } else {
                            // 0 sukses, 0 gagal — entah belum ada gambar, atau semua sudah ter-upload
                            patientsSkipped++;
                        }
                    } catch (Exception e) {
                        System.out.println("Error processing row " + modelRow + ": " + e);
                    }
                }
                return new int[]{totalSuccess, totalFail, patientsProcessed, patientsSkipped, totalAlready};
            }

            @Override
            protected void process(java.util.List<BatchProgress> chunks) {
                BatchProgress last = chunks.get(chunks.size() - 1);
                monitor.setProgress(last.idx);
                monitor.setNote(String.format("(%d/%d) %s",
                        last.idx, last.total,
                        last.label.length() > 60 ? last.label.substring(0, 60) + "..." : last.label));
            }

            @Override
            protected void done() {
                monitor.close();
                try {
                    int[] r = get();
                    int totalAlready = r.length > 4 ? r[4] : 0;
                    String headline = isCancelled() || monitor.isCanceled()
                            ? "Proses Batch DIBATALKAN"
                            : "Proses Batch Selesai!";
                    JOptionPane.showMessageDialog(null, headline + "\n\n"
                        + "Pasien Diproses: " + r[2] + "\n"
                        + "Pasien Tanpa Gambar: " + r[3] + "\n"
                        + "------------------------\n"
                        + "Total Gambar Terkirim: " + r[0] + "\n"
                        + "Total Gambar Gagal: " + r[1] + "\n"
                        + "Sudah Pernah Ter-upload (di-skip): " + totalAlready);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,
                            "Batch error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }//GEN-LAST:event_BtnConvertKirimOrthancBatchActionPerformed

    /** Snapshot progress untuk SwingWorker.publish/process. */
    private static final class BatchProgress {
        final int idx, total;
        final String label;
        BatchProgress(int idx, int total, String label) {
            this.idx = idx; this.total = total; this.label = label;
        }
    }

    private void BtnConvertKirimOrthancBatchKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnConvertKirimOrthancBatchKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnConvertKirimOrthancBatchKeyPressed
    
    private boolean processSingleRadTagSign(String noRawat, String namaPasien, int rowIndex, boolean isBatch) {
        try {
            // Get Date and Time from table for specific selection
            String tglPeriksa = tbDokter.getValueAt(rowIndex, 3).toString();
            String jamPeriksa = tbDokter.getValueAt(rowIndex, 4).toString();
            
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            pemeriksaan = "";
            
            // Fetch pemeriksaan data
            ps2 = koneksi.prepareStatement(
                    "select jns_perawatan_radiologi.kd_jenis_prw,jns_perawatan_radiologi.nm_perawatan,periksa_radiologi.biaya,"
                    + "periksa_radiologi.kd_dokter,periksa_radiologi.nip,periksa_radiologi.proyeksi,periksa_radiologi.kV,periksa_radiologi.mAS,periksa_radiologi.FFD,"
                    + "periksa_radiologi.BSF,periksa_radiologi.inak,periksa_radiologi.jml_penyinaran,periksa_radiologi.dosis from periksa_radiologi inner join jns_perawatan_radiologi "
                    + "on periksa_radiologi.kd_jenis_prw=jns_perawatan_radiologi.kd_jenis_prw where periksa_radiologi.no_rawat=? and periksa_radiologi.tgl_periksa=? "
                    + "and periksa_radiologi.jam=?");
            try {
                ps2.setString(1, noRawat);
                ps2.setString(2, tglPeriksa);
                ps2.setString(3, jamPeriksa);
                rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    pemeriksaan = rs2.getString("nm_perawatan") + ", " + pemeriksaan;
                    kdpenjab = rs2.getString("kd_dokter");
                    kdpetugas = rs2.getString("nip");
                }
            } catch (Exception e) {
                System.out.println("simrskhanza.DlgCariPeriksaRadiologi.processSingleRadTagSign() ps2 : " + e);
            } finally {
                if (rs2 != null) {
                    rs2.close();
                }
                if (ps2 != null) {
                    ps2.close();
                }
            }

            // Fetch patient data
            Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat=? ", NoRM, noRawat);
            Sequel.cariIsi("select pasien.jk from pasien where pasien.no_rkm_medis=? ", Jk, NoRM.getText());
            Sequel.cariIsi("select pasien.umur from pasien where pasien.no_rkm_medis=?", Umur, NoRM.getText());
            Sequel.cariIsi("select concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat from pasien inner join kelurahan inner join kecamatan inner join kabupaten on pasien.kd_kel=kelurahan.kd_kel and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab where no_rkm_medis=? ", Alamat, NoRM.getText());

            kamar = Sequel.cariIsi("select ifnull(kamar_inap.kd_kamar,'') from kamar_inap where kamar_inap.no_rawat='" + noRawat + "' order by kamar_inap.tgl_masuk desc limit 1");
            if (!kamar.equals("")) {
                namakamar = kamar + ", " + Sequel.cariIsi("select bangsal.nm_bangsal from bangsal inner join kamar on bangsal.kd_bangsal=kamar.kd_bangsal "
                        + " where kamar.kd_kamar='" + kamar + "' ");
                kamar = "Kamar";
            } else if (kamar.equals("")) {
                kamar = "Poli";
                namakamar = Sequel.cariIsi("select poliklinik.nm_poli from poliklinik inner join reg_periksa on poliklinik.kd_poli=reg_periksa.kd_poli "
                        + "where reg_periksa.no_rawat='" + noRawat + "'");
            }
            
            // Prepare parameters
            Map<String, Object> param = new HashMap<>();
            param.put("noperiksa", noRawat);
            param.put("norm", NoRM.getText());
            param.put("namapasien", Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=? ", NoRM.getText()));
            param.put("jkel", Jk.getText());
            param.put("umur", Umur.getText());
            param.put("lahir", Sequel.cariIsi("select DATE_FORMAT(pasien.tgl_lahir,'%d-%m-%Y') from pasien where pasien.no_rkm_medis=? ", NoRM.getText()));
            param.put("pengirim", tbDokter.getValueAt(rowIndex, 5).toString());
            param.put("tanggal", Valid.SetTgl3(tbDokter.getValueAt(rowIndex, 3).toString()));
            param.put("penjab", tbDokter.getValueAt(rowIndex, 6).toString());
            param.put("petugas", tbDokter.getValueAt(rowIndex, 2).toString());
            param.put("alamat", Alamat.getText());
            param.put("kamar", kamar);
            param.put("namakamar", namakamar);
            param.put("pemeriksaan", pemeriksaan);
            param.put("jam", tbDokter.getValueAt(rowIndex, 4).toString());
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            
            // Fetch hasil periksa
            String hasilPeriksa = "";
            try {
                ps3 = koneksi.prepareStatement("select hasil from hasil_radiologi where no_rawat=? and tgl_periksa=? and jam=?");
                ps3.setString(1, noRawat);
                ps3.setString(2, tglPeriksa);
                ps3.setString(3, jamPeriksa);
                rs3 = ps3.executeQuery();
                if (rs3.next()) {
                    hasilPeriksa = rs3.getString("hasil");
                }
            } catch (Exception e) {
                System.out.println("Error fetching hasil: " + e);
            } finally {
                if (rs3 != null) rs3.close();
                if (ps3 != null) ps3.close();
            }
            param.put("hasil", hasilPeriksa);
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            param.put("logobsre", Sequel.cariGambar("select gambar.bsre from gambar"));
            
            // Use tag_signer for finger parameter
            String tagSigner = Sequel.cariIsi("select tag_signer from dokter where dokter.kd_dokter=?", kdpenjab);
            if (tagSigner == null || tagSigner.trim().isEmpty()) {
                tagSigner = "#";
            }
            param.put("finger", tagSigner);
            
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", kdpetugas);
            param.put("finger2", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbDokter.getValueAt(rowIndex, 2).toString() + "\nID " + (finger.equals("") ? kdpetugas : finger) + "\n" + Valid.SetTgl3(tbDokter.getValueAt(rowIndex, 3).toString()));

            // Generate PDF using TTE report using MyReportPDFUpload (saves to tmpPDF and avoids GUI)
            String reportName = "rptPeriksaRadiologiTte.jasper";
            // Define File Name (without extension for MyReportPDFUpload)
            String fileNameWithoutExt = "Radiologi_" + noRawat.replaceAll("/", "") + "_" + namaPasien.replaceAll("[^a-zA-Z0-9]", "");
            
            System.out.println("DEBUG: Generating PDF with template: " + reportName);
            System.out.println("DEBUG: Output filename: " + fileNameWithoutExt);
            
            try {
                // Call MyReportPDFUpload: reportName, reportDirName, judul, FileName, parameters
                Valid.MyReportPDFUpload(reportName, "report", "::[ Pemeriksaan Radiologi ]::", fileNameWithoutExt, param);
                System.out.println("DEBUG: Valid.MyReportPDFUpload executed.");
            } catch (Exception e) {
                System.err.println("DEBUG: Error in Valid.MyReportPDFUpload: " + e.getMessage());
                e.printStackTrace();
                throw e; 
            }
            
            String destFile = "tmpPDF/" + fileNameWithoutExt + ".pdf";
            System.out.println("DEBUG: Destination file expected at: " + destFile);

            File f = new File(destFile);
            if (!f.exists() || f.length() == 0) {
                System.err.println("DEBUG: Generated PDF not found or empty: " + f.getAbsolutePath());
                this.setCursor(Cursor.getDefaultCursor());
                return false;
            } else {
                System.out.println("DEBUG: PDF File found. Size: " + f.length() + " bytes.");
            }

            // Upload to Local Server (berkas_digital_perawatan)
            System.out.println("DEBUG: Uploading document...");
            UploadPDF(fileNameWithoutExt, "berkasrawat/pages/upload/", noRawat);

            // Delete temporary file
            if (f.exists()) {
                f.delete();
            }

            this.setCursor(Cursor.getDefaultCursor());
            return true;

        } catch (Exception e) {
            System.out.println("Notifikasi processSingleRadTagSign : " + e);
            e.printStackTrace();
            this.setCursor(Cursor.getDefaultCursor());
            return false;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgCariPeriksaRadiologi dialog = new DlgCariPeriksaRadiologi(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    /**
     * Buat link publik hasil radiologi untuk baris terpilih, lalu tampilkan
     * sebagai QR code yang bisa dipindai pasien. Link membuka hasil di EPasien
     * TANPA login akun, namun pasien wajib memasukkan tanggal lahir untuk
     * membuka (gate privasi). Token bersifat acak (tidak bisa dipalsukan) dan
     * berlaku 7 hari.
     *
     * Panggil dari actionPerformed sebuah tombol (mis. BtnQRHasil) yang Anda
     * tambahkan lewat NetBeans GUI Designer.
     */
    private void tampilQRHasilRadiologi() {
        if (tbDokter.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Pilih dulu baris pemeriksaan radiologi.");
            return;
        }
        String urlEpasien = koneksiDB.URLEPASIEN();
        if (urlEpasien == null || urlEpasien.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "URL EPasien belum diatur. Isi entry 'URLEPASIEN' di setting/database.xml.");
            return;
        }
        // kolom: 0=No.Rawat, 3=Tgl.Periksa, 4=Jam Periksa
        String noRawat = tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString();
        String tglPrx  = tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString();
        String jamPrx  = tbDokter.getValueAt(tbDokter.getSelectedRow(), 4).toString();

        // token acak 32 hex (128-bit) — tidak bisa ditebak/dipalsukan
        byte[] rnd = new byte[16];
        new java.security.SecureRandom().nextBytes(rnd);
        StringBuilder sb = new StringBuilder();
        for (byte b : rnd) sb.append(String.format("%02x", b));
        String token = sb.toString();

        // simpan token -> pemeriksaan, berlaku 7 hari
        Sequel.queryu2(
            "insert into link_radiologi_publik(token,no_rawat,tgl_periksa,jam,expired) "
          + "values(?,?,?,?,date_add(now(),interval 7 day))",
            4, new String[]{token, noRawat, tglPrx, jamPrx});

        String link = urlEpasien.replaceAll("/+$", "") + "/lihatradiologi.php?t=" + token;

        // Identitas + "password" (tanggal lahir) untuk diinfokan tim radiologi ke pasien
        String namaPasien = Sequel.cariIsi("select p.nm_pasien from reg_periksa r "
            + "inner join pasien p on p.no_rkm_medis=r.no_rkm_medis where r.no_rawat='" + noRawat + "'");
        String tglLahir = Sequel.cariIsi("select date_format(p.tgl_lahir,'%d%m%Y') from reg_periksa r "
            + "inner join pasien p on p.no_rkm_medis=r.no_rkm_medis where r.no_rawat='" + noRawat + "'");
        if (namaPasien == null) namaPasien = "";
        if (tglLahir == null) tglLahir = "";

        // Buat PDF hasil radiologi (dengan gambar) — jasper yang sama dengan WA —
        // lalu enkripsi (password = tanggal lahir) dan upload ke server webapps
        // sebagai <token>.pdf. Saat QR di-scan, EPasien akan langsung menyajikan
        // PDF ini untuk diunduh.
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        boolean pdfSiap = false;
        try {
            String pdfPassword = tglLahir.isEmpty() ? "12345678" : tglLahir;
            String namaFilePdf = "QRRad-" + token;
            CreatePDFGambar(namaFilePdf); // -> tmpPDF/QRRad-<token>.pdf (rptPeriksaRadiologiGambar)
            java.io.File pdfAsli = new java.io.File("tmpPDF/" + namaFilePdf + ".pdf");
            if (pdfAsli.exists()) {
                String encPath = encryptPDF(pdfAsli.getAbsolutePath(), pdfPassword);
                java.io.File pdfEnc = new java.io.File(encPath);
                if (pdfEnc.exists()) {
                    pdfSiap = uploadPdfHasilRadiologi(pdfEnc, token);
                }
            }
        } catch (Exception e) {
            System.out.println("tampilQRHasilRadiologi buat/upload PDF: " + e);
        }
        this.setCursor(Cursor.getDefaultCursor());
        if (!pdfSiap) {
            JOptionPane.showMessageDialog(null,
                "Peringatan: PDF hasil gagal disiapkan/diupload.\n"
              + "QR tetap dibuat, tetapi pasien belum bisa mengunduh hasil sampai PDF berhasil dibuat.\n"
              + "Pastikan hasil sudah final dan koneksi ke server webapps tersedia, lalu ulangi.");
        }

        try {
            java.util.Map<com.google.zxing.EncodeHintType, Object> hint = new java.util.HashMap<>();
            hint.put(com.google.zxing.EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.M);
            hint.put(com.google.zxing.EncodeHintType.MARGIN, 1);
            com.google.zxing.common.BitMatrix matrix =
                new com.google.zxing.qrcode.QRCodeWriter().encode(link, com.google.zxing.BarcodeFormat.QR_CODE, 320, 320, hint);
            java.awt.image.BufferedImage qr = com.google.zxing.client.j2se.MatrixToImageWriter.toBufferedImage(matrix);

            javax.swing.JLabel lblQr = new javax.swing.JLabel(new javax.swing.ImageIcon(qr));
            javax.swing.JTextField txtUrl = new javax.swing.JTextField(link);
            txtUrl.setEditable(false);
            javax.swing.JButton btnSalin = new javax.swing.JButton("Salin URL");
            btnSalin.addActionListener(e -> {
                java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
                    .setContents(new java.awt.datatransfer.StringSelection(link), null);
                JOptionPane.showMessageDialog(null, "URL disalin ke clipboard.");
            });

            javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout(8, 8));
            javax.swing.JLabel info = new javax.swing.JLabel(
                "<html><center>Scan QR ini dengan HP pasien untuk <b>mengunduh PDF hasil</b>.<br/>"
              + "PDF terkunci — dibuka dengan <b>tanggal lahir</b> (ddmmyyyy).<br/>"
              + "Link berlaku 7 hari.</center></html>", javax.swing.SwingConstants.CENTER);
            panel.add(info, java.awt.BorderLayout.NORTH);
            panel.add(lblQr, java.awt.BorderLayout.CENTER);

            // di bawah QR: identitas + password (tanggal lahir) utk diinfokan ke pasien
            javax.swing.JPanel bawah = new javax.swing.JPanel();
            bawah.setLayout(new javax.swing.BoxLayout(bawah, javax.swing.BoxLayout.Y_AXIS));
            javax.swing.JLabel lblKredensial = new javax.swing.JLabel(
                "<html><div style='text-align:center;'>"
              + "Pasien: <b>" + namaPasien + "</b><br/>"
              + "Kata sandi buka (tanggal lahir): <b style='font-size:14px;'>" + tglLahir + "</b>"
              + "</div></html>", javax.swing.SwingConstants.CENTER);
            lblKredensial.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
            lblKredensial.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 0, 6, 0));
            javax.swing.JPanel barisUrl = new javax.swing.JPanel(new java.awt.BorderLayout(4, 4));
            barisUrl.add(txtUrl, java.awt.BorderLayout.CENTER);
            barisUrl.add(btnSalin, java.awt.BorderLayout.EAST);
            bawah.add(lblKredensial);
            bawah.add(barisUrl);
            panel.add(bawah, java.awt.BorderLayout.SOUTH);

            JOptionPane.showMessageDialog(null, panel, "QR Hasil Radiologi", JOptionPane.PLAIN_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Gagal membuat QR: " + e.getMessage());
        }
    }

    /**
     * Upload PDF hasil radiologi (sudah terenkripsi) ke server webapps sebagai
     * &lt;token&gt;.pdf, dipakai oleh EPasien saat QR di-scan. Auth memakai
     * PASHYBRIDWEB yang sudah ada di setting/database.xml (nol config baru).
     */
    private boolean uploadPdfHasilRadiologi(java.io.File pdf, String token) {
        try {
            String url = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB()
                       + "/" + koneksiDB.HYBRIDWEB() + "/hasilradiologipublik/unggah.php";
            try ( CloseableHttpClient http = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(url);
                MultipartEntityBuilder b = MultipartEntityBuilder.create();
                b.setCharset(StandardCharsets.UTF_8);
                b.setMode(HttpMultipartMode.EXTENDED);
                b.addTextBody("key", koneksiDB.PASHYBRIDWEB(), ContentType.create("text/plain", StandardCharsets.UTF_8));
                b.addTextBody("token", token, ContentType.create("text/plain", StandardCharsets.UTF_8));
                b.addBinaryBody("file", pdf, ContentType.APPLICATION_PDF, token + ".pdf");
                post.setEntity(b.build());
                try ( CloseableHttpResponse resp = http.execute(post)) {
                    String body = EntityUtils.toString(resp.getEntity());
                    System.out.println("uploadPdfHasilRadiologi resp: " + resp.getCode() + " " + body);
                    return resp.getCode() == 200 && body.trim().startsWith("OK");
                }
            }
        } catch (Exception e) {
            System.out.println("uploadPdfHasilRadiologi: " + e);
            return false;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.TextBox Alamat;
    private widget.Button BtnAll;
    private widget.Button BtnCari;
    private widget.Button BtnCloseIn4;
    private widget.Button BtnClosePhrase;
    private widget.Button BtnConvertKirimOrthanc;
    private widget.Button BtnConvertKirimOrthancBatch;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnPrint1;
    private widget.Button BtnRefreshPhoto;
    private widget.Button BtnSimpan;
    private widget.Button BtnSimpan4;
    private widget.Button BtnSimpanTandaTangan;
    private widget.CekBox ChkAccor;
    private widget.PanelBiasa FormHasilRadiologi;
    private widget.panelisi FormInput;
    private widget.PanelBiasa FormOrthan;
    private widget.PanelBiasa FormPass2;
    private widget.PanelBiasa FormPhoto;
    private widget.TextArea HasilPeriksa;
    private widget.Label JamDicari;
    private widget.TextBox Jk;
    private widget.TextBox Kd2;
    private widget.TextBox KdPtgUbah;
    private widget.TextBox KodePerujuk;
    private widget.TextBox KodePj;
    private widget.editorpane LoadHTML;
    private javax.swing.JMenuItem MnCetakNota;
    private javax.swing.JMenuItem MnTte;
    private javax.swing.JMenuItem MnUbahDokterPetugas;
    private widget.TextBox NmDokterPj;
    private widget.TextBox NmPerujuk;
    private widget.TextBox NmPtgUbah;
    private widget.TextBox NoRM;
    private widget.TextBox NoRawat;
    private widget.Label NoRawatDicari;
    private javax.swing.JMenuItem PDFExpertiseGambarRadiologi;
    private widget.PanelBiasa PanelAccor;
    private widget.panelisi PanelDataDicari;
    private widget.Label PasienDicari;
    private widget.TextBox Penjab;
    private widget.TextBox Petugas;
    private widget.TextBox Phrase;
    private widget.ScrollPane Scroll3;
    private widget.ScrollPane Scroll4;
    private widget.ScrollPane Scroll5;
    private widget.TextBox TCari;
    private javax.swing.JTabbedPane TabData;
    private javax.swing.JMenuItem TagSignerDokter;
    private javax.swing.JMenuItem TagSignerDokterBatch;
    private widget.Tanggal Tgl1;
    private widget.Tanggal Tgl2;
    private widget.Label TglDicari;
    private widget.TextBox Umur;
    private javax.swing.JMenuItem UploadPDF;
    private javax.swing.JDialog WindowGantiDokterParamedis;
    private javax.swing.JDialog WindowPhrase;
    private widget.Button btnAmbilPhoto;
    private widget.Button btnAmbilPhoto1;
    private widget.Button btnDicom;
    private widget.Button btnDokter;
    private widget.Button btnDokterPj;
    private widget.Button btnKirimWa;
    private widget.Button btnKirimWaDokterPJ;
    private widget.Button btnKirimWaDokterPengirim;
    private widget.Button btnPasien;
    private widget.Button btnPetugas;
    private widget.Button btnPetugas1;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame5;
    private widget.InternalFrame internalFrame8;
    private widget.Label jLabel12;
    private widget.Label jLabel45;
    private widget.Label jLabel46;
    private widget.Label jLabel7;
    private widget.Label jLabel9;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.TextBox kdmem;
    private widget.TextBox kdptg;
    private widget.Label label10;
    private widget.Label label11;
    private widget.Label label13;
    private widget.Label label15;
    private widget.Label label16;
    private widget.Label label17;
    private widget.Label label18;
    private widget.Label label19;
    private widget.Label label20;
    private widget.Label label9;
    private widget.TextBox nmmem;
    private widget.TextBox nmptg;
    private widget.panelisi panelGlass6;
    private widget.panelisi panelGlass7;
    private widget.panelisi panelisi1;
    private widget.panelisi panelisi3;
    private widget.panelisi panelisi5;
    private javax.swing.JMenuItem ppBerkasDigital;
    private javax.swing.JMenuItem ppDicomImage;
    private javax.swing.JMenuItem ppRiwayat;
    private widget.ScrollPane scrollPane1;
    private widget.Table tbDokter;
    private widget.Table tbListDicom;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        try {
            Valid.tabelKosong(tabMode);
            if (NoRawat.getText().equals("") && kdmem.getText().equals("") && kdptg.getText().equals("") && TCari.getText().equals("")) {
                ps = koneksi.prepareStatement(
                        "select periksa_radiologi.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,petugas.nama,periksa_radiologi.tgl_periksa,"
                        + "periksa_radiologi.jam,periksa_radiologi.dokter_perujuk,periksa_radiologi.kd_dokter,penjab.png_jawab,dokter.nm_dokter,pasien.no_tlp,"
                        + "ifnull(wa_radiologi_status.status_kirim,'') as status_wa,"
                        + "(select group_concat(distinct coalesce(mrm2.modality_code,'OT')) from periksa_radiologi pr2 "
                        + "left join mapping_radiologi_modality mrm2 on mrm2.kd_jenis_prw=pr2.kd_jenis_prw "
                        + "where pr2.no_rawat=periksa_radiologi.no_rawat and pr2.tgl_periksa=periksa_radiologi.tgl_periksa and pr2.jam=periksa_radiologi.jam) as modality_list,"
                        + "(select pr3.noorder from permintaan_radiologi pr3 "
                        + "where pr3.no_rawat=periksa_radiologi.no_rawat and pr3.tgl_permintaan<=periksa_radiologi.tgl_periksa "
                        + "order by pr3.tgl_permintaan desc, pr3.jam_permintaan desc limit 1) as no_order "
                        + "from periksa_radiologi inner join reg_periksa on periksa_radiologi.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "inner join petugas on periksa_radiologi.nip=petugas.nip "
                        + "inner join penjab on reg_periksa.kd_pj=penjab.kd_pj "
                        + "inner join dokter on periksa_radiologi.kd_dokter=dokter.kd_dokter "
                        + "left join wa_radiologi_status on periksa_radiologi.no_rawat=wa_radiologi_status.no_rawat "
                        + "and periksa_radiologi.tgl_periksa=wa_radiologi_status.tgl_periksa "
                        + "and periksa_radiologi.jam=wa_radiologi_status.jam where "
                        + "periksa_radiologi.tgl_periksa between ? and ? group by concat(periksa_radiologi.no_rawat,periksa_radiologi.tgl_periksa,periksa_radiologi.jam) "
                        + "order by periksa_radiologi.tgl_periksa desc,periksa_radiologi.jam desc");
            } else {
                ps = koneksi.prepareStatement(
                        "select periksa_radiologi.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,petugas.nama,periksa_radiologi.tgl_periksa,"
                        + "periksa_radiologi.jam,periksa_radiologi.dokter_perujuk,periksa_radiologi.kd_dokter,penjab.png_jawab,dokter.nm_dokter,pasien.no_tlp,"
                        + "ifnull(wa_radiologi_status.status_kirim,'') as status_wa,"
                        + "(select group_concat(distinct coalesce(mrm2.modality_code,'OT')) from periksa_radiologi pr2 "
                        + "left join mapping_radiologi_modality mrm2 on mrm2.kd_jenis_prw=pr2.kd_jenis_prw "
                        + "where pr2.no_rawat=periksa_radiologi.no_rawat and pr2.tgl_periksa=periksa_radiologi.tgl_periksa and pr2.jam=periksa_radiologi.jam) as modality_list,"
                        + "(select pr3.noorder from permintaan_radiologi pr3 "
                        + "where pr3.no_rawat=periksa_radiologi.no_rawat and pr3.tgl_permintaan<=periksa_radiologi.tgl_periksa "
                        + "order by pr3.tgl_permintaan desc, pr3.jam_permintaan desc limit 1) as no_order "
                        + "from periksa_radiologi inner join reg_periksa on periksa_radiologi.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "inner join petugas on periksa_radiologi.nip=petugas.nip "
                        + "inner join penjab on reg_periksa.kd_pj=penjab.kd_pj "
                        + "inner join dokter on periksa_radiologi.kd_dokter=dokter.kd_dokter "
                        + "left join wa_radiologi_status on periksa_radiologi.no_rawat=wa_radiologi_status.no_rawat "
                        + "and periksa_radiologi.tgl_periksa=wa_radiologi_status.tgl_periksa "
                        + "and periksa_radiologi.jam=wa_radiologi_status.jam where "
                        + "periksa_radiologi.tgl_periksa between ? and ? and periksa_radiologi.no_rawat like ? and reg_periksa.no_rkm_medis like ? "
                        + "and petugas.nip like ? and (pasien.nm_pasien like ? or petugas.nama like ? or reg_periksa.no_rkm_medis like ? or penjab.png_jawab like ? ) "
                        + "group by concat(periksa_radiologi.no_rawat,periksa_radiologi.tgl_periksa,periksa_radiologi.jam) "
                        + "order by periksa_radiologi.tgl_periksa desc,periksa_radiologi.jam desc");
            }

            try {
                if (NoRawat.getText().equals("") && kdmem.getText().equals("") && kdptg.getText().equals("") && TCari.getText().equals("")) {
                    ps.setString(1, Valid.SetTgl(Tgl1.getSelectedItem() + ""));
                    ps.setString(2, Valid.SetTgl(Tgl2.getSelectedItem() + ""));
                } else {
                    ps.setString(1, Valid.SetTgl(Tgl1.getSelectedItem() + ""));
                    ps.setString(2, Valid.SetTgl(Tgl2.getSelectedItem() + ""));
                    ps.setString(3, "%" + NoRawat.getText() + "%");
                    ps.setString(4, "%" + kdmem.getText() + "%");
                    ps.setString(5, "%" + kdptg.getText() + "%");
                    ps.setString(6, "%" + TCari.getText().trim() + "%");
                    ps.setString(7, "%" + TCari.getText().trim() + "%");
                    ps.setString(8, "%" + TCari.getText().trim() + "%");
                    ps.setString(9, "%" + TCari.getText().trim() + "%");
                }

                rs = ps.executeQuery();
                ttl = 0;
                while (rs.next()) {
                    kamar = Sequel.cariIsi("select ifnull(kamar_inap.kd_kamar,'') from kamar_inap where kamar_inap.no_rawat='" + rs.getString("no_rawat") + "' order by kamar_inap.tgl_masuk desc limit 1");
                    if (!kamar.equals("")) {
                        namakamar = kamar + ", " + Sequel.cariIsi("select bangsal.nm_bangsal from bangsal inner join kamar on bangsal.kd_bangsal=kamar.kd_bangsal "
                                + " where kamar.kd_kamar='" + kamar + "' ");
                        kamar = "Kamar";
                    } else if (kamar.equals("")) {
                        kamar = "Poli";
                        namakamar = Sequel.cariIsi("select poliklinik.nm_poli from poliklinik inner join reg_periksa on poliklinik.kd_poli=reg_periksa.kd_poli "
                                + "where reg_periksa.no_rawat='" + rs.getString("no_rawat") + "'");
                    }
                    tabMode.addRow(new Object[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis") + " " + rs.getString("nm_pasien") + " (" + kamar + " : " + namakamar + ")", rs.getString("nama"),
                        rs.getString("tgl_periksa"), rs.getString("jam"), dokter.tampil3(rs.getString("dokter_perujuk")), rs.getString("nm_dokter"), rs.getString("no_tlp"), rs.getString("status_wa"), rs.getString("modality_list"), rs.getString("no_order") == null ? "" : rs.getString("no_order")
                    });
                    tabMode.addRow(new Object[]{"", "Proyeksi & Dosis Radiasi", "Kode Periksa", "Nama Pemeriksaan", "Biaya", "Cara Bayar : " + rs.getString("png_jawab"), "", "", "", "", ""});
                    ps2 = koneksi.prepareStatement(
                            "select jns_perawatan_radiologi.kd_jenis_prw,jns_perawatan_radiologi.nm_perawatan,periksa_radiologi.biaya," + "concat("
                            + "if(periksa_radiologi.proyeksi<>'',concat('Proyeksi : ',periksa_radiologi.proyeksi,', '),''),"
                            + "if(periksa_radiologi.kV<>'',concat('kV : ',periksa_radiologi.kV,', '),''),"
                            + "if(periksa_radiologi.mAS<>'',concat('mAS : ',periksa_radiologi.mAS,', '),''),"
                            + "if(periksa_radiologi.FFD<>'',concat('FFD : ',periksa_radiologi.FFD,', '),''),"
                            + "if(periksa_radiologi.BSF<>'',concat('BSF : ',periksa_radiologi.BSF,', '),''),"
                            + "if(periksa_radiologi.inak<>'',concat('Inak : ',periksa_radiologi.inak,', '),''),"
                            + "if(periksa_radiologi.jml_penyinaran<>'',concat('Jml Penyinaran : ',periksa_radiologi.jml_penyinaran,', '),''),"
                            + "if(periksa_radiologi.dosis<>'',concat('Dosis Radiasi : ',periksa_radiologi.dosis),'')) as proyeksi from periksa_radiologi "
                            + "inner join jns_perawatan_radiologi on periksa_radiologi.kd_jenis_prw=jns_perawatan_radiologi.kd_jenis_prw where periksa_radiologi.no_rawat=? and periksa_radiologi.tgl_periksa=? "
                            + "and periksa_radiologi.jam=?");
                    try {
                        ps2.setString(1, rs.getString("no_rawat"));
                        ps2.setString(2, rs.getString("tgl_periksa"));
                        ps2.setString(3, rs.getString("jam"));
                        rs2 = ps2.executeQuery();
                        while (rs2.next()) {
                            ttl = ttl + rs2.getDouble("biaya");
                            tabMode.addRow(new Object[]{"", rs2.getString("proyeksi"), rs2.getString("kd_jenis_prw"), rs2.getString("nm_perawatan"), Valid.SetAngka(rs2.getDouble("biaya")), "", "", "", "", "", ""});
                        }
                    } catch (Exception e) {
                        System.out.println("simrskhanza.DlgCariPeriksaRadiologi.tampil() ps2 : " + e);
                    } finally {
                        if (rs2 != null) {
                            rs2.close();
                        }
                        if (ps2 != null) {
                            ps2.close();
                        }
                    }

                    ps3 = koneksi.prepareStatement(
                            "select beri_bhp_radiologi.kode_brng,ipsrsbarang.nama_brng,beri_bhp_radiologi.kode_sat,beri_bhp_radiologi.jumlah, "
                            + "beri_bhp_radiologi.total from beri_bhp_radiologi inner join ipsrsbarang on ipsrsbarang.kode_brng=beri_bhp_radiologi.kode_brng "
                            + "where beri_bhp_radiologi.no_rawat=? and beri_bhp_radiologi.tgl_periksa=? and beri_bhp_radiologi.jam=?");
                    try {
                        ps3.setString(1, rs.getString("no_rawat"));
                        ps3.setString(2, rs.getString("tgl_periksa"));
                        ps3.setString(3, rs.getString("jam"));
                        rs3 = ps3.executeQuery();
                        rs3.last();
                        if (rs3.getRow() > 0) {
                            tabMode.addRow(new Object[]{"", "", "Kode BHP", "Nama BHP", "Satuan", "Jumlah", "", "", "", "", ""});
                            rs3.beforeFirst();
                            while (rs3.next()) {
                                tabMode.addRow(new Object[]{"", "", rs3.getString("kode_brng"), rs3.getString("nama_brng"), rs3.getString("kode_sat"), rs3.getString("jumlah"), "", "", "", "", ""});
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Notif ps3 : " + e);
                    } finally {
                        if (rs3 != null) {
                            rs3.close();
                        }
                        if (ps3 != null) {
                            ps3.close();
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("simrskhanza.DlgCariPeriksaRadiologi.tampil() PS : " + e);
            } finally {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            }
            if (ttl > 0) {
                tabMode.addRow(new Object[]{">>", "Total : " + Valid.SetAngka(ttl), "", "", "", "", "", "", "", "", ""});
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

    public void SetNoRw(String norw) {
        NoRawat.setText(norw);
        tampil();
        Sequel.cariIsi("select reg_periksa.tgl_registrasi from reg_periksa where reg_periksa.no_rawat='" + norw + "'", Tgl1);
    }

    private void getData() {
        Kd2.setText("");
        if (tbDokter.getSelectedRow() != -1) {
            Kd2.setText(tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString());
            Petugas.setText(tbDokter.getValueAt(tbDokter.getSelectedRow(), 6).toString());
        }
    }

    public void isCek() {
        MnCetakNota.setEnabled(akses.getperiksa_radiologi());
        BtnHapus.setEnabled(akses.getperiksa_radiologi());
        MnUbahDokterPetugas.setEnabled(akses.getperiksa_radiologi());
        btnAmbilPhoto.setEnabled(akses.getperiksa_radiologi());
        BtnSimpan.setEnabled(akses.getperiksa_radiologi());
        BtnPrint.setEnabled(akses.getperiksa_radiologi());
        ppRiwayat.setEnabled(akses.getresume_pasien());
        ppBerkasDigital.setEnabled(akses.getberkas_digital_perawatan());
    }

    public void setPasien(String pasien) {
        NoRawat.setText(pasien);
    }

    private void panggilMedis() {
        try {
            ps5 = koneksi.prepareStatement(
                    "select periksa_radiologi.nip,petugas.nama,periksa_radiologi.dokter_perujuk,"
                    + "periksa_radiologi.kd_dokter,dokter.nm_dokter from periksa_radiologi "
                    + "inner join petugas on periksa_radiologi.nip=petugas.nip "
                    + "inner join dokter on periksa_radiologi.kd_dokter=dokter.kd_dokter "
                    + "where periksa_radiologi.no_rawat=? "
                    + "and periksa_radiologi.tgl_periksa=? and periksa_radiologi.jam=?");
            try {
                ps5.setString(1, tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString());
                ps5.setString(2, tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString());
                ps5.setString(3, tbDokter.getValueAt(tbDokter.getSelectedRow(), 4).toString());
                rs5 = ps5.executeQuery();
                if (rs5.next()) {
                    KodePerujuk.setText(rs5.getString("dokter_perujuk"));
                    NmPerujuk.setText(dokter.tampil3(rs5.getString("dokter_perujuk")));
                    KodePj.setText(rs5.getString("kd_dokter"));
                    NmDokterPj.setText(rs5.getString("nm_dokter"));
                    KdPtgUbah.setText(rs5.getString("nip"));
                    NmPtgUbah.setText(rs5.getString("nama"));
                    WindowGantiDokterParamedis.setSize(600, 130);
                    WindowGantiDokterParamedis.setLocationRelativeTo(internalFrame1);
                    WindowGantiDokterParamedis.setVisible(true);
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rs5 != null) {
                    rs5.close();
                }
                if (ps5 != null) {
                    ps5.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }
    }

    private void isPhoto() {
        if (ChkAccor.isSelected() == true) {
            PanelDataDicari.setVisible(true);
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(internalFrame1.getWidth() - 300, HEIGHT));
            TabData.setVisible(true);
            ChkAccor.setVisible(true);
        } else if (ChkAccor.isSelected() == false) {
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(15, HEIGHT));
            TabData.setVisible(false);
            PanelDataDicari.setVisible(false);
            ChkAccor.setVisible(true);
        }
    }

    private void panggilPhoto() {
        if (TabData.isVisible() == true) {
            if (tbDokter.getSelectedRow() != -1) {
                if ((!Kd2.getText().equals("")) && (!Petugas.getText().equals(""))) {
                    NoRawatDicari.setText(tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString());
                    PasienDicari.setText(tbDokter.getValueAt(tbDokter.getSelectedRow(), 1).toString());
                    TglDicari.setText(tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString());
                    JamDicari.setText(tbDokter.getValueAt(tbDokter.getSelectedRow(), 4).toString());
                    try {
                        ps = koneksi.prepareStatement("select gambar_radiologi.lokasi_gambar from gambar_radiologi where gambar_radiologi.no_rawat=? and gambar_radiologi.tgl_periksa=? and gambar_radiologi.jam=? ");
                        try {
                            ps.setString(1, tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString());
                            ps.setString(2, tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString());
                            ps.setString(3, tbDokter.getValueAt(tbDokter.getSelectedRow(), 4).toString());
                            rs = ps.executeQuery();
                            htmlContent = new StringBuilder();
                            while (rs.next()) {
                                htmlContent.append("<tr><td border='0' align='center'><a href='http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/radiologi/" + rs.getString("lokasi_gambar") + "'><img src='http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/radiologi/" + rs.getString("lokasi_gambar") + "' alt='photo' width='" + (internalFrame1.getWidth() - 370) + "' height='" + (internalFrame1.getWidth() - 370) + "'/></a></td></tr>");
                            }
                            LoadHTML.setText(
                                    "<html>"
                                    + "<table width='100%' border='0' align='center' cellpadding='1px' cellspacing='1px' class='tbl_form'>"
                                    + htmlContent.toString()
                                    + "</table>"
                                    + "</html>"
                            );
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

                        ps5 = koneksi.prepareStatement("select hasil_radiologi.hasil from hasil_radiologi where hasil_radiologi.no_rawat=? and hasil_radiologi.tgl_periksa=? and hasil_radiologi.jam=?");
                        try {
                            ps5.setString(1, tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString());
                            ps5.setString(2, tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString());
                            ps5.setString(3, tbDokter.getValueAt(tbDokter.getSelectedRow(), 4).toString());
                            rs5 = ps5.executeQuery();
                            if (rs5.next()) {
                                HasilPeriksa.setText(rs5.getString("hasil"));
                            } else {
                                HasilPeriksa.setText("");
                            }
                        } catch (Exception e) {
                            System.out.println("Notif ps5 : " + e);
                        } finally {
                            if (rs5 != null) {
                                rs5.close();
                            }
                            if (ps5 != null) {
                                ps5.close();
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Notif : " + e);
                    }
                } else {
                    NoRawatDicari.setText("");
                    PasienDicari.setText("");
                    TglDicari.setText("");
                    JamDicari.setText("");
                    LoadHTML.setText("<html><body><center><br><br><font face='tahoma' size='2' color='#434343'></font></center></body></html>");
                    HasilPeriksa.setText("");
                }
            }
        }
    }

    private void tampilOrthanc() {
        if (TabData.isVisible() == true) {
            if (tbDokter.getSelectedRow() != -1) {
                if ((!Kd2.getText().equals("")) && (!Petugas.getText().equals(""))) {
                    if (TabData.getSelectedIndex() == 2) {
                        try {
                            Valid.tabelKosong(tabModeDicom);
                            ApiOrthanc orthanc = new ApiOrthanc();
                            root = orthanc.AmbilSeries(Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat=?", tbDokter.getValueAt(tbDokter.getSelectedRow(), 0).toString()), Valid.SetTgl(Tgl1.getSelectedItem() + "").replaceAll("-", ""), Valid.SetTgl(Tgl2.getSelectedItem() + "").replaceAll("-", ""));
                            for (JsonNode list : root) {
                                for (JsonNode sublist : list.path("Series")) {
                                    tabModeDicom.addRow(new Object[]{
                                        list.path("PatientMainDicomTags").path("PatientID").asText(), list.path("ID").asText(), sublist.asText()
                                    });
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Notif : " + e);
                        }
                    }
                }
            }
        }
    }

    private void CreatePDF(String FileName) {
        if (Kd2.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu...!!!!");
        } else {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            pemeriksaan = "";
            try {
                ps2 = koneksi.prepareStatement(
                        "select jns_perawatan_radiologi.kd_jenis_prw,jns_perawatan_radiologi.nm_perawatan,periksa_radiologi.biaya,"
                        + "periksa_radiologi.kd_dokter,periksa_radiologi.nip,periksa_radiologi.proyeksi,periksa_radiologi.kV,periksa_radiologi.mAS,periksa_radiologi.FFD,"
                        + "periksa_radiologi.BSF,periksa_radiologi.inak,periksa_radiologi.jml_penyinaran,periksa_radiologi.dosis from periksa_radiologi inner join jns_perawatan_radiologi "
                        + "on periksa_radiologi.kd_jenis_prw=jns_perawatan_radiologi.kd_jenis_prw where periksa_radiologi.no_rawat=? and periksa_radiologi.tgl_periksa=? "
                        + "and periksa_radiologi.jam=?");
                try {
                    ps2.setString(1, NoRawatDicari.getText());
                    ps2.setString(2, TglDicari.getText());
                    ps2.setString(3, JamDicari.getText());
                    rs2 = ps2.executeQuery();
                    while (rs2.next()) {
                        pemeriksaan = rs2.getString("nm_perawatan") + ", " + pemeriksaan;
                        kdpenjab = rs2.getString("kd_dokter");
                        kdpetugas = rs2.getString("nip");
                    }
                } catch (Exception e) {
                    System.out.println("simrskhanza.DlgCariPeriksaRadiologi.BtnPrint1ActionPerformed() ps2 : " + e);
                } finally {
                    if (rs2 != null) {
                        rs2.close();
                    }
                    if (ps2 != null) {
                        ps2.close();
                    }
                }

            } catch (Exception e) {
                System.out.println("Notifikasi Pemeriksaan : " + e);
            }
            Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat=? ", NoRM, Kd2.getText());
            Sequel.cariIsi("select pasien.jk from pasien where pasien.no_rkm_medis=? ", Jk, NoRM.getText());
            Sequel.cariIsi("select pasien.umur from pasien where pasien.no_rkm_medis=?", Umur, NoRM.getText());
            Sequel.cariIsi("select concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat from pasien inner join kelurahan inner join kecamatan inner join kabupaten on pasien.kd_kel=kelurahan.kd_kel and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab where no_rkm_medis=? ", Alamat, NoRM.getText());

            kamar = Sequel.cariIsi("select ifnull(kamar_inap.kd_kamar,'') from kamar_inap where kamar_inap.no_rawat='" + Kd2.getText() + "' order by kamar_inap.tgl_masuk desc limit 1");
            if (!kamar.equals("")) {
                namakamar = kamar + ", " + Sequel.cariIsi("select bangsal.nm_bangsal from bangsal inner join kamar on bangsal.kd_bangsal=kamar.kd_bangsal "
                        + " where kamar.kd_kamar='" + kamar + "' ");
                kamar = "Kamar";
            } else if (kamar.equals("")) {
                kamar = "Poli";
                namakamar = Sequel.cariIsi("select poliklinik.nm_poli from poliklinik inner join reg_periksa on poliklinik.kd_poli=reg_periksa.kd_poli "
                        + "where reg_periksa.no_rawat='" + Kd2.getText() + "'");
            }
            Map<String, Object> param = new HashMap<>();
            param.put("noperiksa", Kd2.getText());
            param.put("norm", NoRM.getText());
            param.put("namapasien", Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=? ", NoRM.getText()));
            param.put("jkel", Jk.getText());
            param.put("umur", Umur.getText());
            param.put("lahir", Sequel.cariIsi("select DATE_FORMAT(pasien.tgl_lahir,'%d-%m-%Y') from pasien where pasien.no_rkm_medis=? ", NoRM.getText()));
            param.put("pengirim", tbDokter.getValueAt(tbDokter.getSelectedRow(), 5).toString());
            param.put("tanggal", Valid.SetTgl3(tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString()));
            param.put("penjab", tbDokter.getValueAt(tbDokter.getSelectedRow(), 6).toString());
            param.put("petugas", tbDokter.getValueAt(tbDokter.getSelectedRow(), 2).toString());
            param.put("alamat", Alamat.getText());
            param.put("kamar", kamar);
            param.put("namakamar", namakamar);
            param.put("pemeriksaan", pemeriksaan);
            param.put("jam", tbDokter.getValueAt(tbDokter.getSelectedRow(), 4).toString());
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("hasil", HasilPeriksa.getText());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", kdpenjab);
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbDokter.getValueAt(tbDokter.getSelectedRow(), 6).toString() + "\nID " + (finger.equals("") ? kdpenjab : finger) + "\n" + Valid.SetTgl3(tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString()));
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", kdpetugas);
            param.put("finger2", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbDokter.getValueAt(tbDokter.getSelectedRow(), 2).toString() + "\nID " + (finger.equals("") ? kdpetugas : finger) + "\n" + Valid.SetTgl3(tbDokter.getValueAt(tbDokter.getSelectedRow(), 3).toString()));
            Valid.MyReportPDFUpload("rptPeriksaRadiologi.jasper", "report", "::[ Hasil Radiologi ]::", FileName, param);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }

    private void CreatePDFGambar(String FileName) {
        // Validasi: harus ada row terpilih (lebih reliable dari cek Kd2.getText())
        if (tbDokter.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu...!!!!");
            return;
        }

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // ── Single source of truth: ambil identifier LANGSUNG dari row terpilih ──
        // Tidak bergantung pada NoRawatDicari/TglDicari/JamDicari/Kd2 yang bisa
        // stale (mis. saat user pindah row pakai keyboard atau panel TabData
        // tidak visible — listener panggilPhoto tidak jalan).
        int row              = tbDokter.getSelectedRow();
        String noRawatRow    = tbDokter.getValueAt(row, 0).toString();
        String namaPasienRow = tbDokter.getValueAt(row, 1).toString();
        String namaPetugasRow= tbDokter.getValueAt(row, 2).toString();
        String tglPeriksaRow = tbDokter.getValueAt(row, 3).toString();
        String jamPeriksaRow = tbDokter.getValueAt(row, 4).toString();
        String namaPengirimRow = tbDokter.getValueAt(row, 5).toString();
        String namaDokterPjRow = tbDokter.getValueAt(row, 6).toString();

        pemeriksaan = "";
        try {
            ps2 = koneksi.prepareStatement(
                    "select jns_perawatan_radiologi.kd_jenis_prw,jns_perawatan_radiologi.nm_perawatan,periksa_radiologi.biaya,"
                    + "periksa_radiologi.kd_dokter,periksa_radiologi.nip,periksa_radiologi.proyeksi,periksa_radiologi.kV,periksa_radiologi.mAS,periksa_radiologi.FFD,"
                    + "periksa_radiologi.BSF,periksa_radiologi.inak,periksa_radiologi.jml_penyinaran,periksa_radiologi.dosis from periksa_radiologi inner join jns_perawatan_radiologi "
                    + "on periksa_radiologi.kd_jenis_prw=jns_perawatan_radiologi.kd_jenis_prw where periksa_radiologi.no_rawat=? and periksa_radiologi.tgl_periksa=? "
                    + "and periksa_radiologi.jam=?");
            try {
                ps2.setString(1, noRawatRow);
                ps2.setString(2, tglPeriksaRow);
                ps2.setString(3, jamPeriksaRow);
                rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    pemeriksaan = rs2.getString("nm_perawatan") + ", " + pemeriksaan;
                    kdpenjab = rs2.getString("kd_dokter");
                    kdpetugas = rs2.getString("nip");
                }
            } catch (Exception e) {
                System.out.println("simrskhanza.DlgCariPeriksaRadiologi.CreatePDFGambar() ps2 : " + e);
            } finally {
                if (rs2 != null) {
                    rs2.close();
                }
                if (ps2 != null) {
                    ps2.close();
                }
            }

        } catch (Exception e) {
            System.out.println("Notifikasi Pemeriksaan : " + e);
        }

        // Ambil data pasien SEGAR dari DB pakai noRawatRow (bukan dari field UI)
        String noRmRow      = Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat=?", noRawatRow);
        String jkRow        = Sequel.cariIsi("select pasien.jk from pasien where pasien.no_rkm_medis=?", noRmRow);
        String umurRow      = Sequel.cariIsi("select pasien.umur from pasien where pasien.no_rkm_medis=?", noRmRow);
        String alamatRow    = Sequel.cariIsi("select concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat from pasien inner join kelurahan inner join kecamatan inner join kabupaten on pasien.kd_kel=kelurahan.kd_kel and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab where no_rkm_medis=?", noRmRow);
        String lahirRow     = Sequel.cariIsi("select DATE_FORMAT(pasien.tgl_lahir,'%d-%m-%Y') from pasien where pasien.no_rkm_medis=?", noRmRow);
        String namaPasienDb = Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=?", noRmRow);

        // Ambil hasil pemeriksaan SEGAR dari DB (jangan pakai HasilPeriksa.getText()
        // yang bisa stale kalau user pindah row tanpa trigger panggilPhoto)
        String hasilRow = "";
        try {
            PreparedStatement psH = koneksi.prepareStatement(
                "select hasil from hasil_radiologi where no_rawat=? and tgl_periksa=? and jam=? limit 1");
            psH.setString(1, noRawatRow);
            psH.setString(2, tglPeriksaRow);
            psH.setString(3, jamPeriksaRow);
            ResultSet rsH = psH.executeQuery();
            if (rsH.next()) { hasilRow = rsH.getString(1); }
            rsH.close(); psH.close();
        } catch (Exception e) {
            System.out.println("CreatePDFGambar lookup hasil_radiologi: " + e);
        }
        if (hasilRow == null) hasilRow = "";

        // Lookup kamar/poli berdasarkan noRawatRow
        kamar = Sequel.cariIsi("select ifnull(kamar_inap.kd_kamar,'') from kamar_inap where kamar_inap.no_rawat='" + noRawatRow + "' order by kamar_inap.tgl_masuk desc limit 1");
        if (!kamar.equals("")) {
            namakamar = kamar + ", " + Sequel.cariIsi("select bangsal.nm_bangsal from bangsal inner join kamar on bangsal.kd_bangsal=kamar.kd_bangsal "
                    + " where kamar.kd_kamar='" + kamar + "' ");
            kamar = "Kamar";
        } else {
            kamar = "Poli";
            namakamar = Sequel.cariIsi("select poliklinik.nm_poli from poliklinik inner join reg_periksa on poliklinik.kd_poli=reg_periksa.kd_poli "
                    + "where reg_periksa.no_rawat='" + noRawatRow + "'");
        }

        Map<String, Object> param = new HashMap<>();
        param.put("noperiksa", noRawatRow);
        param.put("norm", noRmRow);
        param.put("namapasien", namaPasienDb.isEmpty() ? namaPasienRow : namaPasienDb);
        param.put("jkel", jkRow);
        param.put("umur", umurRow);
        param.put("lahir", lahirRow);
        param.put("pengirim", namaPengirimRow);
        param.put("tanggal", Valid.SetTgl3(tglPeriksaRow));
        param.put("penjab", namaDokterPjRow);
        param.put("petugas", namaPetugasRow);
        param.put("alamat", alamatRow);
        param.put("kamar", kamar);
        param.put("namakamar", namakamar);
        param.put("pemeriksaan", pemeriksaan);
        param.put("jam", jamPeriksaRow);
        param.put("namars", akses.getnamars());
        param.put("alamatrs", akses.getalamatrs());
        param.put("kotars", akses.getkabupatenrs());
        param.put("propinsirs", akses.getpropinsirs());
        param.put("kontakrs", akses.getkontakrs());
        param.put("emailrs", akses.getemailrs());
        param.put("hasil", hasilRow);
        param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", kdpenjab);
        param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + namaDokterPjRow + "\nID " + (finger.equals("") ? kdpenjab : finger) + "\n" + Valid.SetTgl3(tglPeriksaRow));
        finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", kdpetugas);
        param.put("finger2", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + namaPetugasRow + "\nID " + (finger.equals("") ? kdpetugas : finger) + "\n" + Valid.SetTgl3(tglPeriksaRow));

        // Parameter untuk subdataset gambar_radiologi di jasper
        param.put("tgl_periksa", tglPeriksaRow);
        param.put("jam_periksa", jamPeriksaRow);
        param.put("p_url", "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/radiologi/");

        // Sinkronkan field UI agar konsisten dengan data yang baru saja dipakai
        // (kalau-kalau ada code lain di alur kirim WA yang masih baca dari field UI)
        Kd2.setText(noRawatRow);
        NoRM.setText(noRmRow);
        Jk.setText(jkRow);
        Umur.setText(umurRow);
        Alamat.setText(alamatRow);
        NoRawatDicari.setText(noRawatRow);
        TglDicari.setText(tglPeriksaRow);
        JamDicari.setText(jamPeriksaRow);
        HasilPeriksa.setText(hasilRow);

        Valid.MyReportPDFUpload("rptPeriksaRadiologiGambar.jasper", "report", "::[ Hasil Radiologi + Gambar ]::", FileName, param);
        this.setCursor(Cursor.getDefaultCursor());
    }

    private String encryptPDF(String inputPath, String password) {
        String outputPath = inputPath.replace(".pdf", "_encrypted.pdf");
        try {
            PdfReader reader = new PdfReader(inputPath);
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputPath));
            stamper.setEncryption(password.getBytes(), password.getBytes(),
                    PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_COPY,
                    PdfWriter.ENCRYPTION_AES_128);
            stamper.close();
            reader.close();
            System.out.println("PDF encrypted successfully: " + outputPath);
            return outputPath;
        } catch (Exception e) {
            System.out.println("Error encrypting PDF: " + e.getMessage());
            e.printStackTrace();
            return inputPath; // Return original if encryption fails
        }
    }

    private void UploadPDF(String FileName, String docpath, String noRawat) {
        try {
            File file = new File("tmpPDF/" + FileName + ".pdf");
            byte[] data = FileUtils.readFileToByteArray(file);

            // Check if file exists and has content
            if (!file.exists() || data.length == 0) {
                 System.out.println("File not found or empty: " + file.getAbsolutePath());
                 return;
            }

            // Create HttpClient 5 instance
            try ( CloseableHttpClient httpClient = HttpClients.createDefault()) {
                String uploadUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/unggah2.php?doc=" + docpath;
                System.out.println("Upload URL: " + uploadUrl);
                System.out.println("File size: " + data.length + " bytes");
                
                HttpPost postRequest = new HttpPost(uploadUrl);

                // Create multipart entity using HttpClient 5
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.LEGACY); // Equivalent to BROWSER_COMPATIBLE
                builder.addBinaryBody("file", data, ContentType.DEFAULT_BINARY, FileName + ".pdf");

                HttpEntity multipartEntity = builder.build();
                postRequest.setEntity(multipartEntity);

                // Execute the request
                 String responseString = "";
                try ( CloseableHttpResponse response = httpClient.execute(postRequest)) {
                    int statusCode = response.getCode();
                    responseString = EntityUtils.toString(response.getEntity());
                    System.out.println("HTTP Status Code: " + statusCode);
                    System.out.println("Server Response: " + responseString);
                }

                if (responseString.contains("SUCCESS")) {
                    // Menyimpan ke database
                    boolean uploadSuccess = false;
                    kodeberkas = Sequel.cariIsi("SELECT kode FROM master_berkas_digital WHERE nama LIKE '%Radiologi%'");
                    if (kodeberkas.equals("")) {
                        kodeberkas = "RAD";
                        if (Sequel.cariInteger("select count(kode) from master_berkas_digital where kode='RAD'") == 0) {
                            Sequel.menyimpan("master_berkas_digital", "'RAD','Radiologi'", "Kategori Radiologi");
                        }
                    }
                    
                    if (Sequel.cariInteger("SELECT COUNT(no_rawat) AS jumlah FROM berkas_digital_perawatan WHERE lokasi_file='pages/upload/" + FileName + ".pdf'") > 0) {
                        // Update existing
                        try {
                            ps = koneksi.prepareStatement("UPDATE berkas_digital_perawatan SET no_rawat=?, kode=?, lokasi_file=?, nip=? WHERE lokasi_file=?");
                            try {
                                ps.setString(1, noRawat);
                                ps.setString(2, kodeberkas);
                                ps.setString(3, "pages/upload/" + FileName + ".pdf");
                                ps.setString(4, akses.getkode());
                                ps.setString(5, "pages/upload/" + FileName + ".pdf");
                                ps.executeUpdate();
                                uploadSuccess = true;
                            } catch (Exception e) {
                                System.out.println("Notifikasi Edit : " + e);
                            } finally {
                                if (ps != null) {
                                    ps.close();
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Notifikasi Edit : " + e);
                        }
                    } else {
                        // Insert new
                        try {
                            ps = koneksi.prepareStatement("INSERT INTO berkas_digital_perawatan (no_rawat, kode, lokasi_file, nip) VALUES (?, ?, ?, ?)");
                            try {
                                ps.setString(1, noRawat);
                                ps.setString(2, kodeberkas);
                                ps.setString(3, "pages/upload/" + FileName + ".pdf");
                                ps.setString(4, akses.getkode());
                                ps.executeUpdate();
                                uploadSuccess = true;
                            } catch (Exception e) {
                                System.out.println("Notifikasi Simpan : " + e);
                            } finally {
                                if (ps != null) {
                                    ps.close();
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Notifikasi Simpan : " + e);
                        }
                    }

                    // Menampilkan notifikasi
                    if (uploadSuccess) {
                        // JOptionPane.showMessageDialog(null, "Upload berhasil!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
                        System.out.println("Upload & DB Insert Success for " + noRawat);
                    } else {
                        System.out.println("Upload successful but DB Insert failed for " + noRawat);
                    }
                } else {
                    System.out.println("Upload Failed: " + responseString);
                    JOptionPane.showMessageDialog(null, "Upload gagal! Server merespon: " + responseString, "Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            System.out.println("Upload error: " + e);
            // JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat upload: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void HapusPDF() {
        File file = new File("tmpPDF");
        String[] myFiles;
        if (file.isDirectory()) {
            myFiles = file.list();
            for (int i = 0; i < myFiles.length; i++) {
                File myFile = new File(file, myFiles[i]);
                myFile.delete();
            }
        }
    }

    // Helper methods untuk status description
    private String buildStatusDescription(boolean usedNoRawatAsFallback, boolean usedRegistrationDate, String baseStatus) {
        StringBuilder status = new StringBuilder(baseStatus);

        if (usedNoRawatAsFallback) {
            status.append(" (No.Rawat sebagai SEP)");
        }

        if (usedRegistrationDate) {
            status.append(" (Tgl.Registrasi)");
        }

        return status.toString();
    }

    private String buildDetailedDescription(String no_sep, String tanggal, boolean usedRegistrationDate,
            boolean usedNoRawatAsFallback, boolean uploadResult, String baseMessage) {
        StringBuilder desc = new StringBuilder(baseMessage);
        desc.append(" | No SEP: ").append(no_sep);

        if (usedNoRawatAsFallback) {
            desc.append(" (pengganti dari No.Rawat)");
        }

        desc.append(" | Tanggal: ").append(tanggal);

        if (usedRegistrationDate) {
            desc.append(" (dari registrasi)");
        } else if (usedNoRawatAsFallback) {
            desc.append(" (sistem)");
        } else {
            desc.append(" (dari SEP)");
        }

        desc.append(" | Upload: ").append(uploadResult ? "Berhasil" : "Gagal");

        return desc.toString();
    }

    private int[] kirimDataOrthanc(int row) {
        int successCount = 0;
        int failCount = 0;
        int alreadyCount = 0;

        try {
            // Pakai tabMode (model) bukan tbDokter (view) supaya konsisten kalau tabel ter-sort.
            // Skip diam-diam baris detail/header/total (no_rawat kosong atau diawali ">>").
            Object v0 = tabMode.getValueAt(row, 0);
            if (v0 == null) return new int[]{0, 0};
            String noRawat = v0.toString().trim();
            if (noRawat.isEmpty() || noRawat.startsWith(">>")) {
                return new int[]{0, 0};
            }

            String tglPeriksa = tabMode.getValueAt(row, 3).toString();
            String jamPeriksa = tabMode.getValueAt(row, 4).toString();
            String pasienInfo = tabMode.getValueAt(row, 1).toString();
            String[] parts = pasienInfo.split(" ", 2);
            String noRM = parts[0];
            String nmPasien = parts.length > 1 ? parts[1] : "";

            // TODO: gambar_radiologi tidak menyimpan kd_jenis_prw, jadi multi-modality visit
            // (mis. Thorax + USG di jam sama) hanya bisa di-tag dengan modality pertama dari
            // group_concat. Fix proper butuh ALTER TABLE gambar_radiologi ADD kd_jenis_prw.
            String modality = "OT";
            try {
                Object modalityVal = tabMode.getValueAt(row, 9);
                if (modalityVal != null) {
                    String m = modalityVal.toString().trim();
                    if (!m.isEmpty()) {
                        modality = m.contains(",") ? m.split(",")[0].trim() : m;
                    }
                }
            } catch (Exception ignored) {}

            // Ambil noOrder dari kolom UI (col 10) — sudah di-fetch saat tampil() dgn filter
            // tgl_permintaan<=tgl_periksa, jadi tidak perlu re-query.
            String noOrder = "";
            try {
                Object orderVal = tabMode.getValueAt(row, 10);
                if (orderVal != null) noOrder = orderVal.toString().trim();
            } catch (Exception ignored) {}

            // ACSN format = noOrder tanpa "PR" + kd_jenis_prw (selaras dgn
            // SatuSehatKirimServiceRequestRadiologi identifier acsn). Multi-modality
            // (mis. Thorax + USG di jam sama) ambil kd_jenis_prw pertama via LIMIT 1
            // — sama trade-off dgn modality di atas. Empty ACSN fallback ke noOrder
            // mentah supaya tidak kirim "0" ke Orthanc.
            String kdJenisPrw = "";
            if (!noOrder.isEmpty()) {
                try {
                    String r = Sequel.cariIsi(
                        "select kd_jenis_prw from permintaan_pemeriksaan_radiologi where noorder=? limit 1",
                        noOrder);
                    if (r != null) kdJenisPrw = r.trim();
                } catch (Exception e) {
                    System.out.println("Gagal cari kd_jenis_prw untuk noorder=" + noOrder + ": " + e);
                }
            }
            String acsn = (kdJenisPrw.isEmpty())
                    ? noOrder
                    : (noOrder.replaceAll("PR", "") + kdJenisPrw);

            String tglLahir = Sequel.cariIsi("select tgl_lahir from pasien where no_rkm_medis=?", noRM);
            String jk = Sequel.cariIsi("select jk from pasien where no_rkm_medis=?", noRM);

            ps = koneksi.prepareStatement("select gambar_radiologi.lokasi_gambar from gambar_radiologi where gambar_radiologi.no_rawat=? and gambar_radiologi.tgl_periksa=? and gambar_radiologi.jam=? ");
            ps.setString(1, noRawat);
            ps.setString(2, tglPeriksa);
            ps.setString(3, jamPeriksa);
            rs = ps.executeQuery();

            while (rs.next()) {
                String fullPath = rs.getString("lokasi_gambar");
                String fileName = new java.io.File(fullPath).getName();
                String imageUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/radiologi/" + fullPath;

                // Idempotency: cek di orthanc_upload_log apakah image ini sudah berhasil di-upload sebelumnya
                if (sudahDiUploadKeOrthanc(noRawat, tglPeriksa, jamPeriksa, fullPath)) {
                    System.out.println("Skip (sudah ada di orthanc_upload_log): " + fullPath);
                    alreadyCount++;
                    continue;
                }
                System.out.println("Processing Image: " + imageUrl);

                try {
                    java.net.URL url = new java.net.URL(imageUrl);
                    java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
                    try (java.io.InputStream inputStream = url.openStream()) {
                        int n;
                        byte[] buffer = new byte[1024];
                        while (-1 != (n = inputStream.read(buffer))) {
                            output.write(buffer, 0, n);
                        }
                    }
                    byte[] imageBytes = output.toByteArray();

                    String boundary = "NextGenBoundary" + System.currentTimeMillis();
                    String LINE_FEED = "\r\n";
                    String charset = "UTF-8";

                    java.io.ByteArrayOutputStream requestBody = new java.io.ByteArrayOutputStream();
                    java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.OutputStreamWriter(requestBody, charset), true);

                    addMultipartField(writer, boundary, "patient_id", noRM, LINE_FEED);
                    addMultipartField(writer, boundary, "patient_name", nmPasien, LINE_FEED);
                    addMultipartField(writer, boundary, "accession_number", acsn, LINE_FEED);
                    addMultipartField(writer, boundary, "modality", modality, LINE_FEED);
                    addMultipartField(writer, boundary, "study_date", tglPeriksa, LINE_FEED);
                    addMultipartField(writer, boundary, "study_description", "Radiology Image Import", LINE_FEED);
                    addMultipartField(writer, boundary, "birth_date", tglLahir, LINE_FEED);
                    addMultipartField(writer, boundary, "sex", "L".equalsIgnoreCase(jk) ? "M" : "F", LINE_FEED);

                    writer.append("--" + boundary).append(LINE_FEED);
                    writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"").append(LINE_FEED);
                    writer.append("Content-Type: image/jpeg").append(LINE_FEED);
                    writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
                    writer.append(LINE_FEED);
                    writer.flush();

                    requestBody.write(imageBytes);

                    writer.append(LINE_FEED);
                    writer.flush();

                    writer.append("--" + boundary + "--").append(LINE_FEED);
                    writer.close();

                    byte[] fullBodyBytes = requestBody.toByteArray();

                    String serverUrl = MWL_API_URL + "/api/v1/tools/image-to-dicom";
                    java.net.HttpURLConnection dbConn = (java.net.HttpURLConnection) new java.net.URL(serverUrl).openConnection();
                    dbConn.setUseCaches(false);
                    dbConn.setDoOutput(true);
                    dbConn.setDoInput(true);
                    dbConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                    dbConn.setFixedLengthStreamingMode(fullBodyBytes.length);

                    if (MWL_API_USERNAME != null && !MWL_API_USERNAME.isEmpty()) {
                        String auth = MWL_API_USERNAME + ":" + MWL_API_PASSWORD;
                        byte[] encodedAuth = java.util.Base64.getEncoder().encode(auth.getBytes());
                        dbConn.setRequestProperty("Authorization", "Basic " + new String(encodedAuth));
                    }

                    try (java.io.OutputStream os = dbConn.getOutputStream()) {
                        os.write(fullBodyBytes);
                        os.flush();
                    }

                    int status = dbConn.getResponseCode();
                    if (status == java.net.HttpURLConnection.HTTP_OK) {
                        successCount++;
                        // Log success → idempotency
                        catatUploadOrthanc(noRawat, tglPeriksa, jamPeriksa, fullPath, modality, noOrder);
                        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(dbConn.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }
                        reader.close();
                    } else {
                        failCount++;
                        System.out.println("Failed upload " + fileName + ": Status " + status);
                        if (dbConn.getErrorStream() != null) {
                            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(dbConn.getErrorStream()));
                            String line;
                            while ((line = reader.readLine()) != null) {
                                System.out.println("Error Body: " + line);
                            }
                            reader.close();
                        }
                    }
                    dbConn.disconnect();

                } catch (Exception e) {
                    System.out.println("Error processing image " + fileName + ": " + e);
                    failCount++;
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception ignored) {}
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
        }

        return new int[]{successCount, failCount, alreadyCount};
    }

    /** Cek apakah image di (no_rawat, tgl, jam, lokasi_gambar) sudah pernah di-upload sukses ke Orthanc. */
    private boolean sudahDiUploadKeOrthanc(String noRawat, String tglPeriksa, String jamPeriksa, String lokasiGambar) {
        try (java.sql.PreparedStatement p = koneksi.prepareStatement(
                "select 1 from orthanc_upload_log "
              + "where no_rawat=? and tgl_periksa=? and jam=? and lokasi_gambar=? limit 1")) {
            p.setString(1, noRawat);
            p.setString(2, tglPeriksa);
            p.setString(3, jamPeriksa);
            p.setString(4, lokasiGambar);
            try (java.sql.ResultSet r = p.executeQuery()) {
                return r.next();
            }
        } catch (Exception e) {
            // Tabel belum ada / DB error → jangan block upload, treat as not-uploaded
            System.out.println("orthanc_upload_log check skipped: " + e.getMessage());
            return false;
        }
    }

    /** Catat upload sukses ke orthanc_upload_log (idempotent — INSERT IGNORE biar duplicate PK aman). */
    private void catatUploadOrthanc(String noRawat, String tglPeriksa, String jamPeriksa,
                                     String lokasiGambar, String modality, String accessionNo) {
        try (java.sql.PreparedStatement p = koneksi.prepareStatement(
                "insert ignore into orthanc_upload_log "
              + "(no_rawat,tgl_periksa,jam,lokasi_gambar,modality,accession_no,uploaded_by) "
              + "values (?,?,?,?,?,?,?)")) {
            p.setString(1, noRawat);
            p.setString(2, tglPeriksa);
            p.setString(3, jamPeriksa);
            p.setString(4, lokasiGambar);
            p.setString(5, modality);
            p.setString(6, accessionNo == null ? "" : accessionNo);
            p.setString(7, akses.getkode() == null ? "" : akses.getkode());
            p.executeUpdate();
        } catch (Exception e) {
            System.out.println("orthanc_upload_log insert gagal: " + e.getMessage());
        }
    }

    private void addMultipartField(java.io.PrintWriter writer, String boundary, String name, String value, String LINE_FEED) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value == null ? "" : value).append(LINE_FEED);
        writer.flush();
    }

    private javax.swing.JMenuItem buatMenuSatuSehat(String teks, boolean tebal) {
        javax.swing.JMenuItem mn = new javax.swing.JMenuItem();
        mn.setBackground(new java.awt.Color(255, 255, 254));
        mn.setFont(new java.awt.Font("Tahoma", tebal ? 1 : 0, 11));
        mn.setForeground(new java.awt.Color(50, 50, 50));
        mn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        mn.setText(teks);
        mn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        mn.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        mn.setPreferredSize(new java.awt.Dimension(360, 28));
        return mn;
    }

    private void initMenuKirimSatuSehat() {
        try {
            linkSatuSehat = koneksiDB.URLFHIRSATUSEHAT();
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }

        jPopupMenu1.addSeparator();

        MnKirimEncounter = buatMenuSatuSehat("Kirim Encounter", false);
        MnKirimEncounter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnKirimEncounterActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnKirimEncounter);

        MnKirimEncounterBatch = buatMenuSatuSehat("Kirim Encounter (Batch)", false);
        MnKirimEncounterBatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnKirimEncounterBatchActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnKirimEncounterBatch);

        MnKirimServiceRequestRadiologi = buatMenuSatuSehat("Kirim Service Request Radiologi", false);
        MnKirimServiceRequestRadiologi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnKirimServiceRequestRadiologiActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnKirimServiceRequestRadiologi);

        MnKirimServiceRequestRadiologiBatch = buatMenuSatuSehat("Kirim Service Request Radiologi (Batch)", false);
        MnKirimServiceRequestRadiologiBatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnKirimServiceRequestRadiologiBatchActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnKirimServiceRequestRadiologiBatch);

        MnKirimSemua = buatMenuSatuSehat("Kirim Semua (Encounter + SR + Orthanc)", true);
        MnKirimSemua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnKirimSemuaActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnKirimSemua);

        MnKirimSemuaBatch = buatMenuSatuSehat("Kirim Semua (Encounter + SR + Orthanc) - Batch", true);
        MnKirimSemuaBatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnKirimSemuaBatchActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnKirimSemuaBatch);

        boolean aksesEnc = akses.getsatu_sehat_kirim_encounter();
        boolean aksesSr = akses.getsatu_sehat_kirim_servicerequest_radiologi();
        MnKirimEncounter.setEnabled(aksesEnc);
        MnKirimEncounterBatch.setEnabled(aksesEnc);
        MnKirimServiceRequestRadiologi.setEnabled(aksesSr);
        MnKirimServiceRequestRadiologiBatch.setEnabled(aksesSr);
        MnKirimSemua.setEnabled(aksesEnc && aksesSr);
        MnKirimSemuaBatch.setEnabled(aksesEnc && aksesSr);
    }

    private String noRawatTerpilih() {
        int viewRow = tbDokter.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(null, "Silahkan pilih data pemeriksaan terlebih dahulu.");
            return "";
        }
        int modelRow = tbDokter.convertRowIndexToModel(viewRow);
        Object v0 = tabMode.getValueAt(modelRow, 0);
        String noRawat = v0 == null ? "" : v0.toString().trim();
        if (noRawat.isEmpty() || noRawat.startsWith(">>")) {
            JOptionPane.showMessageDialog(null, "Pilih baris pemeriksaan (yang ada No.Rawat),\nbukan baris detail tindakan / BHP / total.");
            return "";
        }
        return noRawat;
    }

    private Set<String> kumpulkanNoRawat() {
        Set<String> daftar = new LinkedHashSet<>();
        for (int r = 0; r < tabMode.getRowCount(); r++) {
            Object v0 = tabMode.getValueAt(r, 0);
            String noRawat = v0 == null ? "" : v0.toString().trim();
            if (!noRawat.isEmpty() && !noRawat.startsWith(">>")) {
                daftar.add(noRawat);
            }
        }
        return daftar;
    }

    private void MnKirimEncounterActionPerformed(java.awt.event.ActionEvent evt) {
        String noRawat = noRawatTerpilih();
        if (noRawat.isEmpty()) return;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        int hasil = kirimEncounter(noRawat);
        this.setCursor(Cursor.getDefaultCursor());
        if (hasil == 1) {
            JOptionPane.showMessageDialog(null, "Encounter " + noRawat + " berhasil dikirim ke Satu Sehat.");
        } else if (hasil == 0) {
            JOptionPane.showMessageDialog(null, "Encounter " + noRawat + " dilewati.\nKemungkinan sudah terkirim, NIK pasien/dokter belum lengkap,\natau poli belum dipetakan lokasi Satu Sehat.");
        } else {
            JOptionPane.showMessageDialog(null, "Gagal mengirim Encounter " + noRawat + ".\nSilahkan periksa log/console.");
        }
    }

    private void MnKirimEncounterBatchActionPerformed(java.awt.event.ActionEvent evt) {
        Set<String> daftar = kumpulkanNoRawat();
        if (daftar.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tidak ada data pemeriksaan yang ditampilkan.");
            return;
        }
        int reply = JOptionPane.showConfirmDialog(this, "Kirim Encounter untuk " + daftar.size() + " no.rawat ke Satu Sehat?\nProses ini mungkin memerlukan waktu beberapa saat.", "Konfirmasi Kirim Encounter Batch", JOptionPane.YES_NO_OPTION);
        if (reply != JOptionPane.YES_OPTION) return;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        int sukses = 0, lewati = 0, gagal = 0;
        for (String noRawat : daftar) {
            try {
                int h = kirimEncounter(noRawat);
                if (h == 1) sukses++;
                else if (h == 0) lewati++;
                else gagal++;
            } catch (Exception e) {
                gagal++;
                System.out.println("Error no.rawat " + noRawat + " : " + e);
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
        JOptionPane.showMessageDialog(null, "Proses Kirim Encounter Batch Selesai!\n\nBerhasil : " + sukses + "\nDilewati : " + lewati + "\nGagal    : " + gagal);
    }

    private void MnKirimServiceRequestRadiologiActionPerformed(java.awt.event.ActionEvent evt) {
        String noRawat = noRawatTerpilih();
        if (noRawat.isEmpty()) return;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        int[] h = kirimServiceRequestRadiologi(noRawat);
        this.setCursor(Cursor.getDefaultCursor());
        JOptionPane.showMessageDialog(null, "Kirim Service Request Radiologi " + noRawat + " selesai.\n\nBerhasil : " + h[0] + "\nDilewati : " + h[1] + "\nGagal    : " + h[2]);
    }

    private void MnKirimServiceRequestRadiologiBatchActionPerformed(java.awt.event.ActionEvent evt) {
        Set<String> daftar = kumpulkanNoRawat();
        if (daftar.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tidak ada data pemeriksaan yang ditampilkan.");
            return;
        }
        int reply = JOptionPane.showConfirmDialog(this, "Kirim Service Request Radiologi untuk " + daftar.size() + " no.rawat ke Satu Sehat?\nProses ini mungkin memerlukan waktu beberapa saat.", "Konfirmasi Kirim Service Request Radiologi Batch", JOptionPane.YES_NO_OPTION);
        if (reply != JOptionPane.YES_OPTION) return;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        int sukses = 0, lewati = 0, gagal = 0;
        for (String noRawat : daftar) {
            try {
                int[] h = kirimServiceRequestRadiologi(noRawat);
                sukses += h[0];
                lewati += h[1];
                gagal += h[2];
            } catch (Exception e) {
                gagal++;
                System.out.println("Error no.rawat " + noRawat + " : " + e);
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
        JOptionPane.showMessageDialog(null, "Proses Kirim Service Request Radiologi Batch Selesai!\n\nBerhasil : " + sukses + "\nDilewati : " + lewati + "\nGagal    : " + gagal);
    }

    private void MnKirimSemuaActionPerformed(java.awt.event.ActionEvent evt) {
        int viewRow = tbDokter.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(null, "Silahkan pilih data pemeriksaan terlebih dahulu.");
            return;
        }
        int modelRow = tbDokter.convertRowIndexToModel(viewRow);
        Object v0 = tabMode.getValueAt(modelRow, 0);
        String noRawat = v0 == null ? "" : v0.toString().trim();
        if (noRawat.isEmpty() || noRawat.startsWith(">>")) {
            JOptionPane.showMessageDialog(null, "Pilih baris pemeriksaan (yang ada No.Rawat),\nbukan baris detail tindakan / BHP / total.");
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        int hEnc = kirimEncounter(noRawat);
        int[] hSr = kirimServiceRequestRadiologi(noRawat);
        int[] hOrt;
        try {
            hOrt = kirimDataOrthanc(modelRow);
        } catch (Exception e) {
            System.out.println("Error Orthanc " + noRawat + " : " + e);
            hOrt = new int[]{0, 0, 0};
        }
        this.setCursor(Cursor.getDefaultCursor());
        int ortSudah = hOrt.length > 2 ? hOrt[2] : 0;
        JOptionPane.showMessageDialog(null, "Proses 'Kirim Semua' untuk No.Rawat " + noRawat + " selesai.\n\n"
                + "1. Encounter       : " + (hEnc == 1 ? "Berhasil" : (hEnc == 0 ? "Dilewati / sudah terkirim" : "Gagal")) + "\n"
                + "2. Service Request : Berhasil " + hSr[0] + ", Dilewati " + hSr[1] + ", Gagal " + hSr[2] + "\n"
                + "3. Orthanc (Gambar): Terkirim " + hOrt[0] + ", Gagal " + hOrt[1] + ", Sudah ada " + ortSudah);
    }

    private void MnKirimSemuaBatchActionPerformed(java.awt.event.ActionEvent evt) {
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Tidak ada data pemeriksaan yang ditampilkan.");
            return;
        }
        Set<String> daftar = kumpulkanNoRawat();
        int reply = JOptionPane.showConfirmDialog(this, "Jalankan 'Kirim Semua' (Encounter + Service Request + Orthanc)\nuntuk " + daftar.size() + " no.rawat?\nProses ini mungkin memerlukan waktu cukup lama.", "Konfirmasi Kirim Semua Batch", JOptionPane.YES_NO_OPTION);
        if (reply != JOptionPane.YES_OPTION) return;
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        int encOk = 0, encSkip = 0, encFail = 0;
        int srOk = 0, srSkip = 0, srFail = 0;
        int ortOk = 0, ortFail = 0, ortSkip = 0;
        for (String noRawat : daftar) {
            try {
                int hEnc = kirimEncounter(noRawat);
                if (hEnc == 1) encOk++;
                else if (hEnc == 0) encSkip++;
                else encFail++;
                int[] hSr = kirimServiceRequestRadiologi(noRawat);
                srOk += hSr[0];
                srSkip += hSr[1];
                srFail += hSr[2];
            } catch (Exception e) {
                System.out.println("Error no.rawat " + noRawat + " : " + e);
            }
        }
        for (int r = 0; r < tabMode.getRowCount(); r++) {
            Object v0 = tabMode.getValueAt(r, 0);
            String noRawat = v0 == null ? "" : v0.toString().trim();
            if (noRawat.isEmpty() || noRawat.startsWith(">>")) continue;
            try {
                int[] hOrt = kirimDataOrthanc(r);
                ortOk += hOrt[0];
                ortFail += hOrt[1];
                if (hOrt.length > 2) ortSkip += hOrt[2];
            } catch (Exception e) {
                System.out.println("Error baris " + r + " Orthanc : " + e);
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
        JOptionPane.showMessageDialog(null, "Proses 'Kirim Semua' Batch Selesai!\n\n"
                + "1. ENCOUNTER (" + daftar.size() + " no.rawat)\n"
                + "   Berhasil " + encOk + ", Dilewati " + encSkip + ", Gagal " + encFail + "\n\n"
                + "2. SERVICE REQUEST RADIOLOGI\n"
                + "   Berhasil " + srOk + ", Dilewati " + srSkip + ", Gagal " + srFail + "\n\n"
                + "3. ORTHANC (Gambar)\n"
                + "   Terkirim " + ortOk + ", Gagal " + ortFail + ", Sudah ada " + ortSkip);
    }

    private int kirimEncounter(String noRawat) {
        try (PreparedStatement psEnc = koneksi.prepareStatement(
                "select reg_periksa.tgl_registrasi,reg_periksa.jam_reg,pasien.nm_pasien,pasien.no_ktp,"
                + "pegawai.nama,pegawai.no_ktp as ktpdokter,poliklinik.nm_poli,"
                + "satu_sehat_mapping_lokasi_ralan.id_lokasi_satusehat,reg_periksa.status_lanjut,"
                + "ifnull(satu_sehat_encounter.id_encounter,'') as id_encounter "
                + "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                + "inner join pegawai on pegawai.nik=reg_periksa.kd_dokter "
                + "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli "
                + "inner join satu_sehat_mapping_lokasi_ralan on satu_sehat_mapping_lokasi_ralan.kd_poli=poliklinik.kd_poli "
                + "left join satu_sehat_encounter on satu_sehat_encounter.no_rawat=reg_periksa.no_rawat "
                + "where reg_periksa.no_rawat=?")) {
            psEnc.setString(1, noRawat);
            try (ResultSet rsEnc = psEnc.executeQuery()) {
                if (!rsEnc.next()) {
                    System.out.println("Kirim Encounter " + noRawat + " : data tidak ditemukan / poli belum dipetakan lokasi Satu Sehat.");
                    return 0;
                }
                if (!rsEnc.getString("id_encounter").equals("")) {
                    System.out.println("Kirim Encounter " + noRawat + " : sudah pernah terkirim (" + rsEnc.getString("id_encounter") + ").");
                    return 0;
                }
                String noKtpPasien = rsEnc.getString("no_ktp") == null ? "" : rsEnc.getString("no_ktp").trim();
                String noKtpDokter = rsEnc.getString("ktpdokter") == null ? "" : rsEnc.getString("ktpdokter").trim();
                if (noKtpPasien.equals("") || noKtpDokter.equals("")) {
                    System.out.println("Kirim Encounter " + noRawat + " : NIK pasien/dokter belum lengkap.");
                    return 0;
                }
                String idpasien = cekViaSatuSehat.tampilIDPasien(noKtpPasien);
                String iddokter = cekViaSatuSehat.tampilIDParktisi(noKtpDokter);
                if (idpasien == null || idpasien.equals("") || iddokter == null || iddokter.equals("")) {
                    System.out.println("Kirim Encounter " + noRawat + " : ID pasien/dokter Satu Sehat tidak ditemukan.");
                    return 0;
                }
                String periode = rsEnc.getString("tgl_registrasi") + "T" + rsEnc.getString("jam_reg") + "+07:00";
                boolean ralan = rsEnc.getString("status_lanjut").equals("Ralan");
                String json = "{"
                        + "\"resourceType\": \"Encounter\","
                        + "\"status\": \"arrived\","
                        + "\"class\": {"
                        + "\"system\": \"http://terminology.hl7.org/CodeSystem/v3-ActCode\","
                        + "\"code\": \"" + (ralan ? "AMB" : "IMP") + "\","
                        + "\"display\": \"" + (ralan ? "ambulatory" : "inpatient encounter") + "\""
                        + "},"
                        + "\"subject\": {"
                        + "\"reference\": \"Patient/" + idpasien + "\","
                        + "\"display\": \"" + rsEnc.getString("nm_pasien") + "\""
                        + "},"
                        + "\"participant\": [{"
                        + "\"type\": [{\"coding\": [{"
                        + "\"system\": \"http://terminology.hl7.org/CodeSystem/v3-ParticipationType\","
                        + "\"code\": \"ATND\",\"display\": \"attender\""
                        + "}]}],"
                        + "\"individual\": {"
                        + "\"reference\": \"Practitioner/" + iddokter + "\","
                        + "\"display\": \"" + rsEnc.getString("nama") + "\""
                        + "}"
                        + "}],"
                        + "\"period\": {\"start\": \"" + periode + "\"},"
                        + "\"location\": [{\"location\": {"
                        + "\"reference\": \"Location/" + rsEnc.getString("id_lokasi_satusehat") + "\","
                        + "\"display\": \"" + rsEnc.getString("nm_poli") + "\""
                        + "}}],"
                        + "\"statusHistory\": [{"
                        + "\"status\": \"arrived\","
                        + "\"period\": {\"start\": \"" + periode + "\"}"
                        + "}],"
                        + "\"serviceProvider\": {\"reference\": \"Organization/" + koneksiDB.IDSATUSEHAT() + "\"},"
                        + "\"identifier\": [{"
                        + "\"system\": \"http://sys-ids.kemkes.go.id/encounter/" + koneksiDB.IDSATUSEHAT() + "\","
                        + "\"value\": \"" + noRawat + "\""
                        + "}]"
                        + "}";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("Authorization", "Bearer " + apiSatuSehat.TokenSatuSehat());
                System.out.println("URL : " + linkSatuSehat + "/Encounter");
                System.out.println("Request JSON : " + json);
                org.springframework.http.HttpEntity requestEntity = new org.springframework.http.HttpEntity(json, headers);
                String hasil = apiSatuSehat.getRest().exchange(linkSatuSehat + "/Encounter", HttpMethod.POST, requestEntity, String.class).getBody();
                System.out.println("Result JSON : " + hasil);
                JsonNode rootEnc = mapper.readTree(hasil);
                String idEnc = rootEnc.path("id").asText();
                if (!idEnc.equals("")) {
                    Sequel.menyimpantf2("satu_sehat_encounter", "?,?", "No.Rawat", 2, new String[]{noRawat, idEnc});
                    return 1;
                }
                return -1;
            }
        } catch (Exception e) {
            if (e instanceof org.springframework.web.client.HttpStatusCodeException) {
                System.out.println("Notifikasi Kirim Encounter " + noRawat + " : " + e + " => " + ((org.springframework.web.client.HttpStatusCodeException) e).getResponseBodyAsString());
            } else {
                System.out.println("Notifikasi Kirim Encounter " + noRawat + " : " + e);
            }
            return -1;
        }
    }

    private int[] kirimServiceRequestRadiologi(String noRawat) {
        int sukses = 0, lewati = 0, gagal = 0;
        String idpasien = "", iddokter = "";
        boolean idResolved = false;
        try (PreparedStatement psSr = koneksi.prepareStatement(
                "select reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.no_ktp,pegawai.nama,pegawai.no_ktp as ktpdokter,"
                + "satu_sehat_encounter.id_encounter,permintaan_radiologi.noorder,permintaan_radiologi.tgl_permintaan,"
                + "permintaan_radiologi.jam_permintaan,permintaan_radiologi.diagnosa_klinis,jns_perawatan_radiologi.nm_perawatan,"
                + "satu_sehat_mapping_radiologi.code,satu_sehat_mapping_radiologi.system,satu_sehat_mapping_radiologi.display,"
                + "ifnull(satu_sehat_servicerequest_radiologi.id_servicerequest,'') as id_servicerequest,permintaan_pemeriksaan_radiologi.kd_jenis_prw "
                + "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                + "inner join pegawai on pegawai.nik=reg_periksa.kd_dokter "
                + "inner join satu_sehat_encounter on satu_sehat_encounter.no_rawat=reg_periksa.no_rawat "
                + "inner join permintaan_radiologi on permintaan_radiologi.no_rawat=reg_periksa.no_rawat "
                + "inner join permintaan_pemeriksaan_radiologi on permintaan_pemeriksaan_radiologi.noorder=permintaan_radiologi.noorder "
                + "inner join jns_perawatan_radiologi on jns_perawatan_radiologi.kd_jenis_prw=permintaan_pemeriksaan_radiologi.kd_jenis_prw "
                + "inner join satu_sehat_mapping_radiologi on satu_sehat_mapping_radiologi.kd_jenis_prw=jns_perawatan_radiologi.kd_jenis_prw "
                + "left join satu_sehat_servicerequest_radiologi on satu_sehat_servicerequest_radiologi.noorder=permintaan_pemeriksaan_radiologi.noorder "
                + "and satu_sehat_servicerequest_radiologi.kd_jenis_prw=permintaan_pemeriksaan_radiologi.kd_jenis_prw "
                + "where reg_periksa.no_rawat=?")) {
            psSr.setString(1, noRawat);
            try (ResultSet rsSr = psSr.executeQuery()) {
                while (rsSr.next()) {
                    try {
                        if (!rsSr.getString("id_servicerequest").equals("")) {
                            lewati++;
                            continue;
                        }
                        String noKtpPasien = rsSr.getString("no_ktp") == null ? "" : rsSr.getString("no_ktp").trim();
                        String noKtpDokter = rsSr.getString("ktpdokter") == null ? "" : rsSr.getString("ktpdokter").trim();
                        String idEncounter = rsSr.getString("id_encounter") == null ? "" : rsSr.getString("id_encounter").trim();
                        if (noKtpPasien.equals("") || noKtpDokter.equals("") || idEncounter.equals("")) {
                            System.out.println("Kirim ServiceRequest " + noRawat + " : NIK pasien/dokter atau ID Encounter belum lengkap.");
                            lewati++;
                            continue;
                        }
                        if (!idResolved) {
                            idpasien = cekViaSatuSehat.tampilIDPasien(noKtpPasien);
                            iddokter = cekViaSatuSehat.tampilIDParktisi(noKtpDokter);
                            idResolved = true;
                        }
                        if (idpasien == null || idpasien.equals("") || iddokter == null || iddokter.equals("")) {
                            System.out.println("Kirim ServiceRequest " + noRawat + " : ID pasien/dokter Satu Sehat tidak ditemukan.");
                            lewati++;
                            continue;
                        }
                        String noorder = rsSr.getString("noorder");
                        String kdJenisPrw = rsSr.getString("kd_jenis_prw");
                        String nmPerawatan = rsSr.getString("nm_perawatan");
                        String nmPasien = rsSr.getString("nm_pasien");
                        String noRkmMedis = rsSr.getString("no_rkm_medis");
                        String mapSystem = rsSr.getString("system");
                        String mapCode = rsSr.getString("code");
                        String mapDisplay = rsSr.getString("display");
                        String diagnosaKlinis = rsSr.getString("diagnosa_klinis");
                        String namaDokter = rsSr.getString("nama");
                        String tglJam = rsSr.getString("tgl_permintaan") + " " + rsSr.getString("jam_permintaan");
                        String idOrg = koneksiDB.IDSATUSEHAT();
                        String json = "{"
                                + "\"resourceType\": \"ServiceRequest\","
                                + "\"identifier\": [{"
                                + "\"system\": \"http://sys-ids.kemkes.go.id/acsn/" + idOrg + "\","
                                + "\"value\": \"" + noorder.replaceAll("PR", "") + kdJenisPrw + "\""
                                + "}],"
                                + "\"status\": \"active\","
                                + "\"intent\": \"order\","
                                + "\"category\": [{"
                                + "\"coding\": [{"
                                + "\"system\": \"http://snomed.info/sct\","
                                + "\"code\": \"363679005\","
                                + "\"display\": \"Imaging\""
                                + "}]"
                                + "}],"
                                + "\"code\": {"
                                + "\"coding\": [{"
                                + "\"system\": \"" + mapSystem + "\","
                                + "\"code\": \"" + mapCode + "\","
                                + "\"display\": \"" + mapDisplay + "\""
                                + "}],"
                                + "\"text\": \"" + nmPerawatan + "\""
                                + "},"
                                + "\"subject\": {\"reference\": \"Patient/" + idpasien + "\"},"
                                + "\"encounter\": {"
                                + "\"reference\": \"Encounter/" + idEncounter + "\","
                                + "\"display\": \"Permintaan " + nmPerawatan + " atas nama pasien " + nmPasien + " No.RM " + noRkmMedis + " No.Rawat " + noRawat + ", pada tanggal " + tglJam + "\""
                                + "},"
                                + "\"authoredOn\": \"" + tglJam.replaceAll(" ", "T") + "+07:00\","
                                + "\"requester\": {"
                                + "\"reference\": \"Practitioner/" + iddokter + "\","
                                + "\"display\": \"" + namaDokter + "\""
                                + "},"
                                + "\"performer\": [{"
                                + "\"reference\": \"Organization/" + idOrg + "\","
                                + "\"display\": \"Ruang Radiologi/Petugas Radiologi\""
                                + "}],"
                                + "\"reasonCode\": [{\"text\": \"" + diagnosaKlinis + "\"}]"
                                + "}";
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("Authorization", "Bearer " + apiSatuSehat.TokenSatuSehat());
                        System.out.println("URL : " + linkSatuSehat + "/ServiceRequest");
                        System.out.println("Request JSON : " + json);
                        org.springframework.http.HttpEntity requestEntity = new org.springframework.http.HttpEntity(json, headers);
                        String hasil = apiSatuSehat.getRest().exchange(linkSatuSehat + "/ServiceRequest", HttpMethod.POST, requestEntity, String.class).getBody();
                        System.out.println("Result JSON : " + hasil);
                        JsonNode rootSr = mapper.readTree(hasil);
                        String idSr = rootSr.path("id").asText();
                        if (!idSr.equals("")) {
                            Sequel.menyimpantf2("satu_sehat_servicerequest_radiologi", "?,?,?", "No.Order", 3, new String[]{noorder, kdJenisPrw, idSr});
                            sukses++;
                        } else {
                            gagal++;
                        }
                    } catch (Exception e) {
                        gagal++;
                        if (e instanceof org.springframework.web.client.HttpStatusCodeException) {
                            System.out.println("Notifikasi Kirim ServiceRequest " + noRawat + " : " + e + " => " + ((org.springframework.web.client.HttpStatusCodeException) e).getResponseBodyAsString());
                        } else {
                            System.out.println("Notifikasi Kirim ServiceRequest " + noRawat + " : " + e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi Kirim ServiceRequest " + noRawat + " : " + e);
        }
        return new int[]{sukses, lewati, gagal};
    }
}
