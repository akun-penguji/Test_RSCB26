package bridging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.akses;
import fungsi.koneksiDB;
import fungsi.validasi;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

public final class AplicareHapusKamarAPI extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private validasi Valid=new validasi();
    private int i=0;
    private ApiBPJSAplicare api=new ApiBPJSAplicare();
    private String URL="",CONSIDAPIAPLICARE="",kodeppk=akses.getkodeppkbpjs(),utc="",requestJson="";
    private HttpHeaders headers;
    private HttpEntity requestEntity;
    private ObjectMapper mapper=new ObjectMapper();
    private JsonNode root;
    private JsonNode nameNode;

    public AplicareHapusKamarAPI(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        this.setLocation(10,2);
        setSize(900,674);

        Object[] row={"P","Kode Kelas","Kode Ruang","Nama Ruang","Nama Kelas","Kapasitas","Tersedia",
                      "Tersedia P&W","Tersedia Pria","Tersedia Wanita"};
        tabMode=new DefaultTableModel(null,row){
             @Override public boolean isCellEditable(int rowIndex, int colIndex){
                boolean a = false;
                if (colIndex==0) {
                    a=true;
                }
                return a;
             }
             Class[] types = new Class[] {
                java.lang.Boolean.class, java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,
                java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,java.lang.Object.class,
                java.lang.Object.class,java.lang.Object.class
             };
             @Override
             public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
             }
        };
        tbKamar.setModel(tabMode);

        tbKamar.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbKamar.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 10; i++) {
            TableColumn column = tbKamar.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(20);
            }else if(i==1){
                column.setPreferredWidth(80);
            }else if(i==2){
                column.setPreferredWidth(80);
            }else if(i==3){
                column.setPreferredWidth(180);
            }else if(i==4){
                column.setPreferredWidth(100);
            }else if(i==5){
                column.setPreferredWidth(65);
            }else if(i==6){
                column.setPreferredWidth(65);
            }else if(i==7){
                column.setPreferredWidth(90);
            }else if(i==8){
                column.setPreferredWidth(80);
            }else if(i==9){
                column.setPreferredWidth(90);
            }
        }
        tbKamar.setDefaultRenderer(Object.class, new WarnaTable());

        TCari.setDocument(new batasInput((byte)100).getKata(TCari));

        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
                @Override
                public void insertUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampilAPI();
                    }
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampilAPI();
                    }
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    if(TCari.getText().length()>2){
                        tampilAPI();
                    }
                }
            });
        }

        try {
            URL = koneksiDB.URLAPIAPLICARE();
            CONSIDAPIAPLICARE=koneksiDB.CONSIDAPIAPLICARE();
        } catch (Exception e) {
            System.out.println("E : "+e);
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbKamar = new widget.Table();
        jPanel3 = new javax.swing.JPanel();
        panelGlass8 = new widget.panelisi();
        BtnHapus = new widget.Button();
        BtnKeluar = new widget.Button();
        panelGlass9 = new widget.panelisi();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Hapus Ruangan Aplicare BPJS (Data dari API) ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));
        internalFrame1.setName("internalFrame1");
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll");
        Scroll.setOpaque(true);

        tbKamar.setAutoCreateRowSorter(true);
        tbKamar.setToolTipText("Centang data yang mau dihapus, lalu klik tombol Hapus");
        tbKamar.setName("tbKamar");
        Scroll.setViewportView(tbKamar);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        jPanel3.setName("jPanel3");
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(44, 100));
        jPanel3.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8");
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png")));
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus");
        BtnHapus.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnHapusActionPerformed(evt);
            }
        });
        BtnHapus.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnHapusKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnHapus);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setText("Keluar");
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar");
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

        panelGlass9.setName("panelGlass9");
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6");
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari");
        TCari.setPreferredSize(new java.awt.Dimension(450, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setMnemonic('6');
        BtnCari.setToolTipText("Alt+6");
        BtnCari.setName("BtnCari");
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnCariActionPerformed(evt);
            }
        });
        panelGlass9.add(BtnCari);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7");
        jLabel7.setPreferredSize(new java.awt.Dimension(75, 23));
        panelGlass9.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount");
        LCount.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass9.add(LCount);

        jPanel3.add(panelGlass9, java.awt.BorderLayout.PAGE_START);

        internalFrame1.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }

    private void formWindowOpened(java.awt.event.WindowEvent evt) {
        tampilAPI();
    }

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {
        int jumlahDipilih=0;
        for(i=0;i<tbKamar.getRowCount();i++){
            if(tbKamar.getValueAt(i,0).toString().equals("true")){
                jumlahDipilih++;
            }
        }
        if(jumlahDipilih==0){
            JOptionPane.showMessageDialog(null,"Silahkan centang data yang mau dihapus...!");
            return;
        }
        int reply = JOptionPane.showConfirmDialog(rootPane,
                "Hapus "+jumlahDipilih+" ruangan dari API Aplicare?","Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if(reply==JOptionPane.YES_OPTION){
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int berhasil=0,gagal=0;
            for(i=0;i<tbKamar.getRowCount();i++){
                if(tbKamar.getValueAt(i,0).toString().equals("true")){
                    try {
                        headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON);
                        headers.add("X-Cons-ID",CONSIDAPIAPLICARE);
                        utc=String.valueOf(api.GetUTCdatetimeAsString());
                        headers.add("X-Timestamp",utc);
                        headers.add("X-Signature",api.getHmac(utc));
                        headers.add("user_key",koneksiDB.USERKEYAPIAPLICARE());
                        requestJson ="{\"kodekelas\":\""+tbKamar.getValueAt(i,1).toString()+"\", "+
                                      "\"koderuang\":\""+tbKamar.getValueAt(i,2).toString()+"\""+
                                      "}";
                        requestEntity = new HttpEntity(requestJson,headers);
                        root = mapper.readTree(api.getRest().exchange(URL+"/rest/bed/delete/"+kodeppk, HttpMethod.POST, requestEntity, String.class).getBody());
                        nameNode = root.path("metadata");
                        if(nameNode.path("message").asText().equals("Data berhasil dihapus.")){
                            berhasil++;
                        }else{
                            gagal++;
                            System.out.println("Gagal hapus "+tbKamar.getValueAt(i,1)+" - "+tbKamar.getValueAt(i,2)+" : "+nameNode.path("message").asText());
                        }
                    }catch (Exception ex) {
                        gagal++;
                        System.out.println("Notifikasi Bridging : "+ex);
                        if(ex.toString().contains("UnknownHostException")){
                            JOptionPane.showMessageDialog(rootPane,"Koneksi ke server Aplicare terputus...!");
                            break;
                        }
                    }
                }
            }
            this.setCursor(Cursor.getDefaultCursor());
            JOptionPane.showMessageDialog(null,"Berhasil hapus : "+berhasil+" data\nGagal hapus : "+gagal+" data");
            tampilAPI();
        }
    }

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnHapusActionPerformed(null);
        }else{
            Valid.pindah(evt, TCari, BtnKeluar);
        }
    }

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            dispose();
        }else{
            Valid.pindah(evt, BtnHapus, TCari);
        }
    }

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            tampilAPI();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnHapus.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            BtnKeluar.requestFocus();
        }
    }

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        tampilAPI();
        this.setCursor(Cursor.getDefaultCursor());
    }

    private void tampilAPI() {
        try {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-Cons-ID",CONSIDAPIAPLICARE);
            utc=String.valueOf(api.GetUTCdatetimeAsString());
            headers.add("X-Timestamp",utc);
            headers.add("X-Signature",api.getHmac(utc));
            headers.add("user_key",koneksiDB.USERKEYAPIAPLICARE());
            requestEntity = new HttpEntity(headers);
            root = mapper.readTree(api.getRest().exchange(URL+"/rest/bed/read/"+kodeppk+"/1/100", HttpMethod.GET, requestEntity, String.class).getBody());
            nameNode = root.path("metadata");
            if(nameNode.path("message").asText().equals("OK")){
                Valid.tabelKosong(tabMode);
                JsonNode response = root.path("response");
                if(response.path("list").isArray()){
                    for(JsonNode list:response.path("list")){
                        if(list.path("kodekelas").asText().toLowerCase().contains(TCari.getText().toLowerCase())||
                                list.path("namaruang").asText().toLowerCase().contains(TCari.getText().toLowerCase())||
                                list.path("koderuang").asText().toLowerCase().contains(TCari.getText().toLowerCase())||
                                TCari.getText().trim().equals("")){
                            tabMode.addRow(new Object[]{
                                false,
                                list.path("kodekelas").asText(),
                                list.path("koderuang").asText(),
                                list.path("namaruang").asText(),
                                list.path("namakelas").asText(),
                                list.path("kapasitas").asText(),
                                list.path("tersedia").asText(),
                                list.path("tersediapriawanita").asText(),
                                list.path("tersediapria").asText(),
                                list.path("tersediawanita").asText()
                            });
                        }
                    }
                }
                LCount.setText(""+tabMode.getRowCount());
            }else{
                JOptionPane.showMessageDialog(null,nameNode.path("message").asText());
            }
        } catch (Exception ex) {
            System.out.println("Notifikasi : "+ex);
            if(ex.toString().contains("UnknownHostException")){
                JOptionPane.showMessageDialog(rootPane,"Koneksi ke server Aplicare terputus...!");
            }
        }
    }

    public JTable getTable(){
        return tbKamar;
    }

    private widget.Button BtnCari;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Label LCount;
    private widget.ScrollPane Scroll;
    private widget.TextBox TCari;
    private widget.InternalFrame internalFrame1;
    private widget.Label jLabel6;
    private widget.Label jLabel7;
    private javax.swing.JPanel jPanel3;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.Table tbKamar;
}
