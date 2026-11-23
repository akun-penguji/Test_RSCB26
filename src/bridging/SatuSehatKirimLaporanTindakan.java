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
public final class SatuSehatKirimLaporanTindakan extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i=0;
    private String link="",json="",idpasien="",idpraktisi="";
    private ApiSatuSehat api=new ApiSatuSehat();
    private HttpHeaders headers;
    private HttpEntity requestEntity;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode response;
    private SatuSehatCekNIK cekViaSatuSehat=new SatuSehatCekNIK();
    private StringBuilder htmlContent;

    /** Creates new form SatuSehatKirimLaporanTindakan
     * @param parent
     * @param modal */
    public SatuSehatKirimLaporanTindakan(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.setLocation(10,2);
        setSize(628,674);

        tabMode=new DefaultTableModel(null,new String[]{
                "P","No.Rawat","No.RM","Nama Pasien","No.KTP Pasien",
                "Kode Dokter","Nama Dokter","No.KTP Dokter","ID Encounter",
                "Tgl.Tindakan","Tindakan Medik","Diagnosa Pra","Diagnosa Pasca",
                "Uraian","Hasil","Kesimpulan","ID Composition"
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

        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 17; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(105);
            }else if(i==2){
                column.setPreferredWidth(70);
            }else if(i==3){
                column.setPreferredWidth(160);
            }else if(i==4){
                column.setPreferredWidth(110);
            }else if(i==5){
                column.setPreferredWidth(85);
            }else if(i==6){
                column.setPreferredWidth(160);
            }else if(i==7){
                column.setPreferredWidth(110);
            }else if(i==8){
                column.setPreferredWidth(210);
            }else if(i==9){
                column.setPreferredWidth(110);
            }else if(i==10){
                column.setPreferredWidth(110);
            }else if(i==11){
                column.setPreferredWidth(150);
            }else if(i==12){
                column.setPreferredWidth(150);
            }else if(i==13){
                column.setPreferredWidth(140);
            }else if(i==14){
                column.setPreferredWidth(85);
            }else if(i==15){
                column.setPreferredWidth(250);
            }else if(i==16){
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

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Pengiriman Data Laporan Tindakan (Composition) Satu Sehat ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
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

        jLabel15.setText("Tgl.Operasi :");
        jLabel15.setName("jLabel15"); // NOI18N
        jLabel15.setPreferredSize(new java.awt.Dimension(85, 23));
        panelGlass9.add(jLabel15);

        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "26-05-2026" }));
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

        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "26-05-2026" }));
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
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Dokter</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Tgl.Operasi</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Selesai</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Dx.Preop</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Dx.Postop</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Jaringan</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>PA</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>ID Composition</b></td>"+
                    "</tr>"
                );
                for (i = 0; i < tabMode.getRowCount(); i++) {
                    htmlContent.append(
                        "<tr class='isi'>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,1).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,2).toString()+"</td>"+
                            "<td valign='top'>"+jsonEscape(tbObat.getValueAt(i,3).toString())+"</td>"+
                            "<td valign='top'>"+jsonEscape(tbObat.getValueAt(i,6).toString())+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,9).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,10).toString()+"</td>"+
                            "<td valign='top'>"+jsonEscape(tbObat.getValueAt(i,11).toString())+"</td>"+
                            "<td valign='top'>"+jsonEscape(tbObat.getValueAt(i,12).toString())+"</td>"+
                            "<td valign='top'>"+jsonEscape(tbObat.getValueAt(i,13).toString())+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,14).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,16).toString()+"</td>"+
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

                File f = new File("DataSatuSehatLaporanOperasi.html");
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                bw.write(LoadHTML.getText().replaceAll("<head>","<head>"+
                            "<link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" />"+
                            "<table width='3000px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                                "<tr class='isi2'>"+
                                    "<td valign='top' align='center'>"+
                                        "<font size='4' face='Tahoma'>"+akses.getnamars()+"</font><br>"+
                                        akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs()+"<br>"+
                                        akses.getkontakrs()+", E-mail : "+akses.getemailrs()+"<br><br>"+
                                        "<font size='2' face='Tahoma'>DATA PENGIRIMAN SATU SEHAT LAPORAN OPERASI<br><br></font>"+
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
                    &&(!tbObat.getValueAt(i,8).toString().equals(""))
                    && tbObat.getValueAt(i,16).toString().equals("")){
                try {
                    idpasien=cekViaSatuSehat.tampilIDPasien(tbObat.getValueAt(i,4).toString());
                    if(idpasien==null||idpasien.equals("")){
                        continue;
                    }
                    String ktpDokter = tbObat.getValueAt(i,7).toString();
                    idpraktisi = "";
                    if(ktpDokter!=null && !ktpDokter.equals("")){
                        idpraktisi = cekViaSatuSehat.tampilIDParktisi(ktpDokter);
                        if(idpraktisi==null){ idpraktisi=""; }
                    }
                    kirimSatuRow(i,idpasien,idpraktisi);
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

    /** HTML-escape untuk konten Narrative.div agar XHTML valid. */
    private static String htmlEscape(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\n","<br/>");
    }

    /** Convert "yyyy-MM-dd HH:mm:ss" or "yyyy-MM-dd" to FHIR ISO UTC +00:00 via IsoTime helper. */
    private static String toIso(String tgl) {
        if (tgl == null) return "";
        String t = tgl.trim();
        if (t.equals("") || t.startsWith("0000-00-00")) return "";
        if (t.length() < 10) return "";
        if (t.length() == 10) return fungsi.IsoTime.toUtc(t, "00:00:00");
        return fungsi.IsoTime.toUtc(t);
    }

    /** Strip slashes/colons/spaces untuk identifier value yang URL-safe. */
    private static String cleanId(String s){
        if(s==null) return "";
        return s.replace("/","").replace(":","").replace(" ","").replace("-","");
    }

    /** POST a resource to SatuSehat FHIR endpoint, return id from response or "" on failure. */
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

    /** PUT (update) resource yang sudah ada. body wajib memuat "id" yang sama. */
    private String putResource(String resourceType, String id, String body) {
        try{
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
            System.out.println("URL : "+link+"/"+resourceType+"/"+id);
            System.out.println("Request JSON : "+body);
            requestEntity = new HttpEntity(body,headers);
            String resp = api.getRest().exchange(link+"/"+resourceType+"/"+id, HttpMethod.PUT, requestEntity, String.class).getBody();
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

    // ===== Entry-based (referensi INC): sub-resource diskret di-link via section.entry =====
    private String postOrPutRes(String noRawat, String tglOp, String jenis, String resourceType, String bodyNoId) {
        String idLama = "";
        try (PreparedStatement p = koneksi.prepareStatement(
                "select id_resource from satu_sehat_laporan_res where laporan='tindakan' and no_rawat=? and tanggal=? and jenis=?")) {
            p.setString(1, noRawat); p.setString(2, tglOp); p.setString(3, jenis);
            try (ResultSet r = p.executeQuery()) { if (r.next()) { String v=r.getString(1); idLama=v==null?"":v; } }
        } catch (Exception e) { System.out.println("Notif cek res "+jenis+" : "+e); }
        String id;
        if (modeUpdate && !idLama.isEmpty()) { id = putResource(resourceType, idLama, "{\"id\":\""+idLama+"\","+bodyNoId.substring(1)); }
        else if (!idLama.isEmpty()) { return idLama; }
        else { id = postResource(resourceType, bodyNoId); }
        if (id != null && !id.isEmpty()) {
            try (PreparedStatement p = koneksi.prepareStatement(
                    "replace into satu_sehat_laporan_res (laporan,no_rawat,tanggal,jenis,id_resource,tgl_kirim) values ('tindakan',?,?,?,?,now())")) {
                p.setString(1, noRawat); p.setString(2, tglOp); p.setString(3, jenis); p.setString(4, id); p.executeUpdate();
            } catch (Exception e) { System.out.println("Notif simpan res "+jenis+" : "+e); }
            return id;
        }
        return idLama;
    }
    private String condBody(String text, String subjectRef, String idEnc, String recordedIso){
        StringBuilder b = new StringBuilder();
        b.append("{\"resourceType\":\"Condition\",");
        b.append("\"clinicalStatus\":{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-clinical\",\"code\":\"active\"}]},");
        b.append("\"category\":[{\"coding\":[{\"system\":\"http://terminology.hl7.org/CodeSystem/condition-category\",\"code\":\"encounter-diagnosis\",\"display\":\"Encounter Diagnosis\"}]}],");
        b.append("\"code\":{\"text\":\"").append(jsonEscape(text)).append("\"},");
        b.append("\"subject\":{\"reference\":\"").append(subjectRef).append("\"},");
        b.append("\"encounter\":{\"reference\":\"Encounter/").append(idEnc).append("\"}");
        if (recordedIso != null && !recordedIso.isEmpty()) b.append(",\"recordedDate\":\"").append(recordedIso).append("\"");
        b.append("}");
        return b.toString();
    }
    private String procBody(String text, String subjectRef, String idEnc, String perfIso, String performerRef){
        StringBuilder b = new StringBuilder();
        b.append("{\"resourceType\":\"Procedure\",\"status\":\"completed\",");
        // SatuSehat mewajibkan Procedure.code.coding (Rule 10015). Tindakan bebas-teks
        // dipetakan ke SNOMED generik 71388002 "Procedure", nama asli di code.text.
        b.append("\"code\":{\"coding\":[{\"system\":\"http://snomed.info/sct\",\"code\":\"71388002\",\"display\":\"Procedure\"}],\"text\":\"").append(jsonEscape(text)).append("\"},");
        b.append("\"subject\":{\"reference\":\"").append(subjectRef).append("\"},");
        b.append("\"encounter\":{\"reference\":\"Encounter/").append(idEnc).append("\"}");
        if (perfIso != null && !perfIso.isEmpty()) b.append(",\"performedDateTime\":\"").append(perfIso).append("\"");
        if (performerRef != null && !performerRef.isEmpty()) b.append(",\"performer\":[{\"actor\":{\"reference\":\"").append(performerRef).append("\"}}]");
        b.append("}");
        return b.toString();
    }
    private String linkAllergyEntries(String noRawat){
        StringBuilder b = new StringBuilder();
        try (PreparedStatement p = koneksi.prepareStatement(
                "select id_allergy_intolerance from satu_sehat_allergy_intolerance where no_rawat=? and ifnull(id_allergy_intolerance,'')<>''")) {
            p.setString(1, noRawat);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) { String id=r.getString(1);
                    if(id!=null && !id.isEmpty()){ if(b.length()>0) b.append(","); b.append("{\"reference\":\"AllergyIntolerance/").append(id).append("\"}"); } }
            }
        } catch (Exception e) { System.out.println("Notif link allergy : "+e); }
        return b.toString();
    }
    private static String refEntry(String type, String id){ return (id==null||id.isEmpty())?"":"{\"reference\":\""+type+"/"+id+"\"}"; }
    private static String joinEntries(String[] arr){
        StringBuilder b=new StringBuilder();
        for(String e:arr){ if(e!=null && !e.isEmpty()){ if(b.length()>0) b.append(","); b.append(e); } }
        return b.toString();
    }
    private static String sekEntry(String title, String entries){
        return (entries==null||entries.isEmpty())?"":"{\"title\":\""+jsonEscape(title)+"\",\"entry\":["+entries+"]}";
    }

    /** SELECT bersama grid & kirim satuan (dari kolom setelah "select"). */
    private static final String SEL_TINDAKAN =
            "select lo.no_rawat, reg_periksa.no_rkm_medis, "+
            "pasien.nm_pasien, pasien.no_ktp as ktppasien, "+
            "lo.kd_dokter as kd_dokter, ifnull(dokter.nm_dokter,'') as nm_dokter, "+
            "ifnull(pegawai.no_ktp,'') as ktpdokter, satu_sehat_encounter.id_encounter, "+
            "lo.tanggal, ifnull(lo.tindakan_medik,'') as tindakan_medik, "+
            "ifnull(lo.diagnosa_pra_tindakan,'') as diagnosa_pra, "+
            "ifnull(lo.diagnosa_pasca_tindakan,'') as diagnosa_pasca, "+
            "ifnull(lo.uraian,'') as uraian, ifnull(lo.hasil,'') as hasil, "+
            "ifnull(lo.kesimpulan,'') as kesimpulan, ifnull(sslo.id_composition,'') as id_composition "+
            "from laporan_tindakan lo "+
            "inner join reg_periksa on reg_periksa.no_rawat=lo.no_rawat "+
            "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
            "inner join satu_sehat_encounter on satu_sehat_encounter.no_rawat=lo.no_rawat "+
            "left join dokter on dokter.kd_dokter=lo.kd_dokter "+
            "left join pegawai on pegawai.nik=lo.kd_dokter "+
            "left join satu_sehat_laporan_tindakan sslo on sslo.no_rawat=lo.no_rawat and sslo.tanggal=lo.tanggal ";

    /** Saat true, kirimSatuNoRawat memPERBARUI (PUT) resource yang sudah terkirim
     *  alih-alih membuat baru. Dipakai lewat updateSatuNoRawat(). */
    private boolean modeUpdate = false;

    /** Perbarui (PUT) Composition yang SUDAH terkirim untuk satu no_rawat. */
    public int updateSatuNoRawat(String noRawat) {
        modeUpdate = true;
        try { return kirimSatuNoRawat(noRawat); }
        finally { modeUpdate = false; }
    }

    /** Kirim Composition Laporan Tindakan untuk 1 no_rawat (semua tindakannya). Form Kirim Bundel. */
    public int kirimSatuNoRawat(String noRawat) {
        Valid.tabelKosong(tabMode);
        try (PreparedStatement ps2 = koneksi.prepareStatement(SEL_TINDAKAN + "where lo.no_rawat=? order by lo.tanggal")) {
            ps2.setString(1, noRawat);
            try (ResultSet rs2 = ps2.executeQuery()) {
                while (rs2.next()) {
                    tabMode.addRow(new Object[]{ false,
                        rs2.getString("no_rawat"), rs2.getString("no_rkm_medis"), rs2.getString("nm_pasien"), rs2.getString("ktppasien"),
                        rs2.getString("kd_dokter"), rs2.getString("nm_dokter"), rs2.getString("ktpdokter"), rs2.getString("id_encounter"),
                        rs2.getString("tanggal"), rs2.getString("tindakan_medik"), rs2.getString("diagnosa_pra"), rs2.getString("diagnosa_pasca"),
                        rs2.getString("uraian"), rs2.getString("hasil"), rs2.getString("kesimpulan"), rs2.getString("id_composition") });
                }
            }
        } catch (Exception e) {
            System.out.println("Notif bundel Laporan Tindakan tampil : " + e);
            return 0;
        }
        int terkirim = 0;
        for (int r = 0; r < tbObat.getRowCount(); r++) {
            boolean sudahTerkirim = !tbObat.getValueAt(r,16).toString().isEmpty();
            // Kirim: lewati yang sudah terkirim. Update: lewati yang BELUM terkirim.
            if (modeUpdate ? !sudahTerkirim : sudahTerkirim) continue;
            if (tbObat.getValueAt(r,4).toString().isEmpty() || tbObat.getValueAt(r,8).toString().isEmpty()) continue;
            String idP = cekViaSatuSehat.tampilIDPasien(tbObat.getValueAt(r,4).toString());
            if (idP == null || idP.isEmpty()) continue;
            String ktpDok = tbObat.getValueAt(r,7).toString();
            String idD = (ktpDok==null||ktpDok.isEmpty()) ? "" : cekViaSatuSehat.tampilIDParktisi(ktpDok);
            if (idD == null) idD = "";
            try {
                kirimSatuRow(r, idP, idD);
                if (!tbObat.getValueAt(r,16).toString().isEmpty()) terkirim++;
            } catch (Exception e) {
                System.out.println("Notif bundel Laporan Tindakan kirim : " + e);
            }
        }
        return terkirim;
    }

    private void kirimSatuRow(int row, String idP, String idD) {
        String noRawat       = tbObat.getValueAt(row,1).toString();
        String nmPasien      = tbObat.getValueAt(row,3).toString();
        String nmDokter      = tbObat.getValueAt(row,6).toString();
        String idEnc         = tbObat.getValueAt(row,8).toString();
        String tglOp         = tbObat.getValueAt(row,9).toString();
        String tindakanMedik = tbObat.getValueAt(row,10).toString();
        String diagPre       = tbObat.getValueAt(row,11).toString();
        String diagPost      = tbObat.getValueAt(row,12).toString();
        String uraian        = tbObat.getValueAt(row,13).toString();
        String hasil         = tbObat.getValueAt(row,14).toString();
        String kesimpulan    = tbObat.getValueAt(row,15).toString();
        String idLama        = tbObat.getValueAt(row,16).toString();   // id Composition lama (utk PUT)

        String identifierValue = "laporan-tindakan-"+cleanId(noRawat)+"-"+cleanId(tglOp);
        String dateIso = toIso(tglOp);

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"resourceType\":\"Composition\",");
        if(modeUpdate && !idLama.isEmpty()) sb.append("\"id\":\"").append(idLama).append("\",");
        sb.append("\"identifier\":{");
        sb.append("\"system\":\"http://sys-ids.kemkes.go.id/composition/").append(koneksiDB.IDSATUSEHAT()).append("\",");
        sb.append("\"value\":\"").append(jsonEscape(identifierValue)).append("\"");
        sb.append("},");
        sb.append("\"status\":\"final\",");
        sb.append("\"type\":{\"coding\":[{");
        sb.append("\"system\":\"http://loinc.org\",");
        sb.append("\"code\":\"28570-0\",");
        sb.append("\"display\":\"Provider-unspecified procedure note\"");
        sb.append("}]},");
        sb.append("\"subject\":{\"reference\":\"Patient/").append(idP).append("\",\"display\":\"").append(jsonEscape(nmPasien)).append("\"},");
        sb.append("\"encounter\":{\"reference\":\"Encounter/").append(idEnc).append("\"},");
        if(!dateIso.equals("")){
            sb.append("\"date\":\"").append(dateIso).append("\",");
        }
        if(idD!=null && !idD.equals("")){
            sb.append("\"author\":[{");
            sb.append("\"reference\":\"Practitioner/").append(idD).append("\",");
            sb.append("\"display\":\"").append(jsonEscape(nmDokter)).append("\"");
            sb.append("}],");
        } else {
            sb.append("\"author\":[{\"reference\":\"Organization/").append(koneksiDB.IDSATUSEHAT()).append("\"}],");  // author wajib (Rule 10133)
        }
        sb.append("\"title\":\"Laporan Tindakan\",");
        sb.append("\"custodian\":{\"reference\":\"Organization/").append(koneksiDB.IDSATUSEHAT()).append("\"},");

        // Section[] -- kode procedure-note (bukan operation-note) sesuai panduan
        // Laporan Tindakan (type 28570-0). Tiap seksi di-skip bila sumber kosong.
        StringBuilder sections = new StringBuilder();
        java.util.List<String[]> seksiList = new java.util.ArrayList<>();
        seksiList.add(new String[]{"Indikasi Tindakan","59768-2","Procedure indications [Interpretation] Narrative",diagPre});
        seksiList.add(new String[]{"Tindakan Medik","59772-4","Planned procedure",tindakanMedik});
        seksiList.add(new String[]{"Uraian Tindakan","29554-3","Procedure Narrative",uraian});
        seksiList.add(new String[]{"Hasil","59776-5","Procedure findings Narrative",hasil});
        seksiList.add(new String[]{"Diagnosa Pasca Tindakan","59769-0","Postprocedure diagnosis Narrative",diagPost});
        seksiList.add(new String[]{"Kesimpulan / Rencana","18776-5","Plan of care note",kesimpulan});
        for(String[] sk : seksiList){
            String teks = sk[3];
            if(teks==null || teks.trim().isEmpty()) continue;
            if(sections.length()>0) sections.append(",");
            sections.append("{\"title\":\"").append(jsonEscape(sk[0])).append("\",")
                    .append("\"code\":{\"coding\":[{\"system\":\"http://loinc.org\",\"code\":\"").append(sk[1])
                    .append("\",\"display\":\"").append(jsonEscape(sk[2])).append("\"}]},")
                    .append("\"text\":{\"status\":\"generated\",\"div\":\"<div xmlns=\\\"http://www.w3.org/1999/xhtml\\\">")
                    .append(jsonEscape(htmlEscape(teks))).append("</div>\"}}");
        }

        // ---- Section entry-based (referensi INC): Allergies, Procedure Findings, Procedures ----
        String subjRef = "Patient/"+idP;
        String performerRef = (idD!=null && !idD.isEmpty()) ? "Practitioner/"+idD : "Organization/"+koneksiDB.IDSATUSEHAT();
        String findingTxt = (hasil!=null && !hasil.trim().isEmpty()) ? hasil : diagPost;
        String idFinding = (findingTxt==null||findingTxt.trim().isEmpty())
                ? "" : postOrPutRes(noRawat,tglOp,"finding","Condition",condBody(findingTxt,subjRef,idEnc,dateIso));
        String idProc = (tindakanMedik==null||tindakanMedik.trim().isEmpty())
                ? "" : postOrPutRes(noRawat,tglOp,"procedure","Procedure",procBody(tindakanMedik,subjRef,idEnc,dateIso,performerRef));
        String allergyEntries = linkAllergyEntries(noRawat);
        String[] entrySekArr = {
            sekEntry("Allergies and Intolerances Section", allergyEntries),
            sekEntry("Procedure Findings Section", joinEntries(new String[]{ refEntry("Condition",idFinding) })),
            sekEntry("Procedures Section", joinEntries(new String[]{ refEntry("Procedure",idProc) }))
        };
        for(String sek : entrySekArr){
            if(sek.isEmpty()) continue;
            if(sections.length()>0) sections.append(",");
            sections.append(sek);
        }

        sb.append("\"section\":[").append(sections).append("]");
        sb.append("}");

        String idComposition = (modeUpdate && !idLama.isEmpty())
                ? putResource("Composition", idLama, sb.toString())
                : postResource("Composition", sb.toString());
        if(!idComposition.equals("")){
            tbObat.setValueAt(idComposition, row, 16);
            tbObat.setValueAt(false, row, 0);

            String sqlSimpan = "replace into satu_sehat_laporan_tindakan (no_rawat, tanggal, id_composition, tgl_kirim) values (?, ?, ?, now())";
            try {
                PreparedStatement psSimpan = koneksi.prepareStatement(sqlSimpan);
                psSimpan.setString(1, noRawat);
                psSimpan.setString(2, tglOp);
                psSimpan.setString(3, idComposition);
                psSimpan.executeUpdate();
                psSimpan.close();
            } catch (Exception eSimpan) {
                System.out.println("Notif simpan tracking : "+eSimpan);
            }
        }
    }

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            SatuSehatKirimLaporanTindakan dialog = new SatuSehatKirimLaporanTindakan(new javax.swing.JFrame(), true);
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
        String tgl1 = Valid.SetTgl(DTPCari1.getSelectedItem()+"");
        String tgl2 = Valid.SetTgl(DTPCari2.getSelectedItem()+"");
        String filter = TCari.getText();
        boolean hasFilter = !filter.equals("");
        String filterSql = hasFilter ?
            "and (lo.no_rawat like ? or reg_periksa.no_rkm_medis like ? or pasien.nm_pasien like ? or lo.diagnosa_pra_tindakan like ? or lo.tindakan_medik like ?) "
            : "";

        String sql =
            "select lo.no_rawat, reg_periksa.no_rkm_medis, "+
            "pasien.nm_pasien, pasien.no_ktp as ktppasien, "+
            "lo.kd_dokter as kd_dokter, ifnull(dokter.nm_dokter,'') as nm_dokter, "+
            "ifnull(pegawai.no_ktp,'') as ktpdokter, "+
            "satu_sehat_encounter.id_encounter, "+
            "lo.tanggal, ifnull(lo.tindakan_medik,'') as tindakan_medik, "+
            "ifnull(lo.diagnosa_pra_tindakan,'') as diagnosa_pra, "+
            "ifnull(lo.diagnosa_pasca_tindakan,'') as diagnosa_pasca, "+
            "ifnull(lo.uraian,'') as uraian, "+
            "ifnull(lo.hasil,'') as hasil, "+
            "ifnull(lo.kesimpulan,'') as kesimpulan, "+
            "ifnull(sslo.id_composition,'') as id_composition "+
            "from laporan_tindakan lo "+
            "inner join reg_periksa on reg_periksa.no_rawat=lo.no_rawat "+
            "inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
            "inner join satu_sehat_encounter on satu_sehat_encounter.no_rawat=lo.no_rawat "+
            "left join dokter on dokter.kd_dokter=lo.kd_dokter "+
            "left join pegawai on pegawai.nik=lo.kd_dokter "+
            "left join satu_sehat_laporan_tindakan sslo on sslo.no_rawat=lo.no_rawat and sslo.tanggal=lo.tanggal "+
            "where lo.tanggal between ? and ? "+ filterSql +
            "order by lo.tanggal";
        try{
            ps=koneksi.prepareStatement(sql);
            try{
                ps.setString(1, tgl1+" 00:00:00");
                ps.setString(2, tgl2+" 23:59:59");
                if(hasFilter){
                    ps.setString(3,"%"+filter+"%");
                    ps.setString(4,"%"+filter+"%");
                    ps.setString(5,"%"+filter+"%");
                    ps.setString(6,"%"+filter+"%");
                    ps.setString(7,"%"+filter+"%");
                }
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        false,
                        rs.getString("no_rawat"),
                        rs.getString("no_rkm_medis"),
                        rs.getString("nm_pasien"),
                        rs.getString("ktppasien"),
                        rs.getString("kd_dokter"),
                        rs.getString("nm_dokter"),
                        rs.getString("ktpdokter"),
                        rs.getString("id_encounter"),
                        rs.getString("tanggal"),
                        rs.getString("tindakan_medik"),
                        rs.getString("diagnosa_pra"),
                        rs.getString("diagnosa_pasca"),
                        rs.getString("uraian"),
                        rs.getString("hasil"),
                        rs.getString("kesimpulan"),
                        rs.getString("id_composition")
                    });
                }
            }catch(Exception e){
                System.out.println("Notif laporan_operasi : "+e);
            }finally{
                if(rs!=null){ rs.close(); }
                if(ps!=null){ ps.close(); }
            }
        }catch(Exception e){
            System.out.println("Notifikasi laporan_operasi : "+e);
        }
        LCount.setText(""+tabMode.getRowCount());
    }

    public void isCek(){
        BtnKirim.setEnabled(akses.getsatu_sehat_kirim_encounter());
        BtnPrint.setEnabled(akses.getsatu_sehat_kirim_encounter());
    }

    public JTable getTable(){
        return tbObat;
    }
}
