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
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariDokter;
import laporan.DlgDiagnosaPenyakit;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.apache.commons.io.FileUtils;
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
public final class RMDataResumePasienRalanJKN extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps, pssoap;
    private ResultSet rs, rssoap;
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
    public RMDataResumePasienRalanJKN(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        WindowPhrase.setSize(320, 100);

        tabMode = new DefaultTableModel(null, new Object[]{
            "Tgl.Rawat", "No.Rawat", "No.RM", "Nama Pasien", "Kode Dokter", "Dokter Penanggung Jawab", "Pemeriksaan Fisik", "Diagnosa 1", "ICD10 1", "Diagnosa 2",
            "ICD10 2", "Diagnosa 3", "ICD10 3", "Diagnosa 4", "ICD10 4", "Prosedur 1", "ICD9 1", "Prosedur Sekunder 2", "ICD9 2", "Obat-obatan ",
            "Rawat Inap", "Alasan Di Rawat"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbObat.setModel(tabMode);

        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 22; i++) {
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
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        PemeriksaanFisik.setDocument(new batasInput((int) 2000).getKata(PemeriksaanFisik));
        Terapi.setDocument(new batasInput((int) 2000).getKata(Terapi));
        Diagnosa1.setDocument(new batasInput((int) 80).getKata(Diagnosa1));
        Diagnosa2.setDocument(new batasInput((int) 80).getKata(Diagnosa2));
        Diagnosa3.setDocument(new batasInput((int) 80).getKata(Diagnosa3));
        Diagnosa4.setDocument(new batasInput((int) 80).getKata(Diagnosa4));
        Prosedur1.setDocument(new batasInput((int) 80).getKata(Prosedur1));
        Prosedur2.setDocument(new batasInput((int) 80).getKata(Prosedur2));
        KodeDiagnosaUtama.setDocument(new batasInput((int) 10).getKata(KodeDiagnosaUtama));
        KodeDiagnosaSekunder1.setDocument(new batasInput((int) 10).getKata(KodeDiagnosaSekunder1));
        KodeDiagnosaSekunder2.setDocument(new batasInput((int) 10).getKata(KodeDiagnosaSekunder2));
        KodeDiagnosaSekunder3.setDocument(new batasInput((int) 10).getKata(KodeDiagnosaSekunder3));
        KodeProsedurUtama.setDocument(new batasInput((int) 8).getKata(KodeProsedurUtama));
        KodeProsedurSekunder1.setDocument(new batasInput((int) 8).getKata(KodeProsedurSekunder1));
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
                    PemeriksaanFisik.append(carikeluhan.getTable().getValueAt(carikeluhan.getTable().getSelectedRow(), 2).toString() + ", ");
                    PemeriksaanFisik.requestFocus();
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
                    PemeriksaanFisik.append(caripemeriksaan.getTable().getValueAt(caripemeriksaan.getTable().getSelectedRow(), 2).toString() + ", ");
                    PemeriksaanFisik.requestFocus();
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
                    Terapi.append(cariobat.getTable().getValueAt(cariobat.getTable().getSelectedRow(), 2).toString() + ", ");
                    Terapi.requestFocus();
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
        Diagnosa3 = new widget.TextBox();
        jLabel26 = new widget.Label();
        Diagnosa1 = new widget.TextBox();
        jLabel27 = new widget.Label();
        Diagnosa4 = new widget.TextBox();
        scrollPane2 = new widget.ScrollPane();
        PemeriksaanFisik = new widget.TextArea();
        jLabel5 = new widget.Label();
        jLabel11 = new widget.Label();
        scrollPane6 = new widget.ScrollPane();
        Terapi = new widget.TextArea();
        jLabel29 = new widget.Label();
        jLabel30 = new widget.Label();
        Diagnosa2 = new widget.TextBox();
        jLabel31 = new widget.Label();
        KodeDiagnosaUtama = new widget.TextBox();
        KodeDiagnosaSekunder1 = new widget.TextBox();
        KodeDiagnosaSekunder2 = new widget.TextBox();
        KodeDiagnosaSekunder3 = new widget.TextBox();
        jLabel32 = new widget.Label();
        Prosedur1 = new widget.TextBox();
        KodeProsedurUtama = new widget.TextBox();
        Prosedur2 = new widget.TextBox();
        jLabel33 = new widget.Label();
        KodeProsedurSekunder1 = new widget.TextBox();
        label14 = new widget.Label();
        KodeDokter = new widget.TextBox();
        NamaDokter = new widget.TextBox();
        BtnDokter = new widget.Button();
        BtnDokter1 = new widget.Button();
        BtnDokter4 = new widget.Button();
        BtnDokter5 = new widget.Button();
        jLabel36 = new widget.Label();
        Dirawat = new widget.ComboBox();
        Alasan = new widget.TextBox();
        jLabel38 = new widget.Label();
        jLabel39 = new widget.Label();
        jLabel40 = new widget.Label();

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

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Resume Medis Pasien Rawat Jalan JKN ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
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
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "26-03-2025" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "26-03-2025" }));
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

        jLabel4.setText("Pemeriksaan Fisik:");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(30, 80, 110, 23);

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

        jLabel25.setText("3.");
        jLabel25.setName("jLabel25"); // NOI18N
        FormInput.add(jLabel25);
        jLabel25.setBounds(20, 220, 145, 23);

        Diagnosa3.setHighlighter(null);
        Diagnosa3.setName("Diagnosa3"); // NOI18N
        Diagnosa3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Diagnosa3KeyPressed(evt);
            }
        });
        FormInput.add(Diagnosa3);
        Diagnosa3.setBounds(170, 220, 520, 23);

        jLabel26.setText("4.");
        jLabel26.setName("jLabel26"); // NOI18N
        FormInput.add(jLabel26);
        jLabel26.setBounds(20, 250, 145, 23);

        Diagnosa1.setHighlighter(null);
        Diagnosa1.setName("Diagnosa1"); // NOI18N
        Diagnosa1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Diagnosa1ActionPerformed(evt);
            }
        });
        Diagnosa1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Diagnosa1KeyPressed(evt);
            }
        });
        FormInput.add(Diagnosa1);
        Diagnosa1.setBounds(170, 160, 520, 23);

        jLabel27.setText("1.");
        jLabel27.setName("jLabel27"); // NOI18N
        FormInput.add(jLabel27);
        jLabel27.setBounds(20, 160, 145, 23);

        Diagnosa4.setHighlighter(null);
        Diagnosa4.setName("Diagnosa4"); // NOI18N
        Diagnosa4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Diagnosa4KeyPressed(evt);
            }
        });
        FormInput.add(Diagnosa4);
        Diagnosa4.setBounds(170, 250, 520, 23);

        scrollPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane2.setName("scrollPane2"); // NOI18N

        PemeriksaanFisik.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        PemeriksaanFisik.setColumns(20);
        PemeriksaanFisik.setRows(5);
        PemeriksaanFisik.setName("PemeriksaanFisik"); // NOI18N
        PemeriksaanFisik.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PemeriksaanFisikKeyPressed(evt);
            }
        });
        scrollPane2.setViewportView(PemeriksaanFisik);

        FormInput.add(scrollPane2);
        scrollPane2.setBounds(160, 80, 530, 50);

        jLabel5.setText("No.Rawat :");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(0, 10, 100, 23);

        jLabel11.setText("Terapi:");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(80, 400, 70, 23);

        scrollPane6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane6.setName("scrollPane6"); // NOI18N

        Terapi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Terapi.setColumns(20);
        Terapi.setRows(5);
        Terapi.setName("Terapi"); // NOI18N
        Terapi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TerapiKeyPressed(evt);
            }
        });
        scrollPane6.setViewportView(Terapi);

        FormInput.add(scrollPane6);
        scrollPane6.setBounds(150, 400, 541, 50);

        jLabel29.setText("Diagnosa:");
        jLabel29.setName("jLabel29"); // NOI18N
        FormInput.add(jLabel29);
        jLabel29.setBounds(20, 140, 97, 23);

        jLabel30.setText("2.");
        jLabel30.setName("jLabel30"); // NOI18N
        FormInput.add(jLabel30);
        jLabel30.setBounds(20, 190, 145, 23);

        Diagnosa2.setHighlighter(null);
        Diagnosa2.setName("Diagnosa2"); // NOI18N
        Diagnosa2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Diagnosa2KeyPressed(evt);
            }
        });
        FormInput.add(Diagnosa2);
        Diagnosa2.setBounds(170, 190, 520, 23);

        jLabel31.setText("Kode ICD 9 :");
        jLabel31.setName("jLabel31"); // NOI18N
        FormInput.add(jLabel31);
        jLabel31.setBounds(730, 300, 70, 23);

        KodeDiagnosaUtama.setHighlighter(null);
        KodeDiagnosaUtama.setName("KodeDiagnosaUtama"); // NOI18N
        KodeDiagnosaUtama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeDiagnosaUtamaKeyPressed(evt);
            }
        });
        FormInput.add(KodeDiagnosaUtama);
        KodeDiagnosaUtama.setBounds(730, 160, 75, 23);

        KodeDiagnosaSekunder1.setHighlighter(null);
        KodeDiagnosaSekunder1.setName("KodeDiagnosaSekunder1"); // NOI18N
        KodeDiagnosaSekunder1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeDiagnosaSekunder1KeyPressed(evt);
            }
        });
        FormInput.add(KodeDiagnosaSekunder1);
        KodeDiagnosaSekunder1.setBounds(730, 190, 75, 23);

        KodeDiagnosaSekunder2.setHighlighter(null);
        KodeDiagnosaSekunder2.setName("KodeDiagnosaSekunder2"); // NOI18N
        KodeDiagnosaSekunder2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeDiagnosaSekunder2KeyPressed(evt);
            }
        });
        FormInput.add(KodeDiagnosaSekunder2);
        KodeDiagnosaSekunder2.setBounds(730, 220, 75, 23);

        KodeDiagnosaSekunder3.setHighlighter(null);
        KodeDiagnosaSekunder3.setName("KodeDiagnosaSekunder3"); // NOI18N
        KodeDiagnosaSekunder3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeDiagnosaSekunder3KeyPressed(evt);
            }
        });
        FormInput.add(KodeDiagnosaSekunder3);
        KodeDiagnosaSekunder3.setBounds(730, 250, 75, 23);

        jLabel32.setText("Prosedur dan Tindakan:");
        jLabel32.setName("jLabel32"); // NOI18N
        FormInput.add(jLabel32);
        jLabel32.setBounds(0, 290, 145, 23);

        Prosedur1.setHighlighter(null);
        Prosedur1.setName("Prosedur1"); // NOI18N
        Prosedur1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Prosedur1KeyPressed(evt);
            }
        });
        FormInput.add(Prosedur1);
        Prosedur1.setBounds(170, 330, 520, 23);

        KodeProsedurUtama.setHighlighter(null);
        KodeProsedurUtama.setName("KodeProsedurUtama"); // NOI18N
        KodeProsedurUtama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeProsedurUtamaKeyPressed(evt);
            }
        });
        FormInput.add(KodeProsedurUtama);
        KodeProsedurUtama.setBounds(730, 330, 75, 23);

        Prosedur2.setHighlighter(null);
        Prosedur2.setName("Prosedur2"); // NOI18N
        Prosedur2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Prosedur2KeyPressed(evt);
            }
        });
        FormInput.add(Prosedur2);
        Prosedur2.setBounds(170, 360, 520, 23);

        jLabel33.setText("2.");
        jLabel33.setName("jLabel33"); // NOI18N
        FormInput.add(jLabel33);
        jLabel33.setBounds(20, 360, 145, 23);

        KodeProsedurSekunder1.setHighlighter(null);
        KodeProsedurSekunder1.setName("KodeProsedurSekunder1"); // NOI18N
        KodeProsedurSekunder1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KodeProsedurSekunder1KeyPressed(evt);
            }
        });
        FormInput.add(KodeProsedurSekunder1);
        KodeProsedurSekunder1.setBounds(730, 360, 75, 23);

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
        BtnDokter1.setBounds(80, 100, 28, 23);

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
        BtnDokter4.setBounds(120, 430, 28, 23);

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
        BtnDokter5.setBounds(110, 100, 28, 23);

        jLabel36.setText("Alasan Dirawat:");
        jLabel36.setName("jLabel36"); // NOI18N
        FormInput.add(jLabel36);
        jLabel36.setBounds(340, 470, 90, 30);

        Dirawat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        Dirawat.setName("Dirawat"); // NOI18N
        Dirawat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DirawatActionPerformed(evt);
            }
        });
        Dirawat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DirawatKeyPressed(evt);
            }
        });
        FormInput.add(Dirawat);
        Dirawat.setBounds(150, 470, 140, 30);

        Alasan.setHighlighter(null);
        Alasan.setName("Alasan"); // NOI18N
        Alasan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AlasanActionPerformed(evt);
            }
        });
        Alasan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AlasanKeyPressed(evt);
            }
        });
        FormInput.add(Alasan);
        Alasan.setBounds(440, 470, 360, 24);

        jLabel38.setText("1.");
        jLabel38.setName("jLabel38"); // NOI18N
        FormInput.add(jLabel38);
        jLabel38.setBounds(20, 330, 145, 23);

        jLabel39.setText("Kode ICD 10 :");
        jLabel39.setName("jLabel39"); // NOI18N
        FormInput.add(jLabel39);
        jLabel39.setBounds(720, 140, 70, 23);

        jLabel40.setText("Dirawat inapkan:");
        jLabel40.setName("jLabel40"); // NOI18N
        FormInput.add(jLabel40);
        jLabel40.setBounds(60, 470, 81, 30);

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
        } else if (PemeriksaanFisik.getText().equals("")) {
            Valid.textKosong(PemeriksaanFisik, "Keluhan utama riwayat penyakit yang postif");
        } else if (Diagnosa1.getText().equals("")) {
            Valid.textKosong(Diagnosa1, "Diagnosa Utama");
        } else {
            if (Sequel.menyimpantf("resume_pasien_jkn", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "No.Rawat", 18, new String[]{
                TNoRw.getText(), KodeDokter.getText(), PemeriksaanFisik.getText(),
                Diagnosa1.getText(), KodeDiagnosaUtama.getText(), Diagnosa2.getText(), KodeDiagnosaSekunder1.getText(), Diagnosa3.getText(),
                KodeDiagnosaSekunder2.getText(), Diagnosa4.getText(), KodeDiagnosaSekunder3.getText(),
                Prosedur1.getText(), KodeProsedurUtama.getText(), Prosedur2.getText(), KodeProsedurSekunder1.getText(), Terapi.getText(),
                Dirawat.getSelectedItem().toString(), Alasan.getText()
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
            Valid.pindah(evt, Terapi, BtnBatal);
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
        } else if (PemeriksaanFisik.getText().equals("")) {
            Valid.textKosong(PemeriksaanFisik, "Keluhan utama riwayat penyakit yang postif");
        } else if (Diagnosa1.getText().equals("")) {
            Valid.textKosong(Diagnosa1, "Diagnosa Utama");
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
                        + "resume_pasien_jkn.kd_dokter,dokter.nm_dokter,resume_pasien_jkn.kondisi_pulang,resume_pasien_jkn.keluhan_utama,resume_pasien_jkn.jalannya_penyakit, "
                        + "resume_pasien_jkn.pemeriksaan_penunjang,resume_pasien_jkn.hasil_laborat,resume_pasien_jkn.diagnosa_utama,resume_pasien_jkn.kd_diagnosa_utama, "
                        + "resume_pasien_jkn.diagnosa_sekunder,resume_pasien_jkn.kd_diagnosa_sekunder,resume_pasien_jkn.diagnosa_sekunder2,resume_pasien_jkn.kd_diagnosa_sekunder2, "
                        + "resume_pasien_jkn.diagnosa_sekunder3,resume_pasien_jkn.kd_diagnosa_sekunder3,resume_pasien_jkn.diagnosa_sekunder4,resume_pasien_jkn.kd_diagnosa_sekunder4, "
                        + "resume_pasien_jkn.prosedur_utama,resume_pasien_jkn.kd_prosedur_utama,resume_pasien_jkn.prosedur_sekunder,resume_pasien_jkn.kd_prosedur_sekunder, "
                        + "resume_pasien_jkn.prosedur_sekunder2,resume_pasien_jkn.kd_prosedur_sekunder2,resume_pasien_jkn.prosedur_sekunder3,resume_pasien_jkn.kd_prosedur_sekunder3, "
                        + "resume_pasien_jkn.obat_pulang,resume_pasien_jkn.keadaan,resume_pasien_jkn.ket_keadaan,resume_pasien_jkn.cara_keluar,resume_pasien_jkn.ket_keluar from resume_pasien_jkn inner join reg_periksa on resume_pasien_jkn.no_rawat=reg_periksa.no_rawat  "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis inner join dokter on resume_pasien_jkn.kd_dokter=dokter.kd_dokter "
                        + "where reg_periksa.tgl_registrasi between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' "
                        + "order by reg_periksa.tgl_registrasi", param);
            } else {
                Valid.MyReportqry("rptDataResumePasien.jasper", "report", "::[ Data Resume Pasien ]::",
                        "select reg_periksa.tgl_registrasi,reg_periksa.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien, "
                        + "resume_pasien_jkn.kd_dokter,dokter.nm_dokter,resume_pasien_jkn.kondisi_pulang,resume_pasien_jkn.keluhan_utama,resume_pasien_jkn.jalannya_penyakit, "
                        + "resume_pasien_jkn.pemeriksaan_penunjang,resume_pasien_jkn.hasil_laborat,resume_pasien_jkn.diagnosa_utama,resume_pasien_jkn.kd_diagnosa_utama, "
                        + "resume_pasien_jkn.diagnosa_sekunder,resume_pasien_jkn.kd_diagnosa_sekunder,resume_pasien_jkn.diagnosa_sekunder2,resume_pasien_jkn.kd_diagnosa_sekunder2, "
                        + "resume_pasien_jkn.diagnosa_sekunder3,resume_pasien_jkn.kd_diagnosa_sekunder3,resume_pasien_jkn.diagnosa_sekunder4,resume_pasien_jkn.kd_diagnosa_sekunder4, "
                        + "resume_pasien_jkn.prosedur_utama,resume_pasien_jkn.kd_prosedur_utama,resume_pasien_jkn.prosedur_sekunder,resume_pasien_jkn.kd_prosedur_sekunder, "
                        + "resume_pasien_jkn.prosedur_sekunder2,resume_pasien_jkn.kd_prosedur_sekunder2,resume_pasien_jkn.prosedur_sekunder3,resume_pasien_jkn.kd_prosedur_sekunder3, "
                        + "resume_pasien_jkn.obat_pulang,resume_pasien_jkn.keadaan,resume_pasien_jkn.ket_keadaan,resume_pasien_jkn.cara_keluar,resume_pasien_jkn.ket_keluar from resume_pasien_jkn inner join reg_periksa on resume_pasien_jkn.no_rawat=reg_periksa.no_rawat  "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis inner join dokter on resume_pasien_jkn.kd_dokter=dokter.kd_dokter "
                        + "where reg_periksa.tgl_registrasi between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' and "
                        + "(reg_periksa.no_rkm_medis like '%" + TCari.getText().trim() + "%' or pasien.nm_pasien like '%" + TCari.getText().trim() + "%' or "
                        + "resume_pasien_jkn.kd_dokter like '%" + TCari.getText().trim() + "%' or dokter.nm_dokter like '%" + TCari.getText().trim() + "%' or "
                        + "resume_pasien_jkn.kondisi_pulang like '%" + TCari.getText().trim() + "%' or resume_pasien_jkn.kd_diagnosa_utama like '%" + TCari.getText().trim() + "%' or "
                        + "resume_pasien_jkn.diagnosa_utama like '%" + TCari.getText().trim() + "%' or resume_pasien_jkn.prosedur_utama like '%" + TCari.getText().trim() + "%' or "
                        + "reg_periksa.no_rawat like '%" + TCari.getText().trim() + "%' or resume_pasien_jkn.kd_prosedur_utama like '%" + TCari.getText().trim() + "%') "
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

    private void Diagnosa3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Diagnosa3KeyPressed
        Valid.pindah(evt, KodeDiagnosaSekunder1, KodeDiagnosaSekunder2);
    }//GEN-LAST:event_Diagnosa3KeyPressed

    private void Diagnosa1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Diagnosa1KeyPressed
        //   Valid.pindah(evt,HasilLaborat,KodeDiagnosaUtama);
    }//GEN-LAST:event_Diagnosa1KeyPressed

    private void Diagnosa4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Diagnosa4KeyPressed
        Valid.pindah(evt, KodeDiagnosaSekunder2, KodeDiagnosaSekunder3);
    }//GEN-LAST:event_Diagnosa4KeyPressed

    private void Diagnosa2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Diagnosa2KeyPressed
        Valid.pindah(evt, KodeDiagnosaUtama, KodeDiagnosaSekunder1);
    }//GEN-LAST:event_Diagnosa2KeyPressed

    private void KodeDiagnosaUtamaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeDiagnosaUtamaKeyPressed
        Valid.pindah(evt, Diagnosa1, Diagnosa2);
    }//GEN-LAST:event_KodeDiagnosaUtamaKeyPressed

    private void KodeDiagnosaSekunder1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeDiagnosaSekunder1KeyPressed
        Valid.pindah(evt, Diagnosa2, Diagnosa3);
    }//GEN-LAST:event_KodeDiagnosaSekunder1KeyPressed

    private void KodeDiagnosaSekunder2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeDiagnosaSekunder2KeyPressed
        Valid.pindah(evt, Diagnosa3, Diagnosa4);
    }//GEN-LAST:event_KodeDiagnosaSekunder2KeyPressed

    private void KodeDiagnosaSekunder3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeDiagnosaSekunder3KeyPressed
        //   Valid.pindah(evt,DiagnosaSekunder3,DiagnosaSekunder4);
    }//GEN-LAST:event_KodeDiagnosaSekunder3KeyPressed

    private void Prosedur1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Prosedur1KeyPressed
        //     Valid.pindah(evt,KodeDiagnosaSekunder4,KodeProsedurUtama);
    }//GEN-LAST:event_Prosedur1KeyPressed

    private void KodeProsedurUtamaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeProsedurUtamaKeyPressed
        Valid.pindah(evt, Prosedur1, Prosedur2);
    }//GEN-LAST:event_KodeProsedurUtamaKeyPressed

    private void Prosedur2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Prosedur2KeyPressed
        Valid.pindah(evt, KodeProsedurUtama, KodeProsedurSekunder1);
    }//GEN-LAST:event_Prosedur2KeyPressed

    private void KodeProsedurSekunder1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeProsedurSekunder1KeyPressed
        //   Valid.pindah(evt,ProsedurSekunder1,ProsedurSekunder2);
    }//GEN-LAST:event_KodeProsedurSekunder1KeyPressed

    private void KodeDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KodeDokterKeyPressed
        //    Valid.pindah(evt,TCari,Kondisi);
    }//GEN-LAST:event_KodeDokterKeyPressed

    private void BtnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokterActionPerformed
        dokter.emptTeks();
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    }//GEN-LAST:event_BtnDokterActionPerformed

    private void BtnDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnDokterKeyPressed
        //     Valid.pindah(evt,TCari,Kondisi);
    }//GEN-LAST:event_BtnDokterKeyPressed

    private void PemeriksaanFisikKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PemeriksaanFisikKeyPressed
        /*    if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            if(evt.isShiftDown()){
                JalannyaPenyakit.requestFocus();
            }
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            Kondisi.requestFocus();
        } */
    }//GEN-LAST:event_PemeriksaanFisikKeyPressed

    private void TerapiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TerapiKeyPressed
        /*    if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            if(evt.isShiftDown()){
                BtnSimpan.requestFocus();
            }
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            KodeProsedurSekunder3.requestFocus();
        } */
    }//GEN-LAST:event_TerapiKeyPressed

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

    private void DirawatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DirawatKeyPressed
        Valid.pindah(evt, Edukasi, Alasan);
    }//GEN-LAST:event_DirawatKeyPressed

    private void AlasanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AlasanKeyPressed
        //     Valid.pindah(evt,Keadaan2,CaraKeluar2);
    }//GEN-LAST:event_AlasanKeyPressed

    private void DirawatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DirawatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DirawatActionPerformed

    private void AlasanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AlasanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_AlasanActionPerformed

    private void Diagnosa1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Diagnosa1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Diagnosa1ActionPerformed

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
            param.put("logo2", Sequel.cariGambar("select setting.logo2 from setting"));
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

            Valid.MyReport("rptLaporanResumeJkn.jasper", "report", "::[ Laporan Resume Pasien ]::", param);
        }
    }//GEN-LAST:event_MnLaporanResumeActionPerformed

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
            param.put("logo2", Sequel.cariGambar("select setting.logo2 from setting"));
            param.put("logobsre", Sequel.cariGambar("select bsre from gambar"));
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
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
        //    param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString() + "\nID " + (finger.equals("") ? tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString() : finger) + "\n" + tanggal);

            Valid.MyReportPDF2("rptLaporanResumeJknTte.jasper", "report", "::[ Laporan Resume Pasien ]::", param);
            File f = new File("./report/rptLaporanResumeJknTte.pdf");

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
                        String fileName = "ResumeRI" + TNoRw.getText().trim().replaceAll("/", "") + "_" + TNoRM.getText().trim().replaceAll(" ", "") + "_" + TPasien.getText().trim().replaceAll(" ", "") + ".pdf";
                        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                        if (response.getCode() == 200) {
                            try ( FileOutputStream fos = new FileOutputStream(new File("ResumeRI" + TNoRw.getText().trim().replaceAll("/", "") + "_" + TNoRM.getText().trim().replaceAll(" ", "") + "_" + TPasien.getText().trim().replaceAll(" ", "") + ".pdf"))) {
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

                                Desktop.getDesktop().browse(new File("ResumeRI" + TNoRw.getText().trim().replaceAll("/", "") + "_" + TNoRM.getText().trim().replaceAll(" ", "") + "_" + TPasien.getText().trim().replaceAll(" ", "") + ".pdf").toURI());
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
                    String fileName = "ResumeRI" + TNoRw.getText().trim().replaceAll("/", "") + "_" + TNoRM.getText().trim().replaceAll(" ", "") + "_" + TPasien.getText().trim().replaceAll(" ", "") + ".pdf";
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
                String fileName = "ResumeRI" + TNoRw.getText().trim().replaceAll("/", "") + "_" + TNoRM.getText().trim().replaceAll(" ", "") + "_" + TPasien.getText().trim().replaceAll(" ", "") + ".pdf";

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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMDataResumePasienRalanJKN dialog = new RMDataResumePasienRalanJKN(new javax.swing.JFrame(), true);
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
    private widget.TextBox Alasan;
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnClosePhrase;
    private widget.Button BtnDokter;
    private widget.Button BtnDokter1;
    private widget.Button BtnDokter4;
    private widget.Button BtnDokter5;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.Button BtnSimpanTandaTangan;
    private widget.CekBox ChkInput;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.TextBox Diagnosa1;
    private widget.TextBox Diagnosa2;
    private widget.TextBox Diagnosa3;
    private widget.TextBox Diagnosa4;
    private widget.ComboBox Dirawat;
    private widget.PanelBiasa FormInput;
    private widget.TextBox KodeDiagnosaSekunder1;
    private widget.TextBox KodeDiagnosaSekunder2;
    private widget.TextBox KodeDiagnosaSekunder3;
    private widget.TextBox KodeDiagnosaUtama;
    private widget.TextBox KodeDokter;
    private widget.TextBox KodeProsedurSekunder1;
    private widget.TextBox KodeProsedurUtama;
    private widget.Label LCount;
    private javax.swing.JMenuItem MnLaporanResume;
    private javax.swing.JMenuItem MnLaporanResumeESign;
    private widget.TextBox NamaDokter;
    private javax.swing.JPanel PanelInput;
    private widget.TextArea PemeriksaanFisik;
    private widget.TextBox Phrase;
    private widget.TextBox Prosedur1;
    private widget.TextBox Prosedur2;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.TextArea Terapi;
    private javax.swing.JDialog WindowPhrase;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame8;
    private widget.Label jLabel11;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel25;
    private widget.Label jLabel26;
    private widget.Label jLabel27;
    private widget.Label jLabel29;
    private widget.Label jLabel30;
    private widget.Label jLabel31;
    private widget.Label jLabel32;
    private widget.Label jLabel33;
    private widget.Label jLabel36;
    private widget.Label jLabel38;
    private widget.Label jLabel39;
    private widget.Label jLabel4;
    private widget.Label jLabel40;
    private widget.Label jLabel45;
    private widget.Label jLabel46;
    private widget.Label jLabel5;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.Label label14;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.panelisi panelisi5;
    private widget.ScrollPane scrollInput;
    private widget.ScrollPane scrollPane2;
    private widget.ScrollPane scrollPane6;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            if (TCari.getText().equals("")) {
                ps = koneksi.prepareStatement(
                        "SELECT reg_periksa.tgl_registrasi, reg_periksa.no_rawat, reg_periksa.no_rkm_medis, pasien.nm_pasien, resume_pasien_jkn.kd_dokter, resume_pasien_jkn.pemeriksaan_fisik, "
                        + "resume_pasien_jkn.diagnosa_utama, resume_pasien_jkn.kd_diagnosa_utama, resume_pasien_jkn.diagnosa_sekunder, resume_pasien_jkn.kd_diagnosa_sekunder, "
                        + "resume_pasien_jkn.diagnosa_sekunder2, resume_pasien_jkn.kd_diagnosa_sekunder2, resume_pasien_jkn.diagnosa_sekunder3, resume_pasien_jkn.kd_diagnosa_sekunder3, "
                        + "resume_pasien_jkn.prosedur_utama, resume_pasien_jkn.kd_prosedur_utama, resume_pasien_jkn.prosedur_sekunder, resume_pasien_jkn.kd_prosedur_sekunder, "
                        + "resume_pasien_jkn.obat_pulang, resume_pasien_jkn.rawatinap, resume_pasien_jkn.alasan, dokter.nm_dokter FROM reg_periksa INNER JOIN pasien ON "
                        + "reg_periksa.no_rkm_medis = pasien.no_rkm_medis INNER JOIN dokter INNER JOIN resume_pasien_jkn ON dokter.kd_dokter = resume_pasien_jkn.kd_dokter AND "
                        + "reg_periksa.no_rawat = resume_pasien_jkn.no_rawat "
                        + "where reg_periksa.tgl_registrasi between ? and ? order by reg_periksa.tgl_registrasi");
            } else {
                ps = koneksi.prepareStatement(
                        "SELECT reg_periksa.tgl_registrasi, reg_periksa.no_rawat, reg_periksa.no_rkm_medis, pasien.nm_pasien, resume_pasien_jkn.kd_dokter, resume_pasien_jkn.pemeriksaan_fisik, "
                        + "resume_pasien_jkn.diagnosa_utama, resume_pasien_jkn.kd_diagnosa_utama, resume_pasien_jkn.diagnosa_sekunder, resume_pasien_jkn.kd_diagnosa_sekunder, "
                        + "resume_pasien_jkn.diagnosa_sekunder2, resume_pasien_jkn.kd_diagnosa_sekunder2, resume_pasien_jkn.diagnosa_sekunder3, resume_pasien_jkn.kd_diagnosa_sekunder3, "
                        + "resume_pasien_jkn.prosedur_utama, resume_pasien_jkn.kd_prosedur_utama, resume_pasien_jkn.prosedur_sekunder, resume_pasien_jkn.kd_prosedur_sekunder, "
                        + "resume_pasien_jkn.obat_pulang, resume_pasien_jkn.rawatinap, resume_pasien_jkn.alasan, dokter.nm_dokter FROM reg_periksa INNER JOIN pasien ON "
                        + "reg_periksa.no_rkm_medis = pasien.no_rkm_medis INNER JOIN dokter INNER JOIN resume_pasien_jkn ON dokter.kd_dokter = resume_pasien_jkn.kd_dokter AND "
                        + "reg_periksa.no_rawat = resume_pasien_jkn.no_rawat "
                        + "where reg_periksa.tgl_registrasi between ? and ? and "
                        + "(reg_periksa.no_rkm_medis like ? or pasien.nm_pasien like ? or resume_pasien_jkn.kd_dokter like ? or "
                        + "dokter.nm_dokter like ? or resume_pasien_jkn.alasan like ? or resume_pasien_jkn.kd_diagnosa_utama like ? or "
                        + "resume_pasien_jkn.diagnosa_utama like ? or resume_pasien_jkn.prosedur_utama like ? or reg_periksa.no_rawat like ? or "
                        + "resume_pasien_jkn.kd_prosedur_utama like ?) order by reg_periksa.tgl_registrasi");
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
                        rs.getString("kd_dokter"), rs.getString("nm_dokter"), rs.getString("pemeriksaan_fisik"), rs.getString("diagnosa_utama"),
                        rs.getString("kd_diagnosa_utama"), rs.getString("diagnosa_sekunder"), rs.getString("kd_diagnosa_sekunder"), rs.getString("diagnosa_sekunder2"),
                        rs.getString("kd_diagnosa_sekunder2"), rs.getString("diagnosa_sekunder3"), rs.getString("kd_diagnosa_sekunder3"), rs.getString("prosedur_utama"),
                        rs.getString("kd_prosedur_utama"), rs.getString("prosedur_sekunder"), rs.getString("kd_prosedur_sekunder"), rs.getString("obat_pulang"),
                        rs.getString("rawatinap"), rs.getString("alasan")
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
        PemeriksaanFisik.setText("");
        Terapi.setText("");
        Diagnosa1.setText("");
        Diagnosa2.setText("");
        Diagnosa3.setText("");
        Diagnosa4.setText("");
        Prosedur1.setText("");
        Prosedur2.setText("");
        KodeDiagnosaUtama.setText("");
        KodeDiagnosaSekunder1.setText("");
        KodeDiagnosaSekunder2.setText("");
        KodeDiagnosaSekunder3.setText("");
        KodeProsedurUtama.setText("");
        KodeProsedurSekunder1.setText("");
        Alasan.setText("");

    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
            KodeDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
            NamaDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            PemeriksaanFisik.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
            Diagnosa1.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString());
            KodeDiagnosaUtama.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString());
            Diagnosa2.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString());
            KodeDiagnosaSekunder1.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString());
            Diagnosa3.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString());
            KodeDiagnosaSekunder2.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString());
            Diagnosa4.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 13).toString());
            KodeDiagnosaSekunder3.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 14).toString());
            Prosedur1.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 15).toString());
            KodeProsedurUtama.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 16).toString());
            Prosedur2.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 17).toString());
            KodeProsedurSekunder1.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 18).toString());
            Terapi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 19).toString());
            Dirawat.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 20).toString());
            Alasan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 21).toString());

        }
    }

    private void isRawat() {
        Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat='" + TNoRw.getText() + "' ", TNoRM);
        if (Sequel.cariInteger("select count(no_rawat) from penilaian_medis_igd where no_rawat='" + TNoRw.getText() + "' ") > 0) {
            PemeriksaanFisik.setText(Sequel.cariIsi("select ket_fisik from penilaian_medis_igd where no_rawat=?", TNoRw.getText()));
            Terapi.setText(Sequel.cariIsi("select tata from penilaian_medis_igd where no_rawat=?", TNoRw.getText()));
        } else if (Sequel.cariInteger("select count(no_rawat) from penilaian_medis_ralan where no_rawat='" + TNoRw.getText() + "' ") > 0) {
            PemeriksaanFisik.setText(Sequel.cariIsi("select ket_fisik from penilaian_medis_ralan where no_rawat=?", TNoRw.getText()));
            Terapi.setText(Sequel.cariIsi("select tata from penilaian_medis_ralan_anak where no_rawat=?", TNoRw.getText()));
        } else if (Sequel.cariInteger("select count(no_rawat) from penilaian_medis_ralan_anak where no_rawat='" + TNoRw.getText() + "' ") > 0) {
            PemeriksaanFisik.setText(Sequel.cariIsi("select ket_fisik from penilaian_medis_ralan_anak where no_rawat=?", TNoRw.getText()));
        } else if (Sequel.cariInteger("select count(no_rawat) from penilaian_medis_ralan_bedah where no_rawat='" + TNoRw.getText() + "' ") > 0) {
            PemeriksaanFisik.setText(Sequel.cariIsi("select lainnya keluhan from penilaian_medis_ralan_bedah where no_rawat=?", TNoRw.getText()));
            Terapi.setText(Sequel.cariIsi("select terapi from penilaian_medis_ralan_bedah where no_rawat=?", TNoRw.getText()));
        } else if (Sequel.cariInteger("select count(no_rawat) from penilaian_medis_ralan_bedah_mulut where no_rawat='" + TNoRw.getText() + "' ") > 0) {
            PemeriksaanFisik.setText(Sequel.cariIsi("select pemeriksaan from penilaian_medis_ralan_bedah_mulut where no_rawat=?", TNoRw.getText()));
            Terapi.setText(Sequel.cariIsi("select terapi from penilaian_medis_ralan_geriatri where no_rawat=?", TNoRw.getText()));
        } else if (Sequel.cariInteger("select count(no_rawat) from penilaian_medis_ralan_geriatri where no_rawat='" + TNoRw.getText() + "' ") > 0) {
            PemeriksaanFisik.setText(Sequel.cariIsi("select lainnya from penilaian_medis_ralan_geriatri where no_rawat=?", TNoRw.getText()));
            Terapi.setText(Sequel.cariIsi("select terapi from penilaian_medis_ralan_geriatri where no_rawat=?", TNoRw.getText()));
        } else if (Sequel.cariInteger("select count(no_rawat) from penilaian_medis_ralan_kandungan where no_rawat='" + TNoRw.getText() + "' ") > 0) {
            PemeriksaanFisik.setText(Sequel.cariIsi("select ket_fisik from penilaian_medis_ralan_kandungan where no_rawat=?", TNoRw.getText()));
            Terapi.setText(Sequel.cariIsi("select tata from penilaian_medis_ralan_kandungan where no_rawat=?", TNoRw.getText()));
        } else if (Sequel.cariInteger("select count(no_rawat) from penilaian_medis_ralan_mata where no_rawat='" + TNoRw.getText() + "' ") > 0) {
            PemeriksaanFisik.setText(Sequel.cariIsi("select ket_fisik from penilaian_medis_ralan_mata where no_rawat=?", TNoRw.getText()));
            Terapi.setText(Sequel.cariIsi("select terapi from penilaian_medis_ralan_mata where no_rawat=?", TNoRw.getText()));
        } else if (Sequel.cariInteger("select count(no_rawat) from penilaian_medis_ralan_neurologi where no_rawat='" + TNoRw.getText() + "' ") > 0) {
            PemeriksaanFisik.setText(Sequel.cariIsi("select concat lainnya from penilaian_medis_ralan_neurologi where no_rawat=?", TNoRw.getText()));
            Terapi.setText(Sequel.cariIsi("select terapi from penilaian_medis_ralan_neurologi where no_rawat=?", TNoRw.getText()));
        } else if (Sequel.cariInteger("select count(no_rawat) from penilaian_medis_ralan_orthopedi where no_rawat='" + TNoRw.getText() + "' ") > 0) {
            PemeriksaanFisik.setText(Sequel.cariIsi("select lainnya from penilaian_medis_ralan_orthopedi where no_rawat=?", TNoRw.getText()));
            Terapi.setText(Sequel.cariIsi("select terapi from penilaian_medis_ralan_orthopedi where no_rawat=?", TNoRw.getText()));
        } else if (Sequel.cariInteger("select count(no_rawat) from penilaian_medis_ralan_penyakit_dalam where no_rawat='" + TNoRw.getText() + "' ") > 0) {
            PemeriksaanFisik.setText(Sequel.cariIsi("select lainnya from penilaian_medis_ralan_penyakit_dalam where no_rawat=?", TNoRw.getText()));
            Terapi.setText(Sequel.cariIsi("select terapi from penilaian_medis_ralan_penyakit_dalam where no_rawat=?", TNoRw.getText()));
        } else if (Sequel.cariInteger("select count(no_rawat) from penilaian_medis_ralan_psikiatrik where no_rawat='" + TNoRw.getText() + "' ") > 0) {
            PemeriksaanFisik.setText(Sequel.cariIsi("select ketfisik from penilaian_medis_ralan_psikiatrik where no_rawat=?", TNoRw.getText()));
            Terapi.setText(Sequel.cariIsi("select tata from penilaian_medis_ralan_psikiatrik where no_rawat=?", TNoRw.getText()));
        } else if (Sequel.cariInteger("select count(no_rawat) from penilaian_medis_ralan_tht where no_rawat='" + TNoRw.getText() + "' ") > 0) {
            PemeriksaanFisik.setText(Sequel.cariIsi("select lainnya from penilaian_medis_ralan_tht where no_rawat=?", TNoRw.getText()));
            Terapi.setText(Sequel.cariIsi("select tata from penilaian_medis_ralan_tht where no_rawat=?", TNoRw.getText()));
        } else if (Sequel.cariInteger("select count(no_rawat) from pemeriksaan_ralan where no_rawat='" + TNoRw.getText() + "' ") > 0) {
            DataSOAP();
        }
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

        try {
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
                        Diagnosa1.setText(rs.getString("nm_penyakit"));
                    }

                    if (rs.getInt("prioritas") == 2) {
                        KodeDiagnosaSekunder1.setText(rs.getString("kd_penyakit"));
                        Diagnosa2.setText(rs.getString("nm_penyakit"));
                    }

                    if (rs.getInt("prioritas") == 3) {
                        KodeDiagnosaSekunder2.setText(rs.getString("kd_penyakit"));
                        Diagnosa3.setText(rs.getString("nm_penyakit"));
                    }

                    if (rs.getInt("prioritas") == 4) {
                        KodeDiagnosaSekunder3.setText(rs.getString("kd_penyakit"));
                        Diagnosa4.setText(rs.getString("nm_penyakit"));
                    }

                    if (rs.getInt("prioritas") == 5) {

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
                        Prosedur1.setText(rs.getString("deskripsi_panjang"));
                    }

                    if (rs.getInt("prioritas") == 2) {
                        KodeProsedurSekunder1.setText(rs.getString("kode"));
                        Prosedur2.setText(rs.getString("deskripsi_panjang"));
                    }

                    if (rs.getInt("prioritas") == 3) {

                    }

                    if (rs.getInt("prioritas") == 4) {

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
        if (Sequel.mengedittf("resume_pasien_jkn", "no_rawat=?", "no_rawat=?,kd_dokter=?,pemeriksaan_fisik=?,diagnosa_utama=?,kd_diagnosa_utama=?,diagnosa_sekunder=?,kd_diagnosa_sekunder=?,diagnosa_sekunder2=?,kd_diagnosa_sekunder2=?,diagnosa_sekunder3=?,kd_diagnosa_sekunder3=?,prosedur_utama=?,kd_prosedur_utama=?,prosedur_sekunder=?,kd_prosedur_sekunder=?,obat_pulang=?,rawatinap=?,alasan=?", 19, new String[]{
            TNoRw.getText(), KodeDokter.getText(), PemeriksaanFisik.getText(),
            Diagnosa1.getText(), KodeDiagnosaUtama.getText(), Diagnosa2.getText(), KodeDiagnosaSekunder1.getText(), Diagnosa3.getText(),
            KodeDiagnosaSekunder2.getText(), Diagnosa4.getText(), KodeDiagnosaSekunder3.getText(),
            Prosedur1.getText(), KodeProsedurUtama.getText(), Prosedur2.getText(), KodeProsedurSekunder1.getText(), Terapi.getText(),
            Dirawat.getSelectedItem().toString(), Alasan.getText(),
            tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString()
        }) == true) {
            tampil();
            emptTeks();
        }
    }

    private void hapus() {
        if (Sequel.queryu2tf("delete from resume_pasien_jkn where no_rawat=?", 1, new String[]{
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

            // Menggunakan HttpClient 5
            CloseableHttpClient httpClient = HttpClients.createDefault();
            String url = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/upload.php?doc=" + docpath;
            HttpPost postRequest = new HttpPost(url);

            // Membuat multipart entity dengan HttpClient 5
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", data, ContentType.DEFAULT_BINARY, FileName + ".pdf");

            HttpEntity multipart = builder.build();
            postRequest.setEntity(multipart);

            // Eksekusi request
            CloseableHttpResponse response = httpClient.execute(postRequest);

            try {
                // Optional: Cek status response
                int statusCode = response.getCode();
                if (statusCode != 200) {
                    System.out.println("Upload response status: " + statusCode);
                }
            } finally {
                response.close();
                httpClient.close();
            }

            // Menyimpan ke database
            boolean uploadSuccess = false;
            kodeberkas = Sequel.cariIsi("SELECT kode FROM master_berkas_digital WHERE nama LIKE '%resume%'");

            if (Sequel.cariInteger("SELECT COUNT(no_rawat) AS jumlah FROM berkas_digital_perawatan WHERE lokasi_file='pages/upload/" + FileName + ".pdf'") > 0) {
                uploadSuccess = Sequel.mengedittf("berkas_digital_perawatan", "lokasi_file=?", "no_rawat=?,kode=?, lokasi_file=?", 4, new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString().trim(), kodeberkas, "pages/upload/" + FileName + ".pdf", "pages/upload/" + FileName + ".pdf"
                });
            } else {
                uploadSuccess = Sequel.menyimpantf("berkas_digital_perawatan", "?,?,?", "No.Rawat", 3, new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString().trim(), kodeberkas, "pages/upload/" + FileName + ".pdf"
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

            Valid.MyReportPDFWithName("rptLaporanResumeTTE.jasper", "report", "tempfile", FileName, "::[ Laporan Resume Pasien ]::", param);
        }
    }

    private void DataSOAP() {
        try {
            pssoap = koneksi.prepareStatement(
                    "select pemeriksaan_ralan.no_rawat,pemeriksaan_ralan.tgl_perawatan,pemeriksaan_ralan.jam_rawat,pemeriksaan_ralan.suhu_tubuh,pemeriksaan_ralan.tensi,pemeriksaan_ralan.nadi,pemeriksaan_ralan.respirasi,pemeriksaan_ralan.tinggi,pemeriksaan_ralan.berat,pemeriksaan_ralan.spo2,"
                    + "pemeriksaan_ralan.gcs,pemeriksaan_ralan.kesadaran,pemeriksaan_ralan.keluhan,pemeriksaan_ralan.pemeriksaan,pemeriksaan_ralan.lingkar_perut,pemeriksaan_ralan.rtl,pemeriksaan_ralan.penilaian,pemeriksaan_ralan.instruksi,pemeriksaan_ralan.evaluasi,pemeriksaan_ralan.nip "
                    + "from pemeriksaan_ralan where pemeriksaan_ralan.no_rawat=? and nip=?");
            try {
                pssoap.setString(1, TNoRw.getText());
                pssoap.setString(2, KodeDokter.getText());
                rssoap = pssoap.executeQuery();
                if (!rssoap.isBeforeFirst()) { // Jika tidak ada baris hasil
                    JOptionPane.showMessageDialog(null, "Tidak Ada Data SOAP Dokter", "Peringatan", JOptionPane.WARNING_MESSAGE);
                } else {
                    while (rssoap.next()) {
                        PemeriksaanFisik.setText(rssoap.getString("pemeriksaan"));
                        Terapi.setText(rssoap.getString("rtl"));
                        Diagnosa1.setText(rssoap.getString("penilaian"));
                    }
                }
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            } finally {
                if (rssoap != null) {
                    rssoap.close();
                }
                if (pssoap != null) {
                    pssoap.close();
                }
            }
        } catch (Exception e) {
            System.out.println("Notif : " + e);
        }

    }

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

}
