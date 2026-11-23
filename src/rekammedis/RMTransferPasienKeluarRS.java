/*
 * Kontribusi dari M. Syukur RS. Jiwa Prov Sultra
 */


package rekammedis;

import fungsi.WarnaTable;
import fungsi.batasInput;
import fungsi.koneksiDB;
import fungsi.sekuel;
import fungsi.validasi;
import fungsi.akses;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import kepegawaian.DlgCariPetugas;
import kepegawaian.DlgCariDokter;
import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.PreparedStatement; 
import java.sql.SQLException;


/**
 *
 * @author perpustakaan
 */
public final class RMTransferPasienKeluarRS extends javax.swing.JDialog {
    private final DefaultTableModel tabMode;
    private Connection koneksi=koneksiDB.condb();
    private sekuel Sequel=new sekuel();
    private validasi Valid=new validasi();
    private PreparedStatement ps;
    private ResultSet rs;
    private int i=0,pilihan=0;
    private DlgCariPetugas petugas=new DlgCariPetugas(null,false);
    private DlgCariDokter dokter=new DlgCariDokter(null,false);    
    private StringBuilder htmlContent;
    private String finger="",finger_pj="",checkboxRM="",checkboxLAB="",checkboxRAD="",checkboxLAINNYA="",checkboxRM1="",checkboxLAB1="",checkboxRAD1="",checkboxLAINNYA1="",
            checkboxKlinikal,checkboxnonKlinikal,checkboxICUPenuh,checkboxRIPenuh,checkboxPermintaanPasien,checkboxKetRujukLainnya,
            checkboxTdkAdaRiwayat,checkboxAdaRiwayat,checkboxPDM,checkboxPJantung,checkboxPLainnya,
            checkboxTdkAlmed,checkboxYaAlmed,checkboxPInfus,checkboxPETT,checkboxPO2,checkboxPCVC,checkboxPNGT,checkboxPCateter,checkboxPBidal,checkboxPPump,checkboxPAlmedLainnya,checkboxKetPAlmedLainnya;             
   
    /** Creates new form DlgRujuk
     * @param parent
     * @param modal */
    public RMTransferPasienKeluarRS(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        
        tabMode=new DefaultTableModel(null,new Object[]{
            "No.Rawat","No.RM","Nama Pasien","Tgl.Lahir","J.K.","Tanggal Kontak","Tanggal Pindah",
            "Asal Ruang Rawat / Poliklinik","Rujuk ke RS","Diagnosa Rujuk",
            "Menyetujui Pemindahan","Nama Keluarga/Penanggung Jawab","Hubungan",
            "Nama Penghubung RS","Telp Penghubung RS","Nama Penerima Kontak RS","Telp Penerima Kontak RS",            
            "Keadaan Pasien SbT","Nadi SbT","Suhu SbT","TD SbT","RR SbT","Kondisi Sebelum Transfer","Obat-obatan Telah Diberikan",
            "Klinikal","Ket Klinikal","Non Klinikal","Ket Non Klinikal","ICU Penuh","RI Penuh","Permintaan Pasien","Rujuk Lainnya","Ket Rujuk Lainnya",
            "Tdk Ada Almed","Ada Almed","Infus","ETT","O2","CVC","NGT","Cateter","Bidal","Pump","Almed Lainnya","Ket Almed Lainnya",
            "Tdk Ada R Penyakit","Ada R Penyakit","R P DM","R P Jantung","R Lainnya","Ket R Lainnya",            
            "Dok RM","Hasil Lab","Hasil Rad","Dok Lainnya","Ket Dok",
            "Kejadian Selama Transfer",
            "Kode Dokter","Dokter Yang Menyerahkan"          
        }){
              @Override public boolean isCellEditable(int rowIndex, int colIndex){return false;}
        };
        
        tbObat.setModel(tabMode);
        tbObat.setPreferredScrollableViewportSize(new Dimension(500,500));
        tbObat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        for (i = 0; i < 59; i++) {
            TableColumn column = tbObat.getColumnModel().getColumn(i);
            if(i==0){
                column.setPreferredWidth(105);
            }else if(i==1){
                column.setPreferredWidth(70);
            }else if(i==2){
                column.setPreferredWidth(150);
            }else if(i==3){
                column.setPreferredWidth(65);
            }else if(i==4){
                column.setPreferredWidth(55);
            }else if(i==5){
                column.setPreferredWidth(115);
            }else if(i==6){
                column.setPreferredWidth(115);
            }else if(i==7){
                column.setPreferredWidth(190);
            }else if(i==8){
                column.setPreferredWidth(140);
            }else if(i==9){
                column.setPreferredWidth(150);
            }else if(i==10){
                column.setPreferredWidth(150);
            }else if(i==11){
                column.setPreferredWidth(108);
            }else if(i==12){
                column.setPreferredWidth(160);
            }else if(i==13){
                column.setPreferredWidth(160);
            }else if(i==14){
                column.setPreferredWidth(200);
            }else if(i==15){
                column.setPreferredWidth(230);
            }else if(i==16){
                column.setPreferredWidth(236);
            }else if(i==17){
                column.setPreferredWidth(134);
            }else if(i==18){
                column.setPreferredWidth(165);
            }else if(i==19){
                column.setPreferredWidth(127);
            }else if(i==20){
                column.setPreferredWidth(180);
            }else if(i==21){
                column.setPreferredWidth(100);
            }else if(i==22){
                column.setPreferredWidth(107);
            }else if(i==23){
                column.setPreferredWidth(50);
            }else if(i==24){
                column.setPreferredWidth(50);
            }else if(i==25){
                column.setPreferredWidth(45);
            }else if(i==26){
                column.setPreferredWidth(50);
            }else if(i==27){
                column.setPreferredWidth(200);
            }else if(i==28){
                column.setPreferredWidth(107);
            }else if(i==29){
                column.setPreferredWidth(50);
            }else if(i==30){
                column.setPreferredWidth(50);
            }else if(i==31){
                column.setPreferredWidth(45);
            }else if(i==32){
                column.setPreferredWidth(50);
            }else if(i==33){
                column.setPreferredWidth(200);
            }else if(i==34){
                column.setPreferredWidth(95);
            }else if(i==35){
                column.setPreferredWidth(150);
            }else if(i==36){
                column.setPreferredWidth(95);
            }else if(i==37){
                column.setPreferredWidth(150);
            }else if(i==38){
                column.setPreferredWidth(150);
            }else if(i==39){
                column.setPreferredWidth(150);
            }else if(i==40){
                column.setPreferredWidth(150);
            }else if(i==41){
                column.setPreferredWidth(150);                
            }else if(i==42){
                column.setPreferredWidth(150);   
            }else if(i==43){
                column.setPreferredWidth(150);
            }else if(i==44){
                column.setPreferredWidth(150);
            }else if(i==45){
                column.setPreferredWidth(150);
            }else if(i==46){
                column.setPreferredWidth(150);
            }else if(i==47){
                column.setPreferredWidth(150);                
            }else if(i==48){
                column.setPreferredWidth(150);     
            }else if(i==49){
                column.setPreferredWidth(150);
            }else if(i==50){
                column.setPreferredWidth(150);
            }else if(i==51){
                column.setPreferredWidth(150);  
            }else if(i==52){
                column.setPreferredWidth(150);                 
            }else if(i==53){
                column.setPreferredWidth(150);     
            }else if(i==54){
                column.setPreferredWidth(150);
            }else if(i==55){
                column.setPreferredWidth(150);
            }else if(i==56){
                column.setPreferredWidth(150);  
            }else if(i==57){
                column.setPreferredWidth(150);   
            }else if(i==58){
                column.setPreferredWidth(150);                 
            }
        }
        tbObat.setDefaultRenderer(Object.class, new WarnaTable());
        
        TNoRw.setDocument(new batasInput((byte)17).getKata(TNoRw));
        AsalRuang.setDocument(new batasInput((byte)30).getKata(AsalRuang));
        RuangSelanjutnya.setDocument(new batasInput((byte)30).getKata(RuangSelanjutnya));
        DiagnosaUtama.setDocument(new batasInput((int)50).getKata(DiagnosaUtama));
//        DiagnosaSekunder.setDocument(new batasInput((int)100).getKata(DiagnosaSekunder));
//        KeteranganIndikasiPindahRuang.setDocument(new batasInput((int)50).getKata(KeteranganIndikasiPindahRuang));
//        ProsedurDilakukan.setDocument(new batasInput((int)800).getKata(ProsedurDilakukan));
//        ObatYangDiberikan.setDocument(new batasInput((int)800).getKata(ObatYangDiberikan));
//        KeteranganPeralatan.setDocument(new batasInput((int)50).getKata(KeteranganPeralatan));
//        PemeriksaanPenunjang.setDocument(new batasInput((int)500).getKata(PemeriksaanPenunjang));
        NamaPJMenyetujui.setDocument(new batasInput((int)50).getKata(NamaPJMenyetujui));
        KeluhanUtamaSebelumTransfer.setDocument(new batasInput((int)200).getKata(KeluhanUtamaSebelumTransfer));
        TDSebelumTransfer.setDocument(new batasInput((int)7).getKata(TDSebelumTransfer));
        NadiSebelumTransfer.setDocument(new batasInput((int)5).getKata(NadiSebelumTransfer));
        RRSebelumTransfer.setDocument(new batasInput((int)5).getKata(RRSebelumTransfer));
        SuhuSebelumTransfer.setDocument(new batasInput((int)5).getKata(SuhuSebelumTransfer));
        KeluhanUtamaSetelahTransfer.setDocument(new batasInput((int)200).getKata(KeluhanUtamaSetelahTransfer));
/*        TDSetelahTransfer.setDocument(new batasInput((int)7).getKata(TDSetelahTransfer));
        NadiSetelahTransfer.setDocument(new batasInput((int)5).getKata(NadiSetelahTransfer));
        RRSetelahTransfer.setDocument(new batasInput((int)5).getKata(RRSetelahTransfer));
        SuhuSetelahTransfer.setDocument(new batasInput((int)5).getKata(SuhuSetelahTransfer));*/
        TCari.setDocument(new batasInput((int)100).getKata(TCari));
        
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
        
        
        petugas.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(petugas.getTable().getSelectedRow()!= -1){
                    if(pilihan==1){
                        KdDokterMenyerahkan.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        NmDokterMenyerahkan.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        KdDokterMenyerahkan.requestFocus();
/*                   }else{
                        KdPetugasMenerima.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),0).toString());
                        NmPetugasMenerima.setText(petugas.getTable().getValueAt(petugas.getTable().getSelectedRow(),1).toString());
                        KdPetugasMenerima.requestFocus();*/
                    }
                }
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
        
         dokter.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
            @Override
            public void windowClosed(WindowEvent e) {
                if(dokter.getTable().getSelectedRow()!= -1){
                    KdDokterMenyerahkan.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),0).toString());
                    NmDokterMenyerahkan.setText(dokter.getTable().getValueAt(dokter.getTable().getSelectedRow(),1).toString());
                    KdDokterMenyerahkan.requestFocus();
                }
            }
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowActivated(WindowEvent e) {}
            @Override
            public void windowDeactivated(WindowEvent e) {}
        });
               
        
