/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DlgAbout.java
 *
 * Created on 23 Jun 10, 19:03:08
 */

package laporan;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import static javafx.concurrent.Worker.State.FAILED;
import javafx.embed.swing.JFXPanel;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author perpustakaan
 */
public class DlgRetensi extends javax.swing.JDialog {
    private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;
 
    private final JPanel panel = new JPanel(new BorderLayout());
    private final JLabel lblStatus = new JLabel();

    private final JTextField txtURL = new JTextField();
    private final JProgressBar progressBar = new JProgressBar();
    private final Properties prop = new Properties(); 
    private final validasi Valid=new validasi();

    // ---- Tab murni Java, tidak bergantung modul web hybrid ----
    private final Connection koneksi = koneksiDB.condb();
    private widget.TabPane tabbedPane;
    private widget.Table tbSudah, tbTanpa;
    private DefaultTableModel modeSudah, modeTanpa;
    private widget.TextBox TCariSudah, TCariTanpa;
    private widget.ComboBox cmbTahun;
    private javax.swing.JCheckBox chkBelumSaja;
    private widget.Label lblJumlahSudah, lblJumlahTanpa;
    private boolean termuatSudah = false, termuatTanpa = false;
    
    public DlgRetensi(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initComponents2();
    }
    
    private void initComponents2() {           
        txtURL.addActionListener((ActionEvent e) -> {
            loadURL(txtURL.getText());
        });
  
        progressBar.setPreferredSize(new Dimension(150, 18));
        progressBar.setStringPainted(true);
        
        panel.add(jfxPanel, BorderLayout.CENTER);

        tabbedPane = new widget.TabPane();
        tabbedPane.setFont(new java.awt.Font("Tahoma", 0, 11));
        tabbedPane.addTab("Berkas Retensi (Web)", panel);
        tabbedPane.addTab("Sudah Ada Berkas Retensi", buatTabSudahRetensi());
        tabbedPane.addTab("Tanpa Kunjungan 5 Tahun", buatTabTanpaKunjungan());
        // Isi tab dimuat saat pertama dibuka: query tab ke-3 menyapu seluruh reg_periksa,
        // percuma dijalankan kalau petugas cuma mau memakai modul web.
        tabbedPane.addChangeListener((javax.swing.event.ChangeEvent e) -> {
            if (tabbedPane.getSelectedIndex() == 1 && !termuatSudah) {
                termuatSudah = true;
                tampilSudahRetensi();
            } else if (tabbedPane.getSelectedIndex() == 2 && !termuatTanpa) {
                termuatTanpa = true;
                tampilTanpaKunjungan();
            }
        });

        internalFrame1.setLayout(new BorderLayout());
        internalFrame1.add(tabbedPane);
    }
    
     private void createScene() {        
        Platform.runLater(new Runnable() {

            public void run() {
                WebView view = new WebView();
                
                engine = view.getEngine();
                engine.setJavaScriptEnabled(true);
                
                engine.setCreatePopupHandler(new Callback<PopupFeatures, WebEngine>() {
                    @Override
                    public WebEngine call(PopupFeatures p) {
                        Stage stage = new Stage(StageStyle.TRANSPARENT);
                        return view.getEngine();
                    }
                });
                
                engine.titleProperty().addListener((ObservableValue<? extends String> observable, String oldValue, final String newValue) -> {
                    SwingUtilities.invokeLater(() -> {
                        DlgRetensi.this.setTitle(newValue);
                    });
                });
                
                
                engine.setOnStatusChanged((final WebEvent<String> event) -> {
                    SwingUtilities.invokeLater(() -> {
                        lblStatus.setText(event.getData());
                    });
                });
                
                
                engine.getLoadWorker().workDoneProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldValue, final Number newValue) -> {
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setValue(newValue.intValue());
                    });                                                   
                });
                
