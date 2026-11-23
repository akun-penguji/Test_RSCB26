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

import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import bridging.EsignService;
import bridging.UploadPDFTte;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import javax.swing.JPasswordField;
import bridging.SertisignMultiSignerService;
import bridging.SignerConfig;
import java.util.List;
import javax.swing.SwingWorker;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import fungsi.akses;

/**
 *
 * @author perpustakaan
 */
public class DlgBerkasRawat extends javax.swing.JDialog {
    private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;
 
    private final JPanel panel = new JPanel(new BorderLayout());
    private final JLabel lblStatus = new JLabel();

    private final JTextField txtURL = new JTextField();
    private final JProgressBar progressBar = new JProgressBar();
    private final validasi Valid=new validasi();
    private final sekuel Sequel=new sekuel();
    private String halaman="",norawat="";
    private PreparedStatement ps;
    private ResultSet rs;
    private final Connection koneksi=koneksiDB.condb();
    public DlgBerkasRawat(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initComponents2();
    }
    
    public void setNoRawat(String no){ 
        norawat=no; 
    }
    
    private void initComponents2() {           
        txtURL.addActionListener((ActionEvent e) -> {
            loadURL(txtURL.getText());
        });
  
        progressBar.setPreferredSize(new Dimension(150, 18));
        progressBar.setStringPainted(true);
        
        panel.add(jfxPanel, BorderLayout.CENTER);
        
        internalFrame1.setLayout(new BorderLayout());
        internalFrame1.add(panel);        
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
                        DlgBerkasRawat.this.setTitle(newValue);
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
                                if(engine.getLocation().replaceAll("http://"+koneksiDB.HOSTHYBRIDWEB()+":"+koneksiDB.PORTWEB()+"/"+koneksiDB.HYBRIDWEB()+"/","").contains("action=GABUNG_SIMPLE")){
                                    // Extract no_rawat from URL if present
                                    System.out.println("DEBUG: Instance norawat before logic: " + norawat);
                                    
                                    // 1. Force refresh from database (Reliable source from list.php)
                                    norawat = Sequel.cariIsi("select temppanggilnorawat.no_rawat from temppanggilnorawat");
                                    System.out.println("DEBUG: Fetched norawat (GABUNG_SIMPLE) from DB: " + norawat);

                                    // 2. Extra safety: Check URL if DB is somehow empty (though unlikely with fixes)
                                    if(norawat.trim().equals("")){
                                        String currentUrl = engine.getLocation();
                                        if (currentUrl.contains("no_rawat=")) {
                                            try {
                                                String extracted = currentUrl.substring(currentUrl.indexOf("no_rawat=") + 9);
                                                if (extracted.contains("&")) {
                                                    extracted = extracted.substring(0, extracted.indexOf("&"));
                                                }
                                                norawat = extracted;
                                                System.out.println("DEBUG: Extracted norawat from URL fallback: " + norawat);
                                            } catch (Exception e) {}
                                        }
                                    }
                                    
                                    System.out.println("DEBUG: Final norawat used for query: " + norawat);
                                    
                                    // Simple PDF merge without TTE/upload workflow
                                    ps=koneksi.prepareStatement("SELECT berkas_digital_perawatan.lokasi_file "+
                                                  "from berkas_digital_perawatan inner join master_berkas_digital "+
                                                  "on berkas_digital_perawatan.kode=master_berkas_digital.kode "+
                                                  "where berkas_digital_perawatan.no_rawat=? ORDER BY master_berkas_digital.nama ASC ");
                                    try {
                                        PDFMergerUtility ut = new PDFMergerUtility();
                                        URL url;
                                        ps.setString(1,norawat);
                                        rs=ps.executeQuery();
                                        int fileCount = 0;
                                        int successCount = 0;
                                        while(rs.next()){
                                            fileCount++;
                                            String lokasi = rs.getString("lokasi_file");
                                            try {
                                                // Sanitization: Remove duplicate berkasrawat prefix if present
                                                String cleanLokasi = lokasi;
                                                if (cleanLokasi.startsWith("berkasrawat/")) {
                                                    cleanLokasi = cleanLokasi.substring("berkasrawat/".length());
                                                }
                                                if (cleanLokasi.startsWith("/")) {
                                                    cleanLokasi = cleanLokasi.substring(1);
                                                }

                                                System.out.println("Processing file (" + fileCount + "): " + lokasi);
                                                String path = "/"+koneksiDB.HYBRIDWEB()+"/berkasrawat/"+cleanLokasi;
                                                InputStream is = getStreamFromUrl(koneksiDB.HOSTHYBRIDWEB(), Integer.parseInt(koneksiDB.PORTWEB()), path);
                                                ut.addSource(is);
                                                successCount++;
                                            } catch (Exception e) {
                                                System.out.println("⚠️ SKIP: File not found or error accessing: " + lokasi + " (" + e.getMessage() + ")");
                                            }
                                        }
                                        System.out.println("Merge Summary: " + successCount + " success out of " + fileCount + " total files.");
                                        
                                        if (successCount > 0) {
                                            ut.setDestinationFileName("merge.pdf");
                                            ut.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
                                            System.out.println("Merge successful, file saved to: merge.pdf");
                                            if (successCount < fileCount) {
                                                 JOptionPane.showMessageDialog(null, "Gabung PDF Selesai.\n" + (fileCount - successCount) + " file dilewati/gagal diakses (Cek Console).");
                                            }
                                        } else {
                                            System.out.println("No valid files found to merge.");
                                            JOptionPane.showMessageDialog(null, "Tidak ada berkas PDF yang VALID untuk digabungkan.");
                                        }
                                        
                                        Properties systemProp = System.getProperties();
                                        String currentDir = systemProp.getProperty("user.dir");
                                        File dir = new File(currentDir);
                                        
                                        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                                        Valid.panggilUrl2(dir+"/merge.pdf");
                                        setCursor(Cursor.getDefaultCursor());
                                    } catch (Exception e) {
                                        System.out.println("Notif : "+e);
                                        JOptionPane.showMessageDialog(null,"Gagal menggabungkan file: "+e.toString() + "\nCek apakah file valid dan akses server benar.");
                                    } finally{
                                        if(rs!=null){
                                            rs.close();
                                        }
                                        if(ps!=null){
                                            ps.close();
                                        }
                                    }


                                } else if(engine.getLocation().replaceAll("http://"+koneksiDB.HOSTHYBRIDWEB()+":"+koneksiDB.PORTWEB()+"/"+koneksiDB.HYBRIDWEB()+"/","").contains("action=GABUNG_SERTISIGN")){
                                    // Extract no_rawat from URL if present
                                    System.out.println("DEBUG: Instance norawat before logic: " + norawat);
                                    
                                    // 1. Force refresh from database
                                    norawat = Sequel.cariIsi("select temppanggilnorawat.no_rawat from temppanggilnorawat");
                                    System.out.println("DEBUG: Fetched norawat (GABUNG_SERTISIGN) from DB: " + norawat);

                                    // 2. Extra safety: Check URL
                                    if(norawat.trim().equals("")){
                                        String currentUrl = engine.getLocation();
                                        if (currentUrl.contains("no_rawat=")) {
                                            try {
                                                String extracted = currentUrl.substring(currentUrl.indexOf("no_rawat=") + 9);
                                                if (extracted.contains("&")) {
                                                    extracted = extracted.substring(0, extracted.indexOf("&"));
                                                }
                                                norawat = extracted;
                                            } catch (Exception e) {}
                                        }
                                    }
                                    
                                    System.out.println("DEBUG: Final norawat used for query: " + norawat);

                                    // TTE Sertisign Multi-Signer
                                    ps=koneksi.prepareStatement("SELECT berkas_digital_perawatan.lokasi_file "+
                                                  "from berkas_digital_perawatan inner join master_berkas_digital "+
                                                  "on berkas_digital_perawatan.kode=master_berkas_digital.kode "+
                                                  "where berkas_digital_perawatan.no_rawat=? ORDER BY master_berkas_digital.nama ASC ");
                                    try {
                                        PDFMergerUtility ut = new PDFMergerUtility();
                                        URL url;
                                        ps.setString(1,norawat);
                                        rs=ps.executeQuery();
                                        int fileCount = 0;
                                        int successCount = 0;
                                        while(rs.next()){
                                            fileCount++;
                                            String lokasi = rs.getString("lokasi_file");
                                            try {
                                                String cleanLokasi = lokasi;
                                                if (cleanLokasi.startsWith("berkasrawat/")) {
                                                    cleanLokasi = cleanLokasi.substring("berkasrawat/".length());
                                                }
                                                if (cleanLokasi.startsWith("/")) {
                                                    cleanLokasi = cleanLokasi.substring(1);
                                                }

                                                System.out.println("Processing file (" + fileCount + "): " + lokasi);
                                                String path = "/"+koneksiDB.HYBRIDWEB()+"/berkasrawat/"+cleanLokasi;
                                                InputStream is = getStreamFromUrl(koneksiDB.HOSTHYBRIDWEB(), Integer.parseInt(koneksiDB.PORTWEB()), path);
                                                ut.addSource(is);
                                                successCount++;
                                            } catch (Exception e) {
                                                System.out.println("⚠️ SKIP (Sertisign): File not found or error accessing: " + lokasi + " (" + e.getMessage() + ")");
                                            }
                                        }
                                        
                                        if (successCount > 0) {
                                            ut.setDestinationFileName("merge.pdf");
                                            ut.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
                                            System.out.println("Merge successful (Sertisign), file saved to: merge.pdf");
                                            String msg = "Proses gabung file selesai..!";
                                            if (successCount < fileCount) {
                                                msg += "\n" + (fileCount - successCount) + " file dilewati karena gagal diakses.";
                                            }
                                            JOptionPane.showMessageDialog(null, msg); // Show regular success message first
                                            
                                            // Then ask for TTE Multi-Signer
                                            Properties systemProp = System.getProperties();
                                            String currentDir = systemProp.getProperty("user.dir");
                                            File dir = new File(currentDir);
                                            
                                            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                                            Valid.panggilUrl2(dir+"/merge.pdf");
                                            setCursor(Cursor.getDefaultCursor());
                                            
                                            int confirm = JOptionPane.showConfirmDialog(null,
                                                "PDF berhasil digabungkan!\n\nApakah Anda ingin melakukan TTE Multi-Signer?",
                                                "TTE Multi-Signer",
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.QUESTION_MESSAGE);
                                            
                                            if (confirm == JOptionPane.YES_OPTION) {
                                                processTteMultiSigner(norawat, dir+"/merge.pdf");
                                            }

                                        } else {
                                            System.out.println("No valid files found to merge (Sertisign).");
                                            JOptionPane.showMessageDialog(null, "Tidak ada berkas PDF yang VALID untuk digabungkan.");
                                        }
                                        
                                    } catch (Exception e) {
                                        System.out.println("Notif : "+e);
                                        JOptionPane.showMessageDialog(null,"Gagal menggabungkan file: "+e.toString() + "\nCek apakah file valid dan akses server benar.");
                                    } finally{
                                        if(rs!=null){
                                            rs.close();
                                        }
                                        if(ps!=null){
                                            ps.close();
                                        }
                                    }

                                } else if(engine.getLocation().replaceAll("http://"+koneksiDB.HOSTHYBRIDWEB()+":"+koneksiDB.PORTWEB()+"/"+koneksiDB.HYBRIDWEB()+"/","").contains("action=GABUNG")){
                                    // Extract no_rawat from URL if present
                                    // Extract no_rawat from URL if present
                                    // Extract no_rawat from URL if present
                                    // 1. Force refresh from database
                                    norawat = Sequel.cariIsi("select temppanggilnorawat.no_rawat from temppanggilnorawat");
                                    System.out.println("DEBUG: Fetched norawat (GABUNG) from DB: " + norawat);

                                    // 2. Extra safety: Check URL
                                    if(norawat.trim().equals("")){
                                        String currentUrl = engine.getLocation();
                                        if (currentUrl.contains("no_rawat=")) {
                                            try {
                                                String extracted = currentUrl.substring(currentUrl.indexOf("no_rawat=") + 9);
                                                if (extracted.contains("&")) {
                                                    extracted = extracted.substring(0, extracted.indexOf("&"));
                                                }
                                                norawat = extracted;
                                            } catch (Exception e) {}
                                        }
                                    }

                                    // Cek Draft Estafet di Server (Table berkas_klaim)
                                    String draftFile = Sequel.cariIsi("select lokasi_file from berkas_klaim where no_rawat='"+norawat+"'");
                                    boolean resumeEstafet = false;
                                    
                                    if (!draftFile.equals("")) {
                                        int resume = JOptionPane.showConfirmDialog(null, "Ditemukan dokumen Estafet yang belum selesai di berkas_klaim.\nLanjutkan penandatanganan dokumen tersebut?", "Resume Estafet", JOptionPane.YES_NO_OPTION);
                                        if (resume == JOptionPane.YES_OPTION) {
                                            try {
                                                // Download draft file from berkasklaim folder
                                                URI uriDraft = new URI("http", null, koneksiDB.HOSTHYBRIDWEB(), Integer.parseInt(koneksiDB.PORTWEB()), "/"+koneksiDB.HYBRIDWEB()+"/berkasklaim/"+draftFile, null, null);
                                                URL urlDraft = uriDraft.toURL();
                                                Properties systemProp = System.getProperties();
                                                String currentDir = systemProp.getProperty("user.dir");
                                                File f = new File(currentDir+"/merge.pdf");
                                                FileUtils.copyURLToFile(urlDraft, f);
                                                resumeEstafet = true;
                                            } catch (Exception e) {
                                                System.out.println("Gagal download draft: "+e);
                                                resumeEstafet = false;
                                            }
                                        }
                                    }

                                    // List untuk menyimpan antrian signer (Estafet Otomatis)
                                    java.util.LinkedHashSet<String> signerSet = new java.util.LinkedHashSet<>();
                                    
                                    ps=koneksi.prepareStatement("SELECT berkas_digital_perawatan.lokasi_file, berkas_digital_perawatan.nip "+
                                                  "from berkas_digital_perawatan inner join master_berkas_digital "+
                                                  "on berkas_digital_perawatan.kode=master_berkas_digital.kode "+
                                                  "where berkas_digital_perawatan.no_rawat=? ORDER BY master_berkas_digital.nama ASC ");
                                    try {
                                        // Merge if not resuming
                                        if (!resumeEstafet) {
                                            PDFMergerUtility ut = new PDFMergerUtility();
                                            URL url;
                                            ps.setString(1,norawat);
                                            rs=ps.executeQuery();
                                            int fileCount = 0;
                                            int successCount = 0;
                                            while(rs.next()){
                                                fileCount++;
                                                String lokasi = rs.getString("lokasi_file");
                                                try {
                                                    String cleanLokasi = lokasi;
                                                    if (cleanLokasi.startsWith("berkasrawat/")) {
                                                        cleanLokasi = cleanLokasi.substring("berkasrawat/".length());
                                                    }
                                                    if (cleanLokasi.startsWith("/")) {
                                                        cleanLokasi = cleanLokasi.substring(1);
                                                    }

                                                    System.out.println("Processing file (" + fileCount + "): " + lokasi);
                                                    String path = "/"+koneksiDB.HYBRIDWEB()+"/berkasrawat/"+cleanLokasi;
                                                    InputStream is = getStreamFromUrl(koneksiDB.HOSTHYBRIDWEB(), Integer.parseInt(koneksiDB.PORTWEB()), path);
                                                    ut.addSource(is);
                                                    successCount++;
                                                    
                                                    String nip = rs.getString("nip");
                                                    if (nip != null && !nip.trim().equals("") && !nip.equals("-")) {
                                                        signerSet.add(nip);
                                                    }
                                                } catch (Exception e) {
                                                    System.out.println("⚠️ SKIP (GABUNG): File not found or error accessing: " + lokasi + " (" + e.getMessage() + ")");
                                                }
                                            }
                                            
                                            System.out.println("Merge Summary (GABUNG): " + successCount + " success out of " + fileCount + " total.");
                                            
                                            if (successCount > 0) {
                                                ut.setDestinationFileName("merge.pdf");
                                                ut.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
                                                System.out.println("Merge successful (GABUNG), file saved to: merge.pdf");
                                                String msg = "Proses gabung file selesai..!";
                                                if (successCount < fileCount) {
                                                    msg += "\n" + (fileCount - successCount) + " file dilewati karena gagal diakses.";
                                                }
                                                JOptionPane.showMessageDialog(null, msg);
                                            } else {
                                                System.out.println("No valid files found to merge (GABUNG).");
                                                JOptionPane.showMessageDialog(null, "Tidak ada berkas PDF yang VALID untuk digabungkan.");
                                            }
                                        } else {
                                            ps.setString(1,norawat);
                                            rs=ps.executeQuery();
                                            while(rs.next()){
                                                String nip = rs.getString("nip");
                                                if (nip != null && !nip.trim().equals("") && !nip.equals("-")) {
                                                    signerSet.add(nip);
                                                }
                                            }
                                        }
                                        
                                        Properties systemProp = System.getProperties();
                                        String currentDir = systemProp.getProperty("user.dir");
                                        File dir = new File(currentDir);

                                        // --- DIALOG PILIHAN 3 OPSI ---
                                        Object[] options = {"Lanjut TTE", "Kirim Ke Berkas Klaim", "Hanya Lihat PDF"};
                                        int choice = JOptionPane.showOptionDialog(null, 
                                            "Pilihan Tindakan untuk Berkas Gabungan:", "Opsi Gabung PDF",
                                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                                            null, options, options[0]);

                                        if (choice == 0) { // Lanjut TTE
                                            // --- MULAI LOGIKA TTE ESTAFET (SERIAL SIGNING) ---
                                            String filePath = dir + "/merge.pdf";
                                            
                                            java.util.ArrayList<String> signerQueue = new java.util.ArrayList<>(signerSet);
                                            boolean isAutoEstafet = !signerQueue.isEmpty();
                                            int currentIndex = 0;
                                            boolean lanjutTte = true;
                                            boolean isLastSigner = false; // Flag to detect if current user is last
                                            
                                            if (isAutoEstafet) {
                                                JOptionPane.showMessageDialog(null, "Terdeteksi " + signerQueue.size() + " penanda tangan dari berkas.\\nMode Estafet Berurutan Aktif.");
                                                // Check if current user is the last one in the queue
                                                if (signerQueue.indexOf(akses.getkode()) == signerQueue.size() - 1) {
                                                    isLastSigner = true;
                                                }
                                            } else {
                                                 // Manual mode - assume final if single signer
                                                 isLastSigner = true;
                                            }
                                            
                                            while (lanjutTte) {
                                            String targetNik = "";
                                            if (isAutoEstafet) {
                                                if (currentIndex >= signerQueue.size()) {
                                                    break; 
                                                }
                                                targetNik = signerQueue.get(currentIndex);
                                            } else {
                                                targetNik = akses.getkode(); 
                                            }
                                            
                                            String namaSigner = Sequel.cariIsi("select nm_dokter from dokter where kd_dokter='"+targetNik+"'");
                                            
                                            JTextField fieldNik = new JTextField();
                                            fieldNik.setText(targetNik);
                                            if (isAutoEstafet) fieldNik.setEditable(false); 
                                            
                                            JPasswordField fieldPass = new JPasswordField();
                                            JTextField fieldTag = new JTextField();
                                            
                                            String autoTag = "#";
                                            try {
                                                String dbTag = Sequel.cariIsi("select tag_signer from dokter where kd_dokter='"+targetNik+"'");
                                                if (!dbTag.equals("")) {
                                                    autoTag = dbTag;
                                                }
                                            } catch(Exception e) {
                                                System.out.println("Kolom tag_signer belum ada di tabel dokter (menggunakan default '#')");
                                            }
                                            fieldTag.setText(autoTag); 
                                            
                                            Object[] message = {
                                                "Giliran Tanda Tangan: " + (namaSigner.equals("") ? targetNik : namaSigner),
                                                "NIK Penanda Tangan:", fieldNik,
                                                "Passphrase:", fieldPass,
                                                "Tag Koordinat (Contoh: #, $, @):", fieldTag
                                            };

                                            int option = JOptionPane.showConfirmDialog(null, message, "Input Kredensial BSrE", JOptionPane.OK_CANCEL_OPTION);
                                            
                                            if (option == JOptionPane.OK_OPTION) {
                                                String userNik = fieldNik.getText();
                                                String passphrase = new String(fieldPass.getPassword());
                                                String tag = fieldTag.getText();
                                                
                                                if (!userNik.isEmpty() && !passphrase.isEmpty() && !tag.isEmpty()) {
                                                    String realNikBSrE = Sequel.cariIsi("select no_ktp from pegawai where nik='"+userNik+"'");
                                                    String namaDokter = Sequel.cariIsi("select nm_dokter from dokter where kd_dokter='"+userNik+"'");
                                                    
                                                    if(realNikBSrE.equals("") || namaDokter.equals("")){
                                                        JOptionPane.showMessageDialog(null, "User/Dokter tidak ditemukan (Cek NIK/Kode Dokter).");
                                                        if (!isAutoEstafet) continue; 
                                                    } else {
                                                        String tglSekarang = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
                                                        String linkQR = "Dikeluarkan di " + akses.getnamars() + ", Kabupaten/Kota " + akses.getkabupatenrs() + " dan ditandatangani secara elektronik oleh " + namaDokter + " ID " + userNik + " Tanggal " + tglSekarang;
 
                                                        String location = akses.getnamars() + ", " + akses.getkabupatenrs();
                                                        String reason = "Dokumen Ini Telah Disetujui dengan Tanda Tangan Elektronik";
                                                        boolean success = EsignService.prosesTTE(filePath, realNikBSrE, passphrase, tag, linkQR, location, reason);
                                                        
                                                        if (success) {
                                                            JOptionPane.showMessageDialog(null, "Tanda Tangan Berhasil ditambahkan!");
                                                            if (isAutoEstafet) currentIndex++; 
                                                            
                                                            if (isAutoEstafet && currentIndex >= signerQueue.size()) {
                                                                // We reached the end of the queue.
                                                                isLastSigner = true; 
                                                            }
                                                        } else {
                                                            JOptionPane.showMessageDialog(null, "Gagal melakukan Tanda Tangan. Cek Passphrase atau Koneksi.");
                                                        }
                                                    }
                                                } else {
                                                    JOptionPane.showMessageDialog(null, "Data tidak lengkap!");
                                                }
                                            } else {
                                                if (isAutoEstafet) {
                                                    int skip = JOptionPane.showConfirmDialog(null, "Batalkan seluruh proses Estafet?", "Batal", JOptionPane.YES_NO_OPTION);
                                                    if (skip == JOptionPane.YES_OPTION) {
                                                        break;
                                                    } else {
                                                        int skipSigner = JOptionPane.showConfirmDialog(null, "Lewati penanda tangan ini ("+namaSigner+")?", "Skip", JOptionPane.YES_NO_OPTION);
                                                        if (skipSigner == JOptionPane.YES_OPTION) currentIndex++;
                                                    }
                                                } else {
                                                    break; 
                                                }
                                            }
                                            
                                            if (!isAutoEstafet) {
                                                int more = JOptionPane.showConfirmDialog(null, "Apakah ada penanda tangan lain (Estafet)?", "Lanjut TTE?", JOptionPane.YES_NO_OPTION);
                                                if (more != JOptionPane.YES_OPTION) {
                                                    lanjutTte = false;
                                                }
                                            } 
                                        }
                                        
                                        // Auto-Detect Status Upload
                                        boolean isFinished = false;
                                        if (isAutoEstafet) {
                                            isFinished = (currentIndex >= signerQueue.size());
                                        } else {
                                            isFinished = true; // Manual mode assumes finished if loop exits normally
                                        }
                                        
                                        if (!isFinished) { // Masih Proses Sign -> Upload Draft
                                             String draftName = "ESTAFET_"+norawat+".pdf";
                                             try {
                                                 ps=koneksi.prepareStatement("create table if not exists berkas_klaim (no_rawat varchar(20), tgl_upload datetime, nama_berkas varchar(200), lokasi_file varchar(200), status varchar(20) DEFAULT 'MENUNGGU_TTD', PRIMARY KEY (no_rawat))");
                                                 ps.execute();
                                                 try { ps = koneksi.prepareStatement("ALTER TABLE berkas_klaim ADD COLUMN status varchar(20) DEFAULT 'MENUNGGU_TTD'"); ps.execute(); } catch(Exception ex){}
                                             } catch (Exception e) {
                                                 System.out.println("Gagal update table berkas_klaim: " + e);
                                             }
                                             
                                             boolean uploaded = uploadDraft(draftName, "merge.pdf"); 
                                             
                                             if (uploaded) {
                                                 JOptionPane.showMessageDialog(null, "Draft Estafet disimpan (Status: Draft/Menunggu TTD).");
                                                 if (Sequel.cariInteger("select count(no_rawat) from berkas_klaim where no_rawat='"+norawat+"'")>0) {
                                                     Sequel.mengedit("berkas_klaim", "no_rawat='"+norawat+"'", "tgl_upload='"+java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+"', nama_berkas='"+draftName+"', lokasi_file='"+draftName+"', status='MENUNGGU_TTD'");
                                                 } else {
                                                     Sequel.menyimpan("berkas_klaim", "'"+norawat+"','"+
                                                         java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+
                                                         "','"+draftName+"','"+draftName+"','MENUNGGU_TTD'");
                                                 }
                                             } else {
                                                 JOptionPane.showMessageDialog(null, "Gagal upload draft ke server.");
                                             }
                                        } else {
                                             // Dokumen Final -> Upload ke Nextcloud
                                            String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                            String dateOnly = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                            String noSep = Sequel.cariIsi("select no_sep from bridging_sep where no_rawat='"+norawat+"' limit 1");
                                            if(noSep.equals("")) noSep = "-";
                                            
                                            UploadPDFTte uploader = new UploadPDFTte("merge.pdf", noSep, dateOnly, norawat, "berkas", now, "NON-INACBG");
                                            uploader.setStatusOverride("SELESAI_TTE");
                                            
                                            if (uploader.upload()) {
                                                JOptionPane.showMessageDialog(null, "Upload Final Berhasil!");
                                                try { ps = koneksi.prepareStatement("ALTER TABLE berkas_klaim ADD COLUMN status varchar(20) DEFAULT 'MENUNGGU_TTD'"); ps.execute(); } catch(Exception ex){}
                                                
                                                if (Sequel.cariInteger("select count(no_rawat) from berkas_klaim where no_rawat='"+norawat+"'")>0) {
                                                     Sequel.mengedit("berkas_klaim", "no_rawat='"+norawat+"'", "status='SUDAH_TTD'");
                                                } else {
                                                    String draftName = "ESTAFET_"+norawat+".pdf";
                                                     Sequel.menyimpan("berkas_klaim", "'"+norawat+"','"+
                                                         java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+
                                                         "','"+draftName+"','"+draftName+"','SUDAH_TTD'");
                                                }
                                            } else {
                                                JOptionPane.showMessageDialog(null, "Upload Gagal.");
                                            }
                                        }
                                        // --- SELESAI LOGIKA TTE ---

                                        } else if (choice == 1) { // Kirim Ke Berkas Klaim
                                            System.out.println("=== DEBUG: Kirim Ke Berkas Klaim - START ===");
                                            System.out.println("No Rawat: " + norawat);
                                            
                                            String draftName = "ESTAFET_"+norawat+".pdf";
                                            System.out.println("Draft Name: " + draftName);
                                            
                                            // Pastikan table berkas_klaim ada
                                            try {
                                                System.out.println("Checking/Creating table berkas_klaim...");
                                                ps=koneksi.prepareStatement("create table if not exists berkas_klaim (no_rawat varchar(20), tgl_upload datetime, nama_berkas varchar(200), lokasi_file varchar(200), status varchar(20) DEFAULT 'MENUNGGU_TTD', PRIMARY KEY (no_rawat))");
                                                ps.execute();
                                                try { ps = koneksi.prepareStatement("ALTER TABLE berkas_klaim ADD COLUMN status varchar(20) DEFAULT 'MENUNGGU_TTD'"); ps.execute(); } catch(Exception ex){}
                                                System.out.println("Table berkas_klaim ready.");
                                            } catch (Exception e) {
                                                System.out.println("ERROR: Gagal membuat table berkas_klaim: " + e);
                                                e.printStackTrace();
                                            }
                                            
                                            // Upload ke folder berkasklaim (root/berkasklaim/)
                                            System.out.println("Starting upload to berkasklaim folder...");
                                            boolean uploaded = uploadDraft(draftName, "merge.pdf"); 
                                            System.out.println("Upload result: " + (uploaded ? "SUCCESS" : "FAILED"));
                                            
                                            if (uploaded) {
                                                System.out.println("Saving to database...");
                                                try {
                                                    // Simpan tracking di berkas_klaim
                                                    if (Sequel.cariInteger("select count(no_rawat) from berkas_klaim where no_rawat='"+norawat+"'")>0) {
                                                        System.out.println("Updating existing record in berkas_klaim...");
                                                        Sequel.mengedit("berkas_klaim", "no_rawat='"+norawat+"'", "tgl_upload='"+java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+"', nama_berkas='"+draftName+"', lokasi_file='"+draftName+"', status='MENUNGGU_TTD'");
                                                    } else {
                                                        System.out.println("Inserting new record to berkas_klaim...");
                                                        Sequel.menyimpan("berkas_klaim", "'"+norawat+"','"+
                                                            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))+
                                                            "','"+draftName+"','"+draftName+"','MENUNGGU_TTD'");
                                                    }
                                                    System.out.println("Database save SUCCESS.");
                                                    JOptionPane.showMessageDialog(null, "Berkas berhasil dikirim ke Berkas Klaim!\n\nFile: " + draftName + "\nStatus: MENUNGGU_TTD", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                                                } catch (Exception e) {
                                                    System.out.println("ERROR: Gagal menyimpan ke database: " + e);
                                                    e.printStackTrace();
                                                    JOptionPane.showMessageDialog(null, "Upload berhasil, tetapi gagal menyimpan ke database.\n\nError: " + e.getMessage(), "Peringatan", JOptionPane.WARNING_MESSAGE);
                                                }
                                            } else {
                                                System.out.println("ERROR: Upload failed. Check console for details.");
                                                JOptionPane.showMessageDialog(null, "Gagal mengirim berkas ke server!\n\nSilakan cek:\n1. Koneksi ke server\n2. Folder berkasklaim ada dan writable\n3. File upload.php ada di folder berkasklaim\n\nCek console untuk detail error.", "Upload Gagal", JOptionPane.ERROR_MESSAGE);
                                            }
                                            System.out.println("=== DEBUG: Kirim Ke Berkas Klaim - END ===");
                                        }
                                        // choice == 2 (Hanya Lihat PDF) - tidak ada aksi tambahan, PDF sudah dibuka di line 425

                                        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                                        Valid.panggilUrl2(dir+"/merge.pdf");
                                        setCursor(Cursor.getDefaultCursor());
                                    } catch (Exception e) {
                                        System.out.println("Notif : "+e);
                                        JOptionPane.showMessageDialog(null,"Gagal menggabungkan file: "+e.toString() + "\nCek apakah file valid dan akses server benar.");
                                    } finally{
                                        if(rs!=null){
                                            rs.close();
                                        }
                                        if(ps!=null){
                                            ps.close();
                                        }
                                    }                                    
                                } else if(engine.getLocation().replaceAll("http://"+koneksiDB.HOSTHYBRIDWEB()+":"+koneksiDB.PORTWEB()+"/"+koneksiDB.HYBRIDWEB()+"/","").contains("berkasrawat/pages/upload")){
                                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                                    Valid.panggilUrl(engine.getLocation().replaceAll("http://"+koneksiDB.HOSTHYBRIDWEB()+":"+koneksiDB.PORTWEB()+"/"+koneksiDB.HYBRIDWEB()+"/berkasrawat/pages/upload/","berkasrawat/").replaceAll("http://"+koneksiDB.HOSTHYBRIDWEB()+"/"+koneksiDB.HYBRIDWEB()+"/berkasrawat/pages/upload/","berkasrawat/"));
                                    engine.executeScript("history.back()");
                                    setCursor(Cursor.getDefaultCursor());
                                } else if(engine.getLocation().replaceAll("http://"+koneksiDB.HOSTHYBRIDWEB()+":"+koneksiDB.PORTWEB()+"/"+koneksiDB.HYBRIDWEB()+"/","").contains("action=Keluar")){
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

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50,50,50))); // NOI18N
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
            DlgBerkasRawat dialog = new DlgBerkasRawat(new javax.swing.JFrame(), true);
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

    public void setJudul(String Judul,String Pages){
        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), Judul, javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50,50,50))); 
        this.halaman=Pages;
    }
    public boolean uploadDraft(String fileName, String sourceFile) {
        System.out.println("--- uploadDraft() START ---");
        System.out.println("Target filename: " + fileName);
        System.out.println("Source file path: " + sourceFile);
        
        try {
            File fileToUpload = new File(sourceFile);
            if (!fileToUpload.exists()) {
                System.out.println("ERROR: File tidak ditemukan di path: " + sourceFile);
                System.out.println("Absolute path: " + fileToUpload.getAbsolutePath());
                return false;
            }
            
            long fileSize = fileToUpload.length();
            System.out.println("File found. Size: " + fileSize + " bytes (" + (fileSize/1024) + " KB)");

            CloseableHttpClient httpClient = HttpClients.createDefault();
            String url = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/berkasklaim/upload.php";
            System.out.println("Upload URL: " + url);
            
            HttpPost httpPost = new HttpPost(url);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody("file", fileToUpload, ContentType.APPLICATION_OCTET_STREAM, fileName);

            HttpEntity entity = builder.build();
            httpPost.setEntity(entity);

            System.out.println("Sending HTTP POST request...");
            CloseableHttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            String reasonPhrase = response.getStatusLine().getReasonPhrase();
            
            System.out.println("HTTP Response Status: " + statusCode + " " + reasonPhrase);
            
            if (statusCode == 200) {
                System.out.println("Upload SUCCESS!");
                System.out.println("--- uploadDraft() END (SUCCESS) ---");
                return true;
            } else {
                System.out.println("ERROR: Server returned non-200 status code.");
                System.out.println("--- uploadDraft() END (FAILED) ---");
                return false;
            }
        } catch (java.net.UnknownHostException e) {
            System.out.println("ERROR: Cannot resolve host - " + e.getMessage());
            System.out.println("Kemungkinan masalah: Hostname salah atau DNS tidak bisa resolve.");
            e.printStackTrace();
            return false;
        } catch (java.net.ConnectException e) {
            System.out.println("ERROR: Connection refused - " + e.getMessage());
            System.out.println("Kemungkinan masalah: Server tidak running atau port salah.");
            e.printStackTrace();
            return false;
        } catch (java.io.FileNotFoundException e) {
            System.out.println("ERROR: File not found - " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.out.println("ERROR: Gagal upload draft - " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Process TTE Multi-Signer for merged PDF
     * @param noRawat Nomor rawat pasien
     * @param pdfPath Path to merged PDF file
     */
    private void processTteMultiSigner(String noRawat, String pdfPath) {
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    publish("🔍 Mengambil data penanda tangan...");
                    
                    // Initialize service
                    SertisignMultiSignerService service = new SertisignMultiSignerService();
                    
                    // Get signers from berkas_digital_perawatan
                    List<SignerConfig> signers = service.getSignersFromBerkas(noRawat);
                    
                    if (signers == null || signers.isEmpty()) {
                        publish("❌ Tidak ada penanda tangan yang ditemukan");
                        JOptionPane.showMessageDialog(null,
                            "Tidak ada penanda tangan yang ditemukan untuk berkas ini.\\n" +
                            "Pastikan berkas digital memiliki NIP petugas/dokter.",
                            "TTE Multi-Signer",
                            JOptionPane.WARNING_MESSAGE);
                        return null;
                    }
                    
                    // Validate signers
                    StringBuilder invalidSigners = new StringBuilder();
                    for (SignerConfig signer : signers) {
                        if (!signer.isValid()) {
                            invalidSigners.append("- ").append(signer.getSignerName())
                                         .append(" (Email atau Tag Signer kosong)\\n");
                        }
                    }
                    
                    if (invalidSigners.length() > 0) {
                        publish("⚠️ Ada penanda tangan dengan data tidak lengkap");
                        JOptionPane.showMessageDialog(null,
                            "Penanda tangan berikut memiliki data tidak lengkap:\\n\\n" +
                            invalidSigners.toString() +
                            "\\nSilakan lengkapi Email dan Tag Signer di master Dokter/Petugas.",
                            "TTE Multi-Signer",
                            JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                    
                    publish("✅ Ditemukan " + signers.size() + " penanda tangan");
                    
                    // Show signer list for confirmation
                    StringBuilder signerList = new StringBuilder("Penanda tangan yang akan melakukan TTE:\\n\\n");
                    for (int i = 0; i < signers.size(); i++) {
                        SignerConfig signer = signers.get(i);
                        signerList.append((i + 1)).append(". ")
                                 .append(signer.getSignerName())
                                 .append(" (").append(signer.getEmail()).append(")")
                                 .append("\\n   Keyword: ").append(signer.getTagSigner())
                                 .append("\\n");
                    }
                    signerList.append("\\nLanjutkan proses TTE?");
                    
                    int confirm = JOptionPane.showConfirmDialog(null,
                        signerList.toString(),
                        "Konfirmasi TTE Multi-Signer",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                    
                    if (confirm != JOptionPane.YES_OPTION) {
                        publish("❌ Proses TTE dibatalkan oleh user");
                        return null;
                    }
                    
                    publish("📤 Mengirim ke Sertisign API...");
                    
                    // Send to Sertisign
                    File pdfFile = new File(pdfPath);
                    String transactionId = service.sendMultiSignerRequest(
                        pdfFile,
                        signers,
                        40, // QR width
                        40, // QR height
                        fungsi.akses.getnamars(),
                        "" // kabupaten not needed for new QR format
                    );
                    
                    publish("✅ Transaction ID: " + transactionId);
                    
                    // Get SEP
                    String noSep = Sequel.cariIsi("select bridging_sep.no_sep from bridging_sep where bridging_sep.no_rawat=?", noRawat);
                    if (noSep == null || noSep.trim().isEmpty()) {
                        noSep = noRawat.replaceAll("/", "");
                    }
                    
                    // Save to database
                    publish("💾 Menyimpan status TTE ke database...");
                    
                    String documentName = "Berkas_Merged_" + noRawat.replaceAll("/", "_") + "_TTE.pdf";
                    String urlCallback = koneksiDB.URLDOKUMENSERTISIGN() + "/" + transactionId + ".pdf";
                    
                    service.saveStatusTte(noRawat, noSep, transactionId, documentName, 
                                        urlCallback, fungsi.akses.getkode());
                    service.saveSigners(transactionId, signers);
                    
                    publish("⏳ Menunggu signed PDF dari Sertisign...");
                    
                    // Download signed PDF - use No SEP as filename
                    String signedPdfFilename = (noSep.isEmpty() ? "Berkas_Merged_TTE" : noSep) + "_SIGNED.pdf";
                    String signedPdfPath = signedPdfFilename;
                    boolean downloadSuccess = service.downloadSignedPdf(
                        transactionId,
                        signedPdfPath,
                        10, // max retries
                        3000 // retry delay ms
                    );
                    
                    if (downloadSuccess) {
                        service.updateStatus(transactionId, "SIGNED", null);
                        publish("✅ TTE Multi-Signer berhasil!");
                        
                        // Upload to Nextcloud
                        publish("☁️ Uploading to Nextcloud...");
                        try {
                            // Get tanggal from reg_periksa
                            String tanggal = Sequel.cariIsi(
                                "SELECT DATE_FORMAT(tgl_registrasi, '%Y-%m-%d') FROM reg_periksa WHERE no_rawat=?",
                                noRawat
                            );
                            
                            if (tanggal == null || tanggal.isEmpty()) {
                                tanggal = new java.text.SimpleDateFormat("yyyy-MM-dd")
                                    .format(new java.util.Date());
                            }
                            
                            // Use No SEP as filename for better identification
                            String uploadFilename = noSep.isEmpty() ? "Berkas_Merged_TTE" : noSep;
                            
                            UploadPDFTte uploader = new UploadPDFTte(
                                signedPdfPath,
                                uploadFilename,  // Use No SEP as filename
                                tanggal,
                                noRawat,
                                "BRM",           // kode berkas merged
                                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()),
                                "INA-CBG"
                            );
                            
                            boolean uploadResult = uploader.upload();
                            
                            if (uploadResult) {
                                publish("✅ Upload to Nextcloud berhasil!");
                            } else {
                                publish("⚠️ Upload to Nextcloud gagal");
                            }
                        } catch (Exception uploadEx) {
                            publish("⚠️ Upload to Nextcloud error: " + uploadEx.getMessage());
                            System.err.println("Nextcloud upload error: " + uploadEx.getMessage());
                        }
                        
                        // Open signed PDF
                        File signedFile = new File(signedPdfPath);
                        Valid.panggilUrl2(signedFile.getAbsolutePath());
                        
                        JOptionPane.showMessageDialog(null,
                            "🎉 TTE Multi-Signer berhasil!\\n\\n" +
                            "File: " + signedPdfPath + "\\n" +
                            "Jumlah penanda tangan: " + signers.size() + "\\n" +
                            "Transaction ID: " + transactionId + "\\n" +
                            "Upload Nextcloud: Berhasil",
                            "TTE Multi-Signer Berhasil",
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        service.updateStatus(transactionId, "FAILED", "Download signed PDF failed after retries");
                        publish("❌ Gagal download signed PDF");
                        
                        JOptionPane.showMessageDialog(null,
                            "Gagal mendownload signed PDF dari Sertisign.\\n" +
                            "Transaction ID: " + transactionId + "\\n\\n" +
                            "Silakan coba download manual dari:\\n" + urlCallback,
                            "TTE Multi-Signer Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (Exception e) {
                    publish("❌ Error: " + e.getMessage());
                    e.printStackTrace();
                    
                    JOptionPane.showMessageDialog(null,
                        "Terjadi kesalahan saat proses TTE Multi-Signer:\\n\\n" + e.getMessage(),
                        "TTE Multi-Signer Error",
                        JOptionPane.ERROR_MESSAGE);
                }
                
                return null;
            }
            
            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    System.out.println(message);
                }
            }
        };
        
        worker.execute();
    }
    
    private java.io.InputStream getStreamFromUrl(String host, int port, String path) throws Exception {
        Exception lastException = null;
        
        // Disable SSL Validation for internal/trusted networks
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[] { 
            new javax.net.ssl.X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
            } 
        };

        try {
            javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(new javax.net.ssl.HostnameVerifier() {
                 public boolean verify(String hostname, javax.net.ssl.SSLSession session) { return true; }
            });
        } catch (Exception e) {
            System.out.println("SSL Bypass setup failed: " + e.getMessage());
        }
        
        // 1. Try HTTP
        try {
            System.out.println("Attempting connection via HTTP...");
            URI uri = new URI("http", null, host, port, path, null, null);
            URL url = uri.toURL();
            java.io.InputStream is = url.openStream();
            System.out.println("Connected via HTTP: " + url.toString());
            return is;
        } catch (Exception e) {
            System.out.println("HTTP connection failed: " + e.getMessage());
            lastException = e;
        }
        
        // 2. Try HTTPS (Fallback)
        try {
            System.out.println("Retrying connection via HTTPS (Trust All)...");
            int httpsPort = (port == 80) ? 443 : port;
            
            URI uri = new URI("https", null, host, httpsPort, path, null, null);
            URL url = uri.toURL();
            
            javax.net.ssl.HttpsURLConnection conn = (javax.net.ssl.HttpsURLConnection) url.openConnection();
            // Apply trust manager specifically to this connection if possible, or assume defaults set above work
            conn.setHostnameVerifier(new javax.net.ssl.HostnameVerifier() {
                 public boolean verify(String hostname, javax.net.ssl.SSLSession session) { return true; }
            });
            
            java.io.InputStream is = conn.getInputStream();
            System.out.println("Connected via HTTPS: " + url.toString());
            return is;
        } catch (Exception e) {
            System.out.println("HTTPS connection also failed: " + e.getMessage());
            if (lastException != null) throw lastException; // Throw original HTTP error if both fail, or maybe the HTTPS one
            throw e;
        }
    }
}
