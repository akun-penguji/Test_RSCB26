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
public final class SatuSehatKirimInvoice extends javax.swing.JDialog {
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

    /** Creates new form SatuSehatKirimInvoice
     * @param parent
     * @param modal */
    public SatuSehatKirimInvoice(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.setLocation(10,2);
        setSize(628,674);

        tabMode=new DefaultTableModel(null,new String[]{
                "P","No.Rawat","No.RM","Nama Pasien","No.KTP Pasien","ID Account",
                "Jml Item","Total Tagihan","ID Invoice"
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
                 java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbObat.setModel(tabMode);

        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        int[] lebar = {20,110,70,180,120,230,60,100,230};
        for (i = 0; i < lebar.length; i++) {
            tbObat.getColumnModel().getColumn(i).setPreferredWidth(lebar[i]);
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

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Pengiriman Data Invoice (Nota Tagihan) Satu Sehat ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
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

        BtnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/refresh.png"))); // NOI18N
        BtnUpdate.setMnemonic('U');
        BtnUpdate.setText("Update");
        BtnUpdate.setToolTipText("Alt+U (perbarui Invoice terkirim dgn ChargeItem terbaru)");
        BtnUpdate.setName("BtnUpdate"); // NOI18N
        BtnUpdate.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnUpdateActionPerformed(evt);
            }
        });
        panelGlass8.add(BtnUpdate);

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

        jLabel15.setText("Tgl.Tindakan :");
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
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>NIK</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>ID Account</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Jml Item</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Total Tagihan</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>ID Invoice</b></td>"+
                    "</tr>"
                );
                for (i = 0; i < tabMode.getRowCount(); i++) {
                    htmlContent.append(
                        "<tr class='isi'>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,1).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,2).toString()+"</td>"+
                            "<td valign='top'>"+jsonEscape(tbObat.getValueAt(i,3).toString())+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,4).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,5).toString()+"</td>"+
                            "<td valign='top' align='center'>"+tbObat.getValueAt(i,6).toString()+"</td>"+
                            "<td valign='top' align='right'>"+tbObat.getValueAt(i,7).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,8).toString()+"</td>"+
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

                File f = new File("DataSatuSehatChargeItem.html");
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));
                bw.write(LoadHTML.getText().replaceAll("<head>","<head>"+
                            "<link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" />"+
                            "<table width='3000px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                                "<tr class='isi2'>"+
                                    "<td valign='top' align='center'>"+
                                        "<font size='4' face='Tahoma'>"+akses.getnamars()+"</font><br>"+
                                        akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs()+"<br>"+
                                        akses.getkontakrs()+", E-mail : "+akses.getemailrs()+"<br><br>"+
                                        "<font size='2' face='Tahoma'>DATA PENGIRIMAN SATU SEHAT CHARGEITEM<br><br></font>"+
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
                    &&(!tbObat.getValueAt(i,4).toString().equals(""))   // NIK pasien
                    &&(!tbObat.getValueAt(i,5).toString().equals(""))   // ID Account
                    && tbObat.getValueAt(i,8).toString().equals("")){   // belum ada Invoice
                try {
                    idpasien=cekViaSatuSehat.tampilIDPasien(tbObat.getValueAt(i,4).toString());
                    if(idpasien==null||idpasien.equals("")){
                        continue;
                    }
                    kirimSatuRow(i,idpasien);
                } catch (Exception e) {
                    System.out.println("Notifikasi : "+e);
                }
            }
        }
    }//GEN-LAST:event_BtnKirimActionPerformed

    private void BtnUpdateActionPerformed(java.awt.event.ActionEvent evt) {
        for(i=0;i<tbObat.getRowCount();i++){
            if(tbObat.getValueAt(i,0).toString().equals("true")
                    &&(!tbObat.getValueAt(i,4).toString().equals(""))   // NIK pasien
                    &&(!tbObat.getValueAt(i,5).toString().equals(""))   // ID Account
                    &&(!tbObat.getValueAt(i,8).toString().equals(""))){ // harus sudah ada Invoice
                try {
                    idpasien=cekViaSatuSehat.tampilIDPasien(tbObat.getValueAt(i,4).toString());
                    if(idpasien==null||idpasien.equals("")){ continue; }
                    updateSatuRow(i,idpasien);
                } catch (Exception e) {
                    System.out.println("Notifikasi : "+e);
                }
            }
        }
    }

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

    /** Convert "yyyy-MM-dd HH:mm:ss" or "yyyy-MM-dd" to FHIR ISO UTC +00:00 via IsoTime helper.
     *  Returns "" if input null/empty/zero-date. */
    private static String toIso(String tgl) {
        if (tgl == null) return "";
        String t = tgl.trim();
        if (t.equals("") || t.startsWith("0000-00-00")) return "";
        if (t.length() < 10) return "";
        if (t.length() == 10) return fungsi.IsoTime.toUtc(t, "00:00:00");
        return fungsi.IsoTime.toUtc(t);
    }

    /** Build ISO datetime (UTC +00:00 via IsoTime helper) from separate date + time strings. */
    private static String toIso(String tgl, String jam){
        if(tgl==null) return "";
        String t = tgl.trim();
        if(t.equals("") || t.startsWith("0000-00-00")) return "";
        if(t.length() < 10) return "";
        String d = t.substring(0,10);
        String j = (jam==null?"":jam.trim());
        if(j.equals("")) j = "00:00:00";
        if(j.length()==5) j = j + ":00";
        return fungsi.IsoTime.toUtc(d, j);
    }

    /** POST a resource to SatuSehat FHIR endpoint, return id from response or "" on failure. */
    private String lastError="";

    private String postResource(String resourceType, String body) {
        lastError="";
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
            lastError=e.getResponseBodyAsString();
            System.out.println("Notifikasi Bridging : "+e);
            System.out.println("Response Body : "+lastError);
        }catch(Exception e){
            lastError=String.valueOf(e);
            System.out.println("Notifikasi Bridging : "+e);
        }
        return "";
    }

    private double lastTotal=0;
    private int lastJml=0;

    /** Bangun JSON Invoice dari ChargeItem milik noRawat. existingId non-kosong => sertakan "id" (untuk PUT). Return null bila tak ada ChargeItem. */
    private String buildInvoiceBody(String noRawat, String idP, String nmPasien, String idAccount, String existingId){
        java.util.List<String> ciIds = new java.util.ArrayList<>();
        java.util.List<Double> ciBiaya = new java.util.ArrayList<>();
        double total = 0; String tglNota = "";
        try {
            PreparedStatement p = koneksi.prepareStatement(
                "select id_chargeitem, biaya, tgl_perawatan from satu_sehat_chargeitem "+
                "where no_rawat=? and id_chargeitem is not null and id_chargeitem<>'' order by tgl_perawatan, jam_rawat");
            p.setString(1, noRawat);
            ResultSet r = p.executeQuery();
            while(r.next()){
                ciIds.add(r.getString("id_chargeitem"));
                double b = r.getDouble("biaya"); ciBiaya.add(b); total += b;
                if(tglNota.equals("")) tglNota = r.getString("tgl_perawatan");
            }
            r.close(); p.close();
        } catch(Exception e){ System.out.println("Notif ambil chargeitem : "+e); }
        if(ciIds.isEmpty()) return null;
        lastTotal = total; lastJml = ciIds.size();
        String dateIso = toIso(tglNota);
        if(dateIso.equals("")) dateIso = toIso(tglNota, "00:00:00");

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"resourceType\":\"Invoice\",");
        if(existingId!=null && !existingId.equals("")) sb.append("\"id\":\"").append(existingId).append("\",");
        sb.append("\"identifier\":[{");
        sb.append("\"system\":\"http://sys-ids.kemkes.go.id/invoice/").append(koneksiDB.IDSATUSEHAT()).append("\",");
        sb.append("\"use\":\"official\",\"value\":\"").append(jsonEscape(noRawat)).append("\"");
        sb.append("}],");
        sb.append("\"status\":\"issued\",");
        sb.append("\"subject\":{\"reference\":\"Patient/").append(idP).append("\",\"display\":\"").append(jsonEscape(nmPasien)).append("\"},");
        sb.append("\"recipient\":{\"reference\":\"Patient/").append(idP).append("\",\"display\":\"").append(jsonEscape(nmPasien)).append("\"},");
        if(!dateIso.equals("")) sb.append("\"date\":\"").append(dateIso).append("\",");
        sb.append("\"issuer\":{\"reference\":\"Organization/").append(koneksiDB.IDSATUSEHAT()).append("\",\"display\":\"").append(jsonEscape(akses.getnamars())).append("\"},");
        sb.append("\"account\":{\"reference\":\"Account/").append(idAccount).append("\"},");
        sb.append("\"lineItem\":[");
        for(int k=0;k<ciIds.size();k++){
            if(k>0) sb.append(",");
            sb.append("{\"sequence\":").append(k+1).append(",");
            sb.append("\"chargeItemReference\":{\"reference\":\"ChargeItem/").append(ciIds.get(k)).append("\"},");
            sb.append("\"priceComponent\":[{\"type\":\"base\",\"amount\":{\"value\":").append(ciBiaya.get(k)).append(",\"currency\":\"IDR\"}}]}");
        }
        sb.append("],");
        sb.append("\"totalNet\":{\"value\":").append(total).append(",\"currency\":\"IDR\"},");
        sb.append("\"totalGross\":{\"value\":").append(total).append(",\"currency\":\"IDR\"}");
        sb.append("}");
        return sb.toString();
    }

    private void simpanTracking(String noRawat, String idInvoice){
        try {
            PreparedStatement ps2 = koneksi.prepareStatement(
                "replace into satu_sehat_invoice (no_rawat,id_invoice,total,jml_item,tgl_kirim) values (?,?,?,?,now())");
            ps2.setString(1, noRawat);
            ps2.setString(2, idInvoice);
            ps2.setDouble(3, lastTotal);
            ps2.setInt(4, lastJml);
            ps2.executeUpdate();
            ps2.close();
        } catch(Exception e){ System.out.println("Notif simpan invoice : "+e); }
    }

    private void kirimSatuRow(int row, String idP) {
        String noRawat   = tbObat.getValueAt(row,1).toString();
        String nmPasien  = tbObat.getValueAt(row,3).toString();
        String idAccount = tbObat.getValueAt(row,5).toString();
        String body = buildInvoiceBody(noRawat, idP, nmPasien, idAccount, "");
        if(body==null){ System.out.println("Skip "+noRawat+" : tidak ada ChargeItem terkirim"); return; }
        String idInvoice = postResource("Invoice", body);
        if(!idInvoice.equals("")){
            tbObat.setValueAt(idInvoice, row, 8);
            tbObat.setValueAt(false, row, 0);
            simpanTracking(noRawat, idInvoice);
        }
    }

    /** Update Invoice yang SUDAH ada (PUT by id) dgn daftar ChargeItem terbaru no_rawat tsb. */
    private void updateSatuRow(int row, String idP) {
        String noRawat   = tbObat.getValueAt(row,1).toString();
        String nmPasien  = tbObat.getValueAt(row,3).toString();
        String idAccount = tbObat.getValueAt(row,5).toString();
        String idInvoice = tbObat.getValueAt(row,8).toString();
        if(idInvoice.equals("")){ System.out.println("Skip "+noRawat+" : belum ada Invoice (pakai Kirim)"); return; }
        String body = buildInvoiceBody(noRawat, idP, nmPasien, idAccount, idInvoice);
        if(body==null){ System.out.println("Skip "+noRawat+" : tidak ada ChargeItem"); return; }
        String idBaru = putResource("Invoice", idInvoice, body);
        if(!idBaru.equals("")){
            tbObat.setValueAt(false, row, 0);
            simpanTracking(noRawat, idBaru);
        }
    }

    private String putResource(String resourceType, String id, String body) {
        lastError="";
        try{
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
            System.out.println("URL PUT : "+link+"/"+resourceType+"/"+id);
            System.out.println("Request JSON : "+body);
            requestEntity = new HttpEntity(body,headers);
            String resp = api.getRest().exchange(link+"/"+resourceType+"/"+id, HttpMethod.PUT, requestEntity, String.class).getBody();
            System.out.println("Result JSON : "+resp);
            JsonNode r = mapper.readTree(resp);
            JsonNode rid = r.path("id");
            if(rid!=null && !rid.asText().equals("")) return rid.asText();
        }catch(org.springframework.web.client.HttpClientErrorException e){
            lastError=e.getResponseBodyAsString();
            System.out.println("Notifikasi Bridging : "+e);
            System.out.println("Response Body : "+lastError);
        }catch(Exception e){
            lastError=String.valueOf(e);
            System.out.println("Notifikasi Bridging : "+e);
        }
        return "";
    }

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try{
            String tgl1 = Valid.SetTgl(DTPCari1.getSelectedItem()+"");
            String tgl2 = Valid.SetTgl(DTPCari2.getSelectedItem()+"");
            boolean hasFilter = !TCari.getText().trim().equals("");
            String sql =
                "select sc.no_rawat, reg_periksa.no_rkm_medis, pasien.nm_pasien, pasien.no_ktp, "+
                "ifnull(sa.id_account,'') id_account, count(*) jml_item, sum(sc.biaya) total, "+
                "ifnull(si.id_invoice,'') id_invoice "+
                "from satu_sehat_chargeitem sc "+
                "inner join reg_periksa on reg_periksa.no_rawat=sc.no_rawat "+
                "inner join pasien on pasien.no_rkm_medis=reg_periksa.no_rkm_medis "+
                "left join satu_sehat_account sa on sa.no_rawat=sc.no_rawat "+
                "left join satu_sehat_invoice si on si.no_rawat=sc.no_rawat "+
                "where sc.id_chargeitem is not null and sc.id_chargeitem<>'' "+
                "and sc.tgl_perawatan between ? and ? "+
                (hasFilter?"and (sc.no_rawat like ? or pasien.nm_pasien like ? or reg_periksa.no_rkm_medis like ?) ":"")+
                "group by sc.no_rawat order by sc.no_rawat";
            ps=koneksi.prepareStatement(sql);
            ps.setString(1, tgl1);
            ps.setString(2, tgl2);
            if(hasFilter){
                ps.setString(3,"%"+TCari.getText()+"%");
                ps.setString(4,"%"+TCari.getText()+"%");
                ps.setString(5,"%"+TCari.getText()+"%");
            }
            rs=ps.executeQuery();
            while(rs.next()){
                tabMode.addRow(new Object[]{
                    false,
                    rs.getString("no_rawat"), rs.getString("no_rkm_medis"),
                    rs.getString("nm_pasien"), rs.getString("no_ktp"),
                    rs.getString("id_account"), rs.getString("jml_item"),
                    rs.getString("total"), rs.getString("id_invoice")
                });
            }
        }catch(Exception e){
            System.out.println("Notif tampil invoice : "+e);
        }finally{
            try{ if(rs!=null) rs.close(); if(ps!=null) ps.close(); }catch(Exception x){}
        }
        LCount.setText(""+tabMode.getRowCount());
    }

    public void isCek(){
        // No specific ChargeItem permission in akses; fallback to encounter permission.
        BtnKirim.setEnabled(akses.getsatu_sehat_kirim_encounter());
        BtnPrint.setEnabled(akses.getsatu_sehat_kirim_encounter());
    }

    public JTable getTable(){
        return tbObat;
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            SatuSehatKirimInvoice dialog = new SatuSehatKirimInvoice(new javax.swing.JFrame(), true);
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
    private widget.Button BtnUpdate;
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

}
