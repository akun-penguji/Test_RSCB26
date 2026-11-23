/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rekammedis;

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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import kepegawaian.DlgCariPegawai;
//import static net.sourceforge.peers.media.MediaMode.file;

/**
 *
 * @author perpustakaan
 */
public final class RMEdukasiPasienKeluargaRawatInap extends javax.swing.JDialog {

    private final DefaultTableModel tabMode, tabModeMasalah;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0, jml = 0, index = 0;
    private DlgCariPegawai pegawai = new DlgCariPegawai(null, false);
    private String finger = "";
    private StringBuilder htmlContent;
    private String TANGGALMUNDUR = "yes";
    private File file;
    private FileWriter fileWriter;
    private String iyem;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode response;
    private FileReader myObj;
    private String[] kode, edukasi;
    private boolean[] pilih;

    /**
     * Creates new form DlgRujuk
     *
     * @param parent
     * @param modal
     */
    public RMEdukasiPasienKeluargaRawatInap(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(628, 674);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.R.M.", "Nama Pasien", "Tgl.Lahir", "Ruangan", "Tanggal/ Jam", "Kode Petugas", "Nama Petugas",
            "Tinggal Bersama", "Hambatan", "Hambatan Keterangan", "Edukasi Diberikan kepada", "Kemampuan Bahasa",
            "Perlu Penerjemah", "Baca dan Tulis", "Kepercayaan Lainnya", "Keterangan Kepercayaan Lain",
            "Kesediaan Menerima Informasi", "Cara Edukasi"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] columnWidths = {105, 65, 160, 65, 120, 122, 90, 150, 100, 160, 96, 150, 80, 110, 104, 156, 170, 170, 98};

