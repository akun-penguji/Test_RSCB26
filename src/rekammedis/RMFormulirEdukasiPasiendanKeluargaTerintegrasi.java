/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rekammedis;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariPegawai;
import widget.PanelTTD;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author perpustakaan
 */
public final class RMFormulirEdukasiPasiendanKeluargaTerintegrasi extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private DlgCariPegawai pegawai = new DlgCariPegawai(null, false);
    private String dpjp = "";
    private String TANGGALMUNDUR = "yes";
    private MasterEdukasiPasien template = new MasterEdukasiPasien(null, false);
    private final SimpleDateFormat tanggalNow = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat jamNow = new SimpleDateFormat("HH:mm:ss");

    /**
     * Creates new form DlgRujuk
     *
     * @param parent
     * @param modal
     */
    public RMFormulirEdukasiPasiendanKeluargaTerintegrasi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(628, 674);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.R.M.", "Nama Pasien", "Tgl.Lahir", "Tanggal", "Jam", "Lama Edukasi",
            "Metode", "Media", "Evaluasi", "Penerima", "Nama Penerima", "Isi Informasi", "NIP", "Nama Pegawai", "Profesi/ Jabatan/ Departemen"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        tbObat.setModel(tabMode);

// tbObat.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(), tbObat.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

// Menyesuaikan lebar kolom untuk setiap kolom tabel
        for (int i = 0; i < 16; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            switch (i) {
                case 0:  // "No.Rawat"
                    column.setPreferredWidth(105);
                    break;
                case 1:  // "No.R.M."
                    column.setPreferredWidth(65);
                    break;
                case 2:  // "Nama Pasien"
                    column.setPreferredWidth(160);
                    break;
                case 3:  // "Tgl.Lahir"
                    column.setPreferredWidth(100);
                    break;
                case 4:  // "Tanggal"
                    column.setPreferredWidth(100);
                    break;
                case 5:  // "Jam"
                    column.setPreferredWidth(65);
                    break;
                case 6:  // "Lama Edukasi"
                    column.setPreferredWidth(80);
                    break;
                case 7:  // "Metode"
                    column.setPreferredWidth(60);
                    break;
                case 8:  // "Media"
                    column.setPreferredWidth(250);
                    break;
                case 9: // "Evaluasi"
                    column.setPreferredWidth(90);
                    break;
                case 10: // "Penerima"
                    column.setPreferredWidth(90);
                    break;
                case 11: // "Nama Penerima"
                    column.setPreferredWidth(160);
                    break;
                case 12: // "Isi Informasi"
                    column.setPreferredWidth(250);
                    break;
                case 13: // "NIP"
                    column.setPreferredWidth(90);
                    break;
                case 14: // "Nama Pegawai"
                    column.setPreferredWidth(160);
                    break;
                case 15: // "Profesi/ Jabatan/ Departemen"
                    column.setPreferredWidth(160);
                    break;
            }
        }

        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        NIP.setDocument(new batasInput((byte) 20).getKata(NIP));
        Informasi.setDocument(new batasInput((int) 2000).getKata(Informasi));
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
                    NamaPegawai.setText(pegawai.getTable().getValueAt(pegawai.getTable().getSelectedRow(), 1).toString());
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

        template.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (akses.getform().equals("RMFormulirEdukasiPasiendanKeluargaTerintegrasi")) {
                    if (template.getTable().getSelectedRow() != -1) {
                        Informasi.setText(template.getTable().getValueAt(template.getTable().getSelectedRow(), 1).toString());
                    }
                    Informasi.requestFocus();
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

        ChkInput.setSelected(false);
        isForm();
        jam();

        try {
            TANGGALMUNDUR = koneksiDB.TANGGALMUNDUR();
        } catch (Exception e) {
            TANGGALMUNDUR = "yes";
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

        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnTtdPasien = new javax.swing.JMenuItem();
        JK = new widget.TextBox();
        Umur = new widget.TextBox();
        TanggalRegistrasi = new widget.TextBox();
        WindowTtd = new javax.swing.JDialog();
        internalFrame6 = new widget.InternalFrame();
        panelGlass6 = new widget.panelisi();
        BtnSimpan1 = new widget.Button();
        BtnHapus1 = new widget.Button();
        BtnKeluar1 = new widget.Button();
        panelGlass10 = new freehand.PanelTTD();
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
        NamaPegawai = new widget.TextBox();
        btnPegawai = new widget.Button();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        jLabel12 = new widget.Label();
        scrollPane2 = new widget.ScrollPane();
        Informasi = new widget.TextArea();
        jLabel93 = new widget.Label();
        PenerimaKet = new widget.TextBox();
        jLabel9 = new widget.Label();
        Lama = new widget.TextBox();
        jLabel10 = new widget.Label();
        jLabel11 = new widget.Label();
        Metode = new widget.ComboBox();
        jLabel13 = new widget.Label();
        Media = new widget.ComboBox();
        jLabel14 = new widget.Label();
        Evaluasi = new widget.ComboBox();
        jLabel15 = new widget.Label();
        Penerima = new widget.ComboBox();
        Jabatan1 = new widget.TextBox();
        btnAmbilPhoto1 = new widget.Button();
        ChkInput = new widget.CekBox();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnTtdPasien.setBackground(new java.awt.Color(255, 255, 254));
        MnTtdPasien.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnTtdPasien.setForeground(new java.awt.Color(50, 50, 50));
        MnTtdPasien.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnTtdPasien.setText("Tanda Tangan Pasien");
        MnTtdPasien.setName("MnTtdPasien"); // NOI18N
        MnTtdPasien.setPreferredSize(new java.awt.Dimension(260, 26));
        MnTtdPasien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnTtdPasienActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnTtdPasien);

        JK.setHighlighter(null);
        JK.setName("JK"); // NOI18N

        Umur.setHighlighter(null);
        Umur.setName("Umur"); // NOI18N

        TanggalRegistrasi.setHighlighter(null);
        TanggalRegistrasi.setName("TanggalRegistrasi"); // NOI18N

        WindowTtd.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        WindowTtd.setName("WindowTtd"); // NOI18N
        WindowTtd.setUndecorated(true);
        WindowTtd.setResizable(false);

        internalFrame6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Tanda Tangan Pasien ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame6.setName("internalFrame6"); // NOI18N
        internalFrame6.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass6.setName("panelGlass6"); // NOI18N
        panelGlass6.setPreferredSize(new java.awt.Dimension(55, 55));
        panelGlass6.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan1.setMnemonic('S');
        BtnSimpan1.setText("Simpan");
        BtnSimpan1.setToolTipText("Alt+S");
        BtnSimpan1.setName("BtnSimpan1"); // NOI18N
        BtnSimpan1.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpan1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpan1ActionPerformed(evt);
            }
        });
        BtnSimpan1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSimpan1KeyPressed(evt);
            }
        });
        panelGlass6.add(BtnSimpan1);

        BtnHapus1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus1.setMnemonic('H');
        BtnHapus1.setText("Hapus");
        BtnHapus1.setToolTipText("Alt+H");
        BtnHapus1.setName("BtnHapus1"); // NOI18N
        BtnHapus1.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapus1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapus1ActionPerformed(evt);
            }
        });
        BtnHapus1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnHapus1KeyPressed(evt);
            }
        });
        panelGlass6.add(BtnHapus1);

        BtnKeluar1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/cross.png"))); // NOI18N
        BtnKeluar1.setMnemonic('T');
        BtnKeluar1.setToolTipText("Alt+T");
        BtnKeluar1.setName("BtnKeluar1"); // NOI18N
        BtnKeluar1.setPreferredSize(new java.awt.Dimension(50, 30));
        BtnKeluar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluar1ActionPerformed(evt);
            }
        });
        BtnKeluar1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnKeluar1KeyPressed(evt);
            }
        });
        panelGlass6.add(BtnKeluar1);

        internalFrame6.add(panelGlass6, java.awt.BorderLayout.PAGE_END);

        panelGlass10.setBackground(new java.awt.Color(255, 255, 255));
        panelGlass10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panelGlass10.setName("panelGlass10"); // NOI18N
        panelGlass10.setPreferredSize(new java.awt.Dimension(300, 300));
        panelGlass10.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                panelGlass10MouseDragged(evt);
            }
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                panelGlass10MouseMoved(evt);
            }
        });
        panelGlass10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelGlass10MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panelGlass10MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                panelGlass10MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                panelGlass10MouseReleased(evt);
            }
        });
        internalFrame6.add(panelGlass10, java.awt.BorderLayout.CENTER);

        WindowTtd.getContentPane().add(internalFrame6, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Formulir Edukasi Pasien dan Keluarga Terintegrasi ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
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
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "09-01-2025" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "09-01-2025" }));
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
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 210));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setBackground(new java.awt.Color(250, 255, 245));
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 225));
        FormInput.setLayout(null);

        jLabel4.setText("No.Rawat :");
        jLabel4.setName("jLabel4"); // NOI18N
        FormInput.add(jLabel4);
        jLabel4.setBounds(0, 10, 70, 23);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(74, 10, 136, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        TPasien.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TPasienKeyPressed(evt);
            }
        });
        FormInput.add(TPasien);
        TPasien.setBounds(326, 10, 295, 23);

        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "09-01-2025" }));
        Tanggal.setDisplayFormat("dd-MM-yyyy");
        Tanggal.setName("Tanggal"); // NOI18N
        Tanggal.setOpaque(false);
        Tanggal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalKeyPressed(evt);
            }
        });
        FormInput.add(Tanggal);
        Tanggal.setBounds(74, 40, 90, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        TNoRM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRMKeyPressed(evt);
            }
        });
        FormInput.add(TNoRM);
        TNoRM.setBounds(212, 10, 112, 23);

        jLabel16.setText("Tanggal :");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setVerifyInputWhenFocusTarget(false);
        FormInput.add(jLabel16);
        jLabel16.setBounds(0, 40, 70, 23);

        Jam.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        Jam.setName("Jam"); // NOI18N
        Jam.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JamKeyPressed(evt);
            }
        });
        FormInput.add(Jam);
        Jam.setBounds(168, 40, 62, 23);

        Menit.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Menit.setName("Menit"); // NOI18N
        Menit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MenitKeyPressed(evt);
            }
        });
        FormInput.add(Menit);
        Menit.setBounds(233, 40, 62, 23);

        Detik.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        Detik.setName("Detik"); // NOI18N
        Detik.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DetikKeyPressed(evt);
            }
        });
        FormInput.add(Detik);
        Detik.setBounds(298, 40, 62, 23);

        ChkKejadian.setBorder(null);
        ChkKejadian.setSelected(true);
        ChkKejadian.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkKejadian.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkKejadian.setName("ChkKejadian"); // NOI18N
        FormInput.add(ChkKejadian);
        ChkKejadian.setBounds(363, 40, 23, 23);

        jLabel18.setText("Pegawai:");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(20, 160, 70, 23);

        NIP.setEditable(false);
        NIP.setHighlighter(null);
        NIP.setName("NIP"); // NOI18N
        NIP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NIPKeyPressed(evt);
            }
        });
        FormInput.add(NIP);
        NIP.setBounds(100, 160, 104, 23);

        NamaPegawai.setEditable(false);
        NamaPegawai.setName("NamaPegawai"); // NOI18N
        FormInput.add(NamaPegawai);
        NamaPegawai.setBounds(210, 160, 187, 23);

        btnPegawai.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPegawai.setMnemonic('2');
        btnPegawai.setToolTipText("ALt+2");
        btnPegawai.setName("btnPegawai"); // NOI18N
        btnPegawai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPegawaiActionPerformed(evt);
            }
        });
        btnPegawai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnPegawaiKeyPressed(evt);
            }
        });
        FormInput.add(btnPegawai);
        btnPegawai.setBounds(400, 160, 28, 23);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(625, 10, 60, 23);

        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir"); // NOI18N
        FormInput.add(TglLahir);
        TglLahir.setBounds(689, 10, 100, 23);

        jLabel12.setText("Isi Informasi/ Pendidikan:");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(-20, 100, 140, 23);

        scrollPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane2.setName("scrollPane2"); // NOI18N

        Informasi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Informasi.setColumns(20);
        Informasi.setRows(5);
        Informasi.setName("Informasi"); // NOI18N
        Informasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                InformasiKeyPressed(evt);
            }
        });
        scrollPane2.setViewportView(Informasi);

        FormInput.add(scrollPane2);
        scrollPane2.setBounds(130, 100, 720, 53);

        jLabel93.setText("Profesi / Jabatan / Departemen :");
        jLabel93.setName("jLabel93"); // NOI18N
        FormInput.add(jLabel93);
        jLabel93.setBounds(430, 160, 170, 23);

        PenerimaKet.setHighlighter(null);
        PenerimaKet.setName("PenerimaKet"); // NOI18N
        FormInput.add(PenerimaKet);
        PenerimaKet.setBounds(640, 70, 160, 23);

        jLabel9.setText("Lama Edukasi:");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(390, 40, 80, 23);

        Lama.setHighlighter(null);
        Lama.setName("Lama"); // NOI18N
        FormInput.add(Lama);
        Lama.setBounds(474, 40, 70, 23);

        jLabel10.setText("Metode:");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(590, 40, 50, 23);

        jLabel11.setText("Menit");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(550, 40, 30, 23);

        Metode.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Penjelasan", "Diskusi", "Demonstrasi" }));
        Metode.setName("Metode"); // NOI18N
        FormInput.add(Metode);
        Metode.setBounds(650, 40, 130, 20);

        jLabel13.setText("Media:");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(10, 70, 50, 23);

        Media.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Leaflet", "Lembar Balik", "Audio Video", "Alat peraga" }));
        Media.setName("Media"); // NOI18N
        FormInput.add(Media);
        Media.setBounds(70, 70, 140, 20);

        jLabel14.setText("Evaluasi:");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(220, 70, 50, 23);

        Evaluasi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Mampu menjelaskan", "Mampu mendemonstrasikan", "Perlu pengulangan" }));
        Evaluasi.setName("Evaluasi"); // NOI18N
        FormInput.add(Evaluasi);
        Evaluasi.setBounds(280, 70, 140, 20);

        jLabel15.setText("Penerima:");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(430, 70, 50, 23);

        Penerima.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Pasien", "Orang tua", "Anak", "Lainnya" }));
        Penerima.setName("Penerima"); // NOI18N
        FormInput.add(Penerima);
        Penerima.setBounds(490, 70, 140, 20);

        Jabatan1.setEditable(false);
        Jabatan1.setHighlighter(null);
        Jabatan1.setName("Jabatan1"); // NOI18N
        FormInput.add(Jabatan1);
        Jabatan1.setBounds(610, 160, 209, 23);

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
        FormInput.add(btnAmbilPhoto1);
        btnAmbilPhoto1.setBounds(850, 110, 105, 30);

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
        } else if (NIP.getText().trim().equals("") || NamaPegawai.getText().trim().equals("")) {
            Valid.textKosong(NIP, "Pegawai");
        } else if (Informasi.getText().trim().equals("")) {
            Valid.textKosong(NIP, "Uraian");
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
            Valid.pindah(evt, Informasi, BtnBatal);
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
                if (NIP.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString())) {
                    if (Sequel.cekTanggal48jam(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString() + " " + tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString(), Sequel.ambiltanggalsekarang()) == true) {
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
        } else if (NIP.getText().trim().equals("") || NamaPegawai.getText().trim().equals("")) {
            Valid.textKosong(NIP, "Pegawai");
        } else if (Informasi.getText().trim().equals("")) {
            Valid.textKosong(NIP, "Uraian");
        } else {
            if (tbObat.getSelectedRow() > -1) {
                if (akses.bolehLewatBatasWaktu()) {
                    ganti();
                } else {
                    if (NIP.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString())) {
                        if (Sequel.cekTanggal48jam(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString() + " " + tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString(), Sequel.ambiltanggalsekarang()) == true) {
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
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            try {
                param.put("url", "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/");
            } catch (Exception e) {
            }

            if (TCari.getText().trim().equals("")) {
                Valid.MyReportqry("rptCetakEdukasiPasiendanKeluargaTerintegrasi.jasper", "report", "::[ Formulir Edukasi Pasien dan Keluarga Terintegrasi ]::",
                        "SELECT pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, reg_periksa.no_rawat, reg_periksa.almt_pj, edukasi_pasien_keluarga_detail.tanggal, "
                        + "edukasi_pasien_keluarga_detail.jam, edukasi_pasien_keluarga_detail.lama, edukasi_pasien_keluarga_detail.metode, edukasi_pasien_keluarga_detail.media, "
                        + "edukasi_pasien_keluarga_detail.evaluasi, edukasi_pasien_keluarga_detail.penerima, edukasi_pasien_keluarga_detail.penerimaket, edukasi_pasien_keluarga_detail.informasi, "
                        + "edukasi_pasien_keluarga_detail.nip, edukasipasien_ttd.url_image, pegawai.nama, pegawai.jbtn, pegawai.departemen FROM pasien INNER JOIN reg_periksa ON pasien.no_rkm_medis = reg_periksa.no_rkm_medis "
                        + "INNER JOIN edukasi_pasien_keluarga_detail ON reg_periksa.no_rawat = edukasi_pasien_keluarga_detail.no_rawat INNER JOIN edukasipasien_ttd ON reg_periksa.no_rawat = edukasipasien_ttd.no_rawat "
                        + "INNER JOIN pegawai ON edukasi_pasien_keluarga_detail.nip = pegawai.nik where "
                        + "edukasi_pasien_keluarga_detail.tanggal between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' order by edukasi_pasien_keluarga_detail.tanggal,edukasi_pasien_keluarga_detail.jam", param);
            } else {
                Valid.MyReportqry("rptCetakEdukasiPasiendanKeluargaTerintegrasi.jasper", "report", "::[ Data Catatan Keperawatan Rawat Inap ]::",
                        "SELECT pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, reg_periksa.no_rawat, reg_periksa.almt_pj, edukasi_pasien_keluarga_detail.tanggal, "
                        + "edukasi_pasien_keluarga_detail.jam, edukasi_pasien_keluarga_detail.lama, edukasi_pasien_keluarga_detail.metode, edukasi_pasien_keluarga_detail.media, "
                        + "edukasi_pasien_keluarga_detail.evaluasi, edukasi_pasien_keluarga_detail.penerima, edukasi_pasien_keluarga_detail.penerimaket, edukasi_pasien_keluarga_detail.informasi, "
                        + "edukasi_pasien_keluarga_detail.nip, edukasipasien_ttd.url_image, pegawai.nama, pegawai.jbtn, pegawai.departemen FROM pasien INNER JOIN reg_periksa ON pasien.no_rkm_medis = reg_periksa.no_rkm_medis "
                        + "INNER JOIN edukasi_pasien_keluarga_detail ON reg_periksa.no_rawat = edukasi_pasien_keluarga_detail.no_rawat INNER JOIN edukasipasien_ttd ON reg_periksa.no_rawat = edukasipasien_ttd.no_rawat "
                        + "INNER JOIN pegawai ON edukasi_pasien_keluarga_detail.nip = pegawai.nik where "
                        + "edukasi_pasien_keluarga_detail.tanggal between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' and "
                        + "(reg_periksa.no_rawat like '%" + TCari.getText().trim() + "%' or pasien.no_rkm_medis like '%" + TCari.getText().trim() + "%' or "
                        + "pasien.nm_pasien like '%" + TCari.getText().trim() + "%' or edukasi_pasien_keluarga_detail.nip like '%" + TCari.getText().trim() + "%' or pegawai.nama like '%" + TCari.getText().trim() + "%') "
                        + "order by edukasi_pasien_keluarga_detail.tanggal,edukasi_pasien_keluarga_detail.jam ", param);
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
        Valid.pindah(evt, Menit, btnPegawai);
    }//GEN-LAST:event_DetikKeyPressed

    private void NIPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NIPKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            NamaPegawai.setText(pegawai.tampil3(NIP.getText()));
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            Detik.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            Informasi.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_UP) {
            btnPegawaiActionPerformed(null);
        }
    }//GEN-LAST:event_NIPKeyPressed

    private void btnPegawaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPegawaiActionPerformed
        pegawai.emptTeks();
        //   pegawai.isCek();
        pegawai.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        pegawai.setLocationRelativeTo(internalFrame1);
        pegawai.setVisible(true);
    }//GEN-LAST:event_btnPegawaiActionPerformed

    private void btnPegawaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnPegawaiKeyPressed
        Valid.pindah(evt, Detik, Informasi);
    }//GEN-LAST:event_btnPegawaiKeyPressed

    private void MnTtdPasienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnTtdPasienActionPerformed
        //   WindowTtd.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        WindowTtd.setSize(350, 350);
        WindowTtd.setLocationRelativeTo(internalFrame1);
        WindowTtd.setVisible(true);

    }//GEN-LAST:event_MnTtdPasienActionPerformed

    private void InformasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_InformasiKeyPressed
        Valid.pindah2(evt, NIP, BtnSimpan);
    }//GEN-LAST:event_InformasiKeyPressed

    private void btnAmbilPhoto1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAmbilPhoto1ActionPerformed
        akses.setform("RMFormulirEdukasiPasiendanKeluargaTerintegrasi");
        //   template.emptTeks();
        template.isCek();
        template.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        template.setLocationRelativeTo(internalFrame1);
        template.setVisible(true);
    }//GEN-LAST:event_btnAmbilPhoto1ActionPerformed

    private void BtnSimpan1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpan1ActionPerformed
        try {
            // Buat objek Robot
            Robot r = new Robot();

            // Ambil posisi dan ukuran panelGlass10
            Point location = panelGlass10.getLocationOnScreen(); // Posisi absolut panel
            Insets insets = panelGlass10.getInsets(); // Mendapatkan padding atau border (jika ada)

            // Hitung area tangkapan tanpa border
            int x = location.x + insets.left;
            int y = location.y + insets.top;
            int width = panelGlass10.getWidth() - insets.left - insets.right;
            int height = panelGlass10.getHeight() - insets.top - insets.bottom;

            // Definisikan area tangkapan
            Rectangle capture = new Rectangle(x, y, width, height);

            // Tangkap gambar dari area panelGlass10
            BufferedImage image = r.createScreenCapture(capture);

            // Simpan gambar ke file
            String TFile = "Ed" + TNoRw.getText().replaceAll("/", "_") + Tanggal.getSelectedItem() + Jam.getSelectedItem() + Menit.getSelectedItem() + Detik.getSelectedItem() + NIP.getText().replaceAll("/","") + ".png";
            String TPath = "imagefreehand/edukasipasien/ttd/";
            String filePath = "tmpImageFreehand/" + TFile;
            ImageIO.write(image, "png", new File(filePath));

            // Upload gambar
            uploadImage(TFile, TPath);

            // Menyimpan ke database
            if (Sequel.cariInteger("select count(no_rawat) as jumlah from edukasipasien_ttd where url_image ='"+TFile + TPath+"' no_rawat='" + TNoRw.getText() + "'") > 0) {
                Sequel.mengedittf("edukasipasien_ttd", "no_rawat=?", "tanggal=?,jam=?,url_image=?", 4, new String[]{
                    tanggalNow.format(new Date()), jamNow.format(new Date()), TPath + TFile, TNoRw.getText()
                });
            } else {
                Sequel.menyimpantf("edukasipasien_ttd", "?,?,?,?", "No.Rawat", 4, new String[]{
                    TNoRw.getText(), tanggalNow.format(new Date()), jamNow.format(new Date()), TPath + TFile
                });
            }

            // Tutup dialog
            WindowTtd.dispose();
        } catch (AWTException | IOException ex) {
            Logger.getLogger(RMFormulirEdukasiPasiendanKeluargaTerintegrasi.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Gagal menyimpan gambar tanda tangan: " + ex.getMessage(),
                    "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_BtnSimpan1ActionPerformed

    private void BtnSimpan1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpan1KeyPressed

    }//GEN-LAST:event_BtnSimpan1KeyPressed

    private void BtnHapus1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapus1ActionPerformed
        panelGlass10.clearAll(); // Hapus semua tanda tangan
    }//GEN-LAST:event_BtnHapus1ActionPerformed

    private void BtnHapus1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapus1KeyPressed

    }//GEN-LAST:event_BtnHapus1KeyPressed

    private void BtnKeluar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluar1ActionPerformed
        WindowTtd.dispose();
    }//GEN-LAST:event_BtnKeluar1ActionPerformed

    private void BtnKeluar1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluar1KeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            dispose();
        }
    }//GEN-LAST:event_BtnKeluar1KeyPressed

    private void panelGlass10MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelGlass10MouseDragged
        panelGlass10.addPoint(evt.getPoint());
    }//GEN-LAST:event_panelGlass10MouseDragged

    private void panelGlass10MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelGlass10MouseMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_panelGlass10MouseMoved

    private void panelGlass10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelGlass10MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_panelGlass10MouseClicked

    private void panelGlass10MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelGlass10MouseExited
        // TODO add your handling code here:
    }//GEN-LAST:event_panelGlass10MouseExited

    private void panelGlass10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelGlass10MousePressed
        panelGlass10.startNewLine(); // Mulai garis baru
    }//GEN-LAST:event_panelGlass10MousePressed

    private void panelGlass10MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelGlass10MouseReleased
        panelGlass10.finishLine(); // Simpan garis ke daftar permanen
    }//GEN-LAST:event_panelGlass10MouseReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMFormulirEdukasiPasiendanKeluargaTerintegrasi dialog = new RMFormulirEdukasiPasiendanKeluargaTerintegrasi(new javax.swing.JFrame(), true);
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
    private widget.Button BtnHapus1;
    private widget.Button BtnKeluar;
    private widget.Button BtnKeluar1;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.Button BtnSimpan1;
    private widget.CekBox ChkInput;
    private widget.CekBox ChkKejadian;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.ComboBox Detik;
    private widget.ComboBox Evaluasi;
    private widget.PanelBiasa FormInput;
    private widget.TextArea Informasi;
    private widget.TextBox JK;
    private widget.TextBox Jabatan1;
    private widget.ComboBox Jam;
    private widget.Label LCount;
    private widget.TextBox Lama;
    private widget.ComboBox Media;
    private widget.ComboBox Menit;
    private widget.ComboBox Metode;
    private javax.swing.JMenuItem MnTtdPasien;
    private widget.TextBox NIP;
    private widget.TextBox NamaPegawai;
    private javax.swing.JPanel PanelInput;
    private widget.ComboBox Penerima;
    private widget.TextBox PenerimaKet;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Tanggal Tanggal;
    private widget.TextBox TanggalRegistrasi;
    private widget.TextBox TglLahir;
    private widget.TextBox Umur;
    private javax.swing.JDialog WindowTtd;
    private widget.Button btnAmbilPhoto1;
    private widget.Button btnPegawai;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame6;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel4;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private widget.Label jLabel93;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private freehand.PanelTTD panelGlass10;
    private widget.panelisi panelGlass6;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.ScrollPane scrollPane2;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            if (TCari.getText().toString().trim().equals("")) {
                ps = koneksi.prepareStatement(
                        "SELECT reg_periksa.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir,"
                        + "edukasi_pasien_keluarga_detail.tanggal, edukasi_pasien_keluarga_detail.jam,edukasi_pasien_keluarga_detail.lama, "
                        + "edukasi_pasien_keluarga_detail.metode,edukasi_pasien_keluarga_detail.media,edukasi_pasien_keluarga_detail.evaluasi,"
                        + "edukasi_pasien_keluarga_detail.penerima, edukasi_pasien_keluarga_detail.penerimaket,edukasi_pasien_keluarga_detail.informasi, "
                        + "edukasi_pasien_keluarga_detail.nip,pegawai.nama,pegawai.departemen FROM   reg_periksa INNER JOIN pasien ON reg_periksa.no_rkm_medis = pasien.no_rkm_medis,"
                        + "edukasi_pasien_keluarga_detail INNER JOIN pegawai ON edukasi_pasien_keluarga_detail.nip = pegawai.nik where "
                        + "edukasi_pasien_keluarga_detail.tanggal between ? and ? order by edukasi_pasien_keluarga_detail.tanggal,edukasi_pasien_keluarga_detail.jam");
            } else {
                ps = koneksi.prepareStatement(
                        "SELECT reg_periksa.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir,"
                        + "edukasi_pasien_keluarga_detail.tanggal, edukasi_pasien_keluarga_detail.jam,edukasi_pasien_keluarga_detail.lama, "
                        + "edukasi_pasien_keluarga_detail.metode,edukasi_pasien_keluarga_detail.media,edukasi_pasien_keluarga_detail.evaluasi,"
                        + "edukasi_pasien_keluarga_detail.penerima, edukasi_pasien_keluarga_detail.penerimaket,edukasi_pasien_keluarga_detail.informasi, "
                        + "edukasi_pasien_keluarga_detail.nip,pegawai.nama,pegawai.departemen FROM   reg_periksa INNER JOIN pasien ON reg_periksa.no_rkm_medis = pasien.no_rkm_medis,"
                        + "edukasi_pasien_keluarga_detail INNER JOIN pegawai ON edukasi_pasien_keluarga_detail.nip = pegawai.nik where "
                        + "edukasi_pasien_keluarga_detail.tanggal between ? and ? and (reg_periksa.no_rawat like ? or pasien.no_rkm_medis like ? or pasien.nm_pasien like ? or edukasi_pasien_keluarga_detail.nip like ? or pegawai.nama like ?) "
                        + "order by edukasi_pasien_keluarga_detail.tanggal,edukasi_pasien_keluarga_detail.jam ");
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
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("tgl_lahir"), rs.getString("tanggal"), rs.getString("jam"), rs.getString("lama"),
                        rs.getString("metode"), rs.getString("media"), rs.getString("evaluasi"), rs.getString("penerima"),
                        rs.getString("penerimaket"), rs.getString("informasi"), rs.getString("nip"), rs.getString("nama"),
                        rs.getString("departemen")
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
        LCount.setText("" + tabMode.getRowCount());
    }

    public void emptTeks() {
        Lama.setText("");
        PenerimaKet.setText("");
        Media.setSelectedIndex(0);
        Evaluasi.setSelectedIndex(0);
        Penerima.setSelectedIndex(0);
        Informasi.setText("");
        Tanggal.setDate(new Date());
        Informasi.requestFocus();
    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            /*
             "No.Rawat", "No.R.M.", "Nama Pasien", "Tgl.Lahir", "Tanggal", "Jam", "Lama Edukasi", 
    "Metode", "Media", "Evaluasi", "Penerima", "Nama Penerima", "Isi Informasi", "NIP", "Nama Pegawai"
             */

            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            TglLahir.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
            Jam.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString().substring(0, 2));
            Menit.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString().substring(3, 5));
            Detik.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString().substring(6, 8));
            Informasi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString());
            Valid.SetTgl(Tanggal, tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
        }
    }

    private void isRawat() {
        try {
            ps = koneksi.prepareStatement(
                    "select reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir,reg_periksa.tgl_registrasi,reg_periksa.umurdaftar,"
                    + "reg_periksa.sttsumur,reg_periksa.jam_reg from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis where reg_periksa.no_rawat=?");
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    DTPCari1.setDate(rs.getDate("tgl_registrasi"));
                    TPasien.setText(rs.getString("nm_pasien"));
                    JK.setText(rs.getString("jk"));
                    Umur.setText(rs.getString("umurdaftar") + " " + rs.getString("sttsumur"));
                    TglLahir.setText(rs.getString("tgl_lahir"));
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
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 210));
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
        BtnSimpan.setEnabled(akses.getcatatan_keperawatan_ranap());
        BtnHapus.setEnabled(akses.getcatatan_keperawatan_ranap());
        BtnEdit.setEnabled(akses.getcatatan_keperawatan_ranap());
        BtnPrint.setEnabled(akses.getcatatan_keperawatan_ranap());
        if (akses.getjml2() >= 1) {
            NIP.setEditable(false);
            btnPegawai.setEnabled(false);
            NIP.setText(akses.getkode());
            NamaPegawai.setText(pegawai.tampil3(NIP.getText()));
            if (NamaPegawai.getText().equals("")) {
                NIP.setText("");
                JOptionPane.showMessageDialog(null, "User login bukan pegawai...!!");
            }
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
        new Timer(1000, taskPerformer).start();
    }

    private void ganti() {
        if (Sequel.mengedittf("edukasi_pasien_keluarga_detail", "tanggal=? and jam=? and no_rawat=?", "no_rawat=?,tanggal=?,jam=?,uraian=?,nip=?", 8, new String[]{
            TNoRw.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + ""), Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(),
            Informasi.getText(), NIP.getText(), tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString(),
            tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
        }) == true) {
            tbObat.setValueAt(TNoRw.getText(), tbObat.getSelectedRow(), 0);
            tbObat.setValueAt(TNoRM.getText(), tbObat.getSelectedRow(), 1);
            tbObat.setValueAt(TPasien.getText(), tbObat.getSelectedRow(), 2);
            tbObat.setValueAt(Umur.getText(), tbObat.getSelectedRow(), 3);
            tbObat.setValueAt(JK.getText(), tbObat.getSelectedRow(), 4);
            tbObat.setValueAt(TglLahir.getText(), tbObat.getSelectedRow(), 5);
            tbObat.setValueAt(Valid.SetTgl(Tanggal.getSelectedItem() + ""), tbObat.getSelectedRow(), 6);
            tbObat.setValueAt(Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(), tbObat.getSelectedRow(), 7);
            tbObat.setValueAt(Informasi.getText(), tbObat.getSelectedRow(), 8);
            tbObat.setValueAt(NIP.getText(), tbObat.getSelectedRow(), 9);
            tbObat.setValueAt(NamaPegawai.getText(), tbObat.getSelectedRow(), 10);
            emptTeks();
        }
    }

    private void hapus() {
        if (Sequel.queryu2tf("delete from edukasi_pasien_keluarga_detail where tanggal=? and jam=? and no_rawat=?", 3, new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
        }) == true) {
            tabMode.removeRow(tbObat.getSelectedRow());
            LCount.setText("" + tabMode.getRowCount());
            emptTeks();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
        }
    }

    private void simpan() {
        if (Sequel.menyimpantf("edukasi_pasien_keluarga_detail", "?,?,?,?,?,?,?,?,?,?,?", "Data", 11, new String[]{
            TNoRw.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + ""), Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(), Lama.getText(), Metode.getSelectedItem().toString(),
            Media.getSelectedItem().toString(), Evaluasi.getSelectedItem().toString(), Penerima.getSelectedItem().toString(), PenerimaKet.getText(), Informasi.getText(), NIP.getText()
        }) == true) {
            tabMode.addRow(new String[]{
                TNoRw.getText(), TNoRM.getText(), TPasien.getText(), TglLahir.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + ""), Jam.getSelectedItem() + ":" + Menit.getSelectedItem() + ":" + Detik.getSelectedItem(), Lama.getText(), Metode.getSelectedItem().toString(),
                Media.getSelectedItem().toString(), Evaluasi.getSelectedItem().toString(), Penerima.getSelectedItem().toString(), PenerimaKet.getText(), Informasi.getText(), NIP.getText(), NamaPegawai.getText(), Jabatan1.getText()
            });
            LCount.setText("" + tabMode.getRowCount());
            emptTeks();
        }
    }

    void uploadImage(String FileName, String docpath) {
        try {
            //    docpath = "rehabpertama";
            File file = new File("tmpImageFreehand/" + FileName);
            byte[] data = new byte[(int) file.length()];
            data = FileUtils.readFileToByteArray(file);
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost postRequest = new HttpPost("http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/upload.php?doc=" + docpath);
            postRequest.addHeader("X-Upload-Token", fungsi.koneksiDB.TOKENUNGGAH());
            ByteArrayBody fileData = new ByteArrayBody(data, FileName);
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            reqEntity.addPart("file", fileData);
            postRequest.setEntity(reqEntity);
            httpClient.execute(postRequest);
//        HttpResponse response = (HttpResponse) httpClient.execute(postRequest); 
            //      deleteFile();

        } catch (Exception e) {
            System.out.println("Upload error" + e);
        }
    }

    void deleteFile() {
        File file = new File("tmpImageFreehand");
        String[] myFiles;
        if (file.isDirectory()) {
            myFiles = file.list();
            for (int i = 0; i < myFiles.length; i++) {
                File myFile = new File(file, myFiles[i]);
                myFile.delete();
            }
        }
    }

}
