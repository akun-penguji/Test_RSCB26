/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * DlgRujuk.java
 *
 * Created on 31 Mei 10, 20:19:56
 */
package rekammedis;

import bridging.UploadPDFTte;
import bridging.ViewPDFNextcloud;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import digitalsignature.DlgViewPdfResumeRalan;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariDokter;
import laporan.DlgBerkasRawat;
import laporan.DlgDiagnosaPenyakit;
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
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ContentType;

/**
 *
 * @author perpustakaan
 */
public final class RMDataResumePasien extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private DlgCariDokter dokter = new DlgCariDokter(null, false);
    private RMCariKeluhan carikeluhan = new RMCariKeluhan(null, false);
    private RMCariPemeriksaan caripemeriksaan = new RMCariPemeriksaan(null, false);
    private RMCariHasilRadiologi cariradiologi = new RMCariHasilRadiologi(null, false);
    private RMCariHasilLaborat carilaborat = new RMCariHasilLaborat(null, false);
    private RMCariJumlahObat cariobat = new RMCariJumlahObat(null, false);
    private DlgDiagnosaPenyakit penyakit = new DlgDiagnosaPenyakit(null, false);
    private String tanggal = "", finger = "", variabel = "", FileName = "", kodeberkas = "", json = "";
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JTextField Edukasi;
    private JTextArea DIlanjutkan;

    /**
     * Creates new form DlgRujuk
     *
     * @param parent
     * @param modal
     */
    public RMDataResumePasien(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        WindowPhrase.setSize(320, 100);

        tabMode = new DefaultTableModel(null, new Object[]{
            "Tgl.Rawat", "No.Rawat", "No.RM", "Nama Pasien", "Kode Dokter", "Dokter Penanggung Jawab", "Pulang", "Keluhan utama riwayat penyakit yang postif",
            "Jalannya penyakit selama perawatan", "Pemeriksaan penunjang yang positif", "Hasil laboratorium yang positif", "Diagnosa Utama", "ICD10 Utama", "Diagnosa Sekunder 1",
            "ICD10 Sek 1", "Diagnosa Sekunder 2", "ICD10 Sek 2", "Diagnosa Sekunder 3", "ICD10 Sek 3", "Diagnosa Sekunder 4", "ICD10 Sek 4", "Prosedur Utama", "ICD9 Utama",
            "Prosedur Sekunder 1", "ICD9 Sek1", "Prosedur Sekunder 2", "ICD9 Sek2", "Prosedur Sekunder 3", "ICD9 Sek3", "Obat-obatan waktu pulang/nasihat", "Keadaan Pulang", "Ket.Keadaan", "Cara Keluar", "Ket.Keluar"
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

        for (i = 0; i < 34; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(65);
            } else if (i == 1) {
                column.setPreferredWidth(105);
            } else if (i == 2) {
                column.setPreferredWidth(65);
            } else if (i == 3) {
                column.setPreferredWidth(150);
            } else if (i == 4) {
                column.setPreferredWidth(90);
            } else if (i == 5) {
                column.setPreferredWidth(150);
            } else if (i == 6) {
                column.setPreferredWidth(60);
            } else if (i == 7) {
                column.setPreferredWidth(250);
            } else if (i == 8) {
                column.setPreferredWidth(250);
            } else if (i == 9) {
                column.setPreferredWidth(250);
            } else if (i == 10) {
                column.setPreferredWidth(250);
            } else if (i == 11) {
                column.setPreferredWidth(170);
            } else if (i == 12) {
                column.setPreferredWidth(75);
            } else if (i == 13) {
                column.setPreferredWidth(170);
            } else if (i == 14) {
                column.setPreferredWidth(75);
            } else if (i == 15) {
                column.setPreferredWidth(170);
            } else if (i == 16) {
                column.setPreferredWidth(75);
            } else if (i == 17) {
                column.setPreferredWidth(170);
            } else if (i == 18) {
                column.setPreferredWidth(75);
            } else if (i == 19) {
                column.setPreferredWidth(170);
            } else if (i == 20) {
                column.setPreferredWidth(75);
            } else if (i == 21) {
                column.setPreferredWidth(170);
            } else if (i == 22) {
                column.setPreferredWidth(75);
            } else if (i == 23) {
                column.setPreferredWidth(170);
            } else if (i == 24) {
                column.setPreferredWidth(75);
            } else if (i == 25) {
                column.setPreferredWidth(170);
            } else if (i == 26) {
                column.setPreferredWidth(75);
            } else if (i == 27) {
                column.setPreferredWidth(170);
            } else if (i == 28) {
                column.setPreferredWidth(75);
            } else if (i == 29) {
                column.setPreferredWidth(250);
            } else if (i == 30) {
                column.setPreferredWidth(100);
            } else if (i == 31) {
                column.setPreferredWidth(150);
            } else if (i == 32) {
                column.setPreferredWidth(100);
            } else if (i == 33) {
                column.setPreferredWidth(150);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        Keluhan.setDocument(new batasInput((int) 2000).getKata(Keluhan));
        JalannyaPenyakit.setDocument(new batasInput((int) 2000).getKata(JalannyaPenyakit));
        PemeriksaanPenunjang.setDocument(new batasInput((int) 2000).getKata(PemeriksaanPenunjang));
        HasilLaborat.setDocument(new batasInput((int) 2000).getKata(HasilLaborat));
        Obat2an.setDocument(new batasInput((int) 2000).getKata(Obat2an));
        DiagnosaUtama.setDocument(new batasInput((int) 80).getKata(DiagnosaUtama));
        DiagnosaSekunder1.setDocument(new batasInput((int) 80).getKata(DiagnosaSekunder1));
        DiagnosaSekunder2.setDocument(new batasInput((int) 80).getKata(DiagnosaSekunder2));
        DiagnosaSekunder3.setDocument(new batasInput((int) 80).getKata(DiagnosaSekunder3));
        DiagnosaSekunder4.setDocument(new batasInput((int) 80).getKata(DiagnosaSekunder4));
        ProsedurUtama.setDocument(new batasInput((int) 80).getKata(ProsedurUtama));
        ProsedurSekunder1.setDocument(new batasInput((int) 80).getKata(ProsedurSekunder1));
        ProsedurSekunder2.setDocument(new batasInput((int) 80).getKata(ProsedurSekunder2));
        ProsedurSekunder3.setDocument(new batasInput((int) 80).getKata(ProsedurSekunder3));
        KodeDiagnosaUtama.setDocument(new batasInput((int) 10).getKata(KodeDiagnosaUtama));
        KodeDiagnosaSekunder1.setDocument(new batasInput((int) 10).getKata(KodeDiagnosaSekunder1));
        KodeDiagnosaSekunder2.setDocument(new batasInput((int) 10).getKata(KodeDiagnosaSekunder2));
        KodeDiagnosaSekunder3.setDocument(new batasInput((int) 10).getKata(KodeDiagnosaSekunder3));
        KodeDiagnosaSekunder4.setDocument(new batasInput((int) 10).getKata(KodeDiagnosaSekunder4));
        KodeProsedurUtama.setDocument(new batasInput((int) 8).getKata(KodeProsedurUtama));
        KodeProsedurSekunder1.setDocument(new batasInput((int) 8).getKata(KodeProsedurSekunder1));
        KodeProsedurSekunder2.setDocument(new batasInput((int) 8).getKata(KodeProsedurSekunder2));
        KodeProsedurSekunder3.setDocument(new batasInput((int) 8).getKata(KodeProsedurSekunder3));
        TCari.setDocument(new batasInput((int) 100).getKata(TCari));

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
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (dokter.getTable().getSelectedRow() != -1) {
                    KodeDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                    NamaDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                    KodeDokter.requestFocus();
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

        carikeluhan.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (carikeluhan.getTable().getSelectedRow() != -1) {
                    Keluhan.append(carikeluhan.getTable().getValueAt(carikeluhan.getTable().getSelectedRow(), 2).toString() + ", ");
                    Keluhan.requestFocus();
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

        caripemeriksaan.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (caripemeriksaan.getTable().getSelectedRow() != -1) {
                    Keluhan.append(caripemeriksaan.getTable().getValueAt(caripemeriksaan.getTable().getSelectedRow(), 2).toString() + ", ");
                    Keluhan.requestFocus();
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

        cariradiologi.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (cariradiologi.getTable().getSelectedRow() != -1) {
                    PemeriksaanPenunjang.append(cariradiologi.getTable().getValueAt(cariradiologi.getTable().getSelectedRow(), 2).toString() + ", ");
                    PemeriksaanPenunjang.requestFocus();
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

        carilaborat.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (carilaborat.getTable().getSelectedRow() != -1) {
                    HasilLaborat.append(carilaborat.getTable().getValueAt(carilaborat.getTable().getSelectedRow(), 2).toString() + ", ");
                    HasilLaborat.requestFocus();
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

        cariobat.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (cariobat.getTable().getSelectedRow() != -1) {
                    Obat2an.append(cariobat.getTable().getValueAt(cariobat.getTable().getSelectedRow(), 2).toString() + ", ");
                    Obat2an.requestFocus();
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

        penyakit.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                tampil();
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

        ChkInput.setSelected(false);
        isForm();

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
        MnLaporanResume = new javax.swing.JMenuItem();
        MnLaporanResumeESign = new javax.swing.JMenuItem();
        MnInputDiagnosa = new javax.swing.JMenuItem();
        ppBerkasDigital = new javax.swing.JMenuItem();
        MnSPBK = new javax.swing.JMenuItem();
        MnSPBK2 = new javax.swing.JMenuItem();
        UploadResume = new javax.swing.JMenuItem();
        TagSignerDokter = new javax.swing.JMenuItem();
        TagSignerDokterBatch = new javax.swing.JMenuItem();
        WindowPhrase = new javax.swing.JDialog();
        internalFrame8 = new widget.InternalFrame();
        jLabel45 = new widget.Label();
        panelisi5 = new widget.panelisi();
        BtnClosePhrase = new widget.Button();
        BtnSimpanTandaTangan = new widget.Button();
        jLabel46 = new widget.Label();
        Phrase = new widget.TextBox();
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
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel19 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        PanelInput = new javax.swing.JPanel();
        ChkInput = new widget.CekBox();
        scrollInput = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        jLabel4 = new widget.Label();
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        TNoRM = new widget.TextBox();
        jLabel25 = new widget.Label();
        DiagnosaSekunder2 = new widget.TextBox();
        jLabel26 = new widget.Label();
        DiagnosaUtama = new widget.TextBox();
        jLabel27 = new widget.Label();
        DiagnosaSekunder3 = new widget.TextBox();
        jLabel28 = new widget.Label();
        DiagnosaSekunder4 = new widget.TextBox();
        scrollPane2 = new widget.ScrollPane();
        Keluhan = new widget.TextArea();
        jLabel5 = new widget.Label();
        jLabel8 = new widget.Label();
        scrollPane3 = new widget.ScrollPane();
        JalannyaPenyakit = new widget.TextArea();
        jLabel9 = new widget.Label();
        scrollPane4 = new widget.ScrollPane();
        PemeriksaanPenunjang = new widget.TextArea();
        jLabel10 = new widget.Label();
        scrollPane5 = new widget.ScrollPane();
        HasilLaborat = new widget.TextArea();
        jLabel11 = new widget.Label();
        scrollPane6 = new widget.ScrollPane();
        Obat2an = new widget.TextArea();
        jLabel29 = new widget.Label();
        jLabel30 = new widget.Label();
        DiagnosaSekunder1 = new widget.TextBox();
        jLabel31 = new widget.Label();
        KodeDiagnosaUtama = new widget.TextBox();
        KodeDiagnosaSekunder1 = new widget.TextBox();
        KodeDiagnosaSekunder2 = new widget.TextBox();
        KodeDiagnosaSekunder3 = new widget.TextBox();
        KodeDiagnosaSekunder4 = new widget.TextBox();
        jLabel32 = new widget.Label();
        ProsedurUtama = new widget.TextBox();
        KodeProsedurUtama = new widget.TextBox();
        ProsedurSekunder1 = new widget.TextBox();
        jLabel33 = new widget.Label();
        KodeProsedurSekunder1 = new widget.TextBox();
        jLabel34 = new widget.Label();
        ProsedurSekunder2 = new widget.TextBox();
        KodeProsedurSekunder2 = new widget.TextBox();
        KodeProsedurSekunder3 = new widget.TextBox();
        ProsedurSekunder3 = new widget.TextBox();
        jLabel35 = new widget.Label();
        label14 = new widget.Label();
        KodeDokter = new widget.TextBox();
        NamaDokter = new widget.TextBox();
        BtnDokter = new widget.Button();
        Kondisi = new widget.ComboBox();
        jLabel12 = new widget.Label();
        BtnDokter1 = new widget.Button();
        BtnDokter2 = new widget.Button();
        BtnDokter3 = new widget.Button();
        BtnDokter4 = new widget.Button();
        BtnDokter5 = new widget.Button();
        jLabel36 = new widget.Label();
        CaraKeluar2 = new widget.ComboBox();
        Keadaan2 = new widget.ComboBox();
        KetKeluar2 = new widget.TextBox();
        KetKeadaanPulang2 = new widget.TextBox();
        jLabel37 = new widget.Label();
        BtnAmbilDataDiagnosaProsedur = new widget.Button();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnLaporanResume.setBackground(new java.awt.Color(255, 255, 254));
        MnLaporanResume.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnLaporanResume.setForeground(new java.awt.Color(50, 50, 50));
        MnLaporanResume.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnLaporanResume.setText("Laporan Resume Pasien");
        MnLaporanResume.setName("MnLaporanResume"); // NOI18N
        MnLaporanResume.setPreferredSize(new java.awt.Dimension(250, 26));
        MnLaporanResume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnLaporanResumeActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnLaporanResume);

        MnLaporanResumeESign.setBackground(new java.awt.Color(255, 255, 254));
        MnLaporanResumeESign.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnLaporanResumeESign.setForeground(new java.awt.Color(50, 50, 50));
        MnLaporanResumeESign.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnLaporanResumeESign.setText("Tanda Tangan Elektronik (TTE) BSrE");
        MnLaporanResumeESign.setName("MnLaporanResumeESign"); // NOI18N
        MnLaporanResumeESign.setPreferredSize(new java.awt.Dimension(250, 26));
        MnLaporanResumeESign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnLaporanResumeESignActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnLaporanResumeESign);

        MnInputDiagnosa.setBackground(new java.awt.Color(255, 255, 254));
        MnInputDiagnosa.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnInputDiagnosa.setForeground(new java.awt.Color(50, 50, 50));
        MnInputDiagnosa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnInputDiagnosa.setText("Input Diagnosa Pasien");
        MnInputDiagnosa.setName("MnInputDiagnosa"); // NOI18N
        MnInputDiagnosa.setPreferredSize(new java.awt.Dimension(250, 26));
        MnInputDiagnosa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnInputDiagnosaActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnInputDiagnosa);

        ppBerkasDigital.setBackground(new java.awt.Color(255, 255, 254));
        ppBerkasDigital.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppBerkasDigital.setForeground(new java.awt.Color(50, 50, 50));
        ppBerkasDigital.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppBerkasDigital.setText("Berkas Digital Perawatan");
        ppBerkasDigital.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppBerkasDigital.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppBerkasDigital.setName("ppBerkasDigital"); // NOI18N
        ppBerkasDigital.setPreferredSize(new java.awt.Dimension(250, 26));
        ppBerkasDigital.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppBerkasDigitalBtnPrintActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppBerkasDigital);

        MnSPBK.setBackground(new java.awt.Color(255, 255, 254));
        MnSPBK.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnSPBK.setForeground(new java.awt.Color(50, 50, 50));
        MnSPBK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnSPBK.setText("Surat Bukti Pelayanan Kesehatan (SBPK)");
        MnSPBK.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnSPBK.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnSPBK.setName("MnSPBK"); // NOI18N
        MnSPBK.setPreferredSize(new java.awt.Dimension(250, 26));
        MnSPBK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnSPBKActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnSPBK);

        MnSPBK2.setBackground(new java.awt.Color(255, 255, 254));
        MnSPBK2.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnSPBK2.setForeground(new java.awt.Color(50, 50, 50));
        MnSPBK2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnSPBK2.setText("Surat Bukti Pelayanan Kesehatan (SBPK) 2");
        MnSPBK2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        MnSPBK2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        MnSPBK2.setName("MnSPBK2"); // NOI18N
        MnSPBK2.setPreferredSize(new java.awt.Dimension(250, 26));
        MnSPBK2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnSPBK2ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnSPBK2);

        UploadResume.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        UploadResume.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        UploadResume.setText("Upload Resume ke Berkas Digital");
        UploadResume.setName("UploadResume"); // NOI18N
        UploadResume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UploadResumeActionPerformed(evt);
            }
        });
        jPopupMenu1.add(UploadResume);

        TagSignerDokter.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TagSignerDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        TagSignerDokter.setText("Tag Signer Dokter");
        TagSignerDokter.setName("TagSignerDokter"); // NOI18N
        TagSignerDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TagSignerDokterActionPerformed(evt);
            }
        });
        jPopupMenu1.add(TagSignerDokter);

        TagSignerDokterBatch.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TagSignerDokterBatch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        TagSignerDokterBatch.setText("Tag Signer Dokter Batch");
        TagSignerDokterBatch.setName("TagSignerDokterBatch"); // NOI18N
        TagSignerDokterBatch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TagSignerDokterBatchActionPerformed(evt);
            }
        });
        jPopupMenu1.add(TagSignerDokterBatch);

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
        BtnSimpanTandaTangan.setText("TTE");
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

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Resume Medis Pasien Rawat Jalan ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
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
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbObatKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbObat);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setMinimumSize(new java.awt.Dimension(400, 20));
        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 32));
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

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass8.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass8.add(LCount);

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

        jLabel19.setText("Tgl.Rawat :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(67, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "24-02-2026" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari1);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel21);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "24-02-2026" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(310, 23));
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

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
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
        panelGlass9.add(BtnAll);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 448));
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

        scrollInput.setName("scrollInput"); // NOI18N

        FormInput.setBackground(new java.awt.Color(250, 255, 245));
        FormInput.setBorder(null);
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 1172));
        FormInput.setLayout(null);

        jLabel4.setText("Keluhan Utama Riwayat Penyakit Yang Positif :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 70, 240, 23);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(104, 10, 141, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        TPasien.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TPasienKeyPressed(evt);
            }
        });
        FormInput.add(TPasien);
        TPasien.setBounds(361, 10, 424, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        TNoRM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRMKeyPressed(evt);
            }
        });
        FormInput.add(TNoRM);
        TNoRM.setBounds(247, 10, 112, 23);

        jLabel25.setText("Diagnosa Sekunder 2 :");
        jLabel25.setName("jLabel25"); // NOI18N
        FormInput.add(jLabel25);
        jLabel25.setBounds(0, 380, 145, 23);

        DiagnosaSekunder2.setHighlighter(null);
        DiagnosaSekunder2.setName("DiagnosaSekunder2"); // NOI18N
        DiagnosaSekunder2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaSekunder2KeyPressed(evt);
            }
        });
        FormInput.add(DiagnosaSekunder2);
        DiagnosaSekunder2.setBounds(149, 380, 520, 23);

        jLabel26.setText("Diagnosa Sekunder 3 :");
        jLabel26.setName("jLabel26"); // NOI18N
        FormInput.add(jLabel26);
        jLabel26.setBounds(0, 410, 145, 23);

        DiagnosaUtama.setHighlighter(null);
        DiagnosaUtama.setName("DiagnosaUtama"); // NOI18N
        DiagnosaUtama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DiagnosaUtamaActionPerformed(evt);
            }
        });
        DiagnosaUtama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaUtamaKeyPressed(evt);
            }
        });
        FormInput.add(DiagnosaUtama);
        DiagnosaUtama.setBounds(149, 320, 520, 23);

        jLabel27.setText("Diagnosa Utama :");
        jLabel27.setName("jLabel27"); // NOI18N
        FormInput.add(jLabel27);
        jLabel27.setBounds(0, 320, 145, 23);

        DiagnosaSekunder3.setHighlighter(null);
        DiagnosaSekunder3.setName("DiagnosaSekunder3"); // NOI18N
        DiagnosaSekunder3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaSekunder3KeyPressed(evt);
            }
        });
        FormInput.add(DiagnosaSekunder3);
        DiagnosaSekunder3.setBounds(149, 410, 520, 23);

        jLabel28.setText("Diagnosa Sekunder 4 :");
        jLabel28.setName("jLabel28"); // NOI18N
        FormInput.add(jLabel28);
        jLabel28.setBounds(0, 440, 145, 23);

        DiagnosaSekunder4.setHighlighter(null);
        DiagnosaSekunder4.setName("DiagnosaSekunder4"); // NOI18N
        DiagnosaSekunder4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaSekunder4KeyPressed(evt);
            }
        });
        FormInput.add(DiagnosaSekunder4);
        DiagnosaSekunder4.setBounds(149, 440, 520, 23);

        scrollPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane2.setName("scrollPane2"); // NOI18N

        Keluhan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Keluhan.setColumns(20);
        Keluhan.setRows(5);
        Keluhan.setName("Keluhan"); // NOI18N
        Keluhan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeluhanKeyPressed(evt);
            }
        });
        scrollPane2.setViewportView(Keluhan);

        FormInput.add(scrollPane2);
        scrollPane2.setBounds(244, 70, 541, 50);

        jLabel5.setText("No.Rawat :");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(0, 10, 100, 23);

        jLabel8.setText("Jalannya Penyakit Selama Perawatan :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(0, 127, 240, 23);

        scrollPane3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane3.setName("scrollPane3"); // NOI18N

        JalannyaPenyakit.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        JalannyaPenyakit.setColumns(20);
        JalannyaPenyakit.setRows(5);
        JalannyaPenyakit.setName("JalannyaPenyakit"); // NOI18N
        JalannyaPenyakit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JalannyaPenyakitKeyPressed(evt);
            }
        });
        scrollPane3.setViewportView(JalannyaPenyakit);

        FormInput.add(scrollPane3);
        scrollPane3.setBounds(244, 127, 541, 50);

        jLabel9.setText("Pemeriksaan Penunjang Yang Positif :");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(0, 184, 240, 23);

        scrollPane4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane4.setName("scrollPane4"); // NOI18N

        PemeriksaanPenunjang.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        PemeriksaanPenunjang.setColumns(20);
        PemeriksaanPenunjang.setRows(5);
        PemeriksaanPenunjang.setName("PemeriksaanPenunjang"); // NOI18N
        PemeriksaanPenunjang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PemeriksaanPenunjangKeyPressed(evt);
            }
        });
        scrollPane4.setViewportView(PemeriksaanPenunjang);

        FormInput.add(scrollPane4);
        scrollPane4.setBounds(244, 184, 541, 50);

        jLabel10.setText("Hasil Laboratorium Yang Positif :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(0, 241, 240, 23);

        scrollPane5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane5.setName("scrollPane5"); // NOI18N

        HasilLaborat.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        HasilLaborat.setColumns(20);
        HasilLaborat.setRows(5);
        HasilLaborat.setName("HasilLaborat"); // NOI18N
        HasilLaborat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                HasilLaboratKeyPressed(evt);
            }
        });
        scrollPane5.setViewportView(HasilLaborat);

        FormInput.add(scrollPane5);
        scrollPane5.setBounds(244, 241, 541, 50);

        jLabel11.setText("Obat-obatan Waktu Pulang/Nasihat :");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(0, 590, 240, 23);

        scrollPane6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane6.setName("scrollPane6"); // NOI18N

        Obat2an.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Obat2an.setColumns(20);
        Obat2an.setRows(5);
        Obat2an.setName("Obat2an"); // NOI18N
        Obat2an.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Obat2anKeyPressed(evt);
            }
        });
        scrollPane6.setViewportView(Obat2an);

        FormInput.add(scrollPane6);
        scrollPane6.setBounds(244, 590, 541, 50);

        jLabel29.setText("Diagnosa Akhir :");
        jLabel29.setName("jLabel29"); // NOI18N
        FormInput.add(jLabel29);
        jLabel29.setBounds(0, 297, 97, 23);

        jLabel30.setText("Diagnosa Sekunder 1 :");
        jLabel30.setName("jLabel30"); // NOI18N
        FormInput.add(jLabel30);
        jLabel30.setBounds(0, 350, 145, 23);

        DiagnosaSekunder1.setHighlighter(null);
        DiagnosaSekunder1.setName("DiagnosaSekunder1"); // NOI18N
        DiagnosaSekunder1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaSekunder1KeyPressed(evt);
            }
        });
        FormInput.add(DiagnosaSekunder1);
        DiagnosaSekunder1.setBounds(149, 350, 520, 23);

        jLabel31.setText("Kode ICD :");
        jLabel31.setName("jLabel31"); // NOI18N
        FormInput.add(jLabel31);
        jLabel31.setBounds(530, 297, 210, 23);

        KodeDiagnosaUtama.setHighlighter(null);
        KodeDiagnosaUtama.setName("KodeDiagnosaUtama"); // NOI18N
        KodeDiagnosaUtama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeDiagnosaUtamaKeyPressed(evt);
            }
        });
        FormInput.add(KodeDiagnosaUtama);
        KodeDiagnosaUtama.setBounds(710, 320, 75, 23);

        KodeDiagnosaSekunder1.setHighlighter(null);
        KodeDiagnosaSekunder1.setName("KodeDiagnosaSekunder1"); // NOI18N
        KodeDiagnosaSekunder1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeDiagnosaSekunder1KeyPressed(evt);
            }
        });
        FormInput.add(KodeDiagnosaSekunder1);
        KodeDiagnosaSekunder1.setBounds(710, 350, 75, 23);

        KodeDiagnosaSekunder2.setHighlighter(null);
        KodeDiagnosaSekunder2.setName("KodeDiagnosaSekunder2"); // NOI18N
        KodeDiagnosaSekunder2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeDiagnosaSekunder2KeyPressed(evt);
            }
        });
        FormInput.add(KodeDiagnosaSekunder2);
        KodeDiagnosaSekunder2.setBounds(710, 380, 75, 23);

        KodeDiagnosaSekunder3.setHighlighter(null);
        KodeDiagnosaSekunder3.setName("KodeDiagnosaSekunder3"); // NOI18N
        KodeDiagnosaSekunder3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeDiagnosaSekunder3KeyPressed(evt);
            }
        });
        FormInput.add(KodeDiagnosaSekunder3);
        KodeDiagnosaSekunder3.setBounds(710, 410, 75, 23);

        KodeDiagnosaSekunder4.setHighlighter(null);
        KodeDiagnosaSekunder4.setName("KodeDiagnosaSekunder4"); // NOI18N
        KodeDiagnosaSekunder4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeDiagnosaSekunder4KeyPressed(evt);
            }
        });
        FormInput.add(KodeDiagnosaSekunder4);
        KodeDiagnosaSekunder4.setBounds(710, 440, 75, 23);

        jLabel32.setText("Prosedur Utama :");
        jLabel32.setName("jLabel32"); // NOI18N
        FormInput.add(jLabel32);
        jLabel32.setBounds(0, 470, 145, 23);

        ProsedurUtama.setHighlighter(null);
        ProsedurUtama.setName("ProsedurUtama"); // NOI18N
        ProsedurUtama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ProsedurUtamaKeyPressed(evt);
            }
        });
        FormInput.add(ProsedurUtama);
        ProsedurUtama.setBounds(149, 470, 520, 23);

        KodeProsedurUtama.setHighlighter(null);
        KodeProsedurUtama.setName("KodeProsedurUtama"); // NOI18N
        KodeProsedurUtama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeProsedurUtamaKeyPressed(evt);
            }
        });
        FormInput.add(KodeProsedurUtama);
        KodeProsedurUtama.setBounds(710, 470, 75, 23);

        ProsedurSekunder1.setHighlighter(null);
        ProsedurSekunder1.setName("ProsedurSekunder1"); // NOI18N
        ProsedurSekunder1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ProsedurSekunder1KeyPressed(evt);
            }
        });
        FormInput.add(ProsedurSekunder1);
        ProsedurSekunder1.setBounds(149, 500, 520, 23);

        jLabel33.setText("Prosedur Sekunder 1 :");
        jLabel33.setName("jLabel33"); // NOI18N
        FormInput.add(jLabel33);
        jLabel33.setBounds(0, 500, 145, 23);

        KodeProsedurSekunder1.setHighlighter(null);
        KodeProsedurSekunder1.setName("KodeProsedurSekunder1"); // NOI18N
        KodeProsedurSekunder1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeProsedurSekunder1KeyPressed(evt);
            }
        });
        FormInput.add(KodeProsedurSekunder1);
        KodeProsedurSekunder1.setBounds(710, 500, 75, 23);

        jLabel34.setText("Prosedur Sekunder 2 :");
        jLabel34.setName("jLabel34"); // NOI18N
        FormInput.add(jLabel34);
        jLabel34.setBounds(0, 530, 145, 23);

        ProsedurSekunder2.setHighlighter(null);
        ProsedurSekunder2.setName("ProsedurSekunder2"); // NOI18N
        ProsedurSekunder2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ProsedurSekunder2KeyPressed(evt);
            }
        });
        FormInput.add(ProsedurSekunder2);
        ProsedurSekunder2.setBounds(149, 530, 520, 23);

        KodeProsedurSekunder2.setHighlighter(null);
        KodeProsedurSekunder2.setName("KodeProsedurSekunder2"); // NOI18N
        KodeProsedurSekunder2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeProsedurSekunder2KeyPressed(evt);
            }
        });
        FormInput.add(KodeProsedurSekunder2);
        KodeProsedurSekunder2.setBounds(710, 530, 75, 23);

        KodeProsedurSekunder3.setHighlighter(null);
        KodeProsedurSekunder3.setName("KodeProsedurSekunder3"); // NOI18N
        KodeProsedurSekunder3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeProsedurSekunder3KeyPressed(evt);
            }
        });
        FormInput.add(KodeProsedurSekunder3);
        KodeProsedurSekunder3.setBounds(710, 560, 75, 23);

        ProsedurSekunder3.setHighlighter(null);
        ProsedurSekunder3.setName("ProsedurSekunder3"); // NOI18N
        ProsedurSekunder3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ProsedurSekunder3KeyPressed(evt);
            }
        });
        FormInput.add(ProsedurSekunder3);
        ProsedurSekunder3.setBounds(149, 560, 520, 23);

        jLabel35.setText("Prosedur Sekunder 3 :");
        jLabel35.setName("jLabel35"); // NOI18N
        FormInput.add(jLabel35);
        jLabel35.setBounds(0, 560, 145, 23);

        label14.setText("Dokter P.J. :");
        label14.setName("label14"); // NOI18N
        label14.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label14);
        label14.setBounds(0, 40, 100, 23);

        KodeDokter.setEditable(false);
        KodeDokter.setName("KodeDokter"); // NOI18N
        KodeDokter.setPreferredSize(new java.awt.Dimension(80, 23));
        KodeDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeDokterKeyPressed(evt);
            }
        });
        FormInput.add(KodeDokter);
        KodeDokter.setBounds(104, 40, 141, 23);

        NamaDokter.setEditable(false);
        NamaDokter.setName("NamaDokter"); // NOI18N
        NamaDokter.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(NamaDokter);
        NamaDokter.setBounds(247, 40, 270, 23);

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
        BtnDokter.setBounds(519, 40, 28, 23);

        Kondisi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Hidup", "Meninggal" }));
        Kondisi.setName("Kondisi"); // NOI18N
        Kondisi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KondisiActionPerformed(evt);
            }
        });
        Kondisi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KondisiKeyPressed(evt);
            }
        });
        FormInput.add(Kondisi);
        Kondisi.setBounds(685, 40, 100, 23);

        jLabel12.setText("Kondisi Pasien Pulang :");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(551, 40, 130, 23);

        BtnDokter1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokter1.setMnemonic('2');
        BtnDokter1.setToolTipText("Alt+2");
        BtnDokter1.setName("BtnDokter1"); // NOI18N
        BtnDokter1.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnDokter1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokter1ActionPerformed(evt);
            }
        });
        FormInput.add(BtnDokter1);
        BtnDokter1.setBounds(180, 96, 28, 23);

        BtnDokter2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokter2.setMnemonic('2');
        BtnDokter2.setToolTipText("Alt+2");
        BtnDokter2.setName("BtnDokter2"); // NOI18N
        BtnDokter2.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnDokter2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokter2ActionPerformed(evt);
            }
        });
        FormInput.add(BtnDokter2);
        BtnDokter2.setBounds(212, 210, 28, 23);

        BtnDokter3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokter3.setMnemonic('2');
        BtnDokter3.setToolTipText("Alt+2");
        BtnDokter3.setName("BtnDokter3"); // NOI18N
        BtnDokter3.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnDokter3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokter3ActionPerformed(evt);
            }
        });
        FormInput.add(BtnDokter3);
        BtnDokter3.setBounds(212, 267, 28, 23);

        BtnDokter4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokter4.setMnemonic('2');
        BtnDokter4.setToolTipText("Alt+2");
        BtnDokter4.setName("BtnDokter4"); // NOI18N
        BtnDokter4.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnDokter4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokter4ActionPerformed(evt);
            }
        });
        FormInput.add(BtnDokter4);
        BtnDokter4.setBounds(212, 616, 28, 23);

        BtnDokter5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokter5.setMnemonic('2');
        BtnDokter5.setToolTipText("Alt+2");
        BtnDokter5.setName("BtnDokter5"); // NOI18N
        BtnDokter5.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnDokter5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokter5ActionPerformed(evt);
            }
        });
        FormInput.add(BtnDokter5);
        BtnDokter5.setBounds(212, 96, 28, 23);

        jLabel36.setText("Keadaan Pulang :");
        jLabel36.setName("jLabel36"); // NOI18N
        FormInput.add(jLabel36);
        jLabel36.setBounds(60, 650, 84, 30);

        CaraKeluar2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Atas Izin Dokter", "Pindah RS", "Pulang Atas Permintaan Sendiri", "Lainnya" }));
        CaraKeluar2.setName("CaraKeluar2"); // NOI18N
        CaraKeluar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CaraKeluar2ActionPerformed(evt);
            }
        });
        CaraKeluar2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CaraKeluar2KeyPressed(evt);
            }
        });
        FormInput.add(CaraKeluar2);
        CaraKeluar2.setBounds(480, 650, 181, 30);

        Keadaan2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Membaik", "Sembuh", "Keadaan Khusus", "Meninggal" }));
        Keadaan2.setName("Keadaan2"); // NOI18N
        Keadaan2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Keadaan2ActionPerformed(evt);
            }
        });
        Keadaan2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Keadaan2KeyPressed(evt);
            }
        });
        FormInput.add(Keadaan2);
        Keadaan2.setBounds(150, 650, 111, 30);

        KetKeluar2.setHighlighter(null);
        KetKeluar2.setName("KetKeluar2"); // NOI18N
        KetKeluar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KetKeluar2ActionPerformed(evt);
            }
        });
        KetKeluar2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KetKeluar2KeyPressed(evt);
            }
        });
        FormInput.add(KetKeluar2);
        KetKeluar2.setBounds(670, 650, 120, 24);

        KetKeadaanPulang2.setHighlighter(null);
        KetKeadaanPulang2.setName("KetKeadaanPulang2"); // NOI18N
        KetKeadaanPulang2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KetKeadaanPulang2ActionPerformed(evt);
            }
        });
        KetKeadaanPulang2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KetKeadaanPulang2KeyPressed(evt);
            }
        });
        FormInput.add(KetKeadaanPulang2);
        KetKeadaanPulang2.setBounds(270, 650, 120, 24);

        jLabel37.setText("Cara Keluar :");
        jLabel37.setName("jLabel37"); // NOI18N
        FormInput.add(jLabel37);
        jLabel37.setBounds(410, 650, 63, 30);

        BtnAmbilDataDiagnosaProsedur.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnAmbilDataDiagnosaProsedur.setMnemonic('S');
        BtnAmbilDataDiagnosaProsedur.setText("Ambil Data Diagnosa dan Prosedur");
        BtnAmbilDataDiagnosaProsedur.setToolTipText("Alt+S");
        BtnAmbilDataDiagnosaProsedur.setName("BtnAmbilDataDiagnosaProsedur"); // NOI18N
        BtnAmbilDataDiagnosaProsedur.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnAmbilDataDiagnosaProsedur.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAmbilDataDiagnosaProsedurActionPerformed(evt);
            }
        });
        BtnAmbilDataDiagnosaProsedur.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnAmbilDataDiagnosaProsedurKeyPressed(evt);
            }
        });
        FormInput.add(BtnAmbilDataDiagnosaProsedur);
        BtnAmbilDataDiagnosaProsedur.setBounds(800, 320, 290, 30);

        scrollInput.setViewportView(FormInput);

        PanelInput.add(scrollInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            isRawat();
            isPsien();
        } else {
            Valid.pindah(evt, TCari, BtnDokter);
        }
}//GEN-LAST:event_TNoRwKeyPressed

    private void TPasienKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TPasienKeyPressed
        Valid.pindah(evt, TCari, BtnSimpan);
}//GEN-LAST:event_TPasienKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (TNoRw.getText().equals("") || TNoRM.getText().equals("") || TPasien.getText().equals("")) {
            Valid.textKosong(TNoRw, "Pasien");
        } else if (KodeDokter.getText().equals("") || NamaDokter.getText().equals("")) {
            Valid.textKosong(BtnDokter, "Dokter Penanggung Jawab");
        } else if (Keluhan.getText().equals("")) {
            Valid.textKosong(Keluhan, "Keluhan utama riwayat penyakit yang postif");
        } else if (JalannyaPenyakit.getText().equals("")) {
            Valid.textKosong(JalannyaPenyakit, "Jalannya penyakit selama perawatan");
        } else if (DiagnosaUtama.getText().equals("")) {
            Valid.textKosong(DiagnosaUtama, "Diagnosa Utama");
        } else {
            if (Sequel.menyimpantf("resume_pasien", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "No.Rawat", 30, new String[]{
                TNoRw.getText(), KodeDokter.getText(), Keluhan.getText(), JalannyaPenyakit.getText(), PemeriksaanPenunjang.getText(), HasilLaborat.getText(),
                DiagnosaUtama.getText(), KodeDiagnosaUtama.getText(), DiagnosaSekunder1.getText(), KodeDiagnosaSekunder1.getText(), DiagnosaSekunder2.getText(),
                KodeDiagnosaSekunder2.getText(), DiagnosaSekunder3.getText(), KodeDiagnosaSekunder3.getText(), DiagnosaSekunder4.getText(), KodeDiagnosaSekunder4.getText(),
                ProsedurUtama.getText(), KodeProsedurUtama.getText(), ProsedurSekunder1.getText(), KodeProsedurSekunder1.getText(), ProsedurSekunder2.getText(),
                KodeProsedurSekunder2.getText(), ProsedurSekunder3.getText(), KodeProsedurSekunder3.getText(), Kondisi.getSelectedItem().toString(), Obat2an.getText(), Keadaan2.getSelectedItem().toString(), KetKeadaanPulang2.getText(), CaraKeluar2.getSelectedItem().toString(), KetKeluar2.getText()
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
            Valid.pindah(evt, Obat2an, BtnBatal);
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
        if (tbObat.getSelectedRow() > -1) {
            if (akses.getkode().equals("Admin Utama")) {
                hapus();
            } else {
                if (KodeDokter.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString())) {
                    hapus();
                } else {
                    JOptionPane.showMessageDialog(null, "Hanya bisa dihapus oleh dokter yang bersangkutan..!!");
                }
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
        }

}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnHapusActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if (TNoRw.getText().equals("") || TNoRM.getText().equals("") || TPasien.getText().equals("")) {
            Valid.textKosong(TNoRw, "Pasien");
        } else if (KodeDokter.getText().equals("") || NamaDokter.getText().equals("")) {
            Valid.textKosong(BtnDokter, "Dokter Penanggung Jawab");
        } else if (Keluhan.getText().equals("")) {
            Valid.textKosong(Keluhan, "Keluhan utama riwayat penyakit yang postif");
        } else if (JalannyaPenyakit.getText().equals("")) {
            Valid.textKosong(JalannyaPenyakit, "Jalannya penyakit selama perawatan");
        } else if (DiagnosaUtama.getText().equals("")) {
            Valid.textKosong(DiagnosaUtama, "Diagnosa Utama");
        } else {
            if (tbObat.getSelectedRow() > -1) {
                if (akses.getkode().equals("Admin Utama")) {
                    ganti();
                } else {
                    if (KodeDokter.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString())) {
                        ganti();
                    } else {
                        JOptionPane.showMessageDialog(null, "Hanya bisa diganti oleh dokter yang bersangkutan..!!");
                    }
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
        carikeluhan.dispose();
        carilaborat.dispose();
        cariobat.dispose();
        cariradiologi.dispose();
        penyakit.dispose();
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
        if (!TCari.getText().trim().equals("")) {
            BtnCariActionPerformed(evt);
        }
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
            if (TCari.getText().equals("")) {
                Valid.MyReportqry("rptDataResumePasien.jasper", "report", "::[ Data Resume Pasien ]::",
                        "select reg_periksa.tgl_registrasi,reg_periksa.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien, "
                        + "resume_pasien.kd_dokter,dokter.nm_dokter,resume_pasien.kondisi_pulang,resume_pasien.keluhan_utama,resume_pasien.jalannya_penyakit, "
                        + "resume_pasien.pemeriksaan_penunjang,resume_pasien.hasil_laborat,resume_pasien.diagnosa_utama,resume_pasien.kd_diagnosa_utama, "
                        + "resume_pasien.diagnosa_sekunder,resume_pasien.kd_diagnosa_sekunder,resume_pasien.diagnosa_sekunder2,resume_pasien.kd_diagnosa_sekunder2, "
                        + "resume_pasien.diagnosa_sekunder3,resume_pasien.kd_diagnosa_sekunder3,resume_pasien.diagnosa_sekunder4,resume_pasien.kd_diagnosa_sekunder4, "
                        + "resume_pasien.prosedur_utama,resume_pasien.kd_prosedur_utama,resume_pasien.prosedur_sekunder,resume_pasien.kd_prosedur_sekunder, "
                        + "resume_pasien.prosedur_sekunder2,resume_pasien.kd_prosedur_sekunder2,resume_pasien.prosedur_sekunder3,resume_pasien.kd_prosedur_sekunder3, "
                        + "resume_pasien.obat_pulang,resume_pasien.keadaan,resume_pasien.ket_keadaan,resume_pasien.cara_keluar,resume_pasien.ket_keluar from resume_pasien inner join reg_periksa on resume_pasien.no_rawat=reg_periksa.no_rawat  "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis inner join dokter on resume_pasien.kd_dokter=dokter.kd_dokter "
                        + "where reg_periksa.tgl_registrasi between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' "
                        + "order by reg_periksa.tgl_registrasi", param);
            } else {
                Valid.MyReportqry("rptDataResumePasien.jasper", "report", "::[ Data Resume Pasien ]::",
                        "select reg_periksa.tgl_registrasi,reg_periksa.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien, "
                        + "resume_pasien.kd_dokter,dokter.nm_dokter,resume_pasien.kondisi_pulang,resume_pasien.keluhan_utama,resume_pasien.jalannya_penyakit, "
                        + "resume_pasien.pemeriksaan_penunjang,resume_pasien.hasil_laborat,resume_pasien.diagnosa_utama,resume_pasien.kd_diagnosa_utama, "
                        + "resume_pasien.diagnosa_sekunder,resume_pasien.kd_diagnosa_sekunder,resume_pasien.diagnosa_sekunder2,resume_pasien.kd_diagnosa_sekunder2, "
                        + "resume_pasien.diagnosa_sekunder3,resume_pasien.kd_diagnosa_sekunder3,resume_pasien.diagnosa_sekunder4,resume_pasien.kd_diagnosa_sekunder4, "
                        + "resume_pasien.prosedur_utama,resume_pasien.kd_prosedur_utama,resume_pasien.prosedur_sekunder,resume_pasien.kd_prosedur_sekunder, "
                        + "resume_pasien.prosedur_sekunder2,resume_pasien.kd_prosedur_sekunder2,resume_pasien.prosedur_sekunder3,resume_pasien.kd_prosedur_sekunder3, "
                        + "resume_pasien.obat_pulang,resume_pasien.keadaan,resume_pasien.ket_keadaan,resume_pasien.cara_keluar,resume_pasien.ket_keluar from resume_pasien inner join reg_periksa on resume_pasien.no_rawat=reg_periksa.no_rawat  "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis inner join dokter on resume_pasien.kd_dokter=dokter.kd_dokter "
                        + "where reg_periksa.tgl_registrasi between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' and "
                        + "(reg_periksa.no_rkm_medis like '%" + TCari.getText().trim() + "%' or pasien.nm_pasien like '%" + TCari.getText().trim() + "%' or "
                        + "resume_pasien.kd_dokter like '%" + TCari.getText().trim() + "%' or dokter.nm_dokter like '%" + TCari.getText().trim() + "%' or "
                        + "resume_pasien.kondisi_pulang like '%" + TCari.getText().trim() + "%' or resume_pasien.kd_diagnosa_utama like '%" + TCari.getText().trim() + "%' or "
                        + "resume_pasien.diagnosa_utama like '%" + TCari.getText().trim() + "%' or resume_pasien.prosedur_utama like '%" + TCari.getText().trim() + "%' or "
                        + "reg_periksa.no_rawat like '%" + TCari.getText().trim() + "%' or resume_pasien.kd_prosedur_utama like '%" + TCari.getText().trim() + "%') "
                        + "order by reg_periksa.tgl_registrasi", param);
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

    private void TNoRMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRMKeyPressed
        // Valid.pindah(evt, TNm, BtnSimpan);
}//GEN-LAST:event_TNoRMKeyPressed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if (tabMode.getRowCount() != 0) {
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
}//GEN-LAST:event_tbObatMouseClicked

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyPressed
        if (tabMode.getRowCount() != 0) {
            if ((evt.getKeyCode() == KeyEvent.VK_ENTER) || (evt.getKeyCode() == KeyEvent.VK_UP) || (evt.getKeyCode() == KeyEvent.VK_DOWN)) {
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            } else if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
                try {
                    ChkInput.setSelected(true);
                    isForm();
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
}//GEN-LAST:event_tbObatKeyPressed

    private void DiagnosaSekunder2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosaSekunder2KeyPressed
        Valid.pindah(evt, KodeDiagnosaSekunder1, KodeDiagnosaSekunder2);
    }//GEN-LAST:event_DiagnosaSekunder2KeyPressed

    private void DiagnosaUtamaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosaUtamaKeyPressed
        Valid.pindah(evt, HasilLaborat, KodeDiagnosaUtama);
    }//GEN-LAST:event_DiagnosaUtamaKeyPressed

    private void DiagnosaSekunder3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosaSekunder3KeyPressed
        Valid.pindah(evt, KodeDiagnosaSekunder2, KodeDiagnosaSekunder3);
    }//GEN-LAST:event_DiagnosaSekunder3KeyPressed

    private void DiagnosaSekunder4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosaSekunder4KeyPressed
        Valid.pindah(evt, KodeDiagnosaSekunder3, KodeDiagnosaSekunder4);
    }//GEN-LAST:event_DiagnosaSekunder4KeyPressed

    private void DiagnosaSekunder1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosaSekunder1KeyPressed
        Valid.pindah(evt, KodeDiagnosaUtama, KodeDiagnosaSekunder1);
    }//GEN-LAST:event_DiagnosaSekunder1KeyPressed

    private void KodeDiagnosaUtamaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeDiagnosaUtamaKeyPressed
        Valid.pindah(evt, DiagnosaUtama, DiagnosaSekunder1);
    }//GEN-LAST:event_KodeDiagnosaUtamaKeyPressed

    private void KodeDiagnosaSekunder1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeDiagnosaSekunder1KeyPressed
        Valid.pindah(evt, DiagnosaSekunder1, DiagnosaSekunder2);
    }//GEN-LAST:event_KodeDiagnosaSekunder1KeyPressed

    private void KodeDiagnosaSekunder2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeDiagnosaSekunder2KeyPressed
        Valid.pindah(evt, DiagnosaSekunder2, DiagnosaSekunder3);
    }//GEN-LAST:event_KodeDiagnosaSekunder2KeyPressed

    private void KodeDiagnosaSekunder3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeDiagnosaSekunder3KeyPressed
        Valid.pindah(evt, DiagnosaSekunder3, DiagnosaSekunder4);
    }//GEN-LAST:event_KodeDiagnosaSekunder3KeyPressed

    private void KodeDiagnosaSekunder4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeDiagnosaSekunder4KeyPressed
        Valid.pindah(evt, DiagnosaSekunder4, ProsedurUtama);
    }//GEN-LAST:event_KodeDiagnosaSekunder4KeyPressed

    private void ProsedurUtamaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ProsedurUtamaKeyPressed
        Valid.pindah(evt, KodeDiagnosaSekunder4, KodeProsedurUtama);
    }//GEN-LAST:event_ProsedurUtamaKeyPressed

    private void KodeProsedurUtamaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeProsedurUtamaKeyPressed
        Valid.pindah(evt, ProsedurUtama, ProsedurSekunder1);
    }//GEN-LAST:event_KodeProsedurUtamaKeyPressed

    private void ProsedurSekunder1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ProsedurSekunder1KeyPressed
        Valid.pindah(evt, KodeProsedurUtama, KodeProsedurSekunder1);
    }//GEN-LAST:event_ProsedurSekunder1KeyPressed

    private void KodeProsedurSekunder1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeProsedurSekunder1KeyPressed
        Valid.pindah(evt, ProsedurSekunder1, ProsedurSekunder2);
    }//GEN-LAST:event_KodeProsedurSekunder1KeyPressed

    private void ProsedurSekunder2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ProsedurSekunder2KeyPressed
        Valid.pindah(evt, KodeProsedurSekunder1, KodeProsedurSekunder2);
    }//GEN-LAST:event_ProsedurSekunder2KeyPressed

    private void KodeProsedurSekunder2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeProsedurSekunder2KeyPressed
        Valid.pindah(evt, ProsedurSekunder2, ProsedurSekunder3);
    }//GEN-LAST:event_KodeProsedurSekunder2KeyPressed

    private void KodeProsedurSekunder3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeProsedurSekunder3KeyPressed
        Valid.pindah(evt, ProsedurSekunder3, Obat2an);
    }//GEN-LAST:event_KodeProsedurSekunder3KeyPressed

    private void ProsedurSekunder3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ProsedurSekunder3KeyPressed
        Valid.pindah(evt, KodeProsedurSekunder2, KodeProsedurSekunder3);
    }//GEN-LAST:event_ProsedurSekunder3KeyPressed

    private void KodeDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeDokterKeyPressed
        Valid.pindah(evt, TCari, Kondisi);
    }//GEN-LAST:event_KodeDokterKeyPressed

    private void BtnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokterActionPerformed
        dokter.emptTeks();
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    }//GEN-LAST:event_BtnDokterActionPerformed

    private void BtnDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnDokterKeyPressed
        Valid.pindah(evt, TCari, Kondisi);
    }//GEN-LAST:event_BtnDokterKeyPressed

    private void KondisiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KondisiKeyPressed
        Valid.pindah(evt, KodeDokter, Keluhan);
    }//GEN-LAST:event_KondisiKeyPressed

    private void KeluhanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KeluhanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (evt.isShiftDown()) {
                JalannyaPenyakit.requestFocus();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            Kondisi.requestFocus();
        }
    }//GEN-LAST:event_KeluhanKeyPressed

    private void JalannyaPenyakitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JalannyaPenyakitKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (evt.isShiftDown()) {
                PemeriksaanPenunjang.requestFocus();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            Keluhan.requestFocus();
        }
    }//GEN-LAST:event_JalannyaPenyakitKeyPressed

    private void PemeriksaanPenunjangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PemeriksaanPenunjangKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (evt.isShiftDown()) {
                HasilLaborat.requestFocus();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            JalannyaPenyakit.requestFocus();
        }
    }//GEN-LAST:event_PemeriksaanPenunjangKeyPressed

    private void HasilLaboratKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_HasilLaboratKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (evt.isShiftDown()) {
                DiagnosaUtama.requestFocus();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            PemeriksaanPenunjang.requestFocus();
        }
    }//GEN-LAST:event_HasilLaboratKeyPressed

    private void Obat2anKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Obat2anKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            if (evt.isShiftDown()) {
                BtnSimpan.requestFocus();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            KodeProsedurSekunder3.requestFocus();
        }
    }//GEN-LAST:event_Obat2anKeyPressed

    private void BtnDokter1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokter1ActionPerformed
        if (TNoRw.getText().equals("") && TNoRM.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Pasien masih kosong...!!!");
        } else {
            carikeluhan.setNoRawat(TNoRw.getText());
            carikeluhan.tampil();
            carikeluhan.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            carikeluhan.setLocationRelativeTo(internalFrame1);
            carikeluhan.setVisible(true);
        }
    }//GEN-LAST:event_BtnDokter1ActionPerformed

    private void BtnDokter2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokter2ActionPerformed
        if (TNoRw.getText().equals("") && TNoRM.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Pasien masih kosong...!!!");
        } else {
            cariradiologi.setNoRawat(TNoRw.getText());
            cariradiologi.tampil();
            cariradiologi.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            cariradiologi.setLocationRelativeTo(internalFrame1);
            cariradiologi.setVisible(true);
        }
    }//GEN-LAST:event_BtnDokter2ActionPerformed

    private void BtnDokter3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokter3ActionPerformed
        if (TNoRw.getText().equals("") && TNoRM.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Pasien masih kosong...!!!");
        } else {
            carilaborat.setNoRawat(TNoRw.getText());
            carilaborat.tampil();
            carilaborat.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            carilaborat.setLocationRelativeTo(internalFrame1);
            carilaborat.setVisible(true);
        }
    }//GEN-LAST:event_BtnDokter3ActionPerformed

    private void MnInputDiagnosaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnInputDiagnosaActionPerformed
        if (TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu pasien...!!!");
            TCari.requestFocus();
        } else {
            penyakit.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            penyakit.setLocationRelativeTo(internalFrame1);
            penyakit.isCek();
            penyakit.setNoRm(TNoRw.getText(), DTPCari1.getDate(), DTPCari2.getDate(), Sequel.cariIsi("select reg_periksa.status_lanjut from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText()));
            penyakit.panelDiagnosa1.tampil();
            penyakit.setVisible(true);
        }
    }//GEN-LAST:event_MnInputDiagnosaActionPerformed

    private void BtnDokter4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokter4ActionPerformed
        if (TNoRw.getText().equals("") && TNoRM.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Pasien masih kosong...!!!");
        } else {
            cariobat.setNoRawat(TNoRw.getText());
            cariobat.tampil();
            cariobat.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            cariobat.setLocationRelativeTo(internalFrame1);
            cariobat.setVisible(true);
        }
    }//GEN-LAST:event_BtnDokter4ActionPerformed

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void ppBerkasDigitalBtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppBerkasDigitalBtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() > -1) {
                if (!tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString().equals("")) {
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    DlgBerkasRawat berkas = new DlgBerkasRawat(null, true);
                    berkas.setJudul("::[ Berkas Digital Perawatan ]::", "berkasrawat/pages");
                    try {
                        if (akses.gethapus_berkas_digital_perawatan() == true) {
                            berkas.loadURL("http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/" + "berkasrawat/login2.php?act=login&usere=" + koneksiDB.USERHYBRIDWEB() + "&passwordte=" + koneksiDB.PASHYBRIDWEB() + "&no_rawat=" + tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
                        } else {
                            berkas.loadURL("http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/" + "berkasrawat/login2nonhapus.php?act=login&usere=" + koneksiDB.USERHYBRIDWEB() + "&passwordte=" + koneksiDB.PASHYBRIDWEB() + "&no_rawat=" + tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
                        }
                    } catch (Exception ex) {
                        System.out.println("Notifikasi : " + ex);
                    }

                    berkas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                    berkas.setLocationRelativeTo(internalFrame1);
                    berkas.setVisible(true);
                    this.setCursor(Cursor.getDefaultCursor());
                }
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_ppBerkasDigitalBtnPrintActionPerformed

    private void MnSPBKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnSPBKActionPerformed
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
            variabel = "Atas Persetujuan Dokter";
            if (Sequel.cariInteger("select count(kamar_inap.no_rawat) from kamar_inap where kamar_inap.no_rawat=?", TNoRw.getText()) > 0) {
                variabel = "MRS";
            }
            if (Sequel.cariInteger("select count(rujuk.no_rawat) from rujuk where rujuk.no_rawat=?", TNoRw.getText()) > 0) {
                variabel = "Dirujuk";
            }
            param.put("carapulang", variabel);
            variabel = Sequel.cariIsi("select bridging_sep.tujuankunjungan from bridging_sep where bridging_sep.no_rawat=?", TNoRw.getText());
            param.put("tujuankunjungan", "- " + (variabel.equals("0") ? "Konsultasi dokter(pertama)" : "Kunjungan Kontrol(ulangan)"));
            variabel = Sequel.cariIsi("select bridging_sep.flagprosedur from bridging_sep where bridging_sep.no_rawat=?", TNoRw.getText());
            param.put("flagprosedur", variabel.replaceAll("0", "- Prosedur Tidak Berkelanjutan").replaceAll("1", "- Prosedur dan Terapi Berkelanjutan"));
            if (tbObat.getSelectedRow() > -1) {
                finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
                param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString() + "\nID " + (finger.equals("") ? tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString() : finger) + "\n" + Valid.SetTgl3(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()));
            }
            param.put("fingerpasien", Sequel.cariIsi("select sha1(sidikjaripasien.sidikjari) from sidikjaripasien where sidikjaripasien.no_rkm_medis=?", TNoRM.getText()));

            try {
                ps = koneksi.prepareStatement(
                        "select resume_pasien.keluhan_utama,resume_pasien.diagnosa_utama,resume_pasien.kd_diagnosa_utama,resume_pasien.diagnosa_sekunder,resume_pasien.kd_diagnosa_sekunder,"
                        + "resume_pasien.diagnosa_sekunder2,resume_pasien.kd_diagnosa_sekunder2,resume_pasien.diagnosa_sekunder3,resume_pasien.kd_diagnosa_sekunder3,resume_pasien.diagnosa_sekunder4,"
                        + "resume_pasien.kd_diagnosa_sekunder4,resume_pasien.prosedur_utama,resume_pasien.kd_prosedur_utama,resume_pasien.prosedur_sekunder,resume_pasien.kd_prosedur_sekunder "
                        + "from resume_pasien where resume_pasien.no_rawat=?");
                try {
                    ps.setString(1, TNoRw.getText());
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        param.put("anamnesa", rs.getString("keluhan_utama"));
                        param.put("diagnosa1", rs.getString("diagnosa_utama"));
                        param.put("icdx1", rs.getString("kd_diagnosa_utama"));
                        param.put("diagnosa2", rs.getString("diagnosa_sekunder"));
                        param.put("icdx2", rs.getString("kd_diagnosa_sekunder"));
                        param.put("diagnosa3", rs.getString("diagnosa_sekunder2"));
                        param.put("icdx3", rs.getString("kd_diagnosa_sekunder2"));
                        param.put("diagnosa4", rs.getString("diagnosa_sekunder3"));
                        param.put("icdx4", rs.getString("kd_diagnosa_sekunder3"));
                        param.put("prosedur1", rs.getString("prosedur_utama"));
                        param.put("icd91", rs.getString("kd_prosedur_utama"));
                        param.put("prosedur2", rs.getString("prosedur_sekunder"));
                        param.put("icd92", rs.getString("kd_prosedur_sekunder"));
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

            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            Valid.MyReportqry("rptSBPK.jasper", "report", "::[ Surat Bukti Pelayanan Kesehatan ]::",
                    "select reg_periksa.no_reg,reg_periksa.no_rawat,reg_periksa.tgl_registrasi,"
                    + "reg_periksa.jam_reg, reg_periksa.kd_dokter,dokter.nm_dokter,reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur)as umur,poliklinik.nm_poli,"
                    + "reg_periksa.p_jawab,reg_periksa.almt_pj,reg_periksa.hubunganpj,reg_periksa.biaya_reg,"
                    + "reg_periksa.stts_daftar,penjab.png_jawab,pasien.no_peserta,pasien.tgl_lahir "
                    + "from reg_periksa inner join dokter inner join pasien inner join poliklinik inner join penjab "
                    + "on reg_periksa.kd_dokter=dokter.kd_dokter and reg_periksa.no_rkm_medis=pasien.no_rkm_medis and reg_periksa.kd_pj=penjab.kd_pj and reg_periksa.kd_poli=poliklinik.kd_poli "
                    + "where reg_periksa.no_rawat='" + TNoRw.getText() + "' ", param);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }//GEN-LAST:event_MnSPBKActionPerformed

    private void MnSPBK2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnSPBK2ActionPerformed
        if (tbObat.getSelectedRow() > -1) {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            param.put("norawat", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            variabel = "Atas Persetujuan Dokter";
            if (Sequel.cariInteger("select count(kamar_inap.no_rawat) from kamar_inap where kamar_inap.no_rawat=?", TNoRw.getText()) > 0) {
                variabel = "MRS";
            }
            if (Sequel.cariInteger("select count(rujuk.no_rawat) from rujuk where rujuk.no_rawat=?", TNoRw.getText()) > 0) {
                variabel = "Dirujuk";
            }
            param.put("carapulang", variabel);
            variabel = Sequel.cariIsi("select bridging_sep.tujuankunjungan from bridging_sep where bridging_sep.no_rawat=?", TNoRw.getText());
            param.put("tujuankunjungan", "- " + (variabel.equals("0") ? "Konsultasi dokter(pertama)" : "Kunjungan Kontrol(ulangan)"));
            variabel = Sequel.cariIsi("select bridging_sep.flagprosedur from bridging_sep where bridging_sep.no_rawat=?", TNoRw.getText());
            param.put("flagprosedur", variabel.replaceAll("0", "- Prosedur Tidak Berkelanjutan").replaceAll("1", "- Prosedur dan Terapi Berkelanjutan"));

            tanggal = "";
            if (Sequel.cariIsi("select reg_periksa.status_lanjut from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText()).equals("Ralan")) {
                param.put("ruang", Sequel.cariIsi("select poliklinik.nm_poli from poliklinik inner join reg_periksa on reg_periksa.kd_poli=poliklinik.kd_poli where reg_periksa.no_rawat=?", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString()));
                tanggal = Sequel.cariIsi("select DATE_FORMAT(tgl_registrasi, '%d-%m-%Y') from reg_periksa where no_rawat=?", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
                param.put("tanggalkeluar", tanggal);
            } else {
                param.put("ruang", Sequel.cariIsi("select nm_bangsal from bangsal inner join kamar inner join kamar_inap on bangsal.kd_bangsal=kamar.kd_bangsal and kamar_inap.kd_kamar=kamar.kd_kamar where no_rawat=? order by tgl_masuk desc limit 1 ", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString()));
                tanggal = Sequel.cariIsi("select DATE_FORMAT(tgl_keluar, '%d-%m-%Y') from kamar_inap where no_rawat=? order by tgl_keluar desc limit 1 ", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
                param.put("tanggalkeluar", tanggal);
            }
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString() + "\nID " + (finger.equals("") ? tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString() : finger) + "\n" + tanggal);
            param.put("fingerpasien", Sequel.cariIsi("select sha1(sidikjaripasien.sidikjari) from sidikjaripasien where sidikjaripasien.no_rkm_medis=?", TNoRM.getText()));

            Valid.MyReport("rptSBPK3.jasper", "report", "::[ Surat Bukti Pelayanan Kesehatan ]::", param);
        }
    }//GEN-LAST:event_MnSPBK2ActionPerformed

    private void BtnDokter5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokter5ActionPerformed
        if (TNoRw.getText().equals("") && TNoRM.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Pasien masih kosong...!!!");
        } else {
            caripemeriksaan.setNoRawat(TNoRw.getText());
            caripemeriksaan.tampil();
            caripemeriksaan.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
            caripemeriksaan.setLocationRelativeTo(internalFrame1);
            caripemeriksaan.setVisible(true);
        }
    }//GEN-LAST:event_BtnDokter5ActionPerformed

    private void Keadaan2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Keadaan2KeyPressed
        Valid.pindah(evt, Edukasi, KetKeadaanPulang2);
    }//GEN-LAST:event_Keadaan2KeyPressed

    private void KetKeadaanPulang2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KetKeadaanPulang2KeyPressed
        Valid.pindah(evt, Keadaan2, CaraKeluar2);
    }//GEN-LAST:event_KetKeadaanPulang2KeyPressed

    private void CaraKeluar2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CaraKeluar2KeyPressed
        Valid.pindah(evt, KetKeadaanPulang2, KetKeluar2);
    }//GEN-LAST:event_CaraKeluar2KeyPressed

    private void KetKeluar2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KetKeluar2KeyPressed
        Valid.pindah(evt, CaraKeluar2, DIlanjutkan);
    }//GEN-LAST:event_KetKeluar2KeyPressed

    private void Keadaan2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Keadaan2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Keadaan2ActionPerformed

    private void KetKeadaanPulang2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KetKeadaanPulang2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_KetKeadaanPulang2ActionPerformed

    private void CaraKeluar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CaraKeluar2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CaraKeluar2ActionPerformed

    private void KetKeluar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KetKeluar2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_KetKeluar2ActionPerformed

    private void DiagnosaUtamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DiagnosaUtamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DiagnosaUtamaActionPerformed

    private void KondisiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KondisiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_KondisiActionPerformed

    private void MnLaporanResumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnLaporanResumeActionPerformed
        if (tbObat.getSelectedRow() > -1) {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        //    param.put("logo2", Sequel.cariGambar("select setting.logo2 from setting"));
            param.put("norawat", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            tanggal = "";
            if (Sequel.cariIsi("select reg_periksa.status_lanjut from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText()).equals("Ralan")) {
                param.put("ruang", Sequel.cariIsi("select poliklinik.nm_poli from poliklinik inner join reg_periksa on reg_periksa.kd_poli=poliklinik.kd_poli where reg_periksa.no_rawat=?", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString()));
                tanggal = Sequel.cariIsi("select DATE_FORMAT(tgl_registrasi, '%d-%m-%Y') from reg_periksa where no_rawat=?", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
                param.put("tanggalkeluar", tanggal);
            } else {
                param.put("ruang", Sequel.cariIsi("select nm_bangsal from bangsal inner join kamar inner join kamar_inap on bangsal.kd_bangsal=kamar.kd_bangsal and kamar_inap.kd_kamar=kamar.kd_kamar where no_rawat=? order by tgl_masuk desc limit 1 ", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString()));
                tanggal = Sequel.cariIsi("select DATE_FORMAT(tgl_keluar, '%d-%m-%Y') from kamar_inap where no_rawat=? order by tgl_keluar desc limit 1 ", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
                param.put("tanggalkeluar", tanggal);
            }
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString() + "\nID " + (finger.equals("") ? tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString() : finger) + "\n" + tanggal);

            Valid.MyReport("rptLaporanResume.jasper", "report", "::[ Laporan Resume Pasien ]::", param);
        }
    }//GEN-LAST:event_MnLaporanResumeActionPerformed

    private void UploadResumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UploadResumeActionPerformed
        FileName = "RESUMERJ_" + tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString().replaceAll("/", "") + "_" + tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString().trim() + "_" + tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString().replaceAll(" ", "");
        CreatePDF(FileName);
        String filePath = "tmpPDF/" + FileName;
        UploadPDF(FileName, "berkasrawat/pages/upload/");
        HapusPDF();
        ppBerkasDigitalBtnPrintActionPerformed(evt);// TODO add your handling code here:
    }//GEN-LAST:event_UploadResumeActionPerformed

    private void BtnClosePhraseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnClosePhraseActionPerformed
        Phrase.setText("");
        WindowPhrase.dispose();
    }//GEN-LAST:event_BtnClosePhraseActionPerformed

    private void BtnSimpanTandaTanganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanTandaTanganActionPerformed
        if (tbObat.getSelectedRow() > -1) {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
        //    param.put("logo2", Sequel.cariGambar("select setting.logo2 from setting"));
            param.put("logobsre", Sequel.cariGambar("select gambar.bsre from gambar"));
            param.put("norawat", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            param.put("finger", "#");
            tanggal = "";
            if (Sequel.cariIsi("select reg_periksa.status_lanjut from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText()).equals("Ralan")) {
                param.put("ruang", Sequel.cariIsi("select poliklinik.nm_poli from poliklinik inner join reg_periksa on reg_periksa.kd_poli=poliklinik.kd_poli where reg_periksa.no_rawat=?", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString()));
                tanggal = Sequel.cariIsi("select DATE_FORMAT(tgl_registrasi, '%d-%m-%Y') from reg_periksa where no_rawat=?", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
                param.put("tanggalkeluar", tanggal);
            } else {
                param.put("ruang", Sequel.cariIsi("select nm_bangsal from bangsal inner join kamar inner join kamar_inap on bangsal.kd_bangsal=kamar.kd_bangsal and kamar_inap.kd_kamar=kamar.kd_kamar where no_rawat=? order by tgl_masuk desc limit 1 ", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString()));
                tanggal = Sequel.cariIsi("select DATE_FORMAT(tgl_keluar, '%d-%m-%Y') from kamar_inap where no_rawat=? order by tgl_keluar desc limit 1 ", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
                param.put("tanggalkeluar", tanggal);
            }

            Valid.MyReportPDF2("rptLaporanResumeTte.jasper", "report", "::[ Laporan Resume Pasien ]::", param);
            File f = new File("./report/rptLaporanResumeTte.pdf");

            // ========== ENHANCED VALIDASI NO_SEP DAN TANGGAL ==========
            String no_sep = "";
            String tanggal = "";
            String tanggal_registrasi = "";
            boolean lanjutkanProses = true;
            boolean usedRegistrationDate = false;
            boolean usedNoRawatAsFallback = false;

            try {
                // Query gabungan untuk mendapatkan data SEP dan registrasi sekaligus
                String sqlQuery = "SELECT bs.no_sep, bs.tglsep, rp.tgl_registrasi "
                        + "FROM reg_periksa rp "
                        + "LEFT JOIN bridging_sep bs ON bs.no_rawat = rp.no_rawat AND bs.jnspelayanan = '2' "
                        + "WHERE rp.no_rawat = ?";

                try ( PreparedStatement psSep = koneksi.prepareStatement(sqlQuery)) {
                    psSep.setString(1, TNoRw.getText());
                    try ( ResultSet rsSep = psSep.executeQuery()) {
                        if (rsSep.next()) {
                            no_sep = rsSep.getString("no_sep");
                            tanggal = rsSep.getString("tglsep");
                            tanggal_registrasi = rsSep.getString("tgl_registrasi");

                            System.out.println("Data ditemukan - SEP: " + no_sep + ", Tanggal SEP: " + tanggal + ", Tanggal Registrasi: " + tanggal_registrasi);
                        } else {
                            System.out.println("Warning: No registration data found for no_rawat: " + TNoRw.getText());
                        }
                    }
                }

                // Cek jika no_sep null atau kosong
                if (no_sep == null || no_sep.trim().isEmpty()) {
                    System.out.println("Warning: No SEP found for no_rawat: " + TNoRw.getText());

                    // Tampilkan dialog konfirmasi ke user dengan informasi tanggal
                    String tanggalInfo = "";
                    if (tanggal_registrasi != null && !tanggal_registrasi.trim().isEmpty()) {
                        tanggalInfo = "\n• Tanggal registrasi tersedia: " + tanggal_registrasi;
                    } else {
                        tanggalInfo = "\n• Akan menggunakan tanggal sistem";
                    }

                    int pilihan = JOptionPane.showConfirmDialog(
                            null,
                            "Nomor SEP tidak ditemukan untuk No. Rawat: " + TNoRw.getText() + "\n\n"
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
                        no_sep = TNoRw.getText().trim().replaceAll("/", "");
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
                    tanggal_registrasi = Sequel.cariIsi("select tgl_registrasi from reg_periksa where no_rawat = ?", TNoRw.getText());
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
                    no_sep = TNoRw.getText().trim().replaceAll("/", "");
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
                            + "\"linkQR\":\"Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + " dan ditandatangani secara elektronik oleh " + NamaDokter.getText() + " ID " + KodeDokter.getText() + " Tanggal " + tanggal + "\","
                            + "\"width\":\"55\","
                            + "\"height\":\"55\","
                            + "\"tag_koordinat\":\"#\""
                            + "}";

                    System.out.println("URL Akses file :" + koneksiDB.URLAKSESFILEESIGN());
                    System.out.println("JSON Dikirim :" + json);
                    System.out.println("No SEP yang digunakan: " + no_sep);
                    System.out.println("Tanggal yang digunakan: " + tanggal + (usedRegistrationDate ? " (dari registrasi)" : usedNoRawatAsFallback ? " (sistem/fallback)" : " (dari SEP)"));

                    // Set request entity
                    post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

                    // Execute request
                    try ( CloseableHttpResponse response = httpClient.execute(post)) {
                        System.out.println("Response Status : " + response.getCode());
                        json = EntityUtils.toString(response.getEntity());
                        root = mapper.readTree(json);
                        String fileName = "ResumeRJ" + TNoRw.getText().trim().replaceAll("/", "") + "_" + TNoRM.getText().trim().replaceAll(" ", "") + "_" + TPasien.getText().trim().replaceAll(" ", "") + ".pdf";
                        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                        if (response.getCode() == 200) {
                            try ( FileOutputStream fos = new FileOutputStream(new File("ResumeRJ" + TNoRw.getText().trim().replaceAll("/", "") + "_" + TNoRM.getText().trim().replaceAll(" ", "") + "_" + TPasien.getText().trim().replaceAll(" ", "") + ".pdf"))) {
                                byte[] fileBytes = Base64.getDecoder().decode(root.path("response").asText());
                                fos.write(fileBytes);

                                // Tampilkan notifikasi SEBELUM WindowPhrase ditutup
                                JOptionPane.showMessageDialog(null, "Proses tanda tangan berhasil...");
                                WindowPhrase.dispose();

                                String kode = Sequel.cariIsi("select kode from master_berkas_digital where nama like 'resume'");
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String tanggalSekarang = sdf.format(new Date());

                                // Upload file dan ambil URL - dengan tanggal yang sudah ditentukan
                                UploadPDFTte uploader = new UploadPDFTte(fileName, no_sep, tanggal, TNoRw.getText(), kode, tanggalSekarang, "INA-CBG");
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
                                    pst.setString(1, TNoRw.getText().trim());
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

                                Desktop.getDesktop().browse(new File("ResumeRJ" + TNoRw.getText().trim().replaceAll("/", "") + "_" + TNoRM.getText().trim().replaceAll(" ", "") + "_" + TPasien.getText().trim().replaceAll(" ", "") + ".pdf").toURI());

                                // Upload ke berkas riwayat perawatan
                                try {
                                    File signedFile = new File("ResumeRJ" + TNoRw.getText().trim().replaceAll("/", "") + "_" + TNoRM.getText().trim().replaceAll(" ", "") + "_" + TPasien.getText().trim().replaceAll(" ", "") + ".pdf");
                                    byte[] signedData = FileUtils.readFileToByteArray(signedFile);

                                    // Upload ke server menggunakan HttpClient 5
                                    try (CloseableHttpClient httpClientUpload = HttpClients.createDefault()) {
                                        String uploadUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/upload.php?doc=berkasrawat/pages/upload/";
                                        HttpPost postRequest = new HttpPost(uploadUrl);

                                        // Multipart entity untuk HttpClient 5
                                        MultipartEntityBuilder builderUpload = MultipartEntityBuilder.create();
                                        builderUpload.setMode(HttpMultipartMode.LEGACY);
                                        builderUpload.addBinaryBody("file", signedData, ContentType.APPLICATION_OCTET_STREAM, fileName);

                                        HttpEntity multipart = builderUpload.build();
                                        postRequest.setEntity(multipart);

                                        // Execute request
                                        try (CloseableHttpResponse responseUpload = httpClientUpload.execute(postRequest)) {
                                            int statusCode = responseUpload.getCode();
                                            if (statusCode == 200) {
                                                // Menyimpan ke database berkas_digital_perawatan
                                                String kodeBerkas = Sequel.cariIsi("SELECT kode FROM master_berkas_digital WHERE nama LIKE '%resume%'");

                                                String curUser = akses.getkode();
                                                // 1. Delete existing record
                                                Sequel.queryu2("DELETE FROM berkas_digital_perawatan WHERE no_rawat=? AND kode=? AND nip=?", 3, new String[]{
                                                    TNoRw.getText().trim(), kodeBerkas, curUser
                                                });
                                                
                                                // 2. Insert new record
                                                Sequel.menyimpantf("berkas_digital_perawatan", "?,?,?,?", "No.Rawat", 4, new String[]{
                                                    TNoRw.getText().trim(),
                                                    kodeBerkas,
                                                    "pages/upload/" + fileName,
                                                    curUser
                                                });
                                                System.out.println("File berhasil diupload ke berkas riwayat perawatan: pages/upload/" + fileName);
                                            } else {
                                                System.out.println("Upload ke berkas riwayat perawatan gagal dengan status code: " + statusCode);
                                            }
                                        }
                                    }
                                } catch (Exception uploadEx) {
                                    System.out.println("Error saat upload ke berkas riwayat perawatan: " + uploadEx);
                                }

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
                                pst.setString(1, TNoRw.getText().trim());
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
                    String fileName = "ResumeRJ" + TNoRw.getText().trim().replaceAll("/", "") + "_" + TNoRM.getText().trim().replaceAll(" ", "") + "_" + TPasien.getText().trim().replaceAll(" ", "") + ".pdf";
                    String keteranganText = buildDetailedDescription(no_sep, tanggal, usedRegistrationDate, usedNoRawatAsFallback, false, "SSL Error: " + e.getMessage());

                    String sql = "INSERT INTO status_tte (no_rawat, nik, tanggal, nama_berkas, status_code, lokasi_file, status, keterangan) VALUES (?, ?, ?,?,?,?,?,?)";
                    try ( PreparedStatement pst = koneksi.prepareStatement(sql)) {
                        pst.setString(1, TNoRw.getText().trim());
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
                String fileName = "ResumeRJ" + TNoRw.getText().trim().replaceAll("/", "") + "_" + TNoRM.getText().trim().replaceAll(" ", "") + "_" + TPasien.getText().trim().replaceAll(" ", "") + ".pdf";

                String fallbackSep = TNoRw.getText().trim().replaceAll("/", "");
                String fallbackDate = (tanggal_registrasi != null && !tanggal_registrasi.trim().isEmpty()) ? tanggal_registrasi : LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                String keteranganText = String.format(
                        "Proses dibatalkan user - No SEP tidak ditemukan untuk No Rawat: %s | No SEP pengganti: %s | Tanggal fallback: %s (%s)",
                        TNoRw.getText(),
                        fallbackSep,
                        fallbackDate,
                        (tanggal_registrasi != null && !tanggal_registrasi.trim().isEmpty()) ? "registrasi" : "sistem"
                );

                String sql = "INSERT INTO status_tte (no_rawat, nik, tanggal, nama_berkas, status_code, lokasi_file, status, keterangan) VALUES (?, ?, ?,?,?,?,?,?)";
                try ( PreparedStatement pst = koneksi.prepareStatement(sql)) {
                    pst.setString(1, TNoRw.getText().trim());
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
        }

    }//GEN-LAST:event_BtnSimpanTandaTanganActionPerformed
// ===== HELPER METHODS =====

    private String buildStatusDescription(boolean usedNoRawatAsFallback, boolean usedRegistrationDate, String baseStatus) {
        if (usedNoRawatAsFallback) {
            String dateInfo = usedRegistrationDate ? ", tanggal dari registrasi" : ", tanggal sistem";
            return baseStatus + " (No SEP diganti dengan No Rawat" + dateInfo + ")";
        }

        if (usedRegistrationDate) {
            return baseStatus + " (tanggal dari registrasi)";
        }

        return baseStatus;
    }

    private String buildDetailedDescription(String noSep, String tanggal, boolean usedRegistrationDate,
            boolean usedNoRawatAsFallback, boolean uploadSuccess, String additionalInfo) {
        String dateSource = "SEP";
        if (usedRegistrationDate) {
            dateSource = "registrasi";
        } else if (usedNoRawatAsFallback) {
            dateSource = "sistem";
        }

        String uploadInfo = "";
        if (additionalInfo.contains("TTE:") && !additionalInfo.contains("SSL Error")) {
            uploadInfo = " | Upload: " + (uploadSuccess ? "Sukses" : "Gagal");
        }

        return String.format("No SEP: %s | Tanggal: %s (dari %s)%s | %s",
                noSep, tanggal, dateSource, uploadInfo, additionalInfo);
    }


    private void MnLaporanResumeESignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnLaporanResumeESignActionPerformed
  
        
        String noRawat = tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString();
        String tanggal = tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(); // Format: yyyy-MM-dd
//        String jam = tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString();     // Format: HH:mm:ss
//        String tanggalLengkap = tanggal + " " + jam;
       String tanggalLengkap = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
       String kode = Sequel.cariIsi("select kode from master_berkas_digital where nama like 'resume'");

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
                    JDialog dialog = new JDialog(parentWindow, "Lihat Resume Rawat Inap", Dialog.ModalityType.APPLICATION_MODAL);
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
    }//GEN-LAST:event_MnLaporanResumeESignActionPerformed

    private void TagSignerDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TagSignerDokterActionPerformed
        if (tbObat.getSelectedRow() != -1) {
            String noRawat = tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString();
            String kdDokter = tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString();
            
            // Validasi: Pastikan user login adalah dokter pembuat resume
            if (!akses.getkode().equals(kdDokter)) {
                JOptionPane.showMessageDialog(null, 
                    "Anda tidak memiliki hak akses untuk menandatangani dokumen ini!\n\n" +
                    "Dokumen dibuat oleh Dokter : " + kdDokter + "\n" +
                    "User Login (Anda)         : " + akses.getkode() + "\n\n" +
                    "Hanya dokter pembuat resume yang dapat membubuhkan tanda tangan digital.", 
                    "Akses Ditolak", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String kodeberkas = Sequel.cariIsi("SELECT kode FROM master_berkas_digital WHERE nama LIKE '%resume%'");
            if (kodeberkas == null || kodeberkas.trim().isEmpty()) {
                kodeberkas = "001"; // Default fallback
            }

            int confirm = JOptionPane.showConfirmDialog(null, 
                "Apakah anda yakin ingin membubuhkan tanda tangan digital pada resume pasien ini?\nNo. Rawat: " + noRawat, 
                "Konfirmasi TTE", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (generateAndUploadPDF(noRawat, tbObat.getSelectedRow(), kodeberkas, kdDokter)) {
                    JOptionPane.showMessageDialog(null, 
                        "Tanda Tangan Digital Berhasil Dibubuhkan dan Dokumen Telah Diupload.", 
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);
                    tampil();
                } else {
                    JOptionPane.showMessageDialog(null, 
                        "Gagal membubuhkan tanda tangan digital. Silahkan cek koneksi atau hubungi IT.", 
                        "Gagal", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, 
                "Silahkan pilih data resume pasien terlebih dahulu di tabel.", 
                "Pilih Data", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_TagSignerDokterActionPerformed

    private void TagSignerDokterBatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TagSignerDokterBatchActionPerformed
        int rowCount = tbObat.getRowCount();
        if (rowCount == 0) {
            JOptionPane.showMessageDialog(null, "Tidak ada data resume pasien!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int validCount = 0;
        String currentUser = akses.getkode();
        for (int row = 0; row < rowCount; row++) {
            try {
                String kdDokter = tbObat.getValueAt(row, 4).toString();
                if (currentUser.equals(kdDokter)) {
                    validCount++;
                }
            } catch (Exception e) {}
        }
        
        if (validCount == 0) {
            JOptionPane.showMessageDialog(null, 
                "Tidak ada dokumen resume milik Anda (" + currentUser + ") di tabel ini.\n" +
                "Hanya pembuat resume yang dapat melakukan tanda tangan.", 
                "Tidak Ada Dokumen Valid", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(null,
                "Ditemukan " + validCount + " dokumen resume milik Anda dari total " + rowCount + " data.\n" +
                "Apakah Anda yakin ingin melakukan tanda tangan elektronik massal (Batch)?\n" +
                "Proses ini mungkin memakan waktu.",
                "Konfirmasi TTE Batch", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) return;

        // Create Progress Dialog
        JDialog progressDialog = new JDialog(this, "Batch TTE Progress", true);
        progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        progressDialog.setSize(400, 120);
        progressDialog.setLocationRelativeTo(this);
        progressDialog.setLayout(new java.awt.BorderLayout(10, 10));
        
        JLabel lblProgress = new JLabel("Memulai proses batch TTE...", SwingConstants.CENTER);
        lblProgress.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 10, 5, 10));
        progressDialog.add(lblProgress, java.awt.BorderLayout.CENTER);
        
        JProgressBar progressBar = new JProgressBar(0, rowCount);
        progressBar.setStringPainted(true);
        progressBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 15, 15, 15));
        progressDialog.add(progressBar, java.awt.BorderLayout.SOUTH);

        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            private int success = 0;
            private int failed = 0;
            private int skipped = 0;
            private int currentProcessed = 0;

            @Override
            protected Void doInBackground() throws Exception {
                String kodeberkas = Sequel.cariIsi("SELECT kode FROM master_berkas_digital WHERE nama LIKE '%resume%'");
                if (kodeberkas == null || kodeberkas.trim().isEmpty()) {
                    kodeberkas = "RES"; // Fallback to RES or check if 001 is better
                }

                for (int row = 0; row < rowCount; row++) {
                    currentProcessed++;
                    String noRawat = tbObat.getValueAt(row, 1).toString();
                    String kdDokter = tbObat.getValueAt(row, 4).toString();
                    
                    if (!currentUser.equals(kdDokter)) {
                        skipped++;
                        publish("Skipping row " + currentProcessed + "/" + rowCount + " (Bukan milik Anda)");
                        setProgress(currentProcessed);
                        continue;
                    }
                    
                    publish("Memproses row " + currentProcessed + "/" + rowCount + ": " + noRawat);
                    if (generateAndUploadPDF(noRawat, row, kodeberkas, kdDokter)) {
                        success++;
                    } else {
                        failed++;
                    }
                    setProgress(currentProcessed);
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                if (!chunks.isEmpty()) {
                    String lastMessage = chunks.get(chunks.size() - 1);
                    lblProgress.setText(lastMessage);
                    progressBar.setValue(currentProcessed);
                }
            }

            @Override
            protected void done() {
                progressDialog.dispose();
                setCursor(Cursor.getDefaultCursor());
                
                String message = "Batch Upload Selesai.\n\n" +
                               "Sukses: " + success + "\n" +
                               "Gagal: " + failed + "\n" +
                               "Dilewati (bukan dokumen Anda): " + skipped;
                
                JOptionPane.showMessageDialog(null, message, "Laporan Batch TTE", JOptionPane.INFORMATION_MESSAGE);
                tampil();
            }
        };

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        worker.execute();
        progressDialog.setVisible(true);
    }//GEN-LAST:event_TagSignerDokterBatchActionPerformed

    private void BtnAmbilDataDiagnosaProsedurActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAmbilDataDiagnosaProsedurActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnAmbilDataDiagnosaProsedurActionPerformed

    private void BtnAmbilDataDiagnosaProsedurKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAmbilDataDiagnosaProsedurKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnAmbilDataDiagnosaProsedurKeyPressed
    
    private boolean generateAndUploadPDF(String noRawat, int rowIndex, String kodeberkas, String kdDokter) {
        try {
            // Setup parameters sama seperti MnLaporanResumeActionPerformed
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            param.put("logobsre", Sequel.cariGambar("select gambar.bsre from gambar"));
            param.put("norawat", noRawat);
            
            // Get ruang dan tanggal keluar
            String tanggal = "";
            if (Sequel.cariIsi("select reg_periksa.status_lanjut from reg_periksa where reg_periksa.no_rawat=?", noRawat).equals("Ralan")) {
                param.put("ruang", Sequel.cariIsi("select poliklinik.nm_poli from poliklinik inner join reg_periksa on reg_periksa.kd_poli=poliklinik.kd_poli where reg_periksa.no_rawat=?", noRawat));
                tanggal = Sequel.cariIsi("select DATE_FORMAT(tgl_registrasi, '%d-%m-%Y') from reg_periksa where no_rawat=?", noRawat);
                param.put("tanggalkeluar", tanggal);
            } else {
                param.put("ruang", Sequel.cariIsi("select nm_bangsal from bangsal inner join kamar inner join kamar_inap on bangsal.kd_bangsal=kamar.kd_bangsal and kamar_inap.kd_kamar=kamar.kd_kamar where no_rawat=? order by tgl_masuk desc limit 1", noRawat));
                tanggal = Sequel.cariIsi("select DATE_FORMAT(tgl_keluar, '%d-%m-%Y') from kamar_inap where no_rawat=? order by tgl_keluar desc limit 1", noRawat);
                param.put("tanggalkeluar", tanggal);
            }
            
            // Get tag_signer dari dokter atau petugas
            String tagSigner = "";
            if(Sequel.cariInteger("select count(kd_dokter) from dokter where kd_dokter=?", kdDokter) > 0){
                tagSigner = Sequel.cariIsi("select tag_signer from dokter where kd_dokter=?", kdDokter);
            } else {
                tagSigner = Sequel.cariIsi("select tag_signer from petugas where nip=?", kdDokter);
            }
            if (tagSigner == null || tagSigner.trim().isEmpty()) {
                tagSigner = "#";
            }
            
            // Get nama dokter
            String nmDokter = Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", kdDokter);
            
            param.put("finger", tagSigner);
            
            // Generate PDF filename
            String noRM = Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", noRawat);
            String nmPasien = Sequel.cariIsi("select nm_pasien from pasien where no_rkm_medis=?", noRM);
            String fileName = "ResumeRJ" + noRawat.replaceAll("/", "") + "_" + noRM.replaceAll(" ", "") + "_" + nmPasien.replaceAll(" ", "") + ".pdf";
            File localFile = new File(fileName);
            
            // Generate PDF using JasperReports dengan report TTE
            PreparedStatement psReport = null;
            ResultSet rsReport = null;
            try {
                String reportName = "rptLaporanResumeTte.jasper";
                java.io.InputStream reportStream = getClass().getResourceAsStream("/report/" + reportName);
                
                if (reportStream == null) {
                    // Fallback to file system
                    File f = new File("report/" + reportName);
                    if (f.exists()) {
                        reportStream = new java.io.FileInputStream(f);
                    } else {
                        System.err.println("Report File Not Found in Classpath or File System: " + reportName);
                        return false;
                    }
                }
                
                // Query data resume pasien dengan JOIN lengkap sesuai rptLaporanResumeTte.jrxml
                String sqlReport = "select reg_periksa.tgl_registrasi,reg_periksa.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir, " +
                                   "reg_periksa.umurdaftar,reg_periksa.sttsumur,pasien.pekerjaan,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab,', ',propinsi.nm_prop) as alamat, " +
                                   "resume_pasien.kd_dokter,dokter.nm_dokter,resume_pasien.kondisi_pulang,resume_pasien.keluhan_utama,resume_pasien.jalannya_penyakit, " +
                                   "resume_pasien.pemeriksaan_penunjang,resume_pasien.hasil_laborat,resume_pasien.diagnosa_utama,resume_pasien.kd_diagnosa_utama, " +
                                   "resume_pasien.diagnosa_sekunder,resume_pasien.kd_diagnosa_sekunder,resume_pasien.diagnosa_sekunder2,resume_pasien.kd_diagnosa_sekunder2, " +
                                   "resume_pasien.diagnosa_sekunder3,resume_pasien.kd_diagnosa_sekunder3,resume_pasien.diagnosa_sekunder4,resume_pasien.kd_diagnosa_sekunder4, " +
                                   "resume_pasien.prosedur_utama,resume_pasien.kd_prosedur_utama,resume_pasien.prosedur_sekunder,resume_pasien.kd_prosedur_sekunder, " +
                                   "resume_pasien.prosedur_sekunder2,resume_pasien.kd_prosedur_sekunder2,resume_pasien.prosedur_sekunder3,resume_pasien.kd_prosedur_sekunder3, " +
                                   "resume_pasien.obat_pulang,resume_pasien.keadaan,resume_pasien.ket_keadaan,resume_pasien.cara_keluar,resume_pasien.ket_keluar " +
                                   "from resume_pasien inner join reg_periksa on resume_pasien.no_rawat=reg_periksa.no_rawat " +
                                   "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis inner join dokter on resume_pasien.kd_dokter=dokter.kd_dokter " +
                                   "inner join kelurahan on pasien.kd_kel=kelurahan.kd_kel inner join kecamatan on pasien.kd_kec=kecamatan.kd_kec " +
                                   "inner join kabupaten on pasien.kd_kab=kabupaten.kd_kab inner join propinsi on pasien.kd_prop=propinsi.kd_prop " +
                                   "where resume_pasien.no_rawat='" + noRawat + "'";
                System.out.println("Executing Report Query: " + sqlReport);
                psReport = koneksi.prepareStatement(sqlReport);
                rsReport = psReport.executeQuery();
                
                net.sf.jasperreports.engine.JRResultSetDataSource jrds = new net.sf.jasperreports.engine.JRResultSetDataSource(rsReport);
                net.sf.jasperreports.engine.JasperReport jasperReport = (net.sf.jasperreports.engine.JasperReport) net.sf.jasperreports.engine.util.JRLoader.loadObject(reportStream);
                
                // Pass connection for subreports if any
                param.put("REPORT_CONNECTION", koneksi);
                
                net.sf.jasperreports.engine.JasperPrint jasperPrint = net.sf.jasperreports.engine.JasperFillManager.fillReport(jasperReport, param, jrds);
                net.sf.jasperreports.engine.JasperExportManager.exportReportToPdfFile(jasperPrint, fileName);
            } catch (Exception e) {
                System.err.println("Error generating PDF: " + e);
                e.printStackTrace();
                return false;
            } finally {
                if(rsReport != null) rsReport.close();
                if(psReport != null) psReport.close();
            }
            
            // Upload Logic
            if(localFile.exists()) {
                try {
                    System.out.println("Starting Cloud Upload for: " + fileName);
                    byte[] data = FileUtils.readFileToByteArray(localFile);
                    
                    // Upload ke server menggunakan HttpClient 5
                    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                        String uploadUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/upload.php?doc=berkasrawat/pages/upload/";
                        System.out.println("Upload Endpoint: " + uploadUrl);
                        
                        HttpPost postRequest = new HttpPost(uploadUrl);
                        
                        // Multipart entity untuk HttpClient 5
                        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                        builder.setMode(HttpMultipartMode.LEGACY);
                        builder.addBinaryBody("file", data, ContentType.APPLICATION_OCTET_STREAM, fileName);
                        
                        HttpEntity multipart = builder.build();
                        postRequest.setEntity(multipart);
                        
                        // Execute request
                        try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                            int statusCode = response.getCode();
                            System.out.println("Upload HTTP Status: " + statusCode + " " + response.getReasonPhrase());
                            
                            if (statusCode != 200) {
                                System.err.println("Upload Failed! Server returned non-200 status code.");
                                return false;
                            }

                            String lokasiFile = "pages/upload/" + fileName;
                            System.out.println("Updating Database Records for: " + lokasiFile);
                            
                            // 1. Delete existing record
                            Sequel.queryu2("DELETE FROM berkas_digital_perawatan WHERE no_rawat=? AND kode=? AND nip=?", 3, new String[]{
                                noRawat, kodeberkas, kdDokter
                            });

                            // 2. Insert new record
                            Sequel.menyimpantf("berkas_digital_perawatan", "?,?,?,?", "No.Rawat", 4, new String[]{
                                noRawat, kodeberkas, lokasiFile, kdDokter
                            });
                            
                            localFile.delete();
                            System.out.println("Upload Process Completed Successfully.");
                            return true;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Upload Exception Occurred:");
                    e.printStackTrace();
                    return false;
                }
            } else {
                System.err.println("PDF file not found: " + fileName);
                return false;
            }

        } catch (Exception e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMDataResumePasien dialog = new RMDataResumePasien(new javax.swing.JFrame(), true);
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
    private widget.Button BtnAmbilDataDiagnosaProsedur;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnClosePhrase;
    private widget.Button BtnDokter;
    private widget.Button BtnDokter1;
    private widget.Button BtnDokter2;
    private widget.Button BtnDokter3;
    private widget.Button BtnDokter4;
    private widget.Button BtnDokter5;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.Button BtnSimpanTandaTangan;
    private widget.ComboBox CaraKeluar2;
    private widget.CekBox ChkInput;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.TextBox DiagnosaSekunder1;
    private widget.TextBox DiagnosaSekunder2;
    private widget.TextBox DiagnosaSekunder3;
    private widget.TextBox DiagnosaSekunder4;
    private widget.TextBox DiagnosaUtama;
    private widget.PanelBiasa FormInput;
    private widget.TextArea HasilLaborat;
    private widget.TextArea JalannyaPenyakit;
    private widget.ComboBox Keadaan2;
    private widget.TextArea Keluhan;
    private widget.TextBox KetKeadaanPulang2;
    private widget.TextBox KetKeluar2;
    private widget.TextBox KodeDiagnosaSekunder1;
    private widget.TextBox KodeDiagnosaSekunder2;
    private widget.TextBox KodeDiagnosaSekunder3;
    private widget.TextBox KodeDiagnosaSekunder4;
    private widget.TextBox KodeDiagnosaUtama;
    private widget.TextBox KodeDokter;
    private widget.TextBox KodeProsedurSekunder1;
    private widget.TextBox KodeProsedurSekunder2;
    private widget.TextBox KodeProsedurSekunder3;
    private widget.TextBox KodeProsedurUtama;
    private widget.ComboBox Kondisi;
    private widget.Label LCount;
    private javax.swing.JMenuItem MnInputDiagnosa;
    private javax.swing.JMenuItem MnLaporanResume;
    private javax.swing.JMenuItem MnLaporanResumeESign;
    private javax.swing.JMenuItem MnSPBK;
    private javax.swing.JMenuItem MnSPBK2;
    private widget.TextBox NamaDokter;
    private widget.TextArea Obat2an;
    private javax.swing.JPanel PanelInput;
    private widget.TextArea PemeriksaanPenunjang;
    private widget.TextBox Phrase;
    private widget.TextBox ProsedurSekunder1;
    private widget.TextBox ProsedurSekunder2;
    private widget.TextBox ProsedurSekunder3;
    private widget.TextBox ProsedurUtama;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private javax.swing.JMenuItem TagSignerDokter;
    private javax.swing.JMenuItem TagSignerDokterBatch;
    private javax.swing.JMenuItem UploadResume;
    private javax.swing.JDialog WindowPhrase;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame8;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel25;
    private widget.Label jLabel26;
    private widget.Label jLabel27;
    private widget.Label jLabel28;
    private widget.Label jLabel29;
    private widget.Label jLabel30;
    private widget.Label jLabel31;
    private widget.Label jLabel32;
    private widget.Label jLabel33;
    private widget.Label jLabel34;
    private widget.Label jLabel35;
    private widget.Label jLabel36;
    private widget.Label jLabel37;
    private widget.Label jLabel4;
    private widget.Label jLabel45;
    private widget.Label jLabel46;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.Label label14;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.panelisi panelisi5;
    private javax.swing.JMenuItem ppBerkasDigital;
    private widget.ScrollPane scrollInput;
    private widget.ScrollPane scrollPane2;
    private widget.ScrollPane scrollPane3;
    private widget.ScrollPane scrollPane4;
    private widget.ScrollPane scrollPane5;
    private widget.ScrollPane scrollPane6;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            if (TCari.getText().equals("")) {
                ps = koneksi.prepareStatement(
                        "select reg_periksa.tgl_registrasi,reg_periksa.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien, "
                        + "resume_pasien.kd_dokter,dokter.nm_dokter,resume_pasien.kondisi_pulang,resume_pasien.keluhan_utama,resume_pasien.jalannya_penyakit, "
                        + "resume_pasien.pemeriksaan_penunjang,resume_pasien.hasil_laborat,resume_pasien.diagnosa_utama,resume_pasien.kd_diagnosa_utama, "
                        + "resume_pasien.diagnosa_sekunder,resume_pasien.kd_diagnosa_sekunder,resume_pasien.diagnosa_sekunder2,resume_pasien.kd_diagnosa_sekunder2, "
                        + "resume_pasien.diagnosa_sekunder3,resume_pasien.kd_diagnosa_sekunder3,resume_pasien.diagnosa_sekunder4,resume_pasien.kd_diagnosa_sekunder4, "
                        + "resume_pasien.prosedur_utama,resume_pasien.kd_prosedur_utama,resume_pasien.prosedur_sekunder,resume_pasien.kd_prosedur_sekunder, "
                        + "resume_pasien.prosedur_sekunder2,resume_pasien.kd_prosedur_sekunder2,resume_pasien.prosedur_sekunder3,resume_pasien.kd_prosedur_sekunder3, "
                        + "resume_pasien.obat_pulang,resume_pasien.keadaan,resume_pasien.ket_keadaan,resume_pasien.cara_keluar,resume_pasien.ket_keluar from resume_pasien inner join reg_periksa on resume_pasien.no_rawat=reg_periksa.no_rawat  "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis inner join dokter on resume_pasien.kd_dokter=dokter.kd_dokter "
                        + "where reg_periksa.tgl_registrasi between ? and ? order by reg_periksa.tgl_registrasi");
            } else {
                ps = koneksi.prepareStatement(
                        "select reg_periksa.tgl_registrasi,reg_periksa.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien, "
                        + "resume_pasien.kd_dokter,dokter.nm_dokter,resume_pasien.kondisi_pulang,resume_pasien.keluhan_utama,resume_pasien.jalannya_penyakit, "
                        + "resume_pasien.pemeriksaan_penunjang,resume_pasien.hasil_laborat,resume_pasien.diagnosa_utama,resume_pasien.kd_diagnosa_utama, "
                        + "resume_pasien.diagnosa_sekunder,resume_pasien.kd_diagnosa_sekunder,resume_pasien.diagnosa_sekunder2,resume_pasien.kd_diagnosa_sekunder2, "
                        + "resume_pasien.diagnosa_sekunder3,resume_pasien.kd_diagnosa_sekunder3,resume_pasien.diagnosa_sekunder4,resume_pasien.kd_diagnosa_sekunder4, "
                        + "resume_pasien.prosedur_utama,resume_pasien.kd_prosedur_utama,resume_pasien.prosedur_sekunder,resume_pasien.kd_prosedur_sekunder, "
                        + "resume_pasien.prosedur_sekunder2,resume_pasien.kd_prosedur_sekunder2,resume_pasien.prosedur_sekunder3,resume_pasien.kd_prosedur_sekunder3, "
                        + "resume_pasien.obat_pulang,resume_pasien.keadaan,resume_pasien.ket_keadaan,resume_pasien.cara_keluar,resume_pasien.ket_keluar from resume_pasien inner join reg_periksa on resume_pasien.no_rawat=reg_periksa.no_rawat  "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis inner join dokter on resume_pasien.kd_dokter=dokter.kd_dokter "
                        + "where reg_periksa.tgl_registrasi between ? and ? and "
                        + "(reg_periksa.no_rkm_medis like ? or pasien.nm_pasien like ? or resume_pasien.kd_dokter like ? or "
                        + "dokter.nm_dokter like ? or resume_pasien.kondisi_pulang like ? or resume_pasien.kd_diagnosa_utama like ? or "
                        + "resume_pasien.diagnosa_utama like ? or resume_pasien.prosedur_utama like ? or reg_periksa.no_rawat like ? or "
                        + "resume_pasien.kd_prosedur_utama like ?) order by reg_periksa.tgl_registrasi");
            }
            try {
                if (TCari.getText().equals("")) {
                    ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
                    ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
                } else {
                    ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
                    ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
                    ps.setString(3, "%" + TCari.getText() + "%");
                    ps.setString(4, "%" + TCari.getText() + "%");
                    ps.setString(5, "%" + TCari.getText() + "%");
                    ps.setString(6, "%" + TCari.getText() + "%");
                    ps.setString(7, "%" + TCari.getText() + "%");
                    ps.setString(8, "%" + TCari.getText() + "%");
                    ps.setString(9, "%" + TCari.getText() + "%");
                    ps.setString(10, "%" + TCari.getText() + "%");
                    ps.setString(11, "%" + TCari.getText() + "%");
                    ps.setString(12, "%" + TCari.getText() + "%");
                }
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString("tgl_registrasi"), rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("kd_dokter"), rs.getString("nm_dokter"), rs.getString("kondisi_pulang"), rs.getString("keluhan_utama"),
                        rs.getString("jalannya_penyakit"), rs.getString("pemeriksaan_penunjang"), rs.getString("hasil_laborat"), rs.getString("diagnosa_utama"),
                        rs.getString("kd_diagnosa_utama"), rs.getString("diagnosa_sekunder"), rs.getString("kd_diagnosa_sekunder"), rs.getString("diagnosa_sekunder2"),
                        rs.getString("kd_diagnosa_sekunder2"), rs.getString("diagnosa_sekunder3"), rs.getString("kd_diagnosa_sekunder3"), rs.getString("diagnosa_sekunder4"),
                        rs.getString("kd_diagnosa_sekunder4"), rs.getString("prosedur_utama"), rs.getString("kd_prosedur_utama"), rs.getString("prosedur_sekunder"),
                        rs.getString("kd_prosedur_sekunder"), rs.getString("prosedur_sekunder2"), rs.getString("kd_prosedur_sekunder2"), rs.getString("prosedur_sekunder3"),
                        rs.getString("kd_prosedur_sekunder3"), rs.getString("obat_pulang"), rs.getString("keadaan"), rs.getString("ket_keadaan"), rs.getString("cara_keluar"), rs.getString("ket_keluar")
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
        } catch (SQLException e) {
            System.out.println("Notifikasi : " + e);
        }
        int b = tabMode.getRowCount();
        LCount.setText("" + b);
    }

    public void emptTeks() {
        Keluhan.setText("");
        JalannyaPenyakit.setText("");
        PemeriksaanPenunjang.setText("");
        HasilLaborat.setText("");
        Obat2an.setText("");
        DiagnosaUtama.setText("");
        DiagnosaSekunder1.setText("");
        DiagnosaSekunder2.setText("");
        DiagnosaSekunder3.setText("");
        DiagnosaSekunder4.setText("");
        ProsedurUtama.setText("");
        ProsedurSekunder1.setText("");
        ProsedurSekunder2.setText("");
        ProsedurSekunder3.setText("");
        KodeDiagnosaUtama.setText("");
        KodeDiagnosaSekunder1.setText("");
        KodeDiagnosaSekunder2.setText("");
        KodeDiagnosaSekunder3.setText("");
        KodeDiagnosaSekunder4.setText("");
        KodeProsedurUtama.setText("");
        KodeProsedurSekunder1.setText("");
        KodeProsedurSekunder2.setText("");
        KodeProsedurSekunder3.setText("");
        Kondisi.requestFocus();
    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
            Kondisi.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
            Keluhan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString());
            JalannyaPenyakit.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString());
            PemeriksaanPenunjang.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString());
            HasilLaborat.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString());
            DiagnosaUtama.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString());
            KodeDiagnosaUtama.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString());
            DiagnosaSekunder1.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 13).toString());
            KodeDiagnosaSekunder1.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 14).toString());
            DiagnosaSekunder2.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 15).toString());
            KodeDiagnosaSekunder2.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 16).toString());
            DiagnosaSekunder3.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 17).toString());
            KodeDiagnosaSekunder3.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 18).toString());
            DiagnosaSekunder4.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 19).toString());
            KodeDiagnosaSekunder4.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 20).toString());
            ProsedurUtama.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 21).toString());
            KodeProsedurUtama.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 22).toString());
            ProsedurSekunder1.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 23).toString());
            KodeProsedurSekunder1.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 24).toString());
            ProsedurSekunder2.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 25).toString());
            KodeProsedurSekunder2.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 26).toString());
            ProsedurSekunder3.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 27).toString());
            KodeProsedurSekunder3.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 28).toString());
            Obat2an.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 29).toString());
            Keadaan2.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 30).toString());
            KetKeadaanPulang2.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 31).toString());
            CaraKeluar2.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 32).toString());
            KetKeluar2.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 33).toString());
        }
    }

    private void isRawat() {
        Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat='" + TNoRw.getText() + "' ", TNoRM);
    }

    private void isPsien() {
        Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis='" + TNoRM.getText() + "' ", TPasien);
    }

    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        Sequel.cariIsi("select reg_periksa.tgl_registrasi from reg_periksa where reg_periksa.no_rawat='" + norwt + "'", DTPCari1);
        DTPCari2.setDate(tgl2);
        isRawat();
        isPsien();
        ChkInput.setSelected(true);
        isForm();
        Kondisi.requestFocus();
    /*    try {
            ps = koneksi.prepareStatement(
                    "select diagnosa_pasien.kd_penyakit,penyakit.nm_penyakit,diagnosa_pasien.prioritas "
                    + "from diagnosa_pasien inner join penyakit on diagnosa_pasien.kd_penyakit=penyakit.kd_penyakit "
                    + "where diagnosa_pasien.no_rawat=? order by diagnosa_pasien.prioritas ");
            try {
                ps.setString(1, norwt);
                rs = ps.executeQuery();
                while (rs.next()) {
                    if (rs.getInt("prioritas") == 1) {
                        KodeDiagnosaUtama.setText(rs.getString("kd_penyakit"));
                        DiagnosaUtama.setText(rs.getString("nm_penyakit"));
                    }

                    if (rs.getInt("prioritas") == 2) {
                        KodeDiagnosaSekunder1.setText(rs.getString("kd_penyakit"));
                        DiagnosaSekunder1.setText(rs.getString("nm_penyakit"));
                    }

                    if (rs.getInt("prioritas") == 3) {
                        KodeDiagnosaSekunder2.setText(rs.getString("kd_penyakit"));
                        DiagnosaSekunder2.setText(rs.getString("nm_penyakit"));
                    }

                    if (rs.getInt("prioritas") == 4) {
                        KodeDiagnosaSekunder3.setText(rs.getString("kd_penyakit"));
                        DiagnosaSekunder3.setText(rs.getString("nm_penyakit"));
                    }

                    if (rs.getInt("prioritas") == 5) {
                        KodeDiagnosaSekunder4.setText(rs.getString("kd_penyakit"));
                        DiagnosaSekunder4.setText(rs.getString("nm_penyakit"));
                    }
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

        try {
            ps = koneksi.prepareStatement(
                    "select prosedur_pasien.kode,icd9.deskripsi_panjang, prosedur_pasien.prioritas "
                    + "from prosedur_pasien inner join icd9 on prosedur_pasien.kode=icd9.kode "
                    + "where prosedur_pasien.no_rawat=? order by prosedur_pasien.prioritas ");
            try {
                ps.setString(1, norwt);
                rs = ps.executeQuery();
                while (rs.next()) {
                    if (rs.getInt("prioritas") == 1) {
                        KodeProsedurUtama.setText(rs.getString("kode"));
                        ProsedurUtama.setText(rs.getString("deskripsi_panjang"));
                    }

                    if (rs.getInt("prioritas") == 2) {
                        KodeProsedurSekunder1.setText(rs.getString("kode"));
                        ProsedurSekunder1.setText(rs.getString("deskripsi_panjang"));
                    }

                    if (rs.getInt("prioritas") == 3) {
                        KodeProsedurSekunder2.setText(rs.getString("kode"));
                        ProsedurSekunder2.setText(rs.getString("deskripsi_panjang"));
                    }

                    if (rs.getInt("prioritas") == 4) {
                        KodeProsedurSekunder3.setText(rs.getString("kode"));
                        ProsedurSekunder3.setText(rs.getString("deskripsi_panjang"));
                    }
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
        }  */
    }

    private void isForm() {
        if (ChkInput.isSelected() == true) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, this.getHeight() - 122));
            scrollInput.setVisible(true);
            ChkInput.setVisible(true);
        } else if (ChkInput.isSelected() == false) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 20));
            scrollInput.setVisible(false);
            ChkInput.setVisible(true);
        }
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getdata_resume_pasien());
        BtnHapus.setEnabled(akses.getdata_resume_pasien());
        BtnEdit.setEnabled(akses.getdata_resume_pasien());
        BtnPrint.setEnabled(akses.getdata_resume_pasien());
        MnInputDiagnosa.setEnabled(akses.getdiagnosa_pasien());
        ppBerkasDigital.setEnabled(akses.getberkas_digital_perawatan());
        if (akses.getjml2() >= 1) {
            KodeDokter.setEditable(false);
            BtnDokter.setEnabled(false);
            KodeDokter.setText(akses.getkode());
            Sequel.cariIsi("select dokter.nm_dokter from dokter where dokter.kd_dokter=?", NamaDokter, KodeDokter.getText());
            if (NamaDokter.getText().equals("")) {
                KodeDokter.setText("");
                JOptionPane.showMessageDialog(null, "User login bukan dokter...!!");
            }
        }
    }

    private void ganti() {
        if (Sequel.mengedittf("resume_pasien", "no_rawat=?", "no_rawat=?,kd_dokter=?,keluhan_utama=?,jalannya_penyakit=?,pemeriksaan_penunjang=?,hasil_laborat=?,diagnosa_utama=?,kd_diagnosa_utama=?,diagnosa_sekunder=?,kd_diagnosa_sekunder=?,diagnosa_sekunder2=?,kd_diagnosa_sekunder2=?,diagnosa_sekunder3=?,kd_diagnosa_sekunder3=?,diagnosa_sekunder4=?,kd_diagnosa_sekunder4=?,prosedur_utama=?,kd_prosedur_utama=?,prosedur_sekunder=?,kd_prosedur_sekunder=?,prosedur_sekunder2=?,kd_prosedur_sekunder2=?,prosedur_sekunder3=?,kd_prosedur_sekunder3=?,kondisi_pulang=?,obat_pulang=?,keadaan=?,ket_keadaan=?,cara_keluar=?,ket_keluar=?", 31, new String[]{
            TNoRw.getText(), KodeDokter.getText(), Keluhan.getText(), JalannyaPenyakit.getText(), PemeriksaanPenunjang.getText(), HasilLaborat.getText(),
            DiagnosaUtama.getText(), KodeDiagnosaUtama.getText(), DiagnosaSekunder1.getText(), KodeDiagnosaSekunder1.getText(), DiagnosaSekunder2.getText(),
            KodeDiagnosaSekunder2.getText(), DiagnosaSekunder3.getText(), KodeDiagnosaSekunder3.getText(), DiagnosaSekunder4.getText(), KodeDiagnosaSekunder4.getText(),
            ProsedurUtama.getText(), KodeProsedurUtama.getText(), ProsedurSekunder1.getText(), KodeProsedurSekunder1.getText(), ProsedurSekunder2.getText(),
            KodeProsedurSekunder2.getText(), ProsedurSekunder3.getText(), KodeProsedurSekunder3.getText(), Kondisi.getSelectedItem().toString(), Obat2an.getText(), 
            Keadaan2.getSelectedItem().toString(), KetKeadaanPulang2.getText(), CaraKeluar2.getSelectedItem().toString(), KetKeluar2.getText(),
            tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString()
        }) == true) {
            tampil();
            emptTeks();
        }
    }

    private void hapus() {
        if (Sequel.queryu2tf("delete from resume_pasien where no_rawat=?", 1, new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString()
        }) == true) {
            tampil();
            emptTeks();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
        }

    }

    private void CreatePDF(String FileName) {
        if (tbObat.getSelectedRow() > -1) {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            param.put("norawat", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            param.put("jamkeluar", Sequel.cariIsi("select jam_rawat from pemeriksaan_ralan where no_rawat=?", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString()));
            tanggal = "";
            if (Sequel.cariIsi("select reg_periksa.status_lanjut from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText()).equals("Ralan")) {
                param.put("ruang", Sequel.cariIsi("select poliklinik.nm_poli from poliklinik inner join reg_periksa on reg_periksa.kd_poli=poliklinik.kd_poli where reg_periksa.no_rawat=?", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString()));
                tanggal = Sequel.cariIsi("select DATE_FORMAT(tgl_registrasi, '%d-%m-%Y') from reg_periksa where no_rawat=?", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
                param.put("tanggalkeluar", tanggal);
            } else {
                param.put("ruang", Sequel.cariIsi("select nm_bangsal from bangsal inner join kamar inner join kamar_inap on bangsal.kd_bangsal=kamar.kd_bangsal and kamar_inap.kd_kamar=kamar.kd_kamar where no_rawat=? order by tgl_masuk desc limit 1 ", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString()));
                tanggal = Sequel.cariIsi("select DATE_FORMAT(tgl_keluar, '%d-%m-%Y') from kamar_inap where no_rawat=? order by tgl_keluar desc limit 1 ", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
                param.put("tanggalkeluar", tanggal);
            }
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString() + "\nID " + (finger.equals("") ? tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString() : finger) + "\n" + tanggal);

            Valid.MyReportPDFUpload("rptLaporanResume.jasper", "report", "::[ Upload Resume ]::", FileName, param);
        }
    }

    private void UploadPDF(String FileName, String docpath) {
        try {
            File file = new File("tmpPDF/" + FileName + ".pdf");
            byte[] data = FileUtils.readFileToByteArray(file);

            // HttpClient 5
            try ( CloseableHttpClient httpClient = HttpClients.createDefault()) {
                String uploadUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/upload.php?doc=" + docpath;
                HttpPost postRequest = new HttpPost(uploadUrl);

                // Multipart entity untuk HttpClient 5
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.LEGACY); // atau gunakan STRICT
                builder.addBinaryBody("file", data, ContentType.APPLICATION_OCTET_STREAM, FileName + ".pdf");

                HttpEntity multipart = builder.build();
                postRequest.setEntity(multipart);

                // Execute request
                try ( CloseableHttpResponse response = httpClient.execute(postRequest)) {
                    // Optional: Check response status
                    int statusCode = response.getCode();
                    if (statusCode != 200) {
                        System.out.println("Upload failed with status code: " + statusCode);
                        throw new Exception("HTTP Error: " + statusCode);
                    }
                }
            }

            // Menyimpan ke database
            boolean uploadSuccess = false;
            kodeberkas = Sequel.cariIsi("SELECT kode FROM master_berkas_digital WHERE nama LIKE '%resume%'");

            if (Sequel.cariInteger("SELECT COUNT(no_rawat) AS jumlah FROM berkas_digital_perawatan WHERE lokasi_file='pages/upload/" + FileName + ".pdf'") > 0) {
                uploadSuccess = Sequel.mengedittf("berkas_digital_perawatan", "lokasi_file=?", "no_rawat=?,kode=?, lokasi_file=?", 4, new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString().trim(),
                    kodeberkas,
                    "pages/upload/" + FileName + ".pdf",
                    "pages/upload/" + FileName + ".pdf"
                });
            } else {
                uploadSuccess = Sequel.menyimpantf("berkas_digital_perawatan", "?,?,?", "No.Rawat", 3, new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString().trim(),
                    kodeberkas,
                    "pages/upload/" + FileName + ".pdf"
                });
            }

            // Menampilkan notifikasi
            if (uploadSuccess) {
                JOptionPane.showMessageDialog(null, "Upload berhasil!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Upload gagal disimpan ke database.", "Peringatan", JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception e) {
            System.out.println("Upload error: " + e);
            JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat upload: " + e.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
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

    void createPdf(String FileName) {
        if (tbObat.getSelectedRow() > -1) {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            param.put("norawat", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            tanggal = "";
            if (Sequel.cariIsi("select reg_periksa.status_lanjut from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText()).equals("Ralan")) {
                param.put("ruang", Sequel.cariIsi("select poliklinik.nm_poli from poliklinik inner join reg_periksa on reg_periksa.kd_poli=poliklinik.kd_poli where reg_periksa.no_rawat=?", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString()));
                tanggal = Sequel.cariIsi("select DATE_FORMAT(tgl_registrasi, '%d-%m-%Y') from reg_periksa where no_rawat=?", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
                param.put("tanggalkeluar", tanggal);
            } else {
                param.put("ruang", Sequel.cariIsi("select nm_bangsal from bangsal inner join kamar inner join kamar_inap on bangsal.kd_bangsal=kamar.kd_bangsal and kamar_inap.kd_kamar=kamar.kd_kamar where no_rawat=? order by tgl_masuk desc limit 1 ", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString()));
                tanggal = Sequel.cariIsi("select DATE_FORMAT(tgl_keluar, '%d-%m-%Y') from kamar_inap where no_rawat=? order by tgl_keluar desc limit 1 ", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
                param.put("tanggalkeluar", tanggal);
            }
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString() + "\nID " + (finger.equals("") ? tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString() : finger) + "\n" + tanggal);
            param.put("logobsre", Sequel.cariGambar("select gambar.bsre from gambar"));

            Valid.MyReportPDFWithName("rptLaporanResumeTTE.jasper", "report", "tempfile", FileName, "::[ Laporan Resume Pasien ]::", param);
        }
    }

}
