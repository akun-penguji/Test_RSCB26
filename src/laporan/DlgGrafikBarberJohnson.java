/*
 * DlgGrafikBarberJohnson.java
 *
 * Rekap indikator efisiensi rawat inap (BOR, AvLOS, TOI, BTO) per periode
 * beserta Grafik Barber Johnson-nya, dalam satu form.
 *
 * Sebelumnya petugas RM harus buka DlgHitungBOR dan DlgHitungALOS terpisah,
 * lalu menghitung TOI/BTO manual. Form ini menghitung keempat indikator
 * sekaligus, per bulan, plus baris TOTAL periode.
 *
 * Rumus (Barber Johnson):
 *   HP  = hari perawatan (sensus, hari keluar tidak dihitung; masuk-keluar
 *         hari sama dihitung 1 hari)
 *   D   = jumlah pasien keluar (hidup + mati), tidak termasuk pindah kamar
 *   TT  = jumlah tempat tidur tersedia
 *   t   = jumlah hari dalam periode
 *   BOR  = HP / (TT x t) x 100%
 *   AvLOS= HP / D
 *   TOI  = ((TT x t) - HP) / D
 *   BTO  = D / TT
 */

package laporan;

import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Rectangle2D;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;
import simrskhanza.DlgCariBangsal;

/**
 *
 * @author Salim Mulyana
 */