/*        HTMLEditorKit kit = new HTMLEditorKit();
        LoadHTML.setEditable(true);
        LoadHTML.setEditorKit(kit);
        LoadHTML2.setEditable(true);
        LoadHTML2.setEditorKit(kit);
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
        LoadHTML2.setDocument(doc);
        
        ChkAccor.setSelected(false);
        isPhoto();
    }*/

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
        MnTransferPasienKeluarRS = new javax.swing.JMenuItem();
        LoadHTML = new widget.editorpane();
        internalFrame1 = new widget.InternalFrame();
        panelGlass8 = new widget.panelisi();
        BtnSimpan = new widget.Button();
        BtnBatal = new widget.Button();
        BtnHapus = new widget.Button();
        BtnEdit = new widget.Button();
        BtnPrint = new widget.Button();
        BtnAll = new widget.Button();
        BtnKeluar = new widget.Button();
        TabRawat = new javax.swing.JTabbedPane();
        internalFrame2 = new widget.InternalFrame();
        scrollInput = new widget.ScrollPane();
        FormInput = new widget.PanelBiasa();
        jSeparator14 = new javax.swing.JSeparator();
        MenyetujuiPemindahan = new widget.ComboBox();
        TNoRw = new widget.TextBox();
        TPasien = new widget.TextBox();
        TNoRM = new widget.TextBox();
        jLabel8 = new widget.Label();
        TglLahir = new widget.TextBox();
        Jk = new widget.TextBox();
        jLabel10 = new widget.Label();
        jLabel11 = new widget.Label();
        label12 = new widget.Label();
        TanggalRujukKeluarRS = new widget.Tanggal();
        jSeparator1 = new javax.swing.JSeparator();
        RuangSelanjutnya = new widget.TextBox();
        jLabel15 = new widget.Label();
        jLabel16 = new widget.Label();
        DiagnosaUtama = new widget.TextBox();
        jLabel18 = new widget.Label();
        jLabel34 = new widget.Label();
        AsalRuang = new widget.TextBox();
        NamaPJMenyetujui = new widget.TextBox();
        jLabel38 = new widget.Label();
        HubunganPJMenyetujui = new widget.ComboBox();
        jLabel39 = new widget.Label();
        jLabel40 = new widget.Label();
        jLabel41 = new widget.Label();
        scrollPane5 = new widget.ScrollPane();
        KeluhanUtamaSebelumTransfer = new widget.TextArea();
        jLabel42 = new widget.Label();
        KeadaanUmumSebelumTransfer = new widget.ComboBox();
        TDSebelumTransfer = new widget.TextBox();
        jLabel23 = new widget.Label();
        jLabel17 = new widget.Label();
        jLabel22 = new widget.Label();
        NadiSebelumTransfer = new widget.TextBox();
        jLabel26 = new widget.Label();
        RRSebelumTransfer = new widget.TextBox();
        jLabel25 = new widget.Label();
        jLabel24 = new widget.Label();
        SuhuSebelumTransfer = new widget.TextBox();
        jLabel27 = new widget.Label();
        jLabel28 = new widget.Label();
        jLabel46 = new widget.Label();
        scrollPane6 = new widget.ScrollPane();
        KeluhanUtamaSetelahTransfer = new widget.TextArea();
        jSeparator7 = new javax.swing.JSeparator();
        label14 = new widget.Label();
        KdDokterMenyerahkan = new widget.TextBox();
        NmDokterMenyerahkan = new widget.TextBox();
        BtnDokter = new widget.Button();
        label16 = new widget.Label();
        jLabel53 = new widget.Label();
        jLabel54 = new widget.Label();
        ChkRM = new widget.CekBox();
        ChkLAB = new widget.CekBox();
        jLabel55 = new widget.Label();
        ChkRAD = new widget.CekBox();
        jLabel56 = new widget.Label();
        ChkLainnya = new widget.CekBox();
        jLabel58 = new widget.Label();
        KetDokLainnya = new widget.TextBox();
        jLabel59 = new widget.Label();
        ChkKlinikal = new widget.CekBox();
        jLabel60 = new widget.Label();
        KetKlinikal = new widget.TextBox();
        ChkNonKlinikal = new widget.CekBox();
        jLabel61 = new widget.Label();
        KetNonKlinikal = new widget.TextBox();
        ChkICUPenuh = new widget.CekBox();
        jLabel62 = new widget.Label();
        ChkRanapPenuh = new widget.CekBox();
        jLabel63 = new widget.Label();
        ChkPermintaanPasien = new widget.CekBox();
        jLabel64 = new widget.Label();
        ChkAlasanLainnya = new widget.CekBox();
        jLabel65 = new widget.Label();
        AlasanLainnya = new widget.TextBox();
        jLabel66 = new widget.Label();
        ChkTdkAdaRiwayat = new widget.CekBox();
        jLabel67 = new widget.Label();
        ChkAdaRiwayat = new widget.CekBox();
        jLabel68 = new widget.Label();
        ChkPDM = new widget.CekBox();
        jLabel69 = new widget.Label();
        ChkPLainnya = new widget.CekBox();
        jLabel70 = new widget.Label();
        KetPLainnya = new widget.TextBox();
        ChkPJantung = new widget.CekBox();
        jLabel71 = new widget.Label();
        jLabel72 = new widget.Label();
        ChkTAlmed = new widget.CekBox();
        jLabel73 = new widget.Label();
        ChkYaAlmed = new widget.CekBox();
        jLabel74 = new widget.Label();
        ChkPInfus = new widget.CekBox();
        jLabel75 = new widget.Label();
        ChkPO2 = new widget.CekBox();
        jLabel76 = new widget.Label();
        ChkPETT = new widget.CekBox();
        jLabel77 = new widget.Label();
        ChkPNGT = new widget.CekBox();
        jLabel78 = new widget.Label();
        ChkPCateter = new widget.CekBox();
        jLabel79 = new widget.Label();
        ChkPBidal = new widget.CekBox();
        jLabel80 = new widget.Label();
        ChkPPump = new widget.CekBox();
        jLabel82 = new widget.Label();
        ChkPCVC = new widget.CekBox();
        jLabel83 = new widget.Label();
        ChkPAlmedLainnya = new widget.CekBox();
        jLabel81 = new widget.Label();
        KetPAmedLainnya = new widget.TextBox();
        jLabel43 = new widget.Label();
        jLabel44 = new widget.Label();
        jLabel45 = new widget.Label();
        jLabel47 = new widget.Label();
        jLabel49 = new widget.Label();
        jLabel50 = new widget.Label();
        jLabel51 = new widget.Label();
        NamaPenghubungRS = new widget.TextBox();
        NoTelpPenghubungRS = new widget.TextBox();
        NamaPenerimaTujuanRS = new widget.TextBox();
        NoTelpPenerimaTujuanRS = new widget.TextBox();
        TanggalKontakRS = new widget.Tanggal();
        scrollPane7 = new widget.ScrollPane();
        ObatTelahDiberikan = new widget.TextArea();
        jLabel48 = new widget.Label();
        jSeparator8 = new javax.swing.JSeparator();
        jSeparator10 = new javax.swing.JSeparator();
        jSeparator11 = new javax.swing.JSeparator();
        internalFrame3 = new widget.InternalFrame();
        Scroll = new widget.ScrollPane();
        tbObat = new widget.Table();
        panelGlass9 = new widget.panelisi();
        jLabel19 = new widget.Label();
        DTPCari1 = new widget.Tanggal();
        jLabel21 = new widget.Label();
        DTPCari2 = new widget.Tanggal();
        jLabel6 = new widget.Label();
        TCari = new widget.TextBox();
        BtnCari = new widget.Button();
        jLabel7 = new widget.Label();
        LCount = new widget.Label();

        jPopupMenu1.setName("jPopupMenu1"); // NOI18N

        MnTransferPasienKeluarRS.setBackground(new java.awt.Color(255, 255, 254));
        MnTransferPasienKeluarRS.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        MnTransferPasienKeluarRS.setForeground(new java.awt.Color(50, 50, 50));
        MnTransferPasienKeluarRS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/category.png"))); // NOI18N
        MnTransferPasienKeluarRS.setText("Transfer Pasien Keluar RS");
        MnTransferPasienKeluarRS.setName("MnTransferPasienKeluarRS"); // NOI18N
        MnTransferPasienKeluarRS.setPreferredSize(new java.awt.Dimension(220, 26));
        MnTransferPasienKeluarRS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MnTransferPasienKeluarRSActionPerformed(evt);
            }
        });
        jPopupMenu1.add(MnTransferPasienKeluarRS);

        LoadHTML.setBorder(null);
        LoadHTML.setName("LoadHTML"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        internalFrame1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(240, 245, 235)), "::[ Transfer Pasien Antar Ruang ]::", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 11), new java.awt.Color(50, 50, 50))); // NOI18N
        internalFrame1.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        internalFrame1.setName("internalFrame1"); // NOI18N
        internalFrame1.setLayout(new java.awt.BorderLayout(1, 1));

        panelGlass8.setName("panelGlass8"); // NOI18N
        panelGlass8.setPreferredSize(new java.awt.Dimension(44, 54));
        panelGlass8.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        BtnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/save-16x16.png"))); // NOI18N
        BtnSimpan.setMnemonic('S');
        BtnSimpan.setText("Simpan");
        BtnSimpan.setToolTipText("Alt+S");
        BtnSimpan.setName("BtnSimpan"); // NOI18N
        BtnSimpan.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSimpanActionPerformed(evt);
            }
        });
        BtnSimpan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnSimpanKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnSimpan);

        BtnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/Cancel-2-16x16.png"))); // NOI18N
        BtnBatal.setMnemonic('B');
        BtnBatal.setText("Baru");
        BtnBatal.setToolTipText("Alt+B");
        BtnBatal.setName("BtnBatal"); // NOI18N
        BtnBatal.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnBatalActionPerformed(evt);
            }
        });
        BtnBatal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnBatalKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnBatal);

        BtnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/stop_f2.png"))); // NOI18N
        BtnHapus.setMnemonic('H');
        BtnHapus.setText("Hapus");
        BtnHapus.setToolTipText("Alt+H");
        BtnHapus.setName("BtnHapus"); // NOI18N
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

        BtnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/inventaris.png"))); // NOI18N
        BtnEdit.setMnemonic('G');
        BtnEdit.setText("Ganti");
        BtnEdit.setToolTipText("Alt+G");
        BtnEdit.setName("BtnEdit"); // NOI18N
        BtnEdit.setPreferredSize(new java.awt.Dimension(100, 30));
        BtnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnEditActionPerformed(evt);
            }
        });
        BtnEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnEditKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnEdit);

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
        BtnPrint.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnPrintKeyPressed(evt);
            }
        });
        panelGlass8.add(BtnPrint);

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

        internalFrame1.add(panelGlass8, java.awt.BorderLayout.PAGE_END);

        TabRawat.setBackground(new java.awt.Color(254, 255, 254));
        TabRawat.setForeground(new java.awt.Color(50, 50, 50));
        TabRawat.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        TabRawat.setName("TabRawat"); // NOI18N
        TabRawat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TabRawatMouseClicked(evt);
            }
        });

        internalFrame2.setBorder(null);
        internalFrame2.setName("internalFrame2"); // NOI18N
        internalFrame2.setLayout(new java.awt.BorderLayout(1, 1));

        scrollInput.setName("scrollInput"); // NOI18N
        scrollInput.setPreferredSize(new java.awt.Dimension(102, 557));

        FormInput.setBackground(new java.awt.Color(255, 255, 255));
        FormInput.setBorder(null);
        FormInput.setName("FormInput"); // NOI18N
        FormInput.setPreferredSize(new java.awt.Dimension(870, 663));
        FormInput.setLayout(null);

        jSeparator14.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator14.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator14.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator14.setName("jSeparator14"); // NOI18N
        FormInput.add(jSeparator14);
        jSeparator14.setBounds(0, 861, 880, 0);

        MenyetujuiPemindahan.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ya", "Tidak" }));
        MenyetujuiPemindahan.setName("MenyetujuiPemindahan"); // NOI18N
        MenyetujuiPemindahan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                MenyetujuiPemindahanKeyPressed(evt);
            }
        });
        FormInput.add(MenyetujuiPemindahan);
        MenyetujuiPemindahan.setBounds(770, 80, 80, 23);

        TNoRw.setHighlighter(null);
        TNoRw.setName("TNoRw"); // NOI18N
        TNoRw.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TNoRwKeyPressed(evt);
            }
        });
        FormInput.add(TNoRw);
        TNoRw.setBounds(74, 10, 131, 23);

        TPasien.setEditable(false);
        TPasien.setHighlighter(null);
        TPasien.setName("TPasien"); // NOI18N
        FormInput.add(TPasien);
        TPasien.setBounds(309, 10, 260, 23);

        TNoRM.setEditable(false);
        TNoRM.setHighlighter(null);
        TNoRM.setName("TNoRM"); // NOI18N
        FormInput.add(TNoRM);
        TNoRM.setBounds(207, 10, 100, 23);

        jLabel8.setText("Tgl.Lahir :");
        jLabel8.setName("jLabel8"); // NOI18N
        FormInput.add(jLabel8);
        jLabel8.setBounds(580, 10, 60, 23);

        TglLahir.setEditable(false);
        TglLahir.setHighlighter(null);
        TglLahir.setName("TglLahir"); // NOI18N
        FormInput.add(TglLahir);
        TglLahir.setBounds(644, 10, 80, 23);

        Jk.setEditable(false);
        Jk.setHighlighter(null);
        Jk.setName("Jk"); // NOI18N
        FormInput.add(Jk);
        Jk.setBounds(774, 10, 80, 23);

        jLabel10.setText("No.Rawat :");
        jLabel10.setName("jLabel10"); // NOI18N
        FormInput.add(jLabel10);
        jLabel10.setBounds(0, 10, 70, 23);

        jLabel11.setText("J.K. :");
        jLabel11.setName("jLabel11"); // NOI18N
        FormInput.add(jLabel11);
        jLabel11.setBounds(740, 10, 30, 23);

        label12.setText("Tanggal Rujuk :");
        label12.setName("label12"); // NOI18N
        label12.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label12);
        label12.setBounds(620, 40, 90, 23);

        TanggalRujukKeluarRS.setForeground(new java.awt.Color(50, 70, 50));
        TanggalRujukKeluarRS.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "01-12-2024 17:17:31" }));
        TanggalRujukKeluarRS.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TanggalRujukKeluarRS.setName("TanggalRujukKeluarRS"); // NOI18N
        TanggalRujukKeluarRS.setOpaque(false);
        TanggalRujukKeluarRS.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalRujukKeluarRSKeyPressed(evt);
            }
        });
        FormInput.add(TanggalRujukKeluarRS);
        TanggalRujukKeluarRS.setBounds(720, 40, 130, 23);

        jSeparator1.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator1.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator1.setName("jSeparator1"); // NOI18N
        FormInput.add(jSeparator1);
        jSeparator1.setBounds(0, 70, 880, 1);

        RuangSelanjutnya.setHighlighter(null);
        RuangSelanjutnya.setName("RuangSelanjutnya"); // NOI18N
        RuangSelanjutnya.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RuangSelanjutnyaKeyPressed(evt);
            }
        });
        FormInput.add(RuangSelanjutnya);
        RuangSelanjutnya.setBounds(350, 42, 260, 23);

        jLabel15.setText("Rujuk ke RS :");
        jLabel15.setName("jLabel15"); // NOI18N
        FormInput.add(jLabel15);
        jLabel15.setBounds(270, 42, 70, 23);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel16.setText("Diagnosa Medis :");
        jLabel16.setName("jLabel16"); // NOI18N
        FormInput.add(jLabel16);
        jLabel16.setBounds(10, 80, 90, 23);

        DiagnosaUtama.setHighlighter(null);
        DiagnosaUtama.setName("DiagnosaUtama"); // NOI18N
        DiagnosaUtama.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DiagnosaUtamaKeyPressed(evt);
            }
        });
        FormInput.add(DiagnosaUtama);
        DiagnosaUtama.setBounds(100, 80, 340, 23);

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel18.setText("Asal Ruang / Poliklinik :");
        jLabel18.setName("jLabel18"); // NOI18N
        FormInput.add(jLabel18);
        jLabel18.setBounds(10, 42, 110, 23);

        jLabel34.setText("Pasien/Keluarga Mengetahui & Menyetujui Alasan Pemindahan :");
        jLabel34.setName("jLabel34"); // NOI18N
        FormInput.add(jLabel34);
        jLabel34.setBounds(440, 80, 320, 23);

        AsalRuang.setHighlighter(null);
        AsalRuang.setName("AsalRuang"); // NOI18N
        AsalRuang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AsalRuangKeyPressed(evt);
            }
        });
        FormInput.add(AsalRuang);
        AsalRuang.setBounds(130, 42, 140, 23);

        NamaPJMenyetujui.setHighlighter(null);
        NamaPJMenyetujui.setName("NamaPJMenyetujui"); // NOI18N
        NamaPJMenyetujui.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NamaPJMenyetujuiKeyPressed(evt);
            }
        });
        FormInput.add(NamaPJMenyetujui);
        NamaPJMenyetujui.setBounds(370, 112, 240, 23);

        jLabel38.setText("Hubungan :");
        jLabel38.setName("jLabel38"); // NOI18N
        FormInput.add(jLabel38);
        jLabel38.setBounds(620, 112, 70, 23);

        HubunganPJMenyetujui.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Kakak", "Adik", "Saudara", "Keluarga", "Kakek", "Nenek", "Orang Tua", "Suami", "Istri", "Penanggung Jawab", "Menantu", "Ipar", "Mertua", "-" }));
        HubunganPJMenyetujui.setName("HubunganPJMenyetujui"); // NOI18N
        HubunganPJMenyetujui.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                HubunganPJMenyetujuiKeyPressed(evt);
            }
        });
        FormInput.add(HubunganPJMenyetujui);
        HubunganPJMenyetujui.setBounds(700, 112, 150, 23);

        jLabel39.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel39.setText("Pemberi Persetujuan Adalah Keluarga/Penanggung Jawab Pasien, Nama :");
        jLabel39.setName("jLabel39"); // NOI18N
        FormInput.add(jLabel39);
        jLabel39.setBounds(10, 112, 370, 23);

        jLabel40.setText("Tanggal Kontak :");
        jLabel40.setName("jLabel40"); // NOI18N
        FormInput.add(jLabel40);
        jLabel40.setBounds(620, 170, 90, 20);

        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel41.setText("Kondisi Pasien :");
        jLabel41.setName("jLabel41"); // NOI18N
        FormInput.add(jLabel41);
        jLabel41.setBounds(410, 250, 80, 23);

        scrollPane5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane5.setName("scrollPane5"); // NOI18N

        KeluhanUtamaSebelumTransfer.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        KeluhanUtamaSebelumTransfer.setColumns(20);
        KeluhanUtamaSebelumTransfer.setRows(5);
        KeluhanUtamaSebelumTransfer.setName("KeluhanUtamaSebelumTransfer"); // NOI18N
        KeluhanUtamaSebelumTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeluhanUtamaSebelumTransferKeyPressed(evt);
            }
        });
        scrollPane5.setViewportView(KeluhanUtamaSebelumTransfer);

        FormInput.add(scrollPane5);
        scrollPane5.setBounds(490, 240, 360, 50);

        jLabel42.setText("Keadaan Umum :");
        jLabel42.setName("jLabel42"); // NOI18N
        FormInput.add(jLabel42);
        jLabel42.setBounds(12, 260, 90, 23);

        KeadaanUmumSebelumTransfer.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Compos Mentis", "Gelisah", "Delirium", "Koma" }));
        KeadaanUmumSebelumTransfer.setName("KeadaanUmumSebelumTransfer"); // NOI18N
        KeadaanUmumSebelumTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeadaanUmumSebelumTransferKeyPressed(evt);
            }
        });
        FormInput.add(KeadaanUmumSebelumTransfer);
        KeadaanUmumSebelumTransfer.setBounds(110, 260, 130, 23);

        TDSebelumTransfer.setFocusTraversalPolicyProvider(true);
        TDSebelumTransfer.setName("TDSebelumTransfer"); // NOI18N
        TDSebelumTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TDSebelumTransferKeyPressed(evt);
            }
        });
        FormInput.add(TDSebelumTransfer);
        TDSebelumTransfer.setBounds(210, 290, 60, 23);

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel23.setText("mmHg");
        jLabel23.setName("jLabel23"); // NOI18N
        FormInput.add(jLabel23);
        jLabel23.setBounds(280, 290, 50, 23);

        jLabel17.setText("Nadi :");
        jLabel17.setName("jLabel17"); // NOI18N
        FormInput.add(jLabel17);
        jLabel17.setBounds(-30, 290, 80, 23);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel22.setText("x/menit");
        jLabel22.setName("jLabel22"); // NOI18N
        FormInput.add(jLabel22);
        jLabel22.setBounds(130, 290, 50, 23);

        NadiSebelumTransfer.setFocusTraversalPolicyProvider(true);
        NadiSebelumTransfer.setName("NadiSebelumTransfer"); // NOI18N
        NadiSebelumTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NadiSebelumTransferKeyPressed(evt);
            }
        });
        FormInput.add(NadiSebelumTransfer);
        NadiSebelumTransfer.setBounds(60, 290, 60, 23);

        jLabel26.setText("RR :");
        jLabel26.setName("jLabel26"); // NOI18N
        FormInput.add(jLabel26);
        jLabel26.setBounds(170, 320, 30, 23);

        RRSebelumTransfer.setFocusTraversalPolicyProvider(true);
        RRSebelumTransfer.setName("RRSebelumTransfer"); // NOI18N
        RRSebelumTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                RRSebelumTransferKeyPressed(evt);
            }
        });
        FormInput.add(RRSebelumTransfer);
        RRSebelumTransfer.setBounds(210, 320, 60, 23);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel25.setText("x/menit");
        jLabel25.setName("jLabel25"); // NOI18N
        FormInput.add(jLabel25);
        jLabel25.setBounds(280, 320, 50, 23);

        jLabel24.setText("Suhu :");
        jLabel24.setName("jLabel24"); // NOI18N
        FormInput.add(jLabel24);
        jLabel24.setBounds(-30, 320, 80, 23);

        SuhuSebelumTransfer.setFocusTraversalPolicyProvider(true);
        SuhuSebelumTransfer.setName("SuhuSebelumTransfer"); // NOI18N
        SuhuSebelumTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                SuhuSebelumTransferKeyPressed(evt);
            }
        });
        FormInput.add(SuhuSebelumTransfer);
        SuhuSebelumTransfer.setBounds(60, 320, 60, 23);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel27.setText("°C");
        jLabel27.setName("jLabel27"); // NOI18N
        FormInput.add(jLabel27);
        jLabel27.setBounds(130, 320, 30, 23);

        jLabel28.setText("TD :");
        jLabel28.setName("jLabel28"); // NOI18N
        FormInput.add(jLabel28);
        jLabel28.setBounds(170, 290, 30, 23);

        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel46.setText("Kejadian Klinis selama proses transfer :");
        jLabel46.setName("jLabel46"); // NOI18N
        FormInput.add(jLabel46);
        jLabel46.setBounds(530, 480, 200, 23);

        scrollPane6.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane6.setName("scrollPane6"); // NOI18N

        KeluhanUtamaSetelahTransfer.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        KeluhanUtamaSetelahTransfer.setColumns(20);
        KeluhanUtamaSetelahTransfer.setRows(5);
        KeluhanUtamaSetelahTransfer.setName("KeluhanUtamaSetelahTransfer"); // NOI18N
        KeluhanUtamaSetelahTransfer.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KeluhanUtamaSetelahTransferKeyPressed(evt);
            }
        });
        scrollPane6.setViewportView(KeluhanUtamaSetelahTransfer);

        FormInput.add(scrollPane6);
        scrollPane6.setBounds(530, 502, 320, 100);

        jSeparator7.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator7.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator7.setName("jSeparator7"); // NOI18N
        FormInput.add(jSeparator7);
        jSeparator7.setBounds(0, 610, 880, 1);

        label14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        label14.setName("label14"); // NOI18N
        label14.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label14);
        label14.setBounds(15, 610, 130, 23);

        KdDokterMenyerahkan.setEditable(false);
        KdDokterMenyerahkan.setName("KdDokterMenyerahkan"); // NOI18N
        KdDokterMenyerahkan.setPreferredSize(new java.awt.Dimension(80, 23));
        FormInput.add(KdDokterMenyerahkan);
        KdDokterMenyerahkan.setBounds(190, 630, 100, 23);

        NmDokterMenyerahkan.setEditable(false);
        NmDokterMenyerahkan.setName("NmDokterMenyerahkan"); // NOI18N
        NmDokterMenyerahkan.setPreferredSize(new java.awt.Dimension(207, 23));
        FormInput.add(NmDokterMenyerahkan);
        NmDokterMenyerahkan.setBounds(290, 630, 180, 23);

        BtnDokter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/190.png"))); // NOI18N
        BtnDokter.setMnemonic('2');
        BtnDokter.setToolTipText("Alt+2");
        BtnDokter.setName("BtnDokter"); // NOI18N
        BtnDokter.setPreferredSize(new java.awt.Dimension(28, 23));
        BtnDokter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDokterActionPerformed(evt);
            }
        });
        BtnDokter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                BtnDokterKeyPressed(evt);
            }
        });
        FormInput.add(BtnDokter);
        BtnDokter.setBounds(470, 630, 28, 23);

        label16.setText("Dokter/Petugas  :");
        label16.setName("label16"); // NOI18N
        label16.setPreferredSize(new java.awt.Dimension(70, 23));
        FormInput.add(label16);
        label16.setBounds(30, 630, 150, 23);

        jLabel53.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel53.setText(" Dokumen yang disertakan :");
        jLabel53.setName("jLabel53"); // NOI18N
        FormInput.add(jLabel53);
        jLabel53.setBounds(304, 480, 160, 23);

        jLabel54.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel54.setText("Rekam Medis");
        jLabel54.setName("jLabel54"); // NOI18N
        FormInput.add(jLabel54);
        jLabel54.setBounds(330, 500, 70, 23);

        ChkRM.setBorder(null);
        ChkRM.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkRM.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkRM.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkRM.setName("ChkRM"); // NOI18N
        FormInput.add(ChkRM);
        ChkRM.setBounds(300, 500, 23, 23);

        ChkLAB.setBorder(null);
        ChkLAB.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkLAB.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkLAB.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkLAB.setName("ChkLAB"); // NOI18N
        FormInput.add(ChkLAB);
        ChkLAB.setBounds(300, 520, 23, 23);

        jLabel55.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel55.setText("Hasil Laboratorium");
        jLabel55.setName("jLabel55"); // NOI18N
        FormInput.add(jLabel55);
        jLabel55.setBounds(330, 520, 90, 23);

        ChkRAD.setBorder(null);
        ChkRAD.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkRAD.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkRAD.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkRAD.setName("ChkRAD"); // NOI18N
        FormInput.add(ChkRAD);
        ChkRAD.setBounds(300, 540, 23, 23);

        jLabel56.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel56.setText("Hasil Radiologi");
        jLabel56.setName("jLabel56"); // NOI18N
        FormInput.add(jLabel56);
        jLabel56.setBounds(330, 540, 90, 23);

        ChkLainnya.setBorder(null);
        ChkLainnya.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkLainnya.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkLainnya.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkLainnya.setName("ChkLainnya"); // NOI18N
        FormInput.add(ChkLainnya);
        ChkLainnya.setBounds(300, 560, 23, 23);

        jLabel58.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel58.setText("Lainnya :");
        jLabel58.setName("jLabel58"); // NOI18N
        FormInput.add(jLabel58);
        jLabel58.setBounds(330, 560, 50, 23);

        KetDokLainnya.setHighlighter(null);
        KetDokLainnya.setName("KetDokLainnya"); // NOI18N
        KetDokLainnya.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KetDokLainnyaActionPerformed(evt);
            }
        });
        KetDokLainnya.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KetDokLainnyaKeyPressed(evt);
            }
        });
        FormInput.add(KetDokLainnya);
        KetDokLainnya.setBounds(380, 560, 130, 23);

        jLabel59.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel59.setText("Alasan Merujuk :");
        jLabel59.setName("jLabel59"); // NOI18N
        FormInput.add(jLabel59);
        jLabel59.setBounds(17, 350, 160, 20);

        ChkKlinikal.setBorder(null);
        ChkKlinikal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkKlinikal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkKlinikal.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkKlinikal.setName("ChkKlinikal"); // NOI18N
        FormInput.add(ChkKlinikal);
        ChkKlinikal.setBounds(10, 370, 23, 23);

        jLabel60.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel60.setText("Klinikal :");
        jLabel60.setName("jLabel60"); // NOI18N
        FormInput.add(jLabel60);
        jLabel60.setBounds(40, 370, 40, 23);

        KetKlinikal.setHighlighter(null);
        KetKlinikal.setName("KetKlinikal"); // NOI18N
        KetKlinikal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KetKlinikalKeyPressed(evt);
            }
        });
        FormInput.add(KetKlinikal);
        KetKlinikal.setBounds(110, 370, 200, 23);

        ChkNonKlinikal.setBorder(null);
        ChkNonKlinikal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkNonKlinikal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkNonKlinikal.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkNonKlinikal.setName("ChkNonKlinikal"); // NOI18N
        FormInput.add(ChkNonKlinikal);
        ChkNonKlinikal.setBounds(10, 400, 23, 23);

        jLabel61.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel61.setText("Non Klinikal :");
        jLabel61.setName("jLabel61"); // NOI18N
        FormInput.add(jLabel61);
        jLabel61.setBounds(40, 400, 60, 23);

        KetNonKlinikal.setHighlighter(null);
        KetNonKlinikal.setName("KetNonKlinikal"); // NOI18N
        KetNonKlinikal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KetNonKlinikalKeyPressed(evt);
            }
        });
        FormInput.add(KetNonKlinikal);
        KetNonKlinikal.setBounds(110, 400, 200, 23);

        ChkICUPenuh.setBorder(null);
        ChkICUPenuh.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkICUPenuh.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkICUPenuh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkICUPenuh.setName("ChkICUPenuh"); // NOI18N
        FormInput.add(ChkICUPenuh);
        ChkICUPenuh.setBounds(10, 428, 23, 23);

        jLabel62.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel62.setText("Tidak ada tempat di ICU");
        jLabel62.setName("jLabel62"); // NOI18N
        FormInput.add(jLabel62);
        jLabel62.setBounds(40, 428, 130, 23);

        ChkRanapPenuh.setBorder(null);
        ChkRanapPenuh.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkRanapPenuh.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkRanapPenuh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkRanapPenuh.setName("ChkRanapPenuh"); // NOI18N
        FormInput.add(ChkRanapPenuh);
        ChkRanapPenuh.setBounds(10, 450, 23, 23);

        jLabel63.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel63.setText("Ruang Rawat Inap Penuh");
        jLabel63.setName("jLabel63"); // NOI18N
        FormInput.add(jLabel63);
        jLabel63.setBounds(40, 450, 130, 23);

        ChkPermintaanPasien.setBorder(null);
        ChkPermintaanPasien.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkPermintaanPasien.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkPermintaanPasien.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkPermintaanPasien.setName("ChkPermintaanPasien"); // NOI18N
        FormInput.add(ChkPermintaanPasien);
        ChkPermintaanPasien.setBounds(170, 428, 23, 23);

        jLabel64.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel64.setText("Permintaan Pasien/Keluarga");
        jLabel64.setName("jLabel64"); // NOI18N
        FormInput.add(jLabel64);
        jLabel64.setBounds(200, 428, 180, 23);

        ChkAlasanLainnya.setBorder(null);
        ChkAlasanLainnya.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkAlasanLainnya.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkAlasanLainnya.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkAlasanLainnya.setName("ChkAlasanLainnya"); // NOI18N
        FormInput.add(ChkAlasanLainnya);
        ChkAlasanLainnya.setBounds(170, 450, 23, 23);

        jLabel65.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel65.setText("Lain-lain :");
        jLabel65.setName("jLabel65"); // NOI18N
        FormInput.add(jLabel65);
        jLabel65.setBounds(200, 450, 50, 23);

        AlasanLainnya.setHighlighter(null);
        AlasanLainnya.setName("AlasanLainnya"); // NOI18N
        AlasanLainnya.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                AlasanLainnyaKeyPressed(evt);
            }
        });
        FormInput.add(AlasanLainnya);
        AlasanLainnya.setBounds(250, 450, 170, 23);

        jLabel66.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel66.setText("Riwayat Penyakit :");
        jLabel66.setName("jLabel66"); // NOI18N
        FormInput.add(jLabel66);
        jLabel66.setBounds(17, 480, 160, 23);

        ChkTdkAdaRiwayat.setBorder(null);
        ChkTdkAdaRiwayat.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkTdkAdaRiwayat.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkTdkAdaRiwayat.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkTdkAdaRiwayat.setName("ChkTdkAdaRiwayat"); // NOI18N
        FormInput.add(ChkTdkAdaRiwayat);
        ChkTdkAdaRiwayat.setBounds(10, 500, 23, 23);

        jLabel67.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel67.setText("Tidak Ada");
        jLabel67.setName("jLabel67"); // NOI18N
        FormInput.add(jLabel67);
        jLabel67.setBounds(40, 500, 70, 23);

        ChkAdaRiwayat.setBorder(null);
        ChkAdaRiwayat.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkAdaRiwayat.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkAdaRiwayat.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkAdaRiwayat.setName("ChkAdaRiwayat"); // NOI18N
        FormInput.add(ChkAdaRiwayat);
        ChkAdaRiwayat.setBounds(10, 520, 23, 23);

        jLabel68.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel68.setText("Ada");
        jLabel68.setName("jLabel68"); // NOI18N
        FormInput.add(jLabel68);
        jLabel68.setBounds(40, 520, 90, 23);

        ChkPDM.setBorder(null);
        ChkPDM.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkPDM.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkPDM.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkPDM.setName("ChkPDM"); // NOI18N
        FormInput.add(ChkPDM);
        ChkPDM.setBounds(10, 540, 23, 23);

        jLabel69.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel69.setText("DM");
        jLabel69.setName("jLabel69"); // NOI18N
        FormInput.add(jLabel69);
        jLabel69.setBounds(40, 540, 90, 23);

        ChkPLainnya.setBorder(null);
        ChkPLainnya.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkPLainnya.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkPLainnya.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkPLainnya.setName("ChkPLainnya"); // NOI18N
        FormInput.add(ChkPLainnya);
        ChkPLainnya.setBounds(10, 580, 23, 23);

        jLabel70.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel70.setText("Lain-lain :");
        jLabel70.setName("jLabel70"); // NOI18N
        FormInput.add(jLabel70);
        jLabel70.setBounds(40, 580, 50, 23);

        KetPLainnya.setHighlighter(null);
        KetPLainnya.setName("KetPLainnya"); // NOI18N
        KetPLainnya.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KetPLainnyaKeyPressed(evt);
            }
        });
        FormInput.add(KetPLainnya);
        KetPLainnya.setBounds(90, 580, 170, 23);

        ChkPJantung.setBorder(null);
        ChkPJantung.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkPJantung.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkPJantung.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkPJantung.setName("ChkPJantung"); // NOI18N
        FormInput.add(ChkPJantung);
        ChkPJantung.setBounds(10, 560, 23, 23);

        jLabel71.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel71.setText("Jantung");
        jLabel71.setName("jLabel71"); // NOI18N
        FormInput.add(jLabel71);
        jLabel71.setBounds(40, 560, 90, 23);

        jLabel72.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel72.setText("Pemakaian Peralatan Medis :");
        jLabel72.setName("jLabel72"); // NOI18N
        FormInput.add(jLabel72);
        jLabel72.setBounds(437, 350, 160, 23);

        ChkTAlmed.setBorder(null);
        ChkTAlmed.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkTAlmed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkTAlmed.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkTAlmed.setName("ChkTAlmed"); // NOI18N
        FormInput.add(ChkTAlmed);
        ChkTAlmed.setBounds(430, 370, 23, 23);

        jLabel73.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel73.setText("Tidak");
        jLabel73.setName("jLabel73"); // NOI18N
        FormInput.add(jLabel73);
        jLabel73.setBounds(460, 370, 70, 23);

        ChkYaAlmed.setBorder(null);
        ChkYaAlmed.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkYaAlmed.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkYaAlmed.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkYaAlmed.setName("ChkYaAlmed"); // NOI18N
        FormInput.add(ChkYaAlmed);
        ChkYaAlmed.setBounds(430, 390, 23, 23);

        jLabel74.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel74.setText("Ada");
        jLabel74.setName("jLabel74"); // NOI18N
        FormInput.add(jLabel74);
        jLabel74.setBounds(460, 390, 30, 23);

        ChkPInfus.setBorder(null);
        ChkPInfus.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkPInfus.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkPInfus.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkPInfus.setName("ChkPInfus"); // NOI18N
        FormInput.add(ChkPInfus);
        ChkPInfus.setBounds(490, 390, 23, 23);

        jLabel75.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel75.setText("Infus");
        jLabel75.setName("jLabel75"); // NOI18N
        FormInput.add(jLabel75);
        jLabel75.setBounds(520, 390, 50, 23);

        ChkPO2.setBorder(null);
        ChkPO2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkPO2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkPO2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkPO2.setName("ChkPO2"); // NOI18N
        FormInput.add(ChkPO2);
        ChkPO2.setBounds(490, 430, 23, 23);

        jLabel76.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel76.setText("O2");
        jLabel76.setName("jLabel76"); // NOI18N
        FormInput.add(jLabel76);
        jLabel76.setBounds(520, 430, 40, 23);

        ChkPETT.setBorder(null);
        ChkPETT.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkPETT.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkPETT.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkPETT.setName("ChkPETT"); // NOI18N
        FormInput.add(ChkPETT);
        ChkPETT.setBounds(490, 410, 23, 23);

        jLabel77.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel77.setText("ETT");
        jLabel77.setName("jLabel77"); // NOI18N
        FormInput.add(jLabel77);
        jLabel77.setBounds(520, 410, 50, 23);

        ChkPNGT.setBorder(null);
        ChkPNGT.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkPNGT.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkPNGT.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkPNGT.setName("ChkPNGT"); // NOI18N
        FormInput.add(ChkPNGT);
        ChkPNGT.setBounds(570, 390, 23, 23);

        jLabel78.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel78.setText("NGT");
        jLabel78.setName("jLabel78"); // NOI18N
        FormInput.add(jLabel78);
        jLabel78.setBounds(600, 390, 70, 23);

        ChkPCateter.setBorder(null);
        ChkPCateter.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkPCateter.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkPCateter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkPCateter.setName("ChkPCateter"); // NOI18N
        FormInput.add(ChkPCateter);
        ChkPCateter.setBounds(570, 410, 23, 23);

        jLabel79.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel79.setText("Cateter");
        jLabel79.setName("jLabel79"); // NOI18N
        FormInput.add(jLabel79);
        jLabel79.setBounds(600, 410, 90, 23);

        ChkPBidal.setBorder(null);
        ChkPBidal.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkPBidal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkPBidal.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkPBidal.setName("ChkPBidal"); // NOI18N
        FormInput.add(ChkPBidal);
        ChkPBidal.setBounds(570, 430, 23, 23);

        jLabel80.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel80.setText("Bidal");
        jLabel80.setName("jLabel80"); // NOI18N
        FormInput.add(jLabel80);
        jLabel80.setBounds(600, 430, 90, 23);

        ChkPPump.setBorder(null);
        ChkPPump.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkPPump.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkPPump.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkPPump.setName("ChkPPump"); // NOI18N
        FormInput.add(ChkPPump);
        ChkPPump.setBounds(570, 450, 23, 23);

        jLabel82.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel82.setText("Pump");
        jLabel82.setName("jLabel82"); // NOI18N
        FormInput.add(jLabel82);
        jLabel82.setBounds(600, 450, 90, 23);

        ChkPCVC.setBorder(null);
        ChkPCVC.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkPCVC.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkPCVC.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkPCVC.setName("ChkPCVC"); // NOI18N
        FormInput.add(ChkPCVC);
        ChkPCVC.setBounds(490, 450, 23, 23);

        jLabel83.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel83.setText("CVC");
        jLabel83.setName("jLabel83"); // NOI18N
        FormInput.add(jLabel83);
        jLabel83.setBounds(520, 450, 40, 23);

        ChkPAlmedLainnya.setBorder(null);
        ChkPAlmedLainnya.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        ChkPAlmedLainnya.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ChkPAlmedLainnya.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        ChkPAlmedLainnya.setName("ChkPAlmedLainnya"); // NOI18N
        ChkPAlmedLainnya.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChkPAlmedLainnyaActionPerformed(evt);
            }
        });
        FormInput.add(ChkPAlmedLainnya);
        ChkPAlmedLainnya.setBounds(630, 390, 23, 23);

        jLabel81.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel81.setText("Lain-lain :");
        jLabel81.setName("jLabel81"); // NOI18N
        FormInput.add(jLabel81);
        jLabel81.setBounds(660, 390, 50, 23);

        KetPAmedLainnya.setHighlighter(null);
        KetPAmedLainnya.setName("KetPAmedLainnya"); // NOI18N
        KetPAmedLainnya.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                KetPAmedLainnyaKeyPressed(evt);
            }
        });
        FormInput.add(KetPAmedLainnya);
        KetPAmedLainnya.setBounds(710, 390, 140, 23);

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel43.setText("Keadaan Pasien Saat Sebelum Transfer :");
        jLabel43.setName("jLabel43"); // NOI18N
        FormInput.add(jLabel43);
        jLabel43.setBounds(10, 230, 320, 23);

        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel44.setText("Staf penghubung dari RS :");
        jLabel44.setName("jLabel44"); // NOI18N
        FormInput.add(jLabel44);
        jLabel44.setBounds(10, 142, 140, 20);

        jLabel45.setText("Nama :");
        jLabel45.setName("jLabel45"); // NOI18N
        FormInput.add(jLabel45);
        jLabel45.setBounds(10, 168, 50, 20);

        jLabel47.setText("No Telp :");
        jLabel47.setName("jLabel47"); // NOI18N
        FormInput.add(jLabel47);
        jLabel47.setBounds(10, 200, 50, 20);

        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel49.setText("Staf RS Tujuan yang dihubungi :");
        jLabel49.setName("jLabel49"); // NOI18N
        FormInput.add(jLabel49);
        jLabel49.setBounds(340, 142, 200, 20);

        jLabel50.setText("Nama :");
        jLabel50.setName("jLabel50"); // NOI18N
        FormInput.add(jLabel50);
        jLabel50.setBounds(330, 170, 50, 20);

        jLabel51.setText("No Telp :");
        jLabel51.setName("jLabel51"); // NOI18N
        FormInput.add(jLabel51);
        jLabel51.setBounds(330, 200, 50, 20);

        NamaPenghubungRS.setHighlighter(null);
        NamaPenghubungRS.setName("NamaPenghubungRS"); // NOI18N
        NamaPenghubungRS.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NamaPenghubungRSKeyPressed(evt);
            }
        });
        FormInput.add(NamaPenghubungRS);
        NamaPenghubungRS.setBounds(70, 168, 250, 23);

        NoTelpPenghubungRS.setHighlighter(null);
        NoTelpPenghubungRS.setName("NoTelpPenghubungRS"); // NOI18N
        NoTelpPenghubungRS.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoTelpPenghubungRSKeyPressed(evt);
            }
        });
        FormInput.add(NoTelpPenghubungRS);
        NoTelpPenghubungRS.setBounds(70, 200, 250, 23);

        NamaPenerimaTujuanRS.setHighlighter(null);
        NamaPenerimaTujuanRS.setName("NamaPenerimaTujuanRS"); // NOI18N
        NamaPenerimaTujuanRS.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NamaPenerimaTujuanRSKeyPressed(evt);
            }
        });
        FormInput.add(NamaPenerimaTujuanRS);
        NamaPenerimaTujuanRS.setBounds(390, 170, 220, 23);

        NoTelpPenerimaTujuanRS.setHighlighter(null);
        NoTelpPenerimaTujuanRS.setName("NoTelpPenerimaTujuanRS"); // NOI18N
        NoTelpPenerimaTujuanRS.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                NoTelpPenerimaTujuanRSKeyPressed(evt);
            }
        });
        FormInput.add(NoTelpPenerimaTujuanRS);
        NoTelpPenerimaTujuanRS.setBounds(390, 200, 220, 23);

        TanggalKontakRS.setForeground(new java.awt.Color(50, 70, 50));
        TanggalKontakRS.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "01-12-2024 17:17:32" }));
        TanggalKontakRS.setDisplayFormat("dd-MM-yyyy HH:mm:ss");
        TanggalKontakRS.setName("TanggalKontakRS"); // NOI18N
        TanggalKontakRS.setOpaque(false);
        TanggalKontakRS.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TanggalKontakRSKeyPressed(evt);
            }
        });
        FormInput.add(TanggalKontakRS);
        TanggalKontakRS.setBounds(720, 170, 130, 23);

        scrollPane7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        scrollPane7.setName("scrollPane7"); // NOI18N

        ObatTelahDiberikan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        ObatTelahDiberikan.setColumns(20);
        ObatTelahDiberikan.setRows(5);
        ObatTelahDiberikan.setName("ObatTelahDiberikan"); // NOI18N
        ObatTelahDiberikan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ObatTelahDiberikanKeyPressed(evt);
            }
        });
        scrollPane7.setViewportView(ObatTelahDiberikan);

        FormInput.add(scrollPane7);
        scrollPane7.setBounds(490, 300, 360, 50);

        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel48.setText("Obat-obatan telah diberikan :");
        jLabel48.setName("jLabel48"); // NOI18N
        FormInput.add(jLabel48);
        jLabel48.setBounds(340, 310, 150, 30);

        jSeparator8.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator8.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator8.setName("jSeparator8"); // NOI18N
        FormInput.add(jSeparator8);
        jSeparator8.setBounds(0, 610, 880, 1);

        jSeparator10.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator10.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator10.setName("jSeparator10"); // NOI18N
        FormInput.add(jSeparator10);
        jSeparator10.setBounds(0, 230, 880, 1);

        jSeparator11.setBackground(new java.awt.Color(239, 244, 234));
        jSeparator11.setForeground(new java.awt.Color(239, 244, 234));
        jSeparator11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(239, 244, 234)));
        jSeparator11.setName("jSeparator11"); // NOI18N
        FormInput.add(jSeparator11);
        jSeparator11.setBounds(0, 140, 880, 1);

        scrollInput.setViewportView(FormInput);

        internalFrame2.add(scrollInput, java.awt.BorderLayout.CENTER);

        TabRawat.addTab("Input Transfer Pasien", internalFrame2);

        internalFrame3.setBorder(null);
        internalFrame3.setName("internalFrame3"); // NOI18N
        internalFrame3.setLayout(new java.awt.BorderLayout(1, 1));

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

        internalFrame3.add(Scroll, java.awt.BorderLayout.CENTER);

        panelGlass9.setName("panelGlass9"); // NOI18N
        panelGlass9.setPreferredSize(new java.awt.Dimension(44, 44));
        panelGlass9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 9));

        jLabel19.setText("Tgl.Pindah :");
        jLabel19.setName("jLabel19"); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(68, 23));
        panelGlass9.add(jLabel19);

        DTPCari1.setForeground(new java.awt.Color(50, 70, 50));
        DTPCari1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "01-12-2024" }));
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
        DTPCari2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "01-12-2024" }));
        DTPCari2.setDisplayFormat("dd-MM-yyyy");
        DTPCari2.setName("DTPCari2"); // NOI18N
        DTPCari2.setOpaque(false);
        DTPCari2.setPreferredSize(new java.awt.Dimension(90, 23));
        panelGlass9.add(DTPCari2);

        jLabel6.setText("Key Word :");
        jLabel6.setName("jLabel6"); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(80, 23));
        panelGlass9.add(jLabel6);

        TCari.setName("TCari"); // NOI18N
        TCari.setPreferredSize(new java.awt.Dimension(197, 23));
        TCari.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                TCariKeyPressed(evt);
            }
        });
        panelGlass9.add(TCari);

        BtnCari.setIcon(new javax.swing.ImageIcon(getClass().getResource("/picture/accept.png"))); // NOI18N
        BtnCari.setMnemonic('3');
        BtnCari.setToolTipText("Alt+3");
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

        jLabel7.setText("Record :");
        jLabel7.setName("jLabel7"); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(60, 23));
        panelGlass9.add(jLabel7);

        LCount.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        LCount.setText("0");
        LCount.setName("LCount"); // NOI18N
        LCount.setPreferredSize(new java.awt.Dimension(70, 23));
        panelGlass9.add(LCount);

        internalFrame3.add(panelGlass9, java.awt.BorderLayout.PAGE_END);

        TabRawat.addTab("Data Transfer Pasien", internalFrame3);

        internalFrame1.add(TabRawat, java.awt.BorderLayout.CENTER);

        getContentPane().add(internalFrame1, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSimpanActionPerformed
        if(TNoRM.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"Nama Pasien");
        }else if(NmDokterMenyerahkan.getText().trim().equals("")){
            Valid.textKosong(BtnDokter,"Dokter Yang Menyerahkan");
/*        }else if(NmPetugasMenerima.getText().trim().equals("")){
            Valid.textKosong(BtnDokter,"Petugas Yang Menerima");
        }else if(AsalRuang.getText().trim().equals("")){
            Valid.textKosong(BtnMenerima,"Petugas Yang Menerima");*/
        }else if(AsalRuang.getText().trim().equals("")){
            Valid.textKosong(AsalRuang,"Asal Ruang");
        }else if(RuangSelanjutnya.getText().trim().equals("")){
            Valid.textKosong(RuangSelanjutnya,"Ruang Selanjutnya");
        }else if(DiagnosaUtama.getText().trim().equals("")){
            Valid.textKosong(DiagnosaUtama,"Diagnosa Utama");
//        }else if(ProsedurDilakukan.getText().trim().equals("")){
//            Valid.textKosong(ProsedurDilakukan,"Prosedur Dilakukan");
//        }else if(ObatYangDiberikan.getText().trim().equals("")){
//            Valid.textKosong(ObatYangDiberikan,"Obat Yang Diberikan");
        }else if(TDSebelumTransfer.getText().trim().equals("")){
            Valid.textKosong(TDSebelumTransfer,"TD Sebelum Transfer");
        }else if(NadiSebelumTransfer.getText().trim().equals("")){
            Valid.textKosong(NadiSebelumTransfer,"Nadi Sebelum Transfer");
        }else if(RRSebelumTransfer.getText().trim().equals("")){
            Valid.textKosong(RRSebelumTransfer,"RR Sebelum Transfer");
        }else if(SuhuSebelumTransfer.getText().trim().equals("")){
            Valid.textKosong(SuhuSebelumTransfer,"Suhu Sebelum Transfer");
        }else if(KeluhanUtamaSebelumTransfer.getText().trim().equals("")){
            Valid.textKosong(KeluhanUtamaSebelumTransfer,"Keluhan Utama Sebelum Transfer");
/*        }else if(TDSetelahTransfer.getText().trim().equals("")){
            Valid.textKosong(TDSetelahTransfer,"TD Setelah Transfer");
        }else if(NadiSetelahTransfer.getText().trim().equals("")){
            Valid.textKosong(NadiSetelahTransfer,"Nadi Setelah Transfer");
        }else if(RRSetelahTransfer.getText().trim().equals("")){
            Valid.textKosong(RRSetelahTransfer,"RR Setelah Transfer");
        }else if(SuhuSetelahTransfer.getText().trim().equals("")){
            Valid.textKosong(SuhuSetelahTransfer,"Suhu Setelah Transfer");*/
//        }else if(KeluhanUtamaSetelahTransfer.getText().trim().equals("")){
//            Valid.textKosong(KeluhanUtamaSetelahTransfer,"Keluhan Utama Setelah Transfer");
        }else{
            if(akses.getkode().equals("Admin Utama")){   
                simpan();
            }else {
                if(akses.getkode().equals(KdDokterMenyerahkan.getText())){//                if(akses.getkode().equals(KdPetugasMenerima.getText())||akses.getkode().equals(KdPetugasMenyerahkan.getText())){
                    simpan();
                }else{
                    JOptionPane.showMessageDialog(null,"Dokter sesuai user login..!!");
                }
            }
        }
}//GEN-LAST:event_BtnSimpanActionPerformed

    private void BtnSimpanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnSimpanKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnSimpanActionPerformed(null);
        }else{
            Valid.pindah(evt,BtnDokter,BtnBatal);//BtnMenerima
        }
}//GEN-LAST:event_BtnSimpanKeyPressed

    private void BtnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnBatalActionPerformed
        emptTeks();
}//GEN-LAST:event_BtnBatalActionPerformed

    private void BtnBatalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnBatalKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            emptTeks();
        }else{Valid.pindah(evt, BtnSimpan, BtnHapus);}
}//GEN-LAST:event_BtnBatalKeyPressed

    private void BtnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnHapusActionPerformed
        if(tbObat.getSelectedRow()>-1){
            if(akses.getkode().equals("Admin Utama")){
//            if(akses.getkode().equals("")){    
                
                hapus();
            }else {
                if(akses.getkode().equals(tbObat.getValueAt(tbObat.getSelectedRow(),57).toString())){ //||akses.getkode().equals(tbObat.getValueAt(tbObat.getSelectedRow(),36).toString())){
                    hapus();
                }else{
                    JOptionPane.showMessageDialog(null,"Hanya bisa dihapus oleh dokter yang bersangkutan..!!");
                }
            }
        }else{
            JOptionPane.showMessageDialog(rootPane,"Silahkan anda pilih data terlebih dahulu..!!");
        }             
            
}//GEN-LAST:event_BtnHapusActionPerformed

    private void BtnHapusKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnHapusKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnHapusActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnBatal, BtnEdit);
        }
}//GEN-LAST:event_BtnHapusKeyPressed

    private void BtnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnEditActionPerformed
        if(TNoRM.getText().trim().equals("")){
            Valid.textKosong(TNoRw,"Nama Pasien");
        }else if(NmDokterMenyerahkan.getText().trim().equals("")){
            Valid.textKosong(BtnDokter,"Dokter Yang Menyerahkan");
/*        }else if(NmPetugasMenerima.getText().trim().equals("")){
            Valid.textKosong(BtnDokter,"Petugas Yang Menerima");
        }else if(AsalRuang.getText().trim().equals("")){
            Valid.textKosong(BtnMenerima,"Petugas Yang Menerima");*/
        }else if(AsalRuang.getText().trim().equals("")){
            Valid.textKosong(AsalRuang,"Asal Ruang");
        }else if(RuangSelanjutnya.getText().trim().equals("")){
            Valid.textKosong(RuangSelanjutnya,"Ruang Selanjutnya");
        }else if(DiagnosaUtama.getText().trim().equals("")){
            Valid.textKosong(DiagnosaUtama,"Diagnosa Utama");
//        }else if(ProsedurDilakukan.getText().trim().equals("")){
//            Valid.textKosong(ProsedurDilakukan,"Prosedur Dilakukan");
//        }else if(ObatYangDiberikan.getText().trim().equals("")){
//            Valid.textKosong(ObatYangDiberikan,"Obat Yang Diberikan");
        }else if(NadiSebelumTransfer.getText().trim().equals("")){
            Valid.textKosong(NadiSebelumTransfer,"Nadi Sebelum Transfer");
        }else if(SuhuSebelumTransfer.getText().trim().equals("")){
            Valid.textKosong(SuhuSebelumTransfer,"Suhu Sebelum Transfer");   
        }else if(TDSebelumTransfer.getText().trim().equals("")){
            Valid.textKosong(TDSebelumTransfer,"TD Sebelum Transfer");            
        }else if(RRSebelumTransfer.getText().trim().equals("")){
            Valid.textKosong(RRSebelumTransfer,"RR Sebelum Transfer");
        }else if(KeluhanUtamaSebelumTransfer.getText().trim().equals("")){
            Valid.textKosong(KeluhanUtamaSebelumTransfer,"Keluhan Utama Sebelum Transfer");
/*        }else if(TDSetelahTransfer.getText().trim().equals("")){
            Valid.textKosong(TDSetelahTransfer,"TD Setelah Transfer");
        }else if(NadiSetelahTransfer.getText().trim().equals("")){
            Valid.textKosong(NadiSetelahTransfer,"Nadi Setelah Transfer");
        }else if(RRSetelahTransfer.getText().trim().equals("")){
            Valid.textKosong(RRSetelahTransfer,"RR Setelah Transfer");
        }else if(SuhuSetelahTransfer.getText().trim().equals("")){
            Valid.textKosong(SuhuSetelahTransfer,"Suhu Setelah Transfer"); */
//        }else if(KeluhanUtamaSetelahTransfer.getText().trim().equals("")){
//            Valid.textKosong(KeluhanUtamaSetelahTransfer,"Keluhan Utama Setelah Transfer");
        }else{
            if(tbObat.getSelectedRow()>-1){
                if(akses.getkode().equals("Admin Utama")){
//                if(akses.getkode().equals("")){    
                    ganti();
                }else {
                    if(akses.getkode().equals(tbObat.getValueAt(tbObat.getSelectedRow(),57).toString())){
                        ganti();
                    }else{
                        JOptionPane.showMessageDialog(null,"Hanya bisa diganti oleh dokter yang bersangkutan..!!");
                    }
                }
            }else{
                JOptionPane.showMessageDialog(rootPane,"Silahkan anda pilih data terlebih dahulu..!!");
            }
        }
}//GEN-LAST:event_BtnEditActionPerformed

    private void BtnEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnEditKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnEditActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnHapus, BtnPrint);
        }
}//GEN-LAST:event_BtnEditKeyPressed

    private void BtnKeluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnKeluarActionPerformed
        dispose();
}//GEN-LAST:event_BtnKeluarActionPerformed

    private void BtnKeluarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnKeluarKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnKeluarActionPerformed(null);
        }else{Valid.pindah(evt,BtnEdit,TCari);}
}//GEN-LAST:event_BtnKeluarKeyPressed

    private void BtnPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnPrintActionPerformed
       if(tbObat.getSelectedRow()>-1){
            Map<String, Object> param = new HashMap<>();
/*            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());
            param.put("logo",Sequel.cariGambar("select logo_rsup from gambar_tambahan"));
            param.put("logo_kemenkes",Sequel.cariGambar("select logo_kemenkes_baru from gambar_tambahan"));
            param.put("logo_akreditasi",Sequel.cariGambar("select logo_akreditasi from gambar_tambahan"));
            param.put("logo_blu",Sequel.cariGambar("select logo_blu from gambar_tambahan"));
            param.put("icon_maps",Sequel.cariGambar("select icon_maps from gambar_tambahan"));
            param.put("icon_telpon",Sequel.cariGambar("select icon_telpon from gambar_tambahan"));
            param.put("icon_website",Sequel.cariGambar("select icon_website from gambar_tambahan"));*/
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());   
            param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
            finger=Sequel.cariIsi("select sha1(sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",tbObat.getValueAt(tbObat.getSelectedRow(),44).toString());
            param.put("finger","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+tbObat.getValueAt(tbObat.getSelectedRow(),45).toString()+"\nID "+(finger.equals("")?tbObat.getValueAt(tbObat.getSelectedRow(),44).toString():finger)+"\n"+Valid.SetTgl3(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString()));
            finger_pj=Sequel.cariIsi("select sha1(sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",tbObat.getValueAt(tbObat.getSelectedRow(),36).toString());
            param.put("finger_pj","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\n Ditandatangani secara elektronik oleh "+tbObat.getValueAt(tbObat.getSelectedRow(),47).toString()+"\nID "+(finger.equals("")?tbObat.getValueAt(tbObat.getSelectedRow(),46).toString():finger)+"\n"+Valid.SetTgl3(tbObat.getValueAt(tbObat.getSelectedRow(),50).toString()));

            Valid.MyReportqry("rptCetakTransferPasienKeluarRS.jasper","report","::[ Formulir Transfer Pasien Keluar Rumah Sakit ]::",
                     "SELECT reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir,reg_periksa.sttsumur,reg_periksa.umurdaftar,reg_periksa.tgl_registrasi,reg_periksa.jam_reg,CONCAT( pasien.alamat,'', kelurahan.nm_kel,',', kecamatan.nm_kec,',', kabupaten.nm_kab) AS alamat,penjab.png_jawab, "+
                     "transfer_pasien_keluar_rs.tanggal_kontak_rs, transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs, transfer_pasien_keluar_rs.asal_ruang," +
                     "transfer_pasien_keluar_rs.ruang_selanjutnya, transfer_pasien_keluar_rs.diagnosa_utama," +
                     "transfer_pasien_keluar_rs.pasien_keluarga_menyetujui, transfer_pasien_keluar_rs.nama_menyetujui, transfer_pasien_keluar_rs.hubungan_menyetujui," +
                     "transfer_pasien_keluar_rs.nama_penghubung_rs, transfer_pasien_keluar_rs.telp_penghubung_rs, transfer_pasien_keluar_rs.nama_penerima_kontak_rs, transfer_pasien_keluar_rs.telp_penerima_kontak_rs," +                          
                     "transfer_pasien_keluar_rs.keadaan_umum_sebelum_transfer, transfer_pasien_keluar_rs.nadi_sebelum_transfer, transfer_pasien_keluar_rs.suhu_sebelum_transfer, transfer_pasien_keluar_rs.td_sebelum_transfer, transfer_pasien_keluar_rs.rr_sebelum_transfer, transfer_pasien_keluar_rs.keluhan_utama_sebelum_transfer, transfer_pasien_keluar_rs.obat_telah_diberikan," +
                     "transfer_pasien_keluar_rs.klinikal, transfer_pasien_keluar_rs.ket_klinikal, transfer_pasien_keluar_rs.non_klinikal, transfer_pasien_keluar_rs.ket_non_kinikal, transfer_pasien_keluar_rs.icu_penuh, transfer_pasien_keluar_rs.ranap_penuh, transfer_pasien_keluar_rs.permintaan_pasien, transfer_pasien_keluar_rs.rujuk_lainnya, transfer_pasien_keluar_rs.ket_rujuk_lainnya," +   
                     "transfer_pasien_keluar_rs.tanpa_almed, transfer_pasien_keluar_rs.terpasang_almed, transfer_pasien_keluar_rs.terpasang_infus, transfer_pasien_keluar_rs.terpasang_ett, transfer_pasien_keluar_rs.terpasang_02, transfer_pasien_keluar_rs.terpasang_cvc, transfer_pasien_keluar_rs.terpasang_ngt, transfer_pasien_keluar_rs.terpasang_cateter, transfer_pasien_keluar_rs.terpasang_bidal, transfer_pasien_keluar_rs.terpasang_pump, transfer_pasien_keluar_rs.terpasang_almed_lainnya, transfer_pasien_keluar_rs.ket_almed_lainnya," +      
                     "transfer_pasien_keluar_rs.tanpa_riwayat_penyakit, transfer_pasien_keluar_rs.ada_riwayat_penyakit, transfer_pasien_keluar_rs.riwayat_dm, transfer_pasien_keluar_rs.riwayat_jantung, transfer_pasien_keluar_rs.riwayat_penyakit_lainnya, transfer_pasien_keluar_rs.ket_riwayat_penyakit_lainnya," +                               
                     "transfer_pasien_keluar_rs.dokumen_rm, transfer_pasien_keluar_rs.dokumen_lab, transfer_pasien_keluar_rs.dokumen_rad, transfer_pasien_keluar_rs.dokumen_lainnya, transfer_pasien_keluar_rs.ket_dokumen_lainnya," +    
                     "transfer_pasien_keluar_rs.keluhan_utama_sesudah_transfer," +   
                     "transfer_pasien_keluar_rs.kd_dokter," +                                
                     "doktermenyerahkan.nm_dokter AS doktermenyerahkan " +  
                "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                "inner join transfer_pasien_keluar_rs on reg_periksa.no_rawat=transfer_pasien_keluar_rs.no_rawat "+
                "inner join dokter as doktermenyerahkan on transfer_pasien_keluar_rs.kd_dokter=doktermenyerahkan.kd_dokter "+
                "inner join kelurahan ON kelurahan.kd_kel = pasien.kd_kel " +
                "inner join kecamatan ON kecamatan.kd_kec = pasien.kd_kec " +
                "inner join kabupaten ON kabupaten.kd_kab = pasien.kd_kab " +
                "inner join penjab on penjab.kd_pj = reg_periksa.kd_pj where " +
//                "inner join petugas as petugasmenerima on transfer_pasien_keluar_rs.nip_menerima=petugasmenerima.nip where "+
                "transfer_pasien_keluar_rs.no_rawat='"+tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()+"' and transfer_pasien_keluar_rs.tanggal_kontak_rs='"+tbObat.getValueAt(tbObat.getSelectedRow(),5).toString()+"' and transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs='"+tbObat.getValueAt(tbObat.getSelectedRow(),6).toString()+"' ",param);
        }
        /* 
                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir,"+
                        "transfer_pasien_keluar_rs.tanggal_kontak_rs,transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs,transfer_pasien_keluar_rs.asal_ruang,"+
                        "transfer_pasien_keluar_rs.ruang_selanjutnya,transfer_pasien_keluar_rs.diagnosa_utama,"+
//                        "transfer_pasien_keluar_rs.indikasi_pindah_ruang,"+transfer_pasien_keluar_rs.diagnosa_sekunder,
//                        "transfer_pasien_keluar_rs.prosedur_yang_sudah_dilakukan,transfer_pasien_keluar_rs.obat_yang_telah_diberikan,"+
//                        "transfer_pasien_keluar_rs.keterangan_indikasi_pindah_ruang,transfer_pasien_keluar_rs.metode_pemindahan_pasien,transfer_pasien_keluar_rs.peralatan_yang_menyertai,transfer_pasien_keluar_rs.keterangan_peralatan_yang_menyertai,"+
//                        "transfer_pasien_keluar_rs.pemeriksaan_penunjang_yang_dilakukan,"+
                        "transfer_pasien_keluar_rs.pasien_keluarga_menyetujui,transfer_pasien_keluar_rs.nama_menyetujui,transfer_pasien_keluar_rs.hubungan_menyetujui,"+
                        "transfer_pasien_keluar_rs.keadaan_umum_sebelum_transfer,"+
                        "transfer_pasien_keluar_rs.nadi_sebelum_transfer,transfer_pasien_keluar_rs.suhu_sebelum_transfer,transfer_pasien_keluar_rs.td_sebelum_transfer,transfer_pasien_keluar_rs.rr_sebelum_transfer,transfer_pasien_keluar_rs.keluhan_utama_sebelum_transfer,transfer_pasien_keluar_rs.obat_telah_diberikan,"+
//                        "transfer_pasien_keluar_rs.keadaan_umum_sesudah_transfer,transfer_pasien_keluar_rs.td_sesudah_transfer,"+
//                        "transfer_pasien_keluar_rs.nadi_sesudah_transfer,transfer_pasien_keluar_rs.rr_sesudah_transfer,transfer_pasien_keluar_rs.suhu_sesudah_transfer,"+
                        "transfer_pasien_keluar_rs.klinikal,transfer_pasien_keluar_rs.ket_klinikal,transfer_pasien_keluar_rs.non_klinikal,transfer_pasien_keluar_rs.ket_non_kinikal,transfer_pasien_keluar_rs.icu_penuh,transfer_pasien_keluar_rs.ranap_penuh,transfer_pasien_keluar_rs.permintaan_pasien,transfer_pasien_keluar_rs.rujuk_lainnya,transfer_pasien_keluar_rs.ket_rujuk_lainnya,"+
                        "transfer_pasien_keluar_rs.tanpa_almed,transfer_pasien_keluar_rs.terpasang_almed,transfer_pasien_keluar_rs.terpasang_infus,transfer_pasien_keluar_rs.terpasang_ett,transfer_pasien_keluar_rs.terpasang_02,transfer_pasien_keluar_rs.terpasang_cvc,transfer_pasien_keluar_rs.terpasang_ngt,transfer_pasien_keluar_rs.terpasang_cateter,transfer_pasien_keluar_rs.terpasang_bidal,transfer_pasien_keluar_rs.terpasang_pump,transfer_pasien_keluar_rs.terpasang_almed_lainnya,transfer_pasien_keluar_rs.ket_almed_lainnya,"+         
                        "transfer_pasien_keluar_rs.dokumen_rm,transfer_pasien_keluar_rs.dokumen_rad,transfer_pasien_keluar_rs.dokumen_lab,transfer_pasien_keluar_rs.dokumen_lainnya,transfer_pasien_keluar_rs.ket_dokumen_lainnya,"+                                
                        "transfer_pasien_keluar_rs.tanpa_riwayat_penyakit,transfer_pasien_keluar_rs.ada_riwayat_penyakit,transfer_pasien_keluar_rs.riwayat_dm,transfer_pasien_keluar_rs.riwayat_jantung,transfer_pasien_keluar_rs.riwayat_penyakit_lainnya,transfer_pasien_keluar_rs.ket_riwayat_penyakit_lainnya,"+                                   
//                        "petugasmenerima.nama as petugasmenerima "+
                        "transfer_pasien_keluar_rs.keluhan_utama_sesudah_transfer,"+   
                        "transfer_pasien_keluar_rs.nip_menyerahkan,"+ //transfer_pasien_keluar_rs.nip_menerima,                                
                        "petugasmenyerahkan.nama as petugasmenyerahkan "+                                
                        "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                        "inner join transfer_pasien_keluar_rs on reg_periksa.no_rawat=transfer_pasien_keluar_rs.no_rawat "+
                        "inner join petugas as petugasmenyerahkan on transfer_pasien_keluar_rs.nip_menyerahkan=petugasmenyerahkan.nip where "+
//                        "inner join petugas as petugasmenerima on transfer_pasien_keluar_rs.nip_menerima=petugasmenerima.nip where "+
                        "transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs between ? and ? order by transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs");*/
    }                                                

    private void MnTranferKeluarRSActionPerformed(java.awt.event.ActionEvent evt) {                                                  
        if(tbObat.getSelectedRow()>-1){
            Map<String, Object> param = new HashMap<>(); 
                param.put("namars",akses.getnamars());
                param.put("alamatrs",akses.getalamatrs());
                param.put("kotars",akses.getkabupatenrs());
                param.put("propinsirs",akses.getpropinsirs());
                param.put("kontakrs",akses.getkontakrs());
                param.put("emailrs",akses.getemailrs());   
                param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
            finger=Sequel.cariIsi("select sha1(sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",tbObat.getValueAt(tbObat.getSelectedRow(),44).toString());
            param.put("finger","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+tbObat.getValueAt(tbObat.getSelectedRow(),45).toString()+"\nID "+(finger.equals("")?tbObat.getValueAt(tbObat.getSelectedRow(),44).toString():finger)+"\n"+Valid.SetTgl3(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString()));
            finger_pj=Sequel.cariIsi("select sha1(sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",tbObat.getValueAt(tbObat.getSelectedRow(),36).toString());
            param.put("finger_pj","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\n Ditandatangani secara elektronik oleh "+tbObat.getValueAt(tbObat.getSelectedRow(),47).toString()+"\nID "+(finger.equals("")?tbObat.getValueAt(tbObat.getSelectedRow(),46).toString():finger)+"\n"+Valid.SetTgl3(tbObat.getValueAt(tbObat.getSelectedRow(),50).toString()));                
                if(TCari.getText().equals("")){
/*        if(tbObat.getSelectedRow()>-1){
            Map<String, Object> param = new HashMap<>();
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());
            param.put("logo",Sequel.cariGambar("select logo_rsup from gambar_tambahan"));
            param.put("logo_kemenkes",Sequel.cariGambar("select logo_kemenkes_baru from gambar_tambahan"));
            param.put("logo_akreditasi",Sequel.cariGambar("select logo_akreditasi from gambar_tambahan"));
            param.put("logo_blu",Sequel.cariGambar("select logo_blu from gambar_tambahan"));
            param.put("icon_maps",Sequel.cariGambar("select icon_maps from gambar_tambahan"));
            param.put("icon_telpon",Sequel.cariGambar("select icon_telpon from gambar_tambahan"));
            param.put("icon_website",Sequel.cariGambar("select icon_website from gambar_tambahan"));
            finger=Sequel.cariIsi("select sha1(sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",tbObat.getValueAt(tbObat.getSelectedRow(),44).toString());
            param.put("finger","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+tbObat.getValueAt(tbObat.getSelectedRow(),45).toString()+"\nID "+(finger.equals("")?tbObat.getValueAt(tbObat.getSelectedRow(),44).toString():finger)+"\n"+Valid.SetTgl3(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString()));
            finger_pj=Sequel.cariIsi("select sha1(sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",tbObat.getValueAt(tbObat.getSelectedRow(),36).toString());
            param.put("finger_pj","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\n Ditandatangani secara elektronik oleh "+tbObat.getValueAt(tbObat.getSelectedRow(),47).toString()+"\nID "+(finger.equals("")?tbObat.getValueAt(tbObat.getSelectedRow(),46).toString():finger)+"\n"+Valid.SetTgl3(tbObat.getValueAt(tbObat.getSelectedRow(),50).toString()));
*/
            Valid.MyReportqry("rptCetakTransferPasienKeluarRS.jasper","report","::[ Formulir Transfer Pasien Keluar Rumah Sakit ]::",
                     "SELECT reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir,reg_periksa.sttsumur,reg_periksa.umurdaftar,reg_periksa.tgl_registrasi,reg_periksa.jam_reg,CONCAT( pasien.alamat,'', kelurahan.nm_kel,',', kecamatan.nm_kec,',', kabupaten.nm_kab) AS alamat,penjab.png_jawab, "+
                     "transfer_pasien_keluar_rs.tanggal_kontak_rs, transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs, transfer_pasien_keluar_rs.asal_ruang," +
                     "transfer_pasien_keluar_rs.ruang_selanjutnya, transfer_pasien_keluar_rs.diagnosa_utama," +
                     "transfer_pasien_keluar_rs.pasien_keluarga_menyetujui, transfer_pasien_keluar_rs.nama_menyetujui, transfer_pasien_keluar_rs.hubungan_menyetujui," +
                     "transfer_pasien_keluar_rs.nama_penghubung_rs, transfer_pasien_keluar_rs.telp_penghubung_rs, transfer_pasien_keluar_rs.nama_penerima_kontak_rs, transfer_pasien_keluar_rs.telp_penerima_kontak_rs," +                          
                     "transfer_pasien_keluar_rs.keadaan_umum_sebelum_transfer, transfer_pasien_keluar_rs.nadi_sebelum_transfer, transfer_pasien_keluar_rs.suhu_sebelum_transfer, transfer_pasien_keluar_rs.td_sebelum_transfer, transfer_pasien_keluar_rs.rr_sebelum_transfer, transfer_pasien_keluar_rs.keluhan_utama_sebelum_transfer, transfer_pasien_keluar_rs.obat_telah_diberikan," +
                     "transfer_pasien_keluar_rs.klinikal, transfer_pasien_keluar_rs.ket_klinikal, transfer_pasien_keluar_rs.non_klinikal, transfer_pasien_keluar_rs.ket_non_kinikal, transfer_pasien_keluar_rs.icu_penuh, transfer_pasien_keluar_rs.ranap_penuh, transfer_pasien_keluar_rs.permintaan_pasien, transfer_pasien_keluar_rs.rujuk_lainnya, transfer_pasien_keluar_rs.ket_rujuk_lainnya," +   
                     "transfer_pasien_keluar_rs.tanpa_almed, transfer_pasien_keluar_rs.terpasang_almed, transfer_pasien_keluar_rs.terpasang_infus, transfer_pasien_keluar_rs.terpasang_ett, transfer_pasien_keluar_rs.terpasang_02, transfer_pasien_keluar_rs.terpasang_cvc, transfer_pasien_keluar_rs.terpasang_ngt, transfer_pasien_keluar_rs.terpasang_cateter, transfer_pasien_keluar_rs.terpasang_bidal, transfer_pasien_keluar_rs.terpasang_pump, transfer_pasien_keluar_rs.terpasang_almed_lainnya, transfer_pasien_keluar_rs.ket_almed_lainnya," +                                    
                     "transfer_pasien_keluar_rs.tanpa_riwayat_penyakit, transfer_pasien_keluar_rs.ada_riwayat_penyakit, transfer_pasien_keluar_rs.riwayat_dm, transfer_pasien_keluar_rs.riwayat_jantung, transfer_pasien_keluar_rs.riwayat_penyakit_lainnya, transfer_pasien_keluar_rs.ket_riwayat_penyakit_lainnya," +      
                     "transfer_pasien_keluar_rs.dokumen_rm, transfer_pasien_keluar_rs.dokumen_lab, transfer_pasien_keluar_rs.dokumen_rad, transfer_pasien_keluar_rs.dokumen_lainnya, transfer_pasien_keluar_rs.ket_dokumen_lainnya," +                             
                     "transfer_pasien_keluar_rs.keluhan_utama_sesudah_transfer," +   
                     "transfer_pasien_keluar_rs.nip_menyerahkan," +                                
                     "doktermenyerahkan.nm_dokter AS doktermenyerahkan " +  
                "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                "inner join transfer_pasien_keluar_rs on reg_periksa.no_rawat=transfer_pasien_keluar_rs.no_rawat "+
                "inner join dokter as doktermenyerahkan on transfer_pasien_keluar_rs.kd_dokter=doktermenyerahkan.kd_dokter "+
                "inner join kelurahan ON kelurahan.kd_kel = pasien.kd_kel " +
                "inner join kecamatan ON kecamatan.kd_kec = pasien.kd_kec " +
                "inner join kabupaten ON kabupaten.kd_kab = pasien.kd_kab " +
                "inner join penjab on penjab.kd_pj = reg_periksa.kd_pj where " +
//                "inner join petugas as petugasmenerima on transfer_pasien_keluar_rs.nip_menerima=petugasmenerima.nip where "+
                "transfer_pasien_keluar_rs.no_rawat='"+tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()+"' and transfer_pasien_keluar_rs.tanggal_kontak_rs='"+tbObat.getValueAt(tbObat.getSelectedRow(),5).toString()+"' and transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs='"+tbObat.getValueAt(tbObat.getSelectedRow(),6).toString()+"' ",param);
        }
        }
        /* 
                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir,"+
                        "transfer_pasien_keluar_rs.tanggal_kontak_rs,transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs,transfer_pasien_keluar_rs.asal_ruang,"+
                        "transfer_pasien_keluar_rs.ruang_selanjutnya,transfer_pasien_keluar_rs.diagnosa_utama,"+
//                        "transfer_pasien_keluar_rs.indikasi_pindah_ruang,"+transfer_pasien_keluar_rs.diagnosa_sekunder,
//                        "transfer_pasien_keluar_rs.prosedur_yang_sudah_dilakukan,transfer_pasien_keluar_rs.obat_yang_telah_diberikan,"+
//                        "transfer_pasien_keluar_rs.keterangan_indikasi_pindah_ruang,transfer_pasien_keluar_rs.metode_pemindahan_pasien,transfer_pasien_keluar_rs.peralatan_yang_menyertai,transfer_pasien_keluar_rs.keterangan_peralatan_yang_menyertai,"+
//                        "transfer_pasien_keluar_rs.pemeriksaan_penunjang_yang_dilakukan,"+
                        "transfer_pasien_keluar_rs.pasien_keluarga_menyetujui,transfer_pasien_keluar_rs.nama_menyetujui,transfer_pasien_keluar_rs.hubungan_menyetujui,"+
                        "transfer_pasien_keluar_rs.keadaan_umum_sebelum_transfer,"+
                        "transfer_pasien_keluar_rs.nadi_sebelum_transfer,transfer_pasien_keluar_rs.suhu_sebelum_transfer,transfer_pasien_keluar_rs.td_sebelum_transfer,transfer_pasien_keluar_rs.rr_sebelum_transfer,transfer_pasien_keluar_rs.keluhan_utama_sebelum_transfer,transfer_pasien_keluar_rs.obat_telah_diberikan,"+
//                        "transfer_pasien_keluar_rs.keadaan_umum_sesudah_transfer,transfer_pasien_keluar_rs.td_sesudah_transfer,"+
//                        "transfer_pasien_keluar_rs.nadi_sesudah_transfer,transfer_pasien_keluar_rs.rr_sesudah_transfer,transfer_pasien_keluar_rs.suhu_sesudah_transfer,"+
                        "transfer_pasien_keluar_rs.klinikal,transfer_pasien_keluar_rs.ket_klinikal,transfer_pasien_keluar_rs.non_klinikal,transfer_pasien_keluar_rs.ket_non_kinikal,transfer_pasien_keluar_rs.icu_penuh,transfer_pasien_keluar_rs.ranap_penuh,transfer_pasien_keluar_rs.permintaan_pasien,transfer_pasien_keluar_rs.rujuk_lainnya,transfer_pasien_keluar_rs.ket_rujuk_lainnya,"+
                        "transfer_pasien_keluar_rs.tanpa_almed,transfer_pasien_keluar_rs.terpasang_almed,transfer_pasien_keluar_rs.terpasang_infus,transfer_pasien_keluar_rs.terpasang_ett,transfer_pasien_keluar_rs.terpasang_02,transfer_pasien_keluar_rs.terpasang_cvc,transfer_pasien_keluar_rs.terpasang_ngt,transfer_pasien_keluar_rs.terpasang_cateter,transfer_pasien_keluar_rs.terpasang_bidal,transfer_pasien_keluar_rs.terpasang_pump,transfer_pasien_keluar_rs.terpasang_almed_lainnya,transfer_pasien_keluar_rs.ket_almed_lainnya,"+         
                        "transfer_pasien_keluar_rs.dokumen_rm,transfer_pasien_keluar_rs.dokumen_rad,transfer_pasien_keluar_rs.dokumen_lab,transfer_pasien_keluar_rs.dokumen_lainnya,transfer_pasien_keluar_rs.ket_dokumen_lainnya,"+                                
                        "transfer_pasien_keluar_rs.tanpa_riwayat_penyakit,transfer_pasien_keluar_rs.ada_riwayat_penyakit,transfer_pasien_keluar_rs.riwayat_dm,transfer_pasien_keluar_rs.riwayat_jantung,transfer_pasien_keluar_rs.riwayat_penyakit_lainnya,transfer_pasien_keluar_rs.ket_riwayat_penyakit_lainnya,"+                                   
//                        "petugasmenerima.nama as petugasmenerima "+
                        "transfer_pasien_keluar_rs.keluhan_utama_sesudah_transfer,"+   
                        "transfer_pasien_keluar_rs.nip_menyerahkan,"+ //transfer_pasien_keluar_rs.nip_menerima,                                
                        "petugasmenyerahkan.nama as petugasmenyerahkan "+                                
                        "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                        "inner join transfer_pasien_keluar_rs on reg_periksa.no_rawat=transfer_pasien_keluar_rs.no_rawat "+
                        "inner join petugas as petugasmenyerahkan on transfer_pasien_keluar_rs.nip_menyerahkan=petugasmenyerahkan.nip where "+
//                        "inner join petugas as petugasmenerima on transfer_pasien_keluar_rs.nip_menerima=petugasmenerima.nip where "+
                        "transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs between ? and ? order by transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs");*/
        /*        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if(tabMode.getRowCount()==0){
            JOptionPane.showMessageDialog(null,"Maaf, data sudah habis. Tidak ada data yang bisa anda print...!!!!");
            BtnBatal.requestFocus();
        }else if(tabMode.getRowCount()!=0){
            try{
                if(TCari.getText().trim().equals("")){
                    ps=koneksi.prepareStatement(
                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir,"+
                        "transfer_pasien_keluar_rs.tanggal_kontak_rs,transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs,transfer_pasien_keluar_rs.asal_ruang,"+
                        "transfer_pasien_keluar_rs.ruang_selanjutnya,transfer_pasien_keluar_rs.diagnosa_utama,"+
//                        "transfer_pasien_keluar_rs.indikasi_pindah_ruang,"+transfer_pasien_keluar_rs.diagnosa_sekunder,
//                        "transfer_pasien_keluar_rs.prosedur_yang_sudah_dilakukan,transfer_pasien_keluar_rs.obat_yang_telah_diberikan,"+
//                        "transfer_pasien_keluar_rs.keterangan_indikasi_pindah_ruang,transfer_pasien_keluar_rs.metode_pemindahan_pasien,transfer_pasien_keluar_rs.peralatan_yang_menyertai,transfer_pasien_keluar_rs.keterangan_peralatan_yang_menyertai,"+
//                        "transfer_pasien_keluar_rs.pemeriksaan_penunjang_yang_dilakukan,"+
                        "transfer_pasien_keluar_rs.pasien_keluarga_menyetujui,transfer_pasien_keluar_rs.nama_menyetujui,transfer_pasien_keluar_rs.hubungan_menyetujui,"+
                        "transfer_pasien_keluar_rs.nama_penghubung_rs,transfer_pasien_keluar_rs.telp_penghubung_rs,transfer_pasien_keluar_rs.nama_penerima_kontak_rs,telp_penerima_kontak_rs=?,"+          
                        "transfer_pasien_keluar_rs.keadaan_umum_sebelum_transfer,"+
                        "transfer_pasien_keluar_rs.nadi_sebelum_transfer,transfer_pasien_keluar_rs.suhu_sebelum_transfer,transfer_pasien_keluar_rs.td_sebelum_transfer,transfer_pasien_keluar_rs.rr_sebelum_transfer,transfer_pasien_keluar_rs.keluhan_utama_sebelum_transfer,transfer_pasien_keluar_rs.obat_telah_diberikan,"+
//                        "transfer_pasien_keluar_rs.keadaan_umum_sesudah_transfer,transfer_pasien_keluar_rs.td_sesudah_transfer,"+
//                        "transfer_pasien_keluar_rs.nadi_sesudah_transfer,transfer_pasien_keluar_rs.rr_sesudah_transfer,transfer_pasien_keluar_rs.suhu_sesudah_transfer,"+
                        "transfer_pasien_keluar_rs.klinikal,transfer_pasien_keluar_rs.ket_klinikal,transfer_pasien_keluar_rs.non_klinikal,transfer_pasien_keluar_rs.ket_non_kinikal,transfer_pasien_keluar_rs.icu_penuh,transfer_pasien_keluar_rs.ranap_penuh,transfer_pasien_keluar_rs.permintaan_pasien,transfer_pasien_keluar_rs.rujuk_lainnya,transfer_pasien_keluar_rs.ket_rujuk_lainnya,"+
                        "transfer_pasien_keluar_rs.tanpa_almed,transfer_pasien_keluar_rs.terpasang_almed,transfer_pasien_keluar_rs.terpasang_infus,transfer_pasien_keluar_rs.terpasang_ett,transfer_pasien_keluar_rs.terpasang_02,transfer_pasien_keluar_rs.terpasang_cvc,transfer_pasien_keluar_rs.terpasang_ngt,transfer_pasien_keluar_rs.terpasang_cateter,transfer_pasien_keluar_rs.terpasang_bidal,transfer_pasien_keluar_rs.terpasang_pump,transfer_pasien_keluar_rs.terpasang_almed_lainnya,transfer_pasien_keluar_rs.ket_almed_lainnya,"+                                      
                        "transfer_pasien_keluar_rs.tanpa_riwayat_penyakit,transfer_pasien_keluar_rs.ada_riwayat_penyakit,transfer_pasien_keluar_rs.riwayat_dm,transfer_pasien_keluar_rs.riwayat_jantung,transfer_pasien_keluar_rs.riwayat_penyakit_lainnya,transfer_pasien_keluar_rs.ket_riwayat_penyakit_lainnya,"+       
                        "transfer_pasien_keluar_rs.dokumen_rm,transfer_pasien_keluar_rs.dokumen_lab,transfer_pasien_keluar_rs.dokumen_rad,transfer_pasien_keluar_rs.dokumen_lainnya,transfer_pasien_keluar_rs.ket_dokumen_lainnya,"+                                   
//                        "petugasmenerima.nama as petugasmenerima "+
                        "transfer_pasien_keluar_rs.keluhan_utama_sesudah_transfer,"+   
                        "transfer_pasien_keluar_rs.kd_dokter,"+ //transfer_pasien_keluar_rs.nip_menerima,                                
                        "doktermenyerahkan.nm_dokter as doktermenyerahkan "+                                
                        "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                        "inner join transfer_pasien_keluar_rs on reg_periksa.no_rawat=transfer_pasien_keluar_rs.no_rawat "+
                        "inner join dokter as doktermenyerahkan on transfer_pasien_keluar_rs.kd_dokter=doktermenyerahkan.kd_dokter where "+
//                        "inner join petugas as petugasmenerima on transfer_pasien_keluar_rs.nip_menerima=petugasmenerima.nip where "+
                        "transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs between ? and ? order by transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs");
                }else{
                    ps=koneksi.prepareStatement(
                        "select reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir,"+
                        "transfer_pasien_keluar_rs.tanggal_kontak_rs,transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs,transfer_pasien_keluar_rs.asal_ruang,"+
                        "transfer_pasien_keluar_rs.ruang_selanjutnya,transfer_pasien_keluar_rs.diagnosa_utama,"+
//                        "transfer_pasien_keluar_rs.indikasi_pindah_ruang,"+ //transfer_pasien_keluar_rs.keterangan_indikasi_pindah_ruang,transfer_pasien_keluar_rs.diagnosa_sekunder,
//                        "transfer_pasien_keluar_rs.prosedur_yang_sudah_dilakukan,transfer_pasien_keluar_rs.obat_yang_telah_diberikan,"+
//                        "transfer_pasien_keluar_rs.metode_pemindahan_pasien,transfer_pasien_keluar_rs.peralatan_yang_menyertai,transfer_pasien_keluar_rs.keterangan_peralatan_yang_menyertai,"+
//                        "transfer_pasien_keluar_rs.pemeriksaan_penunjang_yang_dilakukan,"+
                        "transfer_pasien_keluar_rs.pasien_keluarga_menyetujui,transfer_pasien_keluar_rs.nama_menyetujui,transfer_pasien_keluar_rs.hubungan_menyetujui,"+
                        "transfer_pasien_keluar_rs.nama_penghubung_rs,transfer_pasien_keluar_rs.telp_penghubung_rs,transfer_pasien_keluar_rs.nama_penerima_kontak_rs,telp_penerima_kontak_rs=?,"+                          
                        "transfer_pasien_keluar_rs.keadaan_umum_sebelum_transfer,transfer_pasien_keluar_rs.nadi_sebelum_transfer,transfer_pasien_keluar_rs.suhu_sebelum_transfer,transfer_pasien_keluar_rs.td_sebelum_transfer,transfer_pasien_keluar_rs.rr_sebelum_transfer,transfer_pasien_keluar_rs.keluhan_utama_sebelum_transfer,transfer_pasien_keluar_rs.obat_telah_diberikan,"+
//                        "transfer_pasien_keluar_rs.keadaan_umum_sesudah_transfer,transfer_pasien_keluar_rs.td_sesudah_transfer,"+
//                        "transfer_pasien_keluar_rs.nadi_sesudah_transfer,transfer_pasien_keluar_rs.rr_sesudah_transfer,transfer_pasien_keluar_rs.suhu_sesudah_transfer,"+
                        "transfer_pasien_keluar_rs.klinikal,transfer_pasien_keluar_rs.ket_klinikal,transfer_pasien_keluar_rs.non_klinikal,transfer_pasien_keluar_rs.ket_non_kinikal,transfer_pasien_keluar_rs.icu_penuh,transfer_pasien_keluar_rs.ranap_penuh,transfer_pasien_keluar_rs.permintaan_pasien,transfer_pasien_keluar_rs.rujuk_lainnya,transfer_pasien_keluar_rs.ket_rujuk_lainnya,"+   
                        "transfer_pasien_keluar_rs.tanpa_almed,transfer_pasien_keluar_rs.terpasang_almed,transfer_pasien_keluar_rs.terpasang_infus,transfer_pasien_keluar_rs.terpasang_ett,transfer_pasien_keluar_rs.terpasang_02,transfer_pasien_keluar_rs.terpasang_cvc,transfer_pasien_keluar_rs.terpasang_ngt,transfer_pasien_keluar_rs.terpasang_cateter,transfer_pasien_keluar_rs.terpasang_bidal,transfer_pasien_keluar_rs.terpasang_pump,transfer_pasien_keluar_rs.terpasang_almed_lainnya,transfer_pasien_keluar_rs.ket_almed_lainnya,"+                                    
                        "transfer_pasien_keluar_rs.tanpa_riwayat_penyakit,transfer_pasien_keluar_rs.ada_riwayat_penyakit,transfer_pasien_keluar_rs.riwayat_dm,transfer_pasien_keluar_rs.riwayat_jantung,transfer_pasien_keluar_rs.riwayat_penyakit_lainnya,transfer_pasien_keluar_rs.ket_riwayat_penyakit_lainnya,"+      
                        "transfer_pasien_keluar_rs.dokumen_rm,transfer_pasien_keluar_rs.dokumen_lab,transfer_pasien_keluar_rs.dokumen_rad,transfer_pasien_keluar_rs.dokumen_lainnya,transfer_pasien_keluar_rs.ket_dokumen_lainnya,"+                                
                        "transfer_pasien_keluar_rs.keluhan_utama_sesudah_transfer,"+   
                        "transfer_pasien_keluar_rs.kd_dokter,"+                                
                        "doktermenyerahkan.nm_dokter as doktermenyerahkan "+   //,transfer_pasien_keluar_rs.nip_menerima                                      
//                        "petugasmenerima.nama as petugasmenerima "+                         
                        "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                        "inner join transfer_pasien_keluar_rs on reg_periksa.no_rawat=transfer_pasien_keluar_rs.no_rawat "+
                        "inner join dokter as doktermenyerahkan on transfer_pasien_keluar_rs.kd_dokter=petugasmenyerahkan.kd_dokter where "+
      //                  "inner join petugas as petugasmenerima on transfer_pasien_keluar_rs.nip_menerima=petugasmenerima.nip where "+
                        "transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs between ? and ? and (reg_periksa.no_rawat like ? or pasien.no_rkm_medis like ? or pasien.nm_pasien like ? or "+
                        "transfer_pasien_keluar_rs.kd_dokter like ? or doktermenyerahkan.nm_dokter like ?) order by transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs");
                }

                try {
                    if(TCari.getText().trim().equals("")){
                        ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
                        ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
                    }else{
                        ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
                        ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
                        ps.setString(3,"%"+TCari.getText()+"%");
                        ps.setString(4,"%"+TCari.getText()+"%");
                        ps.setString(5,"%"+TCari.getText()+"%");
                        ps.setString(6,"%"+TCari.getText()+"%");
                        ps.setString(7,"%"+TCari.getText()+"%");
                    }
                    rs=ps.executeQuery();
                    htmlContent = new StringBuilder();
                    htmlContent.append(                             
                        "<tr class='isi'>"+
                           "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>No.Rawat</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>No.RM</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Nama Pasien</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Tgl.Lahir</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>J.K.</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Tanggal Masuk</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Tanggal Pindah</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Indikasi Pindah</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Keterangan Indikasi Pindah</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Asal Ruang Rawat / Poliklinik</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Ruang Rawat Selanjutnya</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Metode Pemindahan</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Diagnosa Utama</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Diagnosa Sekunder</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Prosedur Yang Sudah Dilakukan</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Obat Yang Telah Diberikan</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Pemeriksaan Penunjang Yang Sudah Dilakukan</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Peralatan Yang Menyertai</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Keterangan Peralatan Menyertai</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Menyetujui Pemindahan</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Nama Keluarga/Penanggung Jawab</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Hubungan</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Keadaan Umum SbT</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>TD SbT</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Nadi SbT</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>RR SbT</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Suhu Sbt</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Keluhan Utama Sebelum Transfer</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Keadaan Umum StT</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>TD StT</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Nadi StT</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>RR StT</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Suhu Stt</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Keluhan Utama Setelah Transfer</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>NIP Menyerahkan</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Petugas Yang Menyerahkan</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>NIP Menerima</b></td>"+
                            "<td valign='middle' bgcolor='#FFFAF8' align='center'><b>Petugas Yang Menerima</b></td>"+
                        "</tr>"
                    );
                    while(rs.next()){
                        htmlContent.append(
                            "<tr class='isi'>"+
                               "<td valign='top'>"+rs.getString("no_rawat")+"</td>"+
                               "<td valign='top'>"+rs.getString("no_rkm_medis")+"</td>"+
                               "<td valign='top'>"+rs.getString("nm_pasien")+"</td>"+
                               "<td valign='top'>"+rs.getString("tgl_lahir")+"</td>"+
                               "<td valign='top'>"+rs.getString("jk")+"</td>"+
                               "<td valign='top'>"+rs.getString("tanggal_kontak_rs")+"</td>"+
                               "<td valign='top'>"+rs.getString("tanggal_rujuk_keluar_rs")+"</td>"+
    //                           "<td valign='top'>"+rs.getString("indikasi_pindah_ruang")+"</td>"+
    //                           "<td valign='top'>"+rs.getString("keterangan_indikasi_pindah_ruang")+"</td>"+
                               "<td valign='top'>"+rs.getString("asal_ruang")+"</td>"+
                               "<td valign='top'>"+rs.getString("ruang_selanjutnya")+"</td>"+
    //                           "<td valign='top'>"+rs.getString("metode_pemindahan_pasien")+"</td>"+
                               "<td valign='top'>"+rs.getString("diagnosa_utama")+"</td>"+
    //                           "<td valign='top'>"+rs.getString("diagnosa_sekunder")+"</td>"+
    //                           "<td valign='top'>"+rs.getString("prosedur_yang_sudah_dilakukan")+"</td>"+
    //                           "<td valign='top'>"+rs.getString("obat_yang_telah_diberikan")+"</td>"+
    //                           "<td valign='top'>"+rs.getString("pemeriksaan_penunjang_yang_dilakukan")+"</td>"+
    //                           "<td valign='top'>"+rs.getString("peralatan_yang_menyertai")+"</td>"+
    //                           "<td valign='top'>"+rs.getString("keterangan_peralatan_yang_menyertai")+"</td>"+
                               "<td valign='top'>"+rs.getString("pasien_keluarga_menyetujui")+"</td>"+
                               "<td valign='top'>"+rs.getString("nama_menyetujui")+"</td>"+
                               "<td valign='top'>"+rs.getString("hubungan_menyetujui")+"</td>"+
                               "<td valign='top'>"+rs.getString("keadaan_umum_sebelum_transfer")+"</td>"+
                               "<td valign='top'>"+rs.getString("td_sebelum_transfer")+"</td>"+
                               "<td valign='top'>"+rs.getString("nadi_sebelum_transfer")+"</td>"+
                               "<td valign='top'>"+rs.getString("rr_sebelum_transfer")+"</td>"+
                               "<td valign='top'>"+rs.getString("suhu_sebelum_transfer")+"</td>"+
                               "<td valign='top'>"+rs.getString("keluhan_utama_sebelum_transfer")+"</td>"+
    //                           "<td valign='top'>"+rs.getString("keadaan_umum_sesudah_transfer")+"</td>"+
    //                           "<td valign='top'>"+rs.getString("td_sesudah_transfer")+"</td>"+
    //                           "<td valign='top'>"+rs.getString("nadi_sesudah_transfer")+"</td>"+
    //                           "<td valign='top'>"+rs.getString("rr_sesudah_transfer")+"</td>"+
   //                            "<td valign='top'>"+rs.getString("suhu_sesudah_transfer")+"</td>"+
                               "<td valign='top'>"+rs.getString("keluhan_utama_sesudah_transfer")+"</td>"+
                               "<td valign='top'>"+rs.getString("kd_dokter")+"</td>"+
                               "<td valign='top'>"+rs.getString("petugasmenyerahkan")+"</td>"+
    //                           "<td valign='top'>"+rs.getString("nip_menerima")+"</td>"+
    //                           "<td valign='top'>"+rs.getString("petugasmenerima")+"</td>"+
                            "</tr>");
                    }
                    LoadHTML.setText(
                        "<html>"+
                          "<table width='4000' border='0' align='center' cellpadding='1px' cellspacing='0' class='tbl_form'>"+
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

                    File f = new File("TransferPasienAntarRuang.html");            
                    BufferedWriter bw = new BufferedWriter(new FileWriter(f));            
                    bw.write(LoadHTML.getText().replaceAll("<head>","<head>"+
                                "<link href=\"file2.css\" rel=\"stylesheet\" type=\"text/css\" />"+
                                "<table width='4000px' border='0' align='center' cellpadding='3px' cellspacing='0' class='tbl_form'>"+
                                    "<tr class='isi2'>"+
                                        "<td valign='top' align='center'>"+
                                            "<font size='4' face='Tahoma'>"+akses.getnamars()+"</font><br>"+
                                            akses.getalamatrs()+", "+akses.getkabupatenrs()+", "+akses.getpropinsirs()+"<br>"+
                                            akses.getkontakrs()+", E-mail : "+akses.getemailrs()+"<br><br>"+
                                            "<font size='2' face='Tahoma'>DATA TRANSFER PASIEN KELUAR RS<br><br></font>"+        
                                        "</td>"+
                                   "</tr>"+
                                "</table>")
                    );
                    bw.close();                         
                    Desktop.getDesktop().browse(f.toURI());
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
        }
        this.setCursor(Cursor.getDefaultCursor());*/
}//GEN-LAST:event_BtnPrintActionPerformed

    private void BtnPrintKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnPrintKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnPrintActionPerformed(null);
        }else{
            Valid.pindah(evt, BtnEdit, BtnKeluar);
        }
}//GEN-LAST:event_BtnPrintKeyPressed

    private void TCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_ENTER){
            BtnCariActionPerformed(null);
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            BtnCari.requestFocus();
        }else if(evt.getKeyCode()==KeyEvent.VK_PAGE_UP){
            BtnKeluar.requestFocus();
        }
}//GEN-LAST:event_TCariKeyPressed

    private void BtnCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnCariActionPerformed
        tampil();
}//GEN-LAST:event_BtnCariActionPerformed

    private void BtnCariKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnCariKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            BtnCariActionPerformed(null);
        }else{
            Valid.pindah(evt, TCari, BtnAll);
        }
}//GEN-LAST:event_BtnCariKeyPressed

    private void BtnAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAllActionPerformed
        TCari.setText("");
        tampil();
}//GEN-LAST:event_BtnAllActionPerformed

    private void BtnAllKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnAllKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_SPACE){
            TCari.setText("");
            tampil();
        }else{
            Valid.pindah(evt, BtnCari, TPasien);
        }
}//GEN-LAST:event_BtnAllKeyPressed

    private void tbObatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbObatMouseClicked
