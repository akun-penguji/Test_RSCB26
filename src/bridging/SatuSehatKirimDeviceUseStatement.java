/*
  By Mas Elkhanza
 */
package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.JOptionPane;
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

public final class SatuSehatKirimDeviceUseStatement extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps,ps2;
    private ResultSet rs,rs2;
    private int i=0;
    private String link="",json="",idpasien="",iddokter="";
    private ApiSatuSehat api=new ApiSatuSehat();
    private HttpHeaders headers;
    private HttpEntity requestEntity;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode response;
    private SatuSehatCekNIK cekViaSatuSehat=new SatuSehatCekNIK();
    private StringBuilder htmlContent;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean ceksukses = false;

    // 0=P, 1=No.Rawat, 2=No.RM, 3=Nama Pasien, 4=No.KTP, 5=Nama Dokter, 6=No.KTP Dokter,
    // 7=ID Encounter, 8=Tgl Registrasi, 9=Masalah Keperawatan, 10=Rencana Keperawatan, 11=ID Goal
    public SatuSehatKirimDeviceUseStatement(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setLocation(10,2);
        setSize(628,674);

        // 0=P,1=No.Rawat,2=No.RM,3=Nama Pasien,4=NIK,5=Nama Dokter,6=NIK Dokter,
        // 7=ID Encounter,8=Tgl,9=Kode Brng,10=Nama Alkes,11=ID Device,12=ID DeviceUseStatement
        tabMode=new DefaultTableModel(null,new String[]{
                "P","No.Rawat","No.RM","Nama Pasien","NIK Pasien","Nama Dokter","NIK Dokter",
                "ID Encounter","Tgl","Kode Brng","Nama Alkes","ID Device","ID DeviceUseStatement"
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
        int[] lebar = {20,105,70,150,110,140,110,210,90,90,220,210,210};
        for (i = 0; i < lebar.length; i++) {
            tbObat.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());
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
        BtnCari = new widget.Button();
        jLabel18 = new widget.Label();
        cmbStatus = new widget.ComboBox();

        jPopupMenu1.setName("jPopupMenu1");
        ppPilihSemua.setBackground(new java.awt.Color(255,255,254)); ppPilihSemua.setFont(new java.awt.Font("Tahoma",0,11)); ppPilihSemua.setForeground(new java.awt.Color(50,50,50));
        ppPilihSemua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); ppPilihSemua.setText("Pilih Semua");
        ppPilihSemua.setHorizontalAlignment(javax.swing.SwingConstants.LEFT); ppPilihSemua.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppPilihSemua.setName("ppPilihSemua"); ppPilihSemua.setPreferredSize(new java.awt.Dimension(150,26));
        ppPilihSemua.addActionListener(e -> { for(i=0;i<tbObat.getRowCount();i++) tbObat.setValueAt(true,i,0); });
        jPopupMenu1.add(ppPilihSemua);
        ppBersihkan.setBackground(new java.awt.Color(255,255,254)); ppBersihkan.setFont(new java.awt.Font("Tahoma",0,11)); ppBersihkan.setForeground(new java.awt.Color(50,50,50));
        ppBersihkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); ppBersihkan.setText("Hilangkan Pilihan");
        ppBersihkan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT); ppBersihkan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppBersihkan.setName("ppBersihkan"); ppBersihkan.setPreferredSize(new java.awt.Dimension(150,26));
        ppBersihkan.addActionListener(e -> { for(i=0;i<tbObat.getRowCount();i++) tbObat.setValueAt(false,i,0); });
        jPopupMenu1.add(ppBersihkan);
        LoadHTML.setBorder(null); LoadHTML.setName("LoadHTML");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE); setIconImage(null); setIconImages(null); setUndecorated(true); setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() { public void windowOpened(java.awt.event.WindowEvent evt) { formWindowOpened(evt); } });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240,245,235)), "::[ Pengiriman Data DeviceUseStatement (Penggunaan Alkes) Satu Sehat ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma",0,11), new java.awt.Color(50,50,50)));
        internalFrame1.setFont(new java.awt.Font("Tahoma",0,11)); internalFrame1.setName("internalFrame1"); internalFrame1.setLayout(new java.awt.BorderLayout(1,1));
        Scroll.setComponentPopupMenu(jPopupMenu1); Scroll.setName("Scroll"); Scroll.setOpaque(true);
        tbObat.setComponentPopupMenu(jPopupMenu1); tbObat.setName("tbObat"); Scroll.setViewportView(tbObat);
        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);
        jPanel3.setName("jPanel3"); jPanel3.setOpaque(false); jPanel3.setPreferredSize(new java.awt.Dimension(44,100)); jPanel3.setLayout(new java.awt.BorderLayout(1,1));
        panelGlass8.setName("panelGlass8"); panelGlass8.setPreferredSize(new java.awt.Dimension(44,44)); panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT,5,9));
        jLabel7.setText("Record :"); jLabel7.setName("jLabel7"); jLabel7.setPreferredSize(new java.awt.Dimension(53,23)); panelGlass8.add(jLabel7);
        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT); LCount.setText("0"); LCount.setName("LCount"); LCount.setPreferredSize(new java.awt.Dimension(60,23)); panelGlass8.add(LCount);
        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); BtnAll.setMnemonic('M'); BtnAll.setText("Semua"); BtnAll.setToolTipText("Alt+M"); BtnAll.setName("BtnAll"); BtnAll.setPreferredSize(new java.awt.Dimension(100,30));
        BtnAll.addActionListener(e -> { TCari.setText(""); runBackground(() -> tampil()); }); panelGlass8.add(BtnAll);
        BtnKirim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/34.png"))); BtnKirim.setMnemonic('K'); BtnKirim.setText("Kirim"); BtnKirim.setToolTipText("Alt+K"); BtnKirim.setName("BtnKirim"); BtnKirim.setPreferredSize(new java.awt.Dimension(100,30));
        BtnKirim.addActionListener(e -> BtnKirimActionPerformed(e)); panelGlass8.add(BtnKirim);
        BtnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/edit_f2.png"))); BtnUpdate.setMnemonic('U'); BtnUpdate.setText("Update"); BtnUpdate.setToolTipText("Alt+U"); BtnUpdate.setName("BtnUpdate"); BtnUpdate.setPreferredSize(new java.awt.Dimension(100,30));
        BtnUpdate.addActionListener(e -> BtnUpdateActionPerformed(e)); panelGlass8.add(BtnUpdate);
        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); BtnPrint.setMnemonic('T'); BtnPrint.setText("Cetak"); BtnPrint.setToolTipText("Alt+T"); BtnPrint.setName("BtnPrint"); BtnPrint.setPreferredSize(new java.awt.Dimension(100,30));
        BtnPrint.addActionListener(e -> BtnPrintActionPerformed(e)); panelGlass8.add(BtnPrint);
        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png"))); BtnKeluar.setMnemonic('K'); BtnKeluar.setText("Keluar"); BtnKeluar.setToolTipText("Alt+K"); BtnKeluar.setName("BtnKeluar"); BtnKeluar.setPreferredSize(new java.awt.Dimension(100,30));
        BtnKeluar.addActionListener(e -> dispose()); panelGlass8.add(BtnKeluar);
        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);
        panelGlass9.setName("panelGlass9"); panelGlass9.setPreferredSize(new java.awt.Dimension(44,44)); panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT,5,9));
        jLabel15.setText("Tgl.Registrasi :"); jLabel15.setName("jLabel15"); jLabel15.setPreferredSize(new java.awt.Dimension(85,23)); panelGlass9.add(jLabel15);
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"08-04-2026"})); DTPCari1.setDisplayFormat("dd-MM-yyyy"); DTPCari1.setName("DTPCari1"); DTPCari1.setOpaque(false); DTPCari1.setPreferredSize(new java.awt.Dimension(95,23)); panelGlass9.add(DTPCari1);
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER); jLabel17.setText("s.d."); jLabel17.setName("jLabel17"); jLabel17.setPreferredSize(new java.awt.Dimension(24,23)); panelGlass9.add(jLabel17);
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"08-04-2026"})); DTPCari2.setDisplayFormat("dd-MM-yyyy"); DTPCari2.setName("DTPCari2"); DTPCari2.setOpaque(false); DTPCari2.setPreferredSize(new java.awt.Dimension(95,23)); panelGlass9.add(DTPCari2);
        jLabel16.setText("Key Word :"); jLabel16.setName("jLabel16"); jLabel16.setPreferredSize(new java.awt.Dimension(70,23)); panelGlass9.add(jLabel16);
        TCari.setName("TCari"); TCari.setPreferredSize(new java.awt.Dimension(210,23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() { public void keyPressed(java.awt.event.KeyEvent evt) { if(evt.getKeyCode()==KeyEvent.VK_ENTER) runBackground(() -> tampil()); } });
        panelGlass9.add(TCari);
        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); BtnCari.setMnemonic('6'); BtnCari.setToolTipText("Alt+6"); BtnCari.setName("BtnCari"); BtnCari.setPreferredSize(new java.awt.Dimension(28,23));
        BtnCari.addActionListener(e -> runBackground(() -> tampil())); panelGlass9.add(BtnCari);
        jLabel18.setText("Status Kirim:"); jLabel18.setName("jLabel18"); jLabel18.setPreferredSize(new java.awt.Dimension(70,23)); panelGlass9.add(jLabel18);
        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Semua","Belum dikirim","Sudah dikirim"})); cmbStatus.setName("cmbStatus"); panelGlass9.add(cmbStatus);
        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);
        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);
        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private String esc(String s){ return s==null?"":s.replaceAll("\"","'"); }

    private String buildDeviceUseStatementJson(int row, String idP, String idD){
        String noRawat=tbObat.getValueAt(row,1).toString(), nmPasien=esc(tbObat.getValueAt(row,3).toString());
        String nmDokter=esc(tbObat.getValueAt(row,5).toString()), idEnc=tbObat.getValueAt(row,7).toString();
        String tgl=tbObat.getValueAt(row,8).toString(), kodeBrng=tbObat.getValueAt(row,9).toString();
        String namaAlkes=esc(tbObat.getValueAt(row,10).toString()), idDevice=tbObat.getValueAt(row,11).toString();
        String dtIso=fungsi.IsoTime.toUtc(tgl);
        StringBuilder sb=new StringBuilder();
        sb.append("{\"resourceType\":\"DeviceUseStatement\",");
        sb.append("\"identifier\":[{\"system\":\"http://sys-ids.kemkes.go.id/deviceusestatement/").append(koneksiDB.IDSATUSEHAT()).append("\",\"use\":\"official\",\"value\":\"DUS-").append(noRawat).append("-").append(kodeBrng).append("\"}],");
        sb.append("\"status\":\"active\",");
        sb.append("\"subject\":{\"reference\":\"Patient/").append(idP).append("\",\"display\":\"").append(nmPasien).append("\"},");
        sb.append("\"device\":{\"reference\":\"Device/").append(idDevice).append("\",\"display\":\"").append(namaAlkes).append("\"},");
        sb.append("\"timingDateTime\":\"").append(dtIso).append("\",");
        sb.append("\"recordedOn\":\"").append(dtIso).append("\"");
        sb.append("}");
        return sb.toString();
    }

    private void BtnKirimActionPerformed(java.awt.event.ActionEvent evt) {
        for(i=0;i<tbObat.getRowCount();i++){
            if(tbObat.getValueAt(i,0).toString().equals("true")
                    && !tbObat.getValueAt(i,4).toString().equals("")    // NIK pasien
                    && !tbObat.getValueAt(i,11).toString().equals("")   // ID Device
                    && tbObat.getValueAt(i,12).toString().equals("")){  // belum dikirim
                try {
                    idpasien=cekViaSatuSehat.tampilIDPasien(tbObat.getValueAt(i,4).toString());
                    if(idpasien==null||idpasien.equals("")) continue;
                    try{
                        headers = new HttpHeaders(); headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
                        json = buildDeviceUseStatementJson(i, idpasien, iddokter);
                        System.out.println("URL : "+link+"/DeviceUseStatement");
                        System.out.println("Request JSON : "+json);
                        requestEntity = new HttpEntity(json,headers);
                        json=api.getRest().exchange(link+"/DeviceUseStatement", HttpMethod.POST, requestEntity, String.class).getBody();
                        System.out.println("Result JSON : "+json);
                        root = mapper.readTree(json); response = root.path("id");
                        if(!response.asText().equals("")){
                            try {
                                PreparedStatement p=koneksi.prepareStatement("replace into satu_sehat_deviceusestatement (no_rawat,kode_brng,id_deviceusestatement,tgl_kirim) values (?,?,?,now())");
                                p.setString(1, tbObat.getValueAt(i,1).toString());
                                p.setString(2, tbObat.getValueAt(i,9).toString());
                                p.setString(3, response.asText());
                                p.executeUpdate(); p.close();
                            } catch(Exception e){ System.out.println("Notif simpan : "+e); }
                            tbObat.setValueAt(response.asText(),i,12);
                            tbObat.setValueAt(false,i,0);
                        }
                    }catch(org.springframework.web.client.HttpClientErrorException e){ System.out.println("Notifikasi Bridging : "+e+" => "+e.getResponseBodyAsString()); }
                    catch(Exception e){ System.out.println("Notifikasi Bridging : "+e); }
                } catch (Exception e) { System.out.println("Notifikasi : "+e); }
            }
        }
    }

    private void BtnUpdateActionPerformed(java.awt.event.ActionEvent evt) {
        JOptionPane.showMessageDialog(this,"Update tidak didukung. Gunakan Kirim.");
    }

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try{
            htmlContent = new StringBuilder();
            htmlContent.append("<tr class='isi'><td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.Rawat</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>Nama Pasien</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>Nama Alkes</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>ID Device</b></td><td valign='middle' bgcolor='#FFFAFA' align='center'><b>ID DeviceUseStatement</b></td></tr>");
            for (i = 0; i < tabMode.getRowCount(); i++) {
                htmlContent.append("<tr class='isi'><td valign='top'>"+tbObat.getValueAt(i,1)+"</td><td valign='top'>"+tbObat.getValueAt(i,3)+"</td><td valign='top'>"+tbObat.getValueAt(i,10)+"</td><td valign='top'>"+tbObat.getValueAt(i,11)+"</td><td valign='top'>"+tbObat.getValueAt(i,12)+"</td></tr>");
            }
            LoadHTML.setText("<html><table width='100%' border='0' align='center' cellpadding='1px' cellspacing='0' class='tbl_form'>"+htmlContent.toString()+"</table></html>");
            htmlContent=null;
            File g = new File("file2.css"); BufferedWriter bg = new BufferedWriter(new FileWriter(g));
            bg.write(".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}.isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}");
            bg.close();
            File f = new File("DataSatuSehatDeviceUseStatement.html"); BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write(LoadHTML.getText().replaceAll("<head>","<head><link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" /><table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'><tr class='isi2'><td valign='top' align='center'><font size='4' face='Tahoma'>"+akses.getnamars()+"</font><br>"+akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs()+"<br>"+akses.getkontakrs()+", E-mail : "+akses.getemailrs()+"<br><br><font size='2' face='Tahoma'>DATA PENGIRIMAN SATU SEHAT DEVICEUSESTATEMENT<br><br></font></td></tr></table>"));
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
            SatuSehatKirimDeviceUseStatement dialog = new SatuSehatKirimDeviceUseStatement(new javax.swing.JFrame(), true);
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
    private widget.Button BtnUpdate;
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
    // End of variables declaration//GEN-END:variables

    private void tampil() {
        Valid.tabelKosong(tabMode);
        try{
            // Sumber: alkes (databarang jenis alkes) dipakai pasien (detail_pemberian_obat),
            // Device harus sudah terkirim (satu_sehat_device.id_device). encounter dari kunjungan.
            ps=koneksi.prepareStatement(
                   "select dpo.no_rawat, rp.no_rkm_medis, p.nm_pasien, p.no_ktp, "+
                   "ifnull(pg.nama,'') as nm_dokter, ifnull(pg.no_ktp,'') as ktpdokter, "+
                   "se.id_encounter, concat(dpo.tgl_perawatan,' ',dpo.jam) as tgl_perawatan, dpo.kode_brng, ifnull(db.nama_brng,'') as nama_alkes, "+
                   "ifnull(ssd.id_device,'') as id_device, ifnull(ssdr.id_deviceusestatement,'') as id_deviceusestatement "+
                   "from detail_pemberian_obat dpo "+
                   "inner join databarang db on db.kode_brng=dpo.kode_brng "+
                   "inner join jenis j on j.kdjns=db.kdjns and ("+SatuSehatAlkes.filter("j")+") "+
                   "inner join reg_periksa rp on rp.no_rawat=dpo.no_rawat "+
                   "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "+
                   "inner join satu_sehat_encounter se on se.no_rawat=dpo.no_rawat "+
                   "inner join satu_sehat_device ssd on ssd.kode_brng=dpo.kode_brng and ssd.id_device<>'' "+
                   "left join pegawai pg on pg.nik=rp.kd_dokter "+
                   "left join satu_sehat_deviceusestatement ssdr on ssdr.no_rawat=dpo.no_rawat and ssdr.kode_brng=dpo.kode_brng "+
                   "where dpo.tgl_perawatan between ? and ? "+
                   (cmbStatus.getSelectedItem().toString().equals("Belum dikirim")?"and (ssdr.id_deviceusestatement is null or ssdr.id_deviceusestatement='') ":"")+
                   (cmbStatus.getSelectedItem().toString().equals("Sudah dikirim")?"and ssdr.id_deviceusestatement is not null and ssdr.id_deviceusestatement!='' ":"")+
                   (TCari.getText().equals("")?"":"and (dpo.no_rawat like ? or p.nm_pasien like ? or p.no_ktp like ? or db.nama_brng like ?) ")+
                   "order by dpo.tgl_perawatan, dpo.no_rawat ");
            try {
                ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+""));
                ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+""));
                if(!TCari.getText().equals("")){
                    ps.setString(3,"%"+TCari.getText()+"%"); ps.setString(4,"%"+TCari.getText()+"%");
                    ps.setString(5,"%"+TCari.getText()+"%"); ps.setString(6,"%"+TCari.getText()+"%");
                }
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        false,rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),rs.getString("no_ktp"),
                        rs.getString("nm_dokter"),rs.getString("ktpdokter"),rs.getString("id_encounter"),
                        rs.getString("tgl_perawatan"),rs.getString("kode_brng"),rs.getString("nama_alkes"),
                        rs.getString("id_device"),rs.getString("id_deviceusestatement")
                    });
                }
            } catch (Exception e) { System.out.println("Notif : "+e);
            } finally{ if(rs!=null) rs.close(); if(ps!=null) ps.close(); }
        }catch(Exception e){ System.out.println("Notifikasi : "+e); }
        LCount.setText(""+tabMode.getRowCount());
    }

    public void isCek(){
        BtnKirim.setEnabled(akses.getsatu_sehat_kirim_encounter());
        BtnUpdate.setEnabled(akses.getsatu_sehat_kirim_encounter());
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
