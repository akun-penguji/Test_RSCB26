package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import javax.swing.JOptionPane;

/**
 * Form Bridging EpisodeOfCare SATUSEHAT for ANC
 */
public final class SatuSehatKirimEpisodeOfCare extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;   
    private int i=0;
    private String link="",json="",idpasien="",idorg="";
    private ApiSatuSehat api=new ApiSatuSehat();
    private HttpHeaders headers ;
    private HttpEntity requestEntity;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode response;
    private SatuSehatCekNIK cekViaSatuSehat=new SatuSehatCekNIK();    
    private StringBuilder htmlContent; 
    
    public SatuSehatKirimEpisodeOfCare(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.setLocation(10,2);
        setSize(628,674);

        tabMode=new DefaultTableModel(null,new String[]{
                "P","Tanggal Kunjungan","No.Rawat","No.RM","Nama Pasien","No.KTP","Umur Kehamilan","ID Episode Of Care"
            }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if (colIndex==0) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                 java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, 
                 java.lang.String.class, java.lang.String.class, java.lang.String.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 8; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(110);
            }else if(i==2){
                column.setPreferredWidth(105);
            }else if(i==3){
                column.setPreferredWidth(70);
            }else if(i==4){
                column.setPreferredWidth(150);
            }else if(i==5){
                column.setPreferredWidth(110);
            }else if(i==6){
                column.setPreferredWidth(100);
            }else if(i==7){
                column.setPreferredWidth(215);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());
        
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
        
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampil();
                    }
                }
            });
        } 
        
        try {
            link=koneksiDB.URLFHIRSATUSEHAT();
            idorg=koneksiDB.IDSATUSEHAT(); 
        } catch (Exception e) {
            System.out.println("Notif : "+e);
        }  
        
        HTMLEditorKit kit = new HTMLEditorKit();
        LoadHTML.setEditable(true);
        LoadHTML.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule(
                ".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}"+
                ".isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                ".isi5 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#AA0000;}"+
                ".isi6 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#FF0000;}"+
                ".isi7 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#C8C800;}"+
                ".isi8 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#00AA00;}"+
                ".isi9 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#969696;}"
        );
        Document doc = kit.createDefaultDocument();
        LoadHTML.setDocument(doc);
    }
    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        jPopupMenu1 = new javax.swing.JPopupMenu();
        ppPilihSemua = new javax.swing.JMenuItem();
        ppBersihkan = new javax.swing.JMenuItem();
        LoadHTML = new widget.editorpane();
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        BtnAll = new widget.Button();
        BtnKirim = new widget.Button();
        BtnUpdate = new widget.Button();
        BtnPrint = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel15 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel17 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel16 = new widget.Label();
        TCari = new widget.TextBox();
        jLabel18 = new widget.Label();
        cmbStatusKirim = new widget.ComboBox();
        BtnCari = new widget.Button();

        jPopupMenu1.setName("jPopupMenu1"); 
        ppPilihSemua.setBackground(new java.awt.Color(255, 255, 254));
        ppPilihSemua.setFont(new java.awt.Font("Tahoma", 0, 11)); 
        ppPilihSemua.setForeground(new java.awt.Color(50, 50, 50));
        ppPilihSemua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); 
        ppPilihSemua.setText("Pilih Semua");
        ppPilihSemua.setName("ppPilihSemua"); 
        ppPilihSemua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                int row = tabMode.getRowCount();
                for (int i = 0; i < row; i++) {
                    tbObat.setValueAt(Boolean.TRUE, i, 0);
                }
            }
        });
        jPopupMenu1.add(ppPilihSemua);

        ppBersihkan.setBackground(new java.awt.Color(255, 255, 254));
        ppBersihkan.setFont(new java.awt.Font("Tahoma", 0, 11)); 
        ppBersihkan.setForeground(new java.awt.Color(50, 50, 50));
        ppBersihkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); 
        ppBersihkan.setText("Hilangkan Pilihan");
        ppBersihkan.setName("ppBersihkan"); 
        ppBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                int row = tabMode.getRowCount();
                for (int i = 0; i < row; i++) {
                    tbObat.setValueAt(Boolean.FALSE, i, 0);
                }
            }
        });
        jPopupMenu1.add(ppBersihkan);
        
        LoadHTML.setBorder(null);
        LoadHTML.setName("LoadHTML"); 
        
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        
        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Pengiriman Data EpisodeOfCare SATUSEHAT ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); 
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));
        
        Scroll.setComponentPopupMenu(jPopupMenu1);
        Scroll.setName("Scroll"); 
        tbObat.setComponentPopupMenu(jPopupMenu1);
        tbObat.setName("tbObat"); 
        Scroll.setViewportView(tbObat);
        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);
        
        jPanel3.setName("jPanel3"); 
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));
        
        panelGlass8.setName("panelGlass8"); 
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));
        
        jLabel7.setText("Record :");
        jLabel7.setPreferredSize(new java.awt.Dimension(53, 23));
        panelGlass8.add(jLabel7);
        
        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass8.add(LCount);
        
        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); 
        BtnAll.setMnemonic('M');
        BtnAll.setText("Semua");
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TCari.setText("");
                tampil();
            }
        });
        panelGlass8.add(BtnAll);
        
        BtnKirim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/34.png"))); 
        BtnKirim.setMnemonic('K');
        BtnKirim.setText("Kirim");
        BtnKirim.setToolTipText("Alt+K");
        BtnKirim.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKirim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKirimActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnKirim);
        
        BtnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/edit_f2.png"))); 
        BtnUpdate.setMnemonic('U');
        BtnUpdate.setText("Update");
        BtnUpdate.setToolTipText("Alt+U");
        BtnUpdate.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnUpdateActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnUpdate);
        
        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); 
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispose();
            }
        });
        panelGlass8.add(BtnKeluar);
        
        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);
        
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));
        
        jLabel15.setText("Tgl.Kunjungan :");
        jLabel15.setPreferredSize(new java.awt.Dimension(85, 23));
        panelGlass9.add(jLabel15);
        
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "25-12-2025" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari1);
        
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("s.d.");
        jLabel17.setPreferredSize(new java.awt.Dimension(24, 23));
        panelGlass9.add(jLabel17);
        
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "25-12-2025" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari2);
        
        jLabel16.setText("Key Word :");
        jLabel16.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel16);
        
        TCari.setPreferredSize(new java.awt.Dimension(210, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if(evt.getKeyCode()==KeyEvent.VK_ENTER){
                    tampil();
                }
            }
        });
        panelGlass9.add(TCari);
        
        jLabel18.setText("Status Kirim :");
        jLabel18.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass9.add(jLabel18);
        
        cmbStatusKirim.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semua", "Belum Terkirim", "Sudah Terkirim" }));
        cmbStatusKirim.setPreferredSize(new java.awt.Dimension(120, 23));
        panelGlass9.add(cmbStatusKirim);
        
        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); 
        BtnCari.setMnemonic('6');
        BtnCari.setToolTipText("Alt+6");
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tampil();
            }
        });
        panelGlass9.add(BtnCari);
        
        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);
        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
        
        pack();
    }// </editor-fold>

    private void tampil() {
        Valid.tabelKosong(tabMode);
        try{
            ps=koneksi.prepareStatement(
                   "select data_anc.tgl_kunjungan,data_anc.no_rawat,data_anc.no_rkm_medis,pasien.nm_pasien,pasien.no_ktp,"+
                   "data_anc.usia_kehamilan_minggu,ifnull(satu_sehat_episodeofcare.id_episode_of_care,'') as id_episode_of_care "+
                   "from data_anc inner join pasien on data_anc.no_rkm_medis=pasien.no_rkm_medis "+
                   "left join satu_sehat_episodeofcare on data_anc.no_rawat=satu_sehat_episodeofcare.no_rawat "+
                   "where data_anc.tgl_kunjungan between ? and ? "+
                   (TCari.getText().trim().equals("")?"":"and (data_anc.no_rawat like ? or pasien.nm_pasien like ? or pasien.no_ktp like ?)")+
                   (cmbStatusKirim.getSelectedIndex()==0?"":" and ifnull(satu_sehat_episodeofcare.id_episode_of_care,'') "+(cmbStatusKirim.getSelectedIndex()==1?"=''":"<>''"))+
                   " order by data_anc.tgl_kunjungan");
            try {
                ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
                ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
                if(!TCari.getText().trim().equals("")){
                    ps.setString(3,"%"+TCari.getText()+"%");
                    ps.setString(4,"%"+TCari.getText()+"%");
                    ps.setString(5,"%"+TCari.getText()+"%");
                }
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        false,rs.getString("tgl_kunjungan"),rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),
                        rs.getString("no_ktp"),rs.getString("usia_kehamilan_minggu")+" Minggu",rs.getString("id_episode_of_care")
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
        LCount.setText(""+tabMode.getRowCount());
    }

    private void BtnKirimActionPerformed(java.awt.event.ActionEvent evt) {
        for(i=0;i<tbObat.getRowCount();i++){
            if(tbObat.getValueAt(i,0).toString().equals("true")&&tbObat.getValueAt(i,7).toString().equals("")){
                try {
                    idpasien=cekViaSatuSehat.tampilIDPasien(tbObat.getValueAt(i,3).toString()); // Cek ID SatuSehat via No RM (Mapped) or KTP
                    try{
                        if(!idpasien.equals("")){
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
                            
                            // JSON EpisodeOfCare
                            json = "{" +
                                   "\"resourceType\": \"EpisodeOfCare\"," +
                                   "\"status\": \"active\"," +
                                   "\"type\": [" +
                                   "  {" +
                                   "    \"coding\": [" +
                                   "      {" +
                                   "        \"system\": \"http://terminology.kemkes.go.id/CodeSystem/episodeofcare-type\"," +
                                   // Layar ini sumbernya tabel data_anc, jadi tipenya memang ANC.
                                   // Sebelumnya "uncoded" — kode itu TIDAK ADA di CodeSystem sehingga
                                   // setiap kiriman pasti 400: "Code not found: 'uncoded' ...
                                   // (RuleNumber: 10110)". Kode yang sah cuma ANC, PNC, CAD, CVD,
                                   // CKD, dan Neonate.
                                   "        \"code\": \"" + SatuSehatEpisodeOfCareHelper.TYPE_ANC[0] + "\"," +
                                   "        \"display\": \"" + SatuSehatEpisodeOfCareHelper.TYPE_ANC[1] + "\"" +
                                   "      }" +
                                   "    ]" +
                                   "  }" +
                                   "]," +
                                   "\"patient\": {" +
                                   "  \"reference\": \"Patient/"+idpasien+"\"" +
                                   "}," +
                                   "\"managingOrganization\": {" +
                                   "  \"reference\": \"Organization/"+idorg+"\"" +
                                   "}," +
                                   "\"period\": {" +
                                   "  \"start\": \""+fungsi.IsoTime.toUtc(tbObat.getValueAt(i,1).toString())+"\"" +
                                   "}" +
                                   "}";
                                   
                            System.out.println("Sending EpisodeOfCare: "+json);
                            requestEntity = new HttpEntity(json,headers);
                            json=api.getRest().exchange(link+"/EpisodeOfCare", HttpMethod.POST, requestEntity, String.class).getBody();
                            System.out.println("Response: "+json);
                            root = mapper.readTree(json);
                            response = root.path("id");
                            if(!response.asText().equals("")){
                                Sequel.menyimpan("satu_sehat_episodeofcare","?,?","Episode Of Care",2,new String[]{
                                    tbObat.getValueAt(i,2).toString(),response.asText()
                                });
                            }
                        }else{
                            System.out.println("ID SatuSehat Pasien tidak ditemukan for "+tbObat.getValueAt(i,2).toString());
                        }
                    }catch(Exception e){
                        System.out.println("Notif : "+e);
                    }
                } catch (Exception e) {
                    System.out.println("Notif : "+e);
                }
            }
        }
        tampil();
    }

    private void BtnUpdateActionPerformed(java.awt.event.ActionEvent evt) {
        // Implement update logic if needed
        JOptionPane.showMessageDialog(null,"Fitur Update belum tersedia");
    }

    private widget.Button BtnAll;
    private widget.Button BtnCari;
    private widget.Button BtnKeluar;
    private widget.Button BtnKirim;
    private widget.Button BtnPrint;
    private widget.Button BtnUpdate;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.Label LCount;
    private widget.editorpane LoadHTML;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.ComboBox cmbStatusKirim;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel7;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private javax.swing.JMenuItem ppBersihkan;
    private javax.swing.JMenuItem ppPilihSemua;
    private widget.Table tbObat;
    
    // Additional import for JOptionPane

    public void isCek(){
        BtnKirim.setEnabled(true);
        BtnPrint.setEnabled(true);
    }

}