        for (int i = 0; i < tabMode.getColumnCount(); i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            column.setPreferredWidth(columnWidths[i]);
        }

        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeMasalah = new DefaultTableModel(null, new Object[]{
            "P", "KODE", "KEBUTUHAN EDUKASI"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                boolean a = false;
                if (colIndex == 0) {
                    a = true;
                }
                return a;
            }
            Class[] types = new Class[]{
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class
            };

            @Override
            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }
        };
        tbMasalahKeperawatan.setModel(tabModeMasalah);

        //tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbObat.getBackground()));
        tbMasalahKeperawatan.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbMasalahKeperawatan.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 3; i++) {
            TableColumn column = tbMasalahKeperawatan.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(20);
            } else if (i == 1) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            } else if (i == 2) {
                column.setPreferredWidth(350);
            }
        }
        tbMasalahKeperawatan.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        NIP.setDocument(new batasInput((byte) 20).getKata(NIP));
        HambatanKet.setDocument(new batasInput((byte) 50).getKata(HambatanKet));
        KepercayaanKet.setDocument(new batasInput((byte) 50).getKata(KepercayaanKet));
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

        pegawai.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (pegawai.getTable().getSelectedRow() != -1) {
                    NIP.setText(pegawai.getTable().getValueAt(pegawai.getTable().getSelectedRow(), 0).toString());
                    NamaPetugas.setText(pegawai.getTable().getValueAt(pegawai.getTable().getSelectedRow(), 1).toString());
                }
                NIP.requestFocus();
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
        jam();

        try {
            TANGGALMUNDUR = koneksiDB.TANGGALMUNDUR();
        } catch (Exception e) {
            TANGGALMUNDUR = "yes";
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
        MnEdukasiPasienKeluarga = new javax.swing.JMenuItem();
        LoadHTML = new widget.editorpane();
        JK = new widget.TextBox();
        TanggalRegistrasi = new widget.TextBox();
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
        Tanggal = new widget.Tanggal();
        TNoRM = new widget.TextBox();
        jLabel16 = new widget.Label();
        Jam = new widget.ComboBox();
        Menit = new widget.ComboBox();
        Detik = new widget.ComboBox();
        ChkKejadian = new widget.CekBox();
        jLabel18 = new widget.Label();
        NIP = new widget.TextBox();
        NamaPetugas = new widget.TextBox();
        btnPetugas = new widget.Button();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        jSeparator2 = new javax.swing.JSeparator();
        Tinggal = new widget.ComboBox();
        HambatanKet = new widget.TextBox();
        jLabel99 = new widget.Label();
        jLabel46 = new widget.Label();
        Bahasa = new widget.TextBox();
        jLabel47 = new widget.Label();
        Penerima = new widget.ComboBox();
        KepercayaanKet = new widget.TextBox();
        Penerjemah = new widget.ComboBox();
        BacaTulis = new widget.ComboBox();
        jLabel50 = new widget.Label();
        Hambatan = new widget.ComboBox();
        Kesediaan = new widget.ComboBox();
        jLabel52 = new widget.Label();
        jLabel53 = new widget.Label();
        jLabel54 = new widget.Label();
        jLabel55 = new widget.Label();
        jLabel56 = new widget.Label();
        jLabel65 = new widget.Label();
        Kepercayaan = new widget.ComboBox();
        jLabel100 = new widget.Label();
        jLabel68 = new widget.Label();
        Edukasi = new widget.ComboBox();
        jLabel10 = new widget.Label();
        Ruangan = new widget.TextBox();
        Scroll6 = new widget.ScrollPane();
        tbMasalahKeperawatan = new widget.Table();
        label12 = new widget.Label();
        TCariMasalah = new widget.TextBox();
        BtnCariMasalah = new widget.Button();
        BtnAllMasalah = new widget.Button();
        BtnTambahMasalah = new widget.Button();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnEdukasiPasienKeluarga.setBackground(new java.awt.Color(255, 255, 254));
        MnEdukasiPasienKeluarga.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnEdukasiPasienKeluarga.setForeground(new java.awt.Color(50, 50, 50));
        MnEdukasiPasienKeluarga.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnEdukasiPasienKeluarga.setText("Formulir Edukasi Pasien & Keluarga Terintegrasi");
        MnEdukasiPasienKeluarga.setName("MnEdukasiPasienKeluarga"); // NOI18N
        MnEdukasiPasienKeluarga.setPreferredSize(new java.awt.Dimension(300, 26));
        MnEdukasiPasienKeluarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnEdukasiPasienKeluargaActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnEdukasiPasienKeluarga);

        LoadHTML.setBorder(null);
        LoadHTML.setName("LoadHTML"); // NOI18N

        JK.setHighlighter(null);
        JK.setName("JK"); // NOI18N

        TanggalRegistrasi.setHighlighter(null);
        TanggalRegistrasi.setName("TanggalRegistrasi"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Edukasi Pasien & Keluarga Terintegrasi Rawat Inap ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
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

        jLabel19.setText("Tanggal :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "08-02-2025" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "08-02-2025" }));
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
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 506));
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
        scrollInput.setPreferredSize(new java.awt.Dimension(102, 557));

        FormInput.setBackground(new java.awt.Color(250, 255, 245));
        FormInput.setBorder(null);
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 483));
        FormInput.setLayout(null);

        jLabel4.setText("No.Rawat :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 10, 80, 23);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(84, 10, 136, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        TPasien.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TPasienKeyPressed(evt);
            }
        });
        FormInput.add(TPasien);
        TPasien.setBounds(336, 10, 285, 23);

        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "08-02-2025" }));
        Tanggal.setDisplayFormat("dd-MM-yyyy");
        Tanggal.setName("Tanggal"); // NOI18N
        Tanggal.setOpaque(false);
        Tanggal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalKeyPressed(evt);
            }
        });
        FormInput.add(Tanggal);
        Tanggal.setBounds(480, 40, 90, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        TNoRM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRMKeyPressed(evt);
            }
        });
        FormInput.add(TNoRM);
        TNoRM.setBounds(222, 10, 112, 23);

        jLabel16.setText("Tanggal :");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setVerifyInputWhenFocusTarget(false);
        FormInput.add(jLabel16);
        jLabel16.setBounds(420, 40, 50, 23);

        Jam.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        Jam.setName("Jam"); // NOI18N
        Jam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JamKeyPressed(evt);
            }
        });
        FormInput.add(Jam);
        Jam.setBounds(570, 40, 62, 23);

        Menit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Menit.setName("Menit"); // NOI18N
        Menit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MenitKeyPressed(evt);
            }
        });
        FormInput.add(Menit);
        Menit.setBounds(640, 40, 62, 23);

        Detik.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Detik.setName("Detik"); // NOI18N
        Detik.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DetikKeyPressed(evt);
            }
        });
        FormInput.add(Detik);
        Detik.setBounds(700, 40, 62, 23);

        ChkKejadian.setBorder(null);
        ChkKejadian.setSelected(true);
        ChkKejadian.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkKejadian.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setName("ChkKejadian"); // NOI18N
        FormInput.add(ChkKejadian);
        ChkKejadian.setBounds(770, 40, 23, 23);

        jLabel18.setText("Petugas :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(10, 70, 70, 23);

        NIP.setEditable(false);
        NIP.setHighlighter(null);
        NIP.setName("NIP"); // NOI18N
        NIP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NIPKeyPressed(evt);
            }
        });
        FormInput.add(NIP);
        NIP.setBounds(90, 70, 94, 23);

        NamaPetugas.setEditable(false);
        NamaPetugas.setName("NamaPetugas"); // NOI18N
        FormInput.add(NamaPetugas);
        NamaPetugas.setBounds(190, 70, 187, 23);

        btnPetugas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPetugas.setMnemonic('2');
        btnPetugas.setToolTipText("ALt+2");
        btnPetugas.setName("btnPetugas"); // NOI18N
        btnPetugas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPetugasActionPerformed(evt);
            }
        });
        btnPetugas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnPetugasKeyPressed(evt);
            }
        });
        FormInput.add(btnPetugas);
        btnPetugas.setBounds(380, 70, 28, 23);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(625, 10, 60, 23);

        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir"); // NOI18N
        FormInput.add(TglLahir);
        TglLahir.setBounds(689, 10, 100, 23);

        jSeparator2.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator2.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator2.setName("jSeparator2"); // NOI18N
        FormInput.add(jSeparator2);
        jSeparator2.setBounds(0, 101, 810, 10);

        Tinggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Anak", "Orangtua", "Sendiri", "Suami", "Istri" }));
        Tinggal.setName("Tinggal"); // NOI18N
        Tinggal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TinggalKeyPressed(evt);
            }
        });
        FormInput.add(Tinggal);
        Tinggal.setBounds(110, 160, 135, 23);

        HambatanKet.setFocusTraversalPolicyProvider(true);
        HambatanKet.setName("HambatanKet"); // NOI18N
        HambatanKet.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                HambatanKetKeyPressed(evt);
            }
        });
        FormInput.add(HambatanKet);
        HambatanKet.setBounds(490, 160, 320, 23);

        jLabel99.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel99.setText("A. ASESMEN KEBUTUHAN EDUKASI");
        jLabel99.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel99.setName("jLabel99"); // NOI18N
        FormInput.add(jLabel99);
        jLabel99.setBounds(10, 120, 220, 23);

        jLabel46.setText("Kemampuan Bahasa:");
        jLabel46.setName("jLabel46"); // NOI18N
        FormInput.add(jLabel46);
        jLabel46.setBounds(250, 190, 120, 23);

        Bahasa.setEditable(false);
        Bahasa.setFocusTraversalPolicyProvider(true);
        Bahasa.setName("Bahasa"); // NOI18N
        Bahasa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BahasaKeyPressed(evt);
            }
        });
        FormInput.add(Bahasa);
        Bahasa.setBounds(370, 190, 440, 23);

        jLabel47.setText("Edukasi diberikan kepada:");
        jLabel47.setName("jLabel47"); // NOI18N
        FormInput.add(jLabel47);
        jLabel47.setBounds(10, 190, 140, 23);

        Penerima.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Pasien", "Ayah", "Ibu", "Suami", "Istri", "Kakak", "Adik" }));
        Penerima.setName("Penerima"); // NOI18N
        Penerima.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PenerimaKeyPressed(evt);
            }
        });
        FormInput.add(Penerima);
        Penerima.setBounds(160, 190, 80, 23);

        KepercayaanKet.setFocusTraversalPolicyProvider(true);
        KepercayaanKet.setName("KepercayaanKet"); // NOI18N
        KepercayaanKet.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KepercayaanKetKeyPressed(evt);
            }
        });
        FormInput.add(KepercayaanKet);
        KepercayaanKet.setBounds(590, 220, 220, 23);

        Penerjemah.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Tidak", "Ya" }));
        Penerjemah.setName("Penerjemah"); // NOI18N
        Penerjemah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PenerjemahKeyPressed(evt);
            }
        });
        FormInput.add(Penerjemah);
        Penerjemah.setBounds(130, 220, 80, 23);

        BacaTulis.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Bisa", "Tidak" }));
        BacaTulis.setName("BacaTulis"); // NOI18N
        BacaTulis.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BacaTulisKeyPressed(evt);
            }
        });
        FormInput.add(BacaTulis);
        BacaTulis.setBounds(300, 220, 80, 23);

        jLabel50.setText("Hambatan  :");
        jLabel50.setName("jLabel50"); // NOI18N
        FormInput.add(jLabel50);
        jLabel50.setBounds(250, 160, 80, 23);

        Hambatan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ada", "Tidak" }));
        Hambatan.setName("Hambatan"); // NOI18N
        Hambatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                HambatanKeyPressed(evt);
            }
        });
        FormInput.add(Hambatan);
        Hambatan.setBounds(330, 160, 150, 23);

        Kesediaan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ya", "Tidak" }));
        Kesediaan.setName("Kesediaan"); // NOI18N
        Kesediaan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KesediaanKeyPressed(evt);
            }
        });
        FormInput.add(Kesediaan);
        Kesediaan.setBounds(180, 250, 130, 23);

        jLabel52.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel52.setText("Tinggal Bersama:");
        jLabel52.setName("jLabel52"); // NOI18N
        FormInput.add(jLabel52);
        jLabel52.setBounds(20, 160, 90, 23);

        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel53.setText("Perlu Penerjemah:");
        jLabel53.setName("jLabel53"); // NOI18N
        FormInput.add(jLabel53);
        jLabel53.setBounds(20, 220, 100, 23);

        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel54.setText("Baca dan Tulis:");
        jLabel54.setName("jLabel54"); // NOI18N
        FormInput.add(jLabel54);
        jLabel54.setBounds(220, 220, 80, 23);

        jLabel55.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel55.setText("Kesediaan Menerima Informasi:");
        jLabel55.setName("jLabel55"); // NOI18N
        FormInput.add(jLabel55);
        jLabel55.setBounds(20, 250, 170, 23);

        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel56.setText("Kebutuhan Edukasi:");
        jLabel56.setName("jLabel56"); // NOI18N
        FormInput.add(jLabel56);
        jLabel56.setBounds(20, 280, 110, 23);

        jLabel65.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel65.setText("Kepercayaan lainnya:");
        jLabel65.setName("jLabel65"); // NOI18N
        FormInput.add(jLabel65);
        jLabel65.setBounds(390, 220, 110, 23);

        Kepercayaan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ada", "Tidak" }));
        Kepercayaan.setName("Kepercayaan"); // NOI18N
        Kepercayaan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KepercayaanKeyPressed(evt);
            }
        });
        FormInput.add(Kepercayaan);
        Kepercayaan.setBounds(500, 220, 80, 23);

        jLabel100.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel100.setText("DATA PASIEN");
        jLabel100.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel100.setName("jLabel100"); // NOI18N
        FormInput.add(jLabel100);
        jLabel100.setBounds(20, 140, 80, 23);

        jLabel68.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel68.setText("Cara Edukasi:");
        jLabel68.setName("jLabel68"); // NOI18N
        FormInput.add(jLabel68);
        jLabel68.setBounds(320, 250, 80, 23);

        Edukasi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Lisan", "Tulisan" }));
        Edukasi.setName("Edukasi"); // NOI18N
        Edukasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EdukasiKeyPressed(evt);
            }
        });
        FormInput.add(Edukasi);
        Edukasi.setBounds(400, 250, 80, 23);

        jLabel10.setText("Ruangan:");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(20, 40, 60, 23);

        Ruangan.setHighlighter(null);
        Ruangan.setName("Ruangan"); // NOI18N
        FormInput.add(Ruangan);
        Ruangan.setBounds(90, 40, 320, 23);

        Scroll6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 253)));
        Scroll6.setName("Scroll6"); // NOI18N
        Scroll6.setOpaque(true);

        tbMasalahKeperawatan.setName("tbMasalahKeperawatan"); // NOI18N
        tbMasalahKeperawatan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbMasalahKeperawatanMouseClicked(evt);
            }
        });
        tbMasalahKeperawatan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbMasalahKeperawatanKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbMasalahKeperawatanKeyReleased(evt);
            }
        });
        Scroll6.setViewportView(tbMasalahKeperawatan);

        FormInput.add(Scroll6);
        Scroll6.setBounds(20, 300, 400, 143);

        label12.setText("Key Word :");
        label12.setName("label12"); // NOI18N
        label12.setPreferredSize(new java.awt.Dimension(60, 23));
        FormInput.add(label12);
        label12.setBounds(30, 450, 60, 23);

        TCariMasalah.setToolTipText("Alt+C");
        TCariMasalah.setName("TCariMasalah"); // NOI18N
        TCariMasalah.setPreferredSize(new java.awt.Dimension(140, 23));
        TCariMasalah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariMasalahKeyPressed(evt);
            }
        });
        FormInput.add(TCariMasalah);
        TCariMasalah.setBounds(90, 450, 215, 23);

        BtnCariMasalah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCariMasalah.setMnemonic('1');
        BtnCariMasalah.setToolTipText("Alt+1");
        BtnCariMasalah.setName("BtnCariMasalah"); // NOI18N
        BtnCariMasalah.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCariMasalah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariMasalahActionPerformed(evt);
            }
        });
        BtnCariMasalah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnCariMasalahKeyPressed(evt);
            }
        });
        FormInput.add(BtnCariMasalah);
        BtnCariMasalah.setBounds(310, 450, 28, 23);

        BtnAllMasalah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAllMasalah.setMnemonic('2');
        BtnAllMasalah.setToolTipText("2Alt+2");
        BtnAllMasalah.setName("BtnAllMasalah"); // NOI18N
        BtnAllMasalah.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAllMasalah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAllMasalahActionPerformed(evt);
            }
        });
        BtnAllMasalah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnAllMasalahKeyPressed(evt);
            }
        });
        FormInput.add(BtnAllMasalah);
        BtnAllMasalah.setBounds(340, 450, 28, 23);

        BtnTambahMasalah.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        BtnTambahMasalah.setMnemonic('3');
        BtnTambahMasalah.setToolTipText("Alt+3");
        BtnTambahMasalah.setName("BtnTambahMasalah"); // NOI18N
        BtnTambahMasalah.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnTambahMasalah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTambahMasalahActionPerformed(evt);
            }
        });
        FormInput.add(BtnTambahMasalah);
        BtnTambahMasalah.setBounds(380, 450, 28, 23);

        scrollInput.setViewportView(FormInput);

        PanelInput.add(scrollInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            isRawat();
        } else {
            Valid.pindah(evt, TCari, Tanggal);
        }
}//GEN-LAST:event_TNoRwKeyPressed

    private void TPasienKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TPasienKeyPressed
        Valid.pindah(evt, TCari, BtnSimpan);
}//GEN-LAST:event_TPasienKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "pasien");
        } else if (NIP.getText().trim().equals("") || NamaPetugas.getText().trim().equals("")) {
            Valid.textKosong(NIP, "Petugas");
        } else {
            if (akses.getkode().equals("Admin Utama")) {
                simpan();
            } else {
                if (TanggalRegistrasi.getText().equals("")) {
                    TanggalRegistrasi.setText(Sequel.cariIsi("select concat(reg_periksa.tgl_registrasi,' ',reg_periksa.jam_reg) from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText()));
                }
                if (Sequel.cekTanggalRegistrasi(TanggalRegistrasi.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + "") + " " + Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem()) == true) {
                    simpan();
                }
            }
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            //  Valid.pindah(evt,RencanaTindakan,BtnBatal);
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
            if (akses.bolehLewatBatasWaktu()) {
                hapus();
            } else {
                if (NIP.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 32).toString())) {
                    if (Sequel.cekTanggal48jam(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString(), Sequel.ambiltanggalsekarang()) == true) {
                        hapus();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Hanya bisa dihapus oleh pegawai yang bersangkutan..!!");
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
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "pasien");
        } else if (NIP.getText().trim().equals("") || NamaPetugas.getText().trim().equals("")) {
            Valid.textKosong(NIP, "Petugas");
        } else {
            if (tbObat.getSelectedRow() > -1) {
                if (akses.bolehLewatBatasWaktu()) {
                    ganti();
                } else {
                    if (NIP.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 32).toString())) {
                        if (Sequel.cekTanggal48jam(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString(), Sequel.ambiltanggalsekarang()) == true) {
                            if (TanggalRegistrasi.getText().equals("")) {
                                TanggalRegistrasi.setText(Sequel.cariIsi("select concat(reg_periksa.tgl_registrasi,' ',reg_periksa.jam_reg) from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText()));
                            }
                            if (Sequel.cekTanggalRegistrasi(TanggalRegistrasi.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + "") + " " + Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem()) == true) {
                                ganti();
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Hanya bisa diganti oleh pegawai yang bersangkutan..!!");
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
        pegawai.dispose();
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
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        } else if (tabMode.getRowCount() != 0) {
            try {
                htmlContent = new StringBuilder();
                htmlContent.append(
                        "<tr class='isi'>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.Rawat</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.R.M.</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Nama Pasien</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Tgl.Lahir</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>JK</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Tanggal</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Bicara</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Keterangan Bicara</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Bahasa Sehari-hari</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Keterangan Bahasa Sehari-hari</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Perlu Penerjemah</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Keterangan Penerjemah</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Bahasa Isyarat</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Cara Belajar</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Hambatan Belajar</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Keterangan Hambatan Belajar</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Kemampuan Belajar</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Keterangan Kemampuan Belajar</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Pendidikan Pasien</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Penyakitnya Merupakan</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Keterangan Penyakitnya Merupakan</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Keputusan Memilih Layanan</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Keterangan Keputusan Memilih Layanan</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Keyakinan Terhadap Terapi</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Keterangan Keyakinan Terhadap Terapi</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Aspek Keyakinan Dipertimbangkan</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Keterangan Aspek Keyakinan Dipertimbangkan</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Kesediaan Menerima Informasi</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Topik Edukasi Penyakit Diderita</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Topik Edukasi Rencana Tindakan/Terapi</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Topik Edukasi Pengobatan/Prosedur Diperlukan</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Topik Edukasi Hasil Pelayanan</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>NIP</b></td>"
                        + "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Petugas</b></td>"
                        + "</tr>"
                );
                for (i = 0; i < tabMode.getRowCount(); i++) {
                    htmlContent.append(
                            "<tr class='isi'>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 0).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 1).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 2).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 3).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 4).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 5).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 6).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 7).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 8).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 9).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 10).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 11).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 12).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 13).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 14).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 15).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 16).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 17).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 18).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 19).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 20).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 21).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 22).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 23).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 24).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 25).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 26).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 27).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 28).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 29).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 30).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 31).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 32).toString() + "</td>"
                            + "<td valign='top'>" + tbObat.getValueAt(i, 33).toString() + "</td>"
                            + "</tr>");
                }
                LoadHTML.setText(
                        "<html>"
                        + "<table width='4200px' border='0' align='center' cellpadding='1px' cellspacing='0' class='tbl_form'>"
                        + htmlContent.toString()
                        + "</table>"
                        + "</html>"
                );

                File g = new File("file2.css");
                BufferedWriter bg = new BufferedWriter(new FileWriter(g));
                bg.write(
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
                bg.close();

                File f = new File("DataEdukasiPasienKeluargaRawatJalan.html");
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                bw.write(LoadHTML.getText().replaceAll("<head>", "<head>"
                        + "<link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" />"
                        + "<table width='4200px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"
                        + "<tr class='isi2'>"
                        + "<td valign='top' align='center'>"
                        + "<font size='4' face='Tahoma'>" + akses.getnamars() + "</font><br>"
                        + akses.getalamatrs() + ", " + akses.getkabupatenrs() + ", " + akses.getpropinsirs() + "<br>"
                        + akses.getkontakrs() + ", E-mail : " + akses.getemailrs() + "<br><br>"
                        + "<font size='2' face='Tahoma'>DATA EDUKASI PASIEN DAN KELUARGA TERINTEGRASI RAWAT JALAN<br><br></font>"
                        + "</td>"
                        + "</tr>"
                        + "</table>")
                );
                bw.close();
                Desktop.getDesktop().browse(f.toURI());

            } catch (Exception e) {
                System.out.println("Notifikasi : " + e);
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

    private void TanggalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalKeyPressed
        Valid.pindah(evt, TCari, Jam);
}//GEN-LAST:event_TanggalKeyPressed

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
            }
        }
}//GEN-LAST:event_tbObatKeyPressed

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void JamKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JamKeyPressed
        Valid.pindah(evt, Tanggal, Menit);
    }//GEN-LAST:event_JamKeyPressed

    private void MenitKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MenitKeyPressed
        Valid.pindah(evt, Jam, Detik);
    }//GEN-LAST:event_MenitKeyPressed

    private void DetikKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DetikKeyPressed
        Valid.pindah(evt, Menit, btnPetugas);
    }//GEN-LAST:event_DetikKeyPressed

    private void NIPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NIPKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            NamaPetugas.setText(pegawai.tampil3(NIP.getText()));
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            Detik.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            //GCS.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            btnPetugasActionPerformed(null);
        }
    }//GEN-LAST:event_NIPKeyPressed

    private void btnPetugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPetugasActionPerformed
        pegawai.emptTeks();
        //    pegawai.isCek();
        pegawai.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        pegawai.setLocationRelativeTo(internalFrame1);
        pegawai.setVisible(true);
    }//GEN-LAST:event_btnPetugasActionPerformed

    private void btnPetugasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnPetugasKeyPressed
        //Valid.pindah(evt,Detik,RPS);
    }//GEN-LAST:event_btnPetugasKeyPressed

    private void MnEdukasiPasienKeluargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnEdukasiPasienKeluargaActionPerformed
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
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", tbObat.getValueAt(tbObat.getSelectedRow(), 32).toString());
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbObat.getValueAt(tbObat.getSelectedRow(), 33).toString() + "\nID " + (finger.equals("") ? tbObat.getValueAt(tbObat.getSelectedRow(), 32).toString() : finger) + "\n" + Tanggal.getSelectedItem());
            Valid.MyReportqry("rptFormulirEdukasiPasienRJ.jasper", "report", "::[ Formulir Edukasi Pasien & Keluarga Terintegrasi Rawat Jalan ]::",
                    "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir,edukasi_pasien_keluarga.tanggal,"
                    + "edukasi_pasien_keluarga.bicara,edukasi_pasien_keluarga.keterangan_bicara,bahasa_pasien.nama_bahasa,edukasi_pasien_keluarga.bahasa_sehari,"
                    + "edukasi_pasien_keluarga.perlu_penerjemah,edukasi_pasien_keluarga.keterangan_penerjemah,edukasi_pasien_keluarga.bahasa_isyarat,"
                    + "edukasi_pasien_keluarga.cara_belajar,edukasi_pasien_keluarga.hambatan_belajar,edukasi_pasien_keluarga.keterangan_hambatan_belajar,"
                    + "edukasi_pasien_keluarga.kemampuan_belajar,edukasi_pasien_keluarga.keterangan_kemampuan_belajar,pasien.pnd,"
                    + "edukasi_pasien_keluarga.penyakitnya_merupakan,edukasi_pasien_keluarga.keterangan_penyakitnya_merupakan,edukasi_pasien_keluarga.keputusan_memilih_layanan,"
                    + "edukasi_pasien_keluarga.keterangan_keputusan_memilih_layanan,edukasi_pasien_keluarga.keyakinan_terhadap_terapi,"
                    + "edukasi_pasien_keluarga.keterangan_keyakinan_terhadap_terapi,edukasi_pasien_keluarga.aspek_keyakinan_dipertimbangkan,"
                    + "edukasi_pasien_keluarga.keterangan_aspek_keyakinan_dipertimbangkan,edukasi_pasien_keluarga.kesediaan_menerima_informasi,"
                    + "edukasi_pasien_keluarga.topik_edukasi_penyakit,edukasi_pasien_keluarga.topik_edukasi_rencana_tindakan,edukasi_pasien_keluarga.topik_edukasi_pengobatan,"
                    + "edukasi_pasien_keluarga.topik_edukasi_hasil_layanan,edukasi_pasien_keluarga.nip,pegawai.nama "
                    + "from edukasi_pasien_keluarga inner join reg_periksa on edukasi_pasien_keluarga.no_rawat=reg_periksa.no_rawat "
                    + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                    + "inner join bahasa_pasien on bahasa_pasien.id=pasien.bahasa_pasien "
                    + "inner join pegawai on edukasi_pasien_keluarga.nip=pegawai.nik where reg_periksa.no_rawat='" + tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "'", param);
        }
    }//GEN-LAST:event_MnEdukasiPasienKeluargaActionPerformed

    private void TinggalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TinggalKeyPressed
        Valid.pindah(evt, btnPetugas, HambatanKet);
    }//GEN-LAST:event_TinggalKeyPressed

    private void HambatanKetKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_HambatanKetKeyPressed
        Valid.pindah(evt, Tinggal, Penerima);
    }//GEN-LAST:event_HambatanKetKeyPressed

    private void BahasaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BahasaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BahasaKeyPressed

    private void PenerimaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PenerimaKeyPressed
        Valid.pindah(evt, HambatanKet, KepercayaanKet);
    }//GEN-LAST:event_PenerimaKeyPressed

    private void KepercayaanKetKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KepercayaanKetKeyPressed
        Valid.pindah(evt, Penerima, Penerjemah);
    }//GEN-LAST:event_KepercayaanKetKeyPressed

    private void PenerjemahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PenerjemahKeyPressed
        // Valid.pindah(evt,KeteranganPenerjemah,KeteranganBahasa);
    }//GEN-LAST:event_PenerjemahKeyPressed

    private void BacaTulisKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BacaTulisKeyPressed
        //   Valid.pindah(evt,KeteranganBahasa,HambatanBelajar);
    }//GEN-LAST:event_BacaTulisKeyPressed

    private void HambatanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_HambatanKeyPressed
        //   Valid.pindah(evt,CaraBelajar,KeteranganHambatanBelajar);
    }//GEN-LAST:event_HambatanKeyPressed

    private void KesediaanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KesediaanKeyPressed
        //    Valid.pindah(evt,KeteranganHambatanBelajar,KeteranganKemampuanBelajar);
    }//GEN-LAST:event_KesediaanKeyPressed

    private void KepercayaanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KepercayaanKeyPressed
        //    Valid.pindah(evt,KeteranganAspekKeyakinan,PenyakitYangDiderita);
    }//GEN-LAST:event_KepercayaanKeyPressed

    private void EdukasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EdukasiKeyPressed
        //    Valid.pindah(evt,KesediaanInformasi,PengobatanProsedur);
    }//GEN-LAST:event_EdukasiKeyPressed

    private void tbMasalahKeperawatanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbMasalahKeperawatanMouseClicked
        /*    if(tabModeMasalah.getRowCount()!=0){
            try {
                tampilRencana2();
            } catch (java.lang.NullPointerException e) {
            }
        } */
    }//GEN-LAST:event_tbMasalahKeperawatanMouseClicked

    private void tbMasalahKeperawatanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbMasalahKeperawatanKeyPressed
        /*     if(tabModeMasalah.getRowCount()!=0){
            if(evt.getKeyCode()==KeyEvent.VK_SHIFT){
                TCariMasalah.setText("");
                TCariMasalah.requestFocus();
            }
        } */
    }//GEN-LAST:event_tbMasalahKeperawatanKeyPressed

    private void tbMasalahKeperawatanKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbMasalahKeperawatanKeyReleased
        /*    if(tabModeMasalah.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    tampilRencana2();
                } catch (java.lang.NullPointerException e) {
                }
            }
        } */
    }//GEN-LAST:event_tbMasalahKeperawatanKeyReleased

    private void TCariMasalahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariMasalahKeyPressed
        /*    if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            tampilMasalah2();
        }else if((evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN)||(evt.getKeyCode()==KeyEvent.VK_TAB)){
            Rencana.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            KetDokter.requestFocus();
        } */
    }//GEN-LAST:event_TCariMasalahKeyPressed

    private void BtnCariMasalahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariMasalahActionPerformed
        tampilEdukasi2();
    }//GEN-LAST:event_BtnCariMasalahActionPerformed

    private void BtnCariMasalahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariMasalahKeyPressed
        /*      if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            tampilMasalah2();
        }else if((evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN)||(evt.getKeyCode()==KeyEvent.VK_TAB)){
            Rencana.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            KetDokter.requestFocus();
        } */
    }//GEN-LAST:event_BtnCariMasalahKeyPressed

    private void BtnAllMasalahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllMasalahActionPerformed
        TCari.setText("");
        tampilEdukasi();
    }//GEN-LAST:event_BtnAllMasalahActionPerformed

    private void BtnAllMasalahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllMasalahKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnAllMasalahActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnCariMasalah, TCariMasalah);
        }
    }//GEN-LAST:event_BtnAllMasalahKeyPressed

    private void BtnTambahMasalahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTambahMasalahActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        MasterKebutuhanEdukasi form = new MasterKebutuhanEdukasi(null, false);
        form.isCek();
        form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        form.setLocationRelativeTo(internalFrame1);
        form.setVisible(true);
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnTambahMasalahActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMEdukasiPasienKeluargaRawatInap dialog = new RMEdukasiPasienKeluargaRawatInap(new javax.swing.JFrame(), true);
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
    private widget.ComboBox BacaTulis;
    private widget.TextBox Bahasa;
    private widget.Button BtnAll;
    private widget.Button BtnAllMasalah;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnCariMasalah;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.Button BtnTambahMasalah;
    private widget.CekBox ChkInput;
    private widget.CekBox ChkKejadian;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.ComboBox Detik;
    private widget.ComboBox Edukasi;
    private widget.PanelBiasa FormInput;
    private widget.ComboBox Hambatan;
    private widget.TextBox HambatanKet;
    private widget.TextBox JK;
    private widget.ComboBox Jam;
    private widget.ComboBox Kepercayaan;
    private widget.TextBox KepercayaanKet;
    private widget.ComboBox Kesediaan;
    private widget.Label LCount;
    private widget.editorpane LoadHTML;
    private widget.ComboBox Menit;
    private javax.swing.JMenuItem MnEdukasiPasienKeluarga;
    private widget.TextBox NIP;
    private widget.TextBox NamaPetugas;
    private javax.swing.JPanel PanelInput;
    private widget.ComboBox Penerima;
    private widget.ComboBox Penerjemah;
    private widget.TextBox Ruangan;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll6;
    private widget.TextBox TCari;
    private widget.TextBox TCariMasalah;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Tanggal Tanggal;
    private widget.TextBox TanggalRegistrasi;
    private widget.TextBox TglLahir;
    private widget.ComboBox Tinggal;
    private widget.Button btnPetugas;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel100;
    private widget.Label jLabel16;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel4;
    private widget.Label jLabel46;
    private widget.Label jLabel47;
    private widget.Label jLabel50;
    private widget.Label jLabel52;
    private widget.Label jLabel53;
    private widget.Label jLabel54;
    private widget.Label jLabel55;
    private widget.Label jLabel56;
    private widget.Label jLabel6;
    private widget.Label jLabel65;
    private widget.Label jLabel68;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel99;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JSeparator jSeparator2;
    private widget.Label label12;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.ScrollPane scrollInput;
    private widget.Table tbMasalahKeperawatan;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            if (TCari.getText().toString().trim().equals("")) {
                ps = koneksi.prepareStatement(
                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir,edukasi_pasien_keluarga.tanggal,"
                        + "edukasi_pasien_keluarga.bicara,edukasi_pasien_keluarga.keterangan_bicara,bahasa_pasien.nama_bahasa,edukasi_pasien_keluarga.bahasa_sehari,"
                        + "edukasi_pasien_keluarga.perlu_penerjemah,edukasi_pasien_keluarga.keterangan_penerjemah,edukasi_pasien_keluarga.bahasa_isyarat,"
                        + "edukasi_pasien_keluarga.cara_belajar,edukasi_pasien_keluarga.hambatan_belajar,edukasi_pasien_keluarga.keterangan_hambatan_belajar,"
                        + "edukasi_pasien_keluarga.kemampuan_belajar,edukasi_pasien_keluarga.keterangan_kemampuan_belajar,pasien.pnd,"
                        + "edukasi_pasien_keluarga.penyakitnya_merupakan,edukasi_pasien_keluarga.keterangan_penyakitnya_merupakan,edukasi_pasien_keluarga.keputusan_memilih_layanan,"
                        + "edukasi_pasien_keluarga.keterangan_keputusan_memilih_layanan,edukasi_pasien_keluarga.keyakinan_terhadap_terapi,"
                        + "edukasi_pasien_keluarga.keterangan_keyakinan_terhadap_terapi,edukasi_pasien_keluarga.aspek_keyakinan_dipertimbangkan,"
                        + "edukasi_pasien_keluarga.keterangan_aspek_keyakinan_dipertimbangkan,edukasi_pasien_keluarga.kesediaan_menerima_informasi,"
                        + "edukasi_pasien_keluarga.topik_edukasi_penyakit,edukasi_pasien_keluarga.topik_edukasi_rencana_tindakan,edukasi_pasien_keluarga.topik_edukasi_pengobatan,"
                        + "edukasi_pasien_keluarga.topik_edukasi_hasil_layanan,edukasi_pasien_keluarga.nip,pegawai.nama "
                        + "from edukasi_pasien_keluarga inner join reg_periksa on edukasi_pasien_keluarga.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "inner join bahasa_pasien on bahasa_pasien.id=pasien.bahasa_pasien "
                        + "inner join pegawai on edukasi_pasien_keluarga.nip=pegawai.nik where "
                        + "edukasi_pasien_keluarga.tanggal between ? and ? order by edukasi_pasien_keluarga.tanggal");
            } else {
                ps = koneksi.prepareStatement(
                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir,edukasi_pasien_keluarga.tanggal,"
                        + "edukasi_pasien_keluarga.bicara,edukasi_pasien_keluarga.keterangan_bicara,bahasa_pasien.nama_bahasa,edukasi_pasien_keluarga.bahasa_sehari,"
                        + "edukasi_pasien_keluarga.perlu_penerjemah,edukasi_pasien_keluarga.keterangan_penerjemah,edukasi_pasien_keluarga.bahasa_isyarat,"
                        + "edukasi_pasien_keluarga.cara_belajar,edukasi_pasien_keluarga.hambatan_belajar,edukasi_pasien_keluarga.keterangan_hambatan_belajar,"
                        + "edukasi_pasien_keluarga.kemampuan_belajar,edukasi_pasien_keluarga.keterangan_kemampuan_belajar,pasien.pnd,"
                        + "edukasi_pasien_keluarga.penyakitnya_merupakan,edukasi_pasien_keluarga.keterangan_penyakitnya_merupakan,edukasi_pasien_keluarga.keputusan_memilih_layanan,"
                        + "edukasi_pasien_keluarga.keterangan_keputusan_memilih_layanan,edukasi_pasien_keluarga.keyakinan_terhadap_terapi,"
                        + "edukasi_pasien_keluarga.keterangan_keyakinan_terhadap_terapi,edukasi_pasien_keluarga.aspek_keyakinan_dipertimbangkan,"
                        + "edukasi_pasien_keluarga.keterangan_aspek_keyakinan_dipertimbangkan,edukasi_pasien_keluarga.kesediaan_menerima_informasi,"
                        + "edukasi_pasien_keluarga.topik_edukasi_penyakit,edukasi_pasien_keluarga.topik_edukasi_rencana_tindakan,edukasi_pasien_keluarga.topik_edukasi_pengobatan,"
                        + "edukasi_pasien_keluarga.topik_edukasi_hasil_layanan,edukasi_pasien_keluarga.nip,pegawai.nama "
                        + "from edukasi_pasien_keluarga inner join reg_periksa on edukasi_pasien_keluarga.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "inner join bahasa_pasien on bahasa_pasien.id=pasien.bahasa_pasien "
                        + "inner join pegawai on edukasi_pasien_keluarga.nip=pegawai.nik where "
                        + "edukasi_pasien_keluarga.tanggal between ? and ? and (reg_periksa.no_rawat like ? or pasien.no_rkm_medis like ? or pasien.nm_pasien like ? or edukasi_pasien_keluarga.nip like ? or pegawai.nama like ?) "
                        + "order by edukasi_pasien_keluarga.tanggal ");
            }

            try {
                if (TCari.getText().toString().trim().equals("")) {
                    ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                    ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                } else {
                    ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + "") + " 00:00:00");
                    ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + "") + " 23:59:59");
                    ps.setString(3, "%" + TCari.getText() + "%");
                    ps.setString(4, "%" + TCari.getText() + "%");
                    ps.setString(5, "%" + TCari.getText() + "%");
                    ps.setString(6, "%" + TCari.getText() + "%");
                    ps.setString(7, "%" + TCari.getText() + "%");
                }

                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"), rs.getString("tgl_lahir"), rs.getString("jk"), rs.getString("tanggal"),
                        rs.getString("bicara"), rs.getString("keterangan_bicara"), rs.getString("nama_bahasa"), rs.getString("bahasa_sehari"), rs.getString("perlu_penerjemah"),
                        rs.getString("keterangan_penerjemah"), rs.getString("bahasa_isyarat"), rs.getString("cara_belajar"), rs.getString("hambatan_belajar"),
                        rs.getString("keterangan_hambatan_belajar"), rs.getString("kemampuan_belajar"), rs.getString("keterangan_kemampuan_belajar"), rs.getString("pnd"),
                        rs.getString("penyakitnya_merupakan"), rs.getString("keterangan_penyakitnya_merupakan"), rs.getString("keputusan_memilih_layanan"),
                        rs.getString("keterangan_keputusan_memilih_layanan"), rs.getString("keyakinan_terhadap_terapi"), rs.getString("keterangan_keyakinan_terhadap_terapi"),
                        rs.getString("aspek_keyakinan_dipertimbangkan"), rs.getString("keterangan_aspek_keyakinan_dipertimbangkan"), rs.getString("kesediaan_menerima_informasi"),
                        rs.getString("topik_edukasi_penyakit"), rs.getString("topik_edukasi_rencana_tindakan"), rs.getString("topik_edukasi_pengobatan"),
                        rs.getString("topik_edukasi_hasil_layanan"), rs.getString("nip"), rs.getString("nama")
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
        Tanggal.setDate(new Date());
        Tinggal.setSelectedIndex(0);
        HambatanKet.setText("");
        Penerima.setSelectedIndex(0);
        KepercayaanKet.setText("");
        Penerjemah.setSelectedIndex(0);
        BacaTulis.setSelectedItem(0);
        Hambatan.setSelectedIndex(0);
        Kesediaan.setSelectedIndex(0);
        Kepercayaan.setSelectedIndex(0);
        Edukasi.setSelectedIndex(0);
        Tinggal.requestFocus();
    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            TglLahir.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
            JK.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
            Tinggal.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
            HambatanKet.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString());
            Bahasa.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString());
            Penerima.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString());
            KepercayaanKet.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString());
            Penerjemah.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString());
            BacaTulis.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 13).toString());
            Hambatan.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 14).toString());

            Kesediaan.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 16).toString());

            Kepercayaan.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 27).toString());
            Edukasi.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 28).toString());

            NIP.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 32).toString());
            NamaPetugas.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 33).toString());
            Jam.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString().substring(11, 13));
            Menit.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString().substring(14, 16));
            Detik.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString().substring(17, 19));
            Valid.SetTgl(Tanggal, tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
        }
    }

    private void isRawat() {
        try {
            ps = koneksi.prepareStatement(
                    "select reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir,"
                    + "bahasa_pasien.nama_bahasa,pasien.pnd,reg_periksa.tgl_registrasi,reg_periksa.jam_reg "
                    + "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                    + "inner join bahasa_pasien on bahasa_pasien.id=pasien.bahasa_pasien "
                    + "where reg_periksa.no_rawat=?");
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    TPasien.setText(rs.getString("nm_pasien"));
                    DTPCari1.setDate(rs.getDate("tgl_registrasi"));
                    TglLahir.setText(rs.getString("tgl_lahir"));

                    JK.setText(rs.getString("jk"));
                    Bahasa.setText(rs.getString("nama_bahasa"));
                    TanggalRegistrasi.setText(rs.getString("tgl_registrasi") + " " + rs.getString("jam_reg"));
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

    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        DTPCari2.setDate(tgl2);
        isRawat();
        ChkInput.setSelected(true);
        isForm();
    }

    private void isForm() {
        if (ChkInput.isSelected() == true) {
            if (internalFrame1.getHeight() > 678) {
                ChkInput.setVisible(false);
                PanelInput.setPreferredSize(new Dimension(WIDTH, 506));
                FormInput.setVisible(true);
                ChkInput.setVisible(true);
            } else {
                ChkInput.setVisible(false);
                PanelInput.setPreferredSize(new Dimension(WIDTH, internalFrame1.getHeight() - 172));
                FormInput.setVisible(true);
                ChkInput.setVisible(true);
            }
        } else if (ChkInput.isSelected() == false) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 20));
            FormInput.setVisible(false);
            ChkInput.setVisible(true);
        }
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.getedukasi_pasien_keluarga_rj());
        BtnHapus.setEnabled(akses.getedukasi_pasien_keluarga_rj());
        BtnEdit.setEnabled(akses.getedukasi_pasien_keluarga_rj());
        BtnPrint.setEnabled(akses.getedukasi_pasien_keluarga_rj());
        if (akses.getjml2() >= 1) {
            NIP.setEditable(false);
            btnPetugas.setEnabled(false);
            NIP.setText(akses.getkode());
            NamaPetugas.setText(pegawai.tampil3(NIP.getText()));
//            if(NamaPetugas.getText().equals("")){
//                NIP.setText("");
//                JOptionPane.showMessageDialog(null,"User login bukan pegawai...!!");
//            }
        }

        if (TANGGALMUNDUR.equals("no")) {
            if (!akses.getkode().equals("Admin Utama")) {
                Tanggal.setEditable(false);
                Tanggal.setEnabled(false);
                ChkKejadian.setEnabled(false);
                Jam.setEnabled(false);
                Menit.setEnabled(false);
                Detik.setEnabled(false);
            }
        }
    }

    private void jam() {
        ActionListener taskPerformer = new ActionListener() {
            private int nilai_jam;
            private int nilai_menit;
            private int nilai_detik;

            public void actionPerformed(ActionEvent e) {
                String nol_jam = "";
                String nol_menit = "";
                String nol_detik = "";

                Date now = Calendar.getInstance().getTime();

                // Mengambil nilaj JAM, MENIT, dan DETIK Sekarang
                if (ChkKejadian.isSelected() == true) {
                    nilai_jam = now.getHours();
                    nilai_menit = now.getMinutes();
                    nilai_detik = now.getSeconds();
                } else if (ChkKejadian.isSelected() == false) {
                    nilai_jam = Jam.getSelectedIndex();
                    nilai_menit = Menit.getSelectedIndex();
                    nilai_detik = Detik.getSelectedIndex();
                }

                // Jika nilai JAM lebih kecil dari 10 (hanya 1 digit)
                if (nilai_jam <= 9) {
                    // Tambahkan "0" didepannya
                    nol_jam = "0";
                }
                // Jika nilai MENIT lebih kecil dari 10 (hanya 1 digit)
                if (nilai_menit <= 9) {
                    // Tambahkan "0" didepannya
                    nol_menit = "0";
                }
                // Jika nilai DETIK lebih kecil dari 10 (hanya 1 digit)
                if (nilai_detik <= 9) {
                    // Tambahkan "0" didepannya
                    nol_detik = "0";
                }
                // Membuat String JAM, MENIT, DETIK
                String jam = nol_jam + Integer.toString(nilai_jam);
                String menit = nol_menit + Integer.toString(nilai_menit);
                String detik = nol_detik + Integer.toString(nilai_detik);
                // Menampilkan pada Layar
                //tampil_jam.setText("  " + jam + " : " + menit + " : " + detik + "  ");
                Jam.setSelectedItem(jam);
                Menit.setSelectedItem(menit);
                Detik.setSelectedItem(detik);
            }
        };
        // Timer
        new Timer(1240, taskPerformer).start();
    }

    private void ganti() {
        /*    if(Sequel.mengedittf("edukasi_pasien_keluarga","no_rawat=?","no_rawat=?,tanggal=?,nip=?,bicara=?,keterangan_bicara=?,bahasa_sehari=?,perlu_penerjemah=?,"+
            "keterangan_penerjemah=?,bahasa_isyarat=?,cara_belajar=?,hambatan_belajar=?,keterangan_hambatan_belajar=?,kemampuan_belajar=?,keterangan_kemampuan_belajar=?,"+
            "penyakitnya_merupakan=?,keterangan_penyakitnya_merupakan=?,keputusan_memilih_layanan=?,keterangan_keputusan_memilih_layanan=?,keyakinan_terhadap_terapi=?,"+
            "keterangan_keyakinan_terhadap_terapi=?,aspek_keyakinan_dipertimbangkan=?,keterangan_aspek_keyakinan_dipertimbangkan=?,kesediaan_menerima_informasi=?,"+
            "topik_edukasi_penyakit=?,topik_edukasi_rencana_tindakan=?,topik_edukasi_pengobatan=?,topik_edukasi_hasil_layanan=?",28,new String[]{
            TNoRw.getText(),Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem(),
            NIP.getText(),Bicara.getSelectedItem().toString(),KeteranganBicara.getText(),KeteranganBahasa.getText(),Penerjemah.getSelectedItem().toString(),
            KeteranganPenerjemah.getText(),BahasaIsyarat.getSelectedItem().toString(),CaraBelajar.getSelectedItem().toString(),HambatanBelajar.getSelectedItem().toString(),
            KeteranganHambatanBelajar.getText(),KemampuanBelajar.getSelectedItem().toString(),KeteranganKemampuanBelajar.getText(),
            PenyakitnyaMerupakan.getSelectedItem().toString(),KeteranganPenyakitnyaMerupakan.getText(),KeputusanMemilihLayanan.getSelectedItem().toString(),
            KeteranganKeputusanMemilihLayanan.getText(),KeyakinanTerhadapHasil.getSelectedItem().toString(),KeteranganKeyakinanTerhadapHasil.getText(),
            AspekKeyakinan.getSelectedItem().toString(),KeteranganAspekKeyakinan.getText(),KesediaanInformasi.getSelectedItem().toString(),
            PenyakitYangDiderita.getSelectedItem().toString(),RencanaTindakan.getSelectedItem().toString(),PengobatanProsedur.getSelectedItem().toString(),
            HasilLayanan.getSelectedItem().toString(),tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()
        })==true){
            tbObat.setValueAt(TNoRw.getText(),tbObat.getSelectedRow(),0);
            tbObat.setValueAt(TNoRM.getText(),tbObat.getSelectedRow(),1);
            tbObat.setValueAt(TPasien.getText(),tbObat.getSelectedRow(),2);
            tbObat.setValueAt(TglLahir.getText(),tbObat.getSelectedRow(),3);
            tbObat.setValueAt(JK.getText().substring(0,1),tbObat.getSelectedRow(),4);
            tbObat.setValueAt(Valid.SetTgl(Tanggal.getSelectedItem()+"")+" "+Jam.getSelectedItem()+":"+Menit.getSelectedItem()+":"+Detik.getSelectedItem(),tbObat.getSelectedRow(),5);
            tbObat.setValueAt(Bicara.getSelectedItem().toString(),tbObat.getSelectedRow(),6);
            tbObat.setValueAt(KeteranganBicara.getText(),tbObat.getSelectedRow(),7);
            tbObat.setValueAt(Bahasa.getText(),tbObat.getSelectedRow(),8);
            tbObat.setValueAt(KeteranganBahasa.getText(),tbObat.getSelectedRow(),9);
            tbObat.setValueAt(Penerjemah.getSelectedItem().toString(),tbObat.getSelectedRow(),10);
            tbObat.setValueAt(KeteranganPenerjemah.getText(),tbObat.getSelectedRow(),11);
            tbObat.setValueAt(BahasaIsyarat.getSelectedItem().toString(),tbObat.getSelectedRow(),12);
            tbObat.setValueAt(CaraBelajar.getSelectedItem().toString(),tbObat.getSelectedRow(),13);
            tbObat.setValueAt(HambatanBelajar.getSelectedItem().toString(),tbObat.getSelectedRow(),14);
            tbObat.setValueAt(KeteranganHambatanBelajar.getText(),tbObat.getSelectedRow(),15);
            tbObat.setValueAt(KemampuanBelajar.getSelectedItem().toString(),tbObat.getSelectedRow(),16);
            tbObat.setValueAt(KeteranganKemampuanBelajar.getText(),tbObat.getSelectedRow(),17);
            tbObat.setValueAt(Pendidikan.getText(),tbObat.getSelectedRow(),18);
            tbObat.setValueAt(PenyakitnyaMerupakan.getSelectedItem().toString(),tbObat.getSelectedRow(),19);
            tbObat.setValueAt(KeteranganPenyakitnyaMerupakan.getText(),tbObat.getSelectedRow(),20);
            tbObat.setValueAt(KeputusanMemilihLayanan.getSelectedItem().toString(),tbObat.getSelectedRow(),21);
            tbObat.setValueAt(KeteranganKeputusanMemilihLayanan.getText(),tbObat.getSelectedRow(),22);
            tbObat.setValueAt(KeyakinanTerhadapHasil.getSelectedItem().toString(),tbObat.getSelectedRow(),23);
            tbObat.setValueAt(KeteranganKeyakinanTerhadapHasil.getText(),tbObat.getSelectedRow(),24);
            tbObat.setValueAt(AspekKeyakinan.getSelectedItem().toString(),tbObat.getSelectedRow(),25);
            tbObat.setValueAt(KeteranganAspekKeyakinan.getText(),tbObat.getSelectedRow(),26);
            tbObat.setValueAt(KesediaanInformasi.getSelectedItem().toString(),tbObat.getSelectedRow(),27);
            tbObat.setValueAt(PenyakitYangDiderita.getSelectedItem().toString(),tbObat.getSelectedRow(),28);
            tbObat.setValueAt(RencanaTindakan.getSelectedItem().toString(),tbObat.getSelectedRow(),29);
            tbObat.setValueAt(PengobatanProsedur.getSelectedItem().toString(),tbObat.getSelectedRow(),30);
            tbObat.setValueAt(HasilLayanan.getSelectedItem().toString(),tbObat.getSelectedRow(),31);
            tbObat.setValueAt(NIP.getText(),tbObat.getSelectedRow(),32);
            tbObat.setValueAt(NamaPetugas.getText(),tbObat.getSelectedRow(),33);
            emptTeks();
        } */
    }

    private void hapus() {
        if (Sequel.queryu2tf("delete from edukasi_pasien_keluarga where no_rawat=?", 1, new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
        }) == true) {
            tabMode.removeRow(tbObat.getSelectedRow());
            LCount.setText("" + tabMode.getRowCount());
            emptTeks();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
        }
    }

    private void simpan() {
        if (Sequel.menyimpantf("edukasi_pasien_keluarga", "?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 14, new String[]{
            TNoRw.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + "") + " " + Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
            NIP.getText(), Tinggal.getSelectedItem().toString(), Hambatan.getSelectedItem().toString(), HambatanKet.getText(), Penerima.getSelectedItem().toString(),
            Bahasa.getText(), Penerjemah.getSelectedItem().toString(), BacaTulis.getSelectedItem().toString(), Kepercayaan.getSelectedItem().toString(),
            KepercayaanKet.getText(), Kesediaan.getSelectedItem().toString(), Edukasi.getSelectedItem().toString()
        }) == true) {
            for (i = 0; i < tbMasalahKeperawatan.getRowCount(); i++) {
                if (tbMasalahKeperawatan.getValueAt(i, 0).toString().equals("true")) {
                    Sequel.menyimpan2("kebutuhanedukasi", "?,?", 2, new String[]{TNoRw.getText(), tbMasalahKeperawatan.getValueAt(i, 1).toString()});
                }
            }
            tabMode.addRow(new String[]{
                TNoRw.getText(), TNoRM.getText(), TPasien.getText(), TglLahir.getText(), JK.getText().substring(0, 1), Valid.SetTgl(Tanggal.getSelectedItem() + "") + " " + Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
                NIP.getText(), NamaPetugas.getText(), Tinggal.getSelectedItem().toString(), Hambatan.getSelectedItem().toString(), HambatanKet.getText(), Penerima.getSelectedItem().toString(),
                Bahasa.getText(), Penerjemah.getSelectedItem().toString(), BacaTulis.getSelectedItem().toString(), Kepercayaan.getSelectedItem().toString(),
                KepercayaanKet.getText(), Kesediaan.getSelectedItem().toString(), Edukasi.getSelectedItem().toString()
            });
            LCount.setText("" + tabMode.getRowCount());
            emptTeks();
        }
    }

    private void tampilEdukasi() {
        try {
            Valid.tabelKosong(tabModeMasalah);
            file = new File("./cache/kebutuhanedukasi.iyem");
            file.createNewFile();
            fileWriter = new FileWriter(file);
            iyem = "";
            ps = koneksi.prepareStatement("select * from master_kebutuhan_edukasi order by master_kebutuhan_edukasi.kode");
            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabModeMasalah.addRow(new Object[]{false, rs.getString(1), rs.getString(2)});
                    iyem = iyem + "{\"Kode\":\"" + rs.getString(1) + "\",\"KebutuhanEdukasi\":\"" + rs.getString(2) + "\"},";
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
            fileWriter.write("{\"kebutuhanedukasi\":[" + iyem.substring(0, iyem.length() - 1) + "]}");
            fileWriter.flush();
            fileWriter.close();
            iyem = null;
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }

    private void tampilEdukasi2() {
        try {
            jml = 0;
            for (i = 0; i < tbMasalahKeperawatan.getRowCount(); i++) {
                if (tbMasalahKeperawatan.getValueAt(i, 0).toString().equals("true")) {
                    jml++;
                }
            }

            pilih = null;
            pilih = new boolean[jml];
            kode = null;
            kode = new String[jml];
            edukasi = null;
            edukasi = new String[jml];

            index = 0;
            for (i = 0; i < tbMasalahKeperawatan.getRowCount(); i++) {
                if (tbMasalahKeperawatan.getValueAt(i, 0).toString().equals("true")) {
                    pilih[index] = true;
                    kode[index] = tbMasalahKeperawatan.getValueAt(i, 1).toString();
                    edukasi[index] = tbMasalahKeperawatan.getValueAt(i, 2).toString();
                    index++;
                }
            }

            Valid.tabelKosong(tabModeMasalah);

            for (i = 0; i < jml; i++) {
                tabModeMasalah.addRow(new Object[]{
                    pilih[i], kode[i], edukasi[i]
                });
            }

            myObj = new FileReader("./cache/kebutuhanedukasi.iyem");
            root = mapper.readTree(myObj);
            response = root.path("kebutuhanedukasi");
            if (response.isArray()) {
                for (JsonNode list : response) {
                    if (list.path("Kode").asText().toLowerCase().contains(TCariMasalah.getText().toLowerCase()) || list.path("KebutuhanEdukasi").asText().toLowerCase().contains(TCariMasalah.getText().toLowerCase())) {
                        tabModeMasalah.addRow(new Object[]{
                            false, list.path("Kode").asText(), list.path("KebutuhanEdukasi").asText()
                        });
                    }
                }
            }
            myObj.close();
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
    }

}