/*        if(tabMode.getRowCount()!=0){
            try {
                isPhoto();
                panggilPhoto();
            } catch (java.lang.NullPointerException e) {
            }
            if((evt.getClickCount()==2)&&(tbObat.getSelectedColumn()==0)){
                TabRawat.setSelectedIndex(0);
            }
        }*/
}//GEN-LAST:event_tbObatMouseClicked

    private void tbObatKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbObatKeyPressed
        if(tabMode.getRowCount()!=0){
            if((evt.getKeyCode()==KeyEvent.VK_ENTER)||(evt.getKeyCode()==KeyEvent.VK_UP)||(evt.getKeyCode()==KeyEvent.VK_DOWN)){
                try {
                    getData();
                } catch (java.lang.NullPointerException e) {
                }
            }else if(evt.getKeyCode()==KeyEvent.VK_SPACE){
                try {
                    getData();
                    TabRawat.setSelectedIndex(0);
                } catch (java.lang.NullPointerException e) {
                }
            }
        }
}//GEN-LAST:event_tbObatKeyPressed

    private void TabRawatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TabRawatMouseClicked
/*        if(TabRawat.getSelectedIndex()==1){
            tampil();
        }*/
        if(tbObat.getRowCount()!=0){
            try {
                getData();
            } catch (java.lang.NullPointerException e) {
            }
        }
    }//GEN-LAST:event_TabRawatMouseClicked

    private void MnTransferPasienKeluarRSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MnTransferPasienKeluarRSActionPerformed
       if(tbObat.getSelectedRow()>-1){
            Map<String, Object> param = new HashMap<>();
/*            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());
            param.put("logo",Sequel.cariGambar("select logo_rsup from gambar_tambahan"));
            param.put("logo_kemenkes",Sequel.cariGambar("select logo_kemenkes_baru from gambar_tambahan"));
            param.put("logo_akreditasi",Sequel.cariGambar("select logo_akreditasi from gambar_tambahan"));
            param.put("logo_blu",Sequel.cariGambar("select logo_blu from gambar_tambahan"));
            param.put("icon_maps",Sequel.cariGambar("select icon_maps from gambar_tambahan"));
            param.put("icon_telpon",Sequel.cariGambar("select icon_telpon from gambar_tambahan"));
            param.put("icon_website",Sequel.cariGambar("select icon_website from gambar_tambahan"));*/
            param.put("namars",akses.getnamars());
            param.put("alamatrs",akses.getalamatrs());
            param.put("kotars",akses.getkabupatenrs());
            param.put("propinsirs",akses.getpropinsirs());
            param.put("kontakrs",akses.getkontakrs());
            param.put("emailrs",akses.getemailrs());   
            param.put("logo",Sequel.cariGambar("select setting.logo from setting")); 
            finger=Sequel.cariIsi("select sha1(sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",tbObat.getValueAt(tbObat.getSelectedRow(),44).toString());
            param.put("finger","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\nDitandatangani secara elektronik oleh "+tbObat.getValueAt(tbObat.getSelectedRow(),45).toString()+"\nID "+(finger.equals("")?tbObat.getValueAt(tbObat.getSelectedRow(),44).toString():finger)+"\n"+Valid.SetTgl3(tbObat.getValueAt(tbObat.getSelectedRow(),6).toString()));
            finger_pj=Sequel.cariIsi("select sha1(sidikjari) from sidikjari inner join pegawai on pegawai.id=sidikjari.id where pegawai.nik=?",tbObat.getValueAt(tbObat.getSelectedRow(),36).toString());
            param.put("finger_pj","Dikeluarkan di "+akses.getnamars()+", Kabupaten/Kota "+akses.getkabupatenrs()+"\n Ditandatangani secara elektronik oleh "+tbObat.getValueAt(tbObat.getSelectedRow(),47).toString()+"\nID "+(finger.equals("")?tbObat.getValueAt(tbObat.getSelectedRow(),46).toString():finger)+"\n"+Valid.SetTgl3(tbObat.getValueAt(tbObat.getSelectedRow(),50).toString()));

            Valid.MyReportqry("rptCetakTransferPasienKeluarRS.jasper","report","::[ Formulir Transfer Pasien Keluar Rumah Sakit ]::",
                     "SELECT reg_periksa.no_rawat,pasien.no_rkm_medis,pasien.nm_pasien,if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir,reg_periksa.sttsumur,reg_periksa.umurdaftar,reg_periksa.tgl_registrasi,reg_periksa.jam_reg,CONCAT( pasien.alamat,'', kelurahan.nm_kel,',', kecamatan.nm_kec,',', kabupaten.nm_kab) AS alamat,penjab.png_jawab, "+
                     "transfer_pasien_keluar_rs.tanggal_kontak_rs, transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs, transfer_pasien_keluar_rs.asal_ruang," +
                     "transfer_pasien_keluar_rs.ruang_selanjutnya, transfer_pasien_keluar_rs.diagnosa_utama," +
                     "transfer_pasien_keluar_rs.pasien_keluarga_menyetujui, transfer_pasien_keluar_rs.nama_menyetujui, transfer_pasien_keluar_rs.hubungan_menyetujui," +
                     "transfer_pasien_keluar_rs.nama_penghubung_rs, transfer_pasien_keluar_rs.telp_penghubung_rs, transfer_pasien_keluar_rs.nama_penerima_kontak_rs, transfer_pasien_keluar_rs.telp_penerima_kontak_rs," +                          
                     "transfer_pasien_keluar_rs.keadaan_umum_sebelum_transfer, transfer_pasien_keluar_rs.nadi_sebelum_transfer, transfer_pasien_keluar_rs.suhu_sebelum_transfer, transfer_pasien_keluar_rs.td_sebelum_transfer, transfer_pasien_keluar_rs.rr_sebelum_transfer, transfer_pasien_keluar_rs.keluhan_utama_sebelum_transfer, transfer_pasien_keluar_rs.obat_telah_diberikan," +
                     "transfer_pasien_keluar_rs.klinikal, transfer_pasien_keluar_rs.ket_klinikal, transfer_pasien_keluar_rs.non_klinikal, transfer_pasien_keluar_rs.ket_non_kinikal, transfer_pasien_keluar_rs.icu_penuh, transfer_pasien_keluar_rs.ranap_penuh, transfer_pasien_keluar_rs.permintaan_pasien, transfer_pasien_keluar_rs.rujuk_lainnya, transfer_pasien_keluar_rs.ket_rujuk_lainnya," +   
                     "transfer_pasien_keluar_rs.tanpa_almed, transfer_pasien_keluar_rs.terpasang_almed, transfer_pasien_keluar_rs.terpasang_infus, transfer_pasien_keluar_rs.terpasang_ett, transfer_pasien_keluar_rs.terpasang_02, transfer_pasien_keluar_rs.terpasang_cvc, transfer_pasien_keluar_rs.terpasang_ngt, transfer_pasien_keluar_rs.terpasang_cateter, transfer_pasien_keluar_rs.terpasang_bidal, transfer_pasien_keluar_rs.terpasang_pump, transfer_pasien_keluar_rs.terpasang_almed_lainnya, transfer_pasien_keluar_rs.ket_almed_lainnya," +      
                     "transfer_pasien_keluar_rs.tanpa_riwayat_penyakit, transfer_pasien_keluar_rs.ada_riwayat_penyakit, transfer_pasien_keluar_rs.riwayat_dm, transfer_pasien_keluar_rs.riwayat_jantung, transfer_pasien_keluar_rs.riwayat_penyakit_lainnya, transfer_pasien_keluar_rs.ket_riwayat_penyakit_lainnya," +                               
                     "transfer_pasien_keluar_rs.dokumen_rm, transfer_pasien_keluar_rs.dokumen_lab, transfer_pasien_keluar_rs.dokumen_rad, transfer_pasien_keluar_rs.dokumen_lainnya, transfer_pasien_keluar_rs.ket_dokumen_lainnya," +    
                     "transfer_pasien_keluar_rs.keluhan_utama_sesudah_transfer," +   
                     "transfer_pasien_keluar_rs.kd_dokter," +                                
                     "doktermenyerahkan.nm_dokter AS doktermenyerahkan " +  
                "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                "inner join transfer_pasien_keluar_rs on reg_periksa.no_rawat=transfer_pasien_keluar_rs.no_rawat "+
                "inner join dokter as doktermenyerahkan on transfer_pasien_keluar_rs.kd_dokter=doktermenyerahkan.kd_dokter "+
                "inner join kelurahan ON kelurahan.kd_kel = pasien.kd_kel " +
                "inner join kecamatan ON kecamatan.kd_kec = pasien.kd_kec " +
                "inner join kabupaten ON kabupaten.kd_kab = pasien.kd_kab " +
                "inner join penjab on penjab.kd_pj = reg_periksa.kd_pj where " +
//                "inner join petugas as petugasmenerima on transfer_pasien_keluar_rs.nip_menerima=petugasmenerima.nip where "+
                "transfer_pasien_keluar_rs.no_rawat='"+tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()+"' and transfer_pasien_keluar_rs.tanggal_kontak_rs='"+tbObat.getValueAt(tbObat.getSelectedRow(),5).toString()+"' and transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs='"+tbObat.getValueAt(tbObat.getSelectedRow(),6).toString()+"' ",param);
        }
    }//GEN-LAST:event_MnTransferPasienKeluarRSActionPerformed

    private void ObatTelahDiberikanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ObatTelahDiberikanKeyPressed
        Valid.pindah(evt,KeluhanUtamaSebelumTransfer,KetKlinikal);        // TODO add your handling code here:
    }//GEN-LAST:event_ObatTelahDiberikanKeyPressed

    private void TanggalKontakRSKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalKontakRSKeyPressed
        Valid.pindah(evt,NoTelpPenerimaTujuanRS,KeadaanUmumSebelumTransfer);
    }//GEN-LAST:event_TanggalKontakRSKeyPressed

    private void NoTelpPenerimaTujuanRSKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoTelpPenerimaTujuanRSKeyPressed
        Valid.pindah(evt,NamaPenerimaTujuanRS,TanggalKontakRS);        // TODO add your handling code here:
    }//GEN-LAST:event_NoTelpPenerimaTujuanRSKeyPressed

    private void NamaPenerimaTujuanRSKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NamaPenerimaTujuanRSKeyPressed
        Valid.pindah(evt,NoTelpPenghubungRS,NoTelpPenerimaTujuanRS);        // TODO add your handling code here:
    }//GEN-LAST:event_NamaPenerimaTujuanRSKeyPressed

    private void NoTelpPenghubungRSKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NoTelpPenghubungRSKeyPressed
        Valid.pindah(evt,NamaPenghubungRS,NamaPenerimaTujuanRS);           // TODO add your handling code here:
    }//GEN-LAST:event_NoTelpPenghubungRSKeyPressed

    private void NamaPenghubungRSKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NamaPenghubungRSKeyPressed
        Valid.pindah(evt,HubunganPJMenyetujui,NoTelpPenghubungRS);
    }//GEN-LAST:event_NamaPenghubungRSKeyPressed

    private void KetPAmedLainnyaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KetPAmedLainnyaKeyPressed
        Valid.pindah(evt,AlasanLainnya,KetPLainnya); //Bt          // TODO add your handling code here:
    }//GEN-LAST:event_KetPAmedLainnyaKeyPressed

    private void ChkPAlmedLainnyaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChkPAlmedLainnyaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChkPAlmedLainnyaActionPerformed

    private void KetPLainnyaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KetPLainnyaKeyPressed
        Valid.pindah(evt,KetPAmedLainnya,KetDokLainnya); //Bt             // TODO add your handling code here:
    }//GEN-LAST:event_KetPLainnyaKeyPressed

    private void AlasanLainnyaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AlasanLainnyaKeyPressed
        Valid.pindah(evt,KetNonKlinikal,KetPAmedLainnya); //Bt             // TODO add your handling code here:
    }//GEN-LAST:event_AlasanLainnyaKeyPressed

    private void KetNonKlinikalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KetNonKlinikalKeyPressed
        Valid.pindah(evt,KetKlinikal,AlasanLainnya); //Bt        // TODO add your handling code here:
    }//GEN-LAST:event_KetNonKlinikalKeyPressed

    private void KetKlinikalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KetKlinikalKeyPressed
        Valid.pindah(evt,ObatTelahDiberikan,KetNonKlinikal); //BtnMenerima
    }//GEN-LAST:event_KetKlinikalKeyPressed

    private void KetDokLainnyaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KetDokLainnyaKeyPressed
        Valid.pindah(evt,KetPLainnya,KeluhanUtamaSetelahTransfer); //Bt          // TODO add your handling code here:
    }//GEN-LAST:event_KetDokLainnyaKeyPressed

    private void KetDokLainnyaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KetDokLainnyaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_KetDokLainnyaActionPerformed

    private void BtnDokterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_BtnDokterKeyPressed
        Valid.pindah(evt,KeluhanUtamaSetelahTransfer,BtnDokter); //BtnMenerima
    }//GEN-LAST:event_BtnDokterKeyPressed

    private void BtnDokterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDokterActionPerformed
        pilihan=1;
        /*    petugas.isCek();
        petugas.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        petugas.setLocationRelativeTo(internalFrame1);
        petugas.setVisible(true);
        dokter.emptTeks();*/
        dokter.isCek();
        dokter.setSize(internalFrame1.getWidth()-20,internalFrame1.getHeight()-20);
        dokter.setLocationRelativeTo(internalFrame1);
        dokter.setVisible(true);
    }//GEN-LAST:event_BtnDokterActionPerformed

    private void KeluhanUtamaSetelahTransferKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KeluhanUtamaSetelahTransferKeyPressed
        Valid.pindah(evt,KetDokLainnya,BtnDokter);        //        Valid.pindah2(evt,SuhuSetelahTransfer,BtnDokter);
    }//GEN-LAST:event_KeluhanUtamaSetelahTransferKeyPressed

    private void SuhuSebelumTransferKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_SuhuSebelumTransferKeyPressed
        Valid.pindah(evt,NadiSebelumTransfer,TDSebelumTransfer);
    }//GEN-LAST:event_SuhuSebelumTransferKeyPressed

    private void RRSebelumTransferKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RRSebelumTransferKeyPressed
        Valid.pindah(evt,TDSebelumTransfer,KeluhanUtamaSebelumTransfer);
    }//GEN-LAST:event_RRSebelumTransferKeyPressed

    private void NadiSebelumTransferKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NadiSebelumTransferKeyPressed
        Valid.pindah(evt,KeadaanUmumSebelumTransfer,SuhuSebelumTransfer);
    }//GEN-LAST:event_NadiSebelumTransferKeyPressed

    private void TDSebelumTransferKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TDSebelumTransferKeyPressed
        Valid.pindah(evt,SuhuSebelumTransfer,RRSebelumTransfer);
    }//GEN-LAST:event_TDSebelumTransferKeyPressed

    private void KeadaanUmumSebelumTransferKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KeadaanUmumSebelumTransferKeyPressed
        Valid.pindah(evt,TanggalKontakRS,NadiSebelumTransfer);
    }//GEN-LAST:event_KeadaanUmumSebelumTransferKeyPressed

    private void KeluhanUtamaSebelumTransferKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_KeluhanUtamaSebelumTransferKeyPressed
        Valid.pindah(evt,RRSebelumTransfer,ObatTelahDiberikan);
    }//GEN-LAST:event_KeluhanUtamaSebelumTransferKeyPressed

    private void HubunganPJMenyetujuiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_HubunganPJMenyetujuiKeyPressed
        Valid.pindah(evt,NamaPJMenyetujui,NamaPenghubungRS);
    }//GEN-LAST:event_HubunganPJMenyetujuiKeyPressed

    private void NamaPJMenyetujuiKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_NamaPJMenyetujuiKeyPressed
        Valid.pindah(evt,MenyetujuiPemindahan,HubunganPJMenyetujui);
    }//GEN-LAST:event_NamaPJMenyetujuiKeyPressed

    private void AsalRuangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_AsalRuangKeyPressed
        Valid.pindah(evt,TNoRw,RuangSelanjutnya);        //        Valid.pindah(evt,KeteranganIndikasiPindahRuang,RuangSelanjutnya);
    }//GEN-LAST:event_AsalRuangKeyPressed

    private void DiagnosaUtamaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DiagnosaUtamaKeyPressed
        Valid.pindah(evt,TanggalRujukKeluarRS,MenyetujuiPemindahan);
    }//GEN-LAST:event_DiagnosaUtamaKeyPressed

    private void RuangSelanjutnyaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_RuangSelanjutnyaKeyPressed
        Valid.pindah(evt,AsalRuang,TanggalRujukKeluarRS);
    }//GEN-LAST:event_RuangSelanjutnyaKeyPressed

    private void TanggalRujukKeluarRSKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TanggalRujukKeluarRSKeyPressed
        Valid.pindah(evt,TanggalRujukKeluarRS,MenyetujuiPemindahan);//        Valid.pindah2(evt,TanggalMasuk,IndikasiPindah);
    }//GEN-LAST:event_TanggalRujukKeluarRSKeyPressed

    private void TNoRwKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TNoRwKeyPressed
        if(evt.getKeyCode()==KeyEvent.VK_PAGE_DOWN){
            isRawat();
        }else{
            //Valid.pindah(evt,TCari,BtnDokter);
        }
    }//GEN-LAST:event_TNoRwKeyPressed

    private void MenyetujuiPemindahanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_MenyetujuiPemindahanKeyPressed
        Valid.pindah(evt,DiagnosaUtama,NamaPJMenyetujui);//
    }//GEN-LAST:event_MenyetujuiPemindahanKeyPressed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            RMTransferPasienKeluarRS dialog = new RMTransferPasienKeluarRS(new javax.swing.JFrame(), true);
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
    private widget.TextBox AlasanLainnya;
    private widget.TextBox AsalRuang;
    private widget.Button BtnAll;
    private widget.Button BtnBatal;
    private widget.Button BtnCari;
    private widget.Button BtnDokter;
    private widget.Button BtnEdit;
    private widget.Button BtnHapus;
    private widget.Button BtnKeluar;
    private widget.Button BtnPrint;
    private widget.Button BtnSimpan;
    private widget.CekBox ChkAdaRiwayat;
    private widget.CekBox ChkAlasanLainnya;
    private widget.CekBox ChkICUPenuh;
    private widget.CekBox ChkKlinikal;
    private widget.CekBox ChkLAB;
    private widget.CekBox ChkLainnya;
    private widget.CekBox ChkNonKlinikal;
    private widget.CekBox ChkPAlmedLainnya;
    private widget.CekBox ChkPBidal;
    private widget.CekBox ChkPCVC;
    private widget.CekBox ChkPCateter;
    private widget.CekBox ChkPDM;
    private widget.CekBox ChkPETT;
    private widget.CekBox ChkPInfus;
    private widget.CekBox ChkPJantung;
    private widget.CekBox ChkPLainnya;
    private widget.CekBox ChkPNGT;
    private widget.CekBox ChkPO2;
    private widget.CekBox ChkPPump;
    private widget.CekBox ChkPermintaanPasien;
    private widget.CekBox ChkRAD;
    private widget.CekBox ChkRM;
    private widget.CekBox ChkRanapPenuh;
    private widget.CekBox ChkTAlmed;
    private widget.CekBox ChkTdkAdaRiwayat;
    private widget.CekBox ChkYaAlmed;
    private widget.Tanggal DTPCari1;
    private widget.Tanggal DTPCari2;
    private widget.TextBox DiagnosaUtama;
    private widget.PanelBiasa FormInput;
    private widget.ComboBox HubunganPJMenyetujui;
    private widget.TextBox Jk;
    private widget.TextBox KdDokterMenyerahkan;
    private widget.ComboBox KeadaanUmumSebelumTransfer;
    private widget.TextArea KeluhanUtamaSebelumTransfer;
    private widget.TextArea KeluhanUtamaSetelahTransfer;
    private widget.TextBox KetDokLainnya;
    private widget.TextBox KetKlinikal;
    private widget.TextBox KetNonKlinikal;
    private widget.TextBox KetPAmedLainnya;
    private widget.TextBox KetPLainnya;
    private widget.Label LCount;
    private widget.editorpane LoadHTML;
    private widget.ComboBox MenyetujuiPemindahan;
    private javax.swing.JMenuItem MnTransferPasienKeluarRS;
    private widget.TextBox NadiSebelumTransfer;
    private widget.TextBox NamaPJMenyetujui;
    private widget.TextBox NamaPenerimaTujuanRS;
    private widget.TextBox NamaPenghubungRS;
    private widget.TextBox NmDokterMenyerahkan;
    private widget.TextBox NoTelpPenerimaTujuanRS;
    private widget.TextBox NoTelpPenghubungRS;
    private widget.TextArea ObatTelahDiberikan;
    private widget.TextBox RRSebelumTransfer;
    private widget.TextBox RuangSelanjutnya;
    private widget.ScrollPane Scroll;
    private widget.TextBox SuhuSebelumTransfer;
    private widget.TextBox TCari;
    private widget.TextBox TDSebelumTransfer;
    private widget.TextBox TNoRM;
    private widget.TextBox TNoRw;
    private widget.TextBox TPasien;
    private javax.swing.JTabbedPane TabRawat;
    private widget.Tanggal TanggalKontakRS;
    private widget.Tanggal TanggalRujukKeluarRS;
    private widget.TextBox TglLahir;
    private widget.InternalFrame internalFrame1;
    private widget.InternalFrame internalFrame2;
    private widget.InternalFrame internalFrame3;
    private widget.Label jLabel10;
    private widget.Label jLabel11;
    private widget.Label jLabel15;
    private widget.Label jLabel16;
    private widget.Label jLabel17;
    private widget.Label jLabel18;
    private widget.Label jLabel19;
    private widget.Label jLabel21;
    private widget.Label jLabel22;
    private widget.Label jLabel23;
    private widget.Label jLabel24;
    private widget.Label jLabel25;
    private widget.Label jLabel26;
    private widget.Label jLabel27;
    private widget.Label jLabel28;
    private widget.Label jLabel34;
    private widget.Label jLabel38;
    private widget.Label jLabel39;
    private widget.Label jLabel40;
    private widget.Label jLabel41;
    private widget.Label jLabel42;
    private widget.Label jLabel43;
    private widget.Label jLabel44;
    private widget.Label jLabel45;
    private widget.Label jLabel46;
    private widget.Label jLabel47;
    private widget.Label jLabel48;
    private widget.Label jLabel49;
    private widget.Label jLabel50;
    private widget.Label jLabel51;
    private widget.Label jLabel53;
    private widget.Label jLabel54;
    private widget.Label jLabel55;
    private widget.Label jLabel56;
    private widget.Label jLabel58;
    private widget.Label jLabel59;
    private widget.Label jLabel6;
    private widget.Label jLabel60;
    private widget.Label jLabel61;
    private widget.Label jLabel62;
    private widget.Label jLabel63;
    private widget.Label jLabel64;
    private widget.Label jLabel65;
    private widget.Label jLabel66;
    private widget.Label jLabel67;
    private widget.Label jLabel68;
    private widget.Label jLabel69;
    private widget.Label jLabel7;
    private widget.Label jLabel70;
    private widget.Label jLabel71;
    private widget.Label jLabel72;
    private widget.Label jLabel73;
    private widget.Label jLabel74;
    private widget.Label jLabel75;
    private widget.Label jLabel76;
    private widget.Label jLabel77;
    private widget.Label jLabel78;
    private widget.Label jLabel79;
    private widget.Label jLabel8;
    private widget.Label jLabel80;
    private widget.Label jLabel81;
    private widget.Label jLabel82;
    private widget.Label jLabel83;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private widget.Label label12;
    private widget.Label label14;
    private widget.Label label16;
    private widget.panelisi panelGlass8;
    private widget.panelisi panelGlass9;
    private widget.ScrollPane scrollInput;
    private widget.ScrollPane scrollPane5;
    private widget.ScrollPane scrollPane6;
    private widget.ScrollPane scrollPane7;
    private widget.Table tbObat;
    // End of variables declaration//GEN-END:variables

    public void tampil() {
        Valid.tabelKosong(tabMode);
        try{
            if(TCari.getText().trim().equals("")){
                ps=koneksi.prepareStatement(
                     "SELECT reg_periksa.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, IF(pasien.jk='L', 'Laki-Laki', 'Perempuan') AS jk, pasien.tgl_lahir," +
                     "transfer_pasien_keluar_rs.tanggal_kontak_rs, transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs, transfer_pasien_keluar_rs.asal_ruang," +
                     "transfer_pasien_keluar_rs.ruang_selanjutnya, transfer_pasien_keluar_rs.diagnosa_utama," +
                     "transfer_pasien_keluar_rs.pasien_keluarga_menyetujui, transfer_pasien_keluar_rs.nama_menyetujui, transfer_pasien_keluar_rs.hubungan_menyetujui," +
                     "transfer_pasien_keluar_rs.nama_penghubung_rs, transfer_pasien_keluar_rs.telp_penghubung_rs, transfer_pasien_keluar_rs.nama_penerima_kontak_rs, transfer_pasien_keluar_rs.telp_penerima_kontak_rs," +
                     "transfer_pasien_keluar_rs.keadaan_umum_sebelum_transfer," +
                     "transfer_pasien_keluar_rs.nadi_sebelum_transfer, transfer_pasien_keluar_rs.suhu_sebelum_transfer, transfer_pasien_keluar_rs.td_sebelum_transfer, transfer_pasien_keluar_rs.rr_sebelum_transfer, transfer_pasien_keluar_rs.keluhan_utama_sebelum_transfer, transfer_pasien_keluar_rs.obat_telah_diberikan," +
                     "transfer_pasien_keluar_rs.klinikal, transfer_pasien_keluar_rs.ket_klinikal, transfer_pasien_keluar_rs.non_klinikal, transfer_pasien_keluar_rs.ket_non_kinikal, transfer_pasien_keluar_rs.icu_penuh, transfer_pasien_keluar_rs.ranap_penuh, transfer_pasien_keluar_rs.permintaan_pasien, transfer_pasien_keluar_rs.rujuk_lainnya, transfer_pasien_keluar_rs.ket_rujuk_lainnya," +
                     "transfer_pasien_keluar_rs.tanpa_almed, transfer_pasien_keluar_rs.terpasang_almed, transfer_pasien_keluar_rs.terpasang_infus, transfer_pasien_keluar_rs.terpasang_ett, transfer_pasien_keluar_rs.terpasang_02, transfer_pasien_keluar_rs.terpasang_cvc, transfer_pasien_keluar_rs.terpasang_ngt, transfer_pasien_keluar_rs.terpasang_cateter, transfer_pasien_keluar_rs.terpasang_bidal, transfer_pasien_keluar_rs.terpasang_pump, transfer_pasien_keluar_rs.terpasang_almed_lainnya, transfer_pasien_keluar_rs.ket_almed_lainnya," +
                     "transfer_pasien_keluar_rs.tanpa_riwayat_penyakit, transfer_pasien_keluar_rs.ada_riwayat_penyakit, transfer_pasien_keluar_rs.riwayat_dm, transfer_pasien_keluar_rs.riwayat_jantung, transfer_pasien_keluar_rs.riwayat_penyakit_lainnya, transfer_pasien_keluar_rs.ket_riwayat_penyakit_lainnya," +
                     "transfer_pasien_keluar_rs.dokumen_rm, transfer_pasien_keluar_rs.dokumen_lab, transfer_pasien_keluar_rs.dokumen_rad, transfer_pasien_keluar_rs.dokumen_lainnya, transfer_pasien_keluar_rs.ket_dokumen_lainnya," +                             
                     "transfer_pasien_keluar_rs.keluhan_utama_sesudah_transfer," +
                     "transfer_pasien_keluar_rs.kd_dokter," +
                     "doktermenyerahkan.nm_dokter AS doktermenyerahkan " +
                     "FROM reg_periksa " +
                     "INNER JOIN pasien ON reg_periksa.no_rkm_medis = pasien.no_rkm_medis " +
                     "INNER JOIN transfer_pasien_keluar_rs ON reg_periksa.no_rawat = transfer_pasien_keluar_rs.no_rawat " +
                     "INNER JOIN dokter AS doktermenyerahkan ON transfer_pasien_keluar_rs.kd_dokter = doktermenyerahkan.kd_dokter " +
                     "WHERE transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs BETWEEN ? AND ? " +
                     "ORDER BY transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs");
            }else{
                ps=koneksi.prepareStatement(
                     "SELECT reg_periksa.no_rawat, pasien.no_rkm_medis, pasien.nm_pasien, IF(pasien.jk='L', 'Laki-Laki', 'Perempuan') AS jk, pasien.tgl_lahir," +
                     "transfer_pasien_keluar_rs.tanggal_kontak_rs, transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs, transfer_pasien_keluar_rs.asal_ruang," +
                     "transfer_pasien_keluar_rs.ruang_selanjutnya, transfer_pasien_keluar_rs.diagnosa_utama," +
                     "transfer_pasien_keluar_rs.pasien_keluarga_menyetujui, transfer_pasien_keluar_rs.nama_menyetujui, transfer_pasien_keluar_rs.hubungan_menyetujui," +
                     "transfer_pasien_keluar_rs.nama_penghubung_rs, transfer_pasien_keluar_rs.telp_penghubung_rs, transfer_pasien_keluar_rs.nama_penerima_kontak_rs, transfer_pasien_keluar_rs.telp_penerima_kontak_rs," +                          
                     "transfer_pasien_keluar_rs.keadaan_umum_sebelum_transfer, transfer_pasien_keluar_rs.nadi_sebelum_transfer, transfer_pasien_keluar_rs.suhu_sebelum_transfer, transfer_pasien_keluar_rs.td_sebelum_transfer, transfer_pasien_keluar_rs.rr_sebelum_transfer, transfer_pasien_keluar_rs.keluhan_utama_sebelum_transfer, transfer_pasien_keluar_rs.obat_telah_diberikan," +
                     "transfer_pasien_keluar_rs.klinikal, transfer_pasien_keluar_rs.ket_klinikal, transfer_pasien_keluar_rs.non_klinikal, transfer_pasien_keluar_rs.ket_non_kinikal, transfer_pasien_keluar_rs.icu_penuh, transfer_pasien_keluar_rs.ranap_penuh, transfer_pasien_keluar_rs.permintaan_pasien, transfer_pasien_keluar_rs.rujuk_lainnya, transfer_pasien_keluar_rs.ket_rujuk_lainnya," +   
                     "transfer_pasien_keluar_rs.tanpa_almed, transfer_pasien_keluar_rs.terpasang_almed, transfer_pasien_keluar_rs.terpasang_infus, transfer_pasien_keluar_rs.terpasang_ett, transfer_pasien_keluar_rs.terpasang_02, transfer_pasien_keluar_rs.terpasang_cvc, transfer_pasien_keluar_rs.terpasang_ngt, transfer_pasien_keluar_rs.terpasang_cateter, transfer_pasien_keluar_rs.terpasang_bidal, transfer_pasien_keluar_rs.terpasang_pump, transfer_pasien_keluar_rs.terpasang_almed_lainnya, transfer_pasien_keluar_rs.ket_almed_lainnya," +                                    
                     "transfer_pasien_keluar_rs.tanpa_riwayat_penyakit, transfer_pasien_keluar_rs.ada_riwayat_penyakit, transfer_pasien_keluar_rs.riwayat_dm, transfer_pasien_keluar_rs.riwayat_jantung, transfer_pasien_keluar_rs.riwayat_penyakit_lainnya, transfer_pasien_keluar_rs.ket_riwayat_penyakit_lainnya," +      
                     "transfer_pasien_keluar_rs.dokumen_rm, transfer_pasien_keluar_rs.dokumen_lab, transfer_pasien_keluar_rs.dokumen_rad, transfer_pasien_keluar_rs.dokumen_lainnya, transfer_pasien_keluar_rs.ket_dokumen_lainnya," +                             
                     "transfer_pasien_keluar_rs.keluhan_utama_sesudah_transfer," +   
                     "transfer_pasien_keluar_rs.kd_dokter," +                                
                     "doktermenyerahkan.nm_dokter AS doktermenyerahkan " +                             
                     "FROM reg_periksa " +
                     "INNER JOIN pasien ON reg_periksa.no_rkm_medis = pasien.no_rkm_medis " +
                     "INNER JOIN transfer_pasien_keluar_rs ON reg_periksa.no_rawat = transfer_pasien_keluar_rs.no_rawat " +
                     "INNER JOIN dokter AS doktermenyerahkan ON transfer_pasien_keluar_rs.kd_dokter = doktermenyerahkan.kd_dokter " +
                     "WHERE transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs BETWEEN ? AND ? " +
                     "AND (reg_periksa.no_rawat LIKE ? OR pasien.no_rkm_medis LIKE ? OR pasien.nm_pasien LIKE ? OR " +
                     "transfer_pasien_keluar_rs.kd_dokter LIKE ? OR doktermenyerahkan.nm_dokter LIKE ?) " +
                     "ORDER BY transfer_pasien_keluar_rs.tanggal_rujuk_keluar_rs");
            }
                
            try {
                if(TCari.getText().trim().equals("")){
                    ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
                    ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");                 
                }else{
                    ps.setString(1,Valid.SetTgl(DTPCari1.getSelectedItem()+"")+" 00:00:00");
                    ps.setString(2,Valid.SetTgl(DTPCari2.getSelectedItem()+"")+" 23:59:59");
                    ps.setString(3,"%"+TCari.getText()+"%");
                    ps.setString(4,"%"+TCari.getText()+"%");
                    ps.setString(5,"%"+TCari.getText()+"%");
                    ps.setString(6,"%"+TCari.getText()+"%");
                    ps.setString(7,"%"+TCari.getText()+"%");                  
                }    
                rs=ps.executeQuery();
                while(rs.next()){
                    tabMode.addRow(new String[]{
                        rs.getString("no_rawat"),rs.getString("no_rkm_medis"),rs.getString("nm_pasien"),rs.getString("tgl_lahir"),rs.getString("jk"),rs.getString("tanggal_kontak_rs"),
                        rs.getString("tanggal_rujuk_keluar_rs"),rs.getString("asal_ruang"),rs.getString("ruang_selanjutnya"), //rs.getString("metode_pemindahan_pasien"),rs.getString("keterangan_indikasi_pindah_ruang"),rs.getString("indikasi_pindah_ruang"),rs.getString("peralatan_yang_menyertai"),rs.getString("keterangan_peralatan_yang_menyertai"),
                        rs.getString("diagnosa_utama"),//rs.getString("pemeriksaan_penunjang_yang_dilakukan"),//rs.getString("diagnosa_sekunder"),rs.getString("prosedur_yang_sudah_dilakukan"),rs.getString("obat_yang_telah_diberikan"),
                        rs.getString("pasien_keluarga_menyetujui"),rs.getString("nama_menyetujui"),rs.getString("hubungan_menyetujui"),
                        rs.getString("nama_penghubung_rs"),rs.getString("telp_penghubung_rs"),rs.getString("nama_penerima_kontak_rs"),rs.getString("telp_penerima_kontak_rs"),                     
                        rs.getString("keadaan_umum_sebelum_transfer"),rs.getString("nadi_sebelum_transfer"),rs.getString("suhu_sebelum_transfer"),rs.getString("td_sebelum_transfer"),rs.getString("rr_sebelum_transfer"),rs.getString("keluhan_utama_sebelum_transfer"),rs.getString("obat_telah_diberikan"),
//                      rs.getString("keadaan_umum_sesudah_transfer"),rs.getString("td_sesudah_transfer"),rs.getString("nadi_sesudah_transfer"),rs.getString("rr_sesudah_transfer"),rs.getString("suhu_sesudah_transfer"),
                        rs.getString("klinikal"),rs.getString("ket_klinikal"),rs.getString("non_klinikal"),rs.getString("ket_non_kinikal"),rs.getString("icu_penuh"),rs.getString("ranap_penuh"),rs.getString("permintaan_pasien"),rs.getString("rujuk_lainnya"),rs.getString("ket_rujuk_lainnya"),
                        rs.getString("tanpa_almed"),rs.getString("terpasang_almed"),rs.getString("terpasang_infus"),rs.getString("terpasang_ett"),rs.getString("terpasang_02"),rs.getString("terpasang_cvc"),rs.getString("terpasang_ngt"),rs.getString("terpasang_cateter"),rs.getString("terpasang_bidal"),rs.getString("terpasang_pump"),rs.getString("terpasang_almed_lainnya"),rs.getString("ket_almed_lainnya"),
                        rs.getString("tanpa_riwayat_penyakit"),rs.getString("ada_riwayat_penyakit"),rs.getString("riwayat_dm"),rs.getString("riwayat_jantung"),rs.getString("riwayat_penyakit_lainnya"),rs.getString("ket_riwayat_penyakit_lainnya"),                        
                        rs.getString("dokumen_rm"),rs.getString("dokumen_lab"),rs.getString("dokumen_rad"),rs.getString("dokumen_lainnya"),rs.getString("ket_dokumen_lainnya"),                        
                        rs.getString("keluhan_utama_sesudah_transfer"),                        
                        rs.getString("kd_dokter"),rs.getString("doktermenyerahkan") //,rs.getString("nip_menerima"),rs.getString("petugasmenerima")                        
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

    public void emptTeks() {
        TanggalKontakRS.setDate(new Date());
        TanggalRujukKeluarRS.setDate(new Date());
//        IndikasiPindah.setSelectedIndex(0);
//        KeteranganIndikasiPindahRuang.setText("");
        AsalRuang.setText("");
        RuangSelanjutnya.setText("");
//        MetodePemindahan.setSelectedIndex(0);
        DiagnosaUtama.setText("");
//        DiagnosaSekunder.setText("");
//        ProsedurDilakukan.setText("");
 //       ObatYangDiberikan.setText("");
 //       PemeriksaanPenunjang.setText("");
//        PeralatanMenyertai.setSelectedIndex(0);
//        KeteranganPeralatan.setText("");
        MenyetujuiPemindahan.setSelectedIndex(0);
        NamaPJMenyetujui.setText("");
        HubunganPJMenyetujui.setSelectedIndex(0);
        NamaPenghubungRS.setText("");
        NoTelpPenghubungRS.setText(""); 
        NamaPenerimaTujuanRS.setText(""); 
        NoTelpPenerimaTujuanRS.setText("");     
        KeadaanUmumSebelumTransfer.setSelectedIndex(0);
        NadiSebelumTransfer.setText("");        
        SuhuSebelumTransfer.setText("");        
        TDSebelumTransfer.setText("");
        RRSebelumTransfer.setText("");
        KeluhanUtamaSebelumTransfer.setText("");
        ObatTelahDiberikan.setText("");        
/*        KeadaanUmumSetelahTransfer.setSelectedIndex(0);
        TDSetelahTransfer.setText("");
        NadiSetelahTransfer.setText("");
        RRSetelahTransfer.setText("");
        SuhuSetelahTransfer.setText("");*/
        ChkKlinikal.setSelected(false);
        KetKlinikal.setText("");         
        ChkNonKlinikal.setSelected(false);
        KetNonKlinikal.setText("");         
        ChkICUPenuh.setSelected(false);
        ChkRanapPenuh.setSelected(false);   
        ChkPermintaanPasien.setSelected(false);
        ChkAlasanLainnya.setSelected(false);           
        AlasanLainnya.setText("");  
        ChkTAlmed.setSelected(false);        
        ChkYaAlmed.setSelected(false);
        ChkPInfus.setSelected(false);   
        ChkPETT.setSelected(false);
        ChkPO2.setSelected(false);
        ChkPCVC.setSelected(false);        
        ChkPNGT.setSelected(false);
        ChkPCateter.setSelected(false);   
        ChkPBidal.setSelected(false);
        ChkPPump.setSelected(false);     
        ChkPAlmedLainnya.setSelected(false); 
//        KetPAmedLainnya.setEnabled(false); 
        KetPAmedLainnya.setText("");    
        ChkTdkAdaRiwayat.setSelected(false);        
        ChkAdaRiwayat.setSelected(false);
        ChkPDM.setSelected(false);   
        ChkPJantung.setSelected(false);
        ChkPLainnya.setSelected(false);           
        KetPLainnya.setText("");        
        ChkRM.setSelected(false);
        ChkLAB.setSelected(false);
        ChkRAD.setSelected(false);        
        ChkLainnya.setSelected(false);   
        KetDokLainnya.setText("");
        KeluhanUtamaSetelahTransfer.setText("");        
        TabRawat.setSelectedIndex(0);
//        IndikasiPindah.requestFocus();
    } 

    private void getData() {
        if(tbObat.getSelectedRow()!= -1){
            TNoRw.setText(tbObat.getValueAt(tbObat.getSelectedRow(),0).toString()); 
            TNoRM.setText(tbObat.getValueAt(tbObat.getSelectedRow(),1).toString());
            TPasien.setText(tbObat.getValueAt(tbObat.getSelectedRow(),2).toString());
            TglLahir.setText(tbObat.getValueAt(tbObat.getSelectedRow(),3).toString());
            Jk.setText(tbObat.getValueAt(tbObat.getSelectedRow(),4).toString()); 
//            IndikasiPindah.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString()); 
//            KeteranganIndikasiPindahRuang.setText(tbObat.getValueAt(tbObat.getSelectedRow(),8).toString());
            AsalRuang.setText(tbObat.getValueAt(tbObat.getSelectedRow(),7).toString());
            RuangSelanjutnya.setText(tbObat.getValueAt(tbObat.getSelectedRow(),8).toString()); 
//            MetodePemindahan.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),11).toString()); 
            DiagnosaUtama.setText(tbObat.getValueAt(tbObat.getSelectedRow(),9).toString()); 
//            DiagnosaSekunder.setText(tbObat.getValueAt(tbObat.getSelectedRow(),13).toString()); 
//            ProsedurDilakukan.setText(tbObat.getValueAt(tbObat.getSelectedRow(),14).toString()); 
//            ObatYangDiberikan.setText(tbObat.getValueAt(tbObat.getSelectedRow(),15).toString()); 
//            PemeriksaanPenunjang.setText(tbObat.getValueAt(tbObat.getSelectedRow(),16).toString()); 
//            PeralatanMenyertai.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),17).toString()); 
//            KeteranganPeralatan.setText(tbObat.getValueAt(tbObat.getSelectedRow(),18).toString()); 
            MenyetujuiPemindahan.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),10).toString());
            NamaPJMenyetujui.setText(tbObat.getValueAt(tbObat.getSelectedRow(),11).toString());  
            HubunganPJMenyetujui.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),12).toString());
            NamaPenghubungRS.setText(tbObat.getValueAt(tbObat.getSelectedRow(),13).toString());
            NoTelpPenghubungRS.setText(tbObat.getValueAt(tbObat.getSelectedRow(),14).toString());
            NamaPenerimaTujuanRS.setText(tbObat.getValueAt(tbObat.getSelectedRow(),15).toString());
            NoTelpPenerimaTujuanRS.setText(tbObat.getValueAt(tbObat.getSelectedRow(),16).toString());            
            KeadaanUmumSebelumTransfer.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),17).toString());
            NadiSebelumTransfer.setText(tbObat.getValueAt(tbObat.getSelectedRow(),18).toString()); 
            SuhuSebelumTransfer.setText(tbObat.getValueAt(tbObat.getSelectedRow(),19).toString());   
            TDSebelumTransfer.setText(tbObat.getValueAt(tbObat.getSelectedRow(),20).toString());             
            RRSebelumTransfer.setText(tbObat.getValueAt(tbObat.getSelectedRow(),21).toString()); 
            KeluhanUtamaSebelumTransfer.setText(tbObat.getValueAt(tbObat.getSelectedRow(),22).toString()); 
            ObatTelahDiberikan.setText(tbObat.getValueAt(tbObat.getSelectedRow(),23).toString()); 
