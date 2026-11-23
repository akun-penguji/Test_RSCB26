package permintaan;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import inventory.DlgPeresepanDokter;
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
import keuangan.DlgCariDaftarOperasi;
import rekammedis.RMAsesmenPraAnestesi;
import rekammedis.RMAsesmenPraAnestesiRscb;
import rekammedis.RMAsesmenPraInduksiRscb;
import rekammedis.RMCatatanSedasiAnestesiRscb;
import rekammedis.RMCatatanSedasiAnestesiRssm;
import rekammedis.RMAsesmenPraBedahRscb;
import rekammedis.RMAsuhanKeperawatanKamarBedah;
import rekammedis.RMCatatanAnastesiSedasi;
import rekammedis.RMChecklistPostOperasi;
import rekammedis.RMChecklistPreOperasi;
import rekammedis.RMDataMonitoringAnestesi;
import rekammedis.RMEdukasiAnestesiRscb;
import rekammedis.RMKonversiAnestesiRscb;
import rekammedis.RMSIAGeneralRscb;
import rekammedis.RMSIARegionalRscb;
import rekammedis.RMFormRobsonKlasifikasi;
import rekammedis.RMLaporanOperasi;
import rekammedis.RMMonitoringAldrettePascaAnestesi;
import rekammedis.RMMonitoringBromagePascaAnestesi;
import rekammedis.RMMonitoringStewardPascaAnestesi;
import rekammedis.RMStatusAnestesi;
import rekammedis.RMPenilaianPreAnastesi;
import rekammedis.RMPenilaianPreInduksi;
import rekammedis.RMPenilaianPreOperasi;
import simrskhanza.DlgKamarInap;
import rekammedis.RMRiwayatPerawatan;
import rekammedis.RMSignInSebelumAnastesi;
import rekammedis.RMSignOutSebelumMenutupLuka;
import rekammedis.RMTimeOutSebelumInsisi;
import rekammedis.RMTransferPasienAntarRuang;
import setting.DlgCariRuangOperasi;
import simrskhanza.DlgTagihanOperasi;

/**
 *
 * @author dosen
 */
