/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * kontribusi dari dokter Salim Mulyana
 */
package rekammedis;

import surat.*;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import kepegawaian.DlgCariDokter;

/**
 *
 * @author salimmulyana
 */
public final class RMFormulirTindakanIntubasiVentilatorRscb extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private DlgCariDokter dokter = new DlgCariDokter(null, false);
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
    public RMFormulirTindakanIntubasiVentilatorRscb(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(628, 674);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No Rawat", "No RM", "Nama Pasien", "Tanggal Lahir", "Jenis Kelamin", "Kode Dokter", "Nama Dokter", "Tanggal/ Jam", "Diagnosa", "Indikasi Pemasangan",
            "Tanggal Intubasi", "Tanggal Pasang Ventilator", "Tanggal Extubasi", "Tanggal Lepas Ventilator", "TD", "Nadi", "RR", "SpO2", "Mode", "FiO2", "PEEP", "i : E"
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

        for (int i = 0; i < tbObat.getColumnCount(); i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);

            switch (i) {
                case 0: // "No Rawat"
                case 1: // "No RM"
                    column.setPreferredWidth(105);
                    break;
                case 2: // "Nama Pasien"
                    column.setPreferredWidth(150);
                    break;
                case 3: // "Tanggal Lahir"
                    column.setPreferredWidth(100);
                    break;
                case 4: // "Jenis Kelamin"
                    column.setPreferredWidth(90);
                    break;
                case 5: // "Kode Dokter"
                case 6: // "Nama Dokter"
                    column.setPreferredWidth(150);
                    break;
                case 7: // "Tanggal/ Jam"
                case 10: // "Tanggal Intubasi"
                case 11: // "Tanggal Pasang Ventilator"
                case 12: // "Tanggal Extubasi"
                case 13: // "Tanggal Lepas Ventilator"
                    column.setPreferredWidth(120);
                    break;
                case 8: // "Diagnosa"
                case 9: // "Indikasi Pemasangan"
                    column.setPreferredWidth(200);
                    break;
                default:
                    column.setPreferredWidth(80);
                    break;
            }
        }

        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

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
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (dokter.getTable().getSelectedRow() != -1) {
                    KdDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                    NmDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                    KdDokter.requestFocus();
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
        MnCetak = new javax.swing.JMenuItem();
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
        DTPCari = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        PanelInput = new javax.swing.JPanel();
        FormInput = new widget.PanelBiasa();
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        TNoRM = new widget.TextBox();
        label14 = new widget.Label();
        KdDokter = new widget.TextBox();
        NmDokter = new widget.TextBox();
        BtnDokter = new widget.Button();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        Jk = new widget.TextBox();
        jLabel10 = new widget.Label();
        jLabel11 = new widget.Label();
        jLabel12 = new widget.Label();
        Peep = new widget.TextBox();
        jLabel13 = new widget.Label();
        Fi02 = new widget.TextBox();
        jLabel15 = new widget.Label();
        jLabel16 = new widget.Label();
        Nadi = new widget.TextBox();
        jLabel17 = new widget.Label();
        jLabel18 = new widget.Label();
        iE = new widget.TextBox();
        jLabel22 = new widget.Label();
        TD = new widget.TextBox();
        jLabel20 = new widget.Label();
        jLabel23 = new widget.Label();
        jLabel25 = new widget.Label();
        RR = new widget.TextBox();
        jLabel26 = new widget.Label();
        jLabel37 = new widget.Label();
        IndikatorPemasangan = new widget.TextBox();
        jLabel28 = new widget.Label();
        Mode = new widget.TextBox();
        jLabel94 = new widget.Label();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel38 = new widget.Label();
        Diagnosa = new widget.TextBox();
        jLabel41 = new widget.Label();
        jLabel29 = new widget.Label();
        SPO = new widget.TextBox();
        jLabel35 = new widget.Label();
        label11 = new widget.Label();
        TglAsuhan = new widget.Tanggal();
        label12 = new widget.Label();
        TanggalIntubasi = new widget.Tanggal();
        label13 = new widget.Label();
        TglPemasangan = new widget.Tanggal();
        label15 = new widget.Label();
        TanggalExtubasi = new widget.Tanggal();
        label16 = new widget.Label();
        TglPencabutan = new widget.Tanggal();
        ChkInput = new widget.CekBox();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnCetak.setBackground(new java.awt.Color(250, 250, 250));
        MnCetak.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnCetak.setForeground(new java.awt.Color(50, 50, 50));
        MnCetak.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnCetak.setText("Cetak ");
        MnCetak.setName("MnCetak"); // NOI18N
        MnCetak.setPreferredSize(new java.awt.Dimension(200, 26));
        MnCetak.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnCetakActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnCetak);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Formulir Tindakan Intubasi & Ventilator ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
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

        jLabel19.setText("Tanggal :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(53, 23));
        panelGlass9.add(jLabel19);

        DTPCari.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "16-04-2025 09:12:20" }));
        DTPCari.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        DTPCari.setName("DTPCari"); // NOI18N
        DTPCari.setOpaque(false);
        DTPCari.setPreferredSize(new java.awt.Dimension(132, 23));
        panelGlass9.add(DTPCari);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel21);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "16-04-2025 09:12:20" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(132, 23));
        panelGlass9.add(DTPCari1);

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
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 300));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 165));
        FormInput.setLayout(null);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(74, 10, 131, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        FormInput.add(TPasien);
        TPasien.setBounds(309, 10, 260, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        FormInput.add(TNoRM);
        TNoRM.setBounds(207, 10, 100, 23);

        label14.setText("Dokter :");
        label14.setName("label14"); // NOI18N
        label14.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label14);
        label14.setBounds(0, 40, 70, 23);

        KdDokter.setEditable(false);
        KdDokter.setName("KdDokter"); // NOI18N
        KdDokter.setPreferredSize(new java.awt.Dimension(80, 23));
        KdDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdDokterKeyPressed(evt);
            }
        });
        FormInput.add(KdDokter);
        KdDokter.setBounds(74, 40, 90, 23);

        NmDokter.setEditable(false);
        NmDokter.setName("NmDokter"); // NOI18N
        NmDokter.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(NmDokter);
        NmDokter.setBounds(166, 40, 180, 23);

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
        BtnDokter.setBounds(348, 40, 28, 23);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(580, 10, 60, 23);

        TglLahir.setEditable(false);
        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir"); // NOI18N
        FormInput.add(TglLahir);
        TglLahir.setBounds(644, 10, 80, 23);

        Jk.setEditable(false);
        Jk.setHighlighter(null);
        Jk.setName("Jk"); // NOI18N
        FormInput.add(Jk);
        Jk.setBounds(774, 10, 80, 23);

        jLabel10.setText("No.Rawat :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(0, 10, 70, 23);

        jLabel11.setText("J.K. :");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(740, 10, 30, 23);

        jLabel12.setText("Peep :");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(240, 240, 50, 23);

        Peep.setFocusTraversalPolicyProvider(true);
        Peep.setName("Peep"); // NOI18N
        Peep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PeepKeyPressed(evt);
            }
        });
        FormInput.add(Peep);
        Peep.setBounds(290, 240, 45, 23);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("Kg");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(340, 240, 30, 23);

        Fi02.setFocusTraversalPolicyProvider(true);
        Fi02.setName("Fi02"); // NOI18N
        Fi02.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Fi02KeyPressed(evt);
            }
        });
        FormInput.add(Fi02);
        Fi02.setBounds(170, 240, 45, 23);

        jLabel15.setText("FiO2 :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(140, 240, 30, 23);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("x/menit");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(300, 190, 50, 23);

        Nadi.setFocusTraversalPolicyProvider(true);
        Nadi.setName("Nadi"); // NOI18N
        Nadi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NadiKeyPressed(evt);
            }
        });
        FormInput.add(Nadi);
        Nadi.setBounds(250, 190, 45, 23);

        jLabel17.setText("Nadi :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(210, 190, 40, 23);

        jLabel18.setText("i : E");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(360, 240, 40, 23);

        iE.setFocusTraversalPolicyProvider(true);
        iE.setName("iE"); // NOI18N
        iE.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                iEKeyPressed(evt);
            }
        });
        FormInput.add(iE);
        iE.setBounds(410, 240, 45, 23);

        jLabel22.setText("TD :");
        jLabel22.setName("jLabel22"); // NOI18N
        FormInput.add(jLabel22);
        jLabel22.setBounds(10, 190, 40, 23);

        TD.setFocusTraversalPolicyProvider(true);
        TD.setName("TD"); // NOI18N
        TD.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TDKeyPressed(evt);
            }
        });
        FormInput.add(TD);
        TD.setBounds(60, 190, 76, 23);

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("°C");
        jLabel20.setName("jLabel20"); // NOI18N
        FormInput.add(jLabel20);
        jLabel20.setBounds(460, 240, 30, 23);

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("mmHg");
        jLabel23.setName("jLabel23"); // NOI18N
        FormInput.add(jLabel23);
        jLabel23.setBounds(140, 190, 50, 23);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("x/menit");
        jLabel25.setName("jLabel25"); // NOI18N
        FormInput.add(jLabel25);
        jLabel25.setBounds(460, 190, 50, 23);

        RR.setFocusTraversalPolicyProvider(true);
        RR.setName("RR"); // NOI18N
        RR.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RRKeyPressed(evt);
            }
        });
        FormInput.add(RR);
        RR.setBounds(410, 190, 45, 23);

        jLabel26.setText("RR :");
        jLabel26.setName("jLabel26"); // NOI18N
        FormInput.add(jLabel26);
        jLabel26.setBounds(370, 190, 40, 23);

        jLabel37.setText("Indikasi Pemasangan:");
        jLabel37.setName("jLabel37"); // NOI18N
        FormInput.add(jLabel37);
        jLabel37.setBounds(0, 70, 120, 23);

        IndikatorPemasangan.setFocusTraversalPolicyProvider(true);
        IndikatorPemasangan.setName("IndikatorPemasangan"); // NOI18N
        IndikatorPemasangan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                IndikatorPemasanganKeyPressed(evt);
            }
        });
        FormInput.add(IndikatorPemasangan);
        IndikatorPemasangan.setBounds(130, 70, 720, 23);

        jLabel28.setText("Mode:");
        jLabel28.setName("jLabel28"); // NOI18N
        FormInput.add(jLabel28);
        jLabel28.setBounds(-10, 240, 70, 23);

        Mode.setFocusTraversalPolicyProvider(true);
        Mode.setName("Mode"); // NOI18N
        Mode.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ModeKeyPressed(evt);
            }
        });
        FormInput.add(Mode);
        Mode.setBounds(60, 240, 60, 23);

        jLabel94.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel94.setText("Monitor:");
        jLabel94.setName("jLabel94"); // NOI18N
        FormInput.add(jLabel94);
        jLabel94.setBounds(10, 170, 180, 23);

        jSeparator1.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator1.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator1.setName("jSeparator1"); // NOI18N
        FormInput.add(jSeparator1);
        jSeparator1.setBounds(0, 70, 880, 1);

        jLabel38.setText("Diagnosa:");
        jLabel38.setName("jLabel38"); // NOI18N
        FormInput.add(jLabel38);
        jLabel38.setBounds(570, 40, 70, 23);

        Diagnosa.setName("Diagnosa"); // NOI18N
        Diagnosa.setPreferredSize(new java.awt.Dimension(207, 23));
        Diagnosa.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaKeyPressed(evt);
            }
        });
        FormInput.add(Diagnosa);
        Diagnosa.setBounds(654, 40, 200, 23);

        jLabel41.setText("Setting ventilator:");
        jLabel41.setName("jLabel41"); // NOI18N
        FormInput.add(jLabel41);
        jLabel41.setBounds(-30, 220, 127, 23);

        jLabel29.setText("SpO2 :");
        jLabel29.setName("jLabel29"); // NOI18N
        FormInput.add(jLabel29);
        jLabel29.setBounds(520, 190, 40, 23);

        SPO.setFocusTraversalPolicyProvider(true);
        SPO.setName("SPO"); // NOI18N
        SPO.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SPOKeyPressed(evt);
            }
        });
        FormInput.add(SPO);
        SPO.setBounds(570, 190, 45, 23);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel35.setText("%");
        jLabel35.setName("jLabel35"); // NOI18N
        FormInput.add(jLabel35);
        jLabel35.setBounds(630, 190, 30, 23);

        label11.setText("Tanggal :");
        label11.setName("label11"); // NOI18N
        label11.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label11);
        label11.setBounds(380, 40, 52, 23);

        TglAsuhan.setForeground(new java.awt.Color(50, 70, 50));
        TglAsuhan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "16-04-2025 09:12:20" }));
        TglAsuhan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglAsuhan.setName("TglAsuhan"); // NOI18N
        TglAsuhan.setOpaque(false);
        TglAsuhan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TglAsuhanKeyPressed(evt);
            }
        });
        FormInput.add(TglAsuhan);
        TglAsuhan.setBounds(436, 40, 130, 23);

        label12.setText("Tanggal intubasi:");
        label12.setName("label12"); // NOI18N
        label12.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label12);
        label12.setBounds(22, 100, 100, 23);

        TanggalIntubasi.setForeground(new java.awt.Color(50, 70, 50));
        TanggalIntubasi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "16-04-2025 09:12:20" }));
        TanggalIntubasi.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TanggalIntubasi.setName("TanggalIntubasi"); // NOI18N
        TanggalIntubasi.setOpaque(false);
        TanggalIntubasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalIntubasiKeyPressed(evt);
            }
        });
        FormInput.add(TanggalIntubasi);
        TanggalIntubasi.setBounds(130, 100, 130, 23);

        label13.setText("Tanggal pasang ventilator:");
        label13.setName("label13"); // NOI18N
        label13.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label13);
        label13.setBounds(270, 100, 140, 23);

        TglPemasangan.setForeground(new java.awt.Color(50, 70, 50));
        TglPemasangan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "16-04-2025 09:12:20" }));
        TglPemasangan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglPemasangan.setName("TglPemasangan"); // NOI18N
        TglPemasangan.setOpaque(false);
        TglPemasangan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TglPemasanganKeyPressed(evt);
            }
        });
        FormInput.add(TglPemasangan);
        TglPemasangan.setBounds(420, 100, 130, 23);

        label15.setText("Tanggal extubasi:");
        label15.setName("label15"); // NOI18N
        label15.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label15);
        label15.setBounds(20, 130, 100, 23);

        TanggalExtubasi.setForeground(new java.awt.Color(50, 70, 50));
        TanggalExtubasi.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "16-04-2025 09:12:20" }));
        TanggalExtubasi.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TanggalExtubasi.setName("TanggalExtubasi"); // NOI18N
        TanggalExtubasi.setOpaque(false);
        TanggalExtubasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalExtubasiKeyPressed(evt);
            }
        });
        FormInput.add(TanggalExtubasi);
        TanggalExtubasi.setBounds(130, 130, 130, 23);

        label16.setText("Tanggal lepas ventilator:");
        label16.setName("label16"); // NOI18N
        label16.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label16);
        label16.setBounds(270, 130, 140, 23);

        TglPencabutan.setForeground(new java.awt.Color(50, 70, 50));
        TglPencabutan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "16-04-2025 09:12:20" }));
        TglPencabutan.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TglPencabutan.setName("TglPencabutan"); // NOI18N
        TglPencabutan.setOpaque(false);
        TglPencabutan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TglPencabutanKeyPressed(evt);
            }
        });
        FormInput.add(TglPencabutan);
        TglPencabutan.setBounds(420, 130, 130, 23);

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

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (IndikatorPemasangan.getText().trim().equals("")) {
            Valid.textKosong(IndikatorPemasangan, "Indikasi Pemasangan");
        } else if (TD.getText().trim().equals("")) {
            Valid.textKosong(TD, "Tensi");
        } else if (Nadi.getText().trim().equals("")) {
            Valid.textKosong(Nadi, "Nadi");
        } else if (RR.getText().trim().equals("")) {
            Valid.textKosong(RR, "Respirasi");
        } else if (SPO.getText().trim().equals("")) {
            Valid.textKosong(SPO, "SPO");
        } else if (TD.getText().trim().equals("")) {
            Valid.textKosong(Mode, "Mode");
        } else if (Fi02.getText().trim().equals("")) {
            Valid.textKosong(Fi02, "Fi02");
        } else if (Peep.getText().trim().equals("")) {
            Valid.textKosong(Peep, "Peep");
        } else if (iE.getText().trim().equals("")) {
            Valid.textKosong(iE, "i:E");
        } else if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "pasien");
        } else {
            if (Sequel.menyimpantf("formulir_tindakan_ventilator", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Penggunaan Ventilator", 17, new String[]{
                TNoRw.getText(), KdDokter.getText(), Diagnosa.getText(), IndikatorPemasangan.getText(), Valid.SetTgl(TanggalIntubasi.getSelectedItem() + "") + " " + TanggalIntubasi.getSelectedItem().toString().substring(11, 19),
                Valid.SetTgl(TglPemasangan.getSelectedItem() + "") + " " + TglPemasangan.getSelectedItem().toString().substring(11, 19),
                Valid.SetTgl(TanggalExtubasi.getSelectedItem() + "") + " " + TanggalExtubasi.getSelectedItem().toString().substring(11, 19),
                Valid.SetTgl(TglPencabutan.getSelectedItem() + "") + " " + TglPencabutan.getSelectedItem().toString().substring(11, 19),
                TD.getText(), Nadi.getText(), RR.getText(), SPO.getText(), Mode.getText(), Fi02.getText(), Peep.getText(), iE.getText()
            }) == true) {
                tabMode.addRow(new String[]{
                    TNoRw.getText(), TNoRM.getText(), TPasien.getText(), TglLahir.getText(), Jk.getText(), KdDokter.getText(), NmDokter.getText(), Diagnosa.getText(), IndikatorPemasangan.getText(), Valid.SetTgl(TanggalIntubasi.getSelectedItem() + "") + " " + TanggalIntubasi.getSelectedItem().toString().substring(11, 19),
                    Valid.SetTgl(TglPemasangan.getSelectedItem() + "") + " " + TglPemasangan.getSelectedItem().toString().substring(11, 19),
                    Valid.SetTgl(TanggalExtubasi.getSelectedItem() + "") + " " + TanggalExtubasi.getSelectedItem().toString().substring(11, 19),
                    Valid.SetTgl(TglPencabutan.getSelectedItem() + "") + " " + TglPencabutan.getSelectedItem().toString().substring(11, 19),
                    TD.getText(), Nadi.getText(), RR.getText(), SPO.getText(), Mode.getText(), Fi02.getText(), Peep.getText(), iE.getText()
                });
                tampil();
                LCount.setText("" + tabMode.getRowCount());
                emptTeks();
            }
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            //    Valid.pindah(evt,BeratBadan,BtnBatal);
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
        if (Valid.hapusTabletf(tabMode, IndikatorPemasangan, "formulir_tindakan_ventilator", "no_rawat") == true) {
            if (tbObat.getSelectedRow() != -1) {
                tabMode.removeRow(tbObat.getSelectedRow());
                emptTeks();
                LCount.setText("" + tabMode.getRowCount());
            }
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
        if (IndikatorPemasangan.getText().trim().equals("")) {
            Valid.textKosong(IndikatorPemasangan, "No.Surat Sakit");
        } else if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "pasien");
        } else {
            if (tbObat.getSelectedRow() != -1) {
                if (Sequel.mengedittf("suratsakit", "no_surat=?", "no_surat=?,no_rawat=?,tanggalawal=?,tanggalakhir=?,lamasakit=?", 6, new String[]{
                    TNoRw.getText(), KdDokter.getText(), Diagnosa.getText(), IndikatorPemasangan.getText(), Valid.SetTgl(TanggalIntubasi.getSelectedItem() + "") + " " + TanggalIntubasi.getSelectedItem().toString().substring(11, 19),
                    Valid.SetTgl(TglPemasangan.getSelectedItem() + "") + " " + TglPemasangan.getSelectedItem().toString().substring(11, 19),
                    Valid.SetTgl(TanggalExtubasi.getSelectedItem() + "") + " " + TanggalExtubasi.getSelectedItem().toString().substring(11, 19),
                    Valid.SetTgl(TglPencabutan.getSelectedItem() + "") + " " + TglPencabutan.getSelectedItem().toString().substring(11, 19),
                    TD.getText(), Nadi.getText(), RR.getText(), SPO.getText(), Mode.getText(), Fi02.getText(), Peep.getText(), iE.getText(), tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
                }) == true) {
                    tbObat.setValueAt(TNoRw.getText(), tbObat.getSelectedRow(), 0);
                    tbObat.setValueAt(TNoRM.getText(), tbObat.getSelectedRow(), 1);
                    tbObat.setValueAt(TPasien.getText(), tbObat.getSelectedRow(), 2);
                    tbObat.setValueAt(TglLahir.getText(), tbObat.getSelectedRow(), 3);
                    tbObat.setValueAt(Jk.getText(), tbObat.getSelectedRow(), 4);
                    tbObat.setValueAt(KdDokter.getText(), tbObat.getSelectedRow(), 5);
                    tbObat.setValueAt(NmDokter.getText(), tbObat.getSelectedRow(), 6);
                    tbObat.setValueAt(Valid.SetTgl(TanggalIntubasi.getSelectedItem() + "") + " " + TanggalIntubasi.getSelectedItem().toString().substring(11, 19), tbObat.getSelectedRow(), 7); // Tanggal/Jam
                    tbObat.setValueAt(Diagnosa.getText(), tbObat.getSelectedRow(), 8);
                    tbObat.setValueAt(IndikatorPemasangan.getText(), tbObat.getSelectedRow(), 9);
                    tbObat.setValueAt(Valid.SetTgl(TanggalIntubasi.getSelectedItem() + "") + " " + TanggalIntubasi.getSelectedItem().toString().substring(11, 19), tbObat.getSelectedRow(), 10);
                    tbObat.setValueAt(Valid.SetTgl(TglPemasangan.getSelectedItem() + "") + " " + TglPemasangan.getSelectedItem().toString().substring(11, 19), tbObat.getSelectedRow(), 11);
                    tbObat.setValueAt(Valid.SetTgl(TanggalExtubasi.getSelectedItem() + "") + " " + TanggalExtubasi.getSelectedItem().toString().substring(11, 19), tbObat.getSelectedRow(), 12);
                    tbObat.setValueAt(Valid.SetTgl(TglPencabutan.getSelectedItem() + "") + " " + TglPencabutan.getSelectedItem().toString().substring(11, 19), tbObat.getSelectedRow(), 13);
                    tbObat.setValueAt(TD.getText(), tbObat.getSelectedRow(), 14);
                    tbObat.setValueAt(Nadi.getText(), tbObat.getSelectedRow(), 15);
                    tbObat.setValueAt(RR.getText(), tbObat.getSelectedRow(), 16);
                    tbObat.setValueAt(SPO.getText(), tbObat.getSelectedRow(), 17);
                    tbObat.setValueAt(Mode.getText(), tbObat.getSelectedRow(), 18);
                    tbObat.setValueAt(Fi02.getText(), tbObat.getSelectedRow(), 19);
                    tbObat.setValueAt(Peep.getText(), tbObat.getSelectedRow(), 20);
                    tbObat.setValueAt(iE.getText(), tbObat.getSelectedRow(), 21);

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
            tgl = " suratsakit.tanggalawal between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari.getSelectedItem() + "") + "' ";
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

    private void MnCetakActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnCetakActionPerformed
     if(TPasien.getText().trim().equals("")){
            JOptionPane.showMessageDialog(null,"Maaf, Silahkan anda pilih dulu pasien...!!!");
        }else{
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                Map<String, Object> param = new HashMap<>();
          //      param.put("hari",BeratBadan.getText());
//                param.put("TanggalAwal",TanggalAwal.getSelectedItem().toString());
//                param.put("TanggalAkhir",TanggalAkhir.getSelectedItem().toString());
                param.put("nosakit",IndikatorPemasangan.getText());
                param.put("namars",akses.getnamars());
                param.put("alamatrs",akses.getalamatrs());
                param.put("kotars",akses.getkabupatenrs());
                param.put("propinsirs",akses.getpropinsirs());
                param.put("kontakrs",akses.getkontakrs());
                param.put("emailrs",akses.getemailrs());  
                param.put("logo2", Sequel.cariGambar("select setting.logo2 from setting"));
                kodedokter=Sequel.cariIsi("select reg_periksa.kd_dokter from reg_periksa where reg_periksa.no_rawat=?",TNoRw.getText());
                namadokter=Sequel.cariIsi("select dokter.nm_dokter from dokter where dokter.kd_dokter=?",kodedokter);
                finger=Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",kodedokter);
                param.put("finger","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+namadokter+"\nID "+(finger.equals("")?kodedokter:finger)+"\n"+Sequel.cariIsi("select DATE_FORMAT(reg_periksa.tgl_registrasi,'%d-%m-%Y') from reg_periksa where reg_periksa.no_rawat=?",TNoRw.getText()));  
                param.put("penyakit",Sequel.cariIsi("select concat(diagnosa_pasien.kd_penyakit,' ',penyakit.nm_penyakit) from diagnosa_pasien inner join reg_periksa inner join penyakit "+
                    "on diagnosa_pasien.no_rawat=reg_periksa.no_rawat and diagnosa_pasien.kd_penyakit=penyakit.kd_penyakit "+
                    "where diagnosa_pasien.no_rawat=? and diagnosa_pasien.prioritas='1'",TNoRw.getText()));
                param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
                Valid.MyReportqry("rptFormulirTindakanIntubasiVentilator.jasper","report","::[ Surat Sakit ]::",
                              "select DATE_FORMAT(reg_periksa.tgl_registrasi,'%d-%m-%Y')as tgl_registrasi,perusahaan_pasien.nama_perusahaan,reg_periksa.no_rawat,dokter.nm_dokter,pasien.keluarga,pasien.namakeluarga,pasien.tgl_lahir,pasien.jk," +
                              " pasien.nm_pasien,pasien.jk,concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur)as umur,pasien.pekerjaan,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab) as alamat" +
                              " from reg_periksa inner join pasien inner join dokter inner join kelurahan inner join perusahaan_pasien inner join kecamatan inner join kabupaten" +
                              " on reg_periksa.no_rkm_medis=pasien.no_rkm_medis and reg_periksa.kd_dokter=dokter.kd_dokter and pasien.kd_kel=kelurahan.kd_kel "+
                              "and pasien.perusahaan_pasien=perusahaan_pasien.kode_perusahaan and pasien.kd_kec=kecamatan.kd_kec and pasien.kd_kab=kabupaten.kd_kab "+
                              "where reg_periksa.no_rawat='"+TNoRw.getText()+"' ",param);
                this.setCursor(Cursor.getDefaultCursor());  
       } 
    }//GEN-LAST:event_MnCetakActionPerformed

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            isRawat();
        } else {
            Valid.pindah(evt, TCari, BtnDokter);
        }
    }//GEN-LAST:event_TNoRwKeyPressed

    private void KdDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdDokterKeyPressed

    }//GEN-LAST:event_KdDokterKeyPressed

    private void BtnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokterActionPerformed
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setAlwaysOnTop(false);
        dokter.setVisible(true);
    }//GEN-LAST:event_BtnDokterActionPerformed

    private void BtnDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnDokterKeyPressed
        Valid.pindah(evt, TCari, BtnSimpan);
    }//GEN-LAST:event_BtnDokterKeyPressed

    private void PeepKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PeepKeyPressed
        Valid.pindah(evt, Fi02, TD);
    }//GEN-LAST:event_PeepKeyPressed

    private void Fi02KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Fi02KeyPressed
        Valid.pindah(evt, Mode, Peep);
    }//GEN-LAST:event_Fi02KeyPressed

    private void NadiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NadiKeyPressed
        Valid.pindah(evt, TD, RR);
    }//GEN-LAST:event_NadiKeyPressed

    private void iEKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_iEKeyPressed
        Valid.pindah(evt, RR, SPO);
    }//GEN-LAST:event_iEKeyPressed

    private void TDKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TDKeyPressed
        Valid.pindah(evt, Peep, Nadi);
    }//GEN-LAST:event_TDKeyPressed

    private void RRKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RRKeyPressed
        Valid.pindah(evt, Nadi, iE);
    }//GEN-LAST:event_RRKeyPressed

    private void IndikatorPemasanganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_IndikatorPemasanganKeyPressed
        //   Valid.pindah(evt,RPO,Keadaan);
    }//GEN-LAST:event_IndikatorPemasanganKeyPressed

    private void ModeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ModeKeyPressed
        //    Valid.pindah(evt,Kesadaran,TB);
    }//GEN-LAST:event_ModeKeyPressed

    private void DiagnosaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosaKeyPressed
        //    Valid.pindah(evt,Anamnesis,KeluhanUtama);
    }//GEN-LAST:event_DiagnosaKeyPressed

    private void SPOKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SPOKeyPressed
        //    Valid.pindah(evt,Suhu,Kepala);
    }//GEN-LAST:event_SPOKeyPressed

    private void TglAsuhanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TglAsuhanKeyPressed
        //   Valid.pindah(evt,Tatalaksana,Anamnesis);
    }//GEN-LAST:event_TglAsuhanKeyPressed

    private void TanggalIntubasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalIntubasiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TanggalIntubasiKeyPressed

    private void TglPemasanganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TglPemasanganKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TglPemasanganKeyPressed

    private void TanggalExtubasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalExtubasiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TanggalExtubasiKeyPressed

    private void TglPencabutanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TglPencabutanKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TglPencabutanKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMFormulirTindakanIntubasiVentilatorRscb dialog = new RMFormulirTindakanIntubasiVentilatorRscb(new javax.swing.JFrame(), true);
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
    private widget.Button BtnDokter;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkInput;
    private widget.Tanggal DTPCari;
    private widget.Tanggal DTPCari1;
    private widget.TextBox Diagnosa;
    private widget.TextBox Fi02;
    private widget.PanelBiasa FormInput;
    private widget.TextBox IndikatorPemasangan;
    private widget.TextBox Jk;
    private widget.TextBox KdDokter;
    private widget.Label LCount;
    private javax.swing.JMenuItem MnCetak;
    private widget.TextBox Mode;
    private widget.TextBox Nadi;
    private widget.TextBox NmDokter;
    private javax.swing.JPanel PanelInput;
    private widget.TextBox Peep;
    private widget.TextBox RR;
    private widget.TextBox SPO;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.TextBox TD;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Tanggal TanggalExtubasi;
    private widget.Tanggal TanggalIntubasi;
    private widget.Tanggal TglAsuhan;
    private widget.TextBox TglLahir;
    private widget.Tanggal TglPemasangan;
    private widget.Tanggal TglPencabutan;
    private widget.TextBox iE;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel13;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel20;
    private widget.Label jLabel21;
    private widget.Label jLabel22;
    private widget.Label jLabel23;
    private widget.Label jLabel25;
    private widget.Label jLabel26;
    private widget.Label jLabel28;
    private widget.Label jLabel29;
    private widget.Label jLabel35;
    private widget.Label jLabel37;
    private widget.Label jLabel38;
    private widget.Label jLabel41;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel94;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JSeparator jSeparator1;
    private widget.Label label11;
    private widget.Label label12;
    private widget.Label label13;
    private widget.Label label14;
    private widget.Label label15;
    private widget.Label label16;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            tgl = "formulir_tindakan_ventilator.tanggalpemasangan between '" + Valid.SetTglJam(DTPCari.getSelectedItem() + "") + "' and '" + Valid.SetTglJam(DTPCari1.getSelectedItem() + "") + "' ";
            if (TCari.getText().trim().equals("")) {
                ps = koneksi.prepareStatement(
                        "select formulir_tindakan_ventilator.no_rawat,formulir_tindakan_ventilator.tanggalpemasangan,formulir_tindakan_ventilator.beratbadan,formulir_tindakan_ventilator.indikatorpemasangan,"
                        + "formulir_tindakan_ventilator.tanggalpencabutan,formulir_tindakan_ventilator.indikatordokter,formulir_tindakan_ventilator.kd_dokter,reg_periksa.no_rkm_medis,pasien.nm_pasien, "
                        + "dokter.nm_dokter from formulir_tindakan_ventilator inner join reg_periksa inner join pasien inner join dokter on formulir_tindakan_ventilator.no_rawat=reg_periksa.no_rawat and reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "and formulir_tindakan_ventilator.kd_dokter=dokter.kd_dokter where '" + tgl + "' ");

            } else {
                ps = koneksi.prepareStatement(
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
                        + "order by suratsakit.no_surat");
            }

            try {
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("tanggalpemasangan"), rs.getString("beratbadan"), rs.getString("indikatorpemasangan"),
                        rs.getString("tanggalpencabutan"), rs.getString("indikatordokter"), rs.getString("kd_dokter"),
                        rs.getString("nm_dokter")
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
        KdDokter.setText("");
        NmDokter.setText("");
        TglLahir.setText("");
        Jk.setText("");
        Diagnosa.setText("");
        IndikatorPemasangan.setText("");
        TD.setText("");
        Nadi.setText("");
        RR.setText("");
        Mode.setText("");
        Fi02.setText("");
        Peep.setText("");
        iE.setText("");

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
            // "Tanggal/Jam" biasanya ditampilkan di komponen tersendiri jika ada, bisa ditambahkan kalau perlu
            Diagnosa.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString());
            IndikatorPemasangan.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString());

            // Tanggal Intubasi
            Valid.SetTgl(TanggalIntubasi, tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString().substring(0, 10));
            // Tanggal Pemasangan Ventilator
            Valid.SetTgl(TglPemasangan, tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString().substring(0, 10));
            // Tanggal Extubasi
            Valid.SetTgl(TanggalExtubasi, tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString().substring(0, 10));
            // Tanggal Lepas Ventilator
            Valid.SetTgl(TglPencabutan, tbObat.getValueAt(tbObat.getSelectedRow(), 13).toString().substring(0, 10));

            TD.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 14).toString());
            Nadi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 15).toString());
            RR.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 16).toString());
            SPO.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 17).toString());
            Mode.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 18).toString());
            Fi02.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 19).toString());
            Peep.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 20).toString());
            iE.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 21).toString());
        }
    }

    private void isRawat() {
        Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat='" + TNoRw.getText() + "' ", TNoRM);
    }

    private void isPsien() {
        Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis='" + TNoRM.getText() + "' ", TPasien);
    }

    public void setNoRm(String norwt, Date tgl1) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        DTPCari1.setDate(tgl1);
        //   DTPCari.setDate(tgl2);
        isRawat();
        isPsien();
        ChkInput.setSelected(true);
        isForm();
    }

    private void isForm() {
        if (ChkInput.isSelected() == true) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 300));
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
