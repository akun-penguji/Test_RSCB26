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
public final class SatuSehatKirimVaksin extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;   
    private int i=0;
    private String link="",json="",idpasien="",iddokter="";
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
    public SatuSehatKirimVaksin(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.setLocation(10,2);
        setSize(628,674);

        tabMode=new DefaultTableModel(null,new String[]{
                "P","Tanggal Registrasi","No.Rawat","No.RM","Nama Pasien","No.KTP Pasien","Stts Rawat","Stts Lanjut",
                "ID Encounter","Vaksin Code","Vaksin System","Kode Vaksin","Vaksin Display","Route Code","Route System",
                "Route Display","Dose Code","Dose System","Dose Unit","No.Batch","Tanggal & Jam Beri","Jml/Kps","Dosis/No",
                "ID Lokasi Satu Sehat","Nama Unit/Poli","Dokter Penanggung Jawab","No.KTP Praktisi","ID Imunisasi Satu Sehat",
                "No.Faktur","Tgl.Kadaluarsa"
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
                 java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class,java.lang.String.class
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

        for (i = 0; i < 29; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(150);
            }else if(i==2){
                column.setPreferredWidth(105);
            }else if(i==3){
                column.setPreferredWidth(70);
            }else if(i==4){
                column.setPreferredWidth(150);
            }else if(i==5){
                column.setPreferredWidth(110);
            }else if(i==6){
                column.setPreferredWidth(63);
            }else if(i==7){
                column.setPreferredWidth(63);
            }else if(i==8){
                column.setPreferredWidth(210);
            }else if(i==9){
                column.setPreferredWidth(80);
            }else if(i==10){
                column.setPreferredWidth(170);
            }else if(i==11){
                column.setPreferredWidth(80);
            }else if(i==12){
                column.setPreferredWidth(170);
            }else if(i==13){
                column.setPreferredWidth(90);
            }else if(i==14){
                column.setPreferredWidth(170);
            }else if(i==15){
                column.setPreferredWidth(170);
            }else if(i==16){
                column.setPreferredWidth(60);
            }else if(i==17){
                column.setPreferredWidth(170);
            }else if(i==18){
                column.setPreferredWidth(60);
            }else if(i==19){
                column.setPreferredWidth(70);
            }else if(i==20){
                column.setPreferredWidth(150);
            }else if(i==21){
                column.setPreferredWidth(50);
            }else if(i==22){
                column.setPreferredWidth(50);
            }else if(i==23){
                column.setPreferredWidth(210);
            }else if(i==24){
                column.setPreferredWidth(150);
            }else if(i==25){
                column.setPreferredWidth(150);
            }else if(i==26){
                column.setPreferredWidth(105);
            }else if(i==27){
                column.setPreferredWidth(210);
            }else if(i==28){
                column.setMinWidth(0);
                column.setMaxWidth(0);
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
        CmbStatus = new widget.ComboBox();

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

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Pengiriman Data Imunisasi Satu Sehat ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
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

        BtnUpdate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/edit_f2.png"))); // NOI18N
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

        jLabel15.setText("Tgl.Registrasi :");
        jLabel15.setName("jLabel15"); // NOI18N
        jLabel15.setPreferredSize(new java.awt.Dimension(85, 23));
        panelGlass9.add(jLabel15);

        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "14-06-2023" }));
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

        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "14-06-2023" }));
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

        jLabel18 = new widget.Label();
        jLabel18.setText("Status :");
        jLabel18.setName("jLabel18");
        jLabel18.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass9.add(jLabel18);

        CmbStatus.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Semua", "Belum Terkirim", "Sudah Terkirim" }));
        CmbStatus.setName("CmbStatus");
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
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Tanggal Registrasi</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.Rawat</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.RM</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Nama Pasien</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.KTP Pasien</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Stts Rawat</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Stts Lanjut</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>ID Encounter</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Vaksin Code</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Vaksin System</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Kode Vaksin</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Vaksin Display</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Route Code</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Route System</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Route Display</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Dose Code</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Dose System</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Dose Unit</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.Batch</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Tanggal & Jam Beri</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Jml/Kps</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Dosis/No</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>ID Lokasi Satu Sehat</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Nama Unit/Poli</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Dokter Penanggung Jawab</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.KTP Praktisi</b></td>"+
                        "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>ID Imunisasi Satu Sehat</b></td>"+
                    "</tr>"
                );
                for (i = 0; i < tabMode.getRowCount(); i++) {
                    htmlContent.append(
                        "<tr class='isi'>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,1).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,2).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,3).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,4).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,5).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,6).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,7).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,8).toString()+"</td>"+
                            "<td valign='top'>"+jsonEscape(tbObat.getValueAt(i,9).toString())+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,10).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,11).toString()+"</td>"+
                            "<td valign='top'>"+jsonEscape(tbObat.getValueAt(i,12).toString())+"</td>"+
                            "<td valign='top'>"+jsonEscape(tbObat.getValueAt(i,13).toString())+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,14).toString()+"</td>"+
                            "<td valign='top'>"+jsonEscape(tbObat.getValueAt(i,15).toString())+"</td>"+
                            "<td valign='top'>"+jsonEscape(tbObat.getValueAt(i,16).toString())+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,17).toString()+"</td>"+
                            "<td valign='top'>"+jsonEscape(tbObat.getValueAt(i,18).toString())+"</td>"+
                            "<td valign='top'>"+jsonEscape(tbObat.getValueAt(i,19).toString())+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,20).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,21).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,22).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,23).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,24).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,25).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,26).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,27).toString()+"</td>"+
                            "<td valign='top'>"+tbObat.getValueAt(i,28).toString()+"</td>"+
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
                    ".isi2 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#323232;}"+
                    ".isi3 td{border-right: 1px solid #e2e7dd;font: 8.5px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                    ".isi4 td{font: 11px tahoma;height:12px;border-top: 1px solid #e2e7dd;background: #ffffff;color:#323232;}"+
                    ".isi5 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#AA0000;}"+
                    ".isi6 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#FF0000;}"+
                    ".isi7 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#C8C800;}"+
                    ".isi8 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#00AA00;}"+
                    ".isi9 td{font: 8.5px tahoma;border:none;height:12px;background: #ffffff;color:#969696;}"
                );
                bg.close();

                File f = new File("DataSatuSehatImunisasi.html");            
                BufferedWriter bw = new BufferedWriter(new FileWriter(f));            
                bw.write(LoadHTML.getText().replaceAll("<head>","<head>"+
                            "<link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" />"+
                            "<table width='3000px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                                "<tr class='isi2'>"+
                                    "<td valign='top' align='center'>"+
                                        "<font size='4' face='Tahoma'>"+akses.getnamars()+"</font><br>"+
                                        akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs()+"<br>"+
                                        akses.getkontakrs()+", E-mail : "+akses.getemailrs()+"<br><br>"+
                                        "<font size='2' face='Tahoma'>DATA PENGIRIMAN SATU SEHAT IMUNISAS<br><br></font>"+        
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
        int tanpaBatch = 0;
        for(i=0;i<tbObat.getRowCount();i++){
            // NIK boleh kosong: bayi baru lahir memakai IHS hasil pendaftaran lewat NIK ibu.
            if(tbObat.getValueAt(i,0).toString().equals("true")&&(!tbObat.getValueAt(i,8).toString().equals(""))&&tbObat.getValueAt(i,27).toString().equals("")){
                try {
                    idpasien=ihsPasienAtauBayi(tbObat.getValueAt(i,5).toString(), tbObat.getValueAt(i,2).toString());
                    iddokter=cekViaSatuSehat.tampilIDParktisi(tbObat.getValueAt(i,26).toString());
                    try{
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
                        String vaccineSystem = tbObat.getValueAt(i,10).toString();
                        String doseStr = tbObat.getValueAt(i,22).toString().toLowerCase().replaceAll("dosis","").replaceAll(" ","");
                        boolean validDose = false;
                        try { int d = Integer.parseInt(doseStr); validDose = d > 0; } catch(Exception ex){}
                        // Fetch FHIR Immunization spec-compliance extras (v2026.05.28.03) :
                        //   - CVX Group/Name (multi-coding vaccineCode)
                        //   - manufacturer Org IHS, targetDisease/series (protocolApplied)
                        //   - site (LA/RA/...), funding_source, routine_timing (reasonCode), dose_number
                        // Semua kolom optional — sender tetap jalan kalau NULL.
                        FhirExtras extras = loadFhirExtras(
                                tbObat.getValueAt(i,11).toString(),  // kode_brng
                                tbObat.getValueAt(i,2).toString(),   // no_rawat
                                tbObat.getValueAt(i,20).toString()); // tgl_jam ISO
                        // SatuSehat mewajibkan lotNumber & expirationDate (Rule 10306/10307).
                        // Kalau batch/kadaluarsa belum ada, baris dilewati supaya tidak
                        // menembak API dengan data yang pasti ditolak; petugas melengkapinya
                        // lewat form Data Imunisasi Pasien.
                        if (!lengkapBatch(tbObat.getValueAt(i,19), tbObat.getValueAt(i,29), extras)) {
                            System.out.println("Lewati " + tbObat.getValueAt(i,4)
                                    + " : no.batch / tgl.kadaluarsa vaksin belum diisi");
                            tanpaBatch++;
                            continue;
                        }
                        String orgIhs = "";
                        try { orgIhs = koneksiDB.IDSATUSEHAT(); } catch(Exception ex){}
                        // Build vaccineCode coding array (KFA + optional CVX Group + CVX Name)
                        StringBuilder vaxCoding = new StringBuilder();
                        vaxCoding.append("{")
                                .append("\"system\": \"").append(vaccineSystem).append("\",")
                                .append("\"code\": \"").append(jsonEscape(tbObat.getValueAt(i,9).toString())).append("\",")
                                .append("\"display\": \"").append(jsonEscape(tbObat.getValueAt(i,12).toString())).append("\"")
                                .append("}");
                        if (extras.cvxGroupCode != null && !extras.cvxGroupCode.isEmpty()) {
                            vaxCoding.append(",{")
                                    .append("\"system\": \"http://terminology.hl7.org/CodeSystem/cvx-group\",")
                                    .append("\"code\": \"").append(jsonEscape(extras.cvxGroupCode)).append("\",")
                                    .append("\"display\": \"").append(jsonEscape(safe(extras.cvxGroupDisplay))).append("\"")
                                    .append("}");
                        }
                        if (extras.cvxNameCode != null && !extras.cvxNameCode.isEmpty()) {
                            vaxCoding.append(",{")
                                    .append("\"system\": \"http://hl7.org/fhir/sid/cvx\",")
                                    .append("\"code\": \"").append(jsonEscape(extras.cvxNameCode)).append("\",")
                                    .append("\"display\": \"").append(jsonEscape(safe(extras.cvxNameDisplay))).append("\"")
                                    .append("}");
                        }
                        // identifier (idempotency) — system Kemkes immunization/{org-ihs}, value unik per pemberian
                        String idempVal = tbObat.getValueAt(i,2).toString()+"|"+tbObat.getValueAt(i,20).toString()+"|"+tbObat.getValueAt(i,11).toString();
                        // reasonCode → routine timing Kemkes (default IM-Ideal)
                        String routineTiming = (extras.routineTiming!=null && !extras.routineTiming.isEmpty() && !extras.routineTiming.equals("-"))
                                ? extras.routineTiming : "IM-Ideal";
                        // fundingSource — map kd_pj → public(BPJS)/private(umum)
                        String fundingCode = mapFundingSource(extras.kdPj);
                        // protocolApplied → gabungkan doseNumber + series + targetDisease + seriesDoses
                        String doseNumber = validDose ? doseStr
                                : (extras.doseNumber>0 ? String.valueOf(extras.doseNumber) : "");
                        StringBuilder protoApplied = new StringBuilder();
                        if (!doseNumber.isEmpty()
                                || (extras.seriesName!=null && !extras.seriesName.isEmpty())
                                || (extras.targetDiseaseCode!=null && !extras.targetDiseaseCode.isEmpty())) {
                            protoApplied.append(",\"protocolApplied\": [{");
                            boolean firstField = true;
                            if (extras.seriesName != null && !extras.seriesName.isEmpty()) {
                                protoApplied.append("\"series\": \"").append(jsonEscape(extras.seriesName)).append("\"");
                                firstField = false;
                            }
                            if (extras.targetDiseaseCode != null && !extras.targetDiseaseCode.isEmpty()) {
                                if (!firstField) protoApplied.append(",");
                                protoApplied.append("\"targetDisease\": [{\"coding\": [{")
                                        .append("\"system\": \"http://hl7.org/fhir/sid/icd-10\",")
                                        .append("\"code\": \"").append(jsonEscape(extras.targetDiseaseCode)).append("\",")
                                        .append("\"display\": \"").append(jsonEscape(safe(extras.targetDiseaseDisplay))).append("\"")
                                        .append("}]}]");
                                firstField = false;
                            }
                            if (!doseNumber.isEmpty()) {
                                if (!firstField) protoApplied.append(",");
                                protoApplied.append("\"doseNumberPositiveInt\": ").append(doseNumber);
                                firstField = false;
                            }
                            if (extras.seriesDoses > 0) {
                                if (!firstField) protoApplied.append(",");
                                protoApplied.append("\"seriesDosesPositiveInt\": ").append(extras.seriesDoses);
                            }
                            protoApplied.append("}]");
                        }
                        // site fragment (HL7 v3 ActSite)
                        String siteFragment = "";
                        if (extras.site != null && !extras.site.isEmpty()) {
                            siteFragment = ",\"site\": {\"coding\": [{"
                                    + "\"system\": \"http://terminology.hl7.org/CodeSystem/v3-ActSite\","
                                    + "\"code\": \"" + jsonEscape(extras.site) + "\","
                                    + "\"display\": \"" + jsonEscape(siteDisplay(extras.site)) + "\""
                                    + "}]}";
                        }
                        // manufacturer fragment (Reference Organization/{ihs})
                        String manufacturerFragment = "";
                        if (extras.manufacturerOrgIhs != null && !extras.manufacturerOrgIhs.isEmpty()) {
                            manufacturerFragment = ",\"manufacturer\": {\"reference\": \"Organization/"
                                    + jsonEscape(extras.manufacturerOrgIhs) + "\"}";
                        }
                        // fundingSource fragment
                        String fundingFragment = "";
                        if (fundingCode != null && !fundingCode.isEmpty()) {
                            fundingFragment = ",\"fundingSource\": {\"coding\": [{"
                                    + "\"system\": \"http://terminology.hl7.org/CodeSystem/immunization-funding-source\","
                                    + "\"code\": \"" + fundingCode + "\""
                                    + "}]}";
                        }
                        json = "{" +
                                    "\"resourceType\": \"Immunization\"," +
                                    "\"identifier\": [{" +
                                        "\"system\": \"http://sys-ids.kemkes.go.id/immunization/"+orgIhs+"\"," +
                                        "\"value\": \""+jsonEscape(idempVal)+"\"" +
                                    "}]," +
                                    "\"status\": \"completed\"," +
                                    "\"vaccineCode\": {" +
                                        "\"coding\": [" + vaxCoding.toString() + "]" +
                                    "}," +
                                    "\"patient\": {" +
                                        "\"reference\": \"Patient/"+idpasien+"\"" +
                                    "}," +
                                    "\"encounter\": {" +
                                        "\"reference\": \"Encounter/"+tbObat.getValueAt(i,8).toString()+"\"" +
                                    "}," +
                                    "\"occurrenceDateTime\": \""+tbObat.getValueAt(i,20).toString()+"\"," +
                                    "\"recorded\": \""+tbObat.getValueAt(i,20).toString()+"\"," +
                                    "\"primarySource\": true," +
                                    "\"reasonCode\": [{\"coding\": [{" +
                                        "\"system\": \"http://terminology.kemkes.go.id/CodeSystem/immunization-routine-timing\"," +
                                        "\"code\": \""+routineTiming+"\"," +
                                        "\"display\": \""+(routineTiming.equals("IM-Kejar")?"Imunisasi Kejar":"Imunisasi Ideal")+"\"" +
                                    "}]}]," +
                                    "\"location\": {" +
                                        "\"reference\": \"Location/"+tbObat.getValueAt(i,23).toString()+"\"" +
                                    "}," +
                                    manufacturerSafeComma(manufacturerFragment) +
                                    // lotNumber/expirationDate opsional: kalau dikirim kosong,
                                    // server menolak (10306 "Text is empty", 10307 "Invalid date format").
                                    lotDanKadaluarsa(tbObat.getValueAt(i,19), tbObat.getValueAt(i,29), extras) +
                                    "\"route\": {" +
                                        "\"coding\": [" +
                                            "{" +
                                                "\"system\": \""+tbObat.getValueAt(i,14).toString()+"\"," +
                                                "\"code\": \""+jsonEscape(tbObat.getValueAt(i,13).toString())+"\"," +
                                                "\"display\": \""+jsonEscape(tbObat.getValueAt(i,15).toString())+"\"" +
                                            "}" +
                                        "]" +
                                    "}," +
                                    "\"doseQuantity\": {" +
                                        "\"value\": "+tbObat.getValueAt(i,21).toString()+"," +
                                        "\"unit\": \""+jsonEscape(tbObat.getValueAt(i,18).toString())+"\"," +
                                        "\"system\": \""+tbObat.getValueAt(i,17).toString()+"\"," +
                                        "\"code\": \""+jsonEscape(tbObat.getValueAt(i,16).toString())+"\"" +
                                    "}," +
                                    "\"performer\": [" +
                                        "{" +
                                            "\"function\": {\"coding\": [{\"system\": \"http://terminology.hl7.org/CodeSystem/v2-0443\",\"code\": \"AP\",\"display\": \"Administering Provider\"}]}," +
                                            "\"actor\": {" +
                                                "\"reference\": \"Practitioner/"+iddokter+"\"" +
                                            "}" +
                                        "}" +
                                    "]" +
                                    siteFragment +
                                    fundingFragment +
                                    protoApplied.toString() +
                                "}";
                        System.out.println("URL : "+link+"/Immunization");
                        System.out.println("Request JSON : "+json);
                        requestEntity = new HttpEntity(json,headers);
                        json=api.getRest().exchange(link+"/Immunization", HttpMethod.POST, requestEntity, String.class).getBody();
                        System.out.println("Result JSON : "+json);
                        root = mapper.readTree(json);
                        response = root.path("id");
                        if(!response.asText().equals("")){
                            // Kolom 20 sudah UTC (WIB-7); satu_sehat_immunization harus menyimpan
                            // waktu LOKAL supaya join balik ke detail_pemberian_obat pada tampil() ketemu.
                            String wibSimpan = wibDari(tbObat.getValueAt(i,20));
                            if(Sequel.menyimpantf2("satu_sehat_immunization","?,?,?,?,?,?,?","Imunisasi/Vaksin",7,new String[]{
                                tbObat.getValueAt(i,2).toString(),wibSimpan.substring(0,10),wibSimpan.substring(11,19),
                                tbObat.getValueAt(i,11).toString(),tbObat.getValueAt(i,19).toString(),tbObat.getValueAt(i,28).toString(),response.asText()
                            })==true){
                                tbObat.setValueAt(response.asText(),i,27);
                                tbObat.setValueAt(false,i,0);
                            }
                        }
                    }catch(org.springframework.web.client.HttpClientErrorException e){
                        System.out.println("Notifikasi Bridging : "+e);
                        System.out.println("Response Body : "+e.getResponseBodyAsString());
                    }catch(Exception e){
                        System.out.println("Notifikasi Bridging : "+e);
                    }
                } catch (Exception e) {
                    System.out.println("Notifikasi : "+e);
                }
            }
        }
            if (tanpaBatch > 0) {
            javax.swing.JOptionPane.showMessageDialog(this, tanpaBatch
                    + " baris dilewati karena no.batch / tgl.kadaluarsa vaksin belum diisi.\n\n"
                    + "SatuSehat mewajibkan lotNumber & expirationDate pada Immunization.\n"
                    + "Lengkapi lewat form Data Imunisasi Pasien, lalu kirim ulang.");
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

    private void BtnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnUpdateActionPerformed
        int tanpaBatch = 0;
        for(i=0;i<tbObat.getRowCount();i++){
            if(tbObat.getValueAt(i,0).toString().equals("true")&&(!tbObat.getValueAt(i,8).toString().equals(""))&&(!tbObat.getValueAt(i,27).toString().equals(""))){
                try {
                    idpasien=ihsPasienAtauBayi(tbObat.getValueAt(i,5).toString(), tbObat.getValueAt(i,2).toString());
                    iddokter=cekViaSatuSehat.tampilIDParktisi(tbObat.getValueAt(i,26).toString());
                    try{
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
                        // FHIR Immunization spec-compliance extras (sama dgn POST) — v2026.05.28.03
                        FhirExtras extras = loadFhirExtras(
                                tbObat.getValueAt(i,11).toString(),
                                tbObat.getValueAt(i,2).toString(),
                                tbObat.getValueAt(i,20).toString());
                        // SatuSehat mewajibkan lotNumber & expirationDate (Rule 10306/10307).
                        // Kalau batch/kadaluarsa belum ada, baris dilewati supaya tidak
                        // menembak API dengan data yang pasti ditolak; petugas melengkapinya
                        // lewat form Data Imunisasi Pasien.
                        if (!lengkapBatch(tbObat.getValueAt(i,19), tbObat.getValueAt(i,29), extras)) {
                            System.out.println("Lewati " + tbObat.getValueAt(i,4)
                                    + " : no.batch / tgl.kadaluarsa vaksin belum diisi");
                            tanpaBatch++;
                            continue;
                        }
                        String orgIhs = "";
                        try { orgIhs = koneksiDB.IDSATUSEHAT(); } catch(Exception ex){}
                        String doseStr = tbObat.getValueAt(i,22).toString().toLowerCase().replaceAll("dosis","").replaceAll(" ","");
                        boolean validDose = false;
                        try { int d = Integer.parseInt(doseStr); validDose = d > 0; } catch(Exception ex){}
                        StringBuilder vaxCoding = new StringBuilder();
                        vaxCoding.append("{")
                                .append("\"system\": \"").append(tbObat.getValueAt(i,10).toString()).append("\",")
                                .append("\"code\": \"").append(jsonEscape(tbObat.getValueAt(i,9).toString())).append("\",")
                                .append("\"display\": \"").append(jsonEscape(tbObat.getValueAt(i,12).toString())).append("\"")
                                .append("}");
                        if (extras.cvxGroupCode != null && !extras.cvxGroupCode.isEmpty()) {
                            vaxCoding.append(",{")
                                    .append("\"system\": \"http://terminology.hl7.org/CodeSystem/cvx-group\",")
                                    .append("\"code\": \"").append(jsonEscape(extras.cvxGroupCode)).append("\",")
                                    .append("\"display\": \"").append(jsonEscape(safe(extras.cvxGroupDisplay))).append("\"")
                                    .append("}");
                        }
                        if (extras.cvxNameCode != null && !extras.cvxNameCode.isEmpty()) {
                            vaxCoding.append(",{")
                                    .append("\"system\": \"http://hl7.org/fhir/sid/cvx\",")
                                    .append("\"code\": \"").append(jsonEscape(extras.cvxNameCode)).append("\",")
                                    .append("\"display\": \"").append(jsonEscape(safe(extras.cvxNameDisplay))).append("\"")
                                    .append("}");
                        }
                        String idempVal = tbObat.getValueAt(i,2).toString()+"|"+tbObat.getValueAt(i,20).toString()+"|"+tbObat.getValueAt(i,11).toString();
                        String routineTiming = (extras.routineTiming!=null && !extras.routineTiming.isEmpty() && !extras.routineTiming.equals("-"))
                                ? extras.routineTiming : "IM-Ideal";
                        String fundingCode = mapFundingSource(extras.kdPj);
                        String doseNumber = validDose ? doseStr
                                : (extras.doseNumber>0 ? String.valueOf(extras.doseNumber) : "");
                        StringBuilder protoApplied = new StringBuilder();
                        if (!doseNumber.isEmpty()
                                || (extras.seriesName!=null && !extras.seriesName.isEmpty())
                                || (extras.targetDiseaseCode!=null && !extras.targetDiseaseCode.isEmpty())) {
                            protoApplied.append(",\"protocolApplied\": [{");
                            boolean firstField = true;
                            if (extras.seriesName != null && !extras.seriesName.isEmpty()) {
                                protoApplied.append("\"series\": \"").append(jsonEscape(extras.seriesName)).append("\"");
                                firstField = false;
                            }
                            if (extras.targetDiseaseCode != null && !extras.targetDiseaseCode.isEmpty()) {
                                if (!firstField) protoApplied.append(",");
                                protoApplied.append("\"targetDisease\": [{\"coding\": [{")
                                        .append("\"system\": \"http://hl7.org/fhir/sid/icd-10\",")
                                        .append("\"code\": \"").append(jsonEscape(extras.targetDiseaseCode)).append("\",")
                                        .append("\"display\": \"").append(jsonEscape(safe(extras.targetDiseaseDisplay))).append("\"")
                                        .append("}]}]");
                                firstField = false;
                            }
                            if (!doseNumber.isEmpty()) {
                                if (!firstField) protoApplied.append(",");
                                protoApplied.append("\"doseNumberPositiveInt\": ").append(doseNumber);
                                firstField = false;
                            }
                            if (extras.seriesDoses > 0) {
                                if (!firstField) protoApplied.append(",");
                                protoApplied.append("\"seriesDosesPositiveInt\": ").append(extras.seriesDoses);
                            }
                            protoApplied.append("}]");
                        }
                        String siteFragment = "";
                        if (extras.site != null && !extras.site.isEmpty()) {
                            siteFragment = ",\"site\": {\"coding\": [{"
                                    + "\"system\": \"http://terminology.hl7.org/CodeSystem/v3-ActSite\","
                                    + "\"code\": \"" + jsonEscape(extras.site) + "\","
                                    + "\"display\": \"" + jsonEscape(siteDisplay(extras.site)) + "\""
                                    + "}]}";
                        }
                        String manufacturerFragment = "";
                        if (extras.manufacturerOrgIhs != null && !extras.manufacturerOrgIhs.isEmpty()) {
                            manufacturerFragment = ",\"manufacturer\": {\"reference\": \"Organization/"
                                    + jsonEscape(extras.manufacturerOrgIhs) + "\"}";
                        }
                        String fundingFragment = "";
                        if (fundingCode != null && !fundingCode.isEmpty()) {
                            fundingFragment = ",\"fundingSource\": {\"coding\": [{"
                                    + "\"system\": \"http://terminology.hl7.org/CodeSystem/immunization-funding-source\","
                                    + "\"code\": \"" + fundingCode + "\""
                                    + "}]}";
                        }
                        json = "{" +
                                    "\"resourceType\": \"Immunization\"," +
                                    "\"id\": \""+tbObat.getValueAt(i,27).toString()+"\"," +
                                    "\"identifier\": [{" +
                                        "\"system\": \"http://sys-ids.kemkes.go.id/immunization/"+orgIhs+"\"," +
                                        "\"value\": \""+jsonEscape(idempVal)+"\"" +
                                    "}]," +
                                    "\"status\": \"completed\"," +
                                    "\"vaccineCode\": {" +
                                        "\"coding\": [" + vaxCoding.toString() + "]" +
                                    "}," +
                                    "\"patient\": {" +
                                        "\"reference\": \"Patient/"+idpasien+"\"" +
                                    "}," +
                                    "\"encounter\": {" +
                                        "\"reference\": \"Encounter/"+tbObat.getValueAt(i,8).toString()+"\"" +
                                    "}," +
                                    "\"occurrenceDateTime\": \""+tbObat.getValueAt(i,20).toString()+"\"," +
                                    "\"recorded\": \""+tbObat.getValueAt(i,20).toString()+"\"," +
                                    "\"primarySource\": true," +
                                    "\"reasonCode\": [{\"coding\": [{" +
                                        "\"system\": \"http://terminology.kemkes.go.id/CodeSystem/immunization-routine-timing\"," +
                                        "\"code\": \""+routineTiming+"\"," +
                                        "\"display\": \""+(routineTiming.equals("IM-Kejar")?"Imunisasi Kejar":"Imunisasi Ideal")+"\"" +
                                    "}]}]," +
                                    "\"location\": {" +
                                        "\"reference\": \"Location/"+tbObat.getValueAt(i,23).toString()+"\"" +
                                    "}," +
                                    manufacturerSafeComma(manufacturerFragment) +
                                    lotDanKadaluarsa(tbObat.getValueAt(i,19), tbObat.getValueAt(i,29), extras) +
                                    "\"route\": {" +
                                        "\"coding\": [" +
                                            "{" +
                                                "\"system\": \""+tbObat.getValueAt(i,14).toString()+"\"," +
                                                "\"code\": \""+jsonEscape(tbObat.getValueAt(i,13).toString())+"\"," +
                                                "\"display\": \""+jsonEscape(tbObat.getValueAt(i,15).toString())+"\"" +
                                            "}" +
                                        "]" +
                                    "}," +
                                    "\"doseQuantity\": {" +
                                        "\"value\": "+tbObat.getValueAt(i,21).toString()+"," +
                                        "\"unit\": \""+jsonEscape(tbObat.getValueAt(i,18).toString())+"\"," +
                                        "\"system\": \""+tbObat.getValueAt(i,17).toString()+"\"," +
                                        "\"code\": \""+jsonEscape(tbObat.getValueAt(i,16).toString())+"\"" +
                                    "}," +
                                    "\"performer\": [" +
                                        "{" +
                                            "\"actor\": {" +
                                                "\"reference\": \"Practitioner/"+iddokter+"\"" +
                                            "}" +
                                        "}" +
                                    "]" +
                                    siteFragment +
                                    fundingFragment +
                                    protoApplied.toString() +
                                "}";
                        System.out.println("URL : "+link+"/Immunization/"+tbObat.getValueAt(i,27).toString());
                        System.out.println("Request JSON : "+json);
                        requestEntity = new HttpEntity(json,headers);
                        json=api.getRest().exchange(link+"/Immunization/"+tbObat.getValueAt(i,27).toString(), HttpMethod.PUT, requestEntity, String.class).getBody();
                        System.out.println("Result JSON : "+json);
                        tbObat.setValueAt(false,i,0);
                    }catch(Exception e){
                        System.out.println("Notifikasi Bridging : "+e);
                    }
                } catch (Exception e) {
                    System.out.println("Notifikasi : "+e);
                }
            }
        }
            if (tanpaBatch > 0) {
            javax.swing.JOptionPane.showMessageDialog(this, tanpaBatch
                    + " baris dilewati karena no.batch / tgl.kadaluarsa vaksin belum diisi.\n\n"
                    + "SatuSehat mewajibkan lotNumber & expirationDate pada Immunization.\n"
                    + "Lengkapi lewat form Data Imunisasi Pasien, lalu kirim ulang.");
        }
    }//GEN-LAST:event_BtnUpdateActionPerformed

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

    private void CmbStatusActionPerformed(java.awt.event.ActionEvent evt) {
        tampil();
    }

    /** Holder data tambahan untuk FHIR Immunization spec compliance (v2026.05.28.03).
     *  Diisi via {@link #loadFhirExtras(String, String, String)} per pemberian vaksin.
     *  Semua field optional — kalau NULL/empty, fragment JSON di-skip. */
    private static final class FhirExtras {
        String cvxGroupCode, cvxGroupDisplay;
        String cvxNameCode, cvxNameDisplay;
        String manufacturerOrgIhs;
        String targetDiseaseCode, targetDiseaseDisplay;
        String seriesName;
        int    seriesDoses;
        // dari imunisasi_pasien (kalau ada row)
        String lotNumber, expirationDate;   // batch & kadaluarsa hasil input petugas
        String site;
        int    doseNumber;
        String routineTiming;
        // dari reg_periksa
        String kdPj;
    }

    /** Fetch FHIR-spec extras dari satu_sehat_mapping_vaksin (CVX/manufacturer/targetDisease)
     *  + LEFT JOIN ke imunisasi_pasien (site/dose/routine) + reg_periksa.kd_pj (funding).
     *  Aman: kolom baru optional, kalau migration 063 belum dijalankan akan throw → return empty extras. */
    private FhirExtras loadFhirExtras(String kodeBrng, String noRawat, String tglJamIso) {
        FhirExtras x = new FhirExtras();
        // tglJamIso sudah digeser ke UTC (WIB-7) oleh IsoTime.toUtc, sedangkan
        // imunisasi_pasien.tgl_perawatan/jam menyimpan waktu LOKAL WIB. Meng-slice
        // string UTC apa adanya membuat join tidak pernah ketemu -> extras kosong ->
        // lotNumber/expirationDate ikut kosong dan semua baris dilewati.
        // Jadi kembalikan dulu ke WIB sebelum dipakai mencocokkan.
        String tglJamWib = (tglJamIso == null || tglJamIso.length() < 19) ? "" : wibDari(tglJamIso);
        PreparedStatement psX = null;
        ResultSet rsX = null;
        try {
            String sql =
                "select m.cvx_group_code, m.cvx_group_display, m.cvx_name_code, m.cvx_name_display, "
              + "       m.manufacturer_org_ihs, m.target_disease_code, m.target_disease_display, "
              + "       m.series_name, m.series_doses, "
              + "       ip.site as ip_site, ip.dose_number as ip_dose, ip.routine_timing as ip_routine, "
              + "       ip.no_batch as ip_batch, ip.tgl_kadaluarsa as ip_kadaluarsa, "
              + "       ip.manufacturer as ip_manufacturer, ip.funding_source as ip_funding, "
              + "       ip.target_disease_code as ip_target_code, ip.target_disease_display as ip_target_display, "
              + "       ip.series as ip_series, ip.series_doses as ip_series_doses, "
              + "       rp.kd_pj as kd_pj "
              + "from satu_sehat_mapping_vaksin m "
              + "left join reg_periksa rp on rp.no_rawat = ? "
              // Cocokkan per kunjungan + item; kalau satu kunjungan punya lebih dari satu
              // pemberian item yang sama, ambil yang jamnya paling dekat.
              + "left join imunisasi_pasien ip on ip.no_rawat = ? and ip.kode_brng = m.kode_brng "
              + "where m.kode_brng = ? "
              + "order by ifnull(abs(timestampdiff(second, concat(ip.tgl_perawatan,' ',ip.jam), ?)), 999999999) "
              + "limit 1";
            psX = koneksi.prepareStatement(sql);
            psX.setString(1, noRawat);
            psX.setString(2, noRawat);
            psX.setString(3, kodeBrng);
            psX.setString(4, tglJamWib);
            rsX = psX.executeQuery();
            if (rsX.next()) {
                x.cvxGroupCode        = rsX.getString("cvx_group_code");
                x.cvxGroupDisplay     = rsX.getString("cvx_group_display");
                x.cvxNameCode         = rsX.getString("cvx_name_code");
                x.cvxNameDisplay      = rsX.getString("cvx_name_display");
                x.manufacturerOrgIhs  = rsX.getString("manufacturer_org_ihs");
                // imunisasi_pasien.manufacturer adalah text bebas (bukan org IHS); skip jika mapping kosong
                // imunisasi_pasien punya target_disease lebih spesifik per pemberian → utamakan
                String ipTarget = rsX.getString("ip_target_code");
                if (ipTarget != null && !ipTarget.isEmpty()) {
                    x.targetDiseaseCode    = ipTarget;
                    x.targetDiseaseDisplay = rsX.getString("ip_target_display");
                } else {
                    x.targetDiseaseCode    = rsX.getString("target_disease_code");
                    x.targetDiseaseDisplay = rsX.getString("target_disease_display");
                }
                String ipSeries = rsX.getString("ip_series");
                if (ipSeries != null && !ipSeries.isEmpty()) {
                    x.seriesName = ipSeries;
                } else {
                    x.seriesName = rsX.getString("series_name");
                }
                int ipSeriesDoses = rsX.getInt("ip_series_doses");
                if (!rsX.wasNull() && ipSeriesDoses > 0) {
                    x.seriesDoses = ipSeriesDoses;
                } else {
                    int sd = rsX.getInt("series_doses");
                    if (!rsX.wasNull()) x.seriesDoses = sd;
                }
                x.lotNumber      = rsX.getString("ip_batch");
                x.expirationDate = rsX.getString("ip_kadaluarsa");
                x.site          = rsX.getString("ip_site");
                int dn = rsX.getInt("ip_dose");
                if (!rsX.wasNull()) x.doseNumber = dn;
                x.routineTiming = rsX.getString("ip_routine");
                x.kdPj          = rsX.getString("kd_pj");
            }
        } catch (Exception e) {
            // Migration 063 belum jalan, atau kolom belum ada, atau imunisasi_pasien belum ada.
            // Sender tetap jalan dengan extras kosong (fields cuma di-skip).
            System.out.println("loadFhirExtras (optional): "+e);
        } finally {
            try { if (rsX != null) rsX.close(); } catch(Exception ignored){}
            try { if (psX != null) psX.close(); } catch(Exception ignored){}
        }
        return x;
    }

    /** Map kd_pj (penjamin Khanza) ke kode FHIR fundingSource:
     *  - BPJS / public → "public"
     *  - Umum / asuransi swasta → "private"
     *  - Default (NULL/empty) → "" (fragment skip) */
    private static String mapFundingSource(String kdPj) {
        if (kdPj == null) return "";
        String k = kdPj.trim().toUpperCase();
        if (k.isEmpty()) return "";
        // 'A02' = kode BPJS lama, 'BPJ' = kode setelah migrasi. Terima keduanya.
        if (k.startsWith("BPJ") || k.equals("JKN") || k.equals("A02")) return "public";
        return "private";
    }

    /** Display name untuk HL7 v3 ActSite codes (LA/RA/LL/RL/BU/OT). */
    private static String siteDisplay(String site) {
        if (site == null) return "";
        switch (site.toUpperCase()) {
            case "LA": return "Left Arm";
            case "RA": return "Right Arm";
            case "LL": return "Left Leg / Thigh";
            case "RL": return "Right Leg / Thigh";
            case "BU": return "Buttock";
            case "OT": return "Other";
            default:   return site;
        }
    }

    /** Null-safe wrapper buat display strings (hindari "null" literal masuk JSON). */
    private static String safe(String s) { return s == null ? "" : s; }

    /** Pastikan manufacturer fragment yg di-emit di antara dua field selalu well-formed JSON.
     *  Kalau kosong → return "" (tidak ada koma stray); kalau ada → strip leading "," dan
     *  append trailing "," supaya bisa diapit antara location dan lotNumber. */
    private static String manufacturerSafeComma(String fragment) {
        if (fragment == null || fragment.isEmpty()) return "";
        // Input format: ",\"manufacturer\": {...}" — kita butuh "\"manufacturer\": {...},"
        if (fragment.startsWith(",")) fragment = fragment.substring(1);
        return fragment + ",";
    }

    /** Escape karakter untuk pembentukan JSON manual.
     *  Mencegah SatuSehat reject "Invalid JSON Payload Format" (Rule 20006)
     *  kalau nama vaksin/lotNumber mengandung TAB / newline / kutip ganda. */
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

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            SatuSehatKirimVaksin dialog = new SatuSehatKirimVaksin(new javax.swing.JFrame(), true);
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
    private widget.Button BtnUpdate;
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
    private widget.ComboBox CmbStatus;
    private widget.Label jLabel18;
    // End of variables declaration//GEN-END:variables
    
    private void tampil() {
        Valid.tabelKosong(tabMode);
        try{
            ps=koneksi.prepareStatement(
                   "select reg_periksa.tgl_registrasi,reg_periksa.jam_reg,reg_periksa.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.no_ktp,"+
                   "reg_periksa.stts,reg_periksa.status_lanjut,satu_sehat_encounter.id_encounter,satu_sehat_mapping_vaksin.vaksin_code,satu_sehat_mapping_vaksin.vaksin_system,"+
                   "satu_sehat_mapping_vaksin.kode_brng,satu_sehat_mapping_vaksin.vaksin_display,satu_sehat_mapping_vaksin.route_code,satu_sehat_mapping_vaksin.route_system,"+
                   "satu_sehat_mapping_vaksin.route_display,satu_sehat_mapping_vaksin.dose_quantity_code,satu_sehat_mapping_vaksin.dose_quantity_system,"+
                   "satu_sehat_mapping_vaksin.dose_quantity_unit,detail_pemberian_obat.no_batch,detail_pemberian_obat.tgl_perawatan,detail_pemberian_obat.jam,"+
                   "detail_pemberian_obat.jml,ifnull(aturan_pakai.aturan,'') as aturan,satu_sehat_mapping_lokasi_ralan.id_lokasi_satusehat,poliklinik.nm_poli,pegawai.nama,pegawai.no_ktp as ktppraktisi,"+
                   "ifnull(satu_sehat_immunization.id_immunization,'') as id_immunization,detail_pemberian_obat.no_faktur,ifnull(data_batch.tgl_kadaluarsa,'') as tgl_kadaluarsa from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                   "inner join satu_sehat_encounter on satu_sehat_encounter.no_rawat=reg_periksa.no_rawat "+
                   "inner join detail_pemberian_obat on detail_pemberian_obat.no_rawat=reg_periksa.no_rawat "+
                   "inner join satu_sehat_mapping_vaksin on satu_sehat_mapping_vaksin.kode_brng=detail_pemberian_obat.kode_brng "+
                   // Aturan pakai jarang diisi untuk vaksin -> jangan mematikan barisnya.
                   "left join aturan_pakai on aturan_pakai.tgl_perawatan=detail_pemberian_obat.tgl_perawatan and aturan_pakai.jam=detail_pemberian_obat.jam and "+
                   "aturan_pakai.no_rawat=detail_pemberian_obat.no_rawat and aturan_pakai.kode_brng=detail_pemberian_obat.kode_brng "+
                   "inner join satu_sehat_mapping_lokasi_ralan on satu_sehat_mapping_lokasi_ralan.kd_poli=reg_periksa.kd_poli "+
                   "inner join poliklinik on poliklinik.kd_poli=satu_sehat_mapping_lokasi_ralan.kd_poli "+
                   "inner join pegawai on reg_periksa.kd_dokter=pegawai.nik "+
                   "inner join nota_jalan on nota_jalan.no_rawat=reg_periksa.no_rawat "+
                   "left join data_batch on data_batch.no_batch=detail_pemberian_obat.no_batch and data_batch.kode_brng=detail_pemberian_obat.kode_brng and data_batch.no_faktur=detail_pemberian_obat.no_faktur "+
                   "left join satu_sehat_immunization on satu_sehat_immunization.no_rawat=detail_pemberian_obat.no_rawat and satu_sehat_immunization.tgl_perawatan=detail_pemberian_obat.tgl_perawatan and "+
                   "satu_sehat_immunization.jam=detail_pemberian_obat.jam and satu_sehat_immunization.kode_brng=detail_pemberian_obat.kode_brng and "+
                   "satu_sehat_immunization.no_batch=detail_pemberian_obat.no_batch and satu_sehat_immunization.no_faktur=detail_pemberian_obat.no_faktur "+
                   // lotNumber bukan field wajib FHIR Immunization -> syarat no_batch dilepas.
                   "where nota_jalan.tanggal between ? and ? "+
                   (TCari.getText().equals("")?"":"and (reg_periksa.no_rawat like ? or reg_periksa.no_rkm_medis like ? or "+
                   "pasien.nm_pasien like ? or pasien.no_ktp like ? or satu_sehat_mapping_vaksin.kode_brng like ? or satu_sehat_mapping_vaksin.vaksin_display like ? or "+
                   "reg_periksa.stts like ? or reg_periksa.status_lanjut like ?)")+
                   (CmbStatus.getSelectedItem().toString().equals("Belum Terkirim") ? " and ifnull(satu_sehat_immunization.id_immunization,'') = ''" : "") +
                   (CmbStatus.getSelectedItem().toString().equals("Sudah Terkirim") ? " and ifnull(satu_sehat_immunization.id_immunization,'') != ''" : "") +
                   " order by detail_pemberian_obat.tgl_perawatan,detail_pemberian_obat.jam");
            try {
                ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+""));
                ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+""));
                if(!TCari.getText().equals("")){
                    ps.setString(3,"%"+TCari.getText()+"%");
                    ps.setString(4,"%"+TCari.getText()+"%");
                    ps.setString(5,"%"+TCari.getText()+"%");
                    ps.setString(6,"%"+TCari.getText()+"%");
                    ps.setString(7,"%"+TCari.getText()+"%");
                    ps.setString(8,"%"+TCari.getText()+"%");
                    ps.setString(9,"%"+TCari.getText()+"%");
                    ps.setString(10,"%"+TCari.getText()+"%");
                }
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        false,fungsi.IsoTime.toUtc(rs.getString("tgl_registrasi"), rs.getString("jam_reg")),rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),
                        rs.getString("no_ktp"),rs.getString("stts"),rs.getString("status_lanjut"),rs.getString("id_encounter"),rs.getString("vaksin_code"),rs.getString("vaksin_system"),
                        rs.getString("kode_brng"),rs.getString("vaksin_display"),rs.getString("route_code"),rs.getString("route_system"),rs.getString("route_display"),rs.getString("dose_quantity_code"),
                        rs.getString("dose_quantity_system"),rs.getString("dose_quantity_unit"),rs.getString("no_batch"),fungsi.IsoTime.toUtc(rs.getString("tgl_perawatan"), rs.getString("jam")),
                        rs.getString("jml"),rs.getString("aturan"),rs.getString("id_lokasi_satusehat"),rs.getString("nm_poli"),rs.getString("nama"),rs.getString("ktppraktisi"),
                        rs.getString("id_immunization"),rs.getString("no_faktur"),rs.getString("tgl_kadaluarsa")
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
            
            ps=koneksi.prepareStatement(
                   "select reg_periksa.tgl_registrasi,reg_periksa.jam_reg,reg_periksa.no_rawat,reg_periksa.no_rkm_medis,pasien.nm_pasien,pasien.no_ktp,"+
                   "reg_periksa.stts,reg_periksa.status_lanjut,satu_sehat_encounter.id_encounter,satu_sehat_mapping_vaksin.vaksin_code,satu_sehat_mapping_vaksin.vaksin_system,"+
                   "satu_sehat_mapping_vaksin.kode_brng,satu_sehat_mapping_vaksin.vaksin_display,satu_sehat_mapping_vaksin.route_code,satu_sehat_mapping_vaksin.route_system,"+
                   "satu_sehat_mapping_vaksin.route_display,satu_sehat_mapping_vaksin.dose_quantity_code,satu_sehat_mapping_vaksin.dose_quantity_system,"+
                   "satu_sehat_mapping_vaksin.dose_quantity_unit,detail_pemberian_obat.no_batch,detail_pemberian_obat.tgl_perawatan,detail_pemberian_obat.jam,"+
                   "detail_pemberian_obat.jml,ifnull(aturan_pakai.aturan,'') as aturan,satu_sehat_mapping_lokasi_ranap.id_lokasi_satusehat,ifnull(poliklinik.nm_poli,'') as nm_poli,pegawai.nama,pegawai.no_ktp as ktppraktisi,"+
                   "ifnull(satu_sehat_immunization.id_immunization,'') as id_immunization,detail_pemberian_obat.no_faktur,ifnull(data_batch.tgl_kadaluarsa,'') as tgl_kadaluarsa from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                   "inner join satu_sehat_encounter on satu_sehat_encounter.no_rawat=reg_periksa.no_rawat "+
                   "inner join detail_pemberian_obat on detail_pemberian_obat.no_rawat=reg_periksa.no_rawat "+
                   "inner join satu_sehat_mapping_vaksin on satu_sehat_mapping_vaksin.kode_brng=detail_pemberian_obat.kode_brng "+
                   // Aturan pakai jarang diisi untuk vaksin -> jangan mematikan barisnya.
                   "left join aturan_pakai on aturan_pakai.tgl_perawatan=detail_pemberian_obat.tgl_perawatan and aturan_pakai.jam=detail_pemberian_obat.jam and "+
                   "aturan_pakai.no_rawat=detail_pemberian_obat.no_rawat and aturan_pakai.kode_brng=detail_pemberian_obat.kode_brng "+
                   // Lokasi ranap diambil dari bangsal kamar yang ditempati saat vaksin diberikan,
                   // bukan dari poli registrasi (bayi baru lahir registrasinya lewat IGD/poli ranap).
                   "inner join kamar_inap ki on ki.no_rawat=reg_periksa.no_rawat and ki.stts_pulang<>'Pindah Kamar' "+
                   "inner join satu_sehat_mapping_lokasi_ranap on satu_sehat_mapping_lokasi_ranap.kd_kamar=ki.kd_kamar "+
                   "left join poliklinik on poliklinik.kd_poli=reg_periksa.kd_poli "+
                   "inner join pegawai on reg_periksa.kd_dokter=pegawai.nik "+
                   "left join data_batch on data_batch.no_batch=detail_pemberian_obat.no_batch and data_batch.kode_brng=detail_pemberian_obat.kode_brng and data_batch.no_faktur=detail_pemberian_obat.no_faktur "+
                   "left join satu_sehat_immunization on satu_sehat_immunization.no_rawat=detail_pemberian_obat.no_rawat and satu_sehat_immunization.tgl_perawatan=detail_pemberian_obat.tgl_perawatan and "+
                   "satu_sehat_immunization.jam=detail_pemberian_obat.jam and satu_sehat_immunization.kode_brng=detail_pemberian_obat.kode_brng and "+
                   "satu_sehat_immunization.no_batch=detail_pemberian_obat.no_batch and satu_sehat_immunization.no_faktur=detail_pemberian_obat.no_faktur "+
                   "where detail_pemberian_obat.tgl_perawatan between ? and ? "+
                   (TCari.getText().equals("")?"":"and (reg_periksa.no_rawat like ? or reg_periksa.no_rkm_medis like ? or "+
                   "pasien.nm_pasien like ? or pasien.no_ktp like ? or satu_sehat_mapping_vaksin.kode_brng like ? or satu_sehat_mapping_vaksin.vaksin_display like ? or "+
                   "reg_periksa.stts like ? or reg_periksa.status_lanjut like ?)")+
                   (CmbStatus.getSelectedItem().toString().equals("Belum Terkirim") ? " and ifnull(satu_sehat_immunization.id_immunization,'') = ''" : "") +
                   (CmbStatus.getSelectedItem().toString().equals("Sudah Terkirim") ? " and ifnull(satu_sehat_immunization.id_immunization,'') != ''" : "") +
                   " order by detail_pemberian_obat.tgl_perawatan,detail_pemberian_obat.jam");
            try {
                ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+""));
                ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+""));
                if(!TCari.getText().equals("")){
                    ps.setString(3,"%"+TCari.getText()+"%");
                    ps.setString(4,"%"+TCari.getText()+"%");
                    ps.setString(5,"%"+TCari.getText()+"%");
                    ps.setString(6,"%"+TCari.getText()+"%");
                    ps.setString(7,"%"+TCari.getText()+"%");
                    ps.setString(8,"%"+TCari.getText()+"%");
                    ps.setString(9,"%"+TCari.getText()+"%");
                    ps.setString(10,"%"+TCari.getText()+"%");
                }
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        false,fungsi.IsoTime.toUtc(rs.getString("tgl_registrasi"), rs.getString("jam_reg")),rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),
                        rs.getString("no_ktp"),rs.getString("stts"),rs.getString("status_lanjut"),rs.getString("id_encounter"),rs.getString("vaksin_code"),rs.getString("vaksin_system"),
                        rs.getString("kode_brng"),rs.getString("vaksin_display"),rs.getString("route_code"),rs.getString("route_system"),rs.getString("route_display"),rs.getString("dose_quantity_code"),
                        rs.getString("dose_quantity_system"),rs.getString("dose_quantity_unit"),rs.getString("no_batch"),fungsi.IsoTime.toUtc(rs.getString("tgl_perawatan"), rs.getString("jam")),
                        rs.getString("jml"),rs.getString("aturan"),rs.getString("id_lokasi_satusehat"),rs.getString("nm_poli"),rs.getString("nama"),rs.getString("ktppraktisi"),
                        rs.getString("id_immunization"),rs.getString("no_faktur"),rs.getString("tgl_kadaluarsa")
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
            System.out.println("Notifikasi : "+e);
        }
        LCount.setText(""+tabMode.getRowCount());
    }

    public void isCek(){
        BtnKirim.setEnabled(akses.getsatu_sehat_kirim_Immunization());
        BtnPrint.setEnabled(akses.getsatu_sehat_kirim_Immunization());
    }
    
    public JTable getTable(){
        return tbObat;
    }

    /** IHS pasien; bila NIK kosong (bayi baru lahir) ambil dari satu_sehat_bayi_baru_lahir. */
    private String ihsPasienAtauBayi(String nik, String noRawat) {
        String n = nik == null ? "" : nik.trim();
        if (n.matches("\\d{16}")) {
            return cekViaSatuSehat.tampilIDPasien(n);
        }
        String ihsBayi = Sequel.cariIsi(
                "select ifnull(ihs_bayi,'') from satu_sehat_bayi_baru_lahir where no_rawat=?", noRawat);
        if (ihsBayi != null && !ihsBayi.trim().isEmpty()) {
            return ihsBayi.trim();
        }
        return cekViaSatuSehat.tampilIDPasien(n);
    }

    /**
     * Potongan JSON lotNumber + expirationDate.
     *
     * Ternyata SatuSehat MEWAJIBKAN keduanya untuk Immunization (Rule 10306 &
     * 10307 berbunyi "Element not found" saat field dihilangkan). Sumbernya:
     * detail_pemberian_obat.no_batch + data_batch.tgl_kadaluarsa; bila kosong
     * dipakai isian petugas di form Data Imunisasi Pasien (imunisasi_pasien).
     * Kalau tetap kosong, baris tidak dikirim — lihat lengkapBatch().
     */
    private String lotDanKadaluarsa(Object lot, Object kadaluarsa, FhirExtras extras) {
        String l = pilihIsi(lot, extras == null ? null : extras.lotNumber);
        String k = tanggalValid(pilihIsi(kadaluarsa, extras == null ? null : extras.expirationDate));
        return "\"lotNumber\": \"" + jsonEscape(l) + "\","
             + "\"expirationDate\": \"" + k + "\",";
    }

    /** true bila batch & kadaluarsa lengkap sehingga baris layak dikirim. */
    private boolean lengkapBatch(Object lot, Object kadaluarsa, FhirExtras extras) {
        return !pilihIsi(lot, extras == null ? null : extras.lotNumber).isEmpty()
            && !tanggalValid(pilihIsi(kadaluarsa, extras == null ? null : extras.expirationDate)).isEmpty();
    }

    private String pilihIsi(Object utama, String cadangan) {
        String u = utama == null ? "" : utama.toString().trim();
        if (!u.isEmpty()) {
            return u;
        }
        return cadangan == null ? "" : cadangan.trim();
    }

    /** Kebalikan IsoTime.toUtc: ISO UTC → "yyyy-MM-dd HH:mm:ss" waktu WIB.
     *  Dipakai untuk mencocokkan / menyimpan ke tabel Khanza yang semuanya
     *  menyimpan waktu lokal. Kalau formatnya tak terduga, kembalikan input
     *  apa adanya (dengan T diganti spasi) supaya pemanggil tidak crash. */
    private String wibDari(Object iso) {
        String s = iso == null ? "" : iso.toString().trim();
        try {
            return java.time.OffsetDateTime.parse(s)
                    .atZoneSameInstant(java.time.ZoneId.of("Asia/Jakarta"))
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } catch (Exception e) {
            return s.replace("T", " ");
        }
    }

    /** Kembalikan yyyy-MM-dd bila wajar, selain itu string kosong. */
    private String tanggalValid(String t) {
        if (t == null || t.length() < 10) {
            return "";
        }
        String d = t.substring(0, 10);
        return (d.matches("\\d{4}-\\d{2}-\\d{2}") && !d.startsWith("0000")) ? d : "";
    }

}