                engine.getLoadWorker().exceptionProperty().addListener((ObservableValue<? extends Throwable> o, Throwable old, final Throwable value) -> {
                    if (engine.getLoadWorker().getState() == FAILED) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(
                                    panel,
                                    (value != null) ?
                                            engine.getLocation() + "\n" + value.getMessage() :
                                            engine.getLocation() + "\nUnexpected Catatan.",
                                    "Loading Catatan...",
                                    JOptionPane.ERROR_MESSAGE);
                        });
                    }
                });
                
                
                engine.locationProperty().addListener((ObservableValue<? extends String> ov, String oldValue, final String newValue) -> {
                    SwingUtilities.invokeLater(() -> {
                        txtURL.setText(newValue);
                    });
                });
                
                engine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
                    @Override
                    public void changed(ObservableValue ov, State oldState, State newState) {
                        if (newState == State.SUCCEEDED) {
                            try {
                                prop.loadFromXML(new FileInputStream("setting/database.xml"));
                                if(engine.getLocation().replaceAll("http://"+koneksiDB.HOSTHYBRIDWEB()+":"+prop.getProperty("PORTWEB")+"/"+prop.getProperty("HYBRIDWEB")+"/","").contains("medrec/pages")){
                                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                                    Valid.panggilUrl(engine.getLocation().replaceAll("http://"+koneksiDB.HOSTHYBRIDWEB()+":"+prop.getProperty("PORTWEB")+"/"+prop.getProperty("HYBRIDWEB")+"/medrec/pages/upload/","medrec/").replaceAll("http://"+koneksiDB.HOSTHYBRIDWEB()+"/"+prop.getProperty("HYBRIDWEB")+"/medrec/pages/upload/","medrec/"));
                                    engine.executeScript("history.back()");
                                    setCursor(Cursor.getDefaultCursor());
                                }else if(engine.getLocation().replaceAll("http://"+koneksiDB.HOSTHYBRIDWEB()+":"+prop.getProperty("PORTWEB")+"/"+prop.getProperty("HYBRIDWEB")+"/","").contains("Keluar")){
                                    dispose();
                                }
                            } catch (Exception ex) {
                                System.out.println("Notifikasi : "+ex);
                            }
                        } 
                    }
                });
                
                jfxPanel.setScene(new Scene(view));
            }
        });
    }
 
    public void loadURL(String url) {  
        try {
            createScene();
        } catch (Exception e) {
        }
        
        Platform.runLater(() -> {
            try {
                engine.load(url);
            }catch (Exception exception) {
                engine.load(url);
            }
        });        
    }    
    
    public void CloseScane(){
        Platform.setImplicitExit(false);
    }
    
    public void print(final Node node) {
        Printer printer = Printer.getDefaultPrinter();
        PageLayout pageLayout = printer.createPageLayout(Paper.NA_LETTER, PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);
        double scaleX = pageLayout.getPrintableWidth() / node.getBoundsInParent().getWidth();
        double scaleY = pageLayout.getPrintableHeight() / node.getBoundsInParent().getHeight();
        node.getTransforms().add(new Scale(scaleX, scaleY));

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean success = job.printPage(node);
            if (success) {
                job.endJob();
            }
        }
    }
    


    // =====================================================================
    //  Tab 2 : pasien yang berkas retensinya sudah ada
    //  Tab 3 : pasien tanpa kunjungan N tahun ke belakang (kandidat retensi)
    //
    //  Keduanya baca langsung dari database (retensi_pasien, pasien,
    //  reg_periksa), jadi tetap bisa dipakai walau modul web hybrid mati.
    // =====================================================================

    private JPanel buatTabSudahRetensi() {
        modeSudah = new DefaultTableModel(null, new Object[]{
            "No.RM", "Nama Pasien", "No.KTP", "J.K.", "Tgl Lahir", "Alamat",
            "Terakhir Daftar", "Tgl Retensi", "Berkas PDF"
        }) {
            @Override
            public boolean isCellEditable(int baris, int kolom) {
                return false;
            }
        };
        tbSudah = new widget.Table();
        tbSudah.setModel(modeSudah);
        tbSudah.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        aturLebar(tbSudah, new int[]{70, 200, 130, 40, 85, 260, 100, 90, 320});
        tbSudah.setDefaultRenderer(Object.class, new WarnaTable());

        TCariSudah = new widget.TextBox();
        TCariSudah.setDocument(new batasInput((byte) 100).getKata(TCariSudah));
        TCariSudah.setPreferredSize(new Dimension(220, 23));
        TCariSudah.addActionListener((ActionEvent e) -> tampilSudahRetensi());

        widget.Button btnCari = new widget.Button();
        btnCari.setText("Cari");
        btnCari.setPreferredSize(new Dimension(80, 23));
        btnCari.addActionListener((ActionEvent e) -> tampilSudahRetensi());

        widget.Button btnSemua = new widget.Button();
        btnSemua.setText("Semua");
        btnSemua.setPreferredSize(new Dimension(80, 23));
        btnSemua.addActionListener((ActionEvent e) -> {
            TCariSudah.setText("");
            tampilSudahRetensi();
        });

        lblJumlahSudah = new widget.Label();
        lblJumlahSudah.setText("0");

        widget.panelisi atas = new widget.panelisi();
        atas.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 4));
        atas.add(labelKecil("Cari No.RM / Nama / Alamat :"));
        atas.add(TCariSudah);
        atas.add(btnCari);
        atas.add(btnSemua);
        atas.add(labelKecil("     Jumlah berkas :"));
        atas.add(lblJumlahSudah);

        widget.ScrollPane scroll = new widget.ScrollPane();
        scroll.setViewportView(tbSudah);

        JPanel isi = new JPanel(new BorderLayout());
        isi.add(atas, BorderLayout.NORTH);
        isi.add(scroll, BorderLayout.CENTER);
        return isi;
    }

    private JPanel buatTabTanpaKunjungan() {
        modeTanpa = new DefaultTableModel(null, new Object[]{
            "No.RM", "Nama Pasien", "No.KTP", "J.K.", "Tgl Lahir", "Alamat",
            "Aktivitas Terakhir", "Sumber", "Lama (thn)", "Status Retensi"
        }) {
            @Override
            public boolean isCellEditable(int baris, int kolom) {
                return false;
            }
        };
        tbTanpa = new widget.Table();
        tbTanpa.setModel(modeTanpa);
        tbTanpa.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        aturLebar(tbTanpa, new int[]{70, 200, 130, 40, 85, 300, 110, 90, 70, 100});
        tbTanpa.setDefaultRenderer(Object.class, new WarnaTable());

        cmbTahun = new widget.ComboBox();
        cmbTahun.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"2", "3", "5", "10"}));
        cmbTahun.setSelectedItem("5");
        cmbTahun.setPreferredSize(new Dimension(60, 23));
        cmbTahun.addActionListener((ActionEvent e) -> {
            if (termuatTanpa) {
                tampilTanpaKunjungan();
            }
        });

        chkBelumSaja = new javax.swing.JCheckBox("Hanya yang belum diretensi");
        chkBelumSaja.setFont(new java.awt.Font("Tahoma", 0, 11));
        chkBelumSaja.setOpaque(false);
        chkBelumSaja.addActionListener((ActionEvent e) -> {
            if (termuatTanpa) {
                tampilTanpaKunjungan();
            }
        });

        TCariTanpa = new widget.TextBox();
        TCariTanpa.setDocument(new batasInput((byte) 100).getKata(TCariTanpa));
        TCariTanpa.setPreferredSize(new Dimension(200, 23));
        TCariTanpa.addActionListener((ActionEvent e) -> tampilTanpaKunjungan());

        widget.Button btnCari = new widget.Button();
        btnCari.setText("Cari");
        btnCari.setPreferredSize(new Dimension(80, 23));
        btnCari.addActionListener((ActionEvent e) -> tampilTanpaKunjungan());

        widget.Button btnSemua = new widget.Button();
        btnSemua.setText("Semua");
        btnSemua.setPreferredSize(new Dimension(80, 23));
        btnSemua.addActionListener((ActionEvent e) -> {
            TCariTanpa.setText("");
            tampilTanpaKunjungan();
        });

        lblJumlahTanpa = new widget.Label();
        lblJumlahTanpa.setText("0");

        widget.panelisi atas = new widget.panelisi();
        atas.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 4));
        atas.add(labelKecil("Tanpa kunjungan minimal"));
        atas.add(cmbTahun);
        atas.add(labelKecil("tahun     "));
        atas.add(chkBelumSaja);
        atas.add(labelKecil("     Cari :"));
        atas.add(TCariTanpa);
        atas.add(btnCari);
        atas.add(btnSemua);
        atas.add(labelKecil("     Jumlah :"));
        atas.add(lblJumlahTanpa);

        widget.ScrollPane scroll = new widget.ScrollPane();
        scroll.setViewportView(tbTanpa);

        JPanel isi = new JPanel(new BorderLayout());
        isi.add(atas, BorderLayout.NORTH);
        isi.add(scroll, BorderLayout.CENTER);
        return isi;
    }

    private widget.Label labelKecil(String teks) {
        widget.Label l = new widget.Label();
        l.setText(teks);
        return l;
    }

    private void aturLebar(widget.Table tabel, int[] lebar) {
        for (int i = 0; i < lebar.length; i++) {
            TableColumn kolom = tabel.getColumnModel().getColumn(i);
            kolom.setPreferredWidth(lebar[i]);
        }
    }

    /** Satu baris = satu berkas retensi. Nomor RM boleh muncul lebih dari sekali
     *  karena satu pasien bisa punya beberapa berkas yang diretensi. */
    private void tampilSudahRetensi() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        modeSudah.setRowCount(0);
        String kunci = "%" + TCariSudah.getText().trim() + "%";
        String sql =
              "select rt.no_rkm_medis, ifnull(p.nm_pasien,'') nm_pasien, ifnull(p.no_ktp,'') no_ktp, "
            + "       ifnull(p.jk,'') jk, ifnull(p.tgl_lahir,'') tgl_lahir, ifnull(p.alamat,'') alamat, "
            + "       ifnull(rt.terakhir_daftar,'') terakhir_daftar, ifnull(rt.tgl_retensi,'') tgl_retensi, "
            + "       ifnull(rt.lokasi_pdf,'') lokasi_pdf "
            + "from retensi_pasien rt left join pasien p on p.no_rkm_medis = rt.no_rkm_medis "
            + "where rt.no_rkm_medis like ? or p.nm_pasien like ? or p.alamat like ? "
            + "order by rt.tgl_retensi desc, rt.no_rkm_medis";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, kunci);
            ps.setString(2, kunci);
            ps.setString(3, kunci);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modeSudah.addRow(new Object[]{
                        rs.getString("no_rkm_medis"), rs.getString("nm_pasien"), rs.getString("no_ktp"),
                        rs.getString("jk"), rs.getString("tgl_lahir"), rs.getString("alamat"),
                        rs.getString("terakhir_daftar"), rs.getString("tgl_retensi"), rs.getString("lokasi_pdf")
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        lblJumlahSudah.setText("" + modeSudah.getRowCount());
        setCursor(Cursor.getDefaultCursor());
    }

    /** Kandidat retensi: tidak ada kunjungan reg_periksa selama N tahun terakhir.
     *
     *  Pasien yang belum pernah registrasi sama sekali dinilai dari pasien.tgl_daftar,
     *  supaya pasien yang baru didaftarkan tahun ini tidak ikut terjaring hanya
     *  karena belum sempat berkunjung.
     *
     *  Status retensi dicek dengan EXISTS, bukan JOIN: retensi_pasien punya nomor RM
     *  ganda (2394 baris untuk 1818 RM) sehingga JOIN akan menggandakan baris pasien. */
    private void tampilTanpaKunjungan() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        modeTanpa.setRowCount(0);
        int tahun = 5;
        try {
            tahun = Integer.parseInt(cmbTahun.getSelectedItem().toString());
        } catch (Exception e) {
            tahun = 5;
        }
        String kunci = "%" + TCariTanpa.getText().trim() + "%";
        String sql =
              "select p.no_rkm_medis, ifnull(p.nm_pasien,'') nm_pasien, ifnull(p.no_ktp,'') no_ktp, "
            + "       ifnull(p.jk,'') jk, ifnull(p.tgl_lahir,'') tgl_lahir, ifnull(p.alamat,'') alamat, "
            + "       ifnull(t.terakhir, p.tgl_daftar) aktivitas, "
            + "       if(t.terakhir is null,'Pendaftaran','Kunjungan') sumber, "
            + "       timestampdiff(year, ifnull(t.terakhir, p.tgl_daftar), curdate()) lama, "
            + "       if(exists(select 1 from retensi_pasien rt where rt.no_rkm_medis = p.no_rkm_medis),"
            + "          'Sudah','Belum') status_retensi "
            + "from pasien p "
            + "left join (select no_rkm_medis, max(tgl_registrasi) terakhir from reg_periksa group by no_rkm_medis) t "
            + "  on t.no_rkm_medis = p.no_rkm_medis "
            + "where ifnull(t.terakhir, p.tgl_daftar) <= date_sub(curdate(), interval " + tahun + " year) "
            + "  and (p.no_rkm_medis like ? or p.nm_pasien like ? or p.alamat like ?) "
            + (chkBelumSaja.isSelected()
                ? "  and not exists(select 1 from retensi_pasien rt where rt.no_rkm_medis = p.no_rkm_medis) " : "")
            + "order by aktivitas, p.no_rkm_medis";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            ps.setString(1, kunci);
            ps.setString(2, kunci);
            ps.setString(3, kunci);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    modeTanpa.addRow(new Object[]{
                        rs.getString("no_rkm_medis"), rs.getString("nm_pasien"), rs.getString("no_ktp"),
                        rs.getString("jk"), rs.getString("tgl_lahir"), rs.getString("alamat"),
                        rs.getString("aktivitas"), rs.getString("sumber"), rs.getString("lama"),
                        rs.getString("status_retensi")
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Notifikasi : " + e);
        }
        lblJumlahTanpa.setText("" + modeTanpa.getRowCount());
        setCursor(Cursor.getDefaultCursor());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("::[ About Program ]::");
        setUndecorated(true);
        setResizable(false);
        addWindowStateListener(new java.awt.event.WindowStateListener() {
            public void windowStateChanged(java.awt.event.WindowEvent evt) {
                formWindowStateChanged(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Retensi Data Rekam Medis Pasien ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50,50,50))); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout());
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        Platform.setImplicitExit(false);
    }//GEN-LAST:event_formWindowClosed

    private void formWindowStateChanged(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowStateChanged
        if(this.isActive()==false){
            Platform.setImplicitExit(false);
        }
    }//GEN-LAST:event_formWindowStateChanged

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgRetensi dialog = new DlgRetensi(new javax.swing.JFrame(), true);
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
    private widget.InternalFrame internalFrame1;
    // End of variables declaration//GEN-END:variables


}
