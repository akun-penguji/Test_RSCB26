/*
  By Mas Elkhanza
  Registrasi Pasien BAYI BARU LAHIR ke SatuSehat MPI (POST Patient pakai NIK ibu).
  Bayi belum punya NIK -> didaftarkan dengan identifier system nik-ibu (NIK ibu) untuk
  mendapatkan IHS number bayi. Sumber: penilaian_bayi_baru_lahir (link ibu via no_rkm_medis_ibu)
  + pasien (identitas bayi & NIK ibu). Address wajib (administrativeCode dari config wilayah RS).
 */
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public final class SatuSehatKirimBayiBaruLahir extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i=0;
    private String link="",json="";
    private ApiSatuSehat api=new ApiSatuSehat();
    private HttpHeaders headers;
    private HttpEntity requestEntity;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode response;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean ceksukses = false;

    // 0=P,1=No.Rawat,2=No.RM Bayi,3=Nama Bayi,4=JK,5=Tgl Lahir,6=NIK Ibu,7=Nama Ibu,
    // 8=BB(g),9=PB(cm),10=LK(cm),11=APGAR(1/5/10),12=IHS Bayi
    public SatuSehatKirimBayiBaruLahir(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(10,2);
        setSize(628,674);
        tabMode=new DefaultTableModel(null,new String[]{
                "P","No.Rawat","No.RM Bayi","Nama Bayi","JK","Tgl Lahir","NIK Ibu","Nama Ibu",
                "BB (g)","PB (cm)","LK (cm)","APGAR 1/5/10","IHS Bayi"
            }){
              @Override public boolean isCellEditable(int r,int c){ return c==0; }
             Class[] types = new Class[]{ java.lang.Boolean.class,
                 java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,
                 java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,
                 java.lang.String.class,java.lang.String.class };
             @Override public Class getColumnClass(int c){ return types[c]; }
        };
        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        int[] w={20,105,80,150,30,90,120,150,55,55,55,90,200};
        for (i=0;i<w.length;i++) tbObat.getColumnModel().getColumn(i).setPreferredWidth(w[i]);
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
        cmbStatus.addActionListener(e -> runBackground(() -> tampil()));
        try { link=koneksiDB.URLFHIRSATUSEHAT(); } catch (Exception e) { System.out.println("Notif : "+e); }
        HTMLEditorKit kit = new HTMLEditorKit();
        LoadHTML.setEditable(true); LoadHTML.setEditorKit(kit);
        StyleSheet ss=kit.getStyleSheet(); ss.addRule(".isi td{font:8.5px tahoma;}");
        Document doc=kit.createDefaultDocument(); LoadHTML.setDocument(doc);
    }

    @SuppressWarnings("unchecked")
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
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel15 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel17 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel16 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel18 = new widget.Label();
        cmbStatus = new widget.ComboBox();

        jPopupMenu1.setName("jPopupMenu1");
        ppPilihSemua.setText("Pilih Semua"); ppPilihSemua.setName("ppPilihSemua"); ppPilihSemua.setPreferredSize(new java.awt.Dimension(150,26));
        ppPilihSemua.addActionListener(e -> { for(i=0;i<tbObat.getRowCount();i++) tbObat.setValueAt(true,i,0); });
        jPopupMenu1.add(ppPilihSemua);
        ppBersihkan.setText("Hilangkan Pilihan"); ppBersihkan.setName("ppBersihkan"); ppBersihkan.setPreferredSize(new java.awt.Dimension(150,26));
        ppBersihkan.addActionListener(e -> { for(i=0;i<tbObat.getRowCount();i++) tbObat.setValueAt(false,i,0); });
        jPopupMenu1.add(ppBersihkan);
        LoadHTML.setBorder(null); LoadHTML.setName("LoadHTML");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE); setUndecorated(true); setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter(){ public void windowOpened(java.awt.event.WindowEvent evt){ formWindowOpened(evt); } });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240,245,235)), "::[ Registrasi Bayi Baru Lahir (Patient) Satu Sehat ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma",0,11), new java.awt.Color(50,50,50)));
        internalFrame1.setName("internalFrame1"); internalFrame1.setLayout(new java.awt.BorderLayout(1,1));
        Scroll.setComponentPopupMenu(jPopupMenu1); Scroll.setName("Scroll"); Scroll.setOpaque(true);
        tbObat.setComponentPopupMenu(jPopupMenu1); tbObat.setName("tbObat"); Scroll.setViewportView(tbObat);
        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);
        jPanel3.setName("jPanel3"); jPanel3.setOpaque(false); jPanel3.setPreferredSize(new java.awt.Dimension(44,100)); jPanel3.setLayout(new java.awt.BorderLayout(1,1));
        panelGlass8.setName("panelGlass8"); panelGlass8.setPreferredSize(new java.awt.Dimension(44,44)); panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT,5,9));
        jLabel7.setText("Record :"); jLabel7.setName("jLabel7"); jLabel7.setPreferredSize(new java.awt.Dimension(53,23)); panelGlass8.add(jLabel7);
        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT); LCount.setText("0"); LCount.setName("LCount"); LCount.setPreferredSize(new java.awt.Dimension(60,23)); panelGlass8.add(LCount);
        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); BtnAll.setMnemonic('M'); BtnAll.setText("Semua"); BtnAll.setName("BtnAll"); BtnAll.setPreferredSize(new java.awt.Dimension(100,30));
        BtnAll.addActionListener(e -> { TCari.setText(""); runBackground(() -> tampil()); }); panelGlass8.add(BtnAll);
        BtnKirim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/34.png"))); BtnKirim.setMnemonic('K'); BtnKirim.setText("Daftar IHS"); BtnKirim.setToolTipText("Alt+K"); BtnKirim.setName("BtnKirim"); BtnKirim.setPreferredSize(new java.awt.Dimension(110,30));
        BtnKirim.addActionListener(e -> BtnKirimActionPerformed(e)); panelGlass8.add(BtnKirim);
        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); BtnKeluar.setText("Keluar"); BtnKeluar.setName("BtnKeluar"); BtnKeluar.setPreferredSize(new java.awt.Dimension(100,30));
        BtnKeluar.addActionListener(e -> dispose()); panelGlass8.add(BtnKeluar);
        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);
        panelGlass9.setName("panelGlass9"); panelGlass9.setPreferredSize(new java.awt.Dimension(44,44)); panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT,5,9));
        jLabel15.setText("Tgl.Triase/Lahir :"); jLabel15.setName("jLabel15"); jLabel15.setPreferredSize(new java.awt.Dimension(95,23)); panelGlass9.add(jLabel15);
        DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setName("DTPCari1"); DTPCari1.setOpaque(false); DTPCari1.setPreferredSize(new java.awt.Dimension(95,23)); panelGlass9.add(DTPCari1);
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER); jLabel17.setText("s.d."); jLabel17.setName("jLabel17"); jLabel17.setPreferredSize(new java.awt.Dimension(24,23)); panelGlass9.add(jLabel17);
        DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setName("DTPCari2"); DTPCari2.setOpaque(false); DTPCari2.setPreferredSize(new java.awt.Dimension(95,23)); panelGlass9.add(DTPCari2);
        jLabel16.setText("Key Word :"); jLabel16.setName("jLabel16"); jLabel16.setPreferredSize(new java.awt.Dimension(70,23)); panelGlass9.add(jLabel16);
        TCari.setName("TCari"); TCari.setPreferredSize(new java.awt.Dimension(190,23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter(){ public void keyPressed(java.awt.event.KeyEvent evt){ if(evt.getKeyCode()==java.awt.event.KeyEvent.VK_ENTER) runBackground(() -> tampil()); } });
        panelGlass9.add(TCari);
        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); BtnCari.setName("BtnCari"); BtnCari.setPreferredSize(new java.awt.Dimension(28,23));
        BtnCari.addActionListener(e -> runBackground(() -> tampil())); panelGlass9.add(BtnCari);
        jLabel18.setText("Status:"); jLabel18.setName("jLabel18"); jLabel18.setPreferredSize(new java.awt.Dimension(45,23)); panelGlass9.add(jLabel18);
        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Semua","Belum daftar","Sudah daftar"})); cmbStatus.setName("cmbStatus"); panelGlass9.add(cmbStatus);
        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);
        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
        pack();
    }

    /** Build Patient bayi baru lahir (identifier nik-ibu + address administrativeCode). */
    private String buildPatientJson(int row) {
        String nikIbu = tbObat.getValueAt(row,6).toString();
        String jk = tbObat.getValueAt(row,4).toString().equalsIgnoreCase("P") ? "female" : "male";
        String tglLahir = tbObat.getValueAt(row,5).toString();
        // Buang gelar/prefiks di nama ibu supaya tidak jadi "By Ny NY. ANU" (dobel).
        String nmIbu = tbObat.getValueAt(row,7).toString().replaceAll("\"","'")
                .replaceAll("(?i)^\\s*(ny|nn|sdri|tn)\\s*[.]?\\s+", "").trim();
        StringBuilder sb=new StringBuilder();
        sb.append("{\"resourceType\":\"Patient\",");
        sb.append("\"meta\":{\"profile\":[\"https://fhir.kemkes.go.id/r4/StructureDefinition/Patient\"]},");
        sb.append("\"identifier\":[{\"use\":\"official\",\"system\":\"https://fhir.kemkes.go.id/id/nik-ibu\",\"value\":\"").append(nikIbu).append("\"}],");
        sb.append("\"name\":[{\"use\":\"official\",\"text\":\"By Ny ").append(nmIbu).append("\"}],");
        sb.append("\"gender\":\"").append(jk).append("\",");
        sb.append("\"birthDate\":\"").append(tglLahir).append("\",");
        sb.append("\"multipleBirthInteger\":1,");
        sb.append("\"address\":[{\"use\":\"home\",\"line\":[\"-\"],\"city\":\"").append(koneksiDB.KABUPATENSATUSEHAT()).append("\",")
          .append("\"postalCode\":\"").append(koneksiDB.KODEPOSSATUSEHAT()).append("\",\"country\":\"ID\",")
          .append("\"extension\":[{\"url\":\"https://fhir.kemkes.go.id/r4/StructureDefinition/administrativeCode\",\"extension\":[")
          .append("{\"url\":\"province\",\"valueCode\":\"").append(koneksiDB.PROPINSISATUSEHAT()).append("\"},")
          .append("{\"url\":\"city\",\"valueCode\":\"").append(koneksiDB.KABUPATENSATUSEHAT()).append("\"},")
          .append("{\"url\":\"district\",\"valueCode\":\"").append(koneksiDB.KECAMATANSATUSEHAT()).append("\"},")
          .append("{\"url\":\"village\",\"valueCode\":\"").append(koneksiDB.KELURAHANSATUSEHAT()).append("\"}")
          .append("]}]}]}");
        return sb.toString();
    }

    /** Umur bayi dalam hari dari kolom tanggal lahir tabel (format yyyy-MM-dd). */
    private long umurHari(String tglLahir) {
        try {
            java.time.LocalDate lahir = java.time.LocalDate.parse(tglLahir.trim().substring(0, 10));
            return java.time.temporal.ChronoUnit.DAYS.between(lahir, java.time.LocalDate.now());
        } catch (Exception e) {
            return 0; // tidak bisa dibaca -> jangan halangi, biarkan server yang menilai
        }
    }

    private void BtnKirimActionPerformed(java.awt.event.ActionEvent evt) {
        int lewatUmur = 0;
        for(i=0;i<tbObat.getRowCount();i++){
            // syarat: dipilih, ada NIK ibu (16 digit), belum punya IHS bayi
            if(tbObat.getValueAt(i,0).toString().equals("true")
               && tbObat.getValueAt(i,6).toString().matches("\\d{16}")
               && tbObat.getValueAt(i,12).toString().equals("")){
                // Rule 10397: MPI menolak pendaftaran bayi via NIK ibu bila selisih
                // birthDate > 60 hari. Disaring di sini supaya tidak menembak API
                // dengan data yang pasti ditolak.
                long umurHari = umurHari(tbObat.getValueAt(i,5).toString());
                if(umurHari > 60){
                    System.out.println("Lewati "+tbObat.getValueAt(i,3)+" : umur "+umurHari
                            +" hari (>60), tidak memenuhi Rule 10397 pendaftaran bayi baru lahir");
                    lewatUmur++;
                    continue;
                }
                try{
                    headers = new HttpHeaders(); headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
                    json = buildPatientJson(i);
                    System.out.println("URL : "+link+"/Patient");
                    System.out.println("Request JSON : "+json);
                    requestEntity = new HttpEntity(json,headers);
                    json=api.getRest().exchange(link+"/Patient", HttpMethod.POST, requestEntity, String.class).getBody();
                    System.out.println("Result JSON : "+json);
                    root = mapper.readTree(json); response = root.path("id");
                    if(!response.asText().equals("")){
                        try {
                            PreparedStatement psS=koneksi.prepareStatement("replace into satu_sehat_bayi_baru_lahir(no_rawat,ihs_bayi,tgl_kirim) values(?,?,now())");
                            psS.setString(1,tbObat.getValueAt(i,1).toString());
                            psS.setString(2,response.asText());
                            psS.executeUpdate(); psS.close();
                        } catch (Exception e2) { System.out.println("Notif simpan : "+e2); }
                        tbObat.setValueAt(response.asText(),i,12);
                        tbObat.setValueAt(false,i,0);
                    }
                }catch(Exception e){
                    if(e instanceof org.springframework.web.client.HttpStatusCodeException)
                        System.out.println("Notifikasi Bridging : "+e+" => "+((org.springframework.web.client.HttpStatusCodeException)e).getResponseBodyAsString());
                    else System.out.println("Notifikasi Bridging : "+e);
                }
            }
        }
        if (lewatUmur > 0) {
            javax.swing.JOptionPane.showMessageDialog(this,
                lewatUmur + " bayi dilewati karena umurnya lebih dari 60 hari.\n\n"
                + "SatuSehat MPI menolak pendaftaran lewat NIK ibu bila selisih tanggal lahir\n"
                + "lebih dari 60 hari (Rule 10397). Bayi selama itu harus didaftarkan memakai\n"
                + "NIK bayi sendiri setelah akta/KK terbit.");
        }
    }

    private void formWindowOpened(java.awt.event.WindowEvent evt) {
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override public void insertUpdate(DocumentEvent e){ if(TCari.getText().length()>2) runBackground(() -> tampil()); }
                @Override public void removeUpdate(DocumentEvent e){ if(TCari.getText().length()>2) runBackground(() -> tampil()); }
                @Override public void changedUpdate(DocumentEvent e){ if(TCari.getText().length()>2) runBackground(() -> tampil()); }
            });
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            SatuSehatKirimBayiBaruLahir d = new SatuSehatKirimBayiBaruLahir(new javax.swing.JFrame(), true);
            d.addWindowListener(new java.awt.event.WindowAdapter(){ @Override public void windowClosing(java.awt.event.WindowEvent e){ System.exit(0); } });
            d.setVisible(true);
        });
    }

    private widget.Button BtnAll;
    private widget.Button BtnCari;
    private widget.Button BtnKeluar;
    private widget.Button BtnKirim;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.Label LCount;
    private widget.editorpane LoadHTML;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.ComboBox cmbStatus;
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

    private void tampil() {
        Valid.tabelKosong(tabMode);
        try{
            try {
                ps=koneksi.prepareStatement("CREATE TABLE IF NOT EXISTS satu_sehat_bayi_baru_lahir (no_rawat varchar(17) NOT NULL, ihs_bayi varchar(60) DEFAULT NULL, tgl_kirim datetime DEFAULT NULL, PRIMARY KEY (no_rawat)) ENGINE=InnoDB DEFAULT CHARSET=latin1");
                ps.execute(); ps.close();
            } catch (Exception e) {}
            ps=koneksi.prepareStatement(
                // Sumber ibu: (1) form Penilaian Bayi Baru Lahir bila diisi, (2) tabel
                // tautan bayi_ibu_mapping hasil pencocokan nm_ibu + ranap ibu saat bayi
                // lahir (sql/106). Tanpa (2), bayi yang formnya belum diisi tidak pernah
                // bisa didaftarkan ke MPI padahal NIK ibunya diketahui.
                "select src.no_rawat,rp.no_rkm_medis,byp.nm_pasien as nm_bayi,byp.jk,byp.tgl_lahir,"+
                "ifnull(ibu.no_ktp,'') as nik_ibu,ibu.nm_pasien as nm_ibu,"+
                "src.bblahir,src.pb,src.lk,src.apgar,"+
                "ifnull(sbl.ihs_bayi,'') as ihs_bayi "+
                "from ("+
                "  select bbl.no_rawat,bbl.no_rkm_medis_ibu,bbl.tanggal,"+
                "         ifnull(bbl.bblahir,'') as bblahir,ifnull(bbl.panjang_badan,'') as pb,"+
                "         ifnull(bbl.lingkar_kepala,'') as lk,"+
                "         concat(ifnull(bbl.n1,'-'),'/',ifnull(bbl.n5,'-'),'/',ifnull(bbl.n10,'-')) as apgar"+
                "  from penilaian_bayi_baru_lahir bbl where bbl.no_rkm_medis_ibu is not null"+
                "  union all"+
                "  select bim.no_rawat_bayi,bim.no_rkm_medis_ibu,rpb.tgl_registrasi,'','','','-/-/-'"+
                "  from bayi_ibu_mapping bim"+
                "  inner join reg_periksa rpb on rpb.no_rawat=bim.no_rawat_bayi"+
                "  where bim.status<>'Ditolak' and bim.no_rkm_medis_ibu is not null"+
                "    and not exists (select 1 from penilaian_bayi_baru_lahir b2"+
                "                    where b2.no_rawat=bim.no_rawat_bayi and b2.no_rkm_medis_ibu is not null)"+
                ") src "+
                "inner join reg_periksa rp on rp.no_rawat=src.no_rawat "+
                "inner join pasien byp on byp.no_rkm_medis=rp.no_rkm_medis "+
                "inner join pasien ibu on ibu.no_rkm_medis=src.no_rkm_medis_ibu "+
                "left join satu_sehat_bayi_baru_lahir sbl on sbl.no_rawat=src.no_rawat "+
                "where src.tanggal between ? and ? "+
                (cmbStatus.getSelectedItem().toString().equals("Belum daftar")?"and (sbl.ihs_bayi is null or sbl.ihs_bayi='') ":"")+
                (cmbStatus.getSelectedItem().toString().equals("Sudah daftar")?"and sbl.ihs_bayi is not null and sbl.ihs_bayi!='' ":"")+
                (TCari.getText().equals("")?"":"and (src.no_rawat like ? or byp.nm_pasien like ? or ibu.nm_pasien like ? or ibu.no_ktp like ?) "));
            try {
                ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
                ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
                if(!TCari.getText().equals("")){
                    ps.setString(3,"%"+TCari.getText()+"%"); ps.setString(4,"%"+TCari.getText()+"%");
                    ps.setString(5,"%"+TCari.getText()+"%"); ps.setString(6,"%"+TCari.getText()+"%");
                }
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        false,rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_bayi"),rs.getString("jk"),
                        rs.getString("tgl_lahir"),rs.getString("nik_ibu"),rs.getString("nm_ibu"),
                        rs.getString("bblahir"),rs.getString("pb"),rs.getString("lk"),rs.getString("apgar"),rs.getString("ihs_bayi")
                    });
                }
            } catch (Exception e) { System.out.println("Notif : "+e);
            } finally{ if(rs!=null) rs.close(); if(ps!=null) ps.close(); }
        }catch(Exception e){ System.out.println("Notifikasi : "+e); }
        LCount.setText(""+tabMode.getRowCount());
    }

    public void isCek(){ BtnKirim.setEnabled(akses.getsatu_sehat_kirim_condition()); }
    public JTable getTable(){ return tbObat; }

    private void runBackground(Runnable task) {
        if (ceksukses) return; if (executor.isShutdown() || executor.isTerminated()) return; if (!isDisplayable()) return;
        ceksukses = true; setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try { executor.submit(() -> { try { task.run(); } finally { ceksukses = false; SwingUtilities.invokeLater(() -> { if (isDisplayable()) setCursor(Cursor.getDefaultCursor()); }); } });
        } catch (RejectedExecutionException ex) { ceksukses = false; }
    }
    @Override public void dispose() { executor.shutdownNow(); super.dispose(); }
}