/*            KeadaanUmumSetelahTransfer.setSelectedItem(tbObat.getValueAt(tbObat.getSelectedRow(),28).toString());
            TDSetelahTransfer.setText(tbObat.getValueAt(tbObat.getSelectedRow(),29).toString()); 
            NadiSetelahTransfer.setText(tbObat.getValueAt(tbObat.getSelectedRow(),30).toString()); 
            RRSetelahTransfer.setText(tbObat.getValueAt(tbObat.getSelectedRow(),31).toString()); 
            SuhuSetelahTransfer.setText(tbObat.getValueAt(tbObat.getSelectedRow(),32).toString()); */
//            KdPetugasMenerima.setText(tbObat.getValueAt(tbObat.getSelectedRow(),31).toString()); 
//            NmPetugasMenerima.setText(tbObat.getValueAt(tbObat.getSelectedRow(),32).toString()); 
            if(tbObat.getValueAt(tbObat.getSelectedRow(),24).toString().equals("Ya")){            
                ChkKlinikal.setSelected(true);
            }else{
                ChkKlinikal.setSelected(false);
            }
            KetKlinikal.setText(tbObat.getValueAt(tbObat.getSelectedRow(),25).toString());              
            if(tbObat.getValueAt(tbObat.getSelectedRow(),26).toString().equals("Ya")){
                ChkNonKlinikal.setSelected(true);
            }else{
                ChkNonKlinikal.setSelected(false);
            }
            KetNonKlinikal.setText(tbObat.getValueAt(tbObat.getSelectedRow(),27).toString());              
            if(tbObat.getValueAt(tbObat.getSelectedRow(),28).toString().equals("Ya")){
                ChkICUPenuh.setSelected(true);
            }else{
                ChkICUPenuh.setSelected(false);
            }
            if(tbObat.getValueAt(tbObat.getSelectedRow(),29).toString().equals("Ya")){
                ChkRanapPenuh.setSelected(true);
            }else{
                ChkRanapPenuh.setSelected(false);
            }   
            if(tbObat.getValueAt(tbObat.getSelectedRow(),30).toString().equals("Ya")){
                ChkPermintaanPasien.setSelected(true);
            }else{
                ChkPermintaanPasien.setSelected(false);
            }
            if(tbObat.getValueAt(tbObat.getSelectedRow(),31).toString().equals("Ya")){
                ChkAlasanLainnya.setSelected(true);
            }else{
                ChkAlasanLainnya.setSelected(false);
            }
            AlasanLainnya.setText(tbObat.getValueAt(tbObat.getSelectedRow(),32).toString()); 
            if(tbObat.getValueAt(tbObat.getSelectedRow(),33).toString().equals("Ya")){
                ChkTAlmed.setSelected(true);
            }else{
                ChkTAlmed.setSelected(false);
            }            
            if(tbObat.getValueAt(tbObat.getSelectedRow(),34).toString().equals("Ya")){
                ChkYaAlmed.setSelected(true);
            }else{
                ChkYaAlmed.setSelected(false);
            }
            if(tbObat.getValueAt(tbObat.getSelectedRow(),35).toString().equals("Ya")){
                ChkPInfus.setSelected(true);
            }else{
                ChkPInfus.setSelected(false);
            }   
            if(tbObat.getValueAt(tbObat.getSelectedRow(),36).toString().equals("Ya")){
                ChkPETT.setSelected(true);
            }else{
                ChkPETT.setSelected(false);
            }
            if(tbObat.getValueAt(tbObat.getSelectedRow(),37).toString().equals("Ya")){
                ChkPO2.setSelected(true);
            }else{
                ChkPO2.setSelected(false);
            }
            if(tbObat.getValueAt(tbObat.getSelectedRow(),38).toString().equals("Ya")){
                ChkPCVC.setSelected(true);
            }else{
                ChkPCVC.setSelected(false);
            }            
            if(tbObat.getValueAt(tbObat.getSelectedRow(),39).toString().equals("Ya")){
                ChkPNGT.setSelected(true);
            }else{
                ChkPNGT.setSelected(false);
            }
            if(tbObat.getValueAt(tbObat.getSelectedRow(),40).toString().equals("Ya")){
                ChkPCateter.setSelected(true);
            }else{
                ChkPCateter.setSelected(false);
            }   
            if(tbObat.getValueAt(tbObat.getSelectedRow(),41).toString().equals("Ya")){
                ChkPBidal.setSelected(true);
            }else{
                ChkPBidal.setSelected(false);
            }
            if(tbObat.getValueAt(tbObat.getSelectedRow(),42).toString().equals("Ya")){
                ChkPPump.setSelected(true);
            }else{
                ChkPPump.setSelected(false);
            } 
            if(tbObat.getValueAt(tbObat.getSelectedRow(),43).toString().equals("Ya")){
                ChkPAlmedLainnya.setSelected(true);
            }else{
                ChkPAlmedLainnya.setSelected(false);
            }              
            KetPAmedLainnya.setText(tbObat.getValueAt(tbObat.getSelectedRow(),44).toString());                        

            if(tbObat.getValueAt(tbObat.getSelectedRow(),45).toString().equals("Ya")){
                ChkTdkAdaRiwayat.setSelected(true);
            }else{
                ChkTdkAdaRiwayat.setSelected(false);
            }            
            if(tbObat.getValueAt(tbObat.getSelectedRow(),46).toString().equals("Ya")){
                ChkAdaRiwayat.setSelected(true);
            }else{
                ChkAdaRiwayat.setSelected(false);
            }
            if(tbObat.getValueAt(tbObat.getSelectedRow(),47).toString().equals("Ya")){
                ChkPDM.setSelected(true);
            }else{
                ChkPDM.setSelected(false);
            }   
            if(tbObat.getValueAt(tbObat.getSelectedRow(),48).toString().equals("Ya")){
                ChkPJantung.setSelected(true);
            }else{
                ChkPJantung.setSelected(false);
            }
            if(tbObat.getValueAt(tbObat.getSelectedRow(),49).toString().equals("Ya")){
                ChkPLainnya.setSelected(true);
            }else{
                ChkPLainnya.setSelected(false);
            }
            KetPLainnya.setText(tbObat.getValueAt(tbObat.getSelectedRow(),50).toString());
            
            if(tbObat.getValueAt(tbObat.getSelectedRow(),51).toString().equals("Ya")){
                ChkRM.setSelected(true);
            }else{
                ChkRM.setSelected(false);
            }
            if(tbObat.getValueAt(tbObat.getSelectedRow(),52).toString().equals("Ya")){
                ChkLAB.setSelected(true);
            }else{
                ChkLAB.setSelected(false);
            }
            if(tbObat.getValueAt(tbObat.getSelectedRow(),53).toString().equals("Ya")){
                ChkRAD.setSelected(true);
            }else{
                ChkRAD.setSelected(false);
            }
            if(tbObat.getValueAt(tbObat.getSelectedRow(),54).toString().equals("Ya")){
                ChkLainnya.setSelected(true);
            }else{
                ChkLainnya.setSelected(false);
            }
            KetDokLainnya.setText(tbObat.getValueAt(tbObat.getSelectedRow(),55).toString());  
            KeluhanUtamaSetelahTransfer.setText(tbObat.getValueAt(tbObat.getSelectedRow(),56).toString());             
            KdDokterMenyerahkan.setText(tbObat.getValueAt(tbObat.getSelectedRow(),57).toString()); 
            NmDokterMenyerahkan.setText(tbObat.getValueAt(tbObat.getSelectedRow(),58).toString());             
            Valid.SetTgl2(TanggalKontakRS,tbObat.getValueAt(tbObat.getSelectedRow(),5).toString());
            Valid.SetTgl2(TanggalRujukKeluarRS,tbObat.getValueAt(tbObat.getSelectedRow(),6).toString());          
        }
    }

    private void isRawat() {
        try {
            ps=koneksi.prepareStatement(
                    "select reg_periksa.no_rkm_medis,pasien.nm_pasien, if(pasien.jk='L','Laki-Laki','Perempuan') as jk,pasien.tgl_lahir,reg_periksa.tgl_registrasi "+
                    "from reg_periksa inner join pasien on reg_periksa.no_rkm_medis=pasien.no_rkm_medis "+
                    "where reg_periksa.no_rawat=?");
            try {
                ps.setString(1,TNoRw.getText());
                rs=ps.executeQuery();
                if(rs.next()){
                    TNoRM.setText(rs.getString("no_rkm_medis"));
                    DTPCari1.setDate(rs.getDate("tgl_registrasi"));
                    TPasien.setText(rs.getString("nm_pasien"));
                    Jk.setText(rs.getString("jk"));
                    TglLahir.setText(rs.getString("tgl_lahir"));
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
        } catch (Exception e) {
            System.out.println("Notif : "+e);
        }
    }
 
    public void setNoRm(String norwt,Date tgl2) {
        TNoRw.setText(norwt);
        TCari.setText(norwt);
        DTPCari2.setDate(tgl2);    
        isRawat(); 
    }
    
/*    public void isCek(){
        BtnSimpan.setEnabled(akses.gettransfer_pasien_antar_ruang());
        BtnHapus.setEnabled(akses.gettransfer_pasien_antar_ruang());
        BtnEdit.setEnabled(akses.gettransfer_pasien_antar_ruang());
        BtnPrint.setEnabled(akses.gettransfer_pasien_antar_ruang()); 
           
    }*/

    public void isCek(){
        BtnSimpan.setEnabled(akses.getregistrasi());
        BtnHapus.setEnabled(akses.getregistrasi());
        BtnEdit.setEnabled(akses.getregistrasi());
//        BtnPrint.setEnabled(akses.getregistrasi());  
        if(akses.getjml2()>=1){
            KdDokterMenyerahkan.setEditable(false);
            BtnDokter.setEnabled(false);
            KdDokterMenyerahkan.setText(akses.getkode());
            NmDokterMenyerahkan.setText(dokter.tampil3(KdDokterMenyerahkan.getText()));
            if(NmDokterMenyerahkan.getText().equals("")){
                KdDokterMenyerahkan.setText("");
                JOptionPane.showMessageDialog(null,"User login bukan dokter...!!");
            }
        }            
    }
    
    public void setTampil(){
       TabRawat.setSelectedIndex(1);
    }

    private void hapus() {
        if(Sequel.queryu2tf("delete from transfer_pasien_keluar_rs where no_rawat=? and tanggal_kontak_rs=?",2,new String[]{
            tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),tbObat.getValueAt(tbObat.getSelectedRow(),5).toString()
        })==true){
            tabMode.removeRow(tbObat.getSelectedRow());
            LCount.setText(""+tabMode.getRowCount());
            TabRawat.setSelectedIndex(1);
        }else{
            JOptionPane.showMessageDialog(null,"Gagal menghapus..!!");
        }
    }

    private void ganti() {
       checkboxKlinikal = "";
       checkboxnonKlinikal = "";
       checkboxICUPenuh = "";
       checkboxRIPenuh = "";  
       checkboxPermintaanPasien = "";
       checkboxKetRujukLainnya = ""; 
       checkboxTdkAlmed = "";
       checkboxYaAlmed = "";
       checkboxPInfus = "";
       checkboxPETT = "";  
       checkboxPO2 = "";
       checkboxPCVC = ""; 
       checkboxPNGT = "";
       checkboxPCateter = "";
       checkboxPBidal = "";  
       checkboxPPump = "";
       checkboxPAlmedLainnya = "";          
       checkboxTdkAdaRiwayat = "";
       checkboxAdaRiwayat = "";
       checkboxPDM = "";  
       checkboxPJantung = "";
       checkboxPLainnya = "";        
       checkboxRM = "";
       checkboxLAB = "";       
       checkboxRAD = "";
       checkboxLAINNYA = "";       
        if(ChkKlinikal.isSelected()){
           checkboxKlinikal="Ya";
          }else{
           checkboxKlinikal="Tidak";
        }
        if(ChkNonKlinikal.isSelected()){
           checkboxnonKlinikal="Ya";
          }else{
           checkboxnonKlinikal="Tidak";
        }
        if(ChkICUPenuh.isSelected()){
           checkboxICUPenuh="Ya";
          }else{
           checkboxICUPenuh="Tidak";
        }
        if(ChkRanapPenuh.isSelected()){
           checkboxRIPenuh="Ya";
          }else{
           checkboxRIPenuh="Tidak";
        }
        if(ChkPermintaanPasien.isSelected()){
           checkboxPermintaanPasien="Ya";
          }else{
           checkboxPermintaanPasien="Tidak";
        }
        if(ChkAlasanLainnya.isSelected()){
           checkboxKetRujukLainnya="Ya";
          }else{
           checkboxKetRujukLainnya="Tidak";
        } 
        if(ChkTAlmed.isSelected()){
           checkboxTdkAlmed="Ya";
          }else{
           checkboxTdkAlmed="Tidak";
        }
        if(ChkYaAlmed.isSelected()){
           checkboxYaAlmed="Ya";
          }else{
           checkboxYaAlmed="Tidak";
        }
        if(ChkPInfus.isSelected()){
           checkboxPInfus="Ya";
          }else{
           checkboxPInfus="Tidak";
        }
        if(ChkPETT.isSelected()){
           checkboxPETT="Ya";
          }else{
           checkboxPETT="Tidak";
        }          
        if(ChkPO2.isSelected()){
           checkboxPO2="Ya";
          }else{
           checkboxPO2="Tidak";
        }             
        if(ChkPCVC.isSelected()){
           checkboxPCVC="Ya";
          }else{
           checkboxPCVC="Tidak";
        } 
        if(ChkPNGT.isSelected()){
           checkboxPNGT="Ya";
          }else{
           checkboxPNGT="Tidak";
        }
        if(ChkPCateter.isSelected()){
           checkboxPCateter="Ya";
          }else{
           checkboxPCateter="Tidak";
        }
        if(ChkPBidal.isSelected()){
           checkboxPBidal="Ya";
          }else{
           checkboxPBidal="Tidak";
        }
        if(ChkPPump.isSelected()){
           checkboxPPump="Ya";
          }else{
           checkboxPPump="Tidak";
        }
        if(ChkPAlmedLainnya.isSelected()){
           checkboxPAlmedLainnya="Ya";
          }else{
           checkboxPAlmedLainnya="Tidak";                    
        }         
        if(ChkTdkAdaRiwayat.isSelected()){
           checkboxTdkAdaRiwayat="Ya";
          }else{
           checkboxTdkAdaRiwayat="Tidak";
        }
        if(ChkAdaRiwayat.isSelected()){
           checkboxAdaRiwayat="Ya";
          }else{
           checkboxAdaRiwayat="Tidak";
        }
        if(ChkPDM.isSelected()){
           checkboxPDM="Ya";
          }else{
           checkboxPDM="Tidak";
        }
        if(ChkPJantung.isSelected()){
           checkboxPJantung="Ya";
          }else{
           checkboxPJantung="Tidak";
        }
        if(ChkPLainnya.isSelected()){
           checkboxPLainnya="Ya";
          }else{
           checkboxPLainnya="Tidak";
        }       
        if(ChkRM.isSelected()){
           checkboxRM="Ya";
          }else{
           checkboxRM="Tidak";
        }
        if(ChkLAB.isSelected()){
           checkboxLAB="Ya";
          }else{
           checkboxLAB="Tidak";
        }
        if(ChkRAD.isSelected()){
           checkboxRAD="Ya";
          }else{
           checkboxRAD="Tidak";
        }        
        if(ChkLainnya.isSelected()){
           checkboxLAINNYA="Ya";
          }else{
           checkboxLAINNYA="Tidak";
        }         
        if(Sequel.mengedittf("transfer_pasien_keluar_rs","no_rawat=? and tanggal_kontak_rs=?","no_rawat=?,tanggal_kontak_rs=?,tanggal_rujuk_keluar_rs=?,asal_ruang=?,ruang_selanjutnya=?,diagnosa_utama=?,"+        
 //               "diagnosa_sekunder=?,indikasi_pindah_ruang=?,prosedur_yang_sudah_dilakukan=?,obat_yang_telah_diberikan=?,metode_pemindahan_pasien=?,pemeriksaan_penunjang_yang_dilakukan=?,"+  //keterangan_indikasi_pindah_ruang=?,peralatan_yang_menyertai=?,keterangan_peralatan_yang_menyertai=?,
                "pasien_keluarga_menyetujui=?,nama_menyetujui=?,hubungan_menyetujui=?,"+
                "nama_penghubung_rs=?,telp_penghubung_rs=?,nama_penerima_kontak_rs=?,telp_penerima_kontak_rs=?,"+             
                "keadaan_umum_sebelum_transfer=?,nadi_sebelum_transfer=?,suhu_sebelum_transfer=?,td_sebelum_transfer=?,rr_sebelum_transfer=?,keluhan_utama_sebelum_transfer=?,obat_telah_diberikan=?,"+
//                "keluhan_utama_sesudah_transfer=?,"+ //keadaan_umum_sesudah_transfer=?,td_sesudah_transfer=?,nadi_sesudah_transfer=?,rr_sesudah_transfer=?,suhu_sesudah_transfer=?,nip_menerima=?,
                "klinikal=?,ket_klinikal=?,non_klinikal=?,ket_non_kinikal=?,icu_penuh=?,ranap_penuh=?,permintaan_pasien=?,rujuk_lainnya=?,ket_rujuk_lainnya=?,"+
                "tanpa_almed=?,terpasang_almed=?,terpasang_infus=?,terpasang_ett=?,terpasang_02=?,terpasang_cvc=?,terpasang_ngt=?,terpasang_cateter=?,terpasang_bidal=?,terpasang_pump=?,terpasang_almed_lainnya=?,ket_almed_lainnya=?,"+
                "tanpa_riwayat_penyakit=?,ada_riwayat_penyakit=?,riwayat_dm=?,riwayat_jantung=?,riwayat_penyakit_lainnya=?,ket_riwayat_penyakit_lainnya=?,dokumen_rm=?,dokumen_lab=?,dokumen_rad=?,dokumen_lainnya=?,ket_dokumen_lainnya=?,"+                
                "keluhan_utama_sesudah_transfer=?,kd_dokter=?",56,new String[]{ //66                   
                TNoRw.getText(),Valid.SetTgl(TanggalKontakRS.getSelectedItem()+"")+" "+TanggalKontakRS.getSelectedItem().toString().substring(11,19),
                Valid.SetTgl(TanggalRujukKeluarRS.getSelectedItem()+"")+" "+TanggalRujukKeluarRS.getSelectedItem().toString().substring(11,19),AsalRuang.getText(), 
                RuangSelanjutnya.getText(),DiagnosaUtama.getText(),                
        //        DiagnosaSekunder.getText(),IndikasiPindah.getSelectedItem().toString(),ProsedurDilakukan.getText(),ObatYangDiberikan.getText(),PemeriksaanPenunjang.getText(),//KeteranganIndikasiPindahRuang.getText(), MetodePemindahan.getSelectedItem().toString(),PeralatanMenyertai.getSelectedItem().toString(),KeteranganPeralatan.getText(),
                MenyetujuiPemindahan.getSelectedItem().toString(),NamaPJMenyetujui.getText(),HubunganPJMenyetujui.getSelectedItem().toString(),
                NamaPenghubungRS.getText(),NoTelpPenghubungRS.getText(),NamaPenerimaTujuanRS.getText(),NoTelpPenerimaTujuanRS.getText(),          
                KeadaanUmumSebelumTransfer.getSelectedItem().toString(),NadiSebelumTransfer.getText(),SuhuSebelumTransfer.getText(),TDSebelumTransfer.getText(),RRSebelumTransfer.getText(),KeluhanUtamaSebelumTransfer.getText(),ObatTelahDiberikan.getText(),                  
//                KeadaanUmumSetelahTransfer.getSelectedItem().toString(),TDSetelahTransfer.getText(),NadiSetelahTransfer.getText(),RRSetelahTransfer.getText(),SuhuSetelahTransfer.getText(),          
                checkboxKlinikal,
                KetKlinikal.getText(),                   
                checkboxnonKlinikal,
                KetNonKlinikal.getText(),                   
                checkboxICUPenuh,
                checkboxRIPenuh,
                checkboxPermintaanPasien,
                checkboxKetRujukLainnya,                
                AlasanLainnya.getText(),    
                checkboxTdkAlmed,
                checkboxYaAlmed,
                checkboxPInfus,
                checkboxPETT,
                checkboxPO2,
                checkboxPCVC,
                checkboxPNGT,
                checkboxPCateter,
                checkboxPBidal,
                checkboxPPump,
                checkboxPAlmedLainnya,  
                KetPAmedLainnya.getText(),
                checkboxTdkAdaRiwayat,
                checkboxAdaRiwayat,
                checkboxPDM,
                checkboxPJantung,
                checkboxPLainnya,
                KetPLainnya.getText(), 
                checkboxRM,
                checkboxLAB,
                checkboxRAD,
                checkboxLAINNYA,
                KetDokLainnya.getText(),                 
                KeluhanUtamaSetelahTransfer.getText(),                
                KdDokterMenyerahkan.getText(),//KdPetugasMenerima.getText(),
                tbObat.getValueAt(tbObat.getSelectedRow(),0).toString(),
                tbObat.getValueAt(tbObat.getSelectedRow(),5).toString()
            })==true){
                tbObat.setValueAt(TNoRw.getText(),tbObat.getSelectedRow(),0);
                tbObat.setValueAt(TNoRM.getText(),tbObat.getSelectedRow(),1);
                tbObat.setValueAt(TPasien.getText(),tbObat.getSelectedRow(),2);
                tbObat.setValueAt(TglLahir.getText(),tbObat.getSelectedRow(),3);
                tbObat.setValueAt(Jk.getText(),tbObat.getSelectedRow(),4);
                tbObat.setValueAt(Valid.SetTgl(TanggalKontakRS.getSelectedItem()+"")+" "+TanggalKontakRS.getSelectedItem().toString().substring(11,19),tbObat.getSelectedRow(),5);
                tbObat.setValueAt(Valid.SetTgl(TanggalRujukKeluarRS.getSelectedItem()+"")+" "+TanggalRujukKeluarRS.getSelectedItem().toString().substring(11,19),tbObat.getSelectedRow(),6);
//                tbObat.setValueAt(IndikasiPindah.getSelectedItem().toString(),tbObat.getSelectedRow(),7);
//                tbObat.setValueAt(KeteranganIndikasiPindahRuang.getText(),tbObat.getSelectedRow(),8);
                tbObat.setValueAt(AsalRuang.getText(),tbObat.getSelectedRow(),7);
                tbObat.setValueAt(RuangSelanjutnya.getText(),tbObat.getSelectedRow(),8);
//                tbObat.setValueAt(MetodePemindahan.getSelectedItem().toString(),tbObat.getSelectedRow(),11);          
//                tbObat.setValueAt(DiagnosaSekunder.getText(),tbObat.getSelectedRow(),13);
//                tbObat.setValueAt(ProsedurDilakukan.getText(),tbObat.getSelectedRow(),14);
//                tbObat.setValueAt(ObatYangDiberikan.getText(),tbObat.getSelectedRow(),15);
//                tbObat.setValueAt(PemeriksaanPenunjang.getText(),tbObat.getSelectedRow(),16);
//                tbObat.setValueAt(PeralatanMenyertai.getSelectedItem().toString(),tbObat.getSelectedRow(),17);
//                tbObat.setValueAt(KeteranganPeralatan.getText(),tbObat.getSelectedRow(),18);
                tbObat.setValueAt(MenyetujuiPemindahan.getSelectedItem().toString(),tbObat.getSelectedRow(),10);
                tbObat.setValueAt(NamaPJMenyetujui.getText(),tbObat.getSelectedRow(),11);
                tbObat.setValueAt(HubunganPJMenyetujui.getSelectedItem().toString(),tbObat.getSelectedRow(),12);
                tbObat.setValueAt(NamaPenghubungRS.getText(),tbObat.getSelectedRow(),13);
                tbObat.setValueAt(NoTelpPenghubungRS.getText(),tbObat.getSelectedRow(),14);
                tbObat.setValueAt(NamaPenerimaTujuanRS.getText(),tbObat.getSelectedRow(),15);
                tbObat.setValueAt(NoTelpPenerimaTujuanRS.getText(),tbObat.getSelectedRow(),16);                                      
                tbObat.setValueAt(KeadaanUmumSebelumTransfer.getSelectedItem().toString(),tbObat.getSelectedRow(),17);
                tbObat.setValueAt(NadiSebelumTransfer.getText(),tbObat.getSelectedRow(),18);
                tbObat.setValueAt(SuhuSebelumTransfer.getText(),tbObat.getSelectedRow(),19); 
                tbObat.setValueAt(TDSebelumTransfer.getText(),tbObat.getSelectedRow(),20);                
                tbObat.setValueAt(RRSebelumTransfer.getText(),tbObat.getSelectedRow(),21);
                tbObat.setValueAt(KeluhanUtamaSebelumTransfer.getText(),tbObat.getSelectedRow(),22);
                tbObat.setValueAt(ObatTelahDiberikan.getText(),tbObat.getSelectedRow(),23);                
/*                tbObat.setValueAt(KeadaanUmumSetelahTransfer.getSelectedItem().toString(),tbObat.getSelectedRow(),28);
                tbObat.setValueAt(TDSetelahTransfer.getText(),tbObat.getSelectedRow(),29);
                tbObat.setValueAt(NadiSetelahTransfer.getText(),tbObat.getSelectedRow(),30);
                tbObat.setValueAt(RRSetelahTransfer.getText(),tbObat.getSelectedRow(),31);
                tbObat.setValueAt(SuhuSetelahTransfer.getText(),tbObat.getSelectedRow(),32);*/
//                tbObat.setValueAt(KdPetugasMenerima.getText(),tbObat.getSelectedRow(),31);
//                tbObat.setValueAt(NmPetugasMenerima.getText(),tbObat.getSelectedRow(),32);                
                tbObat.setValueAt(checkboxKlinikal,tbObat.getSelectedRow(),24);
                tbObat.setValueAt(KetKlinikal.getText(),tbObat.getSelectedRow(),25);                
                tbObat.setValueAt(checkboxnonKlinikal,tbObat.getSelectedRow(),26);
                tbObat.setValueAt(KetNonKlinikal.getText(),tbObat.getSelectedRow(),27);                
                tbObat.setValueAt(checkboxICUPenuh,tbObat.getSelectedRow(),28);
                tbObat.setValueAt(checkboxRIPenuh,tbObat.getSelectedRow(),29);
                tbObat.setValueAt(checkboxPermintaanPasien,tbObat.getSelectedRow(),30);
                tbObat.setValueAt(checkboxKetRujukLainnya,tbObat.getSelectedRow(),31);   
                tbObat.setValueAt(AlasanLainnya.getText(),tbObat.getSelectedRow(),32);          
                tbObat.setValueAt(checkboxTdkAlmed,tbObat.getSelectedRow(),33);
                tbObat.setValueAt(checkboxYaAlmed,tbObat.getSelectedRow(),34);
                tbObat.setValueAt(checkboxPInfus,tbObat.getSelectedRow(),35);
                tbObat.setValueAt(checkboxPETT,tbObat.getSelectedRow(),36);   
                tbObat.setValueAt(checkboxPO2,tbObat.getSelectedRow(),37);
                tbObat.setValueAt(checkboxPCVC,tbObat.getSelectedRow(),38);
                tbObat.setValueAt(checkboxPNGT,tbObat.getSelectedRow(),39);
                tbObat.setValueAt(checkboxPCateter,tbObat.getSelectedRow(),40);
                tbObat.setValueAt(checkboxPBidal,tbObat.getSelectedRow(),41);   
                tbObat.setValueAt(checkboxPPump,tbObat.getSelectedRow(),42); 
                tbObat.setValueAt(checkboxPAlmedLainnya,tbObat.getSelectedRow(),43);                 
                tbObat.setValueAt(KetPAmedLainnya.getText(),tbObat.getSelectedRow(),44);           
                tbObat.setValueAt(checkboxTdkAdaRiwayat,tbObat.getSelectedRow(),45);
                tbObat.setValueAt(checkboxAdaRiwayat,tbObat.getSelectedRow(),46);
                tbObat.setValueAt(checkboxPDM,tbObat.getSelectedRow(),47);
                tbObat.setValueAt(checkboxPJantung,tbObat.getSelectedRow(),48);   
                tbObat.setValueAt(checkboxPLainnya,tbObat.getSelectedRow(),49);
                tbObat.setValueAt(KetPLainnya.getText(),tbObat.getSelectedRow(),50);                  
                tbObat.setValueAt(checkboxRM,tbObat.getSelectedRow(),51);
                tbObat.setValueAt(checkboxLAB,tbObat.getSelectedRow(),52);
                tbObat.setValueAt(checkboxRAD,tbObat.getSelectedRow(),53);                
                tbObat.setValueAt(checkboxLAINNYA,tbObat.getSelectedRow(),54);
                tbObat.setValueAt(KetDokLainnya.getText(),tbObat.getSelectedRow(),55);                     
                tbObat.setValueAt(KeluhanUtamaSetelahTransfer.getText(),tbObat.getSelectedRow(),56);
                tbObat.setValueAt(KdDokterMenyerahkan.getText(),tbObat.getSelectedRow(),57);
                tbObat.setValueAt(NmDokterMenyerahkan.getText(),tbObat.getSelectedRow(),58);                
                emptTeks();
                TabRawat.setSelectedIndex(1);
        }
    }

    private void simpan() {

       checkboxKlinikal = "";
       checkboxnonKlinikal = "";
       checkboxICUPenuh = "";
       checkboxRIPenuh = "";  
       checkboxPermintaanPasien = "";
       checkboxKetRujukLainnya = "";   
       checkboxTdkAlmed = "";
       checkboxYaAlmed = "";
       checkboxPInfus = "";
       checkboxPETT = "";  
       checkboxPO2 = "";
       checkboxPCVC = ""; 
       checkboxPNGT = "";
       checkboxPCateter = "";
       checkboxPBidal = "";  
       checkboxPPump = "";
       checkboxPAlmedLainnya = "";   
       checkboxTdkAdaRiwayat = "";
       checkboxAdaRiwayat = "";
       checkboxPDM = "";  
       checkboxPJantung = "";
       checkboxPLainnya = "";         
       checkboxRM = "";
       checkboxLAB = "";
       checkboxRAD = "";       
       checkboxLAINNYA = "";                      

        if(ChkKlinikal.isSelected()){
           checkboxKlinikal="Ya";
          }else{
           checkboxKlinikal="Tidak";
        }
        if(ChkNonKlinikal.isSelected()){
           checkboxnonKlinikal="Ya";
          }else{
           checkboxnonKlinikal="Tidak";
        }
        if(ChkICUPenuh.isSelected()){
           checkboxICUPenuh="Ya";
          }else{
           checkboxICUPenuh="Tidak";
        }
        if(ChkRanapPenuh.isSelected()){
           checkboxRIPenuh="Ya";
          }else{
           checkboxRIPenuh="Tidak";
        }
        if(ChkPermintaanPasien.isSelected()){
           checkboxPermintaanPasien="Ya";
          }else{
           checkboxPermintaanPasien="Tidak";
        }
        if(ChkAlasanLainnya.isSelected()){
           checkboxKetRujukLainnya="Ya";
          }else{
           checkboxKetRujukLainnya="Tidak";
        }      
        if(ChkTAlmed.isSelected()){
           checkboxTdkAlmed="Ya";
          }else{
           checkboxTdkAlmed="Tidak";
        }
        if(ChkYaAlmed.isSelected()){
           checkboxYaAlmed="Ya";
          }else{
           checkboxYaAlmed="Tidak";
        }
        if(ChkPInfus.isSelected()){
           checkboxPInfus="Ya";
          }else{
           checkboxPInfus="Tidak";
        }
        if(ChkPETT.isSelected()){
           checkboxPETT="Ya";
          }else{
           checkboxPETT="Tidak";
        }
        if(ChkPO2.isSelected()){
           checkboxPO2="Ya";
          }else{
           checkboxPO2="Tidak";
        }
        if(ChkPCVC.isSelected()){
           checkboxPCVC="Ya";
          }else{
           checkboxPCVC="Tidak";
        }        
        if(ChkPNGT.isSelected()){
           checkboxPNGT="Ya";
          }else{
           checkboxPNGT="Tidak";
        }
        if(ChkPCateter.isSelected()){
           checkboxPCateter="Ya";
          }else{
           checkboxPCateter="Tidak";
        }
        if(ChkPBidal.isSelected()){
           checkboxPBidal="Ya";
          }else{
           checkboxPBidal="Tidak";
        }
        if(ChkPPump.isSelected()){
           checkboxPPump="Ya";
          }else{
           checkboxPPump="Tidak";
        }        
        if(ChkPAlmedLainnya.isSelected()){
           checkboxPAlmedLainnya="Ya";
          }else{
           checkboxPAlmedLainnya="Tidak";
        }     
        if(ChkTdkAdaRiwayat.isSelected()){
           checkboxTdkAdaRiwayat="Ya";
          }else{
           checkboxTdkAdaRiwayat="Tidak";
        }
        if(ChkAdaRiwayat.isSelected()){
           checkboxAdaRiwayat="Ya";
          }else{
           checkboxAdaRiwayat="Tidak";
        }
        if(ChkPDM.isSelected()){
           checkboxPDM="Ya";
          }else{
           checkboxPDM="Tidak";
        }
        if(ChkPJantung.isSelected()){
           checkboxPJantung="Ya";
          }else{
           checkboxPJantung="Tidak";
        }
        if(ChkPLainnya.isSelected()){
           checkboxPLainnya="Ya";
          }else{
           checkboxPLainnya="Tidak";
        }         
        if(ChkRM.isSelected()){
           checkboxRM="Ya";
          }else{
           checkboxRM="Tidak";
        }
        if(ChkLAB.isSelected()){
           checkboxLAB="Ya";
          }else{
           checkboxLAB="Tidak";
        }
        if(ChkRAD.isSelected()){
           checkboxRAD="Ya";
          }else{
           checkboxRAD="Tidak";
        }        
        if(ChkLainnya.isSelected()){
           checkboxLAINNYA="Ya";
          }else{
           checkboxLAINNYA="Tidak";
        }            
        if(Sequel.menyimpantf("transfer_pasien_keluar_rs","?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?","No.Rawat & Tanggal Masuk",54,new String[]{ //64
                TNoRw.getText(),Valid.SetTgl(TanggalKontakRS.getSelectedItem()+"")+" "+TanggalKontakRS.getSelectedItem().toString().substring(11,19),
                Valid.SetTgl(TanggalRujukKeluarRS.getSelectedItem()+"")+" "+TanggalRujukKeluarRS.getSelectedItem().toString().substring(11,19),AsalRuang.getText(), 
                RuangSelanjutnya.getText(),DiagnosaUtama.getText(),
//                DiagnosaSekunder.getText(),IndikasiPindah.getSelectedItem().toString(),ProsedurDilakukan.getText(),ObatYangDiberikan.getText(),PemeriksaanPenunjang.getText(),//KeteranganIndikasiPindahRuang.getText(), MetodePemindahan.getSelectedItem().toString(),PeralatanMenyertai.getSelectedItem().toString(),KeteranganPeralatan.getText(),
                MenyetujuiPemindahan.getSelectedItem().toString(),NamaPJMenyetujui.getText(),HubunganPJMenyetujui.getSelectedItem().toString(),                
                NamaPenghubungRS.getText(),NoTelpPenghubungRS.getText(),NamaPenerimaTujuanRS.getText(),NoTelpPenerimaTujuanRS.getText(),                   
                KeadaanUmumSebelumTransfer.getSelectedItem().toString(),NadiSebelumTransfer.getText(),SuhuSebelumTransfer.getText(),TDSebelumTransfer.getText(),RRSebelumTransfer.getText(),KeluhanUtamaSebelumTransfer.getText(),ObatTelahDiberikan.getText(),
              
                checkboxKlinikal,
                KetKlinikal.getText(),                
                checkboxnonKlinikal,
                KetNonKlinikal.getText(),                
                checkboxICUPenuh,
                checkboxRIPenuh,  
                checkboxPermintaanPasien,
                checkboxKetRujukLainnya,  
                AlasanLainnya.getText(), 
                checkboxTdkAlmed,
                checkboxYaAlmed,
                checkboxPInfus,
                checkboxPETT,
                checkboxPO2, 
                checkboxPCVC,
                checkboxPNGT,
                checkboxPCateter,
                checkboxPBidal,
                checkboxPPump, 
                checkboxPAlmedLainnya,                
                KetPAmedLainnya.getText(), 
                checkboxTdkAdaRiwayat,
                checkboxAdaRiwayat,
                checkboxPDM,
                checkboxPJantung,
                checkboxPLainnya, 
                KetPLainnya.getText(),                
                checkboxRM,
                checkboxLAB,
                checkboxRAD,
                checkboxLAINNYA,
                KetDokLainnya.getText(),                    
                KeluhanUtamaSetelahTransfer.getText(),
//                KeadaanUmumSetelahTransfer.getSelectedItem().toString(),TDSetelahTransfer.getText(),NadiSetelahTransfer.getText(),RRSetelahTransfer.getText(),SuhuSetelahTransfer.getText(), 
                KdDokterMenyerahkan.getText(),//KdPetugasMenerima.getText(),                           
            })==true){
                emptTeks();
                TabRawat.setSelectedIndex(1); 
                tampil();
        }
    }
    
/*    private void isPhoto(){
        if(ChkAccor.isSelected()==true){
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(480,HEIGHT));
            FormPhoto.setVisible(true);  
            ChkAccor.setVisible(true);
        }else if(ChkAccor.isSelected()==false){    
            ChkAccor.setVisible(false);
            PanelAccor.setPreferredSize(new Dimension(15,HEIGHT));
            FormPhoto.setVisible(false);  
            ChkAccor.setVisible(true);
        }
    }

    private void panggilPhoto() {
        if(FormPhoto.isVisible()==true){
            try {
                ps=koneksi.prepareStatement("select bukti_persetujuan_transfer_pasien_keluar_rs.photo from bukti_persetujuan_transfer_pasien_keluar_rs where bukti_persetujuan_transfer_pasien_keluar_rs.no_rawat=? and bukti_persetujuan_transfer_pasien_keluar_rs.tanggal_kontak_rs=?");
                try {
                    ps.setString(1,tbObat.getValueAt(tbObat.getSelectedRow(),0).toString());
                    ps.setString(2,tbObat.getValueAt(tbObat.getSelectedRow(),5).toString());
                    rs=ps.executeQuery();
                    if(rs.next()){
                        if(rs.getString("photo").equals("")||rs.getString("photo").equals("-")){
                            LoadHTML2.setText("<html><body><center><br><br><font face='tahoma' size='2' color='#434343'>Kosong</font></center></body></html>");
                        }else{
                            LoadHTML2.setText("<html><body><center><img src='http://"+koneksiDB.HOSTHYBRIDWEB()+":"+koneksiDB.PORTWEB()+"/"+koneksiDB.HYBRIDWEB()+"/persetujuantransferruang/"+rs.getString("photo")+"' alt='photo' width='500' height='500'/></center></body></html>");
                        }  
                    }else{
                        LoadHTML2.setText("<html><body><center><br><br><font face='tahoma' size='2' color='#434343'>Kosong</font></center></body></html>");
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
            } catch (Exception e) {
                System.out.println("Notif : "+e);
            }
        }
    }*/
}
