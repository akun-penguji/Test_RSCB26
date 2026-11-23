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
import java.io.FileReader;
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

/**
 *
 * @author dosen
 */
public final class SatuSehatKirimAllergyIntolerance extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private Connection koneksi=koneksiDB.condb();
    private PreparedStatement ps;
    private ResultSet rs;   
    private int i=0;
    private String link="",json="",idpasien="",idpraktisi="";
    private ApiSatuSehat api=new ApiSatuSehat();
    private HttpHeaders headers ;
    private HttpEntity requestEntity;
    private ObjectMapper mapper = new ObjectMapper();
    private JsonNode root;
    private JsonNode response;
    private SatuSehatCekNIK cekViaSatuSehat=new SatuSehatCekNIK();
    private StringBuilder htmlContent;
    private widget.ComboBox CmbSumber;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private volatile boolean ceksukses = false; 
    
    /** Creates new form DlgKamar
     * @param parent
     * @param modal */
    public SatuSehatKirimAllergyIntolerance(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.setLocation(10,2);
        setSize(628,674);

        tabMode=new DefaultTableModel(null,new String[]{
                "P","Tanggal Registrasi","No.Rawat","No.RM","Nama Pasien","No.KTP Pasien","ID Encounter",
                "Alergi","Petugas/Praktisi","No.KTP Praktisi","Tanggal","ID Allergy Intolerance",
                "Status",
                // Kolom tambahan untuk mode "Terstruktur" (dari tabel alergi_pasien)
                "AllergyCode","AllergyDisplay","Category","ReactionCode","ReactionDisplay","Severity","Note","Sumber"
            }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if (colIndex==0) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                 java.lang.Boolean.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                 java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                 java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                 java.lang.String.class,
                 // tambahan
                 java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class,
                 java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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

        for (i = 0; i < 21; i++) {
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
                column.setPreferredWidth(215);
            }else if(i==7){
                column.setPreferredWidth(250);
            }else if(i==8){
                column.setPreferredWidth(150);
            }else if(i==9){
                column.setPreferredWidth(120);
            }else if(i==10){
                column.setPreferredWidth(110);
            }else if(i==11){
                column.setPreferredWidth(220);
            }else if(i==12){
                column.setPreferredWidth(60);
            }else if(i==20){
                column.setPreferredWidth(80);  // Sumber (Terstruktur/Legacy)
            }else{
                // 13..19 — kolom data struktural, hidden dari UI tapi dipakai builder JSON
                column.setMinWidth(0);
                column.setMaxWidth(0);
                column.setPreferredWidth(0);
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());

        TCari.setDocument(new batasInput((byte)100).getKata(TCari));

        // ComboBox sumber data: Terstruktur (alergi_pasien) vs Legacy (pemeriksaan_ralan.alergi)
        CmbSumber = new widget.ComboBox();
        CmbSumber.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{
            "Alergi Terstruktur","Alergi Text (Legacy)"
        }));
        CmbSumber.setPreferredSize(new Dimension(155,23));
        CmbSumber.setToolTipText("Sumber data: 'Terstruktur' baca dari tabel alergi_pasien (lengkap dgn reaction & severity); 'Legacy' baca dari kolom pemeriksaan_ralan.alergi");
        CmbSumber.addActionListener((java.awt.event.ActionEvent e) -> tampil());
        if (panelGlass9 != null) {
            panelGlass9.add(new widget.Label() {{ setText("Sumber:"); setPreferredSize(new Dimension(55,23)); }});
            panelGlass9.add(CmbSumber);
            panelGlass9.revalidate();
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
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Pengiriman Allergy Intolerance Satu Sehat ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
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

        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "06-04-2026" }));
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

        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "06-04-2026" }));
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
        try{
            htmlContent = new StringBuilder();
            htmlContent.append(                             
                "<tr class='isi'>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Tanggal Registrasi</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.Rawat</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.RM</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Nama Pasien</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.KTP Pasien</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>ID Encounter</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Alergi</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Petugas/Praktisi</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>No.KTP Praktisi</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>Tanggal</b></td>"+
                    "<td valign='middle' bgcolor='#FFFAFA' align='center'><b>ID Allergy Intolerance</b></td>"+
                "</tr>"
            );
            for (i = 0; i < tabMode.getRowCount(); i++) {
                htmlContent.append(
                    "<tr class='isi'>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,1).toString()+"</td>"+
                        "<td valign='top'>"+jsonEscape(tbObat.getValueAt(i,2).toString())+"</td>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,3).toString()+"</td>"+
                        "<td valign='top'>"+jsonEscape(tbObat.getValueAt(i,4).toString())+"</td>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,5).toString()+"</td>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,6).toString()+"</td>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,7).toString()+"</td>"+
                        "<td valign='top'>"+jsonEscape(tbObat.getValueAt(i,8).toString())+"</td>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,9).toString()+"</td>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,10).toString()+"</td>"+
                        "<td valign='top'>"+tbObat.getValueAt(i,11).toString()+"</td>"+
                    "</tr>");
            }
            LoadHTML.setText(
                "<html>"+
                  "<table width='1500px' border='0' align='center' cellpadding='1px' cellspacing='0' class='tbl_form'>"+
                   htmlContent.toString()+
                  "</table>"+
                "</html>"
            );
            htmlContent=null;

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

            File f = new File("DataSatuSehatAllergyIntolerance.html");            
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));            
            bw.write(LoadHTML.getText().replaceAll("<head>","<head>"+
                        "<link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" />"+
                        "<table width='1700px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                            "<tr class='isi2'>"+
                                "<td valign='top' align='center'>"+
                                    "<font size='4' face='Tahoma'>"+akses.getnamars()+"</font><br>"+
                                    akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs()+"<br>"+
                                    akses.getkontakrs()+", E-mail : "+akses.getemailrs()+"<br><br>"+
                                    "<font size='2' face='Tahoma'>DATA PENGIRIMAN SATU SEHAT ALLERGY INTOLERANCE<br><br></font>"+        
                                "</td>"+
                           "</tr>"+
                        "</table>")
            );
            bw.close();                         
            Desktop.getDesktop().browse(f.toURI());
        }catch(Exception e){
            System.out.println("Notifikasi : "+e);
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
        runBackground(() ->tampil());
    }//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt,TCari,BtnPrint);
        }
    }//GEN-LAST:event_BtnCariKeyPressed

    private void BtnKirimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKirimActionPerformed
        // Cek file cache hanya kalau ada baris Legacy yg akan dikirim
        // (mode Terstruktur tidak butuh cacheFile, code SNOMED sudah dari DB).
        File cacheFile = new File("./cache/alergisatusehat.iyem");
        boolean adaLegacy = false;
        for (int r = 0; r < tbObat.getRowCount(); r++) {
            if (tbObat.getValueAt(r, 0).toString().equals("true")
                    && "Legacy".equals(String.valueOf(tbObat.getValueAt(r, 20)))) {
                adaLegacy = true; break;
            }
        }
        if (adaLegacy && !cacheFile.exists()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "File cache alergi tidak ditemukan:\n" + cacheFile.getAbsolutePath() +
                "\nDibutuhkan untuk lookup SNOMED dari mode Legacy.",
                "File Cache Tidak Ada", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Cek apakah ada baris yang diceklis
        boolean adaYangDipilih = false;
        for (i = 0; i < tbObat.getRowCount(); i++) {
            if (tbObat.getValueAt(i, 0).toString().equals("true")) { adaYangDipilih = true; break; }
        }
        if (!adaYangDipilih) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Belum ada data yang dipilih.\nSilakan centang baris yang ingin dikirim.",
                "Tidak Ada Pilihan", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int berhasil = 0, gagal = 0, dilewati = 0;
        StringBuilder pesanLewati = new StringBuilder();

        for(i=0;i<tbObat.getRowCount();i++){
            if(!tbObat.getValueAt(i,0).toString().equals("true")) continue;

            String noRawat  = tbObat.getValueAt(i,2).toString();
            String nmPasien = tbObat.getValueAt(i,4).toString();
            String noKtpPasien  = tbObat.getValueAt(i,5).toString();
            String idEncounter  = tbObat.getValueAt(i,6).toString();
            String noKtpPraktisi= tbObat.getValueAt(i,9).toString();
            String idAllergy    = tbObat.getValueAt(i,11).toString();
            String sumber       = String.valueOf(tbObat.getValueAt(i,20));

            // Validasi per-baris
            if (!idAllergy.equals("")) {
                pesanLewati.append("- ").append(nmPasien).append(" (").append(noRawat).append("): sudah terkirim\n");
                dilewati++; continue;
            }
            if (noKtpPasien.equals("")) {
                pesanLewati.append("- ").append(nmPasien).append(" (").append(noRawat).append("): No. KTP pasien kosong\n");
                dilewati++; continue;
            }
            if (idEncounter.equals("")) {
                pesanLewati.append("- ").append(nmPasien).append(" (").append(noRawat).append("): ID Encounter kosong, kirim Encounter dulu\n");
                dilewati++; continue;
            }
            if (noKtpPraktisi.equals("")) {
                pesanLewati.append("- ").append(nmPasien).append(" (").append(noRawat).append("): No. KTP Praktisi kosong\n");
                dilewati++; continue;
            }

            // === MODE TERSTRUKTUR (sumber=alergi_pasien) ===
            if ("Terstruktur".equals(sumber)) {
                String allergyCode = tbObat.getValueAt(i,13).toString();
                if (allergyCode.isEmpty()) {
                    pesanLewati.append("- ").append(nmPasien).append(" (").append(noRawat).append("): allergy_code kosong di alergi_pasien\n");
                    dilewati++; continue;
                }
                try {
                    idpraktisi = cekViaSatuSehat.tampilIDParktisi(noKtpPraktisi);
                    idpasien   = cekViaSatuSehat.tampilIDPasien(noKtpPasien);
                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
                    json = buildAllergyJsonTerstruktur(i, idpasien, idpraktisi, null);
                    System.out.println("URL : "+link+"/AllergyIntolerance");
                    System.out.println("Request JSON : "+json);
                    requestEntity = new HttpEntity(json, headers);
                    json = api.getRest().exchange(link+"/AllergyIntolerance", HttpMethod.POST, requestEntity, String.class).getBody();
                    System.out.println("Result JSON : "+json);
                    root = mapper.readTree(json);
                    response = root.path("id");
                    if (!response.asText().equals("")) {
                        if (Sequel.menyimpantf2("satu_sehat_allergy_intolerance","?,?,?,?,?","Rencana Perawatan",5,new String[]{
                                noRawat,
                                tbObat.getValueAt(i,10).toString().substring(0, Math.min(10, tbObat.getValueAt(i,10).toString().length())),
                                tbObat.getValueAt(i,10).toString().length()>=19 ? tbObat.getValueAt(i,10).toString().substring(11,19) : "00:00:00",
                                tbObat.getValueAt(i,12).toString(),
                                response.asText()
                        })){
                            tbObat.setValueAt(response.asText(), i, 11);
                            tbObat.setValueAt(false, i, 0);
                            berhasil++;
                        }
                    } else {
                        gagal++;
                    }
                } catch (Exception ex) {
                    gagal++;
                    System.out.println("Notifikasi Bridging Terstruktur : "+ex);
                }
                continue;
            }

            // === MODE LEGACY (sumber=pemeriksaan_*.alergi) — pakai cacheFile lookup ===
            FileReader myObj=null;
            try {
                String dicari=tbObat.getValueAt(i,7).toString().replaceAll("(\r\n|\r|\n|\n\r)","").replaceAll("\t", ""),
                       category="",coding_system="",coding_code="",coding_display="",text="";
                myObj = new FileReader(cacheFile);
                root = mapper.readTree(myObj);
                response = root.path("alergi");
                if(response.isArray()){
                    for(JsonNode list:response){
                        if(dicari.toLowerCase().contains(list.path("keyword").asText().toLowerCase())){
                            category=list.path("category").asText();
                            coding_system=list.path("coding_system").asText();
                            coding_code=list.path("coding_code").asText();
                            coding_display=list.path("coding_display").asText();
                            text=list.path("text").asText();
                            break;
                        }
                    }
                }
                if(category.equals("")){
                    pesanLewati.append("- ").append(nmPasien).append(" (").append(noRawat).append("): alergi \"").append(dicari).append("\" tidak ditemukan di referensi\n");
                    dilewati++;
                } else {
                    idpraktisi=cekViaSatuSehat.tampilIDParktisi(noKtpPraktisi);
                    idpasien=cekViaSatuSehat.tampilIDPasien(noKtpPasien);
                    try{
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
                        json = "{" +
                                    "\"resourceType\": \"AllergyIntolerance\"," +
                                    "\"identifier\": [" +
                                        "{" +
                                            "\"system\": \"http://sys-ids.kemkes.go.id/allergy/"+koneksiDB.IDSATUSEHAT()+"\"," +
                                            "\"value\" : \""+jsonEscape(tbObat.getValueAt(i,2).toString())+"\"" +
                                        "}" +
                                    "]," +
                                    "\"clinicalStatus\": {" +
                                        "\"coding\": [" +
                                            "{" +
                                                "\"system\": \"http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical\"," +
                                                "\"code\": \"active\"," +
                                                "\"display\": \"Active\"" +
                                            "}" +
                                        "]" +
                                    "}," +
                                    "\"verificationStatus\": {" +
                                        "\"coding\": [" +
                                            "{" +
                                                "\"system\": \"http://terminology.hl7.org/CodeSystem/allergyintolerance-verification\"," +
                                                "\"code\": \"confirmed\"," +
                                                "\"display\": \"Confirmed\"" +
                                            "}" +
                                        "]" +
                                    "}," +
                                    "\"category\": [" +
                                        "\""+category+"\"" +
                                    "]," +
                                    "\"code\": {" +
                                        "\"coding\": [" +
                                            "{" +
                                                "\"system\": \""+coding_system+"\"," +
                                                "\"code\": \""+jsonEscape(coding_code)+"\"," +
                                                "\"display\": \""+jsonEscape(coding_display)+"\"" +
                                            "}" +
                                        "]," +
                                        "\"text\": \""+jsonEscape(text)+"\"" +
                                    "},"+
                                    "\"patient\": {" +
                                        "\"reference\" : \"Patient/"+idpasien+"\"," +
                                        "\"display\" : \""+jsonEscape(tbObat.getValueAt(i,4).toString())+"\"" +
                                    "}," +
                                    "\"encounter\" : {" +
                                        "\"reference\" : \"Encounter/"+idEncounter+"\","+
                                        "\"display\" : \"Kunjungan "+jsonEscape(tbObat.getValueAt(i,4).toString())+" pada tanggal "+tbObat.getValueAt(i,1).toString()+" dengan nomor kunjungan "+jsonEscape(tbObat.getValueAt(i,2).toString())+"\""+
                                    "}," +
                                    "\"recordedDate\": \""+fungsi.IsoTime.toUtc(tbObat.getValueAt(i,10).toString())+"\"," +
                                    "\"recorder\": {" +
                                        "\"reference\" : \"Practitioner/"+idpraktisi+"\"," +
                                        "\"display\" : \""+jsonEscape(tbObat.getValueAt(i,8).toString())+"\"" +
                                    "}" +
                                "}";
                        System.out.println("URL : "+link+"/AllergyIntolerance");
                        System.out.println("Request JSON : "+json);
                        requestEntity = new HttpEntity(json,headers);
                        json=api.getRest().exchange(link+"/AllergyIntolerance", HttpMethod.POST, requestEntity, String.class).getBody();
                        System.out.println("Result JSON : "+json);
                        root = mapper.readTree(json);
                        response = root.path("id");
                        if(!response.asText().equals("")){
                            if(Sequel.menyimpantf2("satu_sehat_allergy_intolerance","?,?,?,?,?","Rencana Perawatan",5,new String[]{
                                tbObat.getValueAt(i,2).toString(),tbObat.getValueAt(i,10).toString().substring(0,10),tbObat.getValueAt(i,10).toString().substring(11,19),tbObat.getValueAt(i,12).toString(),response.asText()
                            })==true){
                                tbObat.setValueAt(response.asText(),i,11);
                                tbObat.setValueAt(false,i,0);
                                berhasil++;
                            }
                        } else {
                            String errMsg = root.path("issue").path(0).path("diagnostics").asText();
                            if (errMsg.isEmpty()) errMsg = json;
                            pesanLewati.append("- ").append(nmPasien).append(" (").append(noRawat).append("): API error - ").append(errMsg).append("\n");
                            gagal++;
                        }
                    }catch(Exception e){
                        pesanLewati.append("- ").append(nmPasien).append(" (").append(noRawat).append("): ").append(e.getMessage()).append("\n");
                        gagal++;
                        System.out.println("Notifikasi Bridging : "+e);
                    }
                }
                myObj.close();
            } catch (Exception e) {
                pesanLewati.append("- ").append(nmPasien).append(" (").append(noRawat).append("): ").append(e.getMessage()).append("\n");
                gagal++;
                System.out.println("Notifikasi : "+e);
            }finally {
                if (myObj != null) try { myObj.close(); } catch (Exception e) {}
                response = null;
                root = null;
            }
        }

        // Tampilkan ringkasan hasil
        StringBuilder ringkasan = new StringBuilder();
        ringkasan.append("Berhasil dikirim : ").append(berhasil).append("\n");
        if (gagal > 0) ringkasan.append("Gagal             : ").append(gagal).append("\n");
        if (dilewati > 0) ringkasan.append("Dilewati          : ").append(dilewati).append("\n");
        if (pesanLewati.length() > 0) {
            ringkasan.append("\nDetail:\n").append(pesanLewati);
        }
        javax.swing.JOptionPane.showMessageDialog(this, ringkasan.toString(),
            berhasil > 0 ? "Kirim Selesai" : "Tidak Ada Data Terkirim",
            berhasil > 0 ? javax.swing.JOptionPane.INFORMATION_MESSAGE : javax.swing.JOptionPane.WARNING_MESSAGE);
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
        for(i=0;i<tbObat.getRowCount();i++){
            if(tbObat.getValueAt(i,0).toString().equals("true")&&(!tbObat.getValueAt(i,5).toString().equals(""))&&(!tbObat.getValueAt(i,6).toString().equals(""))&&(!tbObat.getValueAt(i,9).toString().equals(""))&&(!tbObat.getValueAt(i,11).toString().equals(""))){
                String sumber = String.valueOf(tbObat.getValueAt(i,20));
                // === MODE TERSTRUKTUR ===
                if ("Terstruktur".equals(sumber)) {
                    String allergyCode = tbObat.getValueAt(i,13).toString();
                    if (allergyCode.isEmpty()) {
                        System.out.println("Skip update Terstruktur (no allergy_code) row "+i);
                        continue;
                    }
                    try {
                        idpraktisi = cekViaSatuSehat.tampilIDParktisi(tbObat.getValueAt(i,9).toString());
                        idpasien   = cekViaSatuSehat.tampilIDPasien(tbObat.getValueAt(i,5).toString());
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
                        String idExisting = tbObat.getValueAt(i,11).toString();
                        json = buildAllergyJsonTerstruktur(i, idpasien, idpraktisi, idExisting);
                        System.out.println("URL : "+link+"/AllergyIntolerance/"+idExisting);
                        System.out.println("Request JSON : "+json);
                        requestEntity = new HttpEntity(json, headers);
                        json = api.getRest().exchange(link+"/AllergyIntolerance/"+idExisting, HttpMethod.PUT, requestEntity, String.class).getBody();
                        System.out.println("Result JSON : "+json);
                        tbObat.setValueAt(false, i, 0);
                    } catch (Exception ex) {
                        System.out.println("Notifikasi Bridging Update Terstruktur : "+ex);
                    }
                    continue;
                }
                // === MODE LEGACY ===
                FileReader myObj=null;
                try {
                    String dicari=tbObat.getValueAt(i,7).toString().replaceAll("(\r\n|\r|\n|\n\r)","").replaceAll("\t", ""),
                           category="",coding_system="",coding_code="",coding_display="",text="";
                    myObj = new FileReader("./cache/alergisatusehat.iyem");
                    root = mapper.readTree(myObj);
                    response = root.path("alergi");
                    if(response.isArray()){
                        for(JsonNode list:response){
                            if(dicari.toLowerCase().contains(list.path("keyword").asText().toLowerCase())){
                                category=list.path("category").asText();
                                coding_system=list.path("coding_system").asText();
                                coding_code=list.path("coding_code").asText();
                                coding_display=list.path("coding_display").asText();
                                text=list.path("text").asText();
                                break;
                            }
                        }
                    }
                    if(!category.equals("")){
                        idpraktisi=cekViaSatuSehat.tampilIDParktisi(tbObat.getValueAt(i,9).toString());
                        idpasien=cekViaSatuSehat.tampilIDPasien(tbObat.getValueAt(i,5).toString());
                        try{
                            headers = new HttpHeaders();
                            headers.setContentType(MediaType.APPLICATION_JSON);
                            headers.add("Authorization", "Bearer "+api.TokenSatuSehat());
                            json = "{" +
                                        "\"resourceType\": \"AllergyIntolerance\"," +
                                        "\"id\": \""+tbObat.getValueAt(i,11).toString()+"\"," +
                                        "\"identifier\": [" +
                                            "{" +
                                                "\"system\": \"http://sys-ids.kemkes.go.id/allergy/"+koneksiDB.IDSATUSEHAT()+"\"," +
                                                "\"value\" : \""+jsonEscape(tbObat.getValueAt(i,2).toString())+"\"" +
                                            "}" +
                                        "]," +
                                        "\"clinicalStatus\": {" +
                                            "\"coding\": [" +
                                                "{" +
                                                    "\"system\": \"http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical\"," +
                                                    "\"code\": \"active\"," +
                                                    "\"display\": \"Active\"" +
                                                "}" +
                                            "]" +
                                        "}," +
                                        "\"verificationStatus\": {" +
                                            "\"coding\": [" +
                                                "{" +
                                                    "\"system\": \"http://terminology.hl7.org/CodeSystem/allergyintolerance-verification\"," +
                                                    "\"code\": \"confirmed\"," +
                                                    "\"display\": \"Confirmed\"" +
                                                "}" +
                                            "]" +
                                        "}," +
                                        "\"category\": [" +
                                            "\""+category+"\"" +
                                        "]," +
                                        "\"code\": {" +
                                            "\"coding\": [" +
                                                "{" +
                                                    "\"system\": \""+coding_system+"\"," +
                                                    "\"code\": \""+jsonEscape(coding_code)+"\"," +
                                                    "\"display\": \""+jsonEscape(coding_display)+"\"" +
                                                "}" +
                                            "]," +
                                            "\"text\": \""+jsonEscape(text)+"\"" +
                                        "},"+
                                        "\"patient\": {" +
                                            "\"reference\" : \"Patient/"+idpasien+"\"," +
                                            "\"display\" : \""+jsonEscape(tbObat.getValueAt(i,4).toString())+"\"" +
                                        "}," +
                                        "\"encounter\" : {" +
                                            "\"reference\" : \"Encounter/"+tbObat.getValueAt(i,6).toString()+"\","+
                                            "\"display\" : \"Kunjungan "+jsonEscape(tbObat.getValueAt(i,4).toString())+" pada tanggal "+tbObat.getValueAt(i,1).toString()+" dengan nomor kunjungan "+jsonEscape(tbObat.getValueAt(i,2).toString())+"\""+
                                        "}," +
                                        "\"recordedDate\": \""+fungsi.IsoTime.toUtc(tbObat.getValueAt(i,10).toString())+"\"," +
                                        "\"recorder\": {" +
                                            "\"reference\" : \"Practitioner/"+idpraktisi+"\"," +
                                            "\"display\" : \""+jsonEscape(tbObat.getValueAt(i,8).toString())+"\"" +
                                        "}" +
                                    "}";
                            System.out.println("URL : "+link+"/AllergyIntolerance/"+tbObat.getValueAt(i,11).toString());
                            System.out.println("Request JSON : "+json);
                            requestEntity = new HttpEntity(json,headers);
                            json=api.getRest().exchange(link+"/AllergyIntolerance/"+tbObat.getValueAt(i,11).toString(), HttpMethod.PUT, requestEntity, String.class).getBody();
                            System.out.println("Result JSON : "+json);
                            tbObat.setValueAt(false,i,0);
                        }catch(Exception e){
                            System.out.println("Notifikasi Bridging : "+e);
                        }
                    }
                    myObj.close();
                } catch (Exception e) {
                    System.out.println("Notifikasi : "+e);
                }finally {
                    if (myObj != null) try { myObj.close(); } catch (Exception e) {}
                    response = null;
                    root = null;
                }
            }
        }
    }//GEN-LAST:event_BtnUpdateActionPerformed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        runBackground(() ->tampil());
    }//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            TCari.setText("");
            runBackground(() ->tampil());
        }else{
            Valid.pindah(evt, BtnPrint, BtnKeluar);
        }
    }//GEN-LAST:event_BtnAllKeyPressed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        runBackground(() ->tampil());
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        runBackground(() ->tampil());
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        runBackground(() ->tampil());
                    }
                }
            });
        } 
    }//GEN-LAST:event_formWindowOpened

    /** Map Khanza alergi_pasien.category ke enum FHIR category. */
    private static String mapCategoryToFhir(String khanza) {
        if (khanza == null) return "medication";
        String c = khanza.trim().toLowerCase();
        switch (c) {
            case "medication": case "obat":      return "medication";
            case "food":       case "makanan":   return "food";
            case "environment":case "lingkungan":return "environment";
            case "biologic":   case "biologis":  return "biologic";
            default: return c.isEmpty() ? "medication" : c;
        }
    }

    /** Map Khanza severity ke FHIR reaction.severity (mild|moderate|severe). */
    private static String mapSeverityToFhir(String khanza) {
        if (khanza == null) return "";
        switch (khanza.trim().toLowerCase()) {
            case "low":  case "mild":     return "mild";
            case "moderate":              return "moderate";
            case "high": case "severe":   return "severe";
            default: return "";
        }
    }

    /** Build JSON AllergyIntolerance untuk mode Terstruktur (dari tabel alergi_pasien).
     *  Kolom yang dipakai: 13=allergy_code, 14=allergy_display, 15=category,
     *  16=reactioncode, 17=reaction_display, 18=severity, 19=note. */
    private String buildAllergyJsonTerstruktur(int row, String idPasien, String idPraktisi, String idExisting) {
        String noRawat      = tbObat.getValueAt(row,2).toString();
        String nmPasien     = tbObat.getValueAt(row,4).toString();
        String idEncounter  = tbObat.getValueAt(row,6).toString();
        String nmPraktisi   = tbObat.getValueAt(row,8).toString();
        String tglPerawatan = tbObat.getValueAt(row,10).toString();
        String allergyCode  = tbObat.getValueAt(row,13).toString();
        String allergyDisplay = tbObat.getValueAt(row,14).toString();
        String catFhir      = mapCategoryToFhir(tbObat.getValueAt(row,15).toString());
        String reactionCode = tbObat.getValueAt(row,16).toString();
        String reactionDisp = tbObat.getValueAt(row,17).toString();
        String severityFhir = mapSeverityToFhir(tbObat.getValueAt(row,18).toString());
        String note         = tbObat.getValueAt(row,19).toString();

        StringBuilder sb = new StringBuilder(2048);
        sb.append("{")
          .append("\"resourceType\": \"AllergyIntolerance\",");
        if (idExisting != null && !idExisting.isEmpty()) {
            sb.append("\"id\": \"").append(idExisting).append("\",");
        }
        sb.append("\"identifier\": [{")
          .append("\"system\": \"http://sys-ids.kemkes.go.id/allergy/").append(koneksiDB.IDSATUSEHAT()).append("\",")
          .append("\"value\" : \"").append(jsonEscape(noRawat)).append("\"")
          .append("}],")
          .append("\"clinicalStatus\": {\"coding\": [{")
          .append("\"system\": \"http://terminology.hl7.org/CodeSystem/allergyintolerance-clinical\",")
          .append("\"code\": \"active\", \"display\": \"Active\"}]},")
          .append("\"verificationStatus\": {\"coding\": [{")
          .append("\"system\": \"http://terminology.hl7.org/CodeSystem/allergyintolerance-verification\",")
          .append("\"code\": \"confirmed\", \"display\": \"Confirmed\"}]},")
          .append("\"category\": [\"").append(catFhir).append("\"],")
          .append("\"code\": {\"coding\": [{")
          .append("\"system\": \"http://snomed.info/sct\",")
          .append("\"code\": \"").append(jsonEscape(allergyCode)).append("\",")
          .append("\"display\": \"").append(jsonEscape(allergyDisplay)).append("\"")
          .append("}]")
          .append(note.isEmpty() ? "" : ",\"text\": \"" + jsonEscape(note) + "\"")
          .append("},")
          .append("\"patient\": {")
          .append("\"reference\" : \"Patient/").append(idPasien).append("\",")
          .append("\"display\" : \"").append(jsonEscape(nmPasien)).append("\"")
          .append("},")
          .append("\"encounter\" : {")
          .append("\"reference\" : \"Encounter/").append(idEncounter).append("\"")
          .append("},")
          .append("\"recordedDate\": \"").append(fungsi.IsoTime.toUtc(tglPerawatan)).append("\",")
          .append("\"recorder\": {")
          .append("\"reference\" : \"Practitioner/").append(idPraktisi).append("\",")
          .append("\"display\" : \"").append(jsonEscape(nmPraktisi)).append("\"")
          .append("}");

        // reaction[] kalau ada reactioncode terisi
        if (!reactionCode.isEmpty()) {
            sb.append(",\"reaction\": [{")
              .append("\"manifestation\": [{\"coding\": [{")
              .append("\"system\": \"http://snomed.info/sct\",")
              .append("\"code\": \"").append(jsonEscape(reactionCode)).append("\",")
              .append("\"display\": \"").append(jsonEscape(reactionDisp)).append("\"")
              .append("}]}]");
            if (!severityFhir.isEmpty()) {
                sb.append(",\"severity\": \"").append(severityFhir).append("\"");
            }
            if (!note.isEmpty()) {
                sb.append(",\"description\": \"").append(jsonEscape(note)).append("\"");
            }
            sb.append("}]");
        }
        sb.append("}");
        return sb.toString();
    }

    /** Escape karakter untuk pembentukan JSON manual.
     *  Mencegah SatuSehat reject "Invalid JSON Payload Format" (Rule 20006)
     *  kalau nama pasien/dokter atau text alergi (mis. dari cacheFile)
     *  mengandung TAB / newline / quote. */
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
            SatuSehatKirimAllergyIntolerance dialog = new SatuSehatKirimAllergyIntolerance(new javax.swing.JFrame(), true);
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
    // End of variables declaration//GEN-END:variables
    
    private void BtnMasterReferensiActionPerformed(java.awt.event.ActionEvent evt) {
        tampilMasterReferensi();
    }

    private void tampilMasterReferensi() {
        File cacheFile = new File("./cache/alergisatusehat.iyem");

        // Load data dari cache
        DefaultTableModel masterModel = new DefaultTableModel(
            new String[]{"Keyword", "Category", "Coding System", "Coding Code", "Coding Display", "Text"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return true; }
        };

        if (cacheFile.exists()) {
            try (FileReader fr = new FileReader(cacheFile)) {
                JsonNode rootNode = mapper.readTree(fr);
                JsonNode alergi = rootNode.path("alergi");
                if (alergi.isArray()) {
                    for (JsonNode item : alergi) {
                        masterModel.addRow(new Object[]{
                            item.path("keyword").asText(),
                            item.path("category").asText(),
                            item.path("coding_system").asText(),
                            item.path("coding_code").asText(),
                            item.path("coding_display").asText(),
                            item.path("text").asText()
                        });
                    }
                }
            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(this, "Gagal membaca file cache:\n" + e.getMessage(),
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Buat dialog
        javax.swing.JDialog dlg = new javax.swing.JDialog((java.awt.Frame) SwingUtilities.getWindowAncestor(this), "Master Referensi Alergi", true);
        dlg.setSize(1000, 550);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new java.awt.BorderLayout(4, 4));

        javax.swing.JTable tblMaster = new javax.swing.JTable(masterModel);
        tblMaster.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tblMaster.getColumnModel().getColumn(0).setPreferredWidth(130);
        tblMaster.getColumnModel().getColumn(1).setPreferredWidth(90);
        tblMaster.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblMaster.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblMaster.getColumnModel().getColumn(4).setPreferredWidth(150);
        tblMaster.getColumnModel().getColumn(5).setPreferredWidth(300);
        tblMaster.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblMaster.setDefaultRenderer(Object.class, new WarnaTable());
        tblMaster.setRowHeight(22);
        dlg.add(new javax.swing.JScrollPane(tblMaster), java.awt.BorderLayout.CENTER);

        // Combobox category
        String[] categories = {"food", "medication", "environment", "biologic", "other"};
        tblMaster.getColumnModel().getColumn(1).setCellEditor(
            new javax.swing.DefaultCellEditor(new javax.swing.JComboBox<>(categories)));

        // Panel tombol bawah
        javax.swing.JPanel btnPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 5));

        javax.swing.JButton btnTambah = new javax.swing.JButton("+ Tambah");
        btnTambah.addActionListener(e -> {
            masterModel.addRow(new Object[]{"", "medication", "http://snomed.info/sct", "", "", ""});
            int lastRow = masterModel.getRowCount() - 1;
            tblMaster.scrollRectToVisible(tblMaster.getCellRect(lastRow, 0, true));
            tblMaster.setRowSelectionInterval(lastRow, lastRow);
            tblMaster.editCellAt(lastRow, 0);
        });

        javax.swing.JButton btnHapus = new javax.swing.JButton("- Hapus");
        btnHapus.addActionListener(e -> {
            int row = tblMaster.getSelectedRow();
            if (row >= 0) {
                int confirm = javax.swing.JOptionPane.showConfirmDialog(dlg,
                    "Hapus keyword \"" + masterModel.getValueAt(row, 0) + "\"?",
                    "Konfirmasi", javax.swing.JOptionPane.YES_NO_OPTION);
                if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                    masterModel.removeRow(row);
                }
            } else {
                javax.swing.JOptionPane.showMessageDialog(dlg, "Pilih baris yang ingin dihapus.");
            }
        });

        javax.swing.JButton btnSimpan = new javax.swing.JButton("Simpan");
        btnSimpan.addActionListener(e -> {
            // Stop editing jika ada cell yang masih aktif
            if (tblMaster.isEditing()) tblMaster.getCellEditor().stopCellEditing();

            // Validasi semua baris terisi
            for (int r = 0; r < masterModel.getRowCount(); r++) {
                String kw = masterModel.getValueAt(r, 0) != null ? masterModel.getValueAt(r, 0).toString().trim() : "";
                String cat = masterModel.getValueAt(r, 1) != null ? masterModel.getValueAt(r, 1).toString().trim() : "";
                String code = masterModel.getValueAt(r, 3) != null ? masterModel.getValueAt(r, 3).toString().trim() : "";
                if (kw.isEmpty() || cat.isEmpty() || code.isEmpty()) {
                    javax.swing.JOptionPane.showMessageDialog(dlg,
                        "Baris " + (r + 1) + ": Keyword, Category, dan Coding Code wajib diisi.",
                        "Validasi", javax.swing.JOptionPane.WARNING_MESSAGE);
                    tblMaster.setRowSelectionInterval(r, r);
                    return;
                }
            }

            // Build JSON
            try {
                StringBuilder sb = new StringBuilder("{\n  \"alergi\": [\n");
                for (int r = 0; r < masterModel.getRowCount(); r++) {
                    sb.append("    { ");
                    sb.append("\"keyword\": \"").append(esc(masterModel.getValueAt(r, 0))).append("\", ");
                    sb.append("\"category\": \"").append(esc(masterModel.getValueAt(r, 1))).append("\", ");
                    sb.append("\"coding_system\": \"").append(esc(masterModel.getValueAt(r, 2))).append("\", ");
                    sb.append("\"coding_code\": \"").append(esc(masterModel.getValueAt(r, 3))).append("\", ");
                    sb.append("\"coding_display\": \"").append(esc(masterModel.getValueAt(r, 4))).append("\", ");
                    sb.append("\"text\": \"").append(esc(masterModel.getValueAt(r, 5))).append("\" }");
                    if (r < masterModel.getRowCount() - 1) sb.append(",");
                    sb.append("\n");
                }
                sb.append("  ]\n}");

                cacheFile.getParentFile().mkdirs();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(cacheFile))) {
                    bw.write(sb.toString());
                }
                javax.swing.JOptionPane.showMessageDialog(dlg,
                    "Berhasil menyimpan " + masterModel.getRowCount() + " entri ke cache.",
                    "Simpan", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                javax.swing.JOptionPane.showMessageDialog(dlg, "Gagal menyimpan:\n" + ex.getMessage(),
                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });

        javax.swing.JButton btnTutup = new javax.swing.JButton("Tutup");
        btnTutup.addActionListener(e -> dlg.dispose());

        javax.swing.JLabel lblTotal = new javax.swing.JLabel();
        masterModel.addTableModelListener(ev -> lblTotal.setText("Total: " + masterModel.getRowCount() + " entri"));
        lblTotal.setText("Total: " + masterModel.getRowCount() + " entri");

        btnPanel.add(btnTambah);
        btnPanel.add(btnHapus);
        btnPanel.add(btnSimpan);
        btnPanel.add(btnTutup);
        btnPanel.add(javax.swing.Box.createHorizontalStrut(20));
        btnPanel.add(lblTotal);
        dlg.add(btnPanel, java.awt.BorderLayout.PAGE_END);

        dlg.setVisible(true);
    }

    private String esc(Object val) {
        if (val == null) return "";
        return val.toString().replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void tampil() {
        Valid.tabelKosong(tabMode);
        int sumber = (CmbSumber == null) ? 0 : CmbSumber.getSelectedIndex();
        if (sumber == 0) {
            tampilTerstruktur();
        } else {
            tampilLegacy();
        }
        LCount.setText(""+tabMode.getRowCount());
    }

    /** Sumber: tabel terstruktur alergi_pasien (lengkap dgn reaction & severity).
     *  Diisi via DlgDataAlergiPasien (Rawat Jalan -> tombol Data Alergi). */
    private void tampilTerstruktur() {
        try{
            String sqlBase =
                "select reg_periksa.tgl_registrasi,reg_periksa.jam_reg,reg_periksa.no_rawat,reg_periksa.no_rkm_medis,"+
                "pasien.nm_pasien,pasien.no_ktp,satu_sehat_encounter.id_encounter,"+
                "concat(ifnull(satu_sehat_ref_allergy.display,alergi_pasien.allergy_code),"+
                "    case when alergi_pasien.note<>'' then concat(' (',alergi_pasien.note,')') else '' end) as alergi_text,"+
                "pegawai.nama,pegawai.no_ktp as ktppraktisi,"+
                "date_format(alergi_pasien.tgl_perawatan,'%Y-%m-%d') as tgl_perawatan,"+
                "date_format(alergi_pasien.tgl_perawatan,'%H:%i:%s') as jam_rawat,"+
                "ifnull(satu_sehat_allergy_intolerance.id_allergy_intolerance,'') as satu_sehat_allergy_intolerance,"+
                "alergi_pasien.allergy_code,"+
                "ifnull(satu_sehat_ref_allergy.display,'') as allergy_display,"+
                "ifnull(alergi_pasien.category,'') as category,"+
                "ifnull(alergi_pasien.reactioncode,'') as reactioncode,"+
                "ifnull(satu_sehat_ref_allergy_reaction.display,'') as reaction_display,"+
                "ifnull(alergi_pasien.severity,'') as severity,"+
                "ifnull(alergi_pasien.note,'') as note "+
                "from alergi_pasien "+
                "inner join reg_periksa on reg_periksa.no_rawat=alergi_pasien.no_rawat "+
                "inner join pasien on pasien.no_rkm_medis=reg_periksa.no_rkm_medis "+
                "inner join satu_sehat_encounter on satu_sehat_encounter.no_rawat=alergi_pasien.no_rawat "+
                "inner join pegawai on pegawai.nik=alergi_pasien.nippetugas "+
                "left join satu_sehat_ref_allergy on satu_sehat_ref_allergy.kode=alergi_pasien.allergy_code "+
                "left join satu_sehat_ref_allergy_reaction on satu_sehat_ref_allergy_reaction.kode=alergi_pasien.reactioncode "+
                "left join satu_sehat_allergy_intolerance on satu_sehat_allergy_intolerance.no_rawat=alergi_pasien.no_rawat "+
                "where date(alergi_pasien.tgl_perawatan) between ? and ? "+
                (TCari.getText().equals("")?"":"and (reg_periksa.no_rawat like ? or reg_periksa.no_rkm_medis like ? or "+
                "pasien.nm_pasien like ? or pasien.no_ktp like ? or pegawai.no_ktp like ? or pegawai.nama like ?) ");
            ps=koneksi.prepareStatement(sqlBase);
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
                }
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new Object[]{
                        false,rs.getString("tgl_registrasi")+" "+rs.getString("jam_reg"),rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),
                        rs.getString("no_ktp"),rs.getString("id_encounter"),rs.getString("alergi_text"),rs.getString("nama"),rs.getString("ktppraktisi"),
                        rs.getString("tgl_perawatan")+" "+rs.getString("jam_rawat"),rs.getString("satu_sehat_allergy_intolerance"),"Ralan/Ranap",
                        rs.getString("allergy_code"),rs.getString("allergy_display"),rs.getString("category"),
                        rs.getString("reactioncode"),rs.getString("reaction_display"),rs.getString("severity"),
                        rs.getString("note"),"Terstruktur"
                    });
                }
            } finally{
                if(rs!=null){ rs.close(); }
                if(ps!=null){ ps.close(); }
            }
        }catch(Exception e){
            System.out.println("Notifikasi tampilTerstruktur : "+e);
        }
    }

    /** Daftar tabel sumber alergi-text (text bebas) yang di-UNION untuk mode Legacy.
     *  Per entry: {tabel, kolom_alergi, kolom_tgl, kolom_jam_or_const, kolom_nip}.
     *  Kolom jam yang tidak ada di tabel pakai literal '00:00:00'.
     *  Sumber awal: hasil scan kolom *alergi* di schema rstrimitra (2026-05-25). */
    private static final String[][] LEGACY_ALERGI_SOURCES = {
        // tabel,                                          kolom_alergi,       tgl,            jam,           nip
        {"pemeriksaan_ralan",                              "alergi",           "tgl_perawatan","jam_rawat",   "nip"},
        {"pemeriksaan_ranap",                              "alergi",           "tgl_perawatan","jam_rawat",   "nip"},
        {"penilaian_medis_ralan",                          "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_medis_ralan_anak",                     "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_medis_ralan_bedah",                    "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_medis_ralan_bedah_mulut",              "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_medis_ralan_geriatri",                 "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_medis_ralan_kandungan",                "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_medis_ralan_mata",                     "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_medis_ralan_neurologi",                "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_medis_ralan_orthopedi",                "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_medis_ralan_paru",                     "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_medis_ralan_penyakit_dalam",           "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_medis_ralan_psikiatrik",               "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_medis_ralan_rehab_medik",              "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_medis_ralan_tht",                      "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_medis_ranap",                          "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_medis_ranap_kandungan",                "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_medis_igd",                            "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_medis_hemodialisa",                    "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_awal_keperawatan_ralan",               "alergi",           "tanggal",      "'00:00:00'",  "nip"},
        {"penilaian_awal_keperawatan_ralan_bayi",          "alergi",           "tanggal",      "'00:00:00'",  "nip"},
        {"penilaian_awal_keperawatan_ralan_geriatri",      "alergi",           "tanggal",      "'00:00:00'",  "nip"},
        {"penilaian_awal_keperawatan_ranap",               "riwayat_alergi",   "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_awal_keperawatan_ranap_bayi",          "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_awal_keperawatan_kebidanan_ranap",     "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_awal_keperawatan_gigi",                "alergi",           "tanggal",      "'00:00:00'",  "nip"},
        {"penilaian_awal_keperawatan_mata",                "alergi",           "tanggal",      "'00:00:00'",  "nip"},
        {"penilaian_mcu",                                  "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_bayi_baru_lahir",                      "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_pasien_imunitas_rendah",               "riwayat_alergi",   "tanggal",      "'00:00:00'",  "kd_dokter"},
        {"penilaian_pasien_penyakit_menular",              "riwayat_alergi",   "tanggal",      "'00:00:00'",  "kd_dokter"},
        // resume_pasien_ranap di-skip: tidak punya kolom tanggal (PK = no_rawat saja).
        // Alergi di resume biasanya copy dari sumber lain, jadi tidak unik.
        {"signin_sebelum_anestesi",                        "alergi",           "tanggal",      "'00:00:00'",  "kd_dokter_anestesi"},
        {"skrining_nutrisi_anak",                          "alergi",           "tanggal",      "'00:00:00'",  "nip"},
        {"skrining_nutrisi_dewasa",                        "alergi",           "tanggal",      "'00:00:00'",  "nip"},
        {"skrining_nutrisi_lansia",                        "alergi",           "tanggal",      "'00:00:00'",  "nip"},
        {"konseling_farmasi",                              "riwayat_alergi",   "tanggal",      "'00:00:00'",  "nip"},
    };

    /** Sumber legacy: text alergi dari ~40 tabel form RM* + pemeriksaan ralan/ranap.
     *  JSON builder akan lookup SNOMED code via cacheFile JSON. */
    private void tampilLegacy() {
        try{
            // Build inner UNION ALL subquery
            StringBuilder inner = new StringBuilder(8192);
            for (int k = 0; k < LEGACY_ALERGI_SOURCES.length; k++) {
                String[] s = LEGACY_ALERGI_SOURCES[k];
                String tbl = s[0], colA = s[1], colT = s[2], colJ = s[3], colN = s[4];
                if (k > 0) inner.append("UNION ALL ");
                inner.append("select no_rawat,").append(colA).append(" as alergi,")
                     .append(colT).append(" as tgl_perawatan,")
                     .append(colJ).append(" as jam_rawat,")
                     .append(colN).append(" as nip,")
                     .append("'").append(tbl).append("' as src_table ")
                     .append("from ").append(tbl)
                     .append(" where ").append(colA).append(" is not null and ").append(colA).append(" <> '' ");
            }

            String sql = "select rp.tgl_registrasi,rp.jam_reg,sub.no_rawat,rp.no_rkm_medis,"
                       + "p.nm_pasien,p.no_ktp,ssE.id_encounter,sub.alergi,"
                       + "ifnull(pg.nama,'') as nama,ifnull(pg.no_ktp,'') as ktppraktisi,"
                       + "sub.tgl_perawatan,sub.jam_rawat,"
                       + "ifnull(ssAI.id_allergy_intolerance,'') as satu_sehat_allergy_intolerance,"
                       + "sub.src_table "
                       + "from (" + inner.toString() + ") sub "
                       + "inner join reg_periksa rp on rp.no_rawat=sub.no_rawat "
                       + "inner join pasien p on p.no_rkm_medis=rp.no_rkm_medis "
                       + "inner join satu_sehat_encounter ssE on ssE.no_rawat=sub.no_rawat "
                       + "left join pegawai pg on pg.nik=sub.nip "
                       + "left join satu_sehat_allergy_intolerance ssAI on ssAI.no_rawat=sub.no_rawat "
                       + "and ssAI.tgl_perawatan=sub.tgl_perawatan and ssAI.jam_rawat=sub.jam_rawat "
                       + "where rp.tgl_registrasi between ? and ? "
                       + (TCari.getText().equals("") ? "" :
                           "and (rp.no_rawat like ? or rp.no_rkm_medis like ? or "
                           + "p.nm_pasien like ? or p.no_ktp like ? or pg.no_ktp like ? or pg.nama like ? "
                           + "or sub.alergi like ? or sub.src_table like ?) ")
                       + "order by rp.tgl_registrasi desc, sub.no_rawat";

            ps = koneksi.prepareStatement(sql);
            try {
                ps.setString(1, Valid.SetTgl(DTPCari1.getSelectedItem()+""));
                ps.setString(2, Valid.SetTgl(DTPCari2.getSelectedItem()+""));
                if (!TCari.getText().equals("")) {
                    for (int k = 3; k <= 10; k++) ps.setString(k, "%"+TCari.getText()+"%");
                }
                rs = ps.executeQuery();
                while (rs.next()) {
                    tabMode.addRow(new Object[]{
                        false, rs.getString("tgl_registrasi")+" "+rs.getString("jam_reg"),
                        rs.getString("no_rawat"), rs.getString("no_rkm_medis"), rs.getString("nm_pasien"),
                        rs.getString("no_ktp"), rs.getString("id_encounter"), rs.getString("alergi"),
                        rs.getString("nama"), rs.getString("ktppraktisi"),
                        rs.getString("tgl_perawatan")+" "+rs.getString("jam_rawat"),
                        rs.getString("satu_sehat_allergy_intolerance"),
                        rs.getString("src_table"),  // dipakai sebagai indikator sumber tabel (gantikan "Ralan/Ranap")
                        "","","","","","","","Legacy"
                    });
                }
            } catch (Exception e) {
                System.out.println("Notif tampilLegacy : "+e);
            } finally {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            }
        } catch(Exception e) {
            System.out.println("Notifikasi : "+e);
        }
    }

    public void isCek(){
        BtnKirim.setEnabled(akses.getsatu_sehat_kirim_careplan());
        BtnUpdate.setEnabled(akses.getsatu_sehat_kirim_careplan());
        BtnPrint.setEnabled(akses.getsatu_sehat_kirim_careplan());
    }
    
    public JTable getTable(){
        return tbObat;
    }
    
    private void runBackground(Runnable task) {
        if (ceksukses) return;
        if (executor.isShutdown() || executor.isTerminated()) return;
        if (!isDisplayable()) return;

        ceksukses = true;
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try {
            executor.submit(() -> {
                try {
                    task.run();
                } finally {
                    ceksukses = false;
                    SwingUtilities.invokeLater(() -> {
                        if (isDisplayable()) {
                            setCursor(Cursor.getDefaultCursor());
                        }
                    });
                }
            });
        } catch (RejectedExecutionException ex) {
            ceksukses = false;
        }
    }
    
    @Override
    public void dispose() {
        executor.shutdownNow();
        super.dispose();
    }
}
