/*
 * DlgRl37NeonatalBayi.java
 * Created on [Current Date]
 */
package laporan;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 * @author SIMRS
 */
public final class DlgRl37NeonatalBayi extends javax.swing.JDialog {

    private final DefaultTableModel tabMode;
    private Connection koneksi = koneksiDB.condb();
    private sekuel Sequel = new sekuel();
    private validasi Valid = new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i = 0;

    // Variables untuk menghitung data
    private int neonatalRujukanRumahSakit = 0, neonatalRujukanBidan = 0, neonatalRujukanPuskesmas = 0,
            neonatalRujukanFaskesLain = 0, neonatalRujukanJumlahHidup = 0, neonatalRujukanJumlahMati = 0,
            neonatalRujukanTotalMedis = 0, neonatalRujukanJumlahHidupNonMedis = 0,
            neonatalRujukanJumlahMatiNonMedis = 0, neonatalRujukanTotalNonMedis = 0,
            neonatalNonRujukanJumlahHidup = 0, neonatalNonRujukanJumlahMati = 0,
            neonatalNonRujukanTotal = 0, neonatalDirujuk = 0;

    // Total keseluruhan
    private int totalNeonatalRujukanRumahSakit = 0, totalNeonatalRujukanBidan = 0,
            totalNeonatalRujukanPuskesmas = 0, totalNeonatalRujukanFaskesLain = 0,
            totalNeonatalRujukanJumlahHidup = 0, totalNeonatalRujukanJumlahMati = 0,
            totalNeonatalRujukanTotalMedis = 0, totalNeonatalRujukanJumlahHidupNonMedis = 0,
            totalNeonatalRujukanJumlahMatiNonMedis = 0, totalNeonatalRujukanTotalNonMedis = 0,
            totalNeonatalNonRujukanJumlahHidup = 0, totalNeonatalNonRujukanJumlahMati = 0,
            totalNeonatalNonRujukanTotal = 0, totalNeonatalDirujuk = 0;

