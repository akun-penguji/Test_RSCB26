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

/**
 *
 * @author dosen
 */
public final class SatuSehatKirimTriaseIGD extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
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

    /** Creates new form SatuSehatKirimTriaseIGD
     * @param parent
     * @param modal */
    public SatuSehatKirimTriaseIGD(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.setLocation(10,2);
        setSize(628,674);

        tabMode=new DefaultTableModel(null,new String[]{
                "P","No.Rawat","No.RM","Nama Pasien","NIK Pasien","Kode Dokter","Nama Dokter","NIK Dokter",
                "ID Encounter","Tgl Triase","Cara Masuk","Kode Kasus","Macam Kasus","Plan","Keluhan",
                "TD","Nadi","Resp","Suhu","SpO2","Nyeri","Source",
                "ID Obs ModeArrival","ID Obs Kategori","ID Obs TD","ID Obs Nadi","ID Obs Resp","ID Obs Suhu","ID Obs SpO2","ID Obs Nyeri","ID Obs Disposition",
                "ID Condition","ID Composition","ID Obs ABCD","ID ClinicalImpression","ID Obs KondisiTiba"
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
                 java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,
                 java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,
                 java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,
                 java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,
                 java.lang.String.class,java.lang.String.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbObat.setModel(tabMode);

        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 36; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(105);
            }else if(i==2){
                column.setPreferredWidth(70);
            }else if(i==3){
                column.setPreferredWidth(150);
            }else if(i==4){
                column.setPreferredWidth(110);
            }else if(i==5){
                column.setPreferredWidth(80);
            }else if(i==6){
                column.setPreferredWidth(150);
            }else if(i==7){
                column.setPreferredWidth(110);
            }else if(i==8){
                column.setPreferredWidth(210);
            }else if(i==9){
                column.setPreferredWidth(135);
            }else if(i==10){
                column.setPreferredWidth(90);
            }else if(i==11){
                column.setPreferredWidth(70);
            }else if(i==12){
                column.setPreferredWidth(150);
            }else if(i==13){
                column.setPreferredWidth(120);
            }else if(i==14){
                column.setPreferredWidth(200);
            }else if(i==15){
                column.setPreferredWidth(60);
            }else if(i==16){
                column.setPreferredWidth(50);
            }else if(i==17){
                column.setPreferredWidth(50);
            }else if(i==18){
                column.setPreferredWidth(50);
            }else if(i==19){
                column.setPreferredWidth(50);
            }else if(i==20){
                column.setPreferredWidth(50);
            }else if(i==21){
                column.setPreferredWidth(70);
            }else if(i>=22 && i<=35){
                column.setPreferredWidth(210);
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
                ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"
        );
        Document doc = kit.createDefaultDocument();
        LoadHTML.setDocument(doc);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
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

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        ppPilihSemua.setBackground(new java.awt.Color(255, 255, 254));
        ppPilihSemua.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        ppPilihSemua.setForeground(new java.awt.Color(50, 50, 50));
        ppPilihSemua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        ppPilihSemua.setText("Pilih Semua");
        ppPilihSemua.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppPilihSemua.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppPilihSemua.setName("ppPilihSemua"); // NOI18N
        ppPilihSemua.setPreferredSize(new java.awt.Dimension(150, 26));
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
        ppBersihkan.setText("Hilangkan Pilihan");
        ppBersihkan.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        ppBersihkan.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        ppBersihkan.setName("ppBersihkan"); // NOI18N
        ppBersihkan.setPreferredSize(new java.awt.Dimension(150, 26));
        ppBersihkan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ppBersihkanActionPerformed(evt);
            }
        });
        jPopupMenu1.add(ppBersihkan);

        LoadHTML.setBorder(null);
        LoadHTML.setName("LoadHTML"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(null);
        setIconImages(null);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Pengiriman Data Triase IGD Satu Sehat ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setComponentPopupMenu(jPopupMenu1);
        Scroll.setName("Scroll"); // NOI18N
        Scroll.setOpaque(true);

        tbObat.setComponentPopupMenu(jPopupMenu1);
        tbObat.setName("tbObat"); // NOI18N
        Scroll.setViewportView(tbObat);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3"); // NOI18N
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(53, 23));
        panelGlass8.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass8.add(LCount);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png"))); // NOI18N
        BtnAll.setMnemonic('M');
        BtnAll.setText("Semua");
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll"); // NOI18N
        BtnAll.setPreferredSize(new java.awt.Dimension(100, 30));
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
        panelGlass8.add(BtnAll);

        BtnKirim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/34.png"))); // NOI18N
        BtnKirim.setMnemonic('K');
        BtnKirim.setText("Kirim");
        BtnKirim.setToolTipText("Alt+K");
        BtnKirim.setName("BtnKirim"); // NOI18N
        BtnKirim.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnKirim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnKirimActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnKirim);

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

        jLabel15.setText("Tgl.Triase :");
        jLabel15.setName("jLabel15"); // NOI18N
        jLabel15.setPreferredSize(new java.awt.Dimension(85, 23));
        panelGlass9.add(jLabel15);

        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "06-11-2024" }));
        DTPCari1.setDisplayFormat("dd-MM-yyyy");
        DTPCari1.setName("DTPCari1"); // NOI18N
        DTPCari1.setOpaque(false);
        DTPCari1.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari1);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("s.d.");
        jLabel17.setName("jLabel17"); // NOI18N
        jLabel17.setPreferredSize(new java.awt.Dimension(24, 23));
        panelGlass9.add(jLabel17);

        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "06-11-2024" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(95, 23));
        panelGlass9.add(DTPCari2);

        jLabel16.setText("Key Word :");
        jLabel16.setName("jLabel16"); // NOI18N
        jLabel16.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel16);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(210, 23));
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
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.Rawat</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.RM</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Nama Pasien</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>NIK Pasien</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Nama Dokter</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>NIK Dokter</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>ID Encounter</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Tgl Triase</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Cara Masuk</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Macam Kasus</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Plan</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Keluhan</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Source</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>ID Composition</b></td>"+
                    "</tr>"
                );
                for (i = 0; i < tabMode.getRowCount(); i++) {
                    htmlContent.append(
                        "<tr class='isi'>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,1).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,2).toString()+"</td>"+
                            "<td valign='top'>"+jsonEscape(tbObat.getValueAt(i,3).toString())+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,4).toString()+"</td>"+
                            "<td valign='top'>"+jsonEscape(tbObat.getValueAt(i,6).toString())+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,7).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,8).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,9).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,10).toString()+"</td>"+
                            "<td valign='top'>"+jsonEscape(tbObat.getValueAt(i,12).toString())+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,13).toString()+"</td>"+
                            "<td valign='top'>"+jsonEscape(tbObat.getValueAt(i,14).toString())+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,21).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,32).toString()+"</td>"+
                        "</tr>");
                }
                LoadHTML.setText(
                    "<html>"+
                      "<table width='3000px' border='0' align='center' cellpadding='1px' cellspacing='0' class='tbl_form'>"+
                       htmlContent.toString()+
                      "</table>"+
                    "</html>"
                );

                File g = new File("file2.css");
                BufferedWriter bg = new BufferedWriter(new FileWriter(g));
                bg.write(
                    ".isi td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-bottom: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                    ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}"
                );
                bg.close();

                File f = new File("DataSatuSehatTriaseIGD.html");
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                bw.write(LoadHTML.getText().replaceAll("<head>","<head>"+
                            "<link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" />"+
                            "<table width='3000px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                                "<tr class='isi2'>"+
                                    "<td valign='top' align='center'>"+
                                        "<font size='4' face='Tahoma'>"+akses.getnamars()+"</font><br>"+
                                        akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs()+"<br>"+
                                        akses.getkontakrs()+", E-mail : "+akses.getemailrs()+"<br><br>"+
                                        "<font size='2' face='Tahoma'>DATA PENGIRIMAN SATU SEHAT TRIASE IGD<br><br></font>"+
                                    "</td>"+
                               "</tr>"+
                            "</table>")
                );
                bw.close();
                Desktop.getDesktop().browse(f.toURI());
            }catch(Exception e){
                System.out.println("Notifikasi : "+e);
            }
        }
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnPrintActionPerformed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            BtnKeluar.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_UP){
            tbObat.requestFocus();
        }
    }//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        tampil();
        this.setCursor(Cursor.getDefaultCursor());
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt,TCari,BtnPrint);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    private void BtnKirimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKirimActionPerformed
        for(i=0;i<tbObat.getRowCount();i++){
            if(tbObat.getValueAt(i,0).toString().equals("true")
                    &&(!tbObat.getValueAt(i,4).toString().equals(""))
                    &&(!tbObat.getValueAt(i,7).toString().equals(""))
                    &&(!tbObat.getValueAt(i,8).toString().equals(""))
                    && tbObat.getValueAt(i,32).toString().equals("")){
                try {
                    idpasien=cekViaSatuSehat.tampilIDPasien(tbObat.getValueAt(i,4).toString());
                    iddokter=cekViaSatuSehat.tampilIDParktisi(tbObat.getValueAt(i,7).toString());
                    if(idpasien==null||idpasien.equals("")||iddokter==null||iddokter.equals("")){
                        continue;
                    }
                    kirimSatuRow(i,idpasien,iddokter);
                } catch (Exception e) {
                    System.out.println("Notifikasi : "+e);
                }
            }
        }
    }//GEN-LAST:event_BtnKirimActionPerformed

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

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        tampil();
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            TCari.setText("");
            tampil();
        }else{
            Valid.pindah(evt, BtnPrint, BtnKeluar);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    /** Escape karakter untuk pembentukan JSON manual.
     *  Mencegah SatuSehat reject "Invalid JSON Payload Format" (Rule 20006). */
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
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    /** Convert "yyyy-MM-dd HH:mm:ss" to FHIR ISO UTC +00:00 via IsoTime helper. */
    private static String toIso(String tgl) {
        if (tgl == null || tgl.isEmpty()) return "";
        String t = tgl.trim();
        if (t.length() < 10) return t;
        if (t.length() == 10) return fungsi.IsoTime.toUtc(t, "00:00:00");
        return fungsi.IsoTime.toUtc(t);
    }

    /** Parse first decimal/integer from a noisy string. */
    private static String parseNum(String s) {
        if (s == null) return "";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+(?:\\.\\d+)?)").matcher(s);
        return m.find() ? m.group(1) : "";
    }

    /** POST a resource to SatuSehat FHIR endpoint, return id from response or "" on failure. */
    /** Mode update: PUT resource yang sudah punya id, bukan POST baru. */
    private boolean modeUpdate = false;
    /** Berapa resource yang benar-benar terkirim/diperbarui pada satu proses. */
    private int jmlKirimResource = 0;

    /**
     * POST bila belum punya id, PUT bila sudah punya id DAN sedang mode update.
     * Semua body di form ini diawali {"resourceType":"...", jadi id untuk PUT
     * cukup disisipkan di depan (urutan field JSON tidak bermakna di FHIR).
     */
    private String kirimResource(String resourceType, String idLama, String body) {
        if (modeUpdate && idLama != null && !idLama.isEmpty()) {
            String bodyPut = "{\"id\":\"" + idLama + "\"," + body.substring(1);
            if (putResource(resourceType, idLama, bodyPut)) { jmlKirimResource++; return idLama; }
            return idLama;                      // gagal update: id lama dipertahankan
        }
        String id = postResource(resourceType, body);
        if (!id.equals("")) jmlKirimResource++;
        return id;
    }

    private boolean putResource(String resourceType, String id, String body) {
        try{
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
            System.out.println("URL : "+link+"/"+resourceType+"/"+id);
            System.out.println("Request JSON : "+body);
            requestEntity = new HttpEntity(body,headers);
            String resp = api.getRest().exchange(link+"/"+resourceType+"/"+id, HttpMethod.PUT, requestEntity, String.class).getBody();
            System.out.println("Result JSON : "+resp);
            return true;
        }catch(org.springframework.web.client.HttpStatusCodeException e){
            System.out.println("Notifikasi Bridging PUT : "+e+" => "+e.getResponseBodyAsString());
        }catch(Exception e){
            System.out.println("Notifikasi Bridging PUT : "+e);
        }
        return false;
    }

    private String postResource(String resourceType, String body) {
        try{
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
            System.out.println("URL : "+link+"/"+resourceType);
            System.out.println("Request JSON : "+body);
            requestEntity = new HttpEntity(body,headers);
            String resp = api.getRest().exchange(link+"/"+resourceType, HttpMethod.POST, requestEntity, String.class).getBody();
            System.out.println("Result JSON : "+resp);
            JsonNode r = mapper.readTree(resp);
            JsonNode rid = r.path("id");
            if(rid!=null && !rid.asText().equals("")){
                return rid.asText();
            }
        }catch(org.springframework.web.client.HttpClientErrorException e){
            System.out.println("Notifikasi Bridging : "+e);
            System.out.println("Response Body : "+e.getResponseBodyAsString());
        }catch(Exception e){
            System.out.println("Notifikasi Bridging : "+e);
        }
        return "";
    }

    /** Build a simple LOINC-coded Observation with valueQuantity. */
    private String buildObsQuantity(String loincCode, String loincDisplay, String idObsPasien, String idObsEnc, String idPerformer,
                                    String effective, String value, String unitDisplay, String ucumCode, String category) {
        return "{"+
            "\"resourceType\":\"Observation\","+
            "\"status\":\"final\","+
            "\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/observation-category\",\"code\":\""+category+"\"}]}],"+
            "\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\""+loincCode+"\",\"display\":\""+loincDisplay+"\"}]},"+
            "\"subject\":{\"reference\":\"Patient/"+idObsPasien+"\"},"+
            "\"encounter\":{\"reference\":\"Encounter/"+idObsEnc+"\"},"+
            "\"performer\":[{\"reference\":\"Practitioner/"+idPerformer+"\"}],"+
            "\"effectiveDateTime\":\""+effective+"\","+
            "\"valueQuantity\":{\"value\":"+value+",\"unit\":\""+unitDisplay+"\",\"system\":\"http://unitsofmeasure.org\",\"code\":\""+ucumCode+"\"}"+
        "}";
    }

    /**
     * Komponen Penilaian ABCDE beserta kode LOINC-nya, urut A-B-C-D-E.
     *
     * Komponen E hanya terkirim bila master triase punya item PAPARAN (EXPOSURE)
     * -- lihat sql/076. Instalasi yang masternya belum punya item itu tetap
     * mengirim A-D saja tanpa perlu perubahan kode.
     */
    private static final String[][] KOMPONEN_ABCD = {
        {"A","69046-1","Airway status"},
        {"B","103776-1","Breathing"},
        {"C","28256-6","Circulation Status [OMAHA]"},
        {"D","101720-1","Disability status"},
        {"E","104964-2","Exposure description Narrative"}
    };

    /**
     * Menggolongkan satu item master_triase_pemeriksaan ke komponen A/B/C/D/E.
     *
     * Sengaja dipetakan dari NAMA, bukan kode_pemeriksaan: isi master berbeda-beda
     * antar rumah sakit yang memakai codebase ini. RSUD Cabang Bungin punya empat item
     * (001 AIRWAY .. 004 DISABILITY), instalasi lain memecahnya per dewasa/anak
     * (001 JALAN NAFAS, 002 PERNAFASAN DEWASA, 003 PERNAFASAN ANAK, 004 SIRKULASI
     * DEWASA, ...). Memetakan lewat kode akan mengirim sirkulasi sebagai disability
     * di instalasi kedua.
     *
     * Varian dewasa/anak sengaja jatuh ke komponen yang sama — temuannya digabung.
     *
     * @return "A", "B", "C", "D", "E", atau null bila item bukan bagian ABCDE
     *         (mis. SKOR NYERI yang sudah dikirim terpisah sebagai LOINC 38208-5).
     */
    private String golonganAbcd(String namaPemeriksaan) {
        if(namaPemeriksaan==null) return null;
        String n = namaPemeriksaan.toUpperCase();
        // "JALAN NAFAS" harus diperiksa lebih dulu: "PERNAFASAN" juga memuat "NAFAS".
        if(n.contains("JALAN NAFAS") || n.contains("JALAN NAPAS") || n.contains("AIRWAY")) return "A";
        if(n.contains("PERNAFASAN") || n.contains("PERNAPASAN") || n.contains("BREATHING")) return "B";
        if(n.contains("SIRKULASI")  || n.contains("CIRCULATION")) return "C";
        if(n.contains("KESADARAN")  || n.contains("MENTAL") || n.contains("DISABILITY")
           || n.contains("NEUROLOGI")) return "D";
        if(n.contains("PAPARAN")    || n.contains("EXPOSURE") || n.contains("ENVIRONMENT")) return "E";
        return null;
    }

    /**
     * Merangkai temuan ABCD pasien dari rantai centang Pemeriksaan pada form triase:
     * data_triase_igddetail_skala1..5 -> master_triase_skala1..5 -> master_triase_pemeriksaan.
     *
     * Skala 1..5 adalah tingkat kegawatan, bukan urutan pemeriksaan, jadi kelimanya
     * digabung lalu dikelompokkan per pemeriksaan. Hasilnya kalimat seperti
     * "DISTRES PERNAPASAN RINGAN, WHEEZING" — bentuk yang diminta panduan.
     *
     * Penggolongan ke A/B/C/D lewat {@link #golonganAbcd(String)} berdasarkan nama
     * pemeriksaan, sehingga tetap benar pada instalasi yang isi masternya berbeda.
     *
     * @return peta golongan (A/B/C/D/E) -> teks temuan; kosong bila pasien tidak punya centang.
     */
    private java.util.Map<String,String> ambilTemuanAbcd(String noRawat) {
        java.util.Map<String,String> hasil = new java.util.LinkedHashMap<>();
        StringBuilder union = new StringBuilder();
        for(int s=1; s<=5; s++){
            if(s>1) union.append(" union all ");
            union.append("select m.kode_pemeriksaan, m.pengkajian_skala").append(s).append(" as temuan ")
                 .append("from data_triase_igddetail_skala").append(s).append(" d ")
                 .append("join master_triase_skala").append(s).append(" m on m.kode_skala").append(s)
                 .append("=d.kode_skala").append(s).append(" where d.no_rawat=?");
        }
        String sql = "select p.nama_pemeriksaan, group_concat(t.temuan order by t.temuan separator ', ') as teks "+
                     "from ("+union+") t "+
                     "join master_triase_pemeriksaan p on p.kode_pemeriksaan=t.kode_pemeriksaan "+
                     "group by p.kode_pemeriksaan, p.nama_pemeriksaan";
        try (PreparedStatement ps = koneksi.prepareStatement(sql)) {
            for(int s=1; s<=5; s++) ps.setString(s, noRawat);
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()){
                    String teks = rs.getString("teks");
                    if(teks==null || teks.trim().isEmpty()) continue;
                    String golongan = golonganAbcd(rs.getString("nama_pemeriksaan"));
                    if(golongan==null) continue;   // mis. SKOR NYERI, ASSESMENT TRIASE
                    // Varian dewasa/anak jatuh ke golongan sama -> temuannya disambung.
                    String sudahAda = hasil.get(golongan);
                    hasil.put(golongan, sudahAda==null ? teks.trim() : sudahAda+", "+teks.trim());
                }
            }
        } catch(Exception e){
            System.out.println("Notif ambil temuan ABCD : "+e);
        }
        return hasil;
    }

    private void kirimSatuRow(int row, String idP, String idD) {
        String noRawat=tbObat.getValueAt(row,1).toString();
        String nmPasien=tbObat.getValueAt(row,3).toString();
        String idEnc=tbObat.getValueAt(row,8).toString();
        String tglTriase=tbObat.getValueAt(row,9).toString();
        String caraMasuk=tbObat.getValueAt(row,10).toString();
        String plan=tbObat.getValueAt(row,13).toString();
        String keluhan=tbObat.getValueAt(row,14).toString();
        String td=tbObat.getValueAt(row,15).toString();
        String nadi=tbObat.getValueAt(row,16).toString();
        String resp=tbObat.getValueAt(row,17).toString();
        String suhu=tbObat.getValueAt(row,18).toString();
        String spo2=tbObat.getValueAt(row,19).toString();
        String nyeri=tbObat.getValueAt(row,20).toString();
        String source=tbObat.getValueAt(row,21).toString();
        String effective=toIso(tglTriase);

        // Anti-duplikat: muat ulang ID yang sudah tersimpan di tracking (mis. vitals/mode
        // yang sudah dikirim di pass primer) agar pass sekunder tidak mengirim ulang -> 400 duplicate.
        try {
            PreparedStatement psc=koneksi.prepareStatement("select id_obs_modearrival,id_obs_kategori,id_obs_td,id_obs_nadi,id_obs_resp,id_obs_suhu,id_obs_spo2,id_obs_nyeri,id_obs_disposition,id_obs_abcd,id_condition,id_composition,id_clinicalimpression,id_obs_kondisitiba from satu_sehat_triase_igd where no_rawat=?");
            psc.setString(1,noRawat);
            ResultSet rsc=psc.executeQuery();
            if(rsc.next()){
                String[] cols={"id_obs_modearrival","id_obs_kategori","id_obs_td","id_obs_nadi","id_obs_resp","id_obs_suhu","id_obs_spo2","id_obs_nyeri","id_obs_disposition","id_obs_abcd","id_condition","id_composition","id_clinicalimpression","id_obs_kondisitiba"};
                int[] idx={22,23,24,25,26,27,28,29,30,33,31,32,34,35};
                for(int k=0;k<cols.length;k++){
                    String v=rsc.getString(cols[k]);
                    if(v!=null && !v.isEmpty() && tbObat.getValueAt(row,idx[k]).toString().isEmpty()) tbObat.setValueAt(v,row,idx[k]);
                }
            }
            rsc.close(); psc.close();
        } catch(Exception eDup){ System.out.println("Notif anti-duplikat triase : "+eDup); }

        // 1. Mode of arrival
        String idObsMode=tbObat.getValueAt(row,22).toString();
        if((modeUpdate || idObsMode.equals("")) && !caraMasuk.equals("") && !caraMasuk.equals("-")){
            String body = "{"+
                "\"resourceType\":\"Observation\","+
                "\"status\":\"final\","+
                "\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/observation-category\",\"code\":\"exam\",\"display\":\"Exam\"}]}],"+
                "\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"11459-5\",\"display\":\"Mode of arrival\"}]},"+
                "\"subject\":{\"reference\":\"Patient/"+idP+"\",\"display\":\""+jsonEscape(nmPasien)+"\"},"+
                "\"encounter\":{\"reference\":\"Encounter/"+idEnc+"\"},"+
                "\"performer\":[{\"reference\":\"Practitioner/"+idD+"\"}],"+
                "\"effectiveDateTime\":\""+effective+"\","+
                "\"valueString\":\""+jsonEscape(caraMasuk)+"\""+
            "}";
            idObsMode = kirimResource("Observation", idObsMode, body);
            if(!idObsMode.equals("")) tbObat.setValueAt(idObsMode, row, 22);
        }

        // 1b. Sarana Transportasi Kedatangan (Observation LOINC 74286-6) dari
        // data_triase_igd.alat_transportasi. Berbeda dari Mode of arrival (cara_masuk):
        // widget "Sarana Transportasi Kedatangan" di Claims Console membaca ini.
        // Tak punya kolom model; id dibaca/ditulis langsung ke penjejak.
        String idObsTransport="";
        try (PreparedStatement pst=koneksi.prepareStatement(
                "select ifnull(id_obs_transport,'') from satu_sehat_triase_igd where no_rawat=?")) {
            pst.setString(1,noRawat);
            try (ResultSet rst=pst.executeQuery()){ if(rst.next()) idObsTransport=rst.getString(1); }
        } catch(Exception et){ System.out.println("Notif baca id_obs_transport : "+et); }
        if(modeUpdate || idObsTransport.equals("")){
            String transport="";
            try (PreparedStatement psa=koneksi.prepareStatement(
                    "select alat_transportasi from data_triase_igd where no_rawat=?")) {
                psa.setString(1,noRawat);
                try (ResultSet rsa=psa.executeQuery()){ if(rsa.next()){ String v=rsa.getString("alat_transportasi"); if(v!=null) transport=v.trim(); } }
            } catch(Exception ea){ System.out.println("Notif ambil alat_transportasi : "+ea); }
            if(!transport.isEmpty() && !transport.equals("-")){
                // Map alat_transportasi -> valueCodeableConcept sesuai playbook IGD.
                String tSys="http://snomed.info/sct", tCode="71783008", tDisp="Car"; // Sendiri/Swasta -> mobil
                String tu=transport.toUpperCase();
                if(tu.contains("AGD")||tu.contains("AMBULAN")){ tSys="http://loinc.org"; tCode="LA9315-8"; tDisp="Ground ambulance"; }
                else if(tu.contains("MOTOR")){ tSys="http://snomed.info/sct"; tCode="90748009"; tDisp="Motorcycle"; }
                String body = "{"+
                    "\"resourceType\":\"Observation\","+
                    "\"status\":\"final\","+
                    "\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/observation-category\",\"code\":\"exam\",\"display\":\"Exam\"}]}],"+
                    "\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"74286-6\",\"display\":\"Transport mode to hospital\"}],\"text\":\"Sarana Transportasi Kedatangan\"},"+
                    "\"subject\":{\"reference\":\"Patient/"+idP+"\",\"display\":\""+jsonEscape(nmPasien)+"\"},"+
                    "\"encounter\":{\"reference\":\"Encounter/"+idEnc+"\"},"+
                    "\"performer\":[{\"reference\":\"Practitioner/"+idD+"\"}],"+
                    "\"effectiveDateTime\":\""+effective+"\","+
                    "\"valueCodeableConcept\":{\"coding\":[{\"system\":\""+tSys+"\",\"code\":\""+tCode+"\",\"display\":\""+tDisp+"\"}],\"text\":\""+jsonEscape(transport)+"\"}"+
                "}";
                idObsTransport = kirimResource("Observation", idObsTransport, body);
            }
        }

        // 1c. Surat Pengantar Rujukan (Observation OC000034, valueBoolean) dari
        // data_triase_igd.alasan_kedatangan = "Rujukan" -> true, selain itu false.
        String idObsRujukan="";
        try (PreparedStatement psr=koneksi.prepareStatement(
                "select ifnull(id_obs_rujukan,'') from satu_sehat_triase_igd where no_rawat=?")) {
            psr.setString(1,noRawat);
            try (ResultSet rsr=psr.executeQuery()){ if(rsr.next()) idObsRujukan=rsr.getString(1); }
        } catch(Exception er){ System.out.println("Notif baca id_obs_rujukan : "+er); }
        if(modeUpdate || idObsRujukan.equals("")){
            boolean adaRujukan=false;
            try (PreparedStatement psar=koneksi.prepareStatement(
                    "select alasan_kedatangan from data_triase_igd where no_rawat=?")) {
                psar.setString(1,noRawat);
                try (ResultSet rsar=psar.executeQuery()){ if(rsar.next()){ String v=rsar.getString("alasan_kedatangan"); adaRujukan = v!=null && v.trim().equalsIgnoreCase("Rujukan"); } }
            } catch(Exception ear){ System.out.println("Notif ambil alasan_kedatangan : "+ear); }
            String body="{"+
                "\"resourceType\":\"Observation\","+
                "\"status\":\"final\","+
                "\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/observation-category\",\"code\":\"exam\",\"display\":\"Exam\"}]}],"+
                "\"code\":{\"coding\":[{\"system\":\"http://terminology.kemkes.go.id/CodeSystem/clinical-term\",\"code\":\"OC000034\",\"display\":\"Surat Pengantar Rujukan\"}]},"+
                "\"subject\":{\"reference\":\"Patient/"+idP+"\",\"display\":\""+jsonEscape(nmPasien)+"\"},"+
                "\"encounter\":{\"reference\":\"Encounter/"+idEnc+"\"},"+
                "\"performer\":[{\"reference\":\"Practitioner/"+idD+"\"}],"+
                "\"effectiveDateTime\":\""+effective+"\","+
                "\"valueBoolean\":"+adaRujukan+
            "}";
            idObsRujukan = kirimResource("Observation", idObsRujukan, body);
        }

        // 1d. Kondisi Pasien Tiba / CTAS (Observation 75910-0, valueCodeableConcept)
        // dipetakan dari warna triase: primer(red)->CTAS 1, Kuning->3, Hijau->5.
        String idObsCtas="";
        try (PreparedStatement psc2=koneksi.prepareStatement(
                "select ifnull(id_obs_ctas,'') from satu_sehat_triase_igd where no_rawat=?")) {
            psc2.setString(1,noRawat);
            try (ResultSet rsc2=psc2.executeQuery()){ if(rsc2.next()) idObsCtas=rsc2.getString(1); }
        } catch(Exception ec){ System.out.println("Notif baca id_obs_ctas : "+ec); }
        if(modeUpdate || idObsCtas.equals("")){
            String cCode="", cDisp="";
            if(source.equals("primer")){ cCode="LA6112-2"; cDisp="1 - Resusitasi"; }
            else if(source.equals("sekunder")){
                if(plan.equalsIgnoreCase("Zona Kuning")){ cCode="LA6114-8"; cDisp="3 - Urgent"; }
                else if(plan.equalsIgnoreCase("Zona Hijau")){ cCode="LA10137-0"; cDisp="5 - Non Urgent"; }
            }
            if(!cCode.equals("")){
                String body="{"+
                    "\"resourceType\":\"Observation\","+
                    "\"status\":\"final\","+
                    "\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/observation-category\",\"code\":\"survey\",\"display\":\"Survey\"}]}],"+
                    "\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"75910-0\",\"display\":\"Canadian triage and acuity scale [CTAS]\"}]},"+
                    "\"subject\":{\"reference\":\"Patient/"+idP+"\",\"display\":\""+jsonEscape(nmPasien)+"\"},"+
                    "\"encounter\":{\"reference\":\"Encounter/"+idEnc+"\"},"+
                    "\"performer\":[{\"reference\":\"Practitioner/"+idD+"\"}],"+
                    "\"effectiveDateTime\":\""+effective+"\","+
                    "\"valueCodeableConcept\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\""+cCode+"\",\"display\":\""+cDisp+"\"}]}"+
                "}";
                idObsCtas = kirimResource("Observation", idObsCtas, body);
            }
        }

        // 2. Kategori Triase
        String idObsKat=tbObat.getValueAt(row,23).toString();
        if((modeUpdate || idObsKat.equals(""))){
            String katCode="", katDisplay="";
            if(source.equals("primer")){
                katCode="371240000"; katDisplay="Triage finding, immediate (red)";
            } else if(source.equals("sekunder")){
                if(plan.equalsIgnoreCase("Zona Kuning")){
                    katCode="50607009"; katDisplay="Triage finding, urgent (yellow)";
                } else if(plan.equalsIgnoreCase("Zona Hijau")){
                    katCode="371923003"; katDisplay="Triage finding, non-urgent (green)";
                }
            }
            if(!katCode.equals("")){
                String body = "{"+
                    "\"resourceType\":\"Observation\","+
                    "\"status\":\"final\","+
                    "\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/observation-category\",\"code\":\"survey\",\"display\":\"Survey\"}]}],"+
                    "\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"273887006\",\"display\":\"Triage index (assessment scale)\"}]},"+
                    "\"subject\":{\"reference\":\"Patient/"+idP+"\",\"display\":\""+jsonEscape(nmPasien)+"\"},"+
                    "\"encounter\":{\"reference\":\"Encounter/"+idEnc+"\"},"+
                    "\"performer\":[{\"reference\":\"Practitioner/"+idD+"\"}],"+
                    "\"effectiveDateTime\":\""+effective+"\","+
                    "\"valueCodeableConcept\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\""+katCode+"\",\"display\":\""+katDisplay+"\"}]}"+
                "}";
                idObsKat = kirimResource("Observation", idObsKat, body);
                if(!idObsKat.equals("")) tbObat.setValueAt(idObsKat, row, 23);
            }
        }

        // 3a. Tekanan Darah (panel + components)
        String idObsTD=tbObat.getValueAt(row,24).toString();
        if((modeUpdate || idObsTD.equals("")) && td!=null && td.contains("/")){
            String[] tdSplit = td.split("/");
            String sistole = parseNum(tdSplit[0]);
            String diastole = tdSplit.length>1 ? parseNum(tdSplit[1]) : "";
            if(!sistole.equals("") && !diastole.equals("")){
                String body = "{"+
                    "\"resourceType\":\"Observation\","+
                    "\"status\":\"final\","+
                    "\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/observation-category\",\"code\":\"vital-signs\",\"display\":\"Vital Signs\"}]}],"+
                    "\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"85354-9\",\"display\":\"Blood pressure panel with all children optional\"}]},"+
                    "\"subject\":{\"reference\":\"Patient/"+idP+"\"},"+
                    "\"encounter\":{\"reference\":\"Encounter/"+idEnc+"\"},"+
                    "\"performer\":[{\"reference\":\"Practitioner/"+idD+"\"}],"+
                    "\"effectiveDateTime\":\""+effective+"\","+
                    "\"component\":["+
                        "{\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"8480-6\",\"display\":\"Systolic blood pressure\"}]},"+
                         "\"valueQuantity\":{\"value\":"+sistole+",\"unit\":\"mmHg\",\"system\":\"http://unitsofmeasure.org\",\"code\":\"mm[Hg]\"}},"+
                        "{\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"8462-4\",\"display\":\"Diastolic blood pressure\"}]},"+
                         "\"valueQuantity\":{\"value\":"+diastole+",\"unit\":\"mmHg\",\"system\":\"http://unitsofmeasure.org\",\"code\":\"mm[Hg]\"}}"+
                    "]"+
                "}";
                idObsTD = kirimResource("Observation", idObsTD, body);
                if(!idObsTD.equals("")) tbObat.setValueAt(idObsTD, row, 24);
            }
        }

        // 3b. Nadi
        String idObsNadi=tbObat.getValueAt(row,25).toString();
        if((modeUpdate || idObsNadi.equals(""))){
            String v = parseNum(nadi);
            if(!v.equals("")){
                String body = buildObsQuantity("8867-4","Heart rate",idP,idEnc,idD,effective,v,"/min","/min","vital-signs");
                idObsNadi = kirimResource("Observation", idObsNadi, body);
                if(!idObsNadi.equals("")) tbObat.setValueAt(idObsNadi, row, 25);
            }
        }

        // 3c. Respiratory rate
        String idObsResp=tbObat.getValueAt(row,26).toString();
        if((modeUpdate || idObsResp.equals(""))){
            String v = parseNum(resp);
            if(!v.equals("")){
                String body = buildObsQuantity("9279-1","Respiratory rate",idP,idEnc,idD,effective,v,"/min","/min","vital-signs");
                idObsResp = kirimResource("Observation", idObsResp, body);
                if(!idObsResp.equals("")) tbObat.setValueAt(idObsResp, row, 26);
            }
        }

        // 3d. Suhu
        String idObsSuhu=tbObat.getValueAt(row,27).toString();
        if((modeUpdate || idObsSuhu.equals(""))){
            String v = parseNum(suhu);
            if(!v.equals("")){
                String body = buildObsQuantity("8310-5","Body temperature",idP,idEnc,idD,effective,v,"Cel","Cel","vital-signs");
                idObsSuhu = kirimResource("Observation", idObsSuhu, body);
                if(!idObsSuhu.equals("")) tbObat.setValueAt(idObsSuhu, row, 27);
            }
        }

        // 3e. SpO2
        String idObsSpO2=tbObat.getValueAt(row,28).toString();
        if((modeUpdate || idObsSpO2.equals(""))){
            String v = parseNum(spo2);
            if(!v.equals("")){
                String body = buildObsQuantity("2708-6","Oxygen saturation in Arterial blood",idP,idEnc,idD,effective,v,"%","%","vital-signs");
                idObsSpO2 = kirimResource("Observation", idObsSpO2, body);
                if(!idObsSpO2.equals("")) tbObat.setValueAt(idObsSpO2, row, 28);
            }
        }

        // 3f. Nyeri (Pain severity 0-10)
        String idObsNyeri=tbObat.getValueAt(row,29).toString();
        if((modeUpdate || idObsNyeri.equals(""))){
            String v = parseNum(nyeri);
            if(!v.equals("")){
                String body = buildObsQuantity("38208-5","Pain severity - 0-10 verbal numeric rating [Score] - Reported",idP,idEnc,idD,effective,v,"{score}","{score}","survey");
                idObsNyeri = kirimResource("Observation", idObsNyeri, body);
                if(!idObsNyeri.equals("")) tbObat.setValueAt(idObsNyeri, row, 29);
            }
        }

        // 4. ED disposition
        String idObsDispo=tbObat.getValueAt(row,30).toString();
        if((modeUpdate || idObsDispo.equals("")) && !plan.equals("")){
            String body = "{"+
                "\"resourceType\":\"Observation\","+
                "\"status\":\"final\","+
                "\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/observation-category\",\"code\":\"exam\",\"display\":\"Exam\"}]}],"+
                "\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"11302-7\",\"display\":\"ED disposition\"}]},"+
                "\"subject\":{\"reference\":\"Patient/"+idP+"\"},"+
                "\"encounter\":{\"reference\":\"Encounter/"+idEnc+"\"},"+
                "\"performer\":[{\"reference\":\"Practitioner/"+idD+"\"}],"+
                "\"effectiveDateTime\":\""+effective+"\","+
                "\"valueString\":\""+jsonEscape(plan)+"\""+
            "}";
            idObsDispo = kirimResource("Observation", idObsDispo, body);
            if(!idObsDispo.equals("")) tbObat.setValueAt(idObsDispo, row, 30);
        }

        // 4b. Penilaian ABCDE (satu Observation, satu component per pemeriksaan)
        String idObsAbcd=tbObat.getValueAt(row,33).toString();
        if((modeUpdate || idObsAbcd.equals(""))){
            java.util.Map<String,String> temuan = ambilTemuanAbcd(noRawat);
            StringBuilder komponen=new StringBuilder();
            for(String[] k : KOMPONEN_ABCD){
                String teks = temuan.get(k[0]);
                if(teks==null || teks.isEmpty()) continue;
                if(komponen.length()>0) komponen.append(",");
                komponen.append("{\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"").append(k[1])
                        .append("\",\"display\":\"").append(k[2]).append("\"}]},")
                        .append("\"valueString\":\"").append(jsonEscape(teks)).append("\"}");
            }
            if(komponen.length()>0){
                String body = "{"+
                    "\"resourceType\":\"Observation\","+
                    "\"status\":\"final\","+
                    "\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/observation-category\",\"code\":\"exam\",\"display\":\"Exam\"}]}],"+
                    "\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"51848-0\",\"display\":\"Assessments\"}],\"text\":\"Penilaian ABCDE (Triase IGD)\"},"+
                    "\"subject\":{\"reference\":\"Patient/"+idP+"\",\"display\":\""+jsonEscape(nmPasien)+"\"},"+
                    "\"encounter\":{\"reference\":\"Encounter/"+idEnc+"\"},"+
                    "\"performer\":[{\"reference\":\"Practitioner/"+idD+"\"}],"+
                    "\"effectiveDateTime\":\""+effective+"\","+
                    "\"component\":["+komponen+"]"+
                "}";
                idObsAbcd = kirimResource("Observation", idObsAbcd, body);
                if(!idObsAbcd.equals("")) tbObat.setValueAt(idObsAbcd, row, 33);
            }
        }

        // 4c. ClinicalImpression (riwayat perjalanan penyakit) -- hanya triase sekunder.
        //
        // Pada triase sekunder, kolom "keluhan" berisi data_triase_igdsekunder.anamnesa_singkat
        // yang isinya narasi perjalanan penyakit ("DEMAM 2 HARI, NYERI ULU HATI"), bukan
        // keluhan utama. Narasi itu rumahnya ClinicalImpression. Condition tetap dikirim
        // karena triase sekunder tidak punya field keluhan utama terpisah, jadi kalau
        // Condition dilepas seksi Keluhan Utama hilang sama sekali.
        //
        // Triase primer memakai keluhan_utama yang memang keluhan utama, jadi tidak
        // menghasilkan ClinicalImpression.
        String idClinImp=tbObat.getValueAt(row,34).toString();
        if((modeUpdate || idClinImp.equals("")) && source.equals("sekunder") && !keluhan.equals("") && !keluhan.equals("-")){
            // ClinicalImpression.code sengaja dikosongkan: SatuSehat menolak LOINC di
            // elemen ini (RuleNumber 10845), dan maknanya sudah dibawa kode seksi
            // Composition 10164-2. Lebih baik tanpa code daripada menegaskan konsep
            // SNOMED yang artinya tidak bisa dipastikan.
            String body = "{"+
                "\"resourceType\":\"ClinicalImpression\","+
                "\"status\":\"completed\","+
                "\"description\":\"Riwayat perjalanan penyakit saat triase IGD\","+
                "\"subject\":{\"reference\":\"Patient/"+idP+"\",\"display\":\""+jsonEscape(nmPasien)+"\"},"+
                "\"encounter\":{\"reference\":\"Encounter/"+idEnc+"\"},"+
                "\"effectiveDateTime\":\""+effective+"\","+
                "\"date\":\""+effective+"\","+
                "\"assessor\":{\"reference\":\"Practitioner/"+idD+"\"},"+
                "\"summary\":\""+jsonEscape(keluhan)+"\""+
            "}";
            idClinImp = kirimResource("ClinicalImpression", idClinImp, body);
            if(!idClinImp.equals("")) tbObat.setValueAt(idClinImp, row, 34);
        }

        // 4d. Kondisi Pasien Tiba (seksi Identitas & Kedatangan pada panduan).
        //
        // Diambil langsung dari DB, bukan lewat kolom tabel: nilainya cuma dipakai
        // di sini, jadi tidak perlu menambah kolom pada model tabel.
        String idObsKondisi=tbObat.getValueAt(row,35).toString();
        if((modeUpdate || idObsKondisi.equals(""))){
            String kondisi="";
            try (PreparedStatement psk=koneksi.prepareStatement(
                    "select kondisi_tiba from data_triase_igd where no_rawat=?")) {
                psk.setString(1,noRawat);
                try (ResultSet rsk=psk.executeQuery()) {
                    if(rsk.next()){
                        String v=rsk.getString("kondisi_tiba");
                        if(v!=null) kondisi=v.trim();
                    }
                }
            } catch(Exception ek){ System.out.println("Notif ambil kondisi tiba : "+ek); }

            if(!kondisi.isEmpty() && !kondisi.equals("-")){
                String body = "{"+
                    "\"resourceType\":\"Observation\","+
                    "\"status\":\"final\","+
                    "\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/observation-category\",\"code\":\"exam\",\"display\":\"Exam\"}]}],"+
                    "\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"67775-7\",\"display\":\"Patient condition\"}],\"text\":\"Kondisi Pasien Tiba\"},"+
                    "\"subject\":{\"reference\":\"Patient/"+idP+"\",\"display\":\""+jsonEscape(nmPasien)+"\"},"+
                    "\"encounter\":{\"reference\":\"Encounter/"+idEnc+"\"},"+
                    "\"performer\":[{\"reference\":\"Practitioner/"+idD+"\"}],"+
                    "\"effectiveDateTime\":\""+effective+"\","+
                    "\"valueString\":\""+jsonEscape(kondisi)+"\""+
                "}";
                idObsKondisi = kirimResource("Observation", idObsKondisi, body);
                if(!idObsKondisi.equals("")) tbObat.setValueAt(idObsKondisi, row, 35);
            }
        }

        // 4e. Procedure "Triage" (SNOMED 225390008) -- komponen #9 Triage note yang
        // sebelumnya belum dikirim (dashboard: "Tindakan Medis: Tidak ada data").
        // Tak punya kolom di model tabel; id-nya dibaca/ditulis langsung ke penjejak.
        String idProcedure="";
        try (PreparedStatement psp=koneksi.prepareStatement(
                "select ifnull(id_procedure,'') from satu_sehat_triase_igd where no_rawat=?")) {
            psp.setString(1,noRawat);
            try (ResultSet rsp=psp.executeQuery()) { if(rsp.next()) idProcedure=rsp.getString(1); }
        } catch(Exception ep){ System.out.println("Notif baca id_procedure : "+ep); }
        if(modeUpdate || idProcedure.equals("")){
            String body = "{"+
                "\"resourceType\":\"Procedure\","+
                "\"status\":\"completed\","+
                "\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"225390008\",\"display\":\"Triage\"}],\"text\":\"Prosedur Triase\"},"+
                "\"subject\":{\"reference\":\"Patient/"+idP+"\",\"display\":\""+jsonEscape(nmPasien)+"\"},"+
                "\"encounter\":{\"reference\":\"Encounter/"+idEnc+"\"},"+
                "\"performedDateTime\":\""+effective+"\","+
                "\"performer\":[{\"actor\":{\"reference\":\"Practitioner/"+idD+"\"}}]"+
            "}";
            idProcedure = kirimResource("Procedure", idProcedure, body);
        }

        // 5. Condition (chief complaint)
        String idCondition=tbObat.getValueAt(row,31).toString();
        if((modeUpdate || idCondition.equals("")) && !keluhan.equals("")){
            String body = "{"+
                "\"resourceType\":\"Condition\","+
                "\"clinicalStatus\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-clinical\",\"code\":\"active\",\"display\":\"Active\"}]},"+
                "\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-category\",\"code\":\"encounter-diagnosis\",\"display\":\"Encounter Diagnosis\"}],\"text\":\"Chief Complaint\"}],"+
                "\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"404684003\",\"display\":\"Clinical finding\"}],\"text\":\""+jsonEscape(keluhan)+"\"},"+
                "\"subject\":{\"reference\":\"Patient/"+idP+"\",\"display\":\""+jsonEscape(nmPasien)+"\"},"+
                "\"encounter\":{\"reference\":\"Encounter/"+idEnc+"\"},"+
                "\"recordedDate\":\""+effective+"\""+
            "}";
            idCondition = kirimResource("Condition", idCondition, body);
            if(!idCondition.equals("")) tbObat.setValueAt(idCondition, row, 31);
        }

        // 6. Composition referencing the above
        String idComposition=tbObat.getValueAt(row,32).toString();
        if((modeUpdate || idComposition.equals(""))){
            StringBuilder sb=new StringBuilder();
            sb.append("{\"resourceType\":\"Composition\",");
            sb.append("\"identifier\":{\"system\":\"http://sys-ids.kemkes.go.id/composition/").append(koneksiDB.IDSATUSEHAT()).append("\",\"value\":\"triage-").append(noRawat.replaceAll("/","")).append("\"},");
            sb.append("\"status\":\"final\",");
            sb.append("\"type\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"75500-9\",\"display\":\"Triage note\"}]},");
            sb.append("\"category\":[{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"LP173421-1\",\"display\":\"Report\"}]}],");
            sb.append("\"subject\":{\"reference\":\"Patient/").append(idP).append("\",\"display\":\"").append(jsonEscape(nmPasien)).append("\"},");
            sb.append("\"encounter\":{\"reference\":\"Encounter/").append(idEnc).append("\"},");
            sb.append("\"date\":\"").append(effective).append("\",");
            sb.append("\"author\":[{\"reference\":\"Practitioner/").append(idD).append("\"}],");
            sb.append("\"title\":\"Triage Note IGD\",");
            sb.append("\"custodian\":{\"reference\":\"Organization/").append(koneksiDB.IDSATUSEHAT()).append("\"},");
            sb.append("\"section\":[");

            boolean firstSec=true;

            // Section: Mode of arrival
            if(!idObsMode.equals("")){
                if(!firstSec) sb.append(","); firstSec=false;
                sb.append("{\"title\":\"Mode of arrival\",\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"11459-5\",\"display\":\"Mode of arrival\"}]},");
                sb.append("\"entry\":[{\"reference\":\"Observation/").append(idObsMode).append("\"}]}");
            }

            // Section: Sarana Transportasi Kedatangan
            if(!idObsTransport.equals("")){
                if(!firstSec) sb.append(","); firstSec=false;
                sb.append("{\"title\":\"Sarana Transportasi Kedatangan\",\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"74286-6\",\"display\":\"Transport to hospital\"}]},");
                sb.append("\"entry\":[{\"reference\":\"Observation/").append(idObsTransport).append("\"}]}");
            }

            // Section: Vital signs (panel + members)
            StringBuilder vsEntries=new StringBuilder();
            if(!idObsTD.equals(""))   { if(vsEntries.length()>0) vsEntries.append(","); vsEntries.append("{\"reference\":\"Observation/").append(idObsTD).append("\"}"); }
            if(!idObsNadi.equals("")) { if(vsEntries.length()>0) vsEntries.append(","); vsEntries.append("{\"reference\":\"Observation/").append(idObsNadi).append("\"}"); }
            if(!idObsResp.equals("")) { if(vsEntries.length()>0) vsEntries.append(","); vsEntries.append("{\"reference\":\"Observation/").append(idObsResp).append("\"}"); }
            if(!idObsSuhu.equals("")) { if(vsEntries.length()>0) vsEntries.append(","); vsEntries.append("{\"reference\":\"Observation/").append(idObsSuhu).append("\"}"); }
            if(!idObsSpO2.equals("")) { if(vsEntries.length()>0) vsEntries.append(","); vsEntries.append("{\"reference\":\"Observation/").append(idObsSpO2).append("\"}"); }
            if(!idObsNyeri.equals("")){ if(vsEntries.length()>0) vsEntries.append(","); vsEntries.append("{\"reference\":\"Observation/").append(idObsNyeri).append("\"}"); }
            if(vsEntries.length()>0){
                if(!firstSec) sb.append(","); firstSec=false;
                sb.append("{\"title\":\"Vital signs\",\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"8716-3\",\"display\":\"Vital signs\"}]},");
                sb.append("\"entry\":[").append(vsEntries).append("]}");
            }

            // Section: Acuity assessment
            if(!idObsKat.equals("")){
                if(!firstSec) sb.append(","); firstSec=false;
                sb.append("{\"title\":\"Acuity assessment\",\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"11283-9\",\"display\":\"Acuity assessment\"}]},");
                sb.append("\"entry\":[{\"reference\":\"Observation/").append(idObsKat).append("\"}]}");
            }

            // Section: Penilaian ABCDE
            if(!idObsAbcd.equals("")){
                if(!firstSec) sb.append(","); firstSec=false;
                sb.append("{\"title\":\"Penilaian ABCDE\",\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"51848-0\",\"display\":\"Assessments\"}]},");
                sb.append("\"entry\":[{\"reference\":\"Observation/").append(idObsAbcd).append("\"}]}");
            }

            // Section: Chief complaint
            if(!idCondition.equals("")){
                if(!firstSec) sb.append(","); firstSec=false;
                sb.append("{\"title\":\"Chief complaint\",\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"10154-3\",\"display\":\"Chief complaint Narrative\"}]},");
                sb.append("\"entry\":[{\"reference\":\"Condition/").append(idCondition).append("\"}]}");
            }

            // Section: Kondisi Pasien Tiba
            if(!idObsKondisi.equals("")){
                if(!firstSec) sb.append(","); firstSec=false;
                sb.append("{\"title\":\"Kondisi Pasien Tiba\",\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"67775-7\",\"display\":\"Patient condition\"}]},");
                sb.append("\"entry\":[{\"reference\":\"Observation/").append(idObsKondisi).append("\"}]}");
            }

            // Section: Riwayat Perjalanan Penyakit
            if(!idClinImp.equals("")){
                if(!firstSec) sb.append(","); firstSec=false;
                sb.append("{\"title\":\"Riwayat Perjalanan Penyakit\",\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"10164-2\",\"display\":\"History of Present illness Narrative\"}]},");
                sb.append("\"entry\":[{\"reference\":\"ClinicalImpression/").append(idClinImp).append("\"}]}");
            }

            // Section: ED disposition
            if(!idObsDispo.equals("")){
                if(!firstSec) sb.append(","); firstSec=false;
                sb.append("{\"title\":\"ED disposition\",\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"11302-7\",\"display\":\"ED disposition\"}]},");
                sb.append("\"entry\":[{\"reference\":\"Observation/").append(idObsDispo).append("\"}]}");
            }

            // Section: Prosedur Triase (Procedure 225390008)
            if(!idProcedure.equals("")){
                if(!firstSec) sb.append(","); firstSec=false;
                sb.append("{\"title\":\"Prosedur Triase\",\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"47519-4\",\"display\":\"History of Procedures\"}]},");
                sb.append("\"entry\":[{\"reference\":\"Procedure/").append(idProcedure).append("\"}]}");
            }

            sb.append("]}");

            // Only POST Composition if there is at least one section entry
            if(!firstSec){
                idComposition = kirimResource("Composition", idComposition, sb.toString());
                if(!idComposition.equals("")){
                    tbObat.setValueAt(idComposition, row, 32);
                    tbObat.setValueAt(false, row, 0);
                }
            }

        }

        // Persist IHS IDs ke satu_sehat_triase_igd (REPLACE supaya bisa kirim ulang).
        //
        // Sengaja DI LUAR blok Composition di atas. Blok ABCD dan ClinicalImpression
        // berjalan meski Composition sudah pernah dibuat -- kalau penyimpanan ikut
        // terkurung di dalamnya, kunjungan lama yang sudah punya Composition akan
        // mengirim ulang kedua resource itu setiap kali tombol ditekan tanpa pernah
        // menyimpan ID-nya, sehingga menumpuk duplikat di SatuSehat.
        {
            String tglKirim = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            String sqlSimpan = "replace into satu_sehat_triase_igd (no_rawat,id_obs_modearrival,id_obs_transport,id_obs_kategori,id_obs_td,id_obs_nadi,id_obs_resp,id_obs_suhu,id_obs_spo2,id_obs_nyeri,id_obs_disposition,id_obs_abcd,id_condition,id_composition,id_clinicalimpression,id_obs_kondisitiba,id_procedure,id_obs_rujukan,id_obs_ctas,tgl_kirim) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            try {
                PreparedStatement psSimpan = koneksi.prepareStatement(sqlSimpan);
                psSimpan.setString(1, tbObat.getValueAt(row,1).toString());
                psSimpan.setString(2, idObsMode);
                psSimpan.setString(3, idObsTransport);
                psSimpan.setString(4, idObsKat);
                psSimpan.setString(5, idObsTD);
                psSimpan.setString(6, idObsNadi);
                psSimpan.setString(7, idObsResp);
                psSimpan.setString(8, idObsSuhu);
                psSimpan.setString(9, idObsSpO2);
                psSimpan.setString(10, idObsNyeri);
                psSimpan.setString(11, idObsDispo);
                psSimpan.setString(12, idObsAbcd);
                psSimpan.setString(13, idCondition);
                psSimpan.setString(14, idComposition);
                psSimpan.setString(15, idClinImp);
                psSimpan.setString(16, idObsKondisi);
                psSimpan.setString(17, idProcedure);
                psSimpan.setString(18, idObsRujukan);
                psSimpan.setString(19, idObsCtas);
                psSimpan.setString(20, tglKirim);
                psSimpan.executeUpdate();
                psSimpan.close();
            } catch (Exception eSimpan) {
                System.out.println("Notif simpan tracking : "+eSimpan);
            }
        }
    }

    /**
     * Update Triase IGD untuk 1 no_rawat: PUT ulang resource yang sudah punya id
     * dengan isian triase terbaru, dan mengirim yang belum ada (mis. vital sign
     * yang baru dilengkapi setelah kiriman pertama). Return jumlah resource yang
     * terkirim/diperbarui.
     */
    public int updateSatuNoRawat(String noRawat) {
        modeUpdate = true;
        try { return kirimSatuNoRawat(noRawat); }
        finally { modeUpdate = false; }
    }

    /** Kirim Triase IGD untuk 1 no_rawat (dipakai form Kirim Bundel). Return jumlah resource terkirim/diperbarui. */
    public int kirimSatuNoRawat(String noRawat) {
        jmlKirimResource = 0;
        int before = countIdTriase(noRawat);
        Valid.tabelKosong(tabMode);
        // Author Triase = PETUGAS triase (dtip.nik/dtis.nik), bukan DPJP kunjungan.
        // Pakai LEFT join pegawai (petugas bisa perawat -> tak ada di tabel dokter);
        // nama diambil dari pegawai.nama. %PTG% diganti dtip.nik / dtis.nik per sumber.
        String selFrom =
            "select reg_periksa.no_rawat, reg_periksa.no_rkm_medis, pasien.nm_pasien, pasien.no_ktp, "+
            "%PTG% as kd_dokter, ifnull(pg.nama,'') as nm_dokter, ifnull(pg.no_ktp,'') as ktpdokter, satu_sehat_encounter.id_encounter, ";
        String tail =
            "dti.tekanan_darah, dti.nadi, dti.pernapasan, dti.suhu, dti.saturasi_o2, dti.nyeri, %SRC% as source, "+
            "ifnull(sst.id_obs_modearrival,'') as id_obs_modearrival, ifnull(sst.id_obs_kategori,'') as id_obs_kategori, "+
            "ifnull(sst.id_obs_td,'') as id_obs_td, ifnull(sst.id_obs_nadi,'') as id_obs_nadi, ifnull(sst.id_obs_resp,'') as id_obs_resp, "+
            "ifnull(sst.id_obs_suhu,'') as id_obs_suhu, ifnull(sst.id_obs_spo2,'') as id_obs_spo2, ifnull(sst.id_obs_nyeri,'') as id_obs_nyeri, "+
            "ifnull(sst.id_obs_disposition,'') as id_obs_disposition, ifnull(sst.id_condition,'') as id_condition, ifnull(sst.id_composition,'') as id_composition, "+
            "ifnull(sst.id_obs_abcd,'') as id_obs_abcd, "+
                "ifnull(sst.id_clinicalimpression,'') as id_clinicalimpression, "+
                "ifnull(sst.id_obs_kondisitiba,'') as id_obs_kondisitiba "+
            "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
            "inner join satu_sehat_encounter on satu_sehat_encounter.no_rawat=reg_periksa.no_rawat "+
            "inner join data_triase_igd dti on dti.no_rawat=reg_periksa.no_rawat "+
            "%JOIN% left join master_triase_macam_kasus mk on mk.kode_kasus=dti.kode_kasus "+
            "left join satu_sehat_triase_igd sst on sst.no_rawat=reg_periksa.no_rawat where reg_periksa.no_rawat=?";
        String[] sqls = {
            (selFrom + "dtip.tanggaltriase, dti.cara_masuk, dti.kode_kasus, ifnull(mk.macam_kasus,'') as macam_kasus, dtip.plan, dtip.keluhan_utama as keluhan, " + tail)
                .replace("%SRC%","'primer'").replace("%PTG%","dtip.nik")
                .replace("%JOIN%","inner join data_triase_igdprimer dtip on dtip.no_rawat=reg_periksa.no_rawat left join pegawai pg on pg.nik=dtip.nik "),
            (selFrom + "dtis.tanggaltriase, dti.cara_masuk, dti.kode_kasus, ifnull(mk.macam_kasus,'') as macam_kasus, dtis.plan, dtis.anamnesa_singkat as keluhan, " + tail)
                .replace("%SRC%","'sekunder'").replace("%PTG%","dtis.nik")
                .replace("%JOIN%","inner join data_triase_igdsekunder dtis on dtis.no_rawat=reg_periksa.no_rawat left join pegawai pg on pg.nik=dtis.nik ")
        };
        for (String sql : sqls) {
            try (PreparedStatement p = koneksi.prepareStatement(sql)) {
                p.setString(1, noRawat);
                ResultSet r = p.executeQuery();
                while (r.next()) {
                    tabMode.addRow(new Object[]{ false,
                        r.getString("no_rawat"), r.getString("no_rkm_medis"), r.getString("nm_pasien"), r.getString("no_ktp"),
                        r.getString("kd_dokter"), r.getString("nm_dokter"), r.getString("ktpdokter"),
                        r.getString("id_encounter"), r.getString("tanggaltriase"),
                        r.getString("cara_masuk"), r.getString("kode_kasus"), r.getString("macam_kasus"),
                        r.getString("plan"), r.getString("keluhan"),
                        r.getString("tekanan_darah"), r.getString("nadi"), r.getString("pernapasan"), r.getString("suhu"), r.getString("saturasi_o2"), r.getString("nyeri"),
                        r.getString("source"),
                        r.getString("id_obs_modearrival"), r.getString("id_obs_kategori"),
                        r.getString("id_obs_td"), r.getString("id_obs_nadi"), r.getString("id_obs_resp"),
                        r.getString("id_obs_suhu"), r.getString("id_obs_spo2"), r.getString("id_obs_nyeri"),
                        r.getString("id_obs_disposition"), r.getString("id_condition"), r.getString("id_composition"),
                        r.getString("id_obs_abcd"), r.getString("id_clinicalimpression"), r.getString("id_obs_kondisitiba") });
                }
                r.close();
            } catch (Exception e) { System.out.println("Notif bundel triase tampil : " + e); }
        }
        for (int row = 0; row < tbObat.getRowCount(); row++) {
            String nik = tbObat.getValueAt(row, 4).toString(), ktpDok = tbObat.getValueAt(row, 7).toString();
            if (tbObat.getValueAt(row, 8).toString().isEmpty()) continue; // Encounter wajib
            String idP = cekViaSatuSehat.tampilIDPasien(nik);
            if (idP == null || idP.isEmpty()) continue;
            String idD = ktpDok.isEmpty() ? "" : cekViaSatuSehat.tampilIDParktisi(ktpDok);
            if (idD == null) idD = "";
            try { kirimSatuRow(row, idP, idD); } catch (Exception e) { System.out.println("Notif bundel triase kirim : " + e); }
        }
        // Mode update tidak menambah id baru, jadi selisih jumlah id selalu 0 —
        // pakai penghitung panggilan yang benar-benar berhasil.
        return modeUpdate ? jmlKirimResource : countIdTriase(noRawat) - before;
    }

    private int countIdTriase(String noRawat) {
        try (PreparedStatement p = koneksi.prepareStatement(
                "select id_obs_modearrival,id_obs_kategori,id_obs_td,id_obs_nadi,id_obs_resp,id_obs_suhu,id_obs_spo2,id_obs_nyeri,id_obs_disposition,id_obs_abcd,id_condition,id_composition,id_clinicalimpression,id_obs_kondisitiba from satu_sehat_triase_igd where no_rawat=?")) {
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            int c = 0;
            // Jumlah kolom diambil dari metadata, bukan angka tetap: dulu 11 dan sempat
            // tertinggal saat kolom id_composition/id_clinicalimpression/id_obs_kondisitiba
            // ditambahkan, sehingga form Bundel melaporkan lebih sedikit dari yang terkirim.
            if (r.next()) {
                int jml = r.getMetaData().getColumnCount();
                for (int k = 1; k <= jml; k++) { String v = r.getString(k); if (v != null && !v.isEmpty()) c++; }
            }
            r.close();
            return c;
        } catch (Exception e) { return 0; }
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            SatuSehatKirimTriaseIGD dialog = new SatuSehatKirimTriaseIGD(new javax.swing.JFrame(), true);
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
    private javax.swing.JPopupMenu jPopupMenu1;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private javax.swing.JMenuItem ppBersihkan;
    private javax.swing.JMenuItem ppPilihSemua;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try{
            // Primer (Resusitasi/Kritis) - Red
            String sqlPrimer =
                "select reg_periksa.no_rawat, reg_periksa.no_rkm_medis, pasien.nm_pasien, pasien.no_ktp, "+
                "dtip.nik as kd_dokter, ifnull(pg.nama,'') as nm_dokter, ifnull(pg.no_ktp,'') as ktpdokter, "+
                "satu_sehat_encounter.id_encounter, dtip.tanggaltriase, dti.cara_masuk, dti.kode_kasus, "+
                "ifnull(mk.macam_kasus,'') as macam_kasus, dtip.plan, dtip.keluhan_utama as keluhan, "+
                "dti.tekanan_darah, dti.nadi, dti.pernapasan, dti.suhu, dti.saturasi_o2, dti.nyeri, "+
                "'primer' as source, "+
                "ifnull(sst.id_obs_modearrival,'') as id_obs_modearrival, "+
                "ifnull(sst.id_obs_kategori,'') as id_obs_kategori, "+
                "ifnull(sst.id_obs_td,'') as id_obs_td, "+
                "ifnull(sst.id_obs_nadi,'') as id_obs_nadi, "+
                "ifnull(sst.id_obs_resp,'') as id_obs_resp, "+
                "ifnull(sst.id_obs_suhu,'') as id_obs_suhu, "+
                "ifnull(sst.id_obs_spo2,'') as id_obs_spo2, "+
                "ifnull(sst.id_obs_nyeri,'') as id_obs_nyeri, "+
                "ifnull(sst.id_obs_disposition,'') as id_obs_disposition, "+
                "ifnull(sst.id_condition,'') as id_condition, "+
                "ifnull(sst.id_composition,'') as id_composition, "+
                "ifnull(sst.id_obs_abcd,'') as id_obs_abcd, "+
                "ifnull(sst.id_clinicalimpression,'') as id_clinicalimpression, "+
                "ifnull(sst.id_obs_kondisitiba,'') as id_obs_kondisitiba "+
                "from reg_periksa "+
                "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                "inner join satu_sehat_encounter on satu_sehat_encounter.no_rawat=reg_periksa.no_rawat "+
                "inner join data_triase_igd dti on dti.no_rawat=reg_periksa.no_rawat "+
                "inner join data_triase_igdprimer dtip on dtip.no_rawat=reg_periksa.no_rawat "+
                "left join pegawai pg on pg.nik=dtip.nik "+
                "left join master_triase_macam_kasus mk on mk.kode_kasus=dti.kode_kasus "+
                "left join satu_sehat_triase_igd sst on sst.no_rawat=reg_periksa.no_rawat "+
                "where dti.tgl_kunjungan between ? and ? "+
                (TCari.getText().equals("")?"":"and (reg_periksa.no_rawat like ? or reg_periksa.no_rkm_medis like ? or "+
                 "pasien.nm_pasien like ? or pasien.no_ktp like ? or pg.nama like ?) ");

            ps=koneksi.prepareStatement(sqlPrimer);
            try {
                ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+""));
                ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+""));
                if(!TCari.getText().equals("")){
                    ps.setString(3,"%"+TCari.getText()+"%");
                    ps.setString(4,"%"+TCari.getText()+"%");
                    ps.setString(5,"%"+TCari.getText()+"%");
                    ps.setString(6,"%"+TCari.getText()+"%");
                    ps.setString(7,"%"+TCari.getText()+"%");
                }
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        false,
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"), rs.getString("no_ktp"),
                        rs.getString("kd_dokter"), rs.getString("nm_dokter"), rs.getString("ktpdokter"),
                        rs.getString("id_encounter"), rs.getString("tanggaltriase"),
                        rs.getString("cara_masuk"), rs.getString("kode_kasus"), rs.getString("macam_kasus"),
                        rs.getString("plan"), rs.getString("keluhan"),
                        rs.getString("tekanan_darah"), rs.getString("nadi"), rs.getString("pernapasan"), rs.getString("suhu"), rs.getString("saturasi_o2"), rs.getString("nyeri"),
                        rs.getString("source"),
                        rs.getString("id_obs_modearrival"), rs.getString("id_obs_kategori"),
                        rs.getString("id_obs_td"), rs.getString("id_obs_nadi"), rs.getString("id_obs_resp"),
                        rs.getString("id_obs_suhu"), rs.getString("id_obs_spo2"), rs.getString("id_obs_nyeri"),
                        rs.getString("id_obs_disposition"), rs.getString("id_condition"), rs.getString("id_composition"),
                        rs.getString("id_obs_abcd"), rs.getString("id_clinicalimpression"), rs.getString("id_obs_kondisitiba")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif : "+e);
            } finally{
                if(rs!=null){ rs.close(); }
                if(ps!=null){ ps.close(); }
            }

            // Sekunder (Kuning/Hijau)
            String sqlSekunder =
                "select reg_periksa.no_rawat, reg_periksa.no_rkm_medis, pasien.nm_pasien, pasien.no_ktp, "+
                "dtis.nik as kd_dokter, ifnull(pg.nama,'') as nm_dokter, ifnull(pg.no_ktp,'') as ktpdokter, "+
                "satu_sehat_encounter.id_encounter, dtis.tanggaltriase, dti.cara_masuk, dti.kode_kasus, "+
                "ifnull(mk.macam_kasus,'') as macam_kasus, dtis.plan, dtis.anamnesa_singkat as keluhan, "+
                "dti.tekanan_darah, dti.nadi, dti.pernapasan, dti.suhu, dti.saturasi_o2, dti.nyeri, "+
                "'sekunder' as source, "+
                "ifnull(sst.id_obs_modearrival,'') as id_obs_modearrival, "+
                "ifnull(sst.id_obs_kategori,'') as id_obs_kategori, "+
                "ifnull(sst.id_obs_td,'') as id_obs_td, "+
                "ifnull(sst.id_obs_nadi,'') as id_obs_nadi, "+
                "ifnull(sst.id_obs_resp,'') as id_obs_resp, "+
                "ifnull(sst.id_obs_suhu,'') as id_obs_suhu, "+
                "ifnull(sst.id_obs_spo2,'') as id_obs_spo2, "+
                "ifnull(sst.id_obs_nyeri,'') as id_obs_nyeri, "+
                "ifnull(sst.id_obs_disposition,'') as id_obs_disposition, "+
                "ifnull(sst.id_condition,'') as id_condition, "+
                "ifnull(sst.id_composition,'') as id_composition, "+
                "ifnull(sst.id_obs_abcd,'') as id_obs_abcd, "+
                "ifnull(sst.id_clinicalimpression,'') as id_clinicalimpression, "+
                "ifnull(sst.id_obs_kondisitiba,'') as id_obs_kondisitiba "+
                "from reg_periksa "+
                "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                "inner join satu_sehat_encounter on satu_sehat_encounter.no_rawat=reg_periksa.no_rawat "+
                "inner join data_triase_igd dti on dti.no_rawat=reg_periksa.no_rawat "+
                "inner join data_triase_igdsekunder dtis on dtis.no_rawat=reg_periksa.no_rawat "+
                "left join pegawai pg on pg.nik=dtis.nik "+
                "left join master_triase_macam_kasus mk on mk.kode_kasus=dti.kode_kasus "+
                "left join satu_sehat_triase_igd sst on sst.no_rawat=reg_periksa.no_rawat "+
                "where dti.tgl_kunjungan between ? and ? "+
                (TCari.getText().equals("")?"":"and (reg_periksa.no_rawat like ? or reg_periksa.no_rkm_medis like ? or "+
                 "pasien.nm_pasien like ? or pasien.no_ktp like ? or pg.nama like ?) ");

            ps=koneksi.prepareStatement(sqlSekunder);
            try {
                ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+""));
                ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+""));
                if(!TCari.getText().equals("")){
                    ps.setString(3,"%"+TCari.getText()+"%");
                    ps.setString(4,"%"+TCari.getText()+"%");
                    ps.setString(5,"%"+TCari.getText()+"%");
                    ps.setString(6,"%"+TCari.getText()+"%");
                    ps.setString(7,"%"+TCari.getText()+"%");
                }
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        false,
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"), rs.getString("no_ktp"),
                        rs.getString("kd_dokter"), rs.getString("nm_dokter"), rs.getString("ktpdokter"),
                        rs.getString("id_encounter"), rs.getString("tanggaltriase"),
                        rs.getString("cara_masuk"), rs.getString("kode_kasus"), rs.getString("macam_kasus"),
                        rs.getString("plan"), rs.getString("keluhan"),
                        rs.getString("tekanan_darah"), rs.getString("nadi"), rs.getString("pernapasan"), rs.getString("suhu"), rs.getString("saturasi_o2"), rs.getString("nyeri"),
                        rs.getString("source"),
                        rs.getString("id_obs_modearrival"), rs.getString("id_obs_kategori"),
                        rs.getString("id_obs_td"), rs.getString("id_obs_nadi"), rs.getString("id_obs_resp"),
                        rs.getString("id_obs_suhu"), rs.getString("id_obs_spo2"), rs.getString("id_obs_nyeri"),
                        rs.getString("id_obs_disposition"), rs.getString("id_condition"), rs.getString("id_composition"),
                        rs.getString("id_obs_abcd"), rs.getString("id_clinicalimpression"), rs.getString("id_obs_kondisitiba")
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif : "+e);
            } finally{
                if(rs!=null){ rs.close(); }
                if(ps!=null){ ps.close(); }
            }
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
        }
        LCount.setText(""+tabMode.getRowCount());
    }

    public void isCek(){
        BtnKirim.setEnabled(akses.getsatu_sehat_kirim_observationttv());
        BtnPrint.setEnabled(akses.getsatu_sehat_kirim_observationttv());
    }

    public JTable getTable(){
        return tbObat;
    }
}
