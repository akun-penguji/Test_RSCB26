package laporan;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.net.URI;
import java.awt.Desktop;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import bridging.EsignServiceV2;
import bridging.PDFTextLocationHelper;
import bridging.PDFTextLocationHelper.TextLocation;
import bridging.EsignServiceV2.SignatureProperty;
import bridging.SignerConfig;
import bridging.SertisignMultiSignerService;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import bridging.UploadPDFTte;
import bridging.TagSignerDetector;
import bridging.TagSignerDetector.TagCoordinate;
import bridging.PDFTextLocationHelper.TextLocation;
import java.util.Map;

/**
 *
 * @author khanza
 */
public class DlgBerkasKlaim extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i=0;

    private kepegawaian.DlgCariDokter dokter=new kepegawaian.DlgCariDokter(null,false);
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JMenuItem MnTTESingle;
    private javax.swing.JMenuItem MnTTESingleV2;
    private javax.swing.JMenuItem MnLihatTTE;

    /** Creates new form DlgBerkasKlaim */
    public DlgBerkasKlaim(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        isCek();

        Object[] row={"No.Rawat","No.RM","Nama Pasien","Tgl.Upload","Nama Berkas","Lokasi File","Dokter / Signer","Status"};
        tabMode=new DefaultTableModel(null,row){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbBerkas.setModel(tabMode);

        tbBerkas.setPreferredScrollableViewportSize(new Dimension(800,800));
        tbBerkas.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 8; i++) {
            TableColumn column = tbBerkas.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(105);
            }else if(i==1){
                column.setPreferredWidth(70);
            }else if(i==2){
                column.setPreferredWidth(200);
            }else if(i==3){
                column.setPreferredWidth(120);
            }else if(i==4){
                column.setPreferredWidth(200);
            }else if(i==5){
                column.setPreferredWidth(250);
            }else if(i==6){
                column.setPreferredWidth(200);
            }else if(i==7){
                column.setPreferredWidth(100);
            }
        }
        tbBerkas.setDefaultRenderer(Object.class, new WarnaTable());

        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
            });
        }
        
        dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(dokter.getTable().getSelectedRow()!= -1){
                    KdDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                    NmDokter.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                    tampil();
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
        
        try {
            // Pastikan table berkas_klaim ada dengan kolom status
            ps=koneksi.prepareStatement("create table if not exists berkas_klaim (no_rawat varchar(20), tgl_upload datetime, nama_berkas varchar(200), lokasi_file varchar(200), status varchar(20) DEFAULT 'MENUNGGU_TTD', PRIMARY KEY (no_rawat))");
            ps.execute();
            // Ensure status column exists (manual alter if needed in dev)
            try { ps = koneksi.prepareStatement("ALTER TABLE berkas_klaim ADD COLUMN status varchar(20) DEFAULT 'MENUNGGU_TTD'"); ps.execute(); } catch(Exception ex){}
        } catch (Exception e) {
            System.out.println(e);
        }

        WindowListener input = new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {
                tampil();
            }
            @Override
            public void windowDeactivated(WindowEvent e) {}
        };
        addWindowListener(input);

        jPopupMenu1 = new javax.swing.JPopupMenu();
        
        javax.swing.JMenuItem MnLihat = new javax.swing.JMenuItem();
        MnLihat.setText("Lihat Dokumen");
        MnLihat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Preview.png")));
        MnLihat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if(tbBerkas.getSelectedRow() != -1){
                    String lokasiFile = tbBerkas.getValueAt(tbBerkas.getSelectedRow(), 5).toString();
                    if(!lokasiFile.isEmpty()){
                        String cleanPath = lokasiFile;
                        if(cleanPath.startsWith("/")){
                            cleanPath = cleanPath.substring(1);
                        }
                        String pdfUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + 
                                    "/" + koneksiDB.HYBRIDWEB() + "/berkasklaim/" + cleanPath;
                        try {
                            java.awt.Desktop.getDesktop().browse(new java.net.URI(pdfUrl));
                        } catch (Exception e) {
                            System.out.println("Error membuka browser: " + e.getMessage());
                        }
                    }
                }
            }
        });
        jPopupMenu1.add(MnLihat);
        
        MnTTESingle = new javax.swing.JMenuItem();
        MnTTESingle.setText("TTE Single (User Login)");
        MnTTESingle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/file-edit-16x16.png")));
        MnTTESingle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnTTESingleActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnTTESingle);
        
        MnTTESingleV2 = new javax.swing.JMenuItem();
        MnTTESingleV2.setText("TTE Single V2");
        MnTTESingleV2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/file-edit-16x16.png")));
        MnTTESingleV2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnTTESingleV2ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnTTESingleV2);
        
        MnLihatTTE = new javax.swing.JMenuItem();
        MnLihatTTE.setText("Dokumen Yang Sudah di TTE");
        MnLihatTTE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        MnLihatTTE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if(tbBerkas.getSelectedRow() != -1){
                    String noRawat = tbBerkas.getValueAt(tbBerkas.getSelectedRow(), 0).toString();
                    try {
                        String filePath = Sequel.cariIsi("SELECT file_path FROM berkas_tte WHERE no_rawat=? ORDER BY uploaded_at DESC LIMIT 1", noRawat);
                        if(filePath != null && !filePath.isEmpty()){
                            String fullUrl = koneksiDB.URLNEXTCLOUD().replace("remote.php/dav/files/", "index.php/apps/files/?dir=/&openfile=") ;
                            // If URLNEXTCLOUD is just the base or DAV base, we might need to adjust.
                            // Standard Khanza pattern for viewing Nextcloud usually involves the DAV path or a proxy.
                            // Given the UploadPDFTte logic, the path is: baseUrl + filePath
                            String viewUrl = koneksiDB.URLNEXTCLOUD() + koneksiDB.USERNEXTCLOUD() + "/" + filePath;
                            
                            java.awt.Desktop.getDesktop().browse(new java.net.URI(viewUrl));
                        } else {
                            JOptionPane.showMessageDialog(rootPane, "Dokumen TTE tidak ditemukan untuk No. Rawat ini.");
                        }
                    } catch (Exception e) {
                        System.out.println("Error membuka dokumen TTE: " + e.getMessage());
                    }
                }
            }
        });
        jPopupMenu1.add(MnLihatTTE);
        
        tbBerkas.setComponentPopupMenu(jPopupMenu1);

        // Set Default Filter
        DTPCari1.setDate(java.sql.Timestamp.valueOf(java.time.LocalDateTime.now().minusWeeks(1)));
        CmbStatus.setSelectedItem("Menunggu TTD");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbBerkas = new widget.Table();
        panelGlass5 = new widget.panelisi();
        jLabel6 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel9 = new widget.Label();
        CmbStatus = new widget.ComboBox();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        jLabel7 = new widget.Label();
        KdDokter = new widget.TextBox();
        NmDokter = new widget.TextBox();
        BtnDokter = new widget.Button();
        jLabel8 = new widget.Label();
        BtnProses = new widget.Button();
        BtnProsesV2 = new widget.Button();
        BtnHapus = new widget.Button();
        BtnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Daftar Berkas Klaim / Draft TTE ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setOpaque(true);
        Scroll.setViewportView(tbBerkas);
        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass5.setPreferredSize(new java.awt.Dimension(55, 55));
        panelGlass5.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel6.setText("Tgl.Upload :");
        jLabel6.setPreferredSize(new java.awt.Dimension(65, 23));
        panelGlass5.add(jLabel6);

        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setDate(new java.util.Date());
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass5.add(DTPCari1);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass5.add(jLabel21);

        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setDate(new java.util.Date());
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass5.add(DTPCari2);

        jLabel9.setText("Stts :");
        jLabel9.setPreferredSize(new java.awt.Dimension(40, 23));
        panelGlass5.add(jLabel9);

        CmbStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semua", "Menunggu TTD", "Sudah TTD" }));
        CmbStatus.setPreferredSize(new java.awt.Dimension(120, 23));
        panelGlass5.add(CmbStatus);

        TCari.setPreferredSize(new java.awt.Dimension(180, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass5.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('2');
        BtnCari.setToolTipText("Alt+2");
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

        jLabel7.setText("Dokter :");
        jLabel7.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass5.add(jLabel7);

        KdDokter.setEditable(false);
        KdDokter.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass5.add(KdDokter);

        NmDokter.setEditable(false);
        NmDokter.setPreferredSize(new java.awt.Dimension(150, 23));
        panelGlass5.add(NmDokter);

        BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokter.setMnemonic('3');
        BtnDokter.setToolTipText("Alt+3");
        BtnDokter.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokterActionPerformed(evt);
            }
        });
        panelGlass5.add(BtnDokter);

        jLabel8.setPreferredSize(new java.awt.Dimension(20, 23));
        panelGlass5.add(jLabel8);

        BtnProses.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/file-edit-16x16.png"))); // NOI18N
        BtnProses.setMnemonic('P');
        BtnProses.setText("TTE Batch V1");
        BtnProses.setToolTipText("Alt+P");
        BtnProses.setPreferredSize(new java.awt.Dimension(160, 23));
        BtnProses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnProsesActionPerformed(evt);
            }
        });
        BtnProses.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnProsesKeyPressed(evt);
            }
        });
        panelGlass5.add(BtnProses);

        BtnProsesV2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/file-edit-16x16.png"))); // NOI18N
        BtnProsesV2.setMnemonic('V');
        BtnProsesV2.setText("TTE Batch V2");
        BtnProsesV2.setToolTipText("Alt+V");
        BtnProsesV2.setPreferredSize(new java.awt.Dimension(160, 23));
        BtnProsesV2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnProsesV2ActionPerformed(evt);
            }
        });
        BtnProsesV2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnProsesV2KeyPressed(evt);
            }
        });
        panelGlass5.add(BtnProsesV2);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 23));
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
        panelGlass5.add(BtnHapus);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); // NOI18N
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setPreferredSize(new java.awt.Dimension(90, 23));
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

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>                        

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {                                          
        dispose();
    }                                         

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {                                         
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{Valid.pindah(evt,BtnHapus,TCari);}
    }                                        

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {                                   
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            BtnKeluar.requestFocus();
        }
    }                                  

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {                                        
        tampil();
    }                                       

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {                                    
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt, TCari, BtnAll);
        }
    }                                   

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {                                       
        TCari.setText("");
        KdDokter.setText("");
        NmDokter.setText("");
        tampil();
    }                                      

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {                                   
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            tampil();
        }else{
            Valid.pindah(evt, BtnCari, BtnProses);
        }
    }                                  
    
    private void BtnDokterActionPerformed(java.awt.event.ActionEvent evt) {                                          
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    } 

    private void BtnProsesActionPerformed(java.awt.event.ActionEvent evt) {  
        int[] rowsToProcess = tbBerkas.getSelectedRows();
        if(rowsToProcess.length == 0){
            // If no selection, default to all rows in table
            int rowCount = tbBerkas.getRowCount();
            if (rowCount == 0) {
                JOptionPane.showMessageDialog(rootPane, "Tabel kosong, tidak ada data untuk diproses.");
                return;
            }
            rowsToProcess = new int[rowCount];
            for (int i = 0; i < rowCount; i++) {
                rowsToProcess[i] = i;
            }
        }
        final int[] selectedRows = rowsToProcess;
        
        // 1. Collect signers and filter for logged-in doctor
        String loggedInDoctor = akses.getkode().trim();
        boolean foundLoggedIn = false;
        for(int row : selectedRows){
            String noRawat = tbBerkas.getValueAt(row, 0).toString();
            if(collectSignerNips(noRawat).contains(loggedInDoctor)){
                foundLoggedIn = true;
                break;
            }
        }
        
        java.util.Set<String> allNips = new java.util.LinkedHashSet<>();
        if(foundLoggedIn) {
            allNips.add(loggedInDoctor);
        } else {
            JOptionPane.showMessageDialog(rootPane, "Dokter yang login tidak ditemukan sebagai penanda tangan pada berkas yang dipilih.");
            return;
        }

        // 2. Cache credentials for unique signers
        class TTECredentials {
            String nik, passphrase, tagCoordinate;
            TTECredentials(String n, String p, String t) { nik = n; passphrase = p; tagCoordinate = t; }
        }
        java.util.Map<String, TTECredentials> credentialCache = new java.util.HashMap<>();
        
        int confirmBatch = JOptionPane.showConfirmDialog(rootPane, 
            "Akan memproses " + selectedRows.length + " berkas dengan total " + allNips.size() + " penanda tangan unik.\nLanjutkan?", 
            "Konfirmasi TTE Batch", JOptionPane.YES_NO_OPTION);
        if(confirmBatch != JOptionPane.YES_OPTION) return;

        // Create progress dialog for credential collection
        javax.swing.JDialog credProgress = new javax.swing.JDialog(this, "Koleksi Kredensial", true);
        credProgress.setSize(300, 100);
        credProgress.setLocationRelativeTo(this);
        credProgress.add(new javax.swing.JLabel("Menunggu input kredensial...", javax.swing.SwingConstants.CENTER));

        int signerIndex = 1;
        for(String nip : allNips){
            laporan.TTECredentialDialog credDialog = new laporan.TTECredentialDialog(credProgress, nip, signerIndex++, allNips.size());
            credDialog.setVisible(true);
            if(!credDialog.isConfirmed()){
                JOptionPane.showMessageDialog(rootPane, "Proses TTE Batch dibatalkan.");
                return;
            }
            credentialCache.put(nip, new TTECredentials(credDialog.getNik(), credDialog.getPassphrase(), credDialog.getTagCoordinate()));
        }

        // 3. Process each document in background
        javax.swing.JDialog progressDialog = new javax.swing.JDialog(this, "Progress TTE Batch", true);
        progressDialog.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);
        progressDialog.setSize(500, 180);
        progressDialog.setLocationRelativeTo(this);
        progressDialog.setLayout(new java.awt.BorderLayout(10, 10));
        
        javax.swing.JLabel lblProgress = new javax.swing.JLabel("Memulai proses batch...", javax.swing.SwingConstants.CENTER);
        lblProgress.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 10, 20));
        progressDialog.add(lblProgress, java.awt.BorderLayout.CENTER);
        
        javax.swing.JProgressBar progressBar = new javax.swing.JProgressBar(0, selectedRows.length);
        progressBar.setStringPainted(true);
        progressBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 20, 20));
        progressDialog.add(progressBar, java.awt.BorderLayout.SOUTH);
        
        javax.swing.SwingWorker<Integer, String> worker = new javax.swing.SwingWorker<Integer, String>() {
            private int successCount = 0;
            private int failCount = 0;
            
            @Override
            protected Integer doInBackground() throws Exception {
                for(int i = 0; i < selectedRows.length; i++){
                    int row = selectedRows[i];
                    String noRawat = tbBerkas.getValueAt(row, 0).toString();
                    String lokasiFile = tbBerkas.getValueAt(row, 5).toString();
                    String tglTtd = tbBerkas.getValueAt(row, 6).toString();
                    
                    publish("DOC_PROGRESS:" + i);
                    publish("Memproses (" + (i+1) + "/" + selectedRows.length + "): " + noRawat);
                    
                    try {
                        java.util.List<String> signers = collectSignerNips(noRawat);
                        java.io.File currentFile = downloadPDFFromServer(lokasiFile);
                        if(currentFile == null) throw new Exception("Gagal unduh PDF");
                        
                        boolean signed = false;
                        for(String nip : signers){
                            System.out.println("Checking signer: [" + nip + "] against cache keys: " + credentialCache.keySet());
                            TTECredentials creds = credentialCache.get(nip);
                            if(creds == null) {
                                System.out.println("Skip: No credentials in batch cache for member: " + nip);
                                continue;
                            }
                            
                            System.out.println("Process TTE for signer: " + nip + " using NIK: " + creds.nik);
                            java.io.File tempSigned = java.io.File.createTempFile("batch_ttd_", ".pdf");
                            String location = akses.getnamars() + ", " + akses.getkabupatenrs();
                            String reason = "Dokumen Ini Telah Disetujui dengan Tanda Tangan Elektronik";
                            String linkQR = "Dikeluarkan di " + akses.getnamars() + " dan ditandatangani secara elektronik oleh " + creds.nik;
                            
                            boolean ok = bridging.EsignService.prosesTTE(currentFile.getAbsolutePath(), tempSigned.getAbsolutePath(),
                                creds.nik, creds.passphrase, creds.tagCoordinate, linkQR, location, reason);

                            // Dicatat per penanda tangan, bukan per berkas: pada estafet satu
                            // dokumen ditandatangani beberapa kali, dan tiap tanda tangan adalah
                            // peristiwa tersendiri yang harus bisa ditelusuri (UKS BSrE poin 9).
                            if(!ok) {
                                bridging.EsignServiceV2.catatLog(noRawat, creds.nik, namaPenandatangan(nip),
                                    "Gagal TTE Berkas Klaim estafet (batch, NIP " + nip + ")", "ERR");
                                throw new Exception("Gagal TTE (NIP: " + nip + ")");
                            }

                            System.out.println("Success TTE for signer: " + nip);
                            bridging.EsignServiceV2.catatLog(noRawat, creds.nik, namaPenandatangan(nip),
                                "Sukses TTE Berkas Klaim estafet (batch, NIP " + nip + ")", "200");
                            // Swap files for sequential signature
                            currentFile = tempSigned;
                            signed = true;
                        }
                        
                        if (!signed) {
                            throw new Exception("Tidak ada penandatanganan dilakukan. Pastikan Anda login sebagai salah satu dokter penandatangan berkas ini.");
                        }
                        
                        // Progress naming for batch (Progress Flow Aware)
                        String fileName = lokasiFile;
                        if (fileName.contains("/")) fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                        String newFileName = fileName;
                        
                        int totalSigners = 1;
                        int currentSignerCount = 0;
                        boolean isProgressFlow = false;
                        
                        Pattern pattern = Pattern.compile("_(\\d+)-(\\d+)\\.pdf$");
                        Matcher matcher = pattern.matcher(fileName);
                        if (matcher.find()) {
                            totalSigners = Integer.parseInt(matcher.group(1));
                            currentSignerCount = Integer.parseInt(matcher.group(2));
                            
                            // Increment suffix only if we signed for the doctor and it's not already complete
                            if (currentSignerCount < totalSigners) {
                                currentSignerCount++;
                                newFileName = fileName.replace("_" + totalSigners + "-" + (currentSignerCount-1) + ".pdf", 
                                                               "_" + totalSigners + "-" + currentSignerCount + ".pdf");
                            }
                            isProgressFlow = true;
                        }
                        
                        // Sync result back to Hybrid Server
                        syncToHybridServer(currentFile, newFileName, noRawat);
                        Sequel.queryu2("UPDATE berkas_klaim SET lokasi_file=? WHERE no_rawat=?", 2, new String[]{newFileName, noRawat});

                        // Final upload to Nextcloud only if all have signed
                        if (!isProgressFlow || currentSignerCount == totalSigners) {
                            if(uploadToNextcloud(currentFile, noRawat)){
                                updateBerkasKlaimStatus(noRawat, "SUDAH_TTD");
                                successCount++;
                            } else {
                                throw new Exception("Gagal upload ke Nextcloud");
                            }
                        } else {
                            successCount++; // Count as success for intermediate signer
                        }
                    } catch (Exception e) {
                        System.out.println("Error processing batch row " + row + ": " + e.getMessage());
                        e.printStackTrace();
                        failCount++;
                    }
                }
                return successCount;
            }
            
            @Override
            protected void process(java.util.List<String> chunks) {
                for(String msg : chunks){
                    if(msg.startsWith("DOC_PROGRESS:")){
                        progressBar.setValue(Integer.parseInt(msg.substring(13)));
                    } else {
                        lblProgress.setText(msg);
                    }
                }
            }
            
            @Override
            protected void done() {
                progressDialog.dispose();
                String summary = "Proses Batch Selesai.\n\nBerhasil: " + successCount + "\nGagal: " + failCount;
                JOptionPane.showMessageDialog(DlgBerkasKlaim.this, summary);
                tampil();
            }
        };
        
        worker.execute();
        progressDialog.setVisible(true);
    }    

    private void BtnProsesV2ActionPerformed(java.awt.event.ActionEvent evt) {  
        int[] rowsToProcess = tbBerkas.getSelectedRows();
        if(rowsToProcess.length == 0){
            // If no selection, default to all rows in table
            int rowCount = tbBerkas.getRowCount();
            if (rowCount == 0) {
                JOptionPane.showMessageDialog(rootPane, "Tabel kosong, tidak ada data untuk diproses.");
                return;
            }
            rowsToProcess = new int[rowCount];
            for (int i = 0; i < rowCount; i++) {
                rowsToProcess[i] = i;
            }
        }
        final int[] selectedRows = rowsToProcess;
        
        // 1. Collect signers and filter for logged-in doctor
        String loggedInDoctor = akses.getkode().trim();
        boolean foundLoggedIn = false;
        for(int row : selectedRows){
            String noRawat = tbBerkas.getValueAt(row, 0).toString();
            if(collectSignerNips(noRawat).contains(loggedInDoctor)){
                foundLoggedIn = true;
                break;
            }
        }
        
        java.util.Set<String> allNips = new java.util.LinkedHashSet<>();
        if(foundLoggedIn) {
            allNips.add(loggedInDoctor);
        } else {
            JOptionPane.showMessageDialog(rootPane, "Dokter yang login tidak ditemukan sebagai penanda tangan pada berkas yang dipilih.");
            return;
        }

        // Prompt for Passphrase (only need for logged in doctor in V2)
        String nik = Sequel.cariIsi("select no_ktp from pegawai where nik=?", loggedInDoctor);
        if (nik == null || nik.trim().isEmpty() || nik.equals("-")) {
            JOptionPane.showMessageDialog(rootPane, "NIK (No. KTP) tidak ditemukan di data pegawai.");
            return;
        }
        
        javax.swing.JPasswordField pf = new javax.swing.JPasswordField();
        int okC = JOptionPane.showConfirmDialog(rootPane, pf, "Masukkan Passphrase BSrE untuk " + nik + " (Batch V2)", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (okC != JOptionPane.OK_OPTION) return;
        String passphrase = new String(pf.getPassword());
        if(passphrase.trim().isEmpty()){
            JOptionPane.showMessageDialog(rootPane, "Passphrase tidak boleh kosong.");
            return;
        }

        // 3. Process each document in background
        javax.swing.JDialog progressDialog = new javax.swing.JDialog(this, "Progress TTE Batch V2", true);
        progressDialog.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);
        progressDialog.setSize(500, 180);
        progressDialog.setLocationRelativeTo(this);
        progressDialog.setLayout(new java.awt.BorderLayout(10, 10));
        
        javax.swing.JLabel lblProgress = new javax.swing.JLabel("Memulai proses batch V2...", javax.swing.SwingConstants.CENTER);
        lblProgress.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 10, 20));
        progressDialog.add(lblProgress, java.awt.BorderLayout.CENTER);
        
        javax.swing.JProgressBar progressBar = new javax.swing.JProgressBar(0, selectedRows.length);
        progressBar.setStringPainted(true);
        progressBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 20, 20));
        progressDialog.add(progressBar, java.awt.BorderLayout.SOUTH);
        
        javax.swing.SwingWorker<Integer, String> worker = new javax.swing.SwingWorker<Integer, String>() {
            private int successCount = 0;
            private int failCount = 0;
            
            @Override
            protected Integer doInBackground() throws Exception {
                for(int i = 0; i < selectedRows.length; i++){
                    int row = selectedRows[i];
                    String noRawat = tbBerkas.getValueAt(row, 0).toString();
                    String lokasiFile = tbBerkas.getValueAt(row, 5).toString();
                    
                    publish("DOC_PROGRESS:" + (i + 1));
                    publish("Memproses (" + (i+1) + "/" + selectedRows.length + "): " + noRawat);
                    
                    try {
                        java.util.List<String> signers = collectSignerNips(noRawat);
                        if (!signers.contains(loggedInDoctor)) {
                            publish("Melewati " + noRawat + " (Bukan Penanda Tangan)");
                            continue; 
                        }

                        String nmUser = Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", loggedInDoctor);
                        if(nmUser.isEmpty()) nmUser = Sequel.cariIsi("select nama from petugas where nip=?", loggedInDoctor);

                        java.io.File downloadedPdf = downloadPDFFromServer(lokasiFile);
                        if(downloadedPdf == null) throw new Exception("Gagal unduh PDF");
                        
                        List<bridging.EsignServiceV2.SignatureProperty> properties = new ArrayList<>();
                        publish("Mendeteksi posisi tanda tangan...");
                        bridging.TagSignerDetector tagDetector = new bridging.TagSignerDetector();
                        Map<String, List<bridging.TagSignerDetector.TagCoordinate>> detectedTags = tagDetector.detectAllTags(downloadedPdf.getAbsolutePath());
                        
                        SignerConfig currentSigner = null;
                        bridging.PDFTextLocationHelper.TextLocation loc = null;
                        String foundTag = null;
                        
                        // 1. Try Tag-First Detection
                        for (String tag : detectedTags.keySet()) {
                            try {
                                String cleanTag = tag.replace("#", "").trim();
                                String sql = "SELECT kd_dokter as id, nm_dokter as nama, email, tag_signer " +
                                            "FROM dokter WHERE (TRIM(tag_signer) = ? OR REPLACE(TRIM(tag_signer), '#', '') = ?) AND TRIM(kd_dokter) = ?";
                                PreparedStatement ps_tag = koneksi.prepareStatement(sql);
                                ps_tag.setString(1, tag);
                                ps_tag.setString(2, cleanTag);
                                ps_tag.setString(3, loggedInDoctor);
                                ResultSet rs_tag = ps_tag.executeQuery();
                                if (rs_tag.next()) {
                                    currentSigner = new SignerConfig();
                                    currentSigner.setSignerId(rs_tag.getString("id").trim());
                                    currentSigner.setSignerName(rs_tag.getString("nama").trim());
                                    currentSigner.setTagSigner(rs_tag.getString("tag_signer").trim());
                                    currentSigner.setSignerType("dokter");
                                }
                                rs_tag.close();
                                ps_tag.close();

                                if (currentSigner == null) {
                                    sql = "SELECT nip as id, nama, email, tag_signer " +
                                          "FROM petugas WHERE (TRIM(tag_signer) = ? OR REPLACE(TRIM(tag_signer), '#', '') = ?) AND TRIM(nip) = ?";
                                    ps_tag = koneksi.prepareStatement(sql);
                                    ps_tag.setString(1, tag);
                                    ps_tag.setString(2, cleanTag);
                                    ps_tag.setString(3, loggedInDoctor);
                                    rs_tag = ps_tag.executeQuery();
                                    if (rs_tag.next()) {
                                        currentSigner = new SignerConfig();
                                        currentSigner.setSignerId(rs_tag.getString("id").trim());
                                        currentSigner.setSignerName(rs_tag.getString("nama").trim());
                                        currentSigner.setTagSigner(rs_tag.getString("tag_signer").trim());
                                        currentSigner.setSignerType("petugas");
                                    }
                                    rs_tag.close();
                                    ps_tag.close();
                                }

                                if (currentSigner != null) {
                                    List<bridging.TagSignerDetector.TagCoordinate> coords = detectedTags.get(tag);
                                    if (!coords.isEmpty()) {
                                        loc = new bridging.PDFTextLocationHelper.TextLocation();
                                        loc.page = coords.get(0).page;
                                        loc.x = coords.get(0).x;
                                        loc.y = coords.get(0).y;
                                        loc.pageHeight = coords.get(0).pageHeight;
                                        loc.pageWidth = coords.get(0).pageWidth;
                                        loc.width = coords.get(0).width;
                                        loc.height = coords.get(0).height;
                                        foundTag = tag;
                                        break;
                                    }
                                }
                            } catch (Exception etag) {
                            }
                        }
                        
                        // 2. Fallback to NIP-based detection
                        if (currentSigner == null) {
                            bridging.PDFTextLocationHelper locationHelper = new bridging.PDFTextLocationHelper();
                            SertisignMultiSignerService signerService = new SertisignMultiSignerService();
                            List<SignerConfig> allSigners = signerService.getSignersFromBerkas(noRawat);
                            
                            int currentSignerIdx = -1;
                            for (int idx = 0; idx < allSigners.size(); idx++) {
                                String signerId = allSigners.get(idx).getSignerId() != null ? allSigners.get(idx).getSignerId().trim() : "";
                                if (signerId.equalsIgnoreCase(loggedInDoctor)) {
                                    currentSigner = allSigners.get(idx);
                                    currentSignerIdx = idx + 1;
                                    break;
                                }
                            }
                            
                            if (currentSigner != null) {
                                String dbTag = currentSigner.getTagSigner();
                                if (dbTag != null && !dbTag.trim().isEmpty()) {
                                    loc = locationHelper.findText(downloadedPdf, dbTag);
                                    if (loc != null) foundTag = dbTag;
                                }
                                
                                if (loc == null && currentSignerIdx != -1) {
                                    String[] tagFormats = {"<signer%d>", "[signer%d]", "#{signer%d}", "#%dA#", "#%dA", "#%d#", "#%d", "#SIGN_DOKTER_%d#", "#SIGN_PETUGAS_%d#", "#SIGN_PETUGAS%d#"};
                                    for (String format : tagFormats) {
                                        String tagStr = String.format(format, currentSignerIdx);
                                        loc = locationHelper.findText(downloadedPdf, tagStr);
                                        if (loc != null) {
                                            foundTag = tagStr;
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        if (loc != null) {
                            bridging.EsignServiceV2.SignatureProperty prop = new bridging.EsignServiceV2.SignatureProperty();
                            prop.page = loc.page;
                            float qrSize = 60.0f;
                            prop.originX = loc.x + (loc.width / 2) - (qrSize / 2);
                            prop.originY = loc.y;
                            prop.pageWidth = loc.pageWidth;
                            prop.pageHeight = loc.pageHeight;
                            prop.width = qrSize;
                            prop.height = qrSize;
                            prop.location = akses.getnamars() + ", " + akses.getkabupatenrs();
                            
                            String finger = Sequel.cariIsi("select sha1(sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", loggedInDoctor);
                            if(finger.isEmpty()) finger = loggedInDoctor;
                            
                            String qrText = "Dikeluarkan di " + akses.getnamars() + ", " + akses.getkabupatenrs() + 
                                           "\nDitandatangani secara elektronik oleh " + nmUser + 
                                           "\nID " + finger + 
                                           "\n" + new java.text.SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date());
                            
                            prop.linkQR = qrText;
                            prop.image = false; 
                            prop.width = qrSize;
                            prop.height = qrSize;
                            
                            properties.add(prop);
                        }

                        if (properties.isEmpty()) {
                            throw new Exception("Gagal mendeteksi tag tanda tangan.");
                        }

                        publish("Mengirim permintaan TTE V2 ke BSrE...");
                        bridging.EsignServiceV2 serviceV2 = new bridging.EsignServiceV2();
                        
                        String fileName = lokasiFile;
                        if (fileName.contains("/")) fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                        
                        String newFileName = fileName;
                        int totalSigners = 1;
                        int progressSignerIndex = 0;
                        boolean isProgressFlow = false;
                        
                        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("_(\\d+)-(\\d+)\\.pdf$");
                        java.util.regex.Matcher matcher = pattern.matcher(fileName);
                        
                        if (matcher.find()) {
                            totalSigners = Integer.parseInt(matcher.group(1));
                            progressSignerIndex = Integer.parseInt(matcher.group(2));
                            
                            if (progressSignerIndex < totalSigners) {
                                int nextSigner = progressSignerIndex + 1;
                                newFileName = fileName.replace("_" + totalSigners + "-" + progressSignerIndex + ".pdf", 
                                                               "_" + totalSigners + "-" + nextSigner + ".pdf");
                                progressSignerIndex = nextSigner;
                            }
                            isProgressFlow = true;
                        } else {
                            String sep = Sequel.cariIsi("SELECT no_sep FROM bridging_sep WHERE no_rawat=?", noRawat);
                            if (sep == null || sep.trim().isEmpty()) sep = noRawat.replace("/", "");
                            newFileName = sep + "_SIGNED_V2.pdf";
                        }
                        
                        java.io.File signedFile = new java.io.File(newFileName);
                        boolean success = serviceV2.prosesTTE(downloadedPdf, signedFile, nik, passphrase, properties);

                        if (success) {
                            bridging.EsignServiceV2.catatLog(noRawat, nik, nmUser,
                                "Sukses TTE Berkas Klaim (batch V2) - File: " + newFileName, "200");
                            publish("Sinkronisasi ke Hybrid Server...");
                            boolean synced = syncToHybridServer(signedFile, newFileName, noRawat);
                            if (!synced) {
                                throw new Exception("Gagal sinkronisasi ke Hybrid Web Server.");
                            }
                            
                            Sequel.queryu2("UPDATE berkas_klaim SET lokasi_file=? WHERE no_rawat=?", 2, new String[]{newFileName, noRawat});

                            if (!isProgressFlow || progressSignerIndex == totalSigners) {
                                publish("Mengupload ke Nextcloud...");
                                if (uploadToNextcloud(signedFile, noRawat)) {
                                    publish("Memperbarui status berkas...");
                                    updateBerkasKlaimStatus(noRawat, "SUDAH_TTD");
                                    successCount++;
                                } else {
                                    throw new Exception("TTE Berhasil, tapi gagal upload ke Nextcloud.");
                                }
                            } else {
                                successCount++;
                            }
                        } else {
                            String detail = serviceV2.getLastErrorMessage();
                            int code = serviceV2.getLastStatusCode();
                            String err = "Gagal proses TTE BSrE API" + (code > 0 ? " (Status: " + code + ")" : "") + (detail != null && !detail.isEmpty() ? " - " + detail : "");
                            bridging.EsignServiceV2.catatLog(noRawat, nik, nmUser, err, String.valueOf(code));
                            throw new Exception(err);
                        }

                    } catch (Exception e) {
                        System.out.println("Error processing batch V2 row " + row + ": " + e.getMessage());
                        bridging.EsignServiceV2.catatLog(noRawat, nik, null, "Error Batch TTE: " + e.getMessage(), "ERR");
                        e.printStackTrace();
                        failCount++;
                    }
                }
                return successCount;
            }
            
            @Override
            protected void process(java.util.List<String> chunks) {
                for(String msg : chunks){
                    if(msg.startsWith("DOC_PROGRESS:")){
                        progressBar.setValue(Integer.parseInt(msg.substring(13)));
                    } else {
                        lblProgress.setText(msg);
                    }
                }
            }
            
            @Override
            protected void done() {
                progressDialog.dispose();
                String summary = "Proses Batch V2 Selesai.\n\nBerhasil: " + successCount + "\nGagal: " + failCount;
                JOptionPane.showMessageDialog(DlgBerkasKlaim.this, summary);
                tampil();
            }
        };
        
        worker.execute();
        progressDialog.setVisible(true);
    }    

    private void BtnProsesKeyPressed(java.awt.event.KeyEvent evt) {
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnProsesActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnAll, BtnProsesV2);
        }
    }

    private void BtnProsesV2KeyPressed(java.awt.event.KeyEvent evt) {
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnProsesV2ActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnProses, BtnHapus);
        }
    }

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {
        if(tbBerkas.getSelectedRow() != -1){
            int reply = JOptionPane.showConfirmDialog(rootPane, "Yakin data akan dihapus?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if(reply == JOptionPane.YES_OPTION){
                if(Sequel.queryu2tf("delete from berkas_klaim where no_rawat=?", 1, new String[]{tbBerkas.getValueAt(tbBerkas.getSelectedRow(), 0).toString()})){
                    tampil();
                }else{
                    JOptionPane.showMessageDialog(rootPane, "Gagal menghapus data.");
                }
            }
        }else{
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data terlebih dahulu.");
        }
    }

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnHapusActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnProses, BtnKeluar);
        }
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            DlgBerkasKlaim dialog = new DlgBerkasKlaim(new javax.swing.JFrame(), true);
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
    private widget.Button BtnDokter;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnProses;
    private widget.Button BtnProsesV2;
    private widget.TextBox KdDokter;
    private widget.TextBox NmDokter;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private widget.Label jLabel8;
    private widget.panelisi panelGlass5;
    private widget.Table tbBerkas;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.Label jLabel21;
    private widget.Label jLabel9;
    private widget.ComboBox CmbStatus;
    // End of variables declaration

    /**
     * Nama penandatangan untuk baris esign_log.
     *
     * Dicoba sebagai dokter dulu, lalu petugas. Kalau dua-duanya kosong, kode
     * login dikembalikan apa adanya supaya baris log tidak pernah kehilangan
     * identitas penandatangan — log audit yang kolom namanya '-' tidak banyak
     * gunanya saat ditelusuri belakangan.
     */
    private String namaPenandatangan(String kode) {
        if (kode == null || kode.trim().isEmpty()) return "-";
        String nama = Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", kode);
        if (nama == null || nama.isEmpty()) {
            nama = Sequel.cariIsi("select nama from petugas where nip=?", kode);
        }
        return (nama == null || nama.isEmpty()) ? kode : nama;
    }

    private void MnTTESingleActionPerformed(java.awt.event.ActionEvent evt) {
        if(tbBerkas.getSelectedRow() == -1){
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data terlebih dahulu.");
            return;
        }

        String noRawat = tbBerkas.getValueAt(tbBerkas.getSelectedRow(), 0).toString();
        String lokasiFile = tbBerkas.getValueAt(tbBerkas.getSelectedRow(), 5).toString();
        String status = tbBerkas.getValueAt(tbBerkas.getSelectedRow(), 7).toString();

        if(noRawat.equals("") || lokasiFile.equals("")){
            JOptionPane.showMessageDialog(rootPane, "Data tidak lengkap.");
            return;
        }

        // Get NIK and Tag Signer for currentUser
        String nik = Sequel.cariIsi("select no_ktp from pegawai where nik=?", akses.getkode());
        String tagSigner = Sequel.cariIsi("select tag_signer from dokter where kd_dokter=?", akses.getkode());

        if(nik.equals("") || tagSigner.equals("") || tagSigner.equals("-")){
             JOptionPane.showMessageDialog(rootPane, "NIK atau Tag Signer user login tidak ditemukan.");
             return;
        }

        // Prompt for Passphrase
        javax.swing.JPasswordField pf = new javax.swing.JPasswordField();
        int okC = JOptionPane.showConfirmDialog(null, pf, "Masukkan Passphrase TTE untuk " + nik, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (okC != JOptionPane.OK_OPTION) return;
        
        String passphrase = new String(pf.getPassword());
        if(passphrase.isEmpty()){
            JOptionPane.showMessageDialog(rootPane, "Passphrase tidak boleh kosong.");
            return;
        }

        // Process in background
        javax.swing.JDialog progressDialog = new javax.swing.JDialog(this, "Progress TTE Single", true);
        progressDialog.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);
        progressDialog.setSize(400, 120);
        progressDialog.setLocationRelativeTo(this);
        progressDialog.setLayout(new java.awt.BorderLayout(10, 10));
        
        javax.swing.JLabel lblProgress = new javax.swing.JLabel("Memproses TTE Single...", javax.swing.SwingConstants.CENTER);
        progressDialog.add(lblProgress, java.awt.BorderLayout.CENTER);

        javax.swing.SwingWorker<Boolean, String> worker = new javax.swing.SwingWorker<Boolean, String>() {
            private String errorMessage = null;
            
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    publish("Mengunduh PDF...");
                    
                    // Download PDF to temp file first (pass file path, not URL)
                    java.io.File downloadedPdf = downloadPDFFromServer(lokasiFile);
                    if(downloadedPdf == null){
                        errorMessage = "Gagal mengunduh PDF dari server.";
                        return false;
                    }
                    
                    publish("Menandatangani PDF...");
                    
                    // Multi-signer progress flow logic
                    String fileName = lokasiFile;
                    if (fileName.contains("/")) fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                    
                    String newFileName = fileName;
                    int totalSigners = 1;
                    int currentSigner = 0;
                    boolean isProgressFlow = false;
                    
                    Pattern pattern = Pattern.compile("_(\\d+)-(\\d+)\\.pdf$");
                    Matcher matcher = pattern.matcher(fileName);
                    
                    if (matcher.find()) {
                        totalSigners = Integer.parseInt(matcher.group(1));
                        currentSigner = Integer.parseInt(matcher.group(2));
                        
                        // Increment suffix only if it hasn't reached the total count
                        if (currentSigner < totalSigners) {
                            int nextSigner = currentSigner + 1;
                            newFileName = fileName.replace("_" + totalSigners + "-" + currentSigner + ".pdf", 
                                                           "_" + totalSigners + "-" + nextSigner + ".pdf");
                            currentSigner = nextSigner;
                        } else {
                            // Already reached final suffix (e.g. Doc retry after Nextcloud fail)
                            // Keep the same name to avoid X-(X+1) naming error
                            newFileName = fileName;
                        }
                        isProgressFlow = true;
                    }
                    
                    // Temporary file for the signed result
                    java.io.File signedFile = java.io.File.createTempFile("signed_berkas_", ".pdf");
                    
                    String location = akses.getnamars() + ", " + akses.getkabupatenrs();
                    String reason = "Dokumen Ini Telah Disetujui dengan Tanda Tangan Elektronik";
                    String linkQR = "Dikeluarkan di " + akses.getnamars() + " dan ditandatangani secara elektronik oleh " + nik;
                    
                    // Use local file path instead of URL
                    boolean success = bridging.EsignService.prosesTTE(downloadedPdf.getAbsolutePath(), signedFile.getAbsolutePath(), nik, passphrase, tagSigner, linkQR, location, reason);

                    if(!success){
                        errorMessage = "Gagal proses TTE BSRE.";
                        bridging.EsignServiceV2.catatLog(noRawat, nik, namaPenandatangan(akses.getkode()),
                            "Gagal TTE Berkas Klaim (single)", "ERR");
                        return false;
                    }

                    // Dicatat di sini, bukan setelah unggah Nextcloud: tanda tangan sudah
                    // benar-benar terjadi pada titik ini. Kegagalan sinkronisasi/unggah
                    // sesudahnya adalah urusan distribusi berkas, bukan pembatalan TTE.
                    bridging.EsignServiceV2.catatLog(noRawat, nik, namaPenandatangan(akses.getkode()),
                        "Sukses TTE Berkas Klaim (single) - File: " + newFileName, "200");

                    // Sync to Hybrid Web Server first (Intermediate or Final)
                    publish("Sinkronisasi ke Hybrid Server...");
                    boolean synced = syncToHybridServer(signedFile, newFileName, noRawat);
                    if (!synced) {
                        errorMessage = "Gagal sinkronisasi ke Hybrid Web Server.";
                        return false;
                    }
                    
                    // Update database with latest filename
                    Sequel.queryu2("UPDATE berkas_klaim SET lokasi_file=? WHERE no_rawat=?", 2, new String[]{newFileName, noRawat});

                    // Final upload to Nextcloud only if all have signed
                    if (!isProgressFlow || currentSigner == totalSigners) {
                        publish("Mengupload ke Nextcloud (Final)...");
                        boolean uploaded = uploadToNextcloud(signedFile, noRawat);
                        if(!uploaded){
                            errorMessage = "Gagal upload ke Nextcloud.";
                            return false;
                        }

                        publish("Memperbarui status...");
                        updateBerkasKlaimStatus(noRawat, "SUDAH_TTD");
                    } else {
                        publish("Berhasil tanda tangan (" + currentSigner + "/" + totalSigners + ")");
                    }
                    
                    return true;
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    return false;
                }
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                if(!chunks.isEmpty()) lblProgress.setText(chunks.get(chunks.size()-1));
            }

            @Override
            protected void done() {
                progressDialog.dispose();
                try {
                    if(get()){
                        JOptionPane.showMessageDialog(DlgBerkasKlaim.this, "TTE Single Berhasil!");
                        tampil();
                    } else {
                        JOptionPane.showMessageDialog(DlgBerkasKlaim.this, "Gagal: " + errorMessage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
        progressDialog.setVisible(true);
    }

    private void MnTTESingleV2ActionPerformed(java.awt.event.ActionEvent evt) {
        if (tbBerkas.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(rootPane, "Silahkan pilih data terlebih dahulu.");
            return;
        }

        String noRawat = tbBerkas.getValueAt(tbBerkas.getSelectedRow(), 0).toString();
        String lokasiFile = tbBerkas.getValueAt(tbBerkas.getSelectedRow(), 5).toString();

        if (noRawat.equals("") || lokasiFile.equals("")) {
            JOptionPane.showMessageDialog(rootPane, "Data tidak lengkap.");
            return;
        }

        // 1. Collect signers (pegawai)
        List<String> signers = collectSignerNips(noRawat);
        if (signers.isEmpty()) {
            JOptionPane.showMessageDialog(rootPane, "Tidak ditemukan penanda tangan untuk berkas ini.");
            return;
        }

        // 2. Prompt for Passphrase
        String nik = Sequel.cariIsi("select no_ktp from pegawai where nik=?", akses.getkode());
        if (nik == null || nik.trim().isEmpty() || nik.equals("-")) {
            JOptionPane.showMessageDialog(rootPane, "NIK (No. KTP) tidak ditemukan di data pegawai.");
            return;
        }
        
        javax.swing.JPasswordField pf = new javax.swing.JPasswordField();
        int okC = JOptionPane.showConfirmDialog(rootPane, pf, "Masukkan Passphrase BSrE untuk " + nik, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (okC != JOptionPane.OK_OPTION) return;
        String passphrase = new String(pf.getPassword());

        // Progress Dialog
        javax.swing.JDialog progressDialog = new javax.swing.JDialog(this, "Progress TTE Single V2 (BSrE)", true);
        progressDialog.setDefaultCloseOperation(javax.swing.JDialog.DO_NOTHING_ON_CLOSE);
        progressDialog.setSize(400, 150);
        progressDialog.setLocationRelativeTo(this);
        progressDialog.setLayout(new java.awt.BorderLayout(10, 10));

        javax.swing.JLabel lblProgress = new javax.swing.JLabel("Memulai proses TTE BSrE V2...", javax.swing.SwingConstants.CENTER);
        lblProgress.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 10, 20));
        progressDialog.add(lblProgress, java.awt.BorderLayout.CENTER);

        javax.swing.JProgressBar progressBar = new javax.swing.JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 20, 20, 20));
        progressDialog.add(progressBar, java.awt.BorderLayout.SOUTH);

        SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {
            String errorMessage = "";

            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    List<SignatureProperty> properties = new ArrayList<>();
                    publish("Mengunduh PDF...");
                    File downloadedPdf = downloadPDFFromServer(lokasiFile);
                    if (downloadedPdf == null) {
                        errorMessage = "Gagal mengunduh PDF dari server.";
                        return false;
                    }

                    String loggedInUser = akses.getkode().trim();
                    String nmUser = Sequel.cariIsi("select nm_dokter from dokter where kd_dokter=?", loggedInUser);
                    if(nmUser.isEmpty()) nmUser = Sequel.cariIsi("select nama from petugas where nip=?", loggedInUser);
                    
                    publish("Mendeteksi posisi tanda tangan...");
                    TagSignerDetector tagDetector = new TagSignerDetector();
                    Map<String, List<TagCoordinate>> detectedTags = tagDetector.detectAllTags(downloadedPdf.getAbsolutePath());
                    
                    SignerConfig currentSigner = null;
                    PDFTextLocationHelper.TextLocation loc = null;
                    String foundTag = null;
                    
                    System.out.println("DEBUG: Terdeteksi " + detectedTags.size() + " jenis tag di PDF.");
                    
                    // 1. Try Tag-First Detection (Sourcelain style)
                    for (String tag : detectedTags.keySet()) {
                        System.out.println("DEBUG: Menelusuri tag: '" + tag + "'");
                        try {
                            // Check both original tag and cleaned tag (hashes removed)
                            String cleanTag = tag.replace("#", "").trim();
                            
                            // Check dokter table
                            String sql = "SELECT kd_dokter as id, nm_dokter as nama, email, tag_signer " +
                                        "FROM dokter WHERE (TRIM(tag_signer) = ? OR REPLACE(TRIM(tag_signer), '#', '') = ?) AND TRIM(kd_dokter) = ?";
                            PreparedStatement ps_tag = koneksi.prepareStatement(sql);
                            ps_tag.setString(1, tag);
                            ps_tag.setString(2, cleanTag);
                            ps_tag.setString(3, loggedInUser);
                            ResultSet rs_tag = ps_tag.executeQuery();
                            if (rs_tag.next()) {
                                currentSigner = new SignerConfig();
                                currentSigner.setSignerId(rs_tag.getString("id").trim());
                                currentSigner.setSignerName(rs_tag.getString("nama").trim());
                                currentSigner.setTagSigner(rs_tag.getString("tag_signer").trim());
                                currentSigner.setSignerType("dokter");
                            }
                            rs_tag.close();
                            ps_tag.close();

                            // If not found in dokter, check petugas
                            if (currentSigner == null) {
                                sql = "SELECT nip as id, nama, email, tag_signer " +
                                      "FROM petugas WHERE (TRIM(tag_signer) = ? OR REPLACE(TRIM(tag_signer), '#', '') = ?) AND TRIM(nip) = ?";
                                ps_tag = koneksi.prepareStatement(sql);
                                ps_tag.setString(1, tag);
                                ps_tag.setString(2, cleanTag);
                                ps_tag.setString(3, loggedInUser);
                                rs_tag = ps_tag.executeQuery();
                                if (rs_tag.next()) {
                                    currentSigner = new SignerConfig();
                                    currentSigner.setSignerId(rs_tag.getString("id").trim());
                                    currentSigner.setSignerName(rs_tag.getString("nama").trim());
                                    currentSigner.setTagSigner(rs_tag.getString("tag_signer").trim());
                                    currentSigner.setSignerType("petugas");
                                }
                                rs_tag.close();
                                ps_tag.close();
                            }

                            if (currentSigner != null) {
                                List<TagCoordinate> coords = detectedTags.get(tag);
                                if (!coords.isEmpty()) {
                                    loc = new TextLocation();
                                    loc.page = coords.get(0).page;
                                    loc.x = coords.get(0).x;
                                    loc.y = coords.get(0).y;
                                    loc.pageHeight = coords.get(0).pageHeight;
                                    loc.pageWidth = coords.get(0).pageWidth;
                                    foundTag = tag;
                                    System.out.println("DEBUG: TAG MATCH! User " + loggedInUser + " cocok dengan tag " + tag + " di hal " + loc.page);
                                    break;
                                }
                            }
                        } catch (Exception etag) {
                            System.out.println("DEBUG: Error matching tag " + tag + ": " + etag.getMessage());
                        }
                    }
                    
                    // 2. Fallback to NIP-based detection if tags didn't work
                    if (currentSigner == null) {
                        System.out.println("DEBUG: Tag-first detection gagal. Mencoba NIP-based fallback...");
                        PDFTextLocationHelper locationHelper = new PDFTextLocationHelper();
                        SertisignMultiSignerService signerService = new SertisignMultiSignerService();
                        List<SignerConfig> allSigners = signerService.getSignersFromBerkas(noRawat);
                        
                        int currentSignerIdx = -1;
                        for (int i = 0; i < allSigners.size(); i++) {
                            String signerId = allSigners.get(i).getSignerId() != null ? allSigners.get(i).getSignerId().trim() : "";
                            if (signerId.equalsIgnoreCase(loggedInUser)) {
                                currentSigner = allSigners.get(i);
                                currentSignerIdx = i + 1;
                                System.out.println("DEBUG: Logged-in user '" + loggedInUser + "' MATCHED with NIP at index " + currentSignerIdx);
                                break;
                            }
                        }
                        
                        if (currentSigner != null) {
                            // Try Specific Tag from DB first
                            String dbTag = currentSigner.getTagSigner();
                            if (dbTag != null && !dbTag.trim().isEmpty()) {
                                System.out.println("Mencari tag database: " + dbTag + " untuk " + loggedInUser);
                                loc = locationHelper.findText(downloadedPdf, dbTag);
                                if (loc != null) {
                                    foundTag = dbTag;
                                }
                            }
                            
                            // Try generic indexed fallback tags if still not found
                            if (loc == null && currentSignerIdx != -1) {
                                String[] tagFormats = {"<signer%d>", "[signer%d]", "#{signer%d}", "#%dA#", "#%dA", "#%d#", "#%d", "#SIGN_DOKTER_%d#", "#SIGN_PETUGAS_%d#", "#SIGN_PETUGAS%d#"};
                                for (String format : tagFormats) {
                                    String tag = String.format(format, currentSignerIdx);
                                    System.out.println("Mencoba tag generic: " + tag);
                                    loc = locationHelper.findText(downloadedPdf, tag);
                                    if (loc != null) {
                                        foundTag = tag;
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if (loc != null) {
                        EsignServiceV2.SignatureProperty prop = new EsignServiceV2.SignatureProperty();
                        prop.page = loc.page;
                        
                        float qrSize = 60.0f; // Adjusted size
                        // Align horizontally to the center of the tag
                        prop.originX = loc.x + (loc.width / 2) - (qrSize / 2);
                        prop.originY = loc.y;
                        
                        prop.pageWidth = loc.pageWidth;
                        prop.pageHeight = loc.pageHeight;
                        prop.width = qrSize;
                        prop.height = qrSize;
                        prop.location = akses.getnamars() + ", " + akses.getkabupatenrs();
                        
                        // Replicate format from /webappsrscb/penggajian/generateqrcode.php
                        String finger = Sequel.cariIsi("select sha1(sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?", loggedInUser);
                        if(finger.isEmpty()) finger = loggedInUser;
                        
                        String qrText = "Dikeluarkan di " + akses.getnamars() + ", " + akses.getkabupatenrs() + 
                                       "\nDitandatangani secara elektronik oleh " + nmUser + 
                                       "\nID " + finger + 
                                       "\n" + new java.text.SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date());
                        
                        prop.linkQR = qrText;
                        prop.image = false; // Use QR instead of specimen image
                        prop.width = qrSize;
                        prop.height = qrSize;
                        
                        properties.add(prop);
                        System.out.println("Signer " + loggedInUser + " ready at (" + prop.originX + "," + prop.originY + ")" + (prop.linkQR.isEmpty() ? "" : " with linkQR"));
                    }

                    if (properties.isEmpty()) {
                        errorMessage = "Gagal mendeteksi tag tanda tangan untuk user: " + loggedInUser;
                        return false;
                    }

                    publish("Mengirim permintaan TTE ke BSrE...");
                    EsignServiceV2 serviceV2 = new EsignServiceV2();
                    
                    // Progress naming logic (same as TTE V1)
                    String fileName = lokasiFile;
                    if (fileName.contains("/")) fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                    
                    String newFileName = fileName;
                    int totalSigners = 1;
                    int progressSignerIndex = 0;
                    boolean isProgressFlow = false;
                    
                    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("_(\\d+)-(\\d+)\\.pdf$");
                    java.util.regex.Matcher matcher = pattern.matcher(fileName);
                    
                    if (matcher.find()) {
                        totalSigners = Integer.parseInt(matcher.group(1));
                        progressSignerIndex = Integer.parseInt(matcher.group(2));
                        
                        if (progressSignerIndex < totalSigners) {
                            int nextSigner = progressSignerIndex + 1;
                            newFileName = fileName.replace("_" + totalSigners + "-" + progressSignerIndex + ".pdf", 
                                                           "_" + totalSigners + "-" + nextSigner + ".pdf");
                            progressSignerIndex = nextSigner;
                        }
                        isProgressFlow = true;
                    } else {
                        // Default name if no pattern found
                        String sep = Sequel.cariIsi("SELECT no_sep FROM bridging_sep WHERE no_rawat=?", noRawat);
                        if (sep == null || sep.trim().isEmpty()) sep = noRawat.replace("/", "");
                        newFileName = sep + "_SIGNED_V2.pdf";
                    }
                    
                    File signedFile = new File(newFileName);
                    boolean success = serviceV2.prosesTTE(downloadedPdf, signedFile, nik, passphrase, properties);

                    if (success) {
                        bridging.EsignServiceV2.catatLog(noRawat, nik, nmUser,
                            "Sukses TTE Berkas Klaim (single V2) - File: " + newFileName, "200");
                        // Sync to Hybrid Web Server first (Intermediate or Final)
                        publish("Sinkronisasi ke Hybrid Server...");
                        boolean synced = syncToHybridServer(signedFile, newFileName, noRawat);
                        if (!synced) {
                            errorMessage = "Gagal sinkronisasi ke Hybrid Web Server.";
                            return false;
                        }
                        
                        // Update database with latest filename
                        Sequel.queryu2("UPDATE berkas_klaim SET lokasi_file=? WHERE no_rawat=?", 2, new String[]{newFileName, noRawat});

                        // Final upload to Nextcloud only if all have signed or not a progress flow
                        if (!isProgressFlow || progressSignerIndex == totalSigners) {
                            publish("Mengupload ke Nextcloud (Final)...");
                            if (uploadToNextcloud(signedFile, noRawat)) {
                                publish("Memperbarui status berkas...");
                                updateBerkasKlaimStatus(noRawat, "SUDAH_TTD");
                                return true;
                            } else {
                                errorMessage = "TTE Berhasil, tapi gagal upload ke Nextcloud.";
                                return false;
                            }
                        } else {
                            publish("Berhasil tanda tangan (" + progressSignerIndex + "/" + totalSigners + ")");
                            return true;
                        }
                    } else {
                        String detail = serviceV2.getLastErrorMessage();
                        int code = serviceV2.getLastStatusCode();
                        errorMessage = "Gagal melakukan TTE via BSrE API.\n"
                                     + (code > 0 ? "Status: " + code + "\n" : "")
                                     + (detail != null && !detail.isEmpty() ? "Pesan Error: " + detail : "");
                        bridging.EsignServiceV2.catatLog(noRawat, nik, nmUser, "Gagal TTE Single: " + detail, String.valueOf(code));
                        return false;
                    }

                } catch (Exception e) {
                    errorMessage = e.getMessage() != null ? e.getMessage() : e.toString();
                    bridging.EsignServiceV2.catatLog(noRawat, nik, null, "Exception TTE Single: " + errorMessage, "EXC");
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void process(List<String> chunks) {
                lblProgress.setText(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                progressDialog.dispose();
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(rootPane, "TTE Single V2 (BSrE) Berhasil.");
                        tampil();
                    } else {
                        JOptionPane.showMessageDialog(rootPane, "Error: " + errorMessage);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(rootPane, "Terjadi kesalahan: " + e.getMessage());
                }
            }
        };

        worker.execute();
        progressDialog.setVisible(true);
    }


    public void tampil() {
        Valid.tabelKosong(tabMode);
        try{
            String sql = "select berkas_klaim.no_rawat, reg_periksa.no_rkm_medis, pasien.nm_pasien, "+
                    "berkas_klaim.tgl_upload, berkas_klaim.nama_berkas, berkas_klaim.lokasi_file, berkas_klaim.status, "+
                    "GROUP_CONCAT(DISTINCT dokter.nm_dokter SEPARATOR ', ') as signers "+
                    "from berkas_klaim inner join reg_periksa on berkas_klaim.no_rawat=reg_periksa.no_rawat "+
                    "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "left join berkas_digital_perawatan on berkas_klaim.no_rawat=berkas_digital_perawatan.no_rawat AND berkas_digital_perawatan.nip IS NOT NULL AND berkas_digital_perawatan.nip!='' AND berkas_digital_perawatan.nip!='-' "+
                    "left join dokter on berkas_digital_perawatan.nip=dokter.kd_dokter "+
                    "where berkas_klaim.tgl_upload between ? and ? and (berkas_klaim.no_rawat like ? or reg_periksa.no_rkm_medis like ? or pasien.nm_pasien like ?) ";
            
            if(CmbStatus.getSelectedIndex()==1){
                 sql += " AND (berkas_klaim.status = 'MENUNGGU_TTD' OR berkas_klaim.status IS NULL) ";
            }else if(CmbStatus.getSelectedIndex()==2){
                 sql += " AND berkas_klaim.status = 'SUDAH_TTD' ";
            }
            
            if(!KdDokter.getText().trim().equals("")){
                sql += " AND berkas_digital_perawatan.nip = ? ";
            }
            
            sql += "group by berkas_klaim.no_rawat, berkas_klaim.tgl_upload, berkas_klaim.nama_berkas, berkas_klaim.lokasi_file, berkas_klaim.status "+
                   "order by berkas_klaim.tgl_upload desc";
            
            ps=koneksi.prepareStatement(sql);
            try {
                ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
                ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
                ps.setString(3,"%"+TCari.getText()+"%");
                ps.setString(4,"%"+TCari.getText()+"%");
                ps.setString(5,"%"+TCari.getText()+"%");
                
                if(!KdDokter.getText().trim().equals("")){
                    ps.setString(6, KdDokter.getText().trim());
                }
                
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),
                        rs.getString("tgl_upload"),rs.getString("nama_berkas"),rs.getString("lokasi_file"),
                        rs.getString("signers"),rs.getString("status")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif : "+e);
            } finally{
                if(rs!=null){
                    rs.close();
                }
                if(ps!=null){
                    ps.close();
                }
            }
        }catch(Exception e){
            System.out.println("Notif : "+e);
        }
    }

    /**
     * Download PDF from server
     */
    private java.io.File downloadPDFFromServer(String lokasiFile) {
        try {
            // Clean path
            String cleanPath = lokasiFile;
            if(cleanPath.startsWith("/")){
                cleanPath = cleanPath.substring(1);
            }
            
            // Build URL
            String url = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + 
                        "/" + koneksiDB.HYBRIDWEB() + "/berkasklaim/" + cleanPath;
            
            System.out.println("Downloading PDF from: " + url);
            
            // Download file
            java.net.URL fileUrl = new java.net.URL(url);
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) fileUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(30000); // 30 seconds connection timeout
            conn.setReadTimeout(60000);    // 60 seconds read timeout
            
            if(conn.getResponseCode() == 200){
                java.io.InputStream is = conn.getInputStream();
                
                // Save to temp file
                java.io.File tempFile = java.io.File.createTempFile("berkas_klaim_", ".pdf");
                java.io.FileOutputStream fos = new java.io.FileOutputStream(tempFile);
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                while((bytesRead = is.read(buffer)) != -1){
                    fos.write(buffer, 0, bytesRead);
                }
                
                fos.close();
                is.close();
                conn.disconnect();
                
                System.out.println("Downloaded to: " + tempFile.getAbsolutePath());
                return tempFile;
            } else {
                System.err.println("Failed to download, HTTP " + conn.getResponseCode());
                return null;
            }
            
        } catch(Exception e){
            System.err.println("Error downloading PDF: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Collect unique signers from berkas_digital_perawatan
     */
    private java.util.List<String> collectSignerNips(String noRawat) {
        java.util.List<String> signers = new java.util.ArrayList<>();
        java.util.Set<String> signerSet = new java.util.LinkedHashSet<>();
        
        try {
            String sql = "SELECT DISTINCT nip FROM berkas_digital_perawatan " +
                        "WHERE no_rawat = ? AND nip IS NOT NULL AND nip != '' AND nip != '-' " +
                        "ORDER BY kode";
            
            PreparedStatement ps = koneksi.prepareStatement(sql);
            ps.setString(1, noRawat);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()){
                String nip = rs.getString("nip");
                if(nip != null && !nip.trim().isEmpty()){
                    signerSet.add(nip.trim());
                }
            }
            
            rs.close();
            ps.close();
            
            signers.addAll(signerSet);
            System.out.println("Collected " + signers.size() + " signers: " + signers);
            
        } catch(Exception e){
            System.err.println("Error collecting signers: " + e.getMessage());
            e.printStackTrace();
        }
        
        return signers;
    }
    
    /**
     * Upload signed PDF to Nextcloud
     */
    private boolean uploadToNextcloud(java.io.File signedFile, String noRawat) {
        try {
            // Get SEP for this noRawat
            String sep = Sequel.cariIsi("SELECT no_sep FROM bridging_sep WHERE no_rawat = ? LIMIT 1", noRawat);
            if(sep == null || sep.isEmpty()){
                sep = noRawat.replace("/", ""); // Fallback to noRawat without slashes
            }
            
            // Get current date and datetime
            String tanggal = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
            String tanggaljam = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            
            // Create uploader with correct parameter order:
            // (fileName, sep, tanggal, norawat, kode, tanggaljam, inacbg)
            bridging.UploadPDFTte uploader = new bridging.UploadPDFTte(
                signedFile.getAbsolutePath(),  // fileName
                sep,                            // sep
                tanggal,                        // tanggal
                noRawat,                        // norawat
                akses.getkode(),                // kode
                tanggaljam,                     // tanggaljam
                "INA-CBG"                       // inacbg
            );
            
            // Upload
            boolean success = uploader.upload();
            
            if(success){
                System.out.println("Upload to Nextcloud successful");
                // System.out.println("URL: " + uploader.getUploadedUrl());
            } else {
                System.err.println("Upload to Nextcloud failed");
            }
            
            return success;
            
        } catch(Exception e){
            System.err.println("Error uploading to Nextcloud: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Update berkas_klaim status
     */
    private boolean syncToHybridServer(java.io.File file, String fileName, String noRawat) {
        try {
            String uploadUrl = "http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + 
                              "/" + koneksiDB.HYBRIDWEB() + "/berkasklaim/unggah.php";
            
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost uploadFile = new HttpPost(uploadUrl);
            
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.EXTENDED);
            builder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, fileName);
            builder.addTextBody("no_rawat", noRawat, ContentType.TEXT_PLAIN);
            
            HttpEntity multipart = builder.build();
            uploadFile.setEntity(multipart);
            
            try (CloseableHttpResponse response = httpClient.execute(uploadFile)) {
                if (response.getCode() == 200) {
                    System.out.println("Sync to Hybrid Server successful: " + fileName);
                    return true;
                } else {
                    System.err.println("Sync to Hybrid Server failed with status code: " + response.getCode());
                }
            }
            httpClient.close();
        } catch (Exception e) {
            System.err.println("Error syncing to hybrid server: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    private void updateBerkasKlaimStatus(String noRawat, String status) {
        try {
            String sql = "UPDATE berkas_klaim SET status = ? WHERE no_rawat = ?";
            PreparedStatement ps = koneksi.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, noRawat);
            ps.executeUpdate();
            ps.close();
            
            System.out.println("Updated berkas_klaim status to: " + status);
            
        } catch(Exception e){
            System.err.println("Error updating status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void isCek() {
        BtnHapus.setEnabled(akses.getinacbg_coder_nik() || akses.getadmin());
    }

}
