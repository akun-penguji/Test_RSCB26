/*
  By dr. Salim Mulyana, S.Kom, MM
  Tahap #11-12 & #14 Playbook Klaim BPJS-K v1.3 Kemkes:
  Khanza polling GET ClaimResponse?request=Claim/{id} untuk ambil status
  Purifikasi (TK000047/TK000048) + Verifikasi (Layak/Tidak/Pending/Kedaluwarsa).
  Khanza adalah Java Swing desktop app (BUKAN HTTP server), webhook receiver
  tidak feasible, jadi pakai polling approach.
 */
package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable;
import fungsi.akses;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public final class SatuSehatCekClaimResponse extends javax.swing.JDialog {
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
    private StringBuilder htmlContent;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean ceksukses = false;

    // 0=P, 1=No.Rawat, 2=No.RM, 3=Nama Pasien, 4=ID Claim, 5=Sub Type, 6=Tgl Claim,
    // 7=ID CR Purifikasi, 8=Status Purifikasi, 9=ID CR Verifikasi, 10=Status Verifikasi,
    // 11=Tgl Sync, 12=Disposition
    public SatuSehatCekClaimResponse(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(10,2);
        setSize(1180,720);

        tabMode=new DefaultTableModel(null,new String[]{
                "P","No.Rawat","No.RM","Nama Pasien","ID Claim","Sub Type","Tgl Claim",
                "ID CR Purifikasi","Status Purifikasi","ID CR Verifikasi","Status Verifikasi",
                "Tgl Sync","Disposition"
            }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){ return colIndex==0; }
             Class[] types = new Class[] {
                 java.lang.Boolean.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,
                 java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,
                 java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,
                 java.lang.String.class
             };
             @Override public Class getColumnClass(int columnIndex) { return types[columnIndex]; }
        };
        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (i = 0; i < 13; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0) column.setPreferredWidth(25);
            else if(i==1) column.setPreferredWidth(105);
            else if(i==2) column.setPreferredWidth(70);
            else if(i==3) column.setPreferredWidth(150);
            else if(i==4) column.setPreferredWidth(170);
            else if(i==5) column.setPreferredWidth(80);
            else if(i==6) column.setPreferredWidth(130);
            else if(i==7) column.setPreferredWidth(170);
            else if(i==8) column.setPreferredWidth(110);
            else if(i==9) column.setPreferredWidth(170);
            else if(i==10) column.setPreferredWidth(120);
            else if(i==11) column.setPreferredWidth(130);
            else if(i==12) column.setPreferredWidth(220);
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());
        tbObat.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting()){
                int row=tbObat.getSelectedRow();
                if(row<0) return;
                String noRawat=tbObat.getValueAt(row,1).toString();
                if(noRawat.equals("")){ taRaw.setText(""); return; }
                try{
                    ps=koneksi.prepareStatement("select ifnull(raw_response_purifikasi,'') as rp, ifnull(raw_response_verifikasi,'') as rv from satu_sehat_claimresponse where no_rawat=?");
                    ps.setString(1,noRawat);
                    rs=ps.executeQuery();
                    if(rs.next()){
                        String rp=rs.getString("rp"); String rv=rs.getString("rv");
                        StringBuilder sb=new StringBuilder();
                        if(!rp.equals("")) sb.append("=== PURIFIKASI ===\n").append(rp).append("\n\n");
                        if(!rv.equals("")) sb.append("=== VERIFIKASI ===\n").append(rv);
                        taRaw.setText(sb.toString());
                    } else taRaw.setText("");
                    rs.close(); ps.close();
                }catch(Exception ex){ System.out.println("Notif raw : "+ex); }
            }
        });
        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
        cmbStatus.addActionListener(e -> runBackground(() -> tampil()));
        try { link=koneksiDB.URLFHIRSATUSEHAT(); } catch (Exception e) { System.out.println("Notif : "+e); }
        HTMLEditorKit kit = new HTMLEditorKit();
        LoadHTML.setEditable(true); LoadHTML.setEditorKit(kit);
        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule(".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}");
        Document doc = kit.createDefaultDocument();
        LoadHTML.setDocument(doc);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        LoadHTML = new widget.editorpane();
        internalFrame1 = new widget.InternalFrame();
        splitPane1 = new javax.swing.JSplitPane();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        ScrollRaw = new widget.ScrollPane();
        taRaw = new widget.TextArea();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        BtnAll = new widget.Button();
        BtnCek = new widget.Button();
        BtnCekSemua = new widget.Button();
        BtnPrint = new widget.Button();
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

        LoadHTML.setBorder(null); LoadHTML.setName("LoadHTML");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE); setIconImage(null); setIconImages(null); setUndecorated(true); setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() { public void windowOpened(java.awt.event.WindowEvent evt) { formWindowOpened(evt); } });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240,245,235)), "::[ Cek ClaimResponse (Purifikasi + Verifikasi) BPJS-K Satu Sehat ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma",0,11), new java.awt.Color(50,50,50)));
        internalFrame1.setFont(new java.awt.Font("Tahoma",0,11)); internalFrame1.setName("internalFrame1"); internalFrame1.setLayout(new java.awt.BorderLayout(1,1));

        splitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT); splitPane1.setDividerLocation(420); splitPane1.setName("splitPane1");
        Scroll.setName("Scroll"); Scroll.setOpaque(true);
        tbObat.setName("tbObat"); Scroll.setViewportView(tbObat);
        splitPane1.setTopComponent(Scroll);
        ScrollRaw.setName("ScrollRaw");
        taRaw.setName("taRaw"); taRaw.setEditable(false); taRaw.setLineWrap(true); taRaw.setWrapStyleWord(true);
        taRaw.setFont(new java.awt.Font("Monospaced",0,11));
        ScrollRaw.setViewportView(taRaw);
        splitPane1.setBottomComponent(ScrollRaw);
        internalFrame1.add(splitPane1, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); jPanel3.setOpaque(false); jPanel3.setPreferredSize(new java.awt.Dimension(44,100)); jPanel3.setLayout(new java.awt.BorderLayout(1,1));
        panelGlass8.setName("panelGlass8"); panelGlass8.setPreferredSize(new java.awt.Dimension(44,44)); panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT,5,9));
        jLabel7.setText("Record :"); jLabel7.setName("jLabel7"); jLabel7.setPreferredSize(new java.awt.Dimension(53,23)); panelGlass8.add(jLabel7);
        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT); LCount.setText("0"); LCount.setName("LCount"); LCount.setPreferredSize(new java.awt.Dimension(60,23)); panelGlass8.add(LCount);
        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); BtnAll.setMnemonic('M'); BtnAll.setText("Semua"); BtnAll.setToolTipText("Alt+M"); BtnAll.setName("BtnAll"); BtnAll.setPreferredSize(new java.awt.Dimension(100,30));
        BtnAll.addActionListener(e -> { TCari.setText(""); runBackground(() -> tampil()); }); panelGlass8.add(BtnAll);
        BtnCek.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); BtnCek.setMnemonic('C'); BtnCek.setText("Cek"); BtnCek.setToolTipText("Alt+C"); BtnCek.setName("BtnCek"); BtnCek.setPreferredSize(new java.awt.Dimension(100,30));
        BtnCek.addActionListener(e -> BtnCekActionPerformed(e)); panelGlass8.add(BtnCek);
        BtnCekSemua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); BtnCekSemua.setMnemonic('S'); BtnCekSemua.setText("Cek Semua"); BtnCekSemua.setToolTipText("Alt+S"); BtnCekSemua.setName("BtnCekSemua"); BtnCekSemua.setPreferredSize(new java.awt.Dimension(110,30));
        BtnCekSemua.addActionListener(e -> runBackground(() -> BtnCekSemuaActionPerformed(e))); panelGlass8.add(BtnCekSemua);
        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); BtnPrint.setMnemonic('T'); BtnPrint.setText("Cetak"); BtnPrint.setToolTipText("Alt+T"); BtnPrint.setName("BtnPrint"); BtnPrint.setPreferredSize(new java.awt.Dimension(100,30));
        BtnPrint.addActionListener(e -> BtnPrintActionPerformed(e)); panelGlass8.add(BtnPrint);
        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); BtnKeluar.setMnemonic('K'); BtnKeluar.setText("Keluar"); BtnKeluar.setToolTipText("Alt+K"); BtnKeluar.setName("BtnKeluar"); BtnKeluar.setPreferredSize(new java.awt.Dimension(100,30));
        BtnKeluar.addActionListener(e -> dispose()); panelGlass8.add(BtnKeluar);
        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); panelGlass9.setPreferredSize(new java.awt.Dimension(44,44)); panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT,5,9));
        jLabel15.setText("Tgl.Kirim Claim :"); jLabel15.setName("jLabel15"); jLabel15.setPreferredSize(new java.awt.Dimension(95,23)); panelGlass9.add(jLabel15);
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"01-01-2026"})); DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setName("DTPCari1"); DTPCari1.setOpaque(false); DTPCari1.setPreferredSize(new java.awt.Dimension(95,23)); panelGlass9.add(DTPCari1);
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER); jLabel17.setText("s.d."); jLabel17.setName("jLabel17"); jLabel17.setPreferredSize(new java.awt.Dimension(24,23)); panelGlass9.add(jLabel17);
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"31-12-2026"})); DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setName("DTPCari2"); DTPCari2.setOpaque(false); DTPCari2.setPreferredSize(new java.awt.Dimension(95,23)); panelGlass9.add(DTPCari2);
        jLabel16.setText("Key Word :"); jLabel16.setName("jLabel16"); jLabel16.setPreferredSize(new java.awt.Dimension(70,23)); panelGlass9.add(jLabel16);
        TCari.setName("TCari"); TCari.setPreferredSize(new java.awt.Dimension(210,23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() { public void keyPressed(java.awt.event.KeyEvent evt) { if(evt.getKeyCode()==KeyEvent.VK_ENTER) runBackground(() -> tampil()); } });
        panelGlass9.add(TCari);
        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); BtnCari.setMnemonic('6'); BtnCari.setToolTipText("Alt+6"); BtnCari.setName("BtnCari"); BtnCari.setPreferredSize(new java.awt.Dimension(28,23));
        BtnCari.addActionListener(e -> runBackground(() -> tampil())); panelGlass9.add(BtnCari);
        jLabel18.setText("Status:"); jLabel18.setName("jLabel18"); jLabel18.setPreferredSize(new java.awt.Dimension(40,23)); panelGlass9.add(jLabel18);
        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Semua","Belum Sync","Purifikasi Pending","Verifikasi Pending","Selesai"})); cmbStatus.setName("cmbStatus"); panelGlass9.add(cmbStatus);
        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);
        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnCekActionPerformed(java.awt.event.ActionEvent evt) {
        int row=tbObat.getSelectedRow();
        if(row<0){ JOptionPane.showMessageDialog(null,"Pilih salah satu baris data terlebih dahulu..!!"); return; }
        String idClaim=tbObat.getValueAt(row,4).toString();
        if(idClaim.equals("")){ JOptionPane.showMessageDialog(null,"ID Claim kosong, tidak bisa GET ClaimResponse."); return; }
        runBackground(() -> { cekClaimResponse(row,idClaim); });
    }

    private void BtnCekSemuaActionPerformed(java.awt.event.ActionEvent evt) {
        for(i=0;i<tbObat.getRowCount();i++){
            String idClaim=tbObat.getValueAt(i,4).toString();
            if(idClaim.equals("")) continue;
            cekClaimResponse(i,idClaim);
            try{ Thread.sleep(100); }catch(InterruptedException ex){ Thread.currentThread().interrupt(); break; }
        }
    }

    /** Map outcome.coding.code → bucket "purifikasi"|"verifikasi" + status text. */
    private String[] classifyOutcome(String code, String display){
        String lc = (code==null?"":code).toLowerCase();
        String ld = (display==null?"":display).toLowerCase();
        // Purifikasi: code TK000047/TK000048 atau display "purifikasi"
        if(lc.contains("tk000047")||ld.contains("lolos purifikasi")||ld.contains("lulus purifikasi")) return new String[]{"purifikasi","Lolos"};
        if(lc.contains("tk000048")||ld.contains("tidak lolos purifikasi")||ld.contains("tidak lulus purifikasi")) return new String[]{"purifikasi","Tidak Lolos"};
        if(ld.contains("purifikasi")) return new String[]{"purifikasi","Pending"};
        // Verifikasi
        if(lc.equals("complete")||ld.contains("layak")) return new String[]{"verifikasi","Layak"};
        if(ld.contains("tidak layak")||ld.contains("tidak-layak")) return new String[]{"verifikasi","Tidak Layak"};
        if(ld.contains("kedaluwarsa")||ld.contains("expired")) return new String[]{"verifikasi","Kedaluwarsa"};
        if(lc.equals("queued")||lc.equals("partial")||ld.contains("verifikasi")) return new String[]{"verifikasi","Pending"};
        // fallback: treat as verifikasi pending
        return new String[]{"verifikasi","Pending"};
    }

    private void cekClaimResponse(int row,String idClaim){
        try{
            String noRawat = tbObat.getValueAt(row,1).toString();
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
            requestEntity = new HttpEntity(headers);
            String url=link+"/ClaimResponse?request=Claim/"+idClaim;
            System.out.println("URL : "+url);
            json=api.getRest().exchange(url, HttpMethod.GET, requestEntity, String.class).getBody();
            System.out.println("Result JSON : "+json);
            root = mapper.readTree(json);

            String idPur="",statPur="",dispPur="",rawPur="";
            String idVer="",statVer="",dispVer="",rawVer="";
            JsonNode entries=root.path("entry");
            if(entries.isArray()){
                for(JsonNode entry:entries){
                    JsonNode resource=entry.path("resource");
                    if(!resource.path("resourceType").asText().equals("ClaimResponse")) continue;
                    String rId=resource.path("id").asText();
                    String disp=resource.path("disposition").asText();
                    // ambil outcome.coding[0] (R4 uses outcome as code; SatuSehat may extend ke CodeableConcept)
                    String code="",display="";
                    JsonNode outcome=resource.path("outcome");
                    if(outcome.isTextual()){
                        code=outcome.asText();
                    } else if(outcome.isObject()){
                        JsonNode coding=outcome.path("coding");
                        if(coding.isArray() && coding.size()>0){
                            code=coding.get(0).path("code").asText();
                            display=coding.get(0).path("display").asText();
                        }
                    }
                    String[] cls=classifyOutcome(code,display);
                    String bucket=cls[0]; String stat=cls[1];
                    String rawEntry=resource.toString();
                    if(bucket.equals("purifikasi")){
                        if(idPur.equals("")||!stat.equals("Pending")){
                            idPur=rId; statPur=stat; dispPur=disp; rawPur=rawEntry;
                        }
                    } else {
                        if(idVer.equals("")||!stat.equals("Pending")){
                            idVer=rId; statVer=stat; dispVer=disp; rawVer=rawEntry;
                        }
                    }
                }
            }

            String now=Sequel.cariIsi("select date_format(now(),'%Y-%m-%d %H:%i:%s')");
            final String fIdP=idPur,fStP=statPur,fDsP=dispPur,fRwP=rawPur;
            final String fIdV=idVer,fStV=statVer,fDsV=dispVer,fRwV=rawVer;
            final String fNow=now;
            final String fDisp = !dispVer.equals("") ? dispVer : dispPur;

            SwingUtilities.invokeLater(() -> {
                tbObat.setValueAt(fIdP,row,7);
                tbObat.setValueAt(fStP.equals("")?"-":fStP,row,8);
                tbObat.setValueAt(fIdV,row,9);
                tbObat.setValueAt(fStV.equals("")?"-":fStV,row,10);
                tbObat.setValueAt(fNow,row,11);
                tbObat.setValueAt(fDisp,row,12);
                taRaw.setText(json);
            });

            Sequel.queryu2tf(
                "insert into satu_sehat_claimresponse"+
                "(no_rawat,id_claimresponse_purifikasi,status_purifikasi,disposition_purifikasi,"+
                "id_claimresponse_verifikasi,status_verifikasi,disposition_verifikasi,"+
                "raw_response_purifikasi,raw_response_verifikasi,tgl_sync) "+
                "values(?,?,?,?,?,?,?,?,?,now()) "+
                "on duplicate key update "+
                "id_claimresponse_purifikasi=values(id_claimresponse_purifikasi),"+
                "status_purifikasi=values(status_purifikasi),"+
                "disposition_purifikasi=values(disposition_purifikasi),"+
                "id_claimresponse_verifikasi=values(id_claimresponse_verifikasi),"+
                "status_verifikasi=values(status_verifikasi),"+
                "disposition_verifikasi=values(disposition_verifikasi),"+
                "raw_response_purifikasi=values(raw_response_purifikasi),"+
                "raw_response_verifikasi=values(raw_response_verifikasi),"+
                "tgl_sync=values(tgl_sync)",
                9,new String[]{noRawat,idPur,statPur,dispPur,idVer,statVer,dispVer,rawPur,rawVer});
        }catch(Exception e){
            final String msg="Error: "+e.getMessage();
            SwingUtilities.invokeLater(() -> { tbObat.setValueAt(msg,row,12); });
            System.out.println("Notifikasi Cek ClaimResponse : "+e);
        }
    }

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try{
            htmlContent = new StringBuilder();
            htmlContent.append("<tr class='isi'><td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.Rawat</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.RM</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>Nama Pasien</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>ID Claim</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>Sub Type</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>ID CR Purifikasi</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>Status Purifikasi</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>ID CR Verifikasi</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>Status Verifikasi</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>Disposition</b></td></tr>");
            for (i = 0; i < tabMode.getRowCount(); i++) {
                htmlContent.append("<tr class='isi'><td valign='top'>"+tbObat.getValueAt(i,1)+"</td><td valign='top'>"+tbObat.getValueAt(i,2)+"</td><td valign='top'>"+tbObat.getValueAt(i,3)+"</td><td valign='top'>"+tbObat.getValueAt(i,4)+"</td><td valign='top'>"+tbObat.getValueAt(i,5)+"</td><td valign='top'>"+tbObat.getValueAt(i,7)+"</td><td valign='top'>"+tbObat.getValueAt(i,8)+"</td><td valign='top'>"+tbObat.getValueAt(i,9)+"</td><td valign='top'>"+tbObat.getValueAt(i,10)+"</td><td valign='top'>"+tbObat.getValueAt(i,12)+"</td></tr>");
            }
            LoadHTML.setText("<html><table width='100%' border='0' align='center' cellpadding='1px' cellspacing='0' class='tbl_form'>"+htmlContent.toString()+"</table></html>");
            htmlContent=null;
            File g = new File("file2.css"); BufferedWriter bg = new BufferedWriter(new FileWriter(g));
            bg.write(".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}");
            bg.close();
            File f = new File("DataSatuSehatCekClaimResponse.html"); BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write(LoadHTML.getText().replaceAll("<head>","<head><link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" /><table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'><tr class='isi2'><td valign='top' align='center'><font size='4' face='Tahoma'>"+akses.getnamars()+"</font><br>"+akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs()+"<br>"+akses.getkontakrs()+", E-mail : "+akses.getemailrs()+"<br><br><font size='2' face='Tahoma'>CEK CLAIMRESPONSE BPJS-K SATU SEHAT<br><br></font></td></tr></table>"));
            bw.close(); Desktop.getDesktop().browse(f.toURI());
        }catch(Exception e){ System.out.println("Notifikasi : "+e); }
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void formWindowOpened(java.awt.event.WindowEvent evt) {
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override public void insertUpdate(DocumentEvent e) { if(TCari.getText().length()>2) runBackground(() -> tampil()); }
                @Override public void removeUpdate(DocumentEvent e) { if(TCari.getText().length()>2) runBackground(() -> tampil()); }
                @Override public void changedUpdate(DocumentEvent e) { if(TCari.getText().length()>2) runBackground(() -> tampil()); }
            });
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            SatuSehatCekClaimResponse dialog = new SatuSehatCekClaimResponse(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() { @Override public void windowClosing(java.awt.event.WindowEvent e) { System.exit(0); } });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnAll;
    private widget.Button BtnCari;
    private widget.Button BtnCek;
    private widget.Button BtnCekSemua;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.Label LCount;
    private widget.editorpane LoadHTML;
    private widget.ScrollPane Scroll;
    private widget.ScrollPane ScrollRaw;
    private widget.TextBox TCari;
    private widget.ComboBox cmbStatus;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel7;
    private javax.swing.JPanel jPanel3;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private javax.swing.JSplitPane splitPane1;
    private widget.TextArea taRaw;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        Valid.tabelKosong(tabMode);
        try{
            try {
                ps = koneksi.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS satu_sehat_claimresponse ("+
                    "no_rawat varchar(17) NOT NULL, "+
                    "id_claimresponse_purifikasi varchar(40) DEFAULT NULL, "+
                    "status_purifikasi varchar(50) DEFAULT NULL, "+
                    "disposition_purifikasi TEXT DEFAULT NULL, "+
                    "id_claimresponse_verifikasi varchar(40) DEFAULT NULL, "+
                    "status_verifikasi varchar(50) DEFAULT NULL, "+
                    "disposition_verifikasi TEXT DEFAULT NULL, "+
                    "raw_response_purifikasi TEXT DEFAULT NULL, "+
                    "raw_response_verifikasi TEXT DEFAULT NULL, "+
                    "tgl_sync datetime NOT NULL DEFAULT current_timestamp(), "+
                    "PRIMARY KEY (no_rawat), KEY idx_tgl_sync (tgl_sync)"+
                    ") ENGINE=InnoDB DEFAULT CHARSET=latin1");
                ps.execute(); ps.close();
            } catch (Exception e) {}

            String filter="";
            String stat=cmbStatus.getSelectedItem().toString();
            if(stat.equals("Belum Sync")) filter=" and sscr.no_rawat is null ";
            else if(stat.equals("Purifikasi Pending")) filter=" and (sscr.status_purifikasi is null or sscr.status_purifikasi='' or sscr.status_purifikasi='Pending') ";
            else if(stat.equals("Verifikasi Pending")) filter=" and sscr.status_purifikasi in ('Lolos','Tidak Lolos') and (sscr.status_verifikasi is null or sscr.status_verifikasi='' or sscr.status_verifikasi='Pending') ";
            else if(stat.equals("Selesai")) filter=" and sscr.status_verifikasi in ('Layak','Tidak Layak','Kedaluwarsa') ";

            ps=koneksi.prepareStatement(
                   "select re.no_rawat,re.no_rkm_medis,p.nm_pasien,"+
                   "sscl.id_claim,sscl.sub_type,date_format(sscl.tgl_kirim,'%Y-%m-%d %H:%i:%s') as tgl_claim,"+
                   "ifnull(sscr.id_claimresponse_purifikasi,'') as id_cr_purifikasi,"+
                   "ifnull(sscr.status_purifikasi,'') as status_purifikasi,"+
                   "ifnull(sscr.id_claimresponse_verifikasi,'') as id_cr_verifikasi,"+
                   "ifnull(sscr.status_verifikasi,'') as status_verifikasi,"+
                   "ifnull(date_format(sscr.tgl_sync,'%Y-%m-%d %H:%i:%s'),'') as tgl_sync,"+
                   "ifnull(coalesce(nullif(sscr.disposition_verifikasi,''),sscr.disposition_purifikasi),'') as disposition "+
                   "from satu_sehat_claim sscl "+
                   "inner join reg_periksa re on re.no_rawat=sscl.no_rawat "+
                   "inner join pasien p on p.no_rkm_medis=re.no_rkm_medis "+
                   "left join satu_sehat_claimresponse sscr on sscr.no_rawat=sscl.no_rawat "+
                   "where sscl.tgl_kirim between ? and ? "+filter+
                   (TCari.getText().equals("")?"":"and (re.no_rawat like ? or re.no_rkm_medis like ? or p.nm_pasien like ? or sscl.id_claim like ?) ")+
                   "order by sscl.tgl_kirim desc");
            try {
                ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
                ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
                if(!TCari.getText().equals("")){
                    ps.setString(3,"%"+TCari.getText()+"%"); ps.setString(4,"%"+TCari.getText()+"%");
                    ps.setString(5,"%"+TCari.getText()+"%"); ps.setString(6,"%"+TCari.getText()+"%");
                }
                rs=ps.executeQuery();
                while(rs.next()){
                    String sp=rs.getString("status_purifikasi"); if(sp.equals("")) sp="-";
                    String sv=rs.getString("status_verifikasi"); if(sv.equals("")) sv="-";
                    tabMode.addRow(new Object[]{
                        false,
                        rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),
                        rs.getString("id_claim"),rs.getString("sub_type"),rs.getString("tgl_claim"),
                        rs.getString("id_cr_purifikasi"),sp,
                        rs.getString("id_cr_verifikasi"),sv,
                        rs.getString("tgl_sync"),rs.getString("disposition")
                    });
                }
            } catch (Exception e) { System.out.println("Notif : "+e);
            } finally{ if(rs!=null) rs.close(); if(ps!=null) ps.close(); }
        }catch(Exception e){ System.out.println("Notifikasi : "+e); }
        LCount.setText(""+tabMode.getRowCount());
    }

    public void isCek(){
        BtnCek.setEnabled(akses.getsatu_sehat_kirim_encounter());
        BtnCekSemua.setEnabled(akses.getsatu_sehat_kirim_encounter());
        BtnPrint.setEnabled(akses.getsatu_sehat_kirim_encounter());
    }

    public JTable getTable(){ return tbObat; }

    private void runBackground(Runnable task) {
        if (ceksukses) return; if (executor.isShutdown() || executor.isTerminated()) return; if (!isDisplayable()) return;
        ceksukses = true; setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try { executor.submit(() -> { try { task.run(); } finally { ceksukses = false; SwingUtilities.invokeLater(() -> { if (isDisplayable()) setCursor(Cursor.getDefaultCursor()); }); } });
        } catch (RejectedExecutionException ex) { ceksukses = false; }
    }

    @Override public void dispose() { executor.shutdownNow(); super.dispose(); }
}
