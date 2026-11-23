/*
  By dr. Salim Mulyana, S.Kom, MM
  Tahap #13 Playbook Klaim BPJS-K v1.3 Kemkes:
  Setelah Khanza terima ClaimResponse Purifikasi (Lolos/Tidak Lolos via
  SatuSehatCekClaimResponse), fasyankes WAJIB POST PurificationDecision (NON-FHIR
  custom JSON Kemkes) untuk konfirmasi: Lanjut (proses verifikasi) atau Batal.

  Body (estimate format, endpoint exact path butuh konfirmasi Kemkes saat
  staging available — see TODO di method postDecision):
    { "claimResponseId": "...", "claimId": "...", "decision": "Lanjut|Batal",
      "reason": "...", "decisionDate": "{ISO UTC}", "decisionMaker": { "organization": "..." } }
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
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
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

public final class SatuSehatKirimPurificationDecision extends javax.swing.JDialog {
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

    // 0=P, 1=No.Rawat, 2=No.RM, 3=Nama Pasien, 4=ID Claim, 5=Sub Type,
    // 6=Status Purifikasi, 7=Disposition Purifikasi, 8=Decision Default,
    // 9=Decision Manual (editable), 10=Reason Notes (editable),
    // 11=ID Decision, 12=Status Kirim
    public SatuSehatKirimPurificationDecision(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(10,2);
        setSize(1200,720);

        tabMode=new DefaultTableModel(null,new String[]{
                "P","No.Rawat","No.RM","Nama Pasien","ID Claim","Sub Type",
                "Status Purifikasi","Disposition Purifikasi","Decision Default",
                "Decision Manual","Reason Notes","ID Decision","Status Kirim"
            }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){
                  return colIndex==0 || colIndex==9 || colIndex==10;
              }
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
            else if(i==3) column.setPreferredWidth(140);
            else if(i==4) column.setPreferredWidth(150);
            else if(i==5) column.setPreferredWidth(70);
            else if(i==6) column.setPreferredWidth(110);
            else if(i==7) column.setPreferredWidth(170);
            else if(i==8) column.setPreferredWidth(95);
            else if(i==9) column.setPreferredWidth(110);
            else if(i==10) column.setPreferredWidth(200);
            else if(i==11) column.setPreferredWidth(150);
            else if(i==12) column.setPreferredWidth(110);
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        // Dropdown editor for kolom 9 (Decision Manual)
        JComboBox<String> cbDecision = new JComboBox<>(new String[]{"","Lanjut","Batal"});
        tbObat.getColumnModel().getColumn(9).setCellEditor(new DefaultCellEditor(cbDecision));

        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override public void insertUpdate(DocumentEvent e) { if(TCari.getText().length()>2) tampil(); }
                @Override public void removeUpdate(DocumentEvent e) { if(TCari.getText().length()>2) tampil(); }
                @Override public void changedUpdate(DocumentEvent e) { if(TCari.getText().length()>2) tampil(); }
            });
        }
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
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        BtnAll = new widget.Button();
        BtnKirim = new widget.Button();
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

        LoadHTML.setBorder(null); LoadHTML.setName("LoadHTML");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE); setIconImage(null); setIconImages(null); setUndecorated(true); setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() { public void windowOpened(java.awt.event.WindowEvent evt) { formWindowOpened(evt); } });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240,245,235)), "::[ Kirim Purification Decision (BPJS-K) Satu Sehat ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma",0,11), new java.awt.Color(50,50,50)));
        internalFrame1.setFont(new java.awt.Font("Tahoma",0,11)); internalFrame1.setName("internalFrame1"); internalFrame1.setLayout(new java.awt.BorderLayout(1,1));

        Scroll.setName("Scroll"); Scroll.setOpaque(true);
        tbObat.setName("tbObat"); Scroll.setViewportView(tbObat);
        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); jPanel3.setOpaque(false); jPanel3.setPreferredSize(new java.awt.Dimension(44,100)); jPanel3.setLayout(new java.awt.BorderLayout(1,1));
        panelGlass8.setName("panelGlass8"); panelGlass8.setPreferredSize(new java.awt.Dimension(44,44)); panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT,5,9));
        jLabel7.setText("Record :"); jLabel7.setName("jLabel7"); jLabel7.setPreferredSize(new java.awt.Dimension(53,23)); panelGlass8.add(jLabel7);
        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT); LCount.setText("0"); LCount.setName("LCount"); LCount.setPreferredSize(new java.awt.Dimension(60,23)); panelGlass8.add(LCount);
        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); BtnAll.setMnemonic('M'); BtnAll.setText("Semua"); BtnAll.setToolTipText("Alt+M"); BtnAll.setName("BtnAll"); BtnAll.setPreferredSize(new java.awt.Dimension(100,30));
        BtnAll.addActionListener(e -> { TCari.setText(""); tampil(); }); panelGlass8.add(BtnAll);
        BtnKirim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/34.png"))); BtnKirim.setMnemonic('K'); BtnKirim.setText("Kirim"); BtnKirim.setToolTipText("Alt+K"); BtnKirim.setName("BtnKirim"); BtnKirim.setPreferredSize(new java.awt.Dimension(100,30));
        BtnKirim.addActionListener(e -> BtnKirimActionPerformed(e)); panelGlass8.add(BtnKirim);
        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); BtnPrint.setMnemonic('T'); BtnPrint.setText("Cetak"); BtnPrint.setToolTipText("Alt+T"); BtnPrint.setName("BtnPrint"); BtnPrint.setPreferredSize(new java.awt.Dimension(100,30));
        BtnPrint.addActionListener(e -> BtnPrintActionPerformed(e)); panelGlass8.add(BtnPrint);
        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); BtnKeluar.setMnemonic('L'); BtnKeluar.setText("Keluar"); BtnKeluar.setToolTipText("Alt+L"); BtnKeluar.setName("BtnKeluar"); BtnKeluar.setPreferredSize(new java.awt.Dimension(100,30));
        BtnKeluar.addActionListener(e -> dispose()); panelGlass8.add(BtnKeluar);
        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); panelGlass9.setPreferredSize(new java.awt.Dimension(44,44)); panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT,5,9));
        jLabel15.setText("Tgl.Kirim Claim :"); jLabel15.setName("jLabel15"); jLabel15.setPreferredSize(new java.awt.Dimension(95,23)); panelGlass9.add(jLabel15);
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"01-01-2026"})); DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setName("DTPCari1"); DTPCari1.setOpaque(false); DTPCari1.setPreferredSize(new java.awt.Dimension(95,23)); panelGlass9.add(DTPCari1);
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER); jLabel17.setText("s.d."); jLabel17.setName("jLabel17"); jLabel17.setPreferredSize(new java.awt.Dimension(24,23)); panelGlass9.add(jLabel17);
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"31-12-2026"})); DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setName("DTPCari2"); DTPCari2.setOpaque(false); DTPCari2.setPreferredSize(new java.awt.Dimension(95,23)); panelGlass9.add(DTPCari2);
        jLabel16.setText("Key Word :"); jLabel16.setName("jLabel16"); jLabel16.setPreferredSize(new java.awt.Dimension(70,23)); panelGlass9.add(jLabel16);
        TCari.setName("TCari"); TCari.setPreferredSize(new java.awt.Dimension(210,23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() { public void keyPressed(java.awt.event.KeyEvent evt) { if(evt.getKeyCode()==KeyEvent.VK_ENTER) tampil(); } });
        panelGlass9.add(TCari);
        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); BtnCari.setMnemonic('6'); BtnCari.setToolTipText("Alt+6"); BtnCari.setName("BtnCari"); BtnCari.setPreferredSize(new java.awt.Dimension(28,23));
        BtnCari.addActionListener(e -> tampil()); panelGlass9.add(BtnCari);
        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);
        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
        pack();
    }// </editor-fold>//GEN-END:initComponents

    /** Escape karakter untuk JSON manual. */
    private static String jsonEscape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder(s.length() + 8);
        for (int idx = 0; idx < s.length(); idx++) {
            char c = s.charAt(idx);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) sb.append(String.format("\\u%04x", (int) c));
                    else sb.append(c);
            }
        }
        return sb.toString();
    }

    private void BtnKirimActionPerformed(java.awt.event.ActionEvent evt) {
        for(i=0;i<tbObat.getRowCount();i++){
            if(!tbObat.getValueAt(i,0).toString().equals("true")) continue;
            if(!tbObat.getValueAt(i,11).toString().equals("")) continue; // sudah pernah dikirim
            try {
                kirimSatuRow(i);
            } catch (Exception e) {
                System.out.println("Notifikasi : "+e);
            }
        }
    }

    /** POST PurificationDecision NON-FHIR JSON ke endpoint Kemkes.
     *  TODO: endpoint exact URL belum di-publish Playbook — placeholder "/PurificationDecision";
     *  butuh konfirmasi dari pihak Kemkes saat staging available. */
    private String postDecision(String body) {
        try{
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
            String url = link+"/PurificationDecision";
            System.out.println("URL : "+url);
            System.out.println("Request JSON : "+body);
            requestEntity = new HttpEntity(body,headers);
            String resp = api.getRest().exchange(url, HttpMethod.POST, requestEntity, String.class).getBody();
            System.out.println("Result JSON : "+resp);
            return resp==null?"":resp;
        }catch(org.springframework.web.client.HttpClientErrorException e){
            System.out.println("Notifikasi Bridging : "+e);
            System.out.println("Response Body : "+e.getResponseBodyAsString());
            return "{\"error\":\""+jsonEscape(e.getMessage())+"\"}";
        }catch(Exception e){
            System.out.println("Notifikasi Bridging : "+e);
            return "{\"error\":\""+jsonEscape(e.getMessage())+"\"}";
        }
    }

    private void kirimSatuRow(int row) {
        String noRawat   = tbObat.getValueAt(row,1).toString();
        String idClaim   = tbObat.getValueAt(row,4).toString();
        Object decMObj   = tbObat.getValueAt(row,9);
        String decManual = decMObj==null?"":decMObj.toString();
        String decDefault= tbObat.getValueAt(row,8).toString();
        Object reasonObj = tbObat.getValueAt(row,10);
        String reason    = reasonObj==null?"":reasonObj.toString();

        String decision = (!decManual.equals("")) ? decManual : decDefault;
        if(!decision.equals("Lanjut") && !decision.equals("Batal")){
            tbObat.setValueAt("Skip: decision kosong/invalid",row,12);
            return;
        }

        // ambil id_claimresponse_purifikasi dari DB
        String idCrPur="";
        try{
            ps=koneksi.prepareStatement("select ifnull(id_claimresponse_purifikasi,'') as id from satu_sehat_claimresponse where no_rawat=?");
            ps.setString(1,noRawat);
            rs=ps.executeQuery();
            if(rs.next()) idCrPur=rs.getString("id");
            rs.close(); ps.close();
        }catch(Exception e){ System.out.println("Notif lookup CR : "+e); }

        if(idCrPur.equals("")){
            tbObat.setValueAt("Skip: id_claimresponse_purifikasi kosong",row,12);
            return;
        }

        String nowIso = fungsi.IsoTime.toUtc(
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"claimResponseId\":\"").append(jsonEscape(idCrPur)).append("\",");
        sb.append("\"claimId\":\"").append(jsonEscape(idClaim)).append("\",");
        sb.append("\"decision\":\"").append(decision).append("\",");
        if(!reason.equals("")){
            sb.append("\"reason\":\"").append(jsonEscape(reason)).append("\",");
        }
        if(!nowIso.equals("")){
            sb.append("\"decisionDate\":\"").append(nowIso).append("\",");
        }
        sb.append("\"decisionMaker\":{");
        sb.append("\"organization\":\"").append(koneksiDB.IDSATUSEHAT()).append("\"");
        sb.append("}");
        sb.append("}");

        String body = sb.toString();
        String resp = postDecision(body);

        // parse response → cari id (kalau Kemkes return id), kalau tidak fallback gen marker dari claimId
        String idDecision="";
        boolean ok=false;
        try{
            root = mapper.readTree(resp);
            JsonNode rid = root.path("id");
            if(rid!=null && !rid.asText().equals("")){
                idDecision = rid.asText();
                ok = true;
            } else if(root.path("error").asText().equals("")){
                idDecision = "DEC-"+idClaim;
                ok = true;
            }
        }catch(Exception e){
            System.out.println("Notif parse resp : "+e);
        }

        final String fId=idDecision, fResp=resp;
        final String stKirim = ok ? "Sukses" : "Gagal";
        tbObat.setValueAt(fId, row, 11);
        tbObat.setValueAt(stKirim, row, 12);
        tbObat.setValueAt(false, row, 0);

        if(ok){
            try {
                Sequel.queryu2tf(
                    "insert into satu_sehat_purification_decision"+
                    "(no_rawat,id_decision,decision,reason,raw_response,tgl_kirim) "+
                    "values(?,?,?,?,?,now()) "+
                    "on duplicate key update id_decision=values(id_decision),"+
                    "decision=values(decision),reason=values(reason),"+
                    "raw_response=values(raw_response),tgl_kirim=values(tgl_kirim)",
                    5,new String[]{noRawat,idDecision,decision,reason,fResp});
            } catch (Exception eSimpan) {
                System.out.println("Notif simpan tracking : "+eSimpan);
            }
        }
    }

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            TCari.requestFocus();
        }else{
            try{
                htmlContent = new StringBuilder();
                htmlContent.append("<tr class='isi'><td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.Rawat</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.RM</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>Nama Pasien</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>ID Claim</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>Sub Type</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>Status Purifikasi</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>Decision</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>Reason</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>ID Decision</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>Status Kirim</b></td></tr>");
                for (i = 0; i < tabMode.getRowCount(); i++) {
                    String decM = tbObat.getValueAt(i,9)==null?"":tbObat.getValueAt(i,9).toString();
                    String decFinal = !decM.equals("") ? decM : tbObat.getValueAt(i,8).toString();
                    String reason = tbObat.getValueAt(i,10)==null?"":tbObat.getValueAt(i,10).toString();
                    htmlContent.append("<tr class='isi'><td valign='top'>"+tbObat.getValueAt(i,1)+"</td><td valign='top'>"+tbObat.getValueAt(i,2)+"</td><td valign='top'>"+tbObat.getValueAt(i,3)+"</td><td valign='top'>"+tbObat.getValueAt(i,4)+"</td><td valign='top'>"+tbObat.getValueAt(i,5)+"</td><td valign='top'>"+tbObat.getValueAt(i,6)+"</td><td valign='top'>"+decFinal+"</td><td valign='top'>"+reason+"</td><td valign='top'>"+tbObat.getValueAt(i,11)+"</td><td valign='top'>"+tbObat.getValueAt(i,12)+"</td></tr>");
                }
                LoadHTML.setText("<html><table width='100%' border='0' align='center' cellpadding='1px' cellspacing='0' class='tbl_form'>"+htmlContent.toString()+"</table></html>");
                File g = new File("file2.css"); BufferedWriter bg = new BufferedWriter(new FileWriter(g));
                bg.write(".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}");
                bg.close();
                File f = new File("DataSatuSehatPurificationDecision.html"); BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                bw.write(LoadHTML.getText().replaceAll("<head>","<head><link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" /><table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'><tr class='isi2'><td valign='top' align='center'><font size='4' face='Tahoma'>"+akses.getnamars()+"</font><br>"+akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs()+"<br>"+akses.getkontakrs()+", E-mail : "+akses.getemailrs()+"<br><br><font size='2' face='Tahoma'>KIRIM PURIFICATION DECISION BPJS-K SATU SEHAT<br><br></font></td></tr></table>"));
                bw.close(); Desktop.getDesktop().browse(f.toURI());
            }catch(Exception e){ System.out.println("Notifikasi : "+e); }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void formWindowOpened(java.awt.event.WindowEvent evt) {
        // no-op (cari cepat handled di constructor)
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            SatuSehatKirimPurificationDecision dialog = new SatuSehatKirimPurificationDecision(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() { @Override public void windowClosing(java.awt.event.WindowEvent e) { System.exit(0); } });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private widget.Button BtnAll;
    private widget.Button BtnCari;
    private widget.Button BtnKeluar;
    private widget.Button BtnKirim;
    private widget.Button BtnPrint;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.Label LCount;
    private widget.editorpane LoadHTML;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel7;
    private javax.swing.JPanel jPanel3;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try{
            try {
                ps = koneksi.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS satu_sehat_purification_decision ("+
                    "no_rawat varchar(17) NOT NULL, "+
                    "id_decision varchar(40) DEFAULT NULL, "+
                    "decision varchar(10) NOT NULL, "+
                    "reason TEXT DEFAULT NULL, "+
                    "raw_response TEXT DEFAULT NULL, "+
                    "tgl_kirim datetime NOT NULL DEFAULT current_timestamp(), "+
                    "PRIMARY KEY (no_rawat), KEY idx_tgl_kirim (tgl_kirim)"+
                    ") ENGINE=InnoDB DEFAULT CHARSET=latin1");
                ps.execute(); ps.close();
            } catch (Exception e) {}

            ps=koneksi.prepareStatement(
                   "select re.no_rawat,re.no_rkm_medis,p.nm_pasien,"+
                   "sscl.id_claim,sscl.sub_type,"+
                   "ifnull(sscr.id_claimresponse_purifikasi,'') as id_cr_purifikasi,"+
                   "sscr.status_purifikasi,"+
                   "ifnull(sscr.disposition_purifikasi,'') as disposition_purifikasi,"+
                   "ifnull(sspd.id_decision,'') as id_decision,"+
                   "ifnull(sspd.decision,'') as decision_sent,"+
                   "ifnull(sspd.reason,'') as reason_sent "+
                   "from satu_sehat_claim sscl "+
                   "inner join reg_periksa re on re.no_rawat=sscl.no_rawat "+
                   "inner join pasien p on p.no_rkm_medis=re.no_rkm_medis "+
                   "inner join satu_sehat_claimresponse sscr on sscr.no_rawat=sscl.no_rawat "+
                   "and sscr.status_purifikasi in ('Lolos','Tidak Lolos') "+
                   "left join satu_sehat_purification_decision sspd on sspd.no_rawat=sscl.no_rawat "+
                   "where sscl.tgl_kirim between ? and ? "+
                   (TCari.getText().equals("")?"":"and (re.no_rawat like ? or re.no_rkm_medis like ? or p.nm_pasien like ? or sscl.id_claim like ?) ")+
                   "order by sscr.tgl_sync desc");
            try {
                ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
                ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
                if(!TCari.getText().equals("")){
                    ps.setString(3,"%"+TCari.getText()+"%"); ps.setString(4,"%"+TCari.getText()+"%");
                    ps.setString(5,"%"+TCari.getText()+"%"); ps.setString(6,"%"+TCari.getText()+"%");
                }
                rs=ps.executeQuery();
                while(rs.next()){
                    String sp = rs.getString("status_purifikasi");
                    // default suggestion: Lolos → Lanjut, Tidak Lolos → Batal
                    String decDefault = "Lolos".equals(sp) ? "Lanjut" : ("Tidak Lolos".equals(sp) ? "Batal" : "");
                    String decSent = rs.getString("decision_sent");
                    String idDec = rs.getString("id_decision");
                    String statusKirim = idDec.equals("") ? "" : "Sukses";
                    tabMode.addRow(new Object[]{
                        false,
                        rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),
                        rs.getString("id_claim"),rs.getString("sub_type"),
                        sp,rs.getString("disposition_purifikasi"),
                        decDefault, decSent, rs.getString("reason_sent"),
                        idDec, statusKirim
                    });
                }
            } catch (Exception e) { System.out.println("Notif : "+e);
            } finally{ if(rs!=null) rs.close(); if(ps!=null) ps.close(); }
        }catch(Exception e){ System.out.println("Notifikasi : "+e); }
        LCount.setText(""+tabMode.getRowCount());
    }

    public void isCek(){
        BtnKirim.setEnabled(akses.getsatu_sehat_kirim_encounter());
        BtnPrint.setEnabled(akses.getsatu_sehat_kirim_encounter());
    }

    public JTable getTable(){ return tbObat; }
}
