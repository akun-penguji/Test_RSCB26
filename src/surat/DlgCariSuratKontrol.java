package surat;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;

public class DlgCariSuratKontrol extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private String norawatnow="";

    public DlgCariSuratKontrol(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        tabMode=new DefaultTableModel(null,new Object[]{
                "Tahun","No.RM","Nama Pasien","Diagnosa","Terapi","Alasan 1","Alasan 2",
                "RTL 1","RTL 2","Tgl.Datang","Tgl.Kembali","No.Antrian","No.Reg","Kode Dokter","Nama Dokter","Kode Poli","Nama Poli","Status"
            }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        tbSurat.setModel(tabMode);

        tbSurat.setPreferredScrollableViewportSize(new Dimension(800,400));
        tbSurat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (int i = 0; i < 18; i++) {
            TableColumn column = tbSurat.getColumnModel().getColumn(i);
            if(i==0){column.setPreferredWidth(45);
            }else if(i==1){column.setPreferredWidth(70);
            }else if(i==2){column.setPreferredWidth(170);
            }else if(i==3){column.setPreferredWidth(150);
            }else if(i==4){column.setPreferredWidth(150);
            }else if(i==5){column.setPreferredWidth(150);
            }else if(i==6){column.setPreferredWidth(150);
            }else if(i==7){column.setPreferredWidth(150);
            }else if(i==8){column.setPreferredWidth(150);
            }else if(i==9){column.setPreferredWidth(120);
            }else if(i==10){column.setPreferredWidth(120);
            }else if(i==11){column.setPreferredWidth(70);
            }else if(i==12){column.setPreferredWidth(50);
            }else if(i==13){column.setMinWidth(0);column.setMaxWidth(0);
            }else if(i==14){column.setPreferredWidth(150);
            }else if(i==15){column.setMinWidth(0);column.setMaxWidth(0);
            }else if(i==16){column.setPreferredWidth(150);
            }else if(i==17){column.setPreferredWidth(80);
            }
        }
        tbSurat.setDefaultRenderer(Object.class, new WarnaTable());

        TCari.setDocument(new batasInput((byte)100).getKata(TCari));
        if(koneksiDB.CARICEPAT().equals("aktif")){
            TCari.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    if(evt.getKeyCode()==KeyEvent.VK_ENTER){
                        tampil();
                    }
                }
            });
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        internalFrame1 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbSurat = new widget.Table();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        MnUploadTTE = new javax.swing.JMenuItem();
        panelGlass9 = new widget.panelisi();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        BtnAll = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();
        BtnKeluar = new widget.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Cari Surat Kontrol BPJS ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50)));
        internalFrame1.setName("internalFrame1");
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        Scroll.setName("Scroll");
        Scroll.setOpaque(true);

        tbSurat.setName("tbSurat");
        tbSurat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbSuratMouseClicked(evt);
            }
        });
        Scroll.setViewportView(tbSurat);

        internalFrame1.add(Scroll, java.awt.BorderLayout.CENTER);

        MnUploadTTE.setBackground(new java.awt.Color(255, 255, 254));
        MnUploadTTE.setFont(new java.awt.Font("Tahoma", 0, 11));
        MnUploadTTE.setForeground(new java.awt.Color(50, 50, 50));
        MnUploadTTE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png")));
        MnUploadTTE.setText("Upload PDF TTE ke Rawat Sekarang");
        MnUploadTTE.setName("MnUploadTTE");
        MnUploadTTE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnUploadTTEActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnUploadTTE);
        tbSurat.setComponentPopupMenu(jPopupMenu1);

        panelGlass9.setName("panelGlass9");
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6");
        jLabel6.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari");
        TCari.setPreferredSize(new java.awt.Dimension(200, 23));
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png")));
        BtnCari.setMnemonic('2');
        BtnCari.setToolTipText("Alt+2");
        BtnCari.setName("BtnCari");
        BtnCari.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnCari.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tampil();
            }
        });
        panelGlass9.add(BtnCari);

        BtnAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Search-16x16.png")));
        BtnAll.setMnemonic('M');
        BtnAll.setToolTipText("Alt+M");
        BtnAll.setName("BtnAll");
        BtnAll.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TCari.setText("");
                tampil();
            }
        });
        panelGlass9.add(BtnAll);

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7");
        jLabel7.setPreferredSize(new java.awt.Dimension(65, 23));
        panelGlass9.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount");
        LCount.setPreferredSize(new java.awt.Dimension(50, 23));
        panelGlass9.add(LCount);

        BtnKeluar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/exit.png")));
        BtnKeluar.setMnemonic('K');
        BtnKeluar.setToolTipText("Alt+K");
        BtnKeluar.setName("BtnKeluar");
        BtnKeluar.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnKeluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispose();
            }
        });
        panelGlass9.add(BtnKeluar);

        internalFrame1.add(panelGlass9, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>

    private void tbSuratMouseClicked(java.awt.event.MouseEvent evt) {
    }

    private void MnUploadTTEActionPerformed(java.awt.event.ActionEvent evt) {
        if(tbSurat.getSelectedRow()!= -1){
            if(norawatnow.equals("")){
               JOptionPane.showMessageDialog(null,"No. Rawat Sekarang Kosong!");
               return;
            }
            
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                Map<String, Object> param = new HashMap<>();  
                param.put("namars",akses.getnamars());
                param.put("alamatrs",akses.getalamatrs());
                param.put("kotars",akses.getkabupatenrs());
                param.put("propinsirs",akses.getpropinsirs());
                param.put("kontakrs",akses.getkontakrs());
                param.put("emailrs",akses.getemailrs());   
                param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
                param.put("logobsre", Sequel.cariGambar("select gambar.bsre from gambar"));
                
                String finger=Sequel.cariIsi("select tag_signer from dokter where kd_dokter=?",tbSurat.getValueAt(tbSurat.getSelectedRow(),13).toString());
                if (finger == null || finger.trim().isEmpty()) {
                    finger = "#";
                }
                param.put("finger",finger);
                
                Sequel.queryu("delete from temporary_booking_registrasi");                
                Sequel.menyimpan("temporary_booking_registrasi","'0','"+
                    tabMode.getValueAt(tbSurat.getSelectedRow(),0).toString()+"','"+
                    tabMode.getValueAt(tbSurat.getSelectedRow(),1).toString()+"','"+
                    tabMode.getValueAt(tbSurat.getSelectedRow(),2).toString()+"','"+
                    tabMode.getValueAt(tbSurat.getSelectedRow(),3).toString()+"','"+
                    tabMode.getValueAt(tbSurat.getSelectedRow(),4).toString()+"','"+
                    tabMode.getValueAt(tbSurat.getSelectedRow(),5).toString()+"','"+
                    tabMode.getValueAt(tbSurat.getSelectedRow(),6).toString()+"','"+
                    tabMode.getValueAt(tbSurat.getSelectedRow(),7).toString()+"','"+
                    tabMode.getValueAt(tbSurat.getSelectedRow(),8).toString()+"','"+
                    tabMode.getValueAt(tbSurat.getSelectedRow(),9).toString()+"','"+
                    tabMode.getValueAt(tbSurat.getSelectedRow(),10).toString()+"','"+
                    tabMode.getValueAt(tbSurat.getSelectedRow(),11).toString()+"','"+
                    tabMode.getValueAt(tbSurat.getSelectedRow(),12).toString()+"','"+
                    tabMode.getValueAt(tbSurat.getSelectedRow(),13).toString()+"','"+
                    tabMode.getValueAt(tbSurat.getSelectedRow(),14).toString()+"','"+
                    tabMode.getValueAt(tbSurat.getSelectedRow(),15).toString()+"','"+
                    tabMode.getValueAt(tbSurat.getSelectedRow(),16).toString()+"','"+
                    tabMode.getValueAt(tbSurat.getSelectedRow(),17).toString()+"','','','','','','','','','','','','','','','','','','',''","Surat Kotrol");

                File file = new File("report/rptSuratSKDPBPJSTte.jasper");
                if (!file.exists()) {
                    JasperCompileManager.compileReportToFile("report/rptSuratSKDPBPJSTte.jrxml", "report/rptSuratSKDPBPJSTte.jasper");
                    file = new File("report/rptSuratSKDPBPJSTte.jasper");
                }
                String query = "select * from temporary_booking_registrasi";
                PreparedStatement ps2 = koneksi.prepareStatement(query);
                ResultSet rs2 = ps2.executeQuery();
                JRResultSetDataSource rsdt = new JRResultSetDataSource(rs2);
                JasperPrint jasperPrint = JasperFillManager.fillReport(file.getPath(), param, rsdt);
                
                String noAntrian = tbSurat.getValueAt(tbSurat.getSelectedRow(), 11).toString().replaceAll("/", "").replaceAll(" ", "_");
                String namaPasien = tbSurat.getValueAt(tbSurat.getSelectedRow(), 2).toString().replaceAll(" ", "_").replaceAll("'", "");
                if (namaPasien.length() > 20) {
                    namaPasien = namaPasien.substring(0, 20);
                }
                String tglKontrol = tbSurat.getValueAt(tbSurat.getSelectedRow(), 9).toString().replaceAll("-", "").replaceAll(":", "_").replaceAll(" ", "_").replaceAll("\\.", "_");

                String fileName = "SuratKontrolSebelumnya_" + noAntrian + "_" + namaPasien + "_" + tglKontrol + "_Tte.pdf";
                File tmpDir = new File(System.getProperty("java.io.tmpdir"));
                File pdfFile = new File(tmpDir, fileName);
                JasperExportManager.exportReportToPdfFile(jasperPrint, pdfFile.getAbsolutePath());
                
                // Upload
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://" + koneksiDB.HOSTHYBRIDWEB() + ":" + koneksiDB.PORTWEB() + "/" + koneksiDB.HYBRIDWEB() + "/unggah.php?doc=berkasrawat/pages/upload/");
                httppost.addHeader("X-Upload-Token", fungsi.koneksiDB.TOKENUNGGAH());
                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("file", new ByteArrayBody(FileUtils.readFileToByteArray(pdfFile), fileName));
                httppost.setEntity(reqEntity);
                httpclient.execute(httppost);
                
                // Cleanup
                rs2.close();
                ps2.close();
                
                // Save to DB
                String kodeBerkas = Sequel.cariIsi("select kode from master_berkas_digital where nama like '%Surat Kontrol%' or nama like '%SKDP%'");
                if (kodeBerkas.equals("")) {
                    kodeBerkas = "SKDP";
                    if (Sequel.cariInteger("select count(kode) from master_berkas_digital where kode='SKDP'") == 0) {
                        Sequel.menyimpan("master_berkas_digital", "'SKDP','Surat Kontrol/SKDP BPJS'", "Kategori SKDP");
                    }
                }
                
                String lokasiFile = "pages/upload/" + fileName;
                String nipDokter = tbSurat.getValueAt(tbSurat.getSelectedRow(), 13).toString();
                
                if(Sequel.cariInteger("SELECT COUNT(*) FROM berkas_digital_perawatan WHERE lokasi_file=? and no_rawat=?", lokasiFile, norawatnow) > 0) {
                     Sequel.mengedittf("berkas_digital_perawatan", "lokasi_file=? and no_rawat=?", "no_rawat=?,kode=?,lokasi_file=?,nip=?", 6, 
                         new String[]{norawatnow, kodeBerkas, lokasiFile, nipDokter, lokasiFile, norawatnow});
                } else {
                     Sequel.menyimpantf("berkas_digital_perawatan", "?,?,?,?", "Upload TTE", 4, 
                         new String[]{norawatnow, kodeBerkas, lokasiFile, nipDokter});
                }
                
                pdfFile.delete();
                JOptionPane.showMessageDialog(null,"Upload Surat Kontrol Berhasil");
            } catch (Exception e) {
                System.out.println("Error Upload Surat Kontrol: " + e);
                JOptionPane.showMessageDialog(null,"Gagal Upload: " + e.getMessage());
            }
            this.setCursor(Cursor.getDefaultCursor());
        }
    }

    public void setNoRm(String norm, String norawat) {
        TCari.setText(norm);
        this.norawatnow = norawat;
        tampil();
    }

    private void tampil() {
        Valid.tabelKosong(tabMode);
        try {
            ps=koneksi.prepareStatement(
                    "select skdp_bpjs.tahun,skdp_bpjs.no_rkm_medis,pasien.nm_pasien,"+
                    "skdp_bpjs.diagnosa,skdp_bpjs.terapi,skdp_bpjs.alasan1,skdp_bpjs.alasan2,"+
                    "skdp_bpjs.rtl1,skdp_bpjs.rtl2,skdp_bpjs.tanggal_datang,skdp_bpjs.tanggal_rujukan,"+
                    "skdp_bpjs.no_antrian,skdp_bpjs.kd_dokter,dokter.nm_dokter,skdp_bpjs.status "+
                    "from skdp_bpjs inner join pasien inner join dokter on "+
                    "skdp_bpjs.no_rkm_medis=pasien.no_rkm_medis and skdp_bpjs.kd_dokter=dokter.kd_dokter "+
                    "where skdp_bpjs.no_rkm_medis like ? or pasien.nm_pasien like ? order by skdp_bpjs.tanggal_rujukan desc");
            try {
                ps.setString(1,"%"+TCari.getText().trim()+"%");
                ps.setString(2,"%"+TCari.getText().trim()+"%");
                rs=ps.executeQuery();
                while(rs.next()){
                    String kdpoli=""; String nmpoli=""; String noantri="";
                    PreparedStatement ps2=koneksi.prepareStatement(
                            "select booking_registrasi.kd_poli,poliklinik.nm_poli,booking_registrasi.no_reg "+
                            "from booking_registrasi inner join poliklinik on booking_registrasi.kd_poli=poliklinik.kd_poli "+
                            "where booking_registrasi.kd_dokter=? and booking_registrasi.tanggal_periksa=? and booking_registrasi.no_rkm_medis=?");
                    try {
                        ps2.setString(1,rs.getString("kd_dokter"));
                        ps2.setString(2,rs.getString("tanggal_datang").substring(0,10));
                        ps2.setString(3,rs.getString("no_rkm_medis"));
                        ResultSet rs2=ps2.executeQuery();
                        if(rs2.next()){
                            kdpoli=rs2.getString("kd_poli");
                            nmpoli=rs2.getString("nm_poli");
                            noantri=rs2.getString("no_reg");
                        }
                        rs2.close();
                        ps2.close();
                    } catch (Exception e) {}
                    
                    tabMode.addRow(new Object[]{
                        rs.getInt("tahun"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),
                        rs.getString("diagnosa"),rs.getString("terapi"),rs.getString("alasan1"),
                        rs.getString("alasan2"),rs.getString("rtl1"),rs.getString("rtl2"),
                        rs.getString("tanggal_datang"),rs.getString("tanggal_rujukan"),rs.getString("no_antrian"),
                        noantri,rs.getString("kd_dokter"),rs.getString("nm_dokter"),kdpoli,
                        nmpoli,rs.getString("status")
                    });                    
                }
            } catch (Exception e) {
                System.out.println("Error tampil: "+e);
            } finally{
                if(rs!=null) rs.close();
                if(ps!=null) ps.close();
            }
        } catch (Exception e) {
            System.out.println("Error tampil: "+e);
        } 
        LCount.setText(""+tabMode.getRowCount());
    }

    private widget.InternalFrame internalFrame1;
    private widget.ScrollPane Scroll;
    private widget.Table tbSurat;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JMenuItem MnUploadTTE;
    private widget.panelisi panelGlass9;
    private widget.Label jLabel6;
    private widget.TextBox TCari;
    private widget.Button BtnCari;
    private widget.Button BtnAll;
    private widget.Label jLabel7;
    private widget.Label LCount;
    private widget.Button BtnKeluar;
}
