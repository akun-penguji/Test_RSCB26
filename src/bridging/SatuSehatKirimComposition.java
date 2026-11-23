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
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

/**
 *
 * @author dosen
 */
public final class SatuSehatKirimComposition extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;   
    private int i=0;
    private String link="",json="",idpasien="",iddokter="",idencounter="";
    private ApiSatuSehat api=new ApiSatuSehat();
    private HttpHeaders headers ;
    private HttpEntity requestEntity;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode response;
    private SatuSehatCekNIK cekViaSatuSehat=new SatuSehatCekNIK();   
    private StringBuilder htmlContent;   
    
    /** Creates new form DlgKamar
     * @param parent
     * @param modal */
    public SatuSehatKirimComposition(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        System.out.println("=== DEBUG: SatuSehatKirimComposition constructor started ===");
        initComponents();

        this.setLocation(10,2);
        setSize(628,674);

        tabMode=new DefaultTableModel(null,new String[]{
                "P","Tgl Resume","No.Rawat","No.RM","Nama Pasien","No.KTP Pasien","Jenis Resume","Dokter",
                "No.KTP Dokter","ID Encounter","ID Patient","ID Practitioner","Keluhan Utama","Diagnosa Utama",
                "Tindakan","Status","ID Composition"
            }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if (colIndex==0) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                 java.lang.Boolean.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,
                 java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,
                 java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,
                 java.lang.String.class,java.lang.String.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbObat.setModel(tabMode);

        //tbKamar.setDefaultRenderer(Object.class, new WarnaTable(panelJudul.getBackground(),tbKamar.getBackground()));
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 17; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(80);
            }else if(i==2){
                column.setPreferredWidth(120);
            }else if(i==3){
                column.setPreferredWidth(80);
            }else if(i==4){
                column.setPreferredWidth(200);
            }else if(i==5){
                column.setPreferredWidth(120);
            }else if(i==6){
                column.setPreferredWidth(100);
            }else if(i==7){
                column.setPreferredWidth(150);
            }else if(i==8){
                column.setPreferredWidth(120);
            }else if(i==9){
                column.setPreferredWidth(200);
            }else if(i==10){
                column.setPreferredWidth(200);
            }else if(i==11){
                column.setPreferredWidth(200);
            }else if(i==12){
                column.setPreferredWidth(250);
            }else if(i==13){
                column.setPreferredWidth(200);
            }else if(i==14){
                column.setPreferredWidth(200);
            }else if(i==15){
                column.setPreferredWidth(80);
            }else if(i==16){
                column.setPreferredWidth(200);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        HTMLEditorKit kit = new HTMLEditorKit();
        LoadHTML.setEditable(false);
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
        
        link=koneksiDB.URLFHIRSATUSEHAT();
        System.out.println("DEBUG: FHIR URL: " + link);
        try{
            link=link.replaceAll("Organization", "Patient");
            System.out.println("DEBUG: URL after replace: " + link);
        }catch(Exception e){
            System.out.println("ERROR in constructor: "+e);
        }
        
        System.out.println("=== DEBUG: SatuSehatKirimComposition constructor completed ===");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
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
        BtnKirim = new widget.Button();
        BtnUpdate = new widget.Button();
        BtnPrint = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel18 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel7 = new widget.Label();
        CmbStatus = new widget.ComboBox();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        ppPilihSemua.setBackground(new java.awt.Color(255, 255, 254));
        ppPilihSemua.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppPilihSemua.setForeground(new java.awt.Color(50, 50, 50));
        ppPilihSemua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppPilihSemua.setText("Pilih Semua");
        ppPilihSemua.setName("ppPilihSemua"); // NOI18N
        ppPilihSemua.setPreferredSize(new java.awt.Dimension(200, 25));
        ppPilihSemua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppPilihSemuaActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppPilihSemua);

        ppBersihkan.setBackground(new java.awt.Color(255, 255, 254));
        ppBersihkan.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppBersihkan.setForeground(new java.awt.Color(50, 50, 50));
        ppBersihkan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppBersihkan.setText("Pilih Semua");
        ppBersihkan.setName("ppBersihkan"); // NOI18N
        ppBersihkan.setPreferredSize(new java.awt.Dimension(200, 25));
        ppBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppBersihkanActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppBersihkan);

        LoadHTML.setBorder(null);
        LoadHTML.setName("LoadHTML"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Kirim Resume Pasien ke SatuSehat ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);
        Scroll.setPreferredSize(new java.awt.Dimension(452, 200));

        tbObat.setToolTipText("Silahkan klik untuk memilih data yang mau diedit ataupun dihapus");
        tbObat.setComponentPopupMenu(jPopupMenu1);
        tbObat.setName("tbObat"); // NOI18N
        tbObat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbObatMouseClicked(evt);
            }
        });
        tbObat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tbObatKeyPressed(evt);
            }
        });
        Scroll.setViewportView(tbObat);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnKirim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/34.png"))); // NOI18N
        BtnKirim.setMnemonic('S');
        BtnKirim.setText("Kirim");
        BtnKirim.setToolTipText("Alt+S");
        BtnKirim.setName("BtnKirim"); // NOI18N
        BtnKirim.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKirim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKirimActionPerformed(evt);
            }
        });
        BtnKirim.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnKirimKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnKirim);

        BtnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        BtnUpdate.setMnemonic('U');
        BtnUpdate.setText("Update");
        BtnUpdate.setToolTipText("Alt+U");
        BtnUpdate.setName("BtnUpdate"); // NOI18N
        BtnUpdate.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnUpdateActionPerformed(evt);
            }
        });
        BtnUpdate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnUpdateKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnUpdate);

        BtnPrint.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/b_print.png"))); // NOI18N
        BtnPrint.setMnemonic('T');
        BtnPrint.setText("Print");
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
        panelGlass8.add(BtnPrint);

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
        panelGlass8.add(BtnKeluar);

        jPanel3.add(panelGlass8, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel18.setText("Tanggal :");
        jLabel18.setName("jLabel18"); // NOI18N
        jLabel18.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel18);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "23-11-2025" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari1);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel21.setText("s.d.");
        jLabel21.setName("jLabel21"); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(23, 23));
        panelGlass9.add(jLabel21);

        DTPCari2.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "23-11-2025" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(205, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('6');
        BtnCari.setToolTipText("Alt+6");
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
        panelGlass9.add(BtnCari);

        jLabel7.setText("Status :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass9.add(jLabel7);

        CmbStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semua", "Belum Terkirim", "Sudah Terkirim" }));
        CmbStatus.setName("CmbStatus"); // NOI18N
        CmbStatus.setPreferredSize(new java.awt.Dimension(110, 23));
        CmbStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmbStatusActionPerformed(evt);
            }
        });
        panelGlass9.add(CmbStatus);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
    }//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{Valid.pindah(evt,BtnPrint,BtnKeluar);}
    }//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            TCari.requestFocus();
        }else if(tabMode.getRowCount()!=0){            
            try{
                htmlContent = new StringBuilder();
                htmlContent.append(                             
                    "<tr class='isi'>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Tgl Resume</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.Rawat</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.RM</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Nama Pasien</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Jenis Resume</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Dokter</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Keluhan Utama</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Diagnosa Utama</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Status</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>ID Composition</b></td>"+
                    "</tr>"
                );
                for (i = 0; i < tabMode.getRowCount(); i++) {
                    htmlContent.append(
                        "<tr class='isi'>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,1).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,2).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,3).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,4).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,6).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,7).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,12).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,13).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,15).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,16).toString()+"</td>"+
                        "</tr>");
                }
                LoadHTML.setText(
                    "<html>"+
                      "<table width='2000px' border='0' align='center' cellpadding='1px' cellspacing='0' class='tbl_form'>"+
                       htmlContent.toString()+
                      "</table>"+
                    "</html>"
                );

                File g = new File("file2.css");            
                BufferedWriter bg = new BufferedWriter(new FileWriter(g));
                bg.write(
                    ".isi td{border-right: 1px solid #e2e7dd;font: 11px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                    ".isi2 td{font: 11px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}"+                    
                    ".isi3 td{border-right: 1px solid #e2e7dd;font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                    ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                    ".isi5 td{font: 8px tahoma;border:none;height:12px;background: #ffffff;color:#AA0000;}"+
                    ".isi6 td{font: 8px tahoma;border:none;height:12px;background: #ffffff;color:#FF0000;}"+
                    ".isi7 td{font: 8px tahoma;border:none;height:12px;background: #ffffff;color:#C8C800;}"+
                    ".isi8 td{font: 8px tahoma;border:none;height:12px;background: #ffffff;color:#00AA00;}"+
                    ".isi9 td{font: 8px tahoma;border:none;height:12px;background: #ffffff;color:#969696;}"
                );
                bg.close();

                File f = new File("DataResumeComposition.html");            
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));            
                bw.write(LoadHTML.getText().replaceAll("<head>","<head>"+
                            "<link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" />"+
                            "<table width='100%' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                                "<tr class='isi2'>"+
                                    "<td valign='top' align='center'>"+
                                        "<font size='4' face='Tahoma'>"+akses.getnamars().toUpperCase()+"</font><br>"+
                                        akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs()+"<br>"+
                                        akses.getkontakrs()+", E-mail : "+akses.getemailrs()+"<br><br>"+
                                        "<font size='2' face='Tahoma'>DATA RESUME KOMPOSISI<br><br></font>"+        
                                    "</td>"+
                               "</tr>"+
                            "</table>")
                );
                bw.close();                         
                Desktop.getDesktop().browse(f.toURI());

            }catch(Exception e){
                System.out.println("Notif : "+e);
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnPrintActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnKeluar, BtnPrint);
        }
    }//GEN-LAST:event_BtnPrintKeyPressed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
        if(tabMode.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_tbObatMouseClicked

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyPressed
        if(tabMode.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
    }//GEN-LAST:event_tbObatKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt, TCari, BtnPrint);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            BtnKeluar.requestFocus();
        }
    }//GEN-LAST:event_TCariKeyPressed

    private void BtnKirimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKirimActionPerformed
        System.out.println("=== DEBUG: Tombol Kirim Diklik ===");
        System.out.println("Total rows in table: " + tbObat.getRowCount());
        
        for(i=0;i<tbObat.getRowCount();i++){
            System.out.println("--- DEBUG ROW " + i + " ---");
            System.out.println("Row " + i + " - Checkbox: " + tbObat.getValueAt(i,0).toString());
            System.out.println("Row " + i + " - ID Composition: " + tbObat.getValueAt(i,16).toString());
            
            if(tbObat.getValueAt(i,0).toString().equals("true")&&tbObat.getValueAt(i,16).toString().equals("")){
                System.out.println("Row " + i + " - DIPILIH UNTUK DIKIRIM");
                
                // Debug semua nilai di row ini
                for(int col = 0; col < 17; col++){
                    System.out.println("  Col[" + col + "]: " + tbObat.getValueAt(i, col));
                }
                
                try {
                    try{
                        System.out.println("--- Preparing headers ---");
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
                        
                        // Get actual SatuSehat IDs like in SatuSehatKirimEncounter
                        System.out.println("--- Resolving IDs ---");
                        System.out.println("No KTP Dokter (col 8): " + tbObat.getValueAt(i,8).toString());
                        System.out.println("No KTP Pasien (col 5): " + tbObat.getValueAt(i,5).toString());
                        System.out.println("ID Encounter (col 9): " + tbObat.getValueAt(i,9).toString());
                        
                        iddokter=cekViaSatuSehat.tampilIDParktisi(tbObat.getValueAt(i,8).toString()); // No KTP dokter 
                        System.out.println("Resolved ID Dokter: " + iddokter);
                        
                        idpasien=cekViaSatuSehat.tampilIDPasien(tbObat.getValueAt(i,5).toString()); // No KTP pasien
                        System.out.println("Resolved ID Pasien: " + idpasien);
                        
                        idencounter=tbObat.getValueAt(i,9).toString(); // ID encounter dari query (kolom ke-9 di tabel)
                        System.out.println("ID Encounter: " + idencounter);
                        
                        // Create FHIR Composition JSON
                        System.out.println("--- Creating JSON ---");
                        json = createCompositionJson(i);
                        
                        System.out.println("URL : "+link+"/Composition");
                        System.out.println("Request JSON : "+json);
                        requestEntity = new HttpEntity(json,headers);
                        json=api.getRest().exchange(link+"/Composition", HttpMethod.POST, requestEntity, String.class).getBody();
                        System.out.println("Result JSON : "+json);
                        root = mapper.readTree(json);
                        response = root.path("id");
                        if(!response.asText().equals("")){
                            System.out.println("DEBUG: Saving composition ID to database...");
                            System.out.println("DEBUG: no_rawat: " + tbObat.getValueAt(i,2).toString());
                            System.out.println("DEBUG: id_composition: " + response.asText());
                            
                            try {
                                // Insert ke table satu_sehat_composition
                                PreparedStatement psInsert = koneksi.prepareStatement(
                                    "INSERT INTO satu_sehat_composition (no_rawat, id_composition, status) VALUES (?, ?, ?) " +
                                    "ON DUPLICATE KEY UPDATE id_composition = ?, tgl_kirim = NOW(), status = ?"
                                );
                                psInsert.setString(1, tbObat.getValueAt(i,2).toString()); // no_rawat
                                psInsert.setString(2, response.asText()); // id_composition
                                psInsert.setString(3, "Terkirim"); // status
                                psInsert.setString(4, response.asText()); // id_composition for update
                                psInsert.setString(5, "Terkirim"); // status for update
                                
                                int result = psInsert.executeUpdate();
                                psInsert.close();
                                
                                if(result > 0) {
                                    System.out.println("DEBUG: Database insert successful");
                                    tbObat.setValueAt(response.asText(),i,16);
                                    tbObat.setValueAt("Sudah Terkirim",i,15);
                                    tbObat.setValueAt(false,i,0);
                                } else {
                                    System.out.println("DEBUG: Database insert failed - no rows affected");
                                }
                            } catch (Exception dbError) {
                                System.out.println("ERROR saving to database: " + dbError.getMessage());
                                dbError.printStackTrace();
                            }
                        }
                    }catch(HttpClientErrorException e){
                        System.out.println("Notifikasi Bridging : "+e.getMessage());
                        System.out.println("Response Body : "+e.getResponseBodyAsString());
                    }catch(Exception e){
                        System.out.println("Notifikasi Bridging : "+e);
                    }
                } catch (Exception e) {
                    System.out.println("Notifikasi : "+e);
                }
            }
        }
    }//GEN-LAST:event_BtnKirimActionPerformed

    private String createCompositionJson(int rowIndex) {
        String currentDate = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new java.util.Date());
        
        return "{"
                + "\"resourceType\": \"Composition\","
                + "\"meta\": {"
                    + "\"profile\": ["
                        + "\"https://fhir.kemkes.go.id/r4/StructureDefinition/Composition\""
                    + "]"
                + "},"
                + "\"identifier\": {"
                    + "\"system\": \"http://sys-ids.kemkes.go.id/composition/" + koneksiDB.IDSATUSEHAT() + "\","
                    + "\"value\": \"" + tbObat.getValueAt(rowIndex, 2).toString() + "\""
                + "},"
                + "\"status\": \"final\","
                + "\"type\": {"
                    + "\"coding\": ["
                        + "{"
                            + "\"system\": \"http://loinc.org\","
                            + "\"code\": \"18842-5\","
                            + "\"display\": \"Discharge summary\""
                        + "}"
                    + "]"
                + "},"
                + "\"subject\": {"
                    + "\"reference\": \"Patient/" + idpasien + "\","
                    + "\"display\": \"" + tbObat.getValueAt(rowIndex, 4).toString() + "\""
                + "},"
                + "\"encounter\": {"
                    + "\"reference\": \"Encounter/" + idencounter + "\""
                + "},"
                + "\"date\": \"" + currentDate + "\","
                + "\"author\": ["
                    + "{"
                        + "\"reference\": \"Practitioner/" + iddokter + "\","
                        + "\"display\": \"" + tbObat.getValueAt(rowIndex, 7).toString() + "\""
                    + "}"
                + "],"
                + "\"title\": \"" + tbObat.getValueAt(rowIndex, 6).toString() + "\","
                + "\"section\": ["
                    + createChiefComplaintSection(rowIndex) + ","
                    + createAssessmentSection(rowIndex) + ","
                    + createPlanSection(rowIndex)
                + "]"
            + "}";
    }
    
    private String createChiefComplaintSection(int rowIndex) {
        return "{"
                + "\"title\": \"Keluhan Utama\","
                + "\"code\": {"
                    + "\"coding\": ["
                        + "{"
                            + "\"system\": \"http://loinc.org\","
                            + "\"code\": \"10154-3\","
                            + "\"display\": \"Chief complaint\""
                        + "}"
                    + "]"
                + "},"
                + "\"text\": {"
                    + "\"status\": \"generated\","
                    + "\"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\">" + 
                      escapeHtml(tbObat.getValueAt(rowIndex, 12).toString()) + "</div>\""
                + "}"
            + "}";
    }
    
    private String createAssessmentSection(int rowIndex) {
        return "{"
                + "\"title\": \"Diagnosa\","
                + "\"code\": {"
                    + "\"coding\": ["
                        + "{"
                            + "\"system\": \"http://loinc.org\","
                            + "\"code\": \"51847-2\","
                            + "\"display\": \"Evaluation note\""
                        + "}"
                    + "]"
                + "},"
                + "\"text\": {"
                    + "\"status\": \"generated\","
                    + "\"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\">" + 
                      escapeHtml(tbObat.getValueAt(rowIndex, 13).toString()) + "</div>\""
                + "}"
            + "}";
    }
    
    private String createPlanSection(int rowIndex) {
        return "{"
                + "\"title\": \"Rencana Tindak Lanjut\","
                + "\"code\": {"
                    + "\"coding\": ["
                        + "{"
                            + "\"system\": \"http://loinc.org\","
                            + "\"code\": \"18776-5\","
                            + "\"display\": \"Plan of care note\""
                        + "}"
                    + "]"
                + "},"
                + "\"text\": {"
                    + "\"status\": \"generated\","
                    + "\"div\": \"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\">" + 
                      escapeHtml(tbObat.getValueAt(rowIndex, 14).toString()) + "</div>\""
                + "}"
            + "}";
    }
    
    private String escapeHtml(String text) {
        if(text == null || text.isEmpty()) {
            return "-";
        }
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#x27;");
    }

    private void BtnKirimKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKirimKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnKirimActionPerformed(null);
        }else{
            Valid.pindah(evt,BtnKeluar,BtnKirim);
        }
    }//GEN-LAST:event_BtnKirimKeyPressed

    private void BtnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnUpdateActionPerformed
        for(i=0;i<tbObat.getRowCount();i++){
            if(tbObat.getValueAt(i,0).toString().equals("true")&&(!tbObat.getValueAt(i,16).toString().equals(""))){
                try {
                    try{
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
                        
                        // Resolve patient and practitioner IDs
                        iddokter=cekViaSatuSehat.tampilIDParktisi(tbObat.getValueAt(i,8).toString()); // No KTP dokter 
                        idpasien=cekViaSatuSehat.tampilIDPasien(tbObat.getValueAt(i,5).toString()); // No KTP pasien
                        idencounter=tbObat.getValueAt(i,9).toString(); // ID encounter dari query (kolom ke-9 di tabel)
                        
                        // Create FHIR Composition JSON with ID for update
                        json = createCompositionJsonForUpdate(i);
                        
                        System.out.println("URL : "+link+"/Composition/"+tbObat.getValueAt(i,16).toString());
                        System.out.println("Request JSON : "+json);
                        requestEntity = new HttpEntity(json,headers);
                        json=api.getRest().exchange(link+"/Composition/"+tbObat.getValueAt(i,16).toString(), HttpMethod.PUT, requestEntity, String.class).getBody();
                        System.out.println("Result JSON : "+json);
                        tbObat.setValueAt(false,i,0);
                    }catch(HttpClientErrorException e){
                        System.out.println("Notifikasi Bridging : "+e.getMessage());
                        System.out.println("Response Body : "+e.getResponseBodyAsString());
                    }catch(Exception e){
                        System.out.println("Notifikasi Bridging : "+e);
                    }
                } catch (Exception e) {
                    System.out.println("Notifikasi : "+e);
                }
            }
        }
    }//GEN-LAST:event_BtnUpdateActionPerformed

    private String createCompositionJsonForUpdate(int rowIndex) {
        String currentDate = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new java.util.Date());
        
        return "{"
                + "\"resourceType\": \"Composition\","
                + "\"id\": \"" + tbObat.getValueAt(rowIndex, 16).toString() + "\","
                + "\"meta\": {"
                    + "\"profile\": ["
                        + "\"https://fhir.kemkes.go.id/r4/StructureDefinition/Composition\""
                    + "]"
                + "},"
                + "\"identifier\": {"
                    + "\"system\": \"http://sys-ids.kemkes.go.id/composition/" + koneksiDB.IDSATUSEHAT() + "\","
                    + "\"value\": \"" + tbObat.getValueAt(rowIndex, 2).toString() + "\""
                + "},"
                + "\"status\": \"final\","
                + "\"type\": {"
                    + "\"coding\": ["
                        + "{"
                            + "\"system\": \"http://loinc.org\","
                            + "\"code\": \"18842-5\","
                            + "\"display\": \"Discharge summary\""
                        + "}"
                    + "]"
                + "},"
                + "\"subject\": {"
                    + "\"reference\": \"Patient/" + idpasien + "\","
                    + "\"display\": \"" + tbObat.getValueAt(rowIndex, 4).toString() + "\""
                + "},"
                + "\"encounter\": {"
                    + "\"reference\": \"Encounter/" + idencounter + "\""
                + "},"
                + "\"date\": \"" + currentDate + "\","
                + "\"author\": ["
                    + "{"
                        + "\"reference\": \"Practitioner/" + iddokter + "\","
                        + "\"display\": \"" + tbObat.getValueAt(rowIndex, 7).toString() + "\""
                    + "}"
                + "],"
                + "\"title\": \"" + tbObat.getValueAt(rowIndex, 6).toString() + "\","
                + "\"section\": ["
                    + createChiefComplaintSection(rowIndex) + ","
                    + createAssessmentSection(rowIndex) + ","
                    + createPlanSection(rowIndex)
                + "]"
            + "}";
    }

    private void BtnUpdateKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnUpdateKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnUpdateActionPerformed(null);
        }else{
            Valid.pindah(evt,BtnKeluar,BtnUpdate);
        }
    }//GEN-LAST:event_BtnUpdateKeyPressed

    private void ppPilihSemuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppPilihSemuaActionPerformed
        for(i=0;i<tbObat.getRowCount();i++){
            tbObat.setValueAt(true,i,0);
        }
    }//GEN-LAST:event_ppPilihSemuaActionPerformed

    private void ppBersihkanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ppBersihkanActionPerformed
        for(i=0;i<tbObat.getRowCount();i++){
            tbObat.setValueAt(false,i,0);
        }
    }//GEN-LAST:event_ppBersihkanActionPerformed

    private void CmbStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CmbStatusActionPerformed
        tampil();
    }//GEN-LAST:event_CmbStatusActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            SatuSehatKirimComposition dialog = new SatuSehatKirimComposition(new javax.swing.JFrame(), true);
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
    private widget.Button BtnCari;
    private widget.Button BtnKeluar;
    private widget.Button BtnKirim;
    private widget.Button BtnPrint;
    private widget.Button BtnUpdate;
    private widget.ComboBox CmbStatus;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.editorpane LoadHTML;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel18;
    private widget.Label jLabel21;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private javax.swing.JMenuItem ppBersihkan;
    private javax.swing.JMenuItem ppPilihSemua;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {        
        System.out.println("=== DEBUG: Method tampil() dipanggil ===");
        Valid.tabelKosong(tabMode);
        try{
            String statusFilter = "";
            if(CmbStatus.getSelectedIndex() == 1) {
                statusFilter = " and ifnull(ssc.id_composition,'') = '' ";
                System.out.println("DEBUG: Status filter = Belum Terkirim");
            } else if(CmbStatus.getSelectedIndex() == 2) {
                statusFilter = " and ifnull(ssc.id_composition,'') <> '' ";
                System.out.println("DEBUG: Status filter = Sudah Terkirim");
            } else {
                System.out.println("DEBUG: Status filter = Semua");
            }
            
            // Query gabungan untuk rawat jalan dan rawat inap - hanya yang sudah ada encounternya
            ps=koneksi.prepareStatement(
                "("
                + "select date(rp.tgl_registrasi) as tgl_resume,rp.no_rawat,rp.no_rkm_medis,"
                + "p.nm_pasien,p.no_ktp,'Resume Rawat Jalan' as jenis_resume,peg.nama as nm_dokter,peg.no_ktp as no_ktp_dokter,"
                + "sse.id_encounter,"
                + "p.no_rkm_medis as id_patient,"
                + "rp.kd_dokter as id_practitioner,"
                + "ifnull(rsp.keluhan_utama,'') as keluhan_utama,"
                + "concat(ifnull(rsp.diagnosa_utama,''),' (',ifnull(rsp.kd_diagnosa_utama,''),')') as diagnosa_utama,"
                + "concat(ifnull(rsp.prosedur_utama,''),' (',ifnull(rsp.kd_prosedur_utama,''),')') as tindakan,"
                + "if(ifnull(ssc.id_composition,'')='','Belum Terkirim','Sudah Terkirim') as status,"
                + "ifnull(ssc.id_composition,'') as id_composition "
                + "from reg_periksa rp "
                + "inner join pasien p on rp.no_rkm_medis=p.no_rkm_medis "
                + "inner join pegawai peg on rp.kd_dokter=peg.nik "
                + "inner join resume_pasien rsp on rp.no_rawat=rsp.no_rawat "
                + "inner join satu_sehat_encounter sse on rp.no_rawat=sse.no_rawat "
                + "left join satu_sehat_composition ssc on rp.no_rawat=ssc.no_rawat "
                + "where date(rp.tgl_registrasi) between ? and ? "
                + "and (rp.no_rawat like ? or p.nm_pasien like ? or p.no_ktp like ? or peg.nama like ?) "
                + statusFilter
                + ") "
                + "union "
                + "("
                + "select date(rpr.tgl_registrasi) as tgl_resume,rpr.no_rawat,rpr.no_rkm_medis,"
                + "p.nm_pasien,p.no_ktp,'Resume Rawat Inap' as jenis_resume,peg2.nama as nm_dokter,peg2.no_ktp as no_ktp_dokter,"
                + "sse.id_encounter,"
                + "p.no_rkm_medis as id_patient,"
                + "rpr.kd_dokter as id_practitioner,"
                + "ifnull(rspr.keluhan_utama,'') as keluhan_utama,"
                + "concat(ifnull(rspr.diagnosa_utama,''),' (',ifnull(rspr.kd_diagnosa_utama,''),')') as diagnosa_utama,"
                + "concat(ifnull(rspr.prosedur_utama,''),' (',ifnull(rspr.kd_prosedur_utama,''),')') as tindakan,"
                + "if(ifnull(ssc.id_composition,'')='','Belum Terkirim','Sudah Terkirim') as status,"
                + "ifnull(ssc.id_composition,'') as id_composition "
                + "from reg_periksa rpr "
                + "inner join pasien p on rpr.no_rkm_medis=p.no_rkm_medis "
                + "inner join pegawai peg2 on rpr.kd_dokter=peg2.nik "
                + "inner join resume_pasien_ranap rspr on rpr.no_rawat=rspr.no_rawat "
                + "inner join satu_sehat_encounter sse on rpr.no_rawat=sse.no_rawat "
                + "left join satu_sehat_composition ssc on rpr.no_rawat=ssc.no_rawat "
                + "where date(rpr.tgl_registrasi) between ? and ? "
                + "and (rpr.no_rawat like ? or p.nm_pasien like ? or p.no_ktp like ? or peg2.nama like ?) "
                + statusFilter
                + ") "
                + "order by tgl_resume desc");
                
            System.out.println("DEBUG: Query prepared");
            System.out.println("DEBUG: Parameter 1: " + Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
            System.out.println("DEBUG: Parameter 2: " + Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
            System.out.println("DEBUG: TCari: %" + TCari.getText() + "%");
            
            ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
            ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
            ps.setString(3,"%"+TCari.getText()+"%");
            ps.setString(4,"%"+TCari.getText()+"%");
            ps.setString(5,"%"+TCari.getText()+"%");
            ps.setString(6,"%"+TCari.getText()+"%");
            ps.setString(7,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
            ps.setString(8,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
            ps.setString(9,"%"+TCari.getText()+"%");
            ps.setString(10,"%"+TCari.getText()+"%");
            ps.setString(11,"%"+TCari.getText()+"%");
            ps.setString(12,"%"+TCari.getText()+"%");
            
            System.out.println("DEBUG: Executing query...");
            rs=ps.executeQuery();
            System.out.println("DEBUG: Query executed successfully");
            
            int rowCount = 0;
            while(rs.next()){
                rowCount++;
                System.out.println("DEBUG: Processing row " + rowCount);
                tabMode.addRow(new Object[]{
                    false,rs.getString("tgl_resume"),rs.getString("no_rawat"),rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"),rs.getString("no_ktp"),rs.getString("jenis_resume"),
                    rs.getString("nm_dokter"),rs.getString("no_ktp_dokter"),rs.getString("id_encounter"),
                    rs.getString("id_patient"),rs.getString("id_practitioner"),rs.getString("keluhan_utama"),
                    rs.getString("diagnosa_utama"),rs.getString("tindakan"),rs.getString("status"),
                    rs.getString("id_composition")
                });
            }
            System.out.println("DEBUG: Total rows added to table: " + rowCount);
        }catch(Exception e){
            System.out.println("ERROR in tampil(): " + e.getMessage());
            e.printStackTrace();
        }finally{
            if(rs!=null){
                try{
                    rs.close();
                }catch(Exception e){
                    System.out.println("Notif : "+e);
                }
            }
            if(ps!=null){
                try{
                    ps.close();
                }catch(Exception e){
                    System.out.println("Notif : "+e);
                }
            }
        }        
    }
    
    public void getData() {
        if(tbObat.getSelectedRow()!= -1){
            // Implementation for getting selected row data if needed
        }
    }
}