public class DlgBookingOperasi extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;
    private String status = "", bangsal = "", lokasistok = "", kamar = "", diagnosa = "", order = "", kelas = "", penjab = "", norawatibu = "", posisi = "", DEPOAKTIFOBAT = "", norm = "";
    private DlgCariDokter dokter = new DlgCariDokter(null, false);
    private DlgCariRuangOperasi ruangok = new DlgCariRuangOperasi(null, false);
    private DlgCariDaftarOperasi operasi = new DlgCariDaftarOperasi(null, false);

    /**
     * Creates new form DlgPemberianInfus
     *
     * @param parent
     * @param modal
     */
    public DlgBookingOperasi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        // Sembunyikan form pra-anestesi lama yang sudah digantikan oleh
        // "Penilaian Pre Anestesi RSCB" (RMAsesmenPraAnestesiRscb, tabel
        // asesmen_pra_anestesi_rscb, sinkron dengan E-Dokter web).
        BtnPreAnastesi.setVisible(false);   // "Penilaian Pre Anestesi" (penilaian_pre_anestesi)
        BtnPreAnastesi2.setVisible(false);  // "Asesmen Pra Anastesi/ Sedasi" (asesmen_pra_anestesi)

        tabMode = new DefaultTableModel(null, new Object[]{
            "No.", "No.Rawat", "Nama Pasien", "Umur", "J.K.", "Tanggal", "Mulai",
            "Selesai", "Status", "Rujukan Dari", "Diagnosa", "Kode Operasi", "Operasi",
            "Kode Operator", "Operator", "Order", "Kode OK", "Nama Ruang Operasi"
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

        for (i = 0; i < 18; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(28);
            } else if (i == 1) {
                column.setPreferredWidth(105);
            } else if (i == 2) {
                column.setPreferredWidth(170);
            } else if (i == 3) {
                column.setPreferredWidth(50);
            } else if (i == 4) {
                column.setPreferredWidth(30);
            } else if (i == 5) {
                column.setPreferredWidth(65);
            } else if (i == 6) {
                column.setPreferredWidth(50);
            } else if (i == 7) {
                column.setPreferredWidth(50);
            } else if (i == 8) {
                column.setPreferredWidth(60);
            } else if (i == 9) {
                column.setPreferredWidth(120);
            } else if (i == 10) {
                column.setPreferredWidth(150);
            } else if (i == 11) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            } else if (i == 12) {
                column.setPreferredWidth(150);
            } else if (i == 13) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            } else if (i == 14) {
                column.setPreferredWidth(150);
            } else if (i == 15) {
                column.setMinWidth(0);
                column.setMaxWidth(0);
            } else if (i == 16) {
                column.setPreferredWidth(50);
            } else if (i == 17) {
                column.setPreferredWidth(130);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TNoRw.setDocument(new batasInput((byte) 17).getKata(TNoRw));
        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));
        KdDokter.setDocument(new batasInput((byte) 3).getKata(KdDokter));
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

        ChkInput.setSelected(false);
        isForm();

        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {;
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (dokter.getTable().getSelectedRow() != -1) {
                    KdDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 0).toString());
                    NmDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(), 1).toString());
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

        ruangok.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {;
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (ruangok.getTable().getSelectedRow() != -1) {
                    KdRuangOperasi.setText(ruangok.getTable().getValueAt(ruangok.getTable().getSelectedRow(), 0).toString());
                    NmRuangOperasi.setText(ruangok.getTable().getValueAt(ruangok.getTable().getSelectedRow(), 1).toString());
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

        operasi.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {;
            }

            @Override
            public void windowClosing(WindowEvent e) {
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if (operasi.getTable().getSelectedRow() != -1) {
                    KdOperasi.setText(operasi.getTable().getValueAt(operasi.getTable().getSelectedRow(), 0).toString());
                    NmOperasi.setText(operasi.getTable().getValueAt(operasi.getTable().getSelectedRow(), 1).toString());
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

        ChkAccor.setSelected(false);
        isMenu();

        // Popup Menu Klik Kanan pada Tabel Pasien Booking Operasi
        javax.swing.JPopupMenu popBooking = new javax.swing.JPopupMenu();
        javax.swing.JMenuItem mnAsesmenPraAnestesi = new javax.swing.JMenuItem("Asesmen Pra Anestesi");
        mnAsesmenPraAnestesi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        mnAsesmenPraAnestesi.addActionListener(evt -> BtnPreAnastesiRscbActionPerformed(evt));
        popBooking.add(mnAsesmenPraAnestesi);

        javax.swing.JMenuItem mnAsesmenPraInduksi = new javax.swing.JMenuItem("Asesmen Pra Induksi");
        mnAsesmenPraInduksi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png")));
        mnAsesmenPraInduksi.addActionListener(evt -> BtnPreInduksiRscbActionPerformed(evt));
        popBooking.add(mnAsesmenPraInduksi);

        popBooking.addSeparator();

        javax.swing.JMenu mnDokumenOK = new javax.swing.JMenu("Dokumen Anestesi & Bedah");
        mnDokumenOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png")));

        javax.swing.JMenuItem mnEdukasiAnestesi = new javax.swing.JMenuItem("Edukasi Anestesi");
        mnEdukasiAnestesi.addActionListener(evt -> BtnEdukasiAnestesiActionPerformed(evt));
        mnDokumenOK.add(mnEdukasiAnestesi);

        javax.swing.JMenuItem mnKonversiAnestesi = new javax.swing.JMenuItem("Konversi Anestesi");
        mnKonversiAnestesi.addActionListener(evt -> BtnKonversiAnestesiActionPerformed(evt));
        mnDokumenOK.add(mnKonversiAnestesi);

        javax.swing.JMenuItem mnSIAGeneral = new javax.swing.JMenuItem("Surat Izin Anestesi General");
        mnSIAGeneral.addActionListener(evt -> BtnSIAGeneralActionPerformed(evt));
        mnDokumenOK.add(mnSIAGeneral);

        javax.swing.JMenuItem mnSIARegional = new javax.swing.JMenuItem("Surat Izin Anestesi Regional");
        mnSIARegional.addActionListener(evt -> BtnSIARegionalActionPerformed(evt));
        mnDokumenOK.add(mnSIARegional);

        javax.swing.JMenuItem mnCatatanSedasi = new javax.swing.JMenuItem("Catatan Sedasi & Anestesi");
        mnCatatanSedasi.addActionListener(evt -> BtnCatatanSedasiRscbActionPerformed(evt));
        mnDokumenOK.add(mnCatatanSedasi);

        javax.swing.JMenuItem mnPraBedah = new javax.swing.JMenuItem("Asesmen Pra Bedah");
        mnPraBedah.addActionListener(evt -> BtnPraBedahRscbActionPerformed(evt));
        mnDokumenOK.add(mnPraBedah);

        javax.swing.JMenuItem mnChecklistOK = new javax.swing.JMenuItem("Checklist Keselamatan Pasien OK");
        mnChecklistOK.addActionListener(evt -> BtnChecklistKeselamatanOKActionPerformed(evt));
        mnDokumenOK.add(mnChecklistOK);

        javax.swing.JMenuItem mnLaporanOperasi = new javax.swing.JMenuItem("Laporan Operasi");
        mnLaporanOperasi.addActionListener(evt -> BtnLaporanOperasiActionPerformed(evt));
        mnDokumenOK.add(mnLaporanOperasi);

        popBooking.add(mnDokumenOK);

        popBooking.addSeparator();

        javax.swing.JMenuItem mnRiwayat = new javax.swing.JMenuItem("Riwayat Perawatan Pasien");
        mnRiwayat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png")));
        mnRiwayat.addActionListener(evt -> BtnRiwayatPasienActionPerformed(evt));
        popBooking.add(mnRiwayat);

        tbObat.setComponentPopupMenu(popBooking);

        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                if (javax.swing.SwingUtilities.isRightMouseButton(evt)) {
                    int r = tbObat.rowAtPoint(evt.getPoint());
                    if (r >= 0 && r < tbObat.getRowCount()) {
                        tbObat.setRowSelectionInterval(r, r);
                        getData();
                    }
                }
            }
        });

        try {
            DEPOAKTIFOBAT = koneksiDB.DEPOAKTIFOBAT();
        } catch (Exception e) {
            System.out.println("E : " + e);
            DEPOAKTIFOBAT = "";
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

        buttonGroup1 = new javax.swing.ButtonGroup();
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
        panelGlass10 = new widget.panelisi();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        panelCari = new widget.panelisi();
        R1 = new widget.RadioButton();
        R2 = new widget.RadioButton();
        DTPCari1 = new widget.Tanggal();
        jLabel22 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        R4 = new widget.RadioButton();
        R3 = new widget.RadioButton();
        DTPCari3 = new widget.Tanggal();
        jLabel25 = new widget.Label();
        DTPCari4 = new widget.Tanggal();
        PanelInput = new javax.swing.JPanel();
        ChkInput = new widget.CekBox();
        FormInput = new widget.PanelBiasa();
        jLabel4 = new widget.Label();
        TNoRw = new widget.TextBox();
        jLabel9 = new widget.Label();
        NmDokter = new widget.TextBox();
        TPasien = new widget.TextBox();
        DTPTgl = new widget.Tanggal();
        Status = new widget.ComboBox();
        jLabel10 = new widget.Label();
        KdDokter = new widget.TextBox();
        BtnOperator = new widget.Button();
        Kamar = new widget.TextBox();
        jLabel35 = new widget.Label();
        JamMulai = new widget.ComboBox();
        MenitMulai = new widget.ComboBox();
        DetikMulai = new widget.ComboBox();
        DetikSelesai = new widget.ComboBox();
        MenitSelesai = new widget.ComboBox();
        JamSelesai = new widget.ComboBox();
        jLabel36 = new widget.Label();
        jLabel37 = new widget.Label();
        jLabel11 = new widget.Label();
        KdOperasi = new widget.TextBox();
        NmOperasi = new widget.TextBox();
        BtnOperasi = new widget.Button();
        jLabel12 = new widget.Label();
        KdRuangOperasi = new widget.TextBox();
        NmRuangOperasi = new widget.TextBox();
        BtnRuangOperasi = new widget.Button();
        PanelAccor = new widget.PanelBiasa();
        ChkAccor = new widget.CekBox();
        ScrollMenu = new widget.ScrollPane();
        FormMenu = new widget.PanelBiasa();
        BtnKamarInap = new widget.Button();
        BtnPermintaanLab = new widget.Button();
        BtnRiwayatPasien = new widget.Button();
        BtnEdukasiAnestesi = new widget.Button();
        BtnKonversiAnestesi = new widget.Button();
        BtnSIAGeneral = new widget.Button();
        BtnSIARegional = new widget.Button();
        BtnPenilaianPreInduksi = new widget.Button();
        BtnChecklistPreOperasi = new widget.Button();
        BtnPenandaanAreaOperasi = new widget.Button();
        BtnMonitoringSingleUse = new widget.Button();
        BtnCatatanSedasiRscb = new widget.Button();
        BtnSignInSebelumAnestesi = new widget.Button();
        BtnTimeOutSebelumInsisi = new widget.Button();
        BtnSignOutSebelumMenutupLuka = new widget.Button();
        BtnChecklistKeselamatanOK = new widget.Button();
        BtnChecklistPostOperasi = new widget.Button();
        BtnPreOperasi = new widget.Button();
        BtnPraBedahRscb = new widget.Button();
        BtnCatatanAnastesiSedasi = new widget.Button();
        BtnPreAnastesi = new widget.Button();
        BtnPreAnastesiRscb = new widget.Button();
        BtnPreInduksiRscb = new widget.Button();
        BtnCatatanSedasiAnestesiRssm = new widget.Button();
        BtnLembarObservasiAnestesi = new widget.Button();
        BtnPreAnastesi1 = new widget.Button();
        BtnTagihanOperasi = new widget.Button();
        BtnLaporanOperasi = new widget.Button();
        BtnObatBhp = new widget.Button();
        BtnTransferAntarRuang = new widget.Button();
        BtnSkorAldrettePascaAnestesi = new widget.Button();
        BtnSkorStewardPascaAnestesi = new widget.Button();
        BtnSkorBromagePascaAnestesi = new widget.Button();
        BtnPreAnastesi2 = new widget.Button();
        BtnFormRobson = new widget.Button();
        BtnPreAnastesi3 = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Jadwal Operasi Pasien ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbObat.setAutoCreateRowSorter(true);
        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
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
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 144));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(55, 55));
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
        jLabel7.setPreferredSize(new java.awt.Dimension(85, 23));
        panelGlass8.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(120, 23));
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

        jPanel3.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        panelGlass10.setName("panelGlass10"); // NOI18N
        panelGlass10.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass10.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(685, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass10.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('2');
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
        panelGlass10.add(BtnCari);

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
        panelGlass10.add(BtnAll);

        jPanel3.add(panelGlass10, java.awt.BorderLayout.CENTER);

        panelCari.setName("panelCari"); // NOI18N
        panelCari.setPreferredSize(new java.awt.Dimension(44, 43));
        panelCari.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 9));

        buttonGroup1.add(R1);
        R1.setSelected(true);
        R1.setText("Menunggu");
        R1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        R1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        R1.setName("R1"); // NOI18N
        R1.setPreferredSize(new java.awt.Dimension(105, 23));
        panelCari.add(R1);

        buttonGroup1.add(R2);
        R2.setText("Tanggal :");
        R2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        R2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        R2.setName("R2"); // NOI18N
        R2.setPreferredSize(new java.awt.Dimension(90, 23));
        panelCari.add(R2);

        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "26-03-2026" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(90, 23));
        DTPCari1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                DTPCari1ItemStateChanged(evt);
            }
        });
        DTPCari1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPCari1KeyPressed(evt);
            }
        });
        panelCari.add(DTPCari1);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("s.d");
        jLabel22.setName("jLabel22"); // NOI18N
        jLabel22.setPreferredSize(new java.awt.Dimension(30, 23));
        panelCari.add(jLabel22);

        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "26-03-2026" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(90, 23));
        DTPCari2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPCari2KeyPressed(evt);
            }
        });
        panelCari.add(DTPCari2);

        buttonGroup1.add(R4);
        R4.setText("Proses");
        R4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        R4.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        R4.setName("R4"); // NOI18N
        R4.setPreferredSize(new java.awt.Dimension(122, 23));
        panelCari.add(R4);

        buttonGroup1.add(R3);
        R3.setText("Selesai :");
        R3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        R3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        R3.setName("R3"); // NOI18N
        R3.setPreferredSize(new java.awt.Dimension(75, 23));
        panelCari.add(R3);

        DTPCari3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "26-03-2026" }));
        DTPCari3.setDisplayFormat("dd-MM-yyyy");
        DTPCari3.setName("DTPCari3"); // NOI18N
        DTPCari3.setOpaque(false);
        DTPCari3.setPreferredSize(new java.awt.Dimension(90, 23));
        DTPCari3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                DTPCari3ItemStateChanged(evt);
            }
        });
        DTPCari3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPCari3KeyPressed(evt);
            }
        });
        panelCari.add(DTPCari3);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel25.setText("s.d");
        jLabel25.setName("jLabel25"); // NOI18N
        jLabel25.setPreferredSize(new java.awt.Dimension(30, 23));
        panelCari.add(jLabel25);

        DTPCari4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "26-03-2026" }));
        DTPCari4.setDisplayFormat("dd-MM-yyyy");
        DTPCari4.setName("DTPCari4"); // NOI18N
        DTPCari4.setOpaque(false);
        DTPCari4.setPreferredSize(new java.awt.Dimension(90, 23));
        DTPCari4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                DTPCari4ItemStateChanged(evt);
            }
        });
        DTPCari4.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPCari4KeyPressed(evt);
            }
        });
        panelCari.add(DTPCari4);

        jPanel3.add(panelCari, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        PanelInput.setName("PanelInput"); // NOI18N
        PanelInput.setOpaque(false);
        PanelInput.setPreferredSize(new java.awt.Dimension(192, 126));
        PanelInput.setLayout(new java.awt.BorderLayout(1, 1));

        ChkInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/143.png"))); // NOI18N
        ChkInput.setMnemonic('M');
        ChkInput.setText(".: Input Data");
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

        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(190, 77));
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
        TNoRw.setBounds(73, 10, 120, 23);

        jLabel9.setText("Operator :");
        jLabel9.setName("jLabel9"); // NOI18N
        FormInput.add(jLabel9);
        jLabel9.setBounds(0, 70, 70, 23);

        NmDokter.setEditable(false);
        NmDokter.setHighlighter(null);
        NmDokter.setName("NmDokter"); // NOI18N
        FormInput.add(NmDokter);
        NmDokter.setBounds(155, 70, 200, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        FormInput.add(TPasien);
        TPasien.setBounds(195, 10, 208, 23);

        DTPTgl.setForeground(new java.awt.Color(50, 70, 50));
        DTPTgl.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "26-03-2026" }));
        DTPTgl.setDisplayFormat("dd-MM-yyyy");
        DTPTgl.setName("DTPTgl"); // NOI18N
        DTPTgl.setOpaque(false);
        DTPTgl.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DTPTglKeyPressed(evt);
            }
        });
        FormInput.add(DTPTgl);
        DTPTgl.setBounds(73, 40, 90, 23);

        Status.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Menunggu", "Proses Operasi", "Selesai" }));
        Status.setName("Status"); // NOI18N
        Status.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                StatusKeyPressed(evt);
            }
        });
        FormInput.add(Status);
        Status.setBounds(703, 40, 140, 23);

        jLabel10.setText("Tanggal :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(0, 40, 70, 23);

        KdDokter.setEditable(false);
        KdDokter.setHighlighter(null);
        KdDokter.setName("KdDokter"); // NOI18N
        FormInput.add(KdDokter);
        KdDokter.setBounds(73, 70, 80, 23);

        BtnOperator.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnOperator.setMnemonic('X');
        BtnOperator.setToolTipText("Alt+X");
        BtnOperator.setName("BtnOperator"); // NOI18N
        BtnOperator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnOperatorActionPerformed(evt);
            }
        });
        BtnOperator.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnOperatorKeyPressed(evt);
            }
        });
        FormInput.add(BtnOperator);
        BtnOperator.setBounds(357, 70, 28, 23);

        Kamar.setEditable(false);
        Kamar.setHighlighter(null);
        Kamar.setName("Kamar"); // NOI18N
        FormInput.add(Kamar);
        Kamar.setBounds(405, 10, 160, 23);

        jLabel35.setText("Mulai :");
        jLabel35.setName("jLabel35"); // NOI18N
        FormInput.add(jLabel35);
        jLabel35.setBounds(165, 40, 40, 23);

        JamMulai.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        JamMulai.setName("JamMulai"); // NOI18N
        JamMulai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JamMulaiKeyPressed(evt);
            }
        });
        FormInput.add(JamMulai);
        JamMulai.setBounds(208, 40, 62, 23);

        MenitMulai.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        MenitMulai.setName("MenitMulai"); // NOI18N
        MenitMulai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MenitMulaiKeyPressed(evt);
            }
        });
        FormInput.add(MenitMulai);
        MenitMulai.setBounds(273, 40, 62, 23);

        DetikMulai.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        DetikMulai.setName("DetikMulai"); // NOI18N
        DetikMulai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DetikMulaiKeyPressed(evt);
            }
        });
        FormInput.add(DetikMulai);
        DetikMulai.setBounds(338, 40, 62, 23);

        DetikSelesai.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        DetikSelesai.setName("DetikSelesai"); // NOI18N
        DetikSelesai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DetikSelesaiKeyPressed(evt);
            }
        });
        FormInput.add(DetikSelesai);
        DetikSelesai.setBounds(557, 40, 62, 23);

        MenitSelesai.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59" }));
        MenitSelesai.setName("MenitSelesai"); // NOI18N
        MenitSelesai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MenitSelesaiKeyPressed(evt);
            }
        });
        FormInput.add(MenitSelesai);
        MenitSelesai.setBounds(492, 40, 62, 23);

        JamSelesai.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23" }));
        JamSelesai.setName("JamSelesai"); // NOI18N
        JamSelesai.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                JamSelesaiKeyPressed(evt);
            }
        });
        FormInput.add(JamSelesai);
        JamSelesai.setBounds(427, 40, 62, 23);

        jLabel36.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel36.setText("s.d.");
        jLabel36.setName("jLabel36"); // NOI18N
        FormInput.add(jLabel36);
        jLabel36.setBounds(400, 40, 25, 23);

        jLabel37.setText("Status :");
        jLabel37.setName("jLabel37"); // NOI18N
        FormInput.add(jLabel37);
        jLabel37.setBounds(650, 40, 50, 23);

        jLabel11.setText("Operasi :");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(385, 70, 60, 23);

        KdOperasi.setEditable(false);
        KdOperasi.setHighlighter(null);
        KdOperasi.setName("KdOperasi"); // NOI18N
        FormInput.add(KdOperasi);
        KdOperasi.setBounds(448, 70, 80, 23);

        NmOperasi.setEditable(false);
        NmOperasi.setHighlighter(null);
        NmOperasi.setName("NmOperasi"); // NOI18N
        FormInput.add(NmOperasi);
        NmOperasi.setBounds(530, 70, 283, 23);

        BtnOperasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnOperasi.setMnemonic('X');
        BtnOperasi.setToolTipText("Alt+X");
        BtnOperasi.setName("BtnOperasi"); // NOI18N
        BtnOperasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnOperasiActionPerformed(evt);
            }
        });
        BtnOperasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnOperasiKeyPressed(evt);
            }
        });
        FormInput.add(BtnOperasi);
        BtnOperasi.setBounds(815, 70, 28, 23);

        jLabel12.setText("Ruang OK :");
        jLabel12.setName("jLabel12"); // NOI18N
        FormInput.add(jLabel12);
        jLabel12.setBounds(570, 10, 60, 23);

        KdRuangOperasi.setEditable(false);
        KdRuangOperasi.setHighlighter(null);
        KdRuangOperasi.setName("KdRuangOperasi"); // NOI18N
        FormInput.add(KdRuangOperasi);
        KdRuangOperasi.setBounds(633, 10, 40, 23);

        NmRuangOperasi.setEditable(false);
        NmRuangOperasi.setHighlighter(null);
        NmRuangOperasi.setName("NmRuangOperasi"); // NOI18N
        FormInput.add(NmRuangOperasi);
        NmRuangOperasi.setBounds(675, 10, 138, 23);

        BtnRuangOperasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnRuangOperasi.setMnemonic('X');
        BtnRuangOperasi.setToolTipText("Alt+X");
        BtnRuangOperasi.setName("BtnRuangOperasi"); // NOI18N
        BtnRuangOperasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnRuangOperasiActionPerformed(evt);
            }
        });
        BtnRuangOperasi.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnRuangOperasiKeyPressed(evt);
            }
        });
        FormInput.add(BtnRuangOperasi);
        BtnRuangOperasi.setBounds(815, 10, 28, 23);

        PanelInput.add(FormInput, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelInput, java.awt.BorderLayout.PAGE_START);

        PanelAccor.setBackground(new java.awt.Color(255, 255, 255));
        PanelAccor.setName("PanelAccor"); // NOI18N
        PanelAccor.setPreferredSize(new java.awt.Dimension(215, 43));
        PanelAccor.setLayout(new java.awt.BorderLayout());

        ChkAccor.setBackground(new java.awt.Color(255, 250, 250));
        ChkAccor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(250, 255, 248)));
        ChkAccor.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kanan.png"))); // NOI18N
        ChkAccor.setFocusable(false);
        ChkAccor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkAccor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkAccor.setName("ChkAccor"); // NOI18N
        ChkAccor.setPreferredSize(new java.awt.Dimension(15, 20));
        ChkAccor.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kanan.png"))); // NOI18N
        ChkAccor.setRolloverSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kiri.png"))); // NOI18N
        ChkAccor.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/kiri.png"))); // NOI18N
        ChkAccor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkAccorActionPerformed(evt);
            }
        });
        PanelAccor.add(ChkAccor, java.awt.BorderLayout.EAST);

        ScrollMenu.setBorder(null);
        ScrollMenu.setName("ScrollMenu"); // NOI18N
        ScrollMenu.setOpaque(true);
        ScrollMenu.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        ScrollMenu.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        FormMenu.setBackground(new java.awt.Color(255, 255, 255));
        FormMenu.setBorder(null);
        FormMenu.setName("FormMenu"); // NOI18N
        FormMenu.setPreferredSize(new java.awt.Dimension(195, 750));
        FormMenu.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 1, 1));

        BtnKamarInap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnKamarInap.setText("Kamar Inap");
        BtnKamarInap.setFocusPainted(false);
        BtnKamarInap.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnKamarInap.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnKamarInap.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnKamarInap.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnKamarInap.setName("BtnKamarInap"); // NOI18N
        BtnKamarInap.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnKamarInap.setRoundRect(false);
        BtnKamarInap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKamarInapActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnKamarInap); // Pindah ke susunan terorganisir di bawah

        BtnPermintaanLab.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnPermintaanLab.setText("Permintaan Lab");
        BtnPermintaanLab.setFocusPainted(false);
        BtnPermintaanLab.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnPermintaanLab.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnPermintaanLab.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnPermintaanLab.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnPermintaanLab.setName("BtnPermintaanLab"); // NOI18N
        BtnPermintaanLab.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnPermintaanLab.setRoundRect(false);
        BtnPermintaanLab.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPermintaanLabActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnPermintaanLab); // Pindah ke susunan terorganisir di bawah

        BtnRiwayatPasien.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnRiwayatPasien.setText("Riwayat Perawatan");
        BtnRiwayatPasien.setFocusPainted(false);
        BtnRiwayatPasien.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnRiwayatPasien.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnRiwayatPasien.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnRiwayatPasien.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnRiwayatPasien.setName("BtnRiwayatPasien"); // NOI18N
        BtnRiwayatPasien.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnRiwayatPasien.setRoundRect(false);
        BtnRiwayatPasien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnRiwayatPasienActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnRiwayatPasien); // Pindah ke susunan terorganisir di bawah

        BtnEdukasiAnestesi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnEdukasiAnestesi.setText("Edukasi Anestesi");
        BtnEdukasiAnestesi.setFocusPainted(false);
        BtnEdukasiAnestesi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnEdukasiAnestesi.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnEdukasiAnestesi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnEdukasiAnestesi.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnEdukasiAnestesi.setName("BtnEdukasiAnestesi"); // NOI18N
        BtnEdukasiAnestesi.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnEdukasiAnestesi.setRoundRect(false);
        BtnEdukasiAnestesi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEdukasiAnestesiActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnEdukasiAnestesi); // Pindah ke susunan terorganisir di bawah

        BtnKonversiAnestesi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnKonversiAnestesi.setText("Konversi Anestesi");
        BtnKonversiAnestesi.setFocusPainted(false);
        BtnKonversiAnestesi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnKonversiAnestesi.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnKonversiAnestesi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnKonversiAnestesi.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnKonversiAnestesi.setName("BtnKonversiAnestesi"); // NOI18N
        BtnKonversiAnestesi.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnKonversiAnestesi.setRoundRect(false);
        BtnKonversiAnestesi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKonversiAnestesiActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnKonversiAnestesi); // Pindah ke susunan terorganisir di bawah

        BtnSIAGeneral.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnSIAGeneral.setText("SIA General Anestesi");
        BtnSIAGeneral.setFocusPainted(false);
        BtnSIAGeneral.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnSIAGeneral.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnSIAGeneral.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnSIAGeneral.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnSIAGeneral.setName("BtnSIAGeneral"); // NOI18N
        BtnSIAGeneral.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnSIAGeneral.setRoundRect(false);
        BtnSIAGeneral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSIAGeneralActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnSIAGeneral); // Pindah ke susunan terorganisir di bawah

        BtnSIARegional.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnSIARegional.setText("SIA Regional Anestesi");
        BtnSIARegional.setFocusPainted(false);
        BtnSIARegional.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnSIARegional.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnSIARegional.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnSIARegional.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnSIARegional.setName("BtnSIARegional"); // NOI18N
        BtnSIARegional.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnSIARegional.setRoundRect(false);
        BtnSIARegional.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSIARegionalActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnSIARegional); // Pindah ke susunan terorganisir di bawah

        BtnPenilaianPreInduksi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnPenilaianPreInduksi.setText("Penilaian Pre Induksi");
        BtnPenilaianPreInduksi.setFocusPainted(false);
        BtnPenilaianPreInduksi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnPenilaianPreInduksi.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnPenilaianPreInduksi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnPenilaianPreInduksi.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnPenilaianPreInduksi.setName("BtnPenilaianPreInduksi"); // NOI18N
        BtnPenilaianPreInduksi.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnPenilaianPreInduksi.setRoundRect(false);
        BtnPenilaianPreInduksi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPenilaianPreInduksiActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnPenilaianPreInduksi); // Pindah ke susunan terorganisir di bawah

        BtnChecklistPreOperasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnChecklistPreOperasi.setText("Check List Pre Operasi");
        BtnChecklistPreOperasi.setFocusPainted(false);
        BtnChecklistPreOperasi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnChecklistPreOperasi.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnChecklistPreOperasi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnChecklistPreOperasi.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnChecklistPreOperasi.setName("BtnChecklistPreOperasi"); // NOI18N
        BtnChecklistPreOperasi.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnChecklistPreOperasi.setRoundRect(false);
        BtnChecklistPreOperasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnChecklistPreOperasiActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnChecklistPreOperasi); // Pindah ke susunan terorganisir di bawah

        BtnSignInSebelumAnestesi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnSignInSebelumAnestesi.setText("Sign-In Sebelum Anestesi");
        BtnSignInSebelumAnestesi.setFocusPainted(false);
        BtnSignInSebelumAnestesi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnSignInSebelumAnestesi.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnSignInSebelumAnestesi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnSignInSebelumAnestesi.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnSignInSebelumAnestesi.setName("BtnSignInSebelumAnestesi"); // NOI18N
        BtnSignInSebelumAnestesi.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnSignInSebelumAnestesi.setRoundRect(false);
        BtnSignInSebelumAnestesi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSignInSebelumAnestesiActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnSignInSebelumAnestesi); // Pindah ke susunan terorganisir di bawah

        BtnTimeOutSebelumInsisi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnTimeOutSebelumInsisi.setText("Time-Out Sebelum Insisi");
        BtnTimeOutSebelumInsisi.setFocusPainted(false);
        BtnTimeOutSebelumInsisi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnTimeOutSebelumInsisi.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnTimeOutSebelumInsisi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnTimeOutSebelumInsisi.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnTimeOutSebelumInsisi.setName("BtnTimeOutSebelumInsisi"); // NOI18N
        BtnTimeOutSebelumInsisi.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnTimeOutSebelumInsisi.setRoundRect(false);
        BtnTimeOutSebelumInsisi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTimeOutSebelumInsisiActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnTimeOutSebelumInsisi); // Pindah ke susunan terorganisir di bawah

        BtnSignOutSebelumMenutupLuka.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnSignOutSebelumMenutupLuka.setText("Sign-Out Sebelum Menutup Luka");
        BtnSignOutSebelumMenutupLuka.setFocusPainted(false);
        BtnSignOutSebelumMenutupLuka.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnSignOutSebelumMenutupLuka.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnSignOutSebelumMenutupLuka.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnSignOutSebelumMenutupLuka.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnSignOutSebelumMenutupLuka.setName("BtnSignOutSebelumMenutupLuka"); // NOI18N
        BtnSignOutSebelumMenutupLuka.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnSignOutSebelumMenutupLuka.setRoundRect(false);
        BtnSignOutSebelumMenutupLuka.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSignOutSebelumMenutupLukaActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnSignOutSebelumMenutupLuka); // Pindah ke susunan terorganisir di bawah

        BtnChecklistKeselamatanOK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnChecklistKeselamatanOK.setText("Checklist Keselamatan OK (3 Fase)");
        BtnChecklistKeselamatanOK.setFocusPainted(false);
        BtnChecklistKeselamatanOK.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnChecklistKeselamatanOK.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnChecklistKeselamatanOK.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnChecklistKeselamatanOK.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnChecklistKeselamatanOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnChecklistKeselamatanOKActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnChecklistKeselamatanOK); // Pindah ke susunan terorganisir di bawah

        BtnChecklistPostOperasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnChecklistPostOperasi.setText("Check List Post Operasi");
        BtnChecklistPostOperasi.setFocusPainted(false);
        BtnChecklistPostOperasi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnChecklistPostOperasi.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnChecklistPostOperasi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnChecklistPostOperasi.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnChecklistPostOperasi.setName("BtnChecklistPostOperasi"); // NOI18N
        BtnChecklistPostOperasi.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnChecklistPostOperasi.setRoundRect(false);
        BtnChecklistPostOperasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnChecklistPostOperasiActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnChecklistPostOperasi); // Pindah ke susunan terorganisir di bawah

        BtnPreOperasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnPreOperasi.setText("Penilaian Pre Operasi");
        BtnPreOperasi.setFocusPainted(false);
        BtnPreOperasi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnPreOperasi.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnPreOperasi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnPreOperasi.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnPreOperasi.setName("BtnPreOperasi"); // NOI18N
        BtnPreOperasi.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnPreOperasi.setRoundRect(false);
        BtnPreOperasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPreOperasiActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnPreOperasi); // Pindah ke susunan terorganisir di bawah

        BtnPenandaanAreaOperasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnPenandaanAreaOperasi.setText("Penandaan Area Operasi");
        BtnPenandaanAreaOperasi.setFocusPainted(false);
        BtnPenandaanAreaOperasi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnPenandaanAreaOperasi.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnPenandaanAreaOperasi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnPenandaanAreaOperasi.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnPenandaanAreaOperasi.setName("BtnPenandaanAreaOperasi"); // NOI18N
        BtnPenandaanAreaOperasi.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnPenandaanAreaOperasi.setRoundRect(false);
        BtnPenandaanAreaOperasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPenandaanAreaOperasiActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnPenandaanAreaOperasi); // Pindah ke susunan terorganisir di bawah

        BtnMonitoringSingleUse.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnMonitoringSingleUse.setText("Monitoring Peralatan Single Use");
        BtnMonitoringSingleUse.setFocusPainted(false);
        BtnMonitoringSingleUse.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnMonitoringSingleUse.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnMonitoringSingleUse.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnMonitoringSingleUse.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnMonitoringSingleUse.setName("BtnMonitoringSingleUse"); // NOI18N
        BtnMonitoringSingleUse.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnMonitoringSingleUse.setRoundRect(false);
        BtnMonitoringSingleUse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnMonitoringSingleUseActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnMonitoringSingleUse); // Pindah ke susunan terorganisir di bawah

        BtnCatatanSedasiRscb.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnCatatanSedasiRscb.setText("Anestesi Sedasi / Status Sedasi");
        BtnCatatanSedasiRscb.setFocusPainted(false);
        BtnCatatanSedasiRscb.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnCatatanSedasiRscb.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnCatatanSedasiRscb.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnCatatanSedasiRscb.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnCatatanSedasiRscb.setName("BtnCatatanSedasiRscb"); // NOI18N
        BtnCatatanSedasiRscb.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnCatatanSedasiRscb.setRoundRect(false);
        BtnCatatanSedasiRscb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCatatanSedasiRscbActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnCatatanSedasiRscb); // Pindah ke susunan terorganisir di bawah

        BtnPraBedahRscb.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnPraBedahRscb.setText("Asesmen Pra Bedah RSCB");
        BtnPraBedahRscb.setFocusPainted(false);
        BtnPraBedahRscb.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnPraBedahRscb.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnPraBedahRscb.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnPraBedahRscb.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnPraBedahRscb.setName("BtnPraBedahRscb"); // NOI18N
        BtnPraBedahRscb.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnPraBedahRscb.setRoundRect(false);
        BtnPraBedahRscb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPraBedahRscbActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnPraBedahRscb); // Pindah ke susunan terorganisir di bawah

        BtnCatatanAnastesiSedasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnCatatanAnastesiSedasi.setText("Catatan Anestesi-Sedasi");
        BtnCatatanAnastesiSedasi.setFocusPainted(false);
        BtnCatatanAnastesiSedasi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnCatatanAnastesiSedasi.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnCatatanAnastesiSedasi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnCatatanAnastesiSedasi.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnCatatanAnastesiSedasi.setName("BtnCatatanAnastesiSedasi"); // NOI18N
        BtnCatatanAnastesiSedasi.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnCatatanAnastesiSedasi.setRoundRect(false);
        BtnCatatanAnastesiSedasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCatatanAnastesiSedasiActionPerformed(evt);
            }
        });
        // Menu "Catatan Anestesi-Sedasi" generic disembunyikan agar tidak
        // membingungkan; pakai "Anestesi Sedasi / Status Sedasi" (RSCB).
        // FormMenu.add(BtnCatatanAnastesiSedasi);

        BtnPreAnastesi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnPreAnastesi.setText("Penilaian Pre Anestesi");
        BtnPreAnastesi.setFocusPainted(false);
        BtnPreAnastesi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnPreAnastesi.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnPreAnastesi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnPreAnastesi.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnPreAnastesi.setName("BtnPreAnastesi"); // NOI18N
        BtnPreAnastesi.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnPreAnastesi.setRoundRect(false);
        BtnPreAnastesi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPreAnastesiActionPerformed(evt);
            }
        });
        // Menu "Penilaian Pre Anestesi" (generic RMAsesmenPraAnestesi) disembunyikan
        // dari sidebar agar tidak membingungkan; pakai "Penilaian Pre Anestesi RSCB".
        // FormMenu.add(BtnPreAnastesi);

        BtnPreAnastesiRscb.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnPreAnastesiRscb.setText("Asesmen Pra Anestesi");
        BtnPreAnastesiRscb.setFocusPainted(false);
        BtnPreAnastesiRscb.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnPreAnastesiRscb.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnPreAnastesiRscb.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnPreAnastesiRscb.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnPreAnastesiRscb.setName("BtnPreAnastesiRscb"); // NOI18N
        BtnPreAnastesiRscb.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnPreAnastesiRscb.setRoundRect(false);
        BtnPreAnastesiRscb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPreAnastesiRscbActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnPreAnastesiRscb); // Pindah ke susunan terorganisir di bawah

        BtnPreInduksiRscb.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnPreInduksiRscb.setText("Asesmen Pra Induksi RSCB");
        BtnPreInduksiRscb.setFocusPainted(false);
        BtnPreInduksiRscb.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnPreInduksiRscb.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnPreInduksiRscb.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnPreInduksiRscb.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnPreInduksiRscb.setName("BtnPreInduksiRscb"); // NOI18N
        BtnPreInduksiRscb.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnPreInduksiRscb.setRoundRect(false);
        BtnPreInduksiRscb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPreInduksiRscbActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnPreInduksiRscb); // Pindah ke susunan terorganisir di bawah

        BtnCatatanSedasiAnestesiRssm.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnCatatanSedasiAnestesiRssm.setText("Catatan Sedasi/Anestesi RSSM");
        BtnCatatanSedasiAnestesiRssm.setFocusPainted(false);
        BtnCatatanSedasiAnestesiRssm.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnCatatanSedasiAnestesiRssm.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnCatatanSedasiAnestesiRssm.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnCatatanSedasiAnestesiRssm.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnCatatanSedasiAnestesiRssm.setName("BtnCatatanSedasiAnestesiRssm"); // NOI18N
        BtnCatatanSedasiAnestesiRssm.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnCatatanSedasiAnestesiRssm.setRoundRect(false);
        BtnCatatanSedasiAnestesiRssm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCatatanSedasiAnestesiRssmActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnCatatanSedasiAnestesiRssm); // Pindah ke susunan terorganisir di bawah

        BtnLembarObservasiAnestesi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnLembarObservasiAnestesi.setText("Status Anastesi");
        BtnLembarObservasiAnestesi.setFocusPainted(false);
        BtnLembarObservasiAnestesi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnLembarObservasiAnestesi.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnLembarObservasiAnestesi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnLembarObservasiAnestesi.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnLembarObservasiAnestesi.setName("BtnLembarObservasiAnestesi"); // NOI18N
        BtnLembarObservasiAnestesi.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnLembarObservasiAnestesi.setRoundRect(false);
        BtnLembarObservasiAnestesi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnLembarObservasiAnestesiActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnLembarObservasiAnestesi); // Pindah ke susunan terorganisir di bawah

        BtnPreAnastesi1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnPreAnastesi1.setText("Monitoring Anestesi");
        BtnPreAnastesi1.setFocusPainted(false);
        BtnPreAnastesi1.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnPreAnastesi1.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnPreAnastesi1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnPreAnastesi1.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnPreAnastesi1.setName("BtnPreAnastesi1"); // NOI18N
        BtnPreAnastesi1.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnPreAnastesi1.setRoundRect(false);
        BtnPreAnastesi1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPreAnastesi1ActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnPreAnastesi1); // Pindah ke susunan terorganisir di bawah

        BtnTagihanOperasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnTagihanOperasi.setText("Tagihan Operasi/VK");
        BtnTagihanOperasi.setFocusPainted(false);
        BtnTagihanOperasi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnTagihanOperasi.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnTagihanOperasi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnTagihanOperasi.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnTagihanOperasi.setName("BtnTagihanOperasi"); // NOI18N
        BtnTagihanOperasi.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnTagihanOperasi.setRoundRect(false);
        BtnTagihanOperasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTagihanOperasiActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnTagihanOperasi); // Pindah ke susunan terorganisir di bawah

        BtnLaporanOperasi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnLaporanOperasi.setText("Laporan Operasi");
        BtnLaporanOperasi.setFocusPainted(false);
        BtnLaporanOperasi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnLaporanOperasi.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnLaporanOperasi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnLaporanOperasi.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnLaporanOperasi.setName("BtnLaporanOperasi"); // NOI18N
        BtnLaporanOperasi.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnLaporanOperasi.setRoundRect(false);
        BtnLaporanOperasi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnLaporanOperasiActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnLaporanOperasi); // Pindah ke susunan terorganisir di bawah

        BtnObatBhp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnObatBhp.setText("Permintaan Resep");
        BtnObatBhp.setFocusPainted(false);
        BtnObatBhp.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnObatBhp.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnObatBhp.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnObatBhp.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnObatBhp.setName("BtnObatBhp"); // NOI18N
        BtnObatBhp.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnObatBhp.setRoundRect(false);
        BtnObatBhp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnObatBhpActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnObatBhp); // Pindah ke susunan terorganisir di bawah

        BtnTransferAntarRuang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnTransferAntarRuang.setText("Transfer Antar Ruang");
        BtnTransferAntarRuang.setFocusPainted(false);
        BtnTransferAntarRuang.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnTransferAntarRuang.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnTransferAntarRuang.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnTransferAntarRuang.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnTransferAntarRuang.setName("BtnTransferAntarRuang"); // NOI18N
        BtnTransferAntarRuang.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnTransferAntarRuang.setRoundRect(false);
        BtnTransferAntarRuang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnTransferAntarRuangActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnTransferAntarRuang); // Pindah ke susunan terorganisir di bawah

        BtnSkorAldrettePascaAnestesi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnSkorAldrettePascaAnestesi.setText("Skor Aldrette Pasca Anestesi");
        BtnSkorAldrettePascaAnestesi.setFocusPainted(false);
        BtnSkorAldrettePascaAnestesi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnSkorAldrettePascaAnestesi.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnSkorAldrettePascaAnestesi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnSkorAldrettePascaAnestesi.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnSkorAldrettePascaAnestesi.setName("BtnSkorAldrettePascaAnestesi"); // NOI18N
        BtnSkorAldrettePascaAnestesi.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnSkorAldrettePascaAnestesi.setRoundRect(false);
        BtnSkorAldrettePascaAnestesi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSkorAldrettePascaAnestesiActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnSkorAldrettePascaAnestesi); // Pindah ke susunan terorganisir di bawah

        BtnSkorStewardPascaAnestesi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnSkorStewardPascaAnestesi.setText("Skor Steward Pasca Anestesi");
        BtnSkorStewardPascaAnestesi.setFocusPainted(false);
        BtnSkorStewardPascaAnestesi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnSkorStewardPascaAnestesi.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnSkorStewardPascaAnestesi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnSkorStewardPascaAnestesi.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnSkorStewardPascaAnestesi.setName("BtnSkorStewardPascaAnestesi"); // NOI18N
        BtnSkorStewardPascaAnestesi.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnSkorStewardPascaAnestesi.setRoundRect(false);
        BtnSkorStewardPascaAnestesi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSkorStewardPascaAnestesiActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnSkorStewardPascaAnestesi); // Pindah ke susunan terorganisir di bawah

        BtnSkorBromagePascaAnestesi.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnSkorBromagePascaAnestesi.setText("Skor Bromage Pasca Anestesi");
        BtnSkorBromagePascaAnestesi.setFocusPainted(false);
        BtnSkorBromagePascaAnestesi.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnSkorBromagePascaAnestesi.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnSkorBromagePascaAnestesi.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnSkorBromagePascaAnestesi.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnSkorBromagePascaAnestesi.setName("BtnSkorBromagePascaAnestesi"); // NOI18N
        BtnSkorBromagePascaAnestesi.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnSkorBromagePascaAnestesi.setRoundRect(false);
        BtnSkorBromagePascaAnestesi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSkorBromagePascaAnestesiActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnSkorBromagePascaAnestesi); // Pindah ke susunan terorganisir di bawah

        BtnPreAnastesi2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnPreAnastesi2.setText("Asesmen Pra Anastesi/ Sedasi");
        BtnPreAnastesi2.setFocusPainted(false);
        BtnPreAnastesi2.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnPreAnastesi2.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnPreAnastesi2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnPreAnastesi2.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnPreAnastesi2.setName("BtnPreAnastesi2"); // NOI18N
        BtnPreAnastesi2.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnPreAnastesi2.setRoundRect(false);
        BtnPreAnastesi2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPreAnastesi2ActionPerformed(evt);
            }
        });
        // Menu "Asesmen Pra Anastesi/ Sedasi" (generic RMAsesmenPraAnestesi) disembunyikan
        // agar tidak membingungkan; pakai "Penilaian Pre Anestesi RSCB" (RMAsesmenPraAnestesiRscb).
        // FormMenu.add(BtnPreAnastesi2);

        BtnFormRobson.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnFormRobson.setText("Form Robson");
        BtnFormRobson.setFocusPainted(false);
        BtnFormRobson.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnFormRobson.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnFormRobson.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnFormRobson.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnFormRobson.setName("BtnFormRobson"); // NOI18N
        BtnFormRobson.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnFormRobson.setRoundRect(false);
        BtnFormRobson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnFormRobsonActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnFormRobson); // Pindah ke susunan terorganisir di bawah

        BtnPreAnastesi3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/item.png"))); // NOI18N
        BtnPreAnastesi3.setText("Asuhan Keperawatan Kamar Bedah");
        BtnPreAnastesi3.setFocusPainted(false);
        BtnPreAnastesi3.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        BtnPreAnastesi3.setGlassColor(new java.awt.Color(255, 255, 255));
        BtnPreAnastesi3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BtnPreAnastesi3.setMargin(new java.awt.Insets(1, 1, 1, 1));
        BtnPreAnastesi3.setName("BtnPreAnastesi3"); // NOI18N
        BtnPreAnastesi3.setPreferredSize(new java.awt.Dimension(190, 23));
        BtnPreAnastesi3.setRoundRect(false);
        BtnPreAnastesi3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnPreAnastesi3ActionPerformed(evt);
            }
        });
        // FormMenu.add(BtnPreAnastesi3); // Pindah ke susunan terorganisir di bawah

        // =========================================================================
        // SUSUNAN MENU SIDEBAR (FormMenu) DlgBookingOperasi - RSUD CABANG BUNGIN
        // Ditata rapi per kelompok alur pelayanan operasi.
        // Form yang kosong dan bukan disimpan ke tabel ber-kata rscb di-hidden.
        // =========================================================================

        // --- 1. PELAYANAN, RAWAT & KEUANGAN ---
        FormMenu.add(BtnKamarInap);
        FormMenu.add(BtnPermintaanLab);
        FormMenu.add(BtnRiwayatPasien);
        FormMenu.add(BtnObatBhp);
        FormMenu.add(BtnTagihanOperasi);
        FormMenu.add(BtnTransferAntarRuang);

        // --- 2. ASESMEN PRA BEDAH & PRA ANESTESI RSCB ---
        FormMenu.add(BtnPraBedahRscb);              // Asesmen Pra Bedah RSCB (asesmen_pra_bedah_rscb)
        FormMenu.add(BtnPreAnastesiRscb);           // Asesmen Pra Anestesi RSCB (asesmen_pra_anestesi_rscb)
        FormMenu.add(BtnPreInduksiRscb);            // Asesmen Pra Induksi RSCB (asesmen_pra_induksi_rscb)
        FormMenu.add(BtnEdukasiAnestesi);           // Edukasi Anestesi RSCB (edukasi_anestesi_rscb)
        FormMenu.add(BtnKonversiAnestesi);          // Konversi Anestesi RSCB (konversi_anestesi_rscb)
        FormMenu.add(BtnSIAGeneral);                // Persetujuan Tindakan Anestesi General (sia_general_rscb)
        FormMenu.add(BtnSIARegional);               // Persetujuan Tindakan Anestesi Regional (sia_regional_rscb)

        // --- 3. SURGICAL SAFETY CHECKLIST (KESELAMATAN OPERASI) ---
        FormMenu.add(BtnChecklistPreOperasi);       // Check List Pre Operasi (checklist_pre_operasi - 74 data)
        FormMenu.add(BtnChecklistKeselamatanOK);    // Checklist Keselamatan OK 3 Fase (RMChecklistKeselamatanOKRscb)
        FormMenu.add(BtnSignInSebelumAnestesi);     // Sign-In Sebelum Anestesi (signin_sebelum_anestesi - 162 data)
        FormMenu.add(BtnTimeOutSebelumInsisi);      // Time-Out Sebelum Insisi (timeout_sebelum_insisi - 161 data)
        FormMenu.add(BtnSignOutSebelumMenutupLuka); // Sign-Out Sebelum Menutup Luka (signout_sebelum_menutup_luka - 159 data)
        FormMenu.add(BtnChecklistPostOperasi);      // Check List Post Operasi (checklist_post_operasi - 66 data)

        // --- 4. INTRA OPERASI & PELAPORAN ---
        FormMenu.add(BtnLaporanOperasi);            // Laporan Operasi (laporan_operasi_casemix - 489 data)
        FormMenu.add(BtnCatatanSedasiRscb);         // Anestesi Sedasi / Status Sedasi RSCB (catatan_sedasi_*)
        FormMenu.add(BtnLembarObservasiAnestesi);   // Status Anastesi (catatan_anestesi_data_awal dll - aktif)
        FormMenu.add(BtnPreAnastesi1);              // Monitoring Anestesi RSCB (monitoring_anestesirscb)
        FormMenu.add(BtnPreAnastesi3);              // Asuhan Keperawatan Kamar Bedah (asuhan_keperawatan_kamar_bedah_data_awal - 91 data)

        // --- 5. PASCA OPERASI & MONITORING ALAT ---
        FormMenu.add(BtnSkorAldrettePascaAnestesi); // Skor Aldrette Pasca Anestesi (skor_aldrette_pasca_anestesi - 16 data)
        FormMenu.add(BtnSkorBromagePascaAnestesi);  // Skor Bromage Pasca Anestesi (skor_bromage_pasca_anestesi - 30 data)
        FormMenu.add(BtnMonitoringSingleUse);       // Monitoring Peralatan Single Use (monitoring_peralatan_single_use_reuse - 32 data)

        // --- FORM YANG DI-HIDDEN (KOSONG & BUKAN TABEL RSCB / GENERIC LAMA) ---
        // FormMenu.add(BtnPenilaianPreInduksi);     // HIDDEN: Tabel penilaian_pre_induksi kosong (0 baris), digantikan BtnPreInduksiRscb
        // FormMenu.add(BtnCatatanSedasiAnestesiRssm);// HIDDEN: Tabel rm_catatan_sedasi_anestesi_rssm kosong (0 baris, form RSSM)
        // FormMenu.add(BtnSkorStewardPascaAnestesi);// HIDDEN: Tabel skor_steward_pasca_anestesi kosong (0 baris)
        // FormMenu.add(BtnFormRobson);              // HIDDEN: Tabel form_robson_klasifikasi kosong (0 baris)
        // FormMenu.add(BtnPreOperasi);              // HIDDEN: Generic lama (1 baris coba), digantikan BtnPraBedahRscb
        // FormMenu.add(BtnPenandaanAreaOperasi);    // HIDDEN: Generic lama (1 baris 2023)
        // FormMenu.add(BtnPreAnastesi);             // HIDDEN: Generic lama, digantikan RMAsesmenPraAnestesiRscb
        // FormMenu.add(BtnPreAnastesi2);            // HIDDEN: Generic lama, digantikan RMAsesmenPraAnestesiRscb
        // FormMenu.add(BtnCatatanAnastesiSedasi);   // HIDDEN: Generic lama, digantikan RMCatatanSedasiAnestesiRscb

        ScrollMenu.setViewportView(FormMenu);

        PanelAccor.add(ScrollMenu, java.awt.BorderLayout.CENTER);

        internalFrame1.add(PanelAccor, java.awt.BorderLayout.WEST);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        Valid.pindah(evt, Status, KdDokter);

}//GEN-LAST:event_TNoRwKeyPressed

    private void DTPTglKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPTglKeyPressed
        Valid.pindah(evt, TCari, JamMulai);
}//GEN-LAST:event_DTPTglKeyPressed

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "pasien");
        } else if (NmDokter.getText().trim().equals("")) {
            Valid.textKosong(KdDokter, "Operator");
        } else if (NmOperasi.getText().trim().equals("")) {
            Valid.textKosong(KdOperasi, "Operasi");
        } else if (NmRuangOperasi.getText().trim().equals("")) {
            Valid.textKosong(BtnRuangOperasi, "Ruang Operasi");
        } else if ((!(JamMulai.getSelectedItem().toString() + MenitMulai.getSelectedItem().toString() + DetikMulai.getSelectedItem().toString()).equals("000000"))
                && (Sequel.cariInteger("select count(booking_operasi.no_rawat) from booking_operasi where booking_operasi.tanggal='" + Valid.SetTgl(DTPTgl.getSelectedItem() + "") + "' "
                        + "and booking_operasi.kd_ruang_ok='" + KdRuangOperasi.getText() + "' and booking_operasi.no_rawat<>'" + TNoRw.getText() + "' and "
                        + "booking_operasi.jam_mulai between '" + JamMulai.getSelectedItem() + ":" + MenitMulai.getSelectedItem() + ":" + DetikMulai.getSelectedItem() + "' and '" + JamSelesai.getSelectedItem() + ":" + MenitSelesai.getSelectedItem() + ":" + DetikSelesai.getSelectedItem() + "'") > 0)) {
            JOptionPane.showMessageDialog(rootPane, "Jadwal bentrok dengan jam mulai operasi yang lain..!!");
            JamMulai.requestFocus();
        } else {
            if (Sequel.cariRegistrasi(TNoRw.getText()) > 0) {
                JOptionPane.showMessageDialog(rootPane, "Data billing sudah terverifikasi.\nSilahkan hubungi bagian kasir/keuangan ..!!");
                TCari.requestFocus();
            } else {
                if (Sequel.menyimpantf("booking_operasi", "?,?,?,?,?,?,?,?", "data", 8, new String[]{
                    TNoRw.getText(), KdOperasi.getText(), Valid.SetTgl(DTPTgl.getSelectedItem() + ""),
                    JamMulai.getSelectedItem() + ":" + MenitMulai.getSelectedItem() + ":" + DetikMulai.getSelectedItem(),
                    JamSelesai.getSelectedItem() + ":" + MenitSelesai.getSelectedItem() + ":" + DetikSelesai.getSelectedItem(),
                    Status.getSelectedItem().toString(), KdDokter.getText(), KdRuangOperasi.getText()
                }) == true) {
                    tampil();
                    emptTeks();
                }
            }
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnSimpanActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnOperasi, BtnBatal);
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
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis...!!!!");
            DTPTgl.requestFocus();
        } else if (TPasien.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Gagal menghapus. Pilih dulu data yang mau dihapus.\nKlik data pada table untuk memilih...!!!!");
        } else if (!(TPasien.getText().trim().equals(""))) {
            if (tbObat.getSelectedRow() != -1) {
                try {
                    Sequel.queryu("delete from booking_operasi "
                            + "where no_rawat='" + TNoRw.getText() + "' "
                            + "and tanggal='" + Valid.SetTgl(DTPTgl.getSelectedItem() + "") + "' "
                            + "and jam_mulai='" + JamMulai.getSelectedItem() + ":" + MenitMulai.getSelectedItem() + ":" + DetikMulai.getSelectedItem() + "' "
                            + "and jam_selesai='" + JamSelesai.getSelectedItem() + ":" + MenitSelesai.getSelectedItem() + ":" + DetikSelesai.getSelectedItem() + "' "
                            + "and kd_dokter='" + KdDokter.getText() + "' "
                            + "and status='" + Status.getSelectedItem() + "' "
                            + "and kode_paket='" + KdOperasi.getText() + "'");
                    tampil();
                    emptTeks();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih terlebih dulu data yang mau anda hapus...\n Klik data pada table untuk memilih data...!!!!");
                }
            }
        }
}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnHapusActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnBatal, BtnPrint);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            dispose();
        } else {
            Valid.pindah(evt, BtnPrint, TCari);
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

            Sequel.queryu("delete from temporary where temp37='" + akses.getalamatip() + "'");
            for (i = 0; i < tabMode.getRowCount(); i++) {
                Sequel.menyimpan("temporary", "'" + i + "','"
                        + tabMode.getValueAt(i, 0).toString() + "','"
                        + tabMode.getValueAt(i, 1).toString() + "','"
                        + tabMode.getValueAt(i, 2).toString() + "','"
                        + tabMode.getValueAt(i, 3).toString() + "','"
                        + tabMode.getValueAt(i, 4).toString() + "','"
                        + tabMode.getValueAt(i, 5).toString() + "','"
                        + tabMode.getValueAt(i, 6).toString() + "','"
                        + tabMode.getValueAt(i, 7).toString() + "','"
                        + tabMode.getValueAt(i, 8).toString() + "','"
                        + tabMode.getValueAt(i, 9).toString() + "','"
                        + tabMode.getValueAt(i, 10).toString() + "','"
                        + tabMode.getValueAt(i, 11).toString() + "','"
                        + tabMode.getValueAt(i, 12).toString() + "','"
                        + tabMode.getValueAt(i, 13).toString() + "','"
                        + tabMode.getValueAt(i, 14).toString() + "','"
                        + tabMode.getValueAt(i, 15).toString() + "','"
                        + tabMode.getValueAt(i, 16).toString() + "','"
                        + tabMode.getValueAt(i, 17).toString() + "','','','','','','','','','','','','','','','','','','','" + akses.getalamatip() + "'", "Rekap Nota Pembayaran");
            }

            Valid.MyReportqry("rptJadwalOperasi.jasper", "report", "::[ Laporan Daftar Jadwal Operasi ]::", "select * from temporary where temporary.temp37='" + akses.getalamatip() + "' order by temporary.no", param);
        }
        this.setCursor(Cursor.getDefaultCursor());
}//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPrintActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnHapus, BtnKeluar);
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

