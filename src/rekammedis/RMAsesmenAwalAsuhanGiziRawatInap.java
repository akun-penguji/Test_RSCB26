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
import kepegawaian.DlgCariPetugas;

/**
 *
 * @author perpustakaan
 */
public final class RMAsesmenAwalAsuhanGiziRawatInap extends javax.swing.JDialog {

    private final DefaultTableModel tabMode, tabModeBiokimia;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0, umur = 0;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);
    private String sttsumur = "", finger = "";

    /**
     * Creates new form DlgRujuk
     *
     * @param parent
     * @param modal
     */
    public RMAsesmenAwalAsuhanGiziRawatInap(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        DlgBiokimia.setSize(650, 192);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "J.K.", "Tgl.Lahir", "Tgl.Asuhan", "Diagnosa Medis", "Riwayat Personal", "BB", "TB", "IMT", "LILA",
            "LILA/U", "BB/U", "TB/U", "BB/TB", "IMT/U", "Status Gizi", "Kesan Umum", "TD", "Respirasi", "Nadi", "Suhu", "Masalah Gastrointestinal",
            "Alergi Makan", "Pola Makan", "Diagnosa Gizi", "Tujuan Diet", "Jenis Diet", "Energi", "Karbohidrat", "Lemak", "Protein", "Bentuk Makanan",
            "Cara Pemberian", "Monitoring", "Kode Petugas", "Nama Petugas", "Implementasi Gizi", "Evaluasi"
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

        for (i = 0; i < 38; i++) { // Sesuaikan dengan jumlah kolom sebenarnya
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(105);
            } else if (i == 1) {
                column.setPreferredWidth(65);
            } else if (i == 2) {
                column.setPreferredWidth(150);
            } else if (i == 3) {
                column.setPreferredWidth(25);
            } else if (i == 4) {
                column.setPreferredWidth(65);
            } else if (i == 5) {
                column.setPreferredWidth(65);
            } else if (i == 6) {
                column.setPreferredWidth(100); // Sesuaikan jika perlu
            } else if (i == 7) {
                column.setPreferredWidth(100); // Sesuaikan jika perlu
            } else if (i == 8) {
                column.setPreferredWidth(65);
            } else if (i == 9) {
                column.setPreferredWidth(52);
            } else if (i == 10) {
                column.setPreferredWidth(43);
            } else if (i == 11) {
                column.setPreferredWidth(58);
            } else if (i == 12) {
                column.setPreferredWidth(68);
            } else if (i == 13) {
                column.setPreferredWidth(50);
            } else if (i == 14) {
                column.setPreferredWidth(50);
            } else if (i == 15) {
                column.setPreferredWidth(55);
            } else if (i == 16) {
                column.setPreferredWidth(58);
            } else if (i == 17) {
                column.setPreferredWidth(180);
            } else if (i == 18) {
                column.setPreferredWidth(180);
            } else if (i == 19) {
                column.setPreferredWidth(38);
            } else if (i == 20) {
                column.setPreferredWidth(55);
            } else if (i == 21) {
                column.setPreferredWidth(44);
            } else if (i == 22) {
                column.setPreferredWidth(42);
            } else if (i == 23) {
                column.setPreferredWidth(42);
            } else if (i == 24) {
                column.setPreferredWidth(38);
            } else if (i == 25) {
                column.setPreferredWidth(53);
            } else if (i == 26) {
                column.setPreferredWidth(180);
            } else if (i == 27) {
                column.setPreferredWidth(180);
            } else if (i == 28) {
                column.setPreferredWidth(180);
            } else if (i == 29) {
                column.setPreferredWidth(180);
            } else if (i == 30) {
                column.setPreferredWidth(180);
            } else if (i == 31) {
                column.setPreferredWidth(80);
            } else if (i == 32) {
                column.setPreferredWidth(150);
            } else if (i == 33) {
                column.setPreferredWidth(150); // Tambahkan untuk kolom yang belum ada
            } else if (i == 34) {
                column.setPreferredWidth(150);
            } else if (i == 35) {
                column.setPreferredWidth(150);
            } else if (i == 36) {
                column.setPreferredWidth(150);
            } else if (i == 37) {
                column.setPreferredWidth(150);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        tabModeBiokimia = new DefaultTableModel(null, new Object[]{
            "No", "Tgl/Tahun", "Nama Pemeriksaan", "Nilai", "Interpretasi"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbBiokimia.setModel(tabModeBiokimia);

        tbBiokimia.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbBiokimia.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 5; i++) {
            TableColumn column = tbBiokimia.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(30);
            } else if (i == 1) {
                column.setPreferredWidth(90);
            } else if (i == 2) {
                column.setPreferredWidth(260);
            } else if (i == 3) {
                column.setPreferredWidth(100);
            } else if (i == 4) {
                column.setPreferredWidth(260);
            }
        }
        tbBiokimia.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        BB.setDocument(new batasInput((byte) 5).getKata(BB));
        TB.setDocument(new batasInput((byte) 5).getKata(TB));
        IMT.setDocument(new batasInput((byte) 5).getKata(IMT));
        LiLA.setDocument(new batasInput((byte) 5).getKata(LiLA));
        ImtU.setDocument(new batasInput((byte) 5).getKata(ImtU));
        BBPerU.setDocument(new batasInput((byte) 5).getKata(BBPerU));
        TBPerU.setDocument(new batasInput((byte) 5).getKata(TBPerU));
        BBPerTB.setDocument(new batasInput((byte) 5).getKata(BBPerTB));
        LiLAPerU.setDocument(new batasInput((byte) 5).getKata(LiLAPerU));
        Personal.setDocument(new batasInput((int) 1000).getKata(Personal));
        KesanUmum.setDocument(new batasInput((int) 100).getKata(KesanUmum));
        StatusGizi.setDocument(new batasInput((int) 100).getKata(StatusGizi));
        MasalahGastro.setDocument(new batasInput((int) 1000).getKata(MasalahGastro));

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

        petugas.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (petugas.getTable().getSelectedRow() != -1) {
                    KdPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                    NmPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
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

        BB.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                isBMI();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                isBMI();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                isBMI();
            }
        });

        TB.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                isBMI();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                isBMI();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                isBMI();
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
        MnAsuhanGizi = new javax.swing.JMenuItem();
        DlgBiokimia = new javax.swing.JDialog();
        internalFrame4 = new widget.InternalFrame();
        panelBiasa2 = new widget.PanelBiasa();
        TanggalPemeriksaan = new widget.Tanggal();
        jLabel99 = new widget.Label();
        BtnKeluarKehamilan = new widget.Button();
        BtnSimpanRiwayatKehamilan = new widget.Button();
        jLabel107 = new widget.Label();
        NamaPemeriksaan = new widget.TextBox();
        jLabel105 = new widget.Label();
        Nilai = new widget.TextBox();
        jLabel109 = new widget.Label();
        Interpretasi = new widget.TextBox();
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
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        TNoRM = new widget.TextBox();
        jLabel5 = new widget.Label();
        label14 = new widget.Label();
        KdPetugas = new widget.TextBox();
        NmPetugas = new widget.TextBox();
        BtnDokter = new widget.Button();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        jLabel9 = new widget.Label();
        Jk = new widget.TextBox();
        jLabel10 = new widget.Label();
        DiagnosaMasukRanap = new widget.TextBox();
        label11 = new widget.Label();
        TglAsuhan = new widget.Tanggal();
        jLabel11 = new widget.Label();
        jLabel12 = new widget.Label();
        BB = new widget.TextBox();
        jLabel13 = new widget.Label();
        TB = new widget.TextBox();
        jLabel15 = new widget.Label();
        jLabel16 = new widget.Label();
        IMT = new widget.TextBox();
        jLabel17 = new widget.Label();
        jLabel18 = new widget.Label();
        LiLA = new widget.TextBox();
        jLabel22 = new widget.Label();
        ImtU = new widget.TextBox();
        jLabel20 = new widget.Label();
        jLabel23 = new widget.Label();
        jLabel24 = new widget.Label();
        jLabel28 = new widget.Label();
        BBPerU = new widget.TextBox();
        jLabel29 = new widget.Label();
        jLabel30 = new widget.Label();
        TBPerU = new widget.TextBox();
        jLabel31 = new widget.Label();
        jLabel32 = new widget.Label();
        BBPerTB = new widget.TextBox();
        jLabel33 = new widget.Label();
        jLabel34 = new widget.Label();
        LiLAPerU = new widget.TextBox();
        jLabel35 = new widget.Label();
        jLabel36 = new widget.Label();
        StatusGizi = new widget.TextBox();
        jLabel38 = new widget.Label();
        jLabel40 = new widget.Label();
        jLabel43 = new widget.Label();
        jLabel44 = new widget.Label();
        jLabel50 = new widget.Label();
        KesanUmum = new widget.TextBox();
        scrollPane2 = new widget.ScrollPane();
        Personal = new widget.TextArea();
        scrollPane5 = new widget.ScrollPane();
        MasalahGastro = new widget.TextArea();
        jLabel52 = new widget.Label();
        Td = new widget.TextBox();
        jLabel53 = new widget.Label();
        jLabel54 = new widget.Label();
        Rr = new widget.TextBox();
        jLabel55 = new widget.Label();
        jLabel56 = new widget.Label();
        Nadi = new widget.TextBox();
        jLabel57 = new widget.Label();
        jLabel58 = new widget.Label();
        Suhu = new widget.TextBox();
        jLabel59 = new widget.Label();
        Scroll6 = new widget.ScrollPane();
        tbBiokimia = new widget.Table();
        BtnTambahMasalah = new widget.Button();
        BtnHapusRiwayatPersalinan = new widget.Button();
        jLabel45 = new widget.Label();
        jLabel46 = new widget.Label();
        jLabel39 = new widget.Label();
        Alergi = new widget.TextBox();
        jLabel47 = new widget.Label();
        Pola = new widget.TextBox();
        scrollPane6 = new widget.ScrollPane();
        DiagnosisGizi = new widget.TextArea();
        jLabel48 = new widget.Label();
        jLabel49 = new widget.Label();
        jLabel51 = new widget.Label();
        jLabel60 = new widget.Label();
        JenisDiet = new widget.TextBox();
        TujuanDiet = new widget.TextBox();
        jLabel61 = new widget.Label();
        jLabel62 = new widget.Label();
        jLabel63 = new widget.Label();
        Karbohidrat = new widget.TextBox();
        Energi = new widget.TextBox();
        jLabel64 = new widget.Label();
        jLabel65 = new widget.Label();
        Lemak = new widget.TextBox();
        Protein = new widget.TextBox();
        jLabel66 = new widget.Label();
        jLabel67 = new widget.Label();
        Cara = new widget.TextBox();
        Bentuk = new widget.TextBox();
        jLabel68 = new widget.Label();
        scrollPane7 = new widget.ScrollPane();
        Monitoring = new widget.TextArea();
        jLabelEval = new widget.Label();
        scrollPaneEval = new widget.ScrollPane();
        Evaluasi = new widget.TextArea();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnAsuhanGizi.setBackground(new java.awt.Color(255, 255, 254));
        MnAsuhanGizi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnAsuhanGizi.setForeground(new java.awt.Color(50, 50, 50));
        MnAsuhanGizi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnAsuhanGizi.setText("Cetak");
        MnAsuhanGizi.setActionCommand("Cetak");
        MnAsuhanGizi.setName("MnAsuhanGizi"); // NOI18N
        MnAsuhanGizi.setPreferredSize(new java.awt.Dimension(220, 26));
        MnAsuhanGizi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnAsuhanGiziActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnAsuhanGizi);

        DlgBiokimia.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        DlgBiokimia.setName("DlgBiokimia"); // NOI18N
        DlgBiokimia.setUndecorated(true);
        DlgBiokimia.setResizable(false);

        internalFrame4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(230, 235, 225)), "::[ Pemeriksaan Biokimia ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 70, 50))); // NOI18N
        internalFrame4.setName("internalFrame4"); // NOI18N
        internalFrame4.setLayout(new java.awt.BorderLayout(1, 1));

        panelBiasa2.setName("panelBiasa2"); // NOI18N
        panelBiasa2.setLayout(null);

        TanggalPemeriksaan.setForeground(new java.awt.Color(50, 70, 50));
        TanggalPemeriksaan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "03-03-2025" }));
        TanggalPemeriksaan.setDisplayFormat("dd-MM-yyyy");
        TanggalPemeriksaan.setName("TanggalPemeriksaan"); // NOI18N
        TanggalPemeriksaan.setOpaque(false);
        panelBiasa2.add(TanggalPemeriksaan);
        TanggalPemeriksaan.setBounds(120, 10, 95, 23);

        jLabel99.setText("Nama Pemeriksaan:");
        jLabel99.setName("jLabel99"); // NOI18N
        panelBiasa2.add(jLabel99);
        jLabel99.setBounds(0, 40, 110, 23);

        BtnKeluarKehamilan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/cross.png"))); // NOI18N
        BtnKeluarKehamilan.setMnemonic('U');
        BtnKeluarKehamilan.setText("Tutup");
        BtnKeluarKehamilan.setToolTipText("Alt+U");
        BtnKeluarKehamilan.setName("BtnKeluarKehamilan"); // NOI18N
        BtnKeluarKehamilan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluarKehamilan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKeluarKehamilanActionPerformed(evt);
            }
        });
        panelBiasa2.add(BtnKeluarKehamilan);
        BtnKeluarKehamilan.setBounds(220, 130, 100, 30);

        BtnSimpanRiwayatKehamilan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpanRiwayatKehamilan.setMnemonic('S');
        BtnSimpanRiwayatKehamilan.setText("Simpan");
        BtnSimpanRiwayatKehamilan.setToolTipText("Alt+S");
        BtnSimpanRiwayatKehamilan.setName("BtnSimpanRiwayatKehamilan"); // NOI18N
        BtnSimpanRiwayatKehamilan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanRiwayatKehamilanActionPerformed(evt);
            }
        });
        panelBiasa2.add(BtnSimpanRiwayatKehamilan);
        BtnSimpanRiwayatKehamilan.setBounds(110, 130, 100, 30);

        jLabel107.setText("Tanggal/Tahun :");
        jLabel107.setName("jLabel107"); // NOI18N
        panelBiasa2.add(jLabel107);
        jLabel107.setBounds(10, 10, 100, 23);

        NamaPemeriksaan.setHighlighter(null);
        NamaPemeriksaan.setName("NamaPemeriksaan"); // NOI18N
        NamaPemeriksaan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NamaPemeriksaanKeyPressed(evt);
            }
        });
        panelBiasa2.add(NamaPemeriksaan);
        NamaPemeriksaan.setBounds(120, 40, 290, 23);

        jLabel105.setText("Nilai:");
        jLabel105.setName("jLabel105"); // NOI18N
        panelBiasa2.add(jLabel105);
        jLabel105.setBounds(0, 70, 110, 23);

        Nilai.setHighlighter(null);
        Nilai.setName("Nilai"); // NOI18N
        Nilai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NilaiKeyPressed(evt);
            }
        });
        panelBiasa2.add(Nilai);
        Nilai.setBounds(120, 70, 290, 23);

        jLabel109.setText("Interpretasi:");
        jLabel109.setName("jLabel109"); // NOI18N
        panelBiasa2.add(jLabel109);
        jLabel109.setBounds(0, 100, 110, 23);

        Interpretasi.setHighlighter(null);
        Interpretasi.setName("Interpretasi"); // NOI18N
        Interpretasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                InterpretasiKeyPressed(evt);
            }
        });
        panelBiasa2.add(Interpretasi);
        Interpretasi.setBounds(120, 100, 290, 23);

        internalFrame4.add(panelBiasa2, java.awt.BorderLayout.CENTER);

        DlgBiokimia.getContentPane().add(internalFrame4, java.awt.BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data ASSESSMENT AWAL ASUHAN GIZI RAWAT INAP ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
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

        jLabel19.setText("Tgl.Rawat :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(67, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "03-03-2025" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "03-03-2025" }));
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
        scrollInput.setPreferredSize(new java.awt.Dimension(102, 900));

        FormInput.setBackground(new java.awt.Color(250, 255, 245));
        FormInput.setBorder(null);
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 1160));
        FormInput.setLayout(null);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(74, 10, 141, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        FormInput.add(TPasien);
        TPasien.setBounds(331, 10, 280, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        FormInput.add(TNoRM);
        TNoRM.setBounds(217, 10, 112, 23);

        jLabel5.setText("Diagnosa medis:");
        jLabel5.setName("jLabel5"); // NOI18N
        FormInput.add(jLabel5);
        jLabel5.setBounds(40, 90, 90, 23);

        label14.setText("Petugas :");
        label14.setName("label14"); // NOI18N
        label14.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label14);
        label14.setBounds(-20, 980, 130, 23);

        KdPetugas.setEditable(false);
        KdPetugas.setName("KdPetugas"); // NOI18N
        KdPetugas.setPreferredSize(new java.awt.Dimension(80, 23));
        KdPetugas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KdPetugasKeyPressed(evt);
            }
        });
        FormInput.add(KdPetugas);
        KdPetugas.setBounds(120, 980, 130, 23);

        NmPetugas.setEditable(false);
        NmPetugas.setName("NmPetugas"); // NOI18N
        NmPetugas.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(NmPetugas);
        NmPetugas.setBounds(260, 980, 300, 23);

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
        BtnDokter.setBounds(570, 980, 28, 23);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(625, 10, 60, 23);

        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir"); // NOI18N
        FormInput.add(TglLahir);
        TglLahir.setBounds(689, 10, 90, 23);

        jLabel9.setText("Riwayat Personal:");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(0, 130, 130, 23);

        Jk.setHighlighter(null);
        Jk.setName("Jk"); // NOI18N
        FormInput.add(Jk);
        Jk.setBounds(74, 40, 100, 23);

        jLabel10.setText("No.Rawat :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(0, 10, 70, 23);

        DiagnosaMasukRanap.setHighlighter(null);
        DiagnosaMasukRanap.setName("DiagnosaMasukRanap"); // NOI18N
        FormInput.add(DiagnosaMasukRanap);
        DiagnosaMasukRanap.setBounds(130, 90, 337, 23);

        label11.setText("Tanggal :");
        label11.setName("label11"); // NOI18N
        label11.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label11);
        label11.setBounds(180, 40, 60, 23);

        TglAsuhan.setDisplayFormat("dd-MM-yyyy");
        TglAsuhan.setName("TglAsuhan"); // NOI18N
        TglAsuhan.setPreferredSize(new java.awt.Dimension(95, 23));
        TglAsuhan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TglAsuhanKeyPressed(evt);
            }
        });
        FormInput.add(TglAsuhan);
        TglAsuhan.setBounds(240, 40, 90, 23);

        jLabel11.setText("J.K. :");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(0, 40, 70, 23);

        jLabel12.setText("BB :");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(-80, 220, 160, 23);

        BB.setFocusTraversalPolicyProvider(true);
        BB.setName("BB"); // NOI18N
        BB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BBKeyPressed(evt);
            }
        });
        FormInput.add(BB);
        BB.setBounds(80, 220, 60, 23);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel13.setText("Kg");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(150, 220, 30, 23);

        TB.setFocusTraversalPolicyProvider(true);
        TB.setName("TB"); // NOI18N
        TB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TBKeyPressed(evt);
            }
        });
        FormInput.add(TB);
        TB.setBounds(220, 220, 60, 23);

        jLabel15.setText("TB :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(170, 220, 50, 23);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("kg/m2");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(460, 220, 40, 23);

        IMT.setFocusTraversalPolicyProvider(true);
        IMT.setName("IMT"); // NOI18N
        IMT.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                IMTKeyPressed(evt);
            }
        });
        FormInput.add(IMT);
        IMT.setBounds(390, 220, 60, 23);

        jLabel17.setText("IMT :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(340, 220, 40, 23);

        jLabel18.setText("LILA:");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(480, 220, 40, 23);

        LiLA.setFocusTraversalPolicyProvider(true);
        LiLA.setName("LiLA"); // NOI18N
        LiLA.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LiLAKeyPressed(evt);
            }
        });
        FormInput.add(LiLA);
        LiLA.setBounds(530, 220, 60, 23);

        jLabel22.setText("IMT/ U");
        jLabel22.setName("jLabel22"); // NOI18N
        FormInput.add(jLabel22);
        jLabel22.setBounds(480, 250, 40, 23);

        ImtU.setFocusTraversalPolicyProvider(true);
        ImtU.setName("ImtU"); // NOI18N
        ImtU.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ImtUKeyPressed(evt);
            }
        });
        FormInput.add(ImtU);
        ImtU.setBounds(530, 250, 60, 23);

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel20.setText("Cm");
        jLabel20.setName("jLabel20"); // NOI18N
        FormInput.add(jLabel20);
        jLabel20.setBounds(600, 220, 30, 23);

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("SD");
        jLabel23.setName("jLabel23"); // NOI18N
        FormInput.add(jLabel23);
        jLabel23.setBounds(600, 250, 30, 23);

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setText("Cm");
        jLabel24.setName("jLabel24"); // NOI18N
        FormInput.add(jLabel24);
        jLabel24.setBounds(290, 220, 30, 23);

        jLabel28.setText("BB/U :");
        jLabel28.setName("jLabel28"); // NOI18N
        FormInput.add(jLabel28);
        jLabel28.setBounds(30, 250, 40, 23);

        BBPerU.setFocusTraversalPolicyProvider(true);
        BBPerU.setName("BBPerU"); // NOI18N
        BBPerU.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BBPerUKeyPressed(evt);
            }
        });
        FormInput.add(BBPerU);
        BBPerU.setBounds(80, 250, 60, 23);

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel29.setText("SD");
        jLabel29.setName("jLabel29"); // NOI18N
        FormInput.add(jLabel29);
        jLabel29.setBounds(150, 250, 30, 23);

        jLabel30.setText("TB/U :");
        jLabel30.setName("jLabel30"); // NOI18N
        FormInput.add(jLabel30);
        jLabel30.setBounds(60, 250, 160, 23);

        TBPerU.setFocusTraversalPolicyProvider(true);
        TBPerU.setName("TBPerU"); // NOI18N
        TBPerU.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TBPerUKeyPressed(evt);
            }
        });
        FormInput.add(TBPerU);
        TBPerU.setBounds(220, 250, 60, 23);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel31.setText("SD");
        jLabel31.setName("jLabel31"); // NOI18N
        FormInput.add(jLabel31);
        jLabel31.setBounds(290, 250, 30, 23);

        jLabel32.setText("BB/TB :");
        jLabel32.setName("jLabel32"); // NOI18N
        FormInput.add(jLabel32);
        jLabel32.setBounds(340, 250, 50, 23);

        BBPerTB.setFocusTraversalPolicyProvider(true);
        BBPerTB.setName("BBPerTB"); // NOI18N
        BBPerTB.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BBPerTBKeyPressed(evt);
            }
        });
        FormInput.add(BBPerTB);
        BBPerTB.setBounds(390, 250, 60, 23);

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel33.setText("SD");
        jLabel33.setName("jLabel33"); // NOI18N
        FormInput.add(jLabel33);
        jLabel33.setBounds(460, 250, 30, 23);

        jLabel34.setText("LiLA/U :");
        jLabel34.setName("jLabel34"); // NOI18N
        FormInput.add(jLabel34);
        jLabel34.setBounds(620, 220, 60, 23);

        LiLAPerU.setFocusTraversalPolicyProvider(true);
        LiLAPerU.setName("LiLAPerU"); // NOI18N
        LiLAPerU.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LiLAPerUKeyPressed(evt);
            }
        });
        FormInput.add(LiLAPerU);
        LiLAPerU.setBounds(690, 220, 60, 23);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel35.setText("SD");
        jLabel35.setName("jLabel35"); // NOI18N
        FormInput.add(jLabel35);
        jLabel35.setBounds(760, 220, 30, 23);

        jLabel36.setText("ANAMNESIS:");
        jLabel36.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel36.setName("jLabel36"); // NOI18N
        FormInput.add(jLabel36);
        jLabel36.setBounds(10, 70, 80, 23);

        StatusGizi.setFocusTraversalPolicyProvider(true);
        StatusGizi.setName("StatusGizi"); // NOI18N
        StatusGizi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                StatusGiziKeyPressed(evt);
            }
        });
        FormInput.add(StatusGizi);
        StatusGizi.setBounds(120, 280, 590, 23);

        jLabel38.setText("Kesan Umum:");
        jLabel38.setName("jLabel38"); // NOI18N
        FormInput.add(jLabel38);
        jLabel38.setBounds(-10, 330, 130, 23);

        jLabel40.setText("Riwayat Makan:");
        jLabel40.setName("jLabel40"); // NOI18N
        FormInput.add(jLabel40);
        jLabel40.setBounds(10, 590, 90, 23);

        jLabel43.setText("ANTROPOMETRI");
        jLabel43.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel43.setName("jLabel43"); // NOI18N
        FormInput.add(jLabel43);
        jLabel43.setBounds(0, 200, 150, 23);

        jLabel44.setText("Status gizi:");
        jLabel44.setName("jLabel44"); // NOI18N
        FormInput.add(jLabel44);
        jLabel44.setBounds(40, 280, 70, 23);

        jLabel50.setText("FISIK KLINIS");
        jLabel50.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel50.setName("jLabel50"); // NOI18N
        FormInput.add(jLabel50);
        jLabel50.setBounds(40, 310, 80, 23);

        KesanUmum.setFocusTraversalPolicyProvider(true);
        KesanUmum.setName("KesanUmum"); // NOI18N
        KesanUmum.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KesanUmumKeyPressed(evt);
            }
        });
        FormInput.add(KesanUmum);
        KesanUmum.setBounds(120, 330, 270, 23);

        scrollPane2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane2.setName("scrollPane2"); // NOI18N

        Personal.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Personal.setColumns(20);
        Personal.setRows(5);
        Personal.setName("Personal"); // NOI18N
        Personal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PersonalKeyPressed(evt);
            }
        });
        scrollPane2.setViewportView(Personal);

        FormInput.add(scrollPane2);
        scrollPane2.setBounds(130, 130, 645, 63);

        scrollPane5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane5.setName("scrollPane5"); // NOI18N

        MasalahGastro.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        MasalahGastro.setColumns(20);
        MasalahGastro.setRows(5);
        MasalahGastro.setName("MasalahGastro"); // NOI18N
        MasalahGastro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MasalahGastroKeyPressed(evt);
            }
        });
        scrollPane5.setViewportView(MasalahGastro);

        FormInput.add(scrollPane5);
        scrollPane5.setBounds(130, 410, 645, 63);

        jLabel52.setText("TD:");
        jLabel52.setName("jLabel52"); // NOI18N
        FormInput.add(jLabel52);
        jLabel52.setBounds(-90, 370, 150, 23);

        Td.setFocusTraversalPolicyProvider(true);
        Td.setName("Td"); // NOI18N
        Td.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TdKeyPressed(evt);
            }
        });
        FormInput.add(Td);
        Td.setBounds(70, 370, 60, 23);

        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel53.setText("mmHg");
        jLabel53.setName("jLabel53"); // NOI18N
        FormInput.add(jLabel53);
        jLabel53.setBounds(140, 370, 40, 23);

        jLabel54.setText("Respirasi:");
        jLabel54.setName("jLabel54"); // NOI18N
        FormInput.add(jLabel54);
        jLabel54.setBounds(190, 370, 50, 23);

        Rr.setFocusTraversalPolicyProvider(true);
        Rr.setName("Rr"); // NOI18N
        Rr.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RrKeyPressed(evt);
            }
        });
        FormInput.add(Rr);
        Rr.setBounds(240, 370, 60, 23);

        jLabel55.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel55.setText("x/menit");
        jLabel55.setName("jLabel55"); // NOI18N
        FormInput.add(jLabel55);
        jLabel55.setBounds(300, 370, 50, 23);

        jLabel56.setText("Nadi:");
        jLabel56.setName("jLabel56"); // NOI18N
        FormInput.add(jLabel56);
        jLabel56.setBounds(320, 370, 60, 23);

        Nadi.setFocusTraversalPolicyProvider(true);
        Nadi.setName("Nadi"); // NOI18N
        Nadi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NadiKeyPressed(evt);
            }
        });
        FormInput.add(Nadi);
        Nadi.setBounds(390, 370, 60, 23);

        jLabel57.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel57.setText("x/mnt");
        jLabel57.setName("jLabel57"); // NOI18N
        FormInput.add(jLabel57);
        jLabel57.setBounds(460, 370, 50, 23);

        jLabel58.setText("Suhu:");
        jLabel58.setName("jLabel58"); // NOI18N
        FormInput.add(jLabel58);
        jLabel58.setBounds(510, 370, 40, 23);

        Suhu.setFocusTraversalPolicyProvider(true);
        Suhu.setName("Suhu"); // NOI18N
        Suhu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SuhuKeyPressed(evt);
            }
        });
        FormInput.add(Suhu);
        Suhu.setBounds(560, 370, 60, 23);

        jLabel59.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel59.setText("C");
        jLabel59.setName("jLabel59"); // NOI18N
        FormInput.add(jLabel59);
        jLabel59.setBounds(630, 370, 30, 23);

        Scroll6.setName("Scroll6"); // NOI18N
        Scroll6.setOpaque(true);

        tbBiokimia.setName("tbBiokimia"); // NOI18N
        Scroll6.setViewportView(tbBiokimia);

        FormInput.add(Scroll6);
        Scroll6.setBounds(100, 490, 650, 93);

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
        BtnTambahMasalah.setBounds(70, 490, 28, 23);

        BtnHapusRiwayatPersalinan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapusRiwayatPersalinan.setMnemonic('3');
        BtnHapusRiwayatPersalinan.setToolTipText("Alt+3");
        BtnHapusRiwayatPersalinan.setName("BtnHapusRiwayatPersalinan"); // NOI18N
        BtnHapusRiwayatPersalinan.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnHapusRiwayatPersalinan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusRiwayatPersalinanActionPerformed(evt);
            }
        });
        FormInput.add(BtnHapusRiwayatPersalinan);
        BtnHapusRiwayatPersalinan.setBounds(70, 520, 28, 23);

        jLabel45.setText("Masalah Terkait Gizi :");
        jLabel45.setName("jLabel45"); // NOI18N
        FormInput.add(jLabel45);
        jLabel45.setBounds(0, 410, 130, 23);

        jLabel46.setText("Biokimia:");
        jLabel46.setName("jLabel46"); // NOI18N
        FormInput.add(jLabel46);
        jLabel46.setBounds(10, 480, 50, 23);

        jLabel39.setText("Alergi Makanan:");
        jLabel39.setName("jLabel39"); // NOI18N
        FormInput.add(jLabel39);
        jLabel39.setBounds(-20, 610, 130, 23);

        Alergi.setFocusTraversalPolicyProvider(true);
        Alergi.setName("Alergi"); // NOI18N
        Alergi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AlergiKeyPressed(evt);
            }
        });
        FormInput.add(Alergi);
        Alergi.setBounds(120, 610, 260, 23);

        jLabel47.setText("Pola makan:");
        jLabel47.setName("jLabel47"); // NOI18N
        FormInput.add(jLabel47);
        jLabel47.setBounds(400, 610, 100, 23);

        Pola.setFocusTraversalPolicyProvider(true);
        Pola.setName("Pola"); // NOI18N
        Pola.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PolaKeyPressed(evt);
            }
        });
        FormInput.add(Pola);
        Pola.setBounds(510, 610, 260, 23);

        scrollPane6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane6.setName("scrollPane6"); // NOI18N

        DiagnosisGizi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        DiagnosisGizi.setColumns(20);
        DiagnosisGizi.setRows(5);
        DiagnosisGizi.setName("DiagnosisGizi"); // NOI18N
        DiagnosisGizi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosisGiziKeyPressed(evt);
            }
        });
        scrollPane6.setViewportView(DiagnosisGizi);

        FormInput.add(scrollPane6);
        scrollPane6.setBounds(120, 640, 645, 63);

        jLabel48.setText("Diagnosa Gizi:");
        jLabel48.setName("jLabel48"); // NOI18N
        FormInput.add(jLabel48);
        jLabel48.setBounds(-10, 640, 120, 23);

        jLabel49.setText("Intervensi Gizi");
        jLabel49.setName("jLabel49"); // NOI18N
        FormInput.add(jLabel49);
        jLabel49.setBounds(10, 710, 90, 23);

        jLabel51.setText("Tujuan Diet:");
        jLabel51.setName("jLabel51"); // NOI18N
        FormInput.add(jLabel51);
        jLabel51.setBounds(-20, 730, 130, 23);

        jLabel60.setText("Jenis Diet:");
        jLabel60.setName("jLabel60"); // NOI18N
        FormInput.add(jLabel60);
        jLabel60.setBounds(400, 730, 100, 23);

        JenisDiet.setFocusTraversalPolicyProvider(true);
        JenisDiet.setName("JenisDiet"); // NOI18N
        JenisDiet.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JenisDietKeyPressed(evt);
            }
        });
        FormInput.add(JenisDiet);
        JenisDiet.setBounds(510, 730, 260, 23);

        TujuanDiet.setFocusTraversalPolicyProvider(true);
        TujuanDiet.setName("TujuanDiet"); // NOI18N
        TujuanDiet.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TujuanDietKeyPressed(evt);
            }
        });
        FormInput.add(TujuanDiet);
        TujuanDiet.setBounds(120, 730, 260, 23);

        jLabel61.setText("Perhitungan diet:");
        jLabel61.setName("jLabel61"); // NOI18N
        FormInput.add(jLabel61);
        jLabel61.setBounds(10, 750, 90, 23);

        jLabel62.setText("Energi (Kal)");
        jLabel62.setName("jLabel62"); // NOI18N
        FormInput.add(jLabel62);
        jLabel62.setBounds(-20, 770, 130, 23);

        jLabel63.setText("Karbohidrat (g)");
        jLabel63.setName("jLabel63"); // NOI18N
        FormInput.add(jLabel63);
        jLabel63.setBounds(370, 770, 130, 23);

        Karbohidrat.setFocusTraversalPolicyProvider(true);
        Karbohidrat.setName("Karbohidrat"); // NOI18N
        Karbohidrat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KarbohidratKeyPressed(evt);
            }
        });
        FormInput.add(Karbohidrat);
        Karbohidrat.setBounds(510, 770, 260, 23);

        Energi.setFocusTraversalPolicyProvider(true);
        Energi.setName("Energi"); // NOI18N
        Energi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EnergiKeyPressed(evt);
            }
        });
        FormInput.add(Energi);
        Energi.setBounds(120, 770, 260, 23);

        jLabel64.setText("Protein (g)");
        jLabel64.setName("jLabel64"); // NOI18N
        FormInput.add(jLabel64);
        jLabel64.setBounds(370, 800, 130, 23);

        jLabel65.setText("Lemak (g)");
        jLabel65.setName("jLabel65"); // NOI18N
        FormInput.add(jLabel65);
        jLabel65.setBounds(-20, 800, 130, 23);

        Lemak.setFocusTraversalPolicyProvider(true);
        Lemak.setName("Lemak"); // NOI18N
        Lemak.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LemakKeyPressed(evt);
            }
        });
        FormInput.add(Lemak);
        Lemak.setBounds(120, 800, 260, 23);

        Protein.setFocusTraversalPolicyProvider(true);
        Protein.setName("Protein"); // NOI18N
        Protein.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ProteinKeyPressed(evt);
            }
        });
        FormInput.add(Protein);
        Protein.setBounds(510, 800, 260, 23);

        jLabel66.setText("Bentuk makanan:");
        jLabel66.setName("jLabel66"); // NOI18N
        FormInput.add(jLabel66);
        jLabel66.setBounds(-10, 830, 130, 23);

        jLabel67.setText("Cara pemberian:");
        jLabel67.setName("jLabel67"); // NOI18N
        FormInput.add(jLabel67);
        jLabel67.setBounds(380, 830, 130, 23);

        Cara.setFocusTraversalPolicyProvider(true);
        Cara.setName("Cara"); // NOI18N
        Cara.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CaraKeyPressed(evt);
            }
        });
        FormInput.add(Cara);
        Cara.setBounds(510, 830, 260, 23);

        Bentuk.setFocusTraversalPolicyProvider(true);
        Bentuk.setName("Bentuk"); // NOI18N
        Bentuk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BentukKeyPressed(evt);
            }
        });
        FormInput.add(Bentuk);
        Bentuk.setBounds(120, 830, 260, 23);

        jLabelImpl = new widget.Label();
        jLabelImpl.setText("Implementasi gizi:");
        jLabelImpl.setName("jLabelImpl");
        FormInput.add(jLabelImpl);
        jLabelImpl.setBounds(-10, 860, 130, 23);
        ImplementasiGizi = new widget.TextArea();
        ImplementasiGizi.setName("ImplementasiGizi");
        ImplementasiGizi.setColumns(20);
        ImplementasiGizi.setRows(3);
        ImplementasiGizi.setLineWrap(true);
        ImplementasiGizi.setWrapStyleWord(true);
        ImplementasiGizi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        scrollPaneImpl = new widget.ScrollPane();
        scrollPaneImpl.setViewportView(ImplementasiGizi);
        FormInput.add(scrollPaneImpl);
        scrollPaneImpl.setBounds(120, 858, 645, 28);

        jLabel68.setText("Monitoring:");
        jLabel68.setName("jLabel68"); // NOI18N
        FormInput.add(jLabel68);
        jLabel68.setBounds(40, 890, 130, 23);

        scrollPane7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane7.setName("scrollPane7"); // NOI18N

        Monitoring.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Monitoring.setColumns(20);
        Monitoring.setRows(5);
        Monitoring.setName("Monitoring"); // NOI18N
        Monitoring.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MonitoringKeyPressed(evt);
            }
        });
        scrollPane7.setViewportView(Monitoring);

        FormInput.add(scrollPane7);
        scrollPane7.setBounds(120, 910, 300, 60);

        jLabelEval.setText("Evaluasi:");
        jLabelEval.setName("jLabelEval"); // NOI18N
        FormInput.add(jLabelEval);
        jLabelEval.setBounds(430, 890, 80, 23);

        scrollPaneEval.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPaneEval.setName("scrollPaneEval"); // NOI18N

        Evaluasi.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        Evaluasi.setColumns(20);
        Evaluasi.setRows(5);
        Evaluasi.setName("Evaluasi"); // NOI18N
        scrollPaneEval.setViewportView(Evaluasi);

        FormInput.add(scrollPaneEval);
        scrollPaneEval.setBounds(500, 910, 265, 60);

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
            //Valid.pindah(evt,TCari,Kejadian);
        }
}//GEN-LAST:event_TNoRwKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (TNoRM.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "Pasien");
        } else if (BB.getText().trim().equals("")) {
            Valid.textKosong(BB, "Berat Badan");
        } else if (TB.getText().trim().equals("")) {
            Valid.textKosong(TB, "Tinggi Badan");
        } else if (IMT.getText().trim().equals("")) {
            Valid.textKosong(IMT, "Indeks Masa Tubuh");
        } else if (LiLA.getText().trim().equals("")) {
            Valid.textKosong(LiLA, "Lingkar Lengan Atas");
        } else if (ImtU.getText().trim().equals("")) {
            Valid.textKosong(ImtU, "Tinggi Lutut");
        } else if (BBPerU.getText().trim().equals("")) {
            Valid.textKosong(BBPerU, "BB/U");
        } else if (TBPerU.getText().trim().equals("")) {
            Valid.textKosong(TBPerU, "TB/U");
        } else if (BBPerTB.getText().trim().equals("")) {
            Valid.textKosong(BBPerTB, "BB/TB");
        } else if (LiLAPerU.getText().trim().equals("")) {
            Valid.textKosong(LiLAPerU, "LilA/U");
        } else if (Personal.getText().trim().equals("")) {
            Valid.textKosong(Personal, "Riwayat Personal");
        } else if (StatusGizi.getText().trim().equals("")) {
            Valid.textKosong(StatusGizi, "Status Gizi");
        } else if (KesanUmum.getText().trim().equals("")) {
            Valid.textKosong(KesanUmum, "Kesan Umum");
        } else if (MasalahGastro.getText().trim().equals("")) {
            Valid.textKosong(MasalahGastro, "Masalah Gastrointestinal");
        } else if (NmPetugas.getText().trim().equals("")) {
            Valid.textKosong(BtnDokter, "Petugas");
        } else {

            if (Sequel.menyimpantf("asesmen_awal_asuhan_gizi_ranap", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "No.Rawat", 35, new String[]{
                TNoRw.getText(), Valid.SetTgl(TglAsuhan.getSelectedItem() + ""), DiagnosaMasukRanap.getText(), Personal.getText(), BB.getText(), TB.getText(), IMT.getText(), LiLA.getText(), LiLAPerU.getText(), BBPerU.getText(),
                TBPerU.getText(), BBPerTB.getText(), ImtU.getText(), StatusGizi.getText(), KesanUmum.getText(), Td.getText(), Rr.getText(), Nadi.getText(), Suhu.getText(),
                MasalahGastro.getText(), Alergi.getText(), Pola.getText(), DiagnosisGizi.getText(), TujuanDiet.getText(), JenisDiet.getText(), Energi.getText(), Karbohidrat.getText(),
                Lemak.getText(), Protein.getText(), Bentuk.getText(), Cara.getText(), Monitoring.getText(), KdPetugas.getText(), ImplementasiGizi.getText(), Evaluasi.getText()
            }) == true) {
                tabMode.addRow(new String[]{
                    TNoRw.getText(), TNoRM.getText(), TPasien.getText(), TglLahir.getText(), Jk.getText(), Valid.SetTgl(TglAsuhan.getSelectedItem() + ""), DiagnosaMasukRanap.getText(), Personal.getText(), BB.getText(), TB.getText(), IMT.getText(), LiLA.getText(), LiLAPerU.getText(), BBPerU.getText(),
                    TBPerU.getText(), BBPerTB.getText(), ImtU.getText(), StatusGizi.getText(), KesanUmum.getText(), Td.getText(), Rr.getText(), Nadi.getText(), Suhu.getText(),
                    MasalahGastro.getText(), Alergi.getText(), Pola.getText(), DiagnosisGizi.getText(), TujuanDiet.getText(), JenisDiet.getText(), Energi.getText(), Karbohidrat.getText(),
                    Lemak.getText(), Protein.getText(), Bentuk.getText(), Cara.getText(), Monitoring.getText(), KdPetugas.getText(), NmPetugas.getText(), ImplementasiGizi.getText(), Evaluasi.getText()
                });
                emptTeks();
            }
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            //Valid.pindah(evt,Obat2an,BtnBatal);
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
                if (KdPetugas.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 36).toString())) {
                    hapus();
                } else {
                    JOptionPane.showMessageDialog(null, "Hanya bisa dihapus oleh petugas yang bersangkutan..!!");
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
        if (TNoRM.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "Pasien");
        } else if (BB.getText().trim().equals("")) {
            Valid.textKosong(BB, "Berat Badan");
        } else if (TB.getText().trim().equals("")) {
            Valid.textKosong(TB, "Tinggi Badan");
        } else if (IMT.getText().trim().equals("")) {
            Valid.textKosong(IMT, "Indeks Masa Tubuh");
        } else if (LiLA.getText().trim().equals("")) {
            Valid.textKosong(LiLA, "Lingkar Lengan Atas");
        } else if (BBPerU.getText().trim().equals("")) {
            Valid.textKosong(BBPerU, "BB/U");
        } else if (TBPerU.getText().trim().equals("")) {
            Valid.textKosong(TBPerU, "TB/U");
        } else if (BBPerTB.getText().trim().equals("")) {
            Valid.textKosong(BBPerTB, "BB/TB");
        } else if (LiLAPerU.getText().trim().equals("")) {
            Valid.textKosong(LiLAPerU, "LilA/U");
        } else if (DiagnosisGizi.getText().trim().equals("")) {
            Valid.textKosong(DiagnosisGizi, "Diagnosis Gizi");
        } else if (Monitoring.getText().trim().equals("")) {
            Valid.textKosong(Monitoring, "Monitoring & Evaluasi");
        } else if (NmPetugas.getText().trim().equals("")) {
            Valid.textKosong(BtnDokter, "Petugas");
        } else {
            if (tbObat.getSelectedRow() > -1) {
                if (akses.getkode().equals("Admin Utama")) {
                    ganti();
                } else {
                    if (KdPetugas.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 36).toString())) {
                        ganti();
                    } else {
                        JOptionPane.showMessageDialog(null, "Hanya bisa diganti oleh petugas yang bersangkutan..!!");
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
            if (TCari.getText().equals("")) {
                Valid.MyReportqry("rptDataAsuhanGiziPasien.jasper", "report", "::[ Data Asuhan Gizi Pasien ]::",
                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir,asesmen_awal_asuhan_gizi_ranap.tanggal,"
                        + "asesmen_awal_asuhan_gizi_ranap.antropometri_bb,asesmen_awal_asuhan_gizi_ranap.antropometri_tb,asesmen_awal_asuhan_gizi_ranap.antropometri_imt,asesmen_awal_asuhan_gizi_ranap.antropometri_lla,"
                        + "asesmen_awal_asuhan_gizi_ranap.antropometri_tl,asesmen_awal_asuhan_gizi_ranap.antropometri_ulna,asesmen_awal_asuhan_gizi_ranap.antropometri_bbideal,asesmen_awal_asuhan_gizi_ranap.antropometri_bbperu,"
                        + "asesmen_awal_asuhan_gizi_ranap.antropometri_tbperu,asesmen_awal_asuhan_gizi_ranap.antropometri_bbpertb,asesmen_awal_asuhan_gizi_ranap.antropometri_llaperu,asesmen_awal_asuhan_gizi_ranap.biokimia,"
                        + "asesmen_awal_asuhan_gizi_ranap.fisik_klinis,asesmen_awal_asuhan_gizi_ranap.alergi_telur,asesmen_awal_asuhan_gizi_ranap.alergi_susu_sapi,asesmen_awal_asuhan_gizi_ranap.alergi_kacang,asesmen_awal_asuhan_gizi_ranap.alergi_gluten,"
                        + "asesmen_awal_asuhan_gizi_ranap.alergi_udang,asesmen_awal_asuhan_gizi_ranap.alergi_ikan,asesmen_awal_asuhan_gizi_ranap.alergi_hazelnut,asesmen_awal_asuhan_gizi_ranap.pola_makan,asesmen_awal_asuhan_gizi_ranap.riwayat_personal,"
                        + "asesmen_awal_asuhan_gizi_ranap.diagnosa,asesmen_awal_asuhan_gizi_ranap.intervensi_gizi,asesmen_awal_asuhan_gizi_ranap.monitoring_evaluasi,asesmen_awal_asuhan_gizi_ranap.nip,petugas.nama "
                        + "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "inner join asesmen_awal_asuhan_gizi_ranap on reg_periksa.no_rawat=asesmen_awal_asuhan_gizi_ranap.no_rawat "
                        + "inner join petugas on asesmen_awal_asuhan_gizi_ranap.nip=petugas.nip where "
                        + "asesmen_awal_asuhan_gizi_ranap.tanggal between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' order by asesmen_awal_asuhan_gizi_ranap.tanggal", param);
            } else {
                Valid.MyReportqry("rptDataAsuhanGiziPasien.jasper", "report", "::[ Data Asuhan Gizi Pasien ]::",
                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir,asesmen_awal_asuhan_gizi_ranap.tanggal,"
                        + "asesmen_awal_asuhan_gizi_ranap.antropometri_bb,asesmen_awal_asuhan_gizi_ranap.antropometri_tb,asesmen_awal_asuhan_gizi_ranap.antropometri_imt,asesmen_awal_asuhan_gizi_ranap.antropometri_lla,"
                        + "asesmen_awal_asuhan_gizi_ranap.antropometri_tl,asesmen_awal_asuhan_gizi_ranap.antropometri_ulna,asesmen_awal_asuhan_gizi_ranap.antropometri_bbideal,asesmen_awal_asuhan_gizi_ranap.antropometri_bbperu,"
                        + "asesmen_awal_asuhan_gizi_ranap.antropometri_tbperu,asesmen_awal_asuhan_gizi_ranap.antropometri_bbpertb,asesmen_awal_asuhan_gizi_ranap.antropometri_llaperu,asesmen_awal_asuhan_gizi_ranap.biokimia,"
                        + "asesmen_awal_asuhan_gizi_ranap.fisik_klinis,asesmen_awal_asuhan_gizi_ranap.alergi_telur,asesmen_awal_asuhan_gizi_ranap.alergi_susu_sapi,asesmen_awal_asuhan_gizi_ranap.alergi_kacang,asesmen_awal_asuhan_gizi_ranap.alergi_gluten,"
                        + "asesmen_awal_asuhan_gizi_ranap.alergi_udang,asesmen_awal_asuhan_gizi_ranap.alergi_ikan,asesmen_awal_asuhan_gizi_ranap.alergi_hazelnut,asesmen_awal_asuhan_gizi_ranap.pola_makan,asesmen_awal_asuhan_gizi_ranap.riwayat_personal,"
                        + "asesmen_awal_asuhan_gizi_ranap.diagnosa,asesmen_awal_asuhan_gizi_ranap.intervensi_gizi,asesmen_awal_asuhan_gizi_ranap.monitoring_evaluasi,asesmen_awal_asuhan_gizi_ranap.nip,petugas.nama "
                        + "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "inner join asesmen_awal_asuhan_gizi_ranap on reg_periksa.no_rawat=asesmen_awal_asuhan_gizi_ranap.no_rawat "
                        + "inner join petugas on asesmen_awal_asuhan_gizi_ranap.nip=petugas.nip where "
                        + "asesmen_awal_asuhan_gizi_ranap.tanggal between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' and reg_periksa.no_rawat like '%" + TCari.getText().trim() + "%' or "
                        + "asesmen_awal_asuhan_gizi_ranap.tanggal between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' and pasien.no_rkm_medis like '%" + TCari.getText().trim() + "%' or "
                        + "asesmen_awal_asuhan_gizi_ranap.tanggal between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' and pasien.nm_pasien like '%" + TCari.getText().trim() + "%' or "
                        + "asesmen_awal_asuhan_gizi_ranap.tanggal between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' and asesmen_awal_asuhan_gizi_ranap.nip like '%" + TCari.getText().trim() + "%' or "
                        + "asesmen_awal_asuhan_gizi_ranap.tanggal between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' and asesmen_awal_asuhan_gizi_ranap.diagnosa like '%" + TCari.getText().trim() + "%' or "
                        + "asesmen_awal_asuhan_gizi_ranap.tanggal between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' and petugas.nama like '%" + TCari.getText().trim() + "%' order by asesmen_awal_asuhan_gizi_ranap.tanggal", param);
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
            TCari.setText("");
            tampil();
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

    private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
        isForm();
    }//GEN-LAST:event_ChkInputActionPerformed

    private void KdPetugasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KdPetugasKeyPressed

    }//GEN-LAST:event_KdPetugasKeyPressed

    private void BtnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokterActionPerformed
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setAlwaysOnTop(false);
        petugas.setVisible(true);
    }//GEN-LAST:event_BtnDokterActionPerformed

    private void BtnDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnDokterKeyPressed
        //     Valid.pindah(evt,Monitoring,BtnSimpan);
    }//GEN-LAST:event_BtnDokterKeyPressed

    private void MnAsuhanGiziActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnAsuhanGiziActionPerformed
        if (tbObat.getSelectedRow() > -1) {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("diagnosa", DiagnosaMasukRanap.getText());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", tbObat.getValueAt(tbObat.getSelectedRow(), 36).toString());
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbObat.getValueAt(tbObat.getSelectedRow(), 37).toString() + "\nID " + (finger.equals("") ? tbObat.getValueAt(tbObat.getSelectedRow(), 36).toString() : finger) + "\n" + TglAsuhan.getSelectedItem());
            try {
                ps = koneksi.prepareStatement("select * from asesmen_awal_asuhan_gizi_ranapbiokimia where no_rawat=? order by tgl_thn");
                try {
                    ps.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                    rs = ps.executeQuery();
                    i = 1;
                    while (rs.next()) {
                        param.put("no" + i, i + "");
                        param.put("tgl" + i, rs.getString("tgl_thn"));
                        param.put("pemeriksaan" + i, rs.getString("pemeriksaan"));
                        param.put("nilai" + i, rs.getString("nilai"));
                        param.put("interpretasi" + i, rs.getString("interpretasi"));
                        i++;
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

            Valid.MyReportqry("rptCetakAsesmenAwalAsuhanGiziRawatInap.jasper", "report", "::[ Laporan Asesmen Awal Asuhan Gizi Rawat Inap ]::",
                    "SELECT reg_periksa.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, asesmen_awal_asuhan_gizi_ranap.tanggal, "
                    + "asesmen_awal_asuhan_gizi_ranap.diagnosa, asesmen_awal_asuhan_gizi_ranap.riwayat, asesmen_awal_asuhan_gizi_ranap.bb, asesmen_awal_asuhan_gizi_ranap.tb, "
                    + "asesmen_awal_asuhan_gizi_ranap.imt, asesmen_awal_asuhan_gizi_ranap.lila, asesmen_awal_asuhan_gizi_ranap.lilau, asesmen_awal_asuhan_gizi_ranap.bbu, "
                    + "asesmen_awal_asuhan_gizi_ranap.tbu, asesmen_awal_asuhan_gizi_ranap.bbtb, asesmen_awal_asuhan_gizi_ranap.imtu, asesmen_awal_asuhan_gizi_ranap.status, "
                    + "asesmen_awal_asuhan_gizi_ranap.kesanumum, asesmen_awal_asuhan_gizi_ranap.tensi, asesmen_awal_asuhan_gizi_ranap.respirasi, asesmen_awal_asuhan_gizi_ranap.nadi, "
                    + "asesmen_awal_asuhan_gizi_ranap.suhu, asesmen_awal_asuhan_gizi_ranap.masalah, asesmen_awal_asuhan_gizi_ranap.alergi, asesmen_awal_asuhan_gizi_ranap.pola, "
                    + "asesmen_awal_asuhan_gizi_ranap.diagnosa_gizi, asesmen_awal_asuhan_gizi_ranap.tujuan, asesmen_awal_asuhan_gizi_ranap.jenis, asesmen_awal_asuhan_gizi_ranap.energi, "
                    + "asesmen_awal_asuhan_gizi_ranap.karbohidrat, asesmen_awal_asuhan_gizi_ranap.lemak, asesmen_awal_asuhan_gizi_ranap.protein, asesmen_awal_asuhan_gizi_ranap.bentuk, "
                    + "asesmen_awal_asuhan_gizi_ranap.cara, asesmen_awal_asuhan_gizi_ranap.monitoring, asesmen_awal_asuhan_gizi_ranap.evaluasi, asesmen_awal_asuhan_gizi_ranap.nip, petugas.nama FROM reg_periksa INNER JOIN pasien ON "
                    + "reg_periksa.no_rkm_medis = pasien.no_rkm_medis INNER JOIN asesmen_awal_asuhan_gizi_ranap ON reg_periksa.no_rawat = asesmen_awal_asuhan_gizi_ranap.no_rawat INNER JOIN petugas ON "
                    + "asesmen_awal_asuhan_gizi_ranap.nip = petugas.nip where asesmen_awal_asuhan_gizi_ranap.no_rawat='" + tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "'", param);
        }
    }//GEN-LAST:event_MnAsuhanGiziActionPerformed

    private void BBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BBKeyPressed
        Valid.pindah(evt, TglAsuhan, TB);
    }//GEN-LAST:event_BBKeyPressed

    private void TglAsuhanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TglAsuhanKeyPressed
        Valid.pindah(evt, TCari, BB);
    }//GEN-LAST:event_TglAsuhanKeyPressed

    private void TBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TBKeyPressed
        Valid.pindah(evt, BB, IMT);
    }//GEN-LAST:event_TBKeyPressed

    private void IMTKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_IMTKeyPressed
        Valid.pindah(evt, TB, LiLA);
    }//GEN-LAST:event_IMTKeyPressed

    private void LiLAKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LiLAKeyPressed
        Valid.pindah(evt, IMT, ImtU);
    }//GEN-LAST:event_LiLAKeyPressed

    private void ImtUKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ImtUKeyPressed
        //    Valid.pindah(evt,LiLA,ULNA);
    }//GEN-LAST:event_ImtUKeyPressed

    private void BBPerUKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BBPerUKeyPressed
        //    Valid.pindah(evt,BBIdeal,TBPerU);
    }//GEN-LAST:event_BBPerUKeyPressed

    private void BBPerTBKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BBPerTBKeyPressed
        Valid.pindah(evt, TBPerU, LiLAPerU);
    }//GEN-LAST:event_BBPerTBKeyPressed

    private void LiLAPerUKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LiLAPerUKeyPressed
        Valid.pindah(evt, BBPerTB, Personal);
    }//GEN-LAST:event_LiLAPerUKeyPressed

    private void TBPerUKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TBPerUKeyPressed
        Valid.pindah(evt, BBPerU, BBPerTB);
    }//GEN-LAST:event_TBPerUKeyPressed

    private void StatusGiziKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_StatusGiziKeyPressed
        //    Valid.pindah(evt,FisikKlinis,RiwayatPersonal);
    }//GEN-LAST:event_StatusGiziKeyPressed

    private void KesanUmumKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KesanUmumKeyPressed
        Valid.pindah(evt, StatusGizi, MasalahGastro);
    }//GEN-LAST:event_KesanUmumKeyPressed

    private void PersonalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PersonalKeyPressed
        //    Valid.pindah2(evt,LiLAPerU,FisikKlinis);
    }//GEN-LAST:event_PersonalKeyPressed

    private void MasalahGastroKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MasalahGastroKeyPressed
        //    Valid.pindah2(evt,RiwayatPersonal,IntervensiGizi);
    }//GEN-LAST:event_MasalahGastroKeyPressed

    private void TdKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TdKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TdKeyPressed

    private void RrKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RrKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_RrKeyPressed

    private void NadiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NadiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NadiKeyPressed

    private void SuhuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SuhuKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_SuhuKeyPressed

    private void BtnTambahMasalahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTambahMasalahActionPerformed
        if (TNoRM.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Pilih terlebih dahulu pasien yang mau dimasukkan data kelarihannya...");

        } else {
            emptTeksBiokimia();
            tampilBiokimia(); // muat data biokimia pasien yg sudah ada saat dialog dibuka
            DlgBiokimia.setLocationRelativeTo(internalFrame1);
            DlgBiokimia.setVisible(true);
        }
    }//GEN-LAST:event_BtnTambahMasalahActionPerformed

    private void BtnHapusRiwayatPersalinanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusRiwayatPersalinanActionPerformed
        if (tbBiokimia.getSelectedRow() > -1) {
            // Hapus baris tepat: key (no_rawat, tgl_thn, pemeriksaan). Dulu keliru
            // pakai TNoRM (no_rkm_medis) sbg no_rawat -> tak pernah cocok.
            Sequel.queryu2tf("delete from asesmen_awal_asuhan_gizi_ranapbiokimia where no_rawat=? and tgl_thn=? and pemeriksaan=?", 3, new String[]{
                TNoRw.getText(),
                tbBiokimia.getValueAt(tbBiokimia.getSelectedRow(), 1).toString(),
                tbBiokimia.getValueAt(tbBiokimia.getSelectedRow(), 2).toString()
            });
            tampilBiokimia();
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih data terlebih dahulu..!!");
        }
    }//GEN-LAST:event_BtnHapusRiwayatPersalinanActionPerformed

    private void AlergiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AlergiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_AlergiKeyPressed

    private void PolaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PolaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_PolaKeyPressed

    private void DiagnosisGiziKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosisGiziKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DiagnosisGiziKeyPressed

    private void JenisDietKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JenisDietKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_JenisDietKeyPressed

    private void TujuanDietKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TujuanDietKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TujuanDietKeyPressed

    private void KarbohidratKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KarbohidratKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_KarbohidratKeyPressed

    private void EnergiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EnergiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_EnergiKeyPressed

    private void LemakKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LemakKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_LemakKeyPressed

    private void ProteinKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ProteinKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_ProteinKeyPressed

    private void CaraKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CaraKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_CaraKeyPressed

    private void BentukKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BentukKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BentukKeyPressed

    private void MonitoringKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MonitoringKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_MonitoringKeyPressed

    private void BtnKeluarKehamilanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarKehamilanActionPerformed
        DlgBiokimia.dispose();
    }//GEN-LAST:event_BtnKeluarKehamilanActionPerformed

    private void BtnSimpanRiwayatKehamilanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanRiwayatKehamilanActionPerformed
        if (NamaPemeriksaan.getText().trim().equals("")) {
            Valid.textKosong(NamaPemeriksaan, "Nama Pemeriksaan");
        } else if (Nilai.getText().trim().equals("")) {
            Valid.textKosong(Nilai, "Nilai");
        } else if (Interpretasi.getText().trim().equals("")) {
            Valid.textKosong(Interpretasi, "Interpretasi");
        } else {
            // Upsert: PK (no_rawat, tgl_thn, pemeriksaan) — pemeriksaan sama pd tgl
            // sama = perbarui nilai/interpretasi (bukan gagal "sudah ada").
            if (Sequel.queryu2tf("insert into asesmen_awal_asuhan_gizi_ranapbiokimia (no_rawat,tgl_thn,pemeriksaan,nilai,interpretasi) values (?,?,?,?,?) "
                    + "on duplicate key update nilai=values(nilai), interpretasi=values(interpretasi)", 5, new String[]{
                TNoRw.getText(), Valid.SetTgl(TanggalPemeriksaan.getSelectedItem() + ""), NamaPemeriksaan.getText(), Nilai.getText(), Interpretasi.getText()
            }) == true) {
                emptTeksBiokimia();
                tampilBiokimia();
            }
        }
    }//GEN-LAST:event_BtnSimpanRiwayatKehamilanActionPerformed

    private void NamaPemeriksaanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NamaPemeriksaanKeyPressed
        Valid.pindah(evt, BtnKeluarKehamilan, Nilai);
    }//GEN-LAST:event_NamaPemeriksaanKeyPressed

    private void NilaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NilaiKeyPressed
        Valid.pindah(evt, NamaPemeriksaan, Interpretasi);
    }//GEN-LAST:event_NilaiKeyPressed

    private void InterpretasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_InterpretasiKeyPressed
        //   Valid.pindah(evt,JenisPersalinan,Penyulit);
    }//GEN-LAST:event_InterpretasiKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMAsesmenAwalAsuhanGiziRawatInap dialog = new RMAsesmenAwalAsuhanGiziRawatInap(new javax.swing.JFrame(), true);
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
    private widget.TextBox Alergi;
    private widget.TextBox BB;
    private widget.TextBox BBPerTB;
    private widget.TextBox BBPerU;
    private widget.TextBox Bentuk;
    private widget.TextArea ImplementasiGizi;
    private widget.ScrollPane scrollPaneImpl;
    private widget.Label jLabelImpl;
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnDokter;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnHapusRiwayatPersalinan;
    private widget.Button BtnKeluar;
    private widget.Button BtnKeluarKehamilan;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.Button BtnSimpanRiwayatKehamilan;
    private widget.Button BtnTambahMasalah;
    private widget.TextBox Cara;
    private widget.CekBox ChkInput;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.TextBox DiagnosaMasukRanap;
    private widget.TextArea DiagnosisGizi;
    private javax.swing.JDialog DlgBiokimia;
    private widget.TextBox Energi;
    private widget.PanelBiasa FormInput;
    private widget.TextBox IMT;
    private widget.TextBox ImtU;
    private widget.TextBox Interpretasi;
    private widget.TextBox JenisDiet;
    private widget.TextBox Jk;
    private widget.TextBox Karbohidrat;
    private widget.TextBox KdPetugas;
    private widget.TextBox KesanUmum;
    private widget.Label LCount;
    private widget.TextBox Lemak;
    private widget.TextBox LiLA;
    private widget.TextBox LiLAPerU;
    private widget.TextArea MasalahGastro;
    private javax.swing.JMenuItem MnAsuhanGizi;
    private widget.TextArea Monitoring;
    private widget.TextArea Evaluasi;
    private widget.TextBox Nadi;
    private widget.TextBox NamaPemeriksaan;
    private widget.TextBox Nilai;
    private widget.TextBox NmPetugas;
    private javax.swing.JPanel PanelInput;
    private widget.TextArea Personal;
    private widget.TextBox Pola;
    private widget.TextBox Protein;
    private widget.TextBox Rr;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll6;
    private widget.TextBox StatusGizi;
    private widget.TextBox Suhu;
    private widget.TextBox TB;
    private widget.TextBox TBPerU;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Tanggal TanggalPemeriksaan;
    private widget.TextBox Td;
    private widget.Tanggal TglAsuhan;
    private widget.TextBox TglLahir;
    private widget.TextBox TujuanDiet;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame4;
    private widget.Label jLabel10;
    private widget.Label jLabel105;
    private widget.Label jLabel107;
    private widget.Label jLabel109;
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
    private widget.Label jLabel24;
    private widget.Label jLabel28;
    private widget.Label jLabel29;
    private widget.Label jLabel30;
    private widget.Label jLabel31;
    private widget.Label jLabel32;
    private widget.Label jLabel33;
    private widget.Label jLabel34;
    private widget.Label jLabel35;
    private widget.Label jLabel36;
    private widget.Label jLabel38;
    private widget.Label jLabel39;
    private widget.Label jLabel40;
    private widget.Label jLabel43;
    private widget.Label jLabel44;
    private widget.Label jLabel45;
    private widget.Label jLabel46;
    private widget.Label jLabel47;
    private widget.Label jLabel48;
    private widget.Label jLabel49;
    private widget.Label jLabel5;
    private widget.Label jLabel50;
    private widget.Label jLabel51;
    private widget.Label jLabel52;
    private widget.Label jLabel53;
    private widget.Label jLabel54;
    private widget.Label jLabel55;
    private widget.Label jLabel56;
    private widget.Label jLabel57;
    private widget.Label jLabel58;
    private widget.Label jLabel59;
    private widget.Label jLabel6;
    private widget.Label jLabel60;
    private widget.Label jLabel61;
    private widget.Label jLabel62;
    private widget.Label jLabel63;
    private widget.Label jLabel64;
    private widget.Label jLabel65;
    private widget.Label jLabel66;
    private widget.Label jLabel67;
    private widget.Label jLabel68;
    private widget.Label jLabelEval;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private widget.Label jLabel99;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.Label label11;
    private widget.Label label14;
    private widget.PanelBiasa panelBiasa2;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.ScrollPane scrollInput;
    private widget.ScrollPane scrollPane2;
    private widget.ScrollPane scrollPane5;
    private widget.ScrollPane scrollPane6;
    private widget.ScrollPane scrollPane7;
    private widget.ScrollPane scrollPaneEval;
    private widget.Table tbBiokimia;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            if (TCari.getText().equals("")) {
                ps = koneksi.prepareStatement(
                        "SELECT reg_periksa.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, asesmen_awal_asuhan_gizi_ranap.tanggal, "
                        + "asesmen_awal_asuhan_gizi_ranap.diagnosa, asesmen_awal_asuhan_gizi_ranap.riwayat, asesmen_awal_asuhan_gizi_ranap.bb, "
                        + "asesmen_awal_asuhan_gizi_ranap.tb, asesmen_awal_asuhan_gizi_ranap.imt, asesmen_awal_asuhan_gizi_ranap.lila,asesmen_awal_asuhan_gizi_ranap.lilau, "
                        + "asesmen_awal_asuhan_gizi_ranap.bbu, asesmen_awal_asuhan_gizi_ranap.tbu, asesmen_awal_asuhan_gizi_ranap.bbtb, "
                        + "asesmen_awal_asuhan_gizi_ranap.imtu, asesmen_awal_asuhan_gizi_ranap.`status`, asesmen_awal_asuhan_gizi_ranap.kesanumum, "
                        + "asesmen_awal_asuhan_gizi_ranap.tensi,asesmen_awal_asuhan_gizi_ranap.respirasi,asesmen_awal_asuhan_gizi_ranap.nadi,asesmen_awal_asuhan_gizi_ranap.suhu,"
                        + "asesmen_awal_asuhan_gizi_ranap.masalah, asesmen_awal_asuhan_gizi_ranap.alergi, asesmen_awal_asuhan_gizi_ranap.pola, "
                        + "asesmen_awal_asuhan_gizi_ranap.diagnosa_gizi, asesmen_awal_asuhan_gizi_ranap.tujuan, asesmen_awal_asuhan_gizi_ranap.jenis, "
                        + "asesmen_awal_asuhan_gizi_ranap.energi, asesmen_awal_asuhan_gizi_ranap.karbohidrat, asesmen_awal_asuhan_gizi_ranap.lemak, "
                        + "asesmen_awal_asuhan_gizi_ranap.protein, asesmen_awal_asuhan_gizi_ranap.bentuk, asesmen_awal_asuhan_gizi_ranap.cara, "
                        + "asesmen_awal_asuhan_gizi_ranap.monitoring, asesmen_awal_asuhan_gizi_ranap.evaluasi, asesmen_awal_asuhan_gizi_ranap.nip, petugas.nama, asesmen_awal_asuhan_gizi_ranap.implementasi FROM reg_periksa INNER JOIN pasien "
                        + "ON reg_periksa.no_rkm_medis = pasien.no_rkm_medis INNER JOIN asesmen_awal_asuhan_gizi_ranap ON "
                        + "reg_periksa.no_rawat = asesmen_awal_asuhan_gizi_ranap.no_rawat inner join petugas on asesmen_awal_asuhan_gizi_ranap.nip=petugas.nip where "
                        + "asesmen_awal_asuhan_gizi_ranap.tanggal between ? and ? order by asesmen_awal_asuhan_gizi_ranap.tanggal");
            } else {
                ps = koneksi.prepareStatement(
                        "SELECT reg_periksa.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, asesmen_awal_asuhan_gizi_ranap.tanggal, "
                        + "asesmen_awal_asuhan_gizi_ranap.diagnosa, asesmen_awal_asuhan_gizi_ranap.riwayat, asesmen_awal_asuhan_gizi_ranap.bb, "
                        + "asesmen_awal_asuhan_gizi_ranap.tb, asesmen_awal_asuhan_gizi_ranap.imt, asesmen_awal_asuhan_gizi_ranap.lila,asesmen_awal_asuhan_gizi_ranap.lilau, "
                        + "asesmen_awal_asuhan_gizi_ranap.bbu, asesmen_awal_asuhan_gizi_ranap.tbu, asesmen_awal_asuhan_gizi_ranap.bbtb, "
                        + "asesmen_awal_asuhan_gizi_ranap.imtu, asesmen_awal_asuhan_gizi_ranap.`status`, asesmen_awal_asuhan_gizi_ranap.kesanumum, "
                        + "asesmen_awal_asuhan_gizi_ranap.tensi,asesmen_awal_asuhan_gizi_ranap.respirasi,asesmen_awal_asuhan_gizi_ranap.nadi,asesmen_awal_asuhan_gizi_ranap.suhu,"
                        + "asesmen_awal_asuhan_gizi_ranap.masalah, asesmen_awal_asuhan_gizi_ranap.alergi, asesmen_awal_asuhan_gizi_ranap.pola, "
                        + "asesmen_awal_asuhan_gizi_ranap.diagnosa_gizi, asesmen_awal_asuhan_gizi_ranap.tujuan, asesmen_awal_asuhan_gizi_ranap.jenis, "
                        + "asesmen_awal_asuhan_gizi_ranap.energi, asesmen_awal_asuhan_gizi_ranap.karbohidrat, asesmen_awal_asuhan_gizi_ranap.lemak, "
                        + "asesmen_awal_asuhan_gizi_ranap.protein, asesmen_awal_asuhan_gizi_ranap.bentuk, asesmen_awal_asuhan_gizi_ranap.cara, "
                        + "asesmen_awal_asuhan_gizi_ranap.monitoring, asesmen_awal_asuhan_gizi_ranap.evaluasi, asesmen_awal_asuhan_gizi_ranap.nip, petugas.nama, asesmen_awal_asuhan_gizi_ranap.implementasi FROM reg_periksa INNER JOIN pasien "
                        + "ON reg_periksa.no_rkm_medis = pasien.no_rkm_medis INNER JOIN asesmen_awal_asuhan_gizi_ranap ON "
                        + "reg_periksa.no_rawat = asesmen_awal_asuhan_gizi_ranap.no_rawat inner join petugas on asesmen_awal_asuhan_gizi_ranap.nip=petugas.nip where "
                        + "asesmen_awal_asuhan_gizi_ranap.tanggal between ? and ? and reg_periksa.no_rawat like ? or "
                        + "asesmen_awal_asuhan_gizi_ranap.tanggal between ? and ? and pasien.no_rkm_medis like ? or "
                        + "asesmen_awal_asuhan_gizi_ranap.tanggal between ? and ? and pasien.nm_pasien like ? or "
                        + "asesmen_awal_asuhan_gizi_ranap.tanggal between ? and ? and asesmen_awal_asuhan_gizi_ranap.nip like ? or "
                        + "asesmen_awal_asuhan_gizi_ranap.tanggal between ? and ? and asesmen_awal_asuhan_gizi_ranap.diagnosa like ? or "
                        + "asesmen_awal_asuhan_gizi_ranap.tanggal between ? and ? and petugas.nama like ? order by asesmen_awal_asuhan_gizi_ranap.tanggal");
            }

            try {
                if (TCari.getText().equals("")) {
                    ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
                    ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
                } else {
                    ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
                    ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
                    ps.setString(3, "%" + TCari.getText() + "%");
                    ps.setString(4, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
                    ps.setString(5, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
                    ps.setString(6, "%" + TCari.getText() + "%");
                    ps.setString(7, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
                    ps.setString(8, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
                    ps.setString(9, "%" + TCari.getText() + "%");
                    ps.setString(10, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
                    ps.setString(11, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
                    ps.setString(12, "%" + TCari.getText() + "%");
                    ps.setString(13, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
                    ps.setString(14, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
                    ps.setString(15, "%" + TCari.getText() + "%");
                    ps.setString(16, Valid.SetTgl(DTPCari1.getSelectedItem() + ""));
                    ps.setString(17, Valid.SetTgl(DTPCari2.getSelectedItem() + ""));
                    ps.setString(18, "%" + TCari.getText() + "%");
                }
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("jk"), rs.getString("tgl_lahir"), rs.getString("tanggal"),
                        rs.getString("diagnosa"), rs.getString("riwayat"), rs.getString("bb"),
                        rs.getString("tb"), rs.getString("imt"), rs.getString("lila"), rs.getString("lilau"),
                        rs.getString("bbu"), rs.getString("tbu"), rs.getString("bbtb"),
                        rs.getString("imtu"), rs.getString("status"), rs.getString("kesanumum"),
                        rs.getString("tensi"), rs.getString("respirasi"), rs.getString("nadi"), rs.getString("suhu"),
                        rs.getString("masalah"), rs.getString("alergi"), rs.getString("pola"),
                        rs.getString("diagnosa_gizi"), rs.getString("tujuan"), rs.getString("jenis"),
                        rs.getString("energi"), rs.getString("karbohidrat"), rs.getString("lemak"),
                        rs.getString("protein"), rs.getString("bentuk"), rs.getString("cara"),
                        rs.getString("monitoring"), rs.getString("nip"), rs.getString("nama"), rs.getString("implementasi"), rs.getString("evaluasi")
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
        int b = tabMode.getRowCount();
        LCount.setText("" + b);
    }

    public void emptTeks() {
        TNoRw.setText("");
        TNoRM.setText("");
        TPasien.setText("");
        TglLahir.setText("");
        Jk.setText("");
        DiagnosaMasukRanap.setText("");
        Personal.setText("");
        BB.setText("");
        TB.setText("");
        IMT.setText("");
        LiLA.setText("");
        LiLAPerU.setText("");
        BBPerU.setText("");
        TBPerU.setText("");
        BBPerTB.setText("");
        ImtU.setText("");
        StatusGizi.setText("");
        KesanUmum.setText("");
        Td.setText("");
        Rr.setText("");
        Nadi.setText("");
        Suhu.setText("");
        MasalahGastro.setText("");
        Alergi.setText("");
        Pola.setText("");
        DiagnosisGizi.setText("");
        TujuanDiet.setText("");
        JenisDiet.setText("");
        Energi.setText("");
        Karbohidrat.setText("");
        Lemak.setText("");
        Protein.setText("");
        Bentuk.setText("");
        ImplementasiGizi.setText("");
        Cara.setText("");
        Monitoring.setText("");
        Evaluasi.setText("");

    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            tampilBiokimia(); // sinkronkan tabel biokimia dgn pasien terpilih

            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            TglLahir.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
            Jk.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
            Valid.SetTgl(TglAsuhan, tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            DiagnosaMasukRanap.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
            Personal.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString());
            BB.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString());
            TB.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString());
            IMT.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString());
            LiLA.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString());
            LiLAPerU.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString());
            BBPerU.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 13).toString());
            TBPerU.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 14).toString());
            BBPerTB.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 15).toString());
            ImtU.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 16).toString());
            StatusGizi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 17).toString());
            KesanUmum.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 18).toString());
            Td.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 19).toString());
            Rr.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 20).toString());
            Nadi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 21).toString());
            Suhu.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 22).toString());
            MasalahGastro.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 23).toString());
            Alergi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 24).toString());
            Pola.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 25).toString());
            DiagnosisGizi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 26).toString());
            TujuanDiet.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 27).toString());
            JenisDiet.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 28).toString());
            Energi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 29).toString());
            Karbohidrat.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 30).toString());
            Lemak.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 31).toString());
            Protein.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 32).toString());
            Bentuk.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 33).toString());
            Cara.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 34).toString());
            Monitoring.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 35).toString());
            KdPetugas.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 36).toString());
            NmPetugas.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 37).toString());
            ImplementasiGizi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 38).toString());
            Evaluasi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 39).toString());
        }
    }

    private void isRawat() {
        Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat=? ", TNoRM, TNoRw.getText());
        Sequel.cariIsi("select diagnosa_awal from kamar_inap where diagnosa_awal<>'' and no_rawat=? ", DiagnosaMasukRanap, TNoRw.getText());
    }

    private void isPsien() {
        Sequel.cariIsi("select pasien.nm_pasien from pasien where pasien.no_rkm_medis=? ", TPasien, TNoRM.getText());
        Sequel.cariIsi("select if(jk='L','Laki-Laki','Perempuan') from pasien where no_rkm_medis=? ", Jk, TNoRM.getText());
        Sequel.cariIsi("select tgl_lahir from pasien where no_rkm_medis=? ", TglLahir, TNoRM.getText());
    }

    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        Sequel.cariIsi("select reg_periksa.tgl_registrasi from reg_periksa where reg_periksa.no_rawat='" + norwt + "'", DTPCari1);
        umur = Sequel.cariInteger("select umurdaftar from reg_periksa where no_rawat=?", norwt);
        sttsumur = Sequel.cariIsi("select sttsumur from reg_periksa where no_rawat=?", norwt);
        DTPCari2.setDate(tgl2);
        isRawat();
        isPsien();
        ChkInput.setSelected(true);
        isForm();
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
        BtnSimpan.setEnabled(akses.getasuhan_gizi());
        BtnHapus.setEnabled(akses.getasuhan_gizi());
        BtnEdit.setEnabled(akses.getasuhan_gizi());
        BtnPrint.setEnabled(akses.getasuhan_gizi());
        if (akses.getjml2() >= 1) {
            KdPetugas.setEditable(false);
            BtnDokter.setEnabled(false);
            KdPetugas.setText(akses.getkode());
            Sequel.cariIsi("select petugas.nama from petugas where petugas.nip=?", NmPetugas, KdPetugas.getText());
            if (NmPetugas.getText().equals("")) {
                KdPetugas.setText("");
                JOptionPane.showMessageDialog(null, "User login bukan petugas...!!");
            }
        }
    }

    private void isBMI() {
        if ((!TB.getText().equals("")) && (!BB.getText().equals(""))) {
            try {
                IMT.setText(Valid.SetAngka8(Valid.SetAngka(BB.getText()) / ((Valid.SetAngka(TB.getText()) / 100) * (Valid.SetAngka(TB.getText()) / 100)), 1) + "");
            } catch (Exception e) {
                IMT.setText("");
            }

            if (sttsumur.equals("Bl")) {
                try {
                    BBPerU.setText(Valid.SetAngka8(Valid.SetAngka(BB.getText()) / umur, 1) + "");
                } catch (Exception e) {
                    BBPerU.setText("");
                }

                try {
                    TBPerU.setText(Valid.SetAngka8(Valid.SetAngka(TB.getText()) / umur, 1) + "");
                } catch (Exception e) {
                    TBPerU.setText("");
                }
            }
        }
    }

    private void ganti() {
        if (Sequel.mengedittf("asesmen_awal_asuhan_gizi_ranap",
                "no_rawat=? and tanggal=?",
                "no_rawat=?, tanggal=?, diagnosa=?, riwayat=?, bb=?, tb=?, imt=?, lila=?, lilau=?, bbu=?, tbu=?, bbtb=?, imtu=?, status=?, kesanumum=?, tensi=?, respirasi=?, nadi=?, suhu=?, masalah=?, alergi=?, pola=?, diagnosa_gizi=?, tujuan=?, jenis=?, energi=?, karbohidrat=?, lemak=?, protein=?, bentuk=?, cara=?, monitoring=?, nip=?, implementasi=?, evaluasi=?",
                37, new String[]{
                    TNoRw.getText(), Valid.SetTgl(TglAsuhan.getSelectedItem() + ""), DiagnosaMasukRanap.getText(), Personal.getText(), BB.getText(), TB.getText(), IMT.getText(),
                    LiLA.getText(), LiLAPerU.getText(), BBPerU.getText(), TBPerU.getText(), BBPerTB.getText(), ImtU.getText(), StatusGizi.getText(), KesanUmum.getText(), Td.getText(),
                    Rr.getText(), Nadi.getText(), Suhu.getText(), MasalahGastro.getText(), Alergi.getText(), Pola.getText(), DiagnosisGizi.getText(), TujuanDiet.getText(), JenisDiet.getText(),
                    Energi.getText(), Karbohidrat.getText(), Lemak.getText(), Protein.getText(), Bentuk.getText(), Cara.getText(), Monitoring.getText(), KdPetugas.getText(), ImplementasiGizi.getText(), Evaluasi.getText(), tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString()
                })) {
            tampil();
            emptTeks();
        }
    }

    private void hapus() {
        if (Sequel.queryu2tf("delete from asesmen_awal_asuhan_gizi_ranap where no_rawat=? and tanggal=?", 2, new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString()
        }) == true) {
            tampil();
            emptTeks();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
        }
    }

    private void emptTeksBiokimia() {
        NamaPemeriksaan.setText("");
        Nilai.setText("");
        Interpretasi.setText("");
        TanggalPemeriksaan.setDate(new Date());
        NamaPemeriksaan.requestFocus();
    }

    private void tampilBiokimia() {
        Valid.tabelKosong(tabModeBiokimia);
        try {
            ps = koneksi.prepareStatement("select * from asesmen_awal_asuhan_gizi_ranapbiokimia where asesmen_awal_asuhan_gizi_ranapbiokimia.no_rawat=? order by asesmen_awal_asuhan_gizi_ranapbiokimia.tgl_thn");
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                i = 1;
                while (rs.next()) {
                    tabModeBiokimia.addRow(new String[]{
                        i + "", rs.getString("tgl_thn"), rs.getString("pemeriksaan"), rs.getString("nilai"), rs.getString("interpretasi")
                    });
                    i++;
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

}