    public DlgRl37NeonatalBayi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8, 1);
        setSize(1200, 700);

        Object[] rowHeaders = {
            "No.", "Jenis Kegiatan", "Rumah Sakit", "Bidan", "Puskesmas", "Faskes Lainnya",
            "Jumlah Hidup", "Jumlah Mati", "Total Rujukan Medis", "Jumlah Hidup",
            "Jumlah Mati", "Total Non Medis", "Jumlah Hidup", "Jumlah Mati",
            "Total Non Rujukan", "Dirujuk"
        };

        tabMode = new DefaultTableModel(null, rowHeaders) {
            @Override
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false;
            }
        };

        tbBangsal.setModel(tabMode);
        tbBangsal.setPreferredScrollableViewportSize(new Dimension(500, 500));
        tbBangsal.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Set column widths
        for (i = 0; i < 16; i++) {
            TableColumn column = tbBangsal.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(40);
            } else if (i == 1) {
                column.setPreferredWidth(200);
            } else {
                column.setPreferredWidth(80);
            }
        }

        tbBangsal.setDefaultRenderer(Object.class, new WarnaTable());
        TCari.setDocument(new batasInput((byte) 100).getKata(TCari));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbBangsal = new widget.Table();
        panelGlass5 = new widget.panelisi();
        label11 = new widget.Label();
        Tgl1 = new widget.Tanggal();
        label18 = new widget.Label();
        Tgl2 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        jLabel7 = new widget.Label();
        BtnPrint = new widget.Button();
        BtnKeluar = new widget.Button();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }

            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)),
                "::[ RL 3.7 Neonatal, Bayi, dan Balita ]::",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("Tahoma", 0, 11),
                new java.awt.Color(50, 50, 50)
        ));
        internalFrame1.setName("internalFrame1");
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll");
        Scroll.setOpaque(true);

        tbBangsal.setName("tbBangsal");
        Scroll.setViewportView(tbBangsal);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass5.setName("panelGlass5");
        panelGlass5.setPreferredSize(new java.awt.Dimension(55, 55));
        panelGlass5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        label11.setText("Tanggal :");
        label11.setName("label11");
        label11.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass5.add(label11);

        Tgl1.setDisplayFormat("dd-MM-yyyy");
        Tgl1.setName("Tgl1");
        Tgl1.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass5.add(Tgl1);

        label18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label18.setText("s.d.");
        label18.setName("label18");
        label18.setPreferredSize(new java.awt.Dimension(25, 23));
        panelGlass5.add(label18);

        Tgl2.setDisplayFormat("dd-MM-yyyy");
        Tgl2.setName("Tgl2");
        Tgl2.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass5.add(Tgl2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6");
        jLabel6.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass5.add(jLabel6);

        TCari.setName("TCari");
        TCari.setPreferredSize(new java.awt.Dimension(155, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass5.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setMnemonic('2');
        BtnCari.setToolTipText("Alt+2");
        BtnCari.setName("BtnCari");
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
        panelGlass5.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setMnemonic('M');
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll");
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
        panelGlass5.add(BtnAll);

        jLabel7.setName("jLabel7");
        jLabel7.setPreferredSize(new java.awt.Dimension(30, 23));
        panelGlass5.add(jLabel7);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png")));
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Cetak");
        BtnPrint.setToolTipText("Alt+T");
        BtnPrint.setName("BtnPrint");
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
        panelGlass5.add(BtnPrint);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar");
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
        panelGlass5.add(BtnKeluar);

        internalFrame1.add(panelGlass5, java.awt.BorderLayout.PAGE_END);

        jPanel1.setName("jPanel1");
        jPanel1.setPreferredSize(new java.awt.Dimension(100, 50));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/48x48/report24.png")));
        jLabel1.setText("RL 3.7 Neonatal, Bayi, dan Balita");
        jLabel1.setIconTextGap(10);
        jLabel1.setName("jLabel1");
        jPanel1.add(jLabel1);

        internalFrame1.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>                        

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (tabMode.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
        } else if (tabMode.getRowCount() != 0) {
            Map<String, Object> param = new HashMap<>();
            param.put("namars", akses.getnamars());
            param.put("alamatrs", akses.getalamatrs());
            param.put("kotars", akses.getkabupatenrs());
            param.put("propinsirs", akses.getpropinsirs());
            param.put("kontakrs", akses.getkontakrs());
            param.put("emailrs", akses.getemailrs());
            param.put("periode", Tgl1.getSelectedItem() + " s.d. " + Tgl2.getSelectedItem());
            param.put("tanggal", Tgl2.getDate());
            param.put("logo", Sequel.cariGambar("select setting.logo from setting"));

            Sequel.queryu("delete from temporary where temp37='" + akses.getalamatip() + "'");
            for (int r = 0; r < tabMode.getRowCount(); r++) {
                if (!tbBangsal.getValueAt(r, 0).toString().contains(">>")) {
                    Sequel.menyimpan("temporary", "'" + r + "','"
                            + tabMode.getValueAt(r, 0).toString() + "','"
                            + tabMode.getValueAt(r, 1).toString().replaceAll("'", "`") + "','"
                            + tabMode.getValueAt(r, 2).toString() + "','"
                            + tabMode.getValueAt(r, 3).toString() + "','"
                            + tabMode.getValueAt(r, 4).toString() + "','"
                            + tabMode.getValueAt(r, 5).toString() + "','"
                            + tabMode.getValueAt(r, 6).toString() + "','"
                            + tabMode.getValueAt(r, 7).toString() + "','"
                            + tabMode.getValueAt(r, 8).toString() + "','"
                            + tabMode.getValueAt(r, 9).toString() + "','"
                            + tabMode.getValueAt(r, 10).toString() + "','"
                            + tabMode.getValueAt(r, 11).toString() + "','"
                            + tabMode.getValueAt(r, 12).toString() + "','"
                            + tabMode.getValueAt(r, 13).toString() + "','"
                            + tabMode.getValueAt(r, 14).toString() + "','"
                            + tabMode.getValueAt(r, 15).toString() + "','','','','','','','','','','','','','','','','','','','','" + akses.getalamatip() + "'", "Rekap RL 3.7");
                }
            }

            Valid.MyReportqry("rptRl37.jasper", "report", "::[ Formulir RL 3.7 ]::", "select * from temporary where temporary.temp37='" + akses.getalamatip() + "' order by temporary.no", param);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnPrintActionPerformed(null);
        }
    }

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            dispose();
        } else {
            Valid.pindah(evt, BtnKeluar, TCari);
        }
    }

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {
        tampil();
    }

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            tampil();
            this.setCursor(Cursor.getDefaultCursor());
        } else {
            Valid.pindah(evt, TCari, BtnPrint);
        }
    }

    private void formWindowOpened(java.awt.event.WindowEvent evt) {
        tampil();
    }

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            BtnCariActionPerformed(null);
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            BtnCari.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            BtnKeluar.requestFocus();
        }
    }

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {
        TCari.setText("");
        tampil();
    }

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
            BtnAllActionPerformed(null);
        }
    }

    private void formWindowActivated(java.awt.event.WindowEvent evt) {
        tampil();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgRl37NeonatalBayi dialog = new DlgRl37NeonatalBayi(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify                     
    private widget.Button BtnAll;
    private widget.Button BtnCari;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.Tanggal Tgl1;
    private widget.Tanggal Tgl2;
    private widget.InternalFrame internalFrame1;
    private javax.swing.JLabel jLabel1;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private javax.swing.JPanel jPanel1;
    private widget.Label label11;
    private widget.Label label18;
    private widget.panelisi panelGlass5;
    private widget.Table tbBangsal;
    // End of variables declaration                   

    private int hitungBayiByBerat(String rentangBerat, String tanggalMulai, String tanggalSelesai, boolean isRujukan, String jenisRujukan, String kategoriUsia) {
        int hasil = 0;
        try {
            String sql = "";
            if (isRujukan) {
                sql = "SELECT COUNT(DISTINCT p.no_rkm_medis) FROM pasien p "
                        + "INNER JOIN pasien_bayi pb ON p.no_rkm_medis = pb.no_rkm_medis "
                        + "INNER JOIN reg_periksa rp ON p.no_rkm_medis = rp.no_rkm_medis "
                        + "INNER JOIN rujuk_masuk rm ON rp.no_rawat = rm.no_rawat "
                        + "WHERE p.tgl_lahir BETWEEN ? AND ? ";

                if (jenisRujukan.equals("rs")) {
                    sql += "AND (rm.perujuk LIKE '%rs%' OR rm.perujuk LIKE '%rumah sakit%') ";
                } else if (jenisRujukan.equals("bidan")) {
                    sql += "AND rm.perujuk LIKE '%bidan%' ";
                } else if (jenisRujukan.equals("puskesmas")) {
                    sql += "AND (rm.perujuk LIKE '%pkm%' OR rm.perujuk LIKE '%puskesmas%') ";
                } else if (jenisRujukan.equals("faskes_lain")) {
                    sql += "AND (rm.perujuk LIKE '%klinik%' OR rm.perujuk LIKE '%dr%') ";
                }
            } else {
                sql = "SELECT COUNT(DISTINCT p.no_rkm_medis) FROM pasien p "
                        + "INNER JOIN pasien_bayi pb ON p.no_rkm_medis = pb.no_rkm_medis "
                        + "INNER JOIN reg_periksa rp ON p.no_rkm_medis = rp.no_rkm_medis "
                        + "WHERE p.tgl_lahir BETWEEN ? AND ? "
                        + "AND rp.no_rawat NOT IN (SELECT no_rawat FROM rujuk_masuk) ";
            }

            // Tambahkan kondisi usia kehamilan
            if (kategoriUsia.equals("prematur")) {
                sql += "AND CAST(pb.umurkehamilan AS UNSIGNED) < 37 ";
            } else if (kategoriUsia.equals("non_prematur")) {
                sql += "AND CAST(pb.umurkehamilan AS UNSIGNED) >= 37 AND CAST(pb.umurkehamilan AS UNSIGNED) <= 41 ";
            }

            // Tambahkan kondisi berat badan
            if (rentangBerat.equals("1500-2500")) {
                sql += "AND CAST(pb.berat_badan AS UNSIGNED) >= 1500 AND CAST(pb.berat_badan AS UNSIGNED) < 2500 ";
            } else if (rentangBerat.equals("1000-1500")) {
                sql += "AND CAST(pb.berat_badan AS UNSIGNED) >= 1000 AND CAST(pb.berat_badan AS UNSIGNED) < 1500 ";
            } else if (rentangBerat.equals("<1000")) {
                sql += "AND CAST(pb.berat_badan AS UNSIGNED) < 1000 ";
            } else if (rentangBerat.equals("2500-4000")) {
                sql += "AND CAST(pb.berat_badan AS UNSIGNED) >= 2500 AND CAST(pb.berat_badan AS UNSIGNED) < 4000 ";
            } else if (rentangBerat.equals(">=4000")) {
                sql += "AND CAST(pb.berat_badan AS UNSIGNED) >= 4000 ";
            }

            PreparedStatement psQuery = koneksi.prepareStatement(sql);
            psQuery.setString(1, tanggalMulai);
            psQuery.setString(2, tanggalSelesai);

            ResultSet rsQuery = psQuery.executeQuery();
            if (rsQuery.next()) {
                hasil = rsQuery.getInt(1);
            }
            rsQuery.close();
            psQuery.close();
        } catch (Exception e) {
            System.out.println("Error hitungBayiByBerat: " + e);
        }
        return hasil;
    }

    private int hitungBayiMeninggal(String rentangBerat, String tanggalMulai, String tanggalSelesai, boolean isRujukan, String jenisRujukan, String kategoriUsia) {
        int hasil = 0;
        try {
            String sql = "";
            if (isRujukan) {
                sql = "SELECT COUNT(DISTINCT p.no_rkm_medis) FROM pasien p "
                        + "INNER JOIN pasien_bayi pb ON p.no_rkm_medis = pb.no_rkm_medis "
                        + "INNER JOIN reg_periksa rp ON p.no_rkm_medis = rp.no_rkm_medis "
                        + "INNER JOIN rujuk_masuk rm ON rp.no_rawat = rm.no_rawat "
                        + "INNER JOIN pasien_mati pm ON p.no_rkm_medis = pm.no_rkm_medis "
                        + "WHERE p.tgl_lahir BETWEEN ? AND ? "
                        + "AND pm.tanggal BETWEEN ? AND ? ";

                if (jenisRujukan.equals("rs")) {
                    sql += "AND (rm.perujuk LIKE '%rs%' OR rm.perujuk LIKE '%rumah sakit%') ";
                } else if (jenisRujukan.equals("bidan")) {
                    sql += "AND rm.perujuk LIKE '%bidan%' ";
                } else if (jenisRujukan.equals("puskesmas")) {
                    sql += "AND (rm.perujuk LIKE '%pkm%' OR rm.perujuk LIKE '%puskesmas%') ";
                } else if (jenisRujukan.equals("faskes_lain")) {
                    sql += "AND (rm.perujuk LIKE '%klinik%' OR rm.perujuk LIKE '%dr%') ";
                }
            } else {
                sql = "SELECT COUNT(DISTINCT p.no_rkm_medis) FROM pasien p "
                        + "INNER JOIN pasien_bayi pb ON p.no_rkm_medis = pb.no_rkm_medis "
                        + "INNER JOIN reg_periksa rp ON p.no_rkm_medis = rp.no_rkm_medis "
                        + "INNER JOIN pasien_mati pm ON p.no_rkm_medis = pm.no_rkm_medis "
                        + "WHERE p.tgl_lahir BETWEEN ? AND ? "
                        + "AND pm.tanggal BETWEEN ? AND ? "
                        + "AND rp.no_rawat NOT IN (SELECT no_rawat FROM rujuk_masuk) ";
            }

            // Tambahkan kondisi usia kehamilan
            if (kategoriUsia.equals("prematur")) {
                sql += "AND CAST(pb.umurkehamilan AS UNSIGNED) < 37 ";
            } else if (kategoriUsia.equals("non_prematur")) {
                sql += "AND CAST(pb.umurkehamilan AS UNSIGNED) >= 37 AND CAST(pb.umurkehamilan AS UNSIGNED) <= 41 ";
            }

            // Tambahkan kondisi berat badan
            if (rentangBerat.equals("1500-2500")) {
                sql += "AND CAST(pb.berat_badan AS UNSIGNED) >= 1500 AND CAST(pb.berat_badan AS UNSIGNED) < 2500 ";
            } else if (rentangBerat.equals("1000-1500")) {
                sql += "AND CAST(pb.berat_badan AS UNSIGNED) >= 1000 AND CAST(pb.berat_badan AS UNSIGNED) < 1500 ";
            } else if (rentangBerat.equals("<1000")) {
                sql += "AND CAST(pb.berat_badan AS UNSIGNED) < 1000 ";
            } else if (rentangBerat.equals("2500-4000")) {
                sql += "AND CAST(pb.berat_badan AS UNSIGNED) >= 2500 AND CAST(pb.berat_badan AS UNSIGNED) < 4000 ";
            } else if (rentangBerat.equals(">=4000")) {
                sql += "AND CAST(pb.berat_badan AS UNSIGNED) >= 4000 ";
            }

            PreparedStatement psQuery = koneksi.prepareStatement(sql);
            psQuery.setString(1, tanggalMulai);
            psQuery.setString(2, tanggalSelesai);
            psQuery.setString(3, tanggalMulai);
            psQuery.setString(4, tanggalSelesai);

            ResultSet rsQuery = psQuery.executeQuery();
            if (rsQuery.next()) {
                hasil = rsQuery.getInt(1);
            }
            rsQuery.close();
            psQuery.close();
        } catch (Exception e) {
            System.out.println("Error hitungBayiMeninggal: " + e);
        }
        return hasil;
    }

    private int hitungBayiDirujuk(String rentangBerat, String tanggalMulai, String tanggalSelesai, String kategoriUsia) {
        int hasil = 0;
        try {
            String sql = "SELECT COUNT(DISTINCT p.no_rkm_medis) FROM pasien p "
                    + "INNER JOIN pasien_bayi pb ON p.no_rkm_medis = pb.no_rkm_medis "
                    + "INNER JOIN reg_periksa rp ON p.no_rkm_medis = rp.no_rkm_medis "
                    + "INNER JOIN rujuk r ON rp.no_rawat = r.no_rawat "
                    + "WHERE p.tgl_lahir BETWEEN ? AND ? "
                    + "AND r.tgl_rujuk BETWEEN ? AND ? ";

            // Tambahkan kondisi usia kehamilan
            if (kategoriUsia.equals("prematur")) {
                sql += "AND CAST(pb.umurkehamilan AS UNSIGNED) < 37 ";
            } else if (kategoriUsia.equals("non_prematur")) {
                sql += "AND CAST(pb.umurkehamilan AS UNSIGNED) >= 37 AND CAST(pb.umurkehamilan AS UNSIGNED) <= 41 ";
            }

            // Tambahkan kondisi berat badan
            if (rentangBerat.equals("1500-2500")) {
                sql += "AND CAST(pb.berat_badan AS UNSIGNED) >= 1500 AND CAST(pb.berat_badan AS UNSIGNED) < 2500 ";
            } else if (rentangBerat.equals("1000-1500")) {
                sql += "AND CAST(pb.berat_badan AS UNSIGNED) >= 1000 AND CAST(pb.berat_badan AS UNSIGNED) < 1500 ";
            } else if (rentangBerat.equals("<1000")) {
                sql += "AND CAST(pb.berat_badan AS UNSIGNED) < 1000 ";
            } else if (rentangBerat.equals("2500-4000")) {
                sql += "AND CAST(pb.berat_badan AS UNSIGNED) >= 2500 AND CAST(pb.berat_badan AS UNSIGNED) < 4000 ";
            } else if (rentangBerat.equals(">=4000")) {
                sql += "AND CAST(pb.berat_badan AS UNSIGNED) >= 4000 ";
            }

            PreparedStatement psQuery = koneksi.prepareStatement(sql);
            psQuery.setString(1, tanggalMulai);
            psQuery.setString(2, tanggalSelesai);
            psQuery.setString(3, tanggalMulai);
            psQuery.setString(4, tanggalSelesai);

            ResultSet rsQuery = psQuery.executeQuery();
            if (rsQuery.next()) {
                hasil = rsQuery.getInt(1);
            }
            rsQuery.close();
            psQuery.close();
        } catch (Exception e) {
            System.out.println("Error hitungBayiDirujuk: " + e);
        }
        return hasil;
    }

    public void tampil() {
        try {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Valid.tabelKosong(tabMode);

            String tanggalMulai = Valid.SetTgl(Tgl1.getSelectedItem() + "");
            String tanggalSelesai = Valid.SetTgl(Tgl2.getSelectedItem() + "");

            // Reset total
            totalNeonatalRujukanRumahSakit = 0;
            totalNeonatalRujukanBidan = 0;
            totalNeonatalRujukanPuskesmas = 0;
            totalNeonatalRujukanFaskesLain = 0;
            totalNeonatalRujukanJumlahHidup = 0;
            totalNeonatalRujukanJumlahMati = 0;
            totalNeonatalRujukanTotalMedis = 0;
            totalNeonatalRujukanJumlahHidupNonMedis = 0;
            totalNeonatalRujukanJumlahMatiNonMedis = 0;
            totalNeonatalRujukanTotalNonMedis = 0;
            totalNeonatalNonRujukanJumlahHidup = 0;
            totalNeonatalNonRujukanJumlahMati = 0;
            totalNeonatalNonRujukanTotal = 0;
            totalNeonatalDirujuk = 0;

            i = 1;

            // A. NEONATAL
            tabMode.addRow(new Object[]{"A", "NEONATAL", "", "", "", "", "", "", "", "", "", "", "", "", "", ""});

            // 1. Bayi Lahir Hidup
            tabMode.addRow(new Object[]{"1", "Bayi Lahir Hidup", "", "", "", "", "", "", "", "", "", "", "", "", "", ""});

            // 1.1 Lahir Prematur (< 37 minggu)
            tabMode.addRow(new Object[]{"1.1", "Lahir Prematur (< 37 minggu)", "", "", "", "", "", "", "", "", "", "", "", "", "", ""});

            // 1.1.1 1500 - <2500 gram (BBLR)
            neonatalRujukanRumahSakit = hitungBayiByBerat("1500-2500", tanggalMulai, tanggalSelesai, true, "rs", "prematur");
            neonatalRujukanBidan = hitungBayiByBerat("1500-2500", tanggalMulai, tanggalSelesai, true, "bidan", "prematur");
            neonatalRujukanPuskesmas = hitungBayiByBerat("1500-2500", tanggalMulai, tanggalSelesai, true, "puskesmas", "prematur");
            neonatalRujukanFaskesLain = hitungBayiByBerat("1500-2500", tanggalMulai, tanggalSelesai, true, "faskes_lain", "prematur");
            neonatalRujukanJumlahHidup = neonatalRujukanRumahSakit + neonatalRujukanBidan + neonatalRujukanPuskesmas + neonatalRujukanFaskesLain;
            neonatalRujukanJumlahMati = hitungBayiMeninggal("1500-2500", tanggalMulai, tanggalSelesai, true, "", "prematur");
            neonatalRujukanTotalMedis = neonatalRujukanJumlahHidup + neonatalRujukanJumlahMati;
            neonatalRujukanJumlahHidupNonMedis = 0; // Untuk non medis, bisa disesuaikan logikanya
            neonatalRujukanJumlahMatiNonMedis = 0;
            neonatalRujukanTotalNonMedis = neonatalRujukanJumlahHidupNonMedis + neonatalRujukanJumlahMatiNonMedis;
            neonatalNonRujukanJumlahHidup = hitungBayiByBerat("1500-2500", tanggalMulai, tanggalSelesai, false, "", "prematur");
            neonatalNonRujukanJumlahMati = hitungBayiMeninggal("1500-2500", tanggalMulai, tanggalSelesai, false, "", "prematur");
            neonatalNonRujukanTotal = neonatalNonRujukanJumlahHidup + neonatalNonRujukanJumlahMati;
            neonatalDirujuk = hitungBayiDirujuk("1500-2500", tanggalMulai, tanggalSelesai, "prematur");

            tabMode.addRow(new Object[]{"1.1.1", "1500 - <2500 gram (BBLR)",
                neonatalRujukanRumahSakit, neonatalRujukanBidan, neonatalRujukanPuskesmas,
                neonatalRujukanFaskesLain, neonatalRujukanJumlahHidup, neonatalRujukanJumlahMati,
                neonatalRujukanTotalMedis, neonatalRujukanJumlahHidupNonMedis,
                neonatalRujukanJumlahMatiNonMedis, neonatalRujukanTotalNonMedis,
                neonatalNonRujukanJumlahHidup, neonatalNonRujukanJumlahMati,
                neonatalNonRujukanTotal, neonatalDirujuk});

            // Update total
            totalNeonatalRujukanRumahSakit += neonatalRujukanRumahSakit;
            totalNeonatalRujukanBidan += neonatalRujukanBidan;
            totalNeonatalRujukanPuskesmas += neonatalRujukanPuskesmas;
            totalNeonatalRujukanFaskesLain += neonatalRujukanFaskesLain;
            totalNeonatalRujukanJumlahHidup += neonatalRujukanJumlahHidup;
            totalNeonatalRujukanJumlahMati += neonatalRujukanJumlahMati;
            totalNeonatalRujukanTotalMedis += neonatalRujukanTotalMedis;
            totalNeonatalRujukanJumlahHidupNonMedis += neonatalRujukanJumlahHidupNonMedis;
            totalNeonatalRujukanJumlahMatiNonMedis += neonatalRujukanJumlahMatiNonMedis;
            totalNeonatalRujukanTotalNonMedis += neonatalRujukanTotalNonMedis;
            totalNeonatalNonRujukanJumlahHidup += neonatalNonRujukanJumlahHidup;
            totalNeonatalNonRujukanJumlahMati += neonatalNonRujukanJumlahMati;
            totalNeonatalNonRujukanTotal += neonatalNonRujukanTotal;
            totalNeonatalDirujuk += neonatalDirujuk;

            // 1.1.2 1000 - <1500 gram (BBLSR)
            neonatalRujukanRumahSakit = hitungBayiByBerat("1000-1500", tanggalMulai, tanggalSelesai, true, "rs", "prematur");
            neonatalRujukanBidan = hitungBayiByBerat("1000-1500", tanggalMulai, tanggalSelesai, true, "bidan", "prematur");
            neonatalRujukanPuskesmas = hitungBayiByBerat("1000-1500", tanggalMulai, tanggalSelesai, true, "puskesmas", "prematur");
            neonatalRujukanFaskesLain = hitungBayiByBerat("1000-1500", tanggalMulai, tanggalSelesai, true, "faskes_lain", "prematur");
            neonatalRujukanJumlahHidup = neonatalRujukanRumahSakit + neonatalRujukanBidan + neonatalRujukanPuskesmas + neonatalRujukanFaskesLain;
            neonatalRujukanJumlahMati = hitungBayiMeninggal("1000-1500", tanggalMulai, tanggalSelesai, true, "", "prematur");
            neonatalRujukanTotalMedis = neonatalRujukanJumlahHidup + neonatalRujukanJumlahMati;
            neonatalRujukanJumlahHidupNonMedis = 0;
            neonatalRujukanJumlahMatiNonMedis = 0;
            neonatalRujukanTotalNonMedis = neonatalRujukanJumlahHidupNonMedis + neonatalRujukanJumlahMatiNonMedis;
            neonatalNonRujukanJumlahHidup = hitungBayiByBerat("1000-1500", tanggalMulai, tanggalSelesai, false, "", "prematur");
            neonatalNonRujukanJumlahMati = hitungBayiMeninggal("1000-1500", tanggalMulai, tanggalSelesai, false, "", "prematur");
            neonatalNonRujukanTotal = neonatalNonRujukanJumlahHidup + neonatalNonRujukanJumlahMati;
            neonatalDirujuk = hitungBayiDirujuk("1000-1500", tanggalMulai, tanggalSelesai, "prematur");

            tabMode.addRow(new Object[]{"1.1.2", "1000 - <1500 gram (BBLSR)",
                neonatalRujukanRumahSakit, neonatalRujukanBidan, neonatalRujukanPuskesmas,
                neonatalRujukanFaskesLain, neonatalRujukanJumlahHidup, neonatalRujukanJumlahMati,
                neonatalRujukanTotalMedis, neonatalRujukanJumlahHidupNonMedis,
                neonatalRujukanJumlahMatiNonMedis, neonatalRujukanTotalNonMedis,
                neonatalNonRujukanJumlahHidup, neonatalNonRujukanJumlahMati,
                neonatalNonRujukanTotal, neonatalDirujuk});

            // Update total
            totalNeonatalRujukanRumahSakit += neonatalRujukanRumahSakit;
            totalNeonatalRujukanBidan += neonatalRujukanBidan;
            totalNeonatalRujukanPuskesmas += neonatalRujukanPuskesmas;
            totalNeonatalRujukanFaskesLain += neonatalRujukanFaskesLain;
            totalNeonatalRujukanJumlahHidup += neonatalRujukanJumlahHidup;
            totalNeonatalRujukanJumlahMati += neonatalRujukanJumlahMati;
            totalNeonatalRujukanTotalMedis += neonatalRujukanTotalMedis;
            totalNeonatalRujukanJumlahHidupNonMedis += neonatalRujukanJumlahHidupNonMedis;
            totalNeonatalRujukanJumlahMatiNonMedis += neonatalRujukanJumlahMatiNonMedis;
            totalNeonatalRujukanTotalNonMedis += neonatalRujukanTotalNonMedis;
            totalNeonatalNonRujukanJumlahHidup += neonatalNonRujukanJumlahHidup;
            totalNeonatalNonRujukanJumlahMati += neonatalNonRujukanJumlahMati;
            totalNeonatalNonRujukanTotal += neonatalNonRujukanTotal;
            totalNeonatalDirujuk += neonatalDirujuk;

            // 1.1.3 <1000 gram (BBLER)
            neonatalRujukanRumahSakit = hitungBayiByBerat("<1000", tanggalMulai, tanggalSelesai, true, "rs", "prematur");
            neonatalRujukanBidan = hitungBayiByBerat("<1000", tanggalMulai, tanggalSelesai, true, "bidan", "prematur");
            neonatalRujukanPuskesmas = hitungBayiByBerat("<1000", tanggalMulai, tanggalSelesai, true, "puskesmas", "prematur");
            neonatalRujukanFaskesLain = hitungBayiByBerat("<1000", tanggalMulai, tanggalSelesai, true, "faskes_lain", "prematur");
            neonatalRujukanJumlahHidup = neonatalRujukanRumahSakit + neonatalRujukanBidan + neonatalRujukanPuskesmas + neonatalRujukanFaskesLain;
            neonatalRujukanJumlahMati = hitungBayiMeninggal("<1000", tanggalMulai, tanggalSelesai, true, "", "prematur");
            neonatalRujukanTotalMedis = neonatalRujukanJumlahHidup + neonatalRujukanJumlahMati;
            neonatalRujukanJumlahHidupNonMedis = 0;
            neonatalRujukanJumlahMatiNonMedis = 0;
            neonatalRujukanTotalNonMedis = neonatalRujukanJumlahHidupNonMedis + neonatalRujukanJumlahMatiNonMedis;
            neonatalNonRujukanJumlahHidup = hitungBayiByBerat("<1000", tanggalMulai, tanggalSelesai, false, "", "prematur");
            neonatalNonRujukanJumlahMati = hitungBayiMeninggal("<1000", tanggalMulai, tanggalSelesai, false, "", "prematur");
            neonatalNonRujukanTotal = neonatalNonRujukanJumlahHidup + neonatalNonRujukanJumlahMati;
            neonatalDirujuk = hitungBayiDirujuk("<1000", tanggalMulai, tanggalSelesai, "prematur");

            tabMode.addRow(new Object[]{"1.1.3", "<1000 gram (BBLER)",
                neonatalRujukanRumahSakit, neonatalRujukanBidan, neonatalRujukanPuskesmas,
                neonatalRujukanFaskesLain, neonatalRujukanJumlahHidup, neonatalRujukanJumlahMati,
                neonatalRujukanTotalMedis, neonatalRujukanJumlahHidupNonMedis,
                neonatalRujukanJumlahMatiNonMedis, neonatalRujukanTotalNonMedis,
                neonatalNonRujukanJumlahHidup, neonatalNonRujukanJumlahMati,
                neonatalNonRujukanTotal, neonatalDirujuk});

            // Update total
            totalNeonatalRujukanRumahSakit += neonatalRujukanRumahSakit;
            totalNeonatalRujukanBidan += neonatalRujukanBidan;
            totalNeonatalRujukanPuskesmas += neonatalRujukanPuskesmas;
            totalNeonatalRujukanFaskesLain += neonatalRujukanFaskesLain;
            totalNeonatalRujukanJumlahHidup += neonatalRujukanJumlahHidup;
            totalNeonatalRujukanJumlahMati += neonatalRujukanJumlahMati;
            totalNeonatalRujukanTotalMedis += neonatalRujukanTotalMedis;
            totalNeonatalRujukanJumlahHidupNonMedis += neonatalRujukanJumlahHidupNonMedis;
            totalNeonatalRujukanJumlahMatiNonMedis += neonatalRujukanJumlahMatiNonMedis;
            totalNeonatalRujukanTotalNonMedis += neonatalRujukanTotalNonMedis;
            totalNeonatalNonRujukanJumlahHidup += neonatalNonRujukanJumlahHidup;
            totalNeonatalNonRujukanJumlahMati += neonatalNonRujukanJumlahMati;
            totalNeonatalNonRujukanTotal += neonatalNonRujukanTotal;
            totalNeonatalDirujuk += neonatalDirujuk;

            // 1.2 Lahir Non Prematur (≥ 37 - 41 minggu)
            tabMode.addRow(new Object[]{"1.2", "Lahir Non Prematur (≥ 37 - 41 minggu)", "", "", "", "", "", "", "", "", "", "", "", "", "", ""});

            // 1.2.1 1500 - <2500 gram (BBLR)
            neonatalRujukanRumahSakit = hitungBayiByBerat("1500-2500", tanggalMulai, tanggalSelesai, true, "rs", "non_prematur");
            neonatalRujukanBidan = hitungBayiByBerat("1500-2500", tanggalMulai, tanggalSelesai, true, "bidan", "non_prematur");
            neonatalRujukanPuskesmas = hitungBayiByBerat("1500-2500", tanggalMulai, tanggalSelesai, true, "puskesmas", "non_prematur");
            neonatalRujukanFaskesLain = hitungBayiByBerat("1500-2500", tanggalMulai, tanggalSelesai, true, "faskes_lain", "non_prematur");
            neonatalRujukanJumlahHidup = neonatalRujukanRumahSakit + neonatalRujukanBidan + neonatalRujukanPuskesmas + neonatalRujukanFaskesLain;
            neonatalRujukanJumlahMati = hitungBayiMeninggal("1500-2500", tanggalMulai, tanggalSelesai, true, "", "non_prematur");
            neonatalRujukanTotalMedis = neonatalRujukanJumlahHidup + neonatalRujukanJumlahMati;
            neonatalRujukanJumlahHidupNonMedis = 0;
            neonatalRujukanJumlahMatiNonMedis = 0;
            neonatalRujukanTotalNonMedis = neonatalRujukanJumlahHidupNonMedis + neonatalRujukanJumlahMatiNonMedis;
            neonatalNonRujukanJumlahHidup = hitungBayiByBerat("1500-2500", tanggalMulai, tanggalSelesai, false, "", "non_prematur");
            neonatalNonRujukanJumlahMati = hitungBayiMeninggal("1500-2500", tanggalMulai, tanggalSelesai, false, "", "non_prematur");
            neonatalNonRujukanTotal = neonatalNonRujukanJumlahHidup + neonatalNonRujukanJumlahMati;
            neonatalDirujuk = hitungBayiDirujuk("1500-2500", tanggalMulai, tanggalSelesai, "non_prematur");

            tabMode.addRow(new Object[]{"1.2.1", "1500 - <2500 gram (BBLR)",
                neonatalRujukanRumahSakit, neonatalRujukanBidan, neonatalRujukanPuskesmas,
                neonatalRujukanFaskesLain, neonatalRujukanJumlahHidup, neonatalRujukanJumlahMati,
                neonatalRujukanTotalMedis, neonatalRujukanJumlahHidupNonMedis,
                neonatalRujukanJumlahMatiNonMedis, neonatalRujukanTotalNonMedis,
                neonatalNonRujukanJumlahHidup, neonatalNonRujukanJumlahMati,
                neonatalNonRujukanTotal, neonatalDirujuk});

            // 1.2.2 2500 - <4000 gram (BBLN)
            neonatalRujukanRumahSakit = hitungBayiByBerat("2500-4000", tanggalMulai, tanggalSelesai, true, "rs", "non_prematur");
            neonatalRujukanBidan = hitungBayiByBerat("2500-4000", tanggalMulai, tanggalSelesai, true, "bidan", "non_prematur");
            neonatalRujukanPuskesmas = hitungBayiByBerat("2500-4000", tanggalMulai, tanggalSelesai, true, "puskesmas", "non_prematur");
            neonatalRujukanFaskesLain = hitungBayiByBerat("2500-4000", tanggalMulai, tanggalSelesai, true, "faskes_lain", "non_prematur");
            neonatalRujukanJumlahHidup = neonatalRujukanRumahSakit + neonatalRujukanBidan + neonatalRujukanPuskesmas + neonatalRujukanFaskesLain;
            neonatalRujukanJumlahMati = hitungBayiMeninggal("2500-4000", tanggalMulai, tanggalSelesai, true, "", "non_prematur");
            neonatalRujukanTotalMedis = neonatalRujukanJumlahHidup + neonatalRujukanJumlahMati;
            neonatalRujukanJumlahHidupNonMedis = 0;
            neonatalRujukanJumlahMatiNonMedis = 0;
            neonatalRujukanTotalNonMedis = neonatalRujukanJumlahHidupNonMedis + neonatalRujukanJumlahMatiNonMedis;
            neonatalNonRujukanJumlahHidup = hitungBayiByBerat("2500-4000", tanggalMulai, tanggalSelesai, false, "", "non_prematur");
            neonatalNonRujukanJumlahMati = hitungBayiMeninggal("2500-4000", tanggalMulai, tanggalSelesai, false, "", "non_prematur");
            neonatalNonRujukanTotal = neonatalNonRujukanJumlahHidup + neonatalNonRujukanJumlahMati;
            neonatalDirujuk = hitungBayiDirujuk("2500-4000", tanggalMulai, tanggalSelesai, "non_prematur");

            tabMode.addRow(new Object[]{"1.2.2", "2500 - <4000 gram (BBLN)",
                neonatalRujukanRumahSakit, neonatalRujukanBidan, neonatalRujukanPuskesmas,
                neonatalRujukanFaskesLain, neonatalRujukanJumlahHidup, neonatalRujukanJumlahMati,
                neonatalRujukanTotalMedis, neonatalRujukanJumlahHidupNonMedis,
                neonatalRujukanJumlahMatiNonMedis, neonatalRujukanTotalNonMedis,
                neonatalNonRujukanJumlahHidup, neonatalNonRujukanJumlahMati,
                neonatalNonRujukanTotal, neonatalDirujuk});

            // 1.2.3 ≥4000 gram (BBLL)
            neonatalRujukanRumahSakit = hitungBayiByBerat(">=4000", tanggalMulai, tanggalSelesai, true, "rs", "non_prematur");
            neonatalRujukanBidan = hitungBayiByBerat(">=4000", tanggalMulai, tanggalSelesai, true, "bidan", "non_prematur");
            neonatalRujukanPuskesmas = hitungBayiByBerat(">=4000", tanggalMulai, tanggalSelesai, true, "puskesmas", "non_prematur");
            neonatalRujukanFaskesLain = hitungBayiByBerat(">=4000", tanggalMulai, tanggalSelesai, true, "faskes_lain", "non_prematur");
            neonatalRujukanJumlahHidup = neonatalRujukanRumahSakit + neonatalRujukanBidan + neonatalRujukanPuskesmas + neonatalRujukanFaskesLain;
            neonatalRujukanJumlahMati = hitungBayiMeninggal(">=4000", tanggalMulai, tanggalSelesai, true, "", "non_prematur");
            neonatalRujukanTotalMedis = neonatalRujukanJumlahHidup + neonatalRujukanJumlahMati;
            neonatalRujukanJumlahHidupNonMedis = 0;
            neonatalRujukanJumlahMatiNonMedis = 0;
            neonatalRujukanTotalNonMedis = neonatalRujukanJumlahHidupNonMedis + neonatalRujukanJumlahMatiNonMedis;
            neonatalNonRujukanJumlahHidup = hitungBayiByBerat(">=4000", tanggalMulai, tanggalSelesai, false, "", "non_prematur");
            neonatalNonRujukanJumlahMati = hitungBayiMeninggal(">=4000", tanggalMulai, tanggalSelesai, false, "", "non_prematur");
            neonatalNonRujukanTotal = neonatalNonRujukanJumlahHidup + neonatalNonRujukanJumlahMati;
            neonatalDirujuk = hitungBayiDirujuk(">=4000", tanggalMulai, tanggalSelesai, "non_prematur");

            tabMode.addRow(new Object[]{"1.2.3", "≥4000 gram (BBLL)",
                neonatalRujukanRumahSakit, neonatalRujukanBidan, neonatalRujukanPuskesmas,
                neonatalRujukanFaskesLain, neonatalRujukanJumlahHidup, neonatalRujukanJumlahMati,
                neonatalRujukanTotalMedis, neonatalRujukanJumlahHidupNonMedis,
                neonatalRujukanJumlahMatiNonMedis, neonatalRujukanTotalNonMedis,
                neonatalNonRujukanJumlahHidup, neonatalNonRujukanJumlahMati,
                neonatalNonRujukanTotal, neonatalDirujuk});

            // Update total akhir untuk kategori 1.2.3
            totalNeonatalRujukanRumahSakit += neonatalRujukanRumahSakit;
            totalNeonatalRujukanBidan += neonatalRujukanBidan;
            totalNeonatalRujukanPuskesmas += neonatalRujukanPuskesmas;
            totalNeonatalRujukanFaskesLain += neonatalRujukanFaskesLain;
            totalNeonatalRujukanJumlahHidup += neonatalRujukanJumlahHidup;
            totalNeonatalRujukanJumlahMati += neonatalRujukanJumlahMati;
            totalNeonatalRujukanTotalMedis += neonatalRujukanTotalMedis;
            totalNeonatalRujukanJumlahHidupNonMedis += neonatalRujukanJumlahHidupNonMedis;
            totalNeonatalRujukanJumlahMatiNonMedis += neonatalRujukanJumlahMatiNonMedis;
            totalNeonatalRujukanTotalNonMedis += neonatalRujukanTotalNonMedis;
            totalNeonatalNonRujukanJumlahHidup += neonatalNonRujukanJumlahHidup;
            totalNeonatalNonRujukanJumlahMati += neonatalNonRujukanJumlahMati;
            totalNeonatalNonRujukanTotal += neonatalNonRujukanTotal;
            totalNeonatalDirujuk += neonatalDirujuk;

            // Tambahkan baris TOTAL NEONATAL jika ada data
            if (totalNeonatalRujukanRumahSakit > 0 || totalNeonatalRujukanBidan > 0
                    || totalNeonatalRujukanPuskesmas > 0 || totalNeonatalRujukanFaskesLain > 0
                    || totalNeonatalNonRujukanJumlahHidup > 0 || totalNeonatalDirujuk > 0) {
                tabMode.addRow(new Object[]{"", "TOTAL NEONATAL",
                    totalNeonatalRujukanRumahSakit, totalNeonatalRujukanBidan, totalNeonatalRujukanPuskesmas,
                    totalNeonatalRujukanFaskesLain, totalNeonatalRujukanJumlahHidup, totalNeonatalRujukanJumlahMati,
                    totalNeonatalRujukanTotalMedis, totalNeonatalRujukanJumlahHidupNonMedis,
                    totalNeonatalRujukanJumlahMatiNonMedis, totalNeonatalRujukanTotalNonMedis,
                    totalNeonatalNonRujukanJumlahHidup, totalNeonatalNonRujukanJumlahMati,
                    totalNeonatalNonRujukanTotal, totalNeonatalDirujuk});
            }

            this.setCursor(Cursor.getDefaultCursor());
        } catch (Exception e) {
            System.out.println("Error tampil: " + e);
            e.printStackTrace();
        }
    }
}