private void BtnOperatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnOperatorActionPerformed
    dokter.isCek();
    dokter.TCari.requestFocus();
    dokter.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
    dokter.setLocationRelativeTo(internalFrame1);
    dokter.setVisible(true);
}//GEN-LAST:event_BtnOperatorActionPerformed

private void BtnOperatorKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnOperatorKeyPressed
    if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
        BtnOperatorActionPerformed(null);
    } else {
        Valid.pindah(evt, Status, BtnOperasi);
    }
}//GEN-LAST:event_BtnOperatorKeyPressed

private void ChkInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkInputActionPerformed
    isForm();
}//GEN-LAST:event_ChkInputActionPerformed

    private void DTPCari1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPCari1KeyPressed

    }//GEN-LAST:event_DTPCari1KeyPressed

    private void DTPCari2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPCari2KeyPressed
        R2.setSelected(true);
    }//GEN-LAST:event_DTPCari2KeyPressed

    private void DTPCari3KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPCari3KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DTPCari3KeyPressed

    private void DTPCari4KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DTPCari4KeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_DTPCari4KeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if (TNoRw.getText().trim().equals("") || TPasien.getText().trim().equals("")) {
            Valid.textKosong(TNoRw, "pasien");
        } else if (NmDokter.getText().trim().equals("")) {
            Valid.textKosong(KdDokter, "Operator");
        } else if (NmOperasi.getText().trim().equals("")) {
            Valid.textKosong(KdOperasi, "Operasi");
        } else if (NmRuangOperasi.getText().trim().equals("")) {
            Valid.textKosong(BtnRuangOperasi, "Ruang Operasi");
        } else if ((!(JamMulai.getSelectedItem().toString() + MenitMulai.getSelectedItem().toString() + DetikMulai.getSelectedItem().toString()).equals("000000"))
                && (Sequel.cariInteger("select count(booking_operasi.no_rawat) from booking_operasi where booking_operasi.tanggal='" + Valid.SetTgl(DTPTgl.getSelectedItem() + "") + "' "
                        + "and booking_operasi.kd_ruang_ok='" + KdRuangOperasi.getText() + "' and booking_operasi.no_rawat<>'" + TNoRw.getText() + "' and "
                        + "booking_operasi.jam_mulai between '" + JamMulai.getSelectedItem() + ":" + MenitMulai.getSelectedItem() + ":" + DetikMulai.getSelectedItem() + "' and '" + JamSelesai.getSelectedItem() + ":" + MenitSelesai.getSelectedItem() + ":" + DetikSelesai.getSelectedItem() + "'") > 0)) {
            JOptionPane.showMessageDialog(rootPane, "Jadwal bentrok dengan jam mulai operasi yang lain..!!");
            JamMulai.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                if (Sequel.mengedittf("booking_operasi", "no_rawat=? and kode_paket=? and tanggal=? and jam_mulai=? and jam_selesai=? and status=? and kd_dokter=? and kd_ruang_ok=?",
                        "no_rawat=?,kode_paket=?,tanggal=?,jam_mulai=?,jam_selesai=?,status=?,kd_dokter=?,kd_ruang_ok=?", 16, new String[]{
                            TNoRw.getText(), KdOperasi.getText(), Valid.SetTgl(DTPTgl.getSelectedItem() + ""),
                            JamMulai.getSelectedItem() + ":" + MenitMulai.getSelectedItem() + ":" + DetikMulai.getSelectedItem(),
                            JamSelesai.getSelectedItem() + ":" + MenitSelesai.getSelectedItem() + ":" + DetikSelesai.getSelectedItem(),
                            Status.getSelectedItem().toString(), KdDokter.getText(), KdRuangOperasi.getText(), tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString(),
                            tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString(),
                            tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString(),
                            tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString(), tbObat.getValueAt(tbObat.getSelectedRow(), 13).toString(),
                            tbObat.getValueAt(tbObat.getSelectedRow(), 16).toString()
                        }) == true) {
                    tampil();
                    emptTeks();
                }
            }
        }
    }//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnEditActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnHapus, BtnKeluar);
        }
    }//GEN-LAST:event_BtnEditKeyPressed

    private void StatusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_StatusKeyPressed
        Valid.pindah(evt, DetikSelesai, BtnOperator);
    }//GEN-LAST:event_StatusKeyPressed

    private void BtnOperasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnOperasiActionPerformed
        penjab = Sequel.cariIsi("select reg_periksa.kd_pj from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText());
        if (posisi.equals("Ranap")) {
            norawatibu = Sequel.cariIsi("select ranap_gabung.no_rawat from ranap_gabung where ranap_gabung.no_rawat2=?", TNoRw.getText());

            if (!norawatibu.equals("")) {
                kelas = Sequel.cariIsi(
                        "select kamar.kelas from kamar inner join kamar_inap on kamar.kd_kamar=kamar_inap.kd_kamar where kamar_inap.no_rawat=? and kamar_inap.stts_pulang='-' order by STR_TO_DATE(concat(kamar_inap.tgl_masuk,' ',kamar_inap.jam_masuk),'%Y-%m-%d %H:%i:%s') desc limit 1", norawatibu);
            } else {
                kelas = Sequel.cariIsi(
                        "select kamar.kelas from kamar inner join kamar_inap on kamar.kd_kamar=kamar_inap.kd_kamar where kamar_inap.no_rawat=? and kamar_inap.stts_pulang='-' order by STR_TO_DATE(concat(kamar_inap.tgl_masuk,' ',kamar_inap.jam_masuk),'%Y-%m-%d %H:%i:%s') desc limit 1", TNoRw.getText());
            }
        } else if (posisi.equals("Ralan")) {
            kelas = "Rawat Jalan";
        }

        operasi.setBayar(penjab, kelas);
        operasi.isCek();
        operasi.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        operasi.setLocationRelativeTo(internalFrame1);
        operasi.setVisible(true);
    }//GEN-LAST:event_BtnOperasiActionPerformed

    private void BtnOperasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnOperasiKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnOperasiActionPerformed(null);
        } else {
            Valid.pindah(evt, BtnOperator, BtnSimpan);
        }
    }//GEN-LAST:event_BtnOperasiKeyPressed

    private void JamMulaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JamMulaiKeyPressed
        Valid.pindah(evt, DTPTgl, MenitMulai);
    }//GEN-LAST:event_JamMulaiKeyPressed

    private void MenitMulaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MenitMulaiKeyPressed
        Valid.pindah(evt, JamMulai, DetikMulai);
    }//GEN-LAST:event_MenitMulaiKeyPressed

    private void DetikMulaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DetikMulaiKeyPressed
        Valid.pindah(evt, MenitMulai, JamSelesai);
    }//GEN-LAST:event_DetikMulaiKeyPressed

    private void JamSelesaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_JamSelesaiKeyPressed
        Valid.pindah(evt, DetikMulai, MenitSelesai);
    }//GEN-LAST:event_JamSelesaiKeyPressed

    private void MenitSelesaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MenitSelesaiKeyPressed
        Valid.pindah(evt, JamSelesai, DetikSelesai);
    }//GEN-LAST:event_MenitSelesaiKeyPressed

    private void DetikSelesaiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DetikSelesaiKeyPressed
        Valid.pindah(evt, MenitSelesai, Status);
    }//GEN-LAST:event_DetikSelesaiKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        tampil();
    }//GEN-LAST:event_formWindowOpened

    private void DTPCari1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_DTPCari1ItemStateChanged
        R2.setSelected(true);
    }//GEN-LAST:event_DTPCari1ItemStateChanged

    private void DTPCari3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_DTPCari3ItemStateChanged
        R3.setSelected(true);
    }//GEN-LAST:event_DTPCari3ItemStateChanged

    private void DTPCari4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_DTPCari4ItemStateChanged
        R3.setSelected(true);
    }//GEN-LAST:event_DTPCari4ItemStateChanged

    private void ChkAccorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkAccorActionPerformed
        isMenu();
    }//GEN-LAST:event_ChkAccorActionPerformed

    private void BtnKamarInapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKamarInapActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                if (Sequel.cariRegistrasi(TNoRw.getText()) > 0) {
                    JOptionPane.showMessageDialog(rootPane, "Data billing sudah terverifikasi..!!");
                } else {
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    akses.setstatus(true);
                    DlgKamarInap kamarinap = new DlgKamarInap(null, false);
                    kamarinap.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                    kamarinap.setLocationRelativeTo(internalFrame1);
                    kamarinap.emptTeks();
                    kamarinap.isCek();
                    norm = Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText());
                    kamarinap.setNoRm(TNoRw.getText(), norm, TPasien.getText());
                    kamarinap.setVisible(true);
                    this.setCursor(Cursor.getDefaultCursor());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnKamarInapActionPerformed

    private void BtnPermintaanLabActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPermintaanLabActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                DlgPermintaanLaboratorium dlgro = new DlgPermintaanLaboratorium(null, false);
                dlgro.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                dlgro.setLocationRelativeTo(internalFrame1);
                dlgro.emptTeks();
                dlgro.isCek();
                dlgro.setNoRm(TNoRw.getText(), Sequel.cariIsi("select reg_periksa.status_lanjut from reg_periksa where reg_periksa.no_rawat=?", TNoRw.getText()), KdDokter.getText(), NmDokter.getText());
                dlgro.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnPermintaanLabActionPerformed

    private void BtnRiwayatPasienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnRiwayatPasienActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMRiwayatPerawatan resume = new RMRiwayatPerawatan(null, true);
                resume.setNoRm(Sequel.cariIsi("select reg_periksa.no_rkm_medis from reg_periksa where reg_periksa.no_rawat=?", tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString()), tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
                resume.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                resume.setLocationRelativeTo(internalFrame1);
                resume.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnRiwayatPasienActionPerformed

    private void BtnTagihanOperasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTagihanOperasiActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                if (Sequel.cariRegistrasi(TNoRw.getText()) > 0) {
                    JOptionPane.showMessageDialog(rootPane, "Data billing sudah terverifikasi, data tidak boleh dihapus.\nSilahkan hubungi bagian kasir/keuangan ..!!");
                    TCari.requestFocus();
                } else {
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    DlgTagihanOperasi dlgro = new DlgTagihanOperasi(null, false);
                    dlgro.SetCariOperasi(KdOperasi.getText(), KdDokter.getText(), NmDokter.getText());
                    dlgro.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                    dlgro.setLocationRelativeTo(internalFrame1);
                    dlgro.setNoRm(TNoRw.getText(), TPasien.getText(), tbObat.getValueAt(tbObat.getSelectedRow(), 15).toString(), KdDokter.getText(), NmDokter.getText());
                    dlgro.setVisible(true);
                    this.setCursor(Cursor.getDefaultCursor());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnTagihanOperasiActionPerformed

    private void BtnObatBhpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnObatBhpActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                if (Sequel.cariRegistrasi(TNoRw.getText()) > 0) {
                    JOptionPane.showMessageDialog(rootPane, "Data billing sudah terverifikasi ..!!");
                } else {
                    this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    if (tbObat.getValueAt(tbObat.getSelectedRow(), 15).toString().equals("Ranap")) {
                        if (!DEPOAKTIFOBAT.equals("")) {
                            lokasistok = DEPOAKTIFOBAT;
                            akses.setkdbangsal(lokasistok);
                        } else {
                            bangsal = Sequel.cariIsi(
                                    "select bangsal.kd_bangsal from bangsal inner join kamar inner join kamar_inap "
                                    + "on bangsal.kd_bangsal=kamar.kd_bangsal and kamar.kd_kamar=kamar_inap.kd_kamar "
                                    + "where kamar_inap.no_rawat=? and kamar_inap.stts_pulang='-' order by STR_TO_DATE(concat(kamar_inap.tgl_masuk,' ',jam_masuk),'%Y-%m-%d %H:%i:%s') desc limit 1", TNoRw.getText());
                            lokasistok = Sequel.cariIsi("select set_depo_ranap.kd_depo from set_depo_ranap where set_depo_ranap.kd_bangsal=?", bangsal);
                            if (lokasistok.equals("")) {
                                if (Sequel.cariIsi("select set_lokasi.asal_stok from set_lokasi").equals("Gunakan Stok Bangsal")) {
                                    lokasistok = bangsal;
                                } else {
                                    lokasistok = Sequel.cariIsi("select set_lokasi.kd_bangsal from set_lokasi");
                                }
                            }
                            akses.setkdbangsal(lokasistok);
                        }
                    }

                    DlgPeresepanDokter resep = new DlgPeresepanDokter(null, false);
                    resep.setSize(internalFrame1.getWidth(), internalFrame1.getHeight());
                    resep.setLocationRelativeTo(internalFrame1);
                    resep.setNoRm(TNoRw.getText(), DTPTgl.getDate(), JamSelesai.getSelectedItem().toString(), MenitSelesai.getSelectedItem().toString(),
                            DetikSelesai.getSelectedItem().toString(), KdDokter.getText(), NmDokter.getText(), tbObat.getValueAt(tbObat.getSelectedRow(), 15).toString().replaceAll("R", "r"));
                    resep.isCek();
                    resep.tampilobat();
                    resep.setVisible(true);
                    this.setCursor(Cursor.getDefaultCursor());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnObatBhpActionPerformed

    private void BtnRuangOperasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnRuangOperasiActionPerformed
        ruangok.isCek();
        ruangok.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
        ruangok.setLocationRelativeTo(internalFrame1);
        ruangok.setVisible(true);
    }//GEN-LAST:event_BtnRuangOperasiActionPerformed

    private void BtnRuangOperasiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnRuangOperasiKeyPressed
        Valid.pindah(evt, DTPTgl, Status);
    }//GEN-LAST:event_BtnRuangOperasiKeyPressed

    private void BtnPreOperasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPreOperasiActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMPenilaianPreOperasi form = new RMPenilaianPreOperasi(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate(), KdDokter.getText(), NmDokter.getText(), NmOperasi.getText());
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnPreOperasiActionPerformed

    private void BtnPreAnastesiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPreAnastesiActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMPenilaianPreAnastesi form = new RMPenilaianPreAnastesi(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnPreAnastesiActionPerformed

    private void BtnTransferAntarRuangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTransferAntarRuangActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                rekammedis.RMTransferInternalRs form = new rekammedis.RMTransferInternalRs(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnTransferAntarRuangActionPerformed

    private void BtnChecklistPreOperasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnChecklistPreOperasiActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMChecklistPreOperasi form = new RMChecklistPreOperasi(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate(), KdDokter.getText(), NmDokter.getText(), NmOperasi.getText());
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnChecklistPreOperasiActionPerformed

    private void BtnSignInSebelumAnestesiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSignInSebelumAnestesiActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMSignInSebelumAnastesi form = new RMSignInSebelumAnastesi(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate(), KdDokter.getText(), NmDokter.getText(), NmOperasi.getText());
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnSignInSebelumAnestesiActionPerformed

    private void BtnPenandaanAreaOperasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPenandaanAreaOperasiActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                rekammedis.RMPenandaanAreaOperasi form = new rekammedis.RMPenandaanAreaOperasi(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.tampil();
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnPenandaanAreaOperasiActionPerformed

    private void BtnMonitoringSingleUseActionPerformed(java.awt.event.ActionEvent evt) {
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                rekammedis.RMMonitoringPeralatanSingleUseReuse form = new rekammedis.RMMonitoringPeralatanSingleUseReuse(null, false);
                form.isCek();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.emptTeks();
                form.tampil();
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    private void BtnCatatanSedasiRscbActionPerformed(java.awt.event.ActionEvent evt) {
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMCatatanSedasiAnestesiRscb form = new RMCatatanSedasiAnestesiRscb(null, false);
                form.isCek();
                form.setNoRm(TNoRw.getText(),
                        tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString(),
                        tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            }
        }
    }

    private void BtnTimeOutSebelumInsisiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnTimeOutSebelumInsisiActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMTimeOutSebelumInsisi form = new RMTimeOutSebelumInsisi(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate(), KdDokter.getText(), NmDokter.getText(), NmOperasi.getText());
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnTimeOutSebelumInsisiActionPerformed

    private void BtnSignOutSebelumMenutupLukaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSignOutSebelumMenutupLukaActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMSignOutSebelumMenutupLuka form = new RMSignOutSebelumMenutupLuka(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate(), KdDokter.getText(), NmDokter.getText(), NmOperasi.getText());
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnSignOutSebelumMenutupLukaActionPerformed

    private void BtnChecklistKeselamatanOKActionPerformed(java.awt.event.ActionEvent evt) {
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                rekammedis.RMChecklistKeselamatanOKRscb form = new rekammedis.RMChecklistKeselamatanOKRscb(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.tampil();
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }

    private void BtnChecklistPostOperasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnChecklistPostOperasiActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMChecklistPostOperasi form = new RMChecklistPostOperasi(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate(), KdDokter.getText(), NmDokter.getText(), NmOperasi.getText());
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnChecklistPostOperasiActionPerformed

    private void BtnSkorAldrettePascaAnestesiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSkorAldrettePascaAnestesiActionPerformed
        if (TPasien.getText().trim().equals("") || TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu dengan menklik data pada table...!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMMonitoringAldrettePascaAnestesi form = new RMMonitoringAldrettePascaAnestesi(null, false);
                form.isCek();
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.tampil();
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnSkorAldrettePascaAnestesiActionPerformed

    private void BtnSkorStewardPascaAnestesiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSkorStewardPascaAnestesiActionPerformed
        if (TPasien.getText().trim().equals("") || TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu dengan menklik data pada table...!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMMonitoringStewardPascaAnestesi form = new RMMonitoringStewardPascaAnestesi(null, false);
                form.isCek();
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.tampil();
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnSkorStewardPascaAnestesiActionPerformed

    private void BtnSkorBromagePascaAnestesiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSkorBromagePascaAnestesiActionPerformed
        if (TPasien.getText().trim().equals("") || TNoRw.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Maaf, Silahkan anda pilih dulu dengan menklik data pada table...!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMMonitoringBromagePascaAnestesi form = new RMMonitoringBromagePascaAnestesi(null, false);
                form.isCek();
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.tampil();
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnSkorBromagePascaAnestesiActionPerformed

    private void BtnPenilaianPreInduksiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPenilaianPreInduksiActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMPenilaianPreInduksi form = new RMPenilaianPreInduksi(null, false);
                form.isCek();
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.emptTeks();
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnPenilaianPreInduksiActionPerformed

    private void BtnCatatanAnastesiSedasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCatatanAnastesiSedasiActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMCatatanAnastesiSedasi form = new RMCatatanAnastesiSedasi(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnCatatanAnastesiSedasiActionPerformed

    private void BtnPreAnastesi1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPreAnastesi1ActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMDataMonitoringAnestesi form = new RMDataMonitoringAnestesi(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnPreAnastesi1ActionPerformed

    private void BtnPreAnastesi2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPreAnastesi2ActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMAsesmenPraAnestesi form = new RMAsesmenPraAnestesi(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnPreAnastesi2ActionPerformed

    private void BtnLembarObservasiAnestesiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnLembarObservasiAnestesiActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMStatusAnestesi form = new RMStatusAnestesi(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnLembarObservasiAnestesiActionPerformed

    private void BtnFormRobsonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnFormRobsonActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMFormRobsonKlasifikasi form = new RMFormRobsonKlasifikasi(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnFormRobsonActionPerformed

    private void BtnAsuhanKamarBedahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAsuhanKamarBedahActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                rekammedis.RMAsuhanKeperawatanKamarBedah form = new rekammedis.RMAsuhanKeperawatanKamarBedah(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnAsuhanKamarBedahActionPerformed

    private void BtnLaporanOperasiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnLaporanOperasiActionPerformed
       if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMLaporanOperasi form = new RMLaporanOperasi(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnLaporanOperasiActionPerformed

    private void BtnPreAnastesi3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPreAnastesi3ActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMAsuhanKeperawatanKamarBedah form = new RMAsuhanKeperawatanKamarBedah(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnPreAnastesi3ActionPerformed

    private void BtnPreAnastesiRscbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPreAnastesiRscbActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMAsesmenPraAnestesiRscb form = new RMAsesmenPraAnestesiRscb(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnPreAnastesiRscbActionPerformed

    private void BtnPreInduksiRscbActionPerformed(java.awt.event.ActionEvent evt) {
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMAsesmenPraInduksiRscb form = new RMAsesmenPraInduksiRscb(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }

    private void BtnCatatanSedasiAnestesiRssmActionPerformed(java.awt.event.ActionEvent evt) {
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                String norm = Sequel.cariIsi("select no_rkm_medis from reg_periksa where no_rawat=?", TNoRw.getText());
                RMCatatanSedasiAnestesiRssm form = new RMCatatanSedasiAnestesiRssm(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), norm, TPasien.getText());
                form.tampil();
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }

    private void BtnPraBedahRscbActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPraBedahRscbActionPerformed
       if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMAsesmenPraBedahRscb form = new RMAsesmenPraBedahRscb(null, false);
                form.isCek();
                form.emptTeks();
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate(), KdDokter.getText(), NmDokter.getText(), NmOperasi.getText());
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnPraBedahRscbActionPerformed

    private void BtnEdukasiAnestesiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEdukasiAnestesiActionPerformed
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMEdukasiAnestesiRscb form = new RMEdukasiAnestesiRscb(null, false);
                form.isCek();
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.emptTeks();
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }//GEN-LAST:event_BtnEdukasiAnestesiActionPerformed

    private void BtnSIARegionalActionPerformed(java.awt.event.ActionEvent evt) {
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMSIARegionalRscb form = new RMSIARegionalRscb(null, false);
                form.isCek();
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.emptTeks();
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }

    private void BtnSIAGeneralActionPerformed(java.awt.event.ActionEvent evt) {
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMSIAGeneralRscb form = new RMSIAGeneralRscb(null, false);
                form.isCek();
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.emptTeks();
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }

    private void BtnKonversiAnestesiActionPerformed(java.awt.event.ActionEvent evt) {
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, table masih kosong...!!!!");
            TCari.requestFocus();
        } else {
            if (tbObat.getSelectedRow() != -1) {
                this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                RMKonversiAnestesiRscb form = new RMKonversiAnestesiRscb(null, false);
                form.isCek();
                form.setSize(internalFrame1.getWidth() - 20, internalFrame1.getHeight() - 20);
                form.setLocationRelativeTo(internalFrame1);
                form.setVisible(true);
                form.setNoRm(TNoRw.getText(), DTPCari2.getDate());
                form.emptTeks();
                this.setCursor(Cursor.getDefaultCursor());
            } else {
                JOptionPane.showMessageDialog(null, "Maaf, silahkan pilih data...!!!!");
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgBookingOperasi dialog = new DlgBookingOperasi(new javax.swing.JFrame(), true);
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
    private widget.Button BtnCatatanAnastesiSedasi;
    private widget.Button BtnChecklistPostOperasi;
    private widget.Button BtnChecklistPreOperasi;
    private widget.Button BtnEdit;
    private widget.Button BtnEdukasiAnestesi;
    private widget.Button BtnKonversiAnestesi;
    private widget.Button BtnSIAGeneral;
    private widget.Button BtnSIARegional;
    private widget.Button BtnFormRobson;
    private widget.Button BtnHapus;
    private widget.Button BtnKamarInap;
    private widget.Button BtnKeluar;
    private widget.Button BtnLaporanOperasi;
    private widget.Button BtnLembarObservasiAnestesi;
    private widget.Button BtnObatBhp;
    private widget.Button BtnOperasi;
    private widget.Button BtnOperator;
    private widget.Button BtnPenilaianPreInduksi;
    private widget.Button BtnPermintaanLab;
    private widget.Button BtnPraBedahRscb;
    private widget.Button BtnPreAnastesi;
    private widget.Button BtnPreAnastesi1;
    private widget.Button BtnPreAnastesi2;
    private widget.Button BtnPreAnastesi3;
    private widget.Button BtnPreAnastesiRscb;
    private widget.Button BtnPreInduksiRscb;
    private widget.Button BtnCatatanSedasiAnestesiRssm;
    private widget.Button BtnPenandaanAreaOperasi;
    private widget.Button BtnMonitoringSingleUse;
    private widget.Button BtnCatatanSedasiRscb;
    private widget.Button BtnPreOperasi;
    private widget.Button BtnPrint;
    private widget.Button BtnRiwayatPasien;
    private widget.Button BtnRuangOperasi;
    private widget.Button BtnSignInSebelumAnestesi;
    private widget.Button BtnSignOutSebelumMenutupLuka;
    private widget.Button BtnChecklistKeselamatanOK;
    private widget.Button BtnSimpan;
    private widget.Button BtnSkorAldrettePascaAnestesi;
    private widget.Button BtnSkorBromagePascaAnestesi;
    private widget.Button BtnSkorStewardPascaAnestesi;
    private widget.Button BtnTagihanOperasi;
    private widget.Button BtnTimeOutSebelumInsisi;
    private widget.Button BtnTransferAntarRuang;
    private widget.CekBox ChkAccor;
    private widget.CekBox ChkInput;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.Tanggal DTPCari3;
    private widget.Tanggal DTPCari4;
    private widget.Tanggal DTPTgl;
    private widget.ComboBox DetikMulai;
    private widget.ComboBox DetikSelesai;
    private widget.PanelBiasa FormInput;
    private widget.PanelBiasa FormMenu;
    private widget.ComboBox JamMulai;
    private widget.ComboBox JamSelesai;
    private widget.TextBox Kamar;
    private widget.TextBox KdDokter;
    private widget.TextBox KdOperasi;
    private widget.TextBox KdRuangOperasi;
    private widget.Label LCount;
    private widget.ComboBox MenitMulai;
    private widget.ComboBox MenitSelesai;
    private widget.TextBox NmDokter;
    private widget.TextBox NmOperasi;
    private widget.TextBox NmRuangOperasi;
    private widget.PanelBiasa PanelAccor;
    private javax.swing.JPanel PanelInput;
    private widget.RadioButton R1;
    private widget.RadioButton R2;
    private widget.RadioButton R3;
    private widget.RadioButton R4;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane ScrollMenu;
    private widget.ComboBox Status;
    private widget.TextBox TCari;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private javax.swing.ButtonGroup buttonGroup1;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel12;
    private widget.Label jLabel22;
    private widget.Label jLabel25;
    private widget.Label jLabel35;
    private widget.Label jLabel36;
    private widget.Label jLabel37;
    private widget.Label jLabel4;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel9;
    private javax.swing.JPanel jPanel3;
    private widget.panelisi panelCari;
    private widget.panelisi panelGlass10;
    private widget.panelisi panelGlass8;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        if (R1.isSelected() == true) {
            status = " booking_operasi.status='Menunggu' ";
        } else if (R4.isSelected() == true) {
            status = " booking_operasi.status='Proses Operasi' ";
        } else if (R2.isSelected() == true) {
            status = " booking_operasi.tanggal between '" + Valid.SetTgl(DTPCari1.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari2.getSelectedItem() + "") + "' ";
        } else if (R3.isSelected() == true) {
            status = " booking_operasi.status='Selesai' and booking_operasi.tanggal between '" + Valid.SetTgl(DTPCari3.getSelectedItem() + "") + "' and '" + Valid.SetTgl(DTPCari4.getSelectedItem() + "") + "' ";
        }
        Valid.tabelKosong(tabMode);
        try {
            ps = koneksi.prepareStatement(
                    "select booking_operasi.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,booking_operasi.tanggal,"
                    + "booking_operasi.jam_mulai,booking_operasi.jam_selesai,booking_operasi.status,booking_operasi.kd_dokter,"
                    + "dokter.nm_dokter,booking_operasi.kode_paket,paket_operasi.nm_perawatan,concat(reg_periksa.umurdaftar,' ',reg_periksa.sttsumur) as umur,"
                    + "pasien.jk,poliklinik.nm_poli,booking_operasi.kd_ruang_ok,ruang_ok.nm_ruang_ok "
                    + "from booking_operasi inner join reg_periksa on booking_operasi.no_rawat=reg_periksa.no_rawat "
                    + "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "
                    + "inner join paket_operasi on booking_operasi.kode_paket=paket_operasi.kode_paket "
                    + "inner join dokter on booking_operasi.kd_dokter=dokter.kd_dokter "
                    + "inner join poliklinik on reg_periksa.kd_poli=poliklinik.kd_poli "
                    + "inner join ruang_ok on booking_operasi.kd_ruang_ok=ruang_ok.kd_ruang_ok "
                    + "where " + status + (TCari.getText().trim().equals("") ? "" : " and (booking_operasi.no_rawat like ? or reg_periksa.no_rkm_medis like ? "
                    + "or pasien.nm_pasien like ? or booking_operasi.status like ? or dokter.nm_dokter like ? or paket_operasi.nm_perawatan like ? "
                    + "or ruang_ok.nm_ruang_ok like ?) ") + "order by booking_operasi.tanggal,booking_operasi.jam_mulai");
            try {
                if (!TCari.getText().trim().equals("")) {
                    ps.setString(1, "%" + TCari.getText().trim() + "%");
                    ps.setString(2, "%" + TCari.getText().trim() + "%");
                    ps.setString(3, "%" + TCari.getText().trim() + "%");
                    ps.setString(4, "%" + TCari.getText().trim() + "%");
                    ps.setString(5, "%" + TCari.getText().trim() + "%");
                    ps.setString(6, "%" + TCari.getText().trim() + "%");
                    ps.setString(7, "%" + TCari.getText().trim() + "%");
                }

                rs = ps.executeQuery();
                i = 1;
                while (rs.next()) {
                    order = "Ranap";
                    kamar = Sequel.cariIsi("select nm_bangsal from bangsal inner join kamar inner join kamar_inap on bangsal.kd_bangsal=kamar.kd_bangsal "
                            + " and kamar_inap.kd_kamar=kamar.kd_kamar where kamar_inap.no_rawat=? order by kamar_inap.tgl_masuk desc limit 1 ", rs.getString("no_rawat"));
                    if (kamar.equals("")) {
                        kamar = rs.getString("nm_poli");
                        order = "Ralan";
                    }

                    diagnosa = Sequel.cariIsi("select concat(diagnosa_pasien.kd_penyakit,' ',penyakit.nm_penyakit) from diagnosa_pasien inner join penyakit on diagnosa_pasien.kd_penyakit=penyakit.kd_penyakit where diagnosa_pasien.no_rawat=? limit 1", rs.getString("no_rawat"));

                    tabMode.addRow(new Object[]{
                        i + ".", rs.getString("no_rawat"), rs.getString("nm_pasien"), rs.getString("umur"),
                        rs.getString("jk"), rs.getString("tanggal"), rs.getString("jam_mulai"), rs.getString("jam_selesai"),
                        rs.getString("status"), kamar, diagnosa, rs.getString("kode_paket"), rs.getString("nm_perawatan"),
                        rs.getString("kd_dokter"), rs.getString("nm_dokter"), order, rs.getString("kd_ruang_ok"),
                        rs.getString("nm_ruang_ok")
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
        LCount.setText("" + tabMode.getRowCount());
    }

    public void emptTeks() {
        KdDokter.setText("");
        NmDokter.setText("");
        KdOperasi.setText("");
        NmOperasi.setText("");
        KdRuangOperasi.setText("");
        NmRuangOperasi.setText("");
        JamMulai.setSelectedItem("00");
        MenitMulai.setSelectedItem("00");
        DetikMulai.setSelectedItem("00");
        JamSelesai.setSelectedItem("00");
        MenitSelesai.setSelectedItem("00");
        DetikSelesai.setSelectedItem("00");
        DTPTgl.setDate(new Date());
        DTPTgl.requestFocus();
    }

    private void getData() {
        if (tbObat.getSelectedRow() != -1) {
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 2).toString());
            JamMulai.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString().substring(0, 2));
            MenitMulai.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString().substring(3, 5));
            DetikMulai.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 6).toString().substring(6, 8));
            JamSelesai.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString().substring(0, 2));
            DetikSelesai.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString().substring(3, 5));
            DetikSelesai.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 7).toString().substring(6, 8));
            Status.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(), 8).toString());
            Kamar.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 9).toString());
            KdOperasi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 11).toString());
            NmOperasi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 12).toString());
            KdDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 13).toString());
            NmDokter.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 14).toString());
            KdRuangOperasi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 16).toString());
            NmRuangOperasi.setText(tbObat.getValueAt(tbObat.getSelectedRow(), 17).toString());
            Valid.SetTgl(DTPTgl, tbObat.getValueAt(tbObat.getSelectedRow(), 5).toString());
        }
    }

    public void setNoRm(String norwt, String norm, String nama, String lokasi, String posisi) {
        TNoRw.setText(norwt);
        TPasien.setText(norm + " " + nama);
        Kamar.setText(lokasi);
        this.posisi = posisi;
        TCari.setText(norwt);
        ChkInput.setSelected(true);
        isForm();
    }

    private void isForm() {
        if (ChkInput.isSelected() == true) {
            ChkInput.setVisible(false);
            PanelInput.setPreferredSize(new Dimension(WIDTH, 126));
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
        BtnSimpan.setEnabled(akses.getbooking_operasi());
        BtnHapus.setEnabled(akses.getbooking_operasi());
        BtnPrint.setEnabled(akses.getbooking_operasi());
        BtnKamarInap.setVisible(akses.getkamar_inap());
        BtnRiwayatPasien.setVisible(akses.getresume_pasien());
        BtnTagihanOperasi.setVisible(akses.getoperasi());
        BtnEdit.setEnabled(akses.getbooking_operasi());
        BtnObatBhp.setVisible(akses.getresep_dokter());
        BtnPermintaanLab.setVisible(akses.getpermintaan_lab());
        BtnPreAnastesi.setVisible(akses.getpenilaian_pre_anestesi());
        BtnPreOperasi.setVisible(akses.getpenilaian_pre_operasi());
        BtnTransferAntarRuang.setVisible(akses.gettransfer_pasien_antar_ruang());
        BtnSignInSebelumAnestesi.setVisible(akses.getsignin_sebelum_anestesi());
        BtnTimeOutSebelumInsisi.setVisible(akses.gettimeout_sebelum_insisi());
        BtnSignOutSebelumMenutupLuka.setVisible(akses.getsignout_sebelum_menutup_luka());
        BtnChecklistPostOperasi.setVisible(akses.getchecklist_post_operasi());
        BtnChecklistPreOperasi.setVisible(akses.getchecklist_pre_operasi());
        BtnSkorAldrettePascaAnestesi.setVisible(akses.getskor_aldrette_pasca_anestesi());
        BtnSkorStewardPascaAnestesi.setVisible(akses.getskor_steward_pasca_anestesi());
        BtnSkorBromagePascaAnestesi.setVisible(akses.getskor_bromage_pasca_anestesi());
        BtnCatatanAnastesiSedasi.setVisible(akses.getcatatan_anestesi_sedasi());
    }

    private void isMenu() {
        if (ChkAccor.isSelected() == true) {
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(205, HEIGHT));
            FormMenu.setVisible(true);
            ChkAccor.setVisible(true);
        } else if (ChkAccor.isSelected() == false) {
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(15, HEIGHT));
            FormMenu.setVisible(false);
            ChkAccor.setVisible(true);
        }
    }
}
