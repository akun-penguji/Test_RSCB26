/*
 * Kontribusi dari M. Syukur RS. Jiwa Prov Sultra
 */
package rekammedis;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
import kepegawaian.DlgCariDokter;
import kepegawaian.DlgCariPetugas;

/**
 *
 * @author perpustakaan
 */
public final class RMTransferInternalRs extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private DlgCariDokter dokter = new DlgCariDokter(null, false);
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0, pilihan = 0;
    private DlgCariPetugas petugas = new DlgCariPetugas(null, false);
    private StringBuilder htmlContent;
    private String finger = "", finger2 = "", finger3 = "";

    /**
     * Creates new form DlgRujuk
     *
     * @param parent
     * @param modal
     */
    public RMTransferInternalRs(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.Rawat", "No.RM", "Nama Pasien", "Tgl.Lahir", "J.K.", "Tanggal Masuk", "Tanggal Pindah", "Asal Ruang/ Poliklinik", "Ruang rawat Selanjutnya",
            "Kode DPJP", "Nama DPJP", "Kode Konsulen", "Nama Konsulen", "Diagnosa Masuk", "Diagnosa Sekarang", "Alergi", "Derajat Kondisi Pasien", "Petugas Pendamping Transfer",
            "Keadaan Umum Sebelum Pindah", "Keadaan Umum Setelah Pindah", "Kesadaran Sebelum Pindah", "Kesadaran Setelah Pindah", "Tekanan Darah Sebelum Pindah", "Tekanan Darah Setelah Pindah",
            "Nadi Sebelum Pindah", "Nadi Setelah Pindah", "Suhu sebelum pindah", "Suhu setelah pindah", "Pernapasan sebelum pindah", "Pernapasan setelah pindah", "Saturasi oksigen sebelum pindah",
            "Saturasi oksigen setelah pindah", "Catatan sebelum pindah", "Catatan setelah pindah", "Diet", "Keseimbangan Cairan", "Mobilisasi",
            "Perawatan luka", "IV Catheter", "Oksigen", "Kateter", "Syring Pump", "NGT", "Kasur Dekubitus", "Skema infus", "CVP", "Drain", "Lain-lain", "Lab", "Radiologi", "CT Scan", "Infus", "Injeksi", "Oral",
            "Rekam Medis", "NIP Menyerahkan", "Petugas Menyerahkan", "NIP Penerima", "Petugas Penerima"
        }) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < tabMode.getColumnCount(); i++) {
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
            } else if (i == 5 || i == 6) {
                column.setPreferredWidth(115);
            } else if (i == 7) {
                column.setPreferredWidth(190);
            } else if (i == 8) {
                column.setPreferredWidth(140);
            } else if (i == 9 || i == 10) {
                column.setPreferredWidth(150);
            } else if (i == 11) {
                column.setPreferredWidth(108);
            } else if (i == 12) {
                column.setPreferredWidth(160);
            } else if (i == 13) {
                column.setPreferredWidth(160);
            } else if (i == 14) {
                column.setPreferredWidth(200);
            } else if (i == 15) {
                column.setPreferredWidth(230);
            } else if (i == 16) {
                column.setPreferredWidth(236);
            } else if (i == 17) {
                column.setPreferredWidth(134);
            } else if (i == 18) {
                column.setPreferredWidth(165);
            } else if (i == 19) {
                column.setPreferredWidth(127);
            } else if (i == 20) {
                column.setPreferredWidth(180);
            } else if (i == 21) {
                column.setPreferredWidth(100);
            } else if (i == 22) {
                column.setPreferredWidth(107);
            } else if (i == 23 || i == 24) {
                column.setPreferredWidth(50);
            } else if (i == 25) {
                column.setPreferredWidth(45);
            } else if (i == 26) {
                column.setPreferredWidth(50);
            } else if (i == 27) {
                column.setPreferredWidth(200);
            } else if (i == 28) {
                column.setPreferredWidth(107);
            } else if (i == 29 || i == 30) {
                column.setPreferredWidth(50);
            } else if (i == 31) {
                column.setPreferredWidth(45);
            } else if (i == 32) {
                column.setPreferredWidth(50);
            } else if (i == 33) {
                column.setPreferredWidth(200);
            } else if (i == 34) {
                column.setPreferredWidth(95);
            } else if (i == 35) {
                column.setPreferredWidth(150);
            } else if (i == 36) {
                column.setPreferredWidth(95);
            } else if (i == 37) {
                column.setPreferredWidth(150);
            } else {
                column.setPreferredWidth(100); // Default width untuk kolom 38 ke atas
            }
        }

        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        AsalRuang.setDocument(new batasInput((byte) 30).getKata(AsalRuang));
        RuangSelanjutnya.setDocument(new batasInput((byte) 30).getKata(RuangSelanjutnya));
        DiagnosaUtama.setDocument(new batasInput((int) 50).getKata(DiagnosaUtama));
        DiagnosaSekarang.setDocument(new batasInput((int) 100).getKata(DiagnosaSekarang));
        dpjp.setDocument(new batasInput((int) 50).getKata(dpjp));
        Alergi.setDocument(new batasInput((int) 800).getKata(Alergi));
        Infus.setDocument(new batasInput((int) 800).getKata(Infus));
        SaturasiSebelumTransfer.setDocument(new batasInput((int) 50).getKata(SaturasiSebelumTransfer));
        PetugasPendamping.setDocument(new batasInput((int) 500).getKata(PetugasPendamping));
        SaturasiSesudahTransfer.setDocument(new batasInput((int) 50).getKata(SaturasiSesudahTransfer));
        Lab.setDocument(new batasInput((int) 200).getKata(Lab));
        TdSebelumTransfer.setDocument(new batasInput((int) 7).getKata(TdSebelumTransfer));
        NadiSebelumTransfer.setDocument(new batasInput((int) 5).getKata(NadiSebelumTransfer));
        RRSebelumTransfer.setDocument(new batasInput((int) 5).getKata(RRSebelumTransfer));
        SuhuSebelumTransfer.setDocument(new batasInput((int) 5).getKata(SuhuSebelumTransfer));
        Rad.setDocument(new batasInput((int) 200).getKata(Rad));
        TDSetelahTransfer.setDocument(new batasInput((int) 7).getKata(TDSetelahTransfer));
        NadiSetelahTransfer.setDocument(new batasInput((int) 5).getKata(NadiSetelahTransfer));
        RRSetelahTransfer.setDocument(new batasInput((int) 5).getKata(RRSetelahTransfer));
        SuhuSetelahTransfer.setDocument(new batasInput((int) 5).getKata(SuhuSetelahTransfer));
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
                    if (pilihan == 1) {
                        kddpjp.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                        dpjp.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                        kddpjp.requestFocus();
                    } else {
                        kdkonsulen.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                        konsulen.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
                        kdkonsulen.requestFocus();
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
                    if (pilihan == 1) {
                        KdPetugasMenyerahkan.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                        NmPetugasMenyerahkan.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                        KdPetugasMenyerahkan.requestFocus();
                    } else {
                        KdPetugasMenerima.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 0).toString());
                        NmPetugasMenerima.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(), 1).toString());
                        KdPetugasMenerima.requestFocus();
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

        HTMLEditorKit kit = new HTMLEditorKit();
        LoadHTML.setEditable(true);
        LoadHTML.setEditorKit(kit);
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
        LoadHTML.setDocument(doc);
        LoadHTML2.setDocument(doc);

        ChkAccor.setSelected(false);
        isPhoto();
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
        MnCetakTransfer = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        BtnAll = new widget.Button();
        BtnKeluar = new widget.Button();
        TabRawat = new javax.swing.JTabbedPane();
        internalFrame2 = new widget.InternalFrame();
        scrollInput = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        jSeparator14 = new javax.swing.JSeparator();
        jLabel57 = new widget.Label();
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        TNoRM = new widget.TextBox();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        Jk = new widget.TextBox();
        jLabel10 = new widget.Label();
        label11 = new widget.Label();
        jLabel11 = new widget.Label();
        TanggalMasuk = new widget.Tanggal();
        label12 = new widget.Label();
        TanggalPindah = new widget.Tanggal();
        dpjp = new widget.TextBox();
        jSeparator1 = new javax.swing.JSeparator();
        SaturasiSebelumTransfer = new widget.TextBox();
        jLabel13 = new widget.Label();
        RuangSelanjutnya = new widget.TextBox();
        jLabel14 = new widget.Label();
        jLabel15 = new widget.Label();
        Rekammedis = new widget.ComboBox();
        jLabel16 = new widget.Label();
        DiagnosaUtama = new widget.TextBox();
        jLabel18 = new widget.Label();
        jLabel20 = new widget.Label();
        jLabel30 = new widget.Label();
        jLabel31 = new widget.Label();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel32 = new widget.Label();
        jLabel33 = new widget.Label();
        AsalRuang = new widget.TextBox();
        Derajat = new widget.ComboBox();
        jLabel36 = new widget.Label();
        SaturasiSesudahTransfer = new widget.TextBox();
        jLabel40 = new widget.Label();
        jLabel41 = new widget.Label();
        jLabel42 = new widget.Label();
        KesadaranSebelumTransfer = new widget.ComboBox();
        TdSebelumTransfer = new widget.TextBox();
        jLabel23 = new widget.Label();
        jLabel17 = new widget.Label();
        jLabel22 = new widget.Label();
        NadiSebelumTransfer = new widget.TextBox();
        jLabel26 = new widget.Label();
        RRSebelumTransfer = new widget.TextBox();
        jLabel25 = new widget.Label();
        jLabel24 = new widget.Label();
        SuhuSebelumTransfer = new widget.TextBox();
        jLabel27 = new widget.Label();
        jLabel28 = new widget.Label();
        jLabel43 = new widget.Label();
        jLabel44 = new widget.Label();
        KesadaranSesudahTransfer = new widget.ComboBox();
        TDSetelahTransfer = new widget.TextBox();
        jLabel45 = new widget.Label();
        NadiSetelahTransfer = new widget.TextBox();
        jLabel48 = new widget.Label();
        RRSetelahTransfer = new widget.TextBox();
        jLabel50 = new widget.Label();
        SuhuSetelahTransfer = new widget.TextBox();
        jLabel52 = new widget.Label();
        label14 = new widget.Label();
        KdPetugasMenyerahkan = new widget.TextBox();
        NmPetugasMenyerahkan = new widget.TextBox();
        BtnDokter = new widget.Button();
        label15 = new widget.Label();
        KdPetugasMenerima = new widget.TextBox();
        NmPetugasMenerima = new widget.TextBox();
        BtnMenerima = new widget.Button();
        label16 = new widget.Label();
        kddpjp = new widget.TextBox();
        jLabel58 = new widget.Label();
        kdkonsulen = new widget.TextBox();
        konsulen = new widget.TextBox();
        jLabel53 = new widget.Label();
        jLabel54 = new widget.Label();
        CatatanSebelumTransfer = new widget.TextBox();
        CatatanSesudahTransfer = new widget.TextBox();
        Diet = new widget.TextBox();
        Mobilisasi = new widget.TextBox();
        KeseimbanganCairan = new widget.TextBox();
        PerawatanLuka = new widget.TextBox();
        jLabel34 = new widget.Label();
        jLabel35 = new widget.Label();
        jLabel39 = new widget.Label();
        Cath = new widget.TextBox();
        jLabel46 = new widget.Label();
        Kateter = new widget.TextBox();
        jLabel55 = new widget.Label();
        Ngt = new widget.TextBox();
        jLabel56 = new widget.Label();
        SkemaInfus = new widget.TextBox();
        jLabel59 = new widget.Label();
        Drain = new widget.TextBox();
        jLabel60 = new widget.Label();
        Oksigen = new widget.TextBox();
        jLabel61 = new widget.Label();
        Syring = new widget.TextBox();
        jLabel62 = new widget.Label();
        Dekubitus = new widget.TextBox();
        jLabel63 = new widget.Label();
        Cvp = new widget.TextBox();
        jLabel64 = new widget.Label();
        Lain = new widget.TextBox();
        jLabel65 = new widget.Label();
        jLabel66 = new widget.Label();
        Lab = new widget.TextBox();
        jLabel67 = new widget.Label();
        Rad = new widget.TextBox();
        Ct = new widget.TextBox();
        jLabel68 = new widget.Label();
        jLabel69 = new widget.Label();
        jLabel70 = new widget.Label();
        Infus = new widget.TextBox();
        jLabel71 = new widget.Label();
        Injeksi = new widget.TextBox();
        Oral = new widget.TextBox();
        jLabel72 = new widget.Label();
        BtnDokter1 = new widget.Button();
        BtnDokter2 = new widget.Button();
        DiagnosaSekarang = new widget.TextBox();
        Alergi = new widget.TextBox();
        PetugasPendamping = new widget.TextBox();
        KuSesudahTransfer = new widget.TextBox();
        KuSebelumTransfer = new widget.TextBox();
        jLabel37 = new widget.Label();
        jLabel38 = new widget.Label();
        internalFrame3 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
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
        PanelAccor = new widget.PanelBiasa();
        ChkAccor = new widget.CekBox();
        FormPhoto = new widget.PanelBiasa();
        FormPass3 = new widget.PanelBiasa();
        btnAmbil = new widget.Button();
        BtnRefreshPhoto1 = new widget.Button();
        Scroll5 = new widget.ScrollPane();
        LoadHTML2 = new widget.editorpane();

        LoadHTML.setBorder(null);
        LoadHTML.setName("LoadHTML"); // NOI18N

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnCetakTransfer.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        MnCetakTransfer.setText("Cetak");
        MnCetakTransfer.setName("MnCetakTransfer"); // NOI18N
        MnCetakTransfer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnCetakTransferActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnCetakTransfer);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Transfer Internal Rumah Sakit ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

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

        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        TabRawat.setBackground(new java.awt.Color(254, 255, 254));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.setName("TabRawat"); // NOI18N
        TabRawat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TabRawatMouseClicked(evt);
            }
        });

        internalFrame2.setBorder(null);
        internalFrame2.setName("internalFrame2"); // NOI18N
        internalFrame2.setLayout(new java.awt.BorderLayout(1, 1));

        scrollInput.setName("scrollInput"); // NOI18N
        scrollInput.setPreferredSize(new java.awt.Dimension(102, 557));

        FormInput.setBackground(new java.awt.Color(255, 255, 255));
        FormInput.setBorder(null);
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(870, 1200));
        FormInput.setLayout(null);

        jSeparator14.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator14.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator14.setName("jSeparator14"); // NOI18N
        FormInput.add(jSeparator14);
        jSeparator14.setBounds(0, 861, 880, 0);

        jLabel57.setText("DPJP:");
        jLabel57.setName("jLabel57"); // NOI18N
        FormInput.add(jLabel57);
        jLabel57.setBounds(420, 80, 30, 23);

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

        label11.setText("Masuk :");
        label11.setName("label11"); // NOI18N
        label11.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label11);
        label11.setBounds(0, 40, 70, 23);

        jLabel11.setText("J.K. :");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(740, 10, 30, 23);

        TanggalMasuk.setForeground(new java.awt.Color(50, 70, 50));
        TanggalMasuk.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10-05-2025 19:31:36" }));
        TanggalMasuk.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TanggalMasuk.setName("TanggalMasuk"); // NOI18N
        TanggalMasuk.setOpaque(false);
        TanggalMasuk.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalMasukKeyPressed(evt);
            }
        });
        FormInput.add(TanggalMasuk);
        TanggalMasuk.setBounds(74, 40, 130, 23);

        label12.setText("Pindah :");
        label12.setName("label12"); // NOI18N
        label12.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label12);
        label12.setBounds(201, 40, 55, 23);

        TanggalPindah.setForeground(new java.awt.Color(50, 70, 50));
        TanggalPindah.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10-05-2025 19:31:36" }));
        TanggalPindah.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TanggalPindah.setName("TanggalPindah"); // NOI18N
        TanggalPindah.setOpaque(false);
        TanggalPindah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalPindahKeyPressed(evt);
            }
        });
        FormInput.add(TanggalPindah);
        TanggalPindah.setBounds(260, 40, 130, 23);

        dpjp.setHighlighter(null);
        dpjp.setName("dpjp"); // NOI18N
        dpjp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                dpjpKeyPressed(evt);
            }
        });
        FormInput.add(dpjp);
        dpjp.setBounds(550, 80, 290, 23);

        jSeparator1.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator1.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator1.setName("jSeparator1"); // NOI18N
        FormInput.add(jSeparator1);
        jSeparator1.setBounds(0, 70, 880, 1);

        SaturasiSebelumTransfer.setHighlighter(null);
        SaturasiSebelumTransfer.setName("SaturasiSebelumTransfer"); // NOI18N
        SaturasiSebelumTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SaturasiSebelumTransferKeyPressed(evt);
            }
        });
        FormInput.add(SaturasiSebelumTransfer);
        SaturasiSebelumTransfer.setBounds(240, 410, 90, 23);

        jLabel13.setText(":");
        jLabel13.setName("jLabel13"); // NOI18N
        FormInput.add(jLabel13);
        jLabel13.setBounds(91, 120, 10, 23);

        RuangSelanjutnya.setHighlighter(null);
        RuangSelanjutnya.setName("RuangSelanjutnya"); // NOI18N
        RuangSelanjutnya.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RuangSelanjutnyaKeyPressed(evt);
            }
        });
        FormInput.add(RuangSelanjutnya);
        RuangSelanjutnya.setBounds(160, 80, 240, 23);

        jLabel14.setText("Derajat kondisi pasien:");
        jLabel14.setName("jLabel14"); // NOI18N
        FormInput.add(jLabel14);
        jLabel14.setBounds(0, 180, 120, 23);

        jLabel15.setText("Ruang Rawat Selanjutnya :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(10, 80, 140, 23);

        Rekammedis.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ada", "Tidak ada" }));
        Rekammedis.setName("Rekammedis"); // NOI18N
        Rekammedis.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RekammedisKeyPressed(evt);
            }
        });
        FormInput.add(Rekammedis);
        Rekammedis.setBounds(110, 1020, 140, 23);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Diagnosa Masuk:");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(520, 120, 90, 23);

        DiagnosaUtama.setHighlighter(null);
        DiagnosaUtama.setName("DiagnosaUtama"); // NOI18N
        DiagnosaUtama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaUtamaKeyPressed(evt);
            }
        });
        FormInput.add(DiagnosaUtama);
        DiagnosaUtama.setBounds(610, 120, 250, 23);

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Asal Ruang Rawat / Poliklinik");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(400, 40, 140, 23);

        jLabel20.setText(":");
        jLabel20.setName("jLabel20"); // NOI18N
        FormInput.add(jLabel20);
        jLabel20.setBounds(540, 40, 10, 23);

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel30.setText("Diagnosa Sekarang:");
        jLabel30.setName("jLabel30"); // NOI18N
        FormInput.add(jLabel30);
        jLabel30.setBounds(15, 150, 170, 23);

        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel31.setText("Alergi:");
        jLabel31.setName("jLabel31"); // NOI18N
        FormInput.add(jLabel31);
        jLabel31.setBounds(450, 150, 160, 23);

        jSeparator2.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator2.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator2.setName("jSeparator2"); // NOI18N
        FormInput.add(jSeparator2);
        jSeparator2.setBounds(0, 110, 880, 1);

        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel32.setText("Alat alat medik dan invasif yang terpasang:");
        jLabel32.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel32.setName("jLabel32"); // NOI18N
        FormInput.add(jLabel32);
        jLabel32.setBounds(20, 590, 270, 23);

        jLabel33.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel33.setText("Petugas Pendamping Transfer:");
        jLabel33.setName("jLabel33"); // NOI18N
        FormInput.add(jLabel33);
        jLabel33.setBounds(300, 180, 160, 23);

        AsalRuang.setHighlighter(null);
        AsalRuang.setName("AsalRuang"); // NOI18N
        AsalRuang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AsalRuangKeyPressed(evt);
            }
        });
        FormInput.add(AsalRuang);
        AsalRuang.setBounds(550, 40, 310, 23);

        Derajat.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Derajat 0", "Derajat 1", "Derajat 2", "Derajat 3" }));
        Derajat.setName("Derajat"); // NOI18N
        Derajat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DerajatKeyPressed(evt);
            }
        });
        FormInput.add(Derajat);
        Derajat.setBounds(130, 180, 160, 23);

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel36.setText("Rekam medis:");
        jLabel36.setName("jLabel36"); // NOI18N
        FormInput.add(jLabel36);
        jLabel36.setBounds(30, 1020, 130, 23);

        SaturasiSesudahTransfer.setHighlighter(null);
        SaturasiSesudahTransfer.setName("SaturasiSesudahTransfer"); // NOI18N
        SaturasiSesudahTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SaturasiSesudahTransferKeyPressed(evt);
            }
        });
        FormInput.add(SaturasiSesudahTransfer);
        SaturasiSesudahTransfer.setBounds(550, 410, 100, 23);

        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel40.setText("Pemeriksaan Fisik:");
        jLabel40.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel40.setName("jLabel40"); // NOI18N
        FormInput.add(jLabel40);
        jLabel40.setBounds(20, 210, 110, 23);

        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel41.setText("Catatan");
        jLabel41.setName("jLabel41"); // NOI18N
        FormInput.add(jLabel41);
        jLabel41.setBounds(60, 440, 160, 23);

        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel42.setText("Kesadaran");
        jLabel42.setName("jLabel42"); // NOI18N
        FormInput.add(jLabel42);
        jLabel42.setBounds(60, 260, 60, 23);

        KesadaranSebelumTransfer.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Compos Mentis", "Gelisah", "Delirium", "Koma" }));
        KesadaranSebelumTransfer.setName("KesadaranSebelumTransfer"); // NOI18N
        KesadaranSebelumTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KesadaranSebelumTransferKeyPressed(evt);
            }
        });
        FormInput.add(KesadaranSebelumTransfer);
        KesadaranSebelumTransfer.setBounds(240, 260, 180, 23);

        TdSebelumTransfer.setFocusTraversalPolicyProvider(true);
        TdSebelumTransfer.setName("TdSebelumTransfer"); // NOI18N
        TdSebelumTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TdSebelumTransferKeyPressed(evt);
            }
        });
        FormInput.add(TdSebelumTransfer);
        TdSebelumTransfer.setBounds(240, 290, 90, 23);

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("mmHg");
        jLabel23.setName("jLabel23"); // NOI18N
        FormInput.add(jLabel23);
        jLabel23.setBounds(340, 290, 40, 23);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("Nadi");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(60, 320, 40, 23);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setText("x/menit");
        jLabel22.setName("jLabel22"); // NOI18N
        FormInput.add(jLabel22);
        jLabel22.setBounds(340, 320, 50, 23);

        NadiSebelumTransfer.setFocusTraversalPolicyProvider(true);
        NadiSebelumTransfer.setName("NadiSebelumTransfer"); // NOI18N
        NadiSebelumTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NadiSebelumTransferKeyPressed(evt);
            }
        });
        FormInput.add(NadiSebelumTransfer);
        NadiSebelumTransfer.setBounds(240, 320, 90, 23);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel26.setText("Pernapasan");
        jLabel26.setName("jLabel26"); // NOI18N
        FormInput.add(jLabel26);
        jLabel26.setBounds(60, 380, 70, 23);

        RRSebelumTransfer.setFocusTraversalPolicyProvider(true);
        RRSebelumTransfer.setName("RRSebelumTransfer"); // NOI18N
        RRSebelumTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RRSebelumTransferKeyPressed(evt);
            }
        });
        FormInput.add(RRSebelumTransfer);
        RRSebelumTransfer.setBounds(240, 380, 60, 23);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("x/menit");
        jLabel25.setName("jLabel25"); // NOI18N
        FormInput.add(jLabel25);
        jLabel25.setBounds(310, 380, 50, 23);

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel24.setText("Suhu ");
        jLabel24.setName("jLabel24"); // NOI18N
        FormInput.add(jLabel24);
        jLabel24.setBounds(60, 350, 90, 23);

        SuhuSebelumTransfer.setFocusTraversalPolicyProvider(true);
        SuhuSebelumTransfer.setName("SuhuSebelumTransfer"); // NOI18N
        SuhuSebelumTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SuhuSebelumTransferKeyPressed(evt);
            }
        });
        FormInput.add(SuhuSebelumTransfer);
        SuhuSebelumTransfer.setBounds(240, 350, 60, 23);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel27.setText("°C");
        jLabel27.setName("jLabel27"); // NOI18N
        FormInput.add(jLabel27);
        jLabel27.setBounds(310, 350, 30, 23);

        jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel28.setText("Tekanan Darah");
        jLabel28.setName("jLabel28"); // NOI18N
        FormInput.add(jLabel28);
        jLabel28.setBounds(60, 290, 90, 23);

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel43.setText("Setelah pindah");
        jLabel43.setName("jLabel43"); // NOI18N
        FormInput.add(jLabel43);
        jLabel43.setBounds(550, 210, 140, 23);

        jLabel44.setText("Saturasi oksigen");
        jLabel44.setName("jLabel44"); // NOI18N
        FormInput.add(jLabel44);
        jLabel44.setBounds(50, 410, 90, 23);

        KesadaranSesudahTransfer.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Compos Mentis", "Gelisah", "Delirium", "Koma" }));
        KesadaranSesudahTransfer.setName("KesadaranSesudahTransfer"); // NOI18N
        KesadaranSesudahTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KesadaranSesudahTransferKeyPressed(evt);
            }
        });
        FormInput.add(KesadaranSesudahTransfer);
        KesadaranSesudahTransfer.setBounds(550, 260, 170, 23);

        TDSetelahTransfer.setFocusTraversalPolicyProvider(true);
        TDSetelahTransfer.setName("TDSetelahTransfer"); // NOI18N
        TDSetelahTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TDSetelahTransferKeyPressed(evt);
            }
        });
        FormInput.add(TDSetelahTransfer);
        TDSetelahTransfer.setBounds(550, 290, 90, 23);

        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel45.setText("mmHg");
        jLabel45.setName("jLabel45"); // NOI18N
        FormInput.add(jLabel45);
        jLabel45.setBounds(650, 290, 50, 23);

        NadiSetelahTransfer.setFocusTraversalPolicyProvider(true);
        NadiSetelahTransfer.setName("NadiSetelahTransfer"); // NOI18N
        NadiSetelahTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NadiSetelahTransferKeyPressed(evt);
            }
        });
        FormInput.add(NadiSetelahTransfer);
        NadiSetelahTransfer.setBounds(550, 320, 90, 23);

        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel48.setText("x/menit");
        jLabel48.setName("jLabel48"); // NOI18N
        FormInput.add(jLabel48);
        jLabel48.setBounds(650, 320, 50, 23);

        RRSetelahTransfer.setFocusTraversalPolicyProvider(true);
        RRSetelahTransfer.setName("RRSetelahTransfer"); // NOI18N
        RRSetelahTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RRSetelahTransferKeyPressed(evt);
            }
        });
        FormInput.add(RRSetelahTransfer);
        RRSetelahTransfer.setBounds(550, 380, 60, 23);

        jLabel50.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel50.setText("x/menit");
        jLabel50.setName("jLabel50"); // NOI18N
        FormInput.add(jLabel50);
        jLabel50.setBounds(620, 380, 40, 23);

        SuhuSetelahTransfer.setFocusTraversalPolicyProvider(true);
        SuhuSetelahTransfer.setName("SuhuSetelahTransfer"); // NOI18N
        SuhuSetelahTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SuhuSetelahTransferKeyPressed(evt);
            }
        });
        FormInput.add(SuhuSetelahTransfer);
        SuhuSetelahTransfer.setBounds(550, 350, 60, 23);

        jLabel52.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel52.setText("°C");
        jLabel52.setName("jLabel52"); // NOI18N
        FormInput.add(jLabel52);
        jLabel52.setBounds(620, 350, 30, 23);

        label14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label14.setText("Petugas / Perawat :");
        label14.setName("label14"); // NOI18N
        label14.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label14);
        label14.setBounds(0, 1050, 130, 23);

        KdPetugasMenyerahkan.setEditable(false);
        KdPetugasMenyerahkan.setName("KdPetugasMenyerahkan"); // NOI18N
        KdPetugasMenyerahkan.setPreferredSize(new java.awt.Dimension(80, 23));
        FormInput.add(KdPetugasMenyerahkan);
        KdPetugasMenyerahkan.setBounds(120, 1070, 100, 23);

        NmPetugasMenyerahkan.setEditable(false);
        NmPetugasMenyerahkan.setName("NmPetugasMenyerahkan"); // NOI18N
        NmPetugasMenyerahkan.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(NmPetugasMenyerahkan);
        NmPetugasMenyerahkan.setBounds(230, 1070, 180, 23);

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
        BtnDokter.setBounds(420, 1070, 28, 23);

        label15.setText("Menerima :");
        label15.setName("label15"); // NOI18N
        label15.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label15);
        label15.setBounds(450, 1070, 70, 23);

        KdPetugasMenerima.setEditable(false);
        KdPetugasMenerima.setName("KdPetugasMenerima"); // NOI18N
        KdPetugasMenerima.setPreferredSize(new java.awt.Dimension(80, 23));
        FormInput.add(KdPetugasMenerima);
        KdPetugasMenerima.setBounds(530, 1070, 90, 23);

        NmPetugasMenerima.setEditable(false);
        NmPetugasMenerima.setName("NmPetugasMenerima"); // NOI18N
        NmPetugasMenerima.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(NmPetugasMenerima);
        NmPetugasMenerima.setBounds(630, 1070, 180, 23);

        BtnMenerima.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnMenerima.setMnemonic('2');
        BtnMenerima.setToolTipText("Alt+2");
        BtnMenerima.setName("BtnMenerima"); // NOI18N
        BtnMenerima.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnMenerima.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnMenerimaActionPerformed(evt);
            }
        });
        BtnMenerima.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnMenerimaKeyPressed(evt);
            }
        });
        FormInput.add(BtnMenerima);
        BtnMenerima.setBounds(820, 1070, 28, 23);

        label16.setText("Menyerahkan :");
        label16.setName("label16"); // NOI18N
        label16.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label16);
        label16.setBounds(30, 1070, 90, 23);

        kddpjp.setHighlighter(null);
        kddpjp.setName("kddpjp"); // NOI18N
        kddpjp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kddpjpKeyPressed(evt);
            }
        });
        FormInput.add(kddpjp);
        kddpjp.setBounds(460, 80, 80, 23);

        jLabel58.setText("Dokter Konsulen:");
        jLabel58.setName("jLabel58"); // NOI18N
        FormInput.add(jLabel58);
        jLabel58.setBounds(10, 120, 90, 23);

        kdkonsulen.setHighlighter(null);
        kdkonsulen.setName("kdkonsulen"); // NOI18N
        kdkonsulen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                kdkonsulenKeyPressed(evt);
            }
        });
        FormInput.add(kdkonsulen);
        kdkonsulen.setBounds(110, 120, 60, 23);

        konsulen.setHighlighter(null);
        konsulen.setName("konsulen"); // NOI18N
        konsulen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                konsulenKeyPressed(evt);
            }
        });
        FormInput.add(konsulen);
        konsulen.setBounds(180, 120, 290, 23);

        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel53.setText("Sebelum pindah");
        jLabel53.setName("jLabel53"); // NOI18N
        FormInput.add(jLabel53);
        jLabel53.setBounds(240, 210, 140, 23);

        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel54.setText("Keadaan Umum ");
        jLabel54.setName("jLabel54"); // NOI18N
        FormInput.add(jLabel54);
        jLabel54.setBounds(60, 230, 80, 23);

        CatatanSebelumTransfer.setHighlighter(null);
        CatatanSebelumTransfer.setName("CatatanSebelumTransfer"); // NOI18N
        CatatanSebelumTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CatatanSebelumTransferKeyPressed(evt);
            }
        });
        FormInput.add(CatatanSebelumTransfer);
        CatatanSebelumTransfer.setBounds(240, 440, 290, 23);

        CatatanSesudahTransfer.setHighlighter(null);
        CatatanSesudahTransfer.setName("CatatanSesudahTransfer"); // NOI18N
        CatatanSesudahTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CatatanSesudahTransferKeyPressed(evt);
            }
        });
        FormInput.add(CatatanSesudahTransfer);
        CatatanSesudahTransfer.setBounds(550, 440, 320, 23);

        Diet.setHighlighter(null);
        Diet.setName("Diet"); // NOI18N
        Diet.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DietKeyPressed(evt);
            }
        });
        FormInput.add(Diet);
        Diet.setBounds(240, 470, 630, 23);

        Mobilisasi.setHighlighter(null);
        Mobilisasi.setName("Mobilisasi"); // NOI18N
        Mobilisasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MobilisasiKeyPressed(evt);
            }
        });
        FormInput.add(Mobilisasi);
        Mobilisasi.setBounds(240, 530, 630, 23);

        KeseimbanganCairan.setHighlighter(null);
        KeseimbanganCairan.setName("KeseimbanganCairan"); // NOI18N
        KeseimbanganCairan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeseimbanganCairanKeyPressed(evt);
            }
        });
        FormInput.add(KeseimbanganCairan);
        KeseimbanganCairan.setBounds(240, 500, 630, 23);

        PerawatanLuka.setHighlighter(null);
        PerawatanLuka.setName("PerawatanLuka"); // NOI18N
        PerawatanLuka.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PerawatanLukaKeyPressed(evt);
            }
        });
        FormInput.add(PerawatanLuka);
        PerawatanLuka.setBounds(240, 560, 630, 23);

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel34.setText("Diet:");
        jLabel34.setName("jLabel34"); // NOI18N
        FormInput.add(jLabel34);
        jLabel34.setBounds(60, 470, 170, 23);

        jLabel35.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel35.setText("Perawatan Luka");
        jLabel35.setName("jLabel35"); // NOI18N
        FormInput.add(jLabel35);
        jLabel35.setBounds(60, 560, 170, 23);

        jLabel39.setText("IV Cath:");
        jLabel39.setName("jLabel39"); // NOI18N
        FormInput.add(jLabel39);
        jLabel39.setBounds(150, 610, 80, 23);

        Cath.setHighlighter(null);
        Cath.setName("Cath"); // NOI18N
        Cath.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CathKeyPressed(evt);
            }
        });
        FormInput.add(Cath);
        Cath.setBounds(240, 610, 290, 23);

        jLabel46.setText("Kateter:");
        jLabel46.setName("jLabel46"); // NOI18N
        FormInput.add(jLabel46);
        jLabel46.setBounds(150, 640, 80, 23);

        Kateter.setHighlighter(null);
        Kateter.setName("Kateter"); // NOI18N
        Kateter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KateterKeyPressed(evt);
            }
        });
        FormInput.add(Kateter);
        Kateter.setBounds(240, 640, 290, 23);

        jLabel55.setText("NGT:");
        jLabel55.setName("jLabel55"); // NOI18N
        FormInput.add(jLabel55);
        jLabel55.setBounds(150, 670, 80, 23);

        Ngt.setHighlighter(null);
        Ngt.setName("Ngt"); // NOI18N
        Ngt.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NgtKeyPressed(evt);
            }
        });
        FormInput.add(Ngt);
        Ngt.setBounds(240, 670, 290, 23);

        jLabel56.setText("Skema Infus:");
        jLabel56.setName("jLabel56"); // NOI18N
        FormInput.add(jLabel56);
        jLabel56.setBounds(150, 700, 80, 23);

        SkemaInfus.setHighlighter(null);
        SkemaInfus.setName("SkemaInfus"); // NOI18N
        SkemaInfus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SkemaInfusKeyPressed(evt);
            }
        });
        FormInput.add(SkemaInfus);
        SkemaInfus.setBounds(240, 700, 290, 23);

        jLabel59.setText("Drain:");
        jLabel59.setName("jLabel59"); // NOI18N
        FormInput.add(jLabel59);
        jLabel59.setBounds(150, 740, 80, 14);

        Drain.setHighlighter(null);
        Drain.setName("Drain"); // NOI18N
        Drain.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DrainKeyPressed(evt);
            }
        });
        FormInput.add(Drain);
        Drain.setBounds(240, 730, 290, 24);

        jLabel60.setText("Oksigen:");
        jLabel60.setName("jLabel60"); // NOI18N
        FormInput.add(jLabel60);
        jLabel60.setBounds(540, 610, 80, 23);

        Oksigen.setHighlighter(null);
        Oksigen.setName("Oksigen"); // NOI18N
        Oksigen.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                OksigenKeyPressed(evt);
            }
        });
        FormInput.add(Oksigen);
        Oksigen.setBounds(630, 610, 240, 23);

        jLabel61.setText("Syring Pump:");
        jLabel61.setName("jLabel61"); // NOI18N
        FormInput.add(jLabel61);
        jLabel61.setBounds(540, 640, 80, 23);

        Syring.setHighlighter(null);
        Syring.setName("Syring"); // NOI18N
        Syring.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SyringKeyPressed(evt);
            }
        });
        FormInput.add(Syring);
        Syring.setBounds(630, 640, 240, 23);

        jLabel62.setText("Kasur Dekubitus:");
        jLabel62.setName("jLabel62"); // NOI18N
        FormInput.add(jLabel62);
        jLabel62.setBounds(520, 670, 100, 23);

        Dekubitus.setHighlighter(null);
        Dekubitus.setName("Dekubitus"); // NOI18N
        Dekubitus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DekubitusKeyPressed(evt);
            }
        });
        FormInput.add(Dekubitus);
        Dekubitus.setBounds(630, 670, 240, 23);

        jLabel63.setText("CVP:");
        jLabel63.setName("jLabel63"); // NOI18N
        FormInput.add(jLabel63);
        jLabel63.setBounds(540, 700, 80, 23);

        Cvp.setHighlighter(null);
        Cvp.setName("Cvp"); // NOI18N
        Cvp.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CvpKeyPressed(evt);
            }
        });
        FormInput.add(Cvp);
        Cvp.setBounds(630, 700, 240, 23);

        jLabel64.setText("Lain-lain:");
        jLabel64.setName("jLabel64"); // NOI18N
        FormInput.add(jLabel64);
        jLabel64.setBounds(540, 740, 80, 14);

        Lain.setHighlighter(null);
        Lain.setName("Lain"); // NOI18N
        Lain.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LainKeyPressed(evt);
            }
        });
        FormInput.add(Lain);
        Lain.setBounds(630, 730, 240, 24);

        jLabel65.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel65.setText("Hasil Pemeriksaan yang disertakan:");
        jLabel65.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel65.setName("jLabel65"); // NOI18N
        FormInput.add(jLabel65);
        jLabel65.setBounds(20, 760, 240, 23);

        jLabel66.setText("Laboratorium:");
        jLabel66.setName("jLabel66"); // NOI18N
        FormInput.add(jLabel66);
        jLabel66.setBounds(20, 790, 80, 23);

        Lab.setHighlighter(null);
        Lab.setName("Lab"); // NOI18N
        Lab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                LabKeyPressed(evt);
            }
        });
        FormInput.add(Lab);
        Lab.setBounds(110, 790, 760, 23);

        jLabel67.setText("Radiologi:");
        jLabel67.setName("jLabel67"); // NOI18N
        FormInput.add(jLabel67);
        jLabel67.setBounds(20, 820, 80, 23);

        Rad.setHighlighter(null);
        Rad.setName("Rad"); // NOI18N
        Rad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RadKeyPressed(evt);
            }
        });
        FormInput.add(Rad);
        Rad.setBounds(110, 820, 760, 23);

        Ct.setHighlighter(null);
        Ct.setName("Ct"); // NOI18N
        Ct.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                CtKeyPressed(evt);
            }
        });
        FormInput.add(Ct);
        Ct.setBounds(110, 850, 760, 23);

        jLabel68.setText("CT Scan:");
        jLabel68.setName("jLabel68"); // NOI18N
        FormInput.add(jLabel68);
        jLabel68.setBounds(20, 850, 80, 23);

        jLabel69.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel69.setText("Obat-obatan:");
        jLabel69.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel69.setName("jLabel69"); // NOI18N
        FormInput.add(jLabel69);
        jLabel69.setBounds(20, 890, 240, 23);

        jLabel70.setText("Infus:");
        jLabel70.setName("jLabel70"); // NOI18N
        FormInput.add(jLabel70);
        jLabel70.setBounds(20, 920, 80, 23);

        Infus.setHighlighter(null);
        Infus.setName("Infus"); // NOI18N
        Infus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                InfusKeyPressed(evt);
            }
        });
        FormInput.add(Infus);
        Infus.setBounds(110, 920, 760, 23);

        jLabel71.setText("Obat injeksi:");
        jLabel71.setName("jLabel71"); // NOI18N
        FormInput.add(jLabel71);
        jLabel71.setBounds(20, 950, 80, 23);

        Injeksi.setHighlighter(null);
        Injeksi.setName("Injeksi"); // NOI18N
        Injeksi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                InjeksiKeyPressed(evt);
            }
        });
        FormInput.add(Injeksi);
        Injeksi.setBounds(110, 950, 760, 23);

        Oral.setHighlighter(null);
        Oral.setName("Oral"); // NOI18N
        Oral.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                OralKeyPressed(evt);
            }
        });
        FormInput.add(Oral);
        Oral.setBounds(110, 980, 760, 23);

        jLabel72.setText("Obat Oral:");
        jLabel72.setName("jLabel72"); // NOI18N
        FormInput.add(jLabel72);
        jLabel72.setBounds(20, 980, 80, 23);

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
        BtnDokter1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnDokter1KeyPressed(evt);
            }
        });
        FormInput.add(BtnDokter1);
        BtnDokter1.setBounds(480, 120, 28, 23);

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
        BtnDokter2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnDokter2KeyPressed(evt);
            }
        });
        FormInput.add(BtnDokter2);
        BtnDokter2.setBounds(850, 80, 28, 23);

        DiagnosaSekarang.setHighlighter(null);
        DiagnosaSekarang.setName("DiagnosaSekarang"); // NOI18N
        DiagnosaSekarang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaSekarangKeyPressed(evt);
            }
        });
        FormInput.add(DiagnosaSekarang);
        DiagnosaSekarang.setBounds(120, 150, 320, 23);

        Alergi.setHighlighter(null);
        Alergi.setName("Alergi"); // NOI18N
        Alergi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AlergiKeyPressed(evt);
            }
        });
        FormInput.add(Alergi);
        Alergi.setBounds(490, 150, 370, 23);

        PetugasPendamping.setHighlighter(null);
        PetugasPendamping.setName("PetugasPendamping"); // NOI18N
        PetugasPendamping.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                PetugasPendampingKeyPressed(evt);
            }
        });
        FormInput.add(PetugasPendamping);
        PetugasPendamping.setBounds(450, 180, 410, 23);

        KuSesudahTransfer.setName("KuSesudahTransfer"); // NOI18N
        FormInput.add(KuSesudahTransfer);
        KuSesudahTransfer.setBounds(550, 230, 310, 24);

        KuSebelumTransfer.setName("KuSebelumTransfer"); // NOI18N
        FormInput.add(KuSebelumTransfer);
        KuSebelumTransfer.setBounds(240, 230, 290, 24);

        jLabel37.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel37.setText("Keseimbangan Cairan");
        jLabel37.setName("jLabel37"); // NOI18N
        FormInput.add(jLabel37);
        jLabel37.setBounds(60, 500, 170, 23);

        jLabel38.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel38.setText("Mobilisasi");
        jLabel38.setName("jLabel38"); // NOI18N
        FormInput.add(jLabel38);
        jLabel38.setBounds(60, 530, 170, 23);

        scrollInput.setViewportView(FormInput);

        internalFrame2.add(scrollInput, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Input Transfer Pasien", internalFrame2);

        internalFrame3.setBorder(null);
        internalFrame3.setName("internalFrame3"); // NOI18N
        internalFrame3.setLayout(new java.awt.BorderLayout(1, 1));

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

        internalFrame3.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel19.setText("Tgl.Pindah :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(68, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10-05-2025" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10-05-2025" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(197, 23));
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
        jLabel7.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(LCount);

        internalFrame3.add(panelGlass9, java.awt.BorderLayout.PAGE_END);

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
        FormPhoto.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), " Bukti Pengambilan Persetujuan : ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
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

        internalFrame3.add(PanelAccor, java.awt.BorderLayout.EAST);

        TabRawat.addTab("Data Transfer Pasien", internalFrame3);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (TNoRM.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "Nama Pasien");
        } else if (NmPetugasMenyerahkan.getText().trim().equals("")) {
            Valid.textKosong(BtnDokter, "Petugas Yang Menyerahkan");
        } else if (NmPetugasMenerima.getText().trim().equals("")) {
            Valid.textKosong(BtnDokter, "Petugas Yang Menerima");
        } else if (AsalRuang.getText().trim().equals("")) {
            Valid.textKosong(BtnMenerima, "Asal Ruang");
        } else if (AsalRuang.getText().trim().equals("")) {
            Valid.textKosong(AsalRuang, "Asal Ruang");
        } else if (RuangSelanjutnya.getText().trim().equals("")) {
            Valid.textKosong(RuangSelanjutnya, "Ruang Selanjutnya");
        } else if (DiagnosaUtama.getText().trim().equals("")) {
            Valid.textKosong(DiagnosaUtama, "Diagnosa Utama");
        } else if (Alergi.getText().trim().equals("")) {
            Valid.textKosong(Alergi, "Alergi");
        } else if (Infus.getText().trim().equals("")) {
            Valid.textKosong(Infus, "Infus");
        } else if (TdSebelumTransfer.getText().trim().equals("")) {
            Valid.textKosong(TdSebelumTransfer, "TD Sebelum Transfer");
        } else if (NadiSebelumTransfer.getText().trim().equals("")) {
            Valid.textKosong(NadiSebelumTransfer, "Nadi Sebelum Transfer");
        } else if (RRSebelumTransfer.getText().trim().equals("")) {
            Valid.textKosong(RRSebelumTransfer, "RR Sebelum Transfer");
        } else if (SuhuSebelumTransfer.getText().trim().equals("")) {
            Valid.textKosong(SuhuSebelumTransfer, "Suhu Sebelum Transfer");
        } else if (Lab.getText().trim().equals("")) {
            Valid.textKosong(Lab, "Lab");
        // "Setelah Pindah" (TD/Nadi/RR/Suhu Setelah) dibuat OPSIONAL: perawat yang
        // menyerahkan bisa simpan bagian sebelum dulu; bagian setelah dilengkapi
        // perawat penerima lewat Ganti (izin sudah mengizinkan nip_menerima).
        } else if (Rad.getText().trim().equals("")) {
            Valid.textKosong(Rad, "Radiologi");
        } else {
            if (akses.getkode().equals("Admin Utama")) {
                simpan();
            } else {
                if (akses.getkode().equals(KdPetugasMenerima.getText()) || akses.getkode().equals(KdPetugasMenyerahkan.getText())) {
                    simpan();
                } else {
                    JOptionPane.showMessageDialog(null, "Harus salah satu petugas sesuai user login..!!");
                }
            }
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnMenerima, BtnBatal);
        }
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
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
                String nipMenyerahkan = tbObat.getValueAt(tbObat.getSelectedRow(), 55).toString();
                String nipMenerima = tbObat.getValueAt(tbObat.getSelectedRow(), 57).toString();
                if (akses.getkode().equals(nipMenyerahkan) || akses.getkode().equals(nipMenerima)
                        || akses.getkode().equals(KdPetugasMenyerahkan.getText().trim()) || akses.getkode().equals(KdPetugasMenerima.getText().trim())) {
                    hapus();
                } else {
                    JOptionPane.showMessageDialog(null, "Harus salah satu petugas sesuai user login..!!");
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
            Valid.textKosong(TNoRw, "Nama Pasien");
        } else if (NmPetugasMenyerahkan.getText().trim().equals("")) {
            Valid.textKosong(BtnDokter, "Petugas Yang Menyerahkan");
        } else if (NmPetugasMenerima.getText().trim().equals("")) {
            Valid.textKosong(BtnDokter, "Petugas Yang Menerima");
        } else if (AsalRuang.getText().trim().equals("")) {
            Valid.textKosong(BtnMenerima, "Asal Ruang");
        } else if (AsalRuang.getText().trim().equals("")) {
            Valid.textKosong(AsalRuang, "Asal Ruang");
        } else if (RuangSelanjutnya.getText().trim().equals("")) {
            Valid.textKosong(RuangSelanjutnya, "Ruang Selanjutnya");
        } else if (DiagnosaUtama.getText().trim().equals("")) {
            Valid.textKosong(DiagnosaUtama, "Diagnosa Utama");
        } else if (Alergi.getText().trim().equals("")) {
            Valid.textKosong(Alergi, "Alergi");
        } else if (Infus.getText().trim().equals("")) {
            Valid.textKosong(Infus, "Infus");
        } else if (TdSebelumTransfer.getText().trim().equals("")) {
            Valid.textKosong(TdSebelumTransfer, "TD Sebelum Transfer");
        } else if (NadiSebelumTransfer.getText().trim().equals("")) {
            Valid.textKosong(NadiSebelumTransfer, "Nadi Sebelum Transfer");
        } else if (RRSebelumTransfer.getText().trim().equals("")) {
            Valid.textKosong(RRSebelumTransfer, "RR Sebelum Transfer");
        } else if (SuhuSebelumTransfer.getText().trim().equals("")) {
            Valid.textKosong(SuhuSebelumTransfer, "Suhu Sebelum Transfer");
        } else if (Lab.getText().trim().equals("")) {
            Valid.textKosong(Lab, "Lab");
        // "Setelah Pindah" (TD/Nadi/RR/Suhu Setelah) dibuat OPSIONAL: perawat yang
        // menyerahkan bisa simpan bagian sebelum dulu; bagian setelah dilengkapi
        // perawat penerima lewat Ganti (izin sudah mengizinkan nip_menerima).
        } else if (Rad.getText().trim().equals("")) {
            Valid.textKosong(Rad, "Radiologi");
        } else {
            if (tbObat.getSelectedRow() > -1) {
                if (akses.getkode().equals("Admin Utama")) {
                    ganti();
                } else {
                    String nipMenyerahkan = tbObat.getValueAt(tbObat.getSelectedRow(), 55).toString();
                    String nipMenerima = tbObat.getValueAt(tbObat.getSelectedRow(), 57).toString();
                    if (akses.getkode().equals(nipMenyerahkan) || akses.getkode().equals(nipMenerima)
                            || akses.getkode().equals(KdPetugasMenyerahkan.getText().trim()) || akses.getkode().equals(KdPetugasMenerima.getText().trim())) {
                        ganti();
                    } else {
                        JOptionPane.showMessageDialog(null, "Harus salah satu petugas sesuai user login..!!");
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
            try {
                if (TCari.getText().trim().equals("")) {
                    ps = koneksi.prepareStatement(
                            "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir,"
                            + "transfer_internal_rs.tanggal_masuk,transfer_internal_rs.tanggal_pindah,transfer_internal_rs.asal_ruang,"
                            + "transfer_internal_rs.ruang_selanjutnya,transfer_internal_rs.diagnosa_utama,transfer_internal_rs.diagnosa_sekunder,"
                            + "transfer_internal_rs.indikasi_pindah_ruang,transfer_internal_rs.keterangan_indikasi_pindah_ruang,"
                            + "transfer_internal_rs.prosedur_yang_sudah_dilakukan,transfer_internal_rs.obat_yang_telah_diberikan,"
                            + "transfer_internal_rs.metode_pemindahan_pasien,transfer_internal_rs.peralatan_yang_menyertai,"
                            + "transfer_internal_rs.keterangan_peralatan_yang_menyertai,transfer_internal_rs.pemeriksaan_penunjang_yang_dilakukan,"
                            + "transfer_internal_rs.pasien_keluarga_menyetujui,transfer_internal_rs.nama_menyetujui,transfer_internal_rs.hubungan_menyetujui,"
                            + "transfer_internal_rs.keluhan_utama_sebelum_transfer,transfer_internal_rs.keadaan_umum_sebelum_transfer,"
                            + "transfer_internal_rs.td_sebelum_transfer,transfer_internal_rs.nadi_sebelum_transfer,transfer_internal_rs.rr_sebelum_transfer,"
                            + "transfer_internal_rs.suhu_sebelum_transfer,transfer_internal_rs.keluhan_utama_sesudah_transfer,"
                            + "transfer_internal_rs.keadaan_umum_sesudah_transfer,transfer_internal_rs.td_sesudah_transfer,"
                            + "transfer_internal_rs.nadi_sesudah_transfer,transfer_internal_rs.rr_sesudah_transfer,transfer_internal_rs.suhu_sesudah_transfer,"
                            + "transfer_internal_rs.nip_menyerahkan,petugasmenyerahkan.nama as petugasmenyerahkan,transfer_internal_rs.nip_menerima,"
                            + "petugasmenerima.nama as petugasmenerima "
                            + "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                            + "inner join transfer_internal_rs on reg_periksa.no_rawat=transfer_internal_rs.no_rawat "
                            + "inner join petugas as petugasmenyerahkan on transfer_internal_rs.nip_menyerahkan=petugasmenyerahkan.nip "
                            + "inner join petugas as petugasmenerima on transfer_internal_rs.nip_menerima=petugasmenerima.nip where "
                            + "transfer_internal_rs.tanggal_pindah between ? and ? order by transfer_internal_rs.tanggal_pindah");
                } else {
                    ps = koneksi.prepareStatement(
                            "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir,"
                            + "transfer_internal_rs.tanggal_masuk,transfer_internal_rs.tanggal_pindah,transfer_internal_rs.asal_ruang,"
                            + "transfer_internal_rs.ruang_selanjutnya,transfer_internal_rs.diagnosa_utama,transfer_internal_rs.diagnosa_sekunder,"
                            + "transfer_internal_rs.indikasi_pindah_ruang,transfer_internal_rs.keterangan_indikasi_pindah_ruang,"
                            + "transfer_internal_rs.prosedur_yang_sudah_dilakukan,transfer_internal_rs.obat_yang_telah_diberikan,"
                            + "transfer_internal_rs.metode_pemindahan_pasien,transfer_internal_rs.peralatan_yang_menyertai,"
                            + "transfer_internal_rs.keterangan_peralatan_yang_menyertai,transfer_internal_rs.pemeriksaan_penunjang_yang_dilakukan,"
                            + "transfer_internal_rs.pasien_keluarga_menyetujui,transfer_internal_rs.nama_menyetujui,transfer_internal_rs.hubungan_menyetujui,"
                            + "transfer_internal_rs.keluhan_utama_sebelum_transfer,transfer_internal_rs.keadaan_umum_sebelum_transfer,"
                            + "transfer_internal_rs.td_sebelum_transfer,transfer_internal_rs.nadi_sebelum_transfer,transfer_internal_rs.rr_sebelum_transfer,"
                            + "transfer_internal_rs.suhu_sebelum_transfer,transfer_internal_rs.keluhan_utama_sesudah_transfer,"
                            + "transfer_internal_rs.keadaan_umum_sesudah_transfer,transfer_internal_rs.td_sesudah_transfer,"
                            + "transfer_internal_rs.nadi_sesudah_transfer,transfer_internal_rs.rr_sesudah_transfer,transfer_internal_rs.suhu_sesudah_transfer,"
                            + "transfer_internal_rs.nip_menyerahkan,petugasmenyerahkan.nama as petugasmenyerahkan,transfer_internal_rs.nip_menerima,"
                            + "petugasmenerima.nama as petugasmenerima "
                            + "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                            + "inner join transfer_internal_rs on reg_periksa.no_rawat=transfer_internal_rs.no_rawat "
                            + "inner join petugas as petugasmenyerahkan on transfer_internal_rs.nip_menyerahkan=petugasmenyerahkan.nip "
                            + "inner join petugas as petugasmenerima on transfer_internal_rs.nip_menerima=petugasmenerima.nip where "
                            + "transfer_internal_rs.tanggal_pindah between ? and ? and (reg_periksa.no_rawat like ? or pasien.no_rkm_medis like ? or pasien.nm_pasien like ? or "
                            + "transfer_internal_rs.nip_menyerahkan like ? or petugasmenyerahkan like ?) order by transfer_internal_rs.tanggal_pindah");
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
                        ps.setString(7, "%" + TCari.getText() + "%");
                    }
                    rs = ps.executeQuery();
                    htmlContent = new StringBuilder();
                    htmlContent.append(
                            "<tr class='isi'>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>No.Rawat</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>No.RM</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Nama Pasien</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Tgl.Lahir</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>J.K.</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Tanggal Masuk</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Tanggal Pindah</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Indikasi Pindah</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Keterangan Indikasi Pindah</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Asal Ruang Rawat / Poliklinik</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Ruang Rawat Selanjutnya</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Metode Pemindahan</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Diagnosa Utama</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Diagnosa Sekunder</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Prosedur Yang Sudah Dilakukan</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Obat Yang Telah Diberikan</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Pemeriksaan Penunjang Yang Sudah Dilakukan</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Peralatan Yang Menyertai</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Keterangan Peralatan Menyertai</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Menyetujui Pemindahan</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Nama Keluarga/Penanggung Jawab</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Hubungan</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Keadaan Umum SbT</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>TD SbT</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Nadi SbT</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>RR SbT</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Suhu Sbt</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Keluhan Utama Sebelum Transfer</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Keadaan Umum StT</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>TD StT</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Nadi StT</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>RR StT</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Suhu Stt</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Keluhan Utama Setelah Transfer</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>NIP Menyerahkan</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Petugas Yang Menyerahkan</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>NIP Menerima</b></td>"
                            + "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Petugas Yang Menerima</b></td>"
                            + "</tr>"
                    );
                    while (rs.next()) {
                        htmlContent.append(
                                "<tr class='isi'>"
                                + "<td valign='top'>" + rs.getString("no_rawat") + "</td>"
                                + "<td valign='top'>" + rs.getString("no_rkm_medis") + "</td>"
                                + "<td valign='top'>" + rs.getString("nm_pasien") + "</td>"
                                + "<td valign='top'>" + rs.getString("tgl_lahir") + "</td>"
                                + "<td valign='top'>" + rs.getString("jk") + "</td>"
                                + "<td valign='top'>" + rs.getString("tanggal_masuk") + "</td>"
                                + "<td valign='top'>" + rs.getString("tanggal_pindah") + "</td>"
                                + "<td valign='top'>" + rs.getString("indikasi_pindah_ruang") + "</td>"
                                + "<td valign='top'>" + rs.getString("keterangan_indikasi_pindah_ruang") + "</td>"
                                + "<td valign='top'>" + rs.getString("asal_ruang") + "</td>"
                                + "<td valign='top'>" + rs.getString("ruang_selanjutnya") + "</td>"
                                + "<td valign='top'>" + rs.getString("metode_pemindahan_pasien") + "</td>"
                                + "<td valign='top'>" + rs.getString("diagnosa_utama") + "</td>"
                                + "<td valign='top'>" + rs.getString("diagnosa_sekunder") + "</td>"
                                + "<td valign='top'>" + rs.getString("prosedur_yang_sudah_dilakukan") + "</td>"
                                + "<td valign='top'>" + rs.getString("obat_yang_telah_diberikan") + "</td>"
                                + "<td valign='top'>" + rs.getString("pemeriksaan_penunjang_yang_dilakukan") + "</td>"
                                + "<td valign='top'>" + rs.getString("peralatan_yang_menyertai") + "</td>"
                                + "<td valign='top'>" + rs.getString("keterangan_peralatan_yang_menyertai") + "</td>"
                                + "<td valign='top'>" + rs.getString("pasien_keluarga_menyetujui") + "</td>"
                                + "<td valign='top'>" + rs.getString("nama_menyetujui") + "</td>"
                                + "<td valign='top'>" + rs.getString("hubungan_menyetujui") + "</td>"
                                + "<td valign='top'>" + rs.getString("keadaan_umum_sebelum_transfer") + "</td>"
                                + "<td valign='top'>" + rs.getString("td_sebelum_transfer") + "</td>"
                                + "<td valign='top'>" + rs.getString("nadi_sebelum_transfer") + "</td>"
                                + "<td valign='top'>" + rs.getString("rr_sebelum_transfer") + "</td>"
                                + "<td valign='top'>" + rs.getString("suhu_sebelum_transfer") + "</td>"
                                + "<td valign='top'>" + rs.getString("keluhan_utama_sebelum_transfer") + "</td>"
                                + "<td valign='top'>" + rs.getString("keadaan_umum_sesudah_transfer") + "</td>"
                                + "<td valign='top'>" + rs.getString("td_sesudah_transfer") + "</td>"
                                + "<td valign='top'>" + rs.getString("nadi_sesudah_transfer") + "</td>"
                                + "<td valign='top'>" + rs.getString("rr_sesudah_transfer") + "</td>"
                                + "<td valign='top'>" + rs.getString("suhu_sesudah_transfer") + "</td>"
                                + "<td valign='top'>" + rs.getString("keluhan_utama_sesudah_transfer") + "</td>"
                                + "<td valign='top'>" + rs.getString("nip_menyerahkan") + "</td>"
                                + "<td valign='top'>" + rs.getString("petugasmenyerahkan") + "</td>"
                                + "<td valign='top'>" + rs.getString("nip_menerima") + "</td>"
                                + "<td valign='top'>" + rs.getString("petugasmenerima") + "</td>"
                                + "</tr>");
                    }
                    LoadHTML.setText(
                            "<html>"
                            + "<table width='4000' border='0' align='center' cellpadding='1px' cellspacing='0' class='tbl_form'>"
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

                    File f = new File("TransferPasienAntarRuang.html");
                    BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                    bw.write(LoadHTML.getText().replaceAll("<head>", "<head>"
                            + "<link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" />"
                            + "<table width='4000px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"
                            + "<tr class='isi2'>"
                            + "<td valign='top' align='center'>"
                            + "<font size='4' face='Tahoma'>" + akses.getnamars() + "</font><br>"
                            + akses.getalamatrs() + ", " + akses.getkabupatenrs() + ", " + akses.getpropinsirs() + "<br>"
                            + akses.getkontakrs() + ", E-mail : " + akses.getemailrs() + "<br><br>"
                            + "<font size='2' face='Tahoma'>DATA TRANSFER PASIEN ANTAR RUANG<br><br></font>"
                            + "</td>"
                            + "</tr>"
                            + "</table>")
                    );
                    bw.close();
                    Desktop.getDesktop().browse(f.toURI());
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
                isPhoto();
                panggilPhoto();
            } catch (java.lang.NullPointerException e) {
            }
            if ((evt.getClickCount() == 2) && (tbObat.getSelectedColumn() == 0)) {
                TabRawat.setSelectedIndex(0);
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
                    getData();
                    TabRawat.setSelectedIndex(0);
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
}//GEN-LAST:event_tbObatKeyPressed

    private void TabRawatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabRawatMouseClicked
        if (TabRawat.getSelectedIndex() == 1) {
            tampil();
        }
    }//GEN-LAST:event_TabRawatMouseClicked

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            isRawat();
        } else {
            //Valid.pindah(evt,TCari,BtnDokter);
        }
    }//GEN-LAST:event_TNoRwKeyPressed

    private void TanggalMasukKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalMasukKeyPressed
        Valid.pindah2(evt, TNoRw, TanggalPindah);
    }//GEN-LAST:event_TanggalMasukKeyPressed

    private void TanggalPindahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalPindahKeyPressed
        //   Valid.pindah2(evt,TanggalMasuk,IndikasiPindah);
    }//GEN-LAST:event_TanggalPindahKeyPressed

    private void dpjpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dpjpKeyPressed
        //   Valid.pindah(evt,IndikasiPindah,AsalRuang);
    }//GEN-LAST:event_dpjpKeyPressed

    private void SaturasiSebelumTransferKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SaturasiSebelumTransferKeyPressed
        // Valid.pindah(evt,Rekammedis,MenyetujuiPemindahan);
    }//GEN-LAST:event_SaturasiSebelumTransferKeyPressed

    private void RuangSelanjutnyaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RuangSelanjutnyaKeyPressed
        Valid.pindah(evt, AsalRuang, Derajat);
    }//GEN-LAST:event_RuangSelanjutnyaKeyPressed

    private void RekammedisKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RekammedisKeyPressed
        Valid.pindah(evt, PetugasPendamping, SaturasiSebelumTransfer);
    }//GEN-LAST:event_RekammedisKeyPressed

    private void DiagnosaUtamaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosaUtamaKeyPressed
        Valid.pindah(evt, Derajat, DiagnosaSekarang);
    }//GEN-LAST:event_DiagnosaUtamaKeyPressed

    private void AsalRuangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AsalRuangKeyPressed
        Valid.pindah(evt, dpjp, RuangSelanjutnya);
    }//GEN-LAST:event_AsalRuangKeyPressed

    private void DerajatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DerajatKeyPressed
        Valid.pindah(evt, RuangSelanjutnya, DiagnosaUtama);
    }//GEN-LAST:event_DerajatKeyPressed

    private void SaturasiSesudahTransferKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SaturasiSesudahTransferKeyPressed
        //   Valid.pindah(evt,MenyetujuiPemindahan,HubunganMenyetujui);
    }//GEN-LAST:event_SaturasiSesudahTransferKeyPressed

    private void KesadaranSebelumTransferKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KesadaranSebelumTransferKeyPressed
        //   Valid.pindah(evt,HubunganMenyetujui,TDSebelumTransfer);
    }//GEN-LAST:event_KesadaranSebelumTransferKeyPressed

    private void TdSebelumTransferKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TdSebelumTransferKeyPressed
        Valid.pindah(evt, KesadaranSebelumTransfer, NadiSebelumTransfer);
    }//GEN-LAST:event_TdSebelumTransferKeyPressed

    private void NadiSebelumTransferKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NadiSebelumTransferKeyPressed
        Valid.pindah(evt, TdSebelumTransfer, RRSebelumTransfer);
    }//GEN-LAST:event_NadiSebelumTransferKeyPressed

    private void RRSebelumTransferKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RRSebelumTransferKeyPressed
        Valid.pindah(evt, NadiSebelumTransfer, SuhuSebelumTransfer);
    }//GEN-LAST:event_RRSebelumTransferKeyPressed

    private void SuhuSebelumTransferKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SuhuSebelumTransferKeyPressed
        Valid.pindah(evt, RRSebelumTransfer, Lab);
    }//GEN-LAST:event_SuhuSebelumTransferKeyPressed

    private void KesadaranSesudahTransferKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KesadaranSesudahTransferKeyPressed
        Valid.pindah(evt, Lab, TDSetelahTransfer);
    }//GEN-LAST:event_KesadaranSesudahTransferKeyPressed

    private void TDSetelahTransferKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TDSetelahTransferKeyPressed
        Valid.pindah(evt, KesadaranSesudahTransfer, NadiSetelahTransfer);
    }//GEN-LAST:event_TDSetelahTransferKeyPressed

    private void NadiSetelahTransferKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NadiSetelahTransferKeyPressed
        Valid.pindah(evt, TDSetelahTransfer, RRSetelahTransfer);
    }//GEN-LAST:event_NadiSetelahTransferKeyPressed

    private void RRSetelahTransferKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RRSetelahTransferKeyPressed
        Valid.pindah(evt, NadiSetelahTransfer, SuhuSetelahTransfer);
    }//GEN-LAST:event_RRSetelahTransferKeyPressed

    private void SuhuSetelahTransferKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SuhuSetelahTransferKeyPressed
        Valid.pindah(evt, RRSetelahTransfer, Rad);
    }//GEN-LAST:event_SuhuSetelahTransferKeyPressed

    private void BtnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokterActionPerformed
        pilihan = 1;
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setAlwaysOnTop(false);
        petugas.setVisible(true);
    }//GEN-LAST:event_BtnDokterActionPerformed

    private void BtnDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnDokterKeyPressed
        Valid.pindah(evt, Rad, BtnMenerima);
    }//GEN-LAST:event_BtnDokterKeyPressed

    private void BtnMenerimaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnMenerimaActionPerformed
        pilihan = 2;
        petugas.isCek();
        petugas.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setAlwaysOnTop(false);
        petugas.setVisible(true);
    }//GEN-LAST:event_BtnMenerimaActionPerformed

    private void BtnMenerimaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnMenerimaKeyPressed
        Valid.pindah(evt, Rad, BtnSimpan);
    }//GEN-LAST:event_BtnMenerimaKeyPressed

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
                Sequel.queryu("delete from antripersetujuantransferantarruang");
                Sequel.queryu("insert into antripersetujuantransferantarruang values('" + tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "','" + tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString() + "')");
                Sequel.queryu("delete from bukti_persetujuan_transfer_internal_rs where no_rawat='" + tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "' and tanggal_masuk='" + tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString() + "'");
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

    private void kddpjpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kddpjpKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_kddpjpKeyPressed

    private void kdkonsulenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_kdkonsulenKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_kdkonsulenKeyPressed

    private void konsulenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_konsulenKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_konsulenKeyPressed

    private void CatatanSebelumTransferKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CatatanSebelumTransferKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_CatatanSebelumTransferKeyPressed

    private void CatatanSesudahTransferKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CatatanSesudahTransferKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_CatatanSesudahTransferKeyPressed

    private void DietKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DietKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DietKeyPressed

    private void MobilisasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MobilisasiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_MobilisasiKeyPressed

    private void KeseimbanganCairanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KeseimbanganCairanKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_KeseimbanganCairanKeyPressed

    private void PerawatanLukaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PerawatanLukaKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_PerawatanLukaKeyPressed

    private void CathKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CathKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_CathKeyPressed

    private void KateterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KateterKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_KateterKeyPressed

    private void NgtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NgtKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_NgtKeyPressed

    private void SkemaInfusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SkemaInfusKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_SkemaInfusKeyPressed

    private void DrainKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DrainKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DrainKeyPressed

    private void OksigenKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_OksigenKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_OksigenKeyPressed

    private void SyringKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SyringKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_SyringKeyPressed

    private void DekubitusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DekubitusKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DekubitusKeyPressed

    private void CvpKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CvpKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_CvpKeyPressed

    private void LainKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LainKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_LainKeyPressed

    private void LabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_LabKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_LabKeyPressed

    private void RadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RadKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_RadKeyPressed

    private void CtKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_CtKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_CtKeyPressed

    private void InfusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_InfusKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_InfusKeyPressed

    private void InjeksiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_InjeksiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_InjeksiKeyPressed

    private void OralKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_OralKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_OralKeyPressed

    private void BtnDokter1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokter1ActionPerformed
        pilihan = 2;
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setAlwaysOnTop(false);
        dokter.setVisible(true);
    }//GEN-LAST:event_BtnDokter1ActionPerformed

    private void BtnDokter1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnDokter1KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnDokter1KeyPressed

    private void BtnDokter2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokter2ActionPerformed
        pilihan = 1;
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setAlwaysOnTop(false);
        dokter.setVisible(true);
    }//GEN-LAST:event_BtnDokter2ActionPerformed

    private void BtnDokter2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnDokter2KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnDokter2KeyPressed

    private void DiagnosaSekarangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosaSekarangKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DiagnosaSekarangKeyPressed

    private void AlergiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AlergiKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_AlergiKeyPressed

    private void PetugasPendampingKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PetugasPendampingKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_PetugasPendampingKeyPressed

    private void MnCetakTransferActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnCetakTransferActionPerformed
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
            String nmPen = NmPetugasMenerima.getText();
            if (nmPen == null || nmPen.trim().isEmpty()) {
                nmPen = Sequel.cariIsi("select ifnull(pt.nama, t.nip_menerima) from transfer_internal_rs t left join petugas pt on pt.nip=t.nip_menerima where t.no_rawat=? order by t.tanggal_pindah desc limit 1", tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
            }
            param.put("nm_penerima", nmPen);

            finger = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString());
            param.put("finger", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbObat.getValueAt(tbObat.getSelectedRow(), 10).toString() + "\nID " + (finger.equals("") ? tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString() : finger) + "\n" + Valid.SetTgl3(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString()));

            finger2 = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", tbObat.getValueAt(tbObat.getSelectedRow(), 54).toString());
            param.put("finger2", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbObat.getValueAt(tbObat.getSelectedRow(), 55).toString() + "\nID " + (finger2.equals("") ? tbObat.getValueAt(tbObat.getSelectedRow(), 54).toString() : finger2) + "\n" + Valid.SetTgl3(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString()));

            finger3 = Sequel.cariIsi("select sha1(sidikjari.sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", tbObat.getValueAt(tbObat.getSelectedRow(), 56).toString());
            param.put("finger3", "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + "\nDitandatangani secara elektronik oleh " + tbObat.getValueAt(tbObat.getSelectedRow(), 57).toString() + "\nID " + (finger3.equals("") ? tbObat.getValueAt(tbObat.getSelectedRow(), 56).toString() : finger3) + "\n" + Valid.SetTgl3(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString()));

            Valid.MyReportqry("rptCetakTransferInternalRs.jasper", "report", "::[ Formulir Transfer Internal Rumah Sakit ]::",
                    "SELECT reg_periksa.no_rawat, reg_periksa.no_rkm_medis, pasien.nm_pasien, pasien.tgl_lahir, transfer_internal_rs.tanggal_masuk, transfer_internal_rs.tanggal_pindah, "
                    + "transfer_internal_rs.asal_ruang, transfer_internal_rs.ruang_selanjutnya, transfer_internal_rs.dpjp, IFNULL(dokterkonsulen.nm_dokter, transfer_internal_rs.konsulen) as konsulen, transfer_internal_rs.diagnosa_masuk, "
                    + "transfer_internal_rs.diagnosa_sekarang, transfer_internal_rs.alergi, transfer_internal_rs.derajat, transfer_internal_rs.pendamping, transfer_internal_rs.keadaan_umum_sebelum_transfer, "
                    + "transfer_internal_rs.keadaan_umum_sesudah_transfer, transfer_internal_rs.kesadaran_sebelum_transfer, transfer_internal_rs.kesadaran_sesudah_transfer, transfer_internal_rs.td_sebelum_transfer, "
                    + "transfer_internal_rs.td_sesudah_transfer, transfer_internal_rs.nadi_sebelum_transfer, transfer_internal_rs.nadi_sesudah_transfer, transfer_internal_rs.suhu_sebelum_transfer, transfer_internal_rs.suhu_sesudah_transfer, "
                    + "transfer_internal_rs.pernapasan_sebelum_transfer, transfer_internal_rs.pernapasan_sesudah_transfer, transfer_internal_rs.saturasi_sebelum_transfer, transfer_internal_rs.saturasi_sesudah_transfer, "
                    + "transfer_internal_rs.catatan_sebelum_transfer, transfer_internal_rs.catatan_sesudah_transfer, transfer_internal_rs.diet, transfer_internal_rs.keseimbangan_cairan, "
                    + "transfer_internal_rs.mobilisasi, transfer_internal_rs.perawatan_luka, transfer_internal_rs.iv, transfer_internal_rs.oksigen, transfer_internal_rs.kateter, transfer_internal_rs.syring, "
                    + "transfer_internal_rs.ngt, transfer_internal_rs.dekubitus, transfer_internal_rs.infus, transfer_internal_rs.cvp, transfer_internal_rs.drain, transfer_internal_rs.lain, transfer_internal_rs.lab, transfer_internal_rs.radiologi, "
                    + "transfer_internal_rs.ctscan, transfer_internal_rs.obat_infus, transfer_internal_rs.injeksi, transfer_internal_rs.oral, transfer_internal_rs.rekammedis, transfer_internal_rs.nip_menyerahkan, transfer_internal_rs.nip_menerima, "
                    + "IFNULL(petugas.nama, transfer_internal_rs.nip_menyerahkan) as nama, IFNULL(dokter.nm_dokter, transfer_internal_rs.dpjp) as nm_dokter FROM reg_periksa INNER JOIN pasien ON reg_periksa.no_rkm_medis = pasien.no_rkm_medis INNER JOIN transfer_internal_rs ON reg_periksa.no_rawat = transfer_internal_rs.no_rawat "
                    + "LEFT JOIN petugas ON transfer_internal_rs.nip_menyerahkan = petugas.nip "
                    + "LEFT JOIN dokter ON transfer_internal_rs.dpjp = dokter.kd_dokter "
                    + "LEFT JOIN dokter AS dokterkonsulen ON transfer_internal_rs.konsulen = dokterkonsulen.kd_dokter "
                    + "where reg_periksa.no_rawat='" + tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString() + "'", param);
        }
    }//GEN-LAST:event_MnCetakTransferActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMTransferInternalRs dialog = new RMTransferInternalRs(new javax.swing.JFrame(), true);
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
    private widget.TextBox AsalRuang;
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnDokter;
    private widget.Button BtnDokter1;
    private widget.Button BtnDokter2;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnMenerima;
    private widget.Button BtnPrint;
    private widget.Button BtnRefreshPhoto1;
    private widget.Button BtnSimpan;
    private widget.TextBox CatatanSebelumTransfer;
    private widget.TextBox CatatanSesudahTransfer;
    private widget.TextBox Cath;
    private widget.CekBox ChkAccor;
    private widget.TextBox Ct;
    private widget.TextBox Cvp;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.TextBox Dekubitus;
    private widget.ComboBox Derajat;
    private widget.TextBox DiagnosaSekarang;
    private widget.TextBox DiagnosaUtama;
    private widget.TextBox Diet;
    private widget.TextBox Drain;
    private widget.PanelBiasa FormInput;
    private widget.PanelBiasa FormPass3;
    private widget.PanelBiasa FormPhoto;
    private widget.TextBox Infus;
    private widget.TextBox Injeksi;
    private widget.TextBox Jk;
    private widget.TextBox Kateter;
    private widget.TextBox KdPetugasMenerima;
    private widget.TextBox KdPetugasMenyerahkan;
    private widget.ComboBox KesadaranSebelumTransfer;
    private widget.ComboBox KesadaranSesudahTransfer;
    private widget.TextBox KeseimbanganCairan;
    private widget.TextBox KuSebelumTransfer;
    private widget.TextBox KuSesudahTransfer;
    private widget.Label LCount;
    private widget.TextBox Lab;
    private widget.TextBox Lain;
    private widget.editorpane LoadHTML;
    private widget.editorpane LoadHTML2;
    private javax.swing.JMenuItem MnCetakTransfer;
    private widget.TextBox Mobilisasi;
    private widget.TextBox NadiSebelumTransfer;
    private widget.TextBox NadiSetelahTransfer;
    private widget.TextBox Ngt;
    private widget.TextBox NmPetugasMenerima;
    private widget.TextBox NmPetugasMenyerahkan;
    private widget.TextBox Oksigen;
    private widget.TextBox Oral;
    private widget.PanelBiasa PanelAccor;
    private widget.TextBox PerawatanLuka;
    private widget.TextBox PetugasPendamping;
    private widget.TextBox RRSebelumTransfer;
    private widget.TextBox RRSetelahTransfer;
    private widget.TextBox Rad;
    private widget.ComboBox Rekammedis;
    private widget.TextBox RuangSelanjutnya;
    private widget.TextBox SaturasiSebelumTransfer;
    private widget.TextBox SaturasiSesudahTransfer;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane Scroll5;
    private widget.TextBox SkemaInfus;
    private widget.TextBox SuhuSebelumTransfer;
    private widget.TextBox SuhuSetelahTransfer;
    private widget.TextBox Syring;
    private widget.TextBox TCari;
    private widget.TextBox TDSetelahTransfer;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private javax.swing.JTabbedPane TabRawat;
    private widget.Tanggal TanggalMasuk;
    private widget.Tanggal TanggalPindah;
    private widget.TextBox TdSebelumTransfer;
    private widget.TextBox TglLahir;
    private widget.Button btnAmbil;
    private widget.TextBox dpjp;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame2;
    private widget.InternalFrame internalFrame3;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel13;
    private widget.Label jLabel14;
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
    private widget.Label jLabel25;
    private widget.Label jLabel26;
    private widget.Label jLabel27;
    private widget.Label jLabel28;
    private widget.Label jLabel30;
    private widget.Label jLabel31;
    private widget.Label jLabel32;
    private widget.Label jLabel33;
    private widget.Label jLabel34;
    private widget.Label jLabel35;
    private widget.Label jLabel36;
    private widget.Label jLabel37;
    private widget.Label jLabel38;
    private widget.Label jLabel39;
    private widget.Label jLabel40;
    private widget.Label jLabel41;
    private widget.Label jLabel42;
    private widget.Label jLabel43;
    private widget.Label jLabel44;
    private widget.Label jLabel45;
    private widget.Label jLabel46;
    private widget.Label jLabel48;
    private widget.Label jLabel50;
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
    private widget.Label jLabel69;
    private widget.Label jLabel7;
    private widget.Label jLabel70;
    private widget.Label jLabel71;
    private widget.Label jLabel72;
    private widget.Label jLabel8;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator2;
    private widget.TextBox kddpjp;
    private widget.TextBox kdkonsulen;
    private widget.TextBox konsulen;
    private widget.Label label11;
    private widget.Label label12;
    private widget.Label label14;
    private widget.Label label15;
    private widget.Label label16;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.ScrollPane scrollInput;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            if (TCari.getText().trim().equals("")) {
                ps = koneksi.prepareStatement(
                        "SELECT transfer_internal_rs.no_rawat, reg_periksa.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, transfer_internal_rs.tanggal_masuk, "
                        + "transfer_internal_rs.tanggal_pindah, transfer_internal_rs.asal_ruang, transfer_internal_rs.ruang_selanjutnya, transfer_internal_rs.dpjp, "
                        + "transfer_internal_rs.konsulen, transfer_internal_rs.diagnosa_masuk, transfer_internal_rs.diagnosa_sekarang, transfer_internal_rs.alergi, "
                        + "transfer_internal_rs.derajat, transfer_internal_rs.pendamping, transfer_internal_rs.keadaan_umum_sebelum_transfer, "
                        + "transfer_internal_rs.keadaan_umum_sesudah_transfer, transfer_internal_rs.kesadaran_sebelum_transfer, "
                        + "transfer_internal_rs.kesadaran_sesudah_transfer, transfer_internal_rs.td_sebelum_transfer, transfer_internal_rs.td_sesudah_transfer, "
                        + "transfer_internal_rs.nadi_sebelum_transfer, transfer_internal_rs.nadi_sesudah_transfer, transfer_internal_rs.suhu_sebelum_transfer, "
                        + "transfer_internal_rs.suhu_sesudah_transfer, transfer_internal_rs.pernapasan_sebelum_transfer, transfer_internal_rs.pernapasan_sesudah_transfer, "
                        + "transfer_internal_rs.saturasi_sebelum_transfer, transfer_internal_rs.saturasi_sesudah_transfer, transfer_internal_rs.catatan_sebelum_transfer, "
                        + "transfer_internal_rs.catatan_sesudah_transfer, transfer_internal_rs.diet, transfer_internal_rs.keseimbangan_cairan, "
                        + "transfer_internal_rs.mobilisasi, transfer_internal_rs.perawatan_luka, transfer_internal_rs.iv, transfer_internal_rs.oksigen, "
                        + "transfer_internal_rs.kateter, transfer_internal_rs.syring, transfer_internal_rs.ngt, transfer_internal_rs.dekubitus, transfer_internal_rs.infus, "
                        + "transfer_internal_rs.cvp, transfer_internal_rs.drain, transfer_internal_rs.lain, transfer_internal_rs.lab, transfer_internal_rs.radiologi, "
                        + "transfer_internal_rs.ctscan, transfer_internal_rs.obat_infus, transfer_internal_rs.injeksi, transfer_internal_rs.oral, transfer_internal_rs.rekammedis, "
                        + "transfer_internal_rs.nip_menyerahkan, transfer_internal_rs.nip_menerima, "
                        + "IFNULL(dokter_dpjp.nm_dokter, '-') as nm_dokter_dpjp, IFNULL(dokter_konsulen.nm_dokter, '-') as nm_dokter_konsulen, "
                        + "IFNULL(petugasmenyerahkan.nama, '-') as nama_petugasmenyerahkan, IFNULL(petugasmenerima.nama, '-') as nama_petugasmenerima "
                        + "FROM transfer_internal_rs "
                        + "LEFT JOIN reg_periksa ON transfer_internal_rs.no_rawat = reg_periksa.no_rawat "
                        + "LEFT JOIN pasien ON reg_periksa.no_rkm_medis = pasien.no_rkm_medis "
                        + "LEFT JOIN dokter AS dokter_dpjp ON transfer_internal_rs.dpjp = dokter_dpjp.kd_dokter "
                        + "LEFT JOIN dokter AS dokter_konsulen ON transfer_internal_rs.konsulen = dokter_konsulen.kd_dokter "
                        + "LEFT JOIN petugas AS petugasmenyerahkan ON transfer_internal_rs.nip_menyerahkan = petugasmenyerahkan.nip "
                        + "LEFT JOIN petugas AS petugasmenerima ON transfer_internal_rs.nip_menerima = petugasmenerima.nip "
                        + "WHERE transfer_internal_rs.tanggal_pindah BETWEEN ? AND ? "
                        + "ORDER BY transfer_internal_rs.tanggal_pindah"
                );
            } else {
                ps = koneksi.prepareStatement(
                        "SELECT transfer_internal_rs.no_rawat, reg_periksa.no_rkm_medis, pasien.nm_pasien, pasien.jk, pasien.tgl_lahir, transfer_internal_rs.tanggal_masuk, "
                        + "transfer_internal_rs.tanggal_pindah, transfer_internal_rs.asal_ruang, transfer_internal_rs.ruang_selanjutnya, transfer_internal_rs.dpjp, "
                        + "transfer_internal_rs.konsulen, transfer_internal_rs.diagnosa_masuk, transfer_internal_rs.diagnosa_sekarang, transfer_internal_rs.alergi, "
                        + "transfer_internal_rs.derajat, transfer_internal_rs.pendamping, transfer_internal_rs.keadaan_umum_sebelum_transfer, "
                        + "transfer_internal_rs.keadaan_umum_sesudah_transfer, transfer_internal_rs.kesadaran_sebelum_transfer, "
                        + "transfer_internal_rs.kesadaran_sesudah_transfer, transfer_internal_rs.td_sebelum_transfer, transfer_internal_rs.td_sesudah_transfer, "
                        + "transfer_internal_rs.nadi_sebelum_transfer, transfer_internal_rs.nadi_sesudah_transfer, transfer_internal_rs.suhu_sebelum_transfer, "
                        + "transfer_internal_rs.suhu_sesudah_transfer, transfer_internal_rs.pernapasan_sebelum_transfer, transfer_internal_rs.pernapasan_sesudah_transfer, "
                        + "transfer_internal_rs.saturasi_sebelum_transfer, transfer_internal_rs.saturasi_sesudah_transfer, transfer_internal_rs.catatan_sebelum_transfer, "
                        + "transfer_internal_rs.catatan_sesudah_transfer, transfer_internal_rs.diet, transfer_internal_rs.keseimbangan_cairan, "
                        + "transfer_internal_rs.mobilisasi, transfer_internal_rs.perawatan_luka, transfer_internal_rs.iv, transfer_internal_rs.oksigen, "
                        + "transfer_internal_rs.kateter, transfer_internal_rs.syring, transfer_internal_rs.ngt, transfer_internal_rs.dekubitus, transfer_internal_rs.infus, "
                        + "transfer_internal_rs.cvp, transfer_internal_rs.drain, transfer_internal_rs.lain, transfer_internal_rs.lab, transfer_internal_rs.radiologi, "
                        + "transfer_internal_rs.ctscan, transfer_internal_rs.obat_infus, transfer_internal_rs.injeksi, transfer_internal_rs.oral, transfer_internal_rs.rekammedis, "
                        + "transfer_internal_rs.nip_menyerahkan, transfer_internal_rs.nip_menerima, "
                        + "IFNULL(dokter_dpjp.nm_dokter, '-') as nm_dokter_dpjp, IFNULL(dokter_konsulen.nm_dokter, '-') as nm_dokter_konsulen, "
                        + "IFNULL(petugasmenyerahkan.nama, '-') as nama_petugasmenyerahkan, IFNULL(petugasmenerima.nama, '-') as nama_petugasmenerima "
                        + "FROM transfer_internal_rs "
                        + "LEFT JOIN reg_periksa ON transfer_internal_rs.no_rawat = reg_periksa.no_rawat "
                        + "LEFT JOIN pasien ON reg_periksa.no_rkm_medis = pasien.no_rkm_medis "
                        + "LEFT JOIN dokter AS dokter_dpjp ON transfer_internal_rs.dpjp = dokter_dpjp.kd_dokter "
                        + "LEFT JOIN dokter AS dokter_konsulen ON transfer_internal_rs.konsulen = dokter_konsulen.kd_dokter "
                        + "LEFT JOIN petugas AS petugasmenyerahkan ON transfer_internal_rs.nip_menyerahkan = petugasmenyerahkan.nip "
                        + "LEFT JOIN petugas AS petugasmenerima ON transfer_internal_rs.nip_menerima = petugasmenerima.nip "
                        + "WHERE transfer_internal_rs.tanggal_pindah BETWEEN ? AND ? "
                        + "AND (transfer_internal_rs.no_rawat LIKE ? OR reg_periksa.no_rkm_medis LIKE ? OR pasien.nm_pasien LIKE ? OR "
                        + "transfer_internal_rs.nip_menyerahkan LIKE ? OR petugasmenyerahkan.nama LIKE ? OR "
                        + "transfer_internal_rs.nip_menerima LIKE ? OR petugasmenerima.nama LIKE ?) "
                        + "ORDER BY transfer_internal_rs.tanggal_pindah"
                );
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
                    ps.setString(7, "%" + TCari.getText() + "%");
                    ps.setString(8, "%" + TCari.getText() + "%");
                    ps.setString(9, "%" + TCari.getText() + "%");
                }
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat") != null ? rs.getString("no_rawat") : "-",
                        rs.getString("no_rkm_medis") != null ? rs.getString("no_rkm_medis") : "-",
                        rs.getString("nm_pasien") != null ? rs.getString("nm_pasien") : "-",
                        rs.getString("jk") != null ? rs.getString("jk") : "-",
                        rs.getString("tgl_lahir") != null ? rs.getString("tgl_lahir") : "-",
                        rs.getString("tanggal_masuk") != null ? rs.getString("tanggal_masuk") : "-",
                        rs.getString("tanggal_pindah") != null ? rs.getString("tanggal_pindah") : "-",
                        rs.getString("asal_ruang") != null ? rs.getString("asal_ruang") : "-",
                        rs.getString("ruang_selanjutnya") != null ? rs.getString("ruang_selanjutnya") : "-",
                        rs.getString("dpjp") != null ? rs.getString("dpjp") : "-",
                        rs.getString("nm_dokter_dpjp") != null ? rs.getString("nm_dokter_dpjp") : "-",
                        rs.getString("konsulen") != null ? rs.getString("konsulen") : "-",
                        rs.getString("nm_dokter_konsulen") != null ? rs.getString("nm_dokter_konsulen") : "-",
                        rs.getString("diagnosa_masuk") != null ? rs.getString("diagnosa_masuk") : "-",
                        rs.getString("diagnosa_sekarang") != null ? rs.getString("diagnosa_sekarang") : "-",
                        rs.getString("alergi") != null ? rs.getString("alergi") : "-",
                        rs.getString("derajat") != null ? rs.getString("derajat") : "-",
                        rs.getString("pendamping") != null ? rs.getString("pendamping") : "-",
                        rs.getString("keadaan_umum_sebelum_transfer") != null ? rs.getString("keadaan_umum_sebelum_transfer") : "-",
                        rs.getString("keadaan_umum_sesudah_transfer") != null ? rs.getString("keadaan_umum_sesudah_transfer") : "-",
                        rs.getString("kesadaran_sebelum_transfer") != null ? rs.getString("kesadaran_sebelum_transfer") : "-",
                        rs.getString("kesadaran_sesudah_transfer") != null ? rs.getString("kesadaran_sesudah_transfer") : "-",
                        rs.getString("td_sebelum_transfer") != null ? rs.getString("td_sebelum_transfer") : "-",
                        rs.getString("td_sesudah_transfer") != null ? rs.getString("td_sesudah_transfer") : "-",
                        rs.getString("nadi_sebelum_transfer") != null ? rs.getString("nadi_sebelum_transfer") : "-",
                        rs.getString("nadi_sesudah_transfer") != null ? rs.getString("nadi_sesudah_transfer") : "-",
                        rs.getString("suhu_sebelum_transfer") != null ? rs.getString("suhu_sebelum_transfer") : "-",
                        rs.getString("suhu_sesudah_transfer") != null ? rs.getString("suhu_sesudah_transfer") : "-",
                        rs.getString("pernapasan_sebelum_transfer") != null ? rs.getString("pernapasan_sebelum_transfer") : "-",
                        rs.getString("pernapasan_sesudah_transfer") != null ? rs.getString("pernapasan_sesudah_transfer") : "-",
                        rs.getString("saturasi_sebelum_transfer") != null ? rs.getString("saturasi_sebelum_transfer") : "-",
                        rs.getString("saturasi_sesudah_transfer") != null ? rs.getString("saturasi_sesudah_transfer") : "-",
                        rs.getString("catatan_sebelum_transfer") != null ? rs.getString("catatan_sebelum_transfer") : "-",
                        rs.getString("catatan_sesudah_transfer") != null ? rs.getString("catatan_sesudah_transfer") : "-",
                        rs.getString("diet") != null ? rs.getString("diet") : "-",
                        rs.getString("keseimbangan_cairan") != null ? rs.getString("keseimbangan_cairan") : "-",
                        rs.getString("mobilisasi") != null ? rs.getString("mobilisasi") : "-",
                        rs.getString("perawatan_luka") != null ? rs.getString("perawatan_luka") : "-",
                        rs.getString("iv") != null ? rs.getString("iv") : "-",
                        rs.getString("oksigen") != null ? rs.getString("oksigen") : "-",
                        rs.getString("kateter") != null ? rs.getString("kateter") : "-",
                        rs.getString("syring") != null ? rs.getString("syring") : "-",
                        rs.getString("ngt") != null ? rs.getString("ngt") : "-",
                        rs.getString("dekubitus") != null ? rs.getString("dekubitus") : "-",
                        rs.getString("infus") != null ? rs.getString("infus") : "-",
                        rs.getString("cvp") != null ? rs.getString("cvp") : "-",
                        rs.getString("drain") != null ? rs.getString("drain") : "-",
                        rs.getString("lain") != null ? rs.getString("lain") : "-",
                        rs.getString("lab") != null ? rs.getString("lab") : "-",
                        rs.getString("radiologi") != null ? rs.getString("radiologi") : "-",
                        rs.getString("ctscan") != null ? rs.getString("ctscan") : "-",
                        rs.getString("obat_infus") != null ? rs.getString("obat_infus") : "-",
                        rs.getString("injeksi") != null ? rs.getString("injeksi") : "-",
                        rs.getString("oral") != null ? rs.getString("oral") : "-",
                        rs.getString("rekammedis") != null ? rs.getString("rekammedis") : "-",
                        rs.getString("nip_menyerahkan") != null ? rs.getString("nip_menyerahkan") : "-",
                        rs.getString("nama_petugasmenyerahkan") != null ? rs.getString("nama_petugasmenyerahkan") : "-",
                        rs.getString("nip_menerima") != null ? rs.getString("nip_menerima") : "-",
                        rs.getString("nama_petugasmenerima") != null ? rs.getString("nama_petugasmenerima") : "-"
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
        TanggalMasuk.setDate(new Date());
        TanggalPindah.setDate(new Date());
        AsalRuang.setText("");
        RuangSelanjutnya.setText("");
        kddpjp.setText("");
        kdkonsulen.setText("");
        dpjp.setText(""); // jika ini adalah field tampilan DPJP
        DiagnosaUtama.setText("");
        DiagnosaSekarang.setText("");
        Alergi.setText("");
        Derajat.setSelectedIndex(0);
        PetugasPendamping.setText("");
        KuSebelumTransfer.setText("");
        KuSesudahTransfer.setText("");
        KesadaranSebelumTransfer.setSelectedIndex(0);
        KesadaranSesudahTransfer.setSelectedIndex(0);
        TdSebelumTransfer.setText("");
        TDSetelahTransfer.setText("");
        NadiSebelumTransfer.setText("");
        NadiSetelahTransfer.setText("");
        SuhuSebelumTransfer.setText("");
        SuhuSetelahTransfer.setText("");
        RRSebelumTransfer.setText("");
        RRSetelahTransfer.setText("");
        SaturasiSebelumTransfer.setText("");
        SaturasiSesudahTransfer.setText("");
        CatatanSebelumTransfer.setText("");
        CatatanSesudahTransfer.setText("");
        Diet.setText("");
        Mobilisasi.setText("");
        KeseimbanganCairan.setText("");
        PerawatanLuka.setText("");
        Cath.setText("");
        Oksigen.setText("");
        Kateter.setText("");
        Syring.setText("");
        Ngt.setText("");
        Dekubitus.setText("");
        SkemaInfus.setText("");
        Cvp.setText("");
        Drain.setText("");
        Lain.setText("");
        Lab.setText("");
        Rad.setText("");
        Ct.setText("");
        Infus.setText("");
        Injeksi.setText("");
        Oral.setText("");
        Rekammedis.setSelectedIndex(0);
        KdPetugasMenyerahkan.setText("");
        KdPetugasMenerima.setText("");
        TabRawat.setSelectedIndex(0); // jika TabRawat adalah tab panel
    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            int row = tbObat.getSelectedRow();

            TNoRw.setText(tbObat.getValueAt(row, 0).toString());
            TNoRM.setText(tbObat.getValueAt(row, 1).toString());
            TPasien.setText(tbObat.getValueAt(row, 2).toString());
            TglLahir.setText(tbObat.getValueAt(row, 3).toString());
            Jk.setText(tbObat.getValueAt(row, 4).toString());

            Valid.SetTgl2(TanggalMasuk, tbObat.getValueAt(row, 5).toString());
            Valid.SetTgl2(TanggalPindah, tbObat.getValueAt(row, 6).toString());

            AsalRuang.setText(tbObat.getValueAt(row, 7).toString());
            RuangSelanjutnya.setText(tbObat.getValueAt(row, 8).toString());

            kddpjp.setText(tbObat.getValueAt(row, 9).toString());
            dpjp.setText(tbObat.getValueAt(row, 10).toString());

            kdkonsulen.setText(tbObat.getValueAt(row, 11).toString());
            // Jika ada field untuk nama konsulen, bisa diisi juga:
            konsulen.setText(tbObat.getValueAt(row, 12).toString());

            DiagnosaUtama.setText(tbObat.getValueAt(row, 13).toString());
            DiagnosaSekarang.setText(tbObat.getValueAt(row, 14).toString());
            Alergi.setText(tbObat.getValueAt(row, 15).toString());
            Derajat.setSelectedItem(tbObat.getValueAt(row, 16).toString());
            PetugasPendamping.setText(tbObat.getValueAt(row, 17).toString());

            KuSebelumTransfer.setText(tbObat.getValueAt(row, 18).toString());
            KuSesudahTransfer.setText(tbObat.getValueAt(row, 19).toString());

            KesadaranSebelumTransfer.setSelectedItem(tbObat.getValueAt(row, 20).toString());
            KesadaranSesudahTransfer.setSelectedItem(tbObat.getValueAt(row, 21).toString());

            TdSebelumTransfer.setText(tbObat.getValueAt(row, 22).toString());
            TDSetelahTransfer.setText(tbObat.getValueAt(row, 23).toString());

            NadiSebelumTransfer.setText(tbObat.getValueAt(row, 24).toString());
            NadiSetelahTransfer.setText(tbObat.getValueAt(row, 25).toString());

            SuhuSebelumTransfer.setText(tbObat.getValueAt(row, 26).toString());
            SuhuSetelahTransfer.setText(tbObat.getValueAt(row, 27).toString());

            RRSebelumTransfer.setText(tbObat.getValueAt(row, 28).toString());
            RRSetelahTransfer.setText(tbObat.getValueAt(row, 29).toString());

            SaturasiSebelumTransfer.setText(tbObat.getValueAt(row, 30).toString());
            SaturasiSesudahTransfer.setText(tbObat.getValueAt(row, 31).toString());

            CatatanSebelumTransfer.setText(tbObat.getValueAt(row, 32).toString());
            CatatanSesudahTransfer.setText(tbObat.getValueAt(row, 33).toString());

            Diet.setText(tbObat.getValueAt(row, 34).toString());
            KeseimbanganCairan.setText(tbObat.getValueAt(row, 35).toString());
            Mobilisasi.setText(tbObat.getValueAt(row, 36).toString());

            PerawatanLuka.setText(tbObat.getValueAt(row, 37).toString());

            Cath.setText(tbObat.getValueAt(row, 38).toString());
            Oksigen.setText(tbObat.getValueAt(row, 39).toString());
            Kateter.setText(tbObat.getValueAt(row, 40).toString());
            Syring.setText(tbObat.getValueAt(row, 41).toString());
            Ngt.setText(tbObat.getValueAt(row, 42).toString());
            Dekubitus.setText(tbObat.getValueAt(row, 43).toString());
            SkemaInfus.setText(tbObat.getValueAt(row, 44).toString());
            Cvp.setText(tbObat.getValueAt(row, 45).toString());
            Drain.setText(tbObat.getValueAt(row, 46).toString());
            Lain.setText(tbObat.getValueAt(row, 47).toString());

            Lab.setText(tbObat.getValueAt(row, 48).toString());
            Rad.setText(tbObat.getValueAt(row, 49).toString());
            Ct.setText(tbObat.getValueAt(row, 50).toString());

            Infus.setText(tbObat.getValueAt(row, 51).toString());
            Injeksi.setText(tbObat.getValueAt(row, 52).toString());
            Oral.setText(tbObat.getValueAt(row, 53).toString());

            Rekammedis.setSelectedItem(tbObat.getValueAt(row, 54).toString());

            KdPetugasMenyerahkan.setText(tbObat.getValueAt(row, 55).toString());
            NmPetugasMenyerahkan.setText(tbObat.getValueAt(row, 56).toString());

            KdPetugasMenerima.setText(tbObat.getValueAt(row, 57).toString());
            NmPetugasMenerima.setText(tbObat.getValueAt(row, 58).toString());
        }
    }

    private void isRawat() {
        try {
            ps = koneksi.prepareStatement(
                    "select reg_periksa.no_rkm_medis,pasien.nm_pasien, if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir,reg_periksa.tgl_registrasi "
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

    public void setNoRm(String norwt, Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        DTPCari2.setDate(tgl2);
        isRawat();
    }

    public void isCek() {
        BtnSimpan.setEnabled(akses.gettransfer_pasien_antar_ruang());
        BtnHapus.setEnabled(akses.gettransfer_pasien_antar_ruang());
        BtnEdit.setEnabled(akses.gettransfer_pasien_antar_ruang());
        BtnPrint.setEnabled(akses.gettransfer_pasien_antar_ruang());
    }

    public void setTampil() {
        TabRawat.setSelectedIndex(1);
    }

    private void hapus() {
        if (Sequel.queryu2tf("delete from transfer_internal_rs where no_rawat=? and tanggal_masuk=?", 2, new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString()
        }) == true) {
            tabMode.removeRow(tbObat.getSelectedRow());
            LCount.setText("" + tabMode.getRowCount());
            TabRawat.setSelectedIndex(1);
        } else {
            JOptionPane.showMessageDialog(null, "Gagal menghapus..!!");
        }
    }

    private void ganti() {
        if (Sequel.mengedittf("transfer_internal_rs", "no_rawat=? and tanggal_masuk=?",
                "no_rawat=?,tanggal_masuk=?,tanggal_pindah=?,asal_ruang=?,ruang_selanjutnya=?,dpjp=?,konsulen=?,diagnosa_masuk=?,diagnosa_sekarang=?,alergi=?,derajat=?,pendamping=?,"
                + "keadaan_umum_sebelum_transfer=?,keadaan_umum_sesudah_transfer=?,kesadaran_sebelum_transfer=?,kesadaran_sesudah_transfer=?,td_sebelum_transfer=?,td_sesudah_transfer=?,"
                + "nadi_sebelum_transfer=?,nadi_sesudah_transfer=?,suhu_sebelum_transfer=?,suhu_sesudah_transfer=?,pernapasan_sebelum_transfer=?,pernapasan_sesudah_transfer=?,"
                + "saturasi_sebelum_transfer=?,saturasi_sesudah_transfer=?,catatan_sebelum_transfer=?,catatan_sesudah_transfer=?,diet=?,keseimbangan_cairan=?,"
                + "mobilisasi=?,perawatan_luka=?,iv=?,oksigen=?,kateter=?,syring=?,ngt=?,dekubitus=?,infus=?,cvp=?,drain=?,lain=?,lab=?,radiologi=?,ctscan=?,"
                + "obat_infus=?,injeksi=?,oral=?,rekammedis=?,nip_menyerahkan=?,nip_menerima=?", 53, new String[]{
                    TNoRw.getText(), Valid.SetTgl(TanggalMasuk.getSelectedItem() + "") + " " + TanggalMasuk.getSelectedItem().toString().substring(11, 19),
                    Valid.SetTgl(TanggalPindah.getSelectedItem() + "") + " " + TanggalPindah.getSelectedItem().toString().substring(11, 19),
                    AsalRuang.getText(), RuangSelanjutnya.getText(), kddpjp.getText(), kdkonsulen.getText(), DiagnosaUtama.getText(), DiagnosaSekarang.getText(), Alergi.getText(),
                    Derajat.getSelectedItem().toString(), PetugasPendamping.getText(), KuSebelumTransfer.getText(), KuSesudahTransfer.getText(),
                    KesadaranSebelumTransfer.getSelectedItem().toString(), KesadaranSesudahTransfer.getSelectedItem().toString(),
                    TdSebelumTransfer.getText(), TDSetelahTransfer.getText(), NadiSebelumTransfer.getText(), NadiSetelahTransfer.getText(),
                    SuhuSebelumTransfer.getText(), SuhuSetelahTransfer.getText(), RRSebelumTransfer.getText(), RRSetelahTransfer.getText(),
                    SaturasiSebelumTransfer.getText(), SaturasiSesudahTransfer.getText(), CatatanSebelumTransfer.getText(), CatatanSesudahTransfer.getText(),
                    Diet.getText(), KeseimbanganCairan.getText(), Mobilisasi.getText(), PerawatanLuka.getText(),
                    Cath.getText(), Oksigen.getText(), Kateter.getText(), Syring.getText(), Ngt.getText(), Dekubitus.getText(), SkemaInfus.getText(),
                    Cvp.getText(), Drain.getText(), Lain.getText(), Lab.getText(), Rad.getText(), Ct.getText(), Infus.getText(), Injeksi.getText(),
                    Oral.getText(), Rekammedis.getSelectedItem().toString(), KdPetugasMenyerahkan.getText(), KdPetugasMenerima.getText(),
                    TNoRw.getText(), Valid.SetTgl(TanggalMasuk.getSelectedItem() + "") + " " + TanggalMasuk.getSelectedItem().toString().substring(11, 19)
                })) {
            tampil();
            emptTeks();
            TabRawat.setSelectedIndex(1);
        }
    }

    private void simpan() {
        if (Sequel.menyimpantf("transfer_internal_rs", "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?", "No.Rawat & Tanggal Masuk", 51, new String[]{
            TNoRw.getText(), Valid.SetTgl(TanggalMasuk.getSelectedItem() + "") + " " + TanggalMasuk.getSelectedItem().toString().substring(11, 19),
            Valid.SetTgl(TanggalPindah.getSelectedItem() + "") + " " + TanggalPindah.getSelectedItem().toString().substring(11, 19), AsalRuang.getText(),
            RuangSelanjutnya.getText(), kddpjp.getText(), kdkonsulen.getText(), DiagnosaUtama.getText(), DiagnosaSekarang.getText(), Alergi.getText(), Derajat.getSelectedItem().toString(), PetugasPendamping.getText(),
            KuSebelumTransfer.getText(), KuSesudahTransfer.getText(), KesadaranSebelumTransfer.getSelectedItem().toString(), KesadaranSesudahTransfer.getSelectedItem().toString(),
            TdSebelumTransfer.getText(), TDSetelahTransfer.getText(), NadiSebelumTransfer.getText(), NadiSetelahTransfer.getText(), SuhuSebelumTransfer.getText(), SuhuSetelahTransfer.getText(),
            RRSebelumTransfer.getText(), RRSetelahTransfer.getText(), SaturasiSebelumTransfer.getText(), SaturasiSesudahTransfer.getText(), CatatanSebelumTransfer.getText(), CatatanSesudahTransfer.getText(),
            Diet.getText(), KeseimbanganCairan.getText(), Mobilisasi.getText(), PerawatanLuka.getText(), Cath.getText(), Oksigen.getText(), Kateter.getText(), Syring.getText(), Ngt.getText(),
            Dekubitus.getText(), SkemaInfus.getText(), Cvp.getText(), Drain.getText(), Lain.getText(), Lab.getText(), Rad.getText(), Ct.getText(), Infus.getText(), Injeksi.getText(), Oral.getText(), Rekammedis.getSelectedItem().toString(),
            KdPetugasMenyerahkan.getText(), KdPetugasMenerima.getText()
        }) == true) {
            emptTeks();
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
            try {
                ps = koneksi.prepareStatement("select bukti_persetujuan_transfer_internal_rs.photo from bukti_persetujuan_transfer_internal_rs where bukti_persetujuan_transfer_internal_rs.no_rawat=? and bukti_persetujuan_transfer_internal_rs.tanggal_masuk=?");
                try {
                    ps.setString(1, tbObat.getValueAt(tbObat.getSelectedRow(), 0).toString());
                    ps.setString(2, tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        if (rs.getString("photo").equals("") || rs.getString("photo").equals("-")) {
                            LoadHTML2.setText("<html><body><center><br><br><font face='tahoma' size='2' color='#434343'>Kosong</font></center></body></html>");
                        } else {
                            LoadHTML2.setText("<html><body><center><img src='http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/persetujuantransferruang/" + rs.getString("photo") + "' alt='photo' width='500' height='500'/></center></body></html>");
                        }
                    } else {
                        LoadHTML2.setText("<html><body><center><br><br><font face='tahoma' size='2' color='#434343'>Kosong</font></center></body></html>");
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
}
