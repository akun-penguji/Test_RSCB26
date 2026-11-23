/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package surat;

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
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import kepegawaian.DlgCariPetugas;
import simrskhanza.DlgPasien;

/**
 *
 * @author windiartohugroho
 */
public final class SuratPernyataanJaminanPasien extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);
    private String finger = "", lokasifile = "", signaturefile = "";
    public DlgPasien pasien = new DlgPasien(null, false);

    public SuratPernyataanJaminanPasien(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(628, 674);

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Pernyataan", "No.Rawat", "No.R.M.", "Nama Pasien", "Umur", "J.K.", "Tgl.Lahir", "Tanggal",
            "Nama Penanggung Jawab", "Tempat Lahir P.J.", "Tgl.Lahir P.J.", "Hubungan", "J.K. P.J.",
            "Nomor Telp/HP", "Nomor KTP", "Alamat P.J.", "NIP", "Nama Petugas", "Kd.Penjab", "Jenis Bayar",
            "Penyakit Diderita" // KOLOM KE-21 (index 20)
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };
        tbObat.setModel(tabMode);

        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // PERBAIKAN: Loop untuk 21 kolom (index 0-20)
        for (i = 0; i < 21; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(105);      // No.Pernyataan
            } else if (i == 1) {
                column.setPreferredWidth(105);      // No.Rawat
            } else if (i == 2) {
                column.setPreferredWidth(70);       // No.R.M.
            } else if (i == 3) {
                column.setPreferredWidth(150);      // Nama Pasien
            } else if (i == 4) {
                column.setPreferredWidth(45);       // Umur
            } else if (i == 5) {
                column.setPreferredWidth(25);       // J.K.
            } else if (i == 6) {
                column.setPreferredWidth(65);       // Tgl.Lahir
            } else if (i == 7) {
                column.setPreferredWidth(65);       // Tanggal
            } else if (i == 8) {
                column.setPreferredWidth(150);      // Nama Penanggung Jawab
            } else if (i == 9) {
                column.setPreferredWidth(100);      // Tempat Lahir P.J.
            } else if (i == 10) {
                column.setPreferredWidth(70);       // Tgl.Lahir P.J.
            } else if (i == 11) {
                column.setPreferredWidth(60);       // Hubungan
            } else if (i == 12) {
                column.setPreferredWidth(45);       // J.K. P.J.
            } else if (i == 13) {
                column.setPreferredWidth(100);      // Nomor Telp/HP
            } else if (i == 14) {
                column.setPreferredWidth(100);      // Nomor KTP
            } else if (i == 15) {
                column.setPreferredWidth(150);      // Alamat P.J.
            } else if (i == 16) {
                column.setPreferredWidth(80);       // NIP
            } else if (i == 17) {
                column.setPreferredWidth(150);      // Nama Petugas
            } else if (i == 18) {
                column.setPreferredWidth(70);       // Kd.Penjab
            } else if (i == 19) {
                column.setPreferredWidth(150);      // Jenis Bayar
            } else if (i == 20) {
                column.setPreferredWidth(200);      // Penyakit Diderita (KOLOM BARU)
            }
        }

        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        NIP.setDocument(new batasInput((byte) 20).getKata(NIP));
        NoSurat.setDocument(new batasInput((byte) 20).getKata(NoSurat));
        TCari.setDocument(new batasInput((int) 100).getKata(TCari));
        NamaPJ.setDocument(new batasInput((byte) 50).getKata(NamaPJ));
        NoKTP.setDocument(new batasInput((byte) 20).getKata(NoKTP));
        AlamatPj.setDocument(new batasInput((int) 100).getKata(AlamatPj));
        TempatLahir.setDocument(new batasInput((byte) 20).getKata(TempatLahir));
        NoTelp.setDocument(new batasInput((byte) 30).getKata(NoTelp));

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
                    NIP.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                    NamaPetugas.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
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

        pasien.penjab.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (akses.getform().equals("DlgReg")) {
                    if (pasien.penjab.getTable().getSelectedRow() != -1) {
                        kdpnj.setText(pasien.penjab.getTable().getValueAt(pasien.penjab.getTable().getSelectedRow(), 1).toString());
                        nmpnj.setText(pasien.penjab.getTable().getValueAt(pasien.penjab.getTable().getSelectedRow(), 2).toString());
                    }
                    kdpnj.requestFocus();
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

        widget.Button btnHistoriCetakSPJP = new widget.Button();
        btnHistoriCetakSPJP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/clock.png")));
        btnHistoriCetakSPJP.setMnemonic('H'); btnHistoriCetakSPJP.setText("Histori Cetak");
        btnHistoriCetakSPJP.setPreferredSize(new java.awt.Dimension(140, 30));
        btnHistoriCetakSPJP.addActionListener((java.awt.event.ActionEvent evt) -> tampilHistoriCetakSPJP());
        panelGlass8.add(btnHistoriCetakSPJP);
        widget.Button btnAuditEHSPJP = new widget.Button();
        btnAuditEHSPJP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/clock.png")));
        btnAuditEHSPJP.setMnemonic('A'); btnAuditEHSPJP.setText("Audit Edit/Hapus");
        btnAuditEHSPJP.setPreferredSize(new java.awt.Dimension(160, 30));
        btnAuditEHSPJP.addActionListener((java.awt.event.ActionEvent evt) -> tampilAuditSPJP());
        panelGlass8.add(btnAuditEHSPJP);
        panelGlass8.revalidate(); panelGlass8.repaint();

        ChkAccor.setSelected(false);
        isPhoto();

        HTMLEditorKit kit = new HTMLEditorKit();
        LoadHTML2.setEditable(true);
        LoadHTML2.setEditorKit(kit);
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
        LoadHTML2.setDocument(doc);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        JK = new widget.TextBox();
        Umur = new widget.TextBox();
        kdpnj = new widget.TextBox();
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
        jLabel4 = new widget.Label();
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        TNoRM = new widget.TextBox();
        jLabel8 = new widget.Label();
        NamaPJ = new widget.TextBox();
        jLabel9 = new widget.Label();
        JKPJ = new widget.ComboBox();
        jLabel10 = new widget.Label();
        jLabel11 = new widget.Label();
        Hubungan = new widget.ComboBox();
        AlamatPj = new widget.TextBox();
        jLabel17 = new widget.Label();
        LahirPasien = new widget.TextBox();
        jLabel18 = new widget.Label();
        NIP = new widget.TextBox();
        NamaPetugas = new widget.TextBox();
        btnPetugas = new widget.Button();
        jLabel16 = new widget.Label();
        TglLahir = new widget.Tanggal();
        jLabel44 = new widget.Label();
        TempatLahir = new widget.TextBox();
        Tanggal = new widget.Tanggal();
        jLabel14 = new widget.Label();
        jLabel3 = new widget.Label();
        NoSurat = new widget.TextBox();
        jLabel15 = new widget.Label();
        NoKTP = new widget.TextBox();
        NoTelp = new widget.TextBox();
        jLabel20 = new widget.Label();
        jLabel22 = new widget.Label();
        nmpnj = new widget.TextBox();
        btnPenjab = new widget.Button();
        jLabel12 = new widget.Label();
        PenyakitDiDerita = new widget.TextBox();
        ChkInput = new widget.CekBox();
        PanelAccor = new widget.PanelBiasa();
        ChkAccor = new widget.CekBox();
        FormPhoto = new widget.PanelBiasa();
        FormPass3 = new widget.PanelBiasa();
        btnAmbil = new widget.Button();
        BtnRefreshPhoto1 = new widget.Button();
        BtnPrint1 = new widget.Button();
        Scroll5 = new widget.ScrollPane();
        LoadHTML2 = new widget.editorpane();

        JK.setHighlighter(null);
        JK.setName("JK"); // NOI18N

        Umur.setHighlighter(null);
        Umur.setName("Umur"); // NOI18N

        kdpnj.setHighlighter(null);
        kdpnj.setName("kdpnj"); // NOI18N
        kdpnj.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdpnjKeyPressed(evt);
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Data Penyataan Jaminan Pasien ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 200));

        tbObat.setAutoCreateRowSorter(true);
        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
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
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "06-07-2025" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "06-07-2025" }));
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
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 230));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(100, 165));
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
        TPasien.setBounds(325, 10, 255, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        TNoRM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRMKeyPressed(evt);
            }
        });
        FormInput.add(TNoRM);
        TNoRM.setBounds(212, 10, 111, 23);

        jLabel8.setText("Hubungan :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(0, 120, 100, 23);

        NamaPJ.setName("NamaPJ"); // NOI18N
        NamaPJ.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NamaPJKeyPressed(evt);
            }
        });
        FormInput.add(NamaPJ);
        NamaPJ.setBounds(104, 90, 260, 23);

        jLabel9.setText("Jenis Kelamin :");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(235, 120, 80, 23);

        JKPJ.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Laki-laki", "Perempuan" }));
        JKPJ.setName("JKPJ"); // NOI18N
        JKPJ.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JKPJKeyPressed(evt);
            }
        });
        FormInput.add(JKPJ);
        JKPJ.setBounds(319, 120, 110, 23);

        jLabel10.setText("Nama :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(0, 90, 100, 23);

        jLabel11.setText("Alamat :");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(260, 150, 55, 23);

        Hubungan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Suami", "Istri", "Anak", "Ayah", "Ibu", "Saudara", "Keponakan" }));
        Hubungan.setName("Hubungan"); // NOI18N
        Hubungan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                HubunganKeyPressed(evt);
            }
        });
        FormInput.add(Hubungan);
        Hubungan.setBounds(104, 120, 110, 23);

        AlamatPj.setName("AlamatPj"); // NOI18N
        AlamatPj.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AlamatPjKeyPressed(evt);
            }
        });
        FormInput.add(AlamatPj);
        AlamatPj.setBounds(319, 150, 414, 23);

        jLabel17.setText("Tgl.Lahir :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(584, 10, 60, 23);

        LahirPasien.setHighlighter(null);
        LahirPasien.setName("LahirPasien"); // NOI18N
        FormInput.add(LahirPasien);
        LahirPasien.setBounds(648, 10, 85, 23);

        jLabel18.setText("Petugas :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(170, 40, 55, 23);

        NIP.setEditable(false);
        NIP.setHighlighter(null);
        NIP.setName("NIP"); // NOI18N
        FormInput.add(NIP);
        NIP.setBounds(229, 40, 100, 23);

        NamaPetugas.setEditable(false);
        NamaPetugas.setName("NamaPetugas"); // NOI18N
        FormInput.add(NamaPetugas);
        NamaPetugas.setBounds(331, 40, 157, 23);

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
        btnPetugas.setBounds(490, 40, 28, 23);

        jLabel16.setText("Tanggal :");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setVerifyInputWhenFocusTarget(false);
        FormInput.add(jLabel16);
        jLabel16.setBounds(0, 40, 70, 23);

        TglLahir.setForeground(new java.awt.Color(50, 70, 50));
        TglLahir.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "06-07-2025" }));
        TglLahir.setDisplayFormat("dd-MM-yyyy");
        TglLahir.setName("TglLahir"); // NOI18N
        TglLahir.setOpaque(false);
        TglLahir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TglLahirKeyPressed(evt);
            }
        });
        FormInput.add(TglLahir);
        TglLahir.setBounds(643, 90, 90, 23);

        jLabel44.setText("Tempat/Tanggal Lahir :");
        jLabel44.setName("jLabel44"); // NOI18N
        FormInput.add(jLabel44);
        jLabel44.setBounds(376, 90, 120, 23);

        TempatLahir.setName("TempatLahir"); // NOI18N
        TempatLahir.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TempatLahirKeyPressed(evt);
            }
        });
        FormInput.add(TempatLahir);
        TempatLahir.setBounds(500, 90, 140, 23);

        Tanggal.setForeground(new java.awt.Color(50, 70, 50));
        Tanggal.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "06-07-2025" }));
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

        jLabel14.setText("Penanggung Jawab Pasien :");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(0, 70, 154, 23);

        jLabel3.setText("No. Pernyataan :");
        jLabel3.setName("jLabel3"); // NOI18N
        FormInput.add(jLabel3);
        jLabel3.setBounds(520, 40, 90, 23);

        NoSurat.setHighlighter(null);
        NoSurat.setName("NoSurat"); // NOI18N
        NoSurat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoSuratKeyPressed(evt);
            }
        });
        FormInput.add(NoSurat);
        NoSurat.setBounds(614, 40, 119, 23);

        jLabel15.setText("Nomor KTP :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(0, 150, 100, 23);

        NoKTP.setName("NoKTP"); // NOI18N
        NoKTP.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoKTPKeyPressed(evt);
            }
        });
        FormInput.add(NoKTP);
        NoKTP.setBounds(104, 150, 150, 23);

        NoTelp.setName("NoTelp"); // NOI18N
        NoTelp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoTelpKeyPressed(evt);
            }
        });
        FormInput.add(NoTelp);
        NoTelp.setBounds(573, 120, 160, 23);

        jLabel20.setText("Nomor Telp/Nomor HP :");
        jLabel20.setName("jLabel20"); // NOI18N
        FormInput.add(jLabel20);
        jLabel20.setBounds(429, 120, 140, 23);

        jLabel22.setText("Jenis Bayar :");
        jLabel22.setName("jLabel22"); // NOI18N
        FormInput.add(jLabel22);
        jLabel22.setBounds(10, 180, 90, 23);

        nmpnj.setEditable(false);
        nmpnj.setName("nmpnj"); // NOI18N
        FormInput.add(nmpnj);
        nmpnj.setBounds(100, 180, 160, 23);

        btnPenjab.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        btnPenjab.setMnemonic('2');
        btnPenjab.setToolTipText("ALt+2");
        btnPenjab.setName("btnPenjab"); // NOI18N
        btnPenjab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPenjabActionPerformed(evt);
            }
        });
        FormInput.add(btnPenjab);
        btnPenjab.setBounds(270, 180, 28, 23);

        jLabel12.setText("Penyakit di derita:");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(305, 180, 90, 23);

        PenyakitDiDerita.setName("PenyakitDiDerita"); // NOI18N
        PenyakitDiDerita.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PenyakitDiDeritaKeyPressed(evt);
            }
        });
        FormInput.add(PenyakitDiDerita);
        PenyakitDiDerita.setBounds(400, 180, 340, 23);

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

        PanelAccor.setBackground(new java.awt.Color(255, 255, 255));
        PanelAccor.setName("PanelAccor"); // NOI18N
        PanelAccor.setPreferredSize(new java.awt.Dimension(430, 43));
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

        FormPhoto.setBackground(new java.awt.Color(255, 255, 255));
        FormPhoto.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), " Bukti Pengambilan Pernyataan : ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        FormPhoto.setName("FormPhoto"); // NOI18N
        FormPhoto.setPreferredSize(new java.awt.Dimension(115, 73));
        FormPhoto.setLayout(new java.awt.BorderLayout());

        FormPass3.setBackground(new java.awt.Color(255, 255, 255));
        FormPass3.setBorder(null);
        FormPass3.setName("FormPass3"); // NOI18N
        FormPass3.setPreferredSize(new java.awt.Dimension(115, 40));

        btnAmbil.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/plus_16.png"))); // NOI18N
        btnAmbil.setMnemonic('U');
        btnAmbil.setText("Ambil");
        btnAmbil.setToolTipText("Alt+U");
        btnAmbil.setName("btnAmbil"); // NOI18N
        btnAmbil.setPreferredSize(new java.awt.Dimension(100, 30));
        btnAmbil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAmbilActionPerformed(evt);
            }
        });
        FormPass3.add(btnAmbil);

        BtnRefreshPhoto1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/refresh.png"))); // NOI18N
        BtnRefreshPhoto1.setMnemonic('U');
        BtnRefreshPhoto1.setText("Refresh");
        BtnRefreshPhoto1.setToolTipText("Alt+U");
        BtnRefreshPhoto1.setName("BtnRefreshPhoto1"); // NOI18N
        BtnRefreshPhoto1.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnRefreshPhoto1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnRefreshPhoto1ActionPerformed(evt);
            }
        });
        FormPass3.add(BtnRefreshPhoto1);

        BtnPrint1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item (copy).png"))); // NOI18N
        BtnPrint1.setMnemonic('T');
        BtnPrint1.setText("Surat");
        BtnPrint1.setToolTipText("Alt+T");
        BtnPrint1.setName("BtnPrint1"); // NOI18N
        BtnPrint1.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnPrint1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPrint1ActionPerformed(evt);
            }
        });
        FormPass3.add(BtnPrint1);

        FormPhoto.add(FormPass3, java.awt.BorderLayout.PAGE_END);

        Scroll5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        Scroll5.setName("Scroll5"); // NOI18N
        Scroll5.setOpaque(true);
        Scroll5.setPreferredSize(new java.awt.Dimension(200, 200));

        LoadHTML2.setBorder(null);
        LoadHTML2.setName("LoadHTML2"); // NOI18N
        Scroll5.setViewportView(LoadHTML2);

        FormPhoto.add(Scroll5, java.awt.BorderLayout.CENTER);

        PanelAccor.add(FormPhoto, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelAccor, java.awt.BorderLayout.EAST);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
        internalFrame1.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "Pasien");
        } else if (NamaPJ.getText().trim().equals("")) {
            Valid.textKosong(NamaPJ, "Nama Penanggung Jawab");
        } else if (AlamatPj.getText().trim().equals("")) {
            Valid.textKosong(AlamatPj, "Alamat Penanggung Jawab");
        } else if (TempatLahir.getText().trim().equals("")) {
            Valid.textKosong(TempatLahir, "Tempat Lahir");
        } else if (NoTelp.getText().trim().equals("")) {
            Valid.textKosong(NoTelp, "Nomor Telp");
        } else if (NoKTP.getText().trim().equals("")) {
            Valid.textKosong(NoKTP, "Nomor KTP");
        } else if (NamaPetugas.getText().trim().equals("")) {
            Valid.textKosong(NamaPetugas, "Petugas");
        } else if (NoSurat.getText().trim().equals("")) {
            Valid.textKosong(NoSurat, "No.Pernyataan");
        } else if (kdpnj.getText().trim().equals("")) {
            Valid.textKosong(kdpnj, "Kode Penjab");
        } else if (!isValidKdPenjab(kdpnj.getText())) {
            JOptionPane.showMessageDialog(null, "Kode Penjab tidak valid..!!");
            kdpnj.requestFocus();
        } else {
            if (Sequel.menyimpantf("surat_pernyataan_jaminan_pasien", "?,?,?,?,?,?,?,?,?,?,?,?,?,?", "Data", 14, new String[]{
                NoSurat.getText(), TNoRw.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + ""), NamaPJ.getText(), NoKTP.getText(),
                TempatLahir.getText(), Valid.SetTgl(TglLahir.getSelectedItem() + ""), JKPJ.getSelectedItem().toString().substring(0, 1),
                AlamatPj.getText(), Hubungan.getSelectedItem().toString(), NoTelp.getText(), NIP.getText(), kdpnj.getText(), PenyakitDiDerita.getText()
            }) == true) {
                tabMode.addRow(new String[]{
                    NoSurat.getText(), TNoRw.getText(), TNoRM.getText(), TPasien.getText(), Umur.getText(), JK.getText(), LahirPasien.getText(),
                    Valid.SetTgl(Tanggal.getSelectedItem() + ""), NamaPJ.getText(), TempatLahir.getText(), Valid.SetTgl(TglLahir.getSelectedItem() + ""),
                    Hubungan.getSelectedItem().toString(), JKPJ.getSelectedItem().toString().substring(0, 1), NoTelp.getText(), NoKTP.getText(),
                    AlamatPj.getText(), NIP.getText(), NamaPetugas.getText(), kdpnj.getText(), nmpnj.getText(), PenyakitDiDerita.getText()
                });
                LCount.setText("" + tabMode.getRowCount());
                emptTeks();
            }
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, AlamatPj, BtnBatal);
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
                if (NIP.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 16).toString())) {
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
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "Pasien");
        } else if (NamaPJ.getText().trim().equals("")) {
            Valid.textKosong(NamaPJ, "Nama Penanggung Jawab");
        } else if (AlamatPj.getText().trim().equals("")) {
            Valid.textKosong(AlamatPj, "Alamat Penanggung Jawab");
        } else if (TempatLahir.getText().trim().equals("")) {
            Valid.textKosong(TempatLahir, "Tempat Lahir");
        } else if (NoTelp.getText().trim().equals("")) {
            Valid.textKosong(NoTelp, "Nomor Telp");
        } else if (NoKTP.getText().trim().equals("")) {
            Valid.textKosong(NoKTP, "Nomor KTP");
        } else if (NamaPetugas.getText().trim().equals("")) {
            Valid.textKosong(NamaPetugas, "Petugas");
        } else if (NoSurat.getText().trim().equals("")) {
            Valid.textKosong(NoSurat, "No.Pernyataan");
        } else if (kdpnj.getText().trim().equals("")) {
            Valid.textKosong(kdpnj, "Kode Penjab");
        } else if (!isValidKdPenjab(kdpnj.getText())) {
            JOptionPane.showMessageDialog(null, "Kode Penjab tidak valid..!!");
            kdpnj.requestFocus();
        } else {
            if (tbObat.getSelectedRow() > -1) {
                if (akses.getkode().equals("Admin Utama")) {
                    ganti();
                } else {
                    if (NIP.getText().equals(tbObat.getValueAt(tbObat.getSelectedRow(), 16).toString())) {
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
        petugas.dispose();
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

            if (TCari.getText().trim().equals("")) {
                Valid.MyReportqry("rptDataFormulirPernyataanPasienUmum.jasper", "report", "::[ Data Formulir Pernyataan Pasien Umum ]::",
                        "select surat_pernyataan_jaminan_pasien.no_surat,reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,"
                        + "reg_periksa.sttsumur,pasien.jk,pasien.tgl_lahir,surat_pernyataan_jaminan_pasien.tanggal,surat_pernyataan_jaminan_pasien.nama_pj,"
                        + "surat_pernyataan_jaminan_pasien.no_ktppj,surat_pernyataan_jaminan_pasien.tempat_lahirpj,surat_pernyataan_jaminan_pasien.lahirpj,"
                        + "surat_pernyataan_jaminan_pasien.jkpj,surat_pernyataan_jaminan_pasien.alamatpj,surat_pernyataan_jaminan_pasien.hubungan,"
                        + "surat_pernyataan_jaminan_pasien.no_telp,surat_pernyataan_jaminan_pasien.nip,petugas.nama,penjab.png_jawab from surat_pernyataan_jaminan_pasien "
                        + "inner join reg_periksa on surat_pernyataan_jaminan_pasien.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "inner join petugas on surat_pernyataan_jaminan_pasien.nip=petugas.nip "
                        + "inner join penjab on surat_pernyataan_jaminan_pasien.kd_pj=penjab.kd_pj where "
                        + "surat_pernyataan_jaminan_pasien.tanggal between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' order by surat_pernyataan_jaminan_pasien.tanggal", param);
            } else {
                Valid.MyReportqry("rptDataFormulirPernyataanPasienUmum.jasper", "report", "::[ Data Formulir Pernyataan Pasien Umum ]::",
                        "select surat_pernyataan_jaminan_pasien.no_surat,reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,"
                        + "reg_periksa.sttsumur,pasien.jk,pasien.tgl_lahir,surat_pernyataan_jaminan_pasien.tanggal,surat_pernyataan_jaminan_pasien.nama_pj,"
                        + "surat_pernyataan_jaminan_pasien.no_ktppj,surat_pernyataan_jaminan_pasien.tempat_lahirpj,surat_pernyataan_jaminan_pasien.lahirpj,"
                        + "surat_pernyataan_jaminan_pasien.jkpj,surat_pernyataan_jaminan_pasien.alamatpj,surat_pernyataan_jaminan_pasien.hubungan,"
                        + "surat_pernyataan_jaminan_pasien.no_telp,surat_pernyataan_jaminan_pasien.nip,petugas.nama,penjab.png_jawab from surat_pernyataan_jaminan_pasien "
                        + "inner join reg_periksa on surat_pernyataan_jaminan_pasien.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "inner join petugas on surat_pernyataan_jaminan_pasien.nip=petugas.nip "
                        + "inner join penjab on surat_pernyataan_jaminan_pasien.kd_pj=penjab.kd_pj where "
                        + "surat_pernyataan_jaminan_pasien.tanggal between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' and "
                        + "(reg_periksa.no_rawat like '%" + TCari.getText().trim() + "%' or pasien.no_rkm_medis like '%" + TCari.getText().trim() + "%' or pasien.nm_pasien like '%" + TCari.getText().trim() + "%' or "
                        + "surat_pernyataan_jaminan_pasien.alamatpj like '%" + TCari.getText().trim() + "%' or surat_pernyataan_jaminan_pasien.nama_pj like '%" + TCari.getText().trim() + "%' or "
                        + "surat_pernyataan_jaminan_pasien.nip like '%" + TCari.getText().trim() + "%' or petugas.nama like '%" + TCari.getText().trim() + "%' or penjab.png_jawab like '%" + TCari.getText().trim() + "%') "
                        + "order by surat_pernyataan_jaminan_pasien.tanggal", param);
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
            try {
                isPhoto();
                panggilPhoto();
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

    private void btnPetugasKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnPetugasKeyPressed
        Valid.pindah(evt, Tanggal, NamaPJ);
    }//GEN-LAST:event_btnPetugasKeyPressed

    private void btnPetugasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPetugasActionPerformed
        petugas.emptTeks();
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
    }//GEN-LAST:event_btnPetugasActionPerformed

    private void AlamatPjKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AlamatPjKeyPressed
        Valid.pindah(evt, NoKTP, BtnSimpan);
    }//GEN-LAST:event_AlamatPjKeyPressed

    private void HubunganKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_HubunganKeyPressed
        Valid.pindah(evt, TglLahir, JKPJ);
    }//GEN-LAST:event_HubunganKeyPressed

    private void JKPJKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JKPJKeyPressed
        Valid.pindah(evt, Hubungan, NoTelp);
    }//GEN-LAST:event_JKPJKeyPressed

    private void NamaPJKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NamaPJKeyPressed
        Valid.pindah(evt, NoSurat, TempatLahir);
    }//GEN-LAST:event_NamaPJKeyPressed

    private void TNoRMKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRMKeyPressed
        // Valid.pindah(evt, TNm, BtnSimpan);
    }//GEN-LAST:event_TNoRMKeyPressed

    private void TPasienKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TPasienKeyPressed
        Valid.pindah(evt, TCari, BtnSimpan);
    }//GEN-LAST:event_TPasienKeyPressed

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            isRawat();
        } else {
            Valid.pindah(evt, TCari, Tanggal);
        }
    }//GEN-LAST:event_TNoRwKeyPressed

    private void TglLahirKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TglLahirKeyPressed
        Valid.pindah2(evt, TempatLahir, Hubungan);
    }//GEN-LAST:event_TglLahirKeyPressed

    private void TanggalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalKeyPressed
        //Valid.pindah(evt,TCari,Jam);
    }//GEN-LAST:event_TanggalKeyPressed

    private void NoSuratKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoSuratKeyPressed
        Valid.pindah(evt, btnPetugas, NamaPJ);
    }//GEN-LAST:event_NoSuratKeyPressed

    private void NoKTPKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoKTPKeyPressed
        Valid.pindah(evt, NoTelp, AlamatPj);
    }//GEN-LAST:event_NoKTPKeyPressed

    private void TempatLahirKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TempatLahirKeyPressed
        Valid.pindah(evt, NamaPJ, TglLahir);
    }//GEN-LAST:event_TempatLahirKeyPressed

    private void NoTelpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoTelpKeyPressed
        Valid.pindah(evt, JKPJ, NoKTP);
    }//GEN-LAST:event_NoTelpKeyPressed

    private void ChkAccorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkAccorActionPerformed
        if (tbObat.getSelectedRow() != -1) {
            isPhoto();
            panggilPhoto();
        } else {
            ChkAccor.setSelected(false);
            JOptionPane.showMessageDialog(null, "Silahkan pilih No.Pernyataan..!!!");
        }
    }//GEN-LAST:event_ChkAccorActionPerformed

    private void btnAmbilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAmbilActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() > -1) {
                // Menggunakan tabel antrijaminanpasien untuk antrian jaminan
                Sequel.queryu("delete from antrijaminanpasien");
                Sequel.queryu("insert into antrijaminanpasien values('" + tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "','" + tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString() + "')");
                Sequel.queryu("delete from surat_pernyataan_jaminan_pasien_pembuat_pernyataan where no_surat='" + tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "'");
            } else {
                JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih No.Pernyataan terlebih dahulu..!!");
            }
        }
    }//GEN-LAST:event_btnAmbilActionPerformed

    private void BtnRefreshPhoto1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnRefreshPhoto1ActionPerformed
        if (tbObat.getSelectedRow() > -1) {
            panggilPhoto();
        } else {
            JOptionPane.showMessageDialog(rootPane, "Silahkan anda pilih No.Pernyataan terlebih dahulu..!!");
        }
    }//GEN-LAST:event_BtnRefreshPhoto1ActionPerformed

    private void BtnPrint1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrint1ActionPerformed
        if (tbObat.getSelectedRow() > -1) {
            if (lokasifile.equals("")) {
                JOptionPane.showMessageDialog(null, "Maaf, Silahkan ambil photo bukti pernyataan pasien umum terlebih dahulu..!!!!");
            } else {
                Sequel.menyimpan("log_cetak_surat_pernyataan_jaminan_pasien","null,?,?,?,?,current_date(),current_time()",4,new String[]{
                    tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),1).toString(),"Surat Pernyataan Jaminan Pasien",akses.getkode()
                });
                Map<String, Object> param = new HashMap<>();
                param.put("namars", akses.getnamars());
                param.put("alamatrs", akses.getalamatrs());
                param.put("kotars", akses.getkabupatenrs());
                param.put("propinsirs", akses.getpropinsirs());
                param.put("kontakrs", akses.getkontakrs());
                param.put("emailrs", akses.getemailrs());
                param.put("logo", Sequel.cariGambar("select setting.logo from setting"));
                param.put("photo", "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/suratpernyataanjaminan/" + lokasifile);
                param.put("signature", signaturefile.equals("") ? "" : "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/suratpernyataanjaminan/" + signaturefile);
                finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", tbObat.getValueAt(tbObat.getSelectedRow(), 16).toString());
                param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbObat.getValueAt(tbObat.getSelectedRow(), 17).toString() + "\nID " + (finger.equals("") ? tbObat.getValueAt(tbObat.getSelectedRow(), 16).toString() : finger) + "\n" + Valid.SetTgl3(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString()));
                Valid.MyReportqry("rptSuratPernyataanPasienUmum.jasper", "report", "::[ Surat Pernyataan Pasien Umum ]::",
                        "select surat_pernyataan_jaminan_pasien.no_surat,reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,pasien.pekerjaan,pasien.tmp_lahir,concat(pasien.alamat,', ',kelurahan.nm_kel,', ',kecamatan.nm_kec,', ',kabupaten.nm_kab,', ',propinsi.nm_prop) as alamat,reg_periksa.sttsumur,pasien.jk,pasien.tgl_lahir,surat_pernyataan_jaminan_pasien.tanggal,surat_pernyataan_jaminan_pasien.nama_pj,"
                        + "surat_pernyataan_jaminan_pasien.no_ktppj,surat_pernyataan_jaminan_pasien.tempat_lahirpj,surat_pernyataan_jaminan_pasien.lahirpj,surat_pernyataan_jaminan_pasien.jkpj,surat_pernyataan_jaminan_pasien.alamatpj,surat_pernyataan_jaminan_pasien.hubungan,surat_pernyataan_jaminan_pasien.no_telp,surat_pernyataan_jaminan_pasien.nip,petugas.nama,penjab.png_jawab from surat_pernyataan_jaminan_pasien inner join reg_periksa on surat_pernyataan_jaminan_pasien.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis inner join petugas on surat_pernyataan_jaminan_pasien.nip=petugas.nip inner join kelurahan on pasien.kd_kel=kelurahan.kd_kel inner join kecamatan on pasien.kd_kec=kecamatan.kd_kec inner join kabupaten on pasien.kd_kab=kabupaten.kd_kab inner join propinsi on pasien.kd_prop=propinsi.kd_prop "
                        + "inner join penjab on surat_pernyataan_jaminan_pasien.kd_pj=penjab.kd_pj "
                        + "where surat_pernyataan_jaminan_pasien.no_surat='" + tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "'", param);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data terlebih dahulu..!!!!");
        }
    }//GEN-LAST:event_BtnPrint1ActionPerformed

    private void btnPenjabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPenjabActionPerformed
        akses.setform("DlgReg");
        pasien.penjab.onCari();
        pasien.penjab.isCek();
        pasien.penjab.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        pasien.penjab.setLocationRelativeTo(internalFrame1);
        pasien.penjab.setVisible(true);
    }//GEN-LAST:event_btnPenjabActionPerformed

    private void kdpnjKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdpnjKeyPressed
    }//GEN-LAST:event_kdpnjKeyPressed

    private void PenyakitDiDeritaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PenyakitDiDeritaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_PenyakitDiDeritaKeyPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            SuratPernyataanJaminanPasien dialog = new SuratPernyataanJaminanPasien(new javax.swing.JFrame(), true);
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
    private widget.TextBox AlamatPj;
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnPrint1;
    private widget.Button BtnRefreshPhoto1;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkAccor;
    private widget.CekBox ChkInput;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.PanelBiasa FormInput;
    private widget.PanelBiasa FormPass3;
    private widget.PanelBiasa FormPhoto;
    private widget.ComboBox Hubungan;
    private widget.TextBox JK;
    private widget.ComboBox JKPJ;
    private widget.Label LCount;
    private widget.TextBox LahirPasien;
    private widget.editorpane LoadHTML2;
    private widget.TextBox NIP;
    private widget.TextBox NamaPJ;
    private widget.TextBox NamaPetugas;
    private widget.TextBox NoKTP;
    private widget.TextBox NoSurat;
    private widget.TextBox NoTelp;
    private widget.PanelBiasa PanelAccor;
    private javax.swing.JPanel PanelInput;
    private widget.TextBox PenyakitDiDerita;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll5;
    private widget.TextBox TCari;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private widget.Tanggal Tanggal;
    private widget.TextBox TempatLahir;
    private widget.Tanggal TglLahir;
    private widget.TextBox Umur;
    private widget.Button btnAmbil;
    private widget.Button btnPenjab;
    private widget.Button btnPetugas;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel14;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel20;
    private widget.Label jLabel21;
    private widget.Label jLabel22;
    private widget.Label jLabel3;
    private widget.Label jLabel4;
    private widget.Label jLabel44;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.Label jLabel9;
    private javax.swing.JPanel jPanel3;
    private widget.TextBox kdpnj;
    private widget.TextBox nmpnj;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    // 3. UPDATE method tampil() - Tambahkan kolom penyakit_diderita di query
    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            if (TCari.getText().trim().equals("")) {
                ps = koneksi.prepareStatement(
                        "select surat_pernyataan_jaminan_pasien.no_surat,reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,"
                        + "reg_periksa.sttsumur,pasien.jk,pasien.tgl_lahir,surat_pernyataan_jaminan_pasien.tanggal,surat_pernyataan_jaminan_pasien.nama_pj,"
                        + "surat_pernyataan_jaminan_pasien.tempat_lahirpj,surat_pernyataan_jaminan_pasien.lahirpj,"
                        + "surat_pernyataan_jaminan_pasien.jkpj,surat_pernyataan_jaminan_pasien.alamatpj,surat_pernyataan_jaminan_pasien.hubungan,"
                        + "surat_pernyataan_jaminan_pasien.no_telp,surat_pernyataan_jaminan_pasien.no_ktppj,surat_pernyataan_jaminan_pasien.nip,petugas.nama,surat_pernyataan_jaminan_pasien.kd_pj,penjab.png_jawab,surat_pernyataan_jaminan_pasien.penyakit_diderita "
                        + "from surat_pernyataan_jaminan_pasien "
                        + "inner join reg_periksa on surat_pernyataan_jaminan_pasien.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "inner join petugas on surat_pernyataan_jaminan_pasien.nip=petugas.nip "
                        + "inner join penjab on surat_pernyataan_jaminan_pasien.kd_pj=penjab.kd_pj where "
                        + "surat_pernyataan_jaminan_pasien.tanggal between ? and ? order by surat_pernyataan_jaminan_pasien.tanggal");
            } else {
                ps = koneksi.prepareStatement(
                        "select surat_pernyataan_jaminan_pasien.no_surat,reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,reg_periksa.umurdaftar,"
                        + "reg_periksa.sttsumur,pasien.jk,pasien.tgl_lahir,surat_pernyataan_jaminan_pasien.tanggal,surat_pernyataan_jaminan_pasien.nama_pj,"
                        + "surat_pernyataan_jaminan_pasien.tempat_lahirpj,surat_pernyataan_jaminan_pasien.lahirpj,"
                        + "surat_pernyataan_jaminan_pasien.jkpj,surat_pernyataan_jaminan_pasien.alamatpj,surat_pernyataan_jaminan_pasien.hubungan,"
                        + "surat_pernyataan_jaminan_pasien.no_telp,surat_pernyataan_jaminan_pasien.no_ktppj,surat_pernyataan_jaminan_pasien.nip,petugas.nama,surat_pernyataan_jaminan_pasien.kd_pj,penjab.png_jawab,surat_pernyataan_jaminan_pasien.penyakit_diderita "
                        + "from surat_pernyataan_jaminan_pasien "
                        + "inner join reg_periksa on surat_pernyataan_jaminan_pasien.no_rawat=reg_periksa.no_rawat "
                        + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                        + "inner join petugas on surat_pernyataan_jaminan_pasien.nip=petugas.nip "
                        + "inner join penjab on surat_pernyataan_jaminan_pasien.kd_pj=penjab.kd_pj where "
                        + "surat_pernyataan_jaminan_pasien.tanggal between ? and ? and "
                        + "(reg_periksa.no_rawat like ? or pasien.no_rkm_medis like ? or pasien.nm_pasien like ? or "
                        + "surat_pernyataan_jaminan_pasien.alamatpj like ? or surat_pernyataan_jaminan_pasien.nama_pj like ? or "
                        + "surat_pernyataan_jaminan_pasien.nip like ? or petugas.nama like ? or penjab.png_jawab like ?) "
                        + "order by surat_pernyataan_jaminan_pasien.tanggal");
            }

            try {
                if (TCari.getText().toString().trim().equals("")) {
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
                }

                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString("no_surat"), rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("umurdaftar") + " " + rs.getString("sttsumur"), rs.getString("jk"), rs.getString("tgl_lahir"),
                        rs.getString("tanggal"), rs.getString("nama_pj"), rs.getString("tempat_lahirpj"), rs.getString("lahirpj"),
                        rs.getString("hubungan"), rs.getString("jkpj"), rs.getString("no_telp"), rs.getString("no_ktppj"),
                        rs.getString("alamatpj"), rs.getString("nip"), rs.getString("nama"), rs.getString("kd_pj"), rs.getString("png_jawab"),
                        rs.getString("penyakit_diderita") != null ? rs.getString("penyakit_diderita") : ""
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
        NamaPJ.setText("");
        TempatLahir.setText("");
        TglLahir.setDate(new Date());
        Hubungan.setSelectedIndex(0);
        JKPJ.setSelectedIndex(0);
        NoTelp.setText("");
        NoKTP.setText("");
        AlamatPj.setText("");
        kdpnj.setText("");
        nmpnj.setText("");
        PenyakitDiDerita.setText(""); // Tambahkan ini
        Valid.autoNomer3("select ifnull(MAX(CONVERT(RIGHT(surat_pernyataan_jaminan_pasien.no_surat,3),signed)),0) from surat_pernyataan_jaminan_pasien where surat_pernyataan_jaminan_pasien.tanggal='" + Valid.SetTgl(Tanggal.getSelectedItem() + "") + "' ",
                "PJP" + Tanggal.getSelectedItem().toString().substring(6, 10) + Tanggal.getSelectedItem().toString().substring(3, 5) + Tanggal.getSelectedItem().toString().substring(0, 2), 3, NoSurat);
        NamaPJ.requestFocus();
    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            NoSurat.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 3).toString());
            Umur.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 4).toString());
            JK.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
            LahirPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString());
            NamaPJ.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString());
            TempatLahir.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString());
            Hubungan.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString());
            JKPJ.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString().replaceAll("L", "Laki-laki").replaceAll("P", "Perempuan"));
            NoTelp.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 13).toString());
            NoKTP.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 14).toString());
            AlamatPj.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 15).toString());
            kdpnj.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 18).toString());
            nmpnj.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 19).toString());
            // Tambahkan ini:
            PenyakitDiDerita.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 20) != null
                    ? tbObat.getValueAt(tbObat.getSelectedRow(), 20).toString() : "");
            Valid.SetTgl(Tanggal, tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString());
            Valid.SetTgl(TglLahir, tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString());
        }
    }

    private void isRawat() {
        try {
            ps = koneksi.prepareStatement(
                    "select reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.jk,pasien.tgl_lahir,reg_periksa.tgl_registrasi,"
                    + "reg_periksa.umurdaftar,reg_periksa.sttsumur,reg_periksa.kd_pj,penjab.png_jawab from reg_periksa "
                    + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                    + "inner join penjab on reg_periksa.kd_pj=penjab.kd_pj "
                    + "where reg_periksa.no_rawat=?");
            try {
                ps.setString(1, TNoRw.getText());
                rs = ps.executeQuery();
                if (rs.next()) {
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    DTPCari1.setDate(rs.getDate("tgl_registrasi"));
                    TPasien.setText(rs.getString("nm_pasien"));
                    JK.setText(rs.getString("jk"));
                    LahirPasien.setText(rs.getString("tgl_lahir"));
                    Umur.setText(rs.getString("umurdaftar") + " " + rs.getString("sttsumur"));
                    kdpnj.setText(rs.getString("kd_pj"));
                    nmpnj.setText(rs.getString("png_jawab"));
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
            PanelInput.setPreferredSize(new Dimension(WIDTH, 230));
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
        BtnSimpan.setEnabled(akses.getsurat_pernyataan_pasien_umum());
        BtnHapus.setEnabled(akses.getsurat_pernyataan_pasien_umum());
        BtnEdit.setEnabled(akses.getsurat_pernyataan_pasien_umum());
        BtnPrint.setEnabled(akses.getsurat_pernyataan_pasien_umum());
        if (akses.getjml2() >= 1) {
            NIP.setEditable(false);
            btnPetugas.setEnabled(false);
            NIP.setText(akses.getkode());
            NamaPetugas.setText(petugas.tampil3(NIP.getText()));
            if (NamaPetugas.getText().equals("")) {
                NIP.setText("");
                JOptionPane.showMessageDialog(null, "User login bukan petugas...!!");
            }
        }
    }

    private void tampilHistoriCetakSPJP() {
        final javax.swing.JDialog dlg = new javax.swing.JDialog(this, "Histori Pencetakan Surat Pernyataan Jaminan Pasien", true);
        dlg.setSize(820, 480); dlg.setLocationRelativeTo(this); dlg.setLayout(new java.awt.BorderLayout(6, 6));
        final DefaultTableModel mdl = new DefaultTableModel(null, new Object[]{"No.Surat","No.Rawat","Jenis","User","Tanggal","Jam"}) { @Override public boolean isCellEditable(int r, int c){ return false; } };
        final JTable tbl = new JTable(mdl); tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {150,120,200,120,100,90};
        for (int i = 0; i < widths.length; i++) tbl.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        javax.swing.JPanel top = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 5));
        top.add(new javax.swing.JLabel("Cari:"));
        final javax.swing.JTextField fCari = new javax.swing.JTextField(20); fCari.setText(TNoRw.getText()); top.add(fCari);
        final javax.swing.JButton bRefresh = new javax.swing.JButton("Refresh"); top.add(bRefresh);
        final javax.swing.JLabel lblInfo = new javax.swing.JLabel(""); top.add(lblInfo);
        Runnable loader = () -> {
            mdl.setRowCount(0);
            String key = "%" + fCari.getText().trim() + "%";
            String sql = "select no_surat,no_rawat,jenis_surat,kd_user,tanggal,jam from log_cetak_surat_pernyataan_jaminan_pasien where no_surat like ? or no_rawat like ? or kd_user like ? order by tanggal desc, jam desc limit 500";
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

    private void tampilAuditSPJP() {
        final javax.swing.JDialog dlg = new javax.swing.JDialog(this, "Audit Edit & Hapus Surat Pernyataan Jaminan Pasien", true);
        dlg.setSize(1200, 500); dlg.setLocationRelativeTo(this); dlg.setLayout(new java.awt.BorderLayout(6, 6));
        final DefaultTableModel mdl = new DefaultTableModel(null, new Object[]{"Aksi","User","Tgl Aksi","Jam","No.Surat","No.Rawat","Tgl","Nama PJ","No.KTP","Tmp Lahir","Lahir","Jk","Alamat","Hub","Telp","NIP","Kd PJ","Penyakit"}) { @Override public boolean isCellEditable(int r, int c){ return false; } };
        final JTable tbl = new JTable(mdl); tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] widths = {60,90,100,80,140,120,90,120,110,80,80,40,140,90,90,90,50,140};
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
            String sql = "select aksi,kd_user,tanggal_aksi,jam_aksi,no_surat,no_rawat,tanggal,nama_pj,no_ktppj,tempat_lahirpj,lahirpj,jkpj,alamatpj,hubungan,no_telp,nip,kd_pj,penyakit_diderita from surat_pernyataan_jaminan_pasien_audit where (no_surat like ? or coalesce(no_rawat,'') like ? or kd_user like ?) " + (aksiSel.equals("SEMUA") ? "" : "and aksi=? ") + "order by tanggal_aksi desc, jam_aksi desc limit 500";
            PreparedStatement p = null; ResultSet r = null;
            try { p = koneksiDB.condb().prepareStatement(sql); p.setString(1, key); p.setString(2, key); p.setString(3, key); if (!aksiSel.equals("SEMUA")) p.setString(4, aksiSel); r = p.executeQuery(); int n = 0; while (r.next()) { Object[] row = new Object[18]; for (int i = 0; i < 18; i++) row[i] = r.getString(i + 1); mdl.addRow(row); n++; } lblInfo.setText("  " + n + " baris");
            } catch (Exception ex) { JOptionPane.showMessageDialog(dlg, "Gagal: " + ex.getMessage()); }
            finally { try { if (r != null) r.close(); } catch (Exception ignore) {} try { if (p != null) p.close(); } catch (Exception ignore) {} }
        };
        bRefresh.addActionListener(e -> loader.run()); fCari.addActionListener(e -> loader.run()); cbAksi.addActionListener(e -> loader.run());
        javax.swing.JButton bClose = new javax.swing.JButton("Tutup"); bClose.addActionListener(e -> dlg.dispose());
        javax.swing.JPanel bottom = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 5)); bottom.add(bClose);
        dlg.add(top, java.awt.BorderLayout.NORTH); dlg.add(new javax.swing.JScrollPane(tbl), java.awt.BorderLayout.CENTER); dlg.add(bottom, java.awt.BorderLayout.SOUTH);
        loader.run(); dlg.setVisible(true);
    }

    private void snapshotSPJPAudit(String noSuratLama, String aksi) {
        PreparedStatement pSel = null, pIns = null; ResultSet rSel = null;
        try {
            pSel = koneksiDB.condb().prepareStatement("select no_surat,no_rawat,tanggal,nama_pj,no_ktppj,tempat_lahirpj,lahirpj,jkpj,alamatpj,hubungan,no_telp,nip,kd_pj,penyakit_diderita from surat_pernyataan_jaminan_pasien where no_surat=?");
            pSel.setString(1, noSuratLama); rSel = pSel.executeQuery();
            if (rSel.next()) {
                pIns = koneksiDB.condb().prepareStatement("insert into surat_pernyataan_jaminan_pasien_audit(aksi,kd_user,tanggal_aksi,jam_aksi,no_surat,no_rawat,tanggal,nama_pj,no_ktppj,tempat_lahirpj,lahirpj,jkpj,alamatpj,hubungan,no_telp,nip,kd_pj,penyakit_diderita) values(?,?,current_date(),current_time(),?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                pIns.setString(1, aksi); pIns.setString(2, akses.getkode());
                for (int k = 1; k <= 14; k++) pIns.setString(k + 2, rSel.getString(k));
                pIns.executeUpdate();
            }
        } catch (Exception ex) { System.out.println("snapshotSPJPAudit gagal: " + ex); }
        finally { try { if (rSel != null) rSel.close(); } catch (Exception ignore) {} try { if (pSel != null) pSel.close(); } catch (Exception ignore) {} try { if (pIns != null) pIns.close(); } catch (Exception ignore) {} }
    }

    private void ganti() {
        snapshotSPJPAudit(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),"EDIT");
        if (Sequel.mengedittf("surat_pernyataan_jaminan_pasien", "no_surat=?", "no_surat=?,no_rawat=?,tanggal=?,nama_pj=?,no_ktppj=?,tempat_lahirpj=?,lahirpj=?,jkpj=?,alamatpj=?,hubungan=?,no_telp=?,nip=?,kd_pj=?,penyakit_diderita=?", 15, new String[]{
            NoSurat.getText(), TNoRw.getText(), Valid.SetTgl(Tanggal.getSelectedItem() + ""), NamaPJ.getText(), NoKTP.getText(),
            TempatLahir.getText(), Valid.SetTgl(TglLahir.getSelectedItem() + ""), JKPJ.getSelectedItem().toString().substring(0, 1),
            AlamatPj.getText(), Hubungan.getSelectedItem().toString(), NoTelp.getText(), NIP.getText(), kdpnj.getText(), PenyakitDiDerita.getText(), // Tambahkan ini
            tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
        }) == true) {
            // Update semua setValue di tabel
            tbObat.setValueAt(NoSurat.getText(), tbObat.getSelectedRow(), 0);
            tbObat.setValueAt(TNoRw.getText(), tbObat.getSelectedRow(), 1);
            tbObat.setValueAt(TNoRM.getText(), tbObat.getSelectedRow(), 2);
            tbObat.setValueAt(TPasien.getText(), tbObat.getSelectedRow(), 3);
            tbObat.setValueAt(Umur.getText(), tbObat.getSelectedRow(), 4);
            tbObat.setValueAt(JK.getText(), tbObat.getSelectedRow(), 5);
            tbObat.setValueAt(LahirPasien.getText(), tbObat.getSelectedRow(), 6);
            tbObat.setValueAt(Valid.SetTgl(Tanggal.getSelectedItem() + ""), tbObat.getSelectedRow(), 7);
            tbObat.setValueAt(NamaPJ.getText(), tbObat.getSelectedRow(), 8);
            tbObat.setValueAt(TempatLahir.getText(), tbObat.getSelectedRow(), 9);
            tbObat.setValueAt(Valid.SetTgl(TglLahir.getSelectedItem() + ""), tbObat.getSelectedRow(), 10);
            tbObat.setValueAt(Hubungan.getSelectedItem().toString(), tbObat.getSelectedRow(), 11);
            tbObat.setValueAt(JKPJ.getSelectedItem().toString().substring(0, 1), tbObat.getSelectedRow(), 12);
            tbObat.setValueAt(NoTelp.getText(), tbObat.getSelectedRow(), 13);
            tbObat.setValueAt(NoKTP.getText(), tbObat.getSelectedRow(), 14);
            tbObat.setValueAt(AlamatPj.getText(), tbObat.getSelectedRow(), 15);
            tbObat.setValueAt(NIP.getText(), tbObat.getSelectedRow(), 16);
            tbObat.setValueAt(NamaPetugas.getText(), tbObat.getSelectedRow(), 17);
            tbObat.setValueAt(kdpnj.getText(), tbObat.getSelectedRow(), 18);
            tbObat.setValueAt(nmpnj.getText(), tbObat.getSelectedRow(), 19);
            tbObat.setValueAt(PenyakitDiDerita.getText(), tbObat.getSelectedRow(), 20); // Tambahkan ini
            emptTeks();
        }
    }

    private void hapus() {
        snapshotSPJPAudit(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),"HAPUS");
        if (Sequel.queryu2tf("delete from surat_pernyataan_jaminan_pasien where no_surat=?", 1, new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString()
        }) == true) {
            // Hapus data terkait dari tabel lain
            String noSurat = tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString();
            Sequel.queryu("delete from surat_pernyataan_jaminan_pasien_signature where no_surat=?", noSurat);
            Sequel.queryu("delete from surat_pernyataan_jaminan_pasien_pembuat_pernyataan where no_surat=?", noSurat);
            // Hapus dari antrian jika ada
            Sequel.queryu("delete from antrijaminanpasien where no_pernyataan=?", noSurat);

            tabMode.removeRow(tbObat.getSelectedRow());
            LCount.setText("" + tabMode.getRowCount());
            emptTeks();
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
        }
    }

    private void isPhoto() {
        if (ChkAccor.isSelected() == true) {
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(480, HEIGHT));
            FormPhoto.setVisible(true);
            ChkAccor.setVisible(true);
        } else if (ChkAccor.isSelected() == false) {
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(15, HEIGHT));
            FormPhoto.setVisible(false);
            ChkAccor.setVisible(true);
        }
    }

    private void panggilPhoto() {
        if (FormPhoto.isVisible() == true) {
            lokasifile = "";
            signaturefile = "";
            String noSurat = tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString();
            String base = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/suratpernyataanjaminan/";
            // Photo pembuat pernyataan (mendukung record dari web)
            try {
                ps = koneksi.prepareStatement("select photo from surat_pernyataan_jaminan_pasien_pembuat_pernyataan where no_surat=?");
                ps.setString(1, noSurat);
                rs = ps.executeQuery();
                if (rs.next() && rs.getString("photo") != null && !rs.getString("photo").trim().equals("") && !rs.getString("photo").equals("-")) {
                    lokasifile = rs.getString("photo");
                }
                rs.close();
                ps.close();
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
            // Tanda tangan (tabel _signature, mendukung record dari web)
            try {
                ps = koneksi.prepareStatement("select signature_path from surat_pernyataan_jaminan_pasien_signature where no_surat=?");
                ps.setString(1, noSurat);
                rs = ps.executeQuery();
                if (rs.next() && rs.getString("signature_path") != null && !rs.getString("signature_path").trim().equals("") && !rs.getString("signature_path").equals("-")) {
                    signaturefile = rs.getString("signature_path");
                }
                rs.close();
                ps.close();
            } catch (Exception e) {
                System.out.println("Notif : " + e);
            }
            if (lokasifile.equals("") && signaturefile.equals("")) {
                LoadHTML2.setText("<html><body><center><br><br><font face='tahoma' size='2' color='#434343'>Kosong</font></center></body></html>");
            } else {
                StringBuilder html = new StringBuilder("<html><body><center>");
                if (!lokasifile.equals("")) {
                    html.append("<img src='").append(base).append(lokasifile).append("' alt='photo' width='450' height='337'/><br>");
                }
                if (!signaturefile.equals("")) {
                    html.append("<img src='").append(base).append(signaturefile).append("' alt='signature' width='450' height='225'/><br>");
                }
                html.append("</center></body></html>");
                LoadHTML2.setText(html.toString());
            }
        }
    }

    // Method untuk validasi kode penjab
    private boolean isValidKdPenjab(String kdPenjab) {
        if (kdPenjab.trim().equals("")) {
            return false;
        }
        try {
            ps = koneksi.prepareStatement("select kd_pj from penjab where kd_pj=?");
            ps.setString(1, kdPenjab);
            rs = ps.executeQuery();
            boolean valid = rs.next();
            rs.close();
            ps.close();
            return valid;
        } catch (Exception e) {
            System.out.println("Error validasi kd_pj: " + e);
            return false;
        }
    }

    // Method untuk ambil nama penjab berdasarkan kode
    private void ambilNamaPenjab() {
        if (!kdpnj.getText().trim().equals("")) {
            try {
                ps = koneksi.prepareStatement("select png_jawab from penjab where kd_pj=?");
                ps.setString(1, kdpnj.getText());
                rs = ps.executeQuery();
                if (rs.next()) {
                    nmpnj.setText(rs.getString("png_jawab"));
                } else {
                    nmpnj.setText("");
                }
                rs.close();
                ps.close();
            } catch (Exception e) {
                System.out.println("Error ambil nama penjab: " + e);
                nmpnj.setText("");
            }
        } else {
            nmpnj.setText("");
        }
    }

}