public final class DlgGrafikBarberJohnson extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private final Connection koneksi=koneksiDB.condb();
    private final sekuel Sequel=new sekuel();
    private final validasi Valid=new validasi();
    private int i=0;
    private String filter="";
    private ChartPanel panelChart=null;
    private final List<Indikator> hasil=new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static final String[] NAMABULAN={"Januari","Februari","Maret","April","Mei","Juni",
                                             "Juli","Agustus","September","Oktober","November","Desember"};

    /** Satu baris indikator untuk satu periode. */
    private static class Indikator {
        String nama;
        double hp;
        int keluar,tt,hari;
        double bor,alos,toi,bto;
        boolean total;
    }

    /** Creates new form DlgGrafikBarberJohnson
     * @param parent
     * @param modal */
    public DlgGrafikBarberJohnson(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(8,1);
        setSize(885,674);

        tabMode=new DefaultTableModel(null,new String[]{
            "No","Periode","Hari Perawatan","Pasien Keluar","TT","Jml.Hari",
            "BOR (%)","AvLOS (hari)","TOI (hari)","BTO (kali)"}){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        Tabel1.setModel(tabMode);
        Tabel1.setPreferredScrollableViewportSize(new Dimension(500,500));
        Tabel1.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] lebar={35,140,100,95,55,70,80,95,85,85};
        for (i = 0; i < lebar.length; i++) {
            TableColumn column = Tabel1.getColumnModel().getColumn(i);
            column.setPreferredWidth(lebar[i]);
        }
        Tabel1.setDefaultRenderer(Object.class, new WarnaTable());

        LocalDate skr=LocalDate.now();
        Tgl1.setDate(java.sql.Date.valueOf(skr.withDayOfYear(1)));
        Tgl2.setDate(java.sql.Date.valueOf(skr));
    }

    private void runBackground(Runnable task) {
        try {
            executor.submit(() -> {
                try {
                    task.run();
                } catch (Exception e) {
                    System.out.println("laporan.DlgGrafikBarberJohnson.runBackground() : "+e);
                }
            });
        } catch (RejectedExecutionException e) {
            System.out.println("laporan.DlgGrafikBarberJohnson.runBackground() : "+e);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TKd = new widget.TextBox();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnSemuaKelas = new javax.swing.JMenuItem();
        MnKelas1 = new javax.swing.JMenuItem();
        MnKelas2 = new javax.swing.JMenuItem();
        MnKelas3 = new javax.swing.JMenuItem();
        MnKelasVIP = new javax.swing.JMenuItem();
        MnKelasVVIP = new javax.swing.JMenuItem();
        MnKelasUtama = new javax.swing.JMenuItem();
        internalFrame1 = new widget.InternalFrame();
        panelGlass5 = new widget.panelisi();
        label11 = new widget.Label();
        Tgl1 = new widget.Tanggal();
        label18 = new widget.Label();
        Tgl2 = new widget.Tanggal();
        jLabel17 = new widget.Label();
        Kamar = new widget.TextBox();
        BtnSeek6 = new widget.Button();
        jLabel19 = new widget.Label();
        TTT = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        jLabel7 = new widget.Label();
        BtnPrint = new widget.Button();
        BtnKeluar = new widget.Button();
        TabRawat = new javax.swing.JTabbedPane();
        internalFrame2 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        Tabel1 = new widget.Table();
        internalFrame3 = new widget.InternalFrame();
        PanelGrafik = new javax.swing.JPanel();

        TKd.setForeground(new java.awt.Color(255, 255, 255));
        TKd.setName("TKd"); // NOI18N

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnSemuaKelas.setBackground(new java.awt.Color(255, 255, 254));
        MnSemuaKelas.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnSemuaKelas.setForeground(new java.awt.Color(50, 50, 50));
        MnSemuaKelas.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnSemuaKelas.setText("Semua Kelas");
        MnSemuaKelas.setName("MnSemuaKelas"); // NOI18N
        MnSemuaKelas.setPreferredSize(new java.awt.Dimension(180, 26));
        MnSemuaKelas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnSemuaKelasActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnSemuaKelas);

        MnKelas1.setBackground(new java.awt.Color(255, 255, 254));
        MnKelas1.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnKelas1.setForeground(new java.awt.Color(50, 50, 50));
        MnKelas1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnKelas1.setText("Filter Kamar Kelas 1");
        MnKelas1.setName("MnKelas1"); // NOI18N
        MnKelas1.setPreferredSize(new java.awt.Dimension(180, 26));
        MnKelas1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnKelas1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnKelas1);

        MnKelas2.setBackground(new java.awt.Color(255, 255, 254));
        MnKelas2.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnKelas2.setForeground(new java.awt.Color(50, 50, 50));
        MnKelas2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnKelas2.setText("Filter Kamar Kelas 2");
        MnKelas2.setName("MnKelas2"); // NOI18N
        MnKelas2.setPreferredSize(new java.awt.Dimension(180, 26));
        MnKelas2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnKelas2ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnKelas2);

        MnKelas3.setBackground(new java.awt.Color(255, 255, 254));
        MnKelas3.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnKelas3.setForeground(new java.awt.Color(50, 50, 50));
        MnKelas3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnKelas3.setText("Filter Kamar Kelas 3");
        MnKelas3.setName("MnKelas3"); // NOI18N
        MnKelas3.setPreferredSize(new java.awt.Dimension(180, 26));
        MnKelas3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnKelas3ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnKelas3);

        MnKelasVIP.setBackground(new java.awt.Color(255, 255, 254));
        MnKelasVIP.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnKelasVIP.setForeground(new java.awt.Color(50, 50, 50));
        MnKelasVIP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnKelasVIP.setText("Filter Kamar Kelas VIP");
        MnKelasVIP.setName("MnKelasVIP"); // NOI18N
        MnKelasVIP.setPreferredSize(new java.awt.Dimension(180, 26));
        MnKelasVIP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnKelasVIPActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnKelasVIP);

        MnKelasVVIP.setBackground(new java.awt.Color(255, 255, 254));
        MnKelasVVIP.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnKelasVVIP.setForeground(new java.awt.Color(50, 50, 50));
        MnKelasVVIP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnKelasVVIP.setText("Filter Kamar Kelas VVIP");
        MnKelasVVIP.setName("MnKelasVVIP"); // NOI18N
        MnKelasVVIP.setPreferredSize(new java.awt.Dimension(180, 26));
        MnKelasVVIP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnKelasVVIPActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnKelasVVIP);

        MnKelasUtama.setBackground(new java.awt.Color(255, 255, 254));
        MnKelasUtama.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnKelasUtama.setForeground(new java.awt.Color(50, 50, 50));
        MnKelasUtama.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnKelasUtama.setText("Filter Kamar Kelas Utama");
        MnKelasUtama.setName("MnKelasUtama"); // NOI18N
        MnKelasUtama.setPreferredSize(new java.awt.Dimension(180, 26));
        MnKelasUtama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnKelasUtamaActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnKelasUtama);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Grafik Barber Johnson ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass5.setName("panelGlass5"); // NOI18N
        panelGlass5.setPreferredSize(new java.awt.Dimension(55, 55));
        panelGlass5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 9));

        label11.setText("Tanggal :");
        label11.setName("label11"); // NOI18N
        label11.setPreferredSize(new java.awt.Dimension(53, 23));
        panelGlass5.add(label11);

        Tgl1.setDisplayFormat("dd-MM-yyyy");
        Tgl1.setName("Tgl1"); // NOI18N
        Tgl1.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass5.add(Tgl1);

        label18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        label18.setText("s.d.");
        label18.setName("label18"); // NOI18N
        label18.setPreferredSize(new java.awt.Dimension(25, 23));
        panelGlass5.add(label18);

        Tgl2.setDisplayFormat("dd-MM-yyyy");
        Tgl2.setName("Tgl2"); // NOI18N
        Tgl2.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass5.add(Tgl2);

        jLabel17.setText("Ruang :");
        jLabel17.setName("jLabel17"); // NOI18N
        jLabel17.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass5.add(jLabel17);

        Kamar.setEditable(false);
        Kamar.setName("Kamar"); // NOI18N
        Kamar.setPreferredSize(new java.awt.Dimension(140, 23));
        panelGlass5.add(Kamar);

        BtnSeek6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnSeek6.setMnemonic('5');
        BtnSeek6.setToolTipText("ALt+5");
        BtnSeek6.setName("BtnSeek6"); // NOI18N
        BtnSeek6.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnSeek6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSeek6ActionPerformed(evt);
            }
        });
        panelGlass5.add(BtnSeek6);

        jLabel19.setText("Jml.TT :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass5.add(jLabel19);

        TTT.setToolTipText("Jumlah tempat tidur tersedia. Kosongkan untuk memakai jumlah kamar dari master data.");
        TTT.setName("TTT"); // NOI18N
        TTT.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass5.add(TTT);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('2');
        BtnCari.setToolTipText("Alt+2");
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
        panelGlass5.add(BtnCari);

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
        panelGlass5.add(BtnAll);

        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(10, 23));
        panelGlass5.add(jLabel7);

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
        panelGlass5.add(BtnPrint);

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
        panelGlass5.add(BtnKeluar);

        internalFrame1.add(panelGlass5, java.awt.BorderLayout.PAGE_END);

        TabRawat.setBackground(new java.awt.Color(255, 255, 255));
        TabRawat.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(241, 246, 236)));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.setName("TabRawat"); // NOI18N
        TabRawat.setOpaque(true);

        internalFrame2.setBackground(new java.awt.Color(235, 255, 235));
        internalFrame2.setBorder(null);
        internalFrame2.setName("internalFrame2"); // NOI18N
        internalFrame2.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setComponentPopupMenu(jPopupMenu1);
        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        Tabel1.setComponentPopupMenu(jPopupMenu1);
        Tabel1.setName("Tabel1"); // NOI18N
        Scroll.setViewportView(Tabel1);

        internalFrame2.add(Scroll, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Rekap Indikator", internalFrame2);

        internalFrame3.setBackground(new java.awt.Color(235, 255, 235));
        internalFrame3.setBorder(null);
        internalFrame3.setName("internalFrame3"); // NOI18N
        internalFrame3.setLayout(new java.awt.BorderLayout(1, 1));

        PanelGrafik.setBackground(new java.awt.Color(255, 255, 255));
        PanelGrafik.setName("PanelGrafik"); // NOI18N
        PanelGrafik.setLayout(new java.awt.BorderLayout());
        internalFrame3.add(PanelGrafik, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Grafik Barber Johnson", internalFrame3);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        runBackground(() ->tampil());
    }//GEN-LAST:event_formWindowOpened

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        runBackground(() ->tampil());
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            runBackground(() ->tampil());
        }else{
            Valid.pindah(evt, TTT, BtnPrint);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TKd.setText("");
        Kamar.setText("");
        TTT.setText("");
        filter="";
        runBackground(() ->tampil());
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnAllActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnCari, BtnKeluar);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void BtnSeek6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSeek6ActionPerformed
        DlgCariBangsal ruang=new DlgCariBangsal(null,false);
        ruang.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(ruang.getTable().getSelectedRow()!= -1){
                    TKd.setText(ruang.getTable().getValueAt(ruang.getTable().getSelectedRow(),0).toString());
                    Kamar.setText(ruang.getTable().getValueAt(ruang.getTable().getSelectedRow(),1).toString());
                    TTT.setText("");
                    Kamar.requestFocus();
                }
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        ruang.isCek();
        ruang.emptTeks();
        ruang.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        ruang.setLocationRelativeTo(internalFrame1);
        ruang.setVisible(true);
    }//GEN-LAST:event_BtnSeek6ActionPerformed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        if(TabRawat.getSelectedIndex()==1){
            if(panelChart==null){
                JOptionPane.showMessageDialog(null,"Maaf, grafik belum terbentuk. Tampilkan data dulu...!!!!");
            }else{
                panelChart.createChartPrintJob();
            }
            return;
        }
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
        }else{
            Map<String, Object> param = new HashMap<>();
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());
            param.put("periode",Tgl1.getSelectedItem()+" s.d. "+Tgl2.getSelectedItem());
            param.put("ruang",Kamar.getText().isEmpty()?"Semua Ruang":Kamar.getText());
            param.put("tanggal",Tgl2.getDate());
            Sequel.queryu("delete from temporary where temp37='"+akses.getalamatip()+"'");
            for(int r=0;r<tabMode.getRowCount();r++){
                Sequel.menyimpan("temporary","'"+r+"','"+
                                tabMode.getValueAt(r,0).toString()+"','"+
                                tabMode.getValueAt(r,1).toString()+"','"+
                                tabMode.getValueAt(r,2).toString()+"','"+
                                tabMode.getValueAt(r,3).toString()+"','"+
                                tabMode.getValueAt(r,4).toString()+"','"+
                                tabMode.getValueAt(r,5).toString()+"','"+
                                tabMode.getValueAt(r,6).toString()+"','"+
                                tabMode.getValueAt(r,7).toString()+"','"+
                                tabMode.getValueAt(r,8).toString()+"','"+
                                tabMode.getValueAt(r,9).toString()+"','','','','','','','','','','','','','','','','','','','','','','','','','','"+akses.getalamatip()+"'","Transaksi");
            }
            Valid.MyReportqry("rptBarberJohnson.jasper","report","::[ Indikator Barber Johnson ]::","select * from temporary where temporary.temp37='"+akses.getalamatip()+"' order by temporary.no",param);
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnPrintActionPerformed(null);
        }
    }//GEN-LAST:event_BtnPrintKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{Valid.pindah(evt,BtnKeluar,TKd);}
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void MnSemuaKelasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnSemuaKelasActionPerformed
        filter="";
        runBackground(() ->tampil());
    }//GEN-LAST:event_MnSemuaKelasActionPerformed

    private void MnKelas1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnKelas1ActionPerformed
        filter="and kamar.kelas='Kelas 1' ";
        runBackground(() ->tampil());
    }//GEN-LAST:event_MnKelas1ActionPerformed

    private void MnKelas2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnKelas2ActionPerformed
        filter="and kamar.kelas='Kelas 2' ";
        runBackground(() ->tampil());
    }//GEN-LAST:event_MnKelas2ActionPerformed

    private void MnKelas3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnKelas3ActionPerformed
        filter="and kamar.kelas='Kelas 3' ";
        runBackground(() ->tampil());
    }//GEN-LAST:event_MnKelas3ActionPerformed

    private void MnKelasVIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnKelasVIPActionPerformed
        filter="and kamar.kelas='Kelas VIP' ";
        runBackground(() ->tampil());
    }//GEN-LAST:event_MnKelasVIPActionPerformed

    private void MnKelasVVIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnKelasVVIPActionPerformed
        filter="and kamar.kelas='Kelas VVIP' ";
        runBackground(() ->tampil());
    }//GEN-LAST:event_MnKelasVVIPActionPerformed

    private void MnKelasUtamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnKelasUtamaActionPerformed
        filter="and kamar.kelas='Kelas Utama' ";
        runBackground(() ->tampil());
    }//GEN-LAST:event_MnKelasUtamaActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgGrafikBarberJohnson dialog = new DlgGrafikBarberJohnson(new javax.swing.JFrame(), true);
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
    private widget.Button BtnCari;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSeek6;
    private widget.TextBox Kamar;
    private javax.swing.JMenuItem MnKelas1;
    private javax.swing.JMenuItem MnKelas2;
    private javax.swing.JMenuItem MnKelas3;
    private javax.swing.JMenuItem MnKelasUtama;
    private javax.swing.JMenuItem MnKelasVIP;
    private javax.swing.JMenuItem MnKelasVVIP;
    private javax.swing.JMenuItem MnSemuaKelas;
    private javax.swing.JPanel PanelGrafik;
    private widget.ScrollPane Scroll;
    private javax.swing.JTabbedPane TabRawat;
    private widget.Table Tabel1;
    private widget.TextBox TKd;
    private widget.TextBox TTT;
    private widget.Tanggal Tgl1;
    private widget.Tanggal Tgl2;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame2;
    private widget.InternalFrame internalFrame3;
    private widget.Label jLabel17;
    private widget.Label jLabel19;
    private widget.Label jLabel7;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.Label label11;
    private widget.Label label18;
    private widget.panelisi panelGlass5;
    // End of variables declaration//GEN-END:variables

    /** Potongan SQL filter bangsal, dipakai bareng {@link #filter} (filter kelas). */
    private String filterBangsal(){
        return TKd.getText().isEmpty()?"":"and kamar.kd_bangsal='"+TKd.getText()+"' ";
    }

    /** Jumlah TT: isian manual kalau diisi, kalau tidak dihitung dari master kamar. */
    private int jumlahTT(){
        if(!TTT.getText().trim().isEmpty()){
            try{
                int tt=Integer.parseInt(TTT.getText().trim());
                if(tt>0){
                    return tt;
                }
            }catch(NumberFormatException e){
                System.out.println("laporan.DlgGrafikBarberJohnson.jumlahTT() : "+e);
            }
        }
        return Sequel.cariInteger("select count(*) from kamar where kamar.statusdata='1' "+filterBangsal()+filter);
    }

    /**
     * Hari perawatan periode awal..akhir (inklusif).
     * Hari keluar tidak dihitung; pasien masuk-keluar hari yang sama dihitung 1 hari.
     */
    private double hariPerawatan(String awal,String akhir){
        String sql=
            "select ifnull(sum("+
            "  greatest(datediff("+
            "     least(if(kamar_inap.tgl_keluar='0000-00-00' or kamar_inap.tgl_keluar is null,"+
            "              date_add(curdate(),interval 1 day),kamar_inap.tgl_keluar),"+
            "           date_add('"+akhir+"',interval 1 day)),"+
            "     greatest(kamar_inap.tgl_masuk,'"+awal+"')),0)"+
            "  + if(kamar_inap.tgl_keluar=kamar_inap.tgl_masuk and kamar_inap.tgl_masuk between '"+awal+"' and '"+akhir+"',1,0)"+
            "),0) "+
            "from kamar_inap inner join kamar on kamar_inap.kd_kamar=kamar.kd_kamar "+
            "where kamar_inap.tgl_masuk<='"+akhir+"' "+
            "and (kamar_inap.tgl_keluar='0000-00-00' or kamar_inap.tgl_keluar is null or kamar_inap.tgl_keluar>='"+awal+"') "+
            filterBangsal()+filter;
        String hasilsql=Sequel.cariIsi(sql);
        try{
            return (hasilsql==null||hasilsql.isEmpty())?0:Double.parseDouble(hasilsql);
        }catch(NumberFormatException e){
            System.out.println("laporan.DlgGrafikBarberJohnson.hariPerawatan() : "+e);
            return 0;
        }
    }

    /** Jumlah pasien keluar (hidup + mati) pada periode, pindah kamar tidak dihitung. */
    private int pasienKeluar(String awal,String akhir){
        return Sequel.cariInteger(
            "select count(distinct kamar_inap.no_rawat) "+
            "from kamar_inap inner join kamar on kamar_inap.kd_kamar=kamar.kd_kamar "+
            "where kamar_inap.tgl_keluar between '"+awal+"' and '"+akhir+"' "+
            "and kamar_inap.stts_pulang<>'Pindah Kamar' "+
            filterBangsal()+filter);
    }

    private Indikator hitung(String nama,LocalDate awal,LocalDate akhir,int tt){
        Indikator ind=new Indikator();
        ind.nama=nama;
        ind.tt=tt;
        ind.hari=(int)(akhir.toEpochDay()-awal.toEpochDay())+1;
        ind.hp=hariPerawatan(awal.toString(),akhir.toString());
        ind.keluar=pasienKeluar(awal.toString(),akhir.toString());
        double kapasitas=(double)tt*ind.hari;
        ind.bor  = kapasitas>0 ? (ind.hp/kapasitas)*100 : 0;
        ind.alos = ind.keluar>0 ? ind.hp/ind.keluar : 0;
        ind.toi  = ind.keluar>0 ? (kapasitas-ind.hp)/ind.keluar : 0;
        ind.bto  = tt>0 ? (double)ind.keluar/tt : 0;
        return ind;
    }

    private void tambahBaris(Indikator ind){
        tabMode.addRow(new Object[]{
            ind.total?"":String.valueOf(tabMode.getRowCount()+1),
            ind.nama,
            Valid.SetAngka2(ind.hp),
            String.valueOf(ind.keluar),
            String.valueOf(ind.tt),
            String.valueOf(ind.hari),
            Valid.SetAngka2(ind.bor),
            Valid.SetAngka2(ind.alos),
            Valid.SetAngka2(ind.toi),
            Valid.SetAngka2(ind.bto)
        });
    }

    public void tampil(){
        try{
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Valid.tabelKosong(tabMode);
            hasil.clear();

            LocalDate d1=LocalDate.parse(Valid.SetTgl(Tgl1.getSelectedItem()+""));
            LocalDate d2=LocalDate.parse(Valid.SetTgl(Tgl2.getSelectedItem()+""));
            if(d2.isBefore(d1)){
                JOptionPane.showMessageDialog(null,"Maaf, tanggal akhir tidak boleh lebih awal dari tanggal awal...!!!!");
                return;
            }

            int tt=jumlahTT();
            SwingUtilities.invokeLater(() ->TTT.setText(String.valueOf(tt)));
            if(tt<=0){
                JOptionPane.showMessageDialog(null,"Maaf, jumlah tempat tidur 0. Isi manual di kolom Jml.TT atau lengkapi master kamar...!!!!");
                return;
            }

            LocalDate cur=d1.withDayOfMonth(1);
            while(!cur.isAfter(d2)){
                LocalDate awal=cur.isBefore(d1)?d1:cur;
                LocalDate akhir=cur.withDayOfMonth(cur.lengthOfMonth());
                if(akhir.isAfter(d2)){
                    akhir=d2;
                }
                hasil.add(hitung(NAMABULAN[cur.getMonthValue()-1]+" "+cur.getYear(),awal,akhir,tt));
                cur=cur.plusMonths(1);
            }

            Indikator total=hitung("TOTAL PERIODE",d1,d2,tt);
            total.total=true;

            SwingUtilities.invokeLater(() -> {
                for(Indikator ind : hasil){
                    tambahBaris(ind);
                }
                tambahBaris(total);
                gambarGrafik(total);
            });
            this.setCursor(Cursor.getDefaultCursor());
        }catch(Exception e){
            System.out.println("laporan.DlgGrafikBarberJohnson.tampil() : "+e);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }

    /** Bangun grafik Barber Johnson: sumbu X = TOI, sumbu Y = AvLOS. */
    private void gambarGrafik(Indikator total){
        XYSeries series=new XYSeries("Periode");
        double maxX=6,maxY=15;
        for(Indikator ind : hasil){
            if(ind.keluar<=0){
                continue;
            }
            series.add(ind.toi,ind.alos);
            maxX=Math.max(maxX,Math.ceil(ind.toi)+2);
            maxY=Math.max(maxY,Math.ceil(ind.alos)+3);
        }
        XYSeriesCollection dataset=new XYSeriesCollection(series);

        JFreeChart chart=ChartFactory.createScatterPlot(
            "Grafik Barber Johnson"+(Kamar.getText().isEmpty()?"":" - "+Kamar.getText()),
            "TOI (hari)","AvLOS (hari)",dataset,PlotOrientation.VERTICAL,false,true,false);
        chart.addSubtitle(new org.jfree.chart.title.TextTitle(
            "Periode "+Tgl1.getSelectedItem()+" s.d. "+Tgl2.getSelectedItem()+
            "   |   BOR "+Valid.SetAngka2(total.bor)+"%  AvLOS "+Valid.SetAngka2(total.alos)+
            " hari  TOI "+Valid.SetAngka2(total.toi)+" hari  BTO "+Valid.SetAngka2(total.bto)+" kali"));

        XYPlot plot=chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(new Color(220,220,220));
        plot.setRangeGridlinePaint(new Color(220,220,220));

        NumberAxis sumbuX=(NumberAxis)plot.getDomainAxis();
        sumbuX.setRange(0,maxX);
        NumberAxis sumbuY=(NumberAxis)plot.getRangeAxis();
        sumbuY.setRange(0,maxY);

        // Daerah efisien: TOI 1-3 hari, AvLOS 3-12 hari
        plot.addAnnotation(new XYShapeAnnotation(
            new Rectangle2D.Double(1,3,2,9),
            new BasicStroke(1.0f),new Color(0,140,0),new Color(120,220,120,60)));
        XYTextAnnotation lblEfisien=new XYTextAnnotation("Daerah Efisien",2,7.5);
        lblEfisien.setPaint(new Color(0,110,0));
        lblEfisien.setFont(new java.awt.Font("Tahoma",java.awt.Font.BOLD,11));
        plot.addAnnotation(lblEfisien);

        // Garis BOR: AvLOS = TOI * BOR/(100-BOR), semua lewat titik (0,0)
        for(double bor : new double[]{50,60,70,75,80,85,90}){
            double slope=bor/(100-bor);
            double xu=maxX,yu=slope*maxX;
            if(yu>maxY){
                yu=maxY;
                xu=maxY/slope;
            }
            boolean penting=(bor==75||bor==85);
            plot.addAnnotation(new XYLineAnnotation(0,0,xu,yu,
                new BasicStroke(penting?1.4f:0.7f),penting?new Color(0,120,0):Color.GRAY));
            XYTextAnnotation lbl=new XYTextAnnotation("BOR "+(int)bor+"%",xu,yu);
            lbl.setTextAnchor(TextAnchor.BOTTOM_RIGHT);
            lbl.setFont(new java.awt.Font("Tahoma",java.awt.Font.PLAIN,10));
            lbl.setPaint(penting?new Color(0,120,0):Color.DARK_GRAY);
            plot.addAnnotation(lbl);
        }

        // Garis BTO: AvLOS + TOI = 365/BTO
        for(double bto : new double[]{15,20,25,30,40}){
            double s=365.0/bto;
            double x1=Math.max(0,s-maxY),y1=Math.min(s,maxY);
            double x2=Math.min(s,maxX),y2=Math.max(0,s-maxX);
            if(x1>maxX){
                continue;
            }
            boolean penting=(bto==30);
            plot.addAnnotation(new XYLineAnnotation(x1,y1,x2,y2,
                new BasicStroke(penting?1.4f:0.7f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,
                                10.0f,new float[]{5.0f,4.0f},0.0f),
                penting?new Color(180,0,0):Color.LIGHT_GRAY));
            XYTextAnnotation lbl=new XYTextAnnotation("BTO "+(int)bto,x2,y2);
            lbl.setTextAnchor(TextAnchor.BOTTOM_LEFT);
            lbl.setFont(new java.awt.Font("Tahoma",java.awt.Font.PLAIN,10));
            lbl.setPaint(penting?new Color(180,0,0):Color.GRAY);
            plot.addAnnotation(lbl);
        }

        // Garis batas TOI 1-3 hari dan AvLOS 3-12 hari
        plot.addAnnotation(new XYLineAnnotation(0,3,maxX,3,new BasicStroke(0.5f),new Color(0,0,160)));
        plot.addAnnotation(new XYLineAnnotation(0,12,maxX,12,new BasicStroke(0.5f),new Color(0,0,160)));
        plot.addAnnotation(new XYLineAnnotation(1,0,1,maxY,new BasicStroke(0.5f),new Color(0,0,160)));
        plot.addAnnotation(new XYLineAnnotation(3,0,3,maxY,new BasicStroke(0.5f),new Color(0,0,160)));

        // Label nama periode di tiap titik
        for(Indikator ind : hasil){
            if(ind.keluar<=0){
                continue;
            }
            XYTextAnnotation lbl=new XYTextAnnotation(ind.nama,ind.toi,ind.alos);
            lbl.setTextAnchor(TextAnchor.BOTTOM_LEFT);
            lbl.setFont(new java.awt.Font("Tahoma",java.awt.Font.PLAIN,10));
            lbl.setPaint(Color.BLACK);
            plot.addAnnotation(lbl);
        }

        XYLineAndShapeRenderer renderer=new XYLineAndShapeRenderer(false,true);
        renderer.setSeriesPaint(0,new Color(200,0,0));
        renderer.setSeriesShape(0,new java.awt.geom.Ellipse2D.Double(-4,-4,8,8));
        plot.setRenderer(renderer);

        PanelGrafik.removeAll();
        panelChart=new ChartPanel(chart);
        PanelGrafik.add(panelChart,java.awt.BorderLayout.CENTER);
        PanelGrafik.revalidate();
        PanelGrafik.repaint();
    }

    @Override
    public void dispose() {
        executor.shutdown();
        super.dispose();
    }
}
